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
import com.airbnb.lottie.LottieDrawable;
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
    private LruCache<LottieDrawable> lottieMemCache;
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
            this.keys = new ArrayList();
            this.filters = new ArrayList();
            this.imageTypes = new ArrayList();
        }

        /* synthetic */ CacheImage(ImageLoader imageLoader, AnonymousClass1 anonymousClass1) {
            this();
        }

        public void addImageReceiver(ImageReceiver imageReceiver, String str, String str2, int i) {
            if (!this.imageReceiverArray.contains(imageReceiver)) {
                this.imageReceiverArray.add(imageReceiver);
                this.keys.add(str);
                this.filters.add(str2);
                this.imageTypes.add(Integer.valueOf(i));
                ImageLoader.this.imageLoadingByTag.put(imageReceiver.getTag(i), this);
            }
        }

        public void replaceImageReceiver(ImageReceiver imageReceiver, String str, String str2, int i) {
            int indexOf = this.imageReceiverArray.indexOf(imageReceiver);
            if (indexOf != -1) {
                if (((Integer) this.imageTypes.get(indexOf)).intValue() != i) {
                    ArrayList arrayList = this.imageReceiverArray;
                    indexOf = arrayList.subList(indexOf + 1, arrayList.size()).indexOf(imageReceiver);
                    if (indexOf == -1) {
                        return;
                    }
                }
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
                AndroidUtilities.runOnUIThread(new -$$Lambda$ImageLoader$CacheImage$DfnPkD34YpkvdtADQzUhGGpSRuE(this, drawable, new ArrayList(this.imageReceiverArray), str));
            }
            for (int i = 0; i < this.imageReceiverArray.size(); i++) {
                ImageLoader.this.imageLoadingByTag.remove(((ImageReceiver) this.imageReceiverArray.get(i)).getTag(this.imageType));
            }
            this.imageReceiverArray.clear();
            if (this.url != null) {
                ImageLoader.this.imageLoadingByUrl.remove(this.url);
            }
            if (this.key != null) {
                ImageLoader.this.imageLoadingByKeys.remove(this.key);
            }
        }

        public /* synthetic */ void lambda$setImageAndClear$0$ImageLoader$CacheImage(Drawable drawable, ArrayList arrayList, String str) {
            int i;
            if (drawable instanceof AnimatedFileDrawable) {
                drawable = (AnimatedFileDrawable) drawable;
                Object obj = null;
                for (i = 0; i < arrayList.size(); i++) {
                    Drawable drawable2;
                    ImageReceiver imageReceiver = (ImageReceiver) arrayList.get(i);
                    if (i == 0) {
                        drawable2 = drawable;
                    } else {
                        drawable2 = drawable.makeCopy();
                    }
                    if (imageReceiver.setImageBitmapByKey(drawable2, this.key, this.imageType, false)) {
                        if (drawable2 == drawable) {
                            obj = 1;
                        }
                    } else if (drawable2 != drawable) {
                        drawable2.recycle();
                    }
                }
                if (obj == null) {
                    drawable.recycle();
                }
            } else {
                for (i = 0; i < arrayList.size(); i++) {
                    ((ImageReceiver) arrayList.get(i)).setImageBitmapByKey(drawable, this.key, ((Integer) this.imageTypes.get(i)).intValue(), false);
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

        /* JADX WARNING: Removed duplicated region for block: B:456:0x06db A:{SYNTHETIC, Splitter:B:456:0x06db} */
        /* JADX WARNING: Removed duplicated region for block: B:682:0x0971  */
        /* JADX WARNING: Removed duplicated region for block: B:681:0x096b  */
        /* JADX WARNING: Removed duplicated region for block: B:675:0x0959 A:{SKIP} */
        /* JADX WARNING: Removed duplicated region for block: B:681:0x096b  */
        /* JADX WARNING: Removed duplicated region for block: B:682:0x0971  */
        /* JADX WARNING: Removed duplicated region for block: B:675:0x0959 A:{SKIP} */
        /* JADX WARNING: Removed duplicated region for block: B:682:0x0971  */
        /* JADX WARNING: Removed duplicated region for block: B:681:0x096b  */
        /* JADX WARNING: Removed duplicated region for block: B:675:0x0959 A:{SKIP} */
        /* JADX WARNING: Removed duplicated region for block: B:681:0x096b  */
        /* JADX WARNING: Removed duplicated region for block: B:682:0x0971  */
        /* JADX WARNING: Removed duplicated region for block: B:433:0x0691  */
        /* JADX WARNING: Removed duplicated region for block: B:301:0x0490 A:{SYNTHETIC, Splitter:B:301:0x0490} */
        /* JADX WARNING: Removed duplicated region for block: B:675:0x0959 A:{SKIP} */
        /* JADX WARNING: Removed duplicated region for block: B:682:0x0971  */
        /* JADX WARNING: Removed duplicated region for block: B:681:0x096b  */
        /* JADX WARNING: Removed duplicated region for block: B:675:0x0959 A:{SKIP} */
        /* JADX WARNING: Removed duplicated region for block: B:681:0x096b  */
        /* JADX WARNING: Removed duplicated region for block: B:682:0x0971  */
        /* JADX WARNING: Removed duplicated region for block: B:675:0x0959 A:{SKIP} */
        /* JADX WARNING: Removed duplicated region for block: B:682:0x0971  */
        /* JADX WARNING: Removed duplicated region for block: B:681:0x096b  */
        /* JADX WARNING: Removed duplicated region for block: B:301:0x0490 A:{SYNTHETIC, Splitter:B:301:0x0490} */
        /* JADX WARNING: Removed duplicated region for block: B:433:0x0691  */
        /* JADX WARNING: Removed duplicated region for block: B:675:0x0959 A:{SKIP} */
        /* JADX WARNING: Removed duplicated region for block: B:681:0x096b  */
        /* JADX WARNING: Removed duplicated region for block: B:682:0x0971  */
        /* JADX WARNING: Removed duplicated region for block: B:433:0x0691  */
        /* JADX WARNING: Removed duplicated region for block: B:301:0x0490 A:{SYNTHETIC, Splitter:B:301:0x0490} */
        /* JADX WARNING: Removed duplicated region for block: B:675:0x0959 A:{SKIP} */
        /* JADX WARNING: Removed duplicated region for block: B:682:0x0971  */
        /* JADX WARNING: Removed duplicated region for block: B:681:0x096b  */
        /* JADX WARNING: Removed duplicated region for block: B:169:0x029f  */
        /* JADX WARNING: Removed duplicated region for block: B:268:0x0422  */
        /* JADX WARNING: Removed duplicated region for block: B:174:0x02b1 A:{Catch:{ Throwable -> 0x0475 }} */
        /* JADX WARNING: Removed duplicated region for block: B:301:0x0490 A:{SYNTHETIC, Splitter:B:301:0x0490} */
        /* JADX WARNING: Removed duplicated region for block: B:433:0x0691  */
        /* JADX WARNING: Removed duplicated region for block: B:675:0x0959 A:{SKIP} */
        /* JADX WARNING: Removed duplicated region for block: B:681:0x096b  */
        /* JADX WARNING: Removed duplicated region for block: B:682:0x0971  */
        /* JADX WARNING: Removed duplicated region for block: B:169:0x029f  */
        /* JADX WARNING: Removed duplicated region for block: B:174:0x02b1 A:{Catch:{ Throwable -> 0x0475 }} */
        /* JADX WARNING: Removed duplicated region for block: B:268:0x0422  */
        /* JADX WARNING: Removed duplicated region for block: B:433:0x0691  */
        /* JADX WARNING: Removed duplicated region for block: B:301:0x0490 A:{SYNTHETIC, Splitter:B:301:0x0490} */
        /* JADX WARNING: Removed duplicated region for block: B:675:0x0959 A:{SKIP} */
        /* JADX WARNING: Removed duplicated region for block: B:682:0x0971  */
        /* JADX WARNING: Removed duplicated region for block: B:681:0x096b  */
        /* JADX WARNING: Removed duplicated region for block: B:675:0x0959 A:{SKIP} */
        /* JADX WARNING: Removed duplicated region for block: B:681:0x096b  */
        /* JADX WARNING: Removed duplicated region for block: B:682:0x0971  */
        /* JADX WARNING: Removed duplicated region for block: B:134:0x0215 A:{SYNTHETIC, Splitter:B:134:0x0215} */
        /* JADX WARNING: Removed duplicated region for block: B:147:0x0232  */
        /* JADX WARNING: Removed duplicated region for block: B:169:0x029f  */
        /* JADX WARNING: Removed duplicated region for block: B:268:0x0422  */
        /* JADX WARNING: Removed duplicated region for block: B:174:0x02b1 A:{Catch:{ Throwable -> 0x0475 }} */
        /* JADX WARNING: Removed duplicated region for block: B:301:0x0490 A:{SYNTHETIC, Splitter:B:301:0x0490} */
        /* JADX WARNING: Removed duplicated region for block: B:433:0x0691  */
        /* JADX WARNING: Removed duplicated region for block: B:675:0x0959 A:{SKIP} */
        /* JADX WARNING: Removed duplicated region for block: B:682:0x0971  */
        /* JADX WARNING: Removed duplicated region for block: B:681:0x096b  */
        /* JADX WARNING: Removed duplicated region for block: B:675:0x0959 A:{SKIP} */
        /* JADX WARNING: Removed duplicated region for block: B:681:0x096b  */
        /* JADX WARNING: Removed duplicated region for block: B:682:0x0971  */
        /* JADX WARNING: Removed duplicated region for block: B:134:0x0215 A:{SYNTHETIC, Splitter:B:134:0x0215} */
        /* JADX WARNING: Removed duplicated region for block: B:147:0x0232  */
        /* JADX WARNING: Removed duplicated region for block: B:169:0x029f  */
        /* JADX WARNING: Removed duplicated region for block: B:174:0x02b1 A:{Catch:{ Throwable -> 0x0475 }} */
        /* JADX WARNING: Removed duplicated region for block: B:268:0x0422  */
        /* JADX WARNING: Removed duplicated region for block: B:433:0x0691  */
        /* JADX WARNING: Removed duplicated region for block: B:301:0x0490 A:{SYNTHETIC, Splitter:B:301:0x0490} */
        /* JADX WARNING: Removed duplicated region for block: B:675:0x0959 A:{SKIP} */
        /* JADX WARNING: Removed duplicated region for block: B:682:0x0971  */
        /* JADX WARNING: Removed duplicated region for block: B:681:0x096b  */
        /* JADX WARNING: Removed duplicated region for block: B:675:0x0959 A:{SKIP} */
        /* JADX WARNING: Removed duplicated region for block: B:681:0x096b  */
        /* JADX WARNING: Removed duplicated region for block: B:682:0x0971  */
        /* JADX WARNING: Removed duplicated region for block: B:301:0x0490 A:{SYNTHETIC, Splitter:B:301:0x0490} */
        /* JADX WARNING: Removed duplicated region for block: B:433:0x0691  */
        /* JADX WARNING: Removed duplicated region for block: B:675:0x0959 A:{SKIP} */
        /* JADX WARNING: Removed duplicated region for block: B:682:0x0971  */
        /* JADX WARNING: Removed duplicated region for block: B:681:0x096b  */
        /* JADX WARNING: Removed duplicated region for block: B:433:0x0691  */
        /* JADX WARNING: Removed duplicated region for block: B:301:0x0490 A:{SYNTHETIC, Splitter:B:301:0x0490} */
        /* JADX WARNING: Removed duplicated region for block: B:675:0x0959 A:{SKIP} */
        /* JADX WARNING: Removed duplicated region for block: B:681:0x096b  */
        /* JADX WARNING: Removed duplicated region for block: B:682:0x0971  */
        /* JADX WARNING: Removed duplicated region for block: B:675:0x0959 A:{SKIP} */
        /* JADX WARNING: Removed duplicated region for block: B:682:0x0971  */
        /* JADX WARNING: Removed duplicated region for block: B:681:0x096b  */
        /* JADX WARNING: Removed duplicated region for block: B:301:0x0490 A:{SYNTHETIC, Splitter:B:301:0x0490} */
        /* JADX WARNING: Removed duplicated region for block: B:433:0x0691  */
        /* JADX WARNING: Removed duplicated region for block: B:675:0x0959 A:{SKIP} */
        /* JADX WARNING: Removed duplicated region for block: B:681:0x096b  */
        /* JADX WARNING: Removed duplicated region for block: B:682:0x0971  */
        /* JADX WARNING: Removed duplicated region for block: B:433:0x0691  */
        /* JADX WARNING: Removed duplicated region for block: B:301:0x0490 A:{SYNTHETIC, Splitter:B:301:0x0490} */
        /* JADX WARNING: Removed duplicated region for block: B:675:0x0959 A:{SKIP} */
        /* JADX WARNING: Removed duplicated region for block: B:682:0x0971  */
        /* JADX WARNING: Removed duplicated region for block: B:681:0x096b  */
        /* JADX WARNING: Removed duplicated region for block: B:301:0x0490 A:{SYNTHETIC, Splitter:B:301:0x0490} */
        /* JADX WARNING: Removed duplicated region for block: B:433:0x0691  */
        /* JADX WARNING: Removed duplicated region for block: B:675:0x0959 A:{SKIP} */
        /* JADX WARNING: Removed duplicated region for block: B:681:0x096b  */
        /* JADX WARNING: Removed duplicated region for block: B:682:0x0971  */
        /* JADX WARNING: Removed duplicated region for block: B:433:0x0691  */
        /* JADX WARNING: Removed duplicated region for block: B:301:0x0490 A:{SYNTHETIC, Splitter:B:301:0x0490} */
        /* JADX WARNING: Removed duplicated region for block: B:675:0x0959 A:{SKIP} */
        /* JADX WARNING: Removed duplicated region for block: B:682:0x0971  */
        /* JADX WARNING: Removed duplicated region for block: B:681:0x096b  */
        /* JADX WARNING: Removed duplicated region for block: B:675:0x0959 A:{SKIP} */
        /* JADX WARNING: Removed duplicated region for block: B:681:0x096b  */
        /* JADX WARNING: Removed duplicated region for block: B:682:0x0971  */
        /* JADX WARNING: Removed duplicated region for block: B:139:0x021d A:{SYNTHETIC, Splitter:B:139:0x021d} */
        /* JADX WARNING: Removed duplicated region for block: B:675:0x0959 A:{SKIP} */
        /* JADX WARNING: Removed duplicated region for block: B:682:0x0971  */
        /* JADX WARNING: Removed duplicated region for block: B:681:0x096b  */
        /* JADX WARNING: Removed duplicated region for block: B:675:0x0959 A:{SKIP} */
        /* JADX WARNING: Removed duplicated region for block: B:681:0x096b  */
        /* JADX WARNING: Removed duplicated region for block: B:682:0x0971  */
        /* JADX WARNING: Removed duplicated region for block: B:301:0x0490 A:{SYNTHETIC, Splitter:B:301:0x0490} */
        /* JADX WARNING: Removed duplicated region for block: B:433:0x0691  */
        /* JADX WARNING: Removed duplicated region for block: B:675:0x0959 A:{SKIP} */
        /* JADX WARNING: Removed duplicated region for block: B:682:0x0971  */
        /* JADX WARNING: Removed duplicated region for block: B:681:0x096b  */
        /* JADX WARNING: Removed duplicated region for block: B:641:0x0923  */
        /* JADX WARNING: Removed duplicated region for block: B:599:0x08a8  */
        /* JADX WARNING: Removed duplicated region for block: B:645:0x092a A:{SYNTHETIC, Splitter:B:645:0x092a} */
        /* JADX WARNING: Removed duplicated region for block: B:675:0x0959 A:{SKIP} */
        /* JADX WARNING: Removed duplicated region for block: B:681:0x096b  */
        /* JADX WARNING: Removed duplicated region for block: B:682:0x0971  */
        /* JADX WARNING: Missing block: B:8:0x0015, code skipped:
            r0 = r1.cacheImage;
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
            r8 = (byte[]) org.telegram.messenger.ImageLoader.access$1600().get();
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
            org.telegram.messenger.ImageLoader.access$1600().set(r8);
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
            if (r0.lottieFile == false) goto L_0x0118;
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
            r2 = new com.airbnb.lottie.LottieDrawable();
     */
        /* JADX WARNING: Missing block: B:52:?, code skipped:
            r0 = new java.io.FileInputStream(r1.cacheImage.finalFilePath);
            r2.setComposition((com.airbnb.lottie.LottieComposition) com.airbnb.lottie.LottieCompositionFactory.fromJsonInputStreamSync(r0, r1.cacheImage.finalFilePath.toString()).getValue());
            r0.close();
     */
        /* JADX WARNING: Missing block: B:53:0x00ff, code skipped:
            r0 = move-exception;
     */
        /* JADX WARNING: Missing block: B:54:0x0100, code skipped:
            org.telegram.messenger.FileLog.e(r0);
     */
        /* JADX WARNING: Missing block: B:61:0x011a, code skipped:
            if (r0.animatedFile == false) goto L_0x0180;
     */
        /* JADX WARNING: Missing block: B:62:0x011c, code skipped:
            r2 = r1.sync;
     */
        /* JADX WARNING: Missing block: B:63:0x011e, code skipped:
            monitor-enter(r2);
     */
        /* JADX WARNING: Missing block: B:66:0x0121, code skipped:
            if (r1.isCancelled == false) goto L_0x0125;
     */
        /* JADX WARNING: Missing block: B:67:0x0123, code skipped:
            monitor-exit(r2);
     */
        /* JADX WARNING: Missing block: B:68:0x0124, code skipped:
            return;
     */
        /* JADX WARNING: Missing block: B:69:0x0125, code skipped:
            monitor-exit(r2);
     */
        /* JADX WARNING: Missing block: B:71:0x0130, code skipped:
            if ("g".equals(r1.cacheImage.filter) == false) goto L_0x0157;
     */
        /* JADX WARNING: Missing block: B:72:0x0132, code skipped:
            r0 = r1.cacheImage;
            r2 = r0.imageLocation.document;
     */
        /* JADX WARNING: Missing block: B:73:0x013a, code skipped:
            if ((r2 instanceof org.telegram.tgnet.TLRPC.TL_documentEncrypted) != false) goto L_0x0157;
     */
        /* JADX WARNING: Missing block: B:74:0x013c, code skipped:
            r7 = r0.finalFilePath;
            r9 = (long) r0.size;
     */
        /* JADX WARNING: Missing block: B:75:0x0146, code skipped:
            if ((r2 instanceof org.telegram.tgnet.TLRPC.Document) == false) goto L_0x014a;
     */
        /* JADX WARNING: Missing block: B:76:0x0148, code skipped:
            r11 = r2;
     */
        /* JADX WARNING: Missing block: B:77:0x014a, code skipped:
            r11 = null;
     */
        /* JADX WARNING: Missing block: B:78:0x014b, code skipped:
            r0 = r1.cacheImage;
            r0 = new org.telegram.ui.Components.AnimatedFileDrawable(r7, false, r9, r11, r0.parentObject, r0.currentAccount);
     */
        /* JADX WARNING: Missing block: B:79:0x0157, code skipped:
            r2 = r1.cacheImage;
            r14 = new org.telegram.ui.Components.AnimatedFileDrawable(r2.finalFilePath, "d".equals(r2.filter), 0, null, null, r1.cacheImage.currentAccount);
     */
        /* JADX WARNING: Missing block: B:80:0x0175, code skipped:
            java.lang.Thread.interrupted();
            onPostExecute(r0);
     */
        /* JADX WARNING: Missing block: B:85:0x0180, code skipped:
            r2 = r0.finalFilePath;
     */
        /* JADX WARNING: Missing block: B:86:0x0184, code skipped:
            if (r0.secureDocument != null) goto L_0x019b;
     */
        /* JADX WARNING: Missing block: B:88:0x0188, code skipped:
            if (r0.encryptionKeyPath == null) goto L_0x0199;
     */
        /* JADX WARNING: Missing block: B:89:0x018a, code skipped:
            if (r2 == null) goto L_0x0199;
     */
        /* JADX WARNING: Missing block: B:91:0x0196, code skipped:
            if (r2.getAbsolutePath().endsWith(".enc") == false) goto L_0x0199;
     */
        /* JADX WARNING: Missing block: B:92:0x0199, code skipped:
            r8 = null;
     */
        /* JADX WARNING: Missing block: B:93:0x019b, code skipped:
            r8 = 1;
     */
        /* JADX WARNING: Missing block: B:94:0x019c, code skipped:
            r0 = r1.cacheImage.secureDocument;
     */
        /* JADX WARNING: Missing block: B:95:0x01a0, code skipped:
            if (r0 == null) goto L_0x01b5;
     */
        /* JADX WARNING: Missing block: B:96:0x01a2, code skipped:
            r9 = r0.secureDocumentKey;
            r0 = r0.secureFile;
     */
        /* JADX WARNING: Missing block: B:97:0x01a6, code skipped:
            if (r0 == null) goto L_0x01ad;
     */
        /* JADX WARNING: Missing block: B:98:0x01a8, code skipped:
            r0 = r0.file_hash;
     */
        /* JADX WARNING: Missing block: B:99:0x01aa, code skipped:
            if (r0 == null) goto L_0x01ad;
     */
        /* JADX WARNING: Missing block: B:100:0x01ad, code skipped:
            r0 = r1.cacheImage.secureDocument.fileHash;
     */
        /* JADX WARNING: Missing block: B:101:0x01b3, code skipped:
            r10 = r0;
     */
        /* JADX WARNING: Missing block: B:102:0x01b5, code skipped:
            r9 = null;
            r10 = null;
     */
        /* JADX WARNING: Missing block: B:104:0x01bb, code skipped:
            if (android.os.Build.VERSION.SDK_INT >= 19) goto L_0x0227;
     */
        /* JADX WARNING: Missing block: B:106:?, code skipped:
            r11 = new java.io.RandomAccessFile(r2, "r");
     */
        /* JADX WARNING: Missing block: B:109:0x01c8, code skipped:
            if (r1.cacheImage.imageType != 1) goto L_0x01cf;
     */
        /* JADX WARNING: Missing block: B:110:0x01ca, code skipped:
            r0 = org.telegram.messenger.ImageLoader.access$1700();
     */
        /* JADX WARNING: Missing block: B:111:0x01cf, code skipped:
            r0 = org.telegram.messenger.ImageLoader.access$1800();
     */
        /* JADX WARNING: Missing block: B:112:0x01d3, code skipped:
            r11.readFully(r0, 0, r0.length);
            r0 = new java.lang.String(r0).toLowerCase().toLowerCase();
     */
        /* JADX WARNING: Missing block: B:113:0x01ea, code skipped:
            if (r0.startsWith("riff") == false) goto L_0x01f7;
     */
        /* JADX WARNING: Missing block: B:115:0x01f3, code skipped:
            if (r0.endsWith("webp") == false) goto L_0x01f7;
     */
        /* JADX WARNING: Missing block: B:116:0x01f5, code skipped:
            r12 = true;
     */
        /* JADX WARNING: Missing block: B:117:0x01f7, code skipped:
            r12 = null;
     */
        /* JADX WARNING: Missing block: B:119:?, code skipped:
            r11.close();
     */
        /* JADX WARNING: Missing block: B:121:?, code skipped:
            r11.close();
     */
        /* JADX WARNING: Missing block: B:122:0x01ff, code skipped:
            r0 = move-exception;
     */
        /* JADX WARNING: Missing block: B:123:0x0200, code skipped:
            org.telegram.messenger.FileLog.e(r0);
     */
        /* JADX WARNING: Missing block: B:124:0x0205, code skipped:
            r0 = e;
     */
        /* JADX WARNING: Missing block: B:125:0x0207, code skipped:
            r0 = e;
     */
        /* JADX WARNING: Missing block: B:126:0x0209, code skipped:
            r0 = move-exception;
     */
        /* JADX WARNING: Missing block: B:127:0x020a, code skipped:
            r2 = r0;
            r11 = null;
     */
        /* JADX WARNING: Missing block: B:128:0x020d, code skipped:
            r0 = e;
     */
        /* JADX WARNING: Missing block: B:129:0x020e, code skipped:
            r11 = null;
     */
        /* JADX WARNING: Missing block: B:130:0x020f, code skipped:
            r12 = null;
     */
        /* JADX WARNING: Missing block: B:132:?, code skipped:
            org.telegram.messenger.FileLog.e(r0);
     */
        /* JADX WARNING: Missing block: B:133:0x0213, code skipped:
            if (r11 != null) goto L_0x0215;
     */
        /* JADX WARNING: Missing block: B:135:?, code skipped:
            r11.close();
     */
        /* JADX WARNING: Missing block: B:136:0x0219, code skipped:
            r0 = move-exception;
     */
        /* JADX WARNING: Missing block: B:137:0x021a, code skipped:
            r2 = r0;
     */
        /* JADX WARNING: Missing block: B:138:0x021b, code skipped:
            if (r11 != null) goto L_0x021d;
     */
        /* JADX WARNING: Missing block: B:140:?, code skipped:
            r11.close();
     */
        /* JADX WARNING: Missing block: B:141:0x0221, code skipped:
            r0 = move-exception;
     */
        /* JADX WARNING: Missing block: B:142:0x0222, code skipped:
            org.telegram.messenger.FileLog.e(r0);
     */
        /* JADX WARNING: Missing block: B:143:0x0226, code skipped:
            throw r2;
     */
        /* JADX WARNING: Missing block: B:144:0x0227, code skipped:
            r12 = null;
     */
        /* JADX WARNING: Missing block: B:145:0x0228, code skipped:
            r0 = r1.cacheImage.imageLocation.path;
     */
        /* JADX WARNING: Missing block: B:146:0x0230, code skipped:
            if (r0 != null) goto L_0x0232;
     */
        /* JADX WARNING: Missing block: B:148:0x0238, code skipped:
            if (r0.startsWith("thumb://") != false) goto L_0x023a;
     */
        /* JADX WARNING: Missing block: B:149:0x023a, code skipped:
            r13 = r0.indexOf(":", 8);
     */
        /* JADX WARNING: Missing block: B:150:0x0240, code skipped:
            if (r13 >= 0) goto L_0x0242;
     */
        /* JADX WARNING: Missing block: B:151:0x0242, code skipped:
            r14 = java.lang.Long.valueOf(java.lang.Long.parseLong(r0.substring(8, r13)));
            r0 = r0.substring(r13 + 1);
     */
        /* JADX WARNING: Missing block: B:152:0x0254, code skipped:
            r0 = null;
            r14 = null;
     */
        /* JADX WARNING: Missing block: B:153:0x0256, code skipped:
            r13 = r0;
     */
        /* JADX WARNING: Missing block: B:154:0x0257, code skipped:
            r15 = null;
     */
        /* JADX WARNING: Missing block: B:155:0x0258, code skipped:
            r16 = null;
     */
        /* JADX WARNING: Missing block: B:157:0x0262, code skipped:
            if (r0.startsWith("vthumb://") != false) goto L_0x0264;
     */
        /* JADX WARNING: Missing block: B:158:0x0264, code skipped:
            r14 = r0.indexOf(":", 9);
     */
        /* JADX WARNING: Missing block: B:159:0x026c, code skipped:
            if (r14 >= 0) goto L_0x026e;
     */
        /* JADX WARNING: Missing block: B:160:0x026e, code skipped:
            r0 = java.lang.Long.valueOf(java.lang.Long.parseLong(r0.substring(9, r14)));
            r13 = 1;
     */
        /* JADX WARNING: Missing block: B:161:0x027c, code skipped:
            r0 = null;
            r13 = null;
     */
        /* JADX WARNING: Missing block: B:162:0x027e, code skipped:
            r14 = r0;
            r15 = r13;
            r13 = null;
     */
        /* JADX WARNING: Missing block: B:164:0x0288, code skipped:
            if (r0.startsWith("http") == false) goto L_0x028a;
     */
        /* JADX WARNING: Missing block: B:165:0x028a, code skipped:
            r13 = null;
            r14 = null;
     */
        /* JADX WARNING: Missing block: B:166:0x028d, code skipped:
            r13 = null;
            r14 = null;
            r15 = null;
            r16 = 1;
     */
        /* JADX WARNING: Missing block: B:167:0x0292, code skipped:
            r11 = new android.graphics.BitmapFactory.Options();
            r11.inSampleSize = 1;
     */
        /* JADX WARNING: Missing block: B:168:0x029d, code skipped:
            if (android.os.Build.VERSION.SDK_INT < 21) goto L_0x029f;
     */
        /* JADX WARNING: Missing block: B:169:0x029f, code skipped:
            r11.inPurgeable = true;
     */
        /* JADX WARNING: Missing block: B:170:0x02a1, code skipped:
            r19 = org.telegram.messenger.ImageLoader.access$1900(r1.this$0);
     */
        /* JADX WARNING: Missing block: B:173:0x02af, code skipped:
            if (r1.cacheImage.filter != null) goto L_0x02b1;
     */
        /* JADX WARNING: Missing block: B:174:0x02b1, code skipped:
            r0 = r1.cacheImage.filter.split("_");
     */
        /* JADX WARNING: Missing block: B:175:0x02bc, code skipped:
            if (r0.length >= 2) goto L_0x02be;
     */
        /* JADX WARNING: Missing block: B:177:0x02c6, code skipped:
            r4 = java.lang.Float.parseFloat(r0[0]) * org.telegram.messenger.AndroidUtilities.density;
     */
        /* JADX WARNING: Missing block: B:180:0x02d0, code skipped:
            r22 = java.lang.Float.parseFloat(r0[1]) * org.telegram.messenger.AndroidUtilities.density;
     */
        /* JADX WARNING: Missing block: B:181:0x02d5, code skipped:
            r0 = th;
     */
        /* JADX WARNING: Missing block: B:182:0x02d6, code skipped:
            r7 = r12;
            r25 = r13;
            r3 = false;
     */
        /* JADX WARNING: Missing block: B:183:0x02dc, code skipped:
            r4 = 0.0f;
            r22 = 0.0f;
     */
        /* JADX WARNING: Missing block: B:186:0x02e9, code skipped:
            if (r1.cacheImage.filter.contains("b2") != false) goto L_0x02eb;
     */
        /* JADX WARNING: Missing block: B:187:0x02eb, code skipped:
            r3 = 3;
     */
        /* JADX WARNING: Missing block: B:189:0x02f7, code skipped:
            if (r1.cacheImage.filter.contains("b1") != false) goto L_0x02f9;
     */
        /* JADX WARNING: Missing block: B:190:0x02f9, code skipped:
            r3 = 2;
     */
        /* JADX WARNING: Missing block: B:192:0x0305, code skipped:
            if (r1.cacheImage.filter.contains("b") != false) goto L_0x0307;
     */
        /* JADX WARNING: Missing block: B:193:0x0307, code skipped:
            r3 = 1;
     */
        /* JADX WARNING: Missing block: B:194:0x0309, code skipped:
            r3 = 0;
     */
        /* JADX WARNING: Missing block: B:196:?, code skipped:
            r5 = r1.cacheImage.filter.contains("i");
     */
        /* JADX WARNING: Missing block: B:198:?, code skipped:
            r7 = "f";
     */
        /* JADX WARNING: Missing block: B:199:0x031e, code skipped:
            if (r1.cacheImage.filter.contains(r7) != false) goto L_0x0320;
     */
        /* JADX WARNING: Missing block: B:200:0x0320, code skipped:
            r19 = true;
     */
        /* JADX WARNING: Missing block: B:201:0x0322, code skipped:
            if (r12 != null) goto L_0x0400;
     */
        /* JADX WARNING: Missing block: B:206:0x032c, code skipped:
            r11.inJustDecodeBounds = true;
     */
        /* JADX WARNING: Missing block: B:207:0x032e, code skipped:
            if (r14 == null) goto L_0x035f;
     */
        /* JADX WARNING: Missing block: B:209:0x0332, code skipped:
            if (r15 != null) goto L_0x0334;
     */
        /* JADX WARNING: Missing block: B:212:0x033a, code skipped:
            r7 = r12;
            r25 = r13;
     */
        /* JADX WARNING: Missing block: B:214:?, code skipped:
            android.provider.MediaStore.Video.Thumbnails.getThumbnail(org.telegram.messenger.ApplicationLoader.applicationContext.getContentResolver(), r14.longValue(), 1, r11);
     */
        /* JADX WARNING: Missing block: B:215:0x0345, code skipped:
            r0 = th;
     */
        /* JADX WARNING: Missing block: B:216:0x0346, code skipped:
            r7 = r12;
            r25 = r13;
     */
        /* JADX WARNING: Missing block: B:217:0x034b, code skipped:
            r7 = r12;
            r25 = r13;
            android.provider.MediaStore.Images.Thumbnails.getThumbnail(org.telegram.messenger.ApplicationLoader.applicationContext.getContentResolver(), r14.longValue(), 1, r11);
     */
        /* JADX WARNING: Missing block: B:218:0x035b, code skipped:
            r26 = r3;
     */
        /* JADX WARNING: Missing block: B:219:0x035f, code skipped:
            r7 = r12;
            r25 = r13;
     */
        /* JADX WARNING: Missing block: B:220:0x0362, code skipped:
            if (r9 != null) goto L_0x0364;
     */
        /* JADX WARNING: Missing block: B:221:0x0364, code skipped:
            r0 = new java.io.RandomAccessFile(r2, "r");
            r13 = (int) r0.length();
            r12 = (byte[]) org.telegram.messenger.ImageLoader.access$1600().get();
     */
        /* JADX WARNING: Missing block: B:222:0x037a, code skipped:
            if (r12 == null) goto L_0x0380;
     */
        /* JADX WARNING: Missing block: B:226:0x0380, code skipped:
            r12 = null;
     */
        /* JADX WARNING: Missing block: B:227:0x0381, code skipped:
            if (r12 == null) goto L_0x0383;
     */
        /* JADX WARNING: Missing block: B:228:0x0383, code skipped:
            r12 = new byte[r13];
            org.telegram.messenger.ImageLoader.access$1600().set(r12);
     */
        /* JADX WARNING: Missing block: B:229:0x038c, code skipped:
            r0.readFully(r12, 0, r13);
            r0.close();
            org.telegram.messenger.secretmedia.EncryptedFileInputStream.decryptBytesWithKeyFile(r12, 0, r13, r9);
            r0 = org.telegram.messenger.Utilities.computeSHA256(r12, 0, r13);
     */
        /* JADX WARNING: Missing block: B:230:0x039a, code skipped:
            if (r10 == null) goto L_0x03a7;
     */
        /* JADX WARNING: Missing block: B:233:0x03a3, code skipped:
            r26 = r3;
            r0 = null;
     */
        /* JADX WARNING: Missing block: B:234:0x03a7, code skipped:
            r26 = r3;
            r0 = 1;
     */
        /* JADX WARNING: Missing block: B:237:?, code skipped:
            r3 = r12[0] & 255;
            r13 = r13 - r3;
     */
        /* JADX WARNING: Missing block: B:238:0x03b0, code skipped:
            if (r0 == null) goto L_0x03b2;
     */
        /* JADX WARNING: Missing block: B:239:0x03b2, code skipped:
            android.graphics.BitmapFactory.decodeByteArray(r12, r3, r13, r11);
     */
        /* JADX WARNING: Missing block: B:240:0x03b6, code skipped:
            r0 = th;
     */
        /* JADX WARNING: Missing block: B:241:0x03b7, code skipped:
            r26 = r3;
     */
        /* JADX WARNING: Missing block: B:242:0x03ba, code skipped:
            r26 = r3;
     */
        /* JADX WARNING: Missing block: B:243:0x03bc, code skipped:
            if (r8 != null) goto L_0x03be;
     */
        /* JADX WARNING: Missing block: B:244:0x03be, code skipped:
            r0 = new org.telegram.messenger.secretmedia.EncryptedFileInputStream(r2, r1.cacheImage.encryptionKeyPath);
     */
        /* JADX WARNING: Missing block: B:245:0x03c8, code skipped:
            r0 = new java.io.FileInputStream(r2);
     */
        /* JADX WARNING: Missing block: B:246:0x03cd, code skipped:
            android.graphics.BitmapFactory.decodeStream(r0, null, r11);
            r0.close();
     */
        /* JADX WARNING: Missing block: B:247:0x03d4, code skipped:
            r0 = (float) r11.outWidth;
            r3 = (float) r11.outHeight;
     */
        /* JADX WARNING: Missing block: B:248:0x03dc, code skipped:
            if (r4 <= r22) goto L_0x03ea;
     */
        /* JADX WARNING: Missing block: B:251:0x03e2, code skipped:
            r0 = java.lang.Math.max(r0 / r4, r3 / r22);
     */
        /* JADX WARNING: Missing block: B:252:0x03ea, code skipped:
            r0 = java.lang.Math.min(r0 / r4, r3 / r22);
     */
        /* JADX WARNING: Missing block: B:254:0x03f3, code skipped:
            if (r0 < 1.0f) goto L_0x03f5;
     */
        /* JADX WARNING: Missing block: B:255:0x03f5, code skipped:
            r0 = 1.0f;
     */
        /* JADX WARNING: Missing block: B:256:0x03f7, code skipped:
            r11.inJustDecodeBounds = false;
            r11.inSampleSize = (int) r0;
     */
        /* JADX WARNING: Missing block: B:257:0x03fe, code skipped:
            r0 = th;
     */
        /* JADX WARNING: Missing block: B:258:0x0400, code skipped:
            r26 = r3;
            r7 = r12;
            r25 = r13;
     */
        /* JADX WARNING: Missing block: B:259:0x0405, code skipped:
            r0 = r5;
            r5 = null;
     */
        /* JADX WARNING: Missing block: B:260:0x0409, code skipped:
            r0 = th;
     */
        /* JADX WARNING: Missing block: B:261:0x040a, code skipped:
            r26 = r3;
            r7 = r12;
            r25 = r13;
     */
        /* JADX WARNING: Missing block: B:262:0x040f, code skipped:
            r3 = r5;
     */
        /* JADX WARNING: Missing block: B:263:0x0411, code skipped:
            r0 = th;
     */
        /* JADX WARNING: Missing block: B:264:0x0412, code skipped:
            r26 = r3;
            r7 = r12;
            r25 = r13;
            r3 = false;
     */
        /* JADX WARNING: Missing block: B:265:0x0418, code skipped:
            r5 = null;
     */
        /* JADX WARNING: Missing block: B:266:0x041b, code skipped:
            r0 = th;
     */
        /* JADX WARNING: Missing block: B:267:0x041c, code skipped:
            r7 = r12;
            r25 = r13;
            r3 = false;
            r5 = null;
     */
        /* JADX WARNING: Missing block: B:268:0x0422, code skipped:
            r7 = r12;
            r25 = r13;
     */
        /* JADX WARNING: Missing block: B:269:0x0425, code skipped:
            if (r25 != null) goto L_0x0427;
     */
        /* JADX WARNING: Missing block: B:272:?, code skipped:
            r11.inJustDecodeBounds = true;
     */
        /* JADX WARNING: Missing block: B:273:0x042a, code skipped:
            if (r19 != false) goto L_0x042c;
     */
        /* JADX WARNING: Missing block: B:274:0x042c, code skipped:
            r0 = android.graphics.Bitmap.Config.ARGB_8888;
     */
        /* JADX WARNING: Missing block: B:275:0x042f, code skipped:
            r0 = android.graphics.Bitmap.Config.RGB_565;
     */
        /* JADX WARNING: Missing block: B:276:0x0431, code skipped:
            r11.inPreferredConfig = r0;
            r0 = new java.io.FileInputStream(r2);
            r5 = android.graphics.BitmapFactory.decodeStream(r0, null, r11);
     */
        /* JADX WARNING: Missing block: B:278:?, code skipped:
            r0.close();
            r0 = r11.outWidth;
            r3 = r11.outHeight;
            r11.inJustDecodeBounds = false;
            r0 = (float) java.lang.Math.max(r0 / 200, r3 / 200);
     */
        /* JADX WARNING: Missing block: B:279:0x0452, code skipped:
            if (r0 < 1.0f) goto L_0x0454;
     */
        /* JADX WARNING: Missing block: B:280:0x0454, code skipped:
            r0 = 1.0f;
     */
        /* JADX WARNING: Missing block: B:281:0x0456, code skipped:
            r3 = 1;
     */
        /* JADX WARNING: Missing block: B:282:0x0457, code skipped:
            r3 = r3 * 2;
     */
        /* JADX WARNING: Missing block: B:283:0x045f, code skipped:
            if (((float) (r3 * 2)) < r0) goto L_0x0457;
     */
        /* JADX WARNING: Missing block: B:284:0x0461, code skipped:
            r11.inSampleSize = r3;
     */
        /* JADX WARNING: Missing block: B:285:0x0463, code skipped:
            r0 = false;
            r4 = 0.0f;
     */
        /* JADX WARNING: Missing block: B:286:0x0466, code skipped:
            r0 = th;
     */
        /* JADX WARNING: Missing block: B:287:0x0467, code skipped:
            r3 = false;
            r4 = 0.0f;
     */
        /* JADX WARNING: Missing block: B:288:0x046a, code skipped:
            r0 = th;
     */
        /* JADX WARNING: Missing block: B:289:0x046c, code skipped:
            r0 = false;
            r4 = 0.0f;
            r5 = null;
     */
        /* JADX WARNING: Missing block: B:290:0x046f, code skipped:
            r22 = 0.0f;
            r26 = 0;
     */
        /* JADX WARNING: Missing block: B:291:0x0473, code skipped:
            r3 = r0;
     */
        /* JADX WARNING: Missing block: B:292:0x0475, code skipped:
            r0 = th;
     */
        /* JADX WARNING: Missing block: B:293:0x0476, code skipped:
            r7 = r12;
            r25 = r13;
     */
        /* JADX WARNING: Missing block: B:294:0x0479, code skipped:
            r3 = false;
            r4 = 0.0f;
     */
        /* JADX WARNING: Missing block: B:295:0x047b, code skipped:
            r5 = null;
     */
        /* JADX WARNING: Missing block: B:296:0x047c, code skipped:
            r22 = 0.0f;
     */
        /* JADX WARNING: Missing block: B:297:0x047e, code skipped:
            r26 = 0;
     */
        /* JADX WARNING: Missing block: B:298:0x0480, code skipped:
            org.telegram.messenger.FileLog.e(r0);
     */
        /* JADX WARNING: Missing block: B:299:0x0483, code skipped:
            r6 = r22;
            r0 = r26;
            r21 = r14;
     */
        /* JADX WARNING: Missing block: B:300:0x048e, code skipped:
            if (r1.cacheImage.imageType == 1) goto L_0x0490;
     */
        /* JADX WARNING: Missing block: B:302:?, code skipped:
            org.telegram.messenger.ImageLoader.access$2002(r1.this$0, java.lang.System.currentTimeMillis());
     */
        /* JADX WARNING: Missing block: B:303:0x049b, code skipped:
            monitor-enter(r1.sync);
     */
        /* JADX WARNING: Missing block: B:306:0x049e, code skipped:
            if (r1.isCancelled != false) goto L_0x04a0;
     */
        /* JADX WARNING: Missing block: B:308:0x04a1, code skipped:
            return;
     */
        /* JADX WARNING: Missing block: B:310:0x04a3, code skipped:
            if (r7 == null) goto L_0x04ea;
     */
        /* JADX WARNING: Missing block: B:312:?, code skipped:
            r6 = new java.io.RandomAccessFile(r2, "r");
            r7 = r6.getChannel().map(java.nio.channels.FileChannel.MapMode.READ_ONLY, 0, r2.length());
            r8 = new android.graphics.BitmapFactory.Options();
            r8.inJustDecodeBounds = true;
            org.telegram.messenger.Utilities.loadWebpImage(null, r7, r7.limit(), r8, true);
            r5 = org.telegram.messenger.Bitmaps.createBitmap(r8.outWidth, r8.outHeight, android.graphics.Bitmap.Config.ARGB_8888);
            r8 = r7.limit();
     */
        /* JADX WARNING: Missing block: B:313:0x04dc, code skipped:
            if (r11.inPurgeable != false) goto L_0x04e0;
     */
        /* JADX WARNING: Missing block: B:314:0x04de, code skipped:
            r9 = true;
     */
        /* JADX WARNING: Missing block: B:315:0x04e0, code skipped:
            r9 = false;
     */
        /* JADX WARNING: Missing block: B:316:0x04e1, code skipped:
            org.telegram.messenger.Utilities.loadWebpImage(r5, r7, r8, null, r9);
            r6.close();
     */
        /* JADX WARNING: Missing block: B:318:0x04ec, code skipped:
            if (r11.inPurgeable != false) goto L_0x050b;
     */
        /* JADX WARNING: Missing block: B:319:0x04ee, code skipped:
            if (r9 == null) goto L_0x04f1;
     */
        /* JADX WARNING: Missing block: B:321:0x04f1, code skipped:
            if (r8 == null) goto L_0x04fd;
     */
        /* JADX WARNING: Missing block: B:322:0x04f3, code skipped:
            r6 = new org.telegram.messenger.secretmedia.EncryptedFileInputStream(r2, r1.cacheImage.encryptionKeyPath);
     */
        /* JADX WARNING: Missing block: B:323:0x04fd, code skipped:
            r6 = new java.io.FileInputStream(r2);
     */
        /* JADX WARNING: Missing block: B:324:0x0502, code skipped:
            r5 = android.graphics.BitmapFactory.decodeStream(r6, null, r11);
            r6.close();
     */
        /* JADX WARNING: Missing block: B:325:0x050b, code skipped:
            r6 = new java.io.RandomAccessFile(r2, "r");
            r7 = (int) r6.length();
            r12 = (byte[]) org.telegram.messenger.ImageLoader.access$2100().get();
     */
        /* JADX WARNING: Missing block: B:326:0x0521, code skipped:
            if (r12 == null) goto L_0x0527;
     */
        /* JADX WARNING: Missing block: B:328:0x0524, code skipped:
            if (r12.length < r7) goto L_0x0527;
     */
        /* JADX WARNING: Missing block: B:330:0x0527, code skipped:
            r12 = null;
     */
        /* JADX WARNING: Missing block: B:331:0x0528, code skipped:
            if (r12 != null) goto L_0x0533;
     */
        /* JADX WARNING: Missing block: B:332:0x052a, code skipped:
            r12 = new byte[r7];
            org.telegram.messenger.ImageLoader.access$2100().set(r12);
     */
        /* JADX WARNING: Missing block: B:333:0x0533, code skipped:
            r6.readFully(r12, 0, r7);
            r6.close();
     */
        /* JADX WARNING: Missing block: B:334:0x053a, code skipped:
            if (r9 == null) goto L_0x0556;
     */
        /* JADX WARNING: Missing block: B:335:0x053c, code skipped:
            org.telegram.messenger.secretmedia.EncryptedFileInputStream.decryptBytesWithKeyFile(r12, 0, r7, r9);
            r6 = org.telegram.messenger.Utilities.computeSHA256(r12, 0, r7);
     */
        /* JADX WARNING: Missing block: B:336:0x0543, code skipped:
            if (r10 == null) goto L_0x054e;
     */
        /* JADX WARNING: Missing block: B:338:0x0549, code skipped:
            if (java.util.Arrays.equals(r6, r10) != false) goto L_0x054c;
     */
        /* JADX WARNING: Missing block: B:340:0x054c, code skipped:
            r6 = null;
     */
        /* JADX WARNING: Missing block: B:341:0x054e, code skipped:
            r6 = 1;
     */
        /* JADX WARNING: Missing block: B:342:0x054f, code skipped:
            r8 = r12[0] & 255;
            r7 = r7 - r8;
     */
        /* JADX WARNING: Missing block: B:343:0x0556, code skipped:
            if (r8 == null) goto L_0x0560;
     */
        /* JADX WARNING: Missing block: B:344:0x0558, code skipped:
            org.telegram.messenger.secretmedia.EncryptedFileInputStream.decryptBytesWithKeyFile(r12, 0, r7, r1.cacheImage.encryptionKeyPath);
     */
        /* JADX WARNING: Missing block: B:345:0x0560, code skipped:
            r6 = null;
            r8 = 0;
     */
        /* JADX WARNING: Missing block: B:346:0x0562, code skipped:
            if (r6 != null) goto L_0x0568;
     */
        /* JADX WARNING: Missing block: B:347:0x0564, code skipped:
            r5 = android.graphics.BitmapFactory.decodeByteArray(r12, r8, r7, r11);
     */
        /* JADX WARNING: Missing block: B:348:0x0568, code skipped:
            if (r5 != null) goto L_0x0581;
     */
        /* JADX WARNING: Missing block: B:350:0x0572, code skipped:
            if (r2.length() == 0) goto L_0x057a;
     */
        /* JADX WARNING: Missing block: B:352:0x0578, code skipped:
            if (r1.cacheImage.filter != null) goto L_0x057d;
     */
        /* JADX WARNING: Missing block: B:353:0x057a, code skipped:
            r2.delete();
     */
        /* JADX WARNING: Missing block: B:354:0x057d, code skipped:
            r2 = r5;
            r7 = false;
     */
        /* JADX WARNING: Missing block: B:356:0x0585, code skipped:
            if (r1.cacheImage.filter == null) goto L_0x05b4;
     */
        /* JADX WARNING: Missing block: B:357:0x0587, code skipped:
            r2 = (float) r5.getWidth();
            r6 = (float) r5.getHeight();
     */
        /* JADX WARNING: Missing block: B:358:0x0593, code skipped:
            if (r11.inPurgeable != false) goto L_0x05b4;
     */
        /* JADX WARNING: Missing block: B:360:0x0597, code skipped:
            if (r4 == 0.0f) goto L_0x05b4;
     */
        /* JADX WARNING: Missing block: B:362:0x059b, code skipped:
            if (r2 == r4) goto L_0x05b4;
     */
        /* JADX WARNING: Missing block: B:364:0x05a3, code skipped:
            if (r2 <= (r4 + 20.0f)) goto L_0x05b4;
     */
        /* JADX WARNING: Missing block: B:365:0x05a5, code skipped:
            r2 = org.telegram.messenger.Bitmaps.createScaledBitmap(r5, (int) r4, (int) (r6 / (r2 / r4)), true);
     */
        /* JADX WARNING: Missing block: B:366:0x05ae, code skipped:
            if (r5 == r2) goto L_0x05b4;
     */
        /* JADX WARNING: Missing block: B:367:0x05b0, code skipped:
            r5.recycle();
     */
        /* JADX WARNING: Missing block: B:368:0x05b4, code skipped:
            r2 = r5;
     */
        /* JADX WARNING: Missing block: B:369:0x05b5, code skipped:
            if (r3 == false) goto L_0x05d8;
     */
        /* JADX WARNING: Missing block: B:372:0x05b9, code skipped:
            if (r11.inPurgeable == false) goto L_0x05bd;
     */
        /* JADX WARNING: Missing block: B:373:0x05bb, code skipped:
            r3 = 0;
     */
        /* JADX WARNING: Missing block: B:374:0x05bd, code skipped:
            r3 = 1;
     */
        /* JADX WARNING: Missing block: B:376:0x05ce, code skipped:
            if (org.telegram.messenger.Utilities.needInvert(r2, r3, r2.getWidth(), r2.getHeight(), r2.getRowBytes()) == 0) goto L_0x05d2;
     */
        /* JADX WARNING: Missing block: B:377:0x05d0, code skipped:
            r7 = true;
     */
        /* JADX WARNING: Missing block: B:378:0x05d2, code skipped:
            r7 = false;
     */
        /* JADX WARNING: Missing block: B:379:0x05d3, code skipped:
            r3 = 1;
     */
        /* JADX WARNING: Missing block: B:380:0x05d5, code skipped:
            r0 = th;
     */
        /* JADX WARNING: Missing block: B:381:0x05d8, code skipped:
            r3 = 1;
            r7 = false;
     */
        /* JADX WARNING: Missing block: B:382:0x05da, code skipped:
            if (r0 != r3) goto L_0x0601;
     */
        /* JADX WARNING: Missing block: B:385:0x05e2, code skipped:
            if (r2.getConfig() != android.graphics.Bitmap.Config.ARGB_8888) goto L_0x068d;
     */
        /* JADX WARNING: Missing block: B:387:0x05e7, code skipped:
            if (r11.inPurgeable == false) goto L_0x05eb;
     */
        /* JADX WARNING: Missing block: B:388:0x05e9, code skipped:
            r14 = 0;
     */
        /* JADX WARNING: Missing block: B:389:0x05eb, code skipped:
            r14 = 1;
     */
        /* JADX WARNING: Missing block: B:390:0x05ec, code skipped:
            org.telegram.messenger.Utilities.blurBitmap(r2, 3, r14, r2.getWidth(), r2.getHeight(), r2.getRowBytes());
     */
        /* JADX WARNING: Missing block: B:391:0x05fe, code skipped:
            r0 = th;
     */
        /* JADX WARNING: Missing block: B:394:0x0602, code skipped:
            if (r0 != 2) goto L_0x0626;
     */
        /* JADX WARNING: Missing block: B:396:0x060a, code skipped:
            if (r2.getConfig() != android.graphics.Bitmap.Config.ARGB_8888) goto L_0x068d;
     */
        /* JADX WARNING: Missing block: B:398:0x060f, code skipped:
            if (r11.inPurgeable == false) goto L_0x0613;
     */
        /* JADX WARNING: Missing block: B:399:0x0611, code skipped:
            r14 = 0;
     */
        /* JADX WARNING: Missing block: B:400:0x0613, code skipped:
            r14 = 1;
     */
        /* JADX WARNING: Missing block: B:401:0x0614, code skipped:
            org.telegram.messenger.Utilities.blurBitmap(r2, 1, r14, r2.getWidth(), r2.getHeight(), r2.getRowBytes());
     */
        /* JADX WARNING: Missing block: B:403:0x0627, code skipped:
            if (r0 != 3) goto L_0x067a;
     */
        /* JADX WARNING: Missing block: B:405:0x062f, code skipped:
            if (r2.getConfig() != android.graphics.Bitmap.Config.ARGB_8888) goto L_0x068d;
     */
        /* JADX WARNING: Missing block: B:407:0x0634, code skipped:
            if (r11.inPurgeable == false) goto L_0x0638;
     */
        /* JADX WARNING: Missing block: B:408:0x0636, code skipped:
            r14 = 0;
     */
        /* JADX WARNING: Missing block: B:409:0x0638, code skipped:
            r14 = 1;
     */
        /* JADX WARNING: Missing block: B:410:0x0639, code skipped:
            org.telegram.messenger.Utilities.blurBitmap(r2, 7, r14, r2.getWidth(), r2.getHeight(), r2.getRowBytes());
     */
        /* JADX WARNING: Missing block: B:411:0x064c, code skipped:
            if (r11.inPurgeable == false) goto L_0x0650;
     */
        /* JADX WARNING: Missing block: B:412:0x064e, code skipped:
            r14 = 0;
     */
        /* JADX WARNING: Missing block: B:413:0x0650, code skipped:
            r14 = 1;
     */
        /* JADX WARNING: Missing block: B:414:0x0651, code skipped:
            org.telegram.messenger.Utilities.blurBitmap(r2, 7, r14, r2.getWidth(), r2.getHeight(), r2.getRowBytes());
     */
        /* JADX WARNING: Missing block: B:415:0x0664, code skipped:
            if (r11.inPurgeable == false) goto L_0x0668;
     */
        /* JADX WARNING: Missing block: B:416:0x0666, code skipped:
            r14 = 0;
     */
        /* JADX WARNING: Missing block: B:417:0x0668, code skipped:
            r14 = 1;
     */
        /* JADX WARNING: Missing block: B:418:0x0669, code skipped:
            org.telegram.messenger.Utilities.blurBitmap(r2, 7, r14, r2.getWidth(), r2.getHeight(), r2.getRowBytes());
     */
        /* JADX WARNING: Missing block: B:419:0x067a, code skipped:
            if (r0 != 0) goto L_0x068d;
     */
        /* JADX WARNING: Missing block: B:421:0x067e, code skipped:
            if (r11.inPurgeable == false) goto L_0x068d;
     */
        /* JADX WARNING: Missing block: B:422:0x0680, code skipped:
            org.telegram.messenger.Utilities.pinBitmap(r2);
     */
        /* JADX WARNING: Missing block: B:428:0x0687, code skipped:
            r0 = th;
     */
        /* JADX WARNING: Missing block: B:429:0x0688, code skipped:
            r2 = r5;
     */
        /* JADX WARNING: Missing block: B:430:0x0689, code skipped:
            r7 = false;
     */
        /* JADX WARNING: Missing block: B:431:0x068a, code skipped:
            org.telegram.messenger.FileLog.e(r0);
     */
        /* JADX WARNING: Missing block: B:432:0x068d, code skipped:
            r0 = 0;
            r12 = null;
     */
        /* JADX WARNING: Missing block: B:433:0x0691, code skipped:
            r12 = 20;
     */
        /* JADX WARNING: Missing block: B:434:0x0693, code skipped:
            if (r21 != null) goto L_0x0695;
     */
        /* JADX WARNING: Missing block: B:435:0x0695, code skipped:
            r12 = 0;
     */
        /* JADX WARNING: Missing block: B:436:0x0696, code skipped:
            if (r12 != 0) goto L_0x0698;
     */
        /* JADX WARNING: Missing block: B:439:0x06a2, code skipped:
            if (org.telegram.messenger.ImageLoader.access$2000(r1.this$0) != 0) goto L_0x06a4;
     */
        /* JADX WARNING: Missing block: B:441:0x06ae, code skipped:
            r30 = r5;
            r23 = r6;
            r5 = (long) r12;
     */
        /* JADX WARNING: Missing block: B:442:0x06b7, code skipped:
            if (org.telegram.messenger.ImageLoader.access$2000(r1.this$0) > (java.lang.System.currentTimeMillis() - r5)) goto L_0x06b9;
     */
        /* JADX WARNING: Missing block: B:445:0x06bd, code skipped:
            if (android.os.Build.VERSION.SDK_INT < 21) goto L_0x06bf;
     */
        /* JADX WARNING: Missing block: B:446:0x06bf, code skipped:
            java.lang.Thread.sleep(r5);
     */
        /* JADX WARNING: Missing block: B:447:0x06c3, code skipped:
            r30 = r5;
     */
        /* JADX WARNING: Missing block: B:449:0x06c5, code skipped:
            r2 = r30;
     */
        /* JADX WARNING: Missing block: B:451:0x06c7, code skipped:
            r7 = false;
            r12 = null;
     */
        /* JADX WARNING: Missing block: B:452:0x06cb, code skipped:
            r30 = r5;
            r23 = r6;
     */
        /* JADX WARNING: Missing block: B:454:?, code skipped:
            org.telegram.messenger.ImageLoader.access$2002(r1.this$0, java.lang.System.currentTimeMillis());
     */
        /* JADX WARNING: Missing block: B:455:0x06da, code skipped:
            monitor-enter(r1.sync);
     */
        /* JADX WARNING: Missing block: B:458:0x06dd, code skipped:
            if (r1.isCancelled != false) goto L_0x06df;
     */
        /* JADX WARNING: Missing block: B:460:0x06e0, code skipped:
            return;
     */
        /* JADX WARNING: Missing block: B:462:0x06e2, code skipped:
            if (r19 != false) goto L_0x06fa;
     */
        /* JADX WARNING: Missing block: B:465:0x06e8, code skipped:
            if (r1.cacheImage.filter == null) goto L_0x06fa;
     */
        /* JADX WARNING: Missing block: B:466:0x06ea, code skipped:
            if (r0 != 0) goto L_0x06fa;
     */
        /* JADX WARNING: Missing block: B:468:0x06f2, code skipped:
            if (r1.cacheImage.imageLocation.path == null) goto L_0x06f5;
     */
        /* JADX WARNING: Missing block: B:470:0x06f5, code skipped:
            r11.inPreferredConfig = android.graphics.Bitmap.Config.RGB_565;
     */
        /* JADX WARNING: Missing block: B:472:?, code skipped:
            r11.inPreferredConfig = android.graphics.Bitmap.Config.ARGB_8888;
     */
        /* JADX WARNING: Missing block: B:473:0x06fe, code skipped:
            r11.inDither = false;
     */
        /* JADX WARNING: Missing block: B:474:0x0701, code skipped:
            if (r21 == null) goto L_0x0727;
     */
        /* JADX WARNING: Missing block: B:475:0x0703, code skipped:
            if (r25 != null) goto L_0x0727;
     */
        /* JADX WARNING: Missing block: B:476:0x0705, code skipped:
            if (r15 == null) goto L_0x0717;
     */
        /* JADX WARNING: Missing block: B:478:?, code skipped:
            r5 = android.provider.MediaStore.Video.Thumbnails.getThumbnail(org.telegram.messenger.ApplicationLoader.applicationContext.getContentResolver(), r21.longValue(), 1, r11);
     */
        /* JADX WARNING: Missing block: B:479:0x0717, code skipped:
            r5 = android.provider.MediaStore.Images.Thumbnails.getThumbnail(org.telegram.messenger.ApplicationLoader.applicationContext.getContentResolver(), r21.longValue(), 1, r11);
     */
        /* JADX WARNING: Missing block: B:480:0x0727, code skipped:
            r5 = r30;
     */
        /* JADX WARNING: Missing block: B:481:0x0729, code skipped:
            if (r5 != null) goto L_0x083c;
     */
        /* JADX WARNING: Missing block: B:482:0x072b, code skipped:
            if (r7 == null) goto L_0x077b;
     */
        /* JADX WARNING: Missing block: B:484:?, code skipped:
            r6 = new java.io.RandomAccessFile(r2, "r");
            r7 = r6.getChannel().map(java.nio.channels.FileChannel.MapMode.READ_ONLY, 0, r2.length());
            r8 = new android.graphics.BitmapFactory.Options();
            r8.inJustDecodeBounds = true;
     */
        /* JADX WARNING: Missing block: B:485:0x0750, code skipped:
            r12 = null;
     */
        /* JADX WARNING: Missing block: B:487:?, code skipped:
            org.telegram.messenger.Utilities.loadWebpImage(null, r7, r7.limit(), r8, true);
     */
        /* JADX WARNING: Missing block: B:489:?, code skipped:
            r5 = org.telegram.messenger.Bitmaps.createBitmap(r8.outWidth, r8.outHeight, android.graphics.Bitmap.Config.ARGB_8888);
            r8 = r7.limit();
     */
        /* JADX WARNING: Missing block: B:490:0x0764, code skipped:
            if (r11.inPurgeable != false) goto L_0x0768;
     */
        /* JADX WARNING: Missing block: B:491:0x0766, code skipped:
            r9 = true;
     */
        /* JADX WARNING: Missing block: B:492:0x0768, code skipped:
            r9 = false;
     */
        /* JADX WARNING: Missing block: B:495:?, code skipped:
            org.telegram.messenger.Utilities.loadWebpImage(r5, r7, r8, null, r9);
     */
        /* JADX WARNING: Missing block: B:497:?, code skipped:
            r6.close();
     */
        /* JADX WARNING: Missing block: B:498:0x0770, code skipped:
            r7 = 0;
            r12 = null;
     */
        /* JADX WARNING: Missing block: B:499:0x0774, code skipped:
            r2 = r5;
            r12 = null;
     */
        /* JADX WARNING: Missing block: B:501:0x0778, code skipped:
            r2 = r5;
     */
        /* JADX WARNING: Missing block: B:505:0x077d, code skipped:
            if (r11.inPurgeable != false) goto L_0x07d9;
     */
        /* JADX WARNING: Missing block: B:506:0x077f, code skipped:
            if (r9 == null) goto L_0x0782;
     */
        /* JADX WARNING: Missing block: B:507:0x0782, code skipped:
            if (r8 == null) goto L_0x078e;
     */
        /* JADX WARNING: Missing block: B:509:?, code skipped:
            r6 = new org.telegram.messenger.secretmedia.EncryptedFileInputStream(r2, r1.cacheImage.encryptionKeyPath);
     */
        /* JADX WARNING: Missing block: B:511:?, code skipped:
            r6 = new java.io.FileInputStream(r2);
     */
        /* JADX WARNING: Missing block: B:513:0x079b, code skipped:
            if ((r1.cacheImage.imageLocation.document instanceof org.telegram.tgnet.TLRPC.TL_document) == false) goto L_0x07cf;
     */
        /* JADX WARNING: Missing block: B:515:?, code skipped:
            r7 = new androidx.exifinterface.media.ExifInterface(r6).getAttributeInt("Orientation", 1);
     */
        /* JADX WARNING: Missing block: B:517:0x07aa, code skipped:
            if (r7 == 3) goto L_0x07ba;
     */
        /* JADX WARNING: Missing block: B:519:0x07ad, code skipped:
            if (r7 == 6) goto L_0x07b7;
     */
        /* JADX WARNING: Missing block: B:521:0x07b1, code skipped:
            if (r7 == 8) goto L_0x07b4;
     */
        /* JADX WARNING: Missing block: B:522:0x07b4, code skipped:
            r7 = 270;
     */
        /* JADX WARNING: Missing block: B:523:0x07b7, code skipped:
            r7 = 90;
     */
        /* JADX WARNING: Missing block: B:524:0x07ba, code skipped:
            r7 = 180;
     */
        /* JADX WARNING: Missing block: B:531:0x07cf, code skipped:
            r7 = 0;
     */
        /* JADX WARNING: Missing block: B:535:0x07d9, code skipped:
            r12 = null;
     */
        /* JADX WARNING: Missing block: B:537:?, code skipped:
            r6 = new java.io.RandomAccessFile(r2, "r");
            r7 = (int) r6.length();
            r13 = (byte[]) org.telegram.messenger.ImageLoader.access$1600().get();
     */
        /* JADX WARNING: Missing block: B:538:0x07f0, code skipped:
            if (r13 == null) goto L_0x07f6;
     */
        /* JADX WARNING: Missing block: B:540:0x07f3, code skipped:
            if (r13.length < r7) goto L_0x07f6;
     */
        /* JADX WARNING: Missing block: B:542:0x07f6, code skipped:
            r13 = null;
     */
        /* JADX WARNING: Missing block: B:543:0x07f7, code skipped:
            if (r13 != null) goto L_0x0802;
     */
        /* JADX WARNING: Missing block: B:544:0x07f9, code skipped:
            r13 = new byte[r7];
            org.telegram.messenger.ImageLoader.access$1600().set(r13);
     */
        /* JADX WARNING: Missing block: B:545:0x0802, code skipped:
            r6.readFully(r13, 0, r7);
            r6.close();
     */
        /* JADX WARNING: Missing block: B:546:0x0809, code skipped:
            if (r9 == null) goto L_0x0825;
     */
        /* JADX WARNING: Missing block: B:547:0x080b, code skipped:
            org.telegram.messenger.secretmedia.EncryptedFileInputStream.decryptBytesWithKeyFile(r13, 0, r7, r9);
            r6 = org.telegram.messenger.Utilities.computeSHA256(r13, 0, r7);
     */
        /* JADX WARNING: Missing block: B:548:0x0812, code skipped:
            if (r10 == null) goto L_0x081d;
     */
        /* JADX WARNING: Missing block: B:550:0x0818, code skipped:
            if (java.util.Arrays.equals(r6, r10) != false) goto L_0x081b;
     */
        /* JADX WARNING: Missing block: B:552:0x081b, code skipped:
            r6 = null;
     */
        /* JADX WARNING: Missing block: B:553:0x081d, code skipped:
            r6 = 1;
     */
        /* JADX WARNING: Missing block: B:554:0x081e, code skipped:
            r8 = r13[0] & 255;
            r7 = r7 - r8;
     */
        /* JADX WARNING: Missing block: B:555:0x0825, code skipped:
            if (r8 == null) goto L_0x082f;
     */
        /* JADX WARNING: Missing block: B:556:0x0827, code skipped:
            org.telegram.messenger.secretmedia.EncryptedFileInputStream.decryptBytesWithKeyFile(r13, 0, r7, r1.cacheImage.encryptionKeyPath);
     */
        /* JADX WARNING: Missing block: B:557:0x082f, code skipped:
            r6 = null;
            r8 = 0;
     */
        /* JADX WARNING: Missing block: B:558:0x0831, code skipped:
            if (r6 != null) goto L_0x083d;
     */
        /* JADX WARNING: Missing block: B:559:0x0833, code skipped:
            r5 = android.graphics.BitmapFactory.decodeByteArray(r13, r8, r7, r11);
     */
        /* JADX WARNING: Missing block: B:560:0x0838, code skipped:
            r12 = null;
     */
        /* JADX WARNING: Missing block: B:562:0x0839, code skipped:
            r2 = r5;
     */
        /* JADX WARNING: Missing block: B:564:0x083c, code skipped:
            r12 = null;
     */
        /* JADX WARNING: Missing block: B:565:0x083d, code skipped:
            r7 = 0;
     */
        /* JADX WARNING: Missing block: B:659:0x0945, code skipped:
            r12 = null;
     */
        /* JADX WARNING: Missing block: B:666:0x094b, code skipped:
            r12 = null;
     */
        /* JADX WARNING: Missing block: B:668:0x094d, code skipped:
            r2 = r30;
     */
        /* JADX WARNING: Missing block: B:670:0x094f, code skipped:
            r7 = false;
     */
        /* JADX WARNING: Missing block: B:671:0x0950, code skipped:
            r24 = 0;
     */
        /* JADX WARNING: Missing block: B:676:0x095c, code skipped:
            if (r2 != null) goto L_0x095e;
     */
        /* JADX WARNING: Missing block: B:677:0x095e, code skipped:
            r5 = new android.graphics.drawable.BitmapDrawable(r2);
     */
        /* JADX WARNING: Missing block: B:678:0x0964, code skipped:
            r5 = r12;
     */
        /* JADX WARNING: Missing block: B:679:0x0965, code skipped:
            onPostExecute(r5);
     */
        /* JADX WARNING: Missing block: B:681:0x096b, code skipped:
            r5 = new org.telegram.messenger.ExtendedBitmapDrawable(r2, r7, r0);
     */
        /* JADX WARNING: Missing block: B:682:0x0971, code skipped:
            r5 = r12;
     */
        public void run() {
            /*
            r35 = this;
            r1 = r35;
            r2 = r1.sync;
            monitor-enter(r2);
            r0 = java.lang.Thread.currentThread();	 Catch:{ all -> 0x0976 }
            r1.runningThread = r0;	 Catch:{ all -> 0x0976 }
            java.lang.Thread.interrupted();	 Catch:{ all -> 0x0976 }
            r0 = r1.isCancelled;	 Catch:{ all -> 0x0976 }
            if (r0 == 0) goto L_0x0014;
        L_0x0012:
            monitor-exit(r2);	 Catch:{ all -> 0x0976 }
            return;
        L_0x0014:
            monitor-exit(r2);	 Catch:{ all -> 0x0976 }
            r0 = r1.cacheImage;
            r2 = r0.imageLocation;
            r2 = r2.photoSize;
            r2 = r2 instanceof org.telegram.tgnet.TLRPC.TL_photoStrippedSize;
            r3 = 2;
            r4 = 3;
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
            r2 = r2 - r4;
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
            r11 = r11 - r4;
            java.lang.System.arraycopy(r9, r4, r8, r10, r11);
            r9 = org.telegram.messenger.Bitmaps.footer;
            r10 = org.telegram.messenger.Bitmaps.header;
            r10 = r10.length;
            r11 = r0.bytes;
            r11 = r11.length;
            r10 = r10 + r11;
            r10 = r10 - r4;
            r4 = org.telegram.messenger.Bitmaps.footer;
            r4 = r4.length;
            java.lang.System.arraycopy(r9, r7, r8, r10, r4);
            r4 = 164; // 0xa4 float:2.3E-43 double:8.1E-322;
            r0 = r0.bytes;
            r6 = r0[r6];
            r8[r4] = r6;
            r4 = 166; // 0xa6 float:2.33E-43 double:8.2E-322;
            r0 = r0[r3];
            r8[r4] = r0;
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
            goto L_0x0975;
        L_0x00c7:
            r0 = move-exception;
            monitor-exit(r2);	 Catch:{ all -> 0x00c7 }
            throw r0;
        L_0x00ca:
            r2 = r0.lottieFile;
            if (r2 == 0) goto L_0x0118;
        L_0x00ce:
            r2 = r1.sync;
            monitor-enter(r2);
            r0 = r1.isCancelled;	 Catch:{ all -> 0x0115 }
            if (r0 == 0) goto L_0x00d7;
        L_0x00d5:
            monitor-exit(r2);	 Catch:{ all -> 0x0115 }
            return;
        L_0x00d7:
            monitor-exit(r2);	 Catch:{ all -> 0x0115 }
            r2 = new com.airbnb.lottie.LottieDrawable;
            r2.<init>();
            r0 = new java.io.FileInputStream;	 Catch:{ Throwable -> 0x00ff }
            r3 = r1.cacheImage;	 Catch:{ Throwable -> 0x00ff }
            r3 = r3.finalFilePath;	 Catch:{ Throwable -> 0x00ff }
            r0.<init>(r3);	 Catch:{ Throwable -> 0x00ff }
            r3 = r1.cacheImage;	 Catch:{ Throwable -> 0x00ff }
            r3 = r3.finalFilePath;	 Catch:{ Throwable -> 0x00ff }
            r3 = r3.toString();	 Catch:{ Throwable -> 0x00ff }
            r3 = com.airbnb.lottie.LottieCompositionFactory.fromJsonInputStreamSync(r0, r3);	 Catch:{ Throwable -> 0x00ff }
            r3 = r3.getValue();	 Catch:{ Throwable -> 0x00ff }
            r3 = (com.airbnb.lottie.LottieComposition) r3;	 Catch:{ Throwable -> 0x00ff }
            r2.setComposition(r3);	 Catch:{ Throwable -> 0x00ff }
            r0.close();	 Catch:{ Throwable -> 0x00ff }
            goto L_0x0103;
        L_0x00ff:
            r0 = move-exception;
            org.telegram.messenger.FileLog.e(r0);
        L_0x0103:
            r2.setRepeatMode(r6);
            r0 = -1;
            r2.setRepeatCount(r0);
            r2.start();
            java.lang.Thread.interrupted();
            r1.onPostExecute(r2);
            goto L_0x0975;
        L_0x0115:
            r0 = move-exception;
            monitor-exit(r2);	 Catch:{ all -> 0x0115 }
            throw r0;
        L_0x0118:
            r2 = r0.animatedFile;
            if (r2 == 0) goto L_0x0180;
        L_0x011c:
            r2 = r1.sync;
            monitor-enter(r2);
            r0 = r1.isCancelled;	 Catch:{ all -> 0x017d }
            if (r0 == 0) goto L_0x0125;
        L_0x0123:
            monitor-exit(r2);	 Catch:{ all -> 0x017d }
            return;
        L_0x0125:
            monitor-exit(r2);	 Catch:{ all -> 0x017d }
            r0 = r1.cacheImage;
            r0 = r0.filter;
            r2 = "g";
            r0 = r2.equals(r0);
            if (r0 == 0) goto L_0x0157;
        L_0x0132:
            r0 = r1.cacheImage;
            r2 = r0.imageLocation;
            r2 = r2.document;
            r3 = r2 instanceof org.telegram.tgnet.TLRPC.TL_documentEncrypted;
            if (r3 != 0) goto L_0x0157;
        L_0x013c:
            r3 = new org.telegram.ui.Components.AnimatedFileDrawable;
            r7 = r0.finalFilePath;
            r8 = 0;
            r0 = r0.size;
            r9 = (long) r0;
            r0 = r2 instanceof org.telegram.tgnet.TLRPC.Document;
            if (r0 == 0) goto L_0x014a;
        L_0x0148:
            r11 = r2;
            goto L_0x014b;
        L_0x014a:
            r11 = 0;
        L_0x014b:
            r0 = r1.cacheImage;
            r12 = r0.parentObject;
            r13 = r0.currentAccount;
            r6 = r3;
            r6.<init>(r7, r8, r9, r11, r12, r13);
            r0 = r3;
            goto L_0x0175;
        L_0x0157:
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
        L_0x0175:
            java.lang.Thread.interrupted();
            r1.onPostExecute(r0);
            goto L_0x0975;
        L_0x017d:
            r0 = move-exception;
            monitor-exit(r2);	 Catch:{ all -> 0x017d }
            throw r0;
        L_0x0180:
            r2 = r0.finalFilePath;
            r8 = r0.secureDocument;
            if (r8 != 0) goto L_0x019b;
        L_0x0186:
            r0 = r0.encryptionKeyPath;
            if (r0 == 0) goto L_0x0199;
        L_0x018a:
            if (r2 == 0) goto L_0x0199;
        L_0x018c:
            r0 = r2.getAbsolutePath();
            r8 = ".enc";
            r0 = r0.endsWith(r8);
            if (r0 == 0) goto L_0x0199;
        L_0x0198:
            goto L_0x019b;
        L_0x0199:
            r8 = 0;
            goto L_0x019c;
        L_0x019b:
            r8 = 1;
        L_0x019c:
            r0 = r1.cacheImage;
            r0 = r0.secureDocument;
            if (r0 == 0) goto L_0x01b5;
        L_0x01a2:
            r9 = r0.secureDocumentKey;
            r0 = r0.secureFile;
            if (r0 == 0) goto L_0x01ad;
        L_0x01a8:
            r0 = r0.file_hash;
            if (r0 == 0) goto L_0x01ad;
        L_0x01ac:
            goto L_0x01b3;
        L_0x01ad:
            r0 = r1.cacheImage;
            r0 = r0.secureDocument;
            r0 = r0.fileHash;
        L_0x01b3:
            r10 = r0;
            goto L_0x01b7;
        L_0x01b5:
            r9 = 0;
            r10 = 0;
        L_0x01b7:
            r0 = android.os.Build.VERSION.SDK_INT;
            r11 = 19;
            if (r0 >= r11) goto L_0x0227;
        L_0x01bd:
            r11 = new java.io.RandomAccessFile;	 Catch:{ Exception -> 0x020d, all -> 0x0209 }
            r0 = "r";
            r11.<init>(r2, r0);	 Catch:{ Exception -> 0x020d, all -> 0x0209 }
            r0 = r1.cacheImage;	 Catch:{ Exception -> 0x0207 }
            r0 = r0.imageType;	 Catch:{ Exception -> 0x0207 }
            if (r0 != r6) goto L_0x01cf;
        L_0x01ca:
            r0 = org.telegram.messenger.ImageLoader.headerThumb;	 Catch:{ Exception -> 0x0207 }
            goto L_0x01d3;
        L_0x01cf:
            r0 = org.telegram.messenger.ImageLoader.header;	 Catch:{ Exception -> 0x0207 }
        L_0x01d3:
            r12 = r0.length;	 Catch:{ Exception -> 0x0207 }
            r11.readFully(r0, r7, r12);	 Catch:{ Exception -> 0x0207 }
            r12 = new java.lang.String;	 Catch:{ Exception -> 0x0207 }
            r12.<init>(r0);	 Catch:{ Exception -> 0x0207 }
            r0 = r12.toLowerCase();	 Catch:{ Exception -> 0x0207 }
            r0 = r0.toLowerCase();	 Catch:{ Exception -> 0x0207 }
            r12 = "riff";
            r12 = r0.startsWith(r12);	 Catch:{ Exception -> 0x0207 }
            if (r12 == 0) goto L_0x01f7;
        L_0x01ec:
            r12 = "webp";
            r0 = r0.endsWith(r12);	 Catch:{ Exception -> 0x0207 }
            if (r0 == 0) goto L_0x01f7;
        L_0x01f5:
            r12 = 1;
            goto L_0x01f8;
        L_0x01f7:
            r12 = 0;
        L_0x01f8:
            r11.close();	 Catch:{ Exception -> 0x0205 }
            r11.close();	 Catch:{ Exception -> 0x01ff }
            goto L_0x0228;
        L_0x01ff:
            r0 = move-exception;
            r11 = r0;
            org.telegram.messenger.FileLog.e(r11);
            goto L_0x0228;
        L_0x0205:
            r0 = move-exception;
            goto L_0x0210;
        L_0x0207:
            r0 = move-exception;
            goto L_0x020f;
        L_0x0209:
            r0 = move-exception;
            r2 = r0;
            r11 = 0;
            goto L_0x021b;
        L_0x020d:
            r0 = move-exception;
            r11 = 0;
        L_0x020f:
            r12 = 0;
        L_0x0210:
            org.telegram.messenger.FileLog.e(r0);	 Catch:{ all -> 0x0219 }
            if (r11 == 0) goto L_0x0228;
        L_0x0215:
            r11.close();	 Catch:{ Exception -> 0x01ff }
            goto L_0x0228;
        L_0x0219:
            r0 = move-exception;
            r2 = r0;
        L_0x021b:
            if (r11 == 0) goto L_0x0226;
        L_0x021d:
            r11.close();	 Catch:{ Exception -> 0x0221 }
            goto L_0x0226;
        L_0x0221:
            r0 = move-exception;
            r3 = r0;
            org.telegram.messenger.FileLog.e(r3);
        L_0x0226:
            throw r2;
        L_0x0227:
            r12 = 0;
        L_0x0228:
            r0 = r1.cacheImage;
            r0 = r0.imageLocation;
            r0 = r0.path;
            r11 = 8;
            if (r0 == 0) goto L_0x028d;
        L_0x0232:
            r13 = "thumb://";
            r13 = r0.startsWith(r13);
            if (r13 == 0) goto L_0x025b;
        L_0x023a:
            r13 = ":";
            r13 = r0.indexOf(r13, r11);
            if (r13 < 0) goto L_0x0254;
        L_0x0242:
            r14 = r0.substring(r11, r13);
            r14 = java.lang.Long.parseLong(r14);
            r14 = java.lang.Long.valueOf(r14);
            r13 = r13 + r6;
            r0 = r0.substring(r13);
            goto L_0x0256;
        L_0x0254:
            r0 = 0;
            r14 = 0;
        L_0x0256:
            r13 = r0;
        L_0x0257:
            r15 = 0;
        L_0x0258:
            r16 = 0;
            goto L_0x0292;
        L_0x025b:
            r13 = "vthumb://";
            r13 = r0.startsWith(r13);
            if (r13 == 0) goto L_0x0282;
        L_0x0264:
            r13 = 9;
            r14 = ":";
            r14 = r0.indexOf(r14, r13);
            if (r14 < 0) goto L_0x027c;
        L_0x026e:
            r0 = r0.substring(r13, r14);
            r13 = java.lang.Long.parseLong(r0);
            r0 = java.lang.Long.valueOf(r13);
            r13 = 1;
            goto L_0x027e;
        L_0x027c:
            r0 = 0;
            r13 = 0;
        L_0x027e:
            r14 = r0;
            r15 = r13;
            r13 = 0;
            goto L_0x0258;
        L_0x0282:
            r13 = "http";
            r0 = r0.startsWith(r13);
            if (r0 != 0) goto L_0x028d;
        L_0x028a:
            r13 = 0;
            r14 = 0;
            goto L_0x0257;
        L_0x028d:
            r13 = 0;
            r14 = 0;
            r15 = 0;
            r16 = 1;
        L_0x0292:
            r11 = new android.graphics.BitmapFactory$Options;
            r11.<init>();
            r11.inSampleSize = r6;
            r0 = android.os.Build.VERSION.SDK_INT;
            r4 = 21;
            if (r0 >= r4) goto L_0x02a1;
        L_0x029f:
            r11.inPurgeable = r6;
        L_0x02a1:
            r0 = org.telegram.messenger.ImageLoader.this;
            r19 = r0.canForce8888;
            r20 = 0;
            r0 = r1.cacheImage;	 Catch:{ Throwable -> 0x0475 }
            r0 = r0.filter;	 Catch:{ Throwable -> 0x0475 }
            r21 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
            if (r0 == 0) goto L_0x0422;
        L_0x02b1:
            r0 = r1.cacheImage;	 Catch:{ Throwable -> 0x0475 }
            r0 = r0.filter;	 Catch:{ Throwable -> 0x0475 }
            r4 = "_";
            r0 = r0.split(r4);	 Catch:{ Throwable -> 0x0475 }
            r4 = r0.length;	 Catch:{ Throwable -> 0x0475 }
            if (r4 < r3) goto L_0x02dc;
        L_0x02be:
            r4 = r0[r7];	 Catch:{ Throwable -> 0x0475 }
            r4 = java.lang.Float.parseFloat(r4);	 Catch:{ Throwable -> 0x0475 }
            r22 = org.telegram.messenger.AndroidUtilities.density;	 Catch:{ Throwable -> 0x0475 }
            r4 = r4 * r22;
            r0 = r0[r6];	 Catch:{ Throwable -> 0x02d5 }
            r0 = java.lang.Float.parseFloat(r0);	 Catch:{ Throwable -> 0x02d5 }
            r22 = org.telegram.messenger.AndroidUtilities.density;	 Catch:{ Throwable -> 0x02d5 }
            r0 = r0 * r22;
            r22 = r0;
            goto L_0x02df;
        L_0x02d5:
            r0 = move-exception;
            r7 = r12;
            r25 = r13;
            r3 = 0;
            goto L_0x047b;
        L_0x02dc:
            r4 = 0;
            r22 = 0;
        L_0x02df:
            r0 = r1.cacheImage;	 Catch:{ Throwable -> 0x041b }
            r0 = r0.filter;	 Catch:{ Throwable -> 0x041b }
            r3 = "b2";
            r0 = r0.contains(r3);	 Catch:{ Throwable -> 0x041b }
            if (r0 == 0) goto L_0x02ed;
        L_0x02eb:
            r3 = 3;
            goto L_0x030a;
        L_0x02ed:
            r0 = r1.cacheImage;	 Catch:{ Throwable -> 0x041b }
            r0 = r0.filter;	 Catch:{ Throwable -> 0x041b }
            r3 = "b1";
            r0 = r0.contains(r3);	 Catch:{ Throwable -> 0x041b }
            if (r0 == 0) goto L_0x02fb;
        L_0x02f9:
            r3 = 2;
            goto L_0x030a;
        L_0x02fb:
            r0 = r1.cacheImage;	 Catch:{ Throwable -> 0x041b }
            r0 = r0.filter;	 Catch:{ Throwable -> 0x041b }
            r3 = "b";
            r0 = r0.contains(r3);	 Catch:{ Throwable -> 0x041b }
            if (r0 == 0) goto L_0x0309;
        L_0x0307:
            r3 = 1;
            goto L_0x030a;
        L_0x0309:
            r3 = 0;
        L_0x030a:
            r0 = r1.cacheImage;	 Catch:{ Throwable -> 0x0411 }
            r0 = r0.filter;	 Catch:{ Throwable -> 0x0411 }
            r5 = "i";
            r5 = r0.contains(r5);	 Catch:{ Throwable -> 0x0411 }
            r0 = r1.cacheImage;	 Catch:{ Throwable -> 0x0409 }
            r0 = r0.filter;	 Catch:{ Throwable -> 0x0409 }
            r7 = "f";
            r0 = r0.contains(r7);	 Catch:{ Throwable -> 0x0409 }
            if (r0 == 0) goto L_0x0322;
        L_0x0320:
            r19 = 1;
        L_0x0322:
            if (r12 != 0) goto L_0x0400;
        L_0x0324:
            r0 = (r4 > r20 ? 1 : (r4 == r20 ? 0 : -1));
            if (r0 == 0) goto L_0x0400;
        L_0x0328:
            r0 = (r22 > r20 ? 1 : (r22 == r20 ? 0 : -1));
            if (r0 == 0) goto L_0x0400;
        L_0x032c:
            r11.inJustDecodeBounds = r6;	 Catch:{ Throwable -> 0x0409 }
            if (r14 == 0) goto L_0x035f;
        L_0x0330:
            if (r13 != 0) goto L_0x035f;
        L_0x0332:
            if (r15 == 0) goto L_0x034b;
        L_0x0334:
            r0 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Throwable -> 0x0345 }
            r0 = r0.getContentResolver();	 Catch:{ Throwable -> 0x0345 }
            r7 = r12;
            r25 = r13;
            r12 = r14.longValue();	 Catch:{ Throwable -> 0x03b6 }
            android.provider.MediaStore.Video.Thumbnails.getThumbnail(r0, r12, r6, r11);	 Catch:{ Throwable -> 0x03b6 }
            goto L_0x035b;
        L_0x0345:
            r0 = move-exception;
            r7 = r12;
            r25 = r13;
            goto L_0x03b7;
        L_0x034b:
            r7 = r12;
            r25 = r13;
            r0 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Throwable -> 0x03b6 }
            r0 = r0.getContentResolver();	 Catch:{ Throwable -> 0x03b6 }
            r12 = r14.longValue();	 Catch:{ Throwable -> 0x03b6 }
            android.provider.MediaStore.Images.Thumbnails.getThumbnail(r0, r12, r6, r11);	 Catch:{ Throwable -> 0x03b6 }
        L_0x035b:
            r26 = r3;
            goto L_0x03d4;
        L_0x035f:
            r7 = r12;
            r25 = r13;
            if (r9 == 0) goto L_0x03ba;
        L_0x0364:
            r0 = new java.io.RandomAccessFile;	 Catch:{ Throwable -> 0x03b6 }
            r12 = "r";
            r0.<init>(r2, r12);	 Catch:{ Throwable -> 0x03b6 }
            r12 = r0.length();	 Catch:{ Throwable -> 0x03b6 }
            r13 = (int) r12;	 Catch:{ Throwable -> 0x03b6 }
            r12 = org.telegram.messenger.ImageLoader.bytesLocal;	 Catch:{ Throwable -> 0x03b6 }
            r12 = r12.get();	 Catch:{ Throwable -> 0x03b6 }
            r12 = (byte[]) r12;	 Catch:{ Throwable -> 0x03b6 }
            if (r12 == 0) goto L_0x0380;
        L_0x037c:
            r6 = r12.length;	 Catch:{ Throwable -> 0x03b6 }
            if (r6 < r13) goto L_0x0380;
        L_0x037f:
            goto L_0x0381;
        L_0x0380:
            r12 = 0;
        L_0x0381:
            if (r12 != 0) goto L_0x038c;
        L_0x0383:
            r12 = new byte[r13];	 Catch:{ Throwable -> 0x03b6 }
            r6 = org.telegram.messenger.ImageLoader.bytesLocal;	 Catch:{ Throwable -> 0x03b6 }
            r6.set(r12);	 Catch:{ Throwable -> 0x03b6 }
        L_0x038c:
            r6 = 0;
            r0.readFully(r12, r6, r13);	 Catch:{ Throwable -> 0x03b6 }
            r0.close();	 Catch:{ Throwable -> 0x03b6 }
            org.telegram.messenger.secretmedia.EncryptedFileInputStream.decryptBytesWithKeyFile(r12, r6, r13, r9);	 Catch:{ Throwable -> 0x03b6 }
            r0 = org.telegram.messenger.Utilities.computeSHA256(r12, r6, r13);	 Catch:{ Throwable -> 0x03b6 }
            if (r10 == 0) goto L_0x03a7;
        L_0x039c:
            r0 = java.util.Arrays.equals(r0, r10);	 Catch:{ Throwable -> 0x03b6 }
            if (r0 != 0) goto L_0x03a3;
        L_0x03a2:
            goto L_0x03a7;
        L_0x03a3:
            r26 = r3;
            r0 = 0;
            goto L_0x03aa;
        L_0x03a7:
            r26 = r3;
            r0 = 1;
        L_0x03aa:
            r6 = 0;
            r3 = r12[r6];	 Catch:{ Throwable -> 0x03fe }
            r3 = r3 & 255;
            r13 = r13 - r3;
            if (r0 != 0) goto L_0x03d4;
        L_0x03b2:
            android.graphics.BitmapFactory.decodeByteArray(r12, r3, r13, r11);	 Catch:{ Throwable -> 0x03fe }
            goto L_0x03d4;
        L_0x03b6:
            r0 = move-exception;
        L_0x03b7:
            r26 = r3;
            goto L_0x040f;
        L_0x03ba:
            r26 = r3;
            if (r8 == 0) goto L_0x03c8;
        L_0x03be:
            r0 = new org.telegram.messenger.secretmedia.EncryptedFileInputStream;	 Catch:{ Throwable -> 0x03fe }
            r3 = r1.cacheImage;	 Catch:{ Throwable -> 0x03fe }
            r3 = r3.encryptionKeyPath;	 Catch:{ Throwable -> 0x03fe }
            r0.<init>(r2, r3);	 Catch:{ Throwable -> 0x03fe }
            goto L_0x03cd;
        L_0x03c8:
            r0 = new java.io.FileInputStream;	 Catch:{ Throwable -> 0x03fe }
            r0.<init>(r2);	 Catch:{ Throwable -> 0x03fe }
        L_0x03cd:
            r3 = 0;
            android.graphics.BitmapFactory.decodeStream(r0, r3, r11);	 Catch:{ Throwable -> 0x03fe }
            r0.close();	 Catch:{ Throwable -> 0x03fe }
        L_0x03d4:
            r0 = r11.outWidth;	 Catch:{ Throwable -> 0x03fe }
            r0 = (float) r0;	 Catch:{ Throwable -> 0x03fe }
            r3 = r11.outHeight;	 Catch:{ Throwable -> 0x03fe }
            r3 = (float) r3;	 Catch:{ Throwable -> 0x03fe }
            r6 = (r4 > r22 ? 1 : (r4 == r22 ? 0 : -1));
            if (r6 <= 0) goto L_0x03ea;
        L_0x03de:
            r6 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1));
            if (r6 <= 0) goto L_0x03ea;
        L_0x03e2:
            r0 = r0 / r4;
            r3 = r3 / r22;
            r0 = java.lang.Math.max(r0, r3);	 Catch:{ Throwable -> 0x03fe }
            goto L_0x03f1;
        L_0x03ea:
            r0 = r0 / r4;
            r3 = r3 / r22;
            r0 = java.lang.Math.min(r0, r3);	 Catch:{ Throwable -> 0x03fe }
        L_0x03f1:
            r3 = (r0 > r21 ? 1 : (r0 == r21 ? 0 : -1));
            if (r3 >= 0) goto L_0x03f7;
        L_0x03f5:
            r0 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        L_0x03f7:
            r3 = 0;
            r11.inJustDecodeBounds = r3;	 Catch:{ Throwable -> 0x03fe }
            r0 = (int) r0;	 Catch:{ Throwable -> 0x03fe }
            r11.inSampleSize = r0;	 Catch:{ Throwable -> 0x03fe }
            goto L_0x0405;
        L_0x03fe:
            r0 = move-exception;
            goto L_0x040f;
        L_0x0400:
            r26 = r3;
            r7 = r12;
            r25 = r13;
        L_0x0405:
            r0 = r5;
            r5 = 0;
            goto L_0x0473;
        L_0x0409:
            r0 = move-exception;
            r26 = r3;
            r7 = r12;
            r25 = r13;
        L_0x040f:
            r3 = r5;
            goto L_0x0418;
        L_0x0411:
            r0 = move-exception;
            r26 = r3;
            r7 = r12;
            r25 = r13;
            r3 = 0;
        L_0x0418:
            r5 = 0;
            goto L_0x0480;
        L_0x041b:
            r0 = move-exception;
            r7 = r12;
            r25 = r13;
            r3 = 0;
            r5 = 0;
            goto L_0x047e;
        L_0x0422:
            r7 = r12;
            r25 = r13;
            if (r25 == 0) goto L_0x046c;
        L_0x0427:
            r3 = 1;
            r11.inJustDecodeBounds = r3;	 Catch:{ Throwable -> 0x046a }
            if (r19 == 0) goto L_0x042f;
        L_0x042c:
            r0 = android.graphics.Bitmap.Config.ARGB_8888;	 Catch:{ Throwable -> 0x046a }
            goto L_0x0431;
        L_0x042f:
            r0 = android.graphics.Bitmap.Config.RGB_565;	 Catch:{ Throwable -> 0x046a }
        L_0x0431:
            r11.inPreferredConfig = r0;	 Catch:{ Throwable -> 0x046a }
            r0 = new java.io.FileInputStream;	 Catch:{ Throwable -> 0x046a }
            r0.<init>(r2);	 Catch:{ Throwable -> 0x046a }
            r3 = 0;
            r5 = android.graphics.BitmapFactory.decodeStream(r0, r3, r11);	 Catch:{ Throwable -> 0x046a }
            r0.close();	 Catch:{ Throwable -> 0x0466 }
            r0 = r11.outWidth;	 Catch:{ Throwable -> 0x0466 }
            r3 = r11.outHeight;	 Catch:{ Throwable -> 0x0466 }
            r4 = 0;
            r11.inJustDecodeBounds = r4;	 Catch:{ Throwable -> 0x0466 }
            r0 = r0 / 200;
            r3 = r3 / 200;
            r0 = java.lang.Math.max(r0, r3);	 Catch:{ Throwable -> 0x0466 }
            r0 = (float) r0;	 Catch:{ Throwable -> 0x0466 }
            r3 = (r0 > r21 ? 1 : (r0 == r21 ? 0 : -1));
            if (r3 >= 0) goto L_0x0456;
        L_0x0454:
            r0 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        L_0x0456:
            r3 = 1;
        L_0x0457:
            r4 = 2;
            r3 = r3 * 2;
            r4 = r3 * 2;
            r4 = (float) r4;	 Catch:{ Throwable -> 0x0466 }
            r4 = (r4 > r0 ? 1 : (r4 == r0 ? 0 : -1));
            if (r4 < 0) goto L_0x0457;
        L_0x0461:
            r11.inSampleSize = r3;	 Catch:{ Throwable -> 0x0466 }
            r0 = 0;
            r4 = 0;
            goto L_0x046f;
        L_0x0466:
            r0 = move-exception;
            r3 = 0;
            r4 = 0;
            goto L_0x047c;
        L_0x046a:
            r0 = move-exception;
            goto L_0x0479;
        L_0x046c:
            r0 = 0;
            r4 = 0;
            r5 = 0;
        L_0x046f:
            r22 = 0;
            r26 = 0;
        L_0x0473:
            r3 = r0;
            goto L_0x0483;
        L_0x0475:
            r0 = move-exception;
            r7 = r12;
            r25 = r13;
        L_0x0479:
            r3 = 0;
            r4 = 0;
        L_0x047b:
            r5 = 0;
        L_0x047c:
            r22 = 0;
        L_0x047e:
            r26 = 0;
        L_0x0480:
            org.telegram.messenger.FileLog.e(r0);
        L_0x0483:
            r6 = r22;
            r0 = r26;
            r12 = r1.cacheImage;
            r12 = r12.imageType;
            r21 = r14;
            r13 = 1;
            if (r12 != r13) goto L_0x0691;
        L_0x0490:
            r6 = org.telegram.messenger.ImageLoader.this;	 Catch:{ Throwable -> 0x0687 }
            r12 = java.lang.System.currentTimeMillis();	 Catch:{ Throwable -> 0x0687 }
            r6.lastCacheOutTime = r12;	 Catch:{ Throwable -> 0x0687 }
            r6 = r1.sync;	 Catch:{ Throwable -> 0x0687 }
            monitor-enter(r6);	 Catch:{ Throwable -> 0x0687 }
            r12 = r1.isCancelled;	 Catch:{ all -> 0x0684 }
            if (r12 == 0) goto L_0x04a2;
        L_0x04a0:
            monitor-exit(r6);	 Catch:{ all -> 0x0684 }
            return;
        L_0x04a2:
            monitor-exit(r6);	 Catch:{ all -> 0x0684 }
            if (r7 == 0) goto L_0x04ea;
        L_0x04a5:
            r6 = new java.io.RandomAccessFile;	 Catch:{ Throwable -> 0x0687 }
            r7 = "r";
            r6.<init>(r2, r7);	 Catch:{ Throwable -> 0x0687 }
            r12 = r6.getChannel();	 Catch:{ Throwable -> 0x0687 }
            r13 = java.nio.channels.FileChannel.MapMode.READ_ONLY;	 Catch:{ Throwable -> 0x0687 }
            r14 = 0;
            r16 = r2.length();	 Catch:{ Throwable -> 0x0687 }
            r7 = r12.map(r13, r14, r16);	 Catch:{ Throwable -> 0x0687 }
            r8 = new android.graphics.BitmapFactory$Options;	 Catch:{ Throwable -> 0x0687 }
            r8.<init>();	 Catch:{ Throwable -> 0x0687 }
            r9 = 1;
            r8.inJustDecodeBounds = r9;	 Catch:{ Throwable -> 0x0687 }
            r10 = r7.limit();	 Catch:{ Throwable -> 0x0687 }
            r12 = 0;
            org.telegram.messenger.Utilities.loadWebpImage(r12, r7, r10, r8, r9);	 Catch:{ Throwable -> 0x0687 }
            r9 = r8.outWidth;	 Catch:{ Throwable -> 0x0687 }
            r8 = r8.outHeight;	 Catch:{ Throwable -> 0x0687 }
            r10 = android.graphics.Bitmap.Config.ARGB_8888;	 Catch:{ Throwable -> 0x0687 }
            r5 = org.telegram.messenger.Bitmaps.createBitmap(r9, r8, r10);	 Catch:{ Throwable -> 0x0687 }
            r8 = r7.limit();	 Catch:{ Throwable -> 0x0687 }
            r9 = r11.inPurgeable;	 Catch:{ Throwable -> 0x0687 }
            if (r9 != 0) goto L_0x04e0;
        L_0x04de:
            r9 = 1;
            goto L_0x04e1;
        L_0x04e0:
            r9 = 0;
        L_0x04e1:
            r10 = 0;
            org.telegram.messenger.Utilities.loadWebpImage(r5, r7, r8, r10, r9);	 Catch:{ Throwable -> 0x0687 }
            r6.close();	 Catch:{ Throwable -> 0x0687 }
            goto L_0x0568;
        L_0x04ea:
            r6 = r11.inPurgeable;	 Catch:{ Throwable -> 0x0687 }
            if (r6 != 0) goto L_0x050b;
        L_0x04ee:
            if (r9 == 0) goto L_0x04f1;
        L_0x04f0:
            goto L_0x050b;
        L_0x04f1:
            if (r8 == 0) goto L_0x04fd;
        L_0x04f3:
            r6 = new org.telegram.messenger.secretmedia.EncryptedFileInputStream;	 Catch:{ Throwable -> 0x0687 }
            r7 = r1.cacheImage;	 Catch:{ Throwable -> 0x0687 }
            r7 = r7.encryptionKeyPath;	 Catch:{ Throwable -> 0x0687 }
            r6.<init>(r2, r7);	 Catch:{ Throwable -> 0x0687 }
            goto L_0x0502;
        L_0x04fd:
            r6 = new java.io.FileInputStream;	 Catch:{ Throwable -> 0x0687 }
            r6.<init>(r2);	 Catch:{ Throwable -> 0x0687 }
        L_0x0502:
            r7 = 0;
            r5 = android.graphics.BitmapFactory.decodeStream(r6, r7, r11);	 Catch:{ Throwable -> 0x0687 }
            r6.close();	 Catch:{ Throwable -> 0x0687 }
            goto L_0x0568;
        L_0x050b:
            r6 = new java.io.RandomAccessFile;	 Catch:{ Throwable -> 0x0687 }
            r7 = "r";
            r6.<init>(r2, r7);	 Catch:{ Throwable -> 0x0687 }
            r12 = r6.length();	 Catch:{ Throwable -> 0x0687 }
            r7 = (int) r12;	 Catch:{ Throwable -> 0x0687 }
            r12 = org.telegram.messenger.ImageLoader.bytesThumbLocal;	 Catch:{ Throwable -> 0x0687 }
            r12 = r12.get();	 Catch:{ Throwable -> 0x0687 }
            r12 = (byte[]) r12;	 Catch:{ Throwable -> 0x0687 }
            if (r12 == 0) goto L_0x0527;
        L_0x0523:
            r13 = r12.length;	 Catch:{ Throwable -> 0x0687 }
            if (r13 < r7) goto L_0x0527;
        L_0x0526:
            goto L_0x0528;
        L_0x0527:
            r12 = 0;
        L_0x0528:
            if (r12 != 0) goto L_0x0533;
        L_0x052a:
            r12 = new byte[r7];	 Catch:{ Throwable -> 0x0687 }
            r13 = org.telegram.messenger.ImageLoader.bytesThumbLocal;	 Catch:{ Throwable -> 0x0687 }
            r13.set(r12);	 Catch:{ Throwable -> 0x0687 }
        L_0x0533:
            r13 = 0;
            r6.readFully(r12, r13, r7);	 Catch:{ Throwable -> 0x0687 }
            r6.close();	 Catch:{ Throwable -> 0x0687 }
            if (r9 == 0) goto L_0x0556;
        L_0x053c:
            org.telegram.messenger.secretmedia.EncryptedFileInputStream.decryptBytesWithKeyFile(r12, r13, r7, r9);	 Catch:{ Throwable -> 0x0687 }
            r6 = org.telegram.messenger.Utilities.computeSHA256(r12, r13, r7);	 Catch:{ Throwable -> 0x0687 }
            if (r10 == 0) goto L_0x054e;
        L_0x0545:
            r6 = java.util.Arrays.equals(r6, r10);	 Catch:{ Throwable -> 0x0687 }
            if (r6 != 0) goto L_0x054c;
        L_0x054b:
            goto L_0x054e;
        L_0x054c:
            r6 = 0;
            goto L_0x054f;
        L_0x054e:
            r6 = 1;
        L_0x054f:
            r8 = 0;
            r9 = r12[r8];	 Catch:{ Throwable -> 0x0687 }
            r8 = r9 & 255;
            r7 = r7 - r8;
            goto L_0x0562;
        L_0x0556:
            if (r8 == 0) goto L_0x0560;
        L_0x0558:
            r6 = r1.cacheImage;	 Catch:{ Throwable -> 0x0687 }
            r6 = r6.encryptionKeyPath;	 Catch:{ Throwable -> 0x0687 }
            r8 = 0;
            org.telegram.messenger.secretmedia.EncryptedFileInputStream.decryptBytesWithKeyFile(r12, r8, r7, r6);	 Catch:{ Throwable -> 0x0687 }
        L_0x0560:
            r6 = 0;
            r8 = 0;
        L_0x0562:
            if (r6 != 0) goto L_0x0568;
        L_0x0564:
            r5 = android.graphics.BitmapFactory.decodeByteArray(r12, r8, r7, r11);	 Catch:{ Throwable -> 0x0687 }
        L_0x0568:
            if (r5 != 0) goto L_0x0581;
        L_0x056a:
            r3 = r2.length();	 Catch:{ Throwable -> 0x0687 }
            r6 = 0;
            r0 = (r3 > r6 ? 1 : (r3 == r6 ? 0 : -1));
            if (r0 == 0) goto L_0x057a;
        L_0x0574:
            r0 = r1.cacheImage;	 Catch:{ Throwable -> 0x0687 }
            r0 = r0.filter;	 Catch:{ Throwable -> 0x0687 }
            if (r0 != 0) goto L_0x057d;
        L_0x057a:
            r2.delete();	 Catch:{ Throwable -> 0x0687 }
        L_0x057d:
            r2 = r5;
            r7 = 0;
            goto L_0x068d;
        L_0x0581:
            r2 = r1.cacheImage;	 Catch:{ Throwable -> 0x0687 }
            r2 = r2.filter;	 Catch:{ Throwable -> 0x0687 }
            if (r2 == 0) goto L_0x05b4;
        L_0x0587:
            r2 = r5.getWidth();	 Catch:{ Throwable -> 0x0687 }
            r2 = (float) r2;	 Catch:{ Throwable -> 0x0687 }
            r6 = r5.getHeight();	 Catch:{ Throwable -> 0x0687 }
            r6 = (float) r6;	 Catch:{ Throwable -> 0x0687 }
            r7 = r11.inPurgeable;	 Catch:{ Throwable -> 0x0687 }
            if (r7 != 0) goto L_0x05b4;
        L_0x0595:
            r7 = (r4 > r20 ? 1 : (r4 == r20 ? 0 : -1));
            if (r7 == 0) goto L_0x05b4;
        L_0x0599:
            r7 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1));
            if (r7 == 0) goto L_0x05b4;
        L_0x059d:
            r7 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
            r13 = r4 + r7;
            r7 = (r2 > r13 ? 1 : (r2 == r13 ? 0 : -1));
            if (r7 <= 0) goto L_0x05b4;
        L_0x05a5:
            r2 = r2 / r4;
            r4 = (int) r4;	 Catch:{ Throwable -> 0x0687 }
            r6 = r6 / r2;
            r2 = (int) r6;	 Catch:{ Throwable -> 0x0687 }
            r6 = 1;
            r2 = org.telegram.messenger.Bitmaps.createScaledBitmap(r5, r4, r2, r6);	 Catch:{ Throwable -> 0x0687 }
            if (r5 == r2) goto L_0x05b4;
        L_0x05b0:
            r5.recycle();	 Catch:{ Throwable -> 0x0687 }
            goto L_0x05b5;
        L_0x05b4:
            r2 = r5;
        L_0x05b5:
            if (r3 == 0) goto L_0x05d8;
        L_0x05b7:
            r3 = r11.inPurgeable;	 Catch:{ Throwable -> 0x05d5 }
            if (r3 == 0) goto L_0x05bd;
        L_0x05bb:
            r3 = 0;
            goto L_0x05be;
        L_0x05bd:
            r3 = 1;
        L_0x05be:
            r4 = r2.getWidth();	 Catch:{ Throwable -> 0x05d5 }
            r5 = r2.getHeight();	 Catch:{ Throwable -> 0x05d5 }
            r6 = r2.getRowBytes();	 Catch:{ Throwable -> 0x05d5 }
            r3 = org.telegram.messenger.Utilities.needInvert(r2, r3, r4, r5, r6);	 Catch:{ Throwable -> 0x05d5 }
            if (r3 == 0) goto L_0x05d2;
        L_0x05d0:
            r7 = 1;
            goto L_0x05d3;
        L_0x05d2:
            r7 = 0;
        L_0x05d3:
            r3 = 1;
            goto L_0x05da;
        L_0x05d5:
            r0 = move-exception;
            goto L_0x0689;
        L_0x05d8:
            r3 = 1;
            r7 = 0;
        L_0x05da:
            if (r0 != r3) goto L_0x0601;
        L_0x05dc:
            r0 = r2.getConfig();	 Catch:{ Throwable -> 0x05fe }
            r3 = android.graphics.Bitmap.Config.ARGB_8888;	 Catch:{ Throwable -> 0x05fe }
            if (r0 != r3) goto L_0x068d;
        L_0x05e4:
            r13 = 3;
            r0 = r11.inPurgeable;	 Catch:{ Throwable -> 0x05fe }
            if (r0 == 0) goto L_0x05eb;
        L_0x05e9:
            r14 = 0;
            goto L_0x05ec;
        L_0x05eb:
            r14 = 1;
        L_0x05ec:
            r15 = r2.getWidth();	 Catch:{ Throwable -> 0x05fe }
            r16 = r2.getHeight();	 Catch:{ Throwable -> 0x05fe }
            r17 = r2.getRowBytes();	 Catch:{ Throwable -> 0x05fe }
            r12 = r2;
            org.telegram.messenger.Utilities.blurBitmap(r12, r13, r14, r15, r16, r17);	 Catch:{ Throwable -> 0x05fe }
            goto L_0x068d;
        L_0x05fe:
            r0 = move-exception;
            goto L_0x068a;
        L_0x0601:
            r3 = 2;
            if (r0 != r3) goto L_0x0626;
        L_0x0604:
            r0 = r2.getConfig();	 Catch:{ Throwable -> 0x05fe }
            r3 = android.graphics.Bitmap.Config.ARGB_8888;	 Catch:{ Throwable -> 0x05fe }
            if (r0 != r3) goto L_0x068d;
        L_0x060c:
            r13 = 1;
            r0 = r11.inPurgeable;	 Catch:{ Throwable -> 0x05fe }
            if (r0 == 0) goto L_0x0613;
        L_0x0611:
            r14 = 0;
            goto L_0x0614;
        L_0x0613:
            r14 = 1;
        L_0x0614:
            r15 = r2.getWidth();	 Catch:{ Throwable -> 0x05fe }
            r16 = r2.getHeight();	 Catch:{ Throwable -> 0x05fe }
            r17 = r2.getRowBytes();	 Catch:{ Throwable -> 0x05fe }
            r12 = r2;
            org.telegram.messenger.Utilities.blurBitmap(r12, r13, r14, r15, r16, r17);	 Catch:{ Throwable -> 0x05fe }
            goto L_0x068d;
        L_0x0626:
            r3 = 3;
            if (r0 != r3) goto L_0x067a;
        L_0x0629:
            r0 = r2.getConfig();	 Catch:{ Throwable -> 0x05fe }
            r3 = android.graphics.Bitmap.Config.ARGB_8888;	 Catch:{ Throwable -> 0x05fe }
            if (r0 != r3) goto L_0x068d;
        L_0x0631:
            r13 = 7;
            r0 = r11.inPurgeable;	 Catch:{ Throwable -> 0x05fe }
            if (r0 == 0) goto L_0x0638;
        L_0x0636:
            r14 = 0;
            goto L_0x0639;
        L_0x0638:
            r14 = 1;
        L_0x0639:
            r15 = r2.getWidth();	 Catch:{ Throwable -> 0x05fe }
            r16 = r2.getHeight();	 Catch:{ Throwable -> 0x05fe }
            r17 = r2.getRowBytes();	 Catch:{ Throwable -> 0x05fe }
            r12 = r2;
            org.telegram.messenger.Utilities.blurBitmap(r12, r13, r14, r15, r16, r17);	 Catch:{ Throwable -> 0x05fe }
            r13 = 7;
            r0 = r11.inPurgeable;	 Catch:{ Throwable -> 0x05fe }
            if (r0 == 0) goto L_0x0650;
        L_0x064e:
            r14 = 0;
            goto L_0x0651;
        L_0x0650:
            r14 = 1;
        L_0x0651:
            r15 = r2.getWidth();	 Catch:{ Throwable -> 0x05fe }
            r16 = r2.getHeight();	 Catch:{ Throwable -> 0x05fe }
            r17 = r2.getRowBytes();	 Catch:{ Throwable -> 0x05fe }
            r12 = r2;
            org.telegram.messenger.Utilities.blurBitmap(r12, r13, r14, r15, r16, r17);	 Catch:{ Throwable -> 0x05fe }
            r13 = 7;
            r0 = r11.inPurgeable;	 Catch:{ Throwable -> 0x05fe }
            if (r0 == 0) goto L_0x0668;
        L_0x0666:
            r14 = 0;
            goto L_0x0669;
        L_0x0668:
            r14 = 1;
        L_0x0669:
            r15 = r2.getWidth();	 Catch:{ Throwable -> 0x05fe }
            r16 = r2.getHeight();	 Catch:{ Throwable -> 0x05fe }
            r17 = r2.getRowBytes();	 Catch:{ Throwable -> 0x05fe }
            r12 = r2;
            org.telegram.messenger.Utilities.blurBitmap(r12, r13, r14, r15, r16, r17);	 Catch:{ Throwable -> 0x05fe }
            goto L_0x068d;
        L_0x067a:
            if (r0 != 0) goto L_0x068d;
        L_0x067c:
            r0 = r11.inPurgeable;	 Catch:{ Throwable -> 0x05fe }
            if (r0 == 0) goto L_0x068d;
        L_0x0680:
            org.telegram.messenger.Utilities.pinBitmap(r2);	 Catch:{ Throwable -> 0x05fe }
            goto L_0x068d;
        L_0x0684:
            r0 = move-exception;
            monitor-exit(r6);	 Catch:{ all -> 0x0684 }
            throw r0;	 Catch:{ Throwable -> 0x0687 }
        L_0x0687:
            r0 = move-exception;
            r2 = r5;
        L_0x0689:
            r7 = 0;
        L_0x068a:
            org.telegram.messenger.FileLog.e(r0);
        L_0x068d:
            r0 = 0;
            r12 = 0;
            goto L_0x0954;
        L_0x0691:
            r12 = 20;
            if (r21 == 0) goto L_0x0696;
        L_0x0695:
            r12 = 0;
        L_0x0696:
            if (r12 == 0) goto L_0x06cb;
        L_0x0698:
            r13 = org.telegram.messenger.ImageLoader.this;	 Catch:{ Throwable -> 0x06c3 }
            r13 = r13.lastCacheOutTime;	 Catch:{ Throwable -> 0x06c3 }
            r26 = 0;
            r23 = (r13 > r26 ? 1 : (r13 == r26 ? 0 : -1));
            if (r23 == 0) goto L_0x06cb;
        L_0x06a4:
            r13 = org.telegram.messenger.ImageLoader.this;	 Catch:{ Throwable -> 0x06c3 }
            r13 = r13.lastCacheOutTime;	 Catch:{ Throwable -> 0x06c3 }
            r28 = java.lang.System.currentTimeMillis();	 Catch:{ Throwable -> 0x06c3 }
            r30 = r5;
            r23 = r6;
            r5 = (long) r12;
            r28 = r28 - r5;
            r12 = (r13 > r28 ? 1 : (r13 == r28 ? 0 : -1));
            if (r12 <= 0) goto L_0x06cf;
        L_0x06b9:
            r12 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Throwable -> 0x06c5 }
            r13 = 21;
            if (r12 >= r13) goto L_0x06cf;
        L_0x06bf:
            java.lang.Thread.sleep(r5);	 Catch:{ Throwable -> 0x06c5 }
            goto L_0x06cf;
        L_0x06c3:
            r30 = r5;
        L_0x06c5:
            r2 = r30;
        L_0x06c7:
            r7 = 0;
            r12 = 0;
            goto L_0x0950;
        L_0x06cb:
            r30 = r5;
            r23 = r6;
        L_0x06cf:
            r5 = org.telegram.messenger.ImageLoader.this;	 Catch:{ Throwable -> 0x094b }
            r12 = java.lang.System.currentTimeMillis();	 Catch:{ Throwable -> 0x094b }
            r5.lastCacheOutTime = r12;	 Catch:{ Throwable -> 0x094b }
            r5 = r1.sync;	 Catch:{ Throwable -> 0x094b }
            monitor-enter(r5);	 Catch:{ Throwable -> 0x094b }
            r6 = r1.isCancelled;	 Catch:{ all -> 0x0944 }
            if (r6 == 0) goto L_0x06e1;
        L_0x06df:
            monitor-exit(r5);	 Catch:{ all -> 0x0944 }
            return;
        L_0x06e1:
            monitor-exit(r5);	 Catch:{ all -> 0x0944 }
            if (r19 != 0) goto L_0x06fa;
        L_0x06e4:
            r5 = r1.cacheImage;	 Catch:{ Throwable -> 0x06c5 }
            r5 = r5.filter;	 Catch:{ Throwable -> 0x06c5 }
            if (r5 == 0) goto L_0x06fa;
        L_0x06ea:
            if (r0 != 0) goto L_0x06fa;
        L_0x06ec:
            r5 = r1.cacheImage;	 Catch:{ Throwable -> 0x06c5 }
            r5 = r5.imageLocation;	 Catch:{ Throwable -> 0x06c5 }
            r5 = r5.path;	 Catch:{ Throwable -> 0x06c5 }
            if (r5 == 0) goto L_0x06f5;
        L_0x06f4:
            goto L_0x06fa;
        L_0x06f5:
            r5 = android.graphics.Bitmap.Config.RGB_565;	 Catch:{ Throwable -> 0x06c5 }
            r11.inPreferredConfig = r5;	 Catch:{ Throwable -> 0x06c5 }
            goto L_0x06fe;
        L_0x06fa:
            r5 = android.graphics.Bitmap.Config.ARGB_8888;	 Catch:{ Throwable -> 0x094b }
            r11.inPreferredConfig = r5;	 Catch:{ Throwable -> 0x094b }
        L_0x06fe:
            r5 = 0;
            r11.inDither = r5;	 Catch:{ Throwable -> 0x094b }
            if (r21 == 0) goto L_0x0727;
        L_0x0703:
            if (r25 != 0) goto L_0x0727;
        L_0x0705:
            if (r15 == 0) goto L_0x0717;
        L_0x0707:
            r5 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Throwable -> 0x06c5 }
            r5 = r5.getContentResolver();	 Catch:{ Throwable -> 0x06c5 }
            r12 = r21.longValue();	 Catch:{ Throwable -> 0x06c5 }
            r6 = 1;
            r5 = android.provider.MediaStore.Video.Thumbnails.getThumbnail(r5, r12, r6, r11);	 Catch:{ Throwable -> 0x06c5 }
            goto L_0x0729;
        L_0x0717:
            r5 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Throwable -> 0x06c5 }
            r5 = r5.getContentResolver();	 Catch:{ Throwable -> 0x06c5 }
            r12 = r21.longValue();	 Catch:{ Throwable -> 0x06c5 }
            r6 = 1;
            r5 = android.provider.MediaStore.Images.Thumbnails.getThumbnail(r5, r12, r6, r11);	 Catch:{ Throwable -> 0x06c5 }
            goto L_0x0729;
        L_0x0727:
            r5 = r30;
        L_0x0729:
            if (r5 != 0) goto L_0x083c;
        L_0x072b:
            if (r7 == 0) goto L_0x077b;
        L_0x072d:
            r6 = new java.io.RandomAccessFile;	 Catch:{ Throwable -> 0x0778 }
            r7 = "r";
            r6.<init>(r2, r7);	 Catch:{ Throwable -> 0x0778 }
            r28 = r6.getChannel();	 Catch:{ Throwable -> 0x0778 }
            r29 = java.nio.channels.FileChannel.MapMode.READ_ONLY;	 Catch:{ Throwable -> 0x0778 }
            r30 = 0;
            r32 = r2.length();	 Catch:{ Throwable -> 0x0778 }
            r7 = r28.map(r29, r30, r32);	 Catch:{ Throwable -> 0x0778 }
            r8 = new android.graphics.BitmapFactory$Options;	 Catch:{ Throwable -> 0x0778 }
            r8.<init>();	 Catch:{ Throwable -> 0x0778 }
            r9 = 1;
            r8.inJustDecodeBounds = r9;	 Catch:{ Throwable -> 0x0778 }
            r10 = r7.limit();	 Catch:{ Throwable -> 0x0778 }
            r12 = 0;
            org.telegram.messenger.Utilities.loadWebpImage(r12, r7, r10, r8, r9);	 Catch:{ Throwable -> 0x0839 }
            r9 = r8.outWidth;	 Catch:{ Throwable -> 0x0778 }
            r8 = r8.outHeight;	 Catch:{ Throwable -> 0x0778 }
            r10 = android.graphics.Bitmap.Config.ARGB_8888;	 Catch:{ Throwable -> 0x0778 }
            r5 = org.telegram.messenger.Bitmaps.createBitmap(r9, r8, r10);	 Catch:{ Throwable -> 0x0778 }
            r8 = r7.limit();	 Catch:{ Throwable -> 0x0778 }
            r9 = r11.inPurgeable;	 Catch:{ Throwable -> 0x0778 }
            if (r9 != 0) goto L_0x0768;
        L_0x0766:
            r9 = 1;
            goto L_0x0769;
        L_0x0768:
            r9 = 0;
        L_0x0769:
            r10 = 0;
            org.telegram.messenger.Utilities.loadWebpImage(r5, r7, r8, r10, r9);	 Catch:{ Throwable -> 0x0774 }
            r6.close();	 Catch:{ Throwable -> 0x0778 }
            r7 = 0;
            r12 = 0;
            goto L_0x083e;
        L_0x0774:
            r2 = r5;
            r12 = r10;
            goto L_0x094f;
        L_0x0778:
            r2 = r5;
            goto L_0x06c7;
        L_0x077b:
            r6 = r11.inPurgeable;	 Catch:{ Throwable -> 0x0838 }
            if (r6 != 0) goto L_0x07d9;
        L_0x077f:
            if (r9 == 0) goto L_0x0782;
        L_0x0781:
            goto L_0x07d9;
        L_0x0782:
            if (r8 == 0) goto L_0x078e;
        L_0x0784:
            r6 = new org.telegram.messenger.secretmedia.EncryptedFileInputStream;	 Catch:{ Throwable -> 0x0778 }
            r7 = r1.cacheImage;	 Catch:{ Throwable -> 0x0778 }
            r7 = r7.encryptionKeyPath;	 Catch:{ Throwable -> 0x0778 }
            r6.<init>(r2, r7);	 Catch:{ Throwable -> 0x0778 }
            goto L_0x0793;
        L_0x078e:
            r6 = new java.io.FileInputStream;	 Catch:{ Throwable -> 0x0838 }
            r6.<init>(r2);	 Catch:{ Throwable -> 0x0838 }
        L_0x0793:
            r7 = r1.cacheImage;	 Catch:{ Throwable -> 0x0838 }
            r7 = r7.imageLocation;	 Catch:{ Throwable -> 0x0838 }
            r7 = r7.document;	 Catch:{ Throwable -> 0x0838 }
            r7 = r7 instanceof org.telegram.tgnet.TLRPC.TL_document;	 Catch:{ Throwable -> 0x0838 }
            if (r7 == 0) goto L_0x07cf;
        L_0x079d:
            r7 = new androidx.exifinterface.media.ExifInterface;	 Catch:{ Throwable -> 0x07bd }
            r7.<init>(r6);	 Catch:{ Throwable -> 0x07bd }
            r8 = "Orientation";
            r9 = 1;
            r7 = r7.getAttributeInt(r8, r9);	 Catch:{ Throwable -> 0x07bd }
            r8 = 3;
            if (r7 == r8) goto L_0x07ba;
        L_0x07ac:
            r8 = 6;
            if (r7 == r8) goto L_0x07b7;
        L_0x07af:
            r8 = 8;
            if (r7 == r8) goto L_0x07b4;
        L_0x07b3:
            goto L_0x07bd;
        L_0x07b4:
            r7 = 270; // 0x10e float:3.78E-43 double:1.334E-321;
            goto L_0x07be;
        L_0x07b7:
            r7 = 90;
            goto L_0x07be;
        L_0x07ba:
            r7 = 180; // 0xb4 float:2.52E-43 double:8.9E-322;
            goto L_0x07be;
        L_0x07bd:
            r7 = 0;
        L_0x07be:
            r8 = r6.getChannel();	 Catch:{ Throwable -> 0x07c8 }
            r9 = 0;
            r8.position(r9);	 Catch:{ Throwable -> 0x07c8 }
            goto L_0x07d0;
        L_0x07c8:
            r2 = r5;
            r24 = r7;
            r7 = 0;
            r12 = 0;
            goto L_0x0952;
        L_0x07cf:
            r7 = 0;
        L_0x07d0:
            r12 = 0;
            r5 = android.graphics.BitmapFactory.decodeStream(r6, r12, r11);	 Catch:{ Throwable -> 0x093f }
            r6.close();	 Catch:{ Throwable -> 0x093f }
            goto L_0x083e;
        L_0x07d9:
            r12 = 0;
            r6 = new java.io.RandomAccessFile;	 Catch:{ Throwable -> 0x0839 }
            r7 = "r";
            r6.<init>(r2, r7);	 Catch:{ Throwable -> 0x0839 }
            r13 = r6.length();	 Catch:{ Throwable -> 0x0839 }
            r7 = (int) r13;	 Catch:{ Throwable -> 0x0839 }
            r13 = org.telegram.messenger.ImageLoader.bytesLocal;	 Catch:{ Throwable -> 0x0839 }
            r13 = r13.get();	 Catch:{ Throwable -> 0x0839 }
            r13 = (byte[]) r13;	 Catch:{ Throwable -> 0x0839 }
            if (r13 == 0) goto L_0x07f6;
        L_0x07f2:
            r14 = r13.length;	 Catch:{ Throwable -> 0x0839 }
            if (r14 < r7) goto L_0x07f6;
        L_0x07f5:
            goto L_0x07f7;
        L_0x07f6:
            r13 = r12;
        L_0x07f7:
            if (r13 != 0) goto L_0x0802;
        L_0x07f9:
            r13 = new byte[r7];	 Catch:{ Throwable -> 0x0839 }
            r14 = org.telegram.messenger.ImageLoader.bytesLocal;	 Catch:{ Throwable -> 0x0839 }
            r14.set(r13);	 Catch:{ Throwable -> 0x0839 }
        L_0x0802:
            r14 = 0;
            r6.readFully(r13, r14, r7);	 Catch:{ Throwable -> 0x0839 }
            r6.close();	 Catch:{ Throwable -> 0x0839 }
            if (r9 == 0) goto L_0x0825;
        L_0x080b:
            org.telegram.messenger.secretmedia.EncryptedFileInputStream.decryptBytesWithKeyFile(r13, r14, r7, r9);	 Catch:{ Throwable -> 0x0839 }
            r6 = org.telegram.messenger.Utilities.computeSHA256(r13, r14, r7);	 Catch:{ Throwable -> 0x0839 }
            if (r10 == 0) goto L_0x081d;
        L_0x0814:
            r6 = java.util.Arrays.equals(r6, r10);	 Catch:{ Throwable -> 0x0839 }
            if (r6 != 0) goto L_0x081b;
        L_0x081a:
            goto L_0x081d;
        L_0x081b:
            r6 = 0;
            goto L_0x081e;
        L_0x081d:
            r6 = 1;
        L_0x081e:
            r8 = 0;
            r9 = r13[r8];	 Catch:{ Throwable -> 0x0839 }
            r8 = r9 & 255;
            r7 = r7 - r8;
            goto L_0x0831;
        L_0x0825:
            if (r8 == 0) goto L_0x082f;
        L_0x0827:
            r6 = r1.cacheImage;	 Catch:{ Throwable -> 0x0839 }
            r6 = r6.encryptionKeyPath;	 Catch:{ Throwable -> 0x0839 }
            r8 = 0;
            org.telegram.messenger.secretmedia.EncryptedFileInputStream.decryptBytesWithKeyFile(r13, r8, r7, r6);	 Catch:{ Throwable -> 0x0839 }
        L_0x082f:
            r6 = 0;
            r8 = 0;
        L_0x0831:
            if (r6 != 0) goto L_0x083d;
        L_0x0833:
            r5 = android.graphics.BitmapFactory.decodeByteArray(r13, r8, r7, r11);	 Catch:{ Throwable -> 0x0839 }
            goto L_0x083d;
        L_0x0838:
            r12 = 0;
        L_0x0839:
            r2 = r5;
            goto L_0x094f;
        L_0x083c:
            r12 = 0;
        L_0x083d:
            r7 = 0;
        L_0x083e:
            if (r5 != 0) goto L_0x0859;
        L_0x0840:
            if (r16 == 0) goto L_0x0855;
        L_0x0842:
            r3 = r2.length();	 Catch:{ Throwable -> 0x093f }
            r8 = 0;
            r0 = (r3 > r8 ? 1 : (r3 == r8 ? 0 : -1));
            if (r0 == 0) goto L_0x0852;
        L_0x084c:
            r0 = r1.cacheImage;	 Catch:{ Throwable -> 0x093f }
            r0 = r0.filter;	 Catch:{ Throwable -> 0x093f }
            if (r0 != 0) goto L_0x0855;
        L_0x0852:
            r2.delete();	 Catch:{ Throwable -> 0x093f }
        L_0x0855:
            r2 = r5;
            r0 = 0;
            goto L_0x0938;
        L_0x0859:
            r2 = r1.cacheImage;	 Catch:{ Throwable -> 0x093e }
            r2 = r2.filter;	 Catch:{ Throwable -> 0x093e }
            if (r2 == 0) goto L_0x0926;
        L_0x085f:
            r2 = r5.getWidth();	 Catch:{ Throwable -> 0x093e }
            r2 = (float) r2;	 Catch:{ Throwable -> 0x093e }
            r6 = r5.getHeight();	 Catch:{ Throwable -> 0x093e }
            r6 = (float) r6;	 Catch:{ Throwable -> 0x093e }
            r8 = r11.inPurgeable;	 Catch:{ Throwable -> 0x093e }
            if (r8 != 0) goto L_0x08a4;
        L_0x086d:
            r8 = (r4 > r20 ? 1 : (r4 == r20 ? 0 : -1));
            if (r8 == 0) goto L_0x08a4;
        L_0x0871:
            r8 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1));
            if (r8 == 0) goto L_0x08a4;
        L_0x0875:
            r8 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
            r13 = r4 + r8;
            r8 = (r2 > r13 ? 1 : (r2 == r13 ? 0 : -1));
            if (r8 <= 0) goto L_0x08a4;
        L_0x087d:
            r8 = (r2 > r6 ? 1 : (r2 == r6 ? 0 : -1));
            if (r8 <= 0) goto L_0x0891;
        L_0x0881:
            r8 = (r4 > r23 ? 1 : (r4 == r23 ? 0 : -1));
            if (r8 <= 0) goto L_0x0891;
        L_0x0885:
            r8 = r2 / r4;
            r4 = (int) r4;
            r8 = r6 / r8;
            r8 = (int) r8;
            r9 = 1;
            r4 = org.telegram.messenger.Bitmaps.createScaledBitmap(r5, r4, r8, r9);	 Catch:{ Throwable -> 0x093f }
            goto L_0x089e;
        L_0x0891:
            r9 = 1;
            r4 = r6 / r23;
            r4 = r2 / r4;
            r4 = (int) r4;	 Catch:{ Throwable -> 0x093f }
            r8 = r23;
            r8 = (int) r8;	 Catch:{ Throwable -> 0x093f }
            r4 = org.telegram.messenger.Bitmaps.createScaledBitmap(r5, r4, r8, r9);	 Catch:{ Throwable -> 0x093f }
        L_0x089e:
            if (r5 == r4) goto L_0x08a5;
        L_0x08a0:
            r5.recycle();	 Catch:{ Throwable -> 0x093f }
            goto L_0x08a6;
        L_0x08a4:
            r9 = 1;
        L_0x08a5:
            r4 = r5;
        L_0x08a6:
            if (r4 == 0) goto L_0x0923;
        L_0x08a8:
            if (r3 == 0) goto L_0x08ea;
        L_0x08aa:
            r3 = r4.getWidth();	 Catch:{ Throwable -> 0x08e6 }
            r5 = r4.getHeight();	 Catch:{ Throwable -> 0x08e6 }
            r3 = r3 * r5;
            r5 = 22500; // 0x57e4 float:3.1529E-41 double:1.11165E-319;
            if (r3 <= r5) goto L_0x08c2;
        L_0x08b8:
            r3 = 100;
            r5 = 100;
            r8 = 0;
            r3 = org.telegram.messenger.Bitmaps.createScaledBitmap(r4, r3, r5, r8);	 Catch:{ Throwable -> 0x08e7 }
            goto L_0x08c4;
        L_0x08c2:
            r8 = 0;
            r3 = r4;
        L_0x08c4:
            r5 = r11.inPurgeable;	 Catch:{ Throwable -> 0x08e7 }
            if (r5 == 0) goto L_0x08ca;
        L_0x08c8:
            r5 = 0;
            goto L_0x08cb;
        L_0x08ca:
            r5 = 1;
        L_0x08cb:
            r10 = r3.getWidth();	 Catch:{ Throwable -> 0x08e7 }
            r13 = r3.getHeight();	 Catch:{ Throwable -> 0x08e7 }
            r14 = r3.getRowBytes();	 Catch:{ Throwable -> 0x08e7 }
            r5 = org.telegram.messenger.Utilities.needInvert(r3, r5, r10, r13, r14);	 Catch:{ Throwable -> 0x08e7 }
            if (r5 == 0) goto L_0x08df;
        L_0x08dd:
            r5 = 1;
            goto L_0x08e0;
        L_0x08df:
            r5 = 0;
        L_0x08e0:
            if (r3 == r4) goto L_0x08ec;
        L_0x08e2:
            r3.recycle();	 Catch:{ Throwable -> 0x091b }
            goto L_0x08ec;
        L_0x08e6:
            r8 = 0;
        L_0x08e7:
            r2 = r4;
            goto L_0x0940;
        L_0x08ea:
            r8 = 0;
            r5 = 0;
        L_0x08ec:
            if (r0 == 0) goto L_0x0920;
        L_0x08ee:
            r0 = NUM; // 0x42CLASSNAME float:100.0 double:5.53552857E-315;
            r3 = (r6 > r0 ? 1 : (r6 == r0 ? 0 : -1));
            if (r3 >= 0) goto L_0x0920;
        L_0x08f4:
            r0 = (r2 > r0 ? 1 : (r2 == r0 ? 0 : -1));
            if (r0 >= 0) goto L_0x0920;
        L_0x08f8:
            r0 = r4.getConfig();	 Catch:{ Throwable -> 0x091b }
            r2 = android.graphics.Bitmap.Config.ARGB_8888;	 Catch:{ Throwable -> 0x091b }
            if (r0 != r2) goto L_0x0918;
        L_0x0900:
            r14 = 3;
            r0 = r11.inPurgeable;	 Catch:{ Throwable -> 0x091b }
            if (r0 == 0) goto L_0x0907;
        L_0x0905:
            r15 = 0;
            goto L_0x0908;
        L_0x0907:
            r15 = 1;
        L_0x0908:
            r16 = r4.getWidth();	 Catch:{ Throwable -> 0x091b }
            r17 = r4.getHeight();	 Catch:{ Throwable -> 0x091b }
            r18 = r4.getRowBytes();	 Catch:{ Throwable -> 0x091b }
            r13 = r4;
            org.telegram.messenger.Utilities.blurBitmap(r13, r14, r15, r16, r17, r18);	 Catch:{ Throwable -> 0x091b }
        L_0x0918:
            r0 = r5;
            r8 = 1;
            goto L_0x0921;
        L_0x091b:
            r2 = r4;
            r24 = r7;
            r7 = r5;
            goto L_0x0952;
        L_0x0920:
            r0 = r5;
        L_0x0921:
            r5 = r4;
            goto L_0x0928;
        L_0x0923:
            r8 = 0;
            r5 = r4;
            goto L_0x0927;
        L_0x0926:
            r8 = 0;
        L_0x0927:
            r0 = 0;
        L_0x0928:
            if (r8 != 0) goto L_0x0937;
        L_0x092a:
            r2 = r11.inPurgeable;	 Catch:{ Throwable -> 0x0932 }
            if (r2 == 0) goto L_0x0937;
        L_0x092e:
            org.telegram.messenger.Utilities.pinBitmap(r5);	 Catch:{ Throwable -> 0x0932 }
            goto L_0x0937;
        L_0x0932:
            r2 = r5;
            r24 = r7;
            r7 = r0;
            goto L_0x0952;
        L_0x0937:
            r2 = r5;
        L_0x0938:
            r34 = r7;
            r7 = r0;
            r0 = r34;
            goto L_0x0954;
        L_0x093e:
            r8 = 0;
        L_0x093f:
            r2 = r5;
        L_0x0940:
            r24 = r7;
            r7 = 0;
            goto L_0x0952;
        L_0x0944:
            r0 = move-exception;
            r8 = 0;
            r12 = 0;
        L_0x0947:
            monitor-exit(r5);	 Catch:{ all -> 0x0949 }
            throw r0;	 Catch:{ Throwable -> 0x094d }
        L_0x0949:
            r0 = move-exception;
            goto L_0x0947;
        L_0x094b:
            r8 = 0;
            r12 = 0;
        L_0x094d:
            r2 = r30;
        L_0x094f:
            r7 = 0;
        L_0x0950:
            r24 = 0;
        L_0x0952:
            r0 = r24;
        L_0x0954:
            java.lang.Thread.interrupted();
            if (r7 != 0) goto L_0x0969;
        L_0x0959:
            if (r0 == 0) goto L_0x095c;
        L_0x095b:
            goto L_0x0969;
        L_0x095c:
            if (r2 == 0) goto L_0x0964;
        L_0x095e:
            r5 = new android.graphics.drawable.BitmapDrawable;
            r5.<init>(r2);
            goto L_0x0965;
        L_0x0964:
            r5 = r12;
        L_0x0965:
            r1.onPostExecute(r5);
            goto L_0x0975;
        L_0x0969:
            if (r2 == 0) goto L_0x0971;
        L_0x096b:
            r5 = new org.telegram.messenger.ExtendedBitmapDrawable;
            r5.<init>(r2, r7, r0);
            goto L_0x0972;
        L_0x0971:
            r5 = r12;
        L_0x0972:
            r1.onPostExecute(r5);
        L_0x0975:
            return;
        L_0x0976:
            r0 = move-exception;
            monitor-exit(r2);	 Catch:{ all -> 0x0976 }
            goto L_0x097a;
        L_0x0979:
            throw r0;
        L_0x097a:
            goto L_0x0979;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ImageLoader$CacheOutTask.run():void");
        }

        private void onPostExecute(Drawable drawable) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$ImageLoader$CacheOutTask$P-Q-SglLFg8CKw46QDkF5nN_7Ko(this, drawable));
        }

        public /* synthetic */ void lambda$onPostExecute$1$ImageLoader$CacheOutTask(Drawable drawable) {
            String str = null;
            Drawable drawable2;
            if (drawable instanceof LottieDrawable) {
                drawable = (LottieDrawable) drawable;
                drawable2 = (Drawable) ImageLoader.this.lottieMemCache.get(this.cacheImage.key);
                if (drawable2 == null) {
                    ImageLoader.this.lottieMemCache.put(this.cacheImage.key, drawable);
                } else {
                    drawable = drawable2;
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
        private Document parentDocument;

        private ThumbGenerateInfo() {
            this.imageReceiverArray = new ArrayList();
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

        /* JADX WARNING: Removed duplicated region for block: B:59:0x0145 A:{Catch:{ Exception -> 0x014e, Throwable -> 0x0173 }} */
        /* JADX WARNING: Removed duplicated region for block: B:58:0x0142 A:{Catch:{ Exception -> 0x014e, Throwable -> 0x0173 }} */
        public void run() {
            /*
            r10 = this;
            r0 = ".jpg";
            r1 = r10.info;	 Catch:{ Throwable -> 0x0173 }
            if (r1 != 0) goto L_0x000a;
        L_0x0006:
            r10.removeTask();	 Catch:{ Throwable -> 0x0173 }
            return;
        L_0x000a:
            r1 = new java.lang.StringBuilder;	 Catch:{ Throwable -> 0x0173 }
            r1.<init>();	 Catch:{ Throwable -> 0x0173 }
            r2 = "q_";
            r1.append(r2);	 Catch:{ Throwable -> 0x0173 }
            r2 = r10.info;	 Catch:{ Throwable -> 0x0173 }
            r2 = r2.parentDocument;	 Catch:{ Throwable -> 0x0173 }
            r2 = r2.dc_id;	 Catch:{ Throwable -> 0x0173 }
            r1.append(r2);	 Catch:{ Throwable -> 0x0173 }
            r2 = "_";
            r1.append(r2);	 Catch:{ Throwable -> 0x0173 }
            r2 = r10.info;	 Catch:{ Throwable -> 0x0173 }
            r2 = r2.parentDocument;	 Catch:{ Throwable -> 0x0173 }
            r2 = r2.id;	 Catch:{ Throwable -> 0x0173 }
            r1.append(r2);	 Catch:{ Throwable -> 0x0173 }
            r1 = r1.toString();	 Catch:{ Throwable -> 0x0173 }
            r2 = new java.io.File;	 Catch:{ Throwable -> 0x0173 }
            r3 = 4;
            r4 = org.telegram.messenger.FileLoader.getDirectory(r3);	 Catch:{ Throwable -> 0x0173 }
            r5 = new java.lang.StringBuilder;	 Catch:{ Throwable -> 0x0173 }
            r5.<init>();	 Catch:{ Throwable -> 0x0173 }
            r5.append(r1);	 Catch:{ Throwable -> 0x0173 }
            r5.append(r0);	 Catch:{ Throwable -> 0x0173 }
            r5 = r5.toString();	 Catch:{ Throwable -> 0x0173 }
            r2.<init>(r4, r5);	 Catch:{ Throwable -> 0x0173 }
            r4 = r2.exists();	 Catch:{ Throwable -> 0x0173 }
            if (r4 != 0) goto L_0x016f;
        L_0x0052:
            r4 = r10.originalPath;	 Catch:{ Throwable -> 0x0173 }
            r4 = r4.exists();	 Catch:{ Throwable -> 0x0173 }
            if (r4 != 0) goto L_0x005c;
        L_0x005a:
            goto L_0x016f;
        L_0x005c:
            r4 = r10.info;	 Catch:{ Throwable -> 0x0173 }
            r4 = r4.big;	 Catch:{ Throwable -> 0x0173 }
            if (r4 == 0) goto L_0x0071;
        L_0x0064:
            r3 = org.telegram.messenger.AndroidUtilities.displaySize;	 Catch:{ Throwable -> 0x0173 }
            r3 = r3.x;	 Catch:{ Throwable -> 0x0173 }
            r4 = org.telegram.messenger.AndroidUtilities.displaySize;	 Catch:{ Throwable -> 0x0173 }
            r4 = r4.y;	 Catch:{ Throwable -> 0x0173 }
            r3 = java.lang.Math.max(r3, r4);	 Catch:{ Throwable -> 0x0173 }
            goto L_0x0084;
        L_0x0071:
            r4 = 180; // 0xb4 float:2.52E-43 double:8.9E-322;
            r5 = org.telegram.messenger.AndroidUtilities.displaySize;	 Catch:{ Throwable -> 0x0173 }
            r5 = r5.x;	 Catch:{ Throwable -> 0x0173 }
            r6 = org.telegram.messenger.AndroidUtilities.displaySize;	 Catch:{ Throwable -> 0x0173 }
            r6 = r6.y;	 Catch:{ Throwable -> 0x0173 }
            r5 = java.lang.Math.min(r5, r6);	 Catch:{ Throwable -> 0x0173 }
            r5 = r5 / r3;
            r3 = java.lang.Math.min(r4, r5);	 Catch:{ Throwable -> 0x0173 }
        L_0x0084:
            r4 = r10.mediaType;	 Catch:{ Throwable -> 0x0173 }
            r5 = 0;
            r6 = 1;
            r7 = 0;
            if (r4 != 0) goto L_0x0097;
        L_0x008b:
            r0 = r10.originalPath;	 Catch:{ Throwable -> 0x0173 }
            r0 = r0.toString();	 Catch:{ Throwable -> 0x0173 }
            r4 = (float) r3;	 Catch:{ Throwable -> 0x0173 }
            r7 = org.telegram.messenger.ImageLoader.loadBitmap(r0, r7, r4, r4, r5);	 Catch:{ Throwable -> 0x0173 }
            goto L_0x0100;
        L_0x0097:
            r4 = r10.mediaType;	 Catch:{ Throwable -> 0x0173 }
            r8 = 2;
            if (r4 != r8) goto L_0x00b1;
        L_0x009c:
            r0 = r10.originalPath;	 Catch:{ Throwable -> 0x0173 }
            r0 = r0.toString();	 Catch:{ Throwable -> 0x0173 }
            r4 = r10.info;	 Catch:{ Throwable -> 0x0173 }
            r4 = r4.big;	 Catch:{ Throwable -> 0x0173 }
            if (r4 == 0) goto L_0x00ab;
        L_0x00aa:
            goto L_0x00ac;
        L_0x00ab:
            r8 = 1;
        L_0x00ac:
            r7 = android.media.ThumbnailUtils.createVideoThumbnail(r0, r8);	 Catch:{ Throwable -> 0x0173 }
            goto L_0x0100;
        L_0x00b1:
            r4 = r10.mediaType;	 Catch:{ Throwable -> 0x0173 }
            r9 = 3;
            if (r4 != r9) goto L_0x0100;
        L_0x00b6:
            r4 = r10.originalPath;	 Catch:{ Throwable -> 0x0173 }
            r4 = r4.toString();	 Catch:{ Throwable -> 0x0173 }
            r4 = r4.toLowerCase();	 Catch:{ Throwable -> 0x0173 }
            r9 = "mp4";
            r9 = r4.endsWith(r9);	 Catch:{ Throwable -> 0x0173 }
            if (r9 == 0) goto L_0x00dd;
        L_0x00c8:
            r0 = r10.originalPath;	 Catch:{ Throwable -> 0x0173 }
            r0 = r0.toString();	 Catch:{ Throwable -> 0x0173 }
            r4 = r10.info;	 Catch:{ Throwable -> 0x0173 }
            r4 = r4.big;	 Catch:{ Throwable -> 0x0173 }
            if (r4 == 0) goto L_0x00d7;
        L_0x00d6:
            goto L_0x00d8;
        L_0x00d7:
            r8 = 1;
        L_0x00d8:
            r7 = android.media.ThumbnailUtils.createVideoThumbnail(r0, r8);	 Catch:{ Throwable -> 0x0173 }
            goto L_0x0100;
        L_0x00dd:
            r0 = r4.endsWith(r0);	 Catch:{ Throwable -> 0x0173 }
            if (r0 != 0) goto L_0x00fb;
        L_0x00e3:
            r0 = ".jpeg";
            r0 = r4.endsWith(r0);	 Catch:{ Throwable -> 0x0173 }
            if (r0 != 0) goto L_0x00fb;
        L_0x00eb:
            r0 = ".png";
            r0 = r4.endsWith(r0);	 Catch:{ Throwable -> 0x0173 }
            if (r0 != 0) goto L_0x00fb;
        L_0x00f3:
            r0 = ".gif";
            r0 = r4.endsWith(r0);	 Catch:{ Throwable -> 0x0173 }
            if (r0 == 0) goto L_0x0100;
        L_0x00fb:
            r0 = (float) r3;	 Catch:{ Throwable -> 0x0173 }
            r7 = org.telegram.messenger.ImageLoader.loadBitmap(r4, r7, r0, r0, r5);	 Catch:{ Throwable -> 0x0173 }
        L_0x0100:
            if (r7 != 0) goto L_0x0106;
        L_0x0102:
            r10.removeTask();	 Catch:{ Throwable -> 0x0173 }
            return;
        L_0x0106:
            r0 = r7.getWidth();	 Catch:{ Throwable -> 0x0173 }
            r4 = r7.getHeight();	 Catch:{ Throwable -> 0x0173 }
            if (r0 == 0) goto L_0x016b;
        L_0x0110:
            if (r4 != 0) goto L_0x0113;
        L_0x0112:
            goto L_0x016b;
        L_0x0113:
            r0 = (float) r0;	 Catch:{ Throwable -> 0x0173 }
            r3 = (float) r3;	 Catch:{ Throwable -> 0x0173 }
            r5 = r0 / r3;
            r4 = (float) r4;	 Catch:{ Throwable -> 0x0173 }
            r3 = r4 / r3;
            r3 = java.lang.Math.min(r5, r3);	 Catch:{ Throwable -> 0x0173 }
            r5 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
            r5 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1));
            if (r5 <= 0) goto L_0x0132;
        L_0x0124:
            r0 = r0 / r3;
            r0 = (int) r0;	 Catch:{ Throwable -> 0x0173 }
            r4 = r4 / r3;
            r3 = (int) r4;	 Catch:{ Throwable -> 0x0173 }
            r0 = org.telegram.messenger.Bitmaps.createScaledBitmap(r7, r0, r3, r6);	 Catch:{ Throwable -> 0x0173 }
            if (r0 == r7) goto L_0x0132;
        L_0x012e:
            r7.recycle();	 Catch:{ Throwable -> 0x0173 }
            goto L_0x0133;
        L_0x0132:
            r0 = r7;
        L_0x0133:
            r3 = new java.io.FileOutputStream;	 Catch:{ Throwable -> 0x0173 }
            r3.<init>(r2);	 Catch:{ Throwable -> 0x0173 }
            r2 = android.graphics.Bitmap.CompressFormat.JPEG;	 Catch:{ Throwable -> 0x0173 }
            r4 = r10.info;	 Catch:{ Throwable -> 0x0173 }
            r4 = r4.big;	 Catch:{ Throwable -> 0x0173 }
            if (r4 == 0) goto L_0x0145;
        L_0x0142:
            r4 = 83;
            goto L_0x0147;
        L_0x0145:
            r4 = 60;
        L_0x0147:
            r0.compress(r2, r4, r3);	 Catch:{ Throwable -> 0x0173 }
            r3.close();	 Catch:{ Exception -> 0x014e }
            goto L_0x0152;
        L_0x014e:
            r2 = move-exception;
            org.telegram.messenger.FileLog.e(r2);	 Catch:{ Throwable -> 0x0173 }
        L_0x0152:
            r2 = new android.graphics.drawable.BitmapDrawable;	 Catch:{ Throwable -> 0x0173 }
            r2.<init>(r0);	 Catch:{ Throwable -> 0x0173 }
            r0 = new java.util.ArrayList;	 Catch:{ Throwable -> 0x0173 }
            r3 = r10.info;	 Catch:{ Throwable -> 0x0173 }
            r3 = r3.imageReceiverArray;	 Catch:{ Throwable -> 0x0173 }
            r0.<init>(r3);	 Catch:{ Throwable -> 0x0173 }
            r3 = new org.telegram.messenger.-$$Lambda$ImageLoader$ThumbGenerateTask$93CLASSNAME-AxUp0yfQKw5ZLfHAidaSg;	 Catch:{ Throwable -> 0x0173 }
            r3.<init>(r10, r1, r0, r2);	 Catch:{ Throwable -> 0x0173 }
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r3);	 Catch:{ Throwable -> 0x0173 }
            goto L_0x017a;
        L_0x016b:
            r10.removeTask();	 Catch:{ Throwable -> 0x0173 }
            return;
        L_0x016f:
            r10.removeTask();	 Catch:{ Throwable -> 0x0173 }
            return;
        L_0x0173:
            r0 = move-exception;
            org.telegram.messenger.FileLog.e(r0);
            r10.removeTask();
        L_0x017a:
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ImageLoader$ThumbGenerateTask.run():void");
        }

        public /* synthetic */ void lambda$run$1$ImageLoader$ThumbGenerateTask(String str, ArrayList arrayList, BitmapDrawable bitmapDrawable) {
            removeTask();
            if (this.info.filter != null) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(str);
                stringBuilder.append("@");
                stringBuilder.append(this.info.filter);
                str = stringBuilder.toString();
            }
            for (int i = 0; i < arrayList.size(); i++) {
                ((ImageReceiver) arrayList.get(i)).setImageBitmapByKey(bitmapDrawable, str, 0, false);
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
        this.lottieMemCache = new LruCache<LottieDrawable>(5) {
            /* Access modifiers changed, original: protected */
            public int sizeOf(String str, LottieDrawable lottieDrawable) {
                return 1;
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

    public boolean isInCache(String str) {
        return this.memCache.get(str) != null;
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
                thumbGenerateInfo.imageReceiverArray.remove(imageReceiver);
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

    private void createLoadOperationForImageReceiver(ImageReceiver imageReceiver, String str, String str2, String str3, ImageLocation imageLocation, String str4, int i, int i2, int i3, int i4) {
        ImageReceiver imageReceiver2 = imageReceiver;
        int i5 = i3;
        if (imageReceiver2 != null && str2 != null && str != null && imageLocation != null) {
            int tag = imageReceiver2.getTag(i5);
            if (tag == 0) {
                tag = this.lastImageNum;
                imageReceiver2.setTag(tag, i5);
                this.lastImageNum++;
                if (this.lastImageNum == Integer.MAX_VALUE) {
                    this.lastImageNum = 0;
                }
            }
            int i6 = tag;
            boolean isNeedsQualityThumb = imageReceiver.isNeedsQualityThumb();
            Object parentObject = imageReceiver.getParentObject();
            Document qulityThumbDocument = imageReceiver.getQulityThumbDocument();
            boolean isShouldGenerateQualityThumb = imageReceiver.isShouldGenerateQualityThumb();
            int currentAccount = imageReceiver.getCurrentAccount();
            boolean z = i5 == 0 && imageReceiver.isCurrentKeyQuality();
            -$$Lambda$ImageLoader$bsD7o_FB_o0LApsZQkahjvu_ZzU -__lambda_imageloader_bsd7o_fb_o0lapszqkahjvu_zzu = r0;
            DispatchQueue dispatchQueue = this.imageLoadQueue;
            -$$Lambda$ImageLoader$bsD7o_FB_o0LApsZQkahjvu_ZzU -__lambda_imageloader_bsd7o_fb_o0lapszqkahjvu_zzu2 = new -$$Lambda$ImageLoader$bsD7o_FB_o0LApsZQkahjvu_ZzU(this, i4, str2, str, i6, imageReceiver, str4, i3, imageLocation, z, parentObject, qulityThumbDocument, isNeedsQualityThumb, isShouldGenerateQualityThumb, i2, i, str3, currentAccount);
            dispatchQueue.postRunnable(-__lambda_imageloader_bsd7o_fb_o0lapszqkahjvu_zzu);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:60:0x014c  */
    /* JADX WARNING: Removed duplicated region for block: B:64:0x015a  */
    /* JADX WARNING: Removed duplicated region for block: B:77:0x01a2  */
    /* JADX WARNING: Removed duplicated region for block: B:66:0x015e  */
    /* JADX WARNING: Removed duplicated region for block: B:162:0x0375  */
    /* JADX WARNING: Removed duplicated region for block: B:214:0x048e  */
    /* JADX WARNING: Removed duplicated region for block: B:213:0x0486  */
    /* JADX WARNING: Missing block: B:61:0x0155, code skipped:
            if (r2.exists() == false) goto L_0x0157;
     */
    /* JADX WARNING: Missing block: B:122:0x0260, code skipped:
            if (r3 == false) goto L_0x02ab;
     */
    public /* synthetic */ void lambda$createLoadOperationForImageReceiver$5$ImageLoader(int r21, java.lang.String r22, java.lang.String r23, int r24, org.telegram.messenger.ImageReceiver r25, java.lang.String r26, int r27, org.telegram.messenger.ImageLocation r28, boolean r29, java.lang.Object r30, org.telegram.tgnet.TLRPC.Document r31, boolean r32, boolean r33, int r34, int r35, java.lang.String r36, int r37) {
        /*
        r20 = this;
        r0 = r20;
        r1 = r21;
        r2 = r22;
        r3 = r23;
        r4 = r24;
        r5 = r25;
        r6 = r26;
        r7 = r27;
        r8 = r28;
        r9 = r30;
        r10 = r31;
        r11 = r34;
        r12 = r35;
        r13 = 2;
        if (r1 == r13) goto L_0x0058;
    L_0x001d:
        r14 = r0.imageLoadingByUrl;
        r14 = r14.get(r2);
        r14 = (org.telegram.messenger.ImageLoader.CacheImage) r14;
        r13 = r0.imageLoadingByKeys;
        r13 = r13.get(r3);
        r13 = (org.telegram.messenger.ImageLoader.CacheImage) r13;
        r15 = r0.imageLoadingByTag;
        r15 = r15.get(r4);
        r15 = (org.telegram.messenger.ImageLoader.CacheImage) r15;
        if (r15 == 0) goto L_0x0046;
    L_0x0037:
        if (r15 != r13) goto L_0x003b;
    L_0x0039:
        r15 = 1;
        goto L_0x0047;
    L_0x003b:
        if (r15 != r14) goto L_0x0043;
    L_0x003d:
        if (r13 != 0) goto L_0x0039;
    L_0x003f:
        r15.replaceImageReceiver(r5, r3, r6, r7);
        goto L_0x0039;
    L_0x0043:
        r15.removeImageReceiver(r5);
    L_0x0046:
        r15 = 0;
    L_0x0047:
        if (r15 != 0) goto L_0x004f;
    L_0x0049:
        if (r13 == 0) goto L_0x004f;
    L_0x004b:
        r13.addImageReceiver(r5, r3, r6, r7);
        r15 = 1;
    L_0x004f:
        if (r15 != 0) goto L_0x0059;
    L_0x0051:
        if (r14 == 0) goto L_0x0059;
    L_0x0053:
        r14.addImageReceiver(r5, r3, r6, r7);
        r15 = 1;
        goto L_0x0059;
    L_0x0058:
        r15 = 0;
    L_0x0059:
        if (r15 != 0) goto L_0x0495;
    L_0x005b:
        r13 = r8.path;
        r14 = "_";
        r15 = "athumb";
        r17 = 3;
        r18 = 4;
        if (r13 == 0) goto L_0x00c8;
    L_0x0067:
        r4 = "http";
        r4 = r13.startsWith(r4);
        if (r4 != 0) goto L_0x00be;
    L_0x006f:
        r4 = r13.startsWith(r15);
        if (r4 != 0) goto L_0x00be;
    L_0x0075:
        r4 = "thumb://";
        r4 = r13.startsWith(r4);
        r10 = ":";
        if (r4 == 0) goto L_0x0097;
    L_0x007f:
        r4 = 8;
        r4 = r13.indexOf(r10, r4);
        if (r4 < 0) goto L_0x0095;
    L_0x0087:
        r10 = new java.io.File;
        r16 = 1;
        r4 = r4 + 1;
        r4 = r13.substring(r4);
        r10.<init>(r4);
        goto L_0x00bc;
    L_0x0095:
        r10 = 0;
        goto L_0x00bc;
    L_0x0097:
        r4 = "vthumb://";
        r4 = r13.startsWith(r4);
        if (r4 == 0) goto L_0x00b6;
    L_0x00a0:
        r4 = 9;
        r4 = r13.indexOf(r10, r4);
        if (r4 < 0) goto L_0x0095;
    L_0x00a8:
        r10 = new java.io.File;
        r16 = 1;
        r4 = r4 + 1;
        r4 = r13.substring(r4);
        r10.<init>(r4);
        goto L_0x00bc;
    L_0x00b6:
        r4 = new java.io.File;
        r4.<init>(r13);
        r10 = r4;
    L_0x00bc:
        r4 = 1;
        goto L_0x00c0;
    L_0x00be:
        r4 = 0;
        r10 = 0;
    L_0x00c0:
        r9 = r10;
        r19 = r15;
        r2 = 0;
        r3 = 2;
        r15 = r4;
        goto L_0x01b7;
    L_0x00c8:
        if (r1 != 0) goto L_0x01af;
    L_0x00ca:
        if (r29 == 0) goto L_0x01af;
    L_0x00cc:
        r13 = r9 instanceof org.telegram.messenger.MessageObject;
        if (r13 == 0) goto L_0x00ee;
    L_0x00d0:
        r10 = r9;
        r10 = (org.telegram.messenger.MessageObject) r10;
        r13 = r10.getDocument();
        r3 = r10.messageOwner;
        r31 = r13;
        r13 = r3.attachPath;
        r3 = org.telegram.messenger.FileLoader.getPathToMessage(r3);
        r10 = r10.getFileType();
        r19 = r15;
        r15 = r10;
        r10 = r31;
        r31 = r3;
        r3 = 0;
        goto L_0x010e;
    L_0x00ee:
        if (r10 == 0) goto L_0x0106;
    L_0x00f0:
        r3 = 1;
        r13 = org.telegram.messenger.FileLoader.getPathToAttach(r10, r3);
        r3 = org.telegram.messenger.MessageObject.isVideoDocument(r31);
        if (r3 == 0) goto L_0x00fd;
    L_0x00fb:
        r3 = 2;
        goto L_0x00fe;
    L_0x00fd:
        r3 = 3;
    L_0x00fe:
        r31 = r13;
        r19 = r15;
        r13 = 0;
        r15 = r3;
        r3 = 1;
        goto L_0x010e;
    L_0x0106:
        r19 = r15;
        r31 = 0;
        r3 = 0;
        r10 = 0;
        r13 = 0;
        r15 = 0;
    L_0x010e:
        if (r10 == 0) goto L_0x01a8;
    L_0x0110:
        if (r32 == 0) goto L_0x0144;
    L_0x0112:
        r9 = new java.io.File;
        r7 = org.telegram.messenger.FileLoader.getDirectory(r18);
        r12 = new java.lang.StringBuilder;
        r12.<init>();
        r11 = "q_";
        r12.append(r11);
        r11 = r10.dc_id;
        r12.append(r11);
        r12.append(r14);
        r1 = r10.id;
        r12.append(r1);
        r1 = ".jpg";
        r12.append(r1);
        r1 = r12.toString();
        r9.<init>(r7, r1);
        r1 = r9.exists();
        if (r1 != 0) goto L_0x0142;
    L_0x0141:
        goto L_0x0144;
    L_0x0142:
        r1 = 1;
        goto L_0x0146;
    L_0x0144:
        r1 = 0;
        r9 = 0;
    L_0x0146:
        r2 = android.text.TextUtils.isEmpty(r13);
        if (r2 != 0) goto L_0x0157;
    L_0x014c:
        r2 = new java.io.File;
        r2.<init>(r13);
        r7 = r2.exists();
        if (r7 != 0) goto L_0x0158;
    L_0x0157:
        r2 = 0;
    L_0x0158:
        if (r2 != 0) goto L_0x015c;
    L_0x015a:
        r2 = r31;
    L_0x015c:
        if (r9 != 0) goto L_0x01a2;
    L_0x015e:
        r1 = org.telegram.messenger.FileLoader.getAttachFileName(r10);
        r7 = r0.waitingForQualityThumb;
        r7 = r7.get(r1);
        r7 = (org.telegram.messenger.ImageLoader.ThumbGenerateInfo) r7;
        if (r7 != 0) goto L_0x0180;
    L_0x016c:
        r7 = new org.telegram.messenger.ImageLoader$ThumbGenerateInfo;
        r8 = 0;
        r7.<init>(r0, r8);
        r7.parentDocument = r10;
        r7.filter = r6;
        r7.big = r3;
        r3 = r0.waitingForQualityThumb;
        r3.put(r1, r7);
    L_0x0180:
        r3 = r7.imageReceiverArray;
        r3 = r3.contains(r5);
        if (r3 != 0) goto L_0x0191;
    L_0x018a:
        r3 = r7.imageReceiverArray;
        r3.add(r5);
    L_0x0191:
        r3 = r0.waitingForQualityThumbByTag;
        r3.put(r4, r1);
        r1 = r2.exists();
        if (r1 == 0) goto L_0x01a1;
    L_0x019c:
        if (r33 == 0) goto L_0x01a1;
    L_0x019e:
        r0.generateThumb(r15, r2, r7);
    L_0x01a1:
        return;
    L_0x01a2:
        r2 = r1;
        r3 = 2;
        r15 = 1;
        r1 = r21;
        goto L_0x01b7;
    L_0x01a8:
        r1 = r21;
        r2 = 0;
        r3 = 2;
        r9 = 0;
        r15 = 1;
        goto L_0x01b7;
    L_0x01af:
        r19 = r15;
        r1 = r21;
        r2 = 0;
        r3 = 2;
        r9 = 0;
        r15 = 0;
    L_0x01b7:
        if (r1 == r3) goto L_0x0495;
    L_0x01b9:
        r3 = r28.isEncrypted();
        r4 = new org.telegram.messenger.ImageLoader$CacheImage;
        r7 = 0;
        r4.<init>(r0, r7);
        if (r29 != 0) goto L_0x0210;
    L_0x01c5:
        r7 = r8.webFile;
        r7 = org.telegram.messenger.MessageObject.isGifDocument(r7);
        if (r7 != 0) goto L_0x020d;
    L_0x01cd:
        r7 = r8.document;
        r7 = org.telegram.messenger.MessageObject.isGifDocument(r7);
        if (r7 != 0) goto L_0x020d;
    L_0x01d5:
        r7 = r8.document;
        r7 = org.telegram.messenger.MessageObject.isRoundVideoDocument(r7);
        if (r7 == 0) goto L_0x01de;
    L_0x01dd:
        goto L_0x020d;
    L_0x01de:
        r7 = r8.path;
        if (r7 == 0) goto L_0x0210;
    L_0x01e2:
        r10 = "vthumb";
        r10 = r7.startsWith(r10);
        if (r10 != 0) goto L_0x0210;
    L_0x01eb:
        r10 = "thumb";
        r10 = r7.startsWith(r10);
        if (r10 != 0) goto L_0x0210;
    L_0x01f3:
        r10 = "jpg";
        r7 = getHttpUrlExtension(r7, r10);
        r10 = "mp4";
        r10 = r7.equals(r10);
        if (r10 != 0) goto L_0x0209;
    L_0x0201:
        r10 = "gif";
        r7 = r7.equals(r10);
        if (r7 == 0) goto L_0x0210;
    L_0x0209:
        r7 = 1;
        r4.animatedFile = r7;
        goto L_0x0210;
    L_0x020d:
        r7 = 1;
        r4.animatedFile = r7;
    L_0x0210:
        if (r9 != 0) goto L_0x034f;
    L_0x0212:
        r7 = r8.photoSize;
        r7 = r7 instanceof org.telegram.tgnet.TLRPC.TL_photoStrippedSize;
        r10 = "g";
        if (r7 == 0) goto L_0x0224;
    L_0x021a:
        r11 = r22;
        r7 = r34;
        r12 = r35;
        r14 = 0;
        r15 = 1;
        goto L_0x0340;
    L_0x0224:
        r7 = r8.secureDocument;
        if (r7 == 0) goto L_0x024a;
    L_0x0228:
        r4.secureDocument = r7;
        r3 = r4.secureDocument;
        r3 = r3.secureFile;
        r3 = r3.dc_id;
        r7 = -NUM; // 0xfffffffvar_ float:-0.0 double:NaN;
        if (r3 != r7) goto L_0x0236;
    L_0x0234:
        r15 = 1;
        goto L_0x0237;
    L_0x0236:
        r15 = 0;
    L_0x0237:
        r3 = new java.io.File;
        r7 = org.telegram.messenger.FileLoader.getDirectory(r18);
        r11 = r22;
        r3.<init>(r7, r11);
        r7 = r34;
        r12 = r35;
    L_0x0246:
        r9 = r3;
    L_0x0247:
        r14 = 0;
        goto L_0x0340;
    L_0x024a:
        r11 = r22;
        r7 = r10.equals(r6);
        r9 = "application/x-tgsticker";
        if (r7 != 0) goto L_0x02a7;
    L_0x0254:
        r7 = r34;
        if (r7 != 0) goto L_0x0263;
    L_0x0258:
        r12 = r35;
        if (r12 <= 0) goto L_0x0265;
    L_0x025c:
        r13 = r8.path;
        if (r13 != 0) goto L_0x0265;
    L_0x0260:
        if (r3 == 0) goto L_0x02ab;
    L_0x0262:
        goto L_0x0265;
    L_0x0263:
        r12 = r35;
    L_0x0265:
        r3 = new java.io.File;
        r13 = org.telegram.messenger.FileLoader.getDirectory(r18);
        r3.<init>(r13, r11);
        r13 = r3.exists();
        if (r13 == 0) goto L_0x0276;
    L_0x0274:
        r2 = 1;
        goto L_0x029a;
    L_0x0276:
        r13 = 2;
        if (r7 != r13) goto L_0x0298;
    L_0x0279:
        r3 = new java.io.File;
        r13 = org.telegram.messenger.FileLoader.getDirectory(r18);
        r14 = new java.lang.StringBuilder;
        r14.<init>();
        r14.append(r11);
        r24 = r2;
        r2 = ".enc";
        r14.append(r2);
        r2 = r14.toString();
        r3.<init>(r13, r2);
        r2 = r24;
        goto L_0x029a;
    L_0x0298:
        r24 = r2;
    L_0x029a:
        r13 = r8.document;
        if (r13 == 0) goto L_0x0246;
    L_0x029e:
        r13 = r13.mime_type;
        r9 = r9.equals(r13);
        r4.lottieFile = r9;
        goto L_0x0246;
    L_0x02a7:
        r7 = r34;
        r12 = r35;
    L_0x02ab:
        r24 = r2;
        r2 = r8.document;
        if (r2 == 0) goto L_0x031f;
    L_0x02b1:
        r3 = r2 instanceof org.telegram.tgnet.TLRPC.TL_documentEncrypted;
        if (r3 == 0) goto L_0x02c1;
    L_0x02b5:
        r3 = new java.io.File;
        r13 = org.telegram.messenger.FileLoader.getDirectory(r18);
        r3.<init>(r13, r11);
        r31 = r15;
        goto L_0x02df;
    L_0x02c1:
        r3 = org.telegram.messenger.MessageObject.isVideoDocument(r2);
        if (r3 == 0) goto L_0x02d4;
    L_0x02c7:
        r3 = new java.io.File;
        r31 = r15;
        r13 = 2;
        r15 = org.telegram.messenger.FileLoader.getDirectory(r13);
        r3.<init>(r15, r11);
        goto L_0x02df;
    L_0x02d4:
        r31 = r15;
        r3 = new java.io.File;
        r13 = org.telegram.messenger.FileLoader.getDirectory(r17);
        r3.<init>(r13, r11);
    L_0x02df:
        r13 = r10.equals(r6);
        if (r13 == 0) goto L_0x030f;
    L_0x02e5:
        r13 = r3.exists();
        if (r13 != 0) goto L_0x030f;
    L_0x02eb:
        r3 = new java.io.File;
        r13 = org.telegram.messenger.FileLoader.getDirectory(r18);
        r15 = new java.lang.StringBuilder;
        r15.<init>();
        r1 = r2.dc_id;
        r15.append(r1);
        r15.append(r14);
        r0 = r2.id;
        r15.append(r0);
        r0 = ".temp";
        r15.append(r0);
        r0 = r15.toString();
        r3.<init>(r13, r0);
    L_0x030f:
        r0 = r2.mime_type;
        r0 = r9.equals(r0);
        r4.lottieFile = r0;
        r14 = r2.size;
        r2 = r24;
        r15 = r31;
        r9 = r3;
        goto L_0x0340;
    L_0x031f:
        r31 = r15;
        r0 = r8.webFile;
        if (r0 == 0) goto L_0x032f;
    L_0x0325:
        r0 = new java.io.File;
        r1 = org.telegram.messenger.FileLoader.getDirectory(r17);
        r0.<init>(r1, r11);
        goto L_0x0339;
    L_0x032f:
        r0 = new java.io.File;
        r1 = 0;
        r2 = org.telegram.messenger.FileLoader.getDirectory(r1);
        r0.<init>(r2, r11);
    L_0x0339:
        r2 = r24;
        r15 = r31;
        r9 = r0;
        goto L_0x0247;
    L_0x0340:
        r0 = r10.equals(r6);
        if (r0 == 0) goto L_0x0359;
    L_0x0346:
        r0 = 1;
        r4.animatedFile = r0;
        r4.size = r14;
        r1 = r27;
        r15 = 1;
        goto L_0x035c;
    L_0x034f:
        r11 = r22;
        r7 = r34;
        r12 = r35;
        r24 = r2;
        r31 = r15;
    L_0x0359:
        r0 = 1;
        r1 = r27;
    L_0x035c:
        r4.imageType = r1;
        r3 = r23;
        r4.key = r3;
        r4.filter = r6;
        r4.imageLocation = r8;
        r10 = r36;
        r4.ext = r10;
        r13 = r37;
        r4.currentAccount = r13;
        r14 = r30;
        r4.parentObject = r14;
        r0 = 2;
        if (r7 != r0) goto L_0x0391;
    L_0x0375:
        r0 = new java.io.File;
        r10 = org.telegram.messenger.FileLoader.getInternalCacheDir();
        r13 = new java.lang.StringBuilder;
        r13.<init>();
        r13.append(r11);
        r14 = ".enc.key";
        r13.append(r14);
        r13 = r13.toString();
        r0.<init>(r10, r13);
        r4.encryptionKeyPath = r0;
    L_0x0391:
        r4.addImageReceiver(r5, r3, r6, r1);
        if (r15 != 0) goto L_0x0472;
    L_0x0396:
        if (r2 != 0) goto L_0x0472;
    L_0x0398:
        r0 = r9.exists();
        if (r0 == 0) goto L_0x03a0;
    L_0x039e:
        goto L_0x0472;
    L_0x03a0:
        r4.url = r11;
        r0 = r20;
        r1 = r0.imageLoadingByUrl;
        r1.put(r11, r4);
        r1 = r8.path;
        if (r1 == 0) goto L_0x0401;
    L_0x03ad:
        r1 = org.telegram.messenger.Utilities.MD5(r1);
        r2 = org.telegram.messenger.FileLoader.getDirectory(r18);
        r3 = new java.io.File;
        r5 = new java.lang.StringBuilder;
        r5.<init>();
        r5.append(r1);
        r1 = "_temp.jpg";
        r5.append(r1);
        r1 = r5.toString();
        r3.<init>(r2, r1);
        r4.tempFilePath = r3;
        r4.finalFilePath = r9;
        r1 = r8.path;
        r2 = r19;
        r1 = r1.startsWith(r2);
        if (r1 == 0) goto L_0x03ed;
    L_0x03d9:
        r1 = new org.telegram.messenger.ImageLoader$ArtworkLoadTask;
        r1.<init>(r4);
        r4.artworkTask = r1;
        r1 = r0.artworkTasks;
        r2 = r4.artworkTask;
        r1.add(r2);
        r1 = 0;
        r0.runArtworkTasks(r1);
        goto L_0x0495;
    L_0x03ed:
        r1 = 0;
        r2 = new org.telegram.messenger.ImageLoader$HttpImageTask;
        r2.<init>(r4, r12);
        r4.httpTask = r2;
        r2 = r0.httpTasks;
        r3 = r4.httpTask;
        r2.add(r3);
        r0.runHttpTasks(r1);
        goto L_0x0495;
    L_0x0401:
        r1 = r8.location;
        if (r1 == 0) goto L_0x0423;
    L_0x0405:
        if (r7 != 0) goto L_0x040f;
    L_0x0407:
        if (r12 <= 0) goto L_0x040d;
    L_0x0409:
        r1 = r8.key;
        if (r1 == 0) goto L_0x040f;
    L_0x040d:
        r11 = 1;
        goto L_0x0410;
    L_0x040f:
        r11 = r7;
    L_0x0410:
        r6 = org.telegram.messenger.FileLoader.getInstance(r37);
        if (r21 == 0) goto L_0x0418;
    L_0x0416:
        r10 = 2;
        goto L_0x0419;
    L_0x0418:
        r10 = 1;
    L_0x0419:
        r7 = r28;
        r8 = r30;
        r9 = r36;
        r6.loadFile(r7, r8, r9, r10, r11);
        goto L_0x045f;
    L_0x0423:
        r1 = r8.document;
        if (r1 == 0) goto L_0x043a;
    L_0x0427:
        r1 = org.telegram.messenger.FileLoader.getInstance(r37);
        r2 = r8.document;
        if (r21 == 0) goto L_0x0433;
    L_0x042f:
        r3 = r30;
        r6 = 2;
        goto L_0x0436;
    L_0x0433:
        r3 = r30;
        r6 = 1;
    L_0x0436:
        r1.loadFile(r2, r3, r6, r7);
        goto L_0x045f;
    L_0x043a:
        r1 = r8.secureDocument;
        if (r1 == 0) goto L_0x044d;
    L_0x043e:
        r1 = org.telegram.messenger.FileLoader.getInstance(r37);
        r2 = r8.secureDocument;
        if (r21 == 0) goto L_0x0448;
    L_0x0446:
        r3 = 2;
        goto L_0x0449;
    L_0x0448:
        r3 = 1;
    L_0x0449:
        r1.loadFile(r2, r3);
        goto L_0x045f;
    L_0x044d:
        r1 = r8.webFile;
        if (r1 == 0) goto L_0x045f;
    L_0x0451:
        r1 = org.telegram.messenger.FileLoader.getInstance(r37);
        r2 = r8.webFile;
        if (r21 == 0) goto L_0x045b;
    L_0x0459:
        r3 = 2;
        goto L_0x045c;
    L_0x045b:
        r3 = 1;
    L_0x045c:
        r1.loadFile(r2, r3, r7);
    L_0x045f:
        r1 = r25.isForceLoding();
        if (r1 == 0) goto L_0x0495;
    L_0x0465:
        r1 = r0.forceLoadingImages;
        r2 = r4.key;
        r3 = 0;
        r3 = java.lang.Integer.valueOf(r3);
        r1.put(r2, r3);
        goto L_0x0495;
    L_0x0472:
        r0 = r20;
        r4.finalFilePath = r9;
        r4.imageLocation = r8;
        r1 = new org.telegram.messenger.ImageLoader$CacheOutTask;
        r1.<init>(r4);
        r4.cacheTask = r1;
        r1 = r0.imageLoadingByKeys;
        r1.put(r3, r4);
        if (r21 == 0) goto L_0x048e;
    L_0x0486:
        r1 = r0.cacheThumbOutQueue;
        r2 = r4.cacheTask;
        r1.postRunnable(r2);
        goto L_0x0495;
    L_0x048e:
        r1 = r0.cacheOutQueue;
        r2 = r4.cacheTask;
        r1.postRunnable(r2);
    L_0x0495:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ImageLoader.lambda$createLoadOperationForImageReceiver$5$ImageLoader(int, java.lang.String, java.lang.String, int, org.telegram.messenger.ImageReceiver, java.lang.String, int, org.telegram.messenger.ImageLocation, boolean, java.lang.Object, org.telegram.tgnet.TLRPC$Document, boolean, boolean, int, int, java.lang.String, int):void");
    }

    /* JADX WARNING: Removed duplicated region for block: B:133:0x027e  */
    /* JADX WARNING: Removed duplicated region for block: B:132:0x0279  */
    /* JADX WARNING: Removed duplicated region for block: B:214:0x0292 A:{SYNTHETIC} */
    /* JADX WARNING: Removed duplicated region for block: B:135:0x0283  */
    /* JADX WARNING: Removed duplicated region for block: B:132:0x0279  */
    /* JADX WARNING: Removed duplicated region for block: B:133:0x027e  */
    /* JADX WARNING: Removed duplicated region for block: B:214:0x0292 A:{SYNTHETIC} */
    /* JADX WARNING: Removed duplicated region for block: B:135:0x0283  */
    /* JADX WARNING: Removed duplicated region for block: B:118:0x023e  */
    /* JADX WARNING: Removed duplicated region for block: B:112:0x021e  */
    /* JADX WARNING: Removed duplicated region for block: B:133:0x027e  */
    /* JADX WARNING: Removed duplicated region for block: B:132:0x0279  */
    /* JADX WARNING: Removed duplicated region for block: B:214:0x0292 A:{SYNTHETIC} */
    /* JADX WARNING: Removed duplicated region for block: B:135:0x0283  */
    /* JADX WARNING: Removed duplicated region for block: B:132:0x0279  */
    /* JADX WARNING: Removed duplicated region for block: B:133:0x027e  */
    /* JADX WARNING: Removed duplicated region for block: B:214:0x0292 A:{SYNTHETIC} */
    /* JADX WARNING: Removed duplicated region for block: B:135:0x0283  */
    /* JADX WARNING: Removed duplicated region for block: B:133:0x027e  */
    /* JADX WARNING: Removed duplicated region for block: B:132:0x0279  */
    /* JADX WARNING: Removed duplicated region for block: B:214:0x0292 A:{SYNTHETIC} */
    /* JADX WARNING: Removed duplicated region for block: B:135:0x0283  */
    /* JADX WARNING: Removed duplicated region for block: B:57:0x00eb  */
    /* JADX WARNING: Removed duplicated region for block: B:211:0x02a0 A:{SYNTHETIC, EDGE_INSN: B:211:0x02a0->B:139:0x02a0 ?: BREAK  , EDGE_INSN: B:211:0x02a0->B:139:0x02a0 ?: BREAK  } */
    /* JADX WARNING: Removed duplicated region for block: B:61:0x0100  */
    /* JADX WARNING: Removed duplicated region for block: B:156:0x0309  */
    /* JADX WARNING: Removed duplicated region for block: B:141:0x02a9  */
    /* JADX WARNING: Removed duplicated region for block: B:197:0x03ed  */
    /* JADX WARNING: Removed duplicated region for block: B:180:0x0396  */
    /* JADX WARNING: Removed duplicated region for block: B:52:0x00db  */
    /* JADX WARNING: Removed duplicated region for block: B:50:0x00ce  */
    /* JADX WARNING: Removed duplicated region for block: B:57:0x00eb  */
    /* JADX WARNING: Removed duplicated region for block: B:61:0x0100  */
    /* JADX WARNING: Removed duplicated region for block: B:211:0x02a0 A:{SYNTHETIC, EDGE_INSN: B:211:0x02a0->B:139:0x02a0 ?: BREAK  , EDGE_INSN: B:211:0x02a0->B:139:0x02a0 ?: BREAK  , EDGE_INSN: B:211:0x02a0->B:139:0x02a0 ?: BREAK  } */
    /* JADX WARNING: Removed duplicated region for block: B:141:0x02a9  */
    /* JADX WARNING: Removed duplicated region for block: B:156:0x0309  */
    /* JADX WARNING: Removed duplicated region for block: B:159:0x0310 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:164:0x032e A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:172:0x0361  */
    /* JADX WARNING: Removed duplicated region for block: B:180:0x0396  */
    /* JADX WARNING: Removed duplicated region for block: B:197:0x03ed  */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x0080  */
    /* JADX WARNING: Removed duplicated region for block: B:50:0x00ce  */
    /* JADX WARNING: Removed duplicated region for block: B:52:0x00db  */
    /* JADX WARNING: Removed duplicated region for block: B:57:0x00eb  */
    /* JADX WARNING: Removed duplicated region for block: B:211:0x02a0 A:{SYNTHETIC, EDGE_INSN: B:211:0x02a0->B:139:0x02a0 ?: BREAK  , EDGE_INSN: B:211:0x02a0->B:139:0x02a0 ?: BREAK  , EDGE_INSN: B:211:0x02a0->B:139:0x02a0 ?: BREAK  , EDGE_INSN: B:211:0x02a0->B:139:0x02a0 ?: BREAK  } */
    /* JADX WARNING: Removed duplicated region for block: B:61:0x0100  */
    /* JADX WARNING: Removed duplicated region for block: B:156:0x0309  */
    /* JADX WARNING: Removed duplicated region for block: B:141:0x02a9  */
    /* JADX WARNING: Removed duplicated region for block: B:159:0x0310 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:164:0x032e A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:172:0x0361  */
    /* JADX WARNING: Removed duplicated region for block: B:197:0x03ed  */
    /* JADX WARNING: Removed duplicated region for block: B:180:0x0396  */
    /* JADX WARNING: Removed duplicated region for block: B:25:0x0068  */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x0080  */
    /* JADX WARNING: Removed duplicated region for block: B:52:0x00db  */
    /* JADX WARNING: Removed duplicated region for block: B:50:0x00ce  */
    /* JADX WARNING: Removed duplicated region for block: B:57:0x00eb  */
    /* JADX WARNING: Removed duplicated region for block: B:61:0x0100  */
    /* JADX WARNING: Removed duplicated region for block: B:211:0x02a0 A:{SYNTHETIC, EDGE_INSN: B:211:0x02a0->B:139:0x02a0 ?: BREAK  , EDGE_INSN: B:211:0x02a0->B:139:0x02a0 ?: BREAK  , EDGE_INSN: B:211:0x02a0->B:139:0x02a0 ?: BREAK  , EDGE_INSN: B:211:0x02a0->B:139:0x02a0 ?: BREAK  , EDGE_INSN: B:211:0x02a0->B:139:0x02a0 ?: BREAK  } */
    /* JADX WARNING: Removed duplicated region for block: B:141:0x02a9  */
    /* JADX WARNING: Removed duplicated region for block: B:156:0x0309  */
    /* JADX WARNING: Removed duplicated region for block: B:159:0x0310 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:164:0x032e A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:172:0x0361  */
    /* JADX WARNING: Removed duplicated region for block: B:180:0x0396  */
    /* JADX WARNING: Removed duplicated region for block: B:197:0x03ed  */
    public void loadImageForImageReceiver(org.telegram.messenger.ImageReceiver r30) {
        /*
        r29 = this;
        r11 = r29;
        r12 = r30;
        if (r12 != 0) goto L_0x0007;
    L_0x0006:
        return;
    L_0x0007:
        r0 = r30.getMediaKey();
        r1 = 0;
        r2 = 1;
        if (r0 == 0) goto L_0x003e;
    L_0x000f:
        r3 = r30.getMediaLocation();
        r3 = r3.document;
        r3 = org.telegram.messenger.MessageObject.isAnimatedStickerDocument(r3);
        if (r3 == 0) goto L_0x0024;
    L_0x001b:
        r3 = r11.lottieMemCache;
        r3 = r3.get(r0);
        r3 = (android.graphics.drawable.Drawable) r3;
        goto L_0x002c;
    L_0x0024:
        r3 = r11.memCache;
        r3 = r3.get(r0);
        r3 = (android.graphics.drawable.Drawable) r3;
    L_0x002c:
        if (r3 == 0) goto L_0x003e;
    L_0x002e:
        r11.cancelLoadingForImageReceiver(r12, r2);
        r4 = 3;
        r12.setImageBitmapByKey(r3, r0, r4, r2);
        r3 = r30.isForcePreview();
        if (r3 != 0) goto L_0x003c;
    L_0x003b:
        return;
    L_0x003c:
        r3 = 1;
        goto L_0x003f;
    L_0x003e:
        r3 = 0;
    L_0x003f:
        r4 = r30.getImageKey();
        if (r3 != 0) goto L_0x0079;
    L_0x0045:
        if (r4 == 0) goto L_0x0079;
    L_0x0047:
        r5 = r30.getImageLocation();
        if (r5 == 0) goto L_0x005e;
    L_0x004d:
        r5 = r5.document;
        r5 = org.telegram.messenger.MessageObject.isAnimatedStickerDocument(r5);
        if (r5 == 0) goto L_0x005e;
    L_0x0055:
        r5 = r11.lottieMemCache;
        r5 = r5.get(r4);
        r5 = (android.graphics.drawable.Drawable) r5;
        goto L_0x0066;
    L_0x005e:
        r5 = r11.memCache;
        r5 = r5.get(r4);
        r5 = (android.graphics.drawable.Drawable) r5;
    L_0x0066:
        if (r5 == 0) goto L_0x0079;
    L_0x0068:
        r11.cancelLoadingForImageReceiver(r12, r2);
        r12.setImageBitmapByKey(r5, r4, r1, r2);
        r3 = r30.isForcePreview();
        if (r3 != 0) goto L_0x0077;
    L_0x0074:
        if (r0 != 0) goto L_0x0077;
    L_0x0076:
        return;
    L_0x0077:
        r13 = 1;
        goto L_0x007a;
    L_0x0079:
        r13 = r3;
    L_0x007a:
        r0 = r30.getThumbKey();
        if (r0 == 0) goto L_0x009b;
    L_0x0080:
        r3 = r11.memCache;
        r3 = r3.get(r0);
        r3 = (android.graphics.drawable.BitmapDrawable) r3;
        if (r3 == 0) goto L_0x009b;
    L_0x008a:
        r12.setImageBitmapByKey(r3, r0, r2, r2);
        r11.cancelLoadingForImageReceiver(r12, r1);
        if (r13 == 0) goto L_0x0099;
    L_0x0092:
        r0 = r30.isForcePreview();
        if (r0 == 0) goto L_0x0099;
    L_0x0098:
        return;
    L_0x0099:
        r0 = 1;
        goto L_0x009c;
    L_0x009b:
        r0 = 0;
    L_0x009c:
        r3 = r30.getParentObject();
        r4 = r30.getQulityThumbDocument();
        r5 = r30.getThumbLocation();
        r6 = r30.getThumbFilter();
        r7 = r30.getMediaLocation();
        r14 = r30.getMediaFilter();
        r8 = r30.getImageLocation();
        r15 = r30.getImageFilter();
        if (r8 != 0) goto L_0x00e2;
    L_0x00be:
        r9 = r30.isNeedsQualityThumb();
        if (r9 == 0) goto L_0x00e2;
    L_0x00c4:
        r9 = r30.isCurrentKeyQuality();
        if (r9 == 0) goto L_0x00e2;
    L_0x00ca:
        r9 = r3 instanceof org.telegram.messenger.MessageObject;
        if (r9 == 0) goto L_0x00db;
    L_0x00ce:
        r4 = r3;
        r4 = (org.telegram.messenger.MessageObject) r4;
        r4 = r4.getDocument();
        r8 = org.telegram.messenger.ImageLocation.getForDocument(r4);
    L_0x00d9:
        r4 = 1;
        goto L_0x00e3;
    L_0x00db:
        if (r4 == 0) goto L_0x00e2;
    L_0x00dd:
        r8 = org.telegram.messenger.ImageLocation.getForDocument(r4);
        goto L_0x00d9;
    L_0x00e2:
        r4 = 0;
    L_0x00e3:
        r9 = r30.getExt();
        r10 = "jpg";
        if (r9 != 0) goto L_0x00ec;
    L_0x00eb:
        r9 = r10;
    L_0x00ec:
        r16 = 0;
        r17 = r7;
        r1 = r16;
        r19 = r1;
        r21 = r19;
        r22 = r21;
        r7 = 0;
        r20 = 0;
    L_0x00fb:
        r2 = 2;
        r11 = ".";
        if (r7 >= r2) goto L_0x02a0;
    L_0x0100:
        if (r7 != 0) goto L_0x0104;
    L_0x0102:
        r2 = r8;
        goto L_0x0106;
    L_0x0104:
        r2 = r17;
    L_0x0106:
        if (r2 != 0) goto L_0x0109;
    L_0x0108:
        goto L_0x0115;
    L_0x0109:
        if (r17 == 0) goto L_0x010e;
    L_0x010b:
        r12 = r17;
        goto L_0x010f;
    L_0x010e:
        r12 = r8;
    L_0x010f:
        r12 = r2.getKey(r3, r12);
        if (r12 != 0) goto L_0x011e;
    L_0x0115:
        r23 = r13;
        r25 = r14;
        r26 = r15;
        r15 = 1;
        goto L_0x0292;
    L_0x011e:
        r23 = r13;
        r13 = r2.path;
        if (r13 == 0) goto L_0x0143;
    L_0x0124:
        r13 = new java.lang.StringBuilder;
        r13.<init>();
        r13.append(r12);
        r13.append(r11);
        r11 = r2.path;
        r11 = getHttpUrlExtension(r11, r10);
        r13.append(r11);
        r11 = r13.toString();
    L_0x013c:
        r25 = r14;
        r26 = r15;
    L_0x0140:
        r15 = 1;
        goto L_0x0277;
    L_0x0143:
        r13 = r2.photoSize;
        r13 = r13 instanceof org.telegram.tgnet.TLRPC.TL_photoStrippedSize;
        if (r13 == 0) goto L_0x015c;
    L_0x0149:
        r13 = new java.lang.StringBuilder;
        r13.<init>();
        r13.append(r12);
        r13.append(r11);
        r13.append(r9);
        r11 = r13.toString();
        goto L_0x013c;
    L_0x015c:
        r13 = r2.location;
        if (r13 == 0) goto L_0x01a2;
    L_0x0160:
        r13 = new java.lang.StringBuilder;
        r13.<init>();
        r13.append(r12);
        r13.append(r11);
        r13.append(r9);
        r11 = r13.toString();
        r13 = r30.getExt();
        if (r13 != 0) goto L_0x0195;
    L_0x0178:
        r13 = r2.location;
        r24 = r11;
        r11 = r13.key;
        if (r11 != 0) goto L_0x0197;
    L_0x0180:
        r25 = r14;
        r26 = r15;
        r14 = r13.volume_id;
        r27 = -NUM; // 0xfffffffvar_ float:-0.0 double:NaN;
        r11 = (r14 > r27 ? 1 : (r14 == r27 ? 0 : -1));
        if (r11 != 0) goto L_0x0192;
    L_0x018d:
        r11 = r13.local_id;
        if (r11 >= 0) goto L_0x0192;
    L_0x0191:
        goto L_0x019b;
    L_0x0192:
        r11 = r24;
        goto L_0x0140;
    L_0x0195:
        r24 = r11;
    L_0x0197:
        r25 = r14;
        r26 = r15;
    L_0x019b:
        r11 = r24;
        r15 = 1;
        r20 = 1;
        goto L_0x0277;
    L_0x01a2:
        r25 = r14;
        r26 = r15;
        r13 = r2.webFile;
        if (r13 == 0) goto L_0x01cc;
    L_0x01aa:
        r13 = r13.mime_type;
        r13 = org.telegram.messenger.FileLoader.getMimeTypePart(r13);
        r14 = new java.lang.StringBuilder;
        r14.<init>();
        r14.append(r12);
        r14.append(r11);
        r11 = r2.webFile;
        r11 = r11.url;
        r11 = getHttpUrlExtension(r11, r13);
        r14.append(r11);
        r11 = r14.toString();
        goto L_0x0140;
    L_0x01cc:
        r13 = r2.secureDocument;
        if (r13 == 0) goto L_0x01e4;
    L_0x01d0:
        r13 = new java.lang.StringBuilder;
        r13.<init>();
        r13.append(r12);
        r13.append(r11);
        r13.append(r9);
        r11 = r13.toString();
        goto L_0x0140;
    L_0x01e4:
        r11 = r2.document;
        if (r11 == 0) goto L_0x0274;
    L_0x01e8:
        if (r7 != 0) goto L_0x01fd;
    L_0x01ea:
        if (r4 == 0) goto L_0x01fd;
    L_0x01ec:
        r11 = new java.lang.StringBuilder;
        r11.<init>();
        r13 = "q_";
        r11.append(r13);
        r11.append(r12);
        r12 = r11.toString();
    L_0x01fd:
        r11 = r2.document;
        r11 = org.telegram.messenger.FileLoader.getDocumentFileName(r11);
        r13 = "";
        if (r11 == 0) goto L_0x0216;
    L_0x0207:
        r14 = 46;
        r14 = r11.lastIndexOf(r14);
        r15 = -1;
        if (r14 != r15) goto L_0x0211;
    L_0x0210:
        goto L_0x0216;
    L_0x0211:
        r11 = r11.substring(r14);
        goto L_0x0217;
    L_0x0216:
        r11 = r13;
    L_0x0217:
        r14 = r11.length();
        r15 = 1;
        if (r14 > r15) goto L_0x023e;
    L_0x021e:
        r11 = r2.document;
        r11 = r11.mime_type;
        r14 = "video/mp4";
        r11 = r14.equals(r11);
        if (r11 == 0) goto L_0x022e;
    L_0x022b:
        r13 = ".mp4";
        goto L_0x023f;
    L_0x022e:
        r11 = r2.document;
        r11 = r11.mime_type;
        r14 = "video/x-matroska";
        r11 = r14.equals(r11);
        if (r11 == 0) goto L_0x023f;
    L_0x023b:
        r13 = ".mkv";
        goto L_0x023f;
    L_0x023e:
        r13 = r11;
    L_0x023f:
        r11 = new java.lang.StringBuilder;
        r11.<init>();
        r11.append(r12);
        r11.append(r13);
        r11 = r11.toString();
        r13 = r2.document;
        r13 = org.telegram.messenger.MessageObject.isVideoDocument(r13);
        if (r13 != 0) goto L_0x0270;
    L_0x0256:
        r13 = r2.document;
        r13 = org.telegram.messenger.MessageObject.isGifDocument(r13);
        if (r13 != 0) goto L_0x0270;
    L_0x025e:
        r13 = r2.document;
        r13 = org.telegram.messenger.MessageObject.isRoundVideoDocument(r13);
        if (r13 != 0) goto L_0x0270;
    L_0x0266:
        r13 = r2.document;
        r13 = org.telegram.messenger.MessageObject.canPreviewDocument(r13);
        if (r13 != 0) goto L_0x0270;
    L_0x026e:
        r13 = 1;
        goto L_0x0271;
    L_0x0270:
        r13 = 0;
    L_0x0271:
        r20 = r13;
        goto L_0x0277;
    L_0x0274:
        r15 = 1;
        r11 = r16;
    L_0x0277:
        if (r7 != 0) goto L_0x027e;
    L_0x0279:
        r21 = r11;
        r19 = r12;
        goto L_0x0281;
    L_0x027e:
        r22 = r11;
        r1 = r12;
    L_0x0281:
        if (r2 != r5) goto L_0x0292;
    L_0x0283:
        if (r7 != 0) goto L_0x028c;
    L_0x0285:
        r8 = r16;
        r19 = r8;
        r21 = r19;
        goto L_0x0292;
    L_0x028c:
        r1 = r16;
        r17 = r1;
        r22 = r17;
    L_0x0292:
        r7 = r7 + 1;
        r11 = r29;
        r12 = r30;
        r13 = r23;
        r14 = r25;
        r15 = r26;
        goto L_0x00fb;
    L_0x02a0:
        r23 = r13;
        r25 = r14;
        r26 = r15;
        r15 = 1;
        if (r5 == 0) goto L_0x0309;
    L_0x02a9:
        r4 = r30.getStrippedLocation();
        if (r4 != 0) goto L_0x02b5;
    L_0x02af:
        if (r17 == 0) goto L_0x02b4;
    L_0x02b1:
        r4 = r17;
        goto L_0x02b5;
    L_0x02b4:
        r4 = r8;
    L_0x02b5:
        r3 = r5.getKey(r3, r4);
        r4 = r5.path;
        if (r4 == 0) goto L_0x02d6;
    L_0x02bd:
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r4.append(r3);
        r4.append(r11);
        r7 = r5.path;
        r7 = getHttpUrlExtension(r7, r10);
        r4.append(r7);
        r4 = r4.toString();
        goto L_0x030c;
    L_0x02d6:
        r4 = r5.photoSize;
        r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_photoStrippedSize;
        if (r4 == 0) goto L_0x02ef;
    L_0x02dc:
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r4.append(r3);
        r4.append(r11);
        r4.append(r9);
        r4 = r4.toString();
        goto L_0x030c;
    L_0x02ef:
        r4 = r5.location;
        if (r4 == 0) goto L_0x0306;
    L_0x02f3:
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r4.append(r3);
        r4.append(r11);
        r4.append(r9);
        r4 = r4.toString();
        goto L_0x030c;
    L_0x0306:
        r4 = r16;
        goto L_0x030c;
    L_0x0309:
        r3 = r16;
        r4 = r3;
    L_0x030c:
        r7 = "@";
        if (r1 == 0) goto L_0x0327;
    L_0x0310:
        if (r25 == 0) goto L_0x0327;
    L_0x0312:
        r10 = new java.lang.StringBuilder;
        r10.<init>();
        r10.append(r1);
        r10.append(r7);
        r11 = r25;
        r10.append(r11);
        r1 = r10.toString();
        goto L_0x0329;
    L_0x0327:
        r11 = r25;
    L_0x0329:
        r12 = r1;
        r1 = r19;
        if (r1 == 0) goto L_0x0345;
    L_0x032e:
        if (r26 == 0) goto L_0x0345;
    L_0x0330:
        r10 = new java.lang.StringBuilder;
        r10.<init>();
        r10.append(r1);
        r10.append(r7);
        r13 = r26;
        r10.append(r13);
        r1 = r10.toString();
        goto L_0x0347;
    L_0x0345:
        r13 = r26;
    L_0x0347:
        r14 = r1;
        if (r3 == 0) goto L_0x035f;
    L_0x034a:
        if (r6 == 0) goto L_0x035f;
    L_0x034c:
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r1.append(r3);
        r1.append(r7);
        r1.append(r6);
        r1 = r1.toString();
        r3 = r1;
    L_0x035f:
        if (r8 == 0) goto L_0x0390;
    L_0x0361:
        r1 = r8.path;
        if (r1 == 0) goto L_0x0390;
    L_0x0365:
        r7 = 0;
        r10 = 1;
        r11 = 1;
        if (r0 == 0) goto L_0x036b;
    L_0x036a:
        r15 = 2;
    L_0x036b:
        r0 = r29;
        r1 = r30;
        r2 = r3;
        r3 = r4;
        r4 = r9;
        r16 = r8;
        r8 = r10;
        r18 = r9;
        r9 = r11;
        r10 = r15;
        r0.createLoadOperationForImageReceiver(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10);
        r7 = r30.getSize();
        r8 = 1;
        r9 = 0;
        r10 = 0;
        r2 = r14;
        r3 = r21;
        r4 = r18;
        r5 = r16;
        r6 = r13;
        r0.createLoadOperationForImageReceiver(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10);
        goto L_0x041f;
    L_0x0390:
        r16 = r8;
        r18 = r9;
        if (r17 == 0) goto L_0x03ed;
    L_0x0396:
        r1 = r30.getCacheType();
        r19 = 1;
        if (r1 != 0) goto L_0x03a3;
    L_0x039e:
        if (r20 == 0) goto L_0x03a3;
    L_0x03a0:
        r20 = 1;
        goto L_0x03a5;
    L_0x03a3:
        r20 = r1;
    L_0x03a5:
        if (r20 != 0) goto L_0x03a9;
    L_0x03a7:
        r8 = 1;
        goto L_0x03ab;
    L_0x03a9:
        r8 = r20;
    L_0x03ab:
        if (r0 != 0) goto L_0x03bf;
    L_0x03ad:
        r7 = 0;
        r9 = 1;
        if (r0 == 0) goto L_0x03b3;
    L_0x03b1:
        r10 = 2;
        goto L_0x03b4;
    L_0x03b3:
        r10 = 1;
    L_0x03b4:
        r0 = r29;
        r1 = r30;
        r2 = r3;
        r3 = r4;
        r4 = r18;
        r0.createLoadOperationForImageReceiver(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10);
    L_0x03bf:
        if (r23 != 0) goto L_0x03d5;
    L_0x03c1:
        r7 = 0;
        r9 = 0;
        r10 = 0;
        r0 = r29;
        r1 = r30;
        r2 = r14;
        r3 = r21;
        r4 = r18;
        r5 = r16;
        r6 = r13;
        r8 = r19;
        r0.createLoadOperationForImageReceiver(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10);
    L_0x03d5:
        r7 = r30.getSize();
        r9 = 3;
        r10 = 0;
        r0 = r29;
        r1 = r30;
        r2 = r12;
        r3 = r22;
        r4 = r18;
        r5 = r17;
        r6 = r11;
        r8 = r20;
        r0.createLoadOperationForImageReceiver(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10);
        goto L_0x041f;
    L_0x03ed:
        r1 = r30.getCacheType();
        if (r1 != 0) goto L_0x03f7;
    L_0x03f3:
        if (r20 == 0) goto L_0x03f7;
    L_0x03f5:
        r11 = 1;
        goto L_0x03f8;
    L_0x03f7:
        r11 = r1;
    L_0x03f8:
        if (r11 != 0) goto L_0x03fc;
    L_0x03fa:
        r8 = 1;
        goto L_0x03fd;
    L_0x03fc:
        r8 = r11;
    L_0x03fd:
        r7 = 0;
        r9 = 1;
        if (r0 == 0) goto L_0x0403;
    L_0x0401:
        r10 = 2;
        goto L_0x0404;
    L_0x0403:
        r10 = 1;
    L_0x0404:
        r0 = r29;
        r1 = r30;
        r2 = r3;
        r3 = r4;
        r4 = r18;
        r0.createLoadOperationForImageReceiver(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10);
        r7 = r30.getSize();
        r9 = 0;
        r10 = 0;
        r2 = r14;
        r3 = r21;
        r5 = r16;
        r6 = r13;
        r8 = r11;
        r0.createLoadOperationForImageReceiver(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10);
    L_0x041f:
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
                cacheImage2.addImageReceiver(imageReceiver, str2, str3, intValue);
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

    /* JADX WARNING: Removed duplicated region for block: B:33:0x00b1  */
    /* JADX WARNING: Removed duplicated region for block: B:37:0x00de  */
    /* JADX WARNING: Removed duplicated region for block: B:36:0x00c5  */
    /* JADX WARNING: Removed duplicated region for block: B:40:0x00ee  */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x00b1  */
    /* JADX WARNING: Removed duplicated region for block: B:36:0x00c5  */
    /* JADX WARNING: Removed duplicated region for block: B:37:0x00de  */
    /* JADX WARNING: Removed duplicated region for block: B:40:0x00ee  */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x00b1  */
    /* JADX WARNING: Removed duplicated region for block: B:37:0x00de  */
    /* JADX WARNING: Removed duplicated region for block: B:36:0x00c5  */
    /* JADX WARNING: Removed duplicated region for block: B:40:0x00ee  */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x00b1  */
    /* JADX WARNING: Removed duplicated region for block: B:36:0x00c5  */
    /* JADX WARNING: Removed duplicated region for block: B:37:0x00de  */
    /* JADX WARNING: Removed duplicated region for block: B:40:0x00ee  */
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
        goto L_0x008b;
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
        goto L_0x008b;
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
        goto L_0x008b;
    L_0x0066:
        r12 = r3.w;
        r0 = 800; // 0x320 float:1.121E-42 double:3.953E-321;
        if (r12 > r0) goto L_0x0076;
    L_0x006c:
        r12 = r3.h;
        if (r12 > r0) goto L_0x0076;
    L_0x0070:
        r12 = "x";
        r3.type = r12;
        goto L_0x008b;
    L_0x0076:
        r12 = r3.w;
        r0 = 1280; // 0x500 float:1.794E-42 double:6.324E-321;
        if (r12 > r0) goto L_0x0086;
    L_0x007c:
        r12 = r3.h;
        if (r12 > r0) goto L_0x0086;
    L_0x0080:
        r12 = "y";
        r3.type = r12;
        goto L_0x008b;
    L_0x0086:
        r12 = "w";
        r3.type = r12;
    L_0x008b:
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
        if (r9 == 0) goto L_0x00b1;
    L_0x00b0:
        goto L_0x00b2;
    L_0x00b1:
        r6 = 4;
    L_0x00b2:
        r6 = org.telegram.messenger.FileLoader.getDirectory(r6);
        r0.<init>(r6, r12);
        r6 = new java.io.FileOutputStream;
        r6.<init>(r0);
        r7 = android.graphics.Bitmap.CompressFormat.JPEG;
        r5.compress(r7, r10, r6);
        if (r11 == 0) goto L_0x00de;
    L_0x00c5:
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
        goto L_0x00e9;
    L_0x00de:
        r7 = r6.getChannel();
        r7 = r7.size();
        r8 = (int) r7;
        r3.size = r8;
    L_0x00e9:
        r6.close();
        if (r5 == r4) goto L_0x00f1;
    L_0x00ee:
        r5.recycle();
    L_0x00f1:
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
