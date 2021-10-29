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
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.SparseArray;
import j$.util.concurrent.ConcurrentHashMap;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Locale;
import org.telegram.messenger.FileLoader;
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
import org.telegram.tgnet.TLRPC$TL_photoSize_layer127;
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
    public LruCache<BitmapDrawable> smallImagesMemCache;
    /* access modifiers changed from: private */
    public File telegramPath;
    /* access modifiers changed from: private */
    public ConcurrentHashMap<String, WebFile> testWebFile;
    /* access modifiers changed from: private */
    public HashMap<String, ThumbGenerateTask> thumbGenerateTasks = new HashMap<>();
    private DispatchQueue thumbGeneratingQueue = new DispatchQueue("thumbGeneratingQueue");
    private HashMap<String, ThumbGenerateInfo> waitingForQualityThumb = new HashMap<>();
    private SparseArray<String> waitingForQualityThumbByTag = new SparseArray<>();
    /* access modifiers changed from: private */
    public LruCache<BitmapDrawable> wallpaperMemCache;

    public void putThumbsToCache(ArrayList<MessageThumb> arrayList) {
        for (int i = 0; i < arrayList.size(); i++) {
            putImageToCache(arrayList.get(i).drawable, arrayList.get(i).key, true);
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
            Utilities.stageQueue.postRunnable(new ImageLoader$HttpFileTask$$ExternalSyntheticLambda0(this, j, j2));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$reportProgress$1(long j, long j2) {
            ImageLoader.this.fileProgresses.put(this.url, new long[]{j, j2});
            AndroidUtilities.runOnUIThread(new ImageLoader$HttpFileTask$$ExternalSyntheticLambda1(this, j, j2));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$reportProgress$0(long j, long j2) {
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.fileLoadProgressChanged, this.url, Long.valueOf(j), Long.valueOf(j2));
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
        /* JADX WARNING: Removed duplicated region for block: B:104:0x0129 A[Catch:{ all -> 0x012d }] */
        /* JADX WARNING: Removed duplicated region for block: B:107:0x0130 A[SYNTHETIC, Splitter:B:107:0x0130] */
        /* JADX WARNING: Removed duplicated region for block: B:112:0x013a A[SYNTHETIC, Splitter:B:112:0x013a] */
        /* JADX WARNING: Removed duplicated region for block: B:128:0x0154 A[SYNTHETIC, Splitter:B:128:0x0154] */
        /* JADX WARNING: Removed duplicated region for block: B:84:0x00f3 A[Catch:{ all -> 0x013e, all -> 0x0147, all -> 0x014e }] */
        /* JADX WARNING: Removed duplicated region for block: B:87:0x00fc A[Catch:{ all -> 0x013e, all -> 0x0147, all -> 0x014e }] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public java.lang.String doInBackground(java.lang.Void... r8) {
            /*
                r7 = this;
                r8 = 0
                r0 = 0
                org.telegram.messenger.ImageLoader$CacheImage r1 = r7.cacheImage     // Catch:{ all -> 0x00ec }
                org.telegram.messenger.ImageLocation r1 = r1.imageLocation     // Catch:{ all -> 0x00ec }
                java.lang.String r1 = r1.path     // Catch:{ all -> 0x00ec }
                java.net.URL r2 = new java.net.URL     // Catch:{ all -> 0x00ec }
                java.lang.String r3 = "athumb://"
                java.lang.String r4 = "https://"
                java.lang.String r1 = r1.replace(r3, r4)     // Catch:{ all -> 0x00ec }
                r2.<init>(r1)     // Catch:{ all -> 0x00ec }
                java.net.URLConnection r1 = r2.openConnection()     // Catch:{ all -> 0x00ec }
                java.net.HttpURLConnection r1 = (java.net.HttpURLConnection) r1     // Catch:{ all -> 0x00ec }
                r7.httpConnection = r1     // Catch:{ all -> 0x00ec }
                r2 = 5000(0x1388, float:7.006E-42)
                r1.setConnectTimeout(r2)     // Catch:{ all -> 0x00ec }
                java.net.HttpURLConnection r1 = r7.httpConnection     // Catch:{ all -> 0x00ec }
                r1.setReadTimeout(r2)     // Catch:{ all -> 0x00ec }
                java.net.HttpURLConnection r1 = r7.httpConnection     // Catch:{ all -> 0x00ec }
                r1.connect()     // Catch:{ all -> 0x00ec }
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
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)     // Catch:{ all -> 0x00ec }
            L_0x0047:
                java.net.HttpURLConnection r1 = r7.httpConnection     // Catch:{ all -> 0x00ec }
                java.io.InputStream r1 = r1.getInputStream()     // Catch:{ all -> 0x00ec }
                java.io.ByteArrayOutputStream r2 = new java.io.ByteArrayOutputStream     // Catch:{ all -> 0x00e6 }
                r2.<init>()     // Catch:{ all -> 0x00e6 }
                r3 = 32768(0x8000, float:4.5918E-41)
                byte[] r3 = new byte[r3]     // Catch:{ all -> 0x00e0 }
            L_0x0057:
                boolean r4 = r7.isCancelled()     // Catch:{ all -> 0x00e0 }
                if (r4 == 0) goto L_0x005e
                goto L_0x0068
            L_0x005e:
                int r4 = r1.read(r3)     // Catch:{ all -> 0x00e0 }
                if (r4 <= 0) goto L_0x0068
                r2.write(r3, r0, r4)     // Catch:{ all -> 0x00e0 }
                goto L_0x0057
            L_0x0068:
                r7.canRetry = r0     // Catch:{ all -> 0x00e0 }
                org.json.JSONObject r3 = new org.json.JSONObject     // Catch:{ all -> 0x00e0 }
                java.lang.String r4 = new java.lang.String     // Catch:{ all -> 0x00e0 }
                byte[] r5 = r2.toByteArray()     // Catch:{ all -> 0x00e0 }
                r4.<init>(r5)     // Catch:{ all -> 0x00e0 }
                r3.<init>(r4)     // Catch:{ all -> 0x00e0 }
                java.lang.String r4 = "results"
                org.json.JSONArray r3 = r3.getJSONArray(r4)     // Catch:{ all -> 0x00e0 }
                int r4 = r3.length()     // Catch:{ all -> 0x00e0 }
                if (r4 <= 0) goto L_0x00c8
                org.json.JSONObject r3 = r3.getJSONObject(r0)     // Catch:{ all -> 0x00e0 }
                java.lang.String r4 = "artworkUrl100"
                java.lang.String r3 = r3.getString(r4)     // Catch:{ all -> 0x00e0 }
                boolean r4 = r7.small     // Catch:{ all -> 0x00e0 }
                if (r4 == 0) goto L_0x00a9
                java.net.HttpURLConnection r8 = r7.httpConnection     // Catch:{ all -> 0x009a }
                if (r8 == 0) goto L_0x009b
                r8.disconnect()     // Catch:{ all -> 0x009a }
                goto L_0x009b
            L_0x009a:
            L_0x009b:
                if (r1 == 0) goto L_0x00a5
                r1.close()     // Catch:{ all -> 0x00a1 }
                goto L_0x00a5
            L_0x00a1:
                r8 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r8)
            L_0x00a5:
                r2.close()     // Catch:{ Exception -> 0x00a8 }
            L_0x00a8:
                return r3
            L_0x00a9:
                java.lang.String r4 = "100x100"
                java.lang.String r5 = "600x600"
                java.lang.String r8 = r3.replace(r4, r5)     // Catch:{ all -> 0x00e0 }
                java.net.HttpURLConnection r0 = r7.httpConnection     // Catch:{ all -> 0x00b9 }
                if (r0 == 0) goto L_0x00ba
                r0.disconnect()     // Catch:{ all -> 0x00b9 }
                goto L_0x00ba
            L_0x00b9:
            L_0x00ba:
                if (r1 == 0) goto L_0x00c4
                r1.close()     // Catch:{ all -> 0x00c0 }
                goto L_0x00c4
            L_0x00c0:
                r0 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            L_0x00c4:
                r2.close()     // Catch:{ Exception -> 0x00c7 }
            L_0x00c7:
                return r8
            L_0x00c8:
                java.net.HttpURLConnection r0 = r7.httpConnection     // Catch:{ all -> 0x00d0 }
                if (r0 == 0) goto L_0x00d1
                r0.disconnect()     // Catch:{ all -> 0x00d0 }
                goto L_0x00d1
            L_0x00d0:
            L_0x00d1:
                if (r1 == 0) goto L_0x00db
                r1.close()     // Catch:{ all -> 0x00d7 }
                goto L_0x00db
            L_0x00d7:
                r0 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            L_0x00db:
                r2.close()     // Catch:{ Exception -> 0x013d }
                goto L_0x013d
            L_0x00e0:
                r3 = move-exception
                r6 = r2
                r2 = r1
                r1 = r3
                r3 = r6
                goto L_0x00ef
            L_0x00e6:
                r2 = move-exception
                r3 = r8
                r6 = r2
                r2 = r1
                r1 = r6
                goto L_0x00ef
            L_0x00ec:
                r1 = move-exception
                r2 = r8
                r3 = r2
            L_0x00ef:
                boolean r4 = r1 instanceof java.net.SocketTimeoutException     // Catch:{ all -> 0x013e }
                if (r4 == 0) goto L_0x00fc
                boolean r4 = org.telegram.messenger.ApplicationLoader.isNetworkOnline()     // Catch:{ all -> 0x013e }
                if (r4 == 0) goto L_0x0122
                r7.canRetry = r0     // Catch:{ all -> 0x013e }
                goto L_0x0122
            L_0x00fc:
                boolean r4 = r1 instanceof java.net.UnknownHostException     // Catch:{ all -> 0x013e }
                if (r4 == 0) goto L_0x0103
                r7.canRetry = r0     // Catch:{ all -> 0x013e }
                goto L_0x0122
            L_0x0103:
                boolean r4 = r1 instanceof java.net.SocketException     // Catch:{ all -> 0x013e }
                if (r4 == 0) goto L_0x011c
                java.lang.String r4 = r1.getMessage()     // Catch:{ all -> 0x013e }
                if (r4 == 0) goto L_0x0122
                java.lang.String r4 = r1.getMessage()     // Catch:{ all -> 0x013e }
                java.lang.String r5 = "ECONNRESET"
                boolean r4 = r4.contains(r5)     // Catch:{ all -> 0x013e }
                if (r4 == 0) goto L_0x0122
                r7.canRetry = r0     // Catch:{ all -> 0x013e }
                goto L_0x0122
            L_0x011c:
                boolean r4 = r1 instanceof java.io.FileNotFoundException     // Catch:{ all -> 0x013e }
                if (r4 == 0) goto L_0x0122
                r7.canRetry = r0     // Catch:{ all -> 0x013e }
            L_0x0122:
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)     // Catch:{ all -> 0x013e }
                java.net.HttpURLConnection r0 = r7.httpConnection     // Catch:{ all -> 0x012d }
                if (r0 == 0) goto L_0x012e
                r0.disconnect()     // Catch:{ all -> 0x012d }
                goto L_0x012e
            L_0x012d:
            L_0x012e:
                if (r2 == 0) goto L_0x0138
                r2.close()     // Catch:{ all -> 0x0134 }
                goto L_0x0138
            L_0x0134:
                r0 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            L_0x0138:
                if (r3 == 0) goto L_0x013d
                r3.close()     // Catch:{ Exception -> 0x013d }
            L_0x013d:
                return r8
            L_0x013e:
                r8 = move-exception
                java.net.HttpURLConnection r0 = r7.httpConnection     // Catch:{ all -> 0x0147 }
                if (r0 == 0) goto L_0x0148
                r0.disconnect()     // Catch:{ all -> 0x0147 }
                goto L_0x0148
            L_0x0147:
            L_0x0148:
                if (r2 == 0) goto L_0x0152
                r2.close()     // Catch:{ all -> 0x014e }
                goto L_0x0152
            L_0x014e:
                r0 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            L_0x0152:
                if (r3 == 0) goto L_0x0157
                r3.close()     // Catch:{ Exception -> 0x0157 }
            L_0x0157:
                goto L_0x0159
            L_0x0158:
                throw r8
            L_0x0159:
                goto L_0x0158
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ImageLoader.ArtworkLoadTask.doInBackground(java.lang.Void[]):java.lang.String");
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(String str) {
            if (str != null) {
                ImageLoader.this.imageLoadQueue.postRunnable(new ImageLoader$ArtworkLoadTask$$ExternalSyntheticLambda2(this, str));
            } else if (this.canRetry) {
                ImageLoader.this.artworkLoadError(this.cacheImage.url);
            }
            ImageLoader.this.imageLoadQueue.postRunnable(new ImageLoader$ArtworkLoadTask$$ExternalSyntheticLambda0(this));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onPostExecute$0(String str) {
            CacheImage cacheImage2 = this.cacheImage;
            cacheImage2.httpTask = new HttpImageTask(cacheImage2, 0, str);
            ImageLoader.this.httpTasks.add(this.cacheImage.httpTask);
            ImageLoader.this.runHttpTasks(false);
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onPostExecute$1() {
            ImageLoader.this.runArtworkTasks(true);
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onCancelled$2() {
            ImageLoader.this.runArtworkTasks(true);
        }

        /* access modifiers changed from: protected */
        public void onCancelled() {
            ImageLoader.this.imageLoadQueue.postRunnable(new ImageLoader$ArtworkLoadTask$$ExternalSyntheticLambda1(this));
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

        /* access modifiers changed from: private */
        public static /* synthetic */ void lambda$doInBackground$2(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
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
            Utilities.stageQueue.postRunnable(new ImageLoader$HttpImageTask$$ExternalSyntheticLambda4(this, j, j2));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$reportProgress$1(long j, long j2) {
            ImageLoader.this.fileProgresses.put(this.cacheImage.url, new long[]{j, j2});
            AndroidUtilities.runOnUIThread(new ImageLoader$HttpImageTask$$ExternalSyntheticLambda5(this, j, j2));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$reportProgress$0(long j, long j2) {
            NotificationCenter.getInstance(this.cacheImage.currentAccount).postNotificationName(NotificationCenter.fileLoadProgressChanged, this.cacheImage.url, Long.valueOf(j), Long.valueOf(j2));
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
                org.telegram.messenger.ImageLoader$HttpImageTask$$ExternalSyntheticLambda8 r5 = org.telegram.messenger.ImageLoader$HttpImageTask$$ExternalSyntheticLambda8.INSTANCE     // Catch:{ all -> 0x00a5 }
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
            Utilities.stageQueue.postRunnable(new ImageLoader$HttpImageTask$$ExternalSyntheticLambda6(this, bool));
            ImageLoader.this.imageLoadQueue.postRunnable(new ImageLoader$HttpImageTask$$ExternalSyntheticLambda0(this));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onPostExecute$4(Boolean bool) {
            ImageLoader.this.fileProgresses.remove(this.cacheImage.url);
            AndroidUtilities.runOnUIThread(new ImageLoader$HttpImageTask$$ExternalSyntheticLambda7(this, bool));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onPostExecute$3(Boolean bool) {
            if (bool.booleanValue()) {
                NotificationCenter instance = NotificationCenter.getInstance(this.cacheImage.currentAccount);
                int i = NotificationCenter.fileLoaded;
                CacheImage cacheImage2 = this.cacheImage;
                instance.postNotificationName(i, cacheImage2.url, cacheImage2.finalFilePath);
                return;
            }
            NotificationCenter.getInstance(this.cacheImage.currentAccount).postNotificationName(NotificationCenter.fileLoadFailed, this.cacheImage.url, 2);
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onPostExecute$5() {
            ImageLoader.this.runHttpTasks(true);
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onCancelled$6() {
            ImageLoader.this.runHttpTasks(true);
        }

        /* access modifiers changed from: protected */
        public void onCancelled() {
            ImageLoader.this.imageLoadQueue.postRunnable(new ImageLoader$HttpImageTask$$ExternalSyntheticLambda2(this));
            Utilities.stageQueue.postRunnable(new ImageLoader$HttpImageTask$$ExternalSyntheticLambda3(this));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onCancelled$8() {
            ImageLoader.this.fileProgresses.remove(this.cacheImage.url);
            AndroidUtilities.runOnUIThread(new ImageLoader$HttpImageTask$$ExternalSyntheticLambda1(this));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onCancelled$7() {
            NotificationCenter.getInstance(this.cacheImage.currentAccount).postNotificationName(NotificationCenter.fileLoadFailed, this.cacheImage.url, 1);
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
                ImageLoader.this.imageLoadQueue.postRunnable(new ImageLoader$ThumbGenerateTask$$ExternalSyntheticLambda0(this, FileLoader.getAttachFileName(thumbGenerateInfo.parentDocument)));
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$removeTask$0(String str) {
            ImageLoader.this.thumbGenerateTasks.remove(str);
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
                                AndroidUtilities.runOnUIThread(new ImageLoader$ThumbGenerateTask$$ExternalSyntheticLambda1(this, str, new ArrayList(this.info.imageReceiverArray), new BitmapDrawable(bitmap), new ArrayList(this.info.imageReceiverGuidsArray)));
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
        public /* synthetic */ void lambda$run$1(String str, ArrayList arrayList, BitmapDrawable bitmapDrawable, ArrayList arrayList2) {
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

    /* JADX WARNING: Missing exception handler attribute for start block: B:20:0x0039 */
    /* JADX WARNING: Missing exception handler attribute for start block: B:25:0x003e */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.lang.String decompressGzip(java.io.File r5) {
        /*
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = ""
            if (r5 != 0) goto L_0x000a
            return r1
        L_0x000a:
            java.util.zip.GZIPInputStream r2 = new java.util.zip.GZIPInputStream     // Catch:{ Exception -> 0x003f }
            java.io.FileInputStream r3 = new java.io.FileInputStream     // Catch:{ Exception -> 0x003f }
            r3.<init>(r5)     // Catch:{ Exception -> 0x003f }
            r2.<init>(r3)     // Catch:{ Exception -> 0x003f }
            java.io.BufferedReader r5 = new java.io.BufferedReader     // Catch:{ all -> 0x003a }
            java.io.InputStreamReader r3 = new java.io.InputStreamReader     // Catch:{ all -> 0x003a }
            java.lang.String r4 = "UTF-8"
            r3.<init>(r2, r4)     // Catch:{ all -> 0x003a }
            r5.<init>(r3)     // Catch:{ all -> 0x003a }
        L_0x0020:
            java.lang.String r3 = r5.readLine()     // Catch:{ all -> 0x0035 }
            if (r3 == 0) goto L_0x002a
            r0.append(r3)     // Catch:{ all -> 0x0035 }
            goto L_0x0020
        L_0x002a:
            java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x0035 }
            r5.close()     // Catch:{ all -> 0x003a }
            r2.close()     // Catch:{ Exception -> 0x003f }
            return r0
        L_0x0035:
            r0 = move-exception
            r5.close()     // Catch:{ all -> 0x0039 }
        L_0x0039:
            throw r0     // Catch:{ all -> 0x003a }
        L_0x003a:
            r5 = move-exception
            r2.close()     // Catch:{ all -> 0x003e }
        L_0x003e:
            throw r5     // Catch:{ Exception -> 0x003f }
        L_0x003f:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ImageLoader.decompressGzip(java.io.File):java.lang.String");
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
        public void run() {
            /*
                r35 = this;
                r1 = r35
                java.lang.Object r2 = r1.sync
                monitor-enter(r2)
                java.lang.Thread r0 = java.lang.Thread.currentThread()     // Catch:{ all -> 0x0b4e }
                r1.runningThread = r0     // Catch:{ all -> 0x0b4e }
                java.lang.Thread.interrupted()     // Catch:{ all -> 0x0b4e }
                boolean r0 = r1.isCancelled     // Catch:{ all -> 0x0b4e }
                if (r0 == 0) goto L_0x0014
                monitor-exit(r2)     // Catch:{ all -> 0x0b4e }
                return
            L_0x0014:
                monitor-exit(r2)     // Catch:{ all -> 0x0b4e }
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
                goto L_0x0b4d
            L_0x0037:
                int r3 = r0.imageType
                r4 = 5
                if (r3 != r4) goto L_0x0057
                org.telegram.ui.Components.ThemePreviewDrawable r0 = new org.telegram.ui.Components.ThemePreviewDrawable     // Catch:{ all -> 0x004d }
                org.telegram.messenger.ImageLoader$CacheImage r2 = r1.cacheImage     // Catch:{ all -> 0x004d }
                java.io.File r3 = r2.finalFilePath     // Catch:{ all -> 0x004d }
                org.telegram.messenger.ImageLocation r2 = r2.imageLocation     // Catch:{ all -> 0x004d }
                org.telegram.tgnet.TLRPC$Document r2 = r2.document     // Catch:{ all -> 0x004d }
                org.telegram.messenger.DocumentObject$ThemeDocument r2 = (org.telegram.messenger.DocumentObject.ThemeDocument) r2     // Catch:{ all -> 0x004d }
                r0.<init>(r3, r2)     // Catch:{ all -> 0x004d }
                r5 = r0
                goto L_0x0052
            L_0x004d:
                r0 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
                r5 = 0
            L_0x0052:
                r1.onPostExecute(r5)
                goto L_0x0b4d
            L_0x0057:
                r6 = 4
                r7 = 3
                r8 = 2
                r9 = 1
                r10 = 0
                if (r3 == r7) goto L_0x0af5
                if (r3 != r6) goto L_0x0062
                goto L_0x0af5
            L_0x0062:
                r11 = 8
                if (r3 != r9) goto L_0x021a
                r0 = 1126865306(0x432a999a, float:170.6)
                int r2 = org.telegram.messenger.AndroidUtilities.dp(r0)
                r3 = 512(0x200, float:7.175E-43)
                int r2 = java.lang.Math.min(r3, r2)
                int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
                int r0 = java.lang.Math.min(r3, r0)
                org.telegram.messenger.ImageLoader$CacheImage r12 = r1.cacheImage
                java.lang.String r12 = r12.filter
                if (r12 == 0) goto L_0x015d
                java.lang.String r13 = "_"
                java.lang.String[] r12 = r12.split(r13)
                int r13 = r12.length
                if (r13 < r8) goto L_0x00d7
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
                if (r0 > 0) goto L_0x00be
                int r0 = (r2 > r14 ? 1 : (r2 == r14 ? 0 : -1))
                if (r0 > 0) goto L_0x00be
                r0 = 160(0xa0, float:2.24E-43)
                int r2 = java.lang.Math.min(r13, r0)
                int r0 = java.lang.Math.min(r3, r0)
                r3 = 1
                goto L_0x00c1
            L_0x00be:
                r0 = r3
                r2 = r13
                r3 = 0
            L_0x00c1:
                int r13 = r12.length
                if (r13 < r7) goto L_0x00d0
                java.lang.String r13 = "pcache"
                r14 = r12[r8]
                boolean r13 = r13.equals(r14)
                if (r13 == 0) goto L_0x00d0
            L_0x00ce:
                r13 = 1
                goto L_0x00d9
            L_0x00d0:
                int r13 = org.telegram.messenger.SharedConfig.getDevicePerformanceClass()
                if (r13 == r8) goto L_0x00d8
                goto L_0x00ce
            L_0x00d7:
                r3 = 0
            L_0x00d8:
                r13 = 0
            L_0x00d9:
                int r14 = r12.length
                if (r14 < r7) goto L_0x0102
                java.lang.String r14 = "nr"
                r15 = r12[r8]
                boolean r14 = r14.equals(r15)
                if (r14 == 0) goto L_0x00e8
                r7 = 2
                goto L_0x0103
            L_0x00e8:
                java.lang.String r14 = "nrs"
                r15 = r12[r8]
                boolean r14 = r14.equals(r15)
                if (r14 == 0) goto L_0x00f3
                goto L_0x0103
            L_0x00f3:
                java.lang.String r14 = "dice"
                r15 = r12[r8]
                boolean r14 = r14.equals(r15)
                if (r14 == 0) goto L_0x0102
                r7 = r12[r7]
                r14 = r7
                r7 = 2
                goto L_0x0104
            L_0x0102:
                r7 = 1
            L_0x0103:
                r14 = 0
            L_0x0104:
                int r15 = r12.length
                if (r15 < r4) goto L_0x0159
                java.lang.String r4 = "c1"
                r15 = r12[r6]
                boolean r4 = r4.equals(r15)
                if (r4 == 0) goto L_0x0119
                int[] r4 = new int[r11]
                r4 = {16219713, 13335381, 16757049, 16168585, 16765248, 16764327, 16768889, 16768965} // fill-array
            L_0x0116:
                r6 = r4
                r4 = r3
                goto L_0x015b
            L_0x0119:
                java.lang.String r4 = "c2"
                r15 = r12[r6]
                boolean r4 = r4.equals(r15)
                if (r4 == 0) goto L_0x0129
                int[] r4 = new int[r11]
                r4 = {16219713, 10771000, 16757049, 14653547, 16765248, 15577475, 16768889, 16040864} // fill-array
                goto L_0x0116
            L_0x0129:
                java.lang.String r4 = "c3"
                r15 = r12[r6]
                boolean r4 = r4.equals(r15)
                if (r4 == 0) goto L_0x0139
                int[] r4 = new int[r11]
                r4 = {16219713, 7354903, 16757049, 11233085, 16765248, 12812110, 16768889, 14194279} // fill-array
                goto L_0x0116
            L_0x0139:
                java.lang.String r4 = "c4"
                r15 = r12[r6]
                boolean r4 = r4.equals(r15)
                if (r4 == 0) goto L_0x0149
                int[] r4 = new int[r11]
                r4 = {16219713, 4858889, 16757049, 8207886, 16765248, 9852201, 16768889, 11100983} // fill-array
                goto L_0x0116
            L_0x0149:
                java.lang.String r4 = "c5"
                r6 = r12[r6]
                boolean r4 = r4.equals(r6)
                if (r4 == 0) goto L_0x0159
                int[] r4 = new int[r11]
                r4 = {16219713, 2101002, 16757049, 4270372, 16765248, 5848375, 16768889, 6505791} // fill-array
                goto L_0x0116
            L_0x0159:
                r4 = r3
                r6 = 0
            L_0x015b:
                r3 = r2
                goto L_0x0163
            L_0x015d:
                r3 = r2
                r4 = 0
                r6 = 0
                r7 = 1
                r13 = 0
                r14 = 0
            L_0x0163:
                r2 = r0
                if (r14 == 0) goto L_0x017c
                java.lang.String r0 = "🎰"
                boolean r0 = r0.equals(r14)
                if (r0 == 0) goto L_0x0175
                org.telegram.ui.Components.SlotsDrawable r0 = new org.telegram.ui.Components.SlotsDrawable
                r0.<init>(r14, r3, r2)
                goto L_0x0204
            L_0x0175:
                org.telegram.ui.Components.RLottieDrawable r0 = new org.telegram.ui.Components.RLottieDrawable
                r0.<init>(r14, r3, r2)
                goto L_0x0204
            L_0x017c:
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage
                java.io.File r0 = r0.finalFilePath
                java.io.RandomAccessFile r11 = new java.io.RandomAccessFile     // Catch:{ Exception -> 0x01c1, all -> 0x01bd }
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage     // Catch:{ Exception -> 0x01c1, all -> 0x01bd }
                java.io.File r0 = r0.finalFilePath     // Catch:{ Exception -> 0x01c1, all -> 0x01bd }
                java.lang.String r12 = "r"
                r11.<init>(r0, r12)     // Catch:{ Exception -> 0x01c1, all -> 0x01bd }
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage     // Catch:{ Exception -> 0x01ba, all -> 0x01b6 }
                int r0 = r0.type     // Catch:{ Exception -> 0x01ba, all -> 0x01b6 }
                if (r0 != r9) goto L_0x0196
                byte[] r0 = org.telegram.messenger.ImageLoader.headerThumb     // Catch:{ Exception -> 0x01ba, all -> 0x01b6 }
                goto L_0x019a
            L_0x0196:
                byte[] r0 = org.telegram.messenger.ImageLoader.header     // Catch:{ Exception -> 0x01ba, all -> 0x01b6 }
            L_0x019a:
                r11.readFully(r0, r10, r8)     // Catch:{ Exception -> 0x01ba, all -> 0x01b6 }
                byte r5 = r0[r10]     // Catch:{ Exception -> 0x01ba, all -> 0x01b6 }
                r8 = 31
                if (r5 != r8) goto L_0x01aa
                byte r0 = r0[r9]     // Catch:{ Exception -> 0x01ba, all -> 0x01b6 }
                r5 = -117(0xffffffffffffff8b, float:NaN)
                if (r0 != r5) goto L_0x01aa
                goto L_0x01ab
            L_0x01aa:
                r9 = 0
            L_0x01ab:
                r11.close()     // Catch:{ Exception -> 0x01af }
                goto L_0x01b4
            L_0x01af:
                r0 = move-exception
                r5 = r0
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r5)
            L_0x01b4:
                r10 = r9
                goto L_0x01d1
            L_0x01b6:
                r0 = move-exception
                r2 = r0
                r5 = r11
                goto L_0x020e
            L_0x01ba:
                r0 = move-exception
                r5 = r11
                goto L_0x01c3
            L_0x01bd:
                r0 = move-exception
                r2 = r0
                r5 = 0
                goto L_0x020e
            L_0x01c1:
                r0 = move-exception
                r5 = 0
            L_0x01c3:
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ all -> 0x020c }
                if (r5 == 0) goto L_0x01d1
                r5.close()     // Catch:{ Exception -> 0x01cc }
                goto L_0x01d1
            L_0x01cc:
                r0 = move-exception
                r5 = r0
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r5)
            L_0x01d1:
                if (r10 == 0) goto L_0x01ee
                org.telegram.ui.Components.RLottieDrawable r0 = new org.telegram.ui.Components.RLottieDrawable
                org.telegram.messenger.ImageLoader$CacheImage r5 = r1.cacheImage
                java.io.File r5 = r5.finalFilePath
                java.lang.String r17 = org.telegram.messenger.ImageLoader.decompressGzip(r5)
                r15 = r0
                r16 = r5
                r18 = r3
                r19 = r2
                r20 = r13
                r21 = r4
                r22 = r6
                r15.<init>(r16, r17, r18, r19, r20, r21, r22)
                goto L_0x0204
            L_0x01ee:
                org.telegram.ui.Components.RLottieDrawable r0 = new org.telegram.ui.Components.RLottieDrawable
                org.telegram.messenger.ImageLoader$CacheImage r5 = r1.cacheImage
                java.io.File r5 = r5.finalFilePath
                r15 = r0
                r16 = r5
                r17 = r3
                r18 = r2
                r19 = r13
                r20 = r4
                r21 = r6
                r15.<init>((java.io.File) r16, (int) r17, (int) r18, (boolean) r19, (boolean) r20, (int[]) r21)
            L_0x0204:
                r0.setAutoRepeat(r7)
                r1.onPostExecute(r0)
                goto L_0x0b4d
            L_0x020c:
                r0 = move-exception
                r2 = r0
            L_0x020e:
                if (r5 == 0) goto L_0x0219
                r5.close()     // Catch:{ Exception -> 0x0214 }
                goto L_0x0219
            L_0x0214:
                r0 = move-exception
                r3 = r0
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r3)
            L_0x0219:
                throw r2
            L_0x021a:
                if (r3 != r8) goto L_0x02c2
                if (r2 == 0) goto L_0x0223
                long r12 = r2.videoSeekTo
                r22 = r12
                goto L_0x0225
            L_0x0223:
                r22 = 0
            L_0x0225:
                java.lang.String r2 = "g"
                java.lang.String r0 = r0.filter
                boolean r0 = r2.equals(r0)
                if (r0 == 0) goto L_0x026c
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage
                org.telegram.messenger.ImageLocation r2 = r0.imageLocation
                org.telegram.tgnet.TLRPC$Document r3 = r2.document
                boolean r4 = r3 instanceof org.telegram.tgnet.TLRPC$TL_documentEncrypted
                if (r4 != 0) goto L_0x026c
                boolean r4 = r3 instanceof org.telegram.tgnet.TLRPC$Document
                if (r4 == 0) goto L_0x0240
                r19 = r3
                goto L_0x0242
            L_0x0240:
                r19 = 0
            L_0x0242:
                if (r19 == 0) goto L_0x0247
                int r0 = r0.size
                goto L_0x0249
            L_0x0247:
                int r0 = r2.currentSize
            L_0x0249:
                org.telegram.ui.Components.AnimatedFileDrawable r2 = new org.telegram.ui.Components.AnimatedFileDrawable
                org.telegram.messenger.ImageLoader$CacheImage r3 = r1.cacheImage
                java.io.File r15 = r3.finalFilePath
                r16 = 0
                long r6 = (long) r0
                if (r19 != 0) goto L_0x0259
                org.telegram.messenger.ImageLocation r5 = r3.imageLocation
                r20 = r5
                goto L_0x025b
            L_0x0259:
                r20 = 0
            L_0x025b:
                java.lang.Object r0 = r3.parentObject
                int r3 = r3.currentAccount
                r25 = 0
                r14 = r2
                r17 = r6
                r21 = r0
                r24 = r3
                r14.<init>(r15, r16, r17, r19, r20, r21, r22, r24, r25)
                goto L_0x02ba
            L_0x026c:
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage
                java.lang.String r0 = r0.filter
                if (r0 == 0) goto L_0x0294
                java.lang.String r2 = "_"
                java.lang.String[] r0 = r0.split(r2)
                int r2 = r0.length
                if (r2 < r8) goto L_0x0294
                r2 = r0[r10]
                float r2 = java.lang.Float.parseFloat(r2)
                r0 = r0[r9]
                float r0 = java.lang.Float.parseFloat(r0)
                float r3 = org.telegram.messenger.AndroidUtilities.density
                float r2 = r2 * r3
                int r10 = (int) r2
                float r0 = r0 * r3
                int r0 = (int) r0
                r27 = r0
                r26 = r10
                goto L_0x0298
            L_0x0294:
                r26 = 0
                r27 = 0
            L_0x0298:
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
                r14.<init>(r15, r16, r17, r19, r20, r21, r22, r24, r25, r26, r27)
            L_0x02ba:
                java.lang.Thread.interrupted()
                r1.onPostExecute(r2)
                goto L_0x0b4d
            L_0x02c2:
                java.io.File r2 = r0.finalFilePath
                org.telegram.messenger.SecureDocument r3 = r0.secureDocument
                if (r3 != 0) goto L_0x02dd
                java.io.File r0 = r0.encryptionKeyPath
                if (r0 == 0) goto L_0x02db
                if (r2 == 0) goto L_0x02db
                java.lang.String r0 = r2.getAbsolutePath()
                java.lang.String r3 = ".enc"
                boolean r0 = r0.endsWith(r3)
                if (r0 == 0) goto L_0x02db
                goto L_0x02dd
            L_0x02db:
                r3 = 0
                goto L_0x02de
            L_0x02dd:
                r3 = 1
            L_0x02de:
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage
                org.telegram.messenger.SecureDocument r0 = r0.secureDocument
                if (r0 == 0) goto L_0x02f2
                org.telegram.messenger.SecureDocumentKey r4 = r0.secureDocumentKey
                org.telegram.tgnet.TLRPC$TL_secureFile r6 = r0.secureFile
                if (r6 == 0) goto L_0x02ef
                byte[] r6 = r6.file_hash
                if (r6 == 0) goto L_0x02ef
                goto L_0x02f4
            L_0x02ef:
                byte[] r6 = r0.fileHash
                goto L_0x02f4
            L_0x02f2:
                r4 = 0
                r6 = 0
            L_0x02f4:
                int r0 = android.os.Build.VERSION.SDK_INT
                r14 = 19
                if (r0 >= r14) goto L_0x0364
                java.io.RandomAccessFile r14 = new java.io.RandomAccessFile     // Catch:{ Exception -> 0x0344, all -> 0x0340 }
                java.lang.String r0 = "r"
                r14.<init>(r2, r0)     // Catch:{ Exception -> 0x0344, all -> 0x0340 }
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage     // Catch:{ Exception -> 0x033e }
                int r0 = r0.type     // Catch:{ Exception -> 0x033e }
                if (r0 != r9) goto L_0x030c
                byte[] r0 = org.telegram.messenger.ImageLoader.headerThumb     // Catch:{ Exception -> 0x033e }
                goto L_0x0310
            L_0x030c:
                byte[] r0 = org.telegram.messenger.ImageLoader.header     // Catch:{ Exception -> 0x033e }
            L_0x0310:
                int r15 = r0.length     // Catch:{ Exception -> 0x033e }
                r14.readFully(r0, r10, r15)     // Catch:{ Exception -> 0x033e }
                java.lang.String r15 = new java.lang.String     // Catch:{ Exception -> 0x033e }
                r15.<init>(r0)     // Catch:{ Exception -> 0x033e }
                java.lang.String r0 = r15.toLowerCase()     // Catch:{ Exception -> 0x033e }
                java.lang.String r0 = r0.toLowerCase()     // Catch:{ Exception -> 0x033e }
                java.lang.String r15 = "riff"
                boolean r15 = r0.startsWith(r15)     // Catch:{ Exception -> 0x033e }
                if (r15 == 0) goto L_0x0333
                java.lang.String r15 = "webp"
                boolean r0 = r0.endsWith(r15)     // Catch:{ Exception -> 0x033e }
                if (r0 == 0) goto L_0x0333
                r15 = 1
                goto L_0x0334
            L_0x0333:
                r15 = 0
            L_0x0334:
                r14.close()     // Catch:{ Exception -> 0x0338 }
                goto L_0x0365
            L_0x0338:
                r0 = move-exception
                r14 = r0
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r14)
                goto L_0x0365
            L_0x033e:
                r0 = move-exception
                goto L_0x0346
            L_0x0340:
                r0 = move-exception
                r2 = r0
                r5 = 0
                goto L_0x0358
            L_0x0344:
                r0 = move-exception
                r14 = 0
            L_0x0346:
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ all -> 0x0355 }
                if (r14 == 0) goto L_0x0364
                r14.close()     // Catch:{ Exception -> 0x034f }
                goto L_0x0364
            L_0x034f:
                r0 = move-exception
                r14 = r0
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r14)
                goto L_0x0364
            L_0x0355:
                r0 = move-exception
                r2 = r0
                r5 = r14
            L_0x0358:
                if (r5 == 0) goto L_0x0363
                r5.close()     // Catch:{ Exception -> 0x035e }
                goto L_0x0363
            L_0x035e:
                r0 = move-exception
                r3 = r0
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r3)
            L_0x0363:
                throw r2
            L_0x0364:
                r15 = 0
            L_0x0365:
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage
                org.telegram.messenger.ImageLocation r0 = r0.imageLocation
                java.lang.String r0 = r0.path
                if (r0 == 0) goto L_0x03cd
                java.lang.String r14 = "thumb://"
                boolean r14 = r0.startsWith(r14)
                if (r14 == 0) goto L_0x0399
                java.lang.String r14 = ":"
                int r14 = r0.indexOf(r14, r11)
                if (r14 < 0) goto L_0x038f
                java.lang.String r16 = r0.substring(r11, r14)
                long r16 = java.lang.Long.parseLong(r16)
                java.lang.Long r16 = java.lang.Long.valueOf(r16)
                int r14 = r14 + r9
                java.lang.String r0 = r0.substring(r14)
                goto L_0x0392
            L_0x038f:
                r0 = 0
                r16 = 0
            L_0x0392:
                r11 = r0
                r17 = r16
                r14 = 0
            L_0x0396:
                r18 = 0
                goto L_0x03d3
            L_0x0399:
                java.lang.String r14 = "vthumb://"
                boolean r14 = r0.startsWith(r14)
                if (r14 == 0) goto L_0x03c0
                java.lang.String r14 = ":"
                r11 = 9
                int r14 = r0.indexOf(r14, r11)
                if (r14 < 0) goto L_0x03b9
                java.lang.String r0 = r0.substring(r11, r14)
                long r17 = java.lang.Long.parseLong(r0)
                java.lang.Long r0 = java.lang.Long.valueOf(r17)
                r11 = 1
                goto L_0x03bb
            L_0x03b9:
                r0 = 0
                r11 = 0
            L_0x03bb:
                r17 = r0
                r14 = r11
                r11 = 0
                goto L_0x0396
            L_0x03c0:
                java.lang.String r11 = "http"
                boolean r0 = r0.startsWith(r11)
                if (r0 != 0) goto L_0x03cd
                r11 = 0
                r14 = 0
                r17 = 0
                goto L_0x0396
            L_0x03cd:
                r11 = 0
                r14 = 0
                r17 = 0
                r18 = 1
            L_0x03d3:
                android.graphics.BitmapFactory$Options r7 = new android.graphics.BitmapFactory$Options
                r7.<init>()
                r7.inSampleSize = r9
                int r0 = android.os.Build.VERSION.SDK_INT
                r12 = 21
                if (r0 >= r12) goto L_0x03e2
                r7.inPurgeable = r9
            L_0x03e2:
                org.telegram.messenger.ImageLoader r0 = org.telegram.messenger.ImageLoader.this
                boolean r13 = r0.canForce8888
                r22 = 0
                r23 = 1065353216(0x3var_, float:1.0)
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage     // Catch:{ all -> 0x0602 }
                java.lang.String r0 = r0.filter     // Catch:{ all -> 0x0602 }
                if (r0 == 0) goto L_0x0591
                java.lang.String r12 = "_"
                java.lang.String[] r0 = r0.split(r12)     // Catch:{ all -> 0x0602 }
                int r12 = r0.length     // Catch:{ all -> 0x0602 }
                if (r12 < r8) goto L_0x0418
                r12 = r0[r10]     // Catch:{ all -> 0x0602 }
                float r12 = java.lang.Float.parseFloat(r12)     // Catch:{ all -> 0x0602 }
                float r25 = org.telegram.messenger.AndroidUtilities.density     // Catch:{ all -> 0x0602 }
                float r12 = r12 * r25
                r0 = r0[r9]     // Catch:{ all -> 0x0413 }
                float r0 = java.lang.Float.parseFloat(r0)     // Catch:{ all -> 0x0413 }
                float r25 = org.telegram.messenger.AndroidUtilities.density     // Catch:{ all -> 0x0413 }
                float r0 = r0 * r25
                r25 = r12
                r12 = r0
                goto L_0x041b
            L_0x0413:
                r0 = move-exception
                r10 = r0
                r0 = 0
                goto L_0x058c
            L_0x0418:
                r12 = 0
                r25 = 0
            L_0x041b:
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage     // Catch:{ all -> 0x0587 }
                java.lang.String r0 = r0.filter     // Catch:{ all -> 0x0587 }
                java.lang.String r8 = "b2"
                boolean r0 = r0.contains(r8)     // Catch:{ all -> 0x0587 }
                if (r0 == 0) goto L_0x0429
                r8 = 3
                goto L_0x0446
            L_0x0429:
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage     // Catch:{ all -> 0x0587 }
                java.lang.String r0 = r0.filter     // Catch:{ all -> 0x0587 }
                java.lang.String r8 = "b1"
                boolean r0 = r0.contains(r8)     // Catch:{ all -> 0x0587 }
                if (r0 == 0) goto L_0x0437
                r8 = 2
                goto L_0x0446
            L_0x0437:
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage     // Catch:{ all -> 0x0587 }
                java.lang.String r0 = r0.filter     // Catch:{ all -> 0x0587 }
                java.lang.String r8 = "b"
                boolean r0 = r0.contains(r8)     // Catch:{ all -> 0x0587 }
                if (r0 == 0) goto L_0x0445
                r8 = 1
                goto L_0x0446
            L_0x0445:
                r8 = 0
            L_0x0446:
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage     // Catch:{ all -> 0x057e }
                java.lang.String r0 = r0.filter     // Catch:{ all -> 0x057e }
                java.lang.String r5 = "i"
                boolean r5 = r0.contains(r5)     // Catch:{ all -> 0x057e }
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage     // Catch:{ all -> 0x0574 }
                java.lang.String r0 = r0.filter     // Catch:{ all -> 0x0574 }
                java.lang.String r10 = "f"
                boolean r0 = r0.contains(r10)     // Catch:{ all -> 0x0574 }
                if (r0 == 0) goto L_0x045d
                r13 = 1
            L_0x045d:
                if (r15 != 0) goto L_0x0569
                int r0 = (r25 > r22 ? 1 : (r25 == r22 ? 0 : -1))
                if (r0 == 0) goto L_0x0569
                int r0 = (r12 > r22 ? 1 : (r12 == r22 ? 0 : -1))
                if (r0 == 0) goto L_0x0569
                r7.inJustDecodeBounds = r9     // Catch:{ all -> 0x0574 }
                if (r17 == 0) goto L_0x049b
                if (r11 != 0) goto L_0x049b
                if (r14 == 0) goto L_0x0485
                android.content.Context r0 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x0480 }
                android.content.ContentResolver r0 = r0.getContentResolver()     // Catch:{ all -> 0x0480 }
                long r9 = r17.longValue()     // Catch:{ all -> 0x0480 }
                r29 = r5
                r5 = 1
                android.provider.MediaStore.Video.Thumbnails.getThumbnail(r0, r9, r5, r7)     // Catch:{ all -> 0x0499 }
                goto L_0x0495
            L_0x0480:
                r0 = move-exception
                r29 = r5
                goto L_0x0579
            L_0x0485:
                r29 = r5
                android.content.Context r0 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x0499 }
                android.content.ContentResolver r0 = r0.getContentResolver()     // Catch:{ all -> 0x0499 }
                long r9 = r17.longValue()     // Catch:{ all -> 0x0499 }
                r5 = 1
                android.provider.MediaStore.Images.Thumbnails.getThumbnail(r0, r9, r5, r7)     // Catch:{ all -> 0x0499 }
            L_0x0495:
                r30 = r8
                goto L_0x0516
            L_0x0499:
                r0 = move-exception
                goto L_0x04f4
            L_0x049b:
                r29 = r5
                if (r4 == 0) goto L_0x04fc
                java.io.RandomAccessFile r0 = new java.io.RandomAccessFile     // Catch:{ all -> 0x04f1 }
                java.lang.String r5 = "r"
                r0.<init>(r2, r5)     // Catch:{ all -> 0x04f1 }
                long r9 = r0.length()     // Catch:{ all -> 0x04f1 }
                int r5 = (int) r9     // Catch:{ all -> 0x04f1 }
                java.lang.ThreadLocal r9 = org.telegram.messenger.ImageLoader.bytesLocal     // Catch:{ all -> 0x04f1 }
                java.lang.Object r9 = r9.get()     // Catch:{ all -> 0x04f1 }
                byte[] r9 = (byte[]) r9     // Catch:{ all -> 0x04f1 }
                if (r9 == 0) goto L_0x04bb
                int r10 = r9.length     // Catch:{ all -> 0x0499 }
                if (r10 < r5) goto L_0x04bb
                goto L_0x04bc
            L_0x04bb:
                r9 = 0
            L_0x04bc:
                if (r9 != 0) goto L_0x04c7
                byte[] r9 = new byte[r5]     // Catch:{ all -> 0x0499 }
                java.lang.ThreadLocal r10 = org.telegram.messenger.ImageLoader.bytesLocal     // Catch:{ all -> 0x0499 }
                r10.set(r9)     // Catch:{ all -> 0x0499 }
            L_0x04c7:
                r10 = 0
                r0.readFully(r9, r10, r5)     // Catch:{ all -> 0x04f1 }
                r0.close()     // Catch:{ all -> 0x04f1 }
                org.telegram.messenger.secretmedia.EncryptedFileInputStream.decryptBytesWithKeyFile((byte[]) r9, (int) r10, (int) r5, (org.telegram.messenger.SecureDocumentKey) r4)     // Catch:{ all -> 0x04f1 }
                byte[] r0 = org.telegram.messenger.Utilities.computeSHA256(r9, r10, r5)     // Catch:{ all -> 0x04f1 }
                if (r6 == 0) goto L_0x04e2
                boolean r0 = java.util.Arrays.equals(r0, r6)     // Catch:{ all -> 0x0499 }
                if (r0 != 0) goto L_0x04de
                goto L_0x04e2
            L_0x04de:
                r30 = r8
                r0 = 0
                goto L_0x04e5
            L_0x04e2:
                r30 = r8
                r0 = 1
            L_0x04e5:
                r10 = 0
                byte r8 = r9[r10]     // Catch:{ all -> 0x055f }
                r8 = r8 & 255(0xff, float:3.57E-43)
                int r5 = r5 - r8
                if (r0 != 0) goto L_0x0516
                android.graphics.BitmapFactory.decodeByteArray(r9, r8, r5, r7)     // Catch:{ all -> 0x055f }
                goto L_0x0516
            L_0x04f1:
                r0 = move-exception
                r30 = r8
            L_0x04f4:
                r10 = r0
                r0 = r12
                r12 = r25
                r5 = r29
                goto L_0x058e
            L_0x04fc:
                r30 = r8
                if (r3 == 0) goto L_0x050a
                org.telegram.messenger.secretmedia.EncryptedFileInputStream r0 = new org.telegram.messenger.secretmedia.EncryptedFileInputStream     // Catch:{ all -> 0x055f }
                org.telegram.messenger.ImageLoader$CacheImage r5 = r1.cacheImage     // Catch:{ all -> 0x055f }
                java.io.File r5 = r5.encryptionKeyPath     // Catch:{ all -> 0x055f }
                r0.<init>((java.io.File) r2, (java.io.File) r5)     // Catch:{ all -> 0x055f }
                goto L_0x050f
            L_0x050a:
                java.io.FileInputStream r0 = new java.io.FileInputStream     // Catch:{ all -> 0x055f }
                r0.<init>(r2)     // Catch:{ all -> 0x055f }
            L_0x050f:
                r5 = 0
                android.graphics.BitmapFactory.decodeStream(r0, r5, r7)     // Catch:{ all -> 0x055f }
                r0.close()     // Catch:{ all -> 0x055f }
            L_0x0516:
                int r0 = r7.outWidth     // Catch:{ all -> 0x055f }
                float r0 = (float) r0     // Catch:{ all -> 0x055f }
                int r5 = r7.outHeight     // Catch:{ all -> 0x055f }
                float r5 = (float) r5     // Catch:{ all -> 0x055f }
                int r8 = (r25 > r12 ? 1 : (r25 == r12 ? 0 : -1))
                if (r8 < 0) goto L_0x052d
                int r8 = (r0 > r5 ? 1 : (r0 == r5 ? 0 : -1))
                if (r8 <= 0) goto L_0x052d
                float r8 = r0 / r25
                float r9 = r5 / r12
                float r8 = java.lang.Math.max(r8, r9)     // Catch:{ all -> 0x055f }
                goto L_0x0535
            L_0x052d:
                float r8 = r0 / r25
                float r9 = r5 / r12
                float r8 = java.lang.Math.min(r8, r9)     // Catch:{ all -> 0x055f }
            L_0x0535:
                r9 = 1067030938(0x3var_a, float:1.2)
                int r9 = (r8 > r9 ? 1 : (r8 == r9 ? 0 : -1))
                if (r9 >= 0) goto L_0x053e
                r8 = 1065353216(0x3var_, float:1.0)
            L_0x053e:
                r9 = 0
                r7.inJustDecodeBounds = r9     // Catch:{ all -> 0x055f }
                int r9 = (r8 > r23 ? 1 : (r8 == r23 ? 0 : -1))
                if (r9 <= 0) goto L_0x055b
                int r0 = (r0 > r25 ? 1 : (r0 == r25 ? 0 : -1))
                if (r0 > 0) goto L_0x054d
                int r0 = (r5 > r12 ? 1 : (r5 == r12 ? 0 : -1))
                if (r0 <= 0) goto L_0x055b
            L_0x054d:
                r0 = 1
            L_0x054e:
                r5 = 2
                int r0 = r0 * 2
                int r5 = r0 * 2
                float r5 = (float) r5     // Catch:{ all -> 0x055f }
                int r5 = (r5 > r8 ? 1 : (r5 == r8 ? 0 : -1))
                if (r5 < 0) goto L_0x054e
                r7.inSampleSize = r0     // Catch:{ all -> 0x055f }
                goto L_0x056d
            L_0x055b:
                int r0 = (int) r8     // Catch:{ all -> 0x055f }
                r7.inSampleSize = r0     // Catch:{ all -> 0x055f }
                goto L_0x056d
            L_0x055f:
                r0 = move-exception
                r10 = r0
                r0 = r12
                r12 = r25
                r5 = r29
                r8 = r30
                goto L_0x058e
            L_0x0569:
                r29 = r5
                r30 = r8
            L_0x056d:
                r5 = r29
                r8 = r30
                r0 = 0
                goto L_0x05fe
            L_0x0574:
                r0 = move-exception
                r29 = r5
                r30 = r8
            L_0x0579:
                r10 = r0
                r0 = r12
                r12 = r25
                goto L_0x058e
            L_0x057e:
                r0 = move-exception
                r30 = r8
                r10 = r0
                r0 = r12
                r12 = r25
                r5 = 0
                goto L_0x058e
            L_0x0587:
                r0 = move-exception
                r10 = r0
                r0 = r12
                r12 = r25
            L_0x058c:
                r5 = 0
                r8 = 0
            L_0x058e:
                r9 = 0
                goto L_0x0609
            L_0x0591:
                if (r11 == 0) goto L_0x05f8
                r5 = 1
                r7.inJustDecodeBounds = r5     // Catch:{ all -> 0x0602 }
                if (r13 == 0) goto L_0x059b
                android.graphics.Bitmap$Config r0 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ all -> 0x0602 }
                goto L_0x059d
            L_0x059b:
                android.graphics.Bitmap$Config r0 = android.graphics.Bitmap.Config.RGB_565     // Catch:{ all -> 0x0602 }
            L_0x059d:
                r7.inPreferredConfig = r0     // Catch:{ all -> 0x0602 }
                java.io.FileInputStream r0 = new java.io.FileInputStream     // Catch:{ all -> 0x0602 }
                r0.<init>(r2)     // Catch:{ all -> 0x0602 }
                r5 = 0
                android.graphics.Bitmap r8 = android.graphics.BitmapFactory.decodeStream(r0, r5, r7)     // Catch:{ all -> 0x0602 }
                r0.close()     // Catch:{ all -> 0x05f1 }
                int r0 = r7.outWidth     // Catch:{ all -> 0x05f1 }
                int r5 = r7.outHeight     // Catch:{ all -> 0x05f1 }
                r9 = 0
                r7.inJustDecodeBounds = r9     // Catch:{ all -> 0x05f1 }
                r9 = 66
                android.graphics.Point r10 = org.telegram.messenger.AndroidUtilities.getRealScreenSize()     // Catch:{ all -> 0x05f1 }
                int r10 = r10.x     // Catch:{ all -> 0x05f1 }
                android.graphics.Point r12 = org.telegram.messenger.AndroidUtilities.getRealScreenSize()     // Catch:{ all -> 0x05f1 }
                int r12 = r12.y     // Catch:{ all -> 0x05f1 }
                int r10 = java.lang.Math.min(r10, r12)     // Catch:{ all -> 0x05f1 }
                int r9 = java.lang.Math.max(r9, r10)     // Catch:{ all -> 0x05f1 }
                int r0 = java.lang.Math.min(r5, r0)     // Catch:{ all -> 0x05f1 }
                float r0 = (float) r0     // Catch:{ all -> 0x05f1 }
                float r5 = (float) r9     // Catch:{ all -> 0x05f1 }
                float r0 = r0 / r5
                r5 = 1086324736(0x40CLASSNAME, float:6.0)
                float r0 = r0 * r5
                int r5 = (r0 > r23 ? 1 : (r0 == r23 ? 0 : -1))
                if (r5 >= 0) goto L_0x05da
                r0 = 1065353216(0x3var_, float:1.0)
            L_0x05da:
                int r5 = (r0 > r23 ? 1 : (r0 == r23 ? 0 : -1))
                if (r5 <= 0) goto L_0x05ec
                r5 = 1
            L_0x05df:
                r9 = 2
                int r5 = r5 * 2
                int r9 = r5 * 2
                float r9 = (float) r9     // Catch:{ all -> 0x05f1 }
                int r9 = (r9 > r0 ? 1 : (r9 == r0 ? 0 : -1))
                if (r9 <= 0) goto L_0x05df
                r7.inSampleSize = r5     // Catch:{ all -> 0x05f1 }
                goto L_0x05ef
            L_0x05ec:
                int r0 = (int) r0     // Catch:{ all -> 0x05f1 }
                r7.inSampleSize = r0     // Catch:{ all -> 0x05f1 }
            L_0x05ef:
                r0 = r8
                goto L_0x05f9
            L_0x05f1:
                r0 = move-exception
                r10 = r0
                r9 = r8
                r0 = 0
                r5 = 0
                r8 = 0
                goto L_0x0608
            L_0x05f8:
                r0 = 0
            L_0x05f9:
                r5 = 0
                r8 = 0
                r12 = 0
                r25 = 0
            L_0x05fe:
                r9 = r0
                r0 = r25
                goto L_0x0611
            L_0x0602:
                r0 = move-exception
                r10 = r0
                r0 = 0
                r5 = 0
                r8 = 0
                r9 = 0
            L_0x0608:
                r12 = 0
            L_0x0609:
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r10)
                r34 = r12
                r12 = r0
                r0 = r34
            L_0x0611:
                org.telegram.messenger.ImageLoader$CacheImage r10 = r1.cacheImage
                int r10 = r10.type
                r25 = 1101004800(0x41a00000, float:20.0)
                r29 = r9
                r9 = 1
                if (r10 != r9) goto L_0x081d
                org.telegram.messenger.ImageLoader r9 = org.telegram.messenger.ImageLoader.this     // Catch:{ all -> 0x0812 }
                long r10 = android.os.SystemClock.elapsedRealtime()     // Catch:{ all -> 0x0812 }
                long unused = r9.lastCacheOutTime = r10     // Catch:{ all -> 0x0812 }
                java.lang.Object r9 = r1.sync     // Catch:{ all -> 0x0812 }
                monitor-enter(r9)     // Catch:{ all -> 0x0812 }
                boolean r10 = r1.isCancelled     // Catch:{ all -> 0x080f }
                if (r10 == 0) goto L_0x062e
                monitor-exit(r9)     // Catch:{ all -> 0x080f }
                return
            L_0x062e:
                monitor-exit(r9)     // Catch:{ all -> 0x080f }
                if (r15 == 0) goto L_0x0676
                java.io.RandomAccessFile r3 = new java.io.RandomAccessFile     // Catch:{ all -> 0x0812 }
                java.lang.String r4 = "r"
                r3.<init>(r2, r4)     // Catch:{ all -> 0x0812 }
                java.nio.channels.FileChannel r9 = r3.getChannel()     // Catch:{ all -> 0x0812 }
                java.nio.channels.FileChannel$MapMode r10 = java.nio.channels.FileChannel.MapMode.READ_ONLY     // Catch:{ all -> 0x0812 }
                r11 = 0
                long r13 = r2.length()     // Catch:{ all -> 0x0812 }
                java.nio.MappedByteBuffer r4 = r9.map(r10, r11, r13)     // Catch:{ all -> 0x0812 }
                android.graphics.BitmapFactory$Options r6 = new android.graphics.BitmapFactory$Options     // Catch:{ all -> 0x0812 }
                r6.<init>()     // Catch:{ all -> 0x0812 }
                r9 = 1
                r6.inJustDecodeBounds = r9     // Catch:{ all -> 0x0812 }
                int r10 = r4.limit()     // Catch:{ all -> 0x0812 }
                r11 = 0
                org.telegram.messenger.Utilities.loadWebpImage(r11, r4, r10, r6, r9)     // Catch:{ all -> 0x0812 }
                int r9 = r6.outWidth     // Catch:{ all -> 0x0812 }
                int r6 = r6.outHeight     // Catch:{ all -> 0x0812 }
                android.graphics.Bitmap$Config r10 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ all -> 0x0812 }
                android.graphics.Bitmap r9 = org.telegram.messenger.Bitmaps.createBitmap(r9, r6, r10)     // Catch:{ all -> 0x0812 }
                int r6 = r4.limit()     // Catch:{ all -> 0x0697 }
                boolean r10 = r7.inPurgeable     // Catch:{ all -> 0x0697 }
                if (r10 != 0) goto L_0x066c
                r10 = 1
                goto L_0x066d
            L_0x066c:
                r10 = 0
            L_0x066d:
                r11 = 0
                org.telegram.messenger.Utilities.loadWebpImage(r9, r4, r6, r11, r10)     // Catch:{ all -> 0x0697 }
                r3.close()     // Catch:{ all -> 0x0697 }
                goto L_0x06fa
            L_0x0676:
                boolean r9 = r7.inPurgeable     // Catch:{ all -> 0x0812 }
                if (r9 != 0) goto L_0x069a
                if (r4 == 0) goto L_0x067d
                goto L_0x069a
            L_0x067d:
                if (r3 == 0) goto L_0x0689
                org.telegram.messenger.secretmedia.EncryptedFileInputStream r3 = new org.telegram.messenger.secretmedia.EncryptedFileInputStream     // Catch:{ all -> 0x0812 }
                org.telegram.messenger.ImageLoader$CacheImage r4 = r1.cacheImage     // Catch:{ all -> 0x0812 }
                java.io.File r4 = r4.encryptionKeyPath     // Catch:{ all -> 0x0812 }
                r3.<init>((java.io.File) r2, (java.io.File) r4)     // Catch:{ all -> 0x0812 }
                goto L_0x068e
            L_0x0689:
                java.io.FileInputStream r3 = new java.io.FileInputStream     // Catch:{ all -> 0x0812 }
                r3.<init>(r2)     // Catch:{ all -> 0x0812 }
            L_0x068e:
                r4 = 0
                android.graphics.Bitmap r9 = android.graphics.BitmapFactory.decodeStream(r3, r4, r7)     // Catch:{ all -> 0x0812 }
                r3.close()     // Catch:{ all -> 0x0697 }
                goto L_0x06fa
            L_0x0697:
                r0 = move-exception
                goto L_0x0815
            L_0x069a:
                java.io.RandomAccessFile r9 = new java.io.RandomAccessFile     // Catch:{ all -> 0x0812 }
                java.lang.String r10 = "r"
                r9.<init>(r2, r10)     // Catch:{ all -> 0x0812 }
                long r10 = r9.length()     // Catch:{ all -> 0x0812 }
                int r11 = (int) r10     // Catch:{ all -> 0x0812 }
                java.lang.ThreadLocal r10 = org.telegram.messenger.ImageLoader.bytesThumbLocal     // Catch:{ all -> 0x0812 }
                java.lang.Object r10 = r10.get()     // Catch:{ all -> 0x0812 }
                byte[] r10 = (byte[]) r10     // Catch:{ all -> 0x0812 }
                if (r10 == 0) goto L_0x06b6
                int r12 = r10.length     // Catch:{ all -> 0x0812 }
                if (r12 < r11) goto L_0x06b6
                goto L_0x06b7
            L_0x06b6:
                r10 = 0
            L_0x06b7:
                if (r10 != 0) goto L_0x06c2
                byte[] r10 = new byte[r11]     // Catch:{ all -> 0x0812 }
                java.lang.ThreadLocal r12 = org.telegram.messenger.ImageLoader.bytesThumbLocal     // Catch:{ all -> 0x0812 }
                r12.set(r10)     // Catch:{ all -> 0x0812 }
            L_0x06c2:
                r12 = 0
                r9.readFully(r10, r12, r11)     // Catch:{ all -> 0x0812 }
                r9.close()     // Catch:{ all -> 0x0812 }
                if (r4 == 0) goto L_0x06e5
                org.telegram.messenger.secretmedia.EncryptedFileInputStream.decryptBytesWithKeyFile((byte[]) r10, (int) r12, (int) r11, (org.telegram.messenger.SecureDocumentKey) r4)     // Catch:{ all -> 0x0812 }
                byte[] r3 = org.telegram.messenger.Utilities.computeSHA256(r10, r12, r11)     // Catch:{ all -> 0x0812 }
                if (r6 == 0) goto L_0x06dd
                boolean r3 = java.util.Arrays.equals(r3, r6)     // Catch:{ all -> 0x0812 }
                if (r3 != 0) goto L_0x06db
                goto L_0x06dd
            L_0x06db:
                r3 = 0
                goto L_0x06de
            L_0x06dd:
                r3 = 1
            L_0x06de:
                r4 = 0
                byte r6 = r10[r4]     // Catch:{ all -> 0x0812 }
                r4 = r6 & 255(0xff, float:3.57E-43)
                int r11 = r11 - r4
                goto L_0x06f1
            L_0x06e5:
                if (r3 == 0) goto L_0x06ef
                org.telegram.messenger.ImageLoader$CacheImage r3 = r1.cacheImage     // Catch:{ all -> 0x0812 }
                java.io.File r3 = r3.encryptionKeyPath     // Catch:{ all -> 0x0812 }
                r4 = 0
                org.telegram.messenger.secretmedia.EncryptedFileInputStream.decryptBytesWithKeyFile((byte[]) r10, (int) r4, (int) r11, (java.io.File) r3)     // Catch:{ all -> 0x0812 }
            L_0x06ef:
                r3 = 0
                r4 = 0
            L_0x06f1:
                if (r3 != 0) goto L_0x06f8
                android.graphics.Bitmap r9 = android.graphics.BitmapFactory.decodeByteArray(r10, r4, r11, r7)     // Catch:{ all -> 0x0812 }
                goto L_0x06fa
            L_0x06f8:
                r9 = r29
            L_0x06fa:
                if (r9 != 0) goto L_0x0712
                long r3 = r2.length()     // Catch:{ all -> 0x0697 }
                r5 = 0
                int r0 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
                if (r0 == 0) goto L_0x070c
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage     // Catch:{ all -> 0x0697 }
                java.lang.String r0 = r0.filter     // Catch:{ all -> 0x0697 }
                if (r0 != 0) goto L_0x070f
            L_0x070c:
                r2.delete()     // Catch:{ all -> 0x0697 }
            L_0x070f:
                r2 = 0
                goto L_0x0819
            L_0x0712:
                org.telegram.messenger.ImageLoader$CacheImage r2 = r1.cacheImage     // Catch:{ all -> 0x0697 }
                java.lang.String r2 = r2.filter     // Catch:{ all -> 0x0697 }
                if (r2 == 0) goto L_0x0743
                int r2 = r9.getWidth()     // Catch:{ all -> 0x0697 }
                float r2 = (float) r2     // Catch:{ all -> 0x0697 }
                int r3 = r9.getHeight()     // Catch:{ all -> 0x0697 }
                float r3 = (float) r3     // Catch:{ all -> 0x0697 }
                boolean r4 = r7.inPurgeable     // Catch:{ all -> 0x0697 }
                if (r4 != 0) goto L_0x0743
                int r4 = (r0 > r22 ? 1 : (r0 == r22 ? 0 : -1))
                if (r4 == 0) goto L_0x0743
                int r4 = (r2 > r0 ? 1 : (r2 == r0 ? 0 : -1))
                if (r4 == 0) goto L_0x0743
                float r25 = r0 + r25
                int r4 = (r2 > r25 ? 1 : (r2 == r25 ? 0 : -1))
                if (r4 <= 0) goto L_0x0743
                float r2 = r2 / r0
                int r0 = (int) r0     // Catch:{ all -> 0x0697 }
                float r3 = r3 / r2
                int r2 = (int) r3     // Catch:{ all -> 0x0697 }
                r3 = 1
                android.graphics.Bitmap r0 = org.telegram.messenger.Bitmaps.createScaledBitmap(r9, r0, r2, r3)     // Catch:{ all -> 0x0697 }
                if (r9 == r0) goto L_0x0743
                r9.recycle()     // Catch:{ all -> 0x0697 }
                r9 = r0
            L_0x0743:
                if (r5 == 0) goto L_0x0763
                boolean r0 = r7.inPurgeable     // Catch:{ all -> 0x0697 }
                if (r0 == 0) goto L_0x074b
                r0 = 0
                goto L_0x074c
            L_0x074b:
                r0 = 1
            L_0x074c:
                int r2 = r9.getWidth()     // Catch:{ all -> 0x0697 }
                int r3 = r9.getHeight()     // Catch:{ all -> 0x0697 }
                int r4 = r9.getRowBytes()     // Catch:{ all -> 0x0697 }
                int r0 = org.telegram.messenger.Utilities.needInvert(r9, r0, r2, r3, r4)     // Catch:{ all -> 0x0697 }
                if (r0 == 0) goto L_0x0760
                r0 = 1
                goto L_0x0761
            L_0x0760:
                r0 = 0
            L_0x0761:
                r2 = r0
                goto L_0x0764
            L_0x0763:
                r2 = 0
            L_0x0764:
                r3 = 1
                if (r8 != r3) goto L_0x078c
                android.graphics.Bitmap$Config r0 = r9.getConfig()     // Catch:{ all -> 0x0789 }
                android.graphics.Bitmap$Config r3 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ all -> 0x0789 }
                if (r0 != r3) goto L_0x0819
                r11 = 3
                boolean r0 = r7.inPurgeable     // Catch:{ all -> 0x0789 }
                if (r0 == 0) goto L_0x0776
                r12 = 0
                goto L_0x0777
            L_0x0776:
                r12 = 1
            L_0x0777:
                int r13 = r9.getWidth()     // Catch:{ all -> 0x0789 }
                int r14 = r9.getHeight()     // Catch:{ all -> 0x0789 }
                int r15 = r9.getRowBytes()     // Catch:{ all -> 0x0789 }
                r10 = r9
                org.telegram.messenger.Utilities.blurBitmap(r10, r11, r12, r13, r14, r15)     // Catch:{ all -> 0x0789 }
                goto L_0x0819
            L_0x0789:
                r0 = move-exception
                goto L_0x0816
            L_0x078c:
                r3 = 2
                if (r8 != r3) goto L_0x07b1
                android.graphics.Bitmap$Config r0 = r9.getConfig()     // Catch:{ all -> 0x0789 }
                android.graphics.Bitmap$Config r3 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ all -> 0x0789 }
                if (r0 != r3) goto L_0x0819
                r11 = 1
                boolean r0 = r7.inPurgeable     // Catch:{ all -> 0x0789 }
                if (r0 == 0) goto L_0x079e
                r12 = 0
                goto L_0x079f
            L_0x079e:
                r12 = 1
            L_0x079f:
                int r13 = r9.getWidth()     // Catch:{ all -> 0x0789 }
                int r14 = r9.getHeight()     // Catch:{ all -> 0x0789 }
                int r15 = r9.getRowBytes()     // Catch:{ all -> 0x0789 }
                r10 = r9
                org.telegram.messenger.Utilities.blurBitmap(r10, r11, r12, r13, r14, r15)     // Catch:{ all -> 0x0789 }
                goto L_0x0819
            L_0x07b1:
                r3 = 3
                if (r8 != r3) goto L_0x0805
                android.graphics.Bitmap$Config r0 = r9.getConfig()     // Catch:{ all -> 0x0789 }
                android.graphics.Bitmap$Config r3 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ all -> 0x0789 }
                if (r0 != r3) goto L_0x0819
                r11 = 7
                boolean r0 = r7.inPurgeable     // Catch:{ all -> 0x0789 }
                if (r0 == 0) goto L_0x07c3
                r12 = 0
                goto L_0x07c4
            L_0x07c3:
                r12 = 1
            L_0x07c4:
                int r13 = r9.getWidth()     // Catch:{ all -> 0x0789 }
                int r14 = r9.getHeight()     // Catch:{ all -> 0x0789 }
                int r15 = r9.getRowBytes()     // Catch:{ all -> 0x0789 }
                r10 = r9
                org.telegram.messenger.Utilities.blurBitmap(r10, r11, r12, r13, r14, r15)     // Catch:{ all -> 0x0789 }
                r11 = 7
                boolean r0 = r7.inPurgeable     // Catch:{ all -> 0x0789 }
                if (r0 == 0) goto L_0x07db
                r12 = 0
                goto L_0x07dc
            L_0x07db:
                r12 = 1
            L_0x07dc:
                int r13 = r9.getWidth()     // Catch:{ all -> 0x0789 }
                int r14 = r9.getHeight()     // Catch:{ all -> 0x0789 }
                int r15 = r9.getRowBytes()     // Catch:{ all -> 0x0789 }
                r10 = r9
                org.telegram.messenger.Utilities.blurBitmap(r10, r11, r12, r13, r14, r15)     // Catch:{ all -> 0x0789 }
                r11 = 7
                boolean r0 = r7.inPurgeable     // Catch:{ all -> 0x0789 }
                if (r0 == 0) goto L_0x07f3
                r12 = 0
                goto L_0x07f4
            L_0x07f3:
                r12 = 1
            L_0x07f4:
                int r13 = r9.getWidth()     // Catch:{ all -> 0x0789 }
                int r14 = r9.getHeight()     // Catch:{ all -> 0x0789 }
                int r15 = r9.getRowBytes()     // Catch:{ all -> 0x0789 }
                r10 = r9
                org.telegram.messenger.Utilities.blurBitmap(r10, r11, r12, r13, r14, r15)     // Catch:{ all -> 0x0789 }
                goto L_0x0819
            L_0x0805:
                if (r8 != 0) goto L_0x0819
                boolean r0 = r7.inPurgeable     // Catch:{ all -> 0x0789 }
                if (r0 == 0) goto L_0x0819
                org.telegram.messenger.Utilities.pinBitmap(r9)     // Catch:{ all -> 0x0789 }
                goto L_0x0819
            L_0x080f:
                r0 = move-exception
                monitor-exit(r9)     // Catch:{ all -> 0x080f }
                throw r0     // Catch:{ all -> 0x0812 }
            L_0x0812:
                r0 = move-exception
                r9 = r29
            L_0x0815:
                r2 = 0
            L_0x0816:
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            L_0x0819:
                r4 = 0
                r10 = 0
                goto L_0x0ad2
            L_0x081d:
                r9 = 20
                if (r17 == 0) goto L_0x0822
                r9 = 0
            L_0x0822:
                if (r9 == 0) goto L_0x0855
                org.telegram.messenger.ImageLoader r10 = org.telegram.messenger.ImageLoader.this     // Catch:{ all -> 0x084f }
                long r30 = r10.lastCacheOutTime     // Catch:{ all -> 0x084f }
                r20 = 0
                int r10 = (r30 > r20 ? 1 : (r30 == r20 ? 0 : -1))
                if (r10 == 0) goto L_0x0855
                org.telegram.messenger.ImageLoader r10 = org.telegram.messenger.ImageLoader.this     // Catch:{ all -> 0x084f }
                long r30 = r10.lastCacheOutTime     // Catch:{ all -> 0x084f }
                long r32 = android.os.SystemClock.elapsedRealtime()     // Catch:{ all -> 0x084f }
                long r9 = (long) r9     // Catch:{ all -> 0x084f }
                long r32 = r32 - r9
                int r26 = (r30 > r32 ? 1 : (r30 == r32 ? 0 : -1))
                if (r26 <= 0) goto L_0x0855
                r26 = r5
                int r5 = android.os.Build.VERSION.SDK_INT     // Catch:{ all -> 0x084f }
                r30 = r12
                r12 = 21
                if (r5 >= r12) goto L_0x0859
                java.lang.Thread.sleep(r9)     // Catch:{ all -> 0x084f }
                goto L_0x0859
            L_0x084f:
                r9 = r29
            L_0x0851:
                r4 = 0
            L_0x0852:
                r10 = 0
                goto L_0x0ace
            L_0x0855:
                r26 = r5
                r30 = r12
            L_0x0859:
                org.telegram.messenger.ImageLoader r5 = org.telegram.messenger.ImageLoader.this     // Catch:{ all -> 0x0aca }
                long r9 = android.os.SystemClock.elapsedRealtime()     // Catch:{ all -> 0x0aca }
                long unused = r5.lastCacheOutTime = r9     // Catch:{ all -> 0x0aca }
                java.lang.Object r5 = r1.sync     // Catch:{ all -> 0x0aca }
                monitor-enter(r5)     // Catch:{ all -> 0x0aca }
                boolean r9 = r1.isCancelled     // Catch:{ all -> 0x0ac4 }
                if (r9 == 0) goto L_0x086b
                monitor-exit(r5)     // Catch:{ all -> 0x0ac4 }
                return
            L_0x086b:
                monitor-exit(r5)     // Catch:{ all -> 0x0ac4 }
                if (r13 != 0) goto L_0x0882
                org.telegram.messenger.ImageLoader$CacheImage r5 = r1.cacheImage     // Catch:{ all -> 0x084f }
                java.lang.String r9 = r5.filter     // Catch:{ all -> 0x084f }
                if (r9 == 0) goto L_0x0882
                if (r8 != 0) goto L_0x0882
                org.telegram.messenger.ImageLocation r5 = r5.imageLocation     // Catch:{ all -> 0x084f }
                java.lang.String r5 = r5.path     // Catch:{ all -> 0x084f }
                if (r5 == 0) goto L_0x087d
                goto L_0x0882
            L_0x087d:
                android.graphics.Bitmap$Config r5 = android.graphics.Bitmap.Config.RGB_565     // Catch:{ all -> 0x084f }
                r7.inPreferredConfig = r5     // Catch:{ all -> 0x084f }
                goto L_0x0886
            L_0x0882:
                android.graphics.Bitmap$Config r5 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ all -> 0x0aca }
                r7.inPreferredConfig = r5     // Catch:{ all -> 0x0aca }
            L_0x0886:
                r5 = 0
                r7.inDither = r5     // Catch:{ all -> 0x0aca }
                if (r17 == 0) goto L_0x08af
                if (r11 != 0) goto L_0x08af
                if (r14 == 0) goto L_0x089f
                android.content.Context r5 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x084f }
                android.content.ContentResolver r5 = r5.getContentResolver()     // Catch:{ all -> 0x084f }
                long r9 = r17.longValue()     // Catch:{ all -> 0x084f }
                r11 = 1
                android.graphics.Bitmap r9 = android.provider.MediaStore.Video.Thumbnails.getThumbnail(r5, r9, r11, r7)     // Catch:{ all -> 0x084f }
                goto L_0x08b1
            L_0x089f:
                android.content.Context r5 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x084f }
                android.content.ContentResolver r5 = r5.getContentResolver()     // Catch:{ all -> 0x084f }
                long r9 = r17.longValue()     // Catch:{ all -> 0x084f }
                r11 = 1
                android.graphics.Bitmap r9 = android.provider.MediaStore.Images.Thumbnails.getThumbnail(r5, r9, r11, r7)     // Catch:{ all -> 0x084f }
                goto L_0x08b1
            L_0x08af:
                r9 = r29
            L_0x08b1:
                if (r9 != 0) goto L_0x09b8
                if (r15 == 0) goto L_0x08ff
                java.io.RandomAccessFile r3 = new java.io.RandomAccessFile     // Catch:{ all -> 0x0851 }
                java.lang.String r4 = "r"
                r3.<init>(r2, r4)     // Catch:{ all -> 0x0851 }
                java.nio.channels.FileChannel r10 = r3.getChannel()     // Catch:{ all -> 0x0851 }
                java.nio.channels.FileChannel$MapMode r11 = java.nio.channels.FileChannel.MapMode.READ_ONLY     // Catch:{ all -> 0x0851 }
                r12 = 0
                long r14 = r2.length()     // Catch:{ all -> 0x0851 }
                java.nio.MappedByteBuffer r4 = r10.map(r11, r12, r14)     // Catch:{ all -> 0x0851 }
                android.graphics.BitmapFactory$Options r5 = new android.graphics.BitmapFactory$Options     // Catch:{ all -> 0x0851 }
                r5.<init>()     // Catch:{ all -> 0x0851 }
                r6 = 1
                r5.inJustDecodeBounds = r6     // Catch:{ all -> 0x0851 }
                int r10 = r4.limit()     // Catch:{ all -> 0x0851 }
                r11 = 0
                org.telegram.messenger.Utilities.loadWebpImage(r11, r4, r10, r5, r6)     // Catch:{ all -> 0x08fc }
                int r6 = r5.outWidth     // Catch:{ all -> 0x0851 }
                int r5 = r5.outHeight     // Catch:{ all -> 0x0851 }
                android.graphics.Bitmap$Config r10 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ all -> 0x0851 }
                android.graphics.Bitmap r9 = org.telegram.messenger.Bitmaps.createBitmap(r6, r5, r10)     // Catch:{ all -> 0x0851 }
                int r5 = r4.limit()     // Catch:{ all -> 0x0851 }
                boolean r6 = r7.inPurgeable     // Catch:{ all -> 0x0851 }
                if (r6 != 0) goto L_0x08f0
                r6 = 1
                goto L_0x08f1
            L_0x08f0:
                r6 = 0
            L_0x08f1:
                r10 = 0
                org.telegram.messenger.Utilities.loadWebpImage(r9, r4, r5, r10, r6)     // Catch:{ all -> 0x0acd }
                r3.close()     // Catch:{ all -> 0x0851 }
                r4 = 0
                r10 = 0
                goto L_0x09ba
            L_0x08fc:
                r10 = r11
                goto L_0x0acd
            L_0x08ff:
                boolean r5 = r7.inPurgeable     // Catch:{ all -> 0x09b5 }
                if (r5 != 0) goto L_0x0956
                if (r4 == 0) goto L_0x0906
                goto L_0x0956
            L_0x0906:
                if (r3 == 0) goto L_0x0912
                org.telegram.messenger.secretmedia.EncryptedFileInputStream r3 = new org.telegram.messenger.secretmedia.EncryptedFileInputStream     // Catch:{ all -> 0x0851 }
                org.telegram.messenger.ImageLoader$CacheImage r4 = r1.cacheImage     // Catch:{ all -> 0x0851 }
                java.io.File r4 = r4.encryptionKeyPath     // Catch:{ all -> 0x0851 }
                r3.<init>((java.io.File) r2, (java.io.File) r4)     // Catch:{ all -> 0x0851 }
                goto L_0x0917
            L_0x0912:
                java.io.FileInputStream r3 = new java.io.FileInputStream     // Catch:{ all -> 0x09b5 }
                r3.<init>(r2)     // Catch:{ all -> 0x09b5 }
            L_0x0917:
                org.telegram.messenger.ImageLoader$CacheImage r4 = r1.cacheImage     // Catch:{ all -> 0x09b5 }
                org.telegram.messenger.ImageLocation r4 = r4.imageLocation     // Catch:{ all -> 0x09b5 }
                org.telegram.tgnet.TLRPC$Document r4 = r4.document     // Catch:{ all -> 0x09b5 }
                boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC$TL_document     // Catch:{ all -> 0x09b5 }
                if (r4 == 0) goto L_0x094c
                androidx.exifinterface.media.ExifInterface r4 = new androidx.exifinterface.media.ExifInterface     // Catch:{ all -> 0x0941 }
                r4.<init>((java.io.InputStream) r3)     // Catch:{ all -> 0x0941 }
                java.lang.String r5 = "Orientation"
                r6 = 1
                int r4 = r4.getAttributeInt(r5, r6)     // Catch:{ all -> 0x0941 }
                r5 = 3
                if (r4 == r5) goto L_0x093e
                r5 = 6
                if (r4 == r5) goto L_0x093b
                r5 = 8
                if (r4 == r5) goto L_0x0938
                goto L_0x0941
            L_0x0938:
                r4 = 270(0x10e, float:3.78E-43)
                goto L_0x0942
            L_0x093b:
                r4 = 90
                goto L_0x0942
            L_0x093e:
                r4 = 180(0xb4, float:2.52E-43)
                goto L_0x0942
            L_0x0941:
                r4 = 0
            L_0x0942:
                java.nio.channels.FileChannel r5 = r3.getChannel()     // Catch:{ all -> 0x0852 }
                r10 = 0
                r5.position(r10)     // Catch:{ all -> 0x0852 }
                goto L_0x094d
            L_0x094c:
                r4 = 0
            L_0x094d:
                r10 = 0
                android.graphics.Bitmap r9 = android.graphics.BitmapFactory.decodeStream(r3, r10, r7)     // Catch:{ all -> 0x0ace }
                r3.close()     // Catch:{ all -> 0x0ace }
                goto L_0x09ba
            L_0x0956:
                r10 = 0
                java.io.RandomAccessFile r5 = new java.io.RandomAccessFile     // Catch:{ all -> 0x0acd }
                java.lang.String r11 = "r"
                r5.<init>(r2, r11)     // Catch:{ all -> 0x0acd }
                long r11 = r5.length()     // Catch:{ all -> 0x0acd }
                int r12 = (int) r11     // Catch:{ all -> 0x0acd }
                java.lang.ThreadLocal r11 = org.telegram.messenger.ImageLoader.bytesLocal     // Catch:{ all -> 0x0acd }
                java.lang.Object r11 = r11.get()     // Catch:{ all -> 0x0acd }
                byte[] r11 = (byte[]) r11     // Catch:{ all -> 0x0acd }
                if (r11 == 0) goto L_0x0973
                int r13 = r11.length     // Catch:{ all -> 0x0acd }
                if (r13 < r12) goto L_0x0973
                goto L_0x0974
            L_0x0973:
                r11 = r10
            L_0x0974:
                if (r11 != 0) goto L_0x097f
                byte[] r11 = new byte[r12]     // Catch:{ all -> 0x0acd }
                java.lang.ThreadLocal r13 = org.telegram.messenger.ImageLoader.bytesLocal     // Catch:{ all -> 0x0acd }
                r13.set(r11)     // Catch:{ all -> 0x0acd }
            L_0x097f:
                r13 = 0
                r5.readFully(r11, r13, r12)     // Catch:{ all -> 0x0acd }
                r5.close()     // Catch:{ all -> 0x0acd }
                if (r4 == 0) goto L_0x09a2
                org.telegram.messenger.secretmedia.EncryptedFileInputStream.decryptBytesWithKeyFile((byte[]) r11, (int) r13, (int) r12, (org.telegram.messenger.SecureDocumentKey) r4)     // Catch:{ all -> 0x0acd }
                byte[] r3 = org.telegram.messenger.Utilities.computeSHA256(r11, r13, r12)     // Catch:{ all -> 0x0acd }
                if (r6 == 0) goto L_0x099a
                boolean r3 = java.util.Arrays.equals(r3, r6)     // Catch:{ all -> 0x0acd }
                if (r3 != 0) goto L_0x0998
                goto L_0x099a
            L_0x0998:
                r3 = 0
                goto L_0x099b
            L_0x099a:
                r3 = 1
            L_0x099b:
                r4 = 0
                byte r5 = r11[r4]     // Catch:{ all -> 0x0acd }
                r4 = r5 & 255(0xff, float:3.57E-43)
                int r12 = r12 - r4
                goto L_0x09ae
            L_0x09a2:
                if (r3 == 0) goto L_0x09ac
                org.telegram.messenger.ImageLoader$CacheImage r3 = r1.cacheImage     // Catch:{ all -> 0x0acd }
                java.io.File r3 = r3.encryptionKeyPath     // Catch:{ all -> 0x0acd }
                r4 = 0
                org.telegram.messenger.secretmedia.EncryptedFileInputStream.decryptBytesWithKeyFile((byte[]) r11, (int) r4, (int) r12, (java.io.File) r3)     // Catch:{ all -> 0x0acd }
            L_0x09ac:
                r3 = 0
                r4 = 0
            L_0x09ae:
                if (r3 != 0) goto L_0x09b9
                android.graphics.Bitmap r9 = android.graphics.BitmapFactory.decodeByteArray(r11, r4, r12, r7)     // Catch:{ all -> 0x0acd }
                goto L_0x09b9
            L_0x09b5:
                r10 = 0
                goto L_0x0acd
            L_0x09b8:
                r10 = 0
            L_0x09b9:
                r4 = 0
            L_0x09ba:
                if (r9 != 0) goto L_0x09d4
                if (r18 == 0) goto L_0x09d1
                long r5 = r2.length()     // Catch:{ all -> 0x0ace }
                r7 = 0
                int r0 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1))
                if (r0 == 0) goto L_0x09ce
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage     // Catch:{ all -> 0x0ace }
                java.lang.String r0 = r0.filter     // Catch:{ all -> 0x0ace }
                if (r0 != 0) goto L_0x09d1
            L_0x09ce:
                r2.delete()     // Catch:{ all -> 0x0ace }
            L_0x09d1:
                r5 = 0
                goto L_0x0ac2
            L_0x09d4:
                org.telegram.messenger.ImageLoader$CacheImage r2 = r1.cacheImage     // Catch:{ all -> 0x0ace }
                java.lang.String r2 = r2.filter     // Catch:{ all -> 0x0ace }
                if (r2 == 0) goto L_0x0ab0
                int r2 = r9.getWidth()     // Catch:{ all -> 0x0ace }
                float r2 = (float) r2     // Catch:{ all -> 0x0ace }
                int r3 = r9.getHeight()     // Catch:{ all -> 0x0ace }
                float r3 = (float) r3     // Catch:{ all -> 0x0ace }
                boolean r5 = r7.inPurgeable     // Catch:{ all -> 0x0ace }
                if (r5 != 0) goto L_0x0a27
                int r5 = (r0 > r22 ? 1 : (r0 == r22 ? 0 : -1))
                if (r5 == 0) goto L_0x0a27
                int r5 = (r2 > r0 ? 1 : (r2 == r0 ? 0 : -1))
                if (r5 == 0) goto L_0x0a27
                float r25 = r0 + r25
                int r5 = (r2 > r25 ? 1 : (r2 == r25 ? 0 : -1))
                if (r5 <= 0) goto L_0x0a27
                int r5 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
                if (r5 <= 0) goto L_0x0a0e
                int r5 = (r0 > r30 ? 1 : (r0 == r30 ? 0 : -1))
                if (r5 <= 0) goto L_0x0a0e
                float r5 = r2 / r0
                int r6 = (r5 > r23 ? 1 : (r5 == r23 ? 0 : -1))
                if (r6 <= 0) goto L_0x0a20
                int r0 = (int) r0     // Catch:{ all -> 0x0ace }
                float r5 = r3 / r5
                int r5 = (int) r5     // Catch:{ all -> 0x0ace }
                r6 = 1
                android.graphics.Bitmap r0 = org.telegram.messenger.Bitmaps.createScaledBitmap(r9, r0, r5, r6)     // Catch:{ all -> 0x0ace }
                goto L_0x0a21
            L_0x0a0e:
                float r0 = r3 / r30
                int r5 = (r0 > r23 ? 1 : (r0 == r23 ? 0 : -1))
                if (r5 <= 0) goto L_0x0a20
                float r0 = r2 / r0
                int r0 = (int) r0     // Catch:{ all -> 0x0ace }
                r12 = r30
                int r5 = (int) r12     // Catch:{ all -> 0x0ace }
                r6 = 1
                android.graphics.Bitmap r0 = org.telegram.messenger.Bitmaps.createScaledBitmap(r9, r0, r5, r6)     // Catch:{ all -> 0x0ace }
                goto L_0x0a21
            L_0x0a20:
                r0 = r9
            L_0x0a21:
                if (r9 == r0) goto L_0x0a27
                r9.recycle()     // Catch:{ all -> 0x0ace }
                r9 = r0
            L_0x0a27:
                if (r9 == 0) goto L_0x0ab0
                if (r26 == 0) goto L_0x0a66
                int r0 = r9.getWidth()     // Catch:{ all -> 0x0ace }
                int r5 = r9.getHeight()     // Catch:{ all -> 0x0ace }
                int r0 = r0 * r5
                r5 = 22500(0x57e4, float:3.1529E-41)
                if (r0 <= r5) goto L_0x0a43
                r0 = 100
                r5 = 100
                r6 = 0
                android.graphics.Bitmap r0 = org.telegram.messenger.Bitmaps.createScaledBitmap(r9, r0, r5, r6)     // Catch:{ all -> 0x0ace }
                goto L_0x0a44
            L_0x0a43:
                r0 = r9
            L_0x0a44:
                boolean r5 = r7.inPurgeable     // Catch:{ all -> 0x0ace }
                if (r5 == 0) goto L_0x0a4a
                r5 = 0
                goto L_0x0a4b
            L_0x0a4a:
                r5 = 1
            L_0x0a4b:
                int r6 = r0.getWidth()     // Catch:{ all -> 0x0ace }
                int r11 = r0.getHeight()     // Catch:{ all -> 0x0ace }
                int r12 = r0.getRowBytes()     // Catch:{ all -> 0x0ace }
                int r5 = org.telegram.messenger.Utilities.needInvert(r0, r5, r6, r11, r12)     // Catch:{ all -> 0x0ace }
                if (r5 == 0) goto L_0x0a5f
                r5 = 1
                goto L_0x0a60
            L_0x0a5f:
                r5 = 0
            L_0x0a60:
                if (r0 == r9) goto L_0x0a67
                r0.recycle()     // Catch:{ all -> 0x0abe }
                goto L_0x0a67
            L_0x0a66:
                r5 = 0
            L_0x0a67:
                r0 = 1117782016(0x42a00000, float:80.0)
                r6 = 1120403456(0x42CLASSNAME, float:100.0)
                if (r8 == 0) goto L_0x0a81
                int r11 = (r3 > r6 ? 1 : (r3 == r6 ? 0 : -1))
                if (r11 > 0) goto L_0x0a75
                int r11 = (r2 > r6 ? 1 : (r2 == r6 ? 0 : -1))
                if (r11 <= 0) goto L_0x0a81
            L_0x0a75:
                r2 = 80
                r3 = 0
                android.graphics.Bitmap r2 = org.telegram.messenger.Bitmaps.createScaledBitmap(r9, r2, r2, r3)     // Catch:{ all -> 0x0abe }
                r9 = r2
                r2 = 1117782016(0x42a00000, float:80.0)
                r3 = 1117782016(0x42a00000, float:80.0)
            L_0x0a81:
                if (r8 == 0) goto L_0x0aae
                int r0 = (r3 > r6 ? 1 : (r3 == r6 ? 0 : -1))
                if (r0 >= 0) goto L_0x0aae
                int r0 = (r2 > r6 ? 1 : (r2 == r6 ? 0 : -1))
                if (r0 >= 0) goto L_0x0aae
                android.graphics.Bitmap$Config r0 = r9.getConfig()     // Catch:{ all -> 0x0abe }
                android.graphics.Bitmap$Config r2 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ all -> 0x0abe }
                if (r0 != r2) goto L_0x0aab
                r12 = 3
                boolean r0 = r7.inPurgeable     // Catch:{ all -> 0x0abe }
                if (r0 == 0) goto L_0x0a9a
                r13 = 0
                goto L_0x0a9b
            L_0x0a9a:
                r13 = 1
            L_0x0a9b:
                int r14 = r9.getWidth()     // Catch:{ all -> 0x0abe }
                int r15 = r9.getHeight()     // Catch:{ all -> 0x0abe }
                int r16 = r9.getRowBytes()     // Catch:{ all -> 0x0abe }
                r11 = r9
                org.telegram.messenger.Utilities.blurBitmap(r11, r12, r13, r14, r15, r16)     // Catch:{ all -> 0x0abe }
            L_0x0aab:
                r0 = r9
                r9 = 1
                goto L_0x0ab3
            L_0x0aae:
                r0 = r9
                goto L_0x0ab2
            L_0x0ab0:
                r0 = r9
                r5 = 0
            L_0x0ab2:
                r9 = 0
            L_0x0ab3:
                if (r9 != 0) goto L_0x0ac1
                boolean r2 = r7.inPurgeable     // Catch:{ all -> 0x0abd }
                if (r2 == 0) goto L_0x0ac1
                org.telegram.messenger.Utilities.pinBitmap(r0)     // Catch:{ all -> 0x0abd }
                goto L_0x0ac1
            L_0x0abd:
                r9 = r0
            L_0x0abe:
                r28 = r5
                goto L_0x0ad0
            L_0x0ac1:
                r9 = r0
            L_0x0ac2:
                r2 = r5
                goto L_0x0ad2
            L_0x0ac4:
                r0 = move-exception
                r10 = 0
            L_0x0ac6:
                monitor-exit(r5)     // Catch:{ all -> 0x0ac8 }
                throw r0     // Catch:{ all -> 0x0acb }
            L_0x0ac8:
                r0 = move-exception
                goto L_0x0ac6
            L_0x0aca:
                r10 = 0
            L_0x0acb:
                r9 = r29
            L_0x0acd:
                r4 = 0
            L_0x0ace:
                r28 = 0
            L_0x0ad0:
                r2 = r28
            L_0x0ad2:
                java.lang.Thread.interrupted()
                if (r2 != 0) goto L_0x0ae8
                if (r4 == 0) goto L_0x0ada
                goto L_0x0ae8
            L_0x0ada:
                if (r9 == 0) goto L_0x0ae2
                android.graphics.drawable.BitmapDrawable r5 = new android.graphics.drawable.BitmapDrawable
                r5.<init>(r9)
                goto L_0x0ae3
            L_0x0ae2:
                r5 = r10
            L_0x0ae3:
                r1.onPostExecute(r5)
                goto L_0x0b4d
            L_0x0ae8:
                if (r9 == 0) goto L_0x0af0
                org.telegram.messenger.ExtendedBitmapDrawable r5 = new org.telegram.messenger.ExtendedBitmapDrawable
                r5.<init>(r9, r2, r4)
                goto L_0x0af1
            L_0x0af0:
                r5 = r10
            L_0x0af1:
                r1.onPostExecute(r5)
                goto L_0x0b4d
            L_0x0af5:
                r10 = 0
                r0 = 1135869952(0x43b40000, float:360.0)
                int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
                r2 = 1142947840(0x44200000, float:640.0)
                int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
                org.telegram.messenger.ImageLoader$CacheImage r3 = r1.cacheImage
                java.lang.String r3 = r3.filter
                if (r3 == 0) goto L_0x0b29
                java.lang.String r4 = "_"
                java.lang.String[] r3 = r3.split(r4)
                int r4 = r3.length
                r5 = 2
                if (r4 < r5) goto L_0x0b29
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
                goto L_0x0b2b
            L_0x0b29:
                r4 = 0
                r5 = 1
            L_0x0b2b:
                org.telegram.messenger.ImageLoader$CacheImage r3 = r1.cacheImage     // Catch:{ all -> 0x0b3b }
                java.io.File r7 = r3.finalFilePath     // Catch:{ all -> 0x0b3b }
                int r3 = r3.imageType     // Catch:{ all -> 0x0b3b }
                if (r3 != r6) goto L_0x0b35
                r9 = 1
                goto L_0x0b36
            L_0x0b35:
                r9 = 0
            L_0x0b36:
                android.graphics.Bitmap r5 = org.telegram.messenger.SvgHelper.getBitmap((java.io.File) r7, (int) r0, (int) r2, (boolean) r9)     // Catch:{ all -> 0x0b3b }
                goto L_0x0b40
            L_0x0b3b:
                r0 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
                r5 = r10
            L_0x0b40:
                if (r5 == 0) goto L_0x0b49
                android.graphics.drawable.BitmapDrawable r0 = new android.graphics.drawable.BitmapDrawable
                r0.<init>(r5)
                r5 = r0
                goto L_0x0b4a
            L_0x0b49:
                r5 = r10
            L_0x0b4a:
                r1.onPostExecute(r5)
            L_0x0b4d:
                return
            L_0x0b4e:
                r0 = move-exception
                monitor-exit(r2)     // Catch:{ all -> 0x0b4e }
                goto L_0x0b52
            L_0x0b51:
                throw r0
            L_0x0b52:
                goto L_0x0b51
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ImageLoader.CacheOutTask.run():void");
        }

        private void onPostExecute(Drawable drawable) {
            AndroidUtilities.runOnUIThread(new ImageLoader$CacheOutTask$$ExternalSyntheticLambda0(this, drawable));
        }

        /* access modifiers changed from: private */
        /* JADX WARNING: Failed to insert additional move for type inference */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public /* synthetic */ void lambda$onPostExecute$1(android.graphics.drawable.Drawable r6) {
            /*
                r5 = this;
                boolean r0 = r6 instanceof org.telegram.ui.Components.RLottieDrawable
                r1 = 0
                if (r0 == 0) goto L_0x003c
                org.telegram.ui.Components.RLottieDrawable r6 = (org.telegram.ui.Components.RLottieDrawable) r6
                org.telegram.messenger.ImageLoader r0 = org.telegram.messenger.ImageLoader.this
                org.telegram.messenger.LruCache r0 = r0.lottieMemCache
                org.telegram.messenger.ImageLoader$CacheImage r2 = r5.cacheImage
                java.lang.String r2 = r2.key
                java.lang.Object r0 = r0.get(r2)
                android.graphics.drawable.Drawable r0 = (android.graphics.drawable.Drawable) r0
                if (r0 != 0) goto L_0x0027
                org.telegram.messenger.ImageLoader r0 = org.telegram.messenger.ImageLoader.this
                org.telegram.messenger.LruCache r0 = r0.lottieMemCache
                org.telegram.messenger.ImageLoader$CacheImage r2 = r5.cacheImage
                java.lang.String r2 = r2.key
                r0.put(r2, r6)
                goto L_0x002b
            L_0x0027:
                r6.recycle()
                r6 = r0
            L_0x002b:
                if (r6 == 0) goto L_0x00d8
                org.telegram.messenger.ImageLoader r0 = org.telegram.messenger.ImageLoader.this
                org.telegram.messenger.ImageLoader$CacheImage r1 = r5.cacheImage
                java.lang.String r1 = r1.key
                r0.incrementUseCount(r1)
                org.telegram.messenger.ImageLoader$CacheImage r0 = r5.cacheImage
                java.lang.String r1 = r0.key
                goto L_0x00d8
            L_0x003c:
                boolean r0 = r6 instanceof org.telegram.ui.Components.AnimatedFileDrawable
                if (r0 == 0) goto L_0x0042
                goto L_0x00d8
            L_0x0042:
                boolean r0 = r6 instanceof android.graphics.drawable.BitmapDrawable
                if (r0 == 0) goto L_0x00d7
                android.graphics.drawable.BitmapDrawable r6 = (android.graphics.drawable.BitmapDrawable) r6
                org.telegram.messenger.ImageLoader r0 = org.telegram.messenger.ImageLoader.this
                org.telegram.messenger.ImageLoader$CacheImage r2 = r5.cacheImage
                java.lang.String r2 = r2.key
                android.graphics.drawable.BitmapDrawable r0 = r0.getFromMemCache(r2)
                r2 = 1
                if (r0 != 0) goto L_0x00bd
                org.telegram.messenger.ImageLoader$CacheImage r0 = r5.cacheImage
                java.lang.String r0 = r0.key
                java.lang.String r3 = "_f"
                boolean r0 = r0.endsWith(r3)
                if (r0 == 0) goto L_0x0071
                org.telegram.messenger.ImageLoader r0 = org.telegram.messenger.ImageLoader.this
                org.telegram.messenger.LruCache r0 = r0.wallpaperMemCache
                org.telegram.messenger.ImageLoader$CacheImage r2 = r5.cacheImage
                java.lang.String r2 = r2.key
                r0.put(r2, r6)
                r0 = 0
                r2 = 0
                goto L_0x00c5
            L_0x0071:
                org.telegram.messenger.ImageLoader$CacheImage r0 = r5.cacheImage
                java.lang.String r0 = r0.key
                java.lang.String r3 = "_isc"
                boolean r0 = r0.endsWith(r3)
                if (r0 != 0) goto L_0x00af
                android.graphics.Bitmap r0 = r6.getBitmap()
                int r0 = r0.getWidth()
                float r0 = (float) r0
                float r3 = org.telegram.messenger.AndroidUtilities.density
                r4 = 1117782016(0x42a00000, float:80.0)
                float r3 = r3 * r4
                int r0 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1))
                if (r0 > 0) goto L_0x00af
                android.graphics.Bitmap r0 = r6.getBitmap()
                int r0 = r0.getHeight()
                float r0 = (float) r0
                float r3 = org.telegram.messenger.AndroidUtilities.density
                float r3 = r3 * r4
                int r0 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1))
                if (r0 > 0) goto L_0x00af
                org.telegram.messenger.ImageLoader r0 = org.telegram.messenger.ImageLoader.this
                org.telegram.messenger.LruCache r0 = r0.smallImagesMemCache
                org.telegram.messenger.ImageLoader$CacheImage r3 = r5.cacheImage
                java.lang.String r3 = r3.key
                r0.put(r3, r6)
                goto L_0x00c5
            L_0x00af:
                org.telegram.messenger.ImageLoader r0 = org.telegram.messenger.ImageLoader.this
                org.telegram.messenger.LruCache r0 = r0.memCache
                org.telegram.messenger.ImageLoader$CacheImage r3 = r5.cacheImage
                java.lang.String r3 = r3.key
                r0.put(r3, r6)
                goto L_0x00c5
            L_0x00bd:
                android.graphics.Bitmap r6 = r6.getBitmap()
                r6.recycle()
                r6 = r0
            L_0x00c5:
                if (r6 == 0) goto L_0x00d8
                if (r2 == 0) goto L_0x00d8
                org.telegram.messenger.ImageLoader r0 = org.telegram.messenger.ImageLoader.this
                org.telegram.messenger.ImageLoader$CacheImage r1 = r5.cacheImage
                java.lang.String r1 = r1.key
                r0.incrementUseCount(r1)
                org.telegram.messenger.ImageLoader$CacheImage r0 = r5.cacheImage
                java.lang.String r1 = r0.key
                goto L_0x00d8
            L_0x00d7:
                r6 = r1
            L_0x00d8:
                org.telegram.messenger.ImageLoader r0 = org.telegram.messenger.ImageLoader.this
                org.telegram.messenger.DispatchQueue r0 = r0.imageLoadQueue
                org.telegram.messenger.ImageLoader$CacheOutTask$$ExternalSyntheticLambda1 r2 = new org.telegram.messenger.ImageLoader$CacheOutTask$$ExternalSyntheticLambda1
                r2.<init>(r5, r6, r1)
                r0.postRunnable(r2)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ImageLoader.CacheOutTask.lambda$onPostExecute$1(android.graphics.drawable.Drawable):void");
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onPostExecute$0(Drawable drawable, String str) {
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
    public BitmapDrawable getFromMemCache(String str) {
        BitmapDrawable bitmapDrawable = this.memCache.get(str);
        if (bitmapDrawable == null) {
            bitmapDrawable = this.smallImagesMemCache.get(str);
        }
        return bitmapDrawable == null ? this.wallpaperMemCache.get(str) : bitmapDrawable;
    }

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
                AndroidUtilities.runOnUIThread(new ImageLoader$CacheImage$$ExternalSyntheticLambda0(this, drawable, new ArrayList(this.imageReceiverArray), new ArrayList(this.imageReceiverGuidsArray), str));
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
        public /* synthetic */ void lambda$setImageAndClear$0(Drawable drawable, ArrayList arrayList, ArrayList arrayList2, String str) {
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
        int min = Math.min(z ? 30 : 15, memoryClass / 7) * 1024 * 1024;
        float f = (float) min;
        this.memCache = new LruCache<BitmapDrawable>((int) (0.8f * f)) {
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
        this.smallImagesMemCache = new LruCache<BitmapDrawable>((int) (f * 0.2f)) {
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
        this.wallpaperMemCache = new LruCache<BitmapDrawable>(min / 4) {
            /* access modifiers changed from: protected */
            public int sizeOf(String str, BitmapDrawable bitmapDrawable) {
                return bitmapDrawable.getBitmap().getByteCount();
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
                        AndroidUtilities.runOnUIThread(new ImageLoader$5$$ExternalSyntheticLambda1(i, str, j, j2, z));
                    }
                }

                public void fileDidUploaded(String str, TLRPC$InputFile tLRPC$InputFile, TLRPC$InputEncryptedFile tLRPC$InputEncryptedFile, byte[] bArr, byte[] bArr2, long j) {
                    Utilities.stageQueue.postRunnable(new ImageLoader$5$$ExternalSyntheticLambda4(this, i, str, tLRPC$InputFile, tLRPC$InputEncryptedFile, bArr, bArr2, j));
                }

                /* access modifiers changed from: private */
                public /* synthetic */ void lambda$fileDidUploaded$2(int i, String str, TLRPC$InputFile tLRPC$InputFile, TLRPC$InputEncryptedFile tLRPC$InputEncryptedFile, byte[] bArr, byte[] bArr2, long j) {
                    AndroidUtilities.runOnUIThread(new ImageLoader$5$$ExternalSyntheticLambda2(i, str, tLRPC$InputFile, tLRPC$InputEncryptedFile, bArr, bArr2, j));
                    ImageLoader.this.fileProgresses.remove(str);
                }

                public void fileDidFailedUpload(String str, boolean z) {
                    Utilities.stageQueue.postRunnable(new ImageLoader$5$$ExternalSyntheticLambda5(this, i, str, z));
                }

                /* access modifiers changed from: private */
                public /* synthetic */ void lambda$fileDidFailedUpload$4(int i, String str, boolean z) {
                    AndroidUtilities.runOnUIThread(new ImageLoader$5$$ExternalSyntheticLambda3(i, str, z));
                    ImageLoader.this.fileProgresses.remove(str);
                }

                public void fileDidLoaded(String str, File file, int i) {
                    ImageLoader.this.fileProgresses.remove(str);
                    AndroidUtilities.runOnUIThread(new ImageLoader$5$$ExternalSyntheticLambda6(this, file, str, i, i));
                }

                /* access modifiers changed from: private */
                public /* synthetic */ void lambda$fileDidLoaded$5(File file, String str, int i, int i2) {
                    if (SharedConfig.saveToGallery && ImageLoader.this.telegramPath != null && file != null && ((str.endsWith(".mp4") || str.endsWith(".jpg")) && file.toString().startsWith(ImageLoader.this.telegramPath.toString()))) {
                        AndroidUtilities.addMediaToGallery(file.toString());
                    }
                    NotificationCenter.getInstance(i).postNotificationName(NotificationCenter.fileLoaded, str, file);
                    ImageLoader.this.fileDidLoaded(str, file, i2);
                }

                public void fileDidFailedLoad(String str, int i) {
                    ImageLoader.this.fileProgresses.remove(str);
                    AndroidUtilities.runOnUIThread(new ImageLoader$5$$ExternalSyntheticLambda7(this, str, i, i));
                }

                /* access modifiers changed from: private */
                public /* synthetic */ void lambda$fileDidFailedLoad$6(String str, int i, int i2) {
                    ImageLoader.this.fileDidFailedLoad(str, i);
                    NotificationCenter.getInstance(i2).postNotificationName(NotificationCenter.fileLoadFailed, str, Integer.valueOf(i));
                }

                public void fileLoadProgressChanged(FileLoadOperation fileLoadOperation, String str, long j, long j2) {
                    FileLoadOperation fileLoadOperation2 = fileLoadOperation;
                    String str2 = str;
                    ImageLoader.this.fileProgresses.put(str, new long[]{j, j2});
                    long elapsedRealtime = SystemClock.elapsedRealtime();
                    long j3 = fileLoadOperation2.lastProgressUpdateTime;
                    if (j3 == 0 || j3 < elapsedRealtime - 500 || j == 0) {
                        fileLoadOperation2.lastProgressUpdateTime = elapsedRealtime;
                        AndroidUtilities.runOnUIThread(new ImageLoader$5$$ExternalSyntheticLambda0(i, str, j, j2));
                    }
                }
            });
        }
        FileLoader.setMediaDirs(sparseArray);
        AnonymousClass6 r0 = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("file system changed");
                }
                ImageLoader$6$$ExternalSyntheticLambda0 imageLoader$6$$ExternalSyntheticLambda0 = new ImageLoader$6$$ExternalSyntheticLambda0(this);
                if ("android.intent.action.MEDIA_UNMOUNTED".equals(intent.getAction())) {
                    AndroidUtilities.runOnUIThread(imageLoader$6$$ExternalSyntheticLambda0, 1000);
                } else {
                    imageLoader$6$$ExternalSyntheticLambda0.run();
                }
            }

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$onReceive$0() {
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
        this.cacheOutQueue.postRunnable(new ImageLoader$$ExternalSyntheticLambda1(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$checkMediaPaths$1() {
        AndroidUtilities.runOnUIThread(new ImageLoader$$ExternalSyntheticLambda0(createMediaPaths()));
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

    /* JADX WARNING: Missing exception handler attribute for start block: B:15:0x002b */
    @android.annotation.TargetApi(26)
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static void moveDirectory(java.io.File r1, java.io.File r2) {
        /*
            boolean r0 = r2.exists()
            if (r0 != 0) goto L_0x000d
            boolean r0 = r2.mkdir()
            if (r0 != 0) goto L_0x000d
            return
        L_0x000d:
            java.nio.file.Path r1 = r1.toPath()     // Catch:{ Exception -> 0x002c }
            java.util.stream.Stream r1 = java.nio.file.Files.list(r1)     // Catch:{ Exception -> 0x002c }
            j$.util.stream.Stream r1 = j$.wrappers.C$r8$wrapper$java$util$stream$Stream$VWRP.convert(r1)     // Catch:{ Exception -> 0x002c }
            org.telegram.messenger.ImageLoader$$ExternalSyntheticLambda13 r0 = new org.telegram.messenger.ImageLoader$$ExternalSyntheticLambda13     // Catch:{ all -> 0x0025 }
            r0.<init>(r2)     // Catch:{ all -> 0x0025 }
            r1.forEach(r0)     // Catch:{ all -> 0x0025 }
            r1.close()     // Catch:{ Exception -> 0x002c }
            goto L_0x0030
        L_0x0025:
            r2 = move-exception
            if (r1 == 0) goto L_0x002b
            r1.close()     // Catch:{ all -> 0x002b }
        L_0x002b:
            throw r2     // Catch:{ Exception -> 0x002c }
        L_0x002c:
            r1 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)
        L_0x0030:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ImageLoader.moveDirectory(java.io.File, java.io.File):void");
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$moveDirectory$2(File file, Path path) {
        File file2 = new File(file, path.getFileName().toString());
        if (Files.isDirectory(path, new LinkOption[0])) {
            moveDirectory(path.toFile(), file2);
            return;
        }
        try {
            Files.move(path, file2.toPath(), new CopyOption[0]);
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:102:0x0116 A[EDGE_INSN: B:102:0x0116->B:47:0x0116 ?: BREAK  , SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:43:0x00f6 A[Catch:{ Exception -> 0x021f }] */
    /* JADX WARNING: Removed duplicated region for block: B:49:0x011e A[SYNTHETIC, Splitter:B:49:0x011e] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.util.SparseArray<java.io.File> createMediaPaths() {
        /*
            r13 = this;
            java.lang.String r0 = "Telegram"
            android.util.SparseArray r1 = new android.util.SparseArray
            r1.<init>()
            java.io.File r2 = org.telegram.messenger.AndroidUtilities.getCacheDir()
            boolean r3 = r2.isDirectory()
            if (r3 != 0) goto L_0x0019
            r2.mkdirs()     // Catch:{ Exception -> 0x0015 }
            goto L_0x0019
        L_0x0015:
            r3 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r3)
        L_0x0019:
            java.io.File r3 = new java.io.File
            java.lang.String r4 = ".nomedia"
            r3.<init>(r2, r4)
            org.telegram.messenger.AndroidUtilities.createEmptyFile(r3)
            r3 = 4
            r1.put(r3, r2)
            boolean r3 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r3 == 0) goto L_0x003f
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r5 = "cache path = "
            r3.append(r5)
            r3.append(r2)
            java.lang.String r3 = r3.toString()
            org.telegram.messenger.FileLog.d(r3)
        L_0x003f:
            java.lang.String r3 = "mounted"
            java.lang.String r5 = android.os.Environment.getExternalStorageState()     // Catch:{ Exception -> 0x021f }
            boolean r3 = r3.equals(r5)     // Catch:{ Exception -> 0x021f }
            if (r3 == 0) goto L_0x0212
            java.io.File r3 = android.os.Environment.getExternalStorageDirectory()     // Catch:{ Exception -> 0x021f }
            int r5 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x021f }
            r6 = 19
            r7 = 0
            if (r5 < r6) goto L_0x0082
            java.lang.String r5 = org.telegram.messenger.SharedConfig.storageCacheDir     // Catch:{ Exception -> 0x021f }
            boolean r5 = android.text.TextUtils.isEmpty(r5)     // Catch:{ Exception -> 0x021f }
            if (r5 != 0) goto L_0x0082
            java.util.ArrayList r5 = org.telegram.messenger.AndroidUtilities.getRootDirs()     // Catch:{ Exception -> 0x021f }
            if (r5 == 0) goto L_0x0082
            int r8 = r5.size()     // Catch:{ Exception -> 0x021f }
            r9 = 0
        L_0x0069:
            if (r9 >= r8) goto L_0x0082
            java.lang.Object r10 = r5.get(r9)     // Catch:{ Exception -> 0x021f }
            java.io.File r10 = (java.io.File) r10     // Catch:{ Exception -> 0x021f }
            java.lang.String r11 = r10.getAbsolutePath()     // Catch:{ Exception -> 0x021f }
            java.lang.String r12 = org.telegram.messenger.SharedConfig.storageCacheDir     // Catch:{ Exception -> 0x021f }
            boolean r11 = r11.startsWith(r12)     // Catch:{ Exception -> 0x021f }
            if (r11 == 0) goto L_0x007f
            r3 = r10
            goto L_0x0082
        L_0x007f:
            int r9 = r9 + 1
            goto L_0x0069
        L_0x0082:
            java.io.File r5 = new java.io.File     // Catch:{ Exception -> 0x021f }
            r5.<init>(r3, r0)     // Catch:{ Exception -> 0x021f }
            r13.telegramPath = r5     // Catch:{ Exception -> 0x021f }
            r5.mkdirs()     // Catch:{ Exception -> 0x021f }
            android.content.Context r5 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x00a1 }
            android.content.pm.PackageManager r5 = r5.getPackageManager()     // Catch:{ all -> 0x00a1 }
            android.content.Context r8 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x00a1 }
            java.lang.String r8 = r8.getPackageName()     // Catch:{ all -> 0x00a1 }
            android.content.pm.ApplicationInfo r5 = r5.getApplicationInfo(r8, r7)     // Catch:{ all -> 0x00a1 }
            if (r5 == 0) goto L_0x00a1
            int r5 = r5.targetSdkVersion     // Catch:{ all -> 0x00a1 }
            goto L_0x00a2
        L_0x00a1:
            r5 = 0
        L_0x00a2:
            android.content.Context r8 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x021f }
            r9 = 0
            java.io.File r8 = r8.getExternalFilesDir(r9)     // Catch:{ Exception -> 0x021f }
            java.io.File r9 = new java.io.File     // Catch:{ Exception -> 0x021f }
            r9.<init>(r8, r0)     // Catch:{ Exception -> 0x021f }
            r13.telegramPath = r9     // Catch:{ Exception -> 0x021f }
            int r8 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x021f }
            r9 = 29
            if (r8 < r9) goto L_0x00e1
            r9 = 30
            if (r5 >= r9) goto L_0x00e1
            java.io.File r5 = new java.io.File     // Catch:{ Exception -> 0x021f }
            r5.<init>(r3, r0)     // Catch:{ Exception -> 0x021f }
            long r9 = android.os.SystemClock.elapsedRealtime()     // Catch:{ Exception -> 0x021f }
            java.io.File r3 = r13.telegramPath     // Catch:{ Exception -> 0x021f }
            moveDirectory(r5, r3)     // Catch:{ Exception -> 0x021f }
            long r11 = android.os.SystemClock.elapsedRealtime()     // Catch:{ Exception -> 0x021f }
            long r11 = r11 - r9
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x021f }
            r3.<init>()     // Catch:{ Exception -> 0x021f }
            java.lang.String r5 = "move time = "
            r3.append(r5)     // Catch:{ Exception -> 0x021f }
            r3.append(r11)     // Catch:{ Exception -> 0x021f }
            java.lang.String r3 = r3.toString()     // Catch:{ Exception -> 0x021f }
            org.telegram.messenger.FileLog.d(r3)     // Catch:{ Exception -> 0x021f }
        L_0x00e1:
            if (r8 < r6) goto L_0x0116
            java.io.File r3 = r13.telegramPath     // Catch:{ Exception -> 0x021f }
            boolean r3 = r3.isDirectory()     // Catch:{ Exception -> 0x021f }
            if (r3 != 0) goto L_0x0116
            java.util.ArrayList r3 = org.telegram.messenger.AndroidUtilities.getDataDirs()     // Catch:{ Exception -> 0x021f }
            int r5 = r3.size()     // Catch:{ Exception -> 0x021f }
            r6 = 0
        L_0x00f4:
            if (r6 >= r5) goto L_0x0116
            java.lang.Object r8 = r3.get(r6)     // Catch:{ Exception -> 0x021f }
            java.io.File r8 = (java.io.File) r8     // Catch:{ Exception -> 0x021f }
            java.lang.String r9 = r8.getAbsolutePath()     // Catch:{ Exception -> 0x021f }
            java.lang.String r10 = org.telegram.messenger.SharedConfig.storageCacheDir     // Catch:{ Exception -> 0x021f }
            boolean r9 = r9.startsWith(r10)     // Catch:{ Exception -> 0x021f }
            if (r9 == 0) goto L_0x0113
            java.io.File r3 = new java.io.File     // Catch:{ Exception -> 0x021f }
            r3.<init>(r8, r0)     // Catch:{ Exception -> 0x021f }
            r13.telegramPath = r3     // Catch:{ Exception -> 0x021f }
            r3.mkdirs()     // Catch:{ Exception -> 0x021f }
            goto L_0x0116
        L_0x0113:
            int r6 = r6 + 1
            goto L_0x00f4
        L_0x0116:
            java.io.File r0 = r13.telegramPath     // Catch:{ Exception -> 0x021f }
            boolean r0 = r0.isDirectory()     // Catch:{ Exception -> 0x021f }
            if (r0 == 0) goto L_0x021b
            java.io.File r0 = new java.io.File     // Catch:{ Exception -> 0x0152 }
            java.io.File r3 = r13.telegramPath     // Catch:{ Exception -> 0x0152 }
            java.lang.String r5 = "Telegram Images"
            r0.<init>(r3, r5)     // Catch:{ Exception -> 0x0152 }
            r0.mkdir()     // Catch:{ Exception -> 0x0152 }
            boolean r3 = r0.isDirectory()     // Catch:{ Exception -> 0x0152 }
            if (r3 == 0) goto L_0x0156
            boolean r3 = r13.canMoveFiles(r2, r0, r7)     // Catch:{ Exception -> 0x0152 }
            if (r3 == 0) goto L_0x0156
            r1.put(r7, r0)     // Catch:{ Exception -> 0x0152 }
            boolean r3 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x0152 }
            if (r3 == 0) goto L_0x0156
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0152 }
            r3.<init>()     // Catch:{ Exception -> 0x0152 }
            java.lang.String r5 = "image path = "
            r3.append(r5)     // Catch:{ Exception -> 0x0152 }
            r3.append(r0)     // Catch:{ Exception -> 0x0152 }
            java.lang.String r0 = r3.toString()     // Catch:{ Exception -> 0x0152 }
            org.telegram.messenger.FileLog.d(r0)     // Catch:{ Exception -> 0x0152 }
            goto L_0x0156
        L_0x0152:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ Exception -> 0x021f }
        L_0x0156:
            java.io.File r0 = new java.io.File     // Catch:{ Exception -> 0x018b }
            java.io.File r3 = r13.telegramPath     // Catch:{ Exception -> 0x018b }
            java.lang.String r5 = "Telegram Video"
            r0.<init>(r3, r5)     // Catch:{ Exception -> 0x018b }
            r0.mkdir()     // Catch:{ Exception -> 0x018b }
            boolean r3 = r0.isDirectory()     // Catch:{ Exception -> 0x018b }
            if (r3 == 0) goto L_0x018f
            r3 = 2
            boolean r5 = r13.canMoveFiles(r2, r0, r3)     // Catch:{ Exception -> 0x018b }
            if (r5 == 0) goto L_0x018f
            r1.put(r3, r0)     // Catch:{ Exception -> 0x018b }
            boolean r3 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x018b }
            if (r3 == 0) goto L_0x018f
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x018b }
            r3.<init>()     // Catch:{ Exception -> 0x018b }
            java.lang.String r5 = "video path = "
            r3.append(r5)     // Catch:{ Exception -> 0x018b }
            r3.append(r0)     // Catch:{ Exception -> 0x018b }
            java.lang.String r0 = r3.toString()     // Catch:{ Exception -> 0x018b }
            org.telegram.messenger.FileLog.d(r0)     // Catch:{ Exception -> 0x018b }
            goto L_0x018f
        L_0x018b:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ Exception -> 0x021f }
        L_0x018f:
            java.io.File r0 = new java.io.File     // Catch:{ Exception -> 0x01cc }
            java.io.File r3 = r13.telegramPath     // Catch:{ Exception -> 0x01cc }
            java.lang.String r5 = "Telegram Audio"
            r0.<init>(r3, r5)     // Catch:{ Exception -> 0x01cc }
            r0.mkdir()     // Catch:{ Exception -> 0x01cc }
            boolean r3 = r0.isDirectory()     // Catch:{ Exception -> 0x01cc }
            if (r3 == 0) goto L_0x01d0
            r3 = 1
            boolean r5 = r13.canMoveFiles(r2, r0, r3)     // Catch:{ Exception -> 0x01cc }
            if (r5 == 0) goto L_0x01d0
            java.io.File r5 = new java.io.File     // Catch:{ Exception -> 0x01cc }
            r5.<init>(r0, r4)     // Catch:{ Exception -> 0x01cc }
            org.telegram.messenger.AndroidUtilities.createEmptyFile(r5)     // Catch:{ Exception -> 0x01cc }
            r1.put(r3, r0)     // Catch:{ Exception -> 0x01cc }
            boolean r3 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x01cc }
            if (r3 == 0) goto L_0x01d0
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x01cc }
            r3.<init>()     // Catch:{ Exception -> 0x01cc }
            java.lang.String r5 = "audio path = "
            r3.append(r5)     // Catch:{ Exception -> 0x01cc }
            r3.append(r0)     // Catch:{ Exception -> 0x01cc }
            java.lang.String r0 = r3.toString()     // Catch:{ Exception -> 0x01cc }
            org.telegram.messenger.FileLog.d(r0)     // Catch:{ Exception -> 0x01cc }
            goto L_0x01d0
        L_0x01cc:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ Exception -> 0x021f }
        L_0x01d0:
            java.io.File r0 = new java.io.File     // Catch:{ Exception -> 0x020d }
            java.io.File r3 = r13.telegramPath     // Catch:{ Exception -> 0x020d }
            java.lang.String r5 = "Telegram Documents"
            r0.<init>(r3, r5)     // Catch:{ Exception -> 0x020d }
            r0.mkdir()     // Catch:{ Exception -> 0x020d }
            boolean r3 = r0.isDirectory()     // Catch:{ Exception -> 0x020d }
            if (r3 == 0) goto L_0x021b
            r3 = 3
            boolean r2 = r13.canMoveFiles(r2, r0, r3)     // Catch:{ Exception -> 0x020d }
            if (r2 == 0) goto L_0x021b
            java.io.File r2 = new java.io.File     // Catch:{ Exception -> 0x020d }
            r2.<init>(r0, r4)     // Catch:{ Exception -> 0x020d }
            org.telegram.messenger.AndroidUtilities.createEmptyFile(r2)     // Catch:{ Exception -> 0x020d }
            r1.put(r3, r0)     // Catch:{ Exception -> 0x020d }
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x020d }
            if (r2 == 0) goto L_0x021b
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x020d }
            r2.<init>()     // Catch:{ Exception -> 0x020d }
            java.lang.String r3 = "documents path = "
            r2.append(r3)     // Catch:{ Exception -> 0x020d }
            r2.append(r0)     // Catch:{ Exception -> 0x020d }
            java.lang.String r0 = r2.toString()     // Catch:{ Exception -> 0x020d }
            org.telegram.messenger.FileLog.d(r0)     // Catch:{ Exception -> 0x020d }
            goto L_0x021b
        L_0x020d:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ Exception -> 0x021f }
            goto L_0x021b
        L_0x0212:
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x021f }
            if (r0 == 0) goto L_0x021b
            java.lang.String r0 = "this Android can't rename files"
            org.telegram.messenger.FileLog.d(r0)     // Catch:{ Exception -> 0x021f }
        L_0x021b:
            org.telegram.messenger.SharedConfig.checkSaveToGalleryFiles()     // Catch:{ Exception -> 0x021f }
            goto L_0x0223
        L_0x021f:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0223:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ImageLoader.createMediaPaths():android.util.SparseArray");
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
        LruCache<BitmapDrawable> lruCache = this.memCache;
        BitmapDrawable bitmapDrawable = lruCache.get(str);
        if (bitmapDrawable == null) {
            lruCache = this.smallImagesMemCache;
            bitmapDrawable = lruCache.get(str);
        }
        this.replacedBitmaps.put(str, str2);
        if (bitmapDrawable != null) {
            BitmapDrawable bitmapDrawable2 = lruCache.get(str2);
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
                lruCache.remove(str);
                lruCache.put(str2, bitmapDrawable);
                this.ignoreRemoval = null;
            } else {
                lruCache.remove(str);
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
        this.smallImagesMemCache.remove(str);
    }

    public boolean isInMemCache(String str, boolean z) {
        if (z) {
            return this.lottieMemCache.get(str) != null;
        }
        if (getFromMemCache(str) != null) {
            return true;
        }
        return false;
    }

    public void clearMemory() {
        this.smallImagesMemCache.evictAll();
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
            ArrayList<Runnable> loadingOperations = imageReceiver.getLoadingOperations();
            if (!loadingOperations.isEmpty()) {
                for (int i = 0; i < loadingOperations.size(); i++) {
                    this.imageLoadQueue.cancelRunnable(loadingOperations.get(i));
                }
                loadingOperations.clear();
            }
            imageReceiver.addLoadingImageRunnable((Runnable) null);
            this.imageLoadQueue.postRunnable(new ImageLoader$$ExternalSyntheticLambda12(this, z, imageReceiver));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$cancelLoadingForImageReceiver$3(boolean z, ImageReceiver imageReceiver) {
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
        return getFromMemCache(str3);
    }

    /* access modifiers changed from: private */
    /* renamed from: replaceImageInCacheInternal */
    public void lambda$replaceImageInCache$4(String str, String str2, ImageLocation imageLocation) {
        ArrayList<String> arrayList;
        for (int i = 0; i < 2; i++) {
            if (i == 0) {
                arrayList = this.memCache.getFilterKeys(str);
            } else {
                arrayList = this.smallImagesMemCache.getFilterKeys(str);
            }
            if (arrayList != null) {
                for (int i2 = 0; i2 < arrayList.size(); i2++) {
                    String str3 = arrayList.get(i2);
                    String str4 = str + "@" + str3;
                    String str5 = str2 + "@" + str3;
                    performReplace(str4, str5);
                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didReplacedPhotoInMemCache, str4, str5, imageLocation);
                }
            } else {
                performReplace(str, str2);
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didReplacedPhotoInMemCache, str, str2, imageLocation);
            }
        }
    }

    public void replaceImageInCache(String str, String str2, ImageLocation imageLocation, boolean z) {
        if (z) {
            AndroidUtilities.runOnUIThread(new ImageLoader$$ExternalSyntheticLambda9(this, str, str2, imageLocation));
        } else {
            lambda$replaceImageInCache$4(str, str2, imageLocation);
        }
    }

    public void putImageToCache(BitmapDrawable bitmapDrawable, String str, boolean z) {
        if (z) {
            this.smallImagesMemCache.put(str, bitmapDrawable);
        } else {
            this.memCache.put(str, bitmapDrawable);
        }
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
            this.imageLoadQueue.postRunnable(new ImageLoader$$ExternalSyntheticLambda4(this, imageKey));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$cancelForceLoadingForImageReceiver$5(String str) {
        this.forceLoadingImages.remove(str);
    }

    private void createLoadOperationForImageReceiver(ImageReceiver imageReceiver, String str, String str2, String str3, ImageLocation imageLocation, String str4, int i, int i2, int i3, int i4, int i5) {
        ImageReceiver imageReceiver2 = imageReceiver;
        int i6 = i3;
        if (imageReceiver2 == null || str2 == null || str == null || imageLocation == null) {
            return;
        }
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
        Runnable[] runnableArr = {new ImageLoader$$ExternalSyntheticLambda2(this, i4, str2, str, i8, imageReceiver, i5, str4, i3, imageLocation, i6 == 0 && imageReceiver.isCurrentKeyQuality(), imageReceiver.getParentObject(), imageReceiver.getQulityThumbDocument(), isNeedsQualityThumb, imageReceiver.isShouldGenerateQualityThumb(), str3, i2, i, imageReceiver.getCurrentAccount())};
        this.imageLoadQueue.postRunnable(runnableArr[0]);
        imageReceiver.addLoadingImageRunnable(runnableArr[0]);
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:68:0x0196, code lost:
        if (r8.exists() == false) goto L_0x0198;
     */
    /* JADX WARNING: Removed duplicated region for block: B:115:0x0270  */
    /* JADX WARNING: Removed duplicated region for block: B:226:0x049a  */
    /* JADX WARNING: Removed duplicated region for block: B:227:0x04a4  */
    /* JADX WARNING: Removed duplicated region for block: B:229:0x04aa  */
    /* JADX WARNING: Removed duplicated region for block: B:232:0x04d2  */
    /* JADX WARNING: Removed duplicated region for block: B:235:0x04d7  */
    /* JADX WARNING: Removed duplicated region for block: B:238:0x0509 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:286:0x05f6  */
    /* JADX WARNING: Removed duplicated region for block: B:287:0x05fe  */
    /* JADX WARNING: Removed duplicated region for block: B:67:0x018d  */
    /* JADX WARNING: Removed duplicated region for block: B:71:0x019b  */
    /* JADX WARNING: Removed duplicated region for block: B:73:0x019f  */
    /* JADX WARNING: Removed duplicated region for block: B:83:0x01ee  */
    /* JADX WARNING: Removed duplicated region for block: B:88:0x01fe  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$createLoadOperationForImageReceiver$6(int r22, java.lang.String r23, java.lang.String r24, int r25, org.telegram.messenger.ImageReceiver r26, int r27, java.lang.String r28, int r29, org.telegram.messenger.ImageLocation r30, boolean r31, java.lang.Object r32, org.telegram.tgnet.TLRPC$Document r33, boolean r34, boolean r35, java.lang.String r36, int r37, int r38, int r39) {
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
            r6 = r38
            r4 = 2
            if (r1 == r4) goto L_0x00a7
            java.util.HashMap<java.lang.String, org.telegram.messenger.ImageLoader$CacheImage> r3 = r0.imageLoadingByUrl
            java.lang.Object r3 = r3.get(r2)
            org.telegram.messenger.ImageLoader$CacheImage r3 = (org.telegram.messenger.ImageLoader.CacheImage) r3
            java.util.HashMap<java.lang.String, org.telegram.messenger.ImageLoader$CacheImage> r4 = r0.imageLoadingByKeys
            java.lang.Object r4 = r4.get(r9)
            org.telegram.messenger.ImageLoader$CacheImage r4 = (org.telegram.messenger.ImageLoader.CacheImage) r4
            android.util.SparseArray<org.telegram.messenger.ImageLoader$CacheImage> r5 = r0.imageLoadingByTag
            java.lang.Object r5 = r5.get(r10)
            org.telegram.messenger.ImageLoader$CacheImage r5 = (org.telegram.messenger.ImageLoader.CacheImage) r5
            if (r5 == 0) goto L_0x0076
            if (r5 != r4) goto L_0x0047
            r9 = r27
            r5.setImageReceiverGuid(r11, r9)
            r16 = r3
            r17 = r4
            r5 = 1
            r9 = 1
            r19 = 0
            goto L_0x007e
        L_0x0047:
            r9 = r27
            if (r5 != r3) goto L_0x006b
            r16 = r3
            if (r4 != 0) goto L_0x0064
            r9 = 1
            r3 = r5
            r17 = r4
            r5 = 2
            r4 = r26
            r19 = 0
            r5 = r24
            r6 = r28
            r7 = r29
            r8 = r27
            r3.replaceImageReceiver(r4, r5, r6, r7, r8)
            goto L_0x0069
        L_0x0064:
            r17 = r4
            r9 = 1
            r19 = 0
        L_0x0069:
            r5 = 1
            goto L_0x007e
        L_0x006b:
            r16 = r3
            r17 = r4
            r9 = 1
            r19 = 0
            r5.removeImageReceiver(r11)
            goto L_0x007d
        L_0x0076:
            r16 = r3
            r17 = r4
            r9 = 1
            r19 = 0
        L_0x007d:
            r5 = 0
        L_0x007e:
            if (r5 != 0) goto L_0x0092
            if (r17 == 0) goto L_0x0092
            r3 = r17
            r4 = r26
            r5 = r24
            r6 = r28
            r7 = r29
            r8 = r27
            r3.addImageReceiver(r4, r5, r6, r7, r8)
            r5 = 1
        L_0x0092:
            if (r5 != 0) goto L_0x00ab
            if (r16 == 0) goto L_0x00ab
            r3 = r16
            r4 = r26
            r5 = r24
            r6 = r28
            r7 = r29
            r8 = r27
            r3.addImageReceiver(r4, r5, r6, r7, r8)
            r5 = 1
            goto L_0x00ab
        L_0x00a7:
            r9 = 1
            r19 = 0
            r5 = 0
        L_0x00ab:
            if (r5 != 0) goto L_0x0606
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
            if (r7 == 0) goto L_0x00e4
            r7 = 8
            int r7 = r3.indexOf(r10, r7)
            if (r7 < 0) goto L_0x00e2
            java.io.File r10 = new java.io.File
            int r7 = r7 + r9
            java.lang.String r3 = r3.substring(r7)
            r10.<init>(r3)
            goto L_0x0104
        L_0x00e2:
            r10 = 0
            goto L_0x0104
        L_0x00e4:
            java.lang.String r7 = "vthumb://"
            boolean r7 = r3.startsWith(r7)
            if (r7 == 0) goto L_0x00ff
            r7 = 9
            int r7 = r3.indexOf(r10, r7)
            if (r7 < 0) goto L_0x00e2
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
            r5 = r3
            r20 = r8
            r6 = 0
        L_0x010c:
            r9 = 2
            goto L_0x01fc
        L_0x010f:
            if (r1 != 0) goto L_0x01f6
            if (r31 == 0) goto L_0x01f6
            boolean r3 = r14 instanceof org.telegram.messenger.MessageObject
            if (r3 == 0) goto L_0x0130
            r3 = r14
            org.telegram.messenger.MessageObject r3 = (org.telegram.messenger.MessageObject) r3
            org.telegram.tgnet.TLRPC$Document r7 = r3.getDocument()
            org.telegram.tgnet.TLRPC$Message r15 = r3.messageOwner
            java.lang.String r5 = r15.attachPath
            java.io.File r15 = org.telegram.messenger.FileLoader.getPathToMessage(r15)
            int r3 = r3.getMediaType()
            r18 = r15
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
            r18 = r3
            r7 = r5
            r3 = 1
            r5 = 0
            goto L_0x014b
        L_0x0145:
            r3 = 0
            r5 = 0
            r7 = 0
            r15 = 0
            r18 = 0
        L_0x014b:
            if (r15 == 0) goto L_0x01f2
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
            r8 = r18
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
            r10 = r9
            r5 = 1
            goto L_0x010c
        L_0x01f2:
            r20 = r8
            r5 = 1
            goto L_0x01f9
        L_0x01f6:
            r20 = r8
            r5 = 0
        L_0x01f9:
            r6 = 0
            r9 = 2
            r10 = 0
        L_0x01fc:
            if (r1 == r9) goto L_0x0606
            boolean r3 = r30.isEncrypted()
            org.telegram.messenger.ImageLoader$CacheImage r13 = new org.telegram.messenger.ImageLoader$CacheImage
            r7 = 0
            r13.<init>()
            r14 = r30
            if (r31 != 0) goto L_0x026c
            int r7 = r14.imageType
            if (r7 == r9) goto L_0x0267
            org.telegram.messenger.WebFile r7 = r14.webFile
            boolean r7 = org.telegram.messenger.MessageObject.isGifDocument((org.telegram.messenger.WebFile) r7)
            if (r7 != 0) goto L_0x0267
            org.telegram.tgnet.TLRPC$Document r7 = r14.document
            boolean r7 = org.telegram.messenger.MessageObject.isGifDocument((org.telegram.tgnet.TLRPC$Document) r7)
            if (r7 != 0) goto L_0x0267
            org.telegram.tgnet.TLRPC$Document r7 = r14.document
            boolean r7 = org.telegram.messenger.MessageObject.isRoundVideoDocument(r7)
            if (r7 == 0) goto L_0x0229
            goto L_0x0267
        L_0x0229:
            java.lang.String r7 = r14.path
            if (r7 == 0) goto L_0x026c
            java.lang.String r8 = "vthumb"
            boolean r8 = r7.startsWith(r8)
            if (r8 != 0) goto L_0x026c
            java.lang.String r8 = "thumb"
            boolean r8 = r7.startsWith(r8)
            if (r8 != 0) goto L_0x026c
            java.lang.String r8 = "jpg"
            java.lang.String r7 = getHttpUrlExtension(r7, r8)
            java.lang.String r8 = "mp4"
            boolean r8 = r7.equals(r8)
            if (r8 != 0) goto L_0x0262
            java.lang.String r8 = "gif"
            boolean r7 = r7.equals(r8)
            if (r7 == 0) goto L_0x0254
            goto L_0x0262
        L_0x0254:
            java.lang.String r7 = "tgs"
            r15 = r36
            boolean r7 = r7.equals(r15)
            if (r7 == 0) goto L_0x026e
            r7 = 1
            r13.imageType = r7
            goto L_0x026e
        L_0x0262:
            r15 = r36
            r13.imageType = r9
            goto L_0x026e
        L_0x0267:
            r15 = r36
            r13.imageType = r9
            goto L_0x026e
        L_0x026c:
            r15 = r36
        L_0x026e:
            if (r10 != 0) goto L_0x04aa
            org.telegram.tgnet.TLRPC$PhotoSize r7 = r14.photoSize
            boolean r8 = r7 instanceof org.telegram.tgnet.TLRPC$TL_photoStrippedSize
            java.lang.String r9 = "g"
            if (r8 != 0) goto L_0x048a
            boolean r7 = r7 instanceof org.telegram.tgnet.TLRPC$TL_photoPathSize
            if (r7 == 0) goto L_0x027e
            goto L_0x048a
        L_0x027e:
            org.telegram.messenger.SecureDocument r7 = r14.secureDocument
            if (r7 == 0) goto L_0x029f
            r13.secureDocument = r7
            org.telegram.tgnet.TLRPC$TL_secureFile r3 = r7.secureFile
            int r3 = r3.dc_id
            r4 = -2147483648(0xfffffffvar_, float:-0.0)
            if (r3 != r4) goto L_0x028e
            r5 = 1
            goto L_0x028f
        L_0x028e:
            r5 = 0
        L_0x028f:
            java.io.File r10 = new java.io.File
            java.io.File r3 = org.telegram.messenger.FileLoader.getDirectory(r16)
            r10.<init>(r3, r2)
            r0 = r37
            r3 = r5
        L_0x029b:
            r5 = r9
        L_0x029c:
            r1 = 1
            goto L_0x0493
        L_0x029f:
            boolean r7 = r9.equals(r12)
            java.lang.String r8 = ".svg"
            java.lang.String r10 = "application/x-tgwallpattern"
            r25 = r5
            java.lang.String r5 = "application/x-tgsticker"
            r33 = r6
            java.lang.String r6 = "application/x-tgsdice"
            if (r7 != 0) goto L_0x0354
            r7 = r37
            r11 = r38
            if (r7 != 0) goto L_0x02bf
            if (r11 <= 0) goto L_0x02bf
            java.lang.String r1 = r14.path
            if (r1 != 0) goto L_0x02bf
            if (r3 == 0) goto L_0x0356
        L_0x02bf:
            java.io.File r1 = new java.io.File
            java.io.File r3 = org.telegram.messenger.FileLoader.getDirectory(r16)
            r1.<init>(r3, r2)
            boolean r3 = r1.exists()
            if (r3 == 0) goto L_0x02d0
            r3 = 1
            goto L_0x02ef
        L_0x02d0:
            r3 = 2
            if (r7 != r3) goto L_0x02ed
            java.io.File r1 = new java.io.File
            java.io.File r3 = org.telegram.messenger.FileLoader.getDirectory(r16)
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r2)
            java.lang.String r11 = ".enc"
            r4.append(r11)
            java.lang.String r4 = r4.toString()
            r1.<init>(r3, r4)
        L_0x02ed:
            r3 = r33
        L_0x02ef:
            org.telegram.tgnet.TLRPC$Document r4 = r14.document
            if (r4 == 0) goto L_0x034a
            boolean r11 = r4 instanceof org.telegram.messenger.DocumentObject.ThemeDocument
            if (r11 == 0) goto L_0x0309
            org.telegram.messenger.DocumentObject$ThemeDocument r4 = (org.telegram.messenger.DocumentObject.ThemeDocument) r4
            org.telegram.tgnet.TLRPC$Document r4 = r4.wallpaper
            if (r4 != 0) goto L_0x02ff
            r4 = 1
            goto L_0x0301
        L_0x02ff:
            r4 = r25
        L_0x0301:
            r5 = 5
            r13.imageType = r5
            r10 = r1
            r6 = r3
            r3 = r4
            r0 = r7
            goto L_0x029b
        L_0x0309:
            java.lang.String r4 = r4.mime_type
            boolean r4 = r6.equals(r4)
            if (r4 == 0) goto L_0x031b
            r4 = 1
            r13.imageType = r4
            r10 = r1
            r6 = r3
            r0 = r7
            r5 = r9
            r1 = 1
            goto L_0x0492
        L_0x031b:
            r4 = 1
            org.telegram.tgnet.TLRPC$Document r6 = r14.document
            java.lang.String r6 = r6.mime_type
            boolean r5 = r5.equals(r6)
            if (r5 == 0) goto L_0x0329
            r13.imageType = r4
            goto L_0x034a
        L_0x0329:
            org.telegram.tgnet.TLRPC$Document r4 = r14.document
            java.lang.String r4 = r4.mime_type
            boolean r4 = r10.equals(r4)
            if (r4 == 0) goto L_0x0337
            r4 = 3
            r13.imageType = r4
            goto L_0x034a
        L_0x0337:
            r4 = 3
            boolean r5 = org.telegram.messenger.BuildVars.DEBUG_PRIVATE_VERSION
            if (r5 == 0) goto L_0x034a
            org.telegram.tgnet.TLRPC$Document r5 = r14.document
            java.lang.String r5 = org.telegram.messenger.FileLoader.getDocumentFileName(r5)
            boolean r5 = r5.endsWith(r8)
            if (r5 == 0) goto L_0x034a
            r13.imageType = r4
        L_0x034a:
            r10 = r1
            r6 = r3
            r0 = r7
            r5 = r9
            r1 = 1
            r4 = 0
            r3 = r25
            goto L_0x0494
        L_0x0354:
            r7 = r37
        L_0x0356:
            org.telegram.tgnet.TLRPC$Document r1 = r14.document
            java.lang.String r3 = ".temp"
            if (r1 == 0) goto L_0x041a
            boolean r11 = r1 instanceof org.telegram.tgnet.TLRPC$TL_documentEncrypted
            if (r11 == 0) goto L_0x036a
            java.io.File r11 = new java.io.File
            java.io.File r0 = org.telegram.messenger.FileLoader.getDirectory(r16)
            r11.<init>(r0, r2)
            goto L_0x0385
        L_0x036a:
            boolean r0 = org.telegram.messenger.MessageObject.isVideoDocument(r1)
            if (r0 == 0) goto L_0x037b
            java.io.File r11 = new java.io.File
            r0 = 2
            java.io.File r15 = org.telegram.messenger.FileLoader.getDirectory(r0)
            r11.<init>(r15, r2)
            goto L_0x0385
        L_0x037b:
            java.io.File r11 = new java.io.File
            r0 = 3
            java.io.File r15 = org.telegram.messenger.FileLoader.getDirectory(r0)
            r11.<init>(r15, r2)
        L_0x0385:
            boolean r0 = r9.equals(r12)
            if (r0 == 0) goto L_0x03b8
            boolean r0 = r11.exists()
            if (r0 != 0) goto L_0x03b8
            java.io.File r0 = new java.io.File
            java.io.File r11 = org.telegram.messenger.FileLoader.getDirectory(r16)
            java.lang.StringBuilder r15 = new java.lang.StringBuilder
            r15.<init>()
            r34 = r9
            int r9 = r1.dc_id
            r15.append(r9)
            r15.append(r4)
            r35 = r8
            long r7 = r1.id
            r15.append(r7)
            r15.append(r3)
            java.lang.String r3 = r15.toString()
            r0.<init>(r11, r3)
            goto L_0x03bd
        L_0x03b8:
            r35 = r8
            r34 = r9
            r0 = r11
        L_0x03bd:
            boolean r3 = r1 instanceof org.telegram.messenger.DocumentObject.ThemeDocument
            if (r3 == 0) goto L_0x03d0
            r3 = r1
            org.telegram.messenger.DocumentObject$ThemeDocument r3 = (org.telegram.messenger.DocumentObject.ThemeDocument) r3
            org.telegram.tgnet.TLRPC$Document r3 = r3.wallpaper
            if (r3 != 0) goto L_0x03ca
            r3 = 1
            goto L_0x03cc
        L_0x03ca:
            r3 = r25
        L_0x03cc:
            r4 = 5
            r13.imageType = r4
            goto L_0x040d
        L_0x03d0:
            org.telegram.tgnet.TLRPC$Document r3 = r14.document
            java.lang.String r3 = r3.mime_type
            boolean r3 = r6.equals(r3)
            if (r3 == 0) goto L_0x03de
            r3 = 1
            r13.imageType = r3
            goto L_0x040d
        L_0x03de:
            r3 = 1
            java.lang.String r4 = r1.mime_type
            boolean r4 = r5.equals(r4)
            if (r4 == 0) goto L_0x03ea
            r13.imageType = r3
            goto L_0x040b
        L_0x03ea:
            java.lang.String r3 = r1.mime_type
            boolean r3 = r10.equals(r3)
            if (r3 == 0) goto L_0x03f6
            r5 = 3
            r13.imageType = r5
            goto L_0x040b
        L_0x03f6:
            r5 = 3
            boolean r3 = org.telegram.messenger.BuildVars.DEBUG_PRIVATE_VERSION
            if (r3 == 0) goto L_0x040b
            org.telegram.tgnet.TLRPC$Document r3 = r14.document
            java.lang.String r3 = org.telegram.messenger.FileLoader.getDocumentFileName(r3)
            r4 = r35
            boolean r3 = r3.endsWith(r4)
            if (r3 == 0) goto L_0x040b
            r13.imageType = r5
        L_0x040b:
            r3 = r25
        L_0x040d:
            int r5 = r1.size
            r6 = r33
            r10 = r0
            r4 = r5
            r1 = 1
            r5 = r34
            r0 = r37
            goto L_0x0494
        L_0x041a:
            r34 = r9
            r5 = 3
            org.telegram.messenger.WebFile r0 = r14.webFile
            if (r0 == 0) goto L_0x0434
            java.io.File r10 = new java.io.File
            java.io.File r0 = org.telegram.messenger.FileLoader.getDirectory(r5)
            r10.<init>(r0, r2)
            r3 = r25
            r6 = r33
            r5 = r34
            r0 = r37
            goto L_0x029c
        L_0x0434:
            r0 = r37
            r1 = 1
            if (r0 != r1) goto L_0x0443
            java.io.File r5 = new java.io.File
            java.io.File r6 = org.telegram.messenger.FileLoader.getDirectory(r16)
            r5.<init>(r6, r2)
            goto L_0x044c
        L_0x0443:
            java.io.File r5 = new java.io.File
            java.io.File r6 = org.telegram.messenger.FileLoader.getDirectory(r19)
            r5.<init>(r6, r2)
        L_0x044c:
            r10 = r5
            r5 = r34
            boolean r6 = r5.equals(r12)
            if (r6 == 0) goto L_0x0485
            org.telegram.tgnet.TLRPC$TL_fileLocationToBeDeprecated r6 = r14.location
            if (r6 == 0) goto L_0x0485
            boolean r6 = r10.exists()
            if (r6 != 0) goto L_0x0485
            java.io.File r10 = new java.io.File
            java.io.File r6 = org.telegram.messenger.FileLoader.getDirectory(r16)
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            org.telegram.tgnet.TLRPC$TL_fileLocationToBeDeprecated r8 = r14.location
            long r8 = r8.volume_id
            r7.append(r8)
            r7.append(r4)
            org.telegram.tgnet.TLRPC$TL_fileLocationToBeDeprecated r4 = r14.location
            int r4 = r4.local_id
            r7.append(r4)
            r7.append(r3)
            java.lang.String r3 = r7.toString()
            r10.<init>(r6, r3)
        L_0x0485:
            r3 = r25
            r6 = r33
            goto L_0x0493
        L_0x048a:
            r0 = r37
            r33 = r6
            r5 = r9
            r1 = 1
            r6 = r33
        L_0x0492:
            r3 = 1
        L_0x0493:
            r4 = 0
        L_0x0494:
            boolean r5 = r5.equals(r12)
            if (r5 == 0) goto L_0x04a4
            r5 = 2
            r13.imageType = r5
            r13.size = r4
            r7 = r29
            r11 = r10
            r9 = 1
            goto L_0x04a8
        L_0x04a4:
            r7 = r29
            r9 = r3
            r11 = r10
        L_0x04a8:
            r10 = r6
            goto L_0x04b8
        L_0x04aa:
            r0 = r37
            r25 = r5
            r33 = r6
            r1 = 1
            r9 = r25
            r7 = r29
            r11 = r10
            r10 = r33
        L_0x04b8:
            r13.type = r7
            r15 = r24
            r13.key = r15
            r13.filter = r12
            r13.imageLocation = r14
            r8 = r36
            r13.ext = r8
            r6 = r39
            r13.currentAccount = r6
            r5 = r32
            r13.parentObject = r5
            int r3 = r14.imageType
            if (r3 == 0) goto L_0x04d4
            r13.imageType = r3
        L_0x04d4:
            r4 = 2
            if (r0 != r4) goto L_0x04f3
            java.io.File r3 = new java.io.File
            java.io.File r1 = org.telegram.messenger.FileLoader.getInternalCacheDir()
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r2)
            java.lang.String r0 = ".enc.key"
            r4.append(r0)
            java.lang.String r0 = r4.toString()
            r3.<init>(r1, r0)
            r13.encryptionKeyPath = r3
        L_0x04f3:
            r3 = r13
            r0 = 2
            r4 = r26
            r1 = r5
            r5 = r24
            r6 = r28
            r12 = r37
            r7 = r29
            r0 = r20
            r8 = r27
            r3.addImageReceiver(r4, r5, r6, r7, r8)
            if (r9 != 0) goto L_0x05e2
            if (r10 != 0) goto L_0x05e2
            boolean r3 = r11.exists()
            if (r3 == 0) goto L_0x0513
            goto L_0x05e2
        L_0x0513:
            r13.url = r2
            r7 = r21
            java.util.HashMap<java.lang.String, org.telegram.messenger.ImageLoader$CacheImage> r3 = r7.imageLoadingByUrl
            r3.put(r2, r13)
            java.lang.String r2 = r14.path
            if (r2 == 0) goto L_0x0570
            java.lang.String r1 = org.telegram.messenger.Utilities.MD5(r2)
            java.io.File r2 = org.telegram.messenger.FileLoader.getDirectory(r16)
            java.io.File r3 = new java.io.File
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r1)
            java.lang.String r1 = "_temp.jpg"
            r4.append(r1)
            java.lang.String r1 = r4.toString()
            r3.<init>(r2, r1)
            r13.tempFilePath = r3
            r13.finalFilePath = r11
            java.lang.String r1 = r14.path
            boolean r0 = r1.startsWith(r0)
            if (r0 == 0) goto L_0x055c
            org.telegram.messenger.ImageLoader$ArtworkLoadTask r0 = new org.telegram.messenger.ImageLoader$ArtworkLoadTask
            r0.<init>(r13)
            r13.artworkTask = r0
            java.util.LinkedList<org.telegram.messenger.ImageLoader$ArtworkLoadTask> r1 = r7.artworkTasks
            r1.add(r0)
            r0 = 0
            r7.runArtworkTasks(r0)
            goto L_0x0607
        L_0x055c:
            r0 = 0
            org.telegram.messenger.ImageLoader$HttpImageTask r1 = new org.telegram.messenger.ImageLoader$HttpImageTask
            r2 = r38
            r1.<init>(r13, r2)
            r13.httpTask = r1
            java.util.LinkedList<org.telegram.messenger.ImageLoader$HttpImageTask> r2 = r7.httpTasks
            r2.add(r1)
            r7.runHttpTasks(r0)
            goto L_0x0607
        L_0x0570:
            r2 = r38
            r0 = 0
            org.telegram.tgnet.TLRPC$TL_fileLocationToBeDeprecated r3 = r14.location
            if (r3 == 0) goto L_0x0597
            if (r12 != 0) goto L_0x0581
            if (r2 <= 0) goto L_0x057f
            byte[] r2 = r14.key
            if (r2 == 0) goto L_0x0581
        L_0x057f:
            r6 = 1
            goto L_0x0582
        L_0x0581:
            r6 = r12
        L_0x0582:
            org.telegram.messenger.FileLoader r2 = org.telegram.messenger.FileLoader.getInstance(r39)
            r3 = r1
            if (r22 == 0) goto L_0x058b
            r5 = 2
            goto L_0x058c
        L_0x058b:
            r5 = 1
        L_0x058c:
            r1 = r2
            r2 = r30
            r3 = r32
            r4 = r36
            r1.loadFile(r2, r3, r4, r5, r6)
            goto L_0x05d0
        L_0x0597:
            r3 = r1
            org.telegram.tgnet.TLRPC$Document r1 = r14.document
            if (r1 == 0) goto L_0x05ab
            org.telegram.messenger.FileLoader r1 = org.telegram.messenger.FileLoader.getInstance(r39)
            org.telegram.tgnet.TLRPC$Document r2 = r14.document
            if (r22 == 0) goto L_0x05a6
            r4 = 2
            goto L_0x05a7
        L_0x05a6:
            r4 = 1
        L_0x05a7:
            r1.loadFile(r2, r3, r4, r12)
            goto L_0x05d0
        L_0x05ab:
            org.telegram.messenger.SecureDocument r1 = r14.secureDocument
            if (r1 == 0) goto L_0x05be
            org.telegram.messenger.FileLoader r1 = org.telegram.messenger.FileLoader.getInstance(r39)
            org.telegram.messenger.SecureDocument r2 = r14.secureDocument
            if (r22 == 0) goto L_0x05b9
            r4 = 2
            goto L_0x05ba
        L_0x05b9:
            r4 = 1
        L_0x05ba:
            r1.loadFile(r2, r4)
            goto L_0x05d0
        L_0x05be:
            org.telegram.messenger.WebFile r1 = r14.webFile
            if (r1 == 0) goto L_0x05d0
            org.telegram.messenger.FileLoader r1 = org.telegram.messenger.FileLoader.getInstance(r39)
            org.telegram.messenger.WebFile r2 = r14.webFile
            if (r22 == 0) goto L_0x05cc
            r4 = 2
            goto L_0x05cd
        L_0x05cc:
            r4 = 1
        L_0x05cd:
            r1.loadFile(r2, r4, r12)
        L_0x05d0:
            boolean r1 = r26.isForceLoding()
            if (r1 == 0) goto L_0x0607
            java.util.HashMap<java.lang.String, java.lang.Integer> r1 = r7.forceLoadingImages
            java.lang.String r2 = r13.key
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            r1.put(r2, r0)
            goto L_0x0607
        L_0x05e2:
            r7 = r21
            r13.finalFilePath = r11
            r13.imageLocation = r14
            org.telegram.messenger.ImageLoader$CacheOutTask r0 = new org.telegram.messenger.ImageLoader$CacheOutTask
            r0.<init>(r13)
            r13.cacheTask = r0
            java.util.HashMap<java.lang.String, org.telegram.messenger.ImageLoader$CacheImage> r0 = r7.imageLoadingByKeys
            r0.put(r15, r13)
            if (r22 == 0) goto L_0x05fe
            org.telegram.messenger.DispatchQueue r0 = r7.cacheThumbOutQueue
            org.telegram.messenger.ImageLoader$CacheOutTask r1 = r13.cacheTask
            r0.postRunnable(r1)
            goto L_0x0607
        L_0x05fe:
            org.telegram.messenger.DispatchQueue r0 = r7.cacheOutQueue
            org.telegram.messenger.ImageLoader$CacheOutTask r1 = r13.cacheTask
            r0.postRunnable(r1)
            goto L_0x0607
        L_0x0606:
            r7 = r0
        L_0x0607:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ImageLoader.lambda$createLoadOperationForImageReceiver$6(int, java.lang.String, java.lang.String, int, org.telegram.messenger.ImageReceiver, int, java.lang.String, int, org.telegram.messenger.ImageLocation, boolean, java.lang.Object, org.telegram.tgnet.TLRPC$Document, boolean, boolean, java.lang.String, int, int, int):void");
    }

    public void preloadArtwork(String str) {
        this.imageLoadQueue.postRunnable(new ImageLoader$$ExternalSyntheticLambda7(this, str));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$preloadArtwork$7(String str) {
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

    /* JADX WARNING: Code restructure failed: missing block: B:155:0x027a, code lost:
        if (r6.local_id >= 0) goto L_0x035b;
     */
    /* JADX WARNING: Removed duplicated region for block: B:104:0x01a6  */
    /* JADX WARNING: Removed duplicated region for block: B:105:0x01a9  */
    /* JADX WARNING: Removed duplicated region for block: B:113:0x01be  */
    /* JADX WARNING: Removed duplicated region for block: B:115:0x01c1  */
    /* JADX WARNING: Removed duplicated region for block: B:116:0x01c4  */
    /* JADX WARNING: Removed duplicated region for block: B:118:0x01c8  */
    /* JADX WARNING: Removed duplicated region for block: B:122:0x01e1  */
    /* JADX WARNING: Removed duplicated region for block: B:203:0x0390  */
    /* JADX WARNING: Removed duplicated region for block: B:220:0x03fe  */
    /* JADX WARNING: Removed duplicated region for block: B:227:0x041c A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:231:0x0437 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:236:0x0457  */
    /* JADX WARNING: Removed duplicated region for block: B:238:0x046d  */
    /* JADX WARNING: Removed duplicated region for block: B:246:0x04ac  */
    /* JADX WARNING: Removed duplicated region for block: B:248:0x04b3  */
    /* JADX WARNING: Removed duplicated region for block: B:262:0x050f  */
    /* JADX WARNING: Removed duplicated region for block: B:275:0x0384 A[EDGE_INSN: B:275:0x0384->B:201:0x0384 ?: BREAK  , SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:39:0x0093  */
    /* JADX WARNING: Removed duplicated region for block: B:40:0x009c  */
    /* JADX WARNING: Removed duplicated region for block: B:52:0x00cf  */
    /* JADX WARNING: Removed duplicated region for block: B:60:0x00ec  */
    /* JADX WARNING: Removed duplicated region for block: B:95:0x0185  */
    /* JADX WARNING: Removed duplicated region for block: B:97:0x0192  */
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
            if (r6 == 0) goto L_0x0078
            org.telegram.messenger.ImageLocation r0 = r37.getMediaLocation()
            if (r0 == 0) goto L_0x002f
            org.telegram.tgnet.TLRPC$Document r1 = r0.document
            boolean r1 = org.telegram.messenger.MessageObject.isAnimatedStickerDocument(r1, r8)
            if (r1 != 0) goto L_0x0025
            int r0 = r0.imageType
            if (r0 != r8) goto L_0x002f
        L_0x0025:
            org.telegram.messenger.LruCache<org.telegram.ui.Components.RLottieDrawable> r0 = r12.lottieMemCache
            java.lang.Object r0 = r0.get(r6)
            android.graphics.drawable.Drawable r0 = (android.graphics.drawable.Drawable) r0
        L_0x002d:
            r1 = r0
            goto L_0x0061
        L_0x002f:
            org.telegram.messenger.LruCache<android.graphics.drawable.BitmapDrawable> r0 = r12.memCache
            java.lang.Object r0 = r0.get(r6)
            android.graphics.drawable.Drawable r0 = (android.graphics.drawable.Drawable) r0
            if (r0 == 0) goto L_0x003e
            org.telegram.messenger.LruCache<android.graphics.drawable.BitmapDrawable> r1 = r12.memCache
            r1.moveToFront(r6)
        L_0x003e:
            if (r0 != 0) goto L_0x004f
            org.telegram.messenger.LruCache<android.graphics.drawable.BitmapDrawable> r0 = r12.smallImagesMemCache
            java.lang.Object r0 = r0.get(r6)
            android.graphics.drawable.Drawable r0 = (android.graphics.drawable.Drawable) r0
            if (r0 == 0) goto L_0x004f
            org.telegram.messenger.LruCache<android.graphics.drawable.BitmapDrawable> r1 = r12.smallImagesMemCache
            r1.moveToFront(r6)
        L_0x004f:
            if (r0 != 0) goto L_0x002d
            org.telegram.messenger.LruCache<android.graphics.drawable.BitmapDrawable> r0 = r12.wallpaperMemCache
            java.lang.Object r0 = r0.get(r6)
            android.graphics.drawable.Drawable r0 = (android.graphics.drawable.Drawable) r0
            if (r0 == 0) goto L_0x002d
            org.telegram.messenger.LruCache<android.graphics.drawable.BitmapDrawable> r1 = r12.wallpaperMemCache
            r1.moveToFront(r6)
            goto L_0x002d
        L_0x0061:
            if (r1 == 0) goto L_0x0078
            r12.cancelLoadingForImageReceiver(r13, r8)
            r3 = 3
            r4 = 1
            r0 = r37
            r2 = r6
            r5 = r14
            r0.setImageBitmapByKey(r1, r2, r3, r4, r5)
            boolean r0 = r37.isForcePreview()
            if (r0 != 0) goto L_0x0076
            return
        L_0x0076:
            r0 = 1
            goto L_0x0079
        L_0x0078:
            r0 = 0
        L_0x0079:
            java.lang.String r2 = r37.getImageKey()
            if (r0 != 0) goto L_0x00e5
            if (r2 == 0) goto L_0x00e5
            org.telegram.messenger.ImageLocation r1 = r37.getImageLocation()
            if (r1 == 0) goto L_0x009c
            org.telegram.tgnet.TLRPC$Document r3 = r1.document
            boolean r3 = org.telegram.messenger.MessageObject.isAnimatedStickerDocument(r3, r8)
            if (r3 != 0) goto L_0x0093
            int r1 = r1.imageType
            if (r1 != r8) goto L_0x009c
        L_0x0093:
            org.telegram.messenger.LruCache<org.telegram.ui.Components.RLottieDrawable> r1 = r12.lottieMemCache
            java.lang.Object r1 = r1.get(r2)
            android.graphics.drawable.Drawable r1 = (android.graphics.drawable.Drawable) r1
            goto L_0x00cd
        L_0x009c:
            org.telegram.messenger.LruCache<android.graphics.drawable.BitmapDrawable> r1 = r12.memCache
            java.lang.Object r1 = r1.get(r2)
            android.graphics.drawable.Drawable r1 = (android.graphics.drawable.Drawable) r1
            if (r1 == 0) goto L_0x00ab
            org.telegram.messenger.LruCache<android.graphics.drawable.BitmapDrawable> r3 = r12.memCache
            r3.moveToFront(r2)
        L_0x00ab:
            if (r1 != 0) goto L_0x00bc
            org.telegram.messenger.LruCache<android.graphics.drawable.BitmapDrawable> r1 = r12.smallImagesMemCache
            java.lang.Object r1 = r1.get(r2)
            android.graphics.drawable.Drawable r1 = (android.graphics.drawable.Drawable) r1
            if (r1 == 0) goto L_0x00bc
            org.telegram.messenger.LruCache<android.graphics.drawable.BitmapDrawable> r3 = r12.smallImagesMemCache
            r3.moveToFront(r2)
        L_0x00bc:
            if (r1 != 0) goto L_0x00cd
            org.telegram.messenger.LruCache<android.graphics.drawable.BitmapDrawable> r1 = r12.wallpaperMemCache
            java.lang.Object r1 = r1.get(r2)
            android.graphics.drawable.Drawable r1 = (android.graphics.drawable.Drawable) r1
            if (r1 == 0) goto L_0x00cd
            org.telegram.messenger.LruCache<android.graphics.drawable.BitmapDrawable> r3 = r12.wallpaperMemCache
            r3.moveToFront(r2)
        L_0x00cd:
            if (r1 == 0) goto L_0x00e5
            r12.cancelLoadingForImageReceiver(r13, r8)
            r3 = 0
            r4 = 1
            r0 = r37
            r5 = r14
            r0.setImageBitmapByKey(r1, r2, r3, r4, r5)
            boolean r0 = r37.isForcePreview()
            if (r0 != 0) goto L_0x00e3
            if (r6 != 0) goto L_0x00e3
            return
        L_0x00e3:
            r15 = 1
            goto L_0x00e6
        L_0x00e5:
            r15 = r0
        L_0x00e6:
            java.lang.String r2 = r37.getThumbKey()
            if (r2 == 0) goto L_0x0152
            org.telegram.messenger.ImageLocation r0 = r37.getThumbLocation()
            if (r0 == 0) goto L_0x0108
            org.telegram.tgnet.TLRPC$Document r1 = r0.document
            boolean r1 = org.telegram.messenger.MessageObject.isAnimatedStickerDocument(r1, r8)
            if (r1 != 0) goto L_0x00fe
            int r0 = r0.imageType
            if (r0 != r8) goto L_0x0108
        L_0x00fe:
            org.telegram.messenger.LruCache<org.telegram.ui.Components.RLottieDrawable> r0 = r12.lottieMemCache
            java.lang.Object r0 = r0.get(r2)
            android.graphics.drawable.Drawable r0 = (android.graphics.drawable.Drawable) r0
        L_0x0106:
            r1 = r0
            goto L_0x013a
        L_0x0108:
            org.telegram.messenger.LruCache<android.graphics.drawable.BitmapDrawable> r0 = r12.memCache
            java.lang.Object r0 = r0.get(r2)
            android.graphics.drawable.Drawable r0 = (android.graphics.drawable.Drawable) r0
            if (r0 == 0) goto L_0x0117
            org.telegram.messenger.LruCache<android.graphics.drawable.BitmapDrawable> r1 = r12.memCache
            r1.moveToFront(r2)
        L_0x0117:
            if (r0 != 0) goto L_0x0128
            org.telegram.messenger.LruCache<android.graphics.drawable.BitmapDrawable> r0 = r12.smallImagesMemCache
            java.lang.Object r0 = r0.get(r2)
            android.graphics.drawable.Drawable r0 = (android.graphics.drawable.Drawable) r0
            if (r0 == 0) goto L_0x0128
            org.telegram.messenger.LruCache<android.graphics.drawable.BitmapDrawable> r1 = r12.smallImagesMemCache
            r1.moveToFront(r2)
        L_0x0128:
            if (r0 != 0) goto L_0x0106
            org.telegram.messenger.LruCache<android.graphics.drawable.BitmapDrawable> r0 = r12.wallpaperMemCache
            java.lang.Object r0 = r0.get(r2)
            android.graphics.drawable.Drawable r0 = (android.graphics.drawable.Drawable) r0
            if (r0 == 0) goto L_0x0106
            org.telegram.messenger.LruCache<android.graphics.drawable.BitmapDrawable> r1 = r12.wallpaperMemCache
            r1.moveToFront(r2)
            goto L_0x0106
        L_0x013a:
            if (r1 == 0) goto L_0x0152
            r3 = 1
            r4 = 1
            r0 = r37
            r5 = r14
            r0.setImageBitmapByKey(r1, r2, r3, r4, r5)
            r12.cancelLoadingForImageReceiver(r13, r7)
            if (r15 == 0) goto L_0x0150
            boolean r0 = r37.isForcePreview()
            if (r0 == 0) goto L_0x0150
            return
        L_0x0150:
            r0 = 1
            goto L_0x0153
        L_0x0152:
            r0 = 0
        L_0x0153:
            java.lang.Object r1 = r37.getParentObject()
            org.telegram.tgnet.TLRPC$Document r2 = r37.getQulityThumbDocument()
            org.telegram.messenger.ImageLocation r5 = r37.getThumbLocation()
            java.lang.String r6 = r37.getThumbFilter()
            org.telegram.messenger.ImageLocation r3 = r37.getMediaLocation()
            java.lang.String r11 = r37.getMediaFilter()
            org.telegram.messenger.ImageLocation r4 = r37.getImageLocation()
            java.lang.String r10 = r37.getImageFilter()
            if (r4 != 0) goto L_0x0199
            boolean r9 = r37.isNeedsQualityThumb()
            if (r9 == 0) goto L_0x0199
            boolean r9 = r37.isCurrentKeyQuality()
            if (r9 == 0) goto L_0x0199
            boolean r9 = r1 instanceof org.telegram.messenger.MessageObject
            if (r9 == 0) goto L_0x0192
            r2 = r1
            org.telegram.messenger.MessageObject r2 = (org.telegram.messenger.MessageObject) r2
            org.telegram.tgnet.TLRPC$Document r2 = r2.getDocument()
            org.telegram.messenger.ImageLocation r2 = org.telegram.messenger.ImageLocation.getForDocument(r2)
        L_0x0190:
            r9 = 1
            goto L_0x019b
        L_0x0192:
            if (r2 == 0) goto L_0x0199
            org.telegram.messenger.ImageLocation r2 = org.telegram.messenger.ImageLocation.getForDocument(r2)
            goto L_0x0190
        L_0x0199:
            r2 = r4
            r9 = 0
        L_0x019b:
            java.lang.String r16 = "mp4"
            r8 = 2
            r18 = 0
            if (r2 == 0) goto L_0x01a9
            int r7 = r2.imageType
            if (r7 != r8) goto L_0x01a9
            r7 = r16
            goto L_0x01ab
        L_0x01a9:
            r7 = r18
        L_0x01ab:
            r20 = r2
            if (r3 == 0) goto L_0x01b4
            int r2 = r3.imageType
            if (r2 != r8) goto L_0x01b4
            goto L_0x01b6
        L_0x01b4:
            r16 = r18
        L_0x01b6:
            java.lang.String r2 = r37.getExt()
            java.lang.String r8 = "jpg"
            if (r2 != 0) goto L_0x01bf
            r2 = r8
        L_0x01bf:
            if (r7 != 0) goto L_0x01c4
            r22 = r2
            goto L_0x01c6
        L_0x01c4:
            r22 = r7
        L_0x01c6:
            if (r16 != 0) goto L_0x01ca
            r16 = r2
        L_0x01ca:
            r24 = r3
            r25 = r4
            r4 = r18
            r12 = r4
            r23 = r12
            r7 = r20
            r3 = 0
            r26 = 0
            r20 = r23
        L_0x01da:
            java.lang.String r13 = "."
            r27 = r15
            r15 = 2
            if (r3 >= r15) goto L_0x0384
            if (r3 != 0) goto L_0x01e9
            r15 = r7
            r28 = r14
            r14 = r22
            goto L_0x01ef
        L_0x01e9:
            r28 = r14
            r14 = r16
            r15 = r24
        L_0x01ef:
            if (r15 != 0) goto L_0x01f6
            r29 = r0
            r30 = r7
            goto L_0x0209
        L_0x01f6:
            r29 = r0
            if (r24 == 0) goto L_0x01ff
            r30 = r7
            r0 = r24
            goto L_0x0202
        L_0x01ff:
            r0 = r7
            r30 = r0
        L_0x0202:
            r7 = 0
            java.lang.String r0 = r15.getKey(r1, r0, r7)
            if (r0 != 0) goto L_0x0211
        L_0x0209:
            r31 = r6
            r32 = r10
        L_0x020d:
            r7 = r30
            goto L_0x0376
        L_0x0211:
            r31 = r6
            if (r24 == 0) goto L_0x0218
            r7 = r24
            goto L_0x021a
        L_0x0218:
            r7 = r30
        L_0x021a:
            r6 = 1
            java.lang.String r7 = r15.getKey(r1, r7, r6)
            java.lang.String r6 = r15.path
            if (r6 == 0) goto L_0x023f
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            r6.append(r7)
            r6.append(r13)
            java.lang.String r7 = r15.path
            java.lang.String r7 = getHttpUrlExtension(r7, r8)
            r6.append(r7)
            java.lang.String r7 = r6.toString()
            r32 = r10
            goto L_0x035b
        L_0x023f:
            org.telegram.tgnet.TLRPC$PhotoSize r6 = r15.photoSize
            r32 = r10
            boolean r10 = r6 instanceof org.telegram.tgnet.TLRPC$TL_photoStrippedSize
            if (r10 != 0) goto L_0x0349
            boolean r6 = r6 instanceof org.telegram.tgnet.TLRPC$TL_photoPathSize
            if (r6 == 0) goto L_0x024d
            goto L_0x0349
        L_0x024d:
            org.telegram.tgnet.TLRPC$TL_fileLocationToBeDeprecated r6 = r15.location
            if (r6 == 0) goto L_0x0280
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            r6.append(r7)
            r6.append(r13)
            r6.append(r14)
            java.lang.String r7 = r6.toString()
            java.lang.String r6 = r37.getExt()
            if (r6 != 0) goto L_0x027c
            org.telegram.tgnet.TLRPC$TL_fileLocationToBeDeprecated r6 = r15.location
            byte[] r10 = r6.key
            if (r10 != 0) goto L_0x027c
            long r13 = r6.volume_id
            r33 = -2147483648(0xfffffffvar_, double:NaN)
            int r10 = (r13 > r33 ? 1 : (r13 == r33 ? 0 : -1))
            if (r10 != 0) goto L_0x035b
            int r6 = r6.local_id
            if (r6 >= 0) goto L_0x035b
        L_0x027c:
            r26 = 1
            goto L_0x035b
        L_0x0280:
            org.telegram.messenger.WebFile r6 = r15.webFile
            if (r6 == 0) goto L_0x02a6
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
            goto L_0x035b
        L_0x02a6:
            org.telegram.messenger.SecureDocument r6 = r15.secureDocument
            if (r6 == 0) goto L_0x02be
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            r6.append(r7)
            r6.append(r13)
            r6.append(r14)
            java.lang.String r7 = r6.toString()
            goto L_0x035b
        L_0x02be:
            org.telegram.tgnet.TLRPC$Document r6 = r15.document
            if (r6 == 0) goto L_0x035b
            if (r3 != 0) goto L_0x02d7
            if (r9 == 0) goto L_0x02d7
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            java.lang.String r10 = "q_"
            r6.append(r10)
            r6.append(r0)
            java.lang.String r0 = r6.toString()
        L_0x02d7:
            org.telegram.tgnet.TLRPC$Document r6 = r15.document
            java.lang.String r6 = org.telegram.messenger.FileLoader.getDocumentFileName(r6)
            r10 = 46
            int r10 = r6.lastIndexOf(r10)
            r13 = -1
            java.lang.String r14 = ""
            if (r10 != r13) goto L_0x02ea
            r6 = r14
            goto L_0x02ee
        L_0x02ea:
            java.lang.String r6 = r6.substring(r10)
        L_0x02ee:
            int r10 = r6.length()
            r13 = 1
            if (r10 > r13) goto L_0x0313
            org.telegram.tgnet.TLRPC$Document r6 = r15.document
            java.lang.String r6 = r6.mime_type
            java.lang.String r10 = "video/mp4"
            boolean r6 = r10.equals(r6)
            if (r6 == 0) goto L_0x0304
            java.lang.String r14 = ".mp4"
            goto L_0x0314
        L_0x0304:
            org.telegram.tgnet.TLRPC$Document r6 = r15.document
            java.lang.String r6 = r6.mime_type
            java.lang.String r10 = "video/x-matroska"
            boolean r6 = r10.equals(r6)
            if (r6 == 0) goto L_0x0314
            java.lang.String r14 = ".mkv"
            goto L_0x0314
        L_0x0313:
            r14 = r6
        L_0x0314:
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            r6.append(r7)
            r6.append(r14)
            java.lang.String r7 = r6.toString()
            org.telegram.tgnet.TLRPC$Document r6 = r15.document
            boolean r6 = org.telegram.messenger.MessageObject.isVideoDocument(r6)
            if (r6 != 0) goto L_0x0345
            org.telegram.tgnet.TLRPC$Document r6 = r15.document
            boolean r6 = org.telegram.messenger.MessageObject.isGifDocument((org.telegram.tgnet.TLRPC$Document) r6)
            if (r6 != 0) goto L_0x0345
            org.telegram.tgnet.TLRPC$Document r6 = r15.document
            boolean r6 = org.telegram.messenger.MessageObject.isRoundVideoDocument(r6)
            if (r6 != 0) goto L_0x0345
            org.telegram.tgnet.TLRPC$Document r6 = r15.document
            boolean r6 = org.telegram.messenger.MessageObject.canPreviewDocument(r6)
            if (r6 != 0) goto L_0x0345
            r6 = 1
            goto L_0x0346
        L_0x0345:
            r6 = 0
        L_0x0346:
            r26 = r6
            goto L_0x035b
        L_0x0349:
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            r6.append(r7)
            r6.append(r13)
            r6.append(r14)
            java.lang.String r7 = r6.toString()
        L_0x035b:
            if (r3 != 0) goto L_0x0361
            r12 = r0
            r20 = r7
            goto L_0x0364
        L_0x0361:
            r4 = r0
            r23 = r7
        L_0x0364:
            if (r15 != r5) goto L_0x020d
            if (r3 != 0) goto L_0x036e
            r7 = r18
            r12 = r7
            r20 = r12
            goto L_0x0376
        L_0x036e:
            r4 = r18
            r23 = r4
            r24 = r23
            goto L_0x020d
        L_0x0376:
            int r3 = r3 + 1
            r15 = r27
            r14 = r28
            r0 = r29
            r6 = r31
            r10 = r32
            goto L_0x01da
        L_0x0384:
            r29 = r0
            r31 = r6
            r30 = r7
            r32 = r10
            r28 = r14
            if (r5 == 0) goto L_0x03fe
            org.telegram.messenger.ImageLocation r0 = r37.getStrippedLocation()
            if (r0 != 0) goto L_0x039c
            if (r24 == 0) goto L_0x039a
            r25 = r24
        L_0x039a:
            r0 = r25
        L_0x039c:
            r3 = 0
            java.lang.String r18 = r5.getKey(r1, r0, r3)
            r3 = 1
            java.lang.String r0 = r5.getKey(r1, r0, r3)
            java.lang.String r1 = r5.path
            if (r1 == 0) goto L_0x03c9
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r0)
            r1.append(r13)
            java.lang.String r0 = r5.path
            java.lang.String r0 = getHttpUrlExtension(r0, r8)
            r1.append(r0)
            java.lang.String r0 = r1.toString()
        L_0x03c2:
            r35 = r18
            r18 = r0
            r0 = r35
            goto L_0x0401
        L_0x03c9:
            org.telegram.tgnet.TLRPC$PhotoSize r1 = r5.photoSize
            boolean r6 = r1 instanceof org.telegram.tgnet.TLRPC$TL_photoStrippedSize
            if (r6 != 0) goto L_0x03eb
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC$TL_photoPathSize
            if (r1 == 0) goto L_0x03d4
            goto L_0x03eb
        L_0x03d4:
            org.telegram.tgnet.TLRPC$TL_fileLocationToBeDeprecated r1 = r5.location
            if (r1 == 0) goto L_0x03c2
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r0)
            r1.append(r13)
            r1.append(r2)
            java.lang.String r0 = r1.toString()
            goto L_0x03c2
        L_0x03eb:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r0)
            r1.append(r13)
            r1.append(r2)
            java.lang.String r0 = r1.toString()
            goto L_0x03c2
        L_0x03fe:
            r3 = 1
            r0 = r18
        L_0x0401:
            java.lang.String r1 = "@"
            if (r4 == 0) goto L_0x0419
            if (r11 == 0) goto L_0x0419
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            r6.append(r4)
            r6.append(r1)
            r6.append(r11)
            java.lang.String r4 = r6.toString()
        L_0x0419:
            r13 = r4
            if (r12 == 0) goto L_0x0433
            if (r32 == 0) goto L_0x0433
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r12)
            r4.append(r1)
            r10 = r32
            r4.append(r10)
            java.lang.String r12 = r4.toString()
            goto L_0x0435
        L_0x0433:
            r10 = r32
        L_0x0435:
            if (r0 == 0) goto L_0x044e
            if (r31 == 0) goto L_0x044e
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r0)
            r4.append(r1)
            r6 = r31
            r4.append(r6)
            java.lang.String r0 = r4.toString()
            goto L_0x0450
        L_0x044e:
            r6 = r31
        L_0x0450:
            r4 = r0
            java.lang.String r0 = r37.getUniqKeyPrefix()
            if (r0 == 0) goto L_0x046b
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = r37.getUniqKeyPrefix()
            r0.append(r1)
            r0.append(r12)
            java.lang.String r0 = r0.toString()
            r12 = r0
        L_0x046b:
            if (r30 == 0) goto L_0x04ac
            r7 = r30
            java.lang.String r0 = r7.path
            if (r0 == 0) goto L_0x04a7
            r8 = 0
            r9 = 1
            r11 = 1
            if (r29 == 0) goto L_0x047b
            r21 = 2
            goto L_0x047d
        L_0x047b:
            r21 = 1
        L_0x047d:
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
            goto L_0x0545
        L_0x04a7:
            r14 = r2
            r15 = r7
            r17 = r10
            goto L_0x04b1
        L_0x04ac:
            r14 = r2
            r17 = r10
            r15 = r30
        L_0x04b1:
            if (r24 == 0) goto L_0x050f
            int r0 = r37.getCacheType()
            r19 = 1
            if (r0 != 0) goto L_0x04c0
            if (r26 == 0) goto L_0x04c0
            r21 = 1
            goto L_0x04c2
        L_0x04c0:
            r21 = r0
        L_0x04c2:
            if (r21 != 0) goto L_0x04c6
            r8 = 1
            goto L_0x04c8
        L_0x04c6:
            r8 = r21
        L_0x04c8:
            if (r29 != 0) goto L_0x04dc
            r7 = 0
            r9 = 1
            r10 = 1
            r0 = r36
            r1 = r37
            r2 = r4
            r3 = r18
            r4 = r14
            r14 = r11
            r11 = r28
            r0.createLoadOperationForImageReceiver(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r11)
            goto L_0x04dd
        L_0x04dc:
            r14 = r11
        L_0x04dd:
            if (r27 != 0) goto L_0x04f5
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
        L_0x04f5:
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
            r8 = r21
            r11 = r28
            r0.createLoadOperationForImageReceiver(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r11)
            goto L_0x0545
        L_0x050f:
            int r0 = r37.getCacheType()
            if (r0 != 0) goto L_0x0519
            if (r26 == 0) goto L_0x0519
            r13 = 1
            goto L_0x051a
        L_0x0519:
            r13 = r0
        L_0x051a:
            if (r13 != 0) goto L_0x051e
            r8 = 1
            goto L_0x051f
        L_0x051e:
            r8 = r13
        L_0x051f:
            r7 = 0
            r9 = 1
            if (r29 == 0) goto L_0x0525
            r10 = 2
            goto L_0x0526
        L_0x0525:
            r10 = 1
        L_0x0526:
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
        L_0x0545:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ImageLoader.loadImageForImageReceiver(org.telegram.messenger.ImageReceiver):void");
    }

    /* access modifiers changed from: private */
    public void httpFileLoadError(String str) {
        this.imageLoadQueue.postRunnable(new ImageLoader$$ExternalSyntheticLambda6(this, str));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$httpFileLoadError$8(String str) {
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
        this.imageLoadQueue.postRunnable(new ImageLoader$$ExternalSyntheticLambda5(this, str));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$artworkLoadError$9(String str) {
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
        this.imageLoadQueue.postRunnable(new ImageLoader$$ExternalSyntheticLambda8(this, str, i, file));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$fileDidLoaded$10(String str, int i, File file) {
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
            this.imageLoadQueue.postRunnable(new ImageLoader$$ExternalSyntheticLambda3(this, str));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$fileDidFailedLoad$11(String str) {
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
        AndroidUtilities.runOnUIThread(new ImageLoader$$ExternalSyntheticLambda11(this, httpFileTask, i));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$runHttpFileLoadTasks$13(HttpFileTask httpFileTask, int i) {
        if (httpFileTask != null) {
            this.currentHttpFileLoadTasksCount--;
        }
        if (httpFileTask != null) {
            if (i == 1) {
                if (httpFileTask.canRetry) {
                    ImageLoader$$ExternalSyntheticLambda10 imageLoader$$ExternalSyntheticLambda10 = new ImageLoader$$ExternalSyntheticLambda10(this, new HttpFileTask(httpFileTask.url, httpFileTask.tempFile, httpFileTask.ext, httpFileTask.currentAccount));
                    this.retryHttpsTasks.put(httpFileTask.url, imageLoader$$ExternalSyntheticLambda10);
                    AndroidUtilities.runOnUIThread(imageLoader$$ExternalSyntheticLambda10, 1000);
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
    public /* synthetic */ void lambda$runHttpFileLoadTasks$12(HttpFileTask httpFileTask) {
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

    /* JADX WARNING: Missing exception handler attribute for start block: B:60:0x00d2 */
    /* JADX WARNING: Removed duplicated region for block: B:120:0x0177  */
    /* JADX WARNING: Removed duplicated region for block: B:28:0x0072  */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x0077  */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x0081  */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x008d  */
    /* JADX WARNING: Removed duplicated region for block: B:42:0x009e  */
    /* JADX WARNING: Removed duplicated region for block: B:43:0x00a0  */
    /* JADX WARNING: Removed duplicated region for block: B:46:0x00a7 A[SYNTHETIC, Splitter:B:46:0x00a7] */
    /* JADX WARNING: Removed duplicated region for block: B:48:0x00b1  */
    /* JADX WARNING: Removed duplicated region for block: B:65:0x00d6  */
    /* JADX WARNING: Removed duplicated region for block: B:81:0x00f5 A[SYNTHETIC, Splitter:B:81:0x00f5] */
    /* JADX WARNING: Removed duplicated region for block: B:89:0x0109  */
    /* JADX WARNING: Removed duplicated region for block: B:93:0x0116 A[SYNTHETIC, Splitter:B:93:0x0116] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static android.graphics.Bitmap loadBitmap(java.lang.String r11, android.net.Uri r12, float r13, float r14, boolean r15) {
        /*
            android.graphics.BitmapFactory$Options r0 = new android.graphics.BitmapFactory$Options
            r0.<init>()
            r1 = 1
            r0.inJustDecodeBounds = r1
            if (r11 != 0) goto L_0x003e
            if (r12 == 0) goto L_0x003e
            java.lang.String r2 = r12.getScheme()
            if (r2 == 0) goto L_0x003e
            java.lang.String r2 = r12.getScheme()
            java.lang.String r3 = "file"
            boolean r2 = r2.contains(r3)
            if (r2 == 0) goto L_0x0023
            java.lang.String r11 = r12.getPath()
            goto L_0x003e
        L_0x0023:
            int r2 = android.os.Build.VERSION.SDK_INT
            r3 = 30
            if (r2 < r3) goto L_0x0035
            java.lang.String r2 = r12.getScheme()
            java.lang.String r3 = "content"
            boolean r2 = r3.equals(r2)
            if (r2 != 0) goto L_0x003e
        L_0x0035:
            java.lang.String r11 = org.telegram.messenger.AndroidUtilities.getPath(r12)     // Catch:{ all -> 0x003a }
            goto L_0x003e
        L_0x003a:
            r2 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)
        L_0x003e:
            r2 = 0
            if (r11 == 0) goto L_0x0045
            android.graphics.BitmapFactory.decodeFile(r11, r0)
            goto L_0x0067
        L_0x0045:
            if (r12 == 0) goto L_0x0067
            android.content.Context r3 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x0062 }
            android.content.ContentResolver r3 = r3.getContentResolver()     // Catch:{ all -> 0x0062 }
            java.io.InputStream r3 = r3.openInputStream(r12)     // Catch:{ all -> 0x0062 }
            android.graphics.BitmapFactory.decodeStream(r3, r2, r0)     // Catch:{ all -> 0x0062 }
            r3.close()     // Catch:{ all -> 0x0062 }
            android.content.Context r3 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x0062 }
            android.content.ContentResolver r3 = r3.getContentResolver()     // Catch:{ all -> 0x0062 }
            java.io.InputStream r3 = r3.openInputStream(r12)     // Catch:{ all -> 0x0062 }
            goto L_0x0068
        L_0x0062:
            r11 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r11)
            return r2
        L_0x0067:
            r3 = r2
        L_0x0068:
            int r4 = r0.outWidth
            float r4 = (float) r4
            int r5 = r0.outHeight
            float r5 = (float) r5
            float r4 = r4 / r13
            float r5 = r5 / r14
            if (r15 == 0) goto L_0x0077
            float r13 = java.lang.Math.max(r4, r5)
            goto L_0x007b
        L_0x0077:
            float r13 = java.lang.Math.min(r4, r5)
        L_0x007b:
            r14 = 1065353216(0x3var_, float:1.0)
            int r15 = (r13 > r14 ? 1 : (r13 == r14 ? 0 : -1))
            if (r15 >= 0) goto L_0x0083
            r13 = 1065353216(0x3var_, float:1.0)
        L_0x0083:
            r15 = 0
            r0.inJustDecodeBounds = r15
            int r4 = (int) r13
            r0.inSampleSize = r4
            int r4 = r4 % 2
            if (r4 == 0) goto L_0x0098
            r4 = 1
        L_0x008e:
            int r5 = r4 * 2
            int r6 = r0.inSampleSize
            if (r5 >= r6) goto L_0x0096
            r4 = r5
            goto L_0x008e
        L_0x0096:
            r0.inSampleSize = r4
        L_0x0098:
            int r4 = android.os.Build.VERSION.SDK_INT
            r5 = 21
            if (r4 >= r5) goto L_0x00a0
            r4 = 1
            goto L_0x00a1
        L_0x00a0:
            r4 = 0
        L_0x00a1:
            r0.inPurgeable = r4
            java.lang.String r4 = "Orientation"
            if (r11 == 0) goto L_0x00b1
            androidx.exifinterface.media.ExifInterface r15 = new androidx.exifinterface.media.ExifInterface     // Catch:{ all -> 0x00dd }
            r15.<init>((java.lang.String) r11)     // Catch:{ all -> 0x00dd }
            int r15 = r15.getAttributeInt(r4, r1)     // Catch:{ all -> 0x00dd }
            goto L_0x00d3
        L_0x00b1:
            if (r12 == 0) goto L_0x00d3
            android.content.Context r5 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x00d3 }
            android.content.ContentResolver r5 = r5.getContentResolver()     // Catch:{ all -> 0x00d3 }
            java.io.InputStream r5 = r5.openInputStream(r12)     // Catch:{ all -> 0x00d3 }
            androidx.exifinterface.media.ExifInterface r6 = new androidx.exifinterface.media.ExifInterface     // Catch:{ all -> 0x00cc }
            r6.<init>((java.io.InputStream) r5)     // Catch:{ all -> 0x00cc }
            int r15 = r6.getAttributeInt(r4, r1)     // Catch:{ all -> 0x00cc }
            if (r5 == 0) goto L_0x00d3
            r5.close()     // Catch:{ all -> 0x00d3 }
            goto L_0x00d3
        L_0x00cc:
            r1 = move-exception
            if (r5 == 0) goto L_0x00d2
            r5.close()     // Catch:{ all -> 0x00d2 }
        L_0x00d2:
            throw r1     // Catch:{ all -> 0x00d3 }
        L_0x00d3:
            r1 = 3
            if (r15 == r1) goto L_0x00f5
            r1 = 6
            if (r15 == r1) goto L_0x00ea
            r1 = 8
            if (r15 == r1) goto L_0x00df
        L_0x00dd:
            r15 = r2
            goto L_0x0101
        L_0x00df:
            android.graphics.Matrix r15 = new android.graphics.Matrix     // Catch:{ all -> 0x00dd }
            r15.<init>()     // Catch:{ all -> 0x00dd }
            r1 = 1132920832(0x43870000, float:270.0)
            r15.postRotate(r1)     // Catch:{ all -> 0x0100 }
            goto L_0x0101
        L_0x00ea:
            android.graphics.Matrix r15 = new android.graphics.Matrix     // Catch:{ all -> 0x00dd }
            r15.<init>()     // Catch:{ all -> 0x00dd }
            r1 = 1119092736(0x42b40000, float:90.0)
            r15.postRotate(r1)     // Catch:{ all -> 0x0100 }
            goto L_0x0101
        L_0x00f5:
            android.graphics.Matrix r15 = new android.graphics.Matrix     // Catch:{ all -> 0x00dd }
            r15.<init>()     // Catch:{ all -> 0x00dd }
            r1 = 1127481344(0x43340000, float:180.0)
            r15.postRotate(r1)     // Catch:{ all -> 0x0100 }
            goto L_0x0101
        L_0x0100:
        L_0x0101:
            int r1 = r0.inSampleSize
            float r1 = (float) r1
            float r13 = r13 / r1
            int r1 = (r13 > r14 ? 1 : (r13 == r14 ? 0 : -1))
            if (r1 <= 0) goto L_0x0114
            if (r15 != 0) goto L_0x0110
            android.graphics.Matrix r15 = new android.graphics.Matrix
            r15.<init>()
        L_0x0110:
            float r14 = r14 / r13
            r15.postScale(r14, r14)
        L_0x0114:
            if (r11 == 0) goto L_0x0177
            android.graphics.Bitmap r2 = android.graphics.BitmapFactory.decodeFile(r11, r0)     // Catch:{ all -> 0x013c }
            if (r2 == 0) goto L_0x01bd
            boolean r12 = r0.inPurgeable     // Catch:{ all -> 0x013c }
            if (r12 == 0) goto L_0x0123
            org.telegram.messenger.Utilities.pinBitmap(r2)     // Catch:{ all -> 0x013c }
        L_0x0123:
            r5 = 0
            r6 = 0
            int r7 = r2.getWidth()     // Catch:{ all -> 0x013c }
            int r8 = r2.getHeight()     // Catch:{ all -> 0x013c }
            r10 = 1
            r4 = r2
            r9 = r15
            android.graphics.Bitmap r12 = org.telegram.messenger.Bitmaps.createBitmap(r4, r5, r6, r7, r8, r9, r10)     // Catch:{ all -> 0x013c }
            if (r12 == r2) goto L_0x01bd
            r2.recycle()     // Catch:{ all -> 0x013c }
            r2 = r12
            goto L_0x01bd
        L_0x013c:
            r12 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r12)
            org.telegram.messenger.ImageLoader r12 = getInstance()
            r12.clearMemory()
            if (r2 != 0) goto L_0x0159
            android.graphics.Bitmap r2 = android.graphics.BitmapFactory.decodeFile(r11, r0)     // Catch:{ all -> 0x0157 }
            if (r2 == 0) goto L_0x0159
            boolean r11 = r0.inPurgeable     // Catch:{ all -> 0x0157 }
            if (r11 == 0) goto L_0x0159
            org.telegram.messenger.Utilities.pinBitmap(r2)     // Catch:{ all -> 0x0157 }
            goto L_0x0159
        L_0x0157:
            r11 = move-exception
            goto L_0x0173
        L_0x0159:
            if (r2 == 0) goto L_0x01bd
            r5 = 0
            r6 = 0
            int r7 = r2.getWidth()     // Catch:{ all -> 0x0157 }
            int r8 = r2.getHeight()     // Catch:{ all -> 0x0157 }
            r10 = 1
            r4 = r2
            r9 = r15
            android.graphics.Bitmap r11 = org.telegram.messenger.Bitmaps.createBitmap(r4, r5, r6, r7, r8, r9, r10)     // Catch:{ all -> 0x0157 }
            if (r11 == r2) goto L_0x01bd
            r2.recycle()     // Catch:{ all -> 0x0157 }
            r2 = r11
            goto L_0x01bd
        L_0x0173:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r11)
            goto L_0x01bd
        L_0x0177:
            if (r12 == 0) goto L_0x01bd
            android.graphics.Bitmap r11 = android.graphics.BitmapFactory.decodeStream(r3, r2, r0)     // Catch:{ all -> 0x01ab }
            if (r11 == 0) goto L_0x01a1
            boolean r12 = r0.inPurgeable     // Catch:{ all -> 0x019e }
            if (r12 == 0) goto L_0x0186
            org.telegram.messenger.Utilities.pinBitmap(r11)     // Catch:{ all -> 0x019e }
        L_0x0186:
            r5 = 0
            r6 = 0
            int r7 = r11.getWidth()     // Catch:{ all -> 0x019e }
            int r8 = r11.getHeight()     // Catch:{ all -> 0x019e }
            r10 = 1
            r4 = r11
            r9 = r15
            android.graphics.Bitmap r12 = org.telegram.messenger.Bitmaps.createBitmap(r4, r5, r6, r7, r8, r9, r10)     // Catch:{ all -> 0x019e }
            if (r12 == r11) goto L_0x01a1
            r11.recycle()     // Catch:{ all -> 0x019e }
            r2 = r12
            goto L_0x01a2
        L_0x019e:
            r12 = move-exception
            r2 = r11
            goto L_0x01ac
        L_0x01a1:
            r2 = r11
        L_0x01a2:
            r3.close()     // Catch:{ all -> 0x01a6 }
            goto L_0x01bd
        L_0x01a6:
            r11 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r11)
            goto L_0x01bd
        L_0x01ab:
            r12 = move-exception
        L_0x01ac:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r12)     // Catch:{ all -> 0x01b3 }
            r3.close()     // Catch:{ all -> 0x01a6 }
            goto L_0x01bd
        L_0x01b3:
            r11 = move-exception
            r3.close()     // Catch:{ all -> 0x01b8 }
            goto L_0x01bc
        L_0x01b8:
            r12 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r12)
        L_0x01bc:
            throw r11
        L_0x01bd:
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

    /* JADX WARNING: Removed duplicated region for block: B:29:0x00a3  */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x00a8  */
    /* JADX WARNING: Removed duplicated region for block: B:36:0x00c0 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:42:0x00f3  */
    /* JADX WARNING: Removed duplicated region for block: B:45:0x0103  */
    /* JADX WARNING: Removed duplicated region for block: B:47:0x0119  */
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
            goto L_0x0083
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
            org.telegram.tgnet.TLRPC$TL_photoSize_layer127 r0 = new org.telegram.tgnet.TLRPC$TL_photoSize_layer127
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
            goto L_0x0083
        L_0x005e:
            r11 = 320(0x140, float:4.48E-43)
            if (r10 > r11) goto L_0x0069
            if (r9 > r11) goto L_0x0069
            java.lang.String r9 = "m"
            r0.type = r9
            goto L_0x0083
        L_0x0069:
            r11 = 800(0x320, float:1.121E-42)
            if (r10 > r11) goto L_0x0074
            if (r9 > r11) goto L_0x0074
            java.lang.String r9 = "x"
            r0.type = r9
            goto L_0x0083
        L_0x0074:
            r11 = 1280(0x500, float:1.794E-42)
            if (r10 > r11) goto L_0x007f
            if (r9 > r11) goto L_0x007f
            java.lang.String r9 = "y"
            r0.type = r9
            goto L_0x0083
        L_0x007f:
            java.lang.String r9 = "w"
            r0.type = r9
        L_0x0083:
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
            if (r25 == 0) goto L_0x00a8
            java.io.File r5 = org.telegram.messenger.FileLoader.getDirectory(r10)
            goto L_0x00b7
        L_0x00a8:
            long r11 = r8.volume_id
            int r8 = (r11 > r6 ? 1 : (r11 == r6 ? 0 : -1))
            if (r8 == 0) goto L_0x00b3
            java.io.File r5 = org.telegram.messenger.FileLoader.getDirectory(r5)
            goto L_0x00b7
        L_0x00b3:
            java.io.File r5 = org.telegram.messenger.FileLoader.getDirectory(r10)
        L_0x00b7:
            java.io.File r6 = new java.io.File
            r6.<init>(r5, r9)
            android.graphics.Bitmap$CompressFormat r5 = android.graphics.Bitmap.CompressFormat.JPEG
            if (r2 != r5) goto L_0x00e9
            if (r16 == 0) goto L_0x00e9
            boolean r5 = org.telegram.messenger.BuildVars.DEBUG_VERSION
            if (r5 == 0) goto L_0x00e9
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
            goto L_0x0101
        L_0x00e9:
            java.io.FileOutputStream r5 = new java.io.FileOutputStream
            r5.<init>(r6)
            r4.compress(r15, r3, r5)
            if (r23 != 0) goto L_0x00fe
            java.nio.channels.FileChannel r6 = r5.getChannel()
            long r6 = r6.size()
            int r7 = (int) r6
            r0.size = r7
        L_0x00fe:
            r5.close()
        L_0x0101:
            if (r23 == 0) goto L_0x0117
            java.io.ByteArrayOutputStream r5 = new java.io.ByteArrayOutputStream
            r5.<init>()
            r4.compress(r15, r3, r5)
            byte[] r2 = r5.toByteArray()
            r0.bytes = r2
            int r2 = r2.length
            r0.size = r2
            r5.close()
        L_0x0117:
            if (r4 == r1) goto L_0x011c
            r4.recycle()
        L_0x011c:
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
            TLRPC$TL_photoSize_layer127 tLRPC$TL_photoSize_layer127 = new TLRPC$TL_photoSize_layer127();
            tLRPC$TL_photoSize_layer127.w = findPhotoCachedSize.w;
            tLRPC$TL_photoSize_layer127.h = findPhotoCachedSize.h;
            tLRPC$TL_photoSize_layer127.location = findPhotoCachedSize.location;
            tLRPC$TL_photoSize_layer127.size = findPhotoCachedSize.size;
            tLRPC$TL_photoSize_layer127.type = findPhotoCachedSize.type;
            TLRPC$MessageMedia tLRPC$MessageMedia = tLRPC$Message.media;
            if (tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaPhoto) {
                int size = tLRPC$MessageMedia.photo.sizes.size();
                while (i < size) {
                    if (tLRPC$Message.media.photo.sizes.get(i) instanceof TLRPC$TL_photoCachedSize) {
                        tLRPC$Message.media.photo.sizes.set(i, tLRPC$TL_photoSize_layer127);
                        return;
                    }
                    i++;
                }
            } else if (tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaDocument) {
                int size2 = tLRPC$MessageMedia.document.thumbs.size();
                while (i < size2) {
                    if (tLRPC$Message.media.document.thumbs.get(i) instanceof TLRPC$TL_photoCachedSize) {
                        tLRPC$Message.media.document.thumbs.set(i, tLRPC$TL_photoSize_layer127);
                        return;
                    }
                    i++;
                }
            } else if (tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaWebPage) {
                int size3 = tLRPC$MessageMedia.webpage.photo.sizes.size();
                while (i < size3) {
                    if (tLRPC$Message.media.webpage.photo.sizes.get(i) instanceof TLRPC$TL_photoCachedSize) {
                        tLRPC$Message.media.webpage.photo.sizes.set(i, tLRPC$TL_photoSize_layer127);
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
                        if (!getInstance().isInMemCache(format, false) && (strippedPhotoBitmap = getStrippedPhotoBitmap(tLRPC$PhotoSize.bytes, (String) null)) != null) {
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
            TLRPC$TL_photoSize_layer127 tLRPC$TL_photoSize_layer127 = new TLRPC$TL_photoSize_layer127();
            tLRPC$TL_photoSize_layer127.w = findPhotoCachedSize.w;
            tLRPC$TL_photoSize_layer127.h = findPhotoCachedSize.h;
            tLRPC$TL_photoSize_layer127.location = findPhotoCachedSize.location;
            tLRPC$TL_photoSize_layer127.size = findPhotoCachedSize.size;
            tLRPC$TL_photoSize_layer127.type = findPhotoCachedSize.type;
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
