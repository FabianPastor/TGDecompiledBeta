package org.telegram.messenger;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
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
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Locale;
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
            long elapsedRealtime = SystemClock.elapsedRealtime();
            if (j != j2) {
                long j3 = this.lastProgressTime;
                if (j3 != 0 && j3 >= elapsedRealtime - 100) {
                    return;
                }
            }
            this.lastProgressTime = elapsedRealtime;
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

        /* access modifiers changed from: private */
        /* renamed from: lambda$reportProgress$1 */
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

        /* access modifiers changed from: private */
        /* renamed from: lambda$null$0 */
        public /* synthetic */ void lambda$null$0$ImageLoader$HttpFileTask(long j, long j2) {
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.FileLoadProgressChanged, this.url, Long.valueOf(j), Long.valueOf(j2));
        }

        /* access modifiers changed from: protected */
        /* JADX WARNING: Code restructure failed: missing block: B:81:0x0120, code lost:
            if (r5 != -1) goto L_0x012e;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:83:?, code lost:
            r0 = r11.fileSize;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:84:0x0124, code lost:
            if (r0 == 0) goto L_0x013d;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:85:0x0126, code lost:
            reportProgress((long) r0, (long) r0);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:86:0x012c, code lost:
            r0 = e;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:87:0x012e, code lost:
            r1 = false;
         */
        /* JADX WARNING: Removed duplicated region for block: B:100:0x0142 A[Catch:{ all -> 0x0148 }] */
        /* JADX WARNING: Removed duplicated region for block: B:104:0x014e A[SYNTHETIC, Splitter:B:104:0x014e] */
        /* JADX WARNING: Removed duplicated region for block: B:24:0x0076  */
        /* JADX WARNING: Removed duplicated region for block: B:27:0x007f  */
        /* JADX WARNING: Removed duplicated region for block: B:43:0x00ad A[SYNTHETIC, Splitter:B:43:0x00ad] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public java.lang.Boolean doInBackground(java.lang.Void... r12) {
            /*
                r11 = this;
                java.lang.String r12 = "Mozilla/5.0 (iPhone; CPU iPhone OS 10_0 like Mac OS X) AppleWebKit/602.1.38 (KHTML, like Gecko) Version/10.0 Mobile/14A5297c Safari/602.1"
                java.lang.String r0 = "User-Agent"
                r1 = 1
                r2 = 0
                r3 = 0
                java.net.URL r4 = new java.net.URL     // Catch:{ all -> 0x006f }
                java.lang.String r5 = r11.url     // Catch:{ all -> 0x006f }
                r4.<init>(r5)     // Catch:{ all -> 0x006f }
                java.net.URLConnection r4 = r4.openConnection()     // Catch:{ all -> 0x006f }
                r4.addRequestProperty(r0, r12)     // Catch:{ all -> 0x006c }
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
                r4.addRequestProperty(r0, r12)     // Catch:{ all -> 0x006c }
            L_0x0054:
                r4.connect()     // Catch:{ all -> 0x006c }
                java.io.InputStream r12 = r4.getInputStream()     // Catch:{ all -> 0x006c }
                java.io.RandomAccessFile r0 = new java.io.RandomAccessFile     // Catch:{ all -> 0x0067 }
                java.io.File r5 = r11.tempFile     // Catch:{ all -> 0x0067 }
                java.lang.String r6 = "rws"
                r0.<init>(r5, r6)     // Catch:{ all -> 0x0067 }
                r11.fileOutputStream = r0     // Catch:{ all -> 0x0067 }
                goto L_0x00a9
            L_0x0067:
                r0 = move-exception
                r10 = r0
                r0 = r12
                r12 = r10
                goto L_0x0072
            L_0x006c:
                r12 = move-exception
                r0 = r2
                goto L_0x0072
            L_0x006f:
                r12 = move-exception
                r0 = r2
                r4 = r0
            L_0x0072:
                boolean r5 = r12 instanceof java.net.SocketTimeoutException
                if (r5 == 0) goto L_0x007f
                boolean r5 = org.telegram.messenger.ApplicationLoader.isNetworkOnline()
                if (r5 == 0) goto L_0x00a5
                r11.canRetry = r3
                goto L_0x00a5
            L_0x007f:
                boolean r5 = r12 instanceof java.net.UnknownHostException
                if (r5 == 0) goto L_0x0086
                r11.canRetry = r3
                goto L_0x00a5
            L_0x0086:
                boolean r5 = r12 instanceof java.net.SocketException
                if (r5 == 0) goto L_0x009f
                java.lang.String r5 = r12.getMessage()
                if (r5 == 0) goto L_0x00a5
                java.lang.String r5 = r12.getMessage()
                java.lang.String r6 = "ECONNRESET"
                boolean r5 = r5.contains(r6)
                if (r5 == 0) goto L_0x00a5
                r11.canRetry = r3
                goto L_0x00a5
            L_0x009f:
                boolean r5 = r12 instanceof java.io.FileNotFoundException
                if (r5 == 0) goto L_0x00a5
                r11.canRetry = r3
            L_0x00a5:
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r12)
                r12 = r0
            L_0x00a9:
                boolean r0 = r11.canRetry
                if (r0 == 0) goto L_0x0156
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
                r11.canRetry = r3     // Catch:{ Exception -> 0x00c7 }
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
                r11.fileSize = r0     // Catch:{ Exception -> 0x00f6 }
                goto L_0x00fa
            L_0x00f6:
                r0 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            L_0x00fa:
                if (r12 == 0) goto L_0x013e
                r0 = 32768(0x8000, float:4.5918E-41)
                byte[] r0 = new byte[r0]     // Catch:{ all -> 0x0138 }
                r4 = 0
            L_0x0102:
                boolean r5 = r11.isCancelled()     // Catch:{ all -> 0x0138 }
                if (r5 == 0) goto L_0x0109
                goto L_0x012e
            L_0x0109:
                int r5 = r12.read(r0)     // Catch:{ Exception -> 0x0130 }
                if (r5 <= 0) goto L_0x011f
                java.io.RandomAccessFile r6 = r11.fileOutputStream     // Catch:{ Exception -> 0x0130 }
                r6.write(r0, r3, r5)     // Catch:{ Exception -> 0x0130 }
                int r4 = r4 + r5
                int r5 = r11.fileSize     // Catch:{ Exception -> 0x0130 }
                if (r5 <= 0) goto L_0x0102
                long r6 = (long) r4     // Catch:{ Exception -> 0x0130 }
                long r8 = (long) r5     // Catch:{ Exception -> 0x0130 }
                r11.reportProgress(r6, r8)     // Catch:{ Exception -> 0x0130 }
                goto L_0x0102
            L_0x011f:
                r0 = -1
                if (r5 != r0) goto L_0x012e
                int r0 = r11.fileSize     // Catch:{ Exception -> 0x012c }
                if (r0 == 0) goto L_0x013d
                long r3 = (long) r0     // Catch:{ Exception -> 0x012c }
                long r5 = (long) r0     // Catch:{ Exception -> 0x012c }
                r11.reportProgress(r3, r5)     // Catch:{ Exception -> 0x012c }
                goto L_0x013d
            L_0x012c:
                r0 = move-exception
                goto L_0x0132
            L_0x012e:
                r1 = 0
                goto L_0x013d
            L_0x0130:
                r0 = move-exception
                r1 = 0
            L_0x0132:
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ all -> 0x0136 }
                goto L_0x013d
            L_0x0136:
                r0 = move-exception
                goto L_0x013a
            L_0x0138:
                r0 = move-exception
                r1 = 0
            L_0x013a:
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            L_0x013d:
                r3 = r1
            L_0x013e:
                java.io.RandomAccessFile r0 = r11.fileOutputStream     // Catch:{ all -> 0x0148 }
                if (r0 == 0) goto L_0x014c
                r0.close()     // Catch:{ all -> 0x0148 }
                r11.fileOutputStream = r2     // Catch:{ all -> 0x0148 }
                goto L_0x014c
            L_0x0148:
                r0 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            L_0x014c:
                if (r12 == 0) goto L_0x0156
                r12.close()     // Catch:{ all -> 0x0152 }
                goto L_0x0156
            L_0x0152:
                r12 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r12)
            L_0x0156:
                java.lang.Boolean r12 = java.lang.Boolean.valueOf(r3)
                return r12
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
        /* JADX WARNING: Removed duplicated region for block: B:105:0x012a A[Catch:{ all -> 0x012e }] */
        /* JADX WARNING: Removed duplicated region for block: B:108:0x0131 A[SYNTHETIC, Splitter:B:108:0x0131] */
        /* JADX WARNING: Removed duplicated region for block: B:113:0x013b A[SYNTHETIC, Splitter:B:113:0x013b] */
        /* JADX WARNING: Removed duplicated region for block: B:129:0x0155 A[SYNTHETIC, Splitter:B:129:0x0155] */
        /* JADX WARNING: Removed duplicated region for block: B:85:0x00f4 A[Catch:{ all -> 0x013f, all -> 0x0148, all -> 0x014f }] */
        /* JADX WARNING: Removed duplicated region for block: B:88:0x00fd A[Catch:{ all -> 0x013f, all -> 0x0148, all -> 0x014f }] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public java.lang.String doInBackground(java.lang.Void... r8) {
            /*
                r7 = this;
                r8 = 0
                r0 = 0
                org.telegram.messenger.ImageLoader$CacheImage r1 = r7.cacheImage     // Catch:{ all -> 0x00ed }
                org.telegram.messenger.ImageLocation r1 = r1.imageLocation     // Catch:{ all -> 0x00ed }
                java.lang.String r1 = r1.path     // Catch:{ all -> 0x00ed }
                java.net.URL r2 = new java.net.URL     // Catch:{ all -> 0x00ed }
                java.lang.String r3 = "athumb://"
                java.lang.String r4 = "https://"
                java.lang.String r1 = r1.replace(r3, r4)     // Catch:{ all -> 0x00ed }
                r2.<init>(r1)     // Catch:{ all -> 0x00ed }
                java.net.URLConnection r1 = r2.openConnection()     // Catch:{ all -> 0x00ed }
                java.net.HttpURLConnection r1 = (java.net.HttpURLConnection) r1     // Catch:{ all -> 0x00ed }
                r7.httpConnection = r1     // Catch:{ all -> 0x00ed }
                r2 = 5000(0x1388, float:7.006E-42)
                r1.setConnectTimeout(r2)     // Catch:{ all -> 0x00ed }
                java.net.HttpURLConnection r1 = r7.httpConnection     // Catch:{ all -> 0x00ed }
                r1.setReadTimeout(r2)     // Catch:{ all -> 0x00ed }
                java.net.HttpURLConnection r1 = r7.httpConnection     // Catch:{ all -> 0x00ed }
                r1.connect()     // Catch:{ all -> 0x00ed }
                java.net.HttpURLConnection r1 = r7.httpConnection     // Catch:{ Exception -> 0x0043 }
                if (r1 == 0) goto L_0x0047
                int r1 = r1.getResponseCode()     // Catch:{ Exception -> 0x0043 }
                r2 = 200(0xc8, float:2.8E-43)
                if (r1 == r2) goto L_0x0047
                r2 = 202(0xca, float:2.83E-43)
                if (r1 == r2) goto L_0x0047
                r2 = 304(0x130, float:4.26E-43)
                if (r1 == r2) goto L_0x0047
                r7.canRetry = r0     // Catch:{ Exception -> 0x0043 }
                goto L_0x0047
            L_0x0043:
                r1 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)     // Catch:{ all -> 0x00ed }
            L_0x0047:
                java.net.HttpURLConnection r1 = r7.httpConnection     // Catch:{ all -> 0x00ed }
                java.io.InputStream r1 = r1.getInputStream()     // Catch:{ all -> 0x00ed }
                java.io.ByteArrayOutputStream r2 = new java.io.ByteArrayOutputStream     // Catch:{ all -> 0x00e7 }
                r2.<init>()     // Catch:{ all -> 0x00e7 }
                r3 = 32768(0x8000, float:4.5918E-41)
                byte[] r3 = new byte[r3]     // Catch:{ all -> 0x00e1 }
            L_0x0057:
                boolean r4 = r7.isCancelled()     // Catch:{ all -> 0x00e1 }
                if (r4 == 0) goto L_0x005e
                goto L_0x0069
            L_0x005e:
                int r4 = r1.read(r3)     // Catch:{ all -> 0x00e1 }
                if (r4 <= 0) goto L_0x0068
                r2.write(r3, r0, r4)     // Catch:{ all -> 0x00e1 }
                goto L_0x0057
            L_0x0068:
                r3 = -1
            L_0x0069:
                r7.canRetry = r0     // Catch:{ all -> 0x00e1 }
                org.json.JSONObject r3 = new org.json.JSONObject     // Catch:{ all -> 0x00e1 }
                java.lang.String r4 = new java.lang.String     // Catch:{ all -> 0x00e1 }
                byte[] r5 = r2.toByteArray()     // Catch:{ all -> 0x00e1 }
                r4.<init>(r5)     // Catch:{ all -> 0x00e1 }
                r3.<init>(r4)     // Catch:{ all -> 0x00e1 }
                java.lang.String r4 = "results"
                org.json.JSONArray r3 = r3.getJSONArray(r4)     // Catch:{ all -> 0x00e1 }
                int r4 = r3.length()     // Catch:{ all -> 0x00e1 }
                if (r4 <= 0) goto L_0x00c9
                org.json.JSONObject r3 = r3.getJSONObject(r0)     // Catch:{ all -> 0x00e1 }
                java.lang.String r4 = "artworkUrl100"
                java.lang.String r3 = r3.getString(r4)     // Catch:{ all -> 0x00e1 }
                boolean r4 = r7.small     // Catch:{ all -> 0x00e1 }
                if (r4 == 0) goto L_0x00aa
                java.net.HttpURLConnection r8 = r7.httpConnection     // Catch:{ all -> 0x009b }
                if (r8 == 0) goto L_0x009c
                r8.disconnect()     // Catch:{ all -> 0x009b }
                goto L_0x009c
            L_0x009b:
            L_0x009c:
                if (r1 == 0) goto L_0x00a6
                r1.close()     // Catch:{ all -> 0x00a2 }
                goto L_0x00a6
            L_0x00a2:
                r8 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r8)
            L_0x00a6:
                r2.close()     // Catch:{ Exception -> 0x00a9 }
            L_0x00a9:
                return r3
            L_0x00aa:
                java.lang.String r4 = "100x100"
                java.lang.String r5 = "600x600"
                java.lang.String r8 = r3.replace(r4, r5)     // Catch:{ all -> 0x00e1 }
                java.net.HttpURLConnection r0 = r7.httpConnection     // Catch:{ all -> 0x00ba }
                if (r0 == 0) goto L_0x00bb
                r0.disconnect()     // Catch:{ all -> 0x00ba }
                goto L_0x00bb
            L_0x00ba:
            L_0x00bb:
                if (r1 == 0) goto L_0x00c5
                r1.close()     // Catch:{ all -> 0x00c1 }
                goto L_0x00c5
            L_0x00c1:
                r0 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            L_0x00c5:
                r2.close()     // Catch:{ Exception -> 0x00c8 }
            L_0x00c8:
                return r8
            L_0x00c9:
                java.net.HttpURLConnection r0 = r7.httpConnection     // Catch:{ all -> 0x00d1 }
                if (r0 == 0) goto L_0x00d2
                r0.disconnect()     // Catch:{ all -> 0x00d1 }
                goto L_0x00d2
            L_0x00d1:
            L_0x00d2:
                if (r1 == 0) goto L_0x00dc
                r1.close()     // Catch:{ all -> 0x00d8 }
                goto L_0x00dc
            L_0x00d8:
                r0 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            L_0x00dc:
                r2.close()     // Catch:{ Exception -> 0x013e }
                goto L_0x013e
            L_0x00e1:
                r3 = move-exception
                r6 = r2
                r2 = r1
                r1 = r3
                r3 = r6
                goto L_0x00f0
            L_0x00e7:
                r2 = move-exception
                r3 = r8
                r6 = r2
                r2 = r1
                r1 = r6
                goto L_0x00f0
            L_0x00ed:
                r1 = move-exception
                r2 = r8
                r3 = r2
            L_0x00f0:
                boolean r4 = r1 instanceof java.net.SocketTimeoutException     // Catch:{ all -> 0x013f }
                if (r4 == 0) goto L_0x00fd
                boolean r4 = org.telegram.messenger.ApplicationLoader.isNetworkOnline()     // Catch:{ all -> 0x013f }
                if (r4 == 0) goto L_0x0123
                r7.canRetry = r0     // Catch:{ all -> 0x013f }
                goto L_0x0123
            L_0x00fd:
                boolean r4 = r1 instanceof java.net.UnknownHostException     // Catch:{ all -> 0x013f }
                if (r4 == 0) goto L_0x0104
                r7.canRetry = r0     // Catch:{ all -> 0x013f }
                goto L_0x0123
            L_0x0104:
                boolean r4 = r1 instanceof java.net.SocketException     // Catch:{ all -> 0x013f }
                if (r4 == 0) goto L_0x011d
                java.lang.String r4 = r1.getMessage()     // Catch:{ all -> 0x013f }
                if (r4 == 0) goto L_0x0123
                java.lang.String r4 = r1.getMessage()     // Catch:{ all -> 0x013f }
                java.lang.String r5 = "ECONNRESET"
                boolean r4 = r4.contains(r5)     // Catch:{ all -> 0x013f }
                if (r4 == 0) goto L_0x0123
                r7.canRetry = r0     // Catch:{ all -> 0x013f }
                goto L_0x0123
            L_0x011d:
                boolean r4 = r1 instanceof java.io.FileNotFoundException     // Catch:{ all -> 0x013f }
                if (r4 == 0) goto L_0x0123
                r7.canRetry = r0     // Catch:{ all -> 0x013f }
            L_0x0123:
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)     // Catch:{ all -> 0x013f }
                java.net.HttpURLConnection r0 = r7.httpConnection     // Catch:{ all -> 0x012e }
                if (r0 == 0) goto L_0x012f
                r0.disconnect()     // Catch:{ all -> 0x012e }
                goto L_0x012f
            L_0x012e:
            L_0x012f:
                if (r2 == 0) goto L_0x0139
                r2.close()     // Catch:{ all -> 0x0135 }
                goto L_0x0139
            L_0x0135:
                r0 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            L_0x0139:
                if (r3 == 0) goto L_0x013e
                r3.close()     // Catch:{ Exception -> 0x013e }
            L_0x013e:
                return r8
            L_0x013f:
                r8 = move-exception
                java.net.HttpURLConnection r0 = r7.httpConnection     // Catch:{ all -> 0x0148 }
                if (r0 == 0) goto L_0x0149
                r0.disconnect()     // Catch:{ all -> 0x0148 }
                goto L_0x0149
            L_0x0148:
            L_0x0149:
                if (r2 == 0) goto L_0x0153
                r2.close()     // Catch:{ all -> 0x014f }
                goto L_0x0153
            L_0x014f:
                r0 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            L_0x0153:
                if (r3 == 0) goto L_0x0158
                r3.close()     // Catch:{ Exception -> 0x0158 }
            L_0x0158:
                goto L_0x015a
            L_0x0159:
                throw r8
            L_0x015a:
                goto L_0x0159
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ImageLoader.ArtworkLoadTask.doInBackground(java.lang.Void[]):java.lang.String");
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(String str) {
            if (str != null) {
                ImageLoader.this.imageLoadQueue.postRunnable(new Runnable(str) {
                    public final /* synthetic */ String f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run() {
                        ImageLoader.ArtworkLoadTask.this.lambda$onPostExecute$0$ImageLoader$ArtworkLoadTask(this.f$1);
                    }
                });
            } else if (this.canRetry) {
                ImageLoader.this.artworkLoadError(this.cacheImage.url);
            }
            ImageLoader.this.imageLoadQueue.postRunnable(new Runnable() {
                public final void run() {
                    ImageLoader.ArtworkLoadTask.this.lambda$onPostExecute$1$ImageLoader$ArtworkLoadTask();
                }
            });
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$onPostExecute$0 */
        public /* synthetic */ void lambda$onPostExecute$0$ImageLoader$ArtworkLoadTask(String str) {
            CacheImage cacheImage2 = this.cacheImage;
            cacheImage2.httpTask = new HttpImageTask(cacheImage2, 0, str);
            ImageLoader.this.httpTasks.add(this.cacheImage.httpTask);
            ImageLoader.this.runHttpTasks(false);
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$onPostExecute$1 */
        public /* synthetic */ void lambda$onPostExecute$1$ImageLoader$ArtworkLoadTask() {
            ImageLoader.this.runArtworkTasks(true);
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$onCancelled$2 */
        public /* synthetic */ void lambda$onCancelled$2$ImageLoader$ArtworkLoadTask() {
            ImageLoader.this.runArtworkTasks(true);
        }

        /* access modifiers changed from: protected */
        public void onCancelled() {
            ImageLoader.this.imageLoadQueue.postRunnable(new Runnable() {
                public final void run() {
                    ImageLoader.ArtworkLoadTask.this.lambda$onCancelled$2$ImageLoader$ArtworkLoadTask();
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
            long elapsedRealtime = SystemClock.elapsedRealtime();
            if (j != j2) {
                long j3 = this.lastProgressTime;
                if (j3 != 0 && j3 >= elapsedRealtime - 100) {
                    return;
                }
            }
            this.lastProgressTime = elapsedRealtime;
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

        /* access modifiers changed from: private */
        /* renamed from: lambda$reportProgress$1 */
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

        /* access modifiers changed from: private */
        /* renamed from: lambda$null$0 */
        public /* synthetic */ void lambda$null$0$ImageLoader$HttpImageTask(long j, long j2) {
            NotificationCenter.getInstance(this.cacheImage.currentAccount).postNotificationName(NotificationCenter.FileLoadProgressChanged, this.cacheImage.url, Long.valueOf(j), Long.valueOf(j2));
        }

        /* access modifiers changed from: protected */
        /* JADX WARNING: Code restructure failed: missing block: B:89:0x015b, code lost:
            if (r5 != -1) goto L_0x016f;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:91:?, code lost:
            r2 = r11.imageSize;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:92:0x015f, code lost:
            if (r2 == 0) goto L_0x0176;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:93:0x0161, code lost:
            reportProgress((long) r2, (long) r2);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:94:0x0167, code lost:
            r2 = move-exception;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:95:0x0168, code lost:
            r0 = r2;
            r2 = true;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:96:0x016b, code lost:
            r2 = move-exception;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:97:0x016c, code lost:
            r0 = r2;
            r2 = true;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:98:0x016f, code lost:
            r0 = false;
         */
        /* JADX WARNING: Removed duplicated region for block: B:109:0x0180 A[Catch:{ all -> 0x0186 }] */
        /* JADX WARNING: Removed duplicated region for block: B:115:0x018e A[Catch:{ all -> 0x0192 }] */
        /* JADX WARNING: Removed duplicated region for block: B:118:0x0195 A[SYNTHETIC, Splitter:B:118:0x0195] */
        /* JADX WARNING: Removed duplicated region for block: B:48:0x00e6 A[SYNTHETIC, Splitter:B:48:0x00e6] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public java.lang.Boolean doInBackground(java.lang.Void... r12) {
            /*
                r11 = this;
                boolean r12 = r11.isCancelled()
                r0 = 1
                r1 = 0
                r2 = 0
                if (r12 != 0) goto L_0x00df
                org.telegram.messenger.ImageLoader$CacheImage r12 = r11.cacheImage     // Catch:{ all -> 0x00a5 }
                org.telegram.messenger.ImageLocation r12 = r12.imageLocation     // Catch:{ all -> 0x00a5 }
                java.lang.String r12 = r12.path     // Catch:{ all -> 0x00a5 }
                java.lang.String r3 = "https://static-maps"
                boolean r3 = r12.startsWith(r3)     // Catch:{ all -> 0x00a5 }
                if (r3 != 0) goto L_0x001f
                java.lang.String r3 = "https://maps.googleapis"
                boolean r3 = r12.startsWith(r3)     // Catch:{ all -> 0x00a5 }
                if (r3 == 0) goto L_0x0057
            L_0x001f:
                org.telegram.messenger.ImageLoader$CacheImage r3 = r11.cacheImage     // Catch:{ all -> 0x00a5 }
                int r3 = r3.currentAccount     // Catch:{ all -> 0x00a5 }
                org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)     // Catch:{ all -> 0x00a5 }
                int r3 = r3.mapProvider     // Catch:{ all -> 0x00a5 }
                r4 = 3
                if (r3 == r4) goto L_0x002f
                r4 = 4
                if (r3 != r4) goto L_0x0057
            L_0x002f:
                org.telegram.messenger.ImageLoader r3 = org.telegram.messenger.ImageLoader.this     // Catch:{ all -> 0x00a5 }
                j$.util.concurrent.ConcurrentHashMap r3 = r3.testWebFile     // Catch:{ all -> 0x00a5 }
                java.lang.Object r3 = r3.get(r12)     // Catch:{ all -> 0x00a5 }
                org.telegram.messenger.WebFile r3 = (org.telegram.messenger.WebFile) r3     // Catch:{ all -> 0x00a5 }
                if (r3 == 0) goto L_0x0057
                org.telegram.tgnet.TLRPC$TL_upload_getWebFile r4 = new org.telegram.tgnet.TLRPC$TL_upload_getWebFile     // Catch:{ all -> 0x00a5 }
                r4.<init>()     // Catch:{ all -> 0x00a5 }
                org.telegram.tgnet.TLRPC$InputWebFileLocation r3 = r3.location     // Catch:{ all -> 0x00a5 }
                r4.location = r3     // Catch:{ all -> 0x00a5 }
                r4.offset = r2     // Catch:{ all -> 0x00a5 }
                r4.limit = r2     // Catch:{ all -> 0x00a5 }
                org.telegram.messenger.ImageLoader$CacheImage r3 = r11.cacheImage     // Catch:{ all -> 0x00a5 }
                int r3 = r3.currentAccount     // Catch:{ all -> 0x00a5 }
                org.telegram.tgnet.ConnectionsManager r3 = org.telegram.tgnet.ConnectionsManager.getInstance(r3)     // Catch:{ all -> 0x00a5 }
                org.telegram.messenger.-$$Lambda$ImageLoader$HttpImageTask$XSVT2vTgbXXcvgcr720eEC0zjMc r5 = org.telegram.messenger.$$Lambda$ImageLoader$HttpImageTask$XSVT2vTgbXXcvgcr720eEC0zjMc.INSTANCE     // Catch:{ all -> 0x00a5 }
                r3.sendRequest(r4, r5)     // Catch:{ all -> 0x00a5 }
            L_0x0057:
                java.net.URL r3 = new java.net.URL     // Catch:{ all -> 0x00a5 }
                java.lang.String r4 = r11.overrideUrl     // Catch:{ all -> 0x00a5 }
                if (r4 == 0) goto L_0x005e
                r12 = r4
            L_0x005e:
                r3.<init>(r12)     // Catch:{ all -> 0x00a5 }
                java.net.URLConnection r12 = r3.openConnection()     // Catch:{ all -> 0x00a5 }
                java.net.HttpURLConnection r12 = (java.net.HttpURLConnection) r12     // Catch:{ all -> 0x00a5 }
                r11.httpConnection = r12     // Catch:{ all -> 0x00a5 }
                java.lang.String r3 = "User-Agent"
                java.lang.String r4 = "Mozilla/5.0 (iPhone; CPU iPhone OS 10_0 like Mac OS X) AppleWebKit/602.1.38 (KHTML, like Gecko) Version/10.0 Mobile/14A5297c Safari/602.1"
                r12.addRequestProperty(r3, r4)     // Catch:{ all -> 0x00a5 }
                java.net.HttpURLConnection r12 = r11.httpConnection     // Catch:{ all -> 0x00a5 }
                r3 = 5000(0x1388, float:7.006E-42)
                r12.setConnectTimeout(r3)     // Catch:{ all -> 0x00a5 }
                java.net.HttpURLConnection r12 = r11.httpConnection     // Catch:{ all -> 0x00a5 }
                r12.setReadTimeout(r3)     // Catch:{ all -> 0x00a5 }
                java.net.HttpURLConnection r12 = r11.httpConnection     // Catch:{ all -> 0x00a5 }
                r12.setInstanceFollowRedirects(r0)     // Catch:{ all -> 0x00a5 }
                boolean r12 = r11.isCancelled()     // Catch:{ all -> 0x00a5 }
                if (r12 != 0) goto L_0x00df
                java.net.HttpURLConnection r12 = r11.httpConnection     // Catch:{ all -> 0x00a5 }
                r12.connect()     // Catch:{ all -> 0x00a5 }
                java.net.HttpURLConnection r12 = r11.httpConnection     // Catch:{ all -> 0x00a5 }
                java.io.InputStream r12 = r12.getInputStream()     // Catch:{ all -> 0x00a5 }
                java.io.RandomAccessFile r3 = new java.io.RandomAccessFile     // Catch:{ all -> 0x00a0 }
                org.telegram.messenger.ImageLoader$CacheImage r4 = r11.cacheImage     // Catch:{ all -> 0x00a0 }
                java.io.File r4 = r4.tempFilePath     // Catch:{ all -> 0x00a0 }
                java.lang.String r5 = "rws"
                r3.<init>(r4, r5)     // Catch:{ all -> 0x00a0 }
                r11.fileOutputStream = r3     // Catch:{ all -> 0x00a0 }
                goto L_0x00e0
            L_0x00a0:
                r3 = move-exception
                r10 = r3
                r3 = r12
                r12 = r10
                goto L_0x00a7
            L_0x00a5:
                r12 = move-exception
                r3 = r1
            L_0x00a7:
                boolean r4 = r12 instanceof java.net.SocketTimeoutException
                if (r4 == 0) goto L_0x00b4
                boolean r4 = org.telegram.messenger.ApplicationLoader.isNetworkOnline()
                if (r4 == 0) goto L_0x00da
                r11.canRetry = r2
                goto L_0x00da
            L_0x00b4:
                boolean r4 = r12 instanceof java.net.UnknownHostException
                if (r4 == 0) goto L_0x00bb
                r11.canRetry = r2
                goto L_0x00da
            L_0x00bb:
                boolean r4 = r12 instanceof java.net.SocketException
                if (r4 == 0) goto L_0x00d4
                java.lang.String r4 = r12.getMessage()
                if (r4 == 0) goto L_0x00da
                java.lang.String r4 = r12.getMessage()
                java.lang.String r5 = "ECONNRESET"
                boolean r4 = r4.contains(r5)
                if (r4 == 0) goto L_0x00da
                r11.canRetry = r2
                goto L_0x00da
            L_0x00d4:
                boolean r4 = r12 instanceof java.io.FileNotFoundException
                if (r4 == 0) goto L_0x00da
                r11.canRetry = r2
            L_0x00da:
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r12)
                r12 = r3
                goto L_0x00e0
            L_0x00df:
                r12 = r1
            L_0x00e0:
                boolean r3 = r11.isCancelled()
                if (r3 != 0) goto L_0x017c
                java.net.HttpURLConnection r3 = r11.httpConnection     // Catch:{ Exception -> 0x00fd }
                if (r3 == 0) goto L_0x0101
                int r3 = r3.getResponseCode()     // Catch:{ Exception -> 0x00fd }
                r4 = 200(0xc8, float:2.8E-43)
                if (r3 == r4) goto L_0x0101
                r4 = 202(0xca, float:2.83E-43)
                if (r3 == r4) goto L_0x0101
                r4 = 304(0x130, float:4.26E-43)
                if (r3 == r4) goto L_0x0101
                r11.canRetry = r2     // Catch:{ Exception -> 0x00fd }
                goto L_0x0101
            L_0x00fd:
                r3 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r3)
            L_0x0101:
                int r3 = r11.imageSize
                if (r3 != 0) goto L_0x0136
                java.net.HttpURLConnection r3 = r11.httpConnection
                if (r3 == 0) goto L_0x0136
                java.util.Map r3 = r3.getHeaderFields()     // Catch:{ Exception -> 0x0132 }
                if (r3 == 0) goto L_0x0136
                java.lang.String r4 = "content-Length"
                java.lang.Object r3 = r3.get(r4)     // Catch:{ Exception -> 0x0132 }
                java.util.List r3 = (java.util.List) r3     // Catch:{ Exception -> 0x0132 }
                if (r3 == 0) goto L_0x0136
                boolean r4 = r3.isEmpty()     // Catch:{ Exception -> 0x0132 }
                if (r4 != 0) goto L_0x0136
                java.lang.Object r3 = r3.get(r2)     // Catch:{ Exception -> 0x0132 }
                java.lang.String r3 = (java.lang.String) r3     // Catch:{ Exception -> 0x0132 }
                if (r3 == 0) goto L_0x0136
                java.lang.Integer r3 = org.telegram.messenger.Utilities.parseInt(r3)     // Catch:{ Exception -> 0x0132 }
                int r3 = r3.intValue()     // Catch:{ Exception -> 0x0132 }
                r11.imageSize = r3     // Catch:{ Exception -> 0x0132 }
                goto L_0x0136
            L_0x0132:
                r3 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r3)
            L_0x0136:
                if (r12 == 0) goto L_0x017c
                r3 = 8192(0x2000, float:1.14794E-41)
                byte[] r3 = new byte[r3]     // Catch:{ all -> 0x0178 }
                r4 = 0
            L_0x013d:
                boolean r5 = r11.isCancelled()     // Catch:{ all -> 0x0178 }
                if (r5 == 0) goto L_0x0144
                goto L_0x016f
            L_0x0144:
                int r5 = r12.read(r3)     // Catch:{ Exception -> 0x0171 }
                if (r5 <= 0) goto L_0x015a
                int r4 = r4 + r5
                java.io.RandomAccessFile r6 = r11.fileOutputStream     // Catch:{ Exception -> 0x0171 }
                r6.write(r3, r2, r5)     // Catch:{ Exception -> 0x0171 }
                int r5 = r11.imageSize     // Catch:{ Exception -> 0x0171 }
                if (r5 == 0) goto L_0x013d
                long r6 = (long) r4     // Catch:{ Exception -> 0x0171 }
                long r8 = (long) r5     // Catch:{ Exception -> 0x0171 }
                r11.reportProgress(r6, r8)     // Catch:{ Exception -> 0x0171 }
                goto L_0x013d
            L_0x015a:
                r3 = -1
                if (r5 != r3) goto L_0x016f
                int r2 = r11.imageSize     // Catch:{ Exception -> 0x016b, all -> 0x0167 }
                if (r2 == 0) goto L_0x0176
                long r3 = (long) r2     // Catch:{ Exception -> 0x016b, all -> 0x0167 }
                long r5 = (long) r2     // Catch:{ Exception -> 0x016b, all -> 0x0167 }
                r11.reportProgress(r3, r5)     // Catch:{ Exception -> 0x016b, all -> 0x0167 }
                goto L_0x0176
            L_0x0167:
                r2 = move-exception
                r0 = r2
                r2 = 1
                goto L_0x0179
            L_0x016b:
                r2 = move-exception
                r0 = r2
                r2 = 1
                goto L_0x0172
            L_0x016f:
                r0 = 0
                goto L_0x0176
            L_0x0171:
                r0 = move-exception
            L_0x0172:
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ all -> 0x0178 }
                r0 = r2
            L_0x0176:
                r2 = r0
                goto L_0x017c
            L_0x0178:
                r0 = move-exception
            L_0x0179:
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            L_0x017c:
                java.io.RandomAccessFile r0 = r11.fileOutputStream     // Catch:{ all -> 0x0186 }
                if (r0 == 0) goto L_0x018a
                r0.close()     // Catch:{ all -> 0x0186 }
                r11.fileOutputStream = r1     // Catch:{ all -> 0x0186 }
                goto L_0x018a
            L_0x0186:
                r0 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            L_0x018a:
                java.net.HttpURLConnection r0 = r11.httpConnection     // Catch:{ all -> 0x0192 }
                if (r0 == 0) goto L_0x0193
                r0.disconnect()     // Catch:{ all -> 0x0192 }
                goto L_0x0193
            L_0x0192:
            L_0x0193:
                if (r12 == 0) goto L_0x019d
                r12.close()     // Catch:{ all -> 0x0199 }
                goto L_0x019d
            L_0x0199:
                r12 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r12)
            L_0x019d:
                if (r2 == 0) goto L_0x01b3
                org.telegram.messenger.ImageLoader$CacheImage r12 = r11.cacheImage
                java.io.File r0 = r12.tempFilePath
                if (r0 == 0) goto L_0x01b3
                java.io.File r12 = r12.finalFilePath
                boolean r12 = r0.renameTo(r12)
                if (r12 != 0) goto L_0x01b3
                org.telegram.messenger.ImageLoader$CacheImage r12 = r11.cacheImage
                java.io.File r0 = r12.tempFilePath
                r12.finalFilePath = r0
            L_0x01b3:
                java.lang.Boolean r12 = java.lang.Boolean.valueOf(r2)
                return r12
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

        /* access modifiers changed from: private */
        /* renamed from: lambda$onPostExecute$4 */
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

        /* access modifiers changed from: private */
        /* renamed from: lambda$null$3 */
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

        /* access modifiers changed from: private */
        /* renamed from: lambda$onPostExecute$5 */
        public /* synthetic */ void lambda$onPostExecute$5$ImageLoader$HttpImageTask() {
            ImageLoader.this.runHttpTasks(true);
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$onCancelled$6 */
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

        /* access modifiers changed from: private */
        /* renamed from: lambda$onCancelled$8 */
        public /* synthetic */ void lambda$onCancelled$8$ImageLoader$HttpImageTask() {
            ImageLoader.this.fileProgresses.remove(this.cacheImage.url);
            AndroidUtilities.runOnUIThread(new Runnable() {
                public final void run() {
                    ImageLoader.HttpImageTask.this.lambda$null$7$ImageLoader$HttpImageTask();
                }
            });
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$null$7 */
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

        /* access modifiers changed from: private */
        /* renamed from: lambda$removeTask$0 */
        public /* synthetic */ void lambda$removeTask$0$ImageLoader$ThumbGenerateTask(String str) {
            ThumbGenerateTask thumbGenerateTask = (ThumbGenerateTask) ImageLoader.this.thumbGenerateTasks.remove(str);
        }

        public void run() {
            int i;
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
                        if (this.info.big) {
                            Point point = AndroidUtilities.displaySize;
                            i = Math.max(point.x, point.y);
                        } else {
                            Point point2 = AndroidUtilities.displaySize;
                            i = Math.min(180, Math.min(point2.x, point2.y) / 4);
                        }
                        int i2 = this.mediaType;
                        Bitmap bitmap = null;
                        if (i2 == 0) {
                            float f = (float) i;
                            bitmap = ImageLoader.loadBitmap(this.originalPath.toString(), (Uri) null, f, f, false);
                        } else {
                            int i3 = 2;
                            if (i2 == 2) {
                                String file2 = this.originalPath.toString();
                                if (!this.info.big) {
                                    i3 = 1;
                                }
                                bitmap = SendMessagesHelper.createVideoThumbnail(file2, i3);
                            } else if (i2 == 3) {
                                String lowerCase = this.originalPath.toString().toLowerCase();
                                if (lowerCase.endsWith("mp4")) {
                                    String file3 = this.originalPath.toString();
                                    if (!this.info.big) {
                                        i3 = 1;
                                    }
                                    bitmap = SendMessagesHelper.createVideoThumbnail(file3, i3);
                                } else if (lowerCase.endsWith(".jpg") || lowerCase.endsWith(".jpeg") || lowerCase.endsWith(".png") || lowerCase.endsWith(".gif")) {
                                    float f2 = (float) i;
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
                                float f4 = (float) i;
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

        /* access modifiers changed from: private */
        /* renamed from: lambda$run$1 */
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

        /* JADX WARNING: type inference failed for: r8v95 */
        /* JADX WARNING: type inference failed for: r8v96 */
        /* JADX WARNING: type inference failed for: r8v98 */
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
        /* JADX WARNING: Removed duplicated region for block: B:709:0x0a61 A[ADDED_TO_REGION] */
        /* JADX WARNING: Removed duplicated region for block: B:714:0x0a71  */
        public void run() {
            /*
                r38 = this;
                r1 = r38
                int r2 = android.os.Build.VERSION.SDK_INT
                java.lang.Object r3 = r1.sync
                monitor-enter(r3)
                java.lang.Thread r0 = java.lang.Thread.currentThread()     // Catch:{ all -> 0x0ad0 }
                r1.runningThread = r0     // Catch:{ all -> 0x0ad0 }
                java.lang.Thread.interrupted()     // Catch:{ all -> 0x0ad0 }
                boolean r0 = r1.isCancelled     // Catch:{ all -> 0x0ad0 }
                if (r0 == 0) goto L_0x0016
                monitor-exit(r3)     // Catch:{ all -> 0x0ad0 }
                return
            L_0x0016:
                monitor-exit(r3)     // Catch:{ all -> 0x0ad0 }
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage
                org.telegram.messenger.ImageLocation r3 = r0.imageLocation
                org.telegram.tgnet.TLRPC$PhotoSize r4 = r3.photoSize
                boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC$TL_photoStrippedSize
                if (r5 == 0) goto L_0x0039
                org.telegram.tgnet.TLRPC$TL_photoStrippedSize r4 = (org.telegram.tgnet.TLRPC$TL_photoStrippedSize) r4
                byte[] r2 = r4.bytes
                java.lang.String r0 = r0.filter
                android.graphics.Bitmap r0 = org.telegram.messenger.ImageLoader.getStrippedPhotoBitmap(r2, r0)
                if (r0 == 0) goto L_0x0033
                android.graphics.drawable.BitmapDrawable r6 = new android.graphics.drawable.BitmapDrawable
                r6.<init>(r0)
                goto L_0x0034
            L_0x0033:
                r6 = 0
            L_0x0034:
                r1.onPostExecute(r6)
                goto L_0x0acf
            L_0x0039:
                int r4 = r0.imageType
                r5 = 5
                if (r4 != r5) goto L_0x0059
                org.telegram.ui.Components.ThemePreviewDrawable r0 = new org.telegram.ui.Components.ThemePreviewDrawable     // Catch:{ all -> 0x004f }
                org.telegram.messenger.ImageLoader$CacheImage r2 = r1.cacheImage     // Catch:{ all -> 0x004f }
                java.io.File r3 = r2.finalFilePath     // Catch:{ all -> 0x004f }
                org.telegram.messenger.ImageLocation r2 = r2.imageLocation     // Catch:{ all -> 0x004f }
                org.telegram.tgnet.TLRPC$Document r2 = r2.document     // Catch:{ all -> 0x004f }
                org.telegram.messenger.DocumentObject$ThemeDocument r2 = (org.telegram.messenger.DocumentObject.ThemeDocument) r2     // Catch:{ all -> 0x004f }
                r0.<init>(r3, r2)     // Catch:{ all -> 0x004f }
                r6 = r0
                goto L_0x0054
            L_0x004f:
                r0 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
                r6 = 0
            L_0x0054:
                r1.onPostExecute(r6)
                goto L_0x0acf
            L_0x0059:
                r7 = 4
                r8 = 3
                r9 = 2
                r10 = 1
                r11 = 0
                if (r4 == r8) goto L_0x0a7a
                if (r4 != r7) goto L_0x0064
                goto L_0x0a7a
            L_0x0064:
                r12 = 8
                if (r4 != r10) goto L_0x018f
                r0 = 1126865306(0x432a999a, float:170.6)
                int r2 = org.telegram.messenger.AndroidUtilities.dp(r0)
                r3 = 512(0x200, float:7.175E-43)
                int r2 = java.lang.Math.min(r3, r2)
                int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
                int r0 = java.lang.Math.min(r3, r0)
                org.telegram.messenger.ImageLoader$CacheImage r4 = r1.cacheImage
                java.lang.String r4 = r4.filter
                if (r4 == 0) goto L_0x015d
                java.lang.String r13 = "_"
                java.lang.String[] r4 = r4.split(r13)
                int r13 = r4.length
                if (r13 < r9) goto L_0x00cb
                r0 = r4[r11]
                float r0 = java.lang.Float.parseFloat(r0)
                r2 = r4[r10]
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
                if (r13 == r9) goto L_0x00cc
                r11 = 1
                goto L_0x00cc
            L_0x00cb:
                r3 = 0
            L_0x00cc:
                int r13 = r4.length
                if (r13 < r8) goto L_0x00f5
                java.lang.String r13 = "nr"
                r14 = r4[r9]
                boolean r13 = r13.equals(r14)
                if (r13 == 0) goto L_0x00db
                r8 = 2
                goto L_0x00f6
            L_0x00db:
                java.lang.String r13 = "nrs"
                r14 = r4[r9]
                boolean r13 = r13.equals(r14)
                if (r13 == 0) goto L_0x00e6
                goto L_0x00f6
            L_0x00e6:
                java.lang.String r13 = "dice"
                r14 = r4[r9]
                boolean r13 = r13.equals(r14)
                if (r13 == 0) goto L_0x00f5
                r8 = r4[r8]
                r9 = r8
                r8 = 2
                goto L_0x00f7
            L_0x00f5:
                r8 = 1
            L_0x00f6:
                r9 = 0
            L_0x00f7:
                int r10 = r4.length
                if (r10 < r5) goto L_0x0154
                java.lang.String r5 = "c1"
                r10 = r4[r7]
                boolean r5 = r5.equals(r10)
                if (r5 == 0) goto L_0x0114
                int[] r6 = new int[r12]
                r6 = {16219713, 13275258, 16757049, 15582629, 16765248, 16245699, 16768889, 16510934} // fill-array
            L_0x0109:
                r15 = r0
                r14 = r2
                r17 = r3
                r18 = r6
                r10 = r8
                r6 = r9
                r16 = r11
                goto L_0x0166
            L_0x0114:
                java.lang.String r5 = "c2"
                r10 = r4[r7]
                boolean r5 = r5.equals(r10)
                if (r5 == 0) goto L_0x0124
                int[] r6 = new int[r12]
                r6 = {16219713, 11172960, 16757049, 13150599, 16765248, 14534815, 16768889, 15128242} // fill-array
                goto L_0x0109
            L_0x0124:
                java.lang.String r5 = "c3"
                r10 = r4[r7]
                boolean r5 = r5.equals(r10)
                if (r5 == 0) goto L_0x0134
                int[] r6 = new int[r12]
                r6 = {16219713, 9199944, 16757049, 11371874, 16765248, 12885622, 16768889, 13939080} // fill-array
                goto L_0x0109
            L_0x0134:
                java.lang.String r5 = "c4"
                r10 = r4[r7]
                boolean r5 = r5.equals(r10)
                if (r5 == 0) goto L_0x0144
                int[] r6 = new int[r12]
                r6 = {16219713, 7224364, 16757049, 9591348, 16765248, 10579526, 16768889, 11303506} // fill-array
                goto L_0x0109
            L_0x0144:
                java.lang.String r5 = "c5"
                r4 = r4[r7]
                boolean r4 = r5.equals(r4)
                if (r4 == 0) goto L_0x0154
                int[] r6 = new int[r12]
                r6 = {16219713, 2694162, 16757049, 4663842, 16765248, 5716784, 16768889, 6834492} // fill-array
                goto L_0x0109
            L_0x0154:
                r15 = r0
                r14 = r2
                r17 = r3
                r10 = r8
                r6 = r9
                r16 = r11
                goto L_0x0164
            L_0x015d:
                r15 = r0
                r14 = r2
                r6 = 0
                r16 = 0
                r17 = 0
            L_0x0164:
                r18 = 0
            L_0x0166:
                if (r6 == 0) goto L_0x017d
                java.lang.String r0 = "🎰"
                boolean r0 = r0.equals(r6)
                if (r0 == 0) goto L_0x0177
                org.telegram.ui.Components.SlotsDrawable r0 = new org.telegram.ui.Components.SlotsDrawable
                r0.<init>(r6, r14, r15)
                goto L_0x0187
            L_0x0177:
                org.telegram.ui.Components.RLottieDrawable r0 = new org.telegram.ui.Components.RLottieDrawable
                r0.<init>(r6, r14, r15)
                goto L_0x0187
            L_0x017d:
                org.telegram.ui.Components.RLottieDrawable r0 = new org.telegram.ui.Components.RLottieDrawable
                org.telegram.messenger.ImageLoader$CacheImage r2 = r1.cacheImage
                java.io.File r13 = r2.finalFilePath
                r12 = r0
                r12.<init>((java.io.File) r13, (int) r14, (int) r15, (boolean) r16, (boolean) r17, (int[]) r18)
            L_0x0187:
                r0.setAutoRepeat(r10)
                r1.onPostExecute(r0)
                goto L_0x0acf
            L_0x018f:
                if (r4 != r9) goto L_0x023b
                if (r3 == 0) goto L_0x0198
                long r13 = r3.videoSeekTo
                r23 = r13
                goto L_0x019a
            L_0x0198:
                r23 = 0
            L_0x019a:
                java.lang.String r2 = "g"
                java.lang.String r0 = r0.filter
                boolean r0 = r2.equals(r0)
                if (r0 == 0) goto L_0x01e3
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage
                org.telegram.messenger.ImageLocation r2 = r0.imageLocation
                org.telegram.tgnet.TLRPC$Document r3 = r2.document
                boolean r4 = r3 instanceof org.telegram.tgnet.TLRPC$TL_documentEncrypted
                if (r4 != 0) goto L_0x01e3
                boolean r4 = r3 instanceof org.telegram.tgnet.TLRPC$Document
                if (r4 == 0) goto L_0x01b5
                r20 = r3
                goto L_0x01b7
            L_0x01b5:
                r20 = 0
            L_0x01b7:
                if (r20 == 0) goto L_0x01bc
                int r0 = r0.size
                goto L_0x01be
            L_0x01bc:
                int r0 = r2.currentSize
            L_0x01be:
                org.telegram.ui.Components.AnimatedFileDrawable r2 = new org.telegram.ui.Components.AnimatedFileDrawable
                org.telegram.messenger.ImageLoader$CacheImage r3 = r1.cacheImage
                java.io.File r4 = r3.finalFilePath
                r17 = 0
                long r7 = (long) r0
                if (r20 != 0) goto L_0x01ce
                org.telegram.messenger.ImageLocation r6 = r3.imageLocation
                r21 = r6
                goto L_0x01d0
            L_0x01ce:
                r21 = 0
            L_0x01d0:
                java.lang.Object r0 = r3.parentObject
                int r3 = r3.currentAccount
                r26 = 0
                r15 = r2
                r16 = r4
                r18 = r7
                r22 = r0
                r25 = r3
                r15.<init>(r16, r17, r18, r20, r21, r22, r23, r25, r26)
                goto L_0x0233
            L_0x01e3:
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage
                java.lang.String r0 = r0.filter
                if (r0 == 0) goto L_0x020b
                java.lang.String r2 = "_"
                java.lang.String[] r0 = r0.split(r2)
                int r2 = r0.length
                if (r2 < r9) goto L_0x020b
                r2 = r0[r11]
                float r2 = java.lang.Float.parseFloat(r2)
                r0 = r0[r10]
                float r0 = java.lang.Float.parseFloat(r0)
                float r3 = org.telegram.messenger.AndroidUtilities.density
                float r2 = r2 * r3
                int r11 = (int) r2
                float r0 = r0 * r3
                int r0 = (int) r0
                r28 = r0
                r27 = r11
                goto L_0x020f
            L_0x020b:
                r27 = 0
                r28 = 0
            L_0x020f:
                org.telegram.ui.Components.AnimatedFileDrawable r2 = new org.telegram.ui.Components.AnimatedFileDrawable
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage
                java.io.File r3 = r0.finalFilePath
                java.lang.String r4 = "d"
                java.lang.String r0 = r0.filter
                boolean r17 = r4.equals(r0)
                r18 = 0
                r20 = 0
                r21 = 0
                r22 = 0
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage
                int r0 = r0.currentAccount
                r26 = 0
                r15 = r2
                r16 = r3
                r25 = r0
                r15.<init>(r16, r17, r18, r20, r21, r22, r23, r25, r26, r27, r28)
            L_0x0233:
                java.lang.Thread.interrupted()
                r1.onPostExecute(r2)
                goto L_0x0acf
            L_0x023b:
                java.io.File r3 = r0.finalFilePath
                org.telegram.messenger.SecureDocument r4 = r0.secureDocument
                if (r4 != 0) goto L_0x0256
                java.io.File r0 = r0.encryptionKeyPath
                if (r0 == 0) goto L_0x0254
                if (r3 == 0) goto L_0x0254
                java.lang.String r0 = r3.getAbsolutePath()
                java.lang.String r4 = ".enc"
                boolean r0 = r0.endsWith(r4)
                if (r0 == 0) goto L_0x0254
                goto L_0x0256
            L_0x0254:
                r4 = 0
                goto L_0x0257
            L_0x0256:
                r4 = 1
            L_0x0257:
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage
                org.telegram.messenger.SecureDocument r0 = r0.secureDocument
                if (r0 == 0) goto L_0x026b
                org.telegram.messenger.SecureDocumentKey r5 = r0.secureDocumentKey
                org.telegram.tgnet.TLRPC$TL_secureFile r7 = r0.secureFile
                if (r7 == 0) goto L_0x0268
                byte[] r7 = r7.file_hash
                if (r7 == 0) goto L_0x0268
                goto L_0x026d
            L_0x0268:
                byte[] r7 = r0.fileHash
                goto L_0x026d
            L_0x026b:
                r5 = 0
                r7 = 0
            L_0x026d:
                r0 = 19
                if (r2 >= r0) goto L_0x02dd
                java.io.RandomAccessFile r15 = new java.io.RandomAccessFile     // Catch:{ Exception -> 0x02c2, all -> 0x02be }
                java.lang.String r0 = "r"
                r15.<init>(r3, r0)     // Catch:{ Exception -> 0x02c2, all -> 0x02be }
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage     // Catch:{ Exception -> 0x02bb }
                int r0 = r0.type     // Catch:{ Exception -> 0x02bb }
                if (r0 != r10) goto L_0x0283
                byte[] r0 = org.telegram.messenger.ImageLoader.headerThumb     // Catch:{ Exception -> 0x02bb }
                goto L_0x0287
            L_0x0283:
                byte[] r0 = org.telegram.messenger.ImageLoader.header     // Catch:{ Exception -> 0x02bb }
            L_0x0287:
                int r8 = r0.length     // Catch:{ Exception -> 0x02bb }
                r15.readFully(r0, r11, r8)     // Catch:{ Exception -> 0x02bb }
                java.lang.String r8 = new java.lang.String     // Catch:{ Exception -> 0x02bb }
                r8.<init>(r0)     // Catch:{ Exception -> 0x02bb }
                java.lang.String r0 = r8.toLowerCase()     // Catch:{ Exception -> 0x02bb }
                java.lang.String r0 = r0.toLowerCase()     // Catch:{ Exception -> 0x02bb }
                java.lang.String r8 = "riff"
                boolean r8 = r0.startsWith(r8)     // Catch:{ Exception -> 0x02bb }
                if (r8 == 0) goto L_0x02ab
                java.lang.String r8 = "webp"
                boolean r0 = r0.endsWith(r8)     // Catch:{ Exception -> 0x02bb }
                if (r0 == 0) goto L_0x02ab
                r8 = 1
                goto L_0x02ac
            L_0x02ab:
                r8 = 0
            L_0x02ac:
                r15.close()     // Catch:{ Exception -> 0x02b9 }
                r15.close()     // Catch:{ Exception -> 0x02b3 }
                goto L_0x02de
            L_0x02b3:
                r0 = move-exception
                r15 = r0
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r15)
                goto L_0x02de
            L_0x02b9:
                r0 = move-exception
                goto L_0x02c5
            L_0x02bb:
                r0 = move-exception
                r8 = 0
                goto L_0x02c5
            L_0x02be:
                r0 = move-exception
                r2 = r0
                r6 = 0
                goto L_0x02d1
            L_0x02c2:
                r0 = move-exception
                r8 = 0
                r15 = 0
            L_0x02c5:
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ all -> 0x02ce }
                if (r15 == 0) goto L_0x02de
                r15.close()     // Catch:{ Exception -> 0x02b3 }
                goto L_0x02de
            L_0x02ce:
                r0 = move-exception
                r2 = r0
                r6 = r15
            L_0x02d1:
                if (r6 == 0) goto L_0x02dc
                r6.close()     // Catch:{ Exception -> 0x02d7 }
                goto L_0x02dc
            L_0x02d7:
                r0 = move-exception
                r3 = r0
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r3)
            L_0x02dc:
                throw r2
            L_0x02dd:
                r8 = 0
            L_0x02de:
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage
                org.telegram.messenger.ImageLocation r0 = r0.imageLocation
                java.lang.String r0 = r0.path
                if (r0 == 0) goto L_0x0347
                java.lang.String r15 = "thumb://"
                boolean r15 = r0.startsWith(r15)
                if (r15 == 0) goto L_0x0312
                java.lang.String r15 = ":"
                int r15 = r0.indexOf(r15, r12)
                if (r15 < 0) goto L_0x0308
                java.lang.String r17 = r0.substring(r12, r15)
                long r17 = java.lang.Long.parseLong(r17)
                java.lang.Long r17 = java.lang.Long.valueOf(r17)
                int r15 = r15 + r10
                java.lang.String r0 = r0.substring(r15)
                goto L_0x030b
            L_0x0308:
                r0 = 0
                r17 = 0
            L_0x030b:
                r12 = r0
                r18 = r17
                r15 = 0
            L_0x030f:
                r19 = 0
                goto L_0x034d
            L_0x0312:
                java.lang.String r15 = "vthumb://"
                boolean r15 = r0.startsWith(r15)
                if (r15 == 0) goto L_0x033a
                java.lang.String r15 = ":"
                r12 = 9
                int r15 = r0.indexOf(r15, r12)
                if (r15 < 0) goto L_0x0333
                java.lang.String r0 = r0.substring(r12, r15)
                long r18 = java.lang.Long.parseLong(r0)
                java.lang.Long r0 = java.lang.Long.valueOf(r18)
                r12 = 1
                goto L_0x0335
            L_0x0333:
                r0 = 0
                r12 = 0
            L_0x0335:
                r18 = r0
                r15 = r12
                r12 = 0
                goto L_0x030f
            L_0x033a:
                java.lang.String r12 = "http"
                boolean r0 = r0.startsWith(r12)
                if (r0 != 0) goto L_0x0347
                r12 = 0
                r15 = 0
                r18 = 0
                goto L_0x030f
            L_0x0347:
                r12 = 0
                r15 = 0
                r18 = 0
                r19 = 1
            L_0x034d:
                android.graphics.BitmapFactory$Options r13 = new android.graphics.BitmapFactory$Options
                r13.<init>()
                r13.inSampleSize = r10
                r14 = 21
                if (r2 >= r14) goto L_0x035a
                r13.inPurgeable = r10
            L_0x035a:
                org.telegram.messenger.ImageLoader r0 = org.telegram.messenger.ImageLoader.this
                boolean r22 = r0.canForce8888
                r23 = 0
                r24 = 1065353216(0x3var_, float:1.0)
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage     // Catch:{ all -> 0x0586 }
                java.lang.String r0 = r0.filter     // Catch:{ all -> 0x0586 }
                if (r0 == 0) goto L_0x0515
                java.lang.String r14 = "_"
                java.lang.String[] r0 = r0.split(r14)     // Catch:{ all -> 0x0586 }
                int r14 = r0.length     // Catch:{ all -> 0x0586 }
                if (r14 < r9) goto L_0x0396
                r14 = r0[r11]     // Catch:{ all -> 0x0391 }
                float r14 = java.lang.Float.parseFloat(r14)     // Catch:{ all -> 0x0391 }
                float r26 = org.telegram.messenger.AndroidUtilities.density     // Catch:{ all -> 0x0391 }
                float r14 = r14 * r26
                r0 = r0[r10]     // Catch:{ all -> 0x038b }
                float r0 = java.lang.Float.parseFloat(r0)     // Catch:{ all -> 0x038b }
                float r26 = org.telegram.messenger.AndroidUtilities.density     // Catch:{ all -> 0x038b }
                float r0 = r0 * r26
                r26 = r14
                r14 = r0
                goto L_0x0399
            L_0x038b:
                r0 = move-exception
                r10 = r0
                r11 = r8
                r0 = 0
                goto L_0x0510
            L_0x0391:
                r0 = move-exception
                r10 = r0
                r11 = r8
                goto L_0x0589
            L_0x0396:
                r14 = 0
                r26 = 0
            L_0x0399:
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage     // Catch:{ all -> 0x050a }
                java.lang.String r0 = r0.filter     // Catch:{ all -> 0x050a }
                java.lang.String r9 = "b2"
                boolean r0 = r0.contains(r9)     // Catch:{ all -> 0x050a }
                if (r0 == 0) goto L_0x03a7
                r9 = 3
                goto L_0x03c4
            L_0x03a7:
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage     // Catch:{ all -> 0x050a }
                java.lang.String r0 = r0.filter     // Catch:{ all -> 0x050a }
                java.lang.String r9 = "b1"
                boolean r0 = r0.contains(r9)     // Catch:{ all -> 0x050a }
                if (r0 == 0) goto L_0x03b5
                r9 = 2
                goto L_0x03c4
            L_0x03b5:
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage     // Catch:{ all -> 0x050a }
                java.lang.String r0 = r0.filter     // Catch:{ all -> 0x050a }
                java.lang.String r9 = "b"
                boolean r0 = r0.contains(r9)     // Catch:{ all -> 0x050a }
                if (r0 == 0) goto L_0x03c3
                r9 = 1
                goto L_0x03c4
            L_0x03c3:
                r9 = 0
            L_0x03c4:
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage     // Catch:{ all -> 0x04fe }
                java.lang.String r0 = r0.filter     // Catch:{ all -> 0x04fe }
                java.lang.String r6 = "i"
                boolean r6 = r0.contains(r6)     // Catch:{ all -> 0x04fe }
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage     // Catch:{ all -> 0x04f3 }
                java.lang.String r0 = r0.filter     // Catch:{ all -> 0x04f3 }
                java.lang.String r11 = "f"
                boolean r0 = r0.contains(r11)     // Catch:{ all -> 0x04f3 }
                if (r0 == 0) goto L_0x03dc
                r22 = 1
            L_0x03dc:
                if (r8 != 0) goto L_0x04e7
                int r0 = (r26 > r23 ? 1 : (r26 == r23 ? 0 : -1))
                if (r0 == 0) goto L_0x04e7
                int r0 = (r14 > r23 ? 1 : (r14 == r23 ? 0 : -1))
                if (r0 == 0) goto L_0x04e7
                r13.inJustDecodeBounds = r10     // Catch:{ all -> 0x04f3 }
                if (r18 == 0) goto L_0x0418
                if (r12 != 0) goto L_0x0418
                if (r15 == 0) goto L_0x0402
                android.content.Context r0 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x03ff }
                android.content.ContentResolver r0 = r0.getContentResolver()     // Catch:{ all -> 0x03ff }
                r11 = r8
                r30 = r9
                long r8 = r18.longValue()     // Catch:{ all -> 0x0416 }
                android.provider.MediaStore.Video.Thumbnails.getThumbnail(r0, r8, r10, r13)     // Catch:{ all -> 0x0416 }
                goto L_0x0412
            L_0x03ff:
                r0 = move-exception
                goto L_0x04f6
            L_0x0402:
                r11 = r8
                r30 = r9
                android.content.Context r0 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x0416 }
                android.content.ContentResolver r0 = r0.getContentResolver()     // Catch:{ all -> 0x0416 }
                long r8 = r18.longValue()     // Catch:{ all -> 0x0416 }
                android.provider.MediaStore.Images.Thumbnails.getThumbnail(r0, r8, r10, r13)     // Catch:{ all -> 0x0416 }
            L_0x0412:
                r31 = r6
                goto L_0x0494
            L_0x0416:
                r0 = move-exception
                goto L_0x0472
            L_0x0418:
                r11 = r8
                r30 = r9
                if (r5 == 0) goto L_0x047a
                java.io.RandomAccessFile r0 = new java.io.RandomAccessFile     // Catch:{ all -> 0x046f }
                java.lang.String r8 = "r"
                r0.<init>(r3, r8)     // Catch:{ all -> 0x046f }
                long r8 = r0.length()     // Catch:{ all -> 0x046f }
                int r9 = (int) r8     // Catch:{ all -> 0x046f }
                java.lang.ThreadLocal r8 = org.telegram.messenger.ImageLoader.bytesLocal     // Catch:{ all -> 0x046f }
                java.lang.Object r8 = r8.get()     // Catch:{ all -> 0x046f }
                byte[] r8 = (byte[]) r8     // Catch:{ all -> 0x046f }
                if (r8 == 0) goto L_0x0439
                int r10 = r8.length     // Catch:{ all -> 0x0416 }
                if (r10 < r9) goto L_0x0439
                goto L_0x043a
            L_0x0439:
                r8 = 0
            L_0x043a:
                if (r8 != 0) goto L_0x0445
                byte[] r8 = new byte[r9]     // Catch:{ all -> 0x0416 }
                java.lang.ThreadLocal r10 = org.telegram.messenger.ImageLoader.bytesLocal     // Catch:{ all -> 0x0416 }
                r10.set(r8)     // Catch:{ all -> 0x0416 }
            L_0x0445:
                r10 = 0
                r0.readFully(r8, r10, r9)     // Catch:{ all -> 0x046f }
                r0.close()     // Catch:{ all -> 0x046f }
                org.telegram.messenger.secretmedia.EncryptedFileInputStream.decryptBytesWithKeyFile((byte[]) r8, (int) r10, (int) r9, (org.telegram.messenger.SecureDocumentKey) r5)     // Catch:{ all -> 0x046f }
                byte[] r0 = org.telegram.messenger.Utilities.computeSHA256(r8, r10, r9)     // Catch:{ all -> 0x046f }
                if (r7 == 0) goto L_0x0460
                boolean r0 = java.util.Arrays.equals(r0, r7)     // Catch:{ all -> 0x0416 }
                if (r0 != 0) goto L_0x045c
                goto L_0x0460
            L_0x045c:
                r31 = r6
                r0 = 0
                goto L_0x0463
            L_0x0460:
                r31 = r6
                r0 = 1
            L_0x0463:
                r10 = 0
                byte r6 = r8[r10]     // Catch:{ all -> 0x04dd }
                r6 = r6 & 255(0xff, float:3.57E-43)
                int r9 = r9 - r6
                if (r0 != 0) goto L_0x0494
                android.graphics.BitmapFactory.decodeByteArray(r8, r6, r9, r13)     // Catch:{ all -> 0x04dd }
                goto L_0x0494
            L_0x046f:
                r0 = move-exception
                r31 = r6
            L_0x0472:
                r10 = r0
                r0 = r14
                r14 = r26
                r9 = r30
                goto L_0x0507
            L_0x047a:
                r31 = r6
                if (r4 == 0) goto L_0x0488
                org.telegram.messenger.secretmedia.EncryptedFileInputStream r0 = new org.telegram.messenger.secretmedia.EncryptedFileInputStream     // Catch:{ all -> 0x04dd }
                org.telegram.messenger.ImageLoader$CacheImage r6 = r1.cacheImage     // Catch:{ all -> 0x04dd }
                java.io.File r6 = r6.encryptionKeyPath     // Catch:{ all -> 0x04dd }
                r0.<init>((java.io.File) r3, (java.io.File) r6)     // Catch:{ all -> 0x04dd }
                goto L_0x048d
            L_0x0488:
                java.io.FileInputStream r0 = new java.io.FileInputStream     // Catch:{ all -> 0x04dd }
                r0.<init>(r3)     // Catch:{ all -> 0x04dd }
            L_0x048d:
                r6 = 0
                android.graphics.BitmapFactory.decodeStream(r0, r6, r13)     // Catch:{ all -> 0x04dd }
                r0.close()     // Catch:{ all -> 0x04dd }
            L_0x0494:
                int r0 = r13.outWidth     // Catch:{ all -> 0x04dd }
                float r0 = (float) r0     // Catch:{ all -> 0x04dd }
                int r6 = r13.outHeight     // Catch:{ all -> 0x04dd }
                float r6 = (float) r6     // Catch:{ all -> 0x04dd }
                int r8 = (r26 > r14 ? 1 : (r26 == r14 ? 0 : -1))
                if (r8 < 0) goto L_0x04ab
                int r8 = (r0 > r6 ? 1 : (r0 == r6 ? 0 : -1))
                if (r8 <= 0) goto L_0x04ab
                float r8 = r0 / r26
                float r9 = r6 / r14
                float r8 = java.lang.Math.max(r8, r9)     // Catch:{ all -> 0x04dd }
                goto L_0x04b3
            L_0x04ab:
                float r8 = r0 / r26
                float r9 = r6 / r14
                float r8 = java.lang.Math.min(r8, r9)     // Catch:{ all -> 0x04dd }
            L_0x04b3:
                r9 = 1067030938(0x3var_a, float:1.2)
                int r9 = (r8 > r9 ? 1 : (r8 == r9 ? 0 : -1))
                if (r9 >= 0) goto L_0x04bc
                r8 = 1065353216(0x3var_, float:1.0)
            L_0x04bc:
                r9 = 0
                r13.inJustDecodeBounds = r9     // Catch:{ all -> 0x04dd }
                int r9 = (r8 > r24 ? 1 : (r8 == r24 ? 0 : -1))
                if (r9 <= 0) goto L_0x04d9
                int r0 = (r0 > r26 ? 1 : (r0 == r26 ? 0 : -1))
                if (r0 > 0) goto L_0x04cb
                int r0 = (r6 > r14 ? 1 : (r6 == r14 ? 0 : -1))
                if (r0 <= 0) goto L_0x04d9
            L_0x04cb:
                r0 = 1
            L_0x04cc:
                r6 = 2
                int r0 = r0 * 2
                int r6 = r0 * 2
                float r6 = (float) r6     // Catch:{ all -> 0x04dd }
                int r6 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1))
                if (r6 < 0) goto L_0x04cc
                r13.inSampleSize = r0     // Catch:{ all -> 0x04dd }
                goto L_0x04ec
            L_0x04d9:
                int r0 = (int) r8     // Catch:{ all -> 0x04dd }
                r13.inSampleSize = r0     // Catch:{ all -> 0x04dd }
                goto L_0x04ec
            L_0x04dd:
                r0 = move-exception
                r10 = r0
                r0 = r14
                r14 = r26
                r9 = r30
                r6 = r31
                goto L_0x0507
            L_0x04e7:
                r31 = r6
                r11 = r8
                r30 = r9
            L_0x04ec:
                r9 = r30
                r6 = r31
                r8 = 0
                goto L_0x0583
            L_0x04f3:
                r0 = move-exception
                r31 = r6
            L_0x04f6:
                r11 = r8
                r30 = r9
                r10 = r0
                r0 = r14
                r14 = r26
                goto L_0x0507
            L_0x04fe:
                r0 = move-exception
                r11 = r8
                r30 = r9
                r10 = r0
                r0 = r14
                r14 = r26
                r6 = 0
            L_0x0507:
                r8 = 0
                goto L_0x058e
            L_0x050a:
                r0 = move-exception
                r11 = r8
                r10 = r0
                r0 = r14
                r14 = r26
            L_0x0510:
                r6 = 0
                r8 = 0
                r9 = 0
                goto L_0x058e
            L_0x0515:
                r11 = r8
                if (r12 == 0) goto L_0x057d
                r6 = 1
                r13.inJustDecodeBounds = r6     // Catch:{ all -> 0x057b }
                if (r22 == 0) goto L_0x0520
                android.graphics.Bitmap$Config r0 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ all -> 0x057b }
                goto L_0x0522
            L_0x0520:
                android.graphics.Bitmap$Config r0 = android.graphics.Bitmap.Config.RGB_565     // Catch:{ all -> 0x057b }
            L_0x0522:
                r13.inPreferredConfig = r0     // Catch:{ all -> 0x057b }
                java.io.FileInputStream r0 = new java.io.FileInputStream     // Catch:{ all -> 0x057b }
                r0.<init>(r3)     // Catch:{ all -> 0x057b }
                r6 = 0
                android.graphics.Bitmap r8 = android.graphics.BitmapFactory.decodeStream(r0, r6, r13)     // Catch:{ all -> 0x057b }
                r0.close()     // Catch:{ all -> 0x0576 }
                int r0 = r13.outWidth     // Catch:{ all -> 0x0576 }
                int r6 = r13.outHeight     // Catch:{ all -> 0x0576 }
                r9 = 0
                r13.inJustDecodeBounds = r9     // Catch:{ all -> 0x0576 }
                r9 = 66
                android.graphics.Point r10 = org.telegram.messenger.AndroidUtilities.getRealScreenSize()     // Catch:{ all -> 0x0576 }
                int r10 = r10.x     // Catch:{ all -> 0x0576 }
                android.graphics.Point r14 = org.telegram.messenger.AndroidUtilities.getRealScreenSize()     // Catch:{ all -> 0x0576 }
                int r14 = r14.y     // Catch:{ all -> 0x0576 }
                int r10 = java.lang.Math.min(r10, r14)     // Catch:{ all -> 0x0576 }
                int r9 = java.lang.Math.max(r9, r10)     // Catch:{ all -> 0x0576 }
                int r0 = java.lang.Math.min(r6, r0)     // Catch:{ all -> 0x0576 }
                float r0 = (float) r0     // Catch:{ all -> 0x0576 }
                float r6 = (float) r9     // Catch:{ all -> 0x0576 }
                float r0 = r0 / r6
                r6 = 1086324736(0x40CLASSNAME, float:6.0)
                float r0 = r0 * r6
                int r6 = (r0 > r24 ? 1 : (r0 == r24 ? 0 : -1))
                if (r6 >= 0) goto L_0x055f
                r0 = 1065353216(0x3var_, float:1.0)
            L_0x055f:
                int r6 = (r0 > r24 ? 1 : (r0 == r24 ? 0 : -1))
                if (r6 <= 0) goto L_0x0571
                r6 = 1
            L_0x0564:
                r9 = 2
                int r6 = r6 * 2
                int r9 = r6 * 2
                float r9 = (float) r9     // Catch:{ all -> 0x0576 }
                int r9 = (r9 > r0 ? 1 : (r9 == r0 ? 0 : -1))
                if (r9 <= 0) goto L_0x0564
                r13.inSampleSize = r6     // Catch:{ all -> 0x0576 }
                goto L_0x0574
            L_0x0571:
                int r0 = (int) r0     // Catch:{ all -> 0x0576 }
                r13.inSampleSize = r0     // Catch:{ all -> 0x0576 }
            L_0x0574:
                r6 = 0
                goto L_0x057f
            L_0x0576:
                r0 = move-exception
                r10 = r0
                r0 = 0
                r6 = 0
                goto L_0x058c
            L_0x057b:
                r0 = move-exception
                goto L_0x0588
            L_0x057d:
                r6 = 0
                r8 = 0
            L_0x057f:
                r9 = 0
                r14 = 0
                r26 = 0
            L_0x0583:
                r0 = r26
                goto L_0x0596
            L_0x0586:
                r0 = move-exception
                r11 = r8
            L_0x0588:
                r10 = r0
            L_0x0589:
                r0 = 0
                r6 = 0
                r8 = 0
            L_0x058c:
                r9 = 0
                r14 = 0
            L_0x058e:
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r10)
                r37 = r14
                r14 = r0
                r0 = r37
            L_0x0596:
                org.telegram.messenger.ImageLoader$CacheImage r10 = r1.cacheImage
                int r10 = r10.type
                r26 = 1101004800(0x41a00000, float:20.0)
                r30 = r8
                r8 = 1
                if (r10 != r8) goto L_0x07b6
                org.telegram.messenger.ImageLoader r2 = org.telegram.messenger.ImageLoader.this     // Catch:{ all -> 0x07ab }
                long r14 = android.os.SystemClock.elapsedRealtime()     // Catch:{ all -> 0x07ab }
                long unused = r2.lastCacheOutTime = r14     // Catch:{ all -> 0x07ab }
                java.lang.Object r2 = r1.sync     // Catch:{ all -> 0x07ab }
                monitor-enter(r2)     // Catch:{ all -> 0x07ab }
                boolean r8 = r1.isCancelled     // Catch:{ all -> 0x07a8 }
                if (r8 == 0) goto L_0x05b3
                monitor-exit(r2)     // Catch:{ all -> 0x07a8 }
                return
            L_0x05b3:
                monitor-exit(r2)     // Catch:{ all -> 0x07a8 }
                if (r11 == 0) goto L_0x05fb
                java.io.RandomAccessFile r2 = new java.io.RandomAccessFile     // Catch:{ all -> 0x07ab }
                java.lang.String r4 = "r"
                r2.<init>(r3, r4)     // Catch:{ all -> 0x07ab }
                java.nio.channels.FileChannel r31 = r2.getChannel()     // Catch:{ all -> 0x07ab }
                java.nio.channels.FileChannel$MapMode r32 = java.nio.channels.FileChannel.MapMode.READ_ONLY     // Catch:{ all -> 0x07ab }
                r33 = 0
                long r35 = r3.length()     // Catch:{ all -> 0x07ab }
                java.nio.MappedByteBuffer r4 = r31.map(r32, r33, r35)     // Catch:{ all -> 0x07ab }
                android.graphics.BitmapFactory$Options r5 = new android.graphics.BitmapFactory$Options     // Catch:{ all -> 0x07ab }
                r5.<init>()     // Catch:{ all -> 0x07ab }
                r7 = 1
                r5.inJustDecodeBounds = r7     // Catch:{ all -> 0x07ab }
                int r8 = r4.limit()     // Catch:{ all -> 0x07ab }
                r10 = 0
                org.telegram.messenger.Utilities.loadWebpImage(r10, r4, r8, r5, r7)     // Catch:{ all -> 0x07ab }
                int r7 = r5.outWidth     // Catch:{ all -> 0x07ab }
                int r5 = r5.outHeight     // Catch:{ all -> 0x07ab }
                android.graphics.Bitmap$Config r8 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ all -> 0x07ab }
                android.graphics.Bitmap r8 = org.telegram.messenger.Bitmaps.createBitmap(r7, r5, r8)     // Catch:{ all -> 0x07ab }
                int r5 = r4.limit()     // Catch:{ all -> 0x061c }
                boolean r7 = r13.inPurgeable     // Catch:{ all -> 0x061c }
                if (r7 != 0) goto L_0x05f1
                r7 = 1
                goto L_0x05f2
            L_0x05f1:
                r7 = 0
            L_0x05f2:
                r10 = 0
                org.telegram.messenger.Utilities.loadWebpImage(r8, r4, r5, r10, r7)     // Catch:{ all -> 0x061c }
                r2.close()     // Catch:{ all -> 0x061c }
                goto L_0x067f
            L_0x05fb:
                boolean r2 = r13.inPurgeable     // Catch:{ all -> 0x07ab }
                if (r2 != 0) goto L_0x061f
                if (r5 == 0) goto L_0x0602
                goto L_0x061f
            L_0x0602:
                if (r4 == 0) goto L_0x060e
                org.telegram.messenger.secretmedia.EncryptedFileInputStream r2 = new org.telegram.messenger.secretmedia.EncryptedFileInputStream     // Catch:{ all -> 0x07ab }
                org.telegram.messenger.ImageLoader$CacheImage r4 = r1.cacheImage     // Catch:{ all -> 0x07ab }
                java.io.File r4 = r4.encryptionKeyPath     // Catch:{ all -> 0x07ab }
                r2.<init>((java.io.File) r3, (java.io.File) r4)     // Catch:{ all -> 0x07ab }
                goto L_0x0613
            L_0x060e:
                java.io.FileInputStream r2 = new java.io.FileInputStream     // Catch:{ all -> 0x07ab }
                r2.<init>(r3)     // Catch:{ all -> 0x07ab }
            L_0x0613:
                r4 = 0
                android.graphics.Bitmap r8 = android.graphics.BitmapFactory.decodeStream(r2, r4, r13)     // Catch:{ all -> 0x07ab }
                r2.close()     // Catch:{ all -> 0x061c }
                goto L_0x067f
            L_0x061c:
                r0 = move-exception
                goto L_0x07ae
            L_0x061f:
                java.io.RandomAccessFile r2 = new java.io.RandomAccessFile     // Catch:{ all -> 0x07ab }
                java.lang.String r8 = "r"
                r2.<init>(r3, r8)     // Catch:{ all -> 0x07ab }
                long r10 = r2.length()     // Catch:{ all -> 0x07ab }
                int r8 = (int) r10     // Catch:{ all -> 0x07ab }
                java.lang.ThreadLocal r10 = org.telegram.messenger.ImageLoader.bytesThumbLocal     // Catch:{ all -> 0x07ab }
                java.lang.Object r10 = r10.get()     // Catch:{ all -> 0x07ab }
                byte[] r10 = (byte[]) r10     // Catch:{ all -> 0x07ab }
                if (r10 == 0) goto L_0x063b
                int r11 = r10.length     // Catch:{ all -> 0x07ab }
                if (r11 < r8) goto L_0x063b
                goto L_0x063c
            L_0x063b:
                r10 = 0
            L_0x063c:
                if (r10 != 0) goto L_0x0647
                byte[] r10 = new byte[r8]     // Catch:{ all -> 0x07ab }
                java.lang.ThreadLocal r11 = org.telegram.messenger.ImageLoader.bytesThumbLocal     // Catch:{ all -> 0x07ab }
                r11.set(r10)     // Catch:{ all -> 0x07ab }
            L_0x0647:
                r11 = 0
                r2.readFully(r10, r11, r8)     // Catch:{ all -> 0x07ab }
                r2.close()     // Catch:{ all -> 0x07ab }
                if (r5 == 0) goto L_0x066a
                org.telegram.messenger.secretmedia.EncryptedFileInputStream.decryptBytesWithKeyFile((byte[]) r10, (int) r11, (int) r8, (org.telegram.messenger.SecureDocumentKey) r5)     // Catch:{ all -> 0x07ab }
                byte[] r2 = org.telegram.messenger.Utilities.computeSHA256(r10, r11, r8)     // Catch:{ all -> 0x07ab }
                if (r7 == 0) goto L_0x0662
                boolean r2 = java.util.Arrays.equals(r2, r7)     // Catch:{ all -> 0x07ab }
                if (r2 != 0) goto L_0x0660
                goto L_0x0662
            L_0x0660:
                r2 = 0
                goto L_0x0663
            L_0x0662:
                r2 = 1
            L_0x0663:
                r4 = 0
                byte r5 = r10[r4]     // Catch:{ all -> 0x07ab }
                r4 = r5 & 255(0xff, float:3.57E-43)
                int r8 = r8 - r4
                goto L_0x0676
            L_0x066a:
                if (r4 == 0) goto L_0x0674
                org.telegram.messenger.ImageLoader$CacheImage r2 = r1.cacheImage     // Catch:{ all -> 0x07ab }
                java.io.File r2 = r2.encryptionKeyPath     // Catch:{ all -> 0x07ab }
                r4 = 0
                org.telegram.messenger.secretmedia.EncryptedFileInputStream.decryptBytesWithKeyFile((byte[]) r10, (int) r4, (int) r8, (java.io.File) r2)     // Catch:{ all -> 0x07ab }
            L_0x0674:
                r2 = 0
                r4 = 0
            L_0x0676:
                if (r2 != 0) goto L_0x067d
                android.graphics.Bitmap r8 = android.graphics.BitmapFactory.decodeByteArray(r10, r4, r8, r13)     // Catch:{ all -> 0x07ab }
                goto L_0x067f
            L_0x067d:
                r8 = r30
            L_0x067f:
                if (r8 != 0) goto L_0x0697
                long r4 = r3.length()     // Catch:{ all -> 0x061c }
                r6 = 0
                int r0 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
                if (r0 == 0) goto L_0x0691
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage     // Catch:{ all -> 0x061c }
                java.lang.String r0 = r0.filter     // Catch:{ all -> 0x061c }
                if (r0 != 0) goto L_0x0694
            L_0x0691:
                r3.delete()     // Catch:{ all -> 0x061c }
            L_0x0694:
                r2 = 0
                goto L_0x07b2
            L_0x0697:
                org.telegram.messenger.ImageLoader$CacheImage r2 = r1.cacheImage     // Catch:{ all -> 0x061c }
                java.lang.String r2 = r2.filter     // Catch:{ all -> 0x061c }
                if (r2 == 0) goto L_0x06c8
                int r2 = r8.getWidth()     // Catch:{ all -> 0x061c }
                float r2 = (float) r2     // Catch:{ all -> 0x061c }
                int r3 = r8.getHeight()     // Catch:{ all -> 0x061c }
                float r3 = (float) r3     // Catch:{ all -> 0x061c }
                boolean r4 = r13.inPurgeable     // Catch:{ all -> 0x061c }
                if (r4 != 0) goto L_0x06c8
                int r4 = (r0 > r23 ? 1 : (r0 == r23 ? 0 : -1))
                if (r4 == 0) goto L_0x06c8
                int r4 = (r2 > r0 ? 1 : (r2 == r0 ? 0 : -1))
                if (r4 == 0) goto L_0x06c8
                float r26 = r0 + r26
                int r4 = (r2 > r26 ? 1 : (r2 == r26 ? 0 : -1))
                if (r4 <= 0) goto L_0x06c8
                float r2 = r2 / r0
                int r0 = (int) r0     // Catch:{ all -> 0x061c }
                float r3 = r3 / r2
                int r2 = (int) r3     // Catch:{ all -> 0x061c }
                r3 = 1
                android.graphics.Bitmap r0 = org.telegram.messenger.Bitmaps.createScaledBitmap(r8, r0, r2, r3)     // Catch:{ all -> 0x061c }
                if (r8 == r0) goto L_0x06c8
                r8.recycle()     // Catch:{ all -> 0x061c }
                r8 = r0
            L_0x06c8:
                if (r6 == 0) goto L_0x06e8
                boolean r0 = r13.inPurgeable     // Catch:{ all -> 0x061c }
                if (r0 == 0) goto L_0x06d0
                r0 = 0
                goto L_0x06d1
            L_0x06d0:
                r0 = 1
            L_0x06d1:
                int r2 = r8.getWidth()     // Catch:{ all -> 0x061c }
                int r3 = r8.getHeight()     // Catch:{ all -> 0x061c }
                int r4 = r8.getRowBytes()     // Catch:{ all -> 0x061c }
                int r0 = org.telegram.messenger.Utilities.needInvert(r8, r0, r2, r3, r4)     // Catch:{ all -> 0x061c }
                if (r0 == 0) goto L_0x06e5
                r0 = 1
                goto L_0x06e6
            L_0x06e5:
                r0 = 0
            L_0x06e6:
                r2 = r0
                goto L_0x06e9
            L_0x06e8:
                r2 = 0
            L_0x06e9:
                r3 = 1
                if (r9 != r3) goto L_0x0715
                android.graphics.Bitmap$Config r0 = r8.getConfig()     // Catch:{ all -> 0x0712 }
                android.graphics.Bitmap$Config r3 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ all -> 0x0712 }
                if (r0 != r3) goto L_0x07b2
                r18 = 3
                boolean r0 = r13.inPurgeable     // Catch:{ all -> 0x0712 }
                if (r0 == 0) goto L_0x06fd
                r19 = 0
                goto L_0x06ff
            L_0x06fd:
                r19 = 1
            L_0x06ff:
                int r20 = r8.getWidth()     // Catch:{ all -> 0x0712 }
                int r21 = r8.getHeight()     // Catch:{ all -> 0x0712 }
                int r22 = r8.getRowBytes()     // Catch:{ all -> 0x0712 }
                r17 = r8
                org.telegram.messenger.Utilities.blurBitmap(r17, r18, r19, r20, r21, r22)     // Catch:{ all -> 0x0712 }
                goto L_0x07b2
            L_0x0712:
                r0 = move-exception
                goto L_0x07af
            L_0x0715:
                r3 = 2
                if (r9 != r3) goto L_0x073e
                android.graphics.Bitmap$Config r0 = r8.getConfig()     // Catch:{ all -> 0x0712 }
                android.graphics.Bitmap$Config r3 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ all -> 0x0712 }
                if (r0 != r3) goto L_0x07b2
                r18 = 1
                boolean r0 = r13.inPurgeable     // Catch:{ all -> 0x0712 }
                if (r0 == 0) goto L_0x0729
                r19 = 0
                goto L_0x072b
            L_0x0729:
                r19 = 1
            L_0x072b:
                int r20 = r8.getWidth()     // Catch:{ all -> 0x0712 }
                int r21 = r8.getHeight()     // Catch:{ all -> 0x0712 }
                int r22 = r8.getRowBytes()     // Catch:{ all -> 0x0712 }
                r17 = r8
                org.telegram.messenger.Utilities.blurBitmap(r17, r18, r19, r20, r21, r22)     // Catch:{ all -> 0x0712 }
                goto L_0x07b2
            L_0x073e:
                r3 = 3
                if (r9 != r3) goto L_0x079e
                android.graphics.Bitmap$Config r0 = r8.getConfig()     // Catch:{ all -> 0x0712 }
                android.graphics.Bitmap$Config r3 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ all -> 0x0712 }
                if (r0 != r3) goto L_0x07b2
                r18 = 7
                boolean r0 = r13.inPurgeable     // Catch:{ all -> 0x0712 }
                if (r0 == 0) goto L_0x0752
                r19 = 0
                goto L_0x0754
            L_0x0752:
                r19 = 1
            L_0x0754:
                int r20 = r8.getWidth()     // Catch:{ all -> 0x0712 }
                int r21 = r8.getHeight()     // Catch:{ all -> 0x0712 }
                int r22 = r8.getRowBytes()     // Catch:{ all -> 0x0712 }
                r17 = r8
                org.telegram.messenger.Utilities.blurBitmap(r17, r18, r19, r20, r21, r22)     // Catch:{ all -> 0x0712 }
                r18 = 7
                boolean r0 = r13.inPurgeable     // Catch:{ all -> 0x0712 }
                if (r0 == 0) goto L_0x076e
                r19 = 0
                goto L_0x0770
            L_0x076e:
                r19 = 1
            L_0x0770:
                int r20 = r8.getWidth()     // Catch:{ all -> 0x0712 }
                int r21 = r8.getHeight()     // Catch:{ all -> 0x0712 }
                int r22 = r8.getRowBytes()     // Catch:{ all -> 0x0712 }
                r17 = r8
                org.telegram.messenger.Utilities.blurBitmap(r17, r18, r19, r20, r21, r22)     // Catch:{ all -> 0x0712 }
                r18 = 7
                boolean r0 = r13.inPurgeable     // Catch:{ all -> 0x0712 }
                if (r0 == 0) goto L_0x078a
                r19 = 0
                goto L_0x078c
            L_0x078a:
                r19 = 1
            L_0x078c:
                int r20 = r8.getWidth()     // Catch:{ all -> 0x0712 }
                int r21 = r8.getHeight()     // Catch:{ all -> 0x0712 }
                int r22 = r8.getRowBytes()     // Catch:{ all -> 0x0712 }
                r17 = r8
                org.telegram.messenger.Utilities.blurBitmap(r17, r18, r19, r20, r21, r22)     // Catch:{ all -> 0x0712 }
                goto L_0x07b2
            L_0x079e:
                if (r9 != 0) goto L_0x07b2
                boolean r0 = r13.inPurgeable     // Catch:{ all -> 0x0712 }
                if (r0 == 0) goto L_0x07b2
                org.telegram.messenger.Utilities.pinBitmap(r8)     // Catch:{ all -> 0x0712 }
                goto L_0x07b2
            L_0x07a8:
                r0 = move-exception
                monitor-exit(r2)     // Catch:{ all -> 0x07a8 }
                throw r0     // Catch:{ all -> 0x07ab }
            L_0x07ab:
                r0 = move-exception
                r8 = r30
            L_0x07ae:
                r2 = 0
            L_0x07af:
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            L_0x07b2:
                r6 = 0
                r11 = 0
                goto L_0x0a5c
            L_0x07b6:
                r8 = 20
                if (r18 == 0) goto L_0x07bb
                r8 = 0
            L_0x07bb:
                if (r8 == 0) goto L_0x07ea
                org.telegram.messenger.ImageLoader r10 = org.telegram.messenger.ImageLoader.this     // Catch:{ all -> 0x07e5 }
                long r31 = r10.lastCacheOutTime     // Catch:{ all -> 0x07e5 }
                r20 = 0
                int r10 = (r31 > r20 ? 1 : (r31 == r20 ? 0 : -1))
                if (r10 == 0) goto L_0x07ea
                org.telegram.messenger.ImageLoader r10 = org.telegram.messenger.ImageLoader.this     // Catch:{ all -> 0x07e5 }
                long r31 = r10.lastCacheOutTime     // Catch:{ all -> 0x07e5 }
                long r33 = android.os.SystemClock.elapsedRealtime()     // Catch:{ all -> 0x07e5 }
                r27 = r6
                r10 = r7
                long r6 = (long) r8     // Catch:{ all -> 0x07e5 }
                long r33 = r33 - r6
                int r8 = (r31 > r33 ? 1 : (r31 == r33 ? 0 : -1))
                if (r8 <= 0) goto L_0x07ed
                r8 = 21
                if (r2 >= r8) goto L_0x07ed
                java.lang.Thread.sleep(r6)     // Catch:{ all -> 0x07e5 }
                goto L_0x07ed
            L_0x07e5:
                r8 = r30
            L_0x07e7:
                r6 = 0
                goto L_0x0a56
            L_0x07ea:
                r27 = r6
                r10 = r7
            L_0x07ed:
                org.telegram.messenger.ImageLoader r2 = org.telegram.messenger.ImageLoader.this     // Catch:{ all -> 0x0a53 }
                long r6 = android.os.SystemClock.elapsedRealtime()     // Catch:{ all -> 0x0a53 }
                long unused = r2.lastCacheOutTime = r6     // Catch:{ all -> 0x0a53 }
                java.lang.Object r2 = r1.sync     // Catch:{ all -> 0x0a53 }
                monitor-enter(r2)     // Catch:{ all -> 0x0a53 }
                boolean r6 = r1.isCancelled     // Catch:{ all -> 0x0a4d }
                if (r6 == 0) goto L_0x07ff
                monitor-exit(r2)     // Catch:{ all -> 0x0a4d }
                return
            L_0x07ff:
                monitor-exit(r2)     // Catch:{ all -> 0x0a4d }
                if (r22 != 0) goto L_0x0816
                org.telegram.messenger.ImageLoader$CacheImage r2 = r1.cacheImage     // Catch:{ all -> 0x07e5 }
                java.lang.String r6 = r2.filter     // Catch:{ all -> 0x07e5 }
                if (r6 == 0) goto L_0x0816
                if (r9 != 0) goto L_0x0816
                org.telegram.messenger.ImageLocation r2 = r2.imageLocation     // Catch:{ all -> 0x07e5 }
                java.lang.String r2 = r2.path     // Catch:{ all -> 0x07e5 }
                if (r2 == 0) goto L_0x0811
                goto L_0x0816
            L_0x0811:
                android.graphics.Bitmap$Config r2 = android.graphics.Bitmap.Config.RGB_565     // Catch:{ all -> 0x07e5 }
                r13.inPreferredConfig = r2     // Catch:{ all -> 0x07e5 }
                goto L_0x081a
            L_0x0816:
                android.graphics.Bitmap$Config r2 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ all -> 0x0a53 }
                r13.inPreferredConfig = r2     // Catch:{ all -> 0x0a53 }
            L_0x081a:
                r2 = 0
                r13.inDither = r2     // Catch:{ all -> 0x0a53 }
                if (r18 == 0) goto L_0x0844
                if (r12 != 0) goto L_0x0844
                if (r15 == 0) goto L_0x0833
                android.content.Context r2 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x07e5 }
                android.content.ContentResolver r2 = r2.getContentResolver()     // Catch:{ all -> 0x07e5 }
                long r6 = r18.longValue()     // Catch:{ all -> 0x07e5 }
                r8 = 1
                android.graphics.Bitmap r2 = android.provider.MediaStore.Video.Thumbnails.getThumbnail(r2, r6, r8, r13)     // Catch:{ all -> 0x07e5 }
                goto L_0x0842
            L_0x0833:
                android.content.Context r2 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x07e5 }
                android.content.ContentResolver r2 = r2.getContentResolver()     // Catch:{ all -> 0x07e5 }
                long r6 = r18.longValue()     // Catch:{ all -> 0x07e5 }
                r8 = 1
                android.graphics.Bitmap r2 = android.provider.MediaStore.Images.Thumbnails.getThumbnail(r2, r6, r8, r13)     // Catch:{ all -> 0x07e5 }
            L_0x0842:
                r8 = r2
                goto L_0x0846
            L_0x0844:
                r8 = r30
            L_0x0846:
                if (r8 != 0) goto L_0x0955
                if (r11 == 0) goto L_0x0895
                java.io.RandomAccessFile r2 = new java.io.RandomAccessFile     // Catch:{ all -> 0x07e7 }
                java.lang.String r4 = "r"
                r2.<init>(r3, r4)     // Catch:{ all -> 0x07e7 }
                java.nio.channels.FileChannel r31 = r2.getChannel()     // Catch:{ all -> 0x07e7 }
                java.nio.channels.FileChannel$MapMode r32 = java.nio.channels.FileChannel.MapMode.READ_ONLY     // Catch:{ all -> 0x07e7 }
                r33 = 0
                long r35 = r3.length()     // Catch:{ all -> 0x07e7 }
                java.nio.MappedByteBuffer r4 = r31.map(r32, r33, r35)     // Catch:{ all -> 0x07e7 }
                android.graphics.BitmapFactory$Options r5 = new android.graphics.BitmapFactory$Options     // Catch:{ all -> 0x07e7 }
                r5.<init>()     // Catch:{ all -> 0x07e7 }
                r6 = 1
                r5.inJustDecodeBounds = r6     // Catch:{ all -> 0x07e7 }
                int r7 = r4.limit()     // Catch:{ all -> 0x07e7 }
                r10 = 0
                org.telegram.messenger.Utilities.loadWebpImage(r10, r4, r7, r5, r6)     // Catch:{ all -> 0x0892 }
                int r6 = r5.outWidth     // Catch:{ all -> 0x07e7 }
                int r5 = r5.outHeight     // Catch:{ all -> 0x07e7 }
                android.graphics.Bitmap$Config r7 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ all -> 0x07e7 }
                android.graphics.Bitmap r8 = org.telegram.messenger.Bitmaps.createBitmap(r6, r5, r7)     // Catch:{ all -> 0x07e7 }
                int r5 = r4.limit()     // Catch:{ all -> 0x07e7 }
                boolean r6 = r13.inPurgeable     // Catch:{ all -> 0x07e7 }
                if (r6 != 0) goto L_0x0885
                r6 = 1
                goto L_0x0886
            L_0x0885:
                r6 = 0
            L_0x0886:
                r7 = 0
                org.telegram.messenger.Utilities.loadWebpImage(r8, r4, r5, r7, r6)     // Catch:{ all -> 0x088f }
                r2.close()     // Catch:{ all -> 0x07e7 }
                goto L_0x0955
            L_0x088f:
                r6 = r7
                goto L_0x0a56
            L_0x0892:
                r6 = r10
                goto L_0x0a56
            L_0x0895:
                boolean r2 = r13.inPurgeable     // Catch:{ all -> 0x07e7 }
                if (r2 != 0) goto L_0x08f6
                if (r5 == 0) goto L_0x089c
                goto L_0x08f6
            L_0x089c:
                if (r4 == 0) goto L_0x08a8
                org.telegram.messenger.secretmedia.EncryptedFileInputStream r2 = new org.telegram.messenger.secretmedia.EncryptedFileInputStream     // Catch:{ all -> 0x07e7 }
                org.telegram.messenger.ImageLoader$CacheImage r4 = r1.cacheImage     // Catch:{ all -> 0x07e7 }
                java.io.File r4 = r4.encryptionKeyPath     // Catch:{ all -> 0x07e7 }
                r2.<init>((java.io.File) r3, (java.io.File) r4)     // Catch:{ all -> 0x07e7 }
                goto L_0x08ad
            L_0x08a8:
                java.io.FileInputStream r2 = new java.io.FileInputStream     // Catch:{ all -> 0x07e7 }
                r2.<init>(r3)     // Catch:{ all -> 0x07e7 }
            L_0x08ad:
                org.telegram.messenger.ImageLoader$CacheImage r4 = r1.cacheImage     // Catch:{ all -> 0x07e7 }
                org.telegram.messenger.ImageLocation r4 = r4.imageLocation     // Catch:{ all -> 0x07e7 }
                org.telegram.tgnet.TLRPC$Document r4 = r4.document     // Catch:{ all -> 0x07e7 }
                boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC$TL_document     // Catch:{ all -> 0x07e7 }
                if (r4 == 0) goto L_0x08e7
                androidx.exifinterface.media.ExifInterface r4 = new androidx.exifinterface.media.ExifInterface     // Catch:{ all -> 0x08d7 }
                r4.<init>((java.io.InputStream) r2)     // Catch:{ all -> 0x08d7 }
                java.lang.String r5 = "Orientation"
                r6 = 1
                int r4 = r4.getAttributeInt(r5, r6)     // Catch:{ all -> 0x08d7 }
                r5 = 3
                if (r4 == r5) goto L_0x08d4
                r5 = 6
                if (r4 == r5) goto L_0x08d1
                r5 = 8
                if (r4 == r5) goto L_0x08ce
                goto L_0x08d7
            L_0x08ce:
                r4 = 270(0x10e, float:3.78E-43)
                goto L_0x08d8
            L_0x08d1:
                r4 = 90
                goto L_0x08d8
            L_0x08d4:
                r4 = 180(0xb4, float:2.52E-43)
                goto L_0x08d8
            L_0x08d7:
                r4 = 0
            L_0x08d8:
                java.nio.channels.FileChannel r5 = r2.getChannel()     // Catch:{ all -> 0x08e2 }
                r6 = 0
                r5.position(r6)     // Catch:{ all -> 0x08e2 }
                goto L_0x08e8
            L_0x08e2:
                r29 = r4
                r6 = 0
                goto L_0x0a4b
            L_0x08e7:
                r4 = 0
            L_0x08e8:
                r6 = 0
                android.graphics.Bitmap r8 = android.graphics.BitmapFactory.decodeStream(r2, r6, r13)     // Catch:{ all -> 0x08f2 }
                r2.close()     // Catch:{ all -> 0x08f2 }
                r10 = r4
                goto L_0x0957
            L_0x08f2:
                r29 = r4
                goto L_0x0a4b
            L_0x08f6:
                r6 = 0
                java.io.RandomAccessFile r2 = new java.io.RandomAccessFile     // Catch:{ all -> 0x0a56 }
                java.lang.String r7 = "r"
                r2.<init>(r3, r7)     // Catch:{ all -> 0x0a56 }
                long r11 = r2.length()     // Catch:{ all -> 0x0a56 }
                int r7 = (int) r11     // Catch:{ all -> 0x0a56 }
                java.lang.ThreadLocal r11 = org.telegram.messenger.ImageLoader.bytesLocal     // Catch:{ all -> 0x0a56 }
                java.lang.Object r11 = r11.get()     // Catch:{ all -> 0x0a56 }
                byte[] r11 = (byte[]) r11     // Catch:{ all -> 0x0a56 }
                if (r11 == 0) goto L_0x0913
                int r12 = r11.length     // Catch:{ all -> 0x0a56 }
                if (r12 < r7) goto L_0x0913
                goto L_0x0914
            L_0x0913:
                r11 = r6
            L_0x0914:
                if (r11 != 0) goto L_0x091f
                byte[] r11 = new byte[r7]     // Catch:{ all -> 0x0a56 }
                java.lang.ThreadLocal r12 = org.telegram.messenger.ImageLoader.bytesLocal     // Catch:{ all -> 0x0a56 }
                r12.set(r11)     // Catch:{ all -> 0x0a56 }
            L_0x091f:
                r12 = 0
                r2.readFully(r11, r12, r7)     // Catch:{ all -> 0x0a56 }
                r2.close()     // Catch:{ all -> 0x0a56 }
                if (r5 == 0) goto L_0x0942
                org.telegram.messenger.secretmedia.EncryptedFileInputStream.decryptBytesWithKeyFile((byte[]) r11, (int) r12, (int) r7, (org.telegram.messenger.SecureDocumentKey) r5)     // Catch:{ all -> 0x0a56 }
                byte[] r2 = org.telegram.messenger.Utilities.computeSHA256(r11, r12, r7)     // Catch:{ all -> 0x0a56 }
                if (r10 == 0) goto L_0x093a
                boolean r2 = java.util.Arrays.equals(r2, r10)     // Catch:{ all -> 0x0a56 }
                if (r2 != 0) goto L_0x0938
                goto L_0x093a
            L_0x0938:
                r2 = 0
                goto L_0x093b
            L_0x093a:
                r2 = 1
            L_0x093b:
                r4 = 0
                byte r5 = r11[r4]     // Catch:{ all -> 0x0a56 }
                r4 = r5 & 255(0xff, float:3.57E-43)
                int r7 = r7 - r4
                goto L_0x094e
            L_0x0942:
                if (r4 == 0) goto L_0x094c
                org.telegram.messenger.ImageLoader$CacheImage r2 = r1.cacheImage     // Catch:{ all -> 0x0a56 }
                java.io.File r2 = r2.encryptionKeyPath     // Catch:{ all -> 0x0a56 }
                r4 = 0
                org.telegram.messenger.secretmedia.EncryptedFileInputStream.decryptBytesWithKeyFile((byte[]) r11, (int) r4, (int) r7, (java.io.File) r2)     // Catch:{ all -> 0x0a56 }
            L_0x094c:
                r2 = 0
                r4 = 0
            L_0x094e:
                if (r2 != 0) goto L_0x0956
                android.graphics.Bitmap r8 = android.graphics.BitmapFactory.decodeByteArray(r11, r4, r7, r13)     // Catch:{ all -> 0x0a56 }
                goto L_0x0956
            L_0x0955:
                r6 = 0
            L_0x0956:
                r10 = 0
            L_0x0957:
                if (r8 != 0) goto L_0x0971
                if (r19 == 0) goto L_0x096e
                long r4 = r3.length()     // Catch:{ all -> 0x0a49 }
                r11 = 0
                int r0 = (r4 > r11 ? 1 : (r4 == r11 ? 0 : -1))
                if (r0 == 0) goto L_0x096b
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage     // Catch:{ all -> 0x0a49 }
                java.lang.String r0 = r0.filter     // Catch:{ all -> 0x0a49 }
                if (r0 != 0) goto L_0x096e
            L_0x096b:
                r3.delete()     // Catch:{ all -> 0x0a49 }
            L_0x096e:
                r11 = 0
                goto L_0x0a46
            L_0x0971:
                org.telegram.messenger.ImageLoader$CacheImage r2 = r1.cacheImage     // Catch:{ all -> 0x0a49 }
                java.lang.String r2 = r2.filter     // Catch:{ all -> 0x0a49 }
                if (r2 == 0) goto L_0x0a36
                int r2 = r8.getWidth()     // Catch:{ all -> 0x0a49 }
                float r2 = (float) r2     // Catch:{ all -> 0x0a49 }
                int r3 = r8.getHeight()     // Catch:{ all -> 0x0a49 }
                float r3 = (float) r3     // Catch:{ all -> 0x0a49 }
                boolean r4 = r13.inPurgeable     // Catch:{ all -> 0x0a49 }
                if (r4 != 0) goto L_0x09c2
                int r4 = (r0 > r23 ? 1 : (r0 == r23 ? 0 : -1))
                if (r4 == 0) goto L_0x09c2
                int r4 = (r2 > r0 ? 1 : (r2 == r0 ? 0 : -1))
                if (r4 == 0) goto L_0x09c2
                float r26 = r0 + r26
                int r4 = (r2 > r26 ? 1 : (r2 == r26 ? 0 : -1))
                if (r4 <= 0) goto L_0x09c2
                int r4 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
                if (r4 <= 0) goto L_0x09ab
                int r4 = (r0 > r14 ? 1 : (r0 == r14 ? 0 : -1))
                if (r4 <= 0) goto L_0x09ab
                float r4 = r2 / r0
                int r5 = (r4 > r24 ? 1 : (r4 == r24 ? 0 : -1))
                if (r5 <= 0) goto L_0x09bb
                int r0 = (int) r0     // Catch:{ all -> 0x0a49 }
                float r4 = r3 / r4
                int r4 = (int) r4     // Catch:{ all -> 0x0a49 }
                r5 = 1
                android.graphics.Bitmap r0 = org.telegram.messenger.Bitmaps.createScaledBitmap(r8, r0, r4, r5)     // Catch:{ all -> 0x0a49 }
                goto L_0x09bc
            L_0x09ab:
                float r0 = r3 / r14
                int r4 = (r0 > r24 ? 1 : (r0 == r24 ? 0 : -1))
                if (r4 <= 0) goto L_0x09bb
                float r0 = r2 / r0
                int r0 = (int) r0     // Catch:{ all -> 0x0a49 }
                int r4 = (int) r14     // Catch:{ all -> 0x0a49 }
                r5 = 1
                android.graphics.Bitmap r0 = org.telegram.messenger.Bitmaps.createScaledBitmap(r8, r0, r4, r5)     // Catch:{ all -> 0x0a49 }
                goto L_0x09bc
            L_0x09bb:
                r0 = r8
            L_0x09bc:
                if (r8 == r0) goto L_0x09c2
                r8.recycle()     // Catch:{ all -> 0x0a49 }
                r8 = r0
            L_0x09c2:
                if (r8 == 0) goto L_0x0a36
                if (r27 == 0) goto L_0x09ff
                int r0 = r8.getWidth()     // Catch:{ all -> 0x0a49 }
                int r4 = r8.getHeight()     // Catch:{ all -> 0x0a49 }
                int r0 = r0 * r4
                r4 = 22500(0x57e4, float:3.1529E-41)
                if (r0 <= r4) goto L_0x09dc
                r0 = 100
                r4 = 0
                android.graphics.Bitmap r0 = org.telegram.messenger.Bitmaps.createScaledBitmap(r8, r0, r0, r4)     // Catch:{ all -> 0x0a49 }
                goto L_0x09dd
            L_0x09dc:
                r0 = r8
            L_0x09dd:
                boolean r4 = r13.inPurgeable     // Catch:{ all -> 0x0a49 }
                if (r4 == 0) goto L_0x09e3
                r4 = 0
                goto L_0x09e4
            L_0x09e3:
                r4 = 1
            L_0x09e4:
                int r5 = r0.getWidth()     // Catch:{ all -> 0x0a49 }
                int r7 = r0.getHeight()     // Catch:{ all -> 0x0a49 }
                int r11 = r0.getRowBytes()     // Catch:{ all -> 0x0a49 }
                int r4 = org.telegram.messenger.Utilities.needInvert(r0, r4, r5, r7, r11)     // Catch:{ all -> 0x0a49 }
                if (r4 == 0) goto L_0x09f8
                r4 = 1
                goto L_0x09f9
            L_0x09f8:
                r4 = 0
            L_0x09f9:
                if (r0 == r8) goto L_0x0a00
                r0.recycle()     // Catch:{ all -> 0x0a32 }
                goto L_0x0a00
            L_0x09ff:
                r4 = 0
            L_0x0a00:
                if (r9 == 0) goto L_0x0a34
                r0 = 1120403456(0x42CLASSNAME, float:100.0)
                int r3 = (r3 > r0 ? 1 : (r3 == r0 ? 0 : -1))
                if (r3 >= 0) goto L_0x0a34
                int r0 = (r2 > r0 ? 1 : (r2 == r0 ? 0 : -1))
                if (r0 >= 0) goto L_0x0a34
                android.graphics.Bitmap$Config r0 = r8.getConfig()     // Catch:{ all -> 0x0a32 }
                android.graphics.Bitmap$Config r2 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ all -> 0x0a32 }
                if (r0 != r2) goto L_0x0a2e
                r15 = 3
                boolean r0 = r13.inPurgeable     // Catch:{ all -> 0x0a32 }
                if (r0 == 0) goto L_0x0a1c
                r16 = 0
                goto L_0x0a1e
            L_0x0a1c:
                r16 = 1
            L_0x0a1e:
                int r17 = r8.getWidth()     // Catch:{ all -> 0x0a32 }
                int r18 = r8.getHeight()     // Catch:{ all -> 0x0a32 }
                int r19 = r8.getRowBytes()     // Catch:{ all -> 0x0a32 }
                r14 = r8
                org.telegram.messenger.Utilities.blurBitmap(r14, r15, r16, r17, r18, r19)     // Catch:{ all -> 0x0a32 }
            L_0x0a2e:
                r11 = r4
                r29 = 1
                goto L_0x0a39
            L_0x0a32:
                r11 = r4
                goto L_0x0a43
            L_0x0a34:
                r11 = r4
                goto L_0x0a37
            L_0x0a36:
                r11 = 0
            L_0x0a37:
                r29 = 0
            L_0x0a39:
                if (r29 != 0) goto L_0x0a46
                boolean r0 = r13.inPurgeable     // Catch:{ all -> 0x0a43 }
                if (r0 == 0) goto L_0x0a46
                org.telegram.messenger.Utilities.pinBitmap(r8)     // Catch:{ all -> 0x0a43 }
                goto L_0x0a46
            L_0x0a43:
                r29 = r10
                goto L_0x0a59
            L_0x0a46:
                r2 = r11
                r11 = r10
                goto L_0x0a5c
            L_0x0a49:
                r29 = r10
            L_0x0a4b:
                r11 = 0
                goto L_0x0a59
            L_0x0a4d:
                r0 = move-exception
                r6 = 0
            L_0x0a4f:
                monitor-exit(r2)     // Catch:{ all -> 0x0a51 }
                throw r0     // Catch:{ all -> 0x0a54 }
            L_0x0a51:
                r0 = move-exception
                goto L_0x0a4f
            L_0x0a53:
                r6 = 0
            L_0x0a54:
                r8 = r30
            L_0x0a56:
                r11 = 0
                r29 = 0
            L_0x0a59:
                r2 = r11
                r11 = r29
            L_0x0a5c:
                java.lang.Thread.interrupted()
                if (r2 != 0) goto L_0x0a6f
                if (r11 == 0) goto L_0x0a64
                goto L_0x0a6f
            L_0x0a64:
                if (r8 == 0) goto L_0x0a6b
                android.graphics.drawable.BitmapDrawable r6 = new android.graphics.drawable.BitmapDrawable
                r6.<init>(r8)
            L_0x0a6b:
                r1.onPostExecute(r6)
                goto L_0x0acf
            L_0x0a6f:
                if (r8 == 0) goto L_0x0a76
                org.telegram.messenger.ExtendedBitmapDrawable r6 = new org.telegram.messenger.ExtendedBitmapDrawable
                r6.<init>(r8, r2, r11)
            L_0x0a76:
                r1.onPostExecute(r6)
                goto L_0x0acf
            L_0x0a7a:
                r6 = 0
                r0 = 1135869952(0x43b40000, float:360.0)
                int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
                r2 = 1142947840(0x44200000, float:640.0)
                int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
                org.telegram.messenger.ImageLoader$CacheImage r3 = r1.cacheImage
                java.lang.String r3 = r3.filter
                if (r3 == 0) goto L_0x0aae
                java.lang.String r4 = "_"
                java.lang.String[] r3 = r3.split(r4)
                int r4 = r3.length
                r5 = 2
                if (r4 < r5) goto L_0x0aae
                r4 = 0
                r0 = r3[r4]
                float r0 = java.lang.Float.parseFloat(r0)
                r5 = 1
                r2 = r3[r5]
                float r2 = java.lang.Float.parseFloat(r2)
                float r3 = org.telegram.messenger.AndroidUtilities.density
                float r0 = r0 * r3
                int r0 = (int) r0
                float r2 = r2 * r3
                int r2 = (int) r2
                goto L_0x0ab0
            L_0x0aae:
                r4 = 0
                r5 = 1
            L_0x0ab0:
                org.telegram.messenger.ImageLoader$CacheImage r3 = r1.cacheImage     // Catch:{ all -> 0x0ac0 }
                java.io.File r8 = r3.finalFilePath     // Catch:{ all -> 0x0ac0 }
                int r3 = r3.imageType     // Catch:{ all -> 0x0ac0 }
                if (r3 != r7) goto L_0x0aba
                r10 = 1
                goto L_0x0abb
            L_0x0aba:
                r10 = 0
            L_0x0abb:
                android.graphics.Bitmap r0 = org.telegram.messenger.SvgHelper.getBitmap((java.io.File) r8, (int) r0, (int) r2, (boolean) r10)     // Catch:{ all -> 0x0ac0 }
                goto L_0x0ac5
            L_0x0ac0:
                r0 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
                r0 = r6
            L_0x0ac5:
                if (r0 == 0) goto L_0x0acc
                android.graphics.drawable.BitmapDrawable r6 = new android.graphics.drawable.BitmapDrawable
                r6.<init>(r0)
            L_0x0acc:
                r1.onPostExecute(r6)
            L_0x0acf:
                return
            L_0x0ad0:
                r0 = move-exception
                monitor-exit(r3)     // Catch:{ all -> 0x0ad0 }
                goto L_0x0ad4
            L_0x0ad3:
                throw r0
            L_0x0ad4:
                goto L_0x0ad3
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

        /* access modifiers changed from: private */
        /* JADX WARNING: Failed to insert additional move for type inference */
        /* renamed from: lambda$onPostExecute$1 */
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
                org.telegram.messenger.-$$Lambda$ImageLoader$CacheOutTask$leYRvNACHvS_ZM-ODvRFPqrAHgk r2 = new org.telegram.messenger.-$$Lambda$ImageLoader$CacheOutTask$leYRvNACHvS_ZM-ODvRFPqrAHgk
                r2.<init>(r4, r1)
                r0.postRunnable(r2)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ImageLoader.CacheOutTask.lambda$onPostExecute$1$ImageLoader$CacheOutTask(android.graphics.drawable.Drawable):void");
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$null$0 */
        public /* synthetic */ void lambda$null$0$ImageLoader$CacheOutTask(Drawable drawable, String str) {
            this.cacheImage.setImageAndClear(drawable, str);
        }

        /* JADX WARNING: Exception block dominator not found, dom blocks: [] */
        /* JADX WARNING: Missing exception handler attribute for start block: B:8:0x0010 */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void cancel() {
            /*
                r2 = this;
                java.lang.Object r0 = r2.sync
                monitor-enter(r0)
                r1 = 1
                r2.isCancelled = r1     // Catch:{ Exception -> 0x0010 }
                java.lang.Thread r1 = r2.runningThread     // Catch:{ Exception -> 0x0010 }
                if (r1 == 0) goto L_0x0010
                r1.interrupt()     // Catch:{ Exception -> 0x0010 }
                goto L_0x0010
            L_0x000e:
                r1 = move-exception
                goto L_0x0012
            L_0x0010:
                monitor-exit(r0)     // Catch:{ all -> 0x000e }
                return
            L_0x0012:
                monitor-exit(r0)     // Catch:{ all -> 0x000e }
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
                        FileLoader.getInstance(this.currentAccount).cancelLoadFile((TLRPC$FileLocation) this.imageLocation.location, this.ext);
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

        /* access modifiers changed from: private */
        /* renamed from: lambda$setImageAndClear$0 */
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
                public void fileUploadProgressChanged(FileUploadOperation fileUploadOperation, String str, long j, long j2, boolean z) {
                    FileUploadOperation fileUploadOperation2 = fileUploadOperation;
                    String str2 = str;
                    ImageLoader.this.fileProgresses.put(str, new long[]{j, j2});
                    long elapsedRealtime = SystemClock.elapsedRealtime();
                    long j3 = fileUploadOperation2.lastProgressUpdateTime;
                    if (j3 == 0 || j3 < elapsedRealtime - 100 || j == j2) {
                        fileUploadOperation2.lastProgressUpdateTime = elapsedRealtime;
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

                /* access modifiers changed from: private */
                /* renamed from: lambda$fileDidUploaded$2 */
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

                /* access modifiers changed from: private */
                /* renamed from: lambda$fileDidFailedUpload$4 */
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

                /* access modifiers changed from: private */
                /* renamed from: lambda$fileDidLoaded$5 */
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

                /* access modifiers changed from: private */
                /* renamed from: lambda$fileDidFailedLoad$6 */
                public /* synthetic */ void lambda$fileDidFailedLoad$6$ImageLoader$3(String str, int i, int i2) {
                    ImageLoader.this.fileDidFailedLoad(str, i);
                    NotificationCenter.getInstance(i2).postNotificationName(NotificationCenter.fileDidFailToLoad, str, Integer.valueOf(i));
                }

                public void fileLoadProgressChanged(FileLoadOperation fileLoadOperation, String str, long j, long j2) {
                    FileLoadOperation fileLoadOperation2 = fileLoadOperation;
                    String str2 = str;
                    ImageLoader.this.fileProgresses.put(str, new long[]{j, j2});
                    long elapsedRealtime = SystemClock.elapsedRealtime();
                    long j3 = fileLoadOperation2.lastProgressUpdateTime;
                    if (j3 == 0 || j3 < elapsedRealtime - 500 || j == 0) {
                        fileLoadOperation2.lastProgressUpdateTime = elapsedRealtime;
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
                $$Lambda$ImageLoader$4$r3N3jDWk7v1nvZU58OCVS6Esw8Y r3 = new Runnable() {
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

            /* access modifiers changed from: private */
            /* renamed from: lambda$onReceive$0 */
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$checkMediaPaths$1 */
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
        ArrayList<File> dataDirs;
        int i = Build.VERSION.SDK_INT;
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
                File externalStorageDirectory = Environment.getExternalStorageDirectory();
                if (i >= 19 && !TextUtils.isEmpty(SharedConfig.storageCacheDir)) {
                    ArrayList<File> rootDirs = AndroidUtilities.getRootDirs();
                    int size = rootDirs.size();
                    int i2 = 0;
                    while (true) {
                        if (i2 >= size) {
                            break;
                        }
                        File file = rootDirs.get(i2);
                        if (file.getAbsolutePath().startsWith(SharedConfig.storageCacheDir)) {
                            externalStorageDirectory = file;
                            break;
                        }
                        i2++;
                    }
                }
                File file2 = new File(externalStorageDirectory, "Telegram");
                this.telegramPath = file2;
                file2.mkdirs();
                if (i >= 19 && !this.telegramPath.isDirectory() && (dataDirs = AndroidUtilities.getDataDirs()) != null) {
                    int size2 = dataDirs.size();
                    int i3 = 0;
                    while (true) {
                        if (i3 >= size2) {
                            break;
                        }
                        File file3 = dataDirs.get(i3);
                        if (file3.getAbsolutePath().startsWith(SharedConfig.storageCacheDir)) {
                            File file4 = new File(file3, "Telegram");
                            this.telegramPath = file4;
                            file4.mkdirs();
                            break;
                        }
                        i3++;
                    }
                }
                if (this.telegramPath.isDirectory()) {
                    try {
                        File file5 = new File(this.telegramPath, "Telegram Images");
                        file5.mkdir();
                        if (file5.isDirectory() && canMoveFiles(cacheDir, file5, 0)) {
                            sparseArray.put(0, file5);
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.d("image path = " + file5);
                            }
                        }
                    } catch (Exception e2) {
                        FileLog.e((Throwable) e2);
                    }
                    try {
                        File file6 = new File(this.telegramPath, "Telegram Video");
                        file6.mkdir();
                        if (file6.isDirectory() && canMoveFiles(cacheDir, file6, 2)) {
                            sparseArray.put(2, file6);
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.d("video path = " + file6);
                            }
                        }
                    } catch (Exception e3) {
                        FileLog.e((Throwable) e3);
                    }
                    try {
                        File file7 = new File(this.telegramPath, "Telegram Audio");
                        file7.mkdir();
                        if (file7.isDirectory() && canMoveFiles(cacheDir, file7, 1)) {
                            AndroidUtilities.createEmptyFile(new File(file7, ".nomedia"));
                            sparseArray.put(1, file7);
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.d("audio path = " + file7);
                            }
                        }
                    } catch (Exception e4) {
                        FileLog.e((Throwable) e4);
                    }
                    try {
                        File file8 = new File(this.telegramPath, "Telegram Documents");
                        file8.mkdir();
                        if (file8.isDirectory() && canMoveFiles(cacheDir, file8, 3)) {
                            AndroidUtilities.createEmptyFile(new File(file8, ".nomedia"));
                            sparseArray.put(3, file8);
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.d("documents path = " + file8);
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
        if (str == null || (jArr = (long[]) this.fileProgresses.get(str)) == null) {
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
        return (long[]) this.fileProgresses.get(str);
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$cancelLoadingForImageReceiver$2 */
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
    public void lambda$replaceImageInCache$3(String str, String str2, ImageLocation imageLocation) {
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
            lambda$replaceImageInCache$3(str, str2, imageLocation);
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$cancelForceLoadingForImageReceiver$4 */
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
            $$Lambda$ImageLoader$RGwpR1Lc1hFIuevUsuImvar_SBjw r20 = r0;
            DispatchQueue dispatchQueue = this.imageLoadQueue;
            $$Lambda$ImageLoader$RGwpR1Lc1hFIuevUsuImvar_SBjw r0 = new Runnable(this, i4, str2, str, i8, imageReceiver, i5, str4, i3, imageLocation, z, parentObject, qulityThumbDocument, isNeedsQualityThumb, isShouldGenerateQualityThumb, i2, i, str3, currentAccount) {
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

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:67:0x0196, code lost:
        if (r8.exists() == false) goto L_0x0198;
     */
    /* JADX WARNING: Removed duplicated region for block: B:220:0x0488  */
    /* JADX WARNING: Removed duplicated region for block: B:221:0x0492  */
    /* JADX WARNING: Removed duplicated region for block: B:66:0x018d  */
    /* JADX WARNING: Removed duplicated region for block: B:70:0x019b  */
    /* JADX WARNING: Removed duplicated region for block: B:72:0x019f  */
    /* JADX WARNING: Removed duplicated region for block: B:82:0x01ee  */
    /* renamed from: lambda$createLoadOperationForImageReceiver$5 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$createLoadOperationForImageReceiver$5$ImageLoader(int r22, java.lang.String r23, java.lang.String r24, int r25, org.telegram.messenger.ImageReceiver r26, int r27, java.lang.String r28, int r29, org.telegram.messenger.ImageLocation r30, boolean r31, java.lang.Object r32, org.telegram.tgnet.TLRPC$Document r33, boolean r34, boolean r35, int r36, int r37, java.lang.String r38, int r39) {
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
            r9 = r27
            r5.setImageReceiverGuid(r11, r9)
            r17 = r3
            r16 = r4
            r6 = 1
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
            r6 = 1
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
            r6 = 0
        L_0x007d:
            if (r6 != 0) goto L_0x0091
            if (r16 == 0) goto L_0x0091
            r3 = r16
            r4 = r26
            r5 = r24
            r6 = r28
            r7 = r29
            r8 = r27
            r3.addImageReceiver(r4, r5, r6, r7, r8)
            r6 = 1
        L_0x0091:
            if (r6 != 0) goto L_0x00aa
            if (r17 == 0) goto L_0x00aa
            r3 = r17
            r4 = r26
            r5 = r24
            r6 = r28
            r7 = r29
            r8 = r27
            r3.addImageReceiver(r4, r5, r6, r7, r8)
            r6 = 1
            goto L_0x00aa
        L_0x00a6:
            r9 = 1
            r18 = 0
            r6 = 0
        L_0x00aa:
            if (r6 != 0) goto L_0x05f6
            java.lang.String r3 = r13.path
            java.lang.String r8 = "athumb"
            java.lang.String r4 = "_"
            r16 = 4
            if (r3 == 0) goto L_0x010f
            java.lang.String r7 = "http"
            boolean r7 = r3.startsWith(r7)
            if (r7 != 0) goto L_0x0106
            boolean r7 = r3.startsWith(r8)
            if (r7 != 0) goto L_0x0106
            java.lang.String r7 = "thumb://"
            boolean r7 = r3.startsWith(r7)
            java.lang.String r10 = ":"
            if (r7 == 0) goto L_0x00e3
            r7 = 8
            int r7 = r3.indexOf(r10, r7)
            if (r7 < 0) goto L_0x00e1
            java.io.File r10 = new java.io.File
            int r7 = r7 + r9
            java.lang.String r3 = r3.substring(r7)
            r10.<init>(r3)
            goto L_0x0104
        L_0x00e1:
            r10 = 0
            goto L_0x0104
        L_0x00e3:
            java.lang.String r7 = "vthumb://"
            boolean r7 = r3.startsWith(r7)
            if (r7 == 0) goto L_0x00ff
            r7 = 9
            int r7 = r3.indexOf(r10, r7)
            if (r7 < 0) goto L_0x00e1
            java.io.File r10 = new java.io.File
            int r7 = r7 + r9
            java.lang.String r3 = r3.substring(r7)
            r10.<init>(r3)
            goto L_0x0104
        L_0x00ff:
            java.io.File r10 = new java.io.File
            r10.<init>(r3)
        L_0x0104:
            r3 = 1
            goto L_0x0108
        L_0x0106:
            r3 = 0
            r10 = 0
        L_0x0108:
            r6 = r3
            r20 = r8
            r3 = 0
            r5 = 2
            goto L_0x01ff
        L_0x010f:
            if (r1 != 0) goto L_0x01f9
            if (r31 == 0) goto L_0x01f9
            boolean r3 = r14 instanceof org.telegram.messenger.MessageObject
            if (r3 == 0) goto L_0x0130
            r3 = r14
            org.telegram.messenger.MessageObject r3 = (org.telegram.messenger.MessageObject) r3
            org.telegram.tgnet.TLRPC$Document r7 = r3.getDocument()
            org.telegram.tgnet.TLRPC$Message r15 = r3.messageOwner
            java.lang.String r5 = r15.attachPath
            java.io.File r15 = org.telegram.messenger.FileLoader.getPathToMessage(r15)
            int r3 = r3.getMediaType()
            r19 = r15
            r15 = r7
            r7 = r3
            r3 = 0
            goto L_0x014b
        L_0x0130:
            if (r15 == 0) goto L_0x0145
            java.io.File r3 = org.telegram.messenger.FileLoader.getPathToAttach(r15, r9)
            boolean r5 = org.telegram.messenger.MessageObject.isVideoDocument(r33)
            if (r5 == 0) goto L_0x013e
            r5 = 2
            goto L_0x013f
        L_0x013e:
            r5 = 3
        L_0x013f:
            r19 = r3
            r7 = r5
            r3 = 1
            r5 = 0
            goto L_0x014b
        L_0x0145:
            r3 = 0
            r5 = 0
            r7 = 0
            r15 = 0
            r19 = 0
        L_0x014b:
            if (r15 == 0) goto L_0x01f3
            if (r34 == 0) goto L_0x0183
            java.io.File r9 = new java.io.File
            java.io.File r6 = org.telegram.messenger.FileLoader.getDirectory(r16)
            r20 = r8
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            java.lang.String r14 = "q_"
            r8.append(r14)
            int r14 = r15.dc_id
            r8.append(r14)
            r8.append(r4)
            long r13 = r15.id
            r8.append(r13)
            java.lang.String r13 = ".jpg"
            r8.append(r13)
            java.lang.String r8 = r8.toString()
            r9.<init>(r6, r8)
            boolean r6 = r9.exists()
            if (r6 != 0) goto L_0x0181
            goto L_0x0185
        L_0x0181:
            r6 = 1
            goto L_0x0187
        L_0x0183:
            r20 = r8
        L_0x0185:
            r6 = 0
            r9 = 0
        L_0x0187:
            boolean r8 = android.text.TextUtils.isEmpty(r5)
            if (r8 != 0) goto L_0x0198
            java.io.File r8 = new java.io.File
            r8.<init>(r5)
            boolean r5 = r8.exists()
            if (r5 != 0) goto L_0x0199
        L_0x0198:
            r8 = 0
        L_0x0199:
            if (r8 != 0) goto L_0x019d
            r8 = r19
        L_0x019d:
            if (r9 != 0) goto L_0x01ee
            java.lang.String r1 = org.telegram.messenger.FileLoader.getAttachFileName(r15)
            java.util.HashMap<java.lang.String, org.telegram.messenger.ImageLoader$ThumbGenerateInfo> r2 = r0.waitingForQualityThumb
            java.lang.Object r2 = r2.get(r1)
            org.telegram.messenger.ImageLoader$ThumbGenerateInfo r2 = (org.telegram.messenger.ImageLoader.ThumbGenerateInfo) r2
            if (r2 != 0) goto L_0x01c1
            org.telegram.messenger.ImageLoader$ThumbGenerateInfo r2 = new org.telegram.messenger.ImageLoader$ThumbGenerateInfo
            r4 = 0
            r2.<init>()
            org.telegram.tgnet.TLRPC$Document unused = r2.parentDocument = r15
            java.lang.String unused = r2.filter = r12
            boolean unused = r2.big = r3
            java.util.HashMap<java.lang.String, org.telegram.messenger.ImageLoader$ThumbGenerateInfo> r3 = r0.waitingForQualityThumb
            r3.put(r1, r2)
        L_0x01c1:
            java.util.ArrayList r3 = r2.imageReceiverArray
            boolean r3 = r3.contains(r11)
            if (r3 != 0) goto L_0x01dd
            java.util.ArrayList r3 = r2.imageReceiverArray
            r3.add(r11)
            java.util.ArrayList r3 = r2.imageReceiverGuidsArray
            java.lang.Integer r4 = java.lang.Integer.valueOf(r27)
            r3.add(r4)
        L_0x01dd:
            android.util.SparseArray<java.lang.String> r3 = r0.waitingForQualityThumbByTag
            r3.put(r10, r1)
            boolean r1 = r8.exists()
            if (r1 == 0) goto L_0x01ed
            if (r35 == 0) goto L_0x01ed
            r0.generateThumb(r7, r8, r2)
        L_0x01ed:
            return
        L_0x01ee:
            r3 = r6
            r10 = r9
            r5 = 2
            r6 = 1
            goto L_0x01ff
        L_0x01f3:
            r20 = r8
            r3 = 0
            r5 = 2
            r6 = 1
            goto L_0x01fe
        L_0x01f9:
            r20 = r8
            r3 = 0
            r5 = 2
            r6 = 0
        L_0x01fe:
            r10 = 0
        L_0x01ff:
            if (r1 == r5) goto L_0x05f6
            boolean r7 = r30.isEncrypted()
            org.telegram.messenger.ImageLoader$CacheImage r9 = new org.telegram.messenger.ImageLoader$CacheImage
            r8 = 0
            r9.<init>()
            r13 = r30
            if (r31 != 0) goto L_0x025e
            int r8 = r13.imageType
            if (r8 == r5) goto L_0x025c
            org.telegram.messenger.WebFile r5 = r13.webFile
            boolean r5 = org.telegram.messenger.MessageObject.isGifDocument((org.telegram.messenger.WebFile) r5)
            if (r5 != 0) goto L_0x025b
            org.telegram.tgnet.TLRPC$Document r5 = r13.document
            boolean r5 = org.telegram.messenger.MessageObject.isGifDocument((org.telegram.tgnet.TLRPC$Document) r5)
            if (r5 != 0) goto L_0x025b
            org.telegram.tgnet.TLRPC$Document r5 = r13.document
            boolean r5 = org.telegram.messenger.MessageObject.isRoundVideoDocument(r5)
            if (r5 == 0) goto L_0x022c
            goto L_0x025b
        L_0x022c:
            java.lang.String r5 = r13.path
            if (r5 == 0) goto L_0x025e
            java.lang.String r8 = "vthumb"
            boolean r8 = r5.startsWith(r8)
            if (r8 != 0) goto L_0x025e
            java.lang.String r8 = "thumb"
            boolean r8 = r5.startsWith(r8)
            if (r8 != 0) goto L_0x025e
            java.lang.String r8 = "jpg"
            java.lang.String r5 = getHttpUrlExtension(r5, r8)
            java.lang.String r8 = "mp4"
            boolean r8 = r5.equals(r8)
            if (r8 != 0) goto L_0x0257
            java.lang.String r8 = "gif"
            boolean r5 = r5.equals(r8)
            if (r5 == 0) goto L_0x025e
        L_0x0257:
            r5 = 2
            r9.imageType = r5
            goto L_0x025e
        L_0x025b:
            r5 = 2
        L_0x025c:
            r9.imageType = r5
        L_0x025e:
            if (r10 != 0) goto L_0x0498
            org.telegram.tgnet.TLRPC$PhotoSize r5 = r13.photoSize
            boolean r8 = r5 instanceof org.telegram.tgnet.TLRPC$TL_photoStrippedSize
            java.lang.String r14 = "g"
            if (r8 != 0) goto L_0x0478
            boolean r5 = r5 instanceof org.telegram.tgnet.TLRPC$TL_photoPathSize
            if (r5 == 0) goto L_0x026e
            goto L_0x0478
        L_0x026e:
            org.telegram.messenger.SecureDocument r5 = r13.secureDocument
            if (r5 == 0) goto L_0x0290
            r9.secureDocument = r5
            org.telegram.tgnet.TLRPC$TL_secureFile r4 = r5.secureFile
            int r4 = r4.dc_id
            r5 = -2147483648(0xfffffffvar_, float:-0.0)
            if (r4 != r5) goto L_0x027e
            r6 = 1
            goto L_0x027f
        L_0x027e:
            r6 = 0
        L_0x027f:
            java.io.File r4 = new java.io.File
            java.io.File r5 = org.telegram.messenger.FileLoader.getDirectory(r16)
            r4.<init>(r5, r2)
            r0 = r36
            r10 = r4
            r4 = r6
            r7 = r12
        L_0x028d:
            r1 = 1
            goto L_0x0481
        L_0x0290:
            boolean r5 = r14.equals(r12)
            java.lang.String r8 = ".svg"
            java.lang.String r10 = "application/x-tgwallpattern"
            java.lang.String r15 = "application/x-tgsticker"
            r33 = r3
            java.lang.String r3 = "application/x-tgsdice"
            if (r5 != 0) goto L_0x0349
            r5 = r36
            r11 = r37
            r34 = r6
            if (r5 != 0) goto L_0x02b0
            if (r11 <= 0) goto L_0x02b0
            java.lang.String r6 = r13.path
            if (r6 != 0) goto L_0x02b0
            if (r7 == 0) goto L_0x034f
        L_0x02b0:
            java.io.File r4 = new java.io.File
            java.io.File r6 = org.telegram.messenger.FileLoader.getDirectory(r16)
            r4.<init>(r6, r2)
            boolean r6 = r4.exists()
            if (r6 == 0) goto L_0x02c1
            r1 = 1
            goto L_0x02e0
        L_0x02c1:
            r6 = 2
            if (r5 != r6) goto L_0x02de
            java.io.File r4 = new java.io.File
            java.io.File r6 = org.telegram.messenger.FileLoader.getDirectory(r16)
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            r7.append(r2)
            java.lang.String r1 = ".enc"
            r7.append(r1)
            java.lang.String r1 = r7.toString()
            r4.<init>(r6, r1)
        L_0x02de:
            r1 = r33
        L_0x02e0:
            org.telegram.tgnet.TLRPC$Document r6 = r13.document
            if (r6 == 0) goto L_0x033f
            boolean r7 = r6 instanceof org.telegram.messenger.DocumentObject.ThemeDocument
            if (r7 == 0) goto L_0x02fe
            org.telegram.messenger.DocumentObject$ThemeDocument r6 = (org.telegram.messenger.DocumentObject.ThemeDocument) r6
            org.telegram.tgnet.TLRPC$Document r3 = r6.wallpaper
            if (r3 != 0) goto L_0x02f0
            r3 = 1
            goto L_0x02f2
        L_0x02f0:
            r3 = r34
        L_0x02f2:
            r6 = 5
            r9.imageType = r6
            r10 = r4
            r0 = r5
            r7 = r12
            r6 = 0
            r4 = r3
            r3 = r1
            r1 = 1
            goto L_0x0482
        L_0x02fe:
            java.lang.String r6 = r6.mime_type
            boolean r3 = r3.equals(r6)
            if (r3 == 0) goto L_0x0310
            r3 = 1
            r9.imageType = r3
            r3 = r1
            r10 = r4
            r0 = r5
            r7 = r12
            r1 = 1
            goto L_0x0480
        L_0x0310:
            r3 = 1
            org.telegram.tgnet.TLRPC$Document r6 = r13.document
            java.lang.String r6 = r6.mime_type
            boolean r6 = r15.equals(r6)
            if (r6 == 0) goto L_0x031e
            r9.imageType = r3
            goto L_0x033f
        L_0x031e:
            org.telegram.tgnet.TLRPC$Document r3 = r13.document
            java.lang.String r3 = r3.mime_type
            boolean r3 = r10.equals(r3)
            if (r3 == 0) goto L_0x032c
            r3 = 3
            r9.imageType = r3
            goto L_0x033f
        L_0x032c:
            r3 = 3
            boolean r6 = org.telegram.messenger.BuildVars.DEBUG_PRIVATE_VERSION
            if (r6 == 0) goto L_0x033f
            org.telegram.tgnet.TLRPC$Document r6 = r13.document
            java.lang.String r6 = org.telegram.messenger.FileLoader.getDocumentFileName(r6)
            boolean r6 = r6.endsWith(r8)
            if (r6 == 0) goto L_0x033f
            r9.imageType = r3
        L_0x033f:
            r3 = r1
            r10 = r4
            r0 = r5
            r7 = r12
            r1 = 1
            r6 = 0
            r4 = r34
            goto L_0x0482
        L_0x0349:
            r5 = r36
            r11 = r37
            r34 = r6
        L_0x034f:
            org.telegram.tgnet.TLRPC$Document r1 = r13.document
            java.lang.String r6 = ".temp"
            if (r1 == 0) goto L_0x0408
            boolean r7 = r1 instanceof org.telegram.tgnet.TLRPC$TL_documentEncrypted
            if (r7 == 0) goto L_0x0363
            java.io.File r7 = new java.io.File
            java.io.File r11 = org.telegram.messenger.FileLoader.getDirectory(r16)
            r7.<init>(r11, r2)
            goto L_0x037e
        L_0x0363:
            boolean r7 = org.telegram.messenger.MessageObject.isVideoDocument(r1)
            if (r7 == 0) goto L_0x0374
            java.io.File r7 = new java.io.File
            r11 = 2
            java.io.File r0 = org.telegram.messenger.FileLoader.getDirectory(r11)
            r7.<init>(r0, r2)
            goto L_0x037e
        L_0x0374:
            java.io.File r7 = new java.io.File
            r0 = 3
            java.io.File r11 = org.telegram.messenger.FileLoader.getDirectory(r0)
            r7.<init>(r11, r2)
        L_0x037e:
            boolean r0 = r14.equals(r12)
            if (r0 == 0) goto L_0x03ac
            boolean r0 = r7.exists()
            if (r0 != 0) goto L_0x03ac
            java.io.File r7 = new java.io.File
            java.io.File r0 = org.telegram.messenger.FileLoader.getDirectory(r16)
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            int r12 = r1.dc_id
            r11.append(r12)
            r11.append(r4)
            long r4 = r1.id
            r11.append(r4)
            r11.append(r6)
            java.lang.String r4 = r11.toString()
            r7.<init>(r0, r4)
        L_0x03ac:
            boolean r0 = r1 instanceof org.telegram.messenger.DocumentObject.ThemeDocument
            if (r0 == 0) goto L_0x03c0
            r0 = r1
            org.telegram.messenger.DocumentObject$ThemeDocument r0 = (org.telegram.messenger.DocumentObject.ThemeDocument) r0
            org.telegram.tgnet.TLRPC$Document r0 = r0.wallpaper
            if (r0 != 0) goto L_0x03ba
            r0 = 5
            r4 = 1
            goto L_0x03bd
        L_0x03ba:
            r4 = r34
            r0 = 5
        L_0x03bd:
            r9.imageType = r0
            goto L_0x03fc
        L_0x03c0:
            org.telegram.tgnet.TLRPC$Document r0 = r13.document
            java.lang.String r0 = r0.mime_type
            boolean r0 = r3.equals(r0)
            if (r0 == 0) goto L_0x03cf
            r0 = 1
            r9.imageType = r0
            r4 = 1
            goto L_0x03fc
        L_0x03cf:
            r0 = 1
            java.lang.String r3 = r1.mime_type
            boolean r3 = r15.equals(r3)
            if (r3 == 0) goto L_0x03db
            r9.imageType = r0
            goto L_0x03fa
        L_0x03db:
            java.lang.String r0 = r1.mime_type
            boolean r0 = r10.equals(r0)
            if (r0 == 0) goto L_0x03e7
            r0 = 3
            r9.imageType = r0
            goto L_0x03fa
        L_0x03e7:
            r0 = 3
            boolean r3 = org.telegram.messenger.BuildVars.DEBUG_PRIVATE_VERSION
            if (r3 == 0) goto L_0x03fa
            org.telegram.tgnet.TLRPC$Document r3 = r13.document
            java.lang.String r3 = org.telegram.messenger.FileLoader.getDocumentFileName(r3)
            boolean r3 = r3.endsWith(r8)
            if (r3 == 0) goto L_0x03fa
            r9.imageType = r0
        L_0x03fa:
            r4 = r34
        L_0x03fc:
            int r6 = r1.size
            r3 = r33
            r0 = r36
            r10 = r7
            r1 = 1
            r7 = r28
            goto L_0x0482
        L_0x0408:
            r0 = 3
            org.telegram.messenger.WebFile r1 = r13.webFile
            if (r1 == 0) goto L_0x0421
            java.io.File r1 = new java.io.File
            java.io.File r0 = org.telegram.messenger.FileLoader.getDirectory(r0)
            r1.<init>(r0, r2)
            r7 = r28
            r3 = r33
            r4 = r34
            r0 = r36
            r10 = r1
            goto L_0x028d
        L_0x0421:
            r0 = r36
            r1 = 1
            if (r0 != r1) goto L_0x0430
            java.io.File r3 = new java.io.File
            java.io.File r5 = org.telegram.messenger.FileLoader.getDirectory(r16)
            r3.<init>(r5, r2)
            goto L_0x0439
        L_0x0430:
            java.io.File r3 = new java.io.File
            java.io.File r5 = org.telegram.messenger.FileLoader.getDirectory(r18)
            r3.<init>(r5, r2)
        L_0x0439:
            r7 = r28
            boolean r5 = r14.equals(r7)
            if (r5 == 0) goto L_0x0471
            org.telegram.tgnet.TLRPC$TL_fileLocationToBeDeprecated r5 = r13.location
            if (r5 == 0) goto L_0x0471
            boolean r5 = r3.exists()
            if (r5 != 0) goto L_0x0471
            java.io.File r3 = new java.io.File
            java.io.File r5 = org.telegram.messenger.FileLoader.getDirectory(r16)
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            org.telegram.tgnet.TLRPC$TL_fileLocationToBeDeprecated r10 = r13.location
            long r10 = r10.volume_id
            r8.append(r10)
            r8.append(r4)
            org.telegram.tgnet.TLRPC$TL_fileLocationToBeDeprecated r4 = r13.location
            int r4 = r4.local_id
            r8.append(r4)
            r8.append(r6)
            java.lang.String r4 = r8.toString()
            r3.<init>(r5, r4)
        L_0x0471:
            r4 = r34
            r10 = r3
            r6 = 0
            r3 = r33
            goto L_0x0482
        L_0x0478:
            r0 = r36
            r33 = r3
            r7 = r12
            r1 = 1
            r3 = r33
        L_0x0480:
            r4 = 1
        L_0x0481:
            r6 = 0
        L_0x0482:
            boolean r5 = r14.equals(r7)
            if (r5 == 0) goto L_0x0492
            r5 = 2
            r9.imageType = r5
            r9.size = r6
            r8 = r29
            r12 = r10
            r11 = 1
            goto L_0x0496
        L_0x0492:
            r8 = r29
            r11 = r4
            r12 = r10
        L_0x0496:
            r10 = r3
            goto L_0x04a7
        L_0x0498:
            r0 = r36
            r33 = r3
            r34 = r6
            r7 = r12
            r1 = 1
            r8 = r29
            r11 = r34
            r12 = r10
            r10 = r33
        L_0x04a7:
            r9.type = r8
            r14 = r24
            r9.key = r14
            r9.filter = r7
            r9.imageLocation = r13
            r15 = r38
            r9.ext = r15
            r6 = r39
            r9.currentAccount = r6
            r5 = r32
            r9.parentObject = r5
            int r3 = r13.imageType
            if (r3 == 0) goto L_0x04c3
            r9.imageType = r3
        L_0x04c3:
            r4 = 2
            if (r0 != r4) goto L_0x04e2
            java.io.File r3 = new java.io.File
            java.io.File r1 = org.telegram.messenger.FileLoader.getInternalCacheDir()
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r2)
            java.lang.String r0 = ".enc.key"
            r4.append(r0)
            java.lang.String r0 = r4.toString()
            r3.<init>(r1, r0)
            r9.encryptionKeyPath = r3
        L_0x04e2:
            r3 = r9
            r0 = 2
            r4 = r26
            r0 = r36
            r1 = r5
            r5 = r24
            r6 = r28
            r7 = r29
            r1 = r20
            r8 = r27
            r3.addImageReceiver(r4, r5, r6, r7, r8)
            if (r11 != 0) goto L_0x05d2
            if (r10 != 0) goto L_0x05d2
            boolean r3 = r12.exists()
            if (r3 == 0) goto L_0x0502
            goto L_0x05d2
        L_0x0502:
            r9.url = r2
            r7 = r21
            java.util.HashMap<java.lang.String, org.telegram.messenger.ImageLoader$CacheImage> r3 = r7.imageLoadingByUrl
            r3.put(r2, r9)
            java.lang.String r2 = r13.path
            if (r2 == 0) goto L_0x055f
            java.lang.String r0 = org.telegram.messenger.Utilities.MD5(r2)
            java.io.File r2 = org.telegram.messenger.FileLoader.getDirectory(r16)
            java.io.File r3 = new java.io.File
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r0)
            java.lang.String r0 = "_temp.jpg"
            r4.append(r0)
            java.lang.String r0 = r4.toString()
            r3.<init>(r2, r0)
            r9.tempFilePath = r3
            r9.finalFilePath = r12
            java.lang.String r0 = r13.path
            boolean r0 = r0.startsWith(r1)
            if (r0 == 0) goto L_0x054b
            org.telegram.messenger.ImageLoader$ArtworkLoadTask r0 = new org.telegram.messenger.ImageLoader$ArtworkLoadTask
            r0.<init>(r9)
            r9.artworkTask = r0
            java.util.LinkedList<org.telegram.messenger.ImageLoader$ArtworkLoadTask> r1 = r7.artworkTasks
            r1.add(r0)
            r8 = 0
            r7.runArtworkTasks(r8)
            goto L_0x05f7
        L_0x054b:
            r8 = 0
            org.telegram.messenger.ImageLoader$HttpImageTask r0 = new org.telegram.messenger.ImageLoader$HttpImageTask
            r1 = r37
            r0.<init>(r9, r1)
            r9.httpTask = r0
            java.util.LinkedList<org.telegram.messenger.ImageLoader$HttpImageTask> r1 = r7.httpTasks
            r1.add(r0)
            r7.runHttpTasks(r8)
            goto L_0x05f7
        L_0x055f:
            r1 = r37
            r8 = 0
            org.telegram.tgnet.TLRPC$TL_fileLocationToBeDeprecated r2 = r13.location
            if (r2 == 0) goto L_0x0586
            if (r0 != 0) goto L_0x0570
            if (r1 <= 0) goto L_0x056e
            byte[] r1 = r13.key
            if (r1 == 0) goto L_0x0570
        L_0x056e:
            r6 = 1
            goto L_0x0571
        L_0x0570:
            r6 = r0
        L_0x0571:
            org.telegram.messenger.FileLoader r1 = org.telegram.messenger.FileLoader.getInstance(r39)
            r3 = r32
            if (r22 == 0) goto L_0x057b
            r5 = 2
            goto L_0x057c
        L_0x057b:
            r5 = 1
        L_0x057c:
            r2 = r30
            r3 = r32
            r4 = r38
            r1.loadFile(r2, r3, r4, r5, r6)
            goto L_0x05c0
        L_0x0586:
            r3 = r32
            org.telegram.tgnet.TLRPC$Document r1 = r13.document
            if (r1 == 0) goto L_0x059b
            org.telegram.messenger.FileLoader r1 = org.telegram.messenger.FileLoader.getInstance(r39)
            org.telegram.tgnet.TLRPC$Document r2 = r13.document
            if (r22 == 0) goto L_0x0596
            r5 = 2
            goto L_0x0597
        L_0x0596:
            r5 = 1
        L_0x0597:
            r1.loadFile(r2, r3, r5, r0)
            goto L_0x05c0
        L_0x059b:
            org.telegram.messenger.SecureDocument r1 = r13.secureDocument
            if (r1 == 0) goto L_0x05ae
            org.telegram.messenger.FileLoader r0 = org.telegram.messenger.FileLoader.getInstance(r39)
            org.telegram.messenger.SecureDocument r1 = r13.secureDocument
            if (r22 == 0) goto L_0x05a9
            r5 = 2
            goto L_0x05aa
        L_0x05a9:
            r5 = 1
        L_0x05aa:
            r0.loadFile(r1, r5)
            goto L_0x05c0
        L_0x05ae:
            org.telegram.messenger.WebFile r1 = r13.webFile
            if (r1 == 0) goto L_0x05c0
            org.telegram.messenger.FileLoader r1 = org.telegram.messenger.FileLoader.getInstance(r39)
            org.telegram.messenger.WebFile r2 = r13.webFile
            if (r22 == 0) goto L_0x05bc
            r5 = 2
            goto L_0x05bd
        L_0x05bc:
            r5 = 1
        L_0x05bd:
            r1.loadFile(r2, r5, r0)
        L_0x05c0:
            boolean r0 = r26.isForceLoding()
            if (r0 == 0) goto L_0x05f7
            java.util.HashMap<java.lang.String, java.lang.Integer> r0 = r7.forceLoadingImages
            java.lang.String r1 = r9.key
            java.lang.Integer r2 = java.lang.Integer.valueOf(r8)
            r0.put(r1, r2)
            goto L_0x05f7
        L_0x05d2:
            r7 = r21
            r9.finalFilePath = r12
            r9.imageLocation = r13
            org.telegram.messenger.ImageLoader$CacheOutTask r0 = new org.telegram.messenger.ImageLoader$CacheOutTask
            r0.<init>(r9)
            r9.cacheTask = r0
            java.util.HashMap<java.lang.String, org.telegram.messenger.ImageLoader$CacheImage> r0 = r7.imageLoadingByKeys
            r0.put(r14, r9)
            if (r22 == 0) goto L_0x05ee
            org.telegram.messenger.DispatchQueue r0 = r7.cacheThumbOutQueue
            org.telegram.messenger.ImageLoader$CacheOutTask r1 = r9.cacheTask
            r0.postRunnable(r1)
            goto L_0x05f7
        L_0x05ee:
            org.telegram.messenger.DispatchQueue r0 = r7.cacheOutQueue
            org.telegram.messenger.ImageLoader$CacheOutTask r1 = r9.cacheTask
            r0.postRunnable(r1)
            goto L_0x05f7
        L_0x05f6:
            r7 = r0
        L_0x05f7:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ImageLoader.lambda$createLoadOperationForImageReceiver$5$ImageLoader(int, java.lang.String, java.lang.String, int, org.telegram.messenger.ImageReceiver, int, java.lang.String, int, org.telegram.messenger.ImageLocation, boolean, java.lang.Object, org.telegram.tgnet.TLRPC$Document, boolean, boolean, int, int, java.lang.String, int):void");
    }

    public void preloadArtwork(String str) {
        this.imageLoadQueue.postRunnable(new Runnable(str) {
            public final /* synthetic */ String f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                ImageLoader.this.lambda$preloadArtwork$6$ImageLoader(this.f$1);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$preloadArtwork$6 */
    public /* synthetic */ void lambda$preloadArtwork$6$ImageLoader(String str) {
        String httpUrlExtension = getHttpUrlExtension(str, "jpg");
        String str2 = Utilities.MD5(str) + "." + httpUrlExtension;
        File file = new File(FileLoader.getDirectory(4), str2);
        if (!file.exists()) {
            ImageLocation forPath = ImageLocation.getForPath(str);
            CacheImage cacheImage = new CacheImage();
            cacheImage.type = 1;
            cacheImage.key = Utilities.MD5(str);
            cacheImage.filter = null;
            cacheImage.imageLocation = forPath;
            cacheImage.ext = httpUrlExtension;
            cacheImage.parentObject = null;
            int i = forPath.imageType;
            if (i != 0) {
                cacheImage.imageType = i;
            }
            cacheImage.url = str2;
            this.imageLoadingByUrl.put(str2, cacheImage);
            String MD5 = Utilities.MD5(forPath.path);
            cacheImage.tempFilePath = new File(FileLoader.getDirectory(4), MD5 + "_temp.jpg");
            cacheImage.finalFilePath = file;
            ArtworkLoadTask artworkLoadTask = new ArtworkLoadTask(cacheImage);
            cacheImage.artworkTask = artworkLoadTask;
            this.artworkTasks.add(artworkLoadTask);
            runArtworkTasks(false);
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:131:0x0212, code lost:
        if (r6.local_id >= 0) goto L_0x02f8;
     */
    /* JADX WARNING: Removed duplicated region for block: B:181:0x032d  */
    /* JADX WARNING: Removed duplicated region for block: B:198:0x039b  */
    /* JADX WARNING: Removed duplicated region for block: B:205:0x03b9 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:209:0x03d5 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:214:0x03f1  */
    /* JADX WARNING: Removed duplicated region for block: B:222:0x0430  */
    /* JADX WARNING: Removed duplicated region for block: B:224:0x0437  */
    /* JADX WARNING: Removed duplicated region for block: B:242:0x0497  */
    /* JADX WARNING: Removed duplicated region for block: B:255:0x0321 A[EDGE_INSN: B:255:0x0321->B:179:0x0321 ?: BREAK  , SYNTHETIC] */
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
            if (r3 >= r15) goto L_0x0321
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
            if (r0 != 0) goto L_0x01a9
        L_0x01a1:
            r31 = r6
            r32 = r10
        L_0x01a5:
            r7 = r30
            goto L_0x0313
        L_0x01a9:
            r31 = r6
            if (r24 == 0) goto L_0x01b0
            r7 = r24
            goto L_0x01b2
        L_0x01b0:
            r7 = r30
        L_0x01b2:
            r6 = 1
            java.lang.String r7 = r15.getKey(r1, r7, r6)
            java.lang.String r6 = r15.path
            if (r6 == 0) goto L_0x01d7
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            r6.append(r7)
            r6.append(r13)
            java.lang.String r7 = r15.path
            java.lang.String r7 = getHttpUrlExtension(r7, r8)
            r6.append(r7)
            java.lang.String r7 = r6.toString()
            r32 = r10
            goto L_0x02f8
        L_0x01d7:
            org.telegram.tgnet.TLRPC$PhotoSize r6 = r15.photoSize
            r32 = r10
            boolean r10 = r6 instanceof org.telegram.tgnet.TLRPC$TL_photoStrippedSize
            if (r10 != 0) goto L_0x02e6
            boolean r6 = r6 instanceof org.telegram.tgnet.TLRPC$TL_photoPathSize
            if (r6 == 0) goto L_0x01e5
            goto L_0x02e6
        L_0x01e5:
            org.telegram.tgnet.TLRPC$TL_fileLocationToBeDeprecated r6 = r15.location
            if (r6 == 0) goto L_0x0218
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            r6.append(r7)
            r6.append(r13)
            r6.append(r14)
            java.lang.String r7 = r6.toString()
            java.lang.String r6 = r37.getExt()
            if (r6 != 0) goto L_0x0214
            org.telegram.tgnet.TLRPC$TL_fileLocationToBeDeprecated r6 = r15.location
            byte[] r10 = r6.key
            if (r10 != 0) goto L_0x0214
            long r13 = r6.volume_id
            r33 = -2147483648(0xfffffffvar_, double:NaN)
            int r10 = (r13 > r33 ? 1 : (r13 == r33 ? 0 : -1))
            if (r10 != 0) goto L_0x02f8
            int r6 = r6.local_id
            if (r6 >= 0) goto L_0x02f8
        L_0x0214:
            r26 = 1
            goto L_0x02f8
        L_0x0218:
            org.telegram.messenger.WebFile r6 = r15.webFile
            if (r6 == 0) goto L_0x023e
            java.lang.String r6 = r6.mime_type
            java.lang.String r6 = org.telegram.messenger.FileLoader.getMimeTypePart(r6)
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            r10.append(r7)
            r10.append(r13)
            org.telegram.messenger.WebFile r7 = r15.webFile
            java.lang.String r7 = r7.url
            java.lang.String r6 = getHttpUrlExtension(r7, r6)
            r10.append(r6)
            java.lang.String r7 = r10.toString()
            goto L_0x02f8
        L_0x023e:
            org.telegram.messenger.SecureDocument r6 = r15.secureDocument
            if (r6 == 0) goto L_0x0256
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            r6.append(r7)
            r6.append(r13)
            r6.append(r14)
            java.lang.String r7 = r6.toString()
            goto L_0x02f8
        L_0x0256:
            org.telegram.tgnet.TLRPC$Document r6 = r15.document
            if (r6 == 0) goto L_0x02f8
            if (r3 != 0) goto L_0x026f
            if (r9 == 0) goto L_0x026f
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            java.lang.String r10 = "q_"
            r6.append(r10)
            r6.append(r0)
            java.lang.String r0 = r6.toString()
        L_0x026f:
            org.telegram.tgnet.TLRPC$Document r6 = r15.document
            java.lang.String r6 = org.telegram.messenger.FileLoader.getDocumentFileName(r6)
            java.lang.String r10 = ""
            if (r6 == 0) goto L_0x0288
            r13 = 46
            int r13 = r6.lastIndexOf(r13)
            r14 = -1
            if (r13 != r14) goto L_0x0283
            goto L_0x0288
        L_0x0283:
            java.lang.String r6 = r6.substring(r13)
            goto L_0x0289
        L_0x0288:
            r6 = r10
        L_0x0289:
            int r13 = r6.length()
            r14 = 1
            if (r13 > r14) goto L_0x02b0
            org.telegram.tgnet.TLRPC$Document r6 = r15.document
            java.lang.String r6 = r6.mime_type
            java.lang.String r13 = "video/mp4"
            boolean r6 = r13.equals(r6)
            if (r6 == 0) goto L_0x02a0
            java.lang.String r10 = ".mp4"
            goto L_0x02b1
        L_0x02a0:
            org.telegram.tgnet.TLRPC$Document r6 = r15.document
            java.lang.String r6 = r6.mime_type
            java.lang.String r13 = "video/x-matroska"
            boolean r6 = r13.equals(r6)
            if (r6 == 0) goto L_0x02b1
            java.lang.String r10 = ".mkv"
            goto L_0x02b1
        L_0x02b0:
            r10 = r6
        L_0x02b1:
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            r6.append(r7)
            r6.append(r10)
            java.lang.String r7 = r6.toString()
            org.telegram.tgnet.TLRPC$Document r6 = r15.document
            boolean r6 = org.telegram.messenger.MessageObject.isVideoDocument(r6)
            if (r6 != 0) goto L_0x02e2
            org.telegram.tgnet.TLRPC$Document r6 = r15.document
            boolean r6 = org.telegram.messenger.MessageObject.isGifDocument((org.telegram.tgnet.TLRPC$Document) r6)
            if (r6 != 0) goto L_0x02e2
            org.telegram.tgnet.TLRPC$Document r6 = r15.document
            boolean r6 = org.telegram.messenger.MessageObject.isRoundVideoDocument(r6)
            if (r6 != 0) goto L_0x02e2
            org.telegram.tgnet.TLRPC$Document r6 = r15.document
            boolean r6 = org.telegram.messenger.MessageObject.canPreviewDocument(r6)
            if (r6 != 0) goto L_0x02e2
            r6 = 1
            goto L_0x02e3
        L_0x02e2:
            r6 = 0
        L_0x02e3:
            r26 = r6
            goto L_0x02f8
        L_0x02e6:
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            r6.append(r7)
            r6.append(r13)
            r6.append(r14)
            java.lang.String r7 = r6.toString()
        L_0x02f8:
            if (r3 != 0) goto L_0x02fe
            r12 = r0
            r20 = r7
            goto L_0x0301
        L_0x02fe:
            r4 = r0
            r23 = r7
        L_0x0301:
            if (r15 != r5) goto L_0x01a5
            if (r3 != 0) goto L_0x030b
            r7 = r18
            r12 = r7
            r20 = r12
            goto L_0x0313
        L_0x030b:
            r4 = r18
            r23 = r4
            r24 = r23
            goto L_0x01a5
        L_0x0313:
            int r3 = r3 + 1
            r15 = r27
            r14 = r28
            r0 = r29
            r6 = r31
            r10 = r32
            goto L_0x0172
        L_0x0321:
            r29 = r0
            r31 = r6
            r30 = r7
            r32 = r10
            r28 = r14
            if (r5 == 0) goto L_0x039b
            org.telegram.messenger.ImageLocation r0 = r37.getStrippedLocation()
            if (r0 != 0) goto L_0x0339
            if (r24 == 0) goto L_0x0337
            r25 = r24
        L_0x0337:
            r0 = r25
        L_0x0339:
            r3 = 0
            java.lang.String r18 = r5.getKey(r1, r0, r3)
            r3 = 1
            java.lang.String r0 = r5.getKey(r1, r0, r3)
            java.lang.String r1 = r5.path
            if (r1 == 0) goto L_0x0366
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r0)
            r1.append(r13)
            java.lang.String r0 = r5.path
            java.lang.String r0 = getHttpUrlExtension(r0, r8)
            r1.append(r0)
            java.lang.String r0 = r1.toString()
        L_0x035f:
            r35 = r18
            r18 = r0
            r0 = r35
            goto L_0x039e
        L_0x0366:
            org.telegram.tgnet.TLRPC$PhotoSize r1 = r5.photoSize
            boolean r6 = r1 instanceof org.telegram.tgnet.TLRPC$TL_photoStrippedSize
            if (r6 != 0) goto L_0x0388
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC$TL_photoPathSize
            if (r1 == 0) goto L_0x0371
            goto L_0x0388
        L_0x0371:
            org.telegram.tgnet.TLRPC$TL_fileLocationToBeDeprecated r1 = r5.location
            if (r1 == 0) goto L_0x035f
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r0)
            r1.append(r13)
            r1.append(r2)
            java.lang.String r0 = r1.toString()
            goto L_0x035f
        L_0x0388:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r0)
            r1.append(r13)
            r1.append(r2)
            java.lang.String r0 = r1.toString()
            goto L_0x035f
        L_0x039b:
            r3 = 1
            r0 = r18
        L_0x039e:
            java.lang.String r1 = "@"
            if (r4 == 0) goto L_0x03b6
            if (r11 == 0) goto L_0x03b6
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            r6.append(r4)
            r6.append(r1)
            r6.append(r11)
            java.lang.String r4 = r6.toString()
        L_0x03b6:
            r13 = r4
            if (r12 == 0) goto L_0x03d1
            if (r32 == 0) goto L_0x03d1
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r12)
            r4.append(r1)
            r10 = r32
            r4.append(r10)
            java.lang.String r4 = r4.toString()
            r12 = r4
            goto L_0x03d3
        L_0x03d1:
            r10 = r32
        L_0x03d3:
            if (r0 == 0) goto L_0x03ec
            if (r31 == 0) goto L_0x03ec
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r0)
            r4.append(r1)
            r6 = r31
            r4.append(r6)
            java.lang.String r0 = r4.toString()
            goto L_0x03ee
        L_0x03ec:
            r6 = r31
        L_0x03ee:
            r4 = r0
            if (r30 == 0) goto L_0x0430
            r7 = r30
            java.lang.String r0 = r7.path
            if (r0 == 0) goto L_0x042b
            r8 = 0
            r9 = 1
            r11 = 1
            if (r29 == 0) goto L_0x03ff
            r21 = 2
            goto L_0x0401
        L_0x03ff:
            r21 = 1
        L_0x0401:
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
            goto L_0x04cd
        L_0x042b:
            r14 = r2
            r15 = r7
            r17 = r10
            goto L_0x0435
        L_0x0430:
            r14 = r2
            r17 = r10
            r15 = r30
        L_0x0435:
            if (r24 == 0) goto L_0x0497
            int r0 = r37.getCacheType()
            r19 = 1
            if (r0 != 0) goto L_0x0444
            if (r26 == 0) goto L_0x0444
            r25 = 1
            goto L_0x0446
        L_0x0444:
            r25 = r0
        L_0x0446:
            if (r25 != 0) goto L_0x044a
            r8 = 1
            goto L_0x044c
        L_0x044a:
            r8 = r25
        L_0x044c:
            if (r29 != 0) goto L_0x0464
            r7 = 0
            r9 = 1
            if (r29 == 0) goto L_0x0454
            r10 = 2
            goto L_0x0455
        L_0x0454:
            r10 = 1
        L_0x0455:
            r0 = r36
            r1 = r37
            r2 = r4
            r3 = r18
            r4 = r14
            r14 = r11
            r11 = r28
            r0.createLoadOperationForImageReceiver(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r11)
            goto L_0x0465
        L_0x0464:
            r14 = r11
        L_0x0465:
            if (r27 != 0) goto L_0x047d
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
        L_0x047d:
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
            goto L_0x04cd
        L_0x0497:
            int r0 = r37.getCacheType()
            if (r0 != 0) goto L_0x04a1
            if (r26 == 0) goto L_0x04a1
            r13 = 1
            goto L_0x04a2
        L_0x04a1:
            r13 = r0
        L_0x04a2:
            if (r13 != 0) goto L_0x04a6
            r8 = 1
            goto L_0x04a7
        L_0x04a6:
            r8 = r13
        L_0x04a7:
            r7 = 0
            r9 = 1
            if (r29 == 0) goto L_0x04ad
            r10 = 2
            goto L_0x04ae
        L_0x04ad:
            r10 = 1
        L_0x04ae:
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
        L_0x04cd:
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
                ImageLoader.this.lambda$httpFileLoadError$7$ImageLoader(this.f$1);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$httpFileLoadError$7 */
    public /* synthetic */ void lambda$httpFileLoadError$7$ImageLoader(String str) {
        CacheImage cacheImage = this.imageLoadingByUrl.get(str);
        if (cacheImage != null) {
            HttpImageTask httpImageTask = cacheImage.httpTask;
            if (httpImageTask != null) {
                HttpImageTask httpImageTask2 = new HttpImageTask(httpImageTask.cacheImage, httpImageTask.imageSize);
                cacheImage.httpTask = httpImageTask2;
                this.httpTasks.add(httpImageTask2);
            }
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
                ImageLoader.this.lambda$artworkLoadError$8$ImageLoader(this.f$1);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$artworkLoadError$8 */
    public /* synthetic */ void lambda$artworkLoadError$8$ImageLoader(String str) {
        CacheImage cacheImage = this.imageLoadingByUrl.get(str);
        if (cacheImage != null) {
            ArtworkLoadTask artworkLoadTask = cacheImage.artworkTask;
            if (artworkLoadTask != null) {
                ArtworkLoadTask artworkLoadTask2 = new ArtworkLoadTask(artworkLoadTask.cacheImage);
                cacheImage.artworkTask = artworkLoadTask2;
                this.artworkTasks.add(artworkLoadTask2);
            }
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
                ImageLoader.this.lambda$fileDidLoaded$9$ImageLoader(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$fileDidLoaded$9 */
    public /* synthetic */ void lambda$fileDidLoaded$9$ImageLoader(String str, int i, File file) {
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
                    ImageLoader.this.lambda$fileDidFailedLoad$10$ImageLoader(this.f$1);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$fileDidFailedLoad$10 */
    public /* synthetic */ void lambda$fileDidFailedLoad$10$ImageLoader(String str) {
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
                ImageLoader.this.lambda$runHttpFileLoadTasks$12$ImageLoader(this.f$1, this.f$2);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$runHttpFileLoadTasks$12 */
    public /* synthetic */ void lambda$runHttpFileLoadTasks$12$ImageLoader(HttpFileTask httpFileTask, int i) {
        if (httpFileTask != null) {
            this.currentHttpFileLoadTasksCount--;
        }
        if (httpFileTask != null) {
            if (i == 1) {
                if (httpFileTask.canRetry) {
                    $$Lambda$ImageLoader$7hSWbk4YxqgHb3MBhiE6lXuZiiI r3 = new Runnable(new HttpFileTask(httpFileTask.url, httpFileTask.tempFile, httpFileTask.ext, httpFileTask.currentAccount)) {
                        public final /* synthetic */ ImageLoader.HttpFileTask f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final void run() {
                            ImageLoader.this.lambda$null$11$ImageLoader(this.f$1);
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$11 */
    public /* synthetic */ void lambda$null$11$ImageLoader(HttpFileTask httpFileTask) {
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

    /* JADX WARNING: Removed duplicated region for block: B:29:0x00a6  */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x00ab  */
    /* JADX WARNING: Removed duplicated region for block: B:36:0x00c3 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:42:0x00f6  */
    /* JADX WARNING: Removed duplicated region for block: B:45:0x0106  */
    /* JADX WARNING: Removed duplicated region for block: B:47:0x011c  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static org.telegram.tgnet.TLRPC$PhotoSize scaleAndSaveImageInternal(org.telegram.tgnet.TLRPC$PhotoSize r13, android.graphics.Bitmap r14, android.graphics.Bitmap.CompressFormat r15, boolean r16, int r17, int r18, float r19, float r20, float r21, int r22, boolean r23, boolean r24, boolean r25) throws java.lang.Exception {
        /*
            r0 = r13
            r1 = r14
            r2 = r15
            r3 = r22
            r4 = 1065353216(0x3var_, float:1.0)
            int r4 = (r21 > r4 ? 1 : (r21 == r4 ? 0 : -1))
            if (r4 > 0) goto L_0x0010
            if (r24 == 0) goto L_0x000e
            goto L_0x0010
        L_0x000e:
            r4 = r1
            goto L_0x0019
        L_0x0010:
            r4 = 1
            r5 = r17
            r6 = r18
            android.graphics.Bitmap r4 = org.telegram.messenger.Bitmaps.createScaledBitmap(r14, r5, r6, r4)
        L_0x0019:
            r5 = 0
            r6 = -2147483648(0xfffffffvar_, double:NaN)
            if (r0 == 0) goto L_0x0029
            org.telegram.tgnet.TLRPC$FileLocation r8 = r0.location
            boolean r9 = r8 instanceof org.telegram.tgnet.TLRPC$TL_fileLocationToBeDeprecated
            if (r9 != 0) goto L_0x0026
            goto L_0x0029
        L_0x0026:
            org.telegram.tgnet.TLRPC$TL_fileLocationToBeDeprecated r8 = (org.telegram.tgnet.TLRPC$TL_fileLocationToBeDeprecated) r8
            goto L_0x0086
        L_0x0029:
            org.telegram.tgnet.TLRPC$TL_fileLocationToBeDeprecated r8 = new org.telegram.tgnet.TLRPC$TL_fileLocationToBeDeprecated
            r8.<init>()
            r8.volume_id = r6
            r0 = -2147483648(0xfffffffvar_, float:-0.0)
            r8.dc_id = r0
            int r0 = org.telegram.messenger.SharedConfig.getLastLocalId()
            r8.local_id = r0
            byte[] r0 = new byte[r5]
            r8.file_reference = r0
            org.telegram.tgnet.TLRPC$TL_photoSize r0 = new org.telegram.tgnet.TLRPC$TL_photoSize
            r0.<init>()
            r0.location = r8
            int r9 = r4.getWidth()
            r0.w = r9
            int r9 = r4.getHeight()
            r0.h = r9
            int r10 = r0.w
            r11 = 100
            if (r10 > r11) goto L_0x005e
            if (r9 > r11) goto L_0x005e
            java.lang.String r9 = "s"
            r0.type = r9
            goto L_0x0086
        L_0x005e:
            r11 = 320(0x140, float:4.48E-43)
            if (r10 > r11) goto L_0x0069
            if (r9 > r11) goto L_0x0069
            java.lang.String r9 = "m"
            r0.type = r9
            goto L_0x0086
        L_0x0069:
            r11 = 800(0x320, float:1.121E-42)
            if (r10 > r11) goto L_0x0075
            if (r9 > r11) goto L_0x0075
            java.lang.String r9 = "x"
            r0.type = r9
            goto L_0x0086
        L_0x0075:
            r11 = 1280(0x500, float:1.794E-42)
            if (r10 > r11) goto L_0x0081
            if (r9 > r11) goto L_0x0081
            java.lang.String r9 = "y"
            r0.type = r9
            goto L_0x0086
        L_0x0081:
            java.lang.String r9 = "w"
            r0.type = r9
        L_0x0086:
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            long r10 = r8.volume_id
            r9.append(r10)
            java.lang.String r10 = "_"
            r9.append(r10)
            int r10 = r8.local_id
            r9.append(r10)
            java.lang.String r10 = ".jpg"
            r9.append(r10)
            java.lang.String r9 = r9.toString()
            r10 = 4
            if (r25 == 0) goto L_0x00ab
            java.io.File r5 = org.telegram.messenger.FileLoader.getDirectory(r10)
            goto L_0x00ba
        L_0x00ab:
            long r11 = r8.volume_id
            int r8 = (r11 > r6 ? 1 : (r11 == r6 ? 0 : -1))
            if (r8 == 0) goto L_0x00b6
            java.io.File r5 = org.telegram.messenger.FileLoader.getDirectory(r5)
            goto L_0x00ba
        L_0x00b6:
            java.io.File r5 = org.telegram.messenger.FileLoader.getDirectory(r10)
        L_0x00ba:
            java.io.File r6 = new java.io.File
            r6.<init>(r5, r9)
            android.graphics.Bitmap$CompressFormat r5 = android.graphics.Bitmap.CompressFormat.JPEG
            if (r2 != r5) goto L_0x00ec
            if (r16 == 0) goto L_0x00ec
            boolean r5 = org.telegram.messenger.BuildVars.DEBUG_VERSION
            if (r5 == 0) goto L_0x00ec
            int r5 = r4.getWidth()
            int r7 = r4.getHeight()
            int r8 = r4.getRowBytes()
            java.lang.String r6 = r6.getAbsolutePath()
            r16 = r4
            r17 = r5
            r18 = r7
            r19 = r8
            r20 = r22
            r21 = r6
            int r5 = org.telegram.messenger.Utilities.saveProgressiveJpeg(r16, r17, r18, r19, r20, r21)
            r0.size = r5
            goto L_0x0104
        L_0x00ec:
            java.io.FileOutputStream r5 = new java.io.FileOutputStream
            r5.<init>(r6)
            r4.compress(r15, r3, r5)
            if (r23 != 0) goto L_0x0101
            java.nio.channels.FileChannel r6 = r5.getChannel()
            long r6 = r6.size()
            int r7 = (int) r6
            r0.size = r7
        L_0x0101:
            r5.close()
        L_0x0104:
            if (r23 == 0) goto L_0x011a
            java.io.ByteArrayOutputStream r5 = new java.io.ByteArrayOutputStream
            r5.<init>()
            r4.compress(r15, r3, r5)
            byte[] r2 = r5.toByteArray()
            r0.bytes = r2
            int r2 = r2.length
            r0.size = r2
            r5.close()
        L_0x011a:
            if (r4 == r1) goto L_0x011f
            r4.recycle()
        L_0x011f:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ImageLoader.scaleAndSaveImageInternal(org.telegram.tgnet.TLRPC$PhotoSize, android.graphics.Bitmap, android.graphics.Bitmap$CompressFormat, boolean, int, int, float, float, float, int, boolean, boolean, boolean):org.telegram.tgnet.TLRPC$PhotoSize");
    }

    public static TLRPC$PhotoSize scaleAndSaveImage(Bitmap bitmap, float f, float f2, int i, boolean z) {
        return scaleAndSaveImage((TLRPC$PhotoSize) null, bitmap, Bitmap.CompressFormat.JPEG, false, f, f2, i, z, 0, 0, false);
    }

    public static TLRPC$PhotoSize scaleAndSaveImage(TLRPC$PhotoSize tLRPC$PhotoSize, Bitmap bitmap, float f, float f2, int i, boolean z, boolean z2) {
        return scaleAndSaveImage(tLRPC$PhotoSize, bitmap, Bitmap.CompressFormat.JPEG, false, f, f2, i, z, 0, 0, z2);
    }

    public static TLRPC$PhotoSize scaleAndSaveImage(Bitmap bitmap, float f, float f2, int i, boolean z, int i2, int i3) {
        return scaleAndSaveImage((TLRPC$PhotoSize) null, bitmap, Bitmap.CompressFormat.JPEG, false, f, f2, i, z, i2, i3, false);
    }

    public static TLRPC$PhotoSize scaleAndSaveImage(Bitmap bitmap, float f, float f2, boolean z, int i, boolean z2, int i2, int i3) {
        return scaleAndSaveImage((TLRPC$PhotoSize) null, bitmap, Bitmap.CompressFormat.JPEG, z, f, f2, i, z2, i2, i3, false);
    }

    public static TLRPC$PhotoSize scaleAndSaveImage(Bitmap bitmap, Bitmap.CompressFormat compressFormat, float f, float f2, int i, boolean z, int i2, int i3) {
        return scaleAndSaveImage((TLRPC$PhotoSize) null, bitmap, compressFormat, false, f, f2, i, z, i2, i3, false);
    }

    public static TLRPC$PhotoSize scaleAndSaveImage(TLRPC$PhotoSize tLRPC$PhotoSize, Bitmap bitmap, Bitmap.CompressFormat compressFormat, boolean z, float f, float f2, int i, boolean z2, int i2, int i3, boolean z3) {
        boolean z4;
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
                    z4 = true;
                    i4 = (int) (width / f3);
                    i5 = (int) (height / f3);
                    if (!(i5 == 0 || i4 == 0)) {
                        int i8 = i5;
                        int i9 = i4;
                        float f7 = height;
                        float f8 = width;
                        return scaleAndSaveImageInternal(tLRPC$PhotoSize, bitmap, compressFormat, z, i4, i5, width, height, f3, i, z2, z4, z3);
                    }
                }
            }
            f3 = max;
            z4 = false;
            i4 = (int) (width / f3);
            i5 = (int) (height / f3);
            int i82 = i5;
            int i92 = i4;
            float var_ = height;
            float var_ = width;
            try {
                return scaleAndSaveImageInternal(tLRPC$PhotoSize, bitmap, compressFormat, z, i4, i5, width, height, f3, i, z2, z4, z3);
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
                        byte[] bArr4 = findPhotoCachedSize.bytes;
                        Utilities.aesCtrDecryptionByteArray(bArr4, bArr2, bArr3, 0, bArr4.length, 0);
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
                        org.telegram.ui.Components.Point messageSize = ChatMessageCell.getMessageSize(i2, i);
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
                org.telegram.ui.Components.Point messageSize2 = ChatMessageCell.getMessageSize(findPhotoCachedSize.w, findPhotoCachedSize.h);
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
