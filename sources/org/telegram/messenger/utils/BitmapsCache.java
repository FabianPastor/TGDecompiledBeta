package org.telegram.messenger.utils;

import android.graphics.Bitmap;
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
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.Utilities;

public class BitmapsCache {
    private static final int N = Utilities.clamp(Runtime.getRuntime().availableProcessors() - 2, 8, 1);
    private static ThreadPoolExecutor bitmapCompressExecutor;
    byte[] bufferTmp;
    volatile boolean cacheCreated;
    RandomAccessFile cachedFile;
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

    public BitmapsCache(File file2, Cacheable cacheable, CacheOptions cacheOptions, int i, int i2) {
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
        this.file = new File(file3, this.fileName + "_" + i + "_" + i2 + ".pcache2");
    }

    /* JADX WARNING: Can't wrap try/catch for region: R(17:1|2|(3:4|5|(3:7|8|9)(2:10|11))|12|13|(2:16|14)|68|17|(4:18|(2:20|21)|25|(1:69)(3:53|(2:55|71)(2:56|70)|57))|27|(9:30|(2:32|33)|37|(2:39|40)|41|42|(2:44|73)(1:74)|45|28)|72|46|(2:49|47)|75|50|(1:52)) */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [] */
    /* JADX WARNING: Missing exception handler attribute for start block: B:12:0x002f */
    /* JADX WARNING: Missing exception handler attribute for start block: B:41:0x00b5 */
    /* JADX WARNING: Removed duplicated region for block: B:16:0x0047 A[Catch:{ FileNotFoundException -> 0x019c, IOException -> 0x0197, all -> 0x0195 }, LOOP:0: B:14:0x0043->B:16:0x0047, LOOP_END] */
    /* JADX WARNING: Removed duplicated region for block: B:20:0x0080 A[SYNTHETIC, Splitter:B:20:0x0080] */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x009e A[Catch:{ FileNotFoundException -> 0x019c, IOException -> 0x0197, all -> 0x0195 }] */
    /* JADX WARNING: Removed duplicated region for block: B:44:0x00b9 A[Catch:{ FileNotFoundException -> 0x019c, IOException -> 0x0197, all -> 0x0195 }] */
    /* JADX WARNING: Removed duplicated region for block: B:49:0x00dd A[Catch:{ FileNotFoundException -> 0x019c, IOException -> 0x0197, all -> 0x0195 }, LOOP:3: B:47:0x00d7->B:49:0x00dd, LOOP_END] */
    /* JADX WARNING: Removed duplicated region for block: B:52:0x0106 A[Catch:{ FileNotFoundException -> 0x019c, IOException -> 0x0197, all -> 0x0195 }] */
    /* JADX WARNING: Removed duplicated region for block: B:53:0x014c A[Catch:{ FileNotFoundException -> 0x019c, IOException -> 0x0197, all -> 0x0195 }] */
    /* JADX WARNING: Removed duplicated region for block: B:69:0x0099 A[EDGE_INSN: B:69:0x0099->B:27:0x0099 ?: BREAK  , SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:74:0x00be A[SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void createCache() {
        /*
            r26 = this;
            r10 = r26
            java.lang.String r0 = "rw"
            long r11 = java.lang.System.currentTimeMillis()     // Catch:{ FileNotFoundException -> 0x019c, IOException -> 0x0197 }
            java.io.RandomAccessFile r1 = new java.io.RandomAccessFile     // Catch:{ FileNotFoundException -> 0x019c, IOException -> 0x0197 }
            java.io.File r2 = r10.file     // Catch:{ FileNotFoundException -> 0x019c, IOException -> 0x0197 }
            r1.<init>(r2, r0)     // Catch:{ FileNotFoundException -> 0x019c, IOException -> 0x0197 }
            java.io.File r2 = r10.file     // Catch:{ FileNotFoundException -> 0x019c, IOException -> 0x0197 }
            boolean r2 = r2.exists()     // Catch:{ FileNotFoundException -> 0x019c, IOException -> 0x0197 }
            if (r2 == 0) goto L_0x002f
            boolean r2 = r1.readBoolean()     // Catch:{ Exception -> 0x002f }
            r10.cacheCreated = r2     // Catch:{ Exception -> 0x002f }
            boolean r2 = r10.cacheCreated     // Catch:{ Exception -> 0x002f }
            if (r2 == 0) goto L_0x002a
            r1.close()     // Catch:{ Exception -> 0x002f }
            org.telegram.messenger.utils.BitmapsCache$Cacheable r0 = r10.source
            r0.releaseForGenerateCache()
            return
        L_0x002a:
            java.io.File r2 = r10.file     // Catch:{ Exception -> 0x002f }
            r2.delete()     // Catch:{ Exception -> 0x002f }
        L_0x002f:
            r1.close()     // Catch:{ FileNotFoundException -> 0x019c, IOException -> 0x0197 }
            java.io.RandomAccessFile r13 = new java.io.RandomAccessFile     // Catch:{ FileNotFoundException -> 0x019c, IOException -> 0x0197 }
            java.io.File r1 = r10.file     // Catch:{ FileNotFoundException -> 0x019c, IOException -> 0x0197 }
            r13.<init>(r1, r0)     // Catch:{ FileNotFoundException -> 0x019c, IOException -> 0x0197 }
            int r0 = N     // Catch:{ FileNotFoundException -> 0x019c, IOException -> 0x0197 }
            android.graphics.Bitmap[] r14 = new android.graphics.Bitmap[r0]     // Catch:{ FileNotFoundException -> 0x019c, IOException -> 0x0197 }
            org.telegram.messenger.utils.BitmapsCache$ByteArrayOutputStream[] r15 = new org.telegram.messenger.utils.BitmapsCache.ByteArrayOutputStream[r0]     // Catch:{ FileNotFoundException -> 0x019c, IOException -> 0x0197 }
            java.util.concurrent.CountDownLatch[] r9 = new java.util.concurrent.CountDownLatch[r0]     // Catch:{ FileNotFoundException -> 0x019c, IOException -> 0x0197 }
            r8 = 0
            r0 = 0
        L_0x0043:
            int r1 = N     // Catch:{ FileNotFoundException -> 0x019c, IOException -> 0x0197 }
            if (r0 >= r1) goto L_0x0065
            int r1 = r10.w     // Catch:{ FileNotFoundException -> 0x019c, IOException -> 0x0197 }
            int r2 = r10.h     // Catch:{ FileNotFoundException -> 0x019c, IOException -> 0x0197 }
            android.graphics.Bitmap$Config r3 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ FileNotFoundException -> 0x019c, IOException -> 0x0197 }
            android.graphics.Bitmap r1 = android.graphics.Bitmap.createBitmap(r1, r2, r3)     // Catch:{ FileNotFoundException -> 0x019c, IOException -> 0x0197 }
            r14[r0] = r1     // Catch:{ FileNotFoundException -> 0x019c, IOException -> 0x0197 }
            org.telegram.messenger.utils.BitmapsCache$ByteArrayOutputStream r1 = new org.telegram.messenger.utils.BitmapsCache$ByteArrayOutputStream     // Catch:{ FileNotFoundException -> 0x019c, IOException -> 0x0197 }
            int r2 = r10.w     // Catch:{ FileNotFoundException -> 0x019c, IOException -> 0x0197 }
            int r3 = r10.h     // Catch:{ FileNotFoundException -> 0x019c, IOException -> 0x0197 }
            int r2 = r2 * r3
            int r2 = r2 * 2
            r1.<init>(r2)     // Catch:{ FileNotFoundException -> 0x019c, IOException -> 0x0197 }
            r15[r0] = r1     // Catch:{ FileNotFoundException -> 0x019c, IOException -> 0x0197 }
            int r0 = r0 + 1
            goto L_0x0043
        L_0x0065:
            java.util.ArrayList r7 = new java.util.ArrayList     // Catch:{ FileNotFoundException -> 0x019c, IOException -> 0x0197 }
            r7.<init>()     // Catch:{ FileNotFoundException -> 0x019c, IOException -> 0x0197 }
            r13.writeBoolean(r8)     // Catch:{ FileNotFoundException -> 0x019c, IOException -> 0x0197 }
            r13.writeInt(r8)     // Catch:{ FileNotFoundException -> 0x019c, IOException -> 0x0197 }
            org.telegram.messenger.utils.BitmapsCache$Cacheable r0 = r10.source     // Catch:{ FileNotFoundException -> 0x019c, IOException -> 0x0197 }
            r0.prepareForGenerateCache()     // Catch:{ FileNotFoundException -> 0x019c, IOException -> 0x0197 }
            r5 = 0
            r1 = r5
            r16 = 0
            r17 = 0
        L_0x007c:
            r0 = r9[r16]     // Catch:{ FileNotFoundException -> 0x019c, IOException -> 0x0197 }
            if (r0 == 0) goto L_0x008a
            r0 = r9[r16]     // Catch:{ InterruptedException -> 0x0086 }
            r0.await()     // Catch:{ InterruptedException -> 0x0086 }
            goto L_0x008a
        L_0x0086:
            r0 = move-exception
            r0.printStackTrace()     // Catch:{ FileNotFoundException -> 0x019c, IOException -> 0x0197 }
        L_0x008a:
            long r3 = java.lang.System.currentTimeMillis()     // Catch:{ FileNotFoundException -> 0x019c, IOException -> 0x0197 }
            org.telegram.messenger.utils.BitmapsCache$Cacheable r0 = r10.source     // Catch:{ FileNotFoundException -> 0x019c, IOException -> 0x0197 }
            r8 = r14[r16]     // Catch:{ FileNotFoundException -> 0x019c, IOException -> 0x0197 }
            int r0 = r0.getNextFrame(r8)     // Catch:{ FileNotFoundException -> 0x019c, IOException -> 0x0197 }
            r8 = 1
            if (r0 == r8) goto L_0x014c
            r3 = 0
        L_0x009a:
            int r0 = N     // Catch:{ FileNotFoundException -> 0x019c, IOException -> 0x0197 }
            if (r3 >= r0) goto L_0x00c1
            r0 = r9[r3]     // Catch:{ FileNotFoundException -> 0x019c, IOException -> 0x0197 }
            if (r0 == 0) goto L_0x00ac
            r0 = r9[r3]     // Catch:{ InterruptedException -> 0x00a8 }
            r0.await()     // Catch:{ InterruptedException -> 0x00a8 }
            goto L_0x00ac
        L_0x00a8:
            r0 = move-exception
            r0.printStackTrace()     // Catch:{ FileNotFoundException -> 0x019c, IOException -> 0x0197 }
        L_0x00ac:
            r0 = r14[r3]     // Catch:{ FileNotFoundException -> 0x019c, IOException -> 0x0197 }
            if (r0 == 0) goto L_0x00b5
            r0 = r14[r3]     // Catch:{ Exception -> 0x00b5 }
            r0.recycle()     // Catch:{ Exception -> 0x00b5 }
        L_0x00b5:
            r0 = r15[r3]     // Catch:{ FileNotFoundException -> 0x019c, IOException -> 0x0197 }
            if (r0 == 0) goto L_0x00be
            r0 = r15[r3]     // Catch:{ FileNotFoundException -> 0x019c, IOException -> 0x0197 }
            r4 = 0
            r0.buf = r4     // Catch:{ FileNotFoundException -> 0x019c, IOException -> 0x0197 }
        L_0x00be:
            int r3 = r3 + 1
            goto L_0x009a
        L_0x00c1:
            long r3 = r13.length()     // Catch:{ FileNotFoundException -> 0x019c, IOException -> 0x0197 }
            int r0 = (int) r3     // Catch:{ FileNotFoundException -> 0x019c, IOException -> 0x0197 }
            org.telegram.messenger.utils.BitmapsCache$$ExternalSyntheticLambda1 r3 = org.telegram.messenger.utils.BitmapsCache$$ExternalSyntheticLambda1.INSTANCE     // Catch:{ FileNotFoundException -> 0x019c, IOException -> 0x0197 }
            java.util.Comparator r3 = j$.util.Comparator$CC.comparingInt(r3)     // Catch:{ FileNotFoundException -> 0x019c, IOException -> 0x0197 }
            java.util.Collections.sort(r7, r3)     // Catch:{ FileNotFoundException -> 0x019c, IOException -> 0x0197 }
            int r3 = r7.size()     // Catch:{ FileNotFoundException -> 0x019c, IOException -> 0x0197 }
            r13.writeInt(r3)     // Catch:{ FileNotFoundException -> 0x019c, IOException -> 0x0197 }
            r3 = 0
        L_0x00d7:
            int r4 = r7.size()     // Catch:{ FileNotFoundException -> 0x019c, IOException -> 0x0197 }
            if (r3 >= r4) goto L_0x00f6
            java.lang.Object r4 = r7.get(r3)     // Catch:{ FileNotFoundException -> 0x019c, IOException -> 0x0197 }
            org.telegram.messenger.utils.BitmapsCache$FrameOffset r4 = (org.telegram.messenger.utils.BitmapsCache.FrameOffset) r4     // Catch:{ FileNotFoundException -> 0x019c, IOException -> 0x0197 }
            int r4 = r4.frameOffset     // Catch:{ FileNotFoundException -> 0x019c, IOException -> 0x0197 }
            r13.writeInt(r4)     // Catch:{ FileNotFoundException -> 0x019c, IOException -> 0x0197 }
            java.lang.Object r4 = r7.get(r3)     // Catch:{ FileNotFoundException -> 0x019c, IOException -> 0x0197 }
            org.telegram.messenger.utils.BitmapsCache$FrameOffset r4 = (org.telegram.messenger.utils.BitmapsCache.FrameOffset) r4     // Catch:{ FileNotFoundException -> 0x019c, IOException -> 0x0197 }
            int r4 = r4.frameSize     // Catch:{ FileNotFoundException -> 0x019c, IOException -> 0x0197 }
            r13.writeInt(r4)     // Catch:{ FileNotFoundException -> 0x019c, IOException -> 0x0197 }
            int r3 = r3 + 1
            goto L_0x00d7
        L_0x00f6:
            r13.seek(r5)     // Catch:{ FileNotFoundException -> 0x019c, IOException -> 0x0197 }
            r13.writeBoolean(r8)     // Catch:{ FileNotFoundException -> 0x019c, IOException -> 0x0197 }
            r13.writeInt(r0)     // Catch:{ FileNotFoundException -> 0x019c, IOException -> 0x0197 }
            r13.close()     // Catch:{ FileNotFoundException -> 0x019c, IOException -> 0x0197 }
            boolean r0 = org.telegram.messenger.BuildVars.DEBUG_VERSION     // Catch:{ FileNotFoundException -> 0x019c, IOException -> 0x0197 }
            if (r0 == 0) goto L_0x01a0
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ FileNotFoundException -> 0x019c, IOException -> 0x0197 }
            r0.<init>()     // Catch:{ FileNotFoundException -> 0x019c, IOException -> 0x0197 }
            java.lang.String r3 = "generate cache for time = "
            r0.append(r3)     // Catch:{ FileNotFoundException -> 0x019c, IOException -> 0x0197 }
            long r3 = java.lang.System.currentTimeMillis()     // Catch:{ FileNotFoundException -> 0x019c, IOException -> 0x0197 }
            long r3 = r3 - r11
            r0.append(r3)     // Catch:{ FileNotFoundException -> 0x019c, IOException -> 0x0197 }
            java.lang.String r3 = " drawFrameTime = "
            r0.append(r3)     // Catch:{ FileNotFoundException -> 0x019c, IOException -> 0x0197 }
            r0.append(r1)     // Catch:{ FileNotFoundException -> 0x019c, IOException -> 0x0197 }
            java.lang.String r1 = " comressTime = "
            r0.append(r1)     // Catch:{ FileNotFoundException -> 0x019c, IOException -> 0x0197 }
            r0.append(r5)     // Catch:{ FileNotFoundException -> 0x019c, IOException -> 0x0197 }
            java.lang.String r1 = " fileSize = "
            r0.append(r1)     // Catch:{ FileNotFoundException -> 0x019c, IOException -> 0x0197 }
            java.io.File r1 = r10.file     // Catch:{ FileNotFoundException -> 0x019c, IOException -> 0x0197 }
            long r1 = r1.length()     // Catch:{ FileNotFoundException -> 0x019c, IOException -> 0x0197 }
            java.lang.String r1 = org.telegram.messenger.AndroidUtilities.formatFileSize(r1)     // Catch:{ FileNotFoundException -> 0x019c, IOException -> 0x0197 }
            r0.append(r1)     // Catch:{ FileNotFoundException -> 0x019c, IOException -> 0x0197 }
            java.lang.String r1 = " "
            r0.append(r1)     // Catch:{ FileNotFoundException -> 0x019c, IOException -> 0x0197 }
            java.lang.String r1 = r10.fileName     // Catch:{ FileNotFoundException -> 0x019c, IOException -> 0x0197 }
            r0.append(r1)     // Catch:{ FileNotFoundException -> 0x019c, IOException -> 0x0197 }
            java.lang.String r0 = r0.toString()     // Catch:{ FileNotFoundException -> 0x019c, IOException -> 0x0197 }
            org.telegram.messenger.FileLog.d(r0)     // Catch:{ FileNotFoundException -> 0x019c, IOException -> 0x0197 }
            goto L_0x01a0
        L_0x014c:
            long r19 = java.lang.System.currentTimeMillis()     // Catch:{ FileNotFoundException -> 0x019c, IOException -> 0x0197 }
            long r19 = r19 - r3
            long r19 = r1 + r19
            java.util.concurrent.CountDownLatch r0 = new java.util.concurrent.CountDownLatch     // Catch:{ FileNotFoundException -> 0x019c, IOException -> 0x0197 }
            r0.<init>(r8)     // Catch:{ FileNotFoundException -> 0x019c, IOException -> 0x0197 }
            r9[r16] = r0     // Catch:{ FileNotFoundException -> 0x019c, IOException -> 0x0197 }
            java.util.concurrent.ThreadPoolExecutor r0 = bitmapCompressExecutor     // Catch:{ FileNotFoundException -> 0x019c, IOException -> 0x0197 }
            org.telegram.messenger.utils.BitmapsCache$$ExternalSyntheticLambda0 r8 = new org.telegram.messenger.utils.BitmapsCache$$ExternalSyntheticLambda0     // Catch:{ FileNotFoundException -> 0x019c, IOException -> 0x0197 }
            r1 = r8
            r2 = r26
            r3 = r14
            r4 = r16
            r21 = r5
            r5 = r15
            r6 = r17
            r23 = r7
            r7 = r13
            r24 = r11
            r12 = 0
            r11 = r8
            r8 = r23
            r18 = r9
            r1.<init>(r2, r3, r4, r5, r6, r7, r8, r9)     // Catch:{ FileNotFoundException -> 0x019c, IOException -> 0x0197 }
            r0.execute(r11)     // Catch:{ FileNotFoundException -> 0x019c, IOException -> 0x0197 }
            int r0 = r16 + 1
            int r17 = r17 + 1
            int r1 = N     // Catch:{ FileNotFoundException -> 0x019c, IOException -> 0x0197 }
            if (r0 < r1) goto L_0x0186
            r16 = 0
            goto L_0x0188
        L_0x0186:
            r16 = r0
        L_0x0188:
            r9 = r18
            r1 = r19
            r5 = r21
            r7 = r23
            r11 = r24
            r8 = 0
            goto L_0x007c
        L_0x0195:
            r0 = move-exception
            goto L_0x01a6
        L_0x0197:
            r0 = move-exception
            r0.printStackTrace()     // Catch:{ all -> 0x0195 }
            goto L_0x01a0
        L_0x019c:
            r0 = move-exception
            r0.printStackTrace()     // Catch:{ all -> 0x0195 }
        L_0x01a0:
            org.telegram.messenger.utils.BitmapsCache$Cacheable r0 = r10.source
            r0.releaseForGenerateCache()
            return
        L_0x01a6:
            org.telegram.messenger.utils.BitmapsCache$Cacheable r1 = r10.source
            r1.releaseForGenerateCache()
            goto L_0x01ad
        L_0x01ac:
            throw r0
        L_0x01ad:
            goto L_0x01ac
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.utils.BitmapsCache.createCache():void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createCache$0(Bitmap[] bitmapArr, int i, ByteArrayOutputStream[] byteArrayOutputStreamArr, int i2, RandomAccessFile randomAccessFile, ArrayList arrayList, CountDownLatch[] countDownLatchArr) {
        bitmapArr[i].compress(Bitmap.CompressFormat.WEBP, this.compressQuality, byteArrayOutputStreamArr[i]);
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

    /* JADX WARNING: Code restructure failed: missing block: B:47:?, code lost:
        r0 = new android.graphics.BitmapFactory.Options();
        r0.inBitmap = r11;
        android.graphics.BitmapFactory.decodeByteArray(r9.bufferTmp, 0, r10.frameSize, r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:48:0x00bc, code lost:
        return 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:49:0x00bd, code lost:
        r10 = e;
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
            java.lang.Object r2 = r9.mutex     // Catch:{ FileNotFoundException -> 0x00cd, IOException -> 0x00c8 }
            monitor-enter(r2)     // Catch:{ FileNotFoundException -> 0x00cd, IOException -> 0x00c8 }
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
            android.graphics.BitmapFactory$Options r0 = new android.graphics.BitmapFactory$Options     // Catch:{ FileNotFoundException -> 0x00c0, IOException -> 0x00bd }
            r0.<init>()     // Catch:{ FileNotFoundException -> 0x00c0, IOException -> 0x00bd }
            r0.inBitmap = r11     // Catch:{ FileNotFoundException -> 0x00c0, IOException -> 0x00bd }
            byte[] r11 = r9.bufferTmp     // Catch:{ FileNotFoundException -> 0x00c0, IOException -> 0x00bd }
            int r10 = r10.frameSize     // Catch:{ FileNotFoundException -> 0x00c0, IOException -> 0x00bd }
            android.graphics.BitmapFactory.decodeByteArray(r11, r4, r10, r0)     // Catch:{ FileNotFoundException -> 0x00c0, IOException -> 0x00bd }
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
            throw r10     // Catch:{ FileNotFoundException -> 0x00cd, IOException -> 0x00c8 }
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
