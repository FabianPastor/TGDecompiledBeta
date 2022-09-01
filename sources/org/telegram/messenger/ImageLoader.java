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
import org.telegram.tgnet.TLRPC$MessageExtendedMedia;
import org.telegram.tgnet.TLRPC$MessageMedia;
import org.telegram.tgnet.TLRPC$Photo;
import org.telegram.tgnet.TLRPC$PhotoSize;
import org.telegram.tgnet.TLRPC$TL_documentAttributeVideo;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_fileLocationToBeDeprecated;
import org.telegram.tgnet.TLRPC$TL_fileLocationUnavailable;
import org.telegram.tgnet.TLRPC$TL_messageExtendedMediaPreview;
import org.telegram.tgnet.TLRPC$TL_messageMediaDocument;
import org.telegram.tgnet.TLRPC$TL_messageMediaInvoice;
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

    public static boolean hasAutoplayFilter(String str) {
        if (str == null) {
            return false;
        }
        String[] split = str.split("_");
        for (String equals : split) {
            if ("g".equals(equals)) {
                return true;
            }
        }
        return false;
    }

    public void moveToFront(String str) {
        if (str != null) {
            if (this.lottieMemCache.get(str) != null) {
                this.lottieMemCache.moveToFront(str);
            }
            if (this.memCache.get(str) != null) {
                this.memCache.moveToFront(str);
            }
            if (this.smallImagesMemCache.get(str) != null) {
                this.smallImagesMemCache.moveToFront(str);
            }
        }
    }

    public void putThumbsToCache(ArrayList<MessageThumb> arrayList) {
        for (int i = 0; i < arrayList.size(); i++) {
            putImageToCache(arrayList.get(i).drawable, arrayList.get(i).key, true);
        }
    }

    public LruCache<BitmapDrawable> getLottieMemCahce() {
        return this.lottieMemCache;
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
                java.lang.Integer r0 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r0)     // Catch:{ Exception -> 0x00f6 }
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
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r1, (boolean) r0)     // Catch:{ all -> 0x00ec }
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
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r1, (boolean) r0)     // Catch:{ all -> 0x013e }
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
        public long imageSize;
        private long lastProgressTime;
        private String overrideUrl;

        /* access modifiers changed from: private */
        public static /* synthetic */ void lambda$doInBackground$2(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        }

        public HttpImageTask(CacheImage cacheImage2, long j) {
            this.cacheImage = cacheImage2;
            this.imageSize = j;
        }

        public HttpImageTask(CacheImage cacheImage2, int i, String str) {
            this.cacheImage = cacheImage2;
            this.imageSize = (long) i;
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
        /* JADX WARNING: Code restructure failed: missing block: B:100:0x0179, code lost:
            r2 = move-exception;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:101:0x017a, code lost:
            r1 = r2;
            r2 = true;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:93:0x0169, code lost:
            if (r7 != -1) goto L_0x017d;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:95:?, code lost:
            r2 = r12.imageSize;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:96:0x016f, code lost:
            if (r2 == 0) goto L_0x0184;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:97:0x0171, code lost:
            reportProgress(r2, r2);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:98:0x0175, code lost:
            r2 = move-exception;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:99:0x0176, code lost:
            r1 = r2;
            r2 = true;
         */
        /* JADX WARNING: Removed duplicated region for block: B:113:0x018e A[Catch:{ all -> 0x0194 }] */
        /* JADX WARNING: Removed duplicated region for block: B:119:0x019c A[Catch:{ all -> 0x01a0 }] */
        /* JADX WARNING: Removed duplicated region for block: B:122:0x01a3 A[SYNTHETIC, Splitter:B:122:0x01a3] */
        /* JADX WARNING: Removed duplicated region for block: B:52:0x00ee A[SYNTHETIC, Splitter:B:52:0x00ee] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public java.lang.Boolean doInBackground(java.lang.Void... r13) {
            /*
                r12 = this;
                boolean r13 = r12.isCancelled()
                r0 = 0
                r1 = 1
                r2 = 0
                if (r13 != 0) goto L_0x00e7
                org.telegram.messenger.ImageLoader$CacheImage r13 = r12.cacheImage     // Catch:{ all -> 0x00a5 }
                org.telegram.messenger.ImageLocation r13 = r13.imageLocation     // Catch:{ all -> 0x00a5 }
                java.lang.String r13 = r13.path     // Catch:{ all -> 0x00a5 }
                java.lang.String r3 = "https://static-maps"
                boolean r3 = r13.startsWith(r3)     // Catch:{ all -> 0x00a5 }
                if (r3 != 0) goto L_0x001f
                java.lang.String r3 = "https://maps.googleapis"
                boolean r3 = r13.startsWith(r3)     // Catch:{ all -> 0x00a5 }
                if (r3 == 0) goto L_0x0057
            L_0x001f:
                org.telegram.messenger.ImageLoader$CacheImage r3 = r12.cacheImage     // Catch:{ all -> 0x00a5 }
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
                java.lang.Object r3 = r3.get(r13)     // Catch:{ all -> 0x00a5 }
                org.telegram.messenger.WebFile r3 = (org.telegram.messenger.WebFile) r3     // Catch:{ all -> 0x00a5 }
                if (r3 == 0) goto L_0x0057
                org.telegram.tgnet.TLRPC$TL_upload_getWebFile r4 = new org.telegram.tgnet.TLRPC$TL_upload_getWebFile     // Catch:{ all -> 0x00a5 }
                r4.<init>()     // Catch:{ all -> 0x00a5 }
                org.telegram.tgnet.TLRPC$InputWebFileLocation r3 = r3.location     // Catch:{ all -> 0x00a5 }
                r4.location = r3     // Catch:{ all -> 0x00a5 }
                r4.offset = r2     // Catch:{ all -> 0x00a5 }
                r4.limit = r2     // Catch:{ all -> 0x00a5 }
                org.telegram.messenger.ImageLoader$CacheImage r3 = r12.cacheImage     // Catch:{ all -> 0x00a5 }
                int r3 = r3.currentAccount     // Catch:{ all -> 0x00a5 }
                org.telegram.tgnet.ConnectionsManager r3 = org.telegram.tgnet.ConnectionsManager.getInstance(r3)     // Catch:{ all -> 0x00a5 }
                org.telegram.messenger.ImageLoader$HttpImageTask$$ExternalSyntheticLambda8 r5 = org.telegram.messenger.ImageLoader$HttpImageTask$$ExternalSyntheticLambda8.INSTANCE     // Catch:{ all -> 0x00a5 }
                r3.sendRequest(r4, r5)     // Catch:{ all -> 0x00a5 }
            L_0x0057:
                java.net.URL r3 = new java.net.URL     // Catch:{ all -> 0x00a5 }
                java.lang.String r4 = r12.overrideUrl     // Catch:{ all -> 0x00a5 }
                if (r4 == 0) goto L_0x005e
                r13 = r4
            L_0x005e:
                r3.<init>(r13)     // Catch:{ all -> 0x00a5 }
                java.net.URLConnection r13 = r3.openConnection()     // Catch:{ all -> 0x00a5 }
                java.net.HttpURLConnection r13 = (java.net.HttpURLConnection) r13     // Catch:{ all -> 0x00a5 }
                r12.httpConnection = r13     // Catch:{ all -> 0x00a5 }
                java.lang.String r3 = "User-Agent"
                java.lang.String r4 = "Mozilla/5.0 (iPhone; CPU iPhone OS 10_0 like Mac OS X) AppleWebKit/602.1.38 (KHTML, like Gecko) Version/10.0 Mobile/14A5297c Safari/602.1"
                r13.addRequestProperty(r3, r4)     // Catch:{ all -> 0x00a5 }
                java.net.HttpURLConnection r13 = r12.httpConnection     // Catch:{ all -> 0x00a5 }
                r3 = 5000(0x1388, float:7.006E-42)
                r13.setConnectTimeout(r3)     // Catch:{ all -> 0x00a5 }
                java.net.HttpURLConnection r13 = r12.httpConnection     // Catch:{ all -> 0x00a5 }
                r13.setReadTimeout(r3)     // Catch:{ all -> 0x00a5 }
                java.net.HttpURLConnection r13 = r12.httpConnection     // Catch:{ all -> 0x00a5 }
                r13.setInstanceFollowRedirects(r1)     // Catch:{ all -> 0x00a5 }
                boolean r13 = r12.isCancelled()     // Catch:{ all -> 0x00a5 }
                if (r13 != 0) goto L_0x00e7
                java.net.HttpURLConnection r13 = r12.httpConnection     // Catch:{ all -> 0x00a5 }
                r13.connect()     // Catch:{ all -> 0x00a5 }
                java.net.HttpURLConnection r13 = r12.httpConnection     // Catch:{ all -> 0x00a5 }
                java.io.InputStream r13 = r13.getInputStream()     // Catch:{ all -> 0x00a5 }
                java.io.RandomAccessFile r3 = new java.io.RandomAccessFile     // Catch:{ all -> 0x00a0 }
                org.telegram.messenger.ImageLoader$CacheImage r4 = r12.cacheImage     // Catch:{ all -> 0x00a0 }
                java.io.File r4 = r4.tempFilePath     // Catch:{ all -> 0x00a0 }
                java.lang.String r5 = "rws"
                r3.<init>(r4, r5)     // Catch:{ all -> 0x00a0 }
                r12.fileOutputStream = r3     // Catch:{ all -> 0x00a0 }
                goto L_0x00e8
            L_0x00a0:
                r3 = move-exception
                r11 = r3
                r3 = r13
                r13 = r11
                goto L_0x00a7
            L_0x00a5:
                r13 = move-exception
                r3 = r0
            L_0x00a7:
                boolean r4 = r13 instanceof java.net.SocketTimeoutException
                if (r4 == 0) goto L_0x00b4
                boolean r4 = org.telegram.messenger.ApplicationLoader.isNetworkOnline()
                if (r4 == 0) goto L_0x00ba
                r12.canRetry = r2
                goto L_0x00ba
            L_0x00b4:
                boolean r4 = r13 instanceof java.net.UnknownHostException
                if (r4 == 0) goto L_0x00bc
                r12.canRetry = r2
            L_0x00ba:
                r4 = 0
                goto L_0x00e2
            L_0x00bc:
                boolean r4 = r13 instanceof java.net.SocketException
                if (r4 == 0) goto L_0x00d5
                java.lang.String r4 = r13.getMessage()
                if (r4 == 0) goto L_0x00ba
                java.lang.String r4 = r13.getMessage()
                java.lang.String r5 = "ECONNRESET"
                boolean r4 = r4.contains(r5)
                if (r4 == 0) goto L_0x00ba
                r12.canRetry = r2
                goto L_0x00ba
            L_0x00d5:
                boolean r4 = r13 instanceof java.io.FileNotFoundException
                if (r4 == 0) goto L_0x00dc
                r12.canRetry = r2
                goto L_0x00ba
            L_0x00dc:
                boolean r4 = r13 instanceof java.io.InterruptedIOException
                if (r4 == 0) goto L_0x00e1
                goto L_0x00ba
            L_0x00e1:
                r4 = 1
            L_0x00e2:
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r13, (boolean) r4)
                r13 = r3
                goto L_0x00e8
            L_0x00e7:
                r13 = r0
            L_0x00e8:
                boolean r3 = r12.isCancelled()
                if (r3 != 0) goto L_0x018a
                java.net.HttpURLConnection r3 = r12.httpConnection     // Catch:{ Exception -> 0x0105 }
                if (r3 == 0) goto L_0x0109
                int r3 = r3.getResponseCode()     // Catch:{ Exception -> 0x0105 }
                r4 = 200(0xc8, float:2.8E-43)
                if (r3 == r4) goto L_0x0109
                r4 = 202(0xca, float:2.83E-43)
                if (r3 == r4) goto L_0x0109
                r4 = 304(0x130, float:4.26E-43)
                if (r3 == r4) goto L_0x0109
                r12.canRetry = r2     // Catch:{ Exception -> 0x0105 }
                goto L_0x0109
            L_0x0105:
                r3 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r3)
            L_0x0109:
                long r3 = r12.imageSize
                r5 = 0
                int r7 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
                if (r7 != 0) goto L_0x0143
                java.net.HttpURLConnection r3 = r12.httpConnection
                if (r3 == 0) goto L_0x0143
                java.util.Map r3 = r3.getHeaderFields()     // Catch:{ Exception -> 0x013f }
                if (r3 == 0) goto L_0x0143
                java.lang.String r4 = "content-Length"
                java.lang.Object r3 = r3.get(r4)     // Catch:{ Exception -> 0x013f }
                java.util.List r3 = (java.util.List) r3     // Catch:{ Exception -> 0x013f }
                if (r3 == 0) goto L_0x0143
                boolean r4 = r3.isEmpty()     // Catch:{ Exception -> 0x013f }
                if (r4 != 0) goto L_0x0143
                java.lang.Object r3 = r3.get(r2)     // Catch:{ Exception -> 0x013f }
                java.lang.String r3 = (java.lang.String) r3     // Catch:{ Exception -> 0x013f }
                if (r3 == 0) goto L_0x0143
                java.lang.Integer r3 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r3)     // Catch:{ Exception -> 0x013f }
                int r3 = r3.intValue()     // Catch:{ Exception -> 0x013f }
                long r3 = (long) r3     // Catch:{ Exception -> 0x013f }
                r12.imageSize = r3     // Catch:{ Exception -> 0x013f }
                goto L_0x0143
            L_0x013f:
                r3 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r3)
            L_0x0143:
                if (r13 == 0) goto L_0x018a
                r3 = 8192(0x2000, float:1.14794E-41)
                byte[] r3 = new byte[r3]     // Catch:{ all -> 0x0186 }
                r4 = 0
            L_0x014a:
                boolean r7 = r12.isCancelled()     // Catch:{ all -> 0x0186 }
                if (r7 == 0) goto L_0x0151
                goto L_0x017d
            L_0x0151:
                int r7 = r13.read(r3)     // Catch:{ Exception -> 0x017f }
                if (r7 <= 0) goto L_0x0168
                int r4 = r4 + r7
                java.io.RandomAccessFile r8 = r12.fileOutputStream     // Catch:{ Exception -> 0x017f }
                r8.write(r3, r2, r7)     // Catch:{ Exception -> 0x017f }
                long r7 = r12.imageSize     // Catch:{ Exception -> 0x017f }
                int r9 = (r7 > r5 ? 1 : (r7 == r5 ? 0 : -1))
                if (r9 == 0) goto L_0x014a
                long r9 = (long) r4     // Catch:{ Exception -> 0x017f }
                r12.reportProgress(r9, r7)     // Catch:{ Exception -> 0x017f }
                goto L_0x014a
            L_0x0168:
                r3 = -1
                if (r7 != r3) goto L_0x017d
                long r2 = r12.imageSize     // Catch:{ Exception -> 0x0179, all -> 0x0175 }
                int r4 = (r2 > r5 ? 1 : (r2 == r5 ? 0 : -1))
                if (r4 == 0) goto L_0x0184
                r12.reportProgress(r2, r2)     // Catch:{ Exception -> 0x0179, all -> 0x0175 }
                goto L_0x0184
            L_0x0175:
                r2 = move-exception
                r1 = r2
                r2 = 1
                goto L_0x0187
            L_0x0179:
                r2 = move-exception
                r1 = r2
                r2 = 1
                goto L_0x0180
            L_0x017d:
                r1 = 0
                goto L_0x0184
            L_0x017f:
                r1 = move-exception
            L_0x0180:
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)     // Catch:{ all -> 0x0186 }
                r1 = r2
            L_0x0184:
                r2 = r1
                goto L_0x018a
            L_0x0186:
                r1 = move-exception
            L_0x0187:
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)
            L_0x018a:
                java.io.RandomAccessFile r1 = r12.fileOutputStream     // Catch:{ all -> 0x0194 }
                if (r1 == 0) goto L_0x0198
                r1.close()     // Catch:{ all -> 0x0194 }
                r12.fileOutputStream = r0     // Catch:{ all -> 0x0194 }
                goto L_0x0198
            L_0x0194:
                r0 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            L_0x0198:
                java.net.HttpURLConnection r0 = r12.httpConnection     // Catch:{ all -> 0x01a0 }
                if (r0 == 0) goto L_0x01a1
                r0.disconnect()     // Catch:{ all -> 0x01a0 }
                goto L_0x01a1
            L_0x01a0:
            L_0x01a1:
                if (r13 == 0) goto L_0x01ab
                r13.close()     // Catch:{ all -> 0x01a7 }
                goto L_0x01ab
            L_0x01a7:
                r13 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r13)
            L_0x01ab:
                if (r2 == 0) goto L_0x01c1
                org.telegram.messenger.ImageLoader$CacheImage r13 = r12.cacheImage
                java.io.File r0 = r13.tempFilePath
                if (r0 == 0) goto L_0x01c1
                java.io.File r13 = r13.finalFilePath
                boolean r13 = r0.renameTo(r13)
                if (r13 != 0) goto L_0x01c1
                org.telegram.messenger.ImageLoader$CacheImage r13 = r12.cacheImage
                java.io.File r0 = r13.tempFilePath
                r13.finalFilePath = r0
            L_0x01c1:
                java.lang.Boolean r13 = java.lang.Boolean.valueOf(r2)
                return r13
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
                r37 = this;
                r1 = r37
                java.lang.Object r2 = r1.sync
                monitor-enter(r2)
                java.lang.Thread r0 = java.lang.Thread.currentThread()     // Catch:{ all -> 0x0d35 }
                r1.runningThread = r0     // Catch:{ all -> 0x0d35 }
                java.lang.Thread.interrupted()     // Catch:{ all -> 0x0d35 }
                boolean r0 = r1.isCancelled     // Catch:{ all -> 0x0d35 }
                if (r0 == 0) goto L_0x0014
                monitor-exit(r2)     // Catch:{ all -> 0x0d35 }
                return
            L_0x0014:
                monitor-exit(r2)     // Catch:{ all -> 0x0d35 }
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage
                org.telegram.messenger.ImageLocation r2 = r0.imageLocation
                org.telegram.tgnet.TLRPC$PhotoSize r3 = r2.photoSize
                boolean r4 = r3 instanceof org.telegram.tgnet.TLRPC$TL_photoStrippedSize
                if (r4 == 0) goto L_0x0037
                org.telegram.tgnet.TLRPC$TL_photoStrippedSize r3 = (org.telegram.tgnet.TLRPC$TL_photoStrippedSize) r3
                byte[] r0 = r3.bytes
                java.lang.String r2 = "b"
                android.graphics.Bitmap r0 = org.telegram.messenger.ImageLoader.getStrippedPhotoBitmap(r0, r2)
                if (r0 == 0) goto L_0x0031
                android.graphics.drawable.BitmapDrawable r5 = new android.graphics.drawable.BitmapDrawable
                r5.<init>(r0)
                goto L_0x0032
            L_0x0031:
                r5 = 0
            L_0x0032:
                r1.onPostExecute(r5)
                goto L_0x0d34
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
                goto L_0x0d34
            L_0x0057:
                r6 = 4
                r7 = 3
                r8 = 2
                r9 = 1
                r10 = 0
                if (r3 == r7) goto L_0x0ce3
                if (r3 != r6) goto L_0x0062
                goto L_0x0ce3
            L_0x0062:
                r13 = 1119092736(0x42b40000, float:90.0)
                if (r3 != r9) goto L_0x0296
                r0 = 1126865306(0x432a999a, float:170.6)
                int r2 = org.telegram.messenger.AndroidUtilities.dp(r0)
                r3 = 512(0x200, float:7.175E-43)
                int r2 = java.lang.Math.min(r3, r2)
                int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
                int r0 = java.lang.Math.min(r3, r0)
                org.telegram.messenger.ImageLoader$CacheImage r14 = r1.cacheImage
                java.lang.String r14 = r14.filter
                if (r14 == 0) goto L_0x0194
                java.lang.String r15 = "_"
                java.lang.String[] r14 = r14.split(r15)
                int r15 = r14.length
                if (r15 < r8) goto L_0x0114
                r0 = r14[r10]
                float r0 = java.lang.Float.parseFloat(r0)
                r2 = r14[r9]
                float r2 = java.lang.Float.parseFloat(r2)
                float r15 = org.telegram.messenger.AndroidUtilities.density
                float r15 = r15 * r0
                int r15 = (int) r15
                int r15 = java.lang.Math.min(r3, r15)
                float r16 = org.telegram.messenger.AndroidUtilities.density
                float r11 = r2 * r16
                int r11 = (int) r11
                int r3 = java.lang.Math.min(r3, r11)
                int r0 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1))
                if (r0 > 0) goto L_0x00c8
                int r0 = (r2 > r13 ? 1 : (r2 == r13 ? 0 : -1))
                if (r0 > 0) goto L_0x00c8
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage
                java.lang.String r0 = r0.filter
                java.lang.String r2 = "nolimit"
                boolean r0 = r0.contains(r2)
                if (r0 != 0) goto L_0x00c8
                r0 = 160(0xa0, float:2.24E-43)
                int r2 = java.lang.Math.min(r15, r0)
                int r0 = java.lang.Math.min(r3, r0)
                r3 = 1
                goto L_0x00cb
            L_0x00c8:
                r0 = r3
                r2 = r15
                r3 = 0
            L_0x00cb:
                int r11 = r14.length
                if (r11 < r7) goto L_0x00da
                java.lang.String r11 = "pcache"
                r13 = r14[r8]
                boolean r11 = r11.equals(r13)
                if (r11 == 0) goto L_0x00da
            L_0x00d8:
                r11 = 1
                goto L_0x00fa
            L_0x00da:
                org.telegram.messenger.ImageLoader$CacheImage r11 = r1.cacheImage
                java.lang.String r11 = r11.filter
                java.lang.String r13 = "pcache"
                boolean r11 = r11.contains(r13)
                if (r11 != 0) goto L_0x00d8
                org.telegram.messenger.ImageLoader$CacheImage r11 = r1.cacheImage
                java.lang.String r11 = r11.filter
                java.lang.String r13 = "nolimit"
                boolean r11 = r11.contains(r13)
                if (r11 != 0) goto L_0x00f9
                int r11 = org.telegram.messenger.SharedConfig.getDevicePerformanceClass()
                if (r11 == r8) goto L_0x00f9
                goto L_0x00d8
            L_0x00f9:
                r11 = 0
            L_0x00fa:
                org.telegram.messenger.ImageLoader$CacheImage r13 = r1.cacheImage
                java.lang.String r13 = r13.filter
                java.lang.String r15 = "lastframe"
                boolean r13 = r13.contains(r15)
                org.telegram.messenger.ImageLoader$CacheImage r15 = r1.cacheImage
                java.lang.String r15 = r15.filter
                java.lang.String r5 = "firstframe"
                boolean r5 = r15.contains(r5)
                if (r5 == 0) goto L_0x0112
                r5 = 1
                goto L_0x0118
            L_0x0112:
                r5 = 0
                goto L_0x0118
            L_0x0114:
                r3 = 0
                r5 = 0
                r11 = 0
                r13 = 0
            L_0x0118:
                int r15 = r14.length
                if (r15 < r7) goto L_0x0142
                java.lang.String r15 = "nr"
                r12 = r14[r8]
                boolean r12 = r15.equals(r12)
                if (r12 == 0) goto L_0x0128
                r12 = 0
            L_0x0126:
                r15 = 2
                goto L_0x0144
            L_0x0128:
                java.lang.String r12 = "nrs"
                r15 = r14[r8]
                boolean r12 = r12.equals(r15)
                if (r12 == 0) goto L_0x0135
                r12 = 0
                r15 = 3
                goto L_0x0144
            L_0x0135:
                java.lang.String r12 = "dice"
                r15 = r14[r8]
                boolean r12 = r12.equals(r15)
                if (r12 == 0) goto L_0x0142
                r12 = r14[r7]
                goto L_0x0126
            L_0x0142:
                r12 = 0
                r15 = 1
            L_0x0144:
                int r7 = r14.length
                if (r7 < r4) goto L_0x018f
                java.lang.String r7 = "c1"
                r4 = r14[r6]
                boolean r4 = r7.equals(r4)
                if (r4 == 0) goto L_0x0157
                r4 = 12
                r4 = r3
                r17 = 12
                goto L_0x0192
            L_0x0157:
                java.lang.String r4 = "c2"
                r7 = r14[r6]
                boolean r4 = r4.equals(r7)
                if (r4 == 0) goto L_0x0165
                r4 = r3
                r17 = 3
                goto L_0x0192
            L_0x0165:
                java.lang.String r4 = "c3"
                r7 = r14[r6]
                boolean r4 = r4.equals(r7)
                if (r4 == 0) goto L_0x0173
                r4 = r3
                r17 = 4
                goto L_0x0192
            L_0x0173:
                java.lang.String r4 = "c4"
                r7 = r14[r6]
                boolean r4 = r4.equals(r7)
                if (r4 == 0) goto L_0x0181
                r4 = r3
                r17 = 5
                goto L_0x0192
            L_0x0181:
                java.lang.String r4 = "c5"
                r6 = r14[r6]
                boolean r4 = r4.equals(r6)
                if (r4 == 0) goto L_0x018f
                r4 = r3
                r17 = 6
                goto L_0x0192
            L_0x018f:
                r4 = r3
                r17 = 0
            L_0x0192:
                r3 = r2
                goto L_0x019d
            L_0x0194:
                r3 = r2
                r4 = 0
                r5 = 0
                r11 = 0
                r12 = 0
                r13 = 0
                r15 = 1
                r17 = 0
            L_0x019d:
                r2 = r0
                if (r12 == 0) goto L_0x01b6
                java.lang.String r0 = "🎰"
                boolean r0 = r0.equals(r12)
                if (r0 == 0) goto L_0x01af
                org.telegram.ui.Components.SlotsDrawable r0 = new org.telegram.ui.Components.SlotsDrawable
                r0.<init>(r12, r3, r2)
                goto L_0x0275
            L_0x01af:
                org.telegram.ui.Components.RLottieDrawable r0 = new org.telegram.ui.Components.RLottieDrawable
                r0.<init>(r12, r3, r2)
                goto L_0x0275
            L_0x01b6:
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage
                java.io.File r0 = r0.finalFilePath
                java.io.RandomAccessFile r6 = new java.io.RandomAccessFile     // Catch:{ Exception -> 0x01f7, all -> 0x01f2 }
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage     // Catch:{ Exception -> 0x01f7, all -> 0x01f2 }
                java.io.File r0 = r0.finalFilePath     // Catch:{ Exception -> 0x01f7, all -> 0x01f2 }
                java.lang.String r7 = "r"
                r6.<init>(r0, r7)     // Catch:{ Exception -> 0x01f7, all -> 0x01f2 }
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage     // Catch:{ Exception -> 0x01f0 }
                int r0 = r0.type     // Catch:{ Exception -> 0x01f0 }
                if (r0 != r9) goto L_0x01d0
                byte[] r0 = org.telegram.messenger.ImageLoader.headerThumb     // Catch:{ Exception -> 0x01f0 }
                goto L_0x01d4
            L_0x01d0:
                byte[] r0 = org.telegram.messenger.ImageLoader.header     // Catch:{ Exception -> 0x01f0 }
            L_0x01d4:
                r6.readFully(r0, r10, r8)     // Catch:{ Exception -> 0x01f0 }
                byte r7 = r0[r10]     // Catch:{ Exception -> 0x01f0 }
                r8 = 31
                if (r7 != r8) goto L_0x01e5
                byte r0 = r0[r9]     // Catch:{ Exception -> 0x01f0 }
                r7 = -117(0xffffffffffffff8b, float:NaN)
                if (r0 != r7) goto L_0x01e5
                r7 = 1
                goto L_0x01e6
            L_0x01e5:
                r7 = 0
            L_0x01e6:
                r6.close()     // Catch:{ Exception -> 0x01ea }
                goto L_0x0208
            L_0x01ea:
                r0 = move-exception
                r6 = r0
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r6)
                goto L_0x0208
            L_0x01f0:
                r0 = move-exception
                goto L_0x01f9
            L_0x01f2:
                r0 = move-exception
                r2 = r0
                r5 = 0
                goto L_0x028a
            L_0x01f7:
                r0 = move-exception
                r6 = 0
            L_0x01f9:
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0, (boolean) r10)     // Catch:{ all -> 0x0287 }
                if (r6 == 0) goto L_0x0207
                r6.close()     // Catch:{ Exception -> 0x0202 }
                goto L_0x0207
            L_0x0202:
                r0 = move-exception
                r6 = r0
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r6)
            L_0x0207:
                r7 = 0
            L_0x0208:
                if (r13 != 0) goto L_0x020e
                if (r5 == 0) goto L_0x020d
                goto L_0x020e
            L_0x020d:
                r10 = r11
            L_0x020e:
                if (r10 == 0) goto L_0x023a
                org.telegram.messenger.utils.BitmapsCache$CacheOptions r0 = new org.telegram.messenger.utils.BitmapsCache$CacheOptions
                r0.<init>()
                org.telegram.messenger.ImageLoader$CacheImage r6 = r1.cacheImage
                java.lang.String r6 = r6.filter
                if (r6 == 0) goto L_0x0227
                java.lang.String r8 = "compress"
                boolean r6 = r6.contains(r8)
                if (r6 == 0) goto L_0x0227
                r6 = 60
                r0.compressQuality = r6
            L_0x0227:
                org.telegram.messenger.ImageLoader$CacheImage r6 = r1.cacheImage
                java.lang.String r6 = r6.filter
                if (r6 == 0) goto L_0x0237
                java.lang.String r8 = "flbk"
                boolean r6 = r6.contains(r8)
                if (r6 == 0) goto L_0x0237
                r0.fallback = r9
            L_0x0237:
                r16 = r0
                goto L_0x023c
            L_0x023a:
                r16 = 0
            L_0x023c:
                if (r7 == 0) goto L_0x025c
                org.telegram.ui.Components.RLottieDrawable r0 = new org.telegram.ui.Components.RLottieDrawable
                org.telegram.messenger.ImageLoader$CacheImage r6 = r1.cacheImage
                java.io.File r6 = r6.finalFilePath
                java.lang.String r21 = org.telegram.messenger.ImageLoader.decompressGzip(r6)
                r26 = 0
                r19 = r0
                r20 = r6
                r22 = r3
                r23 = r2
                r24 = r16
                r25 = r4
                r27 = r17
                r19.<init>(r20, r21, r22, r23, r24, r25, r26, r27)
                goto L_0x0275
            L_0x025c:
                org.telegram.ui.Components.RLottieDrawable r0 = new org.telegram.ui.Components.RLottieDrawable
                org.telegram.messenger.ImageLoader$CacheImage r6 = r1.cacheImage
                java.io.File r6 = r6.finalFilePath
                r25 = 0
                r19 = r0
                r20 = r6
                r21 = r3
                r22 = r2
                r23 = r16
                r24 = r4
                r26 = r17
                r19.<init>(r20, r21, r22, r23, r24, r25, r26)
            L_0x0275:
                if (r13 != 0) goto L_0x0282
                if (r5 == 0) goto L_0x027a
                goto L_0x0282
            L_0x027a:
                r0.setAutoRepeat(r15)
                r1.onPostExecute(r0)
                goto L_0x0d34
            L_0x0282:
                r1.loadLastFrame(r0, r2, r3, r13)
                goto L_0x0d34
            L_0x0287:
                r0 = move-exception
                r2 = r0
                r5 = r6
            L_0x028a:
                if (r5 == 0) goto L_0x0295
                r5.close()     // Catch:{ Exception -> 0x0290 }
                goto L_0x0295
            L_0x0290:
                r0 = move-exception
                r3 = r0
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r3)
            L_0x0295:
                throw r2
            L_0x0296:
                if (r3 != r8) goto L_0x0421
                if (r2 == 0) goto L_0x029f
                long r4 = r2.videoSeekTo
                r27 = r4
                goto L_0x02a1
            L_0x029f:
                r27 = 0
            L_0x02a1:
                java.lang.String r0 = r0.filter
                if (r0 == 0) goto L_0x02e4
                java.lang.String r2 = "_"
                java.lang.String[] r0 = r0.split(r2)
                int r2 = r0.length
                if (r2 < r8) goto L_0x02d0
                r2 = r0[r10]
                float r2 = java.lang.Float.parseFloat(r2)
                r3 = r0[r9]
                float r3 = java.lang.Float.parseFloat(r3)
                int r2 = (r2 > r13 ? 1 : (r2 == r13 ? 0 : -1))
                if (r2 > 0) goto L_0x02d0
                int r2 = (r3 > r13 ? 1 : (r3 == r13 ? 0 : -1))
                if (r2 > 0) goto L_0x02d0
                org.telegram.messenger.ImageLoader$CacheImage r2 = r1.cacheImage
                java.lang.String r2 = r2.filter
                java.lang.String r3 = "nolimit"
                boolean r2 = r2.contains(r3)
                if (r2 != 0) goto L_0x02d0
                r2 = 1
                goto L_0x02d1
            L_0x02d0:
                r2 = 0
            L_0x02d1:
                r3 = 0
                r4 = 0
            L_0x02d3:
                int r5 = r0.length
                if (r3 >= r5) goto L_0x02e6
                java.lang.String r5 = "pcache"
                r6 = r0[r3]
                boolean r5 = r5.equals(r6)
                if (r5 == 0) goto L_0x02e1
                r4 = 1
            L_0x02e1:
                int r3 = r3 + 1
                goto L_0x02d3
            L_0x02e4:
                r2 = 0
                r4 = 0
            L_0x02e6:
                org.telegram.messenger.ImageLoader r0 = org.telegram.messenger.ImageLoader.this
                org.telegram.messenger.ImageLoader$CacheImage r3 = r1.cacheImage
                java.lang.String r3 = r3.filter
                boolean r0 = r0.isAnimatedAvatar(r3)
                if (r0 != 0) goto L_0x02fe
                java.lang.String r0 = "g"
                org.telegram.messenger.ImageLoader$CacheImage r3 = r1.cacheImage
                java.lang.String r3 = r3.filter
                boolean r0 = r0.equals(r3)
                if (r0 == 0) goto L_0x037a
            L_0x02fe:
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage
                org.telegram.messenger.ImageLocation r3 = r0.imageLocation
                org.telegram.tgnet.TLRPC$Document r5 = r3.document
                boolean r6 = r5 instanceof org.telegram.tgnet.TLRPC$TL_documentEncrypted
                if (r6 != 0) goto L_0x037a
                if (r4 != 0) goto L_0x037a
                boolean r6 = r5 instanceof org.telegram.tgnet.TLRPC$Document
                if (r6 == 0) goto L_0x030f
                goto L_0x0310
            L_0x030f:
                r5 = 0
            L_0x0310:
                if (r5 == 0) goto L_0x0315
                long r6 = r0.size
                goto L_0x0317
            L_0x0315:
                long r6 = r3.currentSize
            L_0x0317:
                r22 = r6
                if (r4 == 0) goto L_0x0335
                org.telegram.messenger.utils.BitmapsCache$CacheOptions r0 = new org.telegram.messenger.utils.BitmapsCache$CacheOptions
                r0.<init>()
                org.telegram.messenger.ImageLoader$CacheImage r3 = r1.cacheImage
                java.lang.String r3 = r3.filter
                if (r3 == 0) goto L_0x0332
                java.lang.String r4 = "compress"
                boolean r3 = r3.contains(r4)
                if (r3 == 0) goto L_0x0332
                r3 = 60
                r0.compressQuality = r3
            L_0x0332:
                r31 = r0
                goto L_0x0337
            L_0x0335:
                r31 = 0
            L_0x0337:
                org.telegram.ui.Components.AnimatedFileDrawable r0 = new org.telegram.ui.Components.AnimatedFileDrawable
                org.telegram.messenger.ImageLoader$CacheImage r3 = r1.cacheImage
                java.io.File r4 = r3.finalFilePath
                r21 = 0
                if (r5 != 0) goto L_0x0346
                org.telegram.messenger.ImageLocation r6 = r3.imageLocation
                r25 = r6
                goto L_0x0348
            L_0x0346:
                r25 = 0
            L_0x0348:
                java.lang.Object r6 = r3.parentObject
                int r3 = r3.currentAccount
                r30 = 0
                r19 = r0
                r20 = r4
                r24 = r5
                r26 = r6
                r29 = r3
                r19.<init>(r20, r21, r22, r24, r25, r26, r27, r29, r30, r31)
                boolean r3 = org.telegram.messenger.MessageObject.isWebM(r5)
                if (r3 != 0) goto L_0x0375
                boolean r3 = org.telegram.messenger.MessageObject.isVideoSticker(r5)
                if (r3 != 0) goto L_0x0375
                org.telegram.messenger.ImageLoader r3 = org.telegram.messenger.ImageLoader.this
                org.telegram.messenger.ImageLoader$CacheImage r4 = r1.cacheImage
                java.lang.String r4 = r4.filter
                boolean r3 = r3.isAnimatedAvatar(r4)
                if (r3 == 0) goto L_0x0374
                goto L_0x0375
            L_0x0374:
                r9 = 0
            L_0x0375:
                r0.setIsWebmSticker(r9)
                goto L_0x0416
            L_0x037a:
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage
                java.lang.String r0 = r0.filter
                if (r0 == 0) goto L_0x03a2
                java.lang.String r3 = "_"
                java.lang.String[] r0 = r0.split(r3)
                int r3 = r0.length
                if (r3 < r8) goto L_0x03a2
                r3 = r0[r10]
                float r3 = java.lang.Float.parseFloat(r3)
                r0 = r0[r9]
                float r0 = java.lang.Float.parseFloat(r0)
                float r5 = org.telegram.messenger.AndroidUtilities.density
                float r3 = r3 * r5
                int r3 = (int) r3
                float r0 = r0 * r5
                int r0 = (int) r0
                r32 = r0
                r31 = r3
                goto L_0x03a6
            L_0x03a2:
                r31 = 0
                r32 = 0
            L_0x03a6:
                if (r4 == 0) goto L_0x03c2
                org.telegram.messenger.utils.BitmapsCache$CacheOptions r5 = new org.telegram.messenger.utils.BitmapsCache$CacheOptions
                r5.<init>()
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage
                java.lang.String r0 = r0.filter
                if (r0 == 0) goto L_0x03bf
                java.lang.String r3 = "compress"
                boolean r0 = r0.contains(r3)
                if (r0 == 0) goto L_0x03bf
                r3 = 60
                r5.compressQuality = r3
            L_0x03bf:
                r33 = r5
                goto L_0x03c4
            L_0x03c2:
                r33 = 0
            L_0x03c4:
                org.telegram.ui.Components.AnimatedFileDrawable r0 = new org.telegram.ui.Components.AnimatedFileDrawable
                org.telegram.messenger.ImageLoader$CacheImage r3 = r1.cacheImage
                java.io.File r4 = r3.finalFilePath
                java.lang.String r5 = "d"
                java.lang.String r3 = r3.filter
                boolean r21 = r5.equals(r3)
                r22 = 0
                org.telegram.messenger.ImageLoader$CacheImage r3 = r1.cacheImage
                org.telegram.messenger.ImageLocation r5 = r3.imageLocation
                org.telegram.tgnet.TLRPC$Document r5 = r5.document
                r25 = 0
                r26 = 0
                int r3 = r3.currentAccount
                r30 = 0
                r19 = r0
                r20 = r4
                r24 = r5
                r29 = r3
                r19.<init>(r20, r21, r22, r24, r25, r26, r27, r29, r30, r31, r32, r33)
                org.telegram.messenger.ImageLoader$CacheImage r3 = r1.cacheImage
                org.telegram.messenger.ImageLocation r3 = r3.imageLocation
                org.telegram.tgnet.TLRPC$Document r3 = r3.document
                boolean r3 = org.telegram.messenger.MessageObject.isWebM(r3)
                if (r3 != 0) goto L_0x0413
                org.telegram.messenger.ImageLoader$CacheImage r3 = r1.cacheImage
                org.telegram.messenger.ImageLocation r3 = r3.imageLocation
                org.telegram.tgnet.TLRPC$Document r3 = r3.document
                boolean r3 = org.telegram.messenger.MessageObject.isVideoSticker(r3)
                if (r3 != 0) goto L_0x0413
                org.telegram.messenger.ImageLoader r3 = org.telegram.messenger.ImageLoader.this
                org.telegram.messenger.ImageLoader$CacheImage r4 = r1.cacheImage
                java.lang.String r4 = r4.filter
                boolean r3 = r3.isAnimatedAvatar(r4)
                if (r3 == 0) goto L_0x0412
                goto L_0x0413
            L_0x0412:
                r9 = 0
            L_0x0413:
                r0.setIsWebmSticker(r9)
            L_0x0416:
                r0.setLimitFps(r2)
                java.lang.Thread.interrupted()
                r1.onPostExecute(r0)
                goto L_0x0d34
            L_0x0421:
                java.io.File r2 = r0.finalFilePath
                org.telegram.messenger.SecureDocument r3 = r0.secureDocument
                if (r3 != 0) goto L_0x043c
                java.io.File r0 = r0.encryptionKeyPath
                if (r0 == 0) goto L_0x043a
                if (r2 == 0) goto L_0x043a
                java.lang.String r0 = r2.getAbsolutePath()
                java.lang.String r3 = ".enc"
                boolean r0 = r0.endsWith(r3)
                if (r0 == 0) goto L_0x043a
                goto L_0x043c
            L_0x043a:
                r3 = 0
                goto L_0x043d
            L_0x043c:
                r3 = 1
            L_0x043d:
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage
                org.telegram.messenger.SecureDocument r0 = r0.secureDocument
                if (r0 == 0) goto L_0x0452
                org.telegram.messenger.SecureDocumentKey r6 = r0.secureDocumentKey
                org.telegram.tgnet.TLRPC$TL_secureFile r7 = r0.secureFile
                if (r7 == 0) goto L_0x044e
                byte[] r7 = r7.file_hash
                if (r7 == 0) goto L_0x044e
                goto L_0x0454
            L_0x044e:
                byte[] r0 = r0.fileHash
                r7 = r0
                goto L_0x0454
            L_0x0452:
                r6 = 0
                r7 = 0
            L_0x0454:
                int r0 = android.os.Build.VERSION.SDK_INT
                r11 = 19
                if (r0 >= r11) goto L_0x04c4
                java.io.RandomAccessFile r11 = new java.io.RandomAccessFile     // Catch:{ Exception -> 0x04a4, all -> 0x04a0 }
                java.lang.String r0 = "r"
                r11.<init>(r2, r0)     // Catch:{ Exception -> 0x04a4, all -> 0x04a0 }
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage     // Catch:{ Exception -> 0x049e }
                int r0 = r0.type     // Catch:{ Exception -> 0x049e }
                if (r0 != r9) goto L_0x046c
                byte[] r0 = org.telegram.messenger.ImageLoader.headerThumb     // Catch:{ Exception -> 0x049e }
                goto L_0x0470
            L_0x046c:
                byte[] r0 = org.telegram.messenger.ImageLoader.header     // Catch:{ Exception -> 0x049e }
            L_0x0470:
                int r12 = r0.length     // Catch:{ Exception -> 0x049e }
                r11.readFully(r0, r10, r12)     // Catch:{ Exception -> 0x049e }
                java.lang.String r12 = new java.lang.String     // Catch:{ Exception -> 0x049e }
                r12.<init>(r0)     // Catch:{ Exception -> 0x049e }
                java.lang.String r0 = r12.toLowerCase()     // Catch:{ Exception -> 0x049e }
                java.lang.String r0 = r0.toLowerCase()     // Catch:{ Exception -> 0x049e }
                java.lang.String r12 = "riff"
                boolean r12 = r0.startsWith(r12)     // Catch:{ Exception -> 0x049e }
                if (r12 == 0) goto L_0x0493
                java.lang.String r12 = "webp"
                boolean r0 = r0.endsWith(r12)     // Catch:{ Exception -> 0x049e }
                if (r0 == 0) goto L_0x0493
                r12 = 1
                goto L_0x0494
            L_0x0493:
                r12 = 0
            L_0x0494:
                r11.close()     // Catch:{ Exception -> 0x0498 }
                goto L_0x04c5
            L_0x0498:
                r0 = move-exception
                r11 = r0
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r11)
                goto L_0x04c5
            L_0x049e:
                r0 = move-exception
                goto L_0x04a6
            L_0x04a0:
                r0 = move-exception
                r2 = r0
                r5 = 0
                goto L_0x04b8
            L_0x04a4:
                r0 = move-exception
                r11 = 0
            L_0x04a6:
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ all -> 0x04b5 }
                if (r11 == 0) goto L_0x04c4
                r11.close()     // Catch:{ Exception -> 0x04af }
                goto L_0x04c4
            L_0x04af:
                r0 = move-exception
                r11 = r0
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r11)
                goto L_0x04c4
            L_0x04b5:
                r0 = move-exception
                r2 = r0
                r5 = r11
            L_0x04b8:
                if (r5 == 0) goto L_0x04c3
                r5.close()     // Catch:{ Exception -> 0x04be }
                goto L_0x04c3
            L_0x04be:
                r0 = move-exception
                r3 = r0
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r3)
            L_0x04c3:
                throw r2
            L_0x04c4:
                r12 = 0
            L_0x04c5:
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage
                org.telegram.messenger.ImageLocation r0 = r0.imageLocation
                java.lang.String r0 = r0.path
                r11 = 8
                if (r0 == 0) goto L_0x052c
                java.lang.String r13 = "thumb://"
                boolean r13 = r0.startsWith(r13)
                if (r13 == 0) goto L_0x04f9
                java.lang.String r13 = ":"
                int r13 = r0.indexOf(r13, r11)
                if (r13 < 0) goto L_0x04f1
                java.lang.String r14 = r0.substring(r11, r13)
                long r14 = java.lang.Long.parseLong(r14)
                java.lang.Long r14 = java.lang.Long.valueOf(r14)
                int r13 = r13 + r9
                java.lang.String r0 = r0.substring(r13)
                goto L_0x04f3
            L_0x04f1:
                r0 = 0
                r14 = 0
            L_0x04f3:
                r15 = r14
                r13 = 0
                r18 = 0
                r14 = r0
                goto L_0x0531
            L_0x04f9:
                java.lang.String r13 = "vthumb://"
                boolean r13 = r0.startsWith(r13)
                if (r13 == 0) goto L_0x051e
                java.lang.String r13 = ":"
                r14 = 9
                int r13 = r0.indexOf(r13, r14)
                if (r13 < 0) goto L_0x0519
                java.lang.String r0 = r0.substring(r14, r13)
                long r13 = java.lang.Long.parseLong(r0)
                java.lang.Long r0 = java.lang.Long.valueOf(r13)
                r13 = 1
                goto L_0x051b
            L_0x0519:
                r0 = 0
                r13 = 0
            L_0x051b:
                r15 = r0
                r14 = 0
                goto L_0x0529
            L_0x051e:
                java.lang.String r13 = "http"
                boolean r0 = r0.startsWith(r13)
                if (r0 != 0) goto L_0x052c
                r13 = 0
                r14 = 0
                r15 = 0
            L_0x0529:
                r18 = 0
                goto L_0x0531
            L_0x052c:
                r13 = 0
                r14 = 0
                r15 = 0
                r18 = 1
            L_0x0531:
                android.graphics.BitmapFactory$Options r11 = new android.graphics.BitmapFactory$Options
                r11.<init>()
                r11.inSampleSize = r9
                int r0 = android.os.Build.VERSION.SDK_INT
                r4 = 21
                if (r0 >= r4) goto L_0x0540
                r11.inPurgeable = r9
            L_0x0540:
                org.telegram.messenger.ImageLoader r0 = org.telegram.messenger.ImageLoader.this
                boolean r5 = r0.canForce8888
                r23 = 0
                r24 = 1065353216(0x3var_, float:1.0)
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage     // Catch:{ all -> 0x07ab }
                java.lang.String r0 = r0.filter     // Catch:{ all -> 0x07ab }
                if (r0 == 0) goto L_0x0732
                java.lang.String r4 = "_"
                java.lang.String[] r0 = r0.split(r4)     // Catch:{ all -> 0x07ab }
                int r4 = r0.length     // Catch:{ all -> 0x07ab }
                if (r4 < r8) goto L_0x0583
                r4 = r0[r10]     // Catch:{ all -> 0x0577 }
                float r4 = java.lang.Float.parseFloat(r4)     // Catch:{ all -> 0x0577 }
                float r26 = org.telegram.messenger.AndroidUtilities.density     // Catch:{ all -> 0x0577 }
                float r4 = r4 * r26
                r0 = r0[r9]     // Catch:{ all -> 0x0571 }
                float r0 = java.lang.Float.parseFloat(r0)     // Catch:{ all -> 0x0571 }
                float r26 = org.telegram.messenger.AndroidUtilities.density     // Catch:{ all -> 0x0571 }
                float r0 = r0 * r26
                r26 = r4
                r4 = r0
                goto L_0x0586
            L_0x0571:
                r0 = move-exception
                r32 = r12
                r33 = r13
                goto L_0x057d
            L_0x0577:
                r0 = move-exception
                r32 = r12
                r33 = r13
                r4 = 0
            L_0x057d:
                r8 = 0
                r9 = 0
                r12 = r0
                r0 = 0
                goto L_0x07b6
            L_0x0583:
                r4 = 0
                r26 = 0
            L_0x0586:
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage     // Catch:{ all -> 0x0727 }
                java.lang.String r0 = r0.filter     // Catch:{ all -> 0x0727 }
                java.lang.String r8 = "b2"
                boolean r0 = r0.contains(r8)     // Catch:{ all -> 0x0727 }
                if (r0 == 0) goto L_0x0594
                r8 = 3
                goto L_0x05b1
            L_0x0594:
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage     // Catch:{ all -> 0x0727 }
                java.lang.String r0 = r0.filter     // Catch:{ all -> 0x0727 }
                java.lang.String r8 = "b1"
                boolean r0 = r0.contains(r8)     // Catch:{ all -> 0x0727 }
                if (r0 == 0) goto L_0x05a2
                r8 = 2
                goto L_0x05b1
            L_0x05a2:
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage     // Catch:{ all -> 0x0727 }
                java.lang.String r0 = r0.filter     // Catch:{ all -> 0x0727 }
                java.lang.String r8 = "b"
                boolean r0 = r0.contains(r8)     // Catch:{ all -> 0x0727 }
                if (r0 == 0) goto L_0x05b0
                r8 = 1
                goto L_0x05b1
            L_0x05b0:
                r8 = 0
            L_0x05b1:
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage     // Catch:{ all -> 0x071a }
                java.lang.String r0 = r0.filter     // Catch:{ all -> 0x071a }
                java.lang.String r10 = "i"
                boolean r10 = r0.contains(r10)     // Catch:{ all -> 0x071a }
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage     // Catch:{ all -> 0x070c }
                java.lang.String r0 = r0.filter     // Catch:{ all -> 0x070c }
                java.lang.String r9 = "f"
                boolean r0 = r0.contains(r9)     // Catch:{ all -> 0x070c }
                if (r0 == 0) goto L_0x05c8
                r5 = 1
            L_0x05c8:
                if (r12 != 0) goto L_0x06fd
                int r0 = (r26 > r23 ? 1 : (r26 == r23 ? 0 : -1))
                if (r0 == 0) goto L_0x06fd
                int r0 = (r4 > r23 ? 1 : (r4 == r23 ? 0 : -1))
                if (r0 == 0) goto L_0x06fd
                r9 = 1
                r11.inJustDecodeBounds = r9     // Catch:{ all -> 0x06f9 }
                if (r15 == 0) goto L_0x062a
                if (r14 != 0) goto L_0x062a
                if (r13 == 0) goto L_0x0602
                android.content.Context r0 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x05f8 }
                android.content.ContentResolver r0 = r0.getContentResolver()     // Catch:{ all -> 0x05f8 }
                r30 = r8
                long r8 = r15.longValue()     // Catch:{ all -> 0x05ee }
                r31 = r5
                r5 = 1
                android.provider.MediaStore.Video.Thumbnails.getThumbnail(r0, r8, r5, r11)     // Catch:{ all -> 0x061a }
                goto L_0x0614
            L_0x05ee:
                r0 = move-exception
                r31 = r5
                r32 = r12
                r33 = r13
                r8 = r30
                goto L_0x0623
            L_0x05f8:
                r0 = move-exception
                r31 = r5
                r30 = r8
                r32 = r12
                r33 = r13
                goto L_0x0623
            L_0x0602:
                r31 = r5
                r30 = r8
                android.content.Context r0 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x061a }
                android.content.ContentResolver r0 = r0.getContentResolver()     // Catch:{ all -> 0x061a }
                long r8 = r15.longValue()     // Catch:{ all -> 0x061a }
                r5 = 1
                android.provider.MediaStore.Images.Thumbnails.getThumbnail(r0, r8, r5, r11)     // Catch:{ all -> 0x061a }
            L_0x0614:
                r32 = r12
                r33 = r13
                goto L_0x06ae
            L_0x061a:
                r0 = move-exception
                r32 = r12
                r33 = r13
                r8 = r30
                r5 = r31
            L_0x0623:
                r9 = 0
                r12 = r0
                r0 = r4
                r4 = r26
                goto L_0x07b6
            L_0x062a:
                r31 = r5
                r30 = r8
                if (r6 == 0) goto L_0x0692
                java.io.RandomAccessFile r0 = new java.io.RandomAccessFile     // Catch:{ all -> 0x0683 }
                java.lang.String r5 = "r"
                r0.<init>(r2, r5)     // Catch:{ all -> 0x0683 }
                long r8 = r0.length()     // Catch:{ all -> 0x0683 }
                int r5 = (int) r8     // Catch:{ all -> 0x0683 }
                java.lang.ThreadLocal r8 = org.telegram.messenger.ImageLoader.bytesLocal     // Catch:{ all -> 0x0683 }
                java.lang.Object r8 = r8.get()     // Catch:{ all -> 0x0683 }
                byte[] r8 = (byte[]) r8     // Catch:{ all -> 0x0683 }
                if (r8 == 0) goto L_0x064c
                int r9 = r8.length     // Catch:{ all -> 0x061a }
                if (r9 < r5) goto L_0x064c
                goto L_0x064d
            L_0x064c:
                r8 = 0
            L_0x064d:
                if (r8 != 0) goto L_0x0658
                byte[] r8 = new byte[r5]     // Catch:{ all -> 0x061a }
                java.lang.ThreadLocal r9 = org.telegram.messenger.ImageLoader.bytesLocal     // Catch:{ all -> 0x061a }
                r9.set(r8)     // Catch:{ all -> 0x061a }
            L_0x0658:
                r9 = 0
                r0.readFully(r8, r9, r5)     // Catch:{ all -> 0x0683 }
                r0.close()     // Catch:{ all -> 0x0683 }
                org.telegram.messenger.secretmedia.EncryptedFileInputStream.decryptBytesWithKeyFile((byte[]) r8, (int) r9, (int) r5, (org.telegram.messenger.SecureDocumentKey) r6)     // Catch:{ all -> 0x0683 }
                r32 = r12
                r33 = r13
                long r12 = (long) r5
                byte[] r0 = org.telegram.messenger.Utilities.computeSHA256(r8, r9, r12)     // Catch:{ all -> 0x06f7 }
                if (r7 == 0) goto L_0x0676
                boolean r0 = java.util.Arrays.equals(r0, r7)     // Catch:{ all -> 0x06f7 }
                if (r0 != 0) goto L_0x0674
                goto L_0x0676
            L_0x0674:
                r0 = 0
                goto L_0x0677
            L_0x0676:
                r0 = 1
            L_0x0677:
                r9 = 0
                byte r12 = r8[r9]     // Catch:{ all -> 0x06f7 }
                r9 = r12 & 255(0xff, float:3.57E-43)
                int r5 = r5 - r9
                if (r0 != 0) goto L_0x06ae
                android.graphics.BitmapFactory.decodeByteArray(r8, r9, r5, r11)     // Catch:{ all -> 0x06f7 }
                goto L_0x06ae
            L_0x0683:
                r0 = move-exception
                r32 = r12
                r33 = r13
            L_0x0688:
                r12 = r0
                r0 = r4
                r4 = r26
                r8 = r30
                r5 = r31
                goto L_0x0717
            L_0x0692:
                r32 = r12
                r33 = r13
                if (r3 == 0) goto L_0x06a2
                org.telegram.messenger.secretmedia.EncryptedFileInputStream r0 = new org.telegram.messenger.secretmedia.EncryptedFileInputStream     // Catch:{ all -> 0x06f7 }
                org.telegram.messenger.ImageLoader$CacheImage r5 = r1.cacheImage     // Catch:{ all -> 0x06f7 }
                java.io.File r5 = r5.encryptionKeyPath     // Catch:{ all -> 0x06f7 }
                r0.<init>((java.io.File) r2, (java.io.File) r5)     // Catch:{ all -> 0x06f7 }
                goto L_0x06a7
            L_0x06a2:
                java.io.FileInputStream r0 = new java.io.FileInputStream     // Catch:{ all -> 0x06f7 }
                r0.<init>(r2)     // Catch:{ all -> 0x06f7 }
            L_0x06a7:
                r5 = 0
                android.graphics.BitmapFactory.decodeStream(r0, r5, r11)     // Catch:{ all -> 0x06f7 }
                r0.close()     // Catch:{ all -> 0x06f7 }
            L_0x06ae:
                int r0 = r11.outWidth     // Catch:{ all -> 0x06f7 }
                float r0 = (float) r0     // Catch:{ all -> 0x06f7 }
                int r5 = r11.outHeight     // Catch:{ all -> 0x06f7 }
                float r5 = (float) r5     // Catch:{ all -> 0x06f7 }
                int r8 = (r26 > r4 ? 1 : (r26 == r4 ? 0 : -1))
                if (r8 < 0) goto L_0x06c5
                int r8 = (r0 > r5 ? 1 : (r0 == r5 ? 0 : -1))
                if (r8 <= 0) goto L_0x06c5
                float r8 = r0 / r26
                float r9 = r5 / r4
                float r8 = java.lang.Math.max(r8, r9)     // Catch:{ all -> 0x06f7 }
                goto L_0x06cd
            L_0x06c5:
                float r8 = r0 / r26
                float r9 = r5 / r4
                float r8 = java.lang.Math.min(r8, r9)     // Catch:{ all -> 0x06f7 }
            L_0x06cd:
                r9 = 1067030938(0x3var_a, float:1.2)
                int r9 = (r8 > r9 ? 1 : (r8 == r9 ? 0 : -1))
                if (r9 >= 0) goto L_0x06d6
                r8 = 1065353216(0x3var_, float:1.0)
            L_0x06d6:
                r9 = 0
                r11.inJustDecodeBounds = r9     // Catch:{ all -> 0x06f7 }
                int r9 = (r8 > r24 ? 1 : (r8 == r24 ? 0 : -1))
                if (r9 <= 0) goto L_0x06f3
                int r0 = (r0 > r26 ? 1 : (r0 == r26 ? 0 : -1))
                if (r0 > 0) goto L_0x06e5
                int r0 = (r5 > r4 ? 1 : (r5 == r4 ? 0 : -1))
                if (r0 <= 0) goto L_0x06f3
            L_0x06e5:
                r0 = 1
            L_0x06e6:
                r5 = 2
                int r0 = r0 * 2
                int r5 = r0 * 2
                float r5 = (float) r5     // Catch:{ all -> 0x06f7 }
                int r5 = (r5 > r8 ? 1 : (r5 == r8 ? 0 : -1))
                if (r5 < 0) goto L_0x06e6
                r11.inSampleSize = r0     // Catch:{ all -> 0x06f7 }
                goto L_0x0705
            L_0x06f3:
                int r0 = (int) r8     // Catch:{ all -> 0x06f7 }
                r11.inSampleSize = r0     // Catch:{ all -> 0x06f7 }
                goto L_0x0705
            L_0x06f7:
                r0 = move-exception
                goto L_0x0688
            L_0x06f9:
                r0 = move-exception
                r31 = r5
                goto L_0x070d
            L_0x06fd:
                r31 = r5
                r30 = r8
                r32 = r12
                r33 = r13
            L_0x0705:
                r8 = r30
                r5 = r31
                r0 = 0
                goto L_0x07a5
            L_0x070c:
                r0 = move-exception
            L_0x070d:
                r30 = r8
                r32 = r12
                r33 = r13
                r12 = r0
                r0 = r4
                r4 = r26
            L_0x0717:
                r9 = 0
                goto L_0x07b6
            L_0x071a:
                r0 = move-exception
                r30 = r8
                r32 = r12
                r33 = r13
                r12 = r0
                r0 = r4
                r4 = r26
                goto L_0x07b4
            L_0x0727:
                r0 = move-exception
                r32 = r12
                r33 = r13
                r12 = r0
                r0 = r4
                r4 = r26
                goto L_0x07b3
            L_0x0732:
                r32 = r12
                r33 = r13
                if (r14 == 0) goto L_0x079f
                r4 = 1
                r11.inJustDecodeBounds = r4     // Catch:{ all -> 0x079d }
                if (r5 == 0) goto L_0x0740
                android.graphics.Bitmap$Config r0 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ all -> 0x079d }
                goto L_0x0742
            L_0x0740:
                android.graphics.Bitmap$Config r0 = android.graphics.Bitmap.Config.RGB_565     // Catch:{ all -> 0x079d }
            L_0x0742:
                r11.inPreferredConfig = r0     // Catch:{ all -> 0x079d }
                java.io.FileInputStream r0 = new java.io.FileInputStream     // Catch:{ all -> 0x079d }
                r0.<init>(r2)     // Catch:{ all -> 0x079d }
                r4 = 0
                android.graphics.Bitmap r8 = android.graphics.BitmapFactory.decodeStream(r0, r4, r11)     // Catch:{ all -> 0x079d }
                r0.close()     // Catch:{ all -> 0x0796 }
                int r0 = r11.outWidth     // Catch:{ all -> 0x0796 }
                int r4 = r11.outHeight     // Catch:{ all -> 0x0796 }
                r9 = 0
                r11.inJustDecodeBounds = r9     // Catch:{ all -> 0x0796 }
                r9 = 66
                android.graphics.Point r10 = org.telegram.messenger.AndroidUtilities.getRealScreenSize()     // Catch:{ all -> 0x0796 }
                int r10 = r10.x     // Catch:{ all -> 0x0796 }
                android.graphics.Point r12 = org.telegram.messenger.AndroidUtilities.getRealScreenSize()     // Catch:{ all -> 0x0796 }
                int r12 = r12.y     // Catch:{ all -> 0x0796 }
                int r10 = java.lang.Math.min(r10, r12)     // Catch:{ all -> 0x0796 }
                int r9 = java.lang.Math.max(r9, r10)     // Catch:{ all -> 0x0796 }
                int r0 = java.lang.Math.min(r4, r0)     // Catch:{ all -> 0x0796 }
                float r0 = (float) r0     // Catch:{ all -> 0x0796 }
                float r4 = (float) r9     // Catch:{ all -> 0x0796 }
                float r0 = r0 / r4
                r4 = 1086324736(0x40CLASSNAME, float:6.0)
                float r0 = r0 * r4
                int r4 = (r0 > r24 ? 1 : (r0 == r24 ? 0 : -1))
                if (r4 >= 0) goto L_0x077f
                r0 = 1065353216(0x3var_, float:1.0)
            L_0x077f:
                int r4 = (r0 > r24 ? 1 : (r0 == r24 ? 0 : -1))
                if (r4 <= 0) goto L_0x0791
                r4 = 1
            L_0x0784:
                r9 = 2
                int r4 = r4 * 2
                int r9 = r4 * 2
                float r9 = (float) r9     // Catch:{ all -> 0x0796 }
                int r9 = (r9 > r0 ? 1 : (r9 == r0 ? 0 : -1))
                if (r9 <= 0) goto L_0x0784
                r11.inSampleSize = r4     // Catch:{ all -> 0x0796 }
                goto L_0x0794
            L_0x0791:
                int r0 = (int) r0     // Catch:{ all -> 0x0796 }
                r11.inSampleSize = r0     // Catch:{ all -> 0x0796 }
            L_0x0794:
                r0 = r8
                goto L_0x07a0
            L_0x0796:
                r0 = move-exception
                r12 = r0
                r9 = r8
                r0 = 0
                r4 = 0
                r8 = 0
                goto L_0x07b5
            L_0x079d:
                r0 = move-exception
                goto L_0x07b0
            L_0x079f:
                r0 = 0
            L_0x07a0:
                r4 = 0
                r8 = 0
                r10 = 0
                r26 = 0
            L_0x07a5:
                r9 = r0
                r0 = r4
                r12 = r26
                r4 = 1
                goto L_0x07c1
            L_0x07ab:
                r0 = move-exception
                r32 = r12
                r33 = r13
            L_0x07b0:
                r12 = r0
                r0 = 0
                r4 = 0
            L_0x07b3:
                r8 = 0
            L_0x07b4:
                r9 = 0
            L_0x07b5:
                r10 = 0
            L_0x07b6:
                boolean r13 = r12 instanceof java.io.FileNotFoundException
                r26 = r4
                r4 = 1
                r13 = r13 ^ r4
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r12, (boolean) r13)
                r12 = r26
            L_0x07c1:
                org.telegram.messenger.ImageLoader$CacheImage r13 = r1.cacheImage
                int r13 = r13.type
                r26 = 1101004800(0x41a00000, float:20.0)
                if (r13 != r4) goto L_0x09d6
                org.telegram.messenger.ImageLoader r0 = org.telegram.messenger.ImageLoader.this     // Catch:{ all -> 0x09ce }
                long r4 = android.os.SystemClock.elapsedRealtime()     // Catch:{ all -> 0x09ce }
                long unused = r0.lastCacheOutTime = r4     // Catch:{ all -> 0x09ce }
                java.lang.Object r4 = r1.sync     // Catch:{ all -> 0x09ce }
                monitor-enter(r4)     // Catch:{ all -> 0x09ce }
                boolean r0 = r1.isCancelled     // Catch:{ all -> 0x09cb }
                if (r0 == 0) goto L_0x07db
                monitor-exit(r4)     // Catch:{ all -> 0x09cb }
                return
            L_0x07db:
                monitor-exit(r4)     // Catch:{ all -> 0x09cb }
                if (r32 == 0) goto L_0x0823
                java.io.RandomAccessFile r0 = new java.io.RandomAccessFile     // Catch:{ all -> 0x09ce }
                java.lang.String r4 = "r"
                r0.<init>(r2, r4)     // Catch:{ all -> 0x09ce }
                java.nio.channels.FileChannel r30 = r0.getChannel()     // Catch:{ all -> 0x09ce }
                java.nio.channels.FileChannel$MapMode r31 = java.nio.channels.FileChannel.MapMode.READ_ONLY     // Catch:{ all -> 0x09ce }
                r32 = 0
                long r34 = r2.length()     // Catch:{ all -> 0x09ce }
                java.nio.MappedByteBuffer r4 = r30.map(r31, r32, r34)     // Catch:{ all -> 0x09ce }
                android.graphics.BitmapFactory$Options r5 = new android.graphics.BitmapFactory$Options     // Catch:{ all -> 0x09ce }
                r5.<init>()     // Catch:{ all -> 0x09ce }
                r6 = 1
                r5.inJustDecodeBounds = r6     // Catch:{ all -> 0x09ce }
                int r7 = r4.limit()     // Catch:{ all -> 0x09ce }
                r13 = 0
                org.telegram.messenger.Utilities.loadWebpImage(r13, r4, r7, r5, r6)     // Catch:{ all -> 0x09ce }
                int r6 = r5.outWidth     // Catch:{ all -> 0x09ce }
                int r5 = r5.outHeight     // Catch:{ all -> 0x09ce }
                android.graphics.Bitmap$Config r7 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ all -> 0x09ce }
                android.graphics.Bitmap r9 = org.telegram.messenger.Bitmaps.createBitmap(r6, r5, r7)     // Catch:{ all -> 0x09ce }
                int r5 = r4.limit()     // Catch:{ all -> 0x09ce }
                boolean r6 = r11.inPurgeable     // Catch:{ all -> 0x09ce }
                if (r6 != 0) goto L_0x0819
                r6 = 1
                goto L_0x081a
            L_0x0819:
                r6 = 0
            L_0x081a:
                r7 = 0
                org.telegram.messenger.Utilities.loadWebpImage(r9, r4, r5, r7, r6)     // Catch:{ all -> 0x09ce }
                r0.close()     // Catch:{ all -> 0x09ce }
                goto L_0x08a2
            L_0x0823:
                boolean r0 = r11.inPurgeable     // Catch:{ all -> 0x09ce }
                if (r0 != 0) goto L_0x0844
                if (r6 == 0) goto L_0x082a
                goto L_0x0844
            L_0x082a:
                if (r3 == 0) goto L_0x0836
                org.telegram.messenger.secretmedia.EncryptedFileInputStream r0 = new org.telegram.messenger.secretmedia.EncryptedFileInputStream     // Catch:{ all -> 0x09ce }
                org.telegram.messenger.ImageLoader$CacheImage r4 = r1.cacheImage     // Catch:{ all -> 0x09ce }
                java.io.File r4 = r4.encryptionKeyPath     // Catch:{ all -> 0x09ce }
                r0.<init>((java.io.File) r2, (java.io.File) r4)     // Catch:{ all -> 0x09ce }
                goto L_0x083b
            L_0x0836:
                java.io.FileInputStream r0 = new java.io.FileInputStream     // Catch:{ all -> 0x09ce }
                r0.<init>(r2)     // Catch:{ all -> 0x09ce }
            L_0x083b:
                r4 = 0
                android.graphics.Bitmap r9 = android.graphics.BitmapFactory.decodeStream(r0, r4, r11)     // Catch:{ all -> 0x09ce }
                r0.close()     // Catch:{ all -> 0x09ce }
                goto L_0x08a2
            L_0x0844:
                java.io.RandomAccessFile r0 = new java.io.RandomAccessFile     // Catch:{ all -> 0x09ce }
                java.lang.String r4 = "r"
                r0.<init>(r2, r4)     // Catch:{ all -> 0x09ce }
                long r4 = r0.length()     // Catch:{ all -> 0x09ce }
                int r5 = (int) r4     // Catch:{ all -> 0x09ce }
                java.lang.ThreadLocal r4 = org.telegram.messenger.ImageLoader.bytesThumbLocal     // Catch:{ all -> 0x09ce }
                java.lang.Object r4 = r4.get()     // Catch:{ all -> 0x09ce }
                byte[] r4 = (byte[]) r4     // Catch:{ all -> 0x09ce }
                if (r4 == 0) goto L_0x0860
                int r13 = r4.length     // Catch:{ all -> 0x09ce }
                if (r13 < r5) goto L_0x0860
                goto L_0x0861
            L_0x0860:
                r4 = 0
            L_0x0861:
                if (r4 != 0) goto L_0x086c
                byte[] r4 = new byte[r5]     // Catch:{ all -> 0x09ce }
                java.lang.ThreadLocal r13 = org.telegram.messenger.ImageLoader.bytesThumbLocal     // Catch:{ all -> 0x09ce }
                r13.set(r4)     // Catch:{ all -> 0x09ce }
            L_0x086c:
                r13 = 0
                r0.readFully(r4, r13, r5)     // Catch:{ all -> 0x09ce }
                r0.close()     // Catch:{ all -> 0x09ce }
                if (r6 == 0) goto L_0x0890
                org.telegram.messenger.secretmedia.EncryptedFileInputStream.decryptBytesWithKeyFile((byte[]) r4, (int) r13, (int) r5, (org.telegram.messenger.SecureDocumentKey) r6)     // Catch:{ all -> 0x09ce }
                long r14 = (long) r5     // Catch:{ all -> 0x09ce }
                byte[] r0 = org.telegram.messenger.Utilities.computeSHA256(r4, r13, r14)     // Catch:{ all -> 0x09ce }
                if (r7 == 0) goto L_0x0888
                boolean r0 = java.util.Arrays.equals(r0, r7)     // Catch:{ all -> 0x09ce }
                if (r0 != 0) goto L_0x0886
                goto L_0x0888
            L_0x0886:
                r0 = 0
                goto L_0x0889
            L_0x0888:
                r0 = 1
            L_0x0889:
                r6 = 0
                byte r7 = r4[r6]     // Catch:{ all -> 0x09ce }
                r6 = r7 & 255(0xff, float:3.57E-43)
                int r5 = r5 - r6
                goto L_0x089c
            L_0x0890:
                if (r3 == 0) goto L_0x089a
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage     // Catch:{ all -> 0x09ce }
                java.io.File r0 = r0.encryptionKeyPath     // Catch:{ all -> 0x09ce }
                r6 = 0
                org.telegram.messenger.secretmedia.EncryptedFileInputStream.decryptBytesWithKeyFile((byte[]) r4, (int) r6, (int) r5, (java.io.File) r0)     // Catch:{ all -> 0x09ce }
            L_0x089a:
                r0 = 0
                r6 = 0
            L_0x089c:
                if (r0 != 0) goto L_0x08a2
                android.graphics.Bitmap r9 = android.graphics.BitmapFactory.decodeByteArray(r4, r6, r5, r11)     // Catch:{ all -> 0x09ce }
            L_0x08a2:
                if (r9 != 0) goto L_0x08ba
                long r4 = r2.length()     // Catch:{ all -> 0x09ce }
                r6 = 0
                int r0 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
                if (r0 == 0) goto L_0x08b4
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage     // Catch:{ all -> 0x09ce }
                java.lang.String r0 = r0.filter     // Catch:{ all -> 0x09ce }
                if (r0 != 0) goto L_0x08b7
            L_0x08b4:
                r2.delete()     // Catch:{ all -> 0x09ce }
            L_0x08b7:
                r4 = 0
                goto L_0x09d3
            L_0x08ba:
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage     // Catch:{ all -> 0x09ce }
                java.lang.String r0 = r0.filter     // Catch:{ all -> 0x09ce }
                if (r0 == 0) goto L_0x08eb
                int r0 = r9.getWidth()     // Catch:{ all -> 0x09ce }
                float r0 = (float) r0     // Catch:{ all -> 0x09ce }
                int r4 = r9.getHeight()     // Catch:{ all -> 0x09ce }
                float r4 = (float) r4     // Catch:{ all -> 0x09ce }
                boolean r5 = r11.inPurgeable     // Catch:{ all -> 0x09ce }
                if (r5 != 0) goto L_0x08eb
                int r5 = (r12 > r23 ? 1 : (r12 == r23 ? 0 : -1))
                if (r5 == 0) goto L_0x08eb
                int r5 = (r0 > r12 ? 1 : (r0 == r12 ? 0 : -1))
                if (r5 == 0) goto L_0x08eb
                float r26 = r12 + r26
                int r5 = (r0 > r26 ? 1 : (r0 == r26 ? 0 : -1))
                if (r5 <= 0) goto L_0x08eb
                float r0 = r0 / r12
                int r5 = (int) r12     // Catch:{ all -> 0x09ce }
                float r4 = r4 / r0
                int r0 = (int) r4     // Catch:{ all -> 0x09ce }
                r4 = 1
                android.graphics.Bitmap r0 = org.telegram.messenger.Bitmaps.createScaledBitmap(r9, r5, r0, r4)     // Catch:{ all -> 0x09ce }
                if (r9 == r0) goto L_0x08eb
                r9.recycle()     // Catch:{ all -> 0x09ce }
                r9 = r0
            L_0x08eb:
                if (r10 == 0) goto L_0x090b
                boolean r0 = r11.inPurgeable     // Catch:{ all -> 0x09ce }
                if (r0 == 0) goto L_0x08f3
                r0 = 0
                goto L_0x08f4
            L_0x08f3:
                r0 = 1
            L_0x08f4:
                int r4 = r9.getWidth()     // Catch:{ all -> 0x09ce }
                int r5 = r9.getHeight()     // Catch:{ all -> 0x09ce }
                int r6 = r9.getRowBytes()     // Catch:{ all -> 0x09ce }
                int r0 = org.telegram.messenger.Utilities.needInvert(r9, r0, r4, r5, r6)     // Catch:{ all -> 0x09ce }
                if (r0 == 0) goto L_0x0908
                r0 = 1
                goto L_0x0909
            L_0x0908:
                r0 = 0
            L_0x0909:
                r4 = r0
                goto L_0x090c
            L_0x090b:
                r4 = 0
            L_0x090c:
                r5 = 1
                if (r8 != r5) goto L_0x0938
                android.graphics.Bitmap$Config r0 = r9.getConfig()     // Catch:{ all -> 0x0935 }
                android.graphics.Bitmap$Config r5 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ all -> 0x0935 }
                if (r0 != r5) goto L_0x09d3
                r21 = 3
                boolean r0 = r11.inPurgeable     // Catch:{ all -> 0x0935 }
                if (r0 == 0) goto L_0x0920
                r22 = 0
                goto L_0x0922
            L_0x0920:
                r22 = 1
            L_0x0922:
                int r23 = r9.getWidth()     // Catch:{ all -> 0x0935 }
                int r24 = r9.getHeight()     // Catch:{ all -> 0x0935 }
                int r25 = r9.getRowBytes()     // Catch:{ all -> 0x0935 }
                r20 = r9
                org.telegram.messenger.Utilities.blurBitmap(r20, r21, r22, r23, r24, r25)     // Catch:{ all -> 0x0935 }
                goto L_0x09d3
            L_0x0935:
                r0 = move-exception
                goto L_0x09d0
            L_0x0938:
                r5 = 2
                if (r8 != r5) goto L_0x0961
                android.graphics.Bitmap$Config r0 = r9.getConfig()     // Catch:{ all -> 0x0935 }
                android.graphics.Bitmap$Config r5 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ all -> 0x0935 }
                if (r0 != r5) goto L_0x09d3
                r21 = 1
                boolean r0 = r11.inPurgeable     // Catch:{ all -> 0x0935 }
                if (r0 == 0) goto L_0x094c
                r22 = 0
                goto L_0x094e
            L_0x094c:
                r22 = 1
            L_0x094e:
                int r23 = r9.getWidth()     // Catch:{ all -> 0x0935 }
                int r24 = r9.getHeight()     // Catch:{ all -> 0x0935 }
                int r25 = r9.getRowBytes()     // Catch:{ all -> 0x0935 }
                r20 = r9
                org.telegram.messenger.Utilities.blurBitmap(r20, r21, r22, r23, r24, r25)     // Catch:{ all -> 0x0935 }
                goto L_0x09d3
            L_0x0961:
                r5 = 3
                if (r8 != r5) goto L_0x09c1
                android.graphics.Bitmap$Config r0 = r9.getConfig()     // Catch:{ all -> 0x0935 }
                android.graphics.Bitmap$Config r5 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ all -> 0x0935 }
                if (r0 != r5) goto L_0x09d3
                r21 = 7
                boolean r0 = r11.inPurgeable     // Catch:{ all -> 0x0935 }
                if (r0 == 0) goto L_0x0975
                r22 = 0
                goto L_0x0977
            L_0x0975:
                r22 = 1
            L_0x0977:
                int r23 = r9.getWidth()     // Catch:{ all -> 0x0935 }
                int r24 = r9.getHeight()     // Catch:{ all -> 0x0935 }
                int r25 = r9.getRowBytes()     // Catch:{ all -> 0x0935 }
                r20 = r9
                org.telegram.messenger.Utilities.blurBitmap(r20, r21, r22, r23, r24, r25)     // Catch:{ all -> 0x0935 }
                r21 = 7
                boolean r0 = r11.inPurgeable     // Catch:{ all -> 0x0935 }
                if (r0 == 0) goto L_0x0991
                r22 = 0
                goto L_0x0993
            L_0x0991:
                r22 = 1
            L_0x0993:
                int r23 = r9.getWidth()     // Catch:{ all -> 0x0935 }
                int r24 = r9.getHeight()     // Catch:{ all -> 0x0935 }
                int r25 = r9.getRowBytes()     // Catch:{ all -> 0x0935 }
                r20 = r9
                org.telegram.messenger.Utilities.blurBitmap(r20, r21, r22, r23, r24, r25)     // Catch:{ all -> 0x0935 }
                r21 = 7
                boolean r0 = r11.inPurgeable     // Catch:{ all -> 0x0935 }
                if (r0 == 0) goto L_0x09ad
                r22 = 0
                goto L_0x09af
            L_0x09ad:
                r22 = 1
            L_0x09af:
                int r23 = r9.getWidth()     // Catch:{ all -> 0x0935 }
                int r24 = r9.getHeight()     // Catch:{ all -> 0x0935 }
                int r25 = r9.getRowBytes()     // Catch:{ all -> 0x0935 }
                r20 = r9
                org.telegram.messenger.Utilities.blurBitmap(r20, r21, r22, r23, r24, r25)     // Catch:{ all -> 0x0935 }
                goto L_0x09d3
            L_0x09c1:
                if (r8 != 0) goto L_0x09d3
                boolean r0 = r11.inPurgeable     // Catch:{ all -> 0x0935 }
                if (r0 == 0) goto L_0x09d3
                org.telegram.messenger.Utilities.pinBitmap(r9)     // Catch:{ all -> 0x0935 }
                goto L_0x09d3
            L_0x09cb:
                r0 = move-exception
                monitor-exit(r4)     // Catch:{ all -> 0x09cb }
                throw r0     // Catch:{ all -> 0x09ce }
            L_0x09ce:
                r0 = move-exception
                r4 = 0
            L_0x09d0:
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            L_0x09d3:
                r6 = 0
                goto L_0x0c9a
            L_0x09d6:
                r4 = 20
                if (r15 == 0) goto L_0x09db
                r4 = 0
            L_0x09db:
                if (r4 == 0) goto L_0x0a11
                org.telegram.messenger.ImageLoader r13 = org.telegram.messenger.ImageLoader.this     // Catch:{ all -> 0x0a0c }
                long r30 = r13.lastCacheOutTime     // Catch:{ all -> 0x0a0c }
                r21 = 0
                int r13 = (r30 > r21 ? 1 : (r30 == r21 ? 0 : -1))
                if (r13 == 0) goto L_0x0a11
                org.telegram.messenger.ImageLoader r13 = org.telegram.messenger.ImageLoader.this     // Catch:{ all -> 0x0a0c }
                long r30 = r13.lastCacheOutTime     // Catch:{ all -> 0x0a0c }
                long r34 = android.os.SystemClock.elapsedRealtime()     // Catch:{ all -> 0x0a0c }
                r27 = r9
                r13 = r10
                long r9 = (long) r4
                long r34 = r34 - r9
                int r4 = (r30 > r34 ? 1 : (r30 == r34 ? 0 : -1))
                if (r4 <= 0) goto L_0x0a09
                int r4 = android.os.Build.VERSION.SDK_INT     // Catch:{ all -> 0x0CLASSNAME }
                r30 = r13
                r13 = 21
                if (r4 >= r13) goto L_0x0a15
                java.lang.Thread.sleep(r9)     // Catch:{ all -> 0x0CLASSNAME }
                goto L_0x0a15
            L_0x0a09:
                r30 = r13
                goto L_0x0a15
            L_0x0a0c:
                r0 = move-exception
                r27 = r9
                goto L_0x0CLASSNAME
            L_0x0a11:
                r27 = r9
                r30 = r10
            L_0x0a15:
                org.telegram.messenger.ImageLoader r4 = org.telegram.messenger.ImageLoader.this     // Catch:{ all -> 0x0CLASSNAME }
                long r9 = android.os.SystemClock.elapsedRealtime()     // Catch:{ all -> 0x0CLASSNAME }
                long unused = r4.lastCacheOutTime = r9     // Catch:{ all -> 0x0CLASSNAME }
                java.lang.Object r4 = r1.sync     // Catch:{ all -> 0x0CLASSNAME }
                monitor-enter(r4)     // Catch:{ all -> 0x0CLASSNAME }
                boolean r9 = r1.isCancelled     // Catch:{ all -> 0x0c8e }
                if (r9 == 0) goto L_0x0a27
                monitor-exit(r4)     // Catch:{ all -> 0x0c8e }
                return
            L_0x0a27:
                monitor-exit(r4)     // Catch:{ all -> 0x0c8e }
                if (r5 != 0) goto L_0x0a3e
                org.telegram.messenger.ImageLoader$CacheImage r4 = r1.cacheImage     // Catch:{ all -> 0x0CLASSNAME }
                java.lang.String r5 = r4.filter     // Catch:{ all -> 0x0CLASSNAME }
                if (r5 == 0) goto L_0x0a3e
                if (r8 != 0) goto L_0x0a3e
                org.telegram.messenger.ImageLocation r4 = r4.imageLocation     // Catch:{ all -> 0x0CLASSNAME }
                java.lang.String r4 = r4.path     // Catch:{ all -> 0x0CLASSNAME }
                if (r4 == 0) goto L_0x0a39
                goto L_0x0a3e
            L_0x0a39:
                android.graphics.Bitmap$Config r4 = android.graphics.Bitmap.Config.RGB_565     // Catch:{ all -> 0x0CLASSNAME }
                r11.inPreferredConfig = r4     // Catch:{ all -> 0x0CLASSNAME }
                goto L_0x0a42
            L_0x0a3e:
                android.graphics.Bitmap$Config r4 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ all -> 0x0CLASSNAME }
                r11.inPreferredConfig = r4     // Catch:{ all -> 0x0CLASSNAME }
            L_0x0a42:
                r4 = 0
                r11.inDither = r4     // Catch:{ all -> 0x0CLASSNAME }
                if (r15 == 0) goto L_0x0a6b
                if (r14 != 0) goto L_0x0a6b
                if (r33 == 0) goto L_0x0a5b
                android.content.Context r4 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x0CLASSNAME }
                android.content.ContentResolver r4 = r4.getContentResolver()     // Catch:{ all -> 0x0CLASSNAME }
                long r9 = r15.longValue()     // Catch:{ all -> 0x0CLASSNAME }
                r5 = 1
                android.graphics.Bitmap r9 = android.provider.MediaStore.Video.Thumbnails.getThumbnail(r4, r9, r5, r11)     // Catch:{ all -> 0x0CLASSNAME }
                goto L_0x0a6d
            L_0x0a5b:
                android.content.Context r4 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ all -> 0x0CLASSNAME }
                android.content.ContentResolver r4 = r4.getContentResolver()     // Catch:{ all -> 0x0CLASSNAME }
                long r9 = r15.longValue()     // Catch:{ all -> 0x0CLASSNAME }
                r5 = 1
                android.graphics.Bitmap r9 = android.provider.MediaStore.Images.Thumbnails.getThumbnail(r4, r9, r5, r11)     // Catch:{ all -> 0x0CLASSNAME }
                goto L_0x0a6d
            L_0x0a6b:
                r9 = r27
            L_0x0a6d:
                if (r9 != 0) goto L_0x0b7c
                if (r32 == 0) goto L_0x0ab6
                java.io.RandomAccessFile r4 = new java.io.RandomAccessFile     // Catch:{ all -> 0x0b79 }
                java.lang.String r5 = "r"
                r4.<init>(r2, r5)     // Catch:{ all -> 0x0b79 }
                java.nio.channels.FileChannel r31 = r4.getChannel()     // Catch:{ all -> 0x0b79 }
                java.nio.channels.FileChannel$MapMode r32 = java.nio.channels.FileChannel.MapMode.READ_ONLY     // Catch:{ all -> 0x0b79 }
                r33 = 0
                long r35 = r2.length()     // Catch:{ all -> 0x0b79 }
                java.nio.MappedByteBuffer r5 = r31.map(r32, r33, r35)     // Catch:{ all -> 0x0b79 }
                android.graphics.BitmapFactory$Options r6 = new android.graphics.BitmapFactory$Options     // Catch:{ all -> 0x0b79 }
                r6.<init>()     // Catch:{ all -> 0x0b79 }
                r7 = 1
                r6.inJustDecodeBounds = r7     // Catch:{ all -> 0x0b79 }
                int r10 = r5.limit()     // Catch:{ all -> 0x0b79 }
                r13 = 0
                org.telegram.messenger.Utilities.loadWebpImage(r13, r5, r10, r6, r7)     // Catch:{ all -> 0x0b79 }
                int r7 = r6.outWidth     // Catch:{ all -> 0x0b79 }
                int r6 = r6.outHeight     // Catch:{ all -> 0x0b79 }
                android.graphics.Bitmap$Config r10 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ all -> 0x0b79 }
                android.graphics.Bitmap r9 = org.telegram.messenger.Bitmaps.createBitmap(r7, r6, r10)     // Catch:{ all -> 0x0b79 }
                int r6 = r5.limit()     // Catch:{ all -> 0x0b79 }
                boolean r7 = r11.inPurgeable     // Catch:{ all -> 0x0b79 }
                if (r7 != 0) goto L_0x0aac
                r7 = 1
                goto L_0x0aad
            L_0x0aac:
                r7 = 0
            L_0x0aad:
                r10 = 0
                org.telegram.messenger.Utilities.loadWebpImage(r9, r5, r6, r10, r7)     // Catch:{ all -> 0x0b79 }
                r4.close()     // Catch:{ all -> 0x0b79 }
                goto L_0x0b7c
            L_0x0ab6:
                boolean r4 = r11.inPurgeable     // Catch:{ all -> 0x0b79 }
                if (r4 != 0) goto L_0x0b19
                if (r6 != 0) goto L_0x0b19
                int r4 = android.os.Build.VERSION.SDK_INT     // Catch:{ all -> 0x0b79 }
                r5 = 29
                if (r4 > r5) goto L_0x0ac3
                goto L_0x0b19
            L_0x0ac3:
                if (r3 == 0) goto L_0x0acf
                org.telegram.messenger.secretmedia.EncryptedFileInputStream r4 = new org.telegram.messenger.secretmedia.EncryptedFileInputStream     // Catch:{ all -> 0x0b79 }
                org.telegram.messenger.ImageLoader$CacheImage r5 = r1.cacheImage     // Catch:{ all -> 0x0b79 }
                java.io.File r5 = r5.encryptionKeyPath     // Catch:{ all -> 0x0b79 }
                r4.<init>((java.io.File) r2, (java.io.File) r5)     // Catch:{ all -> 0x0b79 }
                goto L_0x0ad4
            L_0x0acf:
                java.io.FileInputStream r4 = new java.io.FileInputStream     // Catch:{ all -> 0x0b79 }
                r4.<init>(r2)     // Catch:{ all -> 0x0b79 }
            L_0x0ad4:
                org.telegram.messenger.ImageLoader$CacheImage r5 = r1.cacheImage     // Catch:{ all -> 0x0b79 }
                org.telegram.messenger.ImageLocation r5 = r5.imageLocation     // Catch:{ all -> 0x0b79 }
                org.telegram.tgnet.TLRPC$Document r5 = r5.document     // Catch:{ all -> 0x0b79 }
                boolean r5 = r5 instanceof org.telegram.tgnet.TLRPC$TL_document     // Catch:{ all -> 0x0b79 }
                if (r5 == 0) goto L_0x0b0f
                androidx.exifinterface.media.ExifInterface r5 = new androidx.exifinterface.media.ExifInterface     // Catch:{ all -> 0x0afe }
                r5.<init>((java.io.InputStream) r4)     // Catch:{ all -> 0x0afe }
                java.lang.String r6 = "Orientation"
                r7 = 1
                int r5 = r5.getAttributeInt(r6, r7)     // Catch:{ all -> 0x0afe }
                r6 = 3
                if (r5 == r6) goto L_0x0afb
                r6 = 6
                if (r5 == r6) goto L_0x0af8
                r6 = 8
                if (r5 == r6) goto L_0x0af5
                goto L_0x0afe
            L_0x0af5:
                r5 = 270(0x10e, float:3.78E-43)
                goto L_0x0aff
            L_0x0af8:
                r5 = 90
                goto L_0x0aff
            L_0x0afb:
                r5 = 180(0xb4, float:2.52E-43)
                goto L_0x0aff
            L_0x0afe:
                r5 = 0
            L_0x0aff:
                java.nio.channels.FileChannel r6 = r4.getChannel()     // Catch:{ all -> 0x0b0b }
                r13 = 0
                r6.position(r13)     // Catch:{ all -> 0x0b0b }
                r6 = r5
                r5 = 0
                goto L_0x0b11
            L_0x0b0b:
                r0 = move-exception
                r6 = r5
                goto L_0x0CLASSNAME
            L_0x0b0f:
                r5 = 0
                r6 = 0
            L_0x0b11:
                android.graphics.Bitmap r9 = android.graphics.BitmapFactory.decodeStream(r4, r5, r11)     // Catch:{ all -> 0x0c8c }
                r4.close()     // Catch:{ all -> 0x0c8c }
                goto L_0x0b7d
            L_0x0b19:
                r5 = 0
                java.io.RandomAccessFile r4 = new java.io.RandomAccessFile     // Catch:{ all -> 0x0b79 }
                java.lang.String r10 = "r"
                r4.<init>(r2, r10)     // Catch:{ all -> 0x0b79 }
                long r13 = r4.length()     // Catch:{ all -> 0x0b79 }
                int r10 = (int) r13     // Catch:{ all -> 0x0b79 }
                java.lang.ThreadLocal r13 = org.telegram.messenger.ImageLoader.bytesLocal     // Catch:{ all -> 0x0b79 }
                java.lang.Object r13 = r13.get()     // Catch:{ all -> 0x0b79 }
                byte[] r13 = (byte[]) r13     // Catch:{ all -> 0x0b79 }
                if (r13 == 0) goto L_0x0b36
                int r14 = r13.length     // Catch:{ all -> 0x0b79 }
                if (r14 < r10) goto L_0x0b36
                goto L_0x0b37
            L_0x0b36:
                r13 = r5
            L_0x0b37:
                if (r13 != 0) goto L_0x0b42
                byte[] r13 = new byte[r10]     // Catch:{ all -> 0x0b79 }
                java.lang.ThreadLocal r14 = org.telegram.messenger.ImageLoader.bytesLocal     // Catch:{ all -> 0x0b79 }
                r14.set(r13)     // Catch:{ all -> 0x0b79 }
            L_0x0b42:
                r14 = 0
                r4.readFully(r13, r14, r10)     // Catch:{ all -> 0x0b79 }
                r4.close()     // Catch:{ all -> 0x0b79 }
                if (r6 == 0) goto L_0x0b66
                org.telegram.messenger.secretmedia.EncryptedFileInputStream.decryptBytesWithKeyFile((byte[]) r13, (int) r14, (int) r10, (org.telegram.messenger.SecureDocumentKey) r6)     // Catch:{ all -> 0x0b79 }
                long r5 = (long) r10     // Catch:{ all -> 0x0b79 }
                byte[] r4 = org.telegram.messenger.Utilities.computeSHA256(r13, r14, r5)     // Catch:{ all -> 0x0b79 }
                if (r7 == 0) goto L_0x0b5e
                boolean r4 = java.util.Arrays.equals(r4, r7)     // Catch:{ all -> 0x0b79 }
                if (r4 != 0) goto L_0x0b5c
                goto L_0x0b5e
            L_0x0b5c:
                r4 = 0
                goto L_0x0b5f
            L_0x0b5e:
                r4 = 1
            L_0x0b5f:
                r5 = 0
                byte r6 = r13[r5]     // Catch:{ all -> 0x0b79 }
                r5 = r6 & 255(0xff, float:3.57E-43)
                int r10 = r10 - r5
                goto L_0x0b72
            L_0x0b66:
                if (r3 == 0) goto L_0x0b70
                org.telegram.messenger.ImageLoader$CacheImage r4 = r1.cacheImage     // Catch:{ all -> 0x0b79 }
                java.io.File r4 = r4.encryptionKeyPath     // Catch:{ all -> 0x0b79 }
                r5 = 0
                org.telegram.messenger.secretmedia.EncryptedFileInputStream.decryptBytesWithKeyFile((byte[]) r13, (int) r5, (int) r10, (java.io.File) r4)     // Catch:{ all -> 0x0b79 }
            L_0x0b70:
                r4 = 0
                r5 = 0
            L_0x0b72:
                if (r4 != 0) goto L_0x0b7c
                android.graphics.Bitmap r9 = android.graphics.BitmapFactory.decodeByteArray(r13, r5, r10, r11)     // Catch:{ all -> 0x0b79 }
                goto L_0x0b7c
            L_0x0b79:
                r0 = move-exception
                goto L_0x0CLASSNAME
            L_0x0b7c:
                r6 = 0
            L_0x0b7d:
                if (r9 != 0) goto L_0x0b97
                if (r18 == 0) goto L_0x0b94
                long r4 = r2.length()     // Catch:{ all -> 0x0c8c }
                r7 = 0
                int r0 = (r4 > r7 ? 1 : (r4 == r7 ? 0 : -1))
                if (r0 == 0) goto L_0x0b91
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage     // Catch:{ all -> 0x0c8c }
                java.lang.String r0 = r0.filter     // Catch:{ all -> 0x0c8c }
                if (r0 != 0) goto L_0x0b94
            L_0x0b91:
                r2.delete()     // Catch:{ all -> 0x0c8c }
            L_0x0b94:
                r7 = 0
                goto L_0x0CLASSNAME
            L_0x0b97:
                org.telegram.messenger.ImageLoader$CacheImage r4 = r1.cacheImage     // Catch:{ all -> 0x0c8c }
                java.lang.String r4 = r4.filter     // Catch:{ all -> 0x0c8c }
                if (r4 == 0) goto L_0x0c7a
                int r4 = r9.getWidth()     // Catch:{ all -> 0x0c8c }
                float r4 = (float) r4     // Catch:{ all -> 0x0c8c }
                int r5 = r9.getHeight()     // Catch:{ all -> 0x0c8c }
                float r5 = (float) r5     // Catch:{ all -> 0x0c8c }
                boolean r7 = r11.inPurgeable     // Catch:{ all -> 0x0c8c }
                if (r7 != 0) goto L_0x0be8
                int r7 = (r12 > r23 ? 1 : (r12 == r23 ? 0 : -1))
                if (r7 == 0) goto L_0x0be8
                int r7 = (r4 > r12 ? 1 : (r4 == r12 ? 0 : -1))
                if (r7 == 0) goto L_0x0be8
                float r26 = r12 + r26
                int r7 = (r4 > r26 ? 1 : (r4 == r26 ? 0 : -1))
                if (r7 <= 0) goto L_0x0be8
                int r7 = (r4 > r5 ? 1 : (r4 == r5 ? 0 : -1))
                if (r7 <= 0) goto L_0x0bd1
                int r7 = (r12 > r0 ? 1 : (r12 == r0 ? 0 : -1))
                if (r7 <= 0) goto L_0x0bd1
                float r0 = r4 / r12
                int r7 = (r0 > r24 ? 1 : (r0 == r24 ? 0 : -1))
                if (r7 <= 0) goto L_0x0be1
                int r7 = (int) r12     // Catch:{ all -> 0x0c8c }
                float r0 = r5 / r0
                int r0 = (int) r0     // Catch:{ all -> 0x0c8c }
                r10 = 1
                android.graphics.Bitmap r0 = org.telegram.messenger.Bitmaps.createScaledBitmap(r9, r7, r0, r10)     // Catch:{ all -> 0x0c8c }
                goto L_0x0be2
            L_0x0bd1:
                float r7 = r5 / r0
                int r10 = (r7 > r24 ? 1 : (r7 == r24 ? 0 : -1))
                if (r10 <= 0) goto L_0x0be1
                float r7 = r4 / r7
                int r7 = (int) r7     // Catch:{ all -> 0x0c8c }
                int r0 = (int) r0     // Catch:{ all -> 0x0c8c }
                r10 = 1
                android.graphics.Bitmap r0 = org.telegram.messenger.Bitmaps.createScaledBitmap(r9, r7, r0, r10)     // Catch:{ all -> 0x0c8c }
                goto L_0x0be2
            L_0x0be1:
                r0 = r9
            L_0x0be2:
                if (r9 == r0) goto L_0x0be8
                r9.recycle()     // Catch:{ all -> 0x0c8c }
                r9 = r0
            L_0x0be8:
                if (r9 == 0) goto L_0x0c7a
                if (r30 == 0) goto L_0x0CLASSNAME
                int r0 = r9.getWidth()     // Catch:{ all -> 0x0c8c }
                int r7 = r9.getHeight()     // Catch:{ all -> 0x0c8c }
                int r0 = r0 * r7
                r7 = 22500(0x57e4, float:3.1529E-41)
                if (r0 <= r7) goto L_0x0CLASSNAME
                r0 = 100
                r7 = 100
                r10 = 0
                android.graphics.Bitmap r0 = org.telegram.messenger.Bitmaps.createScaledBitmap(r9, r0, r7, r10)     // Catch:{ all -> 0x0c8c }
                goto L_0x0CLASSNAME
            L_0x0CLASSNAME:
                r0 = r9
            L_0x0CLASSNAME:
                boolean r7 = r11.inPurgeable     // Catch:{ all -> 0x0c8c }
                if (r7 == 0) goto L_0x0c0b
                r7 = 0
                goto L_0x0c0c
            L_0x0c0b:
                r7 = 1
            L_0x0c0c:
                int r10 = r0.getWidth()     // Catch:{ all -> 0x0c8c }
                int r12 = r0.getHeight()     // Catch:{ all -> 0x0c8c }
                int r13 = r0.getRowBytes()     // Catch:{ all -> 0x0c8c }
                int r7 = org.telegram.messenger.Utilities.needInvert(r0, r7, r10, r12, r13)     // Catch:{ all -> 0x0c8c }
                if (r7 == 0) goto L_0x0CLASSNAME
                r7 = 1
                goto L_0x0CLASSNAME
            L_0x0CLASSNAME:
                r7 = 0
            L_0x0CLASSNAME:
                if (r0 == r9) goto L_0x0CLASSNAME
                r0.recycle()     // Catch:{ all -> 0x0CLASSNAME }
                goto L_0x0CLASSNAME
            L_0x0CLASSNAME:
                r7 = 0
            L_0x0CLASSNAME:
                r0 = 1120403456(0x42CLASSNAME, float:100.0)
                if (r8 == 0) goto L_0x0CLASSNAME
                int r10 = (r5 > r0 ? 1 : (r5 == r0 ? 0 : -1))
                if (r10 > 0) goto L_0x0CLASSNAME
                int r10 = (r4 > r0 ? 1 : (r4 == r0 ? 0 : -1))
                if (r10 <= 0) goto L_0x0CLASSNAME
            L_0x0CLASSNAME:
                r4 = 80
                r5 = 80
                r10 = 0
                android.graphics.Bitmap r4 = org.telegram.messenger.Bitmaps.createScaledBitmap(r9, r4, r5, r10)     // Catch:{ all -> 0x0CLASSNAME }
                r5 = 1117782016(0x42a00000, float:80.0)
                r9 = 1117782016(0x42a00000, float:80.0)
                r9 = r4
                r4 = 1117782016(0x42a00000, float:80.0)
                goto L_0x0CLASSNAME
            L_0x0CLASSNAME:
                r0 = move-exception
                goto L_0x0CLASSNAME
            L_0x0CLASSNAME:
                if (r8 == 0) goto L_0x0CLASSNAME
                int r5 = (r5 > r0 ? 1 : (r5 == r0 ? 0 : -1))
                if (r5 >= 0) goto L_0x0CLASSNAME
                int r0 = (r4 > r0 ? 1 : (r4 == r0 ? 0 : -1))
                if (r0 >= 0) goto L_0x0CLASSNAME
                android.graphics.Bitmap$Config r0 = r9.getConfig()     // Catch:{ all -> 0x0CLASSNAME }
                android.graphics.Bitmap$Config r4 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ all -> 0x0CLASSNAME }
                if (r0 != r4) goto L_0x0CLASSNAME
                r18 = 3
                boolean r0 = r11.inPurgeable     // Catch:{ all -> 0x0CLASSNAME }
                if (r0 == 0) goto L_0x0CLASSNAME
                r19 = 0
                goto L_0x0CLASSNAME
            L_0x0CLASSNAME:
                r19 = 1
            L_0x0CLASSNAME:
                int r20 = r9.getWidth()     // Catch:{ all -> 0x0CLASSNAME }
                int r21 = r9.getHeight()     // Catch:{ all -> 0x0CLASSNAME }
                int r22 = r9.getRowBytes()     // Catch:{ all -> 0x0CLASSNAME }
                r17 = r9
                org.telegram.messenger.Utilities.blurBitmap(r17, r18, r19, r20, r21, r22)     // Catch:{ all -> 0x0CLASSNAME }
            L_0x0CLASSNAME:
                r4 = r9
                r9 = 1
                goto L_0x0c7d
            L_0x0CLASSNAME:
                r4 = r9
                goto L_0x0c7c
            L_0x0c7a:
                r4 = r9
                r7 = 0
            L_0x0c7c:
                r9 = 0
            L_0x0c7d:
                if (r9 != 0) goto L_0x0c8a
                boolean r0 = r11.inPurgeable     // Catch:{ all -> 0x0CLASSNAME }
                if (r0 == 0) goto L_0x0c8a
                org.telegram.messenger.Utilities.pinBitmap(r4)     // Catch:{ all -> 0x0CLASSNAME }
                goto L_0x0c8a
            L_0x0CLASSNAME:
                r0 = move-exception
                r9 = r4
                goto L_0x0CLASSNAME
            L_0x0c8a:
                r9 = r4
                goto L_0x0CLASSNAME
            L_0x0c8c:
                r0 = move-exception
                goto L_0x0CLASSNAME
            L_0x0c8e:
                r0 = move-exception
                monitor-exit(r4)     // Catch:{ all -> 0x0c8e }
                throw r0     // Catch:{ all -> 0x0CLASSNAME }
            L_0x0CLASSNAME:
                r0 = move-exception
                r9 = r27
            L_0x0CLASSNAME:
                r6 = 0
            L_0x0CLASSNAME:
                r7 = 0
            L_0x0CLASSNAME:
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            L_0x0CLASSNAME:
                r4 = r7
            L_0x0c9a:
                java.lang.Thread.interrupted()
                boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
                if (r0 == 0) goto L_0x0cc4
                if (r3 == 0) goto L_0x0cc4
                java.lang.StringBuilder r0 = new java.lang.StringBuilder
                r0.<init>()
                java.lang.String r3 = "Image Loader image is empty = "
                r0.append(r3)
                if (r9 != 0) goto L_0x0cb1
                r3 = 1
                goto L_0x0cb2
            L_0x0cb1:
                r3 = 0
            L_0x0cb2:
                r0.append(r3)
                java.lang.String r3 = " "
                r0.append(r3)
                r0.append(r2)
                java.lang.String r0 = r0.toString()
                org.telegram.messenger.FileLog.e((java.lang.String) r0)
            L_0x0cc4:
                if (r4 != 0) goto L_0x0cd6
                if (r6 == 0) goto L_0x0cc9
                goto L_0x0cd6
            L_0x0cc9:
                if (r9 == 0) goto L_0x0cd1
                android.graphics.drawable.BitmapDrawable r5 = new android.graphics.drawable.BitmapDrawable
                r5.<init>(r9)
                goto L_0x0cd2
            L_0x0cd1:
                r5 = 0
            L_0x0cd2:
                r1.onPostExecute(r5)
                goto L_0x0d34
            L_0x0cd6:
                if (r9 == 0) goto L_0x0cde
                org.telegram.messenger.ExtendedBitmapDrawable r5 = new org.telegram.messenger.ExtendedBitmapDrawable
                r5.<init>(r9, r4, r6)
                goto L_0x0cdf
            L_0x0cde:
                r5 = 0
            L_0x0cdf:
                r1.onPostExecute(r5)
                goto L_0x0d34
            L_0x0ce3:
                android.graphics.Point r2 = org.telegram.messenger.AndroidUtilities.displaySize
                int r3 = r2.x
                int r2 = r2.y
                java.lang.String r0 = r0.filter
                if (r0 == 0) goto L_0x0d10
                java.lang.String r4 = "_"
                java.lang.String[] r0 = r0.split(r4)
                int r4 = r0.length
                r5 = 2
                if (r4 < r5) goto L_0x0d10
                r4 = 0
                r2 = r0[r4]
                float r2 = java.lang.Float.parseFloat(r2)
                r5 = 1
                r0 = r0[r5]
                float r0 = java.lang.Float.parseFloat(r0)
                float r3 = org.telegram.messenger.AndroidUtilities.density
                float r2 = r2 * r3
                int r2 = (int) r2
                float r0 = r0 * r3
                int r0 = (int) r0
                r3 = r2
                r2 = r0
                goto L_0x0d12
            L_0x0d10:
                r4 = 0
                r5 = 1
            L_0x0d12:
                org.telegram.messenger.ImageLoader$CacheImage r0 = r1.cacheImage     // Catch:{ all -> 0x0d22 }
                java.io.File r7 = r0.finalFilePath     // Catch:{ all -> 0x0d22 }
                int r0 = r0.imageType     // Catch:{ all -> 0x0d22 }
                if (r0 != r6) goto L_0x0d1c
                r9 = 1
                goto L_0x0d1d
            L_0x0d1c:
                r9 = 0
            L_0x0d1d:
                android.graphics.Bitmap r5 = org.telegram.messenger.SvgHelper.getBitmap((java.io.File) r7, (int) r3, (int) r2, (boolean) r9)     // Catch:{ all -> 0x0d22 }
                goto L_0x0d27
            L_0x0d22:
                r0 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
                r5 = 0
            L_0x0d27:
                if (r5 == 0) goto L_0x0d30
                android.graphics.drawable.BitmapDrawable r0 = new android.graphics.drawable.BitmapDrawable
                r0.<init>(r5)
                r5 = r0
                goto L_0x0d31
            L_0x0d30:
                r5 = 0
            L_0x0d31:
                r1.onPostExecute(r5)
            L_0x0d34:
                return
            L_0x0d35:
                r0 = move-exception
                monitor-exit(r2)     // Catch:{ all -> 0x0d35 }
                goto L_0x0d39
            L_0x0d38:
                throw r0
            L_0x0d39:
                goto L_0x0d38
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ImageLoader.CacheOutTask.run():void");
        }

        private void loadLastFrame(RLottieDrawable rLottieDrawable, int i, int i2, boolean z) {
            Bitmap createBitmap = Bitmap.createBitmap(i, i2, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(createBitmap);
            if (z) {
                canvas.scale(2.0f, 2.0f, ((float) i) / 2.0f, ((float) i2) / 2.0f);
            }
            AndroidUtilities.runOnUIThread(new ImageLoader$CacheOutTask$$ExternalSyntheticLambda3(this, rLottieDrawable, canvas, z, i, i2, createBitmap));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$loadLastFrame$1(RLottieDrawable rLottieDrawable, Canvas canvas, boolean z, int i, int i2, Bitmap bitmap) {
            rLottieDrawable.setOnFrameReadyRunnable(new ImageLoader$CacheOutTask$$ExternalSyntheticLambda2(this, rLottieDrawable, canvas, z, i, i2, bitmap));
            rLottieDrawable.setCurrentFrame(z ? rLottieDrawable.getFramesCount() - 1 : 0, true, true);
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$loadLastFrame$0(RLottieDrawable rLottieDrawable, Canvas canvas, boolean z, int i, int i2, Bitmap bitmap) {
            BitmapDrawable bitmapDrawable = null;
            rLottieDrawable.setOnFrameReadyRunnable((Runnable) null);
            if (!(rLottieDrawable.getBackgroundBitmap() == null && rLottieDrawable.getRenderingBitmap() == null)) {
                Bitmap backgroundBitmap = rLottieDrawable.getBackgroundBitmap() != null ? rLottieDrawable.getBackgroundBitmap() : rLottieDrawable.getRenderingBitmap();
                canvas.save();
                if (!z) {
                    canvas.scale((float) (backgroundBitmap.getWidth() / i), (float) (backgroundBitmap.getHeight() / i2), ((float) i) / 2.0f, ((float) i2) / 2.0f);
                }
                Paint paint = new Paint(1);
                paint.setFilterBitmap(true);
                canvas.drawBitmap(backgroundBitmap, 0.0f, 0.0f, paint);
                bitmapDrawable = new BitmapDrawable(bitmap);
            }
            onPostExecute(bitmapDrawable);
            rLottieDrawable.recycle();
        }

        private void onPostExecute(Drawable drawable) {
            AndroidUtilities.runOnUIThread(new ImageLoader$CacheOutTask$$ExternalSyntheticLambda0(this, drawable));
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v1, resolved type: java.lang.String} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v2, resolved type: android.graphics.drawable.BitmapDrawable} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v0, resolved type: android.graphics.drawable.BitmapDrawable} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v3, resolved type: java.lang.String} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v4, resolved type: java.lang.String} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v6, resolved type: android.graphics.drawable.BitmapDrawable} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v8, resolved type: android.graphics.drawable.BitmapDrawable} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v11, resolved type: android.graphics.drawable.Drawable} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v14, resolved type: org.telegram.ui.Components.AnimatedFileDrawable} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v19, resolved type: android.graphics.drawable.BitmapDrawable} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v20, resolved type: android.graphics.drawable.BitmapDrawable} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v21, resolved type: android.graphics.drawable.BitmapDrawable} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v17, resolved type: android.graphics.drawable.BitmapDrawable} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v23, resolved type: android.graphics.drawable.Drawable} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v24, resolved type: org.telegram.ui.Components.RLottieDrawable} */
        /* JADX WARNING: type inference failed for: r1v6, types: [java.lang.String] */
        /* JADX WARNING: type inference failed for: r1v11, types: [java.lang.String] */
        /* JADX WARNING: type inference failed for: r1v16, types: [java.lang.String] */
        /* access modifiers changed from: private */
        /* JADX WARNING: Failed to insert additional move for type inference */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public /* synthetic */ void lambda$onPostExecute$3(android.graphics.drawable.Drawable r7) {
            /*
                r6 = this;
                boolean r0 = r7 instanceof org.telegram.ui.Components.RLottieDrawable
                r1 = 0
                if (r0 == 0) goto L_0x003b
                org.telegram.ui.Components.RLottieDrawable r7 = (org.telegram.ui.Components.RLottieDrawable) r7
                org.telegram.messenger.ImageLoader r0 = org.telegram.messenger.ImageLoader.this
                org.telegram.messenger.LruCache r0 = r0.lottieMemCache
                org.telegram.messenger.ImageLoader$CacheImage r2 = r6.cacheImage
                java.lang.String r2 = r2.key
                java.lang.Object r0 = r0.get(r2)
                android.graphics.drawable.Drawable r0 = (android.graphics.drawable.Drawable) r0
                if (r0 != 0) goto L_0x0027
                org.telegram.messenger.ImageLoader r0 = org.telegram.messenger.ImageLoader.this
                org.telegram.messenger.LruCache r0 = r0.lottieMemCache
                org.telegram.messenger.ImageLoader$CacheImage r2 = r6.cacheImage
                java.lang.String r2 = r2.key
                r0.put(r2, r7)
                goto L_0x002b
            L_0x0027:
                r7.recycle()
                r7 = r0
            L_0x002b:
                if (r7 == 0) goto L_0x0071
                org.telegram.messenger.ImageLoader r0 = org.telegram.messenger.ImageLoader.this
                org.telegram.messenger.ImageLoader$CacheImage r1 = r6.cacheImage
                java.lang.String r1 = r1.key
                r0.incrementUseCount(r1)
                org.telegram.messenger.ImageLoader$CacheImage r0 = r6.cacheImage
                java.lang.String r1 = r0.key
                goto L_0x0071
            L_0x003b:
                boolean r0 = r7 instanceof org.telegram.ui.Components.AnimatedFileDrawable
                if (r0 == 0) goto L_0x0076
                r0 = r7
                org.telegram.ui.Components.AnimatedFileDrawable r0 = (org.telegram.ui.Components.AnimatedFileDrawable) r0
                boolean r2 = r0.isWebmSticker
                if (r2 == 0) goto L_0x0071
                org.telegram.messenger.ImageLoader r7 = org.telegram.messenger.ImageLoader.this
                org.telegram.messenger.ImageLoader$CacheImage r1 = r6.cacheImage
                java.lang.String r1 = r1.key
                android.graphics.drawable.BitmapDrawable r7 = r7.getFromLottieCache(r1)
                if (r7 != 0) goto L_0x0061
                org.telegram.messenger.ImageLoader r7 = org.telegram.messenger.ImageLoader.this
                org.telegram.messenger.LruCache r7 = r7.lottieMemCache
                org.telegram.messenger.ImageLoader$CacheImage r1 = r6.cacheImage
                java.lang.String r1 = r1.key
                r7.put(r1, r0)
                r7 = r0
                goto L_0x0064
            L_0x0061:
                r0.recycle()
            L_0x0064:
                org.telegram.messenger.ImageLoader r0 = org.telegram.messenger.ImageLoader.this
                org.telegram.messenger.ImageLoader$CacheImage r1 = r6.cacheImage
                java.lang.String r1 = r1.key
                r0.incrementUseCount(r1)
                org.telegram.messenger.ImageLoader$CacheImage r0 = r6.cacheImage
                java.lang.String r1 = r0.key
            L_0x0071:
                r5 = r1
                r1 = r7
                r7 = r5
                goto L_0x010d
            L_0x0076:
                boolean r0 = r7 instanceof android.graphics.drawable.BitmapDrawable
                if (r0 == 0) goto L_0x010c
                android.graphics.drawable.BitmapDrawable r7 = (android.graphics.drawable.BitmapDrawable) r7
                org.telegram.messenger.ImageLoader r0 = org.telegram.messenger.ImageLoader.this
                org.telegram.messenger.ImageLoader$CacheImage r2 = r6.cacheImage
                java.lang.String r2 = r2.key
                android.graphics.drawable.BitmapDrawable r0 = r0.getFromMemCache(r2)
                r2 = 1
                if (r0 != 0) goto L_0x00f1
                org.telegram.messenger.ImageLoader$CacheImage r0 = r6.cacheImage
                java.lang.String r0 = r0.key
                java.lang.String r3 = "_f"
                boolean r0 = r0.endsWith(r3)
                if (r0 == 0) goto L_0x00a5
                org.telegram.messenger.ImageLoader r0 = org.telegram.messenger.ImageLoader.this
                org.telegram.messenger.LruCache r0 = r0.wallpaperMemCache
                org.telegram.messenger.ImageLoader$CacheImage r2 = r6.cacheImage
                java.lang.String r2 = r2.key
                r0.put(r2, r7)
                r0 = 0
                r2 = 0
                goto L_0x00f9
            L_0x00a5:
                org.telegram.messenger.ImageLoader$CacheImage r0 = r6.cacheImage
                java.lang.String r0 = r0.key
                java.lang.String r3 = "_isc"
                boolean r0 = r0.endsWith(r3)
                if (r0 != 0) goto L_0x00e3
                android.graphics.Bitmap r0 = r7.getBitmap()
                int r0 = r0.getWidth()
                float r0 = (float) r0
                float r3 = org.telegram.messenger.AndroidUtilities.density
                r4 = 1117782016(0x42a00000, float:80.0)
                float r3 = r3 * r4
                int r0 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1))
                if (r0 > 0) goto L_0x00e3
                android.graphics.Bitmap r0 = r7.getBitmap()
                int r0 = r0.getHeight()
                float r0 = (float) r0
                float r3 = org.telegram.messenger.AndroidUtilities.density
                float r3 = r3 * r4
                int r0 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1))
                if (r0 > 0) goto L_0x00e3
                org.telegram.messenger.ImageLoader r0 = org.telegram.messenger.ImageLoader.this
                org.telegram.messenger.LruCache r0 = r0.smallImagesMemCache
                org.telegram.messenger.ImageLoader$CacheImage r3 = r6.cacheImage
                java.lang.String r3 = r3.key
                r0.put(r3, r7)
                goto L_0x00f9
            L_0x00e3:
                org.telegram.messenger.ImageLoader r0 = org.telegram.messenger.ImageLoader.this
                org.telegram.messenger.LruCache r0 = r0.memCache
                org.telegram.messenger.ImageLoader$CacheImage r3 = r6.cacheImage
                java.lang.String r3 = r3.key
                r0.put(r3, r7)
                goto L_0x00f9
            L_0x00f1:
                android.graphics.Bitmap r7 = r7.getBitmap()
                r7.recycle()
                r7 = r0
            L_0x00f9:
                if (r7 == 0) goto L_0x0071
                if (r2 == 0) goto L_0x0071
                org.telegram.messenger.ImageLoader r0 = org.telegram.messenger.ImageLoader.this
                org.telegram.messenger.ImageLoader$CacheImage r1 = r6.cacheImage
                java.lang.String r1 = r1.key
                r0.incrementUseCount(r1)
                org.telegram.messenger.ImageLoader$CacheImage r0 = r6.cacheImage
                java.lang.String r1 = r0.key
                goto L_0x0071
            L_0x010c:
                r7 = r1
            L_0x010d:
                org.telegram.messenger.ImageLoader r0 = org.telegram.messenger.ImageLoader.this
                org.telegram.messenger.DispatchQueue r0 = r0.imageLoadQueue
                org.telegram.messenger.ImageLoader$CacheOutTask$$ExternalSyntheticLambda1 r2 = new org.telegram.messenger.ImageLoader$CacheOutTask$$ExternalSyntheticLambda1
                r2.<init>(r6, r1, r7)
                r0.postRunnable(r2)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ImageLoader.CacheOutTask.lambda$onPostExecute$3(android.graphics.drawable.Drawable):void");
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onPostExecute$2(Drawable drawable, String str) {
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
    public boolean isAnimatedAvatar(String str) {
        return str != null && str.endsWith("avatar");
    }

    /* access modifiers changed from: private */
    public BitmapDrawable getFromMemCache(String str) {
        BitmapDrawable bitmapDrawable = this.memCache.get(str);
        if (bitmapDrawable == null) {
            bitmapDrawable = this.smallImagesMemCache.get(str);
        }
        if (bitmapDrawable == null) {
            bitmapDrawable = this.wallpaperMemCache.get(str);
        }
        return bitmapDrawable == null ? getFromLottieCache(str) : bitmapDrawable;
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
        /* JADX WARNING: Removed duplicated region for block: B:24:0x0079  */
        /* JADX WARNING: Removed duplicated region for block: B:33:? A[RETURN, SYNTHETIC] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public /* synthetic */ void lambda$setImageAndClear$0(android.graphics.drawable.Drawable r10, java.util.ArrayList r11, java.util.ArrayList r12, java.lang.String r13) {
            /*
                r9 = this;
                boolean r0 = r10 instanceof org.telegram.ui.Components.AnimatedFileDrawable
                r1 = 0
                if (r0 == 0) goto L_0x004a
                r0 = r10
                org.telegram.ui.Components.AnimatedFileDrawable r0 = (org.telegram.ui.Components.AnimatedFileDrawable) r0
                boolean r2 = r0.isWebmSticker
                if (r2 != 0) goto L_0x004a
                r10 = 0
            L_0x000d:
                int r2 = r11.size()
                if (r1 >= r2) goto L_0x0044
                java.lang.Object r2 = r11.get(r1)
                r3 = r2
                org.telegram.messenger.ImageReceiver r3 = (org.telegram.messenger.ImageReceiver) r3
                if (r1 != 0) goto L_0x001e
                r2 = r0
                goto L_0x0022
            L_0x001e:
                org.telegram.ui.Components.AnimatedFileDrawable r2 = r0.makeCopy()
            L_0x0022:
                java.lang.String r5 = r9.key
                int r6 = r9.type
                r7 = 0
                java.lang.Object r4 = r12.get(r1)
                java.lang.Integer r4 = (java.lang.Integer) r4
                int r8 = r4.intValue()
                r4 = r2
                boolean r3 = r3.setImageBitmapByKey(r4, r5, r6, r7, r8)
                if (r3 == 0) goto L_0x003c
                if (r2 != r0) goto L_0x0041
                r10 = 1
                goto L_0x0041
            L_0x003c:
                if (r2 == r0) goto L_0x0041
                r2.recycle()
            L_0x0041:
                int r1 = r1 + 1
                goto L_0x000d
            L_0x0044:
                if (r10 != 0) goto L_0x0077
                r0.recycle()
                goto L_0x0077
            L_0x004a:
                int r0 = r11.size()
                if (r1 >= r0) goto L_0x0077
                java.lang.Object r0 = r11.get(r1)
                r2 = r0
                org.telegram.messenger.ImageReceiver r2 = (org.telegram.messenger.ImageReceiver) r2
                java.lang.String r4 = r9.key
                java.util.ArrayList<java.lang.Integer> r0 = r9.types
                java.lang.Object r0 = r0.get(r1)
                java.lang.Integer r0 = (java.lang.Integer) r0
                int r5 = r0.intValue()
                r6 = 0
                java.lang.Object r0 = r12.get(r1)
                java.lang.Integer r0 = (java.lang.Integer) r0
                int r7 = r0.intValue()
                r3 = r10
                r2.setImageBitmapByKey(r3, r4, r5, r6, r7)
                int r1 = r1 + 1
                goto L_0x004a
            L_0x0077:
                if (r13 == 0) goto L_0x007e
                org.telegram.messenger.ImageLoader r10 = org.telegram.messenger.ImageLoader.this
                r10.decrementUseCount(r13)
            L_0x007e:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ImageLoader.CacheImage.lambda$setImageAndClear$0(android.graphics.drawable.Drawable, java.util.ArrayList, java.util.ArrayList, java.lang.String):void");
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
                            ArrayList arrayList = new ArrayList();
                            arrayList.add(bitmap);
                            AndroidUtilities.recycleBitmaps(arrayList);
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
                            ArrayList arrayList = new ArrayList();
                            arrayList.add(bitmap);
                            AndroidUtilities.recycleBitmaps(arrayList);
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
        this.lottieMemCache = new LruCache<BitmapDrawable>(10485760) {
            /* access modifiers changed from: protected */
            public int sizeOf(String str, BitmapDrawable bitmapDrawable) {
                return bitmapDrawable.getIntrinsicWidth() * bitmapDrawable.getIntrinsicHeight() * 4 * 2;
            }

            public BitmapDrawable put(String str, BitmapDrawable bitmapDrawable) {
                if (bitmapDrawable instanceof AnimatedFileDrawable) {
                    ImageLoader.this.cachedAnimatedFileDrawables.add((AnimatedFileDrawable) bitmapDrawable);
                }
                return (BitmapDrawable) super.put(str, bitmapDrawable);
            }

            /* access modifiers changed from: protected */
            public void entryRemoved(boolean z, String str, BitmapDrawable bitmapDrawable, BitmapDrawable bitmapDrawable2) {
                Integer num = (Integer) ImageLoader.this.bitmapUseCounts.get(str);
                boolean z2 = bitmapDrawable instanceof AnimatedFileDrawable;
                if (z2) {
                    ImageLoader.this.cachedAnimatedFileDrawables.remove((AnimatedFileDrawable) bitmapDrawable);
                }
                if (num == null || num.intValue() == 0) {
                    if (z2) {
                        ((AnimatedFileDrawable) bitmapDrawable).recycle();
                    }
                    if (bitmapDrawable instanceof RLottieDrawable) {
                        ((RLottieDrawable) bitmapDrawable).recycle();
                    }
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
        for (final int i = 0; i < 4; i++) {
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

                public void fileDidLoaded(String str, File file, Object obj, int i) {
                    ImageLoader.this.fileProgresses.remove(str);
                    AndroidUtilities.runOnUIThread(new ImageLoader$5$$ExternalSyntheticLambda6(this, file, str, obj, i, i));
                }

                /* access modifiers changed from: private */
                public /* synthetic */ void lambda$fileDidLoaded$5(File file, String str, Object obj, int i, int i2) {
                    int i3;
                    if (!(SharedConfig.saveToGalleryFlags == 0 || file == null || ((!str.endsWith(".mp4") && !str.endsWith(".jpg")) || !(obj instanceof MessageObject)))) {
                        long dialogId = ((MessageObject) obj).getDialogId();
                        if (dialogId >= 0) {
                            i3 = 1;
                        } else {
                            i3 = ChatObject.isChannelAndNotMegaGroup(MessagesController.getInstance(i).getChat(Long.valueOf(-dialogId))) ? 4 : 2;
                        }
                        if ((i3 & SharedConfig.saveToGalleryFlags) != 0) {
                            AndroidUtilities.addMediaToGallery(file.toString());
                        }
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

    /* JADX WARNING: Missing exception handler attribute for start block: B:16:0x0031 */
    @android.annotation.TargetApi(26)
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static void moveDirectory(java.io.File r1, java.io.File r2) {
        /*
            boolean r0 = r1.exists()
            if (r0 == 0) goto L_0x0036
            boolean r0 = r2.exists()
            if (r0 != 0) goto L_0x0013
            boolean r0 = r2.mkdir()
            if (r0 != 0) goto L_0x0013
            goto L_0x0036
        L_0x0013:
            java.nio.file.Path r1 = r1.toPath()     // Catch:{ Exception -> 0x0032 }
            java.util.stream.Stream r1 = java.nio.file.Files.list(r1)     // Catch:{ Exception -> 0x0032 }
            j$.util.stream.Stream r1 = j$.wrappers.C$r8$wrapper$java$util$stream$Stream$VWRP.convert(r1)     // Catch:{ Exception -> 0x0032 }
            org.telegram.messenger.ImageLoader$$ExternalSyntheticLambda13 r0 = new org.telegram.messenger.ImageLoader$$ExternalSyntheticLambda13     // Catch:{ all -> 0x002b }
            r0.<init>(r2)     // Catch:{ all -> 0x002b }
            r1.forEach(r0)     // Catch:{ all -> 0x002b }
            r1.close()     // Catch:{ Exception -> 0x0032 }
            goto L_0x0036
        L_0x002b:
            r2 = move-exception
            if (r1 == 0) goto L_0x0031
            r1.close()     // Catch:{ all -> 0x0031 }
        L_0x0031:
            throw r2     // Catch:{ Exception -> 0x0032 }
        L_0x0032:
            r1 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)
        L_0x0036:
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

    /* JADX WARNING: Removed duplicated region for block: B:124:0x0264 A[Catch:{ Exception -> 0x0277 }] */
    /* JADX WARNING: Removed duplicated region for block: B:135:0x0298 A[Catch:{ Exception -> 0x02ab }] */
    /* JADX WARNING: Removed duplicated region for block: B:148:0x0103 A[EDGE_INSN: B:148:0x0103->B:56:0x0103 ?: BREAK  , SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:52:0x00e3 A[Catch:{ Exception -> 0x02bd }] */
    /* JADX WARNING: Removed duplicated region for block: B:59:0x0114 A[SYNTHETIC, Splitter:B:59:0x0114] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.util.SparseArray<java.io.File> createMediaPaths() {
        /*
            r14 = this;
            android.util.SparseArray r0 = new android.util.SparseArray
            r0.<init>()
            java.io.File r1 = org.telegram.messenger.AndroidUtilities.getCacheDir()
            boolean r2 = r1.isDirectory()
            if (r2 != 0) goto L_0x0017
            r1.mkdirs()     // Catch:{ Exception -> 0x0013 }
            goto L_0x0017
        L_0x0013:
            r2 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)
        L_0x0017:
            java.io.File r2 = new java.io.File
            java.lang.String r3 = ".nomedia"
            r2.<init>(r1, r3)
            org.telegram.messenger.AndroidUtilities.createEmptyFile(r2)
            r2 = 4
            r0.put(r2, r1)
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r2 == 0) goto L_0x003d
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r4 = "cache path = "
            r2.append(r4)
            r2.append(r1)
            java.lang.String r2 = r2.toString()
            org.telegram.messenger.FileLog.d(r2)
        L_0x003d:
            java.lang.String r2 = "mounted"
            java.lang.String r4 = android.os.Environment.getExternalStorageState()     // Catch:{ Exception -> 0x02bd }
            boolean r2 = r2.equals(r4)     // Catch:{ Exception -> 0x02bd }
            if (r2 == 0) goto L_0x02b0
            java.io.File r2 = android.os.Environment.getExternalStorageDirectory()     // Catch:{ Exception -> 0x02bd }
            int r4 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x02bd }
            r5 = 19
            r6 = 0
            if (r4 < r5) goto L_0x0080
            java.lang.String r4 = org.telegram.messenger.SharedConfig.storageCacheDir     // Catch:{ Exception -> 0x02bd }
            boolean r4 = android.text.TextUtils.isEmpty(r4)     // Catch:{ Exception -> 0x02bd }
            if (r4 != 0) goto L_0x0080
            java.util.ArrayList r4 = org.telegram.messenger.AndroidUtilities.getRootDirs()     // Catch:{ Exception -> 0x02bd }
            if (r4 == 0) goto L_0x0080
            int r7 = r4.size()     // Catch:{ Exception -> 0x02bd }
            r8 = 0
        L_0x0067:
            if (r8 >= r7) goto L_0x0080
            java.lang.Object r9 = r4.get(r8)     // Catch:{ Exception -> 0x02bd }
            java.io.File r9 = (java.io.File) r9     // Catch:{ Exception -> 0x02bd }
            java.lang.String r10 = r9.getAbsolutePath()     // Catch:{ Exception -> 0x02bd }
            java.lang.String r11 = org.telegram.messenger.SharedConfig.storageCacheDir     // Catch:{ Exception -> 0x02bd }
            boolean r10 = r10.startsWith(r11)     // Catch:{ Exception -> 0x02bd }
            if (r10 == 0) goto L_0x007d
            r2 = r9
            goto L_0x0080
        L_0x007d:
            int r8 = r8 + 1
            goto L_0x0067
        L_0x0080:
            int r4 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x02bd }
            r7 = 30
            r8 = 0
            java.lang.String r9 = "Telegram"
            if (r4 < r7) goto L_0x00c0
            android.content.Context r2 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x00ac }
            java.io.File[] r2 = r2.getExternalMediaDirs()     // Catch:{ Exception -> 0x00ac }
            int r2 = r2.length     // Catch:{ Exception -> 0x00ac }
            if (r2 <= 0) goto L_0x00aa
            android.content.Context r2 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x00ac }
            java.io.File[] r2 = r2.getExternalMediaDirs()     // Catch:{ Exception -> 0x00ac }
            r2 = r2[r6]     // Catch:{ Exception -> 0x00ac }
            java.io.File r4 = new java.io.File     // Catch:{ Exception -> 0x00a5 }
            r4.<init>(r2, r9)     // Catch:{ Exception -> 0x00a5 }
            r4.mkdirs()     // Catch:{ Exception -> 0x00a3 }
            goto L_0x00b1
        L_0x00a3:
            r2 = move-exception
            goto L_0x00ae
        L_0x00a5:
            r4 = move-exception
            r13 = r4
            r4 = r2
            r2 = r13
            goto L_0x00ae
        L_0x00aa:
            r4 = r8
            goto L_0x00b1
        L_0x00ac:
            r2 = move-exception
            r4 = r8
        L_0x00ae:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)     // Catch:{ Exception -> 0x02bd }
        L_0x00b1:
            android.content.Context r2 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x02bd }
            java.io.File r2 = r2.getExternalFilesDir(r8)     // Catch:{ Exception -> 0x02bd }
            java.io.File r7 = new java.io.File     // Catch:{ Exception -> 0x02bd }
            r7.<init>(r2, r9)     // Catch:{ Exception -> 0x02bd }
            r14.telegramPath = r7     // Catch:{ Exception -> 0x02bd }
            r8 = r4
            goto L_0x00c7
        L_0x00c0:
            java.io.File r4 = new java.io.File     // Catch:{ Exception -> 0x02bd }
            r4.<init>(r2, r9)     // Catch:{ Exception -> 0x02bd }
            r14.telegramPath = r4     // Catch:{ Exception -> 0x02bd }
        L_0x00c7:
            java.io.File r2 = r14.telegramPath     // Catch:{ Exception -> 0x02bd }
            r2.mkdirs()     // Catch:{ Exception -> 0x02bd }
            int r2 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x02bd }
            if (r2 < r5) goto L_0x0103
            java.io.File r2 = r14.telegramPath     // Catch:{ Exception -> 0x02bd }
            boolean r2 = r2.isDirectory()     // Catch:{ Exception -> 0x02bd }
            if (r2 != 0) goto L_0x0103
            java.util.ArrayList r2 = org.telegram.messenger.AndroidUtilities.getDataDirs()     // Catch:{ Exception -> 0x02bd }
            int r4 = r2.size()     // Catch:{ Exception -> 0x02bd }
            r5 = 0
        L_0x00e1:
            if (r5 >= r4) goto L_0x0103
            java.lang.Object r7 = r2.get(r5)     // Catch:{ Exception -> 0x02bd }
            java.io.File r7 = (java.io.File) r7     // Catch:{ Exception -> 0x02bd }
            java.lang.String r10 = r7.getAbsolutePath()     // Catch:{ Exception -> 0x02bd }
            java.lang.String r11 = org.telegram.messenger.SharedConfig.storageCacheDir     // Catch:{ Exception -> 0x02bd }
            boolean r10 = r10.startsWith(r11)     // Catch:{ Exception -> 0x02bd }
            if (r10 == 0) goto L_0x0100
            java.io.File r2 = new java.io.File     // Catch:{ Exception -> 0x02bd }
            r2.<init>(r7, r9)     // Catch:{ Exception -> 0x02bd }
            r14.telegramPath = r2     // Catch:{ Exception -> 0x02bd }
            r2.mkdirs()     // Catch:{ Exception -> 0x02bd }
            goto L_0x0103
        L_0x0100:
            int r5 = r5 + 1
            goto L_0x00e1
        L_0x0103:
            java.io.File r2 = r14.telegramPath     // Catch:{ Exception -> 0x02bd }
            boolean r2 = r2.isDirectory()     // Catch:{ Exception -> 0x02bd }
            java.lang.String r4 = "video path = "
            java.lang.String r5 = "image path = "
            java.lang.String r7 = "Telegram Video"
            java.lang.String r9 = "Telegram Images"
            r10 = 2
            if (r2 == 0) goto L_0x023f
            java.io.File r2 = new java.io.File     // Catch:{ Exception -> 0x0144 }
            java.io.File r11 = r14.telegramPath     // Catch:{ Exception -> 0x0144 }
            r2.<init>(r11, r9)     // Catch:{ Exception -> 0x0144 }
            r2.mkdir()     // Catch:{ Exception -> 0x0144 }
            boolean r11 = r2.isDirectory()     // Catch:{ Exception -> 0x0144 }
            if (r11 == 0) goto L_0x0148
            boolean r11 = r14.canMoveFiles(r1, r2, r6)     // Catch:{ Exception -> 0x0144 }
            if (r11 == 0) goto L_0x0148
            r0.put(r6, r2)     // Catch:{ Exception -> 0x0144 }
            boolean r11 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x0144 }
            if (r11 == 0) goto L_0x0148
            java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0144 }
            r11.<init>()     // Catch:{ Exception -> 0x0144 }
            r11.append(r5)     // Catch:{ Exception -> 0x0144 }
            r11.append(r2)     // Catch:{ Exception -> 0x0144 }
            java.lang.String r2 = r11.toString()     // Catch:{ Exception -> 0x0144 }
            org.telegram.messenger.FileLog.d(r2)     // Catch:{ Exception -> 0x0144 }
            goto L_0x0148
        L_0x0144:
            r2 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)     // Catch:{ Exception -> 0x02bd }
        L_0x0148:
            java.io.File r2 = new java.io.File     // Catch:{ Exception -> 0x0178 }
            java.io.File r11 = r14.telegramPath     // Catch:{ Exception -> 0x0178 }
            r2.<init>(r11, r7)     // Catch:{ Exception -> 0x0178 }
            r2.mkdir()     // Catch:{ Exception -> 0x0178 }
            boolean r11 = r2.isDirectory()     // Catch:{ Exception -> 0x0178 }
            if (r11 == 0) goto L_0x017c
            boolean r11 = r14.canMoveFiles(r1, r2, r10)     // Catch:{ Exception -> 0x0178 }
            if (r11 == 0) goto L_0x017c
            r0.put(r10, r2)     // Catch:{ Exception -> 0x0178 }
            boolean r11 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x0178 }
            if (r11 == 0) goto L_0x017c
            java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0178 }
            r11.<init>()     // Catch:{ Exception -> 0x0178 }
            r11.append(r4)     // Catch:{ Exception -> 0x0178 }
            r11.append(r2)     // Catch:{ Exception -> 0x0178 }
            java.lang.String r2 = r11.toString()     // Catch:{ Exception -> 0x0178 }
            org.telegram.messenger.FileLog.d(r2)     // Catch:{ Exception -> 0x0178 }
            goto L_0x017c
        L_0x0178:
            r2 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)     // Catch:{ Exception -> 0x02bd }
        L_0x017c:
            java.io.File r2 = new java.io.File     // Catch:{ Exception -> 0x01b9 }
            java.io.File r11 = r14.telegramPath     // Catch:{ Exception -> 0x01b9 }
            java.lang.String r12 = "Telegram Audio"
            r2.<init>(r11, r12)     // Catch:{ Exception -> 0x01b9 }
            r2.mkdir()     // Catch:{ Exception -> 0x01b9 }
            boolean r11 = r2.isDirectory()     // Catch:{ Exception -> 0x01b9 }
            if (r11 == 0) goto L_0x01bd
            r11 = 1
            boolean r12 = r14.canMoveFiles(r1, r2, r11)     // Catch:{ Exception -> 0x01b9 }
            if (r12 == 0) goto L_0x01bd
            java.io.File r12 = new java.io.File     // Catch:{ Exception -> 0x01b9 }
            r12.<init>(r2, r3)     // Catch:{ Exception -> 0x01b9 }
            org.telegram.messenger.AndroidUtilities.createEmptyFile(r12)     // Catch:{ Exception -> 0x01b9 }
            r0.put(r11, r2)     // Catch:{ Exception -> 0x01b9 }
            boolean r11 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x01b9 }
            if (r11 == 0) goto L_0x01bd
            java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x01b9 }
            r11.<init>()     // Catch:{ Exception -> 0x01b9 }
            java.lang.String r12 = "audio path = "
            r11.append(r12)     // Catch:{ Exception -> 0x01b9 }
            r11.append(r2)     // Catch:{ Exception -> 0x01b9 }
            java.lang.String r2 = r11.toString()     // Catch:{ Exception -> 0x01b9 }
            org.telegram.messenger.FileLog.d(r2)     // Catch:{ Exception -> 0x01b9 }
            goto L_0x01bd
        L_0x01b9:
            r2 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)     // Catch:{ Exception -> 0x02bd }
        L_0x01bd:
            java.io.File r2 = new java.io.File     // Catch:{ Exception -> 0x01fa }
            java.io.File r11 = r14.telegramPath     // Catch:{ Exception -> 0x01fa }
            java.lang.String r12 = "Telegram Documents"
            r2.<init>(r11, r12)     // Catch:{ Exception -> 0x01fa }
            r2.mkdir()     // Catch:{ Exception -> 0x01fa }
            boolean r11 = r2.isDirectory()     // Catch:{ Exception -> 0x01fa }
            if (r11 == 0) goto L_0x01fe
            r11 = 3
            boolean r12 = r14.canMoveFiles(r1, r2, r11)     // Catch:{ Exception -> 0x01fa }
            if (r12 == 0) goto L_0x01fe
            java.io.File r12 = new java.io.File     // Catch:{ Exception -> 0x01fa }
            r12.<init>(r2, r3)     // Catch:{ Exception -> 0x01fa }
            org.telegram.messenger.AndroidUtilities.createEmptyFile(r12)     // Catch:{ Exception -> 0x01fa }
            r0.put(r11, r2)     // Catch:{ Exception -> 0x01fa }
            boolean r11 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x01fa }
            if (r11 == 0) goto L_0x01fe
            java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x01fa }
            r11.<init>()     // Catch:{ Exception -> 0x01fa }
            java.lang.String r12 = "documents path = "
            r11.append(r12)     // Catch:{ Exception -> 0x01fa }
            r11.append(r2)     // Catch:{ Exception -> 0x01fa }
            java.lang.String r2 = r11.toString()     // Catch:{ Exception -> 0x01fa }
            org.telegram.messenger.FileLog.d(r2)     // Catch:{ Exception -> 0x01fa }
            goto L_0x01fe
        L_0x01fa:
            r2 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)     // Catch:{ Exception -> 0x02bd }
        L_0x01fe:
            java.io.File r2 = new java.io.File     // Catch:{ Exception -> 0x023b }
            java.io.File r11 = r14.telegramPath     // Catch:{ Exception -> 0x023b }
            java.lang.String r12 = "Telegram Files"
            r2.<init>(r11, r12)     // Catch:{ Exception -> 0x023b }
            r2.mkdir()     // Catch:{ Exception -> 0x023b }
            boolean r11 = r2.isDirectory()     // Catch:{ Exception -> 0x023b }
            if (r11 == 0) goto L_0x023f
            r11 = 5
            boolean r12 = r14.canMoveFiles(r1, r2, r11)     // Catch:{ Exception -> 0x023b }
            if (r12 == 0) goto L_0x023f
            java.io.File r12 = new java.io.File     // Catch:{ Exception -> 0x023b }
            r12.<init>(r2, r3)     // Catch:{ Exception -> 0x023b }
            org.telegram.messenger.AndroidUtilities.createEmptyFile(r12)     // Catch:{ Exception -> 0x023b }
            r0.put(r11, r2)     // Catch:{ Exception -> 0x023b }
            boolean r3 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x023b }
            if (r3 == 0) goto L_0x023f
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x023b }
            r3.<init>()     // Catch:{ Exception -> 0x023b }
            java.lang.String r11 = "files path = "
            r3.append(r11)     // Catch:{ Exception -> 0x023b }
            r3.append(r2)     // Catch:{ Exception -> 0x023b }
            java.lang.String r2 = r3.toString()     // Catch:{ Exception -> 0x023b }
            org.telegram.messenger.FileLog.d(r2)     // Catch:{ Exception -> 0x023b }
            goto L_0x023f
        L_0x023b:
            r2 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)     // Catch:{ Exception -> 0x02bd }
        L_0x023f:
            if (r8 == 0) goto L_0x02b9
            boolean r2 = r8.isDirectory()     // Catch:{ Exception -> 0x02bd }
            if (r2 == 0) goto L_0x02b9
            java.io.File r2 = new java.io.File     // Catch:{ Exception -> 0x0277 }
            r2.<init>(r8, r9)     // Catch:{ Exception -> 0x0277 }
            r2.mkdir()     // Catch:{ Exception -> 0x0277 }
            boolean r3 = r2.isDirectory()     // Catch:{ Exception -> 0x0277 }
            if (r3 == 0) goto L_0x027b
            boolean r3 = r14.canMoveFiles(r1, r2, r6)     // Catch:{ Exception -> 0x0277 }
            if (r3 == 0) goto L_0x027b
            r3 = 100
            r0.put(r3, r2)     // Catch:{ Exception -> 0x0277 }
            boolean r3 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x0277 }
            if (r3 == 0) goto L_0x027b
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0277 }
            r3.<init>()     // Catch:{ Exception -> 0x0277 }
            r3.append(r5)     // Catch:{ Exception -> 0x0277 }
            r3.append(r2)     // Catch:{ Exception -> 0x0277 }
            java.lang.String r2 = r3.toString()     // Catch:{ Exception -> 0x0277 }
            org.telegram.messenger.FileLog.d(r2)     // Catch:{ Exception -> 0x0277 }
            goto L_0x027b
        L_0x0277:
            r2 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)     // Catch:{ Exception -> 0x02bd }
        L_0x027b:
            java.io.File r2 = new java.io.File     // Catch:{ Exception -> 0x02ab }
            r2.<init>(r8, r7)     // Catch:{ Exception -> 0x02ab }
            r2.mkdir()     // Catch:{ Exception -> 0x02ab }
            boolean r3 = r2.isDirectory()     // Catch:{ Exception -> 0x02ab }
            if (r3 == 0) goto L_0x02b9
            boolean r1 = r14.canMoveFiles(r1, r2, r10)     // Catch:{ Exception -> 0x02ab }
            if (r1 == 0) goto L_0x02b9
            r1 = 101(0x65, float:1.42E-43)
            r0.put(r1, r2)     // Catch:{ Exception -> 0x02ab }
            boolean r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x02ab }
            if (r1 == 0) goto L_0x02b9
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x02ab }
            r1.<init>()     // Catch:{ Exception -> 0x02ab }
            r1.append(r4)     // Catch:{ Exception -> 0x02ab }
            r1.append(r2)     // Catch:{ Exception -> 0x02ab }
            java.lang.String r1 = r1.toString()     // Catch:{ Exception -> 0x02ab }
            org.telegram.messenger.FileLog.d(r1)     // Catch:{ Exception -> 0x02ab }
            goto L_0x02b9
        L_0x02ab:
            r1 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)     // Catch:{ Exception -> 0x02bd }
            goto L_0x02b9
        L_0x02b0:
            boolean r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x02bd }
            if (r1 == 0) goto L_0x02b9
            java.lang.String r1 = "this Android can't rename files"
            org.telegram.messenger.FileLog.d(r1)     // Catch:{ Exception -> 0x02bd }
        L_0x02b9:
            org.telegram.messenger.SharedConfig.checkSaveToGalleryFiles()     // Catch:{ Exception -> 0x02bd }
            goto L_0x02c1
        L_0x02bd:
            r1 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)
        L_0x02c1:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ImageLoader.createMediaPaths():android.util.SparseArray");
    }

    /* JADX WARNING: Removed duplicated region for block: B:34:0x0072 A[SYNTHETIC, Splitter:B:34:0x0072] */
    /* JADX WARNING: Removed duplicated region for block: B:39:0x007e A[SYNTHETIC, Splitter:B:39:0x007e] */
    /* JADX WARNING: Removed duplicated region for block: B:46:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean canMoveFiles(java.io.File r6, java.io.File r7, int r8) {
        /*
            r5 = this;
            r0 = 1
            java.lang.String r1 = "000000000_999999.f"
            java.lang.String r2 = "000000000_999999_temp.f"
            r3 = 0
            if (r8 != 0) goto L_0x0018
            java.io.File r8 = new java.io.File     // Catch:{ Exception -> 0x0016 }
            r8.<init>(r6, r2)     // Catch:{ Exception -> 0x0016 }
            java.io.File r6 = new java.io.File     // Catch:{ Exception -> 0x0016 }
            r6.<init>(r7, r1)     // Catch:{ Exception -> 0x0016 }
            goto L_0x0047
        L_0x0013:
            r6 = move-exception
            goto L_0x007c
        L_0x0016:
            r6 = move-exception
            goto L_0x006d
        L_0x0018:
            r4 = 3
            if (r8 == r4) goto L_0x003d
            r4 = 5
            if (r8 != r4) goto L_0x001f
            goto L_0x003d
        L_0x001f:
            if (r8 != r0) goto L_0x002c
            java.io.File r8 = new java.io.File     // Catch:{ Exception -> 0x0016 }
            r8.<init>(r6, r2)     // Catch:{ Exception -> 0x0016 }
            java.io.File r6 = new java.io.File     // Catch:{ Exception -> 0x0016 }
            r6.<init>(r7, r1)     // Catch:{ Exception -> 0x0016 }
            goto L_0x0047
        L_0x002c:
            r4 = 2
            if (r8 != r4) goto L_0x003a
            java.io.File r8 = new java.io.File     // Catch:{ Exception -> 0x0016 }
            r8.<init>(r6, r2)     // Catch:{ Exception -> 0x0016 }
            java.io.File r6 = new java.io.File     // Catch:{ Exception -> 0x0016 }
            r6.<init>(r7, r1)     // Catch:{ Exception -> 0x0016 }
            goto L_0x0047
        L_0x003a:
            r6 = r3
            r8 = r6
            goto L_0x0047
        L_0x003d:
            java.io.File r8 = new java.io.File     // Catch:{ Exception -> 0x0016 }
            r8.<init>(r6, r2)     // Catch:{ Exception -> 0x0016 }
            java.io.File r6 = new java.io.File     // Catch:{ Exception -> 0x0016 }
            r6.<init>(r7, r1)     // Catch:{ Exception -> 0x0016 }
        L_0x0047:
            r7 = 1024(0x400, float:1.435E-42)
            byte[] r7 = new byte[r7]     // Catch:{ Exception -> 0x0016 }
            r8.createNewFile()     // Catch:{ Exception -> 0x0016 }
            java.io.RandomAccessFile r1 = new java.io.RandomAccessFile     // Catch:{ Exception -> 0x0016 }
            java.lang.String r2 = "rws"
            r1.<init>(r8, r2)     // Catch:{ Exception -> 0x0016 }
            r1.write(r7)     // Catch:{ Exception -> 0x006b, all -> 0x0068 }
            r1.close()     // Catch:{ Exception -> 0x006b, all -> 0x0068 }
            boolean r7 = r8.renameTo(r6)     // Catch:{ Exception -> 0x0016 }
            r8.delete()     // Catch:{ Exception -> 0x0016 }
            r6.delete()     // Catch:{ Exception -> 0x0016 }
            if (r7 == 0) goto L_0x007a
            return r0
        L_0x0068:
            r6 = move-exception
            r3 = r1
            goto L_0x007c
        L_0x006b:
            r6 = move-exception
            r3 = r1
        L_0x006d:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r6)     // Catch:{ all -> 0x0013 }
            if (r3 == 0) goto L_0x007a
            r3.close()     // Catch:{ Exception -> 0x0076 }
            goto L_0x007a
        L_0x0076:
            r6 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r6)
        L_0x007a:
            r6 = 0
            return r6
        L_0x007c:
            if (r3 == 0) goto L_0x0086
            r3.close()     // Catch:{ Exception -> 0x0082 }
            goto L_0x0086
        L_0x0082:
            r7 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r7)
        L_0x0086:
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
            return getFromLottieCache(str) != null;
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

    private void createLoadOperationForImageReceiver(ImageReceiver imageReceiver, String str, String str2, String str3, ImageLocation imageLocation, String str4, long j, int i, int i2, int i3, int i4) {
        ImageReceiver imageReceiver2 = imageReceiver;
        int i5 = i2;
        if (imageReceiver2 == null || str2 == null || str == null || imageLocation == null) {
            return;
        }
        int tag = imageReceiver2.getTag(i5);
        if (tag == 0) {
            tag = this.lastImageNum;
            imageReceiver2.setTag(tag, i5);
            int i6 = this.lastImageNum + 1;
            this.lastImageNum = i6;
            if (i6 == Integer.MAX_VALUE) {
                this.lastImageNum = 0;
            }
        }
        int i7 = tag;
        boolean isNeedsQualityThumb = imageReceiver.isNeedsQualityThumb();
        Object parentObject = imageReceiver.getParentObject();
        TLRPC$Document qualityThumbDocument = imageReceiver.getQualityThumbDocument();
        boolean isShouldGenerateQualityThumb = imageReceiver.isShouldGenerateQualityThumb();
        ImageLoader$$ExternalSyntheticLambda2 imageLoader$$ExternalSyntheticLambda2 = r0;
        ImageLoader$$ExternalSyntheticLambda2 imageLoader$$ExternalSyntheticLambda22 = new ImageLoader$$ExternalSyntheticLambda2(this, i3, str2, str, i7, imageReceiver, i4, str4, i2, imageLocation, i5 == 0 && imageReceiver.isCurrentKeyQuality(), parentObject, imageReceiver.getCurrentAccount(), qualityThumbDocument, isNeedsQualityThumb, isShouldGenerateQualityThumb, str3, i, j);
        ImageLoader$$ExternalSyntheticLambda2 imageLoader$$ExternalSyntheticLambda23 = imageLoader$$ExternalSyntheticLambda2;
        this.imageLoadQueue.postRunnable(imageLoader$$ExternalSyntheticLambda23);
        imageReceiver.addLoadingImageRunnable(imageLoader$$ExternalSyntheticLambda23);
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:142:0x0308, code lost:
        if (r7 == false) goto L_0x03a5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:186:0x03e0, code lost:
        if (r7.equals(r8) != false) goto L_0x03e5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:228:0x04ba, code lost:
        if (r10.exists() == false) goto L_0x04bf;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:68:0x01b8, code lost:
        if (r8.exists() == false) goto L_0x01ba;
     */
    /* JADX WARNING: Removed duplicated region for block: B:194:0x0418  */
    /* JADX WARNING: Removed duplicated region for block: B:199:0x0429  */
    /* JADX WARNING: Removed duplicated region for block: B:67:0x01ad  */
    /* JADX WARNING: Removed duplicated region for block: B:71:0x01bd  */
    /* JADX WARNING: Removed duplicated region for block: B:72:0x01c0  */
    /* JADX WARNING: Removed duplicated region for block: B:74:0x01c3  */
    /* JADX WARNING: Removed duplicated region for block: B:84:0x0214  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$createLoadOperationForImageReceiver$6(int r25, java.lang.String r26, java.lang.String r27, int r28, org.telegram.messenger.ImageReceiver r29, int r30, java.lang.String r31, int r32, org.telegram.messenger.ImageLocation r33, boolean r34, java.lang.Object r35, int r36, org.telegram.tgnet.TLRPC$Document r37, boolean r38, boolean r39, java.lang.String r40, int r41, long r42) {
        /*
            r24 = this;
            r0 = r24
            r1 = r25
            r2 = r26
            r9 = r27
            r10 = r28
            r11 = r29
            r12 = r31
            r13 = r33
            r14 = r35
            r15 = r37
            r8 = r40
            r7 = r41
            r5 = r42
            r4 = 2
            if (r1 == r4) goto L_0x00b0
            java.util.HashMap<java.lang.String, org.telegram.messenger.ImageLoader$CacheImage> r3 = r0.imageLoadingByUrl
            java.lang.Object r3 = r3.get(r2)
            org.telegram.messenger.ImageLoader$CacheImage r3 = (org.telegram.messenger.ImageLoader.CacheImage) r3
            java.util.HashMap<java.lang.String, org.telegram.messenger.ImageLoader$CacheImage> r4 = r0.imageLoadingByKeys
            java.lang.Object r4 = r4.get(r9)
            org.telegram.messenger.ImageLoader$CacheImage r4 = (org.telegram.messenger.ImageLoader.CacheImage) r4
            android.util.SparseArray<org.telegram.messenger.ImageLoader$CacheImage> r12 = r0.imageLoadingByTag
            java.lang.Object r12 = r12.get(r10)
            org.telegram.messenger.ImageLoader$CacheImage r12 = (org.telegram.messenger.ImageLoader.CacheImage) r12
            if (r12 == 0) goto L_0x007b
            if (r12 != r4) goto L_0x0048
            r9 = r30
            r12.setImageReceiverGuid(r11, r9)
            r16 = r3
            r17 = r4
            r9 = r8
            r3 = 1
            r8 = 2
            r18 = 0
            goto L_0x0084
        L_0x0048:
            r9 = r30
            if (r12 != r3) goto L_0x006f
            r16 = r3
            if (r4 != 0) goto L_0x0066
            r18 = 0
            r3 = r12
            r17 = r4
            r12 = 2
            r4 = r29
            r5 = r27
            r6 = r31
            r9 = r7
            r7 = r32
            r9 = r8
            r8 = r30
            r3.replaceImageReceiver(r4, r5, r6, r7, r8)
            goto L_0x006c
        L_0x0066:
            r17 = r4
            r9 = r8
            r12 = 2
            r18 = 0
        L_0x006c:
            r3 = 1
            r8 = 2
            goto L_0x0084
        L_0x006f:
            r16 = r3
            r17 = r4
            r9 = r8
            r8 = 2
            r18 = 0
            r12.removeImageReceiver(r11)
            goto L_0x0083
        L_0x007b:
            r16 = r3
            r17 = r4
            r9 = r8
            r8 = 2
            r18 = 0
        L_0x0083:
            r3 = 0
        L_0x0084:
            if (r3 != 0) goto L_0x009a
            if (r17 == 0) goto L_0x009a
            r3 = r17
            r4 = r29
            r5 = r27
            r6 = r31
            r7 = r32
            r12 = 2
            r8 = r30
            r3.addImageReceiver(r4, r5, r6, r7, r8)
            r3 = 1
            goto L_0x009b
        L_0x009a:
            r12 = 2
        L_0x009b:
            if (r3 != 0) goto L_0x00b5
            if (r16 == 0) goto L_0x00b5
            r3 = r16
            r4 = r29
            r5 = r27
            r6 = r31
            r7 = r32
            r8 = r30
            r3.addImageReceiver(r4, r5, r6, r7, r8)
            r3 = 1
            goto L_0x00b5
        L_0x00b0:
            r9 = r8
            r12 = 2
            r18 = 0
            r3 = 0
        L_0x00b5:
            if (r3 != 0) goto L_0x0672
            java.lang.String r3 = r13.path
            java.lang.String r8 = "athumb"
            java.lang.String r4 = "_"
            r16 = 4
            if (r3 == 0) goto L_0x011d
            java.lang.String r6 = "http"
            boolean r6 = r3.startsWith(r6)
            if (r6 != 0) goto L_0x0112
            boolean r6 = r3.startsWith(r8)
            if (r6 != 0) goto L_0x0112
            java.lang.String r6 = "thumb://"
            boolean r6 = r3.startsWith(r6)
            java.lang.String r10 = ":"
            if (r6 == 0) goto L_0x00ef
            r6 = 8
            int r6 = r3.indexOf(r10, r6)
            if (r6 < 0) goto L_0x00ed
            java.io.File r10 = new java.io.File
            r15 = 1
            int r6 = r6 + r15
            java.lang.String r3 = r3.substring(r6)
            r10.<init>(r3)
            goto L_0x0110
        L_0x00ed:
            r10 = 0
            goto L_0x0110
        L_0x00ef:
            java.lang.String r6 = "vthumb://"
            boolean r6 = r3.startsWith(r6)
            if (r6 == 0) goto L_0x010b
            r6 = 9
            int r6 = r3.indexOf(r10, r6)
            if (r6 < 0) goto L_0x00ed
            java.io.File r10 = new java.io.File
            r15 = 1
            int r6 = r6 + r15
            java.lang.String r3 = r3.substring(r6)
            r10.<init>(r3)
            goto L_0x0110
        L_0x010b:
            java.io.File r10 = new java.io.File
            r10.<init>(r3)
        L_0x0110:
            r3 = 1
            goto L_0x0114
        L_0x0112:
            r3 = 0
            r10 = 0
        L_0x0114:
            r20 = r8
            r5 = 0
            r6 = 2
            r12 = 1
            r8 = r31
            goto L_0x022b
        L_0x011d:
            if (r1 != 0) goto L_0x0222
            if (r34 == 0) goto L_0x0222
            boolean r3 = r14 instanceof org.telegram.messenger.MessageObject
            if (r3 == 0) goto L_0x0146
            r3 = r14
            org.telegram.messenger.MessageObject r3 = (org.telegram.messenger.MessageObject) r3
            org.telegram.tgnet.TLRPC$Document r6 = r3.getDocument()
            org.telegram.tgnet.TLRPC$Message r15 = r3.messageOwner
            java.lang.String r15 = r15.attachPath
            org.telegram.messenger.FileLoader r7 = org.telegram.messenger.FileLoader.getInstance(r36)
            org.telegram.tgnet.TLRPC$Message r12 = r3.messageOwner
            java.io.File r7 = r7.getPathToMessage(r12)
            int r3 = r3.getMediaType()
            r12 = r7
            r37 = r15
            r7 = r3
            r15 = r6
            r3 = 0
            r6 = 1
            goto L_0x0167
        L_0x0146:
            if (r15 == 0) goto L_0x0160
            org.telegram.messenger.FileLoader r3 = org.telegram.messenger.FileLoader.getInstance(r36)
            r6 = 1
            java.io.File r7 = r3.getPathToAttach(r15, r6)
            boolean r3 = org.telegram.messenger.MessageObject.isVideoDocument(r37)
            if (r3 == 0) goto L_0x0159
            r3 = 2
            goto L_0x015a
        L_0x0159:
            r3 = 3
        L_0x015a:
            r12 = r7
            r37 = 0
            r7 = r3
            r3 = 1
            goto L_0x0167
        L_0x0160:
            r6 = 1
            r37 = 0
            r3 = 0
            r7 = 0
            r12 = 0
            r15 = 0
        L_0x0167:
            if (r15 == 0) goto L_0x021b
            if (r38 == 0) goto L_0x01a1
            java.io.File r6 = new java.io.File
            java.io.File r5 = org.telegram.messenger.FileLoader.getDirectory(r16)
            r20 = r8
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            r21 = r12
            java.lang.String r12 = "q_"
            r8.append(r12)
            int r12 = r15.dc_id
            r8.append(r12)
            r8.append(r4)
            long r12 = r15.id
            r8.append(r12)
            java.lang.String r12 = ".jpg"
            r8.append(r12)
            java.lang.String r8 = r8.toString()
            r6.<init>(r5, r8)
            boolean r5 = r6.exists()
            if (r5 != 0) goto L_0x019f
            goto L_0x01a5
        L_0x019f:
            r5 = 1
            goto L_0x01a7
        L_0x01a1:
            r20 = r8
            r21 = r12
        L_0x01a5:
            r5 = 0
            r6 = 0
        L_0x01a7:
            boolean r8 = android.text.TextUtils.isEmpty(r37)
            if (r8 != 0) goto L_0x01ba
            java.io.File r8 = new java.io.File
            r12 = r37
            r8.<init>(r12)
            boolean r12 = r8.exists()
            if (r12 != 0) goto L_0x01bb
        L_0x01ba:
            r8 = 0
        L_0x01bb:
            if (r8 != 0) goto L_0x01c0
            r12 = r21
            goto L_0x01c1
        L_0x01c0:
            r12 = r8
        L_0x01c1:
            if (r6 != 0) goto L_0x0214
            java.lang.String r1 = org.telegram.messenger.FileLoader.getAttachFileName(r15)
            java.util.HashMap<java.lang.String, org.telegram.messenger.ImageLoader$ThumbGenerateInfo> r2 = r0.waitingForQualityThumb
            java.lang.Object r2 = r2.get(r1)
            org.telegram.messenger.ImageLoader$ThumbGenerateInfo r2 = (org.telegram.messenger.ImageLoader.ThumbGenerateInfo) r2
            if (r2 != 0) goto L_0x01e7
            org.telegram.messenger.ImageLoader$ThumbGenerateInfo r2 = new org.telegram.messenger.ImageLoader$ThumbGenerateInfo
            r4 = 0
            r2.<init>()
            org.telegram.tgnet.TLRPC$Document unused = r2.parentDocument = r15
            r8 = r31
            java.lang.String unused = r2.filter = r8
            boolean unused = r2.big = r3
            java.util.HashMap<java.lang.String, org.telegram.messenger.ImageLoader$ThumbGenerateInfo> r3 = r0.waitingForQualityThumb
            r3.put(r1, r2)
        L_0x01e7:
            java.util.ArrayList r3 = r2.imageReceiverArray
            boolean r3 = r3.contains(r11)
            if (r3 != 0) goto L_0x0203
            java.util.ArrayList r3 = r2.imageReceiverArray
            r3.add(r11)
            java.util.ArrayList r3 = r2.imageReceiverGuidsArray
            java.lang.Integer r4 = java.lang.Integer.valueOf(r30)
            r3.add(r4)
        L_0x0203:
            android.util.SparseArray<java.lang.String> r3 = r0.waitingForQualityThumbByTag
            r3.put(r10, r1)
            boolean r1 = r12.exists()
            if (r1 == 0) goto L_0x0213
            if (r39 == 0) goto L_0x0213
            r0.generateThumb(r7, r12, r2)
        L_0x0213:
            return
        L_0x0214:
            r8 = r31
            r12 = 1
            r10 = r6
            r3 = 1
            r6 = 2
            goto L_0x022b
        L_0x021b:
            r20 = r8
            r12 = 1
            r8 = r31
            r3 = 1
            goto L_0x0228
        L_0x0222:
            r20 = r8
            r12 = 1
            r8 = r31
            r3 = 0
        L_0x0228:
            r5 = 0
            r6 = 2
            r10 = 0
        L_0x022b:
            if (r1 == r6) goto L_0x0672
            boolean r7 = r33.isEncrypted()
            org.telegram.messenger.ImageLoader$CacheImage r13 = new org.telegram.messenger.ImageLoader$CacheImage
            r15 = 0
            r13.<init>()
            r15 = r33
            if (r34 != 0) goto L_0x02a6
            int r12 = r15.imageType
            if (r12 == r6) goto L_0x02a4
            org.telegram.messenger.WebFile r6 = r15.webFile
            boolean r6 = org.telegram.messenger.MessageObject.isGifDocument((org.telegram.messenger.WebFile) r6)
            if (r6 != 0) goto L_0x02a3
            org.telegram.tgnet.TLRPC$Document r6 = r15.document
            boolean r6 = org.telegram.messenger.MessageObject.isGifDocument((org.telegram.tgnet.TLRPC$Document) r6)
            if (r6 != 0) goto L_0x02a3
            org.telegram.tgnet.TLRPC$Document r6 = r15.document
            boolean r6 = org.telegram.messenger.MessageObject.isRoundVideoDocument(r6)
            if (r6 != 0) goto L_0x02a3
            org.telegram.tgnet.TLRPC$Document r6 = r15.document
            boolean r6 = org.telegram.messenger.MessageObject.isVideoSticker(r6)
            if (r6 == 0) goto L_0x0260
            goto L_0x02a3
        L_0x0260:
            java.lang.String r6 = r15.path
            if (r6 == 0) goto L_0x02a6
            java.lang.String r12 = "vthumb"
            boolean r12 = r6.startsWith(r12)
            if (r12 != 0) goto L_0x02a6
            java.lang.String r12 = "thumb"
            boolean r12 = r6.startsWith(r12)
            if (r12 != 0) goto L_0x02a6
            java.lang.String r12 = "jpg"
            java.lang.String r6 = getHttpUrlExtension(r6, r12)
            java.lang.String r12 = "webm"
            boolean r12 = r6.equals(r12)
            if (r12 != 0) goto L_0x029f
            java.lang.String r12 = "mp4"
            boolean r12 = r6.equals(r12)
            if (r12 != 0) goto L_0x029f
            java.lang.String r12 = "gif"
            boolean r6 = r6.equals(r12)
            if (r6 == 0) goto L_0x0293
            goto L_0x029f
        L_0x0293:
            java.lang.String r6 = "tgs"
            boolean r6 = r6.equals(r9)
            if (r6 == 0) goto L_0x02a6
            r6 = 1
            r13.imageType = r6
            goto L_0x02a6
        L_0x029f:
            r6 = 2
            r13.imageType = r6
            goto L_0x02a6
        L_0x02a3:
            r6 = 2
        L_0x02a4:
            r13.imageType = r6
        L_0x02a6:
            r21 = 0
            if (r10 != 0) goto L_0x051d
            org.telegram.tgnet.TLRPC$PhotoSize r6 = r15.photoSize
            boolean r12 = r6 instanceof org.telegram.tgnet.TLRPC$TL_photoStrippedSize
            r28 = r3
            java.lang.String r3 = "g"
            if (r12 != 0) goto L_0x04ea
            boolean r6 = r6 instanceof org.telegram.tgnet.TLRPC$TL_photoPathSize
            if (r6 == 0) goto L_0x02ba
            goto L_0x04ea
        L_0x02ba:
            org.telegram.messenger.SecureDocument r6 = r15.secureDocument
            if (r6 == 0) goto L_0x02de
            r13.secureDocument = r6
            org.telegram.tgnet.TLRPC$TL_secureFile r4 = r6.secureFile
            int r4 = r4.dc_id
            r6 = -2147483648(0xfffffffvar_, float:-0.0)
            if (r4 != r6) goto L_0x02ca
            r4 = 1
            goto L_0x02cb
        L_0x02ca:
            r4 = 0
        L_0x02cb:
            java.io.File r10 = new java.io.File
            java.io.File r6 = org.telegram.messenger.FileLoader.getDirectory(r16)
            r10.<init>(r6, r2)
            r11 = r41
            r12 = r4
            r6 = r8
            r1 = r21
            r8 = 3
            r4 = r3
            goto L_0x04f6
        L_0x02de:
            boolean r6 = r3.equals(r8)
            java.lang.String r10 = ".svg"
            java.lang.String r12 = "application/x-tgwallpattern"
            r37 = r5
            java.lang.String r5 = "application/x-tgsticker"
            java.lang.String r9 = "application/x-tgsdice"
            if (r6 != 0) goto L_0x039d
            boolean r6 = r0.isAnimatedAvatar(r8)
            if (r6 != 0) goto L_0x039d
            r6 = r40
            r11 = r41
            r38 = r3
            if (r11 != 0) goto L_0x030a
            r19 = r4
            r3 = r42
            int r23 = (r3 > r21 ? 1 : (r3 == r21 ? 0 : -1))
            if (r23 <= 0) goto L_0x030a
            java.lang.String r3 = r15.path
            if (r3 != 0) goto L_0x030a
            if (r7 == 0) goto L_0x03a5
        L_0x030a:
            java.io.File r3 = new java.io.File
            java.io.File r4 = org.telegram.messenger.FileLoader.getDirectory(r16)
            r3.<init>(r4, r2)
            boolean r4 = r3.exists()
            if (r4 == 0) goto L_0x031b
            r1 = 1
            goto L_0x033a
        L_0x031b:
            r4 = 2
            if (r11 != r4) goto L_0x0338
            java.io.File r3 = new java.io.File
            java.io.File r4 = org.telegram.messenger.FileLoader.getDirectory(r16)
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            r7.append(r2)
            java.lang.String r1 = ".enc"
            r7.append(r1)
            java.lang.String r1 = r7.toString()
            r3.<init>(r4, r1)
        L_0x0338:
            r1 = r37
        L_0x033a:
            org.telegram.tgnet.TLRPC$Document r4 = r15.document
            if (r4 == 0) goto L_0x0391
            boolean r7 = r4 instanceof org.telegram.messenger.DocumentObject.ThemeDocument
            if (r7 == 0) goto L_0x0351
            org.telegram.messenger.DocumentObject$ThemeDocument r4 = (org.telegram.messenger.DocumentObject.ThemeDocument) r4
            org.telegram.tgnet.TLRPC$Document r4 = r4.wallpaper
            if (r4 != 0) goto L_0x034b
            r4 = 5
            r12 = 1
            goto L_0x034e
        L_0x034b:
            r12 = r28
            r4 = 5
        L_0x034e:
            r13.imageType = r4
            goto L_0x0393
        L_0x0351:
            java.lang.String r4 = r4.mime_type
            boolean r4 = r9.equals(r4)
            if (r4 == 0) goto L_0x0366
            r4 = 1
            r13.imageType = r4
            r4 = r38
            r5 = r1
            r10 = r3
            r6 = r8
            r1 = r21
            r8 = 3
            goto L_0x04f5
        L_0x0366:
            r4 = 1
            org.telegram.tgnet.TLRPC$Document r7 = r15.document
            java.lang.String r7 = r7.mime_type
            boolean r5 = r5.equals(r7)
            if (r5 == 0) goto L_0x0374
            r13.imageType = r4
            goto L_0x0391
        L_0x0374:
            org.telegram.tgnet.TLRPC$Document r4 = r15.document
            java.lang.String r4 = r4.mime_type
            boolean r4 = r12.equals(r4)
            if (r4 == 0) goto L_0x0382
            r4 = 3
            r13.imageType = r4
            goto L_0x0391
        L_0x0382:
            r4 = 3
            org.telegram.tgnet.TLRPC$Document r5 = r15.document
            java.lang.String r5 = org.telegram.messenger.FileLoader.getDocumentFileName(r5)
            boolean r5 = r5.endsWith(r10)
            if (r5 == 0) goto L_0x0391
            r13.imageType = r4
        L_0x0391:
            r12 = r28
        L_0x0393:
            r4 = r38
            r5 = r1
            r10 = r3
            r6 = r8
            r1 = r21
            r8 = 3
            goto L_0x04f6
        L_0x039d:
            r6 = r40
            r11 = r41
            r38 = r3
            r19 = r4
        L_0x03a5:
            org.telegram.tgnet.TLRPC$Document r1 = r15.document
            java.lang.String r3 = ".temp"
            if (r1 == 0) goto L_0x046f
            boolean r4 = r1 instanceof org.telegram.tgnet.TLRPC$TL_documentEncrypted
            if (r4 == 0) goto L_0x03b9
            java.io.File r4 = new java.io.File
            java.io.File r7 = org.telegram.messenger.FileLoader.getDirectory(r16)
            r4.<init>(r7, r2)
            goto L_0x03d4
        L_0x03b9:
            boolean r4 = org.telegram.messenger.MessageObject.isVideoDocument(r1)
            if (r4 == 0) goto L_0x03ca
            java.io.File r4 = new java.io.File
            r7 = 2
            java.io.File r14 = org.telegram.messenger.FileLoader.getDirectory(r7)
            r4.<init>(r14, r2)
            goto L_0x03d4
        L_0x03ca:
            java.io.File r4 = new java.io.File
            r7 = 3
            java.io.File r14 = org.telegram.messenger.FileLoader.getDirectory(r7)
            r4.<init>(r14, r2)
        L_0x03d4:
            boolean r7 = r0.isAnimatedAvatar(r8)
            if (r7 != 0) goto L_0x03e3
            r7 = r38
            boolean r14 = r7.equals(r8)
            if (r14 == 0) goto L_0x0412
            goto L_0x03e5
        L_0x03e3:
            r7 = r38
        L_0x03e5:
            boolean r14 = r4.exists()
            if (r14 != 0) goto L_0x0412
            java.io.File r4 = new java.io.File
            java.io.File r14 = org.telegram.messenger.FileLoader.getDirectory(r16)
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            r38 = r7
            int r7 = r1.dc_id
            r6.append(r7)
            r7 = r19
            r6.append(r7)
            long r7 = r1.id
            r6.append(r7)
            r6.append(r3)
            java.lang.String r3 = r6.toString()
            r4.<init>(r14, r3)
            goto L_0x0414
        L_0x0412:
            r38 = r7
        L_0x0414:
            boolean r3 = r1 instanceof org.telegram.messenger.DocumentObject.ThemeDocument
            if (r3 == 0) goto L_0x0429
            r3 = r1
            org.telegram.messenger.DocumentObject$ThemeDocument r3 = (org.telegram.messenger.DocumentObject.ThemeDocument) r3
            org.telegram.tgnet.TLRPC$Document r3 = r3.wallpaper
            if (r3 != 0) goto L_0x0422
            r3 = 5
            r12 = 1
            goto L_0x0425
        L_0x0422:
            r12 = r28
            r3 = 5
        L_0x0425:
            r13.imageType = r3
            r8 = 3
            goto L_0x0463
        L_0x0429:
            org.telegram.tgnet.TLRPC$Document r3 = r15.document
            java.lang.String r3 = r3.mime_type
            boolean r3 = r9.equals(r3)
            if (r3 == 0) goto L_0x0439
            r3 = 1
            r13.imageType = r3
            r8 = 3
            r12 = 1
            goto L_0x0463
        L_0x0439:
            r3 = 1
            java.lang.String r6 = r1.mime_type
            boolean r5 = r5.equals(r6)
            if (r5 == 0) goto L_0x0446
            r13.imageType = r3
            r8 = 3
            goto L_0x0461
        L_0x0446:
            java.lang.String r3 = r1.mime_type
            boolean r3 = r12.equals(r3)
            if (r3 == 0) goto L_0x0452
            r8 = 3
            r13.imageType = r8
            goto L_0x0461
        L_0x0452:
            r8 = 3
            org.telegram.tgnet.TLRPC$Document r3 = r15.document
            java.lang.String r3 = org.telegram.messenger.FileLoader.getDocumentFileName(r3)
            boolean r3 = r3.endsWith(r10)
            if (r3 == 0) goto L_0x0461
            r13.imageType = r8
        L_0x0461:
            r12 = r28
        L_0x0463:
            long r5 = r1.size
            r10 = r4
            r1 = r5
            r6 = r31
            r5 = r37
            r4 = r38
            goto L_0x04f6
        L_0x046f:
            r7 = r19
            r8 = 3
            org.telegram.messenger.WebFile r1 = r15.webFile
            if (r1 == 0) goto L_0x048b
            java.io.File r10 = new java.io.File
            java.io.File r1 = org.telegram.messenger.FileLoader.getDirectory(r8)
            r10.<init>(r1, r2)
            r12 = r28
            r6 = r31
            r5 = r37
            r4 = r38
        L_0x0487:
            r1 = r21
            goto L_0x04f6
        L_0x048b:
            r1 = 1
            if (r11 != r1) goto L_0x0498
            java.io.File r4 = new java.io.File
            java.io.File r5 = org.telegram.messenger.FileLoader.getDirectory(r16)
            r4.<init>(r5, r2)
            goto L_0x04a1
        L_0x0498:
            java.io.File r4 = new java.io.File
            java.io.File r5 = org.telegram.messenger.FileLoader.getDirectory(r18)
            r4.<init>(r5, r2)
        L_0x04a1:
            r6 = r31
            r10 = r4
            boolean r4 = r0.isAnimatedAvatar(r6)
            if (r4 != 0) goto L_0x04bd
            r4 = r38
            boolean r5 = r4.equals(r6)
            if (r5 == 0) goto L_0x04e5
            org.telegram.tgnet.TLRPC$TL_fileLocationToBeDeprecated r5 = r15.location
            if (r5 == 0) goto L_0x04e5
            boolean r5 = r10.exists()
            if (r5 != 0) goto L_0x04e5
            goto L_0x04bf
        L_0x04bd:
            r4 = r38
        L_0x04bf:
            java.io.File r10 = new java.io.File
            java.io.File r5 = org.telegram.messenger.FileLoader.getDirectory(r16)
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            org.telegram.tgnet.TLRPC$TL_fileLocationToBeDeprecated r12 = r15.location
            long r1 = r12.volume_id
            r9.append(r1)
            r9.append(r7)
            org.telegram.tgnet.TLRPC$TL_fileLocationToBeDeprecated r1 = r15.location
            int r1 = r1.local_id
            r9.append(r1)
            r9.append(r3)
            java.lang.String r1 = r9.toString()
            r10.<init>(r5, r1)
        L_0x04e5:
            r12 = r28
            r5 = r37
            goto L_0x0487
        L_0x04ea:
            r11 = r41
            r4 = r3
            r37 = r5
            r6 = r8
            r8 = 3
            r5 = r37
            r1 = r21
        L_0x04f5:
            r12 = 1
        L_0x04f6:
            boolean r3 = hasAutoplayFilter(r31)
            if (r3 != 0) goto L_0x0502
            boolean r3 = r0.isAnimatedAvatar(r6)
            if (r3 == 0) goto L_0x0514
        L_0x0502:
            r3 = 2
            r13.imageType = r3
            r13.size = r1
            boolean r1 = r4.equals(r6)
            if (r1 != 0) goto L_0x0518
            boolean r1 = r0.isAnimatedAvatar(r6)
            if (r1 == 0) goto L_0x0514
            goto L_0x0518
        L_0x0514:
            r2 = r32
            r1 = r5
            goto L_0x052b
        L_0x0518:
            r2 = r32
            r1 = r5
            r12 = 1
            goto L_0x052b
        L_0x051d:
            r11 = r41
            r28 = r3
            r37 = r5
            r6 = r8
            r8 = 3
            r12 = r28
            r2 = r32
            r1 = r37
        L_0x052b:
            r13.type = r2
            r9 = r27
            r13.key = r9
            r13.filter = r6
            r13.imageLocation = r15
            r7 = r40
            r13.ext = r7
            r14 = r36
            r13.currentAccount = r14
            r5 = r35
            r13.parentObject = r5
            int r3 = r15.imageType
            if (r3 == 0) goto L_0x0547
            r13.imageType = r3
        L_0x0547:
            r3 = 2
            if (r11 != r3) goto L_0x0569
            java.io.File r3 = new java.io.File
            java.io.File r4 = org.telegram.messenger.FileLoader.getInternalCacheDir()
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            r2 = r26
            r8.append(r2)
            java.lang.String r5 = ".enc.key"
            r8.append(r5)
            java.lang.String r5 = r8.toString()
            r3.<init>(r4, r5)
            r13.encryptionKeyPath = r3
            goto L_0x056b
        L_0x0569:
            r2 = r26
        L_0x056b:
            r4 = r42
            r3 = r13
            r4 = r29
            r8 = r35
            r5 = r27
            r6 = r31
            r17 = 3
            r7 = r32
            r14 = r8
            r9 = r20
            r8 = r30
            r3.addImageReceiver(r4, r5, r6, r7, r8)
            if (r12 != 0) goto L_0x064f
            if (r1 != 0) goto L_0x064f
            boolean r1 = r10.exists()
            if (r1 == 0) goto L_0x058e
            goto L_0x064f
        L_0x058e:
            r13.url = r2
            java.util.HashMap<java.lang.String, org.telegram.messenger.ImageLoader$CacheImage> r1 = r0.imageLoadingByUrl
            r1.put(r2, r13)
            java.lang.String r1 = r15.path
            if (r1 == 0) goto L_0x05e9
            java.lang.String r1 = org.telegram.messenger.Utilities.MD5(r1)
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
            r13.finalFilePath = r10
            java.lang.String r1 = r15.path
            boolean r1 = r1.startsWith(r9)
            if (r1 == 0) goto L_0x05d5
            org.telegram.messenger.ImageLoader$ArtworkLoadTask r1 = new org.telegram.messenger.ImageLoader$ArtworkLoadTask
            r1.<init>(r13)
            r13.artworkTask = r1
            java.util.LinkedList<org.telegram.messenger.ImageLoader$ArtworkLoadTask> r2 = r0.artworkTasks
            r2.add(r1)
            r7 = 0
            r0.runArtworkTasks(r7)
            goto L_0x0672
        L_0x05d5:
            r7 = 0
            org.telegram.messenger.ImageLoader$HttpImageTask r1 = new org.telegram.messenger.ImageLoader$HttpImageTask
            r2 = r42
            r1.<init>(r13, r2)
            r13.httpTask = r1
            java.util.LinkedList<org.telegram.messenger.ImageLoader$HttpImageTask> r2 = r0.httpTasks
            r2.add(r1)
            r0.runHttpTasks(r7)
            goto L_0x0672
        L_0x05e9:
            r2 = r42
            r7 = 0
            if (r25 == 0) goto L_0x05f0
            r5 = 3
            goto L_0x05f5
        L_0x05f0:
            int r1 = r29.getFileLoadingPriority()
            r5 = r1
        L_0x05f5:
            org.telegram.tgnet.TLRPC$TL_fileLocationToBeDeprecated r1 = r15.location
            if (r1 == 0) goto L_0x0614
            if (r11 != 0) goto L_0x0605
            int r1 = (r2 > r21 ? 1 : (r2 == r21 ? 0 : -1))
            if (r1 <= 0) goto L_0x0603
            byte[] r1 = r15.key
            if (r1 == 0) goto L_0x0605
        L_0x0603:
            r6 = 1
            goto L_0x0606
        L_0x0605:
            r6 = r11
        L_0x0606:
            org.telegram.messenger.FileLoader r1 = org.telegram.messenger.FileLoader.getInstance(r36)
            r2 = r33
            r3 = r35
            r4 = r40
            r1.loadFile(r2, r3, r4, r5, r6)
            goto L_0x063d
        L_0x0614:
            org.telegram.tgnet.TLRPC$Document r1 = r15.document
            if (r1 == 0) goto L_0x0622
            org.telegram.messenger.FileLoader r1 = org.telegram.messenger.FileLoader.getInstance(r36)
            org.telegram.tgnet.TLRPC$Document r2 = r15.document
            r1.loadFile(r2, r14, r5, r11)
            goto L_0x063d
        L_0x0622:
            org.telegram.messenger.SecureDocument r1 = r15.secureDocument
            if (r1 == 0) goto L_0x0630
            org.telegram.messenger.FileLoader r1 = org.telegram.messenger.FileLoader.getInstance(r36)
            org.telegram.messenger.SecureDocument r2 = r15.secureDocument
            r1.loadFile(r2, r5)
            goto L_0x063d
        L_0x0630:
            org.telegram.messenger.WebFile r1 = r15.webFile
            if (r1 == 0) goto L_0x063d
            org.telegram.messenger.FileLoader r1 = org.telegram.messenger.FileLoader.getInstance(r36)
            org.telegram.messenger.WebFile r2 = r15.webFile
            r1.loadFile(r2, r5, r11)
        L_0x063d:
            boolean r1 = r29.isForceLoding()
            if (r1 == 0) goto L_0x0672
            java.util.HashMap<java.lang.String, java.lang.Integer> r1 = r0.forceLoadingImages
            java.lang.String r2 = r13.key
            java.lang.Integer r3 = java.lang.Integer.valueOf(r7)
            r1.put(r2, r3)
            goto L_0x0672
        L_0x064f:
            r13.finalFilePath = r10
            r13.imageLocation = r15
            org.telegram.messenger.ImageLoader$CacheOutTask r1 = new org.telegram.messenger.ImageLoader$CacheOutTask
            r1.<init>(r13)
            r13.cacheTask = r1
            java.util.HashMap<java.lang.String, org.telegram.messenger.ImageLoader$CacheImage> r1 = r0.imageLoadingByKeys
            r2 = r27
            r1.put(r2, r13)
            if (r25 == 0) goto L_0x066b
            org.telegram.messenger.DispatchQueue r1 = r0.cacheThumbOutQueue
            org.telegram.messenger.ImageLoader$CacheOutTask r2 = r13.cacheTask
            r1.postRunnable(r2)
            goto L_0x0672
        L_0x066b:
            org.telegram.messenger.DispatchQueue r1 = r0.cacheOutQueue
            org.telegram.messenger.ImageLoader$CacheOutTask r2 = r13.cacheTask
            r1.postRunnable(r2)
        L_0x0672:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ImageLoader.lambda$createLoadOperationForImageReceiver$6(int, java.lang.String, java.lang.String, int, org.telegram.messenger.ImageReceiver, int, java.lang.String, int, org.telegram.messenger.ImageLocation, boolean, java.lang.Object, int, org.telegram.tgnet.TLRPC$Document, boolean, boolean, java.lang.String, int, long):void");
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

    /* JADX WARNING: Code restructure failed: missing block: B:155:0x0286, code lost:
        if (r6.local_id < 0) goto L_0x028b;
     */
    /* JADX WARNING: Removed duplicated region for block: B:105:0x01b3  */
    /* JADX WARNING: Removed duplicated region for block: B:106:0x01b6  */
    /* JADX WARNING: Removed duplicated region for block: B:113:0x01c8  */
    /* JADX WARNING: Removed duplicated region for block: B:115:0x01cb  */
    /* JADX WARNING: Removed duplicated region for block: B:116:0x01ce  */
    /* JADX WARNING: Removed duplicated region for block: B:118:0x01d2  */
    /* JADX WARNING: Removed duplicated region for block: B:122:0x01e7  */
    /* JADX WARNING: Removed duplicated region for block: B:204:0x03a5  */
    /* JADX WARNING: Removed duplicated region for block: B:221:0x0410  */
    /* JADX WARNING: Removed duplicated region for block: B:224:0x0418 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:228:0x0433 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:232:0x044e A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:237:0x046f A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:242:0x048d A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:246:0x04a7  */
    /* JADX WARNING: Removed duplicated region for block: B:254:0x04e5  */
    /* JADX WARNING: Removed duplicated region for block: B:256:0x04ea  */
    /* JADX WARNING: Removed duplicated region for block: B:271:0x0554  */
    /* JADX WARNING: Removed duplicated region for block: B:42:0x00ab  */
    /* JADX WARNING: Removed duplicated region for block: B:43:0x00b0  */
    /* JADX WARNING: Removed duplicated region for block: B:45:0x00b3  */
    /* JADX WARNING: Removed duplicated region for block: B:57:0x00e6  */
    /* JADX WARNING: Removed duplicated region for block: B:65:0x0107  */
    /* JADX WARNING: Removed duplicated region for block: B:96:0x0194  */
    /* JADX WARNING: Removed duplicated region for block: B:98:0x01a1  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void loadImageForImageReceiver(org.telegram.messenger.ImageReceiver r38) {
        /*
            r37 = this;
            r13 = r37
            r14 = r38
            if (r14 != 0) goto L_0x0007
            return
        L_0x0007:
            java.lang.String r6 = r38.getMediaKey()
            int r15 = r38.getNewGuid()
            r7 = 0
            r8 = 1
            if (r6 == 0) goto L_0x0096
            org.telegram.messenger.ImageLocation r0 = r38.getMediaLocation()
            boolean r0 = r13.useLottieMemCache(r0, r6)
            if (r0 == 0) goto L_0x0023
            android.graphics.drawable.BitmapDrawable r0 = r13.getFromLottieCache(r6)
        L_0x0021:
            r1 = r0
            goto L_0x0055
        L_0x0023:
            org.telegram.messenger.LruCache<android.graphics.drawable.BitmapDrawable> r0 = r13.memCache
            java.lang.Object r0 = r0.get(r6)
            android.graphics.drawable.Drawable r0 = (android.graphics.drawable.Drawable) r0
            if (r0 == 0) goto L_0x0032
            org.telegram.messenger.LruCache<android.graphics.drawable.BitmapDrawable> r1 = r13.memCache
            r1.moveToFront(r6)
        L_0x0032:
            if (r0 != 0) goto L_0x0043
            org.telegram.messenger.LruCache<android.graphics.drawable.BitmapDrawable> r0 = r13.smallImagesMemCache
            java.lang.Object r0 = r0.get(r6)
            android.graphics.drawable.Drawable r0 = (android.graphics.drawable.Drawable) r0
            if (r0 == 0) goto L_0x0043
            org.telegram.messenger.LruCache<android.graphics.drawable.BitmapDrawable> r1 = r13.smallImagesMemCache
            r1.moveToFront(r6)
        L_0x0043:
            if (r0 != 0) goto L_0x0021
            org.telegram.messenger.LruCache<android.graphics.drawable.BitmapDrawable> r0 = r13.wallpaperMemCache
            java.lang.Object r0 = r0.get(r6)
            android.graphics.drawable.Drawable r0 = (android.graphics.drawable.Drawable) r0
            if (r0 == 0) goto L_0x0021
            org.telegram.messenger.LruCache<android.graphics.drawable.BitmapDrawable> r1 = r13.wallpaperMemCache
            r1.moveToFront(r6)
            goto L_0x0021
        L_0x0055:
            boolean r0 = r1 instanceof org.telegram.ui.Components.RLottieDrawable
            if (r0 == 0) goto L_0x0061
            r0 = r1
            org.telegram.ui.Components.RLottieDrawable r0 = (org.telegram.ui.Components.RLottieDrawable) r0
            boolean r0 = r0.hasBitmap()
            goto L_0x006e
        L_0x0061:
            boolean r0 = r1 instanceof org.telegram.ui.Components.AnimatedFileDrawable
            if (r0 == 0) goto L_0x006d
            r0 = r1
            org.telegram.ui.Components.AnimatedFileDrawable r0 = (org.telegram.ui.Components.AnimatedFileDrawable) r0
            boolean r0 = r0.hasBitmap()
            goto L_0x006e
        L_0x006d:
            r0 = 1
        L_0x006e:
            if (r0 == 0) goto L_0x0087
            if (r1 == 0) goto L_0x0087
            r13.cancelLoadingForImageReceiver(r14, r8)
            r3 = 3
            r4 = 1
            r0 = r38
            r2 = r6
            r5 = r15
            r0.setImageBitmapByKey(r1, r2, r3, r4, r5)
            boolean r0 = r38.isForcePreview()
            if (r0 != 0) goto L_0x0085
            return
        L_0x0085:
            r0 = 1
            goto L_0x0097
        L_0x0087:
            if (r1 == 0) goto L_0x0096
            r3 = 3
            r4 = 1
            r0 = r38
            r2 = r6
            r5 = r15
            r0.setImageBitmapByKey(r1, r2, r3, r4, r5)
            r0 = 0
            r16 = 1
            goto L_0x0099
        L_0x0096:
            r0 = 0
        L_0x0097:
            r16 = 0
        L_0x0099:
            java.lang.String r2 = r38.getImageKey()
            if (r0 != 0) goto L_0x00ff
            if (r2 == 0) goto L_0x00ff
            org.telegram.messenger.ImageLocation r1 = r38.getImageLocation()
            boolean r1 = r13.useLottieMemCache(r1, r2)
            if (r1 == 0) goto L_0x00b0
            android.graphics.drawable.BitmapDrawable r1 = r13.getFromLottieCache(r2)
            goto L_0x00b1
        L_0x00b0:
            r1 = 0
        L_0x00b1:
            if (r1 != 0) goto L_0x00e4
            org.telegram.messenger.LruCache<android.graphics.drawable.BitmapDrawable> r1 = r13.memCache
            java.lang.Object r1 = r1.get(r2)
            android.graphics.drawable.Drawable r1 = (android.graphics.drawable.Drawable) r1
            if (r1 == 0) goto L_0x00c2
            org.telegram.messenger.LruCache<android.graphics.drawable.BitmapDrawable> r3 = r13.memCache
            r3.moveToFront(r2)
        L_0x00c2:
            if (r1 != 0) goto L_0x00d3
            org.telegram.messenger.LruCache<android.graphics.drawable.BitmapDrawable> r1 = r13.smallImagesMemCache
            java.lang.Object r1 = r1.get(r2)
            android.graphics.drawable.Drawable r1 = (android.graphics.drawable.Drawable) r1
            if (r1 == 0) goto L_0x00d3
            org.telegram.messenger.LruCache<android.graphics.drawable.BitmapDrawable> r3 = r13.smallImagesMemCache
            r3.moveToFront(r2)
        L_0x00d3:
            if (r1 != 0) goto L_0x00e4
            org.telegram.messenger.LruCache<android.graphics.drawable.BitmapDrawable> r1 = r13.wallpaperMemCache
            java.lang.Object r1 = r1.get(r2)
            android.graphics.drawable.Drawable r1 = (android.graphics.drawable.Drawable) r1
            if (r1 == 0) goto L_0x00e4
            org.telegram.messenger.LruCache<android.graphics.drawable.BitmapDrawable> r3 = r13.wallpaperMemCache
            r3.moveToFront(r2)
        L_0x00e4:
            if (r1 == 0) goto L_0x00ff
            r13.cancelLoadingForImageReceiver(r14, r8)
            r3 = 0
            r4 = 1
            r0 = r38
            r5 = r15
            r0.setImageBitmapByKey(r1, r2, r3, r4, r5)
            boolean r0 = r38.isForcePreview()
            if (r0 != 0) goto L_0x00fc
            if (r6 == 0) goto L_0x00fb
            if (r16 == 0) goto L_0x00fc
        L_0x00fb:
            return
        L_0x00fc:
            r17 = 1
            goto L_0x0101
        L_0x00ff:
            r17 = r0
        L_0x0101:
            java.lang.String r2 = r38.getThumbKey()
            if (r2 == 0) goto L_0x0161
            org.telegram.messenger.ImageLocation r0 = r38.getThumbLocation()
            boolean r0 = r13.useLottieMemCache(r0, r2)
            if (r0 == 0) goto L_0x0117
            android.graphics.drawable.BitmapDrawable r0 = r13.getFromLottieCache(r2)
        L_0x0115:
            r1 = r0
            goto L_0x0149
        L_0x0117:
            org.telegram.messenger.LruCache<android.graphics.drawable.BitmapDrawable> r0 = r13.memCache
            java.lang.Object r0 = r0.get(r2)
            android.graphics.drawable.Drawable r0 = (android.graphics.drawable.Drawable) r0
            if (r0 == 0) goto L_0x0126
            org.telegram.messenger.LruCache<android.graphics.drawable.BitmapDrawable> r1 = r13.memCache
            r1.moveToFront(r2)
        L_0x0126:
            if (r0 != 0) goto L_0x0137
            org.telegram.messenger.LruCache<android.graphics.drawable.BitmapDrawable> r0 = r13.smallImagesMemCache
            java.lang.Object r0 = r0.get(r2)
            android.graphics.drawable.Drawable r0 = (android.graphics.drawable.Drawable) r0
            if (r0 == 0) goto L_0x0137
            org.telegram.messenger.LruCache<android.graphics.drawable.BitmapDrawable> r1 = r13.smallImagesMemCache
            r1.moveToFront(r2)
        L_0x0137:
            if (r0 != 0) goto L_0x0115
            org.telegram.messenger.LruCache<android.graphics.drawable.BitmapDrawable> r0 = r13.wallpaperMemCache
            java.lang.Object r0 = r0.get(r2)
            android.graphics.drawable.Drawable r0 = (android.graphics.drawable.Drawable) r0
            if (r0 == 0) goto L_0x0115
            org.telegram.messenger.LruCache<android.graphics.drawable.BitmapDrawable> r1 = r13.wallpaperMemCache
            r1.moveToFront(r2)
            goto L_0x0115
        L_0x0149:
            if (r1 == 0) goto L_0x0161
            r3 = 1
            r4 = 1
            r0 = r38
            r5 = r15
            r0.setImageBitmapByKey(r1, r2, r3, r4, r5)
            r13.cancelLoadingForImageReceiver(r14, r7)
            if (r17 == 0) goto L_0x015f
            boolean r0 = r38.isForcePreview()
            if (r0 == 0) goto L_0x015f
            return
        L_0x015f:
            r0 = 1
            goto L_0x0162
        L_0x0161:
            r0 = 0
        L_0x0162:
            java.lang.Object r1 = r38.getParentObject()
            org.telegram.tgnet.TLRPC$Document r2 = r38.getQualityThumbDocument()
            org.telegram.messenger.ImageLocation r5 = r38.getThumbLocation()
            java.lang.String r6 = r38.getThumbFilter()
            org.telegram.messenger.ImageLocation r3 = r38.getMediaLocation()
            java.lang.String r12 = r38.getMediaFilter()
            org.telegram.messenger.ImageLocation r4 = r38.getImageLocation()
            java.lang.String r11 = r38.getImageFilter()
            if (r4 != 0) goto L_0x01a8
            boolean r10 = r38.isNeedsQualityThumb()
            if (r10 == 0) goto L_0x01a8
            boolean r10 = r38.isCurrentKeyQuality()
            if (r10 == 0) goto L_0x01a8
            boolean r10 = r1 instanceof org.telegram.messenger.MessageObject
            if (r10 == 0) goto L_0x01a1
            r2 = r1
            org.telegram.messenger.MessageObject r2 = (org.telegram.messenger.MessageObject) r2
            org.telegram.tgnet.TLRPC$Document r2 = r2.getDocument()
            org.telegram.messenger.ImageLocation r2 = org.telegram.messenger.ImageLocation.getForDocument(r2)
        L_0x019f:
            r10 = 1
            goto L_0x01aa
        L_0x01a1:
            if (r2 == 0) goto L_0x01a8
            org.telegram.messenger.ImageLocation r2 = org.telegram.messenger.ImageLocation.getForDocument(r2)
            goto L_0x019f
        L_0x01a8:
            r2 = r4
            r10 = 0
        L_0x01aa:
            java.lang.String r18 = "mp4"
            r9 = 2
            if (r2 == 0) goto L_0x01b6
            int r8 = r2.imageType
            if (r8 != r9) goto L_0x01b6
            r8 = r18
            goto L_0x01b7
        L_0x01b6:
            r8 = 0
        L_0x01b7:
            if (r3 == 0) goto L_0x01be
            int r7 = r3.imageType
            if (r7 != r9) goto L_0x01be
            goto L_0x01c0
        L_0x01be:
            r18 = 0
        L_0x01c0:
            java.lang.String r7 = r38.getExt()
            java.lang.String r9 = "jpg"
            if (r7 != 0) goto L_0x01c9
            r7 = r9
        L_0x01c9:
            if (r8 != 0) goto L_0x01ce
            r23 = r7
            goto L_0x01d0
        L_0x01ce:
            r23 = r8
        L_0x01d0:
            if (r18 != 0) goto L_0x01d4
            r18 = r7
        L_0x01d4:
            r8 = r2
            r26 = r3
            r27 = r4
            r2 = 0
            r3 = 0
            r4 = 0
            r24 = 0
            r25 = 0
            r28 = 0
        L_0x01e2:
            java.lang.String r13 = "."
            r14 = 2
            if (r2 >= r14) goto L_0x0397
            if (r2 != 0) goto L_0x01ef
            r14 = r8
            r29 = r15
            r15 = r23
            goto L_0x01f5
        L_0x01ef:
            r29 = r15
            r15 = r18
            r14 = r26
        L_0x01f5:
            if (r14 != 0) goto L_0x01fc
            r30 = r0
            r31 = r8
            goto L_0x020f
        L_0x01fc:
            r30 = r0
            if (r26 == 0) goto L_0x0205
            r31 = r8
            r0 = r26
            goto L_0x0208
        L_0x0205:
            r0 = r8
            r31 = r0
        L_0x0208:
            r8 = 0
            java.lang.String r0 = r14.getKey(r1, r0, r8)
            if (r0 != 0) goto L_0x0219
        L_0x020f:
            r32 = r6
            r33 = r11
            r34 = r12
        L_0x0215:
            r8 = r31
            goto L_0x0387
        L_0x0219:
            r32 = r6
            if (r26 == 0) goto L_0x0220
            r8 = r26
            goto L_0x0222
        L_0x0220:
            r8 = r31
        L_0x0222:
            r6 = 1
            java.lang.String r8 = r14.getKey(r1, r8, r6)
            java.lang.String r6 = r14.path
            if (r6 == 0) goto L_0x0249
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            r6.append(r8)
            r6.append(r13)
            java.lang.String r8 = r14.path
            java.lang.String r8 = getHttpUrlExtension(r8, r9)
            r6.append(r8)
            java.lang.String r8 = r6.toString()
            r33 = r11
            r34 = r12
            goto L_0x036e
        L_0x0249:
            org.telegram.tgnet.TLRPC$PhotoSize r6 = r14.photoSize
            r33 = r11
            boolean r11 = r6 instanceof org.telegram.tgnet.TLRPC$TL_photoStrippedSize
            if (r11 != 0) goto L_0x035a
            boolean r6 = r6 instanceof org.telegram.tgnet.TLRPC$TL_photoPathSize
            if (r6 == 0) goto L_0x0257
            goto L_0x035a
        L_0x0257:
            org.telegram.tgnet.TLRPC$TL_fileLocationToBeDeprecated r6 = r14.location
            if (r6 == 0) goto L_0x028f
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            r6.append(r8)
            r6.append(r13)
            r6.append(r15)
            java.lang.String r8 = r6.toString()
            java.lang.String r6 = r38.getExt()
            if (r6 != 0) goto L_0x0289
            org.telegram.tgnet.TLRPC$TL_fileLocationToBeDeprecated r6 = r14.location
            byte[] r11 = r6.key
            if (r11 != 0) goto L_0x0289
            r34 = r12
            long r11 = r6.volume_id
            r35 = -2147483648(0xfffffffvar_, double:NaN)
            int r13 = (r11 > r35 ? 1 : (r11 == r35 ? 0 : -1))
            if (r13 != 0) goto L_0x036e
            int r6 = r6.local_id
            if (r6 >= 0) goto L_0x036e
            goto L_0x028b
        L_0x0289:
            r34 = r12
        L_0x028b:
            r28 = 1
            goto L_0x036e
        L_0x028f:
            r34 = r12
            org.telegram.messenger.WebFile r6 = r14.webFile
            if (r6 == 0) goto L_0x02b7
            java.lang.String r6 = r6.mime_type
            java.lang.String r6 = org.telegram.messenger.FileLoader.getMimeTypePart(r6)
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            r11.append(r8)
            r11.append(r13)
            org.telegram.messenger.WebFile r8 = r14.webFile
            java.lang.String r8 = r8.url
            java.lang.String r6 = getHttpUrlExtension(r8, r6)
            r11.append(r6)
            java.lang.String r8 = r11.toString()
            goto L_0x036e
        L_0x02b7:
            org.telegram.messenger.SecureDocument r6 = r14.secureDocument
            if (r6 == 0) goto L_0x02cf
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            r6.append(r8)
            r6.append(r13)
            r6.append(r15)
            java.lang.String r8 = r6.toString()
            goto L_0x036e
        L_0x02cf:
            org.telegram.tgnet.TLRPC$Document r6 = r14.document
            if (r6 == 0) goto L_0x036e
            if (r2 != 0) goto L_0x02e8
            if (r10 == 0) goto L_0x02e8
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            java.lang.String r11 = "q_"
            r6.append(r11)
            r6.append(r0)
            java.lang.String r0 = r6.toString()
        L_0x02e8:
            org.telegram.tgnet.TLRPC$Document r6 = r14.document
            java.lang.String r6 = org.telegram.messenger.FileLoader.getDocumentFileName(r6)
            r11 = 46
            int r11 = r6.lastIndexOf(r11)
            r12 = -1
            java.lang.String r13 = ""
            if (r11 != r12) goto L_0x02fb
            r6 = r13
            goto L_0x02ff
        L_0x02fb:
            java.lang.String r6 = r6.substring(r11)
        L_0x02ff:
            int r11 = r6.length()
            r12 = 1
            if (r11 > r12) goto L_0x0324
            org.telegram.tgnet.TLRPC$Document r6 = r14.document
            java.lang.String r6 = r6.mime_type
            java.lang.String r11 = "video/mp4"
            boolean r6 = r11.equals(r6)
            if (r6 == 0) goto L_0x0315
            java.lang.String r13 = ".mp4"
            goto L_0x0325
        L_0x0315:
            org.telegram.tgnet.TLRPC$Document r6 = r14.document
            java.lang.String r6 = r6.mime_type
            java.lang.String r11 = "video/x-matroska"
            boolean r6 = r11.equals(r6)
            if (r6 == 0) goto L_0x0325
            java.lang.String r13 = ".mkv"
            goto L_0x0325
        L_0x0324:
            r13 = r6
        L_0x0325:
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            r6.append(r8)
            r6.append(r13)
            java.lang.String r8 = r6.toString()
            org.telegram.tgnet.TLRPC$Document r6 = r14.document
            boolean r6 = org.telegram.messenger.MessageObject.isVideoDocument(r6)
            if (r6 != 0) goto L_0x0356
            org.telegram.tgnet.TLRPC$Document r6 = r14.document
            boolean r6 = org.telegram.messenger.MessageObject.isGifDocument((org.telegram.tgnet.TLRPC$Document) r6)
            if (r6 != 0) goto L_0x0356
            org.telegram.tgnet.TLRPC$Document r6 = r14.document
            boolean r6 = org.telegram.messenger.MessageObject.isRoundVideoDocument(r6)
            if (r6 != 0) goto L_0x0356
            org.telegram.tgnet.TLRPC$Document r6 = r14.document
            boolean r6 = org.telegram.messenger.MessageObject.canPreviewDocument(r6)
            if (r6 != 0) goto L_0x0356
            r6 = 1
            goto L_0x0357
        L_0x0356:
            r6 = 0
        L_0x0357:
            r28 = r6
            goto L_0x036e
        L_0x035a:
            r34 = r12
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            r6.append(r8)
            r6.append(r13)
            r6.append(r15)
            java.lang.String r8 = r6.toString()
        L_0x036e:
            if (r2 != 0) goto L_0x0374
            r4 = r0
            r24 = r8
            goto L_0x0377
        L_0x0374:
            r3 = r0
            r25 = r8
        L_0x0377:
            if (r14 != r5) goto L_0x0215
            if (r2 != 0) goto L_0x0380
            r4 = 0
            r8 = 0
            r24 = 0
            goto L_0x0387
        L_0x0380:
            r8 = r31
            r3 = 0
            r25 = 0
            r26 = 0
        L_0x0387:
            int r2 = r2 + 1
            r14 = r38
            r15 = r29
            r0 = r30
            r6 = r32
            r11 = r33
            r12 = r34
            goto L_0x01e2
        L_0x0397:
            r30 = r0
            r32 = r6
            r31 = r8
            r33 = r11
            r34 = r12
            r29 = r15
            if (r5 == 0) goto L_0x0410
            org.telegram.messenger.ImageLocation r0 = r38.getStrippedLocation()
            if (r0 != 0) goto L_0x03b1
            if (r26 == 0) goto L_0x03af
            r27 = r26
        L_0x03af:
            r0 = r27
        L_0x03b1:
            r2 = 0
            java.lang.String r2 = r5.getKey(r1, r0, r2)
            r6 = 1
            java.lang.String r0 = r5.getKey(r1, r0, r6)
            java.lang.String r1 = r5.path
            if (r1 == 0) goto L_0x03db
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r0)
            r1.append(r13)
            java.lang.String r0 = r5.path
            java.lang.String r0 = getHttpUrlExtension(r0, r9)
            r1.append(r0)
            java.lang.String r0 = r1.toString()
        L_0x03d7:
            r19 = r0
            r9 = r2
            goto L_0x0414
        L_0x03db:
            org.telegram.tgnet.TLRPC$PhotoSize r1 = r5.photoSize
            boolean r8 = r1 instanceof org.telegram.tgnet.TLRPC$TL_photoStrippedSize
            if (r8 != 0) goto L_0x03fd
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC$TL_photoPathSize
            if (r1 == 0) goto L_0x03e6
            goto L_0x03fd
        L_0x03e6:
            org.telegram.tgnet.TLRPC$TL_fileLocationToBeDeprecated r1 = r5.location
            if (r1 == 0) goto L_0x03d7
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r0)
            r1.append(r13)
            r1.append(r7)
            java.lang.String r0 = r1.toString()
            goto L_0x03d7
        L_0x03fd:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r0)
            r1.append(r13)
            r1.append(r7)
            java.lang.String r0 = r1.toString()
            goto L_0x03d7
        L_0x0410:
            r6 = 1
            r9 = 0
            r19 = 0
        L_0x0414:
            java.lang.String r0 = "@"
            if (r3 == 0) goto L_0x042f
            if (r34 == 0) goto L_0x042f
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r3)
            r1.append(r0)
            r12 = r34
            r1.append(r12)
            java.lang.String r3 = r1.toString()
            goto L_0x0431
        L_0x042f:
            r12 = r34
        L_0x0431:
            if (r4 == 0) goto L_0x044a
            if (r33 == 0) goto L_0x044a
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r4)
            r1.append(r0)
            r11 = r33
            r1.append(r11)
            java.lang.String r4 = r1.toString()
            goto L_0x044c
        L_0x044a:
            r11 = r33
        L_0x044c:
            if (r9 == 0) goto L_0x0466
            if (r32 == 0) goto L_0x0466
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r9)
            r1.append(r0)
            r8 = r32
            r1.append(r8)
            java.lang.String r0 = r1.toString()
            r2 = r0
            goto L_0x0469
        L_0x0466:
            r8 = r32
            r2 = r9
        L_0x0469:
            java.lang.String r0 = r38.getUniqKeyPrefix()
            if (r0 == 0) goto L_0x0486
            if (r4 == 0) goto L_0x0486
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = r38.getUniqKeyPrefix()
            r0.append(r1)
            r0.append(r4)
            java.lang.String r0 = r0.toString()
            r13 = r0
            goto L_0x0487
        L_0x0486:
            r13 = r4
        L_0x0487:
            java.lang.String r0 = r38.getUniqKeyPrefix()
            if (r0 == 0) goto L_0x04a4
            if (r3 == 0) goto L_0x04a4
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = r38.getUniqKeyPrefix()
            r0.append(r1)
            r0.append(r3)
            java.lang.String r0 = r0.toString()
            r14 = r0
            goto L_0x04a5
        L_0x04a4:
            r14 = r3
        L_0x04a5:
            if (r31 == 0) goto L_0x04e5
            r9 = r31
            java.lang.String r0 = r9.path
            if (r0 == 0) goto L_0x04e1
            r14 = 0
            r10 = 1
            r12 = 1
            if (r30 == 0) goto L_0x04b6
            r22 = 2
            goto L_0x04b8
        L_0x04b6:
            r22 = 1
        L_0x04b8:
            r0 = r37
            r1 = r38
            r3 = r19
            r4 = r7
            r6 = r8
            r20 = r9
            r7 = r14
            r9 = r10
            r10 = r12
            r15 = r11
            r11 = r22
            r12 = r29
            r0.createLoadOperationForImageReceiver(r1, r2, r3, r4, r5, r6, r7, r9, r10, r11, r12)
            long r7 = r38.getSize()
            r9 = 1
            r10 = 0
            r11 = 0
            r2 = r13
            r3 = r24
            r4 = r23
            r5 = r20
            r6 = r15
            r0.createLoadOperationForImageReceiver(r1, r2, r3, r4, r5, r6, r7, r9, r10, r11, r12)
            goto L_0x0591
        L_0x04e1:
            r20 = r9
            r15 = r11
            goto L_0x04e8
        L_0x04e5:
            r15 = r11
            r20 = r31
        L_0x04e8:
            if (r26 == 0) goto L_0x0554
            int r0 = r38.getCacheType()
            r21 = 1
            if (r0 != 0) goto L_0x04f7
            if (r28 == 0) goto L_0x04f7
            r22 = 1
            goto L_0x04f9
        L_0x04f7:
            r22 = r0
        L_0x04f9:
            if (r22 != 0) goto L_0x04fd
            r9 = 1
            goto L_0x04ff
        L_0x04fd:
            r9 = r22
        L_0x04ff:
            if (r30 != 0) goto L_0x051c
            r10 = 0
            r27 = 1
            r28 = 1
            r0 = r37
            r1 = r38
            r3 = r19
            r4 = r7
            r6 = r8
            r7 = r10
            r10 = r27
            r11 = r28
            r19 = r12
            r12 = r29
            r0.createLoadOperationForImageReceiver(r1, r2, r3, r4, r5, r6, r7, r9, r10, r11, r12)
            goto L_0x051e
        L_0x051c:
            r19 = r12
        L_0x051e:
            if (r17 != 0) goto L_0x0537
            r7 = 0
            r10 = 0
            r11 = 0
            r0 = r37
            r1 = r38
            r2 = r13
            r3 = r24
            r4 = r23
            r5 = r20
            r6 = r15
            r9 = r21
            r12 = r29
            r0.createLoadOperationForImageReceiver(r1, r2, r3, r4, r5, r6, r7, r9, r10, r11, r12)
        L_0x0537:
            if (r16 != 0) goto L_0x0591
            long r7 = r38.getSize()
            r10 = 3
            r11 = 0
            r0 = r37
            r1 = r38
            r2 = r14
            r3 = r25
            r4 = r18
            r5 = r26
            r6 = r19
            r9 = r22
            r12 = r29
            r0.createLoadOperationForImageReceiver(r1, r2, r3, r4, r5, r6, r7, r9, r10, r11, r12)
            goto L_0x0591
        L_0x0554:
            int r0 = r38.getCacheType()
            if (r0 != 0) goto L_0x055e
            if (r28 == 0) goto L_0x055e
            r14 = 1
            goto L_0x055f
        L_0x055e:
            r14 = r0
        L_0x055f:
            if (r14 != 0) goto L_0x0563
            r9 = 1
            goto L_0x0564
        L_0x0563:
            r9 = r14
        L_0x0564:
            r10 = 0
            r12 = 1
            if (r30 == 0) goto L_0x056c
            r22 = 2
            goto L_0x056e
        L_0x056c:
            r22 = 1
        L_0x056e:
            r0 = r37
            r1 = r38
            r3 = r19
            r4 = r7
            r6 = r8
            r7 = r10
            r10 = r12
            r11 = r22
            r12 = r29
            r0.createLoadOperationForImageReceiver(r1, r2, r3, r4, r5, r6, r7, r9, r10, r11, r12)
            long r7 = r38.getSize()
            r10 = 0
            r11 = 0
            r2 = r13
            r3 = r24
            r4 = r23
            r5 = r20
            r6 = r15
            r9 = r14
            r0.createLoadOperationForImageReceiver(r1, r2, r3, r4, r5, r6, r7, r9, r10, r11, r12)
        L_0x0591:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ImageLoader.loadImageForImageReceiver(org.telegram.messenger.ImageReceiver):void");
    }

    /* access modifiers changed from: private */
    public BitmapDrawable getFromLottieCache(String str) {
        BitmapDrawable bitmapDrawable = this.lottieMemCache.get(str);
        if (!(bitmapDrawable instanceof AnimatedFileDrawable) || !((AnimatedFileDrawable) bitmapDrawable).isRecycled()) {
            return bitmapDrawable;
        }
        this.lottieMemCache.remove(str);
        return null;
    }

    private boolean useLottieMemCache(ImageLocation imageLocation, String str) {
        return (imageLocation != null && (MessageObject.isAnimatedStickerDocument(imageLocation.document, true) || imageLocation.imageType == 1 || MessageObject.isVideoSticker(imageLocation.document))) || isAnimatedAvatar(str);
    }

    public boolean hasLottieMemCache(String str) {
        LruCache<BitmapDrawable> lruCache = this.lottieMemCache;
        return lruCache != null && lruCache.contains(str);
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
                    RandomAccessFile randomAccessFile = new RandomAccessFile(FileLoader.getInstance(UserConfig.selectedAccount).getPathToAttach(tLRPC$PhotoSize, true), "r");
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

    /* JADX WARNING: Removed duplicated region for block: B:29:0x009a  */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x009f  */
    /* JADX WARNING: Removed duplicated region for block: B:36:0x00bd  */
    /* JADX WARNING: Removed duplicated region for block: B:39:0x00cd  */
    /* JADX WARNING: Removed duplicated region for block: B:41:0x00e3  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static org.telegram.tgnet.TLRPC$PhotoSize scaleAndSaveImageInternal(org.telegram.tgnet.TLRPC$PhotoSize r2, android.graphics.Bitmap r3, android.graphics.Bitmap.CompressFormat r4, boolean r5, int r6, int r7, float r8, float r9, float r10, int r11, boolean r12, boolean r13, boolean r14) throws java.lang.Exception {
        /*
            r5 = 1065353216(0x3var_, float:1.0)
            int r5 = (r10 > r5 ? 1 : (r10 == r5 ? 0 : -1))
            if (r5 > 0) goto L_0x000b
            if (r13 == 0) goto L_0x0009
            goto L_0x000b
        L_0x0009:
            r5 = r3
            goto L_0x0010
        L_0x000b:
            r5 = 1
            android.graphics.Bitmap r5 = org.telegram.messenger.Bitmaps.createScaledBitmap(r3, r6, r7, r5)
        L_0x0010:
            r6 = 0
            r7 = -2147483648(0xfffffffvar_, double:NaN)
            if (r2 == 0) goto L_0x0020
            org.telegram.tgnet.TLRPC$FileLocation r9 = r2.location
            boolean r10 = r9 instanceof org.telegram.tgnet.TLRPC$TL_fileLocationToBeDeprecated
            if (r10 != 0) goto L_0x001d
            goto L_0x0020
        L_0x001d:
            org.telegram.tgnet.TLRPC$TL_fileLocationToBeDeprecated r9 = (org.telegram.tgnet.TLRPC$TL_fileLocationToBeDeprecated) r9
            goto L_0x007a
        L_0x0020:
            org.telegram.tgnet.TLRPC$TL_fileLocationToBeDeprecated r9 = new org.telegram.tgnet.TLRPC$TL_fileLocationToBeDeprecated
            r9.<init>()
            r9.volume_id = r7
            r2 = -2147483648(0xfffffffvar_, float:-0.0)
            r9.dc_id = r2
            int r2 = org.telegram.messenger.SharedConfig.getLastLocalId()
            r9.local_id = r2
            byte[] r2 = new byte[r6]
            r9.file_reference = r2
            org.telegram.tgnet.TLRPC$TL_photoSize_layer127 r2 = new org.telegram.tgnet.TLRPC$TL_photoSize_layer127
            r2.<init>()
            r2.location = r9
            int r10 = r5.getWidth()
            r2.w = r10
            int r10 = r5.getHeight()
            r2.h = r10
            int r13 = r2.w
            r0 = 100
            if (r13 > r0) goto L_0x0055
            if (r10 > r0) goto L_0x0055
            java.lang.String r10 = "s"
            r2.type = r10
            goto L_0x007a
        L_0x0055:
            r0 = 320(0x140, float:4.48E-43)
            if (r13 > r0) goto L_0x0060
            if (r10 > r0) goto L_0x0060
            java.lang.String r10 = "m"
            r2.type = r10
            goto L_0x007a
        L_0x0060:
            r0 = 800(0x320, float:1.121E-42)
            if (r13 > r0) goto L_0x006b
            if (r10 > r0) goto L_0x006b
            java.lang.String r10 = "x"
            r2.type = r10
            goto L_0x007a
        L_0x006b:
            r0 = 1280(0x500, float:1.794E-42)
            if (r13 > r0) goto L_0x0076
            if (r10 > r0) goto L_0x0076
            java.lang.String r10 = "y"
            r2.type = r10
            goto L_0x007a
        L_0x0076:
            java.lang.String r10 = "w"
            r2.type = r10
        L_0x007a:
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            long r0 = r9.volume_id
            r10.append(r0)
            java.lang.String r13 = "_"
            r10.append(r13)
            int r13 = r9.local_id
            r10.append(r13)
            java.lang.String r13 = ".jpg"
            r10.append(r13)
            java.lang.String r10 = r10.toString()
            r13 = 4
            if (r14 == 0) goto L_0x009f
            java.io.File r6 = org.telegram.messenger.FileLoader.getDirectory(r13)
            goto L_0x00ae
        L_0x009f:
            long r0 = r9.volume_id
            int r9 = (r0 > r7 ? 1 : (r0 == r7 ? 0 : -1))
            if (r9 == 0) goto L_0x00aa
            java.io.File r6 = org.telegram.messenger.FileLoader.getDirectory(r6)
            goto L_0x00ae
        L_0x00aa:
            java.io.File r6 = org.telegram.messenger.FileLoader.getDirectory(r13)
        L_0x00ae:
            java.io.File r7 = new java.io.File
            r7.<init>(r6, r10)
            java.io.FileOutputStream r6 = new java.io.FileOutputStream
            r6.<init>(r7)
            r5.compress(r4, r11, r6)
            if (r12 != 0) goto L_0x00c8
            java.nio.channels.FileChannel r7 = r6.getChannel()
            long r7 = r7.size()
            int r8 = (int) r7
            r2.size = r8
        L_0x00c8:
            r6.close()
            if (r12 == 0) goto L_0x00e1
            java.io.ByteArrayOutputStream r6 = new java.io.ByteArrayOutputStream
            r6.<init>()
            r5.compress(r4, r11, r6)
            byte[] r4 = r6.toByteArray()
            r2.bytes = r4
            int r4 = r4.length
            r2.size = r4
            r6.close()
        L_0x00e1:
            if (r5 == r3) goto L_0x00e6
            r5.recycle()
        L_0x00e6:
            return r2
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
            File pathToAttach = FileLoader.getInstance(UserConfig.selectedAccount).getPathToAttach(findPhotoCachedSize, true);
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
                        Utilities.aesCtrDecryptionByteArray(bArr4, bArr2, bArr3, 0, (long) bArr4.length, 0);
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
        } else if (tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaWebPage) {
            TLRPC$Photo tLRPC$Photo = tLRPC$MessageMedia.webpage.photo;
            if (tLRPC$Photo == null) {
                return null;
            }
            int size3 = tLRPC$Photo.sizes.size();
            while (i < size3) {
                tLRPC$PhotoSize = tLRPC$Message.media.webpage.photo.sizes.get(i);
                if (!(tLRPC$PhotoSize instanceof TLRPC$TL_photoCachedSize)) {
                    i++;
                }
            }
            return null;
        } else if (!(tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaInvoice)) {
            return null;
        } else {
            TLRPC$MessageExtendedMedia tLRPC$MessageExtendedMedia = tLRPC$MessageMedia.extended_media;
            if (tLRPC$MessageExtendedMedia instanceof TLRPC$TL_messageExtendedMediaPreview) {
                return ((TLRPC$TL_messageExtendedMediaPreview) tLRPC$MessageExtendedMedia).thumb;
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
                        String format = String.format(Locale.US, "%s_false@%d_%d_b", new Object[]{ImageLocation.getStrippedKey(tLRPC$Message2, tLRPC$Message2, tLRPC$PhotoSize), Integer.valueOf((int) (messageSize.x / AndroidUtilities.density)), Integer.valueOf((int) (messageSize.y / AndroidUtilities.density))});
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
            File pathToAttach = FileLoader.getInstance(UserConfig.selectedAccount).getPathToAttach(findPhotoCachedSize, true);
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

        public MessageThumb(String str, BitmapDrawable bitmapDrawable) {
            this.key = str;
            this.drawable = bitmapDrawable;
        }
    }
}
