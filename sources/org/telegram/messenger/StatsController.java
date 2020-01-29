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
    /* JADX WARNING: Removed duplicated region for block: B:23:0x012c  */
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
            r3 = 7
            r4 = 3
            int[] r5 = new int[]{r4, r3}
            java.lang.Object r5 = java.lang.reflect.Array.newInstance(r1, r5)
            long[][] r5 = (long[][]) r5
            r14.sentBytes = r5
            int[] r5 = new int[]{r4, r3}
            java.lang.Object r1 = java.lang.reflect.Array.newInstance(r1, r5)
            long[][] r1 = (long[][]) r1
            r14.receivedBytes = r1
            int[] r1 = new int[]{r4, r3}
            java.lang.Object r1 = java.lang.reflect.Array.newInstance(r0, r1)
            int[][] r1 = (int[][]) r1
            r14.sentItems = r1
            int[] r1 = new int[]{r4, r3}
            java.lang.Object r0 = java.lang.reflect.Array.newInstance(r0, r1)
            int[][] r0 = (int[][]) r0
            r14.receivedItems = r0
            long[] r0 = new long[r4]
            r14.resetStatsDate = r0
            int[] r0 = new int[r4]
            r14.callsTotalTime = r0
            org.telegram.messenger.StatsController$2 r0 = new org.telegram.messenger.StatsController$2
            r0.<init>()
            r14.saveRunnable = r0
            java.io.File r0 = org.telegram.messenger.ApplicationLoader.getFilesDirFixed()
            if (r15 == 0) goto L_0x0076
            java.io.File r0 = new java.io.File
            java.io.File r1 = org.telegram.messenger.ApplicationLoader.getFilesDirFixed()
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r6 = "account"
            r5.append(r6)
            r5.append(r15)
            java.lang.String r6 = "/"
            r5.append(r6)
            java.lang.String r5 = r5.toString()
            r0.<init>(r1, r5)
            r0.mkdirs()
        L_0x0076:
            r5 = 0
            r1 = 1
            r7 = 0
            java.io.RandomAccessFile r8 = new java.io.RandomAccessFile     // Catch:{ Exception -> 0x0129 }
            java.io.File r9 = new java.io.File     // Catch:{ Exception -> 0x0129 }
            java.lang.String r10 = "stats2.dat"
            r9.<init>(r0, r10)     // Catch:{ Exception -> 0x0129 }
            java.lang.String r0 = "rw"
            r8.<init>(r9, r0)     // Catch:{ Exception -> 0x0129 }
            r14.statsFile = r8     // Catch:{ Exception -> 0x0129 }
            java.io.RandomAccessFile r0 = r14.statsFile     // Catch:{ Exception -> 0x0129 }
            long r8 = r0.length()     // Catch:{ Exception -> 0x0129 }
            int r0 = (r8 > r5 ? 1 : (r8 == r5 ? 0 : -1))
            if (r0 <= 0) goto L_0x0129
            r0 = 0
            r8 = 0
        L_0x0096:
            if (r0 >= r4) goto L_0x0122
            r9 = 0
        L_0x0099:
            r10 = 4
            if (r9 >= r3) goto L_0x00eb
            java.io.RandomAccessFile r11 = r14.statsFile     // Catch:{ Exception -> 0x0129 }
            byte[] r12 = r14.buffer     // Catch:{ Exception -> 0x0129 }
            r11.readFully(r12, r7, r2)     // Catch:{ Exception -> 0x0129 }
            long[][] r11 = r14.sentBytes     // Catch:{ Exception -> 0x0129 }
            r11 = r11[r0]     // Catch:{ Exception -> 0x0129 }
            byte[] r12 = r14.buffer     // Catch:{ Exception -> 0x0129 }
            long r12 = r14.bytesToLong(r12)     // Catch:{ Exception -> 0x0129 }
            r11[r9] = r12     // Catch:{ Exception -> 0x0129 }
            java.io.RandomAccessFile r11 = r14.statsFile     // Catch:{ Exception -> 0x0129 }
            byte[] r12 = r14.buffer     // Catch:{ Exception -> 0x0129 }
            r11.readFully(r12, r7, r2)     // Catch:{ Exception -> 0x0129 }
            long[][] r11 = r14.receivedBytes     // Catch:{ Exception -> 0x0129 }
            r11 = r11[r0]     // Catch:{ Exception -> 0x0129 }
            byte[] r12 = r14.buffer     // Catch:{ Exception -> 0x0129 }
            long r12 = r14.bytesToLong(r12)     // Catch:{ Exception -> 0x0129 }
            r11[r9] = r12     // Catch:{ Exception -> 0x0129 }
            java.io.RandomAccessFile r11 = r14.statsFile     // Catch:{ Exception -> 0x0129 }
            byte[] r12 = r14.buffer     // Catch:{ Exception -> 0x0129 }
            r11.readFully(r12, r7, r10)     // Catch:{ Exception -> 0x0129 }
            int[][] r11 = r14.sentItems     // Catch:{ Exception -> 0x0129 }
            r11 = r11[r0]     // Catch:{ Exception -> 0x0129 }
            byte[] r12 = r14.buffer     // Catch:{ Exception -> 0x0129 }
            int r12 = r14.bytesToInt(r12)     // Catch:{ Exception -> 0x0129 }
            r11[r9] = r12     // Catch:{ Exception -> 0x0129 }
            java.io.RandomAccessFile r11 = r14.statsFile     // Catch:{ Exception -> 0x0129 }
            byte[] r12 = r14.buffer     // Catch:{ Exception -> 0x0129 }
            r11.readFully(r12, r7, r10)     // Catch:{ Exception -> 0x0129 }
            int[][] r10 = r14.receivedItems     // Catch:{ Exception -> 0x0129 }
            r10 = r10[r0]     // Catch:{ Exception -> 0x0129 }
            byte[] r11 = r14.buffer     // Catch:{ Exception -> 0x0129 }
            int r11 = r14.bytesToInt(r11)     // Catch:{ Exception -> 0x0129 }
            r10[r9] = r11     // Catch:{ Exception -> 0x0129 }
            int r9 = r9 + 1
            goto L_0x0099
        L_0x00eb:
            java.io.RandomAccessFile r9 = r14.statsFile     // Catch:{ Exception -> 0x0129 }
            byte[] r11 = r14.buffer     // Catch:{ Exception -> 0x0129 }
            r9.readFully(r11, r7, r10)     // Catch:{ Exception -> 0x0129 }
            int[] r9 = r14.callsTotalTime     // Catch:{ Exception -> 0x0129 }
            byte[] r10 = r14.buffer     // Catch:{ Exception -> 0x0129 }
            int r10 = r14.bytesToInt(r10)     // Catch:{ Exception -> 0x0129 }
            r9[r0] = r10     // Catch:{ Exception -> 0x0129 }
            java.io.RandomAccessFile r9 = r14.statsFile     // Catch:{ Exception -> 0x0129 }
            byte[] r10 = r14.buffer     // Catch:{ Exception -> 0x0129 }
            r9.readFully(r10, r7, r2)     // Catch:{ Exception -> 0x0129 }
            long[] r9 = r14.resetStatsDate     // Catch:{ Exception -> 0x0129 }
            byte[] r10 = r14.buffer     // Catch:{ Exception -> 0x0129 }
            long r10 = r14.bytesToLong(r10)     // Catch:{ Exception -> 0x0129 }
            r9[r0] = r10     // Catch:{ Exception -> 0x0129 }
            long[] r9 = r14.resetStatsDate     // Catch:{ Exception -> 0x0129 }
            r10 = r9[r0]     // Catch:{ Exception -> 0x0129 }
            int r9 = (r10 > r5 ? 1 : (r10 == r5 ? 0 : -1))
            if (r9 != 0) goto L_0x011e
            long[] r8 = r14.resetStatsDate     // Catch:{ Exception -> 0x0129 }
            long r9 = java.lang.System.currentTimeMillis()     // Catch:{ Exception -> 0x0129 }
            r8[r0] = r9     // Catch:{ Exception -> 0x0129 }
            r8 = 1
        L_0x011e:
            int r0 = r0 + 1
            goto L_0x0096
        L_0x0122:
            if (r8 == 0) goto L_0x0127
            r14.saveStats()     // Catch:{ Exception -> 0x0129 }
        L_0x0127:
            r0 = 0
            goto L_0x012a
        L_0x0129:
            r0 = 1
        L_0x012a:
            if (r0 == 0) goto L_0x0227
            java.lang.String r0 = "stats"
            if (r15 != 0) goto L_0x0137
            android.content.Context r15 = org.telegram.messenger.ApplicationLoader.applicationContext
            android.content.SharedPreferences r15 = r15.getSharedPreferences(r0, r7)
            goto L_0x014c
        L_0x0137:
            android.content.Context r2 = org.telegram.messenger.ApplicationLoader.applicationContext
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            r8.append(r0)
            r8.append(r15)
            java.lang.String r15 = r8.toString()
            android.content.SharedPreferences r15 = r2.getSharedPreferences(r15, r7)
        L_0x014c:
            r0 = 0
            r2 = 0
        L_0x014e:
            if (r0 >= r4) goto L_0x0222
            int[] r8 = r14.callsTotalTime
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            java.lang.String r10 = "callsTotalTime"
            r9.append(r10)
            r9.append(r0)
            java.lang.String r9 = r9.toString()
            int r9 = r15.getInt(r9, r7)
            r8[r0] = r9
            long[] r8 = r14.resetStatsDate
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            java.lang.String r10 = "resetStatsDate"
            r9.append(r10)
            r9.append(r0)
            java.lang.String r9 = r9.toString()
            long r9 = r15.getLong(r9, r5)
            r8[r0] = r9
            r8 = 0
        L_0x0183:
            if (r8 >= r3) goto L_0x020f
            long[][] r9 = r14.sentBytes
            r9 = r9[r0]
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            java.lang.String r11 = "sentBytes"
            r10.append(r11)
            r10.append(r0)
            java.lang.String r11 = "_"
            r10.append(r11)
            r10.append(r8)
            java.lang.String r10 = r10.toString()
            long r12 = r15.getLong(r10, r5)
            r9[r8] = r12
            long[][] r9 = r14.receivedBytes
            r9 = r9[r0]
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            java.lang.String r12 = "receivedBytes"
            r10.append(r12)
            r10.append(r0)
            r10.append(r11)
            r10.append(r8)
            java.lang.String r10 = r10.toString()
            long r12 = r15.getLong(r10, r5)
            r9[r8] = r12
            int[][] r9 = r14.sentItems
            r9 = r9[r0]
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            java.lang.String r12 = "sentItems"
            r10.append(r12)
            r10.append(r0)
            r10.append(r11)
            r10.append(r8)
            java.lang.String r10 = r10.toString()
            int r10 = r15.getInt(r10, r7)
            r9[r8] = r10
            int[][] r9 = r14.receivedItems
            r9 = r9[r0]
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            java.lang.String r12 = "receivedItems"
            r10.append(r12)
            r10.append(r0)
            r10.append(r11)
            r10.append(r8)
            java.lang.String r10 = r10.toString()
            int r10 = r15.getInt(r10, r7)
            r9[r8] = r10
            int r8 = r8 + 1
            goto L_0x0183
        L_0x020f:
            long[] r8 = r14.resetStatsDate
            r9 = r8[r0]
            int r11 = (r9 > r5 ? 1 : (r9 == r5 ? 0 : -1))
            if (r11 != 0) goto L_0x021e
            long r9 = java.lang.System.currentTimeMillis()
            r8[r0] = r9
            r2 = 1
        L_0x021e:
            int r0 = r0 + 1
            goto L_0x014e
        L_0x0222:
            if (r2 == 0) goto L_0x0227
            r14.saveStats()
        L_0x0227:
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
        if (Math.abs(currentTimeMillis - lastStatsSaveTime.get().longValue()) >= 2000) {
            lastStatsSaveTime.set(Long.valueOf(currentTimeMillis));
            statsSaveQueue.postRunnable(this.saveRunnable);
        }
    }
}
