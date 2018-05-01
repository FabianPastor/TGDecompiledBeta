package org.telegram.messenger;

import java.io.RandomAccessFile;
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
            /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:60)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
            /*
            r10 = this;
            r0 = java.lang.System.currentTimeMillis();
            r2 = org.telegram.messenger.StatsController.this;
            r2 = r2.lastInternalStatsSaveTime;
            r4 = r0 - r2;
            r2 = java.lang.Math.abs(r4);
            r4 = 2000; // 0x7d0 float:2.803E-42 double:9.88E-321;
            r6 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1));
            if (r6 >= 0) goto L_0x0017;
        L_0x0016:
            return;
        L_0x0017:
            r2 = org.telegram.messenger.StatsController.this;
            r2.lastInternalStatsSaveTime = r0;
            r0 = org.telegram.messenger.StatsController.this;	 Catch:{ Exception -> 0x00d9 }
            r0 = r0.statsFile;	 Catch:{ Exception -> 0x00d9 }
            r1 = 0;	 Catch:{ Exception -> 0x00d9 }
            r0.seek(r1);	 Catch:{ Exception -> 0x00d9 }
            r0 = 0;	 Catch:{ Exception -> 0x00d9 }
            r1 = r0;	 Catch:{ Exception -> 0x00d9 }
        L_0x0029:
            r2 = 3;	 Catch:{ Exception -> 0x00d9 }
            if (r1 >= r2) goto L_0x00cc;	 Catch:{ Exception -> 0x00d9 }
        L_0x002c:
            r2 = r0;	 Catch:{ Exception -> 0x00d9 }
        L_0x002d:
            r3 = 7;	 Catch:{ Exception -> 0x00d9 }
            r4 = 4;	 Catch:{ Exception -> 0x00d9 }
            r5 = 8;	 Catch:{ Exception -> 0x00d9 }
            if (r2 >= r3) goto L_0x009a;	 Catch:{ Exception -> 0x00d9 }
        L_0x0033:
            r3 = org.telegram.messenger.StatsController.this;	 Catch:{ Exception -> 0x00d9 }
            r3 = r3.statsFile;	 Catch:{ Exception -> 0x00d9 }
            r6 = org.telegram.messenger.StatsController.this;	 Catch:{ Exception -> 0x00d9 }
            r7 = org.telegram.messenger.StatsController.this;	 Catch:{ Exception -> 0x00d9 }
            r7 = r7.sentBytes;	 Catch:{ Exception -> 0x00d9 }
            r7 = r7[r1];	 Catch:{ Exception -> 0x00d9 }
            r8 = r7[r2];	 Catch:{ Exception -> 0x00d9 }
            r6 = r6.longToBytes(r8);	 Catch:{ Exception -> 0x00d9 }
            r3.write(r6, r0, r5);	 Catch:{ Exception -> 0x00d9 }
            r3 = org.telegram.messenger.StatsController.this;	 Catch:{ Exception -> 0x00d9 }
            r3 = r3.statsFile;	 Catch:{ Exception -> 0x00d9 }
            r6 = org.telegram.messenger.StatsController.this;	 Catch:{ Exception -> 0x00d9 }
            r7 = org.telegram.messenger.StatsController.this;	 Catch:{ Exception -> 0x00d9 }
            r7 = r7.receivedBytes;	 Catch:{ Exception -> 0x00d9 }
            r7 = r7[r1];	 Catch:{ Exception -> 0x00d9 }
            r8 = r7[r2];	 Catch:{ Exception -> 0x00d9 }
            r6 = r6.longToBytes(r8);	 Catch:{ Exception -> 0x00d9 }
            r3.write(r6, r0, r5);	 Catch:{ Exception -> 0x00d9 }
            r3 = org.telegram.messenger.StatsController.this;	 Catch:{ Exception -> 0x00d9 }
            r3 = r3.statsFile;	 Catch:{ Exception -> 0x00d9 }
            r5 = org.telegram.messenger.StatsController.this;	 Catch:{ Exception -> 0x00d9 }
            r6 = org.telegram.messenger.StatsController.this;	 Catch:{ Exception -> 0x00d9 }
            r6 = r6.sentItems;	 Catch:{ Exception -> 0x00d9 }
            r6 = r6[r1];	 Catch:{ Exception -> 0x00d9 }
            r6 = r6[r2];	 Catch:{ Exception -> 0x00d9 }
            r5 = r5.intToBytes(r6);	 Catch:{ Exception -> 0x00d9 }
            r3.write(r5, r0, r4);	 Catch:{ Exception -> 0x00d9 }
            r3 = org.telegram.messenger.StatsController.this;	 Catch:{ Exception -> 0x00d9 }
            r3 = r3.statsFile;	 Catch:{ Exception -> 0x00d9 }
            r5 = org.telegram.messenger.StatsController.this;	 Catch:{ Exception -> 0x00d9 }
            r6 = org.telegram.messenger.StatsController.this;	 Catch:{ Exception -> 0x00d9 }
            r6 = r6.receivedItems;	 Catch:{ Exception -> 0x00d9 }
            r6 = r6[r1];	 Catch:{ Exception -> 0x00d9 }
            r6 = r6[r2];	 Catch:{ Exception -> 0x00d9 }
            r5 = r5.intToBytes(r6);	 Catch:{ Exception -> 0x00d9 }
            r3.write(r5, r0, r4);	 Catch:{ Exception -> 0x00d9 }
            r2 = r2 + 1;	 Catch:{ Exception -> 0x00d9 }
            goto L_0x002d;	 Catch:{ Exception -> 0x00d9 }
        L_0x009a:
            r2 = org.telegram.messenger.StatsController.this;	 Catch:{ Exception -> 0x00d9 }
            r2 = r2.statsFile;	 Catch:{ Exception -> 0x00d9 }
            r3 = org.telegram.messenger.StatsController.this;	 Catch:{ Exception -> 0x00d9 }
            r6 = org.telegram.messenger.StatsController.this;	 Catch:{ Exception -> 0x00d9 }
            r6 = r6.callsTotalTime;	 Catch:{ Exception -> 0x00d9 }
            r6 = r6[r1];	 Catch:{ Exception -> 0x00d9 }
            r3 = r3.intToBytes(r6);	 Catch:{ Exception -> 0x00d9 }
            r2.write(r3, r0, r4);	 Catch:{ Exception -> 0x00d9 }
            r2 = org.telegram.messenger.StatsController.this;	 Catch:{ Exception -> 0x00d9 }
            r2 = r2.statsFile;	 Catch:{ Exception -> 0x00d9 }
            r3 = org.telegram.messenger.StatsController.this;	 Catch:{ Exception -> 0x00d9 }
            r4 = org.telegram.messenger.StatsController.this;	 Catch:{ Exception -> 0x00d9 }
            r4 = r4.resetStatsDate;	 Catch:{ Exception -> 0x00d9 }
            r6 = r4[r1];	 Catch:{ Exception -> 0x00d9 }
            r3 = r3.longToBytes(r6);	 Catch:{ Exception -> 0x00d9 }
            r2.write(r3, r0, r5);	 Catch:{ Exception -> 0x00d9 }
            r1 = r1 + 1;	 Catch:{ Exception -> 0x00d9 }
            goto L_0x0029;	 Catch:{ Exception -> 0x00d9 }
        L_0x00cc:
            r0 = org.telegram.messenger.StatsController.this;	 Catch:{ Exception -> 0x00d9 }
            r0 = r0.statsFile;	 Catch:{ Exception -> 0x00d9 }
            r0 = r0.getFD();	 Catch:{ Exception -> 0x00d9 }
            r0.sync();	 Catch:{ Exception -> 0x00d9 }
        L_0x00d9:
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.StatsController.2.run():void");
        }
    }

    private byte[] intToBytes(int i) {
        this.buffer[0] = (byte) (i >>> 24);
        this.buffer[1] = (byte) (i >>> 16);
        this.buffer[2] = (byte) (i >>> 8);
        this.buffer[3] = (byte) i;
        return this.buffer;
    }

    private int bytesToInt(byte[] bArr) {
        return (bArr[3] & 255) | (((bArr[0] << 24) | ((bArr[1] & 255) << 16)) | ((bArr[2] & 255) << 8));
    }

    private byte[] longToBytes(long j) {
        this.buffer[0] = (byte) ((int) (j >>> 56));
        this.buffer[1] = (byte) ((int) (j >>> 48));
        this.buffer[2] = (byte) ((int) (j >>> 40));
        this.buffer[3] = (byte) ((int) (j >>> 32));
        this.buffer[4] = (byte) ((int) (j >>> 24));
        this.buffer[5] = (byte) ((int) (j >>> 16));
        this.buffer[6] = (byte) ((int) (j >>> 8));
        this.buffer[7] = (byte) ((int) j);
        return this.buffer;
    }

    private long bytesToLong(byte[] bArr) {
        return ((((((((((long) bArr[0]) & 255) << 56) | ((((long) bArr[1]) & 255) << 48)) | ((((long) bArr[2]) & 255) << 40)) | ((((long) bArr[3]) & 255) << 32)) | ((((long) bArr[4]) & 255) << 24)) | ((((long) bArr[5]) & 255) << 16)) | ((((long) bArr[6]) & 255) << 8)) | (((long) bArr[7]) & 255);
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

    private StatsController(int r15) {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:60)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
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
        r10 = "stats2.dat";	 Catch:{ Exception -> 0x012d }
        r9.<init>(r3, r10);	 Catch:{ Exception -> 0x012d }
        r3 = "rw";	 Catch:{ Exception -> 0x012d }
        r8.<init>(r9, r3);	 Catch:{ Exception -> 0x012d }
        r14.statsFile = r8;	 Catch:{ Exception -> 0x012d }
        r3 = r14.statsFile;	 Catch:{ Exception -> 0x012d }
        r8 = r3.length();	 Catch:{ Exception -> 0x012d }
        r3 = (r8 > r4 ? 1 : (r8 == r4 ? 0 : -1));	 Catch:{ Exception -> 0x012d }
        if (r3 <= 0) goto L_0x012d;	 Catch:{ Exception -> 0x012d }
    L_0x0098:
        r3 = r7;	 Catch:{ Exception -> 0x012d }
        r8 = r3;	 Catch:{ Exception -> 0x012d }
    L_0x009a:
        if (r3 >= r2) goto L_0x0126;	 Catch:{ Exception -> 0x012d }
    L_0x009c:
        r9 = r7;	 Catch:{ Exception -> 0x012d }
    L_0x009d:
        r10 = 4;	 Catch:{ Exception -> 0x012d }
        if (r9 >= r1) goto L_0x00ef;	 Catch:{ Exception -> 0x012d }
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
        r9 = r9 + 1;	 Catch:{ Exception -> 0x012d }
        goto L_0x009d;	 Catch:{ Exception -> 0x012d }
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
        r9 = (r10 > r4 ? 1 : (r10 == r4 ? 0 : -1));	 Catch:{ Exception -> 0x012d }
        if (r9 != 0) goto L_0x0122;	 Catch:{ Exception -> 0x012d }
    L_0x0119:
        r8 = r14.resetStatsDate;	 Catch:{ Exception -> 0x012d }
        r9 = java.lang.System.currentTimeMillis();	 Catch:{ Exception -> 0x012d }
        r8[r3] = r9;	 Catch:{ Exception -> 0x012d }
        r8 = r6;	 Catch:{ Exception -> 0x012d }
    L_0x0122:
        r3 = r3 + 1;	 Catch:{ Exception -> 0x012d }
        goto L_0x009a;	 Catch:{ Exception -> 0x012d }
    L_0x0126:
        if (r8 == 0) goto L_0x012b;	 Catch:{ Exception -> 0x012d }
    L_0x0128:
        r14.saveStats();	 Catch:{ Exception -> 0x012d }
    L_0x012b:
        r0 = r7;
        goto L_0x012e;
    L_0x012d:
        r0 = r6;
    L_0x012e:
        if (r0 == 0) goto L_0x0235;
    L_0x0130:
        if (r15 != 0) goto L_0x013b;
    L_0x0132:
        r15 = org.telegram.messenger.ApplicationLoader.applicationContext;
        r0 = "stats";
        r15 = r15.getSharedPreferences(r0, r7);
        goto L_0x0152;
    L_0x013b:
        r0 = org.telegram.messenger.ApplicationLoader.applicationContext;
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r8 = "stats";
        r3.append(r8);
        r3.append(r15);
        r15 = r3.toString();
        r15 = r0.getSharedPreferences(r15, r7);
    L_0x0152:
        r0 = r7;
        r3 = r0;
    L_0x0154:
        if (r0 >= r2) goto L_0x0230;
    L_0x0156:
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
        r8 = r7;
    L_0x0189:
        if (r8 >= r1) goto L_0x021b;
    L_0x018b:
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
        r10 = r15.getLong(r10, r4);
        r9[r8] = r10;
        r9 = r14.receivedBytes;
        r9 = r9[r0];
        r10 = new java.lang.StringBuilder;
        r10.<init>();
        r11 = "receivedBytes";
        r10.append(r11);
        r10.append(r0);
        r11 = "_";
        r10.append(r11);
        r10.append(r8);
        r10 = r10.toString();
        r10 = r15.getLong(r10, r4);
        r9[r8] = r10;
        r9 = r14.sentItems;
        r9 = r9[r0];
        r10 = new java.lang.StringBuilder;
        r10.<init>();
        r11 = "sentItems";
        r10.append(r11);
        r10.append(r0);
        r11 = "_";
        r10.append(r11);
        r10.append(r8);
        r10 = r10.toString();
        r10 = r15.getInt(r10, r7);
        r9[r8] = r10;
        r9 = r14.receivedItems;
        r9 = r9[r0];
        r10 = new java.lang.StringBuilder;
        r10.<init>();
        r11 = "receivedItems";
        r10.append(r11);
        r10.append(r0);
        r11 = "_";
        r10.append(r11);
        r10.append(r8);
        r10 = r10.toString();
        r10 = r15.getInt(r10, r7);
        r9[r8] = r10;
        r8 = r8 + 1;
        goto L_0x0189;
    L_0x021b:
        r8 = r14.resetStatsDate;
        r9 = r8[r0];
        r8 = (r9 > r4 ? 1 : (r9 == r4 ? 0 : -1));
        if (r8 != 0) goto L_0x022c;
    L_0x0223:
        r3 = r14.resetStatsDate;
        r8 = java.lang.System.currentTimeMillis();
        r3[r0] = r8;
        r3 = r6;
    L_0x022c:
        r0 = r0 + 1;
        goto L_0x0154;
    L_0x0230:
        if (r3 == 0) goto L_0x0235;
    L_0x0232:
        r14.saveStats();
    L_0x0235:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.StatsController.<init>(int):void");
    }

    public void incrementReceivedItemsCount(int i, int i2, int i3) {
        i = this.receivedItems[i];
        i[i2] = i[i2] + i3;
        saveStats();
    }

    public void incrementSentItemsCount(int i, int i2, int i3) {
        i = this.sentItems[i];
        i[i2] = i[i2] + i3;
        saveStats();
    }

    public void incrementReceivedBytesCount(int i, int i2, long j) {
        i = this.receivedBytes[i];
        i[i2] = i[i2] + j;
        saveStats();
    }

    public void incrementSentBytesCount(int i, int i2, long j) {
        i = this.sentBytes[i];
        i[i2] = i[i2] + j;
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
        if (i2 == 1) {
            return (((this.sentBytes[i][6] - this.sentBytes[i][5]) - this.sentBytes[i][3]) - this.sentBytes[i][2]) - this.sentBytes[i][4];
        }
        return this.sentBytes[i][i2];
    }

    public long getReceivedBytesCount(int i, int i2) {
        if (i2 == 1) {
            return (((this.receivedBytes[i][6] - this.receivedBytes[i][5]) - this.receivedBytes[i][3]) - this.receivedBytes[i][2]) - this.receivedBytes[i][4];
        }
        return this.receivedBytes[i][i2];
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
        if (Math.abs(currentTimeMillis - ((Long) lastStatsSaveTime.get()).longValue()) >= AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS) {
            lastStatsSaveTime.set(Long.valueOf(currentTimeMillis));
            statsSaveQueue.postRunnable(this.saveRunnable);
        }
    }
}
