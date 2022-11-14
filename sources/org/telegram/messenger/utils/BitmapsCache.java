package org.telegram.messenger.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import j$.util.concurrent.ConcurrentHashMap;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.utils.BitmapsCache;
import org.telegram.ui.Components.RLottieDrawable;
/* loaded from: classes.dex */
public class BitmapsCache {
    private static ThreadPoolExecutor bitmapCompressExecutor;
    static volatile boolean cleanupScheduled;
    private static CacheGeneratorSharedTools sharedTools;
    private static int taskCounter;
    byte[] bufferTmp;
    volatile boolean cacheCreated;
    RandomAccessFile cachedFile;
    volatile boolean checkCache;
    int compressQuality;
    boolean error;
    final File file;
    volatile boolean fileExist;
    String fileName;
    private int frameIndex;
    int h;
    BitmapFactory.Options options;
    volatile boolean recycled;
    private final Cacheable source;
    private int tryCount;
    final boolean useSharedBuffers;
    int w;
    static ConcurrentHashMap<Thread, byte[]> sharedBuffers = new ConcurrentHashMap<>();
    private static final int N = Utilities.clamp(Runtime.getRuntime().availableProcessors() - 2, 8, 1);
    ArrayList<FrameOffset> frameOffsets = new ArrayList<>();
    private final Object mutex = new Object();
    public AtomicBoolean cancelled = new AtomicBoolean(false);
    private Runnable cleanupSharedBuffers = new Runnable() { // from class: org.telegram.messenger.utils.BitmapsCache.1
        @Override // java.lang.Runnable
        public void run() {
            for (Thread thread : BitmapsCache.sharedBuffers.keySet()) {
                if (!thread.isAlive()) {
                    BitmapsCache.sharedBuffers.remove(thread);
                }
            }
            if (!BitmapsCache.sharedBuffers.isEmpty()) {
                AndroidUtilities.runOnUIThread(BitmapsCache.this.cleanupSharedBuffers, 5000L);
            } else {
                BitmapsCache.cleanupScheduled = false;
            }
        }
    };

    /* loaded from: classes.dex */
    public static class CacheOptions {
        public int compressQuality = 100;
        public boolean fallback = false;
    }

    /* loaded from: classes.dex */
    public interface Cacheable {
        int getNextFrame(Bitmap bitmap);

        void prepareForGenerateCache();

        void releaseForGenerateCache();
    }

    /* loaded from: classes.dex */
    public static class Metadata {
        public int frame;
    }

    public void cancelCreate() {
    }

    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:47:0x010c -> B:66:0x0124). Please submit an issue!!! */
    public BitmapsCache(File file, Cacheable cacheable, CacheOptions cacheOptions, int i, int i2, boolean z) {
        RandomAccessFile randomAccessFile;
        Throwable th;
        this.source = cacheable;
        this.w = i;
        this.h = i2;
        this.compressQuality = cacheOptions.compressQuality;
        this.fileName = file.getName();
        if (bitmapCompressExecutor == null) {
            int i3 = N;
            bitmapCompressExecutor = new ThreadPoolExecutor(i3, i3, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue());
        }
        File file2 = new File(FileLoader.checkDirectory(4), "acache");
        StringBuilder sb = new StringBuilder();
        sb.append(this.fileName);
        sb.append("_");
        sb.append(i);
        sb.append("_");
        sb.append(i2);
        sb.append(z ? "_nolimit" : " ");
        sb.append(".pcache2");
        File file3 = new File(file2, sb.toString());
        this.file = file3;
        this.useSharedBuffers = i < AndroidUtilities.dp(60.0f) && i2 < AndroidUtilities.dp(60.0f);
        if (SharedConfig.getDevicePerformanceClass() >= 2) {
            this.fileExist = file3.exists();
            if (!this.fileExist) {
                return;
            }
            try {
                try {
                    randomAccessFile = new RandomAccessFile(file3, "r");
                    try {
                        this.cacheCreated = randomAccessFile.readBoolean();
                        if (this.cacheCreated && this.frameOffsets.isEmpty()) {
                            randomAccessFile.seek(randomAccessFile.readInt());
                            int readInt = randomAccessFile.readInt();
                            fillFrames(randomAccessFile, readInt > 10000 ? 0 : readInt);
                            if (this.frameOffsets.size() == 0) {
                                file3.delete();
                                this.cacheCreated = false;
                                this.fileExist = false;
                            } else {
                                this.cachedFile = randomAccessFile;
                            }
                        }
                        if (this.cachedFile != randomAccessFile) {
                            randomAccessFile.close();
                        }
                    } catch (Throwable th2) {
                        th = th2;
                        try {
                            th.printStackTrace();
                            this.file.delete();
                            this.fileExist = false;
                            if (this.cachedFile != randomAccessFile && randomAccessFile != null) {
                                randomAccessFile.close();
                            }
                            return;
                        } catch (Throwable th3) {
                            try {
                                if (this.cachedFile != randomAccessFile && randomAccessFile != null) {
                                    randomAccessFile.close();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            throw th3;
                        }
                    }
                } catch (Throwable th4) {
                    randomAccessFile = null;
                    th = th4;
                }
            } catch (IOException e2) {
                e2.printStackTrace();
            }
            return;
        }
        this.fileExist = false;
        this.cacheCreated = false;
    }

    public static void incrementTaskCounter() {
        taskCounter++;
    }

    public static void decrementTaskCounter() {
        int i = taskCounter - 1;
        taskCounter = i;
        if (i <= 0) {
            taskCounter = 0;
            RLottieDrawable.lottieCacheGenerateQueue.postRunnable(BitmapsCache$$ExternalSyntheticLambda1.INSTANCE);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$decrementTaskCounter$0() {
        CacheGeneratorSharedTools cacheGeneratorSharedTools = sharedTools;
        if (cacheGeneratorSharedTools != null) {
            cacheGeneratorSharedTools.release();
            sharedTools = null;
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:24:0x005e, code lost:
        if (r24.cachedFile != r0) goto L107;
     */
    /* JADX WARN: Code restructure failed: missing block: B:70:0x01b3, code lost:
        r13 = r5;
        r20 = r7;
        r22 = r9;
     */
    /* JADX WARN: Code restructure failed: missing block: B:71:0x01bc, code lost:
        if (org.telegram.messenger.BuildVars.DEBUG_VERSION == false) goto L55;
     */
    /* JADX WARN: Code restructure failed: missing block: B:72:0x01be, code lost:
        org.telegram.messenger.FileLog.d("cancelled cache generation");
     */
    /* JADX WARN: Code restructure failed: missing block: B:73:0x01c3, code lost:
        r13.set(true);
        r10 = 0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:75:0x01c9, code lost:
        if (r10 >= org.telegram.messenger.utils.BitmapsCache.N) goto L74;
     */
    /* JADX WARN: Code restructure failed: missing block: B:77:0x01cd, code lost:
        if (r20[r10] == null) goto L60;
     */
    /* JADX WARN: Code restructure failed: missing block: B:78:0x01cf, code lost:
        r20[r10].await();
     */
    /* JADX WARN: Code restructure failed: missing block: B:80:0x01d5, code lost:
        r0 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:81:0x01d6, code lost:
        r0.printStackTrace();
     */
    /* JADX WARN: Code restructure failed: missing block: B:86:0x01e5, code lost:
        r22.close();
        r24.source.releaseForGenerateCache();
     */
    /* JADX WARN: Removed duplicated region for block: B:111:0x00b9 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:124:0x00dd A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:35:0x007e A[Catch: all -> 0x01ef, IOException -> 0x01f1, FileNotFoundException -> 0x01f6, TryCatch #12 {FileNotFoundException -> 0x01f6, IOException -> 0x01f1, blocks: (B:3:0x0002, B:14:0x0044, B:23:0x005c, B:29:0x006a, B:33:0x0071, B:35:0x007e, B:36:0x0085, B:37:0x00b5, B:39:0x00b9, B:43:0x00c3, B:45:0x00cb, B:48:0x00d3, B:51:0x00de, B:53:0x00e2, B:55:0x00e6, B:59:0x00f0, B:58:0x00ed, B:60:0x00f3, B:61:0x0110, B:63:0x0116, B:64:0x0133, B:65:0x016b, B:70:0x01b3, B:72:0x01be, B:73:0x01c3, B:74:0x01c7, B:76:0x01cb, B:78:0x01cf, B:82:0x01d9, B:81:0x01d6, B:86:0x01e5, B:42:0x00c0), top: B:121:0x0002, outer: #11 }] */
    /* JADX WARN: Removed duplicated region for block: B:65:0x016b A[Catch: all -> 0x01ef, IOException -> 0x01f1, FileNotFoundException -> 0x01f6, TryCatch #12 {FileNotFoundException -> 0x01f6, IOException -> 0x01f1, blocks: (B:3:0x0002, B:14:0x0044, B:23:0x005c, B:29:0x006a, B:33:0x0071, B:35:0x007e, B:36:0x0085, B:37:0x00b5, B:39:0x00b9, B:43:0x00c3, B:45:0x00cb, B:48:0x00d3, B:51:0x00de, B:53:0x00e2, B:55:0x00e6, B:59:0x00f0, B:58:0x00ed, B:60:0x00f3, B:61:0x0110, B:63:0x0116, B:64:0x0133, B:65:0x016b, B:70:0x01b3, B:72:0x01be, B:73:0x01c3, B:74:0x01c7, B:76:0x01cb, B:78:0x01cf, B:82:0x01d9, B:81:0x01d6, B:86:0x01e5, B:42:0x00c0), top: B:121:0x0002, outer: #11 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void createCache() {
        /*
            Method dump skipped, instructions count: 520
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.utils.BitmapsCache.createCache():void");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createCache$1(AtomicBoolean atomicBoolean, Bitmap[] bitmapArr, int i, ByteArrayOutputStream[] byteArrayOutputStreamArr, int i2, RandomAccessFile randomAccessFile, ArrayList arrayList, CountDownLatch[] countDownLatchArr) {
        if (this.cancelled.get() || atomicBoolean.get()) {
            return;
        }
        Bitmap.CompressFormat compressFormat = Bitmap.CompressFormat.WEBP;
        if (Build.VERSION.SDK_INT <= 26) {
            compressFormat = Bitmap.CompressFormat.PNG;
        }
        bitmapArr[i].compress(compressFormat, this.compressQuality, byteArrayOutputStreamArr[i]);
        int i3 = byteArrayOutputStreamArr[i].count;
        try {
            synchronized (this.mutex) {
                FrameOffset frameOffset = new FrameOffset(i2);
                frameOffset.frameOffset = (int) randomAccessFile.length();
                arrayList.add(frameOffset);
                randomAccessFile.write(byteArrayOutputStreamArr[i].buf, 0, i3);
                frameOffset.frameSize = i3;
                byteArrayOutputStreamArr[i].reset();
            }
        } catch (IOException e) {
            e.printStackTrace();
            try {
                randomAccessFile.close();
            } catch (Exception unused) {
            } catch (Throwable th) {
                atomicBoolean.set(true);
                throw th;
            }
            atomicBoolean.set(true);
        }
        countDownLatchArr[i].countDown();
    }

    private void fillFrames(RandomAccessFile randomAccessFile, int i) throws Throwable {
        byte[] bArr = new byte[i * 8];
        randomAccessFile.read(bArr);
        ByteBuffer wrap = ByteBuffer.wrap(bArr);
        for (int i2 = 0; i2 < i; i2++) {
            FrameOffset frameOffset = new FrameOffset(i2);
            frameOffset.frameOffset = wrap.getInt();
            frameOffset.frameSize = wrap.getInt();
            this.frameOffsets.add(frameOffset);
        }
    }

    public int getFrame(Bitmap bitmap, Metadata metadata) {
        int frame = getFrame(this.frameIndex, bitmap);
        metadata.frame = this.frameIndex;
        if (this.cacheCreated && !this.frameOffsets.isEmpty()) {
            int i = this.frameIndex + 1;
            this.frameIndex = i;
            if (i >= this.frameOffsets.size()) {
                this.frameIndex = 0;
            }
        }
        return frame;
    }

    public boolean cacheExist() {
        RandomAccessFile randomAccessFile;
        Throwable th;
        if (this.checkCache) {
            return this.cacheCreated;
        }
        RandomAccessFile randomAccessFile2 = null;
        try {
            try {
                try {
                } catch (Throwable th2) {
                    th = th2;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception unused) {
        } catch (Throwable th3) {
            randomAccessFile = null;
            th = th3;
        }
        synchronized (this.mutex) {
            try {
                RandomAccessFile randomAccessFile3 = new RandomAccessFile(this.file, "r");
                this.cacheCreated = randomAccessFile3.readBoolean();
                if (randomAccessFile3.readInt() <= 0) {
                    this.cacheCreated = false;
                }
                randomAccessFile3.close();
                this.checkCache = true;
                return this.cacheCreated;
            } catch (Throwable th4) {
                randomAccessFile = null;
                th = th4;
                try {
                    throw th;
                } catch (Exception unused2) {
                    randomAccessFile2 = null;
                    if (randomAccessFile2 != null) {
                        randomAccessFile2.close();
                    }
                    this.checkCache = true;
                    return this.cacheCreated;
                } catch (Throwable th5) {
                    th = th5;
                    if (randomAccessFile != null) {
                        try {
                            randomAccessFile.close();
                        } catch (IOException e2) {
                            e2.printStackTrace();
                        }
                    }
                    throw th;
                }
            }
        }
    }

    public int getFrame(int i, Bitmap bitmap) {
        RandomAccessFile randomAccessFile;
        if (this.error) {
            return -1;
        }
        RandomAccessFile randomAccessFile2 = null;
        try {
            if (!this.cacheCreated && !this.fileExist) {
                return -1;
            }
            if (!this.cacheCreated || (randomAccessFile = this.cachedFile) == null) {
                randomAccessFile = new RandomAccessFile(this.file, "r");
                try {
                    this.cacheCreated = randomAccessFile.readBoolean();
                    if (this.cacheCreated && this.frameOffsets.isEmpty()) {
                        randomAccessFile.seek(randomAccessFile.readInt());
                        fillFrames(randomAccessFile, randomAccessFile.readInt());
                    }
                    if (this.frameOffsets.size() == 0) {
                        this.cacheCreated = false;
                    }
                    if (!this.cacheCreated) {
                        randomAccessFile.close();
                        return -1;
                    }
                } catch (FileNotFoundException unused) {
                    randomAccessFile2 = randomAccessFile;
                    if (this.error && randomAccessFile2 != null) {
                        try {
                            randomAccessFile2.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    return -1;
                } catch (Throwable th) {
                    th = th;
                    randomAccessFile2 = randomAccessFile;
                    FileLog.e(th, false);
                    int i2 = this.tryCount + 1;
                    this.tryCount = i2;
                    if (i2 > 10) {
                        this.error = true;
                    }
                    if (this.error) {
                        randomAccessFile2.close();
                    }
                    return -1;
                }
            }
            FrameOffset frameOffset = this.frameOffsets.get(Utilities.clamp(i, this.frameOffsets.size() - 1, 0));
            randomAccessFile.seek(frameOffset.frameOffset);
            byte[] buffer = getBuffer(frameOffset);
            randomAccessFile.readFully(buffer, 0, frameOffset.frameSize);
            if (!this.recycled) {
                this.cachedFile = randomAccessFile;
            } else {
                this.cachedFile = null;
                randomAccessFile.close();
            }
            if (this.options == null) {
                this.options = new BitmapFactory.Options();
            }
            BitmapFactory.Options options = this.options;
            options.inBitmap = bitmap;
            BitmapFactory.decodeByteArray(buffer, 0, frameOffset.frameSize, options);
            return 0;
        } catch (FileNotFoundException unused2) {
        } catch (Throwable th2) {
            th = th2;
        }
    }

    private byte[] getBuffer(FrameOffset frameOffset) {
        byte[] bArr;
        boolean z = this.useSharedBuffers && Thread.currentThread().getName().startsWith("DispatchQueuePoolThreadSafety_");
        if (z) {
            bArr = sharedBuffers.get(Thread.currentThread());
        } else {
            bArr = this.bufferTmp;
        }
        if (bArr == null || bArr.length < frameOffset.frameSize) {
            bArr = new byte[(int) (frameOffset.frameSize * 1.3f)];
            if (z) {
                sharedBuffers.put(Thread.currentThread(), bArr);
                if (!cleanupScheduled) {
                    cleanupScheduled = true;
                    AndroidUtilities.runOnUIThread(this.cleanupSharedBuffers, 5000L);
                }
            } else {
                this.bufferTmp = bArr;
            }
        }
        return bArr;
    }

    public boolean needGenCache() {
        return !this.cacheCreated || !this.fileExist;
    }

    public void recycle() {
        RandomAccessFile randomAccessFile = this.cachedFile;
        if (randomAccessFile != null) {
            try {
                randomAccessFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.cachedFile = null;
        }
        this.recycled = true;
    }

    public int getFrameCount() {
        return this.frameOffsets.size();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class FrameOffset {
        int frameOffset;
        int frameSize;
        final int index;

        private FrameOffset(BitmapsCache bitmapsCache, int i) {
            this.index = i;
        }
    }

    /* loaded from: classes.dex */
    public static class ByteArrayOutputStream extends OutputStream {
        protected byte[] buf;
        protected int count;

        public ByteArrayOutputStream(int i) {
            this.buf = new byte[i];
        }

        private void ensureCapacity(int i) {
            if (i - this.buf.length > 0) {
                grow(i);
            }
        }

        private void grow(int i) {
            int length = this.buf.length << 1;
            if (length - i < 0) {
                length = i;
            }
            if (length - NUM > 0) {
                length = hugeCapacity(i);
            }
            this.buf = Arrays.copyOf(this.buf, length);
        }

        private static int hugeCapacity(int i) {
            if (i >= 0) {
                return i > NUM ? Integer.MAX_VALUE : NUM;
            }
            throw new OutOfMemoryError();
        }

        @Override // java.io.OutputStream
        public synchronized void write(int i) {
            ensureCapacity(this.count + 1);
            byte[] bArr = this.buf;
            int i2 = this.count;
            bArr[i2] = (byte) i;
            this.count = i2 + 1;
        }

        public void writeInt(int i) {
            ensureCapacity(this.count + 4);
            byte[] bArr = this.buf;
            int i2 = this.count;
            bArr[i2] = (byte) (i >>> 24);
            bArr[i2 + 1] = (byte) (i >>> 16);
            bArr[i2 + 2] = (byte) (i >>> 8);
            bArr[i2 + 3] = (byte) i;
            this.count = i2 + 4;
        }

        @Override // java.io.OutputStream
        public synchronized void write(byte[] bArr, int i, int i2) {
            if (i >= 0) {
                if (i <= bArr.length && i2 >= 0 && (i + i2) - bArr.length <= 0) {
                    ensureCapacity(this.count + i2);
                    System.arraycopy(bArr, i, this.buf, this.count, i2);
                    this.count += i2;
                }
            }
            throw new IndexOutOfBoundsException();
        }

        public synchronized void reset() {
            this.count = 0;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class CacheGeneratorSharedTools {
        private Bitmap[] bitmap;
        ByteArrayOutputStream[] byteArrayOutputStream;
        private int lastSize;

        private CacheGeneratorSharedTools() {
            this.byteArrayOutputStream = new ByteArrayOutputStream[BitmapsCache.N];
            this.bitmap = new Bitmap[BitmapsCache.N];
        }

        void allocate(int i, int i2) {
            int i3 = (i2 << 16) + i;
            boolean z = this.lastSize != i3;
            this.lastSize = i3;
            for (int i4 = 0; i4 < BitmapsCache.N; i4++) {
                if (z || this.bitmap[i4] == null) {
                    Bitmap[] bitmapArr = this.bitmap;
                    if (bitmapArr[i4] != null) {
                        final Bitmap bitmap = bitmapArr[i4];
                        Utilities.globalQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.utils.BitmapsCache$CacheGeneratorSharedTools$$ExternalSyntheticLambda0
                            @Override // java.lang.Runnable
                            public final void run() {
                                BitmapsCache.CacheGeneratorSharedTools.lambda$allocate$0(bitmap);
                            }
                        });
                    }
                    this.bitmap[i4] = Bitmap.createBitmap(i2, i, Bitmap.Config.ARGB_8888);
                }
                ByteArrayOutputStream[] byteArrayOutputStreamArr = this.byteArrayOutputStream;
                if (byteArrayOutputStreamArr[i4] == null) {
                    byteArrayOutputStreamArr[i4] = new ByteArrayOutputStream(i2 * i * 2);
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static /* synthetic */ void lambda$allocate$0(Bitmap bitmap) {
            try {
                bitmap.recycle();
            } catch (Exception unused) {
            }
        }

        void release() {
            final ArrayList arrayList = null;
            for (int i = 0; i < BitmapsCache.N; i++) {
                if (this.bitmap[i] != null) {
                    if (arrayList == null) {
                        arrayList = new ArrayList();
                    }
                    arrayList.add(this.bitmap[i]);
                }
                this.bitmap[i] = null;
                this.byteArrayOutputStream[i] = null;
            }
            if (!arrayList.isEmpty()) {
                Utilities.globalQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.utils.BitmapsCache$CacheGeneratorSharedTools$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        BitmapsCache.CacheGeneratorSharedTools.lambda$release$1(arrayList);
                    }
                });
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static /* synthetic */ void lambda$release$1(ArrayList arrayList) {
            Iterator it = arrayList.iterator();
            while (it.hasNext()) {
                ((Bitmap) it.next()).recycle();
            }
        }
    }
}
