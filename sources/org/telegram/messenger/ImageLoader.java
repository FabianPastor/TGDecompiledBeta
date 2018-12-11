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
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Environment;
import android.support.media.ExifInterface;
import android.text.TextUtils;
import android.util.SparseArray;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.util.MimeTypes;
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
import org.telegram.messenger.support.widget.helper.ItemTouchHelper.Callback;
import org.telegram.p005ui.Components.AnimatedFileDrawable;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.InputEncryptedFile;
import org.telegram.tgnet.TLRPC.InputFile;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.TL_documentEncrypted;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_fileEncryptedLocation;
import org.telegram.tgnet.TLRPC.TL_fileLocation;
import org.telegram.tgnet.TLRPC.TL_fileLocationUnavailable;
import org.telegram.tgnet.TLRPC.TL_messageMediaDocument;
import org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;
import org.telegram.tgnet.TLRPC.TL_messageMediaWebPage;
import org.telegram.tgnet.TLRPC.TL_photoCachedSize;
import org.telegram.tgnet.TLRPC.TL_photoSize;
import org.telegram.tgnet.TLRPC.TL_upload_getWebFile;

public class ImageLoader {
    private static volatile ImageLoader Instance = null;
    private static byte[] bytes;
    private static byte[] bytesThumb;
    private static byte[] header = new byte[12];
    private static byte[] headerThumb = new byte[12];
    private HashMap<String, Integer> bitmapUseCounts = new HashMap();
    private DispatchQueue cacheOutQueue = new DispatchQueue("cacheOutQueue");
    private DispatchQueue cacheThumbOutQueue = new DispatchQueue("cacheThumbOutQueue");
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

    /* renamed from: org.telegram.messenger.ImageLoader$3 */
    class CLASSNAME extends BroadcastReceiver {
        CLASSNAME() {
        }

        public void onReceive(Context arg0, Intent intent) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.m11d("file system changed");
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
    }

    private class CacheImage {
        protected boolean animatedFile;
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

        /* synthetic */ CacheImage(ImageLoader x0, CLASSNAME x1) {
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

        /* JADX WARNING: Removed duplicated region for block: B:106:0x01b2 A:{SYNTHETIC, Splitter: B:106:0x01b2} */
        /* JADX WARNING: Removed duplicated region for block: B:415:0x08bd  */
        /* JADX WARNING: Removed duplicated region for block: B:87:0x0172  */
        /* JADX WARNING: Missing block: B:8:0x001f, code:
            if (r56.cacheImage.animatedFile == false) goto L_0x0067;
     */
        /* JADX WARNING: Missing block: B:9:0x0021, code:
            r6 = r56.sync;
     */
        /* JADX WARNING: Missing block: B:10:0x0025, code:
            monitor-enter(r6);
     */
        /* JADX WARNING: Missing block: B:13:0x002a, code:
            if (r56.isCancelled == false) goto L_0x0034;
     */
        /* JADX WARNING: Missing block: B:14:0x002c, code:
            monitor-exit(r6);
     */
        /* JADX WARNING: Missing block: B:23:?, code:
            monitor-exit(r6);
     */
        /* JADX WARNING: Missing block: B:24:0x0035, code:
            r6 = r56.cacheImage.finalFilePath;
     */
        /* JADX WARNING: Missing block: B:25:0x0043, code:
            if (r56.cacheImage.filter == null) goto L_0x0065;
     */
        /* JADX WARNING: Missing block: B:27:0x0052, code:
            if (r56.cacheImage.filter.equals("d") == false) goto L_0x0065;
     */
        /* JADX WARNING: Missing block: B:28:0x0054, code:
            r5 = true;
     */
        /* JADX WARNING: Missing block: B:29:0x0055, code:
            r0 = new org.telegram.p005ui.Components.AnimatedFileDrawable(r6, r5);
            java.lang.Thread.interrupted();
            onPostExecute(r0);
     */
        /* JADX WARNING: Missing block: B:30:0x0065, code:
            r5 = false;
     */
        /* JADX WARNING: Missing block: B:31:0x0067, code:
            r37 = null;
            r38 = false;
            r21 = r56.cacheImage.finalFilePath;
     */
        /* JADX WARNING: Missing block: B:32:0x007b, code:
            if (r56.cacheImage.secureDocument != null) goto L_0x0094;
     */
        /* JADX WARNING: Missing block: B:34:0x0083, code:
            if (r56.cacheImage.encryptionKeyPath == null) goto L_0x017e;
     */
        /* JADX WARNING: Missing block: B:35:0x0085, code:
            if (r21 == null) goto L_0x017e;
     */
        /* JADX WARNING: Missing block: B:37:0x0092, code:
            if (r21.getAbsolutePath().endsWith(".enc") == false) goto L_0x017e;
     */
        /* JADX WARNING: Missing block: B:38:0x0094, code:
            r34 = true;
     */
        /* JADX WARNING: Missing block: B:40:0x009c, code:
            if (r56.cacheImage.secureDocument == null) goto L_0x018e;
     */
        /* JADX WARNING: Missing block: B:41:0x009e, code:
            r52 = r56.cacheImage.secureDocument.secureDocumentKey;
     */
        /* JADX WARNING: Missing block: B:42:0x00b0, code:
            if (r56.cacheImage.secureDocument.secureFile == null) goto L_0x0182;
     */
        /* JADX WARNING: Missing block: B:44:0x00bc, code:
            if (r56.cacheImage.secureDocument.secureFile.file_hash == null) goto L_0x0182;
     */
        /* JADX WARNING: Missing block: B:45:0x00be, code:
            r51 = r56.cacheImage.secureDocument.secureFile.file_hash;
     */
        /* JADX WARNING: Missing block: B:46:0x00ca, code:
            r22 = true;
            r54 = false;
     */
        /* JADX WARNING: Missing block: B:47:0x00d2, code:
            if (android.os.Build.VERSION.SDK_INT >= 19) goto L_0x0128;
     */
        /* JADX WARNING: Missing block: B:48:0x00d4, code:
            r46 = null;
     */
        /* JADX WARNING: Missing block: B:50:?, code:
            r0 = new java.io.RandomAccessFile(r21, "r");
     */
        /* JADX WARNING: Missing block: B:53:0x00e8, code:
            if (r56.cacheImage.selfThumb == false) goto L_0x0194;
     */
        /* JADX WARNING: Missing block: B:54:0x00ea, code:
            r20 = org.telegram.messenger.ImageLoader.access$900();
     */
        /* JADX WARNING: Missing block: B:55:0x00ee, code:
            r0.readFully(r20, 0, r20.length);
            r53 = new java.lang.String(r20).toLowerCase().toLowerCase();
     */
        /* JADX WARNING: Missing block: B:56:0x0111, code:
            if (r53.startsWith("riff") == false) goto L_0x0120;
     */
        /* JADX WARNING: Missing block: B:58:0x011c, code:
            if (r53.endsWith("webp") == false) goto L_0x0120;
     */
        /* JADX WARNING: Missing block: B:59:0x011e, code:
            r54 = true;
     */
        /* JADX WARNING: Missing block: B:60:0x0120, code:
            r0.close();
     */
        /* JADX WARNING: Missing block: B:61:0x0123, code:
            if (r0 == null) goto L_0x0128;
     */
        /* JADX WARNING: Missing block: B:63:?, code:
            r0.close();
     */
        /* JADX WARNING: Missing block: B:65:0x012e, code:
            if (r56.cacheImage.selfThumb != false) goto L_0x0130;
     */
        /* JADX WARNING: Missing block: B:66:0x0130, code:
            r16 = 0;
     */
        /* JADX WARNING: Missing block: B:67:0x0138, code:
            if (r56.cacheImage.filter != null) goto L_0x013a;
     */
        /* JADX WARNING: Missing block: B:69:0x0147, code:
            if (r56.cacheImage.filter.contains("b2") != false) goto L_0x0149;
     */
        /* JADX WARNING: Missing block: B:70:0x0149, code:
            r16 = 3;
     */
        /* JADX WARNING: Missing block: B:72:?, code:
            org.telegram.messenger.ImageLoader.access$1102(r56.this$0, java.lang.System.currentTimeMillis());
     */
        /* JADX WARNING: Missing block: B:73:0x015a, code:
            monitor-enter(r56.sync);
     */
        /* JADX WARNING: Missing block: B:76:0x015f, code:
            if (r56.isCancelled != false) goto L_0x0161;
     */
        /* JADX WARNING: Missing block: B:82:0x0167, code:
            r25 = th;
     */
        /* JADX WARNING: Missing block: B:83:0x0168, code:
            r4 = null;
     */
        /* JADX WARNING: Missing block: B:87:0x0172, code:
            r5 = new android.graphics.drawable.BitmapDrawable(r4);
     */
        /* JADX WARNING: Missing block: B:89:0x017e, code:
            r34 = false;
     */
        /* JADX WARNING: Missing block: B:90:0x0182, code:
            r51 = r56.cacheImage.secureDocument.fileHash;
     */
        /* JADX WARNING: Missing block: B:91:0x018e, code:
            r52 = null;
            r51 = null;
     */
        /* JADX WARNING: Missing block: B:93:?, code:
            r20 = org.telegram.messenger.ImageLoader.access$1000();
     */
        /* JADX WARNING: Missing block: B:94:0x019a, code:
            r25 = move-exception;
     */
        /* JADX WARNING: Missing block: B:95:0x019b, code:
            org.telegram.messenger.FileLog.m14e(r25);
     */
        /* JADX WARNING: Missing block: B:96:0x019f, code:
            r25 = e;
     */
        /* JADX WARNING: Missing block: B:98:?, code:
            org.telegram.messenger.FileLog.m14e(r25);
     */
        /* JADX WARNING: Missing block: B:99:0x01a3, code:
            if (r46 != null) goto L_0x01a5;
     */
        /* JADX WARNING: Missing block: B:101:?, code:
            r46.close();
     */
        /* JADX WARNING: Missing block: B:102:0x01a9, code:
            r25 = move-exception;
     */
        /* JADX WARNING: Missing block: B:103:0x01aa, code:
            org.telegram.messenger.FileLog.m14e(r25);
     */
        /* JADX WARNING: Missing block: B:104:0x01af, code:
            r5 = th;
     */
        /* JADX WARNING: Missing block: B:105:0x01b0, code:
            if (r46 != null) goto L_0x01b2;
     */
        /* JADX WARNING: Missing block: B:107:?, code:
            r46.close();
     */
        /* JADX WARNING: Missing block: B:108:0x01b5, code:
            throw r5;
     */
        /* JADX WARNING: Missing block: B:109:0x01b6, code:
            r25 = move-exception;
     */
        /* JADX WARNING: Missing block: B:110:0x01b7, code:
            org.telegram.messenger.FileLog.m14e(r25);
     */
        /* JADX WARNING: Missing block: B:112:0x01c8, code:
            if (r56.cacheImage.filter.contains("b1") != false) goto L_0x01ca;
     */
        /* JADX WARNING: Missing block: B:113:0x01ca, code:
            r16 = 2;
     */
        /* JADX WARNING: Missing block: B:115:0x01db, code:
            if (r56.cacheImage.filter.contains("b") != false) goto L_0x01dd;
     */
        /* JADX WARNING: Missing block: B:116:0x01dd, code:
            r16 = 1;
     */
        /* JADX WARNING: Missing block: B:120:?, code:
            r41 = new android.graphics.BitmapFactory.Options();
            r41.inSampleSize = 1;
     */
        /* JADX WARNING: Missing block: B:121:0x01f0, code:
            if (android.os.Build.VERSION.SDK_INT >= 21) goto L_0x01f7;
     */
        /* JADX WARNING: Missing block: B:122:0x01f2, code:
            r41.inPurgeable = true;
     */
        /* JADX WARNING: Missing block: B:123:0x01f7, code:
            if (r54 == false) goto L_0x026c;
     */
        /* JADX WARNING: Missing block: B:124:0x01f9, code:
            r0 = new java.io.RandomAccessFile(r21, "r");
            r19 = r0.getChannel().map(java.nio.channels.FileChannel.MapMode.READ_ONLY, 0, r21.length());
            r18 = new android.graphics.BitmapFactory.Options();
            r18.inJustDecodeBounds = true;
            org.telegram.messenger.Utilities.loadWebpImage(null, r19, r19.limit(), r18, true);
            r4 = org.telegram.messenger.Bitmaps.createBitmap(r18.outWidth, r18.outHeight, android.graphics.Bitmap.Config.ARGB_8888);
     */
        /* JADX WARNING: Missing block: B:126:?, code:
            r6 = r19.limit();
     */
        /* JADX WARNING: Missing block: B:127:0x0243, code:
            if (r41.inPurgeable != false) goto L_0x026a;
     */
        /* JADX WARNING: Missing block: B:128:0x0245, code:
            r5 = true;
     */
        /* JADX WARNING: Missing block: B:129:0x0246, code:
            org.telegram.messenger.Utilities.loadWebpImage(r4, r19, r6, null, r5);
            r0.close();
     */
        /* JADX WARNING: Missing block: B:130:0x024e, code:
            if (r4 != null) goto L_0x032f;
     */
        /* JADX WARNING: Missing block: B:132:0x0258, code:
            if (r21.length() == 0) goto L_0x0262;
     */
        /* JADX WARNING: Missing block: B:134:0x0260, code:
            if (r56.cacheImage.filter != null) goto L_0x016d;
     */
        /* JADX WARNING: Missing block: B:135:0x0262, code:
            r21.delete();
     */
        /* JADX WARNING: Missing block: B:136:0x0267, code:
            r25 = th;
     */
        /* JADX WARNING: Missing block: B:137:0x026a, code:
            r5 = false;
     */
        /* JADX WARNING: Missing block: B:140:0x0270, code:
            if (r41.inPurgeable != false) goto L_0x0274;
     */
        /* JADX WARNING: Missing block: B:141:0x0272, code:
            if (r52 == null) goto L_0x0306;
     */
        /* JADX WARNING: Missing block: B:142:0x0274, code:
            r0 = new java.io.RandomAccessFile(r21, "r");
            r36 = (int) r0.length();
            r40 = 0;
     */
        /* JADX WARNING: Missing block: B:143:0x028d, code:
            if (org.telegram.messenger.ImageLoader.access$1200() == null) goto L_0x02f2;
     */
        /* JADX WARNING: Missing block: B:145:0x0296, code:
            if (org.telegram.messenger.ImageLoader.access$1200().length < r36) goto L_0x02f2;
     */
        /* JADX WARNING: Missing block: B:146:0x0298, code:
            r23 = org.telegram.messenger.ImageLoader.access$1200();
     */
        /* JADX WARNING: Missing block: B:147:0x029c, code:
            if (r23 != null) goto L_0x02a7;
     */
        /* JADX WARNING: Missing block: B:148:0x029e, code:
            r23 = new byte[r36];
            org.telegram.messenger.ImageLoader.access$1202(r23);
     */
        /* JADX WARNING: Missing block: B:149:0x02a7, code:
            r0.readFully(r23, 0, r36);
            r0.close();
            r26 = false;
     */
        /* JADX WARNING: Missing block: B:150:0x02b6, code:
            if (r52 == null) goto L_0x02f5;
     */
        /* JADX WARNING: Missing block: B:151:0x02b8, code:
            org.telegram.messenger.secretmedia.EncryptedFileInputStream.decryptBytesWithKeyFile(r23, 0, r36, r52);
            r31 = org.telegram.messenger.Utilities.computeSHA256(r23, 0, r36);
     */
        /* JADX WARNING: Missing block: B:152:0x02cb, code:
            if (r51 == null) goto L_0x02d7;
     */
        /* JADX WARNING: Missing block: B:154:0x02d5, code:
            if (java.util.Arrays.equals(r31, r51) != false) goto L_0x02d9;
     */
        /* JADX WARNING: Missing block: B:155:0x02d7, code:
            r26 = true;
     */
        /* JADX WARNING: Missing block: B:156:0x02d9, code:
            r40 = r23[0] & 255;
            r36 = r36 - r40;
     */
        /* JADX WARNING: Missing block: B:157:0x02e2, code:
            if (r26 != false) goto L_0x08d2;
     */
        /* JADX WARNING: Missing block: B:158:0x02e4, code:
            r4 = android.graphics.BitmapFactory.decodeByteArray(r23, r40, r36, r41);
     */
        /* JADX WARNING: Missing block: B:159:0x02f2, code:
            r23 = null;
     */
        /* JADX WARNING: Missing block: B:160:0x02f5, code:
            if (r34 == false) goto L_0x02e2;
     */
        /* JADX WARNING: Missing block: B:161:0x02f7, code:
            org.telegram.messenger.secretmedia.EncryptedFileInputStream.decryptBytesWithKeyFile(r23, 0, r36, r56.cacheImage.encryptionKeyPath);
     */
        /* JADX WARNING: Missing block: B:162:0x0306, code:
            if (r34 == false) goto L_0x0325;
     */
        /* JADX WARNING: Missing block: B:163:0x0308, code:
            r0 = new org.telegram.messenger.secretmedia.EncryptedFileInputStream(r21, r56.cacheImage.encryptionKeyPath);
     */
        /* JADX WARNING: Missing block: B:164:0x0317, code:
            r4 = android.graphics.BitmapFactory.decodeStream(r35, null, r41);
     */
        /* JADX WARNING: Missing block: B:166:?, code:
            r35.close();
     */
        /* JADX WARNING: Missing block: B:168:?, code:
            r0 = new java.io.FileInputStream(r21);
     */
        /* JADX WARNING: Missing block: B:170:0x0332, code:
            if (r16 != 1) goto L_0x0357;
     */
        /* JADX WARNING: Missing block: B:173:0x033a, code:
            if (r4.getConfig() != android.graphics.Bitmap.Config.ARGB_8888) goto L_0x016d;
     */
        /* JADX WARNING: Missing block: B:175:0x0341, code:
            if (r41.inPurgeable == false) goto L_0x0355;
     */
        /* JADX WARNING: Missing block: B:176:0x0343, code:
            r6 = 0;
     */
        /* JADX WARNING: Missing block: B:177:0x0344, code:
            org.telegram.messenger.Utilities.blurBitmap(r4, 3, r6, r4.getWidth(), r4.getHeight(), r4.getRowBytes());
     */
        /* JADX WARNING: Missing block: B:178:0x0355, code:
            r6 = 1;
     */
        /* JADX WARNING: Missing block: B:180:0x035a, code:
            if (r16 != 2) goto L_0x037f;
     */
        /* JADX WARNING: Missing block: B:182:0x0362, code:
            if (r4.getConfig() != android.graphics.Bitmap.Config.ARGB_8888) goto L_0x016d;
     */
        /* JADX WARNING: Missing block: B:184:0x0369, code:
            if (r41.inPurgeable == false) goto L_0x037d;
     */
        /* JADX WARNING: Missing block: B:185:0x036b, code:
            r6 = 0;
     */
        /* JADX WARNING: Missing block: B:186:0x036c, code:
            org.telegram.messenger.Utilities.blurBitmap(r4, 1, r6, r4.getWidth(), r4.getHeight(), r4.getRowBytes());
     */
        /* JADX WARNING: Missing block: B:187:0x037d, code:
            r6 = 1;
     */
        /* JADX WARNING: Missing block: B:189:0x0382, code:
            if (r16 != 3) goto L_0x03d9;
     */
        /* JADX WARNING: Missing block: B:191:0x038a, code:
            if (r4.getConfig() != android.graphics.Bitmap.Config.ARGB_8888) goto L_0x016d;
     */
        /* JADX WARNING: Missing block: B:193:0x0391, code:
            if (r41.inPurgeable == false) goto L_0x03d3;
     */
        /* JADX WARNING: Missing block: B:194:0x0393, code:
            r6 = 0;
     */
        /* JADX WARNING: Missing block: B:195:0x0394, code:
            org.telegram.messenger.Utilities.blurBitmap(r4, 7, r6, r4.getWidth(), r4.getHeight(), r4.getRowBytes());
     */
        /* JADX WARNING: Missing block: B:196:0x03a8, code:
            if (r41.inPurgeable == false) goto L_0x03d5;
     */
        /* JADX WARNING: Missing block: B:197:0x03aa, code:
            r6 = 0;
     */
        /* JADX WARNING: Missing block: B:198:0x03ab, code:
            org.telegram.messenger.Utilities.blurBitmap(r4, 7, r6, r4.getWidth(), r4.getHeight(), r4.getRowBytes());
     */
        /* JADX WARNING: Missing block: B:199:0x03bf, code:
            if (r41.inPurgeable == false) goto L_0x03d7;
     */
        /* JADX WARNING: Missing block: B:200:0x03c1, code:
            r6 = 0;
     */
        /* JADX WARNING: Missing block: B:201:0x03c2, code:
            org.telegram.messenger.Utilities.blurBitmap(r4, 7, r6, r4.getWidth(), r4.getHeight(), r4.getRowBytes());
     */
        /* JADX WARNING: Missing block: B:202:0x03d3, code:
            r6 = 1;
     */
        /* JADX WARNING: Missing block: B:203:0x03d5, code:
            r6 = 1;
     */
        /* JADX WARNING: Missing block: B:204:0x03d7, code:
            r6 = 1;
     */
        /* JADX WARNING: Missing block: B:205:0x03d9, code:
            if (r16 != 0) goto L_0x016d;
     */
        /* JADX WARNING: Missing block: B:207:0x03df, code:
            if (r41.inPurgeable == false) goto L_0x016d;
     */
        /* JADX WARNING: Missing block: B:208:0x03e1, code:
            org.telegram.messenger.Utilities.pinBitmap(r4);
     */
        /* JADX WARNING: Missing block: B:209:0x03e6, code:
            r39 = null;
     */
        /* JADX WARNING: Missing block: B:212:0x03ee, code:
            if (r56.cacheImage.httpUrl != null) goto L_0x03f0;
     */
        /* JADX WARNING: Missing block: B:214:0x03fd, code:
            if (r56.cacheImage.httpUrl.startsWith("thumb://") != false) goto L_0x03ff;
     */
        /* JADX WARNING: Missing block: B:215:0x03ff, code:
            r32 = r56.cacheImage.httpUrl.indexOf(":", 8);
     */
        /* JADX WARNING: Missing block: B:216:0x040e, code:
            if (r32 >= 0) goto L_0x0410;
     */
        /* JADX WARNING: Missing block: B:217:0x0410, code:
            r37 = java.lang.Long.valueOf(java.lang.Long.parseLong(r56.cacheImage.httpUrl.substring(8, r32)));
            r38 = false;
            r39 = r56.cacheImage.httpUrl.substring(r32 + 1);
     */
        /* JADX WARNING: Missing block: B:218:0x0434, code:
            r22 = false;
     */
        /* JADX WARNING: Missing block: B:219:0x0436, code:
            r24 = 20;
     */
        /* JADX WARNING: Missing block: B:220:0x0438, code:
            if (r37 != null) goto L_0x043a;
     */
        /* JADX WARNING: Missing block: B:221:0x043a, code:
            r24 = 0;
     */
        /* JADX WARNING: Missing block: B:229:0x0466, code:
            java.lang.Thread.sleep((long) r24);
     */
        /* JADX WARNING: Missing block: B:230:0x046c, code:
            org.telegram.messenger.ImageLoader.access$1102(r56.this$0, java.lang.System.currentTimeMillis());
     */
        /* JADX WARNING: Missing block: B:231:0x047b, code:
            monitor-enter(r56.sync);
     */
        /* JADX WARNING: Missing block: B:234:0x0480, code:
            if (r56.isCancelled != false) goto L_0x0482;
     */
        /* JADX WARNING: Missing block: B:241:0x0489, code:
            r4 = null;
     */
        /* JADX WARNING: Missing block: B:243:0x049a, code:
            if (r56.cacheImage.httpUrl.startsWith("vthumb://") != false) goto L_0x049c;
     */
        /* JADX WARNING: Missing block: B:244:0x049c, code:
            r32 = r56.cacheImage.httpUrl.indexOf(":", 9);
     */
        /* JADX WARNING: Missing block: B:245:0x04ab, code:
            if (r32 >= 0) goto L_0x04ad;
     */
        /* JADX WARNING: Missing block: B:246:0x04ad, code:
            r37 = java.lang.Long.valueOf(java.lang.Long.parseLong(r56.cacheImage.httpUrl.substring(9, r32)));
            r38 = true;
     */
        /* JADX WARNING: Missing block: B:247:0x04c5, code:
            r22 = false;
     */
        /* JADX WARNING: Missing block: B:249:0x04d6, code:
            if (r56.cacheImage.httpUrl.startsWith("http") == false) goto L_0x04d8;
     */
        /* JADX WARNING: Missing block: B:250:0x04d8, code:
            r22 = false;
     */
        /* JADX WARNING: Missing block: B:254:?, code:
            r41 = new android.graphics.BitmapFactory.Options();
            r41.inSampleSize = 1;
            r55 = 0.0f;
            r30 = 0.0f;
            r15 = false;
     */
        /* JADX WARNING: Missing block: B:255:0x04f2, code:
            if (r56.cacheImage.filter == null) goto L_0x064b;
     */
        /* JADX WARNING: Missing block: B:256:0x04f4, code:
            r12 = r56.cacheImage.filter.split("_");
     */
        /* JADX WARNING: Missing block: B:257:0x0503, code:
            if (r12.length < 2) goto L_0x051b;
     */
        /* JADX WARNING: Missing block: B:258:0x0505, code:
            r55 = java.lang.Float.parseFloat(r12[0]) * org.telegram.messenger.AndroidUtilities.density;
            r30 = java.lang.Float.parseFloat(r12[1]) * org.telegram.messenger.AndroidUtilities.density;
     */
        /* JADX WARNING: Missing block: B:260:0x0528, code:
            if (r56.cacheImage.filter.contains("b") == false) goto L_0x052b;
     */
        /* JADX WARNING: Missing block: B:261:0x052a, code:
            r15 = true;
     */
        /* JADX WARNING: Missing block: B:263:0x052e, code:
            if (r55 == 0.0f) goto L_0x08ca;
     */
        /* JADX WARNING: Missing block: B:265:0x0533, code:
            if (r30 == 0.0f) goto L_0x08ca;
     */
        /* JADX WARNING: Missing block: B:266:0x0535, code:
            r41.inJustDecodeBounds = true;
     */
        /* JADX WARNING: Missing block: B:267:0x053a, code:
            if (r37 == null) goto L_0x05a3;
     */
        /* JADX WARNING: Missing block: B:268:0x053c, code:
            if (r39 != null) goto L_0x05a3;
     */
        /* JADX WARNING: Missing block: B:269:0x053e, code:
            if (r38 == false) goto L_0x0590;
     */
        /* JADX WARNING: Missing block: B:270:0x0540, code:
            android.provider.MediaStore.Video.Thumbnails.getThumbnail(org.telegram.messenger.ApplicationLoader.applicationContext.getContentResolver(), r37.longValue(), 1, r41);
     */
        /* JADX WARNING: Missing block: B:271:0x0550, code:
            r4 = null;
     */
        /* JADX WARNING: Missing block: B:273:?, code:
            r49 = java.lang.Math.max(((float) r41.outWidth) / r55, ((float) r41.outHeight) / r30);
     */
        /* JADX WARNING: Missing block: B:274:0x056c, code:
            if (r49 >= 1.0f) goto L_0x0570;
     */
        /* JADX WARNING: Missing block: B:275:0x056e, code:
            r49 = 1.0f;
     */
        /* JADX WARNING: Missing block: B:276:0x0570, code:
            r41.inJustDecodeBounds = false;
            r41.inSampleSize = (int) r49;
     */
        /* JADX WARNING: Missing block: B:277:0x057c, code:
            r6 = r56.sync;
     */
        /* JADX WARNING: Missing block: B:278:0x0580, code:
            monitor-enter(r6);
     */
        /* JADX WARNING: Missing block: B:281:0x0585, code:
            if (r56.isCancelled == false) goto L_0x06a8;
     */
        /* JADX WARNING: Missing block: B:282:0x0587, code:
            monitor-exit(r6);
     */
        /* JADX WARNING: Missing block: B:289:?, code:
            android.provider.MediaStore.Images.Thumbnails.getThumbnail(org.telegram.messenger.ApplicationLoader.applicationContext.getContentResolver(), r37.longValue(), 1, r41);
            r4 = null;
     */
        /* JADX WARNING: Missing block: B:290:0x05a3, code:
            if (r52 == null) goto L_0x0622;
     */
        /* JADX WARNING: Missing block: B:291:0x05a5, code:
            r0 = new java.io.RandomAccessFile(r21, "r");
            r36 = (int) r0.length();
     */
        /* JADX WARNING: Missing block: B:292:0x05bc, code:
            if (org.telegram.messenger.ImageLoader.access$1300() == null) goto L_0x061f;
     */
        /* JADX WARNING: Missing block: B:294:0x05c5, code:
            if (org.telegram.messenger.ImageLoader.access$1300().length < r36) goto L_0x061f;
     */
        /* JADX WARNING: Missing block: B:295:0x05c7, code:
            r23 = org.telegram.messenger.ImageLoader.access$1300();
     */
        /* JADX WARNING: Missing block: B:296:0x05cb, code:
            if (r23 != null) goto L_0x05d6;
     */
        /* JADX WARNING: Missing block: B:297:0x05cd, code:
            r23 = new byte[r36];
            org.telegram.messenger.ImageLoader.access$1302(r23);
     */
        /* JADX WARNING: Missing block: B:298:0x05d6, code:
            r0.readFully(r23, 0, r36);
            r0.close();
            org.telegram.messenger.secretmedia.EncryptedFileInputStream.decryptBytesWithKeyFile(r23, 0, r36, r52);
            r31 = org.telegram.messenger.Utilities.computeSHA256(r23, 0, r36);
            r26 = false;
     */
        /* JADX WARNING: Missing block: B:299:0x05f8, code:
            if (r51 == null) goto L_0x0604;
     */
        /* JADX WARNING: Missing block: B:301:0x0602, code:
            if (java.util.Arrays.equals(r31, r51) != false) goto L_0x0606;
     */
        /* JADX WARNING: Missing block: B:302:0x0604, code:
            r26 = true;
     */
        /* JADX WARNING: Missing block: B:303:0x0606, code:
            r40 = r23[0] & 255;
            r36 = r36 - r40;
     */
        /* JADX WARNING: Missing block: B:304:0x060f, code:
            if (r26 != false) goto L_0x08ce;
     */
        /* JADX WARNING: Missing block: B:305:0x0611, code:
            r4 = android.graphics.BitmapFactory.decodeByteArray(r23, r40, r36, r41);
     */
        /* JADX WARNING: Missing block: B:306:0x061f, code:
            r23 = null;
     */
        /* JADX WARNING: Missing block: B:307:0x0622, code:
            if (r34 == false) goto L_0x0641;
     */
        /* JADX WARNING: Missing block: B:308:0x0624, code:
            r0 = new org.telegram.messenger.secretmedia.EncryptedFileInputStream(r21, r56.cacheImage.encryptionKeyPath);
     */
        /* JADX WARNING: Missing block: B:309:0x0633, code:
            r4 = android.graphics.BitmapFactory.decodeStream(r35, null, r41);
     */
        /* JADX WARNING: Missing block: B:311:?, code:
            r35.close();
     */
        /* JADX WARNING: Missing block: B:313:?, code:
            r0 = new java.io.FileInputStream(r21);
     */
        /* JADX WARNING: Missing block: B:314:0x064b, code:
            if (r39 == null) goto L_0x08ca;
     */
        /* JADX WARNING: Missing block: B:315:0x064d, code:
            r41.inJustDecodeBounds = true;
            r41.inPreferredConfig = android.graphics.Bitmap.Config.RGB_565;
            r0 = new java.io.FileInputStream(r21);
            r4 = android.graphics.BitmapFactory.decodeStream(r0, null, r41);
     */
        /* JADX WARNING: Missing block: B:317:?, code:
            r0.close();
            r45 = r41.outWidth;
            r43 = r41.outHeight;
            r41.inJustDecodeBounds = false;
            r49 = (float) java.lang.Math.max(r45 / org.telegram.messenger.support.widget.helper.ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION, r43 / org.telegram.messenger.support.widget.helper.ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION);
     */
        /* JADX WARNING: Missing block: B:318:0x0691, code:
            if (r49 >= 1.0f) goto L_0x0695;
     */
        /* JADX WARNING: Missing block: B:319:0x0693, code:
            r49 = 1.0f;
     */
        /* JADX WARNING: Missing block: B:320:0x0695, code:
            r48 = 1;
     */
        /* JADX WARNING: Missing block: B:321:0x0697, code:
            r48 = r48 * 2;
     */
        /* JADX WARNING: Missing block: B:322:0x069e, code:
            if (((float) (r48 * 2)) < r49) goto L_0x0697;
     */
        /* JADX WARNING: Missing block: B:323:0x06a0, code:
            r41.inSampleSize = r48;
     */
        /* JADX WARNING: Missing block: B:325:?, code:
            monitor-exit(r6);
     */
        /* JADX WARNING: Missing block: B:328:0x06af, code:
            if (r56.cacheImage.filter == null) goto L_0x06bb;
     */
        /* JADX WARNING: Missing block: B:329:0x06b1, code:
            if (r15 != false) goto L_0x06bb;
     */
        /* JADX WARNING: Missing block: B:331:0x06b9, code:
            if (r56.cacheImage.httpUrl == null) goto L_0x075c;
     */
        /* JADX WARNING: Missing block: B:332:0x06bb, code:
            r41.inPreferredConfig = android.graphics.Bitmap.Config.ARGB_8888;
     */
        /* JADX WARNING: Missing block: B:334:0x06c5, code:
            if (android.os.Build.VERSION.SDK_INT >= 21) goto L_0x06cc;
     */
        /* JADX WARNING: Missing block: B:335:0x06c7, code:
            r41.inPurgeable = true;
     */
        /* JADX WARNING: Missing block: B:336:0x06cc, code:
            r41.inDither = false;
     */
        /* JADX WARNING: Missing block: B:337:0x06d1, code:
            if (r37 == null) goto L_0x06e8;
     */
        /* JADX WARNING: Missing block: B:338:0x06d3, code:
            if (r39 != null) goto L_0x06e8;
     */
        /* JADX WARNING: Missing block: B:339:0x06d5, code:
            if (r38 == false) goto L_0x0764;
     */
        /* JADX WARNING: Missing block: B:340:0x06d7, code:
            r4 = android.provider.MediaStore.Video.Thumbnails.getThumbnail(org.telegram.messenger.ApplicationLoader.applicationContext.getContentResolver(), r37.longValue(), 1, r41);
     */
        /* JADX WARNING: Missing block: B:341:0x06e8, code:
            if (r4 != null) goto L_0x0741;
     */
        /* JADX WARNING: Missing block: B:342:0x06ea, code:
            if (r54 == false) goto L_0x0779;
     */
        /* JADX WARNING: Missing block: B:343:0x06ec, code:
            r0 = new java.io.RandomAccessFile(r21, "r");
            r19 = r0.getChannel().map(java.nio.channels.FileChannel.MapMode.READ_ONLY, 0, r21.length());
            r18 = new android.graphics.BitmapFactory.Options();
            r18.inJustDecodeBounds = true;
            org.telegram.messenger.Utilities.loadWebpImage(null, r19, r19.limit(), r18, true);
            r4 = org.telegram.messenger.Bitmaps.createBitmap(r18.outWidth, r18.outHeight, android.graphics.Bitmap.Config.ARGB_8888);
            r6 = r19.limit();
     */
        /* JADX WARNING: Missing block: B:344:0x0736, code:
            if (r41.inPurgeable != false) goto L_0x0777;
     */
        /* JADX WARNING: Missing block: B:345:0x0738, code:
            r5 = true;
     */
        /* JADX WARNING: Missing block: B:346:0x0739, code:
            org.telegram.messenger.Utilities.loadWebpImage(r4, r19, r6, null, r5);
            r0.close();
     */
        /* JADX WARNING: Missing block: B:347:0x0741, code:
            if (r4 != null) goto L_0x083c;
     */
        /* JADX WARNING: Missing block: B:348:0x0743, code:
            if (r22 == false) goto L_0x016d;
     */
        /* JADX WARNING: Missing block: B:350:0x074d, code:
            if (r21.length() == 0) goto L_0x0757;
     */
        /* JADX WARNING: Missing block: B:352:0x0755, code:
            if (r56.cacheImage.filter != null) goto L_0x016d;
     */
        /* JADX WARNING: Missing block: B:353:0x0757, code:
            r21.delete();
     */
        /* JADX WARNING: Missing block: B:354:0x075c, code:
            r41.inPreferredConfig = android.graphics.Bitmap.Config.RGB_565;
     */
        /* JADX WARNING: Missing block: B:355:0x0764, code:
            r4 = android.provider.MediaStore.Images.Thumbnails.getThumbnail(org.telegram.messenger.ApplicationLoader.applicationContext.getContentResolver(), r37.longValue(), 1, r41);
     */
        /* JADX WARNING: Missing block: B:356:0x0777, code:
            r5 = false;
     */
        /* JADX WARNING: Missing block: B:358:0x077d, code:
            if (r41.inPurgeable != false) goto L_0x0781;
     */
        /* JADX WARNING: Missing block: B:359:0x077f, code:
            if (r52 == null) goto L_0x0813;
     */
        /* JADX WARNING: Missing block: B:360:0x0781, code:
            r0 = new java.io.RandomAccessFile(r21, "r");
            r36 = (int) r0.length();
            r40 = 0;
     */
        /* JADX WARNING: Missing block: B:361:0x079a, code:
            if (org.telegram.messenger.ImageLoader.access$1300() == null) goto L_0x07ff;
     */
        /* JADX WARNING: Missing block: B:363:0x07a3, code:
            if (org.telegram.messenger.ImageLoader.access$1300().length < r36) goto L_0x07ff;
     */
        /* JADX WARNING: Missing block: B:364:0x07a5, code:
            r23 = org.telegram.messenger.ImageLoader.access$1300();
     */
        /* JADX WARNING: Missing block: B:365:0x07a9, code:
            if (r23 != null) goto L_0x07b4;
     */
        /* JADX WARNING: Missing block: B:366:0x07ab, code:
            r23 = new byte[r36];
            org.telegram.messenger.ImageLoader.access$1302(r23);
     */
        /* JADX WARNING: Missing block: B:367:0x07b4, code:
            r0.readFully(r23, 0, r36);
            r0.close();
            r26 = false;
     */
        /* JADX WARNING: Missing block: B:368:0x07c3, code:
            if (r52 == null) goto L_0x0802;
     */
        /* JADX WARNING: Missing block: B:369:0x07c5, code:
            org.telegram.messenger.secretmedia.EncryptedFileInputStream.decryptBytesWithKeyFile(r23, 0, r36, r52);
            r31 = org.telegram.messenger.Utilities.computeSHA256(r23, 0, r36);
     */
        /* JADX WARNING: Missing block: B:370:0x07d8, code:
            if (r51 == null) goto L_0x07e4;
     */
        /* JADX WARNING: Missing block: B:372:0x07e2, code:
            if (java.util.Arrays.equals(r31, r51) != false) goto L_0x07e6;
     */
        /* JADX WARNING: Missing block: B:373:0x07e4, code:
            r26 = true;
     */
        /* JADX WARNING: Missing block: B:374:0x07e6, code:
            r40 = r23[0] & 255;
            r36 = r36 - r40;
     */
        /* JADX WARNING: Missing block: B:375:0x07ef, code:
            if (r26 != false) goto L_0x0741;
     */
        /* JADX WARNING: Missing block: B:376:0x07f1, code:
            r4 = android.graphics.BitmapFactory.decodeByteArray(r23, r40, r36, r41);
     */
        /* JADX WARNING: Missing block: B:377:0x07ff, code:
            r23 = null;
     */
        /* JADX WARNING: Missing block: B:378:0x0802, code:
            if (r34 == false) goto L_0x07ef;
     */
        /* JADX WARNING: Missing block: B:379:0x0804, code:
            org.telegram.messenger.secretmedia.EncryptedFileInputStream.decryptBytesWithKeyFile(r23, 0, r36, r56.cacheImage.encryptionKeyPath);
     */
        /* JADX WARNING: Missing block: B:380:0x0813, code:
            if (r34 == false) goto L_0x0832;
     */
        /* JADX WARNING: Missing block: B:381:0x0815, code:
            r0 = new org.telegram.messenger.secretmedia.EncryptedFileInputStream(r21, r56.cacheImage.encryptionKeyPath);
     */
        /* JADX WARNING: Missing block: B:382:0x0824, code:
            r4 = android.graphics.BitmapFactory.decodeStream(r35, null, r41);
            r35.close();
     */
        /* JADX WARNING: Missing block: B:383:0x0832, code:
            r0 = new java.io.FileInputStream(r21);
     */
        /* JADX WARNING: Missing block: B:384:0x083c, code:
            r17 = false;
     */
        /* JADX WARNING: Missing block: B:385:0x0844, code:
            if (r56.cacheImage.filter == null) goto L_0x08ae;
     */
        /* JADX WARNING: Missing block: B:386:0x0846, code:
            r14 = (float) r4.getWidth();
            r13 = (float) r4.getHeight();
     */
        /* JADX WARNING: Missing block: B:387:0x0854, code:
            if (r41.inPurgeable != false) goto L_0x087d;
     */
        /* JADX WARNING: Missing block: B:389:0x0859, code:
            if (r55 == 0.0f) goto L_0x087d;
     */
        /* JADX WARNING: Missing block: B:391:0x085d, code:
            if (r14 == r55) goto L_0x087d;
     */
        /* JADX WARNING: Missing block: B:393:0x0865, code:
            if (r14 <= (20.0f + r55)) goto L_0x087d;
     */
        /* JADX WARNING: Missing block: B:394:0x0867, code:
            r50 = org.telegram.messenger.Bitmaps.createScaledBitmap(r4, (int) r55, (int) (r13 / (r14 / r55)), true);
     */
        /* JADX WARNING: Missing block: B:395:0x0876, code:
            if (r4 == r50) goto L_0x087d;
     */
        /* JADX WARNING: Missing block: B:396:0x0878, code:
            r4.recycle();
            r4 = r50;
     */
        /* JADX WARNING: Missing block: B:397:0x087d, code:
            if (r4 == null) goto L_0x08ae;
     */
        /* JADX WARNING: Missing block: B:398:0x087f, code:
            if (r15 == false) goto L_0x08ae;
     */
        /* JADX WARNING: Missing block: B:400:0x0885, code:
            if (r13 >= 100.0f) goto L_0x08ae;
     */
        /* JADX WARNING: Missing block: B:402:0x088b, code:
            if (r14 >= 100.0f) goto L_0x08ae;
     */
        /* JADX WARNING: Missing block: B:404:0x0893, code:
            if (r4.getConfig() != android.graphics.Bitmap.Config.ARGB_8888) goto L_0x08ac;
     */
        /* JADX WARNING: Missing block: B:406:0x089a, code:
            if (r41.inPurgeable == false) goto L_0x08bb;
     */
        /* JADX WARNING: Missing block: B:407:0x089c, code:
            r6 = 0;
     */
        /* JADX WARNING: Missing block: B:408:0x089d, code:
            org.telegram.messenger.Utilities.blurBitmap(r4, 3, r6, r4.getWidth(), r4.getHeight(), r4.getRowBytes());
     */
        /* JADX WARNING: Missing block: B:409:0x08ac, code:
            r17 = true;
     */
        /* JADX WARNING: Missing block: B:410:0x08ae, code:
            if (r17 != false) goto L_0x016d;
     */
        /* JADX WARNING: Missing block: B:412:0x08b4, code:
            if (r41.inPurgeable == false) goto L_0x016d;
     */
        /* JADX WARNING: Missing block: B:413:0x08b6, code:
            org.telegram.messenger.Utilities.pinBitmap(r4);
     */
        /* JADX WARNING: Missing block: B:414:0x08bb, code:
            r6 = 1;
     */
        /* JADX WARNING: Missing block: B:415:0x08bd, code:
            r5 = null;
     */
        /* JADX WARNING: Missing block: B:416:0x08c0, code:
            r5 = th;
     */
        /* JADX WARNING: Missing block: B:417:0x08c1, code:
            r46 = r0;
     */
        /* JADX WARNING: Missing block: B:418:0x08c5, code:
            r25 = e;
     */
        /* JADX WARNING: Missing block: B:419:0x08c6, code:
            r46 = r0;
     */
        /* JADX WARNING: Missing block: B:420:0x08ca, code:
            r4 = null;
     */
        /* JADX WARNING: Missing block: B:421:0x08ce, code:
            r4 = null;
     */
        /* JADX WARNING: Missing block: B:422:0x08d2, code:
            r4 = null;
     */
        /* JADX WARNING: Missing block: B:428:?, code:
            return;
     */
        /* JADX WARNING: Missing block: B:430:?, code:
            return;
     */
        /* JADX WARNING: Missing block: B:431:?, code:
            return;
     */
        /* JADX WARNING: Missing block: B:432:?, code:
            return;
     */
        /* JADX WARNING: Missing block: B:433:?, code:
            return;
     */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void run() {
            synchronized (this.sync) {
                this.runningThread = Thread.currentThread();
                Thread.interrupted();
                if (this.isCancelled) {
                    return;
                }
            }
            FileLog.m14e(e);
            Thread.interrupted();
            if (image == null) {
            }
            onPostExecute(r5);
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
                httpConnection.setConnectTimeout(DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS);
                httpConnection.setReadTimeout(DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS);
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
                    if (ConnectionsManager.isNetworkOnline()) {
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
                FileLog.m14e(e);
            }
            if (this.canRetry) {
                if (httpConnection != null) {
                    try {
                        if (httpConnection instanceof HttpURLConnection) {
                            int code = ((HttpURLConnection) httpConnection).getResponseCode();
                            if (!(code == 200 || code == 202 || code == 304)) {
                                this.canRetry = false;
                            }
                        }
                    } catch (Throwable e2) {
                        FileLog.m14e(e2);
                    }
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
                        FileLog.m14e(e22);
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
                        FileLog.m14e(e222);
                    } catch (Throwable e2222) {
                        FileLog.m14e(e2222);
                    }
                }
                try {
                    if (this.fileOutputStream != null) {
                        this.fileOutputStream.close();
                        this.fileOutputStream = null;
                    }
                } catch (Throwable e22222) {
                    FileLog.m14e(e22222);
                }
                if (httpConnectionStream != null) {
                    try {
                        httpConnectionStream.close();
                    } catch (Throwable e222222) {
                        FileLog.m14e(e222222);
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

        public HttpImageTask(CacheImage cacheImage, int size) {
            this.cacheImage = cacheImage;
            this.imageSize = size;
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
                    this.httpConnection = (HttpURLConnection) new URL(this.cacheImage.httpUrl).openConnection();
                    this.httpConnection.addRequestProperty("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 10_0 like Mac OS X) AppleWebKit/602.1.38 (KHTML, like Gecko) Version/10.0 Mobile/14A5297c Safari/602.1");
                    this.httpConnection.setConnectTimeout(DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS);
                    this.httpConnection.setReadTimeout(DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS);
                    this.httpConnection.setInstanceFollowRedirects(true);
                    if (!isCancelled()) {
                        this.httpConnection.connect();
                        httpConnectionStream = this.httpConnection.getInputStream();
                        this.fileOutputStream = new RandomAccessFile(this.cacheImage.tempFilePath, "rws");
                    }
                } catch (Throwable e) {
                    if (e instanceof SocketTimeoutException) {
                        if (ConnectionsManager.isNetworkOnline()) {
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
                    FileLog.m14e(e);
                }
            }
            if (!isCancelled()) {
                try {
                    if (this.httpConnection != null && (this.httpConnection instanceof HttpURLConnection)) {
                        int code = this.httpConnection.getResponseCode();
                        if (!(code == Callback.DEFAULT_DRAG_ANIMATION_DURATION || code == 202 || code == 304)) {
                            this.canRetry = false;
                        }
                    }
                } catch (Throwable e2) {
                    FileLog.m14e(e2);
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
                        FileLog.m14e(e22);
                    }
                }
                if (httpConnectionStream != null) {
                    try {
                        byte[] data = new byte[MessagesController.UPDATE_MASK_CHANNEL];
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
                        FileLog.m14e(e222);
                    } catch (Throwable e2222) {
                        FileLog.m14e(e2222);
                    }
                }
            }
            try {
                if (this.fileOutputStream != null) {
                    this.fileOutputStream.close();
                    this.fileOutputStream = null;
                }
            } catch (Throwable e22222) {
                FileLog.m14e(e22222);
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
                    FileLog.m14e(e222222);
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
                NotificationCenter.getInstance(this.cacheImage.currentAccount).postNotificationName(NotificationCenter.FileDidLoaded, this.cacheImage.url);
                return;
            }
            NotificationCenter.getInstance(this.cacheImage.currentAccount).postNotificationName(NotificationCenter.FileDidFailedLoad, this.cacheImage.url, Integer.valueOf(2));
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
            NotificationCenter.getInstance(this.cacheImage.currentAccount).postNotificationName(NotificationCenter.FileDidFailedLoad, this.cacheImage.url, Integer.valueOf(1));
        }
    }

    private class ThumbGenerateInfo {
        private int count;
        private FileLocation fileLocation;
        private String filter;

        private ThumbGenerateInfo() {
        }

        /* synthetic */ ThumbGenerateInfo(ImageLoader x0, CLASSNAME x1) {
            this();
        }
    }

    private class ThumbGenerateTask implements Runnable {
        private String filter;
        private int mediaType;
        private File originalPath;
        private FileLocation thumbLocation;

        public ThumbGenerateTask(int type, File path, FileLocation location, String f) {
            this.mediaType = type;
            this.originalPath = path;
            this.thumbLocation = location;
            this.filter = f;
        }

        private void removeTask() {
            if (this.thumbLocation != null) {
                ImageLoader.this.imageLoadQueue.postRunnable(new ImageLoader$ThumbGenerateTask$$Lambda$0(this, FileLoader.getAttachFileName(this.thumbLocation)));
            }
        }

        final /* synthetic */ void lambda$removeTask$0$ImageLoader$ThumbGenerateTask(String name) {
            ThumbGenerateTask thumbGenerateTask = (ThumbGenerateTask) ImageLoader.this.thumbGenerateTasks.remove(name);
        }

        public void run() {
            try {
                if (this.thumbLocation == null) {
                    removeTask();
                    return;
                }
                String key = this.thumbLocation.volume_id + "_" + this.thumbLocation.local_id;
                File thumbFile = new File(FileLoader.getDirectory(4), "q_" + key + ".jpg");
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
                AndroidUtilities.runOnUIThread(new ImageLoader$ThumbGenerateTask$$Lambda$1(this, key, new BitmapDrawable(originalBitmap)));
            } catch (Throwable e) {
                FileLog.m14e(e);
            } catch (Throwable e2) {
                FileLog.m14e(e2);
                removeTask();
            }
        }

        final /* synthetic */ void lambda$run$1$ImageLoader$ThumbGenerateTask(String key, BitmapDrawable bitmapDrawable) {
            removeTask();
            String kf = key;
            if (this.filter != null) {
                kf = kf + "@" + this.filter;
            }
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.messageThumbGenerated, bitmapDrawable, kf);
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
                if (ImageLoader.this.ignoreRemoval == null || key == null || !ImageLoader.this.ignoreRemoval.equals(key)) {
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
                FileLog.m14e(e);
            }
        }
        try {
            new File(cachePath, ".nomedia").createNewFile();
        } catch (Throwable e2) {
            FileLog.m14e(e2);
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
                    NotificationCenter.getInstance(currentAccount).postNotificationName(NotificationCenter.FileDidLoaded, location);
                    ImageLoader.this.fileDidLoaded(location, finalFile, type);
                }

                public void fileDidFailedLoad(String location, int canceled) {
                    ImageLoader.this.fileProgresses.remove(location);
                    AndroidUtilities.runOnUIThread(new ImageLoader$2$$Lambda$4(this, location, canceled, currentAccount));
                }

                final /* synthetic */ void lambda$fileDidFailedLoad$6$ImageLoader$2(String location, int canceled, int currentAccount) {
                    ImageLoader.this.fileDidFailedLoad(location, canceled);
                    NotificationCenter.getInstance(currentAccount).postNotificationName(NotificationCenter.FileDidFailedLoad, location, Integer.valueOf(canceled));
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
        BroadcastReceiver receiver = new CLASSNAME();
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
        AndroidUtilities.runOnUIThread(new ImageLoader$$Lambda$10(createMediaPaths()));
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
                FileLog.m14e(e);
            }
        }
        try {
            new File(cachePath, ".nomedia").createNewFile();
        } catch (Throwable e2) {
            FileLog.m14e(e2);
        }
        mediaDirs.put(4, cachePath);
        if (BuildVars.LOGS_ENABLED) {
            FileLog.m11d("cache path = " + cachePath);
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
                                FileLog.m11d("image path = " + imagePath);
                            }
                        }
                    } catch (Throwable e22) {
                        FileLog.m14e(e22);
                    }
                    try {
                        File videoPath = new File(this.telegramPath, "Telegram Video");
                        videoPath.mkdir();
                        if (videoPath.isDirectory() && canMoveFiles(cachePath, videoPath, 2)) {
                            mediaDirs.put(2, videoPath);
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.m11d("video path = " + videoPath);
                            }
                        }
                    } catch (Throwable e222) {
                        FileLog.m14e(e222);
                    }
                    try {
                        File audioPath = new File(this.telegramPath, "Telegram Audio");
                        audioPath.mkdir();
                        if (audioPath.isDirectory() && canMoveFiles(cachePath, audioPath, 1)) {
                            new File(audioPath, ".nomedia").createNewFile();
                            mediaDirs.put(1, audioPath);
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.m11d("audio path = " + audioPath);
                            }
                        }
                    } catch (Throwable e2222) {
                        FileLog.m14e(e2222);
                    }
                    try {
                        File documentPath = new File(this.telegramPath, "Telegram Documents");
                        documentPath.mkdir();
                        if (documentPath.isDirectory() && canMoveFiles(cachePath, documentPath, 3)) {
                            new File(documentPath, ".nomedia").createNewFile();
                            mediaDirs.put(3, documentPath);
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.m11d("documents path = " + documentPath);
                            }
                        }
                    } catch (Throwable e22222) {
                        FileLog.m14e(e22222);
                    }
                }
            } else if (BuildVars.LOGS_ENABLED) {
                FileLog.m11d("this Android can't rename files");
            }
            SharedConfig.checkSaveToGalleryFiles();
        } catch (Throwable e222222) {
            FileLog.m14e(e222222);
        }
        return mediaDirs;
    }

    /* JADX WARNING: Removed duplicated region for block: B:51:0x0098 A:{SYNTHETIC, Splitter: B:51:0x0098} */
    /* JADX WARNING: Removed duplicated region for block: B:51:0x0098 A:{SYNTHETIC, Splitter: B:51:0x0098} */
    /* JADX WARNING: Removed duplicated region for block: B:57:0x00a4 A:{SYNTHETIC, Splitter: B:57:0x00a4} */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean canMoveFiles(File from, File to, int type) {
        Throwable e;
        Throwable th;
        RandomAccessFile file = null;
        File srcFile = null;
        File dstFile = null;
        File srcFile2;
        if (type == 0) {
            try {
                srcFile2 = new File(from, "000000000_999999_temp.jpg");
                try {
                    dstFile = new File(to, "000000000_999999.jpg");
                    srcFile = srcFile2;
                } catch (Exception e2) {
                    e = e2;
                    srcFile = srcFile2;
                    try {
                        FileLog.m14e(e);
                        if (file != null) {
                        }
                        return false;
                    } catch (Throwable th2) {
                        th = th2;
                        if (file != null) {
                            try {
                                file.close();
                            } catch (Throwable e3) {
                                FileLog.m14e(e3);
                            }
                        }
                        throw th;
                    }
                }
            } catch (Exception e4) {
                e3 = e4;
                FileLog.m14e(e3);
                if (file != null) {
                    try {
                        file.close();
                    } catch (Throwable e32) {
                        FileLog.m14e(e32);
                    }
                }
                return false;
            }
        } else if (type == 3) {
            srcFile2 = new File(from, "000000000_999999_temp.doc");
            dstFile = new File(to, "000000000_999999.doc");
            srcFile = srcFile2;
        } else if (type == 1) {
            srcFile2 = new File(from, "000000000_999999_temp.ogg");
            dstFile = new File(to, "000000000_999999.ogg");
            srcFile = srcFile2;
        } else if (type == 2) {
            srcFile2 = new File(from, "000000000_999999_temp.mp4");
            dstFile = new File(to, "000000000_999999.mp4");
            srcFile = srcFile2;
        }
        byte[] buffer = new byte[1024];
        srcFile.createNewFile();
        RandomAccessFile file2 = new RandomAccessFile(srcFile, "rws");
        try {
            file2.write(buffer);
            file2.close();
            file = null;
            boolean canRename = srcFile.renameTo(dstFile);
            srcFile.delete();
            dstFile.delete();
            if (!canRename) {
                if (file != null) {
                    try {
                        file.close();
                    } catch (Throwable e322) {
                        FileLog.m14e(e322);
                    }
                }
                return false;
            } else if (file == null) {
                return true;
            } else {
                try {
                    file.close();
                    return true;
                } catch (Throwable e3222) {
                    FileLog.m14e(e3222);
                    return true;
                }
            }
        } catch (Exception e5) {
            e3222 = e5;
            file = file2;
            FileLog.m14e(e3222);
            if (file != null) {
            }
            return false;
        } catch (Throwable th3) {
            th = th3;
            file = file2;
            if (file != null) {
            }
            throw th;
        }
    }

    public Float getFileProgress(String location) {
        if (location == null) {
            return null;
        }
        return (Float) this.fileProgresses.get(location);
    }

    public String getReplacedKey(String oldKey) {
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

    private void removeFromWaitingForThumb(int TAG) {
        String location = (String) this.waitingForQualityThumbByTag.get(TAG);
        if (location != null) {
            ThumbGenerateInfo info = (ThumbGenerateInfo) this.waitingForQualityThumb.get(location);
            if (info != null) {
                info.count = info.count - 1;
                if (info.count == 0) {
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
                removeFromWaitingForThumb(TAG);
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
            if (location2.version == 0) {
                key = location2.dc_id + "_" + location2.var_id;
            } else {
                key = location2.dc_id + "_" + location2.var_id + "_" + location2.version;
            }
        } else if (fileLocation instanceof SecureDocument) {
            SecureDocument location3 = (SecureDocument) fileLocation;
            key = location3.secureFile.dc_id + "_" + location3.secureFile.var_id;
        } else if (fileLocation instanceof WebFile) {
            key = Utilities.MD5(((WebFile) fileLocation).url);
        }
        if (filter != null) {
            key = key + "@" + filter;
        }
        return this.memCache.get(key);
    }

    private void replaceImageInCacheInternal(String oldKey, String newKey, FileLocation newLocation) {
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

    public void replaceImageInCache(String oldKey, String newKey, FileLocation newLocation, boolean post) {
        if (post) {
            AndroidUtilities.runOnUIThread(new ImageLoader$$Lambda$2(this, oldKey, newKey, newLocation));
        } else {
            replaceImageInCacheInternal(oldKey, newKey, newLocation);
        }
    }

    public void putImageToCache(BitmapDrawable bitmap, String key) {
        this.memCache.put(key, bitmap);
    }

    private void generateThumb(int mediaType, File originalPath, FileLocation thumbLocation, String filter) {
        if ((mediaType == 0 || mediaType == 2 || mediaType == 3) && originalPath != null && thumbLocation != null) {
            if (((ThumbGenerateTask) this.thumbGenerateTasks.get(FileLoader.getAttachFileName(thumbLocation))) == null) {
                this.thumbGeneratingQueue.postRunnable(new ThumbGenerateTask(mediaType, originalPath, thumbLocation, filter));
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
                if (this.lastImageNum == ConnectionsManager.DEFAULT_DATACENTER_ID) {
                    this.lastImageNum = 0;
                }
            }
            this.imageLoadQueue.postRunnable(new ImageLoader$$Lambda$4(this, thumb, url, key, TAG, imageReceiver, filter, httpLocation, imageReceiver.isNeedsQualityThumb(), imageReceiver.getParentMessageObject(), imageLocation, imageReceiver.isShouldGenerateQualityThumb(), cacheType, size, ext, imageReceiver.getcurrentAccount()));
        }
    }

    final /* synthetic */ void lambda$createLoadOperationForImageReceiver$5$ImageLoader(int thumb, String url, String key, int finalTag, ImageReceiver imageReceiver, String filter, String httpLocation, boolean finalIsNeedsQualityThumb, MessageObject parentMessageObject, TLObject imageLocation, boolean shouldGenerateQualityThumb, int cacheType, int size, String ext, int currentAccount) {
        boolean added = false;
        if (thumb != 2) {
            CacheImage alreadyLoadingUrl = (CacheImage) this.imageLoadingByUrl.get(url);
            CacheImage alreadyLoadingCache = (CacheImage) this.imageLoadingByKeys.get(key);
            CacheImage alreadyLoadingImage = (CacheImage) this.imageLoadingByTag.get(finalTag);
            if (alreadyLoadingImage != null) {
                if (alreadyLoadingImage == alreadyLoadingCache) {
                    added = true;
                } else if (alreadyLoadingImage == alreadyLoadingUrl) {
                    if (alreadyLoadingCache == null) {
                        alreadyLoadingImage.replaceImageReceiver(imageReceiver, key, filter, thumb != 0);
                    }
                    added = true;
                } else {
                    alreadyLoadingImage.removeImageReceiver(imageReceiver);
                }
            }
            if (!(added || alreadyLoadingCache == null)) {
                alreadyLoadingCache.addImageReceiver(imageReceiver, key, filter, thumb != 0);
                added = true;
            }
            if (!(added || alreadyLoadingUrl == null)) {
                alreadyLoadingUrl.addImageReceiver(imageReceiver, key, filter, thumb != 0);
                added = true;
            }
        }
        if (!added) {
            boolean onlyCache = false;
            File cacheFile = null;
            boolean cacheFileExists = false;
            if (httpLocation != null) {
                if (!httpLocation.startsWith("http")) {
                    onlyCache = true;
                    int idx;
                    if (httpLocation.startsWith("thumb://")) {
                        idx = httpLocation.indexOf(":", 8);
                        if (idx >= 0) {
                            cacheFile = new File(httpLocation.substring(idx + 1));
                        }
                    } else if (httpLocation.startsWith("vthumb://")) {
                        idx = httpLocation.indexOf(":", 9);
                        if (idx >= 0) {
                            cacheFile = new File(httpLocation.substring(idx + 1));
                        }
                    } else {
                        cacheFile = new File(httpLocation);
                    }
                }
            } else if (thumb != 0) {
                if (finalIsNeedsQualityThumb) {
                    cacheFile = new File(FileLoader.getDirectory(4), "q_" + url);
                    if (cacheFile.exists()) {
                        cacheFileExists = true;
                    } else {
                        cacheFile = null;
                    }
                }
                if (parentMessageObject != null) {
                    File attachPath = null;
                    if (parentMessageObject.messageOwner.attachPath != null && parentMessageObject.messageOwner.attachPath.length() > 0) {
                        attachPath = new File(parentMessageObject.messageOwner.attachPath);
                        if (!attachPath.exists()) {
                            attachPath = null;
                        }
                    }
                    if (attachPath == null) {
                        attachPath = FileLoader.getPathToMessage(parentMessageObject.messageOwner);
                    }
                    if (finalIsNeedsQualityThumb && cacheFile == null) {
                        String location = parentMessageObject.getFileName();
                        ThumbGenerateInfo info = (ThumbGenerateInfo) this.waitingForQualityThumb.get(location);
                        if (info == null) {
                            ThumbGenerateInfo thumbGenerateInfo = new ThumbGenerateInfo(this, null);
                            thumbGenerateInfo.fileLocation = (FileLocation) imageLocation;
                            thumbGenerateInfo.filter = filter;
                            this.waitingForQualityThumb.put(location, thumbGenerateInfo);
                        }
                        info.count = info.count + 1;
                        this.waitingForQualityThumbByTag.put(finalTag, location);
                    }
                    if (attachPath.exists() && shouldGenerateQualityThumb) {
                        generateThumb(parentMessageObject.getFileType(), attachPath, (FileLocation) imageLocation, filter);
                    }
                }
            }
            if (thumb != 2) {
                boolean isEncrypted = (imageLocation instanceof TL_documentEncrypted) || (imageLocation instanceof TL_fileEncryptedLocation);
                CacheImage img = new CacheImage(this, null);
                if (httpLocation != null && !httpLocation.startsWith("vthumb") && !httpLocation.startsWith("thumb")) {
                    String trueExt = getHttpUrlExtension(httpLocation, "jpg");
                    if (trueExt.equals("mp4") || trueExt.equals("gif")) {
                        img.animatedFile = true;
                    }
                } else if (((imageLocation instanceof WebFile) && MessageObject.isGifDocument((WebFile) imageLocation)) || ((imageLocation instanceof Document) && (MessageObject.isGifDocument((Document) imageLocation) || MessageObject.isRoundVideoDocument((Document) imageLocation)))) {
                    img.animatedFile = true;
                }
                if (cacheFile == null) {
                    if (imageLocation instanceof SecureDocument) {
                        img.secureDocument = (SecureDocument) imageLocation;
                        onlyCache = img.secureDocument.secureFile.dc_id == Integer.MIN_VALUE;
                        cacheFile = new File(FileLoader.getDirectory(4), url);
                    } else if (cacheType != 0 || size <= 0 || httpLocation != null || isEncrypted) {
                        cacheFile = new File(FileLoader.getDirectory(4), url);
                        if (cacheFile.exists()) {
                            cacheFileExists = true;
                        } else if (cacheType == 2) {
                            cacheFile = new File(FileLoader.getDirectory(4), url + ".enc");
                        }
                    } else {
                        cacheFile = imageLocation instanceof Document ? MessageObject.isVideoDocument((Document) imageLocation) ? new File(FileLoader.getDirectory(2), url) : new File(FileLoader.getDirectory(3), url) : imageLocation instanceof WebFile ? new File(FileLoader.getDirectory(3), url) : new File(FileLoader.getDirectory(0), url);
                    }
                }
                img.selfThumb = thumb != 0;
                img.key = key;
                img.filter = filter;
                img.httpUrl = httpLocation;
                img.ext = ext;
                img.currentAccount = currentAccount;
                if (cacheType == 2) {
                    img.encryptionKeyPath = new File(FileLoader.getInternalCacheDir(), url + ".enc.key");
                }
                img.addImageReceiver(imageReceiver, key, filter, thumb != 0);
                if (onlyCache || cacheFileExists || cacheFile.exists()) {
                    img.finalFilePath = cacheFile;
                    img.cacheTask = new CacheOutTask(img);
                    this.imageLoadingByKeys.put(key, img);
                    if (thumb != 0) {
                        this.cacheThumbOutQueue.postRunnable(img.cacheTask);
                        return;
                    } else {
                        this.cacheOutQueue.postRunnable(img.cacheTask);
                        return;
                    }
                }
                img.url = url;
                img.location = imageLocation;
                this.imageLoadingByUrl.put(url, img);
                if (httpLocation == null) {
                    if (imageLocation instanceof FileLocation) {
                        FileLocation location2 = (FileLocation) imageLocation;
                        int localCacheType = cacheType;
                        if (localCacheType == 0 && (size <= 0 || location2.key != null)) {
                            localCacheType = 1;
                        }
                        FileLoader.getInstance(currentAccount).loadFile(location2, ext, size, localCacheType);
                    } else if (imageLocation instanceof Document) {
                        FileLoader.getInstance(currentAccount).loadFile((Document) imageLocation, true, cacheType);
                    } else if (imageLocation instanceof SecureDocument) {
                        FileLoader.getInstance(currentAccount).loadFile((SecureDocument) imageLocation, true);
                    } else if (imageLocation instanceof WebFile) {
                        FileLoader.getInstance(currentAccount).loadFile((WebFile) imageLocation, true, cacheType);
                    }
                    if (imageReceiver.isForceLoding()) {
                        this.forceLoadingImages.put(img.key, Integer.valueOf(0));
                        return;
                    }
                    return;
                }
                img.tempFilePath = new File(FileLoader.getDirectory(4), Utilities.MD5(httpLocation) + "_temp.jpg");
                img.finalFilePath = cacheFile;
                img.httpTask = new HttpImageTask(img, size);
                this.httpTasks.add(img.httpTask);
                runHttpTasks(false);
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:74:0x02b2  */
    /* JADX WARNING: Removed duplicated region for block: B:81:0x02df  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void loadImageForImageReceiver(ImageReceiver imageReceiver) {
        if (imageReceiver != null) {
            BitmapDrawable bitmapDrawable;
            boolean imageSet = false;
            String key = imageReceiver.getKey();
            if (key != null) {
                bitmapDrawable = this.memCache.get(key);
                if (bitmapDrawable != null) {
                    cancelLoadingForImageReceiver(imageReceiver, 0);
                    imageReceiver.setImageBitmapByKey(bitmapDrawable, key, false, true);
                    imageSet = true;
                    if (!imageReceiver.isForcePreview()) {
                        return;
                    }
                }
            }
            boolean thumbSet = false;
            String thumbKey = imageReceiver.getThumbKey();
            if (thumbKey != null) {
                bitmapDrawable = this.memCache.get(thumbKey);
                if (bitmapDrawable != null) {
                    imageReceiver.setImageBitmapByKey(bitmapDrawable, thumbKey, true, true);
                    cancelLoadingForImageReceiver(imageReceiver, 1);
                    if (!imageSet || !imageReceiver.isForcePreview()) {
                        thumbSet = true;
                    } else {
                        return;
                    }
                }
            }
            TLObject thumbLocation = imageReceiver.getThumbLocation();
            TLObject imageLocation = imageReceiver.getImageLocation();
            String httpLocation = imageReceiver.getHttpImageLocation();
            boolean saveImageToCache = false;
            String url = null;
            String thumbUrl = null;
            key = null;
            thumbKey = null;
            String ext = imageReceiver.getExt();
            if (ext == null) {
                ext = "jpg";
            }
            if (httpLocation != null) {
                key = Utilities.MD5(httpLocation);
                url = key + "." + getHttpUrlExtension(httpLocation, "jpg");
            } else if (imageLocation != null) {
                if (imageLocation instanceof FileLocation) {
                    FileLocation location = (FileLocation) imageLocation;
                    key = location.volume_id + "_" + location.local_id;
                    url = key + "." + ext;
                    if (!(imageReceiver.getExt() == null && location.key == null && (location.volume_id != -2147483648L || location.local_id >= 0))) {
                        saveImageToCache = true;
                    }
                } else if (imageLocation instanceof WebFile) {
                    WebFile document = (WebFile) imageLocation;
                    String defaultExt = FileLoader.getExtensionByMime(document.mime_type);
                    key = Utilities.MD5(document.url);
                    url = key + "." + getHttpUrlExtension(document.url, defaultExt);
                } else if (imageLocation instanceof SecureDocument) {
                    SecureDocument document2 = (SecureDocument) imageLocation;
                    key = document2.secureFile.dc_id + "_" + document2.secureFile.var_id;
                    url = key + "." + ext;
                    if (null != null) {
                        thumbUrl = null + "." + ext;
                    }
                } else if (imageLocation instanceof Document) {
                    Document document3 = (Document) imageLocation;
                    if (document3.var_id != 0 && document3.dc_id != 0) {
                        if (document3.version == 0) {
                            key = document3.dc_id + "_" + document3.var_id;
                        } else {
                            key = document3.dc_id + "_" + document3.var_id + "_" + document3.version;
                        }
                        String docExt = FileLoader.getDocumentFileName(document3);
                        if (docExt != null) {
                            int idx = docExt.lastIndexOf(46);
                            if (idx != -1) {
                                docExt = docExt.substring(idx);
                                if (docExt.length() <= 1) {
                                    if (document3.mime_type == null || !document3.mime_type.equals(MimeTypes.VIDEO_MP4)) {
                                        docExt = TtmlNode.ANONYMOUS_REGION_ID;
                                    } else {
                                        docExt = ".mp4";
                                    }
                                }
                                url = key + docExt;
                                if (null != null) {
                                    thumbUrl = null + "." + ext;
                                }
                                saveImageToCache = MessageObject.isGifDocument(document3) && !MessageObject.isRoundVideoDocument((Document) imageLocation);
                            }
                        }
                        docExt = TtmlNode.ANONYMOUS_REGION_ID;
                        if (docExt.length() <= 1) {
                        }
                        url = key + docExt;
                        if (null != null) {
                        }
                        if (MessageObject.isGifDocument(document3)) {
                        }
                    } else {
                        return;
                    }
                }
                if (imageLocation == thumbLocation) {
                    imageLocation = null;
                    key = null;
                    url = null;
                }
            }
            if (thumbLocation != null) {
                thumbKey = thumbLocation.volume_id + "_" + thumbLocation.local_id;
                thumbUrl = thumbKey + "." + ext;
            }
            String filter = imageReceiver.getFilter();
            String thumbFilter = imageReceiver.getThumbFilter();
            if (!(key == null || filter == null)) {
                key = key + "@" + filter;
            }
            if (!(thumbKey == null || thumbFilter == null)) {
                thumbKey = thumbKey + "@" + thumbFilter;
            }
            if (httpLocation != null) {
                createLoadOperationForImageReceiver(imageReceiver, thumbKey, thumbUrl, ext, thumbLocation, null, thumbFilter, 0, 1, thumbSet ? 2 : 1);
                createLoadOperationForImageReceiver(imageReceiver, key, url, ext, null, httpLocation, filter, 0, 1, 0);
                return;
            }
            int i;
            int cacheType = imageReceiver.getCacheType();
            if (cacheType == 0 && saveImageToCache) {
                cacheType = 1;
            }
            if (cacheType == 0) {
                i = 1;
            } else {
                i = cacheType;
            }
            createLoadOperationForImageReceiver(imageReceiver, thumbKey, thumbUrl, ext, thumbLocation, null, thumbFilter, 0, i, thumbSet ? 2 : 1);
            createLoadOperationForImageReceiver(imageReceiver, key, url, ext, imageLocation, null, filter, imageReceiver.getSize(), cacheType, 0);
        }
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

    private void fileDidLoaded(String location, File finalFile, int type) {
        this.imageLoadQueue.postRunnable(new ImageLoader$$Lambda$6(this, location, type, finalFile));
    }

    final /* synthetic */ void lambda$fileDidLoaded$7$ImageLoader(String location, int type, File finalFile) {
        ThumbGenerateInfo info = (ThumbGenerateInfo) this.waitingForQualityThumb.get(location);
        if (info != null) {
            generateThumb(type, finalFile, info.fileLocation, info.filter);
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
            this.imageLoadQueue.postRunnable(new ImageLoader$$Lambda$7(this, location));
        }
    }

    final /* synthetic */ void lambda$fileDidFailedLoad$8$ImageLoader(String location) {
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
            ((HttpImageTask) this.httpTasks.poll()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
            this.currentHttpTasksCount++;
        }
    }

    public boolean isLoadingHttpFile(String url) {
        return this.httpFileLoadTasksByKeys.containsKey(url);
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
        AndroidUtilities.runOnUIThread(new ImageLoader$$Lambda$8(this, oldTask, reason));
    }

    final /* synthetic */ void lambda$runHttpFileLoadTasks$10$ImageLoader(HttpFileTask oldTask, int reason) {
        if (oldTask != null) {
            this.currentHttpFileLoadTasksCount--;
        }
        if (oldTask != null) {
            if (reason == 1) {
                if (oldTask.canRetry) {
                    Runnable runnable = new ImageLoader$$Lambda$9(this, new HttpFileTask(oldTask.url, oldTask.tempFile, oldTask.ext, oldTask.currentAccount));
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
                NotificationCenter.getInstance(oldTask.currentAccount).postNotificationName(NotificationCenter.httpFileDidLoaded, oldTask.url, result);
            }
        }
        while (this.currentHttpFileLoadTasksCount < 2 && !this.httpFileLoadTasks.isEmpty()) {
            ((HttpFileTask) this.httpFileLoadTasks.poll()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
            this.currentHttpFileLoadTasksCount++;
        }
    }

    final /* synthetic */ void lambda$null$9$ImageLoader(HttpFileTask newTask) {
        this.httpFileLoadTasks.add(newTask);
        runHttpFileLoadTasks(null, 0);
    }

    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static Bitmap loadBitmap(String path, Uri uri, float maxWidth, float maxHeight, boolean useMaxScale) {
        float scaleFactor;
        Options bmOptions = new Options();
        bmOptions.inJustDecodeBounds = true;
        InputStream inputStream = null;
        if (!(path != null || uri == null || uri.getScheme() == null)) {
            if (uri.getScheme().contains("file")) {
                path = uri.getPath();
            } else {
                try {
                    path = AndroidUtilities.getPath(uri);
                } catch (Throwable e) {
                    FileLog.m14e(e);
                }
            }
        }
        if (path != null) {
            BitmapFactory.decodeFile(path, bmOptions);
        } else if (uri != null) {
            try {
                inputStream = ApplicationLoader.applicationContext.getContentResolver().openInputStream(uri);
                BitmapFactory.decodeStream(inputStream, null, bmOptions);
                inputStream.close();
                inputStream = ApplicationLoader.applicationContext.getContentResolver().openInputStream(uri);
            } catch (Throwable e2) {
                FileLog.m14e(e2);
                return null;
            }
        }
        float photoW = (float) bmOptions.outWidth;
        float photoH = (float) bmOptions.outHeight;
        if (useMaxScale) {
            scaleFactor = Math.max(photoW / maxWidth, photoH / maxHeight);
        } else {
            scaleFactor = Math.min(photoW / maxWidth, photoH / maxHeight);
        }
        if (scaleFactor < 1.0f) {
            scaleFactor = 1.0f;
        }
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = (int) scaleFactor;
        if (bmOptions.inSampleSize % 2 != 0) {
            int sample = 1;
            while (sample * 2 < bmOptions.inSampleSize) {
                sample *= 2;
            }
            bmOptions.inSampleSize = sample;
        }
        bmOptions.inPurgeable = VERSION.SDK_INT < 21;
        String exifPath = null;
        if (path != null) {
            exifPath = path;
        } else if (uri != null) {
            exifPath = AndroidUtilities.getPath(uri);
        }
        Matrix matrix = null;
        if (exifPath != null) {
            try {
                int orientation = new ExifInterface(exifPath).getAttributeInt("Orientation", 1);
                Matrix matrix2 = new Matrix();
                switch (orientation) {
                    case 3:
                        matrix2.postRotate(180.0f);
                    case 6:
                        try {
                            matrix2.postRotate(90.0f);
                        } catch (Throwable th) {
                            matrix = matrix2;
                            break;
                        }
                    case 8:
                        matrix2.postRotate(270.0f);
                        matrix = matrix2;
                        break;
                }
                matrix = matrix2;
            } catch (Throwable th2) {
            }
        }
        Bitmap b = null;
        Bitmap newBitmap;
        if (path != null) {
            try {
                b = BitmapFactory.decodeFile(path, bmOptions);
                if (b == null) {
                    return b;
                }
                if (bmOptions.inPurgeable) {
                    Utilities.pinBitmap(b);
                }
                newBitmap = Bitmaps.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), matrix, true);
                if (newBitmap == b) {
                    return b;
                }
                b.recycle();
                return newBitmap;
            } catch (Throwable e22) {
                FileLog.m14e(e22);
                return null;
            }
        } else if (uri == null) {
            return null;
        } else {
            try {
                b = BitmapFactory.decodeStream(inputStream, null, bmOptions);
                if (b != null) {
                    if (bmOptions.inPurgeable) {
                        Utilities.pinBitmap(b);
                    }
                    newBitmap = Bitmaps.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), matrix, true);
                    if (newBitmap != b) {
                        b.recycle();
                        b = newBitmap;
                    }
                }
                try {
                    inputStream.close();
                    return b;
                } catch (Throwable e23) {
                    FileLog.m14e(e23);
                    return b;
                }
            } catch (Throwable e232) {
                FileLog.m14e(e232);
                return null;
            }
        }
    }

    public static void fillPhotoSizeWithBytes(PhotoSize photoSize) {
        if (photoSize != null && photoSize.bytes == null) {
            try {
                RandomAccessFile f = new RandomAccessFile(FileLoader.getPathToAttach(photoSize, true), "r");
                if (((int) f.length()) < 20000) {
                    photoSize.bytes = new byte[((int) f.length())];
                    f.readFully(photoSize.bytes, 0, photoSize.bytes.length);
                }
            } catch (Throwable e) {
                FileLog.m14e(e);
            }
        }
    }

    private static PhotoSize scaleAndSaveImageInternal(Bitmap bitmap, int w, int h, float photoW, float photoH, float scaleFactor, int quality, boolean cache, boolean scaleAnyway) throws Exception {
        Bitmap scaledBitmap;
        if (scaleFactor > 1.0f || scaleAnyway) {
            scaledBitmap = Bitmaps.createScaledBitmap(bitmap, w, h, true);
        } else {
            scaledBitmap = bitmap;
        }
        TL_fileLocation location = new TL_fileLocation();
        location.volume_id = -2147483648L;
        location.dc_id = Integer.MIN_VALUE;
        location.local_id = SharedConfig.getLastLocalId();
        PhotoSize size = new TL_photoSize();
        size.location = location;
        size.var_w = scaledBitmap.getWidth();
        size.var_h = scaledBitmap.getHeight();
        if (size.var_w <= 100 && size.var_h <= 100) {
            size.type = "s";
        } else if (size.var_w <= 320 && size.var_h <= 320) {
            size.type = "m";
        } else if (size.var_w <= 800 && size.var_h <= 800) {
            size.type = "x";
        } else if (size.var_w > 1280 || size.var_h > 1280) {
            size.type = "w";
        } else {
            size.type = "y";
        }
        FileOutputStream stream = new FileOutputStream(new File(FileLoader.getDirectory(4), location.volume_id + "_" + location.local_id + ".jpg"));
        scaledBitmap.compress(CompressFormat.JPEG, quality, stream);
        if (cache) {
            ByteArrayOutputStream stream2 = new ByteArrayOutputStream();
            scaledBitmap.compress(CompressFormat.JPEG, quality, stream2);
            size.bytes = stream2.toByteArray();
            size.size = size.bytes.length;
            stream2.close();
        } else {
            size.size = (int) stream.getChannel().size();
        }
        stream.close();
        if (scaledBitmap != bitmap) {
            scaledBitmap.recycle();
        }
        return size;
    }

    public static PhotoSize scaleAndSaveImage(Bitmap bitmap, float maxWidth, float maxHeight, int quality, boolean cache) {
        return scaleAndSaveImage(bitmap, maxWidth, maxHeight, quality, cache, 0, 0);
    }

    public static PhotoSize scaleAndSaveImage(Bitmap bitmap, float maxWidth, float maxHeight, int quality, boolean cache, int minWidth, int minHeight) {
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
            return scaleAndSaveImageInternal(bitmap, w, h, photoW, photoH, scaleFactor, quality, cache, scaleAnyway);
        } catch (Throwable e2) {
            FileLog.m14e(e2);
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
            if (message.media.document.thumb instanceof TL_photoCachedSize) {
                photoSize = message.media.document.thumb;
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
            if (photoSize.location instanceof TL_fileLocationUnavailable) {
                photoSize.location = new TL_fileLocation();
                photoSize.location.volume_id = -2147483648L;
                photoSize.location.dc_id = Integer.MIN_VALUE;
                photoSize.location.local_id = SharedConfig.getLastLocalId();
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
                        FileLog.m14e(e);
                    }
                }
                RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rws");
                randomAccessFile.write(photoSize.bytes);
                randomAccessFile.close();
            }
            PhotoSize newPhotoSize = new TL_photoSize();
            newPhotoSize.var_w = photoSize.var_w;
            newPhotoSize.var_h = photoSize.var_h;
            newPhotoSize.location = photoSize.location;
            newPhotoSize.size = photoSize.size;
            newPhotoSize.type = photoSize.type;
            if (message.media instanceof TL_messageMediaPhoto) {
                count = message.media.photo.sizes.size();
                for (a = 0; a < count; a++) {
                    if (message.media.photo.sizes.get(a) instanceof TL_photoCachedSize) {
                        message.media.photo.sizes.set(a, newPhotoSize);
                        return;
                    }
                }
            } else if (message.media instanceof TL_messageMediaDocument) {
                message.media.document.thumb = newPhotoSize;
            } else if (message.media instanceof TL_messageMediaWebPage) {
                count = message.media.webpage.photo.sizes.size();
                for (a = 0; a < count; a++) {
                    if (message.media.webpage.photo.sizes.get(a) instanceof TL_photoCachedSize) {
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
