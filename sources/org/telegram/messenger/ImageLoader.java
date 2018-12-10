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
import com.google.android.exoplayer2.CLASSNAMEC;
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
import org.json.JSONArray;
import org.json.JSONObject;
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

    /* renamed from: org.telegram.messenger.ImageLoader$3 */
    class CLASSNAME extends BroadcastReceiver {
        CLASSNAME() {
        }

        public void onReceive(Context arg0, Intent intent) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.m10d("file system changed");
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
        /* Code decompiled incorrectly, please refer to instructions dump. */
        protected String doInBackground(Void... voids) {
            Throwable e;
            Throwable th;
            ByteArrayOutputStream outbuf = null;
            InputStream httpConnectionStream = null;
            try {
                this.httpConnection = (HttpURLConnection) new URL(this.cacheImage.httpUrl.replace("athumb://", "https://")).openConnection();
                this.httpConnection.addRequestProperty("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 10_0 like Mac OS X) AppleWebKit/602.1.38 (KHTML, like Gecko) Version/10.0 Mobile/14A5297c Safari/602.1");
                this.httpConnection.setConnectTimeout(DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS);
                this.httpConnection.setReadTimeout(DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS);
                this.httpConnection.connect();
                if (this.httpConnection != null) {
                    int code = this.httpConnection.getResponseCode();
                    if (!(code == Callback.DEFAULT_DRAG_ANIMATION_DURATION || code == 202 || code == 304)) {
                        this.canRetry = false;
                    }
                }
            } catch (Throwable e2) {
                FileLog.m13e(e2);
            } catch (Throwable th2) {
                e2 = th2;
            }
            httpConnectionStream = this.httpConnection.getInputStream();
            ByteArrayOutputStream outbuf2 = new ByteArrayOutputStream();
            try {
                byte[] data = new byte[32768];
                while (!isCancelled()) {
                    int read = httpConnectionStream.read(data);
                    if (read > 0) {
                        outbuf2.write(data, 0, read);
                    } else if (read == -1) {
                    }
                }
                this.canRetry = false;
                JSONArray array = new JSONObject(new String(outbuf2.toByteArray(), CLASSNAMEC.UTF8_NAME)).getJSONArray("results");
                if (array.length() > 0) {
                    String artworkUrl100 = array.getJSONObject(0).getString("artworkUrl100");
                    if (this.small) {
                        try {
                            if (this.httpConnection != null) {
                                this.httpConnection.disconnect();
                            }
                        } catch (Throwable th3) {
                        }
                        if (httpConnectionStream != null) {
                            try {
                                httpConnectionStream.close();
                            } catch (Throwable e22) {
                                FileLog.m13e(e22);
                            }
                        }
                        if (outbuf2 != null) {
                            try {
                                outbuf2.close();
                            } catch (Exception e3) {
                            }
                        }
                        outbuf = outbuf2;
                        return artworkUrl100;
                    }
                    artworkUrl100 = artworkUrl100.replace("100x100", "600x600");
                    try {
                        if (this.httpConnection != null) {
                            this.httpConnection.disconnect();
                        }
                    } catch (Throwable th4) {
                    }
                    if (httpConnectionStream != null) {
                        try {
                            httpConnectionStream.close();
                        } catch (Throwable e222) {
                            FileLog.m13e(e222);
                        }
                    }
                    if (outbuf2 != null) {
                        try {
                            outbuf2.close();
                        } catch (Exception e4) {
                        }
                    }
                    outbuf = outbuf2;
                    return artworkUrl100;
                }
                try {
                    if (this.httpConnection != null) {
                        this.httpConnection.disconnect();
                    }
                } catch (Throwable th5) {
                }
                if (httpConnectionStream != null) {
                    try {
                        httpConnectionStream.close();
                    } catch (Throwable e2222) {
                        FileLog.m13e(e2222);
                    }
                }
                if (outbuf2 != null) {
                    try {
                        outbuf2.close();
                    } catch (Exception e5) {
                        outbuf = outbuf2;
                    }
                }
                outbuf = outbuf2;
                return null;
            } catch (Throwable th6) {
                th = th6;
                outbuf = outbuf2;
                try {
                    if (this.httpConnection != null) {
                        this.httpConnection.disconnect();
                    }
                } catch (Throwable th7) {
                }
                if (httpConnectionStream != null) {
                    try {
                        httpConnectionStream.close();
                    } catch (Throwable e22222) {
                        FileLog.m13e(e22222);
                    }
                }
                if (outbuf != null) {
                    try {
                        outbuf.close();
                    } catch (Exception e6) {
                    }
                }
                throw th;
            }
            if (httpConnectionStream != null) {
                try {
                    httpConnectionStream.close();
                } catch (Throwable e222222) {
                    FileLog.m13e(e222222);
                }
            }
            if (outbuf != null) {
                try {
                    outbuf.close();
                } catch (Exception e7) {
                }
            }
            return null;
            if (outbuf != null) {
            }
            return null;
            try {
                if (e222222 instanceof SocketTimeoutException) {
                    if (ApplicationLoader.isNetworkOnline()) {
                        this.canRetry = false;
                    }
                } else if (e222222 instanceof UnknownHostException) {
                    this.canRetry = false;
                } else if (e222222 instanceof SocketException) {
                    if (e222222.getMessage() != null && e222222.getMessage().contains("ECONNRESET")) {
                        this.canRetry = false;
                    }
                } else if (e222222 instanceof FileNotFoundException) {
                    this.canRetry = false;
                }
                FileLog.m13e(e222222);
                try {
                    if (this.httpConnection != null) {
                        this.httpConnection.disconnect();
                    }
                } catch (Throwable th8) {
                }
                if (httpConnectionStream != null) {
                }
                if (outbuf != null) {
                }
                return null;
            } catch (Throwable th9) {
                th = th9;
                if (this.httpConnection != null) {
                }
                if (httpConnectionStream != null) {
                }
                if (outbuf != null) {
                }
                throw th;
            }
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

        /* JADX WARNING: Removed duplicated region for block: B:110:0x01cd A:{SYNTHETIC, Splitter: B:110:0x01cd} */
        /* JADX WARNING: Removed duplicated region for block: B:446:0x0951  */
        /* JADX WARNING: Removed duplicated region for block: B:90:0x0187  */
        /* JADX WARNING: Missing block: B:8:0x001f, code:
            if (r61.cacheImage.animatedFile == false) goto L_0x0067;
     */
        /* JADX WARNING: Missing block: B:9:0x0021, code:
            r6 = r61.sync;
     */
        /* JADX WARNING: Missing block: B:10:0x0025, code:
            monitor-enter(r6);
     */
        /* JADX WARNING: Missing block: B:13:0x002a, code:
            if (r61.isCancelled == false) goto L_0x0034;
     */
        /* JADX WARNING: Missing block: B:14:0x002c, code:
            monitor-exit(r6);
     */
        /* JADX WARNING: Missing block: B:23:?, code:
            monitor-exit(r6);
     */
        /* JADX WARNING: Missing block: B:24:0x0035, code:
            r6 = r61.cacheImage.finalFilePath;
     */
        /* JADX WARNING: Missing block: B:25:0x0043, code:
            if (r61.cacheImage.filter == null) goto L_0x0065;
     */
        /* JADX WARNING: Missing block: B:27:0x0052, code:
            if (r61.cacheImage.filter.equals("d") == false) goto L_0x0065;
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
            r40 = null;
            r41 = false;
            r43 = false;
            r22 = r61.cacheImage.finalFilePath;
     */
        /* JADX WARNING: Missing block: B:32:0x007d, code:
            if (r61.cacheImage.secureDocument != null) goto L_0x0096;
     */
        /* JADX WARNING: Missing block: B:34:0x0085, code:
            if (r61.cacheImage.encryptionKeyPath == null) goto L_0x0197;
     */
        /* JADX WARNING: Missing block: B:35:0x0087, code:
            if (r22 == null) goto L_0x0197;
     */
        /* JADX WARNING: Missing block: B:37:0x0094, code:
            if (r22.getAbsolutePath().endsWith(".enc") == false) goto L_0x0197;
     */
        /* JADX WARNING: Missing block: B:38:0x0096, code:
            r37 = true;
     */
        /* JADX WARNING: Missing block: B:40:0x009e, code:
            if (r61.cacheImage.secureDocument == null) goto L_0x01a7;
     */
        /* JADX WARNING: Missing block: B:41:0x00a0, code:
            r56 = r61.cacheImage.secureDocument.secureDocumentKey;
     */
        /* JADX WARNING: Missing block: B:42:0x00b2, code:
            if (r61.cacheImage.secureDocument.secureFile == null) goto L_0x019b;
     */
        /* JADX WARNING: Missing block: B:44:0x00be, code:
            if (r61.cacheImage.secureDocument.secureFile.file_hash == null) goto L_0x019b;
     */
        /* JADX WARNING: Missing block: B:45:0x00c0, code:
            r55 = r61.cacheImage.secureDocument.secureFile.file_hash;
     */
        /* JADX WARNING: Missing block: B:46:0x00cc, code:
            r23 = true;
            r58 = false;
     */
        /* JADX WARNING: Missing block: B:47:0x00d4, code:
            if (android.os.Build.VERSION.SDK_INT >= 19) goto L_0x012a;
     */
        /* JADX WARNING: Missing block: B:48:0x00d6, code:
            r50 = null;
     */
        /* JADX WARNING: Missing block: B:50:?, code:
            r0 = new java.io.RandomAccessFile(r22, "r");
     */
        /* JADX WARNING: Missing block: B:53:0x00ea, code:
            if (r61.cacheImage.selfThumb == false) goto L_0x01ad;
     */
        /* JADX WARNING: Missing block: B:54:0x00ec, code:
            r21 = org.telegram.messenger.ImageLoader.access$1200();
     */
        /* JADX WARNING: Missing block: B:55:0x00f0, code:
            r0.readFully(r21, 0, r21.length);
            r57 = new java.lang.String(r21).toLowerCase().toLowerCase();
     */
        /* JADX WARNING: Missing block: B:56:0x0113, code:
            if (r57.startsWith("riff") == false) goto L_0x0122;
     */
        /* JADX WARNING: Missing block: B:58:0x011e, code:
            if (r57.endsWith("webp") == false) goto L_0x0122;
     */
        /* JADX WARNING: Missing block: B:59:0x0120, code:
            r58 = true;
     */
        /* JADX WARNING: Missing block: B:60:0x0122, code:
            r0.close();
     */
        /* JADX WARNING: Missing block: B:61:0x0125, code:
            if (r0 == null) goto L_0x012a;
     */
        /* JADX WARNING: Missing block: B:63:?, code:
            r0.close();
     */
        /* JADX WARNING: Missing block: B:65:0x0130, code:
            if (r61.cacheImage.selfThumb != false) goto L_0x0132;
     */
        /* JADX WARNING: Missing block: B:66:0x0132, code:
            r17 = 0;
            r24 = false;
     */
        /* JADX WARNING: Missing block: B:67:0x013c, code:
            if (r61.cacheImage.filter != null) goto L_0x013e;
     */
        /* JADX WARNING: Missing block: B:69:0x014b, code:
            if (r61.cacheImage.filter.contains("b2") != false) goto L_0x014d;
     */
        /* JADX WARNING: Missing block: B:70:0x014d, code:
            r17 = 3;
     */
        /* JADX WARNING: Missing block: B:72:0x015c, code:
            if (r61.cacheImage.filter.contains("i") != false) goto L_0x015e;
     */
        /* JADX WARNING: Missing block: B:73:0x015e, code:
            r24 = true;
     */
        /* JADX WARNING: Missing block: B:75:?, code:
            org.telegram.messenger.ImageLoader.access$1402(r61.this$0, java.lang.System.currentTimeMillis());
     */
        /* JADX WARNING: Missing block: B:76:0x016f, code:
            monitor-enter(r61.sync);
     */
        /* JADX WARNING: Missing block: B:79:0x0174, code:
            if (r61.isCancelled != false) goto L_0x0176;
     */
        /* JADX WARNING: Missing block: B:85:0x017c, code:
            r27 = th;
     */
        /* JADX WARNING: Missing block: B:86:0x017d, code:
            r4 = null;
     */
        /* JADX WARNING: Missing block: B:90:0x0187, code:
            if (r4 != null) goto L_0x0189;
     */
        /* JADX WARNING: Missing block: B:91:0x0189, code:
            r5 = new org.telegram.messenger.ExtendedBitmapDrawable(r4, r43);
     */
        /* JADX WARNING: Missing block: B:92:0x0190, code:
            onPostExecute(r5);
     */
        /* JADX WARNING: Missing block: B:93:0x0197, code:
            r37 = false;
     */
        /* JADX WARNING: Missing block: B:94:0x019b, code:
            r55 = r61.cacheImage.secureDocument.fileHash;
     */
        /* JADX WARNING: Missing block: B:95:0x01a7, code:
            r56 = null;
            r55 = null;
     */
        /* JADX WARNING: Missing block: B:97:?, code:
            r21 = org.telegram.messenger.ImageLoader.access$1300();
     */
        /* JADX WARNING: Missing block: B:98:0x01b3, code:
            r27 = move-exception;
     */
        /* JADX WARNING: Missing block: B:99:0x01b4, code:
            org.telegram.messenger.FileLog.m13e(r27);
     */
        /* JADX WARNING: Missing block: B:100:0x01b9, code:
            r27 = e;
     */
        /* JADX WARNING: Missing block: B:102:?, code:
            org.telegram.messenger.FileLog.m13e(r27);
     */
        /* JADX WARNING: Missing block: B:103:0x01bd, code:
            if (r50 != null) goto L_0x01bf;
     */
        /* JADX WARNING: Missing block: B:105:?, code:
            r50.close();
     */
        /* JADX WARNING: Missing block: B:106:0x01c4, code:
            r27 = move-exception;
     */
        /* JADX WARNING: Missing block: B:107:0x01c5, code:
            org.telegram.messenger.FileLog.m13e(r27);
     */
        /* JADX WARNING: Missing block: B:108:0x01ca, code:
            r5 = th;
     */
        /* JADX WARNING: Missing block: B:109:0x01cb, code:
            if (r50 != null) goto L_0x01cd;
     */
        /* JADX WARNING: Missing block: B:111:?, code:
            r50.close();
     */
        /* JADX WARNING: Missing block: B:112:0x01d0, code:
            throw r5;
     */
        /* JADX WARNING: Missing block: B:113:0x01d1, code:
            r27 = move-exception;
     */
        /* JADX WARNING: Missing block: B:114:0x01d2, code:
            org.telegram.messenger.FileLog.m13e(r27);
     */
        /* JADX WARNING: Missing block: B:116:0x01e3, code:
            if (r61.cacheImage.filter.contains("b1") != false) goto L_0x01e5;
     */
        /* JADX WARNING: Missing block: B:117:0x01e5, code:
            r17 = 2;
     */
        /* JADX WARNING: Missing block: B:119:0x01f6, code:
            if (r61.cacheImage.filter.contains("b") != false) goto L_0x01f8;
     */
        /* JADX WARNING: Missing block: B:120:0x01f8, code:
            r17 = 1;
     */
        /* JADX WARNING: Missing block: B:124:?, code:
            r45 = new android.graphics.BitmapFactory.Options();
            r45.inSampleSize = 1;
     */
        /* JADX WARNING: Missing block: B:125:0x020b, code:
            if (android.os.Build.VERSION.SDK_INT >= 21) goto L_0x0212;
     */
        /* JADX WARNING: Missing block: B:126:0x020d, code:
            r45.inPurgeable = true;
     */
        /* JADX WARNING: Missing block: B:127:0x0212, code:
            if (r58 == false) goto L_0x0287;
     */
        /* JADX WARNING: Missing block: B:128:0x0214, code:
            r0 = new java.io.RandomAccessFile(r22, "r");
            r20 = r0.getChannel().map(java.nio.channels.FileChannel.MapMode.READ_ONLY, 0, r22.length());
            r19 = new android.graphics.BitmapFactory.Options();
            r19.inJustDecodeBounds = true;
            org.telegram.messenger.Utilities.loadWebpImage(null, r20, r20.limit(), r19, true);
            r4 = org.telegram.messenger.Bitmaps.createBitmap(r19.outWidth, r19.outHeight, android.graphics.Bitmap.Config.ARGB_8888);
     */
        /* JADX WARNING: Missing block: B:130:?, code:
            r6 = r20.limit();
     */
        /* JADX WARNING: Missing block: B:131:0x025e, code:
            if (r45.inPurgeable != false) goto L_0x0285;
     */
        /* JADX WARNING: Missing block: B:132:0x0260, code:
            r5 = true;
     */
        /* JADX WARNING: Missing block: B:133:0x0261, code:
            org.telegram.messenger.Utilities.loadWebpImage(r4, r20, r6, null, r5);
            r0.close();
     */
        /* JADX WARNING: Missing block: B:134:0x0269, code:
            if (r4 != null) goto L_0x034a;
     */
        /* JADX WARNING: Missing block: B:136:0x0273, code:
            if (r22.length() == 0) goto L_0x027d;
     */
        /* JADX WARNING: Missing block: B:138:0x027b, code:
            if (r61.cacheImage.filter != null) goto L_0x0182;
     */
        /* JADX WARNING: Missing block: B:139:0x027d, code:
            r22.delete();
     */
        /* JADX WARNING: Missing block: B:140:0x0282, code:
            r27 = th;
     */
        /* JADX WARNING: Missing block: B:141:0x0285, code:
            r5 = false;
     */
        /* JADX WARNING: Missing block: B:144:0x028b, code:
            if (r45.inPurgeable != false) goto L_0x028f;
     */
        /* JADX WARNING: Missing block: B:145:0x028d, code:
            if (r56 == null) goto L_0x0321;
     */
        /* JADX WARNING: Missing block: B:146:0x028f, code:
            r0 = new java.io.RandomAccessFile(r22, "r");
            r39 = (int) r0.length();
            r44 = 0;
     */
        /* JADX WARNING: Missing block: B:147:0x02a8, code:
            if (org.telegram.messenger.ImageLoader.access$1500() == null) goto L_0x030d;
     */
        /* JADX WARNING: Missing block: B:149:0x02b1, code:
            if (org.telegram.messenger.ImageLoader.access$1500().length < r39) goto L_0x030d;
     */
        /* JADX WARNING: Missing block: B:150:0x02b3, code:
            r25 = org.telegram.messenger.ImageLoader.access$1500();
     */
        /* JADX WARNING: Missing block: B:151:0x02b7, code:
            if (r25 != null) goto L_0x02c2;
     */
        /* JADX WARNING: Missing block: B:152:0x02b9, code:
            r25 = new byte[r39];
            org.telegram.messenger.ImageLoader.access$1502(r25);
     */
        /* JADX WARNING: Missing block: B:153:0x02c2, code:
            r0.readFully(r25, 0, r39);
            r0.close();
            r28 = false;
     */
        /* JADX WARNING: Missing block: B:154:0x02d1, code:
            if (r56 == null) goto L_0x0310;
     */
        /* JADX WARNING: Missing block: B:155:0x02d3, code:
            org.telegram.messenger.secretmedia.EncryptedFileInputStream.decryptBytesWithKeyFile(r25, 0, r39, r56);
            r34 = org.telegram.messenger.Utilities.computeSHA256(r25, 0, r39);
     */
        /* JADX WARNING: Missing block: B:156:0x02e6, code:
            if (r55 == null) goto L_0x02f2;
     */
        /* JADX WARNING: Missing block: B:158:0x02f0, code:
            if (java.util.Arrays.equals(r34, r55) != false) goto L_0x02f4;
     */
        /* JADX WARNING: Missing block: B:159:0x02f2, code:
            r28 = true;
     */
        /* JADX WARNING: Missing block: B:160:0x02f4, code:
            r44 = r25[0] & 255;
            r39 = r39 - r44;
     */
        /* JADX WARNING: Missing block: B:161:0x02fd, code:
            if (r28 != false) goto L_0x0973;
     */
        /* JADX WARNING: Missing block: B:162:0x02ff, code:
            r4 = android.graphics.BitmapFactory.decodeByteArray(r25, r44, r39, r45);
     */
        /* JADX WARNING: Missing block: B:163:0x030d, code:
            r25 = null;
     */
        /* JADX WARNING: Missing block: B:164:0x0310, code:
            if (r37 == false) goto L_0x02fd;
     */
        /* JADX WARNING: Missing block: B:165:0x0312, code:
            org.telegram.messenger.secretmedia.EncryptedFileInputStream.decryptBytesWithKeyFile(r25, 0, r39, r61.cacheImage.encryptionKeyPath);
     */
        /* JADX WARNING: Missing block: B:166:0x0321, code:
            if (r37 == false) goto L_0x0340;
     */
        /* JADX WARNING: Missing block: B:167:0x0323, code:
            r0 = new org.telegram.messenger.secretmedia.EncryptedFileInputStream(r22, r61.cacheImage.encryptionKeyPath);
     */
        /* JADX WARNING: Missing block: B:168:0x0332, code:
            r4 = android.graphics.BitmapFactory.decodeStream(r38, null, r45);
     */
        /* JADX WARNING: Missing block: B:170:?, code:
            r38.close();
     */
        /* JADX WARNING: Missing block: B:172:?, code:
            r0 = new java.io.FileInputStream(r22);
     */
        /* JADX WARNING: Missing block: B:173:0x034a, code:
            if (r24 == false) goto L_0x0367;
     */
        /* JADX WARNING: Missing block: B:176:0x0350, code:
            if (r45.inPurgeable == false) goto L_0x038d;
     */
        /* JADX WARNING: Missing block: B:177:0x0352, code:
            r5 = 0;
     */
        /* JADX WARNING: Missing block: B:179:0x0363, code:
            if (org.telegram.messenger.Utilities.needInvert(r4, r5, r4.getWidth(), r4.getHeight(), r4.getRowBytes()) == 0) goto L_0x038f;
     */
        /* JADX WARNING: Missing block: B:180:0x0365, code:
            r43 = true;
     */
        /* JADX WARNING: Missing block: B:182:0x036a, code:
            if (r17 != 1) goto L_0x0394;
     */
        /* JADX WARNING: Missing block: B:184:0x0372, code:
            if (r4.getConfig() != android.graphics.Bitmap.Config.ARGB_8888) goto L_0x0182;
     */
        /* JADX WARNING: Missing block: B:186:0x0379, code:
            if (r45.inPurgeable == false) goto L_0x0392;
     */
        /* JADX WARNING: Missing block: B:187:0x037b, code:
            r6 = 0;
     */
        /* JADX WARNING: Missing block: B:188:0x037c, code:
            org.telegram.messenger.Utilities.blurBitmap(r4, 3, r6, r4.getWidth(), r4.getHeight(), r4.getRowBytes());
     */
        /* JADX WARNING: Missing block: B:189:0x038d, code:
            r5 = 1;
     */
        /* JADX WARNING: Missing block: B:190:0x038f, code:
            r43 = false;
     */
        /* JADX WARNING: Missing block: B:191:0x0392, code:
            r6 = 1;
     */
        /* JADX WARNING: Missing block: B:193:0x0397, code:
            if (r17 != 2) goto L_0x03bc;
     */
        /* JADX WARNING: Missing block: B:195:0x039f, code:
            if (r4.getConfig() != android.graphics.Bitmap.Config.ARGB_8888) goto L_0x0182;
     */
        /* JADX WARNING: Missing block: B:197:0x03a6, code:
            if (r45.inPurgeable == false) goto L_0x03ba;
     */
        /* JADX WARNING: Missing block: B:198:0x03a8, code:
            r6 = 0;
     */
        /* JADX WARNING: Missing block: B:199:0x03a9, code:
            org.telegram.messenger.Utilities.blurBitmap(r4, 1, r6, r4.getWidth(), r4.getHeight(), r4.getRowBytes());
     */
        /* JADX WARNING: Missing block: B:200:0x03ba, code:
            r6 = 1;
     */
        /* JADX WARNING: Missing block: B:202:0x03bf, code:
            if (r17 != 3) goto L_0x0416;
     */
        /* JADX WARNING: Missing block: B:204:0x03c7, code:
            if (r4.getConfig() != android.graphics.Bitmap.Config.ARGB_8888) goto L_0x0182;
     */
        /* JADX WARNING: Missing block: B:206:0x03ce, code:
            if (r45.inPurgeable == false) goto L_0x0410;
     */
        /* JADX WARNING: Missing block: B:207:0x03d0, code:
            r6 = 0;
     */
        /* JADX WARNING: Missing block: B:208:0x03d1, code:
            org.telegram.messenger.Utilities.blurBitmap(r4, 7, r6, r4.getWidth(), r4.getHeight(), r4.getRowBytes());
     */
        /* JADX WARNING: Missing block: B:209:0x03e5, code:
            if (r45.inPurgeable == false) goto L_0x0412;
     */
        /* JADX WARNING: Missing block: B:210:0x03e7, code:
            r6 = 0;
     */
        /* JADX WARNING: Missing block: B:211:0x03e8, code:
            org.telegram.messenger.Utilities.blurBitmap(r4, 7, r6, r4.getWidth(), r4.getHeight(), r4.getRowBytes());
     */
        /* JADX WARNING: Missing block: B:212:0x03fc, code:
            if (r45.inPurgeable == false) goto L_0x0414;
     */
        /* JADX WARNING: Missing block: B:213:0x03fe, code:
            r6 = 0;
     */
        /* JADX WARNING: Missing block: B:214:0x03ff, code:
            org.telegram.messenger.Utilities.blurBitmap(r4, 7, r6, r4.getWidth(), r4.getHeight(), r4.getRowBytes());
     */
        /* JADX WARNING: Missing block: B:215:0x0410, code:
            r6 = 1;
     */
        /* JADX WARNING: Missing block: B:216:0x0412, code:
            r6 = 1;
     */
        /* JADX WARNING: Missing block: B:217:0x0414, code:
            r6 = 1;
     */
        /* JADX WARNING: Missing block: B:218:0x0416, code:
            if (r17 != 0) goto L_0x0182;
     */
        /* JADX WARNING: Missing block: B:220:0x041c, code:
            if (r45.inPurgeable == false) goto L_0x0182;
     */
        /* JADX WARNING: Missing block: B:221:0x041e, code:
            org.telegram.messenger.Utilities.pinBitmap(r4);
     */
        /* JADX WARNING: Missing block: B:222:0x0423, code:
            r42 = null;
     */
        /* JADX WARNING: Missing block: B:225:0x042b, code:
            if (r61.cacheImage.httpUrl != null) goto L_0x042d;
     */
        /* JADX WARNING: Missing block: B:227:0x043a, code:
            if (r61.cacheImage.httpUrl.startsWith("thumb://") != false) goto L_0x043c;
     */
        /* JADX WARNING: Missing block: B:228:0x043c, code:
            r35 = r61.cacheImage.httpUrl.indexOf(":", 8);
     */
        /* JADX WARNING: Missing block: B:229:0x044b, code:
            if (r35 >= 0) goto L_0x044d;
     */
        /* JADX WARNING: Missing block: B:230:0x044d, code:
            r40 = java.lang.Long.valueOf(java.lang.Long.parseLong(r61.cacheImage.httpUrl.substring(8, r35)));
            r41 = false;
            r42 = r61.cacheImage.httpUrl.substring(r35 + 1);
     */
        /* JADX WARNING: Missing block: B:231:0x0471, code:
            r23 = false;
     */
        /* JADX WARNING: Missing block: B:232:0x0473, code:
            r26 = 20;
     */
        /* JADX WARNING: Missing block: B:233:0x0475, code:
            if (r40 != null) goto L_0x0477;
     */
        /* JADX WARNING: Missing block: B:234:0x0477, code:
            r26 = 0;
     */
        /* JADX WARNING: Missing block: B:242:0x04a3, code:
            java.lang.Thread.sleep((long) r26);
     */
        /* JADX WARNING: Missing block: B:243:0x04a9, code:
            org.telegram.messenger.ImageLoader.access$1402(r61.this$0, java.lang.System.currentTimeMillis());
     */
        /* JADX WARNING: Missing block: B:244:0x04b8, code:
            monitor-enter(r61.sync);
     */
        /* JADX WARNING: Missing block: B:247:0x04bd, code:
            if (r61.isCancelled != false) goto L_0x04bf;
     */
        /* JADX WARNING: Missing block: B:254:0x04c6, code:
            r4 = null;
     */
        /* JADX WARNING: Missing block: B:256:0x04d7, code:
            if (r61.cacheImage.httpUrl.startsWith("vthumb://") != false) goto L_0x04d9;
     */
        /* JADX WARNING: Missing block: B:257:0x04d9, code:
            r35 = r61.cacheImage.httpUrl.indexOf(":", 9);
     */
        /* JADX WARNING: Missing block: B:258:0x04e8, code:
            if (r35 >= 0) goto L_0x04ea;
     */
        /* JADX WARNING: Missing block: B:259:0x04ea, code:
            r40 = java.lang.Long.valueOf(java.lang.Long.parseLong(r61.cacheImage.httpUrl.substring(9, r35)));
            r41 = true;
     */
        /* JADX WARNING: Missing block: B:260:0x0502, code:
            r23 = false;
     */
        /* JADX WARNING: Missing block: B:262:0x0513, code:
            if (r61.cacheImage.httpUrl.startsWith("http") == false) goto L_0x0515;
     */
        /* JADX WARNING: Missing block: B:263:0x0515, code:
            r23 = false;
     */
        /* JADX WARNING: Missing block: B:267:?, code:
            r45 = new android.graphics.BitmapFactory.Options();
            r45.inSampleSize = 1;
            r60 = 0.0f;
            r33 = 0.0f;
            r16 = false;
            r24 = false;
     */
        /* JADX WARNING: Missing block: B:268:0x0532, code:
            if (r61.cacheImage.filter == null) goto L_0x069d;
     */
        /* JADX WARNING: Missing block: B:269:0x0534, code:
            r12 = r61.cacheImage.filter.split("_");
     */
        /* JADX WARNING: Missing block: B:270:0x0543, code:
            if (r12.length < 2) goto L_0x055b;
     */
        /* JADX WARNING: Missing block: B:271:0x0545, code:
            r60 = java.lang.Float.parseFloat(r12[0]) * org.telegram.messenger.AndroidUtilities.density;
            r33 = java.lang.Float.parseFloat(r12[1]) * org.telegram.messenger.AndroidUtilities.density;
     */
        /* JADX WARNING: Missing block: B:273:0x0568, code:
            if (r61.cacheImage.filter.contains("b") == false) goto L_0x056c;
     */
        /* JADX WARNING: Missing block: B:274:0x056a, code:
            r16 = true;
     */
        /* JADX WARNING: Missing block: B:276:0x0579, code:
            if (r61.cacheImage.filter.contains("i") == false) goto L_0x057d;
     */
        /* JADX WARNING: Missing block: B:277:0x057b, code:
            r24 = true;
     */
        /* JADX WARNING: Missing block: B:279:0x0580, code:
            if (r60 == 0.0f) goto L_0x096b;
     */
        /* JADX WARNING: Missing block: B:281:0x0585, code:
            if (r33 == 0.0f) goto L_0x096b;
     */
        /* JADX WARNING: Missing block: B:282:0x0587, code:
            r45.inJustDecodeBounds = true;
     */
        /* JADX WARNING: Missing block: B:283:0x058c, code:
            if (r40 == null) goto L_0x05f5;
     */
        /* JADX WARNING: Missing block: B:284:0x058e, code:
            if (r42 != null) goto L_0x05f5;
     */
        /* JADX WARNING: Missing block: B:285:0x0590, code:
            if (r41 == false) goto L_0x05e2;
     */
        /* JADX WARNING: Missing block: B:286:0x0592, code:
            android.provider.MediaStore.Video.Thumbnails.getThumbnail(org.telegram.messenger.ApplicationLoader.applicationContext.getContentResolver(), r40.longValue(), 1, r45);
     */
        /* JADX WARNING: Missing block: B:287:0x05a2, code:
            r4 = null;
     */
        /* JADX WARNING: Missing block: B:289:?, code:
            r53 = java.lang.Math.max(((float) r45.outWidth) / r60, ((float) r45.outHeight) / r33);
     */
        /* JADX WARNING: Missing block: B:290:0x05be, code:
            if (r53 >= 1.0f) goto L_0x05c2;
     */
        /* JADX WARNING: Missing block: B:291:0x05c0, code:
            r53 = 1.0f;
     */
        /* JADX WARNING: Missing block: B:292:0x05c2, code:
            r45.inJustDecodeBounds = false;
            r45.inSampleSize = (int) r53;
     */
        /* JADX WARNING: Missing block: B:293:0x05ce, code:
            r6 = r61.sync;
     */
        /* JADX WARNING: Missing block: B:294:0x05d2, code:
            monitor-enter(r6);
     */
        /* JADX WARNING: Missing block: B:297:0x05d7, code:
            if (r61.isCancelled == false) goto L_0x06fa;
     */
        /* JADX WARNING: Missing block: B:298:0x05d9, code:
            monitor-exit(r6);
     */
        /* JADX WARNING: Missing block: B:305:?, code:
            android.provider.MediaStore.Images.Thumbnails.getThumbnail(org.telegram.messenger.ApplicationLoader.applicationContext.getContentResolver(), r40.longValue(), 1, r45);
            r4 = null;
     */
        /* JADX WARNING: Missing block: B:306:0x05f5, code:
            if (r56 == null) goto L_0x0674;
     */
        /* JADX WARNING: Missing block: B:307:0x05f7, code:
            r0 = new java.io.RandomAccessFile(r22, "r");
            r39 = (int) r0.length();
     */
        /* JADX WARNING: Missing block: B:308:0x060e, code:
            if (org.telegram.messenger.ImageLoader.access$1600() == null) goto L_0x0671;
     */
        /* JADX WARNING: Missing block: B:310:0x0617, code:
            if (org.telegram.messenger.ImageLoader.access$1600().length < r39) goto L_0x0671;
     */
        /* JADX WARNING: Missing block: B:311:0x0619, code:
            r25 = org.telegram.messenger.ImageLoader.access$1600();
     */
        /* JADX WARNING: Missing block: B:312:0x061d, code:
            if (r25 != null) goto L_0x0628;
     */
        /* JADX WARNING: Missing block: B:313:0x061f, code:
            r25 = new byte[r39];
            org.telegram.messenger.ImageLoader.access$1602(r25);
     */
        /* JADX WARNING: Missing block: B:314:0x0628, code:
            r0.readFully(r25, 0, r39);
            r0.close();
            org.telegram.messenger.secretmedia.EncryptedFileInputStream.decryptBytesWithKeyFile(r25, 0, r39, r56);
            r34 = org.telegram.messenger.Utilities.computeSHA256(r25, 0, r39);
            r28 = false;
     */
        /* JADX WARNING: Missing block: B:315:0x064a, code:
            if (r55 == null) goto L_0x0656;
     */
        /* JADX WARNING: Missing block: B:317:0x0654, code:
            if (java.util.Arrays.equals(r34, r55) != false) goto L_0x0658;
     */
        /* JADX WARNING: Missing block: B:318:0x0656, code:
            r28 = true;
     */
        /* JADX WARNING: Missing block: B:319:0x0658, code:
            r44 = r25[0] & 255;
            r39 = r39 - r44;
     */
        /* JADX WARNING: Missing block: B:320:0x0661, code:
            if (r28 != false) goto L_0x096f;
     */
        /* JADX WARNING: Missing block: B:321:0x0663, code:
            r4 = android.graphics.BitmapFactory.decodeByteArray(r25, r44, r39, r45);
     */
        /* JADX WARNING: Missing block: B:322:0x0671, code:
            r25 = null;
     */
        /* JADX WARNING: Missing block: B:323:0x0674, code:
            if (r37 == false) goto L_0x0693;
     */
        /* JADX WARNING: Missing block: B:324:0x0676, code:
            r0 = new org.telegram.messenger.secretmedia.EncryptedFileInputStream(r22, r61.cacheImage.encryptionKeyPath);
     */
        /* JADX WARNING: Missing block: B:325:0x0685, code:
            r4 = android.graphics.BitmapFactory.decodeStream(r38, null, r45);
     */
        /* JADX WARNING: Missing block: B:327:?, code:
            r38.close();
     */
        /* JADX WARNING: Missing block: B:329:?, code:
            r0 = new java.io.FileInputStream(r22);
     */
        /* JADX WARNING: Missing block: B:330:0x069d, code:
            if (r42 == null) goto L_0x096b;
     */
        /* JADX WARNING: Missing block: B:331:0x069f, code:
            r45.inJustDecodeBounds = true;
            r45.inPreferredConfig = android.graphics.Bitmap.Config.RGB_565;
            r0 = new java.io.FileInputStream(r22);
            r4 = android.graphics.BitmapFactory.decodeStream(r0, null, r45);
     */
        /* JADX WARNING: Missing block: B:333:?, code:
            r0.close();
            r49 = r45.outWidth;
            r47 = r45.outHeight;
            r45.inJustDecodeBounds = false;
            r53 = (float) java.lang.Math.max(r49 / org.telegram.messenger.support.widget.helper.ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION, r47 / org.telegram.messenger.support.widget.helper.ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION);
     */
        /* JADX WARNING: Missing block: B:334:0x06e3, code:
            if (r53 >= 1.0f) goto L_0x06e7;
     */
        /* JADX WARNING: Missing block: B:335:0x06e5, code:
            r53 = 1.0f;
     */
        /* JADX WARNING: Missing block: B:336:0x06e7, code:
            r52 = 1;
     */
        /* JADX WARNING: Missing block: B:337:0x06e9, code:
            r52 = r52 * 2;
     */
        /* JADX WARNING: Missing block: B:338:0x06f0, code:
            if (((float) (r52 * 2)) < r53) goto L_0x06e9;
     */
        /* JADX WARNING: Missing block: B:339:0x06f2, code:
            r45.inSampleSize = r52;
     */
        /* JADX WARNING: Missing block: B:341:?, code:
            monitor-exit(r6);
     */
        /* JADX WARNING: Missing block: B:344:0x0701, code:
            if (r61.cacheImage.filter == null) goto L_0x070d;
     */
        /* JADX WARNING: Missing block: B:345:0x0703, code:
            if (r16 != false) goto L_0x070d;
     */
        /* JADX WARNING: Missing block: B:347:0x070b, code:
            if (r61.cacheImage.httpUrl == null) goto L_0x07ae;
     */
        /* JADX WARNING: Missing block: B:348:0x070d, code:
            r45.inPreferredConfig = android.graphics.Bitmap.Config.ARGB_8888;
     */
        /* JADX WARNING: Missing block: B:350:0x0717, code:
            if (android.os.Build.VERSION.SDK_INT >= 21) goto L_0x071e;
     */
        /* JADX WARNING: Missing block: B:351:0x0719, code:
            r45.inPurgeable = true;
     */
        /* JADX WARNING: Missing block: B:352:0x071e, code:
            r45.inDither = false;
     */
        /* JADX WARNING: Missing block: B:353:0x0723, code:
            if (r40 == null) goto L_0x073a;
     */
        /* JADX WARNING: Missing block: B:354:0x0725, code:
            if (r42 != null) goto L_0x073a;
     */
        /* JADX WARNING: Missing block: B:355:0x0727, code:
            if (r41 == false) goto L_0x07b6;
     */
        /* JADX WARNING: Missing block: B:356:0x0729, code:
            r4 = android.provider.MediaStore.Video.Thumbnails.getThumbnail(org.telegram.messenger.ApplicationLoader.applicationContext.getContentResolver(), r40.longValue(), 1, r45);
     */
        /* JADX WARNING: Missing block: B:357:0x073a, code:
            if (r4 != null) goto L_0x0793;
     */
        /* JADX WARNING: Missing block: B:358:0x073c, code:
            if (r58 == false) goto L_0x07cb;
     */
        /* JADX WARNING: Missing block: B:359:0x073e, code:
            r0 = new java.io.RandomAccessFile(r22, "r");
            r20 = r0.getChannel().map(java.nio.channels.FileChannel.MapMode.READ_ONLY, 0, r22.length());
            r19 = new android.graphics.BitmapFactory.Options();
            r19.inJustDecodeBounds = true;
            org.telegram.messenger.Utilities.loadWebpImage(null, r20, r20.limit(), r19, true);
            r4 = org.telegram.messenger.Bitmaps.createBitmap(r19.outWidth, r19.outHeight, android.graphics.Bitmap.Config.ARGB_8888);
            r6 = r20.limit();
     */
        /* JADX WARNING: Missing block: B:360:0x0788, code:
            if (r45.inPurgeable != false) goto L_0x07c9;
     */
        /* JADX WARNING: Missing block: B:361:0x078a, code:
            r5 = true;
     */
        /* JADX WARNING: Missing block: B:362:0x078b, code:
            org.telegram.messenger.Utilities.loadWebpImage(r4, r20, r6, null, r5);
            r0.close();
     */
        /* JADX WARNING: Missing block: B:363:0x0793, code:
            if (r4 != null) goto L_0x088e;
     */
        /* JADX WARNING: Missing block: B:364:0x0795, code:
            if (r23 == false) goto L_0x0182;
     */
        /* JADX WARNING: Missing block: B:366:0x079f, code:
            if (r22.length() == 0) goto L_0x07a9;
     */
        /* JADX WARNING: Missing block: B:368:0x07a7, code:
            if (r61.cacheImage.filter != null) goto L_0x0182;
     */
        /* JADX WARNING: Missing block: B:369:0x07a9, code:
            r22.delete();
     */
        /* JADX WARNING: Missing block: B:370:0x07ae, code:
            r45.inPreferredConfig = android.graphics.Bitmap.Config.RGB_565;
     */
        /* JADX WARNING: Missing block: B:371:0x07b6, code:
            r4 = android.provider.MediaStore.Images.Thumbnails.getThumbnail(org.telegram.messenger.ApplicationLoader.applicationContext.getContentResolver(), r40.longValue(), 1, r45);
     */
        /* JADX WARNING: Missing block: B:372:0x07c9, code:
            r5 = false;
     */
        /* JADX WARNING: Missing block: B:374:0x07cf, code:
            if (r45.inPurgeable != false) goto L_0x07d3;
     */
        /* JADX WARNING: Missing block: B:375:0x07d1, code:
            if (r56 == null) goto L_0x0865;
     */
        /* JADX WARNING: Missing block: B:376:0x07d3, code:
            r0 = new java.io.RandomAccessFile(r22, "r");
            r39 = (int) r0.length();
            r44 = 0;
     */
        /* JADX WARNING: Missing block: B:377:0x07ec, code:
            if (org.telegram.messenger.ImageLoader.access$1600() == null) goto L_0x0851;
     */
        /* JADX WARNING: Missing block: B:379:0x07f5, code:
            if (org.telegram.messenger.ImageLoader.access$1600().length < r39) goto L_0x0851;
     */
        /* JADX WARNING: Missing block: B:380:0x07f7, code:
            r25 = org.telegram.messenger.ImageLoader.access$1600();
     */
        /* JADX WARNING: Missing block: B:381:0x07fb, code:
            if (r25 != null) goto L_0x0806;
     */
        /* JADX WARNING: Missing block: B:382:0x07fd, code:
            r25 = new byte[r39];
            org.telegram.messenger.ImageLoader.access$1602(r25);
     */
        /* JADX WARNING: Missing block: B:383:0x0806, code:
            r0.readFully(r25, 0, r39);
            r0.close();
            r28 = false;
     */
        /* JADX WARNING: Missing block: B:384:0x0815, code:
            if (r56 == null) goto L_0x0854;
     */
        /* JADX WARNING: Missing block: B:385:0x0817, code:
            org.telegram.messenger.secretmedia.EncryptedFileInputStream.decryptBytesWithKeyFile(r25, 0, r39, r56);
            r34 = org.telegram.messenger.Utilities.computeSHA256(r25, 0, r39);
     */
        /* JADX WARNING: Missing block: B:386:0x082a, code:
            if (r55 == null) goto L_0x0836;
     */
        /* JADX WARNING: Missing block: B:388:0x0834, code:
            if (java.util.Arrays.equals(r34, r55) != false) goto L_0x0838;
     */
        /* JADX WARNING: Missing block: B:389:0x0836, code:
            r28 = true;
     */
        /* JADX WARNING: Missing block: B:390:0x0838, code:
            r44 = r25[0] & 255;
            r39 = r39 - r44;
     */
        /* JADX WARNING: Missing block: B:391:0x0841, code:
            if (r28 != false) goto L_0x0793;
     */
        /* JADX WARNING: Missing block: B:392:0x0843, code:
            r4 = android.graphics.BitmapFactory.decodeByteArray(r25, r44, r39, r45);
     */
        /* JADX WARNING: Missing block: B:393:0x0851, code:
            r25 = null;
     */
        /* JADX WARNING: Missing block: B:394:0x0854, code:
            if (r37 == false) goto L_0x0841;
     */
        /* JADX WARNING: Missing block: B:395:0x0856, code:
            org.telegram.messenger.secretmedia.EncryptedFileInputStream.decryptBytesWithKeyFile(r25, 0, r39, r61.cacheImage.encryptionKeyPath);
     */
        /* JADX WARNING: Missing block: B:396:0x0865, code:
            if (r37 == false) goto L_0x0884;
     */
        /* JADX WARNING: Missing block: B:397:0x0867, code:
            r0 = new org.telegram.messenger.secretmedia.EncryptedFileInputStream(r22, r61.cacheImage.encryptionKeyPath);
     */
        /* JADX WARNING: Missing block: B:398:0x0876, code:
            r4 = android.graphics.BitmapFactory.decodeStream(r38, null, r45);
            r38.close();
     */
        /* JADX WARNING: Missing block: B:399:0x0884, code:
            r0 = new java.io.FileInputStream(r22);
     */
        /* JADX WARNING: Missing block: B:400:0x088e, code:
            r18 = false;
     */
        /* JADX WARNING: Missing block: B:401:0x0896, code:
            if (r61.cacheImage.filter == null) goto L_0x093a;
     */
        /* JADX WARNING: Missing block: B:402:0x0898, code:
            r15 = (float) r4.getWidth();
            r14 = (float) r4.getHeight();
     */
        /* JADX WARNING: Missing block: B:403:0x08a6, code:
            if (r45.inPurgeable != false) goto L_0x08cf;
     */
        /* JADX WARNING: Missing block: B:405:0x08ab, code:
            if (r60 == 0.0f) goto L_0x08cf;
     */
        /* JADX WARNING: Missing block: B:407:0x08af, code:
            if (r15 == r60) goto L_0x08cf;
     */
        /* JADX WARNING: Missing block: B:409:0x08b7, code:
            if (r15 <= (20.0f + r60)) goto L_0x08cf;
     */
        /* JADX WARNING: Missing block: B:410:0x08b9, code:
            r54 = org.telegram.messenger.Bitmaps.createScaledBitmap(r4, (int) r60, (int) (r14 / (r15 / r60)), true);
     */
        /* JADX WARNING: Missing block: B:411:0x08c8, code:
            if (r4 == r54) goto L_0x08cf;
     */
        /* JADX WARNING: Missing block: B:412:0x08ca, code:
            r4.recycle();
            r4 = r54;
     */
        /* JADX WARNING: Missing block: B:413:0x08cf, code:
            if (r4 == null) goto L_0x093a;
     */
        /* JADX WARNING: Missing block: B:414:0x08d1, code:
            if (r24 == false) goto L_0x090b;
     */
        /* JADX WARNING: Missing block: B:415:0x08d3, code:
            r13 = r4;
     */
        /* JADX WARNING: Missing block: B:416:0x08e0, code:
            if ((r4.getWidth() * r4.getHeight()) <= 22500) goto L_0x08eb;
     */
        /* JADX WARNING: Missing block: B:417:0x08e2, code:
            r13 = org.telegram.messenger.Bitmaps.createScaledBitmap(r4, 100, 100, false);
     */
        /* JADX WARNING: Missing block: B:419:0x08ef, code:
            if (r45.inPurgeable == false) goto L_0x0947;
     */
        /* JADX WARNING: Missing block: B:420:0x08f1, code:
            r5 = 0;
     */
        /* JADX WARNING: Missing block: B:422:0x0902, code:
            if (org.telegram.messenger.Utilities.needInvert(r13, r5, r13.getWidth(), r13.getHeight(), r13.getRowBytes()) == 0) goto L_0x0949;
     */
        /* JADX WARNING: Missing block: B:423:0x0904, code:
            r43 = true;
     */
        /* JADX WARNING: Missing block: B:424:0x0906, code:
            if (r13 == r4) goto L_0x090b;
     */
        /* JADX WARNING: Missing block: B:425:0x0908, code:
            r13.recycle();
     */
        /* JADX WARNING: Missing block: B:426:0x090b, code:
            if (r16 == false) goto L_0x093a;
     */
        /* JADX WARNING: Missing block: B:428:0x0911, code:
            if (r14 >= 100.0f) goto L_0x093a;
     */
        /* JADX WARNING: Missing block: B:430:0x0917, code:
            if (r15 >= 100.0f) goto L_0x093a;
     */
        /* JADX WARNING: Missing block: B:432:0x091f, code:
            if (r4.getConfig() != android.graphics.Bitmap.Config.ARGB_8888) goto L_0x0938;
     */
        /* JADX WARNING: Missing block: B:434:0x0926, code:
            if (r45.inPurgeable == false) goto L_0x094c;
     */
        /* JADX WARNING: Missing block: B:435:0x0928, code:
            r6 = 0;
     */
        /* JADX WARNING: Missing block: B:436:0x0929, code:
            org.telegram.messenger.Utilities.blurBitmap(r4, 3, r6, r4.getWidth(), r4.getHeight(), r4.getRowBytes());
     */
        /* JADX WARNING: Missing block: B:437:0x0938, code:
            r18 = true;
     */
        /* JADX WARNING: Missing block: B:438:0x093a, code:
            if (r18 != false) goto L_0x0182;
     */
        /* JADX WARNING: Missing block: B:440:0x0940, code:
            if (r45.inPurgeable == false) goto L_0x0182;
     */
        /* JADX WARNING: Missing block: B:441:0x0942, code:
            org.telegram.messenger.Utilities.pinBitmap(r4);
     */
        /* JADX WARNING: Missing block: B:442:0x0947, code:
            r5 = 1;
     */
        /* JADX WARNING: Missing block: B:443:0x0949, code:
            r43 = false;
     */
        /* JADX WARNING: Missing block: B:444:0x094c, code:
            r6 = 1;
     */
        /* JADX WARNING: Missing block: B:445:0x094e, code:
            r5 = null;
     */
        /* JADX WARNING: Missing block: B:446:0x0951, code:
            if (r4 != null) goto L_0x0953;
     */
        /* JADX WARNING: Missing block: B:447:0x0953, code:
            r5 = new android.graphics.drawable.BitmapDrawable(r4);
     */
        /* JADX WARNING: Missing block: B:448:0x0958, code:
            onPostExecute(r5);
     */
        /* JADX WARNING: Missing block: B:449:0x095f, code:
            r5 = null;
     */
        /* JADX WARNING: Missing block: B:450:0x0961, code:
            r5 = th;
     */
        /* JADX WARNING: Missing block: B:451:0x0962, code:
            r50 = r0;
     */
        /* JADX WARNING: Missing block: B:452:0x0966, code:
            r27 = e;
     */
        /* JADX WARNING: Missing block: B:453:0x0967, code:
            r50 = r0;
     */
        /* JADX WARNING: Missing block: B:454:0x096b, code:
            r4 = null;
     */
        /* JADX WARNING: Missing block: B:455:0x096f, code:
            r4 = null;
     */
        /* JADX WARNING: Missing block: B:456:0x0973, code:
            r4 = null;
     */
        /* JADX WARNING: Missing block: B:462:?, code:
            return;
     */
        /* JADX WARNING: Missing block: B:463:?, code:
            return;
     */
        /* JADX WARNING: Missing block: B:464:?, code:
            return;
     */
        /* JADX WARNING: Missing block: B:465:?, code:
            return;
     */
        /* JADX WARNING: Missing block: B:466:?, code:
            return;
     */
        /* JADX WARNING: Missing block: B:467:?, code:
            return;
     */
        /* JADX WARNING: Missing block: B:468:?, code:
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
            FileLog.m13e(e);
            Thread.interrupted();
            if (needInvert) {
            }
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
                FileLog.m13e(e);
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
                        FileLog.m13e(e2);
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
                        FileLog.m13e(e22);
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
                        FileLog.m13e(e222);
                    } catch (Throwable e2222) {
                        FileLog.m13e(e2222);
                    }
                }
                try {
                    if (this.fileOutputStream != null) {
                        this.fileOutputStream.close();
                        this.fileOutputStream = null;
                    }
                } catch (Throwable e22222) {
                    FileLog.m13e(e22222);
                }
                if (httpConnectionStream != null) {
                    try {
                        httpConnectionStream.close();
                    } catch (Throwable e222222) {
                        FileLog.m13e(e222222);
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
                    FileLog.m13e(e);
                }
            }
            if (!isCancelled()) {
                try {
                    if (this.httpConnection != null) {
                        int code = this.httpConnection.getResponseCode();
                        if (!(code == Callback.DEFAULT_DRAG_ANIMATION_DURATION || code == 202 || code == 304)) {
                            this.canRetry = false;
                        }
                    }
                } catch (Throwable e2) {
                    FileLog.m13e(e2);
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
                        FileLog.m13e(e22);
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
                        FileLog.m13e(e222);
                    } catch (Throwable e2222) {
                        FileLog.m13e(e2222);
                    }
                }
            }
            try {
                if (this.fileOutputStream != null) {
                    this.fileOutputStream.close();
                    this.fileOutputStream = null;
                }
            } catch (Throwable e22222) {
                FileLog.m13e(e22222);
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
                    FileLog.m13e(e222222);
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
                FileLog.m13e(e);
            } catch (Throwable e2) {
                FileLog.m13e(e2);
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
                FileLog.m13e(e);
            }
        }
        try {
            new File(cachePath, ".nomedia").createNewFile();
        } catch (Throwable e2) {
            FileLog.m13e(e2);
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
                FileLog.m13e(e);
            }
        }
        try {
            new File(cachePath, ".nomedia").createNewFile();
        } catch (Throwable e2) {
            FileLog.m13e(e2);
        }
        mediaDirs.put(4, cachePath);
        if (BuildVars.LOGS_ENABLED) {
            FileLog.m10d("cache path = " + cachePath);
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
                                FileLog.m10d("image path = " + imagePath);
                            }
                        }
                    } catch (Throwable e22) {
                        FileLog.m13e(e22);
                    }
                    try {
                        File videoPath = new File(this.telegramPath, "Telegram Video");
                        videoPath.mkdir();
                        if (videoPath.isDirectory() && canMoveFiles(cachePath, videoPath, 2)) {
                            mediaDirs.put(2, videoPath);
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.m10d("video path = " + videoPath);
                            }
                        }
                    } catch (Throwable e222) {
                        FileLog.m13e(e222);
                    }
                    try {
                        File audioPath = new File(this.telegramPath, "Telegram Audio");
                        audioPath.mkdir();
                        if (audioPath.isDirectory() && canMoveFiles(cachePath, audioPath, 1)) {
                            new File(audioPath, ".nomedia").createNewFile();
                            mediaDirs.put(1, audioPath);
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.m10d("audio path = " + audioPath);
                            }
                        }
                    } catch (Throwable e2222) {
                        FileLog.m13e(e2222);
                    }
                    try {
                        File documentPath = new File(this.telegramPath, "Telegram Documents");
                        documentPath.mkdir();
                        if (documentPath.isDirectory() && canMoveFiles(cachePath, documentPath, 3)) {
                            new File(documentPath, ".nomedia").createNewFile();
                            mediaDirs.put(3, documentPath);
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.m10d("documents path = " + documentPath);
                            }
                        }
                    } catch (Throwable e22222) {
                        FileLog.m13e(e22222);
                    }
                }
            } else if (BuildVars.LOGS_ENABLED) {
                FileLog.m10d("this Android can't rename files");
            }
            SharedConfig.checkSaveToGalleryFiles();
        } catch (Throwable e222222) {
            FileLog.m13e(e222222);
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
                        FileLog.m13e(e);
                        if (file != null) {
                        }
                        return false;
                    } catch (Throwable th2) {
                        th = th2;
                        if (file != null) {
                            try {
                                file.close();
                            } catch (Throwable e3) {
                                FileLog.m13e(e3);
                            }
                        }
                        throw th;
                    }
                }
            } catch (Exception e4) {
                e3 = e4;
                FileLog.m13e(e3);
                if (file != null) {
                    try {
                        file.close();
                    } catch (Throwable e32) {
                        FileLog.m13e(e32);
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
                        FileLog.m13e(e322);
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
                    FileLog.m13e(e3222);
                    return true;
                }
            }
        } catch (Exception e5) {
            e3222 = e5;
            file = file2;
            FileLog.m13e(e3222);
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
            key = "hash" + httpUrl.hashCode();
        } else if (fileLocation instanceof FileLocation) {
            FileLocation location = (FileLocation) fileLocation;
            key = location.volume_id + "_" + location.local_id;
        } else if (fileLocation instanceof Document) {
            Document location2 = (Document) fileLocation;
            key = location2.dc_id + "_" + location2.f84id;
        } else if (fileLocation instanceof SecureDocument) {
            SecureDocument location3 = (SecureDocument) fileLocation;
            key = location3.secureFile.dc_id + "_" + location3.secureFile.f203id;
        } else if (fileLocation instanceof WebFile) {
            key = "hash" + ((WebFile) fileLocation).url.hashCode();
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
            this.imageLoadQueue.postRunnable(new ImageLoader$$Lambda$4(this, thumb, url, key, TAG, imageReceiver, filter, httpLocation, imageReceiver.isNeedsQualityThumb(), imageReceiver.getParentObject(), imageLocation, imageReceiver.isShouldGenerateQualityThumb(), cacheType, size, ext, imageReceiver.getcurrentAccount()));
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:47:0x00f7  */
    /* JADX WARNING: Removed duplicated region for block: B:144:0x03a9  */
    /* JADX WARNING: Removed duplicated region for block: B:54:0x0123  */
    /* JADX WARNING: Removed duplicated region for block: B:57:0x014b  */
    /* JADX WARNING: Removed duplicated region for block: B:145:0x03ac  */
    /* JADX WARNING: Removed duplicated region for block: B:59:0x0170  */
    /* JADX WARNING: Removed duplicated region for block: B:146:0x03af  */
    /* JADX WARNING: Removed duplicated region for block: B:67:0x01a6  */
    /* JADX WARNING: Missing block: B:44:0x00ee, code:
            if (r27.equals("gif") != false) goto L_0x00f0;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    final /* synthetic */ void lambda$createLoadOperationForImageReceiver$5$ImageLoader(int thumb, String url, String key, int finalTag, ImageReceiver imageReceiver, String filter, String httpLocation, boolean finalIsNeedsQualityThumb, Object parentObject, TLObject imageLocation, boolean shouldGenerateQualityThumb, int cacheType, int size, String ext, int currentAccount) {
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
            File file;
            boolean onlyCache = false;
            File cacheFile = null;
            boolean cacheFileExists = false;
            if (httpLocation != null) {
                if (!httpLocation.startsWith("http")) {
                    if (!httpLocation.startsWith("athumb")) {
                        onlyCache = true;
                        int idx;
                        if (httpLocation.startsWith("thumb://")) {
                            idx = httpLocation.indexOf(":", 8);
                            if (idx >= 0) {
                                file = new File(httpLocation.substring(idx + 1));
                            }
                        } else {
                            if (httpLocation.startsWith("vthumb://")) {
                                idx = httpLocation.indexOf(":", 9);
                                if (idx >= 0) {
                                    file = new File(httpLocation.substring(idx + 1));
                                }
                            } else {
                                file = new File(httpLocation);
                            }
                        }
                    }
                }
            } else if (thumb != 0) {
                if (finalIsNeedsQualityThumb) {
                    file = new File(FileLoader.getDirectory(4), "q_" + url);
                    if (file.exists()) {
                        cacheFileExists = true;
                    } else {
                        cacheFile = null;
                    }
                }
                if (parentObject instanceof MessageObject) {
                    MessageObject parentMessageObject = (MessageObject) parentObject;
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
                CacheImage cacheImage = new CacheImage(this, null);
                if (httpLocation != null) {
                    if (!httpLocation.startsWith("vthumb")) {
                        if (!httpLocation.startsWith("thumb")) {
                            String trueExt = getHttpUrlExtension(httpLocation, "jpg");
                            if (!trueExt.equals("mp4")) {
                            }
                            cacheImage.animatedFile = true;
                            if (cacheFile == null) {
                                if (imageLocation instanceof SecureDocument) {
                                    cacheImage.secureDocument = (SecureDocument) imageLocation;
                                    onlyCache = cacheImage.secureDocument.secureFile.dc_id == Integer.MIN_VALUE;
                                    file = new File(FileLoader.getDirectory(4), url);
                                } else if (cacheType != 0 || size <= 0 || httpLocation != null || isEncrypted) {
                                    file = new File(FileLoader.getDirectory(4), url);
                                    if (file.exists()) {
                                        cacheFileExists = true;
                                    } else if (cacheType == 2) {
                                        file = new File(FileLoader.getDirectory(4), url + ".enc");
                                    }
                                } else if (imageLocation instanceof Document) {
                                    if (MessageObject.isVideoDocument((Document) imageLocation)) {
                                        file = new File(FileLoader.getDirectory(2), url);
                                    } else {
                                        file = new File(FileLoader.getDirectory(3), url);
                                    }
                                } else if (imageLocation instanceof WebFile) {
                                    file = new File(FileLoader.getDirectory(3), url);
                                } else {
                                    file = new File(FileLoader.getDirectory(0), url);
                                }
                            }
                            cacheImage.selfThumb = thumb == 0;
                            cacheImage.key = key;
                            cacheImage.filter = filter;
                            cacheImage.httpUrl = httpLocation;
                            cacheImage.ext = ext;
                            cacheImage.currentAccount = currentAccount;
                            if (cacheType == 2) {
                                cacheImage.encryptionKeyPath = new File(FileLoader.getInternalCacheDir(), url + ".enc.key");
                            }
                            cacheImage.addImageReceiver(imageReceiver, key, filter, thumb == 0);
                            if (!onlyCache || cacheFileExists || cacheFile.exists()) {
                                cacheImage.finalFilePath = cacheFile;
                                cacheImage.cacheTask = new CacheOutTask(cacheImage);
                                this.imageLoadingByKeys.put(key, cacheImage);
                                if (thumb == 0) {
                                    this.cacheThumbOutQueue.postRunnable(cacheImage.cacheTask);
                                    return;
                                } else {
                                    this.cacheOutQueue.postRunnable(cacheImage.cacheTask);
                                    return;
                                }
                            }
                            cacheImage.url = url;
                            cacheImage.location = imageLocation;
                            this.imageLoadingByUrl.put(url, cacheImage);
                            if (httpLocation == null) {
                                if (imageLocation instanceof FileLocation) {
                                    FileLocation location2 = (FileLocation) imageLocation;
                                    int localCacheType = cacheType;
                                    if (localCacheType == 0 && (size <= 0 || location2.key != null)) {
                                        localCacheType = 1;
                                    }
                                    FileLoader.getInstance(currentAccount).loadFile(location2, parentObject, ext, size, thumb != 0 ? 2 : 1, localCacheType);
                                } else if (imageLocation instanceof Document) {
                                    FileLoader.getInstance(currentAccount).loadFile((Document) imageLocation, parentObject, thumb != 0 ? 2 : 1, cacheType);
                                } else if (imageLocation instanceof SecureDocument) {
                                    FileLoader.getInstance(currentAccount).loadFile((SecureDocument) imageLocation, thumb != 0 ? 2 : 1);
                                } else if (imageLocation instanceof WebFile) {
                                    FileLoader.getInstance(currentAccount).loadFile((WebFile) imageLocation, thumb != 0 ? 2 : 1, cacheType);
                                }
                                if (imageReceiver.isForceLoding()) {
                                    this.forceLoadingImages.put(cacheImage.key, Integer.valueOf(0));
                                    return;
                                }
                                return;
                            }
                            cacheImage.tempFilePath = new File(FileLoader.getDirectory(4), Utilities.MD5(httpLocation) + "_temp.jpg");
                            cacheImage.finalFilePath = cacheFile;
                            if (httpLocation.startsWith("athumb")) {
                                cacheImage.artworkTask = new ArtworkLoadTask(this, cacheImage);
                                this.artworkTasks.add(cacheImage.artworkTask);
                                runArtworkTasks(false);
                                return;
                            }
                            cacheImage.httpTask = new HttpImageTask(cacheImage, size);
                            this.httpTasks.add(cacheImage.httpTask);
                            runHttpTasks(false);
                            return;
                        }
                    }
                }
                if (((imageLocation instanceof WebFile) && MessageObject.isGifDocument((WebFile) imageLocation)) || ((imageLocation instanceof Document) && (MessageObject.isGifDocument((Document) imageLocation) || MessageObject.isRoundVideoDocument((Document) imageLocation)))) {
                    cacheImage.animatedFile = true;
                }
                if (cacheFile == null) {
                }
                if (thumb == 0) {
                }
                cacheImage.selfThumb = thumb == 0;
                cacheImage.key = key;
                cacheImage.filter = filter;
                cacheImage.httpUrl = httpLocation;
                cacheImage.ext = ext;
                cacheImage.currentAccount = currentAccount;
                if (cacheType == 2) {
                }
                if (thumb == 0) {
                }
                cacheImage.addImageReceiver(imageReceiver, key, filter, thumb == 0);
                if (onlyCache) {
                }
                cacheImage.finalFilePath = cacheFile;
                cacheImage.cacheTask = new CacheOutTask(cacheImage);
                this.imageLoadingByKeys.put(key, cacheImage);
                if (thumb == 0) {
                }
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:71:0x02d4  */
    /* JADX WARNING: Removed duplicated region for block: B:78:0x0301  */
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
                key = "hash" + httpLocation.hashCode();
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
                    key = "hash" + document.url.hashCode();
                    url = key + "." + getHttpUrlExtension(document.url, defaultExt);
                } else if (imageLocation instanceof SecureDocument) {
                    SecureDocument document2 = (SecureDocument) imageLocation;
                    key = document2.secureFile.dc_id + "_" + document2.secureFile.f203id;
                    url = key + "." + ext;
                    if (null != null) {
                        thumbUrl = null + "." + ext;
                    }
                } else if (imageLocation instanceof Document) {
                    Document document3 = (Document) imageLocation;
                    if (document3.f84id != 0 && document3.dc_id != 0) {
                        key = document3.dc_id + "_" + document3.f84id;
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
                                saveImageToCache = (!MessageObject.isGifDocument(document3) || MessageObject.isRoundVideoDocument((Document) imageLocation) || MessageObject.canPreviewDocument(document3)) ? false : true;
                            }
                        }
                        docExt = TtmlNode.ANONYMOUS_REGION_ID;
                        if (docExt.length() <= 1) {
                        }
                        url = key + docExt;
                        if (null != null) {
                        }
                        if (!MessageObject.isGifDocument(document3)) {
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
            ((HttpImageTask) this.httpTasks.poll()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
            this.currentHttpTasksCount++;
        }
    }

    private void runArtworkTasks(boolean complete) {
        if (complete) {
            this.currentArtworkTasksCount--;
        }
        while (this.currentArtworkTasksCount < 4 && !this.artworkTasks.isEmpty()) {
            ((ArtworkLoadTask) this.artworkTasks.poll()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{null, null, null});
            this.currentArtworkTasksCount++;
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
                    FileLog.m13e(e);
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
                FileLog.m13e(e2);
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
                    FileLog.m13e(e);
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
                FileLog.m13e(e2);
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
                FileLog.m13e(e22);
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
                    FileLog.m13e(e23);
                    return b;
                }
            } catch (Throwable e232) {
                FileLog.m13e(e232);
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
                FileLog.m13e(e);
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
            photoSize.f108w = scaledBitmap.getWidth();
            photoSize.f107h = scaledBitmap.getHeight();
            if (photoSize.f108w <= 100 && photoSize.f107h <= 100) {
                photoSize.type = "s";
            } else if (photoSize.f108w <= 320 && photoSize.f107h <= 320) {
                photoSize.type = "m";
            } else if (photoSize.f108w <= 800 && photoSize.f107h <= 800) {
                photoSize.type = "x";
            } else if (photoSize.f108w > 1280 || photoSize.f107h > 1280) {
                photoSize.type = "w";
            } else {
                photoSize.type = "y";
            }
        } else {
            location = (TL_fileLocation) photoSize.location;
        }
        FileOutputStream stream = new FileOutputStream(new File(FileLoader.getDirectory(4), location.volume_id + "_" + location.local_id + ".jpg"));
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
            FileLog.m13e(e2);
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
                        FileLog.m13e(e);
                    }
                }
                RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rws");
                randomAccessFile.write(photoSize.bytes);
                randomAccessFile.close();
            }
            PhotoSize newPhotoSize = new TL_photoSize();
            newPhotoSize.f108w = photoSize.f108w;
            newPhotoSize.f107h = photoSize.f107h;
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
