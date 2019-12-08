package org.telegram.messenger;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.text.TextUtils;
import android.util.SparseArray;
import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import org.telegram.messenger.FileLoader.FileLoaderDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.InputEncryptedFile;
import org.telegram.tgnet.TLRPC.InputFile;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.Components.AnimatedFileDrawable;
import org.telegram.ui.Components.RLottieDrawable;

public class ImageLoader {
    public static final String AUTOPLAY_FILTER = "g";
    private static volatile ImageLoader Instance = null;
    private static ThreadLocal<byte[]> bytesLocal = new ThreadLocal();
    private static ThreadLocal<byte[]> bytesThumbLocal = new ThreadLocal();
    private static byte[] header = new byte[12];
    private static byte[] headerThumb = new byte[12];
    private LinkedList<ArtworkLoadTask> artworkTasks = new LinkedList();
    private HashMap<String, Integer> bitmapUseCounts = new HashMap();
    private DispatchQueue cacheOutQueue = new DispatchQueue("cacheOutQueue");
    private DispatchQueue cacheThumbOutQueue = new DispatchQueue("cacheThumbOutQueue");
    private boolean canForce8888;
    private int currentArtworkTasksCount;
    private int currentHttpFileLoadTasksCount;
    private int currentHttpTasksCount;
    private ConcurrentHashMap<String, Float> fileProgresses = new ConcurrentHashMap();
    private HashMap<String, Integer> forceLoadingImages = new HashMap();
    private LinkedList<HttpFileTask> httpFileLoadTasks;
    private HashMap<String, HttpFileTask> httpFileLoadTasksByKeys;
    private LinkedList<HttpImageTask> httpTasks = new LinkedList();
    private String ignoreRemoval;
    private DispatchQueue imageLoadQueue = new DispatchQueue("imageLoadQueue");
    private HashMap<String, CacheImage> imageLoadingByKeys = new HashMap();
    private SparseArray<CacheImage> imageLoadingByTag = new SparseArray();
    private HashMap<String, CacheImage> imageLoadingByUrl = new HashMap();
    private volatile long lastCacheOutTime;
    private int lastImageNum;
    private long lastProgressUpdateTime;
    private LruCache<RLottieDrawable> lottieMemCache;
    private LruCache<BitmapDrawable> memCache;
    private HashMap<String, String> replacedBitmaps = new HashMap();
    private HashMap<String, Runnable> retryHttpsTasks;
    private File telegramPath;
    private ConcurrentHashMap<String, WebFile> testWebFile;
    private HashMap<String, ThumbGenerateTask> thumbGenerateTasks = new HashMap();
    private DispatchQueue thumbGeneratingQueue = new DispatchQueue("thumbGeneratingQueue");
    private HashMap<String, ThumbGenerateInfo> waitingForQualityThumb = new HashMap();
    private SparseArray<String> waitingForQualityThumbByTag = new SparseArray();

    private class ArtworkLoadTask extends AsyncTask<Void, Void, String> {
        private CacheImage cacheImage;
        private boolean canRetry = true;
        private HttpURLConnection httpConnection;
        private boolean small;

        public ArtworkLoadTask(CacheImage cacheImage) {
            boolean z = true;
            this.cacheImage = cacheImage;
            if (Uri.parse(cacheImage.imageLocation.path).getQueryParameter("s") == null) {
                z = false;
            }
            this.small = z;
        }

        /* Access modifiers changed, original: protected|varargs */
        /* JADX WARNING: Removed duplicated region for block: B:93:0x0112 A:{Catch:{ all -> 0x0152 }} */
        /* JADX WARNING: Removed duplicated region for block: B:90:0x0109 A:{Catch:{ all -> 0x0152 }} */
        /* JADX WARNING: Removed duplicated region for block: B:110:0x013f A:{Catch:{ Throwable -> 0x0144 }} */
        /* JADX WARNING: Removed duplicated region for block: B:113:0x0146 A:{SYNTHETIC, Splitter:B:113:0x0146} */
        /* JADX WARNING: Removed duplicated region for block: B:125:0x0158 A:{Catch:{ Throwable -> 0x015d }} */
        /* JADX WARNING: Removed duplicated region for block: B:128:0x015f A:{SYNTHETIC, Splitter:B:128:0x015f} */
        /* JADX WARNING: Removed duplicated region for block: B:133:0x0169 A:{SYNTHETIC, Splitter:B:133:0x0169} */
        /* JADX WARNING: Removed duplicated region for block: B:90:0x0109 A:{Catch:{ all -> 0x0152 }} */
        /* JADX WARNING: Removed duplicated region for block: B:93:0x0112 A:{Catch:{ all -> 0x0152 }} */
        /* JADX WARNING: Removed duplicated region for block: B:110:0x013f A:{Catch:{ Throwable -> 0x0144 }} */
        /* JADX WARNING: Removed duplicated region for block: B:113:0x0146 A:{SYNTHETIC, Splitter:B:113:0x0146} */
        /* JADX WARNING: Removed duplicated region for block: B:125:0x0158 A:{Catch:{ Throwable -> 0x015d }} */
        /* JADX WARNING: Removed duplicated region for block: B:128:0x015f A:{SYNTHETIC, Splitter:B:128:0x015f} */
        /* JADX WARNING: Removed duplicated region for block: B:133:0x0169 A:{SYNTHETIC, Splitter:B:133:0x0169} */
        /* JADX WARNING: Removed duplicated region for block: B:125:0x0158 A:{Catch:{ Throwable -> 0x015d }} */
        /* JADX WARNING: Removed duplicated region for block: B:128:0x015f A:{SYNTHETIC, Splitter:B:128:0x015f} */
        /* JADX WARNING: Removed duplicated region for block: B:133:0x0169 A:{SYNTHETIC, Splitter:B:133:0x0169} */
        /* JADX WARNING: Missing block: B:117:0x014e, code skipped:
            if (r2 == null) goto L_0x0151;
     */
        public java.lang.String doInBackground(java.lang.Void... r8) {
            /*
            r7 = this;
            r8 = 0;
            r0 = 0;
            r1 = r7.cacheImage;	 Catch:{ Throwable -> 0x0102, all -> 0x00ff }
            r1 = r1.imageLocation;	 Catch:{ Throwable -> 0x0102, all -> 0x00ff }
            r1 = r1.path;	 Catch:{ Throwable -> 0x0102, all -> 0x00ff }
            r2 = new java.net.URL;	 Catch:{ Throwable -> 0x0102, all -> 0x00ff }
            r3 = "athumb://";
            r4 = "https://";
            r1 = r1.replace(r3, r4);	 Catch:{ Throwable -> 0x0102, all -> 0x00ff }
            r2.<init>(r1);	 Catch:{ Throwable -> 0x0102, all -> 0x00ff }
            r1 = r2.openConnection();	 Catch:{ Throwable -> 0x0102, all -> 0x00ff }
            r1 = (java.net.HttpURLConnection) r1;	 Catch:{ Throwable -> 0x0102, all -> 0x00ff }
            r7.httpConnection = r1;	 Catch:{ Throwable -> 0x0102, all -> 0x00ff }
            r1 = r7.httpConnection;	 Catch:{ Throwable -> 0x0102, all -> 0x00ff }
            r2 = "User-Agent";
            r3 = "Mozilla/5.0 (iPhone; CPU iPhone OS 10_0 like Mac OS X) AppleWebKit/602.1.38 (KHTML, like Gecko) Version/10.0 Mobile/14A5297c Safari/602.1";
            r1.addRequestProperty(r2, r3);	 Catch:{ Throwable -> 0x0102, all -> 0x00ff }
            r1 = r7.httpConnection;	 Catch:{ Throwable -> 0x0102, all -> 0x00ff }
            r2 = 5000; // 0x1388 float:7.006E-42 double:2.4703E-320;
            r1.setConnectTimeout(r2);	 Catch:{ Throwable -> 0x0102, all -> 0x00ff }
            r1 = r7.httpConnection;	 Catch:{ Throwable -> 0x0102, all -> 0x00ff }
            r1.setReadTimeout(r2);	 Catch:{ Throwable -> 0x0102, all -> 0x00ff }
            r1 = r7.httpConnection;	 Catch:{ Throwable -> 0x0102, all -> 0x00ff }
            r1.connect();	 Catch:{ Throwable -> 0x0102, all -> 0x00ff }
            r1 = r7.httpConnection;	 Catch:{ Exception -> 0x0050 }
            if (r1 == 0) goto L_0x0054;
        L_0x003b:
            r1 = r7.httpConnection;	 Catch:{ Exception -> 0x0050 }
            r1 = r1.getResponseCode();	 Catch:{ Exception -> 0x0050 }
            r2 = 200; // 0xc8 float:2.8E-43 double:9.9E-322;
            if (r1 == r2) goto L_0x0054;
        L_0x0045:
            r2 = 202; // 0xca float:2.83E-43 double:1.0E-321;
            if (r1 == r2) goto L_0x0054;
        L_0x0049:
            r2 = 304; // 0x130 float:4.26E-43 double:1.5E-321;
            if (r1 == r2) goto L_0x0054;
        L_0x004d:
            r7.canRetry = r0;	 Catch:{ Exception -> 0x0050 }
            goto L_0x0054;
        L_0x0050:
            r1 = move-exception;
            org.telegram.messenger.FileLog.e(r1);	 Catch:{ Throwable -> 0x0102, all -> 0x00ff }
        L_0x0054:
            r1 = r7.httpConnection;	 Catch:{ Throwable -> 0x0102, all -> 0x00ff }
            r1 = r1.getInputStream();	 Catch:{ Throwable -> 0x0102, all -> 0x00ff }
            r2 = new java.io.ByteArrayOutputStream;	 Catch:{ Throwable -> 0x00fa, all -> 0x00f5 }
            r2.<init>();	 Catch:{ Throwable -> 0x00fa, all -> 0x00f5 }
            r3 = 32768; // 0x8000 float:4.5918E-41 double:1.61895E-319;
            r3 = new byte[r3];	 Catch:{ Throwable -> 0x00f0, all -> 0x00ee }
        L_0x0064:
            r4 = r7.isCancelled();	 Catch:{ Throwable -> 0x00f0, all -> 0x00ee }
            if (r4 == 0) goto L_0x006b;
        L_0x006a:
            goto L_0x0076;
        L_0x006b:
            r4 = r1.read(r3);	 Catch:{ Throwable -> 0x00f0, all -> 0x00ee }
            if (r4 <= 0) goto L_0x0075;
        L_0x0071:
            r2.write(r3, r0, r4);	 Catch:{ Throwable -> 0x00f0, all -> 0x00ee }
            goto L_0x0064;
        L_0x0075:
            r3 = -1;
        L_0x0076:
            r7.canRetry = r0;	 Catch:{ Throwable -> 0x00f0, all -> 0x00ee }
            r3 = new org.json.JSONObject;	 Catch:{ Throwable -> 0x00f0, all -> 0x00ee }
            r4 = new java.lang.String;	 Catch:{ Throwable -> 0x00f0, all -> 0x00ee }
            r5 = r2.toByteArray();	 Catch:{ Throwable -> 0x00f0, all -> 0x00ee }
            r4.<init>(r5);	 Catch:{ Throwable -> 0x00f0, all -> 0x00ee }
            r3.<init>(r4);	 Catch:{ Throwable -> 0x00f0, all -> 0x00ee }
            r4 = "results";
            r3 = r3.getJSONArray(r4);	 Catch:{ Throwable -> 0x00f0, all -> 0x00ee }
            r4 = r3.length();	 Catch:{ Throwable -> 0x00f0, all -> 0x00ee }
            if (r4 <= 0) goto L_0x00d6;
        L_0x0092:
            r3 = r3.getJSONObject(r0);	 Catch:{ Throwable -> 0x00f0, all -> 0x00ee }
            r4 = "artworkUrl100";
            r3 = r3.getString(r4);	 Catch:{ Throwable -> 0x00f0, all -> 0x00ee }
            r4 = r7.small;	 Catch:{ Throwable -> 0x00f0, all -> 0x00ee }
            if (r4 == 0) goto L_0x00b7;
        L_0x00a0:
            r8 = r7.httpConnection;	 Catch:{ Throwable -> 0x00a9 }
            if (r8 == 0) goto L_0x00a9;
        L_0x00a4:
            r8 = r7.httpConnection;	 Catch:{ Throwable -> 0x00a9 }
            r8.disconnect();	 Catch:{ Throwable -> 0x00a9 }
        L_0x00a9:
            if (r1 == 0) goto L_0x00b3;
        L_0x00ab:
            r1.close();	 Catch:{ Throwable -> 0x00af }
            goto L_0x00b3;
        L_0x00af:
            r8 = move-exception;
            org.telegram.messenger.FileLog.e(r8);
        L_0x00b3:
            r2.close();	 Catch:{ Exception -> 0x00b6 }
        L_0x00b6:
            return r3;
        L_0x00b7:
            r4 = "100x100";
            r5 = "600x600";
            r8 = r3.replace(r4, r5);	 Catch:{ Throwable -> 0x00f0, all -> 0x00ee }
            r0 = r7.httpConnection;	 Catch:{ Throwable -> 0x00c8 }
            if (r0 == 0) goto L_0x00c8;
        L_0x00c3:
            r0 = r7.httpConnection;	 Catch:{ Throwable -> 0x00c8 }
            r0.disconnect();	 Catch:{ Throwable -> 0x00c8 }
        L_0x00c8:
            if (r1 == 0) goto L_0x00d2;
        L_0x00ca:
            r1.close();	 Catch:{ Throwable -> 0x00ce }
            goto L_0x00d2;
        L_0x00ce:
            r0 = move-exception;
            org.telegram.messenger.FileLog.e(r0);
        L_0x00d2:
            r2.close();	 Catch:{ Exception -> 0x00d5 }
        L_0x00d5:
            return r8;
        L_0x00d6:
            r0 = r7.httpConnection;	 Catch:{ Throwable -> 0x00df }
            if (r0 == 0) goto L_0x00df;
        L_0x00da:
            r0 = r7.httpConnection;	 Catch:{ Throwable -> 0x00df }
            r0.disconnect();	 Catch:{ Throwable -> 0x00df }
        L_0x00df:
            if (r1 == 0) goto L_0x00e9;
        L_0x00e1:
            r1.close();	 Catch:{ Throwable -> 0x00e5 }
            goto L_0x00e9;
        L_0x00e5:
            r0 = move-exception;
            org.telegram.messenger.FileLog.e(r0);
        L_0x00e9:
            r2.close();	 Catch:{ Exception -> 0x0151 }
            goto L_0x0151;
        L_0x00ee:
            r0 = move-exception;
            goto L_0x00f7;
        L_0x00f0:
            r3 = move-exception;
            r6 = r3;
            r3 = r1;
            r1 = r6;
            goto L_0x0105;
        L_0x00f5:
            r0 = move-exception;
            r2 = r8;
        L_0x00f7:
            r8 = r1;
            goto L_0x0154;
        L_0x00fa:
            r2 = move-exception;
            r3 = r1;
            r1 = r2;
            r2 = r8;
            goto L_0x0105;
        L_0x00ff:
            r0 = move-exception;
            r2 = r8;
            goto L_0x0154;
        L_0x0102:
            r1 = move-exception;
            r2 = r8;
            r3 = r2;
        L_0x0105:
            r4 = r1 instanceof java.net.SocketTimeoutException;	 Catch:{ all -> 0x0152 }
            if (r4 == 0) goto L_0x0112;
        L_0x0109:
            r4 = org.telegram.messenger.ApplicationLoader.isNetworkOnline();	 Catch:{ all -> 0x0152 }
            if (r4 == 0) goto L_0x0138;
        L_0x010f:
            r7.canRetry = r0;	 Catch:{ all -> 0x0152 }
            goto L_0x0138;
        L_0x0112:
            r4 = r1 instanceof java.net.UnknownHostException;	 Catch:{ all -> 0x0152 }
            if (r4 == 0) goto L_0x0119;
        L_0x0116:
            r7.canRetry = r0;	 Catch:{ all -> 0x0152 }
            goto L_0x0138;
        L_0x0119:
            r4 = r1 instanceof java.net.SocketException;	 Catch:{ all -> 0x0152 }
            if (r4 == 0) goto L_0x0132;
        L_0x011d:
            r4 = r1.getMessage();	 Catch:{ all -> 0x0152 }
            if (r4 == 0) goto L_0x0138;
        L_0x0123:
            r4 = r1.getMessage();	 Catch:{ all -> 0x0152 }
            r5 = "ECONNRESET";
            r4 = r4.contains(r5);	 Catch:{ all -> 0x0152 }
            if (r4 == 0) goto L_0x0138;
        L_0x012f:
            r7.canRetry = r0;	 Catch:{ all -> 0x0152 }
            goto L_0x0138;
        L_0x0132:
            r4 = r1 instanceof java.io.FileNotFoundException;	 Catch:{ all -> 0x0152 }
            if (r4 == 0) goto L_0x0138;
        L_0x0136:
            r7.canRetry = r0;	 Catch:{ all -> 0x0152 }
        L_0x0138:
            org.telegram.messenger.FileLog.e(r1);	 Catch:{ all -> 0x0152 }
            r0 = r7.httpConnection;	 Catch:{ Throwable -> 0x0144 }
            if (r0 == 0) goto L_0x0144;
        L_0x013f:
            r0 = r7.httpConnection;	 Catch:{ Throwable -> 0x0144 }
            r0.disconnect();	 Catch:{ Throwable -> 0x0144 }
        L_0x0144:
            if (r3 == 0) goto L_0x014e;
        L_0x0146:
            r3.close();	 Catch:{ Throwable -> 0x014a }
            goto L_0x014e;
        L_0x014a:
            r0 = move-exception;
            org.telegram.messenger.FileLog.e(r0);
        L_0x014e:
            if (r2 == 0) goto L_0x0151;
        L_0x0150:
            goto L_0x00e9;
        L_0x0151:
            return r8;
        L_0x0152:
            r0 = move-exception;
            r8 = r3;
        L_0x0154:
            r1 = r7.httpConnection;	 Catch:{ Throwable -> 0x015d }
            if (r1 == 0) goto L_0x015d;
        L_0x0158:
            r1 = r7.httpConnection;	 Catch:{ Throwable -> 0x015d }
            r1.disconnect();	 Catch:{ Throwable -> 0x015d }
        L_0x015d:
            if (r8 == 0) goto L_0x0167;
        L_0x015f:
            r8.close();	 Catch:{ Throwable -> 0x0163 }
            goto L_0x0167;
        L_0x0163:
            r8 = move-exception;
            org.telegram.messenger.FileLog.e(r8);
        L_0x0167:
            if (r2 == 0) goto L_0x016c;
        L_0x0169:
            r2.close();	 Catch:{ Exception -> 0x016c }
        L_0x016c:
            goto L_0x016e;
        L_0x016d:
            throw r0;
        L_0x016e:
            goto L_0x016d;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ImageLoader$ArtworkLoadTask.doInBackground(java.lang.Void[]):java.lang.String");
        }

        /* Access modifiers changed, original: protected */
        public void onPostExecute(String str) {
            if (str != null) {
                CacheImage cacheImage = this.cacheImage;
                cacheImage.httpTask = new HttpImageTask(cacheImage, 0, str);
                ImageLoader.this.httpTasks.add(this.cacheImage.httpTask);
                ImageLoader.this.runHttpTasks(false);
            } else if (this.canRetry) {
                ImageLoader.this.artworkLoadError(this.cacheImage.url);
            }
            ImageLoader.this.imageLoadQueue.postRunnable(new -$$Lambda$ImageLoader$ArtworkLoadTask$AYFfY8-xR4BmCIfdHvNLIsGZyV0(this));
        }

        public /* synthetic */ void lambda$onPostExecute$0$ImageLoader$ArtworkLoadTask() {
            ImageLoader.this.runArtworkTasks(true);
        }

        public /* synthetic */ void lambda$onCancelled$1$ImageLoader$ArtworkLoadTask() {
            ImageLoader.this.runArtworkTasks(true);
        }

        /* Access modifiers changed, original: protected */
        public void onCancelled() {
            ImageLoader.this.imageLoadQueue.postRunnable(new -$$Lambda$ImageLoader$ArtworkLoadTask$MXyHRkFD1Tybf8nHKTkuKr4tAsA(this));
        }
    }

    private class CacheImage {
        protected boolean animatedFile;
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
        protected ArrayList<Integer> imageTypes;
        protected String key;
        protected ArrayList<String> keys;
        protected boolean lottieFile;
        protected Object parentObject;
        protected SecureDocument secureDocument;
        protected int size;
        protected File tempFilePath;
        protected String url;

        private CacheImage() {
            this.imageReceiverArray = new ArrayList();
            this.imageReceiverGuidsArray = new ArrayList();
            this.keys = new ArrayList();
            this.filters = new ArrayList();
            this.imageTypes = new ArrayList();
        }

        /* synthetic */ CacheImage(ImageLoader imageLoader, AnonymousClass1 anonymousClass1) {
            this();
        }

        public void addImageReceiver(ImageReceiver imageReceiver, String str, String str2, int i, int i2) {
            if (!this.imageReceiverArray.contains(imageReceiver)) {
                this.imageReceiverArray.add(imageReceiver);
                this.imageReceiverGuidsArray.add(Integer.valueOf(i2));
                this.keys.add(str);
                this.filters.add(str2);
                this.imageTypes.add(Integer.valueOf(i));
                ImageLoader.this.imageLoadingByTag.put(imageReceiver.getTag(i), this);
            }
        }

        public void replaceImageReceiver(ImageReceiver imageReceiver, String str, String str2, int i, int i2) {
            int indexOf = this.imageReceiverArray.indexOf(imageReceiver);
            if (indexOf != -1) {
                if (((Integer) this.imageTypes.get(indexOf)).intValue() != i) {
                    ArrayList arrayList = this.imageReceiverArray;
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

        public void removeImageReceiver(ImageReceiver imageReceiver) {
            int i = this.imageType;
            int i2 = 0;
            while (i2 < this.imageReceiverArray.size()) {
                ImageReceiver imageReceiver2 = (ImageReceiver) this.imageReceiverArray.get(i2);
                if (imageReceiver2 == null || imageReceiver2 == imageReceiver) {
                    this.imageReceiverArray.remove(i2);
                    this.imageReceiverGuidsArray.remove(i2);
                    this.keys.remove(i2);
                    this.filters.remove(i2);
                    i = ((Integer) this.imageTypes.remove(i2)).intValue();
                    if (imageReceiver2 != null) {
                        ImageLoader.this.imageLoadingByTag.remove(imageReceiver2.getTag(i));
                    }
                    i2--;
                }
                i2++;
            }
            if (this.imageReceiverArray.size() == 0) {
                for (int i3 = 0; i3 < this.imageReceiverArray.size(); i3++) {
                    ImageLoader.this.imageLoadingByTag.remove(((ImageReceiver) this.imageReceiverArray.get(i3)).getTag(i));
                }
                this.imageReceiverArray.clear();
                if (!(this.imageLocation == null || ImageLoader.this.forceLoadingImages.containsKey(this.key))) {
                    ImageLocation imageLocation = this.imageLocation;
                    if (imageLocation.location != null) {
                        FileLoader.getInstance(this.currentAccount).cancelLoadFile(this.imageLocation.location, this.ext);
                    } else if (imageLocation.document != null) {
                        FileLoader.getInstance(this.currentAccount).cancelLoadFile(this.imageLocation.document);
                    } else if (imageLocation.secureDocument != null) {
                        FileLoader.getInstance(this.currentAccount).cancelLoadFile(this.imageLocation.secureDocument);
                    } else if (imageLocation.webFile != null) {
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
                AndroidUtilities.runOnUIThread(new -$$Lambda$ImageLoader$CacheImage$iXayShOZEpIUbem7Yub1sgbEZr4(this, drawable, new ArrayList(this.imageReceiverArray), new ArrayList(this.imageReceiverGuidsArray), str));
            }
            for (int i = 0; i < this.imageReceiverArray.size(); i++) {
                ImageLoader.this.imageLoadingByTag.remove(((ImageReceiver) this.imageReceiverArray.get(i)).getTag(this.imageType));
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
            int i = 0;
            if (drawable instanceof AnimatedFileDrawable) {
                drawable = (AnimatedFileDrawable) drawable;
                Object obj = null;
                while (i < arrayList.size()) {
                    Drawable drawable2;
                    ImageReceiver imageReceiver = (ImageReceiver) arrayList.get(i);
                    if (i == 0) {
                        drawable2 = drawable;
                    } else {
                        drawable2 = drawable.makeCopy();
                    }
                    if (imageReceiver.setImageBitmapByKey(drawable2, this.key, this.imageType, false, ((Integer) arrayList2.get(i)).intValue())) {
                        if (drawable2 == drawable) {
                            obj = 1;
                        }
                    } else if (drawable2 != drawable) {
                        drawable2.recycle();
                    }
                    i++;
                }
                if (obj == null) {
                    drawable.recycle();
                }
            } else {
                while (i < arrayList.size()) {
                    ((ImageReceiver) arrayList.get(i)).setImageBitmapByKey(drawable, this.key, ((Integer) this.imageTypes.get(i)).intValue(), false, ((Integer) arrayList2.get(i)).intValue());
                    i++;
                }
            }
            if (str != null) {
                ImageLoader.this.decrementUseCount(str);
            }
        }
    }

    private class CacheOutTask implements Runnable {
        private CacheImage cacheImage;
        private boolean isCancelled;
        private Thread runningThread;
        private final Object sync = new Object();

        public CacheOutTask(CacheImage cacheImage) {
            this.cacheImage = cacheImage;
        }

        /* JADX WARNING: Removed duplicated region for block: B:469:0x06fe A:{SYNTHETIC, Splitter:B:469:0x06fe} */
        /* JADX WARNING: Removed duplicated region for block: B:682:0x097e  */
        /* JADX WARNING: Removed duplicated region for block: B:450:0x06bc  */
        /* JADX WARNING: Removed duplicated region for block: B:305:0x04b3 A:{SYNTHETIC, Splitter:B:305:0x04b3} */
        /* JADX WARNING: Removed duplicated region for block: B:677:0x096d A:{SKIP} */
        /* JADX WARNING: Removed duplicated region for block: B:682:0x097e  */
        /* JADX WARNING: Removed duplicated region for block: B:305:0x04b3 A:{SYNTHETIC, Splitter:B:305:0x04b3} */
        /* JADX WARNING: Removed duplicated region for block: B:450:0x06bc  */
        /* JADX WARNING: Removed duplicated region for block: B:677:0x096d A:{SKIP} */
        /* JADX WARNING: Removed duplicated region for block: B:682:0x097e  */
        /* JADX WARNING: Removed duplicated region for block: B:677:0x096d A:{SKIP} */
        /* JADX WARNING: Removed duplicated region for block: B:682:0x097e  */
        /* JADX WARNING: Removed duplicated region for block: B:677:0x096d A:{SKIP} */
        /* JADX WARNING: Removed duplicated region for block: B:682:0x097e  */
        /* JADX WARNING: Removed duplicated region for block: B:450:0x06bc  */
        /* JADX WARNING: Removed duplicated region for block: B:305:0x04b3 A:{SYNTHETIC, Splitter:B:305:0x04b3} */
        /* JADX WARNING: Removed duplicated region for block: B:677:0x096d A:{SKIP} */
        /* JADX WARNING: Removed duplicated region for block: B:682:0x097e  */
        /* JADX WARNING: Removed duplicated region for block: B:179:0x02d8  */
        /* JADX WARNING: Removed duplicated region for block: B:276:0x044d  */
        /* JADX WARNING: Removed duplicated region for block: B:184:0x02ea A:{Catch:{ Throwable -> 0x0499 }} */
        /* JADX WARNING: Removed duplicated region for block: B:305:0x04b3 A:{SYNTHETIC, Splitter:B:305:0x04b3} */
        /* JADX WARNING: Removed duplicated region for block: B:450:0x06bc  */
        /* JADX WARNING: Removed duplicated region for block: B:677:0x096d A:{SKIP} */
        /* JADX WARNING: Removed duplicated region for block: B:682:0x097e  */
        /* JADX WARNING: Removed duplicated region for block: B:179:0x02d8  */
        /* JADX WARNING: Removed duplicated region for block: B:184:0x02ea A:{Catch:{ Throwable -> 0x0499 }} */
        /* JADX WARNING: Removed duplicated region for block: B:276:0x044d  */
        /* JADX WARNING: Removed duplicated region for block: B:450:0x06bc  */
        /* JADX WARNING: Removed duplicated region for block: B:305:0x04b3 A:{SYNTHETIC, Splitter:B:305:0x04b3} */
        /* JADX WARNING: Removed duplicated region for block: B:677:0x096d A:{SKIP} */
        /* JADX WARNING: Removed duplicated region for block: B:682:0x097e  */
        /* JADX WARNING: Removed duplicated region for block: B:677:0x096d A:{SKIP} */
        /* JADX WARNING: Removed duplicated region for block: B:682:0x097e  */
        /* JADX WARNING: Removed duplicated region for block: B:677:0x096d A:{SKIP} */
        /* JADX WARNING: Removed duplicated region for block: B:682:0x097e  */
        /* JADX WARNING: Removed duplicated region for block: B:144:0x024f A:{SYNTHETIC, Splitter:B:144:0x024f} */
        /* JADX WARNING: Removed duplicated region for block: B:157:0x026c  */
        /* JADX WARNING: Removed duplicated region for block: B:179:0x02d8  */
        /* JADX WARNING: Removed duplicated region for block: B:276:0x044d  */
        /* JADX WARNING: Removed duplicated region for block: B:184:0x02ea A:{Catch:{ Throwable -> 0x0499 }} */
        /* JADX WARNING: Removed duplicated region for block: B:305:0x04b3 A:{SYNTHETIC, Splitter:B:305:0x04b3} */
        /* JADX WARNING: Removed duplicated region for block: B:450:0x06bc  */
        /* JADX WARNING: Removed duplicated region for block: B:677:0x096d A:{SKIP} */
        /* JADX WARNING: Removed duplicated region for block: B:682:0x097e  */
        /* JADX WARNING: Removed duplicated region for block: B:677:0x096d A:{SKIP} */
        /* JADX WARNING: Removed duplicated region for block: B:682:0x097e  */
        /* JADX WARNING: Removed duplicated region for block: B:450:0x06bc  */
        /* JADX WARNING: Removed duplicated region for block: B:305:0x04b3 A:{SYNTHETIC, Splitter:B:305:0x04b3} */
        /* JADX WARNING: Removed duplicated region for block: B:677:0x096d A:{SKIP} */
        /* JADX WARNING: Removed duplicated region for block: B:682:0x097e  */
        /* JADX WARNING: Removed duplicated region for block: B:144:0x024f A:{SYNTHETIC, Splitter:B:144:0x024f} */
        /* JADX WARNING: Removed duplicated region for block: B:157:0x026c  */
        /* JADX WARNING: Removed duplicated region for block: B:179:0x02d8  */
        /* JADX WARNING: Removed duplicated region for block: B:184:0x02ea A:{Catch:{ Throwable -> 0x0499 }} */
        /* JADX WARNING: Removed duplicated region for block: B:276:0x044d  */
        /* JADX WARNING: Removed duplicated region for block: B:305:0x04b3 A:{SYNTHETIC, Splitter:B:305:0x04b3} */
        /* JADX WARNING: Removed duplicated region for block: B:450:0x06bc  */
        /* JADX WARNING: Removed duplicated region for block: B:677:0x096d A:{SKIP} */
        /* JADX WARNING: Removed duplicated region for block: B:682:0x097e  */
        /* JADX WARNING: Removed duplicated region for block: B:677:0x096d A:{SKIP} */
        /* JADX WARNING: Removed duplicated region for block: B:682:0x097e  */
        /* JADX WARNING: Removed duplicated region for block: B:677:0x096d A:{SKIP} */
        /* JADX WARNING: Removed duplicated region for block: B:682:0x097e  */
        /* JADX WARNING: Removed duplicated region for block: B:677:0x096d A:{SKIP} */
        /* JADX WARNING: Removed duplicated region for block: B:682:0x097e  */
        /* JADX WARNING: Removed duplicated region for block: B:677:0x096d A:{SKIP} */
        /* JADX WARNING: Removed duplicated region for block: B:682:0x097e  */
        /* JADX WARNING: Removed duplicated region for block: B:450:0x06bc  */
        /* JADX WARNING: Removed duplicated region for block: B:305:0x04b3 A:{SYNTHETIC, Splitter:B:305:0x04b3} */
        /* JADX WARNING: Removed duplicated region for block: B:677:0x096d A:{SKIP} */
        /* JADX WARNING: Removed duplicated region for block: B:682:0x097e  */
        /* JADX WARNING: Removed duplicated region for block: B:149:0x0257 A:{SYNTHETIC, Splitter:B:149:0x0257} */
        /* JADX WARNING: Removed duplicated region for block: B:305:0x04b3 A:{SYNTHETIC, Splitter:B:305:0x04b3} */
        /* JADX WARNING: Removed duplicated region for block: B:450:0x06bc  */
        /* JADX WARNING: Removed duplicated region for block: B:677:0x096d A:{SKIP} */
        /* JADX WARNING: Removed duplicated region for block: B:682:0x097e  */
        /* JADX WARNING: Removed duplicated region for block: B:677:0x096d A:{SKIP} */
        /* JADX WARNING: Removed duplicated region for block: B:682:0x097e  */
        /* JADX WARNING: Removed duplicated region for block: B:677:0x096d A:{SKIP} */
        /* JADX WARNING: Removed duplicated region for block: B:682:0x097e  */
        /* JADX WARNING: Removed duplicated region for block: B:450:0x06bc  */
        /* JADX WARNING: Removed duplicated region for block: B:305:0x04b3 A:{SYNTHETIC, Splitter:B:305:0x04b3} */
        /* JADX WARNING: Removed duplicated region for block: B:677:0x096d A:{SKIP} */
        /* JADX WARNING: Removed duplicated region for block: B:682:0x097e  */
        /* JADX WARNING: Removed duplicated region for block: B:677:0x096d A:{SKIP} */
        /* JADX WARNING: Removed duplicated region for block: B:682:0x097e  */
        /* JADX WARNING: Removed duplicated region for block: B:305:0x04b3 A:{SYNTHETIC, Splitter:B:305:0x04b3} */
        /* JADX WARNING: Removed duplicated region for block: B:450:0x06bc  */
        /* JADX WARNING: Removed duplicated region for block: B:677:0x096d A:{SKIP} */
        /* JADX WARNING: Removed duplicated region for block: B:682:0x097e  */
        /* JADX WARNING: Removed duplicated region for block: B:450:0x06bc  */
        /* JADX WARNING: Removed duplicated region for block: B:305:0x04b3 A:{SYNTHETIC, Splitter:B:305:0x04b3} */
        /* JADX WARNING: Removed duplicated region for block: B:677:0x096d A:{SKIP} */
        /* JADX WARNING: Removed duplicated region for block: B:682:0x097e  */
        /* JADX WARNING: Removed duplicated region for block: B:677:0x096d A:{SKIP} */
        /* JADX WARNING: Removed duplicated region for block: B:682:0x097e  */
        /* JADX WARNING: Missing block: B:8:0x0015, code skipped:
            r0 = r1.cacheImage;
            r7 = false;
     */
        /* JADX WARNING: Missing block: B:9:0x0021, code skipped:
            if ((r0.imageLocation.photoSize instanceof org.telegram.tgnet.TLRPC.TL_photoStrippedSize) == false) goto L_0x00ca;
     */
        /* JADX WARNING: Missing block: B:10:0x0023, code skipped:
            r2 = r1.sync;
     */
        /* JADX WARNING: Missing block: B:11:0x0025, code skipped:
            monitor-enter(r2);
     */
        /* JADX WARNING: Missing block: B:14:0x0028, code skipped:
            if (r1.isCancelled == false) goto L_0x002c;
     */
        /* JADX WARNING: Missing block: B:15:0x002a, code skipped:
            monitor-exit(r2);
     */
        /* JADX WARNING: Missing block: B:16:0x002b, code skipped:
            return;
     */
        /* JADX WARNING: Missing block: B:17:0x002c, code skipped:
            monitor-exit(r2);
     */
        /* JADX WARNING: Missing block: B:18:0x002d, code skipped:
            r0 = (org.telegram.tgnet.TLRPC.TL_photoStrippedSize) r1.cacheImage.imageLocation.photoSize;
            r2 = ((r0.bytes.length - 3) + org.telegram.messenger.Bitmaps.header.length) + org.telegram.messenger.Bitmaps.footer.length;
            r8 = (byte[]) org.telegram.messenger.ImageLoader.access$1700().get();
     */
        /* JADX WARNING: Missing block: B:19:0x004b, code skipped:
            if (r8 == null) goto L_0x0051;
     */
        /* JADX WARNING: Missing block: B:21:0x004e, code skipped:
            if (r8.length < r2) goto L_0x0051;
     */
        /* JADX WARNING: Missing block: B:22:0x0051, code skipped:
            r8 = null;
     */
        /* JADX WARNING: Missing block: B:23:0x0052, code skipped:
            if (r8 != null) goto L_0x005d;
     */
        /* JADX WARNING: Missing block: B:24:0x0054, code skipped:
            r8 = new byte[r2];
            org.telegram.messenger.ImageLoader.access$1700().set(r8);
     */
        /* JADX WARNING: Missing block: B:25:0x005d, code skipped:
            r9 = org.telegram.messenger.Bitmaps.header;
            java.lang.System.arraycopy(r9, 0, r8, 0, r9.length);
            r9 = r0.bytes;
            java.lang.System.arraycopy(r9, 3, r8, org.telegram.messenger.Bitmaps.header.length, r9.length - 3);
            java.lang.System.arraycopy(org.telegram.messenger.Bitmaps.footer, 0, r8, (org.telegram.messenger.Bitmaps.header.length + r0.bytes.length) - 3, org.telegram.messenger.Bitmaps.footer.length);
            r0 = r0.bytes;
            r8[164] = r0[1];
            r8[166] = r0[2];
            r0 = android.graphics.BitmapFactory.decodeByteArray(r8, 0, r2);
     */
        /* JADX WARNING: Missing block: B:26:0x008f, code skipped:
            if (r0 == null) goto L_0x00b9;
     */
        /* JADX WARNING: Missing block: B:28:0x0099, code skipped:
            if (android.text.TextUtils.isEmpty(r1.cacheImage.filter) != false) goto L_0x00b9;
     */
        /* JADX WARNING: Missing block: B:30:0x00a5, code skipped:
            if (r1.cacheImage.filter.contains("b") == false) goto L_0x00b9;
     */
        /* JADX WARNING: Missing block: B:31:0x00a7, code skipped:
            org.telegram.messenger.Utilities.blurBitmap(r0, 3, 1, r0.getWidth(), r0.getHeight(), r0.getRowBytes());
     */
        /* JADX WARNING: Missing block: B:32:0x00b9, code skipped:
            if (r0 == null) goto L_0x00c1;
     */
        /* JADX WARNING: Missing block: B:33:0x00bb, code skipped:
            r5 = new android.graphics.drawable.BitmapDrawable(r0);
     */
        /* JADX WARNING: Missing block: B:34:0x00c1, code skipped:
            r5 = null;
     */
        /* JADX WARNING: Missing block: B:35:0x00c2, code skipped:
            onPostExecute(r5);
     */
        /* JADX WARNING: Missing block: B:41:0x00cc, code skipped:
            if (r0.lottieFile == false) goto L_0x0153;
     */
        /* JADX WARNING: Missing block: B:42:0x00ce, code skipped:
            r2 = r1.sync;
     */
        /* JADX WARNING: Missing block: B:43:0x00d0, code skipped:
            monitor-enter(r2);
     */
        /* JADX WARNING: Missing block: B:46:0x00d3, code skipped:
            if (r1.isCancelled == false) goto L_0x00d7;
     */
        /* JADX WARNING: Missing block: B:47:0x00d5, code skipped:
            monitor-exit(r2);
     */
        /* JADX WARNING: Missing block: B:48:0x00d6, code skipped:
            return;
     */
        /* JADX WARNING: Missing block: B:49:0x00d7, code skipped:
            monitor-exit(r2);
     */
        /* JADX WARNING: Missing block: B:50:0x00d8, code skipped:
            r2 = java.lang.Math.min(512, org.telegram.messenger.AndroidUtilities.dp(170.6f));
            r0 = java.lang.Math.min(512, org.telegram.messenger.AndroidUtilities.dp(170.6f));
            r5 = r1.cacheImage.filter;
     */
        /* JADX WARNING: Missing block: B:51:0x00f1, code skipped:
            if (r5 == null) goto L_0x013d;
     */
        /* JADX WARNING: Missing block: B:52:0x00f3, code skipped:
            r5 = r5.split("_");
     */
        /* JADX WARNING: Missing block: B:53:0x00fa, code skipped:
            if (r5.length < 2) goto L_0x013d;
     */
        /* JADX WARNING: Missing block: B:54:0x00fc, code skipped:
            r0 = java.lang.Float.parseFloat(r5[0]);
            r2 = java.lang.Float.parseFloat(r5[1]);
            r5 = java.lang.Math.min(512, (int) (org.telegram.messenger.AndroidUtilities.density * r0));
            r3 = java.lang.Math.min(512, (int) (org.telegram.messenger.AndroidUtilities.density * r2));
     */
        /* JADX WARNING: Missing block: B:55:0x011e, code skipped:
            if (r0 > 90.0f) goto L_0x013a;
     */
        /* JADX WARNING: Missing block: B:57:0x0122, code skipped:
            if (r2 > 90.0f) goto L_0x013a;
     */
        /* JADX WARNING: Missing block: B:58:0x0124, code skipped:
            r2 = java.lang.Math.min(r5, 160);
            r0 = java.lang.Math.min(r3, 160);
     */
        /* JADX WARNING: Missing block: B:59:0x0132, code skipped:
            if (org.telegram.messenger.SharedConfig.getDevicePerfomanceClass() == 2) goto L_0x0135;
     */
        /* JADX WARNING: Missing block: B:60:0x0134, code skipped:
            r7 = true;
     */
        /* JADX WARNING: Missing block: B:61:0x0135, code skipped:
            r11 = r0;
            r10 = r2;
            r12 = r7;
            r13 = true;
     */
        /* JADX WARNING: Missing block: B:62:0x013a, code skipped:
            r11 = r3;
            r10 = r5;
     */
        /* JADX WARNING: Missing block: B:63:0x013d, code skipped:
            r11 = r0;
            r10 = r2;
     */
        /* JADX WARNING: Missing block: B:64:0x013f, code skipped:
            r12 = false;
            r13 = false;
     */
        /* JADX WARNING: Missing block: B:65:0x0141, code skipped:
            onPostExecute(new org.telegram.ui.Components.RLottieDrawable(r1.cacheImage.finalFilePath, r10, r11, r12, r13));
     */
        /* JADX WARNING: Missing block: B:71:0x0155, code skipped:
            if (r0.animatedFile == false) goto L_0x01bb;
     */
        /* JADX WARNING: Missing block: B:72:0x0157, code skipped:
            r2 = r1.sync;
     */
        /* JADX WARNING: Missing block: B:73:0x0159, code skipped:
            monitor-enter(r2);
     */
        /* JADX WARNING: Missing block: B:76:0x015c, code skipped:
            if (r1.isCancelled == false) goto L_0x0160;
     */
        /* JADX WARNING: Missing block: B:77:0x015e, code skipped:
            monitor-exit(r2);
     */
        /* JADX WARNING: Missing block: B:78:0x015f, code skipped:
            return;
     */
        /* JADX WARNING: Missing block: B:79:0x0160, code skipped:
            monitor-exit(r2);
     */
        /* JADX WARNING: Missing block: B:81:0x016b, code skipped:
            if ("g".equals(r1.cacheImage.filter) == false) goto L_0x0192;
     */
        /* JADX WARNING: Missing block: B:82:0x016d, code skipped:
            r0 = r1.cacheImage;
            r2 = r0.imageLocation.document;
     */
        /* JADX WARNING: Missing block: B:83:0x0175, code skipped:
            if ((r2 instanceof org.telegram.tgnet.TLRPC.TL_documentEncrypted) != false) goto L_0x0192;
     */
        /* JADX WARNING: Missing block: B:84:0x0177, code skipped:
            r7 = r0.finalFilePath;
            r9 = (long) r0.size;
     */
        /* JADX WARNING: Missing block: B:85:0x0181, code skipped:
            if ((r2 instanceof org.telegram.tgnet.TLRPC.Document) == false) goto L_0x0185;
     */
        /* JADX WARNING: Missing block: B:86:0x0183, code skipped:
            r11 = r2;
     */
        /* JADX WARNING: Missing block: B:87:0x0185, code skipped:
            r11 = null;
     */
        /* JADX WARNING: Missing block: B:88:0x0186, code skipped:
            r0 = r1.cacheImage;
            r0 = new org.telegram.ui.Components.AnimatedFileDrawable(r7, false, r9, r11, r0.parentObject, r0.currentAccount);
     */
        /* JADX WARNING: Missing block: B:89:0x0192, code skipped:
            r2 = r1.cacheImage;
            r14 = new org.telegram.ui.Components.AnimatedFileDrawable(r2.finalFilePath, "d".equals(r2.filter), 0, null, null, r1.cacheImage.currentAccount);
     */
        /* JADX WARNING: Missing block: B:90:0x01b0, code skipped:
            java.lang.Thread.interrupted();
            onPostExecute(r0);
     */
        /* JADX WARNING: Missing block: B:95:0x01bb, code skipped:
            r2 = r0.finalFilePath;
     */
        /* JADX WARNING: Missing block: B:96:0x01bf, code skipped:
            if (r0.secureDocument != null) goto L_0x01d6;
     */
        /* JADX WARNING: Missing block: B:98:0x01c3, code skipped:
            if (r0.encryptionKeyPath == null) goto L_0x01d4;
     */
        /* JADX WARNING: Missing block: B:99:0x01c5, code skipped:
            if (r2 == null) goto L_0x01d4;
     */
        /* JADX WARNING: Missing block: B:101:0x01d1, code skipped:
            if (r2.getAbsolutePath().endsWith(".enc") == false) goto L_0x01d4;
     */
        /* JADX WARNING: Missing block: B:102:0x01d4, code skipped:
            r8 = null;
     */
        /* JADX WARNING: Missing block: B:103:0x01d6, code skipped:
            r8 = 1;
     */
        /* JADX WARNING: Missing block: B:104:0x01d7, code skipped:
            r0 = r1.cacheImage.secureDocument;
     */
        /* JADX WARNING: Missing block: B:105:0x01db, code skipped:
            if (r0 == null) goto L_0x01f0;
     */
        /* JADX WARNING: Missing block: B:106:0x01dd, code skipped:
            r9 = r0.secureDocumentKey;
            r0 = r0.secureFile;
     */
        /* JADX WARNING: Missing block: B:107:0x01e1, code skipped:
            if (r0 == null) goto L_0x01e8;
     */
        /* JADX WARNING: Missing block: B:108:0x01e3, code skipped:
            r0 = r0.file_hash;
     */
        /* JADX WARNING: Missing block: B:109:0x01e5, code skipped:
            if (r0 == null) goto L_0x01e8;
     */
        /* JADX WARNING: Missing block: B:110:0x01e8, code skipped:
            r0 = r1.cacheImage.secureDocument.fileHash;
     */
        /* JADX WARNING: Missing block: B:111:0x01ee, code skipped:
            r10 = r0;
     */
        /* JADX WARNING: Missing block: B:112:0x01f0, code skipped:
            r9 = null;
            r10 = null;
     */
        /* JADX WARNING: Missing block: B:114:0x01f6, code skipped:
            if (android.os.Build.VERSION.SDK_INT >= 19) goto L_0x0261;
     */
        /* JADX WARNING: Missing block: B:116:?, code skipped:
            r11 = new java.io.RandomAccessFile(r2, "r");
     */
        /* JADX WARNING: Missing block: B:119:0x0203, code skipped:
            if (r1.cacheImage.imageType != 1) goto L_0x020a;
     */
        /* JADX WARNING: Missing block: B:120:0x0205, code skipped:
            r0 = org.telegram.messenger.ImageLoader.access$1800();
     */
        /* JADX WARNING: Missing block: B:121:0x020a, code skipped:
            r0 = org.telegram.messenger.ImageLoader.access$1900();
     */
        /* JADX WARNING: Missing block: B:122:0x020e, code skipped:
            r11.readFully(r0, 0, r0.length);
            r0 = new java.lang.String(r0).toLowerCase().toLowerCase();
     */
        /* JADX WARNING: Missing block: B:123:0x0225, code skipped:
            if (r0.startsWith("riff") == false) goto L_0x0231;
     */
        /* JADX WARNING: Missing block: B:125:0x022d, code skipped:
            if (r0.endsWith("webp") == false) goto L_0x0231;
     */
        /* JADX WARNING: Missing block: B:126:0x022f, code skipped:
            r12 = 1;
     */
        /* JADX WARNING: Missing block: B:127:0x0231, code skipped:
            r12 = null;
     */
        /* JADX WARNING: Missing block: B:129:?, code skipped:
            r11.close();
     */
        /* JADX WARNING: Missing block: B:131:?, code skipped:
            r11.close();
     */
        /* JADX WARNING: Missing block: B:132:0x0239, code skipped:
            r0 = move-exception;
     */
        /* JADX WARNING: Missing block: B:133:0x023a, code skipped:
            org.telegram.messenger.FileLog.e(r0);
     */
        /* JADX WARNING: Missing block: B:134:0x023f, code skipped:
            r0 = e;
     */
        /* JADX WARNING: Missing block: B:135:0x0241, code skipped:
            r0 = e;
     */
        /* JADX WARNING: Missing block: B:136:0x0243, code skipped:
            r0 = move-exception;
     */
        /* JADX WARNING: Missing block: B:137:0x0244, code skipped:
            r2 = r0;
            r11 = null;
     */
        /* JADX WARNING: Missing block: B:138:0x0247, code skipped:
            r0 = e;
     */
        /* JADX WARNING: Missing block: B:139:0x0248, code skipped:
            r11 = null;
     */
        /* JADX WARNING: Missing block: B:140:0x0249, code skipped:
            r12 = null;
     */
        /* JADX WARNING: Missing block: B:142:?, code skipped:
            org.telegram.messenger.FileLog.e(r0);
     */
        /* JADX WARNING: Missing block: B:143:0x024d, code skipped:
            if (r11 != null) goto L_0x024f;
     */
        /* JADX WARNING: Missing block: B:145:?, code skipped:
            r11.close();
     */
        /* JADX WARNING: Missing block: B:146:0x0253, code skipped:
            r0 = move-exception;
     */
        /* JADX WARNING: Missing block: B:147:0x0254, code skipped:
            r2 = r0;
     */
        /* JADX WARNING: Missing block: B:148:0x0255, code skipped:
            if (r11 != null) goto L_0x0257;
     */
        /* JADX WARNING: Missing block: B:150:?, code skipped:
            r11.close();
     */
        /* JADX WARNING: Missing block: B:151:0x025b, code skipped:
            r0 = move-exception;
     */
        /* JADX WARNING: Missing block: B:152:0x025c, code skipped:
            org.telegram.messenger.FileLog.e(r0);
     */
        /* JADX WARNING: Missing block: B:153:0x0260, code skipped:
            throw r2;
     */
        /* JADX WARNING: Missing block: B:154:0x0261, code skipped:
            r12 = null;
     */
        /* JADX WARNING: Missing block: B:155:0x0262, code skipped:
            r0 = r1.cacheImage.imageLocation.path;
     */
        /* JADX WARNING: Missing block: B:156:0x026a, code skipped:
            if (r0 != null) goto L_0x026c;
     */
        /* JADX WARNING: Missing block: B:158:0x0272, code skipped:
            if (r0.startsWith("thumb://") != false) goto L_0x0274;
     */
        /* JADX WARNING: Missing block: B:159:0x0274, code skipped:
            r13 = r0.indexOf(":", 8);
     */
        /* JADX WARNING: Missing block: B:160:0x027a, code skipped:
            if (r13 >= 0) goto L_0x027c;
     */
        /* JADX WARNING: Missing block: B:161:0x027c, code skipped:
            r14 = java.lang.Long.valueOf(java.lang.Long.parseLong(r0.substring(8, r13)));
            r0 = r0.substring(r13 + 1);
     */
        /* JADX WARNING: Missing block: B:162:0x028e, code skipped:
            r0 = null;
            r14 = null;
     */
        /* JADX WARNING: Missing block: B:163:0x0290, code skipped:
            r13 = r0;
     */
        /* JADX WARNING: Missing block: B:164:0x0291, code skipped:
            r15 = null;
     */
        /* JADX WARNING: Missing block: B:165:0x0292, code skipped:
            r16 = null;
     */
        /* JADX WARNING: Missing block: B:167:0x029b, code skipped:
            if (r0.startsWith("vthumb://") != false) goto L_0x029d;
     */
        /* JADX WARNING: Missing block: B:168:0x029d, code skipped:
            r14 = r0.indexOf(":", 9);
     */
        /* JADX WARNING: Missing block: B:169:0x02a5, code skipped:
            if (r14 >= 0) goto L_0x02a7;
     */
        /* JADX WARNING: Missing block: B:170:0x02a7, code skipped:
            r0 = java.lang.Long.valueOf(java.lang.Long.parseLong(r0.substring(9, r14)));
            r13 = 1;
     */
        /* JADX WARNING: Missing block: B:171:0x02b5, code skipped:
            r0 = null;
            r13 = null;
     */
        /* JADX WARNING: Missing block: B:172:0x02b7, code skipped:
            r14 = r0;
            r15 = r13;
            r13 = null;
     */
        /* JADX WARNING: Missing block: B:174:0x02c1, code skipped:
            if (r0.startsWith("http") == false) goto L_0x02c3;
     */
        /* JADX WARNING: Missing block: B:175:0x02c3, code skipped:
            r13 = null;
            r14 = null;
     */
        /* JADX WARNING: Missing block: B:176:0x02c6, code skipped:
            r13 = null;
            r14 = null;
            r15 = null;
            r16 = 1;
     */
        /* JADX WARNING: Missing block: B:177:0x02cb, code skipped:
            r11 = new android.graphics.BitmapFactory.Options();
            r11.inSampleSize = 1;
     */
        /* JADX WARNING: Missing block: B:178:0x02d6, code skipped:
            if (android.os.Build.VERSION.SDK_INT < 21) goto L_0x02d8;
     */
        /* JADX WARNING: Missing block: B:179:0x02d8, code skipped:
            r11.inPurgeable = true;
     */
        /* JADX WARNING: Missing block: B:180:0x02da, code skipped:
            r19 = org.telegram.messenger.ImageLoader.access$2000(r1.this$0);
     */
        /* JADX WARNING: Missing block: B:183:0x02e8, code skipped:
            if (r1.cacheImage.filter != null) goto L_0x02ea;
     */
        /* JADX WARNING: Missing block: B:184:0x02ea, code skipped:
            r0 = r1.cacheImage.filter.split("_");
     */
        /* JADX WARNING: Missing block: B:185:0x02f5, code skipped:
            if (r0.length >= 2) goto L_0x02f7;
     */
        /* JADX WARNING: Missing block: B:187:0x02ff, code skipped:
            r3 = java.lang.Float.parseFloat(r0[0]) * org.telegram.messenger.AndroidUtilities.density;
     */
        /* JADX WARNING: Missing block: B:190:0x0309, code skipped:
            r22 = java.lang.Float.parseFloat(r0[1]) * org.telegram.messenger.AndroidUtilities.density;
     */
        /* JADX WARNING: Missing block: B:191:0x030e, code skipped:
            r0 = th;
     */
        /* JADX WARNING: Missing block: B:192:0x0311, code skipped:
            r3 = 0.0f;
            r22 = 0.0f;
     */
        /* JADX WARNING: Missing block: B:195:0x031e, code skipped:
            if (r1.cacheImage.filter.contains("b2") != false) goto L_0x0320;
     */
        /* JADX WARNING: Missing block: B:196:0x0320, code skipped:
            r4 = 3;
     */
        /* JADX WARNING: Missing block: B:198:0x032c, code skipped:
            if (r1.cacheImage.filter.contains("b1") != false) goto L_0x032e;
     */
        /* JADX WARNING: Missing block: B:199:0x032e, code skipped:
            r4 = 2;
     */
        /* JADX WARNING: Missing block: B:201:0x033a, code skipped:
            if (r1.cacheImage.filter.contains("b") != false) goto L_0x033c;
     */
        /* JADX WARNING: Missing block: B:202:0x033c, code skipped:
            r4 = true;
     */
        /* JADX WARNING: Missing block: B:203:0x033e, code skipped:
            r4 = null;
     */
        /* JADX WARNING: Missing block: B:205:?, code skipped:
            r5 = r1.cacheImage.filter.contains("i");
     */
        /* JADX WARNING: Missing block: B:207:?, code skipped:
            r7 = "f";
     */
        /* JADX WARNING: Missing block: B:208:0x0353, code skipped:
            if (r1.cacheImage.filter.contains(r7) != false) goto L_0x0355;
     */
        /* JADX WARNING: Missing block: B:209:0x0355, code skipped:
            r19 = true;
     */
        /* JADX WARNING: Missing block: B:210:0x0357, code skipped:
            if (r12 != null) goto L_0x0435;
     */
        /* JADX WARNING: Missing block: B:215:0x0361, code skipped:
            r11.inJustDecodeBounds = true;
     */
        /* JADX WARNING: Missing block: B:216:0x0363, code skipped:
            if (r14 == null) goto L_0x0394;
     */
        /* JADX WARNING: Missing block: B:218:0x0367, code skipped:
            if (r15 != null) goto L_0x0369;
     */
        /* JADX WARNING: Missing block: B:221:0x036f, code skipped:
            r7 = r4;
            r25 = r5;
     */
        /* JADX WARNING: Missing block: B:223:?, code skipped:
            android.provider.MediaStore.Video.Thumbnails.getThumbnail(org.telegram.messenger.ApplicationLoader.applicationContext.getContentResolver(), r14.longValue(), 1, r11);
     */
        /* JADX WARNING: Missing block: B:224:0x037a, code skipped:
            r0 = th;
     */
        /* JADX WARNING: Missing block: B:225:0x037b, code skipped:
            r7 = r4;
            r25 = r5;
     */
        /* JADX WARNING: Missing block: B:226:0x0380, code skipped:
            r7 = r4;
            r25 = r5;
            android.provider.MediaStore.Images.Thumbnails.getThumbnail(org.telegram.messenger.ApplicationLoader.applicationContext.getContentResolver(), r14.longValue(), 1, r11);
     */
        /* JADX WARNING: Missing block: B:227:0x0390, code skipped:
            r26 = r7;
     */
        /* JADX WARNING: Missing block: B:228:0x0394, code skipped:
            r7 = r4;
            r25 = r5;
     */
        /* JADX WARNING: Missing block: B:229:0x0397, code skipped:
            if (r9 != null) goto L_0x0399;
     */
        /* JADX WARNING: Missing block: B:230:0x0399, code skipped:
            r0 = new java.io.RandomAccessFile(r2, "r");
            r5 = (int) r0.length();
            r4 = (byte[]) org.telegram.messenger.ImageLoader.access$1700().get();
     */
        /* JADX WARNING: Missing block: B:231:0x03af, code skipped:
            if (r4 == null) goto L_0x03b5;
     */
        /* JADX WARNING: Missing block: B:235:0x03b5, code skipped:
            r4 = null;
     */
        /* JADX WARNING: Missing block: B:236:0x03b6, code skipped:
            if (r4 == null) goto L_0x03b8;
     */
        /* JADX WARNING: Missing block: B:237:0x03b8, code skipped:
            r4 = new byte[r5];
            org.telegram.messenger.ImageLoader.access$1700().set(r4);
     */
        /* JADX WARNING: Missing block: B:238:0x03c1, code skipped:
            r0.readFully(r4, 0, r5);
            r0.close();
            org.telegram.messenger.secretmedia.EncryptedFileInputStream.decryptBytesWithKeyFile(r4, 0, r5, r9);
            r0 = org.telegram.messenger.Utilities.computeSHA256(r4, 0, r5);
     */
        /* JADX WARNING: Missing block: B:239:0x03cf, code skipped:
            if (r10 == null) goto L_0x03dc;
     */
        /* JADX WARNING: Missing block: B:242:0x03d8, code skipped:
            r26 = r7;
            r0 = null;
     */
        /* JADX WARNING: Missing block: B:243:0x03dc, code skipped:
            r26 = r7;
            r0 = 1;
     */
        /* JADX WARNING: Missing block: B:246:?, code skipped:
            r6 = r4[0] & 255;
            r5 = r5 - r6;
     */
        /* JADX WARNING: Missing block: B:247:0x03e5, code skipped:
            if (r0 == null) goto L_0x03e7;
     */
        /* JADX WARNING: Missing block: B:248:0x03e7, code skipped:
            android.graphics.BitmapFactory.decodeByteArray(r4, r6, r5, r11);
     */
        /* JADX WARNING: Missing block: B:249:0x03eb, code skipped:
            r0 = th;
     */
        /* JADX WARNING: Missing block: B:250:0x03ec, code skipped:
            r26 = r7;
     */
        /* JADX WARNING: Missing block: B:251:0x03ef, code skipped:
            r26 = r7;
     */
        /* JADX WARNING: Missing block: B:252:0x03f1, code skipped:
            if (r8 != null) goto L_0x03f3;
     */
        /* JADX WARNING: Missing block: B:253:0x03f3, code skipped:
            r0 = new org.telegram.messenger.secretmedia.EncryptedFileInputStream(r2, r1.cacheImage.encryptionKeyPath);
     */
        /* JADX WARNING: Missing block: B:254:0x03fd, code skipped:
            r0 = new java.io.FileInputStream(r2);
     */
        /* JADX WARNING: Missing block: B:255:0x0402, code skipped:
            android.graphics.BitmapFactory.decodeStream(r0, null, r11);
            r0.close();
     */
        /* JADX WARNING: Missing block: B:256:0x0409, code skipped:
            r0 = (float) r11.outWidth;
            r4 = (float) r11.outHeight;
     */
        /* JADX WARNING: Missing block: B:257:0x0411, code skipped:
            if (r3 <= r22) goto L_0x041f;
     */
        /* JADX WARNING: Missing block: B:260:0x0417, code skipped:
            r0 = java.lang.Math.max(r0 / r3, r4 / r22);
     */
        /* JADX WARNING: Missing block: B:261:0x041f, code skipped:
            r0 = java.lang.Math.min(r0 / r3, r4 / r22);
     */
        /* JADX WARNING: Missing block: B:263:0x0428, code skipped:
            if (r0 < 1.0f) goto L_0x042a;
     */
        /* JADX WARNING: Missing block: B:264:0x042a, code skipped:
            r0 = 1.0f;
     */
        /* JADX WARNING: Missing block: B:265:0x042c, code skipped:
            r11.inJustDecodeBounds = false;
            r11.inSampleSize = (int) r0;
     */
        /* JADX WARNING: Missing block: B:266:0x0433, code skipped:
            r0 = th;
     */
        /* JADX WARNING: Missing block: B:267:0x0435, code skipped:
            r26 = r4;
            r25 = r5;
     */
        /* JADX WARNING: Missing block: B:268:0x0439, code skipped:
            r5 = null;
     */
        /* JADX WARNING: Missing block: B:269:0x043c, code skipped:
            r0 = th;
     */
        /* JADX WARNING: Missing block: B:270:0x043d, code skipped:
            r26 = r4;
            r25 = r5;
     */
        /* JADX WARNING: Missing block: B:271:0x0441, code skipped:
            r5 = null;
     */
        /* JADX WARNING: Missing block: B:272:0x0443, code skipped:
            r0 = th;
     */
        /* JADX WARNING: Missing block: B:273:0x0444, code skipped:
            r26 = r4;
            r5 = null;
            r25 = false;
     */
        /* JADX WARNING: Missing block: B:274:0x044a, code skipped:
            r0 = th;
     */
        /* JADX WARNING: Missing block: B:275:0x044b, code skipped:
            r5 = null;
     */
        /* JADX WARNING: Missing block: B:276:0x044d, code skipped:
            if (r13 != null) goto L_0x044f;
     */
        /* JADX WARNING: Missing block: B:279:?, code skipped:
            r11.inJustDecodeBounds = true;
     */
        /* JADX WARNING: Missing block: B:280:0x0452, code skipped:
            if (r19 != false) goto L_0x0454;
     */
        /* JADX WARNING: Missing block: B:281:0x0454, code skipped:
            r0 = android.graphics.Bitmap.Config.ARGB_8888;
     */
        /* JADX WARNING: Missing block: B:282:0x0457, code skipped:
            r0 = android.graphics.Bitmap.Config.RGB_565;
     */
        /* JADX WARNING: Missing block: B:283:0x0459, code skipped:
            r11.inPreferredConfig = r0;
            r0 = new java.io.FileInputStream(r2);
            r5 = android.graphics.BitmapFactory.decodeStream(r0, null, r11);
     */
        /* JADX WARNING: Missing block: B:285:?, code skipped:
            r0.close();
            r0 = r11.outWidth;
            r3 = r11.outHeight;
            r11.inJustDecodeBounds = false;
            r0 = (float) java.lang.Math.max(r0 / 200, r3 / 200);
     */
        /* JADX WARNING: Missing block: B:286:0x047a, code skipped:
            if (r0 < 1.0f) goto L_0x047c;
     */
        /* JADX WARNING: Missing block: B:287:0x047c, code skipped:
            r0 = 1.0f;
     */
        /* JADX WARNING: Missing block: B:288:0x047e, code skipped:
            r3 = 1;
     */
        /* JADX WARNING: Missing block: B:289:0x047f, code skipped:
            r3 = r3 * 2;
     */
        /* JADX WARNING: Missing block: B:290:0x0487, code skipped:
            if (((float) (r3 * 2)) < r0) goto L_0x047f;
     */
        /* JADX WARNING: Missing block: B:291:0x0489, code skipped:
            r11.inSampleSize = r3;
     */
        /* JADX WARNING: Missing block: B:292:0x048b, code skipped:
            r3 = 0.0f;
     */
        /* JADX WARNING: Missing block: B:293:0x048d, code skipped:
            r0 = th;
     */
        /* JADX WARNING: Missing block: B:294:0x048e, code skipped:
            r3 = 0.0f;
     */
        /* JADX WARNING: Missing block: B:295:0x0490, code skipped:
            r3 = 0.0f;
            r5 = null;
     */
        /* JADX WARNING: Missing block: B:296:0x0492, code skipped:
            r22 = 0.0f;
            r25 = false;
            r26 = null;
     */
        /* JADX WARNING: Missing block: B:297:0x0499, code skipped:
            r0 = th;
     */
        /* JADX WARNING: Missing block: B:298:0x049a, code skipped:
            r3 = 0.0f;
     */
        /* JADX WARNING: Missing block: B:299:0x049b, code skipped:
            r5 = null;
     */
        /* JADX WARNING: Missing block: B:300:0x049c, code skipped:
            r22 = 0.0f;
     */
        /* JADX WARNING: Missing block: B:301:0x049e, code skipped:
            r25 = false;
            r26 = null;
     */
        /* JADX WARNING: Missing block: B:302:0x04a2, code skipped:
            org.telegram.messenger.FileLog.e(r0);
     */
        /* JADX WARNING: Missing block: B:303:0x04a5, code skipped:
            r0 = r26;
            r21 = r22;
            r7 = r5;
     */
        /* JADX WARNING: Missing block: B:304:0x04b1, code skipped:
            if (r1.cacheImage.imageType != 1) goto L_0x06bc;
     */
        /* JADX WARNING: Missing block: B:306:?, code skipped:
            org.telegram.messenger.ImageLoader.access$2102(r1.this$0, java.lang.System.currentTimeMillis());
     */
        /* JADX WARNING: Missing block: B:307:0x04be, code skipped:
            monitor-enter(r1.sync);
     */
        /* JADX WARNING: Missing block: B:310:0x04c1, code skipped:
            if (r1.isCancelled != false) goto L_0x04c3;
     */
        /* JADX WARNING: Missing block: B:312:0x04c4, code skipped:
            return;
     */
        /* JADX WARNING: Missing block: B:314:0x04c6, code skipped:
            if (r12 == null) goto L_0x050c;
     */
        /* JADX WARNING: Missing block: B:316:?, code skipped:
            r4 = new java.io.RandomAccessFile(r2, "r");
            r5 = r4.getChannel().map(java.nio.channels.FileChannel.MapMode.READ_ONLY, 0, r2.length());
            r6 = new android.graphics.BitmapFactory.Options();
            r6.inJustDecodeBounds = true;
            org.telegram.messenger.Utilities.loadWebpImage(null, r5, r5.limit(), r6, true);
            r6 = org.telegram.messenger.Bitmaps.createBitmap(r6.outWidth, r6.outHeight, android.graphics.Bitmap.Config.ARGB_8888);
     */
        /* JADX WARNING: Missing block: B:318:?, code skipped:
            r7 = r5.limit();
     */
        /* JADX WARNING: Missing block: B:319:0x04ff, code skipped:
            if (r11.inPurgeable != false) goto L_0x0503;
     */
        /* JADX WARNING: Missing block: B:320:0x0501, code skipped:
            r8 = true;
     */
        /* JADX WARNING: Missing block: B:321:0x0503, code skipped:
            r8 = false;
     */
        /* JADX WARNING: Missing block: B:322:0x0504, code skipped:
            org.telegram.messenger.Utilities.loadWebpImage(r6, r5, r7, null, r8);
            r4.close();
     */
        /* JADX WARNING: Missing block: B:325:0x050e, code skipped:
            if (r11.inPurgeable != false) goto L_0x0532;
     */
        /* JADX WARNING: Missing block: B:326:0x0510, code skipped:
            if (r9 == null) goto L_0x0513;
     */
        /* JADX WARNING: Missing block: B:328:0x0513, code skipped:
            if (r8 == null) goto L_0x051f;
     */
        /* JADX WARNING: Missing block: B:329:0x0515, code skipped:
            r4 = new org.telegram.messenger.secretmedia.EncryptedFileInputStream(r2, r1.cacheImage.encryptionKeyPath);
     */
        /* JADX WARNING: Missing block: B:330:0x051f, code skipped:
            r4 = new java.io.FileInputStream(r2);
     */
        /* JADX WARNING: Missing block: B:331:0x0524, code skipped:
            r6 = android.graphics.BitmapFactory.decodeStream(r4, null, r11);
     */
        /* JADX WARNING: Missing block: B:333:?, code skipped:
            r4.close();
     */
        /* JADX WARNING: Missing block: B:334:0x052c, code skipped:
            r5 = r6;
     */
        /* JADX WARNING: Missing block: B:335:0x052e, code skipped:
            r0 = th;
     */
        /* JADX WARNING: Missing block: B:336:0x052f, code skipped:
            r2 = r6;
     */
        /* JADX WARNING: Missing block: B:338:?, code skipped:
            r4 = new java.io.RandomAccessFile(r2, "r");
            r6 = (int) r4.length();
            r5 = (byte[]) org.telegram.messenger.ImageLoader.access$2200().get();
     */
        /* JADX WARNING: Missing block: B:339:0x0548, code skipped:
            if (r5 == null) goto L_0x054e;
     */
        /* JADX WARNING: Missing block: B:341:0x054b, code skipped:
            if (r5.length < r6) goto L_0x054e;
     */
        /* JADX WARNING: Missing block: B:343:0x054e, code skipped:
            r5 = null;
     */
        /* JADX WARNING: Missing block: B:344:0x054f, code skipped:
            if (r5 != null) goto L_0x055a;
     */
        /* JADX WARNING: Missing block: B:345:0x0551, code skipped:
            r5 = new byte[r6];
            org.telegram.messenger.ImageLoader.access$2200().set(r5);
     */
        /* JADX WARNING: Missing block: B:346:0x055a, code skipped:
            r4.readFully(r5, 0, r6);
            r4.close();
     */
        /* JADX WARNING: Missing block: B:347:0x0561, code skipped:
            if (r9 == null) goto L_0x057d;
     */
        /* JADX WARNING: Missing block: B:348:0x0563, code skipped:
            org.telegram.messenger.secretmedia.EncryptedFileInputStream.decryptBytesWithKeyFile(r5, 0, r6, r9);
            r4 = org.telegram.messenger.Utilities.computeSHA256(r5, 0, r6);
     */
        /* JADX WARNING: Missing block: B:349:0x056a, code skipped:
            if (r10 == null) goto L_0x0575;
     */
        /* JADX WARNING: Missing block: B:351:0x0570, code skipped:
            if (java.util.Arrays.equals(r4, r10) != false) goto L_0x0573;
     */
        /* JADX WARNING: Missing block: B:353:0x0573, code skipped:
            r4 = null;
     */
        /* JADX WARNING: Missing block: B:354:0x0575, code skipped:
            r4 = 1;
     */
        /* JADX WARNING: Missing block: B:355:0x0576, code skipped:
            r8 = r5[0] & 255;
            r6 = r6 - r8;
     */
        /* JADX WARNING: Missing block: B:356:0x057d, code skipped:
            if (r8 == null) goto L_0x0587;
     */
        /* JADX WARNING: Missing block: B:357:0x057f, code skipped:
            org.telegram.messenger.secretmedia.EncryptedFileInputStream.decryptBytesWithKeyFile(r5, 0, r6, r1.cacheImage.encryptionKeyPath);
     */
        /* JADX WARNING: Missing block: B:358:0x0587, code skipped:
            r4 = null;
            r8 = 0;
     */
        /* JADX WARNING: Missing block: B:359:0x0589, code skipped:
            if (r4 != null) goto L_0x0590;
     */
        /* JADX WARNING: Missing block: B:360:0x058b, code skipped:
            r5 = android.graphics.BitmapFactory.decodeByteArray(r5, r8, r6, r11);
     */
        /* JADX WARNING: Missing block: B:361:0x0590, code skipped:
            r5 = r7;
     */
        /* JADX WARNING: Missing block: B:362:0x0591, code skipped:
            if (r5 != null) goto L_0x05aa;
     */
        /* JADX WARNING: Missing block: B:365:0x059b, code skipped:
            if (r2.length() == 0) goto L_0x05a3;
     */
        /* JADX WARNING: Missing block: B:367:0x05a1, code skipped:
            if (r1.cacheImage.filter != null) goto L_0x05a6;
     */
        /* JADX WARNING: Missing block: B:368:0x05a3, code skipped:
            r2.delete();
     */
        /* JADX WARNING: Missing block: B:369:0x05a6, code skipped:
            r2 = r5;
            r7 = false;
     */
        /* JADX WARNING: Missing block: B:371:0x05ae, code skipped:
            if (r1.cacheImage.filter == null) goto L_0x05dc;
     */
        /* JADX WARNING: Missing block: B:372:0x05b0, code skipped:
            r2 = (float) r5.getWidth();
            r4 = (float) r5.getHeight();
     */
        /* JADX WARNING: Missing block: B:373:0x05bc, code skipped:
            if (r11.inPurgeable != false) goto L_0x05dc;
     */
        /* JADX WARNING: Missing block: B:375:0x05c0, code skipped:
            if (r3 == 0.0f) goto L_0x05dc;
     */
        /* JADX WARNING: Missing block: B:377:0x05c4, code skipped:
            if (r2 == r3) goto L_0x05dc;
     */
        /* JADX WARNING: Missing block: B:379:0x05cb, code skipped:
            if (r2 <= (20.0f + r3)) goto L_0x05dc;
     */
        /* JADX WARNING: Missing block: B:380:0x05cd, code skipped:
            r2 = org.telegram.messenger.Bitmaps.createScaledBitmap(r5, (int) r3, (int) (r4 / (r2 / r3)), true);
     */
        /* JADX WARNING: Missing block: B:381:0x05d6, code skipped:
            if (r5 == r2) goto L_0x05dc;
     */
        /* JADX WARNING: Missing block: B:382:0x05d8, code skipped:
            r5.recycle();
     */
        /* JADX WARNING: Missing block: B:383:0x05dc, code skipped:
            r2 = r5;
     */
        /* JADX WARNING: Missing block: B:384:0x05dd, code skipped:
            if (r25 == false) goto L_0x0600;
     */
        /* JADX WARNING: Missing block: B:387:0x05e1, code skipped:
            if (r11.inPurgeable == false) goto L_0x05e5;
     */
        /* JADX WARNING: Missing block: B:388:0x05e3, code skipped:
            r3 = 0;
     */
        /* JADX WARNING: Missing block: B:389:0x05e5, code skipped:
            r3 = 1;
     */
        /* JADX WARNING: Missing block: B:391:0x05f6, code skipped:
            if (org.telegram.messenger.Utilities.needInvert(r2, r3, r2.getWidth(), r2.getHeight(), r2.getRowBytes()) == 0) goto L_0x05fa;
     */
        /* JADX WARNING: Missing block: B:392:0x05f8, code skipped:
            r7 = true;
     */
        /* JADX WARNING: Missing block: B:393:0x05fa, code skipped:
            r7 = false;
     */
        /* JADX WARNING: Missing block: B:394:0x05fb, code skipped:
            r3 = 1;
     */
        /* JADX WARNING: Missing block: B:395:0x05fd, code skipped:
            r0 = th;
     */
        /* JADX WARNING: Missing block: B:396:0x0600, code skipped:
            r3 = 1;
            r7 = false;
     */
        /* JADX WARNING: Missing block: B:397:0x0602, code skipped:
            if (r0 != r3) goto L_0x0629;
     */
        /* JADX WARNING: Missing block: B:400:0x060a, code skipped:
            if (r2.getConfig() != android.graphics.Bitmap.Config.ARGB_8888) goto L_0x06b8;
     */
        /* JADX WARNING: Missing block: B:402:0x060f, code skipped:
            if (r11.inPurgeable == false) goto L_0x0613;
     */
        /* JADX WARNING: Missing block: B:403:0x0611, code skipped:
            r14 = 0;
     */
        /* JADX WARNING: Missing block: B:404:0x0613, code skipped:
            r14 = 1;
     */
        /* JADX WARNING: Missing block: B:405:0x0614, code skipped:
            org.telegram.messenger.Utilities.blurBitmap(r2, 3, r14, r2.getWidth(), r2.getHeight(), r2.getRowBytes());
     */
        /* JADX WARNING: Missing block: B:406:0x0626, code skipped:
            r0 = th;
     */
        /* JADX WARNING: Missing block: B:409:0x062a, code skipped:
            if (r0 != 2) goto L_0x064e;
     */
        /* JADX WARNING: Missing block: B:411:0x0632, code skipped:
            if (r2.getConfig() != android.graphics.Bitmap.Config.ARGB_8888) goto L_0x06b8;
     */
        /* JADX WARNING: Missing block: B:413:0x0637, code skipped:
            if (r11.inPurgeable == false) goto L_0x063b;
     */
        /* JADX WARNING: Missing block: B:414:0x0639, code skipped:
            r14 = 0;
     */
        /* JADX WARNING: Missing block: B:415:0x063b, code skipped:
            r14 = 1;
     */
        /* JADX WARNING: Missing block: B:416:0x063c, code skipped:
            org.telegram.messenger.Utilities.blurBitmap(r2, 1, r14, r2.getWidth(), r2.getHeight(), r2.getRowBytes());
     */
        /* JADX WARNING: Missing block: B:418:0x064f, code skipped:
            if (r0 != 3) goto L_0x06a2;
     */
        /* JADX WARNING: Missing block: B:420:0x0657, code skipped:
            if (r2.getConfig() != android.graphics.Bitmap.Config.ARGB_8888) goto L_0x06b8;
     */
        /* JADX WARNING: Missing block: B:422:0x065c, code skipped:
            if (r11.inPurgeable == false) goto L_0x0660;
     */
        /* JADX WARNING: Missing block: B:423:0x065e, code skipped:
            r14 = 0;
     */
        /* JADX WARNING: Missing block: B:424:0x0660, code skipped:
            r14 = 1;
     */
        /* JADX WARNING: Missing block: B:425:0x0661, code skipped:
            org.telegram.messenger.Utilities.blurBitmap(r2, 7, r14, r2.getWidth(), r2.getHeight(), r2.getRowBytes());
     */
        /* JADX WARNING: Missing block: B:426:0x0674, code skipped:
            if (r11.inPurgeable == false) goto L_0x0678;
     */
        /* JADX WARNING: Missing block: B:427:0x0676, code skipped:
            r14 = 0;
     */
        /* JADX WARNING: Missing block: B:428:0x0678, code skipped:
            r14 = 1;
     */
        /* JADX WARNING: Missing block: B:429:0x0679, code skipped:
            org.telegram.messenger.Utilities.blurBitmap(r2, 7, r14, r2.getWidth(), r2.getHeight(), r2.getRowBytes());
     */
        /* JADX WARNING: Missing block: B:430:0x068c, code skipped:
            if (r11.inPurgeable == false) goto L_0x0690;
     */
        /* JADX WARNING: Missing block: B:431:0x068e, code skipped:
            r14 = 0;
     */
        /* JADX WARNING: Missing block: B:432:0x0690, code skipped:
            r14 = 1;
     */
        /* JADX WARNING: Missing block: B:433:0x0691, code skipped:
            org.telegram.messenger.Utilities.blurBitmap(r2, 7, r14, r2.getWidth(), r2.getHeight(), r2.getRowBytes());
     */
        /* JADX WARNING: Missing block: B:434:0x06a2, code skipped:
            if (r0 != 0) goto L_0x06b8;
     */
        /* JADX WARNING: Missing block: B:436:0x06a6, code skipped:
            if (r11.inPurgeable == false) goto L_0x06b8;
     */
        /* JADX WARNING: Missing block: B:437:0x06a8, code skipped:
            org.telegram.messenger.Utilities.pinBitmap(r2);
     */
        /* JADX WARNING: Missing block: B:438:0x06ac, code skipped:
            r0 = th;
     */
        /* JADX WARNING: Missing block: B:439:0x06ad, code skipped:
            r2 = r5;
     */
        /* JADX WARNING: Missing block: B:445:0x06b2, code skipped:
            r0 = th;
     */
        /* JADX WARNING: Missing block: B:446:0x06b3, code skipped:
            r2 = r7;
     */
        /* JADX WARNING: Missing block: B:447:0x06b4, code skipped:
            r7 = false;
     */
        /* JADX WARNING: Missing block: B:448:0x06b5, code skipped:
            org.telegram.messenger.FileLog.e(r0);
     */
        /* JADX WARNING: Missing block: B:449:0x06b8, code skipped:
            r0 = 0;
            r4 = null;
     */
        /* JADX WARNING: Missing block: B:450:0x06bc, code skipped:
            r4 = 20;
     */
        /* JADX WARNING: Missing block: B:451:0x06be, code skipped:
            if (r14 != null) goto L_0x06c0;
     */
        /* JADX WARNING: Missing block: B:452:0x06c0, code skipped:
            r4 = 0;
     */
        /* JADX WARNING: Missing block: B:453:0x06c1, code skipped:
            if (r4 != 0) goto L_0x06c3;
     */
        /* JADX WARNING: Missing block: B:456:0x06cd, code skipped:
            if (org.telegram.messenger.ImageLoader.access$2100(r1.this$0) != 0) goto L_0x06cf;
     */
        /* JADX WARNING: Missing block: B:457:0x06cf, code skipped:
            r27 = r3;
            r3 = (long) r4;
     */
        /* JADX WARNING: Missing block: B:461:0x06e8, code skipped:
            java.lang.Thread.sleep(r3);
     */
        /* JADX WARNING: Missing block: B:462:0x06ec, code skipped:
            r2 = r7;
     */
        /* JADX WARNING: Missing block: B:464:0x06ed, code skipped:
            r4 = null;
     */
        /* JADX WARNING: Missing block: B:465:0x06f0, code skipped:
            r27 = r3;
     */
        /* JADX WARNING: Missing block: B:467:?, code skipped:
            org.telegram.messenger.ImageLoader.access$2102(r1.this$0, java.lang.System.currentTimeMillis());
     */
        /* JADX WARNING: Missing block: B:468:0x06fd, code skipped:
            monitor-enter(r1.sync);
     */
        /* JADX WARNING: Missing block: B:471:0x0700, code skipped:
            if (r1.isCancelled != false) goto L_0x0702;
     */
        /* JADX WARNING: Missing block: B:473:0x0703, code skipped:
            return;
     */
        /* JADX WARNING: Missing block: B:475:0x0705, code skipped:
            if (r19 != false) goto L_0x071d;
     */
        /* JADX WARNING: Missing block: B:478:0x070b, code skipped:
            if (r1.cacheImage.filter == null) goto L_0x071d;
     */
        /* JADX WARNING: Missing block: B:479:0x070d, code skipped:
            if (r0 != 0) goto L_0x071d;
     */
        /* JADX WARNING: Missing block: B:481:0x0715, code skipped:
            if (r1.cacheImage.imageLocation.path == null) goto L_0x0718;
     */
        /* JADX WARNING: Missing block: B:483:0x0718, code skipped:
            r11.inPreferredConfig = android.graphics.Bitmap.Config.RGB_565;
     */
        /* JADX WARNING: Missing block: B:485:?, code skipped:
            r11.inPreferredConfig = android.graphics.Bitmap.Config.ARGB_8888;
     */
        /* JADX WARNING: Missing block: B:486:0x0721, code skipped:
            r11.inDither = false;
     */
        /* JADX WARNING: Missing block: B:487:0x0724, code skipped:
            if (r14 == null) goto L_0x074a;
     */
        /* JADX WARNING: Missing block: B:488:0x0726, code skipped:
            if (r13 != null) goto L_0x074a;
     */
        /* JADX WARNING: Missing block: B:489:0x0728, code skipped:
            if (r15 == null) goto L_0x073a;
     */
        /* JADX WARNING: Missing block: B:491:?, code skipped:
            r5 = android.provider.MediaStore.Video.Thumbnails.getThumbnail(org.telegram.messenger.ApplicationLoader.applicationContext.getContentResolver(), r14.longValue(), 1, r11);
     */
        /* JADX WARNING: Missing block: B:492:0x073a, code skipped:
            r5 = android.provider.MediaStore.Images.Thumbnails.getThumbnail(org.telegram.messenger.ApplicationLoader.applicationContext.getContentResolver(), r14.longValue(), 1, r11);
     */
        /* JADX WARNING: Missing block: B:493:0x074a, code skipped:
            r5 = r7;
     */
        /* JADX WARNING: Missing block: B:494:0x074b, code skipped:
            if (r5 != null) goto L_0x0860;
     */
        /* JADX WARNING: Missing block: B:495:0x074d, code skipped:
            if (r12 == null) goto L_0x079f;
     */
        /* JADX WARNING: Missing block: B:497:?, code skipped:
            r3 = new java.io.RandomAccessFile(r2, "r");
            r4 = r3.getChannel().map(java.nio.channels.FileChannel.MapMode.READ_ONLY, 0, r2.length());
            r6 = new android.graphics.BitmapFactory.Options();
            r6.inJustDecodeBounds = true;
     */
        /* JADX WARNING: Missing block: B:500:?, code skipped:
            org.telegram.messenger.Utilities.loadWebpImage(null, r4, r4.limit(), r6, true);
     */
        /* JADX WARNING: Missing block: B:502:?, code skipped:
            r5 = org.telegram.messenger.Bitmaps.createBitmap(r6.outWidth, r6.outHeight, android.graphics.Bitmap.Config.ARGB_8888);
            r6 = r4.limit();
     */
        /* JADX WARNING: Missing block: B:503:0x0786, code skipped:
            if (r11.inPurgeable != false) goto L_0x078a;
     */
        /* JADX WARNING: Missing block: B:504:0x0788, code skipped:
            r7 = true;
     */
        /* JADX WARNING: Missing block: B:505:0x078a, code skipped:
            r7 = false;
     */
        /* JADX WARNING: Missing block: B:508:?, code skipped:
            org.telegram.messenger.Utilities.loadWebpImage(r5, r4, r6, null, r7);
     */
        /* JADX WARNING: Missing block: B:510:?, code skipped:
            r3.close();
     */
        /* JADX WARNING: Missing block: B:511:0x0794, code skipped:
            r2 = r5;
            r4 = null;
     */
        /* JADX WARNING: Missing block: B:513:0x0798, code skipped:
            r2 = r5;
            r4 = null;
     */
        /* JADX WARNING: Missing block: B:515:0x079c, code skipped:
            r2 = r5;
     */
        /* JADX WARNING: Missing block: B:519:0x07a1, code skipped:
            if (r11.inPurgeable != false) goto L_0x07fd;
     */
        /* JADX WARNING: Missing block: B:520:0x07a3, code skipped:
            if (r9 == null) goto L_0x07a6;
     */
        /* JADX WARNING: Missing block: B:521:0x07a6, code skipped:
            if (r8 == null) goto L_0x07b2;
     */
        /* JADX WARNING: Missing block: B:523:?, code skipped:
            r3 = new org.telegram.messenger.secretmedia.EncryptedFileInputStream(r2, r1.cacheImage.encryptionKeyPath);
     */
        /* JADX WARNING: Missing block: B:525:?, code skipped:
            r3 = new java.io.FileInputStream(r2);
     */
        /* JADX WARNING: Missing block: B:527:0x07bf, code skipped:
            if ((r1.cacheImage.imageLocation.document instanceof org.telegram.tgnet.TLRPC.TL_document) == false) goto L_0x07f3;
     */
        /* JADX WARNING: Missing block: B:529:?, code skipped:
            r4 = new androidx.exifinterface.media.ExifInterface(r3).getAttributeInt("Orientation", 1);
     */
        /* JADX WARNING: Missing block: B:531:0x07ce, code skipped:
            if (r4 == 3) goto L_0x07de;
     */
        /* JADX WARNING: Missing block: B:533:0x07d1, code skipped:
            if (r4 == 6) goto L_0x07db;
     */
        /* JADX WARNING: Missing block: B:535:0x07d5, code skipped:
            if (r4 == 8) goto L_0x07d8;
     */
        /* JADX WARNING: Missing block: B:536:0x07d8, code skipped:
            r7 = 270;
     */
        /* JADX WARNING: Missing block: B:537:0x07db, code skipped:
            r7 = 90;
     */
        /* JADX WARNING: Missing block: B:538:0x07de, code skipped:
            r7 = 180;
     */
        /* JADX WARNING: Missing block: B:546:0x07f3, code skipped:
            r4 = null;
            r7 = 0;
     */
        /* JADX WARNING: Missing block: B:549:0x07fd, code skipped:
            r4 = null;
     */
        /* JADX WARNING: Missing block: B:551:?, code skipped:
            r3 = new java.io.RandomAccessFile(r2, "r");
            r7 = (int) r3.length();
            r6 = (byte[]) org.telegram.messenger.ImageLoader.access$1700().get();
     */
        /* JADX WARNING: Missing block: B:552:0x0814, code skipped:
            if (r6 == null) goto L_0x081a;
     */
        /* JADX WARNING: Missing block: B:554:0x0817, code skipped:
            if (r6.length < r7) goto L_0x081a;
     */
        /* JADX WARNING: Missing block: B:556:0x081a, code skipped:
            r6 = null;
     */
        /* JADX WARNING: Missing block: B:557:0x081b, code skipped:
            if (r6 != null) goto L_0x0826;
     */
        /* JADX WARNING: Missing block: B:558:0x081d, code skipped:
            r6 = new byte[r7];
            org.telegram.messenger.ImageLoader.access$1700().set(r6);
     */
        /* JADX WARNING: Missing block: B:559:0x0826, code skipped:
            r3.readFully(r6, 0, r7);
            r3.close();
     */
        /* JADX WARNING: Missing block: B:560:0x082d, code skipped:
            if (r9 == null) goto L_0x0849;
     */
        /* JADX WARNING: Missing block: B:561:0x082f, code skipped:
            org.telegram.messenger.secretmedia.EncryptedFileInputStream.decryptBytesWithKeyFile(r6, 0, r7, r9);
            r3 = org.telegram.messenger.Utilities.computeSHA256(r6, 0, r7);
     */
        /* JADX WARNING: Missing block: B:562:0x0836, code skipped:
            if (r10 == null) goto L_0x0841;
     */
        /* JADX WARNING: Missing block: B:564:0x083c, code skipped:
            if (java.util.Arrays.equals(r3, r10) != false) goto L_0x083f;
     */
        /* JADX WARNING: Missing block: B:566:0x083f, code skipped:
            r3 = null;
     */
        /* JADX WARNING: Missing block: B:567:0x0841, code skipped:
            r3 = 1;
     */
        /* JADX WARNING: Missing block: B:568:0x0842, code skipped:
            r8 = r6[0] & 255;
            r7 = r7 - r8;
     */
        /* JADX WARNING: Missing block: B:569:0x0849, code skipped:
            if (r8 == null) goto L_0x0853;
     */
        /* JADX WARNING: Missing block: B:570:0x084b, code skipped:
            org.telegram.messenger.secretmedia.EncryptedFileInputStream.decryptBytesWithKeyFile(r6, 0, r7, r1.cacheImage.encryptionKeyPath);
     */
        /* JADX WARNING: Missing block: B:571:0x0853, code skipped:
            r3 = null;
            r8 = 0;
     */
        /* JADX WARNING: Missing block: B:572:0x0855, code skipped:
            if (r3 != null) goto L_0x0861;
     */
        /* JADX WARNING: Missing block: B:573:0x0857, code skipped:
            r5 = android.graphics.BitmapFactory.decodeByteArray(r6, r8, r7, r11);
     */
        /* JADX WARNING: Missing block: B:574:0x085c, code skipped:
            r4 = null;
     */
        /* JADX WARNING: Missing block: B:576:0x085d, code skipped:
            r2 = r5;
     */
        /* JADX WARNING: Missing block: B:578:0x0860, code skipped:
            r4 = null;
     */
        /* JADX WARNING: Missing block: B:579:0x0861, code skipped:
            r7 = 0;
     */
        /* JADX WARNING: Missing block: B:662:0x095a, code skipped:
            r4 = null;
     */
        /* JADX WARNING: Missing block: B:669:0x0960, code skipped:
            r4 = null;
     */
        /* JADX WARNING: Missing block: B:671:0x0962, code skipped:
            r2 = r7;
     */
        /* JADX WARNING: Missing block: B:673:0x0963, code skipped:
            r7 = false;
            r24 = 0;
     */
        /* JADX WARNING: Missing block: B:678:0x0970, code skipped:
            if (r2 != null) goto L_0x0972;
     */
        /* JADX WARNING: Missing block: B:679:0x0972, code skipped:
            r4 = new android.graphics.drawable.BitmapDrawable(r2);
     */
        /* JADX WARNING: Missing block: B:680:0x0978, code skipped:
            onPostExecute(r4);
     */
        /* JADX WARNING: Missing block: B:682:0x097e, code skipped:
            r4 = new org.telegram.messenger.ExtendedBitmapDrawable(r2, r7, r0);
     */
        public void run() {
            /*
            r34 = this;
            r1 = r34;
            r2 = r1.sync;
            monitor-enter(r2);
            r0 = java.lang.Thread.currentThread();	 Catch:{ all -> 0x0988 }
            r1.runningThread = r0;	 Catch:{ all -> 0x0988 }
            java.lang.Thread.interrupted();	 Catch:{ all -> 0x0988 }
            r0 = r1.isCancelled;	 Catch:{ all -> 0x0988 }
            if (r0 == 0) goto L_0x0014;
        L_0x0012:
            monitor-exit(r2);	 Catch:{ all -> 0x0988 }
            return;
        L_0x0014:
            monitor-exit(r2);	 Catch:{ all -> 0x0988 }
            r0 = r1.cacheImage;
            r2 = r0.imageLocation;
            r2 = r2.photoSize;
            r2 = r2 instanceof org.telegram.tgnet.TLRPC.TL_photoStrippedSize;
            r3 = 3;
            r4 = 2;
            r6 = 1;
            r7 = 0;
            if (r2 == 0) goto L_0x00ca;
        L_0x0023:
            r2 = r1.sync;
            monitor-enter(r2);
            r0 = r1.isCancelled;	 Catch:{ all -> 0x00c7 }
            if (r0 == 0) goto L_0x002c;
        L_0x002a:
            monitor-exit(r2);	 Catch:{ all -> 0x00c7 }
            return;
        L_0x002c:
            monitor-exit(r2);	 Catch:{ all -> 0x00c7 }
            r0 = r1.cacheImage;
            r0 = r0.imageLocation;
            r0 = r0.photoSize;
            r0 = (org.telegram.tgnet.TLRPC.TL_photoStrippedSize) r0;
            r2 = r0.bytes;
            r2 = r2.length;
            r2 = r2 - r3;
            r8 = org.telegram.messenger.Bitmaps.header;
            r8 = r8.length;
            r2 = r2 + r8;
            r8 = org.telegram.messenger.Bitmaps.footer;
            r8 = r8.length;
            r2 = r2 + r8;
            r8 = org.telegram.messenger.ImageLoader.bytesLocal;
            r8 = r8.get();
            r8 = (byte[]) r8;
            if (r8 == 0) goto L_0x0051;
        L_0x004d:
            r9 = r8.length;
            if (r9 < r2) goto L_0x0051;
        L_0x0050:
            goto L_0x0052;
        L_0x0051:
            r8 = 0;
        L_0x0052:
            if (r8 != 0) goto L_0x005d;
        L_0x0054:
            r8 = new byte[r2];
            r9 = org.telegram.messenger.ImageLoader.bytesLocal;
            r9.set(r8);
        L_0x005d:
            r9 = org.telegram.messenger.Bitmaps.header;
            r10 = r9.length;
            java.lang.System.arraycopy(r9, r7, r8, r7, r10);
            r9 = r0.bytes;
            r10 = org.telegram.messenger.Bitmaps.header;
            r10 = r10.length;
            r11 = r9.length;
            r11 = r11 - r3;
            java.lang.System.arraycopy(r9, r3, r8, r10, r11);
            r9 = org.telegram.messenger.Bitmaps.footer;
            r10 = org.telegram.messenger.Bitmaps.header;
            r10 = r10.length;
            r11 = r0.bytes;
            r11 = r11.length;
            r10 = r10 + r11;
            r10 = r10 - r3;
            r3 = org.telegram.messenger.Bitmaps.footer;
            r3 = r3.length;
            java.lang.System.arraycopy(r9, r7, r8, r10, r3);
            r3 = 164; // 0xa4 float:2.3E-43 double:8.1E-322;
            r0 = r0.bytes;
            r6 = r0[r6];
            r8[r3] = r6;
            r3 = 166; // 0xa6 float:2.33E-43 double:8.2E-322;
            r0 = r0[r4];
            r8[r3] = r0;
            r0 = android.graphics.BitmapFactory.decodeByteArray(r8, r7, r2);
            if (r0 == 0) goto L_0x00b9;
        L_0x0091:
            r2 = r1.cacheImage;
            r2 = r2.filter;
            r2 = android.text.TextUtils.isEmpty(r2);
            if (r2 != 0) goto L_0x00b9;
        L_0x009b:
            r2 = r1.cacheImage;
            r2 = r2.filter;
            r3 = "b";
            r2 = r2.contains(r3);
            if (r2 == 0) goto L_0x00b9;
        L_0x00a7:
            r10 = 3;
            r11 = 1;
            r12 = r0.getWidth();
            r13 = r0.getHeight();
            r14 = r0.getRowBytes();
            r9 = r0;
            org.telegram.messenger.Utilities.blurBitmap(r9, r10, r11, r12, r13, r14);
        L_0x00b9:
            if (r0 == 0) goto L_0x00c1;
        L_0x00bb:
            r5 = new android.graphics.drawable.BitmapDrawable;
            r5.<init>(r0);
            goto L_0x00c2;
        L_0x00c1:
            r5 = 0;
        L_0x00c2:
            r1.onPostExecute(r5);
            goto L_0x0987;
        L_0x00c7:
            r0 = move-exception;
            monitor-exit(r2);	 Catch:{ all -> 0x00c7 }
            throw r0;
        L_0x00ca:
            r2 = r0.lottieFile;
            if (r2 == 0) goto L_0x0153;
        L_0x00ce:
            r2 = r1.sync;
            monitor-enter(r2);
            r0 = r1.isCancelled;	 Catch:{ all -> 0x0150 }
            if (r0 == 0) goto L_0x00d7;
        L_0x00d5:
            monitor-exit(r2);	 Catch:{ all -> 0x0150 }
            return;
        L_0x00d7:
            monitor-exit(r2);	 Catch:{ all -> 0x0150 }
            r0 = NUM; // 0x432a999a float:170.6 double:5.56745435E-315;
            r2 = org.telegram.messenger.AndroidUtilities.dp(r0);
            r3 = 512; // 0x200 float:7.175E-43 double:2.53E-321;
            r2 = java.lang.Math.min(r3, r2);
            r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
            r0 = java.lang.Math.min(r3, r0);
            r5 = r1.cacheImage;
            r5 = r5.filter;
            if (r5 == 0) goto L_0x013d;
        L_0x00f3:
            r8 = "_";
            r5 = r5.split(r8);
            r8 = r5.length;
            if (r8 < r4) goto L_0x013d;
        L_0x00fc:
            r0 = r5[r7];
            r0 = java.lang.Float.parseFloat(r0);
            r2 = r5[r6];
            r2 = java.lang.Float.parseFloat(r2);
            r5 = org.telegram.messenger.AndroidUtilities.density;
            r5 = r5 * r0;
            r5 = (int) r5;
            r5 = java.lang.Math.min(r3, r5);
            r8 = org.telegram.messenger.AndroidUtilities.density;
            r8 = r8 * r2;
            r8 = (int) r8;
            r3 = java.lang.Math.min(r3, r8);
            r8 = NUM; // 0x42b40000 float:90.0 double:5.529052754E-315;
            r0 = (r0 > r8 ? 1 : (r0 == r8 ? 0 : -1));
            if (r0 > 0) goto L_0x013a;
        L_0x0120:
            r0 = (r2 > r8 ? 1 : (r2 == r8 ? 0 : -1));
            if (r0 > 0) goto L_0x013a;
        L_0x0124:
            r0 = 160; // 0xa0 float:2.24E-43 double:7.9E-322;
            r2 = java.lang.Math.min(r5, r0);
            r0 = java.lang.Math.min(r3, r0);
            r3 = org.telegram.messenger.SharedConfig.getDevicePerfomanceClass();
            if (r3 == r4) goto L_0x0135;
        L_0x0134:
            r7 = 1;
        L_0x0135:
            r11 = r0;
            r10 = r2;
            r12 = r7;
            r13 = 1;
            goto L_0x0141;
        L_0x013a:
            r11 = r3;
            r10 = r5;
            goto L_0x013f;
        L_0x013d:
            r11 = r0;
            r10 = r2;
        L_0x013f:
            r12 = 0;
            r13 = 0;
        L_0x0141:
            r0 = new org.telegram.ui.Components.RLottieDrawable;
            r2 = r1.cacheImage;
            r9 = r2.finalFilePath;
            r8 = r0;
            r8.<init>(r9, r10, r11, r12, r13);
            r1.onPostExecute(r0);
            goto L_0x0987;
        L_0x0150:
            r0 = move-exception;
            monitor-exit(r2);	 Catch:{ all -> 0x0150 }
            throw r0;
        L_0x0153:
            r2 = r0.animatedFile;
            if (r2 == 0) goto L_0x01bb;
        L_0x0157:
            r2 = r1.sync;
            monitor-enter(r2);
            r0 = r1.isCancelled;	 Catch:{ all -> 0x01b8 }
            if (r0 == 0) goto L_0x0160;
        L_0x015e:
            monitor-exit(r2);	 Catch:{ all -> 0x01b8 }
            return;
        L_0x0160:
            monitor-exit(r2);	 Catch:{ all -> 0x01b8 }
            r0 = r1.cacheImage;
            r0 = r0.filter;
            r2 = "g";
            r0 = r2.equals(r0);
            if (r0 == 0) goto L_0x0192;
        L_0x016d:
            r0 = r1.cacheImage;
            r2 = r0.imageLocation;
            r2 = r2.document;
            r3 = r2 instanceof org.telegram.tgnet.TLRPC.TL_documentEncrypted;
            if (r3 != 0) goto L_0x0192;
        L_0x0177:
            r3 = new org.telegram.ui.Components.AnimatedFileDrawable;
            r7 = r0.finalFilePath;
            r8 = 0;
            r0 = r0.size;
            r9 = (long) r0;
            r0 = r2 instanceof org.telegram.tgnet.TLRPC.Document;
            if (r0 == 0) goto L_0x0185;
        L_0x0183:
            r11 = r2;
            goto L_0x0186;
        L_0x0185:
            r11 = 0;
        L_0x0186:
            r0 = r1.cacheImage;
            r12 = r0.parentObject;
            r13 = r0.currentAccount;
            r6 = r3;
            r6.<init>(r7, r8, r9, r11, r12, r13);
            r0 = r3;
            goto L_0x01b0;
        L_0x0192:
            r0 = new org.telegram.ui.Components.AnimatedFileDrawable;
            r2 = r1.cacheImage;
            r15 = r2.finalFilePath;
            r2 = r2.filter;
            r3 = "d";
            r16 = r3.equals(r2);
            r17 = 0;
            r19 = 0;
            r20 = 0;
            r2 = r1.cacheImage;
            r2 = r2.currentAccount;
            r14 = r0;
            r21 = r2;
            r14.<init>(r15, r16, r17, r19, r20, r21);
        L_0x01b0:
            java.lang.Thread.interrupted();
            r1.onPostExecute(r0);
            goto L_0x0987;
        L_0x01b8:
            r0 = move-exception;
            monitor-exit(r2);	 Catch:{ all -> 0x01b8 }
            throw r0;
        L_0x01bb:
            r2 = r0.finalFilePath;
            r8 = r0.secureDocument;
            if (r8 != 0) goto L_0x01d6;
        L_0x01c1:
            r0 = r0.encryptionKeyPath;
            if (r0 == 0) goto L_0x01d4;
        L_0x01c5:
            if (r2 == 0) goto L_0x01d4;
        L_0x01c7:
            r0 = r2.getAbsolutePath();
            r8 = ".enc";
            r0 = r0.endsWith(r8);
            if (r0 == 0) goto L_0x01d4;
        L_0x01d3:
            goto L_0x01d6;
        L_0x01d4:
            r8 = 0;
            goto L_0x01d7;
        L_0x01d6:
            r8 = 1;
        L_0x01d7:
            r0 = r1.cacheImage;
            r0 = r0.secureDocument;
            if (r0 == 0) goto L_0x01f0;
        L_0x01dd:
            r9 = r0.secureDocumentKey;
            r0 = r0.secureFile;
            if (r0 == 0) goto L_0x01e8;
        L_0x01e3:
            r0 = r0.file_hash;
            if (r0 == 0) goto L_0x01e8;
        L_0x01e7:
            goto L_0x01ee;
        L_0x01e8:
            r0 = r1.cacheImage;
            r0 = r0.secureDocument;
            r0 = r0.fileHash;
        L_0x01ee:
            r10 = r0;
            goto L_0x01f2;
        L_0x01f0:
            r9 = 0;
            r10 = 0;
        L_0x01f2:
            r0 = android.os.Build.VERSION.SDK_INT;
            r11 = 19;
            if (r0 >= r11) goto L_0x0261;
        L_0x01f8:
            r11 = new java.io.RandomAccessFile;	 Catch:{ Exception -> 0x0247, all -> 0x0243 }
            r0 = "r";
            r11.<init>(r2, r0);	 Catch:{ Exception -> 0x0247, all -> 0x0243 }
            r0 = r1.cacheImage;	 Catch:{ Exception -> 0x0241 }
            r0 = r0.imageType;	 Catch:{ Exception -> 0x0241 }
            if (r0 != r6) goto L_0x020a;
        L_0x0205:
            r0 = org.telegram.messenger.ImageLoader.headerThumb;	 Catch:{ Exception -> 0x0241 }
            goto L_0x020e;
        L_0x020a:
            r0 = org.telegram.messenger.ImageLoader.header;	 Catch:{ Exception -> 0x0241 }
        L_0x020e:
            r12 = r0.length;	 Catch:{ Exception -> 0x0241 }
            r11.readFully(r0, r7, r12);	 Catch:{ Exception -> 0x0241 }
            r12 = new java.lang.String;	 Catch:{ Exception -> 0x0241 }
            r12.<init>(r0);	 Catch:{ Exception -> 0x0241 }
            r0 = r12.toLowerCase();	 Catch:{ Exception -> 0x0241 }
            r0 = r0.toLowerCase();	 Catch:{ Exception -> 0x0241 }
            r12 = "riff";
            r12 = r0.startsWith(r12);	 Catch:{ Exception -> 0x0241 }
            if (r12 == 0) goto L_0x0231;
        L_0x0227:
            r12 = "webp";
            r0 = r0.endsWith(r12);	 Catch:{ Exception -> 0x0241 }
            if (r0 == 0) goto L_0x0231;
        L_0x022f:
            r12 = 1;
            goto L_0x0232;
        L_0x0231:
            r12 = 0;
        L_0x0232:
            r11.close();	 Catch:{ Exception -> 0x023f }
            r11.close();	 Catch:{ Exception -> 0x0239 }
            goto L_0x0262;
        L_0x0239:
            r0 = move-exception;
            r11 = r0;
            org.telegram.messenger.FileLog.e(r11);
            goto L_0x0262;
        L_0x023f:
            r0 = move-exception;
            goto L_0x024a;
        L_0x0241:
            r0 = move-exception;
            goto L_0x0249;
        L_0x0243:
            r0 = move-exception;
            r2 = r0;
            r11 = 0;
            goto L_0x0255;
        L_0x0247:
            r0 = move-exception;
            r11 = 0;
        L_0x0249:
            r12 = 0;
        L_0x024a:
            org.telegram.messenger.FileLog.e(r0);	 Catch:{ all -> 0x0253 }
            if (r11 == 0) goto L_0x0262;
        L_0x024f:
            r11.close();	 Catch:{ Exception -> 0x0239 }
            goto L_0x0262;
        L_0x0253:
            r0 = move-exception;
            r2 = r0;
        L_0x0255:
            if (r11 == 0) goto L_0x0260;
        L_0x0257:
            r11.close();	 Catch:{ Exception -> 0x025b }
            goto L_0x0260;
        L_0x025b:
            r0 = move-exception;
            r3 = r0;
            org.telegram.messenger.FileLog.e(r3);
        L_0x0260:
            throw r2;
        L_0x0261:
            r12 = 0;
        L_0x0262:
            r0 = r1.cacheImage;
            r0 = r0.imageLocation;
            r0 = r0.path;
            r11 = 8;
            if (r0 == 0) goto L_0x02c6;
        L_0x026c:
            r13 = "thumb://";
            r13 = r0.startsWith(r13);
            if (r13 == 0) goto L_0x0295;
        L_0x0274:
            r13 = ":";
            r13 = r0.indexOf(r13, r11);
            if (r13 < 0) goto L_0x028e;
        L_0x027c:
            r14 = r0.substring(r11, r13);
            r14 = java.lang.Long.parseLong(r14);
            r14 = java.lang.Long.valueOf(r14);
            r13 = r13 + r6;
            r0 = r0.substring(r13);
            goto L_0x0290;
        L_0x028e:
            r0 = 0;
            r14 = 0;
        L_0x0290:
            r13 = r0;
        L_0x0291:
            r15 = 0;
        L_0x0292:
            r16 = 0;
            goto L_0x02cb;
        L_0x0295:
            r13 = "vthumb://";
            r13 = r0.startsWith(r13);
            if (r13 == 0) goto L_0x02bb;
        L_0x029d:
            r13 = 9;
            r14 = ":";
            r14 = r0.indexOf(r14, r13);
            if (r14 < 0) goto L_0x02b5;
        L_0x02a7:
            r0 = r0.substring(r13, r14);
            r13 = java.lang.Long.parseLong(r0);
            r0 = java.lang.Long.valueOf(r13);
            r13 = 1;
            goto L_0x02b7;
        L_0x02b5:
            r0 = 0;
            r13 = 0;
        L_0x02b7:
            r14 = r0;
            r15 = r13;
            r13 = 0;
            goto L_0x0292;
        L_0x02bb:
            r13 = "http";
            r0 = r0.startsWith(r13);
            if (r0 != 0) goto L_0x02c6;
        L_0x02c3:
            r13 = 0;
            r14 = 0;
            goto L_0x0291;
        L_0x02c6:
            r13 = 0;
            r14 = 0;
            r15 = 0;
            r16 = 1;
        L_0x02cb:
            r11 = new android.graphics.BitmapFactory$Options;
            r11.<init>();
            r11.inSampleSize = r6;
            r0 = android.os.Build.VERSION.SDK_INT;
            r3 = 21;
            if (r0 >= r3) goto L_0x02da;
        L_0x02d8:
            r11.inPurgeable = r6;
        L_0x02da:
            r0 = org.telegram.messenger.ImageLoader.this;
            r19 = r0.canForce8888;
            r20 = 0;
            r0 = r1.cacheImage;	 Catch:{ Throwable -> 0x0499 }
            r0 = r0.filter;	 Catch:{ Throwable -> 0x0499 }
            r21 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
            if (r0 == 0) goto L_0x044d;
        L_0x02ea:
            r0 = r1.cacheImage;	 Catch:{ Throwable -> 0x0499 }
            r0 = r0.filter;	 Catch:{ Throwable -> 0x0499 }
            r3 = "_";
            r0 = r0.split(r3);	 Catch:{ Throwable -> 0x0499 }
            r3 = r0.length;	 Catch:{ Throwable -> 0x0499 }
            if (r3 < r4) goto L_0x0311;
        L_0x02f7:
            r3 = r0[r7];	 Catch:{ Throwable -> 0x0499 }
            r3 = java.lang.Float.parseFloat(r3);	 Catch:{ Throwable -> 0x0499 }
            r22 = org.telegram.messenger.AndroidUtilities.density;	 Catch:{ Throwable -> 0x0499 }
            r3 = r3 * r22;
            r0 = r0[r6];	 Catch:{ Throwable -> 0x030e }
            r0 = java.lang.Float.parseFloat(r0);	 Catch:{ Throwable -> 0x030e }
            r22 = org.telegram.messenger.AndroidUtilities.density;	 Catch:{ Throwable -> 0x030e }
            r0 = r0 * r22;
            r22 = r0;
            goto L_0x0314;
        L_0x030e:
            r0 = move-exception;
            goto L_0x049b;
        L_0x0311:
            r3 = 0;
            r22 = 0;
        L_0x0314:
            r0 = r1.cacheImage;	 Catch:{ Throwable -> 0x044a }
            r0 = r0.filter;	 Catch:{ Throwable -> 0x044a }
            r4 = "b2";
            r0 = r0.contains(r4);	 Catch:{ Throwable -> 0x044a }
            if (r0 == 0) goto L_0x0322;
        L_0x0320:
            r4 = 3;
            goto L_0x033f;
        L_0x0322:
            r0 = r1.cacheImage;	 Catch:{ Throwable -> 0x044a }
            r0 = r0.filter;	 Catch:{ Throwable -> 0x044a }
            r4 = "b1";
            r0 = r0.contains(r4);	 Catch:{ Throwable -> 0x044a }
            if (r0 == 0) goto L_0x0330;
        L_0x032e:
            r4 = 2;
            goto L_0x033f;
        L_0x0330:
            r0 = r1.cacheImage;	 Catch:{ Throwable -> 0x044a }
            r0 = r0.filter;	 Catch:{ Throwable -> 0x044a }
            r4 = "b";
            r0 = r0.contains(r4);	 Catch:{ Throwable -> 0x044a }
            if (r0 == 0) goto L_0x033e;
        L_0x033c:
            r4 = 1;
            goto L_0x033f;
        L_0x033e:
            r4 = 0;
        L_0x033f:
            r0 = r1.cacheImage;	 Catch:{ Throwable -> 0x0443 }
            r0 = r0.filter;	 Catch:{ Throwable -> 0x0443 }
            r5 = "i";
            r5 = r0.contains(r5);	 Catch:{ Throwable -> 0x0443 }
            r0 = r1.cacheImage;	 Catch:{ Throwable -> 0x043c }
            r0 = r0.filter;	 Catch:{ Throwable -> 0x043c }
            r7 = "f";
            r0 = r0.contains(r7);	 Catch:{ Throwable -> 0x043c }
            if (r0 == 0) goto L_0x0357;
        L_0x0355:
            r19 = 1;
        L_0x0357:
            if (r12 != 0) goto L_0x0435;
        L_0x0359:
            r0 = (r3 > r20 ? 1 : (r3 == r20 ? 0 : -1));
            if (r0 == 0) goto L_0x0435;
        L_0x035d:
            r0 = (r22 > r20 ? 1 : (r22 == r20 ? 0 : -1));
            if (r0 == 0) goto L_0x0435;
        L_0x0361:
            r11.inJustDecodeBounds = r6;	 Catch:{ Throwable -> 0x043c }
            if (r14 == 0) goto L_0x0394;
        L_0x0365:
            if (r13 != 0) goto L_0x0394;
        L_0x0367:
            if (r15 == 0) goto L_0x0380;
        L_0x0369:
            r0 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Throwable -> 0x037a }
            r0 = r0.getContentResolver();	 Catch:{ Throwable -> 0x037a }
            r7 = r4;
            r25 = r5;
            r4 = r14.longValue();	 Catch:{ Throwable -> 0x03eb }
            android.provider.MediaStore.Video.Thumbnails.getThumbnail(r0, r4, r6, r11);	 Catch:{ Throwable -> 0x03eb }
            goto L_0x0390;
        L_0x037a:
            r0 = move-exception;
            r7 = r4;
            r25 = r5;
            goto L_0x03ec;
        L_0x0380:
            r7 = r4;
            r25 = r5;
            r0 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Throwable -> 0x03eb }
            r0 = r0.getContentResolver();	 Catch:{ Throwable -> 0x03eb }
            r4 = r14.longValue();	 Catch:{ Throwable -> 0x03eb }
            android.provider.MediaStore.Images.Thumbnails.getThumbnail(r0, r4, r6, r11);	 Catch:{ Throwable -> 0x03eb }
        L_0x0390:
            r26 = r7;
            goto L_0x0409;
        L_0x0394:
            r7 = r4;
            r25 = r5;
            if (r9 == 0) goto L_0x03ef;
        L_0x0399:
            r0 = new java.io.RandomAccessFile;	 Catch:{ Throwable -> 0x03eb }
            r4 = "r";
            r0.<init>(r2, r4);	 Catch:{ Throwable -> 0x03eb }
            r4 = r0.length();	 Catch:{ Throwable -> 0x03eb }
            r5 = (int) r4;	 Catch:{ Throwable -> 0x03eb }
            r4 = org.telegram.messenger.ImageLoader.bytesLocal;	 Catch:{ Throwable -> 0x03eb }
            r4 = r4.get();	 Catch:{ Throwable -> 0x03eb }
            r4 = (byte[]) r4;	 Catch:{ Throwable -> 0x03eb }
            if (r4 == 0) goto L_0x03b5;
        L_0x03b1:
            r6 = r4.length;	 Catch:{ Throwable -> 0x03eb }
            if (r6 < r5) goto L_0x03b5;
        L_0x03b4:
            goto L_0x03b6;
        L_0x03b5:
            r4 = 0;
        L_0x03b6:
            if (r4 != 0) goto L_0x03c1;
        L_0x03b8:
            r4 = new byte[r5];	 Catch:{ Throwable -> 0x03eb }
            r6 = org.telegram.messenger.ImageLoader.bytesLocal;	 Catch:{ Throwable -> 0x03eb }
            r6.set(r4);	 Catch:{ Throwable -> 0x03eb }
        L_0x03c1:
            r6 = 0;
            r0.readFully(r4, r6, r5);	 Catch:{ Throwable -> 0x03eb }
            r0.close();	 Catch:{ Throwable -> 0x03eb }
            org.telegram.messenger.secretmedia.EncryptedFileInputStream.decryptBytesWithKeyFile(r4, r6, r5, r9);	 Catch:{ Throwable -> 0x03eb }
            r0 = org.telegram.messenger.Utilities.computeSHA256(r4, r6, r5);	 Catch:{ Throwable -> 0x03eb }
            if (r10 == 0) goto L_0x03dc;
        L_0x03d1:
            r0 = java.util.Arrays.equals(r0, r10);	 Catch:{ Throwable -> 0x03eb }
            if (r0 != 0) goto L_0x03d8;
        L_0x03d7:
            goto L_0x03dc;
        L_0x03d8:
            r26 = r7;
            r0 = 0;
            goto L_0x03df;
        L_0x03dc:
            r26 = r7;
            r0 = 1;
        L_0x03df:
            r6 = 0;
            r7 = r4[r6];	 Catch:{ Throwable -> 0x0433 }
            r6 = r7 & 255;
            r5 = r5 - r6;
            if (r0 != 0) goto L_0x0409;
        L_0x03e7:
            android.graphics.BitmapFactory.decodeByteArray(r4, r6, r5, r11);	 Catch:{ Throwable -> 0x0433 }
            goto L_0x0409;
        L_0x03eb:
            r0 = move-exception;
        L_0x03ec:
            r26 = r7;
            goto L_0x0441;
        L_0x03ef:
            r26 = r7;
            if (r8 == 0) goto L_0x03fd;
        L_0x03f3:
            r0 = new org.telegram.messenger.secretmedia.EncryptedFileInputStream;	 Catch:{ Throwable -> 0x0433 }
            r4 = r1.cacheImage;	 Catch:{ Throwable -> 0x0433 }
            r4 = r4.encryptionKeyPath;	 Catch:{ Throwable -> 0x0433 }
            r0.<init>(r2, r4);	 Catch:{ Throwable -> 0x0433 }
            goto L_0x0402;
        L_0x03fd:
            r0 = new java.io.FileInputStream;	 Catch:{ Throwable -> 0x0433 }
            r0.<init>(r2);	 Catch:{ Throwable -> 0x0433 }
        L_0x0402:
            r4 = 0;
            android.graphics.BitmapFactory.decodeStream(r0, r4, r11);	 Catch:{ Throwable -> 0x0433 }
            r0.close();	 Catch:{ Throwable -> 0x0433 }
        L_0x0409:
            r0 = r11.outWidth;	 Catch:{ Throwable -> 0x0433 }
            r0 = (float) r0;	 Catch:{ Throwable -> 0x0433 }
            r4 = r11.outHeight;	 Catch:{ Throwable -> 0x0433 }
            r4 = (float) r4;	 Catch:{ Throwable -> 0x0433 }
            r5 = (r3 > r22 ? 1 : (r3 == r22 ? 0 : -1));
            if (r5 <= 0) goto L_0x041f;
        L_0x0413:
            r5 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1));
            if (r5 <= 0) goto L_0x041f;
        L_0x0417:
            r0 = r0 / r3;
            r4 = r4 / r22;
            r0 = java.lang.Math.max(r0, r4);	 Catch:{ Throwable -> 0x0433 }
            goto L_0x0426;
        L_0x041f:
            r0 = r0 / r3;
            r4 = r4 / r22;
            r0 = java.lang.Math.min(r0, r4);	 Catch:{ Throwable -> 0x0433 }
        L_0x0426:
            r4 = (r0 > r21 ? 1 : (r0 == r21 ? 0 : -1));
            if (r4 >= 0) goto L_0x042c;
        L_0x042a:
            r0 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        L_0x042c:
            r4 = 0;
            r11.inJustDecodeBounds = r4;	 Catch:{ Throwable -> 0x0433 }
            r0 = (int) r0;	 Catch:{ Throwable -> 0x0433 }
            r11.inSampleSize = r0;	 Catch:{ Throwable -> 0x0433 }
            goto L_0x0439;
        L_0x0433:
            r0 = move-exception;
            goto L_0x0441;
        L_0x0435:
            r26 = r4;
            r25 = r5;
        L_0x0439:
            r5 = 0;
            goto L_0x04a5;
        L_0x043c:
            r0 = move-exception;
            r26 = r4;
            r25 = r5;
        L_0x0441:
            r5 = 0;
            goto L_0x04a2;
        L_0x0443:
            r0 = move-exception;
            r26 = r4;
            r5 = 0;
            r25 = 0;
            goto L_0x04a2;
        L_0x044a:
            r0 = move-exception;
            r5 = 0;
            goto L_0x049e;
        L_0x044d:
            if (r13 == 0) goto L_0x0490;
        L_0x044f:
            r3 = 1;
            r11.inJustDecodeBounds = r3;	 Catch:{ Throwable -> 0x0499 }
            if (r19 == 0) goto L_0x0457;
        L_0x0454:
            r0 = android.graphics.Bitmap.Config.ARGB_8888;	 Catch:{ Throwable -> 0x0499 }
            goto L_0x0459;
        L_0x0457:
            r0 = android.graphics.Bitmap.Config.RGB_565;	 Catch:{ Throwable -> 0x0499 }
        L_0x0459:
            r11.inPreferredConfig = r0;	 Catch:{ Throwable -> 0x0499 }
            r0 = new java.io.FileInputStream;	 Catch:{ Throwable -> 0x0499 }
            r0.<init>(r2);	 Catch:{ Throwable -> 0x0499 }
            r3 = 0;
            r5 = android.graphics.BitmapFactory.decodeStream(r0, r3, r11);	 Catch:{ Throwable -> 0x0499 }
            r0.close();	 Catch:{ Throwable -> 0x048d }
            r0 = r11.outWidth;	 Catch:{ Throwable -> 0x048d }
            r3 = r11.outHeight;	 Catch:{ Throwable -> 0x048d }
            r4 = 0;
            r11.inJustDecodeBounds = r4;	 Catch:{ Throwable -> 0x048d }
            r0 = r0 / 200;
            r3 = r3 / 200;
            r0 = java.lang.Math.max(r0, r3);	 Catch:{ Throwable -> 0x048d }
            r0 = (float) r0;	 Catch:{ Throwable -> 0x048d }
            r3 = (r0 > r21 ? 1 : (r0 == r21 ? 0 : -1));
            if (r3 >= 0) goto L_0x047e;
        L_0x047c:
            r0 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        L_0x047e:
            r3 = 1;
        L_0x047f:
            r4 = 2;
            r3 = r3 * 2;
            r4 = r3 * 2;
            r4 = (float) r4;	 Catch:{ Throwable -> 0x048d }
            r4 = (r4 > r0 ? 1 : (r4 == r0 ? 0 : -1));
            if (r4 < 0) goto L_0x047f;
        L_0x0489:
            r11.inSampleSize = r3;	 Catch:{ Throwable -> 0x048d }
            r3 = 0;
            goto L_0x0492;
        L_0x048d:
            r0 = move-exception;
            r3 = 0;
            goto L_0x049c;
        L_0x0490:
            r3 = 0;
            r5 = 0;
        L_0x0492:
            r22 = 0;
            r25 = 0;
            r26 = 0;
            goto L_0x04a5;
        L_0x0499:
            r0 = move-exception;
            r3 = 0;
        L_0x049b:
            r5 = 0;
        L_0x049c:
            r22 = 0;
        L_0x049e:
            r25 = 0;
            r26 = 0;
        L_0x04a2:
            org.telegram.messenger.FileLog.e(r0);
        L_0x04a5:
            r4 = r22;
            r0 = r26;
            r6 = r1.cacheImage;
            r6 = r6.imageType;
            r21 = r4;
            r7 = r5;
            r4 = 1;
            if (r6 != r4) goto L_0x06bc;
        L_0x04b3:
            r4 = org.telegram.messenger.ImageLoader.this;	 Catch:{ Throwable -> 0x06b2 }
            r5 = java.lang.System.currentTimeMillis();	 Catch:{ Throwable -> 0x06b2 }
            r4.lastCacheOutTime = r5;	 Catch:{ Throwable -> 0x06b2 }
            r4 = r1.sync;	 Catch:{ Throwable -> 0x06b2 }
            monitor-enter(r4);	 Catch:{ Throwable -> 0x06b2 }
            r5 = r1.isCancelled;	 Catch:{ all -> 0x06af }
            if (r5 == 0) goto L_0x04c5;
        L_0x04c3:
            monitor-exit(r4);	 Catch:{ all -> 0x06af }
            return;
        L_0x04c5:
            monitor-exit(r4);	 Catch:{ all -> 0x06af }
            if (r12 == 0) goto L_0x050c;
        L_0x04c8:
            r4 = new java.io.RandomAccessFile;	 Catch:{ Throwable -> 0x06b2 }
            r5 = "r";
            r4.<init>(r2, r5);	 Catch:{ Throwable -> 0x06b2 }
            r12 = r4.getChannel();	 Catch:{ Throwable -> 0x06b2 }
            r13 = java.nio.channels.FileChannel.MapMode.READ_ONLY;	 Catch:{ Throwable -> 0x06b2 }
            r14 = 0;
            r16 = r2.length();	 Catch:{ Throwable -> 0x06b2 }
            r5 = r12.map(r13, r14, r16);	 Catch:{ Throwable -> 0x06b2 }
            r6 = new android.graphics.BitmapFactory$Options;	 Catch:{ Throwable -> 0x06b2 }
            r6.<init>();	 Catch:{ Throwable -> 0x06b2 }
            r8 = 1;
            r6.inJustDecodeBounds = r8;	 Catch:{ Throwable -> 0x06b2 }
            r9 = r5.limit();	 Catch:{ Throwable -> 0x06b2 }
            r10 = 0;
            org.telegram.messenger.Utilities.loadWebpImage(r10, r5, r9, r6, r8);	 Catch:{ Throwable -> 0x06b2 }
            r8 = r6.outWidth;	 Catch:{ Throwable -> 0x06b2 }
            r6 = r6.outHeight;	 Catch:{ Throwable -> 0x06b2 }
            r9 = android.graphics.Bitmap.Config.ARGB_8888;	 Catch:{ Throwable -> 0x06b2 }
            r6 = org.telegram.messenger.Bitmaps.createBitmap(r8, r6, r9);	 Catch:{ Throwable -> 0x06b2 }
            r7 = r5.limit();	 Catch:{ Throwable -> 0x052e }
            r8 = r11.inPurgeable;	 Catch:{ Throwable -> 0x052e }
            if (r8 != 0) goto L_0x0503;
        L_0x0501:
            r8 = 1;
            goto L_0x0504;
        L_0x0503:
            r8 = 0;
        L_0x0504:
            r9 = 0;
            org.telegram.messenger.Utilities.loadWebpImage(r6, r5, r7, r9, r8);	 Catch:{ Throwable -> 0x052e }
            r4.close();	 Catch:{ Throwable -> 0x052e }
            goto L_0x052c;
        L_0x050c:
            r4 = r11.inPurgeable;	 Catch:{ Throwable -> 0x06b2 }
            if (r4 != 0) goto L_0x0532;
        L_0x0510:
            if (r9 == 0) goto L_0x0513;
        L_0x0512:
            goto L_0x0532;
        L_0x0513:
            if (r8 == 0) goto L_0x051f;
        L_0x0515:
            r4 = new org.telegram.messenger.secretmedia.EncryptedFileInputStream;	 Catch:{ Throwable -> 0x06b2 }
            r5 = r1.cacheImage;	 Catch:{ Throwable -> 0x06b2 }
            r5 = r5.encryptionKeyPath;	 Catch:{ Throwable -> 0x06b2 }
            r4.<init>(r2, r5);	 Catch:{ Throwable -> 0x06b2 }
            goto L_0x0524;
        L_0x051f:
            r4 = new java.io.FileInputStream;	 Catch:{ Throwable -> 0x06b2 }
            r4.<init>(r2);	 Catch:{ Throwable -> 0x06b2 }
        L_0x0524:
            r5 = 0;
            r6 = android.graphics.BitmapFactory.decodeStream(r4, r5, r11);	 Catch:{ Throwable -> 0x06b2 }
            r4.close();	 Catch:{ Throwable -> 0x052e }
        L_0x052c:
            r5 = r6;
            goto L_0x0591;
        L_0x052e:
            r0 = move-exception;
            r2 = r6;
            goto L_0x06b4;
        L_0x0532:
            r4 = new java.io.RandomAccessFile;	 Catch:{ Throwable -> 0x06b2 }
            r5 = "r";
            r4.<init>(r2, r5);	 Catch:{ Throwable -> 0x06b2 }
            r5 = r4.length();	 Catch:{ Throwable -> 0x06b2 }
            r6 = (int) r5;	 Catch:{ Throwable -> 0x06b2 }
            r5 = org.telegram.messenger.ImageLoader.bytesThumbLocal;	 Catch:{ Throwable -> 0x06b2 }
            r5 = r5.get();	 Catch:{ Throwable -> 0x06b2 }
            r5 = (byte[]) r5;	 Catch:{ Throwable -> 0x06b2 }
            if (r5 == 0) goto L_0x054e;
        L_0x054a:
            r12 = r5.length;	 Catch:{ Throwable -> 0x06b2 }
            if (r12 < r6) goto L_0x054e;
        L_0x054d:
            goto L_0x054f;
        L_0x054e:
            r5 = 0;
        L_0x054f:
            if (r5 != 0) goto L_0x055a;
        L_0x0551:
            r5 = new byte[r6];	 Catch:{ Throwable -> 0x06b2 }
            r12 = org.telegram.messenger.ImageLoader.bytesThumbLocal;	 Catch:{ Throwable -> 0x06b2 }
            r12.set(r5);	 Catch:{ Throwable -> 0x06b2 }
        L_0x055a:
            r12 = 0;
            r4.readFully(r5, r12, r6);	 Catch:{ Throwable -> 0x06b2 }
            r4.close();	 Catch:{ Throwable -> 0x06b2 }
            if (r9 == 0) goto L_0x057d;
        L_0x0563:
            org.telegram.messenger.secretmedia.EncryptedFileInputStream.decryptBytesWithKeyFile(r5, r12, r6, r9);	 Catch:{ Throwable -> 0x06b2 }
            r4 = org.telegram.messenger.Utilities.computeSHA256(r5, r12, r6);	 Catch:{ Throwable -> 0x06b2 }
            if (r10 == 0) goto L_0x0575;
        L_0x056c:
            r4 = java.util.Arrays.equals(r4, r10);	 Catch:{ Throwable -> 0x06b2 }
            if (r4 != 0) goto L_0x0573;
        L_0x0572:
            goto L_0x0575;
        L_0x0573:
            r4 = 0;
            goto L_0x0576;
        L_0x0575:
            r4 = 1;
        L_0x0576:
            r8 = 0;
            r9 = r5[r8];	 Catch:{ Throwable -> 0x06b2 }
            r8 = r9 & 255;
            r6 = r6 - r8;
            goto L_0x0589;
        L_0x057d:
            if (r8 == 0) goto L_0x0587;
        L_0x057f:
            r4 = r1.cacheImage;	 Catch:{ Throwable -> 0x06b2 }
            r4 = r4.encryptionKeyPath;	 Catch:{ Throwable -> 0x06b2 }
            r8 = 0;
            org.telegram.messenger.secretmedia.EncryptedFileInputStream.decryptBytesWithKeyFile(r5, r8, r6, r4);	 Catch:{ Throwable -> 0x06b2 }
        L_0x0587:
            r4 = 0;
            r8 = 0;
        L_0x0589:
            if (r4 != 0) goto L_0x0590;
        L_0x058b:
            r5 = android.graphics.BitmapFactory.decodeByteArray(r5, r8, r6, r11);	 Catch:{ Throwable -> 0x06b2 }
            goto L_0x0591;
        L_0x0590:
            r5 = r7;
        L_0x0591:
            if (r5 != 0) goto L_0x05aa;
        L_0x0593:
            r3 = r2.length();	 Catch:{ Throwable -> 0x06ac }
            r6 = 0;
            r0 = (r3 > r6 ? 1 : (r3 == r6 ? 0 : -1));
            if (r0 == 0) goto L_0x05a3;
        L_0x059d:
            r0 = r1.cacheImage;	 Catch:{ Throwable -> 0x06ac }
            r0 = r0.filter;	 Catch:{ Throwable -> 0x06ac }
            if (r0 != 0) goto L_0x05a6;
        L_0x05a3:
            r2.delete();	 Catch:{ Throwable -> 0x06ac }
        L_0x05a6:
            r2 = r5;
            r7 = 0;
            goto L_0x06b8;
        L_0x05aa:
            r2 = r1.cacheImage;	 Catch:{ Throwable -> 0x06ac }
            r2 = r2.filter;	 Catch:{ Throwable -> 0x06ac }
            if (r2 == 0) goto L_0x05dc;
        L_0x05b0:
            r2 = r5.getWidth();	 Catch:{ Throwable -> 0x06ac }
            r2 = (float) r2;	 Catch:{ Throwable -> 0x06ac }
            r4 = r5.getHeight();	 Catch:{ Throwable -> 0x06ac }
            r4 = (float) r4;	 Catch:{ Throwable -> 0x06ac }
            r6 = r11.inPurgeable;	 Catch:{ Throwable -> 0x06ac }
            if (r6 != 0) goto L_0x05dc;
        L_0x05be:
            r6 = (r3 > r20 ? 1 : (r3 == r20 ? 0 : -1));
            if (r6 == 0) goto L_0x05dc;
        L_0x05c2:
            r6 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1));
            if (r6 == 0) goto L_0x05dc;
        L_0x05c6:
            r6 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
            r6 = r6 + r3;
            r6 = (r2 > r6 ? 1 : (r2 == r6 ? 0 : -1));
            if (r6 <= 0) goto L_0x05dc;
        L_0x05cd:
            r2 = r2 / r3;
            r3 = (int) r3;	 Catch:{ Throwable -> 0x06ac }
            r4 = r4 / r2;
            r2 = (int) r4;	 Catch:{ Throwable -> 0x06ac }
            r4 = 1;
            r2 = org.telegram.messenger.Bitmaps.createScaledBitmap(r5, r3, r2, r4);	 Catch:{ Throwable -> 0x06ac }
            if (r5 == r2) goto L_0x05dc;
        L_0x05d8:
            r5.recycle();	 Catch:{ Throwable -> 0x06ac }
            goto L_0x05dd;
        L_0x05dc:
            r2 = r5;
        L_0x05dd:
            if (r25 == 0) goto L_0x0600;
        L_0x05df:
            r3 = r11.inPurgeable;	 Catch:{ Throwable -> 0x05fd }
            if (r3 == 0) goto L_0x05e5;
        L_0x05e3:
            r3 = 0;
            goto L_0x05e6;
        L_0x05e5:
            r3 = 1;
        L_0x05e6:
            r4 = r2.getWidth();	 Catch:{ Throwable -> 0x05fd }
            r5 = r2.getHeight();	 Catch:{ Throwable -> 0x05fd }
            r6 = r2.getRowBytes();	 Catch:{ Throwable -> 0x05fd }
            r3 = org.telegram.messenger.Utilities.needInvert(r2, r3, r4, r5, r6);	 Catch:{ Throwable -> 0x05fd }
            if (r3 == 0) goto L_0x05fa;
        L_0x05f8:
            r7 = 1;
            goto L_0x05fb;
        L_0x05fa:
            r7 = 0;
        L_0x05fb:
            r3 = 1;
            goto L_0x0602;
        L_0x05fd:
            r0 = move-exception;
            goto L_0x06b4;
        L_0x0600:
            r3 = 1;
            r7 = 0;
        L_0x0602:
            if (r0 != r3) goto L_0x0629;
        L_0x0604:
            r0 = r2.getConfig();	 Catch:{ Throwable -> 0x0626 }
            r3 = android.graphics.Bitmap.Config.ARGB_8888;	 Catch:{ Throwable -> 0x0626 }
            if (r0 != r3) goto L_0x06b8;
        L_0x060c:
            r13 = 3;
            r0 = r11.inPurgeable;	 Catch:{ Throwable -> 0x0626 }
            if (r0 == 0) goto L_0x0613;
        L_0x0611:
            r14 = 0;
            goto L_0x0614;
        L_0x0613:
            r14 = 1;
        L_0x0614:
            r15 = r2.getWidth();	 Catch:{ Throwable -> 0x0626 }
            r16 = r2.getHeight();	 Catch:{ Throwable -> 0x0626 }
            r17 = r2.getRowBytes();	 Catch:{ Throwable -> 0x0626 }
            r12 = r2;
            org.telegram.messenger.Utilities.blurBitmap(r12, r13, r14, r15, r16, r17);	 Catch:{ Throwable -> 0x0626 }
            goto L_0x06b8;
        L_0x0626:
            r0 = move-exception;
            goto L_0x06b5;
        L_0x0629:
            r3 = 2;
            if (r0 != r3) goto L_0x064e;
        L_0x062c:
            r0 = r2.getConfig();	 Catch:{ Throwable -> 0x0626 }
            r3 = android.graphics.Bitmap.Config.ARGB_8888;	 Catch:{ Throwable -> 0x0626 }
            if (r0 != r3) goto L_0x06b8;
        L_0x0634:
            r13 = 1;
            r0 = r11.inPurgeable;	 Catch:{ Throwable -> 0x0626 }
            if (r0 == 0) goto L_0x063b;
        L_0x0639:
            r14 = 0;
            goto L_0x063c;
        L_0x063b:
            r14 = 1;
        L_0x063c:
            r15 = r2.getWidth();	 Catch:{ Throwable -> 0x0626 }
            r16 = r2.getHeight();	 Catch:{ Throwable -> 0x0626 }
            r17 = r2.getRowBytes();	 Catch:{ Throwable -> 0x0626 }
            r12 = r2;
            org.telegram.messenger.Utilities.blurBitmap(r12, r13, r14, r15, r16, r17);	 Catch:{ Throwable -> 0x0626 }
            goto L_0x06b8;
        L_0x064e:
            r3 = 3;
            if (r0 != r3) goto L_0x06a2;
        L_0x0651:
            r0 = r2.getConfig();	 Catch:{ Throwable -> 0x0626 }
            r3 = android.graphics.Bitmap.Config.ARGB_8888;	 Catch:{ Throwable -> 0x0626 }
            if (r0 != r3) goto L_0x06b8;
        L_0x0659:
            r13 = 7;
            r0 = r11.inPurgeable;	 Catch:{ Throwable -> 0x0626 }
            if (r0 == 0) goto L_0x0660;
        L_0x065e:
            r14 = 0;
            goto L_0x0661;
        L_0x0660:
            r14 = 1;
        L_0x0661:
            r15 = r2.getWidth();	 Catch:{ Throwable -> 0x0626 }
            r16 = r2.getHeight();	 Catch:{ Throwable -> 0x0626 }
            r17 = r2.getRowBytes();	 Catch:{ Throwable -> 0x0626 }
            r12 = r2;
            org.telegram.messenger.Utilities.blurBitmap(r12, r13, r14, r15, r16, r17);	 Catch:{ Throwable -> 0x0626 }
            r13 = 7;
            r0 = r11.inPurgeable;	 Catch:{ Throwable -> 0x0626 }
            if (r0 == 0) goto L_0x0678;
        L_0x0676:
            r14 = 0;
            goto L_0x0679;
        L_0x0678:
            r14 = 1;
        L_0x0679:
            r15 = r2.getWidth();	 Catch:{ Throwable -> 0x0626 }
            r16 = r2.getHeight();	 Catch:{ Throwable -> 0x0626 }
            r17 = r2.getRowBytes();	 Catch:{ Throwable -> 0x0626 }
            r12 = r2;
            org.telegram.messenger.Utilities.blurBitmap(r12, r13, r14, r15, r16, r17);	 Catch:{ Throwable -> 0x0626 }
            r13 = 7;
            r0 = r11.inPurgeable;	 Catch:{ Throwable -> 0x0626 }
            if (r0 == 0) goto L_0x0690;
        L_0x068e:
            r14 = 0;
            goto L_0x0691;
        L_0x0690:
            r14 = 1;
        L_0x0691:
            r15 = r2.getWidth();	 Catch:{ Throwable -> 0x0626 }
            r16 = r2.getHeight();	 Catch:{ Throwable -> 0x0626 }
            r17 = r2.getRowBytes();	 Catch:{ Throwable -> 0x0626 }
            r12 = r2;
            org.telegram.messenger.Utilities.blurBitmap(r12, r13, r14, r15, r16, r17);	 Catch:{ Throwable -> 0x0626 }
            goto L_0x06b8;
        L_0x06a2:
            if (r0 != 0) goto L_0x06b8;
        L_0x06a4:
            r0 = r11.inPurgeable;	 Catch:{ Throwable -> 0x0626 }
            if (r0 == 0) goto L_0x06b8;
        L_0x06a8:
            org.telegram.messenger.Utilities.pinBitmap(r2);	 Catch:{ Throwable -> 0x0626 }
            goto L_0x06b8;
        L_0x06ac:
            r0 = move-exception;
            r2 = r5;
            goto L_0x06b4;
        L_0x06af:
            r0 = move-exception;
            monitor-exit(r4);	 Catch:{ all -> 0x06af }
            throw r0;	 Catch:{ Throwable -> 0x06b2 }
        L_0x06b2:
            r0 = move-exception;
            r2 = r7;
        L_0x06b4:
            r7 = 0;
        L_0x06b5:
            org.telegram.messenger.FileLog.e(r0);
        L_0x06b8:
            r0 = 0;
            r4 = 0;
            goto L_0x0968;
        L_0x06bc:
            r4 = 20;
            if (r14 == 0) goto L_0x06c1;
        L_0x06c0:
            r4 = 0;
        L_0x06c1:
            if (r4 == 0) goto L_0x06f0;
        L_0x06c3:
            r5 = org.telegram.messenger.ImageLoader.this;	 Catch:{ Throwable -> 0x06ec }
            r5 = r5.lastCacheOutTime;	 Catch:{ Throwable -> 0x06ec }
            r22 = 0;
            r27 = (r5 > r22 ? 1 : (r5 == r22 ? 0 : -1));
            if (r27 == 0) goto L_0x06f0;
        L_0x06cf:
            r5 = org.telegram.messenger.ImageLoader.this;	 Catch:{ Throwable -> 0x06ec }
            r5 = r5.lastCacheOutTime;	 Catch:{ Throwable -> 0x06ec }
            r22 = java.lang.System.currentTimeMillis();	 Catch:{ Throwable -> 0x06ec }
            r27 = r3;
            r3 = (long) r4;	 Catch:{ Throwable -> 0x06ec }
            r22 = r22 - r3;
            r28 = (r5 > r22 ? 1 : (r5 == r22 ? 0 : -1));
            if (r28 <= 0) goto L_0x06f2;
        L_0x06e2:
            r5 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Throwable -> 0x06ec }
            r6 = 21;
            if (r5 >= r6) goto L_0x06f2;
        L_0x06e8:
            java.lang.Thread.sleep(r3);	 Catch:{ Throwable -> 0x06ec }
            goto L_0x06f2;
        L_0x06ec:
            r2 = r7;
        L_0x06ed:
            r4 = 0;
            goto L_0x0963;
        L_0x06f0:
            r27 = r3;
        L_0x06f2:
            r3 = org.telegram.messenger.ImageLoader.this;	 Catch:{ Throwable -> 0x0960 }
            r4 = java.lang.System.currentTimeMillis();	 Catch:{ Throwable -> 0x0960 }
            r3.lastCacheOutTime = r4;	 Catch:{ Throwable -> 0x0960 }
            r3 = r1.sync;	 Catch:{ Throwable -> 0x0960 }
            monitor-enter(r3);	 Catch:{ Throwable -> 0x0960 }
            r4 = r1.isCancelled;	 Catch:{ all -> 0x0959 }
            if (r4 == 0) goto L_0x0704;
        L_0x0702:
            monitor-exit(r3);	 Catch:{ all -> 0x0959 }
            return;
        L_0x0704:
            monitor-exit(r3);	 Catch:{ all -> 0x0959 }
            if (r19 != 0) goto L_0x071d;
        L_0x0707:
            r3 = r1.cacheImage;	 Catch:{ Throwable -> 0x06ec }
            r3 = r3.filter;	 Catch:{ Throwable -> 0x06ec }
            if (r3 == 0) goto L_0x071d;
        L_0x070d:
            if (r0 != 0) goto L_0x071d;
        L_0x070f:
            r3 = r1.cacheImage;	 Catch:{ Throwable -> 0x06ec }
            r3 = r3.imageLocation;	 Catch:{ Throwable -> 0x06ec }
            r3 = r3.path;	 Catch:{ Throwable -> 0x06ec }
            if (r3 == 0) goto L_0x0718;
        L_0x0717:
            goto L_0x071d;
        L_0x0718:
            r3 = android.graphics.Bitmap.Config.RGB_565;	 Catch:{ Throwable -> 0x06ec }
            r11.inPreferredConfig = r3;	 Catch:{ Throwable -> 0x06ec }
            goto L_0x0721;
        L_0x071d:
            r3 = android.graphics.Bitmap.Config.ARGB_8888;	 Catch:{ Throwable -> 0x0960 }
            r11.inPreferredConfig = r3;	 Catch:{ Throwable -> 0x0960 }
        L_0x0721:
            r3 = 0;
            r11.inDither = r3;	 Catch:{ Throwable -> 0x0960 }
            if (r14 == 0) goto L_0x074a;
        L_0x0726:
            if (r13 != 0) goto L_0x074a;
        L_0x0728:
            if (r15 == 0) goto L_0x073a;
        L_0x072a:
            r3 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Throwable -> 0x06ec }
            r3 = r3.getContentResolver();	 Catch:{ Throwable -> 0x06ec }
            r4 = r14.longValue();	 Catch:{ Throwable -> 0x06ec }
            r6 = 1;
            r5 = android.provider.MediaStore.Video.Thumbnails.getThumbnail(r3, r4, r6, r11);	 Catch:{ Throwable -> 0x06ec }
            goto L_0x074b;
        L_0x073a:
            r3 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Throwable -> 0x06ec }
            r3 = r3.getContentResolver();	 Catch:{ Throwable -> 0x06ec }
            r4 = r14.longValue();	 Catch:{ Throwable -> 0x06ec }
            r6 = 1;
            r5 = android.provider.MediaStore.Images.Thumbnails.getThumbnail(r3, r4, r6, r11);	 Catch:{ Throwable -> 0x06ec }
            goto L_0x074b;
        L_0x074a:
            r5 = r7;
        L_0x074b:
            if (r5 != 0) goto L_0x0860;
        L_0x074d:
            if (r12 == 0) goto L_0x079f;
        L_0x074f:
            r3 = new java.io.RandomAccessFile;	 Catch:{ Throwable -> 0x079c }
            r4 = "r";
            r3.<init>(r2, r4);	 Catch:{ Throwable -> 0x079c }
            r28 = r3.getChannel();	 Catch:{ Throwable -> 0x079c }
            r29 = java.nio.channels.FileChannel.MapMode.READ_ONLY;	 Catch:{ Throwable -> 0x079c }
            r30 = 0;
            r32 = r2.length();	 Catch:{ Throwable -> 0x079c }
            r4 = r28.map(r29, r30, r32);	 Catch:{ Throwable -> 0x079c }
            r6 = new android.graphics.BitmapFactory$Options;	 Catch:{ Throwable -> 0x079c }
            r6.<init>();	 Catch:{ Throwable -> 0x079c }
            r7 = 1;
            r6.inJustDecodeBounds = r7;	 Catch:{ Throwable -> 0x079c }
            r8 = r4.limit();	 Catch:{ Throwable -> 0x079c }
            r9 = 0;
            org.telegram.messenger.Utilities.loadWebpImage(r9, r4, r8, r6, r7);	 Catch:{ Throwable -> 0x0798 }
            r7 = r6.outWidth;	 Catch:{ Throwable -> 0x079c }
            r6 = r6.outHeight;	 Catch:{ Throwable -> 0x079c }
            r8 = android.graphics.Bitmap.Config.ARGB_8888;	 Catch:{ Throwable -> 0x079c }
            r5 = org.telegram.messenger.Bitmaps.createBitmap(r7, r6, r8);	 Catch:{ Throwable -> 0x079c }
            r6 = r4.limit();	 Catch:{ Throwable -> 0x079c }
            r7 = r11.inPurgeable;	 Catch:{ Throwable -> 0x079c }
            if (r7 != 0) goto L_0x078a;
        L_0x0788:
            r7 = 1;
            goto L_0x078b;
        L_0x078a:
            r7 = 0;
        L_0x078b:
            r8 = 0;
            org.telegram.messenger.Utilities.loadWebpImage(r5, r4, r6, r8, r7);	 Catch:{ Throwable -> 0x0794 }
            r3.close();	 Catch:{ Throwable -> 0x079c }
            goto L_0x0860;
        L_0x0794:
            r2 = r5;
            r4 = r8;
            goto L_0x0963;
        L_0x0798:
            r2 = r5;
            r4 = r9;
            goto L_0x0963;
        L_0x079c:
            r2 = r5;
            goto L_0x06ed;
        L_0x079f:
            r3 = r11.inPurgeable;	 Catch:{ Throwable -> 0x085c }
            if (r3 != 0) goto L_0x07fd;
        L_0x07a3:
            if (r9 == 0) goto L_0x07a6;
        L_0x07a5:
            goto L_0x07fd;
        L_0x07a6:
            if (r8 == 0) goto L_0x07b2;
        L_0x07a8:
            r3 = new org.telegram.messenger.secretmedia.EncryptedFileInputStream;	 Catch:{ Throwable -> 0x079c }
            r4 = r1.cacheImage;	 Catch:{ Throwable -> 0x079c }
            r4 = r4.encryptionKeyPath;	 Catch:{ Throwable -> 0x079c }
            r3.<init>(r2, r4);	 Catch:{ Throwable -> 0x079c }
            goto L_0x07b7;
        L_0x07b2:
            r3 = new java.io.FileInputStream;	 Catch:{ Throwable -> 0x085c }
            r3.<init>(r2);	 Catch:{ Throwable -> 0x085c }
        L_0x07b7:
            r4 = r1.cacheImage;	 Catch:{ Throwable -> 0x085c }
            r4 = r4.imageLocation;	 Catch:{ Throwable -> 0x085c }
            r4 = r4.document;	 Catch:{ Throwable -> 0x085c }
            r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_document;	 Catch:{ Throwable -> 0x085c }
            if (r4 == 0) goto L_0x07f3;
        L_0x07c1:
            r4 = new androidx.exifinterface.media.ExifInterface;	 Catch:{ Throwable -> 0x07e1 }
            r4.<init>(r3);	 Catch:{ Throwable -> 0x07e1 }
            r6 = "Orientation";
            r7 = 1;
            r4 = r4.getAttributeInt(r6, r7);	 Catch:{ Throwable -> 0x07e1 }
            r6 = 3;
            if (r4 == r6) goto L_0x07de;
        L_0x07d0:
            r6 = 6;
            if (r4 == r6) goto L_0x07db;
        L_0x07d3:
            r6 = 8;
            if (r4 == r6) goto L_0x07d8;
        L_0x07d7:
            goto L_0x07e1;
        L_0x07d8:
            r7 = 270; // 0x10e float:3.78E-43 double:1.334E-321;
            goto L_0x07e2;
        L_0x07db:
            r7 = 90;
            goto L_0x07e2;
        L_0x07de:
            r7 = 180; // 0xb4 float:2.52E-43 double:8.9E-322;
            goto L_0x07e2;
        L_0x07e1:
            r7 = 0;
        L_0x07e2:
            r4 = r3.getChannel();	 Catch:{ Throwable -> 0x07ed }
            r8 = 0;
            r4.position(r8);	 Catch:{ Throwable -> 0x07ed }
            r4 = 0;
            goto L_0x07f5;
        L_0x07ed:
            r2 = r5;
            r24 = r7;
            r4 = 0;
            goto L_0x0957;
        L_0x07f3:
            r4 = 0;
            r7 = 0;
        L_0x07f5:
            r5 = android.graphics.BitmapFactory.decodeStream(r3, r4, r11);	 Catch:{ Throwable -> 0x0954 }
            r3.close();	 Catch:{ Throwable -> 0x0954 }
            goto L_0x0862;
        L_0x07fd:
            r4 = 0;
            r3 = new java.io.RandomAccessFile;	 Catch:{ Throwable -> 0x085d }
            r6 = "r";
            r3.<init>(r2, r6);	 Catch:{ Throwable -> 0x085d }
            r6 = r3.length();	 Catch:{ Throwable -> 0x085d }
            r7 = (int) r6;	 Catch:{ Throwable -> 0x085d }
            r6 = org.telegram.messenger.ImageLoader.bytesLocal;	 Catch:{ Throwable -> 0x085d }
            r6 = r6.get();	 Catch:{ Throwable -> 0x085d }
            r6 = (byte[]) r6;	 Catch:{ Throwable -> 0x085d }
            if (r6 == 0) goto L_0x081a;
        L_0x0816:
            r12 = r6.length;	 Catch:{ Throwable -> 0x085d }
            if (r12 < r7) goto L_0x081a;
        L_0x0819:
            goto L_0x081b;
        L_0x081a:
            r6 = r4;
        L_0x081b:
            if (r6 != 0) goto L_0x0826;
        L_0x081d:
            r6 = new byte[r7];	 Catch:{ Throwable -> 0x085d }
            r12 = org.telegram.messenger.ImageLoader.bytesLocal;	 Catch:{ Throwable -> 0x085d }
            r12.set(r6);	 Catch:{ Throwable -> 0x085d }
        L_0x0826:
            r12 = 0;
            r3.readFully(r6, r12, r7);	 Catch:{ Throwable -> 0x085d }
            r3.close();	 Catch:{ Throwable -> 0x085d }
            if (r9 == 0) goto L_0x0849;
        L_0x082f:
            org.telegram.messenger.secretmedia.EncryptedFileInputStream.decryptBytesWithKeyFile(r6, r12, r7, r9);	 Catch:{ Throwable -> 0x085d }
            r3 = org.telegram.messenger.Utilities.computeSHA256(r6, r12, r7);	 Catch:{ Throwable -> 0x085d }
            if (r10 == 0) goto L_0x0841;
        L_0x0838:
            r3 = java.util.Arrays.equals(r3, r10);	 Catch:{ Throwable -> 0x085d }
            if (r3 != 0) goto L_0x083f;
        L_0x083e:
            goto L_0x0841;
        L_0x083f:
            r3 = 0;
            goto L_0x0842;
        L_0x0841:
            r3 = 1;
        L_0x0842:
            r8 = 0;
            r9 = r6[r8];	 Catch:{ Throwable -> 0x085d }
            r8 = r9 & 255;
            r7 = r7 - r8;
            goto L_0x0855;
        L_0x0849:
            if (r8 == 0) goto L_0x0853;
        L_0x084b:
            r3 = r1.cacheImage;	 Catch:{ Throwable -> 0x085d }
            r3 = r3.encryptionKeyPath;	 Catch:{ Throwable -> 0x085d }
            r8 = 0;
            org.telegram.messenger.secretmedia.EncryptedFileInputStream.decryptBytesWithKeyFile(r6, r8, r7, r3);	 Catch:{ Throwable -> 0x085d }
        L_0x0853:
            r3 = 0;
            r8 = 0;
        L_0x0855:
            if (r3 != 0) goto L_0x0861;
        L_0x0857:
            r5 = android.graphics.BitmapFactory.decodeByteArray(r6, r8, r7, r11);	 Catch:{ Throwable -> 0x085d }
            goto L_0x0861;
        L_0x085c:
            r4 = 0;
        L_0x085d:
            r2 = r5;
            goto L_0x0963;
        L_0x0860:
            r4 = 0;
        L_0x0861:
            r7 = 0;
        L_0x0862:
            if (r5 != 0) goto L_0x087d;
        L_0x0864:
            if (r16 == 0) goto L_0x0879;
        L_0x0866:
            r8 = r2.length();	 Catch:{ Throwable -> 0x0954 }
            r10 = 0;
            r0 = (r8 > r10 ? 1 : (r8 == r10 ? 0 : -1));
            if (r0 == 0) goto L_0x0876;
        L_0x0870:
            r0 = r1.cacheImage;	 Catch:{ Throwable -> 0x0954 }
            r0 = r0.filter;	 Catch:{ Throwable -> 0x0954 }
            if (r0 != 0) goto L_0x0879;
        L_0x0876:
            r2.delete();	 Catch:{ Throwable -> 0x0954 }
        L_0x0879:
            r2 = r5;
            r8 = 0;
            goto L_0x0950;
        L_0x087d:
            r2 = r1.cacheImage;	 Catch:{ Throwable -> 0x0953 }
            r2 = r2.filter;	 Catch:{ Throwable -> 0x0953 }
            if (r2 == 0) goto L_0x093e;
        L_0x0883:
            r2 = r5.getWidth();	 Catch:{ Throwable -> 0x0953 }
            r2 = (float) r2;	 Catch:{ Throwable -> 0x0953 }
            r3 = r5.getHeight();	 Catch:{ Throwable -> 0x0953 }
            r3 = (float) r3;	 Catch:{ Throwable -> 0x0953 }
            r6 = r11.inPurgeable;	 Catch:{ Throwable -> 0x0953 }
            if (r6 != 0) goto L_0x08cb;
        L_0x0891:
            r6 = (r27 > r20 ? 1 : (r27 == r20 ? 0 : -1));
            if (r6 == 0) goto L_0x08cb;
        L_0x0895:
            r6 = (r2 > r27 ? 1 : (r2 == r27 ? 0 : -1));
            if (r6 == 0) goto L_0x08cb;
        L_0x0899:
            r6 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
            r6 = r27 + r6;
            r6 = (r2 > r6 ? 1 : (r2 == r6 ? 0 : -1));
            if (r6 <= 0) goto L_0x08cb;
        L_0x08a1:
            r6 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1));
            if (r6 <= 0) goto L_0x08b7;
        L_0x08a5:
            r6 = (r27 > r21 ? 1 : (r27 == r21 ? 0 : -1));
            if (r6 <= 0) goto L_0x08b7;
        L_0x08a9:
            r6 = r2 / r27;
            r8 = r27;
            r8 = (int) r8;
            r6 = r3 / r6;
            r6 = (int) r6;
            r9 = 1;
            r6 = org.telegram.messenger.Bitmaps.createScaledBitmap(r5, r8, r6, r9);	 Catch:{ Throwable -> 0x0954 }
            goto L_0x08c4;
        L_0x08b7:
            r9 = 1;
            r6 = r3 / r21;
            r6 = r2 / r6;
            r6 = (int) r6;	 Catch:{ Throwable -> 0x0954 }
            r8 = r21;
            r8 = (int) r8;	 Catch:{ Throwable -> 0x0954 }
            r6 = org.telegram.messenger.Bitmaps.createScaledBitmap(r5, r6, r8, r9);	 Catch:{ Throwable -> 0x0954 }
        L_0x08c4:
            if (r5 == r6) goto L_0x08cc;
        L_0x08c6:
            r5.recycle();	 Catch:{ Throwable -> 0x0954 }
            r5 = r6;
            goto L_0x08cc;
        L_0x08cb:
            r9 = 1;
        L_0x08cc:
            if (r5 == 0) goto L_0x093e;
        L_0x08ce:
            if (r25 == 0) goto L_0x090c;
        L_0x08d0:
            r6 = r5.getWidth();	 Catch:{ Throwable -> 0x0953 }
            r8 = r5.getHeight();	 Catch:{ Throwable -> 0x0953 }
            r6 = r6 * r8;
            r8 = 22500; // 0x57e4 float:3.1529E-41 double:1.11165E-319;
            if (r6 <= r8) goto L_0x08e8;
        L_0x08de:
            r6 = 100;
            r8 = 100;
            r10 = 0;
            r6 = org.telegram.messenger.Bitmaps.createScaledBitmap(r5, r6, r8, r10);	 Catch:{ Throwable -> 0x0954 }
            goto L_0x08ea;
        L_0x08e8:
            r10 = 0;
            r6 = r5;
        L_0x08ea:
            r8 = r11.inPurgeable;	 Catch:{ Throwable -> 0x0954 }
            if (r8 == 0) goto L_0x08f0;
        L_0x08ee:
            r8 = 0;
            goto L_0x08f1;
        L_0x08f0:
            r8 = 1;
        L_0x08f1:
            r12 = r6.getWidth();	 Catch:{ Throwable -> 0x0954 }
            r13 = r6.getHeight();	 Catch:{ Throwable -> 0x0954 }
            r14 = r6.getRowBytes();	 Catch:{ Throwable -> 0x0954 }
            r8 = org.telegram.messenger.Utilities.needInvert(r6, r8, r12, r13, r14);	 Catch:{ Throwable -> 0x0954 }
            if (r8 == 0) goto L_0x0905;
        L_0x0903:
            r8 = 1;
            goto L_0x0906;
        L_0x0905:
            r8 = 0;
        L_0x0906:
            if (r6 == r5) goto L_0x090e;
        L_0x0908:
            r6.recycle();	 Catch:{ Throwable -> 0x094a }
            goto L_0x090e;
        L_0x090c:
            r10 = 0;
            r8 = 0;
        L_0x090e:
            if (r0 == 0) goto L_0x0940;
        L_0x0910:
            r0 = NUM; // 0x42CLASSNAME float:100.0 double:5.53552857E-315;
            r0 = (r3 > r0 ? 1 : (r3 == r0 ? 0 : -1));
            if (r0 >= 0) goto L_0x0940;
        L_0x0916:
            r0 = NUM; // 0x42CLASSNAME float:100.0 double:5.53552857E-315;
            r0 = (r2 > r0 ? 1 : (r2 == r0 ? 0 : -1));
            if (r0 >= 0) goto L_0x0940;
        L_0x091c:
            r0 = r5.getConfig();	 Catch:{ Throwable -> 0x094a }
            r2 = android.graphics.Bitmap.Config.ARGB_8888;	 Catch:{ Throwable -> 0x094a }
            if (r0 != r2) goto L_0x093c;
        L_0x0924:
            r13 = 3;
            r0 = r11.inPurgeable;	 Catch:{ Throwable -> 0x094a }
            if (r0 == 0) goto L_0x092b;
        L_0x0929:
            r14 = 0;
            goto L_0x092c;
        L_0x092b:
            r14 = 1;
        L_0x092c:
            r15 = r5.getWidth();	 Catch:{ Throwable -> 0x094a }
            r16 = r5.getHeight();	 Catch:{ Throwable -> 0x094a }
            r17 = r5.getRowBytes();	 Catch:{ Throwable -> 0x094a }
            r12 = r5;
            org.telegram.messenger.Utilities.blurBitmap(r12, r13, r14, r15, r16, r17);	 Catch:{ Throwable -> 0x094a }
        L_0x093c:
            r10 = 1;
            goto L_0x0940;
        L_0x093e:
            r10 = 0;
            r8 = 0;
        L_0x0940:
            if (r10 != 0) goto L_0x094f;
        L_0x0942:
            r0 = r11.inPurgeable;	 Catch:{ Throwable -> 0x094a }
            if (r0 == 0) goto L_0x094f;
        L_0x0946:
            org.telegram.messenger.Utilities.pinBitmap(r5);	 Catch:{ Throwable -> 0x094a }
            goto L_0x094f;
        L_0x094a:
            r2 = r5;
            r24 = r7;
            r7 = r8;
            goto L_0x0966;
        L_0x094f:
            r2 = r5;
        L_0x0950:
            r0 = r7;
            r7 = r8;
            goto L_0x0968;
        L_0x0953:
            r10 = 0;
        L_0x0954:
            r2 = r5;
            r24 = r7;
        L_0x0957:
            r7 = 0;
            goto L_0x0966;
        L_0x0959:
            r0 = move-exception;
            r4 = 0;
            r10 = 0;
        L_0x095c:
            monitor-exit(r3);	 Catch:{ all -> 0x095e }
            throw r0;	 Catch:{ Throwable -> 0x0962 }
        L_0x095e:
            r0 = move-exception;
            goto L_0x095c;
        L_0x0960:
            r4 = 0;
            r10 = 0;
        L_0x0962:
            r2 = r7;
        L_0x0963:
            r7 = 0;
            r24 = 0;
        L_0x0966:
            r0 = r24;
        L_0x0968:
            java.lang.Thread.interrupted();
            if (r7 != 0) goto L_0x097c;
        L_0x096d:
            if (r0 == 0) goto L_0x0970;
        L_0x096f:
            goto L_0x097c;
        L_0x0970:
            if (r2 == 0) goto L_0x0978;
        L_0x0972:
            r5 = new android.graphics.drawable.BitmapDrawable;
            r5.<init>(r2);
            r4 = r5;
        L_0x0978:
            r1.onPostExecute(r4);
            goto L_0x0987;
        L_0x097c:
            if (r2 == 0) goto L_0x0984;
        L_0x097e:
            r5 = new org.telegram.messenger.ExtendedBitmapDrawable;
            r5.<init>(r2, r7, r0);
            r4 = r5;
        L_0x0984:
            r1.onPostExecute(r4);
        L_0x0987:
            return;
        L_0x0988:
            r0 = move-exception;
            monitor-exit(r2);	 Catch:{ all -> 0x0988 }
            goto L_0x098c;
        L_0x098b:
            throw r0;
        L_0x098c:
            goto L_0x098b;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ImageLoader$CacheOutTask.run():void");
        }

        private void onPostExecute(Drawable drawable) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$ImageLoader$CacheOutTask$P-Q-SglLFg8CKw46QDkF5nN_7Ko(this, drawable));
        }

        public /* synthetic */ void lambda$onPostExecute$1$ImageLoader$CacheOutTask(Drawable drawable) {
            String str = null;
            Drawable drawable2;
            if (drawable instanceof RLottieDrawable) {
                drawable = (RLottieDrawable) drawable;
                drawable2 = (Drawable) ImageLoader.this.lottieMemCache.get(this.cacheImage.key);
                if (drawable2 == null) {
                    ImageLoader.this.lottieMemCache.put(this.cacheImage.key, drawable);
                } else {
                    drawable.recycle();
                    drawable = drawable2;
                }
                if (drawable != null) {
                    ImageLoader.this.incrementUseCount(this.cacheImage.key);
                    str = this.cacheImage.key;
                }
            } else if (!(drawable instanceof AnimatedFileDrawable)) {
                if (drawable instanceof BitmapDrawable) {
                    drawable = (BitmapDrawable) drawable;
                    drawable2 = (Drawable) ImageLoader.this.memCache.get(this.cacheImage.key);
                    if (drawable2 == null) {
                        ImageLoader.this.memCache.put(this.cacheImage.key, drawable);
                    } else {
                        drawable.getBitmap().recycle();
                        drawable = drawable2;
                    }
                    if (drawable != null) {
                        ImageLoader.this.incrementUseCount(this.cacheImage.key);
                        str = this.cacheImage.key;
                    }
                } else {
                    drawable = null;
                }
            }
            ImageLoader.this.imageLoadQueue.postRunnable(new -$$Lambda$ImageLoader$CacheOutTask$L2FS7HdPO2NRh4SX6OgH248fhO4(this, drawable, str));
        }

        public /* synthetic */ void lambda$null$0$ImageLoader$CacheOutTask(Drawable drawable, String str) {
            this.cacheImage.setImageAndClear(drawable, str);
        }

        /* JADX WARNING: Missing exception handler attribute for start block: B:8:0x0012 */
        /* JADX WARNING: Exception block dominator not found, dom blocks: [] */
        public void cancel() {
            /*
            r2 = this;
            r0 = r2.sync;
            monitor-enter(r0);
            r1 = 1;
            r2.isCancelled = r1;	 Catch:{ Exception -> 0x0012 }
            r1 = r2.runningThread;	 Catch:{ Exception -> 0x0012 }
            if (r1 == 0) goto L_0x0012;
        L_0x000a:
            r1 = r2.runningThread;	 Catch:{ Exception -> 0x0012 }
            r1.interrupt();	 Catch:{ Exception -> 0x0012 }
            goto L_0x0012;
        L_0x0010:
            r1 = move-exception;
            goto L_0x0014;
        L_0x0012:
            monitor-exit(r0);	 Catch:{ all -> 0x0010 }
            return;
        L_0x0014:
            monitor-exit(r0);	 Catch:{ all -> 0x0010 }
            throw r1;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ImageLoader$CacheOutTask.cancel():void");
        }
    }

    private class HttpFileTask extends AsyncTask<Void, Void, Boolean> {
        private boolean canRetry = true;
        private int currentAccount;
        private String ext;
        private RandomAccessFile fileOutputStream = null;
        private int fileSize;
        private long lastProgressTime;
        private File tempFile;
        private String url;

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
            Utilities.stageQueue.postRunnable(new -$$Lambda$ImageLoader$HttpFileTask$CbdoQhu0HscXntXREbdZu5bUbuA(this, f));
        }

        public /* synthetic */ void lambda$reportProgress$1$ImageLoader$HttpFileTask(float f) {
            ImageLoader.this.fileProgresses.put(this.url, Float.valueOf(f));
            AndroidUtilities.runOnUIThread(new -$$Lambda$ImageLoader$HttpFileTask$jxNOungyWTOpPFlVvFvzXDtQEx0(this, f));
        }

        public /* synthetic */ void lambda$null$0$ImageLoader$HttpFileTask(float f) {
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.FileLoadProgressChanged, this.url, Float.valueOf(f));
        }

        /* Access modifiers changed, original: protected|varargs */
        /* JADX WARNING: Removed duplicated region for block: B:103:0x0148 A:{Catch:{ Throwable -> 0x0150 }} */
        /* JADX WARNING: Removed duplicated region for block: B:107:0x0156 A:{SYNTHETIC, Splitter:B:107:0x0156} */
        /* JADX WARNING: Removed duplicated region for block: B:43:0x00ad A:{SYNTHETIC, Splitter:B:43:0x00ad} */
        /* JADX WARNING: Removed duplicated region for block: B:27:0x007f  */
        /* JADX WARNING: Removed duplicated region for block: B:24:0x0076  */
        /* JADX WARNING: Removed duplicated region for block: B:43:0x00ad A:{SYNTHETIC, Splitter:B:43:0x00ad} */
        /* JADX WARNING: Removed duplicated region for block: B:103:0x0148 A:{Catch:{ Throwable -> 0x0150 }} */
        /* JADX WARNING: Removed duplicated region for block: B:107:0x0156 A:{SYNTHETIC, Splitter:B:107:0x0156} */
        /* JADX WARNING: Removed duplicated region for block: B:103:0x0148 A:{Catch:{ Throwable -> 0x0150 }} */
        /* JADX WARNING: Removed duplicated region for block: B:107:0x0156 A:{SYNTHETIC, Splitter:B:107:0x0156} */
        /* JADX WARNING: Removed duplicated region for block: B:103:0x0148 A:{Catch:{ Throwable -> 0x0150 }} */
        /* JADX WARNING: Removed duplicated region for block: B:107:0x0156 A:{SYNTHETIC, Splitter:B:107:0x0156} */
        /* JADX WARNING: Removed duplicated region for block: B:103:0x0148 A:{Catch:{ Throwable -> 0x0150 }} */
        /* JADX WARNING: Removed duplicated region for block: B:107:0x0156 A:{SYNTHETIC, Splitter:B:107:0x0156} */
        public java.lang.Boolean doInBackground(java.lang.Void... r10) {
            /*
            r9 = this;
            r10 = "Mozilla/5.0 (iPhone; CPU iPhone OS 10_0 like Mac OS X) AppleWebKit/602.1.38 (KHTML, like Gecko) Version/10.0 Mobile/14A5297c Safari/602.1";
            r0 = "User-Agent";
            r1 = 1;
            r2 = 0;
            r3 = 0;
            r4 = new java.net.URL;	 Catch:{ Throwable -> 0x006f }
            r5 = r9.url;	 Catch:{ Throwable -> 0x006f }
            r4.<init>(r5);	 Catch:{ Throwable -> 0x006f }
            r4 = r4.openConnection();	 Catch:{ Throwable -> 0x006f }
            r4.addRequestProperty(r0, r10);	 Catch:{ Throwable -> 0x006c }
            r5 = 5000; // 0x1388 float:7.006E-42 double:2.4703E-320;
            r4.setConnectTimeout(r5);	 Catch:{ Throwable -> 0x006c }
            r4.setReadTimeout(r5);	 Catch:{ Throwable -> 0x006c }
            r5 = r4 instanceof java.net.HttpURLConnection;	 Catch:{ Throwable -> 0x006c }
            if (r5 == 0) goto L_0x0054;
        L_0x0021:
            r5 = r4;
            r5 = (java.net.HttpURLConnection) r5;	 Catch:{ Throwable -> 0x006c }
            r5.setInstanceFollowRedirects(r1);	 Catch:{ Throwable -> 0x006c }
            r6 = r5.getResponseCode();	 Catch:{ Throwable -> 0x006c }
            r7 = 302; // 0x12e float:4.23E-43 double:1.49E-321;
            if (r6 == r7) goto L_0x0037;
        L_0x002f:
            r7 = 301; // 0x12d float:4.22E-43 double:1.487E-321;
            if (r6 == r7) goto L_0x0037;
        L_0x0033:
            r7 = 303; // 0x12f float:4.25E-43 double:1.497E-321;
            if (r6 != r7) goto L_0x0054;
        L_0x0037:
            r6 = "Location";
            r6 = r5.getHeaderField(r6);	 Catch:{ Throwable -> 0x006c }
            r7 = "Set-Cookie";
            r5 = r5.getHeaderField(r7);	 Catch:{ Throwable -> 0x006c }
            r7 = new java.net.URL;	 Catch:{ Throwable -> 0x006c }
            r7.<init>(r6);	 Catch:{ Throwable -> 0x006c }
            r4 = r7.openConnection();	 Catch:{ Throwable -> 0x006c }
            r6 = "Cookie";
            r4.setRequestProperty(r6, r5);	 Catch:{ Throwable -> 0x006c }
            r4.addRequestProperty(r0, r10);	 Catch:{ Throwable -> 0x006c }
        L_0x0054:
            r4.connect();	 Catch:{ Throwable -> 0x006c }
            r10 = r4.getInputStream();	 Catch:{ Throwable -> 0x006c }
            r0 = new java.io.RandomAccessFile;	 Catch:{ Throwable -> 0x0067 }
            r5 = r9.tempFile;	 Catch:{ Throwable -> 0x0067 }
            r6 = "rws";
            r0.<init>(r5, r6);	 Catch:{ Throwable -> 0x0067 }
            r9.fileOutputStream = r0;	 Catch:{ Throwable -> 0x0067 }
            goto L_0x00a9;
        L_0x0067:
            r0 = move-exception;
            r8 = r0;
            r0 = r10;
            r10 = r8;
            goto L_0x0072;
        L_0x006c:
            r10 = move-exception;
            r0 = r2;
            goto L_0x0072;
        L_0x006f:
            r10 = move-exception;
            r0 = r2;
            r4 = r0;
        L_0x0072:
            r5 = r10 instanceof java.net.SocketTimeoutException;
            if (r5 == 0) goto L_0x007f;
        L_0x0076:
            r5 = org.telegram.messenger.ApplicationLoader.isNetworkOnline();
            if (r5 == 0) goto L_0x00a5;
        L_0x007c:
            r9.canRetry = r3;
            goto L_0x00a5;
        L_0x007f:
            r5 = r10 instanceof java.net.UnknownHostException;
            if (r5 == 0) goto L_0x0086;
        L_0x0083:
            r9.canRetry = r3;
            goto L_0x00a5;
        L_0x0086:
            r5 = r10 instanceof java.net.SocketException;
            if (r5 == 0) goto L_0x009f;
        L_0x008a:
            r5 = r10.getMessage();
            if (r5 == 0) goto L_0x00a5;
        L_0x0090:
            r5 = r10.getMessage();
            r6 = "ECONNRESET";
            r5 = r5.contains(r6);
            if (r5 == 0) goto L_0x00a5;
        L_0x009c:
            r9.canRetry = r3;
            goto L_0x00a5;
        L_0x009f:
            r5 = r10 instanceof java.io.FileNotFoundException;
            if (r5 == 0) goto L_0x00a5;
        L_0x00a3:
            r9.canRetry = r3;
        L_0x00a5:
            org.telegram.messenger.FileLog.e(r10);
            r10 = r0;
        L_0x00a9:
            r0 = r9.canRetry;
            if (r0 == 0) goto L_0x015e;
        L_0x00ad:
            r0 = r4 instanceof java.net.HttpURLConnection;	 Catch:{ Exception -> 0x00c7 }
            if (r0 == 0) goto L_0x00cb;
        L_0x00b1:
            r0 = r4;
            r0 = (java.net.HttpURLConnection) r0;	 Catch:{ Exception -> 0x00c7 }
            r0 = r0.getResponseCode();	 Catch:{ Exception -> 0x00c7 }
            r5 = 200; // 0xc8 float:2.8E-43 double:9.9E-322;
            if (r0 == r5) goto L_0x00cb;
        L_0x00bc:
            r5 = 202; // 0xca float:2.83E-43 double:1.0E-321;
            if (r0 == r5) goto L_0x00cb;
        L_0x00c0:
            r5 = 304; // 0x130 float:4.26E-43 double:1.5E-321;
            if (r0 == r5) goto L_0x00cb;
        L_0x00c4:
            r9.canRetry = r3;	 Catch:{ Exception -> 0x00c7 }
            goto L_0x00cb;
        L_0x00c7:
            r0 = move-exception;
            org.telegram.messenger.FileLog.e(r0);
        L_0x00cb:
            if (r4 == 0) goto L_0x00fa;
        L_0x00cd:
            r0 = r4.getHeaderFields();	 Catch:{ Exception -> 0x00f6 }
            if (r0 == 0) goto L_0x00fa;
        L_0x00d3:
            r4 = "content-Length";
            r0 = r0.get(r4);	 Catch:{ Exception -> 0x00f6 }
            r0 = (java.util.List) r0;	 Catch:{ Exception -> 0x00f6 }
            if (r0 == 0) goto L_0x00fa;
        L_0x00dd:
            r4 = r0.isEmpty();	 Catch:{ Exception -> 0x00f6 }
            if (r4 != 0) goto L_0x00fa;
        L_0x00e3:
            r0 = r0.get(r3);	 Catch:{ Exception -> 0x00f6 }
            r0 = (java.lang.String) r0;	 Catch:{ Exception -> 0x00f6 }
            if (r0 == 0) goto L_0x00fa;
        L_0x00eb:
            r0 = org.telegram.messenger.Utilities.parseInt(r0);	 Catch:{ Exception -> 0x00f6 }
            r0 = r0.intValue();	 Catch:{ Exception -> 0x00f6 }
            r9.fileSize = r0;	 Catch:{ Exception -> 0x00f6 }
            goto L_0x00fa;
        L_0x00f6:
            r0 = move-exception;
            org.telegram.messenger.FileLog.e(r0);
        L_0x00fa:
            if (r10 == 0) goto L_0x0144;
        L_0x00fc:
            r0 = 32768; // 0x8000 float:4.5918E-41 double:1.61895E-319;
            r0 = new byte[r0];	 Catch:{ Throwable -> 0x0140 }
            r4 = 0;
        L_0x0102:
            r5 = r9.isCancelled();	 Catch:{ Throwable -> 0x0140 }
            if (r5 == 0) goto L_0x0109;
        L_0x0108:
            goto L_0x0134;
        L_0x0109:
            r5 = r10.read(r0);	 Catch:{ Exception -> 0x0136 }
            if (r5 <= 0) goto L_0x0122;
        L_0x010f:
            r6 = r9.fileOutputStream;	 Catch:{ Exception -> 0x0136 }
            r6.write(r0, r3, r5);	 Catch:{ Exception -> 0x0136 }
            r4 = r4 + r5;
            r5 = r9.fileSize;	 Catch:{ Exception -> 0x0136 }
            if (r5 <= 0) goto L_0x0102;
        L_0x0119:
            r5 = (float) r4;	 Catch:{ Exception -> 0x0136 }
            r6 = r9.fileSize;	 Catch:{ Exception -> 0x0136 }
            r6 = (float) r6;	 Catch:{ Exception -> 0x0136 }
            r5 = r5 / r6;
            r9.reportProgress(r5);	 Catch:{ Exception -> 0x0136 }
            goto L_0x0102;
        L_0x0122:
            r0 = -1;
            if (r5 != r0) goto L_0x0134;
        L_0x0125:
            r0 = r9.fileSize;	 Catch:{ Exception -> 0x0132, Throwable -> 0x012f }
            if (r0 == 0) goto L_0x013b;
        L_0x0129:
            r0 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
            r9.reportProgress(r0);	 Catch:{ Exception -> 0x0132, Throwable -> 0x012f }
            goto L_0x013b;
        L_0x012f:
            r0 = move-exception;
            r3 = 1;
            goto L_0x0141;
        L_0x0132:
            r0 = move-exception;
            goto L_0x0138;
        L_0x0134:
            r1 = 0;
            goto L_0x013b;
        L_0x0136:
            r0 = move-exception;
            r1 = 0;
        L_0x0138:
            org.telegram.messenger.FileLog.e(r0);	 Catch:{ Throwable -> 0x013d }
        L_0x013b:
            r3 = r1;
            goto L_0x0144;
        L_0x013d:
            r0 = move-exception;
            r3 = r1;
            goto L_0x0141;
        L_0x0140:
            r0 = move-exception;
        L_0x0141:
            org.telegram.messenger.FileLog.e(r0);
        L_0x0144:
            r0 = r9.fileOutputStream;	 Catch:{ Throwable -> 0x0150 }
            if (r0 == 0) goto L_0x0154;
        L_0x0148:
            r0 = r9.fileOutputStream;	 Catch:{ Throwable -> 0x0150 }
            r0.close();	 Catch:{ Throwable -> 0x0150 }
            r9.fileOutputStream = r2;	 Catch:{ Throwable -> 0x0150 }
            goto L_0x0154;
        L_0x0150:
            r0 = move-exception;
            org.telegram.messenger.FileLog.e(r0);
        L_0x0154:
            if (r10 == 0) goto L_0x015e;
        L_0x0156:
            r10.close();	 Catch:{ Throwable -> 0x015a }
            goto L_0x015e;
        L_0x015a:
            r10 = move-exception;
            org.telegram.messenger.FileLog.e(r10);
        L_0x015e:
            r10 = java.lang.Boolean.valueOf(r3);
            return r10;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ImageLoader$HttpFileTask.doInBackground(java.lang.Void[]):java.lang.Boolean");
        }

        /* Access modifiers changed, original: protected */
        public void onPostExecute(Boolean bool) {
            ImageLoader.this.runHttpFileLoadTasks(this, bool.booleanValue() ? 2 : 1);
        }

        /* Access modifiers changed, original: protected */
        public void onCancelled() {
            ImageLoader.this.runHttpFileLoadTasks(this, 2);
        }
    }

    private class HttpImageTask extends AsyncTask<Void, Void, Boolean> {
        private CacheImage cacheImage;
        private boolean canRetry = true;
        private RandomAccessFile fileOutputStream;
        private HttpURLConnection httpConnection;
        private int imageSize;
        private long lastProgressTime;
        private String overrideUrl;

        static /* synthetic */ void lambda$doInBackground$2(TLObject tLObject, TL_error tL_error) {
        }

        public HttpImageTask(CacheImage cacheImage, int i) {
            this.cacheImage = cacheImage;
            this.imageSize = i;
        }

        public HttpImageTask(CacheImage cacheImage, int i, String str) {
            this.cacheImage = cacheImage;
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
            Utilities.stageQueue.postRunnable(new -$$Lambda$ImageLoader$HttpImageTask$WWTxHtUw7-WIiuq5bKLqtQ8BNBI(this, f));
        }

        public /* synthetic */ void lambda$reportProgress$1$ImageLoader$HttpImageTask(float f) {
            ImageLoader.this.fileProgresses.put(this.cacheImage.url, Float.valueOf(f));
            AndroidUtilities.runOnUIThread(new -$$Lambda$ImageLoader$HttpImageTask$5HGicoisjrTisboRYDJXYCptNjk(this, f));
        }

        public /* synthetic */ void lambda$null$0$ImageLoader$HttpImageTask(float f) {
            NotificationCenter.getInstance(this.cacheImage.currentAccount).postNotificationName(NotificationCenter.FileLoadProgressChanged, this.cacheImage.url, Float.valueOf(f));
        }

        /* Access modifiers changed, original: protected|varargs */
        /* JADX WARNING: Removed duplicated region for block: B:48:0x00e9 A:{SYNTHETIC, Splitter:B:48:0x00e9} */
        /* JADX WARNING: Removed duplicated region for block: B:108:0x0185 A:{Catch:{ Throwable -> 0x018d }} */
        /* JADX WARNING: Removed duplicated region for block: B:114:0x0195 A:{Catch:{ Throwable -> 0x019a }} */
        /* JADX WARNING: Removed duplicated region for block: B:117:0x019c A:{SYNTHETIC, Splitter:B:117:0x019c} */
        /* JADX WARNING: Removed duplicated region for block: B:122:0x01a6  */
        /* JADX WARNING: Removed duplicated region for block: B:108:0x0185 A:{Catch:{ Throwable -> 0x018d }} */
        /* JADX WARNING: Removed duplicated region for block: B:114:0x0195 A:{Catch:{ Throwable -> 0x019a }} */
        /* JADX WARNING: Removed duplicated region for block: B:117:0x019c A:{SYNTHETIC, Splitter:B:117:0x019c} */
        /* JADX WARNING: Removed duplicated region for block: B:122:0x01a6  */
        /* JADX WARNING: Removed duplicated region for block: B:48:0x00e9 A:{SYNTHETIC, Splitter:B:48:0x00e9} */
        /* JADX WARNING: Removed duplicated region for block: B:108:0x0185 A:{Catch:{ Throwable -> 0x018d }} */
        /* JADX WARNING: Removed duplicated region for block: B:114:0x0195 A:{Catch:{ Throwable -> 0x019a }} */
        /* JADX WARNING: Removed duplicated region for block: B:117:0x019c A:{SYNTHETIC, Splitter:B:117:0x019c} */
        /* JADX WARNING: Removed duplicated region for block: B:122:0x01a6  */
        /* JADX WARNING: Removed duplicated region for block: B:108:0x0185 A:{Catch:{ Throwable -> 0x018d }} */
        /* JADX WARNING: Removed duplicated region for block: B:114:0x0195 A:{Catch:{ Throwable -> 0x019a }} */
        /* JADX WARNING: Removed duplicated region for block: B:117:0x019c A:{SYNTHETIC, Splitter:B:117:0x019c} */
        /* JADX WARNING: Removed duplicated region for block: B:122:0x01a6  */
        /* JADX WARNING: Removed duplicated region for block: B:108:0x0185 A:{Catch:{ Throwable -> 0x018d }} */
        /* JADX WARNING: Removed duplicated region for block: B:114:0x0195 A:{Catch:{ Throwable -> 0x019a }} */
        /* JADX WARNING: Removed duplicated region for block: B:117:0x019c A:{SYNTHETIC, Splitter:B:117:0x019c} */
        /* JADX WARNING: Removed duplicated region for block: B:122:0x01a6  */
        public java.lang.Boolean doInBackground(java.lang.Void... r9) {
            /*
            r8 = this;
            r9 = r8.isCancelled();
            r0 = 1;
            r1 = 0;
            r2 = 0;
            if (r9 != 0) goto L_0x00e2;
        L_0x0009:
            r9 = r8.cacheImage;	 Catch:{ Throwable -> 0x00a8 }
            r9 = r9.imageLocation;	 Catch:{ Throwable -> 0x00a8 }
            r9 = r9.path;	 Catch:{ Throwable -> 0x00a8 }
            r3 = "https://static-maps";
            r3 = r9.startsWith(r3);	 Catch:{ Throwable -> 0x00a8 }
            if (r3 != 0) goto L_0x001f;
        L_0x0017:
            r3 = "https://maps.googleapis";
            r3 = r9.startsWith(r3);	 Catch:{ Throwable -> 0x00a8 }
            if (r3 == 0) goto L_0x0057;
        L_0x001f:
            r3 = r8.cacheImage;	 Catch:{ Throwable -> 0x00a8 }
            r3 = r3.currentAccount;	 Catch:{ Throwable -> 0x00a8 }
            r3 = org.telegram.messenger.MessagesController.getInstance(r3);	 Catch:{ Throwable -> 0x00a8 }
            r3 = r3.mapProvider;	 Catch:{ Throwable -> 0x00a8 }
            r4 = 3;
            if (r3 == r4) goto L_0x002f;
        L_0x002c:
            r4 = 4;
            if (r3 != r4) goto L_0x0057;
        L_0x002f:
            r3 = org.telegram.messenger.ImageLoader.this;	 Catch:{ Throwable -> 0x00a8 }
            r3 = r3.testWebFile;	 Catch:{ Throwable -> 0x00a8 }
            r3 = r3.get(r9);	 Catch:{ Throwable -> 0x00a8 }
            r3 = (org.telegram.messenger.WebFile) r3;	 Catch:{ Throwable -> 0x00a8 }
            if (r3 == 0) goto L_0x0057;
        L_0x003d:
            r4 = new org.telegram.tgnet.TLRPC$TL_upload_getWebFile;	 Catch:{ Throwable -> 0x00a8 }
            r4.<init>();	 Catch:{ Throwable -> 0x00a8 }
            r3 = r3.location;	 Catch:{ Throwable -> 0x00a8 }
            r4.location = r3;	 Catch:{ Throwable -> 0x00a8 }
            r4.offset = r2;	 Catch:{ Throwable -> 0x00a8 }
            r4.limit = r2;	 Catch:{ Throwable -> 0x00a8 }
            r3 = r8.cacheImage;	 Catch:{ Throwable -> 0x00a8 }
            r3 = r3.currentAccount;	 Catch:{ Throwable -> 0x00a8 }
            r3 = org.telegram.tgnet.ConnectionsManager.getInstance(r3);	 Catch:{ Throwable -> 0x00a8 }
            r5 = org.telegram.messenger.-$$Lambda$ImageLoader$HttpImageTask$T115Ddi3sI3XyS3851ENmLig_I8.INSTANCE;	 Catch:{ Throwable -> 0x00a8 }
            r3.sendRequest(r4, r5);	 Catch:{ Throwable -> 0x00a8 }
        L_0x0057:
            r3 = new java.net.URL;	 Catch:{ Throwable -> 0x00a8 }
            r4 = r8.overrideUrl;	 Catch:{ Throwable -> 0x00a8 }
            if (r4 == 0) goto L_0x005f;
        L_0x005d:
            r9 = r8.overrideUrl;	 Catch:{ Throwable -> 0x00a8 }
        L_0x005f:
            r3.<init>(r9);	 Catch:{ Throwable -> 0x00a8 }
            r9 = r3.openConnection();	 Catch:{ Throwable -> 0x00a8 }
            r9 = (java.net.HttpURLConnection) r9;	 Catch:{ Throwable -> 0x00a8 }
            r8.httpConnection = r9;	 Catch:{ Throwable -> 0x00a8 }
            r9 = r8.httpConnection;	 Catch:{ Throwable -> 0x00a8 }
            r3 = "User-Agent";
            r4 = "Mozilla/5.0 (iPhone; CPU iPhone OS 10_0 like Mac OS X) AppleWebKit/602.1.38 (KHTML, like Gecko) Version/10.0 Mobile/14A5297c Safari/602.1";
            r9.addRequestProperty(r3, r4);	 Catch:{ Throwable -> 0x00a8 }
            r9 = r8.httpConnection;	 Catch:{ Throwable -> 0x00a8 }
            r3 = 5000; // 0x1388 float:7.006E-42 double:2.4703E-320;
            r9.setConnectTimeout(r3);	 Catch:{ Throwable -> 0x00a8 }
            r9 = r8.httpConnection;	 Catch:{ Throwable -> 0x00a8 }
            r9.setReadTimeout(r3);	 Catch:{ Throwable -> 0x00a8 }
            r9 = r8.httpConnection;	 Catch:{ Throwable -> 0x00a8 }
            r9.setInstanceFollowRedirects(r0);	 Catch:{ Throwable -> 0x00a8 }
            r9 = r8.isCancelled();	 Catch:{ Throwable -> 0x00a8 }
            if (r9 != 0) goto L_0x00e2;
        L_0x008a:
            r9 = r8.httpConnection;	 Catch:{ Throwable -> 0x00a8 }
            r9.connect();	 Catch:{ Throwable -> 0x00a8 }
            r9 = r8.httpConnection;	 Catch:{ Throwable -> 0x00a8 }
            r9 = r9.getInputStream();	 Catch:{ Throwable -> 0x00a8 }
            r3 = new java.io.RandomAccessFile;	 Catch:{ Throwable -> 0x00a3 }
            r4 = r8.cacheImage;	 Catch:{ Throwable -> 0x00a3 }
            r4 = r4.tempFilePath;	 Catch:{ Throwable -> 0x00a3 }
            r5 = "rws";
            r3.<init>(r4, r5);	 Catch:{ Throwable -> 0x00a3 }
            r8.fileOutputStream = r3;	 Catch:{ Throwable -> 0x00a3 }
            goto L_0x00e3;
        L_0x00a3:
            r3 = move-exception;
            r7 = r3;
            r3 = r9;
            r9 = r7;
            goto L_0x00aa;
        L_0x00a8:
            r9 = move-exception;
            r3 = r1;
        L_0x00aa:
            r4 = r9 instanceof java.net.SocketTimeoutException;
            if (r4 == 0) goto L_0x00b7;
        L_0x00ae:
            r4 = org.telegram.messenger.ApplicationLoader.isNetworkOnline();
            if (r4 == 0) goto L_0x00dd;
        L_0x00b4:
            r8.canRetry = r2;
            goto L_0x00dd;
        L_0x00b7:
            r4 = r9 instanceof java.net.UnknownHostException;
            if (r4 == 0) goto L_0x00be;
        L_0x00bb:
            r8.canRetry = r2;
            goto L_0x00dd;
        L_0x00be:
            r4 = r9 instanceof java.net.SocketException;
            if (r4 == 0) goto L_0x00d7;
        L_0x00c2:
            r4 = r9.getMessage();
            if (r4 == 0) goto L_0x00dd;
        L_0x00c8:
            r4 = r9.getMessage();
            r5 = "ECONNRESET";
            r4 = r4.contains(r5);
            if (r4 == 0) goto L_0x00dd;
        L_0x00d4:
            r8.canRetry = r2;
            goto L_0x00dd;
        L_0x00d7:
            r4 = r9 instanceof java.io.FileNotFoundException;
            if (r4 == 0) goto L_0x00dd;
        L_0x00db:
            r8.canRetry = r2;
        L_0x00dd:
            org.telegram.messenger.FileLog.e(r9);
            r9 = r3;
            goto L_0x00e3;
        L_0x00e2:
            r9 = r1;
        L_0x00e3:
            r3 = r8.isCancelled();
            if (r3 != 0) goto L_0x0181;
        L_0x00e9:
            r3 = r8.httpConnection;	 Catch:{ Exception -> 0x0102 }
            if (r3 == 0) goto L_0x0106;
        L_0x00ed:
            r3 = r8.httpConnection;	 Catch:{ Exception -> 0x0102 }
            r3 = r3.getResponseCode();	 Catch:{ Exception -> 0x0102 }
            r4 = 200; // 0xc8 float:2.8E-43 double:9.9E-322;
            if (r3 == r4) goto L_0x0106;
        L_0x00f7:
            r4 = 202; // 0xca float:2.83E-43 double:1.0E-321;
            if (r3 == r4) goto L_0x0106;
        L_0x00fb:
            r4 = 304; // 0x130 float:4.26E-43 double:1.5E-321;
            if (r3 == r4) goto L_0x0106;
        L_0x00ff:
            r8.canRetry = r2;	 Catch:{ Exception -> 0x0102 }
            goto L_0x0106;
        L_0x0102:
            r3 = move-exception;
            org.telegram.messenger.FileLog.e(r3);
        L_0x0106:
            r3 = r8.imageSize;
            if (r3 != 0) goto L_0x013b;
        L_0x010a:
            r3 = r8.httpConnection;
            if (r3 == 0) goto L_0x013b;
        L_0x010e:
            r3 = r3.getHeaderFields();	 Catch:{ Exception -> 0x0137 }
            if (r3 == 0) goto L_0x013b;
        L_0x0114:
            r4 = "content-Length";
            r3 = r3.get(r4);	 Catch:{ Exception -> 0x0137 }
            r3 = (java.util.List) r3;	 Catch:{ Exception -> 0x0137 }
            if (r3 == 0) goto L_0x013b;
        L_0x011e:
            r4 = r3.isEmpty();	 Catch:{ Exception -> 0x0137 }
            if (r4 != 0) goto L_0x013b;
        L_0x0124:
            r3 = r3.get(r2);	 Catch:{ Exception -> 0x0137 }
            r3 = (java.lang.String) r3;	 Catch:{ Exception -> 0x0137 }
            if (r3 == 0) goto L_0x013b;
        L_0x012c:
            r3 = org.telegram.messenger.Utilities.parseInt(r3);	 Catch:{ Exception -> 0x0137 }
            r3 = r3.intValue();	 Catch:{ Exception -> 0x0137 }
            r8.imageSize = r3;	 Catch:{ Exception -> 0x0137 }
            goto L_0x013b;
        L_0x0137:
            r3 = move-exception;
            org.telegram.messenger.FileLog.e(r3);
        L_0x013b:
            if (r9 == 0) goto L_0x0181;
        L_0x013d:
            r3 = 8192; // 0x2000 float:1.14794E-41 double:4.0474E-320;
            r3 = new byte[r3];	 Catch:{ Throwable -> 0x017d }
            r4 = 0;
        L_0x0142:
            r5 = r8.isCancelled();	 Catch:{ Throwable -> 0x017d }
            if (r5 == 0) goto L_0x0149;
        L_0x0148:
            goto L_0x0181;
        L_0x0149:
            r5 = r9.read(r3);	 Catch:{ Exception -> 0x0178 }
            if (r5 <= 0) goto L_0x0162;
        L_0x014f:
            r4 = r4 + r5;
            r6 = r8.fileOutputStream;	 Catch:{ Exception -> 0x0178 }
            r6.write(r3, r2, r5);	 Catch:{ Exception -> 0x0178 }
            r5 = r8.imageSize;	 Catch:{ Exception -> 0x0178 }
            if (r5 == 0) goto L_0x0142;
        L_0x0159:
            r5 = (float) r4;	 Catch:{ Exception -> 0x0178 }
            r6 = r8.imageSize;	 Catch:{ Exception -> 0x0178 }
            r6 = (float) r6;	 Catch:{ Exception -> 0x0178 }
            r5 = r5 / r6;
            r8.reportProgress(r5);	 Catch:{ Exception -> 0x0178 }
            goto L_0x0142;
        L_0x0162:
            r3 = -1;
            if (r5 != r3) goto L_0x0181;
        L_0x0165:
            r2 = r8.imageSize;	 Catch:{ Exception -> 0x0174, Throwable -> 0x0170 }
            if (r2 == 0) goto L_0x016e;
        L_0x0169:
            r2 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
            r8.reportProgress(r2);	 Catch:{ Exception -> 0x0174, Throwable -> 0x0170 }
        L_0x016e:
            r2 = 1;
            goto L_0x0181;
        L_0x0170:
            r2 = move-exception;
            r0 = r2;
            r2 = 1;
            goto L_0x017e;
        L_0x0174:
            r2 = move-exception;
            r0 = r2;
            r2 = 1;
            goto L_0x0179;
        L_0x0178:
            r0 = move-exception;
        L_0x0179:
            org.telegram.messenger.FileLog.e(r0);	 Catch:{ Throwable -> 0x017d }
            goto L_0x0181;
        L_0x017d:
            r0 = move-exception;
        L_0x017e:
            org.telegram.messenger.FileLog.e(r0);
        L_0x0181:
            r0 = r8.fileOutputStream;	 Catch:{ Throwable -> 0x018d }
            if (r0 == 0) goto L_0x0191;
        L_0x0185:
            r0 = r8.fileOutputStream;	 Catch:{ Throwable -> 0x018d }
            r0.close();	 Catch:{ Throwable -> 0x018d }
            r8.fileOutputStream = r1;	 Catch:{ Throwable -> 0x018d }
            goto L_0x0191;
        L_0x018d:
            r0 = move-exception;
            org.telegram.messenger.FileLog.e(r0);
        L_0x0191:
            r0 = r8.httpConnection;	 Catch:{ Throwable -> 0x019a }
            if (r0 == 0) goto L_0x019a;
        L_0x0195:
            r0 = r8.httpConnection;	 Catch:{ Throwable -> 0x019a }
            r0.disconnect();	 Catch:{ Throwable -> 0x019a }
        L_0x019a:
            if (r9 == 0) goto L_0x01a4;
        L_0x019c:
            r9.close();	 Catch:{ Throwable -> 0x01a0 }
            goto L_0x01a4;
        L_0x01a0:
            r9 = move-exception;
            org.telegram.messenger.FileLog.e(r9);
        L_0x01a4:
            if (r2 == 0) goto L_0x01ba;
        L_0x01a6:
            r9 = r8.cacheImage;
            r0 = r9.tempFilePath;
            if (r0 == 0) goto L_0x01ba;
        L_0x01ac:
            r9 = r9.finalFilePath;
            r9 = r0.renameTo(r9);
            if (r9 != 0) goto L_0x01ba;
        L_0x01b4:
            r9 = r8.cacheImage;
            r0 = r9.tempFilePath;
            r9.finalFilePath = r0;
        L_0x01ba:
            r9 = java.lang.Boolean.valueOf(r2);
            return r9;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ImageLoader$HttpImageTask.doInBackground(java.lang.Void[]):java.lang.Boolean");
        }

        /* Access modifiers changed, original: protected */
        public void onPostExecute(Boolean bool) {
            if (bool.booleanValue() || !this.canRetry) {
                ImageLoader imageLoader = ImageLoader.this;
                CacheImage cacheImage = this.cacheImage;
                imageLoader.fileDidLoaded(cacheImage.url, cacheImage.finalFilePath, 0);
            } else {
                ImageLoader.this.httpFileLoadError(this.cacheImage.url);
            }
            Utilities.stageQueue.postRunnable(new -$$Lambda$ImageLoader$HttpImageTask$SfPPeQgJq15qYgHfn-IXbR-DnQ0(this, bool));
            ImageLoader.this.imageLoadQueue.postRunnable(new -$$Lambda$ImageLoader$HttpImageTask$Z2AJLo51Bz13ruMq5jl4ihFrKyc(this));
        }

        public /* synthetic */ void lambda$onPostExecute$4$ImageLoader$HttpImageTask(Boolean bool) {
            ImageLoader.this.fileProgresses.remove(this.cacheImage.url);
            AndroidUtilities.runOnUIThread(new -$$Lambda$ImageLoader$HttpImageTask$pG_0hY9R-vFFkVPjhMdZBfbtL24(this, bool));
        }

        public /* synthetic */ void lambda$null$3$ImageLoader$HttpImageTask(Boolean bool) {
            if (bool.booleanValue()) {
                NotificationCenter.getInstance(this.cacheImage.currentAccount).postNotificationName(NotificationCenter.fileDidLoad, this.cacheImage.url);
                return;
            }
            NotificationCenter.getInstance(this.cacheImage.currentAccount).postNotificationName(NotificationCenter.fileDidFailedLoad, this.cacheImage.url, Integer.valueOf(2));
        }

        public /* synthetic */ void lambda$onPostExecute$5$ImageLoader$HttpImageTask() {
            ImageLoader.this.runHttpTasks(true);
        }

        public /* synthetic */ void lambda$onCancelled$6$ImageLoader$HttpImageTask() {
            ImageLoader.this.runHttpTasks(true);
        }

        /* Access modifiers changed, original: protected */
        public void onCancelled() {
            ImageLoader.this.imageLoadQueue.postRunnable(new -$$Lambda$ImageLoader$HttpImageTask$hp6Qb_emVVm8eUAVBNZgyYyMaoI(this));
            Utilities.stageQueue.postRunnable(new -$$Lambda$ImageLoader$HttpImageTask$a-vnJl4U3DAZgDJvr8vdGGH5i2s(this));
        }

        public /* synthetic */ void lambda$onCancelled$8$ImageLoader$HttpImageTask() {
            ImageLoader.this.fileProgresses.remove(this.cacheImage.url);
            AndroidUtilities.runOnUIThread(new -$$Lambda$ImageLoader$HttpImageTask$gXa55exgYzWejvB_mF-uS-Y2fOo(this));
        }

        public /* synthetic */ void lambda$null$7$ImageLoader$HttpImageTask() {
            NotificationCenter.getInstance(this.cacheImage.currentAccount).postNotificationName(NotificationCenter.fileDidFailedLoad, this.cacheImage.url, Integer.valueOf(1));
        }
    }

    private class ThumbGenerateInfo {
        private boolean big;
        private String filter;
        private ArrayList<ImageReceiver> imageReceiverArray;
        private ArrayList<Integer> imageReceiverGuidsArray;
        private Document parentDocument;

        private ThumbGenerateInfo() {
            this.imageReceiverArray = new ArrayList();
            this.imageReceiverGuidsArray = new ArrayList();
        }

        /* synthetic */ ThumbGenerateInfo(ImageLoader imageLoader, AnonymousClass1 anonymousClass1) {
            this();
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
                ImageLoader.this.imageLoadQueue.postRunnable(new -$$Lambda$ImageLoader$ThumbGenerateTask$wNtiv0w_w5JLAy5lgehk-MfB6UY(this, FileLoader.getAttachFileName(thumbGenerateInfo.parentDocument)));
            }
        }

        public /* synthetic */ void lambda$removeTask$0$ImageLoader$ThumbGenerateTask(String str) {
            ThumbGenerateTask thumbGenerateTask = (ThumbGenerateTask) ImageLoader.this.thumbGenerateTasks.remove(str);
        }

        /* JADX WARNING: Removed duplicated region for block: B:59:0x0145 A:{Catch:{ Exception -> 0x014e, Throwable -> 0x0180 }} */
        /* JADX WARNING: Removed duplicated region for block: B:58:0x0142 A:{Catch:{ Exception -> 0x014e, Throwable -> 0x0180 }} */
        public void run() {
            /*
            r10 = this;
            r0 = ".jpg";
            r1 = r10.info;	 Catch:{ Throwable -> 0x0180 }
            if (r1 != 0) goto L_0x000a;
        L_0x0006:
            r10.removeTask();	 Catch:{ Throwable -> 0x0180 }
            return;
        L_0x000a:
            r1 = new java.lang.StringBuilder;	 Catch:{ Throwable -> 0x0180 }
            r1.<init>();	 Catch:{ Throwable -> 0x0180 }
            r2 = "q_";
            r1.append(r2);	 Catch:{ Throwable -> 0x0180 }
            r2 = r10.info;	 Catch:{ Throwable -> 0x0180 }
            r2 = r2.parentDocument;	 Catch:{ Throwable -> 0x0180 }
            r2 = r2.dc_id;	 Catch:{ Throwable -> 0x0180 }
            r1.append(r2);	 Catch:{ Throwable -> 0x0180 }
            r2 = "_";
            r1.append(r2);	 Catch:{ Throwable -> 0x0180 }
            r2 = r10.info;	 Catch:{ Throwable -> 0x0180 }
            r2 = r2.parentDocument;	 Catch:{ Throwable -> 0x0180 }
            r2 = r2.id;	 Catch:{ Throwable -> 0x0180 }
            r1.append(r2);	 Catch:{ Throwable -> 0x0180 }
            r6 = r1.toString();	 Catch:{ Throwable -> 0x0180 }
            r1 = new java.io.File;	 Catch:{ Throwable -> 0x0180 }
            r2 = 4;
            r3 = org.telegram.messenger.FileLoader.getDirectory(r2);	 Catch:{ Throwable -> 0x0180 }
            r4 = new java.lang.StringBuilder;	 Catch:{ Throwable -> 0x0180 }
            r4.<init>();	 Catch:{ Throwable -> 0x0180 }
            r4.append(r6);	 Catch:{ Throwable -> 0x0180 }
            r4.append(r0);	 Catch:{ Throwable -> 0x0180 }
            r4 = r4.toString();	 Catch:{ Throwable -> 0x0180 }
            r1.<init>(r3, r4);	 Catch:{ Throwable -> 0x0180 }
            r3 = r1.exists();	 Catch:{ Throwable -> 0x0180 }
            if (r3 != 0) goto L_0x017c;
        L_0x0052:
            r3 = r10.originalPath;	 Catch:{ Throwable -> 0x0180 }
            r3 = r3.exists();	 Catch:{ Throwable -> 0x0180 }
            if (r3 != 0) goto L_0x005c;
        L_0x005a:
            goto L_0x017c;
        L_0x005c:
            r3 = r10.info;	 Catch:{ Throwable -> 0x0180 }
            r3 = r3.big;	 Catch:{ Throwable -> 0x0180 }
            if (r3 == 0) goto L_0x0071;
        L_0x0064:
            r2 = org.telegram.messenger.AndroidUtilities.displaySize;	 Catch:{ Throwable -> 0x0180 }
            r2 = r2.x;	 Catch:{ Throwable -> 0x0180 }
            r3 = org.telegram.messenger.AndroidUtilities.displaySize;	 Catch:{ Throwable -> 0x0180 }
            r3 = r3.y;	 Catch:{ Throwable -> 0x0180 }
            r2 = java.lang.Math.max(r2, r3);	 Catch:{ Throwable -> 0x0180 }
            goto L_0x0084;
        L_0x0071:
            r3 = 180; // 0xb4 float:2.52E-43 double:8.9E-322;
            r4 = org.telegram.messenger.AndroidUtilities.displaySize;	 Catch:{ Throwable -> 0x0180 }
            r4 = r4.x;	 Catch:{ Throwable -> 0x0180 }
            r5 = org.telegram.messenger.AndroidUtilities.displaySize;	 Catch:{ Throwable -> 0x0180 }
            r5 = r5.y;	 Catch:{ Throwable -> 0x0180 }
            r4 = java.lang.Math.min(r4, r5);	 Catch:{ Throwable -> 0x0180 }
            r4 = r4 / r2;
            r2 = java.lang.Math.min(r3, r4);	 Catch:{ Throwable -> 0x0180 }
        L_0x0084:
            r3 = r10.mediaType;	 Catch:{ Throwable -> 0x0180 }
            r4 = 0;
            r5 = 1;
            r7 = 0;
            if (r3 != 0) goto L_0x0097;
        L_0x008b:
            r0 = r10.originalPath;	 Catch:{ Throwable -> 0x0180 }
            r0 = r0.toString();	 Catch:{ Throwable -> 0x0180 }
            r3 = (float) r2;	 Catch:{ Throwable -> 0x0180 }
            r7 = org.telegram.messenger.ImageLoader.loadBitmap(r0, r7, r3, r3, r4);	 Catch:{ Throwable -> 0x0180 }
            goto L_0x0100;
        L_0x0097:
            r3 = r10.mediaType;	 Catch:{ Throwable -> 0x0180 }
            r8 = 2;
            if (r3 != r8) goto L_0x00b1;
        L_0x009c:
            r0 = r10.originalPath;	 Catch:{ Throwable -> 0x0180 }
            r0 = r0.toString();	 Catch:{ Throwable -> 0x0180 }
            r3 = r10.info;	 Catch:{ Throwable -> 0x0180 }
            r3 = r3.big;	 Catch:{ Throwable -> 0x0180 }
            if (r3 == 0) goto L_0x00ab;
        L_0x00aa:
            goto L_0x00ac;
        L_0x00ab:
            r8 = 1;
        L_0x00ac:
            r7 = android.media.ThumbnailUtils.createVideoThumbnail(r0, r8);	 Catch:{ Throwable -> 0x0180 }
            goto L_0x0100;
        L_0x00b1:
            r3 = r10.mediaType;	 Catch:{ Throwable -> 0x0180 }
            r9 = 3;
            if (r3 != r9) goto L_0x0100;
        L_0x00b6:
            r3 = r10.originalPath;	 Catch:{ Throwable -> 0x0180 }
            r3 = r3.toString();	 Catch:{ Throwable -> 0x0180 }
            r3 = r3.toLowerCase();	 Catch:{ Throwable -> 0x0180 }
            r9 = "mp4";
            r9 = r3.endsWith(r9);	 Catch:{ Throwable -> 0x0180 }
            if (r9 == 0) goto L_0x00dd;
        L_0x00c8:
            r0 = r10.originalPath;	 Catch:{ Throwable -> 0x0180 }
            r0 = r0.toString();	 Catch:{ Throwable -> 0x0180 }
            r3 = r10.info;	 Catch:{ Throwable -> 0x0180 }
            r3 = r3.big;	 Catch:{ Throwable -> 0x0180 }
            if (r3 == 0) goto L_0x00d7;
        L_0x00d6:
            goto L_0x00d8;
        L_0x00d7:
            r8 = 1;
        L_0x00d8:
            r7 = android.media.ThumbnailUtils.createVideoThumbnail(r0, r8);	 Catch:{ Throwable -> 0x0180 }
            goto L_0x0100;
        L_0x00dd:
            r0 = r3.endsWith(r0);	 Catch:{ Throwable -> 0x0180 }
            if (r0 != 0) goto L_0x00fb;
        L_0x00e3:
            r0 = ".jpeg";
            r0 = r3.endsWith(r0);	 Catch:{ Throwable -> 0x0180 }
            if (r0 != 0) goto L_0x00fb;
        L_0x00eb:
            r0 = ".png";
            r0 = r3.endsWith(r0);	 Catch:{ Throwable -> 0x0180 }
            if (r0 != 0) goto L_0x00fb;
        L_0x00f3:
            r0 = ".gif";
            r0 = r3.endsWith(r0);	 Catch:{ Throwable -> 0x0180 }
            if (r0 == 0) goto L_0x0100;
        L_0x00fb:
            r0 = (float) r2;	 Catch:{ Throwable -> 0x0180 }
            r7 = org.telegram.messenger.ImageLoader.loadBitmap(r3, r7, r0, r0, r4);	 Catch:{ Throwable -> 0x0180 }
        L_0x0100:
            if (r7 != 0) goto L_0x0106;
        L_0x0102:
            r10.removeTask();	 Catch:{ Throwable -> 0x0180 }
            return;
        L_0x0106:
            r0 = r7.getWidth();	 Catch:{ Throwable -> 0x0180 }
            r3 = r7.getHeight();	 Catch:{ Throwable -> 0x0180 }
            if (r0 == 0) goto L_0x0178;
        L_0x0110:
            if (r3 != 0) goto L_0x0113;
        L_0x0112:
            goto L_0x0178;
        L_0x0113:
            r0 = (float) r0;	 Catch:{ Throwable -> 0x0180 }
            r2 = (float) r2;	 Catch:{ Throwable -> 0x0180 }
            r4 = r0 / r2;
            r3 = (float) r3;	 Catch:{ Throwable -> 0x0180 }
            r2 = r3 / r2;
            r2 = java.lang.Math.min(r4, r2);	 Catch:{ Throwable -> 0x0180 }
            r4 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
            r4 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1));
            if (r4 <= 0) goto L_0x0132;
        L_0x0124:
            r0 = r0 / r2;
            r0 = (int) r0;	 Catch:{ Throwable -> 0x0180 }
            r3 = r3 / r2;
            r2 = (int) r3;	 Catch:{ Throwable -> 0x0180 }
            r0 = org.telegram.messenger.Bitmaps.createScaledBitmap(r7, r0, r2, r5);	 Catch:{ Throwable -> 0x0180 }
            if (r0 == r7) goto L_0x0132;
        L_0x012e:
            r7.recycle();	 Catch:{ Throwable -> 0x0180 }
            goto L_0x0133;
        L_0x0132:
            r0 = r7;
        L_0x0133:
            r2 = new java.io.FileOutputStream;	 Catch:{ Throwable -> 0x0180 }
            r2.<init>(r1);	 Catch:{ Throwable -> 0x0180 }
            r1 = android.graphics.Bitmap.CompressFormat.JPEG;	 Catch:{ Throwable -> 0x0180 }
            r3 = r10.info;	 Catch:{ Throwable -> 0x0180 }
            r3 = r3.big;	 Catch:{ Throwable -> 0x0180 }
            if (r3 == 0) goto L_0x0145;
        L_0x0142:
            r3 = 83;
            goto L_0x0147;
        L_0x0145:
            r3 = 60;
        L_0x0147:
            r0.compress(r1, r3, r2);	 Catch:{ Throwable -> 0x0180 }
            r2.close();	 Catch:{ Exception -> 0x014e }
            goto L_0x0152;
        L_0x014e:
            r1 = move-exception;
            org.telegram.messenger.FileLog.e(r1);	 Catch:{ Throwable -> 0x0180 }
        L_0x0152:
            r8 = new android.graphics.drawable.BitmapDrawable;	 Catch:{ Throwable -> 0x0180 }
            r8.<init>(r0);	 Catch:{ Throwable -> 0x0180 }
            r7 = new java.util.ArrayList;	 Catch:{ Throwable -> 0x0180 }
            r0 = r10.info;	 Catch:{ Throwable -> 0x0180 }
            r0 = r0.imageReceiverArray;	 Catch:{ Throwable -> 0x0180 }
            r7.<init>(r0);	 Catch:{ Throwable -> 0x0180 }
            r9 = new java.util.ArrayList;	 Catch:{ Throwable -> 0x0180 }
            r0 = r10.info;	 Catch:{ Throwable -> 0x0180 }
            r0 = r0.imageReceiverGuidsArray;	 Catch:{ Throwable -> 0x0180 }
            r9.<init>(r0);	 Catch:{ Throwable -> 0x0180 }
            r0 = new org.telegram.messenger.-$$Lambda$ImageLoader$ThumbGenerateTask$Q_bLt0Pje4_VwZd4dMzoXU74lq4;	 Catch:{ Throwable -> 0x0180 }
            r4 = r0;
            r5 = r10;
            r4.<init>(r5, r6, r7, r8, r9);	 Catch:{ Throwable -> 0x0180 }
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r0);	 Catch:{ Throwable -> 0x0180 }
            goto L_0x0187;
        L_0x0178:
            r10.removeTask();	 Catch:{ Throwable -> 0x0180 }
            return;
        L_0x017c:
            r10.removeTask();	 Catch:{ Throwable -> 0x0180 }
            return;
        L_0x0180:
            r0 = move-exception;
            org.telegram.messenger.FileLog.e(r0);
            r10.removeTask();
        L_0x0187:
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ImageLoader$ThumbGenerateTask.run():void");
        }

        public /* synthetic */ void lambda$run$1$ImageLoader$ThumbGenerateTask(String str, ArrayList arrayList, BitmapDrawable bitmapDrawable, ArrayList arrayList2) {
            removeTask();
            if (this.info.filter != null) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(str);
                stringBuilder.append("@");
                stringBuilder.append(this.info.filter);
                str = stringBuilder.toString();
            }
            for (int i = 0; i < arrayList.size(); i++) {
                ((ImageReceiver) arrayList.get(i)).setImageBitmapByKey(bitmapDrawable, str, 0, false, ((Integer) arrayList2.get(i)).intValue());
            }
            ImageLoader.this.memCache.put(str, bitmapDrawable);
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
        int i = 0;
        this.currentHttpTasksCount = 0;
        this.currentArtworkTasksCount = 0;
        this.testWebFile = new ConcurrentHashMap();
        this.httpFileLoadTasks = new LinkedList();
        this.httpFileLoadTasksByKeys = new HashMap();
        this.retryHttpsTasks = new HashMap();
        this.currentHttpFileLoadTasksCount = 0;
        this.ignoreRemoval = null;
        this.lastCacheOutTime = 0;
        this.lastImageNum = 0;
        this.lastProgressUpdateTime = 0;
        this.telegramPath = null;
        boolean z = true;
        this.thumbGeneratingQueue.setPriority(1);
        int memoryClass = ((ActivityManager) ApplicationLoader.applicationContext.getSystemService("activity")).getMemoryClass();
        if (memoryClass < 192) {
            z = false;
        }
        this.canForce8888 = z;
        this.memCache = new LruCache<BitmapDrawable>((Math.min(z ? 30 : 15, memoryClass / 7) * 1024) * 1024) {
            /* Access modifiers changed, original: protected */
            public int sizeOf(String str, BitmapDrawable bitmapDrawable) {
                return bitmapDrawable.getBitmap().getByteCount();
            }

            /* Access modifiers changed, original: protected */
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
            /* Access modifiers changed, original: protected */
            public int sizeOf(String str, RLottieDrawable rLottieDrawable) {
                return ((rLottieDrawable.getIntrinsicWidth() * rLottieDrawable.getIntrinsicHeight()) * 4) * 2;
            }

            /* Access modifiers changed, original: protected */
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
                FileLog.e(e);
            }
        }
        try {
            new File(cacheDir, ".nomedia").createNewFile();
        } catch (Exception e2) {
            FileLog.e(e2);
        }
        sparseArray.put(4, cacheDir);
        while (i < 3) {
            FileLoader.getInstance(i).setDelegate(new FileLoaderDelegate() {
                public void fileUploadProgressChanged(String str, float f, boolean z) {
                    ImageLoader.this.fileProgresses.put(str, Float.valueOf(f));
                    long currentTimeMillis = System.currentTimeMillis();
                    if (ImageLoader.this.lastProgressUpdateTime == 0 || ImageLoader.this.lastProgressUpdateTime < currentTimeMillis - 500) {
                        ImageLoader.this.lastProgressUpdateTime = currentTimeMillis;
                        AndroidUtilities.runOnUIThread(new -$$Lambda$ImageLoader$3$FqjDIba7eslh2iQtqmCAn4WCKjU(i, str, f, z));
                    }
                }

                public void fileDidUploaded(String str, InputFile inputFile, InputEncryptedFile inputEncryptedFile, byte[] bArr, byte[] bArr2, long j) {
                    Utilities.stageQueue.postRunnable(new -$$Lambda$ImageLoader$3$_9uOaZVxDmXjnClpJdro4iTil8Q(this, i, str, inputFile, inputEncryptedFile, bArr, bArr2, j));
                }

                public /* synthetic */ void lambda$fileDidUploaded$2$ImageLoader$3(int i, String str, InputFile inputFile, InputEncryptedFile inputEncryptedFile, byte[] bArr, byte[] bArr2, long j) {
                    AndroidUtilities.runOnUIThread(new -$$Lambda$ImageLoader$3$O9MuPZ9kD1RfxaVvbxm2MMOXv_E(i, str, inputFile, inputEncryptedFile, bArr, bArr2, j));
                    ImageLoader.this.fileProgresses.remove(str);
                }

                public void fileDidFailedUpload(String str, boolean z) {
                    Utilities.stageQueue.postRunnable(new -$$Lambda$ImageLoader$3$3M_VSd8r5buZPqJNpilxe2zYwos(this, i, str, z));
                }

                public /* synthetic */ void lambda$fileDidFailedUpload$4$ImageLoader$3(int i, String str, boolean z) {
                    AndroidUtilities.runOnUIThread(new -$$Lambda$ImageLoader$3$lo1j3M5K6zygAvdIFH_K82igfdE(i, str, z));
                    ImageLoader.this.fileProgresses.remove(str);
                }

                public void fileDidLoaded(String str, File file, int i) {
                    ImageLoader.this.fileProgresses.remove(str);
                    AndroidUtilities.runOnUIThread(new -$$Lambda$ImageLoader$3$5Bs2fzd8mEyFObZ9x2J38wn4r-c(this, file, str, i, i));
                }

                public /* synthetic */ void lambda$fileDidLoaded$5$ImageLoader$3(File file, String str, int i, int i2) {
                    if (SharedConfig.saveToGallery && ImageLoader.this.telegramPath != null && file != null && ((str.endsWith(".mp4") || str.endsWith(".jpg")) && file.toString().startsWith(ImageLoader.this.telegramPath.toString()))) {
                        AndroidUtilities.addMediaToGallery(file.toString());
                    }
                    NotificationCenter.getInstance(i).postNotificationName(NotificationCenter.fileDidLoad, str);
                    ImageLoader.this.fileDidLoaded(str, file, i2);
                }

                public void fileDidFailedLoad(String str, int i) {
                    ImageLoader.this.fileProgresses.remove(str);
                    AndroidUtilities.runOnUIThread(new -$$Lambda$ImageLoader$3$9ENgl3dEFjSW9fmN2KOTidfJzZ0(this, str, i, i));
                }

                public /* synthetic */ void lambda$fileDidFailedLoad$6$ImageLoader$3(String str, int i, int i2) {
                    ImageLoader.this.fileDidFailedLoad(str, i);
                    NotificationCenter.getInstance(i2).postNotificationName(NotificationCenter.fileDidFailedLoad, str, Integer.valueOf(i));
                }

                public void fileLoadProgressChanged(String str, float f) {
                    ImageLoader.this.fileProgresses.put(str, Float.valueOf(f));
                    long currentTimeMillis = System.currentTimeMillis();
                    if (ImageLoader.this.lastProgressUpdateTime == 0 || ImageLoader.this.lastProgressUpdateTime < currentTimeMillis - 500) {
                        ImageLoader.this.lastProgressUpdateTime = currentTimeMillis;
                        AndroidUtilities.runOnUIThread(new -$$Lambda$ImageLoader$3$iY0R0L0rfhonnXAaHAq36LTxLHA(i, str, f));
                    }
                }
            });
            i++;
        }
        FileLoader.setMediaDirs(sparseArray);
        AnonymousClass4 anonymousClass4 = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("file system changed");
                }
                -$$Lambda$ImageLoader$4$C7Gf_cAEPSage-rixfg5JW73rtw -__lambda_imageloader_4_c7gf_caepsage-rixfg5jw73rtw = new -$$Lambda$ImageLoader$4$C7Gf_cAEPSage-rixfg5JW73rtw(this);
                if ("android.intent.action.MEDIA_UNMOUNTED".equals(intent.getAction())) {
                    AndroidUtilities.runOnUIThread(-__lambda_imageloader_4_c7gf_caepsage-rixfg5jw73rtw, 1000);
                } else {
                    -__lambda_imageloader_4_c7gf_caepsage-rixfg5jw73rtw.run();
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
            ApplicationLoader.applicationContext.registerReceiver(anonymousClass4, intentFilter);
        } catch (Throwable unused) {
        }
        checkMediaPaths();
    }

    public void checkMediaPaths() {
        this.cacheOutQueue.postRunnable(new -$$Lambda$ImageLoader$TEcsmbVkFIlJCFa-8B6JxwYMU3A(this));
    }

    public /* synthetic */ void lambda$checkMediaPaths$1$ImageLoader() {
        AndroidUtilities.runOnUIThread(new -$$Lambda$ImageLoader$54eJN0C_gHRDy8W6-JUJzMnPHt0(createMediaPaths()));
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
        String str = ".nomedia";
        SparseArray sparseArray = new SparseArray();
        File cacheDir = AndroidUtilities.getCacheDir();
        if (!cacheDir.isDirectory()) {
            try {
                cacheDir.mkdirs();
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
        try {
            new File(cacheDir, str).createNewFile();
        } catch (Exception e2) {
            FileLog.e(e2);
        }
        sparseArray.put(4, cacheDir);
        if (BuildVars.LOGS_ENABLED) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("cache path = ");
            stringBuilder.append(cacheDir);
            FileLog.d(stringBuilder.toString());
        }
        try {
            if ("mounted".equals(Environment.getExternalStorageState())) {
                this.telegramPath = new File(Environment.getExternalStorageDirectory(), "Telegram");
                this.telegramPath.mkdirs();
                if (this.telegramPath.isDirectory()) {
                    File file;
                    StringBuilder stringBuilder2;
                    try {
                        file = new File(this.telegramPath, "Telegram Images");
                        file.mkdir();
                        if (file.isDirectory() && canMoveFiles(cacheDir, file, 0)) {
                            sparseArray.put(0, file);
                            if (BuildVars.LOGS_ENABLED) {
                                stringBuilder2 = new StringBuilder();
                                stringBuilder2.append("image path = ");
                                stringBuilder2.append(file);
                                FileLog.d(stringBuilder2.toString());
                            }
                        }
                    } catch (Exception e22) {
                        FileLog.e(e22);
                    }
                    try {
                        file = new File(this.telegramPath, "Telegram Video");
                        file.mkdir();
                        if (file.isDirectory() && canMoveFiles(cacheDir, file, 2)) {
                            sparseArray.put(2, file);
                            if (BuildVars.LOGS_ENABLED) {
                                stringBuilder2 = new StringBuilder();
                                stringBuilder2.append("video path = ");
                                stringBuilder2.append(file);
                                FileLog.d(stringBuilder2.toString());
                            }
                        }
                    } catch (Exception e222) {
                        FileLog.e(e222);
                    }
                    try {
                        file = new File(this.telegramPath, "Telegram Audio");
                        file.mkdir();
                        if (file.isDirectory() && canMoveFiles(cacheDir, file, 1)) {
                            new File(file, str).createNewFile();
                            sparseArray.put(1, file);
                            if (BuildVars.LOGS_ENABLED) {
                                stringBuilder2 = new StringBuilder();
                                stringBuilder2.append("audio path = ");
                                stringBuilder2.append(file);
                                FileLog.d(stringBuilder2.toString());
                            }
                        }
                    } catch (Exception e2222) {
                        FileLog.e(e2222);
                    }
                    try {
                        file = new File(this.telegramPath, "Telegram Documents");
                        file.mkdir();
                        if (file.isDirectory() && canMoveFiles(cacheDir, file, 3)) {
                            new File(file, str).createNewFile();
                            sparseArray.put(3, file);
                            if (BuildVars.LOGS_ENABLED) {
                                StringBuilder stringBuilder3 = new StringBuilder();
                                stringBuilder3.append("documents path = ");
                                stringBuilder3.append(file);
                                FileLog.d(stringBuilder3.toString());
                            }
                        }
                    } catch (Exception e3) {
                        FileLog.e(e3);
                    }
                }
            } else if (BuildVars.LOGS_ENABLED) {
                FileLog.d("this Android can't rename files");
            }
            SharedConfig.checkSaveToGalleryFiles();
        } catch (Exception e32) {
            FileLog.e(e32);
        }
        return sparseArray;
    }

    /* JADX WARNING: Removed duplicated region for block: B:38:0x0086 A:{SYNTHETIC, Splitter:B:38:0x0086} */
    /* JADX WARNING: Removed duplicated region for block: B:31:0x007a A:{SYNTHETIC, Splitter:B:31:0x007a} */
    private boolean canMoveFiles(java.io.File r5, java.io.File r6, int r7) {
        /*
        r4 = this;
        r0 = 1;
        r1 = 0;
        if (r7 != 0) goto L_0x0018;
    L_0x0004:
        r7 = new java.io.File;	 Catch:{ Exception -> 0x0016 }
        r2 = "000000000_999999_temp.jpg";
        r7.<init>(r5, r2);	 Catch:{ Exception -> 0x0016 }
        r5 = new java.io.File;	 Catch:{ Exception -> 0x0016 }
        r2 = "000000000_999999.jpg";
        r5.<init>(r6, r2);	 Catch:{ Exception -> 0x0016 }
        goto L_0x004f;
    L_0x0013:
        r5 = move-exception;
        goto L_0x0084;
    L_0x0016:
        r5 = move-exception;
        goto L_0x0075;
    L_0x0018:
        r2 = 3;
        if (r7 != r2) goto L_0x002a;
    L_0x001b:
        r7 = new java.io.File;	 Catch:{ Exception -> 0x0016 }
        r2 = "000000000_999999_temp.doc";
        r7.<init>(r5, r2);	 Catch:{ Exception -> 0x0016 }
        r5 = new java.io.File;	 Catch:{ Exception -> 0x0016 }
        r2 = "000000000_999999.doc";
        r5.<init>(r6, r2);	 Catch:{ Exception -> 0x0016 }
        goto L_0x004f;
    L_0x002a:
        if (r7 != r0) goto L_0x003b;
    L_0x002c:
        r7 = new java.io.File;	 Catch:{ Exception -> 0x0016 }
        r2 = "000000000_999999_temp.ogg";
        r7.<init>(r5, r2);	 Catch:{ Exception -> 0x0016 }
        r5 = new java.io.File;	 Catch:{ Exception -> 0x0016 }
        r2 = "000000000_999999.ogg";
        r5.<init>(r6, r2);	 Catch:{ Exception -> 0x0016 }
        goto L_0x004f;
    L_0x003b:
        r2 = 2;
        if (r7 != r2) goto L_0x004d;
    L_0x003e:
        r7 = new java.io.File;	 Catch:{ Exception -> 0x0016 }
        r2 = "000000000_999999_temp.mp4";
        r7.<init>(r5, r2);	 Catch:{ Exception -> 0x0016 }
        r5 = new java.io.File;	 Catch:{ Exception -> 0x0016 }
        r2 = "000000000_999999.mp4";
        r5.<init>(r6, r2);	 Catch:{ Exception -> 0x0016 }
        goto L_0x004f;
    L_0x004d:
        r5 = r1;
        r7 = r5;
    L_0x004f:
        r6 = 1024; // 0x400 float:1.435E-42 double:5.06E-321;
        r6 = new byte[r6];	 Catch:{ Exception -> 0x0016 }
        r7.createNewFile();	 Catch:{ Exception -> 0x0016 }
        r2 = new java.io.RandomAccessFile;	 Catch:{ Exception -> 0x0016 }
        r3 = "rws";
        r2.<init>(r7, r3);	 Catch:{ Exception -> 0x0016 }
        r2.write(r6);	 Catch:{ Exception -> 0x0073, all -> 0x0070 }
        r2.close();	 Catch:{ Exception -> 0x0073, all -> 0x0070 }
        r6 = r7.renameTo(r5);	 Catch:{ Exception -> 0x0016 }
        r7.delete();	 Catch:{ Exception -> 0x0016 }
        r5.delete();	 Catch:{ Exception -> 0x0016 }
        if (r6 == 0) goto L_0x0082;
    L_0x006f:
        return r0;
    L_0x0070:
        r5 = move-exception;
        r1 = r2;
        goto L_0x0084;
    L_0x0073:
        r5 = move-exception;
        r1 = r2;
    L_0x0075:
        org.telegram.messenger.FileLog.e(r5);	 Catch:{ all -> 0x0013 }
        if (r1 == 0) goto L_0x0082;
    L_0x007a:
        r1.close();	 Catch:{ Exception -> 0x007e }
        goto L_0x0082;
    L_0x007e:
        r5 = move-exception;
        org.telegram.messenger.FileLog.e(r5);
    L_0x0082:
        r5 = 0;
        return r5;
    L_0x0084:
        if (r1 == 0) goto L_0x008e;
    L_0x0086:
        r1.close();	 Catch:{ Exception -> 0x008a }
        goto L_0x008e;
    L_0x008a:
        r6 = move-exception;
        org.telegram.messenger.FileLog.e(r6);
    L_0x008e:
        throw r5;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ImageLoader.canMoveFiles(java.io.File, java.io.File, int):boolean");
    }

    public Float getFileProgress(String str) {
        return str == null ? null : (Float) this.fileProgresses.get(str);
    }

    public String getReplacedKey(String str) {
        return str == null ? null : (String) this.replacedBitmaps.get(str);
    }

    private void performReplace(String str, String str2) {
        BitmapDrawable bitmapDrawable = (BitmapDrawable) this.memCache.get(str);
        this.replacedBitmaps.put(str, str2);
        if (bitmapDrawable != null) {
            BitmapDrawable bitmapDrawable2 = (BitmapDrawable) this.memCache.get(str2);
            Object obj = null;
            if (!(bitmapDrawable2 == null || bitmapDrawable2.getBitmap() == null || bitmapDrawable.getBitmap() == null)) {
                Bitmap bitmap = bitmapDrawable2.getBitmap();
                Bitmap bitmap2 = bitmapDrawable.getBitmap();
                if (bitmap.getWidth() > bitmap2.getWidth() || bitmap.getHeight() > bitmap2.getHeight()) {
                    obj = 1;
                }
            }
            if (obj == null) {
                this.ignoreRemoval = str;
                this.memCache.remove(str);
                this.memCache.put(str2, bitmapDrawable);
                this.ignoreRemoval = null;
            } else {
                this.memCache.remove(str);
            }
        }
        Integer num = (Integer) this.bitmapUseCounts.get(str);
        if (num != null) {
            this.bitmapUseCounts.put(str2, num);
            this.bitmapUseCounts.remove(str);
        }
    }

    public void incrementUseCount(String str) {
        Integer num = (Integer) this.bitmapUseCounts.get(str);
        if (num == null) {
            this.bitmapUseCounts.put(str, Integer.valueOf(1));
        } else {
            this.bitmapUseCounts.put(str, Integer.valueOf(num.intValue() + 1));
        }
    }

    public boolean decrementUseCount(String str) {
        Integer num = (Integer) this.bitmapUseCounts.get(str);
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
        boolean z2 = true;
        if (z) {
            if (this.lottieMemCache.get(str) == null) {
                z2 = false;
            }
            return z2;
        }
        if (this.memCache.get(str) == null) {
            z2 = false;
        }
        return z2;
    }

    public void clearMemory() {
        this.memCache.evictAll();
        this.lottieMemCache.evictAll();
    }

    private void removeFromWaitingForThumb(int i, ImageReceiver imageReceiver) {
        String str = (String) this.waitingForQualityThumbByTag.get(i);
        if (str != null) {
            ThumbGenerateInfo thumbGenerateInfo = (ThumbGenerateInfo) this.waitingForQualityThumb.get(str);
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
            this.imageLoadQueue.postRunnable(new -$$Lambda$ImageLoader$QQrxxTOTOPgi4Ibzj2dcFh6tMmY(this, z, imageReceiver));
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
                i2 = imageReceiver.getTag(i2);
                if (i2 != 0) {
                    if (i == 0) {
                        removeFromWaitingForThumb(i2, imageReceiver);
                    }
                    CacheImage cacheImage = (CacheImage) this.imageLoadingByTag.get(i2);
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
        BitmapDrawable bitmapDrawable = (BitmapDrawable) this.memCache.get(str);
        if (bitmapDrawable == null) {
            ArrayList filterKeys = this.memCache.getFilterKeys(str);
            if (!(filterKeys == null || filterKeys.isEmpty())) {
                LruCache lruCache = this.memCache;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(str);
                stringBuilder.append("@");
                stringBuilder.append((String) filterKeys.get(0));
                return (BitmapDrawable) lruCache.get(stringBuilder.toString());
            }
        }
        return bitmapDrawable;
    }

    public BitmapDrawable getImageFromMemory(TLObject tLObject, String str, String str2) {
        String str3 = null;
        if (tLObject == null && str == null) {
            return null;
        }
        if (str != null) {
            str3 = Utilities.MD5(str);
        } else {
            String str4 = "_";
            StringBuilder stringBuilder;
            if (tLObject instanceof FileLocation) {
                FileLocation fileLocation = (FileLocation) tLObject;
                stringBuilder = new StringBuilder();
                stringBuilder.append(fileLocation.volume_id);
                stringBuilder.append(str4);
                stringBuilder.append(fileLocation.local_id);
                str3 = stringBuilder.toString();
            } else if (tLObject instanceof Document) {
                Document document = (Document) tLObject;
                stringBuilder = new StringBuilder();
                stringBuilder.append(document.dc_id);
                stringBuilder.append(str4);
                stringBuilder.append(document.id);
                str3 = stringBuilder.toString();
            } else if (tLObject instanceof SecureDocument) {
                SecureDocument secureDocument = (SecureDocument) tLObject;
                stringBuilder = new StringBuilder();
                stringBuilder.append(secureDocument.secureFile.dc_id);
                stringBuilder.append(str4);
                stringBuilder.append(secureDocument.secureFile.id);
                str3 = stringBuilder.toString();
            } else if (tLObject instanceof WebFile) {
                str3 = Utilities.MD5(((WebFile) tLObject).url);
            }
        }
        if (str2 != null) {
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append(str3);
            stringBuilder2.append("@");
            stringBuilder2.append(str2);
            str3 = stringBuilder2.toString();
        }
        return (BitmapDrawable) this.memCache.get(str3);
    }

    private void replaceImageInCacheInternal(String str, String str2, ImageLocation imageLocation) {
        ArrayList filterKeys = this.memCache.getFilterKeys(str);
        if (filterKeys != null) {
            for (int i = 0; i < filterKeys.size(); i++) {
                String str3 = (String) filterKeys.get(i);
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(str);
                String str4 = "@";
                stringBuilder.append(str4);
                stringBuilder.append(str3);
                String stringBuilder2 = stringBuilder.toString();
                StringBuilder stringBuilder3 = new StringBuilder();
                stringBuilder3.append(str2);
                stringBuilder3.append(str4);
                stringBuilder3.append(str3);
                performReplace(stringBuilder2, stringBuilder3.toString());
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didReplacedPhotoInMemCache, stringBuilder2, str3, imageLocation);
            }
            return;
        }
        performReplace(str, str2);
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didReplacedPhotoInMemCache, str, str2, imageLocation);
    }

    public /* synthetic */ void lambda$replaceImageInCache$3$ImageLoader(String str, String str2, ImageLocation imageLocation) {
        replaceImageInCacheInternal(str, str2, imageLocation);
    }

    public void replaceImageInCache(String str, String str2, ImageLocation imageLocation, boolean z) {
        if (z) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$ImageLoader$goqDHHQdnb5snOP60neGaS99rrI(this, str, str2, imageLocation));
        } else {
            replaceImageInCacheInternal(str, str2, imageLocation);
        }
    }

    public void putImageToCache(BitmapDrawable bitmapDrawable, String str) {
        this.memCache.put(str, bitmapDrawable);
    }

    private void generateThumb(int i, File file, ThumbGenerateInfo thumbGenerateInfo) {
        if ((i == 0 || i == 2 || i == 3) && file != null && thumbGenerateInfo != null) {
            if (((ThumbGenerateTask) this.thumbGenerateTasks.get(FileLoader.getAttachFileName(thumbGenerateInfo.parentDocument))) == null) {
                this.thumbGeneratingQueue.postRunnable(new ThumbGenerateTask(i, file, thumbGenerateInfo));
            }
        }
    }

    public void cancelForceLoadingForImageReceiver(ImageReceiver imageReceiver) {
        if (imageReceiver != null) {
            String imageKey = imageReceiver.getImageKey();
            if (imageKey != null) {
                this.imageLoadQueue.postRunnable(new -$$Lambda$ImageLoader$pOb77-ep1O4qdDkpbIPheznVQgg(this, imageKey));
            }
        }
    }

    public /* synthetic */ void lambda$cancelForceLoadingForImageReceiver$4$ImageLoader(String str) {
        Integer num = (Integer) this.forceLoadingImages.remove(str);
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
            Document qulityThumbDocument = imageReceiver.getQulityThumbDocument();
            boolean isShouldGenerateQualityThumb = imageReceiver.isShouldGenerateQualityThumb();
            int currentAccount = imageReceiver.getCurrentAccount();
            boolean z = i6 == 0 && imageReceiver.isCurrentKeyQuality();
            -$$Lambda$ImageLoader$EuQQJ-fWIczut7cMYUsdE2vomJE -__lambda_imageloader_euqqj-fwiczut7cmyusde2vomje = r0;
            DispatchQueue dispatchQueue = this.imageLoadQueue;
            -$$Lambda$ImageLoader$EuQQJ-fWIczut7cMYUsdE2vomJE -__lambda_imageloader_euqqj-fwiczut7cmyusde2vomje2 = new -$$Lambda$ImageLoader$EuQQJ-fWIczut7cMYUsdE2vomJE(this, i4, str2, str, i7, imageReceiver, str4, i3, i5, imageLocation, z, parentObject, qulityThumbDocument, isNeedsQualityThumb, isShouldGenerateQualityThumb, i2, i, str3, currentAccount);
            dispatchQueue.postRunnable(-__lambda_imageloader_euqqj-fwiczut7cmyusde2vomje);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:168:0x038c  */
    /* JADX WARNING: Removed duplicated region for block: B:167:0x0381  */
    /* JADX WARNING: Removed duplicated region for block: B:167:0x0381  */
    /* JADX WARNING: Removed duplicated region for block: B:168:0x038c  */
    /* JADX WARNING: Removed duplicated region for block: B:65:0x0187  */
    /* JADX WARNING: Removed duplicated region for block: B:69:0x0195  */
    /* JADX WARNING: Removed duplicated region for block: B:82:0x01e8  */
    /* JADX WARNING: Removed duplicated region for block: B:71:0x0199  */
    /* JADX WARNING: Removed duplicated region for block: B:89:0x01f7  */
    /* JADX WARNING: Missing block: B:66:0x0190, code skipped:
            if (r8.exists() == false) goto L_0x0192;
     */
    /* JADX WARNING: Missing block: B:130:0x0297, code skipped:
            if (r7 == false) goto L_0x02e7;
     */
    public /* synthetic */ void lambda$createLoadOperationForImageReceiver$5$ImageLoader(int r22, java.lang.String r23, java.lang.String r24, int r25, org.telegram.messenger.ImageReceiver r26, java.lang.String r27, int r28, int r29, org.telegram.messenger.ImageLocation r30, boolean r31, java.lang.Object r32, org.telegram.tgnet.TLRPC.Document r33, boolean r34, boolean r35, int r36, int r37, java.lang.String r38, int r39) {
        /*
        r21 = this;
        r0 = r21;
        r1 = r22;
        r2 = r23;
        r9 = r24;
        r10 = r25;
        r11 = r26;
        r12 = r27;
        r13 = r30;
        r14 = r32;
        r15 = r33;
        r8 = r36;
        r7 = r37;
        r6 = 2;
        if (r1 == r6) goto L_0x00a2;
    L_0x001b:
        r3 = r0.imageLoadingByUrl;
        r3 = r3.get(r2);
        r3 = (org.telegram.messenger.ImageLoader.CacheImage) r3;
        r4 = r0.imageLoadingByKeys;
        r4 = r4.get(r9);
        r4 = (org.telegram.messenger.ImageLoader.CacheImage) r4;
        r5 = r0.imageLoadingByTag;
        r5 = r5.get(r10);
        r5 = (org.telegram.messenger.ImageLoader.CacheImage) r5;
        if (r5 == 0) goto L_0x006f;
    L_0x0035:
        if (r5 != r4) goto L_0x0040;
    L_0x0037:
        r18 = r3;
        r16 = r4;
        r3 = 1;
        r9 = 1;
        r19 = 0;
        goto L_0x0077;
    L_0x0040:
        if (r5 != r3) goto L_0x0064;
    L_0x0042:
        if (r4 != 0) goto L_0x005b;
    L_0x0044:
        r18 = r3;
        r3 = r5;
        r16 = r4;
        r5 = 1;
        r4 = r26;
        r9 = 1;
        r19 = 0;
        r5 = r24;
        r6 = r27;
        r7 = r28;
        r8 = r29;
        r3.replaceImageReceiver(r4, r5, r6, r7, r8);
        goto L_0x0062;
    L_0x005b:
        r18 = r3;
        r16 = r4;
        r9 = 1;
        r19 = 0;
    L_0x0062:
        r3 = 1;
        goto L_0x0077;
    L_0x0064:
        r18 = r3;
        r16 = r4;
        r9 = 1;
        r19 = 0;
        r5.removeImageReceiver(r11);
        goto L_0x0076;
    L_0x006f:
        r18 = r3;
        r16 = r4;
        r9 = 1;
        r19 = 0;
    L_0x0076:
        r3 = 0;
    L_0x0077:
        if (r3 != 0) goto L_0x008c;
    L_0x0079:
        if (r16 == 0) goto L_0x008c;
    L_0x007b:
        r3 = r16;
        r4 = r26;
        r5 = r24;
        r6 = r27;
        r7 = r28;
        r8 = r29;
        r3.addImageReceiver(r4, r5, r6, r7, r8);
        r5 = 1;
        goto L_0x008d;
    L_0x008c:
        r5 = r3;
    L_0x008d:
        if (r5 != 0) goto L_0x00a6;
    L_0x008f:
        if (r18 == 0) goto L_0x00a6;
    L_0x0091:
        r3 = r18;
        r4 = r26;
        r5 = r24;
        r6 = r27;
        r7 = r28;
        r8 = r29;
        r3.addImageReceiver(r4, r5, r6, r7, r8);
        r5 = 1;
        goto L_0x00a6;
    L_0x00a2:
        r9 = 1;
        r19 = 0;
        r5 = 0;
    L_0x00a6:
        if (r5 != 0) goto L_0x04fe;
    L_0x00a8:
        r3 = r13.path;
        r4 = "_";
        r8 = "athumb";
        r16 = 4;
        if (r3 == 0) goto L_0x0109;
    L_0x00b2:
        r7 = "http";
        r7 = r3.startsWith(r7);
        if (r7 != 0) goto L_0x0102;
    L_0x00ba:
        r7 = r3.startsWith(r8);
        if (r7 != 0) goto L_0x0102;
    L_0x00c0:
        r7 = "thumb://";
        r7 = r3.startsWith(r7);
        r10 = ":";
        if (r7 == 0) goto L_0x00df;
    L_0x00ca:
        r7 = 8;
        r7 = r3.indexOf(r10, r7);
        if (r7 < 0) goto L_0x00dd;
    L_0x00d2:
        r10 = new java.io.File;
        r7 = r7 + r9;
        r3 = r3.substring(r7);
        r10.<init>(r3);
        goto L_0x0100;
    L_0x00dd:
        r10 = 0;
        goto L_0x0100;
    L_0x00df:
        r7 = "vthumb://";
        r7 = r3.startsWith(r7);
        if (r7 == 0) goto L_0x00fa;
    L_0x00e7:
        r7 = 9;
        r7 = r3.indexOf(r10, r7);
        if (r7 < 0) goto L_0x00dd;
    L_0x00ef:
        r10 = new java.io.File;
        r7 = r7 + r9;
        r3 = r3.substring(r7);
        r10.<init>(r3);
        goto L_0x0100;
    L_0x00fa:
        r7 = new java.io.File;
        r7.<init>(r3);
        r10 = r7;
    L_0x0100:
        r3 = 1;
        goto L_0x0104;
    L_0x0102:
        r3 = 0;
        r10 = 0;
    L_0x0104:
        r20 = r8;
        r5 = r10;
        goto L_0x01f3;
    L_0x0109:
        if (r1 != 0) goto L_0x01ef;
    L_0x010b:
        if (r31 == 0) goto L_0x01ef;
    L_0x010d:
        r3 = r14 instanceof org.telegram.messenger.MessageObject;
        if (r3 == 0) goto L_0x0129;
    L_0x0111:
        r3 = r14;
        r3 = (org.telegram.messenger.MessageObject) r3;
        r7 = r3.getDocument();
        r15 = r3.messageOwner;
        r6 = r15.attachPath;
        r15 = org.telegram.messenger.FileLoader.getPathToMessage(r15);
        r3 = r3.getFileType();
        r9 = r3;
        r3 = r15;
        r15 = r7;
        r7 = 0;
        goto L_0x0141;
    L_0x0129:
        if (r15 == 0) goto L_0x013c;
    L_0x012b:
        r3 = org.telegram.messenger.FileLoader.getPathToAttach(r15, r9);
        r6 = org.telegram.messenger.MessageObject.isVideoDocument(r33);
        if (r6 == 0) goto L_0x0137;
    L_0x0135:
        r6 = 2;
        goto L_0x0138;
    L_0x0137:
        r6 = 3;
    L_0x0138:
        r9 = r6;
        r6 = 0;
        r7 = 1;
        goto L_0x0141;
    L_0x013c:
        r3 = 0;
        r6 = 0;
        r7 = 0;
        r9 = 0;
        r15 = 0;
    L_0x0141:
        if (r15 == 0) goto L_0x01eb;
    L_0x0143:
        if (r34 == 0) goto L_0x017b;
    L_0x0145:
        r5 = new java.io.File;
        r33 = r3;
        r3 = org.telegram.messenger.FileLoader.getDirectory(r16);
        r20 = r8;
        r8 = new java.lang.StringBuilder;
        r8.<init>();
        r14 = "q_";
        r8.append(r14);
        r14 = r15.dc_id;
        r8.append(r14);
        r8.append(r4);
        r13 = r15.id;
        r8.append(r13);
        r13 = ".jpg";
        r8.append(r13);
        r8 = r8.toString();
        r5.<init>(r3, r8);
        r3 = r5.exists();
        if (r3 != 0) goto L_0x0179;
    L_0x0178:
        goto L_0x017f;
    L_0x0179:
        r3 = 1;
        goto L_0x0181;
    L_0x017b:
        r33 = r3;
        r20 = r8;
    L_0x017f:
        r3 = 0;
        r5 = 0;
    L_0x0181:
        r8 = android.text.TextUtils.isEmpty(r6);
        if (r8 != 0) goto L_0x0192;
    L_0x0187:
        r8 = new java.io.File;
        r8.<init>(r6);
        r6 = r8.exists();
        if (r6 != 0) goto L_0x0193;
    L_0x0192:
        r8 = 0;
    L_0x0193:
        if (r8 != 0) goto L_0x0197;
    L_0x0195:
        r8 = r33;
    L_0x0197:
        if (r5 != 0) goto L_0x01e8;
    L_0x0199:
        r1 = org.telegram.messenger.FileLoader.getAttachFileName(r15);
        r2 = r0.waitingForQualityThumb;
        r2 = r2.get(r1);
        r2 = (org.telegram.messenger.ImageLoader.ThumbGenerateInfo) r2;
        if (r2 != 0) goto L_0x01bb;
    L_0x01a7:
        r2 = new org.telegram.messenger.ImageLoader$ThumbGenerateInfo;
        r3 = 0;
        r2.<init>(r0, r3);
        r2.parentDocument = r15;
        r2.filter = r12;
        r2.big = r7;
        r3 = r0.waitingForQualityThumb;
        r3.put(r1, r2);
    L_0x01bb:
        r3 = r2.imageReceiverArray;
        r3 = r3.contains(r11);
        if (r3 != 0) goto L_0x01d7;
    L_0x01c5:
        r3 = r2.imageReceiverArray;
        r3.add(r11);
        r3 = r2.imageReceiverGuidsArray;
        r4 = java.lang.Integer.valueOf(r29);
        r3.add(r4);
    L_0x01d7:
        r3 = r0.waitingForQualityThumbByTag;
        r3.put(r10, r1);
        r1 = r8.exists();
        if (r1 == 0) goto L_0x01e7;
    L_0x01e2:
        if (r35 == 0) goto L_0x01e7;
    L_0x01e4:
        r0.generateThumb(r9, r8, r2);
    L_0x01e7:
        return;
    L_0x01e8:
        r6 = r3;
        r3 = 1;
        goto L_0x01f4;
    L_0x01eb:
        r20 = r8;
        r3 = 1;
        goto L_0x01f2;
    L_0x01ef:
        r20 = r8;
        r3 = 0;
    L_0x01f2:
        r5 = 0;
    L_0x01f3:
        r6 = 0;
    L_0x01f4:
        r9 = 2;
        if (r1 == r9) goto L_0x04fe;
    L_0x01f7:
        r7 = r30.isEncrypted();
        r10 = new org.telegram.messenger.ImageLoader$CacheImage;
        r8 = 0;
        r10.<init>(r0, r8);
        if (r31 != 0) goto L_0x0250;
    L_0x0203:
        r13 = r30;
        r8 = r13.webFile;
        r8 = org.telegram.messenger.MessageObject.isGifDocument(r8);
        if (r8 != 0) goto L_0x024c;
    L_0x020d:
        r8 = r13.document;
        r8 = org.telegram.messenger.MessageObject.isGifDocument(r8);
        if (r8 != 0) goto L_0x024c;
    L_0x0215:
        r8 = r13.document;
        r8 = org.telegram.messenger.MessageObject.isRoundVideoDocument(r8);
        if (r8 == 0) goto L_0x021e;
    L_0x021d:
        goto L_0x024c;
    L_0x021e:
        r8 = r13.path;
        if (r8 == 0) goto L_0x0252;
    L_0x0222:
        r14 = "vthumb";
        r14 = r8.startsWith(r14);
        if (r14 != 0) goto L_0x0252;
    L_0x022a:
        r14 = "thumb";
        r14 = r8.startsWith(r14);
        if (r14 != 0) goto L_0x0252;
    L_0x0232:
        r14 = "jpg";
        r8 = getHttpUrlExtension(r8, r14);
        r14 = "mp4";
        r14 = r8.equals(r14);
        if (r14 != 0) goto L_0x0248;
    L_0x0240:
        r14 = "gif";
        r8 = r8.equals(r14);
        if (r8 == 0) goto L_0x0252;
    L_0x0248:
        r8 = 1;
        r10.animatedFile = r8;
        goto L_0x0252;
    L_0x024c:
        r8 = 1;
        r10.animatedFile = r8;
        goto L_0x0252;
    L_0x0250:
        r13 = r30;
    L_0x0252:
        if (r5 != 0) goto L_0x0395;
    L_0x0254:
        r8 = r13.photoSize;
        r8 = r8 instanceof org.telegram.tgnet.TLRPC.TL_photoStrippedSize;
        r14 = "g";
        if (r8 == 0) goto L_0x0263;
    L_0x025c:
        r15 = r36;
        r0 = r5;
        r4 = 1;
    L_0x0260:
        r5 = 0;
        goto L_0x037b;
    L_0x0263:
        r5 = r13.secureDocument;
        if (r5 == 0) goto L_0x0283;
    L_0x0267:
        r10.secureDocument = r5;
        r3 = r10.secureDocument;
        r3 = r3.secureFile;
        r3 = r3.dc_id;
        r4 = -NUM; // 0xfffffffvar_ float:-0.0 double:NaN;
        if (r3 != r4) goto L_0x0275;
    L_0x0273:
        r4 = 1;
        goto L_0x0276;
    L_0x0275:
        r4 = 0;
    L_0x0276:
        r3 = new java.io.File;
        r5 = org.telegram.messenger.FileLoader.getDirectory(r16);
        r3.<init>(r5, r2);
        r15 = r36;
        r0 = r3;
        goto L_0x0260;
    L_0x0283:
        r5 = r14.equals(r12);
        r8 = "application/x-tgsticker";
        if (r5 != 0) goto L_0x02e3;
    L_0x028b:
        r15 = r36;
        if (r15 != 0) goto L_0x029a;
    L_0x028f:
        r5 = r37;
        if (r5 <= 0) goto L_0x029c;
    L_0x0293:
        r9 = r13.path;
        if (r9 != 0) goto L_0x029c;
    L_0x0297:
        if (r7 == 0) goto L_0x02e7;
    L_0x0299:
        goto L_0x029c;
    L_0x029a:
        r5 = r37;
    L_0x029c:
        r4 = new java.io.File;
        r7 = org.telegram.messenger.FileLoader.getDirectory(r16);
        r4.<init>(r7, r2);
        r7 = r4.exists();
        if (r7 == 0) goto L_0x02af;
    L_0x02ab:
        r25 = r3;
        r6 = 1;
        goto L_0x02d1;
    L_0x02af:
        r7 = 2;
        if (r15 != r7) goto L_0x02cf;
    L_0x02b2:
        r4 = new java.io.File;
        r7 = org.telegram.messenger.FileLoader.getDirectory(r16);
        r9 = new java.lang.StringBuilder;
        r9.<init>();
        r9.append(r2);
        r25 = r3;
        r3 = ".enc";
        r9.append(r3);
        r3 = r9.toString();
        r4.<init>(r7, r3);
        goto L_0x02d1;
    L_0x02cf:
        r25 = r3;
    L_0x02d1:
        r3 = r13.document;
        if (r3 == 0) goto L_0x02dd;
    L_0x02d5:
        r3 = r3.mime_type;
        r3 = r8.equals(r3);
        r10.lottieFile = r3;
    L_0x02dd:
        r0 = r4;
        r5 = 0;
        r4 = r25;
        goto L_0x037b;
    L_0x02e3:
        r15 = r36;
        r5 = r37;
    L_0x02e7:
        r25 = r3;
        r3 = r13.document;
        if (r3 == 0) goto L_0x035b;
    L_0x02ed:
        r7 = r3 instanceof org.telegram.tgnet.TLRPC.TL_documentEncrypted;
        if (r7 == 0) goto L_0x02fb;
    L_0x02f1:
        r7 = new java.io.File;
        r9 = org.telegram.messenger.FileLoader.getDirectory(r16);
        r7.<init>(r9, r2);
        goto L_0x0316;
    L_0x02fb:
        r7 = org.telegram.messenger.MessageObject.isVideoDocument(r3);
        if (r7 == 0) goto L_0x030c;
    L_0x0301:
        r7 = new java.io.File;
        r9 = 2;
        r5 = org.telegram.messenger.FileLoader.getDirectory(r9);
        r7.<init>(r5, r2);
        goto L_0x0316;
    L_0x030c:
        r7 = new java.io.File;
        r5 = 3;
        r5 = org.telegram.messenger.FileLoader.getDirectory(r5);
        r7.<init>(r5, r2);
    L_0x0316:
        r5 = r14.equals(r12);
        if (r5 == 0) goto L_0x0349;
    L_0x031c:
        r5 = r7.exists();
        if (r5 != 0) goto L_0x0349;
    L_0x0322:
        r7 = new java.io.File;
        r5 = org.telegram.messenger.FileLoader.getDirectory(r16);
        r9 = new java.lang.StringBuilder;
        r9.<init>();
        r33 = r6;
        r6 = r3.dc_id;
        r9.append(r6);
        r9.append(r4);
        r0 = r3.id;
        r9.append(r0);
        r0 = ".temp";
        r9.append(r0);
        r0 = r9.toString();
        r7.<init>(r5, r0);
        goto L_0x034b;
    L_0x0349:
        r33 = r6;
    L_0x034b:
        r0 = r3.mime_type;
        r0 = r8.equals(r0);
        r10.lottieFile = r0;
        r5 = r3.size;
        r4 = r25;
        r6 = r33;
        r0 = r7;
        goto L_0x037b;
    L_0x035b:
        r33 = r6;
        r0 = r13.webFile;
        if (r0 == 0) goto L_0x036c;
    L_0x0361:
        r0 = new java.io.File;
        r1 = 3;
        r1 = org.telegram.messenger.FileLoader.getDirectory(r1);
        r0.<init>(r1, r2);
        goto L_0x0375;
    L_0x036c:
        r0 = new java.io.File;
        r1 = org.telegram.messenger.FileLoader.getDirectory(r19);
        r0.<init>(r1, r2);
    L_0x0375:
        r4 = r25;
        r6 = r33;
        goto L_0x0260;
    L_0x037b:
        r1 = r14.equals(r12);
        if (r1 == 0) goto L_0x038c;
    L_0x0381:
        r3 = 1;
        r10.animatedFile = r3;
        r10.size = r5;
        r1 = r28;
        r8 = r0;
        r14 = r6;
        r9 = 1;
        goto L_0x0392;
    L_0x038c:
        r3 = 1;
        r1 = r28;
        r8 = r0;
        r9 = r4;
        r14 = r6;
    L_0x0392:
        r0 = r37;
        goto L_0x03a5;
    L_0x0395:
        r15 = r36;
        r0 = r37;
        r25 = r3;
        r33 = r6;
        r3 = 1;
        r9 = r25;
        r1 = r28;
        r14 = r33;
        r8 = r5;
    L_0x03a5:
        r10.imageType = r1;
        r7 = r24;
        r6 = 1;
        r10.key = r7;
        r10.filter = r12;
        r10.imageLocation = r13;
        r5 = r38;
        r10.ext = r5;
        r4 = r39;
        r10.currentAccount = r4;
        r3 = r32;
        r10.parentObject = r3;
        r1 = r13.lottieAnimation;
        if (r1 == 0) goto L_0x03c2;
    L_0x03c0:
        r10.lottieFile = r6;
    L_0x03c2:
        r1 = 2;
        if (r15 != r1) goto L_0x03e1;
    L_0x03c5:
        r1 = new java.io.File;
        r6 = org.telegram.messenger.FileLoader.getInternalCacheDir();
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r3.append(r2);
        r4 = ".enc.key";
        r3.append(r4);
        r3 = r3.toString();
        r1.<init>(r6, r3);
        r10.encryptionKeyPath = r1;
    L_0x03e1:
        r1 = r32;
        r3 = r10;
        r4 = r26;
        r5 = r24;
        r17 = 1;
        r6 = r27;
        r12 = r7;
        r7 = r28;
        r25 = r8;
        r1 = r20;
        r8 = r29;
        r3.addImageReceiver(r4, r5, r6, r7, r8);
        if (r9 != 0) goto L_0x04d8;
    L_0x03fa:
        if (r14 != 0) goto L_0x04d8;
    L_0x03fc:
        r3 = r25.exists();
        if (r3 == 0) goto L_0x0404;
    L_0x0402:
        goto L_0x04d8;
    L_0x0404:
        r10.url = r2;
        r7 = r21;
        r3 = r7.imageLoadingByUrl;
        r3.put(r2, r10);
        r2 = r13.path;
        if (r2 == 0) goto L_0x0465;
    L_0x0411:
        r2 = org.telegram.messenger.Utilities.MD5(r2);
        r3 = org.telegram.messenger.FileLoader.getDirectory(r16);
        r4 = new java.io.File;
        r5 = new java.lang.StringBuilder;
        r5.<init>();
        r5.append(r2);
        r2 = "_temp.jpg";
        r5.append(r2);
        r2 = r5.toString();
        r4.<init>(r3, r2);
        r10.tempFilePath = r4;
        r5 = r25;
        r10.finalFilePath = r5;
        r2 = r13.path;
        r1 = r2.startsWith(r1);
        if (r1 == 0) goto L_0x0451;
    L_0x043d:
        r0 = new org.telegram.messenger.ImageLoader$ArtworkLoadTask;
        r0.<init>(r10);
        r10.artworkTask = r0;
        r0 = r7.artworkTasks;
        r1 = r10.artworkTask;
        r0.add(r1);
        r8 = 0;
        r7.runArtworkTasks(r8);
        goto L_0x04ff;
    L_0x0451:
        r8 = 0;
        r1 = new org.telegram.messenger.ImageLoader$HttpImageTask;
        r1.<init>(r10, r0);
        r10.httpTask = r1;
        r0 = r7.httpTasks;
        r1 = r10.httpTask;
        r0.add(r1);
        r7.runHttpTasks(r8);
        goto L_0x04ff;
    L_0x0465:
        r8 = 0;
        r1 = r13.location;
        if (r1 == 0) goto L_0x048c;
    L_0x046a:
        if (r15 != 0) goto L_0x0474;
    L_0x046c:
        if (r0 <= 0) goto L_0x0472;
    L_0x046e:
        r0 = r13.key;
        if (r0 == 0) goto L_0x0474;
    L_0x0472:
        r6 = 1;
        goto L_0x0475;
    L_0x0474:
        r6 = r15;
    L_0x0475:
        r1 = org.telegram.messenger.FileLoader.getInstance(r39);
        if (r22 == 0) goto L_0x047f;
    L_0x047b:
        r0 = r32;
        r5 = 2;
        goto L_0x0482;
    L_0x047f:
        r0 = r32;
        r5 = 1;
    L_0x0482:
        r2 = r30;
        r3 = r32;
        r4 = r38;
        r1.loadFile(r2, r3, r4, r5, r6);
        goto L_0x04c6;
    L_0x048c:
        r0 = r32;
        r1 = r13.document;
        if (r1 == 0) goto L_0x04a1;
    L_0x0492:
        r1 = org.telegram.messenger.FileLoader.getInstance(r39);
        r2 = r13.document;
        if (r22 == 0) goto L_0x049c;
    L_0x049a:
        r3 = 2;
        goto L_0x049d;
    L_0x049c:
        r3 = 1;
    L_0x049d:
        r1.loadFile(r2, r0, r3, r15);
        goto L_0x04c6;
    L_0x04a1:
        r0 = r13.secureDocument;
        if (r0 == 0) goto L_0x04b4;
    L_0x04a5:
        r0 = org.telegram.messenger.FileLoader.getInstance(r39);
        r1 = r13.secureDocument;
        if (r22 == 0) goto L_0x04af;
    L_0x04ad:
        r2 = 2;
        goto L_0x04b0;
    L_0x04af:
        r2 = 1;
    L_0x04b0:
        r0.loadFile(r1, r2);
        goto L_0x04c6;
    L_0x04b4:
        r0 = r13.webFile;
        if (r0 == 0) goto L_0x04c6;
    L_0x04b8:
        r0 = org.telegram.messenger.FileLoader.getInstance(r39);
        r1 = r13.webFile;
        if (r22 == 0) goto L_0x04c2;
    L_0x04c0:
        r2 = 2;
        goto L_0x04c3;
    L_0x04c2:
        r2 = 1;
    L_0x04c3:
        r0.loadFile(r1, r2, r15);
    L_0x04c6:
        r0 = r26.isForceLoding();
        if (r0 == 0) goto L_0x04ff;
    L_0x04cc:
        r0 = r7.forceLoadingImages;
        r1 = r10.key;
        r2 = java.lang.Integer.valueOf(r8);
        r0.put(r1, r2);
        goto L_0x04ff;
    L_0x04d8:
        r7 = r21;
        r5 = r25;
        r10.finalFilePath = r5;
        r10.imageLocation = r13;
        r0 = new org.telegram.messenger.ImageLoader$CacheOutTask;
        r0.<init>(r10);
        r10.cacheTask = r0;
        r0 = r7.imageLoadingByKeys;
        r0.put(r12, r10);
        if (r22 == 0) goto L_0x04f6;
    L_0x04ee:
        r0 = r7.cacheThumbOutQueue;
        r1 = r10.cacheTask;
        r0.postRunnable(r1);
        goto L_0x04ff;
    L_0x04f6:
        r0 = r7.cacheOutQueue;
        r1 = r10.cacheTask;
        r0.postRunnable(r1);
        goto L_0x04ff;
    L_0x04fe:
        r7 = r0;
    L_0x04ff:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ImageLoader.lambda$createLoadOperationForImageReceiver$5$ImageLoader(int, java.lang.String, java.lang.String, int, org.telegram.messenger.ImageReceiver, java.lang.String, int, int, org.telegram.messenger.ImageLocation, boolean, java.lang.Object, org.telegram.tgnet.TLRPC$Document, boolean, boolean, int, int, java.lang.String, int):void");
    }

    /* JADX WARNING: Removed duplicated region for block: B:140:0x02ac  */
    /* JADX WARNING: Removed duplicated region for block: B:139:0x02a7  */
    /* JADX WARNING: Removed duplicated region for block: B:222:0x02c1 A:{SYNTHETIC} */
    /* JADX WARNING: Removed duplicated region for block: B:142:0x02b2  */
    /* JADX WARNING: Removed duplicated region for block: B:125:0x026c  */
    /* JADX WARNING: Removed duplicated region for block: B:119:0x024e  */
    /* JADX WARNING: Removed duplicated region for block: B:139:0x02a7  */
    /* JADX WARNING: Removed duplicated region for block: B:140:0x02ac  */
    /* JADX WARNING: Removed duplicated region for block: B:222:0x02c1 A:{SYNTHETIC} */
    /* JADX WARNING: Removed duplicated region for block: B:142:0x02b2  */
    /* JADX WARNING: Removed duplicated region for block: B:140:0x02ac  */
    /* JADX WARNING: Removed duplicated region for block: B:139:0x02a7  */
    /* JADX WARNING: Removed duplicated region for block: B:222:0x02c1 A:{SYNTHETIC} */
    /* JADX WARNING: Removed duplicated region for block: B:142:0x02b2  */
    /* JADX WARNING: Removed duplicated region for block: B:139:0x02a7  */
    /* JADX WARNING: Removed duplicated region for block: B:140:0x02ac  */
    /* JADX WARNING: Removed duplicated region for block: B:222:0x02c1 A:{SYNTHETIC} */
    /* JADX WARNING: Removed duplicated region for block: B:142:0x02b2  */
    /* JADX WARNING: Removed duplicated region for block: B:64:0x0115  */
    /* JADX WARNING: Removed duplicated region for block: B:219:0x02cf A:{SYNTHETIC, EDGE_INSN: B:219:0x02cf->B:146:0x02cf ?: BREAK  , EDGE_INSN: B:219:0x02cf->B:146:0x02cf ?: BREAK  } */
    /* JADX WARNING: Removed duplicated region for block: B:68:0x012a  */
    /* JADX WARNING: Removed duplicated region for block: B:164:0x0339  */
    /* JADX WARNING: Removed duplicated region for block: B:148:0x02d8  */
    /* JADX WARNING: Removed duplicated region for block: B:206:0x0426  */
    /* JADX WARNING: Removed duplicated region for block: B:189:0x03c9  */
    /* JADX WARNING: Removed duplicated region for block: B:59:0x0105  */
    /* JADX WARNING: Removed duplicated region for block: B:57:0x00f8  */
    /* JADX WARNING: Removed duplicated region for block: B:64:0x0115  */
    /* JADX WARNING: Removed duplicated region for block: B:68:0x012a  */
    /* JADX WARNING: Removed duplicated region for block: B:219:0x02cf A:{SYNTHETIC, EDGE_INSN: B:219:0x02cf->B:146:0x02cf ?: BREAK  , EDGE_INSN: B:219:0x02cf->B:146:0x02cf ?: BREAK  , EDGE_INSN: B:219:0x02cf->B:146:0x02cf ?: BREAK  } */
    /* JADX WARNING: Removed duplicated region for block: B:148:0x02d8  */
    /* JADX WARNING: Removed duplicated region for block: B:164:0x0339  */
    /* JADX WARNING: Removed duplicated region for block: B:167:0x0342 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:172:0x0360 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:181:0x0394  */
    /* JADX WARNING: Removed duplicated region for block: B:189:0x03c9  */
    /* JADX WARNING: Removed duplicated region for block: B:206:0x0426  */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x009d  */
    /* JADX WARNING: Removed duplicated region for block: B:57:0x00f8  */
    /* JADX WARNING: Removed duplicated region for block: B:59:0x0105  */
    /* JADX WARNING: Removed duplicated region for block: B:64:0x0115  */
    /* JADX WARNING: Removed duplicated region for block: B:219:0x02cf A:{SYNTHETIC, EDGE_INSN: B:219:0x02cf->B:146:0x02cf ?: BREAK  , EDGE_INSN: B:219:0x02cf->B:146:0x02cf ?: BREAK  , EDGE_INSN: B:219:0x02cf->B:146:0x02cf ?: BREAK  , EDGE_INSN: B:219:0x02cf->B:146:0x02cf ?: BREAK  } */
    /* JADX WARNING: Removed duplicated region for block: B:68:0x012a  */
    /* JADX WARNING: Removed duplicated region for block: B:164:0x0339  */
    /* JADX WARNING: Removed duplicated region for block: B:148:0x02d8  */
    /* JADX WARNING: Removed duplicated region for block: B:167:0x0342 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:172:0x0360 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:181:0x0394  */
    /* JADX WARNING: Removed duplicated region for block: B:206:0x0426  */
    /* JADX WARNING: Removed duplicated region for block: B:189:0x03c9  */
    /* JADX WARNING: Removed duplicated region for block: B:28:0x0079  */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x0080  */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x009d  */
    /* JADX WARNING: Removed duplicated region for block: B:59:0x0105  */
    /* JADX WARNING: Removed duplicated region for block: B:57:0x00f8  */
    /* JADX WARNING: Removed duplicated region for block: B:64:0x0115  */
    /* JADX WARNING: Removed duplicated region for block: B:68:0x012a  */
    /* JADX WARNING: Removed duplicated region for block: B:219:0x02cf A:{SYNTHETIC, EDGE_INSN: B:219:0x02cf->B:146:0x02cf ?: BREAK  , EDGE_INSN: B:219:0x02cf->B:146:0x02cf ?: BREAK  , EDGE_INSN: B:219:0x02cf->B:146:0x02cf ?: BREAK  , EDGE_INSN: B:219:0x02cf->B:146:0x02cf ?: BREAK  , EDGE_INSN: B:219:0x02cf->B:146:0x02cf ?: BREAK  } */
    /* JADX WARNING: Removed duplicated region for block: B:148:0x02d8  */
    /* JADX WARNING: Removed duplicated region for block: B:164:0x0339  */
    /* JADX WARNING: Removed duplicated region for block: B:167:0x0342 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:172:0x0360 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:181:0x0394  */
    /* JADX WARNING: Removed duplicated region for block: B:189:0x03c9  */
    /* JADX WARNING: Removed duplicated region for block: B:206:0x0426  */
    public void loadImageForImageReceiver(org.telegram.messenger.ImageReceiver r31) {
        /*
        r30 = this;
        r12 = r30;
        r13 = r31;
        if (r13 != 0) goto L_0x0007;
    L_0x0006:
        return;
    L_0x0007:
        r6 = r31.getMediaKey();
        r14 = r31.getNewGuid();
        r7 = 0;
        r8 = 1;
        if (r6 == 0) goto L_0x004f;
    L_0x0013:
        r0 = r31.getMediaLocation();
        r0 = r0.document;
        r0 = org.telegram.messenger.MessageObject.isAnimatedStickerDocument(r0);
        if (r0 == 0) goto L_0x0028;
    L_0x001f:
        r0 = r12.lottieMemCache;
        r0 = r0.get(r6);
        r0 = (android.graphics.drawable.Drawable) r0;
        goto L_0x0037;
    L_0x0028:
        r0 = r12.memCache;
        r0 = r0.get(r6);
        r0 = (android.graphics.drawable.Drawable) r0;
        if (r0 == 0) goto L_0x0037;
    L_0x0032:
        r1 = r12.memCache;
        r1.moveToFront(r6);
    L_0x0037:
        r1 = r0;
        if (r1 == 0) goto L_0x004f;
    L_0x003a:
        r12.cancelLoadingForImageReceiver(r13, r8);
        r3 = 3;
        r4 = 1;
        r0 = r31;
        r2 = r6;
        r5 = r14;
        r0.setImageBitmapByKey(r1, r2, r3, r4, r5);
        r0 = r31.isForcePreview();
        if (r0 != 0) goto L_0x004d;
    L_0x004c:
        return;
    L_0x004d:
        r0 = 1;
        goto L_0x0050;
    L_0x004f:
        r0 = 0;
    L_0x0050:
        r2 = r31.getImageKey();
        if (r0 != 0) goto L_0x0096;
    L_0x0056:
        if (r2 == 0) goto L_0x0096;
    L_0x0058:
        r1 = r31.getImageLocation();
        if (r1 == 0) goto L_0x006f;
    L_0x005e:
        r1 = r1.document;
        r1 = org.telegram.messenger.MessageObject.isAnimatedStickerDocument(r1);
        if (r1 == 0) goto L_0x006f;
    L_0x0066:
        r1 = r12.lottieMemCache;
        r1 = r1.get(r2);
        r1 = (android.graphics.drawable.Drawable) r1;
        goto L_0x007e;
    L_0x006f:
        r1 = r12.memCache;
        r1 = r1.get(r2);
        r1 = (android.graphics.drawable.Drawable) r1;
        if (r1 == 0) goto L_0x007e;
    L_0x0079:
        r3 = r12.memCache;
        r3.moveToFront(r2);
    L_0x007e:
        if (r1 == 0) goto L_0x0096;
    L_0x0080:
        r12.cancelLoadingForImageReceiver(r13, r8);
        r3 = 0;
        r4 = 1;
        r0 = r31;
        r5 = r14;
        r0.setImageBitmapByKey(r1, r2, r3, r4, r5);
        r0 = r31.isForcePreview();
        if (r0 != 0) goto L_0x0094;
    L_0x0091:
        if (r6 != 0) goto L_0x0094;
    L_0x0093:
        return;
    L_0x0094:
        r15 = 1;
        goto L_0x0097;
    L_0x0096:
        r15 = r0;
    L_0x0097:
        r2 = r31.getThumbKey();
        if (r2 == 0) goto L_0x00c5;
    L_0x009d:
        r0 = r12.memCache;
        r0 = r0.get(r2);
        r1 = r0;
        r1 = (android.graphics.drawable.BitmapDrawable) r1;
        if (r1 == 0) goto L_0x00ad;
    L_0x00a8:
        r0 = r12.memCache;
        r0.moveToFront(r2);
    L_0x00ad:
        if (r1 == 0) goto L_0x00c5;
    L_0x00af:
        r3 = 1;
        r4 = 1;
        r0 = r31;
        r5 = r14;
        r0.setImageBitmapByKey(r1, r2, r3, r4, r5);
        r12.cancelLoadingForImageReceiver(r13, r7);
        if (r15 == 0) goto L_0x00c3;
    L_0x00bc:
        r0 = r31.isForcePreview();
        if (r0 == 0) goto L_0x00c3;
    L_0x00c2:
        return;
    L_0x00c3:
        r0 = 1;
        goto L_0x00c6;
    L_0x00c5:
        r0 = 0;
    L_0x00c6:
        r1 = r31.getParentObject();
        r2 = r31.getQulityThumbDocument();
        r5 = r31.getThumbLocation();
        r6 = r31.getThumbFilter();
        r3 = r31.getMediaLocation();
        r11 = r31.getMediaFilter();
        r4 = r31.getImageLocation();
        r10 = r31.getImageFilter();
        if (r4 != 0) goto L_0x010c;
    L_0x00e8:
        r9 = r31.isNeedsQualityThumb();
        if (r9 == 0) goto L_0x010c;
    L_0x00ee:
        r9 = r31.isCurrentKeyQuality();
        if (r9 == 0) goto L_0x010c;
    L_0x00f4:
        r9 = r1 instanceof org.telegram.messenger.MessageObject;
        if (r9 == 0) goto L_0x0105;
    L_0x00f8:
        r2 = r1;
        r2 = (org.telegram.messenger.MessageObject) r2;
        r2 = r2.getDocument();
        r4 = org.telegram.messenger.ImageLocation.getForDocument(r2);
    L_0x0103:
        r2 = 1;
        goto L_0x010d;
    L_0x0105:
        if (r2 == 0) goto L_0x010c;
    L_0x0107:
        r4 = org.telegram.messenger.ImageLocation.getForDocument(r2);
        goto L_0x0103;
    L_0x010c:
        r2 = 0;
    L_0x010d:
        r9 = r31.getExt();
        r7 = "jpg";
        if (r9 != 0) goto L_0x0116;
    L_0x0115:
        r9 = r7;
    L_0x0116:
        r17 = 0;
        r18 = r3;
        r19 = r17;
        r20 = r19;
        r22 = r20;
        r23 = r22;
        r3 = 0;
        r21 = 0;
    L_0x0125:
        r8 = 2;
        r12 = ".";
        if (r3 >= r8) goto L_0x02cf;
    L_0x012a:
        if (r3 != 0) goto L_0x012e;
    L_0x012c:
        r8 = r4;
        goto L_0x0130;
    L_0x012e:
        r8 = r18;
    L_0x0130:
        if (r8 != 0) goto L_0x0133;
    L_0x0132:
        goto L_0x013f;
    L_0x0133:
        if (r18 == 0) goto L_0x0138;
    L_0x0135:
        r13 = r18;
        goto L_0x0139;
    L_0x0138:
        r13 = r4;
    L_0x0139:
        r13 = r8.getKey(r1, r13);
        if (r13 != 0) goto L_0x0148;
    L_0x013f:
        r27 = r10;
        r26 = r11;
        r24 = r15;
        r15 = 1;
        goto L_0x02c1;
    L_0x0148:
        r24 = r15;
        r15 = r8.path;
        if (r15 == 0) goto L_0x016e;
    L_0x014e:
        r15 = new java.lang.StringBuilder;
        r15.<init>();
        r15.append(r13);
        r15.append(r12);
        r12 = r8.path;
        r12 = getHttpUrlExtension(r12, r7);
        r15.append(r12);
        r12 = r15.toString();
    L_0x0166:
        r27 = r10;
        r26 = r11;
        r10 = r12;
    L_0x016b:
        r15 = 1;
        goto L_0x02a5;
    L_0x016e:
        r15 = r8.photoSize;
        r15 = r15 instanceof org.telegram.tgnet.TLRPC.TL_photoStrippedSize;
        if (r15 == 0) goto L_0x0187;
    L_0x0174:
        r15 = new java.lang.StringBuilder;
        r15.<init>();
        r15.append(r13);
        r15.append(r12);
        r15.append(r9);
        r12 = r15.toString();
        goto L_0x0166;
    L_0x0187:
        r15 = r8.location;
        if (r15 == 0) goto L_0x01d2;
    L_0x018b:
        r15 = new java.lang.StringBuilder;
        r15.<init>();
        r15.append(r13);
        r15.append(r12);
        r15.append(r9);
        r12 = r15.toString();
        r15 = r31.getExt();
        if (r15 != 0) goto L_0x01c5;
    L_0x01a3:
        r15 = r8.location;
        r25 = r12;
        r12 = r15.key;
        if (r12 != 0) goto L_0x01c0;
    L_0x01ab:
        r27 = r10;
        r26 = r11;
        r10 = r15.volume_id;
        r28 = -NUM; // 0xfffffffvar_ float:-0.0 double:NaN;
        r12 = (r10 > r28 ? 1 : (r10 == r28 ? 0 : -1));
        if (r12 != 0) goto L_0x01bd;
    L_0x01b8:
        r10 = r15.local_id;
        if (r10 >= 0) goto L_0x01bd;
    L_0x01bc:
        goto L_0x01cb;
    L_0x01bd:
        r10 = r25;
        goto L_0x016b;
    L_0x01c0:
        r27 = r10;
        r26 = r11;
        goto L_0x01cb;
    L_0x01c5:
        r27 = r10;
        r26 = r11;
        r25 = r12;
    L_0x01cb:
        r10 = r25;
        r15 = 1;
        r21 = 1;
        goto L_0x02a5;
    L_0x01d2:
        r27 = r10;
        r26 = r11;
        r10 = r8.webFile;
        if (r10 == 0) goto L_0x01fc;
    L_0x01da:
        r10 = r10.mime_type;
        r10 = org.telegram.messenger.FileLoader.getMimeTypePart(r10);
        r11 = new java.lang.StringBuilder;
        r11.<init>();
        r11.append(r13);
        r11.append(r12);
        r12 = r8.webFile;
        r12 = r12.url;
        r10 = getHttpUrlExtension(r12, r10);
        r11.append(r10);
        r10 = r11.toString();
        goto L_0x016b;
    L_0x01fc:
        r10 = r8.secureDocument;
        if (r10 == 0) goto L_0x0214;
    L_0x0200:
        r10 = new java.lang.StringBuilder;
        r10.<init>();
        r10.append(r13);
        r10.append(r12);
        r10.append(r9);
        r10 = r10.toString();
        goto L_0x016b;
    L_0x0214:
        r10 = r8.document;
        if (r10 == 0) goto L_0x02a2;
    L_0x0218:
        if (r3 != 0) goto L_0x022d;
    L_0x021a:
        if (r2 == 0) goto L_0x022d;
    L_0x021c:
        r10 = new java.lang.StringBuilder;
        r10.<init>();
        r11 = "q_";
        r10.append(r11);
        r10.append(r13);
        r13 = r10.toString();
    L_0x022d:
        r10 = r8.document;
        r10 = org.telegram.messenger.FileLoader.getDocumentFileName(r10);
        r11 = "";
        if (r10 == 0) goto L_0x0246;
    L_0x0237:
        r12 = 46;
        r12 = r10.lastIndexOf(r12);
        r15 = -1;
        if (r12 != r15) goto L_0x0241;
    L_0x0240:
        goto L_0x0246;
    L_0x0241:
        r10 = r10.substring(r12);
        goto L_0x0247;
    L_0x0246:
        r10 = r11;
    L_0x0247:
        r12 = r10.length();
        r15 = 1;
        if (r12 > r15) goto L_0x026c;
    L_0x024e:
        r10 = r8.document;
        r10 = r10.mime_type;
        r12 = "video/mp4";
        r10 = r12.equals(r10);
        if (r10 == 0) goto L_0x025d;
    L_0x025a:
        r11 = ".mp4";
        goto L_0x026d;
    L_0x025d:
        r10 = r8.document;
        r10 = r10.mime_type;
        r12 = "video/x-matroska";
        r10 = r12.equals(r10);
        if (r10 == 0) goto L_0x026d;
    L_0x0269:
        r11 = ".mkv";
        goto L_0x026d;
    L_0x026c:
        r11 = r10;
    L_0x026d:
        r10 = new java.lang.StringBuilder;
        r10.<init>();
        r10.append(r13);
        r10.append(r11);
        r10 = r10.toString();
        r11 = r8.document;
        r11 = org.telegram.messenger.MessageObject.isVideoDocument(r11);
        if (r11 != 0) goto L_0x029e;
    L_0x0284:
        r11 = r8.document;
        r11 = org.telegram.messenger.MessageObject.isGifDocument(r11);
        if (r11 != 0) goto L_0x029e;
    L_0x028c:
        r11 = r8.document;
        r11 = org.telegram.messenger.MessageObject.isRoundVideoDocument(r11);
        if (r11 != 0) goto L_0x029e;
    L_0x0294:
        r11 = r8.document;
        r11 = org.telegram.messenger.MessageObject.canPreviewDocument(r11);
        if (r11 != 0) goto L_0x029e;
    L_0x029c:
        r11 = 1;
        goto L_0x029f;
    L_0x029e:
        r11 = 0;
    L_0x029f:
        r21 = r11;
        goto L_0x02a5;
    L_0x02a2:
        r15 = 1;
        r10 = r17;
    L_0x02a5:
        if (r3 != 0) goto L_0x02ac;
    L_0x02a7:
        r22 = r10;
        r20 = r13;
        goto L_0x02b0;
    L_0x02ac:
        r23 = r10;
        r19 = r13;
    L_0x02b0:
        if (r8 != r5) goto L_0x02c1;
    L_0x02b2:
        if (r3 != 0) goto L_0x02bb;
    L_0x02b4:
        r4 = r17;
        r20 = r4;
        r22 = r20;
        goto L_0x02c1;
    L_0x02bb:
        r18 = r17;
        r19 = r18;
        r23 = r19;
    L_0x02c1:
        r3 = r3 + 1;
        r12 = r30;
        r13 = r31;
        r15 = r24;
        r11 = r26;
        r10 = r27;
        goto L_0x0125;
    L_0x02cf:
        r27 = r10;
        r26 = r11;
        r24 = r15;
        r15 = 1;
        if (r5 == 0) goto L_0x0339;
    L_0x02d8:
        r2 = r31.getStrippedLocation();
        if (r2 != 0) goto L_0x02e4;
    L_0x02de:
        if (r18 == 0) goto L_0x02e3;
    L_0x02e0:
        r2 = r18;
        goto L_0x02e4;
    L_0x02e3:
        r2 = r4;
    L_0x02e4:
        r1 = r5.getKey(r1, r2);
        r2 = r5.path;
        if (r2 == 0) goto L_0x0306;
    L_0x02ec:
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r2.append(r1);
        r2.append(r12);
        r3 = r5.path;
        r3 = getHttpUrlExtension(r3, r7);
        r2.append(r3);
        r2 = r2.toString();
    L_0x0304:
        r3 = r2;
        goto L_0x033c;
    L_0x0306:
        r2 = r5.photoSize;
        r2 = r2 instanceof org.telegram.tgnet.TLRPC.TL_photoStrippedSize;
        if (r2 == 0) goto L_0x031f;
    L_0x030c:
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r2.append(r1);
        r2.append(r12);
        r2.append(r9);
        r2 = r2.toString();
        goto L_0x0304;
    L_0x031f:
        r2 = r5.location;
        if (r2 == 0) goto L_0x0336;
    L_0x0323:
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r2.append(r1);
        r2.append(r12);
        r2.append(r9);
        r2 = r2.toString();
        goto L_0x0304;
    L_0x0336:
        r3 = r17;
        goto L_0x033c;
    L_0x0339:
        r1 = r17;
        r3 = r1;
    L_0x033c:
        r2 = "@";
        r7 = r19;
        if (r7 == 0) goto L_0x0359;
    L_0x0342:
        if (r26 == 0) goto L_0x0359;
    L_0x0344:
        r10 = new java.lang.StringBuilder;
        r10.<init>();
        r10.append(r7);
        r10.append(r2);
        r11 = r26;
        r10.append(r11);
        r7 = r10.toString();
        goto L_0x035b;
    L_0x0359:
        r11 = r26;
    L_0x035b:
        r12 = r7;
        r7 = r20;
        if (r7 == 0) goto L_0x0377;
    L_0x0360:
        if (r27 == 0) goto L_0x0377;
    L_0x0362:
        r10 = new java.lang.StringBuilder;
        r10.<init>();
        r10.append(r7);
        r10.append(r2);
        r13 = r27;
        r10.append(r13);
        r7 = r10.toString();
        goto L_0x0379;
    L_0x0377:
        r13 = r27;
    L_0x0379:
        r16 = r7;
        if (r1 == 0) goto L_0x0391;
    L_0x037d:
        if (r6 == 0) goto L_0x0391;
    L_0x037f:
        r7 = new java.lang.StringBuilder;
        r7.<init>();
        r7.append(r1);
        r7.append(r2);
        r7.append(r6);
        r1 = r7.toString();
    L_0x0391:
        r2 = r1;
        if (r4 == 0) goto L_0x03c3;
    L_0x0394:
        r1 = r4.path;
        if (r1 == 0) goto L_0x03c3;
    L_0x0398:
        r7 = 0;
        r10 = 1;
        r11 = 1;
        if (r0 == 0) goto L_0x039e;
    L_0x039d:
        r15 = 2;
    L_0x039e:
        r0 = r30;
        r1 = r31;
        r17 = r4;
        r4 = r9;
        r8 = r10;
        r19 = r9;
        r9 = r11;
        r10 = r15;
        r11 = r14;
        r0.createLoadOperationForImageReceiver(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r11);
        r7 = r31.getSize();
        r8 = 1;
        r9 = 0;
        r10 = 0;
        r2 = r16;
        r3 = r22;
        r4 = r19;
        r5 = r17;
        r6 = r13;
        r0.createLoadOperationForImageReceiver(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r11);
        goto L_0x0459;
    L_0x03c3:
        r17 = r4;
        r19 = r9;
        if (r18 == 0) goto L_0x0426;
    L_0x03c9:
        r1 = r31.getCacheType();
        r20 = 1;
        if (r1 != 0) goto L_0x03d6;
    L_0x03d1:
        if (r21 == 0) goto L_0x03d6;
    L_0x03d3:
        r21 = 1;
        goto L_0x03d8;
    L_0x03d6:
        r21 = r1;
    L_0x03d8:
        if (r21 != 0) goto L_0x03dc;
    L_0x03da:
        r9 = 1;
        goto L_0x03de;
    L_0x03dc:
        r9 = r21;
    L_0x03de:
        if (r0 != 0) goto L_0x03f4;
    L_0x03e0:
        r7 = 0;
        r10 = 1;
        if (r0 == 0) goto L_0x03e5;
    L_0x03e4:
        r15 = 2;
    L_0x03e5:
        r0 = r30;
        r1 = r31;
        r4 = r19;
        r8 = r9;
        r9 = r10;
        r10 = r15;
        r15 = r11;
        r11 = r14;
        r0.createLoadOperationForImageReceiver(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r11);
        goto L_0x03f5;
    L_0x03f4:
        r15 = r11;
    L_0x03f5:
        if (r24 != 0) goto L_0x040d;
    L_0x03f7:
        r7 = 0;
        r9 = 0;
        r10 = 0;
        r0 = r30;
        r1 = r31;
        r2 = r16;
        r3 = r22;
        r4 = r19;
        r5 = r17;
        r6 = r13;
        r8 = r20;
        r11 = r14;
        r0.createLoadOperationForImageReceiver(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r11);
    L_0x040d:
        r7 = r31.getSize();
        r9 = 3;
        r10 = 0;
        r0 = r30;
        r1 = r31;
        r2 = r12;
        r3 = r23;
        r4 = r19;
        r5 = r18;
        r6 = r15;
        r8 = r21;
        r11 = r14;
        r0.createLoadOperationForImageReceiver(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r11);
        goto L_0x0459;
    L_0x0426:
        r1 = r31.getCacheType();
        if (r1 != 0) goto L_0x0430;
    L_0x042c:
        if (r21 == 0) goto L_0x0430;
    L_0x042e:
        r12 = 1;
        goto L_0x0431;
    L_0x0430:
        r12 = r1;
    L_0x0431:
        if (r12 != 0) goto L_0x0435;
    L_0x0433:
        r9 = 1;
        goto L_0x0436;
    L_0x0435:
        r9 = r12;
    L_0x0436:
        r7 = 0;
        r10 = 1;
        if (r0 == 0) goto L_0x043b;
    L_0x043a:
        r15 = 2;
    L_0x043b:
        r0 = r30;
        r1 = r31;
        r4 = r19;
        r8 = r9;
        r9 = r10;
        r10 = r15;
        r11 = r14;
        r0.createLoadOperationForImageReceiver(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r11);
        r7 = r31.getSize();
        r9 = 0;
        r10 = 0;
        r2 = r16;
        r3 = r22;
        r5 = r17;
        r6 = r13;
        r8 = r12;
        r0.createLoadOperationForImageReceiver(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r11);
    L_0x0459:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ImageLoader.loadImageForImageReceiver(org.telegram.messenger.ImageReceiver):void");
    }

    private void httpFileLoadError(String str) {
        this.imageLoadQueue.postRunnable(new -$$Lambda$ImageLoader$ZaOfz0BNqcCgsH2qEkswBKN9Vb0(this, str));
    }

    public /* synthetic */ void lambda$httpFileLoadError$6$ImageLoader(String str) {
        CacheImage cacheImage = (CacheImage) this.imageLoadingByUrl.get(str);
        if (cacheImage != null) {
            HttpImageTask httpImageTask = cacheImage.httpTask;
            cacheImage.httpTask = new HttpImageTask(httpImageTask.cacheImage, httpImageTask.imageSize);
            this.httpTasks.add(cacheImage.httpTask);
            runHttpTasks(false);
        }
    }

    private void artworkLoadError(String str) {
        this.imageLoadQueue.postRunnable(new -$$Lambda$ImageLoader$aEOKyJRqeuDWeruDQhba5rQv8xo(this, str));
    }

    public /* synthetic */ void lambda$artworkLoadError$7$ImageLoader(String str) {
        CacheImage cacheImage = (CacheImage) this.imageLoadingByUrl.get(str);
        if (cacheImage != null) {
            cacheImage.artworkTask = new ArtworkLoadTask(cacheImage.artworkTask.cacheImage);
            this.artworkTasks.add(cacheImage.artworkTask);
            runArtworkTasks(false);
        }
    }

    private void fileDidLoaded(String str, File file, int i) {
        this.imageLoadQueue.postRunnable(new -$$Lambda$ImageLoader$k74dlCvVooO9jw6g30Qy8tUSaQg(this, str, i, file));
    }

    public /* synthetic */ void lambda$fileDidLoaded$8$ImageLoader(String str, int i, File file) {
        ThumbGenerateInfo thumbGenerateInfo = (ThumbGenerateInfo) this.waitingForQualityThumb.get(str);
        if (!(thumbGenerateInfo == null || thumbGenerateInfo.parentDocument == null)) {
            generateThumb(i, file, thumbGenerateInfo);
            this.waitingForQualityThumb.remove(str);
        }
        CacheImage cacheImage = (CacheImage) this.imageLoadingByUrl.get(str);
        if (cacheImage != null) {
            this.imageLoadingByUrl.remove(str);
            ArrayList arrayList = new ArrayList();
            for (int i2 = 0; i2 < cacheImage.imageReceiverArray.size(); i2++) {
                String str2 = (String) cacheImage.keys.get(i2);
                String str3 = (String) cacheImage.filters.get(i2);
                int intValue = ((Integer) cacheImage.imageTypes.get(i2)).intValue();
                ImageReceiver imageReceiver = (ImageReceiver) cacheImage.imageReceiverArray.get(i2);
                int intValue2 = ((Integer) cacheImage.imageReceiverGuidsArray.get(i2)).intValue();
                CacheImage cacheImage2 = (CacheImage) this.imageLoadingByKeys.get(str2);
                if (cacheImage2 == null) {
                    cacheImage2 = new CacheImage(this, null);
                    cacheImage2.secureDocument = cacheImage.secureDocument;
                    cacheImage2.currentAccount = cacheImage.currentAccount;
                    cacheImage2.finalFilePath = file;
                    cacheImage2.key = str2;
                    cacheImage2.imageLocation = cacheImage.imageLocation;
                    cacheImage2.imageType = intValue;
                    cacheImage2.ext = cacheImage.ext;
                    cacheImage2.encryptionKeyPath = cacheImage.encryptionKeyPath;
                    cacheImage2.cacheTask = new CacheOutTask(cacheImage2);
                    cacheImage2.filter = str3;
                    cacheImage2.animatedFile = cacheImage.animatedFile;
                    cacheImage2.lottieFile = cacheImage.lottieFile;
                    this.imageLoadingByKeys.put(str2, cacheImage2);
                    arrayList.add(cacheImage2.cacheTask);
                }
                cacheImage2.addImageReceiver(imageReceiver, str2, str3, intValue, intValue2);
            }
            for (int i3 = 0; i3 < arrayList.size(); i3++) {
                CacheOutTask cacheOutTask = (CacheOutTask) arrayList.get(i3);
                if (cacheOutTask.cacheImage.imageType == 1) {
                    this.cacheThumbOutQueue.postRunnable(cacheOutTask);
                } else {
                    this.cacheOutQueue.postRunnable(cacheOutTask);
                }
            }
        }
    }

    private void fileDidFailedLoad(String str, int i) {
        if (i != 1) {
            this.imageLoadQueue.postRunnable(new -$$Lambda$ImageLoader$oYsbNqws1vmTTbWlpv4MDvOVi0o(this, str));
        }
    }

    public /* synthetic */ void lambda$fileDidFailedLoad$9$ImageLoader(String str) {
        CacheImage cacheImage = (CacheImage) this.imageLoadingByUrl.get(str);
        if (cacheImage != null) {
            cacheImage.setImageAndClear(null, null);
        }
    }

    private void runHttpTasks(boolean z) {
        if (z) {
            this.currentHttpTasksCount--;
        }
        while (this.currentHttpTasksCount < 4 && !this.httpTasks.isEmpty()) {
            HttpImageTask httpImageTask = (HttpImageTask) this.httpTasks.poll();
            if (httpImageTask != null) {
                httpImageTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                this.currentHttpTasksCount++;
            }
        }
    }

    private void runArtworkTasks(boolean z) {
        if (z) {
            this.currentArtworkTasksCount--;
        }
        while (this.currentArtworkTasksCount < 4 && !this.artworkTasks.isEmpty()) {
            try {
                ((ArtworkLoadTask) this.artworkTasks.poll()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
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
        str2 = getHttpUrlExtension(str, str2);
        File directory = FileLoader.getDirectory(4);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(Utilities.MD5(str));
        stringBuilder.append(".");
        stringBuilder.append(str2);
        return new File(directory, stringBuilder.toString());
    }

    public void loadHttpFile(String str, String str2, int i) {
        if (str != null && str.length() != 0 && !this.httpFileLoadTasksByKeys.containsKey(str)) {
            String httpUrlExtension = getHttpUrlExtension(str, str2);
            File directory = FileLoader.getDirectory(4);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(Utilities.MD5(str));
            stringBuilder.append("_temp.");
            stringBuilder.append(httpUrlExtension);
            File file = new File(directory, stringBuilder.toString());
            file.delete();
            HttpFileTask httpFileTask = new HttpFileTask(str, file, httpUrlExtension, i);
            this.httpFileLoadTasks.add(httpFileTask);
            this.httpFileLoadTasksByKeys.put(str, httpFileTask);
            runHttpFileLoadTasks(null, 0);
        }
    }

    public void cancelLoadHttpFile(String str) {
        HttpFileTask httpFileTask = (HttpFileTask) this.httpFileLoadTasksByKeys.get(str);
        if (httpFileTask != null) {
            httpFileTask.cancel(true);
            this.httpFileLoadTasksByKeys.remove(str);
            this.httpFileLoadTasks.remove(httpFileTask);
        }
        Runnable runnable = (Runnable) this.retryHttpsTasks.get(str);
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
        }
        runHttpFileLoadTasks(null, 0);
    }

    private void runHttpFileLoadTasks(HttpFileTask httpFileTask, int i) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$ImageLoader$X9CLASSNAMEkpfS01SJNOymJYOCf_gN1g(this, httpFileTask, i));
    }

    public /* synthetic */ void lambda$runHttpFileLoadTasks$11$ImageLoader(HttpFileTask httpFileTask, int i) {
        if (httpFileTask != null) {
            this.currentHttpFileLoadTasksCount--;
        }
        if (httpFileTask != null) {
            if (i == 1) {
                if (httpFileTask.canRetry) {
                    -$$Lambda$ImageLoader$NIWBUvKKa0U_8_0TQJUIDKfrzig -__lambda_imageloader_niwbuvkka0u_8_0tqjuidkfrzig = new -$$Lambda$ImageLoader$NIWBUvKKa0U_8_0TQJUIDKfrzig(this, new HttpFileTask(httpFileTask.url, httpFileTask.tempFile, httpFileTask.ext, httpFileTask.currentAccount));
                    this.retryHttpsTasks.put(httpFileTask.url, -__lambda_imageloader_niwbuvkka0u_8_0tqjuidkfrzig);
                    AndroidUtilities.runOnUIThread(-__lambda_imageloader_niwbuvkka0u_8_0tqjuidkfrzig, 1000);
                } else {
                    this.httpFileLoadTasksByKeys.remove(httpFileTask.url);
                    NotificationCenter.getInstance(httpFileTask.currentAccount).postNotificationName(NotificationCenter.httpFileDidFailedLoad, httpFileTask.url, Integer.valueOf(0));
                }
            } else if (i == 2) {
                this.httpFileLoadTasksByKeys.remove(httpFileTask.url);
                File directory = FileLoader.getDirectory(4);
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(Utilities.MD5(httpFileTask.url));
                stringBuilder.append(".");
                stringBuilder.append(httpFileTask.ext);
                File file = new File(directory, stringBuilder.toString());
                if (!httpFileTask.tempFile.renameTo(file)) {
                    file = httpFileTask.tempFile;
                }
                String file2 = file.toString();
                NotificationCenter.getInstance(httpFileTask.currentAccount).postNotificationName(NotificationCenter.httpFileDidLoad, httpFileTask.url, file2);
            }
        }
        while (this.currentHttpFileLoadTasksCount < 2 && !this.httpFileLoadTasks.isEmpty()) {
            ((HttpFileTask) this.httpFileLoadTasks.poll()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
            this.currentHttpFileLoadTasksCount++;
        }
    }

    public /* synthetic */ void lambda$null$10$ImageLoader(HttpFileTask httpFileTask) {
        this.httpFileLoadTasks.add(httpFileTask);
        runHttpFileLoadTasks(null, 0);
    }

    public static boolean shouldSendImageAsDocument(String str, Uri uri) {
        Options options = new Options();
        boolean z = true;
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
                BitmapFactory.decodeStream(openInputStream, null, options);
                openInputStream.close();
            } catch (Throwable th2) {
                FileLog.e(th2);
                return false;
            }
        }
        float f = (float) options.outWidth;
        float f2 = (float) options.outHeight;
        if (f / f2 <= 10.0f && f2 / f <= 10.0f) {
            z = false;
        }
        return z;
    }

    /* JADX WARNING: Removed duplicated region for block: B:95:0x013c  */
    /* JADX WARNING: Removed duplicated region for block: B:63:0x00d3 A:{SYNTHETIC, Splitter:B:63:0x00d3} */
    /* JADX WARNING: Removed duplicated region for block: B:25:0x0065  */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x005e  */
    /* JADX WARNING: Removed duplicated region for block: B:28:0x0071  */
    /* JADX WARNING: Removed duplicated region for block: B:31:0x007f  */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x0090  */
    /* JADX WARNING: Removed duplicated region for block: B:42:0x0097  */
    /* JADX WARNING: Removed duplicated region for block: B:41:0x0095  */
    /* JADX WARNING: Removed duplicated region for block: B:46:0x00a1 A:{SYNTHETIC, Splitter:B:46:0x00a1} */
    /* JADX WARNING: Removed duplicated region for block: B:63:0x00d3 A:{SYNTHETIC, Splitter:B:63:0x00d3} */
    /* JADX WARNING: Removed duplicated region for block: B:95:0x013c  */
    /* JADX WARNING: Removed duplicated region for block: B:80:0x010c A:{SYNTHETIC, Splitter:B:80:0x010c} */
    /* JADX WARNING: Removed duplicated region for block: B:89:0x011e A:{Catch:{ Throwable -> 0x011a }} */
    public static android.graphics.Bitmap loadBitmap(java.lang.String r11, android.net.Uri r12, float r13, float r14, boolean r15) {
        /*
        r0 = new android.graphics.BitmapFactory$Options;
        r0.<init>();
        r1 = 1;
        r0.inJustDecodeBounds = r1;
        if (r11 != 0) goto L_0x002c;
    L_0x000a:
        if (r12 == 0) goto L_0x002c;
    L_0x000c:
        r2 = r12.getScheme();
        if (r2 == 0) goto L_0x002c;
    L_0x0012:
        r2 = r12.getScheme();
        r3 = "file";
        r2 = r2.contains(r3);
        if (r2 == 0) goto L_0x0023;
    L_0x001e:
        r11 = r12.getPath();
        goto L_0x002c;
    L_0x0023:
        r11 = org.telegram.messenger.AndroidUtilities.getPath(r12);	 Catch:{ Throwable -> 0x0028 }
        goto L_0x002c;
    L_0x0028:
        r2 = move-exception;
        org.telegram.messenger.FileLog.e(r2);
    L_0x002c:
        r2 = 0;
        if (r11 == 0) goto L_0x0033;
    L_0x002f:
        android.graphics.BitmapFactory.decodeFile(r11, r0);
        goto L_0x0055;
    L_0x0033:
        if (r12 == 0) goto L_0x0055;
    L_0x0035:
        r3 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Throwable -> 0x0050 }
        r3 = r3.getContentResolver();	 Catch:{ Throwable -> 0x0050 }
        r3 = r3.openInputStream(r12);	 Catch:{ Throwable -> 0x0050 }
        android.graphics.BitmapFactory.decodeStream(r3, r2, r0);	 Catch:{ Throwable -> 0x0050 }
        r3.close();	 Catch:{ Throwable -> 0x0050 }
        r3 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Throwable -> 0x0050 }
        r3 = r3.getContentResolver();	 Catch:{ Throwable -> 0x0050 }
        r3 = r3.openInputStream(r12);	 Catch:{ Throwable -> 0x0050 }
        goto L_0x0056;
    L_0x0050:
        r11 = move-exception;
        org.telegram.messenger.FileLog.e(r11);
        return r2;
    L_0x0055:
        r3 = r2;
    L_0x0056:
        r4 = r0.outWidth;
        r4 = (float) r4;
        r5 = r0.outHeight;
        r5 = (float) r5;
        if (r15 == 0) goto L_0x0065;
    L_0x005e:
        r4 = r4 / r13;
        r5 = r5 / r14;
        r13 = java.lang.Math.max(r4, r5);
        goto L_0x006b;
    L_0x0065:
        r4 = r4 / r13;
        r5 = r5 / r14;
        r13 = java.lang.Math.min(r4, r5);
    L_0x006b:
        r14 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r15 = (r13 > r14 ? 1 : (r13 == r14 ? 0 : -1));
        if (r15 >= 0) goto L_0x0073;
    L_0x0071:
        r13 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
    L_0x0073:
        r14 = 0;
        r0.inJustDecodeBounds = r14;
        r13 = (int) r13;
        r0.inSampleSize = r13;
        r13 = r0.inSampleSize;
        r13 = r13 % 2;
        if (r13 == 0) goto L_0x008a;
    L_0x007f:
        r13 = 1;
    L_0x0080:
        r15 = r13 * 2;
        r4 = r0.inSampleSize;
        if (r15 >= r4) goto L_0x0088;
    L_0x0086:
        r13 = r15;
        goto L_0x0080;
    L_0x0088:
        r0.inSampleSize = r13;
    L_0x008a:
        r13 = android.os.Build.VERSION.SDK_INT;
        r15 = 21;
        if (r13 >= r15) goto L_0x0091;
    L_0x0090:
        r14 = 1;
    L_0x0091:
        r0.inPurgeable = r14;
        if (r11 == 0) goto L_0x0097;
    L_0x0095:
        r13 = r11;
        goto L_0x009f;
    L_0x0097:
        if (r12 == 0) goto L_0x009e;
    L_0x0099:
        r13 = org.telegram.messenger.AndroidUtilities.getPath(r12);
        goto L_0x009f;
    L_0x009e:
        r13 = r2;
    L_0x009f:
        if (r13 == 0) goto L_0x00d0;
    L_0x00a1:
        r14 = new androidx.exifinterface.media.ExifInterface;	 Catch:{ Throwable -> 0x00d0 }
        r14.<init>(r13);	 Catch:{ Throwable -> 0x00d0 }
        r13 = "Orientation";
        r13 = r14.getAttributeInt(r13, r1);	 Catch:{ Throwable -> 0x00d0 }
        r14 = new android.graphics.Matrix;	 Catch:{ Throwable -> 0x00d0 }
        r14.<init>();	 Catch:{ Throwable -> 0x00d0 }
        r15 = 3;
        if (r13 == r15) goto L_0x00c8;
    L_0x00b4:
        r15 = 6;
        if (r13 == r15) goto L_0x00c2;
    L_0x00b7:
        r15 = 8;
        if (r13 == r15) goto L_0x00bc;
    L_0x00bb:
        goto L_0x00d1;
    L_0x00bc:
        r13 = NUM; // 0x43870000 float:270.0 double:5.597372625E-315;
        r14.postRotate(r13);	 Catch:{ Throwable -> 0x00ce }
        goto L_0x00d1;
    L_0x00c2:
        r13 = NUM; // 0x42b40000 float:90.0 double:5.529052754E-315;
        r14.postRotate(r13);	 Catch:{ Throwable -> 0x00ce }
        goto L_0x00d1;
    L_0x00c8:
        r13 = NUM; // 0x43340000 float:180.0 double:5.570497984E-315;
        r14.postRotate(r13);	 Catch:{ Throwable -> 0x00ce }
        goto L_0x00d1;
        goto L_0x00d1;
    L_0x00d0:
        r14 = r2;
    L_0x00d1:
        if (r11 == 0) goto L_0x013c;
    L_0x00d3:
        r12 = android.graphics.BitmapFactory.decodeFile(r11, r0);	 Catch:{ Throwable -> 0x00fe }
        if (r12 == 0) goto L_0x00fb;
    L_0x00d9:
        r13 = r0.inPurgeable;	 Catch:{ Throwable -> 0x00f9 }
        if (r13 == 0) goto L_0x00e0;
    L_0x00dd:
        org.telegram.messenger.Utilities.pinBitmap(r12);	 Catch:{ Throwable -> 0x00f9 }
    L_0x00e0:
        r5 = 0;
        r6 = 0;
        r7 = r12.getWidth();	 Catch:{ Throwable -> 0x00f9 }
        r8 = r12.getHeight();	 Catch:{ Throwable -> 0x00f9 }
        r10 = 1;
        r4 = r12;
        r9 = r14;
        r13 = org.telegram.messenger.Bitmaps.createBitmap(r4, r5, r6, r7, r8, r9, r10);	 Catch:{ Throwable -> 0x00f9 }
        if (r13 == r12) goto L_0x00fb;
    L_0x00f3:
        r12.recycle();	 Catch:{ Throwable -> 0x00f9 }
        r2 = r13;
        goto L_0x0183;
    L_0x00f9:
        r13 = move-exception;
        goto L_0x0100;
    L_0x00fb:
        r2 = r12;
        goto L_0x0183;
    L_0x00fe:
        r13 = move-exception;
        r12 = r2;
    L_0x0100:
        org.telegram.messenger.FileLog.e(r13);
        r13 = getInstance();
        r13.clearMemory();
        if (r12 != 0) goto L_0x011c;
    L_0x010c:
        r12 = android.graphics.BitmapFactory.decodeFile(r11, r0);	 Catch:{ Throwable -> 0x011a }
        if (r12 == 0) goto L_0x011c;
    L_0x0112:
        r11 = r0.inPurgeable;	 Catch:{ Throwable -> 0x011a }
        if (r11 == 0) goto L_0x011c;
    L_0x0116:
        org.telegram.messenger.Utilities.pinBitmap(r12);	 Catch:{ Throwable -> 0x011a }
        goto L_0x011c;
    L_0x011a:
        r11 = move-exception;
        goto L_0x0135;
    L_0x011c:
        if (r12 == 0) goto L_0x0139;
    L_0x011e:
        r5 = 0;
        r6 = 0;
        r7 = r12.getWidth();	 Catch:{ Throwable -> 0x011a }
        r8 = r12.getHeight();	 Catch:{ Throwable -> 0x011a }
        r10 = 1;
        r4 = r12;
        r9 = r14;
        r11 = org.telegram.messenger.Bitmaps.createBitmap(r4, r5, r6, r7, r8, r9, r10);	 Catch:{ Throwable -> 0x011a }
        if (r11 == r12) goto L_0x0139;
    L_0x0131:
        r12.recycle();	 Catch:{ Throwable -> 0x011a }
        goto L_0x013a;
    L_0x0135:
        org.telegram.messenger.FileLog.e(r11);
        goto L_0x00fb;
    L_0x0139:
        r11 = r12;
    L_0x013a:
        r2 = r11;
        goto L_0x0183;
    L_0x013c:
        if (r12 == 0) goto L_0x0183;
    L_0x013e:
        r11 = android.graphics.BitmapFactory.decodeStream(r3, r2, r0);	 Catch:{ Throwable -> 0x0172 }
        if (r11 == 0) goto L_0x0166;
    L_0x0144:
        r12 = r0.inPurgeable;	 Catch:{ Throwable -> 0x0163 }
        if (r12 == 0) goto L_0x014b;
    L_0x0148:
        org.telegram.messenger.Utilities.pinBitmap(r11);	 Catch:{ Throwable -> 0x0163 }
    L_0x014b:
        r5 = 0;
        r6 = 0;
        r7 = r11.getWidth();	 Catch:{ Throwable -> 0x0163 }
        r8 = r11.getHeight();	 Catch:{ Throwable -> 0x0163 }
        r10 = 1;
        r4 = r11;
        r9 = r14;
        r12 = org.telegram.messenger.Bitmaps.createBitmap(r4, r5, r6, r7, r8, r9, r10);	 Catch:{ Throwable -> 0x0163 }
        if (r12 == r11) goto L_0x0166;
    L_0x015e:
        r11.recycle();	 Catch:{ Throwable -> 0x0163 }
        r2 = r12;
        goto L_0x0167;
    L_0x0163:
        r12 = move-exception;
        r2 = r11;
        goto L_0x0173;
    L_0x0166:
        r2 = r11;
    L_0x0167:
        r3.close();	 Catch:{ Throwable -> 0x016b }
        goto L_0x0183;
    L_0x016b:
        r11 = move-exception;
        org.telegram.messenger.FileLog.e(r11);
        goto L_0x0183;
    L_0x0170:
        r11 = move-exception;
        goto L_0x017a;
    L_0x0172:
        r12 = move-exception;
    L_0x0173:
        org.telegram.messenger.FileLog.e(r12);	 Catch:{ all -> 0x0170 }
        r3.close();	 Catch:{ Throwable -> 0x016b }
        goto L_0x0183;
    L_0x017a:
        r3.close();	 Catch:{ Throwable -> 0x017e }
        goto L_0x0182;
    L_0x017e:
        r12 = move-exception;
        org.telegram.messenger.FileLog.e(r12);
    L_0x0182:
        throw r11;
    L_0x0183:
        return r2;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ImageLoader.loadBitmap(java.lang.String, android.net.Uri, float, float, boolean):android.graphics.Bitmap");
    }

    public static void fillPhotoSizeWithBytes(PhotoSize photoSize) {
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

    /* JADX WARNING: Removed duplicated region for block: B:33:0x00ae  */
    /* JADX WARNING: Removed duplicated region for block: B:37:0x00db  */
    /* JADX WARNING: Removed duplicated region for block: B:36:0x00c2  */
    /* JADX WARNING: Removed duplicated region for block: B:40:0x00eb  */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x00ae  */
    /* JADX WARNING: Removed duplicated region for block: B:36:0x00c2  */
    /* JADX WARNING: Removed duplicated region for block: B:37:0x00db  */
    /* JADX WARNING: Removed duplicated region for block: B:40:0x00eb  */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x00ae  */
    /* JADX WARNING: Removed duplicated region for block: B:37:0x00db  */
    /* JADX WARNING: Removed duplicated region for block: B:36:0x00c2  */
    /* JADX WARNING: Removed duplicated region for block: B:40:0x00eb  */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x00ae  */
    /* JADX WARNING: Removed duplicated region for block: B:36:0x00c2  */
    /* JADX WARNING: Removed duplicated region for block: B:37:0x00db  */
    /* JADX WARNING: Removed duplicated region for block: B:40:0x00eb  */
    private static org.telegram.tgnet.TLRPC.PhotoSize scaleAndSaveImageInternal(org.telegram.tgnet.TLRPC.PhotoSize r3, android.graphics.Bitmap r4, int r5, int r6, float r7, float r8, float r9, int r10, boolean r11, boolean r12) throws java.lang.Exception {
        /*
        r7 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r7 = (r9 > r7 ? 1 : (r9 == r7 ? 0 : -1));
        if (r7 > 0) goto L_0x000b;
    L_0x0006:
        if (r12 == 0) goto L_0x0009;
    L_0x0008:
        goto L_0x000b;
    L_0x0009:
        r5 = r4;
        goto L_0x0010;
    L_0x000b:
        r7 = 1;
        r5 = org.telegram.messenger.Bitmaps.createScaledBitmap(r4, r5, r6, r7);
    L_0x0010:
        r6 = 0;
        r7 = -NUM; // 0xfffffffvar_ float:-0.0 double:NaN;
        if (r3 == 0) goto L_0x0020;
    L_0x0016:
        r9 = r3.location;
        r12 = r9 instanceof org.telegram.tgnet.TLRPC.TL_fileLocationToBeDeprecated;
        if (r12 != 0) goto L_0x001d;
    L_0x001c:
        goto L_0x0020;
    L_0x001d:
        r9 = (org.telegram.tgnet.TLRPC.TL_fileLocationToBeDeprecated) r9;
        goto L_0x0088;
    L_0x0020:
        r9 = new org.telegram.tgnet.TLRPC$TL_fileLocationToBeDeprecated;
        r9.<init>();
        r9.volume_id = r7;
        r3 = -NUM; // 0xfffffffvar_ float:-0.0 double:NaN;
        r9.dc_id = r3;
        r3 = org.telegram.messenger.SharedConfig.getLastLocalId();
        r9.local_id = r3;
        r3 = new byte[r6];
        r9.file_reference = r3;
        r3 = new org.telegram.tgnet.TLRPC$TL_photoSize;
        r3.<init>();
        r3.location = r9;
        r12 = r5.getWidth();
        r3.w = r12;
        r12 = r5.getHeight();
        r3.h = r12;
        r12 = r3.w;
        r0 = 100;
        if (r12 > r0) goto L_0x0057;
    L_0x004e:
        r12 = r3.h;
        if (r12 > r0) goto L_0x0057;
    L_0x0052:
        r12 = "s";
        r3.type = r12;
        goto L_0x0088;
    L_0x0057:
        r12 = r3.w;
        r0 = 320; // 0x140 float:4.48E-43 double:1.58E-321;
        if (r12 > r0) goto L_0x0066;
    L_0x005d:
        r12 = r3.h;
        if (r12 > r0) goto L_0x0066;
    L_0x0061:
        r12 = "m";
        r3.type = r12;
        goto L_0x0088;
    L_0x0066:
        r12 = r3.w;
        r0 = 800; // 0x320 float:1.121E-42 double:3.953E-321;
        if (r12 > r0) goto L_0x0075;
    L_0x006c:
        r12 = r3.h;
        if (r12 > r0) goto L_0x0075;
    L_0x0070:
        r12 = "x";
        r3.type = r12;
        goto L_0x0088;
    L_0x0075:
        r12 = r3.w;
        r0 = 1280; // 0x500 float:1.794E-42 double:6.324E-321;
        if (r12 > r0) goto L_0x0084;
    L_0x007b:
        r12 = r3.h;
        if (r12 > r0) goto L_0x0084;
    L_0x007f:
        r12 = "y";
        r3.type = r12;
        goto L_0x0088;
    L_0x0084:
        r12 = "w";
        r3.type = r12;
    L_0x0088:
        r12 = new java.lang.StringBuilder;
        r12.<init>();
        r0 = r9.volume_id;
        r12.append(r0);
        r0 = "_";
        r12.append(r0);
        r0 = r9.local_id;
        r12.append(r0);
        r0 = ".jpg";
        r12.append(r0);
        r12 = r12.toString();
        r0 = new java.io.File;
        r1 = r9.volume_id;
        r9 = (r1 > r7 ? 1 : (r1 == r7 ? 0 : -1));
        if (r9 == 0) goto L_0x00ae;
    L_0x00ad:
        goto L_0x00af;
    L_0x00ae:
        r6 = 4;
    L_0x00af:
        r6 = org.telegram.messenger.FileLoader.getDirectory(r6);
        r0.<init>(r6, r12);
        r6 = new java.io.FileOutputStream;
        r6.<init>(r0);
        r7 = android.graphics.Bitmap.CompressFormat.JPEG;
        r5.compress(r7, r10, r6);
        if (r11 == 0) goto L_0x00db;
    L_0x00c2:
        r7 = new java.io.ByteArrayOutputStream;
        r7.<init>();
        r8 = android.graphics.Bitmap.CompressFormat.JPEG;
        r5.compress(r8, r10, r7);
        r8 = r7.toByteArray();
        r3.bytes = r8;
        r8 = r3.bytes;
        r8 = r8.length;
        r3.size = r8;
        r7.close();
        goto L_0x00e6;
    L_0x00db:
        r7 = r6.getChannel();
        r7 = r7.size();
        r8 = (int) r7;
        r3.size = r8;
    L_0x00e6:
        r6.close();
        if (r5 == r4) goto L_0x00ee;
    L_0x00eb:
        r5.recycle();
    L_0x00ee:
        return r3;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ImageLoader.scaleAndSaveImageInternal(org.telegram.tgnet.TLRPC$PhotoSize, android.graphics.Bitmap, int, int, float, float, float, int, boolean, boolean):org.telegram.tgnet.TLRPC$PhotoSize");
    }

    public static PhotoSize scaleAndSaveImage(Bitmap bitmap, float f, float f2, int i, boolean z) {
        return scaleAndSaveImage(null, bitmap, f, f2, i, z, 0, 0);
    }

    public static PhotoSize scaleAndSaveImage(PhotoSize photoSize, Bitmap bitmap, float f, float f2, int i, boolean z) {
        return scaleAndSaveImage(photoSize, bitmap, f, f2, i, z, 0, 0);
    }

    public static PhotoSize scaleAndSaveImage(Bitmap bitmap, float f, float f2, int i, boolean z, int i2, int i3) {
        return scaleAndSaveImage(null, bitmap, f, f2, i, z, i2, i3);
    }

    public static PhotoSize scaleAndSaveImage(PhotoSize photoSize, Bitmap bitmap, float f, float f2, int i, boolean z, int i2, int i3) {
        int i4;
        int i5;
        int i6 = i2;
        int i7 = i3;
        if (bitmap == null) {
            return null;
        }
        float width = (float) bitmap.getWidth();
        float height = (float) bitmap.getHeight();
        if (!(width == 0.0f || height == 0.0f)) {
            float f3;
            boolean z2;
            int i8;
            int i9;
            float max = Math.max(width / f, height / f2);
            if (!(i6 == 0 || i7 == 0)) {
                float f4 = (float) i6;
                if (width < f4 || height < ((float) i7)) {
                    if (width >= f4 || height <= ((float) i7)) {
                        if (width > f4) {
                            float f5 = (float) i7;
                            if (height < f5) {
                                f4 = height / f5;
                            }
                        }
                        f4 = Math.max(width / f4, height / ((float) i7));
                    } else {
                        f4 = width / f4;
                    }
                    f3 = f4;
                    z2 = true;
                    i8 = (int) (width / f3);
                    i9 = (int) (height / f3);
                    if (!(i9 == 0 || i8 == 0)) {
                        i4 = i9;
                        i5 = i8;
                        return scaleAndSaveImageInternal(photoSize, bitmap, i8, i9, width, height, f3, i, z, z2);
                    }
                }
            }
            f3 = max;
            z2 = false;
            i8 = (int) (width / f3);
            i9 = (int) (height / f3);
            i4 = i9;
            i5 = i8;
            try {
                return scaleAndSaveImageInternal(photoSize, bitmap, i8, i9, width, height, f3, i, z, z2);
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
        str = lastIndexOf != -1 ? str.substring(lastIndexOf + 1) : null;
        return (str == null || str.length() == 0 || str.length() > 4) ? str2 : str;
    }

    /* JADX WARNING: Removed duplicated region for block: B:89:? A:{SYNTHETIC, RETURN, ORIG_RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:25:0x0076  */
    /* JADX WARNING: Removed duplicated region for block: B:25:0x0076  */
    /* JADX WARNING: Removed duplicated region for block: B:89:? A:{SYNTHETIC, RETURN, ORIG_RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:89:? A:{SYNTHETIC, RETURN, ORIG_RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:25:0x0076  */
    public static void saveMessageThumbs(org.telegram.tgnet.TLRPC.Message r15) {
        /*
        r0 = r15.media;
        r1 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;
        r2 = 0;
        r3 = 0;
        if (r1 == 0) goto L_0x0027;
    L_0x0008:
        r0 = r0.photo;
        r0 = r0.sizes;
        r0 = r0.size();
        r1 = 0;
    L_0x0011:
        if (r1 >= r0) goto L_0x0074;
    L_0x0013:
        r4 = r15.media;
        r4 = r4.photo;
        r4 = r4.sizes;
        r4 = r4.get(r1);
        r4 = (org.telegram.tgnet.TLRPC.PhotoSize) r4;
        r5 = r4 instanceof org.telegram.tgnet.TLRPC.TL_photoCachedSize;
        if (r5 == 0) goto L_0x0024;
    L_0x0023:
        goto L_0x006f;
    L_0x0024:
        r1 = r1 + 1;
        goto L_0x0011;
    L_0x0027:
        r1 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaDocument;
        if (r1 == 0) goto L_0x004a;
    L_0x002b:
        r0 = r0.document;
        r0 = r0.thumbs;
        r0 = r0.size();
        r1 = 0;
    L_0x0034:
        if (r1 >= r0) goto L_0x0074;
    L_0x0036:
        r4 = r15.media;
        r4 = r4.document;
        r4 = r4.thumbs;
        r4 = r4.get(r1);
        r4 = (org.telegram.tgnet.TLRPC.PhotoSize) r4;
        r5 = r4 instanceof org.telegram.tgnet.TLRPC.TL_photoCachedSize;
        if (r5 == 0) goto L_0x0047;
    L_0x0046:
        goto L_0x006f;
    L_0x0047:
        r1 = r1 + 1;
        goto L_0x0034;
    L_0x004a:
        r1 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaWebPage;
        if (r1 == 0) goto L_0x0074;
    L_0x004e:
        r0 = r0.webpage;
        r0 = r0.photo;
        if (r0 == 0) goto L_0x0074;
    L_0x0054:
        r0 = r0.sizes;
        r0 = r0.size();
        r1 = 0;
    L_0x005b:
        if (r1 >= r0) goto L_0x0074;
    L_0x005d:
        r4 = r15.media;
        r4 = r4.webpage;
        r4 = r4.photo;
        r4 = r4.sizes;
        r4 = r4.get(r1);
        r4 = (org.telegram.tgnet.TLRPC.PhotoSize) r4;
        r5 = r4 instanceof org.telegram.tgnet.TLRPC.TL_photoCachedSize;
        if (r5 == 0) goto L_0x0071;
    L_0x006f:
        r3 = r4;
        goto L_0x0074;
    L_0x0071:
        r1 = r1 + 1;
        goto L_0x005b;
    L_0x0074:
        if (r3 == 0) goto L_0x01df;
    L_0x0076:
        r0 = r3.bytes;
        if (r0 == 0) goto L_0x01df;
    L_0x007a:
        r0 = r0.length;
        if (r0 == 0) goto L_0x01df;
    L_0x007d:
        r0 = r3.location;
        if (r0 == 0) goto L_0x0085;
    L_0x0081:
        r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_fileLocationUnavailable;
        if (r0 == 0) goto L_0x0099;
    L_0x0085:
        r0 = new org.telegram.tgnet.TLRPC$TL_fileLocationToBeDeprecated;
        r0.<init>();
        r3.location = r0;
        r0 = r3.location;
        r4 = -NUM; // 0xfffffffvar_ float:-0.0 double:NaN;
        r0.volume_id = r4;
        r1 = org.telegram.messenger.SharedConfig.getLastLocalId();
        r0.local_id = r1;
    L_0x0099:
        r0 = 1;
        r1 = org.telegram.messenger.FileLoader.getPathToAttach(r3, r0);
        r4 = org.telegram.messenger.MessageObject.shouldEncryptPhotoOrVideo(r15);
        if (r4 == 0) goto L_0x00c0;
    L_0x00a4:
        r4 = new java.io.File;
        r5 = new java.lang.StringBuilder;
        r5.<init>();
        r1 = r1.getAbsolutePath();
        r5.append(r1);
        r1 = ".enc";
        r5.append(r1);
        r1 = r5.toString();
        r4.<init>(r1);
        r1 = r4;
        goto L_0x00c1;
    L_0x00c0:
        r0 = 0;
    L_0x00c1:
        r4 = r1.exists();
        if (r4 != 0) goto L_0x013d;
    L_0x00c7:
        r4 = "rws";
        if (r0 == 0) goto L_0x012b;
    L_0x00cb:
        r0 = new java.io.File;	 Catch:{ Exception -> 0x0139 }
        r5 = org.telegram.messenger.FileLoader.getInternalCacheDir();	 Catch:{ Exception -> 0x0139 }
        r6 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0139 }
        r6.<init>();	 Catch:{ Exception -> 0x0139 }
        r7 = r1.getName();	 Catch:{ Exception -> 0x0139 }
        r6.append(r7);	 Catch:{ Exception -> 0x0139 }
        r7 = ".key";
        r6.append(r7);	 Catch:{ Exception -> 0x0139 }
        r6 = r6.toString();	 Catch:{ Exception -> 0x0139 }
        r0.<init>(r5, r6);	 Catch:{ Exception -> 0x0139 }
        r5 = new java.io.RandomAccessFile;	 Catch:{ Exception -> 0x0139 }
        r5.<init>(r0, r4);	 Catch:{ Exception -> 0x0139 }
        r6 = r5.length();	 Catch:{ Exception -> 0x0139 }
        r0 = 32;
        r9 = new byte[r0];	 Catch:{ Exception -> 0x0139 }
        r8 = 16;
        r10 = new byte[r8];	 Catch:{ Exception -> 0x0139 }
        r11 = 0;
        r13 = (r6 > r11 ? 1 : (r6 == r11 ? 0 : -1));
        if (r13 <= 0) goto L_0x010e;
    L_0x0100:
        r13 = 48;
        r6 = r6 % r13;
        r13 = (r6 > r11 ? 1 : (r6 == r11 ? 0 : -1));
        if (r13 != 0) goto L_0x010e;
    L_0x0107:
        r5.read(r9, r2, r0);	 Catch:{ Exception -> 0x0139 }
        r5.read(r10, r2, r8);	 Catch:{ Exception -> 0x0139 }
        goto L_0x011e;
    L_0x010e:
        r0 = org.telegram.messenger.Utilities.random;	 Catch:{ Exception -> 0x0139 }
        r0.nextBytes(r9);	 Catch:{ Exception -> 0x0139 }
        r0 = org.telegram.messenger.Utilities.random;	 Catch:{ Exception -> 0x0139 }
        r0.nextBytes(r10);	 Catch:{ Exception -> 0x0139 }
        r5.write(r9);	 Catch:{ Exception -> 0x0139 }
        r5.write(r10);	 Catch:{ Exception -> 0x0139 }
    L_0x011e:
        r5.close();	 Catch:{ Exception -> 0x0139 }
        r8 = r3.bytes;	 Catch:{ Exception -> 0x0139 }
        r11 = 0;
        r0 = r3.bytes;	 Catch:{ Exception -> 0x0139 }
        r12 = r0.length;	 Catch:{ Exception -> 0x0139 }
        r13 = 0;
        org.telegram.messenger.Utilities.aesCtrDecryptionByteArray(r8, r9, r10, r11, r12, r13);	 Catch:{ Exception -> 0x0139 }
    L_0x012b:
        r0 = new java.io.RandomAccessFile;	 Catch:{ Exception -> 0x0139 }
        r0.<init>(r1, r4);	 Catch:{ Exception -> 0x0139 }
        r1 = r3.bytes;	 Catch:{ Exception -> 0x0139 }
        r0.write(r1);	 Catch:{ Exception -> 0x0139 }
        r0.close();	 Catch:{ Exception -> 0x0139 }
        goto L_0x013d;
    L_0x0139:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x013d:
        r0 = new org.telegram.tgnet.TLRPC$TL_photoSize;
        r0.<init>();
        r1 = r3.w;
        r0.w = r1;
        r1 = r3.h;
        r0.h = r1;
        r1 = r3.location;
        r0.location = r1;
        r1 = r3.size;
        r0.size = r1;
        r1 = r3.type;
        r0.type = r1;
        r1 = r15.media;
        r3 = r1 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;
        if (r3 == 0) goto L_0x0183;
    L_0x015c:
        r1 = r1.photo;
        r1 = r1.sizes;
        r1 = r1.size();
    L_0x0164:
        if (r2 >= r1) goto L_0x01df;
    L_0x0166:
        r3 = r15.media;
        r3 = r3.photo;
        r3 = r3.sizes;
        r3 = r3.get(r2);
        r3 = (org.telegram.tgnet.TLRPC.PhotoSize) r3;
        r3 = r3 instanceof org.telegram.tgnet.TLRPC.TL_photoCachedSize;
        if (r3 == 0) goto L_0x0180;
    L_0x0176:
        r15 = r15.media;
        r15 = r15.photo;
        r15 = r15.sizes;
        r15.set(r2, r0);
        goto L_0x01df;
    L_0x0180:
        r2 = r2 + 1;
        goto L_0x0164;
    L_0x0183:
        r3 = r1 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaDocument;
        if (r3 == 0) goto L_0x01ae;
    L_0x0187:
        r1 = r1.document;
        r1 = r1.thumbs;
        r1 = r1.size();
    L_0x018f:
        if (r2 >= r1) goto L_0x01df;
    L_0x0191:
        r3 = r15.media;
        r3 = r3.document;
        r3 = r3.thumbs;
        r3 = r3.get(r2);
        r3 = (org.telegram.tgnet.TLRPC.PhotoSize) r3;
        r3 = r3 instanceof org.telegram.tgnet.TLRPC.TL_photoCachedSize;
        if (r3 == 0) goto L_0x01ab;
    L_0x01a1:
        r15 = r15.media;
        r15 = r15.document;
        r15 = r15.thumbs;
        r15.set(r2, r0);
        goto L_0x01df;
    L_0x01ab:
        r2 = r2 + 1;
        goto L_0x018f;
    L_0x01ae:
        r3 = r1 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaWebPage;
        if (r3 == 0) goto L_0x01df;
    L_0x01b2:
        r1 = r1.webpage;
        r1 = r1.photo;
        r1 = r1.sizes;
        r1 = r1.size();
    L_0x01bc:
        if (r2 >= r1) goto L_0x01df;
    L_0x01be:
        r3 = r15.media;
        r3 = r3.webpage;
        r3 = r3.photo;
        r3 = r3.sizes;
        r3 = r3.get(r2);
        r3 = (org.telegram.tgnet.TLRPC.PhotoSize) r3;
        r3 = r3 instanceof org.telegram.tgnet.TLRPC.TL_photoCachedSize;
        if (r3 == 0) goto L_0x01dc;
    L_0x01d0:
        r15 = r15.media;
        r15 = r15.webpage;
        r15 = r15.photo;
        r15 = r15.sizes;
        r15.set(r2, r0);
        goto L_0x01df;
    L_0x01dc:
        r2 = r2 + 1;
        goto L_0x01bc;
    L_0x01df:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ImageLoader.saveMessageThumbs(org.telegram.tgnet.TLRPC$Message):void");
    }

    public static void saveMessagesThumbs(ArrayList<Message> arrayList) {
        if (arrayList != null && !arrayList.isEmpty()) {
            for (int i = 0; i < arrayList.size(); i++) {
                saveMessageThumbs((Message) arrayList.get(i));
            }
        }
    }
}
