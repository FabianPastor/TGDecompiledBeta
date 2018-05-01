package org.telegram.messenger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
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
import java.io.OutputStream;
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
import org.telegram.messenger.exoplayer2.DefaultLoadControl;
import org.telegram.messenger.exoplayer2.util.MimeTypes;
import org.telegram.messenger.support.widget.helper.ItemTouchHelper.Callback;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.TL_documentEncrypted;
import org.telegram.tgnet.TLRPC.TL_fileEncryptedLocation;
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
    private static volatile ImageLoader Instance;
    private static byte[] bytes;
    private static byte[] bytesThumb;
    private static byte[] header = new byte[12];
    private static byte[] headerThumb = new byte[12];
    private HashMap<String, Integer> bitmapUseCounts = new HashMap();
    private DispatchQueue cacheOutQueue = new DispatchQueue("cacheOutQueue");
    private DispatchQueue cacheThumbOutQueue = new DispatchQueue("cacheThumbOutQueue");
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
    private LruCache memCache;
    private HashMap<String, Runnable> retryHttpsTasks;
    private File telegramPath;
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

        public void onReceive(Context context, Intent intent) {
            if (BuildVars.LOGS_ENABLED != null) {
                FileLog.m0d("file system changed");
            }
            context = new C01941();
            if ("android.intent.action.MEDIA_UNMOUNTED".equals(intent.getAction()) != null) {
                AndroidUtilities.runOnUIThread(context, 1000);
            } else {
                context.run();
            }
        }
    }

    /* renamed from: org.telegram.messenger.ImageLoader$4 */
    class C01974 implements Runnable {
        C01974() {
        }

        public void run() {
            final SparseArray createMediaPaths = ImageLoader.this.createMediaPaths();
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    FileLoader.setMediaDirs(createMediaPaths);
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

        public void addImageReceiver(ImageReceiver imageReceiver, String str, String str2, boolean z) {
            if (!this.imageReceiverArray.contains(imageReceiver)) {
                this.imageReceiverArray.add(imageReceiver);
                this.keys.add(str);
                this.filters.add(str2);
                this.thumbs.add(Boolean.valueOf(z));
                ImageLoader.this.imageLoadingByTag.put(imageReceiver.getTag(z), this);
            }
        }

        public void replaceImageReceiver(ImageReceiver imageReceiver, String str, String str2, boolean z) {
            int indexOf = this.imageReceiverArray.indexOf(imageReceiver);
            if (indexOf != -1) {
                if (((Boolean) this.thumbs.get(indexOf)).booleanValue() != z) {
                    indexOf = this.imageReceiverArray.subList(indexOf + 1, this.imageReceiverArray.size()).indexOf(imageReceiver);
                    if (indexOf == -1) {
                        return;
                    }
                }
                this.keys.set(indexOf, str);
                this.filters.set(indexOf, str2);
            }
        }

        public void removeImageReceiver(ImageReceiver imageReceiver) {
            int i = 0;
            Boolean valueOf = Boolean.valueOf(this.selfThumb);
            int i2 = 0;
            while (i2 < this.imageReceiverArray.size()) {
                ImageReceiver imageReceiver2 = (ImageReceiver) this.imageReceiverArray.get(i2);
                if (imageReceiver2 == null || imageReceiver2 == imageReceiver) {
                    this.imageReceiverArray.remove(i2);
                    this.keys.remove(i2);
                    this.filters.remove(i2);
                    valueOf = (Boolean) this.thumbs.remove(i2);
                    if (imageReceiver2 != null) {
                        ImageLoader.this.imageLoadingByTag.remove(imageReceiver2.getTag(valueOf.booleanValue()));
                    }
                    i2--;
                }
                i2++;
            }
            if (this.imageReceiverArray.size() == null) {
                while (i < this.imageReceiverArray.size()) {
                    ImageLoader.this.imageLoadingByTag.remove(((ImageReceiver) this.imageReceiverArray.get(i)).getTag(valueOf.booleanValue()));
                    i++;
                }
                this.imageReceiverArray.clear();
                if (this.location != null && ImageLoader.this.forceLoadingImages.containsKey(this.key) == null) {
                    if ((this.location instanceof FileLocation) != null) {
                        FileLoader.getInstance(this.currentAccount).cancelLoadFile((FileLocation) this.location, this.ext);
                    } else if ((this.location instanceof Document) != null) {
                        FileLoader.getInstance(this.currentAccount).cancelLoadFile((Document) this.location);
                    } else if ((this.location instanceof TL_webDocument) != null) {
                        FileLoader.getInstance(this.currentAccount).cancelLoadFile((TL_webDocument) this.location);
                    }
                }
                if (this.cacheTask != null) {
                    if (this.selfThumb != null) {
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

        public void setImageAndClear(final BitmapDrawable bitmapDrawable) {
            if (bitmapDrawable != null) {
                final ArrayList arrayList = new ArrayList(this.imageReceiverArray);
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        if (bitmapDrawable instanceof AnimatedFileDrawable) {
                            BitmapDrawable bitmapDrawable = (AnimatedFileDrawable) bitmapDrawable;
                            int i = 0;
                            int i2 = i;
                            while (i < arrayList.size()) {
                                BitmapDrawable bitmapDrawable2;
                                ImageReceiver imageReceiver = (ImageReceiver) arrayList.get(i);
                                if (i == 0) {
                                    bitmapDrawable2 = bitmapDrawable;
                                } else {
                                    bitmapDrawable2 = bitmapDrawable.makeCopy();
                                }
                                if (imageReceiver.setImageBitmapByKey(bitmapDrawable2, CacheImage.this.key, CacheImage.this.selfThumb, false)) {
                                    i2 = 1;
                                }
                                i++;
                            }
                            if (i2 == 0) {
                                ((AnimatedFileDrawable) bitmapDrawable).recycle();
                                return;
                            }
                            return;
                        }
                        for (int i3 = 0; i3 < arrayList.size(); i3++) {
                            ((ImageReceiver) arrayList.get(i3)).setImageBitmapByKey(bitmapDrawable, CacheImage.this.key, CacheImage.this.selfThumb, false);
                        }
                    }
                });
            }
            for (bitmapDrawable = null; bitmapDrawable < this.imageReceiverArray.size(); bitmapDrawable++) {
                ImageLoader.this.imageLoadingByTag.remove(((ImageReceiver) this.imageReceiverArray.get(bitmapDrawable)).getTag(this.selfThumb));
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

        public CacheOutTask(CacheImage cacheImage) {
            this.cacheImage = cacheImage;
        }

        public void run() {
            /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Unknown predecessor block by arg (r7_26 java.lang.Throwable) in PHI: PHI: (r7_27 android.graphics.Bitmap) = (r7_19 android.graphics.Bitmap), (r7_19 android.graphics.Bitmap), (r7_19 android.graphics.Bitmap), (r7_26 java.lang.Throwable), (r7_26 java.lang.Throwable) binds: {(r7_19 android.graphics.Bitmap)=B:102:0x0125, (r7_19 android.graphics.Bitmap)=B:111:0x0138, (r7_19 android.graphics.Bitmap)=B:121:0x018a, (r7_26 java.lang.Throwable)=B:177:0x02a3, (r7_26 java.lang.Throwable)=B:178:?}
	at jadx.core.dex.instructions.PhiInsn.replaceArg(PhiInsn.java:79)
	at jadx.core.dex.visitors.ssa.EliminatePhiNodes.replaceMerge(EliminatePhiNodes.java:119)
	at jadx.core.dex.visitors.ssa.EliminatePhiNodes.replaceMergeInstructions(EliminatePhiNodes.java:68)
	at jadx.core.dex.visitors.ssa.EliminatePhiNodes.visit(EliminatePhiNodes.java:31)
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
            r26 = this;
            r1 = r26;
            r2 = r1.sync;
            monitor-enter(r2);
            r3 = java.lang.Thread.currentThread();	 Catch:{ all -> 0x0636 }
            r1.runningThread = r3;	 Catch:{ all -> 0x0636 }
            java.lang.Thread.interrupted();	 Catch:{ all -> 0x0636 }
            r3 = r1.isCancelled;	 Catch:{ all -> 0x0636 }
            if (r3 == 0) goto L_0x0014;	 Catch:{ all -> 0x0636 }
        L_0x0012:
            monitor-exit(r2);	 Catch:{ all -> 0x0636 }
            return;	 Catch:{ all -> 0x0636 }
        L_0x0014:
            monitor-exit(r2);	 Catch:{ all -> 0x0636 }
            r2 = r1.cacheImage;
            r2 = r2.animatedFile;
            r3 = 0;
            r4 = 1;
            if (r2 == 0) goto L_0x004f;
        L_0x001d:
            r2 = r1.sync;
            monitor-enter(r2);
            r5 = r1.isCancelled;	 Catch:{ all -> 0x004b }
            if (r5 == 0) goto L_0x0026;	 Catch:{ all -> 0x004b }
        L_0x0024:
            monitor-exit(r2);	 Catch:{ all -> 0x004b }
            return;	 Catch:{ all -> 0x004b }
        L_0x0026:
            monitor-exit(r2);	 Catch:{ all -> 0x004b }
            r2 = new org.telegram.ui.Components.AnimatedFileDrawable;
            r5 = r1.cacheImage;
            r5 = r5.finalFilePath;
            r6 = r1.cacheImage;
            r6 = r6.filter;
            if (r6 == 0) goto L_0x0040;
        L_0x0033:
            r6 = r1.cacheImage;
            r6 = r6.filter;
            r7 = "d";
            r6 = r6.equals(r7);
            if (r6 == 0) goto L_0x0040;
        L_0x003f:
            r3 = r4;
        L_0x0040:
            r2.<init>(r5, r3);
            java.lang.Thread.interrupted();
            r1.onPostExecute(r2);
            goto L_0x0635;
        L_0x004b:
            r0 = move-exception;
            r3 = r0;
            monitor-exit(r2);	 Catch:{ all -> 0x004b }
            throw r3;
        L_0x004f:
            r2 = r1.cacheImage;
            r2 = r2.finalFilePath;
            r5 = r1.cacheImage;
            r5 = r5.encryptionKeyPath;
            if (r5 == 0) goto L_0x0069;
        L_0x0059:
            if (r2 == 0) goto L_0x0069;
        L_0x005b:
            r5 = r2.getAbsolutePath();
            r6 = ".enc";
            r5 = r5.endsWith(r6);
            if (r5 == 0) goto L_0x0069;
        L_0x0067:
            r5 = r4;
            goto L_0x006a;
        L_0x0069:
            r5 = r3;
        L_0x006a:
            r6 = android.os.Build.VERSION.SDK_INT;
            r7 = 19;
            r8 = 0;
            if (r6 >= r7) goto L_0x00e9;
        L_0x0071:
            r6 = new java.io.RandomAccessFile;	 Catch:{ Exception -> 0x00c8, all -> 0x00c4 }
            r7 = "r";	 Catch:{ Exception -> 0x00c8, all -> 0x00c4 }
            r6.<init>(r2, r7);	 Catch:{ Exception -> 0x00c8, all -> 0x00c4 }
            r7 = r1.cacheImage;	 Catch:{ Exception -> 0x00bf, all -> 0x00bc }
            r7 = r7.selfThumb;	 Catch:{ Exception -> 0x00bf, all -> 0x00bc }
            if (r7 == 0) goto L_0x0083;	 Catch:{ Exception -> 0x00bf, all -> 0x00bc }
        L_0x007e:
            r7 = org.telegram.messenger.ImageLoader.headerThumb;	 Catch:{ Exception -> 0x00bf, all -> 0x00bc }
            goto L_0x0087;	 Catch:{ Exception -> 0x00bf, all -> 0x00bc }
        L_0x0083:
            r7 = org.telegram.messenger.ImageLoader.header;	 Catch:{ Exception -> 0x00bf, all -> 0x00bc }
        L_0x0087:
            r9 = r7.length;	 Catch:{ Exception -> 0x00bf, all -> 0x00bc }
            r6.readFully(r7, r3, r9);	 Catch:{ Exception -> 0x00bf, all -> 0x00bc }
            r9 = new java.lang.String;	 Catch:{ Exception -> 0x00bf, all -> 0x00bc }
            r9.<init>(r7);	 Catch:{ Exception -> 0x00bf, all -> 0x00bc }
            r7 = r9.toLowerCase();	 Catch:{ Exception -> 0x00bf, all -> 0x00bc }
            r7 = r7.toLowerCase();	 Catch:{ Exception -> 0x00bf, all -> 0x00bc }
            r9 = "riff";	 Catch:{ Exception -> 0x00bf, all -> 0x00bc }
            r9 = r7.startsWith(r9);	 Catch:{ Exception -> 0x00bf, all -> 0x00bc }
            if (r9 == 0) goto L_0x00aa;	 Catch:{ Exception -> 0x00bf, all -> 0x00bc }
        L_0x00a0:
            r9 = "webp";	 Catch:{ Exception -> 0x00bf, all -> 0x00bc }
            r7 = r7.endsWith(r9);	 Catch:{ Exception -> 0x00bf, all -> 0x00bc }
            if (r7 == 0) goto L_0x00aa;
        L_0x00a8:
            r7 = r4;
            goto L_0x00ab;
        L_0x00aa:
            r7 = r3;
        L_0x00ab:
            r6.close();	 Catch:{ Exception -> 0x00b9, all -> 0x00bc }
            if (r6 == 0) goto L_0x00ea;
        L_0x00b0:
            r6.close();	 Catch:{ Exception -> 0x00b4 }
            goto L_0x00ea;
        L_0x00b4:
            r0 = move-exception;
            org.telegram.messenger.FileLog.m3e(r0);
            goto L_0x00ea;
        L_0x00b9:
            r0 = move-exception;
            r9 = r7;
            goto L_0x00c1;
        L_0x00bc:
            r0 = move-exception;
            r2 = r0;
            goto L_0x00de;
        L_0x00bf:
            r0 = move-exception;
            r9 = r3;
        L_0x00c1:
            r7 = r6;
            r6 = r0;
            goto L_0x00cc;
        L_0x00c4:
            r0 = move-exception;
            r2 = r0;
            r6 = r8;
            goto L_0x00de;
        L_0x00c8:
            r0 = move-exception;
            r6 = r0;
            r9 = r3;
            r7 = r8;
        L_0x00cc:
            org.telegram.messenger.FileLog.m3e(r6);	 Catch:{ all -> 0x00db }
            if (r7 == 0) goto L_0x00d9;
        L_0x00d1:
            r7.close();	 Catch:{ Exception -> 0x00d5 }
            goto L_0x00d9;
        L_0x00d5:
            r0 = move-exception;
            org.telegram.messenger.FileLog.m3e(r0);
        L_0x00d9:
            r7 = r9;
            goto L_0x00ea;
        L_0x00db:
            r0 = move-exception;
            r2 = r0;
            r6 = r7;
        L_0x00de:
            if (r6 == 0) goto L_0x00e8;
        L_0x00e0:
            r6.close();	 Catch:{ Exception -> 0x00e4 }
            goto L_0x00e8;
        L_0x00e4:
            r0 = move-exception;
            org.telegram.messenger.FileLog.m3e(r0);
        L_0x00e8:
            throw r2;
        L_0x00e9:
            r7 = r3;
        L_0x00ea:
            r6 = r1.cacheImage;
            r6 = r6.selfThumb;
            r9 = 0;
            r11 = 21;
            if (r6 == 0) goto L_0x02ad;
        L_0x00f4:
            r6 = r1.cacheImage;
            r6 = r6.filter;
            if (r6 == 0) goto L_0x0124;
        L_0x00fa:
            r6 = r1.cacheImage;
            r6 = r6.filter;
            r14 = "b2";
            r6 = r6.contains(r14);
            if (r6 == 0) goto L_0x0108;
        L_0x0106:
            r6 = 3;
            goto L_0x0125;
        L_0x0108:
            r6 = r1.cacheImage;
            r6 = r6.filter;
            r14 = "b1";
            r6 = r6.contains(r14);
            if (r6 == 0) goto L_0x0116;
        L_0x0114:
            r6 = 2;
            goto L_0x0125;
        L_0x0116:
            r6 = r1.cacheImage;
            r6 = r6.filter;
            r14 = "b";
            r6 = r6.contains(r14);
            if (r6 == 0) goto L_0x0124;
        L_0x0122:
            r6 = r4;
            goto L_0x0125;
        L_0x0124:
            r6 = r3;
        L_0x0125:
            r14 = org.telegram.messenger.ImageLoader.this;	 Catch:{ Throwable -> 0x02a4 }
            r12 = java.lang.System.currentTimeMillis();	 Catch:{ Throwable -> 0x02a4 }
            r14.lastCacheOutTime = r12;	 Catch:{ Throwable -> 0x02a4 }
            r12 = r1.sync;	 Catch:{ Throwable -> 0x02a4 }
            monitor-enter(r12);	 Catch:{ Throwable -> 0x02a4 }
            r13 = r1.isCancelled;	 Catch:{ all -> 0x02a0 }
            if (r13 == 0) goto L_0x0137;	 Catch:{ all -> 0x02a0 }
        L_0x0135:
            monitor-exit(r12);	 Catch:{ all -> 0x02a0 }
            return;	 Catch:{ all -> 0x02a0 }
        L_0x0137:
            monitor-exit(r12);	 Catch:{ all -> 0x02a0 }
            r12 = new android.graphics.BitmapFactory$Options;	 Catch:{ Throwable -> 0x02a4 }
            r12.<init>();	 Catch:{ Throwable -> 0x02a4 }
            r12.inSampleSize = r4;	 Catch:{ Throwable -> 0x02a4 }
            r13 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Throwable -> 0x02a4 }
            if (r13 >= r11) goto L_0x0145;	 Catch:{ Throwable -> 0x02a4 }
        L_0x0143:
            r12.inPurgeable = r4;	 Catch:{ Throwable -> 0x02a4 }
        L_0x0145:
            if (r7 == 0) goto L_0x018a;	 Catch:{ Throwable -> 0x02a4 }
        L_0x0147:
            r3 = new java.io.RandomAccessFile;	 Catch:{ Throwable -> 0x02a4 }
            r5 = "r";	 Catch:{ Throwable -> 0x02a4 }
            r3.<init>(r2, r5);	 Catch:{ Throwable -> 0x02a4 }
            r17 = r3.getChannel();	 Catch:{ Throwable -> 0x02a4 }
            r18 = java.nio.channels.FileChannel.MapMode.READ_ONLY;	 Catch:{ Throwable -> 0x02a4 }
            r19 = 0;	 Catch:{ Throwable -> 0x02a4 }
            r21 = r2.length();	 Catch:{ Throwable -> 0x02a4 }
            r5 = r17.map(r18, r19, r21);	 Catch:{ Throwable -> 0x02a4 }
            r7 = new android.graphics.BitmapFactory$Options;	 Catch:{ Throwable -> 0x02a4 }
            r7.<init>();	 Catch:{ Throwable -> 0x02a4 }
            r7.inJustDecodeBounds = r4;	 Catch:{ Throwable -> 0x02a4 }
            r11 = r5.limit();	 Catch:{ Throwable -> 0x02a4 }
            org.telegram.messenger.Utilities.loadWebpImage(r8, r5, r11, r7, r4);	 Catch:{ Throwable -> 0x02a4 }
            r11 = r7.outWidth;	 Catch:{ Throwable -> 0x02a4 }
            r7 = r7.outHeight;	 Catch:{ Throwable -> 0x02a4 }
            r13 = android.graphics.Bitmap.Config.ARGB_8888;	 Catch:{ Throwable -> 0x02a4 }
            r7 = org.telegram.messenger.Bitmaps.createBitmap(r11, r7, r13);	 Catch:{ Throwable -> 0x02a4 }
            r11 = r5.limit();	 Catch:{ Throwable -> 0x0185 }
            r13 = r12.inPurgeable;	 Catch:{ Throwable -> 0x0185 }
            r13 = r13 ^ r4;	 Catch:{ Throwable -> 0x0185 }
            org.telegram.messenger.Utilities.loadWebpImage(r7, r5, r11, r8, r13);	 Catch:{ Throwable -> 0x0185 }
            r3.close();	 Catch:{ Throwable -> 0x0185 }
            r3 = r7;
            goto L_0x01e1;
        L_0x0185:
            r0 = move-exception;
            r2 = r0;
            r3 = r7;
            goto L_0x02a7;
        L_0x018a:
            r7 = r12.inPurgeable;	 Catch:{ Throwable -> 0x02a4 }
            if (r7 == 0) goto L_0x01c8;	 Catch:{ Throwable -> 0x02a4 }
        L_0x018e:
            r7 = new java.io.RandomAccessFile;	 Catch:{ Throwable -> 0x02a4 }
            r11 = "r";	 Catch:{ Throwable -> 0x02a4 }
            r7.<init>(r2, r11);	 Catch:{ Throwable -> 0x02a4 }
            r13 = r7.length();	 Catch:{ Throwable -> 0x02a4 }
            r11 = (int) r13;	 Catch:{ Throwable -> 0x02a4 }
            r13 = org.telegram.messenger.ImageLoader.bytesThumb;	 Catch:{ Throwable -> 0x02a4 }
            if (r13 == 0) goto L_0x01ac;	 Catch:{ Throwable -> 0x02a4 }
        L_0x01a0:
            r13 = org.telegram.messenger.ImageLoader.bytesThumb;	 Catch:{ Throwable -> 0x02a4 }
            r13 = r13.length;	 Catch:{ Throwable -> 0x02a4 }
            if (r13 < r11) goto L_0x01ac;	 Catch:{ Throwable -> 0x02a4 }
        L_0x01a7:
            r13 = org.telegram.messenger.ImageLoader.bytesThumb;	 Catch:{ Throwable -> 0x02a4 }
            goto L_0x01ad;	 Catch:{ Throwable -> 0x02a4 }
        L_0x01ac:
            r13 = r8;	 Catch:{ Throwable -> 0x02a4 }
        L_0x01ad:
            if (r13 != 0) goto L_0x01b4;	 Catch:{ Throwable -> 0x02a4 }
        L_0x01af:
            r13 = new byte[r11];	 Catch:{ Throwable -> 0x02a4 }
            org.telegram.messenger.ImageLoader.bytesThumb = r13;	 Catch:{ Throwable -> 0x02a4 }
        L_0x01b4:
            r7.readFully(r13, r3, r11);	 Catch:{ Throwable -> 0x02a4 }
            r7.close();	 Catch:{ Throwable -> 0x02a4 }
            if (r5 == 0) goto L_0x01c3;	 Catch:{ Throwable -> 0x02a4 }
        L_0x01bc:
            r5 = r1.cacheImage;	 Catch:{ Throwable -> 0x02a4 }
            r5 = r5.encryptionKeyPath;	 Catch:{ Throwable -> 0x02a4 }
            org.telegram.messenger.secretmedia.EncryptedFileInputStream.decryptBytesWithKeyFile(r13, r3, r11, r5);	 Catch:{ Throwable -> 0x02a4 }
        L_0x01c3:
            r3 = android.graphics.BitmapFactory.decodeByteArray(r13, r3, r11, r12);	 Catch:{ Throwable -> 0x02a4 }
            goto L_0x01e1;	 Catch:{ Throwable -> 0x02a4 }
        L_0x01c8:
            if (r5 == 0) goto L_0x01d4;	 Catch:{ Throwable -> 0x02a4 }
        L_0x01ca:
            r3 = new org.telegram.messenger.secretmedia.EncryptedFileInputStream;	 Catch:{ Throwable -> 0x02a4 }
            r5 = r1.cacheImage;	 Catch:{ Throwable -> 0x02a4 }
            r5 = r5.encryptionKeyPath;	 Catch:{ Throwable -> 0x02a4 }
            r3.<init>(r2, r5);	 Catch:{ Throwable -> 0x02a4 }
            goto L_0x01d9;	 Catch:{ Throwable -> 0x02a4 }
        L_0x01d4:
            r3 = new java.io.FileInputStream;	 Catch:{ Throwable -> 0x02a4 }
            r3.<init>(r2);	 Catch:{ Throwable -> 0x02a4 }
        L_0x01d9:
            r5 = android.graphics.BitmapFactory.decodeStream(r3, r8, r12);	 Catch:{ Throwable -> 0x02a4 }
            r3.close();	 Catch:{ Throwable -> 0x029c }
            r3 = r5;
        L_0x01e1:
            if (r3 != 0) goto L_0x01fa;
        L_0x01e3:
            r4 = r2.length();	 Catch:{ Throwable -> 0x01f6 }
            r6 = (r4 > r9 ? 1 : (r4 == r9 ? 0 : -1));	 Catch:{ Throwable -> 0x01f6 }
            if (r6 == 0) goto L_0x01f1;	 Catch:{ Throwable -> 0x01f6 }
        L_0x01eb:
            r4 = r1.cacheImage;	 Catch:{ Throwable -> 0x01f6 }
            r4 = r4.filter;	 Catch:{ Throwable -> 0x01f6 }
            if (r4 != 0) goto L_0x02aa;	 Catch:{ Throwable -> 0x01f6 }
        L_0x01f1:
            r2.delete();	 Catch:{ Throwable -> 0x01f6 }
            goto L_0x02aa;	 Catch:{ Throwable -> 0x01f6 }
        L_0x01f6:
            r0 = move-exception;	 Catch:{ Throwable -> 0x01f6 }
            r2 = r0;	 Catch:{ Throwable -> 0x01f6 }
            goto L_0x02a7;	 Catch:{ Throwable -> 0x01f6 }
        L_0x01fa:
            if (r6 != r4) goto L_0x021d;	 Catch:{ Throwable -> 0x01f6 }
        L_0x01fc:
            r2 = r3.getConfig();	 Catch:{ Throwable -> 0x01f6 }
            r5 = android.graphics.Bitmap.Config.ARGB_8888;	 Catch:{ Throwable -> 0x01f6 }
            if (r2 != r5) goto L_0x02aa;	 Catch:{ Throwable -> 0x01f6 }
        L_0x0204:
            r18 = 3;	 Catch:{ Throwable -> 0x01f6 }
            r2 = r12.inPurgeable;	 Catch:{ Throwable -> 0x01f6 }
            r19 = r2 ^ 1;	 Catch:{ Throwable -> 0x01f6 }
            r20 = r3.getWidth();	 Catch:{ Throwable -> 0x01f6 }
            r21 = r3.getHeight();	 Catch:{ Throwable -> 0x01f6 }
            r22 = r3.getRowBytes();	 Catch:{ Throwable -> 0x01f6 }
            r17 = r3;	 Catch:{ Throwable -> 0x01f6 }
            org.telegram.messenger.Utilities.blurBitmap(r17, r18, r19, r20, r21, r22);	 Catch:{ Throwable -> 0x01f6 }
            goto L_0x02aa;	 Catch:{ Throwable -> 0x01f6 }
        L_0x021d:
            r2 = 2;	 Catch:{ Throwable -> 0x01f6 }
            if (r6 != r2) goto L_0x0241;	 Catch:{ Throwable -> 0x01f6 }
        L_0x0220:
            r2 = r3.getConfig();	 Catch:{ Throwable -> 0x01f6 }
            r5 = android.graphics.Bitmap.Config.ARGB_8888;	 Catch:{ Throwable -> 0x01f6 }
            if (r2 != r5) goto L_0x02aa;	 Catch:{ Throwable -> 0x01f6 }
        L_0x0228:
            r18 = 1;	 Catch:{ Throwable -> 0x01f6 }
            r2 = r12.inPurgeable;	 Catch:{ Throwable -> 0x01f6 }
            r19 = r2 ^ 1;	 Catch:{ Throwable -> 0x01f6 }
            r20 = r3.getWidth();	 Catch:{ Throwable -> 0x01f6 }
            r21 = r3.getHeight();	 Catch:{ Throwable -> 0x01f6 }
            r22 = r3.getRowBytes();	 Catch:{ Throwable -> 0x01f6 }
            r17 = r3;	 Catch:{ Throwable -> 0x01f6 }
            org.telegram.messenger.Utilities.blurBitmap(r17, r18, r19, r20, r21, r22);	 Catch:{ Throwable -> 0x01f6 }
            goto L_0x02aa;	 Catch:{ Throwable -> 0x01f6 }
        L_0x0241:
            r2 = 3;	 Catch:{ Throwable -> 0x01f6 }
            if (r6 != r2) goto L_0x0292;	 Catch:{ Throwable -> 0x01f6 }
        L_0x0244:
            r2 = r3.getConfig();	 Catch:{ Throwable -> 0x01f6 }
            r5 = android.graphics.Bitmap.Config.ARGB_8888;	 Catch:{ Throwable -> 0x01f6 }
            if (r2 != r5) goto L_0x02aa;	 Catch:{ Throwable -> 0x01f6 }
        L_0x024c:
            r18 = 7;	 Catch:{ Throwable -> 0x01f6 }
            r2 = r12.inPurgeable;	 Catch:{ Throwable -> 0x01f6 }
            r19 = r2 ^ 1;	 Catch:{ Throwable -> 0x01f6 }
            r20 = r3.getWidth();	 Catch:{ Throwable -> 0x01f6 }
            r21 = r3.getHeight();	 Catch:{ Throwable -> 0x01f6 }
            r22 = r3.getRowBytes();	 Catch:{ Throwable -> 0x01f6 }
            r17 = r3;	 Catch:{ Throwable -> 0x01f6 }
            org.telegram.messenger.Utilities.blurBitmap(r17, r18, r19, r20, r21, r22);	 Catch:{ Throwable -> 0x01f6 }
            r18 = 7;	 Catch:{ Throwable -> 0x01f6 }
            r2 = r12.inPurgeable;	 Catch:{ Throwable -> 0x01f6 }
            r19 = r2 ^ 1;	 Catch:{ Throwable -> 0x01f6 }
            r20 = r3.getWidth();	 Catch:{ Throwable -> 0x01f6 }
            r21 = r3.getHeight();	 Catch:{ Throwable -> 0x01f6 }
            r22 = r3.getRowBytes();	 Catch:{ Throwable -> 0x01f6 }
            r17 = r3;	 Catch:{ Throwable -> 0x01f6 }
            org.telegram.messenger.Utilities.blurBitmap(r17, r18, r19, r20, r21, r22);	 Catch:{ Throwable -> 0x01f6 }
            r18 = 7;	 Catch:{ Throwable -> 0x01f6 }
            r2 = r12.inPurgeable;	 Catch:{ Throwable -> 0x01f6 }
            r19 = r2 ^ 1;	 Catch:{ Throwable -> 0x01f6 }
            r20 = r3.getWidth();	 Catch:{ Throwable -> 0x01f6 }
            r21 = r3.getHeight();	 Catch:{ Throwable -> 0x01f6 }
            r22 = r3.getRowBytes();	 Catch:{ Throwable -> 0x01f6 }
            r17 = r3;	 Catch:{ Throwable -> 0x01f6 }
            org.telegram.messenger.Utilities.blurBitmap(r17, r18, r19, r20, r21, r22);	 Catch:{ Throwable -> 0x01f6 }
            goto L_0x02aa;	 Catch:{ Throwable -> 0x01f6 }
        L_0x0292:
            if (r6 != 0) goto L_0x02aa;	 Catch:{ Throwable -> 0x01f6 }
        L_0x0294:
            r2 = r12.inPurgeable;	 Catch:{ Throwable -> 0x01f6 }
            if (r2 == 0) goto L_0x02aa;	 Catch:{ Throwable -> 0x01f6 }
        L_0x0298:
            org.telegram.messenger.Utilities.pinBitmap(r3);	 Catch:{ Throwable -> 0x01f6 }
            goto L_0x02aa;
        L_0x029c:
            r0 = move-exception;
            r2 = r0;
            r3 = r5;
            goto L_0x02a7;
        L_0x02a0:
            r0 = move-exception;
            r2 = r0;
            monitor-exit(r12);	 Catch:{ all -> 0x02a0 }
            throw r2;	 Catch:{ Throwable -> 0x02a4 }
        L_0x02a4:
            r0 = move-exception;
            r2 = r0;
            r3 = r8;
        L_0x02a7:
            org.telegram.messenger.FileLog.m3e(r2);
        L_0x02aa:
            r11 = r8;
            goto L_0x0626;
        L_0x02ad:
            r6 = r1.cacheImage;	 Catch:{ Throwable -> 0x0624 }
            r6 = r6.httpUrl;	 Catch:{ Throwable -> 0x0624 }
            if (r6 == 0) goto L_0x0336;
        L_0x02b3:
            r6 = r1.cacheImage;	 Catch:{ Throwable -> 0x0332 }
            r6 = r6.httpUrl;	 Catch:{ Throwable -> 0x0332 }
            r12 = "thumb://";	 Catch:{ Throwable -> 0x0332 }
            r6 = r6.startsWith(r12);	 Catch:{ Throwable -> 0x0332 }
            if (r6 == 0) goto L_0x02f1;	 Catch:{ Throwable -> 0x0332 }
        L_0x02bf:
            r6 = r1.cacheImage;	 Catch:{ Throwable -> 0x0332 }
            r6 = r6.httpUrl;	 Catch:{ Throwable -> 0x0332 }
            r12 = ":";	 Catch:{ Throwable -> 0x0332 }
            r13 = 8;	 Catch:{ Throwable -> 0x0332 }
            r6 = r6.indexOf(r12, r13);	 Catch:{ Throwable -> 0x0332 }
            if (r6 < 0) goto L_0x02e7;	 Catch:{ Throwable -> 0x0332 }
        L_0x02cd:
            r12 = r1.cacheImage;	 Catch:{ Throwable -> 0x0332 }
            r12 = r12.httpUrl;	 Catch:{ Throwable -> 0x0332 }
            r12 = r12.substring(r13, r6);	 Catch:{ Throwable -> 0x0332 }
            r12 = java.lang.Long.parseLong(r12);	 Catch:{ Throwable -> 0x0332 }
            r12 = java.lang.Long.valueOf(r12);	 Catch:{ Throwable -> 0x0332 }
            r13 = r1.cacheImage;	 Catch:{ Throwable -> 0x0332 }
            r13 = r13.httpUrl;	 Catch:{ Throwable -> 0x0332 }
            r6 = r6 + r4;	 Catch:{ Throwable -> 0x0332 }
            r6 = r13.substring(r6);	 Catch:{ Throwable -> 0x0332 }
            goto L_0x02e9;	 Catch:{ Throwable -> 0x0332 }
        L_0x02e7:
            r6 = r8;	 Catch:{ Throwable -> 0x0332 }
            r12 = r6;	 Catch:{ Throwable -> 0x0332 }
        L_0x02e9:
            r13 = r3;	 Catch:{ Throwable -> 0x0332 }
            r14 = r13;	 Catch:{ Throwable -> 0x0332 }
            r25 = r12;	 Catch:{ Throwable -> 0x0332 }
            r12 = r6;	 Catch:{ Throwable -> 0x0332 }
            r6 = r25;	 Catch:{ Throwable -> 0x0332 }
            goto L_0x033a;	 Catch:{ Throwable -> 0x0332 }
        L_0x02f1:
            r6 = r1.cacheImage;	 Catch:{ Throwable -> 0x0332 }
            r6 = r6.httpUrl;	 Catch:{ Throwable -> 0x0332 }
            r12 = "vthumb://";	 Catch:{ Throwable -> 0x0332 }
            r6 = r6.startsWith(r12);	 Catch:{ Throwable -> 0x0332 }
            if (r6 == 0) goto L_0x0323;	 Catch:{ Throwable -> 0x0332 }
        L_0x02fd:
            r6 = r1.cacheImage;	 Catch:{ Throwable -> 0x0332 }
            r6 = r6.httpUrl;	 Catch:{ Throwable -> 0x0332 }
            r12 = ":";	 Catch:{ Throwable -> 0x0332 }
            r13 = 9;	 Catch:{ Throwable -> 0x0332 }
            r6 = r6.indexOf(r12, r13);	 Catch:{ Throwable -> 0x0332 }
            if (r6 < 0) goto L_0x031d;	 Catch:{ Throwable -> 0x0332 }
        L_0x030b:
            r12 = r1.cacheImage;	 Catch:{ Throwable -> 0x0332 }
            r12 = r12.httpUrl;	 Catch:{ Throwable -> 0x0332 }
            r6 = r12.substring(r13, r6);	 Catch:{ Throwable -> 0x0332 }
            r12 = java.lang.Long.parseLong(r6);	 Catch:{ Throwable -> 0x0332 }
            r6 = java.lang.Long.valueOf(r12);	 Catch:{ Throwable -> 0x0332 }
            r12 = r4;	 Catch:{ Throwable -> 0x0332 }
            goto L_0x031f;	 Catch:{ Throwable -> 0x0332 }
        L_0x031d:
            r12 = r3;	 Catch:{ Throwable -> 0x0332 }
            r6 = r8;	 Catch:{ Throwable -> 0x0332 }
        L_0x031f:
            r14 = r3;	 Catch:{ Throwable -> 0x0332 }
            r13 = r12;	 Catch:{ Throwable -> 0x0332 }
            r12 = r8;	 Catch:{ Throwable -> 0x0332 }
            goto L_0x033a;	 Catch:{ Throwable -> 0x0332 }
        L_0x0323:
            r6 = r1.cacheImage;	 Catch:{ Throwable -> 0x0332 }
            r6 = r6.httpUrl;	 Catch:{ Throwable -> 0x0332 }
            r12 = "http";	 Catch:{ Throwable -> 0x0332 }
            r6 = r6.startsWith(r12);	 Catch:{ Throwable -> 0x0332 }
            if (r6 != 0) goto L_0x0336;
        L_0x032f:
            r13 = r3;
            r14 = r13;
            goto L_0x0338;
        L_0x0332:
            r3 = r8;
            r11 = r3;
            goto L_0x0626;
        L_0x0336:
            r13 = r3;
            r14 = r4;
        L_0x0338:
            r6 = r8;
            r12 = r6;
        L_0x033a:
            r16 = 20;
            if (r6 == 0) goto L_0x0340;
        L_0x033e:
            r15 = r3;
            goto L_0x0342;
        L_0x0340:
            r15 = r16;
        L_0x0342:
            if (r15 == 0) goto L_0x036b;
        L_0x0344:
            r8 = org.telegram.messenger.ImageLoader.this;	 Catch:{ Throwable -> 0x0367 }
            r16 = r8.lastCacheOutTime;	 Catch:{ Throwable -> 0x0367 }
            r8 = (r16 > r9 ? 1 : (r16 == r9 ? 0 : -1));	 Catch:{ Throwable -> 0x0367 }
            if (r8 == 0) goto L_0x036b;	 Catch:{ Throwable -> 0x0367 }
        L_0x034e:
            r8 = org.telegram.messenger.ImageLoader.this;	 Catch:{ Throwable -> 0x0367 }
            r16 = r8.lastCacheOutTime;	 Catch:{ Throwable -> 0x0367 }
            r18 = java.lang.System.currentTimeMillis();	 Catch:{ Throwable -> 0x0367 }
            r9 = (long) r15;	 Catch:{ Throwable -> 0x0367 }
            r20 = r18 - r9;	 Catch:{ Throwable -> 0x0367 }
            r8 = (r16 > r20 ? 1 : (r16 == r20 ? 0 : -1));	 Catch:{ Throwable -> 0x0367 }
            if (r8 <= 0) goto L_0x036b;	 Catch:{ Throwable -> 0x0367 }
        L_0x035f:
            r8 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Throwable -> 0x0367 }
            if (r8 >= r11) goto L_0x036b;	 Catch:{ Throwable -> 0x0367 }
        L_0x0363:
            java.lang.Thread.sleep(r9);	 Catch:{ Throwable -> 0x0367 }
            goto L_0x036b;
        L_0x0367:
            r3 = 0;
        L_0x0368:
            r11 = 0;
            goto L_0x0626;
        L_0x036b:
            r8 = org.telegram.messenger.ImageLoader.this;	 Catch:{ Throwable -> 0x0622 }
            r9 = java.lang.System.currentTimeMillis();	 Catch:{ Throwable -> 0x0622 }
            r8.lastCacheOutTime = r9;	 Catch:{ Throwable -> 0x0622 }
            r8 = r1.sync;	 Catch:{ Throwable -> 0x0622 }
            monitor-enter(r8);	 Catch:{ Throwable -> 0x0622 }
            r9 = r1.isCancelled;	 Catch:{ all -> 0x061b }
            if (r9 == 0) goto L_0x0382;
        L_0x037b:
            monitor-exit(r8);	 Catch:{ all -> 0x037d }
            return;
        L_0x037d:
            r0 = move-exception;
            r2 = r0;
            r11 = 0;
            goto L_0x061e;
        L_0x0382:
            monitor-exit(r8);	 Catch:{ all -> 0x061b }
            r8 = new android.graphics.BitmapFactory$Options;	 Catch:{ Throwable -> 0x0622 }
            r8.<init>();	 Catch:{ Throwable -> 0x0622 }
            r8.inSampleSize = r4;	 Catch:{ Throwable -> 0x0622 }
            r9 = r1.cacheImage;	 Catch:{ Throwable -> 0x0622 }
            r9 = r9.filter;	 Catch:{ Throwable -> 0x0622 }
            r16 = 0;
            if (r9 == 0) goto L_0x0438;
        L_0x0392:
            r9 = r1.cacheImage;	 Catch:{ Throwable -> 0x0367 }
            r9 = r9.filter;	 Catch:{ Throwable -> 0x0367 }
            r15 = "_";	 Catch:{ Throwable -> 0x0367 }
            r9 = r9.split(r15);	 Catch:{ Throwable -> 0x0367 }
            r15 = r9.length;	 Catch:{ Throwable -> 0x0367 }
            r11 = 2;	 Catch:{ Throwable -> 0x0367 }
            if (r15 < r11) goto L_0x03b3;	 Catch:{ Throwable -> 0x0367 }
        L_0x03a0:
            r11 = r9[r3];	 Catch:{ Throwable -> 0x0367 }
            r11 = java.lang.Float.parseFloat(r11);	 Catch:{ Throwable -> 0x0367 }
            r15 = org.telegram.messenger.AndroidUtilities.density;	 Catch:{ Throwable -> 0x0367 }
            r11 = r11 * r15;	 Catch:{ Throwable -> 0x0367 }
            r9 = r9[r4];	 Catch:{ Throwable -> 0x0367 }
            r9 = java.lang.Float.parseFloat(r9);	 Catch:{ Throwable -> 0x0367 }
            r15 = org.telegram.messenger.AndroidUtilities.density;	 Catch:{ Throwable -> 0x0367 }
            r9 = r9 * r15;	 Catch:{ Throwable -> 0x0367 }
            goto L_0x03b6;	 Catch:{ Throwable -> 0x0367 }
        L_0x03b3:
            r9 = r16;	 Catch:{ Throwable -> 0x0367 }
            r11 = r9;	 Catch:{ Throwable -> 0x0367 }
        L_0x03b6:
            r15 = r1.cacheImage;	 Catch:{ Throwable -> 0x0367 }
            r15 = r15.filter;	 Catch:{ Throwable -> 0x0367 }
            r3 = "b";	 Catch:{ Throwable -> 0x0367 }
            r3 = r15.contains(r3);	 Catch:{ Throwable -> 0x0367 }
            r15 = (r11 > r16 ? 1 : (r11 == r16 ? 0 : -1));	 Catch:{ Throwable -> 0x0367 }
            if (r15 == 0) goto L_0x0430;	 Catch:{ Throwable -> 0x0367 }
        L_0x03c4:
            r15 = (r9 > r16 ? 1 : (r9 == r16 ? 0 : -1));	 Catch:{ Throwable -> 0x0367 }
            if (r15 == 0) goto L_0x0430;	 Catch:{ Throwable -> 0x0367 }
        L_0x03c8:
            r8.inJustDecodeBounds = r4;	 Catch:{ Throwable -> 0x0367 }
            if (r6 == 0) goto L_0x03f5;	 Catch:{ Throwable -> 0x0367 }
        L_0x03cc:
            if (r12 != 0) goto L_0x03f5;	 Catch:{ Throwable -> 0x0367 }
        L_0x03ce:
            if (r13 == 0) goto L_0x03e2;	 Catch:{ Throwable -> 0x0367 }
        L_0x03d0:
            r15 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Throwable -> 0x0367 }
            r15 = r15.getContentResolver();	 Catch:{ Throwable -> 0x0367 }
            r23 = r11;	 Catch:{ Throwable -> 0x0367 }
            r10 = r6.longValue();	 Catch:{ Throwable -> 0x0367 }
            android.provider.MediaStore.Video.Thumbnails.getThumbnail(r15, r10, r4, r8);	 Catch:{ Throwable -> 0x0367 }
            r24 = r14;	 Catch:{ Throwable -> 0x0367 }
            goto L_0x03f3;	 Catch:{ Throwable -> 0x0367 }
        L_0x03e2:
            r23 = r11;	 Catch:{ Throwable -> 0x0367 }
            r10 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Throwable -> 0x0367 }
            r10 = r10.getContentResolver();	 Catch:{ Throwable -> 0x0367 }
            r24 = r14;	 Catch:{ Throwable -> 0x0367 }
            r14 = r6.longValue();	 Catch:{ Throwable -> 0x0367 }
            android.provider.MediaStore.Images.Thumbnails.getThumbnail(r10, r14, r4, r8);	 Catch:{ Throwable -> 0x0367 }
        L_0x03f3:
            r14 = 0;	 Catch:{ Throwable -> 0x0367 }
            goto L_0x0412;	 Catch:{ Throwable -> 0x0367 }
        L_0x03f5:
            r23 = r11;	 Catch:{ Throwable -> 0x0367 }
            r24 = r14;	 Catch:{ Throwable -> 0x0367 }
            if (r5 == 0) goto L_0x0405;	 Catch:{ Throwable -> 0x0367 }
        L_0x03fb:
            r10 = new org.telegram.messenger.secretmedia.EncryptedFileInputStream;	 Catch:{ Throwable -> 0x0367 }
            r11 = r1.cacheImage;	 Catch:{ Throwable -> 0x0367 }
            r11 = r11.encryptionKeyPath;	 Catch:{ Throwable -> 0x0367 }
            r10.<init>(r2, r11);	 Catch:{ Throwable -> 0x0367 }
            goto L_0x040a;	 Catch:{ Throwable -> 0x0367 }
        L_0x0405:
            r10 = new java.io.FileInputStream;	 Catch:{ Throwable -> 0x0367 }
            r10.<init>(r2);	 Catch:{ Throwable -> 0x0367 }
        L_0x040a:
            r11 = 0;	 Catch:{ Throwable -> 0x0367 }
            r14 = android.graphics.BitmapFactory.decodeStream(r10, r11, r8);	 Catch:{ Throwable -> 0x0367 }
            r10.close();	 Catch:{ Throwable -> 0x04a1 }
        L_0x0412:
            r10 = r8.outWidth;	 Catch:{ Throwable -> 0x04a1 }
            r10 = (float) r10;	 Catch:{ Throwable -> 0x04a1 }
            r11 = r8.outHeight;	 Catch:{ Throwable -> 0x04a1 }
            r11 = (float) r11;	 Catch:{ Throwable -> 0x04a1 }
            r10 = r10 / r23;	 Catch:{ Throwable -> 0x04a1 }
            r11 = r11 / r9;	 Catch:{ Throwable -> 0x04a1 }
            r10 = java.lang.Math.max(r10, r11);	 Catch:{ Throwable -> 0x04a1 }
            r9 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;	 Catch:{ Throwable -> 0x04a1 }
            r11 = (r10 > r9 ? 1 : (r10 == r9 ? 0 : -1));	 Catch:{ Throwable -> 0x04a1 }
            if (r11 >= 0) goto L_0x0429;	 Catch:{ Throwable -> 0x04a1 }
        L_0x0425:
            r9 = 0;	 Catch:{ Throwable -> 0x04a1 }
            r10 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;	 Catch:{ Throwable -> 0x04a1 }
            goto L_0x042a;	 Catch:{ Throwable -> 0x04a1 }
        L_0x0429:
            r9 = 0;	 Catch:{ Throwable -> 0x04a1 }
        L_0x042a:
            r8.inJustDecodeBounds = r9;	 Catch:{ Throwable -> 0x04a1 }
            r9 = (int) r10;	 Catch:{ Throwable -> 0x04a1 }
            r8.inSampleSize = r9;	 Catch:{ Throwable -> 0x04a1 }
            goto L_0x0435;
        L_0x0430:
            r23 = r11;
            r24 = r14;
            r14 = 0;
        L_0x0435:
            r9 = r23;
            goto L_0x047e;
        L_0x0438:
            r24 = r14;
            if (r12 == 0) goto L_0x047a;
        L_0x043c:
            r8.inJustDecodeBounds = r4;	 Catch:{ Throwable -> 0x0367 }
            r3 = android.graphics.Bitmap.Config.RGB_565;	 Catch:{ Throwable -> 0x0367 }
            r8.inPreferredConfig = r3;	 Catch:{ Throwable -> 0x0367 }
            r3 = new java.io.FileInputStream;	 Catch:{ Throwable -> 0x0367 }
            r3.<init>(r2);	 Catch:{ Throwable -> 0x0367 }
            r9 = 0;	 Catch:{ Throwable -> 0x0367 }
            r10 = android.graphics.BitmapFactory.decodeStream(r3, r9, r8);	 Catch:{ Throwable -> 0x0367 }
            r3.close();	 Catch:{ Throwable -> 0x0477 }
            r3 = r8.outWidth;	 Catch:{ Throwable -> 0x0477 }
            r9 = r8.outHeight;	 Catch:{ Throwable -> 0x0477 }
            r11 = 0;	 Catch:{ Throwable -> 0x0477 }
            r8.inJustDecodeBounds = r11;	 Catch:{ Throwable -> 0x0477 }
            r3 = r3 / 200;	 Catch:{ Throwable -> 0x0477 }
            r9 = r9 / 200;	 Catch:{ Throwable -> 0x0477 }
            r3 = java.lang.Math.max(r3, r9);	 Catch:{ Throwable -> 0x0477 }
            r3 = (float) r3;	 Catch:{ Throwable -> 0x0477 }
            r9 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;	 Catch:{ Throwable -> 0x0477 }
            r11 = (r3 > r9 ? 1 : (r3 == r9 ? 0 : -1));	 Catch:{ Throwable -> 0x0477 }
            if (r11 >= 0) goto L_0x0466;	 Catch:{ Throwable -> 0x0477 }
        L_0x0465:
            r3 = r9;	 Catch:{ Throwable -> 0x0477 }
        L_0x0466:
            r9 = r4;	 Catch:{ Throwable -> 0x0477 }
            r11 = 2;	 Catch:{ Throwable -> 0x0477 }
        L_0x0468:
            r9 = r9 * r11;	 Catch:{ Throwable -> 0x0477 }
            r14 = r9 * 2;	 Catch:{ Throwable -> 0x0477 }
            r14 = (float) r14;	 Catch:{ Throwable -> 0x0477 }
            r14 = (r14 > r3 ? 1 : (r14 == r3 ? 0 : -1));	 Catch:{ Throwable -> 0x0477 }
            if (r14 < 0) goto L_0x0468;	 Catch:{ Throwable -> 0x0477 }
        L_0x0470:
            r8.inSampleSize = r9;	 Catch:{ Throwable -> 0x0477 }
            r14 = r10;
            r9 = r16;
            r3 = 0;
            goto L_0x047e;
        L_0x0477:
            r3 = r10;
            goto L_0x0368;
        L_0x047a:
            r9 = r16;
            r3 = 0;
            r14 = 0;
        L_0x047e:
            r10 = r1.sync;	 Catch:{ Throwable -> 0x0618 }
            monitor-enter(r10);	 Catch:{ Throwable -> 0x0618 }
            r11 = r1.isCancelled;	 Catch:{ all -> 0x0611 }
            if (r11 == 0) goto L_0x048c;
        L_0x0485:
            monitor-exit(r10);	 Catch:{ all -> 0x0487 }
            return;
        L_0x0487:
            r0 = move-exception;
            r2 = r0;
            r11 = 0;
            goto L_0x0614;
        L_0x048c:
            monitor-exit(r10);	 Catch:{ all -> 0x0611 }
            r10 = r1.cacheImage;	 Catch:{ Throwable -> 0x0618 }
            r10 = r10.filter;	 Catch:{ Throwable -> 0x0618 }
            if (r10 == 0) goto L_0x04a4;
        L_0x0493:
            if (r3 != 0) goto L_0x04a4;
        L_0x0495:
            r10 = r1.cacheImage;	 Catch:{ Throwable -> 0x04a1 }
            r10 = r10.httpUrl;	 Catch:{ Throwable -> 0x04a1 }
            if (r10 == 0) goto L_0x049c;	 Catch:{ Throwable -> 0x04a1 }
        L_0x049b:
            goto L_0x04a4;	 Catch:{ Throwable -> 0x04a1 }
        L_0x049c:
            r10 = android.graphics.Bitmap.Config.RGB_565;	 Catch:{ Throwable -> 0x04a1 }
            r8.inPreferredConfig = r10;	 Catch:{ Throwable -> 0x04a1 }
            goto L_0x04a8;
        L_0x04a1:
            r3 = r14;
            goto L_0x0368;
        L_0x04a4:
            r10 = android.graphics.Bitmap.Config.ARGB_8888;	 Catch:{ Throwable -> 0x0618 }
            r8.inPreferredConfig = r10;	 Catch:{ Throwable -> 0x0618 }
        L_0x04a8:
            r10 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Throwable -> 0x0618 }
            r11 = 21;
            if (r10 >= r11) goto L_0x04b0;
        L_0x04ae:
            r8.inPurgeable = r4;	 Catch:{ Throwable -> 0x04a1 }
        L_0x04b0:
            r10 = 0;
            r8.inDither = r10;	 Catch:{ Throwable -> 0x0618 }
            if (r6 == 0) goto L_0x04d7;
        L_0x04b5:
            if (r12 != 0) goto L_0x04d7;
        L_0x04b7:
            if (r13 == 0) goto L_0x04c8;
        L_0x04b9:
            r10 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Throwable -> 0x04a1 }
            r10 = r10.getContentResolver();	 Catch:{ Throwable -> 0x04a1 }
            r11 = r6.longValue();	 Catch:{ Throwable -> 0x04a1 }
            r6 = android.provider.MediaStore.Video.Thumbnails.getThumbnail(r10, r11, r4, r8);	 Catch:{ Throwable -> 0x04a1 }
            goto L_0x04d8;	 Catch:{ Throwable -> 0x04a1 }
        L_0x04c8:
            r10 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Throwable -> 0x04a1 }
            r10 = r10.getContentResolver();	 Catch:{ Throwable -> 0x04a1 }
            r11 = r6.longValue();	 Catch:{ Throwable -> 0x04a1 }
            r6 = android.provider.MediaStore.Images.Thumbnails.getThumbnail(r10, r11, r4, r8);	 Catch:{ Throwable -> 0x04a1 }
            goto L_0x04d8;
        L_0x04d7:
            r6 = r14;
        L_0x04d8:
            if (r6 != 0) goto L_0x058b;
        L_0x04da:
            if (r7 == 0) goto L_0x0528;
        L_0x04dc:
            r5 = new java.io.RandomAccessFile;	 Catch:{ Throwable -> 0x0525 }
            r7 = "r";	 Catch:{ Throwable -> 0x0525 }
            r5.<init>(r2, r7);	 Catch:{ Throwable -> 0x0525 }
            r10 = r5.getChannel();	 Catch:{ Throwable -> 0x0525 }
            r11 = java.nio.channels.FileChannel.MapMode.READ_ONLY;	 Catch:{ Throwable -> 0x0525 }
            r12 = 0;	 Catch:{ Throwable -> 0x0525 }
            r14 = r2.length();	 Catch:{ Throwable -> 0x0525 }
            r7 = r10.map(r11, r12, r14);	 Catch:{ Throwable -> 0x0525 }
            r10 = new android.graphics.BitmapFactory$Options;	 Catch:{ Throwable -> 0x0525 }
            r10.<init>();	 Catch:{ Throwable -> 0x0525 }
            r10.inJustDecodeBounds = r4;	 Catch:{ Throwable -> 0x0525 }
            r11 = r7.limit();	 Catch:{ Throwable -> 0x0525 }
            r12 = 0;
            org.telegram.messenger.Utilities.loadWebpImage(r12, r7, r11, r10, r4);	 Catch:{ Throwable -> 0x0521 }
            r11 = r10.outWidth;	 Catch:{ Throwable -> 0x0525 }
            r10 = r10.outHeight;	 Catch:{ Throwable -> 0x0525 }
            r12 = android.graphics.Bitmap.Config.ARGB_8888;	 Catch:{ Throwable -> 0x0525 }
            r10 = org.telegram.messenger.Bitmaps.createBitmap(r11, r10, r12);	 Catch:{ Throwable -> 0x0525 }
            r6 = r7.limit();	 Catch:{ Throwable -> 0x0477 }
            r11 = r8.inPurgeable;	 Catch:{ Throwable -> 0x0477 }
            r11 = r11 ^ r4;
            r12 = 0;
            org.telegram.messenger.Utilities.loadWebpImage(r10, r7, r6, r12, r11);	 Catch:{ Throwable -> 0x051f }
            r5.close();	 Catch:{ Throwable -> 0x0477 }
            r5 = r10;
            r11 = 0;
            r12 = 0;
            goto L_0x058e;
        L_0x051f:
            r3 = r10;
            goto L_0x0522;
        L_0x0521:
            r3 = r6;
        L_0x0522:
            r11 = r12;
            goto L_0x0626;
        L_0x0525:
            r3 = r6;
            goto L_0x0368;
        L_0x0528:
            r7 = r8.inPurgeable;	 Catch:{ Throwable -> 0x0587 }
            if (r7 == 0) goto L_0x0568;
        L_0x052c:
            r7 = new java.io.RandomAccessFile;	 Catch:{ Throwable -> 0x0525 }
            r10 = "r";	 Catch:{ Throwable -> 0x0525 }
            r7.<init>(r2, r10);	 Catch:{ Throwable -> 0x0525 }
            r10 = r7.length();	 Catch:{ Throwable -> 0x0525 }
            r10 = (int) r10;	 Catch:{ Throwable -> 0x0525 }
            r11 = org.telegram.messenger.ImageLoader.bytes;	 Catch:{ Throwable -> 0x0525 }
            if (r11 == 0) goto L_0x054a;	 Catch:{ Throwable -> 0x0525 }
        L_0x053e:
            r11 = org.telegram.messenger.ImageLoader.bytes;	 Catch:{ Throwable -> 0x0525 }
            r11 = r11.length;	 Catch:{ Throwable -> 0x0525 }
            if (r11 < r10) goto L_0x054a;	 Catch:{ Throwable -> 0x0525 }
        L_0x0545:
            r11 = org.telegram.messenger.ImageLoader.bytes;	 Catch:{ Throwable -> 0x0525 }
            goto L_0x054b;	 Catch:{ Throwable -> 0x0525 }
        L_0x054a:
            r11 = 0;	 Catch:{ Throwable -> 0x0525 }
        L_0x054b:
            if (r11 != 0) goto L_0x0552;	 Catch:{ Throwable -> 0x0525 }
        L_0x054d:
            r11 = new byte[r10];	 Catch:{ Throwable -> 0x0525 }
            org.telegram.messenger.ImageLoader.bytes = r11;	 Catch:{ Throwable -> 0x0525 }
        L_0x0552:
            r12 = 0;	 Catch:{ Throwable -> 0x0525 }
            r7.readFully(r11, r12, r10);	 Catch:{ Throwable -> 0x0525 }
            r7.close();	 Catch:{ Throwable -> 0x0525 }
            if (r5 == 0) goto L_0x0562;	 Catch:{ Throwable -> 0x0525 }
        L_0x055b:
            r5 = r1.cacheImage;	 Catch:{ Throwable -> 0x0525 }
            r5 = r5.encryptionKeyPath;	 Catch:{ Throwable -> 0x0525 }
            org.telegram.messenger.secretmedia.EncryptedFileInputStream.decryptBytesWithKeyFile(r11, r12, r10, r5);	 Catch:{ Throwable -> 0x0525 }
        L_0x0562:
            r5 = android.graphics.BitmapFactory.decodeByteArray(r11, r12, r10, r8);	 Catch:{ Throwable -> 0x0525 }
            r11 = 0;	 Catch:{ Throwable -> 0x0525 }
            goto L_0x058e;	 Catch:{ Throwable -> 0x0525 }
        L_0x0568:
            r12 = 0;	 Catch:{ Throwable -> 0x0525 }
            if (r5 == 0) goto L_0x0575;	 Catch:{ Throwable -> 0x0525 }
        L_0x056b:
            r5 = new org.telegram.messenger.secretmedia.EncryptedFileInputStream;	 Catch:{ Throwable -> 0x0525 }
            r7 = r1.cacheImage;	 Catch:{ Throwable -> 0x0525 }
            r7 = r7.encryptionKeyPath;	 Catch:{ Throwable -> 0x0525 }
            r5.<init>(r2, r7);	 Catch:{ Throwable -> 0x0525 }
            goto L_0x057a;
        L_0x0575:
            r5 = new java.io.FileInputStream;	 Catch:{ Throwable -> 0x0587 }
            r5.<init>(r2);	 Catch:{ Throwable -> 0x0587 }
        L_0x057a:
            r11 = 0;
            r7 = android.graphics.BitmapFactory.decodeStream(r5, r11, r8);	 Catch:{ Throwable -> 0x0588 }
            r5.close();	 Catch:{ Throwable -> 0x0584 }
            r5 = r7;
            goto L_0x058e;
        L_0x0584:
            r3 = r7;
            goto L_0x0626;
        L_0x0587:
            r11 = 0;
        L_0x0588:
            r3 = r6;
            goto L_0x0626;
        L_0x058b:
            r11 = 0;
            r12 = 0;
            r5 = r6;
        L_0x058e:
            if (r5 != 0) goto L_0x05a8;
        L_0x0590:
            if (r24 == 0) goto L_0x05a5;
        L_0x0592:
            r3 = r2.length();	 Catch:{ Throwable -> 0x05a5 }
            r6 = 0;	 Catch:{ Throwable -> 0x05a5 }
            r8 = (r3 > r6 ? 1 : (r3 == r6 ? 0 : -1));	 Catch:{ Throwable -> 0x05a5 }
            if (r8 == 0) goto L_0x05a2;	 Catch:{ Throwable -> 0x05a5 }
        L_0x059c:
            r3 = r1.cacheImage;	 Catch:{ Throwable -> 0x05a5 }
            r3 = r3.filter;	 Catch:{ Throwable -> 0x05a5 }
            if (r3 != 0) goto L_0x05a5;	 Catch:{ Throwable -> 0x05a5 }
        L_0x05a2:
            r2.delete();	 Catch:{ Throwable -> 0x05a5 }
        L_0x05a5:
            r3 = r5;	 Catch:{ Throwable -> 0x05a5 }
            goto L_0x0626;	 Catch:{ Throwable -> 0x05a5 }
        L_0x05a8:
            r2 = r1.cacheImage;	 Catch:{ Throwable -> 0x05a5 }
            r2 = r2.filter;	 Catch:{ Throwable -> 0x05a5 }
            if (r2 == 0) goto L_0x0607;	 Catch:{ Throwable -> 0x05a5 }
        L_0x05ae:
            r2 = r5.getWidth();	 Catch:{ Throwable -> 0x05a5 }
            r2 = (float) r2;	 Catch:{ Throwable -> 0x05a5 }
            r6 = r5.getHeight();	 Catch:{ Throwable -> 0x05a5 }
            r6 = (float) r6;	 Catch:{ Throwable -> 0x05a5 }
            r7 = r8.inPurgeable;	 Catch:{ Throwable -> 0x05a5 }
            if (r7 != 0) goto L_0x05db;	 Catch:{ Throwable -> 0x05a5 }
        L_0x05bc:
            r7 = (r9 > r16 ? 1 : (r9 == r16 ? 0 : -1));	 Catch:{ Throwable -> 0x05a5 }
            if (r7 == 0) goto L_0x05db;	 Catch:{ Throwable -> 0x05a5 }
        L_0x05c0:
            r7 = (r2 > r9 ? 1 : (r2 == r9 ? 0 : -1));	 Catch:{ Throwable -> 0x05a5 }
            if (r7 == 0) goto L_0x05db;	 Catch:{ Throwable -> 0x05a5 }
        L_0x05c4:
            r7 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;	 Catch:{ Throwable -> 0x05a5 }
            r7 = r7 + r9;	 Catch:{ Throwable -> 0x05a5 }
            r7 = (r2 > r7 ? 1 : (r2 == r7 ? 0 : -1));	 Catch:{ Throwable -> 0x05a5 }
            if (r7 <= 0) goto L_0x05db;	 Catch:{ Throwable -> 0x05a5 }
        L_0x05cb:
            r7 = r2 / r9;	 Catch:{ Throwable -> 0x05a5 }
            r9 = (int) r9;	 Catch:{ Throwable -> 0x05a5 }
            r7 = r6 / r7;	 Catch:{ Throwable -> 0x05a5 }
            r7 = (int) r7;	 Catch:{ Throwable -> 0x05a5 }
            r7 = org.telegram.messenger.Bitmaps.createScaledBitmap(r5, r9, r7, r4);	 Catch:{ Throwable -> 0x05a5 }
            if (r5 == r7) goto L_0x05db;	 Catch:{ Throwable -> 0x05a5 }
        L_0x05d7:
            r5.recycle();	 Catch:{ Throwable -> 0x05a5 }
            r5 = r7;	 Catch:{ Throwable -> 0x05a5 }
        L_0x05db:
            if (r5 == 0) goto L_0x0607;	 Catch:{ Throwable -> 0x05a5 }
        L_0x05dd:
            if (r3 == 0) goto L_0x0607;	 Catch:{ Throwable -> 0x05a5 }
        L_0x05df:
            r3 = NUM; // 0x42c80000 float:100.0 double:5.53552857E-315;	 Catch:{ Throwable -> 0x05a5 }
            r6 = (r6 > r3 ? 1 : (r6 == r3 ? 0 : -1));	 Catch:{ Throwable -> 0x05a5 }
            if (r6 >= 0) goto L_0x0607;	 Catch:{ Throwable -> 0x05a5 }
        L_0x05e5:
            r2 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1));	 Catch:{ Throwable -> 0x05a5 }
            if (r2 >= 0) goto L_0x0607;	 Catch:{ Throwable -> 0x05a5 }
        L_0x05e9:
            r2 = r5.getConfig();	 Catch:{ Throwable -> 0x05a5 }
            r3 = android.graphics.Bitmap.Config.ARGB_8888;	 Catch:{ Throwable -> 0x05a5 }
            if (r2 != r3) goto L_0x0606;	 Catch:{ Throwable -> 0x05a5 }
        L_0x05f1:
            r14 = 3;	 Catch:{ Throwable -> 0x05a5 }
            r2 = r8.inPurgeable;	 Catch:{ Throwable -> 0x05a5 }
            r15 = r2 ^ 1;	 Catch:{ Throwable -> 0x05a5 }
            r16 = r5.getWidth();	 Catch:{ Throwable -> 0x05a5 }
            r17 = r5.getHeight();	 Catch:{ Throwable -> 0x05a5 }
            r18 = r5.getRowBytes();	 Catch:{ Throwable -> 0x05a5 }
            r13 = r5;	 Catch:{ Throwable -> 0x05a5 }
            org.telegram.messenger.Utilities.blurBitmap(r13, r14, r15, r16, r17, r18);	 Catch:{ Throwable -> 0x05a5 }
        L_0x0606:
            r12 = r4;	 Catch:{ Throwable -> 0x05a5 }
        L_0x0607:
            if (r12 != 0) goto L_0x05a5;	 Catch:{ Throwable -> 0x05a5 }
        L_0x0609:
            r2 = r8.inPurgeable;	 Catch:{ Throwable -> 0x05a5 }
            if (r2 == 0) goto L_0x05a5;	 Catch:{ Throwable -> 0x05a5 }
        L_0x060d:
            org.telegram.messenger.Utilities.pinBitmap(r5);	 Catch:{ Throwable -> 0x05a5 }
            goto L_0x05a5;
        L_0x0611:
            r0 = move-exception;
            r11 = 0;
        L_0x0613:
            r2 = r0;
        L_0x0614:
            monitor-exit(r10);	 Catch:{ all -> 0x0616 }
            throw r2;	 Catch:{ Throwable -> 0x0619 }
        L_0x0616:
            r0 = move-exception;
            goto L_0x0613;
        L_0x0618:
            r11 = 0;
        L_0x0619:
            r3 = r14;
            goto L_0x0626;
        L_0x061b:
            r0 = move-exception;
            r11 = 0;
        L_0x061d:
            r2 = r0;
        L_0x061e:
            monitor-exit(r8);	 Catch:{ all -> 0x0620 }
            throw r2;	 Catch:{ Throwable -> 0x0625 }
        L_0x0620:
            r0 = move-exception;
            goto L_0x061d;
        L_0x0622:
            r11 = 0;
            goto L_0x0625;
        L_0x0624:
            r11 = r8;
        L_0x0625:
            r3 = r11;
        L_0x0626:
            java.lang.Thread.interrupted();
            if (r3 == 0) goto L_0x0631;
        L_0x062b:
            r8 = new android.graphics.drawable.BitmapDrawable;
            r8.<init>(r3);
            goto L_0x0632;
        L_0x0631:
            r8 = r11;
        L_0x0632:
            r1.onPostExecute(r8);
        L_0x0635:
            return;
        L_0x0636:
            r0 = move-exception;
            r3 = r0;
            monitor-exit(r2);	 Catch:{ all -> 0x0636 }
            throw r3;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ImageLoader.CacheOutTask.run():void");
        }

        private void onPostExecute(final BitmapDrawable bitmapDrawable) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    BitmapDrawable bitmapDrawable;
                    if (bitmapDrawable instanceof AnimatedFileDrawable) {
                        bitmapDrawable = bitmapDrawable;
                    } else if (bitmapDrawable != null) {
                        bitmapDrawable = ImageLoader.this.memCache.get(CacheOutTask.this.cacheImage.key);
                        if (bitmapDrawable == null) {
                            ImageLoader.this.memCache.put(CacheOutTask.this.cacheImage.key, bitmapDrawable);
                            bitmapDrawable = bitmapDrawable;
                        } else {
                            bitmapDrawable.getBitmap().recycle();
                        }
                    } else {
                        bitmapDrawable = null;
                    }
                    ImageLoader.this.imageLoadQueue.postRunnable(new Runnable() {
                        public void run() {
                            CacheOutTask.this.cacheImage.setImageAndClear(bitmapDrawable);
                        }
                    });
                }
            });
        }

        public void cancel() {
            /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
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
            r2 = this;
            r0 = r2.sync;
            monitor-enter(r0);
            r1 = 1;
            r2.isCancelled = r1;	 Catch:{ Exception -> 0x0012 }
            r1 = r2.runningThread;	 Catch:{ Exception -> 0x0012 }
            if (r1 == 0) goto L_0x0012;	 Catch:{ Exception -> 0x0012 }
        L_0x000a:
            r1 = r2.runningThread;	 Catch:{ Exception -> 0x0012 }
            r1.interrupt();	 Catch:{ Exception -> 0x0012 }
            goto L_0x0012;
        L_0x0010:
            r1 = move-exception;
            goto L_0x0014;
        L_0x0012:
            monitor-exit(r0);	 Catch:{ all -> 0x0010 }
            return;	 Catch:{ all -> 0x0010 }
        L_0x0014:
            monitor-exit(r0);	 Catch:{ all -> 0x0010 }
            throw r1;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ImageLoader.CacheOutTask.cancel():void");
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

        private void reportProgress(final float f) {
            long currentTimeMillis = System.currentTimeMillis();
            if (f == 1.0f || this.lastProgressTime == 0 || this.lastProgressTime < currentTimeMillis - 500) {
                this.lastProgressTime = currentTimeMillis;
                Utilities.stageQueue.postRunnable(new Runnable() {

                    /* renamed from: org.telegram.messenger.ImageLoader$HttpFileTask$1$1 */
                    class C02061 implements Runnable {
                        C02061() {
                        }

                        public void run() {
                            NotificationCenter.getInstance(HttpFileTask.this.currentAccount).postNotificationName(NotificationCenter.FileLoadProgressChanged, HttpFileTask.this.url, Float.valueOf(f));
                        }
                    }

                    public void run() {
                        ImageLoader.this.fileProgresses.put(HttpFileTask.this.url, Float.valueOf(f));
                        AndroidUtilities.runOnUIThread(new C02061());
                    }
                });
            }
        }

        protected Boolean doInBackground(Void... voidArr) {
            URLConnection openConnection;
            int responseCode;
            Throwable th;
            InputStream inputStream;
            InputStream inputStream2;
            Map headerFields;
            List list;
            String str;
            byte[] bArr;
            int read;
            Throwable th2;
            Throwable th3;
            boolean z = false;
            URLConnection openConnection2;
            try {
                openConnection = new URL(this.url).openConnection();
                try {
                    openConnection.addRequestProperty("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 10_0 like Mac OS X) AppleWebKit/602.1.38 (KHTML, like Gecko) Version/10.0 Mobile/14A5297c Safari/602.1");
                    openConnection.setConnectTimeout(DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS);
                    openConnection.setReadTimeout(DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS);
                    if (openConnection instanceof HttpURLConnection) {
                        HttpURLConnection httpURLConnection = (HttpURLConnection) openConnection;
                        httpURLConnection.setInstanceFollowRedirects(true);
                        responseCode = httpURLConnection.getResponseCode();
                        if (responseCode == 302 || responseCode == 301 || responseCode == 303) {
                            String headerField = httpURLConnection.getHeaderField("Location");
                            String headerField2 = httpURLConnection.getHeaderField("Set-Cookie");
                            openConnection2 = new URL(headerField).openConnection();
                            try {
                                openConnection2.setRequestProperty("Cookie", headerField2);
                                openConnection2.addRequestProperty("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 10_0 like Mac OS X) AppleWebKit/602.1.38 (KHTML, like Gecko) Version/10.0 Mobile/14A5297c Safari/602.1");
                                openConnection = openConnection2;
                            } catch (Throwable th4) {
                                th = th4;
                                inputStream = null;
                                if (th instanceof SocketTimeoutException) {
                                    if (ConnectionsManager.isNetworkOnline()) {
                                        this.canRetry = false;
                                    }
                                } else if (!(th instanceof UnknownHostException)) {
                                    this.canRetry = false;
                                } else if (!(th instanceof SocketException)) {
                                    this.canRetry = false;
                                } else if (th instanceof FileNotFoundException) {
                                    this.canRetry = false;
                                }
                                FileLog.m3e(th);
                                inputStream2 = inputStream;
                                openConnection = openConnection2;
                                if (this.canRetry) {
                                    if (openConnection != null) {
                                        try {
                                            if (openConnection instanceof HttpURLConnection) {
                                                responseCode = ((HttpURLConnection) openConnection).getResponseCode();
                                                this.canRetry = false;
                                            }
                                        } catch (Throwable e) {
                                            FileLog.m3e(e);
                                        }
                                    }
                                    if (openConnection != null) {
                                        try {
                                            headerFields = openConnection.getHeaderFields();
                                            if (headerFields != null) {
                                                list = (List) headerFields.get("content-Length");
                                                str = (String) list.get(0);
                                                if (str != null) {
                                                    this.fileSize = Utilities.parseInt(str).intValue();
                                                }
                                            }
                                        } catch (Throwable th42) {
                                            FileLog.m3e(th42);
                                        }
                                    }
                                    if (inputStream2 != null) {
                                        try {
                                            bArr = new byte[32768];
                                            responseCode = 0;
                                            while (!isCancelled()) {
                                                try {
                                                    read = inputStream2.read(bArr);
                                                    if (read > 0) {
                                                        this.fileOutputStream.write(bArr, 0, read);
                                                        responseCode += read;
                                                        if (this.fileSize > 0) {
                                                            reportProgress(((float) responseCode) / ((float) this.fileSize));
                                                        }
                                                    } else if (read == -1) {
                                                        try {
                                                            if (this.fileSize != 0) {
                                                                reportProgress(1.0f);
                                                            }
                                                            z = true;
                                                        } catch (Throwable e2) {
                                                            th2 = e2;
                                                            z = true;
                                                            th3 = th2;
                                                            FileLog.m3e(th3);
                                                            if (this.fileOutputStream != null) {
                                                                this.fileOutputStream.close();
                                                                this.fileOutputStream = null;
                                                            }
                                                            if (inputStream2 != null) {
                                                                try {
                                                                    inputStream2.close();
                                                                } catch (Throwable th32) {
                                                                    FileLog.m3e(th32);
                                                                }
                                                            }
                                                            return Boolean.valueOf(z);
                                                        } catch (Throwable e22) {
                                                            th2 = e22;
                                                            z = true;
                                                            th32 = th2;
                                                            FileLog.m3e(th32);
                                                            if (this.fileOutputStream != null) {
                                                                this.fileOutputStream.close();
                                                                this.fileOutputStream = null;
                                                            }
                                                            if (inputStream2 != null) {
                                                                inputStream2.close();
                                                            }
                                                            return Boolean.valueOf(z);
                                                        }
                                                    }
                                                } catch (Exception e3) {
                                                    th32 = e3;
                                                }
                                            }
                                        } catch (Throwable th5) {
                                            th32 = th5;
                                            FileLog.m3e(th32);
                                            if (this.fileOutputStream != null) {
                                                this.fileOutputStream.close();
                                                this.fileOutputStream = null;
                                            }
                                            if (inputStream2 != null) {
                                                inputStream2.close();
                                            }
                                            return Boolean.valueOf(z);
                                        }
                                    }
                                    try {
                                        if (this.fileOutputStream != null) {
                                            this.fileOutputStream.close();
                                            this.fileOutputStream = null;
                                        }
                                    } catch (Throwable th322) {
                                        FileLog.m3e(th322);
                                    }
                                    if (inputStream2 != null) {
                                        inputStream2.close();
                                    }
                                }
                                return Boolean.valueOf(z);
                            }
                        }
                    }
                    openConnection.connect();
                    inputStream2 = openConnection.getInputStream();
                } catch (Throwable th6) {
                    th = th6;
                    openConnection2 = openConnection;
                    inputStream = null;
                    if (th instanceof SocketTimeoutException) {
                        if (!(th instanceof UnknownHostException)) {
                            this.canRetry = false;
                        } else if (!(th instanceof SocketException)) {
                            this.canRetry = false;
                        } else if (th instanceof FileNotFoundException) {
                            this.canRetry = false;
                        }
                    } else if (ConnectionsManager.isNetworkOnline()) {
                        this.canRetry = false;
                    }
                    FileLog.m3e(th);
                    inputStream2 = inputStream;
                    openConnection = openConnection2;
                    if (this.canRetry) {
                        if (openConnection != null) {
                            if (openConnection instanceof HttpURLConnection) {
                                responseCode = ((HttpURLConnection) openConnection).getResponseCode();
                                this.canRetry = false;
                            }
                        }
                        if (openConnection != null) {
                            headerFields = openConnection.getHeaderFields();
                            if (headerFields != null) {
                                list = (List) headerFields.get("content-Length");
                                str = (String) list.get(0);
                                if (str != null) {
                                    this.fileSize = Utilities.parseInt(str).intValue();
                                }
                            }
                        }
                        if (inputStream2 != null) {
                            bArr = new byte[32768];
                            responseCode = 0;
                            while (!isCancelled()) {
                                read = inputStream2.read(bArr);
                                if (read > 0) {
                                    this.fileOutputStream.write(bArr, 0, read);
                                    responseCode += read;
                                    if (this.fileSize > 0) {
                                        reportProgress(((float) responseCode) / ((float) this.fileSize));
                                    }
                                } else if (read == -1) {
                                    if (this.fileSize != 0) {
                                        reportProgress(1.0f);
                                    }
                                    z = true;
                                }
                            }
                        }
                        if (this.fileOutputStream != null) {
                            this.fileOutputStream.close();
                            this.fileOutputStream = null;
                        }
                        if (inputStream2 != null) {
                            inputStream2.close();
                        }
                    }
                    return Boolean.valueOf(z);
                }
                try {
                    this.fileOutputStream = new RandomAccessFile(this.tempFile, "rws");
                } catch (Throwable e4) {
                    th2 = e4;
                    openConnection2 = openConnection;
                    inputStream = inputStream2;
                    th = th2;
                    if (th instanceof SocketTimeoutException) {
                        if (ConnectionsManager.isNetworkOnline()) {
                            this.canRetry = false;
                        }
                    } else if (!(th instanceof UnknownHostException)) {
                        this.canRetry = false;
                    } else if (!(th instanceof SocketException)) {
                        if (th.getMessage() != null && th.getMessage().contains("ECONNRESET")) {
                            this.canRetry = false;
                        }
                    } else if (th instanceof FileNotFoundException) {
                        this.canRetry = false;
                    }
                    FileLog.m3e(th);
                    inputStream2 = inputStream;
                    openConnection = openConnection2;
                    if (this.canRetry) {
                        if (openConnection != null) {
                            if (openConnection instanceof HttpURLConnection) {
                                responseCode = ((HttpURLConnection) openConnection).getResponseCode();
                                this.canRetry = false;
                            }
                        }
                        if (openConnection != null) {
                            headerFields = openConnection.getHeaderFields();
                            if (headerFields != null) {
                                list = (List) headerFields.get("content-Length");
                                str = (String) list.get(0);
                                if (str != null) {
                                    this.fileSize = Utilities.parseInt(str).intValue();
                                }
                            }
                        }
                        if (inputStream2 != null) {
                            bArr = new byte[32768];
                            responseCode = 0;
                            while (!isCancelled()) {
                                read = inputStream2.read(bArr);
                                if (read > 0) {
                                    this.fileOutputStream.write(bArr, 0, read);
                                    responseCode += read;
                                    if (this.fileSize > 0) {
                                        reportProgress(((float) responseCode) / ((float) this.fileSize));
                                    }
                                } else if (read == -1) {
                                    if (this.fileSize != 0) {
                                        reportProgress(1.0f);
                                    }
                                    z = true;
                                }
                            }
                        }
                        if (this.fileOutputStream != null) {
                            this.fileOutputStream.close();
                            this.fileOutputStream = null;
                        }
                        if (inputStream2 != null) {
                            inputStream2.close();
                        }
                    }
                    return Boolean.valueOf(z);
                }
            } catch (Throwable th422) {
                openConnection2 = null;
                th = th422;
                inputStream = openConnection2;
                if (th instanceof SocketTimeoutException) {
                    if (ConnectionsManager.isNetworkOnline()) {
                        this.canRetry = false;
                    }
                } else if (!(th instanceof UnknownHostException)) {
                    this.canRetry = false;
                } else if (!(th instanceof SocketException)) {
                    this.canRetry = false;
                } else if (th instanceof FileNotFoundException) {
                    this.canRetry = false;
                }
                FileLog.m3e(th);
                inputStream2 = inputStream;
                openConnection = openConnection2;
                if (this.canRetry) {
                    if (openConnection != null) {
                        if (openConnection instanceof HttpURLConnection) {
                            responseCode = ((HttpURLConnection) openConnection).getResponseCode();
                            this.canRetry = false;
                        }
                    }
                    if (openConnection != null) {
                        headerFields = openConnection.getHeaderFields();
                        if (headerFields != null) {
                            list = (List) headerFields.get("content-Length");
                            str = (String) list.get(0);
                            if (str != null) {
                                this.fileSize = Utilities.parseInt(str).intValue();
                            }
                        }
                    }
                    if (inputStream2 != null) {
                        bArr = new byte[32768];
                        responseCode = 0;
                        while (!isCancelled()) {
                            read = inputStream2.read(bArr);
                            if (read > 0) {
                                this.fileOutputStream.write(bArr, 0, read);
                                responseCode += read;
                                if (this.fileSize > 0) {
                                    reportProgress(((float) responseCode) / ((float) this.fileSize));
                                }
                            } else if (read == -1) {
                                if (this.fileSize != 0) {
                                    reportProgress(1.0f);
                                }
                                z = true;
                            }
                        }
                    }
                    if (this.fileOutputStream != null) {
                        this.fileOutputStream.close();
                        this.fileOutputStream = null;
                    }
                    if (inputStream2 != null) {
                        inputStream2.close();
                    }
                }
                return Boolean.valueOf(z);
            }
            if (this.canRetry) {
                if (openConnection != null) {
                    if (openConnection instanceof HttpURLConnection) {
                        responseCode = ((HttpURLConnection) openConnection).getResponseCode();
                        if (!(responseCode == Callback.DEFAULT_DRAG_ANIMATION_DURATION || responseCode == 202 || responseCode == 304)) {
                            this.canRetry = false;
                        }
                    }
                }
                if (openConnection != null) {
                    headerFields = openConnection.getHeaderFields();
                    if (headerFields != null) {
                        list = (List) headerFields.get("content-Length");
                        if (!(list == null || list.isEmpty())) {
                            str = (String) list.get(0);
                            if (str != null) {
                                this.fileSize = Utilities.parseInt(str).intValue();
                            }
                        }
                    }
                }
                if (inputStream2 != null) {
                    bArr = new byte[32768];
                    responseCode = 0;
                    while (!isCancelled()) {
                        read = inputStream2.read(bArr);
                        if (read > 0) {
                            this.fileOutputStream.write(bArr, 0, read);
                            responseCode += read;
                            if (this.fileSize > 0) {
                                reportProgress(((float) responseCode) / ((float) this.fileSize));
                            }
                        } else if (read == -1) {
                            if (this.fileSize != 0) {
                                reportProgress(1.0f);
                            }
                            z = true;
                        }
                    }
                }
                if (this.fileOutputStream != null) {
                    this.fileOutputStream.close();
                    this.fileOutputStream = null;
                }
                if (inputStream2 != null) {
                    inputStream2.close();
                }
            }
            return Boolean.valueOf(z);
        }

        protected void onPostExecute(Boolean bool) {
            ImageLoader.this.runHttpFileLoadTasks(this, bool.booleanValue() != null ? 2 : true);
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

        public HttpImageTask(CacheImage cacheImage, int i) {
            this.cacheImage = cacheImage;
            this.imageSize = i;
        }

        private void reportProgress(final float f) {
            long currentTimeMillis = System.currentTimeMillis();
            if (f == 1.0f || this.lastProgressTime == 0 || this.lastProgressTime < currentTimeMillis - 500) {
                this.lastProgressTime = currentTimeMillis;
                Utilities.stageQueue.postRunnable(new Runnable() {

                    /* renamed from: org.telegram.messenger.ImageLoader$HttpImageTask$1$1 */
                    class C02081 implements Runnable {
                        C02081() {
                        }

                        public void run() {
                            NotificationCenter.getInstance(HttpImageTask.this.cacheImage.currentAccount).postNotificationName(NotificationCenter.FileLoadProgressChanged, HttpImageTask.this.cacheImage.url, Float.valueOf(f));
                        }
                    }

                    public void run() {
                        ImageLoader.this.fileProgresses.put(HttpImageTask.this.cacheImage.url, Float.valueOf(f));
                        AndroidUtilities.runOnUIThread(new C02081());
                    }
                });
            }
        }

        protected java.lang.Boolean doInBackground(java.lang.Void... r9) {
            /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
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
            r8 = this;
            r9 = r8.isCancelled();
            r0 = 1;
            r1 = 0;
            r2 = 0;
            if (r9 != 0) goto L_0x008e;
        L_0x0009:
            r9 = new java.net.URL;	 Catch:{ Throwable -> 0x0055 }
            r3 = r8.cacheImage;	 Catch:{ Throwable -> 0x0055 }
            r3 = r3.httpUrl;	 Catch:{ Throwable -> 0x0055 }
            r9.<init>(r3);	 Catch:{ Throwable -> 0x0055 }
            r9 = r9.openConnection();	 Catch:{ Throwable -> 0x0055 }
            r9 = (java.net.HttpURLConnection) r9;	 Catch:{ Throwable -> 0x0055 }
            r8.httpConnection = r9;	 Catch:{ Throwable -> 0x0055 }
            r9 = r8.httpConnection;	 Catch:{ Throwable -> 0x0055 }
            r3 = "User-Agent";	 Catch:{ Throwable -> 0x0055 }
            r4 = "Mozilla/5.0 (iPhone; CPU iPhone OS 10_0 like Mac OS X) AppleWebKit/602.1.38 (KHTML, like Gecko) Version/10.0 Mobile/14A5297c Safari/602.1";	 Catch:{ Throwable -> 0x0055 }
            r9.addRequestProperty(r3, r4);	 Catch:{ Throwable -> 0x0055 }
            r9 = r8.httpConnection;	 Catch:{ Throwable -> 0x0055 }
            r3 = 5000; // 0x1388 float:7.006E-42 double:2.4703E-320;	 Catch:{ Throwable -> 0x0055 }
            r9.setConnectTimeout(r3);	 Catch:{ Throwable -> 0x0055 }
            r9 = r8.httpConnection;	 Catch:{ Throwable -> 0x0055 }
            r9.setReadTimeout(r3);	 Catch:{ Throwable -> 0x0055 }
            r9 = r8.httpConnection;	 Catch:{ Throwable -> 0x0055 }
            r9.setInstanceFollowRedirects(r0);	 Catch:{ Throwable -> 0x0055 }
            r9 = r8.isCancelled();	 Catch:{ Throwable -> 0x0055 }
            if (r9 != 0) goto L_0x008e;	 Catch:{ Throwable -> 0x0055 }
        L_0x003a:
            r9 = r8.httpConnection;	 Catch:{ Throwable -> 0x0055 }
            r9.connect();	 Catch:{ Throwable -> 0x0055 }
            r9 = r8.httpConnection;	 Catch:{ Throwable -> 0x0055 }
            r9 = r9.getInputStream();	 Catch:{ Throwable -> 0x0055 }
            r3 = new java.io.RandomAccessFile;	 Catch:{ Throwable -> 0x0053 }
            r4 = r8.cacheImage;	 Catch:{ Throwable -> 0x0053 }
            r4 = r4.tempFilePath;	 Catch:{ Throwable -> 0x0053 }
            r5 = "rws";	 Catch:{ Throwable -> 0x0053 }
            r3.<init>(r4, r5);	 Catch:{ Throwable -> 0x0053 }
            r8.fileOutputStream = r3;	 Catch:{ Throwable -> 0x0053 }
            goto L_0x008f;
        L_0x0053:
            r3 = move-exception;
            goto L_0x0057;
        L_0x0055:
            r3 = move-exception;
            r9 = r1;
        L_0x0057:
            r4 = r3 instanceof java.net.SocketTimeoutException;
            if (r4 == 0) goto L_0x0064;
        L_0x005b:
            r4 = org.telegram.tgnet.ConnectionsManager.isNetworkOnline();
            if (r4 == 0) goto L_0x008a;
        L_0x0061:
            r8.canRetry = r2;
            goto L_0x008a;
        L_0x0064:
            r4 = r3 instanceof java.net.UnknownHostException;
            if (r4 == 0) goto L_0x006b;
        L_0x0068:
            r8.canRetry = r2;
            goto L_0x008a;
        L_0x006b:
            r4 = r3 instanceof java.net.SocketException;
            if (r4 == 0) goto L_0x0084;
        L_0x006f:
            r4 = r3.getMessage();
            if (r4 == 0) goto L_0x008a;
        L_0x0075:
            r4 = r3.getMessage();
            r5 = "ECONNRESET";
            r4 = r4.contains(r5);
            if (r4 == 0) goto L_0x008a;
        L_0x0081:
            r8.canRetry = r2;
            goto L_0x008a;
        L_0x0084:
            r4 = r3 instanceof java.io.FileNotFoundException;
            if (r4 == 0) goto L_0x008a;
        L_0x0088:
            r8.canRetry = r2;
        L_0x008a:
            org.telegram.messenger.FileLog.m3e(r3);
            goto L_0x008f;
        L_0x008e:
            r9 = r1;
        L_0x008f:
            r3 = r8.isCancelled();
            if (r3 != 0) goto L_0x0137;
        L_0x0095:
            r3 = r8.httpConnection;	 Catch:{ Exception -> 0x00b4 }
            if (r3 == 0) goto L_0x00b8;	 Catch:{ Exception -> 0x00b4 }
        L_0x0099:
            r3 = r8.httpConnection;	 Catch:{ Exception -> 0x00b4 }
            r3 = r3 instanceof java.net.HttpURLConnection;	 Catch:{ Exception -> 0x00b4 }
            if (r3 == 0) goto L_0x00b8;	 Catch:{ Exception -> 0x00b4 }
        L_0x009f:
            r3 = r8.httpConnection;	 Catch:{ Exception -> 0x00b4 }
            r3 = r3.getResponseCode();	 Catch:{ Exception -> 0x00b4 }
            r4 = 200; // 0xc8 float:2.8E-43 double:9.9E-322;	 Catch:{ Exception -> 0x00b4 }
            if (r3 == r4) goto L_0x00b8;	 Catch:{ Exception -> 0x00b4 }
        L_0x00a9:
            r4 = 202; // 0xca float:2.83E-43 double:1.0E-321;	 Catch:{ Exception -> 0x00b4 }
            if (r3 == r4) goto L_0x00b8;	 Catch:{ Exception -> 0x00b4 }
        L_0x00ad:
            r4 = 304; // 0x130 float:4.26E-43 double:1.5E-321;	 Catch:{ Exception -> 0x00b4 }
            if (r3 == r4) goto L_0x00b8;	 Catch:{ Exception -> 0x00b4 }
        L_0x00b1:
            r8.canRetry = r2;	 Catch:{ Exception -> 0x00b4 }
            goto L_0x00b8;
        L_0x00b4:
            r3 = move-exception;
            org.telegram.messenger.FileLog.m3e(r3);
        L_0x00b8:
            r3 = r8.imageSize;
            if (r3 != 0) goto L_0x00ef;
        L_0x00bc:
            r3 = r8.httpConnection;
            if (r3 == 0) goto L_0x00ef;
        L_0x00c0:
            r3 = r8.httpConnection;	 Catch:{ Exception -> 0x00eb }
            r3 = r3.getHeaderFields();	 Catch:{ Exception -> 0x00eb }
            if (r3 == 0) goto L_0x00ef;	 Catch:{ Exception -> 0x00eb }
        L_0x00c8:
            r4 = "content-Length";	 Catch:{ Exception -> 0x00eb }
            r3 = r3.get(r4);	 Catch:{ Exception -> 0x00eb }
            r3 = (java.util.List) r3;	 Catch:{ Exception -> 0x00eb }
            if (r3 == 0) goto L_0x00ef;	 Catch:{ Exception -> 0x00eb }
        L_0x00d2:
            r4 = r3.isEmpty();	 Catch:{ Exception -> 0x00eb }
            if (r4 != 0) goto L_0x00ef;	 Catch:{ Exception -> 0x00eb }
        L_0x00d8:
            r3 = r3.get(r2);	 Catch:{ Exception -> 0x00eb }
            r3 = (java.lang.String) r3;	 Catch:{ Exception -> 0x00eb }
            if (r3 == 0) goto L_0x00ef;	 Catch:{ Exception -> 0x00eb }
        L_0x00e0:
            r3 = org.telegram.messenger.Utilities.parseInt(r3);	 Catch:{ Exception -> 0x00eb }
            r3 = r3.intValue();	 Catch:{ Exception -> 0x00eb }
            r8.imageSize = r3;	 Catch:{ Exception -> 0x00eb }
            goto L_0x00ef;
        L_0x00eb:
            r3 = move-exception;
            org.telegram.messenger.FileLog.m3e(r3);
        L_0x00ef:
            if (r9 == 0) goto L_0x0137;
        L_0x00f1:
            r3 = 8192; // 0x2000 float:1.14794E-41 double:4.0474E-320;
            r3 = new byte[r3];	 Catch:{ Throwable -> 0x0133 }
            r4 = r2;	 Catch:{ Throwable -> 0x0133 }
        L_0x00f6:
            r5 = r8.isCancelled();	 Catch:{ Throwable -> 0x0133 }
            if (r5 == 0) goto L_0x00fd;
        L_0x00fc:
            goto L_0x0137;
        L_0x00fd:
            r5 = r9.read(r3);	 Catch:{ Exception -> 0x012e }
            if (r5 <= 0) goto L_0x0116;	 Catch:{ Exception -> 0x012e }
        L_0x0103:
            r4 = r4 + r5;	 Catch:{ Exception -> 0x012e }
            r6 = r8.fileOutputStream;	 Catch:{ Exception -> 0x012e }
            r6.write(r3, r2, r5);	 Catch:{ Exception -> 0x012e }
            r5 = r8.imageSize;	 Catch:{ Exception -> 0x012e }
            if (r5 == 0) goto L_0x00f6;	 Catch:{ Exception -> 0x012e }
        L_0x010d:
            r5 = (float) r4;	 Catch:{ Exception -> 0x012e }
            r6 = r8.imageSize;	 Catch:{ Exception -> 0x012e }
            r6 = (float) r6;	 Catch:{ Exception -> 0x012e }
            r5 = r5 / r6;	 Catch:{ Exception -> 0x012e }
            r8.reportProgress(r5);	 Catch:{ Exception -> 0x012e }
            goto L_0x00f6;
        L_0x0116:
            r3 = -1;
            if (r5 != r3) goto L_0x0137;
        L_0x0119:
            r2 = r8.imageSize;	 Catch:{ Exception -> 0x0129, Throwable -> 0x0124 }
            if (r2 == 0) goto L_0x0122;	 Catch:{ Exception -> 0x0129, Throwable -> 0x0124 }
        L_0x011d:
            r2 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;	 Catch:{ Exception -> 0x0129, Throwable -> 0x0124 }
            r8.reportProgress(r2);	 Catch:{ Exception -> 0x0129, Throwable -> 0x0124 }
        L_0x0122:
            r2 = r0;
            goto L_0x0137;
        L_0x0124:
            r2 = move-exception;
            r7 = r2;
            r2 = r0;
            r0 = r7;
            goto L_0x0134;
        L_0x0129:
            r2 = move-exception;
            r7 = r2;
            r2 = r0;
            r0 = r7;
            goto L_0x012f;
        L_0x012e:
            r0 = move-exception;
        L_0x012f:
            org.telegram.messenger.FileLog.m3e(r0);	 Catch:{ Throwable -> 0x0133 }
            goto L_0x0137;
        L_0x0133:
            r0 = move-exception;
        L_0x0134:
            org.telegram.messenger.FileLog.m3e(r0);
        L_0x0137:
            r0 = r8.fileOutputStream;	 Catch:{ Throwable -> 0x0143 }
            if (r0 == 0) goto L_0x0147;	 Catch:{ Throwable -> 0x0143 }
        L_0x013b:
            r0 = r8.fileOutputStream;	 Catch:{ Throwable -> 0x0143 }
            r0.close();	 Catch:{ Throwable -> 0x0143 }
            r8.fileOutputStream = r1;	 Catch:{ Throwable -> 0x0143 }
            goto L_0x0147;
        L_0x0143:
            r0 = move-exception;
            org.telegram.messenger.FileLog.m3e(r0);
        L_0x0147:
            r0 = r8.httpConnection;	 Catch:{ Throwable -> 0x0150 }
            if (r0 == 0) goto L_0x0150;	 Catch:{ Throwable -> 0x0150 }
        L_0x014b:
            r0 = r8.httpConnection;	 Catch:{ Throwable -> 0x0150 }
            r0.disconnect();	 Catch:{ Throwable -> 0x0150 }
        L_0x0150:
            if (r9 == 0) goto L_0x015a;
        L_0x0152:
            r9.close();	 Catch:{ Throwable -> 0x0156 }
            goto L_0x015a;
        L_0x0156:
            r9 = move-exception;
            org.telegram.messenger.FileLog.m3e(r9);
        L_0x015a:
            if (r2 == 0) goto L_0x0178;
        L_0x015c:
            r9 = r8.cacheImage;
            r9 = r9.tempFilePath;
            if (r9 == 0) goto L_0x0178;
        L_0x0162:
            r9 = r8.cacheImage;
            r9 = r9.tempFilePath;
            r0 = r8.cacheImage;
            r0 = r0.finalFilePath;
            r9 = r9.renameTo(r0);
            if (r9 != 0) goto L_0x0178;
        L_0x0170:
            r9 = r8.cacheImage;
            r0 = r8.cacheImage;
            r0 = r0.tempFilePath;
            r9.finalFilePath = r0;
        L_0x0178:
            r9 = java.lang.Boolean.valueOf(r2);
            return r9;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ImageLoader.HttpImageTask.doInBackground(java.lang.Void[]):java.lang.Boolean");
        }

        protected void onPostExecute(final Boolean bool) {
            if (!bool.booleanValue()) {
                if (this.canRetry) {
                    ImageLoader.this.httpFileLoadError(this.cacheImage.url);
                    Utilities.stageQueue.postRunnable(new Runnable() {

                        /* renamed from: org.telegram.messenger.ImageLoader$HttpImageTask$2$1 */
                        class C02101 implements Runnable {
                            C02101() {
                            }

                            public void run() {
                                if (bool.booleanValue()) {
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
            }
            ImageLoader.this.fileDidLoaded(this.cacheImage.url, this.cacheImage.finalFilePath, 0);
            Utilities.stageQueue.postRunnable(/* anonymous class already generated */);
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

        public ThumbGenerateTask(int i, File file, FileLocation fileLocation, String str) {
            this.mediaType = i;
            this.originalPath = file;
            this.thumbLocation = fileLocation;
            this.filter = str;
        }

        private void removeTask() {
            if (this.thumbLocation != null) {
                final String attachFileName = FileLoader.getAttachFileName(this.thumbLocation);
                ImageLoader.this.imageLoadQueue.postRunnable(new Runnable() {
                    public void run() {
                        ImageLoader.this.thumbGenerateTasks.remove(attachFileName);
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
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(this.thumbLocation.volume_id);
                stringBuilder.append("_");
                stringBuilder.append(this.thumbLocation.local_id);
                final String stringBuilder2 = stringBuilder.toString();
                File directory = FileLoader.getDirectory(4);
                StringBuilder stringBuilder3 = new StringBuilder();
                stringBuilder3.append("q_");
                stringBuilder3.append(stringBuilder2);
                stringBuilder3.append(".jpg");
                File file = new File(directory, stringBuilder3.toString());
                if (!file.exists()) {
                    if (this.originalPath.exists()) {
                        int min = Math.min(180, Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y) / 4);
                        Bitmap bitmap = null;
                        float f;
                        if (this.mediaType == 0) {
                            f = (float) min;
                            bitmap = ImageLoader.loadBitmap(this.originalPath.toString(), null, f, f, false);
                        } else if (this.mediaType == 2) {
                            bitmap = ThumbnailUtils.createVideoThumbnail(this.originalPath.toString(), 1);
                        } else if (this.mediaType == 3) {
                            String toLowerCase = this.originalPath.toString().toLowerCase();
                            if (toLowerCase.endsWith(".jpg") || toLowerCase.endsWith(".jpeg") || toLowerCase.endsWith(".png") || toLowerCase.endsWith(".gif")) {
                                f = (float) min;
                                bitmap = ImageLoader.loadBitmap(toLowerCase, null, f, f, false);
                            } else {
                                removeTask();
                                return;
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
                                float f2 = (float) width;
                                float f3 = (float) min;
                                float f4 = (float) height;
                                f3 = Math.min(f2 / f3, f4 / f3);
                                Bitmap createScaledBitmap = Bitmaps.createScaledBitmap(bitmap, (int) (f2 / f3), (int) (f4 / f3), true);
                                if (createScaledBitmap != bitmap) {
                                    bitmap.recycle();
                                } else {
                                    createScaledBitmap = bitmap;
                                }
                                OutputStream fileOutputStream = new FileOutputStream(file);
                                createScaledBitmap.compress(CompressFormat.JPEG, 60, fileOutputStream);
                                fileOutputStream.close();
                                final BitmapDrawable bitmapDrawable = new BitmapDrawable(createScaledBitmap);
                                AndroidUtilities.runOnUIThread(new Runnable() {
                                    public void run() {
                                        ThumbGenerateTask.this.removeTask();
                                        String str = stringBuilder2;
                                        if (ThumbGenerateTask.this.filter != null) {
                                            StringBuilder stringBuilder = new StringBuilder();
                                            stringBuilder.append(str);
                                            stringBuilder.append("@");
                                            stringBuilder.append(ThumbGenerateTask.this.filter);
                                            str = stringBuilder.toString();
                                        }
                                        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.messageThumbGenerated, bitmapDrawable, str);
                                        ImageLoader.this.memCache.put(str, bitmapDrawable);
                                    }
                                });
                                return;
                            }
                        }
                        removeTask();
                        return;
                    }
                }
                removeTask();
            } catch (Throwable e) {
                FileLog.m3e(e);
            } catch (Throwable th) {
                FileLog.m3e(th);
                removeTask();
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
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:60)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
        /*
        r5 = this;
        r5.<init>();
        r0 = new java.util.HashMap;
        r0.<init>();
        r5.bitmapUseCounts = r0;
        r0 = new java.util.HashMap;
        r0.<init>();
        r5.imageLoadingByUrl = r0;
        r0 = new java.util.HashMap;
        r0.<init>();
        r5.imageLoadingByKeys = r0;
        r0 = new android.util.SparseArray;
        r0.<init>();
        r5.imageLoadingByTag = r0;
        r0 = new java.util.HashMap;
        r0.<init>();
        r5.waitingForQualityThumb = r0;
        r0 = new android.util.SparseArray;
        r0.<init>();
        r5.waitingForQualityThumbByTag = r0;
        r0 = new java.util.LinkedList;
        r0.<init>();
        r5.httpTasks = r0;
        r0 = new org.telegram.messenger.DispatchQueue;
        r1 = "cacheOutQueue";
        r0.<init>(r1);
        r5.cacheOutQueue = r0;
        r0 = new org.telegram.messenger.DispatchQueue;
        r1 = "cacheThumbOutQueue";
        r0.<init>(r1);
        r5.cacheThumbOutQueue = r0;
        r0 = new org.telegram.messenger.DispatchQueue;
        r1 = "thumbGeneratingQueue";
        r0.<init>(r1);
        r5.thumbGeneratingQueue = r0;
        r0 = new org.telegram.messenger.DispatchQueue;
        r1 = "imageLoadQueue";
        r0.<init>(r1);
        r5.imageLoadQueue = r0;
        r0 = new java.util.concurrent.ConcurrentHashMap;
        r0.<init>();
        r5.fileProgresses = r0;
        r0 = new java.util.HashMap;
        r0.<init>();
        r5.thumbGenerateTasks = r0;
        r0 = new java.util.HashMap;
        r0.<init>();
        r5.forceLoadingImages = r0;
        r0 = 0;
        r5.currentHttpTasksCount = r0;
        r1 = new java.util.LinkedList;
        r1.<init>();
        r5.httpFileLoadTasks = r1;
        r1 = new java.util.HashMap;
        r1.<init>();
        r5.httpFileLoadTasksByKeys = r1;
        r1 = new java.util.HashMap;
        r1.<init>();
        r5.retryHttpsTasks = r1;
        r5.currentHttpFileLoadTasksCount = r0;
        r1 = 0;
        r5.ignoreRemoval = r1;
        r2 = 0;
        r5.lastCacheOutTime = r2;
        r5.lastImageNum = r0;
        r5.lastProgressUpdateTime = r2;
        r5.telegramPath = r1;
        r1 = r5.thumbGeneratingQueue;
        r2 = 1;
        r1.setPriority(r2);
        r1 = org.telegram.messenger.ApplicationLoader.applicationContext;
        r2 = "activity";
        r1 = r1.getSystemService(r2);
        r1 = (android.app.ActivityManager) r1;
        r1 = r1.getMemoryClass();
        r1 = r1 / 7;
        r2 = 15;
        r1 = java.lang.Math.min(r2, r1);
        r1 = r1 * 1024;
        r1 = r1 * 1024;
        r2 = new org.telegram.messenger.ImageLoader$1;
        r2.<init>(r1);
        r5.memCache = r2;
        r1 = new android.util.SparseArray;
        r1.<init>();
        r2 = org.telegram.messenger.AndroidUtilities.getCacheDir();
        r3 = r2.isDirectory();
        if (r3 != 0) goto L_0x00d2;
    L_0x00ca:
        r2.mkdirs();	 Catch:{ Exception -> 0x00ce }
        goto L_0x00d2;
    L_0x00ce:
        r3 = move-exception;
        org.telegram.messenger.FileLog.m3e(r3);
    L_0x00d2:
        r3 = new java.io.File;	 Catch:{ Exception -> 0x00dd }
        r4 = ".nomedia";	 Catch:{ Exception -> 0x00dd }
        r3.<init>(r2, r4);	 Catch:{ Exception -> 0x00dd }
        r3.createNewFile();	 Catch:{ Exception -> 0x00dd }
        goto L_0x00e1;
    L_0x00dd:
        r3 = move-exception;
        org.telegram.messenger.FileLog.m3e(r3);
    L_0x00e1:
        r3 = 4;
        r1.put(r3, r2);
    L_0x00e5:
        r2 = 3;
        if (r0 >= r2) goto L_0x00f7;
    L_0x00e8:
        r2 = org.telegram.messenger.FileLoader.getInstance(r0);
        r3 = new org.telegram.messenger.ImageLoader$2;
        r3.<init>(r0);
        r2.setDelegate(r3);
        r0 = r0 + 1;
        goto L_0x00e5;
    L_0x00f7:
        org.telegram.messenger.FileLoader.setMediaDirs(r1);
        r0 = new org.telegram.messenger.ImageLoader$3;
        r0.<init>();
        r1 = new android.content.IntentFilter;
        r1.<init>();
        r2 = "android.intent.action.MEDIA_BAD_REMOVAL";
        r1.addAction(r2);
        r2 = "android.intent.action.MEDIA_CHECKING";
        r1.addAction(r2);
        r2 = "android.intent.action.MEDIA_EJECT";
        r1.addAction(r2);
        r2 = "android.intent.action.MEDIA_MOUNTED";
        r1.addAction(r2);
        r2 = "android.intent.action.MEDIA_NOFS";
        r1.addAction(r2);
        r2 = "android.intent.action.MEDIA_REMOVED";
        r1.addAction(r2);
        r2 = "android.intent.action.MEDIA_SHARED";
        r1.addAction(r2);
        r2 = "android.intent.action.MEDIA_UNMOUNTABLE";
        r1.addAction(r2);
        r2 = "android.intent.action.MEDIA_UNMOUNTED";
        r1.addAction(r2);
        r2 = "file";
        r1.addDataScheme(r2);
        r2 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Throwable -> 0x013b }
        r2.registerReceiver(r0, r1);	 Catch:{ Throwable -> 0x013b }
    L_0x013b:
        r5.checkMediaPaths();
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ImageLoader.<init>():void");
    }

    public void checkMediaPaths() {
        this.cacheOutQueue.postRunnable(new C01974());
    }

    public SparseArray<File> createMediaPaths() {
        SparseArray<File> sparseArray = new SparseArray();
        File cacheDir = AndroidUtilities.getCacheDir();
        if (!cacheDir.isDirectory()) {
            try {
                cacheDir.mkdirs();
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
        }
        try {
            new File(cacheDir, ".nomedia").createNewFile();
        } catch (Throwable e2) {
            FileLog.m3e(e2);
        }
        sparseArray.put(4, cacheDir);
        if (BuildVars.LOGS_ENABLED) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("cache path = ");
            stringBuilder.append(cacheDir);
            FileLog.m0d(stringBuilder.toString());
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
                                FileLog.m0d(stringBuilder2.toString());
                            }
                        }
                    } catch (Throwable e22) {
                        FileLog.m3e(e22);
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
                                FileLog.m0d(stringBuilder2.toString());
                            }
                        }
                    } catch (Throwable e222) {
                        FileLog.m3e(e222);
                    }
                    try {
                        file = new File(this.telegramPath, "Telegram Audio");
                        file.mkdir();
                        if (file.isDirectory() && canMoveFiles(cacheDir, file, 1)) {
                            new File(file, ".nomedia").createNewFile();
                            sparseArray.put(1, file);
                            if (BuildVars.LOGS_ENABLED) {
                                stringBuilder2 = new StringBuilder();
                                stringBuilder2.append("audio path = ");
                                stringBuilder2.append(file);
                                FileLog.m0d(stringBuilder2.toString());
                            }
                        }
                    } catch (Throwable e2222) {
                        FileLog.m3e(e2222);
                    }
                    try {
                        file = new File(this.telegramPath, "Telegram Documents");
                        file.mkdir();
                        if (file.isDirectory() && canMoveFiles(cacheDir, file, 3)) {
                            new File(file, ".nomedia").createNewFile();
                            sparseArray.put(3, file);
                            if (BuildVars.LOGS_ENABLED) {
                                StringBuilder stringBuilder3 = new StringBuilder();
                                stringBuilder3.append("documents path = ");
                                stringBuilder3.append(file);
                                FileLog.m0d(stringBuilder3.toString());
                            }
                        }
                    } catch (Throwable e3) {
                        FileLog.m3e(e3);
                    }
                }
            } else if (BuildVars.LOGS_ENABLED) {
                FileLog.m0d("this Android can't rename files");
            }
            SharedConfig.checkSaveToGalleryFiles();
        } catch (Throwable e32) {
            FileLog.m3e(e32);
        }
        return sparseArray;
    }

    private boolean canMoveFiles(File file, File file2, int i) {
        RandomAccessFile randomAccessFile = null;
        if (i == 0) {
            try {
                i = new File(file, "000000000_999999_temp.jpg");
                file = new File(file2, "000000000_999999.jpg");
            } catch (Exception e) {
                e = e;
                try {
                    Throwable e2;
                    FileLog.m3e(e2);
                    if (randomAccessFile != null) {
                        try {
                            randomAccessFile.close();
                        } catch (Throwable e22) {
                            FileLog.m3e(e22);
                        }
                    }
                    return null;
                } catch (Throwable th) {
                    file = th;
                    if (randomAccessFile != null) {
                        try {
                            randomAccessFile.close();
                        } catch (Throwable e3) {
                            FileLog.m3e(e3);
                        }
                    }
                    throw file;
                }
            }
        } else if (i == 3) {
            i = new File(file, "000000000_999999_temp.doc");
            file = new File(file2, "000000000_999999.doc");
        } else if (i == 1) {
            i = new File(file, "000000000_999999_temp.ogg");
            file = new File(file2, "000000000_999999.ogg");
        } else if (i == 2) {
            i = new File(file, "000000000_999999_temp.mp4");
            file = new File(file2, "000000000_999999.mp4");
        } else {
            file = null;
            i = file;
        }
        file2 = new byte[1024];
        i.createNewFile();
        RandomAccessFile randomAccessFile2 = new RandomAccessFile(i, "rws");
        try {
            randomAccessFile2.write(file2);
            randomAccessFile2.close();
            file2 = i.renameTo(file);
            i.delete();
            file.delete();
            if (file2 != null) {
                return true;
            }
        } catch (Exception e4) {
            e22 = e4;
            randomAccessFile = randomAccessFile2;
            FileLog.m3e(e22);
            if (randomAccessFile != null) {
                randomAccessFile.close();
            }
            return null;
        } catch (Throwable th2) {
            file = th2;
            randomAccessFile = randomAccessFile2;
            if (randomAccessFile != null) {
                randomAccessFile.close();
            }
            throw file;
        }
        return null;
    }

    public Float getFileProgress(String str) {
        return str == null ? null : (Float) this.fileProgresses.get(str);
    }

    private void performReplace(String str, String str2) {
        BitmapDrawable bitmapDrawable = this.memCache.get(str);
        if (bitmapDrawable != null) {
            BitmapDrawable bitmapDrawable2 = this.memCache.get(str2);
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
        return null;
    }

    public void removeImage(String str) {
        this.bitmapUseCounts.remove(str);
        this.memCache.remove(str);
    }

    public boolean isInCache(String str) {
        return this.memCache.get(str) != null ? true : null;
    }

    public void clearMemory() {
        this.memCache.evictAll();
    }

    private void removeFromWaitingForThumb(int i) {
        String str = (String) this.waitingForQualityThumbByTag.get(i);
        if (str != null) {
            ThumbGenerateInfo thumbGenerateInfo = (ThumbGenerateInfo) this.waitingForQualityThumb.get(str);
            if (thumbGenerateInfo != null) {
                thumbGenerateInfo.count = thumbGenerateInfo.count - 1;
                if (thumbGenerateInfo.count == 0) {
                    this.waitingForQualityThumb.remove(str);
                }
            }
            this.waitingForQualityThumbByTag.remove(i);
        }
    }

    public void cancelLoadingForImageReceiver(final ImageReceiver imageReceiver, final int i) {
        if (imageReceiver != null) {
            this.imageLoadQueue.postRunnable(new Runnable() {
                public void run() {
                    int i;
                    int i2 = 2;
                    if (i == 1) {
                        i = 0;
                        i2 = 1;
                    } else {
                        i = i == 2 ? 1 : 0;
                    }
                    while (i < i2) {
                        int tag = imageReceiver.getTag(i == 0);
                        if (i == 0) {
                            ImageLoader.this.removeFromWaitingForThumb(tag);
                        }
                        if (tag != 0) {
                            CacheImage cacheImage = (CacheImage) ImageLoader.this.imageLoadingByTag.get(tag);
                            if (cacheImage != null) {
                                cacheImage.removeImageReceiver(imageReceiver);
                            }
                        }
                        i++;
                    }
                }
            });
        }
    }

    public BitmapDrawable getImageFromMemory(String str) {
        return this.memCache.get(str);
    }

    public BitmapDrawable getImageFromMemory(TLObject tLObject, String str, String str2) {
        String str3 = null;
        if (tLObject == null && str == null) {
            return null;
        }
        if (str != null) {
            str3 = Utilities.MD5(str);
        } else if ((tLObject instanceof FileLocation) != null) {
            FileLocation fileLocation = (FileLocation) tLObject;
            str = new StringBuilder();
            str.append(fileLocation.volume_id);
            str.append("_");
            str.append(fileLocation.local_id);
            str3 = str.toString();
        } else if ((tLObject instanceof Document) != null) {
            Document document = (Document) tLObject;
            if (document.version == null) {
                str = new StringBuilder();
                str.append(document.dc_id);
                str.append("_");
                str.append(document.id);
                tLObject = str.toString();
            } else {
                str = new StringBuilder();
                str.append(document.dc_id);
                str.append("_");
                str.append(document.id);
                str.append("_");
                str.append(document.version);
                tLObject = str.toString();
            }
            str3 = tLObject;
        } else if ((tLObject instanceof TL_webDocument) != null) {
            str3 = Utilities.MD5(((TL_webDocument) tLObject).url);
        }
        if (str2 != null) {
            tLObject = new StringBuilder();
            tLObject.append(str3);
            tLObject.append("@");
            tLObject.append(str2);
            str3 = tLObject.toString();
        }
        return this.memCache.get(str3);
    }

    private void replaceImageInCacheInternal(String str, String str2, FileLocation fileLocation) {
        ArrayList filterKeys = this.memCache.getFilterKeys(str);
        if (filterKeys != null) {
            for (int i = 0; i < filterKeys.size(); i++) {
                String str3 = (String) filterKeys.get(i);
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(str);
                stringBuilder.append("@");
                stringBuilder.append(str3);
                String stringBuilder2 = stringBuilder.toString();
                StringBuilder stringBuilder3 = new StringBuilder();
                stringBuilder3.append(str2);
                stringBuilder3.append("@");
                stringBuilder3.append(str3);
                performReplace(stringBuilder2, stringBuilder3.toString());
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didReplacedPhotoInMemCache, stringBuilder2, str3, fileLocation);
            }
            return;
        }
        performReplace(str, str2);
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didReplacedPhotoInMemCache, str, str2, fileLocation);
    }

    public void replaceImageInCache(final String str, final String str2, final FileLocation fileLocation, boolean z) {
        if (z) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    ImageLoader.this.replaceImageInCacheInternal(str, str2, fileLocation);
                }
            });
        } else {
            replaceImageInCacheInternal(str, str2, fileLocation);
        }
    }

    public void putImageToCache(BitmapDrawable bitmapDrawable, String str) {
        this.memCache.put(str, bitmapDrawable);
    }

    private void generateThumb(int i, File file, FileLocation fileLocation, String str) {
        if ((i == 0 || i == 2 || i == 3) && file != null) {
            if (fileLocation != null) {
                if (((ThumbGenerateTask) this.thumbGenerateTasks.get(FileLoader.getAttachFileName(fileLocation))) == null) {
                    this.thumbGeneratingQueue.postRunnable(new ThumbGenerateTask(i, file, fileLocation, str));
                }
            }
        }
    }

    public void cancelForceLoadingForImageReceiver(ImageReceiver imageReceiver) {
        if (imageReceiver != null) {
            imageReceiver = imageReceiver.getKey();
            if (imageReceiver != null) {
                this.imageLoadQueue.postRunnable(new Runnable() {
                    public void run() {
                        ImageLoader.this.forceLoadingImages.remove(imageReceiver);
                    }
                });
            }
        }
    }

    private void createLoadOperationForImageReceiver(ImageReceiver imageReceiver, String str, String str2, String str3, TLObject tLObject, String str4, String str5, int i, int i2, int i3) {
        ImageLoader imageLoader = this;
        final ImageReceiver imageReceiver2 = imageReceiver;
        if (!(imageReceiver2 == null || str2 == null)) {
            if (str != null) {
                int tag = imageReceiver2.getTag(i3 != 0);
                if (tag == 0) {
                    tag = imageLoader.lastImageNum;
                    imageReceiver2.setTag(tag, i3 != 0);
                    imageLoader.lastImageNum++;
                    if (imageLoader.lastImageNum == ConnectionsManager.DEFAULT_DATACENTER_ID) {
                        imageLoader.lastImageNum = 0;
                    }
                }
                final boolean isNeedsQualityThumb = imageReceiver.isNeedsQualityThumb();
                final MessageObject parentMessageObject = imageReceiver.getParentMessageObject();
                final boolean isShouldGenerateQualityThumb = imageReceiver.isShouldGenerateQualityThumb();
                final int i4 = imageReceiver.getcurrentAccount();
                final int i5 = i3;
                final String str6 = str2;
                final String str7 = str;
                final String str8 = str5;
                final String str9 = str4;
                final TLObject tLObject2 = tLObject;
                C02018 c02018 = r0;
                final int i6 = i2;
                DispatchQueue dispatchQueue = imageLoader.imageLoadQueue;
                final int i7 = i;
                final String str10 = str3;
                C02018 c020182 = new Runnable() {
                    /* JADX WARNING: inconsistent code. */
                    /* Code decompiled incorrectly, please refer to instructions dump. */
                    public void run() {
                        boolean z;
                        if (i5 != 2) {
                            CacheImage cacheImage = (CacheImage) ImageLoader.this.imageLoadingByUrl.get(str6);
                            CacheImage cacheImage2 = (CacheImage) ImageLoader.this.imageLoadingByKeys.get(str7);
                            CacheImage cacheImage3 = (CacheImage) ImageLoader.this.imageLoadingByTag.get(tag);
                            if (cacheImage3 != null) {
                                if (cacheImage3 != cacheImage2) {
                                    if (cacheImage3 != cacheImage) {
                                        cacheImage3.removeImageReceiver(imageReceiver2);
                                    } else if (cacheImage2 == null) {
                                        cacheImage3.replaceImageReceiver(imageReceiver2, str7, str8, i5 != 0);
                                    }
                                }
                                z = true;
                                if (!(z || cacheImage2 == null)) {
                                    cacheImage2.addImageReceiver(imageReceiver2, str7, str8, i5 == 0);
                                    z = true;
                                }
                                if (!(z || cacheImage == null)) {
                                    cacheImage.addImageReceiver(imageReceiver2, str7, str8, i5 == 0);
                                    z = true;
                                }
                            }
                            z = false;
                            if (i5 == 0) {
                            }
                            cacheImage2.addImageReceiver(imageReceiver2, str7, str8, i5 == 0);
                            z = true;
                            if (i5 == 0) {
                            }
                            cacheImage.addImageReceiver(imageReceiver2, str7, str8, i5 == 0);
                            z = true;
                        } else {
                            z = false;
                        }
                        if (!z) {
                            boolean z2;
                            File file;
                            boolean z3;
                            boolean z4;
                            CacheImage cacheImage4;
                            File directory;
                            StringBuilder stringBuilder;
                            FileLocation fileLocation;
                            int i;
                            String MD5;
                            File directory2;
                            StringBuilder stringBuilder2;
                            File file2;
                            if (str9 != null) {
                                if (!str9.startsWith("http")) {
                                    int indexOf;
                                    if (str9.startsWith("thumb://")) {
                                        indexOf = str9.indexOf(":", 8);
                                        if (indexOf >= 0) {
                                            file2 = new File(str9.substring(indexOf + 1));
                                            z2 = false;
                                            file = file2;
                                            z3 = true;
                                            if (i5 == 2) {
                                                if (!(tLObject2 instanceof TL_documentEncrypted)) {
                                                    if (tLObject2 instanceof TL_fileEncryptedLocation) {
                                                        z4 = false;
                                                        cacheImage4 = new CacheImage();
                                                        if (str9 == null && !str9.startsWith("vthumb") && !str9.startsWith("thumb")) {
                                                            String httpUrlExtension = ImageLoader.getHttpUrlExtension(str9, "jpg");
                                                            if (httpUrlExtension.equals("mp4") || httpUrlExtension.equals("gif")) {
                                                                cacheImage4.animatedFile = true;
                                                            }
                                                        } else if (((tLObject2 instanceof TL_webDocument) && MessageObject.isGifDocument((TL_webDocument) tLObject2)) || ((tLObject2 instanceof Document) && (MessageObject.isGifDocument((Document) tLObject2) || MessageObject.isRoundVideoDocument((Document) tLObject2)))) {
                                                            cacheImage4.animatedFile = true;
                                                        }
                                                        if (file == null) {
                                                            if (i6 == 0 && i7 > 0 && str9 == null) {
                                                                if (!z4) {
                                                                    file = tLObject2 instanceof Document ? MessageObject.isVideoDocument((Document) tLObject2) ? new File(FileLoader.getDirectory(2), str6) : new File(FileLoader.getDirectory(3), str6) : tLObject2 instanceof TL_webDocument ? new File(FileLoader.getDirectory(3), str6) : new File(FileLoader.getDirectory(0), str6);
                                                                }
                                                            }
                                                            file = new File(FileLoader.getDirectory(4), str6);
                                                            if (file.exists()) {
                                                                z2 = true;
                                                            } else if (i6 == 2) {
                                                                directory = FileLoader.getDirectory(4);
                                                                stringBuilder = new StringBuilder();
                                                                stringBuilder.append(str6);
                                                                stringBuilder.append(".enc");
                                                                file = new File(directory, stringBuilder.toString());
                                                            }
                                                        }
                                                        cacheImage4.selfThumb = i5 == 0;
                                                        cacheImage4.key = str7;
                                                        cacheImage4.filter = str8;
                                                        cacheImage4.httpUrl = str9;
                                                        cacheImage4.ext = str10;
                                                        cacheImage4.currentAccount = i4;
                                                        if (i6 == 2) {
                                                            directory = FileLoader.getInternalCacheDir();
                                                            stringBuilder = new StringBuilder();
                                                            stringBuilder.append(str6);
                                                            stringBuilder.append(".enc.key");
                                                            cacheImage4.encryptionKeyPath = new File(directory, stringBuilder.toString());
                                                        }
                                                        cacheImage4.addImageReceiver(imageReceiver2, str7, str8, i5 == 0);
                                                        if (!(z3 || r7)) {
                                                            if (file.exists()) {
                                                                cacheImage4.url = str6;
                                                                cacheImage4.location = tLObject2;
                                                                ImageLoader.this.imageLoadingByUrl.put(str6, cacheImage4);
                                                                if (str9 != null) {
                                                                    if (tLObject2 instanceof FileLocation) {
                                                                        fileLocation = (FileLocation) tLObject2;
                                                                        i = i6;
                                                                        if (i == 0 && (i7 <= 0 || fileLocation.key != null)) {
                                                                            i = 1;
                                                                        }
                                                                        FileLoader.getInstance(i4).loadFile(fileLocation, str10, i7, i);
                                                                    } else if (tLObject2 instanceof Document) {
                                                                        FileLoader.getInstance(i4).loadFile((Document) tLObject2, true, i6);
                                                                    } else if (tLObject2 instanceof TL_webDocument) {
                                                                        FileLoader.getInstance(i4).loadFile((TL_webDocument) tLObject2, true, i6);
                                                                    }
                                                                    if (imageReceiver2.isForceLoding()) {
                                                                        ImageLoader.this.forceLoadingImages.put(cacheImage4.key, Integer.valueOf(0));
                                                                        return;
                                                                    }
                                                                    return;
                                                                }
                                                                MD5 = Utilities.MD5(str9);
                                                                directory2 = FileLoader.getDirectory(4);
                                                                stringBuilder2 = new StringBuilder();
                                                                stringBuilder2.append(MD5);
                                                                stringBuilder2.append("_temp.jpg");
                                                                cacheImage4.tempFilePath = new File(directory2, stringBuilder2.toString());
                                                                cacheImage4.finalFilePath = file;
                                                                cacheImage4.httpTask = new HttpImageTask(cacheImage4, i7);
                                                                ImageLoader.this.httpTasks.add(cacheImage4.httpTask);
                                                                ImageLoader.this.runHttpTasks(false);
                                                                return;
                                                            }
                                                        }
                                                        cacheImage4.finalFilePath = file;
                                                        cacheImage4.cacheTask = new CacheOutTask(cacheImage4);
                                                        ImageLoader.this.imageLoadingByKeys.put(str7, cacheImage4);
                                                        if (i5 == 0) {
                                                            ImageLoader.this.cacheThumbOutQueue.postRunnable(cacheImage4.cacheTask);
                                                        } else {
                                                            ImageLoader.this.cacheOutQueue.postRunnable(cacheImage4.cacheTask);
                                                        }
                                                    }
                                                }
                                                z4 = true;
                                                cacheImage4 = new CacheImage();
                                                if (str9 == null) {
                                                }
                                                cacheImage4.animatedFile = true;
                                                if (file == null) {
                                                    if (!z4) {
                                                        file = new File(FileLoader.getDirectory(4), str6);
                                                        if (file.exists()) {
                                                            z2 = true;
                                                        } else if (i6 == 2) {
                                                            directory = FileLoader.getDirectory(4);
                                                            stringBuilder = new StringBuilder();
                                                            stringBuilder.append(str6);
                                                            stringBuilder.append(".enc");
                                                            file = new File(directory, stringBuilder.toString());
                                                        }
                                                    } else if (tLObject2 instanceof Document) {
                                                        if (MessageObject.isVideoDocument((Document) tLObject2)) {
                                                        }
                                                    }
                                                }
                                                if (i5 == 0) {
                                                }
                                                cacheImage4.selfThumb = i5 == 0;
                                                cacheImage4.key = str7;
                                                cacheImage4.filter = str8;
                                                cacheImage4.httpUrl = str9;
                                                cacheImage4.ext = str10;
                                                cacheImage4.currentAccount = i4;
                                                if (i6 == 2) {
                                                    directory = FileLoader.getInternalCacheDir();
                                                    stringBuilder = new StringBuilder();
                                                    stringBuilder.append(str6);
                                                    stringBuilder.append(".enc.key");
                                                    cacheImage4.encryptionKeyPath = new File(directory, stringBuilder.toString());
                                                }
                                                if (i5 == 0) {
                                                }
                                                cacheImage4.addImageReceiver(imageReceiver2, str7, str8, i5 == 0);
                                                if (file.exists()) {
                                                    cacheImage4.url = str6;
                                                    cacheImage4.location = tLObject2;
                                                    ImageLoader.this.imageLoadingByUrl.put(str6, cacheImage4);
                                                    if (str9 != null) {
                                                        MD5 = Utilities.MD5(str9);
                                                        directory2 = FileLoader.getDirectory(4);
                                                        stringBuilder2 = new StringBuilder();
                                                        stringBuilder2.append(MD5);
                                                        stringBuilder2.append("_temp.jpg");
                                                        cacheImage4.tempFilePath = new File(directory2, stringBuilder2.toString());
                                                        cacheImage4.finalFilePath = file;
                                                        cacheImage4.httpTask = new HttpImageTask(cacheImage4, i7);
                                                        ImageLoader.this.httpTasks.add(cacheImage4.httpTask);
                                                        ImageLoader.this.runHttpTasks(false);
                                                        return;
                                                    }
                                                    if (tLObject2 instanceof FileLocation) {
                                                        fileLocation = (FileLocation) tLObject2;
                                                        i = i6;
                                                        i = 1;
                                                        FileLoader.getInstance(i4).loadFile(fileLocation, str10, i7, i);
                                                    } else if (tLObject2 instanceof Document) {
                                                        FileLoader.getInstance(i4).loadFile((Document) tLObject2, true, i6);
                                                    } else if (tLObject2 instanceof TL_webDocument) {
                                                        FileLoader.getInstance(i4).loadFile((TL_webDocument) tLObject2, true, i6);
                                                    }
                                                    if (imageReceiver2.isForceLoding()) {
                                                        ImageLoader.this.forceLoadingImages.put(cacheImage4.key, Integer.valueOf(0));
                                                        return;
                                                    }
                                                    return;
                                                }
                                                cacheImage4.finalFilePath = file;
                                                cacheImage4.cacheTask = new CacheOutTask(cacheImage4);
                                                ImageLoader.this.imageLoadingByKeys.put(str7, cacheImage4);
                                                if (i5 == 0) {
                                                    ImageLoader.this.cacheOutQueue.postRunnable(cacheImage4.cacheTask);
                                                } else {
                                                    ImageLoader.this.cacheThumbOutQueue.postRunnable(cacheImage4.cacheTask);
                                                }
                                            }
                                        }
                                    } else if (str9.startsWith("vthumb://")) {
                                        indexOf = str9.indexOf(":", 9);
                                        if (indexOf >= 0) {
                                            file2 = new File(str9.substring(indexOf + 1));
                                            z2 = false;
                                            file = file2;
                                            z3 = true;
                                            if (i5 == 2) {
                                                if (tLObject2 instanceof TL_documentEncrypted) {
                                                    if (tLObject2 instanceof TL_fileEncryptedLocation) {
                                                        z4 = false;
                                                        cacheImage4 = new CacheImage();
                                                        if (str9 == null) {
                                                        }
                                                        cacheImage4.animatedFile = true;
                                                        if (file == null) {
                                                            if (!z4) {
                                                                file = new File(FileLoader.getDirectory(4), str6);
                                                                if (file.exists()) {
                                                                    z2 = true;
                                                                } else if (i6 == 2) {
                                                                    directory = FileLoader.getDirectory(4);
                                                                    stringBuilder = new StringBuilder();
                                                                    stringBuilder.append(str6);
                                                                    stringBuilder.append(".enc");
                                                                    file = new File(directory, stringBuilder.toString());
                                                                }
                                                            } else if (tLObject2 instanceof Document) {
                                                                if (MessageObject.isVideoDocument((Document) tLObject2)) {
                                                                }
                                                            }
                                                        }
                                                        if (i5 == 0) {
                                                        }
                                                        cacheImage4.selfThumb = i5 == 0;
                                                        cacheImage4.key = str7;
                                                        cacheImage4.filter = str8;
                                                        cacheImage4.httpUrl = str9;
                                                        cacheImage4.ext = str10;
                                                        cacheImage4.currentAccount = i4;
                                                        if (i6 == 2) {
                                                            directory = FileLoader.getInternalCacheDir();
                                                            stringBuilder = new StringBuilder();
                                                            stringBuilder.append(str6);
                                                            stringBuilder.append(".enc.key");
                                                            cacheImage4.encryptionKeyPath = new File(directory, stringBuilder.toString());
                                                        }
                                                        if (i5 == 0) {
                                                        }
                                                        cacheImage4.addImageReceiver(imageReceiver2, str7, str8, i5 == 0);
                                                        if (file.exists()) {
                                                            cacheImage4.finalFilePath = file;
                                                            cacheImage4.cacheTask = new CacheOutTask(cacheImage4);
                                                            ImageLoader.this.imageLoadingByKeys.put(str7, cacheImage4);
                                                            if (i5 == 0) {
                                                                ImageLoader.this.cacheThumbOutQueue.postRunnable(cacheImage4.cacheTask);
                                                            } else {
                                                                ImageLoader.this.cacheOutQueue.postRunnable(cacheImage4.cacheTask);
                                                            }
                                                        }
                                                        cacheImage4.url = str6;
                                                        cacheImage4.location = tLObject2;
                                                        ImageLoader.this.imageLoadingByUrl.put(str6, cacheImage4);
                                                        if (str9 != null) {
                                                            if (tLObject2 instanceof FileLocation) {
                                                                fileLocation = (FileLocation) tLObject2;
                                                                i = i6;
                                                                i = 1;
                                                                FileLoader.getInstance(i4).loadFile(fileLocation, str10, i7, i);
                                                            } else if (tLObject2 instanceof Document) {
                                                                FileLoader.getInstance(i4).loadFile((Document) tLObject2, true, i6);
                                                            } else if (tLObject2 instanceof TL_webDocument) {
                                                                FileLoader.getInstance(i4).loadFile((TL_webDocument) tLObject2, true, i6);
                                                            }
                                                            if (imageReceiver2.isForceLoding()) {
                                                                ImageLoader.this.forceLoadingImages.put(cacheImage4.key, Integer.valueOf(0));
                                                                return;
                                                            }
                                                            return;
                                                        }
                                                        MD5 = Utilities.MD5(str9);
                                                        directory2 = FileLoader.getDirectory(4);
                                                        stringBuilder2 = new StringBuilder();
                                                        stringBuilder2.append(MD5);
                                                        stringBuilder2.append("_temp.jpg");
                                                        cacheImage4.tempFilePath = new File(directory2, stringBuilder2.toString());
                                                        cacheImage4.finalFilePath = file;
                                                        cacheImage4.httpTask = new HttpImageTask(cacheImage4, i7);
                                                        ImageLoader.this.httpTasks.add(cacheImage4.httpTask);
                                                        ImageLoader.this.runHttpTasks(false);
                                                        return;
                                                    }
                                                }
                                                z4 = true;
                                                cacheImage4 = new CacheImage();
                                                if (str9 == null) {
                                                }
                                                cacheImage4.animatedFile = true;
                                                if (file == null) {
                                                    if (!z4) {
                                                        file = new File(FileLoader.getDirectory(4), str6);
                                                        if (file.exists()) {
                                                            z2 = true;
                                                        } else if (i6 == 2) {
                                                            directory = FileLoader.getDirectory(4);
                                                            stringBuilder = new StringBuilder();
                                                            stringBuilder.append(str6);
                                                            stringBuilder.append(".enc");
                                                            file = new File(directory, stringBuilder.toString());
                                                        }
                                                    } else if (tLObject2 instanceof Document) {
                                                        if (tLObject2 instanceof TL_webDocument) {
                                                        }
                                                    }
                                                }
                                                if (i5 == 0) {
                                                }
                                                cacheImage4.selfThumb = i5 == 0;
                                                cacheImage4.key = str7;
                                                cacheImage4.filter = str8;
                                                cacheImage4.httpUrl = str9;
                                                cacheImage4.ext = str10;
                                                cacheImage4.currentAccount = i4;
                                                if (i6 == 2) {
                                                    directory = FileLoader.getInternalCacheDir();
                                                    stringBuilder = new StringBuilder();
                                                    stringBuilder.append(str6);
                                                    stringBuilder.append(".enc.key");
                                                    cacheImage4.encryptionKeyPath = new File(directory, stringBuilder.toString());
                                                }
                                                if (i5 == 0) {
                                                }
                                                cacheImage4.addImageReceiver(imageReceiver2, str7, str8, i5 == 0);
                                                if (file.exists()) {
                                                    cacheImage4.url = str6;
                                                    cacheImage4.location = tLObject2;
                                                    ImageLoader.this.imageLoadingByUrl.put(str6, cacheImage4);
                                                    if (str9 != null) {
                                                        MD5 = Utilities.MD5(str9);
                                                        directory2 = FileLoader.getDirectory(4);
                                                        stringBuilder2 = new StringBuilder();
                                                        stringBuilder2.append(MD5);
                                                        stringBuilder2.append("_temp.jpg");
                                                        cacheImage4.tempFilePath = new File(directory2, stringBuilder2.toString());
                                                        cacheImage4.finalFilePath = file;
                                                        cacheImage4.httpTask = new HttpImageTask(cacheImage4, i7);
                                                        ImageLoader.this.httpTasks.add(cacheImage4.httpTask);
                                                        ImageLoader.this.runHttpTasks(false);
                                                        return;
                                                    }
                                                    if (tLObject2 instanceof FileLocation) {
                                                        fileLocation = (FileLocation) tLObject2;
                                                        i = i6;
                                                        i = 1;
                                                        FileLoader.getInstance(i4).loadFile(fileLocation, str10, i7, i);
                                                    } else if (tLObject2 instanceof Document) {
                                                        FileLoader.getInstance(i4).loadFile((Document) tLObject2, true, i6);
                                                    } else if (tLObject2 instanceof TL_webDocument) {
                                                        FileLoader.getInstance(i4).loadFile((TL_webDocument) tLObject2, true, i6);
                                                    }
                                                    if (imageReceiver2.isForceLoding()) {
                                                        ImageLoader.this.forceLoadingImages.put(cacheImage4.key, Integer.valueOf(0));
                                                        return;
                                                    }
                                                    return;
                                                }
                                                cacheImage4.finalFilePath = file;
                                                cacheImage4.cacheTask = new CacheOutTask(cacheImage4);
                                                ImageLoader.this.imageLoadingByKeys.put(str7, cacheImage4);
                                                if (i5 == 0) {
                                                    ImageLoader.this.cacheOutQueue.postRunnable(cacheImage4.cacheTask);
                                                } else {
                                                    ImageLoader.this.cacheThumbOutQueue.postRunnable(cacheImage4.cacheTask);
                                                }
                                            }
                                        }
                                    } else {
                                        file = new File(str9);
                                        z2 = false;
                                        z3 = true;
                                        if (i5 == 2) {
                                            if (tLObject2 instanceof TL_documentEncrypted) {
                                                if (tLObject2 instanceof TL_fileEncryptedLocation) {
                                                    z4 = false;
                                                    cacheImage4 = new CacheImage();
                                                    if (str9 == null) {
                                                    }
                                                    cacheImage4.animatedFile = true;
                                                    if (file == null) {
                                                        if (!z4) {
                                                            file = new File(FileLoader.getDirectory(4), str6);
                                                            if (file.exists()) {
                                                                z2 = true;
                                                            } else if (i6 == 2) {
                                                                directory = FileLoader.getDirectory(4);
                                                                stringBuilder = new StringBuilder();
                                                                stringBuilder.append(str6);
                                                                stringBuilder.append(".enc");
                                                                file = new File(directory, stringBuilder.toString());
                                                            }
                                                        } else if (tLObject2 instanceof Document) {
                                                            if (MessageObject.isVideoDocument((Document) tLObject2)) {
                                                            }
                                                        }
                                                    }
                                                    if (i5 == 0) {
                                                    }
                                                    cacheImage4.selfThumb = i5 == 0;
                                                    cacheImage4.key = str7;
                                                    cacheImage4.filter = str8;
                                                    cacheImage4.httpUrl = str9;
                                                    cacheImage4.ext = str10;
                                                    cacheImage4.currentAccount = i4;
                                                    if (i6 == 2) {
                                                        directory = FileLoader.getInternalCacheDir();
                                                        stringBuilder = new StringBuilder();
                                                        stringBuilder.append(str6);
                                                        stringBuilder.append(".enc.key");
                                                        cacheImage4.encryptionKeyPath = new File(directory, stringBuilder.toString());
                                                    }
                                                    if (i5 == 0) {
                                                    }
                                                    cacheImage4.addImageReceiver(imageReceiver2, str7, str8, i5 == 0);
                                                    if (file.exists()) {
                                                        cacheImage4.finalFilePath = file;
                                                        cacheImage4.cacheTask = new CacheOutTask(cacheImage4);
                                                        ImageLoader.this.imageLoadingByKeys.put(str7, cacheImage4);
                                                        if (i5 == 0) {
                                                            ImageLoader.this.cacheThumbOutQueue.postRunnable(cacheImage4.cacheTask);
                                                        } else {
                                                            ImageLoader.this.cacheOutQueue.postRunnable(cacheImage4.cacheTask);
                                                        }
                                                    }
                                                    cacheImage4.url = str6;
                                                    cacheImage4.location = tLObject2;
                                                    ImageLoader.this.imageLoadingByUrl.put(str6, cacheImage4);
                                                    if (str9 != null) {
                                                        if (tLObject2 instanceof FileLocation) {
                                                            fileLocation = (FileLocation) tLObject2;
                                                            i = i6;
                                                            i = 1;
                                                            FileLoader.getInstance(i4).loadFile(fileLocation, str10, i7, i);
                                                        } else if (tLObject2 instanceof Document) {
                                                            FileLoader.getInstance(i4).loadFile((Document) tLObject2, true, i6);
                                                        } else if (tLObject2 instanceof TL_webDocument) {
                                                            FileLoader.getInstance(i4).loadFile((TL_webDocument) tLObject2, true, i6);
                                                        }
                                                        if (imageReceiver2.isForceLoding()) {
                                                            ImageLoader.this.forceLoadingImages.put(cacheImage4.key, Integer.valueOf(0));
                                                            return;
                                                        }
                                                        return;
                                                    }
                                                    MD5 = Utilities.MD5(str9);
                                                    directory2 = FileLoader.getDirectory(4);
                                                    stringBuilder2 = new StringBuilder();
                                                    stringBuilder2.append(MD5);
                                                    stringBuilder2.append("_temp.jpg");
                                                    cacheImage4.tempFilePath = new File(directory2, stringBuilder2.toString());
                                                    cacheImage4.finalFilePath = file;
                                                    cacheImage4.httpTask = new HttpImageTask(cacheImage4, i7);
                                                    ImageLoader.this.httpTasks.add(cacheImage4.httpTask);
                                                    ImageLoader.this.runHttpTasks(false);
                                                    return;
                                                }
                                            }
                                            z4 = true;
                                            cacheImage4 = new CacheImage();
                                            if (str9 == null) {
                                            }
                                            cacheImage4.animatedFile = true;
                                            if (file == null) {
                                                if (!z4) {
                                                    file = new File(FileLoader.getDirectory(4), str6);
                                                    if (file.exists()) {
                                                        z2 = true;
                                                    } else if (i6 == 2) {
                                                        directory = FileLoader.getDirectory(4);
                                                        stringBuilder = new StringBuilder();
                                                        stringBuilder.append(str6);
                                                        stringBuilder.append(".enc");
                                                        file = new File(directory, stringBuilder.toString());
                                                    }
                                                } else if (tLObject2 instanceof Document) {
                                                    if (tLObject2 instanceof TL_webDocument) {
                                                    }
                                                }
                                            }
                                            if (i5 == 0) {
                                            }
                                            cacheImage4.selfThumb = i5 == 0;
                                            cacheImage4.key = str7;
                                            cacheImage4.filter = str8;
                                            cacheImage4.httpUrl = str9;
                                            cacheImage4.ext = str10;
                                            cacheImage4.currentAccount = i4;
                                            if (i6 == 2) {
                                                directory = FileLoader.getInternalCacheDir();
                                                stringBuilder = new StringBuilder();
                                                stringBuilder.append(str6);
                                                stringBuilder.append(".enc.key");
                                                cacheImage4.encryptionKeyPath = new File(directory, stringBuilder.toString());
                                            }
                                            if (i5 == 0) {
                                            }
                                            cacheImage4.addImageReceiver(imageReceiver2, str7, str8, i5 == 0);
                                            if (file.exists()) {
                                                cacheImage4.url = str6;
                                                cacheImage4.location = tLObject2;
                                                ImageLoader.this.imageLoadingByUrl.put(str6, cacheImage4);
                                                if (str9 != null) {
                                                    MD5 = Utilities.MD5(str9);
                                                    directory2 = FileLoader.getDirectory(4);
                                                    stringBuilder2 = new StringBuilder();
                                                    stringBuilder2.append(MD5);
                                                    stringBuilder2.append("_temp.jpg");
                                                    cacheImage4.tempFilePath = new File(directory2, stringBuilder2.toString());
                                                    cacheImage4.finalFilePath = file;
                                                    cacheImage4.httpTask = new HttpImageTask(cacheImage4, i7);
                                                    ImageLoader.this.httpTasks.add(cacheImage4.httpTask);
                                                    ImageLoader.this.runHttpTasks(false);
                                                    return;
                                                }
                                                if (tLObject2 instanceof FileLocation) {
                                                    fileLocation = (FileLocation) tLObject2;
                                                    i = i6;
                                                    i = 1;
                                                    FileLoader.getInstance(i4).loadFile(fileLocation, str10, i7, i);
                                                } else if (tLObject2 instanceof Document) {
                                                    FileLoader.getInstance(i4).loadFile((Document) tLObject2, true, i6);
                                                } else if (tLObject2 instanceof TL_webDocument) {
                                                    FileLoader.getInstance(i4).loadFile((TL_webDocument) tLObject2, true, i6);
                                                }
                                                if (imageReceiver2.isForceLoding()) {
                                                    ImageLoader.this.forceLoadingImages.put(cacheImage4.key, Integer.valueOf(0));
                                                    return;
                                                }
                                                return;
                                            }
                                            cacheImage4.finalFilePath = file;
                                            cacheImage4.cacheTask = new CacheOutTask(cacheImage4);
                                            ImageLoader.this.imageLoadingByKeys.put(str7, cacheImage4);
                                            if (i5 == 0) {
                                                ImageLoader.this.cacheOutQueue.postRunnable(cacheImage4.cacheTask);
                                            } else {
                                                ImageLoader.this.cacheThumbOutQueue.postRunnable(cacheImage4.cacheTask);
                                            }
                                        }
                                    }
                                    file2 = null;
                                    z2 = false;
                                    file = file2;
                                    z3 = true;
                                    if (i5 == 2) {
                                        if (tLObject2 instanceof TL_documentEncrypted) {
                                            if (tLObject2 instanceof TL_fileEncryptedLocation) {
                                                z4 = false;
                                                cacheImage4 = new CacheImage();
                                                if (str9 == null) {
                                                }
                                                cacheImage4.animatedFile = true;
                                                if (file == null) {
                                                    if (!z4) {
                                                        file = new File(FileLoader.getDirectory(4), str6);
                                                        if (file.exists()) {
                                                            z2 = true;
                                                        } else if (i6 == 2) {
                                                            directory = FileLoader.getDirectory(4);
                                                            stringBuilder = new StringBuilder();
                                                            stringBuilder.append(str6);
                                                            stringBuilder.append(".enc");
                                                            file = new File(directory, stringBuilder.toString());
                                                        }
                                                    } else if (tLObject2 instanceof Document) {
                                                        if (MessageObject.isVideoDocument((Document) tLObject2)) {
                                                        }
                                                    }
                                                }
                                                if (i5 == 0) {
                                                }
                                                cacheImage4.selfThumb = i5 == 0;
                                                cacheImage4.key = str7;
                                                cacheImage4.filter = str8;
                                                cacheImage4.httpUrl = str9;
                                                cacheImage4.ext = str10;
                                                cacheImage4.currentAccount = i4;
                                                if (i6 == 2) {
                                                    directory = FileLoader.getInternalCacheDir();
                                                    stringBuilder = new StringBuilder();
                                                    stringBuilder.append(str6);
                                                    stringBuilder.append(".enc.key");
                                                    cacheImage4.encryptionKeyPath = new File(directory, stringBuilder.toString());
                                                }
                                                if (i5 == 0) {
                                                }
                                                cacheImage4.addImageReceiver(imageReceiver2, str7, str8, i5 == 0);
                                                if (file.exists()) {
                                                    cacheImage4.finalFilePath = file;
                                                    cacheImage4.cacheTask = new CacheOutTask(cacheImage4);
                                                    ImageLoader.this.imageLoadingByKeys.put(str7, cacheImage4);
                                                    if (i5 == 0) {
                                                        ImageLoader.this.cacheThumbOutQueue.postRunnable(cacheImage4.cacheTask);
                                                    } else {
                                                        ImageLoader.this.cacheOutQueue.postRunnable(cacheImage4.cacheTask);
                                                    }
                                                }
                                                cacheImage4.url = str6;
                                                cacheImage4.location = tLObject2;
                                                ImageLoader.this.imageLoadingByUrl.put(str6, cacheImage4);
                                                if (str9 != null) {
                                                    if (tLObject2 instanceof FileLocation) {
                                                        fileLocation = (FileLocation) tLObject2;
                                                        i = i6;
                                                        i = 1;
                                                        FileLoader.getInstance(i4).loadFile(fileLocation, str10, i7, i);
                                                    } else if (tLObject2 instanceof Document) {
                                                        FileLoader.getInstance(i4).loadFile((Document) tLObject2, true, i6);
                                                    } else if (tLObject2 instanceof TL_webDocument) {
                                                        FileLoader.getInstance(i4).loadFile((TL_webDocument) tLObject2, true, i6);
                                                    }
                                                    if (imageReceiver2.isForceLoding()) {
                                                        ImageLoader.this.forceLoadingImages.put(cacheImage4.key, Integer.valueOf(0));
                                                        return;
                                                    }
                                                    return;
                                                }
                                                MD5 = Utilities.MD5(str9);
                                                directory2 = FileLoader.getDirectory(4);
                                                stringBuilder2 = new StringBuilder();
                                                stringBuilder2.append(MD5);
                                                stringBuilder2.append("_temp.jpg");
                                                cacheImage4.tempFilePath = new File(directory2, stringBuilder2.toString());
                                                cacheImage4.finalFilePath = file;
                                                cacheImage4.httpTask = new HttpImageTask(cacheImage4, i7);
                                                ImageLoader.this.httpTasks.add(cacheImage4.httpTask);
                                                ImageLoader.this.runHttpTasks(false);
                                                return;
                                            }
                                        }
                                        z4 = true;
                                        cacheImage4 = new CacheImage();
                                        if (str9 == null) {
                                        }
                                        cacheImage4.animatedFile = true;
                                        if (file == null) {
                                            if (!z4) {
                                                file = new File(FileLoader.getDirectory(4), str6);
                                                if (file.exists()) {
                                                    z2 = true;
                                                } else if (i6 == 2) {
                                                    directory = FileLoader.getDirectory(4);
                                                    stringBuilder = new StringBuilder();
                                                    stringBuilder.append(str6);
                                                    stringBuilder.append(".enc");
                                                    file = new File(directory, stringBuilder.toString());
                                                }
                                            } else if (tLObject2 instanceof Document) {
                                                if (tLObject2 instanceof TL_webDocument) {
                                                }
                                            }
                                        }
                                        if (i5 == 0) {
                                        }
                                        cacheImage4.selfThumb = i5 == 0;
                                        cacheImage4.key = str7;
                                        cacheImage4.filter = str8;
                                        cacheImage4.httpUrl = str9;
                                        cacheImage4.ext = str10;
                                        cacheImage4.currentAccount = i4;
                                        if (i6 == 2) {
                                            directory = FileLoader.getInternalCacheDir();
                                            stringBuilder = new StringBuilder();
                                            stringBuilder.append(str6);
                                            stringBuilder.append(".enc.key");
                                            cacheImage4.encryptionKeyPath = new File(directory, stringBuilder.toString());
                                        }
                                        if (i5 == 0) {
                                        }
                                        cacheImage4.addImageReceiver(imageReceiver2, str7, str8, i5 == 0);
                                        if (file.exists()) {
                                            cacheImage4.url = str6;
                                            cacheImage4.location = tLObject2;
                                            ImageLoader.this.imageLoadingByUrl.put(str6, cacheImage4);
                                            if (str9 != null) {
                                                MD5 = Utilities.MD5(str9);
                                                directory2 = FileLoader.getDirectory(4);
                                                stringBuilder2 = new StringBuilder();
                                                stringBuilder2.append(MD5);
                                                stringBuilder2.append("_temp.jpg");
                                                cacheImage4.tempFilePath = new File(directory2, stringBuilder2.toString());
                                                cacheImage4.finalFilePath = file;
                                                cacheImage4.httpTask = new HttpImageTask(cacheImage4, i7);
                                                ImageLoader.this.httpTasks.add(cacheImage4.httpTask);
                                                ImageLoader.this.runHttpTasks(false);
                                                return;
                                            }
                                            if (tLObject2 instanceof FileLocation) {
                                                fileLocation = (FileLocation) tLObject2;
                                                i = i6;
                                                i = 1;
                                                FileLoader.getInstance(i4).loadFile(fileLocation, str10, i7, i);
                                            } else if (tLObject2 instanceof Document) {
                                                FileLoader.getInstance(i4).loadFile((Document) tLObject2, true, i6);
                                            } else if (tLObject2 instanceof TL_webDocument) {
                                                FileLoader.getInstance(i4).loadFile((TL_webDocument) tLObject2, true, i6);
                                            }
                                            if (imageReceiver2.isForceLoding()) {
                                                ImageLoader.this.forceLoadingImages.put(cacheImage4.key, Integer.valueOf(0));
                                                return;
                                            }
                                            return;
                                        }
                                        cacheImage4.finalFilePath = file;
                                        cacheImage4.cacheTask = new CacheOutTask(cacheImage4);
                                        ImageLoader.this.imageLoadingByKeys.put(str7, cacheImage4);
                                        if (i5 == 0) {
                                            ImageLoader.this.cacheOutQueue.postRunnable(cacheImage4.cacheTask);
                                        } else {
                                            ImageLoader.this.cacheThumbOutQueue.postRunnable(cacheImage4.cacheTask);
                                        }
                                    }
                                }
                            } else if (i5 != 0) {
                                File file3;
                                if (isNeedsQualityThumb) {
                                    file2 = FileLoader.getDirectory(4);
                                    StringBuilder stringBuilder3 = new StringBuilder();
                                    stringBuilder3.append("q_");
                                    stringBuilder3.append(str6);
                                    file = new File(file2, stringBuilder3.toString());
                                    if (file.exists()) {
                                        z3 = true;
                                        if (parentMessageObject != null) {
                                            if (parentMessageObject.messageOwner.attachPath != null && parentMessageObject.messageOwner.attachPath.length() > 0) {
                                                file3 = new File(parentMessageObject.messageOwner.attachPath);
                                            }
                                            file3 = null;
                                            if (file3 == null) {
                                                file3 = FileLoader.getPathToMessage(parentMessageObject.messageOwner);
                                            }
                                            if (isNeedsQualityThumb && r0 == null) {
                                                String fileName = parentMessageObject.getFileName();
                                                ThumbGenerateInfo thumbGenerateInfo = (ThumbGenerateInfo) ImageLoader.this.waitingForQualityThumb.get(fileName);
                                                if (thumbGenerateInfo == null) {
                                                    thumbGenerateInfo = new ThumbGenerateInfo();
                                                    thumbGenerateInfo.fileLocation = (FileLocation) tLObject2;
                                                    thumbGenerateInfo.filter = str8;
                                                    ImageLoader.this.waitingForQualityThumb.put(fileName, thumbGenerateInfo);
                                                }
                                                thumbGenerateInfo.count = thumbGenerateInfo.count + 1;
                                                ImageLoader.this.waitingForQualityThumbByTag.put(tag, fileName);
                                            }
                                            if (file3.exists() && isShouldGenerateQualityThumb) {
                                                ImageLoader.this.generateThumb(parentMessageObject.getFileType(), file3, (FileLocation) tLObject2, str8);
                                            }
                                        }
                                        z2 = z3;
                                        z3 = false;
                                        if (i5 == 2) {
                                            if (tLObject2 instanceof TL_documentEncrypted) {
                                                if (tLObject2 instanceof TL_fileEncryptedLocation) {
                                                    z4 = false;
                                                    cacheImage4 = new CacheImage();
                                                    if (str9 == null) {
                                                    }
                                                    cacheImage4.animatedFile = true;
                                                    if (file == null) {
                                                        if (!z4) {
                                                            file = new File(FileLoader.getDirectory(4), str6);
                                                            if (file.exists()) {
                                                                z2 = true;
                                                            } else if (i6 == 2) {
                                                                directory = FileLoader.getDirectory(4);
                                                                stringBuilder = new StringBuilder();
                                                                stringBuilder.append(str6);
                                                                stringBuilder.append(".enc");
                                                                file = new File(directory, stringBuilder.toString());
                                                            }
                                                        } else if (tLObject2 instanceof Document) {
                                                            if (MessageObject.isVideoDocument((Document) tLObject2)) {
                                                            }
                                                        }
                                                    }
                                                    if (i5 == 0) {
                                                    }
                                                    cacheImage4.selfThumb = i5 == 0;
                                                    cacheImage4.key = str7;
                                                    cacheImage4.filter = str8;
                                                    cacheImage4.httpUrl = str9;
                                                    cacheImage4.ext = str10;
                                                    cacheImage4.currentAccount = i4;
                                                    if (i6 == 2) {
                                                        directory = FileLoader.getInternalCacheDir();
                                                        stringBuilder = new StringBuilder();
                                                        stringBuilder.append(str6);
                                                        stringBuilder.append(".enc.key");
                                                        cacheImage4.encryptionKeyPath = new File(directory, stringBuilder.toString());
                                                    }
                                                    if (i5 == 0) {
                                                    }
                                                    cacheImage4.addImageReceiver(imageReceiver2, str7, str8, i5 == 0);
                                                    if (file.exists()) {
                                                        cacheImage4.finalFilePath = file;
                                                        cacheImage4.cacheTask = new CacheOutTask(cacheImage4);
                                                        ImageLoader.this.imageLoadingByKeys.put(str7, cacheImage4);
                                                        if (i5 == 0) {
                                                            ImageLoader.this.cacheThumbOutQueue.postRunnable(cacheImage4.cacheTask);
                                                        } else {
                                                            ImageLoader.this.cacheOutQueue.postRunnable(cacheImage4.cacheTask);
                                                        }
                                                    }
                                                    cacheImage4.url = str6;
                                                    cacheImage4.location = tLObject2;
                                                    ImageLoader.this.imageLoadingByUrl.put(str6, cacheImage4);
                                                    if (str9 != null) {
                                                        if (tLObject2 instanceof FileLocation) {
                                                            fileLocation = (FileLocation) tLObject2;
                                                            i = i6;
                                                            i = 1;
                                                            FileLoader.getInstance(i4).loadFile(fileLocation, str10, i7, i);
                                                        } else if (tLObject2 instanceof Document) {
                                                            FileLoader.getInstance(i4).loadFile((Document) tLObject2, true, i6);
                                                        } else if (tLObject2 instanceof TL_webDocument) {
                                                            FileLoader.getInstance(i4).loadFile((TL_webDocument) tLObject2, true, i6);
                                                        }
                                                        if (imageReceiver2.isForceLoding()) {
                                                            ImageLoader.this.forceLoadingImages.put(cacheImage4.key, Integer.valueOf(0));
                                                            return;
                                                        }
                                                        return;
                                                    }
                                                    MD5 = Utilities.MD5(str9);
                                                    directory2 = FileLoader.getDirectory(4);
                                                    stringBuilder2 = new StringBuilder();
                                                    stringBuilder2.append(MD5);
                                                    stringBuilder2.append("_temp.jpg");
                                                    cacheImage4.tempFilePath = new File(directory2, stringBuilder2.toString());
                                                    cacheImage4.finalFilePath = file;
                                                    cacheImage4.httpTask = new HttpImageTask(cacheImage4, i7);
                                                    ImageLoader.this.httpTasks.add(cacheImage4.httpTask);
                                                    ImageLoader.this.runHttpTasks(false);
                                                    return;
                                                }
                                            }
                                            z4 = true;
                                            cacheImage4 = new CacheImage();
                                            if (str9 == null) {
                                            }
                                            cacheImage4.animatedFile = true;
                                            if (file == null) {
                                                if (!z4) {
                                                    file = new File(FileLoader.getDirectory(4), str6);
                                                    if (file.exists()) {
                                                        z2 = true;
                                                    } else if (i6 == 2) {
                                                        directory = FileLoader.getDirectory(4);
                                                        stringBuilder = new StringBuilder();
                                                        stringBuilder.append(str6);
                                                        stringBuilder.append(".enc");
                                                        file = new File(directory, stringBuilder.toString());
                                                    }
                                                } else if (tLObject2 instanceof Document) {
                                                    if (tLObject2 instanceof TL_webDocument) {
                                                    }
                                                }
                                            }
                                            if (i5 == 0) {
                                            }
                                            cacheImage4.selfThumb = i5 == 0;
                                            cacheImage4.key = str7;
                                            cacheImage4.filter = str8;
                                            cacheImage4.httpUrl = str9;
                                            cacheImage4.ext = str10;
                                            cacheImage4.currentAccount = i4;
                                            if (i6 == 2) {
                                                directory = FileLoader.getInternalCacheDir();
                                                stringBuilder = new StringBuilder();
                                                stringBuilder.append(str6);
                                                stringBuilder.append(".enc.key");
                                                cacheImage4.encryptionKeyPath = new File(directory, stringBuilder.toString());
                                            }
                                            if (i5 == 0) {
                                            }
                                            cacheImage4.addImageReceiver(imageReceiver2, str7, str8, i5 == 0);
                                            if (file.exists()) {
                                                cacheImage4.url = str6;
                                                cacheImage4.location = tLObject2;
                                                ImageLoader.this.imageLoadingByUrl.put(str6, cacheImage4);
                                                if (str9 != null) {
                                                    MD5 = Utilities.MD5(str9);
                                                    directory2 = FileLoader.getDirectory(4);
                                                    stringBuilder2 = new StringBuilder();
                                                    stringBuilder2.append(MD5);
                                                    stringBuilder2.append("_temp.jpg");
                                                    cacheImage4.tempFilePath = new File(directory2, stringBuilder2.toString());
                                                    cacheImage4.finalFilePath = file;
                                                    cacheImage4.httpTask = new HttpImageTask(cacheImage4, i7);
                                                    ImageLoader.this.httpTasks.add(cacheImage4.httpTask);
                                                    ImageLoader.this.runHttpTasks(false);
                                                    return;
                                                }
                                                if (tLObject2 instanceof FileLocation) {
                                                    fileLocation = (FileLocation) tLObject2;
                                                    i = i6;
                                                    i = 1;
                                                    FileLoader.getInstance(i4).loadFile(fileLocation, str10, i7, i);
                                                } else if (tLObject2 instanceof Document) {
                                                    FileLoader.getInstance(i4).loadFile((Document) tLObject2, true, i6);
                                                } else if (tLObject2 instanceof TL_webDocument) {
                                                    FileLoader.getInstance(i4).loadFile((TL_webDocument) tLObject2, true, i6);
                                                }
                                                if (imageReceiver2.isForceLoding()) {
                                                    ImageLoader.this.forceLoadingImages.put(cacheImage4.key, Integer.valueOf(0));
                                                    return;
                                                }
                                                return;
                                            }
                                            cacheImage4.finalFilePath = file;
                                            cacheImage4.cacheTask = new CacheOutTask(cacheImage4);
                                            ImageLoader.this.imageLoadingByKeys.put(str7, cacheImage4);
                                            if (i5 == 0) {
                                                ImageLoader.this.cacheOutQueue.postRunnable(cacheImage4.cacheTask);
                                            } else {
                                                ImageLoader.this.cacheThumbOutQueue.postRunnable(cacheImage4.cacheTask);
                                            }
                                        }
                                    }
                                }
                                z3 = false;
                                file = null;
                                if (parentMessageObject != null) {
                                    file3 = new File(parentMessageObject.messageOwner.attachPath);
                                }
                                z2 = z3;
                                z3 = false;
                                if (i5 == 2) {
                                    if (tLObject2 instanceof TL_documentEncrypted) {
                                        if (tLObject2 instanceof TL_fileEncryptedLocation) {
                                            z4 = false;
                                            cacheImage4 = new CacheImage();
                                            if (str9 == null) {
                                            }
                                            cacheImage4.animatedFile = true;
                                            if (file == null) {
                                                if (!z4) {
                                                    file = new File(FileLoader.getDirectory(4), str6);
                                                    if (file.exists()) {
                                                        z2 = true;
                                                    } else if (i6 == 2) {
                                                        directory = FileLoader.getDirectory(4);
                                                        stringBuilder = new StringBuilder();
                                                        stringBuilder.append(str6);
                                                        stringBuilder.append(".enc");
                                                        file = new File(directory, stringBuilder.toString());
                                                    }
                                                } else if (tLObject2 instanceof Document) {
                                                    if (MessageObject.isVideoDocument((Document) tLObject2)) {
                                                    }
                                                }
                                            }
                                            if (i5 == 0) {
                                            }
                                            cacheImage4.selfThumb = i5 == 0;
                                            cacheImage4.key = str7;
                                            cacheImage4.filter = str8;
                                            cacheImage4.httpUrl = str9;
                                            cacheImage4.ext = str10;
                                            cacheImage4.currentAccount = i4;
                                            if (i6 == 2) {
                                                directory = FileLoader.getInternalCacheDir();
                                                stringBuilder = new StringBuilder();
                                                stringBuilder.append(str6);
                                                stringBuilder.append(".enc.key");
                                                cacheImage4.encryptionKeyPath = new File(directory, stringBuilder.toString());
                                            }
                                            if (i5 == 0) {
                                            }
                                            cacheImage4.addImageReceiver(imageReceiver2, str7, str8, i5 == 0);
                                            if (file.exists()) {
                                                cacheImage4.finalFilePath = file;
                                                cacheImage4.cacheTask = new CacheOutTask(cacheImage4);
                                                ImageLoader.this.imageLoadingByKeys.put(str7, cacheImage4);
                                                if (i5 == 0) {
                                                    ImageLoader.this.cacheThumbOutQueue.postRunnable(cacheImage4.cacheTask);
                                                } else {
                                                    ImageLoader.this.cacheOutQueue.postRunnable(cacheImage4.cacheTask);
                                                }
                                            }
                                            cacheImage4.url = str6;
                                            cacheImage4.location = tLObject2;
                                            ImageLoader.this.imageLoadingByUrl.put(str6, cacheImage4);
                                            if (str9 != null) {
                                                if (tLObject2 instanceof FileLocation) {
                                                    fileLocation = (FileLocation) tLObject2;
                                                    i = i6;
                                                    i = 1;
                                                    FileLoader.getInstance(i4).loadFile(fileLocation, str10, i7, i);
                                                } else if (tLObject2 instanceof Document) {
                                                    FileLoader.getInstance(i4).loadFile((Document) tLObject2, true, i6);
                                                } else if (tLObject2 instanceof TL_webDocument) {
                                                    FileLoader.getInstance(i4).loadFile((TL_webDocument) tLObject2, true, i6);
                                                }
                                                if (imageReceiver2.isForceLoding()) {
                                                    ImageLoader.this.forceLoadingImages.put(cacheImage4.key, Integer.valueOf(0));
                                                    return;
                                                }
                                                return;
                                            }
                                            MD5 = Utilities.MD5(str9);
                                            directory2 = FileLoader.getDirectory(4);
                                            stringBuilder2 = new StringBuilder();
                                            stringBuilder2.append(MD5);
                                            stringBuilder2.append("_temp.jpg");
                                            cacheImage4.tempFilePath = new File(directory2, stringBuilder2.toString());
                                            cacheImage4.finalFilePath = file;
                                            cacheImage4.httpTask = new HttpImageTask(cacheImage4, i7);
                                            ImageLoader.this.httpTasks.add(cacheImage4.httpTask);
                                            ImageLoader.this.runHttpTasks(false);
                                            return;
                                        }
                                    }
                                    z4 = true;
                                    cacheImage4 = new CacheImage();
                                    if (str9 == null) {
                                    }
                                    cacheImage4.animatedFile = true;
                                    if (file == null) {
                                        if (!z4) {
                                            file = new File(FileLoader.getDirectory(4), str6);
                                            if (file.exists()) {
                                                z2 = true;
                                            } else if (i6 == 2) {
                                                directory = FileLoader.getDirectory(4);
                                                stringBuilder = new StringBuilder();
                                                stringBuilder.append(str6);
                                                stringBuilder.append(".enc");
                                                file = new File(directory, stringBuilder.toString());
                                            }
                                        } else if (tLObject2 instanceof Document) {
                                            if (tLObject2 instanceof TL_webDocument) {
                                            }
                                        }
                                    }
                                    if (i5 == 0) {
                                    }
                                    cacheImage4.selfThumb = i5 == 0;
                                    cacheImage4.key = str7;
                                    cacheImage4.filter = str8;
                                    cacheImage4.httpUrl = str9;
                                    cacheImage4.ext = str10;
                                    cacheImage4.currentAccount = i4;
                                    if (i6 == 2) {
                                        directory = FileLoader.getInternalCacheDir();
                                        stringBuilder = new StringBuilder();
                                        stringBuilder.append(str6);
                                        stringBuilder.append(".enc.key");
                                        cacheImage4.encryptionKeyPath = new File(directory, stringBuilder.toString());
                                    }
                                    if (i5 == 0) {
                                    }
                                    cacheImage4.addImageReceiver(imageReceiver2, str7, str8, i5 == 0);
                                    if (file.exists()) {
                                        cacheImage4.url = str6;
                                        cacheImage4.location = tLObject2;
                                        ImageLoader.this.imageLoadingByUrl.put(str6, cacheImage4);
                                        if (str9 != null) {
                                            MD5 = Utilities.MD5(str9);
                                            directory2 = FileLoader.getDirectory(4);
                                            stringBuilder2 = new StringBuilder();
                                            stringBuilder2.append(MD5);
                                            stringBuilder2.append("_temp.jpg");
                                            cacheImage4.tempFilePath = new File(directory2, stringBuilder2.toString());
                                            cacheImage4.finalFilePath = file;
                                            cacheImage4.httpTask = new HttpImageTask(cacheImage4, i7);
                                            ImageLoader.this.httpTasks.add(cacheImage4.httpTask);
                                            ImageLoader.this.runHttpTasks(false);
                                            return;
                                        }
                                        if (tLObject2 instanceof FileLocation) {
                                            fileLocation = (FileLocation) tLObject2;
                                            i = i6;
                                            i = 1;
                                            FileLoader.getInstance(i4).loadFile(fileLocation, str10, i7, i);
                                        } else if (tLObject2 instanceof Document) {
                                            FileLoader.getInstance(i4).loadFile((Document) tLObject2, true, i6);
                                        } else if (tLObject2 instanceof TL_webDocument) {
                                            FileLoader.getInstance(i4).loadFile((TL_webDocument) tLObject2, true, i6);
                                        }
                                        if (imageReceiver2.isForceLoding()) {
                                            ImageLoader.this.forceLoadingImages.put(cacheImage4.key, Integer.valueOf(0));
                                            return;
                                        }
                                        return;
                                    }
                                    cacheImage4.finalFilePath = file;
                                    cacheImage4.cacheTask = new CacheOutTask(cacheImage4);
                                    ImageLoader.this.imageLoadingByKeys.put(str7, cacheImage4);
                                    if (i5 == 0) {
                                        ImageLoader.this.cacheOutQueue.postRunnable(cacheImage4.cacheTask);
                                    } else {
                                        ImageLoader.this.cacheThumbOutQueue.postRunnable(cacheImage4.cacheTask);
                                    }
                                }
                            }
                            z3 = false;
                            z2 = z3;
                            file = null;
                            if (i5 == 2) {
                                if (tLObject2 instanceof TL_documentEncrypted) {
                                    if (tLObject2 instanceof TL_fileEncryptedLocation) {
                                        z4 = false;
                                        cacheImage4 = new CacheImage();
                                        if (str9 == null) {
                                        }
                                        cacheImage4.animatedFile = true;
                                        if (file == null) {
                                            if (!z4) {
                                                file = new File(FileLoader.getDirectory(4), str6);
                                                if (file.exists()) {
                                                    z2 = true;
                                                } else if (i6 == 2) {
                                                    directory = FileLoader.getDirectory(4);
                                                    stringBuilder = new StringBuilder();
                                                    stringBuilder.append(str6);
                                                    stringBuilder.append(".enc");
                                                    file = new File(directory, stringBuilder.toString());
                                                }
                                            } else if (tLObject2 instanceof Document) {
                                                if (MessageObject.isVideoDocument((Document) tLObject2)) {
                                                }
                                            }
                                        }
                                        if (i5 == 0) {
                                        }
                                        cacheImage4.selfThumb = i5 == 0;
                                        cacheImage4.key = str7;
                                        cacheImage4.filter = str8;
                                        cacheImage4.httpUrl = str9;
                                        cacheImage4.ext = str10;
                                        cacheImage4.currentAccount = i4;
                                        if (i6 == 2) {
                                            directory = FileLoader.getInternalCacheDir();
                                            stringBuilder = new StringBuilder();
                                            stringBuilder.append(str6);
                                            stringBuilder.append(".enc.key");
                                            cacheImage4.encryptionKeyPath = new File(directory, stringBuilder.toString());
                                        }
                                        if (i5 == 0) {
                                        }
                                        cacheImage4.addImageReceiver(imageReceiver2, str7, str8, i5 == 0);
                                        if (file.exists()) {
                                            cacheImage4.finalFilePath = file;
                                            cacheImage4.cacheTask = new CacheOutTask(cacheImage4);
                                            ImageLoader.this.imageLoadingByKeys.put(str7, cacheImage4);
                                            if (i5 == 0) {
                                                ImageLoader.this.cacheThumbOutQueue.postRunnable(cacheImage4.cacheTask);
                                            } else {
                                                ImageLoader.this.cacheOutQueue.postRunnable(cacheImage4.cacheTask);
                                            }
                                        }
                                        cacheImage4.url = str6;
                                        cacheImage4.location = tLObject2;
                                        ImageLoader.this.imageLoadingByUrl.put(str6, cacheImage4);
                                        if (str9 != null) {
                                            if (tLObject2 instanceof FileLocation) {
                                                fileLocation = (FileLocation) tLObject2;
                                                i = i6;
                                                i = 1;
                                                FileLoader.getInstance(i4).loadFile(fileLocation, str10, i7, i);
                                            } else if (tLObject2 instanceof Document) {
                                                FileLoader.getInstance(i4).loadFile((Document) tLObject2, true, i6);
                                            } else if (tLObject2 instanceof TL_webDocument) {
                                                FileLoader.getInstance(i4).loadFile((TL_webDocument) tLObject2, true, i6);
                                            }
                                            if (imageReceiver2.isForceLoding()) {
                                                ImageLoader.this.forceLoadingImages.put(cacheImage4.key, Integer.valueOf(0));
                                                return;
                                            }
                                            return;
                                        }
                                        MD5 = Utilities.MD5(str9);
                                        directory2 = FileLoader.getDirectory(4);
                                        stringBuilder2 = new StringBuilder();
                                        stringBuilder2.append(MD5);
                                        stringBuilder2.append("_temp.jpg");
                                        cacheImage4.tempFilePath = new File(directory2, stringBuilder2.toString());
                                        cacheImage4.finalFilePath = file;
                                        cacheImage4.httpTask = new HttpImageTask(cacheImage4, i7);
                                        ImageLoader.this.httpTasks.add(cacheImage4.httpTask);
                                        ImageLoader.this.runHttpTasks(false);
                                        return;
                                    }
                                }
                                z4 = true;
                                cacheImage4 = new CacheImage();
                                if (str9 == null) {
                                }
                                cacheImage4.animatedFile = true;
                                if (file == null) {
                                    if (!z4) {
                                        file = new File(FileLoader.getDirectory(4), str6);
                                        if (file.exists()) {
                                            z2 = true;
                                        } else if (i6 == 2) {
                                            directory = FileLoader.getDirectory(4);
                                            stringBuilder = new StringBuilder();
                                            stringBuilder.append(str6);
                                            stringBuilder.append(".enc");
                                            file = new File(directory, stringBuilder.toString());
                                        }
                                    } else if (tLObject2 instanceof Document) {
                                        if (tLObject2 instanceof TL_webDocument) {
                                        }
                                    }
                                }
                                if (i5 == 0) {
                                }
                                cacheImage4.selfThumb = i5 == 0;
                                cacheImage4.key = str7;
                                cacheImage4.filter = str8;
                                cacheImage4.httpUrl = str9;
                                cacheImage4.ext = str10;
                                cacheImage4.currentAccount = i4;
                                if (i6 == 2) {
                                    directory = FileLoader.getInternalCacheDir();
                                    stringBuilder = new StringBuilder();
                                    stringBuilder.append(str6);
                                    stringBuilder.append(".enc.key");
                                    cacheImage4.encryptionKeyPath = new File(directory, stringBuilder.toString());
                                }
                                if (i5 == 0) {
                                }
                                cacheImage4.addImageReceiver(imageReceiver2, str7, str8, i5 == 0);
                                if (file.exists()) {
                                    cacheImage4.url = str6;
                                    cacheImage4.location = tLObject2;
                                    ImageLoader.this.imageLoadingByUrl.put(str6, cacheImage4);
                                    if (str9 != null) {
                                        MD5 = Utilities.MD5(str9);
                                        directory2 = FileLoader.getDirectory(4);
                                        stringBuilder2 = new StringBuilder();
                                        stringBuilder2.append(MD5);
                                        stringBuilder2.append("_temp.jpg");
                                        cacheImage4.tempFilePath = new File(directory2, stringBuilder2.toString());
                                        cacheImage4.finalFilePath = file;
                                        cacheImage4.httpTask = new HttpImageTask(cacheImage4, i7);
                                        ImageLoader.this.httpTasks.add(cacheImage4.httpTask);
                                        ImageLoader.this.runHttpTasks(false);
                                        return;
                                    }
                                    if (tLObject2 instanceof FileLocation) {
                                        fileLocation = (FileLocation) tLObject2;
                                        i = i6;
                                        i = 1;
                                        FileLoader.getInstance(i4).loadFile(fileLocation, str10, i7, i);
                                    } else if (tLObject2 instanceof Document) {
                                        FileLoader.getInstance(i4).loadFile((Document) tLObject2, true, i6);
                                    } else if (tLObject2 instanceof TL_webDocument) {
                                        FileLoader.getInstance(i4).loadFile((TL_webDocument) tLObject2, true, i6);
                                    }
                                    if (imageReceiver2.isForceLoding()) {
                                        ImageLoader.this.forceLoadingImages.put(cacheImage4.key, Integer.valueOf(0));
                                        return;
                                    }
                                    return;
                                }
                                cacheImage4.finalFilePath = file;
                                cacheImage4.cacheTask = new CacheOutTask(cacheImage4);
                                ImageLoader.this.imageLoadingByKeys.put(str7, cacheImage4);
                                if (i5 == 0) {
                                    ImageLoader.this.cacheOutQueue.postRunnable(cacheImage4.cacheTask);
                                } else {
                                    ImageLoader.this.cacheThumbOutQueue.postRunnable(cacheImage4.cacheTask);
                                }
                            }
                        }
                    }
                };
                dispatchQueue.postRunnable(c02018);
            }
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void loadImageForImageReceiver(ImageReceiver imageReceiver) {
        ImageLoader imageLoader = this;
        ImageReceiver imageReceiver2 = imageReceiver;
        if (imageReceiver2 != null) {
            boolean z;
            String thumbKey;
            BitmapDrawable bitmapDrawable;
            TLObject thumbLocation;
            TLObject imageLocation;
            String httpImageLocation;
            String ext;
            String str;
            String MD5;
            StringBuilder stringBuilder;
            TLObject tLObject;
            String stringBuilder2;
            TL_webDocument tL_webDocument;
            String extensionByMime;
            String MD52;
            StringBuilder stringBuilder3;
            Document document;
            int lastIndexOf;
            FileLocation fileLocation;
            StringBuilder stringBuilder4;
            StringBuilder stringBuilder5;
            String filter;
            String str2;
            StringBuilder stringBuilder6;
            ImageReceiver imageReceiver3;
            String str3;
            String str4;
            boolean cacheType;
            boolean z2;
            String key = imageReceiver.getKey();
            boolean z3 = false;
            if (key != null) {
                BitmapDrawable bitmapDrawable2 = imageLoader.memCache.get(key);
                if (bitmapDrawable2 != null) {
                    cancelLoadingForImageReceiver(imageReceiver2, 0);
                    imageReceiver2.setImageBitmapByKey(bitmapDrawable2, key, false, true);
                    if (imageReceiver.isForcePreview()) {
                        z = true;
                        thumbKey = imageReceiver.getThumbKey();
                        if (thumbKey != null) {
                            bitmapDrawable = imageLoader.memCache.get(thumbKey);
                            if (bitmapDrawable != null) {
                                imageReceiver2.setImageBitmapByKey(bitmapDrawable, thumbKey, true, true);
                                cancelLoadingForImageReceiver(imageReceiver2, 1);
                                if (z || !imageReceiver.isForcePreview()) {
                                    z = true;
                                    thumbLocation = imageReceiver.getThumbLocation();
                                    imageLocation = imageReceiver.getImageLocation();
                                    httpImageLocation = imageReceiver.getHttpImageLocation();
                                    ext = imageReceiver.getExt();
                                    if (ext == null) {
                                        ext = "jpg";
                                    }
                                    str = ext;
                                    ext = null;
                                    if (httpImageLocation == null) {
                                        MD5 = Utilities.MD5(httpImageLocation);
                                        stringBuilder = new StringBuilder();
                                        stringBuilder.append(MD5);
                                        stringBuilder.append(".");
                                        stringBuilder.append(getHttpUrlExtension(httpImageLocation, "jpg"));
                                        tLObject = imageLocation;
                                        stringBuilder2 = stringBuilder.toString();
                                    } else if (imageLocation == null) {
                                        if (imageLocation instanceof FileLocation) {
                                            if (!(imageLocation instanceof TL_webDocument)) {
                                                tL_webDocument = (TL_webDocument) imageLocation;
                                                extensionByMime = FileLoader.getExtensionByMime(tL_webDocument.mime_type);
                                                MD52 = Utilities.MD5(tL_webDocument.url);
                                                stringBuilder3 = new StringBuilder();
                                                stringBuilder3.append(MD52);
                                                stringBuilder3.append(".");
                                                stringBuilder3.append(getHttpUrlExtension(tL_webDocument.url, extensionByMime));
                                                extensionByMime = MD52;
                                                MD52 = stringBuilder3.toString();
                                            } else if (imageLocation instanceof Document) {
                                                extensionByMime = null;
                                                MD52 = extensionByMime;
                                            } else {
                                                document = (Document) imageLocation;
                                                if (document.id != 0) {
                                                    if (document.dc_id != 0) {
                                                        if (document.version != 0) {
                                                            stringBuilder = new StringBuilder();
                                                            stringBuilder.append(document.dc_id);
                                                            stringBuilder.append("_");
                                                            stringBuilder.append(document.id);
                                                            extensionByMime = stringBuilder.toString();
                                                        } else {
                                                            stringBuilder = new StringBuilder();
                                                            stringBuilder.append(document.dc_id);
                                                            stringBuilder.append("_");
                                                            stringBuilder.append(document.id);
                                                            stringBuilder.append("_");
                                                            stringBuilder.append(document.version);
                                                            extensionByMime = stringBuilder.toString();
                                                        }
                                                        MD52 = FileLoader.getDocumentFileName(document);
                                                        if (MD52 != null) {
                                                            lastIndexOf = MD52.lastIndexOf(46);
                                                            if (lastIndexOf != -1) {
                                                                MD52 = MD52.substring(lastIndexOf);
                                                                if (MD52.length() <= 1) {
                                                                    MD52 = (document.mime_type == null && document.mime_type.equals(MimeTypes.VIDEO_MP4)) ? ".mp4" : TtmlNode.ANONYMOUS_REGION_ID;
                                                                }
                                                                stringBuilder3 = new StringBuilder();
                                                                stringBuilder3.append(extensionByMime);
                                                                stringBuilder3.append(MD52);
                                                                MD52 = stringBuilder3.toString();
                                                                if (!(MessageObject.isGifDocument(document) || MessageObject.isRoundVideoDocument(document))) {
                                                                }
                                                            }
                                                        }
                                                        MD52 = TtmlNode.ANONYMOUS_REGION_ID;
                                                        if (MD52.length() <= 1) {
                                                            if (document.mime_type == null) {
                                                            }
                                                        }
                                                        stringBuilder3 = new StringBuilder();
                                                        stringBuilder3.append(extensionByMime);
                                                        stringBuilder3.append(MD52);
                                                        MD52 = stringBuilder3.toString();
                                                    }
                                                }
                                                return;
                                            }
                                            if (imageLocation != thumbLocation) {
                                                tLObject = imageLocation;
                                                MD5 = extensionByMime;
                                                stringBuilder2 = MD52;
                                            } else {
                                                MD5 = null;
                                                stringBuilder2 = MD5;
                                                tLObject = stringBuilder2;
                                            }
                                        } else {
                                            fileLocation = (FileLocation) imageLocation;
                                            stringBuilder = new StringBuilder();
                                            stringBuilder.append(fileLocation.volume_id);
                                            stringBuilder.append("_");
                                            stringBuilder.append(fileLocation.local_id);
                                            extensionByMime = stringBuilder.toString();
                                            stringBuilder4 = new StringBuilder();
                                            stringBuilder4.append(extensionByMime);
                                            stringBuilder4.append(".");
                                            stringBuilder4.append(str);
                                            MD52 = stringBuilder4.toString();
                                            if (imageReceiver.getExt() == null) {
                                                if (fileLocation.key == null) {
                                                    if (fileLocation.volume_id == -2147483648L) {
                                                    }
                                                    if (imageLocation != thumbLocation) {
                                                        MD5 = null;
                                                        stringBuilder2 = MD5;
                                                        tLObject = stringBuilder2;
                                                    } else {
                                                        tLObject = imageLocation;
                                                        MD5 = extensionByMime;
                                                        stringBuilder2 = MD52;
                                                    }
                                                }
                                            }
                                        }
                                        z3 = true;
                                        if (imageLocation != thumbLocation) {
                                            MD5 = null;
                                            stringBuilder2 = MD5;
                                            tLObject = stringBuilder2;
                                        } else {
                                            tLObject = imageLocation;
                                            MD5 = extensionByMime;
                                            stringBuilder2 = MD52;
                                        }
                                    } else {
                                        tLObject = imageLocation;
                                        MD5 = null;
                                        stringBuilder2 = MD5;
                                    }
                                    if (thumbLocation == null) {
                                        stringBuilder5 = new StringBuilder();
                                        stringBuilder5.append(thumbLocation.volume_id);
                                        stringBuilder5.append("_");
                                        stringBuilder5.append(thumbLocation.local_id);
                                        ext = stringBuilder5.toString();
                                        stringBuilder5 = new StringBuilder();
                                        stringBuilder5.append(ext);
                                        stringBuilder5.append(".");
                                        stringBuilder5.append(str);
                                        thumbKey = stringBuilder5.toString();
                                    } else {
                                        thumbKey = null;
                                    }
                                    filter = imageReceiver.getFilter();
                                    extensionByMime = imageReceiver.getThumbFilter();
                                    if (!(MD5 == null || filter == null)) {
                                        stringBuilder4 = new StringBuilder();
                                        stringBuilder4.append(MD5);
                                        stringBuilder4.append("@");
                                        stringBuilder4.append(filter);
                                        MD5 = stringBuilder4.toString();
                                    }
                                    str2 = MD5;
                                    if (!(ext == null || extensionByMime == null)) {
                                        stringBuilder6 = new StringBuilder();
                                        stringBuilder6.append(ext);
                                        stringBuilder6.append("@");
                                        stringBuilder6.append(extensionByMime);
                                        ext = stringBuilder6.toString();
                                    }
                                    if (httpImageLocation == null) {
                                        imageReceiver3 = imageReceiver2;
                                        str3 = ext;
                                        ext = str;
                                        str4 = filter;
                                        createLoadOperationForImageReceiver(imageReceiver3, str3, thumbKey, ext, thumbLocation, null, extensionByMime, 0, 1, z ? true : true);
                                        createLoadOperationForImageReceiver(imageReceiver3, str2, stringBuilder2, ext, null, httpImageLocation, str4, 0, 1, 0);
                                    } else {
                                        str4 = filter;
                                        cacheType = imageReceiver.getCacheType();
                                        z2 = (cacheType || !z3) ? cacheType : true;
                                        imageReceiver3 = imageReceiver2;
                                        str3 = ext;
                                        ext = str;
                                        createLoadOperationForImageReceiver(imageReceiver3, str3, thumbKey, ext, thumbLocation, null, extensionByMime, 0, z2 ? true : z2, z ? true : true);
                                        createLoadOperationForImageReceiver(imageReceiver3, str2, stringBuilder2, ext, tLObject, null, str4, imageReceiver.getSize(), z2, 0);
                                    }
                                }
                                return;
                            }
                        }
                        z = false;
                        thumbLocation = imageReceiver.getThumbLocation();
                        imageLocation = imageReceiver.getImageLocation();
                        httpImageLocation = imageReceiver.getHttpImageLocation();
                        ext = imageReceiver.getExt();
                        if (ext == null) {
                            ext = "jpg";
                        }
                        str = ext;
                        ext = null;
                        if (httpImageLocation == null) {
                            MD5 = Utilities.MD5(httpImageLocation);
                            stringBuilder = new StringBuilder();
                            stringBuilder.append(MD5);
                            stringBuilder.append(".");
                            stringBuilder.append(getHttpUrlExtension(httpImageLocation, "jpg"));
                            tLObject = imageLocation;
                            stringBuilder2 = stringBuilder.toString();
                        } else if (imageLocation == null) {
                            tLObject = imageLocation;
                            MD5 = null;
                            stringBuilder2 = MD5;
                        } else {
                            if (imageLocation instanceof FileLocation) {
                                if (!(imageLocation instanceof TL_webDocument)) {
                                    tL_webDocument = (TL_webDocument) imageLocation;
                                    extensionByMime = FileLoader.getExtensionByMime(tL_webDocument.mime_type);
                                    MD52 = Utilities.MD5(tL_webDocument.url);
                                    stringBuilder3 = new StringBuilder();
                                    stringBuilder3.append(MD52);
                                    stringBuilder3.append(".");
                                    stringBuilder3.append(getHttpUrlExtension(tL_webDocument.url, extensionByMime));
                                    extensionByMime = MD52;
                                    MD52 = stringBuilder3.toString();
                                } else if (imageLocation instanceof Document) {
                                    extensionByMime = null;
                                    MD52 = extensionByMime;
                                } else {
                                    document = (Document) imageLocation;
                                    if (document.id != 0) {
                                        if (document.dc_id != 0) {
                                            if (document.version != 0) {
                                                stringBuilder = new StringBuilder();
                                                stringBuilder.append(document.dc_id);
                                                stringBuilder.append("_");
                                                stringBuilder.append(document.id);
                                                stringBuilder.append("_");
                                                stringBuilder.append(document.version);
                                                extensionByMime = stringBuilder.toString();
                                            } else {
                                                stringBuilder = new StringBuilder();
                                                stringBuilder.append(document.dc_id);
                                                stringBuilder.append("_");
                                                stringBuilder.append(document.id);
                                                extensionByMime = stringBuilder.toString();
                                            }
                                            MD52 = FileLoader.getDocumentFileName(document);
                                            if (MD52 != null) {
                                                lastIndexOf = MD52.lastIndexOf(46);
                                                if (lastIndexOf != -1) {
                                                    MD52 = MD52.substring(lastIndexOf);
                                                    if (MD52.length() <= 1) {
                                                        if (document.mime_type == null) {
                                                        }
                                                    }
                                                    stringBuilder3 = new StringBuilder();
                                                    stringBuilder3.append(extensionByMime);
                                                    stringBuilder3.append(MD52);
                                                    MD52 = stringBuilder3.toString();
                                                }
                                            }
                                            MD52 = TtmlNode.ANONYMOUS_REGION_ID;
                                            if (MD52.length() <= 1) {
                                                if (document.mime_type == null) {
                                                }
                                            }
                                            stringBuilder3 = new StringBuilder();
                                            stringBuilder3.append(extensionByMime);
                                            stringBuilder3.append(MD52);
                                            MD52 = stringBuilder3.toString();
                                        }
                                    }
                                    return;
                                }
                                if (imageLocation != thumbLocation) {
                                    tLObject = imageLocation;
                                    MD5 = extensionByMime;
                                    stringBuilder2 = MD52;
                                } else {
                                    MD5 = null;
                                    stringBuilder2 = MD5;
                                    tLObject = stringBuilder2;
                                }
                            } else {
                                fileLocation = (FileLocation) imageLocation;
                                stringBuilder = new StringBuilder();
                                stringBuilder.append(fileLocation.volume_id);
                                stringBuilder.append("_");
                                stringBuilder.append(fileLocation.local_id);
                                extensionByMime = stringBuilder.toString();
                                stringBuilder4 = new StringBuilder();
                                stringBuilder4.append(extensionByMime);
                                stringBuilder4.append(".");
                                stringBuilder4.append(str);
                                MD52 = stringBuilder4.toString();
                                if (imageReceiver.getExt() == null) {
                                    if (fileLocation.key == null) {
                                        if (fileLocation.volume_id == -2147483648L) {
                                        }
                                        if (imageLocation != thumbLocation) {
                                            MD5 = null;
                                            stringBuilder2 = MD5;
                                            tLObject = stringBuilder2;
                                        } else {
                                            tLObject = imageLocation;
                                            MD5 = extensionByMime;
                                            stringBuilder2 = MD52;
                                        }
                                    }
                                }
                            }
                            z3 = true;
                            if (imageLocation != thumbLocation) {
                                tLObject = imageLocation;
                                MD5 = extensionByMime;
                                stringBuilder2 = MD52;
                            } else {
                                MD5 = null;
                                stringBuilder2 = MD5;
                                tLObject = stringBuilder2;
                            }
                        }
                        if (thumbLocation == null) {
                            thumbKey = null;
                        } else {
                            stringBuilder5 = new StringBuilder();
                            stringBuilder5.append(thumbLocation.volume_id);
                            stringBuilder5.append("_");
                            stringBuilder5.append(thumbLocation.local_id);
                            ext = stringBuilder5.toString();
                            stringBuilder5 = new StringBuilder();
                            stringBuilder5.append(ext);
                            stringBuilder5.append(".");
                            stringBuilder5.append(str);
                            thumbKey = stringBuilder5.toString();
                        }
                        filter = imageReceiver.getFilter();
                        extensionByMime = imageReceiver.getThumbFilter();
                        stringBuilder4 = new StringBuilder();
                        stringBuilder4.append(MD5);
                        stringBuilder4.append("@");
                        stringBuilder4.append(filter);
                        MD5 = stringBuilder4.toString();
                        str2 = MD5;
                        stringBuilder6 = new StringBuilder();
                        stringBuilder6.append(ext);
                        stringBuilder6.append("@");
                        stringBuilder6.append(extensionByMime);
                        ext = stringBuilder6.toString();
                        if (httpImageLocation == null) {
                            str4 = filter;
                            cacheType = imageReceiver.getCacheType();
                            if (!cacheType) {
                            }
                            if (z2) {
                            }
                            if (z) {
                            }
                            imageReceiver3 = imageReceiver2;
                            str3 = ext;
                            ext = str;
                            createLoadOperationForImageReceiver(imageReceiver3, str3, thumbKey, ext, thumbLocation, null, extensionByMime, 0, z2 ? true : z2, z ? true : true);
                            createLoadOperationForImageReceiver(imageReceiver3, str2, stringBuilder2, ext, tLObject, null, str4, imageReceiver.getSize(), z2, 0);
                        } else {
                            if (z) {
                            }
                            imageReceiver3 = imageReceiver2;
                            str3 = ext;
                            ext = str;
                            str4 = filter;
                            createLoadOperationForImageReceiver(imageReceiver3, str3, thumbKey, ext, thumbLocation, null, extensionByMime, 0, 1, z ? true : true);
                            createLoadOperationForImageReceiver(imageReceiver3, str2, stringBuilder2, ext, null, httpImageLocation, str4, 0, 1, 0);
                        }
                    }
                    return;
                }
            }
            z = false;
            thumbKey = imageReceiver.getThumbKey();
            if (thumbKey != null) {
                bitmapDrawable = imageLoader.memCache.get(thumbKey);
                if (bitmapDrawable != null) {
                    imageReceiver2.setImageBitmapByKey(bitmapDrawable, thumbKey, true, true);
                    cancelLoadingForImageReceiver(imageReceiver2, 1);
                    if (z) {
                    }
                    z = true;
                    thumbLocation = imageReceiver.getThumbLocation();
                    imageLocation = imageReceiver.getImageLocation();
                    httpImageLocation = imageReceiver.getHttpImageLocation();
                    ext = imageReceiver.getExt();
                    if (ext == null) {
                        ext = "jpg";
                    }
                    str = ext;
                    ext = null;
                    if (httpImageLocation == null) {
                        MD5 = Utilities.MD5(httpImageLocation);
                        stringBuilder = new StringBuilder();
                        stringBuilder.append(MD5);
                        stringBuilder.append(".");
                        stringBuilder.append(getHttpUrlExtension(httpImageLocation, "jpg"));
                        tLObject = imageLocation;
                        stringBuilder2 = stringBuilder.toString();
                    } else if (imageLocation == null) {
                        if (imageLocation instanceof FileLocation) {
                            fileLocation = (FileLocation) imageLocation;
                            stringBuilder = new StringBuilder();
                            stringBuilder.append(fileLocation.volume_id);
                            stringBuilder.append("_");
                            stringBuilder.append(fileLocation.local_id);
                            extensionByMime = stringBuilder.toString();
                            stringBuilder4 = new StringBuilder();
                            stringBuilder4.append(extensionByMime);
                            stringBuilder4.append(".");
                            stringBuilder4.append(str);
                            MD52 = stringBuilder4.toString();
                            if (imageReceiver.getExt() == null) {
                                if (fileLocation.key == null) {
                                    if (fileLocation.volume_id == -2147483648L) {
                                    }
                                    if (imageLocation != thumbLocation) {
                                        MD5 = null;
                                        stringBuilder2 = MD5;
                                        tLObject = stringBuilder2;
                                    } else {
                                        tLObject = imageLocation;
                                        MD5 = extensionByMime;
                                        stringBuilder2 = MD52;
                                    }
                                }
                            }
                        } else {
                            if (!(imageLocation instanceof TL_webDocument)) {
                                tL_webDocument = (TL_webDocument) imageLocation;
                                extensionByMime = FileLoader.getExtensionByMime(tL_webDocument.mime_type);
                                MD52 = Utilities.MD5(tL_webDocument.url);
                                stringBuilder3 = new StringBuilder();
                                stringBuilder3.append(MD52);
                                stringBuilder3.append(".");
                                stringBuilder3.append(getHttpUrlExtension(tL_webDocument.url, extensionByMime));
                                extensionByMime = MD52;
                                MD52 = stringBuilder3.toString();
                            } else if (imageLocation instanceof Document) {
                                document = (Document) imageLocation;
                                if (document.id != 0) {
                                    if (document.dc_id != 0) {
                                        if (document.version != 0) {
                                            stringBuilder = new StringBuilder();
                                            stringBuilder.append(document.dc_id);
                                            stringBuilder.append("_");
                                            stringBuilder.append(document.id);
                                            extensionByMime = stringBuilder.toString();
                                        } else {
                                            stringBuilder = new StringBuilder();
                                            stringBuilder.append(document.dc_id);
                                            stringBuilder.append("_");
                                            stringBuilder.append(document.id);
                                            stringBuilder.append("_");
                                            stringBuilder.append(document.version);
                                            extensionByMime = stringBuilder.toString();
                                        }
                                        MD52 = FileLoader.getDocumentFileName(document);
                                        if (MD52 != null) {
                                            lastIndexOf = MD52.lastIndexOf(46);
                                            if (lastIndexOf != -1) {
                                                MD52 = MD52.substring(lastIndexOf);
                                                if (MD52.length() <= 1) {
                                                    if (document.mime_type == null) {
                                                    }
                                                }
                                                stringBuilder3 = new StringBuilder();
                                                stringBuilder3.append(extensionByMime);
                                                stringBuilder3.append(MD52);
                                                MD52 = stringBuilder3.toString();
                                            }
                                        }
                                        MD52 = TtmlNode.ANONYMOUS_REGION_ID;
                                        if (MD52.length() <= 1) {
                                            if (document.mime_type == null) {
                                            }
                                        }
                                        stringBuilder3 = new StringBuilder();
                                        stringBuilder3.append(extensionByMime);
                                        stringBuilder3.append(MD52);
                                        MD52 = stringBuilder3.toString();
                                    }
                                }
                                return;
                            } else {
                                extensionByMime = null;
                                MD52 = extensionByMime;
                            }
                            if (imageLocation != thumbLocation) {
                                tLObject = imageLocation;
                                MD5 = extensionByMime;
                                stringBuilder2 = MD52;
                            } else {
                                MD5 = null;
                                stringBuilder2 = MD5;
                                tLObject = stringBuilder2;
                            }
                        }
                        z3 = true;
                        if (imageLocation != thumbLocation) {
                            MD5 = null;
                            stringBuilder2 = MD5;
                            tLObject = stringBuilder2;
                        } else {
                            tLObject = imageLocation;
                            MD5 = extensionByMime;
                            stringBuilder2 = MD52;
                        }
                    } else {
                        tLObject = imageLocation;
                        MD5 = null;
                        stringBuilder2 = MD5;
                    }
                    if (thumbLocation == null) {
                        stringBuilder5 = new StringBuilder();
                        stringBuilder5.append(thumbLocation.volume_id);
                        stringBuilder5.append("_");
                        stringBuilder5.append(thumbLocation.local_id);
                        ext = stringBuilder5.toString();
                        stringBuilder5 = new StringBuilder();
                        stringBuilder5.append(ext);
                        stringBuilder5.append(".");
                        stringBuilder5.append(str);
                        thumbKey = stringBuilder5.toString();
                    } else {
                        thumbKey = null;
                    }
                    filter = imageReceiver.getFilter();
                    extensionByMime = imageReceiver.getThumbFilter();
                    stringBuilder4 = new StringBuilder();
                    stringBuilder4.append(MD5);
                    stringBuilder4.append("@");
                    stringBuilder4.append(filter);
                    MD5 = stringBuilder4.toString();
                    str2 = MD5;
                    stringBuilder6 = new StringBuilder();
                    stringBuilder6.append(ext);
                    stringBuilder6.append("@");
                    stringBuilder6.append(extensionByMime);
                    ext = stringBuilder6.toString();
                    if (httpImageLocation == null) {
                        if (z) {
                        }
                        imageReceiver3 = imageReceiver2;
                        str3 = ext;
                        ext = str;
                        str4 = filter;
                        createLoadOperationForImageReceiver(imageReceiver3, str3, thumbKey, ext, thumbLocation, null, extensionByMime, 0, 1, z ? true : true);
                        createLoadOperationForImageReceiver(imageReceiver3, str2, stringBuilder2, ext, null, httpImageLocation, str4, 0, 1, 0);
                    } else {
                        str4 = filter;
                        cacheType = imageReceiver.getCacheType();
                        if (cacheType) {
                        }
                        if (z2) {
                        }
                        if (z) {
                        }
                        imageReceiver3 = imageReceiver2;
                        str3 = ext;
                        ext = str;
                        createLoadOperationForImageReceiver(imageReceiver3, str3, thumbKey, ext, thumbLocation, null, extensionByMime, 0, z2 ? true : z2, z ? true : true);
                        createLoadOperationForImageReceiver(imageReceiver3, str2, stringBuilder2, ext, tLObject, null, str4, imageReceiver.getSize(), z2, 0);
                    }
                }
            }
            z = false;
            thumbLocation = imageReceiver.getThumbLocation();
            imageLocation = imageReceiver.getImageLocation();
            httpImageLocation = imageReceiver.getHttpImageLocation();
            ext = imageReceiver.getExt();
            if (ext == null) {
                ext = "jpg";
            }
            str = ext;
            ext = null;
            if (httpImageLocation == null) {
                MD5 = Utilities.MD5(httpImageLocation);
                stringBuilder = new StringBuilder();
                stringBuilder.append(MD5);
                stringBuilder.append(".");
                stringBuilder.append(getHttpUrlExtension(httpImageLocation, "jpg"));
                tLObject = imageLocation;
                stringBuilder2 = stringBuilder.toString();
            } else if (imageLocation == null) {
                tLObject = imageLocation;
                MD5 = null;
                stringBuilder2 = MD5;
            } else {
                if (imageLocation instanceof FileLocation) {
                    if (!(imageLocation instanceof TL_webDocument)) {
                        tL_webDocument = (TL_webDocument) imageLocation;
                        extensionByMime = FileLoader.getExtensionByMime(tL_webDocument.mime_type);
                        MD52 = Utilities.MD5(tL_webDocument.url);
                        stringBuilder3 = new StringBuilder();
                        stringBuilder3.append(MD52);
                        stringBuilder3.append(".");
                        stringBuilder3.append(getHttpUrlExtension(tL_webDocument.url, extensionByMime));
                        extensionByMime = MD52;
                        MD52 = stringBuilder3.toString();
                    } else if (imageLocation instanceof Document) {
                        extensionByMime = null;
                        MD52 = extensionByMime;
                    } else {
                        document = (Document) imageLocation;
                        if (document.id != 0) {
                            if (document.dc_id != 0) {
                                if (document.version != 0) {
                                    stringBuilder = new StringBuilder();
                                    stringBuilder.append(document.dc_id);
                                    stringBuilder.append("_");
                                    stringBuilder.append(document.id);
                                    stringBuilder.append("_");
                                    stringBuilder.append(document.version);
                                    extensionByMime = stringBuilder.toString();
                                } else {
                                    stringBuilder = new StringBuilder();
                                    stringBuilder.append(document.dc_id);
                                    stringBuilder.append("_");
                                    stringBuilder.append(document.id);
                                    extensionByMime = stringBuilder.toString();
                                }
                                MD52 = FileLoader.getDocumentFileName(document);
                                if (MD52 != null) {
                                    lastIndexOf = MD52.lastIndexOf(46);
                                    if (lastIndexOf != -1) {
                                        MD52 = MD52.substring(lastIndexOf);
                                        if (MD52.length() <= 1) {
                                            if (document.mime_type == null) {
                                            }
                                        }
                                        stringBuilder3 = new StringBuilder();
                                        stringBuilder3.append(extensionByMime);
                                        stringBuilder3.append(MD52);
                                        MD52 = stringBuilder3.toString();
                                    }
                                }
                                MD52 = TtmlNode.ANONYMOUS_REGION_ID;
                                if (MD52.length() <= 1) {
                                    if (document.mime_type == null) {
                                    }
                                }
                                stringBuilder3 = new StringBuilder();
                                stringBuilder3.append(extensionByMime);
                                stringBuilder3.append(MD52);
                                MD52 = stringBuilder3.toString();
                            }
                        }
                        return;
                    }
                    if (imageLocation != thumbLocation) {
                        tLObject = imageLocation;
                        MD5 = extensionByMime;
                        stringBuilder2 = MD52;
                    } else {
                        MD5 = null;
                        stringBuilder2 = MD5;
                        tLObject = stringBuilder2;
                    }
                } else {
                    fileLocation = (FileLocation) imageLocation;
                    stringBuilder = new StringBuilder();
                    stringBuilder.append(fileLocation.volume_id);
                    stringBuilder.append("_");
                    stringBuilder.append(fileLocation.local_id);
                    extensionByMime = stringBuilder.toString();
                    stringBuilder4 = new StringBuilder();
                    stringBuilder4.append(extensionByMime);
                    stringBuilder4.append(".");
                    stringBuilder4.append(str);
                    MD52 = stringBuilder4.toString();
                    if (imageReceiver.getExt() == null) {
                        if (fileLocation.key == null) {
                            if (fileLocation.volume_id == -2147483648L) {
                            }
                            if (imageLocation != thumbLocation) {
                                MD5 = null;
                                stringBuilder2 = MD5;
                                tLObject = stringBuilder2;
                            } else {
                                tLObject = imageLocation;
                                MD5 = extensionByMime;
                                stringBuilder2 = MD52;
                            }
                        }
                    }
                }
                z3 = true;
                if (imageLocation != thumbLocation) {
                    tLObject = imageLocation;
                    MD5 = extensionByMime;
                    stringBuilder2 = MD52;
                } else {
                    MD5 = null;
                    stringBuilder2 = MD5;
                    tLObject = stringBuilder2;
                }
            }
            if (thumbLocation == null) {
                thumbKey = null;
            } else {
                stringBuilder5 = new StringBuilder();
                stringBuilder5.append(thumbLocation.volume_id);
                stringBuilder5.append("_");
                stringBuilder5.append(thumbLocation.local_id);
                ext = stringBuilder5.toString();
                stringBuilder5 = new StringBuilder();
                stringBuilder5.append(ext);
                stringBuilder5.append(".");
                stringBuilder5.append(str);
                thumbKey = stringBuilder5.toString();
            }
            filter = imageReceiver.getFilter();
            extensionByMime = imageReceiver.getThumbFilter();
            stringBuilder4 = new StringBuilder();
            stringBuilder4.append(MD5);
            stringBuilder4.append("@");
            stringBuilder4.append(filter);
            MD5 = stringBuilder4.toString();
            str2 = MD5;
            stringBuilder6 = new StringBuilder();
            stringBuilder6.append(ext);
            stringBuilder6.append("@");
            stringBuilder6.append(extensionByMime);
            ext = stringBuilder6.toString();
            if (httpImageLocation == null) {
                str4 = filter;
                cacheType = imageReceiver.getCacheType();
                if (cacheType) {
                }
                if (z2) {
                }
                if (z) {
                }
                imageReceiver3 = imageReceiver2;
                str3 = ext;
                ext = str;
                createLoadOperationForImageReceiver(imageReceiver3, str3, thumbKey, ext, thumbLocation, null, extensionByMime, 0, z2 ? true : z2, z ? true : true);
                createLoadOperationForImageReceiver(imageReceiver3, str2, stringBuilder2, ext, tLObject, null, str4, imageReceiver.getSize(), z2, 0);
            } else {
                if (z) {
                }
                imageReceiver3 = imageReceiver2;
                str3 = ext;
                ext = str;
                str4 = filter;
                createLoadOperationForImageReceiver(imageReceiver3, str3, thumbKey, ext, thumbLocation, null, extensionByMime, 0, 1, z ? true : true);
                createLoadOperationForImageReceiver(imageReceiver3, str2, stringBuilder2, ext, null, httpImageLocation, str4, 0, 1, 0);
            }
        }
    }

    private void httpFileLoadError(final String str) {
        this.imageLoadQueue.postRunnable(new Runnable() {
            public void run() {
                CacheImage cacheImage = (CacheImage) ImageLoader.this.imageLoadingByUrl.get(str);
                if (cacheImage != null) {
                    HttpImageTask httpImageTask = cacheImage.httpTask;
                    cacheImage.httpTask = new HttpImageTask(httpImageTask.cacheImage, httpImageTask.imageSize);
                    ImageLoader.this.httpTasks.add(cacheImage.httpTask);
                    ImageLoader.this.runHttpTasks(false);
                }
            }
        });
    }

    private void fileDidLoaded(final String str, final File file, final int i) {
        this.imageLoadQueue.postRunnable(new Runnable() {
            public void run() {
                ThumbGenerateInfo thumbGenerateInfo = (ThumbGenerateInfo) ImageLoader.this.waitingForQualityThumb.get(str);
                if (thumbGenerateInfo != null) {
                    ImageLoader.this.generateThumb(i, file, thumbGenerateInfo.fileLocation, thumbGenerateInfo.filter);
                    ImageLoader.this.waitingForQualityThumb.remove(str);
                }
                CacheImage cacheImage = (CacheImage) ImageLoader.this.imageLoadingByUrl.get(str);
                if (cacheImage != null) {
                    ImageLoader.this.imageLoadingByUrl.remove(str);
                    ArrayList arrayList = new ArrayList();
                    int i = 0;
                    for (int i2 = 0; i2 < cacheImage.imageReceiverArray.size(); i2++) {
                        String str = (String) cacheImage.keys.get(i2);
                        String str2 = (String) cacheImage.filters.get(i2);
                        Boolean bool = (Boolean) cacheImage.thumbs.get(i2);
                        ImageReceiver imageReceiver = (ImageReceiver) cacheImage.imageReceiverArray.get(i2);
                        CacheImage cacheImage2 = (CacheImage) ImageLoader.this.imageLoadingByKeys.get(str);
                        if (cacheImage2 == null) {
                            cacheImage2 = new CacheImage();
                            cacheImage2.currentAccount = cacheImage.currentAccount;
                            cacheImage2.finalFilePath = file;
                            cacheImage2.key = str;
                            cacheImage2.httpUrl = cacheImage.httpUrl;
                            cacheImage2.selfThumb = bool.booleanValue();
                            cacheImage2.ext = cacheImage.ext;
                            cacheImage2.encryptionKeyPath = cacheImage.encryptionKeyPath;
                            cacheImage2.cacheTask = new CacheOutTask(cacheImage2);
                            cacheImage2.filter = str2;
                            cacheImage2.animatedFile = cacheImage.animatedFile;
                            ImageLoader.this.imageLoadingByKeys.put(str, cacheImage2);
                            arrayList.add(cacheImage2.cacheTask);
                        }
                        cacheImage2.addImageReceiver(imageReceiver, str, str2, bool.booleanValue());
                    }
                    while (i < arrayList.size()) {
                        CacheOutTask cacheOutTask = (CacheOutTask) arrayList.get(i);
                        if (cacheOutTask.cacheImage.selfThumb) {
                            ImageLoader.this.cacheThumbOutQueue.postRunnable(cacheOutTask);
                        } else {
                            ImageLoader.this.cacheOutQueue.postRunnable(cacheOutTask);
                        }
                        i++;
                    }
                }
            }
        });
    }

    private void fileDidFailedLoad(final String str, int i) {
        if (i != 1) {
            this.imageLoadQueue.postRunnable(new Runnable() {
                public void run() {
                    CacheImage cacheImage = (CacheImage) ImageLoader.this.imageLoadingByUrl.get(str);
                    if (cacheImage != null) {
                        cacheImage.setImageAndClear(null);
                    }
                }
            });
        }
    }

    private void runHttpTasks(boolean z) {
        if (z) {
            this.currentHttpTasksCount -= true;
        }
        while (this.currentHttpTasksCount < true && !this.httpTasks.isEmpty()) {
            ((HttpImageTask) this.httpTasks.poll()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
            this.currentHttpTasksCount += true;
        }
    }

    public boolean isLoadingHttpFile(String str) {
        return this.httpFileLoadTasksByKeys.containsKey(str);
    }

    public void loadHttpFile(String str, String str2, int i) {
        if (!(str == null || str.length() == 0)) {
            if (!this.httpFileLoadTasksByKeys.containsKey(str)) {
                String httpUrlExtension = getHttpUrlExtension(str, str2);
                str2 = FileLoader.getDirectory(4);
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(Utilities.MD5(str));
                stringBuilder.append("_temp.");
                stringBuilder.append(httpUrlExtension);
                File file = new File(str2, stringBuilder.toString());
                file.delete();
                String httpFileTask = new HttpFileTask(str, file, httpUrlExtension, i);
                this.httpFileLoadTasks.add(httpFileTask);
                this.httpFileLoadTasksByKeys.put(str, httpFileTask);
                runHttpFileLoadTasks(null, null);
            }
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

    private void runHttpFileLoadTasks(final HttpFileTask httpFileTask, final int i) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                if (httpFileTask != null) {
                    ImageLoader.this.currentHttpFileLoadTasksCount = ImageLoader.this.currentHttpFileLoadTasksCount - 1;
                }
                if (httpFileTask != null) {
                    if (i == 1) {
                        if (httpFileTask.canRetry) {
                            final HttpFileTask httpFileTask = new HttpFileTask(httpFileTask.url, httpFileTask.tempFile, httpFileTask.ext, httpFileTask.currentAccount);
                            Runnable c01851 = new Runnable() {
                                public void run() {
                                    ImageLoader.this.httpFileLoadTasks.add(httpFileTask);
                                    ImageLoader.this.runHttpFileLoadTasks(null, 0);
                                }
                            };
                            ImageLoader.this.retryHttpsTasks.put(httpFileTask.url, c01851);
                            AndroidUtilities.runOnUIThread(c01851, 1000);
                        } else {
                            ImageLoader.this.httpFileLoadTasksByKeys.remove(httpFileTask.url);
                            NotificationCenter.getInstance(httpFileTask.currentAccount).postNotificationName(NotificationCenter.httpFileDidFailedLoad, httpFileTask.url, Integer.valueOf(0));
                        }
                    } else if (i == 2) {
                        ImageLoader.this.httpFileLoadTasksByKeys.remove(httpFileTask.url);
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
                        NotificationCenter.getInstance(httpFileTask.currentAccount).postNotificationName(NotificationCenter.httpFileDidLoaded, httpFileTask.url, file2);
                    }
                }
                while (ImageLoader.this.currentHttpFileLoadTasksCount < 2 && !ImageLoader.this.httpFileLoadTasks.isEmpty()) {
                    ((HttpFileTask) ImageLoader.this.httpFileLoadTasks.poll()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
                    ImageLoader.this.currentHttpFileLoadTasksCount = ImageLoader.this.currentHttpFileLoadTasksCount + 1;
                }
            }
        });
    }

    public static android.graphics.Bitmap loadBitmap(java.lang.String r16, android.net.Uri r17, float r18, float r19, boolean r20) {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:60)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
        /*
        r1 = r17;
        r4 = new android.graphics.BitmapFactory$Options;
        r4.<init>();
        r5 = 1;
        r4.inJustDecodeBounds = r5;
        if (r16 != 0) goto L_0x002f;
    L_0x000c:
        if (r1 == 0) goto L_0x002f;
    L_0x000e:
        r7 = r17.getScheme();
        if (r7 == 0) goto L_0x002f;
    L_0x0014:
        r7 = r17.getScheme();
        r8 = "file";
        r7 = r7.contains(r8);
        if (r7 == 0) goto L_0x0025;
    L_0x0020:
        r6 = r17.getPath();
        goto L_0x0031;
    L_0x0025:
        r7 = org.telegram.messenger.AndroidUtilities.getPath(r17);	 Catch:{ Throwable -> 0x002b }
        r6 = r7;
        goto L_0x0031;
    L_0x002b:
        r0 = move-exception;
        org.telegram.messenger.FileLog.m3e(r0);
    L_0x002f:
        r6 = r16;
    L_0x0031:
        r7 = 0;
        if (r6 == 0) goto L_0x0038;
    L_0x0034:
        android.graphics.BitmapFactory.decodeFile(r6, r4);
        goto L_0x005b;
    L_0x0038:
        if (r1 == 0) goto L_0x005b;
    L_0x003a:
        r8 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Throwable -> 0x0055 }
        r8 = r8.getContentResolver();	 Catch:{ Throwable -> 0x0055 }
        r8 = r8.openInputStream(r1);	 Catch:{ Throwable -> 0x0055 }
        android.graphics.BitmapFactory.decodeStream(r8, r7, r4);	 Catch:{ Throwable -> 0x0055 }
        r8.close();	 Catch:{ Throwable -> 0x0055 }
        r8 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Throwable -> 0x0055 }
        r8 = r8.getContentResolver();	 Catch:{ Throwable -> 0x0055 }
        r8 = r8.openInputStream(r1);	 Catch:{ Throwable -> 0x0055 }
        goto L_0x005c;
    L_0x0055:
        r0 = move-exception;
        r1 = r0;
        org.telegram.messenger.FileLog.m3e(r1);
        return r7;
    L_0x005b:
        r8 = r7;
    L_0x005c:
        r9 = r4.outWidth;
        r9 = (float) r9;
        r10 = r4.outHeight;
        r10 = (float) r10;
        if (r20 == 0) goto L_0x006d;
    L_0x0064:
        r9 = r9 / r18;
        r10 = r10 / r19;
        r2 = java.lang.Math.max(r9, r10);
        goto L_0x0075;
    L_0x006d:
        r9 = r9 / r18;
        r10 = r10 / r19;
        r2 = java.lang.Math.min(r9, r10);
    L_0x0075:
        r3 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r9 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1));
        if (r9 >= 0) goto L_0x007c;
    L_0x007b:
        r2 = r3;
    L_0x007c:
        r3 = 0;
        r4.inJustDecodeBounds = r3;
        r2 = (int) r2;
        r4.inSampleSize = r2;
        r2 = r4.inSampleSize;
        r2 = r2 % 2;
        if (r2 == 0) goto L_0x0093;
    L_0x0088:
        r2 = r5;
    L_0x0089:
        r9 = r2 * 2;
        r10 = r4.inSampleSize;
        if (r9 >= r10) goto L_0x0091;
    L_0x008f:
        r2 = r9;
        goto L_0x0089;
    L_0x0091:
        r4.inSampleSize = r2;
    L_0x0093:
        r2 = android.os.Build.VERSION.SDK_INT;
        r9 = 21;
        if (r2 >= r9) goto L_0x009a;
    L_0x0099:
        r3 = r5;
    L_0x009a:
        r4.inPurgeable = r3;
        if (r6 == 0) goto L_0x00a0;
    L_0x009e:
        r2 = r6;
        goto L_0x00a8;
    L_0x00a0:
        if (r1 == 0) goto L_0x00a7;
    L_0x00a2:
        r2 = org.telegram.messenger.AndroidUtilities.getPath(r17);
        goto L_0x00a8;
    L_0x00a7:
        r2 = r7;
    L_0x00a8:
        if (r2 == 0) goto L_0x00d7;
    L_0x00aa:
        r3 = new android.support.media.ExifInterface;	 Catch:{ Throwable -> 0x00d7 }
        r3.<init>(r2);	 Catch:{ Throwable -> 0x00d7 }
        r2 = "Orientation";	 Catch:{ Throwable -> 0x00d7 }
        r2 = r3.getAttributeInt(r2, r5);	 Catch:{ Throwable -> 0x00d7 }
        r3 = new android.graphics.Matrix;	 Catch:{ Throwable -> 0x00d7 }
        r3.<init>();	 Catch:{ Throwable -> 0x00d7 }
        r5 = 3;
        if (r2 == r5) goto L_0x00d1;
    L_0x00bd:
        r5 = 6;
        if (r2 == r5) goto L_0x00cb;
    L_0x00c0:
        r5 = 8;
        if (r2 == r5) goto L_0x00c5;
    L_0x00c4:
        goto L_0x00d8;
    L_0x00c5:
        r2 = NUM; // 0x43870000 float:270.0 double:5.597372625E-315;
        r3.postRotate(r2);	 Catch:{ Throwable -> 0x00d8 }
        goto L_0x00d8;	 Catch:{ Throwable -> 0x00d8 }
    L_0x00cb:
        r2 = NUM; // 0x42b40000 float:90.0 double:5.529052754E-315;	 Catch:{ Throwable -> 0x00d8 }
        r3.postRotate(r2);	 Catch:{ Throwable -> 0x00d8 }
        goto L_0x00d8;	 Catch:{ Throwable -> 0x00d8 }
    L_0x00d1:
        r2 = NUM; // 0x43340000 float:180.0 double:5.570497984E-315;	 Catch:{ Throwable -> 0x00d8 }
        r3.postRotate(r2);	 Catch:{ Throwable -> 0x00d8 }
        goto L_0x00d8;
    L_0x00d7:
        r3 = r7;
    L_0x00d8:
        if (r6 == 0) goto L_0x0147;
    L_0x00da:
        r1 = android.graphics.BitmapFactory.decodeFile(r6, r4);	 Catch:{ Throwable -> 0x0106 }
        if (r1 == 0) goto L_0x0103;
    L_0x00e0:
        r2 = r4.inPurgeable;	 Catch:{ Throwable -> 0x0100 }
        if (r2 == 0) goto L_0x00e7;	 Catch:{ Throwable -> 0x0100 }
    L_0x00e4:
        org.telegram.messenger.Utilities.pinBitmap(r1);	 Catch:{ Throwable -> 0x0100 }
    L_0x00e7:
        r10 = 0;	 Catch:{ Throwable -> 0x0100 }
        r11 = 0;	 Catch:{ Throwable -> 0x0100 }
        r12 = r1.getWidth();	 Catch:{ Throwable -> 0x0100 }
        r13 = r1.getHeight();	 Catch:{ Throwable -> 0x0100 }
        r15 = 1;	 Catch:{ Throwable -> 0x0100 }
        r9 = r1;	 Catch:{ Throwable -> 0x0100 }
        r14 = r3;	 Catch:{ Throwable -> 0x0100 }
        r2 = org.telegram.messenger.Bitmaps.createBitmap(r9, r10, r11, r12, r13, r14, r15);	 Catch:{ Throwable -> 0x0100 }
        if (r2 == r1) goto L_0x0103;	 Catch:{ Throwable -> 0x0100 }
    L_0x00fa:
        r1.recycle();	 Catch:{ Throwable -> 0x0100 }
        r7 = r2;
        goto L_0x0190;
    L_0x0100:
        r0 = move-exception;
        r7 = r1;
        goto L_0x0107;
    L_0x0103:
        r7 = r1;
        goto L_0x0190;
    L_0x0106:
        r0 = move-exception;
    L_0x0107:
        r1 = r0;
        org.telegram.messenger.FileLog.m3e(r1);
        r1 = getInstance();
        r1.clearMemory();
        if (r7 != 0) goto L_0x0125;
    L_0x0114:
        r1 = android.graphics.BitmapFactory.decodeFile(r6, r4);	 Catch:{ Throwable -> 0x0122 }
        if (r1 == 0) goto L_0x0126;
    L_0x011a:
        r2 = r4.inPurgeable;	 Catch:{ Throwable -> 0x0140 }
        if (r2 == 0) goto L_0x0126;	 Catch:{ Throwable -> 0x0140 }
    L_0x011e:
        org.telegram.messenger.Utilities.pinBitmap(r1);	 Catch:{ Throwable -> 0x0140 }
        goto L_0x0126;	 Catch:{ Throwable -> 0x0140 }
    L_0x0122:
        r0 = move-exception;	 Catch:{ Throwable -> 0x0140 }
    L_0x0123:
        r1 = r0;	 Catch:{ Throwable -> 0x0140 }
        goto L_0x0143;	 Catch:{ Throwable -> 0x0140 }
    L_0x0125:
        r1 = r7;	 Catch:{ Throwable -> 0x0140 }
    L_0x0126:
        if (r1 == 0) goto L_0x0103;	 Catch:{ Throwable -> 0x0140 }
    L_0x0128:
        r10 = 0;	 Catch:{ Throwable -> 0x0140 }
        r11 = 0;	 Catch:{ Throwable -> 0x0140 }
        r12 = r1.getWidth();	 Catch:{ Throwable -> 0x0140 }
        r13 = r1.getHeight();	 Catch:{ Throwable -> 0x0140 }
        r15 = 1;	 Catch:{ Throwable -> 0x0140 }
        r9 = r1;	 Catch:{ Throwable -> 0x0140 }
        r14 = r3;	 Catch:{ Throwable -> 0x0140 }
        r2 = org.telegram.messenger.Bitmaps.createBitmap(r9, r10, r11, r12, r13, r14, r15);	 Catch:{ Throwable -> 0x0140 }
        if (r2 == r1) goto L_0x0103;	 Catch:{ Throwable -> 0x0140 }
    L_0x013b:
        r1.recycle();	 Catch:{ Throwable -> 0x0140 }
        r1 = r2;
        goto L_0x0103;
    L_0x0140:
        r0 = move-exception;
        r7 = r1;
        goto L_0x0123;
    L_0x0143:
        org.telegram.messenger.FileLog.m3e(r1);
        goto L_0x0190;
    L_0x0147:
        if (r1 == 0) goto L_0x0190;
    L_0x0149:
        r1 = android.graphics.BitmapFactory.decodeStream(r8, r7, r4);	 Catch:{ Throwable -> 0x017e }
        if (r1 == 0) goto L_0x0171;
    L_0x014f:
        r2 = r4.inPurgeable;	 Catch:{ Throwable -> 0x016e }
        if (r2 == 0) goto L_0x0156;	 Catch:{ Throwable -> 0x016e }
    L_0x0153:
        org.telegram.messenger.Utilities.pinBitmap(r1);	 Catch:{ Throwable -> 0x016e }
    L_0x0156:
        r10 = 0;	 Catch:{ Throwable -> 0x016e }
        r11 = 0;	 Catch:{ Throwable -> 0x016e }
        r12 = r1.getWidth();	 Catch:{ Throwable -> 0x016e }
        r13 = r1.getHeight();	 Catch:{ Throwable -> 0x016e }
        r15 = 1;	 Catch:{ Throwable -> 0x016e }
        r9 = r1;	 Catch:{ Throwable -> 0x016e }
        r14 = r3;	 Catch:{ Throwable -> 0x016e }
        r2 = org.telegram.messenger.Bitmaps.createBitmap(r9, r10, r11, r12, r13, r14, r15);	 Catch:{ Throwable -> 0x016e }
        if (r2 == r1) goto L_0x0171;	 Catch:{ Throwable -> 0x016e }
    L_0x0169:
        r1.recycle();	 Catch:{ Throwable -> 0x016e }
        r7 = r2;
        goto L_0x0172;
    L_0x016e:
        r0 = move-exception;
        r7 = r1;
        goto L_0x017f;
    L_0x0171:
        r7 = r1;
    L_0x0172:
        r8.close();	 Catch:{ Throwable -> 0x0176 }
        goto L_0x0190;
    L_0x0176:
        r0 = move-exception;
        org.telegram.messenger.FileLog.m3e(r0);
        goto L_0x0190;
    L_0x017b:
        r0 = move-exception;
        r1 = r0;
        goto L_0x0187;
    L_0x017e:
        r0 = move-exception;
    L_0x017f:
        r1 = r0;
        org.telegram.messenger.FileLog.m3e(r1);	 Catch:{ all -> 0x017b }
        r8.close();	 Catch:{ Throwable -> 0x0176 }
        goto L_0x0190;
    L_0x0187:
        r8.close();	 Catch:{ Throwable -> 0x018b }
        goto L_0x018f;
    L_0x018b:
        r0 = move-exception;
        org.telegram.messenger.FileLog.m3e(r0);
    L_0x018f:
        throw r1;
    L_0x0190:
        return r7;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ImageLoader.loadBitmap(java.lang.String, android.net.Uri, float, float, boolean):android.graphics.Bitmap");
    }

    public static void fillPhotoSizeWithBytes(PhotoSize photoSize) {
        if (photoSize != null) {
            if (photoSize.bytes == null) {
                try {
                    RandomAccessFile randomAccessFile = new RandomAccessFile(FileLoader.getPathToAttach(photoSize, true), "r");
                    if (((int) randomAccessFile.length()) < 20000) {
                        photoSize.bytes = new byte[((int) randomAccessFile.length())];
                        randomAccessFile.readFully(photoSize.bytes, 0, photoSize.bytes.length);
                    }
                } catch (Throwable th) {
                    FileLog.m3e(th);
                }
            }
        }
    }

    private static PhotoSize scaleAndSaveImageInternal(Bitmap bitmap, int i, int i2, float f, float f2, float f3, int i3, boolean z, boolean z2) throws Exception {
        if (f3 <= 1.0f) {
            if (!z2) {
                i = bitmap;
                i2 = new TL_fileLocation();
                i2.volume_id = -0.0f;
                i2.dc_id = Integer.MIN_VALUE;
                i2.local_id = SharedConfig.getLastLocalId();
                f = new TL_photoSize();
                f.location = i2;
                f.f43w = i.getWidth();
                f.f42h = i.getHeight();
                if (f.f43w > 1.4E-43f && f.f42h <= 1.4E-43f) {
                    f.type = "s";
                } else if (f.f43w > 4.48E-43f && f.f42h <= 4.48E-43f) {
                    f.type = "m";
                } else if (f.f43w > 1.121E-42f && f.f42h <= 1.121E-42f) {
                    f.type = "x";
                } else if (f.f43w <= 1.794E-42f || f.f42h > 1.794E-42f) {
                    f.type = "w";
                } else {
                    f.type = "y";
                }
                f2 = new StringBuilder();
                f2.append(i2.volume_id);
                f2.append("_");
                f2.append(i2.local_id);
                f2.append(".jpg");
                i2 = new FileOutputStream(new File(FileLoader.getDirectory(5.6E-45f), f2.toString()));
                i.compress(CompressFormat.JPEG, i3, i2);
                if (z) {
                    f.size = (int) i2.getChannel().size();
                } else {
                    f2 = new ByteArrayOutputStream();
                    i.compress(CompressFormat.JPEG, i3, f2);
                    f.bytes = f2.toByteArray();
                    f.size = f.bytes.length;
                    f2.close();
                }
                i2.close();
                if (i != bitmap) {
                    i.recycle();
                }
                return f;
            }
        }
        i = Bitmaps.createScaledBitmap(bitmap, i, i2, Float.MIN_VALUE);
        i2 = new TL_fileLocation();
        i2.volume_id = -0.0f;
        i2.dc_id = Integer.MIN_VALUE;
        i2.local_id = SharedConfig.getLastLocalId();
        f = new TL_photoSize();
        f.location = i2;
        f.f43w = i.getWidth();
        f.f42h = i.getHeight();
        if (f.f43w > 1.4E-43f) {
        }
        if (f.f43w > 4.48E-43f) {
        }
        if (f.f43w > 1.121E-42f) {
        }
        if (f.f43w <= 1.794E-42f) {
        }
        f.type = "w";
        f2 = new StringBuilder();
        f2.append(i2.volume_id);
        f2.append("_");
        f2.append(i2.local_id);
        f2.append(".jpg");
        i2 = new FileOutputStream(new File(FileLoader.getDirectory(5.6E-45f), f2.toString()));
        i.compress(CompressFormat.JPEG, i3, i2);
        if (z) {
            f.size = (int) i2.getChannel().size();
        } else {
            f2 = new ByteArrayOutputStream();
            i.compress(CompressFormat.JPEG, i3, f2);
            f.bytes = f2.toByteArray();
            f.size = f.bytes.length;
            f2.close();
        }
        i2.close();
        if (i != bitmap) {
            i.recycle();
        }
        return f;
    }

    public static PhotoSize scaleAndSaveImage(Bitmap bitmap, float f, float f2, int i, boolean z) {
        return scaleAndSaveImage(bitmap, f, f2, i, z, 0, 0);
    }

    public static PhotoSize scaleAndSaveImage(Bitmap bitmap, float f, float f2, int i, boolean z, int i2, int i3) {
        int i4;
        int i5;
        int i6 = i2;
        int i7 = i3;
        if (bitmap == null) {
            return null;
        }
        float width = (float) bitmap.getWidth();
        float height = (float) bitmap.getHeight();
        if (width != 0.0f) {
            if (height != 0.0f) {
                float f3;
                boolean z2;
                int i8;
                int i9;
                float max = Math.max(width / f, height / f2);
                if (!(i6 == 0 || i7 == 0)) {
                    f3 = (float) i6;
                    if (width < f3 || height < ((float) i7)) {
                        if (width >= f3 || height <= ((float) i7)) {
                            if (width > f3) {
                                float f4 = (float) i7;
                                if (height < f4) {
                                    f3 = height / f4;
                                }
                            }
                            f3 = Math.max(width / f3, height / ((float) i7));
                        } else {
                            f3 = width / f3;
                        }
                        z2 = true;
                        i8 = (int) (width / f3);
                        i9 = (int) (height / f3);
                        if (i9 != 0) {
                            if (i8 == 0) {
                                i4 = i9;
                                i5 = i8;
                                try {
                                    return scaleAndSaveImageInternal(bitmap, i8, i9, width, height, f3, i, z, z2);
                                } catch (Throwable th) {
                                    FileLog.m3e(th);
                                    return null;
                                }
                            }
                        }
                        return null;
                    }
                }
                z2 = false;
                f3 = max;
                i8 = (int) (width / f3);
                i9 = (int) (height / f3);
                if (i9 != 0) {
                    if (i8 == 0) {
                        i4 = i9;
                        i5 = i8;
                        return scaleAndSaveImageInternal(bitmap, i8, i9, width, height, f3, i, z, z2);
                    }
                }
                return null;
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
        TLObject tLObject;
        int i = 0;
        TLObject tLObject2 = null;
        int size;
        int i2;
        if (message.media instanceof TL_messageMediaPhoto) {
            size = message.media.photo.sizes.size();
            i2 = 0;
            while (i2 < size) {
                tLObject = (PhotoSize) message.media.photo.sizes.get(i2);
                if (!(tLObject instanceof TL_photoCachedSize)) {
                    i2++;
                }
            }
            if (tLObject2 != null && tLObject2.bytes != null && tLObject2.bytes.length != 0) {
                StringBuilder stringBuilder;
                if (tLObject2.location instanceof TL_fileLocationUnavailable) {
                    tLObject2.location = new TL_fileLocation();
                    tLObject2.location.volume_id = -2147483648L;
                    tLObject2.location.dc_id = Integer.MIN_VALUE;
                    tLObject2.location.local_id = SharedConfig.getLastLocalId();
                }
                boolean z = true;
                File pathToAttach = FileLoader.getPathToAttach(tLObject2, true);
                if (MessageObject.shouldEncryptPhotoOrVideo(message)) {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append(pathToAttach.getAbsolutePath());
                    stringBuilder.append(".enc");
                    pathToAttach = new File(stringBuilder.toString());
                } else {
                    z = false;
                }
                if (!pathToAttach.exists()) {
                    if (z) {
                        try {
                            File internalCacheDir = FileLoader.getInternalCacheDir();
                            stringBuilder = new StringBuilder();
                            stringBuilder.append(pathToAttach.getName());
                            stringBuilder.append(".key");
                            RandomAccessFile randomAccessFile = new RandomAccessFile(new File(internalCacheDir, stringBuilder.toString()), "rws");
                            long length = randomAccessFile.length();
                            byte[] bArr = new byte[32];
                            byte[] bArr2 = new byte[16];
                            if (length <= 0 || length % 48 != 0) {
                                Utilities.random.nextBytes(bArr);
                                Utilities.random.nextBytes(bArr2);
                                randomAccessFile.write(bArr);
                                randomAccessFile.write(bArr2);
                            } else {
                                randomAccessFile.read(bArr, 0, 32);
                                randomAccessFile.read(bArr2, 0, 16);
                            }
                            randomAccessFile.close();
                            Utilities.aesCtrDecryptionByteArray(tLObject2.bytes, bArr, bArr2, 0, tLObject2.bytes.length, 0);
                        } catch (Throwable e) {
                            FileLog.m3e(e);
                        }
                    }
                    RandomAccessFile randomAccessFile2 = new RandomAccessFile(pathToAttach, "rws");
                    randomAccessFile2.write(tLObject2.bytes);
                    randomAccessFile2.close();
                }
                PhotoSize tL_photoSize = new TL_photoSize();
                tL_photoSize.w = tLObject2.f43w;
                tL_photoSize.h = tLObject2.f42h;
                tL_photoSize.location = tLObject2.location;
                tL_photoSize.size = tLObject2.size;
                tL_photoSize.type = tLObject2.type;
                int size2;
                if (message.media instanceof TL_messageMediaPhoto) {
                    size2 = message.media.photo.sizes.size();
                    while (i < size2) {
                        if (message.media.photo.sizes.get(i) instanceof TL_photoCachedSize) {
                            message.media.photo.sizes.set(i, tL_photoSize);
                            return;
                        }
                        i++;
                    }
                    return;
                } else if (message.media instanceof TL_messageMediaDocument) {
                    message.media.document.thumb = tL_photoSize;
                    return;
                } else if (message.media instanceof TL_messageMediaWebPage) {
                    size2 = message.media.webpage.photo.sizes.size();
                    while (i < size2) {
                        if (message.media.webpage.photo.sizes.get(i) instanceof TL_photoCachedSize) {
                            message.media.webpage.photo.sizes.set(i, tL_photoSize);
                            return;
                        }
                        i++;
                    }
                    return;
                } else {
                    return;
                }
            }
            return;
        }
        if (message.media instanceof TL_messageMediaDocument) {
            if (message.media.document.thumb instanceof TL_photoCachedSize) {
                tLObject2 = message.media.document.thumb;
            }
        } else if ((message.media instanceof TL_messageMediaWebPage) && message.media.webpage.photo != null) {
            size = message.media.webpage.photo.sizes.size();
            i2 = 0;
            while (i2 < size) {
                tLObject = (PhotoSize) message.media.webpage.photo.sizes.get(i2);
                if (!(tLObject instanceof TL_photoCachedSize)) {
                    i2++;
                }
            }
        }
        if (tLObject2 != null) {
        }
        tLObject2 = tLObject;
        if (tLObject2 != null) {
        }
    }

    public static void saveMessagesThumbs(ArrayList<Message> arrayList) {
        if (arrayList != null) {
            if (!arrayList.isEmpty()) {
                for (int i = 0; i < arrayList.size(); i++) {
                    saveMessageThumbs((Message) arrayList.get(i));
                }
            }
        }
    }
}
