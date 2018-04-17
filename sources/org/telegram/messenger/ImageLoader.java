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
    private static volatile ImageLoader Instance = null;
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
    class C01933 extends BroadcastReceiver {

        /* renamed from: org.telegram.messenger.ImageLoader$3$1 */
        class C01921 implements Runnable {
            C01921() {
            }

            public void run() {
                ImageLoader.this.checkMediaPaths();
            }
        }

        C01933() {
        }

        public void onReceive(Context arg0, Intent intent) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.m0d("file system changed");
            }
            Runnable r = new C01921();
            if ("android.intent.action.MEDIA_UNMOUNTED".equals(intent.getAction())) {
                AndroidUtilities.runOnUIThread(r, 1000);
            } else {
                r.run();
            }
        }
    }

    /* renamed from: org.telegram.messenger.ImageLoader$4 */
    class C01954 implements Runnable {
        C01954() {
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
            int a = 0;
            Boolean thumb = Boolean.valueOf(this.selfThumb);
            int a2 = 0;
            while (a2 < this.imageReceiverArray.size()) {
                ImageReceiver obj = (ImageReceiver) this.imageReceiverArray.get(a2);
                if (obj == null || obj == imageReceiver) {
                    this.imageReceiverArray.remove(a2);
                    this.keys.remove(a2);
                    this.filters.remove(a2);
                    thumb = (Boolean) this.thumbs.remove(a2);
                    if (obj != null) {
                        ImageLoader.this.imageLoadingByTag.remove(obj.getTag(thumb.booleanValue()));
                    }
                    a2--;
                }
                a2++;
            }
            if (this.imageReceiverArray.size() == 0) {
                while (true) {
                    a2 = a;
                    if (a2 >= this.imageReceiverArray.size()) {
                        break;
                    }
                    ImageLoader.this.imageLoadingByTag.remove(((ImageReceiver) this.imageReceiverArray.get(a2)).getTag(thumb.booleanValue()));
                    a = a2 + 1;
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
                            BitmapDrawable fileDrawable = image;
                            boolean imageSet = false;
                            a = 0;
                            while (a < finalImageReceiverArray.size()) {
                                if (((ImageReceiver) finalImageReceiverArray.get(a)).setImageBitmapByKey(a == 0 ? fileDrawable : fileDrawable.makeCopy(), CacheImage.this.key, CacheImage.this.selfThumb, false)) {
                                    imageSet = true;
                                }
                                a++;
                            }
                            if (!imageSet) {
                                ((AnimatedFileDrawable) image).recycle();
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

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void run() {
            boolean mediaIsVideo;
            boolean z;
            boolean canDeleteFile;
            Throwable th;
            Long l;
            int i;
            boolean z2;
            Object obj;
            AnimatedFileDrawable fileDrawable = this.sync;
            synchronized (fileDrawable) {
                try {
                    r1.runningThread = Thread.currentThread();
                    Thread.interrupted();
                    if (r1.isCancelled) {
                        return;
                    }
                } finally {
                    boolean z3 = 
/*
Method generation error in method: org.telegram.messenger.ImageLoader.CacheOutTask.run():void, dex: classes.dex
jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x06c0: MERGE  (r3_80 'z3' boolean) = (r0_34 boolean), (r4_60 boolean) in method: org.telegram.messenger.ImageLoader.CacheOutTask.run():void, dex: classes.dex
	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:226)
	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:203)
	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:100)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:50)
	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:87)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:53)
	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:93)
	at jadx.core.codegen.RegionGen.makeTryCatch(RegionGen.java:299)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:63)
	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:87)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:53)
	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:87)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:53)
	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:93)
	at jadx.core.codegen.RegionGen.makeSynchronizedRegion(RegionGen.java:229)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:65)
	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:87)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:53)
	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:187)
	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:320)
	at jadx.core.codegen.ClassGen.addMethods(ClassGen.java:257)
	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:220)
	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:110)
	at jadx.core.codegen.ClassGen.addInnerClasses(ClassGen.java:233)
	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:219)
	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:110)
	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:75)
	at jadx.core.codegen.CodeGen.visit(CodeGen.java:12)
	at jadx.core.ProcessClass.process(ProcessClass.java:40)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
Caused by: jadx.core.utils.exceptions.CodegenException: MERGE can be used only in fallback mode
	at jadx.core.codegen.InsnGen.fallbackOnlyInsn(InsnGen.java:537)
	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:509)
	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:220)
	... 31 more

*/

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
                                class C02041 implements Runnable {
                                    C02041() {
                                    }

                                    public void run() {
                                        NotificationCenter.getInstance(HttpFileTask.this.currentAccount).postNotificationName(NotificationCenter.FileLoadProgressChanged, HttpFileTask.this.url, Float.valueOf(progress));
                                    }
                                }

                                public void run() {
                                    ImageLoader.this.fileProgresses.put(HttpFileTask.this.url, Float.valueOf(progress));
                                    AndroidUtilities.runOnUIThread(new C02041());
                                }
                            });
                        }
                    }

                    protected Boolean doInBackground(Void... voids) {
                        int status;
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
                                status = httpURLConnection.getResponseCode();
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
                                        if (!(code == Callback.DEFAULT_DRAG_ANIMATION_DURATION || code == 202 || code == 304)) {
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
                                        status = httpConnectionStream.read(data);
                                        if (status > 0) {
                                            this.fileOutputStream.write(data, 0, status);
                                            totalLoaded += status;
                                            if (this.fileSize > 0) {
                                                reportProgress(((float) totalLoaded) / ((float) this.fileSize));
                                            }
                                        } else if (status == -1) {
                                            done = true;
                                            if (this.fileSize != 0) {
                                                reportProgress(1.0f);
                                            }
                                        }
                                    }
                                } catch (Throwable e3) {
                                    FileLog.m3e(e3);
                                } catch (Throwable e32) {
                                    FileLog.m3e(e32);
                                }
                            }
                            try {
                                if (this.fileOutputStream != null) {
                                    this.fileOutputStream.close();
                                    this.fileOutputStream = null;
                                }
                            } catch (Throwable e4) {
                                FileLog.m3e(e4);
                            }
                            if (httpConnectionStream != null) {
                                try {
                                    httpConnectionStream.close();
                                } catch (Throwable e42) {
                                    FileLog.m3e(e42);
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
                    class C02103 implements Runnable {
                        C02103() {
                        }

                        public void run() {
                            ImageLoader.this.runHttpTasks(true);
                        }
                    }

                    /* renamed from: org.telegram.messenger.ImageLoader$HttpImageTask$4 */
                    class C02114 implements Runnable {
                        C02114() {
                        }

                        public void run() {
                            ImageLoader.this.runHttpTasks(true);
                        }
                    }

                    /* renamed from: org.telegram.messenger.ImageLoader$HttpImageTask$5 */
                    class C02135 implements Runnable {

                        /* renamed from: org.telegram.messenger.ImageLoader$HttpImageTask$5$1 */
                        class C02121 implements Runnable {
                            C02121() {
                            }

                            public void run() {
                                NotificationCenter.getInstance(HttpImageTask.this.cacheImage.currentAccount).postNotificationName(NotificationCenter.FileDidFailedLoad, HttpImageTask.this.cacheImage.url, Integer.valueOf(1));
                            }
                        }

                        C02135() {
                        }

                        public void run() {
                            ImageLoader.this.fileProgresses.remove(HttpImageTask.this.cacheImage.url);
                            AndroidUtilities.runOnUIThread(new C02121());
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
                                class C02061 implements Runnable {
                                    C02061() {
                                    }

                                    public void run() {
                                        NotificationCenter.getInstance(HttpImageTask.this.cacheImage.currentAccount).postNotificationName(NotificationCenter.FileLoadProgressChanged, HttpImageTask.this.cacheImage.url, Float.valueOf(progress));
                                    }
                                }

                                public void run() {
                                    ImageLoader.this.fileProgresses.put(HttpImageTask.this.cacheImage.url, Float.valueOf(progress));
                                    AndroidUtilities.runOnUIThread(new C02061());
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
                                } catch (Throwable e3) {
                                    FileLog.m3e(e3);
                                } catch (Throwable e222) {
                                    FileLog.m3e(e222);
                                }
                            }
                        }
                        try {
                            if (this.fileOutputStream != null) {
                                this.fileOutputStream.close();
                                this.fileOutputStream = null;
                            }
                        } catch (Throwable e2222) {
                            FileLog.m3e(e2222);
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
                            } catch (Throwable e22222) {
                                FileLog.m3e(e22222);
                            }
                        }
                        if (!(!done || this.cacheImage.tempFilePath == null || this.cacheImage.tempFilePath.renameTo(this.cacheImage.finalFilePath))) {
                            this.cacheImage.finalFilePath = this.cacheImage.tempFilePath;
                        }
                        return Boolean.valueOf(done);
                    }

                    protected void onPostExecute(final Boolean result) {
                        if (!result.booleanValue()) {
                            if (this.canRetry) {
                                ImageLoader.this.httpFileLoadError(this.cacheImage.url);
                                Utilities.stageQueue.postRunnable(new Runnable() {

                                    /* renamed from: org.telegram.messenger.ImageLoader$HttpImageTask$2$1 */
                                    class C02081 implements Runnable {
                                        C02081() {
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
                                        AndroidUtilities.runOnUIThread(new C02081());
                                    }
                                });
                                ImageLoader.this.imageLoadQueue.postRunnable(new C02103());
                            }
                        }
                        ImageLoader.this.fileDidLoaded(this.cacheImage.url, this.cacheImage.finalFilePath, 0);
                        Utilities.stageQueue.postRunnable(/* anonymous class already generated */);
                        ImageLoader.this.imageLoadQueue.postRunnable(new C02103());
                    }

                    protected void onCancelled() {
                        ImageLoader.this.imageLoadQueue.postRunnable(new C02114());
                        Utilities.stageQueue.postRunnable(new C02135());
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
                            String key = new StringBuilder();
                            key.append(this.thumbLocation.volume_id);
                            key.append("_");
                            key.append(this.thumbLocation.local_id);
                            key = key.toString();
                            File directory = FileLoader.getDirectory(4);
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append("q_");
                            stringBuilder.append(key);
                            stringBuilder.append(".jpg");
                            File thumbFile = new File(directory, stringBuilder.toString());
                            if (!thumbFile.exists()) {
                                if (this.originalPath.exists()) {
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
                                    if (w != 0) {
                                        if (h != 0) {
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
                                                        StringBuilder stringBuilder = new StringBuilder();
                                                        stringBuilder.append(kf);
                                                        stringBuilder.append("@");
                                                        stringBuilder.append(ThumbGenerateTask.this.filter);
                                                        kf = stringBuilder.toString();
                                                    }
                                                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.messageThumbGenerated, bitmapDrawable, kf);
                                                    ImageLoader.this.memCache.put(kf, bitmapDrawable);
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
                            localInstance = Instance;
                            if (localInstance == null) {
                                ImageLoader imageLoader = new ImageLoader();
                                localInstance = imageLoader;
                                Instance = imageLoader;
                            }
                        }
                    }
                    return localInstance;
                }

                public ImageLoader() {
                    int a = 0;
                    this.currentHttpTasksCount = 0;
                    this.httpFileLoadTasks = new LinkedList();
                    this.httpFileLoadTasksByKeys = new HashMap();
                    this.retryHttpsTasks = new HashMap();
                    this.currentHttpFileLoadTasksCount = 0;
                    this.ignoreRemoval = null;
                    this.lastCacheOutTime = 0;
                    this.lastImageNum = 0;
                    this.lastProgressUpdateTime = 0;
                    this.telegramPath = null;
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
                    while (a < 3) {
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
                                    class C01851 implements Runnable {
                                        C01851() {
                                        }

                                        public void run() {
                                            NotificationCenter.getInstance(currentAccount).postNotificationName(NotificationCenter.FileDidUpload, str, inputFile2, inputEncryptedFile2, bArr, bArr2, Long.valueOf(j));
                                        }
                                    }

                                    public void run() {
                                        AndroidUtilities.runOnUIThread(new C01851());
                                        ImageLoader.this.fileProgresses.remove(str);
                                    }
                                });
                            }

                            public void fileDidFailedUpload(final String location, final boolean isEncrypted) {
                                Utilities.stageQueue.postRunnable(new Runnable() {

                                    /* renamed from: org.telegram.messenger.ImageLoader$2$3$1 */
                                    class C01871 implements Runnable {
                                        C01871() {
                                        }

                                        public void run() {
                                            NotificationCenter.getInstance(currentAccount).postNotificationName(NotificationCenter.FileDidFailUpload, location, Boolean.valueOf(isEncrypted));
                                        }
                                    }

                                    public void run() {
                                        AndroidUtilities.runOnUIThread(new C01871());
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
                        a++;
                    }
                    FileLoader.setMediaDirs(mediaDirs);
                    BroadcastReceiver receiver = new C01933();
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
                    this.cacheOutQueue.postRunnable(new C01954());
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
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("cache path = ");
                        stringBuilder.append(cachePath);
                        FileLog.m0d(stringBuilder.toString());
                    }
                    try {
                        if ("mounted".equals(Environment.getExternalStorageState())) {
                            this.telegramPath = new File(Environment.getExternalStorageDirectory(), "Telegram");
                            this.telegramPath.mkdirs();
                            if (this.telegramPath.isDirectory()) {
                                File imagePath;
                                StringBuilder stringBuilder2;
                                try {
                                    imagePath = new File(this.telegramPath, "Telegram Images");
                                    imagePath.mkdir();
                                    if (imagePath.isDirectory() && canMoveFiles(cachePath, imagePath, 0)) {
                                        mediaDirs.put(0, imagePath);
                                        if (BuildVars.LOGS_ENABLED) {
                                            stringBuilder2 = new StringBuilder();
                                            stringBuilder2.append("image path = ");
                                            stringBuilder2.append(imagePath);
                                            FileLog.m0d(stringBuilder2.toString());
                                        }
                                    }
                                } catch (Throwable e22) {
                                    FileLog.m3e(e22);
                                }
                                try {
                                    imagePath = new File(this.telegramPath, "Telegram Video");
                                    imagePath.mkdir();
                                    if (imagePath.isDirectory() && canMoveFiles(cachePath, imagePath, 2)) {
                                        mediaDirs.put(2, imagePath);
                                        if (BuildVars.LOGS_ENABLED) {
                                            stringBuilder2 = new StringBuilder();
                                            stringBuilder2.append("video path = ");
                                            stringBuilder2.append(imagePath);
                                            FileLog.m0d(stringBuilder2.toString());
                                        }
                                    }
                                } catch (Throwable e222) {
                                    FileLog.m3e(e222);
                                }
                                try {
                                    imagePath = new File(this.telegramPath, "Telegram Audio");
                                    imagePath.mkdir();
                                    if (imagePath.isDirectory() && canMoveFiles(cachePath, imagePath, 1)) {
                                        new File(imagePath, ".nomedia").createNewFile();
                                        mediaDirs.put(1, imagePath);
                                        if (BuildVars.LOGS_ENABLED) {
                                            stringBuilder2 = new StringBuilder();
                                            stringBuilder2.append("audio path = ");
                                            stringBuilder2.append(imagePath);
                                            FileLog.m0d(stringBuilder2.toString());
                                        }
                                    }
                                } catch (Throwable e2222) {
                                    FileLog.m3e(e2222);
                                }
                                try {
                                    imagePath = new File(this.telegramPath, "Telegram Documents");
                                    imagePath.mkdir();
                                    if (imagePath.isDirectory() && canMoveFiles(cachePath, imagePath, 3)) {
                                        new File(imagePath, ".nomedia").createNewFile();
                                        mediaDirs.put(3, imagePath);
                                        if (BuildVars.LOGS_ENABLED) {
                                            stringBuilder2 = new StringBuilder();
                                            stringBuilder2.append("documents path = ");
                                            stringBuilder2.append(imagePath);
                                            FileLog.m0d(stringBuilder2.toString());
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
                    RandomAccessFile file = null;
                    File srcFile = null;
                    File dstFile = null;
                    if (type == 0) {
                        try {
                            srcFile = new File(from, "000000000_999999_temp.jpg");
                            dstFile = new File(to, "000000000_999999.jpg");
                        } catch (Throwable e) {
                            FileLog.m3e(e);
                            if (file != null) {
                                file.close();
                            }
                        } catch (Throwable th) {
                            if (file != null) {
                                try {
                                    file.close();
                                } catch (Throwable e2) {
                                    FileLog.m3e(e2);
                                }
                            }
                        }
                    } else if (type == 3) {
                        srcFile = new File(from, "000000000_999999_temp.doc");
                        dstFile = new File(to, "000000000_999999.doc");
                    } else if (type == 1) {
                        srcFile = new File(from, "000000000_999999_temp.ogg");
                        dstFile = new File(to, "000000000_999999.ogg");
                    } else if (type == 2) {
                        srcFile = new File(from, "000000000_999999_temp.mp4");
                        dstFile = new File(to, "000000000_999999.mp4");
                    }
                    byte[] buffer = new byte[1024];
                    srcFile.createNewFile();
                    file = new RandomAccessFile(srcFile, "rws");
                    file.write(buffer);
                    file.close();
                    file = null;
                    boolean canRename = srcFile.renameTo(dstFile);
                    srcFile.delete();
                    dstFile.delete();
                    if (canRename) {
                        if (file != null) {
                            try {
                                file.close();
                            } catch (Throwable e3) {
                                FileLog.m3e(e3);
                            }
                        }
                        return true;
                    }
                    if (file != null) {
                        try {
                            file.close();
                        } catch (Throwable e4) {
                            FileLog.m3e(e4);
                        }
                    }
                    return false;
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
                        r2 = new StringBuilder();
                        r2.append(location.volume_id);
                        r2.append("_");
                        r2.append(location.local_id);
                        key = r2.toString();
                    } else if (fileLocation instanceof Document) {
                        Document location2 = (Document) fileLocation;
                        if (location2.version == 0) {
                            r2 = new StringBuilder();
                            r2.append(location2.dc_id);
                            r2.append("_");
                            r2.append(location2.id);
                            key = r2.toString();
                        } else {
                            r2 = new StringBuilder();
                            r2.append(location2.dc_id);
                            r2.append("_");
                            r2.append(location2.id);
                            r2.append("_");
                            r2.append(location2.version);
                            key = r2.toString();
                        }
                    } else if (fileLocation instanceof TL_webDocument) {
                        key = Utilities.MD5(((TL_webDocument) fileLocation).url);
                    }
                    if (filter != null) {
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append(key);
                        stringBuilder.append("@");
                        stringBuilder.append(filter);
                        key = stringBuilder.toString();
                    }
                    return this.memCache.get(key);
                }

                private void replaceImageInCacheInternal(String oldKey, String newKey, FileLocation newLocation) {
                    ArrayList<String> arr = this.memCache.getFilterKeys(oldKey);
                    if (arr != null) {
                        for (int a = 0; a < arr.size(); a++) {
                            String filter = (String) arr.get(a);
                            String oldK = new StringBuilder();
                            oldK.append(oldKey);
                            oldK.append("@");
                            oldK.append(filter);
                            oldK = oldK.toString();
                            String newK = new StringBuilder();
                            newK.append(newKey);
                            newK.append("@");
                            newK.append(filter);
                            performReplace(oldK, newK.toString());
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
                    if ((mediaType == 0 || mediaType == 2 || mediaType == 3) && originalPath != null) {
                        if (thumbLocation != null) {
                            if (((ThumbGenerateTask) this.thumbGenerateTasks.get(FileLoader.getAttachFileName(thumbLocation))) == null) {
                                this.thumbGeneratingQueue.postRunnable(new ThumbGenerateTask(mediaType, originalPath, thumbLocation, filter));
                            }
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
                    ImageLoader imageLoader = this;
                    ImageReceiver imageReceiver2 = imageReceiver;
                    if (!(imageReceiver2 == null || url == null)) {
                        if (key != null) {
                            int TAG = imageReceiver2.getTag(thumb != 0 ? 1 : 0);
                            if (TAG == 0) {
                                int i = imageLoader.lastImageNum;
                                TAG = i;
                                imageReceiver2.setTag(i, thumb != 0);
                                imageLoader.lastImageNum++;
                                if (imageLoader.lastImageNum == ConnectionsManager.DEFAULT_DATACENTER_ID) {
                                    imageLoader.lastImageNum = 0;
                                }
                            }
                            final int finalTag = TAG;
                            boolean finalIsNeedsQualityThumb = imageReceiver.isNeedsQualityThumb();
                            MessageObject parentMessageObject = imageReceiver.getParentMessageObject();
                            boolean shouldGenerateQualityThumb = imageReceiver.isShouldGenerateQualityThumb();
                            int currentAccount = imageReceiver.getcurrentAccount();
                            TAG = thumb;
                            final String str = url;
                            final String str2 = key;
                            final ImageReceiver imageReceiver3 = imageReceiver2;
                            final String str3 = filter;
                            final String str4 = httpLocation;
                            final boolean z = finalIsNeedsQualityThumb;
                            final MessageObject messageObject = parentMessageObject;
                            final TLObject tLObject = imageLocation;
                            C01998 c01998 = r0;
                            final boolean z2 = shouldGenerateQualityThumb;
                            DispatchQueue dispatchQueue = imageLoader.imageLoadQueue;
                            final int i2 = cacheType;
                            final int i3 = size;
                            final String str5 = ext;
                            final int i4 = currentAccount;
                            C01998 c019982 = new Runnable() {
                                public void run() {
                                    boolean added = false;
                                    if (TAG != 2) {
                                        CacheImage alreadyLoadingUrl = (CacheImage) ImageLoader.this.imageLoadingByUrl.get(str);
                                        CacheImage alreadyLoadingCache = (CacheImage) ImageLoader.this.imageLoadingByKeys.get(str2);
                                        CacheImage alreadyLoadingImage = (CacheImage) ImageLoader.this.imageLoadingByTag.get(finalTag);
                                        if (alreadyLoadingImage != null) {
                                            if (alreadyLoadingImage == alreadyLoadingCache) {
                                                added = true;
                                            } else if (alreadyLoadingImage == alreadyLoadingUrl) {
                                                if (alreadyLoadingCache == null) {
                                                    alreadyLoadingImage.replaceImageReceiver(imageReceiver3, str2, str3, TAG != 0);
                                                }
                                                added = true;
                                            } else {
                                                alreadyLoadingImage.removeImageReceiver(imageReceiver3);
                                            }
                                        }
                                        if (!(added || alreadyLoadingCache == null)) {
                                            alreadyLoadingCache.addImageReceiver(imageReceiver3, str2, str3, TAG != 0);
                                            added = true;
                                        }
                                        if (!(added || alreadyLoadingUrl == null)) {
                                            alreadyLoadingUrl.addImageReceiver(imageReceiver3, str2, str3, TAG != 0);
                                            added = true;
                                        }
                                    }
                                    if (!added) {
                                        File directory;
                                        StringBuilder stringBuilder;
                                        String location;
                                        boolean onlyCache = false;
                                        File cacheFile = null;
                                        boolean cacheFileExists = false;
                                        if (str4 != null) {
                                            if (!str4.startsWith("http")) {
                                                onlyCache = true;
                                                int idx;
                                                if (str4.startsWith("thumb://")) {
                                                    idx = str4.indexOf(":", 8);
                                                    if (idx >= 0) {
                                                        cacheFile = new File(str4.substring(idx + 1));
                                                    }
                                                } else if (str4.startsWith("vthumb://")) {
                                                    idx = str4.indexOf(":", 9);
                                                    if (idx >= 0) {
                                                        cacheFile = new File(str4.substring(idx + 1));
                                                    }
                                                } else {
                                                    cacheFile = new File(str4);
                                                }
                                            }
                                        } else if (TAG != 0) {
                                            if (z) {
                                                directory = FileLoader.getDirectory(4);
                                                stringBuilder = new StringBuilder();
                                                stringBuilder.append("q_");
                                                stringBuilder.append(str);
                                                cacheFile = new File(directory, stringBuilder.toString());
                                                if (cacheFile.exists()) {
                                                    cacheFileExists = true;
                                                } else {
                                                    cacheFile = null;
                                                }
                                            }
                                            if (messageObject != null) {
                                                File attachPath = null;
                                                if (messageObject.messageOwner.attachPath != null && messageObject.messageOwner.attachPath.length() > 0) {
                                                    attachPath = new File(messageObject.messageOwner.attachPath);
                                                    if (!attachPath.exists()) {
                                                        attachPath = null;
                                                    }
                                                }
                                                if (attachPath == null) {
                                                    attachPath = FileLoader.getPathToMessage(messageObject.messageOwner);
                                                }
                                                if (z && cacheFile == null) {
                                                    location = messageObject.getFileName();
                                                    ThumbGenerateInfo info = (ThumbGenerateInfo) ImageLoader.this.waitingForQualityThumb.get(location);
                                                    if (info == null) {
                                                        info = new ThumbGenerateInfo();
                                                        info.fileLocation = (FileLocation) tLObject;
                                                        info.filter = str3;
                                                        ImageLoader.this.waitingForQualityThumb.put(location, info);
                                                    }
                                                    info.count = info.count + 1;
                                                    ImageLoader.this.waitingForQualityThumbByTag.put(finalTag, location);
                                                }
                                                if (attachPath.exists() && z2) {
                                                    ImageLoader.this.generateThumb(messageObject.getFileType(), attachPath, (FileLocation) tLObject, str3);
                                                }
                                            }
                                        }
                                        if (TAG != 2) {
                                            boolean isEncrypted;
                                            CacheImage img;
                                            File directory2;
                                            StringBuilder stringBuilder2;
                                            FileLocation location2;
                                            int localCacheType;
                                            String file;
                                            File cacheDir;
                                            StringBuilder stringBuilder3;
                                            if (!(tLObject instanceof TL_documentEncrypted)) {
                                                if (!(tLObject instanceof TL_fileEncryptedLocation)) {
                                                    isEncrypted = false;
                                                    img = new CacheImage();
                                                    if (str4 == null && !str4.startsWith("vthumb") && !str4.startsWith("thumb")) {
                                                        location = ImageLoader.getHttpUrlExtension(str4, "jpg");
                                                        if (location.equals("mp4") || location.equals("gif")) {
                                                            img.animatedFile = true;
                                                        }
                                                    } else if (((tLObject instanceof TL_webDocument) && MessageObject.isGifDocument((TL_webDocument) tLObject)) || ((tLObject instanceof Document) && (MessageObject.isGifDocument((Document) tLObject) || MessageObject.isRoundVideoDocument((Document) tLObject)))) {
                                                        img.animatedFile = true;
                                                    }
                                                    if (cacheFile == null) {
                                                        if (i2 == 0 && i3 > 0 && str4 == null) {
                                                            if (!isEncrypted) {
                                                                cacheFile = tLObject instanceof Document ? MessageObject.isVideoDocument((Document) tLObject) ? new File(FileLoader.getDirectory(2), str) : new File(FileLoader.getDirectory(3), str) : tLObject instanceof TL_webDocument ? new File(FileLoader.getDirectory(3), str) : new File(FileLoader.getDirectory(0), str);
                                                            }
                                                        }
                                                        cacheFile = new File(FileLoader.getDirectory(4), str);
                                                        if (cacheFile.exists()) {
                                                            cacheFileExists = true;
                                                        } else if (i2 == 2) {
                                                            directory2 = FileLoader.getDirectory(4);
                                                            stringBuilder2 = new StringBuilder();
                                                            stringBuilder2.append(str);
                                                            stringBuilder2.append(".enc");
                                                            cacheFile = new File(directory2, stringBuilder2.toString());
                                                        }
                                                    }
                                                    img.selfThumb = TAG == 0;
                                                    img.key = str2;
                                                    img.filter = str3;
                                                    img.httpUrl = str4;
                                                    img.ext = str5;
                                                    img.currentAccount = i4;
                                                    if (i2 == 2) {
                                                        directory = FileLoader.getInternalCacheDir();
                                                        stringBuilder = new StringBuilder();
                                                        stringBuilder.append(str);
                                                        stringBuilder.append(".enc.key");
                                                        img.encryptionKeyPath = new File(directory, stringBuilder.toString());
                                                    }
                                                    img.addImageReceiver(imageReceiver3, str2, str3, TAG == 0);
                                                    if (!(onlyCache || cacheFileExists)) {
                                                        if (cacheFile.exists()) {
                                                            img.url = str;
                                                            img.location = tLObject;
                                                            ImageLoader.this.imageLoadingByUrl.put(str, img);
                                                            if (str4 != null) {
                                                                if (tLObject instanceof FileLocation) {
                                                                    location2 = tLObject;
                                                                    localCacheType = i2;
                                                                    if (localCacheType == 0 && (i3 <= 0 || location2.key != null)) {
                                                                        localCacheType = 1;
                                                                    }
                                                                    FileLoader.getInstance(i4).loadFile(location2, str5, i3, localCacheType);
                                                                } else if (tLObject instanceof Document) {
                                                                    FileLoader.getInstance(i4).loadFile((Document) tLObject, true, i2);
                                                                } else if (tLObject instanceof TL_webDocument) {
                                                                    FileLoader.getInstance(i4).loadFile((TL_webDocument) tLObject, true, i2);
                                                                }
                                                                if (imageReceiver3.isForceLoding()) {
                                                                    ImageLoader.this.forceLoadingImages.put(img.key, Integer.valueOf(0));
                                                                    return;
                                                                }
                                                                return;
                                                            }
                                                            file = Utilities.MD5(str4);
                                                            cacheDir = FileLoader.getDirectory(4);
                                                            stringBuilder3 = new StringBuilder();
                                                            stringBuilder3.append(file);
                                                            stringBuilder3.append("_temp.jpg");
                                                            img.tempFilePath = new File(cacheDir, stringBuilder3.toString());
                                                            img.finalFilePath = cacheFile;
                                                            img.httpTask = new HttpImageTask(img, i3);
                                                            ImageLoader.this.httpTasks.add(img.httpTask);
                                                            ImageLoader.this.runHttpTasks(false);
                                                            return;
                                                        }
                                                    }
                                                    img.finalFilePath = cacheFile;
                                                    img.cacheTask = new CacheOutTask(img);
                                                    ImageLoader.this.imageLoadingByKeys.put(str2, img);
                                                    if (TAG == 0) {
                                                        ImageLoader.this.cacheThumbOutQueue.postRunnable(img.cacheTask);
                                                    } else {
                                                        ImageLoader.this.cacheOutQueue.postRunnable(img.cacheTask);
                                                    }
                                                }
                                            }
                                            isEncrypted = true;
                                            img = new CacheImage();
                                            if (str4 == null) {
                                            }
                                            img.animatedFile = true;
                                            if (cacheFile == null) {
                                                if (!isEncrypted) {
                                                    cacheFile = new File(FileLoader.getDirectory(4), str);
                                                    if (cacheFile.exists()) {
                                                        cacheFileExists = true;
                                                    } else if (i2 == 2) {
                                                        directory2 = FileLoader.getDirectory(4);
                                                        stringBuilder2 = new StringBuilder();
                                                        stringBuilder2.append(str);
                                                        stringBuilder2.append(".enc");
                                                        cacheFile = new File(directory2, stringBuilder2.toString());
                                                    }
                                                } else if (tLObject instanceof Document) {
                                                    if (MessageObject.isVideoDocument((Document) tLObject)) {
                                                    }
                                                }
                                            }
                                            if (TAG == 0) {
                                            }
                                            img.selfThumb = TAG == 0;
                                            img.key = str2;
                                            img.filter = str3;
                                            img.httpUrl = str4;
                                            img.ext = str5;
                                            img.currentAccount = i4;
                                            if (i2 == 2) {
                                                directory = FileLoader.getInternalCacheDir();
                                                stringBuilder = new StringBuilder();
                                                stringBuilder.append(str);
                                                stringBuilder.append(".enc.key");
                                                img.encryptionKeyPath = new File(directory, stringBuilder.toString());
                                            }
                                            if (TAG == 0) {
                                            }
                                            img.addImageReceiver(imageReceiver3, str2, str3, TAG == 0);
                                            if (cacheFile.exists()) {
                                                img.url = str;
                                                img.location = tLObject;
                                                ImageLoader.this.imageLoadingByUrl.put(str, img);
                                                if (str4 != null) {
                                                    file = Utilities.MD5(str4);
                                                    cacheDir = FileLoader.getDirectory(4);
                                                    stringBuilder3 = new StringBuilder();
                                                    stringBuilder3.append(file);
                                                    stringBuilder3.append("_temp.jpg");
                                                    img.tempFilePath = new File(cacheDir, stringBuilder3.toString());
                                                    img.finalFilePath = cacheFile;
                                                    img.httpTask = new HttpImageTask(img, i3);
                                                    ImageLoader.this.httpTasks.add(img.httpTask);
                                                    ImageLoader.this.runHttpTasks(false);
                                                    return;
                                                }
                                                if (tLObject instanceof FileLocation) {
                                                    location2 = tLObject;
                                                    localCacheType = i2;
                                                    localCacheType = 1;
                                                    FileLoader.getInstance(i4).loadFile(location2, str5, i3, localCacheType);
                                                } else if (tLObject instanceof Document) {
                                                    FileLoader.getInstance(i4).loadFile((Document) tLObject, true, i2);
                                                } else if (tLObject instanceof TL_webDocument) {
                                                    FileLoader.getInstance(i4).loadFile((TL_webDocument) tLObject, true, i2);
                                                }
                                                if (imageReceiver3.isForceLoding()) {
                                                    ImageLoader.this.forceLoadingImages.put(img.key, Integer.valueOf(0));
                                                    return;
                                                }
                                                return;
                                            }
                                            img.finalFilePath = cacheFile;
                                            img.cacheTask = new CacheOutTask(img);
                                            ImageLoader.this.imageLoadingByKeys.put(str2, img);
                                            if (TAG == 0) {
                                                ImageLoader.this.cacheOutQueue.postRunnable(img.cacheTask);
                                            } else {
                                                ImageLoader.this.cacheThumbOutQueue.postRunnable(img.cacheTask);
                                            }
                                        }
                                    }
                                }
                            };
                            dispatchQueue.postRunnable(c01998);
                        }
                    }
                }

                public void loadImageForImageReceiver(ImageReceiver imageReceiver) {
                    ImageLoader imageLoader = this;
                    ImageReceiver imageReceiver2 = imageReceiver;
                    if (imageReceiver2 != null) {
                        TLObject imageLocation;
                        String url;
                        String key;
                        boolean z;
                        StringBuilder stringBuilder;
                        String thumbUrl;
                        String thumbUrl2;
                        String key2;
                        String thumbKey;
                        ImageReceiver imageReceiver3;
                        String filter;
                        String httpLocation;
                        String thumbFilter;
                        int cacheType;
                        int cacheType2;
                        boolean imageSet = false;
                        String key3 = imageReceiver.getKey();
                        if (key3 != null) {
                            BitmapDrawable bitmapDrawable = imageLoader.memCache.get(key3);
                            if (bitmapDrawable != null) {
                                cancelLoadingForImageReceiver(imageReceiver2, 0);
                                imageReceiver2.setImageBitmapByKey(bitmapDrawable, key3, false, true);
                                imageSet = true;
                                if (!imageReceiver.isForcePreview()) {
                                    return;
                                }
                            }
                        }
                        boolean imageSet2 = imageSet;
                        imageSet = false;
                        String thumbKey2 = imageReceiver.getThumbKey();
                        if (thumbKey2 != null) {
                            BitmapDrawable bitmapDrawable2 = imageLoader.memCache.get(thumbKey2);
                            if (bitmapDrawable2 != null) {
                                imageReceiver2.setImageBitmapByKey(bitmapDrawable2, thumbKey2, true, true);
                                cancelLoadingForImageReceiver(imageReceiver2, 1);
                                if (!imageSet2 || !imageReceiver.isForcePreview()) {
                                    imageSet = true;
                                } else {
                                    return;
                                }
                            }
                        }
                        boolean thumbSet = imageSet;
                        TLObject thumbLocation = imageReceiver.getThumbLocation();
                        TLObject imageLocation2 = imageReceiver.getImageLocation();
                        String httpLocation2 = imageReceiver.getHttpImageLocation();
                        boolean saveImageToCache = false;
                        String url2 = null;
                        String thumbUrl3 = null;
                        key3 = null;
                        String ext = imageReceiver.getExt();
                        if (ext == null) {
                            ext = "jpg";
                        }
                        String ext2 = ext;
                        String url3;
                        if (httpLocation2 != null) {
                            key3 = Utilities.MD5(httpLocation2);
                            url3 = new StringBuilder();
                            url3.append(key3);
                            url3.append(".");
                            url3.append(getHttpUrlExtension(httpLocation2, "jpg"));
                            imageLocation = imageLocation2;
                            url = url3.toString();
                            ext = null;
                        } else {
                            if (imageLocation2 != null) {
                                String thumbKey3;
                                if (imageLocation2 instanceof FileLocation) {
                                    FileLocation location = (FileLocation) imageLocation2;
                                    StringBuilder stringBuilder2 = new StringBuilder();
                                    thumbKey3 = null;
                                    stringBuilder2.append(location.volume_id);
                                    stringBuilder2.append("_");
                                    stringBuilder2.append(location.local_id);
                                    key3 = stringBuilder2.toString();
                                    StringBuilder stringBuilder3 = new StringBuilder();
                                    stringBuilder3.append(key3);
                                    stringBuilder3.append(".");
                                    stringBuilder3.append(ext2);
                                    url2 = stringBuilder3.toString();
                                    if (!(imageReceiver.getExt() == null && location.key == null && (location.volume_id != -2147483648L || location.local_id >= 0))) {
                                        saveImageToCache = true;
                                    }
                                } else {
                                    thumbKey3 = null;
                                    StringBuilder stringBuilder4;
                                    if (imageLocation2 instanceof TL_webDocument) {
                                        TL_webDocument document = (TL_webDocument) imageLocation2;
                                        String defaultExt = FileLoader.getExtensionByMime(document.mime_type);
                                        key3 = Utilities.MD5(document.url);
                                        stringBuilder4 = new StringBuilder();
                                        stringBuilder4.append(key3);
                                        stringBuilder4.append(".");
                                        stringBuilder4.append(getHttpUrlExtension(document.url, defaultExt));
                                        url2 = stringBuilder4.toString();
                                    } else {
                                        if (imageLocation2 instanceof Document) {
                                            Document document2 = (Document) imageLocation2;
                                            key = null;
                                            if (document2.id == 0) {
                                                z = false;
                                                ext = thumbKey3;
                                            } else if (document2.dc_id == 0) {
                                                z = false;
                                                ext = thumbKey3;
                                            } else {
                                                boolean z2;
                                                StringBuilder stringBuilder5;
                                                if (document2.version == 0) {
                                                    stringBuilder5 = new StringBuilder();
                                                    stringBuilder5.append(document2.dc_id);
                                                    stringBuilder5.append("_");
                                                    z = false;
                                                    stringBuilder5.append(document2.id);
                                                    key3 = stringBuilder5.toString();
                                                } else {
                                                    z = false;
                                                    stringBuilder5 = new StringBuilder();
                                                    stringBuilder5.append(document2.dc_id);
                                                    stringBuilder5.append("_");
                                                    stringBuilder5.append(document2.id);
                                                    stringBuilder5.append("_");
                                                    stringBuilder5.append(document2.version);
                                                    key3 = stringBuilder5.toString();
                                                }
                                                url3 = FileLoader.getDocumentFileName(document2);
                                                if (url3 != null) {
                                                    boolean lastIndexOf = url3.lastIndexOf(46);
                                                    saveImageToCache = lastIndexOf;
                                                    if (!lastIndexOf) {
                                                        url3 = url3.substring(saveImageToCache);
                                                        if (url3.length() <= true) {
                                                            if (document2.mime_type == null && document2.mime_type.equals(MimeTypes.VIDEO_MP4)) {
                                                                url3 = ".mp4";
                                                            } else {
                                                                url3 = TtmlNode.ANONYMOUS_REGION_ID;
                                                            }
                                                        }
                                                        stringBuilder4 = new StringBuilder();
                                                        stringBuilder4.append(key3);
                                                        stringBuilder4.append(url3);
                                                        url2 = stringBuilder4.toString();
                                                        if (thumbKey3 == null) {
                                                            stringBuilder4 = new StringBuilder();
                                                            ext = thumbKey3;
                                                            stringBuilder4.append(ext);
                                                            stringBuilder4.append(".");
                                                            stringBuilder4.append(ext2);
                                                            thumbUrl3 = stringBuilder4.toString();
                                                        } else {
                                                            ext = thumbKey3;
                                                        }
                                                        z2 = MessageObject.isGifDocument(document2) && !MessageObject.isRoundVideoDocument((Document) imageLocation2);
                                                        saveImageToCache = z2;
                                                    }
                                                }
                                                url3 = TtmlNode.ANONYMOUS_REGION_ID;
                                                if (url3.length() <= true) {
                                                    if (document2.mime_type == null) {
                                                    }
                                                    url3 = TtmlNode.ANONYMOUS_REGION_ID;
                                                }
                                                stringBuilder4 = new StringBuilder();
                                                stringBuilder4.append(key3);
                                                stringBuilder4.append(url3);
                                                url2 = stringBuilder4.toString();
                                                if (thumbKey3 == null) {
                                                    ext = thumbKey3;
                                                } else {
                                                    stringBuilder4 = new StringBuilder();
                                                    ext = thumbKey3;
                                                    stringBuilder4.append(ext);
                                                    stringBuilder4.append(".");
                                                    stringBuilder4.append(ext2);
                                                    thumbUrl3 = stringBuilder4.toString();
                                                }
                                                if (MessageObject.isGifDocument(document2)) {
                                                }
                                                saveImageToCache = z2;
                                            }
                                            return;
                                        }
                                        key = null;
                                        z = false;
                                        ext = thumbKey3;
                                        if (imageLocation2 != thumbLocation) {
                                            key3 = null;
                                            imageLocation = null;
                                            url = null;
                                        } else {
                                            imageLocation = imageLocation2;
                                            z = saveImageToCache;
                                            url = url2;
                                        }
                                    }
                                }
                                ext = thumbKey3;
                                if (imageLocation2 != thumbLocation) {
                                    imageLocation = imageLocation2;
                                    z = saveImageToCache;
                                    url = url2;
                                } else {
                                    key3 = null;
                                    imageLocation = null;
                                    url = null;
                                }
                            } else {
                                key = null;
                                ext = null;
                                z = false;
                                imageLocation = imageLocation2;
                                url = null;
                            }
                            if (thumbLocation == null) {
                                stringBuilder = new StringBuilder();
                                stringBuilder.append(thumbLocation.volume_id);
                                stringBuilder.append("_");
                                stringBuilder.append(thumbLocation.local_id);
                                thumbKey2 = stringBuilder.toString();
                                thumbUrl = new StringBuilder();
                                thumbUrl.append(thumbKey2);
                                thumbUrl.append(".");
                                thumbUrl.append(ext2);
                                thumbUrl2 = thumbUrl.toString();
                            } else {
                                thumbUrl2 = thumbUrl3;
                                thumbKey2 = ext;
                            }
                            ext = imageReceiver.getFilter();
                            thumbUrl3 = imageReceiver.getThumbFilter();
                            if (key3 != null || ext == null) {
                                key2 = key3;
                            } else {
                                thumbUrl = new StringBuilder();
                                thumbUrl.append(key3);
                                thumbUrl.append("@");
                                thumbUrl.append(ext);
                                key2 = thumbUrl.toString();
                            }
                            if (thumbKey2 != null || thumbUrl3 == null) {
                                thumbKey = thumbKey2;
                            } else {
                                thumbUrl = new StringBuilder();
                                thumbUrl.append(thumbKey2);
                                thumbUrl.append("@");
                                thumbUrl.append(thumbUrl3);
                                thumbKey = thumbUrl.toString();
                            }
                            if (httpLocation2 == null) {
                                imageReceiver3 = imageReceiver2;
                                filter = ext;
                                key = ext2;
                                httpLocation = httpLocation2;
                                createLoadOperationForImageReceiver(imageReceiver3, thumbKey, thumbUrl2, ext2, thumbLocation, null, thumbUrl3, 0, 1, thumbSet ? 2 : true);
                                createLoadOperationForImageReceiver(imageReceiver3, key2, url, key, null, httpLocation, filter, 0, 1, 0);
                            } else {
                                thumbFilter = thumbUrl3;
                                filter = ext;
                                key = ext2;
                                httpLocation = httpLocation2;
                                cacheType = imageReceiver.getCacheType();
                                if (cacheType == 0 && saveImageToCache) {
                                    cacheType = 1;
                                }
                                cacheType2 = cacheType;
                                imageReceiver3 = imageReceiver2;
                                thumbKey2 = key;
                                createLoadOperationForImageReceiver(imageReceiver3, thumbKey, thumbUrl2, thumbKey2, thumbLocation, null, thumbFilter, 0, cacheType2 != 0 ? 1 : cacheType2, thumbSet ? 2 : 1);
                                createLoadOperationForImageReceiver(imageReceiver3, key2, url, thumbKey2, imageLocation, null, filter, imageReceiver.getSize(), cacheType2, 0);
                            }
                        }
                        z = saveImageToCache;
                        if (thumbLocation == null) {
                            thumbUrl2 = thumbUrl3;
                            thumbKey2 = ext;
                        } else {
                            stringBuilder = new StringBuilder();
                            stringBuilder.append(thumbLocation.volume_id);
                            stringBuilder.append("_");
                            stringBuilder.append(thumbLocation.local_id);
                            thumbKey2 = stringBuilder.toString();
                            thumbUrl = new StringBuilder();
                            thumbUrl.append(thumbKey2);
                            thumbUrl.append(".");
                            thumbUrl.append(ext2);
                            thumbUrl2 = thumbUrl.toString();
                        }
                        ext = imageReceiver.getFilter();
                        thumbUrl3 = imageReceiver.getThumbFilter();
                        if (key3 != null) {
                        }
                        key2 = key3;
                        if (thumbKey2 != null) {
                        }
                        thumbKey = thumbKey2;
                        if (httpLocation2 == null) {
                            thumbFilter = thumbUrl3;
                            filter = ext;
                            key = ext2;
                            httpLocation = httpLocation2;
                            cacheType = imageReceiver.getCacheType();
                            cacheType = 1;
                            cacheType2 = cacheType;
                            if (cacheType2 != 0) {
                            }
                            if (thumbSet) {
                            }
                            imageReceiver3 = imageReceiver2;
                            thumbKey2 = key;
                            createLoadOperationForImageReceiver(imageReceiver3, thumbKey, thumbUrl2, thumbKey2, thumbLocation, null, thumbFilter, 0, cacheType2 != 0 ? 1 : cacheType2, thumbSet ? 2 : 1);
                            createLoadOperationForImageReceiver(imageReceiver3, key2, url, thumbKey2, imageLocation, null, filter, imageReceiver.getSize(), cacheType2, 0);
                        } else {
                            if (thumbSet) {
                            }
                            imageReceiver3 = imageReceiver2;
                            filter = ext;
                            key = ext2;
                            httpLocation = httpLocation2;
                            createLoadOperationForImageReceiver(imageReceiver3, thumbKey, thumbUrl2, ext2, thumbLocation, null, thumbUrl3, 0, 1, thumbSet ? 2 : true);
                            createLoadOperationForImageReceiver(imageReceiver3, key2, url, key, null, httpLocation, filter, 0, 1, 0);
                        }
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
                                ImageLoader.this.imageLoadingByUrl.remove(location);
                                ArrayList<CacheOutTask> tasks = new ArrayList();
                                int a = 0;
                                for (int a2 = 0; a2 < img.imageReceiverArray.size(); a2++) {
                                    String key = (String) img.keys.get(a2);
                                    String filter = (String) img.filters.get(a2);
                                    Boolean thumb = (Boolean) img.thumbs.get(a2);
                                    ImageReceiver imageReceiver = (ImageReceiver) img.imageReceiverArray.get(a2);
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
                                while (a < tasks.size()) {
                                    CacheOutTask task = (CacheOutTask) tasks.get(a);
                                    if (task.cacheImage.selfThumb) {
                                        ImageLoader.this.cacheThumbOutQueue.postRunnable(task);
                                    } else {
                                        ImageLoader.this.cacheOutQueue.postRunnable(task);
                                    }
                                    a++;
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
                    if (!(url == null || url.length() == 0)) {
                        if (!this.httpFileLoadTasksByKeys.containsKey(url)) {
                            String ext = getHttpUrlExtension(url, defaultExt);
                            File directory = FileLoader.getDirectory(4);
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append(Utilities.MD5(url));
                            stringBuilder.append("_temp.");
                            stringBuilder.append(ext);
                            File file = new File(directory, stringBuilder.toString());
                            file.delete();
                            HttpFileTask task = new HttpFileTask(url, file, ext, currentAccount);
                            this.httpFileLoadTasks.add(task);
                            this.httpFileLoadTasksByKeys.put(url, task);
                            runHttpFileLoadTasks(null, 0);
                        }
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
                                        final HttpFileTask httpFileTask = new HttpFileTask(oldTask.url, oldTask.tempFile, oldTask.ext, oldTask.currentAccount);
                                        Runnable runnable = new Runnable() {
                                            public void run() {
                                                ImageLoader.this.httpFileLoadTasks.add(httpFileTask);
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
                                    File directory = FileLoader.getDirectory(4);
                                    StringBuilder stringBuilder = new StringBuilder();
                                    stringBuilder.append(Utilities.MD5(oldTask.url));
                                    stringBuilder.append(".");
                                    stringBuilder.append(oldTask.ext);
                                    File file = new File(directory, stringBuilder.toString());
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

                public static Bitmap loadBitmap(String path, Uri uri, float maxWidth, float maxHeight, boolean useMaxScale) {
                    String path2;
                    Throwable th;
                    float photoW;
                    float photoH;
                    float scaleFactor;
                    int sample;
                    String exifPath;
                    Matrix matrix;
                    int orientation;
                    Bitmap b;
                    Bitmap newBitmap;
                    Bitmap b2;
                    Throwable th2;
                    Uri uri2 = uri;
                    Options bmOptions = new Options();
                    bmOptions.inJustDecodeBounds = true;
                    InputStream inputStream = null;
                    if (!(path != null || uri2 == null || uri.getScheme() == null)) {
                        if (uri.getScheme().contains("file")) {
                            path2 = uri.getPath();
                        } else {
                            try {
                                path2 = AndroidUtilities.getPath(uri);
                            } catch (Throwable th3) {
                                FileLog.m3e(th3);
                            }
                        }
                        if (path2 != null) {
                            BitmapFactory.decodeFile(path2, bmOptions);
                        } else if (uri2 != null) {
                            boolean error = false;
                            try {
                                inputStream = ApplicationLoader.applicationContext.getContentResolver().openInputStream(uri2);
                                BitmapFactory.decodeStream(inputStream, null, bmOptions);
                                inputStream.close();
                                inputStream = ApplicationLoader.applicationContext.getContentResolver().openInputStream(uri2);
                            } catch (Throwable th32) {
                                FileLog.m3e(th32);
                                return null;
                            }
                        }
                        photoW = (float) bmOptions.outWidth;
                        photoH = (float) bmOptions.outHeight;
                        scaleFactor = useMaxScale ? Math.max(photoW / maxWidth, photoH / maxHeight) : Math.min(photoW / maxWidth, photoH / maxHeight);
                        if (scaleFactor < 1.0f) {
                            scaleFactor = 1.0f;
                        }
                        bmOptions.inJustDecodeBounds = false;
                        bmOptions.inSampleSize = (int) scaleFactor;
                        if (bmOptions.inSampleSize % 2 != 0) {
                            sample = 1;
                            while (sample * 2 < bmOptions.inSampleSize) {
                                sample *= 2;
                            }
                            bmOptions.inSampleSize = sample;
                        }
                        bmOptions.inPurgeable = VERSION.SDK_INT >= 21;
                        exifPath = null;
                        if (path2 != null) {
                            exifPath = path2;
                        } else if (uri2 != null) {
                            exifPath = AndroidUtilities.getPath(uri);
                        }
                        matrix = null;
                        if (exifPath != null) {
                            try {
                                orientation = new ExifInterface(exifPath).getAttributeInt("Orientation", 1);
                                matrix = new Matrix();
                                if (orientation != 3) {
                                    matrix.postRotate(180.0f);
                                } else if (orientation != 6) {
                                    matrix.postRotate(90.0f);
                                } else if (orientation != 8) {
                                    matrix.postRotate(270.0f);
                                }
                            } catch (Throwable th4) {
                            }
                        }
                        b = null;
                        if (path2 != null) {
                            try {
                                b = BitmapFactory.decodeFile(path2, bmOptions);
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
                            } catch (Throwable th322) {
                                b2 = newBitmap;
                                FileLog.m3e(th322);
                                b = b2;
                            }
                        } else if (uri2 != null) {
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
                                } catch (Throwable th3222) {
                                    FileLog.m3e(th3222);
                                }
                            } catch (Throwable th32222) {
                                FileLog.m3e(th32222);
                            }
                        }
                        return b;
                    }
                    path2 = path;
                    if (path2 != null) {
                        BitmapFactory.decodeFile(path2, bmOptions);
                    } else if (uri2 != null) {
                        boolean error2 = false;
                        inputStream = ApplicationLoader.applicationContext.getContentResolver().openInputStream(uri2);
                        BitmapFactory.decodeStream(inputStream, null, bmOptions);
                        inputStream.close();
                        inputStream = ApplicationLoader.applicationContext.getContentResolver().openInputStream(uri2);
                    }
                    photoW = (float) bmOptions.outWidth;
                    photoH = (float) bmOptions.outHeight;
                    if (useMaxScale) {
                    }
                    if (scaleFactor < 1.0f) {
                        scaleFactor = 1.0f;
                    }
                    bmOptions.inJustDecodeBounds = false;
                    bmOptions.inSampleSize = (int) scaleFactor;
                    if (bmOptions.inSampleSize % 2 != 0) {
                        sample = 1;
                        while (sample * 2 < bmOptions.inSampleSize) {
                            sample *= 2;
                        }
                        bmOptions.inSampleSize = sample;
                    }
                    if (VERSION.SDK_INT >= 21) {
                    }
                    bmOptions.inPurgeable = VERSION.SDK_INT >= 21;
                    exifPath = null;
                    if (path2 != null) {
                        exifPath = path2;
                    } else if (uri2 != null) {
                        exifPath = AndroidUtilities.getPath(uri);
                    }
                    matrix = null;
                    if (exifPath != null) {
                        orientation = new ExifInterface(exifPath).getAttributeInt("Orientation", 1);
                        matrix = new Matrix();
                        if (orientation != 3) {
                            matrix.postRotate(180.0f);
                        } else if (orientation != 6) {
                            matrix.postRotate(90.0f);
                        } else if (orientation != 8) {
                            matrix.postRotate(270.0f);
                        }
                    }
                    b = null;
                    if (path2 != null) {
                        b = BitmapFactory.decodeFile(path2, bmOptions);
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
                    } else if (uri2 != null) {
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
                        inputStream.close();
                    }
                    return b;
                    b = newBitmap;
                    return b;
                }

                public static void fillPhotoSizeWithBytes(PhotoSize photoSize) {
                    if (photoSize != null) {
                        if (photoSize.bytes == null) {
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
                }

                private static PhotoSize scaleAndSaveImageInternal(Bitmap bitmap, int w, int h, float photoW, float photoH, float scaleFactor, int quality, boolean cache, boolean scaleAnyway) throws Exception {
                    Bitmap scaledBitmap;
                    TL_fileLocation location;
                    PhotoSize size;
                    String fileName;
                    FileOutputStream stream;
                    Bitmap bitmap2 = bitmap;
                    int i = quality;
                    if (scaleFactor <= 1.0f) {
                        if (!scaleAnyway) {
                            int i2 = w;
                            int i3 = h;
                            scaledBitmap = bitmap2;
                            location = new TL_fileLocation();
                            location.volume_id = -2147483648L;
                            location.dc_id = Integer.MIN_VALUE;
                            location.local_id = SharedConfig.getLastLocalId();
                            size = new TL_photoSize();
                            size.location = location;
                            size.f43w = scaledBitmap.getWidth();
                            size.f42h = scaledBitmap.getHeight();
                            if (size.f43w > 100 && size.f42h <= 100) {
                                size.type = "s";
                            } else if (size.f43w > 320 && size.f42h <= 320) {
                                size.type = "m";
                            } else if (size.f43w > 800 && size.f42h <= 800) {
                                size.type = "x";
                            } else if (size.f43w <= 1280 || size.f42h > 1280) {
                                size.type = "w";
                            } else {
                                size.type = "y";
                            }
                            fileName = new StringBuilder();
                            fileName.append(location.volume_id);
                            fileName.append("_");
                            fileName.append(location.local_id);
                            fileName.append(".jpg");
                            stream = new FileOutputStream(new File(FileLoader.getDirectory(4), fileName.toString()));
                            scaledBitmap.compress(CompressFormat.JPEG, i, stream);
                            if (cache) {
                                size.size = (int) stream.getChannel().size();
                            } else {
                                ByteArrayOutputStream stream2 = new ByteArrayOutputStream();
                                scaledBitmap.compress(CompressFormat.JPEG, i, stream2);
                                size.bytes = stream2.toByteArray();
                                size.size = size.bytes.length;
                                stream2.close();
                            }
                            stream.close();
                            if (scaledBitmap != bitmap2) {
                                scaledBitmap.recycle();
                            }
                            return size;
                        }
                    }
                    scaledBitmap = Bitmaps.createScaledBitmap(bitmap2, w, h, true);
                    location = new TL_fileLocation();
                    location.volume_id = -2147483648L;
                    location.dc_id = Integer.MIN_VALUE;
                    location.local_id = SharedConfig.getLastLocalId();
                    size = new TL_photoSize();
                    size.location = location;
                    size.f43w = scaledBitmap.getWidth();
                    size.f42h = scaledBitmap.getHeight();
                    if (size.f43w > 100) {
                    }
                    if (size.f43w > 320) {
                    }
                    if (size.f43w > 800) {
                    }
                    if (size.f43w <= 1280) {
                    }
                    size.type = "w";
                    fileName = new StringBuilder();
                    fileName.append(location.volume_id);
                    fileName.append("_");
                    fileName.append(location.local_id);
                    fileName.append(".jpg");
                    stream = new FileOutputStream(new File(FileLoader.getDirectory(4), fileName.toString()));
                    scaledBitmap.compress(CompressFormat.JPEG, i, stream);
                    if (cache) {
                        size.size = (int) stream.getChannel().size();
                    } else {
                        ByteArrayOutputStream stream22 = new ByteArrayOutputStream();
                        scaledBitmap.compress(CompressFormat.JPEG, i, stream22);
                        size.bytes = stream22.toByteArray();
                        size.size = size.bytes.length;
                        stream22.close();
                    }
                    stream.close();
                    if (scaledBitmap != bitmap2) {
                        scaledBitmap.recycle();
                    }
                    return size;
                }

                public static PhotoSize scaleAndSaveImage(Bitmap bitmap, float maxWidth, float maxHeight, int quality, boolean cache) {
                    return scaleAndSaveImage(bitmap, maxWidth, maxHeight, quality, cache, 0, 0);
                }

                public static PhotoSize scaleAndSaveImage(Bitmap bitmap, float maxWidth, float maxHeight, int quality, boolean cache, int minWidth, int minHeight) {
                    int i;
                    int i2;
                    int i3 = minWidth;
                    int i4 = minHeight;
                    if (bitmap == null) {
                        return null;
                    }
                    float photoW = (float) bitmap.getWidth();
                    float photoH = (float) bitmap.getHeight();
                    if (photoW != 0.0f) {
                        if (photoH != 0.0f) {
                            boolean scaleAnyway = false;
                            float scaleFactor = Math.max(photoW / maxWidth, photoH / maxHeight);
                            if (!(i3 == 0 || i4 == 0 || (photoW >= ((float) i3) && photoH >= ((float) i4)))) {
                                if (photoW < ((float) i3) && photoH > ((float) i4)) {
                                    scaleFactor = photoW / ((float) i3);
                                } else if (photoW <= ((float) i3) || photoH >= ((float) i4)) {
                                    scaleFactor = Math.max(photoW / ((float) i3), photoH / ((float) i4));
                                } else {
                                    scaleFactor = photoH / ((float) i4);
                                }
                                scaleAnyway = true;
                            }
                            boolean scaleAnyway2 = scaleAnyway;
                            float scaleFactor2 = scaleFactor;
                            int w = (int) (photoW / scaleFactor2);
                            int h = (int) (photoH / scaleFactor2);
                            if (h == 0) {
                                i = w;
                            } else if (w == 0) {
                                i2 = h;
                                i = w;
                            } else {
                                i2 = h;
                                i = w;
                                try {
                                    return scaleAndSaveImageInternal(bitmap, w, h, photoW, photoH, scaleFactor2, quality, cache, scaleAnyway2);
                                } catch (Throwable th) {
                                    FileLog.m3e(th);
                                    return null;
                                }
                            }
                            return null;
                        }
                    }
                    return null;
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
                    Message message2 = message;
                    PhotoSize photoSize = null;
                    int count;
                    int a;
                    PhotoSize size;
                    if (message2.media instanceof TL_messageMediaPhoto) {
                        count = message2.media.photo.sizes.size();
                        for (a = 0; a < count; a++) {
                            size = (PhotoSize) message2.media.photo.sizes.get(a);
                            if (size instanceof TL_photoCachedSize) {
                                photoSize = size;
                                break;
                            }
                        }
                    } else if (message2.media instanceof TL_messageMediaDocument) {
                        if (message2.media.document.thumb instanceof TL_photoCachedSize) {
                            photoSize = message2.media.document.thumb;
                        }
                    } else if ((message2.media instanceof TL_messageMediaWebPage) && message2.media.webpage.photo != null) {
                        count = message2.media.webpage.photo.sizes.size();
                        for (a = 0; a < count; a++) {
                            size = (PhotoSize) message2.media.webpage.photo.sizes.get(a);
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
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append(file.getAbsolutePath());
                            stringBuilder.append(".enc");
                            file = new File(stringBuilder.toString());
                            isEncrypted = true;
                        }
                        if (!file.exists()) {
                            if (isEncrypted) {
                                try {
                                    File internalCacheDir = FileLoader.getInternalCacheDir();
                                    StringBuilder stringBuilder2 = new StringBuilder();
                                    stringBuilder2.append(file.getName());
                                    stringBuilder2.append(".key");
                                    RandomAccessFile keyFile = new RandomAccessFile(new File(internalCacheDir, stringBuilder2.toString()), "rws");
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
                            Exception e2 = new RandomAccessFile(file, "rws");
                            e2.write(photoSize.bytes);
                            e2.close();
                        }
                        TL_photoSize newPhotoSize = new TL_photoSize();
                        newPhotoSize.w = photoSize.f43w;
                        newPhotoSize.h = photoSize.f42h;
                        newPhotoSize.location = photoSize.location;
                        newPhotoSize.size = photoSize.size;
                        newPhotoSize.type = photoSize.type;
                        int count2;
                        int a2;
                        if (message2.media instanceof TL_messageMediaPhoto) {
                            count2 = message2.media.photo.sizes.size();
                            for (a2 = 0; a2 < count2; a2++) {
                                if (message2.media.photo.sizes.get(a2) instanceof TL_photoCachedSize) {
                                    message2.media.photo.sizes.set(a2, newPhotoSize);
                                    return;
                                }
                            }
                        } else if (message2.media instanceof TL_messageMediaDocument) {
                            message2.media.document.thumb = newPhotoSize;
                        } else if (message2.media instanceof TL_messageMediaWebPage) {
                            count2 = message2.media.webpage.photo.sizes.size();
                            for (a2 = 0; a2 < count2; a2++) {
                                if (message2.media.webpage.photo.sizes.get(a2) instanceof TL_photoCachedSize) {
                                    message2.media.webpage.photo.sizes.set(a2, newPhotoSize);
                                    return;
                                }
                            }
                        }
                    }
                }

                public static void saveMessagesThumbs(ArrayList<Message> messages) {
                    if (messages != null) {
                        if (!messages.isEmpty()) {
                            for (int a = 0; a < messages.size(); a++) {
                                saveMessageThumbs((Message) messages.get(a));
                            }
                        }
                    }
                }
            }
