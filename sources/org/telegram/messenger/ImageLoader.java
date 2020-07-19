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
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$FileLocation;
import org.telegram.tgnet.TLRPC$InputEncryptedFile;
import org.telegram.tgnet.TLRPC$InputFile;
import org.telegram.tgnet.TLRPC$Message;
import org.telegram.tgnet.TLRPC$MessageMedia;
import org.telegram.tgnet.TLRPC$Photo;
import org.telegram.tgnet.TLRPC$PhotoSize;
import org.telegram.tgnet.TLRPC$TL_documentAttributeVideo;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_fileLocationToBeDeprecated;
import org.telegram.tgnet.TLRPC$TL_fileLocationUnavailable;
import org.telegram.tgnet.TLRPC$TL_messageMediaDocument;
import org.telegram.tgnet.TLRPC$TL_messageMediaPhoto;
import org.telegram.tgnet.TLRPC$TL_messageMediaWebPage;
import org.telegram.tgnet.TLRPC$TL_photoCachedSize;
import org.telegram.tgnet.TLRPC$TL_photoSize;
import org.telegram.tgnet.TLRPC$TL_photoStrippedSize;
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
        public TLRPC$Document parentDocument;

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

        private void reportProgress(long j, long j2) {
            long currentTimeMillis = System.currentTimeMillis();
            if (j != j2) {
                long j3 = this.lastProgressTime;
                if (j3 != 0 && j3 >= currentTimeMillis - 100) {
                    return;
                }
            }
            this.lastProgressTime = currentTimeMillis;
            Utilities.stageQueue.postRunnable(new Runnable(j, j2) {
                public final /* synthetic */ long f$1;
                public final /* synthetic */ long f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r4;
                }

                public final void run() {
                    ImageLoader.HttpFileTask.this.lambda$reportProgress$1$ImageLoader$HttpFileTask(this.f$1, this.f$2);
                }
            });
        }

        public /* synthetic */ void lambda$reportProgress$1$ImageLoader$HttpFileTask(long j, long j2) {
            ImageLoader.this.fileProgresses.put(this.url, new long[]{j, j2});
            AndroidUtilities.runOnUIThread(new Runnable(j, j2) {
                public final /* synthetic */ long f$1;
                public final /* synthetic */ long f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r4;
                }

                public final void run() {
                    ImageLoader.HttpFileTask.this.lambda$null$0$ImageLoader$HttpFileTask(this.f$1, this.f$2);
                }
            });
        }

        public /* synthetic */ void lambda$null$0$ImageLoader$HttpFileTask(long j, long j2) {
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.FileLoadProgressChanged, this.url, Long.valueOf(j), Long.valueOf(j2));
        }

        /* access modifiers changed from: protected */
        /* JADX WARNING: Code restructure failed: missing block: B:81:0x0122, code lost:
            if (r5 != -1) goto L_0x0134;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:84:0x0126, code lost:
            if (r10.fileSize == 0) goto L_0x0143;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:85:0x0128, code lost:
            reportProgress((long) r10.fileSize, (long) r10.fileSize);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:86:0x0132, code lost:
            r0 = e;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:87:0x0134, code lost:
            r1 = false;
         */
        /* JADX WARNING: Removed duplicated region for block: B:100:0x0148 A[Catch:{ all -> 0x0150 }] */
        /* JADX WARNING: Removed duplicated region for block: B:104:0x0156 A[SYNTHETIC, Splitter:B:104:0x0156] */
        /* JADX WARNING: Removed duplicated region for block: B:24:0x0076  */
        /* JADX WARNING: Removed duplicated region for block: B:27:0x007f  */
        /* JADX WARNING: Removed duplicated region for block: B:43:0x00ad A[SYNTHETIC, Splitter:B:43:0x00ad] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public java.lang.Boolean doInBackground(java.lang.Void... r11) {
            /*
                r10 = this;
                java.lang.String r11 = "Mozilla/5.0 (iPhone; CPU iPhone OS 10_0 like Mac OS X) AppleWebKit/602.1.38 (KHTML, like Gecko) Version/10.0 Mobile/14A5297c Safari/602.1"
                java.lang.String r0 = "User-Agent"
                r1 = 1
                r2 = 0
                r3 = 0
                java.net.URL r4 = new java.net.URL     // Catch:{ all -> 0x006f }
                java.lang.String r5 = r10.url     // Catch:{ all -> 0x006f }
                r4.<init>(r5)     // Catch:{ all -> 0x006f }
                java.net.URLConnection r4 = r4.openConnection()     // Catch:{ all -> 0x006f }
                r4.addRequestProperty(r0, r11)     // Catch:{ all -> 0x006c }
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
                r4.addRequestProperty(r0, r11)     // Catch:{ all -> 0x006c }
            L_0x0054:
                r4.connect()     // Catch:{ all -> 0x006c }
                java.io.InputStream r11 = r4.getInputStream()     // Catch:{ all -> 0x006c }
                java.io.RandomAccessFile r0 = new java.io.RandomAccessFile     // Catch:{ all -> 0x0067 }
                java.io.File r5 = r10.tempFile     // Catch:{ all -> 0x0067 }
                java.lang.String r6 = "rws"
                r0.<init>(r5, r6)     // Catch:{ all -> 0x0067 }
                r10.fileOutputStream = r0     // Catch:{ all -> 0x0067 }
                goto L_0x00a9
            L_0x0067:
                r0 = move-exception
                r9 = r0
                r0 = r11
                r11 = r9
                goto L_0x0072
            L_0x006c:
                r11 = move-exception
                r0 = r2
                goto L_0x0072
            L_0x006f:
                r11 = move-exception
                r0 = r2
                r4 = r0
            L_0x0072:
                boolean r5 = r11 instanceof java.net.SocketTimeoutException
                if (r5 == 0) goto L_0x007f
                boolean r5 = org.telegram.messenger.ApplicationLoader.isNetworkOnline()
                if (r5 == 0) goto L_0x00a5
                r10.canRetry = r3
                goto L_0x00a5
            L_0x007f:
                boolean r5 = r11 instanceof java.net.UnknownHostException
                if (r5 == 0) goto L_0x0086
                r10.canRetry = r3
                goto L_0x00a5
            L_0x0086:
                boolean r5 = r11 instanceof java.net.SocketException
                if (r5 == 0) goto L_0x009f
                java.lang.String r5 = r11.getMessage()
                if (r5 == 0) goto L_0x00a5
                java.lang.String r5 = r11.getMessage()
                java.lang.String r6 = "ECONNRESET"
                boolean r5 = r5.contains(r6)
                if (r5 == 0) goto L_0x00a5
                r10.canRetry = r3
                goto L_0x00a5
            L_0x009f:
                boolean r5 = r11 instanceof java.io.FileNotFoundException
                if (r5 == 0) goto L_0x00a5
                r10.canRetry = r3
            L_0x00a5:
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r11)
                r11 = r0
            L_0x00a9:
                boolean r0 = r10.canRetry
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
                r10.canRetry = r3     // Catch:{ Exception -> 0x00c7 }
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
                r10.fileSize = r0     // Catch:{ Exception -> 0x00f6 }
                goto L_0x00fa
            L_0x00f6:
                r0 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            L_0x00fa:
                if (r11 == 0) goto L_0x0144
                r0 = 32768(0x8000, float:4.5918E-41)
                byte[] r0 = new byte[r0]     // Catch:{ all -> 0x013e }
                r4 = 0
            L_0x0102:
                boolean r5 = r10.isCancelled()     // Catch:{ all -> 0x013e }
                if (r5 == 0) goto L_0x0109
                goto L_0x0134
            L_0x0109:
                int r5 = r11.read(r0)     // Catch:{ Exception -> 0x0136 }
                if (r5 <= 0) goto L_0x0121
                java.io.RandomAccessFile r6 = r10.fileOutputStream     // Catch:{ Exception -> 0x0136 }
                r6.write(r0, r3, r5)     // Catch:{ Exception -> 0x0136 }
                int r4 = r4 + r5
                int r5 = r10.fileSize     // Catch:{ Exception -> 0x0136 }
                if (r5 <= 0) goto L_0x0102
                long r5 = (long) r4     // Catch:{ Exception -> 0x0136 }
                int r7 = r10.fileSize     // Catch:{ Exception -> 0x0136 }
                long r7 = (long) r7     // Catch:{ Exception -> 0x0136 }
                r10.reportProgress(r5, r7)     // Catch:{ Exception -> 0x0136 }
                goto L_0x0102
            L_0x0121:
                r0 = -1
                if (r5 != r0) goto L_0x0134
                int r0 = r10.fileSize     // Catch:{ Exception -> 0x0132 }
                if (r0 == 0) goto L_0x0143
                int r0 = r10.fileSize     // Catch:{ Exception -> 0x0132 }
                long r3 = (long) r0     // Catch:{ Exception -> 0x0132 }
                int r0 = r10.fileSize     // Catch:{ Exception -> 0x0132 }
                long r5 = (long) r0     // Catch:{ Exception -> 0x0132 }
                r10.reportProgress(r3, r5)     // Catch:{ Exception -> 0x0132 }
                goto L_0x0143
            L_0x0132:
                r0 = move-exception
                goto L_0x0138
            L_0x0134:
                r1 = 0
                goto L_0x0143
            L_0x0136:
                r0 = move-exception
                r1 = 0
            L_0x0138:
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ all -> 0x013c }
                goto L_0x0143
            L_0x013c:
                r0 = move-exception
                goto L_0x0140
            L_0x013e:
                r0 = move-exception
                r1 = 0
            L_0x0140:
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            L_0x0143:
                r3 = r1
            L_0x0144:
                java.io.RandomAccessFile r0 = r10.fileOutputStream     // Catch:{ all -> 0x0150 }
                if (r0 == 0) goto L_0x0154
                java.io.RandomAccessFile r0 = r10.fileOutputStream     // Catch:{ all -> 0x0150 }
                r0.close()     // Catch:{ all -> 0x0150 }
                r10.fileOutputStream = r2     // Catch:{ all -> 0x0150 }
                goto L_0x0154
            L_0x0150:
                r0 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            L_0x0154:
                if (r11 == 0) goto L_0x015e
                r11.close()     // Catch:{ all -> 0x015a }
                goto L_0x015e
            L_0x015a:
                r11 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r11)
            L_0x015e:
                java.lang.Boolean r11 = java.lang.Boolean.valueOf(r3)
                return r11
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
        /* JADX WARNING: Removed duplicated region for block: B:105:0x013b A[Catch:{ all -> 0x0141 }] */
        /* JADX WARNING: Removed duplicated region for block: B:108:0x0144 A[SYNTHETIC, Splitter:B:108:0x0144] */
        /* JADX WARNING: Removed duplicated region for block: B:113:0x014e A[SYNTHETIC, Splitter:B:113:0x014e] */
        /* JADX WARNING: Removed duplicated region for block: B:129:0x016a A[SYNTHETIC, Splitter:B:129:0x016a] */
        /* JADX WARNING: Removed duplicated region for block: B:85:0x0105 A[Catch:{ all -> 0x0152, all -> 0x015d, all -> 0x0164 }] */
        /* JADX WARNING: Removed duplicated region for block: B:88:0x010e A[Catch:{ all -> 0x0152, all -> 0x015d, all -> 0x0164 }] */
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
                java.net.HttpURLConnection r1 = r7.httpConnection     // Catch:{ Exception -> 0x004e }
                if (r1 == 0) goto L_0x0052
                java.net.HttpURLConnection r1 = r7.httpConnection     // Catch:{ Exception -> 0x004e }
                int r1 = r1.getResponseCode()     // Catch:{ Exception -> 0x004e }
                r2 = 200(0xc8, float:2.8E-43)
                if (r1 == r2) goto L_0x0052
                r2 = 202(0xca, float:2.83E-43)
                if (r1 == r2) goto L_0x0052
                r2 = 304(0x130, float:4.26E-43)
                if (r1 == r2) goto L_0x0052
                r7.canRetry = r0     // Catch:{ Exception -> 0x004e }
                goto L_0x0052
            L_0x004e:
                r1 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)     // Catch:{ all -> 0x00fe }
            L_0x0052:
                java.net.HttpURLConnection r1 = r7.httpConnection     // Catch:{ all -> 0x00fe }
                java.io.InputStream r1 = r1.getInputStream()     // Catch:{ all -> 0x00fe }
                java.io.ByteArrayOutputStream r2 = new java.io.ByteArrayOutputStream     // Catch:{ all -> 0x00f8 }
                r2.<init>()     // Catch:{ all -> 0x00f8 }
                r3 = 32768(0x8000, float:4.5918E-41)
                byte[] r3 = new byte[r3]     // Catch:{ all -> 0x00f2 }
            L_0x0062:
                boolean r4 = r7.isCancelled()     // Catch:{ all -> 0x00f2 }
                if (r4 == 0) goto L_0x0069
                goto L_0x0074
            L_0x0069:
                int r4 = r1.read(r3)     // Catch:{ all -> 0x00f2 }
                if (r4 <= 0) goto L_0x0073
                r2.write(r3, r0, r4)     // Catch:{ all -> 0x00f2 }
                goto L_0x0062
            L_0x0073:
                r3 = -1
            L_0x0074:
                r7.canRetry = r0     // Catch:{ all -> 0x00f2 }
                org.json.JSONObject r3 = new org.json.JSONObject     // Catch:{ all -> 0x00f2 }
                java.lang.String r4 = new java.lang.String     // Catch:{ all -> 0x00f2 }
                byte[] r5 = r2.toByteArray()     // Catch:{ all -> 0x00f2 }
                r4.<init>(r5)     // Catch:{ all -> 0x00f2 }
                r3.<init>(r4)     // Catch:{ all -> 0x00f2 }
                java.lang.String r4 = "results"
                org.json.JSONArray r3 = r3.getJSONArray(r4)     // Catch:{ all -> 0x00f2 }
                int r4 = r3.length()     // Catch:{ all -> 0x00f2 }
                if (r4 <= 0) goto L_0x00d8
                org.json.JSONObject r3 = r3.getJSONObject(r0)     // Catch:{ all -> 0x00f2 }
                java.lang.String r4 = "artworkUrl100"
                java.lang.String r3 = r3.getString(r4)     // Catch:{ all -> 0x00f2 }
                boolean r4 = r7.small     // Catch:{ all -> 0x00f2 }
                if (r4 == 0) goto L_0x00b7
                java.net.HttpURLConnection r8 = r7.httpConnection     // Catch:{ all -> 0x00a8 }
                if (r8 == 0) goto L_0x00a9
                java.net.HttpURLConnection r8 = r7.httpConnection     // Catch:{ all -> 0x00a8 }
                r8.disconnect()     // Catch:{ all -> 0x00a8 }
                goto L_0x00a9
            L_0x00a8:
            L_0x00a9:
                if (r1 == 0) goto L_0x00b3
                r1.close()     // Catch:{ all -> 0x00af }
                goto L_0x00b3
            L_0x00af:
                r8 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r8)
            L_0x00b3:
                r2.close()     // Catch:{ Exception -> 0x00b6 }
            L_0x00b6:
                return r3
            L_0x00b7:
                java.lang.String r4 = "100x100"
                java.lang.String r5 = "600x600"
                java.lang.String r8 = r3.replace(r4, r5)     // Catch:{ all -> 0x00f2 }
                java.net.HttpURLConnection r0 = r7.httpConnection     // Catch:{ all -> 0x00c9 }
                if (r0 == 0) goto L_0x00ca
                java.net.HttpURLConnection r0 = r7.httpConnection     // Catch:{ all -> 0x00c9 }
                r0.disconnect()     // Catch:{ all -> 0x00c9 }
                goto L_0x00ca
            L_0x00c9:
            L_0x00ca:
                if (r1 == 0) goto L_0x00d4
                r1.close()     // Catch:{ all -> 0x00d0 }
                goto L_0x00d4
            L_0x00d0:
                r0 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            L_0x00d4:
                r2.close()     // Catch:{ Exception -> 0x00d7 }
            L_0x00d7:
                return r8
            L_0x00d8:
                java.net.HttpURLConnection r0 = r7.httpConnection     // Catch:{ all -> 0x00e2 }
                if (r0 == 0) goto L_0x00e3
                java.net.HttpURLConnection r0 = r7.httpConnection     // Catch:{ all -> 0x00e2 }
                r0.disconnect()     // Catch:{ all -> 0x00e2 }
                goto L_0x00e3
            L_0x00e2:
            L_0x00e3:
                if (r1 == 0) goto L_0x00ed
                r1.close()     // Catch:{ all -> 0x00e9 }
                goto L_0x00ed
            L_0x00e9:
                r0 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            L_0x00ed:
                r2.close()     // Catch:{ Exception -> 0x0151 }
                goto L_0x0151
            L_0x00f2:
                r3 = move-exception
                r6 = r2
                r2 = r1
                r1 = r3
                r3 = r6
                goto L_0x0101
            L_0x00f8:
                r2 = move-exception
                r3 = r8
                r6 = r2
                r2 = r1
                r1 = r6
                goto L_0x0101
            L_0x00fe:
                r1 = move-exception
                r2 = r8
                r3 = r2
            L_0x0101:
                boolean r4 = r1 instanceof java.net.SocketTimeoutException     // Catch:{ all -> 0x0152 }
                if (r4 == 0) goto L_0x010e
                boolean r4 = org.telegram.messenger.ApplicationLoader.isNetworkOnline()     // Catch:{ all -> 0x0152 }
                if (r4 == 0) goto L_0x0134
                r7.canRetry = r0     // Catch:{ all -> 0x0152 }
                goto L_0x0134
            L_0x010e:
                boolean r4 = r1 instanceof java.net.UnknownHostException     // Catch:{ all -> 0x0152 }
                if (r4 == 0) goto L_0x0115
                r7.canRetry = r0     // Catch:{ all -> 0x0152 }
                goto L_0x0134
            L_0x0115:
                boolean r4 = r1 instanceof java.net.SocketException     // Catch:{ all -> 0x0152 }
                if (r4 == 0) goto L_0x012e
                java.lang.String r4 = r1.getMessage()     // Catch:{ all -> 0x0152 }
                if (r4 == 0) goto L_0x0134
                java.lang.String r4 = r1.getMessage()     // Catch:{ all -> 0x0152 }
                java.lang.String r5 = "ECONNRESET"
                boolean r4 = r4.contains(r5)     // Catch:{ all -> 0x0152 }
                if (r4 == 0) goto L_0x0134
                r7.canRetry = r0     // Catch:{ all -> 0x0152 }
                goto L_0x0134
            L_0x012e:
                boolean r4 = r1 instanceof java.io.FileNotFoundException     // Catch:{ all -> 0x0152 }
                if (r4 == 0) goto L_0x0134
                r7.canRetry = r0     // Catch:{ all -> 0x0152 }
            L_0x0134:
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)     // Catch:{ all -> 0x0152 }
                java.net.HttpURLConnection r0 = r7.httpConnection     // Catch:{ all -> 0x0141 }
                if (r0 == 0) goto L_0x0142
                java.net.HttpURLConnection r0 = r7.httpConnection     // Catch:{ all -> 0x0141 }
                r0.disconnect()     // Catch:{ all -> 0x0141 }
                goto L_0x0142
            L_0x0141:
            L_0x0142:
                if (r2 == 0) goto L_0x014c
                r2.close()     // Catch:{ all -> 0x0148 }
                goto L_0x014c
            L_0x0148:
                r0 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            L_0x014c:
                if (r3 == 0) goto L_0x0151
                r3.close()     // Catch:{ Exception -> 0x0151 }
            L_0x0151:
                return r8
            L_0x0152:
                r8 = move-exception
                java.net.HttpURLConnection r0 = r7.httpConnection     // Catch:{ all -> 0x015d }
                if (r0 == 0) goto L_0x015e
                java.net.HttpURLConnection r0 = r7.httpConnection     // Catch:{ all -> 0x015d }
                r0.disconnect()     // Catch:{ all -> 0x015d }
                goto L_0x015e
            L_0x015d:
            L_0x015e:
                if (r2 == 0) goto L_0x0168
                r2.close()     // Catch:{ all -> 0x0164 }
                goto L_0x0168
            L_0x0164:
                r0 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            L_0x0168:
                if (r3 == 0) goto L_0x016d
                r3.close()     // Catch:{ Exception -> 0x016d }
            L_0x016d:
                goto L_0x016f
            L_0x016e:
                throw r8
            L_0x016f:
                goto L_0x016e
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

        static /* synthetic */ void lambda$doInBackground$2(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
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

        private void reportProgress(long j, long j2) {
            long currentTimeMillis = System.currentTimeMillis();
            if (j != j2) {
                long j3 = this.lastProgressTime;
                if (j3 != 0 && j3 >= currentTimeMillis - 100) {
                    return;
                }
            }
            this.lastProgressTime = currentTimeMillis;
            Utilities.stageQueue.postRunnable(new Runnable(j, j2) {
                public final /* synthetic */ long f$1;
                public final /* synthetic */ long f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r4;
                }

                public final void run() {
                    ImageLoader.HttpImageTask.this.lambda$reportProgress$1$ImageLoader$HttpImageTask(this.f$1, this.f$2);
                }
            });
        }

        public /* synthetic */ void lambda$reportProgress$1$ImageLoader$HttpImageTask(long j, long j2) {
            ImageLoader.this.fileProgresses.put(this.cacheImage.url, new long[]{j, j2});
            AndroidUtilities.runOnUIThread(new Runnable(j, j2) {
                public final /* synthetic */ long f$1;
                public final /* synthetic */ long f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r4;
                }

                public final void run() {
                    ImageLoader.HttpImageTask.this.lambda$null$0$ImageLoader$HttpImageTask(this.f$1, this.f$2);
                }
            });
        }

        public /* synthetic */ void lambda$null$0$ImageLoader$HttpImageTask(long j, long j2) {
            NotificationCenter.getInstance(this.cacheImage.currentAccount).postNotificationName(NotificationCenter.FileLoadProgressChanged, this.cacheImage.url, Long.valueOf(j), Long.valueOf(j2));
        }

        /* access modifiers changed from: protected */
        /* JADX WARNING: Code restructure failed: missing block: B:89:0x0160, code lost:
            if (r5 != -1) goto L_0x0178;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:92:0x0164, code lost:
            if (r10.imageSize == 0) goto L_0x017f;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:93:0x0166, code lost:
            reportProgress((long) r10.imageSize, (long) r10.imageSize);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:94:0x0170, code lost:
            r2 = move-exception;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:95:0x0171, code lost:
            r0 = r2;
            r2 = true;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:96:0x0174, code lost:
            r2 = move-exception;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:97:0x0175, code lost:
            r0 = r2;
            r2 = true;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:98:0x0178, code lost:
            r0 = false;
         */
        /* JADX WARNING: Removed duplicated region for block: B:109:0x0189 A[Catch:{ all -> 0x0191 }] */
        /* JADX WARNING: Removed duplicated region for block: B:115:0x0199 A[Catch:{ all -> 0x019f }] */
        /* JADX WARNING: Removed duplicated region for block: B:118:0x01a2 A[SYNTHETIC, Splitter:B:118:0x01a2] */
        /* JADX WARNING: Removed duplicated region for block: B:48:0x00e7 A[SYNTHETIC, Splitter:B:48:0x00e7] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public java.lang.Boolean doInBackground(java.lang.Void... r11) {
            /*
                r10 = this;
                boolean r11 = r10.isCancelled()
                r0 = 1
                r1 = 0
                r2 = 0
                if (r11 != 0) goto L_0x00e0
                org.telegram.messenger.ImageLoader$CacheImage r11 = r10.cacheImage     // Catch:{ all -> 0x00a6 }
                org.telegram.messenger.ImageLocation r11 = r11.imageLocation     // Catch:{ all -> 0x00a6 }
                java.lang.String r11 = r11.path     // Catch:{ all -> 0x00a6 }
                java.lang.String r3 = "https://static-maps"
                boolean r3 = r11.startsWith(r3)     // Catch:{ all -> 0x00a6 }
                if (r3 != 0) goto L_0x001f
                java.lang.String r3 = "https://maps.googleapis"
                boolean r3 = r11.startsWith(r3)     // Catch:{ all -> 0x00a6 }
                if (r3 == 0) goto L_0x0057
            L_0x001f:
                org.telegram.messenger.ImageLoader$CacheImage r3 = r10.cacheImage     // Catch:{ all -> 0x00a6 }
                int r3 = r3.currentAccount     // Catch:{ all -> 0x00a6 }
                org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)     // Catch:{ all -> 0x00a6 }
                int r3 = r3.mapProvider     // Catch:{ all -> 0x00a6 }
                r4 = 3
                if (r3 == r4) goto L_0x002f
                r4 = 4
                if (r3 != r4) goto L_0x0057
            L_0x002f:
                org.telegram.messenger.ImageLoader r3 = org.telegram.messenger.ImageLoader.this     // Catch:{ all -> 0x00a6 }
                java.util.concurrent.ConcurrentHashMap r3 = r3.testWebFile     // Catch:{ all -> 0x00a6 }
                java.lang.Object r3 = r3.get(r11)     // Catch:{ all -> 0x00a6 }
                org.telegram.messenger.WebFile r3 = (org.telegram.messenger.WebFile) r3     // Catch:{ all -> 0x00a6 }
                if (r3 == 0) goto L_0x0057
                org.telegram.tgnet.TLRPC$TL_upload_getWebFile r4 = new org.telegram.tgnet.TLRPC$TL_upload_getWebFile     // Catch:{ all -> 0x00a6 }
                r4.<init>()     // Catch:{ all -> 0x00a6 }
                org.telegram.tgnet.TLRPC$InputWebFileLocation r3 = r3.location     // Catch:{ all -> 0x00a6 }
                r4.location = r3     // Catch:{ all -> 0x00a6 }
                r4.offset = r2     // Catch:{ all -> 0x00a6 }
                r4.limit = r2     // Catch:{ all -> 0x00a6 }
                org.telegram.messenger.ImageLoader$CacheImage r3 = r10.cacheImage     // Catch:{ all -> 0x00a6 }
                int r3 = r3.currentAccount     // Catch:{ all -> 0x00a6 }
                org.telegram.tgnet.ConnectionsManager r3 = org.telegram.tgnet.ConnectionsManager.getInstance(r3)     // Catch:{ all -> 0x00a6 }
                org.telegram.messenger.-$$Lambda$ImageLoader$HttpImageTask$T115Ddi3sI3XyS3851ENmLig_I8 r5 = org.telegram.messenger.$$Lambda$ImageLoader$HttpImageTask$T115Ddi3sI3XyS3851ENmLig_I8.INSTANCE     // Catch:{ all -> 0x00a6 }
                r3.sendRequest(r4, r5)     // Catch:{ all -> 0x00a6 }
            L_0x0057:
                java.net.URL r3 = new java.net.URL     // Catch:{ all -> 0x00a6 }
                java.lang.String r4 = r10.overrideUrl     // Catch:{ all -> 0x00a6 }
                if (r4 == 0) goto L_0x005f
                java.lang.String r11 = r10.overrideUrl     // Catch:{ all -> 0x00a6 }
            L_0x005f:
                r3.<init>(r11)     // Catch:{ all -> 0x00a6 }
                java.net.URLConnection r11 = r3.openConnection()     // Catch:{ all -> 0x00a6 }
                java.net.HttpURLConnection r11 = (java.net.HttpURLConnection) r11     // Catch:{ all -> 0x00a6 }
                r10.httpConnection = r11     // Catch:{ all -> 0x00a6 }
                java.lang.String r3 = "User-Agent"
                java.lang.String r4 = "Mozilla/5.0 (iPhone; CPU iPhone OS 10_0 like Mac OS X) AppleWebKit/602.1.38 (KHTML, like Gecko) Version/10.0 Mobile/14A5297c Safari/602.1"
                r11.addRequestProperty(r3, r4)     // Catch:{ all -> 0x00a6 }
                java.net.HttpURLConnection r11 = r10.httpConnection     // Catch:{ all -> 0x00a6 }
                r3 = 5000(0x1388, float:7.006E-42)
                r11.setConnectTimeout(r3)     // Catch:{ all -> 0x00a6 }
                java.net.HttpURLConnection r11 = r10.httpConnection     // Catch:{ all -> 0x00a6 }
                r11.setReadTimeout(r3)     // Catch:{ all -> 0x00a6 }
                java.net.HttpURLConnection r11 = r10.httpConnection     // Catch:{ all -> 0x00a6 }
                r11.setInstanceFollowRedirects(r0)     // Catch:{ all -> 0x00a6 }
                boolean r11 = r10.isCancelled()     // Catch:{ all -> 0x00a6 }
                if (r11 != 0) goto L_0x00e0
                java.net.HttpURLConnection r11 = r10.httpConnection     // Catch:{ all -> 0x00a6 }
                r11.connect()     // Catch:{ all -> 0x00a6 }
                java.net.HttpURLConnection r11 = r10.httpConnection     // Catch:{ all -> 0x00a6 }
                java.io.InputStream r11 = r11.getInputStream()     // Catch:{ all -> 0x00a6 }
                java.io.RandomAccessFile r3 = new java.io.RandomAccessFile     // Catch:{ all -> 0x00a1 }
                org.telegram.messenger.ImageLoader$CacheImage r4 = r10.cacheImage     // Catch:{ all -> 0x00a1 }
                java.io.File r4 = r4.tempFilePath     // Catch:{ all -> 0x00a1 }
                java.lang.String r5 = "rws"
                r3.<init>(r4, r5)     // Catch:{ all -> 0x00a1 }
                r10.fileOutputStream = r3     // Catch:{ all -> 0x00a1 }
                goto L_0x00e1
            L_0x00a1:
                r3 = move-exception
                r9 = r3
                r3 = r11
                r11 = r9
                goto L_0x00a8
            L_0x00a6:
                r11 = move-exception
                r3 = r1
            L_0x00a8:
                boolean r4 = r11 instanceof java.net.SocketTimeoutException
                if (r4 == 0) goto L_0x00b5
                boolean r4 = org.telegram.messenger.ApplicationLoader.isNetworkOnline()
                if (r4 == 0) goto L_0x00db
                r10.canRetry = r2
                goto L_0x00db
            L_0x00b5:
                boolean r4 = r11 instanceof java.net.UnknownHostException
                if (r4 == 0) goto L_0x00bc
                r10.canRetry = r2
                goto L_0x00db
            L_0x00bc:
                boolean r4 = r11 instanceof java.net.SocketException
                if (r4 == 0) goto L_0x00d5
                java.lang.String r4 = r11.getMessage()
                if (r4 == 0) goto L_0x00db
                java.lang.String r4 = r11.getMessage()
                java.lang.String r5 = "ECONNRESET"
                boolean r4 = r4.contains(r5)
                if (r4 == 0) goto L_0x00db
                r10.canRetry = r2
                goto L_0x00db
            L_0x00d5:
                boolean r4 = r11 instanceof java.io.FileNotFoundException
                if (r4 == 0) goto L_0x00db
                r10.canRetry = r2
            L_0x00db:
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r11)
                r11 = r3
                goto L_0x00e1
            L_0x00e0:
                r11 = r1
            L_0x00e1:
                boolean r3 = r10.isCancelled()
                if (r3 != 0) goto L_0x0185
                java.net.HttpURLConnection r3 = r10.httpConnection     // Catch:{ Exception -> 0x0100 }
                if (r3 == 0) goto L_0x0104
                java.net.HttpURLConnection r3 = r10.httpConnection     // Catch:{ Exception -> 0x0100 }
                int r3 = r3.getResponseCode()     // Catch:{ Exception -> 0x0100 }
                r4 = 200(0xc8, float:2.8E-43)
                if (r3 == r4) goto L_0x0104
                r4 = 202(0xca, float:2.83E-43)
                if (r3 == r4) goto L_0x0104
                r4 = 304(0x130, float:4.26E-43)
                if (r3 == r4) goto L_0x0104
                r10.canRetry = r2     // Catch:{ Exception -> 0x0100 }
                goto L_0x0104
            L_0x0100:
                r3 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r3)
            L_0x0104:
                int r3 = r10.imageSize
                if (r3 != 0) goto L_0x0139
                java.net.HttpURLConnection r3 = r10.httpConnection
                if (r3 == 0) goto L_0x0139
                java.util.Map r3 = r3.getHeaderFields()     // Catch:{ Exception -> 0x0135 }
                if (r3 == 0) goto L_0x0139
                java.lang.String r4 = "content-Length"
                java.lang.Object r3 = r3.get(r4)     // Catch:{ Exception -> 0x0135 }
                java.util.List r3 = (java.util.List) r3     // Catch:{ Exception -> 0x0135 }
                if (r3 == 0) goto L_0x0139
                boolean r4 = r3.isEmpty()     // Catch:{ Exception -> 0x0135 }
                if (r4 != 0) goto L_0x0139
                java.lang.Object r3 = r3.get(r2)     // Catch:{ Exception -> 0x0135 }
                java.lang.String r3 = (java.lang.String) r3     // Catch:{ Exception -> 0x0135 }
                if (r3 == 0) goto L_0x0139
                java.lang.Integer r3 = org.telegram.messenger.Utilities.parseInt(r3)     // Catch:{ Exception -> 0x0135 }
                int r3 = r3.intValue()     // Catch:{ Exception -> 0x0135 }
                r10.imageSize = r3     // Catch:{ Exception -> 0x0135 }
                goto L_0x0139
            L_0x0135:
                r3 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r3)
            L_0x0139:
                if (r11 == 0) goto L_0x0185
                r3 = 8192(0x2000, float:1.14794E-41)
                byte[] r3 = new byte[r3]     // Catch:{ all -> 0x0181 }
                r4 = 0
            L_0x0140:
                boolean r5 = r10.isCancelled()     // Catch:{ all -> 0x0181 }
                if (r5 == 0) goto L_0x0147
                goto L_0x0178
            L_0x0147:
                int r5 = r11.read(r3)     // Catch:{ Exception -> 0x017a }
                if (r5 <= 0) goto L_0x015f
                int r4 = r4 + r5
                java.io.RandomAccessFile r6 = r10.fileOutputStream     // Catch:{ Exception -> 0x017a }
                r6.write(r3, r2, r5)     // Catch:{ Exception -> 0x017a }
                int r5 = r10.imageSize     // Catch:{ Exception -> 0x017a }
                if (r5 == 0) goto L_0x0140
                long r5 = (long) r4     // Catch:{ Exception -> 0x017a }
                int r7 = r10.imageSize     // Catch:{ Exception -> 0x017a }
                long r7 = (long) r7     // Catch:{ Exception -> 0x017a }
                r10.reportProgress(r5, r7)     // Catch:{ Exception -> 0x017a }
                goto L_0x0140
            L_0x015f:
                r3 = -1
                if (r5 != r3) goto L_0x0178
                int r2 = r10.imageSize     // Catch:{ Exception -> 0x0174, all -> 0x0170 }
                if (r2 == 0) goto L_0x017f
                int r2 = r10.imageSize     // Catch:{ Exception -> 0x0174, all -> 0x0170 }
                long r2 = (long) r2     // Catch:{ Exception -> 0x0174, all -> 0x0170 }
                int r4 = r10.imageSize     // Catch:{ Exception -> 0x0174, all -> 0x0170 }
                long r4 = (long) r4     // Catch:{ Exception -> 0x0174, all -> 0x0170 }
                r10.reportProgress(r2, r4)     // Catch:{ Exception -> 0x0174, all -> 0x0170 }
                goto L_0x017f
            L_0x0170:
                r2 = move-exception
                r0 = r2
                r2 = 1
                goto L_0x0182
            L_0x0174:
                r2 = move-exception
                r0 = r2
                r2 = 1
                goto L_0x017b
            L_0x0178:
                r0 = 0
                goto L_0x017f
            L_0x017a:
                r0 = move-exception
            L_0x017b:
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ all -> 0x0181 }
                r0 = r2
            L_0x017f:
                r2 = r0
                goto L_0x0185
            L_0x0181:
                r0 = move-exception
            L_0x0182:
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            L_0x0185:
                java.io.RandomAccessFile r0 = r10.fileOutputStream     // Catch:{ all -> 0x0191 }
                if (r0 == 0) goto L_0x0195
                java.io.RandomAccessFile r0 = r10.fileOutputStream     // Catch:{ all -> 0x0191 }
                r0.close()     // Catch:{ all -> 0x0191 }
                r10.fileOutputStream = r1     // Catch:{ all -> 0x0191 }
                goto L_0x0195
            L_0x0191:
                r0 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            L_0x0195:
                java.net.HttpURLConnection r0 = r10.httpConnection     // Catch:{ all -> 0x019f }
                if (r0 == 0) goto L_0x01a0
                java.net.HttpURLConnection r0 = r10.httpConnection     // Catch:{ all -> 0x019f }
                r0.disconnect()     // Catch:{ all -> 0x019f }
                goto L_0x01a0
            L_0x019f:
            L_0x01a0:
                if (r11 == 0) goto L_0x01aa
                r11.close()     // Catch:{ all -> 0x01a6 }
                goto L_0x01aa
            L_0x01a6:
                r11 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r11)
            L_0x01aa:
                if (r2 == 0) goto L_0x01c0
                org.telegram.messenger.ImageLoader$CacheImage r11 = r10.cacheImage
                java.io.File r0 = r11.tempFilePath
                if (r0 == 0) goto L_0x01c0
                java.io.File r11 = r11.finalFilePath
                boolean r11 = r0.renameTo(r11)
                if (r11 != 0) goto L_0x01c0
                org.telegram.messenger.ImageLoader$CacheImage r11 = r10.cacheImage
                java.io.File r0 = r11.tempFilePath
                r11.finalFilePath = r0
            L_0x01c0:
                java.lang.Boolean r11 = java.lang.Boolean.valueOf(r2)
                return r11
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
                public final /* synthetic */ Boolean f$1;

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
                public final /* synthetic */ Boolean f$1;

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
                    public final /* synthetic */ String f$1;

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
            Bitmap createScaledBitmap;
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
                        Bitmap bitmap = null;
                        if (this.mediaType == 0) {
                            float f = (float) max;
                            bitmap = ImageLoader.loadBitmap(this.originalPath.toString(), (Uri) null, f, f, false);
                        } else {
                            int i = 2;
                            if (this.mediaType == 2) {
                                String file2 = this.originalPath.toString();
                                if (!this.info.big) {
                                    i = 1;
                                }
                                bitmap = SendMessagesHelper.createVideoThumbnail(file2, i);
                            } else if (this.mediaType == 3) {
                                String lowerCase = this.originalPath.toString().toLowerCase();
                                if (lowerCase.endsWith("mp4")) {
                                    String file3 = this.originalPath.toString();
                                    if (!this.info.big) {
                                        i = 1;
                                    }
                                    bitmap = SendMessagesHelper.createVideoThumbnail(file3, i);
                                } else if (lowerCase.endsWith(".jpg") || lowerCase.endsWith(".jpeg") || lowerCase.endsWith(".png") || lowerCase.endsWith(".gif")) {
                                    float f2 = (float) max;
                                    bitmap = ImageLoader.loadBitmap(lowerCase, (Uri) null, f2, f2, false);
                                }
                            }
                        }
                        if (bitmap == null) {
                            removeTask();
                            return;
                        }
                        int width = bitmap.getWidth();
                        int height = bitmap.getHeight();
                        if (width != 0) {
                            if (height != 0) {
                                float f3 = (float) width;
                                float f4 = (float) max;
                                float f5 = (float) height;
                                float min = Math.min(f3 / f4, f5 / f4);
                                if (min > 1.0f && (createScaledBitmap = Bitmaps.createScaledBitmap(bitmap, (int) (f3 / min), (int) (f5 / min), true)) != bitmap) {
                                    bitmap.recycle();
                                    bitmap = createScaledBitmap;
                                }
                                FileOutputStream fileOutputStream = new FileOutputStream(file);
                                bitmap.compress(Bitmap.CompressFormat.JPEG, this.info.big ? 83 : 60, fileOutputStream);
                                fileOutputStream.close();
                                AndroidUtilities.runOnUIThread(new Runnable(str, new ArrayList(this.info.imageReceiverArray), new BitmapDrawable(bitmap), new ArrayList(this.info.imageReceiverGuidsArray)) {
                                    public final /* synthetic */ String f$1;
                                    public final /* synthetic */ ArrayList f$2;
                                    public final /* synthetic */ BitmapDrawable f$3;
                                    public final /* synthetic */ ArrayList f$4;

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

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v4, resolved type: org.telegram.messenger.ExtendedBitmapDrawable} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v5, resolved type: org.telegram.messenger.ExtendedBitmapDrawable} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v17, resolved type: android.graphics.drawable.Drawable} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v18, resolved type: android.graphics.drawable.Drawable} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v19, resolved type: android.graphics.drawable.Drawable} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v31, resolved type: android.graphics.Rect} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v32, resolved type: android.graphics.Rect} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v35, resolved type: android.graphics.Rect} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v111, resolved type: org.telegram.messenger.ExtendedBitmapDrawable} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v112, resolved type: android.graphics.drawable.BitmapDrawable} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v125, resolved type: android.graphics.drawable.Drawable} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v126, resolved type: android.graphics.drawable.Drawable} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v127, resolved type: android.graphics.drawable.Drawable} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v128, resolved type: android.graphics.drawable.Drawable} */
        /* JADX WARNING: type inference failed for: r11v24 */
        /* JADX WARNING: type inference failed for: r11v26 */
        /* JADX WARNING: type inference failed for: r11v27 */
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
            	at jadx.core.dex.visitors.regions.RegionMaker.processIf(RegionMaker.java:693)
            	at jadx.core.dex.visitors.regions.RegionMaker.traverse(RegionMaker.java:123)
            	at jadx.core.dex.visitors.regions.RegionMaker.makeRegion(RegionMaker.java:86)
            	at jadx.core.dex.visitors.regions.RegionMaker.processIf(RegionMaker.java:693)
            	at jadx.core.dex.visitors.regions.RegionMaker.traverse(RegionMaker.java:123)
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
                r34 = this;
                r1 = r34
                java.lang.Object r2 = r1.sync
                monitor-enter(r2)
                java.lang.Thread r0 = java.lang.Thread.currentThread()     // Catch:{ all -> 0x0a96 }
                r1.runningThread = r0     // Catch:{ all -> 0x0a96 }
                java.lang.Thread.interrupted()     // Catch:{ all -> 0x0a96 }
                boolean r0 = r1.isCancelled     // Catch:{ all -> 0x0a96 }
                if (r0 == 0) goto L_0x0014
                monitor-exit(r2)     // Catch:{ all -> 0x0a96 }
                return
            L_0x0014:
                monitor-exit(r2)     // Catch:{ all -> 0x0a96 }
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage
                org.telegram.messenger.ImageLocation r2 = r0.imageLocation
                org.telegram.tgnet.TLRPC$PhotoSize r3 = r2.photoSize
                boolean r4 = r3 instanceof org.telegram.tgnet.TLRPC$TL_photoStrippedSize
                if (r4 == 0) goto L_0x0037
                org.telegram.tgnet.TLRPC$TL_photoStrippedSize r3 = (org.telegram.tgnet.TLRPC$TL_photoStrippedSize) r3
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
                goto L_0x0a95
            L_0x0037:
                int r3 = r0.imageType
                r4 = 5
                if (r3 != r4) goto L_0x0059
                org.telegram.ui.Components.ThemePreviewDrawable r0 = new org.telegram.ui.Components.ThemePreviewDrawable     // Catch:{ all -> 0x004f }
                org.telegram.messenger.ImageLoader$CacheImage r2 = r1.cacheImage     // Catch:{ all -> 0x004f }
                java.io.File r2 = r2.finalFilePath     // Catch:{ all -> 0x004f }
                org.telegram.messenger.ImageLoader$CacheImage r3 = r1.cacheImage     // Catch:{ all -> 0x004f }
                org.telegram.messenger.ImageLocation r3 = r3.imageLocation     // Catch:{ all -> 0x004f }
                org.telegram.tgnet.TLRPC$Document r3 = r3.document     // Catch:{ all -> 0x004f }
                org.telegram.messenger.DocumentObject$ThemeDocument r3 = (org.telegram.messenger.DocumentObject.ThemeDocument) r3     // Catch:{ all -> 0x004f }
                r0.<init>(r2, r3)     // Catch:{ all -> 0x004f }
                r5 = r0
                goto L_0x0054
            L_0x004f:
                r0 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
                r5 = 0
            L_0x0054:
                r1.onPostExecute(r5)
                goto L_0x0a95
            L_0x0059:
                r6 = 4
                r7 = 3
                r8 = 2
                r9 = 1
                r10 = 0
                if (r3 == r7) goto L_0x0a3e
                if (r3 != r6) goto L_0x0064
                goto L_0x0a3e
            L_0x0064:
                r11 = 8
                if (r3 != r9) goto L_0x017d
                r0 = 1126865306(0x432a999a, float:170.6)
                int r2 = org.telegram.messenger.AndroidUtilities.dp(r0)
                r3 = 512(0x200, float:7.175E-43)
                int r2 = java.lang.Math.min(r3, r2)
                int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
                int r0 = java.lang.Math.min(r3, r0)
                org.telegram.messenger.ImageLoader$CacheImage r12 = r1.cacheImage
                java.lang.String r12 = r12.filter
                if (r12 == 0) goto L_0x015b
                java.lang.String r13 = "_"
                java.lang.String[] r12 = r12.split(r13)
                int r13 = r12.length
                if (r13 < r8) goto L_0x00cb
                r0 = r12[r10]
                float r0 = java.lang.Float.parseFloat(r0)
                r2 = r12[r9]
                float r2 = java.lang.Float.parseFloat(r2)
                float r13 = org.telegram.messenger.AndroidUtilities.density
                float r13 = r13 * r0
                int r13 = (int) r13
                int r13 = java.lang.Math.min(r3, r13)
                float r14 = org.telegram.messenger.AndroidUtilities.density
                float r14 = r14 * r2
                int r14 = (int) r14
                int r3 = java.lang.Math.min(r3, r14)
                r14 = 1119092736(0x42b40000, float:90.0)
                int r0 = (r0 > r14 ? 1 : (r0 == r14 ? 0 : -1))
                if (r0 > 0) goto L_0x00c0
                int r0 = (r2 > r14 ? 1 : (r2 == r14 ? 0 : -1))
                if (r0 > 0) goto L_0x00c0
                r0 = 160(0xa0, float:2.24E-43)
                int r2 = java.lang.Math.min(r13, r0)
                int r0 = java.lang.Math.min(r3, r0)
                r3 = 1
                goto L_0x00c3
            L_0x00c0:
                r0 = r3
                r2 = r13
                r3 = 0
            L_0x00c3:
                int r13 = org.telegram.messenger.SharedConfig.getDevicePerformanceClass()
                if (r13 == r8) goto L_0x00cc
                r10 = 1
                goto L_0x00cc
            L_0x00cb:
                r3 = 0
            L_0x00cc:
                int r13 = r12.length
                if (r13 < r7) goto L_0x00f5
                java.lang.String r13 = "nr"
                r14 = r12[r8]
                boolean r13 = r13.equals(r14)
                if (r13 == 0) goto L_0x00db
                r7 = 2
                goto L_0x00f6
            L_0x00db:
                java.lang.String r13 = "nrs"
                r14 = r12[r8]
                boolean r13 = r13.equals(r14)
                if (r13 == 0) goto L_0x00e6
                goto L_0x00f6
            L_0x00e6:
                java.lang.String r13 = "dice"
                r14 = r12[r8]
                boolean r13 = r13.equals(r14)
                if (r13 == 0) goto L_0x00f5
                r7 = r12[r7]
                r8 = r7
                r7 = 2
                goto L_0x00f7
            L_0x00f5:
                r7 = 1
            L_0x00f6:
                r8 = 0
            L_0x00f7:
                int r9 = r12.length
                if (r9 < r4) goto L_0x0153
                java.lang.String r4 = "c1"
                r9 = r12[r6]
                boolean r4 = r4.equals(r9)
                if (r4 == 0) goto L_0x0113
                int[] r5 = new int[r11]
                r5 = {16219713, 13275258, 16757049, 15582629, 16765248, 16245699, 16768889, 16510934} // fill-array
            L_0x0109:
                r14 = r0
                r13 = r2
                r16 = r3
                r17 = r5
                r9 = r7
                r5 = r8
                r15 = r10
                goto L_0x0163
            L_0x0113:
                java.lang.String r4 = "c2"
                r9 = r12[r6]
                boolean r4 = r4.equals(r9)
                if (r4 == 0) goto L_0x0123
                int[] r5 = new int[r11]
                r5 = {16219713, 11172960, 16757049, 13150599, 16765248, 14534815, 16768889, 15128242} // fill-array
                goto L_0x0109
            L_0x0123:
                java.lang.String r4 = "c3"
                r9 = r12[r6]
                boolean r4 = r4.equals(r9)
                if (r4 == 0) goto L_0x0133
                int[] r5 = new int[r11]
                r5 = {16219713, 9199944, 16757049, 11371874, 16765248, 12885622, 16768889, 13939080} // fill-array
                goto L_0x0109
            L_0x0133:
                java.lang.String r4 = "c4"
                r9 = r12[r6]
                boolean r4 = r4.equals(r9)
                if (r4 == 0) goto L_0x0143
                int[] r5 = new int[r11]
                r5 = {16219713, 7224364, 16757049, 9591348, 16765248, 10579526, 16768889, 11303506} // fill-array
                goto L_0x0109
            L_0x0143:
                java.lang.String r4 = "c5"
                r6 = r12[r6]
                boolean r4 = r4.equals(r6)
                if (r4 == 0) goto L_0x0153
                int[] r5 = new int[r11]
                r5 = {16219713, 2694162, 16757049, 4663842, 16765248, 5716784, 16768889, 6834492} // fill-array
                goto L_0x0109
            L_0x0153:
                r14 = r0
                r13 = r2
                r16 = r3
                r9 = r7
                r5 = r8
                r15 = r10
                goto L_0x0161
            L_0x015b:
                r14 = r0
                r13 = r2
                r5 = 0
                r15 = 0
                r16 = 0
            L_0x0161:
                r17 = 0
            L_0x0163:
                if (r5 == 0) goto L_0x016b
                org.telegram.ui.Components.RLottieDrawable r0 = new org.telegram.ui.Components.RLottieDrawable
                r0.<init>(r5, r13, r14)
                goto L_0x0175
            L_0x016b:
                org.telegram.ui.Components.RLottieDrawable r0 = new org.telegram.ui.Components.RLottieDrawable
                org.telegram.messenger.ImageLoader$CacheImage r2 = r1.cacheImage
                java.io.File r12 = r2.finalFilePath
                r11 = r0
                r11.<init>((java.io.File) r12, (int) r13, (int) r14, (boolean) r15, (boolean) r16, (int[]) r17)
            L_0x0175:
                r0.setAutoRepeat(r9)
                r1.onPostExecute(r0)
                goto L_0x0a95
            L_0x017d:
                if (r3 != r8) goto L_0x0201
                if (r2 == 0) goto L_0x0186
                long r12 = r2.videoSeekTo
                r22 = r12
                goto L_0x0188
            L_0x0186:
                r22 = 0
            L_0x0188:
                java.lang.String r0 = "g"
                org.telegram.messenger.ImageLoader$CacheImage r2 = r1.cacheImage
                java.lang.String r2 = r2.filter
                boolean r0 = r0.equals(r2)
                if (r0 == 0) goto L_0x01d7
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage
                org.telegram.messenger.ImageLocation r0 = r0.imageLocation
                org.telegram.tgnet.TLRPC$Document r0 = r0.document
                boolean r2 = r0 instanceof org.telegram.tgnet.TLRPC$TL_documentEncrypted
                if (r2 != 0) goto L_0x01d7
                boolean r2 = r0 instanceof org.telegram.tgnet.TLRPC$Document
                if (r2 == 0) goto L_0x01a5
                r19 = r0
                goto L_0x01a7
            L_0x01a5:
                r19 = 0
            L_0x01a7:
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage
                if (r19 == 0) goto L_0x01ae
                int r0 = r0.size
                goto L_0x01b2
            L_0x01ae:
                org.telegram.messenger.ImageLocation r0 = r0.imageLocation
                int r0 = r0.currentSize
            L_0x01b2:
                org.telegram.ui.Components.AnimatedFileDrawable r2 = new org.telegram.ui.Components.AnimatedFileDrawable
                org.telegram.messenger.ImageLoader$CacheImage r3 = r1.cacheImage
                java.io.File r15 = r3.finalFilePath
                r16 = 0
                long r6 = (long) r0
                if (r19 != 0) goto L_0x01c2
                org.telegram.messenger.ImageLocation r5 = r3.imageLocation
                r20 = r5
                goto L_0x01c4
            L_0x01c2:
                r20 = 0
            L_0x01c4:
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage
                java.lang.Object r3 = r0.parentObject
                int r0 = r0.currentAccount
                r25 = 0
                r14 = r2
                r17 = r6
                r21 = r3
                r24 = r0
                r14.<init>(r15, r16, r17, r19, r20, r21, r22, r24, r25)
                goto L_0x01f9
            L_0x01d7:
                org.telegram.ui.Components.AnimatedFileDrawable r2 = new org.telegram.ui.Components.AnimatedFileDrawable
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage
                java.io.File r15 = r0.finalFilePath
                java.lang.String r3 = "d"
                java.lang.String r0 = r0.filter
                boolean r16 = r3.equals(r0)
                r17 = 0
                r19 = 0
                r20 = 0
                r21 = 0
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage
                int r0 = r0.currentAccount
                r25 = 0
                r14 = r2
                r24 = r0
                r14.<init>(r15, r16, r17, r19, r20, r21, r22, r24, r25)
            L_0x01f9:
                java.lang.Thread.interrupted()
                r1.onPostExecute(r2)
                goto L_0x0a95
            L_0x0201:
                java.io.File r2 = r0.finalFilePath
                org.telegram.messenger.SecureDocument r3 = r0.secureDocument
                if (r3 != 0) goto L_0x021c
                java.io.File r0 = r0.encryptionKeyPath
                if (r0 == 0) goto L_0x021a
                if (r2 == 0) goto L_0x021a
                java.lang.String r0 = r2.getAbsolutePath()
                java.lang.String r3 = ".enc"
                boolean r0 = r0.endsWith(r3)
                if (r0 == 0) goto L_0x021a
                goto L_0x021c
            L_0x021a:
                r3 = 0
                goto L_0x021d
            L_0x021c:
                r3 = 1
            L_0x021d:
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage
                org.telegram.messenger.SecureDocument r0 = r0.secureDocument
                if (r0 == 0) goto L_0x0237
                org.telegram.messenger.SecureDocumentKey r4 = r0.secureDocumentKey
                org.telegram.tgnet.TLRPC$TL_secureFile r0 = r0.secureFile
                if (r0 == 0) goto L_0x022e
                byte[] r0 = r0.file_hash
                if (r0 == 0) goto L_0x022e
                goto L_0x0234
            L_0x022e:
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage
                org.telegram.messenger.SecureDocument r0 = r0.secureDocument
                byte[] r0 = r0.fileHash
            L_0x0234:
                r6 = r4
                r4 = r0
                goto L_0x0239
            L_0x0237:
                r4 = 0
                r6 = 0
            L_0x0239:
                int r0 = android.os.Build.VERSION.SDK_INT
                r14 = 19
                if (r0 >= r14) goto L_0x02a9
                java.io.RandomAccessFile r14 = new java.io.RandomAccessFile     // Catch:{ Exception -> 0x028e, all -> 0x028a }
                java.lang.String r0 = "r"
                r14.<init>(r2, r0)     // Catch:{ Exception -> 0x028e, all -> 0x028a }
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage     // Catch:{ Exception -> 0x0288 }
                int r0 = r0.type     // Catch:{ Exception -> 0x0288 }
                if (r0 != r9) goto L_0x0251
                byte[] r0 = org.telegram.messenger.ImageLoader.headerThumb     // Catch:{ Exception -> 0x0288 }
                goto L_0x0255
            L_0x0251:
                byte[] r0 = org.telegram.messenger.ImageLoader.header     // Catch:{ Exception -> 0x0288 }
            L_0x0255:
                int r15 = r0.length     // Catch:{ Exception -> 0x0288 }
                r14.readFully(r0, r10, r15)     // Catch:{ Exception -> 0x0288 }
                java.lang.String r15 = new java.lang.String     // Catch:{ Exception -> 0x0288 }
                r15.<init>(r0)     // Catch:{ Exception -> 0x0288 }
                java.lang.String r0 = r15.toLowerCase()     // Catch:{ Exception -> 0x0288 }
                java.lang.String r0 = r0.toLowerCase()     // Catch:{ Exception -> 0x0288 }
                java.lang.String r15 = "riff"
                boolean r15 = r0.startsWith(r15)     // Catch:{ Exception -> 0x0288 }
                if (r15 == 0) goto L_0x0278
                java.lang.String r15 = "webp"
                boolean r0 = r0.endsWith(r15)     // Catch:{ Exception -> 0x0288 }
                if (r0 == 0) goto L_0x0278
                r15 = 1
                goto L_0x0279
            L_0x0278:
                r15 = 0
            L_0x0279:
                r14.close()     // Catch:{ Exception -> 0x0286 }
                r14.close()     // Catch:{ Exception -> 0x0280 }
                goto L_0x02aa
            L_0x0280:
                r0 = move-exception
                r14 = r0
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r14)
                goto L_0x02aa
            L_0x0286:
                r0 = move-exception
                goto L_0x0291
            L_0x0288:
                r0 = move-exception
                goto L_0x0290
            L_0x028a:
                r0 = move-exception
                r2 = r0
                r5 = 0
                goto L_0x029d
            L_0x028e:
                r0 = move-exception
                r14 = 0
            L_0x0290:
                r15 = 0
            L_0x0291:
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ all -> 0x029a }
                if (r14 == 0) goto L_0x02aa
                r14.close()     // Catch:{ Exception -> 0x0280 }
                goto L_0x02aa
            L_0x029a:
                r0 = move-exception
                r2 = r0
                r5 = r14
            L_0x029d:
                if (r5 == 0) goto L_0x02a8
                r5.close()     // Catch:{ Exception -> 0x02a3 }
                goto L_0x02a8
            L_0x02a3:
                r0 = move-exception
                r3 = r0
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r3)
            L_0x02a8:
                throw r2
            L_0x02a9:
                r15 = 0
            L_0x02aa:
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage
                org.telegram.messenger.ImageLocation r0 = r0.imageLocation
                java.lang.String r0 = r0.path
                if (r0 == 0) goto L_0x0312
                java.lang.String r14 = "thumb://"
                boolean r14 = r0.startsWith(r14)
                if (r14 == 0) goto L_0x02de
                java.lang.String r14 = ":"
                int r14 = r0.indexOf(r14, r11)
                if (r14 < 0) goto L_0x02d4
                java.lang.String r16 = r0.substring(r11, r14)
                long r16 = java.lang.Long.parseLong(r16)
                java.lang.Long r16 = java.lang.Long.valueOf(r16)
                int r14 = r14 + r9
                java.lang.String r0 = r0.substring(r14)
                goto L_0x02d7
            L_0x02d4:
                r0 = 0
                r16 = 0
            L_0x02d7:
                r11 = r0
                r17 = r16
                r14 = 0
            L_0x02db:
                r18 = 0
                goto L_0x0318
            L_0x02de:
                java.lang.String r14 = "vthumb://"
                boolean r14 = r0.startsWith(r14)
                if (r14 == 0) goto L_0x0305
                java.lang.String r14 = ":"
                r11 = 9
                int r14 = r0.indexOf(r14, r11)
                if (r14 < 0) goto L_0x02fe
                java.lang.String r0 = r0.substring(r11, r14)
                long r17 = java.lang.Long.parseLong(r0)
                java.lang.Long r0 = java.lang.Long.valueOf(r17)
                r11 = 1
                goto L_0x0300
            L_0x02fe:
                r0 = 0
                r11 = 0
            L_0x0300:
                r17 = r0
                r14 = r11
                r11 = 0
                goto L_0x02db
            L_0x0305:
                java.lang.String r11 = "http"
                boolean r0 = r0.startsWith(r11)
                if (r0 != 0) goto L_0x0312
                r11 = 0
                r14 = 0
                r17 = 0
                goto L_0x02db
            L_0x0312:
                r11 = 0
                r14 = 0
                r17 = 0
                r18 = 1
            L_0x0318:
                android.graphics.BitmapFactory$Options r7 = new android.graphics.BitmapFactory$Options
                r7.<init>()
                r7.inSampleSize = r9
                int r0 = android.os.Build.VERSION.SDK_INT
                r12 = 21
                if (r0 >= r12) goto L_0x0327
                r7.inPurgeable = r9
            L_0x0327:
                org.telegram.messenger.ImageLoader r0 = org.telegram.messenger.ImageLoader.this
                boolean r13 = r0.canForce8888
                r22 = 0
                r23 = 1065353216(0x3var_, float:1.0)
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage     // Catch:{ all -> 0x0546 }
                java.lang.String r0 = r0.filter     // Catch:{ all -> 0x0546 }
                if (r0 == 0) goto L_0x04ea
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage     // Catch:{ all -> 0x0546 }
                java.lang.String r0 = r0.filter     // Catch:{ all -> 0x0546 }
                java.lang.String r12 = "_"
                java.lang.String[] r0 = r0.split(r12)     // Catch:{ all -> 0x0546 }
                int r12 = r0.length     // Catch:{ all -> 0x0546 }
                if (r12 < r8) goto L_0x036c
                r12 = r0[r10]     // Catch:{ all -> 0x0362 }
                float r12 = java.lang.Float.parseFloat(r12)     // Catch:{ all -> 0x0362 }
                float r25 = org.telegram.messenger.AndroidUtilities.density     // Catch:{ all -> 0x0362 }
                float r12 = r12 * r25
                r0 = r0[r9]     // Catch:{ all -> 0x035c }
                float r0 = java.lang.Float.parseFloat(r0)     // Catch:{ all -> 0x035c }
                float r25 = org.telegram.messenger.AndroidUtilities.density     // Catch:{ all -> 0x035c }
                float r0 = r0 * r25
                r25 = r12
                r12 = r0
                goto L_0x036f
            L_0x035c:
                r0 = move-exception
                r10 = r14
                r5 = 0
                r8 = 0
                r9 = 0
                goto L_0x0368
            L_0x0362:
                r0 = move-exception
                r10 = r14
                r5 = 0
                r8 = 0
                r9 = 0
                r12 = 0
            L_0x0368:
                r14 = r0
                r0 = 0
                goto L_0x054e
            L_0x036c:
                r12 = 0
                r25 = 0
            L_0x036f:
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage     // Catch:{ all -> 0x04df }
                java.lang.String r0 = r0.filter     // Catch:{ all -> 0x04df }
                java.lang.String r8 = "b2"
                boolean r0 = r0.contains(r8)     // Catch:{ all -> 0x04df }
                if (r0 == 0) goto L_0x037d
                r8 = 3
                goto L_0x039a
            L_0x037d:
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage     // Catch:{ all -> 0x04df }
                java.lang.String r0 = r0.filter     // Catch:{ all -> 0x04df }
                java.lang.String r8 = "b1"
                boolean r0 = r0.contains(r8)     // Catch:{ all -> 0x04df }
                if (r0 == 0) goto L_0x038b
                r8 = 2
                goto L_0x039a
            L_0x038b:
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage     // Catch:{ all -> 0x04df }
                java.lang.String r0 = r0.filter     // Catch:{ all -> 0x04df }
                java.lang.String r8 = "b"
                boolean r0 = r0.contains(r8)     // Catch:{ all -> 0x04df }
                if (r0 == 0) goto L_0x0399
                r8 = 1
                goto L_0x039a
            L_0x0399:
                r8 = 0
            L_0x039a:
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage     // Catch:{ all -> 0x04d7 }
                java.lang.String r0 = r0.filter     // Catch:{ all -> 0x04d7 }
                java.lang.String r5 = "i"
                boolean r5 = r0.contains(r5)     // Catch:{ all -> 0x04d7 }
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage     // Catch:{ all -> 0x04ce }
                java.lang.String r0 = r0.filter     // Catch:{ all -> 0x04ce }
                java.lang.String r10 = "f"
                boolean r0 = r0.contains(r10)     // Catch:{ all -> 0x04ce }
                if (r0 == 0) goto L_0x03b1
                r13 = 1
            L_0x03b1:
                if (r15 != 0) goto L_0x04c2
                int r0 = (r25 > r22 ? 1 : (r25 == r22 ? 0 : -1))
                if (r0 == 0) goto L_0x04c2
                int r0 = (r12 > r22 ? 1 : (r12 == r22 ? 0 : -1))
                if (r0 == 0) goto L_0x04c2
                r7.inJustDecodeBounds = r9     // Catch:{ all -> 0x04bc }
                if (r17 == 0) goto L_0x03ed
                if (r11 != 0) goto L_0x03ed
                if (r14 == 0) goto L_0x03d7
                android.content.Context r0 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x03d4 }
                android.content.ContentResolver r0 = r0.getContentResolver()     // Catch:{ all -> 0x03d4 }
                r28 = r13
                r10 = r14
                long r13 = r17.longValue()     // Catch:{ all -> 0x03eb }
                android.provider.MediaStore.Video.Thumbnails.getThumbnail(r0, r13, r9, r7)     // Catch:{ all -> 0x03eb }
                goto L_0x03e7
            L_0x03d4:
                r0 = move-exception
                goto L_0x04bf
            L_0x03d7:
                r28 = r13
                r10 = r14
                android.content.Context r0 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x03eb }
                android.content.ContentResolver r0 = r0.getContentResolver()     // Catch:{ all -> 0x03eb }
                long r13 = r17.longValue()     // Catch:{ all -> 0x03eb }
                android.provider.MediaStore.Images.Thumbnails.getThumbnail(r0, r13, r9, r7)     // Catch:{ all -> 0x03eb }
            L_0x03e7:
                r29 = r5
                goto L_0x0469
            L_0x03eb:
                r0 = move-exception
                goto L_0x0447
            L_0x03ed:
                r28 = r13
                r10 = r14
                if (r6 == 0) goto L_0x044f
                java.io.RandomAccessFile r0 = new java.io.RandomAccessFile     // Catch:{ all -> 0x0444 }
                java.lang.String r13 = "r"
                r0.<init>(r2, r13)     // Catch:{ all -> 0x0444 }
                long r13 = r0.length()     // Catch:{ all -> 0x0444 }
                int r14 = (int) r13     // Catch:{ all -> 0x0444 }
                java.lang.ThreadLocal r13 = org.telegram.messenger.ImageLoader.bytesLocal     // Catch:{ all -> 0x0444 }
                java.lang.Object r13 = r13.get()     // Catch:{ all -> 0x0444 }
                byte[] r13 = (byte[]) r13     // Catch:{ all -> 0x0444 }
                if (r13 == 0) goto L_0x040e
                int r9 = r13.length     // Catch:{ all -> 0x03eb }
                if (r9 < r14) goto L_0x040e
                goto L_0x040f
            L_0x040e:
                r13 = 0
            L_0x040f:
                if (r13 != 0) goto L_0x041a
                byte[] r13 = new byte[r14]     // Catch:{ all -> 0x03eb }
                java.lang.ThreadLocal r9 = org.telegram.messenger.ImageLoader.bytesLocal     // Catch:{ all -> 0x03eb }
                r9.set(r13)     // Catch:{ all -> 0x03eb }
            L_0x041a:
                r9 = 0
                r0.readFully(r13, r9, r14)     // Catch:{ all -> 0x0444 }
                r0.close()     // Catch:{ all -> 0x0444 }
                org.telegram.messenger.secretmedia.EncryptedFileInputStream.decryptBytesWithKeyFile((byte[]) r13, (int) r9, (int) r14, (org.telegram.messenger.SecureDocumentKey) r6)     // Catch:{ all -> 0x0444 }
                byte[] r0 = org.telegram.messenger.Utilities.computeSHA256(r13, r9, r14)     // Catch:{ all -> 0x0444 }
                if (r4 == 0) goto L_0x0435
                boolean r0 = java.util.Arrays.equals(r0, r4)     // Catch:{ all -> 0x03eb }
                if (r0 != 0) goto L_0x0431
                goto L_0x0435
            L_0x0431:
                r29 = r5
                r0 = 0
                goto L_0x0438
            L_0x0435:
                r29 = r5
                r0 = 1
            L_0x0438:
                r9 = 0
                byte r5 = r13[r9]     // Catch:{ all -> 0x04b2 }
                r5 = r5 & 255(0xff, float:3.57E-43)
                int r14 = r14 - r5
                if (r0 != 0) goto L_0x0469
                android.graphics.BitmapFactory.decodeByteArray(r13, r5, r14, r7)     // Catch:{ all -> 0x04b2 }
                goto L_0x0469
            L_0x0444:
                r0 = move-exception
                r29 = r5
            L_0x0447:
                r14 = r0
                r0 = r12
                r12 = r25
                r13 = r28
                goto L_0x04e7
            L_0x044f:
                r29 = r5
                if (r3 == 0) goto L_0x045d
                org.telegram.messenger.secretmedia.EncryptedFileInputStream r0 = new org.telegram.messenger.secretmedia.EncryptedFileInputStream     // Catch:{ all -> 0x04b2 }
                org.telegram.messenger.ImageLoader$CacheImage r5 = r1.cacheImage     // Catch:{ all -> 0x04b2 }
                java.io.File r5 = r5.encryptionKeyPath     // Catch:{ all -> 0x04b2 }
                r0.<init>((java.io.File) r2, (java.io.File) r5)     // Catch:{ all -> 0x04b2 }
                goto L_0x0462
            L_0x045d:
                java.io.FileInputStream r0 = new java.io.FileInputStream     // Catch:{ all -> 0x04b2 }
                r0.<init>(r2)     // Catch:{ all -> 0x04b2 }
            L_0x0462:
                r5 = 0
                android.graphics.BitmapFactory.decodeStream(r0, r5, r7)     // Catch:{ all -> 0x04b2 }
                r0.close()     // Catch:{ all -> 0x04b2 }
            L_0x0469:
                int r0 = r7.outWidth     // Catch:{ all -> 0x04b2 }
                float r0 = (float) r0     // Catch:{ all -> 0x04b2 }
                int r5 = r7.outHeight     // Catch:{ all -> 0x04b2 }
                float r5 = (float) r5     // Catch:{ all -> 0x04b2 }
                int r9 = (r25 > r12 ? 1 : (r25 == r12 ? 0 : -1))
                if (r9 < 0) goto L_0x0480
                int r9 = (r0 > r5 ? 1 : (r0 == r5 ? 0 : -1))
                if (r9 <= 0) goto L_0x0480
                float r9 = r0 / r25
                float r13 = r5 / r12
                float r9 = java.lang.Math.max(r9, r13)     // Catch:{ all -> 0x04b2 }
                goto L_0x0488
            L_0x0480:
                float r9 = r0 / r25
                float r13 = r5 / r12
                float r9 = java.lang.Math.min(r9, r13)     // Catch:{ all -> 0x04b2 }
            L_0x0488:
                r13 = 1067030938(0x3var_a, float:1.2)
                int r13 = (r9 > r13 ? 1 : (r9 == r13 ? 0 : -1))
                if (r13 >= 0) goto L_0x0491
                r9 = 1065353216(0x3var_, float:1.0)
            L_0x0491:
                r13 = 0
                r7.inJustDecodeBounds = r13     // Catch:{ all -> 0x04b2 }
                int r13 = (r9 > r23 ? 1 : (r9 == r23 ? 0 : -1))
                if (r13 <= 0) goto L_0x04ae
                int r0 = (r0 > r25 ? 1 : (r0 == r25 ? 0 : -1))
                if (r0 > 0) goto L_0x04a0
                int r0 = (r5 > r12 ? 1 : (r5 == r12 ? 0 : -1))
                if (r0 <= 0) goto L_0x04ae
            L_0x04a0:
                r0 = 1
            L_0x04a1:
                r5 = 2
                int r0 = r0 * 2
                int r5 = r0 * 2
                float r5 = (float) r5     // Catch:{ all -> 0x04b2 }
                int r5 = (r5 > r9 ? 1 : (r5 == r9 ? 0 : -1))
                if (r5 < 0) goto L_0x04a1
                r7.inSampleSize = r0     // Catch:{ all -> 0x04b2 }
                goto L_0x04c7
            L_0x04ae:
                int r0 = (int) r9     // Catch:{ all -> 0x04b2 }
                r7.inSampleSize = r0     // Catch:{ all -> 0x04b2 }
                goto L_0x04c7
            L_0x04b2:
                r0 = move-exception
                r14 = r0
                r0 = r12
                r12 = r25
                r13 = r28
                r5 = r29
                goto L_0x04e7
            L_0x04bc:
                r0 = move-exception
                r29 = r5
            L_0x04bf:
                r28 = r13
                goto L_0x04d1
            L_0x04c2:
                r29 = r5
                r28 = r13
                r10 = r14
            L_0x04c7:
                r13 = r28
                r5 = r29
                r0 = 0
                goto L_0x0542
            L_0x04ce:
                r0 = move-exception
                r29 = r5
            L_0x04d1:
                r10 = r14
                r14 = r0
                r0 = r12
                r12 = r25
                goto L_0x04e7
            L_0x04d7:
                r0 = move-exception
                r10 = r14
                r14 = r0
                r0 = r12
                r12 = r25
                r5 = 0
                goto L_0x04e7
            L_0x04df:
                r0 = move-exception
                r10 = r14
                r14 = r0
                r0 = r12
                r12 = r25
                r5 = 0
                r8 = 0
            L_0x04e7:
                r9 = 0
                goto L_0x054e
            L_0x04ea:
                r10 = r14
                if (r11 == 0) goto L_0x053c
                r5 = 1
                r7.inJustDecodeBounds = r5     // Catch:{ all -> 0x053a }
                if (r13 == 0) goto L_0x04f5
                android.graphics.Bitmap$Config r0 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ all -> 0x053a }
                goto L_0x04f7
            L_0x04f5:
                android.graphics.Bitmap$Config r0 = android.graphics.Bitmap.Config.RGB_565     // Catch:{ all -> 0x053a }
            L_0x04f7:
                r7.inPreferredConfig = r0     // Catch:{ all -> 0x053a }
                java.io.FileInputStream r0 = new java.io.FileInputStream     // Catch:{ all -> 0x053a }
                r0.<init>(r2)     // Catch:{ all -> 0x053a }
                r5 = 0
                android.graphics.Bitmap r8 = android.graphics.BitmapFactory.decodeStream(r0, r5, r7)     // Catch:{ all -> 0x053a }
                r0.close()     // Catch:{ all -> 0x0533 }
                int r0 = r7.outWidth     // Catch:{ all -> 0x0533 }
                int r5 = r7.outHeight     // Catch:{ all -> 0x0533 }
                r9 = 0
                r7.inJustDecodeBounds = r9     // Catch:{ all -> 0x0533 }
                int r0 = r0 / 200
                int r5 = r5 / 200
                int r0 = java.lang.Math.max(r0, r5)     // Catch:{ all -> 0x0533 }
                float r0 = (float) r0     // Catch:{ all -> 0x0533 }
                int r5 = (r0 > r23 ? 1 : (r0 == r23 ? 0 : -1))
                if (r5 >= 0) goto L_0x051c
                r0 = 1065353216(0x3var_, float:1.0)
            L_0x051c:
                int r5 = (r0 > r23 ? 1 : (r0 == r23 ? 0 : -1))
                if (r5 <= 0) goto L_0x052e
                r5 = 1
            L_0x0521:
                r9 = 2
                int r5 = r5 * 2
                int r9 = r5 * 2
                float r9 = (float) r9     // Catch:{ all -> 0x0533 }
                int r9 = (r9 > r0 ? 1 : (r9 == r0 ? 0 : -1))
                if (r9 < 0) goto L_0x0521
                r7.inSampleSize = r5     // Catch:{ all -> 0x0533 }
                goto L_0x0531
            L_0x052e:
                int r0 = (int) r0     // Catch:{ all -> 0x0533 }
                r7.inSampleSize = r0     // Catch:{ all -> 0x0533 }
            L_0x0531:
                r0 = r8
                goto L_0x053d
            L_0x0533:
                r0 = move-exception
                r14 = r0
                r9 = r8
                r0 = 0
                r5 = 0
                r8 = 0
                goto L_0x054d
            L_0x053a:
                r0 = move-exception
                goto L_0x0548
            L_0x053c:
                r0 = 0
            L_0x053d:
                r5 = 0
                r8 = 0
                r12 = 0
                r25 = 0
            L_0x0542:
                r9 = r0
                r0 = r25
                goto L_0x0556
            L_0x0546:
                r0 = move-exception
                r10 = r14
            L_0x0548:
                r14 = r0
                r0 = 0
                r5 = 0
                r8 = 0
                r9 = 0
            L_0x054d:
                r12 = 0
            L_0x054e:
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r14)
                r33 = r12
                r12 = r0
                r0 = r33
            L_0x0556:
                org.telegram.messenger.ImageLoader$CacheImage r14 = r1.cacheImage
                int r14 = r14.type
                r25 = 1101004800(0x41a00000, float:20.0)
                r28 = r9
                r9 = 1
                if (r14 != r9) goto L_0x0762
                org.telegram.messenger.ImageLoader r9 = org.telegram.messenger.ImageLoader.this     // Catch:{ all -> 0x0757 }
                long r10 = java.lang.System.currentTimeMillis()     // Catch:{ all -> 0x0757 }
                long unused = r9.lastCacheOutTime = r10     // Catch:{ all -> 0x0757 }
                java.lang.Object r9 = r1.sync     // Catch:{ all -> 0x0757 }
                monitor-enter(r9)     // Catch:{ all -> 0x0757 }
                boolean r10 = r1.isCancelled     // Catch:{ all -> 0x0754 }
                if (r10 == 0) goto L_0x0573
                monitor-exit(r9)     // Catch:{ all -> 0x0754 }
                return
            L_0x0573:
                monitor-exit(r9)     // Catch:{ all -> 0x0754 }
                if (r15 == 0) goto L_0x05bb
                java.io.RandomAccessFile r3 = new java.io.RandomAccessFile     // Catch:{ all -> 0x0757 }
                java.lang.String r4 = "r"
                r3.<init>(r2, r4)     // Catch:{ all -> 0x0757 }
                java.nio.channels.FileChannel r9 = r3.getChannel()     // Catch:{ all -> 0x0757 }
                java.nio.channels.FileChannel$MapMode r10 = java.nio.channels.FileChannel.MapMode.READ_ONLY     // Catch:{ all -> 0x0757 }
                r11 = 0
                long r13 = r2.length()     // Catch:{ all -> 0x0757 }
                java.nio.MappedByteBuffer r4 = r9.map(r10, r11, r13)     // Catch:{ all -> 0x0757 }
                android.graphics.BitmapFactory$Options r6 = new android.graphics.BitmapFactory$Options     // Catch:{ all -> 0x0757 }
                r6.<init>()     // Catch:{ all -> 0x0757 }
                r9 = 1
                r6.inJustDecodeBounds = r9     // Catch:{ all -> 0x0757 }
                int r10 = r4.limit()     // Catch:{ all -> 0x0757 }
                r11 = 0
                org.telegram.messenger.Utilities.loadWebpImage(r11, r4, r10, r6, r9)     // Catch:{ all -> 0x0757 }
                int r9 = r6.outWidth     // Catch:{ all -> 0x0757 }
                int r6 = r6.outHeight     // Catch:{ all -> 0x0757 }
                android.graphics.Bitmap$Config r10 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ all -> 0x0757 }
                android.graphics.Bitmap r9 = org.telegram.messenger.Bitmaps.createBitmap(r9, r6, r10)     // Catch:{ all -> 0x0757 }
                int r6 = r4.limit()     // Catch:{ all -> 0x05dc }
                boolean r10 = r7.inPurgeable     // Catch:{ all -> 0x05dc }
                if (r10 != 0) goto L_0x05b1
                r10 = 1
                goto L_0x05b2
            L_0x05b1:
                r10 = 0
            L_0x05b2:
                r11 = 0
                org.telegram.messenger.Utilities.loadWebpImage(r9, r4, r6, r11, r10)     // Catch:{ all -> 0x05dc }
                r3.close()     // Catch:{ all -> 0x05dc }
                goto L_0x063f
            L_0x05bb:
                boolean r9 = r7.inPurgeable     // Catch:{ all -> 0x0757 }
                if (r9 != 0) goto L_0x05df
                if (r6 == 0) goto L_0x05c2
                goto L_0x05df
            L_0x05c2:
                if (r3 == 0) goto L_0x05ce
                org.telegram.messenger.secretmedia.EncryptedFileInputStream r3 = new org.telegram.messenger.secretmedia.EncryptedFileInputStream     // Catch:{ all -> 0x0757 }
                org.telegram.messenger.ImageLoader$CacheImage r4 = r1.cacheImage     // Catch:{ all -> 0x0757 }
                java.io.File r4 = r4.encryptionKeyPath     // Catch:{ all -> 0x0757 }
                r3.<init>((java.io.File) r2, (java.io.File) r4)     // Catch:{ all -> 0x0757 }
                goto L_0x05d3
            L_0x05ce:
                java.io.FileInputStream r3 = new java.io.FileInputStream     // Catch:{ all -> 0x0757 }
                r3.<init>(r2)     // Catch:{ all -> 0x0757 }
            L_0x05d3:
                r4 = 0
                android.graphics.Bitmap r9 = android.graphics.BitmapFactory.decodeStream(r3, r4, r7)     // Catch:{ all -> 0x0757 }
                r3.close()     // Catch:{ all -> 0x05dc }
                goto L_0x063f
            L_0x05dc:
                r0 = move-exception
                goto L_0x075a
            L_0x05df:
                java.io.RandomAccessFile r9 = new java.io.RandomAccessFile     // Catch:{ all -> 0x0757 }
                java.lang.String r10 = "r"
                r9.<init>(r2, r10)     // Catch:{ all -> 0x0757 }
                long r10 = r9.length()     // Catch:{ all -> 0x0757 }
                int r11 = (int) r10     // Catch:{ all -> 0x0757 }
                java.lang.ThreadLocal r10 = org.telegram.messenger.ImageLoader.bytesThumbLocal     // Catch:{ all -> 0x0757 }
                java.lang.Object r10 = r10.get()     // Catch:{ all -> 0x0757 }
                byte[] r10 = (byte[]) r10     // Catch:{ all -> 0x0757 }
                if (r10 == 0) goto L_0x05fb
                int r12 = r10.length     // Catch:{ all -> 0x0757 }
                if (r12 < r11) goto L_0x05fb
                goto L_0x05fc
            L_0x05fb:
                r10 = 0
            L_0x05fc:
                if (r10 != 0) goto L_0x0607
                byte[] r10 = new byte[r11]     // Catch:{ all -> 0x0757 }
                java.lang.ThreadLocal r12 = org.telegram.messenger.ImageLoader.bytesThumbLocal     // Catch:{ all -> 0x0757 }
                r12.set(r10)     // Catch:{ all -> 0x0757 }
            L_0x0607:
                r12 = 0
                r9.readFully(r10, r12, r11)     // Catch:{ all -> 0x0757 }
                r9.close()     // Catch:{ all -> 0x0757 }
                if (r6 == 0) goto L_0x062a
                org.telegram.messenger.secretmedia.EncryptedFileInputStream.decryptBytesWithKeyFile((byte[]) r10, (int) r12, (int) r11, (org.telegram.messenger.SecureDocumentKey) r6)     // Catch:{ all -> 0x0757 }
                byte[] r3 = org.telegram.messenger.Utilities.computeSHA256(r10, r12, r11)     // Catch:{ all -> 0x0757 }
                if (r4 == 0) goto L_0x0622
                boolean r3 = java.util.Arrays.equals(r3, r4)     // Catch:{ all -> 0x0757 }
                if (r3 != 0) goto L_0x0620
                goto L_0x0622
            L_0x0620:
                r3 = 0
                goto L_0x0623
            L_0x0622:
                r3 = 1
            L_0x0623:
                r4 = 0
                byte r6 = r10[r4]     // Catch:{ all -> 0x0757 }
                r4 = r6 & 255(0xff, float:3.57E-43)
                int r11 = r11 - r4
                goto L_0x0636
            L_0x062a:
                if (r3 == 0) goto L_0x0634
                org.telegram.messenger.ImageLoader$CacheImage r3 = r1.cacheImage     // Catch:{ all -> 0x0757 }
                java.io.File r3 = r3.encryptionKeyPath     // Catch:{ all -> 0x0757 }
                r4 = 0
                org.telegram.messenger.secretmedia.EncryptedFileInputStream.decryptBytesWithKeyFile((byte[]) r10, (int) r4, (int) r11, (java.io.File) r3)     // Catch:{ all -> 0x0757 }
            L_0x0634:
                r3 = 0
                r4 = 0
            L_0x0636:
                if (r3 != 0) goto L_0x063d
                android.graphics.Bitmap r9 = android.graphics.BitmapFactory.decodeByteArray(r10, r4, r11, r7)     // Catch:{ all -> 0x0757 }
                goto L_0x063f
            L_0x063d:
                r9 = r28
            L_0x063f:
                if (r9 != 0) goto L_0x0657
                long r3 = r2.length()     // Catch:{ all -> 0x05dc }
                r5 = 0
                int r0 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
                if (r0 == 0) goto L_0x0651
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage     // Catch:{ all -> 0x05dc }
                java.lang.String r0 = r0.filter     // Catch:{ all -> 0x05dc }
                if (r0 != 0) goto L_0x0654
            L_0x0651:
                r2.delete()     // Catch:{ all -> 0x05dc }
            L_0x0654:
                r2 = 0
                goto L_0x075e
            L_0x0657:
                org.telegram.messenger.ImageLoader$CacheImage r2 = r1.cacheImage     // Catch:{ all -> 0x05dc }
                java.lang.String r2 = r2.filter     // Catch:{ all -> 0x05dc }
                if (r2 == 0) goto L_0x0688
                int r2 = r9.getWidth()     // Catch:{ all -> 0x05dc }
                float r2 = (float) r2     // Catch:{ all -> 0x05dc }
                int r3 = r9.getHeight()     // Catch:{ all -> 0x05dc }
                float r3 = (float) r3     // Catch:{ all -> 0x05dc }
                boolean r4 = r7.inPurgeable     // Catch:{ all -> 0x05dc }
                if (r4 != 0) goto L_0x0688
                int r4 = (r0 > r22 ? 1 : (r0 == r22 ? 0 : -1))
                if (r4 == 0) goto L_0x0688
                int r4 = (r2 > r0 ? 1 : (r2 == r0 ? 0 : -1))
                if (r4 == 0) goto L_0x0688
                float r25 = r0 + r25
                int r4 = (r2 > r25 ? 1 : (r2 == r25 ? 0 : -1))
                if (r4 <= 0) goto L_0x0688
                float r2 = r2 / r0
                int r0 = (int) r0     // Catch:{ all -> 0x05dc }
                float r3 = r3 / r2
                int r2 = (int) r3     // Catch:{ all -> 0x05dc }
                r3 = 1
                android.graphics.Bitmap r0 = org.telegram.messenger.Bitmaps.createScaledBitmap(r9, r0, r2, r3)     // Catch:{ all -> 0x05dc }
                if (r9 == r0) goto L_0x0688
                r9.recycle()     // Catch:{ all -> 0x05dc }
                r9 = r0
            L_0x0688:
                if (r5 == 0) goto L_0x06a8
                boolean r0 = r7.inPurgeable     // Catch:{ all -> 0x05dc }
                if (r0 == 0) goto L_0x0690
                r0 = 0
                goto L_0x0691
            L_0x0690:
                r0 = 1
            L_0x0691:
                int r2 = r9.getWidth()     // Catch:{ all -> 0x05dc }
                int r3 = r9.getHeight()     // Catch:{ all -> 0x05dc }
                int r4 = r9.getRowBytes()     // Catch:{ all -> 0x05dc }
                int r0 = org.telegram.messenger.Utilities.needInvert(r9, r0, r2, r3, r4)     // Catch:{ all -> 0x05dc }
                if (r0 == 0) goto L_0x06a5
                r0 = 1
                goto L_0x06a6
            L_0x06a5:
                r0 = 0
            L_0x06a6:
                r2 = r0
                goto L_0x06a9
            L_0x06a8:
                r2 = 0
            L_0x06a9:
                r3 = 1
                if (r8 != r3) goto L_0x06d1
                android.graphics.Bitmap$Config r0 = r9.getConfig()     // Catch:{ all -> 0x06ce }
                android.graphics.Bitmap$Config r3 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ all -> 0x06ce }
                if (r0 != r3) goto L_0x075e
                r11 = 3
                boolean r0 = r7.inPurgeable     // Catch:{ all -> 0x06ce }
                if (r0 == 0) goto L_0x06bb
                r12 = 0
                goto L_0x06bc
            L_0x06bb:
                r12 = 1
            L_0x06bc:
                int r13 = r9.getWidth()     // Catch:{ all -> 0x06ce }
                int r14 = r9.getHeight()     // Catch:{ all -> 0x06ce }
                int r15 = r9.getRowBytes()     // Catch:{ all -> 0x06ce }
                r10 = r9
                org.telegram.messenger.Utilities.blurBitmap(r10, r11, r12, r13, r14, r15)     // Catch:{ all -> 0x06ce }
                goto L_0x075e
            L_0x06ce:
                r0 = move-exception
                goto L_0x075b
            L_0x06d1:
                r3 = 2
                if (r8 != r3) goto L_0x06f6
                android.graphics.Bitmap$Config r0 = r9.getConfig()     // Catch:{ all -> 0x06ce }
                android.graphics.Bitmap$Config r3 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ all -> 0x06ce }
                if (r0 != r3) goto L_0x075e
                r11 = 1
                boolean r0 = r7.inPurgeable     // Catch:{ all -> 0x06ce }
                if (r0 == 0) goto L_0x06e3
                r12 = 0
                goto L_0x06e4
            L_0x06e3:
                r12 = 1
            L_0x06e4:
                int r13 = r9.getWidth()     // Catch:{ all -> 0x06ce }
                int r14 = r9.getHeight()     // Catch:{ all -> 0x06ce }
                int r15 = r9.getRowBytes()     // Catch:{ all -> 0x06ce }
                r10 = r9
                org.telegram.messenger.Utilities.blurBitmap(r10, r11, r12, r13, r14, r15)     // Catch:{ all -> 0x06ce }
                goto L_0x075e
            L_0x06f6:
                r3 = 3
                if (r8 != r3) goto L_0x074a
                android.graphics.Bitmap$Config r0 = r9.getConfig()     // Catch:{ all -> 0x06ce }
                android.graphics.Bitmap$Config r3 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ all -> 0x06ce }
                if (r0 != r3) goto L_0x075e
                r11 = 7
                boolean r0 = r7.inPurgeable     // Catch:{ all -> 0x06ce }
                if (r0 == 0) goto L_0x0708
                r12 = 0
                goto L_0x0709
            L_0x0708:
                r12 = 1
            L_0x0709:
                int r13 = r9.getWidth()     // Catch:{ all -> 0x06ce }
                int r14 = r9.getHeight()     // Catch:{ all -> 0x06ce }
                int r15 = r9.getRowBytes()     // Catch:{ all -> 0x06ce }
                r10 = r9
                org.telegram.messenger.Utilities.blurBitmap(r10, r11, r12, r13, r14, r15)     // Catch:{ all -> 0x06ce }
                r11 = 7
                boolean r0 = r7.inPurgeable     // Catch:{ all -> 0x06ce }
                if (r0 == 0) goto L_0x0720
                r12 = 0
                goto L_0x0721
            L_0x0720:
                r12 = 1
            L_0x0721:
                int r13 = r9.getWidth()     // Catch:{ all -> 0x06ce }
                int r14 = r9.getHeight()     // Catch:{ all -> 0x06ce }
                int r15 = r9.getRowBytes()     // Catch:{ all -> 0x06ce }
                r10 = r9
                org.telegram.messenger.Utilities.blurBitmap(r10, r11, r12, r13, r14, r15)     // Catch:{ all -> 0x06ce }
                r11 = 7
                boolean r0 = r7.inPurgeable     // Catch:{ all -> 0x06ce }
                if (r0 == 0) goto L_0x0738
                r12 = 0
                goto L_0x0739
            L_0x0738:
                r12 = 1
            L_0x0739:
                int r13 = r9.getWidth()     // Catch:{ all -> 0x06ce }
                int r14 = r9.getHeight()     // Catch:{ all -> 0x06ce }
                int r15 = r9.getRowBytes()     // Catch:{ all -> 0x06ce }
                r10 = r9
                org.telegram.messenger.Utilities.blurBitmap(r10, r11, r12, r13, r14, r15)     // Catch:{ all -> 0x06ce }
                goto L_0x075e
            L_0x074a:
                if (r8 != 0) goto L_0x075e
                boolean r0 = r7.inPurgeable     // Catch:{ all -> 0x06ce }
                if (r0 == 0) goto L_0x075e
                org.telegram.messenger.Utilities.pinBitmap(r9)     // Catch:{ all -> 0x06ce }
                goto L_0x075e
            L_0x0754:
                r0 = move-exception
                monitor-exit(r9)     // Catch:{ all -> 0x0754 }
                throw r0     // Catch:{ all -> 0x0757 }
            L_0x0757:
                r0 = move-exception
                r9 = r28
            L_0x075a:
                r2 = 0
            L_0x075b:
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            L_0x075e:
                r5 = 0
                r10 = 0
                goto L_0x0a20
            L_0x0762:
                r9 = 20
                if (r17 == 0) goto L_0x0767
                r9 = 0
            L_0x0767:
                if (r9 == 0) goto L_0x079a
                org.telegram.messenger.ImageLoader r14 = org.telegram.messenger.ImageLoader.this     // Catch:{ all -> 0x0795 }
                long r29 = r14.lastCacheOutTime     // Catch:{ all -> 0x0795 }
                r20 = 0
                int r14 = (r29 > r20 ? 1 : (r29 == r20 ? 0 : -1))
                if (r14 == 0) goto L_0x079a
                org.telegram.messenger.ImageLoader r14 = org.telegram.messenger.ImageLoader.this     // Catch:{ all -> 0x0795 }
                long r29 = r14.lastCacheOutTime     // Catch:{ all -> 0x0795 }
                long r31 = java.lang.System.currentTimeMillis()     // Catch:{ all -> 0x0795 }
                r14 = r4
                r26 = r5
                long r4 = (long) r9     // Catch:{ all -> 0x0795 }
                long r31 = r31 - r4
                int r9 = (r29 > r31 ? 1 : (r29 == r31 ? 0 : -1))
                if (r9 <= 0) goto L_0x079d
                int r9 = android.os.Build.VERSION.SDK_INT     // Catch:{ all -> 0x0795 }
                r29 = r12
                r12 = 21
                if (r9 >= r12) goto L_0x079f
                java.lang.Thread.sleep(r4)     // Catch:{ all -> 0x0795 }
                goto L_0x079f
            L_0x0795:
                r9 = r28
            L_0x0797:
                r5 = 0
                goto L_0x0a1a
            L_0x079a:
                r14 = r4
                r26 = r5
            L_0x079d:
                r29 = r12
            L_0x079f:
                org.telegram.messenger.ImageLoader r4 = org.telegram.messenger.ImageLoader.this     // Catch:{ all -> 0x0a17 }
                r9 = r6
                long r5 = java.lang.System.currentTimeMillis()     // Catch:{ all -> 0x0a17 }
                long unused = r4.lastCacheOutTime = r5     // Catch:{ all -> 0x0a17 }
                java.lang.Object r4 = r1.sync     // Catch:{ all -> 0x0a17 }
                monitor-enter(r4)     // Catch:{ all -> 0x0a17 }
                boolean r5 = r1.isCancelled     // Catch:{ all -> 0x0a11 }
                if (r5 == 0) goto L_0x07b2
                monitor-exit(r4)     // Catch:{ all -> 0x0a11 }
                return
            L_0x07b2:
                monitor-exit(r4)     // Catch:{ all -> 0x0a11 }
                if (r13 != 0) goto L_0x07cb
                org.telegram.messenger.ImageLoader$CacheImage r4 = r1.cacheImage     // Catch:{ all -> 0x0795 }
                java.lang.String r4 = r4.filter     // Catch:{ all -> 0x0795 }
                if (r4 == 0) goto L_0x07cb
                if (r8 != 0) goto L_0x07cb
                org.telegram.messenger.ImageLoader$CacheImage r4 = r1.cacheImage     // Catch:{ all -> 0x0795 }
                org.telegram.messenger.ImageLocation r4 = r4.imageLocation     // Catch:{ all -> 0x0795 }
                java.lang.String r4 = r4.path     // Catch:{ all -> 0x0795 }
                if (r4 == 0) goto L_0x07c6
                goto L_0x07cb
            L_0x07c6:
                android.graphics.Bitmap$Config r4 = android.graphics.Bitmap.Config.RGB_565     // Catch:{ all -> 0x0795 }
                r7.inPreferredConfig = r4     // Catch:{ all -> 0x0795 }
                goto L_0x07cf
            L_0x07cb:
                android.graphics.Bitmap$Config r4 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ all -> 0x0a17 }
                r7.inPreferredConfig = r4     // Catch:{ all -> 0x0a17 }
            L_0x07cf:
                r4 = 0
                r7.inDither = r4     // Catch:{ all -> 0x0a17 }
                if (r17 == 0) goto L_0x07f8
                if (r11 != 0) goto L_0x07f8
                if (r10 == 0) goto L_0x07e8
                android.content.Context r4 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x0795 }
                android.content.ContentResolver r4 = r4.getContentResolver()     // Catch:{ all -> 0x0795 }
                long r5 = r17.longValue()     // Catch:{ all -> 0x0795 }
                r10 = 1
                android.graphics.Bitmap r4 = android.provider.MediaStore.Video.Thumbnails.getThumbnail(r4, r5, r10, r7)     // Catch:{ all -> 0x0795 }
                goto L_0x07fa
            L_0x07e8:
                android.content.Context r4 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x0795 }
                android.content.ContentResolver r4 = r4.getContentResolver()     // Catch:{ all -> 0x0795 }
                long r5 = r17.longValue()     // Catch:{ all -> 0x0795 }
                r10 = 1
                android.graphics.Bitmap r4 = android.provider.MediaStore.Images.Thumbnails.getThumbnail(r4, r5, r10, r7)     // Catch:{ all -> 0x0795 }
                goto L_0x07fa
            L_0x07f8:
                r4 = r28
            L_0x07fa:
                if (r4 != 0) goto L_0x0914
                if (r15 == 0) goto L_0x084e
                java.io.RandomAccessFile r3 = new java.io.RandomAccessFile     // Catch:{ all -> 0x084b }
                java.lang.String r5 = "r"
                r3.<init>(r2, r5)     // Catch:{ all -> 0x084b }
                java.nio.channels.FileChannel r9 = r3.getChannel()     // Catch:{ all -> 0x084b }
                java.nio.channels.FileChannel$MapMode r10 = java.nio.channels.FileChannel.MapMode.READ_ONLY     // Catch:{ all -> 0x084b }
                r11 = 0
                long r13 = r2.length()     // Catch:{ all -> 0x084b }
                java.nio.MappedByteBuffer r5 = r9.map(r10, r11, r13)     // Catch:{ all -> 0x084b }
                android.graphics.BitmapFactory$Options r6 = new android.graphics.BitmapFactory$Options     // Catch:{ all -> 0x084b }
                r6.<init>()     // Catch:{ all -> 0x084b }
                r9 = 1
                r6.inJustDecodeBounds = r9     // Catch:{ all -> 0x084b }
                int r10 = r5.limit()     // Catch:{ all -> 0x084b }
                r11 = 0
                org.telegram.messenger.Utilities.loadWebpImage(r11, r5, r10, r6, r9)     // Catch:{ all -> 0x0847 }
                int r9 = r6.outWidth     // Catch:{ all -> 0x084b }
                int r6 = r6.outHeight     // Catch:{ all -> 0x084b }
                android.graphics.Bitmap$Config r10 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ all -> 0x084b }
                android.graphics.Bitmap r9 = org.telegram.messenger.Bitmaps.createBitmap(r9, r6, r10)     // Catch:{ all -> 0x084b }
                int r4 = r5.limit()     // Catch:{ all -> 0x0797 }
                boolean r6 = r7.inPurgeable     // Catch:{ all -> 0x0797 }
                if (r6 != 0) goto L_0x0839
                r6 = 1
                goto L_0x083a
            L_0x0839:
                r6 = 0
            L_0x083a:
                r10 = 0
                org.telegram.messenger.Utilities.loadWebpImage(r9, r5, r4, r10, r6)     // Catch:{ all -> 0x0844 }
                r3.close()     // Catch:{ all -> 0x0797 }
                r5 = 0
                goto L_0x0916
            L_0x0844:
                r5 = r10
                goto L_0x0a1a
            L_0x0847:
                r9 = r4
                r5 = r11
                goto L_0x0a1a
            L_0x084b:
                r9 = r4
                goto L_0x0797
            L_0x084e:
                boolean r5 = r7.inPurgeable     // Catch:{ all -> 0x0910 }
                if (r5 != 0) goto L_0x08b1
                if (r9 == 0) goto L_0x0855
                goto L_0x08b1
            L_0x0855:
                if (r3 == 0) goto L_0x0861
                org.telegram.messenger.secretmedia.EncryptedFileInputStream r3 = new org.telegram.messenger.secretmedia.EncryptedFileInputStream     // Catch:{ all -> 0x084b }
                org.telegram.messenger.ImageLoader$CacheImage r5 = r1.cacheImage     // Catch:{ all -> 0x084b }
                java.io.File r5 = r5.encryptionKeyPath     // Catch:{ all -> 0x084b }
                r3.<init>((java.io.File) r2, (java.io.File) r5)     // Catch:{ all -> 0x084b }
                goto L_0x0866
            L_0x0861:
                java.io.FileInputStream r3 = new java.io.FileInputStream     // Catch:{ all -> 0x0910 }
                r3.<init>(r2)     // Catch:{ all -> 0x0910 }
            L_0x0866:
                org.telegram.messenger.ImageLoader$CacheImage r5 = r1.cacheImage     // Catch:{ all -> 0x0910 }
                org.telegram.messenger.ImageLocation r5 = r5.imageLocation     // Catch:{ all -> 0x0910 }
                org.telegram.tgnet.TLRPC$Document r5 = r5.document     // Catch:{ all -> 0x0910 }
                boolean r5 = r5 instanceof org.telegram.tgnet.TLRPC$TL_document     // Catch:{ all -> 0x0910 }
                if (r5 == 0) goto L_0x08a3
                androidx.exifinterface.media.ExifInterface r5 = new androidx.exifinterface.media.ExifInterface     // Catch:{ all -> 0x0890 }
                r5.<init>((java.io.InputStream) r3)     // Catch:{ all -> 0x0890 }
                java.lang.String r6 = "Orientation"
                r9 = 1
                int r5 = r5.getAttributeInt(r6, r9)     // Catch:{ all -> 0x0890 }
                r6 = 3
                if (r5 == r6) goto L_0x088d
                r6 = 6
                if (r5 == r6) goto L_0x088a
                r6 = 8
                if (r5 == r6) goto L_0x0887
                goto L_0x0890
            L_0x0887:
                r5 = 270(0x10e, float:3.78E-43)
                goto L_0x0891
            L_0x088a:
                r5 = 90
                goto L_0x0891
            L_0x088d:
                r5 = 180(0xb4, float:2.52E-43)
                goto L_0x0891
            L_0x0890:
                r5 = 0
            L_0x0891:
                java.nio.channels.FileChannel r6 = r3.getChannel()     // Catch:{ all -> 0x089d }
                r9 = 0
                r6.position(r9)     // Catch:{ all -> 0x089d }
                r6 = r5
                r5 = 0
                goto L_0x08a5
            L_0x089d:
                r9 = r4
                r27 = r5
                r5 = 0
                goto L_0x0a0f
            L_0x08a3:
                r5 = 0
                r6 = 0
            L_0x08a5:
                android.graphics.Bitmap r9 = android.graphics.BitmapFactory.decodeStream(r3, r5, r7)     // Catch:{ all -> 0x08ae }
                r3.close()     // Catch:{ all -> 0x0a0d }
                goto L_0x0917
            L_0x08ae:
                r9 = r4
                goto L_0x0a0d
            L_0x08b1:
                r5 = 0
                java.io.RandomAccessFile r6 = new java.io.RandomAccessFile     // Catch:{ all -> 0x0911 }
                java.lang.String r10 = "r"
                r6.<init>(r2, r10)     // Catch:{ all -> 0x0911 }
                long r10 = r6.length()     // Catch:{ all -> 0x0911 }
                int r11 = (int) r10     // Catch:{ all -> 0x0911 }
                java.lang.ThreadLocal r10 = org.telegram.messenger.ImageLoader.bytesLocal     // Catch:{ all -> 0x0911 }
                java.lang.Object r10 = r10.get()     // Catch:{ all -> 0x0911 }
                byte[] r10 = (byte[]) r10     // Catch:{ all -> 0x0911 }
                if (r10 == 0) goto L_0x08ce
                int r12 = r10.length     // Catch:{ all -> 0x0911 }
                if (r12 < r11) goto L_0x08ce
                goto L_0x08cf
            L_0x08ce:
                r10 = r5
            L_0x08cf:
                if (r10 != 0) goto L_0x08da
                byte[] r10 = new byte[r11]     // Catch:{ all -> 0x0911 }
                java.lang.ThreadLocal r12 = org.telegram.messenger.ImageLoader.bytesLocal     // Catch:{ all -> 0x0911 }
                r12.set(r10)     // Catch:{ all -> 0x0911 }
            L_0x08da:
                r12 = 0
                r6.readFully(r10, r12, r11)     // Catch:{ all -> 0x0911 }
                r6.close()     // Catch:{ all -> 0x0911 }
                if (r9 == 0) goto L_0x08fd
                org.telegram.messenger.secretmedia.EncryptedFileInputStream.decryptBytesWithKeyFile((byte[]) r10, (int) r12, (int) r11, (org.telegram.messenger.SecureDocumentKey) r9)     // Catch:{ all -> 0x0911 }
                byte[] r3 = org.telegram.messenger.Utilities.computeSHA256(r10, r12, r11)     // Catch:{ all -> 0x0911 }
                if (r14 == 0) goto L_0x08f5
                boolean r3 = java.util.Arrays.equals(r3, r14)     // Catch:{ all -> 0x0911 }
                if (r3 != 0) goto L_0x08f3
                goto L_0x08f5
            L_0x08f3:
                r3 = 0
                goto L_0x08f6
            L_0x08f5:
                r3 = 1
            L_0x08f6:
                r6 = 0
                byte r9 = r10[r6]     // Catch:{ all -> 0x0911 }
                r6 = r9 & 255(0xff, float:3.57E-43)
                int r11 = r11 - r6
                goto L_0x0909
            L_0x08fd:
                if (r3 == 0) goto L_0x0907
                org.telegram.messenger.ImageLoader$CacheImage r3 = r1.cacheImage     // Catch:{ all -> 0x0911 }
                java.io.File r3 = r3.encryptionKeyPath     // Catch:{ all -> 0x0911 }
                r6 = 0
                org.telegram.messenger.secretmedia.EncryptedFileInputStream.decryptBytesWithKeyFile((byte[]) r10, (int) r6, (int) r11, (java.io.File) r3)     // Catch:{ all -> 0x0911 }
            L_0x0907:
                r3 = 0
                r6 = 0
            L_0x0909:
                if (r3 != 0) goto L_0x0915
                android.graphics.Bitmap r4 = android.graphics.BitmapFactory.decodeByteArray(r10, r6, r11, r7)     // Catch:{ all -> 0x0911 }
                goto L_0x0915
            L_0x0910:
                r5 = 0
            L_0x0911:
                r9 = r4
                goto L_0x0a1a
            L_0x0914:
                r5 = 0
            L_0x0915:
                r9 = r4
            L_0x0916:
                r6 = 0
            L_0x0917:
                if (r9 != 0) goto L_0x0931
                if (r18 == 0) goto L_0x092e
                long r3 = r2.length()     // Catch:{ all -> 0x0a0d }
                r7 = 0
                int r0 = (r3 > r7 ? 1 : (r3 == r7 ? 0 : -1))
                if (r0 == 0) goto L_0x092b
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage     // Catch:{ all -> 0x0a0d }
                java.lang.String r0 = r0.filter     // Catch:{ all -> 0x0a0d }
                if (r0 != 0) goto L_0x092e
            L_0x092b:
                r2.delete()     // Catch:{ all -> 0x0a0d }
            L_0x092e:
                r10 = 0
                goto L_0x0a0a
            L_0x0931:
                org.telegram.messenger.ImageLoader$CacheImage r2 = r1.cacheImage     // Catch:{ all -> 0x0a0d }
                java.lang.String r2 = r2.filter     // Catch:{ all -> 0x0a0d }
                if (r2 == 0) goto L_0x09f8
                int r2 = r9.getWidth()     // Catch:{ all -> 0x0a0d }
                float r2 = (float) r2     // Catch:{ all -> 0x0a0d }
                int r3 = r9.getHeight()     // Catch:{ all -> 0x0a0d }
                float r3 = (float) r3     // Catch:{ all -> 0x0a0d }
                boolean r4 = r7.inPurgeable     // Catch:{ all -> 0x0a0d }
                if (r4 != 0) goto L_0x0984
                int r4 = (r0 > r22 ? 1 : (r0 == r22 ? 0 : -1))
                if (r4 == 0) goto L_0x0984
                int r4 = (r2 > r0 ? 1 : (r2 == r0 ? 0 : -1))
                if (r4 == 0) goto L_0x0984
                float r25 = r0 + r25
                int r4 = (r2 > r25 ? 1 : (r2 == r25 ? 0 : -1))
                if (r4 <= 0) goto L_0x0984
                int r4 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
                if (r4 <= 0) goto L_0x096b
                int r4 = (r0 > r29 ? 1 : (r0 == r29 ? 0 : -1))
                if (r4 <= 0) goto L_0x096b
                float r4 = r2 / r0
                int r10 = (r4 > r23 ? 1 : (r4 == r23 ? 0 : -1))
                if (r10 <= 0) goto L_0x097d
                int r0 = (int) r0     // Catch:{ all -> 0x0a0d }
                float r4 = r3 / r4
                int r4 = (int) r4     // Catch:{ all -> 0x0a0d }
                r10 = 1
                android.graphics.Bitmap r0 = org.telegram.messenger.Bitmaps.createScaledBitmap(r9, r0, r4, r10)     // Catch:{ all -> 0x0a0d }
                goto L_0x097e
            L_0x096b:
                float r0 = r3 / r29
                int r4 = (r0 > r23 ? 1 : (r0 == r23 ? 0 : -1))
                if (r4 <= 0) goto L_0x097d
                float r0 = r2 / r0
                int r0 = (int) r0     // Catch:{ all -> 0x0a0d }
                r12 = r29
                int r4 = (int) r12     // Catch:{ all -> 0x0a0d }
                r10 = 1
                android.graphics.Bitmap r0 = org.telegram.messenger.Bitmaps.createScaledBitmap(r9, r0, r4, r10)     // Catch:{ all -> 0x0a0d }
                goto L_0x097e
            L_0x097d:
                r0 = r9
            L_0x097e:
                if (r9 == r0) goto L_0x0984
                r9.recycle()     // Catch:{ all -> 0x0a0d }
                r9 = r0
            L_0x0984:
                if (r9 == 0) goto L_0x09f8
                if (r26 == 0) goto L_0x09c1
                int r0 = r9.getWidth()     // Catch:{ all -> 0x0a0d }
                int r4 = r9.getHeight()     // Catch:{ all -> 0x0a0d }
                int r0 = r0 * r4
                r4 = 22500(0x57e4, float:3.1529E-41)
                if (r0 <= r4) goto L_0x099e
                r0 = 100
                r4 = 0
                android.graphics.Bitmap r0 = org.telegram.messenger.Bitmaps.createScaledBitmap(r9, r0, r0, r4)     // Catch:{ all -> 0x0a0d }
                goto L_0x099f
            L_0x099e:
                r0 = r9
            L_0x099f:
                boolean r4 = r7.inPurgeable     // Catch:{ all -> 0x0a0d }
                if (r4 == 0) goto L_0x09a5
                r4 = 0
                goto L_0x09a6
            L_0x09a5:
                r4 = 1
            L_0x09a6:
                int r10 = r0.getWidth()     // Catch:{ all -> 0x0a0d }
                int r11 = r0.getHeight()     // Catch:{ all -> 0x0a0d }
                int r12 = r0.getRowBytes()     // Catch:{ all -> 0x0a0d }
                int r4 = org.telegram.messenger.Utilities.needInvert(r0, r4, r10, r11, r12)     // Catch:{ all -> 0x0a0d }
                if (r4 == 0) goto L_0x09ba
                r4 = 1
                goto L_0x09bb
            L_0x09ba:
                r4 = 0
            L_0x09bb:
                if (r0 == r9) goto L_0x09c2
                r0.recycle()     // Catch:{ all -> 0x09f2 }
                goto L_0x09c2
            L_0x09c1:
                r4 = 0
            L_0x09c2:
                if (r8 == 0) goto L_0x09f4
                r0 = 1120403456(0x42CLASSNAME, float:100.0)
                int r3 = (r3 > r0 ? 1 : (r3 == r0 ? 0 : -1))
                if (r3 >= 0) goto L_0x09f4
                int r0 = (r2 > r0 ? 1 : (r2 == r0 ? 0 : -1))
                if (r0 >= 0) goto L_0x09f4
                android.graphics.Bitmap$Config r0 = r9.getConfig()     // Catch:{ all -> 0x09f2 }
                android.graphics.Bitmap$Config r2 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ all -> 0x09f2 }
                if (r0 != r2) goto L_0x09ee
                r11 = 3
                boolean r0 = r7.inPurgeable     // Catch:{ all -> 0x09f2 }
                if (r0 == 0) goto L_0x09dd
                r12 = 0
                goto L_0x09de
            L_0x09dd:
                r12 = 1
            L_0x09de:
                int r13 = r9.getWidth()     // Catch:{ all -> 0x09f2 }
                int r14 = r9.getHeight()     // Catch:{ all -> 0x09f2 }
                int r15 = r9.getRowBytes()     // Catch:{ all -> 0x09f2 }
                r10 = r9
                org.telegram.messenger.Utilities.blurBitmap(r10, r11, r12, r13, r14, r15)     // Catch:{ all -> 0x09f2 }
            L_0x09ee:
                r10 = r4
                r0 = r9
                r9 = 1
                goto L_0x09fb
            L_0x09f2:
                r10 = r4
                goto L_0x0a06
            L_0x09f4:
                r10 = r4
                r0 = r9
                r9 = 0
                goto L_0x09fb
            L_0x09f8:
                r0 = r9
                r9 = 0
                r10 = 0
            L_0x09fb:
                if (r9 != 0) goto L_0x0a09
                boolean r2 = r7.inPurgeable     // Catch:{ all -> 0x0a05 }
                if (r2 == 0) goto L_0x0a09
                org.telegram.messenger.Utilities.pinBitmap(r0)     // Catch:{ all -> 0x0a05 }
                goto L_0x0a09
            L_0x0a05:
                r9 = r0
            L_0x0a06:
                r27 = r6
                goto L_0x0a1d
            L_0x0a09:
                r9 = r0
            L_0x0a0a:
                r2 = r10
                r10 = r6
                goto L_0x0a20
            L_0x0a0d:
                r27 = r6
            L_0x0a0f:
                r10 = 0
                goto L_0x0a1d
            L_0x0a11:
                r0 = move-exception
                r5 = 0
            L_0x0a13:
                monitor-exit(r4)     // Catch:{ all -> 0x0a15 }
                throw r0     // Catch:{ all -> 0x0a18 }
            L_0x0a15:
                r0 = move-exception
                goto L_0x0a13
            L_0x0a17:
                r5 = 0
            L_0x0a18:
                r9 = r28
            L_0x0a1a:
                r10 = 0
                r27 = 0
            L_0x0a1d:
                r2 = r10
                r10 = r27
            L_0x0a20:
                java.lang.Thread.interrupted()
                if (r2 != 0) goto L_0x0a33
                if (r10 == 0) goto L_0x0a28
                goto L_0x0a33
            L_0x0a28:
                if (r9 == 0) goto L_0x0a2f
                android.graphics.drawable.BitmapDrawable r5 = new android.graphics.drawable.BitmapDrawable
                r5.<init>(r9)
            L_0x0a2f:
                r1.onPostExecute(r5)
                goto L_0x0a95
            L_0x0a33:
                if (r9 == 0) goto L_0x0a3a
                org.telegram.messenger.ExtendedBitmapDrawable r5 = new org.telegram.messenger.ExtendedBitmapDrawable
                r5.<init>(r9, r2, r10)
            L_0x0a3a:
                r1.onPostExecute(r5)
                goto L_0x0a95
            L_0x0a3e:
                r5 = 0
                r0 = 1135869952(0x43b40000, float:360.0)
                int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
                r2 = 1142947840(0x44200000, float:640.0)
                int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
                org.telegram.messenger.ImageLoader$CacheImage r3 = r1.cacheImage
                java.lang.String r3 = r3.filter
                if (r3 == 0) goto L_0x0a72
                java.lang.String r4 = "_"
                java.lang.String[] r3 = r3.split(r4)
                int r4 = r3.length
                r7 = 2
                if (r4 < r7) goto L_0x0a72
                r4 = 0
                r0 = r3[r4]
                float r0 = java.lang.Float.parseFloat(r0)
                r7 = 1
                r2 = r3[r7]
                float r2 = java.lang.Float.parseFloat(r2)
                float r3 = org.telegram.messenger.AndroidUtilities.density
                float r0 = r0 * r3
                int r0 = (int) r0
                float r2 = r2 * r3
                int r2 = (int) r2
                goto L_0x0a74
            L_0x0a72:
                r4 = 0
                r7 = 1
            L_0x0a74:
                org.telegram.messenger.ImageLoader$CacheImage r3 = r1.cacheImage     // Catch:{ all -> 0x0a86 }
                java.io.File r3 = r3.finalFilePath     // Catch:{ all -> 0x0a86 }
                org.telegram.messenger.ImageLoader$CacheImage r8 = r1.cacheImage     // Catch:{ all -> 0x0a86 }
                int r8 = r8.imageType     // Catch:{ all -> 0x0a86 }
                if (r8 != r6) goto L_0x0a80
                r9 = 1
                goto L_0x0a81
            L_0x0a80:
                r9 = 0
            L_0x0a81:
                android.graphics.Bitmap r0 = org.telegram.ui.Components.SvgHelper.getBitmap(r3, r0, r2, r9)     // Catch:{ all -> 0x0a86 }
                goto L_0x0a8b
            L_0x0a86:
                r0 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
                r0 = r5
            L_0x0a8b:
                if (r0 == 0) goto L_0x0a92
                android.graphics.drawable.BitmapDrawable r5 = new android.graphics.drawable.BitmapDrawable
                r5.<init>(r0)
            L_0x0a92:
                r1.onPostExecute(r5)
            L_0x0a95:
                return
            L_0x0a96:
                r0 = move-exception
                monitor-exit(r2)     // Catch:{ all -> 0x0a96 }
                goto L_0x0a9a
            L_0x0a99:
                throw r0
            L_0x0a9a:
                goto L_0x0a99
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ImageLoader.CacheOutTask.run():void");
        }

        private void onPostExecute(Drawable drawable) {
            AndroidUtilities.runOnUIThread(new Runnable(drawable) {
                public final /* synthetic */ Drawable f$1;

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
                    public final /* synthetic */ Drawable f$1;
                    public final /* synthetic */ ArrayList f$2;
                    public final /* synthetic */ ArrayList f$3;
                    public final /* synthetic */ String f$4;

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
        AndroidUtilities.createEmptyFile(new File(cacheDir, ".nomedia"));
        sparseArray.put(4, cacheDir);
        for (final int i = 0; i < 3; i++) {
            FileLoader.getInstance(i).setDelegate(new FileLoader.FileLoaderDelegate() {
                public void fileUploadProgressChanged(String str, long j, long j2, boolean z) {
                    String str2 = str;
                    ImageLoader.this.fileProgresses.put(str, new long[]{j, j2});
                    long currentTimeMillis = System.currentTimeMillis();
                    if (ImageLoader.this.lastProgressUpdateTime == 0 || ImageLoader.this.lastProgressUpdateTime < currentTimeMillis - 500) {
                        long unused = ImageLoader.this.lastProgressUpdateTime = currentTimeMillis;
                        AndroidUtilities.runOnUIThread(new Runnable(i, str, j, j2, z) {
                            public final /* synthetic */ int f$0;
                            public final /* synthetic */ String f$1;
                            public final /* synthetic */ long f$2;
                            public final /* synthetic */ long f$3;
                            public final /* synthetic */ boolean f$4;

                            {
                                this.f$0 = r1;
                                this.f$1 = r2;
                                this.f$2 = r3;
                                this.f$3 = r5;
                                this.f$4 = r7;
                            }

                            public final void run() {
                                NotificationCenter.getInstance(this.f$0).postNotificationName(NotificationCenter.FileUploadProgressChanged, this.f$1, Long.valueOf(this.f$2), Long.valueOf(this.f$3), Boolean.valueOf(this.f$4));
                            }
                        });
                    }
                }

                public void fileDidUploaded(String str, TLRPC$InputFile tLRPC$InputFile, TLRPC$InputEncryptedFile tLRPC$InputEncryptedFile, byte[] bArr, byte[] bArr2, long j) {
                    Utilities.stageQueue.postRunnable(new Runnable(i, str, tLRPC$InputFile, tLRPC$InputEncryptedFile, bArr, bArr2, j) {
                        public final /* synthetic */ int f$1;
                        public final /* synthetic */ String f$2;
                        public final /* synthetic */ TLRPC$InputFile f$3;
                        public final /* synthetic */ TLRPC$InputEncryptedFile f$4;
                        public final /* synthetic */ byte[] f$5;
                        public final /* synthetic */ byte[] f$6;
                        public final /* synthetic */ long f$7;

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

                public /* synthetic */ void lambda$fileDidUploaded$2$ImageLoader$3(int i, String str, TLRPC$InputFile tLRPC$InputFile, TLRPC$InputEncryptedFile tLRPC$InputEncryptedFile, byte[] bArr, byte[] bArr2, long j) {
                    AndroidUtilities.runOnUIThread(new Runnable(i, str, tLRPC$InputFile, tLRPC$InputEncryptedFile, bArr, bArr2, j) {
                        public final /* synthetic */ int f$0;
                        public final /* synthetic */ String f$1;
                        public final /* synthetic */ TLRPC$InputFile f$2;
                        public final /* synthetic */ TLRPC$InputEncryptedFile f$3;
                        public final /* synthetic */ byte[] f$4;
                        public final /* synthetic */ byte[] f$5;
                        public final /* synthetic */ long f$6;

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
                        public final /* synthetic */ int f$1;
                        public final /* synthetic */ String f$2;
                        public final /* synthetic */ boolean f$3;

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
                        public final /* synthetic */ int f$0;
                        public final /* synthetic */ String f$1;
                        public final /* synthetic */ boolean f$2;

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
                        public final /* synthetic */ File f$1;
                        public final /* synthetic */ String f$2;
                        public final /* synthetic */ int f$3;
                        public final /* synthetic */ int f$4;

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
                        public final /* synthetic */ String f$1;
                        public final /* synthetic */ int f$2;
                        public final /* synthetic */ int f$3;

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

                public void fileLoadProgressChanged(String str, long j, long j2) {
                    ImageLoader.this.fileProgresses.put(str, new long[]{j, j2});
                    long currentTimeMillis = System.currentTimeMillis();
                    if (ImageLoader.this.lastProgressUpdateTime == 0 || ImageLoader.this.lastProgressUpdateTime < currentTimeMillis - 500 || j == 0) {
                        long unused = ImageLoader.this.lastProgressUpdateTime = currentTimeMillis;
                        AndroidUtilities.runOnUIThread(new Runnable(i, str, j, j2) {
                            public final /* synthetic */ int f$0;
                            public final /* synthetic */ String f$1;
                            public final /* synthetic */ long f$2;
                            public final /* synthetic */ long f$3;

                            {
                                this.f$0 = r1;
                                this.f$1 = r2;
                                this.f$2 = r3;
                                this.f$3 = r5;
                            }

                            public final void run() {
                                NotificationCenter.getInstance(this.f$0).postNotificationName(NotificationCenter.FileLoadProgressChanged, this.f$1, Long.valueOf(this.f$2), Long.valueOf(this.f$3));
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
            public final /* synthetic */ SparseArray f$0;

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
        AndroidUtilities.createEmptyFile(new File(cacheDir, ".nomedia"));
        sparseArray.put(4, cacheDir);
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("cache path = " + cacheDir);
        }
        try {
            if ("mounted".equals(Environment.getExternalStorageState())) {
                File file = new File(Environment.getExternalStorageDirectory(), "Telegram");
                this.telegramPath = file;
                file.mkdirs();
                if (this.telegramPath.isDirectory()) {
                    try {
                        File file2 = new File(this.telegramPath, "Telegram Images");
                        file2.mkdir();
                        if (file2.isDirectory() && canMoveFiles(cacheDir, file2, 0)) {
                            sparseArray.put(0, file2);
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.d("image path = " + file2);
                            }
                        }
                    } catch (Exception e2) {
                        FileLog.e((Throwable) e2);
                    }
                    try {
                        File file3 = new File(this.telegramPath, "Telegram Video");
                        file3.mkdir();
                        if (file3.isDirectory() && canMoveFiles(cacheDir, file3, 2)) {
                            sparseArray.put(2, file3);
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.d("video path = " + file3);
                            }
                        }
                    } catch (Exception e3) {
                        FileLog.e((Throwable) e3);
                    }
                    try {
                        File file4 = new File(this.telegramPath, "Telegram Audio");
                        file4.mkdir();
                        if (file4.isDirectory() && canMoveFiles(cacheDir, file4, 1)) {
                            AndroidUtilities.createEmptyFile(new File(file4, ".nomedia"));
                            sparseArray.put(1, file4);
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.d("audio path = " + file4);
                            }
                        }
                    } catch (Exception e4) {
                        FileLog.e((Throwable) e4);
                    }
                    try {
                        File file5 = new File(this.telegramPath, "Telegram Documents");
                        file5.mkdir();
                        if (file5.isDirectory() && canMoveFiles(cacheDir, file5, 3)) {
                            AndroidUtilities.createEmptyFile(new File(file5, ".nomedia"));
                            sparseArray.put(3, file5);
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.d("documents path = " + file5);
                            }
                        }
                    } catch (Exception e5) {
                        FileLog.e((Throwable) e5);
                    }
                }
            } else if (BuildVars.LOGS_ENABLED) {
                FileLog.d("this Android can't rename files");
            }
            SharedConfig.checkSaveToGalleryFiles();
        } catch (Exception e6) {
            FileLog.e((Throwable) e6);
        }
        return sparseArray;
    }

    /* JADX WARNING: Removed duplicated region for block: B:31:0x006d A[SYNTHETIC, Splitter:B:31:0x006d] */
    /* JADX WARNING: Removed duplicated region for block: B:36:0x0079 A[SYNTHETIC, Splitter:B:36:0x0079] */
    /* JADX WARNING: Removed duplicated region for block: B:43:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean canMoveFiles(java.io.File r6, java.io.File r7, int r8) {
        /*
            r5 = this;
            r0 = 1
            java.lang.String r1 = "000000000_999999.f"
            java.lang.String r2 = "000000000_999999_temp.f"
            r3 = 0
            if (r8 != 0) goto L_0x0017
            java.io.File r8 = new java.io.File     // Catch:{ Exception -> 0x0015 }
            r8.<init>(r6, r2)     // Catch:{ Exception -> 0x0015 }
            java.io.File r6 = new java.io.File     // Catch:{ Exception -> 0x0015 }
            r6.<init>(r7, r1)     // Catch:{ Exception -> 0x0015 }
            goto L_0x0042
        L_0x0013:
            r6 = move-exception
            goto L_0x0077
        L_0x0015:
            r6 = move-exception
            goto L_0x0068
        L_0x0017:
            r4 = 3
            if (r8 != r4) goto L_0x0025
            java.io.File r8 = new java.io.File     // Catch:{ Exception -> 0x0015 }
            r8.<init>(r6, r2)     // Catch:{ Exception -> 0x0015 }
            java.io.File r6 = new java.io.File     // Catch:{ Exception -> 0x0015 }
            r6.<init>(r7, r1)     // Catch:{ Exception -> 0x0015 }
            goto L_0x0042
        L_0x0025:
            if (r8 != r0) goto L_0x0032
            java.io.File r8 = new java.io.File     // Catch:{ Exception -> 0x0015 }
            r8.<init>(r6, r2)     // Catch:{ Exception -> 0x0015 }
            java.io.File r6 = new java.io.File     // Catch:{ Exception -> 0x0015 }
            r6.<init>(r7, r1)     // Catch:{ Exception -> 0x0015 }
            goto L_0x0042
        L_0x0032:
            r4 = 2
            if (r8 != r4) goto L_0x0040
            java.io.File r8 = new java.io.File     // Catch:{ Exception -> 0x0015 }
            r8.<init>(r6, r2)     // Catch:{ Exception -> 0x0015 }
            java.io.File r6 = new java.io.File     // Catch:{ Exception -> 0x0015 }
            r6.<init>(r7, r1)     // Catch:{ Exception -> 0x0015 }
            goto L_0x0042
        L_0x0040:
            r6 = r3
            r8 = r6
        L_0x0042:
            r7 = 1024(0x400, float:1.435E-42)
            byte[] r7 = new byte[r7]     // Catch:{ Exception -> 0x0015 }
            r8.createNewFile()     // Catch:{ Exception -> 0x0015 }
            java.io.RandomAccessFile r1 = new java.io.RandomAccessFile     // Catch:{ Exception -> 0x0015 }
            java.lang.String r2 = "rws"
            r1.<init>(r8, r2)     // Catch:{ Exception -> 0x0015 }
            r1.write(r7)     // Catch:{ Exception -> 0x0066, all -> 0x0063 }
            r1.close()     // Catch:{ Exception -> 0x0066, all -> 0x0063 }
            boolean r7 = r8.renameTo(r6)     // Catch:{ Exception -> 0x0015 }
            r8.delete()     // Catch:{ Exception -> 0x0015 }
            r6.delete()     // Catch:{ Exception -> 0x0015 }
            if (r7 == 0) goto L_0x0075
            return r0
        L_0x0063:
            r6 = move-exception
            r3 = r1
            goto L_0x0077
        L_0x0066:
            r6 = move-exception
            r3 = r1
        L_0x0068:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r6)     // Catch:{ all -> 0x0013 }
            if (r3 == 0) goto L_0x0075
            r3.close()     // Catch:{ Exception -> 0x0071 }
            goto L_0x0075
        L_0x0071:
            r6 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r6)
        L_0x0075:
            r6 = 0
            return r6
        L_0x0077:
            if (r3 == 0) goto L_0x0081
            r3.close()     // Catch:{ Exception -> 0x007d }
            goto L_0x0081
        L_0x007d:
            r7 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r7)
        L_0x0081:
            throw r6
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ImageLoader.canMoveFiles(java.io.File, java.io.File, int):boolean");
    }

    public Float getFileProgress(String str) {
        long[] jArr;
        if (str == null || (jArr = this.fileProgresses.get(str)) == null) {
            return null;
        }
        if (jArr[1] == 0) {
            return Float.valueOf(0.0f);
        }
        return Float.valueOf(Math.min(1.0f, ((float) jArr[0]) / ((float) jArr[1])));
    }

    public long[] getFileProgressSizes(String str) {
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
                public final /* synthetic */ boolean f$1;
                public final /* synthetic */ ImageReceiver f$2;

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
        } else if (tLObject instanceof TLRPC$FileLocation) {
            TLRPC$FileLocation tLRPC$FileLocation = (TLRPC$FileLocation) tLObject;
            str3 = tLRPC$FileLocation.volume_id + "_" + tLRPC$FileLocation.local_id;
        } else if (tLObject instanceof TLRPC$Document) {
            TLRPC$Document tLRPC$Document = (TLRPC$Document) tLObject;
            str3 = tLRPC$Document.dc_id + "_" + tLRPC$Document.id;
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
                public final /* synthetic */ String f$1;
                public final /* synthetic */ String f$2;
                public final /* synthetic */ ImageLocation f$3;

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
                public final /* synthetic */ String f$1;

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
                int i7 = this.lastImageNum + 1;
                this.lastImageNum = i7;
                if (i7 == Integer.MAX_VALUE) {
                    this.lastImageNum = 0;
                }
            }
            int i8 = tag;
            boolean isNeedsQualityThumb = imageReceiver.isNeedsQualityThumb();
            Object parentObject = imageReceiver.getParentObject();
            TLRPC$Document qulityThumbDocument = imageReceiver.getQulityThumbDocument();
            boolean isShouldGenerateQualityThumb = imageReceiver.isShouldGenerateQualityThumb();
            int currentAccount = imageReceiver.getCurrentAccount();
            boolean z = i6 == 0 && imageReceiver.isCurrentKeyQuality();
            $$Lambda$ImageLoader$NAwQrHUBEJ5v2N5PPZ_I1OIh4Js r20 = r0;
            DispatchQueue dispatchQueue = this.imageLoadQueue;
            $$Lambda$ImageLoader$NAwQrHUBEJ5v2N5PPZ_I1OIh4Js r0 = new Runnable(this, i4, str2, str, i8, imageReceiver, i5, str4, i3, imageLocation, z, parentObject, qulityThumbDocument, isNeedsQualityThumb, isShouldGenerateQualityThumb, i2, i, str3, currentAccount) {
                public final /* synthetic */ ImageLoader f$0;
                public final /* synthetic */ int f$1;
                public final /* synthetic */ boolean f$10;
                public final /* synthetic */ Object f$11;
                public final /* synthetic */ TLRPC$Document f$12;
                public final /* synthetic */ boolean f$13;
                public final /* synthetic */ boolean f$14;
                public final /* synthetic */ int f$15;
                public final /* synthetic */ int f$16;
                public final /* synthetic */ String f$17;
                public final /* synthetic */ int f$18;
                public final /* synthetic */ String f$2;
                public final /* synthetic */ String f$3;
                public final /* synthetic */ int f$4;
                public final /* synthetic */ ImageReceiver f$5;
                public final /* synthetic */ int f$6;
                public final /* synthetic */ String f$7;
                public final /* synthetic */ int f$8;
                public final /* synthetic */ ImageLocation f$9;

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

    /* JADX WARNING: Code restructure failed: missing block: B:67:0x0193, code lost:
        if (r8.exists() == false) goto L_0x0195;
     */
    /* JADX WARNING: Removed duplicated region for block: B:219:0x0490  */
    /* JADX WARNING: Removed duplicated region for block: B:220:0x049a  */
    /* JADX WARNING: Removed duplicated region for block: B:66:0x018a  */
    /* JADX WARNING: Removed duplicated region for block: B:70:0x0198  */
    /* JADX WARNING: Removed duplicated region for block: B:72:0x019c  */
    /* JADX WARNING: Removed duplicated region for block: B:82:0x01eb  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$createLoadOperationForImageReceiver$5$ImageLoader(int r23, java.lang.String r24, java.lang.String r25, int r26, org.telegram.messenger.ImageReceiver r27, int r28, java.lang.String r29, int r30, org.telegram.messenger.ImageLocation r31, boolean r32, java.lang.Object r33, org.telegram.tgnet.TLRPC$Document r34, boolean r35, boolean r36, int r37, int r38, java.lang.String r39, int r40) {
        /*
            r22 = this;
            r0 = r22
            r1 = r23
            r2 = r24
            r9 = r25
            r10 = r26
            r11 = r27
            r12 = r29
            r13 = r31
            r14 = r33
            r15 = r34
            r8 = r37
            r7 = r38
            r6 = 0
            r5 = 2
            if (r1 == r5) goto L_0x00a6
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
            r9 = r28
            r5.setImageReceiverGuid(r11, r9)
            r18 = r3
            r16 = r4
            r6 = 1
            r9 = 1
            r19 = 0
            goto L_0x007d
        L_0x0046:
            r9 = r28
            if (r5 != r3) goto L_0x006a
            r18 = r3
            if (r4 != 0) goto L_0x0063
            r3 = r5
            r16 = r4
            r5 = 1
            r4 = r27
            r9 = 1
            r5 = r25
            r19 = 0
            r6 = r29
            r7 = r30
            r8 = r28
            r3.replaceImageReceiver(r4, r5, r6, r7, r8)
            goto L_0x0068
        L_0x0063:
            r16 = r4
            r9 = 1
            r19 = 0
        L_0x0068:
            r6 = 1
            goto L_0x007d
        L_0x006a:
            r18 = r3
            r16 = r4
            r9 = 1
            r19 = 0
            r5.removeImageReceiver(r11)
            goto L_0x007c
        L_0x0075:
            r18 = r3
            r16 = r4
            r9 = 1
            r19 = 0
        L_0x007c:
            r6 = 0
        L_0x007d:
            if (r6 != 0) goto L_0x0091
            if (r16 == 0) goto L_0x0091
            r3 = r16
            r4 = r27
            r5 = r25
            r6 = r29
            r7 = r30
            r8 = r28
            r3.addImageReceiver(r4, r5, r6, r7, r8)
            r6 = 1
        L_0x0091:
            if (r6 != 0) goto L_0x00aa
            if (r18 == 0) goto L_0x00aa
            r3 = r18
            r4 = r27
            r5 = r25
            r6 = r29
            r7 = r30
            r8 = r28
            r3.addImageReceiver(r4, r5, r6, r7, r8)
            r6 = 1
            goto L_0x00aa
        L_0x00a6:
            r9 = 1
            r19 = 0
            r6 = 0
        L_0x00aa:
            if (r6 != 0) goto L_0x05fb
            java.lang.String r3 = r13.path
            java.lang.String r8 = "athumb"
            java.lang.String r4 = "_"
            r7 = 4
            if (r3 == 0) goto L_0x010d
            java.lang.String r10 = "http"
            boolean r10 = r3.startsWith(r10)
            if (r10 != 0) goto L_0x0104
            boolean r10 = r3.startsWith(r8)
            if (r10 != 0) goto L_0x0104
            java.lang.String r10 = "thumb://"
            boolean r10 = r3.startsWith(r10)
            java.lang.String r15 = ":"
            if (r10 == 0) goto L_0x00e2
            r10 = 8
            int r10 = r3.indexOf(r15, r10)
            if (r10 < 0) goto L_0x00e0
            java.io.File r15 = new java.io.File
            int r10 = r10 + r9
            java.lang.String r3 = r3.substring(r10)
            r15.<init>(r3)
            goto L_0x0102
        L_0x00e0:
            r15 = 0
            goto L_0x0102
        L_0x00e2:
            java.lang.String r10 = "vthumb://"
            boolean r10 = r3.startsWith(r10)
            if (r10 == 0) goto L_0x00fd
            r10 = 9
            int r10 = r3.indexOf(r15, r10)
            if (r10 < 0) goto L_0x00e0
            java.io.File r15 = new java.io.File
            int r10 = r10 + r9
            java.lang.String r3 = r3.substring(r10)
            r15.<init>(r3)
            goto L_0x0102
        L_0x00fd:
            java.io.File r15 = new java.io.File
            r15.<init>(r3)
        L_0x0102:
            r3 = 1
            goto L_0x0106
        L_0x0104:
            r3 = 0
            r15 = 0
        L_0x0106:
            r6 = r3
            r21 = r8
            r3 = 2
            r7 = 0
            goto L_0x01fa
        L_0x010d:
            if (r1 != 0) goto L_0x01f4
            if (r32 == 0) goto L_0x01f4
            boolean r3 = r14 instanceof org.telegram.messenger.MessageObject
            if (r3 == 0) goto L_0x012d
            r3 = r14
            org.telegram.messenger.MessageObject r3 = (org.telegram.messenger.MessageObject) r3
            org.telegram.tgnet.TLRPC$Document r15 = r3.getDocument()
            org.telegram.tgnet.TLRPC$Message r5 = r3.messageOwner
            java.lang.String r6 = r5.attachPath
            java.io.File r5 = org.telegram.messenger.FileLoader.getPathToMessage(r5)
            int r3 = r3.getMediaType()
            r20 = r5
            r5 = r6
            r6 = 0
            goto L_0x0148
        L_0x012d:
            if (r15 == 0) goto L_0x0142
            java.io.File r3 = org.telegram.messenger.FileLoader.getPathToAttach(r15, r9)
            boolean r5 = org.telegram.messenger.MessageObject.isVideoDocument(r34)
            if (r5 == 0) goto L_0x013b
            r6 = 2
            goto L_0x013c
        L_0x013b:
            r6 = 3
        L_0x013c:
            r20 = r3
            r3 = r6
            r5 = 0
            r6 = 1
            goto L_0x0148
        L_0x0142:
            r3 = 0
            r5 = 0
            r6 = 0
            r15 = 0
            r20 = 0
        L_0x0148:
            if (r15 == 0) goto L_0x01ef
            if (r35 == 0) goto L_0x0180
            java.io.File r9 = new java.io.File
            r21 = r8
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
            r9.<init>(r8, r7)
            boolean r7 = r9.exists()
            if (r7 != 0) goto L_0x017e
            goto L_0x0182
        L_0x017e:
            r7 = 1
            goto L_0x0184
        L_0x0180:
            r21 = r8
        L_0x0182:
            r7 = 0
            r9 = 0
        L_0x0184:
            boolean r8 = android.text.TextUtils.isEmpty(r5)
            if (r8 != 0) goto L_0x0195
            java.io.File r8 = new java.io.File
            r8.<init>(r5)
            boolean r5 = r8.exists()
            if (r5 != 0) goto L_0x0196
        L_0x0195:
            r8 = 0
        L_0x0196:
            if (r8 != 0) goto L_0x019a
            r8 = r20
        L_0x019a:
            if (r9 != 0) goto L_0x01eb
            java.lang.String r1 = org.telegram.messenger.FileLoader.getAttachFileName(r15)
            java.util.HashMap<java.lang.String, org.telegram.messenger.ImageLoader$ThumbGenerateInfo> r2 = r0.waitingForQualityThumb
            java.lang.Object r2 = r2.get(r1)
            org.telegram.messenger.ImageLoader$ThumbGenerateInfo r2 = (org.telegram.messenger.ImageLoader.ThumbGenerateInfo) r2
            if (r2 != 0) goto L_0x01be
            org.telegram.messenger.ImageLoader$ThumbGenerateInfo r2 = new org.telegram.messenger.ImageLoader$ThumbGenerateInfo
            r4 = 0
            r2.<init>()
            org.telegram.tgnet.TLRPC$Document unused = r2.parentDocument = r15
            java.lang.String unused = r2.filter = r12
            boolean unused = r2.big = r6
            java.util.HashMap<java.lang.String, org.telegram.messenger.ImageLoader$ThumbGenerateInfo> r4 = r0.waitingForQualityThumb
            r4.put(r1, r2)
        L_0x01be:
            java.util.ArrayList r4 = r2.imageReceiverArray
            boolean r4 = r4.contains(r11)
            if (r4 != 0) goto L_0x01da
            java.util.ArrayList r4 = r2.imageReceiverArray
            r4.add(r11)
            java.util.ArrayList r4 = r2.imageReceiverGuidsArray
            java.lang.Integer r5 = java.lang.Integer.valueOf(r28)
            r4.add(r5)
        L_0x01da:
            android.util.SparseArray<java.lang.String> r4 = r0.waitingForQualityThumbByTag
            r4.put(r10, r1)
            boolean r1 = r8.exists()
            if (r1 == 0) goto L_0x01ea
            if (r36 == 0) goto L_0x01ea
            r0.generateThumb(r3, r8, r2)
        L_0x01ea:
            return
        L_0x01eb:
            r15 = r9
            r3 = 2
            r6 = 1
            goto L_0x01fa
        L_0x01ef:
            r21 = r8
            r3 = 2
            r6 = 1
            goto L_0x01f8
        L_0x01f4:
            r21 = r8
            r3 = 2
            r6 = 0
        L_0x01f8:
            r7 = 0
            r15 = 0
        L_0x01fa:
            if (r1 == r3) goto L_0x05fb
            boolean r5 = r31.isEncrypted()
            org.telegram.messenger.ImageLoader$CacheImage r9 = new org.telegram.messenger.ImageLoader$CacheImage
            r8 = 0
            r9.<init>()
            r10 = r31
            if (r32 != 0) goto L_0x0258
            int r8 = r10.imageType
            if (r8 == r3) goto L_0x0256
            org.telegram.messenger.WebFile r3 = r10.webFile
            boolean r3 = org.telegram.messenger.MessageObject.isGifDocument((org.telegram.messenger.WebFile) r3)
            if (r3 != 0) goto L_0x0255
            org.telegram.tgnet.TLRPC$Document r3 = r10.document
            boolean r3 = org.telegram.messenger.MessageObject.isGifDocument((org.telegram.tgnet.TLRPC$Document) r3)
            if (r3 != 0) goto L_0x0255
            org.telegram.tgnet.TLRPC$Document r3 = r10.document
            boolean r3 = org.telegram.messenger.MessageObject.isRoundVideoDocument(r3)
            if (r3 == 0) goto L_0x0227
            goto L_0x0255
        L_0x0227:
            java.lang.String r3 = r10.path
            if (r3 == 0) goto L_0x0258
            java.lang.String r8 = "vthumb"
            boolean r8 = r3.startsWith(r8)
            if (r8 != 0) goto L_0x0258
            java.lang.String r8 = "thumb"
            boolean r8 = r3.startsWith(r8)
            if (r8 != 0) goto L_0x0258
            java.lang.String r8 = "jpg"
            java.lang.String r3 = getHttpUrlExtension(r3, r8)
            java.lang.String r8 = "mp4"
            boolean r8 = r3.equals(r8)
            if (r8 != 0) goto L_0x0251
            java.lang.String r8 = "gif"
            boolean r3 = r3.equals(r8)
            if (r3 == 0) goto L_0x0258
        L_0x0251:
            r3 = 2
            r9.imageType = r3
            goto L_0x0258
        L_0x0255:
            r3 = 2
        L_0x0256:
            r9.imageType = r3
        L_0x0258:
            if (r15 != 0) goto L_0x049f
            org.telegram.tgnet.TLRPC$PhotoSize r3 = r10.photoSize
            boolean r3 = r3 instanceof org.telegram.tgnet.TLRPC$TL_photoStrippedSize
            java.lang.String r8 = "g"
            if (r3 == 0) goto L_0x026c
            r0 = r37
            r5 = r7
        L_0x0265:
            r6 = r8
            r1 = 1
            r3 = 0
            r4 = 1
        L_0x0269:
            r7 = 4
            goto L_0x048a
        L_0x026c:
            org.telegram.messenger.SecureDocument r3 = r10.secureDocument
            if (r3 == 0) goto L_0x0290
            r9.secureDocument = r3
            org.telegram.tgnet.TLRPC$TL_secureFile r3 = r3.secureFile
            int r3 = r3.dc_id
            r4 = -2147483648(0xfffffffvar_, float:-0.0)
            if (r3 != r4) goto L_0x027c
            r6 = 1
            goto L_0x027d
        L_0x027c:
            r6 = 0
        L_0x027d:
            java.io.File r3 = new java.io.File
            r4 = 4
            java.io.File r5 = org.telegram.messenger.FileLoader.getDirectory(r4)
            r3.<init>(r5, r2)
            r0 = r37
            r15 = r3
            r4 = r6
            r5 = r7
            r6 = r8
            r1 = 1
            r3 = 0
            goto L_0x0269
        L_0x0290:
            boolean r3 = r8.equals(r12)
            java.lang.String r13 = ".svg"
            java.lang.String r14 = "application/x-tgwallpattern"
            java.lang.String r15 = "application/x-tgsticker"
            r34 = r6
            java.lang.String r6 = "application/x-tgsdice"
            if (r3 != 0) goto L_0x034a
            r3 = r37
            r11 = r38
            r35 = r7
            if (r3 != 0) goto L_0x02b0
            if (r11 <= 0) goto L_0x02b0
            java.lang.String r7 = r10.path
            if (r7 != 0) goto L_0x02b0
            if (r5 == 0) goto L_0x0350
        L_0x02b0:
            java.io.File r4 = new java.io.File
            r5 = 4
            java.io.File r7 = org.telegram.messenger.FileLoader.getDirectory(r5)
            r4.<init>(r7, r2)
            boolean r7 = r4.exists()
            if (r7 == 0) goto L_0x02c2
            r1 = 1
            goto L_0x02e1
        L_0x02c2:
            r7 = 2
            if (r3 != r7) goto L_0x02df
            java.io.File r4 = new java.io.File
            java.io.File r7 = org.telegram.messenger.FileLoader.getDirectory(r5)
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            r5.append(r2)
            java.lang.String r1 = ".enc"
            r5.append(r1)
            java.lang.String r1 = r5.toString()
            r4.<init>(r7, r1)
        L_0x02df:
            r1 = r35
        L_0x02e1:
            org.telegram.tgnet.TLRPC$Document r5 = r10.document
            if (r5 == 0) goto L_0x033f
            boolean r7 = r5 instanceof org.telegram.messenger.DocumentObject.ThemeDocument
            if (r7 == 0) goto L_0x0300
            org.telegram.messenger.DocumentObject$ThemeDocument r5 = (org.telegram.messenger.DocumentObject.ThemeDocument) r5
            org.telegram.tgnet.TLRPC$Document r5 = r5.wallpaper
            if (r5 != 0) goto L_0x02f1
            r5 = 1
            goto L_0x02f3
        L_0x02f1:
            r5 = r34
        L_0x02f3:
            r6 = 5
            r9.imageType = r6
            r0 = r3
            r15 = r4
            r4 = r5
            r6 = r8
            r3 = 0
            r7 = 4
            r5 = r1
            r1 = 1
            goto L_0x048a
        L_0x0300:
            java.lang.String r5 = r5.mime_type
            boolean r5 = r6.equals(r5)
            if (r5 == 0) goto L_0x0310
            r5 = 1
            r9.imageType = r5
            r5 = r1
            r0 = r3
            r15 = r4
            goto L_0x0265
        L_0x0310:
            r5 = 1
            org.telegram.tgnet.TLRPC$Document r6 = r10.document
            java.lang.String r6 = r6.mime_type
            boolean r6 = r15.equals(r6)
            if (r6 == 0) goto L_0x031e
            r9.imageType = r5
            goto L_0x033f
        L_0x031e:
            org.telegram.tgnet.TLRPC$Document r5 = r10.document
            java.lang.String r5 = r5.mime_type
            boolean r5 = r14.equals(r5)
            if (r5 == 0) goto L_0x032c
            r5 = 3
            r9.imageType = r5
            goto L_0x033f
        L_0x032c:
            boolean r5 = org.telegram.messenger.BuildVars.DEBUG_PRIVATE_VERSION
            if (r5 == 0) goto L_0x033f
            org.telegram.tgnet.TLRPC$Document r5 = r10.document
            java.lang.String r5 = org.telegram.messenger.FileLoader.getDocumentFileName(r5)
            boolean r5 = r5.endsWith(r13)
            if (r5 == 0) goto L_0x033f
            r5 = 4
            r9.imageType = r5
        L_0x033f:
            r5 = r1
            r0 = r3
            r15 = r4
            r6 = r8
            r1 = 1
            r3 = 0
            r7 = 4
            r4 = r34
            goto L_0x048a
        L_0x034a:
            r3 = r37
            r11 = r38
            r35 = r7
        L_0x0350:
            org.telegram.tgnet.TLRPC$Document r1 = r10.document
            java.lang.String r5 = ".temp"
            if (r1 == 0) goto L_0x0413
            boolean r7 = r1 instanceof org.telegram.tgnet.TLRPC$TL_documentEncrypted
            if (r7 == 0) goto L_0x0366
            java.io.File r7 = new java.io.File
            r18 = 4
            java.io.File r11 = org.telegram.messenger.FileLoader.getDirectory(r18)
            r7.<init>(r11, r2)
            goto L_0x0381
        L_0x0366:
            boolean r7 = org.telegram.messenger.MessageObject.isVideoDocument(r1)
            if (r7 == 0) goto L_0x0377
            java.io.File r7 = new java.io.File
            r11 = 2
            java.io.File r0 = org.telegram.messenger.FileLoader.getDirectory(r11)
            r7.<init>(r0, r2)
            goto L_0x0381
        L_0x0377:
            java.io.File r7 = new java.io.File
            r0 = 3
            java.io.File r11 = org.telegram.messenger.FileLoader.getDirectory(r0)
            r7.<init>(r11, r2)
        L_0x0381:
            boolean r0 = r8.equals(r12)
            if (r0 == 0) goto L_0x03b3
            boolean r0 = r7.exists()
            if (r0 != 0) goto L_0x03b3
            java.io.File r7 = new java.io.File
            r0 = 4
            java.io.File r11 = org.telegram.messenger.FileLoader.getDirectory(r0)
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r32 = r8
            int r8 = r1.dc_id
            r0.append(r8)
            r0.append(r4)
            long r3 = r1.id
            r0.append(r3)
            r0.append(r5)
            java.lang.String r0 = r0.toString()
            r7.<init>(r11, r0)
            goto L_0x03b5
        L_0x03b3:
            r32 = r8
        L_0x03b5:
            boolean r0 = r1 instanceof org.telegram.messenger.DocumentObject.ThemeDocument
            if (r0 == 0) goto L_0x03c9
            r0 = r1
            org.telegram.messenger.DocumentObject$ThemeDocument r0 = (org.telegram.messenger.DocumentObject.ThemeDocument) r0
            org.telegram.tgnet.TLRPC$Document r0 = r0.wallpaper
            if (r0 != 0) goto L_0x03c3
            r0 = 5
            r4 = 1
            goto L_0x03c6
        L_0x03c3:
            r4 = r34
            r0 = 5
        L_0x03c6:
            r9.imageType = r0
            goto L_0x0405
        L_0x03c9:
            org.telegram.tgnet.TLRPC$Document r0 = r10.document
            java.lang.String r0 = r0.mime_type
            boolean r0 = r6.equals(r0)
            if (r0 == 0) goto L_0x03d8
            r0 = 1
            r9.imageType = r0
            r4 = 1
            goto L_0x0405
        L_0x03d8:
            r0 = 1
            java.lang.String r3 = r1.mime_type
            boolean r3 = r15.equals(r3)
            if (r3 == 0) goto L_0x03e4
            r9.imageType = r0
            goto L_0x0403
        L_0x03e4:
            java.lang.String r0 = r1.mime_type
            boolean r0 = r14.equals(r0)
            if (r0 == 0) goto L_0x03f0
            r0 = 3
            r9.imageType = r0
            goto L_0x0403
        L_0x03f0:
            boolean r0 = org.telegram.messenger.BuildVars.DEBUG_PRIVATE_VERSION
            if (r0 == 0) goto L_0x0403
            org.telegram.tgnet.TLRPC$Document r0 = r10.document
            java.lang.String r0 = org.telegram.messenger.FileLoader.getDocumentFileName(r0)
            boolean r0 = r0.endsWith(r13)
            if (r0 == 0) goto L_0x0403
            r0 = 4
            r9.imageType = r0
        L_0x0403:
            r4 = r34
        L_0x0405:
            int r6 = r1.size
            r5 = r35
            r0 = r37
            r3 = r6
            r15 = r7
            r1 = 1
            r7 = 4
            r6 = r32
            goto L_0x048a
        L_0x0413:
            r32 = r8
            org.telegram.messenger.WebFile r0 = r10.webFile
            if (r0 == 0) goto L_0x0430
            java.io.File r0 = new java.io.File
            r1 = 3
            java.io.File r1 = org.telegram.messenger.FileLoader.getDirectory(r1)
            r0.<init>(r1, r2)
            r6 = r32
            r4 = r34
            r5 = r35
            r15 = r0
            r1 = 1
            r3 = 0
            r7 = 4
            r0 = r37
            goto L_0x048a
        L_0x0430:
            r0 = r37
            r1 = 1
            if (r0 != r1) goto L_0x0440
            java.io.File r3 = new java.io.File
            r6 = 4
            java.io.File r7 = org.telegram.messenger.FileLoader.getDirectory(r6)
            r3.<init>(r7, r2)
            goto L_0x0449
        L_0x0440:
            java.io.File r3 = new java.io.File
            java.io.File r6 = org.telegram.messenger.FileLoader.getDirectory(r19)
            r3.<init>(r6, r2)
        L_0x0449:
            r6 = r32
            boolean r7 = r6.equals(r12)
            if (r7 == 0) goto L_0x0483
            org.telegram.tgnet.TLRPC$TL_fileLocationToBeDeprecated r7 = r10.location
            if (r7 == 0) goto L_0x0483
            boolean r7 = r3.exists()
            if (r7 != 0) goto L_0x0483
            java.io.File r3 = new java.io.File
            r7 = 4
            java.io.File r8 = org.telegram.messenger.FileLoader.getDirectory(r7)
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            org.telegram.tgnet.TLRPC$TL_fileLocationToBeDeprecated r13 = r10.location
            long r13 = r13.volume_id
            r11.append(r13)
            r11.append(r4)
            org.telegram.tgnet.TLRPC$TL_fileLocationToBeDeprecated r4 = r10.location
            int r4 = r4.local_id
            r11.append(r4)
            r11.append(r5)
            java.lang.String r4 = r11.toString()
            r3.<init>(r8, r4)
            goto L_0x0484
        L_0x0483:
            r7 = 4
        L_0x0484:
            r4 = r34
            r5 = r35
            r15 = r3
            r3 = 0
        L_0x048a:
            boolean r6 = r6.equals(r12)
            if (r6 == 0) goto L_0x049a
            r6 = 2
            r9.imageType = r6
            r9.size = r3
            r8 = r30
            r13 = r5
            r11 = 1
            goto L_0x04ad
        L_0x049a:
            r8 = r30
            r11 = r4
            r13 = r5
            goto L_0x04ad
        L_0x049f:
            r0 = r37
            r34 = r6
            r35 = r7
            r1 = 1
            r7 = 4
            r8 = r30
            r11 = r34
            r13 = r35
        L_0x04ad:
            r9.type = r8
            r14 = r25
            r9.key = r14
            r9.filter = r12
            r9.imageLocation = r10
            r6 = r39
            r9.ext = r6
            r5 = r40
            r9.currentAccount = r5
            r4 = r33
            r9.parentObject = r4
            int r3 = r10.imageType
            if (r3 == 0) goto L_0x04c9
            r9.imageType = r3
        L_0x04c9:
            r3 = 2
            if (r0 != r3) goto L_0x04e8
            java.io.File r1 = new java.io.File
            java.io.File r3 = org.telegram.messenger.FileLoader.getInternalCacheDir()
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            r7.append(r2)
            java.lang.String r0 = ".enc.key"
            r7.append(r0)
            java.lang.String r0 = r7.toString()
            r1.<init>(r3, r0)
            r9.encryptionKeyPath = r1
        L_0x04e8:
            r0 = r37
            r1 = 2
            r3 = r9
            r7 = r4
            r4 = r27
            r5 = r25
            r6 = r29
            r12 = r7
            r17 = 4
            r7 = r30
            r1 = r21
            r8 = r28
            r3.addImageReceiver(r4, r5, r6, r7, r8)
            if (r11 != 0) goto L_0x05d7
            if (r13 != 0) goto L_0x05d7
            boolean r3 = r15.exists()
            if (r3 == 0) goto L_0x050b
            goto L_0x05d7
        L_0x050b:
            r9.url = r2
            r7 = r22
            java.util.HashMap<java.lang.String, org.telegram.messenger.ImageLoader$CacheImage> r3 = r7.imageLoadingByUrl
            r3.put(r2, r9)
            java.lang.String r2 = r10.path
            if (r2 == 0) goto L_0x0568
            java.lang.String r0 = org.telegram.messenger.Utilities.MD5(r2)
            java.io.File r2 = org.telegram.messenger.FileLoader.getDirectory(r17)
            java.io.File r3 = new java.io.File
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r0)
            java.lang.String r0 = "_temp.jpg"
            r4.append(r0)
            java.lang.String r0 = r4.toString()
            r3.<init>(r2, r0)
            r9.tempFilePath = r3
            r9.finalFilePath = r15
            java.lang.String r0 = r10.path
            boolean r0 = r0.startsWith(r1)
            if (r0 == 0) goto L_0x0554
            org.telegram.messenger.ImageLoader$ArtworkLoadTask r0 = new org.telegram.messenger.ImageLoader$ArtworkLoadTask
            r0.<init>(r9)
            r9.artworkTask = r0
            java.util.LinkedList<org.telegram.messenger.ImageLoader$ArtworkLoadTask> r1 = r7.artworkTasks
            r1.add(r0)
            r8 = 0
            r7.runArtworkTasks(r8)
            goto L_0x05fc
        L_0x0554:
            r8 = 0
            org.telegram.messenger.ImageLoader$HttpImageTask r0 = new org.telegram.messenger.ImageLoader$HttpImageTask
            r1 = r38
            r0.<init>(r9, r1)
            r9.httpTask = r0
            java.util.LinkedList<org.telegram.messenger.ImageLoader$HttpImageTask> r1 = r7.httpTasks
            r1.add(r0)
            r7.runHttpTasks(r8)
            goto L_0x05fc
        L_0x0568:
            r1 = r38
            r8 = 0
            org.telegram.tgnet.TLRPC$TL_fileLocationToBeDeprecated r2 = r10.location
            if (r2 == 0) goto L_0x058d
            if (r0 != 0) goto L_0x0579
            if (r1 <= 0) goto L_0x0577
            byte[] r1 = r10.key
            if (r1 == 0) goto L_0x0579
        L_0x0577:
            r6 = 1
            goto L_0x057a
        L_0x0579:
            r6 = r0
        L_0x057a:
            org.telegram.messenger.FileLoader r1 = org.telegram.messenger.FileLoader.getInstance(r40)
            if (r23 == 0) goto L_0x0582
            r5 = 2
            goto L_0x0583
        L_0x0582:
            r5 = 1
        L_0x0583:
            r2 = r31
            r3 = r33
            r4 = r39
            r1.loadFile(r2, r3, r4, r5, r6)
            goto L_0x05c5
        L_0x058d:
            org.telegram.tgnet.TLRPC$Document r1 = r10.document
            if (r1 == 0) goto L_0x05a0
            org.telegram.messenger.FileLoader r1 = org.telegram.messenger.FileLoader.getInstance(r40)
            org.telegram.tgnet.TLRPC$Document r2 = r10.document
            if (r23 == 0) goto L_0x059b
            r5 = 2
            goto L_0x059c
        L_0x059b:
            r5 = 1
        L_0x059c:
            r1.loadFile(r2, r12, r5, r0)
            goto L_0x05c5
        L_0x05a0:
            org.telegram.messenger.SecureDocument r1 = r10.secureDocument
            if (r1 == 0) goto L_0x05b3
            org.telegram.messenger.FileLoader r0 = org.telegram.messenger.FileLoader.getInstance(r40)
            org.telegram.messenger.SecureDocument r1 = r10.secureDocument
            if (r23 == 0) goto L_0x05ae
            r5 = 2
            goto L_0x05af
        L_0x05ae:
            r5 = 1
        L_0x05af:
            r0.loadFile(r1, r5)
            goto L_0x05c5
        L_0x05b3:
            org.telegram.messenger.WebFile r1 = r10.webFile
            if (r1 == 0) goto L_0x05c5
            org.telegram.messenger.FileLoader r1 = org.telegram.messenger.FileLoader.getInstance(r40)
            org.telegram.messenger.WebFile r2 = r10.webFile
            if (r23 == 0) goto L_0x05c1
            r5 = 2
            goto L_0x05c2
        L_0x05c1:
            r5 = 1
        L_0x05c2:
            r1.loadFile(r2, r5, r0)
        L_0x05c5:
            boolean r0 = r27.isForceLoding()
            if (r0 == 0) goto L_0x05fc
            java.util.HashMap<java.lang.String, java.lang.Integer> r0 = r7.forceLoadingImages
            java.lang.String r1 = r9.key
            java.lang.Integer r2 = java.lang.Integer.valueOf(r8)
            r0.put(r1, r2)
            goto L_0x05fc
        L_0x05d7:
            r7 = r22
            r9.finalFilePath = r15
            r9.imageLocation = r10
            org.telegram.messenger.ImageLoader$CacheOutTask r0 = new org.telegram.messenger.ImageLoader$CacheOutTask
            r0.<init>(r9)
            r9.cacheTask = r0
            java.util.HashMap<java.lang.String, org.telegram.messenger.ImageLoader$CacheImage> r0 = r7.imageLoadingByKeys
            r0.put(r14, r9)
            if (r23 == 0) goto L_0x05f3
            org.telegram.messenger.DispatchQueue r0 = r7.cacheThumbOutQueue
            org.telegram.messenger.ImageLoader$CacheOutTask r1 = r9.cacheTask
            r0.postRunnable(r1)
            goto L_0x05fc
        L_0x05f3:
            org.telegram.messenger.DispatchQueue r0 = r7.cacheOutQueue
            org.telegram.messenger.ImageLoader$CacheOutTask r1 = r9.cacheTask
            r0.postRunnable(r1)
            goto L_0x05fc
        L_0x05fb:
            r7 = r0
        L_0x05fc:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ImageLoader.lambda$createLoadOperationForImageReceiver$5$ImageLoader(int, java.lang.String, java.lang.String, int, org.telegram.messenger.ImageReceiver, int, java.lang.String, int, org.telegram.messenger.ImageLocation, boolean, java.lang.Object, org.telegram.tgnet.TLRPC$Document, boolean, boolean, int, int, java.lang.String, int):void");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:130:0x021a, code lost:
        if (r6.local_id >= 0) goto L_0x02f3;
     */
    /* JADX WARNING: Removed duplicated region for block: B:152:0x029c  */
    /* JADX WARNING: Removed duplicated region for block: B:158:0x02ba  */
    /* JADX WARNING: Removed duplicated region for block: B:167:0x02ea  */
    /* JADX WARNING: Removed duplicated region for block: B:168:0x02ec  */
    /* JADX WARNING: Removed duplicated region for block: B:180:0x0324  */
    /* JADX WARNING: Removed duplicated region for block: B:195:0x038d  */
    /* JADX WARNING: Removed duplicated region for block: B:205:0x03c2 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:210:0x03de  */
    /* JADX WARNING: Removed duplicated region for block: B:218:0x041d  */
    /* JADX WARNING: Removed duplicated region for block: B:220:0x0424  */
    /* JADX WARNING: Removed duplicated region for block: B:238:0x0484  */
    /* JADX WARNING: Removed duplicated region for block: B:251:0x031a A[EDGE_INSN: B:251:0x031a->B:178:0x031a ?: BREAK  , SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:31:0x0070  */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x0079  */
    /* JADX WARNING: Removed duplicated region for block: B:36:0x008a  */
    /* JADX WARNING: Removed duplicated region for block: B:44:0x00a7  */
    /* JADX WARNING: Removed duplicated region for block: B:71:0x011d  */
    /* JADX WARNING: Removed duplicated region for block: B:73:0x012a  */
    /* JADX WARNING: Removed duplicated region for block: B:80:0x013e  */
    /* JADX WARNING: Removed duplicated region for block: B:81:0x0141  */
    /* JADX WARNING: Removed duplicated region for block: B:89:0x0156  */
    /* JADX WARNING: Removed duplicated region for block: B:91:0x0159  */
    /* JADX WARNING: Removed duplicated region for block: B:92:0x015c  */
    /* JADX WARNING: Removed duplicated region for block: B:94:0x0160  */
    /* JADX WARNING: Removed duplicated region for block: B:98:0x0179  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void loadImageForImageReceiver(org.telegram.messenger.ImageReceiver r37) {
        /*
            r36 = this;
            r12 = r36
            r13 = r37
            if (r13 != 0) goto L_0x0007
            return
        L_0x0007:
            java.lang.String r6 = r37.getMediaKey()
            int r14 = r37.getNewGuid()
            r7 = 0
            r8 = 1
            if (r6 == 0) goto L_0x0055
            org.telegram.messenger.ImageLocation r0 = r37.getMediaLocation()
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
            r0 = r37
            r2 = r6
            r5 = r14
            r0.setImageBitmapByKey(r1, r2, r3, r4, r5)
            boolean r0 = r37.isForcePreview()
            if (r0 != 0) goto L_0x0053
            return
        L_0x0053:
            r0 = 1
            goto L_0x0056
        L_0x0055:
            r0 = 0
        L_0x0056:
            java.lang.String r2 = r37.getImageKey()
            if (r0 != 0) goto L_0x00a0
            if (r2 == 0) goto L_0x00a0
            org.telegram.messenger.ImageLocation r1 = r37.getImageLocation()
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
            r0 = r37
            r5 = r14
            r0.setImageBitmapByKey(r1, r2, r3, r4, r5)
            boolean r0 = r37.isForcePreview()
            if (r0 != 0) goto L_0x009e
            if (r6 != 0) goto L_0x009e
            return
        L_0x009e:
            r15 = 1
            goto L_0x00a1
        L_0x00a0:
            r15 = r0
        L_0x00a1:
            java.lang.String r2 = r37.getThumbKey()
            if (r2 == 0) goto L_0x00ea
            org.telegram.messenger.ImageLocation r0 = r37.getThumbLocation()
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
            r0 = r37
            r5 = r14
            r0.setImageBitmapByKey(r1, r2, r3, r4, r5)
            r12.cancelLoadingForImageReceiver(r13, r7)
            if (r15 == 0) goto L_0x00e8
            boolean r0 = r37.isForcePreview()
            if (r0 == 0) goto L_0x00e8
            return
        L_0x00e8:
            r0 = 1
            goto L_0x00eb
        L_0x00ea:
            r0 = 0
        L_0x00eb:
            java.lang.Object r1 = r37.getParentObject()
            org.telegram.tgnet.TLRPC$Document r2 = r37.getQulityThumbDocument()
            org.telegram.messenger.ImageLocation r5 = r37.getThumbLocation()
            java.lang.String r6 = r37.getThumbFilter()
            org.telegram.messenger.ImageLocation r3 = r37.getMediaLocation()
            java.lang.String r11 = r37.getMediaFilter()
            org.telegram.messenger.ImageLocation r4 = r37.getImageLocation()
            java.lang.String r10 = r37.getImageFilter()
            if (r4 != 0) goto L_0x0131
            boolean r9 = r37.isNeedsQualityThumb()
            if (r9 == 0) goto L_0x0131
            boolean r9 = r37.isCurrentKeyQuality()
            if (r9 == 0) goto L_0x0131
            boolean r9 = r1 instanceof org.telegram.messenger.MessageObject
            if (r9 == 0) goto L_0x012a
            r2 = r1
            org.telegram.messenger.MessageObject r2 = (org.telegram.messenger.MessageObject) r2
            org.telegram.tgnet.TLRPC$Document r2 = r2.getDocument()
            org.telegram.messenger.ImageLocation r2 = org.telegram.messenger.ImageLocation.getForDocument(r2)
        L_0x0128:
            r9 = 1
            goto L_0x0133
        L_0x012a:
            if (r2 == 0) goto L_0x0131
            org.telegram.messenger.ImageLocation r2 = org.telegram.messenger.ImageLocation.getForDocument(r2)
            goto L_0x0128
        L_0x0131:
            r2 = r4
            r9 = 0
        L_0x0133:
            java.lang.String r16 = "mp4"
            r8 = 2
            r18 = 0
            if (r2 == 0) goto L_0x0141
            int r7 = r2.imageType
            if (r7 != r8) goto L_0x0141
            r7 = r16
            goto L_0x0143
        L_0x0141:
            r7 = r18
        L_0x0143:
            r20 = r2
            if (r3 == 0) goto L_0x014c
            int r2 = r3.imageType
            if (r2 != r8) goto L_0x014c
            goto L_0x014e
        L_0x014c:
            r16 = r18
        L_0x014e:
            java.lang.String r2 = r37.getExt()
            java.lang.String r8 = "jpg"
            if (r2 != 0) goto L_0x0157
            r2 = r8
        L_0x0157:
            if (r7 != 0) goto L_0x015c
            r22 = r2
            goto L_0x015e
        L_0x015c:
            r22 = r7
        L_0x015e:
            if (r16 != 0) goto L_0x0162
            r16 = r2
        L_0x0162:
            r24 = r3
            r25 = r4
            r4 = r18
            r12 = r4
            r23 = r12
            r7 = r20
            r3 = 0
            r26 = 0
            r20 = r23
        L_0x0172:
            java.lang.String r13 = "."
            r27 = r15
            r15 = 2
            if (r3 >= r15) goto L_0x031a
            if (r3 != 0) goto L_0x0181
            r15 = r7
            r28 = r14
            r14 = r22
            goto L_0x0187
        L_0x0181:
            r28 = r14
            r14 = r16
            r15 = r24
        L_0x0187:
            if (r15 != 0) goto L_0x018e
            r29 = r0
            r30 = r7
            goto L_0x01a1
        L_0x018e:
            r29 = r0
            if (r24 == 0) goto L_0x0197
            r30 = r7
            r0 = r24
            goto L_0x019a
        L_0x0197:
            r0 = r7
            r30 = r0
        L_0x019a:
            r7 = 0
            java.lang.String r0 = r15.getKey(r1, r0, r7)
            if (r0 != 0) goto L_0x01a7
        L_0x01a1:
            r31 = r6
        L_0x01a3:
            r7 = r30
            goto L_0x030e
        L_0x01a7:
            r31 = r6
            if (r24 == 0) goto L_0x01ae
            r7 = r24
            goto L_0x01b0
        L_0x01ae:
            r7 = r30
        L_0x01b0:
            r6 = 1
            java.lang.String r7 = r15.getKey(r1, r7, r6)
            java.lang.String r6 = r15.path
            if (r6 == 0) goto L_0x01d3
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            r6.append(r7)
            r6.append(r13)
            java.lang.String r7 = r15.path
            java.lang.String r7 = getHttpUrlExtension(r7, r8)
            r6.append(r7)
            java.lang.String r7 = r6.toString()
            goto L_0x02f3
        L_0x01d3:
            org.telegram.tgnet.TLRPC$PhotoSize r6 = r15.photoSize
            boolean r6 = r6 instanceof org.telegram.tgnet.TLRPC$TL_photoStrippedSize
            if (r6 == 0) goto L_0x01ed
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            r6.append(r7)
            r6.append(r13)
            r6.append(r14)
            java.lang.String r7 = r6.toString()
            goto L_0x02f3
        L_0x01ed:
            org.telegram.tgnet.TLRPC$TL_fileLocationToBeDeprecated r6 = r15.location
            if (r6 == 0) goto L_0x0220
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            r6.append(r7)
            r6.append(r13)
            r6.append(r14)
            java.lang.String r7 = r6.toString()
            java.lang.String r6 = r37.getExt()
            if (r6 != 0) goto L_0x021c
            org.telegram.tgnet.TLRPC$TL_fileLocationToBeDeprecated r6 = r15.location
            byte[] r13 = r6.key
            if (r13 != 0) goto L_0x021c
            long r13 = r6.volume_id
            r32 = -2147483648(0xfffffffvar_, double:NaN)
            int r34 = (r13 > r32 ? 1 : (r13 == r32 ? 0 : -1))
            if (r34 != 0) goto L_0x02f3
            int r6 = r6.local_id
            if (r6 >= 0) goto L_0x02f3
        L_0x021c:
            r26 = 1
            goto L_0x02f3
        L_0x0220:
            org.telegram.messenger.WebFile r6 = r15.webFile
            if (r6 == 0) goto L_0x0246
            java.lang.String r6 = r6.mime_type
            java.lang.String r6 = org.telegram.messenger.FileLoader.getMimeTypePart(r6)
            java.lang.StringBuilder r14 = new java.lang.StringBuilder
            r14.<init>()
            r14.append(r7)
            r14.append(r13)
            org.telegram.messenger.WebFile r7 = r15.webFile
            java.lang.String r7 = r7.url
            java.lang.String r6 = getHttpUrlExtension(r7, r6)
            r14.append(r6)
            java.lang.String r7 = r14.toString()
            goto L_0x02f3
        L_0x0246:
            org.telegram.messenger.SecureDocument r6 = r15.secureDocument
            if (r6 == 0) goto L_0x025e
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            r6.append(r7)
            r6.append(r13)
            r6.append(r14)
            java.lang.String r7 = r6.toString()
            goto L_0x02f3
        L_0x025e:
            org.telegram.tgnet.TLRPC$Document r6 = r15.document
            if (r6 == 0) goto L_0x02f3
            if (r3 != 0) goto L_0x0277
            if (r9 == 0) goto L_0x0277
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            java.lang.String r13 = "q_"
            r6.append(r13)
            r6.append(r0)
            java.lang.String r0 = r6.toString()
        L_0x0277:
            org.telegram.tgnet.TLRPC$Document r6 = r15.document
            java.lang.String r6 = org.telegram.messenger.FileLoader.getDocumentFileName(r6)
            java.lang.String r13 = ""
            if (r6 == 0) goto L_0x0292
            r14 = 46
            int r14 = r6.lastIndexOf(r14)
            r26 = r0
            r0 = -1
            if (r14 != r0) goto L_0x028d
            goto L_0x0294
        L_0x028d:
            java.lang.String r0 = r6.substring(r14)
            goto L_0x0295
        L_0x0292:
            r26 = r0
        L_0x0294:
            r0 = r13
        L_0x0295:
            int r6 = r0.length()
            r14 = 1
            if (r6 > r14) goto L_0x02ba
            org.telegram.tgnet.TLRPC$Document r0 = r15.document
            java.lang.String r0 = r0.mime_type
            java.lang.String r6 = "video/mp4"
            boolean r0 = r6.equals(r0)
            if (r0 == 0) goto L_0x02ab
            java.lang.String r13 = ".mp4"
            goto L_0x02bb
        L_0x02ab:
            org.telegram.tgnet.TLRPC$Document r0 = r15.document
            java.lang.String r0 = r0.mime_type
            java.lang.String r6 = "video/x-matroska"
            boolean r0 = r6.equals(r0)
            if (r0 == 0) goto L_0x02bb
            java.lang.String r13 = ".mkv"
            goto L_0x02bb
        L_0x02ba:
            r13 = r0
        L_0x02bb:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r0.append(r7)
            r0.append(r13)
            java.lang.String r7 = r0.toString()
            org.telegram.tgnet.TLRPC$Document r0 = r15.document
            boolean r0 = org.telegram.messenger.MessageObject.isVideoDocument(r0)
            if (r0 != 0) goto L_0x02ec
            org.telegram.tgnet.TLRPC$Document r0 = r15.document
            boolean r0 = org.telegram.messenger.MessageObject.isGifDocument((org.telegram.tgnet.TLRPC$Document) r0)
            if (r0 != 0) goto L_0x02ec
            org.telegram.tgnet.TLRPC$Document r0 = r15.document
            boolean r0 = org.telegram.messenger.MessageObject.isRoundVideoDocument(r0)
            if (r0 != 0) goto L_0x02ec
            org.telegram.tgnet.TLRPC$Document r0 = r15.document
            boolean r0 = org.telegram.messenger.MessageObject.canPreviewDocument(r0)
            if (r0 != 0) goto L_0x02ec
            r0 = 1
            goto L_0x02ed
        L_0x02ec:
            r0 = 0
        L_0x02ed:
            r35 = r26
            r26 = r0
            r0 = r35
        L_0x02f3:
            if (r3 != 0) goto L_0x02f9
            r12 = r0
            r20 = r7
            goto L_0x02fc
        L_0x02f9:
            r4 = r0
            r23 = r7
        L_0x02fc:
            if (r15 != r5) goto L_0x01a3
            if (r3 != 0) goto L_0x0306
            r7 = r18
            r12 = r7
            r20 = r12
            goto L_0x030e
        L_0x0306:
            r4 = r18
            r23 = r4
            r24 = r23
            goto L_0x01a3
        L_0x030e:
            int r3 = r3 + 1
            r15 = r27
            r14 = r28
            r0 = r29
            r6 = r31
            goto L_0x0172
        L_0x031a:
            r29 = r0
            r31 = r6
            r30 = r7
            r28 = r14
            if (r5 == 0) goto L_0x038d
            org.telegram.messenger.ImageLocation r0 = r37.getStrippedLocation()
            if (r0 != 0) goto L_0x0330
            if (r24 == 0) goto L_0x032e
            r25 = r24
        L_0x032e:
            r0 = r25
        L_0x0330:
            r3 = 0
            java.lang.String r18 = r5.getKey(r1, r0, r3)
            r3 = 1
            java.lang.String r0 = r5.getKey(r1, r0, r3)
            java.lang.String r1 = r5.path
            if (r1 == 0) goto L_0x035d
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r0)
            r1.append(r13)
            java.lang.String r0 = r5.path
            java.lang.String r0 = getHttpUrlExtension(r0, r8)
            r1.append(r0)
            java.lang.String r0 = r1.toString()
        L_0x0356:
            r35 = r18
            r18 = r0
            r0 = r35
            goto L_0x0390
        L_0x035d:
            org.telegram.tgnet.TLRPC$PhotoSize r1 = r5.photoSize
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC$TL_photoStrippedSize
            if (r1 == 0) goto L_0x0376
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r0)
            r1.append(r13)
            r1.append(r2)
            java.lang.String r0 = r1.toString()
            goto L_0x0356
        L_0x0376:
            org.telegram.tgnet.TLRPC$TL_fileLocationToBeDeprecated r1 = r5.location
            if (r1 == 0) goto L_0x0356
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r0)
            r1.append(r13)
            r1.append(r2)
            java.lang.String r0 = r1.toString()
            goto L_0x0356
        L_0x038d:
            r3 = 1
            r0 = r18
        L_0x0390:
            java.lang.String r1 = "@"
            if (r4 == 0) goto L_0x03a8
            if (r11 == 0) goto L_0x03a8
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            r6.append(r4)
            r6.append(r1)
            r6.append(r11)
            java.lang.String r4 = r6.toString()
        L_0x03a8:
            r13 = r4
            if (r12 == 0) goto L_0x03c0
            if (r10 == 0) goto L_0x03c0
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r12)
            r4.append(r1)
            r4.append(r10)
            java.lang.String r4 = r4.toString()
            r12 = r4
        L_0x03c0:
            if (r0 == 0) goto L_0x03d9
            if (r31 == 0) goto L_0x03d9
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r0)
            r4.append(r1)
            r6 = r31
            r4.append(r6)
            java.lang.String r0 = r4.toString()
            goto L_0x03db
        L_0x03d9:
            r6 = r31
        L_0x03db:
            r4 = r0
            if (r30 == 0) goto L_0x041d
            r7 = r30
            java.lang.String r0 = r7.path
            if (r0 == 0) goto L_0x0418
            r8 = 0
            r9 = 1
            r11 = 1
            if (r29 == 0) goto L_0x03ec
            r21 = 2
            goto L_0x03ee
        L_0x03ec:
            r21 = 1
        L_0x03ee:
            r0 = r36
            r1 = r37
            r14 = r2
            r2 = r4
            r3 = r18
            r4 = r14
            r15 = r7
            r7 = r8
            r8 = r9
            r9 = r11
            r17 = r10
            r10 = r21
            r11 = r28
            r0.createLoadOperationForImageReceiver(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r11)
            int r7 = r37.getSize()
            r8 = 1
            r9 = 0
            r10 = 0
            r2 = r12
            r3 = r20
            r4 = r22
            r5 = r15
            r6 = r17
            r0.createLoadOperationForImageReceiver(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r11)
            goto L_0x04ba
        L_0x0418:
            r14 = r2
            r15 = r7
            r17 = r10
            goto L_0x0422
        L_0x041d:
            r14 = r2
            r17 = r10
            r15 = r30
        L_0x0422:
            if (r24 == 0) goto L_0x0484
            int r0 = r37.getCacheType()
            r19 = 1
            if (r0 != 0) goto L_0x0431
            if (r26 == 0) goto L_0x0431
            r25 = 1
            goto L_0x0433
        L_0x0431:
            r25 = r0
        L_0x0433:
            if (r25 != 0) goto L_0x0437
            r8 = 1
            goto L_0x0439
        L_0x0437:
            r8 = r25
        L_0x0439:
            if (r29 != 0) goto L_0x0451
            r7 = 0
            r9 = 1
            if (r29 == 0) goto L_0x0441
            r10 = 2
            goto L_0x0442
        L_0x0441:
            r10 = 1
        L_0x0442:
            r0 = r36
            r1 = r37
            r2 = r4
            r3 = r18
            r4 = r14
            r14 = r11
            r11 = r28
            r0.createLoadOperationForImageReceiver(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r11)
            goto L_0x0452
        L_0x0451:
            r14 = r11
        L_0x0452:
            if (r27 != 0) goto L_0x046a
            r7 = 0
            r9 = 0
            r10 = 0
            r0 = r36
            r1 = r37
            r2 = r12
            r3 = r20
            r4 = r22
            r5 = r15
            r6 = r17
            r8 = r19
            r11 = r28
            r0.createLoadOperationForImageReceiver(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r11)
        L_0x046a:
            int r7 = r37.getSize()
            r9 = 3
            r10 = 0
            r0 = r36
            r1 = r37
            r2 = r13
            r3 = r23
            r4 = r16
            r5 = r24
            r6 = r14
            r8 = r25
            r11 = r28
            r0.createLoadOperationForImageReceiver(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r11)
            goto L_0x04ba
        L_0x0484:
            int r0 = r37.getCacheType()
            if (r0 != 0) goto L_0x048e
            if (r26 == 0) goto L_0x048e
            r13 = 1
            goto L_0x048f
        L_0x048e:
            r13 = r0
        L_0x048f:
            if (r13 != 0) goto L_0x0493
            r8 = 1
            goto L_0x0494
        L_0x0493:
            r8 = r13
        L_0x0494:
            r7 = 0
            r9 = 1
            if (r29 == 0) goto L_0x049a
            r10 = 2
            goto L_0x049b
        L_0x049a:
            r10 = 1
        L_0x049b:
            r0 = r36
            r1 = r37
            r2 = r4
            r3 = r18
            r4 = r14
            r11 = r28
            r0.createLoadOperationForImageReceiver(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r11)
            int r7 = r37.getSize()
            r9 = 0
            r10 = 0
            r2 = r12
            r3 = r20
            r4 = r22
            r5 = r15
            r6 = r17
            r8 = r13
            r0.createLoadOperationForImageReceiver(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r11)
        L_0x04ba:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ImageLoader.loadImageForImageReceiver(org.telegram.messenger.ImageReceiver):void");
    }

    /* access modifiers changed from: private */
    public void httpFileLoadError(String str) {
        this.imageLoadQueue.postRunnable(new Runnable(str) {
            public final /* synthetic */ String f$1;

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
            HttpImageTask httpImageTask2 = new HttpImageTask(httpImageTask.cacheImage, httpImageTask.imageSize);
            cacheImage.httpTask = httpImageTask2;
            this.httpTasks.add(httpImageTask2);
            runHttpTasks(false);
        }
    }

    /* access modifiers changed from: private */
    public void artworkLoadError(String str) {
        this.imageLoadQueue.postRunnable(new Runnable(str) {
            public final /* synthetic */ String f$1;

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
            ArtworkLoadTask artworkLoadTask = new ArtworkLoadTask(cacheImage.artworkTask.cacheImage);
            cacheImage.artworkTask = artworkLoadTask;
            this.artworkTasks.add(artworkLoadTask);
            runArtworkTasks(false);
        }
    }

    /* access modifiers changed from: private */
    public void fileDidLoaded(String str, File file, int i) {
        this.imageLoadQueue.postRunnable(new Runnable(str, i, file) {
            public final /* synthetic */ String f$1;
            public final /* synthetic */ int f$2;
            public final /* synthetic */ File f$3;

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
                    cacheImage2.parentObject = cacheImage.parentObject;
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
                public final /* synthetic */ String f$1;

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
            public final /* synthetic */ ImageLoader.HttpFileTask f$1;
            public final /* synthetic */ int f$2;

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
                        public final /* synthetic */ ImageLoader.HttpFileTask f$1;

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
    /* JADX WARNING: Removed duplicated region for block: B:31:0x007b  */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x008c  */
    /* JADX WARNING: Removed duplicated region for block: B:41:0x0091  */
    /* JADX WARNING: Removed duplicated region for block: B:42:0x0093  */
    /* JADX WARNING: Removed duplicated region for block: B:46:0x009d A[SYNTHETIC, Splitter:B:46:0x009d] */
    /* JADX WARNING: Removed duplicated region for block: B:64:0x00d5  */
    /* JADX WARNING: Removed duplicated region for block: B:68:0x00e2 A[SYNTHETIC, Splitter:B:68:0x00e2] */
    /* JADX WARNING: Removed duplicated region for block: B:95:0x0143  */
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
            r15 = 0
            r0.inJustDecodeBounds = r15
            int r4 = (int) r13
            r0.inSampleSize = r4
            int r4 = r4 % 2
            if (r4 == 0) goto L_0x0086
            r4 = 1
        L_0x007c:
            int r5 = r4 * 2
            int r6 = r0.inSampleSize
            if (r5 >= r6) goto L_0x0084
            r4 = r5
            goto L_0x007c
        L_0x0084:
            r0.inSampleSize = r4
        L_0x0086:
            int r4 = android.os.Build.VERSION.SDK_INT
            r5 = 21
            if (r4 >= r5) goto L_0x008d
            r15 = 1
        L_0x008d:
            r0.inPurgeable = r15
            if (r11 == 0) goto L_0x0093
            r15 = r11
            goto L_0x009b
        L_0x0093:
            if (r12 == 0) goto L_0x009a
            java.lang.String r15 = org.telegram.messenger.AndroidUtilities.getPath(r12)
            goto L_0x009b
        L_0x009a:
            r15 = r2
        L_0x009b:
            if (r15 == 0) goto L_0x00cc
            androidx.exifinterface.media.ExifInterface r4 = new androidx.exifinterface.media.ExifInterface     // Catch:{ all -> 0x00cc }
            r4.<init>((java.lang.String) r15)     // Catch:{ all -> 0x00cc }
            java.lang.String r15 = "Orientation"
            int r15 = r4.getAttributeInt(r15, r1)     // Catch:{ all -> 0x00cc }
            android.graphics.Matrix r1 = new android.graphics.Matrix     // Catch:{ all -> 0x00cc }
            r1.<init>()     // Catch:{ all -> 0x00cc }
            r4 = 3
            if (r15 == r4) goto L_0x00c4
            r4 = 6
            if (r15 == r4) goto L_0x00be
            r4 = 8
            if (r15 == r4) goto L_0x00b8
            goto L_0x00cd
        L_0x00b8:
            r15 = 1132920832(0x43870000, float:270.0)
            r1.postRotate(r15)     // Catch:{ all -> 0x00ca }
            goto L_0x00cd
        L_0x00be:
            r15 = 1119092736(0x42b40000, float:90.0)
            r1.postRotate(r15)     // Catch:{ all -> 0x00ca }
            goto L_0x00cd
        L_0x00c4:
            r15 = 1127481344(0x43340000, float:180.0)
            r1.postRotate(r15)     // Catch:{ all -> 0x00ca }
            goto L_0x00cd
        L_0x00ca:
            goto L_0x00cd
        L_0x00cc:
            r1 = r2
        L_0x00cd:
            int r15 = r0.inSampleSize
            float r15 = (float) r15
            float r13 = r13 / r15
            int r15 = (r13 > r14 ? 1 : (r13 == r14 ? 0 : -1))
            if (r15 <= 0) goto L_0x00e0
            if (r1 != 0) goto L_0x00dc
            android.graphics.Matrix r1 = new android.graphics.Matrix
            r1.<init>()
        L_0x00dc:
            float r14 = r14 / r13
            r1.postScale(r14, r14)
        L_0x00e0:
            if (r11 == 0) goto L_0x0143
            android.graphics.Bitmap r2 = android.graphics.BitmapFactory.decodeFile(r11, r0)     // Catch:{ all -> 0x0108 }
            if (r2 == 0) goto L_0x0189
            boolean r12 = r0.inPurgeable     // Catch:{ all -> 0x0108 }
            if (r12 == 0) goto L_0x00ef
            org.telegram.messenger.Utilities.pinBitmap(r2)     // Catch:{ all -> 0x0108 }
        L_0x00ef:
            r5 = 0
            r6 = 0
            int r7 = r2.getWidth()     // Catch:{ all -> 0x0108 }
            int r8 = r2.getHeight()     // Catch:{ all -> 0x0108 }
            r10 = 1
            r4 = r2
            r9 = r1
            android.graphics.Bitmap r12 = org.telegram.messenger.Bitmaps.createBitmap(r4, r5, r6, r7, r8, r9, r10)     // Catch:{ all -> 0x0108 }
            if (r12 == r2) goto L_0x0189
            r2.recycle()     // Catch:{ all -> 0x0108 }
            r2 = r12
            goto L_0x0189
        L_0x0108:
            r12 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r12)
            org.telegram.messenger.ImageLoader r12 = getInstance()
            r12.clearMemory()
            if (r2 != 0) goto L_0x0125
            android.graphics.Bitmap r2 = android.graphics.BitmapFactory.decodeFile(r11, r0)     // Catch:{ all -> 0x0123 }
            if (r2 == 0) goto L_0x0125
            boolean r11 = r0.inPurgeable     // Catch:{ all -> 0x0123 }
            if (r11 == 0) goto L_0x0125
            org.telegram.messenger.Utilities.pinBitmap(r2)     // Catch:{ all -> 0x0123 }
            goto L_0x0125
        L_0x0123:
            r11 = move-exception
            goto L_0x013f
        L_0x0125:
            if (r2 == 0) goto L_0x0189
            r5 = 0
            r6 = 0
            int r7 = r2.getWidth()     // Catch:{ all -> 0x0123 }
            int r8 = r2.getHeight()     // Catch:{ all -> 0x0123 }
            r10 = 1
            r4 = r2
            r9 = r1
            android.graphics.Bitmap r11 = org.telegram.messenger.Bitmaps.createBitmap(r4, r5, r6, r7, r8, r9, r10)     // Catch:{ all -> 0x0123 }
            if (r11 == r2) goto L_0x0189
            r2.recycle()     // Catch:{ all -> 0x0123 }
            r2 = r11
            goto L_0x0189
        L_0x013f:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r11)
            goto L_0x0189
        L_0x0143:
            if (r12 == 0) goto L_0x0189
            android.graphics.Bitmap r11 = android.graphics.BitmapFactory.decodeStream(r3, r2, r0)     // Catch:{ all -> 0x0177 }
            if (r11 == 0) goto L_0x016d
            boolean r12 = r0.inPurgeable     // Catch:{ all -> 0x016a }
            if (r12 == 0) goto L_0x0152
            org.telegram.messenger.Utilities.pinBitmap(r11)     // Catch:{ all -> 0x016a }
        L_0x0152:
            r5 = 0
            r6 = 0
            int r7 = r11.getWidth()     // Catch:{ all -> 0x016a }
            int r8 = r11.getHeight()     // Catch:{ all -> 0x016a }
            r10 = 1
            r4 = r11
            r9 = r1
            android.graphics.Bitmap r12 = org.telegram.messenger.Bitmaps.createBitmap(r4, r5, r6, r7, r8, r9, r10)     // Catch:{ all -> 0x016a }
            if (r12 == r11) goto L_0x016d
            r11.recycle()     // Catch:{ all -> 0x016a }
            r2 = r12
            goto L_0x016e
        L_0x016a:
            r12 = move-exception
            r2 = r11
            goto L_0x0178
        L_0x016d:
            r2 = r11
        L_0x016e:
            r3.close()     // Catch:{ all -> 0x0172 }
            goto L_0x0189
        L_0x0172:
            r11 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r11)
            goto L_0x0189
        L_0x0177:
            r12 = move-exception
        L_0x0178:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r12)     // Catch:{ all -> 0x017f }
            r3.close()     // Catch:{ all -> 0x0172 }
            goto L_0x0189
        L_0x017f:
            r11 = move-exception
            r3.close()     // Catch:{ all -> 0x0184 }
            goto L_0x0188
        L_0x0184:
            r12 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r12)
        L_0x0188:
            throw r11
        L_0x0189:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ImageLoader.loadBitmap(java.lang.String, android.net.Uri, float, float, boolean):android.graphics.Bitmap");
    }

    public static void fillPhotoSizeWithBytes(TLRPC$PhotoSize tLRPC$PhotoSize) {
        if (tLRPC$PhotoSize != null) {
            byte[] bArr = tLRPC$PhotoSize.bytes;
            if (bArr == null || bArr.length == 0) {
                try {
                    RandomAccessFile randomAccessFile = new RandomAccessFile(FileLoader.getPathToAttach(tLRPC$PhotoSize, true), "r");
                    if (((int) randomAccessFile.length()) < 20000) {
                        byte[] bArr2 = new byte[((int) randomAccessFile.length())];
                        tLRPC$PhotoSize.bytes = bArr2;
                        randomAccessFile.readFully(bArr2, 0, bArr2.length);
                    }
                } catch (Throwable th) {
                    FileLog.e(th);
                }
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:32:0x00a6  */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x00ab  */
    /* JADX WARNING: Removed duplicated region for block: B:39:0x00c9  */
    /* JADX WARNING: Removed duplicated region for block: B:40:0x00de  */
    /* JADX WARNING: Removed duplicated region for block: B:43:0x00ee  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static org.telegram.tgnet.TLRPC$PhotoSize scaleAndSaveImageInternal(org.telegram.tgnet.TLRPC$PhotoSize r3, android.graphics.Bitmap r4, android.graphics.Bitmap.CompressFormat r5, int r6, int r7, float r8, float r9, float r10, int r11, boolean r12, boolean r13, boolean r14) throws java.lang.Exception {
        /*
            r8 = 1065353216(0x3var_, float:1.0)
            int r8 = (r10 > r8 ? 1 : (r10 == r8 ? 0 : -1))
            if (r8 > 0) goto L_0x000b
            if (r13 == 0) goto L_0x0009
            goto L_0x000b
        L_0x0009:
            r6 = r4
            goto L_0x0010
        L_0x000b:
            r8 = 1
            android.graphics.Bitmap r6 = org.telegram.messenger.Bitmaps.createScaledBitmap(r4, r6, r7, r8)
        L_0x0010:
            r7 = 0
            r8 = -2147483648(0xfffffffvar_, double:NaN)
            if (r3 == 0) goto L_0x0020
            org.telegram.tgnet.TLRPC$FileLocation r10 = r3.location
            boolean r13 = r10 instanceof org.telegram.tgnet.TLRPC$TL_fileLocationToBeDeprecated
            if (r13 != 0) goto L_0x001d
            goto L_0x0020
        L_0x001d:
            org.telegram.tgnet.TLRPC$TL_fileLocationToBeDeprecated r10 = (org.telegram.tgnet.TLRPC$TL_fileLocationToBeDeprecated) r10
            goto L_0x0086
        L_0x0020:
            org.telegram.tgnet.TLRPC$TL_fileLocationToBeDeprecated r10 = new org.telegram.tgnet.TLRPC$TL_fileLocationToBeDeprecated
            r10.<init>()
            r10.volume_id = r8
            r3 = -2147483648(0xfffffffvar_, float:-0.0)
            r10.dc_id = r3
            int r3 = org.telegram.messenger.SharedConfig.getLastLocalId()
            r10.local_id = r3
            byte[] r3 = new byte[r7]
            r10.file_reference = r3
            org.telegram.tgnet.TLRPC$TL_photoSize r3 = new org.telegram.tgnet.TLRPC$TL_photoSize
            r3.<init>()
            r3.location = r10
            int r13 = r6.getWidth()
            r3.w = r13
            int r13 = r6.getHeight()
            r3.h = r13
            int r0 = r3.w
            r1 = 100
            if (r0 > r1) goto L_0x0055
            if (r13 > r1) goto L_0x0055
            java.lang.String r13 = "s"
            r3.type = r13
            goto L_0x0086
        L_0x0055:
            int r13 = r3.w
            r0 = 320(0x140, float:4.48E-43)
            if (r13 > r0) goto L_0x0064
            int r13 = r3.h
            if (r13 > r0) goto L_0x0064
            java.lang.String r13 = "m"
            r3.type = r13
            goto L_0x0086
        L_0x0064:
            int r13 = r3.w
            r0 = 800(0x320, float:1.121E-42)
            if (r13 > r0) goto L_0x0073
            int r13 = r3.h
            if (r13 > r0) goto L_0x0073
            java.lang.String r13 = "x"
            r3.type = r13
            goto L_0x0086
        L_0x0073:
            int r13 = r3.w
            r0 = 1280(0x500, float:1.794E-42)
            if (r13 > r0) goto L_0x0082
            int r13 = r3.h
            if (r13 > r0) goto L_0x0082
            java.lang.String r13 = "y"
            r3.type = r13
            goto L_0x0086
        L_0x0082:
            java.lang.String r13 = "w"
            r3.type = r13
        L_0x0086:
            java.lang.StringBuilder r13 = new java.lang.StringBuilder
            r13.<init>()
            long r0 = r10.volume_id
            r13.append(r0)
            java.lang.String r0 = "_"
            r13.append(r0)
            int r0 = r10.local_id
            r13.append(r0)
            java.lang.String r0 = ".jpg"
            r13.append(r0)
            java.lang.String r13 = r13.toString()
            r0 = 4
            if (r14 == 0) goto L_0x00ab
            java.io.File r7 = org.telegram.messenger.FileLoader.getDirectory(r0)
            goto L_0x00ba
        L_0x00ab:
            long r1 = r10.volume_id
            int r10 = (r1 > r8 ? 1 : (r1 == r8 ? 0 : -1))
            if (r10 == 0) goto L_0x00b6
            java.io.File r7 = org.telegram.messenger.FileLoader.getDirectory(r7)
            goto L_0x00ba
        L_0x00b6:
            java.io.File r7 = org.telegram.messenger.FileLoader.getDirectory(r0)
        L_0x00ba:
            java.io.File r8 = new java.io.File
            r8.<init>(r7, r13)
            java.io.FileOutputStream r7 = new java.io.FileOutputStream
            r7.<init>(r8)
            r6.compress(r5, r11, r7)
            if (r12 == 0) goto L_0x00de
            java.io.ByteArrayOutputStream r8 = new java.io.ByteArrayOutputStream
            r8.<init>()
            r6.compress(r5, r11, r8)
            byte[] r5 = r8.toByteArray()
            r3.bytes = r5
            int r5 = r5.length
            r3.size = r5
            r8.close()
            goto L_0x00e9
        L_0x00de:
            java.nio.channels.FileChannel r5 = r7.getChannel()
            long r8 = r5.size()
            int r5 = (int) r8
            r3.size = r5
        L_0x00e9:
            r7.close()
            if (r6 == r4) goto L_0x00f1
            r6.recycle()
        L_0x00f1:
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ImageLoader.scaleAndSaveImageInternal(org.telegram.tgnet.TLRPC$PhotoSize, android.graphics.Bitmap, android.graphics.Bitmap$CompressFormat, int, int, float, float, float, int, boolean, boolean, boolean):org.telegram.tgnet.TLRPC$PhotoSize");
    }

    public static TLRPC$PhotoSize scaleAndSaveImage(Bitmap bitmap, float f, float f2, int i, boolean z) {
        return scaleAndSaveImage((TLRPC$PhotoSize) null, bitmap, Bitmap.CompressFormat.JPEG, f, f2, i, z, 0, 0, false);
    }

    public static TLRPC$PhotoSize scaleAndSaveImage(TLRPC$PhotoSize tLRPC$PhotoSize, Bitmap bitmap, float f, float f2, int i, boolean z, boolean z2) {
        return scaleAndSaveImage(tLRPC$PhotoSize, bitmap, Bitmap.CompressFormat.JPEG, f, f2, i, z, 0, 0, z2);
    }

    public static TLRPC$PhotoSize scaleAndSaveImage(Bitmap bitmap, float f, float f2, int i, boolean z, int i2, int i3) {
        return scaleAndSaveImage((TLRPC$PhotoSize) null, bitmap, Bitmap.CompressFormat.JPEG, f, f2, i, z, i2, i3, false);
    }

    public static TLRPC$PhotoSize scaleAndSaveImage(Bitmap bitmap, Bitmap.CompressFormat compressFormat, float f, float f2, int i, boolean z, int i2, int i3) {
        return scaleAndSaveImage((TLRPC$PhotoSize) null, bitmap, compressFormat, f, f2, i, z, i2, i3, false);
    }

    public static TLRPC$PhotoSize scaleAndSaveImage(TLRPC$PhotoSize tLRPC$PhotoSize, Bitmap bitmap, Bitmap.CompressFormat compressFormat, float f, float f2, int i, boolean z, int i2, int i3, boolean z2) {
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
                        float f7 = height;
                        return scaleAndSaveImageInternal(tLRPC$PhotoSize, bitmap, compressFormat, i4, i5, width, height, f3, i, z, z3, z2);
                    }
                }
            }
            f3 = max;
            z3 = false;
            i4 = (int) (width / f3);
            i5 = (int) (height / f3);
            int i82 = i5;
            int i92 = i4;
            float var_ = height;
            try {
                return scaleAndSaveImageInternal(tLRPC$PhotoSize, bitmap, compressFormat, i4, i5, width, height, f3, i, z, z3, z2);
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

    public static void saveMessageThumbs(TLRPC$Message tLRPC$Message) {
        TLRPC$PhotoSize findPhotoCachedSize;
        byte[] bArr;
        if (tLRPC$Message.media != null && (findPhotoCachedSize = findPhotoCachedSize(tLRPC$Message)) != null && (bArr = findPhotoCachedSize.bytes) != null && bArr.length != 0) {
            TLRPC$FileLocation tLRPC$FileLocation = findPhotoCachedSize.location;
            if (tLRPC$FileLocation == null || (tLRPC$FileLocation instanceof TLRPC$TL_fileLocationUnavailable)) {
                TLRPC$TL_fileLocationToBeDeprecated tLRPC$TL_fileLocationToBeDeprecated = new TLRPC$TL_fileLocationToBeDeprecated();
                findPhotoCachedSize.location = tLRPC$TL_fileLocationToBeDeprecated;
                tLRPC$TL_fileLocationToBeDeprecated.volume_id = -2147483648L;
                tLRPC$TL_fileLocationToBeDeprecated.local_id = SharedConfig.getLastLocalId();
            }
            boolean z = true;
            File pathToAttach = FileLoader.getPathToAttach(findPhotoCachedSize, true);
            int i = 0;
            if (MessageObject.shouldEncryptPhotoOrVideo(tLRPC$Message)) {
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
            TLRPC$TL_photoSize tLRPC$TL_photoSize = new TLRPC$TL_photoSize();
            tLRPC$TL_photoSize.w = findPhotoCachedSize.w;
            tLRPC$TL_photoSize.h = findPhotoCachedSize.h;
            tLRPC$TL_photoSize.location = findPhotoCachedSize.location;
            tLRPC$TL_photoSize.size = findPhotoCachedSize.size;
            tLRPC$TL_photoSize.type = findPhotoCachedSize.type;
            TLRPC$MessageMedia tLRPC$MessageMedia = tLRPC$Message.media;
            if (tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaPhoto) {
                int size = tLRPC$MessageMedia.photo.sizes.size();
                while (i < size) {
                    if (tLRPC$Message.media.photo.sizes.get(i) instanceof TLRPC$TL_photoCachedSize) {
                        tLRPC$Message.media.photo.sizes.set(i, tLRPC$TL_photoSize);
                        return;
                    }
                    i++;
                }
            } else if (tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaDocument) {
                int size2 = tLRPC$MessageMedia.document.thumbs.size();
                while (i < size2) {
                    if (tLRPC$Message.media.document.thumbs.get(i) instanceof TLRPC$TL_photoCachedSize) {
                        tLRPC$Message.media.document.thumbs.set(i, tLRPC$TL_photoSize);
                        return;
                    }
                    i++;
                }
            } else if (tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaWebPage) {
                int size3 = tLRPC$MessageMedia.webpage.photo.sizes.size();
                while (i < size3) {
                    if (tLRPC$Message.media.webpage.photo.sizes.get(i) instanceof TLRPC$TL_photoCachedSize) {
                        tLRPC$Message.media.webpage.photo.sizes.set(i, tLRPC$TL_photoSize);
                        return;
                    }
                    i++;
                }
            }
        }
    }

    private static TLRPC$PhotoSize findPhotoCachedSize(TLRPC$Message tLRPC$Message) {
        TLRPC$PhotoSize tLRPC$PhotoSize;
        TLRPC$Photo tLRPC$Photo;
        TLRPC$MessageMedia tLRPC$MessageMedia = tLRPC$Message.media;
        int i = 0;
        if (tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaPhoto) {
            int size = tLRPC$MessageMedia.photo.sizes.size();
            while (i < size) {
                tLRPC$PhotoSize = tLRPC$Message.media.photo.sizes.get(i);
                if (!(tLRPC$PhotoSize instanceof TLRPC$TL_photoCachedSize)) {
                    i++;
                }
            }
            return null;
        } else if (tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaDocument) {
            int size2 = tLRPC$MessageMedia.document.thumbs.size();
            while (i < size2) {
                tLRPC$PhotoSize = tLRPC$Message.media.document.thumbs.get(i);
                if (!(tLRPC$PhotoSize instanceof TLRPC$TL_photoCachedSize)) {
                    i++;
                }
            }
            return null;
        } else if (!(tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaWebPage) || (tLRPC$Photo = tLRPC$MessageMedia.webpage.photo) == null) {
            return null;
        } else {
            int size3 = tLRPC$Photo.sizes.size();
            while (i < size3) {
                tLRPC$PhotoSize = tLRPC$Message.media.webpage.photo.sizes.get(i);
                if (!(tLRPC$PhotoSize instanceof TLRPC$TL_photoCachedSize)) {
                    i++;
                }
            }
            return null;
        }
        return tLRPC$PhotoSize;
    }

    public static void saveMessagesThumbs(ArrayList<TLRPC$Message> arrayList) {
        if (arrayList != null && !arrayList.isEmpty()) {
            for (int i = 0; i < arrayList.size(); i++) {
                saveMessageThumbs(arrayList.get(i));
            }
        }
    }

    public static MessageThumb generateMessageThumb(TLRPC$Message tLRPC$Message) {
        int i;
        int i2;
        Bitmap strippedPhotoBitmap;
        byte[] bArr;
        TLRPC$Message tLRPC$Message2 = tLRPC$Message;
        TLRPC$PhotoSize findPhotoCachedSize = findPhotoCachedSize(tLRPC$Message);
        if (findPhotoCachedSize == null || (bArr = findPhotoCachedSize.bytes) == null || bArr.length == 0) {
            TLRPC$MessageMedia tLRPC$MessageMedia = tLRPC$Message2.media;
            if (tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaDocument) {
                int size = tLRPC$MessageMedia.document.thumbs.size();
                for (int i3 = 0; i3 < size; i3++) {
                    TLRPC$PhotoSize tLRPC$PhotoSize = tLRPC$Message2.media.document.thumbs.get(i3);
                    if (tLRPC$PhotoSize instanceof TLRPC$TL_photoStrippedSize) {
                        TLRPC$PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(tLRPC$Message2.media.document.thumbs, 320);
                        if (closestPhotoSizeWithSize == null) {
                            int i4 = 0;
                            while (true) {
                                if (i4 >= tLRPC$Message2.media.document.attributes.size()) {
                                    i2 = 0;
                                    i = 0;
                                    break;
                                } else if (tLRPC$Message2.media.document.attributes.get(i4) instanceof TLRPC$TL_documentAttributeVideo) {
                                    TLRPC$TL_documentAttributeVideo tLRPC$TL_documentAttributeVideo = (TLRPC$TL_documentAttributeVideo) tLRPC$Message2.media.document.attributes.get(i4);
                                    i = tLRPC$TL_documentAttributeVideo.h;
                                    i2 = tLRPC$TL_documentAttributeVideo.w;
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
                        String format = String.format(Locale.US, "%s_false@%d_%d_b", new Object[]{ImageLocation.getStippedKey(tLRPC$Message2, tLRPC$Message2, tLRPC$PhotoSize), Integer.valueOf((int) (messageSize.x / AndroidUtilities.density)), Integer.valueOf((int) (messageSize.y / AndroidUtilities.density))});
                        if (!getInstance().memCache.contains(format) && (strippedPhotoBitmap = getStrippedPhotoBitmap(tLRPC$PhotoSize.bytes, (String) null)) != null) {
                            Utilities.blurBitmap(strippedPhotoBitmap, 3, 1, strippedPhotoBitmap.getWidth(), strippedPhotoBitmap.getHeight(), strippedPhotoBitmap.getRowBytes());
                            float f = messageSize.x;
                            float f2 = AndroidUtilities.density;
                            Bitmap createScaledBitmap = Bitmaps.createScaledBitmap(strippedPhotoBitmap, (int) (f / f2), (int) (messageSize.y / f2), true);
                            if (createScaledBitmap != strippedPhotoBitmap) {
                                strippedPhotoBitmap.recycle();
                                strippedPhotoBitmap = createScaledBitmap;
                            }
                            return new MessageThumb(format, new BitmapDrawable(strippedPhotoBitmap));
                        }
                    }
                }
            }
        } else {
            File pathToAttach = FileLoader.getPathToAttach(findPhotoCachedSize, true);
            TLRPC$TL_photoSize tLRPC$TL_photoSize = new TLRPC$TL_photoSize();
            tLRPC$TL_photoSize.w = findPhotoCachedSize.w;
            tLRPC$TL_photoSize.h = findPhotoCachedSize.h;
            tLRPC$TL_photoSize.location = findPhotoCachedSize.location;
            tLRPC$TL_photoSize.size = findPhotoCachedSize.size;
            tLRPC$TL_photoSize.type = findPhotoCachedSize.type;
            if (pathToAttach.exists() && tLRPC$Message2.grouped_id == 0) {
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
                            loadBitmap = createScaledBitmap2;
                        }
                        return new MessageThumb(format2, new BitmapDrawable(loadBitmap));
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
