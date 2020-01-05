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
            this.imageReceiverArray = new ArrayList();
            this.imageReceiverGuidsArray = new ArrayList();
            this.keys = new ArrayList();
            this.filters = new ArrayList();
            this.types = new ArrayList();
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
            this.types.add(Integer.valueOf(i));
            ImageLoader.this.imageLoadingByTag.put(imageReceiver.getTag(i), this);
        }

        public void replaceImageReceiver(ImageReceiver imageReceiver, String str, String str2, int i, int i2) {
            int indexOf = this.imageReceiverArray.indexOf(imageReceiver);
            if (indexOf != -1) {
                if (((Integer) this.types.get(indexOf)).intValue() != i) {
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
                ImageReceiver imageReceiver2 = (ImageReceiver) this.imageReceiverArray.get(i2);
                if (imageReceiver2 == null || imageReceiver2 == imageReceiver) {
                    this.imageReceiverArray.remove(i2);
                    this.imageReceiverGuidsArray.remove(i2);
                    this.keys.remove(i2);
                    this.filters.remove(i2);
                    i = ((Integer) this.types.remove(i2)).intValue();
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
                ImageLoader.this.imageLoadingByTag.remove(((ImageReceiver) this.imageReceiverArray.get(i)).getTag(this.type));
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
                    if (imageReceiver.setImageBitmapByKey(drawable2, this.key, this.type, false, ((Integer) arrayList2.get(i)).intValue())) {
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
                    ((ImageReceiver) arrayList.get(i)).setImageBitmapByKey(drawable, this.key, ((Integer) this.types.get(i)).intValue(), false, ((Integer) arrayList2.get(i)).intValue());
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

        /* JADX WARNING: Removed duplicated region for block: B:470:0x074b A:{SYNTHETIC, Splitter:B:470:0x074b} */
        /* JADX WARNING: Removed duplicated region for block: B:675:0x09d1  */
        /* JADX WARNING: Removed duplicated region for block: B:674:0x09cb  */
        /* JADX WARNING: Removed duplicated region for block: B:668:0x09b9 A:{SKIP} */
        /* JADX WARNING: Removed duplicated region for block: B:674:0x09cb  */
        /* JADX WARNING: Removed duplicated region for block: B:675:0x09d1  */
        /* JADX WARNING: Removed duplicated region for block: B:452:0x0705  */
        /* JADX WARNING: Removed duplicated region for block: B:324:0x050b A:{SYNTHETIC, Splitter:B:324:0x050b} */
        /* JADX WARNING: Removed duplicated region for block: B:668:0x09b9 A:{SKIP} */
        /* JADX WARNING: Removed duplicated region for block: B:675:0x09d1  */
        /* JADX WARNING: Removed duplicated region for block: B:674:0x09cb  */
        /* JADX WARNING: Removed duplicated region for block: B:324:0x050b A:{SYNTHETIC, Splitter:B:324:0x050b} */
        /* JADX WARNING: Removed duplicated region for block: B:452:0x0705  */
        /* JADX WARNING: Removed duplicated region for block: B:668:0x09b9 A:{SKIP} */
        /* JADX WARNING: Removed duplicated region for block: B:674:0x09cb  */
        /* JADX WARNING: Removed duplicated region for block: B:675:0x09d1  */
        /* JADX WARNING: Removed duplicated region for block: B:452:0x0705  */
        /* JADX WARNING: Removed duplicated region for block: B:324:0x050b A:{SYNTHETIC, Splitter:B:324:0x050b} */
        /* JADX WARNING: Removed duplicated region for block: B:668:0x09b9 A:{SKIP} */
        /* JADX WARNING: Removed duplicated region for block: B:675:0x09d1  */
        /* JADX WARNING: Removed duplicated region for block: B:674:0x09cb  */
        /* JADX WARNING: Removed duplicated region for block: B:324:0x050b A:{SYNTHETIC, Splitter:B:324:0x050b} */
        /* JADX WARNING: Removed duplicated region for block: B:452:0x0705  */
        /* JADX WARNING: Removed duplicated region for block: B:668:0x09b9 A:{SKIP} */
        /* JADX WARNING: Removed duplicated region for block: B:674:0x09cb  */
        /* JADX WARNING: Removed duplicated region for block: B:675:0x09d1  */
        /* JADX WARNING: Removed duplicated region for block: B:452:0x0705  */
        /* JADX WARNING: Removed duplicated region for block: B:324:0x050b A:{SYNTHETIC, Splitter:B:324:0x050b} */
        /* JADX WARNING: Removed duplicated region for block: B:668:0x09b9 A:{SKIP} */
        /* JADX WARNING: Removed duplicated region for block: B:675:0x09d1  */
        /* JADX WARNING: Removed duplicated region for block: B:674:0x09cb  */
        /* JADX WARNING: Removed duplicated region for block: B:668:0x09b9 A:{SKIP} */
        /* JADX WARNING: Removed duplicated region for block: B:674:0x09cb  */
        /* JADX WARNING: Removed duplicated region for block: B:675:0x09d1  */
        /* JADX WARNING: Removed duplicated region for block: B:246:0x03fc A:{Catch:{ all -> 0x0400, all -> 0x0468 }} */
        /* JADX WARNING: Removed duplicated region for block: B:232:0x03cd A:{Catch:{ all -> 0x03a5 }} */
        /* JADX WARNING: Removed duplicated region for block: B:237:0x03e6 A:{SYNTHETIC, Splitter:B:237:0x03e6} */
        /* JADX WARNING: Removed duplicated region for block: B:246:0x03fc A:{Catch:{ all -> 0x0400, all -> 0x0468 }} */
        /* JADX WARNING: Removed duplicated region for block: B:324:0x050b A:{SYNTHETIC, Splitter:B:324:0x050b} */
        /* JADX WARNING: Removed duplicated region for block: B:452:0x0705  */
        /* JADX WARNING: Removed duplicated region for block: B:668:0x09b9 A:{SKIP} */
        /* JADX WARNING: Removed duplicated region for block: B:675:0x09d1  */
        /* JADX WARNING: Removed duplicated region for block: B:674:0x09cb  */
        /* JADX WARNING: Removed duplicated region for block: B:167:0x02db  */
        /* JADX WARNING: Removed duplicated region for block: B:289:0x0498  */
        /* JADX WARNING: Removed duplicated region for block: B:172:0x02ed A:{Catch:{ all -> 0x04f2 }} */
        /* JADX WARNING: Removed duplicated region for block: B:452:0x0705  */
        /* JADX WARNING: Removed duplicated region for block: B:324:0x050b A:{SYNTHETIC, Splitter:B:324:0x050b} */
        /* JADX WARNING: Removed duplicated region for block: B:668:0x09b9 A:{SKIP} */
        /* JADX WARNING: Removed duplicated region for block: B:674:0x09cb  */
        /* JADX WARNING: Removed duplicated region for block: B:675:0x09d1  */
        /* JADX WARNING: Removed duplicated region for block: B:167:0x02db  */
        /* JADX WARNING: Removed duplicated region for block: B:172:0x02ed A:{Catch:{ all -> 0x04f2 }} */
        /* JADX WARNING: Removed duplicated region for block: B:289:0x0498  */
        /* JADX WARNING: Removed duplicated region for block: B:324:0x050b A:{SYNTHETIC, Splitter:B:324:0x050b} */
        /* JADX WARNING: Removed duplicated region for block: B:452:0x0705  */
        /* JADX WARNING: Removed duplicated region for block: B:668:0x09b9 A:{SKIP} */
        /* JADX WARNING: Removed duplicated region for block: B:675:0x09d1  */
        /* JADX WARNING: Removed duplicated region for block: B:674:0x09cb  */
        /* JADX WARNING: Removed duplicated region for block: B:132:0x0250 A:{SYNTHETIC, Splitter:B:132:0x0250} */
        /* JADX WARNING: Removed duplicated region for block: B:145:0x026b  */
        /* JADX WARNING: Removed duplicated region for block: B:167:0x02db  */
        /* JADX WARNING: Removed duplicated region for block: B:289:0x0498  */
        /* JADX WARNING: Removed duplicated region for block: B:172:0x02ed A:{Catch:{ all -> 0x04f2 }} */
        /* JADX WARNING: Removed duplicated region for block: B:452:0x0705  */
        /* JADX WARNING: Removed duplicated region for block: B:324:0x050b A:{SYNTHETIC, Splitter:B:324:0x050b} */
        /* JADX WARNING: Removed duplicated region for block: B:668:0x09b9 A:{SKIP} */
        /* JADX WARNING: Removed duplicated region for block: B:674:0x09cb  */
        /* JADX WARNING: Removed duplicated region for block: B:675:0x09d1  */
        /* JADX WARNING: Removed duplicated region for block: B:324:0x050b A:{SYNTHETIC, Splitter:B:324:0x050b} */
        /* JADX WARNING: Removed duplicated region for block: B:452:0x0705  */
        /* JADX WARNING: Removed duplicated region for block: B:668:0x09b9 A:{SKIP} */
        /* JADX WARNING: Removed duplicated region for block: B:675:0x09d1  */
        /* JADX WARNING: Removed duplicated region for block: B:674:0x09cb  */
        /* JADX WARNING: Removed duplicated region for block: B:668:0x09b9 A:{SKIP} */
        /* JADX WARNING: Removed duplicated region for block: B:674:0x09cb  */
        /* JADX WARNING: Removed duplicated region for block: B:675:0x09d1  */
        /* JADX WARNING: Removed duplicated region for block: B:668:0x09b9 A:{SKIP} */
        /* JADX WARNING: Removed duplicated region for block: B:675:0x09d1  */
        /* JADX WARNING: Removed duplicated region for block: B:674:0x09cb  */
        /* JADX WARNING: Removed duplicated region for block: B:668:0x09b9 A:{SKIP} */
        /* JADX WARNING: Removed duplicated region for block: B:674:0x09cb  */
        /* JADX WARNING: Removed duplicated region for block: B:675:0x09d1  */
        /* JADX WARNING: Removed duplicated region for block: B:452:0x0705  */
        /* JADX WARNING: Removed duplicated region for block: B:324:0x050b A:{SYNTHETIC, Splitter:B:324:0x050b} */
        /* JADX WARNING: Removed duplicated region for block: B:668:0x09b9 A:{SKIP} */
        /* JADX WARNING: Removed duplicated region for block: B:675:0x09d1  */
        /* JADX WARNING: Removed duplicated region for block: B:674:0x09cb  */
        /* JADX WARNING: Removed duplicated region for block: B:324:0x050b A:{SYNTHETIC, Splitter:B:324:0x050b} */
        /* JADX WARNING: Removed duplicated region for block: B:452:0x0705  */
        /* JADX WARNING: Removed duplicated region for block: B:668:0x09b9 A:{SKIP} */
        /* JADX WARNING: Removed duplicated region for block: B:674:0x09cb  */
        /* JADX WARNING: Removed duplicated region for block: B:675:0x09d1  */
        /* JADX WARNING: Removed duplicated region for block: B:452:0x0705  */
        /* JADX WARNING: Removed duplicated region for block: B:324:0x050b A:{SYNTHETIC, Splitter:B:324:0x050b} */
        /* JADX WARNING: Removed duplicated region for block: B:668:0x09b9 A:{SKIP} */
        /* JADX WARNING: Removed duplicated region for block: B:675:0x09d1  */
        /* JADX WARNING: Removed duplicated region for block: B:674:0x09cb  */
        /* JADX WARNING: Removed duplicated region for block: B:324:0x050b A:{SYNTHETIC, Splitter:B:324:0x050b} */
        /* JADX WARNING: Removed duplicated region for block: B:452:0x0705  */
        /* JADX WARNING: Removed duplicated region for block: B:668:0x09b9 A:{SKIP} */
        /* JADX WARNING: Removed duplicated region for block: B:674:0x09cb  */
        /* JADX WARNING: Removed duplicated region for block: B:675:0x09d1  */
        /* JADX WARNING: Removed duplicated region for block: B:132:0x0250 A:{SYNTHETIC, Splitter:B:132:0x0250} */
        /* JADX WARNING: Removed duplicated region for block: B:145:0x026b  */
        /* JADX WARNING: Removed duplicated region for block: B:167:0x02db  */
        /* JADX WARNING: Removed duplicated region for block: B:172:0x02ed A:{Catch:{ all -> 0x04f2 }} */
        /* JADX WARNING: Removed duplicated region for block: B:289:0x0498  */
        /* JADX WARNING: Removed duplicated region for block: B:452:0x0705  */
        /* JADX WARNING: Removed duplicated region for block: B:324:0x050b A:{SYNTHETIC, Splitter:B:324:0x050b} */
        /* JADX WARNING: Removed duplicated region for block: B:668:0x09b9 A:{SKIP} */
        /* JADX WARNING: Removed duplicated region for block: B:675:0x09d1  */
        /* JADX WARNING: Removed duplicated region for block: B:674:0x09cb  */
        /* JADX WARNING: Removed duplicated region for block: B:137:0x0258 A:{SYNTHETIC, Splitter:B:137:0x0258} */
        /* JADX WARNING: Removed duplicated region for block: B:668:0x09b9 A:{SKIP} */
        /* JADX WARNING: Removed duplicated region for block: B:674:0x09cb  */
        /* JADX WARNING: Removed duplicated region for block: B:675:0x09d1  */
        /* JADX WARNING: Removed duplicated region for block: B:324:0x050b A:{SYNTHETIC, Splitter:B:324:0x050b} */
        /* JADX WARNING: Removed duplicated region for block: B:452:0x0705  */
        /* JADX WARNING: Removed duplicated region for block: B:668:0x09b9 A:{SKIP} */
        /* JADX WARNING: Removed duplicated region for block: B:675:0x09d1  */
        /* JADX WARNING: Removed duplicated region for block: B:674:0x09cb  */
        /* JADX WARNING: Removed duplicated region for block: B:640:0x098c  */
        /* JADX WARNING: Removed duplicated region for block: B:603:0x090d  */
        /* JADX WARNING: Removed duplicated region for block: B:643:0x0992 A:{SYNTHETIC, Splitter:B:643:0x0992} */
        /* JADX WARNING: Removed duplicated region for block: B:668:0x09b9 A:{SKIP} */
        /* JADX WARNING: Removed duplicated region for block: B:674:0x09cb  */
        /* JADX WARNING: Removed duplicated region for block: B:675:0x09d1  */
        /* JADX WARNING: Missing block: B:8:0x0015, code skipped:
            r0 = r1.cacheImage;
            r2 = r0.imageLocation;
            r3 = r2.photoSize;
     */
        /* JADX WARNING: Missing block: B:9:0x001d, code skipped:
            if ((r3 instanceof org.telegram.tgnet.TLRPC.TL_photoStrippedSize) == false) goto L_0x0037;
     */
        /* JADX WARNING: Missing block: B:10:0x001f, code skipped:
            r0 = org.telegram.messenger.ImageLoader.access$1700(((org.telegram.tgnet.TLRPC.TL_photoStrippedSize) r3).bytes, r0.filter);
     */
        /* JADX WARNING: Missing block: B:11:0x0029, code skipped:
            if (r0 == null) goto L_0x0031;
     */
        /* JADX WARNING: Missing block: B:12:0x002b, code skipped:
            r5 = new android.graphics.drawable.BitmapDrawable(r0);
     */
        /* JADX WARNING: Missing block: B:13:0x0031, code skipped:
            r5 = null;
     */
        /* JADX WARNING: Missing block: B:14:0x0032, code skipped:
            onPostExecute(r5);
     */
        /* JADX WARNING: Missing block: B:15:0x0037, code skipped:
            r3 = r0.imageType;
     */
        /* JADX WARNING: Missing block: B:16:0x003a, code skipped:
            if (r3 != 5) goto L_0x0052;
     */
        /* JADX WARNING: Missing block: B:18:?, code skipped:
            r3 = new org.telegram.ui.Components.ThemePreviewDrawable(r0.finalFilePath, (org.telegram.messenger.DocumentObject.ThemeDocument) r2.document);
     */
        /* JADX WARNING: Missing block: B:19:0x0048, code skipped:
            r0 = move-exception;
     */
        /* JADX WARNING: Missing block: B:20:0x0049, code skipped:
            org.telegram.messenger.FileLog.e(r0);
            r3 = null;
     */
        /* JADX WARNING: Missing block: B:22:0x0052, code skipped:
            r8 = 1;
            r9 = false;
     */
        /* JADX WARNING: Missing block: B:23:0x0057, code skipped:
            if (r3 == 3) goto L_0x09d6;
     */
        /* JADX WARNING: Missing block: B:24:0x0059, code skipped:
            if (r3 != 4) goto L_0x005d;
     */
        /* JADX WARNING: Missing block: B:26:0x005f, code skipped:
            if (r3 != 1) goto L_0x015f;
     */
        /* JADX WARNING: Missing block: B:27:0x0061, code skipped:
            r3 = java.lang.Math.min(512, org.telegram.messenger.AndroidUtilities.dp(170.6f));
            r0 = java.lang.Math.min(512, org.telegram.messenger.AndroidUtilities.dp(170.6f));
            r12 = r1.cacheImage.filter;
     */
        /* JADX WARNING: Missing block: B:28:0x007a, code skipped:
            if (r12 == null) goto L_0x0145;
     */
        /* JADX WARNING: Missing block: B:29:0x007c, code skipped:
            r12 = r12.split("_");
     */
        /* JADX WARNING: Missing block: B:30:0x0083, code skipped:
            if (r12.length < 2) goto L_0x00c7;
     */
        /* JADX WARNING: Missing block: B:31:0x0085, code skipped:
            r0 = java.lang.Float.parseFloat(r12[0]);
            r3 = java.lang.Float.parseFloat(r12[1]);
            r13 = java.lang.Math.min(512, (int) (org.telegram.messenger.AndroidUtilities.density * r0));
            r11 = java.lang.Math.min(512, (int) (org.telegram.messenger.AndroidUtilities.density * r3));
     */
        /* JADX WARNING: Missing block: B:32:0x00a7, code skipped:
            if (r0 > 90.0f) goto L_0x00bd;
     */
        /* JADX WARNING: Missing block: B:34:0x00ab, code skipped:
            if (r3 > 90.0f) goto L_0x00bd;
     */
        /* JADX WARNING: Missing block: B:35:0x00ad, code skipped:
            r0 = java.lang.Math.min(r13, 160);
            r11 = java.lang.Math.min(r11, 160);
            r3 = r0;
            r0 = true;
     */
        /* JADX WARNING: Missing block: B:36:0x00bd, code skipped:
            r3 = r13;
            r0 = false;
     */
        /* JADX WARNING: Missing block: B:38:0x00c3, code skipped:
            if (org.telegram.messenger.SharedConfig.getDevicePerfomanceClass() == 2) goto L_0x00c9;
     */
        /* JADX WARNING: Missing block: B:39:0x00c5, code skipped:
            r9 = true;
     */
        /* JADX WARNING: Missing block: B:40:0x00c7, code skipped:
            r11 = r0;
            r0 = false;
     */
        /* JADX WARNING: Missing block: B:42:0x00ca, code skipped:
            if (r12.length < 3) goto L_0x00e3;
     */
        /* JADX WARNING: Missing block: B:44:0x00d4, code skipped:
            if ("nr".equals(r12[2]) == false) goto L_0x00d8;
     */
        /* JADX WARNING: Missing block: B:45:0x00d6, code skipped:
            r8 = 2;
     */
        /* JADX WARNING: Missing block: B:47:0x00e0, code skipped:
            if ("nrs".equals(r12[2]) == false) goto L_0x00e3;
     */
        /* JADX WARNING: Missing block: B:48:0x00e2, code skipped:
            r8 = 3;
     */
        /* JADX WARNING: Missing block: B:50:0x00e4, code skipped:
            if (r12.length < 5) goto L_0x013e;
     */
        /* JADX WARNING: Missing block: B:52:0x00ee, code skipped:
            if ("c1".equals(r12[4]) == false) goto L_0x00fe;
     */
        /* JADX WARNING: Missing block: B:53:0x00f0, code skipped:
            r5 = new int[]{16219713, 13275258, 16757049, 15582629, 16765248, 16245699, 16768889, 16510934};
     */
        /* JADX WARNING: Missing block: B:54:0x00f5, code skipped:
            r17 = r0;
            r14 = r3;
            r18 = r5;
            r16 = r9;
            r15 = r11;
     */
        /* JADX WARNING: Missing block: B:56:0x0106, code skipped:
            if ("c2".equals(r12[4]) == false) goto L_0x010e;
     */
        /* JADX WARNING: Missing block: B:57:0x0108, code skipped:
            r5 = new int[]{16219713, 11172960, 16757049, 13150599, 16765248, 14534815, 16768889, 15128242};
     */
        /* JADX WARNING: Missing block: B:59:0x0116, code skipped:
            if ("c3".equals(r12[4]) == false) goto L_0x011e;
     */
        /* JADX WARNING: Missing block: B:60:0x0118, code skipped:
            r5 = new int[]{16219713, 9199944, 16757049, 11371874, 16765248, 12885622, 16768889, 13939080};
     */
        /* JADX WARNING: Missing block: B:62:0x0126, code skipped:
            if ("c4".equals(r12[4]) == false) goto L_0x012e;
     */
        /* JADX WARNING: Missing block: B:63:0x0128, code skipped:
            r5 = new int[]{16219713, 7224364, 16757049, 9591348, 16765248, 10579526, 16768889, 11303506};
     */
        /* JADX WARNING: Missing block: B:65:0x0136, code skipped:
            if ("c5".equals(r12[4]) == false) goto L_0x013e;
     */
        /* JADX WARNING: Missing block: B:66:0x0138, code skipped:
            r5 = new int[]{16219713, 2694162, 16757049, 4663842, 16765248, 5716784, 16768889, 6834492};
     */
        /* JADX WARNING: Missing block: B:67:0x013e, code skipped:
            r17 = r0;
            r14 = r3;
            r16 = r9;
            r15 = r11;
     */
        /* JADX WARNING: Missing block: B:68:0x0145, code skipped:
            r15 = r0;
            r14 = r3;
            r16 = false;
            r17 = false;
     */
        /* JADX WARNING: Missing block: B:69:0x014b, code skipped:
            r18 = null;
     */
        /* JADX WARNING: Missing block: B:70:0x014d, code skipped:
            r12 = new org.telegram.ui.Components.RLottieDrawable(r1.cacheImage.finalFilePath, r14, r15, r16, r17, r18);
            r12.setAutoRepeat(r8);
            onPostExecute(r12);
     */
        /* JADX WARNING: Missing block: B:71:0x015f, code skipped:
            if (r3 != 2) goto L_0x01bb;
     */
        /* JADX WARNING: Missing block: B:73:0x0169, code skipped:
            if ("g".equals(r0.filter) == false) goto L_0x0191;
     */
        /* JADX WARNING: Missing block: B:74:0x016b, code skipped:
            r0 = r1.cacheImage;
            r2 = r0.imageLocation.document;
     */
        /* JADX WARNING: Missing block: B:75:0x0173, code skipped:
            if ((r2 instanceof org.telegram.tgnet.TLRPC.TL_documentEncrypted) != false) goto L_0x0191;
     */
        /* JADX WARNING: Missing block: B:76:0x0175, code skipped:
            r7 = r0.finalFilePath;
            r9 = (long) r0.size;
     */
        /* JADX WARNING: Missing block: B:77:0x017f, code skipped:
            if ((r2 instanceof org.telegram.tgnet.TLRPC.Document) == false) goto L_0x0183;
     */
        /* JADX WARNING: Missing block: B:78:0x0181, code skipped:
            r11 = r2;
     */
        /* JADX WARNING: Missing block: B:79:0x0183, code skipped:
            r11 = null;
     */
        /* JADX WARNING: Missing block: B:80:0x0184, code skipped:
            r0 = r1.cacheImage;
            r0 = new org.telegram.ui.Components.AnimatedFileDrawable(r7, false, r9, r11, r0.parentObject, r0.currentAccount, false);
     */
        /* JADX WARNING: Missing block: B:81:0x0191, code skipped:
            r2 = r1.cacheImage;
            r15 = new org.telegram.ui.Components.AnimatedFileDrawable(r2.finalFilePath, "d".equals(r2.filter), 0, null, null, r1.cacheImage.currentAccount, false);
     */
        /* JADX WARNING: Missing block: B:82:0x01b3, code skipped:
            java.lang.Thread.interrupted();
            onPostExecute(r0);
     */
        /* JADX WARNING: Missing block: B:83:0x01bb, code skipped:
            r3 = r0.finalFilePath;
     */
        /* JADX WARNING: Missing block: B:84:0x01bf, code skipped:
            if (r0.secureDocument != null) goto L_0x01d6;
     */
        /* JADX WARNING: Missing block: B:86:0x01c3, code skipped:
            if (r0.encryptionKeyPath == null) goto L_0x01d4;
     */
        /* JADX WARNING: Missing block: B:87:0x01c5, code skipped:
            if (r3 == null) goto L_0x01d4;
     */
        /* JADX WARNING: Missing block: B:89:0x01d1, code skipped:
            if (r3.getAbsolutePath().endsWith(".enc") == false) goto L_0x01d4;
     */
        /* JADX WARNING: Missing block: B:90:0x01d4, code skipped:
            r4 = null;
     */
        /* JADX WARNING: Missing block: B:91:0x01d6, code skipped:
            r4 = 1;
     */
        /* JADX WARNING: Missing block: B:92:0x01d7, code skipped:
            r0 = r1.cacheImage.secureDocument;
     */
        /* JADX WARNING: Missing block: B:93:0x01db, code skipped:
            if (r0 == null) goto L_0x01f0;
     */
        /* JADX WARNING: Missing block: B:94:0x01dd, code skipped:
            r6 = r0.secureDocumentKey;
            r0 = r0.secureFile;
     */
        /* JADX WARNING: Missing block: B:95:0x01e1, code skipped:
            if (r0 == null) goto L_0x01e8;
     */
        /* JADX WARNING: Missing block: B:96:0x01e3, code skipped:
            r0 = r0.file_hash;
     */
        /* JADX WARNING: Missing block: B:97:0x01e5, code skipped:
            if (r0 == null) goto L_0x01e8;
     */
        /* JADX WARNING: Missing block: B:98:0x01e8, code skipped:
            r0 = r1.cacheImage.secureDocument.fileHash;
     */
        /* JADX WARNING: Missing block: B:99:0x01ee, code skipped:
            r11 = r0;
     */
        /* JADX WARNING: Missing block: B:100:0x01f0, code skipped:
            r6 = null;
            r11 = null;
     */
        /* JADX WARNING: Missing block: B:102:0x01f6, code skipped:
            if (android.os.Build.VERSION.SDK_INT >= 19) goto L_0x0262;
     */
        /* JADX WARNING: Missing block: B:104:?, code skipped:
            r12 = new java.io.RandomAccessFile(r3, "r");
     */
        /* JADX WARNING: Missing block: B:107:0x0203, code skipped:
            if (r1.cacheImage.type != 1) goto L_0x020a;
     */
        /* JADX WARNING: Missing block: B:108:0x0205, code skipped:
            r0 = org.telegram.messenger.ImageLoader.access$1800();
     */
        /* JADX WARNING: Missing block: B:109:0x020a, code skipped:
            r0 = org.telegram.messenger.ImageLoader.access$1900();
     */
        /* JADX WARNING: Missing block: B:110:0x020e, code skipped:
            r12.readFully(r0, 0, r0.length);
            r0 = new java.lang.String(r0).toLowerCase().toLowerCase();
     */
        /* JADX WARNING: Missing block: B:111:0x0225, code skipped:
            if (r0.startsWith("riff") == false) goto L_0x0232;
     */
        /* JADX WARNING: Missing block: B:113:0x022e, code skipped:
            if (r0.endsWith("webp") == false) goto L_0x0232;
     */
        /* JADX WARNING: Missing block: B:114:0x0230, code skipped:
            r13 = 1;
     */
        /* JADX WARNING: Missing block: B:115:0x0232, code skipped:
            r13 = null;
     */
        /* JADX WARNING: Missing block: B:117:?, code skipped:
            r12.close();
     */
        /* JADX WARNING: Missing block: B:119:?, code skipped:
            r12.close();
     */
        /* JADX WARNING: Missing block: B:120:0x023a, code skipped:
            r0 = move-exception;
     */
        /* JADX WARNING: Missing block: B:121:0x023b, code skipped:
            org.telegram.messenger.FileLog.e(r0);
     */
        /* JADX WARNING: Missing block: B:122:0x0240, code skipped:
            r0 = e;
     */
        /* JADX WARNING: Missing block: B:123:0x0242, code skipped:
            r0 = e;
     */
        /* JADX WARNING: Missing block: B:124:0x0244, code skipped:
            r0 = move-exception;
     */
        /* JADX WARNING: Missing block: B:125:0x0245, code skipped:
            r2 = r0;
            r12 = null;
     */
        /* JADX WARNING: Missing block: B:126:0x0248, code skipped:
            r0 = e;
     */
        /* JADX WARNING: Missing block: B:127:0x0249, code skipped:
            r12 = null;
     */
        /* JADX WARNING: Missing block: B:128:0x024a, code skipped:
            r13 = null;
     */
        /* JADX WARNING: Missing block: B:130:?, code skipped:
            org.telegram.messenger.FileLog.e(r0);
     */
        /* JADX WARNING: Missing block: B:131:0x024e, code skipped:
            if (r12 != null) goto L_0x0250;
     */
        /* JADX WARNING: Missing block: B:133:?, code skipped:
            r12.close();
     */
        /* JADX WARNING: Missing block: B:134:0x0254, code skipped:
            r0 = move-exception;
     */
        /* JADX WARNING: Missing block: B:135:0x0255, code skipped:
            r2 = r0;
     */
        /* JADX WARNING: Missing block: B:136:0x0256, code skipped:
            if (r12 != null) goto L_0x0258;
     */
        /* JADX WARNING: Missing block: B:138:?, code skipped:
            r12.close();
     */
        /* JADX WARNING: Missing block: B:139:0x025c, code skipped:
            r0 = move-exception;
     */
        /* JADX WARNING: Missing block: B:140:0x025d, code skipped:
            org.telegram.messenger.FileLog.e(r0);
     */
        /* JADX WARNING: Missing block: B:141:0x0261, code skipped:
            throw r2;
     */
        /* JADX WARNING: Missing block: B:142:0x0262, code skipped:
            r13 = null;
     */
        /* JADX WARNING: Missing block: B:143:0x0263, code skipped:
            r0 = r1.cacheImage.imageLocation.path;
     */
        /* JADX WARNING: Missing block: B:144:0x0269, code skipped:
            if (r0 != null) goto L_0x026b;
     */
        /* JADX WARNING: Missing block: B:146:0x0272, code skipped:
            if (r0.startsWith("thumb://") != false) goto L_0x0274;
     */
        /* JADX WARNING: Missing block: B:147:0x0274, code skipped:
            r12 = r0.indexOf(":", 8);
     */
        /* JADX WARNING: Missing block: B:148:0x027a, code skipped:
            if (r12 >= 0) goto L_0x027c;
     */
        /* JADX WARNING: Missing block: B:149:0x027c, code skipped:
            r14 = java.lang.Long.valueOf(java.lang.Long.parseLong(r0.substring(8, r12)));
            r0 = r0.substring(r12 + 1);
     */
        /* JADX WARNING: Missing block: B:150:0x028e, code skipped:
            r0 = null;
            r14 = null;
     */
        /* JADX WARNING: Missing block: B:151:0x0290, code skipped:
            r12 = r0;
     */
        /* JADX WARNING: Missing block: B:152:0x0291, code skipped:
            r15 = null;
     */
        /* JADX WARNING: Missing block: B:153:0x0292, code skipped:
            r16 = null;
     */
        /* JADX WARNING: Missing block: B:155:0x029c, code skipped:
            if (r0.startsWith("vthumb://") != false) goto L_0x029e;
     */
        /* JADX WARNING: Missing block: B:156:0x029e, code skipped:
            r12 = r0.indexOf(":", 9);
     */
        /* JADX WARNING: Missing block: B:157:0x02a6, code skipped:
            if (r12 >= 0) goto L_0x02a8;
     */
        /* JADX WARNING: Missing block: B:158:0x02a8, code skipped:
            r0 = java.lang.Long.valueOf(java.lang.Long.parseLong(r0.substring(9, r12)));
            r12 = 1;
     */
        /* JADX WARNING: Missing block: B:159:0x02b8, code skipped:
            r0 = null;
            r12 = null;
     */
        /* JADX WARNING: Missing block: B:160:0x02ba, code skipped:
            r14 = r0;
            r15 = r12;
            r12 = null;
     */
        /* JADX WARNING: Missing block: B:162:0x02c4, code skipped:
            if (r0.startsWith("http") == false) goto L_0x02c6;
     */
        /* JADX WARNING: Missing block: B:163:0x02c6, code skipped:
            r12 = null;
            r14 = null;
     */
        /* JADX WARNING: Missing block: B:164:0x02c9, code skipped:
            r12 = null;
            r14 = null;
            r15 = null;
            r16 = 1;
     */
        /* JADX WARNING: Missing block: B:165:0x02ce, code skipped:
            r10 = new android.graphics.BitmapFactory.Options();
            r10.inSampleSize = 1;
     */
        /* JADX WARNING: Missing block: B:166:0x02d9, code skipped:
            if (android.os.Build.VERSION.SDK_INT < 21) goto L_0x02db;
     */
        /* JADX WARNING: Missing block: B:167:0x02db, code skipped:
            r10.inPurgeable = true;
     */
        /* JADX WARNING: Missing block: B:168:0x02dd, code skipped:
            r2 = org.telegram.messenger.ImageLoader.access$2000(r1.this$0);
     */
        /* JADX WARNING: Missing block: B:171:0x02eb, code skipped:
            if (r1.cacheImage.filter != null) goto L_0x02ed;
     */
        /* JADX WARNING: Missing block: B:172:0x02ed, code skipped:
            r0 = r1.cacheImage.filter.split("_");
     */
        /* JADX WARNING: Missing block: B:173:0x02f8, code skipped:
            if (r0.length >= 2) goto L_0x02fa;
     */
        /* JADX WARNING: Missing block: B:176:0x0302, code skipped:
            r5 = java.lang.Float.parseFloat(r0[0]) * org.telegram.messenger.AndroidUtilities.density;
     */
        /* JADX WARNING: Missing block: B:179:0x030c, code skipped:
            r22 = java.lang.Float.parseFloat(r0[1]) * org.telegram.messenger.AndroidUtilities.density;
     */
        /* JADX WARNING: Missing block: B:180:0x0311, code skipped:
            r0 = th;
     */
        /* JADX WARNING: Missing block: B:181:0x0312, code skipped:
            r8 = r5;
            r26 = r12;
            r25 = r13;
            r5 = null;
            r7 = 0;
     */
        /* JADX WARNING: Missing block: B:182:0x031b, code skipped:
            r0 = th;
     */
        /* JADX WARNING: Missing block: B:183:0x031c, code skipped:
            r26 = r12;
            r25 = r13;
            r5 = null;
            r7 = 0;
            r8 = 0.0f;
     */
        /* JADX WARNING: Missing block: B:184:0x0325, code skipped:
            r5 = 0.0f;
            r22 = 0.0f;
     */
        /* JADX WARNING: Missing block: B:187:0x0332, code skipped:
            if (r1.cacheImage.filter.contains("b2") != false) goto L_0x0334;
     */
        /* JADX WARNING: Missing block: B:188:0x0334, code skipped:
            r7 = 3;
     */
        /* JADX WARNING: Missing block: B:190:0x0340, code skipped:
            if (r1.cacheImage.filter.contains("b1") != false) goto L_0x0342;
     */
        /* JADX WARNING: Missing block: B:191:0x0342, code skipped:
            r7 = 2;
     */
        /* JADX WARNING: Missing block: B:193:0x034e, code skipped:
            if (r1.cacheImage.filter.contains("b") != false) goto L_0x0350;
     */
        /* JADX WARNING: Missing block: B:194:0x0350, code skipped:
            r7 = 1;
     */
        /* JADX WARNING: Missing block: B:195:0x0352, code skipped:
            r7 = 0;
     */
        /* JADX WARNING: Missing block: B:197:?, code skipped:
            r9 = r1.cacheImage.filter.contains("i");
     */
        /* JADX WARNING: Missing block: B:200:0x0367, code skipped:
            if (r1.cacheImage.filter.contains("f") != false) goto L_0x0369;
     */
        /* JADX WARNING: Missing block: B:201:0x0369, code skipped:
            r2 = true;
     */
        /* JADX WARNING: Missing block: B:202:0x036a, code skipped:
            if (r13 != null) goto L_0x0471;
     */
        /* JADX WARNING: Missing block: B:209:?, code skipped:
            r10.inJustDecodeBounds = true;
     */
        /* JADX WARNING: Missing block: B:210:0x0377, code skipped:
            if (r14 == null) goto L_0x03a8;
     */
        /* JADX WARNING: Missing block: B:212:0x037b, code skipped:
            if (r15 != null) goto L_0x037d;
     */
        /* JADX WARNING: Missing block: B:215:0x0383, code skipped:
            r26 = r12;
            r25 = r13;
     */
        /* JADX WARNING: Missing block: B:217:?, code skipped:
            android.provider.MediaStore.Video.Thumbnails.getThumbnail(org.telegram.messenger.ApplicationLoader.applicationContext.getContentResolver(), r14.longValue(), 1, r10);
     */
        /* JADX WARNING: Missing block: B:218:0x038f, code skipped:
            r26 = r12;
            r25 = r13;
            android.provider.MediaStore.Images.Thumbnails.getThumbnail(org.telegram.messenger.ApplicationLoader.applicationContext.getContentResolver(), r14.longValue(), 1, r10);
     */
        /* JADX WARNING: Missing block: B:219:0x03a1, code skipped:
            r27 = r2;
     */
        /* JADX WARNING: Missing block: B:220:0x03a5, code skipped:
            r0 = th;
     */
        /* JADX WARNING: Missing block: B:221:0x03a8, code skipped:
            r26 = r12;
            r25 = r13;
     */
        /* JADX WARNING: Missing block: B:222:0x03ac, code skipped:
            if (r6 != null) goto L_0x03ae;
     */
        /* JADX WARNING: Missing block: B:224:?, code skipped:
            r0 = new java.io.RandomAccessFile(r3, "r");
            r8 = (int) r0.length();
            r12 = (byte[]) org.telegram.messenger.ImageLoader.access$2100().get();
     */
        /* JADX WARNING: Missing block: B:225:0x03c4, code skipped:
            if (r12 != null) goto L_0x03c6;
     */
        /* JADX WARNING: Missing block: B:228:0x03c7, code skipped:
            if (r12.length >= r8) goto L_0x03c9;
     */
        /* JADX WARNING: Missing block: B:230:0x03ca, code skipped:
            r12 = null;
     */
        /* JADX WARNING: Missing block: B:231:0x03cb, code skipped:
            if (r12 == null) goto L_0x03cd;
     */
        /* JADX WARNING: Missing block: B:232:0x03cd, code skipped:
            r12 = new byte[r8];
            org.telegram.messenger.ImageLoader.access$2100().set(r12);
     */
        /* JADX WARNING: Missing block: B:235:?, code skipped:
            r0.readFully(r12, 0, r8);
            r0.close();
            org.telegram.messenger.secretmedia.EncryptedFileInputStream.decryptBytesWithKeyFile(r12, 0, r8, r6);
            r0 = org.telegram.messenger.Utilities.computeSHA256(r12, 0, r8);
     */
        /* JADX WARNING: Missing block: B:236:0x03e4, code skipped:
            if (r11 != null) goto L_0x03e6;
     */
        /* JADX WARNING: Missing block: B:239:0x03ea, code skipped:
            if (java.util.Arrays.equals(r0, r11) != false) goto L_0x03ed;
     */
        /* JADX WARNING: Missing block: B:240:0x03ed, code skipped:
            r27 = r2;
            r0 = null;
     */
        /* JADX WARNING: Missing block: B:241:0x03f1, code skipped:
            r27 = r2;
            r0 = 1;
     */
        /* JADX WARNING: Missing block: B:244:?, code skipped:
            r2 = r12[0] & 255;
            r8 = r8 - r2;
     */
        /* JADX WARNING: Missing block: B:245:0x03fa, code skipped:
            if (r0 == null) goto L_0x03fc;
     */
        /* JADX WARNING: Missing block: B:246:0x03fc, code skipped:
            android.graphics.BitmapFactory.decodeByteArray(r12, r2, r8, r10);
     */
        /* JADX WARNING: Missing block: B:247:0x0400, code skipped:
            r0 = th;
     */
        /* JADX WARNING: Missing block: B:248:0x0401, code skipped:
            r27 = r2;
     */
        /* JADX WARNING: Missing block: B:249:0x0405, code skipped:
            r27 = r2;
     */
        /* JADX WARNING: Missing block: B:250:0x0407, code skipped:
            if (r4 != null) goto L_0x0409;
     */
        /* JADX WARNING: Missing block: B:251:0x0409, code skipped:
            r0 = new org.telegram.messenger.secretmedia.EncryptedFileInputStream(r3, r1.cacheImage.encryptionKeyPath);
     */
        /* JADX WARNING: Missing block: B:252:0x0413, code skipped:
            r0 = new java.io.FileInputStream(r3);
     */
        /* JADX WARNING: Missing block: B:253:0x0418, code skipped:
            android.graphics.BitmapFactory.decodeStream(r0, null, r10);
            r0.close();
     */
        /* JADX WARNING: Missing block: B:254:0x041f, code skipped:
            r0 = (float) r10.outWidth;
            r2 = (float) r10.outHeight;
     */
        /* JADX WARNING: Missing block: B:255:0x0427, code skipped:
            if (r5 < r22) goto L_0x0436;
     */
        /* JADX WARNING: Missing block: B:258:0x042d, code skipped:
            r8 = java.lang.Math.max(r0 / r5, r2 / r22);
     */
        /* JADX WARNING: Missing block: B:259:0x0436, code skipped:
            r8 = java.lang.Math.min(r0 / r5, r2 / r22);
     */
        /* JADX WARNING: Missing block: B:261:0x0443, code skipped:
            if (r8 < 1.2f) goto L_0x0445;
     */
        /* JADX WARNING: Missing block: B:262:0x0445, code skipped:
            r8 = 1.0f;
     */
        /* JADX WARNING: Missing block: B:263:0x0447, code skipped:
            r10.inJustDecodeBounds = false;
     */
        /* JADX WARNING: Missing block: B:264:0x044c, code skipped:
            if (r8 <= 1.0f) goto L_0x0464;
     */
        /* JADX WARNING: Missing block: B:269:0x0456, code skipped:
            r0 = 1;
     */
        /* JADX WARNING: Missing block: B:270:0x0457, code skipped:
            r0 = r0 * 2;
     */
        /* JADX WARNING: Missing block: B:271:0x045f, code skipped:
            if (((float) (r0 * 2)) < r8) goto L_0x0457;
     */
        /* JADX WARNING: Missing block: B:272:0x0461, code skipped:
            r10.inSampleSize = r0;
     */
        /* JADX WARNING: Missing block: B:273:0x0464, code skipped:
            r10.inSampleSize = (int) r8;
     */
        /* JADX WARNING: Missing block: B:274:0x0468, code skipped:
            r0 = th;
     */
        /* JADX WARNING: Missing block: B:275:0x0469, code skipped:
            r8 = r5;
            r2 = r27;
     */
        /* JADX WARNING: Missing block: B:276:0x046d, code skipped:
            r0 = th;
     */
        /* JADX WARNING: Missing block: B:277:0x046e, code skipped:
            r27 = r2;
     */
        /* JADX WARNING: Missing block: B:278:0x0471, code skipped:
            r27 = r2;
            r26 = r12;
            r25 = r13;
     */
        /* JADX WARNING: Missing block: B:279:0x0477, code skipped:
            r2 = r27;
            r0 = null;
     */
        /* JADX WARNING: Missing block: B:280:0x047c, code skipped:
            r0 = th;
     */
        /* JADX WARNING: Missing block: B:281:0x047d, code skipped:
            r26 = r12;
            r25 = r13;
     */
        /* JADX WARNING: Missing block: B:282:0x0481, code skipped:
            r8 = r5;
     */
        /* JADX WARNING: Missing block: B:283:0x0482, code skipped:
            r5 = null;
     */
        /* JADX WARNING: Missing block: B:284:0x0485, code skipped:
            r0 = th;
     */
        /* JADX WARNING: Missing block: B:285:0x0486, code skipped:
            r26 = r12;
            r25 = r13;
            r8 = r5;
            r5 = null;
     */
        /* JADX WARNING: Missing block: B:286:0x048d, code skipped:
            r0 = th;
     */
        /* JADX WARNING: Missing block: B:287:0x048e, code skipped:
            r26 = r12;
            r25 = r13;
            r8 = r5;
            r5 = null;
            r7 = 0;
     */
        /* JADX WARNING: Missing block: B:288:0x0495, code skipped:
            r9 = false;
     */
        /* JADX WARNING: Missing block: B:289:0x0498, code skipped:
            r26 = r12;
            r25 = r13;
     */
        /* JADX WARNING: Missing block: B:290:0x049c, code skipped:
            if (r26 != null) goto L_0x049e;
     */
        /* JADX WARNING: Missing block: B:293:?, code skipped:
            r10.inJustDecodeBounds = true;
     */
        /* JADX WARNING: Missing block: B:294:0x04a1, code skipped:
            if (r2 != false) goto L_0x04a3;
     */
        /* JADX WARNING: Missing block: B:295:0x04a3, code skipped:
            r0 = android.graphics.Bitmap.Config.ARGB_8888;
     */
        /* JADX WARNING: Missing block: B:296:0x04a6, code skipped:
            r0 = android.graphics.Bitmap.Config.RGB_565;
     */
        /* JADX WARNING: Missing block: B:297:0x04a8, code skipped:
            r10.inPreferredConfig = r0;
            r0 = new java.io.FileInputStream(r3);
            r7 = android.graphics.BitmapFactory.decodeStream(r0, null, r10);
     */
        /* JADX WARNING: Missing block: B:299:?, code skipped:
            r0.close();
            r0 = r10.outWidth;
            r5 = r10.outHeight;
            r10.inJustDecodeBounds = false;
            r0 = (float) java.lang.Math.max(r0 / 200, r5 / 200);
     */
        /* JADX WARNING: Missing block: B:300:0x04c9, code skipped:
            if (r0 < 1.0f) goto L_0x04cb;
     */
        /* JADX WARNING: Missing block: B:301:0x04cb, code skipped:
            r0 = 1.0f;
     */
        /* JADX WARNING: Missing block: B:303:0x04cf, code skipped:
            if (r0 > 1.0f) goto L_0x04d1;
     */
        /* JADX WARNING: Missing block: B:304:0x04d1, code skipped:
            r5 = 1;
     */
        /* JADX WARNING: Missing block: B:305:0x04d2, code skipped:
            r5 = r5 * 2;
     */
        /* JADX WARNING: Missing block: B:306:0x04da, code skipped:
            if (((float) (r5 * 2)) < r0) goto L_0x04d2;
     */
        /* JADX WARNING: Missing block: B:307:0x04dc, code skipped:
            r10.inSampleSize = r5;
     */
        /* JADX WARNING: Missing block: B:308:0x04df, code skipped:
            r10.inSampleSize = (int) r0;
     */
        /* JADX WARNING: Missing block: B:309:0x04e2, code skipped:
            r0 = r7;
     */
        /* JADX WARNING: Missing block: B:310:0x04e4, code skipped:
            r0 = th;
     */
        /* JADX WARNING: Missing block: B:311:0x04e5, code skipped:
            r5 = r7;
     */
        /* JADX WARNING: Missing block: B:312:0x04e7, code skipped:
            r0 = th;
     */
        /* JADX WARNING: Missing block: B:313:0x04e9, code skipped:
            r0 = null;
     */
        /* JADX WARNING: Missing block: B:314:0x04ea, code skipped:
            r5 = 0.0f;
            r7 = 0;
            r9 = false;
            r22 = 0.0f;
     */
        /* JADX WARNING: Missing block: B:315:0x04ef, code skipped:
            r8 = r5;
            r5 = r0;
     */
        /* JADX WARNING: Missing block: B:316:0x04f2, code skipped:
            r0 = th;
     */
        /* JADX WARNING: Missing block: B:317:0x04f3, code skipped:
            r26 = r12;
            r25 = r13;
     */
        /* JADX WARNING: Missing block: B:318:0x04f7, code skipped:
            r5 = null;
     */
        /* JADX WARNING: Missing block: B:319:0x04f8, code skipped:
            r7 = 0;
            r8 = 0.0f;
            r9 = false;
     */
        /* JADX WARNING: Missing block: B:320:0x04fb, code skipped:
            r22 = 0.0f;
     */
        /* JADX WARNING: Missing block: B:321:0x04fd, code skipped:
            org.telegram.messenger.FileLog.e(r0);
     */
        /* JADX WARNING: Missing block: B:322:0x0500, code skipped:
            r0 = r22;
            r20 = r14;
     */
        /* JADX WARNING: Missing block: B:323:0x0509, code skipped:
            if (r1.cacheImage.type == 1) goto L_0x050b;
     */
        /* JADX WARNING: Missing block: B:325:?, code skipped:
            org.telegram.messenger.ImageLoader.access$2202(r1.this$0, java.lang.System.currentTimeMillis());
     */
        /* JADX WARNING: Missing block: B:326:0x0516, code skipped:
            monitor-enter(r1.sync);
     */
        /* JADX WARNING: Missing block: B:329:0x0519, code skipped:
            if (r1.isCancelled != false) goto L_0x051b;
     */
        /* JADX WARNING: Missing block: B:331:0x051c, code skipped:
            return;
     */
        /* JADX WARNING: Missing block: B:333:0x051e, code skipped:
            if (r25 == null) goto L_0x0565;
     */
        /* JADX WARNING: Missing block: B:335:?, code skipped:
            r0 = new java.io.RandomAccessFile(r3, "r");
            r2 = r0.getChannel().map(java.nio.channels.FileChannel.MapMode.READ_ONLY, 0, r3.length());
            r4 = new android.graphics.BitmapFactory.Options();
            r4.inJustDecodeBounds = true;
            org.telegram.messenger.Utilities.loadWebpImage(null, r2, r2.limit(), r4, true);
            r5 = org.telegram.messenger.Bitmaps.createBitmap(r4.outWidth, r4.outHeight, android.graphics.Bitmap.Config.ARGB_8888);
            r4 = r2.limit();
     */
        /* JADX WARNING: Missing block: B:336:0x0557, code skipped:
            if (r10.inPurgeable != false) goto L_0x055b;
     */
        /* JADX WARNING: Missing block: B:337:0x0559, code skipped:
            r6 = true;
     */
        /* JADX WARNING: Missing block: B:338:0x055b, code skipped:
            r6 = false;
     */
        /* JADX WARNING: Missing block: B:339:0x055c, code skipped:
            org.telegram.messenger.Utilities.loadWebpImage(r5, r2, r4, null, r6);
            r0.close();
     */
        /* JADX WARNING: Missing block: B:341:0x0567, code skipped:
            if (r10.inPurgeable != false) goto L_0x0586;
     */
        /* JADX WARNING: Missing block: B:342:0x0569, code skipped:
            if (r6 == null) goto L_0x056c;
     */
        /* JADX WARNING: Missing block: B:344:0x056c, code skipped:
            if (r4 == null) goto L_0x0578;
     */
        /* JADX WARNING: Missing block: B:345:0x056e, code skipped:
            r0 = new org.telegram.messenger.secretmedia.EncryptedFileInputStream(r3, r1.cacheImage.encryptionKeyPath);
     */
        /* JADX WARNING: Missing block: B:346:0x0578, code skipped:
            r0 = new java.io.FileInputStream(r3);
     */
        /* JADX WARNING: Missing block: B:347:0x057d, code skipped:
            r5 = android.graphics.BitmapFactory.decodeStream(r0, null, r10);
            r0.close();
     */
        /* JADX WARNING: Missing block: B:348:0x0586, code skipped:
            r0 = new java.io.RandomAccessFile(r3, "r");
            r2 = (int) r0.length();
            r12 = (byte[]) org.telegram.messenger.ImageLoader.access$2300().get();
     */
        /* JADX WARNING: Missing block: B:349:0x059c, code skipped:
            if (r12 == null) goto L_0x05a2;
     */
        /* JADX WARNING: Missing block: B:351:0x059f, code skipped:
            if (r12.length < r2) goto L_0x05a2;
     */
        /* JADX WARNING: Missing block: B:353:0x05a2, code skipped:
            r12 = null;
     */
        /* JADX WARNING: Missing block: B:354:0x05a3, code skipped:
            if (r12 != null) goto L_0x05ae;
     */
        /* JADX WARNING: Missing block: B:355:0x05a5, code skipped:
            r12 = new byte[r2];
            org.telegram.messenger.ImageLoader.access$2300().set(r12);
     */
        /* JADX WARNING: Missing block: B:356:0x05ae, code skipped:
            r0.readFully(r12, 0, r2);
            r0.close();
     */
        /* JADX WARNING: Missing block: B:357:0x05b5, code skipped:
            if (r6 == null) goto L_0x05d1;
     */
        /* JADX WARNING: Missing block: B:358:0x05b7, code skipped:
            org.telegram.messenger.secretmedia.EncryptedFileInputStream.decryptBytesWithKeyFile(r12, 0, r2, r6);
            r0 = org.telegram.messenger.Utilities.computeSHA256(r12, 0, r2);
     */
        /* JADX WARNING: Missing block: B:359:0x05be, code skipped:
            if (r11 == null) goto L_0x05c9;
     */
        /* JADX WARNING: Missing block: B:361:0x05c4, code skipped:
            if (java.util.Arrays.equals(r0, r11) != false) goto L_0x05c7;
     */
        /* JADX WARNING: Missing block: B:363:0x05c7, code skipped:
            r0 = null;
     */
        /* JADX WARNING: Missing block: B:364:0x05c9, code skipped:
            r0 = 1;
     */
        /* JADX WARNING: Missing block: B:365:0x05ca, code skipped:
            r4 = r12[0] & 255;
            r2 = r2 - r4;
     */
        /* JADX WARNING: Missing block: B:366:0x05d1, code skipped:
            if (r4 == null) goto L_0x05db;
     */
        /* JADX WARNING: Missing block: B:367:0x05d3, code skipped:
            org.telegram.messenger.secretmedia.EncryptedFileInputStream.decryptBytesWithKeyFile(r12, 0, r2, r1.cacheImage.encryptionKeyPath);
     */
        /* JADX WARNING: Missing block: B:368:0x05db, code skipped:
            r0 = null;
            r4 = 0;
     */
        /* JADX WARNING: Missing block: B:369:0x05dd, code skipped:
            if (r0 != null) goto L_0x05e3;
     */
        /* JADX WARNING: Missing block: B:370:0x05df, code skipped:
            r5 = android.graphics.BitmapFactory.decodeByteArray(r12, r4, r2, r10);
     */
        /* JADX WARNING: Missing block: B:371:0x05e3, code skipped:
            if (r5 != null) goto L_0x05fb;
     */
        /* JADX WARNING: Missing block: B:373:0x05ed, code skipped:
            if (r3.length() == 0) goto L_0x05f5;
     */
        /* JADX WARNING: Missing block: B:375:0x05f3, code skipped:
            if (r1.cacheImage.filter != null) goto L_0x05f8;
     */
        /* JADX WARNING: Missing block: B:376:0x05f5, code skipped:
            r3.delete();
     */
        /* JADX WARNING: Missing block: B:377:0x05f8, code skipped:
            r9 = false;
     */
        /* JADX WARNING: Missing block: B:379:0x05ff, code skipped:
            if (r1.cacheImage.filter == null) goto L_0x062d;
     */
        /* JADX WARNING: Missing block: B:380:0x0601, code skipped:
            r0 = (float) r5.getWidth();
            r2 = (float) r5.getHeight();
     */
        /* JADX WARNING: Missing block: B:381:0x060d, code skipped:
            if (r10.inPurgeable != false) goto L_0x062d;
     */
        /* JADX WARNING: Missing block: B:383:0x0611, code skipped:
            if (r8 == 0.0f) goto L_0x062d;
     */
        /* JADX WARNING: Missing block: B:385:0x0615, code skipped:
            if (r0 == r8) goto L_0x062d;
     */
        /* JADX WARNING: Missing block: B:387:0x061c, code skipped:
            if (r0 <= (20.0f + r8)) goto L_0x062d;
     */
        /* JADX WARNING: Missing block: B:388:0x061e, code skipped:
            r0 = org.telegram.messenger.Bitmaps.createScaledBitmap(r5, (int) r8, (int) (r2 / (r0 / r8)), true);
     */
        /* JADX WARNING: Missing block: B:389:0x0627, code skipped:
            if (r5 == r0) goto L_0x062d;
     */
        /* JADX WARNING: Missing block: B:390:0x0629, code skipped:
            r5.recycle();
            r5 = r0;
     */
        /* JADX WARNING: Missing block: B:391:0x062d, code skipped:
            if (r9 == false) goto L_0x064d;
     */
        /* JADX WARNING: Missing block: B:393:0x0631, code skipped:
            if (r10.inPurgeable == false) goto L_0x0635;
     */
        /* JADX WARNING: Missing block: B:394:0x0633, code skipped:
            r0 = 0;
     */
        /* JADX WARNING: Missing block: B:395:0x0635, code skipped:
            r0 = 1;
     */
        /* JADX WARNING: Missing block: B:397:0x0646, code skipped:
            if (org.telegram.messenger.Utilities.needInvert(r5, r0, r5.getWidth(), r5.getHeight(), r5.getRowBytes()) == 0) goto L_0x064a;
     */
        /* JADX WARNING: Missing block: B:398:0x0648, code skipped:
            r9 = true;
     */
        /* JADX WARNING: Missing block: B:399:0x064a, code skipped:
            r9 = false;
     */
        /* JADX WARNING: Missing block: B:400:0x064b, code skipped:
            r2 = 1;
     */
        /* JADX WARNING: Missing block: B:401:0x064d, code skipped:
            r2 = 1;
            r9 = false;
     */
        /* JADX WARNING: Missing block: B:402:0x064f, code skipped:
            if (r7 != r2) goto L_0x0676;
     */
        /* JADX WARNING: Missing block: B:405:0x0657, code skipped:
            if (r5.getConfig() != android.graphics.Bitmap.Config.ARGB_8888) goto L_0x0701;
     */
        /* JADX WARNING: Missing block: B:407:0x065c, code skipped:
            if (r10.inPurgeable == false) goto L_0x0660;
     */
        /* JADX WARNING: Missing block: B:408:0x065e, code skipped:
            r13 = 0;
     */
        /* JADX WARNING: Missing block: B:409:0x0660, code skipped:
            r13 = 1;
     */
        /* JADX WARNING: Missing block: B:410:0x0661, code skipped:
            org.telegram.messenger.Utilities.blurBitmap(r5, 3, r13, r5.getWidth(), r5.getHeight(), r5.getRowBytes());
     */
        /* JADX WARNING: Missing block: B:411:0x0673, code skipped:
            r0 = th;
     */
        /* JADX WARNING: Missing block: B:414:0x0677, code skipped:
            if (r7 != 2) goto L_0x069b;
     */
        /* JADX WARNING: Missing block: B:416:0x067f, code skipped:
            if (r5.getConfig() != android.graphics.Bitmap.Config.ARGB_8888) goto L_0x0701;
     */
        /* JADX WARNING: Missing block: B:418:0x0684, code skipped:
            if (r10.inPurgeable == false) goto L_0x0688;
     */
        /* JADX WARNING: Missing block: B:419:0x0686, code skipped:
            r13 = 0;
     */
        /* JADX WARNING: Missing block: B:420:0x0688, code skipped:
            r13 = 1;
     */
        /* JADX WARNING: Missing block: B:421:0x0689, code skipped:
            org.telegram.messenger.Utilities.blurBitmap(r5, 1, r13, r5.getWidth(), r5.getHeight(), r5.getRowBytes());
     */
        /* JADX WARNING: Missing block: B:423:0x069c, code skipped:
            if (r7 != 3) goto L_0x06ef;
     */
        /* JADX WARNING: Missing block: B:425:0x06a4, code skipped:
            if (r5.getConfig() != android.graphics.Bitmap.Config.ARGB_8888) goto L_0x0701;
     */
        /* JADX WARNING: Missing block: B:427:0x06a9, code skipped:
            if (r10.inPurgeable == false) goto L_0x06ad;
     */
        /* JADX WARNING: Missing block: B:428:0x06ab, code skipped:
            r13 = 0;
     */
        /* JADX WARNING: Missing block: B:429:0x06ad, code skipped:
            r13 = 1;
     */
        /* JADX WARNING: Missing block: B:430:0x06ae, code skipped:
            org.telegram.messenger.Utilities.blurBitmap(r5, 7, r13, r5.getWidth(), r5.getHeight(), r5.getRowBytes());
     */
        /* JADX WARNING: Missing block: B:431:0x06c1, code skipped:
            if (r10.inPurgeable == false) goto L_0x06c5;
     */
        /* JADX WARNING: Missing block: B:432:0x06c3, code skipped:
            r13 = 0;
     */
        /* JADX WARNING: Missing block: B:433:0x06c5, code skipped:
            r13 = 1;
     */
        /* JADX WARNING: Missing block: B:434:0x06c6, code skipped:
            org.telegram.messenger.Utilities.blurBitmap(r5, 7, r13, r5.getWidth(), r5.getHeight(), r5.getRowBytes());
     */
        /* JADX WARNING: Missing block: B:435:0x06d9, code skipped:
            if (r10.inPurgeable == false) goto L_0x06dd;
     */
        /* JADX WARNING: Missing block: B:436:0x06db, code skipped:
            r13 = 0;
     */
        /* JADX WARNING: Missing block: B:437:0x06dd, code skipped:
            r13 = 1;
     */
        /* JADX WARNING: Missing block: B:438:0x06de, code skipped:
            org.telegram.messenger.Utilities.blurBitmap(r5, 7, r13, r5.getWidth(), r5.getHeight(), r5.getRowBytes());
     */
        /* JADX WARNING: Missing block: B:439:0x06ef, code skipped:
            if (r7 != 0) goto L_0x0701;
     */
        /* JADX WARNING: Missing block: B:441:0x06f3, code skipped:
            if (r10.inPurgeable == false) goto L_0x0701;
     */
        /* JADX WARNING: Missing block: B:442:0x06f5, code skipped:
            org.telegram.messenger.Utilities.pinBitmap(r5);
     */
        /* JADX WARNING: Missing block: B:448:0x06fc, code skipped:
            r0 = th;
     */
        /* JADX WARNING: Missing block: B:449:0x06fd, code skipped:
            r9 = false;
     */
        /* JADX WARNING: Missing block: B:450:0x06fe, code skipped:
            org.telegram.messenger.FileLog.e(r0);
     */
        /* JADX WARNING: Missing block: B:451:0x0701, code skipped:
            r0 = 0;
            r12 = null;
     */
        /* JADX WARNING: Missing block: B:452:0x0705, code skipped:
            r12 = 20;
     */
        /* JADX WARNING: Missing block: B:453:0x0707, code skipped:
            if (r20 != null) goto L_0x0709;
     */
        /* JADX WARNING: Missing block: B:454:0x0709, code skipped:
            r12 = 0;
     */
        /* JADX WARNING: Missing block: B:455:0x070a, code skipped:
            if (r12 != 0) goto L_0x070c;
     */
        /* JADX WARNING: Missing block: B:458:0x0716, code skipped:
            if (org.telegram.messenger.ImageLoader.access$2200(r1.this$0) != 0) goto L_0x0718;
     */
        /* JADX WARNING: Missing block: B:459:0x0718, code skipped:
            r28 = r8;
            r29 = r9;
            r8 = (long) r12;
     */
        /* JADX WARNING: Missing block: B:463:0x0733, code skipped:
            java.lang.Thread.sleep(r8);
     */
        /* JADX WARNING: Missing block: B:464:0x0737, code skipped:
            r9 = false;
            r12 = null;
     */
        /* JADX WARNING: Missing block: B:466:0x073b, code skipped:
            r28 = r8;
            r29 = r9;
     */
        /* JADX WARNING: Missing block: B:468:?, code skipped:
            org.telegram.messenger.ImageLoader.access$2202(r1.this$0, java.lang.System.currentTimeMillis());
     */
        /* JADX WARNING: Missing block: B:469:0x074a, code skipped:
            monitor-enter(r1.sync);
     */
        /* JADX WARNING: Missing block: B:472:0x074d, code skipped:
            if (r1.isCancelled != false) goto L_0x074f;
     */
        /* JADX WARNING: Missing block: B:474:0x0750, code skipped:
            return;
     */
        /* JADX WARNING: Missing block: B:476:0x0752, code skipped:
            if (r2 != false) goto L_0x076a;
     */
        /* JADX WARNING: Missing block: B:479:0x0758, code skipped:
            if (r1.cacheImage.filter == null) goto L_0x076a;
     */
        /* JADX WARNING: Missing block: B:480:0x075a, code skipped:
            if (r7 != 0) goto L_0x076a;
     */
        /* JADX WARNING: Missing block: B:482:0x0762, code skipped:
            if (r1.cacheImage.imageLocation.path == null) goto L_0x0765;
     */
        /* JADX WARNING: Missing block: B:484:0x0765, code skipped:
            r10.inPreferredConfig = android.graphics.Bitmap.Config.RGB_565;
     */
        /* JADX WARNING: Missing block: B:486:?, code skipped:
            r10.inPreferredConfig = android.graphics.Bitmap.Config.ARGB_8888;
     */
        /* JADX WARNING: Missing block: B:487:0x076e, code skipped:
            r10.inDither = false;
     */
        /* JADX WARNING: Missing block: B:488:0x0771, code skipped:
            if (r20 == null) goto L_0x0796;
     */
        /* JADX WARNING: Missing block: B:489:0x0773, code skipped:
            if (r26 != null) goto L_0x0796;
     */
        /* JADX WARNING: Missing block: B:490:0x0775, code skipped:
            if (r15 == null) goto L_0x0787;
     */
        /* JADX WARNING: Missing block: B:492:?, code skipped:
            r5 = android.provider.MediaStore.Video.Thumbnails.getThumbnail(org.telegram.messenger.ApplicationLoader.applicationContext.getContentResolver(), r20.longValue(), 1, r10);
     */
        /* JADX WARNING: Missing block: B:493:0x0787, code skipped:
            r5 = android.provider.MediaStore.Images.Thumbnails.getThumbnail(org.telegram.messenger.ApplicationLoader.applicationContext.getContentResolver(), r20.longValue(), 1, r10);
     */
        /* JADX WARNING: Missing block: B:494:0x0796, code skipped:
            if (r5 != null) goto L_0x08a3;
     */
        /* JADX WARNING: Missing block: B:495:0x0798, code skipped:
            if (r25 == null) goto L_0x07e7;
     */
        /* JADX WARNING: Missing block: B:496:0x079a, code skipped:
            r2 = new java.io.RandomAccessFile(r3, "r");
            r4 = r2.getChannel().map(java.nio.channels.FileChannel.MapMode.READ_ONLY, 0, r3.length());
            r6 = new android.graphics.BitmapFactory.Options();
            r6.inJustDecodeBounds = true;
     */
        /* JADX WARNING: Missing block: B:499:?, code skipped:
            org.telegram.messenger.Utilities.loadWebpImage(null, r4, r4.limit(), r6, true);
     */
        /* JADX WARNING: Missing block: B:501:?, code skipped:
            r5 = org.telegram.messenger.Bitmaps.createBitmap(r6.outWidth, r6.outHeight, android.graphics.Bitmap.Config.ARGB_8888);
            r6 = r4.limit();
     */
        /* JADX WARNING: Missing block: B:502:0x07d1, code skipped:
            if (r10.inPurgeable != false) goto L_0x07d5;
     */
        /* JADX WARNING: Missing block: B:503:0x07d3, code skipped:
            r8 = true;
     */
        /* JADX WARNING: Missing block: B:504:0x07d5, code skipped:
            r8 = false;
     */
        /* JADX WARNING: Missing block: B:507:?, code skipped:
            org.telegram.messenger.Utilities.loadWebpImage(r5, r4, r6, null, r8);
     */
        /* JADX WARNING: Missing block: B:509:?, code skipped:
            r2.close();
     */
        /* JADX WARNING: Missing block: B:510:0x07dd, code skipped:
            r9 = 0;
            r12 = null;
     */
        /* JADX WARNING: Missing block: B:511:0x07e1, code skipped:
            r12 = null;
     */
        /* JADX WARNING: Missing block: B:513:0x07e4, code skipped:
            r12 = null;
     */
        /* JADX WARNING: Missing block: B:517:0x07e9, code skipped:
            if (r10.inPurgeable != false) goto L_0x0844;
     */
        /* JADX WARNING: Missing block: B:518:0x07eb, code skipped:
            if (r6 == null) goto L_0x07ee;
     */
        /* JADX WARNING: Missing block: B:519:0x07ee, code skipped:
            if (r4 == null) goto L_0x07fa;
     */
        /* JADX WARNING: Missing block: B:521:?, code skipped:
            r2 = new org.telegram.messenger.secretmedia.EncryptedFileInputStream(r3, r1.cacheImage.encryptionKeyPath);
     */
        /* JADX WARNING: Missing block: B:523:?, code skipped:
            r2 = new java.io.FileInputStream(r3);
     */
        /* JADX WARNING: Missing block: B:525:0x0807, code skipped:
            if ((r1.cacheImage.imageLocation.document instanceof org.telegram.tgnet.TLRPC.TL_document) == false) goto L_0x083a;
     */
        /* JADX WARNING: Missing block: B:527:?, code skipped:
            r4 = new androidx.exifinterface.media.ExifInterface(r2).getAttributeInt("Orientation", 1);
     */
        /* JADX WARNING: Missing block: B:529:0x0816, code skipped:
            if (r4 == 3) goto L_0x0826;
     */
        /* JADX WARNING: Missing block: B:531:0x0819, code skipped:
            if (r4 == 6) goto L_0x0823;
     */
        /* JADX WARNING: Missing block: B:533:0x081d, code skipped:
            if (r4 == 8) goto L_0x0820;
     */
        /* JADX WARNING: Missing block: B:534:0x0820, code skipped:
            r9 = 270;
     */
        /* JADX WARNING: Missing block: B:535:0x0823, code skipped:
            r9 = 90;
     */
        /* JADX WARNING: Missing block: B:536:0x0826, code skipped:
            r9 = 180;
     */
        /* JADX WARNING: Missing block: B:543:0x083a, code skipped:
            r9 = 0;
     */
        /* JADX WARNING: Missing block: B:547:0x0844, code skipped:
            r12 = null;
     */
        /* JADX WARNING: Missing block: B:549:?, code skipped:
            r2 = new java.io.RandomAccessFile(r3, "r");
            r9 = (int) r2.length();
            r8 = (byte[]) org.telegram.messenger.ImageLoader.access$2100().get();
     */
        /* JADX WARNING: Missing block: B:550:0x085b, code skipped:
            if (r8 == null) goto L_0x0861;
     */
        /* JADX WARNING: Missing block: B:552:0x085e, code skipped:
            if (r8.length < r9) goto L_0x0861;
     */
        /* JADX WARNING: Missing block: B:554:0x0861, code skipped:
            r8 = null;
     */
        /* JADX WARNING: Missing block: B:555:0x0862, code skipped:
            if (r8 != null) goto L_0x086d;
     */
        /* JADX WARNING: Missing block: B:556:0x0864, code skipped:
            r8 = new byte[r9];
            org.telegram.messenger.ImageLoader.access$2100().set(r8);
     */
        /* JADX WARNING: Missing block: B:557:0x086d, code skipped:
            r2.readFully(r8, 0, r9);
            r2.close();
     */
        /* JADX WARNING: Missing block: B:558:0x0874, code skipped:
            if (r6 == null) goto L_0x0890;
     */
        /* JADX WARNING: Missing block: B:559:0x0876, code skipped:
            org.telegram.messenger.secretmedia.EncryptedFileInputStream.decryptBytesWithKeyFile(r8, 0, r9, r6);
            r2 = org.telegram.messenger.Utilities.computeSHA256(r8, 0, r9);
     */
        /* JADX WARNING: Missing block: B:560:0x087d, code skipped:
            if (r11 == null) goto L_0x0888;
     */
        /* JADX WARNING: Missing block: B:562:0x0883, code skipped:
            if (java.util.Arrays.equals(r2, r11) != false) goto L_0x0886;
     */
        /* JADX WARNING: Missing block: B:564:0x0886, code skipped:
            r2 = null;
     */
        /* JADX WARNING: Missing block: B:565:0x0888, code skipped:
            r2 = 1;
     */
        /* JADX WARNING: Missing block: B:566:0x0889, code skipped:
            r4 = r8[0] & 255;
            r9 = r9 - r4;
     */
        /* JADX WARNING: Missing block: B:567:0x0890, code skipped:
            if (r4 == null) goto L_0x089a;
     */
        /* JADX WARNING: Missing block: B:568:0x0892, code skipped:
            org.telegram.messenger.secretmedia.EncryptedFileInputStream.decryptBytesWithKeyFile(r8, 0, r9, r1.cacheImage.encryptionKeyPath);
     */
        /* JADX WARNING: Missing block: B:569:0x089a, code skipped:
            r2 = null;
            r4 = 0;
     */
        /* JADX WARNING: Missing block: B:570:0x089c, code skipped:
            if (r2 != null) goto L_0x08a4;
     */
        /* JADX WARNING: Missing block: B:571:0x089e, code skipped:
            r5 = android.graphics.BitmapFactory.decodeByteArray(r8, r4, r9, r10);
     */
        /* JADX WARNING: Missing block: B:572:0x08a3, code skipped:
            r12 = null;
     */
        /* JADX WARNING: Missing block: B:573:0x08a4, code skipped:
            r9 = 0;
     */
        /* JADX WARNING: Missing block: B:653:0x09a9, code skipped:
            r12 = null;
     */
        /* JADX WARNING: Missing block: B:660:0x09ae, code skipped:
            r12 = null;
     */
        /* JADX WARNING: Missing block: B:662:0x09af, code skipped:
            r9 = false;
     */
        /* JADX WARNING: Missing block: B:664:0x09b0, code skipped:
            r24 = 0;
     */
        /* JADX WARNING: Missing block: B:669:0x09bc, code skipped:
            if (r5 != null) goto L_0x09be;
     */
        /* JADX WARNING: Missing block: B:670:0x09be, code skipped:
            r0 = new android.graphics.drawable.BitmapDrawable(r5);
     */
        /* JADX WARNING: Missing block: B:671:0x09c4, code skipped:
            r0 = r12;
     */
        /* JADX WARNING: Missing block: B:672:0x09c5, code skipped:
            onPostExecute(r0);
     */
        /* JADX WARNING: Missing block: B:674:0x09cb, code skipped:
            r2 = new org.telegram.messenger.ExtendedBitmapDrawable(r5, r9, r0);
     */
        /* JADX WARNING: Missing block: B:675:0x09d1, code skipped:
            r2 = r12;
     */
        /* JADX WARNING: Missing block: B:677:0x09d6, code skipped:
            r0 = org.telegram.messenger.AndroidUtilities.dp(360.0f);
            r2 = org.telegram.messenger.AndroidUtilities.dp(640.0f);
            r3 = r1.cacheImage.filter;
     */
        /* JADX WARNING: Missing block: B:678:0x09e7, code skipped:
            if (r3 == null) goto L_0x0a0a;
     */
        /* JADX WARNING: Missing block: B:679:0x09e9, code skipped:
            r3 = r3.split("_");
     */
        /* JADX WARNING: Missing block: B:680:0x09f1, code skipped:
            if (r3.length < 2) goto L_0x0a0a;
     */
        /* JADX WARNING: Missing block: B:681:0x09f3, code skipped:
            r4 = false;
            r0 = java.lang.Float.parseFloat(r3[0]);
            r2 = java.lang.Float.parseFloat(r3[1]);
            r3 = org.telegram.messenger.AndroidUtilities.density;
            r0 = (int) (r0 * r3);
            r2 = (int) (r2 * r3);
     */
        /* JADX WARNING: Missing block: B:682:0x0a0a, code skipped:
            r4 = false;
     */
        /* JADX WARNING: Missing block: B:684:?, code skipped:
            r3 = r1.cacheImage.finalFilePath;
     */
        /* JADX WARNING: Missing block: B:685:0x0a14, code skipped:
            if (r1.cacheImage.imageType != 4) goto L_0x0a17;
     */
        /* JADX WARNING: Missing block: B:686:0x0a16, code skipped:
            r4 = true;
     */
        /* JADX WARNING: Missing block: B:687:0x0a17, code skipped:
            r5 = org.telegram.ui.Components.SvgHelper.getBitmap(r3, r0, r2, r4);
     */
        /* JADX WARNING: Missing block: B:688:0x0a1c, code skipped:
            r0 = move-exception;
     */
        /* JADX WARNING: Missing block: B:689:0x0a1d, code skipped:
            org.telegram.messenger.FileLog.e(r0);
            r5 = null;
     */
        public void run() {
            /*
            r37 = this;
            r1 = r37;
            r2 = r1.sync;
            monitor-enter(r2);
            r0 = java.lang.Thread.currentThread();	 Catch:{ all -> 0x0a2e }
            r1.runningThread = r0;	 Catch:{ all -> 0x0a2e }
            java.lang.Thread.interrupted();	 Catch:{ all -> 0x0a2e }
            r0 = r1.isCancelled;	 Catch:{ all -> 0x0a2e }
            if (r0 == 0) goto L_0x0014;
        L_0x0012:
            monitor-exit(r2);	 Catch:{ all -> 0x0a2e }
            return;
        L_0x0014:
            monitor-exit(r2);	 Catch:{ all -> 0x0a2e }
            r0 = r1.cacheImage;
            r2 = r0.imageLocation;
            r3 = r2.photoSize;
            r4 = r3 instanceof org.telegram.tgnet.TLRPC.TL_photoStrippedSize;
            if (r4 == 0) goto L_0x0037;
        L_0x001f:
            r3 = (org.telegram.tgnet.TLRPC.TL_photoStrippedSize) r3;
            r2 = r3.bytes;
            r0 = r0.filter;
            r0 = org.telegram.messenger.ImageLoader.getStrippedPhotoBitmap(r2, r0);
            if (r0 == 0) goto L_0x0031;
        L_0x002b:
            r5 = new android.graphics.drawable.BitmapDrawable;
            r5.<init>(r0);
            goto L_0x0032;
        L_0x0031:
            r5 = 0;
        L_0x0032:
            r1.onPostExecute(r5);
            goto L_0x0a2d;
        L_0x0037:
            r3 = r0.imageType;
            r4 = 5;
            if (r3 != r4) goto L_0x0052;
        L_0x003c:
            r3 = new org.telegram.ui.Components.ThemePreviewDrawable;	 Catch:{ all -> 0x0048 }
            r0 = r0.finalFilePath;	 Catch:{ all -> 0x0048 }
            r2 = r2.document;	 Catch:{ all -> 0x0048 }
            r2 = (org.telegram.messenger.DocumentObject.ThemeDocument) r2;	 Catch:{ all -> 0x0048 }
            r3.<init>(r0, r2);	 Catch:{ all -> 0x0048 }
            goto L_0x004d;
        L_0x0048:
            r0 = move-exception;
            org.telegram.messenger.FileLog.e(r0);
            r3 = 0;
        L_0x004d:
            r1.onPostExecute(r3);
            goto L_0x0a2d;
        L_0x0052:
            r2 = 3;
            r6 = 4;
            r7 = 2;
            r8 = 1;
            r9 = 0;
            if (r3 == r2) goto L_0x09d6;
        L_0x0059:
            if (r3 != r6) goto L_0x005d;
        L_0x005b:
            goto L_0x09d6;
        L_0x005d:
            r10 = 8;
            if (r3 != r8) goto L_0x015f;
        L_0x0061:
            r0 = NUM; // 0x432a999a float:170.6 double:5.56745435E-315;
            r3 = org.telegram.messenger.AndroidUtilities.dp(r0);
            r11 = 512; // 0x200 float:7.175E-43 double:2.53E-321;
            r3 = java.lang.Math.min(r11, r3);
            r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
            r0 = java.lang.Math.min(r11, r0);
            r12 = r1.cacheImage;
            r12 = r12.filter;
            if (r12 == 0) goto L_0x0145;
        L_0x007c:
            r13 = "_";
            r12 = r12.split(r13);
            r13 = r12.length;
            if (r13 < r7) goto L_0x00c7;
        L_0x0085:
            r0 = r12[r9];
            r0 = java.lang.Float.parseFloat(r0);
            r3 = r12[r8];
            r3 = java.lang.Float.parseFloat(r3);
            r13 = org.telegram.messenger.AndroidUtilities.density;
            r13 = r13 * r0;
            r13 = (int) r13;
            r13 = java.lang.Math.min(r11, r13);
            r14 = org.telegram.messenger.AndroidUtilities.density;
            r14 = r14 * r3;
            r14 = (int) r14;
            r11 = java.lang.Math.min(r11, r14);
            r14 = NUM; // 0x42b40000 float:90.0 double:5.529052754E-315;
            r0 = (r0 > r14 ? 1 : (r0 == r14 ? 0 : -1));
            if (r0 > 0) goto L_0x00bd;
        L_0x00a9:
            r0 = (r3 > r14 ? 1 : (r3 == r14 ? 0 : -1));
            if (r0 > 0) goto L_0x00bd;
        L_0x00ad:
            r0 = 160; // 0xa0 float:2.24E-43 double:7.9E-322;
            r0 = java.lang.Math.min(r13, r0);
            r3 = 160; // 0xa0 float:2.24E-43 double:7.9E-322;
            r3 = java.lang.Math.min(r11, r3);
            r11 = r3;
            r3 = r0;
            r0 = 1;
            goto L_0x00bf;
        L_0x00bd:
            r3 = r13;
            r0 = 0;
        L_0x00bf:
            r13 = org.telegram.messenger.SharedConfig.getDevicePerfomanceClass();
            if (r13 == r7) goto L_0x00c9;
        L_0x00c5:
            r9 = 1;
            goto L_0x00c9;
        L_0x00c7:
            r11 = r0;
            r0 = 0;
        L_0x00c9:
            r13 = r12.length;
            if (r13 < r2) goto L_0x00e3;
        L_0x00cc:
            r13 = r12[r7];
            r14 = "nr";
            r13 = r14.equals(r13);
            if (r13 == 0) goto L_0x00d8;
        L_0x00d6:
            r8 = 2;
            goto L_0x00e3;
        L_0x00d8:
            r7 = r12[r7];
            r13 = "nrs";
            r7 = r13.equals(r7);
            if (r7 == 0) goto L_0x00e3;
        L_0x00e2:
            r8 = 3;
        L_0x00e3:
            r2 = r12.length;
            if (r2 < r4) goto L_0x013e;
        L_0x00e6:
            r2 = r12[r6];
            r4 = "c1";
            r2 = r4.equals(r2);
            if (r2 == 0) goto L_0x00fe;
        L_0x00f0:
            r5 = new int[r10];
            r5 = {16219713, 13275258, 16757049, 15582629, 16765248, 16245699, 16768889, 16510934};
        L_0x00f5:
            r17 = r0;
            r14 = r3;
            r18 = r5;
            r16 = r9;
            r15 = r11;
            goto L_0x014d;
        L_0x00fe:
            r2 = r12[r6];
            r4 = "c2";
            r2 = r4.equals(r2);
            if (r2 == 0) goto L_0x010e;
        L_0x0108:
            r5 = new int[r10];
            r5 = {16219713, 11172960, 16757049, 13150599, 16765248, 14534815, 16768889, 15128242};
            goto L_0x00f5;
        L_0x010e:
            r2 = r12[r6];
            r4 = "c3";
            r2 = r4.equals(r2);
            if (r2 == 0) goto L_0x011e;
        L_0x0118:
            r5 = new int[r10];
            r5 = {16219713, 9199944, 16757049, 11371874, 16765248, 12885622, 16768889, 13939080};
            goto L_0x00f5;
        L_0x011e:
            r2 = r12[r6];
            r4 = "c4";
            r2 = r4.equals(r2);
            if (r2 == 0) goto L_0x012e;
        L_0x0128:
            r5 = new int[r10];
            r5 = {16219713, 7224364, 16757049, 9591348, 16765248, 10579526, 16768889, 11303506};
            goto L_0x00f5;
        L_0x012e:
            r2 = r12[r6];
            r4 = "c5";
            r2 = r4.equals(r2);
            if (r2 == 0) goto L_0x013e;
        L_0x0138:
            r5 = new int[r10];
            r5 = {16219713, 2694162, 16757049, 4663842, 16765248, 5716784, 16768889, 6834492};
            goto L_0x00f5;
        L_0x013e:
            r17 = r0;
            r14 = r3;
            r16 = r9;
            r15 = r11;
            goto L_0x014b;
        L_0x0145:
            r15 = r0;
            r14 = r3;
            r16 = 0;
            r17 = 0;
        L_0x014b:
            r18 = 0;
        L_0x014d:
            r0 = new org.telegram.ui.Components.RLottieDrawable;
            r2 = r1.cacheImage;
            r13 = r2.finalFilePath;
            r12 = r0;
            r12.<init>(r13, r14, r15, r16, r17, r18);
            r0.setAutoRepeat(r8);
            r1.onPostExecute(r0);
            goto L_0x0a2d;
        L_0x015f:
            if (r3 != r7) goto L_0x01bb;
        L_0x0161:
            r0 = r0.filter;
            r2 = "g";
            r0 = r2.equals(r0);
            if (r0 == 0) goto L_0x0191;
        L_0x016b:
            r0 = r1.cacheImage;
            r2 = r0.imageLocation;
            r2 = r2.document;
            r3 = r2 instanceof org.telegram.tgnet.TLRPC.TL_documentEncrypted;
            if (r3 != 0) goto L_0x0191;
        L_0x0175:
            r3 = new org.telegram.ui.Components.AnimatedFileDrawable;
            r7 = r0.finalFilePath;
            r8 = 0;
            r0 = r0.size;
            r9 = (long) r0;
            r0 = r2 instanceof org.telegram.tgnet.TLRPC.Document;
            if (r0 == 0) goto L_0x0183;
        L_0x0181:
            r11 = r2;
            goto L_0x0184;
        L_0x0183:
            r11 = 0;
        L_0x0184:
            r0 = r1.cacheImage;
            r12 = r0.parentObject;
            r13 = r0.currentAccount;
            r14 = 0;
            r6 = r3;
            r6.<init>(r7, r8, r9, r11, r12, r13, r14);
            r0 = r3;
            goto L_0x01b3;
        L_0x0191:
            r0 = new org.telegram.ui.Components.AnimatedFileDrawable;
            r2 = r1.cacheImage;
            r3 = r2.finalFilePath;
            r2 = r2.filter;
            r4 = "d";
            r17 = r4.equals(r2);
            r18 = 0;
            r20 = 0;
            r21 = 0;
            r2 = r1.cacheImage;
            r2 = r2.currentAccount;
            r23 = 0;
            r15 = r0;
            r16 = r3;
            r22 = r2;
            r15.<init>(r16, r17, r18, r20, r21, r22, r23);
        L_0x01b3:
            java.lang.Thread.interrupted();
            r1.onPostExecute(r0);
            goto L_0x0a2d;
        L_0x01bb:
            r3 = r0.finalFilePath;
            r4 = r0.secureDocument;
            if (r4 != 0) goto L_0x01d6;
        L_0x01c1:
            r0 = r0.encryptionKeyPath;
            if (r0 == 0) goto L_0x01d4;
        L_0x01c5:
            if (r3 == 0) goto L_0x01d4;
        L_0x01c7:
            r0 = r3.getAbsolutePath();
            r4 = ".enc";
            r0 = r0.endsWith(r4);
            if (r0 == 0) goto L_0x01d4;
        L_0x01d3:
            goto L_0x01d6;
        L_0x01d4:
            r4 = 0;
            goto L_0x01d7;
        L_0x01d6:
            r4 = 1;
        L_0x01d7:
            r0 = r1.cacheImage;
            r0 = r0.secureDocument;
            if (r0 == 0) goto L_0x01f0;
        L_0x01dd:
            r6 = r0.secureDocumentKey;
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
            r11 = r0;
            goto L_0x01f2;
        L_0x01f0:
            r6 = 0;
            r11 = 0;
        L_0x01f2:
            r0 = android.os.Build.VERSION.SDK_INT;
            r12 = 19;
            if (r0 >= r12) goto L_0x0262;
        L_0x01f8:
            r12 = new java.io.RandomAccessFile;	 Catch:{ Exception -> 0x0248, all -> 0x0244 }
            r0 = "r";
            r12.<init>(r3, r0);	 Catch:{ Exception -> 0x0248, all -> 0x0244 }
            r0 = r1.cacheImage;	 Catch:{ Exception -> 0x0242 }
            r0 = r0.type;	 Catch:{ Exception -> 0x0242 }
            if (r0 != r8) goto L_0x020a;
        L_0x0205:
            r0 = org.telegram.messenger.ImageLoader.headerThumb;	 Catch:{ Exception -> 0x0242 }
            goto L_0x020e;
        L_0x020a:
            r0 = org.telegram.messenger.ImageLoader.header;	 Catch:{ Exception -> 0x0242 }
        L_0x020e:
            r13 = r0.length;	 Catch:{ Exception -> 0x0242 }
            r12.readFully(r0, r9, r13);	 Catch:{ Exception -> 0x0242 }
            r13 = new java.lang.String;	 Catch:{ Exception -> 0x0242 }
            r13.<init>(r0);	 Catch:{ Exception -> 0x0242 }
            r0 = r13.toLowerCase();	 Catch:{ Exception -> 0x0242 }
            r0 = r0.toLowerCase();	 Catch:{ Exception -> 0x0242 }
            r13 = "riff";
            r13 = r0.startsWith(r13);	 Catch:{ Exception -> 0x0242 }
            if (r13 == 0) goto L_0x0232;
        L_0x0227:
            r13 = "webp";
            r0 = r0.endsWith(r13);	 Catch:{ Exception -> 0x0242 }
            if (r0 == 0) goto L_0x0232;
        L_0x0230:
            r13 = 1;
            goto L_0x0233;
        L_0x0232:
            r13 = 0;
        L_0x0233:
            r12.close();	 Catch:{ Exception -> 0x0240 }
            r12.close();	 Catch:{ Exception -> 0x023a }
            goto L_0x0263;
        L_0x023a:
            r0 = move-exception;
            r12 = r0;
            org.telegram.messenger.FileLog.e(r12);
            goto L_0x0263;
        L_0x0240:
            r0 = move-exception;
            goto L_0x024b;
        L_0x0242:
            r0 = move-exception;
            goto L_0x024a;
        L_0x0244:
            r0 = move-exception;
            r2 = r0;
            r12 = 0;
            goto L_0x0256;
        L_0x0248:
            r0 = move-exception;
            r12 = 0;
        L_0x024a:
            r13 = 0;
        L_0x024b:
            org.telegram.messenger.FileLog.e(r0);	 Catch:{ all -> 0x0254 }
            if (r12 == 0) goto L_0x0263;
        L_0x0250:
            r12.close();	 Catch:{ Exception -> 0x023a }
            goto L_0x0263;
        L_0x0254:
            r0 = move-exception;
            r2 = r0;
        L_0x0256:
            if (r12 == 0) goto L_0x0261;
        L_0x0258:
            r12.close();	 Catch:{ Exception -> 0x025c }
            goto L_0x0261;
        L_0x025c:
            r0 = move-exception;
            r3 = r0;
            org.telegram.messenger.FileLog.e(r3);
        L_0x0261:
            throw r2;
        L_0x0262:
            r13 = 0;
        L_0x0263:
            r0 = r1.cacheImage;
            r0 = r0.imageLocation;
            r0 = r0.path;
            if (r0 == 0) goto L_0x02c9;
        L_0x026b:
            r12 = "thumb://";
            r12 = r0.startsWith(r12);
            if (r12 == 0) goto L_0x0295;
        L_0x0274:
            r12 = ":";
            r12 = r0.indexOf(r12, r10);
            if (r12 < 0) goto L_0x028e;
        L_0x027c:
            r14 = r0.substring(r10, r12);
            r14 = java.lang.Long.parseLong(r14);
            r14 = java.lang.Long.valueOf(r14);
            r12 = r12 + r8;
            r0 = r0.substring(r12);
            goto L_0x0290;
        L_0x028e:
            r0 = 0;
            r14 = 0;
        L_0x0290:
            r12 = r0;
        L_0x0291:
            r15 = 0;
        L_0x0292:
            r16 = 0;
            goto L_0x02ce;
        L_0x0295:
            r12 = "vthumb://";
            r12 = r0.startsWith(r12);
            if (r12 == 0) goto L_0x02be;
        L_0x029e:
            r12 = 9;
            r14 = ":";
            r12 = r0.indexOf(r14, r12);
            if (r12 < 0) goto L_0x02b8;
        L_0x02a8:
            r14 = 9;
            r0 = r0.substring(r14, r12);
            r14 = java.lang.Long.parseLong(r0);
            r0 = java.lang.Long.valueOf(r14);
            r12 = 1;
            goto L_0x02ba;
        L_0x02b8:
            r0 = 0;
            r12 = 0;
        L_0x02ba:
            r14 = r0;
            r15 = r12;
            r12 = 0;
            goto L_0x0292;
        L_0x02be:
            r12 = "http";
            r0 = r0.startsWith(r12);
            if (r0 != 0) goto L_0x02c9;
        L_0x02c6:
            r12 = 0;
            r14 = 0;
            goto L_0x0291;
        L_0x02c9:
            r12 = 0;
            r14 = 0;
            r15 = 0;
            r16 = 1;
        L_0x02ce:
            r10 = new android.graphics.BitmapFactory$Options;
            r10.<init>();
            r10.inSampleSize = r8;
            r0 = android.os.Build.VERSION.SDK_INT;
            r2 = 21;
            if (r0 >= r2) goto L_0x02dd;
        L_0x02db:
            r10.inPurgeable = r8;
        L_0x02dd:
            r0 = org.telegram.messenger.ImageLoader.this;
            r2 = r0.canForce8888;
            r19 = 0;
            r0 = r1.cacheImage;	 Catch:{ all -> 0x04f2 }
            r0 = r0.filter;	 Catch:{ all -> 0x04f2 }
            r20 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
            if (r0 == 0) goto L_0x0498;
        L_0x02ed:
            r0 = r1.cacheImage;	 Catch:{ all -> 0x04f2 }
            r0 = r0.filter;	 Catch:{ all -> 0x04f2 }
            r5 = "_";
            r0 = r0.split(r5);	 Catch:{ all -> 0x04f2 }
            r5 = r0.length;	 Catch:{ all -> 0x04f2 }
            if (r5 < r7) goto L_0x0325;
        L_0x02fa:
            r5 = r0[r9];	 Catch:{ all -> 0x031b }
            r5 = java.lang.Float.parseFloat(r5);	 Catch:{ all -> 0x031b }
            r22 = org.telegram.messenger.AndroidUtilities.density;	 Catch:{ all -> 0x031b }
            r5 = r5 * r22;
            r0 = r0[r8];	 Catch:{ all -> 0x0311 }
            r0 = java.lang.Float.parseFloat(r0);	 Catch:{ all -> 0x0311 }
            r22 = org.telegram.messenger.AndroidUtilities.density;	 Catch:{ all -> 0x0311 }
            r0 = r0 * r22;
            r22 = r0;
            goto L_0x0328;
        L_0x0311:
            r0 = move-exception;
            r8 = r5;
            r26 = r12;
            r25 = r13;
            r5 = 0;
            r7 = 0;
            goto L_0x04fb;
        L_0x031b:
            r0 = move-exception;
            r26 = r12;
            r25 = r13;
            r5 = 0;
            r7 = 0;
            r8 = 0;
            goto L_0x04fb;
        L_0x0325:
            r5 = 0;
            r22 = 0;
        L_0x0328:
            r0 = r1.cacheImage;	 Catch:{ all -> 0x048d }
            r0 = r0.filter;	 Catch:{ all -> 0x048d }
            r7 = "b2";
            r0 = r0.contains(r7);	 Catch:{ all -> 0x048d }
            if (r0 == 0) goto L_0x0336;
        L_0x0334:
            r7 = 3;
            goto L_0x0353;
        L_0x0336:
            r0 = r1.cacheImage;	 Catch:{ all -> 0x048d }
            r0 = r0.filter;	 Catch:{ all -> 0x048d }
            r7 = "b1";
            r0 = r0.contains(r7);	 Catch:{ all -> 0x048d }
            if (r0 == 0) goto L_0x0344;
        L_0x0342:
            r7 = 2;
            goto L_0x0353;
        L_0x0344:
            r0 = r1.cacheImage;	 Catch:{ all -> 0x048d }
            r0 = r0.filter;	 Catch:{ all -> 0x048d }
            r7 = "b";
            r0 = r0.contains(r7);	 Catch:{ all -> 0x048d }
            if (r0 == 0) goto L_0x0352;
        L_0x0350:
            r7 = 1;
            goto L_0x0353;
        L_0x0352:
            r7 = 0;
        L_0x0353:
            r0 = r1.cacheImage;	 Catch:{ all -> 0x0485 }
            r0 = r0.filter;	 Catch:{ all -> 0x0485 }
            r9 = "i";
            r9 = r0.contains(r9);	 Catch:{ all -> 0x0485 }
            r0 = r1.cacheImage;	 Catch:{ all -> 0x047c }
            r0 = r0.filter;	 Catch:{ all -> 0x047c }
            r8 = "f";
            r0 = r0.contains(r8);	 Catch:{ all -> 0x047c }
            if (r0 == 0) goto L_0x036a;
        L_0x0369:
            r2 = 1;
        L_0x036a:
            if (r13 != 0) goto L_0x0471;
        L_0x036c:
            r0 = (r5 > r19 ? 1 : (r5 == r19 ? 0 : -1));
            if (r0 == 0) goto L_0x0471;
        L_0x0370:
            r0 = (r22 > r19 ? 1 : (r22 == r19 ? 0 : -1));
            if (r0 == 0) goto L_0x0471;
        L_0x0374:
            r8 = 1;
            r10.inJustDecodeBounds = r8;	 Catch:{ all -> 0x046d }
            if (r14 == 0) goto L_0x03a8;
        L_0x0379:
            if (r12 != 0) goto L_0x03a8;
        L_0x037b:
            if (r15 == 0) goto L_0x038f;
        L_0x037d:
            r0 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ all -> 0x047c }
            r0 = r0.getContentResolver();	 Catch:{ all -> 0x047c }
            r26 = r12;
            r25 = r13;
            r12 = r14.longValue();	 Catch:{ all -> 0x03a5 }
            android.provider.MediaStore.Video.Thumbnails.getThumbnail(r0, r12, r8, r10);	 Catch:{ all -> 0x03a5 }
            goto L_0x03a1;
        L_0x038f:
            r26 = r12;
            r25 = r13;
            r0 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ all -> 0x03a5 }
            r0 = r0.getContentResolver();	 Catch:{ all -> 0x03a5 }
            r12 = r14.longValue();	 Catch:{ all -> 0x03a5 }
            r8 = 1;
            android.provider.MediaStore.Images.Thumbnails.getThumbnail(r0, r12, r8, r10);	 Catch:{ all -> 0x03a5 }
        L_0x03a1:
            r27 = r2;
            goto L_0x041f;
        L_0x03a5:
            r0 = move-exception;
            goto L_0x0481;
        L_0x03a8:
            r26 = r12;
            r25 = r13;
            if (r6 == 0) goto L_0x0405;
        L_0x03ae:
            r0 = new java.io.RandomAccessFile;	 Catch:{ all -> 0x0400 }
            r8 = "r";
            r0.<init>(r3, r8);	 Catch:{ all -> 0x0400 }
            r12 = r0.length();	 Catch:{ all -> 0x0400 }
            r8 = (int) r12;	 Catch:{ all -> 0x0400 }
            r12 = org.telegram.messenger.ImageLoader.bytesLocal;	 Catch:{ all -> 0x0400 }
            r12 = r12.get();	 Catch:{ all -> 0x0400 }
            r12 = (byte[]) r12;	 Catch:{ all -> 0x0400 }
            if (r12 == 0) goto L_0x03ca;
        L_0x03c6:
            r13 = r12.length;	 Catch:{ all -> 0x03a5 }
            if (r13 < r8) goto L_0x03ca;
        L_0x03c9:
            goto L_0x03cb;
        L_0x03ca:
            r12 = 0;
        L_0x03cb:
            if (r12 != 0) goto L_0x03d6;
        L_0x03cd:
            r12 = new byte[r8];	 Catch:{ all -> 0x03a5 }
            r13 = org.telegram.messenger.ImageLoader.bytesLocal;	 Catch:{ all -> 0x03a5 }
            r13.set(r12);	 Catch:{ all -> 0x03a5 }
        L_0x03d6:
            r13 = 0;
            r0.readFully(r12, r13, r8);	 Catch:{ all -> 0x0400 }
            r0.close();	 Catch:{ all -> 0x0400 }
            org.telegram.messenger.secretmedia.EncryptedFileInputStream.decryptBytesWithKeyFile(r12, r13, r8, r6);	 Catch:{ all -> 0x0400 }
            r0 = org.telegram.messenger.Utilities.computeSHA256(r12, r13, r8);	 Catch:{ all -> 0x0400 }
            if (r11 == 0) goto L_0x03f1;
        L_0x03e6:
            r0 = java.util.Arrays.equals(r0, r11);	 Catch:{ all -> 0x03a5 }
            if (r0 != 0) goto L_0x03ed;
        L_0x03ec:
            goto L_0x03f1;
        L_0x03ed:
            r27 = r2;
            r0 = 0;
            goto L_0x03f4;
        L_0x03f1:
            r27 = r2;
            r0 = 1;
        L_0x03f4:
            r13 = 0;
            r2 = r12[r13];	 Catch:{ all -> 0x0468 }
            r2 = r2 & 255;
            r8 = r8 - r2;
            if (r0 != 0) goto L_0x041f;
        L_0x03fc:
            android.graphics.BitmapFactory.decodeByteArray(r12, r2, r8, r10);	 Catch:{ all -> 0x0468 }
            goto L_0x041f;
        L_0x0400:
            r0 = move-exception;
            r27 = r2;
            goto L_0x0481;
        L_0x0405:
            r27 = r2;
            if (r4 == 0) goto L_0x0413;
        L_0x0409:
            r0 = new org.telegram.messenger.secretmedia.EncryptedFileInputStream;	 Catch:{ all -> 0x0468 }
            r2 = r1.cacheImage;	 Catch:{ all -> 0x0468 }
            r2 = r2.encryptionKeyPath;	 Catch:{ all -> 0x0468 }
            r0.<init>(r3, r2);	 Catch:{ all -> 0x0468 }
            goto L_0x0418;
        L_0x0413:
            r0 = new java.io.FileInputStream;	 Catch:{ all -> 0x0468 }
            r0.<init>(r3);	 Catch:{ all -> 0x0468 }
        L_0x0418:
            r2 = 0;
            android.graphics.BitmapFactory.decodeStream(r0, r2, r10);	 Catch:{ all -> 0x0468 }
            r0.close();	 Catch:{ all -> 0x0468 }
        L_0x041f:
            r0 = r10.outWidth;	 Catch:{ all -> 0x0468 }
            r0 = (float) r0;	 Catch:{ all -> 0x0468 }
            r2 = r10.outHeight;	 Catch:{ all -> 0x0468 }
            r2 = (float) r2;	 Catch:{ all -> 0x0468 }
            r8 = (r5 > r22 ? 1 : (r5 == r22 ? 0 : -1));
            if (r8 < 0) goto L_0x0436;
        L_0x0429:
            r8 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1));
            if (r8 <= 0) goto L_0x0436;
        L_0x042d:
            r8 = r0 / r5;
            r12 = r2 / r22;
            r8 = java.lang.Math.max(r8, r12);	 Catch:{ all -> 0x0468 }
            goto L_0x043e;
        L_0x0436:
            r8 = r0 / r5;
            r12 = r2 / r22;
            r8 = java.lang.Math.min(r8, r12);	 Catch:{ all -> 0x0468 }
        L_0x043e:
            r12 = NUM; // 0x3var_a float:1.2 double:5.271833295E-315;
            r12 = (r8 > r12 ? 1 : (r8 == r12 ? 0 : -1));
            if (r12 >= 0) goto L_0x0447;
        L_0x0445:
            r8 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        L_0x0447:
            r12 = 0;
            r10.inJustDecodeBounds = r12;	 Catch:{ all -> 0x0468 }
            r12 = (r8 > r20 ? 1 : (r8 == r20 ? 0 : -1));
            if (r12 <= 0) goto L_0x0464;
        L_0x044e:
            r0 = (r0 > r5 ? 1 : (r0 == r5 ? 0 : -1));
            if (r0 > 0) goto L_0x0456;
        L_0x0452:
            r0 = (r2 > r22 ? 1 : (r2 == r22 ? 0 : -1));
            if (r0 <= 0) goto L_0x0464;
        L_0x0456:
            r0 = 1;
        L_0x0457:
            r2 = 2;
            r0 = r0 * 2;
            r2 = r0 * 2;
            r2 = (float) r2;	 Catch:{ all -> 0x0468 }
            r2 = (r2 > r8 ? 1 : (r2 == r8 ? 0 : -1));
            if (r2 < 0) goto L_0x0457;
        L_0x0461:
            r10.inSampleSize = r0;	 Catch:{ all -> 0x0468 }
            goto L_0x0477;
        L_0x0464:
            r0 = (int) r8;	 Catch:{ all -> 0x0468 }
            r10.inSampleSize = r0;	 Catch:{ all -> 0x0468 }
            goto L_0x0477;
        L_0x0468:
            r0 = move-exception;
            r8 = r5;
            r2 = r27;
            goto L_0x0482;
        L_0x046d:
            r0 = move-exception;
            r27 = r2;
            goto L_0x047d;
        L_0x0471:
            r27 = r2;
            r26 = r12;
            r25 = r13;
        L_0x0477:
            r2 = r27;
            r0 = 0;
            goto L_0x04ef;
        L_0x047c:
            r0 = move-exception;
        L_0x047d:
            r26 = r12;
            r25 = r13;
        L_0x0481:
            r8 = r5;
        L_0x0482:
            r5 = 0;
            goto L_0x04fd;
        L_0x0485:
            r0 = move-exception;
            r26 = r12;
            r25 = r13;
            r8 = r5;
            r5 = 0;
            goto L_0x0495;
        L_0x048d:
            r0 = move-exception;
            r26 = r12;
            r25 = r13;
            r8 = r5;
            r5 = 0;
            r7 = 0;
        L_0x0495:
            r9 = 0;
            goto L_0x04fd;
        L_0x0498:
            r26 = r12;
            r25 = r13;
            if (r26 == 0) goto L_0x04e9;
        L_0x049e:
            r5 = 1;
            r10.inJustDecodeBounds = r5;	 Catch:{ all -> 0x04e7 }
            if (r2 == 0) goto L_0x04a6;
        L_0x04a3:
            r0 = android.graphics.Bitmap.Config.ARGB_8888;	 Catch:{ all -> 0x04e7 }
            goto L_0x04a8;
        L_0x04a6:
            r0 = android.graphics.Bitmap.Config.RGB_565;	 Catch:{ all -> 0x04e7 }
        L_0x04a8:
            r10.inPreferredConfig = r0;	 Catch:{ all -> 0x04e7 }
            r0 = new java.io.FileInputStream;	 Catch:{ all -> 0x04e7 }
            r0.<init>(r3);	 Catch:{ all -> 0x04e7 }
            r5 = 0;
            r7 = android.graphics.BitmapFactory.decodeStream(r0, r5, r10);	 Catch:{ all -> 0x04e7 }
            r0.close();	 Catch:{ all -> 0x04e4 }
            r0 = r10.outWidth;	 Catch:{ all -> 0x04e4 }
            r5 = r10.outHeight;	 Catch:{ all -> 0x04e4 }
            r8 = 0;
            r10.inJustDecodeBounds = r8;	 Catch:{ all -> 0x04e4 }
            r0 = r0 / 200;
            r5 = r5 / 200;
            r0 = java.lang.Math.max(r0, r5);	 Catch:{ all -> 0x04e4 }
            r0 = (float) r0;	 Catch:{ all -> 0x04e4 }
            r5 = (r0 > r20 ? 1 : (r0 == r20 ? 0 : -1));
            if (r5 >= 0) goto L_0x04cd;
        L_0x04cb:
            r0 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        L_0x04cd:
            r5 = (r0 > r20 ? 1 : (r0 == r20 ? 0 : -1));
            if (r5 <= 0) goto L_0x04df;
        L_0x04d1:
            r5 = 1;
        L_0x04d2:
            r8 = 2;
            r5 = r5 * 2;
            r8 = r5 * 2;
            r8 = (float) r8;	 Catch:{ all -> 0x04e4 }
            r8 = (r8 > r0 ? 1 : (r8 == r0 ? 0 : -1));
            if (r8 < 0) goto L_0x04d2;
        L_0x04dc:
            r10.inSampleSize = r5;	 Catch:{ all -> 0x04e4 }
            goto L_0x04e2;
        L_0x04df:
            r0 = (int) r0;	 Catch:{ all -> 0x04e4 }
            r10.inSampleSize = r0;	 Catch:{ all -> 0x04e4 }
        L_0x04e2:
            r0 = r7;
            goto L_0x04ea;
        L_0x04e4:
            r0 = move-exception;
            r5 = r7;
            goto L_0x04f8;
        L_0x04e7:
            r0 = move-exception;
            goto L_0x04f7;
        L_0x04e9:
            r0 = 0;
        L_0x04ea:
            r5 = 0;
            r7 = 0;
            r9 = 0;
            r22 = 0;
        L_0x04ef:
            r8 = r5;
            r5 = r0;
            goto L_0x0500;
        L_0x04f2:
            r0 = move-exception;
            r26 = r12;
            r25 = r13;
        L_0x04f7:
            r5 = 0;
        L_0x04f8:
            r7 = 0;
            r8 = 0;
            r9 = 0;
        L_0x04fb:
            r22 = 0;
        L_0x04fd:
            org.telegram.messenger.FileLog.e(r0);
        L_0x0500:
            r0 = r22;
            r12 = r1.cacheImage;
            r12 = r12.type;
            r20 = r14;
            r13 = 1;
            if (r12 != r13) goto L_0x0705;
        L_0x050b:
            r0 = org.telegram.messenger.ImageLoader.this;	 Catch:{ all -> 0x06fc }
            r12 = java.lang.System.currentTimeMillis();	 Catch:{ all -> 0x06fc }
            r0.lastCacheOutTime = r12;	 Catch:{ all -> 0x06fc }
            r2 = r1.sync;	 Catch:{ all -> 0x06fc }
            monitor-enter(r2);	 Catch:{ all -> 0x06fc }
            r0 = r1.isCancelled;	 Catch:{ all -> 0x06f9 }
            if (r0 == 0) goto L_0x051d;
        L_0x051b:
            monitor-exit(r2);	 Catch:{ all -> 0x06f9 }
            return;
        L_0x051d:
            monitor-exit(r2);	 Catch:{ all -> 0x06f9 }
            if (r25 == 0) goto L_0x0565;
        L_0x0520:
            r0 = new java.io.RandomAccessFile;	 Catch:{ all -> 0x06fc }
            r2 = "r";
            r0.<init>(r3, r2);	 Catch:{ all -> 0x06fc }
            r11 = r0.getChannel();	 Catch:{ all -> 0x06fc }
            r12 = java.nio.channels.FileChannel.MapMode.READ_ONLY;	 Catch:{ all -> 0x06fc }
            r13 = 0;
            r15 = r3.length();	 Catch:{ all -> 0x06fc }
            r2 = r11.map(r12, r13, r15);	 Catch:{ all -> 0x06fc }
            r4 = new android.graphics.BitmapFactory$Options;	 Catch:{ all -> 0x06fc }
            r4.<init>();	 Catch:{ all -> 0x06fc }
            r6 = 1;
            r4.inJustDecodeBounds = r6;	 Catch:{ all -> 0x06fc }
            r11 = r2.limit();	 Catch:{ all -> 0x06fc }
            r12 = 0;
            org.telegram.messenger.Utilities.loadWebpImage(r12, r2, r11, r4, r6);	 Catch:{ all -> 0x06fc }
            r6 = r4.outWidth;	 Catch:{ all -> 0x06fc }
            r4 = r4.outHeight;	 Catch:{ all -> 0x06fc }
            r11 = android.graphics.Bitmap.Config.ARGB_8888;	 Catch:{ all -> 0x06fc }
            r5 = org.telegram.messenger.Bitmaps.createBitmap(r6, r4, r11);	 Catch:{ all -> 0x06fc }
            r4 = r2.limit();	 Catch:{ all -> 0x06fc }
            r6 = r10.inPurgeable;	 Catch:{ all -> 0x06fc }
            if (r6 != 0) goto L_0x055b;
        L_0x0559:
            r6 = 1;
            goto L_0x055c;
        L_0x055b:
            r6 = 0;
        L_0x055c:
            r11 = 0;
            org.telegram.messenger.Utilities.loadWebpImage(r5, r2, r4, r11, r6);	 Catch:{ all -> 0x06fc }
            r0.close();	 Catch:{ all -> 0x06fc }
            goto L_0x05e3;
        L_0x0565:
            r0 = r10.inPurgeable;	 Catch:{ all -> 0x06fc }
            if (r0 != 0) goto L_0x0586;
        L_0x0569:
            if (r6 == 0) goto L_0x056c;
        L_0x056b:
            goto L_0x0586;
        L_0x056c:
            if (r4 == 0) goto L_0x0578;
        L_0x056e:
            r0 = new org.telegram.messenger.secretmedia.EncryptedFileInputStream;	 Catch:{ all -> 0x06fc }
            r2 = r1.cacheImage;	 Catch:{ all -> 0x06fc }
            r2 = r2.encryptionKeyPath;	 Catch:{ all -> 0x06fc }
            r0.<init>(r3, r2);	 Catch:{ all -> 0x06fc }
            goto L_0x057d;
        L_0x0578:
            r0 = new java.io.FileInputStream;	 Catch:{ all -> 0x06fc }
            r0.<init>(r3);	 Catch:{ all -> 0x06fc }
        L_0x057d:
            r2 = 0;
            r5 = android.graphics.BitmapFactory.decodeStream(r0, r2, r10);	 Catch:{ all -> 0x06fc }
            r0.close();	 Catch:{ all -> 0x06fc }
            goto L_0x05e3;
        L_0x0586:
            r0 = new java.io.RandomAccessFile;	 Catch:{ all -> 0x06fc }
            r2 = "r";
            r0.<init>(r3, r2);	 Catch:{ all -> 0x06fc }
            r12 = r0.length();	 Catch:{ all -> 0x06fc }
            r2 = (int) r12;	 Catch:{ all -> 0x06fc }
            r12 = org.telegram.messenger.ImageLoader.bytesThumbLocal;	 Catch:{ all -> 0x06fc }
            r12 = r12.get();	 Catch:{ all -> 0x06fc }
            r12 = (byte[]) r12;	 Catch:{ all -> 0x06fc }
            if (r12 == 0) goto L_0x05a2;
        L_0x059e:
            r13 = r12.length;	 Catch:{ all -> 0x06fc }
            if (r13 < r2) goto L_0x05a2;
        L_0x05a1:
            goto L_0x05a3;
        L_0x05a2:
            r12 = 0;
        L_0x05a3:
            if (r12 != 0) goto L_0x05ae;
        L_0x05a5:
            r12 = new byte[r2];	 Catch:{ all -> 0x06fc }
            r13 = org.telegram.messenger.ImageLoader.bytesThumbLocal;	 Catch:{ all -> 0x06fc }
            r13.set(r12);	 Catch:{ all -> 0x06fc }
        L_0x05ae:
            r13 = 0;
            r0.readFully(r12, r13, r2);	 Catch:{ all -> 0x06fc }
            r0.close();	 Catch:{ all -> 0x06fc }
            if (r6 == 0) goto L_0x05d1;
        L_0x05b7:
            org.telegram.messenger.secretmedia.EncryptedFileInputStream.decryptBytesWithKeyFile(r12, r13, r2, r6);	 Catch:{ all -> 0x06fc }
            r0 = org.telegram.messenger.Utilities.computeSHA256(r12, r13, r2);	 Catch:{ all -> 0x06fc }
            if (r11 == 0) goto L_0x05c9;
        L_0x05c0:
            r0 = java.util.Arrays.equals(r0, r11);	 Catch:{ all -> 0x06fc }
            if (r0 != 0) goto L_0x05c7;
        L_0x05c6:
            goto L_0x05c9;
        L_0x05c7:
            r0 = 0;
            goto L_0x05ca;
        L_0x05c9:
            r0 = 1;
        L_0x05ca:
            r4 = 0;
            r6 = r12[r4];	 Catch:{ all -> 0x06fc }
            r4 = r6 & 255;
            r2 = r2 - r4;
            goto L_0x05dd;
        L_0x05d1:
            if (r4 == 0) goto L_0x05db;
        L_0x05d3:
            r0 = r1.cacheImage;	 Catch:{ all -> 0x06fc }
            r0 = r0.encryptionKeyPath;	 Catch:{ all -> 0x06fc }
            r4 = 0;
            org.telegram.messenger.secretmedia.EncryptedFileInputStream.decryptBytesWithKeyFile(r12, r4, r2, r0);	 Catch:{ all -> 0x06fc }
        L_0x05db:
            r0 = 0;
            r4 = 0;
        L_0x05dd:
            if (r0 != 0) goto L_0x05e3;
        L_0x05df:
            r5 = android.graphics.BitmapFactory.decodeByteArray(r12, r4, r2, r10);	 Catch:{ all -> 0x06fc }
        L_0x05e3:
            if (r5 != 0) goto L_0x05fb;
        L_0x05e5:
            r6 = r3.length();	 Catch:{ all -> 0x06fc }
            r8 = 0;
            r0 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1));
            if (r0 == 0) goto L_0x05f5;
        L_0x05ef:
            r0 = r1.cacheImage;	 Catch:{ all -> 0x06fc }
            r0 = r0.filter;	 Catch:{ all -> 0x06fc }
            if (r0 != 0) goto L_0x05f8;
        L_0x05f5:
            r3.delete();	 Catch:{ all -> 0x06fc }
        L_0x05f8:
            r9 = 0;
            goto L_0x0701;
        L_0x05fb:
            r0 = r1.cacheImage;	 Catch:{ all -> 0x06fc }
            r0 = r0.filter;	 Catch:{ all -> 0x06fc }
            if (r0 == 0) goto L_0x062d;
        L_0x0601:
            r0 = r5.getWidth();	 Catch:{ all -> 0x06fc }
            r0 = (float) r0;	 Catch:{ all -> 0x06fc }
            r2 = r5.getHeight();	 Catch:{ all -> 0x06fc }
            r2 = (float) r2;	 Catch:{ all -> 0x06fc }
            r3 = r10.inPurgeable;	 Catch:{ all -> 0x06fc }
            if (r3 != 0) goto L_0x062d;
        L_0x060f:
            r3 = (r8 > r19 ? 1 : (r8 == r19 ? 0 : -1));
            if (r3 == 0) goto L_0x062d;
        L_0x0613:
            r3 = (r0 > r8 ? 1 : (r0 == r8 ? 0 : -1));
            if (r3 == 0) goto L_0x062d;
        L_0x0617:
            r3 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
            r3 = r3 + r8;
            r3 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1));
            if (r3 <= 0) goto L_0x062d;
        L_0x061e:
            r0 = r0 / r8;
            r3 = (int) r8;	 Catch:{ all -> 0x06fc }
            r2 = r2 / r0;
            r0 = (int) r2;	 Catch:{ all -> 0x06fc }
            r2 = 1;
            r0 = org.telegram.messenger.Bitmaps.createScaledBitmap(r5, r3, r0, r2);	 Catch:{ all -> 0x06fc }
            if (r5 == r0) goto L_0x062d;
        L_0x0629:
            r5.recycle();	 Catch:{ all -> 0x06fc }
            r5 = r0;
        L_0x062d:
            if (r9 == 0) goto L_0x064d;
        L_0x062f:
            r0 = r10.inPurgeable;	 Catch:{ all -> 0x06fc }
            if (r0 == 0) goto L_0x0635;
        L_0x0633:
            r0 = 0;
            goto L_0x0636;
        L_0x0635:
            r0 = 1;
        L_0x0636:
            r2 = r5.getWidth();	 Catch:{ all -> 0x06fc }
            r3 = r5.getHeight();	 Catch:{ all -> 0x06fc }
            r4 = r5.getRowBytes();	 Catch:{ all -> 0x06fc }
            r0 = org.telegram.messenger.Utilities.needInvert(r5, r0, r2, r3, r4);	 Catch:{ all -> 0x06fc }
            if (r0 == 0) goto L_0x064a;
        L_0x0648:
            r9 = 1;
            goto L_0x064b;
        L_0x064a:
            r9 = 0;
        L_0x064b:
            r2 = 1;
            goto L_0x064f;
        L_0x064d:
            r2 = 1;
            r9 = 0;
        L_0x064f:
            if (r7 != r2) goto L_0x0676;
        L_0x0651:
            r0 = r5.getConfig();	 Catch:{ all -> 0x0673 }
            r2 = android.graphics.Bitmap.Config.ARGB_8888;	 Catch:{ all -> 0x0673 }
            if (r0 != r2) goto L_0x0701;
        L_0x0659:
            r12 = 3;
            r0 = r10.inPurgeable;	 Catch:{ all -> 0x0673 }
            if (r0 == 0) goto L_0x0660;
        L_0x065e:
            r13 = 0;
            goto L_0x0661;
        L_0x0660:
            r13 = 1;
        L_0x0661:
            r14 = r5.getWidth();	 Catch:{ all -> 0x0673 }
            r15 = r5.getHeight();	 Catch:{ all -> 0x0673 }
            r16 = r5.getRowBytes();	 Catch:{ all -> 0x0673 }
            r11 = r5;
            org.telegram.messenger.Utilities.blurBitmap(r11, r12, r13, r14, r15, r16);	 Catch:{ all -> 0x0673 }
            goto L_0x0701;
        L_0x0673:
            r0 = move-exception;
            goto L_0x06fe;
        L_0x0676:
            r2 = 2;
            if (r7 != r2) goto L_0x069b;
        L_0x0679:
            r0 = r5.getConfig();	 Catch:{ all -> 0x0673 }
            r2 = android.graphics.Bitmap.Config.ARGB_8888;	 Catch:{ all -> 0x0673 }
            if (r0 != r2) goto L_0x0701;
        L_0x0681:
            r12 = 1;
            r0 = r10.inPurgeable;	 Catch:{ all -> 0x0673 }
            if (r0 == 0) goto L_0x0688;
        L_0x0686:
            r13 = 0;
            goto L_0x0689;
        L_0x0688:
            r13 = 1;
        L_0x0689:
            r14 = r5.getWidth();	 Catch:{ all -> 0x0673 }
            r15 = r5.getHeight();	 Catch:{ all -> 0x0673 }
            r16 = r5.getRowBytes();	 Catch:{ all -> 0x0673 }
            r11 = r5;
            org.telegram.messenger.Utilities.blurBitmap(r11, r12, r13, r14, r15, r16);	 Catch:{ all -> 0x0673 }
            goto L_0x0701;
        L_0x069b:
            r2 = 3;
            if (r7 != r2) goto L_0x06ef;
        L_0x069e:
            r0 = r5.getConfig();	 Catch:{ all -> 0x0673 }
            r2 = android.graphics.Bitmap.Config.ARGB_8888;	 Catch:{ all -> 0x0673 }
            if (r0 != r2) goto L_0x0701;
        L_0x06a6:
            r12 = 7;
            r0 = r10.inPurgeable;	 Catch:{ all -> 0x0673 }
            if (r0 == 0) goto L_0x06ad;
        L_0x06ab:
            r13 = 0;
            goto L_0x06ae;
        L_0x06ad:
            r13 = 1;
        L_0x06ae:
            r14 = r5.getWidth();	 Catch:{ all -> 0x0673 }
            r15 = r5.getHeight();	 Catch:{ all -> 0x0673 }
            r16 = r5.getRowBytes();	 Catch:{ all -> 0x0673 }
            r11 = r5;
            org.telegram.messenger.Utilities.blurBitmap(r11, r12, r13, r14, r15, r16);	 Catch:{ all -> 0x0673 }
            r12 = 7;
            r0 = r10.inPurgeable;	 Catch:{ all -> 0x0673 }
            if (r0 == 0) goto L_0x06c5;
        L_0x06c3:
            r13 = 0;
            goto L_0x06c6;
        L_0x06c5:
            r13 = 1;
        L_0x06c6:
            r14 = r5.getWidth();	 Catch:{ all -> 0x0673 }
            r15 = r5.getHeight();	 Catch:{ all -> 0x0673 }
            r16 = r5.getRowBytes();	 Catch:{ all -> 0x0673 }
            r11 = r5;
            org.telegram.messenger.Utilities.blurBitmap(r11, r12, r13, r14, r15, r16);	 Catch:{ all -> 0x0673 }
            r12 = 7;
            r0 = r10.inPurgeable;	 Catch:{ all -> 0x0673 }
            if (r0 == 0) goto L_0x06dd;
        L_0x06db:
            r13 = 0;
            goto L_0x06de;
        L_0x06dd:
            r13 = 1;
        L_0x06de:
            r14 = r5.getWidth();	 Catch:{ all -> 0x0673 }
            r15 = r5.getHeight();	 Catch:{ all -> 0x0673 }
            r16 = r5.getRowBytes();	 Catch:{ all -> 0x0673 }
            r11 = r5;
            org.telegram.messenger.Utilities.blurBitmap(r11, r12, r13, r14, r15, r16);	 Catch:{ all -> 0x0673 }
            goto L_0x0701;
        L_0x06ef:
            if (r7 != 0) goto L_0x0701;
        L_0x06f1:
            r0 = r10.inPurgeable;	 Catch:{ all -> 0x0673 }
            if (r0 == 0) goto L_0x0701;
        L_0x06f5:
            org.telegram.messenger.Utilities.pinBitmap(r5);	 Catch:{ all -> 0x0673 }
            goto L_0x0701;
        L_0x06f9:
            r0 = move-exception;
            monitor-exit(r2);	 Catch:{ all -> 0x06f9 }
            throw r0;	 Catch:{ all -> 0x06fc }
        L_0x06fc:
            r0 = move-exception;
            r9 = 0;
        L_0x06fe:
            org.telegram.messenger.FileLog.e(r0);
        L_0x0701:
            r0 = 0;
            r12 = 0;
            goto L_0x09b4;
        L_0x0705:
            r12 = 20;
            if (r20 == 0) goto L_0x070a;
        L_0x0709:
            r12 = 0;
        L_0x070a:
            if (r12 == 0) goto L_0x073b;
        L_0x070c:
            r13 = org.telegram.messenger.ImageLoader.this;	 Catch:{ all -> 0x0737 }
            r13 = r13.lastCacheOutTime;	 Catch:{ all -> 0x0737 }
            r22 = 0;
            r28 = (r13 > r22 ? 1 : (r13 == r22 ? 0 : -1));
            if (r28 == 0) goto L_0x073b;
        L_0x0718:
            r13 = org.telegram.messenger.ImageLoader.this;	 Catch:{ all -> 0x0737 }
            r13 = r13.lastCacheOutTime;	 Catch:{ all -> 0x0737 }
            r22 = java.lang.System.currentTimeMillis();	 Catch:{ all -> 0x0737 }
            r28 = r8;
            r29 = r9;
            r8 = (long) r12;	 Catch:{ all -> 0x0737 }
            r22 = r22 - r8;
            r12 = (r13 > r22 ? 1 : (r13 == r22 ? 0 : -1));
            if (r12 <= 0) goto L_0x073f;
        L_0x072d:
            r12 = android.os.Build.VERSION.SDK_INT;	 Catch:{ all -> 0x0737 }
            r13 = 21;
            if (r12 >= r13) goto L_0x073f;
        L_0x0733:
            java.lang.Thread.sleep(r8);	 Catch:{ all -> 0x0737 }
            goto L_0x073f;
        L_0x0737:
            r9 = 0;
            r12 = 0;
            goto L_0x09b0;
        L_0x073b:
            r28 = r8;
            r29 = r9;
        L_0x073f:
            r8 = org.telegram.messenger.ImageLoader.this;	 Catch:{ all -> 0x09ae }
            r12 = java.lang.System.currentTimeMillis();	 Catch:{ all -> 0x09ae }
            r8.lastCacheOutTime = r12;	 Catch:{ all -> 0x09ae }
            r8 = r1.sync;	 Catch:{ all -> 0x09ae }
            monitor-enter(r8);	 Catch:{ all -> 0x09ae }
            r9 = r1.isCancelled;	 Catch:{ all -> 0x09a8 }
            if (r9 == 0) goto L_0x0751;
        L_0x074f:
            monitor-exit(r8);	 Catch:{ all -> 0x09a8 }
            return;
        L_0x0751:
            monitor-exit(r8);	 Catch:{ all -> 0x09a8 }
            if (r2 != 0) goto L_0x076a;
        L_0x0754:
            r2 = r1.cacheImage;	 Catch:{ all -> 0x0737 }
            r2 = r2.filter;	 Catch:{ all -> 0x0737 }
            if (r2 == 0) goto L_0x076a;
        L_0x075a:
            if (r7 != 0) goto L_0x076a;
        L_0x075c:
            r2 = r1.cacheImage;	 Catch:{ all -> 0x0737 }
            r2 = r2.imageLocation;	 Catch:{ all -> 0x0737 }
            r2 = r2.path;	 Catch:{ all -> 0x0737 }
            if (r2 == 0) goto L_0x0765;
        L_0x0764:
            goto L_0x076a;
        L_0x0765:
            r2 = android.graphics.Bitmap.Config.RGB_565;	 Catch:{ all -> 0x0737 }
            r10.inPreferredConfig = r2;	 Catch:{ all -> 0x0737 }
            goto L_0x076e;
        L_0x076a:
            r2 = android.graphics.Bitmap.Config.ARGB_8888;	 Catch:{ all -> 0x09ae }
            r10.inPreferredConfig = r2;	 Catch:{ all -> 0x09ae }
        L_0x076e:
            r2 = 0;
            r10.inDither = r2;	 Catch:{ all -> 0x09ae }
            if (r20 == 0) goto L_0x0796;
        L_0x0773:
            if (r26 != 0) goto L_0x0796;
        L_0x0775:
            if (r15 == 0) goto L_0x0787;
        L_0x0777:
            r2 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ all -> 0x0737 }
            r2 = r2.getContentResolver();	 Catch:{ all -> 0x0737 }
            r8 = r20.longValue();	 Catch:{ all -> 0x0737 }
            r12 = 1;
            r5 = android.provider.MediaStore.Video.Thumbnails.getThumbnail(r2, r8, r12, r10);	 Catch:{ all -> 0x0737 }
            goto L_0x0796;
        L_0x0787:
            r2 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ all -> 0x0737 }
            r2 = r2.getContentResolver();	 Catch:{ all -> 0x0737 }
            r8 = r20.longValue();	 Catch:{ all -> 0x0737 }
            r12 = 1;
            r5 = android.provider.MediaStore.Images.Thumbnails.getThumbnail(r2, r8, r12, r10);	 Catch:{ all -> 0x0737 }
        L_0x0796:
            if (r5 != 0) goto L_0x08a3;
        L_0x0798:
            if (r25 == 0) goto L_0x07e7;
        L_0x079a:
            r2 = new java.io.RandomAccessFile;	 Catch:{ all -> 0x0737 }
            r4 = "r";
            r2.<init>(r3, r4);	 Catch:{ all -> 0x0737 }
            r30 = r2.getChannel();	 Catch:{ all -> 0x0737 }
            r31 = java.nio.channels.FileChannel.MapMode.READ_ONLY;	 Catch:{ all -> 0x0737 }
            r32 = 0;
            r34 = r3.length();	 Catch:{ all -> 0x0737 }
            r4 = r30.map(r31, r32, r34);	 Catch:{ all -> 0x0737 }
            r6 = new android.graphics.BitmapFactory$Options;	 Catch:{ all -> 0x0737 }
            r6.<init>();	 Catch:{ all -> 0x0737 }
            r8 = 1;
            r6.inJustDecodeBounds = r8;	 Catch:{ all -> 0x0737 }
            r9 = r4.limit();	 Catch:{ all -> 0x0737 }
            r11 = 0;
            org.telegram.messenger.Utilities.loadWebpImage(r11, r4, r9, r6, r8);	 Catch:{ all -> 0x07e4 }
            r8 = r6.outWidth;	 Catch:{ all -> 0x0737 }
            r6 = r6.outHeight;	 Catch:{ all -> 0x0737 }
            r9 = android.graphics.Bitmap.Config.ARGB_8888;	 Catch:{ all -> 0x0737 }
            r5 = org.telegram.messenger.Bitmaps.createBitmap(r8, r6, r9);	 Catch:{ all -> 0x0737 }
            r6 = r4.limit();	 Catch:{ all -> 0x0737 }
            r8 = r10.inPurgeable;	 Catch:{ all -> 0x0737 }
            if (r8 != 0) goto L_0x07d5;
        L_0x07d3:
            r8 = 1;
            goto L_0x07d6;
        L_0x07d5:
            r8 = 0;
        L_0x07d6:
            r9 = 0;
            org.telegram.messenger.Utilities.loadWebpImage(r5, r4, r6, r9, r8);	 Catch:{ all -> 0x07e1 }
            r2.close();	 Catch:{ all -> 0x0737 }
            r9 = 0;
            r12 = 0;
            goto L_0x08a5;
        L_0x07e1:
            r12 = r9;
            goto L_0x09af;
        L_0x07e4:
            r12 = r11;
            goto L_0x09af;
        L_0x07e7:
            r2 = r10.inPurgeable;	 Catch:{ all -> 0x09ae }
            if (r2 != 0) goto L_0x0844;
        L_0x07eb:
            if (r6 == 0) goto L_0x07ee;
        L_0x07ed:
            goto L_0x0844;
        L_0x07ee:
            if (r4 == 0) goto L_0x07fa;
        L_0x07f0:
            r2 = new org.telegram.messenger.secretmedia.EncryptedFileInputStream;	 Catch:{ all -> 0x0737 }
            r4 = r1.cacheImage;	 Catch:{ all -> 0x0737 }
            r4 = r4.encryptionKeyPath;	 Catch:{ all -> 0x0737 }
            r2.<init>(r3, r4);	 Catch:{ all -> 0x0737 }
            goto L_0x07ff;
        L_0x07fa:
            r2 = new java.io.FileInputStream;	 Catch:{ all -> 0x09ae }
            r2.<init>(r3);	 Catch:{ all -> 0x09ae }
        L_0x07ff:
            r4 = r1.cacheImage;	 Catch:{ all -> 0x09ae }
            r4 = r4.imageLocation;	 Catch:{ all -> 0x09ae }
            r4 = r4.document;	 Catch:{ all -> 0x09ae }
            r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_document;	 Catch:{ all -> 0x09ae }
            if (r4 == 0) goto L_0x083a;
        L_0x0809:
            r4 = new androidx.exifinterface.media.ExifInterface;	 Catch:{ all -> 0x0829 }
            r4.<init>(r2);	 Catch:{ all -> 0x0829 }
            r6 = "Orientation";
            r8 = 1;
            r4 = r4.getAttributeInt(r6, r8);	 Catch:{ all -> 0x0829 }
            r6 = 3;
            if (r4 == r6) goto L_0x0826;
        L_0x0818:
            r6 = 6;
            if (r4 == r6) goto L_0x0823;
        L_0x081b:
            r6 = 8;
            if (r4 == r6) goto L_0x0820;
        L_0x081f:
            goto L_0x0829;
        L_0x0820:
            r9 = 270; // 0x10e float:3.78E-43 double:1.334E-321;
            goto L_0x082a;
        L_0x0823:
            r9 = 90;
            goto L_0x082a;
        L_0x0826:
            r9 = 180; // 0xb4 float:2.52E-43 double:8.9E-322;
            goto L_0x082a;
        L_0x0829:
            r9 = 0;
        L_0x082a:
            r4 = r2.getChannel();	 Catch:{ all -> 0x0834 }
            r11 = 0;
            r4.position(r11);	 Catch:{ all -> 0x0834 }
            goto L_0x083b;
        L_0x0834:
            r24 = r9;
            r9 = 0;
            r12 = 0;
            goto L_0x09b2;
        L_0x083a:
            r9 = 0;
        L_0x083b:
            r12 = 0;
            r5 = android.graphics.BitmapFactory.decodeStream(r2, r12, r10);	 Catch:{ all -> 0x09a4 }
            r2.close();	 Catch:{ all -> 0x09a4 }
            goto L_0x08a5;
        L_0x0844:
            r12 = 0;
            r2 = new java.io.RandomAccessFile;	 Catch:{ all -> 0x09af }
            r8 = "r";
            r2.<init>(r3, r8);	 Catch:{ all -> 0x09af }
            r8 = r2.length();	 Catch:{ all -> 0x09af }
            r9 = (int) r8;	 Catch:{ all -> 0x09af }
            r8 = org.telegram.messenger.ImageLoader.bytesLocal;	 Catch:{ all -> 0x09af }
            r8 = r8.get();	 Catch:{ all -> 0x09af }
            r8 = (byte[]) r8;	 Catch:{ all -> 0x09af }
            if (r8 == 0) goto L_0x0861;
        L_0x085d:
            r13 = r8.length;	 Catch:{ all -> 0x09af }
            if (r13 < r9) goto L_0x0861;
        L_0x0860:
            goto L_0x0862;
        L_0x0861:
            r8 = r12;
        L_0x0862:
            if (r8 != 0) goto L_0x086d;
        L_0x0864:
            r8 = new byte[r9];	 Catch:{ all -> 0x09af }
            r13 = org.telegram.messenger.ImageLoader.bytesLocal;	 Catch:{ all -> 0x09af }
            r13.set(r8);	 Catch:{ all -> 0x09af }
        L_0x086d:
            r13 = 0;
            r2.readFully(r8, r13, r9);	 Catch:{ all -> 0x09af }
            r2.close();	 Catch:{ all -> 0x09af }
            if (r6 == 0) goto L_0x0890;
        L_0x0876:
            org.telegram.messenger.secretmedia.EncryptedFileInputStream.decryptBytesWithKeyFile(r8, r13, r9, r6);	 Catch:{ all -> 0x09af }
            r2 = org.telegram.messenger.Utilities.computeSHA256(r8, r13, r9);	 Catch:{ all -> 0x09af }
            if (r11 == 0) goto L_0x0888;
        L_0x087f:
            r2 = java.util.Arrays.equals(r2, r11);	 Catch:{ all -> 0x09af }
            if (r2 != 0) goto L_0x0886;
        L_0x0885:
            goto L_0x0888;
        L_0x0886:
            r2 = 0;
            goto L_0x0889;
        L_0x0888:
            r2 = 1;
        L_0x0889:
            r4 = 0;
            r6 = r8[r4];	 Catch:{ all -> 0x09af }
            r4 = r6 & 255;
            r9 = r9 - r4;
            goto L_0x089c;
        L_0x0890:
            if (r4 == 0) goto L_0x089a;
        L_0x0892:
            r2 = r1.cacheImage;	 Catch:{ all -> 0x09af }
            r2 = r2.encryptionKeyPath;	 Catch:{ all -> 0x09af }
            r4 = 0;
            org.telegram.messenger.secretmedia.EncryptedFileInputStream.decryptBytesWithKeyFile(r8, r4, r9, r2);	 Catch:{ all -> 0x09af }
        L_0x089a:
            r2 = 0;
            r4 = 0;
        L_0x089c:
            if (r2 != 0) goto L_0x08a4;
        L_0x089e:
            r5 = android.graphics.BitmapFactory.decodeByteArray(r8, r4, r9, r10);	 Catch:{ all -> 0x09af }
            goto L_0x08a4;
        L_0x08a3:
            r12 = 0;
        L_0x08a4:
            r9 = 0;
        L_0x08a5:
            if (r5 != 0) goto L_0x08bf;
        L_0x08a7:
            if (r16 == 0) goto L_0x08bc;
        L_0x08a9:
            r6 = r3.length();	 Catch:{ all -> 0x09a4 }
            r10 = 0;
            r0 = (r6 > r10 ? 1 : (r6 == r10 ? 0 : -1));
            if (r0 == 0) goto L_0x08b9;
        L_0x08b3:
            r0 = r1.cacheImage;	 Catch:{ all -> 0x09a4 }
            r0 = r0.filter;	 Catch:{ all -> 0x09a4 }
            if (r0 != 0) goto L_0x08bc;
        L_0x08b9:
            r3.delete();	 Catch:{ all -> 0x09a4 }
        L_0x08bc:
            r0 = 0;
            goto L_0x099e;
        L_0x08bf:
            r2 = r1.cacheImage;	 Catch:{ all -> 0x09a4 }
            r2 = r2.filter;	 Catch:{ all -> 0x09a4 }
            if (r2 == 0) goto L_0x098d;
        L_0x08c5:
            r2 = r5.getWidth();	 Catch:{ all -> 0x09a4 }
            r2 = (float) r2;	 Catch:{ all -> 0x09a4 }
            r3 = r5.getHeight();	 Catch:{ all -> 0x09a4 }
            r3 = (float) r3;	 Catch:{ all -> 0x09a4 }
            r4 = r10.inPurgeable;	 Catch:{ all -> 0x09a4 }
            if (r4 != 0) goto L_0x090a;
        L_0x08d3:
            r4 = (r28 > r19 ? 1 : (r28 == r19 ? 0 : -1));
            if (r4 == 0) goto L_0x090a;
        L_0x08d7:
            r4 = (r2 > r28 ? 1 : (r2 == r28 ? 0 : -1));
            if (r4 == 0) goto L_0x090a;
        L_0x08db:
            r4 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
            r8 = r28 + r4;
            r4 = (r2 > r8 ? 1 : (r2 == r8 ? 0 : -1));
            if (r4 <= 0) goto L_0x090a;
        L_0x08e3:
            r4 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1));
            if (r4 <= 0) goto L_0x08f9;
        L_0x08e7:
            r4 = (r28 > r0 ? 1 : (r28 == r0 ? 0 : -1));
            if (r4 <= 0) goto L_0x08f9;
        L_0x08eb:
            r0 = r2 / r28;
            r8 = r28;
            r4 = (int) r8;	 Catch:{ all -> 0x09a4 }
            r0 = r3 / r0;
            r0 = (int) r0;	 Catch:{ all -> 0x09a4 }
            r6 = 1;
            r0 = org.telegram.messenger.Bitmaps.createScaledBitmap(r5, r4, r0, r6);	 Catch:{ all -> 0x09a4 }
            goto L_0x0904;
        L_0x08f9:
            r6 = 1;
            r4 = r3 / r0;
            r4 = r2 / r4;
            r4 = (int) r4;	 Catch:{ all -> 0x09a4 }
            r0 = (int) r0;	 Catch:{ all -> 0x09a4 }
            r0 = org.telegram.messenger.Bitmaps.createScaledBitmap(r5, r4, r0, r6);	 Catch:{ all -> 0x09a4 }
        L_0x0904:
            if (r5 == r0) goto L_0x090a;
        L_0x0906:
            r5.recycle();	 Catch:{ all -> 0x09a4 }
            goto L_0x090b;
        L_0x090a:
            r0 = r5;
        L_0x090b:
            if (r0 == 0) goto L_0x098c;
        L_0x090d:
            if (r29 == 0) goto L_0x094d;
        L_0x090f:
            r4 = r0.getWidth();	 Catch:{ all -> 0x094a }
            r5 = r0.getHeight();	 Catch:{ all -> 0x094a }
            r4 = r4 * r5;
            r5 = 22500; // 0x57e4 float:3.1529E-41 double:1.11165E-319;
            if (r4 <= r5) goto L_0x0927;
        L_0x091d:
            r4 = 100;
            r5 = 100;
            r6 = 0;
            r4 = org.telegram.messenger.Bitmaps.createScaledBitmap(r0, r4, r5, r6);	 Catch:{ all -> 0x094a }
            goto L_0x0928;
        L_0x0927:
            r4 = r0;
        L_0x0928:
            r5 = r10.inPurgeable;	 Catch:{ all -> 0x094a }
            if (r5 == 0) goto L_0x092e;
        L_0x092c:
            r5 = 0;
            goto L_0x092f;
        L_0x092e:
            r5 = 1;
        L_0x092f:
            r6 = r4.getWidth();	 Catch:{ all -> 0x094a }
            r8 = r4.getHeight();	 Catch:{ all -> 0x094a }
            r11 = r4.getRowBytes();	 Catch:{ all -> 0x094a }
            r5 = org.telegram.messenger.Utilities.needInvert(r4, r5, r6, r8, r11);	 Catch:{ all -> 0x094a }
            if (r5 == 0) goto L_0x0943;
        L_0x0941:
            r5 = 1;
            goto L_0x0944;
        L_0x0943:
            r5 = 0;
        L_0x0944:
            if (r4 == r0) goto L_0x094e;
        L_0x0946:
            r4.recycle();	 Catch:{ all -> 0x097f }
            goto L_0x094e;
        L_0x094a:
            r5 = r0;
            goto L_0x09a4;
        L_0x094d:
            r5 = 0;
        L_0x094e:
            if (r7 == 0) goto L_0x0984;
        L_0x0950:
            r4 = NUM; // 0x42CLASSNAME float:100.0 double:5.53552857E-315;
            r3 = (r3 > r4 ? 1 : (r3 == r4 ? 0 : -1));
            if (r3 >= 0) goto L_0x0984;
        L_0x0956:
            r3 = NUM; // 0x42CLASSNAME float:100.0 double:5.53552857E-315;
            r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1));
            if (r2 >= 0) goto L_0x0984;
        L_0x095c:
            r2 = r0.getConfig();	 Catch:{ all -> 0x097f }
            r3 = android.graphics.Bitmap.Config.ARGB_8888;	 Catch:{ all -> 0x097f }
            if (r2 != r3) goto L_0x097c;
        L_0x0964:
            r14 = 3;
            r2 = r10.inPurgeable;	 Catch:{ all -> 0x097f }
            if (r2 == 0) goto L_0x096b;
        L_0x0969:
            r15 = 0;
            goto L_0x096c;
        L_0x096b:
            r15 = 1;
        L_0x096c:
            r16 = r0.getWidth();	 Catch:{ all -> 0x097f }
            r17 = r0.getHeight();	 Catch:{ all -> 0x097f }
            r18 = r0.getRowBytes();	 Catch:{ all -> 0x097f }
            r13 = r0;
            org.telegram.messenger.Utilities.blurBitmap(r13, r14, r15, r16, r17, r18);	 Catch:{ all -> 0x097f }
        L_0x097c:
            r24 = 1;
            goto L_0x0986;
        L_0x097f:
            r24 = r9;
            r9 = r5;
            r5 = r0;
            goto L_0x09b2;
        L_0x0984:
            r24 = 0;
        L_0x0986:
            r36 = r5;
            r5 = r0;
            r0 = r36;
            goto L_0x0990;
        L_0x098c:
            r5 = r0;
        L_0x098d:
            r0 = 0;
            r24 = 0;
        L_0x0990:
            if (r24 != 0) goto L_0x099e;
        L_0x0992:
            r2 = r10.inPurgeable;	 Catch:{ all -> 0x099a }
            if (r2 == 0) goto L_0x099e;
        L_0x0996:
            org.telegram.messenger.Utilities.pinBitmap(r5);	 Catch:{ all -> 0x099a }
            goto L_0x099e;
        L_0x099a:
            r24 = r9;
            r9 = r0;
            goto L_0x09b2;
        L_0x099e:
            r36 = r9;
            r9 = r0;
            r0 = r36;
            goto L_0x09b4;
        L_0x09a4:
            r24 = r9;
            r9 = 0;
            goto L_0x09b2;
        L_0x09a8:
            r0 = move-exception;
            r12 = 0;
        L_0x09aa:
            monitor-exit(r8);	 Catch:{ all -> 0x09ac }
            throw r0;	 Catch:{ all -> 0x09af }
        L_0x09ac:
            r0 = move-exception;
            goto L_0x09aa;
        L_0x09ae:
            r12 = 0;
        L_0x09af:
            r9 = 0;
        L_0x09b0:
            r24 = 0;
        L_0x09b2:
            r0 = r24;
        L_0x09b4:
            java.lang.Thread.interrupted();
            if (r9 != 0) goto L_0x09c9;
        L_0x09b9:
            if (r0 == 0) goto L_0x09bc;
        L_0x09bb:
            goto L_0x09c9;
        L_0x09bc:
            if (r5 == 0) goto L_0x09c4;
        L_0x09be:
            r0 = new android.graphics.drawable.BitmapDrawable;
            r0.<init>(r5);
            goto L_0x09c5;
        L_0x09c4:
            r0 = r12;
        L_0x09c5:
            r1.onPostExecute(r0);
            goto L_0x0a2d;
        L_0x09c9:
            if (r5 == 0) goto L_0x09d1;
        L_0x09cb:
            r2 = new org.telegram.messenger.ExtendedBitmapDrawable;
            r2.<init>(r5, r9, r0);
            goto L_0x09d2;
        L_0x09d1:
            r2 = r12;
        L_0x09d2:
            r1.onPostExecute(r2);
            goto L_0x0a2d;
        L_0x09d6:
            r12 = 0;
            r0 = NUM; // 0x43b40000 float:360.0 double:5.611943214E-315;
            r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
            r2 = NUM; // 0x44200000 float:640.0 double:5.646912627E-315;
            r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
            r3 = r1.cacheImage;
            r3 = r3.filter;
            if (r3 == 0) goto L_0x0a0a;
        L_0x09e9:
            r4 = "_";
            r3 = r3.split(r4);
            r4 = r3.length;
            r5 = 2;
            if (r4 < r5) goto L_0x0a0a;
        L_0x09f3:
            r4 = 0;
            r0 = r3[r4];
            r0 = java.lang.Float.parseFloat(r0);
            r8 = 1;
            r2 = r3[r8];
            r2 = java.lang.Float.parseFloat(r2);
            r3 = org.telegram.messenger.AndroidUtilities.density;
            r0 = r0 * r3;
            r0 = (int) r0;
            r2 = r2 * r3;
            r2 = (int) r2;
            goto L_0x0a0c;
        L_0x0a0a:
            r4 = 0;
            r8 = 1;
        L_0x0a0c:
            r3 = r1.cacheImage;	 Catch:{ all -> 0x0a1c }
            r3 = r3.finalFilePath;	 Catch:{ all -> 0x0a1c }
            r5 = r1.cacheImage;	 Catch:{ all -> 0x0a1c }
            r5 = r5.imageType;	 Catch:{ all -> 0x0a1c }
            if (r5 != r6) goto L_0x0a17;
        L_0x0a16:
            r4 = 1;
        L_0x0a17:
            r5 = org.telegram.ui.Components.SvgHelper.getBitmap(r3, r0, r2, r4);	 Catch:{ all -> 0x0a1c }
            goto L_0x0a21;
        L_0x0a1c:
            r0 = move-exception;
            org.telegram.messenger.FileLog.e(r0);
            r5 = r12;
        L_0x0a21:
            if (r5 == 0) goto L_0x0a29;
        L_0x0a23:
            r0 = new android.graphics.drawable.BitmapDrawable;
            r0.<init>(r5);
            goto L_0x0a2a;
        L_0x0a29:
            r0 = r12;
        L_0x0a2a:
            r1.onPostExecute(r0);
        L_0x0a2d:
            return;
        L_0x0a2e:
            r0 = move-exception;
            monitor-exit(r2);	 Catch:{ all -> 0x0a2e }
            goto L_0x0a32;
        L_0x0a31:
            throw r0;
        L_0x0a32:
            goto L_0x0a31;
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

    public static class MessageThumb {
        BitmapDrawable drawable;
        String key;

        public MessageThumb(String str, BitmapDrawable bitmapDrawable) {
            this.key = str;
            this.drawable = bitmapDrawable;
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

    public void putThumbsToCache(ArrayList<MessageThumb> arrayList) {
        for (int i = 0; i < arrayList.size(); i++) {
            putImageToCache(((MessageThumb) arrayList.get(i)).drawable, ((MessageThumb) arrayList.get(i)).key);
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
            -$$Lambda$ImageLoader$NAwQrHUBEJ5v2N5PPZ_I1OIh4Js -__lambda_imageloader_nawqrhubej5v2n5ppz_i1oih4js = r0;
            DispatchQueue dispatchQueue = this.imageLoadQueue;
            -$$Lambda$ImageLoader$NAwQrHUBEJ5v2N5PPZ_I1OIh4Js -__lambda_imageloader_nawqrhubej5v2n5ppz_i1oih4js2 = new -$$Lambda$ImageLoader$NAwQrHUBEJ5v2N5PPZ_I1OIh4Js(this, i4, str2, str, i7, imageReceiver, i5, str4, i3, imageLocation, z, parentObject, qulityThumbDocument, isNeedsQualityThumb, isShouldGenerateQualityThumb, i2, i, str3, currentAccount);
            dispatchQueue.postRunnable(-__lambda_imageloader_nawqrhubej5v2n5ppz_i1oih4js);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:68:0x018c  */
    /* JADX WARNING: Removed duplicated region for block: B:72:0x019a  */
    /* JADX WARNING: Removed duplicated region for block: B:85:0x01ed  */
    /* JADX WARNING: Removed duplicated region for block: B:74:0x019e  */
    /* JADX WARNING: Removed duplicated region for block: B:203:0x0420  */
    /* JADX WARNING: Removed duplicated region for block: B:202:0x0416  */
    /* JADX WARNING: Removed duplicated region for block: B:202:0x0416  */
    /* JADX WARNING: Removed duplicated region for block: B:203:0x0420  */
    /* JADX WARNING: Removed duplicated region for block: B:203:0x0420  */
    /* JADX WARNING: Removed duplicated region for block: B:202:0x0416  */
    /* JADX WARNING: Missing block: B:69:0x0195, code skipped:
            if (r8.exists() == false) goto L_0x0197;
     */
    public /* synthetic */ void lambda$createLoadOperationForImageReceiver$5$ImageLoader(int r22, java.lang.String r23, java.lang.String r24, int r25, org.telegram.messenger.ImageReceiver r26, int r27, java.lang.String r28, int r29, org.telegram.messenger.ImageLocation r30, boolean r31, java.lang.Object r32, org.telegram.tgnet.TLRPC.Document r33, boolean r34, boolean r35, int r36, int r37, java.lang.String r38, int r39) {
        /*
        r21 = this;
        r0 = r21;
        r1 = r22;
        r2 = r23;
        r9 = r24;
        r10 = r25;
        r11 = r26;
        r12 = r28;
        r13 = r30;
        r14 = r32;
        r15 = r33;
        r8 = r36;
        r7 = r37;
        r6 = 0;
        r5 = 2;
        if (r1 == r5) goto L_0x00a8;
    L_0x001c:
        r3 = r0.imageLoadingByUrl;
        r3 = r3.get(r2);
        r3 = (org.telegram.messenger.ImageLoader.CacheImage) r3;
        r4 = r0.imageLoadingByKeys;
        r4 = r4.get(r9);
        r4 = (org.telegram.messenger.ImageLoader.CacheImage) r4;
        r5 = r0.imageLoadingByTag;
        r5 = r5.get(r10);
        r5 = (org.telegram.messenger.ImageLoader.CacheImage) r5;
        if (r5 == 0) goto L_0x0075;
    L_0x0036:
        if (r5 != r4) goto L_0x0046;
    L_0x0038:
        r9 = r27;
        r5.setImageReceiverGuid(r11, r9);
        r17 = r3;
        r16 = r4;
        r3 = 1;
        r9 = 1;
        r18 = 0;
        goto L_0x007d;
    L_0x0046:
        r9 = r27;
        if (r5 != r3) goto L_0x006a;
    L_0x004a:
        r17 = r3;
        if (r4 != 0) goto L_0x0063;
    L_0x004e:
        r3 = r5;
        r16 = r4;
        r5 = 1;
        r4 = r26;
        r9 = 1;
        r5 = r24;
        r18 = 0;
        r6 = r28;
        r7 = r29;
        r8 = r27;
        r3.replaceImageReceiver(r4, r5, r6, r7, r8);
        goto L_0x0068;
    L_0x0063:
        r16 = r4;
        r9 = 1;
        r18 = 0;
    L_0x0068:
        r3 = 1;
        goto L_0x007d;
    L_0x006a:
        r17 = r3;
        r16 = r4;
        r9 = 1;
        r18 = 0;
        r5.removeImageReceiver(r11);
        goto L_0x007c;
    L_0x0075:
        r17 = r3;
        r16 = r4;
        r9 = 1;
        r18 = 0;
    L_0x007c:
        r3 = 0;
    L_0x007d:
        if (r3 != 0) goto L_0x0092;
    L_0x007f:
        if (r16 == 0) goto L_0x0092;
    L_0x0081:
        r3 = r16;
        r4 = r26;
        r5 = r24;
        r6 = r28;
        r7 = r29;
        r8 = r27;
        r3.addImageReceiver(r4, r5, r6, r7, r8);
        r6 = 1;
        goto L_0x0093;
    L_0x0092:
        r6 = r3;
    L_0x0093:
        if (r6 != 0) goto L_0x00ac;
    L_0x0095:
        if (r17 == 0) goto L_0x00ac;
    L_0x0097:
        r3 = r17;
        r4 = r26;
        r5 = r24;
        r6 = r28;
        r7 = r29;
        r8 = r27;
        r3.addImageReceiver(r4, r5, r6, r7, r8);
        r6 = 1;
        goto L_0x00ac;
    L_0x00a8:
        r9 = 1;
        r18 = 0;
        r6 = 0;
    L_0x00ac:
        if (r6 != 0) goto L_0x0584;
    L_0x00ae:
        r3 = r13.path;
        r4 = "_";
        r8 = "athumb";
        r7 = 4;
        if (r3 == 0) goto L_0x0111;
    L_0x00b7:
        r10 = "http";
        r10 = r3.startsWith(r10);
        if (r10 != 0) goto L_0x0109;
    L_0x00bf:
        r10 = r3.startsWith(r8);
        if (r10 != 0) goto L_0x0109;
    L_0x00c5:
        r10 = "thumb://";
        r10 = r3.startsWith(r10);
        r15 = ":";
        if (r10 == 0) goto L_0x00e5;
    L_0x00d0:
        r10 = 8;
        r10 = r3.indexOf(r15, r10);
        if (r10 < 0) goto L_0x00e3;
    L_0x00d8:
        r15 = new java.io.File;
        r10 = r10 + r9;
        r3 = r3.substring(r10);
        r15.<init>(r3);
        goto L_0x0107;
    L_0x00e3:
        r15 = 0;
        goto L_0x0107;
    L_0x00e5:
        r10 = "vthumb://";
        r10 = r3.startsWith(r10);
        if (r10 == 0) goto L_0x0101;
    L_0x00ee:
        r10 = 9;
        r10 = r3.indexOf(r15, r10);
        if (r10 < 0) goto L_0x00e3;
    L_0x00f6:
        r15 = new java.io.File;
        r10 = r10 + r9;
        r3 = r3.substring(r10);
        r15.<init>(r3);
        goto L_0x0107;
    L_0x0101:
        r10 = new java.io.File;
        r10.<init>(r3);
        r15 = r10;
    L_0x0107:
        r3 = 1;
        goto L_0x010b;
    L_0x0109:
        r3 = 0;
        r15 = 0;
    L_0x010b:
        r19 = r8;
        r5 = 2;
        r7 = 0;
        goto L_0x01fb;
    L_0x0111:
        if (r1 != 0) goto L_0x01f5;
    L_0x0113:
        if (r31 == 0) goto L_0x01f5;
    L_0x0115:
        r3 = r14 instanceof org.telegram.messenger.MessageObject;
        if (r3 == 0) goto L_0x012f;
    L_0x0119:
        r3 = r14;
        r3 = (org.telegram.messenger.MessageObject) r3;
        r15 = r3.getDocument();
        r5 = r3.messageOwner;
        r6 = r5.attachPath;
        r5 = org.telegram.messenger.FileLoader.getPathToMessage(r5);
        r3 = r3.getMediaType();
        r9 = r3;
        r3 = 0;
        goto L_0x0148;
    L_0x012f:
        if (r15 == 0) goto L_0x0143;
    L_0x0131:
        r6 = org.telegram.messenger.FileLoader.getPathToAttach(r15, r9);
        r3 = org.telegram.messenger.MessageObject.isVideoDocument(r33);
        if (r3 == 0) goto L_0x013d;
    L_0x013b:
        r3 = 2;
        goto L_0x013e;
    L_0x013d:
        r3 = 3;
    L_0x013e:
        r9 = r3;
        r5 = r6;
        r3 = 1;
        r6 = 0;
        goto L_0x0148;
    L_0x0143:
        r3 = 0;
        r5 = 0;
        r6 = 0;
        r9 = 0;
        r15 = 0;
    L_0x0148:
        if (r15 == 0) goto L_0x01f1;
    L_0x014a:
        r33 = r5;
        if (r34 == 0) goto L_0x0182;
    L_0x014e:
        r5 = new java.io.File;
        r19 = r8;
        r8 = org.telegram.messenger.FileLoader.getDirectory(r7);
        r7 = new java.lang.StringBuilder;
        r7.<init>();
        r14 = "q_";
        r7.append(r14);
        r14 = r15.dc_id;
        r7.append(r14);
        r7.append(r4);
        r13 = r15.id;
        r7.append(r13);
        r13 = ".jpg";
        r7.append(r13);
        r7 = r7.toString();
        r5.<init>(r8, r7);
        r7 = r5.exists();
        if (r7 != 0) goto L_0x0180;
    L_0x017f:
        goto L_0x0184;
    L_0x0180:
        r7 = 1;
        goto L_0x0186;
    L_0x0182:
        r19 = r8;
    L_0x0184:
        r5 = 0;
        r7 = 0;
    L_0x0186:
        r8 = android.text.TextUtils.isEmpty(r6);
        if (r8 != 0) goto L_0x0197;
    L_0x018c:
        r8 = new java.io.File;
        r8.<init>(r6);
        r6 = r8.exists();
        if (r6 != 0) goto L_0x0198;
    L_0x0197:
        r8 = 0;
    L_0x0198:
        if (r8 != 0) goto L_0x019c;
    L_0x019a:
        r8 = r33;
    L_0x019c:
        if (r5 != 0) goto L_0x01ed;
    L_0x019e:
        r1 = org.telegram.messenger.FileLoader.getAttachFileName(r15);
        r2 = r0.waitingForQualityThumb;
        r2 = r2.get(r1);
        r2 = (org.telegram.messenger.ImageLoader.ThumbGenerateInfo) r2;
        if (r2 != 0) goto L_0x01c0;
    L_0x01ac:
        r2 = new org.telegram.messenger.ImageLoader$ThumbGenerateInfo;
        r4 = 0;
        r2.<init>(r0, r4);
        r2.parentDocument = r15;
        r2.filter = r12;
        r2.big = r3;
        r3 = r0.waitingForQualityThumb;
        r3.put(r1, r2);
    L_0x01c0:
        r3 = r2.imageReceiverArray;
        r3 = r3.contains(r11);
        if (r3 != 0) goto L_0x01dc;
    L_0x01ca:
        r3 = r2.imageReceiverArray;
        r3.add(r11);
        r3 = r2.imageReceiverGuidsArray;
        r4 = java.lang.Integer.valueOf(r27);
        r3.add(r4);
    L_0x01dc:
        r3 = r0.waitingForQualityThumbByTag;
        r3.put(r10, r1);
        r1 = r8.exists();
        if (r1 == 0) goto L_0x01ec;
    L_0x01e7:
        if (r35 == 0) goto L_0x01ec;
    L_0x01e9:
        r0.generateThumb(r9, r8, r2);
    L_0x01ec:
        return;
    L_0x01ed:
        r15 = r5;
        r3 = 1;
        r5 = 2;
        goto L_0x01fb;
    L_0x01f1:
        r19 = r8;
        r3 = 1;
        goto L_0x01f8;
    L_0x01f5:
        r19 = r8;
        r3 = 0;
    L_0x01f8:
        r5 = 2;
        r7 = 0;
        r15 = 0;
    L_0x01fb:
        if (r1 == r5) goto L_0x0584;
    L_0x01fd:
        r5 = r30.isEncrypted();
        r9 = new org.telegram.messenger.ImageLoader$CacheImage;
        r6 = 0;
        r9.<init>(r0, r6);
        r10 = r30;
        if (r31 != 0) goto L_0x0257;
    L_0x020b:
        r6 = r10.webFile;
        r6 = org.telegram.messenger.MessageObject.isGifDocument(r6);
        if (r6 != 0) goto L_0x0254;
    L_0x0213:
        r6 = r10.document;
        r6 = org.telegram.messenger.MessageObject.isGifDocument(r6);
        if (r6 != 0) goto L_0x0254;
    L_0x021b:
        r6 = r10.document;
        r6 = org.telegram.messenger.MessageObject.isRoundVideoDocument(r6);
        if (r6 == 0) goto L_0x0224;
    L_0x0223:
        goto L_0x0254;
    L_0x0224:
        r6 = r10.path;
        if (r6 == 0) goto L_0x0257;
    L_0x0228:
        r8 = "vthumb";
        r8 = r6.startsWith(r8);
        if (r8 != 0) goto L_0x0257;
    L_0x0231:
        r8 = "thumb";
        r8 = r6.startsWith(r8);
        if (r8 != 0) goto L_0x0257;
    L_0x023a:
        r8 = "jpg";
        r6 = getHttpUrlExtension(r6, r8);
        r8 = "mp4";
        r8 = r6.equals(r8);
        if (r8 != 0) goto L_0x0250;
    L_0x0248:
        r8 = "gif";
        r6 = r6.equals(r8);
        if (r6 == 0) goto L_0x0257;
    L_0x0250:
        r6 = 2;
        r9.imageType = r6;
        goto L_0x0257;
    L_0x0254:
        r6 = 2;
        r9.imageType = r6;
    L_0x0257:
        if (r15 != 0) goto L_0x0425;
    L_0x0259:
        r6 = r10.photoSize;
        r6 = r6 instanceof org.telegram.tgnet.TLRPC.TL_photoStrippedSize;
        r8 = "g";
        if (r6 == 0) goto L_0x026b;
    L_0x0261:
        r6 = r36;
        r5 = r7;
        r11 = r12;
        r1 = 1;
        r3 = 0;
        r4 = 1;
    L_0x0268:
        r7 = 4;
        goto L_0x0410;
    L_0x026b:
        r6 = r10.secureDocument;
        if (r6 == 0) goto L_0x0290;
    L_0x026f:
        r9.secureDocument = r6;
        r3 = r9.secureDocument;
        r3 = r3.secureFile;
        r3 = r3.dc_id;
        r4 = -NUM; // 0xfffffffvar_ float:-0.0 double:NaN;
        if (r3 != r4) goto L_0x027d;
    L_0x027b:
        r4 = 1;
        goto L_0x027e;
    L_0x027d:
        r4 = 0;
    L_0x027e:
        r3 = new java.io.File;
        r5 = 4;
        r6 = org.telegram.messenger.FileLoader.getDirectory(r5);
        r3.<init>(r6, r2);
        r6 = r36;
        r15 = r3;
        r5 = r7;
        r11 = r12;
        r1 = 1;
        r3 = 0;
        goto L_0x0268;
    L_0x0290:
        r6 = r8.equals(r12);
        r13 = ".svg";
        r15 = "application/x-tgwallpattern";
        r14 = "application/x-tgsticker";
        if (r6 != 0) goto L_0x033a;
    L_0x029c:
        r6 = r36;
        r11 = r37;
        r33 = r3;
        if (r6 != 0) goto L_0x02ac;
    L_0x02a4:
        if (r11 <= 0) goto L_0x02ac;
    L_0x02a6:
        r3 = r10.path;
        if (r3 != 0) goto L_0x02ac;
    L_0x02aa:
        if (r5 == 0) goto L_0x0340;
    L_0x02ac:
        r3 = new java.io.File;
        r4 = 4;
        r5 = org.telegram.messenger.FileLoader.getDirectory(r4);
        r3.<init>(r5, r2);
        r5 = r3.exists();
        if (r5 == 0) goto L_0x02be;
    L_0x02bc:
        r4 = 1;
        goto L_0x02e2;
    L_0x02be:
        r5 = 2;
        if (r6 != r5) goto L_0x02de;
    L_0x02c1:
        r3 = new java.io.File;
        r5 = org.telegram.messenger.FileLoader.getDirectory(r4);
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r4.append(r2);
        r34 = r7;
        r7 = ".enc";
        r4.append(r7);
        r4 = r4.toString();
        r3.<init>(r5, r4);
        goto L_0x02e0;
    L_0x02de:
        r34 = r7;
    L_0x02e0:
        r4 = r34;
    L_0x02e2:
        r5 = r10.document;
        if (r5 == 0) goto L_0x0330;
    L_0x02e6:
        r7 = r5 instanceof org.telegram.messenger.DocumentObject.ThemeDocument;
        if (r7 == 0) goto L_0x0303;
    L_0x02ea:
        r5 = (org.telegram.messenger.DocumentObject.ThemeDocument) r5;
        r5 = r5.wallpaper;
        if (r5 != 0) goto L_0x02f2;
    L_0x02f0:
        r5 = 1;
        goto L_0x02f4;
    L_0x02f2:
        r5 = r33;
    L_0x02f4:
        r7 = 5;
        r9.imageType = r7;
        r15 = r3;
        r11 = r12;
        r1 = 1;
        r3 = 0;
        r7 = 4;
        r20 = r5;
        r5 = r4;
        r4 = r20;
        goto L_0x0410;
    L_0x0303:
        r5 = r5.mime_type;
        r5 = r14.equals(r5);
        if (r5 == 0) goto L_0x030f;
    L_0x030b:
        r5 = 1;
        r9.imageType = r5;
        goto L_0x0330;
    L_0x030f:
        r5 = r10.document;
        r5 = r5.mime_type;
        r5 = r15.equals(r5);
        if (r5 == 0) goto L_0x031d;
    L_0x0319:
        r5 = 3;
        r9.imageType = r5;
        goto L_0x0330;
    L_0x031d:
        r5 = org.telegram.messenger.BuildVars.DEBUG_PRIVATE_VERSION;
        if (r5 == 0) goto L_0x0330;
    L_0x0321:
        r5 = r10.document;
        r5 = org.telegram.messenger.FileLoader.getDocumentFileName(r5);
        r5 = r5.endsWith(r13);
        if (r5 == 0) goto L_0x0330;
    L_0x032d:
        r5 = 4;
        r9.imageType = r5;
    L_0x0330:
        r15 = r3;
        r5 = r4;
        r11 = r12;
        r1 = 1;
        r3 = 0;
        r7 = 4;
        r4 = r33;
        goto L_0x0410;
    L_0x033a:
        r6 = r36;
        r11 = r37;
        r33 = r3;
    L_0x0340:
        r34 = r7;
        r3 = r10.document;
        if (r3 == 0) goto L_0x03ee;
    L_0x0346:
        r5 = r3 instanceof org.telegram.tgnet.TLRPC.TL_documentEncrypted;
        if (r5 == 0) goto L_0x0355;
    L_0x034a:
        r5 = new java.io.File;
        r7 = 4;
        r1 = org.telegram.messenger.FileLoader.getDirectory(r7);
        r5.<init>(r1, r2);
        goto L_0x0370;
    L_0x0355:
        r1 = org.telegram.messenger.MessageObject.isVideoDocument(r3);
        if (r1 == 0) goto L_0x0366;
    L_0x035b:
        r5 = new java.io.File;
        r1 = 2;
        r7 = org.telegram.messenger.FileLoader.getDirectory(r1);
        r5.<init>(r7, r2);
        goto L_0x0370;
    L_0x0366:
        r5 = new java.io.File;
        r1 = 3;
        r7 = org.telegram.messenger.FileLoader.getDirectory(r1);
        r5.<init>(r7, r2);
    L_0x0370:
        r1 = r8.equals(r12);
        if (r1 == 0) goto L_0x03a1;
    L_0x0376:
        r1 = r5.exists();
        if (r1 != 0) goto L_0x03a1;
    L_0x037c:
        r5 = new java.io.File;
        r1 = 4;
        r7 = org.telegram.messenger.FileLoader.getDirectory(r1);
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r11 = r3.dc_id;
        r1.append(r11);
        r1.append(r4);
        r11 = r3.id;
        r1.append(r11);
        r4 = ".temp";
        r1.append(r4);
        r1 = r1.toString();
        r5.<init>(r7, r1);
    L_0x03a1:
        r1 = r3 instanceof org.telegram.messenger.DocumentObject.ThemeDocument;
        if (r1 == 0) goto L_0x03b7;
    L_0x03a5:
        r1 = r3;
        r1 = (org.telegram.messenger.DocumentObject.ThemeDocument) r1;
        r1 = r1.wallpaper;
        if (r1 != 0) goto L_0x03ae;
    L_0x03ac:
        r1 = 1;
        goto L_0x03b0;
    L_0x03ae:
        r1 = r33;
    L_0x03b0:
        r4 = 5;
        r9.imageType = r4;
        r4 = r1;
        r1 = 1;
        r7 = 4;
        goto L_0x03e6;
    L_0x03b7:
        r1 = r3.mime_type;
        r1 = r14.equals(r1);
        if (r1 == 0) goto L_0x03c4;
    L_0x03bf:
        r1 = 1;
        r9.imageType = r1;
    L_0x03c2:
        r7 = 4;
        goto L_0x03e4;
    L_0x03c4:
        r1 = 1;
        r4 = r3.mime_type;
        r4 = r15.equals(r4);
        if (r4 == 0) goto L_0x03d1;
    L_0x03cd:
        r4 = 3;
        r9.imageType = r4;
        goto L_0x03c2;
    L_0x03d1:
        r4 = org.telegram.messenger.BuildVars.DEBUG_PRIVATE_VERSION;
        if (r4 == 0) goto L_0x03c2;
    L_0x03d5:
        r4 = r10.document;
        r4 = org.telegram.messenger.FileLoader.getDocumentFileName(r4);
        r4 = r4.endsWith(r13);
        if (r4 == 0) goto L_0x03c2;
    L_0x03e1:
        r7 = 4;
        r9.imageType = r7;
    L_0x03e4:
        r4 = r33;
    L_0x03e6:
        r3 = r3.size;
        r11 = r28;
        r15 = r5;
        r5 = r34;
        goto L_0x0410;
    L_0x03ee:
        r1 = 1;
        r7 = 4;
        r3 = r10.webFile;
        if (r3 == 0) goto L_0x03ff;
    L_0x03f4:
        r3 = new java.io.File;
        r4 = 3;
        r4 = org.telegram.messenger.FileLoader.getDirectory(r4);
        r3.<init>(r4, r2);
        goto L_0x0408;
    L_0x03ff:
        r3 = new java.io.File;
        r4 = org.telegram.messenger.FileLoader.getDirectory(r18);
        r3.<init>(r4, r2);
    L_0x0408:
        r11 = r28;
        r4 = r33;
        r5 = r34;
        r15 = r3;
        r3 = 0;
    L_0x0410:
        r8 = r8.equals(r11);
        if (r8 == 0) goto L_0x0420;
    L_0x0416:
        r8 = 2;
        r9.imageType = r8;
        r9.size = r3;
        r8 = r29;
        r13 = r5;
        r12 = 1;
        goto L_0x0434;
    L_0x0420:
        r8 = r29;
        r12 = r4;
        r13 = r5;
        goto L_0x0434;
    L_0x0425:
        r6 = r36;
        r33 = r3;
        r34 = r7;
        r11 = r12;
        r1 = 1;
        r7 = 4;
        r8 = r29;
        r12 = r33;
        r13 = r34;
    L_0x0434:
        r9.type = r8;
        r14 = r24;
        r9.key = r14;
        r9.filter = r11;
        r9.imageLocation = r10;
        r5 = r38;
        r9.ext = r5;
        r4 = r39;
        r9.currentAccount = r4;
        r3 = r32;
        r9.parentObject = r3;
        r1 = r10.imageType;
        if (r1 == 0) goto L_0x0450;
    L_0x044e:
        r9.imageType = r1;
    L_0x0450:
        r1 = 2;
        if (r6 != r1) goto L_0x046f;
    L_0x0453:
        r1 = new java.io.File;
        r7 = org.telegram.messenger.FileLoader.getInternalCacheDir();
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r3.append(r2);
        r4 = ".enc.key";
        r3.append(r4);
        r3 = r3.toString();
        r1.<init>(r7, r3);
        r9.encryptionKeyPath = r1;
    L_0x046f:
        r1 = r32;
        r3 = r9;
        r4 = r26;
        r5 = r24;
        r7 = r6;
        r6 = r28;
        r11 = r7;
        r17 = 4;
        r7 = r29;
        r1 = r19;
        r8 = r27;
        r3.addImageReceiver(r4, r5, r6, r7, r8);
        if (r12 != 0) goto L_0x0563;
    L_0x0487:
        if (r13 != 0) goto L_0x0563;
    L_0x0489:
        r3 = r15.exists();
        if (r3 == 0) goto L_0x0491;
    L_0x048f:
        goto L_0x0563;
    L_0x0491:
        r9.url = r2;
        r3 = r0.imageLoadingByUrl;
        r3.put(r2, r9);
        r2 = r10.path;
        if (r2 == 0) goto L_0x04f0;
    L_0x049c:
        r2 = org.telegram.messenger.Utilities.MD5(r2);
        r3 = org.telegram.messenger.FileLoader.getDirectory(r17);
        r4 = new java.io.File;
        r5 = new java.lang.StringBuilder;
        r5.<init>();
        r5.append(r2);
        r2 = "_temp.jpg";
        r5.append(r2);
        r2 = r5.toString();
        r4.<init>(r3, r2);
        r9.tempFilePath = r4;
        r9.finalFilePath = r15;
        r2 = r10.path;
        r1 = r2.startsWith(r1);
        if (r1 == 0) goto L_0x04da;
    L_0x04c6:
        r1 = new org.telegram.messenger.ImageLoader$ArtworkLoadTask;
        r1.<init>(r9);
        r9.artworkTask = r1;
        r1 = r0.artworkTasks;
        r2 = r9.artworkTask;
        r1.add(r2);
        r7 = 0;
        r0.runArtworkTasks(r7);
        goto L_0x0584;
    L_0x04da:
        r7 = 0;
        r1 = new org.telegram.messenger.ImageLoader$HttpImageTask;
        r2 = r37;
        r1.<init>(r9, r2);
        r9.httpTask = r1;
        r1 = r0.httpTasks;
        r2 = r9.httpTask;
        r1.add(r2);
        r0.runHttpTasks(r7);
        goto L_0x0584;
    L_0x04f0:
        r2 = r37;
        r7 = 0;
        r1 = r10.location;
        if (r1 == 0) goto L_0x0517;
    L_0x04f7:
        if (r11 != 0) goto L_0x0501;
    L_0x04f9:
        if (r2 <= 0) goto L_0x04ff;
    L_0x04fb:
        r1 = r10.key;
        if (r1 == 0) goto L_0x0501;
    L_0x04ff:
        r6 = 1;
        goto L_0x0502;
    L_0x0501:
        r6 = r11;
    L_0x0502:
        r1 = org.telegram.messenger.FileLoader.getInstance(r39);
        r3 = r32;
        if (r22 == 0) goto L_0x050c;
    L_0x050a:
        r5 = 2;
        goto L_0x050d;
    L_0x050c:
        r5 = 1;
    L_0x050d:
        r2 = r30;
        r3 = r32;
        r4 = r38;
        r1.loadFile(r2, r3, r4, r5, r6);
        goto L_0x0551;
    L_0x0517:
        r3 = r32;
        r1 = r10.document;
        if (r1 == 0) goto L_0x052c;
    L_0x051d:
        r1 = org.telegram.messenger.FileLoader.getInstance(r39);
        r2 = r10.document;
        if (r22 == 0) goto L_0x0527;
    L_0x0525:
        r4 = 2;
        goto L_0x0528;
    L_0x0527:
        r4 = 1;
    L_0x0528:
        r1.loadFile(r2, r3, r4, r11);
        goto L_0x0551;
    L_0x052c:
        r1 = r10.secureDocument;
        if (r1 == 0) goto L_0x053f;
    L_0x0530:
        r1 = org.telegram.messenger.FileLoader.getInstance(r39);
        r2 = r10.secureDocument;
        if (r22 == 0) goto L_0x053a;
    L_0x0538:
        r3 = 2;
        goto L_0x053b;
    L_0x053a:
        r3 = 1;
    L_0x053b:
        r1.loadFile(r2, r3);
        goto L_0x0551;
    L_0x053f:
        r1 = r10.webFile;
        if (r1 == 0) goto L_0x0551;
    L_0x0543:
        r1 = org.telegram.messenger.FileLoader.getInstance(r39);
        r2 = r10.webFile;
        if (r22 == 0) goto L_0x054d;
    L_0x054b:
        r3 = 2;
        goto L_0x054e;
    L_0x054d:
        r3 = 1;
    L_0x054e:
        r1.loadFile(r2, r3, r11);
    L_0x0551:
        r1 = r26.isForceLoding();
        if (r1 == 0) goto L_0x0584;
    L_0x0557:
        r1 = r0.forceLoadingImages;
        r2 = r9.key;
        r3 = java.lang.Integer.valueOf(r7);
        r1.put(r2, r3);
        goto L_0x0584;
    L_0x0563:
        r9.finalFilePath = r15;
        r9.imageLocation = r10;
        r1 = new org.telegram.messenger.ImageLoader$CacheOutTask;
        r1.<init>(r9);
        r9.cacheTask = r1;
        r1 = r0.imageLoadingByKeys;
        r1.put(r14, r9);
        if (r22 == 0) goto L_0x057d;
    L_0x0575:
        r1 = r0.cacheThumbOutQueue;
        r2 = r9.cacheTask;
        r1.postRunnable(r2);
        goto L_0x0584;
    L_0x057d:
        r1 = r0.cacheOutQueue;
        r2 = r9.cacheTask;
        r1.postRunnable(r2);
    L_0x0584:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ImageLoader.lambda$createLoadOperationForImageReceiver$5$ImageLoader(int, java.lang.String, java.lang.String, int, org.telegram.messenger.ImageReceiver, int, java.lang.String, int, org.telegram.messenger.ImageLocation, boolean, java.lang.Object, org.telegram.tgnet.TLRPC$Document, boolean, boolean, int, int, java.lang.String, int):void");
    }

    /* JADX WARNING: Removed duplicated region for block: B:144:0x02a0  */
    /* JADX WARNING: Removed duplicated region for block: B:138:0x0280  */
    /* JADX WARNING: Removed duplicated region for block: B:158:0x02da  */
    /* JADX WARNING: Removed duplicated region for block: B:157:0x02d7  */
    /* JADX WARNING: Removed duplicated region for block: B:163:0x02f4  */
    /* JADX WARNING: Removed duplicated region for block: B:160:0x02e2  */
    /* JADX WARNING: Removed duplicated region for block: B:78:0x013a  */
    /* JADX WARNING: Removed duplicated region for block: B:238:0x0306 A:{SYNTHETIC, EDGE_INSN: B:238:0x0306->B:165:0x0306 ?: BREAK  , EDGE_INSN: B:238:0x0306->B:165:0x0306 ?: BREAK  } */
    /* JADX WARNING: Removed duplicated region for block: B:82:0x014f  */
    /* JADX WARNING: Removed duplicated region for block: B:182:0x0374  */
    /* JADX WARNING: Removed duplicated region for block: B:167:0x030e  */
    /* JADX WARNING: Removed duplicated region for block: B:224:0x0469  */
    /* JADX WARNING: Removed duplicated region for block: B:206:0x0400  */
    /* JADX WARNING: Removed duplicated region for block: B:73:0x012a  */
    /* JADX WARNING: Removed duplicated region for block: B:71:0x011d  */
    /* JADX WARNING: Removed duplicated region for block: B:78:0x013a  */
    /* JADX WARNING: Removed duplicated region for block: B:82:0x014f  */
    /* JADX WARNING: Removed duplicated region for block: B:238:0x0306 A:{SYNTHETIC, EDGE_INSN: B:238:0x0306->B:165:0x0306 ?: BREAK  , EDGE_INSN: B:238:0x0306->B:165:0x0306 ?: BREAK  , EDGE_INSN: B:238:0x0306->B:165:0x0306 ?: BREAK  } */
    /* JADX WARNING: Removed duplicated region for block: B:167:0x030e  */
    /* JADX WARNING: Removed duplicated region for block: B:182:0x0374  */
    /* JADX WARNING: Removed duplicated region for block: B:197:0x03c5  */
    /* JADX WARNING: Removed duplicated region for block: B:206:0x0400  */
    /* JADX WARNING: Removed duplicated region for block: B:224:0x0469  */
    /* JADX WARNING: Removed duplicated region for block: B:44:0x00a7  */
    /* JADX WARNING: Removed duplicated region for block: B:71:0x011d  */
    /* JADX WARNING: Removed duplicated region for block: B:73:0x012a  */
    /* JADX WARNING: Removed duplicated region for block: B:78:0x013a  */
    /* JADX WARNING: Removed duplicated region for block: B:238:0x0306 A:{SYNTHETIC, EDGE_INSN: B:238:0x0306->B:165:0x0306 ?: BREAK  , EDGE_INSN: B:238:0x0306->B:165:0x0306 ?: BREAK  , EDGE_INSN: B:238:0x0306->B:165:0x0306 ?: BREAK  , EDGE_INSN: B:238:0x0306->B:165:0x0306 ?: BREAK  } */
    /* JADX WARNING: Removed duplicated region for block: B:82:0x014f  */
    /* JADX WARNING: Removed duplicated region for block: B:182:0x0374  */
    /* JADX WARNING: Removed duplicated region for block: B:167:0x030e  */
    /* JADX WARNING: Removed duplicated region for block: B:197:0x03c5  */
    /* JADX WARNING: Removed duplicated region for block: B:224:0x0469  */
    /* JADX WARNING: Removed duplicated region for block: B:206:0x0400  */
    /* JADX WARNING: Removed duplicated region for block: B:34:0x0083  */
    /* JADX WARNING: Removed duplicated region for block: B:36:0x008a  */
    /* JADX WARNING: Removed duplicated region for block: B:44:0x00a7  */
    /* JADX WARNING: Removed duplicated region for block: B:73:0x012a  */
    /* JADX WARNING: Removed duplicated region for block: B:71:0x011d  */
    /* JADX WARNING: Removed duplicated region for block: B:78:0x013a  */
    /* JADX WARNING: Removed duplicated region for block: B:82:0x014f  */
    /* JADX WARNING: Removed duplicated region for block: B:238:0x0306 A:{SYNTHETIC, EDGE_INSN: B:238:0x0306->B:165:0x0306 ?: BREAK  , EDGE_INSN: B:238:0x0306->B:165:0x0306 ?: BREAK  , EDGE_INSN: B:238:0x0306->B:165:0x0306 ?: BREAK  , EDGE_INSN: B:238:0x0306->B:165:0x0306 ?: BREAK  , EDGE_INSN: B:238:0x0306->B:165:0x0306 ?: BREAK  } */
    /* JADX WARNING: Removed duplicated region for block: B:167:0x030e  */
    /* JADX WARNING: Removed duplicated region for block: B:182:0x0374  */
    /* JADX WARNING: Removed duplicated region for block: B:197:0x03c5  */
    /* JADX WARNING: Removed duplicated region for block: B:206:0x0400  */
    /* JADX WARNING: Removed duplicated region for block: B:224:0x0469  */
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
        r0 = r0.imageType;
        if (r0 != r8) goto L_0x002e;
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
        r1 = r1.imageType;
        if (r1 != r8) goto L_0x0079;
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
        r0 = r0.imageType;
        if (r0 != r8) goto L_0x00c2;
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
        r8 = "jpg";
        if (r9 != 0) goto L_0x013b;
    L_0x013a:
        r9 = r8;
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
        r7 = 2;
        r12 = ".";
        if (r3 >= r7) goto L_0x0306;
    L_0x014f:
        if (r3 != 0) goto L_0x0153;
    L_0x0151:
        r7 = r4;
        goto L_0x0155;
    L_0x0153:
        r7 = r18;
    L_0x0155:
        if (r7 != 0) goto L_0x015a;
    L_0x0157:
        r24 = r15;
        goto L_0x016b;
    L_0x015a:
        if (r18 == 0) goto L_0x0161;
    L_0x015c:
        r24 = r15;
        r13 = r18;
        goto L_0x0164;
    L_0x0161:
        r13 = r4;
        r24 = r15;
    L_0x0164:
        r15 = 0;
        r13 = r7.getKey(r1, r13, r15);
        if (r13 != 0) goto L_0x0173;
    L_0x016b:
        r26 = r0;
        r27 = r2;
        r25 = r14;
        goto L_0x02f6;
    L_0x0173:
        if (r18 == 0) goto L_0x017a;
    L_0x0175:
        r25 = r14;
        r15 = r18;
        goto L_0x017d;
    L_0x017a:
        r15 = r4;
        r25 = r14;
    L_0x017d:
        r14 = 1;
        r15 = r7.getKey(r1, r15, r14);
        r14 = r7.path;
        if (r14 == 0) goto L_0x01a4;
    L_0x0186:
        r14 = new java.lang.StringBuilder;
        r14.<init>();
        r14.append(r15);
        r14.append(r12);
        r12 = r7.path;
        r12 = getHttpUrlExtension(r12, r8);
        r14.append(r12);
        r15 = r14.toString();
    L_0x019e:
        r26 = r0;
    L_0x01a0:
        r27 = r2;
        goto L_0x02d5;
    L_0x01a4:
        r14 = r7.photoSize;
        r14 = r14 instanceof org.telegram.tgnet.TLRPC.TL_photoStrippedSize;
        if (r14 == 0) goto L_0x01bd;
    L_0x01aa:
        r14 = new java.lang.StringBuilder;
        r14.<init>();
        r14.append(r15);
        r14.append(r12);
        r14.append(r9);
        r15 = r14.toString();
        goto L_0x019e;
    L_0x01bd:
        r14 = r7.location;
        if (r14 == 0) goto L_0x0200;
    L_0x01c1:
        r14 = new java.lang.StringBuilder;
        r14.<init>();
        r14.append(r15);
        r14.append(r12);
        r14.append(r9);
        r15 = r14.toString();
        r12 = r31.getExt();
        if (r12 != 0) goto L_0x01f4;
    L_0x01d9:
        r12 = r7.location;
        r14 = r12.key;
        if (r14 != 0) goto L_0x01f4;
    L_0x01df:
        r26 = r15;
        r14 = r12.volume_id;
        r27 = -NUM; // 0xfffffffvar_ float:-0.0 double:NaN;
        r29 = (r14 > r27 ? 1 : (r14 == r27 ? 0 : -1));
        if (r29 != 0) goto L_0x01ef;
    L_0x01ea:
        r12 = r12.local_id;
        if (r12 >= 0) goto L_0x01ef;
    L_0x01ee:
        goto L_0x01f6;
    L_0x01ef:
        r27 = r2;
        r15 = r26;
        goto L_0x01fc;
    L_0x01f4:
        r26 = r15;
    L_0x01f6:
        r27 = r2;
        r15 = r26;
        r21 = 1;
    L_0x01fc:
        r26 = r0;
        goto L_0x02d5;
    L_0x0200:
        r14 = r7.webFile;
        if (r14 == 0) goto L_0x0228;
    L_0x0204:
        r14 = r14.mime_type;
        r14 = org.telegram.messenger.FileLoader.getMimeTypePart(r14);
        r26 = r0;
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r0.append(r15);
        r0.append(r12);
        r12 = r7.webFile;
        r12 = r12.url;
        r12 = getHttpUrlExtension(r12, r14);
        r0.append(r12);
        r15 = r0.toString();
        goto L_0x01a0;
    L_0x0228:
        r26 = r0;
        r0 = r7.secureDocument;
        if (r0 == 0) goto L_0x0242;
    L_0x022e:
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r0.append(r15);
        r0.append(r12);
        r0.append(r9);
        r15 = r0.toString();
        goto L_0x01a0;
    L_0x0242:
        r0 = r7.document;
        if (r0 == 0) goto L_0x01a0;
    L_0x0246:
        if (r3 != 0) goto L_0x025b;
    L_0x0248:
        if (r2 == 0) goto L_0x025b;
    L_0x024a:
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r12 = "q_";
        r0.append(r12);
        r0.append(r13);
        r13 = r0.toString();
    L_0x025b:
        r0 = r7.document;
        r0 = org.telegram.messenger.FileLoader.getDocumentFileName(r0);
        r12 = "";
        if (r0 == 0) goto L_0x0276;
    L_0x0265:
        r14 = 46;
        r14 = r0.lastIndexOf(r14);
        r27 = r2;
        r2 = -1;
        if (r14 != r2) goto L_0x0271;
    L_0x0270:
        goto L_0x0278;
    L_0x0271:
        r0 = r0.substring(r14);
        goto L_0x0279;
    L_0x0276:
        r27 = r2;
    L_0x0278:
        r0 = r12;
    L_0x0279:
        r2 = r0.length();
        r14 = 1;
        if (r2 > r14) goto L_0x02a0;
    L_0x0280:
        r0 = r7.document;
        r0 = r0.mime_type;
        r2 = "video/mp4";
        r0 = r2.equals(r0);
        if (r0 == 0) goto L_0x0290;
    L_0x028d:
        r12 = ".mp4";
        goto L_0x02a1;
    L_0x0290:
        r0 = r7.document;
        r0 = r0.mime_type;
        r2 = "video/x-matroska";
        r0 = r2.equals(r0);
        if (r0 == 0) goto L_0x02a1;
    L_0x029d:
        r12 = ".mkv";
        goto L_0x02a1;
    L_0x02a0:
        r12 = r0;
    L_0x02a1:
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r0.append(r15);
        r0.append(r12);
        r15 = r0.toString();
        r0 = r7.document;
        r0 = org.telegram.messenger.MessageObject.isVideoDocument(r0);
        if (r0 != 0) goto L_0x02d2;
    L_0x02b8:
        r0 = r7.document;
        r0 = org.telegram.messenger.MessageObject.isGifDocument(r0);
        if (r0 != 0) goto L_0x02d2;
    L_0x02c0:
        r0 = r7.document;
        r0 = org.telegram.messenger.MessageObject.isRoundVideoDocument(r0);
        if (r0 != 0) goto L_0x02d2;
    L_0x02c8:
        r0 = r7.document;
        r0 = org.telegram.messenger.MessageObject.canPreviewDocument(r0);
        if (r0 != 0) goto L_0x02d2;
    L_0x02d0:
        r0 = 1;
        goto L_0x02d3;
    L_0x02d2:
        r0 = 0;
    L_0x02d3:
        r21 = r0;
    L_0x02d5:
        if (r3 != 0) goto L_0x02da;
    L_0x02d7:
        r20 = r13;
        goto L_0x02e0;
    L_0x02da:
        r19 = r13;
        r23 = r15;
        r15 = r22;
    L_0x02e0:
        if (r7 != r5) goto L_0x02f4;
    L_0x02e2:
        if (r3 != 0) goto L_0x02eb;
    L_0x02e4:
        r4 = r17;
        r20 = r4;
        r22 = r20;
        goto L_0x02f6;
    L_0x02eb:
        r22 = r15;
        r18 = r17;
        r19 = r18;
        r23 = r19;
        goto L_0x02f6;
    L_0x02f4:
        r22 = r15;
    L_0x02f6:
        r3 = r3 + 1;
        r12 = r30;
        r13 = r31;
        r15 = r24;
        r14 = r25;
        r0 = r26;
        r2 = r27;
        goto L_0x014a;
    L_0x0306:
        r26 = r0;
        r25 = r14;
        r24 = r15;
        if (r5 == 0) goto L_0x0374;
    L_0x030e:
        r0 = r31.getStrippedLocation();
        if (r0 != 0) goto L_0x031a;
    L_0x0314:
        if (r18 == 0) goto L_0x0319;
    L_0x0316:
        r0 = r18;
        goto L_0x031a;
    L_0x0319:
        r0 = r4;
    L_0x031a:
        r2 = 0;
        r17 = r5.getKey(r1, r0, r2);
        r2 = 1;
        r0 = r5.getKey(r1, r0, r2);
        r1 = r5.path;
        if (r1 == 0) goto L_0x0344;
    L_0x0328:
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r1.append(r0);
        r1.append(r12);
        r0 = r5.path;
        r0 = getHttpUrlExtension(r0, r8);
        r1.append(r0);
        r0 = r1.toString();
    L_0x0340:
        r3 = r0;
        r0 = r17;
        goto L_0x0378;
    L_0x0344:
        r1 = r5.photoSize;
        r1 = r1 instanceof org.telegram.tgnet.TLRPC.TL_photoStrippedSize;
        if (r1 == 0) goto L_0x035d;
    L_0x034a:
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r1.append(r0);
        r1.append(r12);
        r1.append(r9);
        r0 = r1.toString();
        goto L_0x0340;
    L_0x035d:
        r1 = r5.location;
        if (r1 == 0) goto L_0x0340;
    L_0x0361:
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r1.append(r0);
        r1.append(r12);
        r1.append(r9);
        r0 = r1.toString();
        goto L_0x0340;
    L_0x0374:
        r2 = 1;
        r0 = r17;
        r3 = r0;
    L_0x0378:
        r1 = "@";
        r8 = r19;
        if (r8 == 0) goto L_0x0392;
    L_0x037e:
        if (r11 == 0) goto L_0x0392;
    L_0x0380:
        r12 = new java.lang.StringBuilder;
        r12.<init>();
        r12.append(r8);
        r12.append(r1);
        r12.append(r11);
        r8 = r12.toString();
    L_0x0392:
        r12 = r8;
        r8 = r20;
        if (r8 == 0) goto L_0x03ab;
    L_0x0397:
        if (r10 == 0) goto L_0x03ab;
    L_0x0399:
        r13 = new java.lang.StringBuilder;
        r13.<init>();
        r13.append(r8);
        r13.append(r1);
        r13.append(r10);
        r8 = r13.toString();
    L_0x03ab:
        r13 = r8;
        if (r0 == 0) goto L_0x03c2;
    L_0x03ae:
        if (r6 == 0) goto L_0x03c2;
    L_0x03b0:
        r8 = new java.lang.StringBuilder;
        r8.<init>();
        r8.append(r0);
        r8.append(r1);
        r8.append(r6);
        r0 = r8.toString();
    L_0x03c2:
        r8 = r0;
        if (r4 == 0) goto L_0x03f9;
    L_0x03c5:
        r0 = r4.path;
        if (r0 == 0) goto L_0x03f9;
    L_0x03c9:
        r11 = 0;
        r12 = 1;
        r14 = 1;
        if (r26 == 0) goto L_0x03d0;
    L_0x03ce:
        r15 = 2;
        goto L_0x03d1;
    L_0x03d0:
        r15 = 1;
    L_0x03d1:
        r0 = r30;
        r1 = r31;
        r2 = r8;
        r16 = r4;
        r4 = r9;
        r7 = r11;
        r8 = r12;
        r17 = r9;
        r9 = r14;
        r14 = r10;
        r10 = r15;
        r11 = r25;
        r0.createLoadOperationForImageReceiver(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r11);
        r7 = r31.getSize();
        r8 = 1;
        r9 = 0;
        r10 = 0;
        r2 = r13;
        r3 = r22;
        r4 = r17;
        r5 = r16;
        r6 = r14;
        r0.createLoadOperationForImageReceiver(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r11);
        goto L_0x04a0;
    L_0x03f9:
        r16 = r4;
        r17 = r9;
        r14 = r10;
        if (r18 == 0) goto L_0x0469;
    L_0x0400:
        r0 = r31.getCacheType();
        r15 = 1;
        if (r0 != 0) goto L_0x040c;
    L_0x0407:
        if (r21 == 0) goto L_0x040c;
    L_0x0409:
        r19 = 1;
        goto L_0x040e;
    L_0x040c:
        r19 = r0;
    L_0x040e:
        if (r19 != 0) goto L_0x0412;
    L_0x0410:
        r9 = 1;
        goto L_0x0414;
    L_0x0412:
        r9 = r19;
    L_0x0414:
        if (r26 != 0) goto L_0x0435;
    L_0x0416:
        r10 = 0;
        r20 = 1;
        if (r26 == 0) goto L_0x041e;
    L_0x041b:
        r21 = 2;
        goto L_0x0420;
    L_0x041e:
        r21 = 1;
    L_0x0420:
        r0 = r30;
        r1 = r31;
        r2 = r8;
        r4 = r17;
        r7 = r10;
        r8 = r9;
        r9 = r20;
        r10 = r21;
        r20 = r11;
        r11 = r25;
        r0.createLoadOperationForImageReceiver(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r11);
        goto L_0x0437;
    L_0x0435:
        r20 = r11;
    L_0x0437:
        if (r24 != 0) goto L_0x044e;
    L_0x0439:
        r7 = 0;
        r9 = 0;
        r10 = 0;
        r0 = r30;
        r1 = r31;
        r2 = r13;
        r3 = r22;
        r4 = r17;
        r5 = r16;
        r6 = r14;
        r8 = r15;
        r11 = r25;
        r0.createLoadOperationForImageReceiver(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r11);
    L_0x044e:
        r7 = r31.getSize();
        r9 = 3;
        r10 = 0;
        r0 = r30;
        r1 = r31;
        r2 = r12;
        r3 = r23;
        r4 = r17;
        r5 = r18;
        r6 = r20;
        r8 = r19;
        r11 = r25;
        r0.createLoadOperationForImageReceiver(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r11);
        goto L_0x04a0;
    L_0x0469:
        r0 = r31.getCacheType();
        if (r0 != 0) goto L_0x0473;
    L_0x046f:
        if (r21 == 0) goto L_0x0473;
    L_0x0471:
        r12 = 1;
        goto L_0x0474;
    L_0x0473:
        r12 = r0;
    L_0x0474:
        if (r12 != 0) goto L_0x0478;
    L_0x0476:
        r9 = 1;
        goto L_0x0479;
    L_0x0478:
        r9 = r12;
    L_0x0479:
        r10 = 0;
        r11 = 1;
        if (r26 == 0) goto L_0x047f;
    L_0x047d:
        r15 = 2;
        goto L_0x0480;
    L_0x047f:
        r15 = 1;
    L_0x0480:
        r0 = r30;
        r1 = r31;
        r2 = r8;
        r4 = r17;
        r7 = r10;
        r8 = r9;
        r9 = r11;
        r10 = r15;
        r11 = r25;
        r0.createLoadOperationForImageReceiver(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r11);
        r7 = r31.getSize();
        r9 = 0;
        r10 = 0;
        r2 = r13;
        r3 = r22;
        r5 = r16;
        r6 = r14;
        r8 = r12;
        r0.createLoadOperationForImageReceiver(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r11);
    L_0x04a0:
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
                int intValue = ((Integer) cacheImage.types.get(i2)).intValue();
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
        if (message.media != null) {
            PhotoSize findPhotoCachedSize = findPhotoCachedSize(message);
            if (findPhotoCachedSize != null) {
                byte[] bArr = findPhotoCachedSize.bytes;
                if (bArr != null && bArr.length != 0) {
                    FileLocation fileLocation = findPhotoCachedSize.location;
                    if (fileLocation == null || (fileLocation instanceof TL_fileLocationUnavailable)) {
                        findPhotoCachedSize.location = new TL_fileLocationToBeDeprecated();
                        fileLocation = findPhotoCachedSize.location;
                        fileLocation.volume_id = -2147483648L;
                        fileLocation.local_id = SharedConfig.getLastLocalId();
                    }
                    boolean z = true;
                    File pathToAttach = FileLoader.getPathToAttach(findPhotoCachedSize, true);
                    int i = 0;
                    if (MessageObject.shouldEncryptPhotoOrVideo(message)) {
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append(pathToAttach.getAbsolutePath());
                        stringBuilder.append(".enc");
                        pathToAttach = new File(stringBuilder.toString());
                    } else {
                        z = false;
                    }
                    if (!pathToAttach.exists()) {
                        String str = "rws";
                        if (z) {
                            try {
                                File internalCacheDir = FileLoader.getInternalCacheDir();
                                StringBuilder stringBuilder2 = new StringBuilder();
                                stringBuilder2.append(pathToAttach.getName());
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
                                Utilities.aesCtrDecryptionByteArray(findPhotoCachedSize.bytes, bArr2, bArr3, 0, findPhotoCachedSize.bytes.length, 0);
                            } catch (Exception e) {
                                FileLog.e(e);
                            }
                        }
                        RandomAccessFile randomAccessFile2 = new RandomAccessFile(pathToAttach, str);
                        randomAccessFile2.write(findPhotoCachedSize.bytes);
                        randomAccessFile2.close();
                    }
                    TL_photoSize tL_photoSize = new TL_photoSize();
                    tL_photoSize.w = findPhotoCachedSize.w;
                    tL_photoSize.h = findPhotoCachedSize.h;
                    tL_photoSize.location = findPhotoCachedSize.location;
                    tL_photoSize.size = findPhotoCachedSize.size;
                    tL_photoSize.type = findPhotoCachedSize.type;
                    MessageMedia messageMedia = message.media;
                    int size;
                    if (messageMedia instanceof TL_messageMediaPhoto) {
                        size = messageMedia.photo.sizes.size();
                        while (i < size) {
                            if (((PhotoSize) message.media.photo.sizes.get(i)) instanceof TL_photoCachedSize) {
                                message.media.photo.sizes.set(i, tL_photoSize);
                                break;
                            }
                            i++;
                        }
                    } else if (messageMedia instanceof TL_messageMediaDocument) {
                        size = messageMedia.document.thumbs.size();
                        while (i < size) {
                            if (((PhotoSize) message.media.document.thumbs.get(i)) instanceof TL_photoCachedSize) {
                                message.media.document.thumbs.set(i, tL_photoSize);
                                break;
                            }
                            i++;
                        }
                    } else if (messageMedia instanceof TL_messageMediaWebPage) {
                        size = messageMedia.webpage.photo.sizes.size();
                        while (i < size) {
                            if (((PhotoSize) message.media.webpage.photo.sizes.get(i)) instanceof TL_photoCachedSize) {
                                message.media.webpage.photo.sizes.set(i, tL_photoSize);
                                break;
                            }
                            i++;
                        }
                    }
                }
            }
        }
    }

    private static PhotoSize findPhotoCachedSize(Message message) {
        PhotoSize photoSize;
        MessageMedia messageMedia = message.media;
        int i = 0;
        int size;
        if (messageMedia instanceof TL_messageMediaPhoto) {
            size = messageMedia.photo.sizes.size();
            while (i < size) {
                photoSize = (PhotoSize) message.media.photo.sizes.get(i);
                if (!(photoSize instanceof TL_photoCachedSize)) {
                    i++;
                }
            }
            return null;
        } else if (messageMedia instanceof TL_messageMediaDocument) {
            size = messageMedia.document.thumbs.size();
            while (i < size) {
                photoSize = (PhotoSize) message.media.document.thumbs.get(i);
                if (!(photoSize instanceof TL_photoCachedSize)) {
                    i++;
                }
            }
            return null;
        } else if (!(messageMedia instanceof TL_messageMediaWebPage)) {
            return null;
        } else {
            Photo photo = messageMedia.webpage.photo;
            if (photo == null) {
                return null;
            }
            size = photo.sizes.size();
            while (i < size) {
                photoSize = (PhotoSize) message.media.webpage.photo.sizes.get(i);
                if (!(photoSize instanceof TL_photoCachedSize)) {
                    i++;
                }
            }
            return null;
        }
        return photoSize;
    }

    public static void saveMessagesThumbs(ArrayList<Message> arrayList) {
        if (arrayList != null && !arrayList.isEmpty()) {
            for (int i = 0; i < arrayList.size(); i++) {
                saveMessageThumbs((Message) arrayList.get(i));
            }
        }
    }

    public static MessageThumb generateMessageThumb(Message message) {
        Bitmap createScaledBitmap;
        Message message2 = message;
        PhotoSize findPhotoCachedSize = findPhotoCachedSize(message);
        if (findPhotoCachedSize != null) {
            byte[] bArr = findPhotoCachedSize.bytes;
            if (!(bArr == null || bArr.length == 0)) {
                File pathToAttach = FileLoader.getPathToAttach(findPhotoCachedSize, true);
                TL_photoSize tL_photoSize = new TL_photoSize();
                tL_photoSize.w = findPhotoCachedSize.w;
                tL_photoSize.h = findPhotoCachedSize.h;
                tL_photoSize.location = findPhotoCachedSize.location;
                tL_photoSize.size = findPhotoCachedSize.size;
                tL_photoSize.type = findPhotoCachedSize.type;
                if (pathToAttach.exists() && message2.grouped_id == 0) {
                    Point messageSize = ChatMessageCell.getMessageSize(findPhotoCachedSize.w, findPhotoCachedSize.h);
                    String format = String.format(Locale.US, "%d_%d@%d_%d_b", new Object[]{Long.valueOf(findPhotoCachedSize.location.volume_id), Integer.valueOf(findPhotoCachedSize.location.local_id), Integer.valueOf((int) (messageSize.x / AndroidUtilities.density)), Integer.valueOf((int) (messageSize.y / AndroidUtilities.density))});
                    if (!getInstance().isInMemCache(format, false)) {
                        String path = pathToAttach.getPath();
                        float f = messageSize.x;
                        float f2 = AndroidUtilities.density;
                        Bitmap loadBitmap = loadBitmap(path, null, (float) ((int) (f / f2)), (float) ((int) (messageSize.y / f2)), false);
                        if (loadBitmap != null) {
                            Utilities.blurBitmap(loadBitmap, 3, 1, loadBitmap.getWidth(), loadBitmap.getHeight(), loadBitmap.getRowBytes());
                            f = messageSize.x;
                            float f3 = AndroidUtilities.density;
                            createScaledBitmap = Bitmaps.createScaledBitmap(loadBitmap, (int) (f / f3), (int) (messageSize.y / f3), true);
                            if (createScaledBitmap != loadBitmap) {
                                loadBitmap.recycle();
                            } else {
                                createScaledBitmap = loadBitmap;
                            }
                            return new MessageThumb(format, new BitmapDrawable(createScaledBitmap));
                        }
                    }
                }
                return null;
            }
        }
        MessageMedia messageMedia = message2.media;
        if (messageMedia instanceof TL_messageMediaDocument) {
            int size = messageMedia.document.thumbs.size();
            for (int i = 0; i < size; i++) {
                PhotoSize photoSize = (PhotoSize) message2.media.document.thumbs.get(i);
                if (photoSize instanceof TL_photoStrippedSize) {
                    int i2;
                    int i3;
                    PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(message2.media.document.thumbs, 320);
                    if (closestPhotoSizeWithSize != null) {
                        i2 = closestPhotoSizeWithSize.h;
                        i3 = closestPhotoSizeWithSize.w;
                    } else {
                        for (i3 = 0; i3 < message2.media.document.attributes.size(); i3++) {
                            if (message2.media.document.attributes.get(i3) instanceof TL_documentAttributeVideo) {
                                TL_documentAttributeVideo tL_documentAttributeVideo = (TL_documentAttributeVideo) message2.media.document.attributes.get(i3);
                                i2 = tL_documentAttributeVideo.h;
                                i3 = tL_documentAttributeVideo.w;
                                break;
                            }
                        }
                        i3 = 0;
                        i2 = 0;
                    }
                    Point messageSize2 = ChatMessageCell.getMessageSize(i3, i2);
                    String format2 = String.format(Locale.US, "%s_false@%d_%d_b", new Object[]{ImageLocation.getStippedKey(message2, message2, photoSize), Integer.valueOf((int) (messageSize2.x / AndroidUtilities.density)), Integer.valueOf((int) (messageSize2.y / AndroidUtilities.density))});
                    if (getInstance().memCache.contains(format2)) {
                        continue;
                    } else {
                        Bitmap strippedPhotoBitmap = getStrippedPhotoBitmap(photoSize.bytes, null);
                        if (strippedPhotoBitmap != null) {
                            Utilities.blurBitmap(strippedPhotoBitmap, 3, 1, strippedPhotoBitmap.getWidth(), strippedPhotoBitmap.getHeight(), strippedPhotoBitmap.getRowBytes());
                            float f4 = messageSize2.x;
                            float f5 = AndroidUtilities.density;
                            createScaledBitmap = Bitmaps.createScaledBitmap(strippedPhotoBitmap, (int) (f4 / f5), (int) (messageSize2.y / f5), true);
                            if (createScaledBitmap != strippedPhotoBitmap) {
                                strippedPhotoBitmap.recycle();
                            } else {
                                createScaledBitmap = strippedPhotoBitmap;
                            }
                            return new MessageThumb(format2, new BitmapDrawable(createScaledBitmap));
                        }
                    }
                }
            }
        }
        return null;
    }
}
