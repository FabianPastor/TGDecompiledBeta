package org.telegram.messenger;

import android.content.Context;
import android.content.SharedPreferences;
import java.io.File;
import java.io.RandomAccessFile;
import java.lang.reflect.Array;

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
    public byte[] intToBytes(int value) {
        byte[] bArr = this.buffer;
        bArr[0] = (byte) (value >>> 24);
        bArr[1] = (byte) (value >>> 16);
        bArr[2] = (byte) (value >>> 8);
        bArr[3] = (byte) value;
        return bArr;
    }

    private int bytesToInt(byte[] bytes) {
        return (bytes[0] << 24) | ((bytes[1] & 255) << 16) | ((bytes[2] & 255) << 8) | (bytes[3] & 255);
    }

    /* access modifiers changed from: private */
    public byte[] longToBytes(long value) {
        byte[] bArr = this.buffer;
        bArr[0] = (byte) ((int) (value >>> 56));
        bArr[1] = (byte) ((int) (value >>> 48));
        bArr[2] = (byte) ((int) (value >>> 40));
        bArr[3] = (byte) ((int) (value >>> 32));
        bArr[4] = (byte) ((int) (value >>> 24));
        bArr[5] = (byte) ((int) (value >>> 16));
        bArr[6] = (byte) ((int) (value >>> 8));
        bArr[7] = (byte) ((int) value);
        return bArr;
    }

    private long bytesToLong(byte[] bytes) {
        return ((((long) bytes[0]) & 255) << 56) | ((((long) bytes[1]) & 255) << 48) | ((((long) bytes[2]) & 255) << 40) | ((((long) bytes[3]) & 255) << 32) | ((((long) bytes[4]) & 255) << 24) | ((((long) bytes[5]) & 255) << 16) | ((((long) bytes[6]) & 255) << 8) | (255 & ((long) bytes[7]));
    }

    public static StatsController getInstance(int num) {
        StatsController localInstance = Instance[num];
        if (localInstance == null) {
            synchronized (StatsController.class) {
                localInstance = Instance[num];
                if (localInstance == null) {
                    StatsController[] statsControllerArr = Instance;
                    StatsController statsController = new StatsController(num);
                    localInstance = statsController;
                    statsControllerArr[num] = statsController;
                }
            }
        }
        return localInstance;
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    private StatsController(int account) {
        super(account);
        File filesDir;
        SharedPreferences sharedPreferences;
        int i = account;
        Class<int> cls = int.class;
        Class<long> cls2 = long.class;
        this.sentBytes = (long[][]) Array.newInstance(cls2, new int[]{3, 7});
        this.receivedBytes = (long[][]) Array.newInstance(cls2, new int[]{3, 7});
        this.sentItems = (int[][]) Array.newInstance(cls, new int[]{3, 7});
        this.receivedItems = (int[][]) Array.newInstance(cls, new int[]{3, 7});
        this.resetStatsDate = new long[3];
        this.callsTotalTime = new int[3];
        this.saveRunnable = new Runnable() {
            public void run() {
                long newTime = System.currentTimeMillis();
                if (Math.abs(newTime - StatsController.this.lastInternalStatsSaveTime) >= 2000) {
                    long unused = StatsController.this.lastInternalStatsSaveTime = newTime;
                    try {
                        StatsController.this.statsFile.seek(0);
                        for (int a = 0; a < 3; a++) {
                            for (int b = 0; b < 7; b++) {
                                RandomAccessFile access$100 = StatsController.this.statsFile;
                                StatsController statsController = StatsController.this;
                                access$100.write(statsController.longToBytes(statsController.sentBytes[a][b]), 0, 8);
                                RandomAccessFile access$1002 = StatsController.this.statsFile;
                                StatsController statsController2 = StatsController.this;
                                access$1002.write(statsController2.longToBytes(statsController2.receivedBytes[a][b]), 0, 8);
                                RandomAccessFile access$1003 = StatsController.this.statsFile;
                                StatsController statsController3 = StatsController.this;
                                access$1003.write(statsController3.intToBytes(statsController3.sentItems[a][b]), 0, 4);
                                RandomAccessFile access$1004 = StatsController.this.statsFile;
                                StatsController statsController4 = StatsController.this;
                                access$1004.write(statsController4.intToBytes(statsController4.receivedItems[a][b]), 0, 4);
                            }
                            RandomAccessFile access$1005 = StatsController.this.statsFile;
                            StatsController statsController5 = StatsController.this;
                            access$1005.write(statsController5.intToBytes(statsController5.callsTotalTime[a]), 0, 4);
                            RandomAccessFile access$1006 = StatsController.this.statsFile;
                            StatsController statsController6 = StatsController.this;
                            access$1006.write(statsController6.longToBytes(statsController6.resetStatsDate[a]), 0, 8);
                        }
                        StatsController.this.statsFile.getFD().sync();
                    } catch (Exception e) {
                    }
                }
            }
        };
        File filesDir2 = ApplicationLoader.getFilesDirFixed();
        if (i != 0) {
            File filesDirFixed = ApplicationLoader.getFilesDirFixed();
            filesDir = new File(filesDirFixed, "account" + i + "/");
            filesDir.mkdirs();
        } else {
            filesDir = filesDir2;
        }
        boolean needConvert = true;
        try {
            RandomAccessFile randomAccessFile = new RandomAccessFile(new File(filesDir, "stats2.dat"), "rw");
            this.statsFile = randomAccessFile;
            if (randomAccessFile.length() > 0) {
                boolean save = false;
                for (int a = 0; a < 3; a++) {
                    for (int b = 0; b < 7; b++) {
                        this.statsFile.readFully(this.buffer, 0, 8);
                        this.sentBytes[a][b] = bytesToLong(this.buffer);
                        this.statsFile.readFully(this.buffer, 0, 8);
                        this.receivedBytes[a][b] = bytesToLong(this.buffer);
                        this.statsFile.readFully(this.buffer, 0, 4);
                        this.sentItems[a][b] = bytesToInt(this.buffer);
                        this.statsFile.readFully(this.buffer, 0, 4);
                        this.receivedItems[a][b] = bytesToInt(this.buffer);
                    }
                    this.statsFile.readFully(this.buffer, 0, 4);
                    this.callsTotalTime[a] = bytesToInt(this.buffer);
                    this.statsFile.readFully(this.buffer, 0, 8);
                    this.resetStatsDate[a] = bytesToLong(this.buffer);
                    long[] jArr = this.resetStatsDate;
                    if (jArr[a] == 0) {
                        save = true;
                        jArr[a] = System.currentTimeMillis();
                    }
                }
                if (save) {
                    saveStats();
                }
                needConvert = false;
            }
        } catch (Exception e) {
        }
        if (needConvert) {
            if (i == 0) {
                sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("stats", 0);
            } else {
                Context context = ApplicationLoader.applicationContext;
                sharedPreferences = context.getSharedPreferences("stats" + i, 0);
            }
            boolean save2 = false;
            int a2 = 0;
            for (int i2 = 3; a2 < i2; i2 = 3) {
                int[] iArr = this.callsTotalTime;
                iArr[a2] = sharedPreferences.getInt("callsTotalTime" + a2, 0);
                long[] jArr2 = this.resetStatsDate;
                jArr2[a2] = sharedPreferences.getLong("resetStatsDate" + a2, 0);
                for (int b2 = 0; b2 < 7; b2++) {
                    long[] jArr3 = this.sentBytes[a2];
                    jArr3[b2] = sharedPreferences.getLong("sentBytes" + a2 + "_" + b2, 0);
                    long[] jArr4 = this.receivedBytes[a2];
                    jArr4[b2] = sharedPreferences.getLong("receivedBytes" + a2 + "_" + b2, 0);
                    int[] iArr2 = this.sentItems[a2];
                    iArr2[b2] = sharedPreferences.getInt("sentItems" + a2 + "_" + b2, 0);
                    int[] iArr3 = this.receivedItems[a2];
                    iArr3[b2] = sharedPreferences.getInt("receivedItems" + a2 + "_" + b2, 0);
                }
                long[] jArr5 = this.resetStatsDate;
                if (jArr5[a2] == 0) {
                    save2 = true;
                    jArr5[a2] = System.currentTimeMillis();
                }
                a2++;
            }
            if (save2) {
                saveStats();
            }
        }
    }

    public void incrementReceivedItemsCount(int networkType, int dataType, int value) {
        int[] iArr = this.receivedItems[networkType];
        iArr[dataType] = iArr[dataType] + value;
        saveStats();
    }

    public void incrementSentItemsCount(int networkType, int dataType, int value) {
        int[] iArr = this.sentItems[networkType];
        iArr[dataType] = iArr[dataType] + value;
        saveStats();
    }

    public void incrementReceivedBytesCount(int networkType, int dataType, long value) {
        long[] jArr = this.receivedBytes[networkType];
        jArr[dataType] = jArr[dataType] + value;
        saveStats();
    }

    public void incrementSentBytesCount(int networkType, int dataType, long value) {
        long[] jArr = this.sentBytes[networkType];
        jArr[dataType] = jArr[dataType] + value;
        saveStats();
    }

    public void incrementTotalCallsTime(int networkType, int value) {
        int[] iArr = this.callsTotalTime;
        iArr[networkType] = iArr[networkType] + value;
        saveStats();
    }

    public int getRecivedItemsCount(int networkType, int dataType) {
        return this.receivedItems[networkType][dataType];
    }

    public int getSentItemsCount(int networkType, int dataType) {
        return this.sentItems[networkType][dataType];
    }

    public long getSentBytesCount(int networkType, int dataType) {
        if (dataType != 1) {
            return this.sentBytes[networkType][dataType];
        }
        long[][] jArr = this.sentBytes;
        return (((jArr[networkType][6] - jArr[networkType][5]) - jArr[networkType][3]) - jArr[networkType][2]) - jArr[networkType][4];
    }

    public long getReceivedBytesCount(int networkType, int dataType) {
        if (dataType != 1) {
            return this.receivedBytes[networkType][dataType];
        }
        long[][] jArr = this.receivedBytes;
        return (((jArr[networkType][6] - jArr[networkType][5]) - jArr[networkType][3]) - jArr[networkType][2]) - jArr[networkType][4];
    }

    public int getCallsTotalTime(int networkType) {
        return this.callsTotalTime[networkType];
    }

    public long getResetStatsDate(int networkType) {
        return this.resetStatsDate[networkType];
    }

    public void resetStats(int networkType) {
        this.resetStatsDate[networkType] = System.currentTimeMillis();
        for (int a = 0; a < 7; a++) {
            this.sentBytes[networkType][a] = 0;
            this.receivedBytes[networkType][a] = 0;
            this.sentItems[networkType][a] = 0;
            this.receivedItems[networkType][a] = 0;
        }
        this.callsTotalTime[networkType] = 0;
        saveStats();
    }

    private void saveStats() {
        long newTime = System.currentTimeMillis();
        ThreadLocal<Long> threadLocal = lastStatsSaveTime;
        if (Math.abs(newTime - threadLocal.get().longValue()) >= 2000) {
            threadLocal.set(Long.valueOf(newTime));
            statsSaveQueue.postRunnable(this.saveRunnable);
        }
    }
}
