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
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import org.telegram.messenger.FileLoader.FileLoaderDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.InputEncryptedFile;
import org.telegram.tgnet.TLRPC.InputFile;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.MessageMedia;
import org.telegram.tgnet.TLRPC.Photo;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.TL_documentAttributeVideo;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_fileLocationToBeDeprecated;
import org.telegram.tgnet.TLRPC.TL_fileLocationUnavailable;
import org.telegram.tgnet.TLRPC.TL_messageMediaDocument;
import org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;
import org.telegram.tgnet.TLRPC.TL_messageMediaWebPage;
import org.telegram.tgnet.TLRPC.TL_photoCachedSize;
import org.telegram.tgnet.TLRPC.TL_photoSize;
import org.telegram.tgnet.TLRPC.TL_photoStrippedSize;
import org.telegram.ui.Cells.ChatMessageCell;
import org.telegram.ui.Components.AnimatedFileDrawable;
import org.telegram.ui.Components.Point;
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
        /* JADX WARNING: Removed duplicated region for block: B:87:0x010e A:{Catch:{ all -> 0x0150, all -> 0x015b, all -> 0x0162 }} */
        /* JADX WARNING: Removed duplicated region for block: B:84:0x0105 A:{Catch:{ all -> 0x0150, all -> 0x015b, all -> 0x0162 }} */
        /* JADX WARNING: Removed duplicated region for block: B:104:0x013b A:{Catch:{ all -> 0x0141 }} */
        /* JADX WARNING: Removed duplicated region for block: B:107:0x0144 A:{SYNTHETIC, Splitter:B:107:0x0144} */
        /* JADX WARNING: Removed duplicated region for block: B:84:0x0105 A:{Catch:{ all -> 0x0150, all -> 0x015b, all -> 0x0162 }} */
        /* JADX WARNING: Removed duplicated region for block: B:87:0x010e A:{Catch:{ all -> 0x0150, all -> 0x015b, all -> 0x0162 }} */
        /* JADX WARNING: Removed duplicated region for block: B:104:0x013b A:{Catch:{ all -> 0x0141 }} */
        /* JADX WARNING: Removed duplicated region for block: B:107:0x0144 A:{SYNTHETIC, Splitter:B:107:0x0144} */
        /* JADX WARNING: Removed duplicated region for block: B:126:0x0168 A:{SYNTHETIC, Splitter:B:126:0x0168} */
        /* JADX WARNING: Missing block: B:111:0x014c, code skipped:
            if (r2 == null) goto L_0x014f;
     */
        public java.lang.String doInBackground(java.lang.Void... r8) {
            /*
            r7 = this;
            r8 = 0;
            r0 = 0;
            r1 = r7.cacheImage;	 Catch:{ all -> 0x00fe }
            r1 = r1.imageLocation;	 Catch:{ all -> 0x00fe }
            r1 = r1.path;	 Catch:{ all -> 0x00fe }
            r2 = new java.net.URL;	 Catch:{ all -> 0x00fe }
            r3 = "athumb://";
            r4 = "https://";
            r1 = r1.replace(r3, r4);	 Catch:{ all -> 0x00fe }
            r2.<init>(r1);	 Catch:{ all -> 0x00fe }
            r1 = r2.openConnection();	 Catch:{ all -> 0x00fe }
            r1 = (java.net.HttpURLConnection) r1;	 Catch:{ all -> 0x00fe }
            r7.httpConnection = r1;	 Catch:{ all -> 0x00fe }
            r1 = r7.httpConnection;	 Catch:{ all -> 0x00fe }
            r2 = "User-Agent";
            r3 = "Mozilla/5.0 (iPhone; CPU iPhone OS 10_0 like Mac OS X) AppleWebKit/602.1.38 (KHTML, like Gecko) Version/10.0 Mobile/14A5297c Safari/602.1";
            r1.addRequestProperty(r2, r3);	 Catch:{ all -> 0x00fe }
            r1 = r7.httpConnection;	 Catch:{ all -> 0x00fe }
            r2 = 5000; // 0x1388 float:7.006E-42 double:2.4703E-320;
            r1.setConnectTimeout(r2);	 Catch:{ all -> 0x00fe }
            r1 = r7.httpConnection;	 Catch:{ all -> 0x00fe }
            r1.setReadTimeout(r2);	 Catch:{ all -> 0x00fe }
            r1 = r7.httpConnection;	 Catch:{ all -> 0x00fe }
            r1.connect();	 Catch:{ all -> 0x00fe }
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
            org.telegram.messenger.FileLog.e(r1);	 Catch:{ all -> 0x00fe }
        L_0x0054:
            r1 = r7.httpConnection;	 Catch:{ all -> 0x00fe }
            r1 = r1.getInputStream();	 Catch:{ all -> 0x00fe }
            r2 = new java.io.ByteArrayOutputStream;	 Catch:{ all -> 0x00f9 }
            r2.<init>();	 Catch:{ all -> 0x00f9 }
            r3 = 32768; // 0x8000 float:4.5918E-41 double:1.61895E-319;
            r3 = new byte[r3];	 Catch:{ all -> 0x00f4 }
        L_0x0064:
            r4 = r7.isCancelled();	 Catch:{ all -> 0x00f4 }
            if (r4 == 0) goto L_0x006b;
        L_0x006a:
            goto L_0x0076;
        L_0x006b:
            r4 = r1.read(r3);	 Catch:{ all -> 0x00f4 }
            if (r4 <= 0) goto L_0x0075;
        L_0x0071:
            r2.write(r3, r0, r4);	 Catch:{ all -> 0x00f4 }
            goto L_0x0064;
        L_0x0075:
            r3 = -1;
        L_0x0076:
            r7.canRetry = r0;	 Catch:{ all -> 0x00f4 }
            r3 = new org.json.JSONObject;	 Catch:{ all -> 0x00f4 }
            r4 = new java.lang.String;	 Catch:{ all -> 0x00f4 }
            r5 = r2.toByteArray();	 Catch:{ all -> 0x00f4 }
            r4.<init>(r5);	 Catch:{ all -> 0x00f4 }
            r3.<init>(r4);	 Catch:{ all -> 0x00f4 }
            r4 = "results";
            r3 = r3.getJSONArray(r4);	 Catch:{ all -> 0x00f4 }
            r4 = r3.length();	 Catch:{ all -> 0x00f4 }
            if (r4 <= 0) goto L_0x00da;
        L_0x0092:
            r3 = r3.getJSONObject(r0);	 Catch:{ all -> 0x00f4 }
            r4 = "artworkUrl100";
            r3 = r3.getString(r4);	 Catch:{ all -> 0x00f4 }
            r4 = r7.small;	 Catch:{ all -> 0x00f4 }
            if (r4 == 0) goto L_0x00b9;
        L_0x00a0:
            r8 = r7.httpConnection;	 Catch:{ all -> 0x00aa }
            if (r8 == 0) goto L_0x00ab;
        L_0x00a4:
            r8 = r7.httpConnection;	 Catch:{ all -> 0x00aa }
            r8.disconnect();	 Catch:{ all -> 0x00aa }
            goto L_0x00ab;
        L_0x00ab:
            if (r1 == 0) goto L_0x00b5;
        L_0x00ad:
            r1.close();	 Catch:{ all -> 0x00b1 }
            goto L_0x00b5;
        L_0x00b1:
            r8 = move-exception;
            org.telegram.messenger.FileLog.e(r8);
        L_0x00b5:
            r2.close();	 Catch:{ Exception -> 0x00b8 }
        L_0x00b8:
            return r3;
        L_0x00b9:
            r4 = "100x100";
            r5 = "600x600";
            r8 = r3.replace(r4, r5);	 Catch:{ all -> 0x00f4 }
            r0 = r7.httpConnection;	 Catch:{ all -> 0x00cb }
            if (r0 == 0) goto L_0x00cc;
        L_0x00c5:
            r0 = r7.httpConnection;	 Catch:{ all -> 0x00cb }
            r0.disconnect();	 Catch:{ all -> 0x00cb }
            goto L_0x00cc;
        L_0x00cc:
            if (r1 == 0) goto L_0x00d6;
        L_0x00ce:
            r1.close();	 Catch:{ all -> 0x00d2 }
            goto L_0x00d6;
        L_0x00d2:
            r0 = move-exception;
            org.telegram.messenger.FileLog.e(r0);
        L_0x00d6:
            r2.close();	 Catch:{ Exception -> 0x00d9 }
        L_0x00d9:
            return r8;
        L_0x00da:
            r0 = r7.httpConnection;	 Catch:{ all -> 0x00e4 }
            if (r0 == 0) goto L_0x00e5;
        L_0x00de:
            r0 = r7.httpConnection;	 Catch:{ all -> 0x00e4 }
            r0.disconnect();	 Catch:{ all -> 0x00e4 }
            goto L_0x00e5;
        L_0x00e5:
            if (r1 == 0) goto L_0x00ef;
        L_0x00e7:
            r1.close();	 Catch:{ all -> 0x00eb }
            goto L_0x00ef;
        L_0x00eb:
            r0 = move-exception;
            org.telegram.messenger.FileLog.e(r0);
        L_0x00ef:
            r2.close();	 Catch:{ Exception -> 0x014f }
            goto L_0x014f;
        L_0x00f4:
            r3 = move-exception;
            r6 = r3;
            r3 = r1;
            r1 = r6;
            goto L_0x0101;
        L_0x00f9:
            r2 = move-exception;
            r3 = r1;
            r1 = r2;
            r2 = r8;
            goto L_0x0101;
        L_0x00fe:
            r1 = move-exception;
            r2 = r8;
            r3 = r2;
        L_0x0101:
            r4 = r1 instanceof java.net.SocketTimeoutException;	 Catch:{ all -> 0x0150 }
            if (r4 == 0) goto L_0x010e;
        L_0x0105:
            r4 = org.telegram.messenger.ApplicationLoader.isNetworkOnline();	 Catch:{ all -> 0x0150 }
            if (r4 == 0) goto L_0x0134;
        L_0x010b:
            r7.canRetry = r0;	 Catch:{ all -> 0x0150 }
            goto L_0x0134;
        L_0x010e:
            r4 = r1 instanceof java.net.UnknownHostException;	 Catch:{ all -> 0x0150 }
            if (r4 == 0) goto L_0x0115;
        L_0x0112:
            r7.canRetry = r0;	 Catch:{ all -> 0x0150 }
            goto L_0x0134;
        L_0x0115:
            r4 = r1 instanceof java.net.SocketException;	 Catch:{ all -> 0x0150 }
            if (r4 == 0) goto L_0x012e;
        L_0x0119:
            r4 = r1.getMessage();	 Catch:{ all -> 0x0150 }
            if (r4 == 0) goto L_0x0134;
        L_0x011f:
            r4 = r1.getMessage();	 Catch:{ all -> 0x0150 }
            r5 = "ECONNRESET";
            r4 = r4.contains(r5);	 Catch:{ all -> 0x0150 }
            if (r4 == 0) goto L_0x0134;
        L_0x012b:
            r7.canRetry = r0;	 Catch:{ all -> 0x0150 }
            goto L_0x0134;
        L_0x012e:
            r4 = r1 instanceof java.io.FileNotFoundException;	 Catch:{ all -> 0x0150 }
            if (r4 == 0) goto L_0x0134;
        L_0x0132:
            r7.canRetry = r0;	 Catch:{ all -> 0x0150 }
        L_0x0134:
            org.telegram.messenger.FileLog.e(r1);	 Catch:{ all -> 0x0150 }
            r0 = r7.httpConnection;	 Catch:{ all -> 0x0141 }
            if (r0 == 0) goto L_0x0142;
        L_0x013b:
            r0 = r7.httpConnection;	 Catch:{ all -> 0x0141 }
            r0.disconnect();	 Catch:{ all -> 0x0141 }
            goto L_0x0142;
        L_0x0142:
            if (r3 == 0) goto L_0x014c;
        L_0x0144:
            r3.close();	 Catch:{ all -> 0x0148 }
            goto L_0x014c;
        L_0x0148:
            r0 = move-exception;
            org.telegram.messenger.FileLog.e(r0);
        L_0x014c:
            if (r2 == 0) goto L_0x014f;
        L_0x014e:
            goto L_0x00ef;
        L_0x014f:
            return r8;
        L_0x0150:
            r8 = move-exception;
            r0 = r7.httpConnection;	 Catch:{ all -> 0x015b }
            if (r0 == 0) goto L_0x015c;
        L_0x0155:
            r0 = r7.httpConnection;	 Catch:{ all -> 0x015b }
            r0.disconnect();	 Catch:{ all -> 0x015b }
            goto L_0x015c;
        L_0x015c:
            if (r3 == 0) goto L_0x0166;
        L_0x015e:
            r3.close();	 Catch:{ all -> 0x0162 }
            goto L_0x0166;
        L_0x0162:
            r0 = move-exception;
            org.telegram.messenger.FileLog.e(r0);
        L_0x0166:
            if (r2 == 0) goto L_0x016b;
        L_0x0168:
            r2.close();	 Catch:{ Exception -> 0x016b }
        L_0x016b:
            goto L_0x016d;
        L_0x016c:
            throw r8;
        L_0x016d:
            goto L_0x016c;
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
            int indexOf = this.imageReceiverArray.indexOf(imageReceiver);
            if (indexOf >= 0) {
                this.imageReceiverGuidsArray.set(indexOf, Integer.valueOf(i2));
                return;
            }
            this.imageReceiverArray.add(imageReceiver);
            this.imageReceiverGuidsArray.add(Integer.valueOf(i2));
            this.keys.add(str);
            this.filters.add(str2);
            this.imageTypes.add(Integer.valueOf(i));
            ImageLoader.this.imageLoadingByTag.put(imageReceiver.getTag(i), this);
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
            if (this.imageReceiverArray.isEmpty()) {
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

        /* JADX WARNING: Removed duplicated region for block: B:491:0x0737 A:{SYNTHETIC, Splitter:B:491:0x0737} */
        /* JADX WARNING: Removed duplicated region for block: B:709:0x09b4  */
        /* JADX WARNING: Removed duplicated region for block: B:708:0x09ae  */
        /* JADX WARNING: Removed duplicated region for block: B:702:0x099c A:{SKIP} */
        /* JADX WARNING: Removed duplicated region for block: B:708:0x09ae  */
        /* JADX WARNING: Removed duplicated region for block: B:709:0x09b4  */
        /* JADX WARNING: Removed duplicated region for block: B:702:0x099c A:{SKIP} */
        /* JADX WARNING: Removed duplicated region for block: B:709:0x09b4  */
        /* JADX WARNING: Removed duplicated region for block: B:708:0x09ae  */
        /* JADX WARNING: Removed duplicated region for block: B:467:0x06ee  */
        /* JADX WARNING: Removed duplicated region for block: B:338:0x04f5 A:{SYNTHETIC, Splitter:B:338:0x04f5} */
        /* JADX WARNING: Removed duplicated region for block: B:702:0x099c A:{SKIP} */
        /* JADX WARNING: Removed duplicated region for block: B:708:0x09ae  */
        /* JADX WARNING: Removed duplicated region for block: B:709:0x09b4  */
        /* JADX WARNING: Removed duplicated region for block: B:338:0x04f5 A:{SYNTHETIC, Splitter:B:338:0x04f5} */
        /* JADX WARNING: Removed duplicated region for block: B:467:0x06ee  */
        /* JADX WARNING: Removed duplicated region for block: B:702:0x099c A:{SKIP} */
        /* JADX WARNING: Removed duplicated region for block: B:709:0x09b4  */
        /* JADX WARNING: Removed duplicated region for block: B:708:0x09ae  */
        /* JADX WARNING: Removed duplicated region for block: B:467:0x06ee  */
        /* JADX WARNING: Removed duplicated region for block: B:338:0x04f5 A:{SYNTHETIC, Splitter:B:338:0x04f5} */
        /* JADX WARNING: Removed duplicated region for block: B:702:0x099c A:{SKIP} */
        /* JADX WARNING: Removed duplicated region for block: B:708:0x09ae  */
        /* JADX WARNING: Removed duplicated region for block: B:709:0x09b4  */
        /* JADX WARNING: Removed duplicated region for block: B:195:0x02e1  */
        /* JADX WARNING: Removed duplicated region for block: B:303:0x0485  */
        /* JADX WARNING: Removed duplicated region for block: B:200:0x02f3 A:{Catch:{ all -> 0x04dd }} */
        /* JADX WARNING: Removed duplicated region for block: B:338:0x04f5 A:{SYNTHETIC, Splitter:B:338:0x04f5} */
        /* JADX WARNING: Removed duplicated region for block: B:467:0x06ee  */
        /* JADX WARNING: Removed duplicated region for block: B:702:0x099c A:{SKIP} */
        /* JADX WARNING: Removed duplicated region for block: B:709:0x09b4  */
        /* JADX WARNING: Removed duplicated region for block: B:708:0x09ae  */
        /* JADX WARNING: Removed duplicated region for block: B:195:0x02e1  */
        /* JADX WARNING: Removed duplicated region for block: B:200:0x02f3 A:{Catch:{ all -> 0x04dd }} */
        /* JADX WARNING: Removed duplicated region for block: B:303:0x0485  */
        /* JADX WARNING: Removed duplicated region for block: B:467:0x06ee  */
        /* JADX WARNING: Removed duplicated region for block: B:338:0x04f5 A:{SYNTHETIC, Splitter:B:338:0x04f5} */
        /* JADX WARNING: Removed duplicated region for block: B:702:0x099c A:{SKIP} */
        /* JADX WARNING: Removed duplicated region for block: B:708:0x09ae  */
        /* JADX WARNING: Removed duplicated region for block: B:709:0x09b4  */
        /* JADX WARNING: Removed duplicated region for block: B:160:0x0257 A:{SYNTHETIC, Splitter:B:160:0x0257} */
        /* JADX WARNING: Removed duplicated region for block: B:173:0x0272  */
        /* JADX WARNING: Removed duplicated region for block: B:195:0x02e1  */
        /* JADX WARNING: Removed duplicated region for block: B:303:0x0485  */
        /* JADX WARNING: Removed duplicated region for block: B:200:0x02f3 A:{Catch:{ all -> 0x04dd }} */
        /* JADX WARNING: Removed duplicated region for block: B:338:0x04f5 A:{SYNTHETIC, Splitter:B:338:0x04f5} */
        /* JADX WARNING: Removed duplicated region for block: B:467:0x06ee  */
        /* JADX WARNING: Removed duplicated region for block: B:702:0x099c A:{SKIP} */
        /* JADX WARNING: Removed duplicated region for block: B:709:0x09b4  */
        /* JADX WARNING: Removed duplicated region for block: B:708:0x09ae  */
        /* JADX WARNING: Removed duplicated region for block: B:160:0x0257 A:{SYNTHETIC, Splitter:B:160:0x0257} */
        /* JADX WARNING: Removed duplicated region for block: B:173:0x0272  */
        /* JADX WARNING: Removed duplicated region for block: B:195:0x02e1  */
        /* JADX WARNING: Removed duplicated region for block: B:200:0x02f3 A:{Catch:{ all -> 0x04dd }} */
        /* JADX WARNING: Removed duplicated region for block: B:303:0x0485  */
        /* JADX WARNING: Removed duplicated region for block: B:467:0x06ee  */
        /* JADX WARNING: Removed duplicated region for block: B:338:0x04f5 A:{SYNTHETIC, Splitter:B:338:0x04f5} */
        /* JADX WARNING: Removed duplicated region for block: B:702:0x099c A:{SKIP} */
        /* JADX WARNING: Removed duplicated region for block: B:708:0x09ae  */
        /* JADX WARNING: Removed duplicated region for block: B:709:0x09b4  */
        /* JADX WARNING: Removed duplicated region for block: B:702:0x099c A:{SKIP} */
        /* JADX WARNING: Removed duplicated region for block: B:709:0x09b4  */
        /* JADX WARNING: Removed duplicated region for block: B:708:0x09ae  */
        /* JADX WARNING: Removed duplicated region for block: B:338:0x04f5 A:{SYNTHETIC, Splitter:B:338:0x04f5} */
        /* JADX WARNING: Removed duplicated region for block: B:467:0x06ee  */
        /* JADX WARNING: Removed duplicated region for block: B:702:0x099c A:{SKIP} */
        /* JADX WARNING: Removed duplicated region for block: B:708:0x09ae  */
        /* JADX WARNING: Removed duplicated region for block: B:709:0x09b4  */
        /* JADX WARNING: Removed duplicated region for block: B:702:0x099c A:{SKIP} */
        /* JADX WARNING: Removed duplicated region for block: B:709:0x09b4  */
        /* JADX WARNING: Removed duplicated region for block: B:708:0x09ae  */
        /* JADX WARNING: Removed duplicated region for block: B:702:0x099c A:{SKIP} */
        /* JADX WARNING: Removed duplicated region for block: B:708:0x09ae  */
        /* JADX WARNING: Removed duplicated region for block: B:709:0x09b4  */
        /* JADX WARNING: Removed duplicated region for block: B:467:0x06ee  */
        /* JADX WARNING: Removed duplicated region for block: B:338:0x04f5 A:{SYNTHETIC, Splitter:B:338:0x04f5} */
        /* JADX WARNING: Removed duplicated region for block: B:702:0x099c A:{SKIP} */
        /* JADX WARNING: Removed duplicated region for block: B:709:0x09b4  */
        /* JADX WARNING: Removed duplicated region for block: B:708:0x09ae  */
        /* JADX WARNING: Removed duplicated region for block: B:702:0x099c A:{SKIP} */
        /* JADX WARNING: Removed duplicated region for block: B:708:0x09ae  */
        /* JADX WARNING: Removed duplicated region for block: B:709:0x09b4  */
        /* JADX WARNING: Removed duplicated region for block: B:702:0x099c A:{SKIP} */
        /* JADX WARNING: Removed duplicated region for block: B:709:0x09b4  */
        /* JADX WARNING: Removed duplicated region for block: B:708:0x09ae  */
        /* JADX WARNING: Removed duplicated region for block: B:338:0x04f5 A:{SYNTHETIC, Splitter:B:338:0x04f5} */
        /* JADX WARNING: Removed duplicated region for block: B:467:0x06ee  */
        /* JADX WARNING: Removed duplicated region for block: B:702:0x099c A:{SKIP} */
        /* JADX WARNING: Removed duplicated region for block: B:708:0x09ae  */
        /* JADX WARNING: Removed duplicated region for block: B:709:0x09b4  */
        /* JADX WARNING: Removed duplicated region for block: B:467:0x06ee  */
        /* JADX WARNING: Removed duplicated region for block: B:338:0x04f5 A:{SYNTHETIC, Splitter:B:338:0x04f5} */
        /* JADX WARNING: Removed duplicated region for block: B:702:0x099c A:{SKIP} */
        /* JADX WARNING: Removed duplicated region for block: B:709:0x09b4  */
        /* JADX WARNING: Removed duplicated region for block: B:708:0x09ae  */
        /* JADX WARNING: Removed duplicated region for block: B:702:0x099c A:{SKIP} */
        /* JADX WARNING: Removed duplicated region for block: B:708:0x09ae  */
        /* JADX WARNING: Removed duplicated region for block: B:709:0x09b4  */
        /* JADX WARNING: Removed duplicated region for block: B:338:0x04f5 A:{SYNTHETIC, Splitter:B:338:0x04f5} */
        /* JADX WARNING: Removed duplicated region for block: B:467:0x06ee  */
        /* JADX WARNING: Removed duplicated region for block: B:702:0x099c A:{SKIP} */
        /* JADX WARNING: Removed duplicated region for block: B:709:0x09b4  */
        /* JADX WARNING: Removed duplicated region for block: B:708:0x09ae  */
        /* JADX WARNING: Removed duplicated region for block: B:702:0x099c A:{SKIP} */
        /* JADX WARNING: Removed duplicated region for block: B:708:0x09ae  */
        /* JADX WARNING: Removed duplicated region for block: B:709:0x09b4  */
        /* JADX WARNING: Removed duplicated region for block: B:467:0x06ee  */
        /* JADX WARNING: Removed duplicated region for block: B:338:0x04f5 A:{SYNTHETIC, Splitter:B:338:0x04f5} */
        /* JADX WARNING: Removed duplicated region for block: B:702:0x099c A:{SKIP} */
        /* JADX WARNING: Removed duplicated region for block: B:709:0x09b4  */
        /* JADX WARNING: Removed duplicated region for block: B:708:0x09ae  */
        /* JADX WARNING: Removed duplicated region for block: B:702:0x099c A:{SKIP} */
        /* JADX WARNING: Removed duplicated region for block: B:708:0x09ae  */
        /* JADX WARNING: Removed duplicated region for block: B:709:0x09b4  */
        /* JADX WARNING: Removed duplicated region for block: B:165:0x025f A:{SYNTHETIC, Splitter:B:165:0x025f} */
        /* JADX WARNING: Removed duplicated region for block: B:702:0x099c A:{SKIP} */
        /* JADX WARNING: Removed duplicated region for block: B:709:0x09b4  */
        /* JADX WARNING: Removed duplicated region for block: B:708:0x09ae  */
        /* JADX WARNING: Removed duplicated region for block: B:338:0x04f5 A:{SYNTHETIC, Splitter:B:338:0x04f5} */
        /* JADX WARNING: Removed duplicated region for block: B:467:0x06ee  */
        /* JADX WARNING: Removed duplicated region for block: B:702:0x099c A:{SKIP} */
        /* JADX WARNING: Removed duplicated region for block: B:708:0x09ae  */
        /* JADX WARNING: Removed duplicated region for block: B:709:0x09b4  */
        /* JADX WARNING: Removed duplicated region for block: B:702:0x099c A:{SKIP} */
        /* JADX WARNING: Removed duplicated region for block: B:709:0x09b4  */
        /* JADX WARNING: Removed duplicated region for block: B:708:0x09ae  */
        /* JADX WARNING: Removed duplicated region for block: B:702:0x099c A:{SKIP} */
        /* JADX WARNING: Removed duplicated region for block: B:708:0x09ae  */
        /* JADX WARNING: Removed duplicated region for block: B:709:0x09b4  */
        /* JADX WARNING: Removed duplicated region for block: B:702:0x099c A:{SKIP} */
        /* JADX WARNING: Removed duplicated region for block: B:709:0x09b4  */
        /* JADX WARNING: Removed duplicated region for block: B:708:0x09ae  */
        /* JADX WARNING: Removed duplicated region for block: B:678:0x097d A:{SYNTHETIC, Splitter:B:678:0x097d} */
        /* JADX WARNING: Removed duplicated region for block: B:702:0x099c A:{SKIP} */
        /* JADX WARNING: Removed duplicated region for block: B:708:0x09ae  */
        /* JADX WARNING: Removed duplicated region for block: B:709:0x09b4  */
        /* JADX WARNING: Removed duplicated region for block: B:674:0x0976  */
        /* JADX WARNING: Removed duplicated region for block: B:633:0x08fd  */
        /* JADX WARNING: Removed duplicated region for block: B:678:0x097d A:{SYNTHETIC, Splitter:B:678:0x097d} */
        /* JADX WARNING: Removed duplicated region for block: B:702:0x099c A:{SKIP} */
        /* JADX WARNING: Removed duplicated region for block: B:709:0x09b4  */
        /* JADX WARNING: Removed duplicated region for block: B:708:0x09ae  */
        /* JADX WARNING: Missing block: B:8:0x0015, code skipped:
            r0 = r1.cacheImage;
     */
        /* JADX WARNING: Missing block: B:9:0x001d, code skipped:
            if ((r0.imageLocation.photoSize instanceof org.telegram.tgnet.TLRPC.TL_photoStrippedSize) == false) goto L_0x004a;
     */
        /* JADX WARNING: Missing block: B:10:0x001f, code skipped:
            r2 = r1.sync;
     */
        /* JADX WARNING: Missing block: B:11:0x0021, code skipped:
            monitor-enter(r2);
     */
        /* JADX WARNING: Missing block: B:14:0x0024, code skipped:
            if (r1.isCancelled == false) goto L_0x0028;
     */
        /* JADX WARNING: Missing block: B:15:0x0026, code skipped:
            monitor-exit(r2);
     */
        /* JADX WARNING: Missing block: B:16:0x0027, code skipped:
            return;
     */
        /* JADX WARNING: Missing block: B:17:0x0028, code skipped:
            monitor-exit(r2);
     */
        /* JADX WARNING: Missing block: B:18:0x0029, code skipped:
            r0 = r1.cacheImage;
            r0 = org.telegram.messenger.ImageLoader.access$1700(((org.telegram.tgnet.TLRPC.TL_photoStrippedSize) r0.imageLocation.photoSize).bytes, r0.filter);
     */
        /* JADX WARNING: Missing block: B:19:0x0039, code skipped:
            if (r0 == null) goto L_0x0041;
     */
        /* JADX WARNING: Missing block: B:20:0x003b, code skipped:
            r3 = new android.graphics.drawable.BitmapDrawable(r0);
     */
        /* JADX WARNING: Missing block: B:21:0x0041, code skipped:
            r3 = null;
     */
        /* JADX WARNING: Missing block: B:22:0x0042, code skipped:
            onPostExecute(r3);
     */
        /* JADX WARNING: Missing block: B:27:0x004a, code skipped:
            r7 = 1;
            r8 = false;
     */
        /* JADX WARNING: Missing block: B:28:0x0052, code skipped:
            if (r0.lottieFile == false) goto L_0x0157;
     */
        /* JADX WARNING: Missing block: B:29:0x0054, code skipped:
            r2 = r1.sync;
     */
        /* JADX WARNING: Missing block: B:30:0x0056, code skipped:
            monitor-enter(r2);
     */
        /* JADX WARNING: Missing block: B:33:0x0059, code skipped:
            if (r1.isCancelled == false) goto L_0x005d;
     */
        /* JADX WARNING: Missing block: B:34:0x005b, code skipped:
            monitor-exit(r2);
     */
        /* JADX WARNING: Missing block: B:35:0x005c, code skipped:
            return;
     */
        /* JADX WARNING: Missing block: B:36:0x005d, code skipped:
            monitor-exit(r2);
     */
        /* JADX WARNING: Missing block: B:37:0x005e, code skipped:
            r2 = java.lang.Math.min(512, org.telegram.messenger.AndroidUtilities.dp(170.6f));
            r0 = java.lang.Math.min(512, org.telegram.messenger.AndroidUtilities.dp(170.6f));
            r10 = r1.cacheImage.filter;
     */
        /* JADX WARNING: Missing block: B:38:0x0077, code skipped:
            if (r10 == null) goto L_0x013c;
     */
        /* JADX WARNING: Missing block: B:39:0x0079, code skipped:
            r10 = r10.split("_");
     */
        /* JADX WARNING: Missing block: B:40:0x0080, code skipped:
            if (r10.length < 2) goto L_0x00c0;
     */
        /* JADX WARNING: Missing block: B:41:0x0082, code skipped:
            r0 = java.lang.Float.parseFloat(r10[0]);
            r2 = java.lang.Float.parseFloat(r10[1]);
            r11 = java.lang.Math.min(512, (int) (org.telegram.messenger.AndroidUtilities.density * r0));
            r9 = java.lang.Math.min(512, (int) (org.telegram.messenger.AndroidUtilities.density * r2));
     */
        /* JADX WARNING: Missing block: B:42:0x00a4, code skipped:
            if (r0 > 90.0f) goto L_0x00be;
     */
        /* JADX WARNING: Missing block: B:44:0x00a8, code skipped:
            if (r2 > 90.0f) goto L_0x00be;
     */
        /* JADX WARNING: Missing block: B:45:0x00aa, code skipped:
            r2 = java.lang.Math.min(r11, 160);
            r0 = java.lang.Math.min(r9, 160);
     */
        /* JADX WARNING: Missing block: B:46:0x00b8, code skipped:
            if (org.telegram.messenger.SharedConfig.getDevicePerfomanceClass() == 2) goto L_0x00bb;
     */
        /* JADX WARNING: Missing block: B:47:0x00ba, code skipped:
            r8 = true;
     */
        /* JADX WARNING: Missing block: B:48:0x00bb, code skipped:
            r9 = r0;
            r0 = true;
     */
        /* JADX WARNING: Missing block: B:49:0x00be, code skipped:
            r2 = r11;
     */
        /* JADX WARNING: Missing block: B:50:0x00c0, code skipped:
            r9 = r0;
     */
        /* JADX WARNING: Missing block: B:51:0x00c1, code skipped:
            r0 = false;
     */
        /* JADX WARNING: Missing block: B:53:0x00c3, code skipped:
            if (r10.length < 3) goto L_0x00dc;
     */
        /* JADX WARNING: Missing block: B:55:0x00cd, code skipped:
            if ("nr".equals(r10[2]) == false) goto L_0x00d1;
     */
        /* JADX WARNING: Missing block: B:56:0x00cf, code skipped:
            r7 = 2;
     */
        /* JADX WARNING: Missing block: B:58:0x00d9, code skipped:
            if ("nrs".equals(r10[2]) == false) goto L_0x00dc;
     */
        /* JADX WARNING: Missing block: B:59:0x00db, code skipped:
            r7 = 3;
     */
        /* JADX WARNING: Missing block: B:61:0x00de, code skipped:
            if (r10.length < 5) goto L_0x0137;
     */
        /* JADX WARNING: Missing block: B:63:0x00e9, code skipped:
            if ("c1".equals(r10[4]) == false) goto L_0x00f7;
     */
        /* JADX WARNING: Missing block: B:64:0x00eb, code skipped:
            r3 = new int[]{16219713, 13275258, 16757049, 15582629, 16765248, 16245699, 16768889, 16510934};
     */
        /* JADX WARNING: Missing block: B:65:0x00f0, code skipped:
            r15 = r0;
            r12 = r2;
            r16 = r3;
            r14 = r8;
            r13 = r9;
     */
        /* JADX WARNING: Missing block: B:67:0x00ff, code skipped:
            if ("c2".equals(r10[4]) == false) goto L_0x0107;
     */
        /* JADX WARNING: Missing block: B:68:0x0101, code skipped:
            r3 = new int[]{16219713, 11172960, 16757049, 13150599, 16765248, 14534815, 16768889, 15128242};
     */
        /* JADX WARNING: Missing block: B:70:0x010f, code skipped:
            if ("c3".equals(r10[4]) == false) goto L_0x0117;
     */
        /* JADX WARNING: Missing block: B:71:0x0111, code skipped:
            r3 = new int[]{16219713, 9199944, 16757049, 11371874, 16765248, 12885622, 16768889, 13939080};
     */
        /* JADX WARNING: Missing block: B:73:0x011f, code skipped:
            if ("c4".equals(r10[4]) == false) goto L_0x0127;
     */
        /* JADX WARNING: Missing block: B:74:0x0121, code skipped:
            r3 = new int[]{16219713, 7224364, 16757049, 9591348, 16765248, 10579526, 16768889, 11303506};
     */
        /* JADX WARNING: Missing block: B:76:0x012f, code skipped:
            if ("c5".equals(r10[4]) == false) goto L_0x0137;
     */
        /* JADX WARNING: Missing block: B:77:0x0131, code skipped:
            r3 = new int[]{16219713, 2694162, 16757049, 4663842, 16765248, 5716784, 16768889, 6834492};
     */
        /* JADX WARNING: Missing block: B:78:0x0137, code skipped:
            r15 = r0;
            r12 = r2;
            r14 = r8;
            r13 = r9;
     */
        /* JADX WARNING: Missing block: B:79:0x013c, code skipped:
            r13 = r0;
            r12 = r2;
            r14 = false;
            r15 = false;
     */
        /* JADX WARNING: Missing block: B:80:0x0140, code skipped:
            r16 = null;
     */
        /* JADX WARNING: Missing block: B:81:0x0142, code skipped:
            r10 = new org.telegram.ui.Components.RLottieDrawable(r1.cacheImage.finalFilePath, r12, r13, r14, r15, r16);
            r10.setAutoRepeat(r7);
            onPostExecute(r10);
     */
        /* JADX WARNING: Missing block: B:87:0x0159, code skipped:
            if (r0.animatedFile == false) goto L_0x01c2;
     */
        /* JADX WARNING: Missing block: B:88:0x015b, code skipped:
            r2 = r1.sync;
     */
        /* JADX WARNING: Missing block: B:89:0x015d, code skipped:
            monitor-enter(r2);
     */
        /* JADX WARNING: Missing block: B:92:0x0160, code skipped:
            if (r1.isCancelled == false) goto L_0x0164;
     */
        /* JADX WARNING: Missing block: B:93:0x0162, code skipped:
            monitor-exit(r2);
     */
        /* JADX WARNING: Missing block: B:94:0x0163, code skipped:
            return;
     */
        /* JADX WARNING: Missing block: B:95:0x0164, code skipped:
            monitor-exit(r2);
     */
        /* JADX WARNING: Missing block: B:97:0x016f, code skipped:
            if ("g".equals(r1.cacheImage.filter) == false) goto L_0x0197;
     */
        /* JADX WARNING: Missing block: B:98:0x0171, code skipped:
            r0 = r1.cacheImage;
            r2 = r0.imageLocation.document;
     */
        /* JADX WARNING: Missing block: B:99:0x0179, code skipped:
            if ((r2 instanceof org.telegram.tgnet.TLRPC.TL_documentEncrypted) != false) goto L_0x0197;
     */
        /* JADX WARNING: Missing block: B:100:0x017b, code skipped:
            r6 = r0.finalFilePath;
            r8 = (long) r0.size;
     */
        /* JADX WARNING: Missing block: B:101:0x0185, code skipped:
            if ((r2 instanceof org.telegram.tgnet.TLRPC.Document) == false) goto L_0x0189;
     */
        /* JADX WARNING: Missing block: B:102:0x0187, code skipped:
            r10 = r2;
     */
        /* JADX WARNING: Missing block: B:103:0x0189, code skipped:
            r10 = null;
     */
        /* JADX WARNING: Missing block: B:104:0x018a, code skipped:
            r0 = r1.cacheImage;
            r0 = new org.telegram.ui.Components.AnimatedFileDrawable(r6, false, r8, r10, r0.parentObject, r0.currentAccount, false);
     */
        /* JADX WARNING: Missing block: B:105:0x0197, code skipped:
            r2 = r1.cacheImage;
            r14 = new org.telegram.ui.Components.AnimatedFileDrawable(r2.finalFilePath, "d".equals(r2.filter), 0, null, null, r1.cacheImage.currentAccount, false);
     */
        /* JADX WARNING: Missing block: B:106:0x01b7, code skipped:
            java.lang.Thread.interrupted();
            onPostExecute(r0);
     */
        /* JADX WARNING: Missing block: B:111:0x01c2, code skipped:
            r2 = r0.finalFilePath;
     */
        /* JADX WARNING: Missing block: B:112:0x01c6, code skipped:
            if (r0.secureDocument != null) goto L_0x01dd;
     */
        /* JADX WARNING: Missing block: B:114:0x01ca, code skipped:
            if (r0.encryptionKeyPath == null) goto L_0x01db;
     */
        /* JADX WARNING: Missing block: B:115:0x01cc, code skipped:
            if (r2 == null) goto L_0x01db;
     */
        /* JADX WARNING: Missing block: B:117:0x01d8, code skipped:
            if (r2.getAbsolutePath().endsWith(".enc") == false) goto L_0x01db;
     */
        /* JADX WARNING: Missing block: B:118:0x01db, code skipped:
            r9 = null;
     */
        /* JADX WARNING: Missing block: B:119:0x01dd, code skipped:
            r9 = 1;
     */
        /* JADX WARNING: Missing block: B:120:0x01de, code skipped:
            r0 = r1.cacheImage.secureDocument;
     */
        /* JADX WARNING: Missing block: B:121:0x01e2, code skipped:
            if (r0 == null) goto L_0x01f7;
     */
        /* JADX WARNING: Missing block: B:122:0x01e4, code skipped:
            r10 = r0.secureDocumentKey;
            r0 = r0.secureFile;
     */
        /* JADX WARNING: Missing block: B:123:0x01e8, code skipped:
            if (r0 == null) goto L_0x01ef;
     */
        /* JADX WARNING: Missing block: B:124:0x01ea, code skipped:
            r0 = r0.file_hash;
     */
        /* JADX WARNING: Missing block: B:125:0x01ec, code skipped:
            if (r0 == null) goto L_0x01ef;
     */
        /* JADX WARNING: Missing block: B:126:0x01ef, code skipped:
            r0 = r1.cacheImage.secureDocument.fileHash;
     */
        /* JADX WARNING: Missing block: B:127:0x01f5, code skipped:
            r11 = r0;
     */
        /* JADX WARNING: Missing block: B:128:0x01f7, code skipped:
            r10 = null;
            r11 = null;
     */
        /* JADX WARNING: Missing block: B:130:0x01fd, code skipped:
            if (android.os.Build.VERSION.SDK_INT >= 19) goto L_0x0269;
     */
        /* JADX WARNING: Missing block: B:132:?, code skipped:
            r12 = new java.io.RandomAccessFile(r2, "r");
     */
        /* JADX WARNING: Missing block: B:135:0x020a, code skipped:
            if (r1.cacheImage.imageType != 1) goto L_0x0211;
     */
        /* JADX WARNING: Missing block: B:136:0x020c, code skipped:
            r0 = org.telegram.messenger.ImageLoader.access$1800();
     */
        /* JADX WARNING: Missing block: B:137:0x0211, code skipped:
            r0 = org.telegram.messenger.ImageLoader.access$1900();
     */
        /* JADX WARNING: Missing block: B:138:0x0215, code skipped:
            r12.readFully(r0, 0, r0.length);
            r0 = new java.lang.String(r0).toLowerCase().toLowerCase();
     */
        /* JADX WARNING: Missing block: B:139:0x022c, code skipped:
            if (r0.startsWith("riff") == false) goto L_0x0239;
     */
        /* JADX WARNING: Missing block: B:141:0x0235, code skipped:
            if (r0.endsWith("webp") == false) goto L_0x0239;
     */
        /* JADX WARNING: Missing block: B:142:0x0237, code skipped:
            r13 = true;
     */
        /* JADX WARNING: Missing block: B:143:0x0239, code skipped:
            r13 = null;
     */
        /* JADX WARNING: Missing block: B:145:?, code skipped:
            r12.close();
     */
        /* JADX WARNING: Missing block: B:147:?, code skipped:
            r12.close();
     */
        /* JADX WARNING: Missing block: B:148:0x0241, code skipped:
            r0 = move-exception;
     */
        /* JADX WARNING: Missing block: B:149:0x0242, code skipped:
            org.telegram.messenger.FileLog.e(r0);
     */
        /* JADX WARNING: Missing block: B:150:0x0247, code skipped:
            r0 = e;
     */
        /* JADX WARNING: Missing block: B:151:0x0249, code skipped:
            r0 = e;
     */
        /* JADX WARNING: Missing block: B:152:0x024b, code skipped:
            r0 = move-exception;
     */
        /* JADX WARNING: Missing block: B:153:0x024c, code skipped:
            r2 = r0;
            r12 = null;
     */
        /* JADX WARNING: Missing block: B:154:0x024f, code skipped:
            r0 = e;
     */
        /* JADX WARNING: Missing block: B:155:0x0250, code skipped:
            r12 = null;
     */
        /* JADX WARNING: Missing block: B:156:0x0251, code skipped:
            r13 = null;
     */
        /* JADX WARNING: Missing block: B:158:?, code skipped:
            org.telegram.messenger.FileLog.e(r0);
     */
        /* JADX WARNING: Missing block: B:159:0x0255, code skipped:
            if (r12 != null) goto L_0x0257;
     */
        /* JADX WARNING: Missing block: B:161:?, code skipped:
            r12.close();
     */
        /* JADX WARNING: Missing block: B:162:0x025b, code skipped:
            r0 = move-exception;
     */
        /* JADX WARNING: Missing block: B:163:0x025c, code skipped:
            r2 = r0;
     */
        /* JADX WARNING: Missing block: B:164:0x025d, code skipped:
            if (r12 != null) goto L_0x025f;
     */
        /* JADX WARNING: Missing block: B:166:?, code skipped:
            r12.close();
     */
        /* JADX WARNING: Missing block: B:167:0x0263, code skipped:
            r0 = move-exception;
     */
        /* JADX WARNING: Missing block: B:168:0x0264, code skipped:
            org.telegram.messenger.FileLog.e(r0);
     */
        /* JADX WARNING: Missing block: B:169:0x0268, code skipped:
            throw r2;
     */
        /* JADX WARNING: Missing block: B:170:0x0269, code skipped:
            r13 = null;
     */
        /* JADX WARNING: Missing block: B:171:0x026a, code skipped:
            r0 = r1.cacheImage.imageLocation.path;
     */
        /* JADX WARNING: Missing block: B:172:0x0270, code skipped:
            if (r0 != null) goto L_0x0272;
     */
        /* JADX WARNING: Missing block: B:174:0x0278, code skipped:
            if (r0.startsWith("thumb://") != false) goto L_0x027a;
     */
        /* JADX WARNING: Missing block: B:175:0x027a, code skipped:
            r12 = r0.indexOf(":", 8);
     */
        /* JADX WARNING: Missing block: B:176:0x0280, code skipped:
            if (r12 >= 0) goto L_0x0282;
     */
        /* JADX WARNING: Missing block: B:177:0x0282, code skipped:
            r14 = java.lang.Long.valueOf(java.lang.Long.parseLong(r0.substring(8, r12)));
            r0 = r0.substring(r12 + 1);
     */
        /* JADX WARNING: Missing block: B:178:0x0294, code skipped:
            r0 = null;
            r14 = null;
     */
        /* JADX WARNING: Missing block: B:179:0x0296, code skipped:
            r12 = r0;
     */
        /* JADX WARNING: Missing block: B:180:0x0297, code skipped:
            r15 = null;
     */
        /* JADX WARNING: Missing block: B:181:0x0298, code skipped:
            r16 = null;
     */
        /* JADX WARNING: Missing block: B:183:0x02a2, code skipped:
            if (r0.startsWith("vthumb://") != false) goto L_0x02a4;
     */
        /* JADX WARNING: Missing block: B:184:0x02a4, code skipped:
            r12 = r0.indexOf(":", 9);
     */
        /* JADX WARNING: Missing block: B:185:0x02ac, code skipped:
            if (r12 >= 0) goto L_0x02ae;
     */
        /* JADX WARNING: Missing block: B:186:0x02ae, code skipped:
            r0 = java.lang.Long.valueOf(java.lang.Long.parseLong(r0.substring(9, r12)));
            r12 = 1;
     */
        /* JADX WARNING: Missing block: B:187:0x02be, code skipped:
            r0 = null;
            r12 = null;
     */
        /* JADX WARNING: Missing block: B:188:0x02c0, code skipped:
            r14 = r0;
            r15 = r12;
            r12 = null;
     */
        /* JADX WARNING: Missing block: B:190:0x02ca, code skipped:
            if (r0.startsWith("http") == false) goto L_0x02cc;
     */
        /* JADX WARNING: Missing block: B:191:0x02cc, code skipped:
            r12 = null;
            r14 = null;
     */
        /* JADX WARNING: Missing block: B:192:0x02cf, code skipped:
            r12 = null;
            r14 = null;
            r15 = null;
            r16 = 1;
     */
        /* JADX WARNING: Missing block: B:193:0x02d4, code skipped:
            r5 = new android.graphics.BitmapFactory.Options();
            r5.inSampleSize = 1;
     */
        /* JADX WARNING: Missing block: B:194:0x02df, code skipped:
            if (android.os.Build.VERSION.SDK_INT < 21) goto L_0x02e1;
     */
        /* JADX WARNING: Missing block: B:195:0x02e1, code skipped:
            r5.inPurgeable = true;
     */
        /* JADX WARNING: Missing block: B:196:0x02e3, code skipped:
            r19 = org.telegram.messenger.ImageLoader.access$2000(r1.this$0);
     */
        /* JADX WARNING: Missing block: B:199:0x02f1, code skipped:
            if (r1.cacheImage.filter != null) goto L_0x02f3;
     */
        /* JADX WARNING: Missing block: B:200:0x02f3, code skipped:
            r0 = r1.cacheImage.filter.split("_");
     */
        /* JADX WARNING: Missing block: B:201:0x02fe, code skipped:
            if (r0.length >= 2) goto L_0x0300;
     */
        /* JADX WARNING: Missing block: B:203:0x0308, code skipped:
            r4 = java.lang.Float.parseFloat(r0[0]) * org.telegram.messenger.AndroidUtilities.density;
     */
        /* JADX WARNING: Missing block: B:206:0x0312, code skipped:
            r23 = java.lang.Float.parseFloat(r0[1]) * org.telegram.messenger.AndroidUtilities.density;
     */
        /* JADX WARNING: Missing block: B:207:0x0317, code skipped:
            r0 = th;
     */
        /* JADX WARNING: Missing block: B:208:0x0318, code skipped:
            r3 = r4;
            r25 = r12;
            r8 = r13;
     */
        /* JADX WARNING: Missing block: B:209:0x031e, code skipped:
            r4 = 0.0f;
            r23 = 0.0f;
     */
        /* JADX WARNING: Missing block: B:212:0x032b, code skipped:
            if (r1.cacheImage.filter.contains("b2") != false) goto L_0x032d;
     */
        /* JADX WARNING: Missing block: B:213:0x032d, code skipped:
            r6 = 3;
     */
        /* JADX WARNING: Missing block: B:215:0x0339, code skipped:
            if (r1.cacheImage.filter.contains("b1") != false) goto L_0x033b;
     */
        /* JADX WARNING: Missing block: B:216:0x033b, code skipped:
            r6 = 2;
     */
        /* JADX WARNING: Missing block: B:218:0x0347, code skipped:
            if (r1.cacheImage.filter.contains("b") != false) goto L_0x0349;
     */
        /* JADX WARNING: Missing block: B:219:0x0349, code skipped:
            r6 = 1;
     */
        /* JADX WARNING: Missing block: B:220:0x034b, code skipped:
            r6 = 0;
     */
        /* JADX WARNING: Missing block: B:222:?, code skipped:
            r3 = r1.cacheImage.filter.contains("i");
     */
        /* JADX WARNING: Missing block: B:224:?, code skipped:
            r8 = "f";
     */
        /* JADX WARNING: Missing block: B:225:0x0360, code skipped:
            if (r1.cacheImage.filter.contains(r8) != false) goto L_0x0362;
     */
        /* JADX WARNING: Missing block: B:226:0x0362, code skipped:
            r19 = true;
     */
        /* JADX WARNING: Missing block: B:227:0x0364, code skipped:
            if (r13 != null) goto L_0x0462;
     */
        /* JADX WARNING: Missing block: B:232:0x036e, code skipped:
            r5.inJustDecodeBounds = true;
     */
        /* JADX WARNING: Missing block: B:233:0x0370, code skipped:
            if (r14 == null) goto L_0x03a1;
     */
        /* JADX WARNING: Missing block: B:235:0x0374, code skipped:
            if (r15 != null) goto L_0x0376;
     */
        /* JADX WARNING: Missing block: B:238:0x037c, code skipped:
            r25 = r12;
            r8 = r13;
     */
        /* JADX WARNING: Missing block: B:240:?, code skipped:
            android.provider.MediaStore.Video.Thumbnails.getThumbnail(org.telegram.messenger.ApplicationLoader.applicationContext.getContentResolver(), r14.longValue(), 1, r5);
     */
        /* JADX WARNING: Missing block: B:241:0x0387, code skipped:
            r0 = th;
     */
        /* JADX WARNING: Missing block: B:242:0x0388, code skipped:
            r25 = r12;
            r8 = r13;
     */
        /* JADX WARNING: Missing block: B:243:0x038d, code skipped:
            r25 = r12;
            r8 = r13;
            android.provider.MediaStore.Images.Thumbnails.getThumbnail(org.telegram.messenger.ApplicationLoader.applicationContext.getContentResolver(), r14.longValue(), 1, r5);
     */
        /* JADX WARNING: Missing block: B:244:0x039d, code skipped:
            r26 = r3;
     */
        /* JADX WARNING: Missing block: B:245:0x03a1, code skipped:
            r25 = r12;
            r8 = r13;
     */
        /* JADX WARNING: Missing block: B:246:0x03a4, code skipped:
            if (r10 != null) goto L_0x03a6;
     */
        /* JADX WARNING: Missing block: B:247:0x03a6, code skipped:
            r0 = new java.io.RandomAccessFile(r2, "r");
            r13 = (int) r0.length();
            r12 = (byte[]) org.telegram.messenger.ImageLoader.access$2100().get();
     */
        /* JADX WARNING: Missing block: B:248:0x03bc, code skipped:
            if (r12 == null) goto L_0x03c2;
     */
        /* JADX WARNING: Missing block: B:252:0x03c2, code skipped:
            r12 = null;
     */
        /* JADX WARNING: Missing block: B:253:0x03c3, code skipped:
            if (r12 == null) goto L_0x03c5;
     */
        /* JADX WARNING: Missing block: B:254:0x03c5, code skipped:
            r12 = new byte[r13];
            org.telegram.messenger.ImageLoader.access$2100().set(r12);
     */
        /* JADX WARNING: Missing block: B:255:0x03ce, code skipped:
            r0.readFully(r12, 0, r13);
            r0.close();
            org.telegram.messenger.secretmedia.EncryptedFileInputStream.decryptBytesWithKeyFile(r12, 0, r13, r10);
            r0 = org.telegram.messenger.Utilities.computeSHA256(r12, 0, r13);
     */
        /* JADX WARNING: Missing block: B:256:0x03dc, code skipped:
            if (r11 == null) goto L_0x03e9;
     */
        /* JADX WARNING: Missing block: B:259:0x03e5, code skipped:
            r26 = r3;
            r0 = null;
     */
        /* JADX WARNING: Missing block: B:260:0x03e9, code skipped:
            r26 = r3;
            r0 = 1;
     */
        /* JADX WARNING: Missing block: B:263:?, code skipped:
            r3 = r12[0] & 255;
            r13 = r13 - r3;
     */
        /* JADX WARNING: Missing block: B:264:0x03f2, code skipped:
            if (r0 == null) goto L_0x03f4;
     */
        /* JADX WARNING: Missing block: B:265:0x03f4, code skipped:
            android.graphics.BitmapFactory.decodeByteArray(r12, r3, r13, r5);
     */
        /* JADX WARNING: Missing block: B:266:0x03f8, code skipped:
            r0 = th;
     */
        /* JADX WARNING: Missing block: B:267:0x03f9, code skipped:
            r26 = r3;
     */
        /* JADX WARNING: Missing block: B:268:0x03fd, code skipped:
            r26 = r3;
     */
        /* JADX WARNING: Missing block: B:269:0x03ff, code skipped:
            if (r9 != null) goto L_0x0401;
     */
        /* JADX WARNING: Missing block: B:270:0x0401, code skipped:
            r0 = new org.telegram.messenger.secretmedia.EncryptedFileInputStream(r2, r1.cacheImage.encryptionKeyPath);
     */
        /* JADX WARNING: Missing block: B:271:0x040b, code skipped:
            r0 = new java.io.FileInputStream(r2);
     */
        /* JADX WARNING: Missing block: B:272:0x0410, code skipped:
            android.graphics.BitmapFactory.decodeStream(r0, null, r5);
            r0.close();
     */
        /* JADX WARNING: Missing block: B:273:0x0417, code skipped:
            r0 = (float) r5.outWidth;
            r3 = (float) r5.outHeight;
     */
        /* JADX WARNING: Missing block: B:274:0x041f, code skipped:
            if (r4 < r23) goto L_0x042e;
     */
        /* JADX WARNING: Missing block: B:277:0x0425, code skipped:
            r7 = java.lang.Math.max(r0 / r4, r3 / r23);
     */
        /* JADX WARNING: Missing block: B:278:0x042e, code skipped:
            r7 = java.lang.Math.min(r0 / r4, r3 / r23);
     */
        /* JADX WARNING: Missing block: B:280:0x043b, code skipped:
            if (r7 < 1.2f) goto L_0x043d;
     */
        /* JADX WARNING: Missing block: B:281:0x043d, code skipped:
            r7 = 1.0f;
     */
        /* JADX WARNING: Missing block: B:282:0x043f, code skipped:
            r5.inJustDecodeBounds = false;
     */
        /* JADX WARNING: Missing block: B:283:0x0444, code skipped:
            if (r7 <= 1.0f) goto L_0x045c;
     */
        /* JADX WARNING: Missing block: B:288:0x044e, code skipped:
            r0 = 1;
     */
        /* JADX WARNING: Missing block: B:289:0x044f, code skipped:
            r0 = r0 * 2;
     */
        /* JADX WARNING: Missing block: B:290:0x0457, code skipped:
            if (((float) (r0 * 2)) < r7) goto L_0x044f;
     */
        /* JADX WARNING: Missing block: B:291:0x0459, code skipped:
            r5.inSampleSize = r0;
     */
        /* JADX WARNING: Missing block: B:292:0x045c, code skipped:
            r5.inSampleSize = (int) r7;
     */
        /* JADX WARNING: Missing block: B:293:0x0460, code skipped:
            r0 = th;
     */
        /* JADX WARNING: Missing block: B:294:0x0462, code skipped:
            r26 = r3;
            r25 = r12;
            r8 = r13;
     */
        /* JADX WARNING: Missing block: B:295:0x0467, code skipped:
            r0 = r4;
            r4 = null;
     */
        /* JADX WARNING: Missing block: B:296:0x046b, code skipped:
            r0 = th;
     */
        /* JADX WARNING: Missing block: B:297:0x046c, code skipped:
            r26 = r3;
            r25 = r12;
            r8 = r13;
     */
        /* JADX WARNING: Missing block: B:298:0x0471, code skipped:
            r3 = r4;
            r4 = null;
     */
        /* JADX WARNING: Missing block: B:299:0x0475, code skipped:
            r0 = th;
     */
        /* JADX WARNING: Missing block: B:300:0x0476, code skipped:
            r25 = r12;
            r8 = r13;
            r3 = r4;
            r4 = null;
     */
        /* JADX WARNING: Missing block: B:301:0x047d, code skipped:
            r0 = th;
     */
        /* JADX WARNING: Missing block: B:302:0x047e, code skipped:
            r25 = r12;
            r8 = r13;
            r3 = r4;
            r4 = null;
            r6 = 0;
     */
        /* JADX WARNING: Missing block: B:303:0x0485, code skipped:
            r25 = r12;
            r8 = r13;
     */
        /* JADX WARNING: Missing block: B:304:0x0488, code skipped:
            if (r25 != null) goto L_0x048a;
     */
        /* JADX WARNING: Missing block: B:307:?, code skipped:
            r5.inJustDecodeBounds = true;
     */
        /* JADX WARNING: Missing block: B:308:0x048d, code skipped:
            if (r19 != false) goto L_0x048f;
     */
        /* JADX WARNING: Missing block: B:309:0x048f, code skipped:
            r0 = android.graphics.Bitmap.Config.ARGB_8888;
     */
        /* JADX WARNING: Missing block: B:310:0x0492, code skipped:
            r0 = android.graphics.Bitmap.Config.RGB_565;
     */
        /* JADX WARNING: Missing block: B:311:0x0494, code skipped:
            r5.inPreferredConfig = r0;
            r0 = new java.io.FileInputStream(r2);
            r4 = android.graphics.BitmapFactory.decodeStream(r0, null, r5);
     */
        /* JADX WARNING: Missing block: B:313:?, code skipped:
            r0.close();
            r0 = r5.outWidth;
            r3 = r5.outHeight;
            r5.inJustDecodeBounds = false;
            r0 = (float) java.lang.Math.max(r0 / 200, r3 / 200);
     */
        /* JADX WARNING: Missing block: B:314:0x04b5, code skipped:
            if (r0 < 1.0f) goto L_0x04b7;
     */
        /* JADX WARNING: Missing block: B:315:0x04b7, code skipped:
            r0 = 1.0f;
     */
        /* JADX WARNING: Missing block: B:317:0x04bb, code skipped:
            if (r0 > 1.0f) goto L_0x04bd;
     */
        /* JADX WARNING: Missing block: B:318:0x04bd, code skipped:
            r3 = 1;
     */
        /* JADX WARNING: Missing block: B:319:0x04be, code skipped:
            r3 = r3 * 2;
     */
        /* JADX WARNING: Missing block: B:320:0x04c6, code skipped:
            if (((float) (r3 * 2)) < r0) goto L_0x04be;
     */
        /* JADX WARNING: Missing block: B:321:0x04c8, code skipped:
            r5.inSampleSize = r3;
     */
        /* JADX WARNING: Missing block: B:322:0x04cb, code skipped:
            r5.inSampleSize = (int) r0;
     */
        /* JADX WARNING: Missing block: B:323:0x04ce, code skipped:
            r0 = 0.0f;
     */
        /* JADX WARNING: Missing block: B:324:0x04d0, code skipped:
            r0 = th;
     */
        /* JADX WARNING: Missing block: B:325:0x04d1, code skipped:
            r3 = 0.0f;
     */
        /* JADX WARNING: Missing block: B:326:0x04d3, code skipped:
            r0 = th;
     */
        /* JADX WARNING: Missing block: B:327:0x04d5, code skipped:
            r0 = 0.0f;
            r4 = null;
     */
        /* JADX WARNING: Missing block: B:328:0x04d7, code skipped:
            r6 = 0;
            r23 = 0.0f;
            r26 = false;
     */
        /* JADX WARNING: Missing block: B:329:0x04dd, code skipped:
            r0 = th;
     */
        /* JADX WARNING: Missing block: B:330:0x04de, code skipped:
            r25 = r12;
            r8 = r13;
     */
        /* JADX WARNING: Missing block: B:331:0x04e1, code skipped:
            r3 = 0.0f;
     */
        /* JADX WARNING: Missing block: B:332:0x04e2, code skipped:
            r4 = null;
     */
        /* JADX WARNING: Missing block: B:333:0x04e3, code skipped:
            r6 = 0;
            r23 = 0.0f;
     */
        /* JADX WARNING: Missing block: B:334:0x04e6, code skipped:
            r26 = false;
     */
        /* JADX WARNING: Missing block: B:335:0x04e8, code skipped:
            org.telegram.messenger.FileLog.e(r0);
            r0 = r3;
     */
        /* JADX WARNING: Missing block: B:336:0x04ec, code skipped:
            r3 = r23;
     */
        /* JADX WARNING: Missing block: B:337:0x04f3, code skipped:
            if (r1.cacheImage.imageType == 1) goto L_0x04f5;
     */
        /* JADX WARNING: Missing block: B:339:?, code skipped:
            org.telegram.messenger.ImageLoader.access$2202(r1.this$0, java.lang.System.currentTimeMillis());
     */
        /* JADX WARNING: Missing block: B:340:0x0500, code skipped:
            monitor-enter(r1.sync);
     */
        /* JADX WARNING: Missing block: B:343:0x0503, code skipped:
            if (r1.isCancelled != false) goto L_0x0505;
     */
        /* JADX WARNING: Missing block: B:345:0x0506, code skipped:
            return;
     */
        /* JADX WARNING: Missing block: B:347:0x0508, code skipped:
            if (r8 == null) goto L_0x054f;
     */
        /* JADX WARNING: Missing block: B:349:?, code skipped:
            r3 = new java.io.RandomAccessFile(r2, "r");
            r7 = r3.getChannel().map(java.nio.channels.FileChannel.MapMode.READ_ONLY, 0, r2.length());
            r8 = new android.graphics.BitmapFactory.Options();
            r8.inJustDecodeBounds = true;
            org.telegram.messenger.Utilities.loadWebpImage(null, r7, r7.limit(), r8, true);
            r4 = org.telegram.messenger.Bitmaps.createBitmap(r8.outWidth, r8.outHeight, android.graphics.Bitmap.Config.ARGB_8888);
            r8 = r7.limit();
     */
        /* JADX WARNING: Missing block: B:350:0x0541, code skipped:
            if (r5.inPurgeable != false) goto L_0x0545;
     */
        /* JADX WARNING: Missing block: B:351:0x0543, code skipped:
            r9 = true;
     */
        /* JADX WARNING: Missing block: B:352:0x0545, code skipped:
            r9 = false;
     */
        /* JADX WARNING: Missing block: B:353:0x0546, code skipped:
            org.telegram.messenger.Utilities.loadWebpImage(r4, r7, r8, null, r9);
            r3.close();
     */
        /* JADX WARNING: Missing block: B:355:0x0551, code skipped:
            if (r5.inPurgeable != false) goto L_0x0570;
     */
        /* JADX WARNING: Missing block: B:356:0x0553, code skipped:
            if (r10 == null) goto L_0x0556;
     */
        /* JADX WARNING: Missing block: B:358:0x0556, code skipped:
            if (r9 == null) goto L_0x0562;
     */
        /* JADX WARNING: Missing block: B:359:0x0558, code skipped:
            r3 = new org.telegram.messenger.secretmedia.EncryptedFileInputStream(r2, r1.cacheImage.encryptionKeyPath);
     */
        /* JADX WARNING: Missing block: B:360:0x0562, code skipped:
            r3 = new java.io.FileInputStream(r2);
     */
        /* JADX WARNING: Missing block: B:361:0x0567, code skipped:
            r4 = android.graphics.BitmapFactory.decodeStream(r3, null, r5);
            r3.close();
     */
        /* JADX WARNING: Missing block: B:362:0x0570, code skipped:
            r3 = new java.io.RandomAccessFile(r2, "r");
            r8 = (int) r3.length();
            r7 = (byte[]) org.telegram.messenger.ImageLoader.access$2300().get();
     */
        /* JADX WARNING: Missing block: B:363:0x0586, code skipped:
            if (r7 == null) goto L_0x058c;
     */
        /* JADX WARNING: Missing block: B:365:0x0589, code skipped:
            if (r7.length < r8) goto L_0x058c;
     */
        /* JADX WARNING: Missing block: B:367:0x058c, code skipped:
            r7 = null;
     */
        /* JADX WARNING: Missing block: B:368:0x058d, code skipped:
            if (r7 != null) goto L_0x0598;
     */
        /* JADX WARNING: Missing block: B:369:0x058f, code skipped:
            r7 = new byte[r8];
            org.telegram.messenger.ImageLoader.access$2300().set(r7);
     */
        /* JADX WARNING: Missing block: B:370:0x0598, code skipped:
            r3.readFully(r7, 0, r8);
            r3.close();
     */
        /* JADX WARNING: Missing block: B:371:0x059f, code skipped:
            if (r10 == null) goto L_0x05bb;
     */
        /* JADX WARNING: Missing block: B:372:0x05a1, code skipped:
            org.telegram.messenger.secretmedia.EncryptedFileInputStream.decryptBytesWithKeyFile(r7, 0, r8, r10);
            r3 = org.telegram.messenger.Utilities.computeSHA256(r7, 0, r8);
     */
        /* JADX WARNING: Missing block: B:373:0x05a8, code skipped:
            if (r11 == null) goto L_0x05b3;
     */
        /* JADX WARNING: Missing block: B:375:0x05ae, code skipped:
            if (java.util.Arrays.equals(r3, r11) != false) goto L_0x05b1;
     */
        /* JADX WARNING: Missing block: B:377:0x05b1, code skipped:
            r3 = null;
     */
        /* JADX WARNING: Missing block: B:378:0x05b3, code skipped:
            r3 = 1;
     */
        /* JADX WARNING: Missing block: B:379:0x05b4, code skipped:
            r9 = r7[0] & 255;
            r8 = r8 - r9;
     */
        /* JADX WARNING: Missing block: B:380:0x05bb, code skipped:
            if (r9 == null) goto L_0x05c5;
     */
        /* JADX WARNING: Missing block: B:381:0x05bd, code skipped:
            org.telegram.messenger.secretmedia.EncryptedFileInputStream.decryptBytesWithKeyFile(r7, 0, r8, r1.cacheImage.encryptionKeyPath);
     */
        /* JADX WARNING: Missing block: B:382:0x05c5, code skipped:
            r3 = null;
            r9 = 0;
     */
        /* JADX WARNING: Missing block: B:383:0x05c7, code skipped:
            if (r3 != null) goto L_0x05cd;
     */
        /* JADX WARNING: Missing block: B:384:0x05c9, code skipped:
            r4 = android.graphics.BitmapFactory.decodeByteArray(r7, r9, r8, r5);
     */
        /* JADX WARNING: Missing block: B:385:0x05cd, code skipped:
            if (r4 != null) goto L_0x05e4;
     */
        /* JADX WARNING: Missing block: B:387:0x05d7, code skipped:
            if (r2.length() == 0) goto L_0x05df;
     */
        /* JADX WARNING: Missing block: B:389:0x05dd, code skipped:
            if (r1.cacheImage.filter != null) goto L_0x0725;
     */
        /* JADX WARNING: Missing block: B:390:0x05df, code skipped:
            r2.delete();
     */
        /* JADX WARNING: Missing block: B:392:0x05e8, code skipped:
            if (r1.cacheImage.filter == null) goto L_0x0616;
     */
        /* JADX WARNING: Missing block: B:393:0x05ea, code skipped:
            r2 = (float) r4.getWidth();
            r3 = (float) r4.getHeight();
     */
        /* JADX WARNING: Missing block: B:394:0x05f6, code skipped:
            if (r5.inPurgeable != false) goto L_0x0616;
     */
        /* JADX WARNING: Missing block: B:396:0x05fa, code skipped:
            if (r0 == 0.0f) goto L_0x0616;
     */
        /* JADX WARNING: Missing block: B:398:0x05fe, code skipped:
            if (r2 == r0) goto L_0x0616;
     */
        /* JADX WARNING: Missing block: B:400:0x0605, code skipped:
            if (r2 <= (20.0f + r0)) goto L_0x0616;
     */
        /* JADX WARNING: Missing block: B:401:0x0607, code skipped:
            r0 = org.telegram.messenger.Bitmaps.createScaledBitmap(r4, (int) r0, (int) (r3 / (r2 / r0)), true);
     */
        /* JADX WARNING: Missing block: B:402:0x0610, code skipped:
            if (r4 == r0) goto L_0x0616;
     */
        /* JADX WARNING: Missing block: B:403:0x0612, code skipped:
            r4.recycle();
            r4 = r0;
     */
        /* JADX WARNING: Missing block: B:404:0x0616, code skipped:
            if (r26 == false) goto L_0x0636;
     */
        /* JADX WARNING: Missing block: B:406:0x061a, code skipped:
            if (r5.inPurgeable == false) goto L_0x061e;
     */
        /* JADX WARNING: Missing block: B:407:0x061c, code skipped:
            r0 = 0;
     */
        /* JADX WARNING: Missing block: B:408:0x061e, code skipped:
            r0 = 1;
     */
        /* JADX WARNING: Missing block: B:410:0x062f, code skipped:
            if (org.telegram.messenger.Utilities.needInvert(r4, r0, r4.getWidth(), r4.getHeight(), r4.getRowBytes()) == 0) goto L_0x0633;
     */
        /* JADX WARNING: Missing block: B:411:0x0631, code skipped:
            r8 = true;
     */
        /* JADX WARNING: Missing block: B:412:0x0633, code skipped:
            r8 = false;
     */
        /* JADX WARNING: Missing block: B:413:0x0634, code skipped:
            r2 = r8;
     */
        /* JADX WARNING: Missing block: B:414:0x0636, code skipped:
            r2 = false;
     */
        /* JADX WARNING: Missing block: B:416:0x0638, code skipped:
            if (r6 != 1) goto L_0x065f;
     */
        /* JADX WARNING: Missing block: B:419:0x0640, code skipped:
            if (r4.getConfig() != android.graphics.Bitmap.Config.ARGB_8888) goto L_0x06ea;
     */
        /* JADX WARNING: Missing block: B:421:0x0645, code skipped:
            if (r5.inPurgeable == false) goto L_0x0649;
     */
        /* JADX WARNING: Missing block: B:422:0x0647, code skipped:
            r9 = 0;
     */
        /* JADX WARNING: Missing block: B:423:0x0649, code skipped:
            r9 = 1;
     */
        /* JADX WARNING: Missing block: B:424:0x064a, code skipped:
            org.telegram.messenger.Utilities.blurBitmap(r4, 3, r9, r4.getWidth(), r4.getHeight(), r4.getRowBytes());
     */
        /* JADX WARNING: Missing block: B:425:0x065c, code skipped:
            r0 = th;
     */
        /* JADX WARNING: Missing block: B:428:0x0660, code skipped:
            if (r6 != 2) goto L_0x0684;
     */
        /* JADX WARNING: Missing block: B:430:0x0668, code skipped:
            if (r4.getConfig() != android.graphics.Bitmap.Config.ARGB_8888) goto L_0x06ea;
     */
        /* JADX WARNING: Missing block: B:432:0x066d, code skipped:
            if (r5.inPurgeable == false) goto L_0x0671;
     */
        /* JADX WARNING: Missing block: B:433:0x066f, code skipped:
            r9 = 0;
     */
        /* JADX WARNING: Missing block: B:434:0x0671, code skipped:
            r9 = 1;
     */
        /* JADX WARNING: Missing block: B:435:0x0672, code skipped:
            org.telegram.messenger.Utilities.blurBitmap(r4, 1, r9, r4.getWidth(), r4.getHeight(), r4.getRowBytes());
     */
        /* JADX WARNING: Missing block: B:437:0x0685, code skipped:
            if (r6 != 3) goto L_0x06d8;
     */
        /* JADX WARNING: Missing block: B:439:0x068d, code skipped:
            if (r4.getConfig() != android.graphics.Bitmap.Config.ARGB_8888) goto L_0x06ea;
     */
        /* JADX WARNING: Missing block: B:441:0x0692, code skipped:
            if (r5.inPurgeable == false) goto L_0x0696;
     */
        /* JADX WARNING: Missing block: B:442:0x0694, code skipped:
            r9 = 0;
     */
        /* JADX WARNING: Missing block: B:443:0x0696, code skipped:
            r9 = 1;
     */
        /* JADX WARNING: Missing block: B:444:0x0697, code skipped:
            org.telegram.messenger.Utilities.blurBitmap(r4, 7, r9, r4.getWidth(), r4.getHeight(), r4.getRowBytes());
     */
        /* JADX WARNING: Missing block: B:445:0x06aa, code skipped:
            if (r5.inPurgeable == false) goto L_0x06ae;
     */
        /* JADX WARNING: Missing block: B:446:0x06ac, code skipped:
            r9 = 0;
     */
        /* JADX WARNING: Missing block: B:447:0x06ae, code skipped:
            r9 = 1;
     */
        /* JADX WARNING: Missing block: B:448:0x06af, code skipped:
            org.telegram.messenger.Utilities.blurBitmap(r4, 7, r9, r4.getWidth(), r4.getHeight(), r4.getRowBytes());
     */
        /* JADX WARNING: Missing block: B:449:0x06c2, code skipped:
            if (r5.inPurgeable == false) goto L_0x06c6;
     */
        /* JADX WARNING: Missing block: B:450:0x06c4, code skipped:
            r9 = 0;
     */
        /* JADX WARNING: Missing block: B:451:0x06c6, code skipped:
            r9 = 1;
     */
        /* JADX WARNING: Missing block: B:452:0x06c7, code skipped:
            org.telegram.messenger.Utilities.blurBitmap(r4, 7, r9, r4.getWidth(), r4.getHeight(), r4.getRowBytes());
     */
        /* JADX WARNING: Missing block: B:453:0x06d8, code skipped:
            if (r6 != 0) goto L_0x06ea;
     */
        /* JADX WARNING: Missing block: B:455:0x06dc, code skipped:
            if (r5.inPurgeable == false) goto L_0x06ea;
     */
        /* JADX WARNING: Missing block: B:456:0x06de, code skipped:
            org.telegram.messenger.Utilities.pinBitmap(r4);
     */
        /* JADX WARNING: Missing block: B:462:0x06e5, code skipped:
            r0 = th;
     */
        /* JADX WARNING: Missing block: B:463:0x06e6, code skipped:
            r2 = false;
     */
        /* JADX WARNING: Missing block: B:464:0x06e7, code skipped:
            org.telegram.messenger.FileLog.e(r0);
     */
        /* JADX WARNING: Missing block: B:465:0x06ea, code skipped:
            r7 = null;
     */
        /* JADX WARNING: Missing block: B:466:0x06eb, code skipped:
            r8 = 0;
     */
        /* JADX WARNING: Missing block: B:467:0x06ee, code skipped:
            r7 = 20;
     */
        /* JADX WARNING: Missing block: B:468:0x06f0, code skipped:
            if (r14 != null) goto L_0x06f2;
     */
        /* JADX WARNING: Missing block: B:469:0x06f2, code skipped:
            r7 = 0;
     */
        /* JADX WARNING: Missing block: B:470:0x06f3, code skipped:
            if (r7 != 0) goto L_0x06f5;
     */
        /* JADX WARNING: Missing block: B:473:0x06ff, code skipped:
            if (org.telegram.messenger.ImageLoader.access$2200(r1.this$0) != 0) goto L_0x0701;
     */
        /* JADX WARNING: Missing block: B:475:0x070b, code skipped:
            r21 = r3;
            r27 = r4;
            r3 = (long) r7;
     */
        /* JADX WARNING: Missing block: B:476:0x0714, code skipped:
            if (org.telegram.messenger.ImageLoader.access$2200(r1.this$0) > (java.lang.System.currentTimeMillis() - r3)) goto L_0x0716;
     */
        /* JADX WARNING: Missing block: B:479:0x071a, code skipped:
            if (android.os.Build.VERSION.SDK_INT < 21) goto L_0x071c;
     */
        /* JADX WARNING: Missing block: B:480:0x071c, code skipped:
            java.lang.Thread.sleep(r3);
     */
        /* JADX WARNING: Missing block: B:481:0x0720, code skipped:
            r4 = r27;
     */
        /* JADX WARNING: Missing block: B:483:0x0723, code skipped:
            r27 = r4;
     */
        /* JADX WARNING: Missing block: B:485:0x0725, code skipped:
            r2 = false;
     */
        /* JADX WARNING: Missing block: B:487:0x0727, code skipped:
            r21 = r3;
            r27 = r4;
     */
        /* JADX WARNING: Missing block: B:489:?, code skipped:
            org.telegram.messenger.ImageLoader.access$2202(r1.this$0, java.lang.System.currentTimeMillis());
     */
        /* JADX WARNING: Missing block: B:490:0x0736, code skipped:
            monitor-enter(r1.sync);
     */
        /* JADX WARNING: Missing block: B:493:0x0739, code skipped:
            if (r1.isCancelled != false) goto L_0x073b;
     */
        /* JADX WARNING: Missing block: B:495:0x073c, code skipped:
            return;
     */
        /* JADX WARNING: Missing block: B:497:0x073e, code skipped:
            if (r19 != false) goto L_0x0756;
     */
        /* JADX WARNING: Missing block: B:500:0x0744, code skipped:
            if (r1.cacheImage.filter == null) goto L_0x0756;
     */
        /* JADX WARNING: Missing block: B:501:0x0746, code skipped:
            if (r6 != 0) goto L_0x0756;
     */
        /* JADX WARNING: Missing block: B:503:0x074e, code skipped:
            if (r1.cacheImage.imageLocation.path == null) goto L_0x0751;
     */
        /* JADX WARNING: Missing block: B:505:0x0751, code skipped:
            r5.inPreferredConfig = android.graphics.Bitmap.Config.RGB_565;
     */
        /* JADX WARNING: Missing block: B:507:?, code skipped:
            r5.inPreferredConfig = android.graphics.Bitmap.Config.ARGB_8888;
     */
        /* JADX WARNING: Missing block: B:508:0x075a, code skipped:
            r5.inDither = false;
     */
        /* JADX WARNING: Missing block: B:509:0x075d, code skipped:
            if (r14 == null) goto L_0x0784;
     */
        /* JADX WARNING: Missing block: B:510:0x075f, code skipped:
            if (r25 != null) goto L_0x0784;
     */
        /* JADX WARNING: Missing block: B:511:0x0761, code skipped:
            if (r15 == null) goto L_0x0773;
     */
        /* JADX WARNING: Missing block: B:513:?, code skipped:
            r3 = android.provider.MediaStore.Video.Thumbnails.getThumbnail(org.telegram.messenger.ApplicationLoader.applicationContext.getContentResolver(), r14.longValue(), 1, r5);
     */
        /* JADX WARNING: Missing block: B:514:0x0773, code skipped:
            r3 = android.provider.MediaStore.Images.Thumbnails.getThumbnail(org.telegram.messenger.ApplicationLoader.applicationContext.getContentResolver(), r14.longValue(), 1, r5);
     */
        /* JADX WARNING: Missing block: B:515:0x0782, code skipped:
            r4 = r3;
     */
        /* JADX WARNING: Missing block: B:516:0x0784, code skipped:
            r4 = r27;
     */
        /* JADX WARNING: Missing block: B:517:0x0786, code skipped:
            if (r4 != null) goto L_0x0893;
     */
        /* JADX WARNING: Missing block: B:518:0x0788, code skipped:
            if (r8 == null) goto L_0x07d5;
     */
        /* JADX WARNING: Missing block: B:520:?, code skipped:
            r3 = new java.io.RandomAccessFile(r2, "r");
            r7 = r3.getChannel().map(java.nio.channels.FileChannel.MapMode.READ_ONLY, 0, r2.length());
            r8 = new android.graphics.BitmapFactory.Options();
            r8.inJustDecodeBounds = true;
     */
        /* JADX WARNING: Missing block: B:523:?, code skipped:
            org.telegram.messenger.Utilities.loadWebpImage(null, r7, r7.limit(), r8, true);
     */
        /* JADX WARNING: Missing block: B:525:?, code skipped:
            r4 = org.telegram.messenger.Bitmaps.createBitmap(r8.outWidth, r8.outHeight, android.graphics.Bitmap.Config.ARGB_8888);
            r8 = r7.limit();
     */
        /* JADX WARNING: Missing block: B:526:0x07c1, code skipped:
            if (r5.inPurgeable != false) goto L_0x07c5;
     */
        /* JADX WARNING: Missing block: B:527:0x07c3, code skipped:
            r9 = true;
     */
        /* JADX WARNING: Missing block: B:528:0x07c5, code skipped:
            r9 = false;
     */
        /* JADX WARNING: Missing block: B:531:?, code skipped:
            org.telegram.messenger.Utilities.loadWebpImage(r4, r7, r8, null, r9);
     */
        /* JADX WARNING: Missing block: B:533:?, code skipped:
            r3.close();
     */
        /* JADX WARNING: Missing block: B:534:0x07cf, code skipped:
            r7 = null;
     */
        /* JADX WARNING: Missing block: B:536:0x07d2, code skipped:
            r7 = null;
     */
        /* JADX WARNING: Missing block: B:540:0x07d7, code skipped:
            if (r5.inPurgeable != false) goto L_0x0831;
     */
        /* JADX WARNING: Missing block: B:541:0x07d9, code skipped:
            if (r10 == null) goto L_0x07dc;
     */
        /* JADX WARNING: Missing block: B:542:0x07dc, code skipped:
            if (r9 == null) goto L_0x07e8;
     */
        /* JADX WARNING: Missing block: B:544:?, code skipped:
            r3 = new org.telegram.messenger.secretmedia.EncryptedFileInputStream(r2, r1.cacheImage.encryptionKeyPath);
     */
        /* JADX WARNING: Missing block: B:546:?, code skipped:
            r3 = new java.io.FileInputStream(r2);
     */
        /* JADX WARNING: Missing block: B:548:0x07f5, code skipped:
            if ((r1.cacheImage.imageLocation.document instanceof org.telegram.tgnet.TLRPC.TL_document) == false) goto L_0x0827;
     */
        /* JADX WARNING: Missing block: B:550:?, code skipped:
            r7 = new androidx.exifinterface.media.ExifInterface(r3).getAttributeInt("Orientation", 1);
     */
        /* JADX WARNING: Missing block: B:552:0x0804, code skipped:
            if (r7 == 3) goto L_0x0814;
     */
        /* JADX WARNING: Missing block: B:554:0x0807, code skipped:
            if (r7 == 6) goto L_0x0811;
     */
        /* JADX WARNING: Missing block: B:556:0x080b, code skipped:
            if (r7 == 8) goto L_0x080e;
     */
        /* JADX WARNING: Missing block: B:557:0x080e, code skipped:
            r8 = 270;
     */
        /* JADX WARNING: Missing block: B:558:0x0811, code skipped:
            r8 = 90;
     */
        /* JADX WARNING: Missing block: B:559:0x0814, code skipped:
            r8 = 180;
     */
        /* JADX WARNING: Missing block: B:567:0x0827, code skipped:
            r7 = null;
            r8 = 0;
     */
        /* JADX WARNING: Missing block: B:570:0x0831, code skipped:
            r7 = null;
     */
        /* JADX WARNING: Missing block: B:572:?, code skipped:
            r3 = new java.io.RandomAccessFile(r2, "r");
            r8 = (int) r3.length();
            r12 = (byte[]) org.telegram.messenger.ImageLoader.access$2100().get();
     */
        /* JADX WARNING: Missing block: B:573:0x0848, code skipped:
            if (r12 == null) goto L_0x084e;
     */
        /* JADX WARNING: Missing block: B:575:0x084b, code skipped:
            if (r12.length < r8) goto L_0x084e;
     */
        /* JADX WARNING: Missing block: B:577:0x084e, code skipped:
            r12 = null;
     */
        /* JADX WARNING: Missing block: B:578:0x084f, code skipped:
            if (r12 != null) goto L_0x085a;
     */
        /* JADX WARNING: Missing block: B:579:0x0851, code skipped:
            r12 = new byte[r8];
            org.telegram.messenger.ImageLoader.access$2100().set(r12);
     */
        /* JADX WARNING: Missing block: B:580:0x085a, code skipped:
            r3.readFully(r12, 0, r8);
            r3.close();
     */
        /* JADX WARNING: Missing block: B:581:0x0861, code skipped:
            if (r10 == null) goto L_0x087d;
     */
        /* JADX WARNING: Missing block: B:582:0x0863, code skipped:
            org.telegram.messenger.secretmedia.EncryptedFileInputStream.decryptBytesWithKeyFile(r12, 0, r8, r10);
            r3 = org.telegram.messenger.Utilities.computeSHA256(r12, 0, r8);
     */
        /* JADX WARNING: Missing block: B:583:0x086a, code skipped:
            if (r11 == null) goto L_0x0875;
     */
        /* JADX WARNING: Missing block: B:585:0x0870, code skipped:
            if (java.util.Arrays.equals(r3, r11) != false) goto L_0x0873;
     */
        /* JADX WARNING: Missing block: B:587:0x0873, code skipped:
            r3 = null;
     */
        /* JADX WARNING: Missing block: B:588:0x0875, code skipped:
            r3 = 1;
     */
        /* JADX WARNING: Missing block: B:589:0x0876, code skipped:
            r9 = r12[0] & 255;
            r8 = r8 - r9;
     */
        /* JADX WARNING: Missing block: B:590:0x087d, code skipped:
            if (r9 == null) goto L_0x0887;
     */
        /* JADX WARNING: Missing block: B:591:0x087f, code skipped:
            org.telegram.messenger.secretmedia.EncryptedFileInputStream.decryptBytesWithKeyFile(r12, 0, r8, r1.cacheImage.encryptionKeyPath);
     */
        /* JADX WARNING: Missing block: B:592:0x0887, code skipped:
            r3 = null;
            r9 = 0;
     */
        /* JADX WARNING: Missing block: B:593:0x0889, code skipped:
            if (r3 != null) goto L_0x0894;
     */
        /* JADX WARNING: Missing block: B:594:0x088b, code skipped:
            r4 = android.graphics.BitmapFactory.decodeByteArray(r12, r9, r8, r5);
     */
        /* JADX WARNING: Missing block: B:595:0x0890, code skipped:
            r7 = null;
     */
        /* JADX WARNING: Missing block: B:597:0x0893, code skipped:
            r7 = null;
     */
        /* JADX WARNING: Missing block: B:598:0x0894, code skipped:
            r8 = 0;
     */
        /* JADX WARNING: Missing block: B:687:0x098a, code skipped:
            r7 = null;
     */
        /* JADX WARNING: Missing block: B:694:0x0990, code skipped:
            r7 = null;
     */
        /* JADX WARNING: Missing block: B:696:0x0992, code skipped:
            r4 = r27;
     */
        /* JADX WARNING: Missing block: B:698:0x0994, code skipped:
            r2 = false;
     */
        /* JADX WARNING: Missing block: B:703:0x099f, code skipped:
            if (r4 != null) goto L_0x09a1;
     */
        /* JADX WARNING: Missing block: B:704:0x09a1, code skipped:
            r3 = new android.graphics.drawable.BitmapDrawable(r4);
     */
        /* JADX WARNING: Missing block: B:705:0x09a7, code skipped:
            r3 = r7;
     */
        /* JADX WARNING: Missing block: B:706:0x09a8, code skipped:
            onPostExecute(r3);
     */
        /* JADX WARNING: Missing block: B:708:0x09ae, code skipped:
            r3 = new org.telegram.messenger.ExtendedBitmapDrawable(r4, r2, r8);
     */
        /* JADX WARNING: Missing block: B:709:0x09b4, code skipped:
            r3 = r7;
     */
        public void run() {
            /*
            r28 = this;
            r1 = r28;
            r2 = r1.sync;
            monitor-enter(r2);
            r0 = java.lang.Thread.currentThread();	 Catch:{ all -> 0x09b9 }
            r1.runningThread = r0;	 Catch:{ all -> 0x09b9 }
            java.lang.Thread.interrupted();	 Catch:{ all -> 0x09b9 }
            r0 = r1.isCancelled;	 Catch:{ all -> 0x09b9 }
            if (r0 == 0) goto L_0x0014;
        L_0x0012:
            monitor-exit(r2);	 Catch:{ all -> 0x09b9 }
            return;
        L_0x0014:
            monitor-exit(r2);	 Catch:{ all -> 0x09b9 }
            r0 = r1.cacheImage;
            r2 = r0.imageLocation;
            r2 = r2.photoSize;
            r2 = r2 instanceof org.telegram.tgnet.TLRPC.TL_photoStrippedSize;
            if (r2 == 0) goto L_0x004a;
        L_0x001f:
            r2 = r1.sync;
            monitor-enter(r2);
            r0 = r1.isCancelled;	 Catch:{ all -> 0x0047 }
            if (r0 == 0) goto L_0x0028;
        L_0x0026:
            monitor-exit(r2);	 Catch:{ all -> 0x0047 }
            return;
        L_0x0028:
            monitor-exit(r2);	 Catch:{ all -> 0x0047 }
            r0 = r1.cacheImage;
            r2 = r0.imageLocation;
            r2 = r2.photoSize;
            r2 = (org.telegram.tgnet.TLRPC.TL_photoStrippedSize) r2;
            r2 = r2.bytes;
            r0 = r0.filter;
            r0 = org.telegram.messenger.ImageLoader.getStrippedPhotoBitmap(r2, r0);
            if (r0 == 0) goto L_0x0041;
        L_0x003b:
            r3 = new android.graphics.drawable.BitmapDrawable;
            r3.<init>(r0);
            goto L_0x0042;
        L_0x0041:
            r3 = 0;
        L_0x0042:
            r1.onPostExecute(r3);
            goto L_0x09b8;
        L_0x0047:
            r0 = move-exception;
            monitor-exit(r2);	 Catch:{ all -> 0x0047 }
            throw r0;
        L_0x004a:
            r2 = r0.lottieFile;
            r4 = 3;
            r5 = 8;
            r6 = 2;
            r7 = 1;
            r8 = 0;
            if (r2 == 0) goto L_0x0157;
        L_0x0054:
            r2 = r1.sync;
            monitor-enter(r2);
            r0 = r1.isCancelled;	 Catch:{ all -> 0x0154 }
            if (r0 == 0) goto L_0x005d;
        L_0x005b:
            monitor-exit(r2);	 Catch:{ all -> 0x0154 }
            return;
        L_0x005d:
            monitor-exit(r2);	 Catch:{ all -> 0x0154 }
            r0 = NUM; // 0x432a999a float:170.6 double:5.56745435E-315;
            r2 = org.telegram.messenger.AndroidUtilities.dp(r0);
            r9 = 512; // 0x200 float:7.175E-43 double:2.53E-321;
            r2 = java.lang.Math.min(r9, r2);
            r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
            r0 = java.lang.Math.min(r9, r0);
            r10 = r1.cacheImage;
            r10 = r10.filter;
            if (r10 == 0) goto L_0x013c;
        L_0x0079:
            r11 = "_";
            r10 = r10.split(r11);
            r11 = r10.length;
            if (r11 < r6) goto L_0x00c0;
        L_0x0082:
            r0 = r10[r8];
            r0 = java.lang.Float.parseFloat(r0);
            r2 = r10[r7];
            r2 = java.lang.Float.parseFloat(r2);
            r11 = org.telegram.messenger.AndroidUtilities.density;
            r11 = r11 * r0;
            r11 = (int) r11;
            r11 = java.lang.Math.min(r9, r11);
            r12 = org.telegram.messenger.AndroidUtilities.density;
            r12 = r12 * r2;
            r12 = (int) r12;
            r9 = java.lang.Math.min(r9, r12);
            r12 = NUM; // 0x42b40000 float:90.0 double:5.529052754E-315;
            r0 = (r0 > r12 ? 1 : (r0 == r12 ? 0 : -1));
            if (r0 > 0) goto L_0x00be;
        L_0x00a6:
            r0 = (r2 > r12 ? 1 : (r2 == r12 ? 0 : -1));
            if (r0 > 0) goto L_0x00be;
        L_0x00aa:
            r0 = 160; // 0xa0 float:2.24E-43 double:7.9E-322;
            r2 = java.lang.Math.min(r11, r0);
            r0 = java.lang.Math.min(r9, r0);
            r9 = org.telegram.messenger.SharedConfig.getDevicePerfomanceClass();
            if (r9 == r6) goto L_0x00bb;
        L_0x00ba:
            r8 = 1;
        L_0x00bb:
            r9 = r0;
            r0 = 1;
            goto L_0x00c2;
        L_0x00be:
            r2 = r11;
            goto L_0x00c1;
        L_0x00c0:
            r9 = r0;
        L_0x00c1:
            r0 = 0;
        L_0x00c2:
            r11 = r10.length;
            if (r11 < r4) goto L_0x00dc;
        L_0x00c5:
            r11 = r10[r6];
            r12 = "nr";
            r11 = r12.equals(r11);
            if (r11 == 0) goto L_0x00d1;
        L_0x00cf:
            r7 = 2;
            goto L_0x00dc;
        L_0x00d1:
            r6 = r10[r6];
            r11 = "nrs";
            r6 = r11.equals(r6);
            if (r6 == 0) goto L_0x00dc;
        L_0x00db:
            r7 = 3;
        L_0x00dc:
            r4 = r10.length;
            r6 = 5;
            if (r4 < r6) goto L_0x0137;
        L_0x00e0:
            r4 = 4;
            r6 = r10[r4];
            r11 = "c1";
            r6 = r11.equals(r6);
            if (r6 == 0) goto L_0x00f7;
        L_0x00eb:
            r3 = new int[r5];
            r3 = {16219713, 13275258, 16757049, 15582629, 16765248, 16245699, 16768889, 16510934};
        L_0x00f0:
            r15 = r0;
            r12 = r2;
            r16 = r3;
            r14 = r8;
            r13 = r9;
            goto L_0x0142;
        L_0x00f7:
            r6 = r10[r4];
            r11 = "c2";
            r6 = r11.equals(r6);
            if (r6 == 0) goto L_0x0107;
        L_0x0101:
            r3 = new int[r5];
            r3 = {16219713, 11172960, 16757049, 13150599, 16765248, 14534815, 16768889, 15128242};
            goto L_0x00f0;
        L_0x0107:
            r6 = r10[r4];
            r11 = "c3";
            r6 = r11.equals(r6);
            if (r6 == 0) goto L_0x0117;
        L_0x0111:
            r3 = new int[r5];
            r3 = {16219713, 9199944, 16757049, 11371874, 16765248, 12885622, 16768889, 13939080};
            goto L_0x00f0;
        L_0x0117:
            r6 = r10[r4];
            r11 = "c4";
            r6 = r11.equals(r6);
            if (r6 == 0) goto L_0x0127;
        L_0x0121:
            r3 = new int[r5];
            r3 = {16219713, 7224364, 16757049, 9591348, 16765248, 10579526, 16768889, 11303506};
            goto L_0x00f0;
        L_0x0127:
            r4 = r10[r4];
            r6 = "c5";
            r4 = r6.equals(r4);
            if (r4 == 0) goto L_0x0137;
        L_0x0131:
            r3 = new int[r5];
            r3 = {16219713, 2694162, 16757049, 4663842, 16765248, 5716784, 16768889, 6834492};
            goto L_0x00f0;
        L_0x0137:
            r15 = r0;
            r12 = r2;
            r14 = r8;
            r13 = r9;
            goto L_0x0140;
        L_0x013c:
            r13 = r0;
            r12 = r2;
            r14 = 0;
            r15 = 0;
        L_0x0140:
            r16 = 0;
        L_0x0142:
            r0 = new org.telegram.ui.Components.RLottieDrawable;
            r2 = r1.cacheImage;
            r11 = r2.finalFilePath;
            r10 = r0;
            r10.<init>(r11, r12, r13, r14, r15, r16);
            r0.setAutoRepeat(r7);
            r1.onPostExecute(r0);
            goto L_0x09b8;
        L_0x0154:
            r0 = move-exception;
            monitor-exit(r2);	 Catch:{ all -> 0x0154 }
            throw r0;
        L_0x0157:
            r2 = r0.animatedFile;
            if (r2 == 0) goto L_0x01c2;
        L_0x015b:
            r2 = r1.sync;
            monitor-enter(r2);
            r0 = r1.isCancelled;	 Catch:{ all -> 0x01bf }
            if (r0 == 0) goto L_0x0164;
        L_0x0162:
            monitor-exit(r2);	 Catch:{ all -> 0x01bf }
            return;
        L_0x0164:
            monitor-exit(r2);	 Catch:{ all -> 0x01bf }
            r0 = r1.cacheImage;
            r0 = r0.filter;
            r2 = "g";
            r0 = r2.equals(r0);
            if (r0 == 0) goto L_0x0197;
        L_0x0171:
            r0 = r1.cacheImage;
            r2 = r0.imageLocation;
            r2 = r2.document;
            r4 = r2 instanceof org.telegram.tgnet.TLRPC.TL_documentEncrypted;
            if (r4 != 0) goto L_0x0197;
        L_0x017b:
            r4 = new org.telegram.ui.Components.AnimatedFileDrawable;
            r6 = r0.finalFilePath;
            r7 = 0;
            r0 = r0.size;
            r8 = (long) r0;
            r0 = r2 instanceof org.telegram.tgnet.TLRPC.Document;
            if (r0 == 0) goto L_0x0189;
        L_0x0187:
            r10 = r2;
            goto L_0x018a;
        L_0x0189:
            r10 = 0;
        L_0x018a:
            r0 = r1.cacheImage;
            r11 = r0.parentObject;
            r12 = r0.currentAccount;
            r13 = 0;
            r5 = r4;
            r5.<init>(r6, r7, r8, r10, r11, r12, r13);
            r0 = r4;
            goto L_0x01b7;
        L_0x0197:
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
            r22 = 0;
            r14 = r0;
            r21 = r2;
            r14.<init>(r15, r16, r17, r19, r20, r21, r22);
        L_0x01b7:
            java.lang.Thread.interrupted();
            r1.onPostExecute(r0);
            goto L_0x09b8;
        L_0x01bf:
            r0 = move-exception;
            monitor-exit(r2);	 Catch:{ all -> 0x01bf }
            throw r0;
        L_0x01c2:
            r2 = r0.finalFilePath;
            r9 = r0.secureDocument;
            if (r9 != 0) goto L_0x01dd;
        L_0x01c8:
            r0 = r0.encryptionKeyPath;
            if (r0 == 0) goto L_0x01db;
        L_0x01cc:
            if (r2 == 0) goto L_0x01db;
        L_0x01ce:
            r0 = r2.getAbsolutePath();
            r9 = ".enc";
            r0 = r0.endsWith(r9);
            if (r0 == 0) goto L_0x01db;
        L_0x01da:
            goto L_0x01dd;
        L_0x01db:
            r9 = 0;
            goto L_0x01de;
        L_0x01dd:
            r9 = 1;
        L_0x01de:
            r0 = r1.cacheImage;
            r0 = r0.secureDocument;
            if (r0 == 0) goto L_0x01f7;
        L_0x01e4:
            r10 = r0.secureDocumentKey;
            r0 = r0.secureFile;
            if (r0 == 0) goto L_0x01ef;
        L_0x01ea:
            r0 = r0.file_hash;
            if (r0 == 0) goto L_0x01ef;
        L_0x01ee:
            goto L_0x01f5;
        L_0x01ef:
            r0 = r1.cacheImage;
            r0 = r0.secureDocument;
            r0 = r0.fileHash;
        L_0x01f5:
            r11 = r0;
            goto L_0x01f9;
        L_0x01f7:
            r10 = 0;
            r11 = 0;
        L_0x01f9:
            r0 = android.os.Build.VERSION.SDK_INT;
            r12 = 19;
            if (r0 >= r12) goto L_0x0269;
        L_0x01ff:
            r12 = new java.io.RandomAccessFile;	 Catch:{ Exception -> 0x024f, all -> 0x024b }
            r0 = "r";
            r12.<init>(r2, r0);	 Catch:{ Exception -> 0x024f, all -> 0x024b }
            r0 = r1.cacheImage;	 Catch:{ Exception -> 0x0249 }
            r0 = r0.imageType;	 Catch:{ Exception -> 0x0249 }
            if (r0 != r7) goto L_0x0211;
        L_0x020c:
            r0 = org.telegram.messenger.ImageLoader.headerThumb;	 Catch:{ Exception -> 0x0249 }
            goto L_0x0215;
        L_0x0211:
            r0 = org.telegram.messenger.ImageLoader.header;	 Catch:{ Exception -> 0x0249 }
        L_0x0215:
            r13 = r0.length;	 Catch:{ Exception -> 0x0249 }
            r12.readFully(r0, r8, r13);	 Catch:{ Exception -> 0x0249 }
            r13 = new java.lang.String;	 Catch:{ Exception -> 0x0249 }
            r13.<init>(r0);	 Catch:{ Exception -> 0x0249 }
            r0 = r13.toLowerCase();	 Catch:{ Exception -> 0x0249 }
            r0 = r0.toLowerCase();	 Catch:{ Exception -> 0x0249 }
            r13 = "riff";
            r13 = r0.startsWith(r13);	 Catch:{ Exception -> 0x0249 }
            if (r13 == 0) goto L_0x0239;
        L_0x022e:
            r13 = "webp";
            r0 = r0.endsWith(r13);	 Catch:{ Exception -> 0x0249 }
            if (r0 == 0) goto L_0x0239;
        L_0x0237:
            r13 = 1;
            goto L_0x023a;
        L_0x0239:
            r13 = 0;
        L_0x023a:
            r12.close();	 Catch:{ Exception -> 0x0247 }
            r12.close();	 Catch:{ Exception -> 0x0241 }
            goto L_0x026a;
        L_0x0241:
            r0 = move-exception;
            r12 = r0;
            org.telegram.messenger.FileLog.e(r12);
            goto L_0x026a;
        L_0x0247:
            r0 = move-exception;
            goto L_0x0252;
        L_0x0249:
            r0 = move-exception;
            goto L_0x0251;
        L_0x024b:
            r0 = move-exception;
            r2 = r0;
            r12 = 0;
            goto L_0x025d;
        L_0x024f:
            r0 = move-exception;
            r12 = 0;
        L_0x0251:
            r13 = 0;
        L_0x0252:
            org.telegram.messenger.FileLog.e(r0);	 Catch:{ all -> 0x025b }
            if (r12 == 0) goto L_0x026a;
        L_0x0257:
            r12.close();	 Catch:{ Exception -> 0x0241 }
            goto L_0x026a;
        L_0x025b:
            r0 = move-exception;
            r2 = r0;
        L_0x025d:
            if (r12 == 0) goto L_0x0268;
        L_0x025f:
            r12.close();	 Catch:{ Exception -> 0x0263 }
            goto L_0x0268;
        L_0x0263:
            r0 = move-exception;
            r3 = r0;
            org.telegram.messenger.FileLog.e(r3);
        L_0x0268:
            throw r2;
        L_0x0269:
            r13 = 0;
        L_0x026a:
            r0 = r1.cacheImage;
            r0 = r0.imageLocation;
            r0 = r0.path;
            if (r0 == 0) goto L_0x02cf;
        L_0x0272:
            r12 = "thumb://";
            r12 = r0.startsWith(r12);
            if (r12 == 0) goto L_0x029b;
        L_0x027a:
            r12 = ":";
            r12 = r0.indexOf(r12, r5);
            if (r12 < 0) goto L_0x0294;
        L_0x0282:
            r14 = r0.substring(r5, r12);
            r14 = java.lang.Long.parseLong(r14);
            r14 = java.lang.Long.valueOf(r14);
            r12 = r12 + r7;
            r0 = r0.substring(r12);
            goto L_0x0296;
        L_0x0294:
            r0 = 0;
            r14 = 0;
        L_0x0296:
            r12 = r0;
        L_0x0297:
            r15 = 0;
        L_0x0298:
            r16 = 0;
            goto L_0x02d4;
        L_0x029b:
            r12 = "vthumb://";
            r12 = r0.startsWith(r12);
            if (r12 == 0) goto L_0x02c4;
        L_0x02a4:
            r12 = 9;
            r14 = ":";
            r12 = r0.indexOf(r14, r12);
            if (r12 < 0) goto L_0x02be;
        L_0x02ae:
            r14 = 9;
            r0 = r0.substring(r14, r12);
            r14 = java.lang.Long.parseLong(r0);
            r0 = java.lang.Long.valueOf(r14);
            r12 = 1;
            goto L_0x02c0;
        L_0x02be:
            r0 = 0;
            r12 = 0;
        L_0x02c0:
            r14 = r0;
            r15 = r12;
            r12 = 0;
            goto L_0x0298;
        L_0x02c4:
            r12 = "http";
            r0 = r0.startsWith(r12);
            if (r0 != 0) goto L_0x02cf;
        L_0x02cc:
            r12 = 0;
            r14 = 0;
            goto L_0x0297;
        L_0x02cf:
            r12 = 0;
            r14 = 0;
            r15 = 0;
            r16 = 1;
        L_0x02d4:
            r5 = new android.graphics.BitmapFactory$Options;
            r5.<init>();
            r5.inSampleSize = r7;
            r0 = android.os.Build.VERSION.SDK_INT;
            r4 = 21;
            if (r0 >= r4) goto L_0x02e3;
        L_0x02e1:
            r5.inPurgeable = r7;
        L_0x02e3:
            r0 = org.telegram.messenger.ImageLoader.this;
            r19 = r0.canForce8888;
            r20 = 0;
            r0 = r1.cacheImage;	 Catch:{ all -> 0x04dd }
            r0 = r0.filter;	 Catch:{ all -> 0x04dd }
            r21 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
            if (r0 == 0) goto L_0x0485;
        L_0x02f3:
            r0 = r1.cacheImage;	 Catch:{ all -> 0x04dd }
            r0 = r0.filter;	 Catch:{ all -> 0x04dd }
            r4 = "_";
            r0 = r0.split(r4);	 Catch:{ all -> 0x04dd }
            r4 = r0.length;	 Catch:{ all -> 0x04dd }
            if (r4 < r6) goto L_0x031e;
        L_0x0300:
            r4 = r0[r8];	 Catch:{ all -> 0x04dd }
            r4 = java.lang.Float.parseFloat(r4);	 Catch:{ all -> 0x04dd }
            r23 = org.telegram.messenger.AndroidUtilities.density;	 Catch:{ all -> 0x04dd }
            r4 = r4 * r23;
            r0 = r0[r7];	 Catch:{ all -> 0x0317 }
            r0 = java.lang.Float.parseFloat(r0);	 Catch:{ all -> 0x0317 }
            r23 = org.telegram.messenger.AndroidUtilities.density;	 Catch:{ all -> 0x0317 }
            r0 = r0 * r23;
            r23 = r0;
            goto L_0x0321;
        L_0x0317:
            r0 = move-exception;
            r3 = r4;
            r25 = r12;
            r8 = r13;
            goto L_0x04e2;
        L_0x031e:
            r4 = 0;
            r23 = 0;
        L_0x0321:
            r0 = r1.cacheImage;	 Catch:{ all -> 0x047d }
            r0 = r0.filter;	 Catch:{ all -> 0x047d }
            r6 = "b2";
            r0 = r0.contains(r6);	 Catch:{ all -> 0x047d }
            if (r0 == 0) goto L_0x032f;
        L_0x032d:
            r6 = 3;
            goto L_0x034c;
        L_0x032f:
            r0 = r1.cacheImage;	 Catch:{ all -> 0x047d }
            r0 = r0.filter;	 Catch:{ all -> 0x047d }
            r6 = "b1";
            r0 = r0.contains(r6);	 Catch:{ all -> 0x047d }
            if (r0 == 0) goto L_0x033d;
        L_0x033b:
            r6 = 2;
            goto L_0x034c;
        L_0x033d:
            r0 = r1.cacheImage;	 Catch:{ all -> 0x047d }
            r0 = r0.filter;	 Catch:{ all -> 0x047d }
            r6 = "b";
            r0 = r0.contains(r6);	 Catch:{ all -> 0x047d }
            if (r0 == 0) goto L_0x034b;
        L_0x0349:
            r6 = 1;
            goto L_0x034c;
        L_0x034b:
            r6 = 0;
        L_0x034c:
            r0 = r1.cacheImage;	 Catch:{ all -> 0x0475 }
            r0 = r0.filter;	 Catch:{ all -> 0x0475 }
            r3 = "i";
            r3 = r0.contains(r3);	 Catch:{ all -> 0x0475 }
            r0 = r1.cacheImage;	 Catch:{ all -> 0x046b }
            r0 = r0.filter;	 Catch:{ all -> 0x046b }
            r8 = "f";
            r0 = r0.contains(r8);	 Catch:{ all -> 0x046b }
            if (r0 == 0) goto L_0x0364;
        L_0x0362:
            r19 = 1;
        L_0x0364:
            if (r13 != 0) goto L_0x0462;
        L_0x0366:
            r0 = (r4 > r20 ? 1 : (r4 == r20 ? 0 : -1));
            if (r0 == 0) goto L_0x0462;
        L_0x036a:
            r0 = (r23 > r20 ? 1 : (r23 == r20 ? 0 : -1));
            if (r0 == 0) goto L_0x0462;
        L_0x036e:
            r5.inJustDecodeBounds = r7;	 Catch:{ all -> 0x046b }
            if (r14 == 0) goto L_0x03a1;
        L_0x0372:
            if (r12 != 0) goto L_0x03a1;
        L_0x0374:
            if (r15 == 0) goto L_0x038d;
        L_0x0376:
            r0 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ all -> 0x0387 }
            r0 = r0.getContentResolver();	 Catch:{ all -> 0x0387 }
            r25 = r12;
            r8 = r13;
            r12 = r14.longValue();	 Catch:{ all -> 0x03f8 }
            android.provider.MediaStore.Video.Thumbnails.getThumbnail(r0, r12, r7, r5);	 Catch:{ all -> 0x03f8 }
            goto L_0x039d;
        L_0x0387:
            r0 = move-exception;
            r25 = r12;
            r8 = r13;
            goto L_0x03f9;
        L_0x038d:
            r25 = r12;
            r8 = r13;
            r0 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ all -> 0x03f8 }
            r0 = r0.getContentResolver();	 Catch:{ all -> 0x03f8 }
            r12 = r14.longValue();	 Catch:{ all -> 0x03f8 }
            android.provider.MediaStore.Images.Thumbnails.getThumbnail(r0, r12, r7, r5);	 Catch:{ all -> 0x03f8 }
        L_0x039d:
            r26 = r3;
            goto L_0x0417;
        L_0x03a1:
            r25 = r12;
            r8 = r13;
            if (r10 == 0) goto L_0x03fd;
        L_0x03a6:
            r0 = new java.io.RandomAccessFile;	 Catch:{ all -> 0x03f8 }
            r12 = "r";
            r0.<init>(r2, r12);	 Catch:{ all -> 0x03f8 }
            r12 = r0.length();	 Catch:{ all -> 0x03f8 }
            r13 = (int) r12;	 Catch:{ all -> 0x03f8 }
            r12 = org.telegram.messenger.ImageLoader.bytesLocal;	 Catch:{ all -> 0x03f8 }
            r12 = r12.get();	 Catch:{ all -> 0x03f8 }
            r12 = (byte[]) r12;	 Catch:{ all -> 0x03f8 }
            if (r12 == 0) goto L_0x03c2;
        L_0x03be:
            r7 = r12.length;	 Catch:{ all -> 0x03f8 }
            if (r7 < r13) goto L_0x03c2;
        L_0x03c1:
            goto L_0x03c3;
        L_0x03c2:
            r12 = 0;
        L_0x03c3:
            if (r12 != 0) goto L_0x03ce;
        L_0x03c5:
            r12 = new byte[r13];	 Catch:{ all -> 0x03f8 }
            r7 = org.telegram.messenger.ImageLoader.bytesLocal;	 Catch:{ all -> 0x03f8 }
            r7.set(r12);	 Catch:{ all -> 0x03f8 }
        L_0x03ce:
            r7 = 0;
            r0.readFully(r12, r7, r13);	 Catch:{ all -> 0x03f8 }
            r0.close();	 Catch:{ all -> 0x03f8 }
            org.telegram.messenger.secretmedia.EncryptedFileInputStream.decryptBytesWithKeyFile(r12, r7, r13, r10);	 Catch:{ all -> 0x03f8 }
            r0 = org.telegram.messenger.Utilities.computeSHA256(r12, r7, r13);	 Catch:{ all -> 0x03f8 }
            if (r11 == 0) goto L_0x03e9;
        L_0x03de:
            r0 = java.util.Arrays.equals(r0, r11);	 Catch:{ all -> 0x03f8 }
            if (r0 != 0) goto L_0x03e5;
        L_0x03e4:
            goto L_0x03e9;
        L_0x03e5:
            r26 = r3;
            r0 = 0;
            goto L_0x03ec;
        L_0x03e9:
            r26 = r3;
            r0 = 1;
        L_0x03ec:
            r7 = 0;
            r3 = r12[r7];	 Catch:{ all -> 0x0460 }
            r3 = r3 & 255;
            r13 = r13 - r3;
            if (r0 != 0) goto L_0x0417;
        L_0x03f4:
            android.graphics.BitmapFactory.decodeByteArray(r12, r3, r13, r5);	 Catch:{ all -> 0x0460 }
            goto L_0x0417;
        L_0x03f8:
            r0 = move-exception;
        L_0x03f9:
            r26 = r3;
            goto L_0x0471;
        L_0x03fd:
            r26 = r3;
            if (r9 == 0) goto L_0x040b;
        L_0x0401:
            r0 = new org.telegram.messenger.secretmedia.EncryptedFileInputStream;	 Catch:{ all -> 0x0460 }
            r3 = r1.cacheImage;	 Catch:{ all -> 0x0460 }
            r3 = r3.encryptionKeyPath;	 Catch:{ all -> 0x0460 }
            r0.<init>(r2, r3);	 Catch:{ all -> 0x0460 }
            goto L_0x0410;
        L_0x040b:
            r0 = new java.io.FileInputStream;	 Catch:{ all -> 0x0460 }
            r0.<init>(r2);	 Catch:{ all -> 0x0460 }
        L_0x0410:
            r3 = 0;
            android.graphics.BitmapFactory.decodeStream(r0, r3, r5);	 Catch:{ all -> 0x0460 }
            r0.close();	 Catch:{ all -> 0x0460 }
        L_0x0417:
            r0 = r5.outWidth;	 Catch:{ all -> 0x0460 }
            r0 = (float) r0;	 Catch:{ all -> 0x0460 }
            r3 = r5.outHeight;	 Catch:{ all -> 0x0460 }
            r3 = (float) r3;	 Catch:{ all -> 0x0460 }
            r7 = (r4 > r23 ? 1 : (r4 == r23 ? 0 : -1));
            if (r7 < 0) goto L_0x042e;
        L_0x0421:
            r7 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1));
            if (r7 <= 0) goto L_0x042e;
        L_0x0425:
            r7 = r0 / r4;
            r12 = r3 / r23;
            r7 = java.lang.Math.max(r7, r12);	 Catch:{ all -> 0x0460 }
            goto L_0x0436;
        L_0x042e:
            r7 = r0 / r4;
            r12 = r3 / r23;
            r7 = java.lang.Math.min(r7, r12);	 Catch:{ all -> 0x0460 }
        L_0x0436:
            r12 = NUM; // 0x3var_a float:1.2 double:5.271833295E-315;
            r12 = (r7 > r12 ? 1 : (r7 == r12 ? 0 : -1));
            if (r12 >= 0) goto L_0x043f;
        L_0x043d:
            r7 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        L_0x043f:
            r12 = 0;
            r5.inJustDecodeBounds = r12;	 Catch:{ all -> 0x0460 }
            r12 = (r7 > r21 ? 1 : (r7 == r21 ? 0 : -1));
            if (r12 <= 0) goto L_0x045c;
        L_0x0446:
            r0 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1));
            if (r0 > 0) goto L_0x044e;
        L_0x044a:
            r0 = (r3 > r23 ? 1 : (r3 == r23 ? 0 : -1));
            if (r0 <= 0) goto L_0x045c;
        L_0x044e:
            r0 = 1;
        L_0x044f:
            r3 = 2;
            r0 = r0 * 2;
            r3 = r0 * 2;
            r3 = (float) r3;	 Catch:{ all -> 0x0460 }
            r3 = (r3 > r7 ? 1 : (r3 == r7 ? 0 : -1));
            if (r3 < 0) goto L_0x044f;
        L_0x0459:
            r5.inSampleSize = r0;	 Catch:{ all -> 0x0460 }
            goto L_0x0467;
        L_0x045c:
            r0 = (int) r7;	 Catch:{ all -> 0x0460 }
            r5.inSampleSize = r0;	 Catch:{ all -> 0x0460 }
            goto L_0x0467;
        L_0x0460:
            r0 = move-exception;
            goto L_0x0471;
        L_0x0462:
            r26 = r3;
            r25 = r12;
            r8 = r13;
        L_0x0467:
            r0 = r4;
            r4 = 0;
            goto L_0x04ec;
        L_0x046b:
            r0 = move-exception;
            r26 = r3;
            r25 = r12;
            r8 = r13;
        L_0x0471:
            r3 = r4;
            r4 = 0;
            goto L_0x04e8;
        L_0x0475:
            r0 = move-exception;
            r25 = r12;
            r8 = r13;
            r3 = r4;
            r4 = 0;
            goto L_0x04e6;
        L_0x047d:
            r0 = move-exception;
            r25 = r12;
            r8 = r13;
            r3 = r4;
            r4 = 0;
            r6 = 0;
            goto L_0x04e6;
        L_0x0485:
            r25 = r12;
            r8 = r13;
            if (r25 == 0) goto L_0x04d5;
        L_0x048a:
            r3 = 1;
            r5.inJustDecodeBounds = r3;	 Catch:{ all -> 0x04d3 }
            if (r19 == 0) goto L_0x0492;
        L_0x048f:
            r0 = android.graphics.Bitmap.Config.ARGB_8888;	 Catch:{ all -> 0x04d3 }
            goto L_0x0494;
        L_0x0492:
            r0 = android.graphics.Bitmap.Config.RGB_565;	 Catch:{ all -> 0x04d3 }
        L_0x0494:
            r5.inPreferredConfig = r0;	 Catch:{ all -> 0x04d3 }
            r0 = new java.io.FileInputStream;	 Catch:{ all -> 0x04d3 }
            r0.<init>(r2);	 Catch:{ all -> 0x04d3 }
            r3 = 0;
            r4 = android.graphics.BitmapFactory.decodeStream(r0, r3, r5);	 Catch:{ all -> 0x04d3 }
            r0.close();	 Catch:{ all -> 0x04d0 }
            r0 = r5.outWidth;	 Catch:{ all -> 0x04d0 }
            r3 = r5.outHeight;	 Catch:{ all -> 0x04d0 }
            r6 = 0;
            r5.inJustDecodeBounds = r6;	 Catch:{ all -> 0x04d0 }
            r0 = r0 / 200;
            r3 = r3 / 200;
            r0 = java.lang.Math.max(r0, r3);	 Catch:{ all -> 0x04d0 }
            r0 = (float) r0;	 Catch:{ all -> 0x04d0 }
            r3 = (r0 > r21 ? 1 : (r0 == r21 ? 0 : -1));
            if (r3 >= 0) goto L_0x04b9;
        L_0x04b7:
            r0 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        L_0x04b9:
            r3 = (r0 > r21 ? 1 : (r0 == r21 ? 0 : -1));
            if (r3 <= 0) goto L_0x04cb;
        L_0x04bd:
            r3 = 1;
        L_0x04be:
            r6 = 2;
            r3 = r3 * 2;
            r6 = r3 * 2;
            r6 = (float) r6;	 Catch:{ all -> 0x04d0 }
            r6 = (r6 > r0 ? 1 : (r6 == r0 ? 0 : -1));
            if (r6 < 0) goto L_0x04be;
        L_0x04c8:
            r5.inSampleSize = r3;	 Catch:{ all -> 0x04d0 }
            goto L_0x04ce;
        L_0x04cb:
            r0 = (int) r0;	 Catch:{ all -> 0x04d0 }
            r5.inSampleSize = r0;	 Catch:{ all -> 0x04d0 }
        L_0x04ce:
            r0 = 0;
            goto L_0x04d7;
        L_0x04d0:
            r0 = move-exception;
            r3 = 0;
            goto L_0x04e3;
        L_0x04d3:
            r0 = move-exception;
            goto L_0x04e1;
        L_0x04d5:
            r0 = 0;
            r4 = 0;
        L_0x04d7:
            r6 = 0;
            r23 = 0;
            r26 = 0;
            goto L_0x04ec;
        L_0x04dd:
            r0 = move-exception;
            r25 = r12;
            r8 = r13;
        L_0x04e1:
            r3 = 0;
        L_0x04e2:
            r4 = 0;
        L_0x04e3:
            r6 = 0;
            r23 = 0;
        L_0x04e6:
            r26 = 0;
        L_0x04e8:
            org.telegram.messenger.FileLog.e(r0);
            r0 = r3;
        L_0x04ec:
            r3 = r23;
            r7 = r1.cacheImage;
            r7 = r7.imageType;
            r12 = 1;
            if (r7 != r12) goto L_0x06ee;
        L_0x04f5:
            r3 = org.telegram.messenger.ImageLoader.this;	 Catch:{ all -> 0x06e5 }
            r12 = java.lang.System.currentTimeMillis();	 Catch:{ all -> 0x06e5 }
            r3.lastCacheOutTime = r12;	 Catch:{ all -> 0x06e5 }
            r3 = r1.sync;	 Catch:{ all -> 0x06e5 }
            monitor-enter(r3);	 Catch:{ all -> 0x06e5 }
            r7 = r1.isCancelled;	 Catch:{ all -> 0x06e2 }
            if (r7 == 0) goto L_0x0507;
        L_0x0505:
            monitor-exit(r3);	 Catch:{ all -> 0x06e2 }
            return;
        L_0x0507:
            monitor-exit(r3);	 Catch:{ all -> 0x06e2 }
            if (r8 == 0) goto L_0x054f;
        L_0x050a:
            r3 = new java.io.RandomAccessFile;	 Catch:{ all -> 0x06e5 }
            r7 = "r";
            r3.<init>(r2, r7);	 Catch:{ all -> 0x06e5 }
            r8 = r3.getChannel();	 Catch:{ all -> 0x06e5 }
            r9 = java.nio.channels.FileChannel.MapMode.READ_ONLY;	 Catch:{ all -> 0x06e5 }
            r10 = 0;
            r12 = r2.length();	 Catch:{ all -> 0x06e5 }
            r7 = r8.map(r9, r10, r12);	 Catch:{ all -> 0x06e5 }
            r8 = new android.graphics.BitmapFactory$Options;	 Catch:{ all -> 0x06e5 }
            r8.<init>();	 Catch:{ all -> 0x06e5 }
            r9 = 1;
            r8.inJustDecodeBounds = r9;	 Catch:{ all -> 0x06e5 }
            r10 = r7.limit();	 Catch:{ all -> 0x06e5 }
            r11 = 0;
            org.telegram.messenger.Utilities.loadWebpImage(r11, r7, r10, r8, r9);	 Catch:{ all -> 0x06e5 }
            r9 = r8.outWidth;	 Catch:{ all -> 0x06e5 }
            r8 = r8.outHeight;	 Catch:{ all -> 0x06e5 }
            r10 = android.graphics.Bitmap.Config.ARGB_8888;	 Catch:{ all -> 0x06e5 }
            r4 = org.telegram.messenger.Bitmaps.createBitmap(r9, r8, r10);	 Catch:{ all -> 0x06e5 }
            r8 = r7.limit();	 Catch:{ all -> 0x06e5 }
            r9 = r5.inPurgeable;	 Catch:{ all -> 0x06e5 }
            if (r9 != 0) goto L_0x0545;
        L_0x0543:
            r9 = 1;
            goto L_0x0546;
        L_0x0545:
            r9 = 0;
        L_0x0546:
            r10 = 0;
            org.telegram.messenger.Utilities.loadWebpImage(r4, r7, r8, r10, r9);	 Catch:{ all -> 0x06e5 }
            r3.close();	 Catch:{ all -> 0x06e5 }
            goto L_0x05cd;
        L_0x054f:
            r3 = r5.inPurgeable;	 Catch:{ all -> 0x06e5 }
            if (r3 != 0) goto L_0x0570;
        L_0x0553:
            if (r10 == 0) goto L_0x0556;
        L_0x0555:
            goto L_0x0570;
        L_0x0556:
            if (r9 == 0) goto L_0x0562;
        L_0x0558:
            r3 = new org.telegram.messenger.secretmedia.EncryptedFileInputStream;	 Catch:{ all -> 0x06e5 }
            r7 = r1.cacheImage;	 Catch:{ all -> 0x06e5 }
            r7 = r7.encryptionKeyPath;	 Catch:{ all -> 0x06e5 }
            r3.<init>(r2, r7);	 Catch:{ all -> 0x06e5 }
            goto L_0x0567;
        L_0x0562:
            r3 = new java.io.FileInputStream;	 Catch:{ all -> 0x06e5 }
            r3.<init>(r2);	 Catch:{ all -> 0x06e5 }
        L_0x0567:
            r7 = 0;
            r4 = android.graphics.BitmapFactory.decodeStream(r3, r7, r5);	 Catch:{ all -> 0x06e5 }
            r3.close();	 Catch:{ all -> 0x06e5 }
            goto L_0x05cd;
        L_0x0570:
            r3 = new java.io.RandomAccessFile;	 Catch:{ all -> 0x06e5 }
            r7 = "r";
            r3.<init>(r2, r7);	 Catch:{ all -> 0x06e5 }
            r7 = r3.length();	 Catch:{ all -> 0x06e5 }
            r8 = (int) r7;	 Catch:{ all -> 0x06e5 }
            r7 = org.telegram.messenger.ImageLoader.bytesThumbLocal;	 Catch:{ all -> 0x06e5 }
            r7 = r7.get();	 Catch:{ all -> 0x06e5 }
            r7 = (byte[]) r7;	 Catch:{ all -> 0x06e5 }
            if (r7 == 0) goto L_0x058c;
        L_0x0588:
            r12 = r7.length;	 Catch:{ all -> 0x06e5 }
            if (r12 < r8) goto L_0x058c;
        L_0x058b:
            goto L_0x058d;
        L_0x058c:
            r7 = 0;
        L_0x058d:
            if (r7 != 0) goto L_0x0598;
        L_0x058f:
            r7 = new byte[r8];	 Catch:{ all -> 0x06e5 }
            r12 = org.telegram.messenger.ImageLoader.bytesThumbLocal;	 Catch:{ all -> 0x06e5 }
            r12.set(r7);	 Catch:{ all -> 0x06e5 }
        L_0x0598:
            r12 = 0;
            r3.readFully(r7, r12, r8);	 Catch:{ all -> 0x06e5 }
            r3.close();	 Catch:{ all -> 0x06e5 }
            if (r10 == 0) goto L_0x05bb;
        L_0x05a1:
            org.telegram.messenger.secretmedia.EncryptedFileInputStream.decryptBytesWithKeyFile(r7, r12, r8, r10);	 Catch:{ all -> 0x06e5 }
            r3 = org.telegram.messenger.Utilities.computeSHA256(r7, r12, r8);	 Catch:{ all -> 0x06e5 }
            if (r11 == 0) goto L_0x05b3;
        L_0x05aa:
            r3 = java.util.Arrays.equals(r3, r11);	 Catch:{ all -> 0x06e5 }
            if (r3 != 0) goto L_0x05b1;
        L_0x05b0:
            goto L_0x05b3;
        L_0x05b1:
            r3 = 0;
            goto L_0x05b4;
        L_0x05b3:
            r3 = 1;
        L_0x05b4:
            r9 = 0;
            r10 = r7[r9];	 Catch:{ all -> 0x06e5 }
            r9 = r10 & 255;
            r8 = r8 - r9;
            goto L_0x05c7;
        L_0x05bb:
            if (r9 == 0) goto L_0x05c5;
        L_0x05bd:
            r3 = r1.cacheImage;	 Catch:{ all -> 0x06e5 }
            r3 = r3.encryptionKeyPath;	 Catch:{ all -> 0x06e5 }
            r9 = 0;
            org.telegram.messenger.secretmedia.EncryptedFileInputStream.decryptBytesWithKeyFile(r7, r9, r8, r3);	 Catch:{ all -> 0x06e5 }
        L_0x05c5:
            r3 = 0;
            r9 = 0;
        L_0x05c7:
            if (r3 != 0) goto L_0x05cd;
        L_0x05c9:
            r4 = android.graphics.BitmapFactory.decodeByteArray(r7, r9, r8, r5);	 Catch:{ all -> 0x06e5 }
        L_0x05cd:
            if (r4 != 0) goto L_0x05e4;
        L_0x05cf:
            r5 = r2.length();	 Catch:{ all -> 0x06e5 }
            r7 = 0;
            r0 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1));
            if (r0 == 0) goto L_0x05df;
        L_0x05d9:
            r0 = r1.cacheImage;	 Catch:{ all -> 0x06e5 }
            r0 = r0.filter;	 Catch:{ all -> 0x06e5 }
            if (r0 != 0) goto L_0x0725;
        L_0x05df:
            r2.delete();	 Catch:{ all -> 0x06e5 }
            goto L_0x0725;
        L_0x05e4:
            r2 = r1.cacheImage;	 Catch:{ all -> 0x06e5 }
            r2 = r2.filter;	 Catch:{ all -> 0x06e5 }
            if (r2 == 0) goto L_0x0616;
        L_0x05ea:
            r2 = r4.getWidth();	 Catch:{ all -> 0x06e5 }
            r2 = (float) r2;	 Catch:{ all -> 0x06e5 }
            r3 = r4.getHeight();	 Catch:{ all -> 0x06e5 }
            r3 = (float) r3;	 Catch:{ all -> 0x06e5 }
            r7 = r5.inPurgeable;	 Catch:{ all -> 0x06e5 }
            if (r7 != 0) goto L_0x0616;
        L_0x05f8:
            r7 = (r0 > r20 ? 1 : (r0 == r20 ? 0 : -1));
            if (r7 == 0) goto L_0x0616;
        L_0x05fc:
            r7 = (r2 > r0 ? 1 : (r2 == r0 ? 0 : -1));
            if (r7 == 0) goto L_0x0616;
        L_0x0600:
            r7 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
            r7 = r7 + r0;
            r7 = (r2 > r7 ? 1 : (r2 == r7 ? 0 : -1));
            if (r7 <= 0) goto L_0x0616;
        L_0x0607:
            r2 = r2 / r0;
            r0 = (int) r0;	 Catch:{ all -> 0x06e5 }
            r3 = r3 / r2;
            r2 = (int) r3;	 Catch:{ all -> 0x06e5 }
            r3 = 1;
            r0 = org.telegram.messenger.Bitmaps.createScaledBitmap(r4, r0, r2, r3);	 Catch:{ all -> 0x06e5 }
            if (r4 == r0) goto L_0x0616;
        L_0x0612:
            r4.recycle();	 Catch:{ all -> 0x06e5 }
            r4 = r0;
        L_0x0616:
            if (r26 == 0) goto L_0x0636;
        L_0x0618:
            r0 = r5.inPurgeable;	 Catch:{ all -> 0x06e5 }
            if (r0 == 0) goto L_0x061e;
        L_0x061c:
            r0 = 0;
            goto L_0x061f;
        L_0x061e:
            r0 = 1;
        L_0x061f:
            r2 = r4.getWidth();	 Catch:{ all -> 0x06e5 }
            r3 = r4.getHeight();	 Catch:{ all -> 0x06e5 }
            r7 = r4.getRowBytes();	 Catch:{ all -> 0x06e5 }
            r0 = org.telegram.messenger.Utilities.needInvert(r4, r0, r2, r3, r7);	 Catch:{ all -> 0x06e5 }
            if (r0 == 0) goto L_0x0633;
        L_0x0631:
            r8 = 1;
            goto L_0x0634;
        L_0x0633:
            r8 = 0;
        L_0x0634:
            r2 = r8;
            goto L_0x0637;
        L_0x0636:
            r2 = 0;
        L_0x0637:
            r3 = 1;
            if (r6 != r3) goto L_0x065f;
        L_0x063a:
            r0 = r4.getConfig();	 Catch:{ all -> 0x065c }
            r3 = android.graphics.Bitmap.Config.ARGB_8888;	 Catch:{ all -> 0x065c }
            if (r0 != r3) goto L_0x06ea;
        L_0x0642:
            r8 = 3;
            r0 = r5.inPurgeable;	 Catch:{ all -> 0x065c }
            if (r0 == 0) goto L_0x0649;
        L_0x0647:
            r9 = 0;
            goto L_0x064a;
        L_0x0649:
            r9 = 1;
        L_0x064a:
            r10 = r4.getWidth();	 Catch:{ all -> 0x065c }
            r11 = r4.getHeight();	 Catch:{ all -> 0x065c }
            r12 = r4.getRowBytes();	 Catch:{ all -> 0x065c }
            r7 = r4;
            org.telegram.messenger.Utilities.blurBitmap(r7, r8, r9, r10, r11, r12);	 Catch:{ all -> 0x065c }
            goto L_0x06ea;
        L_0x065c:
            r0 = move-exception;
            goto L_0x06e7;
        L_0x065f:
            r3 = 2;
            if (r6 != r3) goto L_0x0684;
        L_0x0662:
            r0 = r4.getConfig();	 Catch:{ all -> 0x065c }
            r3 = android.graphics.Bitmap.Config.ARGB_8888;	 Catch:{ all -> 0x065c }
            if (r0 != r3) goto L_0x06ea;
        L_0x066a:
            r8 = 1;
            r0 = r5.inPurgeable;	 Catch:{ all -> 0x065c }
            if (r0 == 0) goto L_0x0671;
        L_0x066f:
            r9 = 0;
            goto L_0x0672;
        L_0x0671:
            r9 = 1;
        L_0x0672:
            r10 = r4.getWidth();	 Catch:{ all -> 0x065c }
            r11 = r4.getHeight();	 Catch:{ all -> 0x065c }
            r12 = r4.getRowBytes();	 Catch:{ all -> 0x065c }
            r7 = r4;
            org.telegram.messenger.Utilities.blurBitmap(r7, r8, r9, r10, r11, r12);	 Catch:{ all -> 0x065c }
            goto L_0x06ea;
        L_0x0684:
            r3 = 3;
            if (r6 != r3) goto L_0x06d8;
        L_0x0687:
            r0 = r4.getConfig();	 Catch:{ all -> 0x065c }
            r3 = android.graphics.Bitmap.Config.ARGB_8888;	 Catch:{ all -> 0x065c }
            if (r0 != r3) goto L_0x06ea;
        L_0x068f:
            r8 = 7;
            r0 = r5.inPurgeable;	 Catch:{ all -> 0x065c }
            if (r0 == 0) goto L_0x0696;
        L_0x0694:
            r9 = 0;
            goto L_0x0697;
        L_0x0696:
            r9 = 1;
        L_0x0697:
            r10 = r4.getWidth();	 Catch:{ all -> 0x065c }
            r11 = r4.getHeight();	 Catch:{ all -> 0x065c }
            r12 = r4.getRowBytes();	 Catch:{ all -> 0x065c }
            r7 = r4;
            org.telegram.messenger.Utilities.blurBitmap(r7, r8, r9, r10, r11, r12);	 Catch:{ all -> 0x065c }
            r8 = 7;
            r0 = r5.inPurgeable;	 Catch:{ all -> 0x065c }
            if (r0 == 0) goto L_0x06ae;
        L_0x06ac:
            r9 = 0;
            goto L_0x06af;
        L_0x06ae:
            r9 = 1;
        L_0x06af:
            r10 = r4.getWidth();	 Catch:{ all -> 0x065c }
            r11 = r4.getHeight();	 Catch:{ all -> 0x065c }
            r12 = r4.getRowBytes();	 Catch:{ all -> 0x065c }
            r7 = r4;
            org.telegram.messenger.Utilities.blurBitmap(r7, r8, r9, r10, r11, r12);	 Catch:{ all -> 0x065c }
            r8 = 7;
            r0 = r5.inPurgeable;	 Catch:{ all -> 0x065c }
            if (r0 == 0) goto L_0x06c6;
        L_0x06c4:
            r9 = 0;
            goto L_0x06c7;
        L_0x06c6:
            r9 = 1;
        L_0x06c7:
            r10 = r4.getWidth();	 Catch:{ all -> 0x065c }
            r11 = r4.getHeight();	 Catch:{ all -> 0x065c }
            r12 = r4.getRowBytes();	 Catch:{ all -> 0x065c }
            r7 = r4;
            org.telegram.messenger.Utilities.blurBitmap(r7, r8, r9, r10, r11, r12);	 Catch:{ all -> 0x065c }
            goto L_0x06ea;
        L_0x06d8:
            if (r6 != 0) goto L_0x06ea;
        L_0x06da:
            r0 = r5.inPurgeable;	 Catch:{ all -> 0x065c }
            if (r0 == 0) goto L_0x06ea;
        L_0x06de:
            org.telegram.messenger.Utilities.pinBitmap(r4);	 Catch:{ all -> 0x065c }
            goto L_0x06ea;
        L_0x06e2:
            r0 = move-exception;
            monitor-exit(r3);	 Catch:{ all -> 0x06e2 }
            throw r0;	 Catch:{ all -> 0x06e5 }
        L_0x06e5:
            r0 = move-exception;
            r2 = 0;
        L_0x06e7:
            org.telegram.messenger.FileLog.e(r0);
        L_0x06ea:
            r7 = 0;
        L_0x06eb:
            r8 = 0;
            goto L_0x0997;
        L_0x06ee:
            r7 = 20;
            if (r14 == 0) goto L_0x06f3;
        L_0x06f2:
            r7 = 0;
        L_0x06f3:
            if (r7 == 0) goto L_0x0727;
        L_0x06f5:
            r12 = org.telegram.messenger.ImageLoader.this;	 Catch:{ all -> 0x0723 }
            r12 = r12.lastCacheOutTime;	 Catch:{ all -> 0x0723 }
            r23 = 0;
            r21 = (r12 > r23 ? 1 : (r12 == r23 ? 0 : -1));
            if (r21 == 0) goto L_0x0727;
        L_0x0701:
            r12 = org.telegram.messenger.ImageLoader.this;	 Catch:{ all -> 0x0723 }
            r12 = r12.lastCacheOutTime;	 Catch:{ all -> 0x0723 }
            r23 = java.lang.System.currentTimeMillis();	 Catch:{ all -> 0x0723 }
            r21 = r3;
            r27 = r4;
            r3 = (long) r7;
            r23 = r23 - r3;
            r7 = (r12 > r23 ? 1 : (r12 == r23 ? 0 : -1));
            if (r7 <= 0) goto L_0x072b;
        L_0x0716:
            r7 = android.os.Build.VERSION.SDK_INT;	 Catch:{ all -> 0x0720 }
            r12 = 21;
            if (r7 >= r12) goto L_0x072b;
        L_0x071c:
            java.lang.Thread.sleep(r3);	 Catch:{ all -> 0x0720 }
            goto L_0x072b;
        L_0x0720:
            r4 = r27;
            goto L_0x0725;
        L_0x0723:
            r27 = r4;
        L_0x0725:
            r2 = 0;
            goto L_0x06ea;
        L_0x0727:
            r21 = r3;
            r27 = r4;
        L_0x072b:
            r3 = org.telegram.messenger.ImageLoader.this;	 Catch:{ all -> 0x0990 }
            r12 = java.lang.System.currentTimeMillis();	 Catch:{ all -> 0x0990 }
            r3.lastCacheOutTime = r12;	 Catch:{ all -> 0x0990 }
            r3 = r1.sync;	 Catch:{ all -> 0x0990 }
            monitor-enter(r3);	 Catch:{ all -> 0x0990 }
            r4 = r1.isCancelled;	 Catch:{ all -> 0x0989 }
            if (r4 == 0) goto L_0x073d;
        L_0x073b:
            monitor-exit(r3);	 Catch:{ all -> 0x0989 }
            return;
        L_0x073d:
            monitor-exit(r3);	 Catch:{ all -> 0x0989 }
            if (r19 != 0) goto L_0x0756;
        L_0x0740:
            r3 = r1.cacheImage;	 Catch:{ all -> 0x0720 }
            r3 = r3.filter;	 Catch:{ all -> 0x0720 }
            if (r3 == 0) goto L_0x0756;
        L_0x0746:
            if (r6 != 0) goto L_0x0756;
        L_0x0748:
            r3 = r1.cacheImage;	 Catch:{ all -> 0x0720 }
            r3 = r3.imageLocation;	 Catch:{ all -> 0x0720 }
            r3 = r3.path;	 Catch:{ all -> 0x0720 }
            if (r3 == 0) goto L_0x0751;
        L_0x0750:
            goto L_0x0756;
        L_0x0751:
            r3 = android.graphics.Bitmap.Config.RGB_565;	 Catch:{ all -> 0x0720 }
            r5.inPreferredConfig = r3;	 Catch:{ all -> 0x0720 }
            goto L_0x075a;
        L_0x0756:
            r3 = android.graphics.Bitmap.Config.ARGB_8888;	 Catch:{ all -> 0x0990 }
            r5.inPreferredConfig = r3;	 Catch:{ all -> 0x0990 }
        L_0x075a:
            r3 = 0;
            r5.inDither = r3;	 Catch:{ all -> 0x0990 }
            if (r14 == 0) goto L_0x0784;
        L_0x075f:
            if (r25 != 0) goto L_0x0784;
        L_0x0761:
            if (r15 == 0) goto L_0x0773;
        L_0x0763:
            r3 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ all -> 0x0720 }
            r3 = r3.getContentResolver();	 Catch:{ all -> 0x0720 }
            r12 = r14.longValue();	 Catch:{ all -> 0x0720 }
            r4 = 1;
            r3 = android.provider.MediaStore.Video.Thumbnails.getThumbnail(r3, r12, r4, r5);	 Catch:{ all -> 0x0720 }
            goto L_0x0782;
        L_0x0773:
            r3 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ all -> 0x0720 }
            r3 = r3.getContentResolver();	 Catch:{ all -> 0x0720 }
            r12 = r14.longValue();	 Catch:{ all -> 0x0720 }
            r4 = 1;
            r3 = android.provider.MediaStore.Images.Thumbnails.getThumbnail(r3, r12, r4, r5);	 Catch:{ all -> 0x0720 }
        L_0x0782:
            r4 = r3;
            goto L_0x0786;
        L_0x0784:
            r4 = r27;
        L_0x0786:
            if (r4 != 0) goto L_0x0893;
        L_0x0788:
            if (r8 == 0) goto L_0x07d5;
        L_0x078a:
            r3 = new java.io.RandomAccessFile;	 Catch:{ all -> 0x0725 }
            r7 = "r";
            r3.<init>(r2, r7);	 Catch:{ all -> 0x0725 }
            r8 = r3.getChannel();	 Catch:{ all -> 0x0725 }
            r9 = java.nio.channels.FileChannel.MapMode.READ_ONLY;	 Catch:{ all -> 0x0725 }
            r10 = 0;
            r12 = r2.length();	 Catch:{ all -> 0x0725 }
            r7 = r8.map(r9, r10, r12);	 Catch:{ all -> 0x0725 }
            r8 = new android.graphics.BitmapFactory$Options;	 Catch:{ all -> 0x0725 }
            r8.<init>();	 Catch:{ all -> 0x0725 }
            r9 = 1;
            r8.inJustDecodeBounds = r9;	 Catch:{ all -> 0x0725 }
            r10 = r7.limit();	 Catch:{ all -> 0x0725 }
            r11 = 0;
            org.telegram.messenger.Utilities.loadWebpImage(r11, r7, r10, r8, r9);	 Catch:{ all -> 0x07d2 }
            r9 = r8.outWidth;	 Catch:{ all -> 0x0725 }
            r8 = r8.outHeight;	 Catch:{ all -> 0x0725 }
            r10 = android.graphics.Bitmap.Config.ARGB_8888;	 Catch:{ all -> 0x0725 }
            r4 = org.telegram.messenger.Bitmaps.createBitmap(r9, r8, r10);	 Catch:{ all -> 0x0725 }
            r8 = r7.limit();	 Catch:{ all -> 0x0725 }
            r9 = r5.inPurgeable;	 Catch:{ all -> 0x0725 }
            if (r9 != 0) goto L_0x07c5;
        L_0x07c3:
            r9 = 1;
            goto L_0x07c6;
        L_0x07c5:
            r9 = 0;
        L_0x07c6:
            r10 = 0;
            org.telegram.messenger.Utilities.loadWebpImage(r4, r7, r8, r10, r9);	 Catch:{ all -> 0x07cf }
            r3.close();	 Catch:{ all -> 0x0725 }
            goto L_0x0893;
        L_0x07cf:
            r7 = r10;
            goto L_0x0994;
        L_0x07d2:
            r7 = r11;
            goto L_0x0994;
        L_0x07d5:
            r3 = r5.inPurgeable;	 Catch:{ all -> 0x0890 }
            if (r3 != 0) goto L_0x0831;
        L_0x07d9:
            if (r10 == 0) goto L_0x07dc;
        L_0x07db:
            goto L_0x0831;
        L_0x07dc:
            if (r9 == 0) goto L_0x07e8;
        L_0x07de:
            r3 = new org.telegram.messenger.secretmedia.EncryptedFileInputStream;	 Catch:{ all -> 0x0725 }
            r7 = r1.cacheImage;	 Catch:{ all -> 0x0725 }
            r7 = r7.encryptionKeyPath;	 Catch:{ all -> 0x0725 }
            r3.<init>(r2, r7);	 Catch:{ all -> 0x0725 }
            goto L_0x07ed;
        L_0x07e8:
            r3 = new java.io.FileInputStream;	 Catch:{ all -> 0x0890 }
            r3.<init>(r2);	 Catch:{ all -> 0x0890 }
        L_0x07ed:
            r7 = r1.cacheImage;	 Catch:{ all -> 0x0890 }
            r7 = r7.imageLocation;	 Catch:{ all -> 0x0890 }
            r7 = r7.document;	 Catch:{ all -> 0x0890 }
            r7 = r7 instanceof org.telegram.tgnet.TLRPC.TL_document;	 Catch:{ all -> 0x0890 }
            if (r7 == 0) goto L_0x0827;
        L_0x07f7:
            r7 = new androidx.exifinterface.media.ExifInterface;	 Catch:{ all -> 0x0817 }
            r7.<init>(r3);	 Catch:{ all -> 0x0817 }
            r8 = "Orientation";
            r9 = 1;
            r7 = r7.getAttributeInt(r8, r9);	 Catch:{ all -> 0x0817 }
            r8 = 3;
            if (r7 == r8) goto L_0x0814;
        L_0x0806:
            r8 = 6;
            if (r7 == r8) goto L_0x0811;
        L_0x0809:
            r8 = 8;
            if (r7 == r8) goto L_0x080e;
        L_0x080d:
            goto L_0x0817;
        L_0x080e:
            r8 = 270; // 0x10e float:3.78E-43 double:1.334E-321;
            goto L_0x0818;
        L_0x0811:
            r8 = 90;
            goto L_0x0818;
        L_0x0814:
            r8 = 180; // 0xb4 float:2.52E-43 double:8.9E-322;
            goto L_0x0818;
        L_0x0817:
            r8 = 0;
        L_0x0818:
            r7 = r3.getChannel();	 Catch:{ all -> 0x0823 }
            r9 = 0;
            r7.position(r9);	 Catch:{ all -> 0x0823 }
            r7 = 0;
            goto L_0x0829;
        L_0x0823:
            r2 = 0;
            r7 = 0;
            goto L_0x0997;
        L_0x0827:
            r7 = 0;
            r8 = 0;
        L_0x0829:
            r4 = android.graphics.BitmapFactory.decodeStream(r3, r7, r5);	 Catch:{ all -> 0x08ac }
            r3.close();	 Catch:{ all -> 0x08ac }
            goto L_0x0895;
        L_0x0831:
            r7 = 0;
            r3 = new java.io.RandomAccessFile;	 Catch:{ all -> 0x0994 }
            r8 = "r";
            r3.<init>(r2, r8);	 Catch:{ all -> 0x0994 }
            r12 = r3.length();	 Catch:{ all -> 0x0994 }
            r8 = (int) r12;	 Catch:{ all -> 0x0994 }
            r12 = org.telegram.messenger.ImageLoader.bytesLocal;	 Catch:{ all -> 0x0994 }
            r12 = r12.get();	 Catch:{ all -> 0x0994 }
            r12 = (byte[]) r12;	 Catch:{ all -> 0x0994 }
            if (r12 == 0) goto L_0x084e;
        L_0x084a:
            r13 = r12.length;	 Catch:{ all -> 0x0994 }
            if (r13 < r8) goto L_0x084e;
        L_0x084d:
            goto L_0x084f;
        L_0x084e:
            r12 = r7;
        L_0x084f:
            if (r12 != 0) goto L_0x085a;
        L_0x0851:
            r12 = new byte[r8];	 Catch:{ all -> 0x0994 }
            r13 = org.telegram.messenger.ImageLoader.bytesLocal;	 Catch:{ all -> 0x0994 }
            r13.set(r12);	 Catch:{ all -> 0x0994 }
        L_0x085a:
            r13 = 0;
            r3.readFully(r12, r13, r8);	 Catch:{ all -> 0x0994 }
            r3.close();	 Catch:{ all -> 0x0994 }
            if (r10 == 0) goto L_0x087d;
        L_0x0863:
            org.telegram.messenger.secretmedia.EncryptedFileInputStream.decryptBytesWithKeyFile(r12, r13, r8, r10);	 Catch:{ all -> 0x0994 }
            r3 = org.telegram.messenger.Utilities.computeSHA256(r12, r13, r8);	 Catch:{ all -> 0x0994 }
            if (r11 == 0) goto L_0x0875;
        L_0x086c:
            r3 = java.util.Arrays.equals(r3, r11);	 Catch:{ all -> 0x0994 }
            if (r3 != 0) goto L_0x0873;
        L_0x0872:
            goto L_0x0875;
        L_0x0873:
            r3 = 0;
            goto L_0x0876;
        L_0x0875:
            r3 = 1;
        L_0x0876:
            r9 = 0;
            r10 = r12[r9];	 Catch:{ all -> 0x0994 }
            r9 = r10 & 255;
            r8 = r8 - r9;
            goto L_0x0889;
        L_0x087d:
            if (r9 == 0) goto L_0x0887;
        L_0x087f:
            r3 = r1.cacheImage;	 Catch:{ all -> 0x0994 }
            r3 = r3.encryptionKeyPath;	 Catch:{ all -> 0x0994 }
            r9 = 0;
            org.telegram.messenger.secretmedia.EncryptedFileInputStream.decryptBytesWithKeyFile(r12, r9, r8, r3);	 Catch:{ all -> 0x0994 }
        L_0x0887:
            r3 = 0;
            r9 = 0;
        L_0x0889:
            if (r3 != 0) goto L_0x0894;
        L_0x088b:
            r4 = android.graphics.BitmapFactory.decodeByteArray(r12, r9, r8, r5);	 Catch:{ all -> 0x0994 }
            goto L_0x0894;
        L_0x0890:
            r7 = 0;
            goto L_0x0994;
        L_0x0893:
            r7 = 0;
        L_0x0894:
            r8 = 0;
        L_0x0895:
            if (r4 != 0) goto L_0x08af;
        L_0x0897:
            if (r16 == 0) goto L_0x08ac;
        L_0x0899:
            r5 = r2.length();	 Catch:{ all -> 0x08ac }
            r9 = 0;
            r0 = (r5 > r9 ? 1 : (r5 == r9 ? 0 : -1));
            if (r0 == 0) goto L_0x08a9;
        L_0x08a3:
            r0 = r1.cacheImage;	 Catch:{ all -> 0x08ac }
            r0 = r0.filter;	 Catch:{ all -> 0x08ac }
            if (r0 != 0) goto L_0x08ac;
        L_0x08a9:
            r2.delete();	 Catch:{ all -> 0x08ac }
        L_0x08ac:
            r2 = 0;
            goto L_0x0997;
        L_0x08af:
            r2 = r1.cacheImage;	 Catch:{ all -> 0x0986 }
            r2 = r2.filter;	 Catch:{ all -> 0x0986 }
            if (r2 == 0) goto L_0x0979;
        L_0x08b5:
            r2 = r4.getWidth();	 Catch:{ all -> 0x0986 }
            r2 = (float) r2;	 Catch:{ all -> 0x0986 }
            r3 = r4.getHeight();	 Catch:{ all -> 0x0986 }
            r3 = (float) r3;	 Catch:{ all -> 0x0986 }
            r9 = r5.inPurgeable;	 Catch:{ all -> 0x0986 }
            if (r9 != 0) goto L_0x08f9;
        L_0x08c3:
            r9 = (r0 > r20 ? 1 : (r0 == r20 ? 0 : -1));
            if (r9 == 0) goto L_0x08f9;
        L_0x08c7:
            r9 = (r2 > r0 ? 1 : (r2 == r0 ? 0 : -1));
            if (r9 == 0) goto L_0x08f9;
        L_0x08cb:
            r9 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
            r9 = r9 + r0;
            r9 = (r2 > r9 ? 1 : (r2 == r9 ? 0 : -1));
            if (r9 <= 0) goto L_0x08f9;
        L_0x08d2:
            r9 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1));
            if (r9 <= 0) goto L_0x08e6;
        L_0x08d6:
            r9 = (r0 > r21 ? 1 : (r0 == r21 ? 0 : -1));
            if (r9 <= 0) goto L_0x08e6;
        L_0x08da:
            r9 = r2 / r0;
            r0 = (int) r0;
            r9 = r3 / r9;
            r9 = (int) r9;
            r10 = 1;
            r0 = org.telegram.messenger.Bitmaps.createScaledBitmap(r4, r0, r9, r10);	 Catch:{ all -> 0x08ac }
            goto L_0x08f3;
        L_0x08e6:
            r10 = 1;
            r0 = r3 / r21;
            r0 = r2 / r0;
            r0 = (int) r0;	 Catch:{ all -> 0x08ac }
            r9 = r21;
            r9 = (int) r9;	 Catch:{ all -> 0x08ac }
            r0 = org.telegram.messenger.Bitmaps.createScaledBitmap(r4, r0, r9, r10);	 Catch:{ all -> 0x08ac }
        L_0x08f3:
            if (r4 == r0) goto L_0x08fa;
        L_0x08f5:
            r4.recycle();	 Catch:{ all -> 0x08ac }
            goto L_0x08fb;
        L_0x08f9:
            r10 = 1;
        L_0x08fa:
            r0 = r4;
        L_0x08fb:
            if (r0 == 0) goto L_0x0976;
        L_0x08fd:
            if (r26 == 0) goto L_0x093f;
        L_0x08ff:
            r4 = r0.getWidth();	 Catch:{ all -> 0x093b }
            r9 = r0.getHeight();	 Catch:{ all -> 0x093b }
            r4 = r4 * r9;
            r9 = 22500; // 0x57e4 float:3.1529E-41 double:1.11165E-319;
            if (r4 <= r9) goto L_0x0917;
        L_0x090d:
            r4 = 100;
            r9 = 100;
            r11 = 0;
            r4 = org.telegram.messenger.Bitmaps.createScaledBitmap(r0, r4, r9, r11);	 Catch:{ all -> 0x093c }
            goto L_0x0919;
        L_0x0917:
            r11 = 0;
            r4 = r0;
        L_0x0919:
            r9 = r5.inPurgeable;	 Catch:{ all -> 0x093c }
            if (r9 == 0) goto L_0x091f;
        L_0x091d:
            r9 = 0;
            goto L_0x0920;
        L_0x091f:
            r9 = 1;
        L_0x0920:
            r12 = r4.getWidth();	 Catch:{ all -> 0x093c }
            r13 = r4.getHeight();	 Catch:{ all -> 0x093c }
            r14 = r4.getRowBytes();	 Catch:{ all -> 0x093c }
            r9 = org.telegram.messenger.Utilities.needInvert(r4, r9, r12, r13, r14);	 Catch:{ all -> 0x093c }
            if (r9 == 0) goto L_0x0934;
        L_0x0932:
            r9 = 1;
            goto L_0x0935;
        L_0x0934:
            r9 = 0;
        L_0x0935:
            if (r4 == r0) goto L_0x0941;
        L_0x0937:
            r4.recycle();	 Catch:{ all -> 0x0972 }
            goto L_0x0941;
        L_0x093b:
            r11 = 0;
        L_0x093c:
            r4 = r0;
            goto L_0x08ac;
        L_0x093f:
            r11 = 0;
            r9 = 0;
        L_0x0941:
            if (r6 == 0) goto L_0x0974;
        L_0x0943:
            r4 = NUM; // 0x42CLASSNAME float:100.0 double:5.53552857E-315;
            r3 = (r3 > r4 ? 1 : (r3 == r4 ? 0 : -1));
            if (r3 >= 0) goto L_0x0974;
        L_0x0949:
            r3 = NUM; // 0x42CLASSNAME float:100.0 double:5.53552857E-315;
            r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1));
            if (r2 >= 0) goto L_0x0974;
        L_0x094f:
            r2 = r0.getConfig();	 Catch:{ all -> 0x0972 }
            r3 = android.graphics.Bitmap.Config.ARGB_8888;	 Catch:{ all -> 0x0972 }
            if (r2 != r3) goto L_0x096f;
        L_0x0957:
            r12 = 3;
            r2 = r5.inPurgeable;	 Catch:{ all -> 0x0972 }
            if (r2 == 0) goto L_0x095e;
        L_0x095c:
            r13 = 0;
            goto L_0x095f;
        L_0x095e:
            r13 = 1;
        L_0x095f:
            r14 = r0.getWidth();	 Catch:{ all -> 0x0972 }
            r15 = r0.getHeight();	 Catch:{ all -> 0x0972 }
            r16 = r0.getRowBytes();	 Catch:{ all -> 0x0972 }
            r11 = r0;
            org.telegram.messenger.Utilities.blurBitmap(r11, r12, r13, r14, r15, r16);	 Catch:{ all -> 0x0972 }
        L_0x096f:
            r4 = r0;
            r11 = 1;
            goto L_0x097b;
        L_0x0972:
            r4 = r0;
            goto L_0x0984;
        L_0x0974:
            r4 = r0;
            goto L_0x097b;
        L_0x0976:
            r11 = 0;
            r4 = r0;
            goto L_0x097a;
        L_0x0979:
            r11 = 0;
        L_0x097a:
            r9 = 0;
        L_0x097b:
            if (r11 != 0) goto L_0x0984;
        L_0x097d:
            r0 = r5.inPurgeable;	 Catch:{ all -> 0x0984 }
            if (r0 == 0) goto L_0x0984;
        L_0x0981:
            org.telegram.messenger.Utilities.pinBitmap(r4);	 Catch:{ all -> 0x0984 }
        L_0x0984:
            r2 = r9;
            goto L_0x0997;
        L_0x0986:
            r11 = 0;
            goto L_0x08ac;
        L_0x0989:
            r0 = move-exception;
            r7 = 0;
            r11 = 0;
        L_0x098c:
            monitor-exit(r3);	 Catch:{ all -> 0x098e }
            throw r0;	 Catch:{ all -> 0x0992 }
        L_0x098e:
            r0 = move-exception;
            goto L_0x098c;
        L_0x0990:
            r7 = 0;
            r11 = 0;
        L_0x0992:
            r4 = r27;
        L_0x0994:
            r2 = 0;
            goto L_0x06eb;
        L_0x0997:
            java.lang.Thread.interrupted();
            if (r2 != 0) goto L_0x09ac;
        L_0x099c:
            if (r8 == 0) goto L_0x099f;
        L_0x099e:
            goto L_0x09ac;
        L_0x099f:
            if (r4 == 0) goto L_0x09a7;
        L_0x09a1:
            r3 = new android.graphics.drawable.BitmapDrawable;
            r3.<init>(r4);
            goto L_0x09a8;
        L_0x09a7:
            r3 = r7;
        L_0x09a8:
            r1.onPostExecute(r3);
            goto L_0x09b8;
        L_0x09ac:
            if (r4 == 0) goto L_0x09b4;
        L_0x09ae:
            r3 = new org.telegram.messenger.ExtendedBitmapDrawable;
            r3.<init>(r4, r2, r8);
            goto L_0x09b5;
        L_0x09b4:
            r3 = r7;
        L_0x09b5:
            r1.onPostExecute(r3);
        L_0x09b8:
            return;
        L_0x09b9:
            r0 = move-exception;
            monitor-exit(r2);	 Catch:{ all -> 0x09b9 }
            goto L_0x09bd;
        L_0x09bc:
            throw r0;
        L_0x09bd:
            goto L_0x09bc;
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
        /* JADX WARNING: Removed duplicated region for block: B:103:0x0148 A:{Catch:{ all -> 0x0150 }} */
        /* JADX WARNING: Removed duplicated region for block: B:107:0x0156 A:{SYNTHETIC, Splitter:B:107:0x0156} */
        /* JADX WARNING: Removed duplicated region for block: B:103:0x0148 A:{Catch:{ all -> 0x0150 }} */
        /* JADX WARNING: Removed duplicated region for block: B:107:0x0156 A:{SYNTHETIC, Splitter:B:107:0x0156} */
        /* JADX WARNING: Removed duplicated region for block: B:103:0x0148 A:{Catch:{ all -> 0x0150 }} */
        /* JADX WARNING: Removed duplicated region for block: B:107:0x0156 A:{SYNTHETIC, Splitter:B:107:0x0156} */
        /* JADX WARNING: Removed duplicated region for block: B:43:0x00ad A:{SYNTHETIC, Splitter:B:43:0x00ad} */
        /* JADX WARNING: Removed duplicated region for block: B:103:0x0148 A:{Catch:{ all -> 0x0150 }} */
        /* JADX WARNING: Removed duplicated region for block: B:107:0x0156 A:{SYNTHETIC, Splitter:B:107:0x0156} */
        /* JADX WARNING: Removed duplicated region for block: B:103:0x0148 A:{Catch:{ all -> 0x0150 }} */
        /* JADX WARNING: Removed duplicated region for block: B:107:0x0156 A:{SYNTHETIC, Splitter:B:107:0x0156} */
        /* JADX WARNING: Removed duplicated region for block: B:27:0x007f  */
        /* JADX WARNING: Removed duplicated region for block: B:24:0x0076  */
        /* JADX WARNING: Removed duplicated region for block: B:43:0x00ad A:{SYNTHETIC, Splitter:B:43:0x00ad} */
        public java.lang.Boolean doInBackground(java.lang.Void... r10) {
            /*
            r9 = this;
            r10 = "Mozilla/5.0 (iPhone; CPU iPhone OS 10_0 like Mac OS X) AppleWebKit/602.1.38 (KHTML, like Gecko) Version/10.0 Mobile/14A5297c Safari/602.1";
            r0 = "User-Agent";
            r1 = 1;
            r2 = 0;
            r3 = 0;
            r4 = new java.net.URL;	 Catch:{ all -> 0x006f }
            r5 = r9.url;	 Catch:{ all -> 0x006f }
            r4.<init>(r5);	 Catch:{ all -> 0x006f }
            r4 = r4.openConnection();	 Catch:{ all -> 0x006f }
            r4.addRequestProperty(r0, r10);	 Catch:{ all -> 0x006c }
            r5 = 5000; // 0x1388 float:7.006E-42 double:2.4703E-320;
            r4.setConnectTimeout(r5);	 Catch:{ all -> 0x006c }
            r4.setReadTimeout(r5);	 Catch:{ all -> 0x006c }
            r5 = r4 instanceof java.net.HttpURLConnection;	 Catch:{ all -> 0x006c }
            if (r5 == 0) goto L_0x0054;
        L_0x0021:
            r5 = r4;
            r5 = (java.net.HttpURLConnection) r5;	 Catch:{ all -> 0x006c }
            r5.setInstanceFollowRedirects(r1);	 Catch:{ all -> 0x006c }
            r6 = r5.getResponseCode();	 Catch:{ all -> 0x006c }
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
            r6 = r5.getHeaderField(r6);	 Catch:{ all -> 0x006c }
            r7 = "Set-Cookie";
            r5 = r5.getHeaderField(r7);	 Catch:{ all -> 0x006c }
            r7 = new java.net.URL;	 Catch:{ all -> 0x006c }
            r7.<init>(r6);	 Catch:{ all -> 0x006c }
            r4 = r7.openConnection();	 Catch:{ all -> 0x006c }
            r6 = "Cookie";
            r4.setRequestProperty(r6, r5);	 Catch:{ all -> 0x006c }
            r4.addRequestProperty(r0, r10);	 Catch:{ all -> 0x006c }
        L_0x0054:
            r4.connect();	 Catch:{ all -> 0x006c }
            r10 = r4.getInputStream();	 Catch:{ all -> 0x006c }
            r0 = new java.io.RandomAccessFile;	 Catch:{ all -> 0x0067 }
            r5 = r9.tempFile;	 Catch:{ all -> 0x0067 }
            r6 = "rws";
            r0.<init>(r5, r6);	 Catch:{ all -> 0x0067 }
            r9.fileOutputStream = r0;	 Catch:{ all -> 0x0067 }
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
            r0 = new byte[r0];	 Catch:{ all -> 0x0140 }
            r4 = 0;
        L_0x0102:
            r5 = r9.isCancelled();	 Catch:{ all -> 0x0140 }
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
            r0 = r9.fileSize;	 Catch:{ Exception -> 0x0132, all -> 0x012f }
            if (r0 == 0) goto L_0x013b;
        L_0x0129:
            r0 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
            r9.reportProgress(r0);	 Catch:{ Exception -> 0x0132, all -> 0x012f }
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
            org.telegram.messenger.FileLog.e(r0);	 Catch:{ all -> 0x013d }
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
            r0 = r9.fileOutputStream;	 Catch:{ all -> 0x0150 }
            if (r0 == 0) goto L_0x0154;
        L_0x0148:
            r0 = r9.fileOutputStream;	 Catch:{ all -> 0x0150 }
            r0.close();	 Catch:{ all -> 0x0150 }
            r9.fileOutputStream = r2;	 Catch:{ all -> 0x0150 }
            goto L_0x0154;
        L_0x0150:
            r0 = move-exception;
            org.telegram.messenger.FileLog.e(r0);
        L_0x0154:
            if (r10 == 0) goto L_0x015e;
        L_0x0156:
            r10.close();	 Catch:{ all -> 0x015a }
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
        /* JADX WARNING: Removed duplicated region for block: B:108:0x0185 A:{Catch:{ all -> 0x018d }} */
        /* JADX WARNING: Removed duplicated region for block: B:114:0x0195 A:{Catch:{ all -> 0x019b }} */
        /* JADX WARNING: Removed duplicated region for block: B:117:0x019e A:{SYNTHETIC, Splitter:B:117:0x019e} */
        /* JADX WARNING: Removed duplicated region for block: B:122:0x01a8  */
        /* JADX WARNING: Removed duplicated region for block: B:48:0x00e9 A:{SYNTHETIC, Splitter:B:48:0x00e9} */
        /* JADX WARNING: Removed duplicated region for block: B:108:0x0185 A:{Catch:{ all -> 0x018d }} */
        /* JADX WARNING: Removed duplicated region for block: B:114:0x0195 A:{Catch:{ all -> 0x019b }} */
        /* JADX WARNING: Removed duplicated region for block: B:117:0x019e A:{SYNTHETIC, Splitter:B:117:0x019e} */
        /* JADX WARNING: Removed duplicated region for block: B:122:0x01a8  */
        /* JADX WARNING: Removed duplicated region for block: B:108:0x0185 A:{Catch:{ all -> 0x018d }} */
        /* JADX WARNING: Removed duplicated region for block: B:114:0x0195 A:{Catch:{ all -> 0x019b }} */
        /* JADX WARNING: Removed duplicated region for block: B:117:0x019e A:{SYNTHETIC, Splitter:B:117:0x019e} */
        /* JADX WARNING: Removed duplicated region for block: B:122:0x01a8  */
        /* JADX WARNING: Removed duplicated region for block: B:108:0x0185 A:{Catch:{ all -> 0x018d }} */
        /* JADX WARNING: Removed duplicated region for block: B:114:0x0195 A:{Catch:{ all -> 0x019b }} */
        /* JADX WARNING: Removed duplicated region for block: B:117:0x019e A:{SYNTHETIC, Splitter:B:117:0x019e} */
        /* JADX WARNING: Removed duplicated region for block: B:122:0x01a8  */
        /* JADX WARNING: Removed duplicated region for block: B:108:0x0185 A:{Catch:{ all -> 0x018d }} */
        /* JADX WARNING: Removed duplicated region for block: B:114:0x0195 A:{Catch:{ all -> 0x019b }} */
        /* JADX WARNING: Removed duplicated region for block: B:117:0x019e A:{SYNTHETIC, Splitter:B:117:0x019e} */
        /* JADX WARNING: Removed duplicated region for block: B:122:0x01a8  */
        public java.lang.Boolean doInBackground(java.lang.Void... r9) {
            /*
            r8 = this;
            r9 = r8.isCancelled();
            r0 = 1;
            r1 = 0;
            r2 = 0;
            if (r9 != 0) goto L_0x00e2;
        L_0x0009:
            r9 = r8.cacheImage;	 Catch:{ all -> 0x00a8 }
            r9 = r9.imageLocation;	 Catch:{ all -> 0x00a8 }
            r9 = r9.path;	 Catch:{ all -> 0x00a8 }
            r3 = "https://static-maps";
            r3 = r9.startsWith(r3);	 Catch:{ all -> 0x00a8 }
            if (r3 != 0) goto L_0x001f;
        L_0x0017:
            r3 = "https://maps.googleapis";
            r3 = r9.startsWith(r3);	 Catch:{ all -> 0x00a8 }
            if (r3 == 0) goto L_0x0057;
        L_0x001f:
            r3 = r8.cacheImage;	 Catch:{ all -> 0x00a8 }
            r3 = r3.currentAccount;	 Catch:{ all -> 0x00a8 }
            r3 = org.telegram.messenger.MessagesController.getInstance(r3);	 Catch:{ all -> 0x00a8 }
            r3 = r3.mapProvider;	 Catch:{ all -> 0x00a8 }
            r4 = 3;
            if (r3 == r4) goto L_0x002f;
        L_0x002c:
            r4 = 4;
            if (r3 != r4) goto L_0x0057;
        L_0x002f:
            r3 = org.telegram.messenger.ImageLoader.this;	 Catch:{ all -> 0x00a8 }
            r3 = r3.testWebFile;	 Catch:{ all -> 0x00a8 }
            r3 = r3.get(r9);	 Catch:{ all -> 0x00a8 }
            r3 = (org.telegram.messenger.WebFile) r3;	 Catch:{ all -> 0x00a8 }
            if (r3 == 0) goto L_0x0057;
        L_0x003d:
            r4 = new org.telegram.tgnet.TLRPC$TL_upload_getWebFile;	 Catch:{ all -> 0x00a8 }
            r4.<init>();	 Catch:{ all -> 0x00a8 }
            r3 = r3.location;	 Catch:{ all -> 0x00a8 }
            r4.location = r3;	 Catch:{ all -> 0x00a8 }
            r4.offset = r2;	 Catch:{ all -> 0x00a8 }
            r4.limit = r2;	 Catch:{ all -> 0x00a8 }
            r3 = r8.cacheImage;	 Catch:{ all -> 0x00a8 }
            r3 = r3.currentAccount;	 Catch:{ all -> 0x00a8 }
            r3 = org.telegram.tgnet.ConnectionsManager.getInstance(r3);	 Catch:{ all -> 0x00a8 }
            r5 = org.telegram.messenger.-$$Lambda$ImageLoader$HttpImageTask$T115Ddi3sI3XyS3851ENmLig_I8.INSTANCE;	 Catch:{ all -> 0x00a8 }
            r3.sendRequest(r4, r5);	 Catch:{ all -> 0x00a8 }
        L_0x0057:
            r3 = new java.net.URL;	 Catch:{ all -> 0x00a8 }
            r4 = r8.overrideUrl;	 Catch:{ all -> 0x00a8 }
            if (r4 == 0) goto L_0x005f;
        L_0x005d:
            r9 = r8.overrideUrl;	 Catch:{ all -> 0x00a8 }
        L_0x005f:
            r3.<init>(r9);	 Catch:{ all -> 0x00a8 }
            r9 = r3.openConnection();	 Catch:{ all -> 0x00a8 }
            r9 = (java.net.HttpURLConnection) r9;	 Catch:{ all -> 0x00a8 }
            r8.httpConnection = r9;	 Catch:{ all -> 0x00a8 }
            r9 = r8.httpConnection;	 Catch:{ all -> 0x00a8 }
            r3 = "User-Agent";
            r4 = "Mozilla/5.0 (iPhone; CPU iPhone OS 10_0 like Mac OS X) AppleWebKit/602.1.38 (KHTML, like Gecko) Version/10.0 Mobile/14A5297c Safari/602.1";
            r9.addRequestProperty(r3, r4);	 Catch:{ all -> 0x00a8 }
            r9 = r8.httpConnection;	 Catch:{ all -> 0x00a8 }
            r3 = 5000; // 0x1388 float:7.006E-42 double:2.4703E-320;
            r9.setConnectTimeout(r3);	 Catch:{ all -> 0x00a8 }
            r9 = r8.httpConnection;	 Catch:{ all -> 0x00a8 }
            r9.setReadTimeout(r3);	 Catch:{ all -> 0x00a8 }
            r9 = r8.httpConnection;	 Catch:{ all -> 0x00a8 }
            r9.setInstanceFollowRedirects(r0);	 Catch:{ all -> 0x00a8 }
            r9 = r8.isCancelled();	 Catch:{ all -> 0x00a8 }
            if (r9 != 0) goto L_0x00e2;
        L_0x008a:
            r9 = r8.httpConnection;	 Catch:{ all -> 0x00a8 }
            r9.connect();	 Catch:{ all -> 0x00a8 }
            r9 = r8.httpConnection;	 Catch:{ all -> 0x00a8 }
            r9 = r9.getInputStream();	 Catch:{ all -> 0x00a8 }
            r3 = new java.io.RandomAccessFile;	 Catch:{ all -> 0x00a3 }
            r4 = r8.cacheImage;	 Catch:{ all -> 0x00a3 }
            r4 = r4.tempFilePath;	 Catch:{ all -> 0x00a3 }
            r5 = "rws";
            r3.<init>(r4, r5);	 Catch:{ all -> 0x00a3 }
            r8.fileOutputStream = r3;	 Catch:{ all -> 0x00a3 }
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
            r3 = new byte[r3];	 Catch:{ all -> 0x017d }
            r4 = 0;
        L_0x0142:
            r5 = r8.isCancelled();	 Catch:{ all -> 0x017d }
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
            r2 = r8.imageSize;	 Catch:{ Exception -> 0x0174, all -> 0x0170 }
            if (r2 == 0) goto L_0x016e;
        L_0x0169:
            r2 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
            r8.reportProgress(r2);	 Catch:{ Exception -> 0x0174, all -> 0x0170 }
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
            org.telegram.messenger.FileLog.e(r0);	 Catch:{ all -> 0x017d }
            goto L_0x0181;
        L_0x017d:
            r0 = move-exception;
        L_0x017e:
            org.telegram.messenger.FileLog.e(r0);
        L_0x0181:
            r0 = r8.fileOutputStream;	 Catch:{ all -> 0x018d }
            if (r0 == 0) goto L_0x0191;
        L_0x0185:
            r0 = r8.fileOutputStream;	 Catch:{ all -> 0x018d }
            r0.close();	 Catch:{ all -> 0x018d }
            r8.fileOutputStream = r1;	 Catch:{ all -> 0x018d }
            goto L_0x0191;
        L_0x018d:
            r0 = move-exception;
            org.telegram.messenger.FileLog.e(r0);
        L_0x0191:
            r0 = r8.httpConnection;	 Catch:{ all -> 0x019b }
            if (r0 == 0) goto L_0x019c;
        L_0x0195:
            r0 = r8.httpConnection;	 Catch:{ all -> 0x019b }
            r0.disconnect();	 Catch:{ all -> 0x019b }
            goto L_0x019c;
        L_0x019c:
            if (r9 == 0) goto L_0x01a6;
        L_0x019e:
            r9.close();	 Catch:{ all -> 0x01a2 }
            goto L_0x01a6;
        L_0x01a2:
            r9 = move-exception;
            org.telegram.messenger.FileLog.e(r9);
        L_0x01a6:
            if (r2 == 0) goto L_0x01bc;
        L_0x01a8:
            r9 = r8.cacheImage;
            r0 = r9.tempFilePath;
            if (r0 == 0) goto L_0x01bc;
        L_0x01ae:
            r9 = r9.finalFilePath;
            r9 = r0.renameTo(r9);
            if (r9 != 0) goto L_0x01bc;
        L_0x01b6:
            r9 = r8.cacheImage;
            r0 = r9.tempFilePath;
            r9.finalFilePath = r0;
        L_0x01bc:
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
                NotificationCenter instance = NotificationCenter.getInstance(this.cacheImage.currentAccount);
                int i = NotificationCenter.fileDidLoad;
                r2 = new Object[2];
                CacheImage cacheImage = this.cacheImage;
                r2[0] = cacheImage.url;
                r2[1] = cacheImage.finalFilePath;
                instance.postNotificationName(i, r2);
                return;
            }
            NotificationCenter.getInstance(this.cacheImage.currentAccount).postNotificationName(NotificationCenter.fileDidFailToLoad, this.cacheImage.url, Integer.valueOf(2));
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
            NotificationCenter.getInstance(this.cacheImage.currentAccount).postNotificationName(NotificationCenter.fileDidFailToLoad, this.cacheImage.url, Integer.valueOf(1));
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

        /* JADX WARNING: Removed duplicated region for block: B:59:0x0145 A:{Catch:{ Exception -> 0x014e, all -> 0x0180 }} */
        /* JADX WARNING: Removed duplicated region for block: B:58:0x0142 A:{Catch:{ Exception -> 0x014e, all -> 0x0180 }} */
        public void run() {
            /*
            r10 = this;
            r0 = ".jpg";
            r1 = r10.info;	 Catch:{ all -> 0x0180 }
            if (r1 != 0) goto L_0x000a;
        L_0x0006:
            r10.removeTask();	 Catch:{ all -> 0x0180 }
            return;
        L_0x000a:
            r1 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0180 }
            r1.<init>();	 Catch:{ all -> 0x0180 }
            r2 = "q_";
            r1.append(r2);	 Catch:{ all -> 0x0180 }
            r2 = r10.info;	 Catch:{ all -> 0x0180 }
            r2 = r2.parentDocument;	 Catch:{ all -> 0x0180 }
            r2 = r2.dc_id;	 Catch:{ all -> 0x0180 }
            r1.append(r2);	 Catch:{ all -> 0x0180 }
            r2 = "_";
            r1.append(r2);	 Catch:{ all -> 0x0180 }
            r2 = r10.info;	 Catch:{ all -> 0x0180 }
            r2 = r2.parentDocument;	 Catch:{ all -> 0x0180 }
            r2 = r2.id;	 Catch:{ all -> 0x0180 }
            r1.append(r2);	 Catch:{ all -> 0x0180 }
            r6 = r1.toString();	 Catch:{ all -> 0x0180 }
            r1 = new java.io.File;	 Catch:{ all -> 0x0180 }
            r2 = 4;
            r3 = org.telegram.messenger.FileLoader.getDirectory(r2);	 Catch:{ all -> 0x0180 }
            r4 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0180 }
            r4.<init>();	 Catch:{ all -> 0x0180 }
            r4.append(r6);	 Catch:{ all -> 0x0180 }
            r4.append(r0);	 Catch:{ all -> 0x0180 }
            r4 = r4.toString();	 Catch:{ all -> 0x0180 }
            r1.<init>(r3, r4);	 Catch:{ all -> 0x0180 }
            r3 = r1.exists();	 Catch:{ all -> 0x0180 }
            if (r3 != 0) goto L_0x017c;
        L_0x0052:
            r3 = r10.originalPath;	 Catch:{ all -> 0x0180 }
            r3 = r3.exists();	 Catch:{ all -> 0x0180 }
            if (r3 != 0) goto L_0x005c;
        L_0x005a:
            goto L_0x017c;
        L_0x005c:
            r3 = r10.info;	 Catch:{ all -> 0x0180 }
            r3 = r3.big;	 Catch:{ all -> 0x0180 }
            if (r3 == 0) goto L_0x0071;
        L_0x0064:
            r2 = org.telegram.messenger.AndroidUtilities.displaySize;	 Catch:{ all -> 0x0180 }
            r2 = r2.x;	 Catch:{ all -> 0x0180 }
            r3 = org.telegram.messenger.AndroidUtilities.displaySize;	 Catch:{ all -> 0x0180 }
            r3 = r3.y;	 Catch:{ all -> 0x0180 }
            r2 = java.lang.Math.max(r2, r3);	 Catch:{ all -> 0x0180 }
            goto L_0x0084;
        L_0x0071:
            r3 = 180; // 0xb4 float:2.52E-43 double:8.9E-322;
            r4 = org.telegram.messenger.AndroidUtilities.displaySize;	 Catch:{ all -> 0x0180 }
            r4 = r4.x;	 Catch:{ all -> 0x0180 }
            r5 = org.telegram.messenger.AndroidUtilities.displaySize;	 Catch:{ all -> 0x0180 }
            r5 = r5.y;	 Catch:{ all -> 0x0180 }
            r4 = java.lang.Math.min(r4, r5);	 Catch:{ all -> 0x0180 }
            r4 = r4 / r2;
            r2 = java.lang.Math.min(r3, r4);	 Catch:{ all -> 0x0180 }
        L_0x0084:
            r3 = r10.mediaType;	 Catch:{ all -> 0x0180 }
            r4 = 0;
            r5 = 1;
            r7 = 0;
            if (r3 != 0) goto L_0x0097;
        L_0x008b:
            r0 = r10.originalPath;	 Catch:{ all -> 0x0180 }
            r0 = r0.toString();	 Catch:{ all -> 0x0180 }
            r3 = (float) r2;	 Catch:{ all -> 0x0180 }
            r7 = org.telegram.messenger.ImageLoader.loadBitmap(r0, r7, r3, r3, r4);	 Catch:{ all -> 0x0180 }
            goto L_0x0100;
        L_0x0097:
            r3 = r10.mediaType;	 Catch:{ all -> 0x0180 }
            r8 = 2;
            if (r3 != r8) goto L_0x00b1;
        L_0x009c:
            r0 = r10.originalPath;	 Catch:{ all -> 0x0180 }
            r0 = r0.toString();	 Catch:{ all -> 0x0180 }
            r3 = r10.info;	 Catch:{ all -> 0x0180 }
            r3 = r3.big;	 Catch:{ all -> 0x0180 }
            if (r3 == 0) goto L_0x00ab;
        L_0x00aa:
            goto L_0x00ac;
        L_0x00ab:
            r8 = 1;
        L_0x00ac:
            r7 = android.media.ThumbnailUtils.createVideoThumbnail(r0, r8);	 Catch:{ all -> 0x0180 }
            goto L_0x0100;
        L_0x00b1:
            r3 = r10.mediaType;	 Catch:{ all -> 0x0180 }
            r9 = 3;
            if (r3 != r9) goto L_0x0100;
        L_0x00b6:
            r3 = r10.originalPath;	 Catch:{ all -> 0x0180 }
            r3 = r3.toString();	 Catch:{ all -> 0x0180 }
            r3 = r3.toLowerCase();	 Catch:{ all -> 0x0180 }
            r9 = "mp4";
            r9 = r3.endsWith(r9);	 Catch:{ all -> 0x0180 }
            if (r9 == 0) goto L_0x00dd;
        L_0x00c8:
            r0 = r10.originalPath;	 Catch:{ all -> 0x0180 }
            r0 = r0.toString();	 Catch:{ all -> 0x0180 }
            r3 = r10.info;	 Catch:{ all -> 0x0180 }
            r3 = r3.big;	 Catch:{ all -> 0x0180 }
            if (r3 == 0) goto L_0x00d7;
        L_0x00d6:
            goto L_0x00d8;
        L_0x00d7:
            r8 = 1;
        L_0x00d8:
            r7 = android.media.ThumbnailUtils.createVideoThumbnail(r0, r8);	 Catch:{ all -> 0x0180 }
            goto L_0x0100;
        L_0x00dd:
            r0 = r3.endsWith(r0);	 Catch:{ all -> 0x0180 }
            if (r0 != 0) goto L_0x00fb;
        L_0x00e3:
            r0 = ".jpeg";
            r0 = r3.endsWith(r0);	 Catch:{ all -> 0x0180 }
            if (r0 != 0) goto L_0x00fb;
        L_0x00eb:
            r0 = ".png";
            r0 = r3.endsWith(r0);	 Catch:{ all -> 0x0180 }
            if (r0 != 0) goto L_0x00fb;
        L_0x00f3:
            r0 = ".gif";
            r0 = r3.endsWith(r0);	 Catch:{ all -> 0x0180 }
            if (r0 == 0) goto L_0x0100;
        L_0x00fb:
            r0 = (float) r2;	 Catch:{ all -> 0x0180 }
            r7 = org.telegram.messenger.ImageLoader.loadBitmap(r3, r7, r0, r0, r4);	 Catch:{ all -> 0x0180 }
        L_0x0100:
            if (r7 != 0) goto L_0x0106;
        L_0x0102:
            r10.removeTask();	 Catch:{ all -> 0x0180 }
            return;
        L_0x0106:
            r0 = r7.getWidth();	 Catch:{ all -> 0x0180 }
            r3 = r7.getHeight();	 Catch:{ all -> 0x0180 }
            if (r0 == 0) goto L_0x0178;
        L_0x0110:
            if (r3 != 0) goto L_0x0113;
        L_0x0112:
            goto L_0x0178;
        L_0x0113:
            r0 = (float) r0;	 Catch:{ all -> 0x0180 }
            r2 = (float) r2;	 Catch:{ all -> 0x0180 }
            r4 = r0 / r2;
            r3 = (float) r3;	 Catch:{ all -> 0x0180 }
            r2 = r3 / r2;
            r2 = java.lang.Math.min(r4, r2);	 Catch:{ all -> 0x0180 }
            r4 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
            r4 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1));
            if (r4 <= 0) goto L_0x0132;
        L_0x0124:
            r0 = r0 / r2;
            r0 = (int) r0;	 Catch:{ all -> 0x0180 }
            r3 = r3 / r2;
            r2 = (int) r3;	 Catch:{ all -> 0x0180 }
            r0 = org.telegram.messenger.Bitmaps.createScaledBitmap(r7, r0, r2, r5);	 Catch:{ all -> 0x0180 }
            if (r0 == r7) goto L_0x0132;
        L_0x012e:
            r7.recycle();	 Catch:{ all -> 0x0180 }
            goto L_0x0133;
        L_0x0132:
            r0 = r7;
        L_0x0133:
            r2 = new java.io.FileOutputStream;	 Catch:{ all -> 0x0180 }
            r2.<init>(r1);	 Catch:{ all -> 0x0180 }
            r1 = android.graphics.Bitmap.CompressFormat.JPEG;	 Catch:{ all -> 0x0180 }
            r3 = r10.info;	 Catch:{ all -> 0x0180 }
            r3 = r3.big;	 Catch:{ all -> 0x0180 }
            if (r3 == 0) goto L_0x0145;
        L_0x0142:
            r3 = 83;
            goto L_0x0147;
        L_0x0145:
            r3 = 60;
        L_0x0147:
            r0.compress(r1, r3, r2);	 Catch:{ all -> 0x0180 }
            r2.close();	 Catch:{ Exception -> 0x014e }
            goto L_0x0152;
        L_0x014e:
            r1 = move-exception;
            org.telegram.messenger.FileLog.e(r1);	 Catch:{ all -> 0x0180 }
        L_0x0152:
            r8 = new android.graphics.drawable.BitmapDrawable;	 Catch:{ all -> 0x0180 }
            r8.<init>(r0);	 Catch:{ all -> 0x0180 }
            r7 = new java.util.ArrayList;	 Catch:{ all -> 0x0180 }
            r0 = r10.info;	 Catch:{ all -> 0x0180 }
            r0 = r0.imageReceiverArray;	 Catch:{ all -> 0x0180 }
            r7.<init>(r0);	 Catch:{ all -> 0x0180 }
            r9 = new java.util.ArrayList;	 Catch:{ all -> 0x0180 }
            r0 = r10.info;	 Catch:{ all -> 0x0180 }
            r0 = r0.imageReceiverGuidsArray;	 Catch:{ all -> 0x0180 }
            r9.<init>(r0);	 Catch:{ all -> 0x0180 }
            r0 = new org.telegram.messenger.-$$Lambda$ImageLoader$ThumbGenerateTask$Q_bLt0Pje4_VwZd4dMzoXU74lq4;	 Catch:{ all -> 0x0180 }
            r4 = r0;
            r5 = r10;
            r4.<init>(r5, r6, r7, r8, r9);	 Catch:{ all -> 0x0180 }
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r0);	 Catch:{ all -> 0x0180 }
            goto L_0x0187;
        L_0x0178:
            r10.removeTask();	 Catch:{ all -> 0x0180 }
            return;
        L_0x017c:
            r10.removeTask();	 Catch:{ all -> 0x0180 }
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

    private static Bitmap getStrippedPhotoBitmap(byte[] bArr, String str) {
        int length = ((bArr.length - 3) + Bitmaps.header.length) + Bitmaps.footer.length;
        Object obj = (byte[]) bytesLocal.get();
        if (obj == null || obj.length < length) {
            obj = null;
        }
        if (obj == null) {
            obj = new byte[length];
            bytesLocal.set(obj);
        }
        byte[] bArr2 = Bitmaps.header;
        System.arraycopy(bArr2, 0, obj, 0, bArr2.length);
        System.arraycopy(bArr, 3, obj, Bitmaps.header.length, bArr.length - 3);
        System.arraycopy(Bitmaps.footer, 0, obj, (Bitmaps.header.length + bArr.length) - 3, Bitmaps.footer.length);
        obj[164] = bArr[1];
        obj[166] = bArr[2];
        Bitmap decodeByteArray = BitmapFactory.decodeByteArray(obj, 0, length);
        if (!(decodeByteArray == null || TextUtils.isEmpty(str) || !str.contains("b"))) {
            Utilities.blurBitmap(decodeByteArray, 3, 1, decodeByteArray.getWidth(), decodeByteArray.getHeight(), decodeByteArray.getRowBytes());
        }
        return decodeByteArray;
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
                    NotificationCenter.getInstance(i).postNotificationName(NotificationCenter.fileDidLoad, str, file);
                    ImageLoader.this.fileDidLoaded(str, file, i2);
                }

                public void fileDidFailedLoad(String str, int i) {
                    ImageLoader.this.fileProgresses.remove(str);
                    AndroidUtilities.runOnUIThread(new -$$Lambda$ImageLoader$3$9ENgl3dEFjSW9fmN2KOTidfJzZ0(this, str, i, i));
                }

                public /* synthetic */ void lambda$fileDidFailedLoad$6$ImageLoader$3(String str, int i, int i2) {
                    ImageLoader.this.fileDidFailedLoad(str, i);
                    NotificationCenter.getInstance(i2).postNotificationName(NotificationCenter.fileDidFailToLoad, str, Integer.valueOf(i));
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

    /* JADX WARNING: Removed duplicated region for block: B:166:0x0384  */
    /* JADX WARNING: Removed duplicated region for block: B:165:0x037a  */
    /* JADX WARNING: Removed duplicated region for block: B:165:0x037a  */
    /* JADX WARNING: Removed duplicated region for block: B:166:0x0384  */
    /* JADX WARNING: Removed duplicated region for block: B:66:0x0186  */
    /* JADX WARNING: Removed duplicated region for block: B:70:0x0194  */
    /* JADX WARNING: Removed duplicated region for block: B:83:0x01e7  */
    /* JADX WARNING: Removed duplicated region for block: B:72:0x0198  */
    /* JADX WARNING: Removed duplicated region for block: B:90:0x01f6  */
    /* JADX WARNING: Missing block: B:67:0x018f, code skipped:
            if (r8.exists() == false) goto L_0x0191;
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
        if (r1 == r6) goto L_0x00a0;
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
        if (r5 == 0) goto L_0x006d;
    L_0x0035:
        if (r5 != r4) goto L_0x0040;
    L_0x0037:
        r18 = r3;
        r16 = r4;
        r3 = 1;
        r9 = 1;
        r19 = 0;
        goto L_0x0075;
    L_0x0040:
        if (r5 != r3) goto L_0x0062;
    L_0x0042:
        r18 = r3;
        if (r4 != 0) goto L_0x005b;
    L_0x0046:
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
        goto L_0x0060;
    L_0x005b:
        r16 = r4;
        r9 = 1;
        r19 = 0;
    L_0x0060:
        r3 = 1;
        goto L_0x0075;
    L_0x0062:
        r18 = r3;
        r16 = r4;
        r9 = 1;
        r19 = 0;
        r5.removeImageReceiver(r11);
        goto L_0x0074;
    L_0x006d:
        r18 = r3;
        r16 = r4;
        r9 = 1;
        r19 = 0;
    L_0x0074:
        r3 = 0;
    L_0x0075:
        if (r3 != 0) goto L_0x008a;
    L_0x0077:
        if (r16 == 0) goto L_0x008a;
    L_0x0079:
        r3 = r16;
        r4 = r26;
        r5 = r24;
        r6 = r27;
        r7 = r28;
        r8 = r29;
        r3.addImageReceiver(r4, r5, r6, r7, r8);
        r5 = 1;
        goto L_0x008b;
    L_0x008a:
        r5 = r3;
    L_0x008b:
        if (r5 != 0) goto L_0x00a4;
    L_0x008d:
        if (r18 == 0) goto L_0x00a4;
    L_0x008f:
        r3 = r18;
        r4 = r26;
        r5 = r24;
        r6 = r27;
        r7 = r28;
        r8 = r29;
        r3.addImageReceiver(r4, r5, r6, r7, r8);
        r5 = 1;
        goto L_0x00a4;
    L_0x00a0:
        r9 = 1;
        r19 = 0;
        r5 = 0;
    L_0x00a4:
        if (r5 != 0) goto L_0x04f3;
    L_0x00a6:
        r3 = r13.path;
        r4 = "_";
        r8 = "athumb";
        r16 = 4;
        if (r3 == 0) goto L_0x0108;
    L_0x00b0:
        r7 = "http";
        r7 = r3.startsWith(r7);
        if (r7 != 0) goto L_0x0101;
    L_0x00b8:
        r7 = r3.startsWith(r8);
        if (r7 != 0) goto L_0x0101;
    L_0x00be:
        r7 = "thumb://";
        r7 = r3.startsWith(r7);
        r10 = ":";
        if (r7 == 0) goto L_0x00dd;
    L_0x00c8:
        r7 = 8;
        r7 = r3.indexOf(r10, r7);
        if (r7 < 0) goto L_0x00db;
    L_0x00d0:
        r10 = new java.io.File;
        r7 = r7 + r9;
        r3 = r3.substring(r7);
        r10.<init>(r3);
        goto L_0x00ff;
    L_0x00db:
        r10 = 0;
        goto L_0x00ff;
    L_0x00dd:
        r7 = "vthumb://";
        r7 = r3.startsWith(r7);
        if (r7 == 0) goto L_0x00f9;
    L_0x00e6:
        r7 = 9;
        r7 = r3.indexOf(r10, r7);
        if (r7 < 0) goto L_0x00db;
    L_0x00ee:
        r10 = new java.io.File;
        r7 = r7 + r9;
        r3 = r3.substring(r7);
        r10.<init>(r3);
        goto L_0x00ff;
    L_0x00f9:
        r7 = new java.io.File;
        r7.<init>(r3);
        r10 = r7;
    L_0x00ff:
        r3 = 1;
        goto L_0x0103;
    L_0x0101:
        r3 = 0;
        r10 = 0;
    L_0x0103:
        r20 = r8;
        r5 = r10;
        goto L_0x01f2;
    L_0x0108:
        if (r1 != 0) goto L_0x01ee;
    L_0x010a:
        if (r31 == 0) goto L_0x01ee;
    L_0x010c:
        r3 = r14 instanceof org.telegram.messenger.MessageObject;
        if (r3 == 0) goto L_0x0128;
    L_0x0110:
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
        goto L_0x0140;
    L_0x0128:
        if (r15 == 0) goto L_0x013b;
    L_0x012a:
        r3 = org.telegram.messenger.FileLoader.getPathToAttach(r15, r9);
        r6 = org.telegram.messenger.MessageObject.isVideoDocument(r33);
        if (r6 == 0) goto L_0x0136;
    L_0x0134:
        r6 = 2;
        goto L_0x0137;
    L_0x0136:
        r6 = 3;
    L_0x0137:
        r9 = r6;
        r6 = 0;
        r7 = 1;
        goto L_0x0140;
    L_0x013b:
        r3 = 0;
        r6 = 0;
        r7 = 0;
        r9 = 0;
        r15 = 0;
    L_0x0140:
        if (r15 == 0) goto L_0x01ea;
    L_0x0142:
        if (r34 == 0) goto L_0x017a;
    L_0x0144:
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
        if (r3 != 0) goto L_0x0178;
    L_0x0177:
        goto L_0x017e;
    L_0x0178:
        r3 = 1;
        goto L_0x0180;
    L_0x017a:
        r33 = r3;
        r20 = r8;
    L_0x017e:
        r3 = 0;
        r5 = 0;
    L_0x0180:
        r8 = android.text.TextUtils.isEmpty(r6);
        if (r8 != 0) goto L_0x0191;
    L_0x0186:
        r8 = new java.io.File;
        r8.<init>(r6);
        r6 = r8.exists();
        if (r6 != 0) goto L_0x0192;
    L_0x0191:
        r8 = 0;
    L_0x0192:
        if (r8 != 0) goto L_0x0196;
    L_0x0194:
        r8 = r33;
    L_0x0196:
        if (r5 != 0) goto L_0x01e7;
    L_0x0198:
        r1 = org.telegram.messenger.FileLoader.getAttachFileName(r15);
        r2 = r0.waitingForQualityThumb;
        r2 = r2.get(r1);
        r2 = (org.telegram.messenger.ImageLoader.ThumbGenerateInfo) r2;
        if (r2 != 0) goto L_0x01ba;
    L_0x01a6:
        r2 = new org.telegram.messenger.ImageLoader$ThumbGenerateInfo;
        r3 = 0;
        r2.<init>(r0, r3);
        r2.parentDocument = r15;
        r2.filter = r12;
        r2.big = r7;
        r3 = r0.waitingForQualityThumb;
        r3.put(r1, r2);
    L_0x01ba:
        r3 = r2.imageReceiverArray;
        r3 = r3.contains(r11);
        if (r3 != 0) goto L_0x01d6;
    L_0x01c4:
        r3 = r2.imageReceiverArray;
        r3.add(r11);
        r3 = r2.imageReceiverGuidsArray;
        r4 = java.lang.Integer.valueOf(r29);
        r3.add(r4);
    L_0x01d6:
        r3 = r0.waitingForQualityThumbByTag;
        r3.put(r10, r1);
        r1 = r8.exists();
        if (r1 == 0) goto L_0x01e6;
    L_0x01e1:
        if (r35 == 0) goto L_0x01e6;
    L_0x01e3:
        r0.generateThumb(r9, r8, r2);
    L_0x01e6:
        return;
    L_0x01e7:
        r6 = r3;
        r3 = 1;
        goto L_0x01f3;
    L_0x01ea:
        r20 = r8;
        r3 = 1;
        goto L_0x01f1;
    L_0x01ee:
        r20 = r8;
        r3 = 0;
    L_0x01f1:
        r5 = 0;
    L_0x01f2:
        r6 = 0;
    L_0x01f3:
        r9 = 2;
        if (r1 == r9) goto L_0x04f3;
    L_0x01f6:
        r7 = r30.isEncrypted();
        r10 = new org.telegram.messenger.ImageLoader$CacheImage;
        r8 = 0;
        r10.<init>(r0, r8);
        r13 = r30;
        if (r31 != 0) goto L_0x024f;
    L_0x0204:
        r8 = r13.webFile;
        r8 = org.telegram.messenger.MessageObject.isGifDocument(r8);
        if (r8 != 0) goto L_0x024c;
    L_0x020c:
        r8 = r13.document;
        r8 = org.telegram.messenger.MessageObject.isGifDocument(r8);
        if (r8 != 0) goto L_0x024c;
    L_0x0214:
        r8 = r13.document;
        r8 = org.telegram.messenger.MessageObject.isRoundVideoDocument(r8);
        if (r8 == 0) goto L_0x021d;
    L_0x021c:
        goto L_0x024c;
    L_0x021d:
        r8 = r13.path;
        if (r8 == 0) goto L_0x024f;
    L_0x0221:
        r14 = "vthumb";
        r14 = r8.startsWith(r14);
        if (r14 != 0) goto L_0x024f;
    L_0x022a:
        r14 = "thumb";
        r14 = r8.startsWith(r14);
        if (r14 != 0) goto L_0x024f;
    L_0x0232:
        r14 = "jpg";
        r8 = getHttpUrlExtension(r8, r14);
        r14 = "mp4";
        r14 = r8.equals(r14);
        if (r14 != 0) goto L_0x0248;
    L_0x0240:
        r14 = "gif";
        r8 = r8.equals(r14);
        if (r8 == 0) goto L_0x024f;
    L_0x0248:
        r8 = 1;
        r10.animatedFile = r8;
        goto L_0x024f;
    L_0x024c:
        r8 = 1;
        r10.animatedFile = r8;
    L_0x024f:
        if (r5 != 0) goto L_0x038c;
    L_0x0251:
        r8 = r13.photoSize;
        r8 = r8 instanceof org.telegram.tgnet.TLRPC.TL_photoStrippedSize;
        r14 = "g";
        if (r8 == 0) goto L_0x0260;
    L_0x0259:
        r15 = r36;
        r0 = r5;
        r4 = 1;
    L_0x025d:
        r5 = 0;
        goto L_0x0373;
    L_0x0260:
        r5 = r13.secureDocument;
        if (r5 == 0) goto L_0x0280;
    L_0x0264:
        r10.secureDocument = r5;
        r3 = r10.secureDocument;
        r3 = r3.secureFile;
        r3 = r3.dc_id;
        r4 = -NUM; // 0xfffffffvar_ float:-0.0 double:NaN;
        if (r3 != r4) goto L_0x0272;
    L_0x0270:
        r4 = 1;
        goto L_0x0273;
    L_0x0272:
        r4 = 0;
    L_0x0273:
        r3 = new java.io.File;
        r5 = org.telegram.messenger.FileLoader.getDirectory(r16);
        r3.<init>(r5, r2);
        r15 = r36;
        r0 = r3;
        goto L_0x025d;
    L_0x0280:
        r5 = r14.equals(r12);
        r8 = "application/x-tgsticker";
        r15 = r36;
        if (r5 != 0) goto L_0x02dd;
    L_0x028a:
        r5 = r37;
        if (r15 != 0) goto L_0x0296;
    L_0x028e:
        if (r5 <= 0) goto L_0x0296;
    L_0x0290:
        r9 = r13.path;
        if (r9 != 0) goto L_0x0296;
    L_0x0294:
        if (r7 == 0) goto L_0x02df;
    L_0x0296:
        r4 = new java.io.File;
        r7 = org.telegram.messenger.FileLoader.getDirectory(r16);
        r4.<init>(r7, r2);
        r7 = r4.exists();
        if (r7 == 0) goto L_0x02a9;
    L_0x02a5:
        r25 = r3;
        r6 = 1;
        goto L_0x02cb;
    L_0x02a9:
        r7 = 2;
        if (r15 != r7) goto L_0x02c9;
    L_0x02ac:
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
        goto L_0x02cb;
    L_0x02c9:
        r25 = r3;
    L_0x02cb:
        r3 = r13.document;
        if (r3 == 0) goto L_0x02d7;
    L_0x02cf:
        r3 = r3.mime_type;
        r3 = r8.equals(r3);
        r10.lottieFile = r3;
    L_0x02d7:
        r0 = r4;
        r5 = 0;
        r4 = r25;
        goto L_0x0373;
    L_0x02dd:
        r5 = r37;
    L_0x02df:
        r25 = r3;
        r3 = r13.document;
        if (r3 == 0) goto L_0x0353;
    L_0x02e5:
        r7 = r3 instanceof org.telegram.tgnet.TLRPC.TL_documentEncrypted;
        if (r7 == 0) goto L_0x02f3;
    L_0x02e9:
        r7 = new java.io.File;
        r9 = org.telegram.messenger.FileLoader.getDirectory(r16);
        r7.<init>(r9, r2);
        goto L_0x030e;
    L_0x02f3:
        r7 = org.telegram.messenger.MessageObject.isVideoDocument(r3);
        if (r7 == 0) goto L_0x0304;
    L_0x02f9:
        r7 = new java.io.File;
        r9 = 2;
        r5 = org.telegram.messenger.FileLoader.getDirectory(r9);
        r7.<init>(r5, r2);
        goto L_0x030e;
    L_0x0304:
        r7 = new java.io.File;
        r5 = 3;
        r5 = org.telegram.messenger.FileLoader.getDirectory(r5);
        r7.<init>(r5, r2);
    L_0x030e:
        r5 = r14.equals(r12);
        if (r5 == 0) goto L_0x0341;
    L_0x0314:
        r5 = r7.exists();
        if (r5 != 0) goto L_0x0341;
    L_0x031a:
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
        goto L_0x0343;
    L_0x0341:
        r33 = r6;
    L_0x0343:
        r0 = r3.mime_type;
        r0 = r8.equals(r0);
        r10.lottieFile = r0;
        r5 = r3.size;
        r4 = r25;
        r6 = r33;
        r0 = r7;
        goto L_0x0373;
    L_0x0353:
        r33 = r6;
        r0 = r13.webFile;
        if (r0 == 0) goto L_0x0364;
    L_0x0359:
        r0 = new java.io.File;
        r1 = 3;
        r1 = org.telegram.messenger.FileLoader.getDirectory(r1);
        r0.<init>(r1, r2);
        goto L_0x036d;
    L_0x0364:
        r0 = new java.io.File;
        r1 = org.telegram.messenger.FileLoader.getDirectory(r19);
        r0.<init>(r1, r2);
    L_0x036d:
        r4 = r25;
        r6 = r33;
        goto L_0x025d;
    L_0x0373:
        r1 = r14.equals(r12);
        r3 = 1;
        if (r1 == 0) goto L_0x0384;
    L_0x037a:
        r10.animatedFile = r3;
        r10.size = r5;
        r1 = r28;
        r8 = r0;
        r14 = r6;
        r9 = 1;
        goto L_0x0389;
    L_0x0384:
        r1 = r28;
        r8 = r0;
        r9 = r4;
        r14 = r6;
    L_0x0389:
        r0 = r37;
        goto L_0x039c;
    L_0x038c:
        r15 = r36;
        r0 = r37;
        r25 = r3;
        r33 = r6;
        r3 = 1;
        r9 = r25;
        r1 = r28;
        r14 = r33;
        r8 = r5;
    L_0x039c:
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
        if (r1 == 0) goto L_0x03b9;
    L_0x03b7:
        r10.lottieFile = r6;
    L_0x03b9:
        r1 = 2;
        if (r15 != r1) goto L_0x03d8;
    L_0x03bc:
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
    L_0x03d8:
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
        if (r9 != 0) goto L_0x04cd;
    L_0x03f1:
        if (r14 != 0) goto L_0x04cd;
    L_0x03f3:
        r3 = r25.exists();
        if (r3 == 0) goto L_0x03fb;
    L_0x03f9:
        goto L_0x04cd;
    L_0x03fb:
        r10.url = r2;
        r7 = r21;
        r3 = r7.imageLoadingByUrl;
        r3.put(r2, r10);
        r2 = r13.path;
        if (r2 == 0) goto L_0x045c;
    L_0x0408:
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
        if (r1 == 0) goto L_0x0448;
    L_0x0434:
        r0 = new org.telegram.messenger.ImageLoader$ArtworkLoadTask;
        r0.<init>(r10);
        r10.artworkTask = r0;
        r0 = r7.artworkTasks;
        r1 = r10.artworkTask;
        r0.add(r1);
        r8 = 0;
        r7.runArtworkTasks(r8);
        goto L_0x04f4;
    L_0x0448:
        r8 = 0;
        r1 = new org.telegram.messenger.ImageLoader$HttpImageTask;
        r1.<init>(r10, r0);
        r10.httpTask = r1;
        r0 = r7.httpTasks;
        r1 = r10.httpTask;
        r0.add(r1);
        r7.runHttpTasks(r8);
        goto L_0x04f4;
    L_0x045c:
        r8 = 0;
        r1 = r13.location;
        if (r1 == 0) goto L_0x0481;
    L_0x0461:
        if (r15 != 0) goto L_0x046b;
    L_0x0463:
        if (r0 <= 0) goto L_0x0469;
    L_0x0465:
        r0 = r13.key;
        if (r0 == 0) goto L_0x046b;
    L_0x0469:
        r6 = 1;
        goto L_0x046c;
    L_0x046b:
        r6 = r15;
    L_0x046c:
        r1 = org.telegram.messenger.FileLoader.getInstance(r39);
        r0 = r32;
        if (r22 == 0) goto L_0x0476;
    L_0x0474:
        r5 = 2;
        goto L_0x0477;
    L_0x0476:
        r5 = 1;
    L_0x0477:
        r2 = r30;
        r3 = r32;
        r4 = r38;
        r1.loadFile(r2, r3, r4, r5, r6);
        goto L_0x04bb;
    L_0x0481:
        r0 = r32;
        r1 = r13.document;
        if (r1 == 0) goto L_0x0496;
    L_0x0487:
        r1 = org.telegram.messenger.FileLoader.getInstance(r39);
        r2 = r13.document;
        if (r22 == 0) goto L_0x0491;
    L_0x048f:
        r3 = 2;
        goto L_0x0492;
    L_0x0491:
        r3 = 1;
    L_0x0492:
        r1.loadFile(r2, r0, r3, r15);
        goto L_0x04bb;
    L_0x0496:
        r0 = r13.secureDocument;
        if (r0 == 0) goto L_0x04a9;
    L_0x049a:
        r0 = org.telegram.messenger.FileLoader.getInstance(r39);
        r1 = r13.secureDocument;
        if (r22 == 0) goto L_0x04a4;
    L_0x04a2:
        r2 = 2;
        goto L_0x04a5;
    L_0x04a4:
        r2 = 1;
    L_0x04a5:
        r0.loadFile(r1, r2);
        goto L_0x04bb;
    L_0x04a9:
        r0 = r13.webFile;
        if (r0 == 0) goto L_0x04bb;
    L_0x04ad:
        r0 = org.telegram.messenger.FileLoader.getInstance(r39);
        r1 = r13.webFile;
        if (r22 == 0) goto L_0x04b7;
    L_0x04b5:
        r2 = 2;
        goto L_0x04b8;
    L_0x04b7:
        r2 = 1;
    L_0x04b8:
        r0.loadFile(r1, r2, r15);
    L_0x04bb:
        r0 = r26.isForceLoding();
        if (r0 == 0) goto L_0x04f4;
    L_0x04c1:
        r0 = r7.forceLoadingImages;
        r1 = r10.key;
        r2 = java.lang.Integer.valueOf(r8);
        r0.put(r1, r2);
        goto L_0x04f4;
    L_0x04cd:
        r7 = r21;
        r5 = r25;
        r10.finalFilePath = r5;
        r10.imageLocation = r13;
        r0 = new org.telegram.messenger.ImageLoader$CacheOutTask;
        r0.<init>(r10);
        r10.cacheTask = r0;
        r0 = r7.imageLoadingByKeys;
        r0.put(r12, r10);
        if (r22 == 0) goto L_0x04eb;
    L_0x04e3:
        r0 = r7.cacheThumbOutQueue;
        r1 = r10.cacheTask;
        r0.postRunnable(r1);
        goto L_0x04f4;
    L_0x04eb:
        r0 = r7.cacheOutQueue;
        r1 = r10.cacheTask;
        r0.postRunnable(r1);
        goto L_0x04f4;
    L_0x04f3:
        r7 = r0;
    L_0x04f4:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ImageLoader.lambda$createLoadOperationForImageReceiver$5$ImageLoader(int, java.lang.String, java.lang.String, int, org.telegram.messenger.ImageReceiver, java.lang.String, int, int, org.telegram.messenger.ImageLocation, boolean, java.lang.Object, org.telegram.tgnet.TLRPC$Document, boolean, boolean, int, int, java.lang.String, int):void");
    }

    /* JADX WARNING: Removed duplicated region for block: B:153:0x02ce  */
    /* JADX WARNING: Removed duplicated region for block: B:152:0x02c9  */
    /* JADX WARNING: Removed duplicated region for block: B:235:0x02e3 A:{SYNTHETIC} */
    /* JADX WARNING: Removed duplicated region for block: B:155:0x02d4  */
    /* JADX WARNING: Removed duplicated region for block: B:138:0x028e  */
    /* JADX WARNING: Removed duplicated region for block: B:132:0x026e  */
    /* JADX WARNING: Removed duplicated region for block: B:152:0x02c9  */
    /* JADX WARNING: Removed duplicated region for block: B:153:0x02ce  */
    /* JADX WARNING: Removed duplicated region for block: B:235:0x02e3 A:{SYNTHETIC} */
    /* JADX WARNING: Removed duplicated region for block: B:155:0x02d4  */
    /* JADX WARNING: Removed duplicated region for block: B:153:0x02ce  */
    /* JADX WARNING: Removed duplicated region for block: B:152:0x02c9  */
    /* JADX WARNING: Removed duplicated region for block: B:235:0x02e3 A:{SYNTHETIC} */
    /* JADX WARNING: Removed duplicated region for block: B:155:0x02d4  */
    /* JADX WARNING: Removed duplicated region for block: B:152:0x02c9  */
    /* JADX WARNING: Removed duplicated region for block: B:153:0x02ce  */
    /* JADX WARNING: Removed duplicated region for block: B:235:0x02e3 A:{SYNTHETIC} */
    /* JADX WARNING: Removed duplicated region for block: B:155:0x02d4  */
    /* JADX WARNING: Removed duplicated region for block: B:78:0x013a  */
    /* JADX WARNING: Removed duplicated region for block: B:232:0x02f1 A:{SYNTHETIC, EDGE_INSN: B:232:0x02f1->B:159:0x02f1 ?: BREAK  , EDGE_INSN: B:232:0x02f1->B:159:0x02f1 ?: BREAK  } */
    /* JADX WARNING: Removed duplicated region for block: B:82:0x014f  */
    /* JADX WARNING: Removed duplicated region for block: B:177:0x035b  */
    /* JADX WARNING: Removed duplicated region for block: B:161:0x02fa  */
    /* JADX WARNING: Removed duplicated region for block: B:219:0x0448  */
    /* JADX WARNING: Removed duplicated region for block: B:202:0x03eb  */
    /* JADX WARNING: Removed duplicated region for block: B:73:0x012a  */
    /* JADX WARNING: Removed duplicated region for block: B:71:0x011d  */
    /* JADX WARNING: Removed duplicated region for block: B:78:0x013a  */
    /* JADX WARNING: Removed duplicated region for block: B:82:0x014f  */
    /* JADX WARNING: Removed duplicated region for block: B:232:0x02f1 A:{SYNTHETIC, EDGE_INSN: B:232:0x02f1->B:159:0x02f1 ?: BREAK  , EDGE_INSN: B:232:0x02f1->B:159:0x02f1 ?: BREAK  , EDGE_INSN: B:232:0x02f1->B:159:0x02f1 ?: BREAK  } */
    /* JADX WARNING: Removed duplicated region for block: B:161:0x02fa  */
    /* JADX WARNING: Removed duplicated region for block: B:177:0x035b  */
    /* JADX WARNING: Removed duplicated region for block: B:180:0x0364 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:185:0x0382 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:194:0x03b6  */
    /* JADX WARNING: Removed duplicated region for block: B:202:0x03eb  */
    /* JADX WARNING: Removed duplicated region for block: B:219:0x0448  */
    /* JADX WARNING: Removed duplicated region for block: B:44:0x00a7  */
    /* JADX WARNING: Removed duplicated region for block: B:71:0x011d  */
    /* JADX WARNING: Removed duplicated region for block: B:73:0x012a  */
    /* JADX WARNING: Removed duplicated region for block: B:78:0x013a  */
    /* JADX WARNING: Removed duplicated region for block: B:232:0x02f1 A:{SYNTHETIC, EDGE_INSN: B:232:0x02f1->B:159:0x02f1 ?: BREAK  , EDGE_INSN: B:232:0x02f1->B:159:0x02f1 ?: BREAK  , EDGE_INSN: B:232:0x02f1->B:159:0x02f1 ?: BREAK  , EDGE_INSN: B:232:0x02f1->B:159:0x02f1 ?: BREAK  } */
    /* JADX WARNING: Removed duplicated region for block: B:82:0x014f  */
    /* JADX WARNING: Removed duplicated region for block: B:177:0x035b  */
    /* JADX WARNING: Removed duplicated region for block: B:161:0x02fa  */
    /* JADX WARNING: Removed duplicated region for block: B:180:0x0364 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:185:0x0382 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:194:0x03b6  */
    /* JADX WARNING: Removed duplicated region for block: B:219:0x0448  */
    /* JADX WARNING: Removed duplicated region for block: B:202:0x03eb  */
    /* JADX WARNING: Removed duplicated region for block: B:34:0x0083  */
    /* JADX WARNING: Removed duplicated region for block: B:36:0x008a  */
    /* JADX WARNING: Removed duplicated region for block: B:44:0x00a7  */
    /* JADX WARNING: Removed duplicated region for block: B:73:0x012a  */
    /* JADX WARNING: Removed duplicated region for block: B:71:0x011d  */
    /* JADX WARNING: Removed duplicated region for block: B:78:0x013a  */
    /* JADX WARNING: Removed duplicated region for block: B:82:0x014f  */
    /* JADX WARNING: Removed duplicated region for block: B:232:0x02f1 A:{SYNTHETIC, EDGE_INSN: B:232:0x02f1->B:159:0x02f1 ?: BREAK  , EDGE_INSN: B:232:0x02f1->B:159:0x02f1 ?: BREAK  , EDGE_INSN: B:232:0x02f1->B:159:0x02f1 ?: BREAK  , EDGE_INSN: B:232:0x02f1->B:159:0x02f1 ?: BREAK  , EDGE_INSN: B:232:0x02f1->B:159:0x02f1 ?: BREAK  } */
    /* JADX WARNING: Removed duplicated region for block: B:161:0x02fa  */
    /* JADX WARNING: Removed duplicated region for block: B:177:0x035b  */
    /* JADX WARNING: Removed duplicated region for block: B:180:0x0364 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:185:0x0382 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:194:0x03b6  */
    /* JADX WARNING: Removed duplicated region for block: B:202:0x03eb  */
    /* JADX WARNING: Removed duplicated region for block: B:219:0x0448  */
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
        if (r6 == 0) goto L_0x0055;
    L_0x0013:
        r0 = r31.getMediaLocation();
        if (r0 == 0) goto L_0x002e;
    L_0x0019:
        r1 = r0.document;
        r1 = org.telegram.messenger.MessageObject.isAnimatedStickerDocument(r1, r8);
        if (r1 != 0) goto L_0x0025;
    L_0x0021:
        r0 = r0.lottieAnimation;
        if (r0 == 0) goto L_0x002e;
    L_0x0025:
        r0 = r12.lottieMemCache;
        r0 = r0.get(r6);
        r0 = (android.graphics.drawable.Drawable) r0;
        goto L_0x003d;
    L_0x002e:
        r0 = r12.memCache;
        r0 = r0.get(r6);
        r0 = (android.graphics.drawable.Drawable) r0;
        if (r0 == 0) goto L_0x003d;
    L_0x0038:
        r1 = r12.memCache;
        r1.moveToFront(r6);
    L_0x003d:
        r1 = r0;
        if (r1 == 0) goto L_0x0055;
    L_0x0040:
        r12.cancelLoadingForImageReceiver(r13, r8);
        r3 = 3;
        r4 = 1;
        r0 = r31;
        r2 = r6;
        r5 = r14;
        r0.setImageBitmapByKey(r1, r2, r3, r4, r5);
        r0 = r31.isForcePreview();
        if (r0 != 0) goto L_0x0053;
    L_0x0052:
        return;
    L_0x0053:
        r0 = 1;
        goto L_0x0056;
    L_0x0055:
        r0 = 0;
    L_0x0056:
        r2 = r31.getImageKey();
        if (r0 != 0) goto L_0x00a0;
    L_0x005c:
        if (r2 == 0) goto L_0x00a0;
    L_0x005e:
        r1 = r31.getImageLocation();
        if (r1 == 0) goto L_0x0079;
    L_0x0064:
        r3 = r1.document;
        r3 = org.telegram.messenger.MessageObject.isAnimatedStickerDocument(r3, r8);
        if (r3 != 0) goto L_0x0070;
    L_0x006c:
        r1 = r1.lottieAnimation;
        if (r1 == 0) goto L_0x0079;
    L_0x0070:
        r1 = r12.lottieMemCache;
        r1 = r1.get(r2);
        r1 = (android.graphics.drawable.Drawable) r1;
        goto L_0x0088;
    L_0x0079:
        r1 = r12.memCache;
        r1 = r1.get(r2);
        r1 = (android.graphics.drawable.Drawable) r1;
        if (r1 == 0) goto L_0x0088;
    L_0x0083:
        r3 = r12.memCache;
        r3.moveToFront(r2);
    L_0x0088:
        if (r1 == 0) goto L_0x00a0;
    L_0x008a:
        r12.cancelLoadingForImageReceiver(r13, r8);
        r3 = 0;
        r4 = 1;
        r0 = r31;
        r5 = r14;
        r0.setImageBitmapByKey(r1, r2, r3, r4, r5);
        r0 = r31.isForcePreview();
        if (r0 != 0) goto L_0x009e;
    L_0x009b:
        if (r6 != 0) goto L_0x009e;
    L_0x009d:
        return;
    L_0x009e:
        r15 = 1;
        goto L_0x00a1;
    L_0x00a0:
        r15 = r0;
    L_0x00a1:
        r2 = r31.getThumbKey();
        if (r2 == 0) goto L_0x00ea;
    L_0x00a7:
        r0 = r31.getThumbLocation();
        if (r0 == 0) goto L_0x00c2;
    L_0x00ad:
        r1 = r0.document;
        r1 = org.telegram.messenger.MessageObject.isAnimatedStickerDocument(r1, r8);
        if (r1 != 0) goto L_0x00b9;
    L_0x00b5:
        r0 = r0.lottieAnimation;
        if (r0 == 0) goto L_0x00c2;
    L_0x00b9:
        r0 = r12.lottieMemCache;
        r0 = r0.get(r2);
        r0 = (android.graphics.drawable.Drawable) r0;
        goto L_0x00d1;
    L_0x00c2:
        r0 = r12.memCache;
        r0 = r0.get(r2);
        r0 = (android.graphics.drawable.Drawable) r0;
        if (r0 == 0) goto L_0x00d1;
    L_0x00cc:
        r1 = r12.memCache;
        r1.moveToFront(r2);
    L_0x00d1:
        r1 = r0;
        if (r1 == 0) goto L_0x00ea;
    L_0x00d4:
        r3 = 1;
        r4 = 1;
        r0 = r31;
        r5 = r14;
        r0.setImageBitmapByKey(r1, r2, r3, r4, r5);
        r12.cancelLoadingForImageReceiver(r13, r7);
        if (r15 == 0) goto L_0x00e8;
    L_0x00e1:
        r0 = r31.isForcePreview();
        if (r0 == 0) goto L_0x00e8;
    L_0x00e7:
        return;
    L_0x00e8:
        r0 = 1;
        goto L_0x00eb;
    L_0x00ea:
        r0 = 0;
    L_0x00eb:
        r1 = r31.getParentObject();
        r2 = r31.getQulityThumbDocument();
        r5 = r31.getThumbLocation();
        r6 = r31.getThumbFilter();
        r3 = r31.getMediaLocation();
        r11 = r31.getMediaFilter();
        r4 = r31.getImageLocation();
        r10 = r31.getImageFilter();
        if (r4 != 0) goto L_0x0131;
    L_0x010d:
        r9 = r31.isNeedsQualityThumb();
        if (r9 == 0) goto L_0x0131;
    L_0x0113:
        r9 = r31.isCurrentKeyQuality();
        if (r9 == 0) goto L_0x0131;
    L_0x0119:
        r9 = r1 instanceof org.telegram.messenger.MessageObject;
        if (r9 == 0) goto L_0x012a;
    L_0x011d:
        r2 = r1;
        r2 = (org.telegram.messenger.MessageObject) r2;
        r2 = r2.getDocument();
        r4 = org.telegram.messenger.ImageLocation.getForDocument(r2);
    L_0x0128:
        r2 = 1;
        goto L_0x0132;
    L_0x012a:
        if (r2 == 0) goto L_0x0131;
    L_0x012c:
        r4 = org.telegram.messenger.ImageLocation.getForDocument(r2);
        goto L_0x0128;
    L_0x0131:
        r2 = 0;
    L_0x0132:
        r9 = r31.getExt();
        r7 = "jpg";
        if (r9 != 0) goto L_0x013b;
    L_0x013a:
        r9 = r7;
    L_0x013b:
        r17 = 0;
        r18 = r3;
        r19 = r17;
        r20 = r19;
        r22 = r20;
        r23 = r22;
        r3 = 0;
        r21 = 0;
    L_0x014a:
        r8 = 2;
        r12 = ".";
        if (r3 >= r8) goto L_0x02f1;
    L_0x014f:
        if (r3 != 0) goto L_0x0153;
    L_0x0151:
        r8 = r4;
        goto L_0x0155;
    L_0x0153:
        r8 = r18;
    L_0x0155:
        if (r8 != 0) goto L_0x0158;
    L_0x0157:
        goto L_0x0164;
    L_0x0158:
        if (r18 == 0) goto L_0x015d;
    L_0x015a:
        r13 = r18;
        goto L_0x015e;
    L_0x015d:
        r13 = r4;
    L_0x015e:
        r13 = r8.getKey(r1, r13);
        if (r13 != 0) goto L_0x016d;
    L_0x0164:
        r27 = r10;
        r26 = r11;
        r24 = r15;
        r15 = 1;
        goto L_0x02e3;
    L_0x016d:
        r24 = r15;
        r15 = r8.path;
        if (r15 == 0) goto L_0x0193;
    L_0x0173:
        r15 = new java.lang.StringBuilder;
        r15.<init>();
        r15.append(r13);
        r15.append(r12);
        r12 = r8.path;
        r12 = getHttpUrlExtension(r12, r7);
        r15.append(r12);
        r12 = r15.toString();
    L_0x018b:
        r27 = r10;
        r26 = r11;
        r10 = r12;
    L_0x0190:
        r15 = 1;
        goto L_0x02c7;
    L_0x0193:
        r15 = r8.photoSize;
        r15 = r15 instanceof org.telegram.tgnet.TLRPC.TL_photoStrippedSize;
        if (r15 == 0) goto L_0x01ac;
    L_0x0199:
        r15 = new java.lang.StringBuilder;
        r15.<init>();
        r15.append(r13);
        r15.append(r12);
        r15.append(r9);
        r12 = r15.toString();
        goto L_0x018b;
    L_0x01ac:
        r15 = r8.location;
        if (r15 == 0) goto L_0x01f2;
    L_0x01b0:
        r15 = new java.lang.StringBuilder;
        r15.<init>();
        r15.append(r13);
        r15.append(r12);
        r15.append(r9);
        r12 = r15.toString();
        r15 = r31.getExt();
        if (r15 != 0) goto L_0x01e5;
    L_0x01c8:
        r15 = r8.location;
        r25 = r12;
        r12 = r15.key;
        r27 = r10;
        r26 = r11;
        if (r12 != 0) goto L_0x01eb;
    L_0x01d4:
        r10 = r15.volume_id;
        r28 = -NUM; // 0xfffffffvar_ float:-0.0 double:NaN;
        r12 = (r10 > r28 ? 1 : (r10 == r28 ? 0 : -1));
        if (r12 != 0) goto L_0x01e2;
    L_0x01dd:
        r10 = r15.local_id;
        if (r10 >= 0) goto L_0x01e2;
    L_0x01e1:
        goto L_0x01eb;
    L_0x01e2:
        r10 = r25;
        goto L_0x0190;
    L_0x01e5:
        r27 = r10;
        r26 = r11;
        r25 = r12;
    L_0x01eb:
        r10 = r25;
        r15 = 1;
        r21 = 1;
        goto L_0x02c7;
    L_0x01f2:
        r27 = r10;
        r26 = r11;
        r10 = r8.webFile;
        if (r10 == 0) goto L_0x021c;
    L_0x01fa:
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
        goto L_0x0190;
    L_0x021c:
        r10 = r8.secureDocument;
        if (r10 == 0) goto L_0x0234;
    L_0x0220:
        r10 = new java.lang.StringBuilder;
        r10.<init>();
        r10.append(r13);
        r10.append(r12);
        r10.append(r9);
        r10 = r10.toString();
        goto L_0x0190;
    L_0x0234:
        r10 = r8.document;
        if (r10 == 0) goto L_0x02c4;
    L_0x0238:
        if (r3 != 0) goto L_0x024d;
    L_0x023a:
        if (r2 == 0) goto L_0x024d;
    L_0x023c:
        r10 = new java.lang.StringBuilder;
        r10.<init>();
        r11 = "q_";
        r10.append(r11);
        r10.append(r13);
        r13 = r10.toString();
    L_0x024d:
        r10 = r8.document;
        r10 = org.telegram.messenger.FileLoader.getDocumentFileName(r10);
        r11 = "";
        if (r10 == 0) goto L_0x0266;
    L_0x0257:
        r12 = 46;
        r12 = r10.lastIndexOf(r12);
        r15 = -1;
        if (r12 != r15) goto L_0x0261;
    L_0x0260:
        goto L_0x0266;
    L_0x0261:
        r10 = r10.substring(r12);
        goto L_0x0267;
    L_0x0266:
        r10 = r11;
    L_0x0267:
        r12 = r10.length();
        r15 = 1;
        if (r12 > r15) goto L_0x028e;
    L_0x026e:
        r10 = r8.document;
        r10 = r10.mime_type;
        r12 = "video/mp4";
        r10 = r12.equals(r10);
        if (r10 == 0) goto L_0x027e;
    L_0x027b:
        r11 = ".mp4";
        goto L_0x028f;
    L_0x027e:
        r10 = r8.document;
        r10 = r10.mime_type;
        r12 = "video/x-matroska";
        r10 = r12.equals(r10);
        if (r10 == 0) goto L_0x028f;
    L_0x028b:
        r11 = ".mkv";
        goto L_0x028f;
    L_0x028e:
        r11 = r10;
    L_0x028f:
        r10 = new java.lang.StringBuilder;
        r10.<init>();
        r10.append(r13);
        r10.append(r11);
        r10 = r10.toString();
        r11 = r8.document;
        r11 = org.telegram.messenger.MessageObject.isVideoDocument(r11);
        if (r11 != 0) goto L_0x02c0;
    L_0x02a6:
        r11 = r8.document;
        r11 = org.telegram.messenger.MessageObject.isGifDocument(r11);
        if (r11 != 0) goto L_0x02c0;
    L_0x02ae:
        r11 = r8.document;
        r11 = org.telegram.messenger.MessageObject.isRoundVideoDocument(r11);
        if (r11 != 0) goto L_0x02c0;
    L_0x02b6:
        r11 = r8.document;
        r11 = org.telegram.messenger.MessageObject.canPreviewDocument(r11);
        if (r11 != 0) goto L_0x02c0;
    L_0x02be:
        r11 = 1;
        goto L_0x02c1;
    L_0x02c0:
        r11 = 0;
    L_0x02c1:
        r21 = r11;
        goto L_0x02c7;
    L_0x02c4:
        r15 = 1;
        r10 = r17;
    L_0x02c7:
        if (r3 != 0) goto L_0x02ce;
    L_0x02c9:
        r22 = r10;
        r20 = r13;
        goto L_0x02d2;
    L_0x02ce:
        r23 = r10;
        r19 = r13;
    L_0x02d2:
        if (r8 != r5) goto L_0x02e3;
    L_0x02d4:
        if (r3 != 0) goto L_0x02dd;
    L_0x02d6:
        r4 = r17;
        r20 = r4;
        r22 = r20;
        goto L_0x02e3;
    L_0x02dd:
        r18 = r17;
        r19 = r18;
        r23 = r19;
    L_0x02e3:
        r3 = r3 + 1;
        r12 = r30;
        r13 = r31;
        r15 = r24;
        r11 = r26;
        r10 = r27;
        goto L_0x014a;
    L_0x02f1:
        r27 = r10;
        r26 = r11;
        r24 = r15;
        r15 = 1;
        if (r5 == 0) goto L_0x035b;
    L_0x02fa:
        r2 = r31.getStrippedLocation();
        if (r2 != 0) goto L_0x0306;
    L_0x0300:
        if (r18 == 0) goto L_0x0305;
    L_0x0302:
        r2 = r18;
        goto L_0x0306;
    L_0x0305:
        r2 = r4;
    L_0x0306:
        r1 = r5.getKey(r1, r2);
        r2 = r5.path;
        if (r2 == 0) goto L_0x0328;
    L_0x030e:
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r2.append(r1);
        r2.append(r12);
        r3 = r5.path;
        r3 = getHttpUrlExtension(r3, r7);
        r2.append(r3);
        r2 = r2.toString();
    L_0x0326:
        r3 = r2;
        goto L_0x035e;
    L_0x0328:
        r2 = r5.photoSize;
        r2 = r2 instanceof org.telegram.tgnet.TLRPC.TL_photoStrippedSize;
        if (r2 == 0) goto L_0x0341;
    L_0x032e:
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r2.append(r1);
        r2.append(r12);
        r2.append(r9);
        r2 = r2.toString();
        goto L_0x0326;
    L_0x0341:
        r2 = r5.location;
        if (r2 == 0) goto L_0x0358;
    L_0x0345:
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r2.append(r1);
        r2.append(r12);
        r2.append(r9);
        r2 = r2.toString();
        goto L_0x0326;
    L_0x0358:
        r3 = r17;
        goto L_0x035e;
    L_0x035b:
        r1 = r17;
        r3 = r1;
    L_0x035e:
        r2 = "@";
        r7 = r19;
        if (r7 == 0) goto L_0x037b;
    L_0x0364:
        if (r26 == 0) goto L_0x037b;
    L_0x0366:
        r10 = new java.lang.StringBuilder;
        r10.<init>();
        r10.append(r7);
        r10.append(r2);
        r11 = r26;
        r10.append(r11);
        r7 = r10.toString();
        goto L_0x037d;
    L_0x037b:
        r11 = r26;
    L_0x037d:
        r12 = r7;
        r7 = r20;
        if (r7 == 0) goto L_0x0399;
    L_0x0382:
        if (r27 == 0) goto L_0x0399;
    L_0x0384:
        r10 = new java.lang.StringBuilder;
        r10.<init>();
        r10.append(r7);
        r10.append(r2);
        r13 = r27;
        r10.append(r13);
        r7 = r10.toString();
        goto L_0x039b;
    L_0x0399:
        r13 = r27;
    L_0x039b:
        r16 = r7;
        if (r1 == 0) goto L_0x03b3;
    L_0x039f:
        if (r6 == 0) goto L_0x03b3;
    L_0x03a1:
        r7 = new java.lang.StringBuilder;
        r7.<init>();
        r7.append(r1);
        r7.append(r2);
        r7.append(r6);
        r1 = r7.toString();
    L_0x03b3:
        r2 = r1;
        if (r4 == 0) goto L_0x03e5;
    L_0x03b6:
        r1 = r4.path;
        if (r1 == 0) goto L_0x03e5;
    L_0x03ba:
        r7 = 0;
        r10 = 1;
        r11 = 1;
        if (r0 == 0) goto L_0x03c0;
    L_0x03bf:
        r15 = 2;
    L_0x03c0:
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
        goto L_0x047b;
    L_0x03e5:
        r17 = r4;
        r19 = r9;
        if (r18 == 0) goto L_0x0448;
    L_0x03eb:
        r1 = r31.getCacheType();
        r20 = 1;
        if (r1 != 0) goto L_0x03f8;
    L_0x03f3:
        if (r21 == 0) goto L_0x03f8;
    L_0x03f5:
        r21 = 1;
        goto L_0x03fa;
    L_0x03f8:
        r21 = r1;
    L_0x03fa:
        if (r21 != 0) goto L_0x03fe;
    L_0x03fc:
        r9 = 1;
        goto L_0x0400;
    L_0x03fe:
        r9 = r21;
    L_0x0400:
        if (r0 != 0) goto L_0x0416;
    L_0x0402:
        r7 = 0;
        r10 = 1;
        if (r0 == 0) goto L_0x0407;
    L_0x0406:
        r15 = 2;
    L_0x0407:
        r0 = r30;
        r1 = r31;
        r4 = r19;
        r8 = r9;
        r9 = r10;
        r10 = r15;
        r15 = r11;
        r11 = r14;
        r0.createLoadOperationForImageReceiver(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r11);
        goto L_0x0417;
    L_0x0416:
        r15 = r11;
    L_0x0417:
        if (r24 != 0) goto L_0x042f;
    L_0x0419:
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
    L_0x042f:
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
        goto L_0x047b;
    L_0x0448:
        r1 = r31.getCacheType();
        if (r1 != 0) goto L_0x0452;
    L_0x044e:
        if (r21 == 0) goto L_0x0452;
    L_0x0450:
        r12 = 1;
        goto L_0x0453;
    L_0x0452:
        r12 = r1;
    L_0x0453:
        if (r12 != 0) goto L_0x0457;
    L_0x0455:
        r9 = 1;
        goto L_0x0458;
    L_0x0457:
        r9 = r12;
    L_0x0458:
        r7 = 0;
        r10 = 1;
        if (r0 == 0) goto L_0x045d;
    L_0x045c:
        r15 = 2;
    L_0x045d:
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
    L_0x047b:
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

    /* JADX WARNING: Removed duplicated region for block: B:95:0x013a  */
    /* JADX WARNING: Removed duplicated region for block: B:63:0x00d1 A:{SYNTHETIC, Splitter:B:63:0x00d1} */
    /* JADX WARNING: Removed duplicated region for block: B:25:0x0065  */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x0060  */
    /* JADX WARNING: Removed duplicated region for block: B:28:0x006f  */
    /* JADX WARNING: Removed duplicated region for block: B:31:0x007d  */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x008e  */
    /* JADX WARNING: Removed duplicated region for block: B:42:0x0095  */
    /* JADX WARNING: Removed duplicated region for block: B:41:0x0093  */
    /* JADX WARNING: Removed duplicated region for block: B:46:0x009f A:{SYNTHETIC, Splitter:B:46:0x009f} */
    /* JADX WARNING: Removed duplicated region for block: B:63:0x00d1 A:{SYNTHETIC, Splitter:B:63:0x00d1} */
    /* JADX WARNING: Removed duplicated region for block: B:95:0x013a  */
    /* JADX WARNING: Removed duplicated region for block: B:80:0x010a A:{SYNTHETIC, Splitter:B:80:0x010a} */
    /* JADX WARNING: Removed duplicated region for block: B:89:0x011c A:{Catch:{ all -> 0x0118 }} */
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
        r11 = org.telegram.messenger.AndroidUtilities.getPath(r12);	 Catch:{ all -> 0x0028 }
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
        r3 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ all -> 0x0050 }
        r3 = r3.getContentResolver();	 Catch:{ all -> 0x0050 }
        r3 = r3.openInputStream(r12);	 Catch:{ all -> 0x0050 }
        android.graphics.BitmapFactory.decodeStream(r3, r2, r0);	 Catch:{ all -> 0x0050 }
        r3.close();	 Catch:{ all -> 0x0050 }
        r3 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ all -> 0x0050 }
        r3 = r3.getContentResolver();	 Catch:{ all -> 0x0050 }
        r3 = r3.openInputStream(r12);	 Catch:{ all -> 0x0050 }
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
        r4 = r4 / r13;
        r5 = r5 / r14;
        if (r15 == 0) goto L_0x0065;
    L_0x0060:
        r13 = java.lang.Math.max(r4, r5);
        goto L_0x0069;
    L_0x0065:
        r13 = java.lang.Math.min(r4, r5);
    L_0x0069:
        r14 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r15 = (r13 > r14 ? 1 : (r13 == r14 ? 0 : -1));
        if (r15 >= 0) goto L_0x0071;
    L_0x006f:
        r13 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
    L_0x0071:
        r14 = 0;
        r0.inJustDecodeBounds = r14;
        r13 = (int) r13;
        r0.inSampleSize = r13;
        r13 = r0.inSampleSize;
        r13 = r13 % 2;
        if (r13 == 0) goto L_0x0088;
    L_0x007d:
        r13 = 1;
    L_0x007e:
        r15 = r13 * 2;
        r4 = r0.inSampleSize;
        if (r15 >= r4) goto L_0x0086;
    L_0x0084:
        r13 = r15;
        goto L_0x007e;
    L_0x0086:
        r0.inSampleSize = r13;
    L_0x0088:
        r13 = android.os.Build.VERSION.SDK_INT;
        r15 = 21;
        if (r13 >= r15) goto L_0x008f;
    L_0x008e:
        r14 = 1;
    L_0x008f:
        r0.inPurgeable = r14;
        if (r11 == 0) goto L_0x0095;
    L_0x0093:
        r13 = r11;
        goto L_0x009d;
    L_0x0095:
        if (r12 == 0) goto L_0x009c;
    L_0x0097:
        r13 = org.telegram.messenger.AndroidUtilities.getPath(r12);
        goto L_0x009d;
    L_0x009c:
        r13 = r2;
    L_0x009d:
        if (r13 == 0) goto L_0x00ce;
    L_0x009f:
        r14 = new androidx.exifinterface.media.ExifInterface;	 Catch:{ all -> 0x00ce }
        r14.<init>(r13);	 Catch:{ all -> 0x00ce }
        r13 = "Orientation";
        r13 = r14.getAttributeInt(r13, r1);	 Catch:{ all -> 0x00ce }
        r14 = new android.graphics.Matrix;	 Catch:{ all -> 0x00ce }
        r14.<init>();	 Catch:{ all -> 0x00ce }
        r15 = 3;
        if (r13 == r15) goto L_0x00c6;
    L_0x00b2:
        r15 = 6;
        if (r13 == r15) goto L_0x00c0;
    L_0x00b5:
        r15 = 8;
        if (r13 == r15) goto L_0x00ba;
    L_0x00b9:
        goto L_0x00cf;
    L_0x00ba:
        r13 = NUM; // 0x43870000 float:270.0 double:5.597372625E-315;
        r14.postRotate(r13);	 Catch:{ all -> 0x00cc }
        goto L_0x00cf;
    L_0x00c0:
        r13 = NUM; // 0x42b40000 float:90.0 double:5.529052754E-315;
        r14.postRotate(r13);	 Catch:{ all -> 0x00cc }
        goto L_0x00cf;
    L_0x00c6:
        r13 = NUM; // 0x43340000 float:180.0 double:5.570497984E-315;
        r14.postRotate(r13);	 Catch:{ all -> 0x00cc }
        goto L_0x00cf;
        goto L_0x00cf;
    L_0x00ce:
        r14 = r2;
    L_0x00cf:
        if (r11 == 0) goto L_0x013a;
    L_0x00d1:
        r12 = android.graphics.BitmapFactory.decodeFile(r11, r0);	 Catch:{ all -> 0x00fc }
        if (r12 == 0) goto L_0x00f9;
    L_0x00d7:
        r13 = r0.inPurgeable;	 Catch:{ all -> 0x00f7 }
        if (r13 == 0) goto L_0x00de;
    L_0x00db:
        org.telegram.messenger.Utilities.pinBitmap(r12);	 Catch:{ all -> 0x00f7 }
    L_0x00de:
        r5 = 0;
        r6 = 0;
        r7 = r12.getWidth();	 Catch:{ all -> 0x00f7 }
        r8 = r12.getHeight();	 Catch:{ all -> 0x00f7 }
        r10 = 1;
        r4 = r12;
        r9 = r14;
        r13 = org.telegram.messenger.Bitmaps.createBitmap(r4, r5, r6, r7, r8, r9, r10);	 Catch:{ all -> 0x00f7 }
        if (r13 == r12) goto L_0x00f9;
    L_0x00f1:
        r12.recycle();	 Catch:{ all -> 0x00f7 }
        r2 = r13;
        goto L_0x0180;
    L_0x00f7:
        r13 = move-exception;
        goto L_0x00fe;
    L_0x00f9:
        r2 = r12;
        goto L_0x0180;
    L_0x00fc:
        r13 = move-exception;
        r12 = r2;
    L_0x00fe:
        org.telegram.messenger.FileLog.e(r13);
        r13 = getInstance();
        r13.clearMemory();
        if (r12 != 0) goto L_0x011a;
    L_0x010a:
        r12 = android.graphics.BitmapFactory.decodeFile(r11, r0);	 Catch:{ all -> 0x0118 }
        if (r12 == 0) goto L_0x011a;
    L_0x0110:
        r11 = r0.inPurgeable;	 Catch:{ all -> 0x0118 }
        if (r11 == 0) goto L_0x011a;
    L_0x0114:
        org.telegram.messenger.Utilities.pinBitmap(r12);	 Catch:{ all -> 0x0118 }
        goto L_0x011a;
    L_0x0118:
        r11 = move-exception;
        goto L_0x0133;
    L_0x011a:
        if (r12 == 0) goto L_0x0137;
    L_0x011c:
        r5 = 0;
        r6 = 0;
        r7 = r12.getWidth();	 Catch:{ all -> 0x0118 }
        r8 = r12.getHeight();	 Catch:{ all -> 0x0118 }
        r10 = 1;
        r4 = r12;
        r9 = r14;
        r11 = org.telegram.messenger.Bitmaps.createBitmap(r4, r5, r6, r7, r8, r9, r10);	 Catch:{ all -> 0x0118 }
        if (r11 == r12) goto L_0x0137;
    L_0x012f:
        r12.recycle();	 Catch:{ all -> 0x0118 }
        goto L_0x0138;
    L_0x0133:
        org.telegram.messenger.FileLog.e(r11);
        goto L_0x00f9;
    L_0x0137:
        r11 = r12;
    L_0x0138:
        r2 = r11;
        goto L_0x0180;
    L_0x013a:
        if (r12 == 0) goto L_0x0180;
    L_0x013c:
        r11 = android.graphics.BitmapFactory.decodeStream(r3, r2, r0);	 Catch:{ all -> 0x016e }
        if (r11 == 0) goto L_0x0164;
    L_0x0142:
        r12 = r0.inPurgeable;	 Catch:{ all -> 0x0161 }
        if (r12 == 0) goto L_0x0149;
    L_0x0146:
        org.telegram.messenger.Utilities.pinBitmap(r11);	 Catch:{ all -> 0x0161 }
    L_0x0149:
        r5 = 0;
        r6 = 0;
        r7 = r11.getWidth();	 Catch:{ all -> 0x0161 }
        r8 = r11.getHeight();	 Catch:{ all -> 0x0161 }
        r10 = 1;
        r4 = r11;
        r9 = r14;
        r12 = org.telegram.messenger.Bitmaps.createBitmap(r4, r5, r6, r7, r8, r9, r10);	 Catch:{ all -> 0x0161 }
        if (r12 == r11) goto L_0x0164;
    L_0x015c:
        r11.recycle();	 Catch:{ all -> 0x0161 }
        r2 = r12;
        goto L_0x0165;
    L_0x0161:
        r12 = move-exception;
        r2 = r11;
        goto L_0x016f;
    L_0x0164:
        r2 = r11;
    L_0x0165:
        r3.close();	 Catch:{ all -> 0x0169 }
        goto L_0x0180;
    L_0x0169:
        r11 = move-exception;
        org.telegram.messenger.FileLog.e(r11);
        goto L_0x0180;
    L_0x016e:
        r12 = move-exception;
    L_0x016f:
        org.telegram.messenger.FileLog.e(r12);	 Catch:{ all -> 0x0176 }
        r3.close();	 Catch:{ all -> 0x0169 }
        goto L_0x0180;
    L_0x0176:
        r11 = move-exception;
        r3.close();	 Catch:{ all -> 0x017b }
        goto L_0x017f;
    L_0x017b:
        r12 = move-exception;
        org.telegram.messenger.FileLog.e(r12);
    L_0x017f:
        throw r11;
    L_0x0180:
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

    /* JADX WARNING: Removed duplicated region for block: B:34:0x00b0  */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x00ab  */
    /* JADX WARNING: Removed duplicated region for block: B:41:0x00e9  */
    /* JADX WARNING: Removed duplicated region for block: B:40:0x00d0  */
    /* JADX WARNING: Removed duplicated region for block: B:44:0x00f9  */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x00ab  */
    /* JADX WARNING: Removed duplicated region for block: B:34:0x00b0  */
    /* JADX WARNING: Removed duplicated region for block: B:40:0x00d0  */
    /* JADX WARNING: Removed duplicated region for block: B:41:0x00e9  */
    /* JADX WARNING: Removed duplicated region for block: B:44:0x00f9  */
    /* JADX WARNING: Removed duplicated region for block: B:34:0x00b0  */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x00ab  */
    /* JADX WARNING: Removed duplicated region for block: B:41:0x00e9  */
    /* JADX WARNING: Removed duplicated region for block: B:40:0x00d0  */
    /* JADX WARNING: Removed duplicated region for block: B:44:0x00f9  */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x00ab  */
    /* JADX WARNING: Removed duplicated region for block: B:34:0x00b0  */
    /* JADX WARNING: Removed duplicated region for block: B:40:0x00d0  */
    /* JADX WARNING: Removed duplicated region for block: B:41:0x00e9  */
    /* JADX WARNING: Removed duplicated region for block: B:44:0x00f9  */
    private static org.telegram.tgnet.TLRPC.PhotoSize scaleAndSaveImageInternal(org.telegram.tgnet.TLRPC.PhotoSize r3, android.graphics.Bitmap r4, int r5, int r6, float r7, float r8, float r9, int r10, boolean r11, boolean r12, boolean r13) throws java.lang.Exception {
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
        r0 = 4;
        if (r13 == 0) goto L_0x00b0;
    L_0x00ab:
        r6 = org.telegram.messenger.FileLoader.getDirectory(r0);
        goto L_0x00bf;
    L_0x00b0:
        r1 = r9.volume_id;
        r9 = (r1 > r7 ? 1 : (r1 == r7 ? 0 : -1));
        if (r9 == 0) goto L_0x00bb;
    L_0x00b6:
        r6 = org.telegram.messenger.FileLoader.getDirectory(r6);
        goto L_0x00bf;
    L_0x00bb:
        r6 = org.telegram.messenger.FileLoader.getDirectory(r0);
    L_0x00bf:
        r7 = new java.io.File;
        r7.<init>(r6, r12);
        r6 = new java.io.FileOutputStream;
        r6.<init>(r7);
        r7 = android.graphics.Bitmap.CompressFormat.JPEG;
        r5.compress(r7, r10, r6);
        if (r11 == 0) goto L_0x00e9;
    L_0x00d0:
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
        goto L_0x00f4;
    L_0x00e9:
        r7 = r6.getChannel();
        r7 = r7.size();
        r8 = (int) r7;
        r3.size = r8;
    L_0x00f4:
        r6.close();
        if (r5 == r4) goto L_0x00fc;
    L_0x00f9:
        r5.recycle();
    L_0x00fc:
        return r3;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ImageLoader.scaleAndSaveImageInternal(org.telegram.tgnet.TLRPC$PhotoSize, android.graphics.Bitmap, int, int, float, float, float, int, boolean, boolean, boolean):org.telegram.tgnet.TLRPC$PhotoSize");
    }

    public static PhotoSize scaleAndSaveImage(Bitmap bitmap, float f, float f2, int i, boolean z) {
        return scaleAndSaveImage(null, bitmap, f, f2, i, z, 0, 0, false);
    }

    public static PhotoSize scaleAndSaveImage(PhotoSize photoSize, Bitmap bitmap, float f, float f2, int i, boolean z, boolean z2) {
        return scaleAndSaveImage(photoSize, bitmap, f, f2, i, z, 0, 0, z2);
    }

    public static PhotoSize scaleAndSaveImage(Bitmap bitmap, float f, float f2, int i, boolean z, int i2, int i3) {
        return scaleAndSaveImage(null, bitmap, f, f2, i, z, i2, i3, false);
    }

    public static PhotoSize scaleAndSaveImage(PhotoSize photoSize, Bitmap bitmap, float f, float f2, int i, boolean z, int i2, int i3, boolean z2) {
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
            boolean z3;
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
                    z3 = true;
                    i8 = (int) (width / f3);
                    i9 = (int) (height / f3);
                    if (!(i9 == 0 || i8 == 0)) {
                        i4 = i9;
                        i5 = i8;
                        return scaleAndSaveImageInternal(photoSize, bitmap, i8, i9, width, height, f3, i, z, z3, z2);
                    }
                }
            }
            f3 = max;
            z3 = false;
            i8 = (int) (width / f3);
            i9 = (int) (height / f3);
            i4 = i9;
            i5 = i8;
            try {
                return scaleAndSaveImageInternal(photoSize, bitmap, i8, i9, width, height, f3, i, z, z3, z2);
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

    public static void saveMessageThumbs(Message message) {
        Message message2 = message;
        MessageMedia messageMedia = message2.media;
        if (messageMedia != null) {
            int size;
            PhotoSize photoSize;
            Bitmap loadBitmap;
            int size2;
            int i;
            if (messageMedia instanceof TL_messageMediaPhoto) {
                size = messageMedia.photo.sizes.size();
                for (i = 0; i < size; i++) {
                    photoSize = (PhotoSize) message2.media.photo.sizes.get(i);
                    if (photoSize instanceof TL_photoCachedSize) {
                        break;
                    }
                }
            } else if (messageMedia instanceof TL_messageMediaDocument) {
                size = messageMedia.document.thumbs.size();
                for (i = 0; i < size; i++) {
                    photoSize = (PhotoSize) message2.media.document.thumbs.get(i);
                    if (photoSize instanceof TL_photoCachedSize) {
                        break;
                    }
                }
            } else if (messageMedia instanceof TL_messageMediaWebPage) {
                Photo photo = messageMedia.webpage.photo;
                if (photo != null) {
                    size = photo.sizes.size();
                    for (i = 0; i < size; i++) {
                        photoSize = (PhotoSize) message2.media.webpage.photo.sizes.get(i);
                        if (photoSize instanceof TL_photoCachedSize) {
                            break;
                        }
                    }
                }
            }
            photoSize = null;
            if (photoSize != null) {
                byte[] bArr = photoSize.bytes;
                if (bArr != null && bArr.length != 0) {
                    File file;
                    Object obj;
                    FileLocation fileLocation = photoSize.location;
                    if (fileLocation == null || (fileLocation instanceof TL_fileLocationUnavailable)) {
                        photoSize.location = new TL_fileLocationToBeDeprecated();
                        fileLocation = photoSize.location;
                        fileLocation.volume_id = -2147483648L;
                        fileLocation.local_id = SharedConfig.getLastLocalId();
                    }
                    File pathToAttach = FileLoader.getPathToAttach(photoSize, true);
                    if (MessageObject.shouldEncryptPhotoOrVideo(message)) {
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append(pathToAttach.getAbsolutePath());
                        stringBuilder.append(".enc");
                        file = new File(stringBuilder.toString());
                        obj = 1;
                    } else {
                        file = pathToAttach;
                        obj = null;
                    }
                    if (!file.exists()) {
                        String str = "rws";
                        if (obj != null) {
                            try {
                                File internalCacheDir = FileLoader.getInternalCacheDir();
                                StringBuilder stringBuilder2 = new StringBuilder();
                                stringBuilder2.append(file.getName());
                                stringBuilder2.append(".key");
                                RandomAccessFile randomAccessFile = new RandomAccessFile(new File(internalCacheDir, stringBuilder2.toString()), str);
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
                                Utilities.aesCtrDecryptionByteArray(photoSize.bytes, bArr2, bArr3, 0, photoSize.bytes.length, 0);
                            } catch (Exception e) {
                                FileLog.e(e);
                            }
                        }
                        RandomAccessFile randomAccessFile2 = new RandomAccessFile(file, str);
                        randomAccessFile2.write(photoSize.bytes);
                        randomAccessFile2.close();
                    }
                    TL_photoSize tL_photoSize = new TL_photoSize();
                    tL_photoSize.w = photoSize.w;
                    tL_photoSize.h = photoSize.h;
                    tL_photoSize.location = photoSize.location;
                    tL_photoSize.size = photoSize.size;
                    tL_photoSize.type = photoSize.type;
                    if (file.exists() && message2.grouped_id == 0) {
                        Point messageSize = ChatMessageCell.getMessageSize(photoSize.w, photoSize.h);
                        String format = String.format(Locale.US, "%d_%d@%d_%d_b", new Object[]{Long.valueOf(photoSize.location.volume_id), Integer.valueOf(photoSize.location.local_id), Integer.valueOf((int) (messageSize.x / AndroidUtilities.density)), Integer.valueOf((int) (messageSize.y / AndroidUtilities.density))});
                        if (!getInstance().isInMemCache(format, false)) {
                            String path = file.getPath();
                            float f = messageSize.x;
                            float f2 = AndroidUtilities.density;
                            loadBitmap = loadBitmap(path, null, (float) ((int) (f / f2)), (float) ((int) (messageSize.y / f2)), false);
                            if (loadBitmap != null) {
                                Utilities.blurBitmap(loadBitmap, 3, 1, loadBitmap.getWidth(), loadBitmap.getHeight(), loadBitmap.getRowBytes());
                                f = messageSize.x;
                                f2 = AndroidUtilities.density;
                                Bitmap createScaledBitmap = Bitmaps.createScaledBitmap(loadBitmap, (int) (f / f2), (int) (messageSize.y / f2), true);
                                if (createScaledBitmap != loadBitmap) {
                                    loadBitmap.recycle();
                                } else {
                                    createScaledBitmap = loadBitmap;
                                }
                                getInstance().memCache.put(format, new BitmapDrawable(createScaledBitmap));
                            }
                        }
                    }
                    MessageMedia messageMedia2 = message2.media;
                    int i2;
                    if (messageMedia2 instanceof TL_messageMediaPhoto) {
                        size2 = messageMedia2.photo.sizes.size();
                        for (i2 = 0; i2 < size2; i2++) {
                            if (((PhotoSize) message2.media.photo.sizes.get(i2)) instanceof TL_photoCachedSize) {
                                message2.media.photo.sizes.set(i2, tL_photoSize);
                                break;
                            }
                        }
                    } else if (messageMedia2 instanceof TL_messageMediaDocument) {
                        size2 = messageMedia2.document.thumbs.size();
                        for (i2 = 0; i2 < size2; i2++) {
                            if (((PhotoSize) message2.media.document.thumbs.get(i2)) instanceof TL_photoCachedSize) {
                                message2.media.document.thumbs.set(i2, tL_photoSize);
                                break;
                            }
                        }
                    } else if (messageMedia2 instanceof TL_messageMediaWebPage) {
                        size2 = messageMedia2.webpage.photo.sizes.size();
                        for (i2 = 0; i2 < size2; i2++) {
                            if (((PhotoSize) message2.media.webpage.photo.sizes.get(i2)) instanceof TL_photoCachedSize) {
                                message2.media.webpage.photo.sizes.set(i2, tL_photoSize);
                                break;
                            }
                        }
                    }
                }
            }
            messageMedia = message2.media;
            if (messageMedia instanceof TL_messageMediaDocument) {
                size = messageMedia.document.thumbs.size();
                for (size2 = 0; size2 < size; size2++) {
                    photoSize = (PhotoSize) message2.media.document.thumbs.get(size2);
                    if (photoSize instanceof TL_photoStrippedSize) {
                        int i3;
                        int i4;
                        PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(message2.media.document.thumbs, 320);
                        if (closestPhotoSizeWithSize != null) {
                            i3 = closestPhotoSizeWithSize.h;
                            i4 = closestPhotoSizeWithSize.w;
                        } else {
                            for (i4 = 0; i4 < message2.media.document.attributes.size(); i4++) {
                                if (message2.media.document.attributes.get(i4) instanceof TL_documentAttributeVideo) {
                                    TL_documentAttributeVideo tL_documentAttributeVideo = (TL_documentAttributeVideo) message2.media.document.attributes.get(i4);
                                    i3 = tL_documentAttributeVideo.h;
                                    i4 = tL_documentAttributeVideo.w;
                                    break;
                                }
                            }
                            i4 = 0;
                            i3 = 0;
                        }
                        Point messageSize2 = ChatMessageCell.getMessageSize(i4, i3);
                        String format2 = String.format(Locale.US, "%s_false@%d_%d_b", new Object[]{ImageLocation.getStippedKey(message2, message2, photoSize), Integer.valueOf((int) (messageSize2.x / AndroidUtilities.density)), Integer.valueOf((int) (messageSize2.y / AndroidUtilities.density))});
                        if (!getInstance().memCache.contains(format2)) {
                            Bitmap strippedPhotoBitmap = getStrippedPhotoBitmap(photoSize.bytes, null);
                            if (strippedPhotoBitmap != null) {
                                Utilities.blurBitmap(strippedPhotoBitmap, 3, 1, strippedPhotoBitmap.getWidth(), strippedPhotoBitmap.getHeight(), strippedPhotoBitmap.getRowBytes());
                                float f3 = messageSize2.x;
                                float f4 = AndroidUtilities.density;
                                loadBitmap = Bitmaps.createScaledBitmap(strippedPhotoBitmap, (int) (f3 / f4), (int) (messageSize2.y / f4), true);
                                if (loadBitmap != strippedPhotoBitmap) {
                                    strippedPhotoBitmap.recycle();
                                    strippedPhotoBitmap = loadBitmap;
                                }
                                getInstance().putImageToCache(new BitmapDrawable(strippedPhotoBitmap), format2);
                            }
                        }
                    }
                }
            }
        }
    }

    public static void saveMessagesThumbs(ArrayList<Message> arrayList) {
        if (arrayList != null && !arrayList.isEmpty()) {
            for (int i = 0; i < arrayList.size(); i++) {
                saveMessageThumbs((Message) arrayList.get(i));
            }
        }
    }
}
