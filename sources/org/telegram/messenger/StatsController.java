package org.telegram.messenger;

import java.io.RandomAccessFile;

public class StatsController extends BaseController {
    private static volatile StatsController[] Instance = new StatsController[3];
    private static final int TYPES_COUNT = 7;
    public static final int TYPE_AUDIOS = 3;
    public static final int TYPE_CALLS = 0;
    public static final int TYPE_FILES = 5;
    public static final int TYPE_MESSAGES = 1;
    public static final int TYPE_MOBILE = 0;
    public static final int TYPE_PHOTOS = 4;
    public static final int TYPE_ROAMING = 2;
    public static final int TYPE_TOTAL = 6;
    public static final int TYPE_VIDEOS = 2;
    public static final int TYPE_WIFI = 1;
    private static final ThreadLocal<Long> lastStatsSaveTime = new ThreadLocal<Long>() {
        /* access modifiers changed from: protected */
        public Long initialValue() {
            return Long.valueOf(System.currentTimeMillis() - 1000);
        }
    };
    private static DispatchQueue statsSaveQueue = new DispatchQueue("statsSaveQueue");
    private byte[] buffer = new byte[8];
    /* access modifiers changed from: private */
    public int[] callsTotalTime;
    /* access modifiers changed from: private */
    public long lastInternalStatsSaveTime;
    /* access modifiers changed from: private */
    public long[][] receivedBytes;
    /* access modifiers changed from: private */
    public int[][] receivedItems;
    /* access modifiers changed from: private */
    public long[] resetStatsDate;
    private Runnable saveRunnable;
    /* access modifiers changed from: private */
    public long[][] sentBytes;
    /* access modifiers changed from: private */
    public int[][] sentItems;
    /* access modifiers changed from: private */
    public RandomAccessFile statsFile;

    /* access modifiers changed from: private */
    public byte[] intToBytes(int i) {
        byte[] bArr = this.buffer;
        bArr[0] = (byte) (i >>> 24);
        bArr[1] = (byte) (i >>> 16);
        bArr[2] = (byte) (i >>> 8);
        bArr[3] = (byte) i;
        return bArr;
    }

    private int bytesToInt(byte[] bArr) {
        return (bArr[3] & 255) | (bArr[0] << 24) | ((bArr[1] & 255) << 16) | ((bArr[2] & 255) << 8);
    }

    /* access modifiers changed from: private */
    public byte[] longToBytes(long j) {
        byte[] bArr = this.buffer;
        bArr[0] = (byte) ((int) (j >>> 56));
        bArr[1] = (byte) ((int) (j >>> 48));
        bArr[2] = (byte) ((int) (j >>> 40));
        bArr[3] = (byte) ((int) (j >>> 32));
        bArr[4] = (byte) ((int) (j >>> 24));
        bArr[5] = (byte) ((int) (j >>> 16));
        bArr[6] = (byte) ((int) (j >>> 8));
        bArr[7] = (byte) ((int) j);
        return bArr;
    }

    private long bytesToLong(byte[] bArr) {
        return ((((long) bArr[0]) & 255) << 56) | ((((long) bArr[1]) & 255) << 48) | ((((long) bArr[2]) & 255) << 40) | ((((long) bArr[3]) & 255) << 32) | ((((long) bArr[4]) & 255) << 24) | ((((long) bArr[5]) & 255) << 16) | ((((long) bArr[6]) & 255) << 8) | (255 & ((long) bArr[7]));
    }

    public static StatsController getInstance(int i) {
        StatsController statsController = Instance[i];
        if (statsController == null) {
            synchronized (StatsController.class) {
                statsController = Instance[i];
                if (statsController == null) {
                    StatsController[] statsControllerArr = Instance;
                    StatsController statsController2 = new StatsController(i);
                    statsControllerArr[i] = statsController2;
                    statsController = statsController2;
                }
            }
        }
        return statsController;
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    /* JADX WARNING: Removed duplicated region for block: B:23:0x012d  */
    /* JADX WARNING: Removed duplicated region for block: B:47:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private StatsController(int r15) {
        /*
            r14 = this;
            java.lang.Class<int> r0 = int.class
            java.lang.Class<long> r1 = long.class
            r14.<init>(r15)
            r2 = 8
            byte[] r3 = new byte[r2]
            r14.buffer = r3
            r3 = 2
            int[] r4 = new int[r3]
            r4 = {3, 7} // fill-array
            java.lang.Object r4 = java.lang.reflect.Array.newInstance(r1, r4)
            long[][] r4 = (long[][]) r4
            r14.sentBytes = r4
            int[] r4 = new int[r3]
            r4 = {3, 7} // fill-array
            java.lang.Object r1 = java.lang.reflect.Array.newInstance(r1, r4)
            long[][] r1 = (long[][]) r1
            r14.receivedBytes = r1
            int[] r1 = new int[r3]
            r1 = {3, 7} // fill-array
            java.lang.Object r1 = java.lang.reflect.Array.newInstance(r0, r1)
            int[][] r1 = (int[][]) r1
            r14.sentItems = r1
            int[] r1 = new int[r3]
            r1 = {3, 7} // fill-array
            java.lang.Object r0 = java.lang.reflect.Array.newInstance(r0, r1)
            int[][] r0 = (int[][]) r0
            r14.receivedItems = r0
            r0 = 3
            long[] r1 = new long[r0]
            r14.resetStatsDate = r1
            int[] r1 = new int[r0]
            r14.callsTotalTime = r1
            org.telegram.messenger.StatsController$2 r1 = new org.telegram.messenger.StatsController$2
            r1.<init>()
            r14.saveRunnable = r1
            java.io.File r1 = org.telegram.messenger.ApplicationLoader.getFilesDirFixed()
            if (r15 == 0) goto L_0x007a
            java.io.File r1 = new java.io.File
            java.io.File r3 = org.telegram.messenger.ApplicationLoader.getFilesDirFixed()
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = "account"
            r4.append(r5)
            r4.append(r15)
            java.lang.String r5 = "/"
            r4.append(r5)
            java.lang.String r4 = r4.toString()
            r1.<init>(r3, r4)
            r1.mkdirs()
        L_0x007a:
            r3 = 7
            r4 = 0
            r6 = 1
            r7 = 0
            java.io.RandomAccessFile r8 = new java.io.RandomAccessFile     // Catch:{ Exception -> 0x012a }
            java.io.File r9 = new java.io.File     // Catch:{ Exception -> 0x012a }
            java.lang.String r10 = "stats2.dat"
            r9.<init>(r1, r10)     // Catch:{ Exception -> 0x012a }
            java.lang.String r1 = "rw"
            r8.<init>(r9, r1)     // Catch:{ Exception -> 0x012a }
            r14.statsFile = r8     // Catch:{ Exception -> 0x012a }
            long r8 = r8.length()     // Catch:{ Exception -> 0x012a }
            int r1 = (r8 > r4 ? 1 : (r8 == r4 ? 0 : -1))
            if (r1 <= 0) goto L_0x012a
            r1 = 0
            r8 = 0
        L_0x0099:
            if (r1 >= r0) goto L_0x0123
            r9 = 0
        L_0x009c:
            r10 = 4
            if (r9 >= r3) goto L_0x00ee
            java.io.RandomAccessFile r11 = r14.statsFile     // Catch:{ Exception -> 0x012a }
            byte[] r12 = r14.buffer     // Catch:{ Exception -> 0x012a }
            r11.readFully(r12, r7, r2)     // Catch:{ Exception -> 0x012a }
            long[][] r11 = r14.sentBytes     // Catch:{ Exception -> 0x012a }
            r11 = r11[r1]     // Catch:{ Exception -> 0x012a }
            byte[] r12 = r14.buffer     // Catch:{ Exception -> 0x012a }
            long r12 = r14.bytesToLong(r12)     // Catch:{ Exception -> 0x012a }
            r11[r9] = r12     // Catch:{ Exception -> 0x012a }
            java.io.RandomAccessFile r11 = r14.statsFile     // Catch:{ Exception -> 0x012a }
            byte[] r12 = r14.buffer     // Catch:{ Exception -> 0x012a }
            r11.readFully(r12, r7, r2)     // Catch:{ Exception -> 0x012a }
            long[][] r11 = r14.receivedBytes     // Catch:{ Exception -> 0x012a }
            r11 = r11[r1]     // Catch:{ Exception -> 0x012a }
            byte[] r12 = r14.buffer     // Catch:{ Exception -> 0x012a }
            long r12 = r14.bytesToLong(r12)     // Catch:{ Exception -> 0x012a }
            r11[r9] = r12     // Catch:{ Exception -> 0x012a }
            java.io.RandomAccessFile r11 = r14.statsFile     // Catch:{ Exception -> 0x012a }
            byte[] r12 = r14.buffer     // Catch:{ Exception -> 0x012a }
            r11.readFully(r12, r7, r10)     // Catch:{ Exception -> 0x012a }
            int[][] r11 = r14.sentItems     // Catch:{ Exception -> 0x012a }
            r11 = r11[r1]     // Catch:{ Exception -> 0x012a }
            byte[] r12 = r14.buffer     // Catch:{ Exception -> 0x012a }
            int r12 = r14.bytesToInt(r12)     // Catch:{ Exception -> 0x012a }
            r11[r9] = r12     // Catch:{ Exception -> 0x012a }
            java.io.RandomAccessFile r11 = r14.statsFile     // Catch:{ Exception -> 0x012a }
            byte[] r12 = r14.buffer     // Catch:{ Exception -> 0x012a }
            r11.readFully(r12, r7, r10)     // Catch:{ Exception -> 0x012a }
            int[][] r10 = r14.receivedItems     // Catch:{ Exception -> 0x012a }
            r10 = r10[r1]     // Catch:{ Exception -> 0x012a }
            byte[] r11 = r14.buffer     // Catch:{ Exception -> 0x012a }
            int r11 = r14.bytesToInt(r11)     // Catch:{ Exception -> 0x012a }
            r10[r9] = r11     // Catch:{ Exception -> 0x012a }
            int r9 = r9 + 1
            goto L_0x009c
        L_0x00ee:
            java.io.RandomAccessFile r9 = r14.statsFile     // Catch:{ Exception -> 0x012a }
            byte[] r11 = r14.buffer     // Catch:{ Exception -> 0x012a }
            r9.readFully(r11, r7, r10)     // Catch:{ Exception -> 0x012a }
            int[] r9 = r14.callsTotalTime     // Catch:{ Exception -> 0x012a }
            byte[] r10 = r14.buffer     // Catch:{ Exception -> 0x012a }
            int r10 = r14.bytesToInt(r10)     // Catch:{ Exception -> 0x012a }
            r9[r1] = r10     // Catch:{ Exception -> 0x012a }
            java.io.RandomAccessFile r9 = r14.statsFile     // Catch:{ Exception -> 0x012a }
            byte[] r10 = r14.buffer     // Catch:{ Exception -> 0x012a }
            r9.readFully(r10, r7, r2)     // Catch:{ Exception -> 0x012a }
            long[] r9 = r14.resetStatsDate     // Catch:{ Exception -> 0x012a }
            byte[] r10 = r14.buffer     // Catch:{ Exception -> 0x012a }
            long r10 = r14.bytesToLong(r10)     // Catch:{ Exception -> 0x012a }
            r9[r1] = r10     // Catch:{ Exception -> 0x012a }
            long[] r9 = r14.resetStatsDate     // Catch:{ Exception -> 0x012a }
            r10 = r9[r1]     // Catch:{ Exception -> 0x012a }
            int r12 = (r10 > r4 ? 1 : (r10 == r4 ? 0 : -1))
            if (r12 != 0) goto L_0x011f
            long r10 = java.lang.System.currentTimeMillis()     // Catch:{ Exception -> 0x012a }
            r9[r1] = r10     // Catch:{ Exception -> 0x012a }
            r8 = 1
        L_0x011f:
            int r1 = r1 + 1
            goto L_0x0099
        L_0x0123:
            if (r8 == 0) goto L_0x0128
            r14.saveStats()     // Catch:{ Exception -> 0x012a }
        L_0x0128:
            r1 = 0
            goto L_0x012b
        L_0x012a:
            r1 = 1
        L_0x012b:
            if (r1 == 0) goto L_0x0228
            java.lang.String r1 = "stats"
            if (r15 != 0) goto L_0x0138
            android.content.Context r15 = org.telegram.messenger.ApplicationLoader.applicationContext
            android.content.SharedPreferences r15 = r15.getSharedPreferences(r1, r7)
            goto L_0x014d
        L_0x0138:
            android.content.Context r2 = org.telegram.messenger.ApplicationLoader.applicationContext
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            r8.append(r1)
            r8.append(r15)
            java.lang.String r15 = r8.toString()
            android.content.SharedPreferences r15 = r2.getSharedPreferences(r15, r7)
        L_0x014d:
            r1 = 0
            r2 = 0
        L_0x014f:
            if (r1 >= r0) goto L_0x0223
            int[] r8 = r14.callsTotalTime
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            java.lang.String r10 = "callsTotalTime"
            r9.append(r10)
            r9.append(r1)
            java.lang.String r9 = r9.toString()
            int r9 = r15.getInt(r9, r7)
            r8[r1] = r9
            long[] r8 = r14.resetStatsDate
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            java.lang.String r10 = "resetStatsDate"
            r9.append(r10)
            r9.append(r1)
            java.lang.String r9 = r9.toString()
            long r9 = r15.getLong(r9, r4)
            r8[r1] = r9
            r8 = 0
        L_0x0184:
            if (r8 >= r3) goto L_0x0210
            long[][] r9 = r14.sentBytes
            r9 = r9[r1]
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            java.lang.String r11 = "sentBytes"
            r10.append(r11)
            r10.append(r1)
            java.lang.String r11 = "_"
            r10.append(r11)
            r10.append(r8)
            java.lang.String r10 = r10.toString()
            long r12 = r15.getLong(r10, r4)
            r9[r8] = r12
            long[][] r9 = r14.receivedBytes
            r9 = r9[r1]
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            java.lang.String r12 = "receivedBytes"
            r10.append(r12)
            r10.append(r1)
            r10.append(r11)
            r10.append(r8)
            java.lang.String r10 = r10.toString()
            long r12 = r15.getLong(r10, r4)
            r9[r8] = r12
            int[][] r9 = r14.sentItems
            r9 = r9[r1]
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            java.lang.String r12 = "sentItems"
            r10.append(r12)
            r10.append(r1)
            r10.append(r11)
            r10.append(r8)
            java.lang.String r10 = r10.toString()
            int r10 = r15.getInt(r10, r7)
            r9[r8] = r10
            int[][] r9 = r14.receivedItems
            r9 = r9[r1]
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            java.lang.String r12 = "receivedItems"
            r10.append(r12)
            r10.append(r1)
            r10.append(r11)
            r10.append(r8)
            java.lang.String r10 = r10.toString()
            int r10 = r15.getInt(r10, r7)
            r9[r8] = r10
            int r8 = r8 + 1
            goto L_0x0184
        L_0x0210:
            long[] r8 = r14.resetStatsDate
            r9 = r8[r1]
            int r11 = (r9 > r4 ? 1 : (r9 == r4 ? 0 : -1))
            if (r11 != 0) goto L_0x021f
            long r9 = java.lang.System.currentTimeMillis()
            r8[r1] = r9
            r2 = 1
        L_0x021f:
            int r1 = r1 + 1
            goto L_0x014f
        L_0x0223:
            if (r2 == 0) goto L_0x0228
            r14.saveStats()
        L_0x0228:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.StatsController.<init>(int):void");
    }

    public void incrementReceivedItemsCount(int i, int i2, int i3) {
        int[] iArr = this.receivedItems[i];
        iArr[i2] = iArr[i2] + i3;
        saveStats();
    }

    public void incrementSentItemsCount(int i, int i2, int i3) {
        int[] iArr = this.sentItems[i];
        iArr[i2] = iArr[i2] + i3;
        saveStats();
    }

    public void incrementReceivedBytesCount(int i, int i2, long j) {
        long[] jArr = this.receivedBytes[i];
        jArr[i2] = jArr[i2] + j;
        saveStats();
    }

    public void incrementSentBytesCount(int i, int i2, long j) {
        long[] jArr = this.sentBytes[i];
        jArr[i2] = jArr[i2] + j;
        saveStats();
    }

    public void incrementTotalCallsTime(int i, int i2) {
        int[] iArr = this.callsTotalTime;
        iArr[i] = iArr[i] + i2;
        saveStats();
    }

    public int getRecivedItemsCount(int i, int i2) {
        return this.receivedItems[i][i2];
    }

    public int getSentItemsCount(int i, int i2) {
        return this.sentItems[i][i2];
    }

    public long getSentBytesCount(int i, int i2) {
        if (i2 != 1) {
            return this.sentBytes[i][i2];
        }
        long[][] jArr = this.sentBytes;
        return (((jArr[i][6] - jArr[i][5]) - jArr[i][3]) - jArr[i][2]) - jArr[i][4];
    }

    public long getReceivedBytesCount(int i, int i2) {
        if (i2 != 1) {
            return this.receivedBytes[i][i2];
        }
        long[][] jArr = this.receivedBytes;
        return (((jArr[i][6] - jArr[i][5]) - jArr[i][3]) - jArr[i][2]) - jArr[i][4];
    }

    public int getCallsTotalTime(int i) {
        return this.callsTotalTime[i];
    }

    public long getResetStatsDate(int i) {
        return this.resetStatsDate[i];
    }

    public void resetStats(int i) {
        this.resetStatsDate[i] = System.currentTimeMillis();
        for (int i2 = 0; i2 < 7; i2++) {
            this.sentBytes[i][i2] = 0;
            this.receivedBytes[i][i2] = 0;
            this.sentItems[i][i2] = 0;
            this.receivedItems[i][i2] = 0;
        }
        this.callsTotalTime[i] = 0;
        saveStats();
    }

    private void saveStats() {
        long currentTimeMillis = System.currentTimeMillis();
        ThreadLocal<Long> threadLocal = lastStatsSaveTime;
        if (Math.abs(currentTimeMillis - threadLocal.get().longValue()) >= 2000) {
            threadLocal.set(Long.valueOf(currentTimeMillis));
            statsSaveQueue.postRunnable(this.saveRunnable);
        }
    }
}
