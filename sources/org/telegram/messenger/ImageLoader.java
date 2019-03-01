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
    public static final String VIDEO_FILTER = "g";
    private static ThreadLocal<byte[]> bytesLocal = new ThreadLocal();
    private static ThreadLocal<byte[]> bytesThumbLocal = new ThreadLocal();
    private static byte[] header = new byte[12];
    private static byte[] headerThumb = new byte[12];
    private LinkedList<ArtworkLoadTask> artworkTasks = new LinkedList();
    private HashMap<String, Integer> bitmapUseCounts = new HashMap();
    private DispatchQueue cacheOutQueue = new DispatchQueue("cacheOutQueue");
    private DispatchQueue cacheThumbOutQueue = new DispatchQueue("cacheThumbOutQueue");
    private boolean canForce8888;
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

        public ArtworkLoadTask(CacheImage cacheImage) {
            this.cacheImage = cacheImage;
            this.small = Uri.parse((String) cacheImage.location).getQueryParameter("s") != null;
        }

        /* JADX WARNING: Removed duplicated region for block: B:106:0x0173 A:{Catch:{ Throwable -> 0x01c4 }} */
        /* JADX WARNING: Removed duplicated region for block: B:108:0x017c A:{SYNTHETIC, Splitter: B:108:0x017c} */
        /* JADX WARNING: Removed duplicated region for block: B:111:0x0181 A:{SYNTHETIC, Splitter: B:111:0x0181} */
        /* JADX WARNING: Removed duplicated region for block: B:51:0x00f5 A:{SYNTHETIC, Splitter: B:51:0x00f5} */
        /* JADX WARNING: Removed duplicated region for block: B:54:0x00fa A:{SYNTHETIC, Splitter: B:54:0x00fa} */
        /* JADX WARNING: Removed duplicated region for block: B:54:0x00fa A:{SYNTHETIC, Splitter: B:54:0x00fa} */
        protected java.lang.String doInBackground(java.lang.Void... r18) {
            /*
            r17 = this;
            r11 = 0;
            r7 = 0;
            r0 = r17;
            r14 = r0.cacheImage;	 Catch:{ Throwable -> 0x00d3 }
            r8 = r14.location;	 Catch:{ Throwable -> 0x00d3 }
            r8 = (java.lang.String) r8;	 Catch:{ Throwable -> 0x00d3 }
            r5 = new java.net.URL;	 Catch:{ Throwable -> 0x00d3 }
            r14 = "athumb://";
            r15 = "https://";
            r14 = r8.replace(r14, r15);	 Catch:{ Throwable -> 0x00d3 }
            r5.<init>(r14);	 Catch:{ Throwable -> 0x00d3 }
            r14 = r5.openConnection();	 Catch:{ Throwable -> 0x00d3 }
            r14 = (java.net.HttpURLConnection) r14;	 Catch:{ Throwable -> 0x00d3 }
            r0 = r17;
            r0.httpConnection = r14;	 Catch:{ Throwable -> 0x00d3 }
            r0 = r17;
            r14 = r0.httpConnection;	 Catch:{ Throwable -> 0x00d3 }
            r15 = "User-Agent";
            r16 = "Mozilla/5.0 (iPhone; CPU iPhone OS 10_0 like Mac OS X) AppleWebKit/602.1.38 (KHTML, like Gecko) Version/10.0 Mobile/14A5297c Safari/602.1";
            r14.addRequestProperty(r15, r16);	 Catch:{ Throwable -> 0x00d3 }
            r0 = r17;
            r14 = r0.httpConnection;	 Catch:{ Throwable -> 0x00d3 }
            r15 = 5000; // 0x1388 float:7.006E-42 double:2.4703E-320;
            r14.setConnectTimeout(r15);	 Catch:{ Throwable -> 0x00d3 }
            r0 = r17;
            r14 = r0.httpConnection;	 Catch:{ Throwable -> 0x00d3 }
            r15 = 5000; // 0x1388 float:7.006E-42 double:2.4703E-320;
            r14.setReadTimeout(r15);	 Catch:{ Throwable -> 0x00d3 }
            r0 = r17;
            r14 = r0.httpConnection;	 Catch:{ Throwable -> 0x00d3 }
            r14.connect();	 Catch:{ Throwable -> 0x00d3 }
            r0 = r17;
            r14 = r0.httpConnection;	 Catch:{ Exception -> 0x00ce }
            if (r14 == 0) goto L_0x0068;
        L_0x004f:
            r0 = r17;
            r14 = r0.httpConnection;	 Catch:{ Exception -> 0x00ce }
            r3 = r14.getResponseCode();	 Catch:{ Exception -> 0x00ce }
            r14 = 200; // 0xc8 float:2.8E-43 double:9.9E-322;
            if (r3 == r14) goto L_0x0068;
        L_0x005b:
            r14 = 202; // 0xca float:2.83E-43 double:1.0E-321;
            if (r3 == r14) goto L_0x0068;
        L_0x005f:
            r14 = 304; // 0x130 float:4.26E-43 double:1.5E-321;
            if (r3 == r14) goto L_0x0068;
        L_0x0063:
            r14 = 0;
            r0 = r17;
            r0.canRetry = r14;	 Catch:{ Exception -> 0x00ce }
        L_0x0068:
            r0 = r17;
            r14 = r0.httpConnection;	 Catch:{ Throwable -> 0x00d3 }
            r7 = r14.getInputStream();	 Catch:{ Throwable -> 0x00d3 }
            r12 = new java.io.ByteArrayOutputStream;	 Catch:{ Throwable -> 0x00d3 }
            r12.<init>();	 Catch:{ Throwable -> 0x00d3 }
            r14 = 32768; // 0x8000 float:4.5918E-41 double:1.61895E-319;
            r4 = new byte[r14];	 Catch:{ Throwable -> 0x010b, all -> 0x01c6 }
        L_0x007a:
            r14 = r17.isCancelled();	 Catch:{ Throwable -> 0x010b, all -> 0x01c6 }
            if (r14 == 0) goto L_0x00ff;
        L_0x0080:
            r14 = 0;
            r0 = r17;
            r0.canRetry = r14;	 Catch:{ Throwable -> 0x010b, all -> 0x01c6 }
            r10 = new org.json.JSONObject;	 Catch:{ Throwable -> 0x010b, all -> 0x01c6 }
            r14 = new java.lang.String;	 Catch:{ Throwable -> 0x010b, all -> 0x01c6 }
            r15 = r12.toByteArray();	 Catch:{ Throwable -> 0x010b, all -> 0x01c6 }
            r16 = "UTF-8";
            r14.<init>(r15, r16);	 Catch:{ Throwable -> 0x010b, all -> 0x01c6 }
            r10.<init>(r14);	 Catch:{ Throwable -> 0x010b, all -> 0x01c6 }
            r14 = "results";
            r1 = r10.getJSONArray(r14);	 Catch:{ Throwable -> 0x010b, all -> 0x01c6 }
            r14 = r1.length();	 Catch:{ Throwable -> 0x010b, all -> 0x01c6 }
            if (r14 <= 0) goto L_0x0140;
        L_0x00a3:
            r14 = 0;
            r9 = r1.getJSONObject(r14);	 Catch:{ Throwable -> 0x010b, all -> 0x01c6 }
            r14 = "artworkUrl100";
            r2 = r9.getString(r14);	 Catch:{ Throwable -> 0x010b, all -> 0x01c6 }
            r0 = r17;
            r14 = r0.small;	 Catch:{ Throwable -> 0x010b, all -> 0x01c6 }
            if (r14 == 0) goto L_0x0118;
        L_0x00b5:
            r0 = r17;
            r14 = r0.httpConnection;	 Catch:{ Throwable -> 0x01d1 }
            if (r14 == 0) goto L_0x00c2;
        L_0x00bb:
            r0 = r17;
            r14 = r0.httpConnection;	 Catch:{ Throwable -> 0x01d1 }
            r14.disconnect();	 Catch:{ Throwable -> 0x01d1 }
        L_0x00c2:
            if (r7 == 0) goto L_0x00c7;
        L_0x00c4:
            r7.close();	 Catch:{ Throwable -> 0x0113 }
        L_0x00c7:
            if (r12 == 0) goto L_0x00cc;
        L_0x00c9:
            r12.close();	 Catch:{ Exception -> 0x01b9 }
        L_0x00cc:
            r11 = r12;
        L_0x00cd:
            return r2;
        L_0x00ce:
            r6 = move-exception;
            org.telegram.messenger.FileLog.e(r6);	 Catch:{ Throwable -> 0x00d3 }
            goto L_0x0068;
        L_0x00d3:
            r6 = move-exception;
        L_0x00d4:
            r14 = r6 instanceof java.net.SocketTimeoutException;	 Catch:{ all -> 0x016c }
            if (r14 == 0) goto L_0x0161;
        L_0x00d8:
            r14 = org.telegram.messenger.ApplicationLoader.isNetworkOnline();	 Catch:{ all -> 0x016c }
            if (r14 == 0) goto L_0x00e3;
        L_0x00de:
            r14 = 0;
            r0 = r17;
            r0.canRetry = r14;	 Catch:{ all -> 0x016c }
        L_0x00e3:
            org.telegram.messenger.FileLog.e(r6);	 Catch:{ all -> 0x016c }
            r0 = r17;
            r14 = r0.httpConnection;	 Catch:{ Throwable -> 0x01c9 }
            if (r14 == 0) goto L_0x00f3;
        L_0x00ec:
            r0 = r17;
            r14 = r0.httpConnection;	 Catch:{ Throwable -> 0x01c9 }
            r14.disconnect();	 Catch:{ Throwable -> 0x01c9 }
        L_0x00f3:
            if (r7 == 0) goto L_0x00f8;
        L_0x00f5:
            r7.close();	 Catch:{ Throwable -> 0x01ae }
        L_0x00f8:
            if (r11 == 0) goto L_0x00fd;
        L_0x00fa:
            r11.close();	 Catch:{ Exception -> 0x01bf }
        L_0x00fd:
            r2 = 0;
            goto L_0x00cd;
        L_0x00ff:
            r13 = r7.read(r4);	 Catch:{ Throwable -> 0x010b, all -> 0x01c6 }
            if (r13 <= 0) goto L_0x010e;
        L_0x0105:
            r14 = 0;
            r12.write(r4, r14, r13);	 Catch:{ Throwable -> 0x010b, all -> 0x01c6 }
            goto L_0x007a;
        L_0x010b:
            r6 = move-exception;
            r11 = r12;
            goto L_0x00d4;
        L_0x010e:
            r14 = -1;
            if (r13 != r14) goto L_0x0080;
        L_0x0111:
            goto L_0x0080;
        L_0x0113:
            r6 = move-exception;
            org.telegram.messenger.FileLog.e(r6);
            goto L_0x00c7;
        L_0x0118:
            r14 = "100x100";
            r15 = "600x600";
            r2 = r2.replace(r14, r15);	 Catch:{ Throwable -> 0x010b, all -> 0x01c6 }
            r0 = r17;
            r14 = r0.httpConnection;	 Catch:{ Throwable -> 0x01ce }
            if (r14 == 0) goto L_0x012f;
        L_0x0128:
            r0 = r17;
            r14 = r0.httpConnection;	 Catch:{ Throwable -> 0x01ce }
            r14.disconnect();	 Catch:{ Throwable -> 0x01ce }
        L_0x012f:
            if (r7 == 0) goto L_0x0134;
        L_0x0131:
            r7.close();	 Catch:{ Throwable -> 0x013b }
        L_0x0134:
            if (r12 == 0) goto L_0x0139;
        L_0x0136:
            r12.close();	 Catch:{ Exception -> 0x01bc }
        L_0x0139:
            r11 = r12;
            goto L_0x00cd;
        L_0x013b:
            r6 = move-exception;
            org.telegram.messenger.FileLog.e(r6);
            goto L_0x0134;
        L_0x0140:
            r0 = r17;
            r14 = r0.httpConnection;	 Catch:{ Throwable -> 0x01cc }
            if (r14 == 0) goto L_0x014d;
        L_0x0146:
            r0 = r17;
            r14 = r0.httpConnection;	 Catch:{ Throwable -> 0x01cc }
            r14.disconnect();	 Catch:{ Throwable -> 0x01cc }
        L_0x014d:
            if (r7 == 0) goto L_0x0152;
        L_0x014f:
            r7.close();	 Catch:{ Throwable -> 0x0159 }
        L_0x0152:
            if (r12 == 0) goto L_0x0157;
        L_0x0154:
            r12.close();	 Catch:{ Exception -> 0x015e }
        L_0x0157:
            r11 = r12;
            goto L_0x00fd;
        L_0x0159:
            r6 = move-exception;
            org.telegram.messenger.FileLog.e(r6);
            goto L_0x0152;
        L_0x015e:
            r14 = move-exception;
            r11 = r12;
            goto L_0x00fd;
        L_0x0161:
            r14 = r6 instanceof java.net.UnknownHostException;	 Catch:{ all -> 0x016c }
            if (r14 == 0) goto L_0x0185;
        L_0x0165:
            r14 = 0;
            r0 = r17;
            r0.canRetry = r14;	 Catch:{ all -> 0x016c }
            goto L_0x00e3;
        L_0x016c:
            r14 = move-exception;
        L_0x016d:
            r0 = r17;
            r15 = r0.httpConnection;	 Catch:{ Throwable -> 0x01c4 }
            if (r15 == 0) goto L_0x017a;
        L_0x0173:
            r0 = r17;
            r15 = r0.httpConnection;	 Catch:{ Throwable -> 0x01c4 }
            r15.disconnect();	 Catch:{ Throwable -> 0x01c4 }
        L_0x017a:
            if (r7 == 0) goto L_0x017f;
        L_0x017c:
            r7.close();	 Catch:{ Throwable -> 0x01b4 }
        L_0x017f:
            if (r11 == 0) goto L_0x0184;
        L_0x0181:
            r11.close();	 Catch:{ Exception -> 0x01c2 }
        L_0x0184:
            throw r14;
        L_0x0185:
            r14 = r6 instanceof java.net.SocketException;	 Catch:{ all -> 0x016c }
            if (r14 == 0) goto L_0x01a3;
        L_0x0189:
            r14 = r6.getMessage();	 Catch:{ all -> 0x016c }
            if (r14 == 0) goto L_0x00e3;
        L_0x018f:
            r14 = r6.getMessage();	 Catch:{ all -> 0x016c }
            r15 = "ECONNRESET";
            r14 = r14.contains(r15);	 Catch:{ all -> 0x016c }
            if (r14 == 0) goto L_0x00e3;
        L_0x019c:
            r14 = 0;
            r0 = r17;
            r0.canRetry = r14;	 Catch:{ all -> 0x016c }
            goto L_0x00e3;
        L_0x01a3:
            r14 = r6 instanceof java.io.FileNotFoundException;	 Catch:{ all -> 0x016c }
            if (r14 == 0) goto L_0x00e3;
        L_0x01a7:
            r14 = 0;
            r0 = r17;
            r0.canRetry = r14;	 Catch:{ all -> 0x016c }
            goto L_0x00e3;
        L_0x01ae:
            r6 = move-exception;
            org.telegram.messenger.FileLog.e(r6);
            goto L_0x00f8;
        L_0x01b4:
            r6 = move-exception;
            org.telegram.messenger.FileLog.e(r6);
            goto L_0x017f;
        L_0x01b9:
            r14 = move-exception;
            goto L_0x00cc;
        L_0x01bc:
            r14 = move-exception;
            goto L_0x0139;
        L_0x01bf:
            r14 = move-exception;
            goto L_0x00fd;
        L_0x01c2:
            r15 = move-exception;
            goto L_0x0184;
        L_0x01c4:
            r15 = move-exception;
            goto L_0x017a;
        L_0x01c6:
            r14 = move-exception;
            r11 = r12;
            goto L_0x016d;
        L_0x01c9:
            r14 = move-exception;
            goto L_0x00f3;
        L_0x01cc:
            r14 = move-exception;
            goto L_0x014d;
        L_0x01ce:
            r14 = move-exception;
            goto L_0x012f;
        L_0x01d1:
            r14 = move-exception;
            goto L_0x00c2;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ImageLoader.ArtworkLoadTask.doInBackground(java.lang.Void[]):java.lang.String");
        }

        protected void onPostExecute(String result) {
            if (result != null) {
                this.cacheImage.httpTask = new HttpImageTask(this.cacheImage, 0, result);
                ImageLoader.this.httpTasks.add(this.cacheImage.httpTask);
                ImageLoader.this.runHttpTasks(false);
            } else if (this.canRetry) {
                ImageLoader.this.artworkLoadError(this.cacheImage.url);
            }
            ImageLoader.this.imageLoadQueue.postRunnable(new ImageLoader$ArtworkLoadTask$$Lambda$0(this));
        }

        final /* synthetic */ void lambda$onPostExecute$0$ImageLoader$ArtworkLoadTask() {
            ImageLoader.this.runArtworkTasks(true);
        }

        final /* synthetic */ void lambda$onCancelled$1$ImageLoader$ArtworkLoadTask() {
            ImageLoader.this.runArtworkTasks(true);
        }

        protected void onCancelled() {
            ImageLoader.this.imageLoadQueue.postRunnable(new ImageLoader$ArtworkLoadTask$$Lambda$1(this));
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
        protected ArrayList<ImageReceiver> imageReceiverArray;
        protected int imageType;
        protected String key;
        protected ArrayList<String> keys;
        protected Object location;
        protected Object parentObject;
        protected SecureDocument secureDocument;
        protected int size;
        protected File tempFilePath;
        protected ArrayList<Integer> thumbs;
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

        public void addImageReceiver(ImageReceiver imageReceiver, String key, String filter, int type) {
            if (!this.imageReceiverArray.contains(imageReceiver)) {
                this.imageReceiverArray.add(imageReceiver);
                this.keys.add(key);
                this.filters.add(filter);
                this.thumbs.add(Integer.valueOf(type));
                ImageLoader.this.imageLoadingByTag.put(imageReceiver.getTag(type), this);
            }
        }

        public void replaceImageReceiver(ImageReceiver imageReceiver, String key, String filter, int type) {
            int index = this.imageReceiverArray.indexOf(imageReceiver);
            if (index != -1) {
                if (((Integer) this.thumbs.get(index)).intValue() != type) {
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
            int currentImageType = this.imageType;
            int a = 0;
            while (a < this.imageReceiverArray.size()) {
                ImageReceiver obj = (ImageReceiver) this.imageReceiverArray.get(a);
                if (obj == null || obj == imageReceiver) {
                    this.imageReceiverArray.remove(a);
                    this.keys.remove(a);
                    this.filters.remove(a);
                    currentImageType = ((Integer) this.thumbs.remove(a)).intValue();
                    if (obj != null) {
                        ImageLoader.this.imageLoadingByTag.remove(obj.getTag(currentImageType));
                    }
                    a--;
                }
                a++;
            }
            if (this.imageReceiverArray.size() == 0) {
                for (a = 0; a < this.imageReceiverArray.size(); a++) {
                    ImageLoader.this.imageLoadingByTag.remove(((ImageReceiver) this.imageReceiverArray.get(a)).getTag(currentImageType));
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
                    if (currentImageType == 1) {
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
                ImageLoader.this.imageLoadingByTag.remove(((ImageReceiver) this.imageReceiverArray.get(a)).getTag(this.imageType));
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
                AnimatedFileDrawable fileDrawable = (AnimatedFileDrawable) image;
                a = 0;
                while (a < finalImageReceiverArray.size()) {
                    ImageReceiver imgView = (ImageReceiver) finalImageReceiverArray.get(a);
                    AnimatedFileDrawable toSet = a == 0 ? fileDrawable : fileDrawable.makeCopy();
                    if (imgView.setImageBitmapByKey(toSet, this.key, this.imageType, false)) {
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
                    return;
                }
                return;
            }
            for (a = 0; a < finalImageReceiverArray.size(); a++) {
                ((ImageReceiver) finalImageReceiverArray.get(a)).setImageBitmapByKey(image, this.key, this.imageType, false);
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

        /* JADX WARNING: Removed duplicated region for block: B:147:0x0311 A:{SYNTHETIC, Splitter: B:147:0x0311} */
        /* JADX WARNING: Removed duplicated region for block: B:502:0x0af9  */
        /* JADX WARNING: Removed duplicated region for block: B:128:0x02cb  */
        /* JADX WARNING: Removed duplicated region for block: B:126:0x02c7 A:{SKIP} */
        /* JADX WARNING: Removed duplicated region for block: B:128:0x02cb  */
        /* JADX WARNING: Removed duplicated region for block: B:502:0x0af9  */
        /* JADX WARNING: Missing block: B:8:0x0021, code:
            if ((r69.cacheImage.location instanceof org.telegram.tgnet.TLRPC.TL_photoStrippedSize) == false) goto L_0x0110;
     */
        /* JADX WARNING: Missing block: B:9:0x0023, code:
            r8 = r69.sync;
     */
        /* JADX WARNING: Missing block: B:10:0x0027, code:
            monitor-enter(r8);
     */
        /* JADX WARNING: Missing block: B:13:0x002c, code:
            if (r69.isCancelled == false) goto L_0x0036;
     */
        /* JADX WARNING: Missing block: B:14:0x002e, code:
            monitor-exit(r8);
     */
        /* JADX WARNING: Missing block: B:23:?, code:
            monitor-exit(r8);
     */
        /* JADX WARNING: Missing block: B:24:0x0037, code:
            r55 = r69.cacheImage.location;
            r44 = ((r55.bytes.length - 3) + org.telegram.messenger.Bitmaps.header.length) + org.telegram.messenger.Bitmaps.footer.length;
            r24 = (byte[]) org.telegram.messenger.ImageLoader.access$1600().get();
     */
        /* JADX WARNING: Missing block: B:25:0x005b, code:
            if (r24 == null) goto L_0x010a;
     */
        /* JADX WARNING: Missing block: B:27:0x0062, code:
            if (r24.length < r44) goto L_0x010a;
     */
        /* JADX WARNING: Missing block: B:28:0x0064, code:
            r29 = r24;
     */
        /* JADX WARNING: Missing block: B:29:0x0066, code:
            if (r29 != null) goto L_0x0079;
     */
        /* JADX WARNING: Missing block: B:30:0x0068, code:
            r29 = new byte[r44];
            org.telegram.messenger.ImageLoader.access$1600().set(r29);
     */
        /* JADX WARNING: Missing block: B:31:0x0079, code:
            java.lang.System.arraycopy(org.telegram.messenger.Bitmaps.header, 0, r29, 0, org.telegram.messenger.Bitmaps.header.length);
            java.lang.System.arraycopy(r55.bytes, 3, r29, org.telegram.messenger.Bitmaps.header.length, r55.bytes.length - 3);
            java.lang.System.arraycopy(org.telegram.messenger.Bitmaps.footer, 0, r29, (org.telegram.messenger.Bitmaps.header.length + r55.bytes.length) - 3, org.telegram.messenger.Bitmaps.footer.length);
            r29[164] = r55.bytes[1];
            r29[166] = r55.bytes[2];
            r4 = android.graphics.BitmapFactory.decodeByteArray(r29, 0, r44);
     */
        /* JADX WARNING: Missing block: B:32:0x00ce, code:
            if (r4 == null) goto L_0x00fc;
     */
        /* JADX WARNING: Missing block: B:34:0x00da, code:
            if (android.text.TextUtils.isEmpty(r69.cacheImage.filter) != false) goto L_0x00fc;
     */
        /* JADX WARNING: Missing block: B:36:0x00e9, code:
            if (r69.cacheImage.filter.contains("b") == false) goto L_0x00fc;
     */
        /* JADX WARNING: Missing block: B:37:0x00eb, code:
            org.telegram.messenger.Utilities.blurBitmap(r4, 3, 1, r4.getWidth(), r4.getHeight(), r4.getRowBytes());
     */
        /* JADX WARNING: Missing block: B:38:0x00fc, code:
            if (r4 == null) goto L_0x010e;
     */
        /* JADX WARNING: Missing block: B:39:0x00fe, code:
            r7 = new android.graphics.drawable.BitmapDrawable(r4);
     */
        /* JADX WARNING: Missing block: B:40:0x0103, code:
            onPostExecute(r7);
     */
        /* JADX WARNING: Missing block: B:41:0x010a, code:
            r29 = null;
     */
        /* JADX WARNING: Missing block: B:42:0x010e, code:
            r7 = null;
     */
        /* JADX WARNING: Missing block: B:44:0x0116, code:
            if (r69.cacheImage.animatedFile == false) goto L_0x01a3;
     */
        /* JADX WARNING: Missing block: B:45:0x0118, code:
            r8 = r69.sync;
     */
        /* JADX WARNING: Missing block: B:46:0x011c, code:
            monitor-enter(r8);
     */
        /* JADX WARNING: Missing block: B:49:0x0121, code:
            if (r69.isCancelled == false) goto L_0x0129;
     */
        /* JADX WARNING: Missing block: B:50:0x0123, code:
            monitor-exit(r8);
     */
        /* JADX WARNING: Missing block: B:55:?, code:
            monitor-exit(r8);
     */
        /* JADX WARNING: Missing block: B:57:0x0137, code:
            if ("g".equals(r69.cacheImage.filter) == false) goto L_0x0180;
     */
        /* JADX WARNING: Missing block: B:59:0x0141, code:
            if ((r69.cacheImage.location instanceof org.telegram.tgnet.TLRPC.TL_documentEncrypted) != false) goto L_0x0180;
     */
        /* JADX WARNING: Missing block: B:60:0x0143, code:
            r6 = r69.cacheImage.finalFilePath;
            r8 = (long) r69.cacheImage.size;
     */
        /* JADX WARNING: Missing block: B:61:0x015b, code:
            if ((r69.cacheImage.location instanceof org.telegram.tgnet.TLRPC.Document) == false) goto L_0x017e;
     */
        /* JADX WARNING: Missing block: B:62:0x015d, code:
            r10 = (org.telegram.tgnet.TLRPC.Document) r69.cacheImage.location;
     */
        /* JADX WARNING: Missing block: B:63:0x0165, code:
            r5 = new org.telegram.ui.Components.AnimatedFileDrawable(r6, false, r8, r10, r69.cacheImage.parentObject, r69.cacheImage.currentAccount);
     */
        /* JADX WARNING: Missing block: B:64:0x0174, code:
            java.lang.Thread.interrupted();
            onPostExecute(r5);
     */
        /* JADX WARNING: Missing block: B:65:0x017e, code:
            r10 = null;
     */
        /* JADX WARNING: Missing block: B:66:0x0180, code:
            r5 = new org.telegram.ui.Components.AnimatedFileDrawable(r69.cacheImage.finalFilePath, "d".equals(r69.cacheImage.filter), 0, null, null, r69.cacheImage.currentAccount);
     */
        /* JADX WARNING: Missing block: B:67:0x01a3, code:
            r46 = null;
            r47 = false;
            r49 = false;
            r52 = 0;
            r26 = r69.cacheImage.finalFilePath;
     */
        /* JADX WARNING: Missing block: B:68:0x01bb, code:
            if (r69.cacheImage.secureDocument != null) goto L_0x01d4;
     */
        /* JADX WARNING: Missing block: B:70:0x01c3, code:
            if (r69.cacheImage.encryptionKeyPath == null) goto L_0x02db;
     */
        /* JADX WARNING: Missing block: B:71:0x01c5, code:
            if (r26 == null) goto L_0x02db;
     */
        /* JADX WARNING: Missing block: B:73:0x01d2, code:
            if (r26.getAbsolutePath().endsWith(".enc") == false) goto L_0x02db;
     */
        /* JADX WARNING: Missing block: B:74:0x01d4, code:
            r42 = true;
     */
        /* JADX WARNING: Missing block: B:76:0x01dc, code:
            if (r69.cacheImage.secureDocument == null) goto L_0x02eb;
     */
        /* JADX WARNING: Missing block: B:77:0x01de, code:
            r64 = r69.cacheImage.secureDocument.secureDocumentKey;
     */
        /* JADX WARNING: Missing block: B:78:0x01f0, code:
            if (r69.cacheImage.secureDocument.secureFile == null) goto L_0x02df;
     */
        /* JADX WARNING: Missing block: B:80:0x01fc, code:
            if (r69.cacheImage.secureDocument.secureFile.file_hash == null) goto L_0x02df;
     */
        /* JADX WARNING: Missing block: B:81:0x01fe, code:
            r63 = r69.cacheImage.secureDocument.secureFile.file_hash;
     */
        /* JADX WARNING: Missing block: B:82:0x020a, code:
            r27 = true;
            r66 = false;
     */
        /* JADX WARNING: Missing block: B:83:0x0212, code:
            if (android.os.Build.VERSION.SDK_INT >= 19) goto L_0x0269;
     */
        /* JADX WARNING: Missing block: B:84:0x0214, code:
            r58 = null;
     */
        /* JADX WARNING: Missing block: B:86:?, code:
            r0 = new java.io.RandomAccessFile(r26, "r");
     */
        /* JADX WARNING: Missing block: B:89:0x0229, code:
            if (r69.cacheImage.imageType != 1) goto L_0x02f1;
     */
        /* JADX WARNING: Missing block: B:90:0x022b, code:
            r24 = org.telegram.messenger.ImageLoader.access$1700();
     */
        /* JADX WARNING: Missing block: B:91:0x022f, code:
            r0.readFully(r24, 0, r24.length);
            r65 = new java.lang.String(r24).toLowerCase().toLowerCase();
     */
        /* JADX WARNING: Missing block: B:92:0x0252, code:
            if (r65.startsWith("riff") == false) goto L_0x0261;
     */
        /* JADX WARNING: Missing block: B:94:0x025d, code:
            if (r65.endsWith("webp") == false) goto L_0x0261;
     */
        /* JADX WARNING: Missing block: B:95:0x025f, code:
            r66 = true;
     */
        /* JADX WARNING: Missing block: B:96:0x0261, code:
            r0.close();
     */
        /* JADX WARNING: Missing block: B:97:0x0264, code:
            if (r0 == null) goto L_0x0269;
     */
        /* JADX WARNING: Missing block: B:99:?, code:
            r0.close();
     */
        /* JADX WARNING: Missing block: B:101:0x0270, code:
            if (r69.cacheImage.imageType == 1) goto L_0x0272;
     */
        /* JADX WARNING: Missing block: B:102:0x0272, code:
            r20 = 0;
            r28 = false;
     */
        /* JADX WARNING: Missing block: B:103:0x027c, code:
            if (r69.cacheImage.filter != null) goto L_0x027e;
     */
        /* JADX WARNING: Missing block: B:105:0x028b, code:
            if (r69.cacheImage.filter.contains("b2") != false) goto L_0x028d;
     */
        /* JADX WARNING: Missing block: B:106:0x028d, code:
            r20 = 3;
     */
        /* JADX WARNING: Missing block: B:108:0x029c, code:
            if (r69.cacheImage.filter.contains("i") != false) goto L_0x029e;
     */
        /* JADX WARNING: Missing block: B:109:0x029e, code:
            r28 = true;
     */
        /* JADX WARNING: Missing block: B:111:?, code:
            org.telegram.messenger.ImageLoader.access$1902(r69.this$0, java.lang.System.currentTimeMillis());
     */
        /* JADX WARNING: Missing block: B:112:0x02af, code:
            monitor-enter(r69.sync);
     */
        /* JADX WARNING: Missing block: B:115:0x02b4, code:
            if (r69.isCancelled != false) goto L_0x02b6;
     */
        /* JADX WARNING: Missing block: B:121:0x02bc, code:
            r31 = th;
     */
        /* JADX WARNING: Missing block: B:122:0x02bd, code:
            r6 = null;
     */
        /* JADX WARNING: Missing block: B:128:0x02cb, code:
            r7 = new org.telegram.messenger.ExtendedBitmapDrawable(r6, r49, r52);
     */
        /* JADX WARNING: Missing block: B:130:0x02db, code:
            r42 = false;
     */
        /* JADX WARNING: Missing block: B:131:0x02df, code:
            r63 = r69.cacheImage.secureDocument.fileHash;
     */
        /* JADX WARNING: Missing block: B:132:0x02eb, code:
            r64 = null;
            r63 = null;
     */
        /* JADX WARNING: Missing block: B:134:?, code:
            r24 = org.telegram.messenger.ImageLoader.access$1800();
     */
        /* JADX WARNING: Missing block: B:135:0x02f7, code:
            r31 = move-exception;
     */
        /* JADX WARNING: Missing block: B:136:0x02f8, code:
            org.telegram.messenger.FileLog.e(r31);
     */
        /* JADX WARNING: Missing block: B:137:0x02fd, code:
            r31 = e;
     */
        /* JADX WARNING: Missing block: B:139:?, code:
            org.telegram.messenger.FileLog.e(r31);
     */
        /* JADX WARNING: Missing block: B:140:0x0301, code:
            if (r58 != null) goto L_0x0303;
     */
        /* JADX WARNING: Missing block: B:142:?, code:
            r58.close();
     */
        /* JADX WARNING: Missing block: B:143:0x0308, code:
            r31 = move-exception;
     */
        /* JADX WARNING: Missing block: B:144:0x0309, code:
            org.telegram.messenger.FileLog.e(r31);
     */
        /* JADX WARNING: Missing block: B:145:0x030e, code:
            r7 = th;
     */
        /* JADX WARNING: Missing block: B:146:0x030f, code:
            if (r58 != null) goto L_0x0311;
     */
        /* JADX WARNING: Missing block: B:148:?, code:
            r58.close();
     */
        /* JADX WARNING: Missing block: B:149:0x0314, code:
            throw r7;
     */
        /* JADX WARNING: Missing block: B:150:0x0315, code:
            r31 = move-exception;
     */
        /* JADX WARNING: Missing block: B:151:0x0316, code:
            org.telegram.messenger.FileLog.e(r31);
     */
        /* JADX WARNING: Missing block: B:153:0x0327, code:
            if (r69.cacheImage.filter.contains("b1") != false) goto L_0x0329;
     */
        /* JADX WARNING: Missing block: B:154:0x0329, code:
            r20 = 2;
     */
        /* JADX WARNING: Missing block: B:156:0x033a, code:
            if (r69.cacheImage.filter.contains("b") != false) goto L_0x033c;
     */
        /* JADX WARNING: Missing block: B:157:0x033c, code:
            r20 = 1;
     */
        /* JADX WARNING: Missing block: B:161:?, code:
            r51 = new android.graphics.BitmapFactory.Options();
            r51.inSampleSize = 1;
     */
        /* JADX WARNING: Missing block: B:162:0x034f, code:
            if (android.os.Build.VERSION.SDK_INT >= 21) goto L_0x0356;
     */
        /* JADX WARNING: Missing block: B:163:0x0351, code:
            r51.inPurgeable = true;
     */
        /* JADX WARNING: Missing block: B:164:0x0356, code:
            if (r66 == false) goto L_0x03cb;
     */
        /* JADX WARNING: Missing block: B:165:0x0358, code:
            r0 = new java.io.RandomAccessFile(r26, "r");
            r23 = r0.getChannel().map(java.nio.channels.FileChannel.MapMode.READ_ONLY, 0, r26.length());
            r22 = new android.graphics.BitmapFactory.Options();
            r22.inJustDecodeBounds = true;
            org.telegram.messenger.Utilities.loadWebpImage(null, r23, r23.limit(), r22, true);
            r6 = org.telegram.messenger.Bitmaps.createBitmap(r22.outWidth, r22.outHeight, android.graphics.Bitmap.Config.ARGB_8888);
     */
        /* JADX WARNING: Missing block: B:167:?, code:
            r8 = r23.limit();
     */
        /* JADX WARNING: Missing block: B:168:0x03a2, code:
            if (r51.inPurgeable != false) goto L_0x03c9;
     */
        /* JADX WARNING: Missing block: B:169:0x03a4, code:
            r7 = true;
     */
        /* JADX WARNING: Missing block: B:170:0x03a5, code:
            org.telegram.messenger.Utilities.loadWebpImage(r6, r23, r8, null, r7);
            r0.close();
     */
        /* JADX WARNING: Missing block: B:171:0x03ad, code:
            if (r6 != null) goto L_0x0498;
     */
        /* JADX WARNING: Missing block: B:173:0x03b7, code:
            if (r26.length() == 0) goto L_0x03c1;
     */
        /* JADX WARNING: Missing block: B:175:0x03bf, code:
            if (r69.cacheImage.filter != null) goto L_0x02c2;
     */
        /* JADX WARNING: Missing block: B:176:0x03c1, code:
            r26.delete();
     */
        /* JADX WARNING: Missing block: B:177:0x03c6, code:
            r31 = th;
     */
        /* JADX WARNING: Missing block: B:178:0x03c9, code:
            r7 = false;
     */
        /* JADX WARNING: Missing block: B:181:0x03cf, code:
            if (r51.inPurgeable != false) goto L_0x03d3;
     */
        /* JADX WARNING: Missing block: B:182:0x03d1, code:
            if (r64 == null) goto L_0x046f;
     */
        /* JADX WARNING: Missing block: B:183:0x03d3, code:
            r0 = new java.io.RandomAccessFile(r26, "r");
            r44 = (int) r0.length();
            r50 = 0;
            r25 = (byte[]) org.telegram.messenger.ImageLoader.access$2000().get();
     */
        /* JADX WARNING: Missing block: B:184:0x03f2, code:
            if (r25 == null) goto L_0x045b;
     */
        /* JADX WARNING: Missing block: B:186:0x03f9, code:
            if (r25.length < r44) goto L_0x045b;
     */
        /* JADX WARNING: Missing block: B:187:0x03fb, code:
            r29 = r25;
     */
        /* JADX WARNING: Missing block: B:188:0x03fd, code:
            if (r29 != null) goto L_0x0410;
     */
        /* JADX WARNING: Missing block: B:189:0x03ff, code:
            r29 = new byte[r44];
            org.telegram.messenger.ImageLoader.access$2000().set(r29);
     */
        /* JADX WARNING: Missing block: B:190:0x0410, code:
            r0.readFully(r29, 0, r44);
            r0.close();
            r32 = false;
     */
        /* JADX WARNING: Missing block: B:191:0x041f, code:
            if (r64 == null) goto L_0x045e;
     */
        /* JADX WARNING: Missing block: B:192:0x0421, code:
            org.telegram.messenger.secretmedia.EncryptedFileInputStream.decryptBytesWithKeyFile(r29, 0, r44, r64);
            r39 = org.telegram.messenger.Utilities.computeSHA256(r29, 0, r44);
     */
        /* JADX WARNING: Missing block: B:193:0x0434, code:
            if (r63 == null) goto L_0x0440;
     */
        /* JADX WARNING: Missing block: B:195:0x043e, code:
            if (java.util.Arrays.equals(r39, r63) != false) goto L_0x0442;
     */
        /* JADX WARNING: Missing block: B:196:0x0440, code:
            r32 = true;
     */
        /* JADX WARNING: Missing block: B:197:0x0442, code:
            r50 = r29[0] & 255;
            r44 = r44 - r50;
     */
        /* JADX WARNING: Missing block: B:198:0x044b, code:
            if (r32 != false) goto L_0x0b21;
     */
        /* JADX WARNING: Missing block: B:199:0x044d, code:
            r6 = android.graphics.BitmapFactory.decodeByteArray(r29, r50, r44, r51);
     */
        /* JADX WARNING: Missing block: B:200:0x045b, code:
            r29 = null;
     */
        /* JADX WARNING: Missing block: B:201:0x045e, code:
            if (r42 == false) goto L_0x044b;
     */
        /* JADX WARNING: Missing block: B:202:0x0460, code:
            org.telegram.messenger.secretmedia.EncryptedFileInputStream.decryptBytesWithKeyFile(r29, 0, r44, r69.cacheImage.encryptionKeyPath);
     */
        /* JADX WARNING: Missing block: B:203:0x046f, code:
            if (r42 == false) goto L_0x048e;
     */
        /* JADX WARNING: Missing block: B:204:0x0471, code:
            r0 = new org.telegram.messenger.secretmedia.EncryptedFileInputStream(r26, r69.cacheImage.encryptionKeyPath);
     */
        /* JADX WARNING: Missing block: B:205:0x0480, code:
            r6 = android.graphics.BitmapFactory.decodeStream(r43, null, r51);
     */
        /* JADX WARNING: Missing block: B:207:?, code:
            r43.close();
     */
        /* JADX WARNING: Missing block: B:209:?, code:
            r0 = new java.io.FileInputStream(r26);
     */
        /* JADX WARNING: Missing block: B:210:0x0498, code:
            if (r28 == false) goto L_0x04b5;
     */
        /* JADX WARNING: Missing block: B:213:0x049e, code:
            if (r51.inPurgeable == false) goto L_0x04db;
     */
        /* JADX WARNING: Missing block: B:214:0x04a0, code:
            r7 = 0;
     */
        /* JADX WARNING: Missing block: B:216:0x04b1, code:
            if (org.telegram.messenger.Utilities.needInvert(r6, r7, r6.getWidth(), r6.getHeight(), r6.getRowBytes()) == 0) goto L_0x04dd;
     */
        /* JADX WARNING: Missing block: B:217:0x04b3, code:
            r49 = true;
     */
        /* JADX WARNING: Missing block: B:219:0x04b8, code:
            if (r20 != 1) goto L_0x04e2;
     */
        /* JADX WARNING: Missing block: B:221:0x04c0, code:
            if (r6.getConfig() != android.graphics.Bitmap.Config.ARGB_8888) goto L_0x02c2;
     */
        /* JADX WARNING: Missing block: B:223:0x04c7, code:
            if (r51.inPurgeable == false) goto L_0x04e0;
     */
        /* JADX WARNING: Missing block: B:224:0x04c9, code:
            r8 = 0;
     */
        /* JADX WARNING: Missing block: B:225:0x04ca, code:
            org.telegram.messenger.Utilities.blurBitmap(r6, 3, r8, r6.getWidth(), r6.getHeight(), r6.getRowBytes());
     */
        /* JADX WARNING: Missing block: B:226:0x04db, code:
            r7 = 1;
     */
        /* JADX WARNING: Missing block: B:227:0x04dd, code:
            r49 = false;
     */
        /* JADX WARNING: Missing block: B:228:0x04e0, code:
            r8 = 1;
     */
        /* JADX WARNING: Missing block: B:230:0x04e5, code:
            if (r20 != 2) goto L_0x050a;
     */
        /* JADX WARNING: Missing block: B:232:0x04ed, code:
            if (r6.getConfig() != android.graphics.Bitmap.Config.ARGB_8888) goto L_0x02c2;
     */
        /* JADX WARNING: Missing block: B:234:0x04f4, code:
            if (r51.inPurgeable == false) goto L_0x0508;
     */
        /* JADX WARNING: Missing block: B:235:0x04f6, code:
            r8 = 0;
     */
        /* JADX WARNING: Missing block: B:236:0x04f7, code:
            org.telegram.messenger.Utilities.blurBitmap(r6, 1, r8, r6.getWidth(), r6.getHeight(), r6.getRowBytes());
     */
        /* JADX WARNING: Missing block: B:237:0x0508, code:
            r8 = 1;
     */
        /* JADX WARNING: Missing block: B:239:0x050d, code:
            if (r20 != 3) goto L_0x0564;
     */
        /* JADX WARNING: Missing block: B:241:0x0515, code:
            if (r6.getConfig() != android.graphics.Bitmap.Config.ARGB_8888) goto L_0x02c2;
     */
        /* JADX WARNING: Missing block: B:243:0x051c, code:
            if (r51.inPurgeable == false) goto L_0x055e;
     */
        /* JADX WARNING: Missing block: B:244:0x051e, code:
            r8 = 0;
     */
        /* JADX WARNING: Missing block: B:245:0x051f, code:
            org.telegram.messenger.Utilities.blurBitmap(r6, 7, r8, r6.getWidth(), r6.getHeight(), r6.getRowBytes());
     */
        /* JADX WARNING: Missing block: B:246:0x0533, code:
            if (r51.inPurgeable == false) goto L_0x0560;
     */
        /* JADX WARNING: Missing block: B:247:0x0535, code:
            r8 = 0;
     */
        /* JADX WARNING: Missing block: B:248:0x0536, code:
            org.telegram.messenger.Utilities.blurBitmap(r6, 7, r8, r6.getWidth(), r6.getHeight(), r6.getRowBytes());
     */
        /* JADX WARNING: Missing block: B:249:0x054a, code:
            if (r51.inPurgeable == false) goto L_0x0562;
     */
        /* JADX WARNING: Missing block: B:250:0x054c, code:
            r8 = 0;
     */
        /* JADX WARNING: Missing block: B:251:0x054d, code:
            org.telegram.messenger.Utilities.blurBitmap(r6, 7, r8, r6.getWidth(), r6.getHeight(), r6.getRowBytes());
     */
        /* JADX WARNING: Missing block: B:252:0x055e, code:
            r8 = 1;
     */
        /* JADX WARNING: Missing block: B:253:0x0560, code:
            r8 = 1;
     */
        /* JADX WARNING: Missing block: B:254:0x0562, code:
            r8 = 1;
     */
        /* JADX WARNING: Missing block: B:255:0x0564, code:
            if (r20 != 0) goto L_0x02c2;
     */
        /* JADX WARNING: Missing block: B:257:0x056a, code:
            if (r51.inPurgeable == false) goto L_0x02c2;
     */
        /* JADX WARNING: Missing block: B:258:0x056c, code:
            org.telegram.messenger.Utilities.pinBitmap(r6);
     */
        /* JADX WARNING: Missing block: B:259:0x0571, code:
            r48 = null;
     */
        /* JADX WARNING: Missing block: B:262:0x057b, code:
            if ((r69.cacheImage.location instanceof java.lang.String) != false) goto L_0x057d;
     */
        /* JADX WARNING: Missing block: B:263:0x057d, code:
            r45 = r69.cacheImage.location;
     */
        /* JADX WARNING: Missing block: B:264:0x0590, code:
            if (r45.startsWith("thumb://") != false) goto L_0x0592;
     */
        /* JADX WARNING: Missing block: B:265:0x0592, code:
            r40 = r45.indexOf(":", 8);
     */
        /* JADX WARNING: Missing block: B:266:0x059d, code:
            if (r40 >= 0) goto L_0x059f;
     */
        /* JADX WARNING: Missing block: B:267:0x059f, code:
            r46 = java.lang.Long.valueOf(java.lang.Long.parseLong(r45.substring(8, r40)));
            r47 = false;
            r48 = r45.substring(r40 + 1);
     */
        /* JADX WARNING: Missing block: B:268:0x05bb, code:
            r27 = false;
     */
        /* JADX WARNING: Missing block: B:269:0x05bd, code:
            r30 = 20;
     */
        /* JADX WARNING: Missing block: B:270:0x05bf, code:
            if (r46 != null) goto L_0x05c1;
     */
        /* JADX WARNING: Missing block: B:271:0x05c1, code:
            r30 = 0;
     */
        /* JADX WARNING: Missing block: B:279:0x05ed, code:
            java.lang.Thread.sleep((long) r30);
     */
        /* JADX WARNING: Missing block: B:280:0x05f3, code:
            org.telegram.messenger.ImageLoader.access$1902(r69.this$0, java.lang.System.currentTimeMillis());
     */
        /* JADX WARNING: Missing block: B:281:0x0602, code:
            monitor-enter(r69.sync);
     */
        /* JADX WARNING: Missing block: B:284:0x0607, code:
            if (r69.isCancelled != false) goto L_0x0609;
     */
        /* JADX WARNING: Missing block: B:291:0x0610, code:
            r6 = null;
     */
        /* JADX WARNING: Missing block: B:293:0x061d, code:
            if (r45.startsWith("vthumb://") != false) goto L_0x061f;
     */
        /* JADX WARNING: Missing block: B:294:0x061f, code:
            r40 = r45.indexOf(":", 9);
     */
        /* JADX WARNING: Missing block: B:295:0x062a, code:
            if (r40 >= 0) goto L_0x062c;
     */
        /* JADX WARNING: Missing block: B:296:0x062c, code:
            r46 = java.lang.Long.valueOf(java.lang.Long.parseLong(r45.substring(9, r40)));
            r47 = true;
     */
        /* JADX WARNING: Missing block: B:297:0x0640, code:
            r27 = false;
     */
        /* JADX WARNING: Missing block: B:299:0x064d, code:
            if (r45.startsWith("http") == false) goto L_0x064f;
     */
        /* JADX WARNING: Missing block: B:300:0x064f, code:
            r27 = false;
     */
        /* JADX WARNING: Missing block: B:304:?, code:
            r51 = new android.graphics.BitmapFactory.Options();
            r51.inSampleSize = 1;
            r68 = 0.0f;
            r38 = 0.0f;
            r19 = false;
            r28 = false;
            r36 = org.telegram.messenger.ImageLoader.access$2100(r69.this$0);
     */
        /* JADX WARNING: Missing block: B:305:0x0674, code:
            if (r69.cacheImage.filter == null) goto L_0x07fa;
     */
        /* JADX WARNING: Missing block: B:306:0x0676, code:
            r14 = r69.cacheImage.filter.split("_");
     */
        /* JADX WARNING: Missing block: B:307:0x0685, code:
            if (r14.length < 2) goto L_0x069d;
     */
        /* JADX WARNING: Missing block: B:308:0x0687, code:
            r68 = java.lang.Float.parseFloat(r14[0]) * org.telegram.messenger.AndroidUtilities.density;
            r38 = java.lang.Float.parseFloat(r14[1]) * org.telegram.messenger.AndroidUtilities.density;
     */
        /* JADX WARNING: Missing block: B:310:0x06aa, code:
            if (r69.cacheImage.filter.contains("b") == false) goto L_0x06ae;
     */
        /* JADX WARNING: Missing block: B:311:0x06ac, code:
            r19 = true;
     */
        /* JADX WARNING: Missing block: B:313:0x06bb, code:
            if (r69.cacheImage.filter.contains("i") == false) goto L_0x06bf;
     */
        /* JADX WARNING: Missing block: B:314:0x06bd, code:
            r28 = true;
     */
        /* JADX WARNING: Missing block: B:316:0x06cc, code:
            if (r69.cacheImage.filter.contains("f") == false) goto L_0x06d0;
     */
        /* JADX WARNING: Missing block: B:317:0x06ce, code:
            r36 = true;
     */
        /* JADX WARNING: Missing block: B:319:0x06d3, code:
            if (r68 == 0.0f) goto L_0x0b19;
     */
        /* JADX WARNING: Missing block: B:321:0x06d8, code:
            if (r38 == 0.0f) goto L_0x0b19;
     */
        /* JADX WARNING: Missing block: B:322:0x06da, code:
            r51.inJustDecodeBounds = true;
     */
        /* JADX WARNING: Missing block: B:323:0x06df, code:
            if (r46 == null) goto L_0x0748;
     */
        /* JADX WARNING: Missing block: B:324:0x06e1, code:
            if (r48 != null) goto L_0x0748;
     */
        /* JADX WARNING: Missing block: B:325:0x06e3, code:
            if (r47 == false) goto L_0x0735;
     */
        /* JADX WARNING: Missing block: B:326:0x06e5, code:
            android.provider.MediaStore.Video.Thumbnails.getThumbnail(org.telegram.messenger.ApplicationLoader.applicationContext.getContentResolver(), r46.longValue(), 1, r51);
     */
        /* JADX WARNING: Missing block: B:327:0x06f5, code:
            r6 = null;
     */
        /* JADX WARNING: Missing block: B:329:?, code:
            r61 = java.lang.Math.max(((float) r51.outWidth) / r68, ((float) r51.outHeight) / r38);
     */
        /* JADX WARNING: Missing block: B:330:0x0711, code:
            if (r61 >= 1.0f) goto L_0x0715;
     */
        /* JADX WARNING: Missing block: B:331:0x0713, code:
            r61 = 1.0f;
     */
        /* JADX WARNING: Missing block: B:332:0x0715, code:
            r51.inJustDecodeBounds = false;
            r51.inSampleSize = (int) r61;
     */
        /* JADX WARNING: Missing block: B:333:0x0721, code:
            r8 = r69.sync;
     */
        /* JADX WARNING: Missing block: B:334:0x0725, code:
            monitor-enter(r8);
     */
        /* JADX WARNING: Missing block: B:337:0x072a, code:
            if (r69.isCancelled == false) goto L_0x085c;
     */
        /* JADX WARNING: Missing block: B:338:0x072c, code:
            monitor-exit(r8);
     */
        /* JADX WARNING: Missing block: B:345:?, code:
            android.provider.MediaStore.Images.Thumbnails.getThumbnail(org.telegram.messenger.ApplicationLoader.applicationContext.getContentResolver(), r46.longValue(), 1, r51);
            r6 = null;
     */
        /* JADX WARNING: Missing block: B:346:0x0748, code:
            if (r64 == null) goto L_0x07d1;
     */
        /* JADX WARNING: Missing block: B:347:0x074a, code:
            r0 = new java.io.RandomAccessFile(r26, "r");
            r44 = (int) r0.length();
            r24 = (byte[]) org.telegram.messenger.ImageLoader.access$1600().get();
     */
        /* JADX WARNING: Missing block: B:348:0x0767, code:
            if (r24 == null) goto L_0x07ce;
     */
        /* JADX WARNING: Missing block: B:350:0x076e, code:
            if (r24.length < r44) goto L_0x07ce;
     */
        /* JADX WARNING: Missing block: B:351:0x0770, code:
            r29 = r24;
     */
        /* JADX WARNING: Missing block: B:352:0x0772, code:
            if (r29 != null) goto L_0x0785;
     */
        /* JADX WARNING: Missing block: B:353:0x0774, code:
            r29 = new byte[r44];
            org.telegram.messenger.ImageLoader.access$1600().set(r29);
     */
        /* JADX WARNING: Missing block: B:354:0x0785, code:
            r0.readFully(r29, 0, r44);
            r0.close();
            org.telegram.messenger.secretmedia.EncryptedFileInputStream.decryptBytesWithKeyFile(r29, 0, r44, r64);
            r39 = org.telegram.messenger.Utilities.computeSHA256(r29, 0, r44);
            r32 = false;
     */
        /* JADX WARNING: Missing block: B:355:0x07a7, code:
            if (r63 == null) goto L_0x07b3;
     */
        /* JADX WARNING: Missing block: B:357:0x07b1, code:
            if (java.util.Arrays.equals(r39, r63) != false) goto L_0x07b5;
     */
        /* JADX WARNING: Missing block: B:358:0x07b3, code:
            r32 = true;
     */
        /* JADX WARNING: Missing block: B:359:0x07b5, code:
            r50 = r29[0] & 255;
            r44 = r44 - r50;
     */
        /* JADX WARNING: Missing block: B:360:0x07be, code:
            if (r32 != false) goto L_0x0b1d;
     */
        /* JADX WARNING: Missing block: B:361:0x07c0, code:
            r6 = android.graphics.BitmapFactory.decodeByteArray(r29, r50, r44, r51);
     */
        /* JADX WARNING: Missing block: B:362:0x07ce, code:
            r29 = null;
     */
        /* JADX WARNING: Missing block: B:363:0x07d1, code:
            if (r42 == false) goto L_0x07f0;
     */
        /* JADX WARNING: Missing block: B:364:0x07d3, code:
            r0 = new org.telegram.messenger.secretmedia.EncryptedFileInputStream(r26, r69.cacheImage.encryptionKeyPath);
     */
        /* JADX WARNING: Missing block: B:365:0x07e2, code:
            r6 = android.graphics.BitmapFactory.decodeStream(r43, null, r51);
     */
        /* JADX WARNING: Missing block: B:367:?, code:
            r43.close();
     */
        /* JADX WARNING: Missing block: B:369:?, code:
            r0 = new java.io.FileInputStream(r26);
     */
        /* JADX WARNING: Missing block: B:370:0x07fa, code:
            if (r48 == null) goto L_0x0b19;
     */
        /* JADX WARNING: Missing block: B:371:0x07fc, code:
            r51.inJustDecodeBounds = true;
     */
        /* JADX WARNING: Missing block: B:372:0x0801, code:
            if (r36 == false) goto L_0x0859;
     */
        /* JADX WARNING: Missing block: B:373:0x0803, code:
            r7 = android.graphics.Bitmap.Config.ARGB_8888;
     */
        /* JADX WARNING: Missing block: B:374:0x0805, code:
            r51.inPreferredConfig = r7;
            r0 = new java.io.FileInputStream(r26);
            r6 = android.graphics.BitmapFactory.decodeStream(r0, null, r51);
     */
        /* JADX WARNING: Missing block: B:376:?, code:
            r0.close();
            r57 = r51.outWidth;
            r54 = r51.outHeight;
            r51.inJustDecodeBounds = false;
            r61 = (float) java.lang.Math.max(r57 / 200, r54 / 200);
     */
        /* JADX WARNING: Missing block: B:377:0x0842, code:
            if (r61 >= 1.0f) goto L_0x0846;
     */
        /* JADX WARNING: Missing block: B:378:0x0844, code:
            r61 = 1.0f;
     */
        /* JADX WARNING: Missing block: B:379:0x0846, code:
            r60 = 1;
     */
        /* JADX WARNING: Missing block: B:380:0x0848, code:
            r60 = r60 * 2;
     */
        /* JADX WARNING: Missing block: B:381:0x084f, code:
            if (((float) (r60 * 2)) < r61) goto L_0x0848;
     */
        /* JADX WARNING: Missing block: B:382:0x0851, code:
            r51.inSampleSize = r60;
     */
        /* JADX WARNING: Missing block: B:384:?, code:
            r7 = android.graphics.Bitmap.Config.RGB_565;
     */
        /* JADX WARNING: Missing block: B:386:?, code:
            monitor-exit(r8);
     */
        /* JADX WARNING: Missing block: B:387:0x085d, code:
            if (r36 != false) goto L_0x0873;
     */
        /* JADX WARNING: Missing block: B:390:0x0865, code:
            if (r69.cacheImage.filter == null) goto L_0x0873;
     */
        /* JADX WARNING: Missing block: B:391:0x0867, code:
            if (r19 != false) goto L_0x0873;
     */
        /* JADX WARNING: Missing block: B:393:0x0871, code:
            if ((r69.cacheImage.location instanceof java.lang.String) == false) goto L_0x0914;
     */
        /* JADX WARNING: Missing block: B:394:0x0873, code:
            r51.inPreferredConfig = android.graphics.Bitmap.Config.ARGB_8888;
     */
        /* JADX WARNING: Missing block: B:396:0x087d, code:
            if (android.os.Build.VERSION.SDK_INT >= 21) goto L_0x0884;
     */
        /* JADX WARNING: Missing block: B:397:0x087f, code:
            r51.inPurgeable = true;
     */
        /* JADX WARNING: Missing block: B:398:0x0884, code:
            r51.inDither = false;
     */
        /* JADX WARNING: Missing block: B:399:0x0889, code:
            if (r46 == null) goto L_0x08a0;
     */
        /* JADX WARNING: Missing block: B:400:0x088b, code:
            if (r48 != null) goto L_0x08a0;
     */
        /* JADX WARNING: Missing block: B:401:0x088d, code:
            if (r47 == false) goto L_0x091c;
     */
        /* JADX WARNING: Missing block: B:402:0x088f, code:
            r6 = android.provider.MediaStore.Video.Thumbnails.getThumbnail(org.telegram.messenger.ApplicationLoader.applicationContext.getContentResolver(), r46.longValue(), 1, r51);
     */
        /* JADX WARNING: Missing block: B:403:0x08a0, code:
            if (r6 != null) goto L_0x08f9;
     */
        /* JADX WARNING: Missing block: B:404:0x08a2, code:
            if (r66 == false) goto L_0x0931;
     */
        /* JADX WARNING: Missing block: B:405:0x08a4, code:
            r0 = new java.io.RandomAccessFile(r26, "r");
            r23 = r0.getChannel().map(java.nio.channels.FileChannel.MapMode.READ_ONLY, 0, r26.length());
            r22 = new android.graphics.BitmapFactory.Options();
            r22.inJustDecodeBounds = true;
            org.telegram.messenger.Utilities.loadWebpImage(null, r23, r23.limit(), r22, true);
            r6 = org.telegram.messenger.Bitmaps.createBitmap(r22.outWidth, r22.outHeight, android.graphics.Bitmap.Config.ARGB_8888);
            r8 = r23.limit();
     */
        /* JADX WARNING: Missing block: B:406:0x08ee, code:
            if (r51.inPurgeable != false) goto L_0x092f;
     */
        /* JADX WARNING: Missing block: B:407:0x08f0, code:
            r7 = true;
     */
        /* JADX WARNING: Missing block: B:408:0x08f1, code:
            org.telegram.messenger.Utilities.loadWebpImage(r6, r23, r8, null, r7);
            r0.close();
     */
        /* JADX WARNING: Missing block: B:416:0x0914, code:
            r51.inPreferredConfig = android.graphics.Bitmap.Config.RGB_565;
     */
        /* JADX WARNING: Missing block: B:417:0x091c, code:
            r6 = android.provider.MediaStore.Images.Thumbnails.getThumbnail(org.telegram.messenger.ApplicationLoader.applicationContext.getContentResolver(), r46.longValue(), 1, r51);
     */
        /* JADX WARNING: Missing block: B:418:0x092f, code:
            r7 = false;
     */
        /* JADX WARNING: Missing block: B:420:0x0935, code:
            if (r51.inPurgeable != false) goto L_0x0939;
     */
        /* JADX WARNING: Missing block: B:421:0x0937, code:
            if (r64 == null) goto L_0x09d5;
     */
        /* JADX WARNING: Missing block: B:422:0x0939, code:
            r0 = new java.io.RandomAccessFile(r26, "r");
            r44 = (int) r0.length();
            r50 = 0;
            r24 = (byte[]) org.telegram.messenger.ImageLoader.access$1600().get();
     */
        /* JADX WARNING: Missing block: B:423:0x0958, code:
            if (r24 == null) goto L_0x09c1;
     */
        /* JADX WARNING: Missing block: B:425:0x095f, code:
            if (r24.length < r44) goto L_0x09c1;
     */
        /* JADX WARNING: Missing block: B:426:0x0961, code:
            r29 = r24;
     */
        /* JADX WARNING: Missing block: B:427:0x0963, code:
            if (r29 != null) goto L_0x0976;
     */
        /* JADX WARNING: Missing block: B:428:0x0965, code:
            r29 = new byte[r44];
            org.telegram.messenger.ImageLoader.access$1600().set(r29);
     */
        /* JADX WARNING: Missing block: B:429:0x0976, code:
            r0.readFully(r29, 0, r44);
            r0.close();
            r32 = false;
     */
        /* JADX WARNING: Missing block: B:430:0x0985, code:
            if (r64 == null) goto L_0x09c4;
     */
        /* JADX WARNING: Missing block: B:431:0x0987, code:
            org.telegram.messenger.secretmedia.EncryptedFileInputStream.decryptBytesWithKeyFile(r29, 0, r44, r64);
            r39 = org.telegram.messenger.Utilities.computeSHA256(r29, 0, r44);
     */
        /* JADX WARNING: Missing block: B:432:0x099a, code:
            if (r63 == null) goto L_0x09a6;
     */
        /* JADX WARNING: Missing block: B:434:0x09a4, code:
            if (java.util.Arrays.equals(r39, r63) != false) goto L_0x09a8;
     */
        /* JADX WARNING: Missing block: B:435:0x09a6, code:
            r32 = true;
     */
        /* JADX WARNING: Missing block: B:436:0x09a8, code:
            r50 = r29[0] & 255;
            r44 = r44 - r50;
     */
        /* JADX WARNING: Missing block: B:437:0x09b1, code:
            if (r32 != false) goto L_0x08f9;
     */
        /* JADX WARNING: Missing block: B:438:0x09b3, code:
            r6 = android.graphics.BitmapFactory.decodeByteArray(r29, r50, r44, r51);
     */
        /* JADX WARNING: Missing block: B:439:0x09c1, code:
            r29 = null;
     */
        /* JADX WARNING: Missing block: B:440:0x09c4, code:
            if (r42 == false) goto L_0x09b1;
     */
        /* JADX WARNING: Missing block: B:441:0x09c6, code:
            org.telegram.messenger.secretmedia.EncryptedFileInputStream.decryptBytesWithKeyFile(r29, 0, r44, r69.cacheImage.encryptionKeyPath);
     */
        /* JADX WARNING: Missing block: B:442:0x09d5, code:
            if (r42 == false) goto L_0x0a1d;
     */
        /* JADX WARNING: Missing block: B:443:0x09d7, code:
            r0 = new org.telegram.messenger.secretmedia.EncryptedFileInputStream(r26, r69.cacheImage.encryptionKeyPath);
     */
        /* JADX WARNING: Missing block: B:445:0x09ee, code:
            if ((r69.cacheImage.location instanceof org.telegram.tgnet.TLRPC.TL_document) == false) goto L_0x0a0f;
     */
        /* JADX WARNING: Missing block: B:448:0x0a03, code:
            switch(new android.support.media.ExifInterface(r43).getAttributeInt("Orientation", 1)) {
                case 3: goto L_0x0a2a;
                case 4: goto L_0x0a06;
                case 5: goto L_0x0a06;
                case 6: goto L_0x0a27;
                case 7: goto L_0x0a06;
                case 8: goto L_0x0a2d;
                default: goto L_0x0a06;
            };
     */
        /* JADX WARNING: Missing block: B:452:0x0a1d, code:
            r0 = new java.io.FileInputStream(r26);
     */
        /* JADX WARNING: Missing block: B:453:0x0a27, code:
            r52 = 90;
     */
        /* JADX WARNING: Missing block: B:454:0x0a2a, code:
            r52 = 180;
     */
        /* JADX WARNING: Missing block: B:455:0x0a2d, code:
            r52 = 270;
     */
        /* JADX WARNING: Missing block: B:502:0x0af9, code:
            r7 = null;
     */
        /* JADX WARNING: Missing block: B:503:0x0afc, code:
            if (r6 != null) goto L_0x0afe;
     */
        /* JADX WARNING: Missing block: B:504:0x0afe, code:
            r7 = new android.graphics.drawable.BitmapDrawable(r6);
     */
        /* JADX WARNING: Missing block: B:505:0x0b03, code:
            onPostExecute(r7);
     */
        /* JADX WARNING: Missing block: B:506:0x0b0a, code:
            r7 = null;
     */
        /* JADX WARNING: Missing block: B:508:0x0b0f, code:
            r7 = th;
     */
        /* JADX WARNING: Missing block: B:509:0x0b10, code:
            r58 = r0;
     */
        /* JADX WARNING: Missing block: B:510:0x0b14, code:
            r31 = e;
     */
        /* JADX WARNING: Missing block: B:511:0x0b15, code:
            r58 = r0;
     */
        /* JADX WARNING: Missing block: B:512:0x0b19, code:
            r6 = null;
     */
        /* JADX WARNING: Missing block: B:513:0x0b1d, code:
            r6 = null;
     */
        /* JADX WARNING: Missing block: B:514:0x0b21, code:
            r6 = null;
     */
        /* JADX WARNING: Missing block: B:520:?, code:
            return;
     */
        /* JADX WARNING: Missing block: B:521:?, code:
            return;
     */
        /* JADX WARNING: Missing block: B:523:?, code:
            return;
     */
        /* JADX WARNING: Missing block: B:524:?, code:
            return;
     */
        /* JADX WARNING: Missing block: B:525:?, code:
            return;
     */
        /* JADX WARNING: Missing block: B:526:?, code:
            return;
     */
        /* JADX WARNING: Missing block: B:527:?, code:
            return;
     */
        /* JADX WARNING: Missing block: B:528:?, code:
            return;
     */
        public void run() {
            /*
            r69 = this;
            r0 = r69;
            r8 = r0.sync;
            monitor-enter(r8);
            r7 = java.lang.Thread.currentThread();	 Catch:{ all -> 0x0033 }
            r0 = r69;
            r0.runningThread = r7;	 Catch:{ all -> 0x0033 }
            java.lang.Thread.interrupted();	 Catch:{ all -> 0x0033 }
            r0 = r69;
            r7 = r0.isCancelled;	 Catch:{ all -> 0x0033 }
            if (r7 == 0) goto L_0x0018;
        L_0x0016:
            monitor-exit(r8);	 Catch:{ all -> 0x0033 }
        L_0x0017:
            return;
        L_0x0018:
            monitor-exit(r8);	 Catch:{ all -> 0x0033 }
            r0 = r69;
            r7 = r0.cacheImage;
            r7 = r7.location;
            r7 = r7 instanceof org.telegram.tgnet.TLRPC.TL_photoStrippedSize;
            if (r7 == 0) goto L_0x0110;
        L_0x0023:
            r0 = r69;
            r8 = r0.sync;
            monitor-enter(r8);
            r0 = r69;
            r7 = r0.isCancelled;	 Catch:{ all -> 0x0030 }
            if (r7 == 0) goto L_0x0036;
        L_0x002e:
            monitor-exit(r8);	 Catch:{ all -> 0x0030 }
            goto L_0x0017;
        L_0x0030:
            r7 = move-exception;
            monitor-exit(r8);	 Catch:{ all -> 0x0030 }
            throw r7;
        L_0x0033:
            r7 = move-exception;
            monitor-exit(r8);	 Catch:{ all -> 0x0033 }
            throw r7;
        L_0x0036:
            monitor-exit(r8);	 Catch:{ all -> 0x0030 }
            r0 = r69;
            r7 = r0.cacheImage;
            r0 = r7.location;
            r55 = r0;
            r55 = (org.telegram.tgnet.TLRPC.TL_photoStrippedSize) r55;
            r0 = r55;
            r7 = r0.bytes;
            r7 = r7.length;
            r7 = r7 + -3;
            r8 = org.telegram.messenger.Bitmaps.header;
            r8 = r8.length;
            r7 = r7 + r8;
            r8 = org.telegram.messenger.Bitmaps.footer;
            r8 = r8.length;
            r44 = r7 + r8;
            r7 = org.telegram.messenger.ImageLoader.bytesLocal;
            r24 = r7.get();
            r24 = (byte[]) r24;
            if (r24 == 0) goto L_0x010a;
        L_0x005d:
            r0 = r24;
            r7 = r0.length;
            r0 = r44;
            if (r7 < r0) goto L_0x010a;
        L_0x0064:
            r29 = r24;
        L_0x0066:
            if (r29 != 0) goto L_0x0079;
        L_0x0068:
            r0 = r44;
            r0 = new byte[r0];
            r29 = r0;
            r24 = r29;
            r7 = org.telegram.messenger.ImageLoader.bytesLocal;
            r0 = r24;
            r7.set(r0);
        L_0x0079:
            r7 = org.telegram.messenger.Bitmaps.header;
            r8 = 0;
            r9 = 0;
            r10 = org.telegram.messenger.Bitmaps.header;
            r10 = r10.length;
            r0 = r29;
            java.lang.System.arraycopy(r7, r8, r0, r9, r10);
            r0 = r55;
            r7 = r0.bytes;
            r8 = 3;
            r9 = org.telegram.messenger.Bitmaps.header;
            r9 = r9.length;
            r0 = r55;
            r10 = r0.bytes;
            r10 = r10.length;
            r10 = r10 + -3;
            r0 = r29;
            java.lang.System.arraycopy(r7, r8, r0, r9, r10);
            r7 = org.telegram.messenger.Bitmaps.footer;
            r8 = 0;
            r9 = org.telegram.messenger.Bitmaps.header;
            r9 = r9.length;
            r0 = r55;
            r10 = r0.bytes;
            r10 = r10.length;
            r9 = r9 + r10;
            r9 = r9 + -3;
            r10 = org.telegram.messenger.Bitmaps.footer;
            r10 = r10.length;
            r0 = r29;
            java.lang.System.arraycopy(r7, r8, r0, r9, r10);
            r7 = 164; // 0xa4 float:2.3E-43 double:8.1E-322;
            r0 = r55;
            r8 = r0.bytes;
            r9 = 1;
            r8 = r8[r9];
            r29[r7] = r8;
            r7 = 166; // 0xa6 float:2.33E-43 double:8.2E-322;
            r0 = r55;
            r8 = r0.bytes;
            r9 = 2;
            r8 = r8[r9];
            r29[r7] = r8;
            r7 = 0;
            r0 = r29;
            r1 = r44;
            r4 = android.graphics.BitmapFactory.decodeByteArray(r0, r7, r1);
            if (r4 == 0) goto L_0x00fc;
        L_0x00d0:
            r0 = r69;
            r7 = r0.cacheImage;
            r7 = r7.filter;
            r7 = android.text.TextUtils.isEmpty(r7);
            if (r7 != 0) goto L_0x00fc;
        L_0x00dc:
            r0 = r69;
            r7 = r0.cacheImage;
            r7 = r7.filter;
            r8 = "b";
            r7 = r7.contains(r8);
            if (r7 == 0) goto L_0x00fc;
        L_0x00eb:
            r5 = 3;
            r6 = 1;
            r7 = r4.getWidth();
            r8 = r4.getHeight();
            r9 = r4.getRowBytes();
            org.telegram.messenger.Utilities.blurBitmap(r4, r5, r6, r7, r8, r9);
        L_0x00fc:
            if (r4 == 0) goto L_0x010e;
        L_0x00fe:
            r7 = new android.graphics.drawable.BitmapDrawable;
            r7.<init>(r4);
        L_0x0103:
            r0 = r69;
            r0.onPostExecute(r7);
            goto L_0x0017;
        L_0x010a:
            r29 = 0;
            goto L_0x0066;
        L_0x010e:
            r7 = 0;
            goto L_0x0103;
        L_0x0110:
            r0 = r69;
            r7 = r0.cacheImage;
            r7 = r7.animatedFile;
            if (r7 == 0) goto L_0x01a3;
        L_0x0118:
            r0 = r69;
            r8 = r0.sync;
            monitor-enter(r8);
            r0 = r69;
            r7 = r0.isCancelled;	 Catch:{ all -> 0x0126 }
            if (r7 == 0) goto L_0x0129;
        L_0x0123:
            monitor-exit(r8);	 Catch:{ all -> 0x0126 }
            goto L_0x0017;
        L_0x0126:
            r7 = move-exception;
            monitor-exit(r8);	 Catch:{ all -> 0x0126 }
            throw r7;
        L_0x0129:
            monitor-exit(r8);	 Catch:{ all -> 0x0126 }
            r7 = "g";
            r0 = r69;
            r8 = r0.cacheImage;
            r8 = r8.filter;
            r7 = r7.equals(r8);
            if (r7 == 0) goto L_0x0180;
        L_0x0139:
            r0 = r69;
            r7 = r0.cacheImage;
            r7 = r7.location;
            r7 = r7 instanceof org.telegram.tgnet.TLRPC.TL_documentEncrypted;
            if (r7 != 0) goto L_0x0180;
        L_0x0143:
            r5 = new org.telegram.ui.Components.AnimatedFileDrawable;
            r0 = r69;
            r7 = r0.cacheImage;
            r6 = r7.finalFilePath;
            r7 = 0;
            r0 = r69;
            r8 = r0.cacheImage;
            r8 = r8.size;
            r8 = (long) r8;
            r0 = r69;
            r10 = r0.cacheImage;
            r10 = r10.location;
            r10 = r10 instanceof org.telegram.tgnet.TLRPC.Document;
            if (r10 == 0) goto L_0x017e;
        L_0x015d:
            r0 = r69;
            r10 = r0.cacheImage;
            r10 = r10.location;
            r10 = (org.telegram.tgnet.TLRPC.Document) r10;
        L_0x0165:
            r0 = r69;
            r11 = r0.cacheImage;
            r11 = r11.parentObject;
            r0 = r69;
            r12 = r0.cacheImage;
            r12 = r12.currentAccount;
            r5.<init>(r6, r7, r8, r10, r11, r12);
        L_0x0174:
            java.lang.Thread.interrupted();
            r0 = r69;
            r0.onPostExecute(r5);
            goto L_0x0017;
        L_0x017e:
            r10 = 0;
            goto L_0x0165;
        L_0x0180:
            r5 = new org.telegram.ui.Components.AnimatedFileDrawable;
            r0 = r69;
            r7 = r0.cacheImage;
            r6 = r7.finalFilePath;
            r7 = "d";
            r0 = r69;
            r8 = r0.cacheImage;
            r8 = r8.filter;
            r7 = r7.equals(r8);
            r8 = 0;
            r10 = 0;
            r11 = 0;
            r0 = r69;
            r12 = r0.cacheImage;
            r12 = r12.currentAccount;
            r5.<init>(r6, r7, r8, r10, r11, r12);
            goto L_0x0174;
        L_0x01a3:
            r46 = 0;
            r47 = 0;
            r41 = 0;
            r49 = 0;
            r52 = 0;
            r0 = r69;
            r7 = r0.cacheImage;
            r0 = r7.finalFilePath;
            r26 = r0;
            r0 = r69;
            r7 = r0.cacheImage;
            r7 = r7.secureDocument;
            if (r7 != 0) goto L_0x01d4;
        L_0x01bd:
            r0 = r69;
            r7 = r0.cacheImage;
            r7 = r7.encryptionKeyPath;
            if (r7 == 0) goto L_0x02db;
        L_0x01c5:
            if (r26 == 0) goto L_0x02db;
        L_0x01c7:
            r7 = r26.getAbsolutePath();
            r8 = ".enc";
            r7 = r7.endsWith(r8);
            if (r7 == 0) goto L_0x02db;
        L_0x01d4:
            r42 = 1;
        L_0x01d6:
            r0 = r69;
            r7 = r0.cacheImage;
            r7 = r7.secureDocument;
            if (r7 == 0) goto L_0x02eb;
        L_0x01de:
            r0 = r69;
            r7 = r0.cacheImage;
            r7 = r7.secureDocument;
            r0 = r7.secureDocumentKey;
            r64 = r0;
            r0 = r69;
            r7 = r0.cacheImage;
            r7 = r7.secureDocument;
            r7 = r7.secureFile;
            if (r7 == 0) goto L_0x02df;
        L_0x01f2:
            r0 = r69;
            r7 = r0.cacheImage;
            r7 = r7.secureDocument;
            r7 = r7.secureFile;
            r7 = r7.file_hash;
            if (r7 == 0) goto L_0x02df;
        L_0x01fe:
            r0 = r69;
            r7 = r0.cacheImage;
            r7 = r7.secureDocument;
            r7 = r7.secureFile;
            r0 = r7.file_hash;
            r63 = r0;
        L_0x020a:
            r27 = 1;
            r66 = 0;
            r7 = android.os.Build.VERSION.SDK_INT;
            r8 = 19;
            if (r7 >= r8) goto L_0x0269;
        L_0x0214:
            r58 = 0;
            r59 = new java.io.RandomAccessFile;	 Catch:{ Exception -> 0x02fd }
            r7 = "r";
            r0 = r59;
            r1 = r26;
            r0.<init>(r1, r7);	 Catch:{ Exception -> 0x02fd }
            r0 = r69;
            r7 = r0.cacheImage;	 Catch:{ Exception -> 0x0b14, all -> 0x0b0f }
            r7 = r7.imageType;	 Catch:{ Exception -> 0x0b14, all -> 0x0b0f }
            r8 = 1;
            if (r7 != r8) goto L_0x02f1;
        L_0x022b:
            r24 = org.telegram.messenger.ImageLoader.headerThumb;	 Catch:{ Exception -> 0x0b14, all -> 0x0b0f }
        L_0x022f:
            r7 = 0;
            r0 = r24;
            r8 = r0.length;	 Catch:{ Exception -> 0x0b14, all -> 0x0b0f }
            r0 = r59;
            r1 = r24;
            r0.readFully(r1, r7, r8);	 Catch:{ Exception -> 0x0b14, all -> 0x0b0f }
            r7 = new java.lang.String;	 Catch:{ Exception -> 0x0b14, all -> 0x0b0f }
            r0 = r24;
            r7.<init>(r0);	 Catch:{ Exception -> 0x0b14, all -> 0x0b0f }
            r65 = r7.toLowerCase();	 Catch:{ Exception -> 0x0b14, all -> 0x0b0f }
            r65 = r65.toLowerCase();	 Catch:{ Exception -> 0x0b14, all -> 0x0b0f }
            r7 = "riff";
            r0 = r65;
            r7 = r0.startsWith(r7);	 Catch:{ Exception -> 0x0b14, all -> 0x0b0f }
            if (r7 == 0) goto L_0x0261;
        L_0x0254:
            r7 = "webp";
            r0 = r65;
            r7 = r0.endsWith(r7);	 Catch:{ Exception -> 0x0b14, all -> 0x0b0f }
            if (r7 == 0) goto L_0x0261;
        L_0x025f:
            r66 = 1;
        L_0x0261:
            r59.close();	 Catch:{ Exception -> 0x0b14, all -> 0x0b0f }
            if (r59 == 0) goto L_0x0269;
        L_0x0266:
            r59.close();	 Catch:{ Exception -> 0x02f7 }
        L_0x0269:
            r0 = r69;
            r7 = r0.cacheImage;
            r7 = r7.imageType;
            r8 = 1;
            if (r7 != r8) goto L_0x0571;
        L_0x0272:
            r20 = 0;
            r28 = 0;
            r0 = r69;
            r7 = r0.cacheImage;
            r7 = r7.filter;
            if (r7 == 0) goto L_0x02a0;
        L_0x027e:
            r0 = r69;
            r7 = r0.cacheImage;
            r7 = r7.filter;
            r8 = "b2";
            r7 = r7.contains(r8);
            if (r7 == 0) goto L_0x031a;
        L_0x028d:
            r20 = 3;
        L_0x028f:
            r0 = r69;
            r7 = r0.cacheImage;
            r7 = r7.filter;
            r8 = "i";
            r7 = r7.contains(r8);
            if (r7 == 0) goto L_0x02a0;
        L_0x029e:
            r28 = 1;
        L_0x02a0:
            r0 = r69;
            r7 = org.telegram.messenger.ImageLoader.this;	 Catch:{ Throwable -> 0x02bc }
            r8 = java.lang.System.currentTimeMillis();	 Catch:{ Throwable -> 0x02bc }
            r7.lastCacheOutTime = r8;	 Catch:{ Throwable -> 0x02bc }
            r0 = r69;
            r8 = r0.sync;	 Catch:{ Throwable -> 0x02bc }
            monitor-enter(r8);	 Catch:{ Throwable -> 0x02bc }
            r0 = r69;
            r7 = r0.isCancelled;	 Catch:{ all -> 0x02b9 }
            if (r7 == 0) goto L_0x0340;
        L_0x02b6:
            monitor-exit(r8);	 Catch:{ all -> 0x02b9 }
            goto L_0x0017;
        L_0x02b9:
            r7 = move-exception;
            monitor-exit(r8);	 Catch:{ all -> 0x02b9 }
            throw r7;	 Catch:{ Throwable -> 0x02bc }
        L_0x02bc:
            r31 = move-exception;
            r6 = r41;
        L_0x02bf:
            org.telegram.messenger.FileLog.e(r31);
        L_0x02c2:
            java.lang.Thread.interrupted();
            if (r49 != 0) goto L_0x02c9;
        L_0x02c7:
            if (r52 == 0) goto L_0x0afc;
        L_0x02c9:
            if (r6 == 0) goto L_0x0af9;
        L_0x02cb:
            r7 = new org.telegram.messenger.ExtendedBitmapDrawable;
            r0 = r49;
            r1 = r52;
            r7.<init>(r6, r0, r1);
        L_0x02d4:
            r0 = r69;
            r0.onPostExecute(r7);
            goto L_0x0017;
        L_0x02db:
            r42 = 0;
            goto L_0x01d6;
        L_0x02df:
            r0 = r69;
            r7 = r0.cacheImage;
            r7 = r7.secureDocument;
            r0 = r7.fileHash;
            r63 = r0;
            goto L_0x020a;
        L_0x02eb:
            r64 = 0;
            r63 = 0;
            goto L_0x020a;
        L_0x02f1:
            r24 = org.telegram.messenger.ImageLoader.header;	 Catch:{ Exception -> 0x0b14, all -> 0x0b0f }
            goto L_0x022f;
        L_0x02f7:
            r31 = move-exception;
            org.telegram.messenger.FileLog.e(r31);
            goto L_0x0269;
        L_0x02fd:
            r31 = move-exception;
        L_0x02fe:
            org.telegram.messenger.FileLog.e(r31);	 Catch:{ all -> 0x030e }
            if (r58 == 0) goto L_0x0269;
        L_0x0303:
            r58.close();	 Catch:{ Exception -> 0x0308 }
            goto L_0x0269;
        L_0x0308:
            r31 = move-exception;
            org.telegram.messenger.FileLog.e(r31);
            goto L_0x0269;
        L_0x030e:
            r7 = move-exception;
        L_0x030f:
            if (r58 == 0) goto L_0x0314;
        L_0x0311:
            r58.close();	 Catch:{ Exception -> 0x0315 }
        L_0x0314:
            throw r7;
        L_0x0315:
            r31 = move-exception;
            org.telegram.messenger.FileLog.e(r31);
            goto L_0x0314;
        L_0x031a:
            r0 = r69;
            r7 = r0.cacheImage;
            r7 = r7.filter;
            r8 = "b1";
            r7 = r7.contains(r8);
            if (r7 == 0) goto L_0x032d;
        L_0x0329:
            r20 = 2;
            goto L_0x028f;
        L_0x032d:
            r0 = r69;
            r7 = r0.cacheImage;
            r7 = r7.filter;
            r8 = "b";
            r7 = r7.contains(r8);
            if (r7 == 0) goto L_0x028f;
        L_0x033c:
            r20 = 1;
            goto L_0x028f;
        L_0x0340:
            monitor-exit(r8);	 Catch:{ all -> 0x02b9 }
            r51 = new android.graphics.BitmapFactory$Options;	 Catch:{ Throwable -> 0x02bc }
            r51.<init>();	 Catch:{ Throwable -> 0x02bc }
            r7 = 1;
            r0 = r51;
            r0.inSampleSize = r7;	 Catch:{ Throwable -> 0x02bc }
            r7 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Throwable -> 0x02bc }
            r8 = 21;
            if (r7 >= r8) goto L_0x0356;
        L_0x0351:
            r7 = 1;
            r0 = r51;
            r0.inPurgeable = r7;	 Catch:{ Throwable -> 0x02bc }
        L_0x0356:
            if (r66 == 0) goto L_0x03cb;
        L_0x0358:
            r35 = new java.io.RandomAccessFile;	 Catch:{ Throwable -> 0x02bc }
            r7 = "r";
            r0 = r35;
            r1 = r26;
            r0.<init>(r1, r7);	 Catch:{ Throwable -> 0x02bc }
            r6 = r35.getChannel();	 Catch:{ Throwable -> 0x02bc }
            r7 = java.nio.channels.FileChannel.MapMode.READ_ONLY;	 Catch:{ Throwable -> 0x02bc }
            r8 = 0;
            r10 = r26.length();	 Catch:{ Throwable -> 0x02bc }
            r23 = r6.map(r7, r8, r10);	 Catch:{ Throwable -> 0x02bc }
            r22 = new android.graphics.BitmapFactory$Options;	 Catch:{ Throwable -> 0x02bc }
            r22.<init>();	 Catch:{ Throwable -> 0x02bc }
            r7 = 1;
            r0 = r22;
            r0.inJustDecodeBounds = r7;	 Catch:{ Throwable -> 0x02bc }
            r7 = 0;
            r8 = r23.limit();	 Catch:{ Throwable -> 0x02bc }
            r9 = 1;
            r0 = r23;
            r1 = r22;
            org.telegram.messenger.Utilities.loadWebpImage(r7, r0, r8, r1, r9);	 Catch:{ Throwable -> 0x02bc }
            r0 = r22;
            r7 = r0.outWidth;	 Catch:{ Throwable -> 0x02bc }
            r0 = r22;
            r8 = r0.outHeight;	 Catch:{ Throwable -> 0x02bc }
            r9 = android.graphics.Bitmap.Config.ARGB_8888;	 Catch:{ Throwable -> 0x02bc }
            r6 = org.telegram.messenger.Bitmaps.createBitmap(r7, r8, r9);	 Catch:{ Throwable -> 0x02bc }
            r8 = r23.limit();	 Catch:{ Throwable -> 0x03c6 }
            r9 = 0;
            r0 = r51;
            r7 = r0.inPurgeable;	 Catch:{ Throwable -> 0x03c6 }
            if (r7 != 0) goto L_0x03c9;
        L_0x03a4:
            r7 = 1;
        L_0x03a5:
            r0 = r23;
            org.telegram.messenger.Utilities.loadWebpImage(r6, r0, r8, r9, r7);	 Catch:{ Throwable -> 0x03c6 }
            r35.close();	 Catch:{ Throwable -> 0x03c6 }
        L_0x03ad:
            if (r6 != 0) goto L_0x0498;
        L_0x03af:
            r8 = r26.length();	 Catch:{ Throwable -> 0x03c6 }
            r10 = 0;
            r7 = (r8 > r10 ? 1 : (r8 == r10 ? 0 : -1));
            if (r7 == 0) goto L_0x03c1;
        L_0x03b9:
            r0 = r69;
            r7 = r0.cacheImage;	 Catch:{ Throwable -> 0x03c6 }
            r7 = r7.filter;	 Catch:{ Throwable -> 0x03c6 }
            if (r7 != 0) goto L_0x02c2;
        L_0x03c1:
            r26.delete();	 Catch:{ Throwable -> 0x03c6 }
            goto L_0x02c2;
        L_0x03c6:
            r31 = move-exception;
            goto L_0x02bf;
        L_0x03c9:
            r7 = 0;
            goto L_0x03a5;
        L_0x03cb:
            r0 = r51;
            r7 = r0.inPurgeable;	 Catch:{ Throwable -> 0x02bc }
            if (r7 != 0) goto L_0x03d3;
        L_0x03d1:
            if (r64 == 0) goto L_0x046f;
        L_0x03d3:
            r34 = new java.io.RandomAccessFile;	 Catch:{ Throwable -> 0x02bc }
            r7 = "r";
            r0 = r34;
            r1 = r26;
            r0.<init>(r1, r7);	 Catch:{ Throwable -> 0x02bc }
            r8 = r34.length();	 Catch:{ Throwable -> 0x02bc }
            r0 = (int) r8;	 Catch:{ Throwable -> 0x02bc }
            r44 = r0;
            r50 = 0;
            r7 = org.telegram.messenger.ImageLoader.bytesThumbLocal;	 Catch:{ Throwable -> 0x02bc }
            r25 = r7.get();	 Catch:{ Throwable -> 0x02bc }
            r25 = (byte[]) r25;	 Catch:{ Throwable -> 0x02bc }
            if (r25 == 0) goto L_0x045b;
        L_0x03f4:
            r0 = r25;
            r7 = r0.length;	 Catch:{ Throwable -> 0x02bc }
            r0 = r44;
            if (r7 < r0) goto L_0x045b;
        L_0x03fb:
            r29 = r25;
        L_0x03fd:
            if (r29 != 0) goto L_0x0410;
        L_0x03ff:
            r0 = r44;
            r0 = new byte[r0];	 Catch:{ Throwable -> 0x02bc }
            r29 = r0;
            r25 = r29;
            r7 = org.telegram.messenger.ImageLoader.bytesThumbLocal;	 Catch:{ Throwable -> 0x02bc }
            r0 = r25;
            r7.set(r0);	 Catch:{ Throwable -> 0x02bc }
        L_0x0410:
            r7 = 0;
            r0 = r34;
            r1 = r29;
            r2 = r44;
            r0.readFully(r1, r7, r2);	 Catch:{ Throwable -> 0x02bc }
            r34.close();	 Catch:{ Throwable -> 0x02bc }
            r32 = 0;
            if (r64 == 0) goto L_0x045e;
        L_0x0421:
            r7 = 0;
            r0 = r29;
            r1 = r44;
            r2 = r64;
            org.telegram.messenger.secretmedia.EncryptedFileInputStream.decryptBytesWithKeyFile(r0, r7, r1, r2);	 Catch:{ Throwable -> 0x02bc }
            r7 = 0;
            r0 = r29;
            r1 = r44;
            r39 = org.telegram.messenger.Utilities.computeSHA256(r0, r7, r1);	 Catch:{ Throwable -> 0x02bc }
            if (r63 == 0) goto L_0x0440;
        L_0x0436:
            r0 = r39;
            r1 = r63;
            r7 = java.util.Arrays.equals(r0, r1);	 Catch:{ Throwable -> 0x02bc }
            if (r7 != 0) goto L_0x0442;
        L_0x0440:
            r32 = 1;
        L_0x0442:
            r7 = 0;
            r7 = r29[r7];	 Catch:{ Throwable -> 0x02bc }
            r0 = r7 & 255;
            r50 = r0;
            r44 = r44 - r50;
        L_0x044b:
            if (r32 != 0) goto L_0x0b21;
        L_0x044d:
            r0 = r29;
            r1 = r50;
            r2 = r44;
            r3 = r51;
            r6 = android.graphics.BitmapFactory.decodeByteArray(r0, r1, r2, r3);	 Catch:{ Throwable -> 0x02bc }
            goto L_0x03ad;
        L_0x045b:
            r29 = 0;
            goto L_0x03fd;
        L_0x045e:
            if (r42 == 0) goto L_0x044b;
        L_0x0460:
            r7 = 0;
            r0 = r69;
            r8 = r0.cacheImage;	 Catch:{ Throwable -> 0x02bc }
            r8 = r8.encryptionKeyPath;	 Catch:{ Throwable -> 0x02bc }
            r0 = r29;
            r1 = r44;
            org.telegram.messenger.secretmedia.EncryptedFileInputStream.decryptBytesWithKeyFile(r0, r7, r1, r8);	 Catch:{ Throwable -> 0x02bc }
            goto L_0x044b;
        L_0x046f:
            if (r42 == 0) goto L_0x048e;
        L_0x0471:
            r43 = new org.telegram.messenger.secretmedia.EncryptedFileInputStream;	 Catch:{ Throwable -> 0x02bc }
            r0 = r69;
            r7 = r0.cacheImage;	 Catch:{ Throwable -> 0x02bc }
            r7 = r7.encryptionKeyPath;	 Catch:{ Throwable -> 0x02bc }
            r0 = r43;
            r1 = r26;
            r0.<init>(r1, r7);	 Catch:{ Throwable -> 0x02bc }
        L_0x0480:
            r7 = 0;
            r0 = r43;
            r1 = r51;
            r6 = android.graphics.BitmapFactory.decodeStream(r0, r7, r1);	 Catch:{ Throwable -> 0x02bc }
            r43.close();	 Catch:{ Throwable -> 0x03c6 }
            goto L_0x03ad;
        L_0x048e:
            r43 = new java.io.FileInputStream;	 Catch:{ Throwable -> 0x02bc }
            r0 = r43;
            r1 = r26;
            r0.<init>(r1);	 Catch:{ Throwable -> 0x02bc }
            goto L_0x0480;
        L_0x0498:
            if (r28 == 0) goto L_0x04b5;
        L_0x049a:
            r0 = r51;
            r7 = r0.inPurgeable;	 Catch:{ Throwable -> 0x03c6 }
            if (r7 == 0) goto L_0x04db;
        L_0x04a0:
            r7 = 0;
        L_0x04a1:
            r8 = r6.getWidth();	 Catch:{ Throwable -> 0x03c6 }
            r9 = r6.getHeight();	 Catch:{ Throwable -> 0x03c6 }
            r10 = r6.getRowBytes();	 Catch:{ Throwable -> 0x03c6 }
            r7 = org.telegram.messenger.Utilities.needInvert(r6, r7, r8, r9, r10);	 Catch:{ Throwable -> 0x03c6 }
            if (r7 == 0) goto L_0x04dd;
        L_0x04b3:
            r49 = 1;
        L_0x04b5:
            r7 = 1;
            r0 = r20;
            if (r0 != r7) goto L_0x04e2;
        L_0x04ba:
            r7 = r6.getConfig();	 Catch:{ Throwable -> 0x03c6 }
            r8 = android.graphics.Bitmap.Config.ARGB_8888;	 Catch:{ Throwable -> 0x03c6 }
            if (r7 != r8) goto L_0x02c2;
        L_0x04c2:
            r7 = 3;
            r0 = r51;
            r8 = r0.inPurgeable;	 Catch:{ Throwable -> 0x03c6 }
            if (r8 == 0) goto L_0x04e0;
        L_0x04c9:
            r8 = 0;
        L_0x04ca:
            r9 = r6.getWidth();	 Catch:{ Throwable -> 0x03c6 }
            r10 = r6.getHeight();	 Catch:{ Throwable -> 0x03c6 }
            r11 = r6.getRowBytes();	 Catch:{ Throwable -> 0x03c6 }
            org.telegram.messenger.Utilities.blurBitmap(r6, r7, r8, r9, r10, r11);	 Catch:{ Throwable -> 0x03c6 }
            goto L_0x02c2;
        L_0x04db:
            r7 = 1;
            goto L_0x04a1;
        L_0x04dd:
            r49 = 0;
            goto L_0x04b5;
        L_0x04e0:
            r8 = 1;
            goto L_0x04ca;
        L_0x04e2:
            r7 = 2;
            r0 = r20;
            if (r0 != r7) goto L_0x050a;
        L_0x04e7:
            r7 = r6.getConfig();	 Catch:{ Throwable -> 0x03c6 }
            r8 = android.graphics.Bitmap.Config.ARGB_8888;	 Catch:{ Throwable -> 0x03c6 }
            if (r7 != r8) goto L_0x02c2;
        L_0x04ef:
            r7 = 1;
            r0 = r51;
            r8 = r0.inPurgeable;	 Catch:{ Throwable -> 0x03c6 }
            if (r8 == 0) goto L_0x0508;
        L_0x04f6:
            r8 = 0;
        L_0x04f7:
            r9 = r6.getWidth();	 Catch:{ Throwable -> 0x03c6 }
            r10 = r6.getHeight();	 Catch:{ Throwable -> 0x03c6 }
            r11 = r6.getRowBytes();	 Catch:{ Throwable -> 0x03c6 }
            org.telegram.messenger.Utilities.blurBitmap(r6, r7, r8, r9, r10, r11);	 Catch:{ Throwable -> 0x03c6 }
            goto L_0x02c2;
        L_0x0508:
            r8 = 1;
            goto L_0x04f7;
        L_0x050a:
            r7 = 3;
            r0 = r20;
            if (r0 != r7) goto L_0x0564;
        L_0x050f:
            r7 = r6.getConfig();	 Catch:{ Throwable -> 0x03c6 }
            r8 = android.graphics.Bitmap.Config.ARGB_8888;	 Catch:{ Throwable -> 0x03c6 }
            if (r7 != r8) goto L_0x02c2;
        L_0x0517:
            r7 = 7;
            r0 = r51;
            r8 = r0.inPurgeable;	 Catch:{ Throwable -> 0x03c6 }
            if (r8 == 0) goto L_0x055e;
        L_0x051e:
            r8 = 0;
        L_0x051f:
            r9 = r6.getWidth();	 Catch:{ Throwable -> 0x03c6 }
            r10 = r6.getHeight();	 Catch:{ Throwable -> 0x03c6 }
            r11 = r6.getRowBytes();	 Catch:{ Throwable -> 0x03c6 }
            org.telegram.messenger.Utilities.blurBitmap(r6, r7, r8, r9, r10, r11);	 Catch:{ Throwable -> 0x03c6 }
            r7 = 7;
            r0 = r51;
            r8 = r0.inPurgeable;	 Catch:{ Throwable -> 0x03c6 }
            if (r8 == 0) goto L_0x0560;
        L_0x0535:
            r8 = 0;
        L_0x0536:
            r9 = r6.getWidth();	 Catch:{ Throwable -> 0x03c6 }
            r10 = r6.getHeight();	 Catch:{ Throwable -> 0x03c6 }
            r11 = r6.getRowBytes();	 Catch:{ Throwable -> 0x03c6 }
            org.telegram.messenger.Utilities.blurBitmap(r6, r7, r8, r9, r10, r11);	 Catch:{ Throwable -> 0x03c6 }
            r7 = 7;
            r0 = r51;
            r8 = r0.inPurgeable;	 Catch:{ Throwable -> 0x03c6 }
            if (r8 == 0) goto L_0x0562;
        L_0x054c:
            r8 = 0;
        L_0x054d:
            r9 = r6.getWidth();	 Catch:{ Throwable -> 0x03c6 }
            r10 = r6.getHeight();	 Catch:{ Throwable -> 0x03c6 }
            r11 = r6.getRowBytes();	 Catch:{ Throwable -> 0x03c6 }
            org.telegram.messenger.Utilities.blurBitmap(r6, r7, r8, r9, r10, r11);	 Catch:{ Throwable -> 0x03c6 }
            goto L_0x02c2;
        L_0x055e:
            r8 = 1;
            goto L_0x051f;
        L_0x0560:
            r8 = 1;
            goto L_0x0536;
        L_0x0562:
            r8 = 1;
            goto L_0x054d;
        L_0x0564:
            if (r20 != 0) goto L_0x02c2;
        L_0x0566:
            r0 = r51;
            r7 = r0.inPurgeable;	 Catch:{ Throwable -> 0x03c6 }
            if (r7 == 0) goto L_0x02c2;
        L_0x056c:
            org.telegram.messenger.Utilities.pinBitmap(r6);	 Catch:{ Throwable -> 0x03c6 }
            goto L_0x02c2;
        L_0x0571:
            r48 = 0;
            r0 = r69;
            r7 = r0.cacheImage;	 Catch:{ Throwable -> 0x060f }
            r7 = r7.location;	 Catch:{ Throwable -> 0x060f }
            r7 = r7 instanceof java.lang.String;	 Catch:{ Throwable -> 0x060f }
            if (r7 == 0) goto L_0x05bd;
        L_0x057d:
            r0 = r69;
            r7 = r0.cacheImage;	 Catch:{ Throwable -> 0x060f }
            r0 = r7.location;	 Catch:{ Throwable -> 0x060f }
            r45 = r0;
            r45 = (java.lang.String) r45;	 Catch:{ Throwable -> 0x060f }
            r7 = "thumb://";
            r0 = r45;
            r7 = r0.startsWith(r7);	 Catch:{ Throwable -> 0x060f }
            if (r7 == 0) goto L_0x0614;
        L_0x0592:
            r7 = ":";
            r8 = 8;
            r0 = r45;
            r40 = r0.indexOf(r7, r8);	 Catch:{ Throwable -> 0x060f }
            if (r40 < 0) goto L_0x05bb;
        L_0x059f:
            r7 = 8;
            r0 = r45;
            r1 = r40;
            r7 = r0.substring(r7, r1);	 Catch:{ Throwable -> 0x060f }
            r8 = java.lang.Long.parseLong(r7);	 Catch:{ Throwable -> 0x060f }
            r46 = java.lang.Long.valueOf(r8);	 Catch:{ Throwable -> 0x060f }
            r47 = 0;
            r7 = r40 + 1;
            r0 = r45;
            r48 = r0.substring(r7);	 Catch:{ Throwable -> 0x060f }
        L_0x05bb:
            r27 = 0;
        L_0x05bd:
            r30 = 20;
            if (r46 == 0) goto L_0x05c3;
        L_0x05c1:
            r30 = 0;
        L_0x05c3:
            if (r30 == 0) goto L_0x05f3;
        L_0x05c5:
            r0 = r69;
            r7 = org.telegram.messenger.ImageLoader.this;	 Catch:{ Throwable -> 0x060f }
            r8 = r7.lastCacheOutTime;	 Catch:{ Throwable -> 0x060f }
            r10 = 0;
            r7 = (r8 > r10 ? 1 : (r8 == r10 ? 0 : -1));
            if (r7 == 0) goto L_0x05f3;
        L_0x05d3:
            r0 = r69;
            r7 = org.telegram.messenger.ImageLoader.this;	 Catch:{ Throwable -> 0x060f }
            r8 = r7.lastCacheOutTime;	 Catch:{ Throwable -> 0x060f }
            r10 = java.lang.System.currentTimeMillis();	 Catch:{ Throwable -> 0x060f }
            r0 = r30;
            r12 = (long) r0;	 Catch:{ Throwable -> 0x060f }
            r10 = r10 - r12;
            r7 = (r8 > r10 ? 1 : (r8 == r10 ? 0 : -1));
            if (r7 <= 0) goto L_0x05f3;
        L_0x05e7:
            r7 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Throwable -> 0x060f }
            r8 = 21;
            if (r7 >= r8) goto L_0x05f3;
        L_0x05ed:
            r0 = r30;
            r8 = (long) r0;	 Catch:{ Throwable -> 0x060f }
            java.lang.Thread.sleep(r8);	 Catch:{ Throwable -> 0x060f }
        L_0x05f3:
            r0 = r69;
            r7 = org.telegram.messenger.ImageLoader.this;	 Catch:{ Throwable -> 0x060f }
            r8 = java.lang.System.currentTimeMillis();	 Catch:{ Throwable -> 0x060f }
            r7.lastCacheOutTime = r8;	 Catch:{ Throwable -> 0x060f }
            r0 = r69;
            r8 = r0.sync;	 Catch:{ Throwable -> 0x060f }
            monitor-enter(r8);	 Catch:{ Throwable -> 0x060f }
            r0 = r69;
            r7 = r0.isCancelled;	 Catch:{ all -> 0x060c }
            if (r7 == 0) goto L_0x0653;
        L_0x0609:
            monitor-exit(r8);	 Catch:{ all -> 0x060c }
            goto L_0x0017;
        L_0x060c:
            r7 = move-exception;
            monitor-exit(r8);	 Catch:{ all -> 0x060c }
            throw r7;	 Catch:{ Throwable -> 0x060f }
        L_0x060f:
            r7 = move-exception;
            r6 = r41;
            goto L_0x02c2;
        L_0x0614:
            r7 = "vthumb://";
            r0 = r45;
            r7 = r0.startsWith(r7);	 Catch:{ Throwable -> 0x060f }
            if (r7 == 0) goto L_0x0644;
        L_0x061f:
            r7 = ":";
            r8 = 9;
            r0 = r45;
            r40 = r0.indexOf(r7, r8);	 Catch:{ Throwable -> 0x060f }
            if (r40 < 0) goto L_0x0640;
        L_0x062c:
            r7 = 9;
            r0 = r45;
            r1 = r40;
            r7 = r0.substring(r7, r1);	 Catch:{ Throwable -> 0x060f }
            r8 = java.lang.Long.parseLong(r7);	 Catch:{ Throwable -> 0x060f }
            r46 = java.lang.Long.valueOf(r8);	 Catch:{ Throwable -> 0x060f }
            r47 = 1;
        L_0x0640:
            r27 = 0;
            goto L_0x05bd;
        L_0x0644:
            r7 = "http";
            r0 = r45;
            r7 = r0.startsWith(r7);	 Catch:{ Throwable -> 0x060f }
            if (r7 != 0) goto L_0x05bd;
        L_0x064f:
            r27 = 0;
            goto L_0x05bd;
        L_0x0653:
            monitor-exit(r8);	 Catch:{ all -> 0x060c }
            r51 = new android.graphics.BitmapFactory$Options;	 Catch:{ Throwable -> 0x060f }
            r51.<init>();	 Catch:{ Throwable -> 0x060f }
            r7 = 1;
            r0 = r51;
            r0.inSampleSize = r7;	 Catch:{ Throwable -> 0x060f }
            r68 = 0;
            r38 = 0;
            r19 = 0;
            r28 = 0;
            r0 = r69;
            r7 = org.telegram.messenger.ImageLoader.this;	 Catch:{ Throwable -> 0x060f }
            r36 = r7.canForce8888;	 Catch:{ Throwable -> 0x060f }
            r0 = r69;
            r7 = r0.cacheImage;	 Catch:{ Throwable -> 0x060f }
            r7 = r7.filter;	 Catch:{ Throwable -> 0x060f }
            if (r7 == 0) goto L_0x07fa;
        L_0x0676:
            r0 = r69;
            r7 = r0.cacheImage;	 Catch:{ Throwable -> 0x060f }
            r7 = r7.filter;	 Catch:{ Throwable -> 0x060f }
            r8 = "_";
            r14 = r7.split(r8);	 Catch:{ Throwable -> 0x060f }
            r7 = r14.length;	 Catch:{ Throwable -> 0x060f }
            r8 = 2;
            if (r7 < r8) goto L_0x069d;
        L_0x0687:
            r7 = 0;
            r7 = r14[r7];	 Catch:{ Throwable -> 0x060f }
            r7 = java.lang.Float.parseFloat(r7);	 Catch:{ Throwable -> 0x060f }
            r8 = org.telegram.messenger.AndroidUtilities.density;	 Catch:{ Throwable -> 0x060f }
            r68 = r7 * r8;
            r7 = 1;
            r7 = r14[r7];	 Catch:{ Throwable -> 0x060f }
            r7 = java.lang.Float.parseFloat(r7);	 Catch:{ Throwable -> 0x060f }
            r8 = org.telegram.messenger.AndroidUtilities.density;	 Catch:{ Throwable -> 0x060f }
            r38 = r7 * r8;
        L_0x069d:
            r0 = r69;
            r7 = r0.cacheImage;	 Catch:{ Throwable -> 0x060f }
            r7 = r7.filter;	 Catch:{ Throwable -> 0x060f }
            r8 = "b";
            r7 = r7.contains(r8);	 Catch:{ Throwable -> 0x060f }
            if (r7 == 0) goto L_0x06ae;
        L_0x06ac:
            r19 = 1;
        L_0x06ae:
            r0 = r69;
            r7 = r0.cacheImage;	 Catch:{ Throwable -> 0x060f }
            r7 = r7.filter;	 Catch:{ Throwable -> 0x060f }
            r8 = "i";
            r7 = r7.contains(r8);	 Catch:{ Throwable -> 0x060f }
            if (r7 == 0) goto L_0x06bf;
        L_0x06bd:
            r28 = 1;
        L_0x06bf:
            r0 = r69;
            r7 = r0.cacheImage;	 Catch:{ Throwable -> 0x060f }
            r7 = r7.filter;	 Catch:{ Throwable -> 0x060f }
            r8 = "f";
            r7 = r7.contains(r8);	 Catch:{ Throwable -> 0x060f }
            if (r7 == 0) goto L_0x06d0;
        L_0x06ce:
            r36 = 1;
        L_0x06d0:
            r7 = 0;
            r7 = (r68 > r7 ? 1 : (r68 == r7 ? 0 : -1));
            if (r7 == 0) goto L_0x0b19;
        L_0x06d5:
            r7 = 0;
            r7 = (r38 > r7 ? 1 : (r38 == r7 ? 0 : -1));
            if (r7 == 0) goto L_0x0b19;
        L_0x06da:
            r7 = 1;
            r0 = r51;
            r0.inJustDecodeBounds = r7;	 Catch:{ Throwable -> 0x060f }
            if (r46 == 0) goto L_0x0748;
        L_0x06e1:
            if (r48 != 0) goto L_0x0748;
        L_0x06e3:
            if (r47 == 0) goto L_0x0735;
        L_0x06e5:
            r7 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Throwable -> 0x060f }
            r7 = r7.getContentResolver();	 Catch:{ Throwable -> 0x060f }
            r8 = r46.longValue();	 Catch:{ Throwable -> 0x060f }
            r10 = 1;
            r0 = r51;
            android.provider.MediaStore.Video.Thumbnails.getThumbnail(r7, r8, r10, r0);	 Catch:{ Throwable -> 0x060f }
            r6 = r41;
        L_0x06f7:
            r0 = r51;
            r7 = r0.outWidth;	 Catch:{ Throwable -> 0x0732 }
            r0 = (float) r7;	 Catch:{ Throwable -> 0x0732 }
            r56 = r0;
            r0 = r51;
            r7 = r0.outHeight;	 Catch:{ Throwable -> 0x0732 }
            r0 = (float) r7;	 Catch:{ Throwable -> 0x0732 }
            r53 = r0;
            r7 = r56 / r68;
            r8 = r53 / r38;
            r61 = java.lang.Math.max(r7, r8);	 Catch:{ Throwable -> 0x0732 }
            r7 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
            r7 = (r61 > r7 ? 1 : (r61 == r7 ? 0 : -1));
            if (r7 >= 0) goto L_0x0715;
        L_0x0713:
            r61 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        L_0x0715:
            r7 = 0;
            r0 = r51;
            r0.inJustDecodeBounds = r7;	 Catch:{ Throwable -> 0x0732 }
            r0 = r61;
            r7 = (int) r0;	 Catch:{ Throwable -> 0x0732 }
            r0 = r51;
            r0.inSampleSize = r7;	 Catch:{ Throwable -> 0x0732 }
        L_0x0721:
            r0 = r69;
            r8 = r0.sync;	 Catch:{ Throwable -> 0x0732 }
            monitor-enter(r8);	 Catch:{ Throwable -> 0x0732 }
            r0 = r69;
            r7 = r0.isCancelled;	 Catch:{ all -> 0x072f }
            if (r7 == 0) goto L_0x085c;
        L_0x072c:
            monitor-exit(r8);	 Catch:{ all -> 0x072f }
            goto L_0x0017;
        L_0x072f:
            r7 = move-exception;
            monitor-exit(r8);	 Catch:{ all -> 0x072f }
            throw r7;	 Catch:{ Throwable -> 0x0732 }
        L_0x0732:
            r7 = move-exception;
            goto L_0x02c2;
        L_0x0735:
            r7 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Throwable -> 0x060f }
            r7 = r7.getContentResolver();	 Catch:{ Throwable -> 0x060f }
            r8 = r46.longValue();	 Catch:{ Throwable -> 0x060f }
            r10 = 1;
            r0 = r51;
            android.provider.MediaStore.Images.Thumbnails.getThumbnail(r7, r8, r10, r0);	 Catch:{ Throwable -> 0x060f }
            r6 = r41;
            goto L_0x06f7;
        L_0x0748:
            if (r64 == 0) goto L_0x07d1;
        L_0x074a:
            r34 = new java.io.RandomAccessFile;	 Catch:{ Throwable -> 0x060f }
            r7 = "r";
            r0 = r34;
            r1 = r26;
            r0.<init>(r1, r7);	 Catch:{ Throwable -> 0x060f }
            r8 = r34.length();	 Catch:{ Throwable -> 0x060f }
            r0 = (int) r8;	 Catch:{ Throwable -> 0x060f }
            r44 = r0;
            r7 = org.telegram.messenger.ImageLoader.bytesLocal;	 Catch:{ Throwable -> 0x060f }
            r24 = r7.get();	 Catch:{ Throwable -> 0x060f }
            r24 = (byte[]) r24;	 Catch:{ Throwable -> 0x060f }
            if (r24 == 0) goto L_0x07ce;
        L_0x0769:
            r0 = r24;
            r7 = r0.length;	 Catch:{ Throwable -> 0x060f }
            r0 = r44;
            if (r7 < r0) goto L_0x07ce;
        L_0x0770:
            r29 = r24;
        L_0x0772:
            if (r29 != 0) goto L_0x0785;
        L_0x0774:
            r0 = r44;
            r0 = new byte[r0];	 Catch:{ Throwable -> 0x060f }
            r29 = r0;
            r24 = r29;
            r7 = org.telegram.messenger.ImageLoader.bytesLocal;	 Catch:{ Throwable -> 0x060f }
            r0 = r24;
            r7.set(r0);	 Catch:{ Throwable -> 0x060f }
        L_0x0785:
            r7 = 0;
            r0 = r34;
            r1 = r29;
            r2 = r44;
            r0.readFully(r1, r7, r2);	 Catch:{ Throwable -> 0x060f }
            r34.close();	 Catch:{ Throwable -> 0x060f }
            r7 = 0;
            r0 = r29;
            r1 = r44;
            r2 = r64;
            org.telegram.messenger.secretmedia.EncryptedFileInputStream.decryptBytesWithKeyFile(r0, r7, r1, r2);	 Catch:{ Throwable -> 0x060f }
            r7 = 0;
            r0 = r29;
            r1 = r44;
            r39 = org.telegram.messenger.Utilities.computeSHA256(r0, r7, r1);	 Catch:{ Throwable -> 0x060f }
            r32 = 0;
            if (r63 == 0) goto L_0x07b3;
        L_0x07a9:
            r0 = r39;
            r1 = r63;
            r7 = java.util.Arrays.equals(r0, r1);	 Catch:{ Throwable -> 0x060f }
            if (r7 != 0) goto L_0x07b5;
        L_0x07b3:
            r32 = 1;
        L_0x07b5:
            r7 = 0;
            r7 = r29[r7];	 Catch:{ Throwable -> 0x060f }
            r0 = r7 & 255;
            r50 = r0;
            r44 = r44 - r50;
            if (r32 != 0) goto L_0x0b1d;
        L_0x07c0:
            r0 = r29;
            r1 = r50;
            r2 = r44;
            r3 = r51;
            r6 = android.graphics.BitmapFactory.decodeByteArray(r0, r1, r2, r3);	 Catch:{ Throwable -> 0x060f }
            goto L_0x06f7;
        L_0x07ce:
            r29 = 0;
            goto L_0x0772;
        L_0x07d1:
            if (r42 == 0) goto L_0x07f0;
        L_0x07d3:
            r43 = new org.telegram.messenger.secretmedia.EncryptedFileInputStream;	 Catch:{ Throwable -> 0x060f }
            r0 = r69;
            r7 = r0.cacheImage;	 Catch:{ Throwable -> 0x060f }
            r7 = r7.encryptionKeyPath;	 Catch:{ Throwable -> 0x060f }
            r0 = r43;
            r1 = r26;
            r0.<init>(r1, r7);	 Catch:{ Throwable -> 0x060f }
        L_0x07e2:
            r7 = 0;
            r0 = r43;
            r1 = r51;
            r6 = android.graphics.BitmapFactory.decodeStream(r0, r7, r1);	 Catch:{ Throwable -> 0x060f }
            r43.close();	 Catch:{ Throwable -> 0x0732 }
            goto L_0x06f7;
        L_0x07f0:
            r43 = new java.io.FileInputStream;	 Catch:{ Throwable -> 0x060f }
            r0 = r43;
            r1 = r26;
            r0.<init>(r1);	 Catch:{ Throwable -> 0x060f }
            goto L_0x07e2;
        L_0x07fa:
            if (r48 == 0) goto L_0x0b19;
        L_0x07fc:
            r7 = 1;
            r0 = r51;
            r0.inJustDecodeBounds = r7;	 Catch:{ Throwable -> 0x060f }
            if (r36 == 0) goto L_0x0859;
        L_0x0803:
            r7 = android.graphics.Bitmap.Config.ARGB_8888;	 Catch:{ Throwable -> 0x060f }
        L_0x0805:
            r0 = r51;
            r0.inPreferredConfig = r7;	 Catch:{ Throwable -> 0x060f }
            r43 = new java.io.FileInputStream;	 Catch:{ Throwable -> 0x060f }
            r0 = r43;
            r1 = r26;
            r0.<init>(r1);	 Catch:{ Throwable -> 0x060f }
            r7 = 0;
            r0 = r43;
            r1 = r51;
            r6 = android.graphics.BitmapFactory.decodeStream(r0, r7, r1);	 Catch:{ Throwable -> 0x060f }
            r43.close();	 Catch:{ Throwable -> 0x0732 }
            r0 = r51;
            r0 = r0.outWidth;	 Catch:{ Throwable -> 0x0732 }
            r57 = r0;
            r0 = r51;
            r0 = r0.outHeight;	 Catch:{ Throwable -> 0x0732 }
            r54 = r0;
            r7 = 0;
            r0 = r51;
            r0.inJustDecodeBounds = r7;	 Catch:{ Throwable -> 0x0732 }
            r0 = r57;
            r7 = r0 / 200;
            r0 = r54;
            r8 = r0 / 200;
            r7 = java.lang.Math.max(r7, r8);	 Catch:{ Throwable -> 0x0732 }
            r0 = (float) r7;	 Catch:{ Throwable -> 0x0732 }
            r61 = r0;
            r7 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
            r7 = (r61 > r7 ? 1 : (r61 == r7 ? 0 : -1));
            if (r7 >= 0) goto L_0x0846;
        L_0x0844:
            r61 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        L_0x0846:
            r60 = 1;
        L_0x0848:
            r60 = r60 * 2;
            r7 = r60 * 2;
            r7 = (float) r7;	 Catch:{ Throwable -> 0x0732 }
            r7 = (r7 > r61 ? 1 : (r7 == r61 ? 0 : -1));
            if (r7 < 0) goto L_0x0848;
        L_0x0851:
            r0 = r60;
            r1 = r51;
            r1.inSampleSize = r0;	 Catch:{ Throwable -> 0x0732 }
            goto L_0x0721;
        L_0x0859:
            r7 = android.graphics.Bitmap.Config.RGB_565;	 Catch:{ Throwable -> 0x060f }
            goto L_0x0805;
        L_0x085c:
            monitor-exit(r8);	 Catch:{ all -> 0x072f }
            if (r36 != 0) goto L_0x0873;
        L_0x085f:
            r0 = r69;
            r7 = r0.cacheImage;	 Catch:{ Throwable -> 0x0732 }
            r7 = r7.filter;	 Catch:{ Throwable -> 0x0732 }
            if (r7 == 0) goto L_0x0873;
        L_0x0867:
            if (r19 != 0) goto L_0x0873;
        L_0x0869:
            r0 = r69;
            r7 = r0.cacheImage;	 Catch:{ Throwable -> 0x0732 }
            r7 = r7.location;	 Catch:{ Throwable -> 0x0732 }
            r7 = r7 instanceof java.lang.String;	 Catch:{ Throwable -> 0x0732 }
            if (r7 == 0) goto L_0x0914;
        L_0x0873:
            r7 = android.graphics.Bitmap.Config.ARGB_8888;	 Catch:{ Throwable -> 0x0732 }
            r0 = r51;
            r0.inPreferredConfig = r7;	 Catch:{ Throwable -> 0x0732 }
        L_0x0879:
            r7 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Throwable -> 0x0732 }
            r8 = 21;
            if (r7 >= r8) goto L_0x0884;
        L_0x087f:
            r7 = 1;
            r0 = r51;
            r0.inPurgeable = r7;	 Catch:{ Throwable -> 0x0732 }
        L_0x0884:
            r7 = 0;
            r0 = r51;
            r0.inDither = r7;	 Catch:{ Throwable -> 0x0732 }
            if (r46 == 0) goto L_0x08a0;
        L_0x088b:
            if (r48 != 0) goto L_0x08a0;
        L_0x088d:
            if (r47 == 0) goto L_0x091c;
        L_0x088f:
            r7 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Throwable -> 0x0732 }
            r7 = r7.getContentResolver();	 Catch:{ Throwable -> 0x0732 }
            r8 = r46.longValue();	 Catch:{ Throwable -> 0x0732 }
            r10 = 1;
            r0 = r51;
            r6 = android.provider.MediaStore.Video.Thumbnails.getThumbnail(r7, r8, r10, r0);	 Catch:{ Throwable -> 0x0732 }
        L_0x08a0:
            if (r6 != 0) goto L_0x08f9;
        L_0x08a2:
            if (r66 == 0) goto L_0x0931;
        L_0x08a4:
            r35 = new java.io.RandomAccessFile;	 Catch:{ Throwable -> 0x0732 }
            r7 = "r";
            r0 = r35;
            r1 = r26;
            r0.<init>(r1, r7);	 Catch:{ Throwable -> 0x0732 }
            r8 = r35.getChannel();	 Catch:{ Throwable -> 0x0732 }
            r9 = java.nio.channels.FileChannel.MapMode.READ_ONLY;	 Catch:{ Throwable -> 0x0732 }
            r10 = 0;
            r12 = r26.length();	 Catch:{ Throwable -> 0x0732 }
            r23 = r8.map(r9, r10, r12);	 Catch:{ Throwable -> 0x0732 }
            r22 = new android.graphics.BitmapFactory$Options;	 Catch:{ Throwable -> 0x0732 }
            r22.<init>();	 Catch:{ Throwable -> 0x0732 }
            r7 = 1;
            r0 = r22;
            r0.inJustDecodeBounds = r7;	 Catch:{ Throwable -> 0x0732 }
            r7 = 0;
            r8 = r23.limit();	 Catch:{ Throwable -> 0x0732 }
            r9 = 1;
            r0 = r23;
            r1 = r22;
            org.telegram.messenger.Utilities.loadWebpImage(r7, r0, r8, r1, r9);	 Catch:{ Throwable -> 0x0732 }
            r0 = r22;
            r7 = r0.outWidth;	 Catch:{ Throwable -> 0x0732 }
            r0 = r22;
            r8 = r0.outHeight;	 Catch:{ Throwable -> 0x0732 }
            r9 = android.graphics.Bitmap.Config.ARGB_8888;	 Catch:{ Throwable -> 0x0732 }
            r6 = org.telegram.messenger.Bitmaps.createBitmap(r7, r8, r9);	 Catch:{ Throwable -> 0x0732 }
            r8 = r23.limit();	 Catch:{ Throwable -> 0x0732 }
            r9 = 0;
            r0 = r51;
            r7 = r0.inPurgeable;	 Catch:{ Throwable -> 0x0732 }
            if (r7 != 0) goto L_0x092f;
        L_0x08f0:
            r7 = 1;
        L_0x08f1:
            r0 = r23;
            org.telegram.messenger.Utilities.loadWebpImage(r6, r0, r8, r9, r7);	 Catch:{ Throwable -> 0x0732 }
            r35.close();	 Catch:{ Throwable -> 0x0732 }
        L_0x08f9:
            if (r6 != 0) goto L_0x0a30;
        L_0x08fb:
            if (r27 == 0) goto L_0x02c2;
        L_0x08fd:
            r8 = r26.length();	 Catch:{ Throwable -> 0x0732 }
            r10 = 0;
            r7 = (r8 > r10 ? 1 : (r8 == r10 ? 0 : -1));
            if (r7 == 0) goto L_0x090f;
        L_0x0907:
            r0 = r69;
            r7 = r0.cacheImage;	 Catch:{ Throwable -> 0x0732 }
            r7 = r7.filter;	 Catch:{ Throwable -> 0x0732 }
            if (r7 != 0) goto L_0x02c2;
        L_0x090f:
            r26.delete();	 Catch:{ Throwable -> 0x0732 }
            goto L_0x02c2;
        L_0x0914:
            r7 = android.graphics.Bitmap.Config.RGB_565;	 Catch:{ Throwable -> 0x0732 }
            r0 = r51;
            r0.inPreferredConfig = r7;	 Catch:{ Throwable -> 0x0732 }
            goto L_0x0879;
        L_0x091c:
            r7 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Throwable -> 0x0732 }
            r7 = r7.getContentResolver();	 Catch:{ Throwable -> 0x0732 }
            r8 = r46.longValue();	 Catch:{ Throwable -> 0x0732 }
            r10 = 1;
            r0 = r51;
            r6 = android.provider.MediaStore.Images.Thumbnails.getThumbnail(r7, r8, r10, r0);	 Catch:{ Throwable -> 0x0732 }
            goto L_0x08a0;
        L_0x092f:
            r7 = 0;
            goto L_0x08f1;
        L_0x0931:
            r0 = r51;
            r7 = r0.inPurgeable;	 Catch:{ Throwable -> 0x0732 }
            if (r7 != 0) goto L_0x0939;
        L_0x0937:
            if (r64 == 0) goto L_0x09d5;
        L_0x0939:
            r34 = new java.io.RandomAccessFile;	 Catch:{ Throwable -> 0x0732 }
            r7 = "r";
            r0 = r34;
            r1 = r26;
            r0.<init>(r1, r7);	 Catch:{ Throwable -> 0x0732 }
            r8 = r34.length();	 Catch:{ Throwable -> 0x0732 }
            r0 = (int) r8;	 Catch:{ Throwable -> 0x0732 }
            r44 = r0;
            r50 = 0;
            r7 = org.telegram.messenger.ImageLoader.bytesLocal;	 Catch:{ Throwable -> 0x0732 }
            r24 = r7.get();	 Catch:{ Throwable -> 0x0732 }
            r24 = (byte[]) r24;	 Catch:{ Throwable -> 0x0732 }
            if (r24 == 0) goto L_0x09c1;
        L_0x095a:
            r0 = r24;
            r7 = r0.length;	 Catch:{ Throwable -> 0x0732 }
            r0 = r44;
            if (r7 < r0) goto L_0x09c1;
        L_0x0961:
            r29 = r24;
        L_0x0963:
            if (r29 != 0) goto L_0x0976;
        L_0x0965:
            r0 = r44;
            r0 = new byte[r0];	 Catch:{ Throwable -> 0x0732 }
            r29 = r0;
            r24 = r29;
            r7 = org.telegram.messenger.ImageLoader.bytesLocal;	 Catch:{ Throwable -> 0x0732 }
            r0 = r24;
            r7.set(r0);	 Catch:{ Throwable -> 0x0732 }
        L_0x0976:
            r7 = 0;
            r0 = r34;
            r1 = r29;
            r2 = r44;
            r0.readFully(r1, r7, r2);	 Catch:{ Throwable -> 0x0732 }
            r34.close();	 Catch:{ Throwable -> 0x0732 }
            r32 = 0;
            if (r64 == 0) goto L_0x09c4;
        L_0x0987:
            r7 = 0;
            r0 = r29;
            r1 = r44;
            r2 = r64;
            org.telegram.messenger.secretmedia.EncryptedFileInputStream.decryptBytesWithKeyFile(r0, r7, r1, r2);	 Catch:{ Throwable -> 0x0732 }
            r7 = 0;
            r0 = r29;
            r1 = r44;
            r39 = org.telegram.messenger.Utilities.computeSHA256(r0, r7, r1);	 Catch:{ Throwable -> 0x0732 }
            if (r63 == 0) goto L_0x09a6;
        L_0x099c:
            r0 = r39;
            r1 = r63;
            r7 = java.util.Arrays.equals(r0, r1);	 Catch:{ Throwable -> 0x0732 }
            if (r7 != 0) goto L_0x09a8;
        L_0x09a6:
            r32 = 1;
        L_0x09a8:
            r7 = 0;
            r7 = r29[r7];	 Catch:{ Throwable -> 0x0732 }
            r0 = r7 & 255;
            r50 = r0;
            r44 = r44 - r50;
        L_0x09b1:
            if (r32 != 0) goto L_0x08f9;
        L_0x09b3:
            r0 = r29;
            r1 = r50;
            r2 = r44;
            r3 = r51;
            r6 = android.graphics.BitmapFactory.decodeByteArray(r0, r1, r2, r3);	 Catch:{ Throwable -> 0x0732 }
            goto L_0x08f9;
        L_0x09c1:
            r29 = 0;
            goto L_0x0963;
        L_0x09c4:
            if (r42 == 0) goto L_0x09b1;
        L_0x09c6:
            r7 = 0;
            r0 = r69;
            r8 = r0.cacheImage;	 Catch:{ Throwable -> 0x0732 }
            r8 = r8.encryptionKeyPath;	 Catch:{ Throwable -> 0x0732 }
            r0 = r29;
            r1 = r44;
            org.telegram.messenger.secretmedia.EncryptedFileInputStream.decryptBytesWithKeyFile(r0, r7, r1, r8);	 Catch:{ Throwable -> 0x0732 }
            goto L_0x09b1;
        L_0x09d5:
            if (r42 == 0) goto L_0x0a1d;
        L_0x09d7:
            r43 = new org.telegram.messenger.secretmedia.EncryptedFileInputStream;	 Catch:{ Throwable -> 0x0732 }
            r0 = r69;
            r7 = r0.cacheImage;	 Catch:{ Throwable -> 0x0732 }
            r7 = r7.encryptionKeyPath;	 Catch:{ Throwable -> 0x0732 }
            r0 = r43;
            r1 = r26;
            r0.<init>(r1, r7);	 Catch:{ Throwable -> 0x0732 }
        L_0x09e6:
            r0 = r69;
            r7 = r0.cacheImage;	 Catch:{ Throwable -> 0x0732 }
            r7 = r7.location;	 Catch:{ Throwable -> 0x0732 }
            r7 = r7 instanceof org.telegram.tgnet.TLRPC.TL_document;	 Catch:{ Throwable -> 0x0732 }
            if (r7 == 0) goto L_0x0a0f;
        L_0x09f0:
            r33 = new android.support.media.ExifInterface;	 Catch:{ Throwable -> 0x0b0c }
            r0 = r33;
            r1 = r43;
            r0.<init>(r1);	 Catch:{ Throwable -> 0x0b0c }
            r7 = "Orientation";
            r8 = 1;
            r0 = r33;
            r15 = r0.getAttributeInt(r7, r8);	 Catch:{ Throwable -> 0x0b0c }
            switch(r15) {
                case 3: goto L_0x0a2a;
                case 4: goto L_0x0a06;
                case 5: goto L_0x0a06;
                case 6: goto L_0x0a27;
                case 7: goto L_0x0a06;
                case 8: goto L_0x0a2d;
                default: goto L_0x0a06;
            };
        L_0x0a06:
            r7 = r43.getChannel();	 Catch:{ Throwable -> 0x0732 }
            r8 = 0;
            r7.position(r8);	 Catch:{ Throwable -> 0x0732 }
        L_0x0a0f:
            r7 = 0;
            r0 = r43;
            r1 = r51;
            r6 = android.graphics.BitmapFactory.decodeStream(r0, r7, r1);	 Catch:{ Throwable -> 0x0732 }
            r43.close();	 Catch:{ Throwable -> 0x0732 }
            goto L_0x08f9;
        L_0x0a1d:
            r43 = new java.io.FileInputStream;	 Catch:{ Throwable -> 0x0732 }
            r0 = r43;
            r1 = r26;
            r0.<init>(r1);	 Catch:{ Throwable -> 0x0732 }
            goto L_0x09e6;
        L_0x0a27:
            r52 = 90;
            goto L_0x0a06;
        L_0x0a2a:
            r52 = 180; // 0xb4 float:2.52E-43 double:8.9E-322;
            goto L_0x0a06;
        L_0x0a2d:
            r52 = 270; // 0x10e float:3.78E-43 double:1.334E-321;
            goto L_0x0a06;
        L_0x0a30:
            r21 = 0;
            r0 = r69;
            r7 = r0.cacheImage;	 Catch:{ Throwable -> 0x0732 }
            r7 = r7.filter;	 Catch:{ Throwable -> 0x0732 }
            if (r7 == 0) goto L_0x0ae5;
        L_0x0a3a:
            r7 = r6.getWidth();	 Catch:{ Throwable -> 0x0732 }
            r0 = (float) r7;	 Catch:{ Throwable -> 0x0732 }
            r18 = r0;
            r7 = r6.getHeight();	 Catch:{ Throwable -> 0x0732 }
            r0 = (float) r7;	 Catch:{ Throwable -> 0x0732 }
            r17 = r0;
            r0 = r51;
            r7 = r0.inPurgeable;	 Catch:{ Throwable -> 0x0732 }
            if (r7 != 0) goto L_0x0a75;
        L_0x0a4e:
            r7 = 0;
            r7 = (r68 > r7 ? 1 : (r68 == r7 ? 0 : -1));
            if (r7 == 0) goto L_0x0a75;
        L_0x0a53:
            r7 = (r18 > r68 ? 1 : (r18 == r68 ? 0 : -1));
            if (r7 == 0) goto L_0x0a75;
        L_0x0a57:
            r7 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
            r7 = r7 + r68;
            r7 = (r18 > r7 ? 1 : (r18 == r7 ? 0 : -1));
            if (r7 <= 0) goto L_0x0a75;
        L_0x0a5f:
            r61 = r18 / r68;
            r0 = r68;
            r7 = (int) r0;	 Catch:{ Throwable -> 0x0732 }
            r8 = r17 / r61;
            r8 = (int) r8;	 Catch:{ Throwable -> 0x0732 }
            r9 = 1;
            r62 = org.telegram.messenger.Bitmaps.createScaledBitmap(r6, r7, r8, r9);	 Catch:{ Throwable -> 0x0732 }
            r0 = r62;
            if (r6 == r0) goto L_0x0a75;
        L_0x0a70:
            r6.recycle();	 Catch:{ Throwable -> 0x0732 }
            r6 = r62;
        L_0x0a75:
            if (r6 == 0) goto L_0x0ae5;
        L_0x0a77:
            if (r28 == 0) goto L_0x0ab6;
        L_0x0a79:
            r16 = r6;
            r67 = r6.getWidth();	 Catch:{ Throwable -> 0x0732 }
            r37 = r6.getHeight();	 Catch:{ Throwable -> 0x0732 }
            r7 = r67 * r37;
            r8 = 22500; // 0x57e4 float:3.1529E-41 double:1.11165E-319;
            if (r7 <= r8) goto L_0x0a92;
        L_0x0a89:
            r7 = 100;
            r8 = 100;
            r9 = 0;
            r16 = org.telegram.messenger.Bitmaps.createScaledBitmap(r6, r7, r8, r9);	 Catch:{ Throwable -> 0x0732 }
        L_0x0a92:
            r0 = r51;
            r7 = r0.inPurgeable;	 Catch:{ Throwable -> 0x0732 }
            if (r7 == 0) goto L_0x0af2;
        L_0x0a98:
            r7 = 0;
        L_0x0a99:
            r8 = r16.getWidth();	 Catch:{ Throwable -> 0x0732 }
            r9 = r16.getHeight();	 Catch:{ Throwable -> 0x0732 }
            r10 = r16.getRowBytes();	 Catch:{ Throwable -> 0x0732 }
            r0 = r16;
            r7 = org.telegram.messenger.Utilities.needInvert(r0, r7, r8, r9, r10);	 Catch:{ Throwable -> 0x0732 }
            if (r7 == 0) goto L_0x0af4;
        L_0x0aad:
            r49 = 1;
        L_0x0aaf:
            r0 = r16;
            if (r0 == r6) goto L_0x0ab6;
        L_0x0ab3:
            r16.recycle();	 Catch:{ Throwable -> 0x0732 }
        L_0x0ab6:
            if (r19 == 0) goto L_0x0ae5;
        L_0x0ab8:
            r7 = NUM; // 0x42CLASSNAME float:100.0 double:5.53552857E-315;
            r7 = (r17 > r7 ? 1 : (r17 == r7 ? 0 : -1));
            if (r7 >= 0) goto L_0x0ae5;
        L_0x0abe:
            r7 = NUM; // 0x42CLASSNAME float:100.0 double:5.53552857E-315;
            r7 = (r18 > r7 ? 1 : (r18 == r7 ? 0 : -1));
            if (r7 >= 0) goto L_0x0ae5;
        L_0x0ac4:
            r7 = r6.getConfig();	 Catch:{ Throwable -> 0x0732 }
            r8 = android.graphics.Bitmap.Config.ARGB_8888;	 Catch:{ Throwable -> 0x0732 }
            if (r7 != r8) goto L_0x0ae3;
        L_0x0acc:
            r7 = 3;
            r0 = r51;
            r8 = r0.inPurgeable;	 Catch:{ Throwable -> 0x0732 }
            if (r8 == 0) goto L_0x0af7;
        L_0x0ad3:
            r8 = 0;
        L_0x0ad4:
            r9 = r6.getWidth();	 Catch:{ Throwable -> 0x0732 }
            r10 = r6.getHeight();	 Catch:{ Throwable -> 0x0732 }
            r11 = r6.getRowBytes();	 Catch:{ Throwable -> 0x0732 }
            org.telegram.messenger.Utilities.blurBitmap(r6, r7, r8, r9, r10, r11);	 Catch:{ Throwable -> 0x0732 }
        L_0x0ae3:
            r21 = 1;
        L_0x0ae5:
            if (r21 != 0) goto L_0x02c2;
        L_0x0ae7:
            r0 = r51;
            r7 = r0.inPurgeable;	 Catch:{ Throwable -> 0x0732 }
            if (r7 == 0) goto L_0x02c2;
        L_0x0aed:
            org.telegram.messenger.Utilities.pinBitmap(r6);	 Catch:{ Throwable -> 0x0732 }
            goto L_0x02c2;
        L_0x0af2:
            r7 = 1;
            goto L_0x0a99;
        L_0x0af4:
            r49 = 0;
            goto L_0x0aaf;
        L_0x0af7:
            r8 = 1;
            goto L_0x0ad4;
        L_0x0af9:
            r7 = 0;
            goto L_0x02d4;
        L_0x0afc:
            if (r6 == 0) goto L_0x0b0a;
        L_0x0afe:
            r7 = new android.graphics.drawable.BitmapDrawable;
            r7.<init>(r6);
        L_0x0b03:
            r0 = r69;
            r0.onPostExecute(r7);
            goto L_0x0017;
        L_0x0b0a:
            r7 = 0;
            goto L_0x0b03;
        L_0x0b0c:
            r7 = move-exception;
            goto L_0x0a06;
        L_0x0b0f:
            r7 = move-exception;
            r58 = r59;
            goto L_0x030f;
        L_0x0b14:
            r31 = move-exception;
            r58 = r59;
            goto L_0x02fe;
        L_0x0b19:
            r6 = r41;
            goto L_0x0721;
        L_0x0b1d:
            r6 = r41;
            goto L_0x06f7;
        L_0x0b21:
            r6 = r41;
            goto L_0x03ad;
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
                    String location = this.cacheImage.location;
                    if (location.startsWith("https://static-maps") || location.startsWith("https://maps.googleapis")) {
                        int provider = MessagesController.getInstance(this.cacheImage.currentAccount).mapProvider;
                        if (provider == 3 || provider == 4) {
                            WebFile webFile = (WebFile) ImageLoader.this.testWebFile.get(location);
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
                        location = this.overrideUrl;
                    }
                    this.httpConnection = (HttpURLConnection) new URL(location).openConnection();
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
        private boolean big;
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
                int size = this.info.big ? Math.max(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y) : Math.min(180, Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y) / 4);
                Bitmap originalBitmap = null;
                if (this.mediaType == 0) {
                    originalBitmap = ImageLoader.loadBitmap(this.originalPath.toString(), null, (float) size, (float) size, false);
                } else if (this.mediaType == 2) {
                    originalBitmap = ThumbnailUtils.createVideoThumbnail(this.originalPath.toString(), this.info.big ? 2 : 1);
                } else if (this.mediaType == 3) {
                    String path = this.originalPath.toString().toLowerCase();
                    if (path.endsWith("mp4")) {
                        originalBitmap = ThumbnailUtils.createVideoThumbnail(this.originalPath.toString(), this.info.big ? 2 : 1);
                    } else if (path.endsWith(".jpg") || path.endsWith(".jpeg") || path.endsWith(".png") || path.endsWith(".gif")) {
                        originalBitmap = ImageLoader.loadBitmap(path, null, (float) size, (float) size, false);
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
                if (scaleFactor > 1.0f) {
                    Bitmap scaledBitmap = Bitmaps.createScaledBitmap(originalBitmap, (int) (((float) w) / scaleFactor), (int) (((float) h) / scaleFactor), true);
                    if (scaledBitmap != originalBitmap) {
                        originalBitmap.recycle();
                        originalBitmap = scaledBitmap;
                    }
                }
                FileOutputStream stream = new FileOutputStream(thumbFile);
                originalBitmap.compress(CompressFormat.JPEG, this.info.big ? 83 : 60, stream);
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
                ((ImageReceiver) finalImageReceiverArray.get(a)).setImageBitmapByKey(bitmapDrawable, kf, 0, false);
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
        int maxSize;
        this.thumbGeneratingQueue.setPriority(1);
        int memoryClass = ((ActivityManager) ApplicationLoader.applicationContext.getSystemService("activity")).getMemoryClass();
        boolean z = memoryClass >= 256;
        this.canForce8888 = z;
        if (z) {
            maxSize = 30;
        } else {
            maxSize = 15;
        }
        this.memCache = new LruCache((Math.min(maxSize, memoryClass / 7) * 1024) * 1024) {
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

    public void cancelLoadingForImageReceiver(ImageReceiver imageReceiver, boolean cancelAll) {
        if (imageReceiver != null) {
            this.imageLoadQueue.postRunnable(new ImageLoader$$Lambda$1(this, cancelAll, imageReceiver));
        }
    }

    final /* synthetic */ void lambda$cancelLoadingForImageReceiver$2$ImageLoader(boolean cancelAll, ImageReceiver imageReceiver) {
        int a = 0;
        while (a < 3) {
            if (a <= 0 || cancelAll) {
                int imageType;
                if (a == 0) {
                    imageType = 1;
                } else if (a == 1) {
                    imageType = 0;
                } else {
                    imageType = 3;
                }
                int TAG = imageReceiver.getTag(imageType);
                if (TAG != 0) {
                    if (a == 0) {
                        removeFromWaitingForThumb(TAG, imageReceiver);
                    }
                    CacheImage ei = (CacheImage) this.imageLoadingByTag.get(TAG);
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
            String key = imageReceiver.getImageKey();
            if (key != null) {
                this.imageLoadQueue.postRunnable(new ImageLoader$$Lambda$3(this, key));
            }
        }
    }

    final /* synthetic */ void lambda$cancelForceLoadingForImageReceiver$4$ImageLoader(String key) {
        Integer num = (Integer) this.forceLoadingImages.remove(key);
    }

    private void createLoadOperationForImageReceiver(ImageReceiver imageReceiver, String key, String url, String ext, Object imageLocation, String filter, int size, int cacheType, int imageType, int thumb) {
        if (imageReceiver != null && url != null && key != null) {
            int TAG = imageReceiver.getTag(imageType);
            if (TAG == 0) {
                TAG = this.lastImageNum;
                imageReceiver.setTag(TAG, imageType);
                this.lastImageNum++;
                if (this.lastImageNum == Integer.MAX_VALUE) {
                    this.lastImageNum = 0;
                }
            }
            int finalTag = TAG;
            boolean finalIsNeedsQualityThumb = imageReceiver.isNeedsQualityThumb();
            Object parentObject = imageReceiver.getParentObject();
            Document qualityDocument = imageReceiver.getQulityThumbDocument();
            boolean shouldGenerateQualityThumb = imageReceiver.isShouldGenerateQualityThumb();
            int currentAccount = imageReceiver.getCurrentAccount();
            boolean currentKeyQuality = imageType == 0 && imageReceiver.isCurrentKeyQuality();
            this.imageLoadQueue.postRunnable(new ImageLoader$$Lambda$4(this, thumb, url, key, finalTag, imageReceiver, filter, imageType, imageLocation, currentKeyQuality, parentObject, qualityDocument, finalIsNeedsQualityThumb, shouldGenerateQualityThumb, cacheType, size, ext, currentAccount));
        }
    }

    /* JADX WARNING: Missing block: B:116:0x0344, code:
            if (r43.equals("gif") != false) goto L_0x0346;
     */
    final /* synthetic */ void lambda$createLoadOperationForImageReceiver$5$ImageLoader(int r45, java.lang.String r46, java.lang.String r47, int r48, org.telegram.messenger.ImageReceiver r49, java.lang.String r50, int r51, java.lang.Object r52, boolean r53, java.lang.Object r54, org.telegram.tgnet.TLRPC.Document r55, boolean r56, boolean r57, int r58, int r59, java.lang.String r60, int r61) {
        /*
        r44 = this;
        r20 = 0;
        r6 = 2;
        r0 = r45;
        if (r0 == r6) goto L_0x005b;
    L_0x0007:
        r0 = r44;
        r6 = r0.imageLoadingByUrl;
        r0 = r46;
        r23 = r6.get(r0);
        r23 = (org.telegram.messenger.ImageLoader.CacheImage) r23;
        r0 = r44;
        r6 = r0.imageLoadingByKeys;
        r0 = r47;
        r21 = r6.get(r0);
        r21 = (org.telegram.messenger.ImageLoader.CacheImage) r21;
        r0 = r44;
        r6 = r0.imageLoadingByTag;
        r0 = r48;
        r22 = r6.get(r0);
        r22 = (org.telegram.messenger.ImageLoader.CacheImage) r22;
        if (r22 == 0) goto L_0x0035;
    L_0x002d:
        r0 = r22;
        r1 = r21;
        if (r0 != r1) goto L_0x0195;
    L_0x0033:
        r20 = 1;
    L_0x0035:
        if (r20 != 0) goto L_0x0048;
    L_0x0037:
        if (r21 == 0) goto L_0x0048;
    L_0x0039:
        r0 = r21;
        r1 = r49;
        r2 = r47;
        r3 = r50;
        r4 = r51;
        r0.addImageReceiver(r1, r2, r3, r4);
        r20 = 1;
    L_0x0048:
        if (r20 != 0) goto L_0x005b;
    L_0x004a:
        if (r23 == 0) goto L_0x005b;
    L_0x004c:
        r0 = r23;
        r1 = r49;
        r2 = r47;
        r3 = r50;
        r4 = r51;
        r0.addImageReceiver(r1, r2, r3, r4);
        r20 = 1;
    L_0x005b:
        if (r20 != 0) goto L_0x0194;
    L_0x005d:
        r39 = 0;
        r37 = 0;
        r27 = 0;
        r28 = 0;
        r0 = r52;
        r6 = r0 instanceof java.lang.String;
        if (r6 == 0) goto L_0x01e3;
    L_0x006b:
        r7 = r52;
        r7 = (java.lang.String) r7;
        r6 = "http";
        r6 = r7.startsWith(r6);
        if (r6 != 0) goto L_0x00a4;
    L_0x0078:
        r6 = "athumb";
        r6 = r7.startsWith(r6);
        if (r6 != 0) goto L_0x00a4;
    L_0x0081:
        r39 = 1;
        r6 = "thumb://";
        r6 = r7.startsWith(r6);
        if (r6 == 0) goto L_0x01b7;
    L_0x008c:
        r6 = ":";
        r8 = 8;
        r33 = r7.indexOf(r6, r8);
        if (r33 < 0) goto L_0x00a4;
    L_0x0097:
        r27 = new java.io.File;
        r6 = r33 + 1;
        r6 = r7.substring(r6);
        r0 = r27;
        r0.<init>(r6);
    L_0x00a4:
        r6 = 2;
        r0 = r45;
        if (r0 == r6) goto L_0x0194;
    L_0x00a9:
        r0 = r52;
        r6 = r0 instanceof org.telegram.tgnet.TLRPC.TL_documentEncrypted;
        if (r6 != 0) goto L_0x00b5;
    L_0x00af:
        r0 = r52;
        r6 = r0 instanceof org.telegram.tgnet.TLRPC.TL_fileEncryptedLocation;
        if (r6 == 0) goto L_0x0309;
    L_0x00b5:
        r36 = 1;
    L_0x00b7:
        r34 = new org.telegram.messenger.ImageLoader$CacheImage;
        r6 = 0;
        r0 = r34;
        r1 = r44;
        r0.<init>(r1, r6);
        r0 = r52;
        r6 = r0 instanceof org.telegram.messenger.WebFile;
        if (r6 == 0) goto L_0x00d1;
    L_0x00c7:
        r6 = r52;
        r6 = (org.telegram.messenger.WebFile) r6;
        r6 = org.telegram.messenger.MessageObject.isGifDocument(r6);
        if (r6 != 0) goto L_0x00eb;
    L_0x00d1:
        r0 = r52;
        r6 = r0 instanceof org.telegram.tgnet.TLRPC.Document;
        if (r6 == 0) goto L_0x030d;
    L_0x00d7:
        r6 = r52;
        r6 = (org.telegram.tgnet.TLRPC.Document) r6;
        r6 = org.telegram.messenger.MessageObject.isGifDocument(r6);
        if (r6 != 0) goto L_0x00eb;
    L_0x00e1:
        r6 = r52;
        r6 = (org.telegram.tgnet.TLRPC.Document) r6;
        r6 = org.telegram.messenger.MessageObject.isRoundVideoDocument(r6);
        if (r6 == 0) goto L_0x030d;
    L_0x00eb:
        r6 = 1;
        r0 = r34;
        r0.animatedFile = r6;
    L_0x00f0:
        if (r27 != 0) goto L_0x00fa;
    L_0x00f2:
        r0 = r52;
        r6 = r0 instanceof org.telegram.tgnet.TLRPC.TL_photoStrippedSize;
        if (r6 == 0) goto L_0x034d;
    L_0x00f8:
        r39 = 1;
    L_0x00fa:
        r0 = r51;
        r1 = r34;
        r1.imageType = r0;
        r0 = r47;
        r1 = r34;
        r1.key = r0;
        r0 = r50;
        r1 = r34;
        r1.filter = r0;
        r0 = r52;
        r1 = r34;
        r1.location = r0;
        r0 = r60;
        r1 = r34;
        r1.ext = r0;
        r0 = r61;
        r1 = r34;
        r1.currentAccount = r0;
        r0 = r54;
        r1 = r34;
        r1.parentObject = r0;
        r6 = 2;
        r0 = r58;
        if (r0 != r6) goto L_0x014c;
    L_0x0129:
        r6 = new java.io.File;
        r8 = org.telegram.messenger.FileLoader.getInternalCacheDir();
        r9 = new java.lang.StringBuilder;
        r9.<init>();
        r0 = r46;
        r9 = r9.append(r0);
        r10 = ".enc.key";
        r9 = r9.append(r10);
        r9 = r9.toString();
        r6.<init>(r8, r9);
        r0 = r34;
        r0.encryptionKeyPath = r6;
    L_0x014c:
        r0 = r34;
        r1 = r49;
        r2 = r47;
        r3 = r50;
        r4 = r51;
        r0.addImageReceiver(r1, r2, r3, r4);
        if (r39 != 0) goto L_0x0163;
    L_0x015b:
        if (r28 != 0) goto L_0x0163;
    L_0x015d:
        r6 = r27.exists();
        if (r6 == 0) goto L_0x049e;
    L_0x0163:
        r0 = r27;
        r1 = r34;
        r1.finalFilePath = r0;
        r0 = r52;
        r1 = r34;
        r1.location = r0;
        r6 = new org.telegram.messenger.ImageLoader$CacheOutTask;
        r0 = r44;
        r1 = r34;
        r6.<init>(r1);
        r0 = r34;
        r0.cacheTask = r6;
        r0 = r44;
        r6 = r0.imageLoadingByKeys;
        r0 = r47;
        r1 = r34;
        r6.put(r0, r1);
        if (r45 == 0) goto L_0x0491;
    L_0x0189:
        r0 = r44;
        r6 = r0.cacheThumbOutQueue;
        r0 = r34;
        r8 = r0.cacheTask;
        r6.postRunnable(r8);
    L_0x0194:
        return;
    L_0x0195:
        r0 = r22;
        r1 = r23;
        if (r0 != r1) goto L_0x01ae;
    L_0x019b:
        if (r21 != 0) goto L_0x01aa;
    L_0x019d:
        r0 = r22;
        r1 = r49;
        r2 = r47;
        r3 = r50;
        r4 = r51;
        r0.replaceImageReceiver(r1, r2, r3, r4);
    L_0x01aa:
        r20 = 1;
        goto L_0x0035;
    L_0x01ae:
        r0 = r22;
        r1 = r49;
        r0.removeImageReceiver(r1);
        goto L_0x0035;
    L_0x01b7:
        r6 = "vthumb://";
        r6 = r7.startsWith(r6);
        if (r6 == 0) goto L_0x01da;
    L_0x01c0:
        r6 = ":";
        r8 = 9;
        r33 = r7.indexOf(r6, r8);
        if (r33 < 0) goto L_0x00a4;
    L_0x01cb:
        r27 = new java.io.File;
        r6 = r33 + 1;
        r6 = r7.substring(r6);
        r0 = r27;
        r0.<init>(r6);
        goto L_0x00a4;
    L_0x01da:
        r27 = new java.io.File;
        r0 = r27;
        r0.<init>(r7);
        goto L_0x00a4;
    L_0x01e3:
        if (r45 != 0) goto L_0x00a4;
    L_0x01e5:
        if (r53 == 0) goto L_0x00a4;
    L_0x01e7:
        r39 = 1;
        r0 = r54;
        r6 = r0 instanceof org.telegram.messenger.MessageObject;
        if (r6 == 0) goto L_0x02dd;
    L_0x01ef:
        r41 = r54;
        r41 = (org.telegram.messenger.MessageObject) r41;
        r40 = r41.getDocument();
        r0 = r41;
        r6 = r0.messageOwner;
        r0 = r6.attachPath;
        r38 = r0;
        r0 = r41;
        r6 = r0.messageOwner;
        r29 = org.telegram.messenger.FileLoader.getPathToMessage(r6);
        r32 = r41.getFileType();
        r25 = 0;
    L_0x020d:
        if (r40 == 0) goto L_0x00a4;
    L_0x020f:
        if (r56 == 0) goto L_0x0253;
    L_0x0211:
        r27 = new java.io.File;
        r6 = 4;
        r6 = org.telegram.messenger.FileLoader.getDirectory(r6);
        r8 = new java.lang.StringBuilder;
        r8.<init>();
        r9 = "q_";
        r8 = r8.append(r9);
        r0 = r40;
        r9 = r0.dc_id;
        r8 = r8.append(r9);
        r9 = "_";
        r8 = r8.append(r9);
        r0 = r40;
        r10 = r0.id;
        r8 = r8.append(r10);
        r9 = ".jpg";
        r8 = r8.append(r9);
        r8 = r8.toString();
        r0 = r27;
        r0.<init>(r6, r8);
        r6 = r27.exists();
        if (r6 != 0) goto L_0x0305;
    L_0x0251:
        r27 = 0;
    L_0x0253:
        r24 = 0;
        r6 = android.text.TextUtils.isEmpty(r38);
        if (r6 != 0) goto L_0x026c;
    L_0x025b:
        r24 = new java.io.File;
        r0 = r24;
        r1 = r38;
        r0.<init>(r1);
        r6 = r24.exists();
        if (r6 != 0) goto L_0x026c;
    L_0x026a:
        r24 = 0;
    L_0x026c:
        if (r24 != 0) goto L_0x0270;
    L_0x026e:
        r24 = r29;
    L_0x0270:
        if (r27 != 0) goto L_0x00a4;
    L_0x0272:
        r7 = org.telegram.messenger.FileLoader.getAttachFileName(r40);
        r0 = r44;
        r6 = r0.waitingForQualityThumb;
        r35 = r6.get(r7);
        r35 = (org.telegram.messenger.ImageLoader.ThumbGenerateInfo) r35;
        if (r35 != 0) goto L_0x02aa;
    L_0x0282:
        r35 = new org.telegram.messenger.ImageLoader$ThumbGenerateInfo;
        r6 = 0;
        r0 = r35;
        r1 = r44;
        r0.<init>(r1, r6);
        r0 = r35;
        r1 = r40;
        r0.parentDocument = r1;
        r0 = r35;
        r1 = r50;
        r0.filter = r1;
        r0 = r35;
        r1 = r25;
        r0.big = r1;
        r0 = r44;
        r6 = r0.waitingForQualityThumb;
        r0 = r35;
        r6.put(r7, r0);
    L_0x02aa:
        r6 = r35.imageReceiverArray;
        r0 = r49;
        r6 = r6.contains(r0);
        if (r6 != 0) goto L_0x02bf;
    L_0x02b6:
        r6 = r35.imageReceiverArray;
        r0 = r49;
        r6.add(r0);
    L_0x02bf:
        r0 = r44;
        r6 = r0.waitingForQualityThumbByTag;
        r0 = r48;
        r6.put(r0, r7);
        r6 = r24.exists();
        if (r6 == 0) goto L_0x0194;
    L_0x02ce:
        if (r57 == 0) goto L_0x0194;
    L_0x02d0:
        r0 = r44;
        r1 = r32;
        r2 = r24;
        r3 = r35;
        r0.generateThumb(r1, r2, r3);
        goto L_0x0194;
    L_0x02dd:
        if (r55 == 0) goto L_0x02f9;
    L_0x02df:
        r40 = r55;
        r6 = 1;
        r0 = r40;
        r29 = org.telegram.messenger.FileLoader.getPathToAttach(r0, r6);
        r6 = org.telegram.messenger.MessageObject.isVideoDocument(r40);
        if (r6 == 0) goto L_0x02f6;
    L_0x02ee:
        r32 = 2;
    L_0x02f0:
        r38 = 0;
        r25 = 1;
        goto L_0x020d;
    L_0x02f6:
        r32 = 3;
        goto L_0x02f0;
    L_0x02f9:
        r40 = 0;
        r38 = 0;
        r29 = 0;
        r32 = 0;
        r25 = 0;
        goto L_0x020d;
    L_0x0305:
        r28 = 1;
        goto L_0x0253;
    L_0x0309:
        r36 = 0;
        goto L_0x00b7;
    L_0x030d:
        r0 = r52;
        r6 = r0 instanceof java.lang.String;
        if (r6 == 0) goto L_0x00f0;
    L_0x0313:
        r7 = r52;
        r7 = (java.lang.String) r7;
        r6 = "vthumb";
        r6 = r7.startsWith(r6);
        if (r6 != 0) goto L_0x00f0;
    L_0x0320:
        r6 = "thumb";
        r6 = r7.startsWith(r6);
        if (r6 != 0) goto L_0x00f0;
    L_0x0329:
        r6 = "jpg";
        r43 = getHttpUrlExtension(r7, r6);
        r6 = "mp4";
        r0 = r43;
        r6 = r0.equals(r6);
        if (r6 != 0) goto L_0x0346;
    L_0x033b:
        r6 = "gif";
        r0 = r43;
        r6 = r0.equals(r6);
        if (r6 == 0) goto L_0x00f0;
    L_0x0346:
        r6 = 1;
        r0 = r34;
        r0.animatedFile = r6;
        goto L_0x00f0;
    L_0x034d:
        r0 = r52;
        r6 = r0 instanceof org.telegram.messenger.SecureDocument;
        if (r6 == 0) goto L_0x037c;
    L_0x0353:
        r6 = r52;
        r6 = (org.telegram.messenger.SecureDocument) r6;
        r0 = r34;
        r0.secureDocument = r6;
        r0 = r34;
        r6 = r0.secureDocument;
        r6 = r6.secureFile;
        r6 = r6.dc_id;
        r8 = -NUM; // 0xfffffffvar_ float:-0.0 double:NaN;
        if (r6 != r8) goto L_0x0379;
    L_0x0367:
        r39 = 1;
    L_0x0369:
        r27 = new java.io.File;
        r6 = 4;
        r6 = org.telegram.messenger.FileLoader.getDirectory(r6);
        r0 = r27;
        r1 = r46;
        r0.<init>(r6, r1);
        goto L_0x00fa;
    L_0x0379:
        r39 = 0;
        goto L_0x0369;
    L_0x037c:
        r6 = "g";
        r0 = r50;
        r6 = r6.equals(r0);
        if (r6 != 0) goto L_0x03d4;
    L_0x0387:
        if (r58 != 0) goto L_0x0393;
    L_0x0389:
        if (r59 <= 0) goto L_0x0393;
    L_0x038b:
        r0 = r52;
        r6 = r0 instanceof java.lang.String;
        if (r6 != 0) goto L_0x0393;
    L_0x0391:
        if (r36 == 0) goto L_0x03d4;
    L_0x0393:
        r27 = new java.io.File;
        r6 = 4;
        r6 = org.telegram.messenger.FileLoader.getDirectory(r6);
        r0 = r27;
        r1 = r46;
        r0.<init>(r6, r1);
        r6 = r27.exists();
        if (r6 == 0) goto L_0x03ab;
    L_0x03a7:
        r28 = 1;
        goto L_0x00fa;
    L_0x03ab:
        r6 = 2;
        r0 = r58;
        if (r0 != r6) goto L_0x00fa;
    L_0x03b0:
        r27 = new java.io.File;
        r6 = 4;
        r6 = org.telegram.messenger.FileLoader.getDirectory(r6);
        r8 = new java.lang.StringBuilder;
        r8.<init>();
        r0 = r46;
        r8 = r8.append(r0);
        r9 = ".enc";
        r8 = r8.append(r9);
        r8 = r8.toString();
        r0 = r27;
        r0.<init>(r6, r8);
        goto L_0x00fa;
    L_0x03d4:
        r0 = r52;
        r6 = r0 instanceof org.telegram.tgnet.TLRPC.Document;
        if (r6 == 0) goto L_0x046b;
    L_0x03da:
        r30 = r52;
        r30 = (org.telegram.tgnet.TLRPC.Document) r30;
        r0 = r30;
        r6 = r0 instanceof org.telegram.tgnet.TLRPC.TL_documentEncrypted;
        if (r6 == 0) goto L_0x0447;
    L_0x03e4:
        r27 = new java.io.File;
        r6 = 4;
        r6 = org.telegram.messenger.FileLoader.getDirectory(r6);
        r0 = r27;
        r1 = r46;
        r0.<init>(r6, r1);
    L_0x03f2:
        r6 = "g";
        r0 = r50;
        r6 = r6.equals(r0);
        if (r6 == 0) goto L_0x00fa;
    L_0x03fd:
        r6 = 1;
        r0 = r34;
        r0.animatedFile = r6;
        r0 = r30;
        r6 = r0.size;
        r0 = r34;
        r0.size = r6;
        r39 = 1;
        r6 = r27.exists();
        if (r6 != 0) goto L_0x00fa;
    L_0x0412:
        r27 = new java.io.File;
        r6 = 4;
        r6 = org.telegram.messenger.FileLoader.getDirectory(r6);
        r8 = new java.lang.StringBuilder;
        r8.<init>();
        r0 = r30;
        r9 = r0.dc_id;
        r8 = r8.append(r9);
        r9 = "_";
        r8 = r8.append(r9);
        r0 = r30;
        r10 = r0.id;
        r8 = r8.append(r10);
        r9 = ".temp";
        r8 = r8.append(r9);
        r8 = r8.toString();
        r0 = r27;
        r0.<init>(r6, r8);
        goto L_0x00fa;
    L_0x0447:
        r6 = org.telegram.messenger.MessageObject.isVideoDocument(r30);
        if (r6 == 0) goto L_0x045c;
    L_0x044d:
        r27 = new java.io.File;
        r6 = 2;
        r6 = org.telegram.messenger.FileLoader.getDirectory(r6);
        r0 = r27;
        r1 = r46;
        r0.<init>(r6, r1);
        goto L_0x03f2;
    L_0x045c:
        r27 = new java.io.File;
        r6 = 3;
        r6 = org.telegram.messenger.FileLoader.getDirectory(r6);
        r0 = r27;
        r1 = r46;
        r0.<init>(r6, r1);
        goto L_0x03f2;
    L_0x046b:
        r0 = r52;
        r6 = r0 instanceof org.telegram.messenger.WebFile;
        if (r6 == 0) goto L_0x0481;
    L_0x0471:
        r27 = new java.io.File;
        r6 = 3;
        r6 = org.telegram.messenger.FileLoader.getDirectory(r6);
        r0 = r27;
        r1 = r46;
        r0.<init>(r6, r1);
        goto L_0x00fa;
    L_0x0481:
        r27 = new java.io.File;
        r6 = 0;
        r6 = org.telegram.messenger.FileLoader.getDirectory(r6);
        r0 = r27;
        r1 = r46;
        r0.<init>(r6, r1);
        goto L_0x00fa;
    L_0x0491:
        r0 = r44;
        r6 = r0.cacheOutQueue;
        r0 = r34;
        r8 = r0.cacheTask;
        r6.postRunnable(r8);
        goto L_0x0194;
    L_0x049e:
        r0 = r46;
        r1 = r34;
        r1.url = r0;
        r0 = r44;
        r6 = r0.imageLoadingByUrl;
        r0 = r46;
        r1 = r34;
        r6.put(r0, r1);
        r0 = r52;
        r6 = r0 instanceof java.lang.String;
        if (r6 == 0) goto L_0x0534;
    L_0x04b5:
        r7 = r52;
        r7 = (java.lang.String) r7;
        r31 = org.telegram.messenger.Utilities.MD5(r7);
        r6 = 4;
        r26 = org.telegram.messenger.FileLoader.getDirectory(r6);
        r6 = new java.io.File;
        r8 = new java.lang.StringBuilder;
        r8.<init>();
        r0 = r31;
        r8 = r8.append(r0);
        r9 = "_temp.jpg";
        r8 = r8.append(r9);
        r8 = r8.toString();
        r0 = r26;
        r6.<init>(r0, r8);
        r0 = r34;
        r0.tempFilePath = r6;
        r0 = r27;
        r1 = r34;
        r1.finalFilePath = r0;
        r6 = "athumb";
        r6 = r7.startsWith(r6);
        if (r6 == 0) goto L_0x0512;
    L_0x04f2:
        r6 = new org.telegram.messenger.ImageLoader$ArtworkLoadTask;
        r0 = r44;
        r1 = r34;
        r6.<init>(r1);
        r0 = r34;
        r0.artworkTask = r6;
        r0 = r44;
        r6 = r0.artworkTasks;
        r0 = r34;
        r8 = r0.artworkTask;
        r6.add(r8);
        r6 = 0;
        r0 = r44;
        r0.runArtworkTasks(r6);
        goto L_0x0194;
    L_0x0512:
        r6 = new org.telegram.messenger.ImageLoader$HttpImageTask;
        r0 = r44;
        r1 = r34;
        r2 = r59;
        r6.<init>(r1, r2);
        r0 = r34;
        r0.httpTask = r6;
        r0 = r44;
        r6 = r0.httpTasks;
        r0 = r34;
        r8 = r0.httpTask;
        r6.add(r8);
        r6 = 0;
        r0 = r44;
        r0.runHttpTasks(r6);
        goto L_0x0194;
    L_0x0534:
        r0 = r52;
        r6 = r0 instanceof org.telegram.tgnet.TLRPC.FileLocation;
        if (r6 == 0) goto L_0x0573;
    L_0x053a:
        r7 = r52;
        r7 = (org.telegram.tgnet.TLRPC.FileLocation) r7;
        r12 = r58;
        if (r12 != 0) goto L_0x0549;
    L_0x0542:
        if (r59 <= 0) goto L_0x0548;
    L_0x0544:
        r6 = r7.key;
        if (r6 == 0) goto L_0x0549;
    L_0x0548:
        r12 = 1;
    L_0x0549:
        r6 = org.telegram.messenger.FileLoader.getInstance(r61);
        if (r45 == 0) goto L_0x0571;
    L_0x054f:
        r11 = 2;
    L_0x0550:
        r8 = r54;
        r9 = r60;
        r10 = r59;
        r6.loadFile(r7, r8, r9, r10, r11, r12);
    L_0x0559:
        r6 = r49.isForceLoding();
        if (r6 == 0) goto L_0x0194;
    L_0x055f:
        r0 = r44;
        r6 = r0.forceLoadingImages;
        r0 = r34;
        r8 = r0.key;
        r9 = 0;
        r9 = java.lang.Integer.valueOf(r9);
        r6.put(r8, r9);
        goto L_0x0194;
    L_0x0571:
        r11 = 1;
        goto L_0x0550;
    L_0x0573:
        r0 = r52;
        r6 = r0 instanceof org.telegram.tgnet.TLRPC.PhotoSize;
        if (r6 == 0) goto L_0x05a7;
    L_0x0579:
        r42 = r52;
        r42 = (org.telegram.tgnet.TLRPC.PhotoSize) r42;
        r12 = r58;
        if (r12 != 0) goto L_0x058c;
    L_0x0581:
        if (r59 <= 0) goto L_0x058b;
    L_0x0583:
        r0 = r42;
        r6 = r0.location;
        r6 = r6.key;
        if (r6 == 0) goto L_0x058c;
    L_0x058b:
        r12 = 1;
    L_0x058c:
        r13 = org.telegram.messenger.FileLoader.getInstance(r61);
        r0 = r42;
        r14 = r0.location;
        if (r45 == 0) goto L_0x05a4;
    L_0x0596:
        r18 = 2;
    L_0x0598:
        r15 = r54;
        r16 = r60;
        r17 = r59;
        r19 = r12;
        r13.loadFile(r14, r15, r16, r17, r18, r19);
        goto L_0x0559;
    L_0x05a4:
        r18 = 1;
        goto L_0x0598;
    L_0x05a7:
        r0 = r52;
        r6 = r0 instanceof org.telegram.tgnet.TLRPC.Document;
        if (r6 == 0) goto L_0x05c2;
    L_0x05ad:
        r8 = org.telegram.messenger.FileLoader.getInstance(r61);
        r52 = (org.telegram.tgnet.TLRPC.Document) r52;
        if (r45 == 0) goto L_0x05c0;
    L_0x05b5:
        r6 = 2;
    L_0x05b6:
        r0 = r52;
        r1 = r54;
        r2 = r58;
        r8.loadFile(r0, r1, r6, r2);
        goto L_0x0559;
    L_0x05c0:
        r6 = 1;
        goto L_0x05b6;
    L_0x05c2:
        r0 = r52;
        r6 = r0 instanceof org.telegram.messenger.SecureDocument;
        if (r6 == 0) goto L_0x05d9;
    L_0x05c8:
        r8 = org.telegram.messenger.FileLoader.getInstance(r61);
        r52 = (org.telegram.messenger.SecureDocument) r52;
        if (r45 == 0) goto L_0x05d7;
    L_0x05d0:
        r6 = 2;
    L_0x05d1:
        r0 = r52;
        r8.loadFile(r0, r6);
        goto L_0x0559;
    L_0x05d7:
        r6 = 1;
        goto L_0x05d1;
    L_0x05d9:
        r0 = r52;
        r6 = r0 instanceof org.telegram.messenger.WebFile;
        if (r6 == 0) goto L_0x0559;
    L_0x05df:
        r8 = org.telegram.messenger.FileLoader.getInstance(r61);
        r52 = (org.telegram.messenger.WebFile) r52;
        if (r45 == 0) goto L_0x05f1;
    L_0x05e7:
        r6 = 2;
    L_0x05e8:
        r0 = r52;
        r1 = r58;
        r8.loadFile(r0, r6, r1);
        goto L_0x0559;
    L_0x05f1:
        r6 = 1;
        goto L_0x05e8;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ImageLoader.lambda$createLoadOperationForImageReceiver$5$ImageLoader(int, java.lang.String, java.lang.String, int, org.telegram.messenger.ImageReceiver, java.lang.String, int, java.lang.Object, boolean, java.lang.Object, org.telegram.tgnet.TLRPC$Document, boolean, boolean, int, int, java.lang.String, int):void");
    }

    /* JADX WARNING: Removed duplicated region for block: B:102:0x033e  */
    public void loadImageForImageReceiver(org.telegram.messenger.ImageReceiver r60) {
        /*
        r59 = this;
        if (r60 != 0) goto L_0x0003;
    L_0x0002:
        return;
    L_0x0003:
        r46 = 0;
        r29 = r60.getMediaKey();
        if (r29 == 0) goto L_0x0032;
    L_0x000b:
        r0 = r59;
        r4 = r0.memCache;
        r0 = r29;
        r39 = r4.get(r0);
        if (r39 == 0) goto L_0x0032;
    L_0x0017:
        r4 = 1;
        r0 = r59;
        r1 = r60;
        r0.cancelLoadingForImageReceiver(r1, r4);
        r4 = 3;
        r5 = 1;
        r0 = r60;
        r1 = r39;
        r2 = r29;
        r0.setImageBitmapByKey(r1, r2, r4, r5);
        r46 = 1;
        r4 = r60.isForcePreview();
        if (r4 == 0) goto L_0x0002;
    L_0x0032:
        r44 = r60.getImageKey();
        if (r46 != 0) goto L_0x0063;
    L_0x0038:
        if (r44 == 0) goto L_0x0063;
    L_0x003a:
        r0 = r59;
        r4 = r0.memCache;
        r0 = r44;
        r39 = r4.get(r0);
        if (r39 == 0) goto L_0x0063;
    L_0x0046:
        r4 = 1;
        r0 = r59;
        r1 = r60;
        r0.cancelLoadingForImageReceiver(r1, r4);
        r4 = 0;
        r5 = 1;
        r0 = r60;
        r1 = r39;
        r2 = r44;
        r0.setImageBitmapByKey(r1, r2, r4, r5);
        r46 = 1;
        r4 = r60.isForcePreview();
        if (r4 != 0) goto L_0x0063;
    L_0x0061:
        if (r29 == 0) goto L_0x0002;
    L_0x0063:
        r57 = 0;
        r6 = r60.getThumbKey();
        if (r6 == 0) goto L_0x0090;
    L_0x006b:
        r0 = r59;
        r4 = r0.memCache;
        r39 = r4.get(r6);
        if (r39 == 0) goto L_0x0090;
    L_0x0075:
        r4 = 1;
        r5 = 1;
        r0 = r60;
        r1 = r39;
        r0.setImageBitmapByKey(r1, r6, r4, r5);
        r4 = 0;
        r0 = r59;
        r1 = r60;
        r0.cancelLoadingForImageReceiver(r1, r4);
        if (r46 == 0) goto L_0x008e;
    L_0x0088:
        r4 = r60.isForcePreview();
        if (r4 != 0) goto L_0x0002;
    L_0x008e:
        r57 = 1;
    L_0x0090:
        r55 = 0;
        r52 = r60.getParentObject();
        r54 = r60.getQulityThumbDocument();
        r9 = r60.getThumbLocation();
        r50 = r60.getMediaLocation();
        r45 = r60.getImageLocation();
        if (r45 != 0) goto L_0x00c4;
    L_0x00a8:
        r4 = r60.isNeedsQualityThumb();
        if (r4 == 0) goto L_0x00c4;
    L_0x00ae:
        r4 = r60.isCurrentKeyQuality();
        if (r4 == 0) goto L_0x00c4;
    L_0x00b4:
        r0 = r52;
        r4 = r0 instanceof org.telegram.messenger.MessageObject;
        if (r4 == 0) goto L_0x00ed;
    L_0x00ba:
        r4 = r52;
        r4 = (org.telegram.messenger.MessageObject) r4;
        r45 = r4.getDocument();
        r55 = 1;
    L_0x00c4:
        r56 = 0;
        r47 = 0;
        r7 = 0;
        r30 = 0;
        r44 = 0;
        r6 = 0;
        r29 = 0;
        r8 = r60.getExt();
        if (r8 != 0) goto L_0x00d9;
    L_0x00d6:
        r8 = "jpg";
    L_0x00d9:
        r38 = 0;
        r16 = r45;
        r32 = r50;
    L_0x00df:
        r4 = 2;
        r0 = r38;
        if (r0 >= r4) goto L_0x03d1;
    L_0x00e4:
        if (r38 != 0) goto L_0x00f4;
    L_0x00e6:
        r51 = r16;
    L_0x00e8:
        if (r51 != 0) goto L_0x00f7;
    L_0x00ea:
        r38 = r38 + 1;
        goto L_0x00df;
    L_0x00ed:
        if (r54 == 0) goto L_0x00c4;
    L_0x00ef:
        r45 = r54;
        r55 = 1;
        goto L_0x00c4;
    L_0x00f4:
        r51 = r32;
        goto L_0x00e8;
    L_0x00f7:
        r48 = 0;
        r58 = 0;
        r0 = r51;
        r4 = r0 instanceof java.lang.String;
        if (r4 == 0) goto L_0x0141;
    L_0x0101:
        r49 = r51;
        r49 = (java.lang.String) r49;
        r48 = org.telegram.messenger.Utilities.MD5(r49);
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r0 = r48;
        r4 = r4.append(r0);
        r5 = ".";
        r4 = r4.append(r5);
        r5 = "jpg";
        r0 = r49;
        r5 = getHttpUrlExtension(r0, r5);
        r4 = r4.append(r5);
        r58 = r4.toString();
    L_0x012c:
        if (r38 != 0) goto L_0x03c1;
    L_0x012e:
        r44 = r48;
        r47 = r58;
    L_0x0132:
        r0 = r51;
        if (r0 != r9) goto L_0x00ea;
    L_0x0136:
        if (r38 != 0) goto L_0x03c7;
    L_0x0138:
        r45 = 0;
        r44 = 0;
        r47 = 0;
        r16 = r45;
        goto L_0x00ea;
    L_0x0141:
        r0 = r51;
        r4 = r0 instanceof org.telegram.tgnet.TLRPC.FileLocation;
        if (r4 == 0) goto L_0x01a5;
    L_0x0147:
        r49 = r51;
        r49 = (org.telegram.tgnet.TLRPC.FileLocation) r49;
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r0 = r49;
        r14 = r0.volume_id;
        r4 = r4.append(r14);
        r5 = "_";
        r4 = r4.append(r5);
        r0 = r49;
        r5 = r0.local_id;
        r4 = r4.append(r5);
        r48 = r4.toString();
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r0 = r48;
        r4 = r4.append(r0);
        r5 = ".";
        r4 = r4.append(r5);
        r4 = r4.append(r8);
        r58 = r4.toString();
        r4 = r60.getExt();
        if (r4 != 0) goto L_0x01a2;
    L_0x018b:
        r0 = r49;
        r4 = r0.key;
        if (r4 != 0) goto L_0x01a2;
    L_0x0191:
        r0 = r49;
        r4 = r0.volume_id;
        r14 = -NUM; // 0xfffffffvar_ float:-0.0 double:NaN;
        r4 = (r4 > r14 ? 1 : (r4 == r14 ? 0 : -1));
        if (r4 != 0) goto L_0x012c;
    L_0x019c:
        r0 = r49;
        r4 = r0.local_id;
        if (r4 >= 0) goto L_0x012c;
    L_0x01a2:
        r56 = 1;
        goto L_0x012c;
    L_0x01a5:
        r0 = r51;
        r4 = r0 instanceof org.telegram.tgnet.TLRPC.TL_photoStrippedSize;
        if (r4 == 0) goto L_0x01e3;
    L_0x01ab:
        r49 = r51;
        r49 = (org.telegram.tgnet.TLRPC.TL_photoStrippedSize) r49;
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r5 = "stripped";
        r4 = r4.append(r5);
        r5 = org.telegram.messenger.FileRefController.getKeyForParentObject(r52);
        r4 = r4.append(r5);
        r48 = r4.toString();
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r0 = r48;
        r4 = r4.append(r0);
        r5 = ".";
        r4 = r4.append(r5);
        r4 = r4.append(r8);
        r58 = r4.toString();
        goto L_0x012c;
    L_0x01e3:
        r0 = r51;
        r4 = r0 instanceof org.telegram.tgnet.TLRPC.TL_photoSize;
        if (r4 == 0) goto L_0x0252;
    L_0x01e9:
        r53 = r51;
        r53 = (org.telegram.tgnet.TLRPC.TL_photoSize) r53;
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r0 = r53;
        r5 = r0.location;
        r14 = r5.volume_id;
        r4 = r4.append(r14);
        r5 = "_";
        r4 = r4.append(r5);
        r0 = r53;
        r5 = r0.location;
        r5 = r5.local_id;
        r4 = r4.append(r5);
        r48 = r4.toString();
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r0 = r48;
        r4 = r4.append(r0);
        r5 = ".";
        r4 = r4.append(r5);
        r4 = r4.append(r8);
        r58 = r4.toString();
        r4 = r60.getExt();
        if (r4 != 0) goto L_0x024e;
    L_0x0231:
        r0 = r53;
        r4 = r0.location;
        r4 = r4.key;
        if (r4 != 0) goto L_0x024e;
    L_0x0239:
        r0 = r53;
        r4 = r0.location;
        r4 = r4.volume_id;
        r14 = -NUM; // 0xfffffffvar_ float:-0.0 double:NaN;
        r4 = (r4 > r14 ? 1 : (r4 == r14 ? 0 : -1));
        if (r4 != 0) goto L_0x012c;
    L_0x0246:
        r0 = r53;
        r4 = r0.location;
        r4 = r4.local_id;
        if (r4 >= 0) goto L_0x012c;
    L_0x024e:
        r56 = 1;
        goto L_0x012c;
    L_0x0252:
        r0 = r51;
        r4 = r0 instanceof org.telegram.messenger.WebFile;
        if (r4 == 0) goto L_0x0292;
    L_0x0258:
        r42 = r51;
        r42 = (org.telegram.messenger.WebFile) r42;
        r0 = r42;
        r4 = r0.mime_type;
        r40 = org.telegram.messenger.FileLoader.getMimeTypePart(r4);
        r0 = r42;
        r4 = r0.url;
        r48 = org.telegram.messenger.Utilities.MD5(r4);
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r0 = r48;
        r4 = r4.append(r0);
        r5 = ".";
        r4 = r4.append(r5);
        r0 = r42;
        r5 = r0.url;
        r0 = r40;
        r5 = getHttpUrlExtension(r5, r0);
        r4 = r4.append(r5);
        r58 = r4.toString();
        goto L_0x012c;
    L_0x0292:
        r0 = r51;
        r4 = r0 instanceof org.telegram.messenger.SecureDocument;
        if (r4 == 0) goto L_0x02dc;
    L_0x0298:
        r42 = r51;
        r42 = (org.telegram.messenger.SecureDocument) r42;
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r0 = r42;
        r5 = r0.secureFile;
        r5 = r5.dc_id;
        r4 = r4.append(r5);
        r5 = "_";
        r4 = r4.append(r5);
        r0 = r42;
        r5 = r0.secureFile;
        r14 = r5.id;
        r4 = r4.append(r14);
        r48 = r4.toString();
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r0 = r48;
        r4 = r4.append(r0);
        r5 = ".";
        r4 = r4.append(r5);
        r4 = r4.append(r8);
        r58 = r4.toString();
        goto L_0x012c;
    L_0x02dc:
        r0 = r51;
        r4 = r0 instanceof org.telegram.tgnet.TLRPC.Document;
        if (r4 == 0) goto L_0x012c;
    L_0x02e2:
        r42 = r51;
        r42 = (org.telegram.tgnet.TLRPC.Document) r42;
        r0 = r42;
        r4 = r0.id;
        r14 = 0;
        r4 = (r4 > r14 ? 1 : (r4 == r14 ? 0 : -1));
        if (r4 == 0) goto L_0x012c;
    L_0x02f0:
        r0 = r42;
        r4 = r0.dc_id;
        if (r4 == 0) goto L_0x012c;
    L_0x02f6:
        if (r38 != 0) goto L_0x037f;
    L_0x02f8:
        if (r55 == 0) goto L_0x037f;
    L_0x02fa:
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r5 = "q_";
        r4 = r4.append(r5);
        r0 = r42;
        r5 = r0.dc_id;
        r4 = r4.append(r5);
        r5 = "_";
        r4 = r4.append(r5);
        r0 = r42;
        r14 = r0.id;
        r4 = r4.append(r14);
        r48 = r4.toString();
    L_0x0321:
        r41 = org.telegram.messenger.FileLoader.getDocumentFileName(r42);
        if (r41 == 0) goto L_0x0334;
    L_0x0327:
        r4 = 46;
        r0 = r41;
        r43 = r0.lastIndexOf(r4);
        r4 = -1;
        r0 = r43;
        if (r0 != r4) goto L_0x03a0;
    L_0x0334:
        r41 = "";
    L_0x0337:
        r4 = r41.length();
        r5 = 1;
        if (r4 > r5) goto L_0x034e;
    L_0x033e:
        r4 = "video/mp4";
        r0 = r42;
        r5 = r0.mime_type;
        r4 = r4.equals(r5);
        if (r4 == 0) goto L_0x03a9;
    L_0x034b:
        r41 = ".mp4";
    L_0x034e:
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r0 = r48;
        r4 = r4.append(r0);
        r0 = r41;
        r4 = r4.append(r0);
        r58 = r4.toString();
        r4 = org.telegram.messenger.MessageObject.isVideoDocument(r42);
        if (r4 != 0) goto L_0x03be;
    L_0x0369:
        r4 = org.telegram.messenger.MessageObject.isGifDocument(r42);
        if (r4 != 0) goto L_0x03be;
    L_0x036f:
        r4 = org.telegram.messenger.MessageObject.isRoundVideoDocument(r42);
        if (r4 != 0) goto L_0x03be;
    L_0x0375:
        r4 = org.telegram.messenger.MessageObject.canPreviewDocument(r42);
        if (r4 != 0) goto L_0x03be;
    L_0x037b:
        r56 = 1;
    L_0x037d:
        goto L_0x012c;
    L_0x037f:
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r0 = r42;
        r5 = r0.dc_id;
        r4 = r4.append(r5);
        r5 = "_";
        r4 = r4.append(r5);
        r0 = r42;
        r14 = r0.id;
        r4 = r4.append(r14);
        r48 = r4.toString();
        goto L_0x0321;
    L_0x03a0:
        r0 = r41;
        r1 = r43;
        r41 = r0.substring(r1);
        goto L_0x0337;
    L_0x03a9:
        r4 = "video/x-matroska";
        r0 = r42;
        r5 = r0.mime_type;
        r4 = r4.equals(r5);
        if (r4 == 0) goto L_0x03ba;
    L_0x03b6:
        r41 = ".mkv";
        goto L_0x034e;
    L_0x03ba:
        r41 = "";
        goto L_0x034e;
    L_0x03be:
        r56 = 0;
        goto L_0x037d;
    L_0x03c1:
        r29 = r48;
        r30 = r58;
        goto L_0x0132;
    L_0x03c7:
        r50 = 0;
        r29 = 0;
        r30 = 0;
        r32 = r50;
        goto L_0x00ea;
    L_0x03d1:
        r4 = r9 instanceof java.lang.String;
        if (r4 == 0) goto L_0x0491;
    L_0x03d5:
        r49 = r9;
        r49 = (java.lang.String) r49;
        r6 = org.telegram.messenger.Utilities.MD5(r49);
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r4 = r4.append(r6);
        r5 = ".";
        r4 = r4.append(r5);
        r5 = "jpg";
        r0 = r49;
        r5 = getHttpUrlExtension(r0, r5);
        r4 = r4.append(r5);
        r7 = r4.toString();
    L_0x03fe:
        r33 = r60.getMediaFilter();
        r17 = r60.getImageFilter();
        r10 = r60.getThumbFilter();
        if (r29 == 0) goto L_0x042a;
    L_0x040c:
        if (r33 == 0) goto L_0x042a;
    L_0x040e:
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r0 = r29;
        r4 = r4.append(r0);
        r5 = "@";
        r4 = r4.append(r5);
        r0 = r33;
        r4 = r4.append(r0);
        r29 = r4.toString();
    L_0x042a:
        if (r44 == 0) goto L_0x044a;
    L_0x042c:
        if (r17 == 0) goto L_0x044a;
    L_0x042e:
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r0 = r44;
        r4 = r4.append(r0);
        r5 = "@";
        r4 = r4.append(r5);
        r0 = r17;
        r4 = r4.append(r0);
        r44 = r4.toString();
    L_0x044a:
        if (r6 == 0) goto L_0x0466;
    L_0x044c:
        if (r10 == 0) goto L_0x0466;
    L_0x044e:
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r4 = r4.append(r6);
        r5 = "@";
        r4 = r4.append(r5);
        r4 = r4.append(r10);
        r6 = r4.toString();
    L_0x0466:
        r0 = r16;
        r4 = r0 instanceof java.lang.String;
        if (r4 == 0) goto L_0x0556;
    L_0x046c:
        r11 = 0;
        r12 = 1;
        r13 = 1;
        if (r57 == 0) goto L_0x0553;
    L_0x0471:
        r14 = 2;
    L_0x0472:
        r4 = r59;
        r5 = r60;
        r4.createLoadOperationForImageReceiver(r5, r6, r7, r8, r9, r10, r11, r12, r13, r14);
        r18 = r60.getSize();
        r19 = 1;
        r20 = 0;
        r21 = 0;
        r11 = r59;
        r12 = r60;
        r13 = r44;
        r14 = r47;
        r15 = r8;
        r11.createLoadOperationForImageReceiver(r12, r13, r14, r15, r16, r17, r18, r19, r20, r21);
        goto L_0x0002;
    L_0x0491:
        r4 = r9 instanceof org.telegram.tgnet.TLRPC.FileLocation;
        if (r4 == 0) goto L_0x04d3;
    L_0x0495:
        r49 = r9;
        r49 = (org.telegram.tgnet.TLRPC.FileLocation) r49;
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r0 = r49;
        r14 = r0.volume_id;
        r4 = r4.append(r14);
        r5 = "_";
        r4 = r4.append(r5);
        r0 = r49;
        r5 = r0.local_id;
        r4 = r4.append(r5);
        r6 = r4.toString();
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r4 = r4.append(r6);
        r5 = ".";
        r4 = r4.append(r5);
        r4 = r4.append(r8);
        r7 = r4.toString();
        goto L_0x03fe;
    L_0x04d3:
        r4 = r9 instanceof org.telegram.tgnet.TLRPC.TL_photoStrippedSize;
        if (r4 == 0) goto L_0x050d;
    L_0x04d7:
        r49 = r9;
        r49 = (org.telegram.tgnet.TLRPC.TL_photoStrippedSize) r49;
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r5 = "stripped";
        r4 = r4.append(r5);
        r5 = org.telegram.messenger.FileRefController.getKeyForParentObject(r52);
        r4 = r4.append(r5);
        r6 = r4.toString();
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r4 = r4.append(r6);
        r5 = ".";
        r4 = r4.append(r5);
        r4 = r4.append(r8);
        r7 = r4.toString();
        goto L_0x03fe;
    L_0x050d:
        r4 = r9 instanceof org.telegram.tgnet.TLRPC.TL_photoSize;
        if (r4 == 0) goto L_0x03fe;
    L_0x0511:
        r53 = r9;
        r53 = (org.telegram.tgnet.TLRPC.TL_photoSize) r53;
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r0 = r53;
        r5 = r0.location;
        r14 = r5.volume_id;
        r4 = r4.append(r14);
        r5 = "_";
        r4 = r4.append(r5);
        r0 = r53;
        r5 = r0.location;
        r5 = r5.local_id;
        r4 = r4.append(r5);
        r6 = r4.toString();
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r4 = r4.append(r6);
        r5 = ".";
        r4 = r4.append(r5);
        r4 = r4.append(r8);
        r7 = r4.toString();
        goto L_0x03fe;
    L_0x0553:
        r14 = 1;
        goto L_0x0472;
    L_0x0556:
        if (r32 == 0) goto L_0x05a6;
    L_0x0558:
        r35 = r60.getCacheType();
        r26 = 1;
        if (r35 != 0) goto L_0x0564;
    L_0x0560:
        if (r56 == 0) goto L_0x0564;
    L_0x0562:
        r35 = 1;
    L_0x0564:
        if (r35 != 0) goto L_0x05a1;
    L_0x0566:
        r12 = 1;
    L_0x0567:
        if (r57 != 0) goto L_0x0575;
    L_0x0569:
        r11 = 0;
        r13 = 1;
        if (r57 == 0) goto L_0x05a4;
    L_0x056d:
        r14 = 2;
    L_0x056e:
        r4 = r59;
        r5 = r60;
        r4.createLoadOperationForImageReceiver(r5, r6, r7, r8, r9, r10, r11, r12, r13, r14);
    L_0x0575:
        if (r46 != 0) goto L_0x058e;
    L_0x0577:
        r25 = 0;
        r27 = 0;
        r28 = 0;
        r18 = r59;
        r19 = r60;
        r20 = r44;
        r21 = r47;
        r22 = r8;
        r23 = r16;
        r24 = r17;
        r18.createLoadOperationForImageReceiver(r19, r20, r21, r22, r23, r24, r25, r26, r27, r28);
    L_0x058e:
        r34 = r60.getSize();
        r36 = 3;
        r37 = 0;
        r27 = r59;
        r28 = r60;
        r31 = r8;
        r27.createLoadOperationForImageReceiver(r28, r29, r30, r31, r32, r33, r34, r35, r36, r37);
        goto L_0x0002;
    L_0x05a1:
        r12 = r35;
        goto L_0x0567;
    L_0x05a4:
        r14 = 1;
        goto L_0x056e;
    L_0x05a6:
        r26 = r60.getCacheType();
        if (r26 != 0) goto L_0x05b0;
    L_0x05ac:
        if (r56 == 0) goto L_0x05b0;
    L_0x05ae:
        r26 = 1;
    L_0x05b0:
        if (r26 != 0) goto L_0x05da;
    L_0x05b2:
        r12 = 1;
    L_0x05b3:
        r11 = 0;
        r13 = 1;
        if (r57 == 0) goto L_0x05dd;
    L_0x05b7:
        r14 = 2;
    L_0x05b8:
        r4 = r59;
        r5 = r60;
        r4.createLoadOperationForImageReceiver(r5, r6, r7, r8, r9, r10, r11, r12, r13, r14);
        r25 = r60.getSize();
        r27 = 0;
        r28 = 0;
        r18 = r59;
        r19 = r60;
        r20 = r44;
        r21 = r47;
        r22 = r8;
        r23 = r16;
        r24 = r17;
        r18.createLoadOperationForImageReceiver(r19, r20, r21, r22, r23, r24, r25, r26, r27, r28);
        goto L_0x0002;
    L_0x05da:
        r12 = r26;
        goto L_0x05b3;
    L_0x05dd:
        r14 = 1;
        goto L_0x05b8;
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
            img.artworkTask = new ArtworkLoadTask(img.artworkTask.cacheImage);
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
                int imageType = ((Integer) img.thumbs.get(a)).intValue();
                ImageReceiver imageReceiver = (ImageReceiver) img.imageReceiverArray.get(a);
                CacheImage cacheImage = (CacheImage) this.imageLoadingByKeys.get(key);
                if (cacheImage == null) {
                    cacheImage = new CacheImage(this, null);
                    cacheImage.secureDocument = img.secureDocument;
                    cacheImage.currentAccount = img.currentAccount;
                    cacheImage.finalFilePath = finalFile;
                    cacheImage.key = key;
                    cacheImage.location = img.location;
                    cacheImage.imageType = imageType;
                    cacheImage.ext = img.ext;
                    cacheImage.encryptionKeyPath = img.encryptionKeyPath;
                    cacheImage.cacheTask = new CacheOutTask(cacheImage);
                    cacheImage.filter = filter;
                    cacheImage.animatedFile = img.animatedFile;
                    this.imageLoadingByKeys.put(key, cacheImage);
                    tasks.add(cacheImage.cacheTask);
                }
                cacheImage.addImageReceiver(imageReceiver, key, filter, imageType);
            }
            for (a = 0; a < tasks.size(); a++) {
                CacheOutTask task = (CacheOutTask) tasks.get(a);
                if (task.cacheImage.imageType == 1) {
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
