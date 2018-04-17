package org.telegram.messenger;

import java.io.File;
import java.io.RandomAccessFile;
import java.lang.reflect.Array;
import org.telegram.messenger.exoplayer2.trackselection.AdaptiveTrackSelection;

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
    private static final ThreadLocal<Long> lastStatsSaveTime = new C05131();
    private static DispatchQueue statsSaveQueue = new DispatchQueue("statsSaveQueue");
    private byte[] buffer = new byte[8];
    private int[] callsTotalTime;
    private long lastInternalStatsSaveTime;
    private long[][] receivedBytes;
    private int[][] receivedItems;
    private long[] resetStatsDate;
    private Runnable saveRunnable;
    private long[][] sentBytes;
    private int[][] sentItems;
    private RandomAccessFile statsFile;

    /* renamed from: org.telegram.messenger.StatsController$1 */
    static class C05131 extends ThreadLocal<Long> {
        C05131() {
        }

        protected Long initialValue() {
            return Long.valueOf(System.currentTimeMillis() - 1000);
        }
    }

    /* renamed from: org.telegram.messenger.StatsController$2 */
    class C05142 implements Runnable {
        C05142() {
        }

        public void run() {
            long newTime = System.currentTimeMillis();
            if (Math.abs(newTime - StatsController.this.lastInternalStatsSaveTime) >= AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS) {
                StatsController.this.lastInternalStatsSaveTime = newTime;
                try {
                    StatsController.this.statsFile.seek(0);
                    for (int a = 0; a < 3; a++) {
                        for (int b = 0; b < 7; b++) {
                            StatsController.this.statsFile.write(StatsController.this.longToBytes(StatsController.this.sentBytes[a][b]), 0, 8);
                            StatsController.this.statsFile.write(StatsController.this.longToBytes(StatsController.this.receivedBytes[a][b]), 0, 8);
                            StatsController.this.statsFile.write(StatsController.this.intToBytes(StatsController.this.sentItems[a][b]), 0, 4);
                            StatsController.this.statsFile.write(StatsController.this.intToBytes(StatsController.this.receivedItems[a][b]), 0, 4);
                        }
                        StatsController.this.statsFile.write(StatsController.this.intToBytes(StatsController.this.callsTotalTime[a]), 0, 4);
                        StatsController.this.statsFile.write(StatsController.this.longToBytes(StatsController.this.resetStatsDate[a]), 0, 8);
                    }
                    StatsController.this.statsFile.getFD().sync();
                } catch (Exception e) {
                }
            }
        }
    }

    private byte[] intToBytes(int value) {
        this.buffer[0] = (byte) (value >>> 24);
        this.buffer[1] = (byte) (value >>> 16);
        this.buffer[2] = (byte) (value >>> 8);
        this.buffer[3] = (byte) value;
        return this.buffer;
    }

    private int bytesToInt(byte[] bytes) {
        return (((bytes[0] << 24) | ((bytes[1] & 255) << 16)) | ((bytes[2] & 255) << 8)) | (bytes[3] & 255);
    }

    private byte[] longToBytes(long value) {
        this.buffer[0] = (byte) ((int) (value >>> 56));
        this.buffer[1] = (byte) ((int) (value >>> 48));
        this.buffer[2] = (byte) ((int) (value >>> 40));
        this.buffer[3] = (byte) ((int) (value >>> 32));
        this.buffer[4] = (byte) ((int) (value >>> 24));
        this.buffer[5] = (byte) ((int) (value >>> 16));
        this.buffer[6] = (byte) ((int) (value >>> 8));
        this.buffer[7] = (byte) ((int) value);
        return this.buffer;
    }

    private long bytesToLong(byte[] bytes) {
        return ((((((((((long) bytes[0]) & 255) << 56) | ((((long) bytes[1]) & 255) << 48)) | ((((long) bytes[2]) & 255) << 40)) | ((((long) bytes[3]) & 255) << 32)) | ((((long) bytes[4]) & 255) << 24)) | ((((long) bytes[5]) & 255) << 16)) | ((((long) bytes[6]) & 255) << 8)) | (((long) bytes[7]) & 255);
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

    private StatsController(int account) {
        int i = account;
        int i2 = 7;
        int i3 = 3;
        this.sentBytes = (long[][]) Array.newInstance(long.class, new int[]{3, 7});
        this.receivedBytes = (long[][]) Array.newInstance(long.class, new int[]{3, 7});
        this.sentItems = (int[][]) Array.newInstance(int.class, new int[]{3, 7});
        this.receivedItems = (int[][]) Array.newInstance(int.class, new int[]{3, 7});
        this.resetStatsDate = new long[3];
        this.callsTotalTime = new int[3];
        this.saveRunnable = new C05142();
        File filesDir = ApplicationLoader.getFilesDirFixed();
        if (i != 0) {
            File filesDirFixed = ApplicationLoader.getFilesDirFixed();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("account");
            stringBuilder.append(i);
            stringBuilder.append("/");
            filesDir = new File(filesDirFixed, stringBuilder.toString());
            filesDir.mkdirs();
        }
        boolean needConvert = true;
        try {
            r1.statsFile = new RandomAccessFile(new File(filesDir, "stats2.dat"), "rw");
            if (r1.statsFile.length() > 0) {
                boolean save = false;
                int a = 0;
                while (a < 3) {
                    int b = 0;
                    for (i2 = 
/*
Method generation error in method: org.telegram.messenger.StatsController.<init>(int):void, dex: classes.dex
jadx.core.utils.exceptions.CodegenException: Error generate insn: PHI: (r4_2 'i2' int) = (r4_1 'i2' int), (r4_24 'i2' int) binds: {(r4_1 'i2' int)=B:7:0x009d, (r4_24 'i2' int)=B:16:0x012a} in method: org.telegram.messenger.StatsController.<init>(int):void, dex: classes.dex
	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:226)
	at jadx.core.codegen.RegionGen.makeLoop(RegionGen.java:184)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:61)
	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:87)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:53)
	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:93)
	at jadx.core.codegen.RegionGen.makeLoop(RegionGen.java:219)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:61)
	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:87)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:53)
	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:93)
	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:118)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:57)
	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:87)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:53)
	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:93)
	at jadx.core.codegen.RegionGen.makeTryCatch(RegionGen.java:279)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:63)
	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:87)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:53)
	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:187)
	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:320)
	at jadx.core.codegen.ClassGen.addMethods(ClassGen.java:257)
	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:220)
	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:110)
	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:75)
	at jadx.core.codegen.CodeGen.visit(CodeGen.java:12)
	at jadx.core.ProcessClass.process(ProcessClass.java:40)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
Caused by: jadx.core.utils.exceptions.CodegenException: PHI can be used only in fallback mode
	at jadx.core.codegen.InsnGen.fallbackOnlyInsn(InsnGen.java:537)
	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:509)
	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:220)
	... 30 more

*/

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
                        if (dataType == 1) {
                            return (((this.sentBytes[networkType][6] - this.sentBytes[networkType][5]) - this.sentBytes[networkType][3]) - this.sentBytes[networkType][2]) - this.sentBytes[networkType][4];
                        }
                        return this.sentBytes[networkType][dataType];
                    }

                    public long getReceivedBytesCount(int networkType, int dataType) {
                        if (dataType == 1) {
                            return (((this.receivedBytes[networkType][6] - this.receivedBytes[networkType][5]) - this.receivedBytes[networkType][3]) - this.receivedBytes[networkType][2]) - this.receivedBytes[networkType][4];
                        }
                        return this.receivedBytes[networkType][dataType];
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
                        if (Math.abs(newTime - ((Long) lastStatsSaveTime.get()).longValue()) >= AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS) {
                            lastStatsSaveTime.set(Long.valueOf(newTime));
                            statsSaveQueue.postRunnable(this.saveRunnable);
                        }
                    }
                }
