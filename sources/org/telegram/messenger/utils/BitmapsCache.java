package org.telegram.messenger.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.Utilities;
/* loaded from: classes.dex */
public class BitmapsCache {
    private static final int N = Utilities.clamp(Runtime.getRuntime().availableProcessors() - 2, 8, 1);
    private static ThreadPoolExecutor bitmapCompressExecutor;
    byte[] bufferTmp;
    volatile boolean cacheCreated;
    RandomAccessFile cachedFile;
    volatile boolean checkCache;
    int compressQuality;
    boolean error;
    final File file;
    String fileName;
    private int frameIndex;
    int h;
    BitmapFactory.Options options;
    volatile boolean recycled;
    private final Cacheable source;
    int w;
    ArrayList<FrameOffset> frameOffsets = new ArrayList<>();
    private final Object mutex = new Object();
    public AtomicBoolean cancelled = new AtomicBoolean(false);

    /* loaded from: classes.dex */
    public static class CacheOptions {
        public int compressQuality = 100;
        public boolean fallback = false;
    }

    /* loaded from: classes.dex */
    public interface Cacheable {
        Bitmap getFirstFrame(Bitmap bitmap);

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

    public BitmapsCache(File file, Cacheable cacheable, CacheOptions cacheOptions, int i, int i2, boolean z) {
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
        this.file = new File(file2, sb.toString());
    }

    /* JADX WARN: Code restructure failed: missing block: B:23:0x0048, code lost:
        if (r0 == null) goto L6;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void createCache() {
        /*
            Method dump skipped, instructions count: 555
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.utils.BitmapsCache.createCache():void");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createCache$0(AtomicBoolean atomicBoolean, Bitmap[] bitmapArr, int i, ByteArrayOutputStream[] byteArrayOutputStreamArr, int i2, RandomAccessFile randomAccessFile, ArrayList arrayList, CountDownLatch[] countDownLatchArr) {
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

    /* JADX WARN: Removed duplicated region for block: B:65:0x00df A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public int getFrame(int r10, android.graphics.Bitmap r11) {
        /*
            Method dump skipped, instructions count: 232
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.utils.BitmapsCache.getFrame(int, android.graphics.Bitmap):int");
    }

    public boolean needGenCache() {
        return !this.cacheCreated;
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
}
