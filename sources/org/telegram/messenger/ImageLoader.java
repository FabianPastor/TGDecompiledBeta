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
import org.telegram.messenger.exoplayer2.DefaultLoadControl;
import org.telegram.messenger.exoplayer2.util.MimeTypes;
import org.telegram.messenger.support.widget.helper.ItemTouchHelper.Callback;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.InputEncryptedFile;
import org.telegram.tgnet.TLRPC.InputFile;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.TL_fileLocation;
import org.telegram.tgnet.TLRPC.TL_fileLocationUnavailable;
import org.telegram.tgnet.TLRPC.TL_messageMediaDocument;
import org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;
import org.telegram.tgnet.TLRPC.TL_messageMediaWebPage;
import org.telegram.tgnet.TLRPC.TL_photoCachedSize;
import org.telegram.tgnet.TLRPC.TL_photoSize;
import org.telegram.tgnet.TLRPC.TL_webDocument;
import org.telegram.ui.Components.AnimatedFileDrawable;

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
    private HashMap<String, Runnable> retryHttpsTasks = new HashMap();
    private File telegramPath = null;
    private HashMap<String, ThumbGenerateTask> thumbGenerateTasks = new HashMap();
    private DispatchQueue thumbGeneratingQueue = new DispatchQueue("thumbGeneratingQueue");
    private HashMap<String, ThumbGenerateInfo> waitingForQualityThumb = new HashMap();
    private SparseArray<String> waitingForQualityThumbByTag = new SparseArray();

    /* renamed from: org.telegram.messenger.ImageLoader$3 */
    class C01953 extends BroadcastReceiver {

        /* renamed from: org.telegram.messenger.ImageLoader$3$1 */
        class C01941 implements Runnable {
            C01941() {
            }

            public void run() {
                ImageLoader.this.checkMediaPaths();
            }
        }

        C01953() {
        }

        public void onReceive(Context arg0, Intent intent) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.m0d("file system changed");
            }
            Runnable r = new C01941();
            if ("android.intent.action.MEDIA_UNMOUNTED".equals(intent.getAction())) {
                AndroidUtilities.runOnUIThread(r, 1000);
            } else {
                r.run();
            }
        }
    }

    /* renamed from: org.telegram.messenger.ImageLoader$4 */
    class C01974 implements Runnable {
        C01974() {
        }

        public void run() {
            final SparseArray<File> paths = ImageLoader.this.createMediaPaths();
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    FileLoader.setMediaDirs(paths);
                }
            });
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
                    } else if (this.location instanceof TL_webDocument) {
                        FileLoader.getInstance(this.currentAccount).cancelLoadFile((TL_webDocument) this.location);
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

        public void setImageAndClear(final BitmapDrawable image) {
            if (image != null) {
                final ArrayList<ImageReceiver> finalImageReceiverArray = new ArrayList(this.imageReceiverArray);
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        int a;
                        if (image instanceof AnimatedFileDrawable) {
                            boolean imageSet = false;
                            BitmapDrawable fileDrawable = image;
                            a = 0;
                            while (a < finalImageReceiverArray.size()) {
                                if (((ImageReceiver) finalImageReceiverArray.get(a)).setImageBitmapByKey(a == 0 ? fileDrawable : fileDrawable.makeCopy(), CacheImage.this.key, CacheImage.this.selfThumb, false)) {
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
                            ((ImageReceiver) finalImageReceiverArray.get(a)).setImageBitmapByKey(image, CacheImage.this.key, CacheImage.this.selfThumb, false);
                        }
                    }
                });
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
    }

    private class CacheOutTask implements Runnable {
        private CacheImage cacheImage;
        private boolean isCancelled;
        private Thread runningThread;
        private final Object sync = new Object();

        public CacheOutTask(CacheImage image) {
            this.cacheImage = image;
        }

        public void run() {
            /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Unknown predecessor block by arg (r33_0 'is' java.io.InputStream) in PHI: PHI: (r33_1 'is' java.io.InputStream) = (r33_0 'is' java.io.InputStream), (r33_2 'is' java.io.InputStream) binds: {(r33_0 'is' java.io.InputStream)=B:150:0x0280, (r33_2 'is' java.io.InputStream)=B:158:0x02a6}
	at jadx.core.dex.instructions.PhiInsn.replaceArg(PhiInsn.java:79)
	at jadx.core.dex.visitors.ModVisitor.processInvoke(ModVisitor.java:222)
	at jadx.core.dex.visitors.ModVisitor.replaceStep(ModVisitor.java:83)
	at jadx.core.dex.visitors.ModVisitor.visit(ModVisitor.java:68)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:60)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
            /*
            r51 = this;
            r0 = r51;
            r6 = r0.sync;
            monitor-enter(r6);
            r5 = java.lang.Thread.currentThread();	 Catch:{ all -> 0x0031 }
            r0 = r51;	 Catch:{ all -> 0x0031 }
            r0.runningThread = r5;	 Catch:{ all -> 0x0031 }
            java.lang.Thread.interrupted();	 Catch:{ all -> 0x0031 }
            r0 = r51;	 Catch:{ all -> 0x0031 }
            r5 = r0.isCancelled;	 Catch:{ all -> 0x0031 }
            if (r5 == 0) goto L_0x0018;	 Catch:{ all -> 0x0031 }
        L_0x0016:
            monitor-exit(r6);	 Catch:{ all -> 0x0031 }
        L_0x0017:
            return;	 Catch:{ all -> 0x0031 }
        L_0x0018:
            monitor-exit(r6);	 Catch:{ all -> 0x0031 }
            r0 = r51;
            r5 = r0.cacheImage;
            r5 = r5.animatedFile;
            if (r5 == 0) goto L_0x0067;
        L_0x0021:
            r0 = r51;
            r6 = r0.sync;
            monitor-enter(r6);
            r0 = r51;	 Catch:{ all -> 0x002e }
            r5 = r0.isCancelled;	 Catch:{ all -> 0x002e }
            if (r5 == 0) goto L_0x0034;	 Catch:{ all -> 0x002e }
        L_0x002c:
            monitor-exit(r6);	 Catch:{ all -> 0x002e }
            goto L_0x0017;	 Catch:{ all -> 0x002e }
        L_0x002e:
            r5 = move-exception;	 Catch:{ all -> 0x002e }
            monitor-exit(r6);	 Catch:{ all -> 0x002e }
            throw r5;
        L_0x0031:
            r5 = move-exception;
            monitor-exit(r6);	 Catch:{ all -> 0x0031 }
            throw r5;
        L_0x0034:
            monitor-exit(r6);	 Catch:{ all -> 0x002e }
            r28 = new org.telegram.ui.Components.AnimatedFileDrawable;
            r0 = r51;
            r5 = r0.cacheImage;
            r6 = r5.finalFilePath;
            r0 = r51;
            r5 = r0.cacheImage;
            r5 = r5.filter;
            if (r5 == 0) goto L_0x0065;
        L_0x0045:
            r0 = r51;
            r5 = r0.cacheImage;
            r5 = r5.filter;
            r7 = "d";
            r5 = r5.equals(r7);
            if (r5 == 0) goto L_0x0065;
        L_0x0054:
            r5 = 1;
        L_0x0055:
            r0 = r28;
            r0.<init>(r6, r5);
            java.lang.Thread.interrupted();
            r0 = r51;
            r1 = r28;
            r0.onPostExecute(r1);
            goto L_0x0017;
        L_0x0065:
            r5 = 0;
            goto L_0x0055;
        L_0x0067:
            r35 = 0;
            r36 = 0;
            r31 = 0;
            r0 = r51;
            r5 = r0.cacheImage;
            r0 = r5.finalFilePath;
            r21 = r0;
            r0 = r51;
            r5 = r0.cacheImage;
            r5 = r5.encryptionKeyPath;
            if (r5 == 0) goto L_0x0142;
        L_0x007d:
            if (r21 == 0) goto L_0x0142;
        L_0x007f:
            r5 = r21.getAbsolutePath();
            r6 = ".enc";
            r5 = r5.endsWith(r6);
            if (r5 == 0) goto L_0x0142;
        L_0x008c:
            r32 = 1;
        L_0x008e:
            r22 = 1;
            r49 = 0;
            r5 = android.os.Build.VERSION.SDK_INT;
            r6 = 19;
            if (r5 >= r6) goto L_0x00ec;
        L_0x0098:
            r43 = 0;
            r44 = new java.io.RandomAccessFile;	 Catch:{ Exception -> 0x0151 }
            r5 = "r";	 Catch:{ Exception -> 0x0151 }
            r0 = r44;	 Catch:{ Exception -> 0x0151 }
            r1 = r21;	 Catch:{ Exception -> 0x0151 }
            r0.<init>(r1, r5);	 Catch:{ Exception -> 0x0151 }
            r0 = r51;	 Catch:{ Exception -> 0x0787, all -> 0x0782 }
            r5 = r0.cacheImage;	 Catch:{ Exception -> 0x0787, all -> 0x0782 }
            r5 = r5.selfThumb;	 Catch:{ Exception -> 0x0787, all -> 0x0782 }
            if (r5 == 0) goto L_0x0146;	 Catch:{ Exception -> 0x0787, all -> 0x0782 }
        L_0x00ae:
            r20 = org.telegram.messenger.ImageLoader.headerThumb;	 Catch:{ Exception -> 0x0787, all -> 0x0782 }
        L_0x00b2:
            r5 = 0;	 Catch:{ Exception -> 0x0787, all -> 0x0782 }
            r0 = r20;	 Catch:{ Exception -> 0x0787, all -> 0x0782 }
            r6 = r0.length;	 Catch:{ Exception -> 0x0787, all -> 0x0782 }
            r0 = r44;	 Catch:{ Exception -> 0x0787, all -> 0x0782 }
            r1 = r20;	 Catch:{ Exception -> 0x0787, all -> 0x0782 }
            r0.readFully(r1, r5, r6);	 Catch:{ Exception -> 0x0787, all -> 0x0782 }
            r5 = new java.lang.String;	 Catch:{ Exception -> 0x0787, all -> 0x0782 }
            r0 = r20;	 Catch:{ Exception -> 0x0787, all -> 0x0782 }
            r5.<init>(r0);	 Catch:{ Exception -> 0x0787, all -> 0x0782 }
            r48 = r5.toLowerCase();	 Catch:{ Exception -> 0x0787, all -> 0x0782 }
            r48 = r48.toLowerCase();	 Catch:{ Exception -> 0x0787, all -> 0x0782 }
            r5 = "riff";	 Catch:{ Exception -> 0x0787, all -> 0x0782 }
            r0 = r48;	 Catch:{ Exception -> 0x0787, all -> 0x0782 }
            r5 = r0.startsWith(r5);	 Catch:{ Exception -> 0x0787, all -> 0x0782 }
            if (r5 == 0) goto L_0x00e4;	 Catch:{ Exception -> 0x0787, all -> 0x0782 }
        L_0x00d7:
            r5 = "webp";	 Catch:{ Exception -> 0x0787, all -> 0x0782 }
            r0 = r48;	 Catch:{ Exception -> 0x0787, all -> 0x0782 }
            r5 = r0.endsWith(r5);	 Catch:{ Exception -> 0x0787, all -> 0x0782 }
            if (r5 == 0) goto L_0x00e4;	 Catch:{ Exception -> 0x0787, all -> 0x0782 }
        L_0x00e2:
            r49 = 1;	 Catch:{ Exception -> 0x0787, all -> 0x0782 }
        L_0x00e4:
            r44.close();	 Catch:{ Exception -> 0x0787, all -> 0x0782 }
            if (r44 == 0) goto L_0x00ec;
        L_0x00e9:
            r44.close();	 Catch:{ Exception -> 0x014c }
        L_0x00ec:
            r0 = r51;
            r5 = r0.cacheImage;
            r5 = r5.selfThumb;
            if (r5 == 0) goto L_0x035e;
        L_0x00f4:
            r16 = 0;
            r0 = r51;
            r5 = r0.cacheImage;
            r5 = r5.filter;
            if (r5 == 0) goto L_0x010f;
        L_0x00fe:
            r0 = r51;
            r5 = r0.cacheImage;
            r5 = r5.filter;
            r6 = "b2";
            r5 = r5.contains(r6);
            if (r5 == 0) goto L_0x016c;
        L_0x010d:
            r16 = 3;
        L_0x010f:
            r0 = r51;	 Catch:{ Throwable -> 0x012b }
            r5 = org.telegram.messenger.ImageLoader.this;	 Catch:{ Throwable -> 0x012b }
            r6 = java.lang.System.currentTimeMillis();	 Catch:{ Throwable -> 0x012b }
            r5.lastCacheOutTime = r6;	 Catch:{ Throwable -> 0x012b }
            r0 = r51;	 Catch:{ Throwable -> 0x012b }
            r6 = r0.sync;	 Catch:{ Throwable -> 0x012b }
            monitor-enter(r6);	 Catch:{ Throwable -> 0x012b }
            r0 = r51;	 Catch:{ all -> 0x0128 }
            r5 = r0.isCancelled;	 Catch:{ all -> 0x0128 }
            if (r5 == 0) goto L_0x0190;	 Catch:{ all -> 0x0128 }
        L_0x0125:
            monitor-exit(r6);	 Catch:{ all -> 0x0128 }
            goto L_0x0017;	 Catch:{ all -> 0x0128 }
        L_0x0128:
            r5 = move-exception;	 Catch:{ all -> 0x0128 }
            monitor-exit(r6);	 Catch:{ all -> 0x0128 }
            throw r5;	 Catch:{ Throwable -> 0x012b }
        L_0x012b:
            r25 = move-exception;
            r4 = r31;
        L_0x012e:
            org.telegram.messenger.FileLog.m3e(r25);
        L_0x0131:
            java.lang.Thread.interrupted();
            if (r4 == 0) goto L_0x077f;
        L_0x0136:
            r5 = new android.graphics.drawable.BitmapDrawable;
            r5.<init>(r4);
        L_0x013b:
            r0 = r51;
            r0.onPostExecute(r5);
            goto L_0x0017;
        L_0x0142:
            r32 = 0;
            goto L_0x008e;
        L_0x0146:
            r20 = org.telegram.messenger.ImageLoader.header;	 Catch:{ Exception -> 0x0787, all -> 0x0782 }
            goto L_0x00b2;
        L_0x014c:
            r25 = move-exception;
            org.telegram.messenger.FileLog.m3e(r25);
            goto L_0x00ec;
        L_0x0151:
            r25 = move-exception;
        L_0x0152:
            org.telegram.messenger.FileLog.m3e(r25);	 Catch:{ all -> 0x0160 }
            if (r43 == 0) goto L_0x00ec;
        L_0x0157:
            r43.close();	 Catch:{ Exception -> 0x015b }
            goto L_0x00ec;
        L_0x015b:
            r25 = move-exception;
            org.telegram.messenger.FileLog.m3e(r25);
            goto L_0x00ec;
        L_0x0160:
            r5 = move-exception;
        L_0x0161:
            if (r43 == 0) goto L_0x0166;
        L_0x0163:
            r43.close();	 Catch:{ Exception -> 0x0167 }
        L_0x0166:
            throw r5;
        L_0x0167:
            r25 = move-exception;
            org.telegram.messenger.FileLog.m3e(r25);
            goto L_0x0166;
        L_0x016c:
            r0 = r51;
            r5 = r0.cacheImage;
            r5 = r5.filter;
            r6 = "b1";
            r5 = r5.contains(r6);
            if (r5 == 0) goto L_0x017e;
        L_0x017b:
            r16 = 2;
            goto L_0x010f;
        L_0x017e:
            r0 = r51;
            r5 = r0.cacheImage;
            r5 = r5.filter;
            r6 = "b";
            r5 = r5.contains(r6);
            if (r5 == 0) goto L_0x010f;
        L_0x018d:
            r16 = 1;
            goto L_0x010f;
        L_0x0190:
            monitor-exit(r6);	 Catch:{ all -> 0x0128 }
            r38 = new android.graphics.BitmapFactory$Options;	 Catch:{ Throwable -> 0x012b }
            r38.<init>();	 Catch:{ Throwable -> 0x012b }
            r5 = 1;	 Catch:{ Throwable -> 0x012b }
            r0 = r38;	 Catch:{ Throwable -> 0x012b }
            r0.inSampleSize = r5;	 Catch:{ Throwable -> 0x012b }
            r5 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Throwable -> 0x012b }
            r6 = 21;	 Catch:{ Throwable -> 0x012b }
            if (r5 >= r6) goto L_0x01a6;	 Catch:{ Throwable -> 0x012b }
        L_0x01a1:
            r5 = 1;	 Catch:{ Throwable -> 0x012b }
            r0 = r38;	 Catch:{ Throwable -> 0x012b }
            r0.inPurgeable = r5;	 Catch:{ Throwable -> 0x012b }
        L_0x01a6:
            if (r49 == 0) goto L_0x021b;	 Catch:{ Throwable -> 0x012b }
        L_0x01a8:
            r27 = new java.io.RandomAccessFile;	 Catch:{ Throwable -> 0x012b }
            r5 = "r";	 Catch:{ Throwable -> 0x012b }
            r0 = r27;	 Catch:{ Throwable -> 0x012b }
            r1 = r21;	 Catch:{ Throwable -> 0x012b }
            r0.<init>(r1, r5);	 Catch:{ Throwable -> 0x012b }
            r4 = r27.getChannel();	 Catch:{ Throwable -> 0x012b }
            r5 = java.nio.channels.FileChannel.MapMode.READ_ONLY;	 Catch:{ Throwable -> 0x012b }
            r6 = 0;	 Catch:{ Throwable -> 0x012b }
            r8 = r21.length();	 Catch:{ Throwable -> 0x012b }
            r19 = r4.map(r5, r6, r8);	 Catch:{ Throwable -> 0x012b }
            r18 = new android.graphics.BitmapFactory$Options;	 Catch:{ Throwable -> 0x012b }
            r18.<init>();	 Catch:{ Throwable -> 0x012b }
            r5 = 1;	 Catch:{ Throwable -> 0x012b }
            r0 = r18;	 Catch:{ Throwable -> 0x012b }
            r0.inJustDecodeBounds = r5;	 Catch:{ Throwable -> 0x012b }
            r5 = 0;	 Catch:{ Throwable -> 0x012b }
            r6 = r19.limit();	 Catch:{ Throwable -> 0x012b }
            r7 = 1;	 Catch:{ Throwable -> 0x012b }
            r0 = r19;	 Catch:{ Throwable -> 0x012b }
            r1 = r18;	 Catch:{ Throwable -> 0x012b }
            org.telegram.messenger.Utilities.loadWebpImage(r5, r0, r6, r1, r7);	 Catch:{ Throwable -> 0x012b }
            r0 = r18;	 Catch:{ Throwable -> 0x012b }
            r5 = r0.outWidth;	 Catch:{ Throwable -> 0x012b }
            r0 = r18;	 Catch:{ Throwable -> 0x012b }
            r6 = r0.outHeight;	 Catch:{ Throwable -> 0x012b }
            r7 = android.graphics.Bitmap.Config.ARGB_8888;	 Catch:{ Throwable -> 0x012b }
            r4 = org.telegram.messenger.Bitmaps.createBitmap(r5, r6, r7);	 Catch:{ Throwable -> 0x012b }
            r6 = r19.limit();	 Catch:{ Throwable -> 0x0216 }
            r7 = 0;	 Catch:{ Throwable -> 0x0216 }
            r0 = r38;	 Catch:{ Throwable -> 0x0216 }
            r5 = r0.inPurgeable;	 Catch:{ Throwable -> 0x0216 }
            if (r5 != 0) goto L_0x0219;	 Catch:{ Throwable -> 0x0216 }
        L_0x01f4:
            r5 = 1;	 Catch:{ Throwable -> 0x0216 }
        L_0x01f5:
            r0 = r19;	 Catch:{ Throwable -> 0x0216 }
            org.telegram.messenger.Utilities.loadWebpImage(r4, r0, r6, r7, r5);	 Catch:{ Throwable -> 0x0216 }
            r27.close();	 Catch:{ Throwable -> 0x0216 }
        L_0x01fd:
            if (r4 != 0) goto L_0x02a7;	 Catch:{ Throwable -> 0x0216 }
        L_0x01ff:
            r6 = r21.length();	 Catch:{ Throwable -> 0x0216 }
            r8 = 0;	 Catch:{ Throwable -> 0x0216 }
            r5 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1));	 Catch:{ Throwable -> 0x0216 }
            if (r5 == 0) goto L_0x0211;	 Catch:{ Throwable -> 0x0216 }
        L_0x0209:
            r0 = r51;	 Catch:{ Throwable -> 0x0216 }
            r5 = r0.cacheImage;	 Catch:{ Throwable -> 0x0216 }
            r5 = r5.filter;	 Catch:{ Throwable -> 0x0216 }
            if (r5 != 0) goto L_0x0131;	 Catch:{ Throwable -> 0x0216 }
        L_0x0211:
            r21.delete();	 Catch:{ Throwable -> 0x0216 }
            goto L_0x0131;
        L_0x0216:
            r25 = move-exception;
            goto L_0x012e;
        L_0x0219:
            r5 = 0;
            goto L_0x01f5;
        L_0x021b:
            r0 = r38;	 Catch:{ Throwable -> 0x012b }
            r5 = r0.inPurgeable;	 Catch:{ Throwable -> 0x012b }
            if (r5 == 0) goto L_0x027e;	 Catch:{ Throwable -> 0x012b }
        L_0x0221:
            r26 = new java.io.RandomAccessFile;	 Catch:{ Throwable -> 0x012b }
            r5 = "r";	 Catch:{ Throwable -> 0x012b }
            r0 = r26;	 Catch:{ Throwable -> 0x012b }
            r1 = r21;	 Catch:{ Throwable -> 0x012b }
            r0.<init>(r1, r5);	 Catch:{ Throwable -> 0x012b }
            r6 = r26.length();	 Catch:{ Throwable -> 0x012b }
            r0 = (int) r6;	 Catch:{ Throwable -> 0x012b }
            r34 = r0;	 Catch:{ Throwable -> 0x012b }
            r5 = org.telegram.messenger.ImageLoader.bytesThumb;	 Catch:{ Throwable -> 0x012b }
            if (r5 == 0) goto L_0x027b;	 Catch:{ Throwable -> 0x012b }
        L_0x023a:
            r5 = org.telegram.messenger.ImageLoader.bytesThumb;	 Catch:{ Throwable -> 0x012b }
            r5 = r5.length;	 Catch:{ Throwable -> 0x012b }
            r0 = r34;	 Catch:{ Throwable -> 0x012b }
            if (r5 < r0) goto L_0x027b;	 Catch:{ Throwable -> 0x012b }
        L_0x0243:
            r23 = org.telegram.messenger.ImageLoader.bytesThumb;	 Catch:{ Throwable -> 0x012b }
        L_0x0247:
            if (r23 != 0) goto L_0x0252;	 Catch:{ Throwable -> 0x012b }
        L_0x0249:
            r0 = r34;	 Catch:{ Throwable -> 0x012b }
            r0 = new byte[r0];	 Catch:{ Throwable -> 0x012b }
            r23 = r0;	 Catch:{ Throwable -> 0x012b }
            org.telegram.messenger.ImageLoader.bytesThumb = r23;	 Catch:{ Throwable -> 0x012b }
        L_0x0252:
            r5 = 0;	 Catch:{ Throwable -> 0x012b }
            r0 = r26;	 Catch:{ Throwable -> 0x012b }
            r1 = r23;	 Catch:{ Throwable -> 0x012b }
            r2 = r34;	 Catch:{ Throwable -> 0x012b }
            r0.readFully(r1, r5, r2);	 Catch:{ Throwable -> 0x012b }
            r26.close();	 Catch:{ Throwable -> 0x012b }
            if (r32 == 0) goto L_0x026f;	 Catch:{ Throwable -> 0x012b }
        L_0x0261:
            r5 = 0;	 Catch:{ Throwable -> 0x012b }
            r0 = r51;	 Catch:{ Throwable -> 0x012b }
            r6 = r0.cacheImage;	 Catch:{ Throwable -> 0x012b }
            r6 = r6.encryptionKeyPath;	 Catch:{ Throwable -> 0x012b }
            r0 = r23;	 Catch:{ Throwable -> 0x012b }
            r1 = r34;	 Catch:{ Throwable -> 0x012b }
            org.telegram.messenger.secretmedia.EncryptedFileInputStream.decryptBytesWithKeyFile(r0, r5, r1, r6);	 Catch:{ Throwable -> 0x012b }
        L_0x026f:
            r5 = 0;	 Catch:{ Throwable -> 0x012b }
            r0 = r23;	 Catch:{ Throwable -> 0x012b }
            r1 = r34;	 Catch:{ Throwable -> 0x012b }
            r2 = r38;	 Catch:{ Throwable -> 0x012b }
            r4 = android.graphics.BitmapFactory.decodeByteArray(r0, r5, r1, r2);	 Catch:{ Throwable -> 0x012b }
            goto L_0x01fd;	 Catch:{ Throwable -> 0x012b }
        L_0x027b:
            r23 = 0;	 Catch:{ Throwable -> 0x012b }
            goto L_0x0247;	 Catch:{ Throwable -> 0x012b }
        L_0x027e:
            if (r32 == 0) goto L_0x029d;	 Catch:{ Throwable -> 0x012b }
        L_0x0280:
            r33 = new org.telegram.messenger.secretmedia.EncryptedFileInputStream;	 Catch:{ Throwable -> 0x012b }
            r0 = r51;	 Catch:{ Throwable -> 0x012b }
            r5 = r0.cacheImage;	 Catch:{ Throwable -> 0x012b }
            r5 = r5.encryptionKeyPath;	 Catch:{ Throwable -> 0x012b }
            r0 = r33;	 Catch:{ Throwable -> 0x012b }
            r1 = r21;	 Catch:{ Throwable -> 0x012b }
            r0.<init>(r1, r5);	 Catch:{ Throwable -> 0x012b }
        L_0x028f:
            r5 = 0;	 Catch:{ Throwable -> 0x012b }
            r0 = r33;	 Catch:{ Throwable -> 0x012b }
            r1 = r38;	 Catch:{ Throwable -> 0x012b }
            r4 = android.graphics.BitmapFactory.decodeStream(r0, r5, r1);	 Catch:{ Throwable -> 0x012b }
            r33.close();	 Catch:{ Throwable -> 0x0216 }
            goto L_0x01fd;
        L_0x029d:
            r33 = new java.io.FileInputStream;	 Catch:{ Throwable -> 0x012b }
            r0 = r33;	 Catch:{ Throwable -> 0x012b }
            r1 = r21;	 Catch:{ Throwable -> 0x012b }
            r0.<init>(r1);	 Catch:{ Throwable -> 0x012b }
            goto L_0x028f;
        L_0x02a7:
            r5 = 1;
            r0 = r16;
            if (r0 != r5) goto L_0x02cf;
        L_0x02ac:
            r5 = r4.getConfig();	 Catch:{ Throwable -> 0x0216 }
            r6 = android.graphics.Bitmap.Config.ARGB_8888;	 Catch:{ Throwable -> 0x0216 }
            if (r5 != r6) goto L_0x0131;	 Catch:{ Throwable -> 0x0216 }
        L_0x02b4:
            r5 = 3;	 Catch:{ Throwable -> 0x0216 }
            r0 = r38;	 Catch:{ Throwable -> 0x0216 }
            r6 = r0.inPurgeable;	 Catch:{ Throwable -> 0x0216 }
            if (r6 == 0) goto L_0x02cd;	 Catch:{ Throwable -> 0x0216 }
        L_0x02bb:
            r6 = 0;	 Catch:{ Throwable -> 0x0216 }
        L_0x02bc:
            r7 = r4.getWidth();	 Catch:{ Throwable -> 0x0216 }
            r8 = r4.getHeight();	 Catch:{ Throwable -> 0x0216 }
            r9 = r4.getRowBytes();	 Catch:{ Throwable -> 0x0216 }
            org.telegram.messenger.Utilities.blurBitmap(r4, r5, r6, r7, r8, r9);	 Catch:{ Throwable -> 0x0216 }
            goto L_0x0131;	 Catch:{ Throwable -> 0x0216 }
        L_0x02cd:
            r6 = 1;	 Catch:{ Throwable -> 0x0216 }
            goto L_0x02bc;	 Catch:{ Throwable -> 0x0216 }
        L_0x02cf:
            r5 = 2;	 Catch:{ Throwable -> 0x0216 }
            r0 = r16;	 Catch:{ Throwable -> 0x0216 }
            if (r0 != r5) goto L_0x02f7;	 Catch:{ Throwable -> 0x0216 }
        L_0x02d4:
            r5 = r4.getConfig();	 Catch:{ Throwable -> 0x0216 }
            r6 = android.graphics.Bitmap.Config.ARGB_8888;	 Catch:{ Throwable -> 0x0216 }
            if (r5 != r6) goto L_0x0131;	 Catch:{ Throwable -> 0x0216 }
        L_0x02dc:
            r5 = 1;	 Catch:{ Throwable -> 0x0216 }
            r0 = r38;	 Catch:{ Throwable -> 0x0216 }
            r6 = r0.inPurgeable;	 Catch:{ Throwable -> 0x0216 }
            if (r6 == 0) goto L_0x02f5;	 Catch:{ Throwable -> 0x0216 }
        L_0x02e3:
            r6 = 0;	 Catch:{ Throwable -> 0x0216 }
        L_0x02e4:
            r7 = r4.getWidth();	 Catch:{ Throwable -> 0x0216 }
            r8 = r4.getHeight();	 Catch:{ Throwable -> 0x0216 }
            r9 = r4.getRowBytes();	 Catch:{ Throwable -> 0x0216 }
            org.telegram.messenger.Utilities.blurBitmap(r4, r5, r6, r7, r8, r9);	 Catch:{ Throwable -> 0x0216 }
            goto L_0x0131;	 Catch:{ Throwable -> 0x0216 }
        L_0x02f5:
            r6 = 1;	 Catch:{ Throwable -> 0x0216 }
            goto L_0x02e4;	 Catch:{ Throwable -> 0x0216 }
        L_0x02f7:
            r5 = 3;	 Catch:{ Throwable -> 0x0216 }
            r0 = r16;	 Catch:{ Throwable -> 0x0216 }
            if (r0 != r5) goto L_0x0351;	 Catch:{ Throwable -> 0x0216 }
        L_0x02fc:
            r5 = r4.getConfig();	 Catch:{ Throwable -> 0x0216 }
            r6 = android.graphics.Bitmap.Config.ARGB_8888;	 Catch:{ Throwable -> 0x0216 }
            if (r5 != r6) goto L_0x0131;	 Catch:{ Throwable -> 0x0216 }
        L_0x0304:
            r5 = 7;	 Catch:{ Throwable -> 0x0216 }
            r0 = r38;	 Catch:{ Throwable -> 0x0216 }
            r6 = r0.inPurgeable;	 Catch:{ Throwable -> 0x0216 }
            if (r6 == 0) goto L_0x034b;	 Catch:{ Throwable -> 0x0216 }
        L_0x030b:
            r6 = 0;	 Catch:{ Throwable -> 0x0216 }
        L_0x030c:
            r7 = r4.getWidth();	 Catch:{ Throwable -> 0x0216 }
            r8 = r4.getHeight();	 Catch:{ Throwable -> 0x0216 }
            r9 = r4.getRowBytes();	 Catch:{ Throwable -> 0x0216 }
            org.telegram.messenger.Utilities.blurBitmap(r4, r5, r6, r7, r8, r9);	 Catch:{ Throwable -> 0x0216 }
            r5 = 7;	 Catch:{ Throwable -> 0x0216 }
            r0 = r38;	 Catch:{ Throwable -> 0x0216 }
            r6 = r0.inPurgeable;	 Catch:{ Throwable -> 0x0216 }
            if (r6 == 0) goto L_0x034d;	 Catch:{ Throwable -> 0x0216 }
        L_0x0322:
            r6 = 0;	 Catch:{ Throwable -> 0x0216 }
        L_0x0323:
            r7 = r4.getWidth();	 Catch:{ Throwable -> 0x0216 }
            r8 = r4.getHeight();	 Catch:{ Throwable -> 0x0216 }
            r9 = r4.getRowBytes();	 Catch:{ Throwable -> 0x0216 }
            org.telegram.messenger.Utilities.blurBitmap(r4, r5, r6, r7, r8, r9);	 Catch:{ Throwable -> 0x0216 }
            r5 = 7;	 Catch:{ Throwable -> 0x0216 }
            r0 = r38;	 Catch:{ Throwable -> 0x0216 }
            r6 = r0.inPurgeable;	 Catch:{ Throwable -> 0x0216 }
            if (r6 == 0) goto L_0x034f;	 Catch:{ Throwable -> 0x0216 }
        L_0x0339:
            r6 = 0;	 Catch:{ Throwable -> 0x0216 }
        L_0x033a:
            r7 = r4.getWidth();	 Catch:{ Throwable -> 0x0216 }
            r8 = r4.getHeight();	 Catch:{ Throwable -> 0x0216 }
            r9 = r4.getRowBytes();	 Catch:{ Throwable -> 0x0216 }
            org.telegram.messenger.Utilities.blurBitmap(r4, r5, r6, r7, r8, r9);	 Catch:{ Throwable -> 0x0216 }
            goto L_0x0131;	 Catch:{ Throwable -> 0x0216 }
        L_0x034b:
            r6 = 1;	 Catch:{ Throwable -> 0x0216 }
            goto L_0x030c;	 Catch:{ Throwable -> 0x0216 }
        L_0x034d:
            r6 = 1;	 Catch:{ Throwable -> 0x0216 }
            goto L_0x0323;	 Catch:{ Throwable -> 0x0216 }
        L_0x034f:
            r6 = 1;	 Catch:{ Throwable -> 0x0216 }
            goto L_0x033a;	 Catch:{ Throwable -> 0x0216 }
        L_0x0351:
            if (r16 != 0) goto L_0x0131;	 Catch:{ Throwable -> 0x0216 }
        L_0x0353:
            r0 = r38;	 Catch:{ Throwable -> 0x0216 }
            r5 = r0.inPurgeable;	 Catch:{ Throwable -> 0x0216 }
            if (r5 == 0) goto L_0x0131;	 Catch:{ Throwable -> 0x0216 }
        L_0x0359:
            org.telegram.messenger.Utilities.pinBitmap(r4);	 Catch:{ Throwable -> 0x0216 }
            goto L_0x0131;
        L_0x035e:
            r37 = 0;
            r0 = r51;	 Catch:{ Throwable -> 0x0400 }
            r5 = r0.cacheImage;	 Catch:{ Throwable -> 0x0400 }
            r5 = r5.httpUrl;	 Catch:{ Throwable -> 0x0400 }
            if (r5 == 0) goto L_0x03ae;	 Catch:{ Throwable -> 0x0400 }
        L_0x0368:
            r0 = r51;	 Catch:{ Throwable -> 0x0400 }
            r5 = r0.cacheImage;	 Catch:{ Throwable -> 0x0400 }
            r5 = r5.httpUrl;	 Catch:{ Throwable -> 0x0400 }
            r6 = "thumb://";	 Catch:{ Throwable -> 0x0400 }
            r5 = r5.startsWith(r6);	 Catch:{ Throwable -> 0x0400 }
            if (r5 == 0) goto L_0x0405;	 Catch:{ Throwable -> 0x0400 }
        L_0x0377:
            r0 = r51;	 Catch:{ Throwable -> 0x0400 }
            r5 = r0.cacheImage;	 Catch:{ Throwable -> 0x0400 }
            r5 = r5.httpUrl;	 Catch:{ Throwable -> 0x0400 }
            r6 = ":";	 Catch:{ Throwable -> 0x0400 }
            r7 = 8;	 Catch:{ Throwable -> 0x0400 }
            r30 = r5.indexOf(r6, r7);	 Catch:{ Throwable -> 0x0400 }
            if (r30 < 0) goto L_0x03ac;	 Catch:{ Throwable -> 0x0400 }
        L_0x0388:
            r0 = r51;	 Catch:{ Throwable -> 0x0400 }
            r5 = r0.cacheImage;	 Catch:{ Throwable -> 0x0400 }
            r5 = r5.httpUrl;	 Catch:{ Throwable -> 0x0400 }
            r6 = 8;	 Catch:{ Throwable -> 0x0400 }
            r0 = r30;	 Catch:{ Throwable -> 0x0400 }
            r5 = r5.substring(r6, r0);	 Catch:{ Throwable -> 0x0400 }
            r6 = java.lang.Long.parseLong(r5);	 Catch:{ Throwable -> 0x0400 }
            r35 = java.lang.Long.valueOf(r6);	 Catch:{ Throwable -> 0x0400 }
            r36 = 0;	 Catch:{ Throwable -> 0x0400 }
            r0 = r51;	 Catch:{ Throwable -> 0x0400 }
            r5 = r0.cacheImage;	 Catch:{ Throwable -> 0x0400 }
            r5 = r5.httpUrl;	 Catch:{ Throwable -> 0x0400 }
            r6 = r30 + 1;	 Catch:{ Throwable -> 0x0400 }
            r37 = r5.substring(r6);	 Catch:{ Throwable -> 0x0400 }
        L_0x03ac:
            r22 = 0;	 Catch:{ Throwable -> 0x0400 }
        L_0x03ae:
            r24 = 20;	 Catch:{ Throwable -> 0x0400 }
            if (r35 == 0) goto L_0x03b4;	 Catch:{ Throwable -> 0x0400 }
        L_0x03b2:
            r24 = 0;	 Catch:{ Throwable -> 0x0400 }
        L_0x03b4:
            if (r24 == 0) goto L_0x03e4;	 Catch:{ Throwable -> 0x0400 }
        L_0x03b6:
            r0 = r51;	 Catch:{ Throwable -> 0x0400 }
            r5 = org.telegram.messenger.ImageLoader.this;	 Catch:{ Throwable -> 0x0400 }
            r6 = r5.lastCacheOutTime;	 Catch:{ Throwable -> 0x0400 }
            r8 = 0;	 Catch:{ Throwable -> 0x0400 }
            r5 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1));	 Catch:{ Throwable -> 0x0400 }
            if (r5 == 0) goto L_0x03e4;	 Catch:{ Throwable -> 0x0400 }
        L_0x03c4:
            r0 = r51;	 Catch:{ Throwable -> 0x0400 }
            r5 = org.telegram.messenger.ImageLoader.this;	 Catch:{ Throwable -> 0x0400 }
            r6 = r5.lastCacheOutTime;	 Catch:{ Throwable -> 0x0400 }
            r8 = java.lang.System.currentTimeMillis();	 Catch:{ Throwable -> 0x0400 }
            r0 = r24;	 Catch:{ Throwable -> 0x0400 }
            r10 = (long) r0;	 Catch:{ Throwable -> 0x0400 }
            r8 = r8 - r10;	 Catch:{ Throwable -> 0x0400 }
            r5 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1));	 Catch:{ Throwable -> 0x0400 }
            if (r5 <= 0) goto L_0x03e4;	 Catch:{ Throwable -> 0x0400 }
        L_0x03d8:
            r5 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Throwable -> 0x0400 }
            r6 = 21;	 Catch:{ Throwable -> 0x0400 }
            if (r5 >= r6) goto L_0x03e4;	 Catch:{ Throwable -> 0x0400 }
        L_0x03de:
            r0 = r24;	 Catch:{ Throwable -> 0x0400 }
            r6 = (long) r0;	 Catch:{ Throwable -> 0x0400 }
            java.lang.Thread.sleep(r6);	 Catch:{ Throwable -> 0x0400 }
        L_0x03e4:
            r0 = r51;	 Catch:{ Throwable -> 0x0400 }
            r5 = org.telegram.messenger.ImageLoader.this;	 Catch:{ Throwable -> 0x0400 }
            r6 = java.lang.System.currentTimeMillis();	 Catch:{ Throwable -> 0x0400 }
            r5.lastCacheOutTime = r6;	 Catch:{ Throwable -> 0x0400 }
            r0 = r51;	 Catch:{ Throwable -> 0x0400 }
            r6 = r0.sync;	 Catch:{ Throwable -> 0x0400 }
            monitor-enter(r6);	 Catch:{ Throwable -> 0x0400 }
            r0 = r51;	 Catch:{ all -> 0x03fd }
            r5 = r0.isCancelled;	 Catch:{ all -> 0x03fd }
            if (r5 == 0) goto L_0x0454;	 Catch:{ all -> 0x03fd }
        L_0x03fa:
            monitor-exit(r6);	 Catch:{ all -> 0x03fd }
            goto L_0x0017;	 Catch:{ all -> 0x03fd }
        L_0x03fd:
            r5 = move-exception;	 Catch:{ all -> 0x03fd }
            monitor-exit(r6);	 Catch:{ all -> 0x03fd }
            throw r5;	 Catch:{ Throwable -> 0x0400 }
        L_0x0400:
            r5 = move-exception;	 Catch:{ Throwable -> 0x0400 }
            r4 = r31;	 Catch:{ Throwable -> 0x0400 }
            goto L_0x0131;	 Catch:{ Throwable -> 0x0400 }
        L_0x0405:
            r0 = r51;	 Catch:{ Throwable -> 0x0400 }
            r5 = r0.cacheImage;	 Catch:{ Throwable -> 0x0400 }
            r5 = r5.httpUrl;	 Catch:{ Throwable -> 0x0400 }
            r6 = "vthumb://";	 Catch:{ Throwable -> 0x0400 }
            r5 = r5.startsWith(r6);	 Catch:{ Throwable -> 0x0400 }
            if (r5 == 0) goto L_0x0441;	 Catch:{ Throwable -> 0x0400 }
        L_0x0414:
            r0 = r51;	 Catch:{ Throwable -> 0x0400 }
            r5 = r0.cacheImage;	 Catch:{ Throwable -> 0x0400 }
            r5 = r5.httpUrl;	 Catch:{ Throwable -> 0x0400 }
            r6 = ":";	 Catch:{ Throwable -> 0x0400 }
            r7 = 9;	 Catch:{ Throwable -> 0x0400 }
            r30 = r5.indexOf(r6, r7);	 Catch:{ Throwable -> 0x0400 }
            if (r30 < 0) goto L_0x043d;	 Catch:{ Throwable -> 0x0400 }
        L_0x0425:
            r0 = r51;	 Catch:{ Throwable -> 0x0400 }
            r5 = r0.cacheImage;	 Catch:{ Throwable -> 0x0400 }
            r5 = r5.httpUrl;	 Catch:{ Throwable -> 0x0400 }
            r6 = 9;	 Catch:{ Throwable -> 0x0400 }
            r0 = r30;	 Catch:{ Throwable -> 0x0400 }
            r5 = r5.substring(r6, r0);	 Catch:{ Throwable -> 0x0400 }
            r6 = java.lang.Long.parseLong(r5);	 Catch:{ Throwable -> 0x0400 }
            r35 = java.lang.Long.valueOf(r6);	 Catch:{ Throwable -> 0x0400 }
            r36 = 1;	 Catch:{ Throwable -> 0x0400 }
        L_0x043d:
            r22 = 0;	 Catch:{ Throwable -> 0x0400 }
            goto L_0x03ae;	 Catch:{ Throwable -> 0x0400 }
        L_0x0441:
            r0 = r51;	 Catch:{ Throwable -> 0x0400 }
            r5 = r0.cacheImage;	 Catch:{ Throwable -> 0x0400 }
            r5 = r5.httpUrl;	 Catch:{ Throwable -> 0x0400 }
            r6 = "http";	 Catch:{ Throwable -> 0x0400 }
            r5 = r5.startsWith(r6);	 Catch:{ Throwable -> 0x0400 }
            if (r5 != 0) goto L_0x03ae;
        L_0x0450:
            r22 = 0;
            goto L_0x03ae;
        L_0x0454:
            monitor-exit(r6);	 Catch:{ all -> 0x03fd }
            r38 = new android.graphics.BitmapFactory$Options;	 Catch:{ Throwable -> 0x0400 }
            r38.<init>();	 Catch:{ Throwable -> 0x0400 }
            r5 = 1;	 Catch:{ Throwable -> 0x0400 }
            r0 = r38;	 Catch:{ Throwable -> 0x0400 }
            r0.inSampleSize = r5;	 Catch:{ Throwable -> 0x0400 }
            r50 = 0;	 Catch:{ Throwable -> 0x0400 }
            r29 = 0;	 Catch:{ Throwable -> 0x0400 }
            r15 = 0;	 Catch:{ Throwable -> 0x0400 }
            r0 = r51;	 Catch:{ Throwable -> 0x0400 }
            r5 = r0.cacheImage;	 Catch:{ Throwable -> 0x0400 }
            r5 = r5.filter;	 Catch:{ Throwable -> 0x0400 }
            if (r5 == 0) goto L_0x0543;	 Catch:{ Throwable -> 0x0400 }
        L_0x046c:
            r0 = r51;	 Catch:{ Throwable -> 0x0400 }
            r5 = r0.cacheImage;	 Catch:{ Throwable -> 0x0400 }
            r5 = r5.filter;	 Catch:{ Throwable -> 0x0400 }
            r6 = "_";	 Catch:{ Throwable -> 0x0400 }
            r12 = r5.split(r6);	 Catch:{ Throwable -> 0x0400 }
            r5 = r12.length;	 Catch:{ Throwable -> 0x0400 }
            r6 = 2;	 Catch:{ Throwable -> 0x0400 }
            if (r5 < r6) goto L_0x0493;	 Catch:{ Throwable -> 0x0400 }
        L_0x047d:
            r5 = 0;	 Catch:{ Throwable -> 0x0400 }
            r5 = r12[r5];	 Catch:{ Throwable -> 0x0400 }
            r5 = java.lang.Float.parseFloat(r5);	 Catch:{ Throwable -> 0x0400 }
            r6 = org.telegram.messenger.AndroidUtilities.density;	 Catch:{ Throwable -> 0x0400 }
            r50 = r5 * r6;	 Catch:{ Throwable -> 0x0400 }
            r5 = 1;	 Catch:{ Throwable -> 0x0400 }
            r5 = r12[r5];	 Catch:{ Throwable -> 0x0400 }
            r5 = java.lang.Float.parseFloat(r5);	 Catch:{ Throwable -> 0x0400 }
            r6 = org.telegram.messenger.AndroidUtilities.density;	 Catch:{ Throwable -> 0x0400 }
            r29 = r5 * r6;	 Catch:{ Throwable -> 0x0400 }
        L_0x0493:
            r0 = r51;	 Catch:{ Throwable -> 0x0400 }
            r5 = r0.cacheImage;	 Catch:{ Throwable -> 0x0400 }
            r5 = r5.filter;	 Catch:{ Throwable -> 0x0400 }
            r6 = "b";	 Catch:{ Throwable -> 0x0400 }
            r5 = r5.contains(r6);	 Catch:{ Throwable -> 0x0400 }
            if (r5 == 0) goto L_0x04a3;	 Catch:{ Throwable -> 0x0400 }
        L_0x04a2:
            r15 = 1;	 Catch:{ Throwable -> 0x0400 }
        L_0x04a3:
            r5 = 0;	 Catch:{ Throwable -> 0x0400 }
            r5 = (r50 > r5 ? 1 : (r50 == r5 ? 0 : -1));	 Catch:{ Throwable -> 0x0400 }
            if (r5 == 0) goto L_0x078c;	 Catch:{ Throwable -> 0x0400 }
        L_0x04a8:
            r5 = 0;	 Catch:{ Throwable -> 0x0400 }
            r5 = (r29 > r5 ? 1 : (r29 == r5 ? 0 : -1));	 Catch:{ Throwable -> 0x0400 }
            if (r5 == 0) goto L_0x078c;	 Catch:{ Throwable -> 0x0400 }
        L_0x04ad:
            r5 = 1;	 Catch:{ Throwable -> 0x0400 }
            r0 = r38;	 Catch:{ Throwable -> 0x0400 }
            r0.inJustDecodeBounds = r5;	 Catch:{ Throwable -> 0x0400 }
            if (r35 == 0) goto L_0x051b;	 Catch:{ Throwable -> 0x0400 }
        L_0x04b4:
            if (r37 != 0) goto L_0x051b;	 Catch:{ Throwable -> 0x0400 }
        L_0x04b6:
            if (r36 == 0) goto L_0x0508;	 Catch:{ Throwable -> 0x0400 }
        L_0x04b8:
            r5 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Throwable -> 0x0400 }
            r5 = r5.getContentResolver();	 Catch:{ Throwable -> 0x0400 }
            r6 = r35.longValue();	 Catch:{ Throwable -> 0x0400 }
            r8 = 1;	 Catch:{ Throwable -> 0x0400 }
            r0 = r38;	 Catch:{ Throwable -> 0x0400 }
            android.provider.MediaStore.Video.Thumbnails.getThumbnail(r5, r6, r8, r0);	 Catch:{ Throwable -> 0x0400 }
            r4 = r31;
        L_0x04ca:
            r0 = r38;	 Catch:{ Throwable -> 0x0505 }
            r5 = r0.outWidth;	 Catch:{ Throwable -> 0x0505 }
            r0 = (float) r5;	 Catch:{ Throwable -> 0x0505 }
            r41 = r0;	 Catch:{ Throwable -> 0x0505 }
            r0 = r38;	 Catch:{ Throwable -> 0x0505 }
            r5 = r0.outHeight;	 Catch:{ Throwable -> 0x0505 }
            r0 = (float) r5;	 Catch:{ Throwable -> 0x0505 }
            r39 = r0;	 Catch:{ Throwable -> 0x0505 }
            r5 = r41 / r50;	 Catch:{ Throwable -> 0x0505 }
            r6 = r39 / r29;	 Catch:{ Throwable -> 0x0505 }
            r46 = java.lang.Math.max(r5, r6);	 Catch:{ Throwable -> 0x0505 }
            r5 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;	 Catch:{ Throwable -> 0x0505 }
            r5 = (r46 > r5 ? 1 : (r46 == r5 ? 0 : -1));	 Catch:{ Throwable -> 0x0505 }
            if (r5 >= 0) goto L_0x04e8;	 Catch:{ Throwable -> 0x0505 }
        L_0x04e6:
            r46 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;	 Catch:{ Throwable -> 0x0505 }
        L_0x04e8:
            r5 = 0;	 Catch:{ Throwable -> 0x0505 }
            r0 = r38;	 Catch:{ Throwable -> 0x0505 }
            r0.inJustDecodeBounds = r5;	 Catch:{ Throwable -> 0x0505 }
            r0 = r46;	 Catch:{ Throwable -> 0x0505 }
            r5 = (int) r0;	 Catch:{ Throwable -> 0x0505 }
            r0 = r38;	 Catch:{ Throwable -> 0x0505 }
            r0.inSampleSize = r5;	 Catch:{ Throwable -> 0x0505 }
        L_0x04f4:
            r0 = r51;	 Catch:{ Throwable -> 0x0505 }
            r6 = r0.sync;	 Catch:{ Throwable -> 0x0505 }
            monitor-enter(r6);	 Catch:{ Throwable -> 0x0505 }
            r0 = r51;	 Catch:{ all -> 0x0502 }
            r5 = r0.isCancelled;	 Catch:{ all -> 0x0502 }
            if (r5 == 0) goto L_0x05a0;	 Catch:{ all -> 0x0502 }
        L_0x04ff:
            monitor-exit(r6);	 Catch:{ all -> 0x0502 }
            goto L_0x0017;	 Catch:{ all -> 0x0502 }
        L_0x0502:
            r5 = move-exception;	 Catch:{ all -> 0x0502 }
            monitor-exit(r6);	 Catch:{ all -> 0x0502 }
            throw r5;	 Catch:{ Throwable -> 0x0505 }
        L_0x0505:
            r5 = move-exception;
            goto L_0x0131;
        L_0x0508:
            r5 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Throwable -> 0x0400 }
            r5 = r5.getContentResolver();	 Catch:{ Throwable -> 0x0400 }
            r6 = r35.longValue();	 Catch:{ Throwable -> 0x0400 }
            r8 = 1;	 Catch:{ Throwable -> 0x0400 }
            r0 = r38;	 Catch:{ Throwable -> 0x0400 }
            android.provider.MediaStore.Images.Thumbnails.getThumbnail(r5, r6, r8, r0);	 Catch:{ Throwable -> 0x0400 }
            r4 = r31;	 Catch:{ Throwable -> 0x0400 }
            goto L_0x04ca;	 Catch:{ Throwable -> 0x0400 }
        L_0x051b:
            if (r32 == 0) goto L_0x0539;	 Catch:{ Throwable -> 0x0400 }
        L_0x051d:
            r33 = new org.telegram.messenger.secretmedia.EncryptedFileInputStream;	 Catch:{ Throwable -> 0x0400 }
            r0 = r51;	 Catch:{ Throwable -> 0x0400 }
            r5 = r0.cacheImage;	 Catch:{ Throwable -> 0x0400 }
            r5 = r5.encryptionKeyPath;	 Catch:{ Throwable -> 0x0400 }
            r0 = r33;	 Catch:{ Throwable -> 0x0400 }
            r1 = r21;	 Catch:{ Throwable -> 0x0400 }
            r0.<init>(r1, r5);	 Catch:{ Throwable -> 0x0400 }
        L_0x052c:
            r5 = 0;	 Catch:{ Throwable -> 0x0400 }
            r0 = r33;	 Catch:{ Throwable -> 0x0400 }
            r1 = r38;	 Catch:{ Throwable -> 0x0400 }
            r4 = android.graphics.BitmapFactory.decodeStream(r0, r5, r1);	 Catch:{ Throwable -> 0x0400 }
            r33.close();	 Catch:{ Throwable -> 0x0505 }
            goto L_0x04ca;
        L_0x0539:
            r33 = new java.io.FileInputStream;	 Catch:{ Throwable -> 0x0400 }
            r0 = r33;	 Catch:{ Throwable -> 0x0400 }
            r1 = r21;	 Catch:{ Throwable -> 0x0400 }
            r0.<init>(r1);	 Catch:{ Throwable -> 0x0400 }
            goto L_0x052c;	 Catch:{ Throwable -> 0x0400 }
        L_0x0543:
            if (r37 == 0) goto L_0x078c;	 Catch:{ Throwable -> 0x0400 }
        L_0x0545:
            r5 = 1;	 Catch:{ Throwable -> 0x0400 }
            r0 = r38;	 Catch:{ Throwable -> 0x0400 }
            r0.inJustDecodeBounds = r5;	 Catch:{ Throwable -> 0x0400 }
            r5 = android.graphics.Bitmap.Config.RGB_565;	 Catch:{ Throwable -> 0x0400 }
            r0 = r38;	 Catch:{ Throwable -> 0x0400 }
            r0.inPreferredConfig = r5;	 Catch:{ Throwable -> 0x0400 }
            r33 = new java.io.FileInputStream;	 Catch:{ Throwable -> 0x0400 }
            r0 = r33;	 Catch:{ Throwable -> 0x0400 }
            r1 = r21;	 Catch:{ Throwable -> 0x0400 }
            r0.<init>(r1);	 Catch:{ Throwable -> 0x0400 }
            r5 = 0;	 Catch:{ Throwable -> 0x0400 }
            r0 = r33;	 Catch:{ Throwable -> 0x0400 }
            r1 = r38;	 Catch:{ Throwable -> 0x0400 }
            r4 = android.graphics.BitmapFactory.decodeStream(r0, r5, r1);	 Catch:{ Throwable -> 0x0400 }
            r33.close();	 Catch:{ Throwable -> 0x0505 }
            r0 = r38;	 Catch:{ Throwable -> 0x0505 }
            r0 = r0.outWidth;	 Catch:{ Throwable -> 0x0505 }
            r42 = r0;	 Catch:{ Throwable -> 0x0505 }
            r0 = r38;	 Catch:{ Throwable -> 0x0505 }
            r0 = r0.outHeight;	 Catch:{ Throwable -> 0x0505 }
            r40 = r0;	 Catch:{ Throwable -> 0x0505 }
            r5 = 0;	 Catch:{ Throwable -> 0x0505 }
            r0 = r38;	 Catch:{ Throwable -> 0x0505 }
            r0.inJustDecodeBounds = r5;	 Catch:{ Throwable -> 0x0505 }
            r0 = r42;	 Catch:{ Throwable -> 0x0505 }
            r5 = r0 / 200;	 Catch:{ Throwable -> 0x0505 }
            r0 = r40;	 Catch:{ Throwable -> 0x0505 }
            r6 = r0 / 200;	 Catch:{ Throwable -> 0x0505 }
            r5 = java.lang.Math.max(r5, r6);	 Catch:{ Throwable -> 0x0505 }
            r0 = (float) r5;	 Catch:{ Throwable -> 0x0505 }
            r46 = r0;	 Catch:{ Throwable -> 0x0505 }
            r5 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;	 Catch:{ Throwable -> 0x0505 }
            r5 = (r46 > r5 ? 1 : (r46 == r5 ? 0 : -1));	 Catch:{ Throwable -> 0x0505 }
            if (r5 >= 0) goto L_0x058d;	 Catch:{ Throwable -> 0x0505 }
        L_0x058b:
            r46 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;	 Catch:{ Throwable -> 0x0505 }
        L_0x058d:
            r45 = 1;	 Catch:{ Throwable -> 0x0505 }
        L_0x058f:
            r45 = r45 * 2;	 Catch:{ Throwable -> 0x0505 }
            r5 = r45 * 2;	 Catch:{ Throwable -> 0x0505 }
            r5 = (float) r5;	 Catch:{ Throwable -> 0x0505 }
            r5 = (r5 > r46 ? 1 : (r5 == r46 ? 0 : -1));	 Catch:{ Throwable -> 0x0505 }
            if (r5 < 0) goto L_0x058f;	 Catch:{ Throwable -> 0x0505 }
        L_0x0598:
            r0 = r45;	 Catch:{ Throwable -> 0x0505 }
            r1 = r38;	 Catch:{ Throwable -> 0x0505 }
            r1.inSampleSize = r0;	 Catch:{ Throwable -> 0x0505 }
            goto L_0x04f4;
        L_0x05a0:
            monitor-exit(r6);	 Catch:{ all -> 0x0502 }
            r0 = r51;	 Catch:{ Throwable -> 0x0505 }
            r5 = r0.cacheImage;	 Catch:{ Throwable -> 0x0505 }
            r5 = r5.filter;	 Catch:{ Throwable -> 0x0505 }
            if (r5 == 0) goto L_0x05b3;	 Catch:{ Throwable -> 0x0505 }
        L_0x05a9:
            if (r15 != 0) goto L_0x05b3;	 Catch:{ Throwable -> 0x0505 }
        L_0x05ab:
            r0 = r51;	 Catch:{ Throwable -> 0x0505 }
            r5 = r0.cacheImage;	 Catch:{ Throwable -> 0x0505 }
            r5 = r5.httpUrl;	 Catch:{ Throwable -> 0x0505 }
            if (r5 == 0) goto L_0x0654;	 Catch:{ Throwable -> 0x0505 }
        L_0x05b3:
            r5 = android.graphics.Bitmap.Config.ARGB_8888;	 Catch:{ Throwable -> 0x0505 }
            r0 = r38;	 Catch:{ Throwable -> 0x0505 }
            r0.inPreferredConfig = r5;	 Catch:{ Throwable -> 0x0505 }
        L_0x05b9:
            r5 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Throwable -> 0x0505 }
            r6 = 21;	 Catch:{ Throwable -> 0x0505 }
            if (r5 >= r6) goto L_0x05c4;	 Catch:{ Throwable -> 0x0505 }
        L_0x05bf:
            r5 = 1;	 Catch:{ Throwable -> 0x0505 }
            r0 = r38;	 Catch:{ Throwable -> 0x0505 }
            r0.inPurgeable = r5;	 Catch:{ Throwable -> 0x0505 }
        L_0x05c4:
            r5 = 0;	 Catch:{ Throwable -> 0x0505 }
            r0 = r38;	 Catch:{ Throwable -> 0x0505 }
            r0.inDither = r5;	 Catch:{ Throwable -> 0x0505 }
            if (r35 == 0) goto L_0x05e0;	 Catch:{ Throwable -> 0x0505 }
        L_0x05cb:
            if (r37 != 0) goto L_0x05e0;	 Catch:{ Throwable -> 0x0505 }
        L_0x05cd:
            if (r36 == 0) goto L_0x065c;	 Catch:{ Throwable -> 0x0505 }
        L_0x05cf:
            r5 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Throwable -> 0x0505 }
            r5 = r5.getContentResolver();	 Catch:{ Throwable -> 0x0505 }
            r6 = r35.longValue();	 Catch:{ Throwable -> 0x0505 }
            r8 = 1;	 Catch:{ Throwable -> 0x0505 }
            r0 = r38;	 Catch:{ Throwable -> 0x0505 }
            r4 = android.provider.MediaStore.Video.Thumbnails.getThumbnail(r5, r6, r8, r0);	 Catch:{ Throwable -> 0x0505 }
        L_0x05e0:
            if (r4 != 0) goto L_0x0639;	 Catch:{ Throwable -> 0x0505 }
        L_0x05e2:
            if (r49 == 0) goto L_0x0671;	 Catch:{ Throwable -> 0x0505 }
        L_0x05e4:
            r27 = new java.io.RandomAccessFile;	 Catch:{ Throwable -> 0x0505 }
            r5 = "r";	 Catch:{ Throwable -> 0x0505 }
            r0 = r27;	 Catch:{ Throwable -> 0x0505 }
            r1 = r21;	 Catch:{ Throwable -> 0x0505 }
            r0.<init>(r1, r5);	 Catch:{ Throwable -> 0x0505 }
            r6 = r27.getChannel();	 Catch:{ Throwable -> 0x0505 }
            r7 = java.nio.channels.FileChannel.MapMode.READ_ONLY;	 Catch:{ Throwable -> 0x0505 }
            r8 = 0;	 Catch:{ Throwable -> 0x0505 }
            r10 = r21.length();	 Catch:{ Throwable -> 0x0505 }
            r19 = r6.map(r7, r8, r10);	 Catch:{ Throwable -> 0x0505 }
            r18 = new android.graphics.BitmapFactory$Options;	 Catch:{ Throwable -> 0x0505 }
            r18.<init>();	 Catch:{ Throwable -> 0x0505 }
            r5 = 1;	 Catch:{ Throwable -> 0x0505 }
            r0 = r18;	 Catch:{ Throwable -> 0x0505 }
            r0.inJustDecodeBounds = r5;	 Catch:{ Throwable -> 0x0505 }
            r5 = 0;	 Catch:{ Throwable -> 0x0505 }
            r6 = r19.limit();	 Catch:{ Throwable -> 0x0505 }
            r7 = 1;	 Catch:{ Throwable -> 0x0505 }
            r0 = r19;	 Catch:{ Throwable -> 0x0505 }
            r1 = r18;	 Catch:{ Throwable -> 0x0505 }
            org.telegram.messenger.Utilities.loadWebpImage(r5, r0, r6, r1, r7);	 Catch:{ Throwable -> 0x0505 }
            r0 = r18;	 Catch:{ Throwable -> 0x0505 }
            r5 = r0.outWidth;	 Catch:{ Throwable -> 0x0505 }
            r0 = r18;	 Catch:{ Throwable -> 0x0505 }
            r6 = r0.outHeight;	 Catch:{ Throwable -> 0x0505 }
            r7 = android.graphics.Bitmap.Config.ARGB_8888;	 Catch:{ Throwable -> 0x0505 }
            r4 = org.telegram.messenger.Bitmaps.createBitmap(r5, r6, r7);	 Catch:{ Throwable -> 0x0505 }
            r6 = r19.limit();	 Catch:{ Throwable -> 0x0505 }
            r7 = 0;	 Catch:{ Throwable -> 0x0505 }
            r0 = r38;	 Catch:{ Throwable -> 0x0505 }
            r5 = r0.inPurgeable;	 Catch:{ Throwable -> 0x0505 }
            if (r5 != 0) goto L_0x066f;	 Catch:{ Throwable -> 0x0505 }
        L_0x0630:
            r5 = 1;	 Catch:{ Throwable -> 0x0505 }
        L_0x0631:
            r0 = r19;	 Catch:{ Throwable -> 0x0505 }
            org.telegram.messenger.Utilities.loadWebpImage(r4, r0, r6, r7, r5);	 Catch:{ Throwable -> 0x0505 }
            r27.close();	 Catch:{ Throwable -> 0x0505 }
        L_0x0639:
            if (r4 != 0) goto L_0x06fe;	 Catch:{ Throwable -> 0x0505 }
        L_0x063b:
            if (r22 == 0) goto L_0x0131;	 Catch:{ Throwable -> 0x0505 }
        L_0x063d:
            r6 = r21.length();	 Catch:{ Throwable -> 0x0505 }
            r8 = 0;	 Catch:{ Throwable -> 0x0505 }
            r5 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1));	 Catch:{ Throwable -> 0x0505 }
            if (r5 == 0) goto L_0x064f;	 Catch:{ Throwable -> 0x0505 }
        L_0x0647:
            r0 = r51;	 Catch:{ Throwable -> 0x0505 }
            r5 = r0.cacheImage;	 Catch:{ Throwable -> 0x0505 }
            r5 = r5.filter;	 Catch:{ Throwable -> 0x0505 }
            if (r5 != 0) goto L_0x0131;	 Catch:{ Throwable -> 0x0505 }
        L_0x064f:
            r21.delete();	 Catch:{ Throwable -> 0x0505 }
            goto L_0x0131;	 Catch:{ Throwable -> 0x0505 }
        L_0x0654:
            r5 = android.graphics.Bitmap.Config.RGB_565;	 Catch:{ Throwable -> 0x0505 }
            r0 = r38;	 Catch:{ Throwable -> 0x0505 }
            r0.inPreferredConfig = r5;	 Catch:{ Throwable -> 0x0505 }
            goto L_0x05b9;	 Catch:{ Throwable -> 0x0505 }
        L_0x065c:
            r5 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Throwable -> 0x0505 }
            r5 = r5.getContentResolver();	 Catch:{ Throwable -> 0x0505 }
            r6 = r35.longValue();	 Catch:{ Throwable -> 0x0505 }
            r8 = 1;	 Catch:{ Throwable -> 0x0505 }
            r0 = r38;	 Catch:{ Throwable -> 0x0505 }
            r4 = android.provider.MediaStore.Images.Thumbnails.getThumbnail(r5, r6, r8, r0);	 Catch:{ Throwable -> 0x0505 }
            goto L_0x05e0;	 Catch:{ Throwable -> 0x0505 }
        L_0x066f:
            r5 = 0;	 Catch:{ Throwable -> 0x0505 }
            goto L_0x0631;	 Catch:{ Throwable -> 0x0505 }
        L_0x0671:
            r0 = r38;	 Catch:{ Throwable -> 0x0505 }
            r5 = r0.inPurgeable;	 Catch:{ Throwable -> 0x0505 }
            if (r5 == 0) goto L_0x06d5;	 Catch:{ Throwable -> 0x0505 }
        L_0x0677:
            r26 = new java.io.RandomAccessFile;	 Catch:{ Throwable -> 0x0505 }
            r5 = "r";	 Catch:{ Throwable -> 0x0505 }
            r0 = r26;	 Catch:{ Throwable -> 0x0505 }
            r1 = r21;	 Catch:{ Throwable -> 0x0505 }
            r0.<init>(r1, r5);	 Catch:{ Throwable -> 0x0505 }
            r6 = r26.length();	 Catch:{ Throwable -> 0x0505 }
            r0 = (int) r6;	 Catch:{ Throwable -> 0x0505 }
            r34 = r0;	 Catch:{ Throwable -> 0x0505 }
            r5 = org.telegram.messenger.ImageLoader.bytes;	 Catch:{ Throwable -> 0x0505 }
            if (r5 == 0) goto L_0x06d2;	 Catch:{ Throwable -> 0x0505 }
        L_0x0690:
            r5 = org.telegram.messenger.ImageLoader.bytes;	 Catch:{ Throwable -> 0x0505 }
            r5 = r5.length;	 Catch:{ Throwable -> 0x0505 }
            r0 = r34;	 Catch:{ Throwable -> 0x0505 }
            if (r5 < r0) goto L_0x06d2;	 Catch:{ Throwable -> 0x0505 }
        L_0x0699:
            r23 = org.telegram.messenger.ImageLoader.bytes;	 Catch:{ Throwable -> 0x0505 }
        L_0x069d:
            if (r23 != 0) goto L_0x06a8;	 Catch:{ Throwable -> 0x0505 }
        L_0x069f:
            r0 = r34;	 Catch:{ Throwable -> 0x0505 }
            r0 = new byte[r0];	 Catch:{ Throwable -> 0x0505 }
            r23 = r0;	 Catch:{ Throwable -> 0x0505 }
            org.telegram.messenger.ImageLoader.bytes = r23;	 Catch:{ Throwable -> 0x0505 }
        L_0x06a8:
            r5 = 0;	 Catch:{ Throwable -> 0x0505 }
            r0 = r26;	 Catch:{ Throwable -> 0x0505 }
            r1 = r23;	 Catch:{ Throwable -> 0x0505 }
            r2 = r34;	 Catch:{ Throwable -> 0x0505 }
            r0.readFully(r1, r5, r2);	 Catch:{ Throwable -> 0x0505 }
            r26.close();	 Catch:{ Throwable -> 0x0505 }
            if (r32 == 0) goto L_0x06c5;	 Catch:{ Throwable -> 0x0505 }
        L_0x06b7:
            r5 = 0;	 Catch:{ Throwable -> 0x0505 }
            r0 = r51;	 Catch:{ Throwable -> 0x0505 }
            r6 = r0.cacheImage;	 Catch:{ Throwable -> 0x0505 }
            r6 = r6.encryptionKeyPath;	 Catch:{ Throwable -> 0x0505 }
            r0 = r23;	 Catch:{ Throwable -> 0x0505 }
            r1 = r34;	 Catch:{ Throwable -> 0x0505 }
            org.telegram.messenger.secretmedia.EncryptedFileInputStream.decryptBytesWithKeyFile(r0, r5, r1, r6);	 Catch:{ Throwable -> 0x0505 }
        L_0x06c5:
            r5 = 0;	 Catch:{ Throwable -> 0x0505 }
            r0 = r23;	 Catch:{ Throwable -> 0x0505 }
            r1 = r34;	 Catch:{ Throwable -> 0x0505 }
            r2 = r38;	 Catch:{ Throwable -> 0x0505 }
            r4 = android.graphics.BitmapFactory.decodeByteArray(r0, r5, r1, r2);	 Catch:{ Throwable -> 0x0505 }
            goto L_0x0639;	 Catch:{ Throwable -> 0x0505 }
        L_0x06d2:
            r23 = 0;	 Catch:{ Throwable -> 0x0505 }
            goto L_0x069d;	 Catch:{ Throwable -> 0x0505 }
        L_0x06d5:
            if (r32 == 0) goto L_0x06f4;	 Catch:{ Throwable -> 0x0505 }
        L_0x06d7:
            r33 = new org.telegram.messenger.secretmedia.EncryptedFileInputStream;	 Catch:{ Throwable -> 0x0505 }
            r0 = r51;	 Catch:{ Throwable -> 0x0505 }
            r5 = r0.cacheImage;	 Catch:{ Throwable -> 0x0505 }
            r5 = r5.encryptionKeyPath;	 Catch:{ Throwable -> 0x0505 }
            r0 = r33;	 Catch:{ Throwable -> 0x0505 }
            r1 = r21;	 Catch:{ Throwable -> 0x0505 }
            r0.<init>(r1, r5);	 Catch:{ Throwable -> 0x0505 }
        L_0x06e6:
            r5 = 0;	 Catch:{ Throwable -> 0x0505 }
            r0 = r33;	 Catch:{ Throwable -> 0x0505 }
            r1 = r38;	 Catch:{ Throwable -> 0x0505 }
            r4 = android.graphics.BitmapFactory.decodeStream(r0, r5, r1);	 Catch:{ Throwable -> 0x0505 }
            r33.close();	 Catch:{ Throwable -> 0x0505 }
            goto L_0x0639;	 Catch:{ Throwable -> 0x0505 }
        L_0x06f4:
            r33 = new java.io.FileInputStream;	 Catch:{ Throwable -> 0x0505 }
            r0 = r33;	 Catch:{ Throwable -> 0x0505 }
            r1 = r21;	 Catch:{ Throwable -> 0x0505 }
            r0.<init>(r1);	 Catch:{ Throwable -> 0x0505 }
            goto L_0x06e6;	 Catch:{ Throwable -> 0x0505 }
        L_0x06fe:
            r17 = 0;	 Catch:{ Throwable -> 0x0505 }
            r0 = r51;	 Catch:{ Throwable -> 0x0505 }
            r5 = r0.cacheImage;	 Catch:{ Throwable -> 0x0505 }
            r5 = r5.filter;	 Catch:{ Throwable -> 0x0505 }
            if (r5 == 0) goto L_0x0770;	 Catch:{ Throwable -> 0x0505 }
        L_0x0708:
            r5 = r4.getWidth();	 Catch:{ Throwable -> 0x0505 }
            r14 = (float) r5;	 Catch:{ Throwable -> 0x0505 }
            r5 = r4.getHeight();	 Catch:{ Throwable -> 0x0505 }
            r13 = (float) r5;	 Catch:{ Throwable -> 0x0505 }
            r0 = r38;	 Catch:{ Throwable -> 0x0505 }
            r5 = r0.inPurgeable;	 Catch:{ Throwable -> 0x0505 }
            if (r5 != 0) goto L_0x073f;	 Catch:{ Throwable -> 0x0505 }
        L_0x0718:
            r5 = 0;	 Catch:{ Throwable -> 0x0505 }
            r5 = (r50 > r5 ? 1 : (r50 == r5 ? 0 : -1));	 Catch:{ Throwable -> 0x0505 }
            if (r5 == 0) goto L_0x073f;	 Catch:{ Throwable -> 0x0505 }
        L_0x071d:
            r5 = (r14 > r50 ? 1 : (r14 == r50 ? 0 : -1));	 Catch:{ Throwable -> 0x0505 }
            if (r5 == 0) goto L_0x073f;	 Catch:{ Throwable -> 0x0505 }
        L_0x0721:
            r5 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;	 Catch:{ Throwable -> 0x0505 }
            r5 = r5 + r50;	 Catch:{ Throwable -> 0x0505 }
            r5 = (r14 > r5 ? 1 : (r14 == r5 ? 0 : -1));	 Catch:{ Throwable -> 0x0505 }
            if (r5 <= 0) goto L_0x073f;	 Catch:{ Throwable -> 0x0505 }
        L_0x0729:
            r46 = r14 / r50;	 Catch:{ Throwable -> 0x0505 }
            r0 = r50;	 Catch:{ Throwable -> 0x0505 }
            r5 = (int) r0;	 Catch:{ Throwable -> 0x0505 }
            r6 = r13 / r46;	 Catch:{ Throwable -> 0x0505 }
            r6 = (int) r6;	 Catch:{ Throwable -> 0x0505 }
            r7 = 1;	 Catch:{ Throwable -> 0x0505 }
            r47 = org.telegram.messenger.Bitmaps.createScaledBitmap(r4, r5, r6, r7);	 Catch:{ Throwable -> 0x0505 }
            r0 = r47;	 Catch:{ Throwable -> 0x0505 }
            if (r4 == r0) goto L_0x073f;	 Catch:{ Throwable -> 0x0505 }
        L_0x073a:
            r4.recycle();	 Catch:{ Throwable -> 0x0505 }
            r4 = r47;	 Catch:{ Throwable -> 0x0505 }
        L_0x073f:
            if (r4 == 0) goto L_0x0770;	 Catch:{ Throwable -> 0x0505 }
        L_0x0741:
            if (r15 == 0) goto L_0x0770;	 Catch:{ Throwable -> 0x0505 }
        L_0x0743:
            r5 = NUM; // 0x42c80000 float:100.0 double:5.53552857E-315;	 Catch:{ Throwable -> 0x0505 }
            r5 = (r13 > r5 ? 1 : (r13 == r5 ? 0 : -1));	 Catch:{ Throwable -> 0x0505 }
            if (r5 >= 0) goto L_0x0770;	 Catch:{ Throwable -> 0x0505 }
        L_0x0749:
            r5 = NUM; // 0x42c80000 float:100.0 double:5.53552857E-315;	 Catch:{ Throwable -> 0x0505 }
            r5 = (r14 > r5 ? 1 : (r14 == r5 ? 0 : -1));	 Catch:{ Throwable -> 0x0505 }
            if (r5 >= 0) goto L_0x0770;	 Catch:{ Throwable -> 0x0505 }
        L_0x074f:
            r5 = r4.getConfig();	 Catch:{ Throwable -> 0x0505 }
            r6 = android.graphics.Bitmap.Config.ARGB_8888;	 Catch:{ Throwable -> 0x0505 }
            if (r5 != r6) goto L_0x076e;	 Catch:{ Throwable -> 0x0505 }
        L_0x0757:
            r5 = 3;	 Catch:{ Throwable -> 0x0505 }
            r0 = r38;	 Catch:{ Throwable -> 0x0505 }
            r6 = r0.inPurgeable;	 Catch:{ Throwable -> 0x0505 }
            if (r6 == 0) goto L_0x077d;	 Catch:{ Throwable -> 0x0505 }
        L_0x075e:
            r6 = 0;	 Catch:{ Throwable -> 0x0505 }
        L_0x075f:
            r7 = r4.getWidth();	 Catch:{ Throwable -> 0x0505 }
            r8 = r4.getHeight();	 Catch:{ Throwable -> 0x0505 }
            r9 = r4.getRowBytes();	 Catch:{ Throwable -> 0x0505 }
            org.telegram.messenger.Utilities.blurBitmap(r4, r5, r6, r7, r8, r9);	 Catch:{ Throwable -> 0x0505 }
        L_0x076e:
            r17 = 1;	 Catch:{ Throwable -> 0x0505 }
        L_0x0770:
            if (r17 != 0) goto L_0x0131;	 Catch:{ Throwable -> 0x0505 }
        L_0x0772:
            r0 = r38;	 Catch:{ Throwable -> 0x0505 }
            r5 = r0.inPurgeable;	 Catch:{ Throwable -> 0x0505 }
            if (r5 == 0) goto L_0x0131;	 Catch:{ Throwable -> 0x0505 }
        L_0x0778:
            org.telegram.messenger.Utilities.pinBitmap(r4);	 Catch:{ Throwable -> 0x0505 }
            goto L_0x0131;
        L_0x077d:
            r6 = 1;
            goto L_0x075f;
        L_0x077f:
            r5 = 0;
            goto L_0x013b;
        L_0x0782:
            r5 = move-exception;
            r43 = r44;
            goto L_0x0161;
        L_0x0787:
            r25 = move-exception;
            r43 = r44;
            goto L_0x0152;
        L_0x078c:
            r4 = r31;
            goto L_0x04f4;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ImageLoader.CacheOutTask.run():void");
        }

        private void onPostExecute(final BitmapDrawable bitmapDrawable) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    BitmapDrawable toSet = null;
                    if (bitmapDrawable instanceof AnimatedFileDrawable) {
                        toSet = bitmapDrawable;
                    } else if (bitmapDrawable != null) {
                        toSet = ImageLoader.this.memCache.get(CacheOutTask.this.cacheImage.key);
                        if (toSet == null) {
                            ImageLoader.this.memCache.put(CacheOutTask.this.cacheImage.key, bitmapDrawable);
                            toSet = bitmapDrawable;
                        } else {
                            bitmapDrawable.getBitmap().recycle();
                        }
                    }
                    final BitmapDrawable toSetFinal = toSet;
                    ImageLoader.this.imageLoadQueue.postRunnable(new Runnable() {
                        public void run() {
                            CacheOutTask.this.cacheImage.setImageAndClear(toSetFinal);
                        }
                    });
                }
            });
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

        private void reportProgress(final float progress) {
            long currentTime = System.currentTimeMillis();
            if (progress == 1.0f || this.lastProgressTime == 0 || this.lastProgressTime < currentTime - 500) {
                this.lastProgressTime = currentTime;
                Utilities.stageQueue.postRunnable(new Runnable() {

                    /* renamed from: org.telegram.messenger.ImageLoader$HttpFileTask$1$1 */
                    class C02061 implements Runnable {
                        C02061() {
                        }

                        public void run() {
                            NotificationCenter.getInstance(HttpFileTask.this.currentAccount).postNotificationName(NotificationCenter.FileLoadProgressChanged, HttpFileTask.this.url, Float.valueOf(progress));
                        }
                    }

                    public void run() {
                        ImageLoader.this.fileProgresses.put(HttpFileTask.this.url, Float.valueOf(progress));
                        AndroidUtilities.runOnUIThread(new C02061());
                    }
                });
            }
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
                FileLog.m3e(e);
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
                        FileLog.m3e(e2);
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
                        FileLog.m3e(e22);
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
                        FileLog.m3e(e222);
                    } catch (Throwable e2222) {
                        FileLog.m3e(e2222);
                    }
                }
                try {
                    if (this.fileOutputStream != null) {
                        this.fileOutputStream.close();
                        this.fileOutputStream = null;
                    }
                } catch (Throwable e22222) {
                    FileLog.m3e(e22222);
                }
                if (httpConnectionStream != null) {
                    try {
                        httpConnectionStream.close();
                    } catch (Throwable e222222) {
                        FileLog.m3e(e222222);
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
        private CacheImage cacheImage = null;
        private boolean canRetry = true;
        private RandomAccessFile fileOutputStream = null;
        private HttpURLConnection httpConnection = null;
        private int imageSize;
        private long lastProgressTime;

        /* renamed from: org.telegram.messenger.ImageLoader$HttpImageTask$3 */
        class C02123 implements Runnable {
            C02123() {
            }

            public void run() {
                ImageLoader.this.runHttpTasks(true);
            }
        }

        /* renamed from: org.telegram.messenger.ImageLoader$HttpImageTask$4 */
        class C02134 implements Runnable {
            C02134() {
            }

            public void run() {
                ImageLoader.this.runHttpTasks(true);
            }
        }

        /* renamed from: org.telegram.messenger.ImageLoader$HttpImageTask$5 */
        class C02155 implements Runnable {

            /* renamed from: org.telegram.messenger.ImageLoader$HttpImageTask$5$1 */
            class C02141 implements Runnable {
                C02141() {
                }

                public void run() {
                    NotificationCenter.getInstance(HttpImageTask.this.cacheImage.currentAccount).postNotificationName(NotificationCenter.FileDidFailedLoad, HttpImageTask.this.cacheImage.url, Integer.valueOf(1));
                }
            }

            C02155() {
            }

            public void run() {
                ImageLoader.this.fileProgresses.remove(HttpImageTask.this.cacheImage.url);
                AndroidUtilities.runOnUIThread(new C02141());
            }
        }

        public HttpImageTask(CacheImage cacheImage, int size) {
            this.cacheImage = cacheImage;
            this.imageSize = size;
        }

        private void reportProgress(final float progress) {
            long currentTime = System.currentTimeMillis();
            if (progress == 1.0f || this.lastProgressTime == 0 || this.lastProgressTime < currentTime - 500) {
                this.lastProgressTime = currentTime;
                Utilities.stageQueue.postRunnable(new Runnable() {

                    /* renamed from: org.telegram.messenger.ImageLoader$HttpImageTask$1$1 */
                    class C02081 implements Runnable {
                        C02081() {
                        }

                        public void run() {
                            NotificationCenter.getInstance(HttpImageTask.this.cacheImage.currentAccount).postNotificationName(NotificationCenter.FileLoadProgressChanged, HttpImageTask.this.cacheImage.url, Float.valueOf(progress));
                        }
                    }

                    public void run() {
                        ImageLoader.this.fileProgresses.put(HttpImageTask.this.cacheImage.url, Float.valueOf(progress));
                        AndroidUtilities.runOnUIThread(new C02081());
                    }
                });
            }
        }

        protected Boolean doInBackground(Void... voids) {
            InputStream httpConnectionStream = null;
            boolean done = false;
            if (!isCancelled()) {
                try {
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
                    FileLog.m3e(e);
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
                    FileLog.m3e(e2);
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
                        FileLog.m3e(e22);
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
                        FileLog.m3e(e222);
                    } catch (Throwable e2222) {
                        FileLog.m3e(e2222);
                    }
                }
            }
            try {
                if (this.fileOutputStream != null) {
                    this.fileOutputStream.close();
                    this.fileOutputStream = null;
                }
            } catch (Throwable e22222) {
                FileLog.m3e(e22222);
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
                    FileLog.m3e(e222222);
                }
            }
            if (!(!done || this.cacheImage.tempFilePath == null || this.cacheImage.tempFilePath.renameTo(this.cacheImage.finalFilePath))) {
                this.cacheImage.finalFilePath = this.cacheImage.tempFilePath;
            }
            return Boolean.valueOf(done);
        }

        protected void onPostExecute(final Boolean result) {
            if (result.booleanValue() || !this.canRetry) {
                ImageLoader.this.fileDidLoaded(this.cacheImage.url, this.cacheImage.finalFilePath, 0);
            } else {
                ImageLoader.this.httpFileLoadError(this.cacheImage.url);
            }
            Utilities.stageQueue.postRunnable(new Runnable() {

                /* renamed from: org.telegram.messenger.ImageLoader$HttpImageTask$2$1 */
                class C02101 implements Runnable {
                    C02101() {
                    }

                    public void run() {
                        if (result.booleanValue()) {
                            NotificationCenter.getInstance(HttpImageTask.this.cacheImage.currentAccount).postNotificationName(NotificationCenter.FileDidLoaded, HttpImageTask.this.cacheImage.url);
                            return;
                        }
                        NotificationCenter.getInstance(HttpImageTask.this.cacheImage.currentAccount).postNotificationName(NotificationCenter.FileDidFailedLoad, HttpImageTask.this.cacheImage.url, Integer.valueOf(2));
                    }
                }

                public void run() {
                    ImageLoader.this.fileProgresses.remove(HttpImageTask.this.cacheImage.url);
                    AndroidUtilities.runOnUIThread(new C02101());
                }
            });
            ImageLoader.this.imageLoadQueue.postRunnable(new C02123());
        }

        protected void onCancelled() {
            ImageLoader.this.imageLoadQueue.postRunnable(new C02134());
            Utilities.stageQueue.postRunnable(new C02155());
        }
    }

    private class ThumbGenerateInfo {
        private int count;
        private FileLocation fileLocation;
        private String filter;

        private ThumbGenerateInfo() {
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
                final String name = FileLoader.getAttachFileName(this.thumbLocation);
                ImageLoader.this.imageLoadQueue.postRunnable(new Runnable() {
                    public void run() {
                        ImageLoader.this.thumbGenerateTasks.remove(name);
                    }
                });
            }
        }

        public void run() {
            try {
                if (this.thumbLocation == null) {
                    removeTask();
                    return;
                }
                final String key = this.thumbLocation.volume_id + "_" + this.thumbLocation.local_id;
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
                final BitmapDrawable bitmapDrawable = new BitmapDrawable(originalBitmap);
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        ThumbGenerateTask.this.removeTask();
                        String kf = key;
                        if (ThumbGenerateTask.this.filter != null) {
                            kf = kf + "@" + ThumbGenerateTask.this.filter;
                        }
                        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.messageThumbGenerated, bitmapDrawable, kf);
                        ImageLoader.this.memCache.put(kf, bitmapDrawable);
                    }
                });
            } catch (Throwable e) {
                FileLog.m3e(e);
            } catch (Throwable e2) {
                FileLog.m3e(e2);
                removeTask();
            }
        }
    }

    public static ImageLoader getInstance() {
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
                        } catch (Throwable th) {
                            Throwable th2 = th;
                            localInstance = localInstance2;
                            throw th2;
                        }
                    }
                } catch (Throwable th3) {
                    th2 = th3;
                    throw th2;
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
                FileLog.m3e(e);
            }
        }
        try {
            new File(cachePath, ".nomedia").createNewFile();
        } catch (Throwable e2) {
            FileLog.m3e(e2);
        }
        mediaDirs.put(4, cachePath);
        for (int a = 0; a < 3; a++) {
            final int currentAccount = a;
            FileLoader.getInstance(a).setDelegate(new FileLoaderDelegate() {
                public void fileUploadProgressChanged(final String location, final float progress, final boolean isEncrypted) {
                    ImageLoader.this.fileProgresses.put(location, Float.valueOf(progress));
                    long currentTime = System.currentTimeMillis();
                    if (ImageLoader.this.lastProgressUpdateTime == 0 || ImageLoader.this.lastProgressUpdateTime < currentTime - 500) {
                        ImageLoader.this.lastProgressUpdateTime = currentTime;
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                NotificationCenter.getInstance(currentAccount).postNotificationName(NotificationCenter.FileUploadProgressChanged, location, Float.valueOf(progress), Boolean.valueOf(isEncrypted));
                            }
                        });
                    }
                }

                public void fileDidUploaded(String location, InputFile inputFile, InputEncryptedFile inputEncryptedFile, byte[] key, byte[] iv, long totalFileSize) {
                    final String str = location;
                    final InputFile inputFile2 = inputFile;
                    final InputEncryptedFile inputEncryptedFile2 = inputEncryptedFile;
                    final byte[] bArr = key;
                    final byte[] bArr2 = iv;
                    final long j = totalFileSize;
                    Utilities.stageQueue.postRunnable(new Runnable() {

                        /* renamed from: org.telegram.messenger.ImageLoader$2$2$1 */
                        class C01871 implements Runnable {
                            C01871() {
                            }

                            public void run() {
                                NotificationCenter.getInstance(currentAccount).postNotificationName(NotificationCenter.FileDidUpload, str, inputFile2, inputEncryptedFile2, bArr, bArr2, Long.valueOf(j));
                            }
                        }

                        public void run() {
                            AndroidUtilities.runOnUIThread(new C01871());
                            ImageLoader.this.fileProgresses.remove(str);
                        }
                    });
                }

                public void fileDidFailedUpload(final String location, final boolean isEncrypted) {
                    Utilities.stageQueue.postRunnable(new Runnable() {

                        /* renamed from: org.telegram.messenger.ImageLoader$2$3$1 */
                        class C01891 implements Runnable {
                            C01891() {
                            }

                            public void run() {
                                NotificationCenter.getInstance(currentAccount).postNotificationName(NotificationCenter.FileDidFailUpload, location, Boolean.valueOf(isEncrypted));
                            }
                        }

                        public void run() {
                            AndroidUtilities.runOnUIThread(new C01891());
                            ImageLoader.this.fileProgresses.remove(location);
                        }
                    });
                }

                public void fileDidLoaded(final String location, final File finalFile, final int type) {
                    ImageLoader.this.fileProgresses.remove(location);
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            if (SharedConfig.saveToGallery && ImageLoader.this.telegramPath != null && finalFile != null && ((location.endsWith(".mp4") || location.endsWith(".jpg")) && finalFile.toString().startsWith(ImageLoader.this.telegramPath.toString()))) {
                                AndroidUtilities.addMediaToGallery(finalFile.toString());
                            }
                            NotificationCenter.getInstance(currentAccount).postNotificationName(NotificationCenter.FileDidLoaded, location);
                            ImageLoader.this.fileDidLoaded(location, finalFile, type);
                        }
                    });
                }

                public void fileDidFailedLoad(final String location, final int canceled) {
                    ImageLoader.this.fileProgresses.remove(location);
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            ImageLoader.this.fileDidFailedLoad(location, canceled);
                            NotificationCenter.getInstance(currentAccount).postNotificationName(NotificationCenter.FileDidFailedLoad, location, Integer.valueOf(canceled));
                        }
                    });
                }

                public void fileLoadProgressChanged(final String location, final float progress) {
                    ImageLoader.this.fileProgresses.put(location, Float.valueOf(progress));
                    long currentTime = System.currentTimeMillis();
                    if (ImageLoader.this.lastProgressUpdateTime == 0 || ImageLoader.this.lastProgressUpdateTime < currentTime - 500) {
                        ImageLoader.this.lastProgressUpdateTime = currentTime;
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                NotificationCenter.getInstance(currentAccount).postNotificationName(NotificationCenter.FileLoadProgressChanged, location, Float.valueOf(progress));
                            }
                        });
                    }
                }
            });
        }
        FileLoader.setMediaDirs(mediaDirs);
        BroadcastReceiver receiver = new C01953();
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
        this.cacheOutQueue.postRunnable(new C01974());
    }

    public SparseArray<File> createMediaPaths() {
        SparseArray<File> mediaDirs = new SparseArray();
        File cachePath = AndroidUtilities.getCacheDir();
        if (!cachePath.isDirectory()) {
            try {
                cachePath.mkdirs();
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
        }
        try {
            new File(cachePath, ".nomedia").createNewFile();
        } catch (Throwable e2) {
            FileLog.m3e(e2);
        }
        mediaDirs.put(4, cachePath);
        if (BuildVars.LOGS_ENABLED) {
            FileLog.m0d("cache path = " + cachePath);
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
                                FileLog.m0d("image path = " + imagePath);
                            }
                        }
                    } catch (Throwable e22) {
                        FileLog.m3e(e22);
                    }
                    try {
                        File videoPath = new File(this.telegramPath, "Telegram Video");
                        videoPath.mkdir();
                        if (videoPath.isDirectory() && canMoveFiles(cachePath, videoPath, 2)) {
                            mediaDirs.put(2, videoPath);
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.m0d("video path = " + videoPath);
                            }
                        }
                    } catch (Throwable e222) {
                        FileLog.m3e(e222);
                    }
                    try {
                        File audioPath = new File(this.telegramPath, "Telegram Audio");
                        audioPath.mkdir();
                        if (audioPath.isDirectory() && canMoveFiles(cachePath, audioPath, 1)) {
                            new File(audioPath, ".nomedia").createNewFile();
                            mediaDirs.put(1, audioPath);
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.m0d("audio path = " + audioPath);
                            }
                        }
                    } catch (Throwable e2222) {
                        FileLog.m3e(e2222);
                    }
                    try {
                        File documentPath = new File(this.telegramPath, "Telegram Documents");
                        documentPath.mkdir();
                        if (documentPath.isDirectory() && canMoveFiles(cachePath, documentPath, 3)) {
                            new File(documentPath, ".nomedia").createNewFile();
                            mediaDirs.put(3, documentPath);
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.m0d("documents path = " + documentPath);
                            }
                        }
                    } catch (Throwable e22222) {
                        FileLog.m3e(e22222);
                    }
                }
            } else if (BuildVars.LOGS_ENABLED) {
                FileLog.m0d("this Android can't rename files");
            }
            SharedConfig.checkSaveToGalleryFiles();
        } catch (Throwable e222222) {
            FileLog.m3e(e222222);
        }
        return mediaDirs;
    }

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
                        FileLog.m3e(e);
                        if (file != null) {
                            try {
                                file.close();
                            } catch (Throwable e3) {
                                FileLog.m3e(e3);
                            }
                        }
                        return false;
                    } catch (Throwable th2) {
                        th = th2;
                        if (file != null) {
                            try {
                                file.close();
                            } catch (Throwable e32) {
                                FileLog.m3e(e32);
                            }
                        }
                        throw th;
                    }
                }
            } catch (Exception e4) {
                e32 = e4;
                FileLog.m3e(e32);
                if (file != null) {
                    file.close();
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
                        FileLog.m3e(e322);
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
                    FileLog.m3e(e3222);
                    return true;
                }
            }
        } catch (Exception e5) {
            e3222 = e5;
            file = file2;
            FileLog.m3e(e3222);
            if (file != null) {
                file.close();
            }
            return false;
        } catch (Throwable th3) {
            th = th3;
            file = file2;
            if (file != null) {
                file.close();
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

    private void performReplace(String oldKey, String newKey) {
        BitmapDrawable b = this.memCache.get(oldKey);
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

    public void cancelLoadingForImageReceiver(final ImageReceiver imageReceiver, final int type) {
        if (imageReceiver != null) {
            this.imageLoadQueue.postRunnable(new Runnable() {
                public void run() {
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
                            ImageLoader.this.removeFromWaitingForThumb(TAG);
                        }
                        if (TAG != 0) {
                            CacheImage ei = (CacheImage) ImageLoader.this.imageLoadingByTag.get(TAG);
                            if (ei != null) {
                                ei.removeImageReceiver(imageReceiver);
                            }
                        }
                        a++;
                    }
                }
            });
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
                key = location2.dc_id + "_" + location2.id;
            } else {
                key = location2.dc_id + "_" + location2.id + "_" + location2.version;
            }
        } else if (fileLocation instanceof TL_webDocument) {
            key = Utilities.MD5(((TL_webDocument) fileLocation).url);
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

    public void replaceImageInCache(final String oldKey, final String newKey, final FileLocation newLocation, boolean post) {
        if (post) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    ImageLoader.this.replaceImageInCacheInternal(oldKey, newKey, newLocation);
                }
            });
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
            final String key = imageReceiver.getKey();
            if (key != null) {
                this.imageLoadQueue.postRunnable(new Runnable() {
                    public void run() {
                        ImageLoader.this.forceLoadingImages.remove(key);
                    }
                });
            }
        }
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
            final int finalTag = TAG;
            final boolean finalIsNeedsQualityThumb = imageReceiver.isNeedsQualityThumb();
            final MessageObject parentMessageObject = imageReceiver.getParentMessageObject();
            final boolean shouldGenerateQualityThumb = imageReceiver.isShouldGenerateQualityThumb();
            final int currentAccount = imageReceiver.getcurrentAccount();
            final int i = thumb;
            final String str = url;
            final String str2 = key;
            final ImageReceiver imageReceiver2 = imageReceiver;
            final String str3 = filter;
            final String str4 = httpLocation;
            final TLObject tLObject = imageLocation;
            final int i2 = cacheType;
            final int i3 = size;
            final String str5 = ext;
            this.imageLoadQueue.postRunnable(new Runnable() {
                public void run() {
                    /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Unknown predecessor block by arg (r16_2 'info' org.telegram.messenger.ImageLoader$ThumbGenerateInfo) in PHI: PHI: (r16_3 'info' org.telegram.messenger.ImageLoader$ThumbGenerateInfo) = (r16_1 'info' org.telegram.messenger.ImageLoader$ThumbGenerateInfo), (r16_2 'info' org.telegram.messenger.ImageLoader$ThumbGenerateInfo) binds: {(r16_1 'info' org.telegram.messenger.ImageLoader$ThumbGenerateInfo)=B:113:0x0407, (r16_2 'info' org.telegram.messenger.ImageLoader$ThumbGenerateInfo)=B:114:0x0409}
	at jadx.core.dex.instructions.PhiInsn.replaceArg(PhiInsn.java:79)
	at jadx.core.dex.visitors.ModVisitor.processInvoke(ModVisitor.java:222)
	at jadx.core.dex.visitors.ModVisitor.replaceStep(ModVisitor.java:83)
	at jadx.core.dex.visitors.ModVisitor.visit(ModVisitor.java:68)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:60)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
                    /*
                    r27 = this;
                    r5 = 0;
                    r0 = r27;
                    r0 = r4;
                    r23 = r0;
                    r24 = 2;
                    r0 = r23;
                    r1 = r24;
                    if (r0 == r1) goto L_0x00ae;
                L_0x000f:
                    r0 = r27;
                    r0 = org.telegram.messenger.ImageLoader.this;
                    r23 = r0;
                    r23 = r23.imageLoadingByUrl;
                    r0 = r27;
                    r0 = r5;
                    r24 = r0;
                    r8 = r23.get(r24);
                    r8 = (org.telegram.messenger.ImageLoader.CacheImage) r8;
                    r0 = r27;
                    r0 = org.telegram.messenger.ImageLoader.this;
                    r23 = r0;
                    r23 = r23.imageLoadingByKeys;
                    r0 = r27;
                    r0 = r6;
                    r24 = r0;
                    r6 = r23.get(r24);
                    r6 = (org.telegram.messenger.ImageLoader.CacheImage) r6;
                    r0 = r27;
                    r0 = org.telegram.messenger.ImageLoader.this;
                    r23 = r0;
                    r23 = r23.imageLoadingByTag;
                    r0 = r27;
                    r0 = r7;
                    r24 = r0;
                    r7 = r23.get(r24);
                    r7 = (org.telegram.messenger.ImageLoader.CacheImage) r7;
                    if (r7 == 0) goto L_0x0056;
                L_0x0053:
                    if (r7 != r6) goto L_0x02ae;
                L_0x0055:
                    r5 = 1;
                L_0x0056:
                    if (r5 != 0) goto L_0x0082;
                L_0x0058:
                    if (r6 == 0) goto L_0x0082;
                L_0x005a:
                    r0 = r27;
                    r0 = r8;
                    r24 = r0;
                    r0 = r27;
                    r0 = r6;
                    r25 = r0;
                    r0 = r27;
                    r0 = r9;
                    r26 = r0;
                    r0 = r27;
                    r0 = r4;
                    r23 = r0;
                    if (r23 == 0) goto L_0x02ec;
                L_0x0074:
                    r23 = 1;
                L_0x0076:
                    r0 = r24;
                    r1 = r25;
                    r2 = r26;
                    r3 = r23;
                    r6.addImageReceiver(r0, r1, r2, r3);
                    r5 = 1;
                L_0x0082:
                    if (r5 != 0) goto L_0x00ae;
                L_0x0084:
                    if (r8 == 0) goto L_0x00ae;
                L_0x0086:
                    r0 = r27;
                    r0 = r8;
                    r24 = r0;
                    r0 = r27;
                    r0 = r6;
                    r25 = r0;
                    r0 = r27;
                    r0 = r9;
                    r26 = r0;
                    r0 = r27;
                    r0 = r4;
                    r23 = r0;
                    if (r23 == 0) goto L_0x02f0;
                L_0x00a0:
                    r23 = 1;
                L_0x00a2:
                    r0 = r24;
                    r1 = r25;
                    r2 = r26;
                    r3 = r23;
                    r8.addImageReceiver(r0, r1, r2, r3);
                    r5 = 1;
                L_0x00ae:
                    if (r5 != 0) goto L_0x02ad;
                L_0x00b0:
                    r21 = 0;
                    r18 = 0;
                    r11 = 0;
                    r12 = 0;
                    r0 = r27;
                    r0 = r10;
                    r23 = r0;
                    if (r23 == 0) goto L_0x0338;
                L_0x00be:
                    r0 = r27;
                    r0 = r10;
                    r23 = r0;
                    r24 = "http";
                    r23 = r23.startsWith(r24);
                    if (r23 != 0) goto L_0x0102;
                L_0x00cd:
                    r21 = 1;
                    r0 = r27;
                    r0 = r10;
                    r23 = r0;
                    r24 = "thumb://";
                    r23 = r23.startsWith(r24);
                    if (r23 == 0) goto L_0x02f4;
                L_0x00de:
                    r0 = r27;
                    r0 = r10;
                    r23 = r0;
                    r24 = ":";
                    r25 = 8;
                    r14 = r23.indexOf(r24, r25);
                    if (r14 < 0) goto L_0x0102;
                L_0x00ef:
                    r11 = new java.io.File;
                    r0 = r27;
                    r0 = r10;
                    r23 = r0;
                    r24 = r14 + 1;
                    r23 = r23.substring(r24);
                    r0 = r23;
                    r11.<init>(r0);
                L_0x0102:
                    r0 = r27;
                    r0 = r4;
                    r23 = r0;
                    r24 = 2;
                    r0 = r23;
                    r1 = r24;
                    if (r0 == r1) goto L_0x02ad;
                L_0x0110:
                    r0 = r27;
                    r0 = r13;
                    r23 = r0;
                    r0 = r23;
                    r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_documentEncrypted;
                    r23 = r0;
                    if (r23 != 0) goto L_0x012c;
                L_0x011e:
                    r0 = r27;
                    r0 = r13;
                    r23 = r0;
                    r0 = r23;
                    r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_fileEncryptedLocation;
                    r23 = r0;
                    if (r23 == 0) goto L_0x04a3;
                L_0x012c:
                    r17 = 1;
                L_0x012e:
                    r15 = new org.telegram.messenger.ImageLoader$CacheImage;
                    r0 = r27;
                    r0 = org.telegram.messenger.ImageLoader.this;
                    r23 = r0;
                    r24 = 0;
                    r0 = r23;
                    r1 = r24;
                    r15.<init>();
                    r0 = r27;
                    r0 = r10;
                    r23 = r0;
                    if (r23 == 0) goto L_0x04a7;
                L_0x0147:
                    r0 = r27;
                    r0 = r10;
                    r23 = r0;
                    r24 = "vthumb";
                    r23 = r23.startsWith(r24);
                    if (r23 != 0) goto L_0x04a7;
                L_0x0156:
                    r0 = r27;
                    r0 = r10;
                    r23 = r0;
                    r24 = "thumb";
                    r23 = r23.startsWith(r24);
                    if (r23 != 0) goto L_0x04a7;
                L_0x0165:
                    r0 = r27;
                    r0 = r10;
                    r23 = r0;
                    r24 = "jpg";
                    r22 = org.telegram.messenger.ImageLoader.getHttpUrlExtension(r23, r24);
                    r23 = "mp4";
                    r23 = r22.equals(r23);
                    if (r23 != 0) goto L_0x0184;
                L_0x017b:
                    r23 = "gif";
                    r23 = r22.equals(r23);
                    if (r23 == 0) goto L_0x018a;
                L_0x0184:
                    r23 = 1;
                    r0 = r23;
                    r15.animatedFile = r0;
                L_0x018a:
                    if (r11 != 0) goto L_0x01c2;
                L_0x018c:
                    r0 = r27;
                    r0 = r15;
                    r23 = r0;
                    if (r23 != 0) goto L_0x01a6;
                L_0x0194:
                    r0 = r27;
                    r0 = r16;
                    r23 = r0;
                    if (r23 <= 0) goto L_0x01a6;
                L_0x019c:
                    r0 = r27;
                    r0 = r10;
                    r23 = r0;
                    if (r23 != 0) goto L_0x01a6;
                L_0x01a4:
                    if (r17 == 0) goto L_0x052e;
                L_0x01a6:
                    r11 = new java.io.File;
                    r23 = 4;
                    r23 = org.telegram.messenger.FileLoader.getDirectory(r23);
                    r0 = r27;
                    r0 = r5;
                    r24 = r0;
                    r0 = r23;
                    r1 = r24;
                    r11.<init>(r0, r1);
                    r23 = r11.exists();
                    if (r23 == 0) goto L_0x04f5;
                L_0x01c1:
                    r12 = 1;
                L_0x01c2:
                    r0 = r27;
                    r0 = r4;
                    r23 = r0;
                    if (r23 == 0) goto L_0x05b4;
                L_0x01ca:
                    r23 = 1;
                L_0x01cc:
                    r0 = r23;
                    r15.selfThumb = r0;
                    r0 = r27;
                    r0 = r6;
                    r23 = r0;
                    r0 = r23;
                    r15.key = r0;
                    r0 = r27;
                    r0 = r9;
                    r23 = r0;
                    r0 = r23;
                    r15.filter = r0;
                    r0 = r27;
                    r0 = r10;
                    r23 = r0;
                    r0 = r23;
                    r15.httpUrl = r0;
                    r0 = r27;
                    r0 = r17;
                    r23 = r0;
                    r0 = r23;
                    r15.ext = r0;
                    r0 = r27;
                    r0 = r18;
                    r23 = r0;
                    r0 = r23;
                    r15.currentAccount = r0;
                    r0 = r27;
                    r0 = r15;
                    r23 = r0;
                    r24 = 2;
                    r0 = r23;
                    r1 = r24;
                    if (r0 != r1) goto L_0x0237;
                L_0x0210:
                    r23 = new java.io.File;
                    r24 = org.telegram.messenger.FileLoader.getInternalCacheDir();
                    r25 = new java.lang.StringBuilder;
                    r25.<init>();
                    r0 = r27;
                    r0 = r5;
                    r26 = r0;
                    r25 = r25.append(r26);
                    r26 = ".enc.key";
                    r25 = r25.append(r26);
                    r25 = r25.toString();
                    r23.<init>(r24, r25);
                    r0 = r23;
                    r15.encryptionKeyPath = r0;
                L_0x0237:
                    r0 = r27;
                    r0 = r8;
                    r24 = r0;
                    r0 = r27;
                    r0 = r6;
                    r25 = r0;
                    r0 = r27;
                    r0 = r9;
                    r26 = r0;
                    r0 = r27;
                    r0 = r4;
                    r23 = r0;
                    if (r23 == 0) goto L_0x05b8;
                L_0x0251:
                    r23 = 1;
                L_0x0253:
                    r0 = r24;
                    r1 = r25;
                    r2 = r26;
                    r3 = r23;
                    r15.addImageReceiver(r0, r1, r2, r3);
                    if (r21 != 0) goto L_0x0268;
                L_0x0260:
                    if (r12 != 0) goto L_0x0268;
                L_0x0262:
                    r23 = r11.exists();
                    if (r23 == 0) goto L_0x05cf;
                L_0x0268:
                    r15.finalFilePath = r11;
                    r23 = new org.telegram.messenger.ImageLoader$CacheOutTask;
                    r0 = r27;
                    r0 = org.telegram.messenger.ImageLoader.this;
                    r24 = r0;
                    r0 = r23;
                    r1 = r24;
                    r0.<init>(r15);
                    r0 = r23;
                    r15.cacheTask = r0;
                    r0 = r27;
                    r0 = org.telegram.messenger.ImageLoader.this;
                    r23 = r0;
                    r23 = r23.imageLoadingByKeys;
                    r0 = r27;
                    r0 = r6;
                    r24 = r0;
                    r0 = r23;
                    r1 = r24;
                    r0.put(r1, r15);
                    r0 = r27;
                    r0 = r4;
                    r23 = r0;
                    if (r23 == 0) goto L_0x05bc;
                L_0x029c:
                    r0 = r27;
                    r0 = org.telegram.messenger.ImageLoader.this;
                    r23 = r0;
                    r23 = r23.cacheThumbOutQueue;
                    r0 = r15.cacheTask;
                    r24 = r0;
                    r23.postRunnable(r24);
                L_0x02ad:
                    return;
                L_0x02ae:
                    if (r7 != r8) goto L_0x02df;
                L_0x02b0:
                    if (r6 != 0) goto L_0x02d9;
                L_0x02b2:
                    r0 = r27;
                    r0 = r8;
                    r24 = r0;
                    r0 = r27;
                    r0 = r6;
                    r25 = r0;
                    r0 = r27;
                    r0 = r9;
                    r26 = r0;
                    r0 = r27;
                    r0 = r4;
                    r23 = r0;
                    if (r23 == 0) goto L_0x02dc;
                L_0x02cc:
                    r23 = 1;
                L_0x02ce:
                    r0 = r24;
                    r1 = r25;
                    r2 = r26;
                    r3 = r23;
                    r7.replaceImageReceiver(r0, r1, r2, r3);
                L_0x02d9:
                    r5 = 1;
                    goto L_0x0056;
                L_0x02dc:
                    r23 = 0;
                    goto L_0x02ce;
                L_0x02df:
                    r0 = r27;
                    r0 = r8;
                    r23 = r0;
                    r0 = r23;
                    r7.removeImageReceiver(r0);
                    goto L_0x0056;
                L_0x02ec:
                    r23 = 0;
                    goto L_0x0076;
                L_0x02f0:
                    r23 = 0;
                    goto L_0x00a2;
                L_0x02f4:
                    r0 = r27;
                    r0 = r10;
                    r23 = r0;
                    r24 = "vthumb://";
                    r23 = r23.startsWith(r24);
                    if (r23 == 0) goto L_0x0329;
                L_0x0303:
                    r0 = r27;
                    r0 = r10;
                    r23 = r0;
                    r24 = ":";
                    r25 = 9;
                    r14 = r23.indexOf(r24, r25);
                    if (r14 < 0) goto L_0x0102;
                L_0x0314:
                    r11 = new java.io.File;
                    r0 = r27;
                    r0 = r10;
                    r23 = r0;
                    r24 = r14 + 1;
                    r23 = r23.substring(r24);
                    r0 = r23;
                    r11.<init>(r0);
                    goto L_0x0102;
                L_0x0329:
                    r11 = new java.io.File;
                    r0 = r27;
                    r0 = r10;
                    r23 = r0;
                    r0 = r23;
                    r11.<init>(r0);
                    goto L_0x0102;
                L_0x0338:
                    r0 = r27;
                    r0 = r4;
                    r23 = r0;
                    if (r23 == 0) goto L_0x0102;
                L_0x0340:
                    r0 = r27;
                    r0 = r11;
                    r23 = r0;
                    if (r23 == 0) goto L_0x0378;
                L_0x0348:
                    r11 = new java.io.File;
                    r23 = 4;
                    r23 = org.telegram.messenger.FileLoader.getDirectory(r23);
                    r24 = new java.lang.StringBuilder;
                    r24.<init>();
                    r25 = "q_";
                    r24 = r24.append(r25);
                    r0 = r27;
                    r0 = r5;
                    r25 = r0;
                    r24 = r24.append(r25);
                    r24 = r24.toString();
                    r0 = r23;
                    r1 = r24;
                    r11.<init>(r0, r1);
                    r23 = r11.exists();
                    if (r23 != 0) goto L_0x04a0;
                L_0x0377:
                    r11 = 0;
                L_0x0378:
                    r0 = r27;
                    r0 = r12;
                    r23 = r0;
                    if (r23 == 0) goto L_0x0102;
                L_0x0380:
                    r9 = 0;
                    r0 = r27;
                    r0 = r12;
                    r23 = r0;
                    r0 = r23;
                    r0 = r0.messageOwner;
                    r23 = r0;
                    r0 = r23;
                    r0 = r0.attachPath;
                    r23 = r0;
                    if (r23 == 0) goto L_0x03cd;
                L_0x0395:
                    r0 = r27;
                    r0 = r12;
                    r23 = r0;
                    r0 = r23;
                    r0 = r0.messageOwner;
                    r23 = r0;
                    r0 = r23;
                    r0 = r0.attachPath;
                    r23 = r0;
                    r23 = r23.length();
                    if (r23 <= 0) goto L_0x03cd;
                L_0x03ad:
                    r9 = new java.io.File;
                    r0 = r27;
                    r0 = r12;
                    r23 = r0;
                    r0 = r23;
                    r0 = r0.messageOwner;
                    r23 = r0;
                    r0 = r23;
                    r0 = r0.attachPath;
                    r23 = r0;
                    r0 = r23;
                    r9.<init>(r0);
                    r23 = r9.exists();
                    if (r23 != 0) goto L_0x03cd;
                L_0x03cc:
                    r9 = 0;
                L_0x03cd:
                    if (r9 != 0) goto L_0x03df;
                L_0x03cf:
                    r0 = r27;
                    r0 = r12;
                    r23 = r0;
                    r0 = r23;
                    r0 = r0.messageOwner;
                    r23 = r0;
                    r9 = org.telegram.messenger.FileLoader.getPathToMessage(r23);
                L_0x03df:
                    r0 = r27;
                    r0 = r11;
                    r23 = r0;
                    if (r23 == 0) goto L_0x0467;
                L_0x03e7:
                    if (r11 != 0) goto L_0x0467;
                L_0x03e9:
                    r0 = r27;
                    r0 = r12;
                    r23 = r0;
                    r20 = r23.getFileName();
                    r0 = r27;
                    r0 = org.telegram.messenger.ImageLoader.this;
                    r23 = r0;
                    r23 = r23.waitingForQualityThumb;
                    r0 = r23;
                    r1 = r20;
                    r16 = r0.get(r1);
                    r16 = (org.telegram.messenger.ImageLoader.ThumbGenerateInfo) r16;
                    if (r16 != 0) goto L_0x044b;
                L_0x0409:
                    r16 = new org.telegram.messenger.ImageLoader$ThumbGenerateInfo;
                    r0 = r27;
                    r0 = org.telegram.messenger.ImageLoader.this;
                    r23 = r0;
                    r24 = 0;
                    r0 = r16;
                    r1 = r23;
                    r2 = r24;
                    r0.<init>();
                    r0 = r27;
                    r0 = r13;
                    r23 = r0;
                    r23 = (org.telegram.tgnet.TLRPC.FileLocation) r23;
                    r0 = r16;
                    r1 = r23;
                    r0.fileLocation = r1;
                    r0 = r27;
                    r0 = r9;
                    r23 = r0;
                    r0 = r16;
                    r1 = r23;
                    r0.filter = r1;
                    r0 = r27;
                    r0 = org.telegram.messenger.ImageLoader.this;
                    r23 = r0;
                    r23 = r23.waitingForQualityThumb;
                    r0 = r23;
                    r1 = r20;
                    r2 = r16;
                    r0.put(r1, r2);
                L_0x044b:
                    r16.count = r16.count + 1;
                    r0 = r27;
                    r0 = org.telegram.messenger.ImageLoader.this;
                    r23 = r0;
                    r23 = r23.waitingForQualityThumbByTag;
                    r0 = r27;
                    r0 = r7;
                    r24 = r0;
                    r0 = r23;
                    r1 = r24;
                    r2 = r20;
                    r0.put(r1, r2);
                L_0x0467:
                    r23 = r9.exists();
                    if (r23 == 0) goto L_0x0102;
                L_0x046d:
                    r0 = r27;
                    r0 = r14;
                    r23 = r0;
                    if (r23 == 0) goto L_0x0102;
                L_0x0475:
                    r0 = r27;
                    r0 = org.telegram.messenger.ImageLoader.this;
                    r24 = r0;
                    r0 = r27;
                    r0 = r12;
                    r23 = r0;
                    r25 = r23.getFileType();
                    r0 = r27;
                    r0 = r13;
                    r23 = r0;
                    r23 = (org.telegram.tgnet.TLRPC.FileLocation) r23;
                    r0 = r27;
                    r0 = r9;
                    r26 = r0;
                    r0 = r24;
                    r1 = r25;
                    r2 = r23;
                    r3 = r26;
                    r0.generateThumb(r1, r9, r2, r3);
                    goto L_0x0102;
                L_0x04a0:
                    r12 = 1;
                    goto L_0x0378;
                L_0x04a3:
                    r17 = 0;
                    goto L_0x012e;
                L_0x04a7:
                    r0 = r27;
                    r0 = r13;
                    r23 = r0;
                    r0 = r23;
                    r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_webDocument;
                    r23 = r0;
                    if (r23 == 0) goto L_0x04c3;
                L_0x04b5:
                    r0 = r27;
                    r0 = r13;
                    r23 = r0;
                    r23 = (org.telegram.tgnet.TLRPC.TL_webDocument) r23;
                    r23 = org.telegram.messenger.MessageObject.isGifDocument(r23);
                    if (r23 != 0) goto L_0x04ed;
                L_0x04c3:
                    r0 = r27;
                    r0 = r13;
                    r23 = r0;
                    r0 = r23;
                    r0 = r0 instanceof org.telegram.tgnet.TLRPC.Document;
                    r23 = r0;
                    if (r23 == 0) goto L_0x018a;
                L_0x04d1:
                    r0 = r27;
                    r0 = r13;
                    r23 = r0;
                    r23 = (org.telegram.tgnet.TLRPC.Document) r23;
                    r23 = org.telegram.messenger.MessageObject.isGifDocument(r23);
                    if (r23 != 0) goto L_0x04ed;
                L_0x04df:
                    r0 = r27;
                    r0 = r13;
                    r23 = r0;
                    r23 = (org.telegram.tgnet.TLRPC.Document) r23;
                    r23 = org.telegram.messenger.MessageObject.isRoundVideoDocument(r23);
                    if (r23 == 0) goto L_0x018a;
                L_0x04ed:
                    r23 = 1;
                    r0 = r23;
                    r15.animatedFile = r0;
                    goto L_0x018a;
                L_0x04f5:
                    r0 = r27;
                    r0 = r15;
                    r23 = r0;
                    r24 = 2;
                    r0 = r23;
                    r1 = r24;
                    if (r0 != r1) goto L_0x01c2;
                L_0x0503:
                    r11 = new java.io.File;
                    r23 = 4;
                    r23 = org.telegram.messenger.FileLoader.getDirectory(r23);
                    r24 = new java.lang.StringBuilder;
                    r24.<init>();
                    r0 = r27;
                    r0 = r5;
                    r25 = r0;
                    r24 = r24.append(r25);
                    r25 = ".enc";
                    r24 = r24.append(r25);
                    r24 = r24.toString();
                    r0 = r23;
                    r1 = r24;
                    r11.<init>(r0, r1);
                    goto L_0x01c2;
                L_0x052e:
                    r0 = r27;
                    r0 = r13;
                    r23 = r0;
                    r0 = r23;
                    r0 = r0 instanceof org.telegram.tgnet.TLRPC.Document;
                    r23 = r0;
                    if (r23 == 0) goto L_0x0578;
                L_0x053c:
                    r0 = r27;
                    r0 = r13;
                    r23 = r0;
                    r23 = (org.telegram.tgnet.TLRPC.Document) r23;
                    r23 = org.telegram.messenger.MessageObject.isVideoDocument(r23);
                    if (r23 == 0) goto L_0x0561;
                L_0x054a:
                    r11 = new java.io.File;
                    r23 = 2;
                    r23 = org.telegram.messenger.FileLoader.getDirectory(r23);
                    r0 = r27;
                    r0 = r5;
                    r24 = r0;
                    r0 = r23;
                    r1 = r24;
                    r11.<init>(r0, r1);
                    goto L_0x01c2;
                L_0x0561:
                    r11 = new java.io.File;
                    r23 = 3;
                    r23 = org.telegram.messenger.FileLoader.getDirectory(r23);
                    r0 = r27;
                    r0 = r5;
                    r24 = r0;
                    r0 = r23;
                    r1 = r24;
                    r11.<init>(r0, r1);
                    goto L_0x01c2;
                L_0x0578:
                    r0 = r27;
                    r0 = r13;
                    r23 = r0;
                    r0 = r23;
                    r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_webDocument;
                    r23 = r0;
                    if (r23 == 0) goto L_0x059d;
                L_0x0586:
                    r11 = new java.io.File;
                    r23 = 3;
                    r23 = org.telegram.messenger.FileLoader.getDirectory(r23);
                    r0 = r27;
                    r0 = r5;
                    r24 = r0;
                    r0 = r23;
                    r1 = r24;
                    r11.<init>(r0, r1);
                    goto L_0x01c2;
                L_0x059d:
                    r11 = new java.io.File;
                    r23 = 0;
                    r23 = org.telegram.messenger.FileLoader.getDirectory(r23);
                    r0 = r27;
                    r0 = r5;
                    r24 = r0;
                    r0 = r23;
                    r1 = r24;
                    r11.<init>(r0, r1);
                    goto L_0x01c2;
                L_0x05b4:
                    r23 = 0;
                    goto L_0x01cc;
                L_0x05b8:
                    r23 = 0;
                    goto L_0x0253;
                L_0x05bc:
                    r0 = r27;
                    r0 = org.telegram.messenger.ImageLoader.this;
                    r23 = r0;
                    r23 = r23.cacheOutQueue;
                    r0 = r15.cacheTask;
                    r24 = r0;
                    r23.postRunnable(r24);
                    goto L_0x02ad;
                L_0x05cf:
                    r0 = r27;
                    r0 = r5;
                    r23 = r0;
                    r0 = r23;
                    r15.url = r0;
                    r0 = r27;
                    r0 = r13;
                    r23 = r0;
                    r0 = r23;
                    r15.location = r0;
                    r0 = r27;
                    r0 = org.telegram.messenger.ImageLoader.this;
                    r23 = r0;
                    r23 = r23.imageLoadingByUrl;
                    r0 = r27;
                    r0 = r5;
                    r24 = r0;
                    r0 = r23;
                    r1 = r24;
                    r0.put(r1, r15);
                    r0 = r27;
                    r0 = r10;
                    r23 = r0;
                    if (r23 != 0) goto L_0x06e3;
                L_0x0602:
                    r0 = r27;
                    r0 = r13;
                    r23 = r0;
                    r0 = r23;
                    r0 = r0 instanceof org.telegram.tgnet.TLRPC.FileLocation;
                    r23 = r0;
                    if (r23 == 0) goto L_0x067a;
                L_0x0610:
                    r0 = r27;
                    r0 = r13;
                    r20 = r0;
                    r20 = (org.telegram.tgnet.TLRPC.FileLocation) r20;
                    r0 = r27;
                    r0 = r15;
                    r19 = r0;
                    if (r19 != 0) goto L_0x0632;
                L_0x0620:
                    r0 = r27;
                    r0 = r16;
                    r23 = r0;
                    if (r23 <= 0) goto L_0x0630;
                L_0x0628:
                    r0 = r20;
                    r0 = r0.key;
                    r23 = r0;
                    if (r23 == 0) goto L_0x0632;
                L_0x0630:
                    r19 = 1;
                L_0x0632:
                    r0 = r27;
                    r0 = r18;
                    r23 = r0;
                    r23 = org.telegram.messenger.FileLoader.getInstance(r23);
                    r0 = r27;
                    r0 = r17;
                    r24 = r0;
                    r0 = r27;
                    r0 = r16;
                    r25 = r0;
                    r0 = r23;
                    r1 = r20;
                    r2 = r24;
                    r3 = r25;
                    r4 = r19;
                    r0.loadFile(r1, r2, r3, r4);
                L_0x0655:
                    r0 = r27;
                    r0 = r8;
                    r23 = r0;
                    r23 = r23.isForceLoding();
                    if (r23 == 0) goto L_0x02ad;
                L_0x0661:
                    r0 = r27;
                    r0 = org.telegram.messenger.ImageLoader.this;
                    r23 = r0;
                    r23 = r23.forceLoadingImages;
                    r0 = r15.key;
                    r24 = r0;
                    r25 = 0;
                    r25 = java.lang.Integer.valueOf(r25);
                    r23.put(r24, r25);
                    goto L_0x02ad;
                L_0x067a:
                    r0 = r27;
                    r0 = r13;
                    r23 = r0;
                    r0 = r23;
                    r0 = r0 instanceof org.telegram.tgnet.TLRPC.Document;
                    r23 = r0;
                    if (r23 == 0) goto L_0x06ae;
                L_0x0688:
                    r0 = r27;
                    r0 = r18;
                    r23 = r0;
                    r24 = org.telegram.messenger.FileLoader.getInstance(r23);
                    r0 = r27;
                    r0 = r13;
                    r23 = r0;
                    r23 = (org.telegram.tgnet.TLRPC.Document) r23;
                    r25 = 1;
                    r0 = r27;
                    r0 = r15;
                    r26 = r0;
                    r0 = r24;
                    r1 = r23;
                    r2 = r25;
                    r3 = r26;
                    r0.loadFile(r1, r2, r3);
                    goto L_0x0655;
                L_0x06ae:
                    r0 = r27;
                    r0 = r13;
                    r23 = r0;
                    r0 = r23;
                    r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_webDocument;
                    r23 = r0;
                    if (r23 == 0) goto L_0x0655;
                L_0x06bc:
                    r0 = r27;
                    r0 = r18;
                    r23 = r0;
                    r24 = org.telegram.messenger.FileLoader.getInstance(r23);
                    r0 = r27;
                    r0 = r13;
                    r23 = r0;
                    r23 = (org.telegram.tgnet.TLRPC.TL_webDocument) r23;
                    r25 = 1;
                    r0 = r27;
                    r0 = r15;
                    r26 = r0;
                    r0 = r24;
                    r1 = r23;
                    r2 = r25;
                    r3 = r26;
                    r0.loadFile(r1, r2, r3);
                    goto L_0x0655;
                L_0x06e3:
                    r0 = r27;
                    r0 = r10;
                    r23 = r0;
                    r13 = org.telegram.messenger.Utilities.MD5(r23);
                    r23 = 4;
                    r10 = org.telegram.messenger.FileLoader.getDirectory(r23);
                    r23 = new java.io.File;
                    r24 = new java.lang.StringBuilder;
                    r24.<init>();
                    r0 = r24;
                    r24 = r0.append(r13);
                    r25 = "_temp.jpg";
                    r24 = r24.append(r25);
                    r24 = r24.toString();
                    r0 = r23;
                    r1 = r24;
                    r0.<init>(r10, r1);
                    r0 = r23;
                    r15.tempFilePath = r0;
                    r15.finalFilePath = r11;
                    r23 = new org.telegram.messenger.ImageLoader$HttpImageTask;
                    r0 = r27;
                    r0 = org.telegram.messenger.ImageLoader.this;
                    r24 = r0;
                    r0 = r27;
                    r0 = r16;
                    r25 = r0;
                    r0 = r23;
                    r1 = r24;
                    r2 = r25;
                    r0.<init>(r15, r2);
                    r0 = r23;
                    r15.httpTask = r0;
                    r0 = r27;
                    r0 = org.telegram.messenger.ImageLoader.this;
                    r23 = r0;
                    r23 = r23.httpTasks;
                    r0 = r15.httpTask;
                    r24 = r0;
                    r23.add(r24);
                    r0 = r27;
                    r0 = org.telegram.messenger.ImageLoader.this;
                    r23 = r0;
                    r24 = 0;
                    r23.runHttpTasks(r24);
                    goto L_0x02ad;
                    */
                    throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ImageLoader.8.run():void");
                }
            });
        }
    }

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
                } else if (imageLocation instanceof TL_webDocument) {
                    TL_webDocument document = (TL_webDocument) imageLocation;
                    String defaultExt = FileLoader.getExtensionByMime(document.mime_type);
                    key = Utilities.MD5(document.url);
                    url = key + "." + getHttpUrlExtension(document.url, defaultExt);
                } else if (imageLocation instanceof Document) {
                    Document document2 = (Document) imageLocation;
                    if (document2.id != 0 && document2.dc_id != 0) {
                        if (document2.version == 0) {
                            key = document2.dc_id + "_" + document2.id;
                        } else {
                            key = document2.dc_id + "_" + document2.id + "_" + document2.version;
                        }
                        String docExt = FileLoader.getDocumentFileName(document2);
                        if (docExt != null) {
                            int idx = docExt.lastIndexOf(46);
                            if (idx != -1) {
                                docExt = docExt.substring(idx);
                                if (docExt.length() <= 1) {
                                    if (document2.mime_type == null && document2.mime_type.equals(MimeTypes.VIDEO_MP4)) {
                                        docExt = ".mp4";
                                    } else {
                                        docExt = TtmlNode.ANONYMOUS_REGION_ID;
                                    }
                                }
                                url = key + docExt;
                                if (null != null) {
                                    thumbUrl = null + "." + ext;
                                }
                                if (!MessageObject.isGifDocument(document2) || MessageObject.isRoundVideoDocument((Document) imageLocation)) {
                                    saveImageToCache = false;
                                } else {
                                    saveImageToCache = true;
                                }
                            }
                        }
                        docExt = TtmlNode.ANONYMOUS_REGION_ID;
                        if (docExt.length() <= 1) {
                            if (document2.mime_type == null) {
                            }
                            docExt = TtmlNode.ANONYMOUS_REGION_ID;
                        }
                        url = key + docExt;
                        if (null != null) {
                            thumbUrl = null + "." + ext;
                        }
                        if (MessageObject.isGifDocument(document2)) {
                        }
                        saveImageToCache = false;
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

    private void httpFileLoadError(final String location) {
        this.imageLoadQueue.postRunnable(new Runnable() {
            public void run() {
                CacheImage img = (CacheImage) ImageLoader.this.imageLoadingByUrl.get(location);
                if (img != null) {
                    HttpImageTask oldTask = img.httpTask;
                    img.httpTask = new HttpImageTask(oldTask.cacheImage, oldTask.imageSize);
                    ImageLoader.this.httpTasks.add(img.httpTask);
                    ImageLoader.this.runHttpTasks(false);
                }
            }
        });
    }

    private void fileDidLoaded(final String location, final File finalFile, final int type) {
        this.imageLoadQueue.postRunnable(new Runnable() {
            public void run() {
                ThumbGenerateInfo info = (ThumbGenerateInfo) ImageLoader.this.waitingForQualityThumb.get(location);
                if (info != null) {
                    ImageLoader.this.generateThumb(type, finalFile, info.fileLocation, info.filter);
                    ImageLoader.this.waitingForQualityThumb.remove(location);
                }
                CacheImage img = (CacheImage) ImageLoader.this.imageLoadingByUrl.get(location);
                if (img != null) {
                    int a;
                    ImageLoader.this.imageLoadingByUrl.remove(location);
                    ArrayList<CacheOutTask> tasks = new ArrayList();
                    for (a = 0; a < img.imageReceiverArray.size(); a++) {
                        String key = (String) img.keys.get(a);
                        String filter = (String) img.filters.get(a);
                        Boolean thumb = (Boolean) img.thumbs.get(a);
                        ImageReceiver imageReceiver = (ImageReceiver) img.imageReceiverArray.get(a);
                        CacheImage cacheImage = (CacheImage) ImageLoader.this.imageLoadingByKeys.get(key);
                        if (cacheImage == null) {
                            cacheImage = new CacheImage();
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
                            ImageLoader.this.imageLoadingByKeys.put(key, cacheImage);
                            tasks.add(cacheImage.cacheTask);
                        }
                        cacheImage.addImageReceiver(imageReceiver, key, filter, thumb.booleanValue());
                    }
                    for (a = 0; a < tasks.size(); a++) {
                        CacheOutTask task = (CacheOutTask) tasks.get(a);
                        if (task.cacheImage.selfThumb) {
                            ImageLoader.this.cacheThumbOutQueue.postRunnable(task);
                        } else {
                            ImageLoader.this.cacheOutQueue.postRunnable(task);
                        }
                    }
                }
            }
        });
    }

    private void fileDidFailedLoad(final String location, int canceled) {
        if (canceled != 1) {
            this.imageLoadQueue.postRunnable(new Runnable() {
                public void run() {
                    CacheImage img = (CacheImage) ImageLoader.this.imageLoadingByUrl.get(location);
                    if (img != null) {
                        img.setImageAndClear(null);
                    }
                }
            });
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

    private void runHttpFileLoadTasks(final HttpFileTask oldTask, final int reason) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                if (oldTask != null) {
                    ImageLoader.this.currentHttpFileLoadTasksCount = ImageLoader.this.currentHttpFileLoadTasksCount - 1;
                }
                if (oldTask != null) {
                    if (reason == 1) {
                        if (oldTask.canRetry) {
                            final HttpFileTask newTask = new HttpFileTask(oldTask.url, oldTask.tempFile, oldTask.ext, oldTask.currentAccount);
                            Runnable runnable = new Runnable() {
                                public void run() {
                                    ImageLoader.this.httpFileLoadTasks.add(newTask);
                                    ImageLoader.this.runHttpFileLoadTasks(null, 0);
                                }
                            };
                            ImageLoader.this.retryHttpsTasks.put(oldTask.url, runnable);
                            AndroidUtilities.runOnUIThread(runnable, 1000);
                        } else {
                            ImageLoader.this.httpFileLoadTasksByKeys.remove(oldTask.url);
                            NotificationCenter.getInstance(oldTask.currentAccount).postNotificationName(NotificationCenter.httpFileDidFailedLoad, oldTask.url, Integer.valueOf(0));
                        }
                    } else if (reason == 2) {
                        ImageLoader.this.httpFileLoadTasksByKeys.remove(oldTask.url);
                        File file = new File(FileLoader.getDirectory(4), Utilities.MD5(oldTask.url) + "." + oldTask.ext);
                        String result = oldTask.tempFile.renameTo(file) ? file.toString() : oldTask.tempFile.toString();
                        NotificationCenter.getInstance(oldTask.currentAccount).postNotificationName(NotificationCenter.httpFileDidLoaded, oldTask.url, result);
                    }
                }
                while (ImageLoader.this.currentHttpFileLoadTasksCount < 2 && !ImageLoader.this.httpFileLoadTasks.isEmpty()) {
                    ((HttpFileTask) ImageLoader.this.httpFileLoadTasks.poll()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                    ImageLoader.this.currentHttpFileLoadTasksCount = ImageLoader.this.currentHttpFileLoadTasksCount + 1;
                }
            }
        });
    }

    /* JADX WARNING: inconsistent code. */
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
                    FileLog.m3e(e);
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
                FileLog.m3e(e2);
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
                FileLog.m3e(e22);
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
                    FileLog.m3e(e23);
                    return b;
                }
            } catch (Throwable e232) {
                FileLog.m3e(e232);
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
                FileLog.m3e(e);
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
        size.f43w = scaledBitmap.getWidth();
        size.f42h = scaledBitmap.getHeight();
        if (size.f43w <= 100 && size.f42h <= 100) {
            size.type = "s";
        } else if (size.f43w <= 320 && size.f42h <= 320) {
            size.type = "m";
        } else if (size.f43w <= 800 && size.f42h <= 800) {
            size.type = "x";
        } else if (size.f43w > 1280 || size.f42h > 1280) {
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
            FileLog.m3e(e2);
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
                        FileLog.m3e(e);
                    }
                }
                RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rws");
                randomAccessFile.write(photoSize.bytes);
                randomAccessFile.close();
            }
            PhotoSize newPhotoSize = new TL_photoSize();
            newPhotoSize.w = photoSize.f43w;
            newPhotoSize.h = photoSize.f42h;
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
