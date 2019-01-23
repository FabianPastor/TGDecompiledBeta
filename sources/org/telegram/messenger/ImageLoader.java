package org.telegram.messenger;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.drawable.BitmapDrawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.text.TextUtils;
import android.util.SparseArray;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.telegram.messenger.FileLoader.FileLoaderDelegate;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.InputEncryptedFile;
import org.telegram.tgnet.TLRPC.InputFile;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_fileLocation;
import org.telegram.tgnet.TLRPC.TL_fileLocationUnavailable;
import org.telegram.tgnet.TLRPC.TL_messageMediaDocument;
import org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;
import org.telegram.tgnet.TLRPC.TL_messageMediaWebPage;
import org.telegram.tgnet.TLRPC.TL_photoCachedSize;
import org.telegram.tgnet.TLRPC.TL_photoSize;
import org.telegram.tgnet.TLRPC.TL_upload_getWebFile;
import org.telegram.ui.Components.AnimatedFileDrawable;

public class ImageLoader {
    private static volatile ImageLoader Instance = null;
    private static byte[] bytes;
    private static byte[] bytesThumb;
    private static byte[] header = new byte[12];
    private static byte[] headerThumb = new byte[12];
    private LinkedList<ArtworkLoadTask> artworkTasks = new LinkedList();
    private HashMap<String, Integer> bitmapUseCounts = new HashMap();
    private DispatchQueue cacheOutQueue = new DispatchQueue("cacheOutQueue");
    private DispatchQueue cacheThumbOutQueue = new DispatchQueue("cacheThumbOutQueue");
    private int currentArtworkTasksCount = 0;
    private int currentHttpFileLoadTasksCount = 0;
    private int currentHttpTasksCount = 0;
    private ConcurrentHashMap<String, Float> fileProgresses = new ConcurrentHashMap();
    private HashMap<String, Integer> forceLoadingImages = new HashMap();
    private LinkedList<HttpFileTask> httpFileLoadTasks = new LinkedList();
    private HashMap<String, HttpFileTask> httpFileLoadTasksByKeys = new HashMap();
    private LinkedList<HttpImageTask> httpTasks = new LinkedList();
    private String ignoreRemoval = null;
    private DispatchQueue imageLoadQueue = new DispatchQueue("imageLoadQueue");
    private HashMap<String, CacheImage> imageLoadingByKeys = new HashMap();
    private SparseArray<CacheImage> imageLoadingByTag = new SparseArray();
    private HashMap<String, CacheImage> imageLoadingByUrl = new HashMap();
    private volatile long lastCacheOutTime = 0;
    private int lastImageNum = 0;
    private long lastProgressUpdateTime = 0;
    private LruCache memCache;
    private HashMap<String, String> replacedBitmaps = new HashMap();
    private HashMap<String, Runnable> retryHttpsTasks = new HashMap();
    private File telegramPath = null;
    private ConcurrentHashMap<String, WebFile> testWebFile = new ConcurrentHashMap();
    private HashMap<String, ThumbGenerateTask> thumbGenerateTasks = new HashMap();
    private DispatchQueue thumbGeneratingQueue = new DispatchQueue("thumbGeneratingQueue");
    private HashMap<String, ThumbGenerateInfo> waitingForQualityThumb = new HashMap();
    private SparseArray<String> waitingForQualityThumbByTag = new SparseArray();

    private class ArtworkLoadTask extends AsyncTask<Void, Void, String> {
        private CacheImage cacheImage;
        private boolean canRetry = true;
        private HttpURLConnection httpConnection;
        private boolean small;
        final /* synthetic */ ImageLoader this$0;

        public ArtworkLoadTask(ImageLoader imageLoader, CacheImage cacheImage) {
            boolean z = true;
            this.this$0 = imageLoader;
            this.cacheImage = cacheImage;
            if (Uri.parse(cacheImage.httpUrl).getQueryParameter("s") == null) {
                z = false;
            }
            this.small = z;
        }

        /* JADX WARNING: Removed duplicated region for block: B:106:0x0142 A:{Catch:{ Throwable -> 0x018c }} */
        /* JADX WARNING: Removed duplicated region for block: B:108:0x0149 A:{SYNTHETIC, Splitter: B:108:0x0149} */
        /* JADX WARNING: Removed duplicated region for block: B:111:0x014e A:{SYNTHETIC, Splitter: B:111:0x014e} */
        /* JADX WARNING: Removed duplicated region for block: B:54:0x00d6 A:{SYNTHETIC, Splitter: B:54:0x00d6} */
        /* JADX WARNING: Removed duplicated region for block: B:51:0x00d1 A:{SYNTHETIC, Splitter: B:51:0x00d1} */
        /* JADX WARNING: Removed duplicated region for block: B:54:0x00d6 A:{SYNTHETIC, Splitter: B:54:0x00d6} */
        protected java.lang.String doInBackground(java.lang.Void... r16) {
            /*
            r15 = this;
            r9 = 0;
            r6 = 0;
            r4 = new java.net.URL;	 Catch:{ Throwable -> 0x00b5 }
            r12 = r15.cacheImage;	 Catch:{ Throwable -> 0x00b5 }
            r12 = r12.httpUrl;	 Catch:{ Throwable -> 0x00b5 }
            r13 = "athumb://";
            r14 = "https://";
            r12 = r12.replace(r13, r14);	 Catch:{ Throwable -> 0x00b5 }
            r4.<init>(r12);	 Catch:{ Throwable -> 0x00b5 }
            r12 = r4.openConnection();	 Catch:{ Throwable -> 0x00b5 }
            r12 = (java.net.HttpURLConnection) r12;	 Catch:{ Throwable -> 0x00b5 }
            r15.httpConnection = r12;	 Catch:{ Throwable -> 0x00b5 }
            r12 = r15.httpConnection;	 Catch:{ Throwable -> 0x00b5 }
            r13 = "User-Agent";
            r14 = "Mozilla/5.0 (iPhone; CPU iPhone OS 10_0 like Mac OS X) AppleWebKit/602.1.38 (KHTML, like Gecko) Version/10.0 Mobile/14A5297c Safari/602.1";
            r12.addRequestProperty(r13, r14);	 Catch:{ Throwable -> 0x00b5 }
            r12 = r15.httpConnection;	 Catch:{ Throwable -> 0x00b5 }
            r13 = 5000; // 0x1388 float:7.006E-42 double:2.4703E-320;
            r12.setConnectTimeout(r13);	 Catch:{ Throwable -> 0x00b5 }
            r12 = r15.httpConnection;	 Catch:{ Throwable -> 0x00b5 }
            r13 = 5000; // 0x1388 float:7.006E-42 double:2.4703E-320;
            r12.setReadTimeout(r13);	 Catch:{ Throwable -> 0x00b5 }
            r12 = r15.httpConnection;	 Catch:{ Throwable -> 0x00b5 }
            r12.connect();	 Catch:{ Throwable -> 0x00b5 }
            r12 = r15.httpConnection;	 Catch:{ Exception -> 0x00b0 }
            if (r12 == 0) goto L_0x0054;
        L_0x003f:
            r12 = r15.httpConnection;	 Catch:{ Exception -> 0x00b0 }
            r2 = r12.getResponseCode();	 Catch:{ Exception -> 0x00b0 }
            r12 = 200; // 0xc8 float:2.8E-43 double:9.9E-322;
            if (r2 == r12) goto L_0x0054;
        L_0x0049:
            r12 = 202; // 0xca float:2.83E-43 double:1.0E-321;
            if (r2 == r12) goto L_0x0054;
        L_0x004d:
            r12 = 304; // 0x130 float:4.26E-43 double:1.5E-321;
            if (r2 == r12) goto L_0x0054;
        L_0x0051:
            r12 = 0;
            r15.canRetry = r12;	 Catch:{ Exception -> 0x00b0 }
        L_0x0054:
            r12 = r15.httpConnection;	 Catch:{ Throwable -> 0x00b5 }
            r6 = r12.getInputStream();	 Catch:{ Throwable -> 0x00b5 }
            r10 = new java.io.ByteArrayOutputStream;	 Catch:{ Throwable -> 0x00b5 }
            r10.<init>();	 Catch:{ Throwable -> 0x00b5 }
            r12 = 32768; // 0x8000 float:4.5918E-41 double:1.61895E-319;
            r3 = new byte[r12];	 Catch:{ Throwable -> 0x00e7, all -> 0x018e }
        L_0x0064:
            r12 = r15.isCancelled();	 Catch:{ Throwable -> 0x00e7, all -> 0x018e }
            if (r12 == 0) goto L_0x00db;
        L_0x006a:
            r12 = 0;
            r15.canRetry = r12;	 Catch:{ Throwable -> 0x00e7, all -> 0x018e }
            r8 = new org.json.JSONObject;	 Catch:{ Throwable -> 0x00e7, all -> 0x018e }
            r12 = new java.lang.String;	 Catch:{ Throwable -> 0x00e7, all -> 0x018e }
            r13 = r10.toByteArray();	 Catch:{ Throwable -> 0x00e7, all -> 0x018e }
            r14 = "UTF-8";
            r12.<init>(r13, r14);	 Catch:{ Throwable -> 0x00e7, all -> 0x018e }
            r8.<init>(r12);	 Catch:{ Throwable -> 0x00e7, all -> 0x018e }
            r12 = "results";
            r0 = r8.getJSONArray(r12);	 Catch:{ Throwable -> 0x00e7, all -> 0x018e }
            r12 = r0.length();	 Catch:{ Throwable -> 0x00e7, all -> 0x018e }
            if (r12 <= 0) goto L_0x0118;
        L_0x008b:
            r12 = 0;
            r7 = r0.getJSONObject(r12);	 Catch:{ Throwable -> 0x00e7, all -> 0x018e }
            r12 = "artworkUrl100";
            r1 = r7.getString(r12);	 Catch:{ Throwable -> 0x00e7, all -> 0x018e }
            r12 = r15.small;	 Catch:{ Throwable -> 0x00e7, all -> 0x018e }
            if (r12 == 0) goto L_0x00f4;
        L_0x009b:
            r12 = r15.httpConnection;	 Catch:{ Throwable -> 0x0199 }
            if (r12 == 0) goto L_0x00a4;
        L_0x009f:
            r12 = r15.httpConnection;	 Catch:{ Throwable -> 0x0199 }
            r12.disconnect();	 Catch:{ Throwable -> 0x0199 }
        L_0x00a4:
            if (r6 == 0) goto L_0x00a9;
        L_0x00a6:
            r6.close();	 Catch:{ Throwable -> 0x00ef }
        L_0x00a9:
            if (r10 == 0) goto L_0x00ae;
        L_0x00ab:
            r10.close();	 Catch:{ Exception -> 0x0182 }
        L_0x00ae:
            r9 = r10;
        L_0x00af:
            return r1;
        L_0x00b0:
            r5 = move-exception;
            org.telegram.messenger.FileLog.e(r5);	 Catch:{ Throwable -> 0x00b5 }
            goto L_0x0054;
        L_0x00b5:
            r5 = move-exception;
        L_0x00b6:
            r12 = r5 instanceof java.net.SocketTimeoutException;	 Catch:{ all -> 0x013d }
            if (r12 == 0) goto L_0x0135;
        L_0x00ba:
            r12 = org.telegram.messenger.ApplicationLoader.isNetworkOnline();	 Catch:{ all -> 0x013d }
            if (r12 == 0) goto L_0x00c3;
        L_0x00c0:
            r12 = 0;
            r15.canRetry = r12;	 Catch:{ all -> 0x013d }
        L_0x00c3:
            org.telegram.messenger.FileLog.e(r5);	 Catch:{ all -> 0x013d }
            r12 = r15.httpConnection;	 Catch:{ Throwable -> 0x0191 }
            if (r12 == 0) goto L_0x00cf;
        L_0x00ca:
            r12 = r15.httpConnection;	 Catch:{ Throwable -> 0x0191 }
            r12.disconnect();	 Catch:{ Throwable -> 0x0191 }
        L_0x00cf:
            if (r6 == 0) goto L_0x00d4;
        L_0x00d1:
            r6.close();	 Catch:{ Throwable -> 0x0177 }
        L_0x00d4:
            if (r9 == 0) goto L_0x00d9;
        L_0x00d6:
            r9.close();	 Catch:{ Exception -> 0x0187 }
        L_0x00d9:
            r1 = 0;
            goto L_0x00af;
        L_0x00db:
            r11 = r6.read(r3);	 Catch:{ Throwable -> 0x00e7, all -> 0x018e }
            if (r11 <= 0) goto L_0x00ea;
        L_0x00e1:
            r12 = 0;
            r10.write(r3, r12, r11);	 Catch:{ Throwable -> 0x00e7, all -> 0x018e }
            goto L_0x0064;
        L_0x00e7:
            r5 = move-exception;
            r9 = r10;
            goto L_0x00b6;
        L_0x00ea:
            r12 = -1;
            if (r11 != r12) goto L_0x006a;
        L_0x00ed:
            goto L_0x006a;
        L_0x00ef:
            r5 = move-exception;
            org.telegram.messenger.FileLog.e(r5);
            goto L_0x00a9;
        L_0x00f4:
            r12 = "100x100";
            r13 = "600x600";
            r1 = r1.replace(r12, r13);	 Catch:{ Throwable -> 0x00e7, all -> 0x018e }
            r12 = r15.httpConnection;	 Catch:{ Throwable -> 0x0196 }
            if (r12 == 0) goto L_0x0107;
        L_0x0102:
            r12 = r15.httpConnection;	 Catch:{ Throwable -> 0x0196 }
            r12.disconnect();	 Catch:{ Throwable -> 0x0196 }
        L_0x0107:
            if (r6 == 0) goto L_0x010c;
        L_0x0109:
            r6.close();	 Catch:{ Throwable -> 0x0113 }
        L_0x010c:
            if (r10 == 0) goto L_0x0111;
        L_0x010e:
            r10.close();	 Catch:{ Exception -> 0x0185 }
        L_0x0111:
            r9 = r10;
            goto L_0x00af;
        L_0x0113:
            r5 = move-exception;
            org.telegram.messenger.FileLog.e(r5);
            goto L_0x010c;
        L_0x0118:
            r12 = r15.httpConnection;	 Catch:{ Throwable -> 0x0194 }
            if (r12 == 0) goto L_0x0121;
        L_0x011c:
            r12 = r15.httpConnection;	 Catch:{ Throwable -> 0x0194 }
            r12.disconnect();	 Catch:{ Throwable -> 0x0194 }
        L_0x0121:
            if (r6 == 0) goto L_0x0126;
        L_0x0123:
            r6.close();	 Catch:{ Throwable -> 0x012d }
        L_0x0126:
            if (r10 == 0) goto L_0x012b;
        L_0x0128:
            r10.close();	 Catch:{ Exception -> 0x0132 }
        L_0x012b:
            r9 = r10;
            goto L_0x00d9;
        L_0x012d:
            r5 = move-exception;
            org.telegram.messenger.FileLog.e(r5);
            goto L_0x0126;
        L_0x0132:
            r12 = move-exception;
            r9 = r10;
            goto L_0x00d9;
        L_0x0135:
            r12 = r5 instanceof java.net.UnknownHostException;	 Catch:{ all -> 0x013d }
            if (r12 == 0) goto L_0x0152;
        L_0x0139:
            r12 = 0;
            r15.canRetry = r12;	 Catch:{ all -> 0x013d }
            goto L_0x00c3;
        L_0x013d:
            r12 = move-exception;
        L_0x013e:
            r13 = r15.httpConnection;	 Catch:{ Throwable -> 0x018c }
            if (r13 == 0) goto L_0x0147;
        L_0x0142:
            r13 = r15.httpConnection;	 Catch:{ Throwable -> 0x018c }
            r13.disconnect();	 Catch:{ Throwable -> 0x018c }
        L_0x0147:
            if (r6 == 0) goto L_0x014c;
        L_0x0149:
            r6.close();	 Catch:{ Throwable -> 0x017d }
        L_0x014c:
            if (r9 == 0) goto L_0x0151;
        L_0x014e:
            r9.close();	 Catch:{ Exception -> 0x018a }
        L_0x0151:
            throw r12;
        L_0x0152:
            r12 = r5 instanceof java.net.SocketException;	 Catch:{ all -> 0x013d }
            if (r12 == 0) goto L_0x016e;
        L_0x0156:
            r12 = r5.getMessage();	 Catch:{ all -> 0x013d }
            if (r12 == 0) goto L_0x00c3;
        L_0x015c:
            r12 = r5.getMessage();	 Catch:{ all -> 0x013d }
            r13 = "ECONNRESET";
            r12 = r12.contains(r13);	 Catch:{ all -> 0x013d }
            if (r12 == 0) goto L_0x00c3;
        L_0x0169:
            r12 = 0;
            r15.canRetry = r12;	 Catch:{ all -> 0x013d }
            goto L_0x00c3;
        L_0x016e:
            r12 = r5 instanceof java.io.FileNotFoundException;	 Catch:{ all -> 0x013d }
            if (r12 == 0) goto L_0x00c3;
        L_0x0172:
            r12 = 0;
            r15.canRetry = r12;	 Catch:{ all -> 0x013d }
            goto L_0x00c3;
        L_0x0177:
            r5 = move-exception;
            org.telegram.messenger.FileLog.e(r5);
            goto L_0x00d4;
        L_0x017d:
            r5 = move-exception;
            org.telegram.messenger.FileLog.e(r5);
            goto L_0x014c;
        L_0x0182:
            r12 = move-exception;
            goto L_0x00ae;
        L_0x0185:
            r12 = move-exception;
            goto L_0x0111;
        L_0x0187:
            r12 = move-exception;
            goto L_0x00d9;
        L_0x018a:
            r13 = move-exception;
            goto L_0x0151;
        L_0x018c:
            r13 = move-exception;
            goto L_0x0147;
        L_0x018e:
            r12 = move-exception;
            r9 = r10;
            goto L_0x013e;
        L_0x0191:
            r12 = move-exception;
            goto L_0x00cf;
        L_0x0194:
            r12 = move-exception;
            goto L_0x0121;
        L_0x0196:
            r12 = move-exception;
            goto L_0x0107;
        L_0x0199:
            r12 = move-exception;
            goto L_0x00a4;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ImageLoader.ArtworkLoadTask.doInBackground(java.lang.Void[]):java.lang.String");
        }

        protected void onPostExecute(String result) {
            if (result != null) {
                this.cacheImage.httpTask = new HttpImageTask(this.cacheImage, 0, result);
                this.this$0.httpTasks.add(this.cacheImage.httpTask);
                this.this$0.runHttpTasks(false);
            } else if (this.canRetry) {
                this.this$0.artworkLoadError(this.cacheImage.url);
            }
            this.this$0.imageLoadQueue.postRunnable(new ImageLoader$ArtworkLoadTask$$Lambda$0(this));
        }

        final /* synthetic */ void lambda$onPostExecute$0$ImageLoader$ArtworkLoadTask() {
            this.this$0.runArtworkTasks(true);
        }

        final /* synthetic */ void lambda$onCancelled$1$ImageLoader$ArtworkLoadTask() {
            this.this$0.runArtworkTasks(true);
        }

        protected void onCancelled() {
            this.this$0.imageLoadQueue.postRunnable(new ImageLoader$ArtworkLoadTask$$Lambda$1(this));
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
        protected String httpUrl;
        protected ArrayList<ImageReceiver> imageReceiverArray;
        protected String key;
        protected ArrayList<String> keys;
        protected TLObject location;
        protected SecureDocument secureDocument;
        protected boolean selfThumb;
        protected File tempFilePath;
        protected ArrayList<Boolean> thumbs;
        protected String url;

        private CacheImage() {
            this.imageReceiverArray = new ArrayList();
            this.keys = new ArrayList();
            this.filters = new ArrayList();
            this.thumbs = new ArrayList();
        }

        /* synthetic */ CacheImage(ImageLoader x0, AnonymousClass1 x1) {
            this();
        }

        public void addImageReceiver(ImageReceiver imageReceiver, String key, String filter, boolean thumb) {
            if (!this.imageReceiverArray.contains(imageReceiver)) {
                this.imageReceiverArray.add(imageReceiver);
                this.keys.add(key);
                this.filters.add(filter);
                this.thumbs.add(Boolean.valueOf(thumb));
                ImageLoader.this.imageLoadingByTag.put(imageReceiver.getTag(thumb), this);
            }
        }

        public void replaceImageReceiver(ImageReceiver imageReceiver, String key, String filter, boolean thumb) {
            int index = this.imageReceiverArray.indexOf(imageReceiver);
            if (index != -1) {
                if (((Boolean) this.thumbs.get(index)).booleanValue() != thumb) {
                    index = this.imageReceiverArray.subList(index + 1, this.imageReceiverArray.size()).indexOf(imageReceiver);
                    if (index == -1) {
                        return;
                    }
                }
                this.keys.set(index, key);
                this.filters.set(index, filter);
            }
        }

        public void removeImageReceiver(ImageReceiver imageReceiver) {
            Boolean thumb = Boolean.valueOf(this.selfThumb);
            int a = 0;
            while (a < this.imageReceiverArray.size()) {
                ImageReceiver obj = (ImageReceiver) this.imageReceiverArray.get(a);
                if (obj == null || obj == imageReceiver) {
                    this.imageReceiverArray.remove(a);
                    this.keys.remove(a);
                    this.filters.remove(a);
                    thumb = (Boolean) this.thumbs.remove(a);
                    if (obj != null) {
                        ImageLoader.this.imageLoadingByTag.remove(obj.getTag(thumb.booleanValue()));
                    }
                    a--;
                }
                a++;
            }
            if (this.imageReceiverArray.size() == 0) {
                for (a = 0; a < this.imageReceiverArray.size(); a++) {
                    ImageLoader.this.imageLoadingByTag.remove(((ImageReceiver) this.imageReceiverArray.get(a)).getTag(thumb.booleanValue()));
                }
                this.imageReceiverArray.clear();
                if (!(this.location == null || ImageLoader.this.forceLoadingImages.containsKey(this.key))) {
                    if (this.location instanceof FileLocation) {
                        FileLoader.getInstance(this.currentAccount).cancelLoadFile((FileLocation) this.location, this.ext);
                    } else if (this.location instanceof Document) {
                        FileLoader.getInstance(this.currentAccount).cancelLoadFile((Document) this.location);
                    } else if (this.location instanceof SecureDocument) {
                        FileLoader.getInstance(this.currentAccount).cancelLoadFile((SecureDocument) this.location);
                    } else if (this.location instanceof WebFile) {
                        FileLoader.getInstance(this.currentAccount).cancelLoadFile((WebFile) this.location);
                    }
                }
                if (this.cacheTask != null) {
                    if (this.selfThumb) {
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

        public void setImageAndClear(BitmapDrawable image) {
            if (image != null) {
                AndroidUtilities.runOnUIThread(new ImageLoader$CacheImage$$Lambda$0(this, image, new ArrayList(this.imageReceiverArray)));
            }
            for (int a = 0; a < this.imageReceiverArray.size(); a++) {
                ImageLoader.this.imageLoadingByTag.remove(((ImageReceiver) this.imageReceiverArray.get(a)).getTag(this.selfThumb));
            }
            this.imageReceiverArray.clear();
            if (this.url != null) {
                ImageLoader.this.imageLoadingByUrl.remove(this.url);
            }
            if (this.key != null) {
                ImageLoader.this.imageLoadingByKeys.remove(this.key);
            }
        }

        final /* synthetic */ void lambda$setImageAndClear$0$ImageLoader$CacheImage(BitmapDrawable image, ArrayList finalImageReceiverArray) {
            int a;
            if (image instanceof AnimatedFileDrawable) {
                boolean imageSet = false;
                BitmapDrawable fileDrawable = (AnimatedFileDrawable) image;
                a = 0;
                while (a < finalImageReceiverArray.size()) {
                    if (((ImageReceiver) finalImageReceiverArray.get(a)).setImageBitmapByKey(a == 0 ? fileDrawable : fileDrawable.makeCopy(), this.key, this.selfThumb, false)) {
                        imageSet = true;
                    }
                    a++;
                }
                if (!imageSet) {
                    ((AnimatedFileDrawable) image).recycle();
                    return;
                }
                return;
            }
            for (a = 0; a < finalImageReceiverArray.size(); a++) {
                ((ImageReceiver) finalImageReceiverArray.get(a)).setImageBitmapByKey(image, this.key, this.selfThumb, false);
            }
        }
    }

    private class CacheOutTask implements Runnable {
        private CacheImage cacheImage;
        private boolean isCancelled;
        private Thread runningThread;
        private final Object sync = new Object();

        public CacheOutTask(CacheImage image) {
            this.cacheImage = image;
        }

        /* JADX WARNING: Removed duplicated region for block: B:142:0x02bc A:{SYNTHETIC, Splitter: B:142:0x02bc} */
        /* JADX WARNING: Removed duplicated region for block: B:469:0x0a65  */
        /* JADX WARNING: Removed duplicated region for block: B:123:0x0276  */
        /* JADX WARNING: Missing block: B:8:0x0021, code:
            if ((r64.cacheImage.location instanceof org.telegram.tgnet.TLRPC.TL_photoStrippedSize) == false) goto L_0x0106;
     */
        /* JADX WARNING: Missing block: B:9:0x0023, code:
            r7 = r64.sync;
     */
        /* JADX WARNING: Missing block: B:10:0x0027, code:
            monitor-enter(r7);
     */
        /* JADX WARNING: Missing block: B:13:0x002c, code:
            if (r64.isCancelled == false) goto L_0x0036;
     */
        /* JADX WARNING: Missing block: B:14:0x002e, code:
            monitor-exit(r7);
     */
        /* JADX WARNING: Missing block: B:23:?, code:
            monitor-exit(r7);
     */
        /* JADX WARNING: Missing block: B:24:0x0037, code:
            r50 = (org.telegram.tgnet.TLRPC.TL_photoStrippedSize) r64.cacheImage.location;
            r40 = ((r50.bytes.length - 3) + org.telegram.messenger.Bitmaps.header.length) + org.telegram.messenger.Bitmaps.footer.length;
     */
        /* JADX WARNING: Missing block: B:25:0x0055, code:
            if (org.telegram.messenger.ImageLoader.access$1500() == null) goto L_0x0100;
     */
        /* JADX WARNING: Missing block: B:27:0x005e, code:
            if (org.telegram.messenger.ImageLoader.access$1500().length < r40) goto L_0x0100;
     */
        /* JADX WARNING: Missing block: B:28:0x0060, code:
            r26 = org.telegram.messenger.ImageLoader.access$1500();
     */
        /* JADX WARNING: Missing block: B:29:0x0064, code:
            if (r26 != null) goto L_0x006f;
     */
        /* JADX WARNING: Missing block: B:30:0x0066, code:
            r26 = new byte[r40];
            org.telegram.messenger.ImageLoader.access$1502(r26);
     */
        /* JADX WARNING: Missing block: B:31:0x006f, code:
            java.lang.System.arraycopy(org.telegram.messenger.Bitmaps.header, 0, r26, 0, org.telegram.messenger.Bitmaps.header.length);
            java.lang.System.arraycopy(r50.bytes, 3, r26, org.telegram.messenger.Bitmaps.header.length, r50.bytes.length - 3);
            java.lang.System.arraycopy(org.telegram.messenger.Bitmaps.footer, 0, r26, (org.telegram.messenger.Bitmaps.header.length + r50.bytes.length) - 3, org.telegram.messenger.Bitmaps.footer.length);
            r26[164] = r50.bytes[1];
            r26[166] = r50.bytes[2];
            r4 = android.graphics.BitmapFactory.decodeByteArray(r26, 0, r40);
     */
        /* JADX WARNING: Missing block: B:32:0x00c4, code:
            if (r4 == null) goto L_0x00f2;
     */
        /* JADX WARNING: Missing block: B:34:0x00d0, code:
            if (android.text.TextUtils.isEmpty(r64.cacheImage.filter) != false) goto L_0x00f2;
     */
        /* JADX WARNING: Missing block: B:36:0x00df, code:
            if (r64.cacheImage.filter.contains("b") == false) goto L_0x00f2;
     */
        /* JADX WARNING: Missing block: B:37:0x00e1, code:
            org.telegram.messenger.Utilities.blurBitmap(r4, 3, 1, r4.getWidth(), r4.getHeight(), r4.getRowBytes());
     */
        /* JADX WARNING: Missing block: B:38:0x00f2, code:
            if (r4 == null) goto L_0x0104;
     */
        /* JADX WARNING: Missing block: B:39:0x00f4, code:
            r6 = new android.graphics.drawable.BitmapDrawable(r4);
     */
        /* JADX WARNING: Missing block: B:40:0x00f9, code:
            onPostExecute(r6);
     */
        /* JADX WARNING: Missing block: B:41:0x0100, code:
            r26 = null;
     */
        /* JADX WARNING: Missing block: B:42:0x0104, code:
            r6 = null;
     */
        /* JADX WARNING: Missing block: B:44:0x010c, code:
            if (r64.cacheImage.animatedFile == false) goto L_0x0153;
     */
        /* JADX WARNING: Missing block: B:45:0x010e, code:
            r7 = r64.sync;
     */
        /* JADX WARNING: Missing block: B:46:0x0112, code:
            monitor-enter(r7);
     */
        /* JADX WARNING: Missing block: B:49:0x0117, code:
            if (r64.isCancelled == false) goto L_0x011f;
     */
        /* JADX WARNING: Missing block: B:50:0x0119, code:
            monitor-exit(r7);
     */
        /* JADX WARNING: Missing block: B:55:?, code:
            monitor-exit(r7);
     */
        /* JADX WARNING: Missing block: B:56:0x0120, code:
            r7 = r64.cacheImage.finalFilePath;
     */
        /* JADX WARNING: Missing block: B:57:0x012e, code:
            if (r64.cacheImage.filter == null) goto L_0x0151;
     */
        /* JADX WARNING: Missing block: B:59:0x013d, code:
            if (r64.cacheImage.filter.equals("d") == false) goto L_0x0151;
     */
        /* JADX WARNING: Missing block: B:60:0x013f, code:
            r6 = true;
     */
        /* JADX WARNING: Missing block: B:61:0x0140, code:
            r0 = new org.telegram.ui.Components.AnimatedFileDrawable(r7, r6);
            java.lang.Thread.interrupted();
            onPostExecute(r0);
     */
        /* JADX WARNING: Missing block: B:62:0x0151, code:
            r6 = false;
     */
        /* JADX WARNING: Missing block: B:63:0x0153, code:
            r41 = null;
            r42 = false;
            r5 = null;
            r44 = false;
            r47 = 0;
            r23 = r64.cacheImage.finalFilePath;
     */
        /* JADX WARNING: Missing block: B:64:0x016a, code:
            if (r64.cacheImage.secureDocument != null) goto L_0x0183;
     */
        /* JADX WARNING: Missing block: B:66:0x0172, code:
            if (r64.cacheImage.encryptionKeyPath == null) goto L_0x0286;
     */
        /* JADX WARNING: Missing block: B:67:0x0174, code:
            if (r23 == null) goto L_0x0286;
     */
        /* JADX WARNING: Missing block: B:69:0x0181, code:
            if (r23.getAbsolutePath().endsWith(".enc") == false) goto L_0x0286;
     */
        /* JADX WARNING: Missing block: B:70:0x0183, code:
            r38 = true;
     */
        /* JADX WARNING: Missing block: B:72:0x018b, code:
            if (r64.cacheImage.secureDocument == null) goto L_0x0296;
     */
        /* JADX WARNING: Missing block: B:73:0x018d, code:
            r59 = r64.cacheImage.secureDocument.secureDocumentKey;
     */
        /* JADX WARNING: Missing block: B:74:0x019f, code:
            if (r64.cacheImage.secureDocument.secureFile == null) goto L_0x028a;
     */
        /* JADX WARNING: Missing block: B:76:0x01ab, code:
            if (r64.cacheImage.secureDocument.secureFile.file_hash == null) goto L_0x028a;
     */
        /* JADX WARNING: Missing block: B:77:0x01ad, code:
            r58 = r64.cacheImage.secureDocument.secureFile.file_hash;
     */
        /* JADX WARNING: Missing block: B:78:0x01b9, code:
            r24 = true;
            r61 = false;
     */
        /* JADX WARNING: Missing block: B:79:0x01c1, code:
            if (android.os.Build.VERSION.SDK_INT >= 19) goto L_0x0217;
     */
        /* JADX WARNING: Missing block: B:80:0x01c3, code:
            r53 = null;
     */
        /* JADX WARNING: Missing block: B:82:?, code:
            r0 = new java.io.RandomAccessFile(r23, "r");
     */
        /* JADX WARNING: Missing block: B:85:0x01d7, code:
            if (r64.cacheImage.selfThumb == false) goto L_0x029c;
     */
        /* JADX WARNING: Missing block: B:86:0x01d9, code:
            r22 = org.telegram.messenger.ImageLoader.access$1600();
     */
        /* JADX WARNING: Missing block: B:87:0x01dd, code:
            r0.readFully(r22, 0, r22.length);
            r60 = new java.lang.String(r22).toLowerCase().toLowerCase();
     */
        /* JADX WARNING: Missing block: B:88:0x0200, code:
            if (r60.startsWith("riff") == false) goto L_0x020f;
     */
        /* JADX WARNING: Missing block: B:90:0x020b, code:
            if (r60.endsWith("webp") == false) goto L_0x020f;
     */
        /* JADX WARNING: Missing block: B:91:0x020d, code:
            r61 = true;
     */
        /* JADX WARNING: Missing block: B:92:0x020f, code:
            r0.close();
     */
        /* JADX WARNING: Missing block: B:93:0x0212, code:
            if (r0 == null) goto L_0x0217;
     */
        /* JADX WARNING: Missing block: B:95:?, code:
            r0.close();
     */
        /* JADX WARNING: Missing block: B:97:0x021d, code:
            if (r64.cacheImage.selfThumb != false) goto L_0x021f;
     */
        /* JADX WARNING: Missing block: B:98:0x021f, code:
            r18 = 0;
            r25 = false;
     */
        /* JADX WARNING: Missing block: B:99:0x0229, code:
            if (r64.cacheImage.filter != null) goto L_0x022b;
     */
        /* JADX WARNING: Missing block: B:101:0x0238, code:
            if (r64.cacheImage.filter.contains("b2") != false) goto L_0x023a;
     */
        /* JADX WARNING: Missing block: B:102:0x023a, code:
            r18 = 3;
     */
        /* JADX WARNING: Missing block: B:104:0x0249, code:
            if (r64.cacheImage.filter.contains("i") != false) goto L_0x024b;
     */
        /* JADX WARNING: Missing block: B:105:0x024b, code:
            r25 = true;
     */
        /* JADX WARNING: Missing block: B:107:?, code:
            org.telegram.messenger.ImageLoader.access$1802(r64.this$0, java.lang.System.currentTimeMillis());
     */
        /* JADX WARNING: Missing block: B:108:0x025c, code:
            monitor-enter(r64.sync);
     */
        /* JADX WARNING: Missing block: B:111:0x0261, code:
            if (r64.isCancelled != false) goto L_0x0263;
     */
        /* JADX WARNING: Missing block: B:117:0x0269, code:
            r28 = move-exception;
     */
        /* JADX WARNING: Missing block: B:118:0x026a, code:
            org.telegram.messenger.FileLog.e(r28);
     */
        /* JADX WARNING: Missing block: B:123:0x0276, code:
            r6 = new org.telegram.messenger.ExtendedBitmapDrawable(r5, r44, r47);
     */
        /* JADX WARNING: Missing block: B:125:0x0286, code:
            r38 = false;
     */
        /* JADX WARNING: Missing block: B:126:0x028a, code:
            r58 = r64.cacheImage.secureDocument.fileHash;
     */
        /* JADX WARNING: Missing block: B:127:0x0296, code:
            r59 = null;
            r58 = null;
     */
        /* JADX WARNING: Missing block: B:129:?, code:
            r22 = org.telegram.messenger.ImageLoader.access$1700();
     */
        /* JADX WARNING: Missing block: B:130:0x02a2, code:
            r28 = move-exception;
     */
        /* JADX WARNING: Missing block: B:131:0x02a3, code:
            org.telegram.messenger.FileLog.e(r28);
     */
        /* JADX WARNING: Missing block: B:132:0x02a8, code:
            r28 = e;
     */
        /* JADX WARNING: Missing block: B:134:?, code:
            org.telegram.messenger.FileLog.e(r28);
     */
        /* JADX WARNING: Missing block: B:135:0x02ac, code:
            if (r53 != null) goto L_0x02ae;
     */
        /* JADX WARNING: Missing block: B:137:?, code:
            r53.close();
     */
        /* JADX WARNING: Missing block: B:138:0x02b3, code:
            r28 = move-exception;
     */
        /* JADX WARNING: Missing block: B:139:0x02b4, code:
            org.telegram.messenger.FileLog.e(r28);
     */
        /* JADX WARNING: Missing block: B:140:0x02b9, code:
            r6 = th;
     */
        /* JADX WARNING: Missing block: B:141:0x02ba, code:
            if (r53 != null) goto L_0x02bc;
     */
        /* JADX WARNING: Missing block: B:143:?, code:
            r53.close();
     */
        /* JADX WARNING: Missing block: B:144:0x02bf, code:
            throw r6;
     */
        /* JADX WARNING: Missing block: B:145:0x02c0, code:
            r28 = move-exception;
     */
        /* JADX WARNING: Missing block: B:146:0x02c1, code:
            org.telegram.messenger.FileLog.e(r28);
     */
        /* JADX WARNING: Missing block: B:148:0x02d2, code:
            if (r64.cacheImage.filter.contains("b1") != false) goto L_0x02d4;
     */
        /* JADX WARNING: Missing block: B:149:0x02d4, code:
            r18 = 2;
     */
        /* JADX WARNING: Missing block: B:151:0x02e5, code:
            if (r64.cacheImage.filter.contains("b") != false) goto L_0x02e7;
     */
        /* JADX WARNING: Missing block: B:152:0x02e7, code:
            r18 = 1;
     */
        /* JADX WARNING: Missing block: B:156:?, code:
            r46 = new android.graphics.BitmapFactory.Options();
            r46.inSampleSize = 1;
     */
        /* JADX WARNING: Missing block: B:157:0x02fa, code:
            if (android.os.Build.VERSION.SDK_INT >= 21) goto L_0x0301;
     */
        /* JADX WARNING: Missing block: B:158:0x02fc, code:
            r46.inPurgeable = true;
     */
        /* JADX WARNING: Missing block: B:159:0x0301, code:
            if (r61 == false) goto L_0x0373;
     */
        /* JADX WARNING: Missing block: B:160:0x0303, code:
            r0 = new java.io.RandomAccessFile(r23, "r");
            r21 = r0.getChannel().map(java.nio.channels.FileChannel.MapMode.READ_ONLY, 0, r23.length());
            r20 = new android.graphics.BitmapFactory.Options();
            r20.inJustDecodeBounds = true;
            org.telegram.messenger.Utilities.loadWebpImage(null, r21, r21.limit(), r20, true);
            r5 = org.telegram.messenger.Bitmaps.createBitmap(r20.outWidth, r20.outHeight, android.graphics.Bitmap.Config.ARGB_8888);
            r7 = r21.limit();
     */
        /* JADX WARNING: Missing block: B:161:0x034d, code:
            if (r46.inPurgeable != false) goto L_0x0371;
     */
        /* JADX WARNING: Missing block: B:162:0x034f, code:
            r6 = true;
     */
        /* JADX WARNING: Missing block: B:163:0x0350, code:
            org.telegram.messenger.Utilities.loadWebpImage(r5, r21, r7, null, r6);
            r0.close();
     */
        /* JADX WARNING: Missing block: B:164:0x0358, code:
            if (r5 != null) goto L_0x0436;
     */
        /* JADX WARNING: Missing block: B:166:0x0362, code:
            if (r23.length() == 0) goto L_0x036c;
     */
        /* JADX WARNING: Missing block: B:168:0x036a, code:
            if (r64.cacheImage.filter != null) goto L_0x026d;
     */
        /* JADX WARNING: Missing block: B:169:0x036c, code:
            r23.delete();
     */
        /* JADX WARNING: Missing block: B:170:0x0371, code:
            r6 = false;
     */
        /* JADX WARNING: Missing block: B:172:0x0377, code:
            if (r46.inPurgeable != false) goto L_0x037b;
     */
        /* JADX WARNING: Missing block: B:173:0x0379, code:
            if (r59 == null) goto L_0x040d;
     */
        /* JADX WARNING: Missing block: B:174:0x037b, code:
            r0 = new java.io.RandomAccessFile(r23, "r");
            r40 = (int) r0.length();
            r45 = 0;
     */
        /* JADX WARNING: Missing block: B:175:0x0394, code:
            if (org.telegram.messenger.ImageLoader.access$1900() == null) goto L_0x03f9;
     */
        /* JADX WARNING: Missing block: B:177:0x039d, code:
            if (org.telegram.messenger.ImageLoader.access$1900().length < r40) goto L_0x03f9;
     */
        /* JADX WARNING: Missing block: B:178:0x039f, code:
            r26 = org.telegram.messenger.ImageLoader.access$1900();
     */
        /* JADX WARNING: Missing block: B:179:0x03a3, code:
            if (r26 != null) goto L_0x03ae;
     */
        /* JADX WARNING: Missing block: B:180:0x03a5, code:
            r26 = new byte[r40];
            org.telegram.messenger.ImageLoader.access$1902(r26);
     */
        /* JADX WARNING: Missing block: B:181:0x03ae, code:
            r0.readFully(r26, 0, r40);
            r0.close();
            r29 = false;
     */
        /* JADX WARNING: Missing block: B:182:0x03bd, code:
            if (r59 == null) goto L_0x03fc;
     */
        /* JADX WARNING: Missing block: B:183:0x03bf, code:
            org.telegram.messenger.secretmedia.EncryptedFileInputStream.decryptBytesWithKeyFile(r26, 0, r40, r59);
            r36 = org.telegram.messenger.Utilities.computeSHA256(r26, 0, r40);
     */
        /* JADX WARNING: Missing block: B:184:0x03d2, code:
            if (r58 == null) goto L_0x03de;
     */
        /* JADX WARNING: Missing block: B:186:0x03dc, code:
            if (java.util.Arrays.equals(r36, r58) != false) goto L_0x03e0;
     */
        /* JADX WARNING: Missing block: B:187:0x03de, code:
            r29 = true;
     */
        /* JADX WARNING: Missing block: B:188:0x03e0, code:
            r45 = r26[0] & 255;
            r40 = r40 - r45;
     */
        /* JADX WARNING: Missing block: B:189:0x03e9, code:
            if (r29 != false) goto L_0x0358;
     */
        /* JADX WARNING: Missing block: B:190:0x03eb, code:
            r5 = android.graphics.BitmapFactory.decodeByteArray(r26, r45, r40, r46);
     */
        /* JADX WARNING: Missing block: B:191:0x03f9, code:
            r26 = null;
     */
        /* JADX WARNING: Missing block: B:192:0x03fc, code:
            if (r38 == false) goto L_0x03e9;
     */
        /* JADX WARNING: Missing block: B:193:0x03fe, code:
            org.telegram.messenger.secretmedia.EncryptedFileInputStream.decryptBytesWithKeyFile(r26, 0, r40, r64.cacheImage.encryptionKeyPath);
     */
        /* JADX WARNING: Missing block: B:194:0x040d, code:
            if (r38 == false) goto L_0x042c;
     */
        /* JADX WARNING: Missing block: B:195:0x040f, code:
            r0 = new org.telegram.messenger.secretmedia.EncryptedFileInputStream(r23, r64.cacheImage.encryptionKeyPath);
     */
        /* JADX WARNING: Missing block: B:196:0x041e, code:
            r5 = android.graphics.BitmapFactory.decodeStream(r39, null, r46);
            r39.close();
     */
        /* JADX WARNING: Missing block: B:197:0x042c, code:
            r0 = new java.io.FileInputStream(r23);
     */
        /* JADX WARNING: Missing block: B:198:0x0436, code:
            if (r25 == false) goto L_0x0453;
     */
        /* JADX WARNING: Missing block: B:200:0x043c, code:
            if (r46.inPurgeable == false) goto L_0x0479;
     */
        /* JADX WARNING: Missing block: B:201:0x043e, code:
            r6 = 0;
     */
        /* JADX WARNING: Missing block: B:203:0x044f, code:
            if (org.telegram.messenger.Utilities.needInvert(r5, r6, r5.getWidth(), r5.getHeight(), r5.getRowBytes()) == 0) goto L_0x047b;
     */
        /* JADX WARNING: Missing block: B:204:0x0451, code:
            r44 = true;
     */
        /* JADX WARNING: Missing block: B:206:0x0456, code:
            if (r18 != 1) goto L_0x0480;
     */
        /* JADX WARNING: Missing block: B:208:0x045e, code:
            if (r5.getConfig() != android.graphics.Bitmap.Config.ARGB_8888) goto L_0x026d;
     */
        /* JADX WARNING: Missing block: B:210:0x0465, code:
            if (r46.inPurgeable == false) goto L_0x047e;
     */
        /* JADX WARNING: Missing block: B:211:0x0467, code:
            r7 = 0;
     */
        /* JADX WARNING: Missing block: B:212:0x0468, code:
            org.telegram.messenger.Utilities.blurBitmap(r5, 3, r7, r5.getWidth(), r5.getHeight(), r5.getRowBytes());
     */
        /* JADX WARNING: Missing block: B:213:0x0479, code:
            r6 = 1;
     */
        /* JADX WARNING: Missing block: B:214:0x047b, code:
            r44 = false;
     */
        /* JADX WARNING: Missing block: B:215:0x047e, code:
            r7 = 1;
     */
        /* JADX WARNING: Missing block: B:217:0x0483, code:
            if (r18 != 2) goto L_0x04a8;
     */
        /* JADX WARNING: Missing block: B:219:0x048b, code:
            if (r5.getConfig() != android.graphics.Bitmap.Config.ARGB_8888) goto L_0x026d;
     */
        /* JADX WARNING: Missing block: B:221:0x0492, code:
            if (r46.inPurgeable == false) goto L_0x04a6;
     */
        /* JADX WARNING: Missing block: B:222:0x0494, code:
            r7 = 0;
     */
        /* JADX WARNING: Missing block: B:223:0x0495, code:
            org.telegram.messenger.Utilities.blurBitmap(r5, 1, r7, r5.getWidth(), r5.getHeight(), r5.getRowBytes());
     */
        /* JADX WARNING: Missing block: B:224:0x04a6, code:
            r7 = 1;
     */
        /* JADX WARNING: Missing block: B:226:0x04ab, code:
            if (r18 != 3) goto L_0x0502;
     */
        /* JADX WARNING: Missing block: B:228:0x04b3, code:
            if (r5.getConfig() != android.graphics.Bitmap.Config.ARGB_8888) goto L_0x026d;
     */
        /* JADX WARNING: Missing block: B:230:0x04ba, code:
            if (r46.inPurgeable == false) goto L_0x04fc;
     */
        /* JADX WARNING: Missing block: B:231:0x04bc, code:
            r7 = 0;
     */
        /* JADX WARNING: Missing block: B:232:0x04bd, code:
            org.telegram.messenger.Utilities.blurBitmap(r5, 7, r7, r5.getWidth(), r5.getHeight(), r5.getRowBytes());
     */
        /* JADX WARNING: Missing block: B:233:0x04d1, code:
            if (r46.inPurgeable == false) goto L_0x04fe;
     */
        /* JADX WARNING: Missing block: B:234:0x04d3, code:
            r7 = 0;
     */
        /* JADX WARNING: Missing block: B:235:0x04d4, code:
            org.telegram.messenger.Utilities.blurBitmap(r5, 7, r7, r5.getWidth(), r5.getHeight(), r5.getRowBytes());
     */
        /* JADX WARNING: Missing block: B:236:0x04e8, code:
            if (r46.inPurgeable == false) goto L_0x0500;
     */
        /* JADX WARNING: Missing block: B:237:0x04ea, code:
            r7 = 0;
     */
        /* JADX WARNING: Missing block: B:238:0x04eb, code:
            org.telegram.messenger.Utilities.blurBitmap(r5, 7, r7, r5.getWidth(), r5.getHeight(), r5.getRowBytes());
     */
        /* JADX WARNING: Missing block: B:239:0x04fc, code:
            r7 = 1;
     */
        /* JADX WARNING: Missing block: B:240:0x04fe, code:
            r7 = 1;
     */
        /* JADX WARNING: Missing block: B:241:0x0500, code:
            r7 = 1;
     */
        /* JADX WARNING: Missing block: B:242:0x0502, code:
            if (r18 != 0) goto L_0x026d;
     */
        /* JADX WARNING: Missing block: B:244:0x0508, code:
            if (r46.inPurgeable == false) goto L_0x026d;
     */
        /* JADX WARNING: Missing block: B:245:0x050a, code:
            org.telegram.messenger.Utilities.pinBitmap(r5);
     */
        /* JADX WARNING: Missing block: B:246:0x050f, code:
            r43 = null;
     */
        /* JADX WARNING: Missing block: B:249:0x0517, code:
            if (r64.cacheImage.httpUrl != null) goto L_0x0519;
     */
        /* JADX WARNING: Missing block: B:251:0x0526, code:
            if (r64.cacheImage.httpUrl.startsWith("thumb://") != false) goto L_0x0528;
     */
        /* JADX WARNING: Missing block: B:252:0x0528, code:
            r37 = r64.cacheImage.httpUrl.indexOf(":", 8);
     */
        /* JADX WARNING: Missing block: B:253:0x0537, code:
            if (r37 >= 0) goto L_0x0539;
     */
        /* JADX WARNING: Missing block: B:254:0x0539, code:
            r41 = java.lang.Long.valueOf(java.lang.Long.parseLong(r64.cacheImage.httpUrl.substring(8, r37)));
            r42 = false;
            r43 = r64.cacheImage.httpUrl.substring(r37 + 1);
     */
        /* JADX WARNING: Missing block: B:255:0x055d, code:
            r24 = false;
     */
        /* JADX WARNING: Missing block: B:256:0x055f, code:
            r27 = 20;
     */
        /* JADX WARNING: Missing block: B:257:0x0561, code:
            if (r41 != null) goto L_0x0563;
     */
        /* JADX WARNING: Missing block: B:258:0x0563, code:
            r27 = 0;
     */
        /* JADX WARNING: Missing block: B:266:0x058f, code:
            java.lang.Thread.sleep((long) r27);
     */
        /* JADX WARNING: Missing block: B:267:0x0595, code:
            org.telegram.messenger.ImageLoader.access$1802(r64.this$0, java.lang.System.currentTimeMillis());
     */
        /* JADX WARNING: Missing block: B:268:0x05a4, code:
            monitor-enter(r64.sync);
     */
        /* JADX WARNING: Missing block: B:271:0x05a9, code:
            if (r64.isCancelled != false) goto L_0x05ab;
     */
        /* JADX WARNING: Missing block: B:279:0x05c1, code:
            if (r64.cacheImage.httpUrl.startsWith("vthumb://") != false) goto L_0x05c3;
     */
        /* JADX WARNING: Missing block: B:280:0x05c3, code:
            r37 = r64.cacheImage.httpUrl.indexOf(":", 9);
     */
        /* JADX WARNING: Missing block: B:281:0x05d2, code:
            if (r37 >= 0) goto L_0x05d4;
     */
        /* JADX WARNING: Missing block: B:282:0x05d4, code:
            r41 = java.lang.Long.valueOf(java.lang.Long.parseLong(r64.cacheImage.httpUrl.substring(9, r37)));
            r42 = true;
     */
        /* JADX WARNING: Missing block: B:283:0x05ec, code:
            r24 = false;
     */
        /* JADX WARNING: Missing block: B:285:0x05fd, code:
            if (r64.cacheImage.httpUrl.startsWith("http") == false) goto L_0x05ff;
     */
        /* JADX WARNING: Missing block: B:286:0x05ff, code:
            r24 = false;
     */
        /* JADX WARNING: Missing block: B:290:?, code:
            r46 = new android.graphics.BitmapFactory.Options();
            r46.inSampleSize = 1;
            r63 = 0.0f;
            r35 = 0.0f;
            r17 = false;
            r25 = false;
     */
        /* JADX WARNING: Missing block: B:291:0x061c, code:
            if (r64.cacheImage.filter == null) goto L_0x0780;
     */
        /* JADX WARNING: Missing block: B:292:0x061e, code:
            r12 = r64.cacheImage.filter.split("_");
     */
        /* JADX WARNING: Missing block: B:293:0x062d, code:
            if (r12.length < 2) goto L_0x0645;
     */
        /* JADX WARNING: Missing block: B:294:0x062f, code:
            r63 = java.lang.Float.parseFloat(r12[0]) * org.telegram.messenger.AndroidUtilities.density;
            r35 = java.lang.Float.parseFloat(r12[1]) * org.telegram.messenger.AndroidUtilities.density;
     */
        /* JADX WARNING: Missing block: B:296:0x0652, code:
            if (r64.cacheImage.filter.contains("b") == false) goto L_0x0656;
     */
        /* JADX WARNING: Missing block: B:297:0x0654, code:
            r17 = true;
     */
        /* JADX WARNING: Missing block: B:299:0x0663, code:
            if (r64.cacheImage.filter.contains("i") == false) goto L_0x0667;
     */
        /* JADX WARNING: Missing block: B:300:0x0665, code:
            r25 = true;
     */
        /* JADX WARNING: Missing block: B:302:0x066a, code:
            if (r63 == 0.0f) goto L_0x06b6;
     */
        /* JADX WARNING: Missing block: B:304:0x066f, code:
            if (r35 == 0.0f) goto L_0x06b6;
     */
        /* JADX WARNING: Missing block: B:305:0x0671, code:
            r46.inJustDecodeBounds = true;
     */
        /* JADX WARNING: Missing block: B:306:0x0676, code:
            if (r41 == null) goto L_0x06d8;
     */
        /* JADX WARNING: Missing block: B:307:0x0678, code:
            if (r43 != null) goto L_0x06d8;
     */
        /* JADX WARNING: Missing block: B:308:0x067a, code:
            if (r42 == false) goto L_0x06c7;
     */
        /* JADX WARNING: Missing block: B:309:0x067c, code:
            android.provider.MediaStore.Video.Thumbnails.getThumbnail(org.telegram.messenger.ApplicationLoader.applicationContext.getContentResolver(), r41.longValue(), 1, r46);
     */
        /* JADX WARNING: Missing block: B:310:0x068c, code:
            r56 = java.lang.Math.max(((float) r46.outWidth) / r63, ((float) r46.outHeight) / r35);
     */
        /* JADX WARNING: Missing block: B:311:0x06a6, code:
            if (r56 >= 1.0f) goto L_0x06aa;
     */
        /* JADX WARNING: Missing block: B:312:0x06a8, code:
            r56 = 1.0f;
     */
        /* JADX WARNING: Missing block: B:313:0x06aa, code:
            r46.inJustDecodeBounds = false;
            r46.inSampleSize = (int) r56;
     */
        /* JADX WARNING: Missing block: B:314:0x06b6, code:
            r7 = r64.sync;
     */
        /* JADX WARNING: Missing block: B:315:0x06ba, code:
            monitor-enter(r7);
     */
        /* JADX WARNING: Missing block: B:318:0x06bf, code:
            if (r64.isCancelled == false) goto L_0x07dd;
     */
        /* JADX WARNING: Missing block: B:319:0x06c1, code:
            monitor-exit(r7);
     */
        /* JADX WARNING: Missing block: B:324:0x06c7, code:
            android.provider.MediaStore.Images.Thumbnails.getThumbnail(org.telegram.messenger.ApplicationLoader.applicationContext.getContentResolver(), r41.longValue(), 1, r46);
     */
        /* JADX WARNING: Missing block: B:325:0x06d8, code:
            if (r59 == null) goto L_0x0757;
     */
        /* JADX WARNING: Missing block: B:326:0x06da, code:
            r0 = new java.io.RandomAccessFile(r23, "r");
            r40 = (int) r0.length();
     */
        /* JADX WARNING: Missing block: B:327:0x06f1, code:
            if (org.telegram.messenger.ImageLoader.access$1500() == null) goto L_0x0754;
     */
        /* JADX WARNING: Missing block: B:329:0x06fa, code:
            if (org.telegram.messenger.ImageLoader.access$1500().length < r40) goto L_0x0754;
     */
        /* JADX WARNING: Missing block: B:330:0x06fc, code:
            r26 = org.telegram.messenger.ImageLoader.access$1500();
     */
        /* JADX WARNING: Missing block: B:331:0x0700, code:
            if (r26 != null) goto L_0x070b;
     */
        /* JADX WARNING: Missing block: B:332:0x0702, code:
            r26 = new byte[r40];
            org.telegram.messenger.ImageLoader.access$1502(r26);
     */
        /* JADX WARNING: Missing block: B:333:0x070b, code:
            r0.readFully(r26, 0, r40);
            r0.close();
            org.telegram.messenger.secretmedia.EncryptedFileInputStream.decryptBytesWithKeyFile(r26, 0, r40, r59);
            r36 = org.telegram.messenger.Utilities.computeSHA256(r26, 0, r40);
            r29 = false;
     */
        /* JADX WARNING: Missing block: B:334:0x072d, code:
            if (r58 == null) goto L_0x0739;
     */
        /* JADX WARNING: Missing block: B:336:0x0737, code:
            if (java.util.Arrays.equals(r36, r58) != false) goto L_0x073b;
     */
        /* JADX WARNING: Missing block: B:337:0x0739, code:
            r29 = true;
     */
        /* JADX WARNING: Missing block: B:338:0x073b, code:
            r45 = r26[0] & 255;
            r40 = r40 - r45;
     */
        /* JADX WARNING: Missing block: B:339:0x0744, code:
            if (r29 != false) goto L_0x068c;
     */
        /* JADX WARNING: Missing block: B:340:0x0746, code:
            r5 = android.graphics.BitmapFactory.decodeByteArray(r26, r45, r40, r46);
     */
        /* JADX WARNING: Missing block: B:341:0x0754, code:
            r26 = null;
     */
        /* JADX WARNING: Missing block: B:342:0x0757, code:
            if (r38 == false) goto L_0x0776;
     */
        /* JADX WARNING: Missing block: B:343:0x0759, code:
            r0 = new org.telegram.messenger.secretmedia.EncryptedFileInputStream(r23, r64.cacheImage.encryptionKeyPath);
     */
        /* JADX WARNING: Missing block: B:344:0x0768, code:
            r5 = android.graphics.BitmapFactory.decodeStream(r39, null, r46);
            r39.close();
     */
        /* JADX WARNING: Missing block: B:345:0x0776, code:
            r0 = new java.io.FileInputStream(r23);
     */
        /* JADX WARNING: Missing block: B:346:0x0780, code:
            if (r43 == null) goto L_0x06b6;
     */
        /* JADX WARNING: Missing block: B:347:0x0782, code:
            r46.inJustDecodeBounds = true;
            r46.inPreferredConfig = android.graphics.Bitmap.Config.RGB_565;
            r0 = new java.io.FileInputStream(r23);
            r5 = android.graphics.BitmapFactory.decodeStream(r0, null, r46);
            r0.close();
            r52 = r46.outWidth;
            r49 = r46.outHeight;
            r46.inJustDecodeBounds = false;
            r56 = (float) java.lang.Math.max(r52 / 200, r49 / 200);
     */
        /* JADX WARNING: Missing block: B:348:0x07c6, code:
            if (r56 >= 1.0f) goto L_0x07ca;
     */
        /* JADX WARNING: Missing block: B:349:0x07c8, code:
            r56 = 1.0f;
     */
        /* JADX WARNING: Missing block: B:350:0x07ca, code:
            r55 = 1;
     */
        /* JADX WARNING: Missing block: B:351:0x07cc, code:
            r55 = r55 * 2;
     */
        /* JADX WARNING: Missing block: B:352:0x07d3, code:
            if (((float) (r55 * 2)) < r56) goto L_0x07cc;
     */
        /* JADX WARNING: Missing block: B:353:0x07d5, code:
            r46.inSampleSize = r55;
     */
        /* JADX WARNING: Missing block: B:355:?, code:
            monitor-exit(r7);
     */
        /* JADX WARNING: Missing block: B:358:0x07e4, code:
            if (r64.cacheImage.filter == null) goto L_0x07f0;
     */
        /* JADX WARNING: Missing block: B:359:0x07e6, code:
            if (r17 != false) goto L_0x07f0;
     */
        /* JADX WARNING: Missing block: B:361:0x07ee, code:
            if (r64.cacheImage.httpUrl == null) goto L_0x0891;
     */
        /* JADX WARNING: Missing block: B:362:0x07f0, code:
            r46.inPreferredConfig = android.graphics.Bitmap.Config.ARGB_8888;
     */
        /* JADX WARNING: Missing block: B:364:0x07fa, code:
            if (android.os.Build.VERSION.SDK_INT >= 21) goto L_0x0801;
     */
        /* JADX WARNING: Missing block: B:365:0x07fc, code:
            r46.inPurgeable = true;
     */
        /* JADX WARNING: Missing block: B:366:0x0801, code:
            r46.inDither = false;
     */
        /* JADX WARNING: Missing block: B:367:0x0806, code:
            if (r41 == null) goto L_0x081d;
     */
        /* JADX WARNING: Missing block: B:368:0x0808, code:
            if (r43 != null) goto L_0x081d;
     */
        /* JADX WARNING: Missing block: B:369:0x080a, code:
            if (r42 == false) goto L_0x0899;
     */
        /* JADX WARNING: Missing block: B:370:0x080c, code:
            r5 = android.provider.MediaStore.Video.Thumbnails.getThumbnail(org.telegram.messenger.ApplicationLoader.applicationContext.getContentResolver(), r41.longValue(), 1, r46);
     */
        /* JADX WARNING: Missing block: B:371:0x081d, code:
            if (r5 != null) goto L_0x0876;
     */
        /* JADX WARNING: Missing block: B:372:0x081f, code:
            if (r61 == false) goto L_0x08ae;
     */
        /* JADX WARNING: Missing block: B:373:0x0821, code:
            r0 = new java.io.RandomAccessFile(r23, "r");
            r21 = r0.getChannel().map(java.nio.channels.FileChannel.MapMode.READ_ONLY, 0, r23.length());
            r20 = new android.graphics.BitmapFactory.Options();
            r20.inJustDecodeBounds = true;
            org.telegram.messenger.Utilities.loadWebpImage(null, r21, r21.limit(), r20, true);
            r5 = org.telegram.messenger.Bitmaps.createBitmap(r20.outWidth, r20.outHeight, android.graphics.Bitmap.Config.ARGB_8888);
            r7 = r21.limit();
     */
        /* JADX WARNING: Missing block: B:374:0x086b, code:
            if (r46.inPurgeable != false) goto L_0x08ac;
     */
        /* JADX WARNING: Missing block: B:375:0x086d, code:
            r6 = true;
     */
        /* JADX WARNING: Missing block: B:376:0x086e, code:
            org.telegram.messenger.Utilities.loadWebpImage(r5, r21, r7, null, r6);
            r0.close();
     */
        /* JADX WARNING: Missing block: B:384:0x0891, code:
            r46.inPreferredConfig = android.graphics.Bitmap.Config.RGB_565;
     */
        /* JADX WARNING: Missing block: B:385:0x0899, code:
            r5 = android.provider.MediaStore.Images.Thumbnails.getThumbnail(org.telegram.messenger.ApplicationLoader.applicationContext.getContentResolver(), r41.longValue(), 1, r46);
     */
        /* JADX WARNING: Missing block: B:386:0x08ac, code:
            r6 = false;
     */
        /* JADX WARNING: Missing block: B:388:0x08b2, code:
            if (r46.inPurgeable != false) goto L_0x08b6;
     */
        /* JADX WARNING: Missing block: B:389:0x08b4, code:
            if (r59 == null) goto L_0x0948;
     */
        /* JADX WARNING: Missing block: B:390:0x08b6, code:
            r0 = new java.io.RandomAccessFile(r23, "r");
            r40 = (int) r0.length();
            r45 = 0;
     */
        /* JADX WARNING: Missing block: B:391:0x08cf, code:
            if (org.telegram.messenger.ImageLoader.access$1500() == null) goto L_0x0934;
     */
        /* JADX WARNING: Missing block: B:393:0x08d8, code:
            if (org.telegram.messenger.ImageLoader.access$1500().length < r40) goto L_0x0934;
     */
        /* JADX WARNING: Missing block: B:394:0x08da, code:
            r26 = org.telegram.messenger.ImageLoader.access$1500();
     */
        /* JADX WARNING: Missing block: B:395:0x08de, code:
            if (r26 != null) goto L_0x08e9;
     */
        /* JADX WARNING: Missing block: B:396:0x08e0, code:
            r26 = new byte[r40];
            org.telegram.messenger.ImageLoader.access$1502(r26);
     */
        /* JADX WARNING: Missing block: B:397:0x08e9, code:
            r0.readFully(r26, 0, r40);
            r0.close();
            r29 = false;
     */
        /* JADX WARNING: Missing block: B:398:0x08f8, code:
            if (r59 == null) goto L_0x0937;
     */
        /* JADX WARNING: Missing block: B:399:0x08fa, code:
            org.telegram.messenger.secretmedia.EncryptedFileInputStream.decryptBytesWithKeyFile(r26, 0, r40, r59);
            r36 = org.telegram.messenger.Utilities.computeSHA256(r26, 0, r40);
     */
        /* JADX WARNING: Missing block: B:400:0x090d, code:
            if (r58 == null) goto L_0x0919;
     */
        /* JADX WARNING: Missing block: B:402:0x0917, code:
            if (java.util.Arrays.equals(r36, r58) != false) goto L_0x091b;
     */
        /* JADX WARNING: Missing block: B:403:0x0919, code:
            r29 = true;
     */
        /* JADX WARNING: Missing block: B:404:0x091b, code:
            r45 = r26[0] & 255;
            r40 = r40 - r45;
     */
        /* JADX WARNING: Missing block: B:405:0x0924, code:
            if (r29 != false) goto L_0x0876;
     */
        /* JADX WARNING: Missing block: B:406:0x0926, code:
            r5 = android.graphics.BitmapFactory.decodeByteArray(r26, r45, r40, r46);
     */
        /* JADX WARNING: Missing block: B:407:0x0934, code:
            r26 = null;
     */
        /* JADX WARNING: Missing block: B:408:0x0937, code:
            if (r38 == false) goto L_0x0924;
     */
        /* JADX WARNING: Missing block: B:409:0x0939, code:
            org.telegram.messenger.secretmedia.EncryptedFileInputStream.decryptBytesWithKeyFile(r26, 0, r40, r64.cacheImage.encryptionKeyPath);
     */
        /* JADX WARNING: Missing block: B:410:0x0948, code:
            if (r38 == false) goto L_0x0990;
     */
        /* JADX WARNING: Missing block: B:411:0x094a, code:
            r0 = new org.telegram.messenger.secretmedia.EncryptedFileInputStream(r23, r64.cacheImage.encryptionKeyPath);
     */
        /* JADX WARNING: Missing block: B:413:0x0961, code:
            if ((r64.cacheImage.location instanceof org.telegram.tgnet.TLRPC.TL_document) == false) goto L_0x0982;
     */
        /* JADX WARNING: Missing block: B:416:0x0976, code:
            switch(new android.support.media.ExifInterface(r39).getAttributeInt("Orientation", 1)) {
                case 3: goto L_0x099d;
                case 4: goto L_0x0979;
                case 5: goto L_0x0979;
                case 6: goto L_0x099a;
                case 7: goto L_0x0979;
                case 8: goto L_0x09a0;
                default: goto L_0x0979;
            };
     */
        /* JADX WARNING: Missing block: B:420:0x0990, code:
            r0 = new java.io.FileInputStream(r23);
     */
        /* JADX WARNING: Missing block: B:421:0x099a, code:
            r47 = 90;
     */
        /* JADX WARNING: Missing block: B:422:0x099d, code:
            r47 = 180;
     */
        /* JADX WARNING: Missing block: B:423:0x09a0, code:
            r47 = 270;
     */
        /* JADX WARNING: Missing block: B:469:0x0a65, code:
            r6 = null;
     */
        /* JADX WARNING: Missing block: B:470:0x0a68, code:
            if (r5 != null) goto L_0x0a6a;
     */
        /* JADX WARNING: Missing block: B:471:0x0a6a, code:
            r6 = new android.graphics.drawable.BitmapDrawable(r5);
     */
        /* JADX WARNING: Missing block: B:472:0x0a6f, code:
            onPostExecute(r6);
     */
        /* JADX WARNING: Missing block: B:473:0x0a76, code:
            r6 = null;
     */
        /* JADX WARNING: Missing block: B:475:0x0a7b, code:
            r6 = th;
     */
        /* JADX WARNING: Missing block: B:476:0x0a7c, code:
            r53 = r0;
     */
        /* JADX WARNING: Missing block: B:477:0x0a80, code:
            r28 = e;
     */
        /* JADX WARNING: Missing block: B:478:0x0a81, code:
            r53 = r0;
     */
        /* JADX WARNING: Missing block: B:484:?, code:
            return;
     */
        /* JADX WARNING: Missing block: B:485:?, code:
            return;
     */
        /* JADX WARNING: Missing block: B:487:?, code:
            return;
     */
        /* JADX WARNING: Missing block: B:488:?, code:
            return;
     */
        /* JADX WARNING: Missing block: B:489:?, code:
            return;
     */
        /* JADX WARNING: Missing block: B:490:?, code:
            return;
     */
        /* JADX WARNING: Missing block: B:491:?, code:
            return;
     */
        /* JADX WARNING: Missing block: B:492:?, code:
            return;
     */
        public void run() {
            /*
            r64 = this;
            r0 = r64;
            r7 = r0.sync;
            monitor-enter(r7);
            r6 = java.lang.Thread.currentThread();	 Catch:{ all -> 0x0033 }
            r0 = r64;
            r0.runningThread = r6;	 Catch:{ all -> 0x0033 }
            java.lang.Thread.interrupted();	 Catch:{ all -> 0x0033 }
            r0 = r64;
            r6 = r0.isCancelled;	 Catch:{ all -> 0x0033 }
            if (r6 == 0) goto L_0x0018;
        L_0x0016:
            monitor-exit(r7);	 Catch:{ all -> 0x0033 }
        L_0x0017:
            return;
        L_0x0018:
            monitor-exit(r7);	 Catch:{ all -> 0x0033 }
            r0 = r64;
            r6 = r0.cacheImage;
            r6 = r6.location;
            r6 = r6 instanceof org.telegram.tgnet.TLRPC.TL_photoStrippedSize;
            if (r6 == 0) goto L_0x0106;
        L_0x0023:
            r0 = r64;
            r7 = r0.sync;
            monitor-enter(r7);
            r0 = r64;
            r6 = r0.isCancelled;	 Catch:{ all -> 0x0030 }
            if (r6 == 0) goto L_0x0036;
        L_0x002e:
            monitor-exit(r7);	 Catch:{ all -> 0x0030 }
            goto L_0x0017;
        L_0x0030:
            r6 = move-exception;
            monitor-exit(r7);	 Catch:{ all -> 0x0030 }
            throw r6;
        L_0x0033:
            r6 = move-exception;
            monitor-exit(r7);	 Catch:{ all -> 0x0033 }
            throw r6;
        L_0x0036:
            monitor-exit(r7);	 Catch:{ all -> 0x0030 }
            r0 = r64;
            r6 = r0.cacheImage;
            r0 = r6.location;
            r50 = r0;
            r50 = (org.telegram.tgnet.TLRPC.TL_photoStrippedSize) r50;
            r0 = r50;
            r6 = r0.bytes;
            r6 = r6.length;
            r6 = r6 + -3;
            r7 = org.telegram.messenger.Bitmaps.header;
            r7 = r7.length;
            r6 = r6 + r7;
            r7 = org.telegram.messenger.Bitmaps.footer;
            r7 = r7.length;
            r40 = r6 + r7;
            r6 = org.telegram.messenger.ImageLoader.bytes;
            if (r6 == 0) goto L_0x0100;
        L_0x0057:
            r6 = org.telegram.messenger.ImageLoader.bytes;
            r6 = r6.length;
            r0 = r40;
            if (r6 < r0) goto L_0x0100;
        L_0x0060:
            r26 = org.telegram.messenger.ImageLoader.bytes;
        L_0x0064:
            if (r26 != 0) goto L_0x006f;
        L_0x0066:
            r0 = r40;
            r0 = new byte[r0];
            r26 = r0;
            org.telegram.messenger.ImageLoader.bytes = r26;
        L_0x006f:
            r6 = org.telegram.messenger.Bitmaps.header;
            r7 = 0;
            r8 = 0;
            r9 = org.telegram.messenger.Bitmaps.header;
            r9 = r9.length;
            r0 = r26;
            java.lang.System.arraycopy(r6, r7, r0, r8, r9);
            r0 = r50;
            r6 = r0.bytes;
            r7 = 3;
            r8 = org.telegram.messenger.Bitmaps.header;
            r8 = r8.length;
            r0 = r50;
            r9 = r0.bytes;
            r9 = r9.length;
            r9 = r9 + -3;
            r0 = r26;
            java.lang.System.arraycopy(r6, r7, r0, r8, r9);
            r6 = org.telegram.messenger.Bitmaps.footer;
            r7 = 0;
            r8 = org.telegram.messenger.Bitmaps.header;
            r8 = r8.length;
            r0 = r50;
            r9 = r0.bytes;
            r9 = r9.length;
            r8 = r8 + r9;
            r8 = r8 + -3;
            r9 = org.telegram.messenger.Bitmaps.footer;
            r9 = r9.length;
            r0 = r26;
            java.lang.System.arraycopy(r6, r7, r0, r8, r9);
            r6 = 164; // 0xa4 float:2.3E-43 double:8.1E-322;
            r0 = r50;
            r7 = r0.bytes;
            r8 = 1;
            r7 = r7[r8];
            r26[r6] = r7;
            r6 = 166; // 0xa6 float:2.33E-43 double:8.2E-322;
            r0 = r50;
            r7 = r0.bytes;
            r8 = 2;
            r7 = r7[r8];
            r26[r6] = r7;
            r6 = 0;
            r0 = r26;
            r1 = r40;
            r4 = android.graphics.BitmapFactory.decodeByteArray(r0, r6, r1);
            if (r4 == 0) goto L_0x00f2;
        L_0x00c6:
            r0 = r64;
            r6 = r0.cacheImage;
            r6 = r6.filter;
            r6 = android.text.TextUtils.isEmpty(r6);
            if (r6 != 0) goto L_0x00f2;
        L_0x00d2:
            r0 = r64;
            r6 = r0.cacheImage;
            r6 = r6.filter;
            r7 = "b";
            r6 = r6.contains(r7);
            if (r6 == 0) goto L_0x00f2;
        L_0x00e1:
            r5 = 3;
            r6 = 1;
            r7 = r4.getWidth();
            r8 = r4.getHeight();
            r9 = r4.getRowBytes();
            org.telegram.messenger.Utilities.blurBitmap(r4, r5, r6, r7, r8, r9);
        L_0x00f2:
            if (r4 == 0) goto L_0x0104;
        L_0x00f4:
            r6 = new android.graphics.drawable.BitmapDrawable;
            r6.<init>(r4);
        L_0x00f9:
            r0 = r64;
            r0.onPostExecute(r6);
            goto L_0x0017;
        L_0x0100:
            r26 = 0;
            goto L_0x0064;
        L_0x0104:
            r6 = 0;
            goto L_0x00f9;
        L_0x0106:
            r0 = r64;
            r6 = r0.cacheImage;
            r6 = r6.animatedFile;
            if (r6 == 0) goto L_0x0153;
        L_0x010e:
            r0 = r64;
            r7 = r0.sync;
            monitor-enter(r7);
            r0 = r64;
            r6 = r0.isCancelled;	 Catch:{ all -> 0x011c }
            if (r6 == 0) goto L_0x011f;
        L_0x0119:
            monitor-exit(r7);	 Catch:{ all -> 0x011c }
            goto L_0x0017;
        L_0x011c:
            r6 = move-exception;
            monitor-exit(r7);	 Catch:{ all -> 0x011c }
            throw r6;
        L_0x011f:
            monitor-exit(r7);	 Catch:{ all -> 0x011c }
            r33 = new org.telegram.ui.Components.AnimatedFileDrawable;
            r0 = r64;
            r6 = r0.cacheImage;
            r7 = r6.finalFilePath;
            r0 = r64;
            r6 = r0.cacheImage;
            r6 = r6.filter;
            if (r6 == 0) goto L_0x0151;
        L_0x0130:
            r0 = r64;
            r6 = r0.cacheImage;
            r6 = r6.filter;
            r8 = "d";
            r6 = r6.equals(r8);
            if (r6 == 0) goto L_0x0151;
        L_0x013f:
            r6 = 1;
        L_0x0140:
            r0 = r33;
            r0.<init>(r7, r6);
            java.lang.Thread.interrupted();
            r0 = r64;
            r1 = r33;
            r0.onPostExecute(r1);
            goto L_0x0017;
        L_0x0151:
            r6 = 0;
            goto L_0x0140;
        L_0x0153:
            r41 = 0;
            r42 = 0;
            r5 = 0;
            r44 = 0;
            r47 = 0;
            r0 = r64;
            r6 = r0.cacheImage;
            r0 = r6.finalFilePath;
            r23 = r0;
            r0 = r64;
            r6 = r0.cacheImage;
            r6 = r6.secureDocument;
            if (r6 != 0) goto L_0x0183;
        L_0x016c:
            r0 = r64;
            r6 = r0.cacheImage;
            r6 = r6.encryptionKeyPath;
            if (r6 == 0) goto L_0x0286;
        L_0x0174:
            if (r23 == 0) goto L_0x0286;
        L_0x0176:
            r6 = r23.getAbsolutePath();
            r7 = ".enc";
            r6 = r6.endsWith(r7);
            if (r6 == 0) goto L_0x0286;
        L_0x0183:
            r38 = 1;
        L_0x0185:
            r0 = r64;
            r6 = r0.cacheImage;
            r6 = r6.secureDocument;
            if (r6 == 0) goto L_0x0296;
        L_0x018d:
            r0 = r64;
            r6 = r0.cacheImage;
            r6 = r6.secureDocument;
            r0 = r6.secureDocumentKey;
            r59 = r0;
            r0 = r64;
            r6 = r0.cacheImage;
            r6 = r6.secureDocument;
            r6 = r6.secureFile;
            if (r6 == 0) goto L_0x028a;
        L_0x01a1:
            r0 = r64;
            r6 = r0.cacheImage;
            r6 = r6.secureDocument;
            r6 = r6.secureFile;
            r6 = r6.file_hash;
            if (r6 == 0) goto L_0x028a;
        L_0x01ad:
            r0 = r64;
            r6 = r0.cacheImage;
            r6 = r6.secureDocument;
            r6 = r6.secureFile;
            r0 = r6.file_hash;
            r58 = r0;
        L_0x01b9:
            r24 = 1;
            r61 = 0;
            r6 = android.os.Build.VERSION.SDK_INT;
            r7 = 19;
            if (r6 >= r7) goto L_0x0217;
        L_0x01c3:
            r53 = 0;
            r54 = new java.io.RandomAccessFile;	 Catch:{ Exception -> 0x02a8 }
            r6 = "r";
            r0 = r54;
            r1 = r23;
            r0.<init>(r1, r6);	 Catch:{ Exception -> 0x02a8 }
            r0 = r64;
            r6 = r0.cacheImage;	 Catch:{ Exception -> 0x0a80, all -> 0x0a7b }
            r6 = r6.selfThumb;	 Catch:{ Exception -> 0x0a80, all -> 0x0a7b }
            if (r6 == 0) goto L_0x029c;
        L_0x01d9:
            r22 = org.telegram.messenger.ImageLoader.headerThumb;	 Catch:{ Exception -> 0x0a80, all -> 0x0a7b }
        L_0x01dd:
            r6 = 0;
            r0 = r22;
            r7 = r0.length;	 Catch:{ Exception -> 0x0a80, all -> 0x0a7b }
            r0 = r54;
            r1 = r22;
            r0.readFully(r1, r6, r7);	 Catch:{ Exception -> 0x0a80, all -> 0x0a7b }
            r6 = new java.lang.String;	 Catch:{ Exception -> 0x0a80, all -> 0x0a7b }
            r0 = r22;
            r6.<init>(r0);	 Catch:{ Exception -> 0x0a80, all -> 0x0a7b }
            r60 = r6.toLowerCase();	 Catch:{ Exception -> 0x0a80, all -> 0x0a7b }
            r60 = r60.toLowerCase();	 Catch:{ Exception -> 0x0a80, all -> 0x0a7b }
            r6 = "riff";
            r0 = r60;
            r6 = r0.startsWith(r6);	 Catch:{ Exception -> 0x0a80, all -> 0x0a7b }
            if (r6 == 0) goto L_0x020f;
        L_0x0202:
            r6 = "webp";
            r0 = r60;
            r6 = r0.endsWith(r6);	 Catch:{ Exception -> 0x0a80, all -> 0x0a7b }
            if (r6 == 0) goto L_0x020f;
        L_0x020d:
            r61 = 1;
        L_0x020f:
            r54.close();	 Catch:{ Exception -> 0x0a80, all -> 0x0a7b }
            if (r54 == 0) goto L_0x0217;
        L_0x0214:
            r54.close();	 Catch:{ Exception -> 0x02a2 }
        L_0x0217:
            r0 = r64;
            r6 = r0.cacheImage;
            r6 = r6.selfThumb;
            if (r6 == 0) goto L_0x050f;
        L_0x021f:
            r18 = 0;
            r25 = 0;
            r0 = r64;
            r6 = r0.cacheImage;
            r6 = r6.filter;
            if (r6 == 0) goto L_0x024d;
        L_0x022b:
            r0 = r64;
            r6 = r0.cacheImage;
            r6 = r6.filter;
            r7 = "b2";
            r6 = r6.contains(r7);
            if (r6 == 0) goto L_0x02c5;
        L_0x023a:
            r18 = 3;
        L_0x023c:
            r0 = r64;
            r6 = r0.cacheImage;
            r6 = r6.filter;
            r7 = "i";
            r6 = r6.contains(r7);
            if (r6 == 0) goto L_0x024d;
        L_0x024b:
            r25 = 1;
        L_0x024d:
            r0 = r64;
            r6 = org.telegram.messenger.ImageLoader.this;	 Catch:{ Throwable -> 0x0269 }
            r8 = java.lang.System.currentTimeMillis();	 Catch:{ Throwable -> 0x0269 }
            r6.lastCacheOutTime = r8;	 Catch:{ Throwable -> 0x0269 }
            r0 = r64;
            r7 = r0.sync;	 Catch:{ Throwable -> 0x0269 }
            monitor-enter(r7);	 Catch:{ Throwable -> 0x0269 }
            r0 = r64;
            r6 = r0.isCancelled;	 Catch:{ all -> 0x0266 }
            if (r6 == 0) goto L_0x02eb;
        L_0x0263:
            monitor-exit(r7);	 Catch:{ all -> 0x0266 }
            goto L_0x0017;
        L_0x0266:
            r6 = move-exception;
            monitor-exit(r7);	 Catch:{ all -> 0x0266 }
            throw r6;	 Catch:{ Throwable -> 0x0269 }
        L_0x0269:
            r28 = move-exception;
            org.telegram.messenger.FileLog.e(r28);
        L_0x026d:
            java.lang.Thread.interrupted();
            if (r44 != 0) goto L_0x0274;
        L_0x0272:
            if (r47 == 0) goto L_0x0a68;
        L_0x0274:
            if (r5 == 0) goto L_0x0a65;
        L_0x0276:
            r6 = new org.telegram.messenger.ExtendedBitmapDrawable;
            r0 = r44;
            r1 = r47;
            r6.<init>(r5, r0, r1);
        L_0x027f:
            r0 = r64;
            r0.onPostExecute(r6);
            goto L_0x0017;
        L_0x0286:
            r38 = 0;
            goto L_0x0185;
        L_0x028a:
            r0 = r64;
            r6 = r0.cacheImage;
            r6 = r6.secureDocument;
            r0 = r6.fileHash;
            r58 = r0;
            goto L_0x01b9;
        L_0x0296:
            r59 = 0;
            r58 = 0;
            goto L_0x01b9;
        L_0x029c:
            r22 = org.telegram.messenger.ImageLoader.header;	 Catch:{ Exception -> 0x0a80, all -> 0x0a7b }
            goto L_0x01dd;
        L_0x02a2:
            r28 = move-exception;
            org.telegram.messenger.FileLog.e(r28);
            goto L_0x0217;
        L_0x02a8:
            r28 = move-exception;
        L_0x02a9:
            org.telegram.messenger.FileLog.e(r28);	 Catch:{ all -> 0x02b9 }
            if (r53 == 0) goto L_0x0217;
        L_0x02ae:
            r53.close();	 Catch:{ Exception -> 0x02b3 }
            goto L_0x0217;
        L_0x02b3:
            r28 = move-exception;
            org.telegram.messenger.FileLog.e(r28);
            goto L_0x0217;
        L_0x02b9:
            r6 = move-exception;
        L_0x02ba:
            if (r53 == 0) goto L_0x02bf;
        L_0x02bc:
            r53.close();	 Catch:{ Exception -> 0x02c0 }
        L_0x02bf:
            throw r6;
        L_0x02c0:
            r28 = move-exception;
            org.telegram.messenger.FileLog.e(r28);
            goto L_0x02bf;
        L_0x02c5:
            r0 = r64;
            r6 = r0.cacheImage;
            r6 = r6.filter;
            r7 = "b1";
            r6 = r6.contains(r7);
            if (r6 == 0) goto L_0x02d8;
        L_0x02d4:
            r18 = 2;
            goto L_0x023c;
        L_0x02d8:
            r0 = r64;
            r6 = r0.cacheImage;
            r6 = r6.filter;
            r7 = "b";
            r6 = r6.contains(r7);
            if (r6 == 0) goto L_0x023c;
        L_0x02e7:
            r18 = 1;
            goto L_0x023c;
        L_0x02eb:
            monitor-exit(r7);	 Catch:{ all -> 0x0266 }
            r46 = new android.graphics.BitmapFactory$Options;	 Catch:{ Throwable -> 0x0269 }
            r46.<init>();	 Catch:{ Throwable -> 0x0269 }
            r6 = 1;
            r0 = r46;
            r0.inSampleSize = r6;	 Catch:{ Throwable -> 0x0269 }
            r6 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Throwable -> 0x0269 }
            r7 = 21;
            if (r6 >= r7) goto L_0x0301;
        L_0x02fc:
            r6 = 1;
            r0 = r46;
            r0.inPurgeable = r6;	 Catch:{ Throwable -> 0x0269 }
        L_0x0301:
            if (r61 == 0) goto L_0x0373;
        L_0x0303:
            r32 = new java.io.RandomAccessFile;	 Catch:{ Throwable -> 0x0269 }
            r6 = "r";
            r0 = r32;
            r1 = r23;
            r0.<init>(r1, r6);	 Catch:{ Throwable -> 0x0269 }
            r6 = r32.getChannel();	 Catch:{ Throwable -> 0x0269 }
            r7 = java.nio.channels.FileChannel.MapMode.READ_ONLY;	 Catch:{ Throwable -> 0x0269 }
            r8 = 0;
            r10 = r23.length();	 Catch:{ Throwable -> 0x0269 }
            r21 = r6.map(r7, r8, r10);	 Catch:{ Throwable -> 0x0269 }
            r20 = new android.graphics.BitmapFactory$Options;	 Catch:{ Throwable -> 0x0269 }
            r20.<init>();	 Catch:{ Throwable -> 0x0269 }
            r6 = 1;
            r0 = r20;
            r0.inJustDecodeBounds = r6;	 Catch:{ Throwable -> 0x0269 }
            r6 = 0;
            r7 = r21.limit();	 Catch:{ Throwable -> 0x0269 }
            r8 = 1;
            r0 = r21;
            r1 = r20;
            org.telegram.messenger.Utilities.loadWebpImage(r6, r0, r7, r1, r8);	 Catch:{ Throwable -> 0x0269 }
            r0 = r20;
            r6 = r0.outWidth;	 Catch:{ Throwable -> 0x0269 }
            r0 = r20;
            r7 = r0.outHeight;	 Catch:{ Throwable -> 0x0269 }
            r8 = android.graphics.Bitmap.Config.ARGB_8888;	 Catch:{ Throwable -> 0x0269 }
            r5 = org.telegram.messenger.Bitmaps.createBitmap(r6, r7, r8);	 Catch:{ Throwable -> 0x0269 }
            r7 = r21.limit();	 Catch:{ Throwable -> 0x0269 }
            r8 = 0;
            r0 = r46;
            r6 = r0.inPurgeable;	 Catch:{ Throwable -> 0x0269 }
            if (r6 != 0) goto L_0x0371;
        L_0x034f:
            r6 = 1;
        L_0x0350:
            r0 = r21;
            org.telegram.messenger.Utilities.loadWebpImage(r5, r0, r7, r8, r6);	 Catch:{ Throwable -> 0x0269 }
            r32.close();	 Catch:{ Throwable -> 0x0269 }
        L_0x0358:
            if (r5 != 0) goto L_0x0436;
        L_0x035a:
            r6 = r23.length();	 Catch:{ Throwable -> 0x0269 }
            r8 = 0;
            r6 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1));
            if (r6 == 0) goto L_0x036c;
        L_0x0364:
            r0 = r64;
            r6 = r0.cacheImage;	 Catch:{ Throwable -> 0x0269 }
            r6 = r6.filter;	 Catch:{ Throwable -> 0x0269 }
            if (r6 != 0) goto L_0x026d;
        L_0x036c:
            r23.delete();	 Catch:{ Throwable -> 0x0269 }
            goto L_0x026d;
        L_0x0371:
            r6 = 0;
            goto L_0x0350;
        L_0x0373:
            r0 = r46;
            r6 = r0.inPurgeable;	 Catch:{ Throwable -> 0x0269 }
            if (r6 != 0) goto L_0x037b;
        L_0x0379:
            if (r59 == 0) goto L_0x040d;
        L_0x037b:
            r31 = new java.io.RandomAccessFile;	 Catch:{ Throwable -> 0x0269 }
            r6 = "r";
            r0 = r31;
            r1 = r23;
            r0.<init>(r1, r6);	 Catch:{ Throwable -> 0x0269 }
            r6 = r31.length();	 Catch:{ Throwable -> 0x0269 }
            r0 = (int) r6;	 Catch:{ Throwable -> 0x0269 }
            r40 = r0;
            r45 = 0;
            r6 = org.telegram.messenger.ImageLoader.bytesThumb;	 Catch:{ Throwable -> 0x0269 }
            if (r6 == 0) goto L_0x03f9;
        L_0x0396:
            r6 = org.telegram.messenger.ImageLoader.bytesThumb;	 Catch:{ Throwable -> 0x0269 }
            r6 = r6.length;	 Catch:{ Throwable -> 0x0269 }
            r0 = r40;
            if (r6 < r0) goto L_0x03f9;
        L_0x039f:
            r26 = org.telegram.messenger.ImageLoader.bytesThumb;	 Catch:{ Throwable -> 0x0269 }
        L_0x03a3:
            if (r26 != 0) goto L_0x03ae;
        L_0x03a5:
            r0 = r40;
            r0 = new byte[r0];	 Catch:{ Throwable -> 0x0269 }
            r26 = r0;
            org.telegram.messenger.ImageLoader.bytesThumb = r26;	 Catch:{ Throwable -> 0x0269 }
        L_0x03ae:
            r6 = 0;
            r0 = r31;
            r1 = r26;
            r2 = r40;
            r0.readFully(r1, r6, r2);	 Catch:{ Throwable -> 0x0269 }
            r31.close();	 Catch:{ Throwable -> 0x0269 }
            r29 = 0;
            if (r59 == 0) goto L_0x03fc;
        L_0x03bf:
            r6 = 0;
            r0 = r26;
            r1 = r40;
            r2 = r59;
            org.telegram.messenger.secretmedia.EncryptedFileInputStream.decryptBytesWithKeyFile(r0, r6, r1, r2);	 Catch:{ Throwable -> 0x0269 }
            r6 = 0;
            r0 = r26;
            r1 = r40;
            r36 = org.telegram.messenger.Utilities.computeSHA256(r0, r6, r1);	 Catch:{ Throwable -> 0x0269 }
            if (r58 == 0) goto L_0x03de;
        L_0x03d4:
            r0 = r36;
            r1 = r58;
            r6 = java.util.Arrays.equals(r0, r1);	 Catch:{ Throwable -> 0x0269 }
            if (r6 != 0) goto L_0x03e0;
        L_0x03de:
            r29 = 1;
        L_0x03e0:
            r6 = 0;
            r6 = r26[r6];	 Catch:{ Throwable -> 0x0269 }
            r0 = r6 & 255;
            r45 = r0;
            r40 = r40 - r45;
        L_0x03e9:
            if (r29 != 0) goto L_0x0358;
        L_0x03eb:
            r0 = r26;
            r1 = r45;
            r2 = r40;
            r3 = r46;
            r5 = android.graphics.BitmapFactory.decodeByteArray(r0, r1, r2, r3);	 Catch:{ Throwable -> 0x0269 }
            goto L_0x0358;
        L_0x03f9:
            r26 = 0;
            goto L_0x03a3;
        L_0x03fc:
            if (r38 == 0) goto L_0x03e9;
        L_0x03fe:
            r6 = 0;
            r0 = r64;
            r7 = r0.cacheImage;	 Catch:{ Throwable -> 0x0269 }
            r7 = r7.encryptionKeyPath;	 Catch:{ Throwable -> 0x0269 }
            r0 = r26;
            r1 = r40;
            org.telegram.messenger.secretmedia.EncryptedFileInputStream.decryptBytesWithKeyFile(r0, r6, r1, r7);	 Catch:{ Throwable -> 0x0269 }
            goto L_0x03e9;
        L_0x040d:
            if (r38 == 0) goto L_0x042c;
        L_0x040f:
            r39 = new org.telegram.messenger.secretmedia.EncryptedFileInputStream;	 Catch:{ Throwable -> 0x0269 }
            r0 = r64;
            r6 = r0.cacheImage;	 Catch:{ Throwable -> 0x0269 }
            r6 = r6.encryptionKeyPath;	 Catch:{ Throwable -> 0x0269 }
            r0 = r39;
            r1 = r23;
            r0.<init>(r1, r6);	 Catch:{ Throwable -> 0x0269 }
        L_0x041e:
            r6 = 0;
            r0 = r39;
            r1 = r46;
            r5 = android.graphics.BitmapFactory.decodeStream(r0, r6, r1);	 Catch:{ Throwable -> 0x0269 }
            r39.close();	 Catch:{ Throwable -> 0x0269 }
            goto L_0x0358;
        L_0x042c:
            r39 = new java.io.FileInputStream;	 Catch:{ Throwable -> 0x0269 }
            r0 = r39;
            r1 = r23;
            r0.<init>(r1);	 Catch:{ Throwable -> 0x0269 }
            goto L_0x041e;
        L_0x0436:
            if (r25 == 0) goto L_0x0453;
        L_0x0438:
            r0 = r46;
            r6 = r0.inPurgeable;	 Catch:{ Throwable -> 0x0269 }
            if (r6 == 0) goto L_0x0479;
        L_0x043e:
            r6 = 0;
        L_0x043f:
            r7 = r5.getWidth();	 Catch:{ Throwable -> 0x0269 }
            r8 = r5.getHeight();	 Catch:{ Throwable -> 0x0269 }
            r9 = r5.getRowBytes();	 Catch:{ Throwable -> 0x0269 }
            r6 = org.telegram.messenger.Utilities.needInvert(r5, r6, r7, r8, r9);	 Catch:{ Throwable -> 0x0269 }
            if (r6 == 0) goto L_0x047b;
        L_0x0451:
            r44 = 1;
        L_0x0453:
            r6 = 1;
            r0 = r18;
            if (r0 != r6) goto L_0x0480;
        L_0x0458:
            r6 = r5.getConfig();	 Catch:{ Throwable -> 0x0269 }
            r7 = android.graphics.Bitmap.Config.ARGB_8888;	 Catch:{ Throwable -> 0x0269 }
            if (r6 != r7) goto L_0x026d;
        L_0x0460:
            r6 = 3;
            r0 = r46;
            r7 = r0.inPurgeable;	 Catch:{ Throwable -> 0x0269 }
            if (r7 == 0) goto L_0x047e;
        L_0x0467:
            r7 = 0;
        L_0x0468:
            r8 = r5.getWidth();	 Catch:{ Throwable -> 0x0269 }
            r9 = r5.getHeight();	 Catch:{ Throwable -> 0x0269 }
            r10 = r5.getRowBytes();	 Catch:{ Throwable -> 0x0269 }
            org.telegram.messenger.Utilities.blurBitmap(r5, r6, r7, r8, r9, r10);	 Catch:{ Throwable -> 0x0269 }
            goto L_0x026d;
        L_0x0479:
            r6 = 1;
            goto L_0x043f;
        L_0x047b:
            r44 = 0;
            goto L_0x0453;
        L_0x047e:
            r7 = 1;
            goto L_0x0468;
        L_0x0480:
            r6 = 2;
            r0 = r18;
            if (r0 != r6) goto L_0x04a8;
        L_0x0485:
            r6 = r5.getConfig();	 Catch:{ Throwable -> 0x0269 }
            r7 = android.graphics.Bitmap.Config.ARGB_8888;	 Catch:{ Throwable -> 0x0269 }
            if (r6 != r7) goto L_0x026d;
        L_0x048d:
            r6 = 1;
            r0 = r46;
            r7 = r0.inPurgeable;	 Catch:{ Throwable -> 0x0269 }
            if (r7 == 0) goto L_0x04a6;
        L_0x0494:
            r7 = 0;
        L_0x0495:
            r8 = r5.getWidth();	 Catch:{ Throwable -> 0x0269 }
            r9 = r5.getHeight();	 Catch:{ Throwable -> 0x0269 }
            r10 = r5.getRowBytes();	 Catch:{ Throwable -> 0x0269 }
            org.telegram.messenger.Utilities.blurBitmap(r5, r6, r7, r8, r9, r10);	 Catch:{ Throwable -> 0x0269 }
            goto L_0x026d;
        L_0x04a6:
            r7 = 1;
            goto L_0x0495;
        L_0x04a8:
            r6 = 3;
            r0 = r18;
            if (r0 != r6) goto L_0x0502;
        L_0x04ad:
            r6 = r5.getConfig();	 Catch:{ Throwable -> 0x0269 }
            r7 = android.graphics.Bitmap.Config.ARGB_8888;	 Catch:{ Throwable -> 0x0269 }
            if (r6 != r7) goto L_0x026d;
        L_0x04b5:
            r6 = 7;
            r0 = r46;
            r7 = r0.inPurgeable;	 Catch:{ Throwable -> 0x0269 }
            if (r7 == 0) goto L_0x04fc;
        L_0x04bc:
            r7 = 0;
        L_0x04bd:
            r8 = r5.getWidth();	 Catch:{ Throwable -> 0x0269 }
            r9 = r5.getHeight();	 Catch:{ Throwable -> 0x0269 }
            r10 = r5.getRowBytes();	 Catch:{ Throwable -> 0x0269 }
            org.telegram.messenger.Utilities.blurBitmap(r5, r6, r7, r8, r9, r10);	 Catch:{ Throwable -> 0x0269 }
            r6 = 7;
            r0 = r46;
            r7 = r0.inPurgeable;	 Catch:{ Throwable -> 0x0269 }
            if (r7 == 0) goto L_0x04fe;
        L_0x04d3:
            r7 = 0;
        L_0x04d4:
            r8 = r5.getWidth();	 Catch:{ Throwable -> 0x0269 }
            r9 = r5.getHeight();	 Catch:{ Throwable -> 0x0269 }
            r10 = r5.getRowBytes();	 Catch:{ Throwable -> 0x0269 }
            org.telegram.messenger.Utilities.blurBitmap(r5, r6, r7, r8, r9, r10);	 Catch:{ Throwable -> 0x0269 }
            r6 = 7;
            r0 = r46;
            r7 = r0.inPurgeable;	 Catch:{ Throwable -> 0x0269 }
            if (r7 == 0) goto L_0x0500;
        L_0x04ea:
            r7 = 0;
        L_0x04eb:
            r8 = r5.getWidth();	 Catch:{ Throwable -> 0x0269 }
            r9 = r5.getHeight();	 Catch:{ Throwable -> 0x0269 }
            r10 = r5.getRowBytes();	 Catch:{ Throwable -> 0x0269 }
            org.telegram.messenger.Utilities.blurBitmap(r5, r6, r7, r8, r9, r10);	 Catch:{ Throwable -> 0x0269 }
            goto L_0x026d;
        L_0x04fc:
            r7 = 1;
            goto L_0x04bd;
        L_0x04fe:
            r7 = 1;
            goto L_0x04d4;
        L_0x0500:
            r7 = 1;
            goto L_0x04eb;
        L_0x0502:
            if (r18 != 0) goto L_0x026d;
        L_0x0504:
            r0 = r46;
            r6 = r0.inPurgeable;	 Catch:{ Throwable -> 0x0269 }
            if (r6 == 0) goto L_0x026d;
        L_0x050a:
            org.telegram.messenger.Utilities.pinBitmap(r5);	 Catch:{ Throwable -> 0x0269 }
            goto L_0x026d;
        L_0x050f:
            r43 = 0;
            r0 = r64;
            r6 = r0.cacheImage;	 Catch:{ Throwable -> 0x05b1 }
            r6 = r6.httpUrl;	 Catch:{ Throwable -> 0x05b1 }
            if (r6 == 0) goto L_0x055f;
        L_0x0519:
            r0 = r64;
            r6 = r0.cacheImage;	 Catch:{ Throwable -> 0x05b1 }
            r6 = r6.httpUrl;	 Catch:{ Throwable -> 0x05b1 }
            r7 = "thumb://";
            r6 = r6.startsWith(r7);	 Catch:{ Throwable -> 0x05b1 }
            if (r6 == 0) goto L_0x05b4;
        L_0x0528:
            r0 = r64;
            r6 = r0.cacheImage;	 Catch:{ Throwable -> 0x05b1 }
            r6 = r6.httpUrl;	 Catch:{ Throwable -> 0x05b1 }
            r7 = ":";
            r8 = 8;
            r37 = r6.indexOf(r7, r8);	 Catch:{ Throwable -> 0x05b1 }
            if (r37 < 0) goto L_0x055d;
        L_0x0539:
            r0 = r64;
            r6 = r0.cacheImage;	 Catch:{ Throwable -> 0x05b1 }
            r6 = r6.httpUrl;	 Catch:{ Throwable -> 0x05b1 }
            r7 = 8;
            r0 = r37;
            r6 = r6.substring(r7, r0);	 Catch:{ Throwable -> 0x05b1 }
            r6 = java.lang.Long.parseLong(r6);	 Catch:{ Throwable -> 0x05b1 }
            r41 = java.lang.Long.valueOf(r6);	 Catch:{ Throwable -> 0x05b1 }
            r42 = 0;
            r0 = r64;
            r6 = r0.cacheImage;	 Catch:{ Throwable -> 0x05b1 }
            r6 = r6.httpUrl;	 Catch:{ Throwable -> 0x05b1 }
            r7 = r37 + 1;
            r43 = r6.substring(r7);	 Catch:{ Throwable -> 0x05b1 }
        L_0x055d:
            r24 = 0;
        L_0x055f:
            r27 = 20;
            if (r41 == 0) goto L_0x0565;
        L_0x0563:
            r27 = 0;
        L_0x0565:
            if (r27 == 0) goto L_0x0595;
        L_0x0567:
            r0 = r64;
            r6 = org.telegram.messenger.ImageLoader.this;	 Catch:{ Throwable -> 0x05b1 }
            r6 = r6.lastCacheOutTime;	 Catch:{ Throwable -> 0x05b1 }
            r8 = 0;
            r6 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1));
            if (r6 == 0) goto L_0x0595;
        L_0x0575:
            r0 = r64;
            r6 = org.telegram.messenger.ImageLoader.this;	 Catch:{ Throwable -> 0x05b1 }
            r6 = r6.lastCacheOutTime;	 Catch:{ Throwable -> 0x05b1 }
            r8 = java.lang.System.currentTimeMillis();	 Catch:{ Throwable -> 0x05b1 }
            r0 = r27;
            r10 = (long) r0;	 Catch:{ Throwable -> 0x05b1 }
            r8 = r8 - r10;
            r6 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1));
            if (r6 <= 0) goto L_0x0595;
        L_0x0589:
            r6 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Throwable -> 0x05b1 }
            r7 = 21;
            if (r6 >= r7) goto L_0x0595;
        L_0x058f:
            r0 = r27;
            r6 = (long) r0;	 Catch:{ Throwable -> 0x05b1 }
            java.lang.Thread.sleep(r6);	 Catch:{ Throwable -> 0x05b1 }
        L_0x0595:
            r0 = r64;
            r6 = org.telegram.messenger.ImageLoader.this;	 Catch:{ Throwable -> 0x05b1 }
            r8 = java.lang.System.currentTimeMillis();	 Catch:{ Throwable -> 0x05b1 }
            r6.lastCacheOutTime = r8;	 Catch:{ Throwable -> 0x05b1 }
            r0 = r64;
            r7 = r0.sync;	 Catch:{ Throwable -> 0x05b1 }
            monitor-enter(r7);	 Catch:{ Throwable -> 0x05b1 }
            r0 = r64;
            r6 = r0.isCancelled;	 Catch:{ all -> 0x05ae }
            if (r6 == 0) goto L_0x0603;
        L_0x05ab:
            monitor-exit(r7);	 Catch:{ all -> 0x05ae }
            goto L_0x0017;
        L_0x05ae:
            r6 = move-exception;
            monitor-exit(r7);	 Catch:{ all -> 0x05ae }
            throw r6;	 Catch:{ Throwable -> 0x05b1 }
        L_0x05b1:
            r6 = move-exception;
            goto L_0x026d;
        L_0x05b4:
            r0 = r64;
            r6 = r0.cacheImage;	 Catch:{ Throwable -> 0x05b1 }
            r6 = r6.httpUrl;	 Catch:{ Throwable -> 0x05b1 }
            r7 = "vthumb://";
            r6 = r6.startsWith(r7);	 Catch:{ Throwable -> 0x05b1 }
            if (r6 == 0) goto L_0x05f0;
        L_0x05c3:
            r0 = r64;
            r6 = r0.cacheImage;	 Catch:{ Throwable -> 0x05b1 }
            r6 = r6.httpUrl;	 Catch:{ Throwable -> 0x05b1 }
            r7 = ":";
            r8 = 9;
            r37 = r6.indexOf(r7, r8);	 Catch:{ Throwable -> 0x05b1 }
            if (r37 < 0) goto L_0x05ec;
        L_0x05d4:
            r0 = r64;
            r6 = r0.cacheImage;	 Catch:{ Throwable -> 0x05b1 }
            r6 = r6.httpUrl;	 Catch:{ Throwable -> 0x05b1 }
            r7 = 9;
            r0 = r37;
            r6 = r6.substring(r7, r0);	 Catch:{ Throwable -> 0x05b1 }
            r6 = java.lang.Long.parseLong(r6);	 Catch:{ Throwable -> 0x05b1 }
            r41 = java.lang.Long.valueOf(r6);	 Catch:{ Throwable -> 0x05b1 }
            r42 = 1;
        L_0x05ec:
            r24 = 0;
            goto L_0x055f;
        L_0x05f0:
            r0 = r64;
            r6 = r0.cacheImage;	 Catch:{ Throwable -> 0x05b1 }
            r6 = r6.httpUrl;	 Catch:{ Throwable -> 0x05b1 }
            r7 = "http";
            r6 = r6.startsWith(r7);	 Catch:{ Throwable -> 0x05b1 }
            if (r6 != 0) goto L_0x055f;
        L_0x05ff:
            r24 = 0;
            goto L_0x055f;
        L_0x0603:
            monitor-exit(r7);	 Catch:{ all -> 0x05ae }
            r46 = new android.graphics.BitmapFactory$Options;	 Catch:{ Throwable -> 0x05b1 }
            r46.<init>();	 Catch:{ Throwable -> 0x05b1 }
            r6 = 1;
            r0 = r46;
            r0.inSampleSize = r6;	 Catch:{ Throwable -> 0x05b1 }
            r63 = 0;
            r35 = 0;
            r17 = 0;
            r25 = 0;
            r0 = r64;
            r6 = r0.cacheImage;	 Catch:{ Throwable -> 0x05b1 }
            r6 = r6.filter;	 Catch:{ Throwable -> 0x05b1 }
            if (r6 == 0) goto L_0x0780;
        L_0x061e:
            r0 = r64;
            r6 = r0.cacheImage;	 Catch:{ Throwable -> 0x05b1 }
            r6 = r6.filter;	 Catch:{ Throwable -> 0x05b1 }
            r7 = "_";
            r12 = r6.split(r7);	 Catch:{ Throwable -> 0x05b1 }
            r6 = r12.length;	 Catch:{ Throwable -> 0x05b1 }
            r7 = 2;
            if (r6 < r7) goto L_0x0645;
        L_0x062f:
            r6 = 0;
            r6 = r12[r6];	 Catch:{ Throwable -> 0x05b1 }
            r6 = java.lang.Float.parseFloat(r6);	 Catch:{ Throwable -> 0x05b1 }
            r7 = org.telegram.messenger.AndroidUtilities.density;	 Catch:{ Throwable -> 0x05b1 }
            r63 = r6 * r7;
            r6 = 1;
            r6 = r12[r6];	 Catch:{ Throwable -> 0x05b1 }
            r6 = java.lang.Float.parseFloat(r6);	 Catch:{ Throwable -> 0x05b1 }
            r7 = org.telegram.messenger.AndroidUtilities.density;	 Catch:{ Throwable -> 0x05b1 }
            r35 = r6 * r7;
        L_0x0645:
            r0 = r64;
            r6 = r0.cacheImage;	 Catch:{ Throwable -> 0x05b1 }
            r6 = r6.filter;	 Catch:{ Throwable -> 0x05b1 }
            r7 = "b";
            r6 = r6.contains(r7);	 Catch:{ Throwable -> 0x05b1 }
            if (r6 == 0) goto L_0x0656;
        L_0x0654:
            r17 = 1;
        L_0x0656:
            r0 = r64;
            r6 = r0.cacheImage;	 Catch:{ Throwable -> 0x05b1 }
            r6 = r6.filter;	 Catch:{ Throwable -> 0x05b1 }
            r7 = "i";
            r6 = r6.contains(r7);	 Catch:{ Throwable -> 0x05b1 }
            if (r6 == 0) goto L_0x0667;
        L_0x0665:
            r25 = 1;
        L_0x0667:
            r6 = 0;
            r6 = (r63 > r6 ? 1 : (r63 == r6 ? 0 : -1));
            if (r6 == 0) goto L_0x06b6;
        L_0x066c:
            r6 = 0;
            r6 = (r35 > r6 ? 1 : (r35 == r6 ? 0 : -1));
            if (r6 == 0) goto L_0x06b6;
        L_0x0671:
            r6 = 1;
            r0 = r46;
            r0.inJustDecodeBounds = r6;	 Catch:{ Throwable -> 0x05b1 }
            if (r41 == 0) goto L_0x06d8;
        L_0x0678:
            if (r43 != 0) goto L_0x06d8;
        L_0x067a:
            if (r42 == 0) goto L_0x06c7;
        L_0x067c:
            r6 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Throwable -> 0x05b1 }
            r6 = r6.getContentResolver();	 Catch:{ Throwable -> 0x05b1 }
            r8 = r41.longValue();	 Catch:{ Throwable -> 0x05b1 }
            r7 = 1;
            r0 = r46;
            android.provider.MediaStore.Video.Thumbnails.getThumbnail(r6, r8, r7, r0);	 Catch:{ Throwable -> 0x05b1 }
        L_0x068c:
            r0 = r46;
            r6 = r0.outWidth;	 Catch:{ Throwable -> 0x05b1 }
            r0 = (float) r6;	 Catch:{ Throwable -> 0x05b1 }
            r51 = r0;
            r0 = r46;
            r6 = r0.outHeight;	 Catch:{ Throwable -> 0x05b1 }
            r0 = (float) r6;	 Catch:{ Throwable -> 0x05b1 }
            r48 = r0;
            r6 = r51 / r63;
            r7 = r48 / r35;
            r56 = java.lang.Math.max(r6, r7);	 Catch:{ Throwable -> 0x05b1 }
            r6 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
            r6 = (r56 > r6 ? 1 : (r56 == r6 ? 0 : -1));
            if (r6 >= 0) goto L_0x06aa;
        L_0x06a8:
            r56 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        L_0x06aa:
            r6 = 0;
            r0 = r46;
            r0.inJustDecodeBounds = r6;	 Catch:{ Throwable -> 0x05b1 }
            r0 = r56;
            r6 = (int) r0;	 Catch:{ Throwable -> 0x05b1 }
            r0 = r46;
            r0.inSampleSize = r6;	 Catch:{ Throwable -> 0x05b1 }
        L_0x06b6:
            r0 = r64;
            r7 = r0.sync;	 Catch:{ Throwable -> 0x05b1 }
            monitor-enter(r7);	 Catch:{ Throwable -> 0x05b1 }
            r0 = r64;
            r6 = r0.isCancelled;	 Catch:{ all -> 0x06c4 }
            if (r6 == 0) goto L_0x07dd;
        L_0x06c1:
            monitor-exit(r7);	 Catch:{ all -> 0x06c4 }
            goto L_0x0017;
        L_0x06c4:
            r6 = move-exception;
            monitor-exit(r7);	 Catch:{ all -> 0x06c4 }
            throw r6;	 Catch:{ Throwable -> 0x05b1 }
        L_0x06c7:
            r6 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Throwable -> 0x05b1 }
            r6 = r6.getContentResolver();	 Catch:{ Throwable -> 0x05b1 }
            r8 = r41.longValue();	 Catch:{ Throwable -> 0x05b1 }
            r7 = 1;
            r0 = r46;
            android.provider.MediaStore.Images.Thumbnails.getThumbnail(r6, r8, r7, r0);	 Catch:{ Throwable -> 0x05b1 }
            goto L_0x068c;
        L_0x06d8:
            if (r59 == 0) goto L_0x0757;
        L_0x06da:
            r31 = new java.io.RandomAccessFile;	 Catch:{ Throwable -> 0x05b1 }
            r6 = "r";
            r0 = r31;
            r1 = r23;
            r0.<init>(r1, r6);	 Catch:{ Throwable -> 0x05b1 }
            r6 = r31.length();	 Catch:{ Throwable -> 0x05b1 }
            r0 = (int) r6;	 Catch:{ Throwable -> 0x05b1 }
            r40 = r0;
            r6 = org.telegram.messenger.ImageLoader.bytes;	 Catch:{ Throwable -> 0x05b1 }
            if (r6 == 0) goto L_0x0754;
        L_0x06f3:
            r6 = org.telegram.messenger.ImageLoader.bytes;	 Catch:{ Throwable -> 0x05b1 }
            r6 = r6.length;	 Catch:{ Throwable -> 0x05b1 }
            r0 = r40;
            if (r6 < r0) goto L_0x0754;
        L_0x06fc:
            r26 = org.telegram.messenger.ImageLoader.bytes;	 Catch:{ Throwable -> 0x05b1 }
        L_0x0700:
            if (r26 != 0) goto L_0x070b;
        L_0x0702:
            r0 = r40;
            r0 = new byte[r0];	 Catch:{ Throwable -> 0x05b1 }
            r26 = r0;
            org.telegram.messenger.ImageLoader.bytes = r26;	 Catch:{ Throwable -> 0x05b1 }
        L_0x070b:
            r6 = 0;
            r0 = r31;
            r1 = r26;
            r2 = r40;
            r0.readFully(r1, r6, r2);	 Catch:{ Throwable -> 0x05b1 }
            r31.close();	 Catch:{ Throwable -> 0x05b1 }
            r6 = 0;
            r0 = r26;
            r1 = r40;
            r2 = r59;
            org.telegram.messenger.secretmedia.EncryptedFileInputStream.decryptBytesWithKeyFile(r0, r6, r1, r2);	 Catch:{ Throwable -> 0x05b1 }
            r6 = 0;
            r0 = r26;
            r1 = r40;
            r36 = org.telegram.messenger.Utilities.computeSHA256(r0, r6, r1);	 Catch:{ Throwable -> 0x05b1 }
            r29 = 0;
            if (r58 == 0) goto L_0x0739;
        L_0x072f:
            r0 = r36;
            r1 = r58;
            r6 = java.util.Arrays.equals(r0, r1);	 Catch:{ Throwable -> 0x05b1 }
            if (r6 != 0) goto L_0x073b;
        L_0x0739:
            r29 = 1;
        L_0x073b:
            r6 = 0;
            r6 = r26[r6];	 Catch:{ Throwable -> 0x05b1 }
            r0 = r6 & 255;
            r45 = r0;
            r40 = r40 - r45;
            if (r29 != 0) goto L_0x068c;
        L_0x0746:
            r0 = r26;
            r1 = r45;
            r2 = r40;
            r3 = r46;
            r5 = android.graphics.BitmapFactory.decodeByteArray(r0, r1, r2, r3);	 Catch:{ Throwable -> 0x05b1 }
            goto L_0x068c;
        L_0x0754:
            r26 = 0;
            goto L_0x0700;
        L_0x0757:
            if (r38 == 0) goto L_0x0776;
        L_0x0759:
            r39 = new org.telegram.messenger.secretmedia.EncryptedFileInputStream;	 Catch:{ Throwable -> 0x05b1 }
            r0 = r64;
            r6 = r0.cacheImage;	 Catch:{ Throwable -> 0x05b1 }
            r6 = r6.encryptionKeyPath;	 Catch:{ Throwable -> 0x05b1 }
            r0 = r39;
            r1 = r23;
            r0.<init>(r1, r6);	 Catch:{ Throwable -> 0x05b1 }
        L_0x0768:
            r6 = 0;
            r0 = r39;
            r1 = r46;
            r5 = android.graphics.BitmapFactory.decodeStream(r0, r6, r1);	 Catch:{ Throwable -> 0x05b1 }
            r39.close();	 Catch:{ Throwable -> 0x05b1 }
            goto L_0x068c;
        L_0x0776:
            r39 = new java.io.FileInputStream;	 Catch:{ Throwable -> 0x05b1 }
            r0 = r39;
            r1 = r23;
            r0.<init>(r1);	 Catch:{ Throwable -> 0x05b1 }
            goto L_0x0768;
        L_0x0780:
            if (r43 == 0) goto L_0x06b6;
        L_0x0782:
            r6 = 1;
            r0 = r46;
            r0.inJustDecodeBounds = r6;	 Catch:{ Throwable -> 0x05b1 }
            r6 = android.graphics.Bitmap.Config.RGB_565;	 Catch:{ Throwable -> 0x05b1 }
            r0 = r46;
            r0.inPreferredConfig = r6;	 Catch:{ Throwable -> 0x05b1 }
            r39 = new java.io.FileInputStream;	 Catch:{ Throwable -> 0x05b1 }
            r0 = r39;
            r1 = r23;
            r0.<init>(r1);	 Catch:{ Throwable -> 0x05b1 }
            r6 = 0;
            r0 = r39;
            r1 = r46;
            r5 = android.graphics.BitmapFactory.decodeStream(r0, r6, r1);	 Catch:{ Throwable -> 0x05b1 }
            r39.close();	 Catch:{ Throwable -> 0x05b1 }
            r0 = r46;
            r0 = r0.outWidth;	 Catch:{ Throwable -> 0x05b1 }
            r52 = r0;
            r0 = r46;
            r0 = r0.outHeight;	 Catch:{ Throwable -> 0x05b1 }
            r49 = r0;
            r6 = 0;
            r0 = r46;
            r0.inJustDecodeBounds = r6;	 Catch:{ Throwable -> 0x05b1 }
            r0 = r52;
            r6 = r0 / 200;
            r0 = r49;
            r7 = r0 / 200;
            r6 = java.lang.Math.max(r6, r7);	 Catch:{ Throwable -> 0x05b1 }
            r0 = (float) r6;	 Catch:{ Throwable -> 0x05b1 }
            r56 = r0;
            r6 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
            r6 = (r56 > r6 ? 1 : (r56 == r6 ? 0 : -1));
            if (r6 >= 0) goto L_0x07ca;
        L_0x07c8:
            r56 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        L_0x07ca:
            r55 = 1;
        L_0x07cc:
            r55 = r55 * 2;
            r6 = r55 * 2;
            r6 = (float) r6;	 Catch:{ Throwable -> 0x05b1 }
            r6 = (r6 > r56 ? 1 : (r6 == r56 ? 0 : -1));
            if (r6 < 0) goto L_0x07cc;
        L_0x07d5:
            r0 = r55;
            r1 = r46;
            r1.inSampleSize = r0;	 Catch:{ Throwable -> 0x05b1 }
            goto L_0x06b6;
        L_0x07dd:
            monitor-exit(r7);	 Catch:{ all -> 0x06c4 }
            r0 = r64;
            r6 = r0.cacheImage;	 Catch:{ Throwable -> 0x05b1 }
            r6 = r6.filter;	 Catch:{ Throwable -> 0x05b1 }
            if (r6 == 0) goto L_0x07f0;
        L_0x07e6:
            if (r17 != 0) goto L_0x07f0;
        L_0x07e8:
            r0 = r64;
            r6 = r0.cacheImage;	 Catch:{ Throwable -> 0x05b1 }
            r6 = r6.httpUrl;	 Catch:{ Throwable -> 0x05b1 }
            if (r6 == 0) goto L_0x0891;
        L_0x07f0:
            r6 = android.graphics.Bitmap.Config.ARGB_8888;	 Catch:{ Throwable -> 0x05b1 }
            r0 = r46;
            r0.inPreferredConfig = r6;	 Catch:{ Throwable -> 0x05b1 }
        L_0x07f6:
            r6 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Throwable -> 0x05b1 }
            r7 = 21;
            if (r6 >= r7) goto L_0x0801;
        L_0x07fc:
            r6 = 1;
            r0 = r46;
            r0.inPurgeable = r6;	 Catch:{ Throwable -> 0x05b1 }
        L_0x0801:
            r6 = 0;
            r0 = r46;
            r0.inDither = r6;	 Catch:{ Throwable -> 0x05b1 }
            if (r41 == 0) goto L_0x081d;
        L_0x0808:
            if (r43 != 0) goto L_0x081d;
        L_0x080a:
            if (r42 == 0) goto L_0x0899;
        L_0x080c:
            r6 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Throwable -> 0x05b1 }
            r6 = r6.getContentResolver();	 Catch:{ Throwable -> 0x05b1 }
            r8 = r41.longValue();	 Catch:{ Throwable -> 0x05b1 }
            r7 = 1;
            r0 = r46;
            r5 = android.provider.MediaStore.Video.Thumbnails.getThumbnail(r6, r8, r7, r0);	 Catch:{ Throwable -> 0x05b1 }
        L_0x081d:
            if (r5 != 0) goto L_0x0876;
        L_0x081f:
            if (r61 == 0) goto L_0x08ae;
        L_0x0821:
            r32 = new java.io.RandomAccessFile;	 Catch:{ Throwable -> 0x05b1 }
            r6 = "r";
            r0 = r32;
            r1 = r23;
            r0.<init>(r1, r6);	 Catch:{ Throwable -> 0x05b1 }
            r6 = r32.getChannel();	 Catch:{ Throwable -> 0x05b1 }
            r7 = java.nio.channels.FileChannel.MapMode.READ_ONLY;	 Catch:{ Throwable -> 0x05b1 }
            r8 = 0;
            r10 = r23.length();	 Catch:{ Throwable -> 0x05b1 }
            r21 = r6.map(r7, r8, r10);	 Catch:{ Throwable -> 0x05b1 }
            r20 = new android.graphics.BitmapFactory$Options;	 Catch:{ Throwable -> 0x05b1 }
            r20.<init>();	 Catch:{ Throwable -> 0x05b1 }
            r6 = 1;
            r0 = r20;
            r0.inJustDecodeBounds = r6;	 Catch:{ Throwable -> 0x05b1 }
            r6 = 0;
            r7 = r21.limit();	 Catch:{ Throwable -> 0x05b1 }
            r8 = 1;
            r0 = r21;
            r1 = r20;
            org.telegram.messenger.Utilities.loadWebpImage(r6, r0, r7, r1, r8);	 Catch:{ Throwable -> 0x05b1 }
            r0 = r20;
            r6 = r0.outWidth;	 Catch:{ Throwable -> 0x05b1 }
            r0 = r20;
            r7 = r0.outHeight;	 Catch:{ Throwable -> 0x05b1 }
            r8 = android.graphics.Bitmap.Config.ARGB_8888;	 Catch:{ Throwable -> 0x05b1 }
            r5 = org.telegram.messenger.Bitmaps.createBitmap(r6, r7, r8);	 Catch:{ Throwable -> 0x05b1 }
            r7 = r21.limit();	 Catch:{ Throwable -> 0x05b1 }
            r8 = 0;
            r0 = r46;
            r6 = r0.inPurgeable;	 Catch:{ Throwable -> 0x05b1 }
            if (r6 != 0) goto L_0x08ac;
        L_0x086d:
            r6 = 1;
        L_0x086e:
            r0 = r21;
            org.telegram.messenger.Utilities.loadWebpImage(r5, r0, r7, r8, r6);	 Catch:{ Throwable -> 0x05b1 }
            r32.close();	 Catch:{ Throwable -> 0x05b1 }
        L_0x0876:
            if (r5 != 0) goto L_0x09a3;
        L_0x0878:
            if (r24 == 0) goto L_0x026d;
        L_0x087a:
            r6 = r23.length();	 Catch:{ Throwable -> 0x05b1 }
            r8 = 0;
            r6 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1));
            if (r6 == 0) goto L_0x088c;
        L_0x0884:
            r0 = r64;
            r6 = r0.cacheImage;	 Catch:{ Throwable -> 0x05b1 }
            r6 = r6.filter;	 Catch:{ Throwable -> 0x05b1 }
            if (r6 != 0) goto L_0x026d;
        L_0x088c:
            r23.delete();	 Catch:{ Throwable -> 0x05b1 }
            goto L_0x026d;
        L_0x0891:
            r6 = android.graphics.Bitmap.Config.RGB_565;	 Catch:{ Throwable -> 0x05b1 }
            r0 = r46;
            r0.inPreferredConfig = r6;	 Catch:{ Throwable -> 0x05b1 }
            goto L_0x07f6;
        L_0x0899:
            r6 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Throwable -> 0x05b1 }
            r6 = r6.getContentResolver();	 Catch:{ Throwable -> 0x05b1 }
            r8 = r41.longValue();	 Catch:{ Throwable -> 0x05b1 }
            r7 = 1;
            r0 = r46;
            r5 = android.provider.MediaStore.Images.Thumbnails.getThumbnail(r6, r8, r7, r0);	 Catch:{ Throwable -> 0x05b1 }
            goto L_0x081d;
        L_0x08ac:
            r6 = 0;
            goto L_0x086e;
        L_0x08ae:
            r0 = r46;
            r6 = r0.inPurgeable;	 Catch:{ Throwable -> 0x05b1 }
            if (r6 != 0) goto L_0x08b6;
        L_0x08b4:
            if (r59 == 0) goto L_0x0948;
        L_0x08b6:
            r31 = new java.io.RandomAccessFile;	 Catch:{ Throwable -> 0x05b1 }
            r6 = "r";
            r0 = r31;
            r1 = r23;
            r0.<init>(r1, r6);	 Catch:{ Throwable -> 0x05b1 }
            r6 = r31.length();	 Catch:{ Throwable -> 0x05b1 }
            r0 = (int) r6;	 Catch:{ Throwable -> 0x05b1 }
            r40 = r0;
            r45 = 0;
            r6 = org.telegram.messenger.ImageLoader.bytes;	 Catch:{ Throwable -> 0x05b1 }
            if (r6 == 0) goto L_0x0934;
        L_0x08d1:
            r6 = org.telegram.messenger.ImageLoader.bytes;	 Catch:{ Throwable -> 0x05b1 }
            r6 = r6.length;	 Catch:{ Throwable -> 0x05b1 }
            r0 = r40;
            if (r6 < r0) goto L_0x0934;
        L_0x08da:
            r26 = org.telegram.messenger.ImageLoader.bytes;	 Catch:{ Throwable -> 0x05b1 }
        L_0x08de:
            if (r26 != 0) goto L_0x08e9;
        L_0x08e0:
            r0 = r40;
            r0 = new byte[r0];	 Catch:{ Throwable -> 0x05b1 }
            r26 = r0;
            org.telegram.messenger.ImageLoader.bytes = r26;	 Catch:{ Throwable -> 0x05b1 }
        L_0x08e9:
            r6 = 0;
            r0 = r31;
            r1 = r26;
            r2 = r40;
            r0.readFully(r1, r6, r2);	 Catch:{ Throwable -> 0x05b1 }
            r31.close();	 Catch:{ Throwable -> 0x05b1 }
            r29 = 0;
            if (r59 == 0) goto L_0x0937;
        L_0x08fa:
            r6 = 0;
            r0 = r26;
            r1 = r40;
            r2 = r59;
            org.telegram.messenger.secretmedia.EncryptedFileInputStream.decryptBytesWithKeyFile(r0, r6, r1, r2);	 Catch:{ Throwable -> 0x05b1 }
            r6 = 0;
            r0 = r26;
            r1 = r40;
            r36 = org.telegram.messenger.Utilities.computeSHA256(r0, r6, r1);	 Catch:{ Throwable -> 0x05b1 }
            if (r58 == 0) goto L_0x0919;
        L_0x090f:
            r0 = r36;
            r1 = r58;
            r6 = java.util.Arrays.equals(r0, r1);	 Catch:{ Throwable -> 0x05b1 }
            if (r6 != 0) goto L_0x091b;
        L_0x0919:
            r29 = 1;
        L_0x091b:
            r6 = 0;
            r6 = r26[r6];	 Catch:{ Throwable -> 0x05b1 }
            r0 = r6 & 255;
            r45 = r0;
            r40 = r40 - r45;
        L_0x0924:
            if (r29 != 0) goto L_0x0876;
        L_0x0926:
            r0 = r26;
            r1 = r45;
            r2 = r40;
            r3 = r46;
            r5 = android.graphics.BitmapFactory.decodeByteArray(r0, r1, r2, r3);	 Catch:{ Throwable -> 0x05b1 }
            goto L_0x0876;
        L_0x0934:
            r26 = 0;
            goto L_0x08de;
        L_0x0937:
            if (r38 == 0) goto L_0x0924;
        L_0x0939:
            r6 = 0;
            r0 = r64;
            r7 = r0.cacheImage;	 Catch:{ Throwable -> 0x05b1 }
            r7 = r7.encryptionKeyPath;	 Catch:{ Throwable -> 0x05b1 }
            r0 = r26;
            r1 = r40;
            org.telegram.messenger.secretmedia.EncryptedFileInputStream.decryptBytesWithKeyFile(r0, r6, r1, r7);	 Catch:{ Throwable -> 0x05b1 }
            goto L_0x0924;
        L_0x0948:
            if (r38 == 0) goto L_0x0990;
        L_0x094a:
            r39 = new org.telegram.messenger.secretmedia.EncryptedFileInputStream;	 Catch:{ Throwable -> 0x05b1 }
            r0 = r64;
            r6 = r0.cacheImage;	 Catch:{ Throwable -> 0x05b1 }
            r6 = r6.encryptionKeyPath;	 Catch:{ Throwable -> 0x05b1 }
            r0 = r39;
            r1 = r23;
            r0.<init>(r1, r6);	 Catch:{ Throwable -> 0x05b1 }
        L_0x0959:
            r0 = r64;
            r6 = r0.cacheImage;	 Catch:{ Throwable -> 0x05b1 }
            r6 = r6.location;	 Catch:{ Throwable -> 0x05b1 }
            r6 = r6 instanceof org.telegram.tgnet.TLRPC.TL_document;	 Catch:{ Throwable -> 0x05b1 }
            if (r6 == 0) goto L_0x0982;
        L_0x0963:
            r30 = new android.support.media.ExifInterface;	 Catch:{ Throwable -> 0x0a78 }
            r0 = r30;
            r1 = r39;
            r0.<init>(r1);	 Catch:{ Throwable -> 0x0a78 }
            r6 = "Orientation";
            r7 = 1;
            r0 = r30;
            r13 = r0.getAttributeInt(r6, r7);	 Catch:{ Throwable -> 0x0a78 }
            switch(r13) {
                case 3: goto L_0x099d;
                case 4: goto L_0x0979;
                case 5: goto L_0x0979;
                case 6: goto L_0x099a;
                case 7: goto L_0x0979;
                case 8: goto L_0x09a0;
                default: goto L_0x0979;
            };
        L_0x0979:
            r6 = r39.getChannel();	 Catch:{ Throwable -> 0x05b1 }
            r8 = 0;
            r6.position(r8);	 Catch:{ Throwable -> 0x05b1 }
        L_0x0982:
            r6 = 0;
            r0 = r39;
            r1 = r46;
            r5 = android.graphics.BitmapFactory.decodeStream(r0, r6, r1);	 Catch:{ Throwable -> 0x05b1 }
            r39.close();	 Catch:{ Throwable -> 0x05b1 }
            goto L_0x0876;
        L_0x0990:
            r39 = new java.io.FileInputStream;	 Catch:{ Throwable -> 0x05b1 }
            r0 = r39;
            r1 = r23;
            r0.<init>(r1);	 Catch:{ Throwable -> 0x05b1 }
            goto L_0x0959;
        L_0x099a:
            r47 = 90;
            goto L_0x0979;
        L_0x099d:
            r47 = 180; // 0xb4 float:2.52E-43 double:8.9E-322;
            goto L_0x0979;
        L_0x09a0:
            r47 = 270; // 0x10e float:3.78E-43 double:1.334E-321;
            goto L_0x0979;
        L_0x09a3:
            r19 = 0;
            r0 = r64;
            r6 = r0.cacheImage;	 Catch:{ Throwable -> 0x05b1 }
            r6 = r6.filter;	 Catch:{ Throwable -> 0x05b1 }
            if (r6 == 0) goto L_0x0a51;
        L_0x09ad:
            r6 = r5.getWidth();	 Catch:{ Throwable -> 0x05b1 }
            r0 = (float) r6;	 Catch:{ Throwable -> 0x05b1 }
            r16 = r0;
            r6 = r5.getHeight();	 Catch:{ Throwable -> 0x05b1 }
            r15 = (float) r6;	 Catch:{ Throwable -> 0x05b1 }
            r0 = r46;
            r6 = r0.inPurgeable;	 Catch:{ Throwable -> 0x05b1 }
            if (r6 != 0) goto L_0x09e6;
        L_0x09bf:
            r6 = 0;
            r6 = (r63 > r6 ? 1 : (r63 == r6 ? 0 : -1));
            if (r6 == 0) goto L_0x09e6;
        L_0x09c4:
            r6 = (r16 > r63 ? 1 : (r16 == r63 ? 0 : -1));
            if (r6 == 0) goto L_0x09e6;
        L_0x09c8:
            r6 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
            r6 = r6 + r63;
            r6 = (r16 > r6 ? 1 : (r16 == r6 ? 0 : -1));
            if (r6 <= 0) goto L_0x09e6;
        L_0x09d0:
            r56 = r16 / r63;
            r0 = r63;
            r6 = (int) r0;	 Catch:{ Throwable -> 0x05b1 }
            r7 = r15 / r56;
            r7 = (int) r7;	 Catch:{ Throwable -> 0x05b1 }
            r8 = 1;
            r57 = org.telegram.messenger.Bitmaps.createScaledBitmap(r5, r6, r7, r8);	 Catch:{ Throwable -> 0x05b1 }
            r0 = r57;
            if (r5 == r0) goto L_0x09e6;
        L_0x09e1:
            r5.recycle();	 Catch:{ Throwable -> 0x05b1 }
            r5 = r57;
        L_0x09e6:
            if (r5 == 0) goto L_0x0a51;
        L_0x09e8:
            if (r25 == 0) goto L_0x0a22;
        L_0x09ea:
            r14 = r5;
            r62 = r5.getWidth();	 Catch:{ Throwable -> 0x05b1 }
            r34 = r5.getHeight();	 Catch:{ Throwable -> 0x05b1 }
            r6 = r62 * r34;
            r7 = 22500; // 0x57e4 float:3.1529E-41 double:1.11165E-319;
            if (r6 <= r7) goto L_0x0a02;
        L_0x09f9:
            r6 = 100;
            r7 = 100;
            r8 = 0;
            r14 = org.telegram.messenger.Bitmaps.createScaledBitmap(r5, r6, r7, r8);	 Catch:{ Throwable -> 0x05b1 }
        L_0x0a02:
            r0 = r46;
            r6 = r0.inPurgeable;	 Catch:{ Throwable -> 0x05b1 }
            if (r6 == 0) goto L_0x0a5e;
        L_0x0a08:
            r6 = 0;
        L_0x0a09:
            r7 = r14.getWidth();	 Catch:{ Throwable -> 0x05b1 }
            r8 = r14.getHeight();	 Catch:{ Throwable -> 0x05b1 }
            r9 = r14.getRowBytes();	 Catch:{ Throwable -> 0x05b1 }
            r6 = org.telegram.messenger.Utilities.needInvert(r14, r6, r7, r8, r9);	 Catch:{ Throwable -> 0x05b1 }
            if (r6 == 0) goto L_0x0a60;
        L_0x0a1b:
            r44 = 1;
        L_0x0a1d:
            if (r14 == r5) goto L_0x0a22;
        L_0x0a1f:
            r14.recycle();	 Catch:{ Throwable -> 0x05b1 }
        L_0x0a22:
            if (r17 == 0) goto L_0x0a51;
        L_0x0a24:
            r6 = NUM; // 0x42CLASSNAME float:100.0 double:5.53552857E-315;
            r6 = (r15 > r6 ? 1 : (r15 == r6 ? 0 : -1));
            if (r6 >= 0) goto L_0x0a51;
        L_0x0a2a:
            r6 = NUM; // 0x42CLASSNAME float:100.0 double:5.53552857E-315;
            r6 = (r16 > r6 ? 1 : (r16 == r6 ? 0 : -1));
            if (r6 >= 0) goto L_0x0a51;
        L_0x0a30:
            r6 = r5.getConfig();	 Catch:{ Throwable -> 0x05b1 }
            r7 = android.graphics.Bitmap.Config.ARGB_8888;	 Catch:{ Throwable -> 0x05b1 }
            if (r6 != r7) goto L_0x0a4f;
        L_0x0a38:
            r6 = 3;
            r0 = r46;
            r7 = r0.inPurgeable;	 Catch:{ Throwable -> 0x05b1 }
            if (r7 == 0) goto L_0x0a63;
        L_0x0a3f:
            r7 = 0;
        L_0x0a40:
            r8 = r5.getWidth();	 Catch:{ Throwable -> 0x05b1 }
            r9 = r5.getHeight();	 Catch:{ Throwable -> 0x05b1 }
            r10 = r5.getRowBytes();	 Catch:{ Throwable -> 0x05b1 }
            org.telegram.messenger.Utilities.blurBitmap(r5, r6, r7, r8, r9, r10);	 Catch:{ Throwable -> 0x05b1 }
        L_0x0a4f:
            r19 = 1;
        L_0x0a51:
            if (r19 != 0) goto L_0x026d;
        L_0x0a53:
            r0 = r46;
            r6 = r0.inPurgeable;	 Catch:{ Throwable -> 0x05b1 }
            if (r6 == 0) goto L_0x026d;
        L_0x0a59:
            org.telegram.messenger.Utilities.pinBitmap(r5);	 Catch:{ Throwable -> 0x05b1 }
            goto L_0x026d;
        L_0x0a5e:
            r6 = 1;
            goto L_0x0a09;
        L_0x0a60:
            r44 = 0;
            goto L_0x0a1d;
        L_0x0a63:
            r7 = 1;
            goto L_0x0a40;
        L_0x0a65:
            r6 = 0;
            goto L_0x027f;
        L_0x0a68:
            if (r5 == 0) goto L_0x0a76;
        L_0x0a6a:
            r6 = new android.graphics.drawable.BitmapDrawable;
            r6.<init>(r5);
        L_0x0a6f:
            r0 = r64;
            r0.onPostExecute(r6);
            goto L_0x0017;
        L_0x0a76:
            r6 = 0;
            goto L_0x0a6f;
        L_0x0a78:
            r6 = move-exception;
            goto L_0x0979;
        L_0x0a7b:
            r6 = move-exception;
            r53 = r54;
            goto L_0x02ba;
        L_0x0a80:
            r28 = move-exception;
            r53 = r54;
            goto L_0x02a9;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ImageLoader.CacheOutTask.run():void");
        }

        private void onPostExecute(BitmapDrawable bitmapDrawable) {
            AndroidUtilities.runOnUIThread(new ImageLoader$CacheOutTask$$Lambda$0(this, bitmapDrawable));
        }

        final /* synthetic */ void lambda$onPostExecute$1$ImageLoader$CacheOutTask(BitmapDrawable bitmapDrawable) {
            BitmapDrawable toSet = null;
            if (bitmapDrawable instanceof AnimatedFileDrawable) {
                toSet = bitmapDrawable;
            } else if (bitmapDrawable != null) {
                toSet = ImageLoader.this.memCache.get(this.cacheImage.key);
                if (toSet == null) {
                    ImageLoader.this.memCache.put(this.cacheImage.key, bitmapDrawable);
                    toSet = bitmapDrawable;
                } else {
                    bitmapDrawable.getBitmap().recycle();
                }
            }
            ImageLoader.this.imageLoadQueue.postRunnable(new ImageLoader$CacheOutTask$$Lambda$1(this, toSet));
        }

        final /* synthetic */ void lambda$null$0$ImageLoader$CacheOutTask(BitmapDrawable toSetFinal) {
            this.cacheImage.setImageAndClear(toSetFinal);
        }

        public void cancel() {
            synchronized (this.sync) {
                try {
                    this.isCancelled = true;
                    if (this.runningThread != null) {
                        this.runningThread.interrupt();
                    }
                } catch (Exception e) {
                }
            }
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

        public HttpFileTask(String url, File tempFile, String ext, int currentAccount) {
            this.url = url;
            this.tempFile = tempFile;
            this.ext = ext;
            this.currentAccount = currentAccount;
        }

        private void reportProgress(float progress) {
            long currentTime = System.currentTimeMillis();
            if (progress == 1.0f || this.lastProgressTime == 0 || this.lastProgressTime < currentTime - 500) {
                this.lastProgressTime = currentTime;
                Utilities.stageQueue.postRunnable(new ImageLoader$HttpFileTask$$Lambda$0(this, progress));
            }
        }

        final /* synthetic */ void lambda$reportProgress$1$ImageLoader$HttpFileTask(float progress) {
            ImageLoader.this.fileProgresses.put(this.url, Float.valueOf(progress));
            AndroidUtilities.runOnUIThread(new ImageLoader$HttpFileTask$$Lambda$1(this, progress));
        }

        final /* synthetic */ void lambda$null$0$ImageLoader$HttpFileTask(float progress) {
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.FileLoadProgressChanged, this.url, Float.valueOf(progress));
        }

        protected Boolean doInBackground(Void... voids) {
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
                    if (httpConnection instanceof HttpURLConnection) {
                        int code = ((HttpURLConnection) httpConnection).getResponseCode();
                        if (!(code == 200 || code == 202 || code == 304)) {
                            this.canRetry = false;
                        }
                    }
                } catch (Throwable e2) {
                    FileLog.e(e2);
                }
                if (httpConnection != null) {
                    try {
                        Map<String, List<String>> headerFields = httpConnection.getHeaderFields();
                        if (headerFields != null) {
                            List values = (List) headerFields.get("content-Length");
                            if (!(values == null || values.isEmpty())) {
                                String length = (String) values.get(0);
                                if (length != null) {
                                    this.fileSize = Utilities.parseInt(length).intValue();
                                }
                            }
                        }
                    } catch (Throwable e22) {
                        FileLog.e(e22);
                    }
                }
                if (httpConnectionStream != null) {
                    try {
                        byte[] data = new byte[32768];
                        int totalLoaded = 0;
                        while (!isCancelled()) {
                            int read = httpConnectionStream.read(data);
                            if (read > 0) {
                                this.fileOutputStream.write(data, 0, read);
                                totalLoaded += read;
                                if (this.fileSize > 0) {
                                    reportProgress(((float) totalLoaded) / ((float) this.fileSize));
                                }
                            } else if (read == -1) {
                                done = true;
                                if (this.fileSize != 0) {
                                    reportProgress(1.0f);
                                }
                            }
                        }
                    } catch (Throwable e222) {
                        FileLog.e(e222);
                    } catch (Throwable e2222) {
                        FileLog.e(e2222);
                    }
                }
                try {
                    if (this.fileOutputStream != null) {
                        this.fileOutputStream.close();
                        this.fileOutputStream = null;
                    }
                } catch (Throwable e22222) {
                    FileLog.e(e22222);
                }
                if (httpConnectionStream != null) {
                    try {
                        httpConnectionStream.close();
                    } catch (Throwable e222222) {
                        FileLog.e(e222222);
                    }
                }
            }
            return Boolean.valueOf(done);
        }

        protected void onPostExecute(Boolean result) {
            ImageLoader.this.runHttpFileLoadTasks(this, result.booleanValue() ? 2 : 1);
        }

        protected void onCancelled() {
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

        public HttpImageTask(CacheImage cacheImage, int size) {
            this.cacheImage = cacheImage;
            this.imageSize = size;
        }

        public HttpImageTask(CacheImage cacheImage, int size, String url) {
            this.cacheImage = cacheImage;
            this.imageSize = size;
            this.overrideUrl = url;
        }

        private void reportProgress(float progress) {
            long currentTime = System.currentTimeMillis();
            if (progress == 1.0f || this.lastProgressTime == 0 || this.lastProgressTime < currentTime - 500) {
                this.lastProgressTime = currentTime;
                Utilities.stageQueue.postRunnable(new ImageLoader$HttpImageTask$$Lambda$0(this, progress));
            }
        }

        final /* synthetic */ void lambda$reportProgress$1$ImageLoader$HttpImageTask(float progress) {
            ImageLoader.this.fileProgresses.put(this.cacheImage.url, Float.valueOf(progress));
            AndroidUtilities.runOnUIThread(new ImageLoader$HttpImageTask$$Lambda$8(this, progress));
        }

        final /* synthetic */ void lambda$null$0$ImageLoader$HttpImageTask(float progress) {
            NotificationCenter.getInstance(this.cacheImage.currentAccount).postNotificationName(NotificationCenter.FileLoadProgressChanged, this.cacheImage.url, Float.valueOf(progress));
        }

        protected Boolean doInBackground(Void... voids) {
            InputStream httpConnectionStream = null;
            boolean done = false;
            if (!isCancelled()) {
                try {
                    String str;
                    if (this.cacheImage.httpUrl.startsWith("https://static-maps") || this.cacheImage.httpUrl.startsWith("https://maps.googleapis")) {
                        int provider = MessagesController.getInstance(this.cacheImage.currentAccount).mapProvider;
                        if (provider == 3 || provider == 4) {
                            WebFile webFile = (WebFile) ImageLoader.this.testWebFile.get(this.cacheImage.httpUrl);
                            if (webFile != null) {
                                TL_upload_getWebFile req = new TL_upload_getWebFile();
                                req.location = webFile.location;
                                req.offset = 0;
                                req.limit = 0;
                                ConnectionsManager.getInstance(this.cacheImage.currentAccount).sendRequest(req, ImageLoader$HttpImageTask$$Lambda$1.$instance);
                            }
                        }
                    }
                    if (this.overrideUrl != null) {
                        str = this.overrideUrl;
                    } else {
                        str = this.cacheImage.httpUrl;
                    }
                    this.httpConnection = (HttpURLConnection) new URL(str).openConnection();
                    this.httpConnection.addRequestProperty("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 10_0 like Mac OS X) AppleWebKit/602.1.38 (KHTML, like Gecko) Version/10.0 Mobile/14A5297c Safari/602.1");
                    this.httpConnection.setConnectTimeout(5000);
                    this.httpConnection.setReadTimeout(5000);
                    this.httpConnection.setInstanceFollowRedirects(true);
                    if (!isCancelled()) {
                        this.httpConnection.connect();
                        httpConnectionStream = this.httpConnection.getInputStream();
                        this.fileOutputStream = new RandomAccessFile(this.cacheImage.tempFilePath, "rws");
                    }
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
            }
            if (!isCancelled()) {
                try {
                    if (this.httpConnection != null) {
                        int code = this.httpConnection.getResponseCode();
                        if (!(code == 200 || code == 202 || code == 304)) {
                            this.canRetry = false;
                        }
                    }
                } catch (Throwable e2) {
                    FileLog.e(e2);
                }
                if (this.imageSize == 0 && this.httpConnection != null) {
                    try {
                        Map<String, List<String>> headerFields = this.httpConnection.getHeaderFields();
                        if (headerFields != null) {
                            List values = (List) headerFields.get("content-Length");
                            if (!(values == null || values.isEmpty())) {
                                String length = (String) values.get(0);
                                if (length != null) {
                                    this.imageSize = Utilities.parseInt(length).intValue();
                                }
                            }
                        }
                    } catch (Throwable e22) {
                        FileLog.e(e22);
                    }
                }
                if (httpConnectionStream != null) {
                    try {
                        byte[] data = new byte[8192];
                        int totalLoaded = 0;
                        while (!isCancelled()) {
                            int read = httpConnectionStream.read(data);
                            if (read > 0) {
                                totalLoaded += read;
                                this.fileOutputStream.write(data, 0, read);
                                if (this.imageSize != 0) {
                                    reportProgress(((float) totalLoaded) / ((float) this.imageSize));
                                }
                            } else if (read == -1) {
                                done = true;
                                if (this.imageSize != 0) {
                                    reportProgress(1.0f);
                                }
                            }
                        }
                    } catch (Throwable e222) {
                        FileLog.e(e222);
                    } catch (Throwable e2222) {
                        FileLog.e(e2222);
                    }
                }
            }
            try {
                if (this.fileOutputStream != null) {
                    this.fileOutputStream.close();
                    this.fileOutputStream = null;
                }
            } catch (Throwable e22222) {
                FileLog.e(e22222);
            }
            try {
                if (this.httpConnection != null) {
                    this.httpConnection.disconnect();
                }
            } catch (Throwable th) {
            }
            if (httpConnectionStream != null) {
                try {
                    httpConnectionStream.close();
                } catch (Throwable e222222) {
                    FileLog.e(e222222);
                }
            }
            if (!(!done || this.cacheImage.tempFilePath == null || this.cacheImage.tempFilePath.renameTo(this.cacheImage.finalFilePath))) {
                this.cacheImage.finalFilePath = this.cacheImage.tempFilePath;
            }
            return Boolean.valueOf(done);
        }

        static final /* synthetic */ void lambda$doInBackground$2$ImageLoader$HttpImageTask(TLObject response, TL_error error) {
        }

        protected void onPostExecute(Boolean result) {
            if (result.booleanValue() || !this.canRetry) {
                ImageLoader.this.fileDidLoaded(this.cacheImage.url, this.cacheImage.finalFilePath, 0);
            } else {
                ImageLoader.this.httpFileLoadError(this.cacheImage.url);
            }
            Utilities.stageQueue.postRunnable(new ImageLoader$HttpImageTask$$Lambda$2(this, result));
            ImageLoader.this.imageLoadQueue.postRunnable(new ImageLoader$HttpImageTask$$Lambda$3(this));
        }

        final /* synthetic */ void lambda$onPostExecute$4$ImageLoader$HttpImageTask(Boolean result) {
            ImageLoader.this.fileProgresses.remove(this.cacheImage.url);
            AndroidUtilities.runOnUIThread(new ImageLoader$HttpImageTask$$Lambda$7(this, result));
        }

        final /* synthetic */ void lambda$null$3$ImageLoader$HttpImageTask(Boolean result) {
            if (result.booleanValue()) {
                NotificationCenter.getInstance(this.cacheImage.currentAccount).postNotificationName(NotificationCenter.fileDidLoad, this.cacheImage.url);
                return;
            }
            NotificationCenter.getInstance(this.cacheImage.currentAccount).postNotificationName(NotificationCenter.fileDidFailedLoad, this.cacheImage.url, Integer.valueOf(2));
        }

        final /* synthetic */ void lambda$onPostExecute$5$ImageLoader$HttpImageTask() {
            ImageLoader.this.runHttpTasks(true);
        }

        final /* synthetic */ void lambda$onCancelled$6$ImageLoader$HttpImageTask() {
            ImageLoader.this.runHttpTasks(true);
        }

        protected void onCancelled() {
            ImageLoader.this.imageLoadQueue.postRunnable(new ImageLoader$HttpImageTask$$Lambda$4(this));
            Utilities.stageQueue.postRunnable(new ImageLoader$HttpImageTask$$Lambda$5(this));
        }

        final /* synthetic */ void lambda$onCancelled$8$ImageLoader$HttpImageTask() {
            ImageLoader.this.fileProgresses.remove(this.cacheImage.url);
            AndroidUtilities.runOnUIThread(new ImageLoader$HttpImageTask$$Lambda$6(this));
        }

        final /* synthetic */ void lambda$null$7$ImageLoader$HttpImageTask() {
            NotificationCenter.getInstance(this.cacheImage.currentAccount).postNotificationName(NotificationCenter.fileDidFailedLoad, this.cacheImage.url, Integer.valueOf(1));
        }
    }

    private class ThumbGenerateInfo {
        private String filter;
        private ArrayList<ImageReceiver> imageReceiverArray;
        private Document parentDocument;

        private ThumbGenerateInfo() {
            this.imageReceiverArray = new ArrayList();
        }

        /* synthetic */ ThumbGenerateInfo(ImageLoader x0, AnonymousClass1 x1) {
            this();
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
            if (this.info != null) {
                ImageLoader.this.imageLoadQueue.postRunnable(new ImageLoader$ThumbGenerateTask$$Lambda$0(this, FileLoader.getAttachFileName(this.info.parentDocument)));
            }
        }

        final /* synthetic */ void lambda$removeTask$0$ImageLoader$ThumbGenerateTask(String name) {
            ThumbGenerateTask thumbGenerateTask = (ThumbGenerateTask) ImageLoader.this.thumbGenerateTasks.remove(name);
        }

        public void run() {
            try {
                if (this.info == null) {
                    removeTask();
                    return;
                }
                String key = "q_" + this.info.parentDocument.dc_id + "_" + this.info.parentDocument.id;
                File thumbFile = new File(FileLoader.getDirectory(4), key + ".jpg");
                if (thumbFile.exists() || !this.originalPath.exists()) {
                    removeTask();
                    return;
                }
                int size = Math.min(180, Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y) / 4);
                Bitmap originalBitmap = null;
                if (this.mediaType == 0) {
                    originalBitmap = ImageLoader.loadBitmap(this.originalPath.toString(), null, (float) size, (float) size, false);
                } else if (this.mediaType == 2) {
                    originalBitmap = ThumbnailUtils.createVideoThumbnail(this.originalPath.toString(), 1);
                } else if (this.mediaType == 3) {
                    String path = this.originalPath.toString().toLowerCase();
                    if (path.endsWith(".jpg") || path.endsWith(".jpeg") || path.endsWith(".png") || path.endsWith(".gif")) {
                        originalBitmap = ImageLoader.loadBitmap(path, null, (float) size, (float) size, false);
                    } else {
                        removeTask();
                        return;
                    }
                }
                if (originalBitmap == null) {
                    removeTask();
                    return;
                }
                int w = originalBitmap.getWidth();
                int h = originalBitmap.getHeight();
                if (w == 0 || h == 0) {
                    removeTask();
                    return;
                }
                float scaleFactor = Math.min(((float) w) / ((float) size), ((float) h) / ((float) size));
                Bitmap scaledBitmap = Bitmaps.createScaledBitmap(originalBitmap, (int) (((float) w) / scaleFactor), (int) (((float) h) / scaleFactor), true);
                if (scaledBitmap != originalBitmap) {
                    originalBitmap.recycle();
                    originalBitmap = scaledBitmap;
                }
                FileOutputStream stream = new FileOutputStream(thumbFile);
                originalBitmap.compress(CompressFormat.JPEG, 60, stream);
                stream.close();
                AndroidUtilities.runOnUIThread(new ImageLoader$ThumbGenerateTask$$Lambda$1(this, key, new ArrayList(this.info.imageReceiverArray), new BitmapDrawable(originalBitmap)));
            } catch (Throwable e) {
                FileLog.e(e);
            } catch (Throwable e2) {
                FileLog.e(e2);
                removeTask();
            }
        }

        final /* synthetic */ void lambda$run$1$ImageLoader$ThumbGenerateTask(String key, ArrayList finalImageReceiverArray, BitmapDrawable bitmapDrawable) {
            removeTask();
            String kf = key;
            if (this.info.filter != null) {
                kf = kf + "@" + this.info.filter;
            }
            for (int a = 0; a < finalImageReceiverArray.size(); a++) {
                ((ImageReceiver) finalImageReceiverArray.get(a)).setImageBitmapByKey(bitmapDrawable, kf, false, false);
            }
            ImageLoader.this.memCache.put(kf, bitmapDrawable);
        }
    }

    public static ImageLoader getInstance() {
        Throwable th;
        ImageLoader localInstance = Instance;
        if (localInstance == null) {
            synchronized (ImageLoader.class) {
                try {
                    localInstance = Instance;
                    if (localInstance == null) {
                        ImageLoader localInstance2 = new ImageLoader();
                        try {
                            Instance = localInstance2;
                            localInstance = localInstance2;
                        } catch (Throwable th2) {
                            th = th2;
                            localInstance = localInstance2;
                            throw th;
                        }
                    }
                } catch (Throwable th3) {
                    th = th3;
                    throw th;
                }
            }
        }
        return localInstance;
    }

    public ImageLoader() {
        this.thumbGeneratingQueue.setPriority(1);
        this.memCache = new LruCache((Math.min(15, ((ActivityManager) ApplicationLoader.applicationContext.getSystemService("activity")).getMemoryClass() / 7) * 1024) * 1024) {
            protected int sizeOf(String key, BitmapDrawable value) {
                return value.getBitmap().getByteCount();
            }

            protected void entryRemoved(boolean evicted, String key, BitmapDrawable oldValue, BitmapDrawable newValue) {
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
        SparseArray<File> mediaDirs = new SparseArray();
        File cachePath = AndroidUtilities.getCacheDir();
        if (!cachePath.isDirectory()) {
            try {
                cachePath.mkdirs();
            } catch (Throwable e) {
                FileLog.e(e);
            }
        }
        try {
            new File(cachePath, ".nomedia").createNewFile();
        } catch (Throwable e2) {
            FileLog.e(e2);
        }
        mediaDirs.put(4, cachePath);
        for (int a = 0; a < 3; a++) {
            final int currentAccount = a;
            FileLoader.getInstance(a).setDelegate(new FileLoaderDelegate() {
                public void fileUploadProgressChanged(String location, float progress, boolean isEncrypted) {
                    ImageLoader.this.fileProgresses.put(location, Float.valueOf(progress));
                    long currentTime = System.currentTimeMillis();
                    if (ImageLoader.this.lastProgressUpdateTime == 0 || ImageLoader.this.lastProgressUpdateTime < currentTime - 500) {
                        ImageLoader.this.lastProgressUpdateTime = currentTime;
                        AndroidUtilities.runOnUIThread(new ImageLoader$2$$Lambda$0(currentAccount, location, progress, isEncrypted));
                    }
                }

                public void fileDidUploaded(String location, InputFile inputFile, InputEncryptedFile inputEncryptedFile, byte[] key, byte[] iv, long totalFileSize) {
                    Utilities.stageQueue.postRunnable(new ImageLoader$2$$Lambda$1(this, currentAccount, location, inputFile, inputEncryptedFile, key, iv, totalFileSize));
                }

                final /* synthetic */ void lambda$fileDidUploaded$2$ImageLoader$2(int currentAccount, String location, InputFile inputFile, InputEncryptedFile inputEncryptedFile, byte[] key, byte[] iv, long totalFileSize) {
                    AndroidUtilities.runOnUIThread(new ImageLoader$2$$Lambda$7(currentAccount, location, inputFile, inputEncryptedFile, key, iv, totalFileSize));
                    ImageLoader.this.fileProgresses.remove(location);
                }

                public void fileDidFailedUpload(String location, boolean isEncrypted) {
                    Utilities.stageQueue.postRunnable(new ImageLoader$2$$Lambda$2(this, currentAccount, location, isEncrypted));
                }

                final /* synthetic */ void lambda$fileDidFailedUpload$4$ImageLoader$2(int currentAccount, String location, boolean isEncrypted) {
                    AndroidUtilities.runOnUIThread(new ImageLoader$2$$Lambda$6(currentAccount, location, isEncrypted));
                    ImageLoader.this.fileProgresses.remove(location);
                }

                public void fileDidLoaded(String location, File finalFile, int type) {
                    ImageLoader.this.fileProgresses.remove(location);
                    AndroidUtilities.runOnUIThread(new ImageLoader$2$$Lambda$3(this, finalFile, location, currentAccount, type));
                }

                final /* synthetic */ void lambda$fileDidLoaded$5$ImageLoader$2(File finalFile, String location, int currentAccount, int type) {
                    if (SharedConfig.saveToGallery && ImageLoader.this.telegramPath != null && finalFile != null && ((location.endsWith(".mp4") || location.endsWith(".jpg")) && finalFile.toString().startsWith(ImageLoader.this.telegramPath.toString()))) {
                        AndroidUtilities.addMediaToGallery(finalFile.toString());
                    }
                    NotificationCenter.getInstance(currentAccount).postNotificationName(NotificationCenter.fileDidLoad, location);
                    ImageLoader.this.fileDidLoaded(location, finalFile, type);
                }

                public void fileDidFailedLoad(String location, int canceled) {
                    ImageLoader.this.fileProgresses.remove(location);
                    AndroidUtilities.runOnUIThread(new ImageLoader$2$$Lambda$4(this, location, canceled, currentAccount));
                }

                final /* synthetic */ void lambda$fileDidFailedLoad$6$ImageLoader$2(String location, int canceled, int currentAccount) {
                    ImageLoader.this.fileDidFailedLoad(location, canceled);
                    NotificationCenter.getInstance(currentAccount).postNotificationName(NotificationCenter.fileDidFailedLoad, location, Integer.valueOf(canceled));
                }

                public void fileLoadProgressChanged(String location, float progress) {
                    ImageLoader.this.fileProgresses.put(location, Float.valueOf(progress));
                    long currentTime = System.currentTimeMillis();
                    if (ImageLoader.this.lastProgressUpdateTime == 0 || ImageLoader.this.lastProgressUpdateTime < currentTime - 500) {
                        ImageLoader.this.lastProgressUpdateTime = currentTime;
                        AndroidUtilities.runOnUIThread(new ImageLoader$2$$Lambda$5(currentAccount, location, progress));
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
                Runnable r = new ImageLoader$3$$Lambda$0(this);
                if ("android.intent.action.MEDIA_UNMOUNTED".equals(intent.getAction())) {
                    AndroidUtilities.runOnUIThread(r, 1000);
                } else {
                    r.run();
                }
            }

            final /* synthetic */ void lambda$onReceive$0$ImageLoader$3() {
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
        this.cacheOutQueue.postRunnable(new ImageLoader$$Lambda$0(this));
    }

    final /* synthetic */ void lambda$checkMediaPaths$1$ImageLoader() {
        AndroidUtilities.runOnUIThread(new ImageLoader$$Lambda$11(createMediaPaths()));
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

    public SparseArray<File> createMediaPaths() {
        SparseArray<File> mediaDirs = new SparseArray();
        File cachePath = AndroidUtilities.getCacheDir();
        if (!cachePath.isDirectory()) {
            try {
                cachePath.mkdirs();
            } catch (Throwable e) {
                FileLog.e(e);
            }
        }
        try {
            new File(cachePath, ".nomedia").createNewFile();
        } catch (Throwable e2) {
            FileLog.e(e2);
        }
        mediaDirs.put(4, cachePath);
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("cache path = " + cachePath);
        }
        try {
            if ("mounted".equals(Environment.getExternalStorageState())) {
                this.telegramPath = new File(Environment.getExternalStorageDirectory(), "Telegram");
                this.telegramPath.mkdirs();
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
                    } catch (Throwable e22) {
                        FileLog.e(e22);
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
                    } catch (Throwable e222) {
                        FileLog.e(e222);
                    }
                    try {
                        File audioPath = new File(this.telegramPath, "Telegram Audio");
                        audioPath.mkdir();
                        if (audioPath.isDirectory() && canMoveFiles(cachePath, audioPath, 1)) {
                            new File(audioPath, ".nomedia").createNewFile();
                            mediaDirs.put(1, audioPath);
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.d("audio path = " + audioPath);
                            }
                        }
                    } catch (Throwable e2222) {
                        FileLog.e(e2222);
                    }
                    try {
                        File documentPath = new File(this.telegramPath, "Telegram Documents");
                        documentPath.mkdir();
                        if (documentPath.isDirectory() && canMoveFiles(cachePath, documentPath, 3)) {
                            new File(documentPath, ".nomedia").createNewFile();
                            mediaDirs.put(3, documentPath);
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.d("documents path = " + documentPath);
                            }
                        }
                    } catch (Throwable e22222) {
                        FileLog.e(e22222);
                    }
                }
            } else if (BuildVars.LOGS_ENABLED) {
                FileLog.d("this Android can't rename files");
            }
            SharedConfig.checkSaveToGalleryFiles();
        } catch (Throwable e222222) {
            FileLog.e(e222222);
        }
        return mediaDirs;
    }

    /* JADX WARNING: Removed duplicated region for block: B:51:0x0098 A:{SYNTHETIC, Splitter: B:51:0x0098} */
    /* JADX WARNING: Removed duplicated region for block: B:51:0x0098 A:{SYNTHETIC, Splitter: B:51:0x0098} */
    /* JADX WARNING: Removed duplicated region for block: B:57:0x00a4 A:{SYNTHETIC, Splitter: B:57:0x00a4} */
    private boolean canMoveFiles(java.io.File r12, java.io.File r13, int r14) {
        /*
        r11 = this;
        r9 = 1;
        r5 = 0;
        r7 = 0;
        r2 = 0;
        if (r14 != 0) goto L_0x0040;
    L_0x0006:
        r8 = new java.io.File;	 Catch:{ Exception -> 0x0092 }
        r10 = "000000000_999999_temp.jpg";
        r8.<init>(r12, r10);	 Catch:{ Exception -> 0x0092 }
        r3 = new java.io.File;	 Catch:{ Exception -> 0x00b0 }
        r10 = "000000000_999999.jpg";
        r3.<init>(r13, r10);	 Catch:{ Exception -> 0x00b0 }
        r2 = r3;
        r7 = r8;
    L_0x0018:
        r10 = 1024; // 0x400 float:1.435E-42 double:5.06E-321;
        r0 = new byte[r10];	 Catch:{ Exception -> 0x0092 }
        r7.createNewFile();	 Catch:{ Exception -> 0x0092 }
        r6 = new java.io.RandomAccessFile;	 Catch:{ Exception -> 0x0092 }
        r10 = "rws";
        r6.<init>(r7, r10);	 Catch:{ Exception -> 0x0092 }
        r6.write(r0);	 Catch:{ Exception -> 0x00b3, all -> 0x00ad }
        r6.close();	 Catch:{ Exception -> 0x00b3, all -> 0x00ad }
        r5 = 0;
        r1 = r7.renameTo(r2);	 Catch:{ Exception -> 0x0092 }
        r7.delete();	 Catch:{ Exception -> 0x0092 }
        r2.delete();	 Catch:{ Exception -> 0x0092 }
        if (r1 == 0) goto L_0x0086;
    L_0x003a:
        if (r5 == 0) goto L_0x003f;
    L_0x003c:
        r5.close();	 Catch:{ Exception -> 0x0081 }
    L_0x003f:
        return r9;
    L_0x0040:
        r10 = 3;
        if (r14 != r10) goto L_0x0056;
    L_0x0043:
        r8 = new java.io.File;	 Catch:{ Exception -> 0x0092 }
        r10 = "000000000_999999_temp.doc";
        r8.<init>(r12, r10);	 Catch:{ Exception -> 0x0092 }
        r3 = new java.io.File;	 Catch:{ Exception -> 0x00b0 }
        r10 = "000000000_999999.doc";
        r3.<init>(r13, r10);	 Catch:{ Exception -> 0x00b0 }
        r2 = r3;
        r7 = r8;
        goto L_0x0018;
    L_0x0056:
        if (r14 != r9) goto L_0x006b;
    L_0x0058:
        r8 = new java.io.File;	 Catch:{ Exception -> 0x0092 }
        r10 = "000000000_999999_temp.ogg";
        r8.<init>(r12, r10);	 Catch:{ Exception -> 0x0092 }
        r3 = new java.io.File;	 Catch:{ Exception -> 0x00b0 }
        r10 = "000000000_999999.ogg";
        r3.<init>(r13, r10);	 Catch:{ Exception -> 0x00b0 }
        r2 = r3;
        r7 = r8;
        goto L_0x0018;
    L_0x006b:
        r10 = 2;
        if (r14 != r10) goto L_0x0018;
    L_0x006e:
        r8 = new java.io.File;	 Catch:{ Exception -> 0x0092 }
        r10 = "000000000_999999_temp.mp4";
        r8.<init>(r12, r10);	 Catch:{ Exception -> 0x0092 }
        r3 = new java.io.File;	 Catch:{ Exception -> 0x00b0 }
        r10 = "000000000_999999.mp4";
        r3.<init>(r13, r10);	 Catch:{ Exception -> 0x00b0 }
        r2 = r3;
        r7 = r8;
        goto L_0x0018;
    L_0x0081:
        r4 = move-exception;
        org.telegram.messenger.FileLog.e(r4);
        goto L_0x003f;
    L_0x0086:
        if (r5 == 0) goto L_0x008b;
    L_0x0088:
        r5.close();	 Catch:{ Exception -> 0x008d }
    L_0x008b:
        r9 = 0;
        goto L_0x003f;
    L_0x008d:
        r4 = move-exception;
        org.telegram.messenger.FileLog.e(r4);
        goto L_0x008b;
    L_0x0092:
        r4 = move-exception;
    L_0x0093:
        org.telegram.messenger.FileLog.e(r4);	 Catch:{ all -> 0x00a1 }
        if (r5 == 0) goto L_0x008b;
    L_0x0098:
        r5.close();	 Catch:{ Exception -> 0x009c }
        goto L_0x008b;
    L_0x009c:
        r4 = move-exception;
        org.telegram.messenger.FileLog.e(r4);
        goto L_0x008b;
    L_0x00a1:
        r9 = move-exception;
    L_0x00a2:
        if (r5 == 0) goto L_0x00a7;
    L_0x00a4:
        r5.close();	 Catch:{ Exception -> 0x00a8 }
    L_0x00a7:
        throw r9;
    L_0x00a8:
        r4 = move-exception;
        org.telegram.messenger.FileLog.e(r4);
        goto L_0x00a7;
    L_0x00ad:
        r9 = move-exception;
        r5 = r6;
        goto L_0x00a2;
    L_0x00b0:
        r4 = move-exception;
        r7 = r8;
        goto L_0x0093;
    L_0x00b3:
        r4 = move-exception;
        r5 = r6;
        goto L_0x0093;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ImageLoader.canMoveFiles(java.io.File, java.io.File, int):boolean");
    }

    public Float getFileProgress(String location) {
        if (location == null) {
            return null;
        }
        return (Float) this.fileProgresses.get(location);
    }

    public String getReplacedKey(String oldKey) {
        if (oldKey == null) {
            return null;
        }
        return (String) this.replacedBitmaps.get(oldKey);
    }

    private void performReplace(String oldKey, String newKey) {
        BitmapDrawable b = this.memCache.get(oldKey);
        this.replacedBitmaps.put(oldKey, newKey);
        if (b != null) {
            BitmapDrawable oldBitmap = this.memCache.get(newKey);
            boolean dontChange = false;
            if (!(oldBitmap == null || oldBitmap.getBitmap() == null || b.getBitmap() == null)) {
                Bitmap oldBitmapObject = oldBitmap.getBitmap();
                Bitmap newBitmapObject = b.getBitmap();
                if (oldBitmapObject.getWidth() > newBitmapObject.getWidth() || oldBitmapObject.getHeight() > newBitmapObject.getHeight()) {
                    dontChange = true;
                }
            }
            if (dontChange) {
                this.memCache.remove(oldKey);
            } else {
                this.ignoreRemoval = oldKey;
                this.memCache.remove(oldKey);
                this.memCache.put(newKey, b);
                this.ignoreRemoval = null;
            }
        }
        Integer val = (Integer) this.bitmapUseCounts.get(oldKey);
        if (val != null) {
            this.bitmapUseCounts.put(newKey, val);
            this.bitmapUseCounts.remove(oldKey);
        }
    }

    public void incrementUseCount(String key) {
        Integer count = (Integer) this.bitmapUseCounts.get(key);
        if (count == null) {
            this.bitmapUseCounts.put(key, Integer.valueOf(1));
        } else {
            this.bitmapUseCounts.put(key, Integer.valueOf(count.intValue() + 1));
        }
    }

    public boolean decrementUseCount(String key) {
        Integer count = (Integer) this.bitmapUseCounts.get(key);
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
    }

    public boolean isInCache(String key) {
        return this.memCache.get(key) != null;
    }

    public void clearMemory() {
        this.memCache.evictAll();
    }

    private void removeFromWaitingForThumb(int TAG, ImageReceiver imageReceiver) {
        String location = (String) this.waitingForQualityThumbByTag.get(TAG);
        if (location != null) {
            ThumbGenerateInfo info = (ThumbGenerateInfo) this.waitingForQualityThumb.get(location);
            if (info != null) {
                info.imageReceiverArray.remove(imageReceiver);
                if (info.imageReceiverArray.isEmpty()) {
                    this.waitingForQualityThumb.remove(location);
                }
            }
            this.waitingForQualityThumbByTag.remove(TAG);
        }
    }

    public void cancelLoadingForImageReceiver(ImageReceiver imageReceiver, int type) {
        if (imageReceiver != null) {
            this.imageLoadQueue.postRunnable(new ImageLoader$$Lambda$1(this, type, imageReceiver));
        }
    }

    final /* synthetic */ void lambda$cancelLoadingForImageReceiver$2$ImageLoader(int type, ImageReceiver imageReceiver) {
        int start = 0;
        int count = 2;
        if (type == 1) {
            count = 1;
        } else if (type == 2) {
            start = 1;
        }
        int a = start;
        while (a < count) {
            int TAG = imageReceiver.getTag(a == 0);
            if (a == 0) {
                removeFromWaitingForThumb(TAG, imageReceiver);
            }
            if (TAG != 0) {
                CacheImage ei = (CacheImage) this.imageLoadingByTag.get(TAG);
                if (ei != null) {
                    ei.removeImageReceiver(imageReceiver);
                }
            }
            a++;
        }
    }

    public BitmapDrawable getAnyImageFromMemory(String key) {
        BitmapDrawable drawable = this.memCache.get(key);
        if (drawable != null) {
            return drawable;
        }
        ArrayList<String> filters = this.memCache.getFilterKeys(key);
        if (filters == null || filters.isEmpty()) {
            return drawable;
        }
        return this.memCache.get(key + "@" + ((String) filters.get(0)));
    }

    public BitmapDrawable getImageFromMemory(String key) {
        return this.memCache.get(key);
    }

    public BitmapDrawable getImageFromMemory(TLObject fileLocation, String httpUrl, String filter) {
        if (fileLocation == null && httpUrl == null) {
            return null;
        }
        String key = null;
        if (httpUrl != null) {
            key = Utilities.MD5(httpUrl);
        } else if (fileLocation instanceof FileLocation) {
            FileLocation location = (FileLocation) fileLocation;
            key = location.volume_id + "_" + location.local_id;
        } else if (fileLocation instanceof Document) {
            Document location2 = (Document) fileLocation;
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
        return this.memCache.get(key);
    }

    private void replaceImageInCacheInternal(String oldKey, String newKey, TLObject newLocation) {
        ArrayList<String> arr = this.memCache.getFilterKeys(oldKey);
        if (arr != null) {
            for (int a = 0; a < arr.size(); a++) {
                String filter = (String) arr.get(a);
                performReplace(oldKey + "@" + filter, newKey + "@" + filter);
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didReplacedPhotoInMemCache, oldK, newK, newLocation);
            }
            return;
        }
        performReplace(oldKey, newKey);
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didReplacedPhotoInMemCache, oldKey, newKey, newLocation);
    }

    public void replaceImageInCache(String oldKey, String newKey, TLObject newLocation, boolean post) {
        if (post) {
            AndroidUtilities.runOnUIThread(new ImageLoader$$Lambda$2(this, oldKey, newKey, newLocation));
        } else {
            replaceImageInCacheInternal(oldKey, newKey, newLocation);
        }
    }

    public void putImageToCache(BitmapDrawable bitmap, String key) {
        this.memCache.put(key, bitmap);
    }

    private void generateThumb(int mediaType, File originalPath, ThumbGenerateInfo info) {
        if ((mediaType == 0 || mediaType == 2 || mediaType == 3) && originalPath != null && info != null) {
            if (((ThumbGenerateTask) this.thumbGenerateTasks.get(FileLoader.getAttachFileName(info.parentDocument))) == null) {
                this.thumbGeneratingQueue.postRunnable(new ThumbGenerateTask(mediaType, originalPath, info));
            }
        }
    }

    public void cancelForceLoadingForImageReceiver(ImageReceiver imageReceiver) {
        if (imageReceiver != null) {
            String key = imageReceiver.getKey();
            if (key != null) {
                this.imageLoadQueue.postRunnable(new ImageLoader$$Lambda$3(this, key));
            }
        }
    }

    final /* synthetic */ void lambda$cancelForceLoadingForImageReceiver$4$ImageLoader(String key) {
        Integer num = (Integer) this.forceLoadingImages.remove(key);
    }

    private void createLoadOperationForImageReceiver(ImageReceiver imageReceiver, String key, String url, String ext, TLObject imageLocation, String httpLocation, String filter, int size, int cacheType, int thumb) {
        if (imageReceiver != null && url != null && key != null) {
            int TAG = imageReceiver.getTag(thumb != 0);
            if (TAG == 0) {
                TAG = this.lastImageNum;
                imageReceiver.setTag(TAG, thumb != 0);
                this.lastImageNum++;
                if (this.lastImageNum == Integer.MAX_VALUE) {
                    this.lastImageNum = 0;
                }
            }
            int finalTag = TAG;
            boolean finalIsNeedsQualityThumb = imageReceiver.isNeedsQualityThumb();
            this.imageLoadQueue.postRunnable(new ImageLoader$$Lambda$4(this, thumb, url, key, finalTag, imageReceiver, filter, httpLocation, imageReceiver.isCurrentKeyQuality(), imageReceiver.getParentObject(), finalIsNeedsQualityThumb, imageReceiver.isShouldGenerateQualityThumb(), imageLocation, cacheType, size, ext, imageReceiver.getcurrentAccount()));
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:48:0x0103  */
    /* JADX WARNING: Removed duplicated region for block: B:151:0x03f9  */
    /* JADX WARNING: Removed duplicated region for block: B:52:0x010d  */
    /* JADX WARNING: Removed duplicated region for block: B:55:0x0135  */
    /* JADX WARNING: Removed duplicated region for block: B:152:0x03fc  */
    /* JADX WARNING: Removed duplicated region for block: B:57:0x015a  */
    /* JADX WARNING: Removed duplicated region for block: B:153:0x03ff  */
    /* JADX WARNING: Removed duplicated region for block: B:65:0x0196  */
    /* JADX WARNING: Missing block: B:45:0x00fa, code:
            if (r36.equals("gif") != false) goto L_0x00fc;
     */
    final /* synthetic */ void lambda$createLoadOperationForImageReceiver$5$ImageLoader(int r38, java.lang.String r39, java.lang.String r40, int r41, org.telegram.messenger.ImageReceiver r42, java.lang.String r43, java.lang.String r44, boolean r45, java.lang.Object r46, boolean r47, boolean r48, org.telegram.tgnet.TLObject r49, int r50, int r51, java.lang.String r52, int r53) {
        /*
        r37 = this;
        r18 = 0;
        r4 = 2;
        r0 = r38;
        if (r0 == r4) goto L_0x005d;
    L_0x0007:
        r0 = r37;
        r4 = r0.imageLoadingByUrl;
        r0 = r39;
        r21 = r4.get(r0);
        r21 = (org.telegram.messenger.ImageLoader.CacheImage) r21;
        r0 = r37;
        r4 = r0.imageLoadingByKeys;
        r0 = r40;
        r19 = r4.get(r0);
        r19 = (org.telegram.messenger.ImageLoader.CacheImage) r19;
        r0 = r37;
        r4 = r0.imageLoadingByTag;
        r0 = r41;
        r20 = r4.get(r0);
        r20 = (org.telegram.messenger.ImageLoader.CacheImage) r20;
        if (r20 == 0) goto L_0x0035;
    L_0x002d:
        r0 = r20;
        r1 = r19;
        if (r0 != r1) goto L_0x01a2;
    L_0x0033:
        r18 = 1;
    L_0x0035:
        if (r18 != 0) goto L_0x0049;
    L_0x0037:
        if (r19 == 0) goto L_0x0049;
    L_0x0039:
        if (r38 == 0) goto L_0x01c7;
    L_0x003b:
        r4 = 1;
    L_0x003c:
        r0 = r19;
        r1 = r42;
        r2 = r40;
        r3 = r43;
        r0.addImageReceiver(r1, r2, r3, r4);
        r18 = 1;
    L_0x0049:
        if (r18 != 0) goto L_0x005d;
    L_0x004b:
        if (r21 == 0) goto L_0x005d;
    L_0x004d:
        if (r38 == 0) goto L_0x01ca;
    L_0x004f:
        r4 = 1;
    L_0x0050:
        r0 = r21;
        r1 = r42;
        r2 = r40;
        r3 = r43;
        r0.addImageReceiver(r1, r2, r3, r4);
        r18 = 1;
    L_0x005d:
        if (r18 != 0) goto L_0x01a1;
    L_0x005f:
        r32 = 0;
        r31 = 0;
        r24 = 0;
        r25 = 0;
        if (r44 == 0) goto L_0x0201;
    L_0x0069:
        r4 = "http";
        r0 = r44;
        r4 = r0.startsWith(r4);
        if (r4 != 0) goto L_0x00a8;
    L_0x0074:
        r4 = "athumb";
        r0 = r44;
        r4 = r0.startsWith(r4);
        if (r4 != 0) goto L_0x00a8;
    L_0x007f:
        r32 = 1;
        r4 = "thumb://";
        r0 = r44;
        r4 = r0.startsWith(r4);
        if (r4 == 0) goto L_0x01cd;
    L_0x008c:
        r4 = ":";
        r6 = 8;
        r0 = r44;
        r27 = r0.indexOf(r4, r6);
        if (r27 < 0) goto L_0x00a8;
    L_0x0099:
        r24 = new java.io.File;
        r4 = r27 + 1;
        r0 = r44;
        r4 = r0.substring(r4);
        r0 = r24;
        r0.<init>(r4);
    L_0x00a8:
        r4 = 2;
        r0 = r38;
        if (r0 == r4) goto L_0x01a1;
    L_0x00ad:
        r0 = r49;
        r4 = r0 instanceof org.telegram.tgnet.TLRPC.TL_documentEncrypted;
        if (r4 != 0) goto L_0x00b9;
    L_0x00b3:
        r0 = r49;
        r4 = r0 instanceof org.telegram.tgnet.TLRPC.TL_fileEncryptedLocation;
        if (r4 == 0) goto L_0x02f6;
    L_0x00b9:
        r30 = 1;
    L_0x00bb:
        r28 = new org.telegram.messenger.ImageLoader$CacheImage;
        r4 = 0;
        r0 = r28;
        r1 = r37;
        r0.<init>(r1, r4);
        if (r44 == 0) goto L_0x02fa;
    L_0x00c7:
        r4 = "vthumb";
        r0 = r44;
        r4 = r0.startsWith(r4);
        if (r4 != 0) goto L_0x02fa;
    L_0x00d2:
        r4 = "thumb";
        r0 = r44;
        r4 = r0.startsWith(r4);
        if (r4 != 0) goto L_0x02fa;
    L_0x00dd:
        r4 = "jpg";
        r0 = r44;
        r36 = getHttpUrlExtension(r0, r4);
        r4 = "mp4";
        r0 = r36;
        r4 = r0.equals(r4);
        if (r4 != 0) goto L_0x00fc;
    L_0x00f1:
        r4 = "gif";
        r0 = r36;
        r4 = r0.equals(r4);
        if (r4 == 0) goto L_0x0101;
    L_0x00fc:
        r4 = 1;
        r0 = r28;
        r0.animatedFile = r4;
    L_0x0101:
        if (r24 != 0) goto L_0x010b;
    L_0x0103:
        r0 = r49;
        r4 = r0 instanceof org.telegram.tgnet.TLRPC.TL_photoStrippedSize;
        if (r4 == 0) goto L_0x032b;
    L_0x0109:
        r32 = 1;
    L_0x010b:
        if (r38 == 0) goto L_0x03f9;
    L_0x010d:
        r4 = 1;
    L_0x010e:
        r0 = r28;
        r0.selfThumb = r4;
        r0 = r40;
        r1 = r28;
        r1.key = r0;
        r0 = r43;
        r1 = r28;
        r1.filter = r0;
        r0 = r44;
        r1 = r28;
        r1.httpUrl = r0;
        r0 = r52;
        r1 = r28;
        r1.ext = r0;
        r0 = r53;
        r1 = r28;
        r1.currentAccount = r0;
        r4 = 2;
        r0 = r50;
        if (r0 != r4) goto L_0x0158;
    L_0x0135:
        r4 = new java.io.File;
        r6 = org.telegram.messenger.FileLoader.getInternalCacheDir();
        r7 = new java.lang.StringBuilder;
        r7.<init>();
        r0 = r39;
        r7 = r7.append(r0);
        r8 = ".enc.key";
        r7 = r7.append(r8);
        r7 = r7.toString();
        r4.<init>(r6, r7);
        r0 = r28;
        r0.encryptionKeyPath = r4;
    L_0x0158:
        if (r38 == 0) goto L_0x03fc;
    L_0x015a:
        r4 = 1;
    L_0x015b:
        r0 = r28;
        r1 = r42;
        r2 = r40;
        r3 = r43;
        r0.addImageReceiver(r1, r2, r3, r4);
        if (r32 != 0) goto L_0x0170;
    L_0x0168:
        if (r25 != 0) goto L_0x0170;
    L_0x016a:
        r4 = r24.exists();
        if (r4 == 0) goto L_0x040c;
    L_0x0170:
        r0 = r24;
        r1 = r28;
        r1.finalFilePath = r0;
        r0 = r49;
        r1 = r28;
        r1.location = r0;
        r4 = new org.telegram.messenger.ImageLoader$CacheOutTask;
        r0 = r37;
        r1 = r28;
        r4.<init>(r1);
        r0 = r28;
        r0.cacheTask = r4;
        r0 = r37;
        r4 = r0.imageLoadingByKeys;
        r0 = r40;
        r1 = r28;
        r4.put(r0, r1);
        if (r38 == 0) goto L_0x03ff;
    L_0x0196:
        r0 = r37;
        r4 = r0.cacheThumbOutQueue;
        r0 = r28;
        r6 = r0.cacheTask;
        r4.postRunnable(r6);
    L_0x01a1:
        return;
    L_0x01a2:
        r0 = r20;
        r1 = r21;
        if (r0 != r1) goto L_0x01be;
    L_0x01a8:
        if (r19 != 0) goto L_0x01b8;
    L_0x01aa:
        if (r38 == 0) goto L_0x01bc;
    L_0x01ac:
        r4 = 1;
    L_0x01ad:
        r0 = r20;
        r1 = r42;
        r2 = r40;
        r3 = r43;
        r0.replaceImageReceiver(r1, r2, r3, r4);
    L_0x01b8:
        r18 = 1;
        goto L_0x0035;
    L_0x01bc:
        r4 = 0;
        goto L_0x01ad;
    L_0x01be:
        r0 = r20;
        r1 = r42;
        r0.removeImageReceiver(r1);
        goto L_0x0035;
    L_0x01c7:
        r4 = 0;
        goto L_0x003c;
    L_0x01ca:
        r4 = 0;
        goto L_0x0050;
    L_0x01cd:
        r4 = "vthumb://";
        r0 = r44;
        r4 = r0.startsWith(r4);
        if (r4 == 0) goto L_0x01f6;
    L_0x01d8:
        r4 = ":";
        r6 = 9;
        r0 = r44;
        r27 = r0.indexOf(r4, r6);
        if (r27 < 0) goto L_0x00a8;
    L_0x01e5:
        r24 = new java.io.File;
        r4 = r27 + 1;
        r0 = r44;
        r4 = r0.substring(r4);
        r0 = r24;
        r0.<init>(r4);
        goto L_0x00a8;
    L_0x01f6:
        r24 = new java.io.File;
        r0 = r24;
        r1 = r44;
        r0.<init>(r1);
        goto L_0x00a8;
    L_0x0201:
        if (r38 != 0) goto L_0x00a8;
    L_0x0203:
        if (r45 == 0) goto L_0x00a8;
    L_0x0205:
        r32 = 1;
        r34 = r46;
        r34 = (org.telegram.messenger.MessageObject) r34;
        r33 = r34.getDocument();
        if (r33 == 0) goto L_0x00a8;
    L_0x0211:
        if (r47 == 0) goto L_0x0255;
    L_0x0213:
        r24 = new java.io.File;
        r4 = 4;
        r4 = org.telegram.messenger.FileLoader.getDirectory(r4);
        r6 = new java.lang.StringBuilder;
        r6.<init>();
        r7 = "q_";
        r6 = r6.append(r7);
        r0 = r33;
        r7 = r0.dc_id;
        r6 = r6.append(r7);
        r7 = "_";
        r6 = r6.append(r7);
        r0 = r33;
        r8 = r0.id;
        r6 = r6.append(r8);
        r7 = ".jpg";
        r6 = r6.append(r7);
        r6 = r6.toString();
        r0 = r24;
        r0.<init>(r4, r6);
        r4 = r24.exists();
        if (r4 != 0) goto L_0x02f2;
    L_0x0253:
        r24 = 0;
    L_0x0255:
        r22 = 0;
        r0 = r34;
        r4 = r0.messageOwner;
        r4 = r4.attachPath;
        if (r4 == 0) goto L_0x0280;
    L_0x025f:
        r0 = r34;
        r4 = r0.messageOwner;
        r4 = r4.attachPath;
        r4 = r4.length();
        if (r4 <= 0) goto L_0x0280;
    L_0x026b:
        r22 = new java.io.File;
        r0 = r34;
        r4 = r0.messageOwner;
        r4 = r4.attachPath;
        r0 = r22;
        r0.<init>(r4);
        r4 = r22.exists();
        if (r4 != 0) goto L_0x0280;
    L_0x027e:
        r22 = 0;
    L_0x0280:
        if (r22 != 0) goto L_0x028a;
    L_0x0282:
        r0 = r34;
        r4 = r0.messageOwner;
        r22 = org.telegram.messenger.FileLoader.getPathToMessage(r4);
    L_0x028a:
        if (r24 != 0) goto L_0x00a8;
    L_0x028c:
        r5 = r34.getFileName();
        r0 = r37;
        r4 = r0.waitingForQualityThumb;
        r29 = r4.get(r5);
        r29 = (org.telegram.messenger.ImageLoader.ThumbGenerateInfo) r29;
        if (r29 != 0) goto L_0x02bd;
    L_0x029c:
        r29 = new org.telegram.messenger.ImageLoader$ThumbGenerateInfo;
        r4 = 0;
        r0 = r29;
        r1 = r37;
        r0.<init>(r1, r4);
        r0 = r29;
        r1 = r33;
        r0.parentDocument = r1;
        r0 = r29;
        r1 = r43;
        r0.filter = r1;
        r0 = r37;
        r4 = r0.waitingForQualityThumb;
        r0 = r29;
        r4.put(r5, r0);
    L_0x02bd:
        r4 = r29.imageReceiverArray;
        r0 = r42;
        r4 = r4.contains(r0);
        if (r4 != 0) goto L_0x02d2;
    L_0x02c9:
        r4 = r29.imageReceiverArray;
        r0 = r42;
        r4.add(r0);
    L_0x02d2:
        r0 = r37;
        r4 = r0.waitingForQualityThumbByTag;
        r0 = r41;
        r4.put(r0, r5);
        r4 = r22.exists();
        if (r4 == 0) goto L_0x01a1;
    L_0x02e1:
        if (r48 == 0) goto L_0x01a1;
    L_0x02e3:
        r4 = r34.getFileType();
        r0 = r37;
        r1 = r22;
        r2 = r29;
        r0.generateThumb(r4, r1, r2);
        goto L_0x01a1;
    L_0x02f2:
        r25 = 1;
        goto L_0x0255;
    L_0x02f6:
        r30 = 0;
        goto L_0x00bb;
    L_0x02fa:
        r0 = r49;
        r4 = r0 instanceof org.telegram.messenger.WebFile;
        if (r4 == 0) goto L_0x030a;
    L_0x0300:
        r4 = r49;
        r4 = (org.telegram.messenger.WebFile) r4;
        r4 = org.telegram.messenger.MessageObject.isGifDocument(r4);
        if (r4 != 0) goto L_0x0324;
    L_0x030a:
        r0 = r49;
        r4 = r0 instanceof org.telegram.tgnet.TLRPC.Document;
        if (r4 == 0) goto L_0x0101;
    L_0x0310:
        r4 = r49;
        r4 = (org.telegram.tgnet.TLRPC.Document) r4;
        r4 = org.telegram.messenger.MessageObject.isGifDocument(r4);
        if (r4 != 0) goto L_0x0324;
    L_0x031a:
        r4 = r49;
        r4 = (org.telegram.tgnet.TLRPC.Document) r4;
        r4 = org.telegram.messenger.MessageObject.isRoundVideoDocument(r4);
        if (r4 == 0) goto L_0x0101;
    L_0x0324:
        r4 = 1;
        r0 = r28;
        r0.animatedFile = r4;
        goto L_0x0101;
    L_0x032b:
        r0 = r49;
        r4 = r0 instanceof org.telegram.messenger.SecureDocument;
        if (r4 == 0) goto L_0x035a;
    L_0x0331:
        r4 = r49;
        r4 = (org.telegram.messenger.SecureDocument) r4;
        r0 = r28;
        r0.secureDocument = r4;
        r0 = r28;
        r4 = r0.secureDocument;
        r4 = r4.secureFile;
        r4 = r4.dc_id;
        r6 = -NUM; // 0xfffffffvar_ float:-0.0 double:NaN;
        if (r4 != r6) goto L_0x0357;
    L_0x0345:
        r32 = 1;
    L_0x0347:
        r24 = new java.io.File;
        r4 = 4;
        r4 = org.telegram.messenger.FileLoader.getDirectory(r4);
        r0 = r24;
        r1 = r39;
        r0.<init>(r4, r1);
        goto L_0x010b;
    L_0x0357:
        r32 = 0;
        goto L_0x0347;
    L_0x035a:
        if (r50 != 0) goto L_0x0362;
    L_0x035c:
        if (r51 <= 0) goto L_0x0362;
    L_0x035e:
        if (r44 != 0) goto L_0x0362;
    L_0x0360:
        if (r30 == 0) goto L_0x03a3;
    L_0x0362:
        r24 = new java.io.File;
        r4 = 4;
        r4 = org.telegram.messenger.FileLoader.getDirectory(r4);
        r0 = r24;
        r1 = r39;
        r0.<init>(r4, r1);
        r4 = r24.exists();
        if (r4 == 0) goto L_0x037a;
    L_0x0376:
        r25 = 1;
        goto L_0x010b;
    L_0x037a:
        r4 = 2;
        r0 = r50;
        if (r0 != r4) goto L_0x010b;
    L_0x037f:
        r24 = new java.io.File;
        r4 = 4;
        r4 = org.telegram.messenger.FileLoader.getDirectory(r4);
        r6 = new java.lang.StringBuilder;
        r6.<init>();
        r0 = r39;
        r6 = r6.append(r0);
        r7 = ".enc";
        r6 = r6.append(r7);
        r6 = r6.toString();
        r0 = r24;
        r0.<init>(r4, r6);
        goto L_0x010b;
    L_0x03a3:
        r0 = r49;
        r4 = r0 instanceof org.telegram.tgnet.TLRPC.Document;
        if (r4 == 0) goto L_0x03d3;
    L_0x03a9:
        r4 = r49;
        r4 = (org.telegram.tgnet.TLRPC.Document) r4;
        r4 = org.telegram.messenger.MessageObject.isVideoDocument(r4);
        if (r4 == 0) goto L_0x03c3;
    L_0x03b3:
        r24 = new java.io.File;
        r4 = 2;
        r4 = org.telegram.messenger.FileLoader.getDirectory(r4);
        r0 = r24;
        r1 = r39;
        r0.<init>(r4, r1);
        goto L_0x010b;
    L_0x03c3:
        r24 = new java.io.File;
        r4 = 3;
        r4 = org.telegram.messenger.FileLoader.getDirectory(r4);
        r0 = r24;
        r1 = r39;
        r0.<init>(r4, r1);
        goto L_0x010b;
    L_0x03d3:
        r0 = r49;
        r4 = r0 instanceof org.telegram.messenger.WebFile;
        if (r4 == 0) goto L_0x03e9;
    L_0x03d9:
        r24 = new java.io.File;
        r4 = 3;
        r4 = org.telegram.messenger.FileLoader.getDirectory(r4);
        r0 = r24;
        r1 = r39;
        r0.<init>(r4, r1);
        goto L_0x010b;
    L_0x03e9:
        r24 = new java.io.File;
        r4 = 0;
        r4 = org.telegram.messenger.FileLoader.getDirectory(r4);
        r0 = r24;
        r1 = r39;
        r0.<init>(r4, r1);
        goto L_0x010b;
    L_0x03f9:
        r4 = 0;
        goto L_0x010e;
    L_0x03fc:
        r4 = 0;
        goto L_0x015b;
    L_0x03ff:
        r0 = r37;
        r4 = r0.cacheOutQueue;
        r0 = r28;
        r6 = r0.cacheTask;
        r4.postRunnable(r6);
        goto L_0x01a1;
    L_0x040c:
        r0 = r39;
        r1 = r28;
        r1.url = r0;
        r0 = r49;
        r1 = r28;
        r1.location = r0;
        r0 = r37;
        r4 = r0.imageLoadingByUrl;
        r0 = r39;
        r1 = r28;
        r4.put(r0, r1);
        if (r44 != 0) goto L_0x04e4;
    L_0x0425:
        r0 = r49;
        r4 = r0 instanceof org.telegram.tgnet.TLRPC.FileLocation;
        if (r4 == 0) goto L_0x0464;
    L_0x042b:
        r5 = r49;
        r5 = (org.telegram.tgnet.TLRPC.FileLocation) r5;
        r10 = r50;
        if (r10 != 0) goto L_0x043a;
    L_0x0433:
        if (r51 <= 0) goto L_0x0439;
    L_0x0435:
        r4 = r5.key;
        if (r4 == 0) goto L_0x043a;
    L_0x0439:
        r10 = 1;
    L_0x043a:
        r4 = org.telegram.messenger.FileLoader.getInstance(r53);
        if (r38 == 0) goto L_0x0462;
    L_0x0440:
        r9 = 2;
    L_0x0441:
        r6 = r46;
        r7 = r52;
        r8 = r51;
        r4.loadFile(r5, r6, r7, r8, r9, r10);
    L_0x044a:
        r4 = r42.isForceLoding();
        if (r4 == 0) goto L_0x01a1;
    L_0x0450:
        r0 = r37;
        r4 = r0.forceLoadingImages;
        r0 = r28;
        r6 = r0.key;
        r7 = 0;
        r7 = java.lang.Integer.valueOf(r7);
        r4.put(r6, r7);
        goto L_0x01a1;
    L_0x0462:
        r9 = 1;
        goto L_0x0441;
    L_0x0464:
        r0 = r49;
        r4 = r0 instanceof org.telegram.tgnet.TLRPC.PhotoSize;
        if (r4 == 0) goto L_0x0498;
    L_0x046a:
        r35 = r49;
        r35 = (org.telegram.tgnet.TLRPC.PhotoSize) r35;
        r10 = r50;
        if (r10 != 0) goto L_0x047d;
    L_0x0472:
        if (r51 <= 0) goto L_0x047c;
    L_0x0474:
        r0 = r35;
        r4 = r0.location;
        r4 = r4.key;
        if (r4 == 0) goto L_0x047d;
    L_0x047c:
        r10 = 1;
    L_0x047d:
        r11 = org.telegram.messenger.FileLoader.getInstance(r53);
        r0 = r35;
        r12 = r0.location;
        if (r38 == 0) goto L_0x0495;
    L_0x0487:
        r16 = 2;
    L_0x0489:
        r13 = r46;
        r14 = r52;
        r15 = r51;
        r17 = r10;
        r11.loadFile(r12, r13, r14, r15, r16, r17);
        goto L_0x044a;
    L_0x0495:
        r16 = 1;
        goto L_0x0489;
    L_0x0498:
        r0 = r49;
        r4 = r0 instanceof org.telegram.tgnet.TLRPC.Document;
        if (r4 == 0) goto L_0x04b3;
    L_0x049e:
        r6 = org.telegram.messenger.FileLoader.getInstance(r53);
        r49 = (org.telegram.tgnet.TLRPC.Document) r49;
        if (r38 == 0) goto L_0x04b1;
    L_0x04a6:
        r4 = 2;
    L_0x04a7:
        r0 = r49;
        r1 = r46;
        r2 = r50;
        r6.loadFile(r0, r1, r4, r2);
        goto L_0x044a;
    L_0x04b1:
        r4 = 1;
        goto L_0x04a7;
    L_0x04b3:
        r0 = r49;
        r4 = r0 instanceof org.telegram.messenger.SecureDocument;
        if (r4 == 0) goto L_0x04ca;
    L_0x04b9:
        r6 = org.telegram.messenger.FileLoader.getInstance(r53);
        r49 = (org.telegram.messenger.SecureDocument) r49;
        if (r38 == 0) goto L_0x04c8;
    L_0x04c1:
        r4 = 2;
    L_0x04c2:
        r0 = r49;
        r6.loadFile(r0, r4);
        goto L_0x044a;
    L_0x04c8:
        r4 = 1;
        goto L_0x04c2;
    L_0x04ca:
        r0 = r49;
        r4 = r0 instanceof org.telegram.messenger.WebFile;
        if (r4 == 0) goto L_0x044a;
    L_0x04d0:
        r6 = org.telegram.messenger.FileLoader.getInstance(r53);
        r49 = (org.telegram.messenger.WebFile) r49;
        if (r38 == 0) goto L_0x04e2;
    L_0x04d8:
        r4 = 2;
    L_0x04d9:
        r0 = r49;
        r1 = r50;
        r6.loadFile(r0, r4, r1);
        goto L_0x044a;
    L_0x04e2:
        r4 = 1;
        goto L_0x04d9;
    L_0x04e4:
        r26 = org.telegram.messenger.Utilities.MD5(r44);
        r4 = 4;
        r23 = org.telegram.messenger.FileLoader.getDirectory(r4);
        r4 = new java.io.File;
        r6 = new java.lang.StringBuilder;
        r6.<init>();
        r0 = r26;
        r6 = r6.append(r0);
        r7 = "_temp.jpg";
        r6 = r6.append(r7);
        r6 = r6.toString();
        r0 = r23;
        r4.<init>(r0, r6);
        r0 = r28;
        r0.tempFilePath = r4;
        r0 = r24;
        r1 = r28;
        r1.finalFilePath = r0;
        r4 = "athumb";
        r0 = r44;
        r4 = r0.startsWith(r4);
        if (r4 == 0) goto L_0x053f;
    L_0x051f:
        r4 = new org.telegram.messenger.ImageLoader$ArtworkLoadTask;
        r0 = r37;
        r1 = r28;
        r4.<init>(r0, r1);
        r0 = r28;
        r0.artworkTask = r4;
        r0 = r37;
        r4 = r0.artworkTasks;
        r0 = r28;
        r6 = r0.artworkTask;
        r4.add(r6);
        r4 = 0;
        r0 = r37;
        r0.runArtworkTasks(r4);
        goto L_0x01a1;
    L_0x053f:
        r4 = new org.telegram.messenger.ImageLoader$HttpImageTask;
        r0 = r37;
        r1 = r28;
        r2 = r51;
        r4.<init>(r1, r2);
        r0 = r28;
        r0.httpTask = r4;
        r0 = r37;
        r4 = r0.httpTasks;
        r0 = r28;
        r6 = r0.httpTask;
        r4.add(r6);
        r4 = 0;
        r0 = r37;
        r0.runHttpTasks(r4);
        goto L_0x01a1;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ImageLoader.lambda$createLoadOperationForImageReceiver$5$ImageLoader(int, java.lang.String, java.lang.String, int, org.telegram.messenger.ImageReceiver, java.lang.String, java.lang.String, boolean, java.lang.Object, boolean, boolean, org.telegram.tgnet.TLObject, int, int, java.lang.String, int):void");
    }

    /* JADX WARNING: Removed duplicated region for block: B:99:0x0392  */
    /* JADX WARNING: Removed duplicated region for block: B:106:0x03bf  */
    public void loadImageForImageReceiver(org.telegram.messenger.ImageReceiver r45) {
        /*
        r44 = this;
        if (r45 != 0) goto L_0x0003;
    L_0x0002:
        return;
    L_0x0003:
        r36 = 0;
        r37 = r45.getKey();
        if (r37 == 0) goto L_0x0032;
    L_0x000b:
        r0 = r44;
        r4 = r0.memCache;
        r0 = r37;
        r31 = r4.get(r0);
        if (r31 == 0) goto L_0x0032;
    L_0x0017:
        r4 = 0;
        r0 = r44;
        r1 = r45;
        r0.cancelLoadingForImageReceiver(r1, r4);
        r4 = 0;
        r5 = 1;
        r0 = r45;
        r1 = r31;
        r2 = r37;
        r0.setImageBitmapByKey(r1, r2, r4, r5);
        r36 = 1;
        r4 = r45.isForcePreview();
        if (r4 == 0) goto L_0x0002;
    L_0x0032:
        r43 = 0;
        r6 = r45.getThumbKey();
        if (r6 == 0) goto L_0x005f;
    L_0x003a:
        r0 = r44;
        r4 = r0.memCache;
        r31 = r4.get(r6);
        if (r31 == 0) goto L_0x005f;
    L_0x0044:
        r4 = 1;
        r5 = 1;
        r0 = r45;
        r1 = r31;
        r0.setImageBitmapByKey(r1, r6, r4, r5);
        r4 = 1;
        r0 = r44;
        r1 = r45;
        r0.cancelLoadingForImageReceiver(r1, r4);
        if (r36 == 0) goto L_0x005d;
    L_0x0057:
        r4 = r45.isForcePreview();
        if (r4 != 0) goto L_0x0002;
    L_0x005d:
        r43 = 1;
    L_0x005f:
        r41 = 0;
        r39 = r45.getParentObject();
        r9 = r45.getThumbLocation();
        r25 = r45.getImageLocation();
        if (r25 != 0) goto L_0x008b;
    L_0x006f:
        r4 = r45.isNeedsQualityThumb();
        if (r4 == 0) goto L_0x008b;
    L_0x0075:
        r4 = r45.isCurrentKeyQuality();
        if (r4 == 0) goto L_0x008b;
    L_0x007b:
        r0 = r39;
        r4 = r0 instanceof org.telegram.messenger.MessageObject;
        if (r4 == 0) goto L_0x008b;
    L_0x0081:
        r4 = r39;
        r4 = (org.telegram.messenger.MessageObject) r4;
        r25 = r4.getDocument();
        r41 = 1;
    L_0x008b:
        r18 = r45.getHttpImageLocation();
        r42 = 0;
        r15 = 0;
        r7 = 0;
        r37 = 0;
        r6 = 0;
        r8 = r45.getExt();
        if (r8 != 0) goto L_0x009f;
    L_0x009c:
        r8 = "jpg";
    L_0x009f:
        if (r18 == 0) goto L_0x0172;
    L_0x00a1:
        r37 = org.telegram.messenger.Utilities.MD5(r18);
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r0 = r37;
        r4 = r4.append(r0);
        r5 = ".";
        r4 = r4.append(r5);
        r5 = "jpg";
        r0 = r18;
        r5 = getHttpUrlExtension(r0, r5);
        r4 = r4.append(r5);
        r15 = r4.toString();
    L_0x00c8:
        r4 = r9 instanceof org.telegram.tgnet.TLRPC.FileLocation;
        if (r4 == 0) goto L_0x0424;
    L_0x00cc:
        r38 = r9;
        r38 = (org.telegram.tgnet.TLRPC.FileLocation) r38;
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r0 = r38;
        r12 = r0.volume_id;
        r4 = r4.append(r12);
        r5 = "_";
        r4 = r4.append(r5);
        r0 = r38;
        r5 = r0.local_id;
        r4 = r4.append(r5);
        r6 = r4.toString();
    L_0x00f0:
        if (r6 == 0) goto L_0x010a;
    L_0x00f2:
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r4 = r4.append(r6);
        r5 = ".";
        r4 = r4.append(r5);
        r4 = r4.append(r8);
        r7 = r4.toString();
    L_0x010a:
        r19 = r45.getFilter();
        r11 = r45.getThumbFilter();
        if (r37 == 0) goto L_0x0132;
    L_0x0114:
        if (r19 == 0) goto L_0x0132;
    L_0x0116:
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r0 = r37;
        r4 = r4.append(r0);
        r5 = "@";
        r4 = r4.append(r5);
        r0 = r19;
        r4 = r4.append(r0);
        r37 = r4.toString();
    L_0x0132:
        if (r6 == 0) goto L_0x014e;
    L_0x0134:
        if (r11 == 0) goto L_0x014e;
    L_0x0136:
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r4 = r4.append(r6);
        r5 = "@";
        r4 = r4.append(r5);
        r4 = r4.append(r11);
        r6 = r4.toString();
    L_0x014e:
        if (r18 == 0) goto L_0x0477;
    L_0x0150:
        r10 = 0;
        r12 = 0;
        r13 = 1;
        if (r43 == 0) goto L_0x0474;
    L_0x0155:
        r14 = 2;
    L_0x0156:
        r4 = r44;
        r5 = r45;
        r4.createLoadOperationForImageReceiver(r5, r6, r7, r8, r9, r10, r11, r12, r13, r14);
        r17 = 0;
        r20 = 0;
        r21 = 1;
        r22 = 0;
        r12 = r44;
        r13 = r45;
        r14 = r37;
        r16 = r8;
        r12.createLoadOperationForImageReceiver(r13, r14, r15, r16, r17, r18, r19, r20, r21, r22);
        goto L_0x0002;
    L_0x0172:
        if (r25 == 0) goto L_0x00c8;
    L_0x0174:
        r0 = r25;
        r4 = r0 instanceof org.telegram.tgnet.TLRPC.FileLocation;
        if (r4 == 0) goto L_0x01e2;
    L_0x017a:
        r38 = r25;
        r38 = (org.telegram.tgnet.TLRPC.FileLocation) r38;
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r0 = r38;
        r12 = r0.volume_id;
        r4 = r4.append(r12);
        r5 = "_";
        r4 = r4.append(r5);
        r0 = r38;
        r5 = r0.local_id;
        r4 = r4.append(r5);
        r37 = r4.toString();
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r0 = r37;
        r4 = r4.append(r0);
        r5 = ".";
        r4 = r4.append(r5);
        r4 = r4.append(r8);
        r15 = r4.toString();
        r4 = r45.getExt();
        if (r4 != 0) goto L_0x01d5;
    L_0x01be:
        r0 = r38;
        r4 = r0.key;
        if (r4 != 0) goto L_0x01d5;
    L_0x01c4:
        r0 = r38;
        r4 = r0.volume_id;
        r12 = -NUM; // 0xfffffffvar_ float:-0.0 double:NaN;
        r4 = (r4 > r12 ? 1 : (r4 == r12 ? 0 : -1));
        if (r4 != 0) goto L_0x01d7;
    L_0x01cf:
        r0 = r38;
        r4 = r0.local_id;
        if (r4 >= 0) goto L_0x01d7;
    L_0x01d5:
        r42 = 1;
    L_0x01d7:
        r0 = r25;
        if (r0 != r9) goto L_0x00c8;
    L_0x01db:
        r25 = 0;
        r37 = 0;
        r15 = 0;
        goto L_0x00c8;
    L_0x01e2:
        r0 = r25;
        r4 = r0 instanceof org.telegram.tgnet.TLRPC.TL_photoStrippedSize;
        if (r4 == 0) goto L_0x021f;
    L_0x01e8:
        r38 = r25;
        r38 = (org.telegram.tgnet.TLRPC.TL_photoStrippedSize) r38;
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r5 = "stripped";
        r4 = r4.append(r5);
        r5 = org.telegram.messenger.FileRefController.getKeyForParentObject(r39);
        r4 = r4.append(r5);
        r37 = r4.toString();
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r0 = r37;
        r4 = r4.append(r0);
        r5 = ".";
        r4 = r4.append(r5);
        r4 = r4.append(r8);
        r15 = r4.toString();
        goto L_0x01d7;
    L_0x021f:
        r0 = r25;
        r4 = r0 instanceof org.telegram.tgnet.TLRPC.TL_photoSize;
        if (r4 == 0) goto L_0x028e;
    L_0x0225:
        r40 = r25;
        r40 = (org.telegram.tgnet.TLRPC.TL_photoSize) r40;
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r0 = r40;
        r5 = r0.location;
        r12 = r5.volume_id;
        r4 = r4.append(r12);
        r5 = "_";
        r4 = r4.append(r5);
        r0 = r40;
        r5 = r0.location;
        r5 = r5.local_id;
        r4 = r4.append(r5);
        r37 = r4.toString();
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r0 = r37;
        r4 = r4.append(r0);
        r5 = ".";
        r4 = r4.append(r5);
        r4 = r4.append(r8);
        r15 = r4.toString();
        r4 = r45.getExt();
        if (r4 != 0) goto L_0x028a;
    L_0x026d:
        r0 = r40;
        r4 = r0.location;
        r4 = r4.key;
        if (r4 != 0) goto L_0x028a;
    L_0x0275:
        r0 = r40;
        r4 = r0.location;
        r4 = r4.volume_id;
        r12 = -NUM; // 0xfffffffvar_ float:-0.0 double:NaN;
        r4 = (r4 > r12 ? 1 : (r4 == r12 ? 0 : -1));
        if (r4 != 0) goto L_0x01d7;
    L_0x0282:
        r0 = r40;
        r4 = r0.location;
        r4 = r4.local_id;
        if (r4 >= 0) goto L_0x01d7;
    L_0x028a:
        r42 = 1;
        goto L_0x01d7;
    L_0x028e:
        r0 = r25;
        r4 = r0 instanceof org.telegram.messenger.WebFile;
        if (r4 == 0) goto L_0x02ce;
    L_0x0294:
        r34 = r25;
        r34 = (org.telegram.messenger.WebFile) r34;
        r0 = r34;
        r4 = r0.mime_type;
        r32 = org.telegram.messenger.FileLoader.getExtensionByMime(r4);
        r0 = r34;
        r4 = r0.url;
        r37 = org.telegram.messenger.Utilities.MD5(r4);
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r0 = r37;
        r4 = r4.append(r0);
        r5 = ".";
        r4 = r4.append(r5);
        r0 = r34;
        r5 = r0.url;
        r0 = r32;
        r5 = getHttpUrlExtension(r5, r0);
        r4 = r4.append(r5);
        r15 = r4.toString();
        goto L_0x01d7;
    L_0x02ce:
        r0 = r25;
        r4 = r0 instanceof org.telegram.messenger.SecureDocument;
        if (r4 == 0) goto L_0x0332;
    L_0x02d4:
        r34 = r25;
        r34 = (org.telegram.messenger.SecureDocument) r34;
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r0 = r34;
        r5 = r0.secureFile;
        r5 = r5.dc_id;
        r4 = r4.append(r5);
        r5 = "_";
        r4 = r4.append(r5);
        r0 = r34;
        r5 = r0.secureFile;
        r12 = r5.id;
        r4 = r4.append(r12);
        r37 = r4.toString();
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r0 = r37;
        r4 = r4.append(r0);
        r5 = ".";
        r4 = r4.append(r5);
        r4 = r4.append(r8);
        r15 = r4.toString();
        if (r6 == 0) goto L_0x01d7;
    L_0x0318:
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r4 = r4.append(r6);
        r5 = ".";
        r4 = r4.append(r5);
        r4 = r4.append(r8);
        r7 = r4.toString();
        goto L_0x01d7;
    L_0x0332:
        r0 = r25;
        r4 = r0 instanceof org.telegram.tgnet.TLRPC.Document;
        if (r4 == 0) goto L_0x01d7;
    L_0x0338:
        r34 = r25;
        r34 = (org.telegram.tgnet.TLRPC.Document) r34;
        r0 = r34;
        r4 = r0.id;
        r12 = 0;
        r4 = (r4 > r12 ? 1 : (r4 == r12 ? 0 : -1));
        if (r4 == 0) goto L_0x01d7;
    L_0x0346:
        r0 = r34;
        r4 = r0.dc_id;
        if (r4 == 0) goto L_0x01d7;
    L_0x034c:
        if (r41 == 0) goto L_0x03f1;
    L_0x034e:
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r5 = "q_";
        r4 = r4.append(r5);
        r0 = r34;
        r5 = r0.dc_id;
        r4 = r4.append(r5);
        r5 = "_";
        r4 = r4.append(r5);
        r0 = r34;
        r12 = r0.id;
        r4 = r4.append(r12);
        r37 = r4.toString();
    L_0x0375:
        r33 = org.telegram.messenger.FileLoader.getDocumentFileName(r34);
        if (r33 == 0) goto L_0x0388;
    L_0x037b:
        r4 = 46;
        r0 = r33;
        r35 = r0.lastIndexOf(r4);
        r4 = -1;
        r0 = r35;
        if (r0 != r4) goto L_0x0413;
    L_0x0388:
        r33 = "";
    L_0x038b:
        r4 = r33.length();
        r5 = 1;
        if (r4 > r5) goto L_0x03a8;
    L_0x0392:
        r0 = r34;
        r4 = r0.mime_type;
        if (r4 == 0) goto L_0x041d;
    L_0x0398:
        r0 = r34;
        r4 = r0.mime_type;
        r5 = "video/mp4";
        r4 = r4.equals(r5);
        if (r4 == 0) goto L_0x041d;
    L_0x03a5:
        r33 = ".mp4";
    L_0x03a8:
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r0 = r37;
        r4 = r4.append(r0);
        r0 = r33;
        r4 = r4.append(r0);
        r15 = r4.toString();
        if (r6 == 0) goto L_0x03d7;
    L_0x03bf:
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r4 = r4.append(r6);
        r5 = ".";
        r4 = r4.append(r5);
        r4 = r4.append(r8);
        r7 = r4.toString();
    L_0x03d7:
        r4 = org.telegram.messenger.MessageObject.isGifDocument(r34);
        if (r4 != 0) goto L_0x0421;
    L_0x03dd:
        r4 = r25;
        r4 = (org.telegram.tgnet.TLRPC.Document) r4;
        r4 = org.telegram.messenger.MessageObject.isRoundVideoDocument(r4);
        if (r4 != 0) goto L_0x0421;
    L_0x03e7:
        r4 = org.telegram.messenger.MessageObject.canPreviewDocument(r34);
        if (r4 != 0) goto L_0x0421;
    L_0x03ed:
        r42 = 1;
    L_0x03ef:
        goto L_0x01d7;
    L_0x03f1:
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r0 = r34;
        r5 = r0.dc_id;
        r4 = r4.append(r5);
        r5 = "_";
        r4 = r4.append(r5);
        r0 = r34;
        r12 = r0.id;
        r4 = r4.append(r12);
        r37 = r4.toString();
        goto L_0x0375;
    L_0x0413:
        r0 = r33;
        r1 = r35;
        r33 = r0.substring(r1);
        goto L_0x038b;
    L_0x041d:
        r33 = "";
        goto L_0x03a8;
    L_0x0421:
        r42 = 0;
        goto L_0x03ef;
    L_0x0424:
        r4 = r9 instanceof org.telegram.tgnet.TLRPC.TL_photoStrippedSize;
        if (r4 == 0) goto L_0x0446;
    L_0x0428:
        r38 = r9;
        r38 = (org.telegram.tgnet.TLRPC.TL_photoStrippedSize) r38;
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r5 = "stripped";
        r4 = r4.append(r5);
        r5 = org.telegram.messenger.FileRefController.getKeyForParentObject(r39);
        r4 = r4.append(r5);
        r6 = r4.toString();
        goto L_0x00f0;
    L_0x0446:
        r4 = r9 instanceof org.telegram.tgnet.TLRPC.TL_photoSize;
        if (r4 == 0) goto L_0x00f0;
    L_0x044a:
        r40 = r9;
        r40 = (org.telegram.tgnet.TLRPC.TL_photoSize) r40;
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r0 = r40;
        r5 = r0.location;
        r12 = r5.volume_id;
        r4 = r4.append(r12);
        r5 = "_";
        r4 = r4.append(r5);
        r0 = r40;
        r5 = r0.location;
        r5 = r5.local_id;
        r4 = r4.append(r5);
        r6 = r4.toString();
        goto L_0x00f0;
    L_0x0474:
        r14 = 1;
        goto L_0x0156;
    L_0x0477:
        r29 = r45.getCacheType();
        if (r29 != 0) goto L_0x0481;
    L_0x047d:
        if (r42 == 0) goto L_0x0481;
    L_0x047f:
        r29 = 1;
    L_0x0481:
        r10 = 0;
        r12 = 0;
        if (r29 != 0) goto L_0x04a9;
    L_0x0485:
        r13 = 1;
    L_0x0486:
        if (r43 == 0) goto L_0x04ac;
    L_0x0488:
        r14 = 2;
    L_0x0489:
        r4 = r44;
        r5 = r45;
        r4.createLoadOperationForImageReceiver(r5, r6, r7, r8, r9, r10, r11, r12, r13, r14);
        r26 = 0;
        r28 = r45.getSize();
        r30 = 0;
        r20 = r44;
        r21 = r45;
        r22 = r37;
        r23 = r15;
        r24 = r8;
        r27 = r19;
        r20.createLoadOperationForImageReceiver(r21, r22, r23, r24, r25, r26, r27, r28, r29, r30);
        goto L_0x0002;
    L_0x04a9:
        r13 = r29;
        goto L_0x0486;
    L_0x04ac:
        r14 = 1;
        goto L_0x0489;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ImageLoader.loadImageForImageReceiver(org.telegram.messenger.ImageReceiver):void");
    }

    private void httpFileLoadError(String location) {
        this.imageLoadQueue.postRunnable(new ImageLoader$$Lambda$5(this, location));
    }

    final /* synthetic */ void lambda$httpFileLoadError$6$ImageLoader(String location) {
        CacheImage img = (CacheImage) this.imageLoadingByUrl.get(location);
        if (img != null) {
            HttpImageTask oldTask = img.httpTask;
            img.httpTask = new HttpImageTask(oldTask.cacheImage, oldTask.imageSize);
            this.httpTasks.add(img.httpTask);
            runHttpTasks(false);
        }
    }

    private void artworkLoadError(String location) {
        this.imageLoadQueue.postRunnable(new ImageLoader$$Lambda$6(this, location));
    }

    final /* synthetic */ void lambda$artworkLoadError$7$ImageLoader(String location) {
        CacheImage img = (CacheImage) this.imageLoadingByUrl.get(location);
        if (img != null) {
            img.artworkTask = new ArtworkLoadTask(this, img.artworkTask.cacheImage);
            this.artworkTasks.add(img.artworkTask);
            runArtworkTasks(false);
        }
    }

    private void fileDidLoaded(String location, File finalFile, int type) {
        this.imageLoadQueue.postRunnable(new ImageLoader$$Lambda$7(this, location, type, finalFile));
    }

    final /* synthetic */ void lambda$fileDidLoaded$8$ImageLoader(String location, int type, File finalFile) {
        ThumbGenerateInfo info = (ThumbGenerateInfo) this.waitingForQualityThumb.get(location);
        if (!(info == null || info.parentDocument == null)) {
            generateThumb(type, finalFile, info);
            this.waitingForQualityThumb.remove(location);
        }
        CacheImage img = (CacheImage) this.imageLoadingByUrl.get(location);
        if (img != null) {
            int a;
            this.imageLoadingByUrl.remove(location);
            ArrayList<CacheOutTask> tasks = new ArrayList();
            for (a = 0; a < img.imageReceiverArray.size(); a++) {
                String key = (String) img.keys.get(a);
                String filter = (String) img.filters.get(a);
                Boolean thumb = (Boolean) img.thumbs.get(a);
                ImageReceiver imageReceiver = (ImageReceiver) img.imageReceiverArray.get(a);
                CacheImage cacheImage = (CacheImage) this.imageLoadingByKeys.get(key);
                if (cacheImage == null) {
                    cacheImage = new CacheImage(this, null);
                    cacheImage.secureDocument = img.secureDocument;
                    cacheImage.currentAccount = img.currentAccount;
                    cacheImage.finalFilePath = finalFile;
                    cacheImage.key = key;
                    cacheImage.httpUrl = img.httpUrl;
                    cacheImage.selfThumb = thumb.booleanValue();
                    cacheImage.ext = img.ext;
                    cacheImage.encryptionKeyPath = img.encryptionKeyPath;
                    cacheImage.cacheTask = new CacheOutTask(cacheImage);
                    cacheImage.filter = filter;
                    cacheImage.animatedFile = img.animatedFile;
                    this.imageLoadingByKeys.put(key, cacheImage);
                    tasks.add(cacheImage.cacheTask);
                }
                cacheImage.addImageReceiver(imageReceiver, key, filter, thumb.booleanValue());
            }
            for (a = 0; a < tasks.size(); a++) {
                CacheOutTask task = (CacheOutTask) tasks.get(a);
                if (task.cacheImage.selfThumb) {
                    this.cacheThumbOutQueue.postRunnable(task);
                } else {
                    this.cacheOutQueue.postRunnable(task);
                }
            }
        }
    }

    private void fileDidFailedLoad(String location, int canceled) {
        if (canceled != 1) {
            this.imageLoadQueue.postRunnable(new ImageLoader$$Lambda$8(this, location));
        }
    }

    final /* synthetic */ void lambda$fileDidFailedLoad$9$ImageLoader(String location) {
        CacheImage img = (CacheImage) this.imageLoadingByUrl.get(location);
        if (img != null) {
            img.setImageAndClear(null);
        }
    }

    private void runHttpTasks(boolean complete) {
        if (complete) {
            this.currentHttpTasksCount--;
        }
        while (this.currentHttpTasksCount < 4 && !this.httpTasks.isEmpty()) {
            HttpImageTask task = (HttpImageTask) this.httpTasks.poll();
            if (task != null) {
                task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                this.currentHttpTasksCount++;
            }
        }
    }

    private void runArtworkTasks(boolean complete) {
        if (complete) {
            this.currentArtworkTasksCount--;
        }
        while (this.currentArtworkTasksCount < 4 && !this.artworkTasks.isEmpty()) {
            try {
                ((ArtworkLoadTask) this.artworkTasks.poll()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
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
        return new File(FileLoader.getDirectory(4), Utilities.MD5(url) + "." + getHttpUrlExtension(url, defaultExt));
    }

    public void loadHttpFile(String url, String defaultExt, int currentAccount) {
        if (url != null && url.length() != 0 && !this.httpFileLoadTasksByKeys.containsKey(url)) {
            String ext = getHttpUrlExtension(url, defaultExt);
            File file = new File(FileLoader.getDirectory(4), Utilities.MD5(url) + "_temp." + ext);
            file.delete();
            HttpFileTask task = new HttpFileTask(url, file, ext, currentAccount);
            this.httpFileLoadTasks.add(task);
            this.httpFileLoadTasksByKeys.put(url, task);
            runHttpFileLoadTasks(null, 0);
        }
    }

    public void cancelLoadHttpFile(String url) {
        HttpFileTask task = (HttpFileTask) this.httpFileLoadTasksByKeys.get(url);
        if (task != null) {
            task.cancel(true);
            this.httpFileLoadTasksByKeys.remove(url);
            this.httpFileLoadTasks.remove(task);
        }
        Runnable runnable = (Runnable) this.retryHttpsTasks.get(url);
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
        }
        runHttpFileLoadTasks(null, 0);
    }

    private void runHttpFileLoadTasks(HttpFileTask oldTask, int reason) {
        AndroidUtilities.runOnUIThread(new ImageLoader$$Lambda$9(this, oldTask, reason));
    }

    final /* synthetic */ void lambda$runHttpFileLoadTasks$11$ImageLoader(HttpFileTask oldTask, int reason) {
        if (oldTask != null) {
            this.currentHttpFileLoadTasksCount--;
        }
        if (oldTask != null) {
            if (reason == 1) {
                if (oldTask.canRetry) {
                    Runnable runnable = new ImageLoader$$Lambda$10(this, new HttpFileTask(oldTask.url, oldTask.tempFile, oldTask.ext, oldTask.currentAccount));
                    this.retryHttpsTasks.put(oldTask.url, runnable);
                    AndroidUtilities.runOnUIThread(runnable, 1000);
                } else {
                    this.httpFileLoadTasksByKeys.remove(oldTask.url);
                    NotificationCenter.getInstance(oldTask.currentAccount).postNotificationName(NotificationCenter.httpFileDidFailedLoad, oldTask.url, Integer.valueOf(0));
                }
            } else if (reason == 2) {
                this.httpFileLoadTasksByKeys.remove(oldTask.url);
                File file = new File(FileLoader.getDirectory(4), Utilities.MD5(oldTask.url) + "." + oldTask.ext);
                String result = oldTask.tempFile.renameTo(file) ? file.toString() : oldTask.tempFile.toString();
                NotificationCenter.getInstance(oldTask.currentAccount).postNotificationName(NotificationCenter.httpFileDidLoad, oldTask.url, result);
            }
        }
        while (this.currentHttpFileLoadTasksCount < 2 && !this.httpFileLoadTasks.isEmpty()) {
            ((HttpFileTask) this.httpFileLoadTasks.poll()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
            this.currentHttpFileLoadTasksCount++;
        }
    }

    final /* synthetic */ void lambda$null$10$ImageLoader(HttpFileTask newTask) {
        this.httpFileLoadTasks.add(newTask);
        runHttpFileLoadTasks(null, 0);
    }

    public static boolean shouldSendImageAsDocument(String path, Uri uri) {
        Options bmOptions = new Options();
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
                BitmapFactory.decodeStream(inputStream, null, bmOptions);
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

    public static android.graphics.Bitmap loadBitmap(java.lang.String r23, android.net.Uri r24, float r25, float r26, boolean r27) {
        /*
        r8 = new android.graphics.BitmapFactory$Options;
        r8.<init>();
        r2 = 1;
        r8.inJustDecodeBounds = r2;
        r15 = 0;
        if (r23 != 0) goto L_0x0025;
    L_0x000b:
        if (r24 == 0) goto L_0x0025;
    L_0x000d:
        r2 = r24.getScheme();
        if (r2 == 0) goto L_0x0025;
    L_0x0013:
        r14 = 0;
        r2 = r24.getScheme();
        r3 = "file";
        r2 = r2.contains(r3);
        if (r2 == 0) goto L_0x0061;
    L_0x0021:
        r23 = r24.getPath();
    L_0x0025:
        if (r23 == 0) goto L_0x006b;
    L_0x0027:
        r0 = r23;
        android.graphics.BitmapFactory.decodeFile(r0, r8);
    L_0x002c:
        r2 = r8.outWidth;
        r0 = (float) r2;
        r20 = r0;
        r2 = r8.outHeight;
        r0 = (float) r2;
        r19 = r0;
        if (r27 == 0) goto L_0x0094;
    L_0x0038:
        r2 = r20 / r25;
        r3 = r19 / r26;
        r22 = java.lang.Math.max(r2, r3);
    L_0x0040:
        r2 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r2 = (r22 > r2 ? 1 : (r22 == r2 ? 0 : -1));
        if (r2 >= 0) goto L_0x0048;
    L_0x0046:
        r22 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
    L_0x0048:
        r2 = 0;
        r8.inJustDecodeBounds = r2;
        r0 = r22;
        r2 = (int) r0;
        r8.inSampleSize = r2;
        r2 = r8.inSampleSize;
        r2 = r2 % 2;
        if (r2 == 0) goto L_0x00a1;
    L_0x0056:
        r21 = 1;
    L_0x0058:
        r2 = r21 * 2;
        r3 = r8.inSampleSize;
        if (r2 >= r3) goto L_0x009d;
    L_0x005e:
        r21 = r21 * 2;
        goto L_0x0058;
    L_0x0061:
        r23 = org.telegram.messenger.AndroidUtilities.getPath(r24);	 Catch:{ Throwable -> 0x0066 }
        goto L_0x0025;
    L_0x0066:
        r9 = move-exception;
        org.telegram.messenger.FileLog.e(r9);
        goto L_0x0025;
    L_0x006b:
        if (r24 == 0) goto L_0x002c;
    L_0x006d:
        r11 = 0;
        r2 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Throwable -> 0x008e }
        r2 = r2.getContentResolver();	 Catch:{ Throwable -> 0x008e }
        r0 = r24;
        r15 = r2.openInputStream(r0);	 Catch:{ Throwable -> 0x008e }
        r2 = 0;
        android.graphics.BitmapFactory.decodeStream(r15, r2, r8);	 Catch:{ Throwable -> 0x008e }
        r15.close();	 Catch:{ Throwable -> 0x008e }
        r2 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Throwable -> 0x008e }
        r2 = r2.getContentResolver();	 Catch:{ Throwable -> 0x008e }
        r0 = r24;
        r15 = r2.openInputStream(r0);	 Catch:{ Throwable -> 0x008e }
        goto L_0x002c;
    L_0x008e:
        r9 = move-exception;
        org.telegram.messenger.FileLog.e(r9);
        r1 = 0;
    L_0x0093:
        return r1;
    L_0x0094:
        r2 = r20 / r25;
        r3 = r19 / r26;
        r22 = java.lang.Math.min(r2, r3);
        goto L_0x0040;
    L_0x009d:
        r0 = r21;
        r8.inSampleSize = r0;
    L_0x00a1:
        r2 = android.os.Build.VERSION.SDK_INT;
        r3 = 21;
        if (r2 >= r3) goto L_0x00f4;
    L_0x00a7:
        r2 = 1;
    L_0x00a8:
        r8.inPurgeable = r2;
        r13 = 0;
        if (r23 == 0) goto L_0x00f6;
    L_0x00ad:
        r13 = r23;
    L_0x00af:
        r6 = 0;
        if (r13 == 0) goto L_0x00c9;
    L_0x00b2:
        r12 = new android.support.media.ExifInterface;	 Catch:{ Throwable -> 0x01a3 }
        r12.<init>(r13);	 Catch:{ Throwable -> 0x01a3 }
        r2 = "Orientation";
        r3 = 1;
        r18 = r12.getAttributeInt(r2, r3);	 Catch:{ Throwable -> 0x01a3 }
        r16 = new android.graphics.Matrix;	 Catch:{ Throwable -> 0x01a3 }
        r16.<init>();	 Catch:{ Throwable -> 0x01a3 }
        switch(r18) {
            case 3: goto L_0x0109;
            case 4: goto L_0x00c7;
            case 5: goto L_0x00c7;
            case 6: goto L_0x00fd;
            case 7: goto L_0x00c7;
            case 8: goto L_0x0111;
            default: goto L_0x00c7;
        };
    L_0x00c7:
        r6 = r16;
    L_0x00c9:
        r1 = 0;
        if (r23 == 0) goto L_0x0157;
    L_0x00cc:
        r0 = r23;
        r1 = android.graphics.BitmapFactory.decodeFile(r0, r8);	 Catch:{ Throwable -> 0x0119 }
        if (r1 == 0) goto L_0x0093;
    L_0x00d4:
        r2 = r8.inPurgeable;	 Catch:{ Throwable -> 0x0119 }
        if (r2 == 0) goto L_0x00db;
    L_0x00d8:
        org.telegram.messenger.Utilities.pinBitmap(r1);	 Catch:{ Throwable -> 0x0119 }
    L_0x00db:
        r2 = 0;
        r3 = 0;
        r4 = r1.getWidth();	 Catch:{ Throwable -> 0x0119 }
        r5 = r1.getHeight();	 Catch:{ Throwable -> 0x0119 }
        r7 = 1;
        r17 = org.telegram.messenger.Bitmaps.createBitmap(r1, r2, r3, r4, r5, r6, r7);	 Catch:{ Throwable -> 0x0119 }
        r0 = r17;
        if (r0 == r1) goto L_0x0093;
    L_0x00ee:
        r1.recycle();	 Catch:{ Throwable -> 0x0119 }
        r1 = r17;
        goto L_0x0093;
    L_0x00f4:
        r2 = 0;
        goto L_0x00a8;
    L_0x00f6:
        if (r24 == 0) goto L_0x00af;
    L_0x00f8:
        r13 = org.telegram.messenger.AndroidUtilities.getPath(r24);
        goto L_0x00af;
    L_0x00fd:
        r2 = NUM; // 0x42b40000 float:90.0 double:5.529052754E-315;
        r0 = r16;
        r0.postRotate(r2);	 Catch:{ Throwable -> 0x0105 }
        goto L_0x00c7;
    L_0x0105:
        r2 = move-exception;
        r6 = r16;
        goto L_0x00c9;
    L_0x0109:
        r2 = NUM; // 0x43340000 float:180.0 double:5.570497984E-315;
        r0 = r16;
        r0.postRotate(r2);	 Catch:{ Throwable -> 0x0105 }
        goto L_0x00c7;
    L_0x0111:
        r2 = NUM; // 0x43870000 float:270.0 double:5.597372625E-315;
        r0 = r16;
        r0.postRotate(r2);	 Catch:{ Throwable -> 0x0105 }
        goto L_0x00c7;
    L_0x0119:
        r9 = move-exception;
        org.telegram.messenger.FileLog.e(r9);
        r2 = getInstance();
        r2.clearMemory();
        if (r1 != 0) goto L_0x0135;
    L_0x0126:
        r0 = r23;
        r1 = android.graphics.BitmapFactory.decodeFile(r0, r8);	 Catch:{ Throwable -> 0x0151 }
        if (r1 == 0) goto L_0x0135;
    L_0x012e:
        r2 = r8.inPurgeable;	 Catch:{ Throwable -> 0x0151 }
        if (r2 == 0) goto L_0x0135;
    L_0x0132:
        org.telegram.messenger.Utilities.pinBitmap(r1);	 Catch:{ Throwable -> 0x0151 }
    L_0x0135:
        if (r1 == 0) goto L_0x0093;
    L_0x0137:
        r2 = 0;
        r3 = 0;
        r4 = r1.getWidth();	 Catch:{ Throwable -> 0x0151 }
        r5 = r1.getHeight();	 Catch:{ Throwable -> 0x0151 }
        r7 = 1;
        r17 = org.telegram.messenger.Bitmaps.createBitmap(r1, r2, r3, r4, r5, r6, r7);	 Catch:{ Throwable -> 0x0151 }
        r0 = r17;
        if (r0 == r1) goto L_0x0093;
    L_0x014a:
        r1.recycle();	 Catch:{ Throwable -> 0x0151 }
        r1 = r17;
        goto L_0x0093;
    L_0x0151:
        r10 = move-exception;
        org.telegram.messenger.FileLog.e(r10);
        goto L_0x0093;
    L_0x0157:
        if (r24 == 0) goto L_0x0093;
    L_0x0159:
        r2 = 0;
        r1 = android.graphics.BitmapFactory.decodeStream(r15, r2, r8);	 Catch:{ Throwable -> 0x018a }
        if (r1 == 0) goto L_0x017f;
    L_0x0160:
        r2 = r8.inPurgeable;	 Catch:{ Throwable -> 0x018a }
        if (r2 == 0) goto L_0x0167;
    L_0x0164:
        org.telegram.messenger.Utilities.pinBitmap(r1);	 Catch:{ Throwable -> 0x018a }
    L_0x0167:
        r2 = 0;
        r3 = 0;
        r4 = r1.getWidth();	 Catch:{ Throwable -> 0x018a }
        r5 = r1.getHeight();	 Catch:{ Throwable -> 0x018a }
        r7 = 1;
        r17 = org.telegram.messenger.Bitmaps.createBitmap(r1, r2, r3, r4, r5, r6, r7);	 Catch:{ Throwable -> 0x018a }
        r0 = r17;
        if (r0 == r1) goto L_0x017f;
    L_0x017a:
        r1.recycle();	 Catch:{ Throwable -> 0x018a }
        r1 = r17;
    L_0x017f:
        r15.close();	 Catch:{ Throwable -> 0x0184 }
        goto L_0x0093;
    L_0x0184:
        r9 = move-exception;
        org.telegram.messenger.FileLog.e(r9);
        goto L_0x0093;
    L_0x018a:
        r9 = move-exception;
        org.telegram.messenger.FileLog.e(r9);	 Catch:{ all -> 0x0199 }
        r15.close();	 Catch:{ Throwable -> 0x0193 }
        goto L_0x0093;
    L_0x0193:
        r9 = move-exception;
        org.telegram.messenger.FileLog.e(r9);
        goto L_0x0093;
    L_0x0199:
        r2 = move-exception;
        r15.close();	 Catch:{ Throwable -> 0x019e }
    L_0x019d:
        throw r2;
    L_0x019e:
        r9 = move-exception;
        org.telegram.messenger.FileLog.e(r9);
        goto L_0x019d;
    L_0x01a3:
        r2 = move-exception;
        goto L_0x00c9;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ImageLoader.loadBitmap(java.lang.String, android.net.Uri, float, float, boolean):android.graphics.Bitmap");
    }

    public static void fillPhotoSizeWithBytes(PhotoSize photoSize) {
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

    private static PhotoSize scaleAndSaveImageInternal(PhotoSize photoSize, Bitmap bitmap, int w, int h, float photoW, float photoH, float scaleFactor, int quality, boolean cache, boolean scaleAnyway) throws Exception {
        Bitmap scaledBitmap;
        TL_fileLocation location;
        if (scaleFactor > 1.0f || scaleAnyway) {
            scaledBitmap = Bitmaps.createScaledBitmap(bitmap, w, h, true);
        } else {
            scaledBitmap = bitmap;
        }
        if (photoSize != null) {
        }
        if (photoSize == null || !(photoSize.location instanceof TL_fileLocation)) {
            location = new TL_fileLocation();
            location.volume_id = -2147483648L;
            location.dc_id = Integer.MIN_VALUE;
            location.local_id = SharedConfig.getLastLocalId();
            location.file_reference = new byte[0];
            photoSize = new TL_photoSize();
            photoSize.location = location;
            photoSize.w = scaledBitmap.getWidth();
            photoSize.h = scaledBitmap.getHeight();
            if (photoSize.w <= 100 && photoSize.h <= 100) {
                photoSize.type = "s";
            } else if (photoSize.w <= 320 && photoSize.h <= 320) {
                photoSize.type = "m";
            } else if (photoSize.w <= 800 && photoSize.h <= 800) {
                photoSize.type = "x";
            } else if (photoSize.w > 1280 || photoSize.h > 1280) {
                photoSize.type = "w";
            } else {
                photoSize.type = "y";
            }
        } else {
            location = (TL_fileLocation) photoSize.location;
        }
        FileOutputStream stream = new FileOutputStream(new File(location.volume_id != -2147483648L ? FileLoader.getDirectory(0) : FileLoader.getDirectory(4), location.volume_id + "_" + location.local_id + ".jpg"));
        scaledBitmap.compress(CompressFormat.JPEG, quality, stream);
        if (cache) {
            ByteArrayOutputStream stream2 = new ByteArrayOutputStream();
            scaledBitmap.compress(CompressFormat.JPEG, quality, stream2);
            photoSize.bytes = stream2.toByteArray();
            photoSize.size = photoSize.bytes.length;
            stream2.close();
        } else {
            photoSize.size = (int) stream.getChannel().size();
        }
        stream.close();
        if (scaledBitmap != bitmap) {
            scaledBitmap.recycle();
        }
        return photoSize;
    }

    public static PhotoSize scaleAndSaveImage(Bitmap bitmap, float maxWidth, float maxHeight, int quality, boolean cache) {
        return scaleAndSaveImage(null, bitmap, maxWidth, maxHeight, quality, cache, 0, 0);
    }

    public static PhotoSize scaleAndSaveImage(PhotoSize photoSize, Bitmap bitmap, float maxWidth, float maxHeight, int quality, boolean cache) {
        return scaleAndSaveImage(photoSize, bitmap, maxWidth, maxHeight, quality, cache, 0, 0);
    }

    public static PhotoSize scaleAndSaveImage(Bitmap bitmap, float maxWidth, float maxHeight, int quality, boolean cache, int minWidth, int minHeight) {
        return scaleAndSaveImage(null, bitmap, maxWidth, maxHeight, quality, cache, minWidth, minHeight);
    }

    public static PhotoSize scaleAndSaveImage(PhotoSize photoSize, Bitmap bitmap, float maxWidth, float maxHeight, int quality, boolean cache, int minWidth, int minHeight) {
        if (bitmap == null) {
            return null;
        }
        float photoW = (float) bitmap.getWidth();
        float photoH = (float) bitmap.getHeight();
        if (photoW == 0.0f || photoH == 0.0f) {
            return null;
        }
        boolean scaleAnyway = false;
        float scaleFactor = Math.max(photoW / maxWidth, photoH / maxHeight);
        if (!(minWidth == 0 || minHeight == 0 || (photoW >= ((float) minWidth) && photoH >= ((float) minHeight)))) {
            if (photoW < ((float) minWidth) && photoH > ((float) minHeight)) {
                scaleFactor = photoW / ((float) minWidth);
            } else if (photoW <= ((float) minWidth) || photoH >= ((float) minHeight)) {
                scaleFactor = Math.max(photoW / ((float) minWidth), photoH / ((float) minHeight));
            } else {
                scaleFactor = photoH / ((float) minHeight);
            }
            scaleAnyway = true;
        }
        int w = (int) (photoW / scaleFactor);
        int h = (int) (photoH / scaleFactor);
        if (h == 0 || w == 0) {
            return null;
        }
        try {
            return scaleAndSaveImageInternal(photoSize, bitmap, w, h, photoW, photoH, scaleFactor, quality, cache, scaleAnyway);
        } catch (Throwable e2) {
            FileLog.e(e2);
            return null;
        }
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

    public static void saveMessageThumbs(Message message) {
        int count;
        int a;
        TLObject photoSize = null;
        TLObject size;
        if (message.media instanceof TL_messageMediaPhoto) {
            count = message.media.photo.sizes.size();
            for (a = 0; a < count; a++) {
                size = (PhotoSize) message.media.photo.sizes.get(a);
                if (size instanceof TL_photoCachedSize) {
                    photoSize = size;
                    break;
                }
            }
        } else if (message.media instanceof TL_messageMediaDocument) {
            count = message.media.document.thumbs.size();
            for (a = 0; a < count; a++) {
                size = (PhotoSize) message.media.document.thumbs.get(a);
                if (size instanceof TL_photoCachedSize) {
                    photoSize = size;
                    break;
                }
            }
        } else if ((message.media instanceof TL_messageMediaWebPage) && message.media.webpage.photo != null) {
            count = message.media.webpage.photo.sizes.size();
            for (a = 0; a < count; a++) {
                size = (PhotoSize) message.media.webpage.photo.sizes.get(a);
                if (size instanceof TL_photoCachedSize) {
                    photoSize = size;
                    break;
                }
            }
        }
        if (photoSize != null && photoSize.bytes != null && photoSize.bytes.length != 0) {
            if (photoSize.location == null || (photoSize.location instanceof TL_fileLocationUnavailable)) {
                photoSize.location = new TL_fileLocation();
                photoSize.location.volume_id = -2147483648L;
                photoSize.location.dc_id = Integer.MIN_VALUE;
                photoSize.location.local_id = SharedConfig.getLastLocalId();
                photoSize.location.file_reference = new byte[0];
            }
            File file = FileLoader.getPathToAttach(photoSize, true);
            boolean isEncrypted = false;
            if (MessageObject.shouldEncryptPhotoOrVideo(message)) {
                isEncrypted = true;
                file = new File(file.getAbsolutePath() + ".enc");
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
                        Utilities.aesCtrDecryptionByteArray(photoSize.bytes, encryptKey, encryptIv, 0, photoSize.bytes.length, 0);
                    } catch (Throwable e) {
                        FileLog.e(e);
                    }
                }
                RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rws");
                randomAccessFile.write(photoSize.bytes);
                randomAccessFile.close();
            }
            TL_photoSize newPhotoSize = new TL_photoSize();
            newPhotoSize.w = photoSize.w;
            newPhotoSize.h = photoSize.h;
            newPhotoSize.location = photoSize.location;
            newPhotoSize.size = photoSize.size;
            newPhotoSize.type = photoSize.type;
            if (message.media instanceof TL_messageMediaPhoto) {
                count = message.media.photo.sizes.size();
                for (a = 0; a < count; a++) {
                    if (((PhotoSize) message.media.photo.sizes.get(a)) instanceof TL_photoCachedSize) {
                        message.media.photo.sizes.set(a, newPhotoSize);
                        return;
                    }
                }
            } else if (message.media instanceof TL_messageMediaDocument) {
                count = message.media.document.thumbs.size();
                for (a = 0; a < count; a++) {
                    if (((PhotoSize) message.media.document.thumbs.get(a)) instanceof TL_photoCachedSize) {
                        message.media.document.thumbs.set(a, newPhotoSize);
                        return;
                    }
                }
            } else if (message.media instanceof TL_messageMediaWebPage) {
                count = message.media.webpage.photo.sizes.size();
                for (a = 0; a < count; a++) {
                    if (((PhotoSize) message.media.webpage.photo.sizes.get(a)) instanceof TL_photoCachedSize) {
                        message.media.webpage.photo.sizes.set(a, newPhotoSize);
                        return;
                    }
                }
            }
        }
    }

    public static void saveMessagesThumbs(ArrayList<Message> messages) {
        if (messages != null && !messages.isEmpty()) {
            for (int a = 0; a < messages.size(); a++) {
                saveMessageThumbs((Message) messages.get(a));
            }
        }
    }
}
