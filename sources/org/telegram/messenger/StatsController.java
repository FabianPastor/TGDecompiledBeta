package org.telegram.messenger;

import java.io.RandomAccessFile;
import java.lang.reflect.Array;

public class StatsController {
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
        /* Access modifiers changed, original: protected */
        public Long initialValue() {
            return Long.valueOf(System.currentTimeMillis() - 1000);
        }
    };
    private static DispatchQueue statsSaveQueue = new DispatchQueue("statsSaveQueue");
    private byte[] buffer = new byte[8];
    private int[] callsTotalTime = new int[3];
    private long lastInternalStatsSaveTime;
    private long[][] receivedBytes = ((long[][]) Array.newInstance(long.class, new int[]{3, 7}));
    private int[][] receivedItems = ((int[][]) Array.newInstance(int.class, new int[]{3, 7}));
    private long[] resetStatsDate = new long[3];
    private Runnable saveRunnable = new Runnable() {
        public void run() {
            long currentTimeMillis = System.currentTimeMillis();
            if (Math.abs(currentTimeMillis - StatsController.this.lastInternalStatsSaveTime) >= 2000) {
                StatsController.this.lastInternalStatsSaveTime = currentTimeMillis;
                try {
                    StatsController.this.statsFile.seek(0);
                    for (int i = 0; i < 3; i++) {
                        for (int i2 = 0; i2 < 7; i2++) {
                            StatsController.this.statsFile.write(StatsController.this.longToBytes(StatsController.this.sentBytes[i][i2]), 0, 8);
                            StatsController.this.statsFile.write(StatsController.this.longToBytes(StatsController.this.receivedBytes[i][i2]), 0, 8);
                            StatsController.this.statsFile.write(StatsController.this.intToBytes(StatsController.this.sentItems[i][i2]), 0, 4);
                            StatsController.this.statsFile.write(StatsController.this.intToBytes(StatsController.this.receivedItems[i][i2]), 0, 4);
                        }
                        StatsController.this.statsFile.write(StatsController.this.intToBytes(StatsController.this.callsTotalTime[i]), 0, 4);
                        StatsController.this.statsFile.write(StatsController.this.longToBytes(StatsController.this.resetStatsDate[i]), 0, 8);
                    }
                    StatsController.this.statsFile.getFD().sync();
                } catch (Exception unused) {
                }
            }
        }
    };
    private long[][] sentBytes = ((long[][]) Array.newInstance(long.class, new int[]{3, 7}));
    private int[][] sentItems = ((int[][]) Array.newInstance(int.class, new int[]{3, 7}));
    private RandomAccessFile statsFile;

    private byte[] intToBytes(int i) {
        byte[] bArr = this.buffer;
        bArr[0] = (byte) (i >>> 24);
        bArr[1] = (byte) (i >>> 16);
        bArr[2] = (byte) (i >>> 8);
        bArr[3] = (byte) i;
        return bArr;
    }

    private int bytesToInt(byte[] bArr) {
        return (bArr[3] & 255) | (((bArr[0] << 24) | ((bArr[1] & 255) << 16)) | ((bArr[2] & 255) << 8));
    }

    private byte[] longToBytes(long j) {
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
        return ((((((((((long) bArr[0]) & 255) << 56) | ((((long) bArr[1]) & 255) << 48)) | ((((long) bArr[2]) & 255) << 40)) | ((((long) bArr[3]) & 255) << 32)) | ((((long) bArr[4]) & 255) << 24)) | ((((long) bArr[5]) & 255) << 16)) | ((((long) bArr[6]) & 255) << 8)) | (255 & ((long) bArr[7]));
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

    /* JADX WARNING: Removed duplicated region for block: B:47:? A:{SYNTHETIC, RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:23:0x0130  */
    private StatsController(int r15) {
        /*
        r14 = this;
        r14.<init>();
        r0 = 8;
        r1 = new byte[r0];
        r14.buffer = r1;
        r1 = 7;
        r2 = 3;
        r3 = new int[]{r2, r1};
        r4 = long.class;
        r3 = java.lang.reflect.Array.newInstance(r4, r3);
        r3 = (long[][]) r3;
        r14.sentBytes = r3;
        r3 = new int[]{r2, r1};
        r4 = long.class;
        r3 = java.lang.reflect.Array.newInstance(r4, r3);
        r3 = (long[][]) r3;
        r14.receivedBytes = r3;
        r3 = new int[]{r2, r1};
        r4 = int.class;
        r3 = java.lang.reflect.Array.newInstance(r4, r3);
        r3 = (int[][]) r3;
        r14.sentItems = r3;
        r3 = new int[]{r2, r1};
        r4 = int.class;
        r3 = java.lang.reflect.Array.newInstance(r4, r3);
        r3 = (int[][]) r3;
        r14.receivedItems = r3;
        r3 = new long[r2];
        r14.resetStatsDate = r3;
        r3 = new int[r2];
        r14.callsTotalTime = r3;
        r3 = new org.telegram.messenger.StatsController$2;
        r3.<init>();
        r14.saveRunnable = r3;
        r3 = org.telegram.messenger.ApplicationLoader.getFilesDirFixed();
        if (r15 == 0) goto L_0x007a;
    L_0x0058:
        r3 = new java.io.File;
        r4 = org.telegram.messenger.ApplicationLoader.getFilesDirFixed();
        r5 = new java.lang.StringBuilder;
        r5.<init>();
        r6 = "account";
        r5.append(r6);
        r5.append(r15);
        r6 = "/";
        r5.append(r6);
        r5 = r5.toString();
        r3.<init>(r4, r5);
        r3.mkdirs();
    L_0x007a:
        r4 = 0;
        r6 = 1;
        r7 = 0;
        r8 = new java.io.RandomAccessFile;	 Catch:{ Exception -> 0x012d }
        r9 = new java.io.File;	 Catch:{ Exception -> 0x012d }
        r10 = "stats2.dat";
        r9.<init>(r3, r10);	 Catch:{ Exception -> 0x012d }
        r3 = "rw";
        r8.<init>(r9, r3);	 Catch:{ Exception -> 0x012d }
        r14.statsFile = r8;	 Catch:{ Exception -> 0x012d }
        r3 = r14.statsFile;	 Catch:{ Exception -> 0x012d }
        r8 = r3.length();	 Catch:{ Exception -> 0x012d }
        r3 = (r8 > r4 ? 1 : (r8 == r4 ? 0 : -1));
        if (r3 <= 0) goto L_0x012d;
    L_0x0098:
        r3 = 0;
        r8 = 0;
    L_0x009a:
        if (r3 >= r2) goto L_0x0126;
    L_0x009c:
        r9 = 0;
    L_0x009d:
        r10 = 4;
        if (r9 >= r1) goto L_0x00ef;
    L_0x00a0:
        r11 = r14.statsFile;	 Catch:{ Exception -> 0x012d }
        r12 = r14.buffer;	 Catch:{ Exception -> 0x012d }
        r11.readFully(r12, r7, r0);	 Catch:{ Exception -> 0x012d }
        r11 = r14.sentBytes;	 Catch:{ Exception -> 0x012d }
        r11 = r11[r3];	 Catch:{ Exception -> 0x012d }
        r12 = r14.buffer;	 Catch:{ Exception -> 0x012d }
        r12 = r14.bytesToLong(r12);	 Catch:{ Exception -> 0x012d }
        r11[r9] = r12;	 Catch:{ Exception -> 0x012d }
        r11 = r14.statsFile;	 Catch:{ Exception -> 0x012d }
        r12 = r14.buffer;	 Catch:{ Exception -> 0x012d }
        r11.readFully(r12, r7, r0);	 Catch:{ Exception -> 0x012d }
        r11 = r14.receivedBytes;	 Catch:{ Exception -> 0x012d }
        r11 = r11[r3];	 Catch:{ Exception -> 0x012d }
        r12 = r14.buffer;	 Catch:{ Exception -> 0x012d }
        r12 = r14.bytesToLong(r12);	 Catch:{ Exception -> 0x012d }
        r11[r9] = r12;	 Catch:{ Exception -> 0x012d }
        r11 = r14.statsFile;	 Catch:{ Exception -> 0x012d }
        r12 = r14.buffer;	 Catch:{ Exception -> 0x012d }
        r11.readFully(r12, r7, r10);	 Catch:{ Exception -> 0x012d }
        r11 = r14.sentItems;	 Catch:{ Exception -> 0x012d }
        r11 = r11[r3];	 Catch:{ Exception -> 0x012d }
        r12 = r14.buffer;	 Catch:{ Exception -> 0x012d }
        r12 = r14.bytesToInt(r12);	 Catch:{ Exception -> 0x012d }
        r11[r9] = r12;	 Catch:{ Exception -> 0x012d }
        r11 = r14.statsFile;	 Catch:{ Exception -> 0x012d }
        r12 = r14.buffer;	 Catch:{ Exception -> 0x012d }
        r11.readFully(r12, r7, r10);	 Catch:{ Exception -> 0x012d }
        r10 = r14.receivedItems;	 Catch:{ Exception -> 0x012d }
        r10 = r10[r3];	 Catch:{ Exception -> 0x012d }
        r11 = r14.buffer;	 Catch:{ Exception -> 0x012d }
        r11 = r14.bytesToInt(r11);	 Catch:{ Exception -> 0x012d }
        r10[r9] = r11;	 Catch:{ Exception -> 0x012d }
        r9 = r9 + 1;
        goto L_0x009d;
    L_0x00ef:
        r9 = r14.statsFile;	 Catch:{ Exception -> 0x012d }
        r11 = r14.buffer;	 Catch:{ Exception -> 0x012d }
        r9.readFully(r11, r7, r10);	 Catch:{ Exception -> 0x012d }
        r9 = r14.callsTotalTime;	 Catch:{ Exception -> 0x012d }
        r10 = r14.buffer;	 Catch:{ Exception -> 0x012d }
        r10 = r14.bytesToInt(r10);	 Catch:{ Exception -> 0x012d }
        r9[r3] = r10;	 Catch:{ Exception -> 0x012d }
        r9 = r14.statsFile;	 Catch:{ Exception -> 0x012d }
        r10 = r14.buffer;	 Catch:{ Exception -> 0x012d }
        r9.readFully(r10, r7, r0);	 Catch:{ Exception -> 0x012d }
        r9 = r14.resetStatsDate;	 Catch:{ Exception -> 0x012d }
        r10 = r14.buffer;	 Catch:{ Exception -> 0x012d }
        r10 = r14.bytesToLong(r10);	 Catch:{ Exception -> 0x012d }
        r9[r3] = r10;	 Catch:{ Exception -> 0x012d }
        r9 = r14.resetStatsDate;	 Catch:{ Exception -> 0x012d }
        r10 = r9[r3];	 Catch:{ Exception -> 0x012d }
        r9 = (r10 > r4 ? 1 : (r10 == r4 ? 0 : -1));
        if (r9 != 0) goto L_0x0122;
    L_0x0119:
        r8 = r14.resetStatsDate;	 Catch:{ Exception -> 0x012d }
        r9 = java.lang.System.currentTimeMillis();	 Catch:{ Exception -> 0x012d }
        r8[r3] = r9;	 Catch:{ Exception -> 0x012d }
        r8 = 1;
    L_0x0122:
        r3 = r3 + 1;
        goto L_0x009a;
    L_0x0126:
        if (r8 == 0) goto L_0x012b;
    L_0x0128:
        r14.saveStats();	 Catch:{ Exception -> 0x012d }
    L_0x012b:
        r0 = 0;
        goto L_0x012e;
    L_0x012d:
        r0 = 1;
    L_0x012e:
        if (r0 == 0) goto L_0x022b;
    L_0x0130:
        r0 = "stats";
        if (r15 != 0) goto L_0x013b;
    L_0x0134:
        r15 = org.telegram.messenger.ApplicationLoader.applicationContext;
        r15 = r15.getSharedPreferences(r0, r7);
        goto L_0x0150;
    L_0x013b:
        r3 = org.telegram.messenger.ApplicationLoader.applicationContext;
        r8 = new java.lang.StringBuilder;
        r8.<init>();
        r8.append(r0);
        r8.append(r15);
        r15 = r8.toString();
        r15 = r3.getSharedPreferences(r15, r7);
    L_0x0150:
        r0 = 0;
        r3 = 0;
    L_0x0152:
        if (r0 >= r2) goto L_0x0226;
    L_0x0154:
        r8 = r14.callsTotalTime;
        r9 = new java.lang.StringBuilder;
        r9.<init>();
        r10 = "callsTotalTime";
        r9.append(r10);
        r9.append(r0);
        r9 = r9.toString();
        r9 = r15.getInt(r9, r7);
        r8[r0] = r9;
        r8 = r14.resetStatsDate;
        r9 = new java.lang.StringBuilder;
        r9.<init>();
        r10 = "resetStatsDate";
        r9.append(r10);
        r9.append(r0);
        r9 = r9.toString();
        r9 = r15.getLong(r9, r4);
        r8[r0] = r9;
        r8 = 0;
    L_0x0187:
        if (r8 >= r1) goto L_0x0213;
    L_0x0189:
        r9 = r14.sentBytes;
        r9 = r9[r0];
        r10 = new java.lang.StringBuilder;
        r10.<init>();
        r11 = "sentBytes";
        r10.append(r11);
        r10.append(r0);
        r11 = "_";
        r10.append(r11);
        r10.append(r8);
        r10 = r10.toString();
        r12 = r15.getLong(r10, r4);
        r9[r8] = r12;
        r9 = r14.receivedBytes;
        r9 = r9[r0];
        r10 = new java.lang.StringBuilder;
        r10.<init>();
        r12 = "receivedBytes";
        r10.append(r12);
        r10.append(r0);
        r10.append(r11);
        r10.append(r8);
        r10 = r10.toString();
        r12 = r15.getLong(r10, r4);
        r9[r8] = r12;
        r9 = r14.sentItems;
        r9 = r9[r0];
        r10 = new java.lang.StringBuilder;
        r10.<init>();
        r12 = "sentItems";
        r10.append(r12);
        r10.append(r0);
        r10.append(r11);
        r10.append(r8);
        r10 = r10.toString();
        r10 = r15.getInt(r10, r7);
        r9[r8] = r10;
        r9 = r14.receivedItems;
        r9 = r9[r0];
        r10 = new java.lang.StringBuilder;
        r10.<init>();
        r12 = "receivedItems";
        r10.append(r12);
        r10.append(r0);
        r10.append(r11);
        r10.append(r8);
        r10 = r10.toString();
        r10 = r15.getInt(r10, r7);
        r9[r8] = r10;
        r8 = r8 + 1;
        goto L_0x0187;
    L_0x0213:
        r8 = r14.resetStatsDate;
        r9 = r8[r0];
        r11 = (r9 > r4 ? 1 : (r9 == r4 ? 0 : -1));
        if (r11 != 0) goto L_0x0222;
    L_0x021b:
        r9 = java.lang.System.currentTimeMillis();
        r8[r0] = r9;
        r3 = 1;
    L_0x0222:
        r0 = r0 + 1;
        goto L_0x0152;
    L_0x0226:
        if (r3 == 0) goto L_0x022b;
    L_0x0228:
        r14.saveStats();
    L_0x022b:
        return;
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
        if (Math.abs(currentTimeMillis - ((Long) lastStatsSaveTime.get()).longValue()) >= 2000) {
            lastStatsSaveTime.set(Long.valueOf(currentTimeMillis));
            statsSaveQueue.postRunnable(this.saveRunnable);
        }
    }
}
