package org.telegram.messenger.utils;

import android.graphics.Bitmap;
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

public class BitmapsCache {
    private static final int N = Utilities.clamp(Runtime.getRuntime().availableProcessors() - 2, 8, 1);
    private static ThreadPoolExecutor bitmapCompressExecutor;
    byte[] bufferTmp;
    volatile boolean cacheCreated;
    RandomAccessFile cachedFile;
    public AtomicBoolean cancelled = new AtomicBoolean(false);
    volatile boolean checkCache;
    int compressQuality;
    boolean error;
    final File file;
    String fileName;
    private int frameIndex;
    ArrayList<FrameOffset> frameOffsets = new ArrayList<>();
    int h;
    private final Object mutex = new Object();
    volatile boolean recycled;
    private final Cacheable source;
    int w;

    public static class CacheOptions {
        public int compressQuality = 100;
        public boolean fallback = false;
    }

    public interface Cacheable {
        Bitmap getFirstFrame(Bitmap bitmap);

        int getNextFrame(Bitmap bitmap);

        void prepareForGenerateCache();

        void releaseForGenerateCache();
    }

    public static class Metadata {
        public int frame;
    }

    public void cancelCreate() {
    }

    public BitmapsCache(File file2, Cacheable cacheable, CacheOptions cacheOptions, int i, int i2, boolean z) {
        this.source = cacheable;
        this.w = i;
        this.h = i2;
        this.compressQuality = cacheOptions.compressQuality;
        this.fileName = file2.getName();
        if (bitmapCompressExecutor == null) {
            int i3 = N;
            bitmapCompressExecutor = new ThreadPoolExecutor(i3, i3, 60, TimeUnit.SECONDS, new LinkedBlockingQueue());
        }
        File file3 = new File(FileLoader.checkDirectory(4), "acache");
        StringBuilder sb = new StringBuilder();
        sb.append(this.fileName);
        sb.append("_");
        sb.append(i);
        sb.append("_");
        sb.append(i2);
        sb.append(z ? "_nolimit" : " ");
        sb.append(".pcache2");
        this.file = new File(file3, sb.toString());
    }

    /* JADX WARNING: Can't wrap try/catch for region: R(18:1|2|(3:4|5|(4:7|8|9|10)(2:11|12))|13|14|(2:17|15)|115|18|(5:19|(2:21|22)|26|27|(3:29|30|(2:118|32)(2:33|(1:116)(4:63|64|(2:66|120)(2:67|119)|68)))(1:117))|69|(1:71)|72|(6:75|(2:77|78)|82|(3:84|85|125)(1:126)|86|73)|88|89|90|(5:91|92|93|9|10)|(2:(6:46|(2:48|49)|50|51|(0)(0)|54)|(9:(10:39|(2:41|42)|46|(0)|50|51|(0)(0)|54|37|36)|121|55|(2:58|56)|124|59|(1:61)|62|128))) */
    /* JADX WARNING: Code restructure failed: missing block: B:102:0x0211, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:103:0x0212, code lost:
        r1 = r11;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:0x00ae, code lost:
        r8 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:53:0x00ce, code lost:
        r10[r8].buf = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:96:0x0205, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:97:0x0206, code lost:
        r1 = r27;
     */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Missing exception handler attribute for start block: B:13:0x002f */
    /* JADX WARNING: Missing exception handler attribute for start block: B:50:0x00ca */
    /* JADX WARNING: Removed duplicated region for block: B:102:0x0211 A[ExcHandler: all (th java.lang.Throwable), PHI: r11 
      PHI: (r11v3 org.telegram.messenger.utils.BitmapsCache) = (r11v0 org.telegram.messenger.utils.BitmapsCache), (r11v0 org.telegram.messenger.utils.BitmapsCache), (r11v4 org.telegram.messenger.utils.BitmapsCache), (r11v4 org.telegram.messenger.utils.BitmapsCache), (r11v4 org.telegram.messenger.utils.BitmapsCache), (r11v4 org.telegram.messenger.utils.BitmapsCache), (r11v4 org.telegram.messenger.utils.BitmapsCache), (r11v4 org.telegram.messenger.utils.BitmapsCache), (r11v0 org.telegram.messenger.utils.BitmapsCache), (r11v0 org.telegram.messenger.utils.BitmapsCache), (r11v0 org.telegram.messenger.utils.BitmapsCache) binds: [B:1:0x0004, B:13:0x002f, B:36:0x00af, B:50:0x00ca, B:48:0x00c5, B:49:?, B:44:0x00be, B:24:0x008c, B:4:0x0017, B:11:0x002a, B:12:?] A[DONT_GENERATE, DONT_INLINE], Splitter:B:13:0x002f] */
    /* JADX WARNING: Removed duplicated region for block: B:117:0x01c2 A[EDGE_INSN: B:117:0x01c2->B:69:0x01c2 ?: BREAK  , SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:123:0x00d3 A[SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:17:0x0047 A[Catch:{ FileNotFoundException -> 0x021a, IOException -> 0x0214, all -> 0x0211, all -> 0x0225 }, LOOP:0: B:15:0x0043->B:17:0x0047, LOOP_END] */
    /* JADX WARNING: Removed duplicated region for block: B:21:0x0085 A[SYNTHETIC, Splitter:B:21:0x0085] */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x0098 A[SYNTHETIC, Splitter:B:29:0x0098] */
    /* JADX WARNING: Removed duplicated region for block: B:48:0x00c5 A[SYNTHETIC, Splitter:B:48:0x00c5] */
    /* JADX WARNING: Removed duplicated region for block: B:53:0x00ce A[Catch:{ FileNotFoundException -> 0x021a, IOException -> 0x0214, all -> 0x0211, all -> 0x0225 }] */
    /* JADX WARNING: Removed duplicated region for block: B:71:0x01cb A[Catch:{ FileNotFoundException -> 0x020d, IOException -> 0x0209, all -> 0x0205 }] */
    /* JADX WARNING: Removed duplicated region for block: B:75:0x01d9 A[Catch:{ FileNotFoundException -> 0x020d, IOException -> 0x0209, all -> 0x0205 }] */
    /* JADX WARNING: Removed duplicated region for block: B:96:0x0205 A[ExcHandler: all (th java.lang.Throwable), Splitter:B:29:0x0098] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void createCache() {
        /*
            r27 = this;
            r11 = r27
            java.lang.String r0 = "rw"
            long r12 = java.lang.System.currentTimeMillis()     // Catch:{ FileNotFoundException -> 0x021a, IOException -> 0x0214, all -> 0x0211 }
            java.io.RandomAccessFile r1 = new java.io.RandomAccessFile     // Catch:{ FileNotFoundException -> 0x021a, IOException -> 0x0214, all -> 0x0211 }
            java.io.File r2 = r11.file     // Catch:{ FileNotFoundException -> 0x021a, IOException -> 0x0214, all -> 0x0211 }
            r1.<init>(r2, r0)     // Catch:{ FileNotFoundException -> 0x021a, IOException -> 0x0214, all -> 0x0211 }
            java.io.File r2 = r11.file     // Catch:{ FileNotFoundException -> 0x021a, IOException -> 0x0214, all -> 0x0211 }
            boolean r2 = r2.exists()     // Catch:{ FileNotFoundException -> 0x021a, IOException -> 0x0214, all -> 0x0211 }
            if (r2 == 0) goto L_0x002f
            boolean r2 = r1.readBoolean()     // Catch:{ Exception -> 0x002f, all -> 0x0211 }
            r11.cacheCreated = r2     // Catch:{ Exception -> 0x002f, all -> 0x0211 }
            boolean r2 = r11.cacheCreated     // Catch:{ Exception -> 0x002f, all -> 0x0211 }
            if (r2 == 0) goto L_0x002a
            r1.close()     // Catch:{ Exception -> 0x002f, all -> 0x0211 }
            org.telegram.messenger.utils.BitmapsCache$Cacheable r0 = r11.source
        L_0x0026:
            r0.releaseForGenerateCache()
            return
        L_0x002a:
            java.io.File r2 = r11.file     // Catch:{ Exception -> 0x002f, all -> 0x0211 }
            r2.delete()     // Catch:{ Exception -> 0x002f, all -> 0x0211 }
        L_0x002f:
            r1.close()     // Catch:{ FileNotFoundException -> 0x021a, IOException -> 0x0214, all -> 0x0211 }
            java.io.RandomAccessFile r14 = new java.io.RandomAccessFile     // Catch:{ FileNotFoundException -> 0x021a, IOException -> 0x0214, all -> 0x0211 }
            java.io.File r1 = r11.file     // Catch:{ FileNotFoundException -> 0x021a, IOException -> 0x0214, all -> 0x0211 }
            r14.<init>(r1, r0)     // Catch:{ FileNotFoundException -> 0x021a, IOException -> 0x0214, all -> 0x0211 }
            int r0 = N     // Catch:{ FileNotFoundException -> 0x021a, IOException -> 0x0214, all -> 0x0211 }
            android.graphics.Bitmap[] r15 = new android.graphics.Bitmap[r0]     // Catch:{ FileNotFoundException -> 0x021a, IOException -> 0x0214, all -> 0x0211 }
            org.telegram.messenger.utils.BitmapsCache$ByteArrayOutputStream[] r10 = new org.telegram.messenger.utils.BitmapsCache.ByteArrayOutputStream[r0]     // Catch:{ FileNotFoundException -> 0x021a, IOException -> 0x0214, all -> 0x0211 }
            java.util.concurrent.CountDownLatch[] r9 = new java.util.concurrent.CountDownLatch[r0]     // Catch:{ FileNotFoundException -> 0x021a, IOException -> 0x0214, all -> 0x0211 }
            r8 = 0
            r0 = 0
        L_0x0043:
            int r1 = N     // Catch:{ FileNotFoundException -> 0x021a, IOException -> 0x0214, all -> 0x0211 }
            if (r0 >= r1) goto L_0x0065
            int r1 = r11.w     // Catch:{ FileNotFoundException -> 0x021a, IOException -> 0x0214, all -> 0x0211 }
            int r2 = r11.h     // Catch:{ FileNotFoundException -> 0x021a, IOException -> 0x0214, all -> 0x0211 }
            android.graphics.Bitmap$Config r3 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ FileNotFoundException -> 0x021a, IOException -> 0x0214, all -> 0x0211 }
            android.graphics.Bitmap r1 = android.graphics.Bitmap.createBitmap(r1, r2, r3)     // Catch:{ FileNotFoundException -> 0x021a, IOException -> 0x0214, all -> 0x0211 }
            r15[r0] = r1     // Catch:{ FileNotFoundException -> 0x021a, IOException -> 0x0214, all -> 0x0211 }
            org.telegram.messenger.utils.BitmapsCache$ByteArrayOutputStream r1 = new org.telegram.messenger.utils.BitmapsCache$ByteArrayOutputStream     // Catch:{ FileNotFoundException -> 0x021a, IOException -> 0x0214, all -> 0x0211 }
            int r2 = r11.w     // Catch:{ FileNotFoundException -> 0x021a, IOException -> 0x0214, all -> 0x0211 }
            int r3 = r11.h     // Catch:{ FileNotFoundException -> 0x021a, IOException -> 0x0214, all -> 0x0211 }
            int r2 = r2 * r3
            int r2 = r2 * 2
            r1.<init>(r2)     // Catch:{ FileNotFoundException -> 0x021a, IOException -> 0x0214, all -> 0x0211 }
            r10[r0] = r1     // Catch:{ FileNotFoundException -> 0x021a, IOException -> 0x0214, all -> 0x0211 }
            int r0 = r0 + 1
            goto L_0x0043
        L_0x0065:
            java.util.ArrayList r7 = new java.util.ArrayList     // Catch:{ FileNotFoundException -> 0x021a, IOException -> 0x0214, all -> 0x0211 }
            r7.<init>()     // Catch:{ FileNotFoundException -> 0x021a, IOException -> 0x0214, all -> 0x0211 }
            r14.writeBoolean(r8)     // Catch:{ FileNotFoundException -> 0x021a, IOException -> 0x0214, all -> 0x0211 }
            r14.writeInt(r8)     // Catch:{ FileNotFoundException -> 0x021a, IOException -> 0x0214, all -> 0x0211 }
            java.util.concurrent.atomic.AtomicBoolean r6 = new java.util.concurrent.atomic.AtomicBoolean     // Catch:{ FileNotFoundException -> 0x021a, IOException -> 0x0214, all -> 0x0211 }
            r6.<init>(r8)     // Catch:{ FileNotFoundException -> 0x021a, IOException -> 0x0214, all -> 0x0211 }
            org.telegram.messenger.utils.BitmapsCache$Cacheable r0 = r11.source     // Catch:{ FileNotFoundException -> 0x021a, IOException -> 0x0214, all -> 0x0211 }
            r0.prepareForGenerateCache()     // Catch:{ FileNotFoundException -> 0x021a, IOException -> 0x0214, all -> 0x0211 }
            r4 = 0
            r1 = r4
            r16 = 0
            r17 = 0
        L_0x0081:
            r0 = r9[r16]     // Catch:{ FileNotFoundException -> 0x021a, IOException -> 0x0214, all -> 0x0211 }
            if (r0 == 0) goto L_0x008f
            r0 = r9[r16]     // Catch:{ InterruptedException -> 0x008b }
            r0.await()     // Catch:{ InterruptedException -> 0x008b }
            goto L_0x008f
        L_0x008b:
            r0 = move-exception
            r0.printStackTrace()     // Catch:{ FileNotFoundException -> 0x021a, IOException -> 0x0214, all -> 0x0211 }
        L_0x008f:
            java.util.concurrent.atomic.AtomicBoolean r0 = r11.cancelled     // Catch:{ FileNotFoundException -> 0x021a, IOException -> 0x0214, all -> 0x0211 }
            boolean r0 = r0.get()     // Catch:{ FileNotFoundException -> 0x021a, IOException -> 0x0214, all -> 0x0211 }
            r3 = 1
            if (r0 != 0) goto L_0x01c2
            boolean r0 = r6.get()     // Catch:{ FileNotFoundException -> 0x020d, IOException -> 0x0209, all -> 0x0205 }
            if (r0 == 0) goto L_0x00a0
            goto L_0x01c2
        L_0x00a0:
            long r18 = java.lang.System.currentTimeMillis()     // Catch:{ FileNotFoundException -> 0x020d, IOException -> 0x0209, all -> 0x0205 }
            org.telegram.messenger.utils.BitmapsCache$Cacheable r0 = r11.source     // Catch:{ FileNotFoundException -> 0x020d, IOException -> 0x0209, all -> 0x0205 }
            r8 = r15[r16]     // Catch:{ FileNotFoundException -> 0x020d, IOException -> 0x0209, all -> 0x0205 }
            int r0 = r0.getNextFrame(r8)     // Catch:{ FileNotFoundException -> 0x020d, IOException -> 0x0209, all -> 0x0205 }
            if (r0 == r3) goto L_0x016f
            r8 = 0
        L_0x00af:
            int r0 = N     // Catch:{ FileNotFoundException -> 0x021a, IOException -> 0x0214, all -> 0x0211 }
            if (r8 >= r0) goto L_0x00d7
            r0 = r9[r8]     // Catch:{ FileNotFoundException -> 0x021a, IOException -> 0x0214, all -> 0x0211 }
            if (r0 == 0) goto L_0x00c1
            r0 = r9[r8]     // Catch:{ InterruptedException -> 0x00bd }
            r0.await()     // Catch:{ InterruptedException -> 0x00bd }
            goto L_0x00c1
        L_0x00bd:
            r0 = move-exception
            r0.printStackTrace()     // Catch:{ FileNotFoundException -> 0x021a, IOException -> 0x0214, all -> 0x0211 }
        L_0x00c1:
            r0 = r15[r8]     // Catch:{ FileNotFoundException -> 0x021a, IOException -> 0x0214, all -> 0x0211 }
            if (r0 == 0) goto L_0x00ca
            r0 = r15[r8]     // Catch:{ Exception -> 0x00ca, all -> 0x0211 }
            r0.recycle()     // Catch:{ Exception -> 0x00ca, all -> 0x0211 }
        L_0x00ca:
            r0 = r10[r8]     // Catch:{ FileNotFoundException -> 0x021a, IOException -> 0x0214, all -> 0x0211 }
            if (r0 == 0) goto L_0x00d3
            r0 = r10[r8]     // Catch:{ FileNotFoundException -> 0x021a, IOException -> 0x0214, all -> 0x0211 }
            r3 = 0
            r0.buf = r3     // Catch:{ FileNotFoundException -> 0x021a, IOException -> 0x0214, all -> 0x0211 }
        L_0x00d3:
            int r8 = r8 + 1
            r3 = 1
            goto L_0x00af
        L_0x00d7:
            long r8 = r14.length()     // Catch:{ FileNotFoundException -> 0x021a, IOException -> 0x0214, all -> 0x0211 }
            int r0 = (int) r8     // Catch:{ FileNotFoundException -> 0x021a, IOException -> 0x0214, all -> 0x0211 }
            org.telegram.messenger.utils.BitmapsCache$$ExternalSyntheticLambda1 r3 = org.telegram.messenger.utils.BitmapsCache$$ExternalSyntheticLambda1.INSTANCE     // Catch:{ FileNotFoundException -> 0x021a, IOException -> 0x0214, all -> 0x0211 }
            java.util.Comparator r3 = j$.util.Comparator$CC.comparingInt(r3)     // Catch:{ FileNotFoundException -> 0x021a, IOException -> 0x0214, all -> 0x0211 }
            java.util.Collections.sort(r7, r3)     // Catch:{ FileNotFoundException -> 0x021a, IOException -> 0x0214, all -> 0x0211 }
            int r3 = r7.size()     // Catch:{ FileNotFoundException -> 0x021a, IOException -> 0x0214, all -> 0x0211 }
            r14.writeInt(r3)     // Catch:{ FileNotFoundException -> 0x021a, IOException -> 0x0214, all -> 0x0211 }
            r8 = 0
        L_0x00ed:
            int r3 = r7.size()     // Catch:{ FileNotFoundException -> 0x021a, IOException -> 0x0214, all -> 0x0211 }
            if (r8 >= r3) goto L_0x010c
            java.lang.Object r3 = r7.get(r8)     // Catch:{ FileNotFoundException -> 0x021a, IOException -> 0x0214, all -> 0x0211 }
            org.telegram.messenger.utils.BitmapsCache$FrameOffset r3 = (org.telegram.messenger.utils.BitmapsCache.FrameOffset) r3     // Catch:{ FileNotFoundException -> 0x021a, IOException -> 0x0214, all -> 0x0211 }
            int r3 = r3.frameOffset     // Catch:{ FileNotFoundException -> 0x021a, IOException -> 0x0214, all -> 0x0211 }
            r14.writeInt(r3)     // Catch:{ FileNotFoundException -> 0x021a, IOException -> 0x0214, all -> 0x0211 }
            java.lang.Object r3 = r7.get(r8)     // Catch:{ FileNotFoundException -> 0x021a, IOException -> 0x0214, all -> 0x0211 }
            org.telegram.messenger.utils.BitmapsCache$FrameOffset r3 = (org.telegram.messenger.utils.BitmapsCache.FrameOffset) r3     // Catch:{ FileNotFoundException -> 0x021a, IOException -> 0x0214, all -> 0x0211 }
            int r3 = r3.frameSize     // Catch:{ FileNotFoundException -> 0x021a, IOException -> 0x0214, all -> 0x0211 }
            r14.writeInt(r3)     // Catch:{ FileNotFoundException -> 0x021a, IOException -> 0x0214, all -> 0x0211 }
            int r8 = r8 + 1
            goto L_0x00ed
        L_0x010c:
            r14.seek(r4)     // Catch:{ FileNotFoundException -> 0x021a, IOException -> 0x0214, all -> 0x0211 }
            r3 = 1
            r14.writeBoolean(r3)     // Catch:{ FileNotFoundException -> 0x021a, IOException -> 0x0214, all -> 0x0211 }
            r14.writeInt(r0)     // Catch:{ FileNotFoundException -> 0x021a, IOException -> 0x0214, all -> 0x0211 }
            r6.set(r3)     // Catch:{ FileNotFoundException -> 0x021a, IOException -> 0x0214, all -> 0x0211 }
            r14.close()     // Catch:{ FileNotFoundException -> 0x021a, IOException -> 0x0214, all -> 0x0211 }
            boolean r0 = org.telegram.messenger.BuildVars.DEBUG_VERSION     // Catch:{ FileNotFoundException -> 0x021a, IOException -> 0x0214, all -> 0x0211 }
            if (r0 == 0) goto L_0x0167
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ FileNotFoundException -> 0x021a, IOException -> 0x0214, all -> 0x0211 }
            r0.<init>()     // Catch:{ FileNotFoundException -> 0x021a, IOException -> 0x0214, all -> 0x0211 }
            java.lang.String r3 = "generate cache for time = "
            r0.append(r3)     // Catch:{ FileNotFoundException -> 0x021a, IOException -> 0x0214, all -> 0x0211 }
            long r3 = java.lang.System.currentTimeMillis()     // Catch:{ FileNotFoundException -> 0x021a, IOException -> 0x0214, all -> 0x0211 }
            long r3 = r3 - r12
            r0.append(r3)     // Catch:{ FileNotFoundException -> 0x021a, IOException -> 0x0214, all -> 0x0211 }
            java.lang.String r3 = " drawFrameTime = "
            r0.append(r3)     // Catch:{ FileNotFoundException -> 0x021a, IOException -> 0x0214, all -> 0x0211 }
            r0.append(r1)     // Catch:{ FileNotFoundException -> 0x021a, IOException -> 0x0214, all -> 0x0211 }
            java.lang.String r1 = " comressQuality = "
            r0.append(r1)     // Catch:{ FileNotFoundException -> 0x021a, IOException -> 0x0214, all -> 0x0211 }
            int r1 = r11.compressQuality     // Catch:{ FileNotFoundException -> 0x021a, IOException -> 0x0214, all -> 0x0211 }
            r0.append(r1)     // Catch:{ FileNotFoundException -> 0x021a, IOException -> 0x0214, all -> 0x0211 }
            java.lang.String r1 = " fileSize = "
            r0.append(r1)     // Catch:{ FileNotFoundException -> 0x021a, IOException -> 0x0214, all -> 0x0211 }
            java.io.File r1 = r11.file     // Catch:{ FileNotFoundException -> 0x021a, IOException -> 0x0214, all -> 0x0211 }
            long r1 = r1.length()     // Catch:{ FileNotFoundException -> 0x021a, IOException -> 0x0214, all -> 0x0211 }
            java.lang.String r1 = org.telegram.messenger.AndroidUtilities.formatFileSize(r1)     // Catch:{ FileNotFoundException -> 0x021a, IOException -> 0x0214, all -> 0x0211 }
            r0.append(r1)     // Catch:{ FileNotFoundException -> 0x021a, IOException -> 0x0214, all -> 0x0211 }
            java.lang.String r1 = " "
            r0.append(r1)     // Catch:{ FileNotFoundException -> 0x021a, IOException -> 0x0214, all -> 0x0211 }
            java.lang.String r1 = r11.fileName     // Catch:{ FileNotFoundException -> 0x021a, IOException -> 0x0214, all -> 0x0211 }
            r0.append(r1)     // Catch:{ FileNotFoundException -> 0x021a, IOException -> 0x0214, all -> 0x0211 }
            java.lang.String r0 = r0.toString()     // Catch:{ FileNotFoundException -> 0x021a, IOException -> 0x0214, all -> 0x0211 }
            org.telegram.messenger.FileLog.d(r0)     // Catch:{ FileNotFoundException -> 0x021a, IOException -> 0x0214, all -> 0x0211 }
        L_0x0167:
            org.telegram.messenger.utils.BitmapsCache$Cacheable r0 = r11.source
            r0.releaseForGenerateCache()
            r1 = r11
            goto L_0x0224
        L_0x016f:
            long r22 = java.lang.System.currentTimeMillis()     // Catch:{ FileNotFoundException -> 0x020d, IOException -> 0x0209, all -> 0x0205 }
            long r22 = r22 - r18
            long r18 = r1 + r22
            java.util.concurrent.CountDownLatch r0 = new java.util.concurrent.CountDownLatch     // Catch:{ FileNotFoundException -> 0x020d, IOException -> 0x0209, all -> 0x0205 }
            r1 = 1
            r0.<init>(r1)     // Catch:{ FileNotFoundException -> 0x020d, IOException -> 0x0209, all -> 0x0205 }
            r9[r16] = r0     // Catch:{ FileNotFoundException -> 0x020d, IOException -> 0x0209, all -> 0x0205 }
            java.util.concurrent.ThreadPoolExecutor r0 = bitmapCompressExecutor     // Catch:{ FileNotFoundException -> 0x020d, IOException -> 0x0209, all -> 0x0205 }
            org.telegram.messenger.utils.BitmapsCache$$ExternalSyntheticLambda0 r8 = new org.telegram.messenger.utils.BitmapsCache$$ExternalSyntheticLambda0     // Catch:{ FileNotFoundException -> 0x020d, IOException -> 0x0209, all -> 0x0205 }
            r1 = r8
            r2 = r27
            r3 = r6
            r21 = r4
            r4 = r15
            r5 = r16
            r23 = r12
            r12 = r6
            r6 = r10
            r13 = r7
            r7 = r17
            r11 = r8
            r20 = 0
            r8 = r14
            r25 = r9
            r9 = r13
            r26 = r10
            r10 = r25
            r1.<init>(r2, r3, r4, r5, r6, r7, r8, r9, r10)     // Catch:{ FileNotFoundException -> 0x020d, IOException -> 0x0209, all -> 0x0205 }
            r0.execute(r11)     // Catch:{ FileNotFoundException -> 0x020d, IOException -> 0x0209, all -> 0x0205 }
            int r0 = r16 + 1
            int r17 = r17 + 1
            int r1 = N     // Catch:{ FileNotFoundException -> 0x020d, IOException -> 0x0209, all -> 0x0205 }
            if (r0 < r1) goto L_0x01af
            r16 = 0
            goto L_0x01b1
        L_0x01af:
            r16 = r0
        L_0x01b1:
            r8 = 0
            r11 = r27
            r6 = r12
            r7 = r13
            r1 = r18
            r4 = r21
            r12 = r23
            r9 = r25
            r10 = r26
            goto L_0x0081
        L_0x01c2:
            r12 = r6
            r25 = r9
            r20 = 0
            boolean r0 = org.telegram.messenger.BuildVars.DEBUG_VERSION     // Catch:{ FileNotFoundException -> 0x020d, IOException -> 0x0209, all -> 0x0205 }
            if (r0 == 0) goto L_0x01d0
            java.lang.String r0 = "cancelled cache generation"
            org.telegram.messenger.FileLog.d(r0)     // Catch:{ FileNotFoundException -> 0x020d, IOException -> 0x0209, all -> 0x0205 }
        L_0x01d0:
            r1 = 1
            r12.set(r1)     // Catch:{ FileNotFoundException -> 0x020d, IOException -> 0x0209, all -> 0x0205 }
            r8 = 0
        L_0x01d5:
            int r0 = N     // Catch:{ FileNotFoundException -> 0x020d, IOException -> 0x0209, all -> 0x0205 }
            if (r8 >= r0) goto L_0x01f3
            r0 = r25[r8]     // Catch:{ FileNotFoundException -> 0x020d, IOException -> 0x0209, all -> 0x0205 }
            if (r0 == 0) goto L_0x01e7
            r0 = r25[r8]     // Catch:{ InterruptedException -> 0x01e3 }
            r0.await()     // Catch:{ InterruptedException -> 0x01e3 }
            goto L_0x01e7
        L_0x01e3:
            r0 = move-exception
            r0.printStackTrace()     // Catch:{ FileNotFoundException -> 0x020d, IOException -> 0x0209, all -> 0x0205 }
        L_0x01e7:
            r0 = r15[r8]     // Catch:{ FileNotFoundException -> 0x020d, IOException -> 0x0209, all -> 0x0205 }
            if (r0 == 0) goto L_0x01f0
            r0 = r15[r8]     // Catch:{ Exception -> 0x01f0, all -> 0x0205 }
            r0.recycle()     // Catch:{ Exception -> 0x01f0, all -> 0x0205 }
        L_0x01f0:
            int r8 = r8 + 1
            goto L_0x01d5
        L_0x01f3:
            r14.close()     // Catch:{ FileNotFoundException -> 0x020d, IOException -> 0x0209, all -> 0x0205 }
            r1 = r27
            org.telegram.messenger.utils.BitmapsCache$Cacheable r0 = r1.source     // Catch:{ FileNotFoundException -> 0x0203, IOException -> 0x0201 }
            r0.releaseForGenerateCache()     // Catch:{ FileNotFoundException -> 0x0203, IOException -> 0x0201 }
            org.telegram.messenger.utils.BitmapsCache$Cacheable r0 = r1.source
            goto L_0x0026
        L_0x0201:
            r0 = move-exception
            goto L_0x0216
        L_0x0203:
            r0 = move-exception
            goto L_0x021c
        L_0x0205:
            r0 = move-exception
            r1 = r27
            goto L_0x0226
        L_0x0209:
            r0 = move-exception
            r1 = r27
            goto L_0x0216
        L_0x020d:
            r0 = move-exception
            r1 = r27
            goto L_0x021c
        L_0x0211:
            r0 = move-exception
            r1 = r11
            goto L_0x0226
        L_0x0214:
            r0 = move-exception
            r1 = r11
        L_0x0216:
            r0.printStackTrace()     // Catch:{ all -> 0x0225 }
            goto L_0x021f
        L_0x021a:
            r0 = move-exception
            r1 = r11
        L_0x021c:
            r0.printStackTrace()     // Catch:{ all -> 0x0225 }
        L_0x021f:
            org.telegram.messenger.utils.BitmapsCache$Cacheable r0 = r1.source
            r0.releaseForGenerateCache()
        L_0x0224:
            return
        L_0x0225:
            r0 = move-exception
        L_0x0226:
            org.telegram.messenger.utils.BitmapsCache$Cacheable r2 = r1.source
            r2.releaseForGenerateCache()
            goto L_0x022d
        L_0x022c:
            throw r0
        L_0x022d:
            goto L_0x022c
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.utils.BitmapsCache.createCache():void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createCache$0(AtomicBoolean atomicBoolean, Bitmap[] bitmapArr, int i, ByteArrayOutputStream[] byteArrayOutputStreamArr, int i2, RandomAccessFile randomAccessFile, ArrayList arrayList, CountDownLatch[] countDownLatchArr) {
        if (!this.cancelled.get() && !atomicBoolean.get()) {
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

    /* JADX WARNING: Code restructure failed: missing block: B:14:?, code lost:
        r2.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:37:0x0040, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:38:0x0041, code lost:
        r0.printStackTrace();
     */
    /* JADX WARNING: Removed duplicated region for block: B:28:0x0030 A[SYNTHETIC, Splitter:B:28:0x0030] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean cacheExist() {
        /*
            r6 = this;
            boolean r0 = r6.checkCache
            if (r0 == 0) goto L_0x0007
            boolean r0 = r6.cacheCreated
            return r0
        L_0x0007:
            r0 = 0
            java.lang.Object r1 = r6.mutex     // Catch:{ Exception -> 0x0039, all -> 0x002b }
            monitor-enter(r1)     // Catch:{ Exception -> 0x0039, all -> 0x002b }
            java.io.RandomAccessFile r2 = new java.io.RandomAccessFile     // Catch:{ all -> 0x001f }
            java.io.File r3 = r6.file     // Catch:{ all -> 0x001f }
            java.lang.String r4 = "r"
            r2.<init>(r3, r4)     // Catch:{ all -> 0x001f }
            boolean r0 = r2.readBoolean()     // Catch:{ all -> 0x0029 }
            r6.cacheCreated = r0     // Catch:{ all -> 0x0029 }
            monitor-exit(r1)     // Catch:{ all -> 0x0029 }
            r2.close()     // Catch:{ IOException -> 0x0040 }
            goto L_0x0044
        L_0x001f:
            r2 = move-exception
            r5 = r2
            r2 = r0
            r0 = r5
        L_0x0023:
            monitor-exit(r1)     // Catch:{ all -> 0x0029 }
            throw r0     // Catch:{ Exception -> 0x0027, all -> 0x0025 }
        L_0x0025:
            r0 = move-exception
            goto L_0x002e
        L_0x0027:
            r0 = r2
            goto L_0x003a
        L_0x0029:
            r0 = move-exception
            goto L_0x0023
        L_0x002b:
            r1 = move-exception
            r2 = r0
            r0 = r1
        L_0x002e:
            if (r2 == 0) goto L_0x0038
            r2.close()     // Catch:{ IOException -> 0x0034 }
            goto L_0x0038
        L_0x0034:
            r1 = move-exception
            r1.printStackTrace()
        L_0x0038:
            throw r0
        L_0x0039:
        L_0x003a:
            if (r0 == 0) goto L_0x0044
            r0.close()     // Catch:{ IOException -> 0x0040 }
            goto L_0x0044
        L_0x0040:
            r0 = move-exception
            r0.printStackTrace()
        L_0x0044:
            r0 = 0
            r6.checkCache = r0
            boolean r0 = r6.cacheCreated
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.utils.BitmapsCache.cacheExist():boolean");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:47:?, code lost:
        r0 = new android.graphics.BitmapFactory.Options();
        r0.inBitmap = r11;
        android.graphics.BitmapFactory.decodeByteArray(r9.bufferTmp, 0, r10.frameSize, r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:48:0x00bc, code lost:
        return 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:49:0x00bd, code lost:
        r10 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:50:0x00be, code lost:
        r0 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:51:0x00c0, code lost:
        r0 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:63:0x00ce, code lost:
        if (r0 != null) goto L_0x00d0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:65:?, code lost:
        r0.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:66:0x00d4, code lost:
        r10 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:67:0x00d5, code lost:
        r10.printStackTrace();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:68:0x00d8, code lost:
        return -1;
     */
    /* JADX WARNING: Removed duplicated region for block: B:64:0x00d0 A[SYNTHETIC, Splitter:B:64:0x00d0] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int getFrame(int r10, android.graphics.Bitmap r11) {
        /*
            r9 = this;
            boolean r0 = r9.error
            r1 = -1
            if (r0 == 0) goto L_0x0006
            return r1
        L_0x0006:
            r0 = 0
            java.lang.Object r2 = r9.mutex     // Catch:{ FileNotFoundException -> 0x00cd, all -> 0x00c8 }
            monitor-enter(r2)     // Catch:{ FileNotFoundException -> 0x00cd, all -> 0x00c8 }
            boolean r3 = r9.cacheCreated     // Catch:{ all -> 0x00c5 }
            r4 = 0
            if (r3 == 0) goto L_0x0013
            java.io.RandomAccessFile r3 = r9.cachedFile     // Catch:{ all -> 0x00c5 }
            if (r3 != 0) goto L_0x0071
        L_0x0013:
            java.io.RandomAccessFile r3 = new java.io.RandomAccessFile     // Catch:{ all -> 0x00c5 }
            java.io.File r5 = r9.file     // Catch:{ all -> 0x00c5 }
            java.lang.String r6 = "r"
            r3.<init>(r5, r6)     // Catch:{ all -> 0x00c5 }
            boolean r5 = r3.readBoolean()     // Catch:{ all -> 0x00c2 }
            r9.cacheCreated = r5     // Catch:{ all -> 0x00c2 }
            boolean r5 = r9.cacheCreated     // Catch:{ all -> 0x00c2 }
            if (r5 == 0) goto L_0x0056
            java.util.ArrayList<org.telegram.messenger.utils.BitmapsCache$FrameOffset> r5 = r9.frameOffsets     // Catch:{ all -> 0x00c2 }
            boolean r5 = r5.isEmpty()     // Catch:{ all -> 0x00c2 }
            if (r5 == 0) goto L_0x0056
            int r5 = r3.readInt()     // Catch:{ all -> 0x00c2 }
            long r5 = (long) r5     // Catch:{ all -> 0x00c2 }
            r3.seek(r5)     // Catch:{ all -> 0x00c2 }
            int r5 = r3.readInt()     // Catch:{ all -> 0x00c2 }
            r6 = 0
        L_0x003b:
            if (r6 >= r5) goto L_0x0056
            org.telegram.messenger.utils.BitmapsCache$FrameOffset r7 = new org.telegram.messenger.utils.BitmapsCache$FrameOffset     // Catch:{ all -> 0x00c2 }
            r7.<init>(r6)     // Catch:{ all -> 0x00c2 }
            int r8 = r3.readInt()     // Catch:{ all -> 0x00c2 }
            r7.frameOffset = r8     // Catch:{ all -> 0x00c2 }
            int r8 = r3.readInt()     // Catch:{ all -> 0x00c2 }
            r7.frameSize = r8     // Catch:{ all -> 0x00c2 }
            java.util.ArrayList<org.telegram.messenger.utils.BitmapsCache$FrameOffset> r8 = r9.frameOffsets     // Catch:{ all -> 0x00c2 }
            r8.add(r7)     // Catch:{ all -> 0x00c2 }
            int r6 = r6 + 1
            goto L_0x003b
        L_0x0056:
            boolean r5 = r9.cacheCreated     // Catch:{ all -> 0x00c2 }
            if (r5 != 0) goto L_0x0064
            r3.close()     // Catch:{ all -> 0x00c2 }
            org.telegram.messenger.utils.BitmapsCache$Cacheable r10 = r9.source     // Catch:{ all -> 0x00c5 }
            r10.getFirstFrame(r11)     // Catch:{ all -> 0x00c5 }
            monitor-exit(r2)     // Catch:{ all -> 0x00c5 }
            return r4
        L_0x0064:
            java.util.ArrayList<org.telegram.messenger.utils.BitmapsCache$FrameOffset> r5 = r9.frameOffsets     // Catch:{ all -> 0x00c2 }
            boolean r5 = r5.isEmpty()     // Catch:{ all -> 0x00c2 }
            if (r5 == 0) goto L_0x0071
            r3.close()     // Catch:{ all -> 0x00c2 }
            monitor-exit(r2)     // Catch:{ all -> 0x00c5 }
            return r1
        L_0x0071:
            java.util.ArrayList<org.telegram.messenger.utils.BitmapsCache$FrameOffset> r5 = r9.frameOffsets     // Catch:{ all -> 0x00c2 }
            int r5 = r5.size()     // Catch:{ all -> 0x00c2 }
            int r5 = r5 + -1
            int r10 = org.telegram.messenger.Utilities.clamp((int) r10, (int) r5, (int) r4)     // Catch:{ all -> 0x00c2 }
            java.util.ArrayList<org.telegram.messenger.utils.BitmapsCache$FrameOffset> r5 = r9.frameOffsets     // Catch:{ all -> 0x00c2 }
            java.lang.Object r10 = r5.get(r10)     // Catch:{ all -> 0x00c2 }
            org.telegram.messenger.utils.BitmapsCache$FrameOffset r10 = (org.telegram.messenger.utils.BitmapsCache.FrameOffset) r10     // Catch:{ all -> 0x00c2 }
            int r5 = r10.frameOffset     // Catch:{ all -> 0x00c2 }
            long r5 = (long) r5     // Catch:{ all -> 0x00c2 }
            r3.seek(r5)     // Catch:{ all -> 0x00c2 }
            byte[] r5 = r9.bufferTmp     // Catch:{ all -> 0x00c2 }
            if (r5 == 0) goto L_0x0094
            int r5 = r5.length     // Catch:{ all -> 0x00c2 }
            int r6 = r10.frameSize     // Catch:{ all -> 0x00c2 }
            if (r5 >= r6) goto L_0x009a
        L_0x0094:
            int r5 = r10.frameSize     // Catch:{ all -> 0x00c2 }
            byte[] r5 = new byte[r5]     // Catch:{ all -> 0x00c2 }
            r9.bufferTmp = r5     // Catch:{ all -> 0x00c2 }
        L_0x009a:
            byte[] r5 = r9.bufferTmp     // Catch:{ all -> 0x00c2 }
            int r6 = r10.frameSize     // Catch:{ all -> 0x00c2 }
            r3.readFully(r5, r4, r6)     // Catch:{ all -> 0x00c2 }
            boolean r5 = r9.recycled     // Catch:{ all -> 0x00c2 }
            if (r5 != 0) goto L_0x00a8
            r9.cachedFile = r3     // Catch:{ all -> 0x00c2 }
            goto L_0x00ad
        L_0x00a8:
            r9.cachedFile = r0     // Catch:{ all -> 0x00c2 }
            r3.close()     // Catch:{ all -> 0x00c2 }
        L_0x00ad:
            monitor-exit(r2)     // Catch:{ all -> 0x00c2 }
            android.graphics.BitmapFactory$Options r0 = new android.graphics.BitmapFactory$Options     // Catch:{ FileNotFoundException -> 0x00c0, all -> 0x00bd }
            r0.<init>()     // Catch:{ FileNotFoundException -> 0x00c0, all -> 0x00bd }
            r0.inBitmap = r11     // Catch:{ FileNotFoundException -> 0x00c0, all -> 0x00bd }
            byte[] r11 = r9.bufferTmp     // Catch:{ FileNotFoundException -> 0x00c0, all -> 0x00bd }
            int r10 = r10.frameSize     // Catch:{ FileNotFoundException -> 0x00c0, all -> 0x00bd }
            android.graphics.BitmapFactory.decodeByteArray(r11, r4, r10, r0)     // Catch:{ FileNotFoundException -> 0x00c0, all -> 0x00bd }
            return r4
        L_0x00bd:
            r10 = move-exception
            r0 = r3
            goto L_0x00c9
        L_0x00c0:
            r0 = r3
            goto L_0x00ce
        L_0x00c2:
            r10 = move-exception
            r0 = r3
            goto L_0x00c6
        L_0x00c5:
            r10 = move-exception
        L_0x00c6:
            monitor-exit(r2)     // Catch:{ all -> 0x00c5 }
            throw r10     // Catch:{ FileNotFoundException -> 0x00cd, all -> 0x00c8 }
        L_0x00c8:
            r10 = move-exception
        L_0x00c9:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r10)
            goto L_0x00ce
        L_0x00cd:
        L_0x00ce:
            if (r0 == 0) goto L_0x00d8
            r0.close()     // Catch:{ IOException -> 0x00d4 }
            goto L_0x00d8
        L_0x00d4:
            r10 = move-exception
            r10.printStackTrace()
        L_0x00d8:
            return r1
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

    private class FrameOffset {
        int frameOffset;
        int frameSize;
        /* access modifiers changed from: package-private */
        public final int index;

        private FrameOffset(BitmapsCache bitmapsCache, int i) {
            this.index = i;
        }
    }

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

        public synchronized void write(int i) {
            ensureCapacity(this.count + 1);
            byte[] bArr = this.buf;
            int i2 = this.count;
            bArr[i2] = (byte) i;
            this.count = i2 + 1;
        }

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
