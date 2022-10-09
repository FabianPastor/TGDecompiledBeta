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

    /* JADX WARN: Code restructure failed: missing block: B:137:?, code lost:
        return;
     */
    /* JADX WARN: Code restructure failed: missing block: B:31:0x00ae, code lost:
        r8 = 0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:33:0x00b1, code lost:
        if (r8 >= org.telegram.messenger.utils.BitmapsCache.N) goto L51;
     */
    /* JADX WARN: Code restructure failed: missing block: B:35:0x00b5, code lost:
        if (r9[r8] == null) goto L34;
     */
    /* JADX WARN: Code restructure failed: missing block: B:36:0x00b7, code lost:
        r9[r8].await();
     */
    /* JADX WARN: Code restructure failed: missing block: B:38:0x00bd, code lost:
        r0 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:39:0x00be, code lost:
        r0.printStackTrace();
     */
    /* JADX WARN: Code restructure failed: missing block: B:47:0x00d7, code lost:
        r0 = (int) r14.length();
        java.util.Collections.sort(r7, j$.util.Comparator$CC.comparingInt(org.telegram.messenger.utils.BitmapsCache$$ExternalSyntheticLambda1.INSTANCE));
        r14.writeInt(r7.size());
        r8 = 0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:49:0x00f1, code lost:
        if (r8 >= r7.size()) goto L56;
     */
    /* JADX WARN: Code restructure failed: missing block: B:50:0x00f3, code lost:
        r14.writeInt(((org.telegram.messenger.utils.BitmapsCache.FrameOffset) r7.get(r8)).frameOffset);
        r14.writeInt(((org.telegram.messenger.utils.BitmapsCache.FrameOffset) r7.get(r8)).frameSize);
        r8 = r8 + 1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:51:0x010c, code lost:
        r14.seek(r4);
        r14.writeBoolean(true);
        r14.writeInt(r0);
        r6.set(true);
        r14.close();
     */
    /* JADX WARN: Code restructure failed: missing block: B:52:0x011e, code lost:
        if (org.telegram.messenger.BuildVars.DEBUG_VERSION == false) goto L61;
     */
    /* JADX WARN: Code restructure failed: missing block: B:53:0x0120, code lost:
        org.telegram.messenger.FileLog.d("generate cache for time = " + (java.lang.System.currentTimeMillis() - r12) + " drawFrameTime = " + r1 + " comressQuality = " + r11.compressQuality + " fileSize = " + org.telegram.messenger.AndroidUtilities.formatFileSize(r11.file.length()) + " " + r11.fileName);
     */
    /* JADX WARN: Code restructure failed: missing block: B:54:0x0167, code lost:
        r11.source.releaseForGenerateCache();
     */
    /* JADX WARN: Removed duplicated region for block: B:132:0x00d3 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:45:0x00ce A[Catch: all -> 0x0211, IOException -> 0x0214, FileNotFoundException -> 0x021a, TryCatch #11 {all -> 0x0211, blocks: (B:3:0x0004, B:5:0x0017, B:7:0x0021, B:11:0x002a, B:12:0x002f, B:13:0x0043, B:15:0x0047, B:16:0x0065, B:17:0x0081, B:19:0x0085, B:23:0x008f, B:32:0x00af, B:34:0x00b3, B:36:0x00b7, B:40:0x00c1, B:42:0x00c5, B:43:0x00ca, B:45:0x00ce, B:46:0x00d3, B:39:0x00be, B:47:0x00d7, B:48:0x00ed, B:50:0x00f3, B:51:0x010c, B:53:0x0120, B:22:0x008c), top: B:112:0x0004 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void createCache() {
        /*
            Method dump skipped, instructions count: 558
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
                randomAccessFile3.close();
                this.checkCache = false;
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
                    this.checkCache = false;
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
