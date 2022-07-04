package org.telegram.messenger;

import android.content.SharedPreferences;
import android.util.Pair;
import android.util.SparseArray;
import androidx.collection.LongSparseArray;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import org.telegram.SQLite.SQLiteCursor;
import org.telegram.SQLite.SQLiteDatabase;
import org.telegram.SQLite.SQLitePreparedStatement;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.tgnet.NativeByteBuffer;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public class DownloadController extends BaseController implements NotificationCenter.NotificationCenterDelegate {
    public static final int AUTODOWNLOAD_TYPE_AUDIO = 2;
    public static final int AUTODOWNLOAD_TYPE_DOCUMENT = 8;
    public static final int AUTODOWNLOAD_TYPE_PHOTO = 1;
    public static final int AUTODOWNLOAD_TYPE_VIDEO = 4;
    private static volatile DownloadController[] Instance = new DownloadController[4];
    public static final int PRESET_NUM_CHANNEL = 3;
    public static final int PRESET_NUM_CONTACT = 0;
    public static final int PRESET_NUM_GROUP = 2;
    public static final int PRESET_NUM_PM = 1;
    public static final int PRESET_SIZE_NUM_AUDIO = 3;
    public static final int PRESET_SIZE_NUM_DOCUMENT = 2;
    public static final int PRESET_SIZE_NUM_PHOTO = 0;
    public static final int PRESET_SIZE_NUM_VIDEO = 1;
    private HashMap<String, FileDownloadProgressListener> addLaterArray = new HashMap<>();
    private ArrayList<DownloadObject> audioDownloadQueue = new ArrayList<>();
    Runnable clearUnviewedDownloadsRunnale = new Runnable() {
        public void run() {
            DownloadController.this.clearUnviewedDownloads();
            DownloadController.this.getNotificationCenter().postNotificationName(NotificationCenter.onDownloadingFilesChanged, new Object[0]);
        }
    };
    public int currentMobilePreset;
    public int currentRoamingPreset;
    public int currentWifiPreset;
    private ArrayList<FileDownloadProgressListener> deleteLaterArray = new ArrayList<>();
    private ArrayList<DownloadObject> documentDownloadQueue = new ArrayList<>();
    private HashMap<String, DownloadObject> downloadQueueKeys = new HashMap<>();
    private HashMap<Pair<Long, Integer>, DownloadObject> downloadQueuePairs = new HashMap<>();
    public final ArrayList<MessageObject> downloadingFiles = new ArrayList<>();
    public Preset highPreset;
    private int lastCheckMask = 0;
    private int lastTag = 0;
    private boolean listenerInProgress = false;
    private boolean loadingAutoDownloadConfig;
    private HashMap<String, ArrayList<MessageObject>> loadingFileMessagesObservers = new HashMap<>();
    private HashMap<String, ArrayList<WeakReference<FileDownloadProgressListener>>> loadingFileObservers = new HashMap<>();
    public Preset lowPreset;
    public Preset mediumPreset;
    public Preset mobilePreset;
    private SparseArray<String> observersByTag = new SparseArray<>();
    private ArrayList<DownloadObject> photoDownloadQueue = new ArrayList<>();
    public final ArrayList<MessageObject> recentDownloadingFiles = new ArrayList<>();
    public Preset roamingPreset;
    private LongSparseArray<Long> typingTimes = new LongSparseArray<>();
    public final SparseArray<MessageObject> unviewedDownloads = new SparseArray<>();
    private ArrayList<DownloadObject> videoDownloadQueue = new ArrayList<>();
    public Preset wifiPreset;

    public interface FileDownloadProgressListener {
        int getObserverTag();

        void onFailedDownload(String str, boolean z);

        void onProgressDownload(String str, long j, long j2);

        void onProgressUpload(String str, long j, long j2, boolean z);

        void onSuccessDownload(String str);
    }

    public static class Preset {
        public boolean enabled;
        public boolean lessCallData;
        public int[] mask;
        public int maxVideoBitrate;
        public boolean preloadMusic;
        public boolean preloadVideo;
        public long[] sizes;

        public Preset(int[] m, long p, long v, long f, boolean pv, boolean pm, boolean e, boolean l, int bitrate) {
            int[] iArr = new int[4];
            this.mask = iArr;
            this.sizes = new long[4];
            int[] iArr2 = m;
            System.arraycopy(m, 0, iArr, 0, iArr.length);
            long[] jArr = this.sizes;
            jArr[0] = p;
            jArr[1] = v;
            jArr[2] = f;
            jArr[3] = 524288;
            this.preloadVideo = pv;
            this.preloadMusic = pm;
            this.lessCallData = l;
            this.maxVideoBitrate = bitrate;
            this.enabled = e;
        }

        public Preset(String str, String deafultValue) {
            this.mask = new int[4];
            this.sizes = new long[4];
            String[] args = str.split("_");
            String[] defaultArgs = null;
            if (args.length >= 11) {
                boolean z = false;
                this.mask[0] = Utilities.parseInt((CharSequence) args[0]).intValue();
                this.mask[1] = Utilities.parseInt((CharSequence) args[1]).intValue();
                this.mask[2] = Utilities.parseInt((CharSequence) args[2]).intValue();
                this.mask[3] = Utilities.parseInt((CharSequence) args[3]).intValue();
                this.sizes[0] = (long) Utilities.parseInt((CharSequence) args[4]).intValue();
                this.sizes[1] = (long) Utilities.parseInt((CharSequence) args[5]).intValue();
                this.sizes[2] = (long) Utilities.parseInt((CharSequence) args[6]).intValue();
                this.sizes[3] = (long) Utilities.parseInt((CharSequence) args[7]).intValue();
                this.preloadVideo = Utilities.parseInt((CharSequence) args[8]).intValue() == 1;
                this.preloadMusic = Utilities.parseInt((CharSequence) args[9]).intValue() == 1;
                this.enabled = Utilities.parseInt((CharSequence) args[10]).intValue() == 1;
                if (args.length >= 12) {
                    this.lessCallData = Utilities.parseInt((CharSequence) args[11]).intValue() == 1 ? true : z;
                } else {
                    defaultArgs = deafultValue.split("_");
                    this.lessCallData = Utilities.parseInt((CharSequence) defaultArgs[11]).intValue() == 1 ? true : z;
                }
                if (args.length >= 13) {
                    this.maxVideoBitrate = Utilities.parseInt((CharSequence) args[12]).intValue();
                } else {
                    this.maxVideoBitrate = Utilities.parseInt((CharSequence) (defaultArgs == null ? deafultValue.split("_") : defaultArgs)[12]).intValue();
                }
            }
        }

        public void set(Preset preset) {
            int[] iArr = preset.mask;
            int[] iArr2 = this.mask;
            System.arraycopy(iArr, 0, iArr2, 0, iArr2.length);
            long[] jArr = preset.sizes;
            long[] jArr2 = this.sizes;
            System.arraycopy(jArr, 0, jArr2, 0, jArr2.length);
            this.preloadVideo = preset.preloadVideo;
            this.preloadMusic = preset.preloadMusic;
            this.lessCallData = preset.lessCallData;
            this.maxVideoBitrate = preset.maxVideoBitrate;
        }

        public void set(TLRPC.TL_autoDownloadSettings settings) {
            this.preloadMusic = settings.audio_preload_next;
            this.preloadVideo = settings.video_preload_large;
            this.lessCallData = settings.phonecalls_less_data;
            this.maxVideoBitrate = settings.video_upload_maxbitrate;
            this.sizes[0] = (long) Math.max(512000, settings.photo_size_max);
            this.sizes[1] = Math.max(512000, settings.video_size_max);
            this.sizes[2] = Math.max(512000, settings.file_size_max);
            for (int a = 0; a < this.mask.length; a++) {
                if (settings.photo_size_max == 0 || settings.disabled) {
                    int[] iArr = this.mask;
                    iArr[a] = iArr[a] & -2;
                } else {
                    int[] iArr2 = this.mask;
                    iArr2[a] = iArr2[a] | 1;
                }
                if (settings.video_size_max == 0 || settings.disabled) {
                    int[] iArr3 = this.mask;
                    iArr3[a] = iArr3[a] & -5;
                } else {
                    int[] iArr4 = this.mask;
                    iArr4[a] = iArr4[a] | 4;
                }
                if (settings.file_size_max == 0 || settings.disabled) {
                    int[] iArr5 = this.mask;
                    iArr5[a] = iArr5[a] & -9;
                } else {
                    int[] iArr6 = this.mask;
                    iArr6[a] = iArr6[a] | 8;
                }
            }
        }

        public String toString() {
            return this.mask[0] + "_" + this.mask[1] + "_" + this.mask[2] + "_" + this.mask[3] + "_" + this.sizes[0] + "_" + this.sizes[1] + "_" + this.sizes[2] + "_" + this.sizes[3] + "_" + (this.preloadVideo ? 1 : 0) + "_" + (this.preloadMusic ? 1 : 0) + "_" + (this.enabled ? 1 : 0) + "_" + (this.lessCallData ? 1 : 0) + "_" + this.maxVideoBitrate;
        }

        public boolean equals(Preset obj) {
            int[] iArr = this.mask;
            int i = iArr[0];
            int[] iArr2 = obj.mask;
            if (i != iArr2[0] || iArr[1] != iArr2[1] || iArr[2] != iArr2[2] || iArr[3] != iArr2[3]) {
                return false;
            }
            long[] jArr = this.sizes;
            long j = jArr[0];
            long[] jArr2 = obj.sizes;
            return j == jArr2[0] && jArr[1] == jArr2[1] && jArr[2] == jArr2[2] && jArr[3] == jArr2[3] && this.preloadVideo == obj.preloadVideo && this.preloadMusic == obj.preloadMusic && this.maxVideoBitrate == obj.maxVideoBitrate;
        }

        public boolean isEnabled() {
            int a = 0;
            while (true) {
                int[] iArr = this.mask;
                if (a >= iArr.length) {
                    return false;
                }
                if (iArr[a] != 0) {
                    return true;
                }
                a++;
            }
        }
    }

    public static DownloadController getInstance(int num) {
        DownloadController localInstance = Instance[num];
        if (localInstance == null) {
            synchronized (DownloadController.class) {
                localInstance = Instance[num];
                if (localInstance == null) {
                    DownloadController[] downloadControllerArr = Instance;
                    DownloadController downloadController = new DownloadController(num);
                    localInstance = downloadController;
                    downloadControllerArr[num] = downloadController;
                }
            }
        }
        return localInstance;
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    /* JADX WARNING: Removed duplicated region for block: B:34:0x0312  */
    /* JADX WARNING: Removed duplicated region for block: B:39:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public DownloadController(int r38) {
        /*
            r37 = this;
            r0 = r37
            r37.<init>(r38)
            r1 = 0
            r0.lastCheckMask = r1
            java.util.ArrayList r2 = new java.util.ArrayList
            r2.<init>()
            r0.photoDownloadQueue = r2
            java.util.ArrayList r2 = new java.util.ArrayList
            r2.<init>()
            r0.audioDownloadQueue = r2
            java.util.ArrayList r2 = new java.util.ArrayList
            r2.<init>()
            r0.documentDownloadQueue = r2
            java.util.ArrayList r2 = new java.util.ArrayList
            r2.<init>()
            r0.videoDownloadQueue = r2
            java.util.HashMap r2 = new java.util.HashMap
            r2.<init>()
            r0.downloadQueueKeys = r2
            java.util.HashMap r2 = new java.util.HashMap
            r2.<init>()
            r0.downloadQueuePairs = r2
            java.util.HashMap r2 = new java.util.HashMap
            r2.<init>()
            r0.loadingFileObservers = r2
            java.util.HashMap r2 = new java.util.HashMap
            r2.<init>()
            r0.loadingFileMessagesObservers = r2
            android.util.SparseArray r2 = new android.util.SparseArray
            r2.<init>()
            r0.observersByTag = r2
            r0.listenerInProgress = r1
            java.util.HashMap r2 = new java.util.HashMap
            r2.<init>()
            r0.addLaterArray = r2
            java.util.ArrayList r2 = new java.util.ArrayList
            r2.<init>()
            r0.deleteLaterArray = r2
            r0.lastTag = r1
            androidx.collection.LongSparseArray r2 = new androidx.collection.LongSparseArray
            r2.<init>()
            r0.typingTimes = r2
            java.util.ArrayList r2 = new java.util.ArrayList
            r2.<init>()
            r0.downloadingFiles = r2
            java.util.ArrayList r2 = new java.util.ArrayList
            r2.<init>()
            r0.recentDownloadingFiles = r2
            android.util.SparseArray r2 = new android.util.SparseArray
            r2.<init>()
            r0.unviewedDownloads = r2
            org.telegram.messenger.DownloadController$2 r2 = new org.telegram.messenger.DownloadController$2
            r2.<init>()
            r0.clearUnviewedDownloadsRunnale = r2
            int r2 = r0.currentAccount
            android.content.SharedPreferences r2 = org.telegram.messenger.MessagesController.getMainSettings(r2)
            java.lang.String r3 = "1_1_1_1_1048576_512000_512000_524288_0_0_1_1_50"
            java.lang.String r4 = "13_13_13_13_1048576_10485760_1048576_524288_1_1_1_0_100"
            java.lang.String r5 = "13_13_13_13_1048576_15728640_3145728_524288_1_1_1_0_100"
            org.telegram.messenger.DownloadController$Preset r6 = new org.telegram.messenger.DownloadController$Preset
            java.lang.String r7 = "preset0"
            java.lang.String r7 = r2.getString(r7, r3)
            r6.<init>(r7, r3)
            r0.lowPreset = r6
            org.telegram.messenger.DownloadController$Preset r6 = new org.telegram.messenger.DownloadController$Preset
            java.lang.String r7 = "preset1"
            java.lang.String r7 = r2.getString(r7, r4)
            r6.<init>(r7, r4)
            r0.mediumPreset = r6
            org.telegram.messenger.DownloadController$Preset r6 = new org.telegram.messenger.DownloadController$Preset
            java.lang.String r7 = "preset2"
            java.lang.String r7 = r2.getString(r7, r5)
            r6.<init>(r7, r5)
            r0.highPreset = r6
            java.lang.String r6 = "newConfig"
            boolean r7 = r2.contains(r6)
            r8 = r7
            java.lang.String r9 = "currentRoamingPreset"
            java.lang.String r10 = "currentWifiPreset"
            java.lang.String r11 = "currentMobilePreset"
            java.lang.String r12 = "roamingPreset"
            java.lang.String r13 = "wifiPreset"
            java.lang.String r14 = "mobilePreset"
            if (r7 != 0) goto L_0x0297
            org.telegram.messenger.UserConfig r7 = r37.getUserConfig()
            boolean r7 = r7.isClientActivated()
            if (r7 != 0) goto L_0x00e0
            r30 = r3
            r32 = r4
            r31 = r5
            r29 = r8
            r1 = r9
            r5 = r12
            r12 = r11
            r36 = r13
            r13 = r10
            r10 = r36
            goto L_0x02a7
        L_0x00e0:
            r7 = 4
            int[] r15 = new int[r7]
            int[] r1 = new int[r7]
            r29 = r8
            int[] r8 = new int[r7]
            r7 = 7
            r30 = r3
            long[] r3 = new long[r7]
            r31 = r5
            long[] r5 = new long[r7]
            long[] r7 = new long[r7]
            r17 = 0
            r32 = r4
            r4 = r17
        L_0x00fa:
            r33 = r9
            r9 = 4
            if (r4 >= r9) goto L_0x018d
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            r34 = r10
            java.lang.String r10 = "mobileDataDownloadMask"
            r9.append(r10)
            java.lang.String r10 = ""
            if (r4 != 0) goto L_0x0112
            r18 = r10
            goto L_0x011a
        L_0x0112:
            java.lang.Integer r17 = java.lang.Integer.valueOf(r4)
            r18 = r10
            r10 = r17
        L_0x011a:
            r9.append(r10)
            java.lang.String r9 = r9.toString()
            if (r4 == 0) goto L_0x0138
            boolean r10 = r2.contains(r9)
            if (r10 == 0) goto L_0x012a
            goto L_0x0138
        L_0x012a:
            r10 = 0
            r17 = r15[r10]
            r15[r4] = r17
            r17 = r1[r10]
            r1[r4] = r17
            r17 = r8[r10]
            r8[r4] = r17
            goto L_0x0185
        L_0x0138:
            r10 = 13
            int r17 = r2.getInt(r9, r10)
            r15[r4] = r17
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            r19 = r9
            java.lang.String r9 = "wifiDownloadMask"
            r10.append(r9)
            if (r4 != 0) goto L_0x0151
            r9 = r18
            goto L_0x0155
        L_0x0151:
            java.lang.Integer r9 = java.lang.Integer.valueOf(r4)
        L_0x0155:
            r10.append(r9)
            java.lang.String r9 = r10.toString()
            r10 = 13
            int r9 = r2.getInt(r9, r10)
            r1[r4] = r9
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            java.lang.String r10 = "roamingDownloadMask"
            r9.append(r10)
            if (r4 != 0) goto L_0x0173
            r10 = r18
            goto L_0x0177
        L_0x0173:
            java.lang.Integer r10 = java.lang.Integer.valueOf(r4)
        L_0x0177:
            r9.append(r10)
            java.lang.String r9 = r9.toString()
            r10 = 1
            int r9 = r2.getInt(r9, r10)
            r8[r4] = r9
        L_0x0185:
            int r4 = r4 + 1
            r9 = r33
            r10 = r34
            goto L_0x00fa
        L_0x018d:
            r34 = r10
            org.telegram.messenger.DownloadController$Preset r4 = r0.mediumPreset
            long[] r4 = r4.sizes
            r35 = r11
            r9 = 1
            r10 = r4[r9]
            java.lang.String r4 = "mobileMaxDownloadSize2"
            long r9 = r2.getLong(r4, r10)
            r4 = 2
            r3[r4] = r9
            org.telegram.messenger.DownloadController$Preset r9 = r0.mediumPreset
            long[] r9 = r9.sizes
            r10 = r9[r4]
            java.lang.String r9 = "mobileMaxDownloadSize3"
            long r9 = r2.getLong(r9, r10)
            r11 = 3
            r3[r11] = r9
            org.telegram.messenger.DownloadController$Preset r9 = r0.highPreset
            long[] r9 = r9.sizes
            r11 = r5
            r10 = 1
            r4 = r9[r10]
            java.lang.String r9 = "wifiMaxDownloadSize2"
            long r4 = r2.getLong(r9, r4)
            r9 = 2
            r11[r9] = r4
            org.telegram.messenger.DownloadController$Preset r4 = r0.highPreset
            long[] r4 = r4.sizes
            r5 = r12
            r10 = r13
            r12 = r4[r9]
            java.lang.String r4 = "wifiMaxDownloadSize3"
            long r12 = r2.getLong(r4, r12)
            r4 = 3
            r11[r4] = r12
            org.telegram.messenger.DownloadController$Preset r4 = r0.lowPreset
            long[] r4 = r4.sizes
            r13 = r10
            r12 = 1
            r9 = r4[r12]
            java.lang.String r4 = "roamingMaxDownloadSize2"
            long r9 = r2.getLong(r4, r9)
            r4 = 2
            r7[r4] = r9
            org.telegram.messenger.DownloadController$Preset r9 = r0.lowPreset
            long[] r9 = r9.sizes
            r10 = r13
            r12 = r9[r4]
            java.lang.String r4 = "roamingMaxDownloadSize3"
            long r12 = r2.getLong(r4, r12)
            r4 = 3
            r7[r4] = r12
            java.lang.String r9 = "globalAutodownloadEnabled"
            r12 = 1
            boolean r9 = r2.getBoolean(r9, r12)
            org.telegram.messenger.DownloadController$Preset r12 = new org.telegram.messenger.DownloadController$Preset
            org.telegram.messenger.DownloadController$Preset r13 = r0.mediumPreset
            long[] r13 = r13.sizes
            r16 = 0
            r18 = r13[r16]
            r13 = 2
            r20 = r3[r13]
            r22 = r3[r4]
            r24 = 1
            r25 = 1
            r27 = 0
            r28 = 100
            r16 = r12
            r17 = r15
            r26 = r9
            r16.<init>(r17, r18, r20, r22, r24, r25, r26, r27, r28)
            r0.mobilePreset = r12
            org.telegram.messenger.DownloadController$Preset r4 = new org.telegram.messenger.DownloadController$Preset
            org.telegram.messenger.DownloadController$Preset r12 = r0.highPreset
            long[] r12 = r12.sizes
            r13 = 0
            r18 = r12[r13]
            r12 = 2
            r20 = r11[r12]
            r12 = 3
            r22 = r11[r12]
            r16 = r4
            r17 = r1
            r16.<init>(r17, r18, r20, r22, r24, r25, r26, r27, r28)
            r0.wifiPreset = r4
            org.telegram.messenger.DownloadController$Preset r4 = new org.telegram.messenger.DownloadController$Preset
            org.telegram.messenger.DownloadController$Preset r12 = r0.lowPreset
            long[] r12 = r12.sizes
            r13 = 0
            r18 = r12[r13]
            r12 = 2
            r20 = r7[r12]
            r12 = 3
            r22 = r7[r12]
            r24 = 0
            r25 = 0
            r27 = 1
            r28 = 50
            r16 = r4
            r17 = r8
            r16.<init>(r17, r18, r20, r22, r24, r25, r26, r27, r28)
            r0.roamingPreset = r4
            android.content.SharedPreferences$Editor r4 = r2.edit()
            r12 = 1
            r4.putBoolean(r6, r12)
            org.telegram.messenger.DownloadController$Preset r6 = r0.mobilePreset
            java.lang.String r6 = r6.toString()
            r4.putString(r14, r6)
            org.telegram.messenger.DownloadController$Preset r6 = r0.wifiPreset
            java.lang.String r6 = r6.toString()
            r4.putString(r10, r6)
            org.telegram.messenger.DownloadController$Preset r6 = r0.roamingPreset
            java.lang.String r6 = r6.toString()
            r4.putString(r5, r6)
            r5 = 3
            r0.currentMobilePreset = r5
            r12 = r35
            r4.putInt(r12, r5)
            r0.currentWifiPreset = r5
            r13 = r34
            r4.putInt(r13, r5)
            r0.currentRoamingPreset = r5
            r6 = r33
            r4.putInt(r6, r5)
            r4.commit()
            r8 = r30
            r7 = r31
            r4 = r32
            goto L_0x02ef
        L_0x0297:
            r30 = r3
            r32 = r4
            r31 = r5
            r29 = r8
            r1 = r9
            r5 = r12
            r12 = r11
            r36 = r13
            r13 = r10
            r10 = r36
        L_0x02a7:
            org.telegram.messenger.DownloadController$Preset r3 = new org.telegram.messenger.DownloadController$Preset
            r4 = r32
            java.lang.String r7 = r2.getString(r14, r4)
            r3.<init>(r7, r4)
            r0.mobilePreset = r3
            org.telegram.messenger.DownloadController$Preset r3 = new org.telegram.messenger.DownloadController$Preset
            r7 = r31
            java.lang.String r8 = r2.getString(r10, r7)
            r3.<init>(r8, r7)
            r0.wifiPreset = r3
            org.telegram.messenger.DownloadController$Preset r3 = new org.telegram.messenger.DownloadController$Preset
            r8 = r30
            java.lang.String r5 = r2.getString(r5, r8)
            r3.<init>(r5, r8)
            r0.roamingPreset = r3
            r3 = 3
            int r5 = r2.getInt(r12, r3)
            r0.currentMobilePreset = r5
            int r5 = r2.getInt(r13, r3)
            r0.currentWifiPreset = r5
            int r1 = r2.getInt(r1, r3)
            r0.currentRoamingPreset = r1
            if (r29 != 0) goto L_0x02ef
            android.content.SharedPreferences$Editor r1 = r2.edit()
            r3 = 1
            android.content.SharedPreferences$Editor r1 = r1.putBoolean(r6, r3)
            r1.commit()
        L_0x02ef:
            org.telegram.messenger.DownloadController$$ExternalSyntheticLambda6 r1 = new org.telegram.messenger.DownloadController$$ExternalSyntheticLambda6
            r1.<init>(r0)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r1)
            org.telegram.messenger.DownloadController$1 r1 = new org.telegram.messenger.DownloadController$1
            r1.<init>()
            android.content.IntentFilter r3 = new android.content.IntentFilter
            java.lang.String r5 = "android.net.conn.CONNECTIVITY_CHANGE"
            r3.<init>(r5)
            android.content.Context r5 = org.telegram.messenger.ApplicationLoader.applicationContext
            r5.registerReceiver(r1, r3)
            org.telegram.messenger.UserConfig r5 = r37.getUserConfig()
            boolean r5 = r5.isClientActivated()
            if (r5 == 0) goto L_0x0315
            r37.checkAutodownloadSettings()
        L_0x0315:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.DownloadController.<init>(int):void");
    }

    /* renamed from: lambda$new$0$org-telegram-messenger-DownloadController  reason: not valid java name */
    public /* synthetic */ void m63lambda$new$0$orgtelegrammessengerDownloadController() {
        getNotificationCenter().addObserver(this, NotificationCenter.fileLoadFailed);
        getNotificationCenter().addObserver(this, NotificationCenter.fileLoaded);
        getNotificationCenter().addObserver(this, NotificationCenter.fileLoadProgressChanged);
        getNotificationCenter().addObserver(this, NotificationCenter.fileUploadProgressChanged);
        getNotificationCenter().addObserver(this, NotificationCenter.httpFileDidLoad);
        getNotificationCenter().addObserver(this, NotificationCenter.httpFileDidFailedLoad);
        loadAutoDownloadConfig(false);
    }

    public void loadAutoDownloadConfig(boolean force) {
        if (this.loadingAutoDownloadConfig) {
            return;
        }
        if (force || Math.abs(System.currentTimeMillis() - getUserConfig().autoDownloadConfigLoadTime) >= 86400000) {
            this.loadingAutoDownloadConfig = true;
            getConnectionsManager().sendRequest(new TLRPC.TL_account_getAutoDownloadSettings(), new DownloadController$$ExternalSyntheticLambda3(this));
        }
    }

    /* renamed from: lambda$loadAutoDownloadConfig$2$org-telegram-messenger-DownloadController  reason: not valid java name */
    public /* synthetic */ void m60xc6var_CLASSNAME(TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new DownloadController$$ExternalSyntheticLambda2(this, response));
    }

    /* renamed from: lambda$loadAutoDownloadConfig$1$org-telegram-messenger-DownloadController  reason: not valid java name */
    public /* synthetic */ void m59xe1b1dda6(TLObject response) {
        Preset preset;
        this.loadingAutoDownloadConfig = false;
        getUserConfig().autoDownloadConfigLoadTime = System.currentTimeMillis();
        getUserConfig().saveConfig(false);
        if (response != null) {
            TLRPC.TL_account_autoDownloadSettings res = (TLRPC.TL_account_autoDownloadSettings) response;
            this.lowPreset.set(res.low);
            this.mediumPreset.set(res.medium);
            this.highPreset.set(res.high);
            for (int a = 0; a < 3; a++) {
                if (a == 0) {
                    preset = this.mobilePreset;
                } else if (a == 1) {
                    preset = this.wifiPreset;
                } else {
                    preset = this.roamingPreset;
                }
                if (preset.equals(this.lowPreset)) {
                    preset.set(res.low);
                } else if (preset.equals(this.mediumPreset)) {
                    preset.set(res.medium);
                } else if (preset.equals(this.highPreset)) {
                    preset.set(res.high);
                }
            }
            SharedPreferences.Editor editor = MessagesController.getMainSettings(this.currentAccount).edit();
            editor.putString("mobilePreset", this.mobilePreset.toString());
            editor.putString("wifiPreset", this.wifiPreset.toString());
            editor.putString("roamingPreset", this.roamingPreset.toString());
            editor.putString("preset0", this.lowPreset.toString());
            editor.putString("preset1", this.mediumPreset.toString());
            editor.putString("preset2", this.highPreset.toString());
            editor.commit();
            String preset2 = this.lowPreset.toString();
            String preset3 = this.mediumPreset.toString();
            String preset4 = this.highPreset.toString();
            checkAutodownloadSettings();
        }
    }

    public Preset getCurrentMobilePreset() {
        int i = this.currentMobilePreset;
        if (i == 0) {
            return this.lowPreset;
        }
        if (i == 1) {
            return this.mediumPreset;
        }
        if (i == 2) {
            return this.highPreset;
        }
        return this.mobilePreset;
    }

    public Preset getCurrentWiFiPreset() {
        int i = this.currentWifiPreset;
        if (i == 0) {
            return this.lowPreset;
        }
        if (i == 1) {
            return this.mediumPreset;
        }
        if (i == 2) {
            return this.highPreset;
        }
        return this.wifiPreset;
    }

    public Preset getCurrentRoamingPreset() {
        int i = this.currentRoamingPreset;
        if (i == 0) {
            return this.lowPreset;
        }
        if (i == 1) {
            return this.mediumPreset;
        }
        if (i == 2) {
            return this.highPreset;
        }
        return this.roamingPreset;
    }

    public static int typeToIndex(int type) {
        if (type == 1) {
            return 0;
        }
        if (type == 2) {
            return 2;
        }
        if (type == 4) {
            return 1;
        }
        if (type == 8) {
            return 2;
        }
        return 0;
    }

    public void cleanup() {
        this.photoDownloadQueue.clear();
        this.audioDownloadQueue.clear();
        this.documentDownloadQueue.clear();
        this.videoDownloadQueue.clear();
        this.downloadQueueKeys.clear();
        this.downloadQueuePairs.clear();
        this.typingTimes.clear();
    }

    public int getMaxVideoBitrate() {
        int networkType = ApplicationLoader.getAutodownloadNetworkType();
        if (networkType == 1) {
            return getCurrentWiFiPreset().maxVideoBitrate;
        }
        if (networkType == 2) {
            return getCurrentRoamingPreset().maxVideoBitrate;
        }
        return getCurrentMobilePreset().maxVideoBitrate;
    }

    public int getAutodownloadMask() {
        int[] masksArray;
        int result = 0;
        int networkType = ApplicationLoader.getAutodownloadNetworkType();
        if (networkType == 1) {
            if (!this.wifiPreset.enabled) {
                return 0;
            }
            masksArray = getCurrentWiFiPreset().mask;
        } else if (networkType == 2) {
            if (!this.roamingPreset.enabled) {
                return 0;
            }
            masksArray = getCurrentRoamingPreset().mask;
        } else if (!this.mobilePreset.enabled) {
            return 0;
        } else {
            masksArray = getCurrentMobilePreset().mask;
        }
        for (int a = 0; a < masksArray.length; a++) {
            int mask = 0;
            if ((masksArray[a] & 1) != 0) {
                mask = 0 | 1;
            }
            if ((masksArray[a] & 2) != 0) {
                mask |= 2;
            }
            if ((masksArray[a] & 4) != 0) {
                mask |= 4;
            }
            if ((masksArray[a] & 8) != 0) {
                mask |= 8;
            }
            result |= mask << (a * 8);
        }
        return result;
    }

    /* access modifiers changed from: protected */
    public int getAutodownloadMaskAll() {
        if (!this.mobilePreset.enabled && !this.roamingPreset.enabled && !this.wifiPreset.enabled) {
            return 0;
        }
        int mask = 0;
        for (int a = 0; a < 4; a++) {
            if (!((getCurrentMobilePreset().mask[a] & 1) == 0 && (getCurrentWiFiPreset().mask[a] & 1) == 0 && (getCurrentRoamingPreset().mask[a] & 1) == 0)) {
                mask |= 1;
            }
            if (!((getCurrentMobilePreset().mask[a] & 2) == 0 && (getCurrentWiFiPreset().mask[a] & 2) == 0 && (getCurrentRoamingPreset().mask[a] & 2) == 0)) {
                mask |= 2;
            }
            if (!((getCurrentMobilePreset().mask[a] & 4) == 0 && (getCurrentWiFiPreset().mask[a] & 4) == 0 && (4 & getCurrentRoamingPreset().mask[a]) == 0)) {
                mask |= 4;
            }
            if ((getCurrentMobilePreset().mask[a] & 8) != 0 || (getCurrentWiFiPreset().mask[a] & 8) != 0 || (getCurrentRoamingPreset().mask[a] & 8) != 0) {
                mask |= 8;
            }
        }
        return mask;
    }

    public void checkAutodownloadSettings() {
        int currentMask = getCurrentDownloadMask();
        if (currentMask != this.lastCheckMask) {
            this.lastCheckMask = currentMask;
            if ((currentMask & 1) == 0) {
                for (int a = 0; a < this.photoDownloadQueue.size(); a++) {
                    DownloadObject downloadObject = this.photoDownloadQueue.get(a);
                    if (downloadObject.object instanceof TLRPC.Photo) {
                        getFileLoader().cancelLoadFile(FileLoader.getClosestPhotoSizeWithSize(((TLRPC.Photo) downloadObject.object).sizes, AndroidUtilities.getPhotoSize()));
                    } else if (downloadObject.object instanceof TLRPC.Document) {
                        getFileLoader().cancelLoadFile((TLRPC.Document) downloadObject.object);
                    }
                }
                this.photoDownloadQueue.clear();
            } else if (this.photoDownloadQueue.isEmpty()) {
                newDownloadObjectsAvailable(1);
            }
            if ((currentMask & 2) == 0) {
                for (int a2 = 0; a2 < this.audioDownloadQueue.size(); a2++) {
                    getFileLoader().cancelLoadFile((TLRPC.Document) this.audioDownloadQueue.get(a2).object);
                }
                this.audioDownloadQueue.clear();
            } else if (this.audioDownloadQueue.isEmpty()) {
                newDownloadObjectsAvailable(2);
            }
            if ((currentMask & 8) == 0) {
                for (int a3 = 0; a3 < this.documentDownloadQueue.size(); a3++) {
                    getFileLoader().cancelLoadFile((TLRPC.Document) this.documentDownloadQueue.get(a3).object);
                }
                this.documentDownloadQueue.clear();
            } else if (this.documentDownloadQueue.isEmpty()) {
                newDownloadObjectsAvailable(8);
            }
            if ((currentMask & 4) == 0) {
                for (int a4 = 0; a4 < this.videoDownloadQueue.size(); a4++) {
                    getFileLoader().cancelLoadFile((TLRPC.Document) this.videoDownloadQueue.get(a4).object);
                }
                this.videoDownloadQueue.clear();
            } else if (this.videoDownloadQueue.isEmpty()) {
                newDownloadObjectsAvailable(4);
            }
            int mask = getAutodownloadMaskAll();
            if (mask == 0) {
                getMessagesStorage().clearDownloadQueue(0);
                return;
            }
            if ((mask & 1) == 0) {
                getMessagesStorage().clearDownloadQueue(1);
            }
            if ((mask & 2) == 0) {
                getMessagesStorage().clearDownloadQueue(2);
            }
            if ((mask & 4) == 0) {
                getMessagesStorage().clearDownloadQueue(4);
            }
            if ((mask & 8) == 0) {
                getMessagesStorage().clearDownloadQueue(8);
            }
        }
    }

    public boolean canDownloadMedia(MessageObject messageObject) {
        return canDownloadMedia(messageObject.messageOwner) == 1;
    }

    public boolean canDownloadMedia(int type, long size) {
        Preset preset;
        int networkType = ApplicationLoader.getAutodownloadNetworkType();
        if (networkType == 1) {
            if (!this.wifiPreset.enabled) {
                return false;
            }
            preset = getCurrentWiFiPreset();
        } else if (networkType == 2) {
            if (!this.roamingPreset.enabled) {
                return false;
            }
            preset = getCurrentRoamingPreset();
        } else if (!this.mobilePreset.enabled) {
            return false;
        } else {
            preset = getCurrentMobilePreset();
        }
        int mask = preset.mask[1];
        long maxSize = preset.sizes[typeToIndex(type)];
        if (type != 1 && (size == 0 || size > maxSize)) {
            return false;
        }
        if (type == 2 || (mask & type) != 0) {
            return true;
        }
        return false;
    }

    public int canDownloadMedia(TLRPC.Message message) {
        int type;
        int index;
        Preset preset;
        long maxSize;
        TLRPC.Message message2 = message;
        if (message2 == null) {
            return 0;
        }
        boolean isVideoMessage = MessageObject.isVideoMessage(message);
        boolean isVideo = isVideoMessage;
        if (isVideoMessage || MessageObject.isGifMessage(message) || MessageObject.isRoundVideoMessage(message) || MessageObject.isGameMessage(message)) {
            type = 4;
        } else if (MessageObject.isVoiceMessage(message)) {
            type = 2;
        } else if (MessageObject.isPhoto(message) != 0 || MessageObject.isStickerMessage(message) || MessageObject.isAnimatedStickerMessage(message)) {
            type = 1;
        } else if (MessageObject.getDocument(message) == null) {
            return 0;
        } else {
            type = 8;
        }
        TLRPC.Peer peer = message2.peer_id;
        if (peer == null) {
            index = 1;
        } else if (peer.user_id != 0) {
            if (getContactsController().contactsDict.containsKey(Long.valueOf(peer.user_id))) {
                index = 0;
            } else {
                index = 1;
            }
        } else if (peer.chat_id == 0) {
            TLRPC.Chat chat = message2.peer_id.channel_id != 0 ? getMessagesController().getChat(Long.valueOf(message2.peer_id.channel_id)) : null;
            if (!ChatObject.isChannel(chat) || !chat.megagroup) {
                index = 3;
            } else if (!(message2.from_id instanceof TLRPC.TL_peerUser) || !getContactsController().contactsDict.containsKey(Long.valueOf(message2.from_id.user_id))) {
                index = 2;
            } else {
                index = 0;
            }
        } else if (!(message2.from_id instanceof TLRPC.TL_peerUser) || !getContactsController().contactsDict.containsKey(Long.valueOf(message2.from_id.user_id))) {
            index = 2;
        } else {
            index = 0;
        }
        int networkType = ApplicationLoader.getAutodownloadNetworkType();
        if (networkType == 1) {
            if (!this.wifiPreset.enabled) {
                return 0;
            }
            preset = getCurrentWiFiPreset();
        } else if (networkType == 2) {
            if (!this.roamingPreset.enabled) {
                return 0;
            }
            preset = getCurrentRoamingPreset();
        } else if (!this.mobilePreset.enabled) {
            return 0;
        } else {
            preset = getCurrentMobilePreset();
        }
        int mask = preset.mask[index];
        if (type == 2) {
            maxSize = Math.max(524288, preset.sizes[typeToIndex(type)]);
        } else {
            maxSize = preset.sizes[typeToIndex(type)];
        }
        long size = MessageObject.getMessageSize(message);
        return (!isVideo || !preset.preloadVideo || size <= maxSize || maxSize <= 2097152) ? ((type == 1 || (size != 0 && size <= maxSize)) && (type == 2 || (mask & type) != 0)) ? 1 : 0 : (mask & type) != 0 ? 2 : 0;
    }

    /* access modifiers changed from: protected */
    public boolean canDownloadNextTrack() {
        int networkType = ApplicationLoader.getAutodownloadNetworkType();
        if (networkType == 1) {
            if (!this.wifiPreset.enabled || !getCurrentWiFiPreset().preloadMusic) {
                return false;
            }
            return true;
        } else if (networkType == 2) {
            if (!this.roamingPreset.enabled || !getCurrentRoamingPreset().preloadMusic) {
                return false;
            }
            return true;
        } else if (!this.mobilePreset.enabled || !getCurrentMobilePreset().preloadMusic) {
            return false;
        } else {
            return true;
        }
    }

    public int getCurrentDownloadMask() {
        int networkType = ApplicationLoader.getAutodownloadNetworkType();
        if (networkType == 1) {
            if (!this.wifiPreset.enabled) {
                return 0;
            }
            int mask = 0;
            for (int a = 0; a < 4; a++) {
                mask |= getCurrentWiFiPreset().mask[a];
            }
            return mask;
        } else if (networkType == 2) {
            if (!this.roamingPreset.enabled) {
                return 0;
            }
            int mask2 = 0;
            for (int a2 = 0; a2 < 4; a2++) {
                mask2 |= getCurrentRoamingPreset().mask[a2];
            }
            return mask2;
        } else if (!this.mobilePreset.enabled) {
            return 0;
        } else {
            int mask3 = 0;
            for (int a3 = 0; a3 < 4; a3++) {
                mask3 |= getCurrentMobilePreset().mask[a3];
            }
            return mask3;
        }
    }

    public void savePresetToServer(int type) {
        boolean enabled;
        Preset preset;
        TLRPC.TL_account_saveAutoDownloadSettings req = new TLRPC.TL_account_saveAutoDownloadSettings();
        if (type == 0) {
            preset = getCurrentMobilePreset();
            enabled = this.mobilePreset.enabled;
        } else if (type == 1) {
            preset = getCurrentWiFiPreset();
            enabled = this.wifiPreset.enabled;
        } else {
            preset = getCurrentRoamingPreset();
            enabled = this.roamingPreset.enabled;
        }
        req.settings = new TLRPC.TL_autoDownloadSettings();
        req.settings.audio_preload_next = preset.preloadMusic;
        req.settings.video_preload_large = preset.preloadVideo;
        req.settings.phonecalls_less_data = preset.lessCallData;
        req.settings.video_upload_maxbitrate = preset.maxVideoBitrate;
        req.settings.disabled = !enabled;
        boolean photo = false;
        boolean video = false;
        boolean document = false;
        for (int a = 0; a < preset.mask.length; a++) {
            if ((preset.mask[a] & 1) != 0) {
                photo = true;
            }
            if ((preset.mask[a] & 4) != 0) {
                video = true;
            }
            if ((preset.mask[a] & 8) != 0) {
                document = true;
            }
            if (photo && video && document) {
                break;
            }
        }
        TLRPC.TL_autoDownloadSettings tL_autoDownloadSettings = req.settings;
        int i = 0;
        if (photo) {
            i = (int) preset.sizes[0];
        }
        tL_autoDownloadSettings.photo_size_max = i;
        long j = 0;
        req.settings.video_size_max = video ? preset.sizes[1] : 0;
        TLRPC.TL_autoDownloadSettings tL_autoDownloadSettings2 = req.settings;
        if (document) {
            j = preset.sizes[2];
        }
        tL_autoDownloadSettings2.file_size_max = j;
        getConnectionsManager().sendRequest(req, DownloadController$$ExternalSyntheticLambda4.INSTANCE);
    }

    static /* synthetic */ void lambda$savePresetToServer$3(TLObject response, TLRPC.TL_error error) {
    }

    /* access modifiers changed from: protected */
    public void cancelDownloading(ArrayList<Pair<Long, Integer>> arrayList) {
        TLRPC.PhotoSize photoSize;
        int N = arrayList.size();
        for (int a = 0; a < N; a++) {
            DownloadObject downloadObject = this.downloadQueuePairs.get(arrayList.get(a));
            if (downloadObject != null) {
                if (downloadObject.object instanceof TLRPC.Document) {
                    getFileLoader().cancelLoadFile((TLRPC.Document) downloadObject.object, true);
                } else if ((downloadObject.object instanceof TLRPC.Photo) && (photoSize = FileLoader.getClosestPhotoSizeWithSize(((TLRPC.Photo) downloadObject.object).sizes, AndroidUtilities.getPhotoSize())) != null) {
                    getFileLoader().cancelLoadFile(photoSize, true);
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public void processDownloadObjects(int type, ArrayList<DownloadObject> objects) {
        ArrayList<DownloadObject> queue;
        String path;
        int cacheType;
        int i = type;
        if (!objects.isEmpty()) {
            if (i == 1) {
                queue = this.photoDownloadQueue;
            } else if (i == 2) {
                queue = this.audioDownloadQueue;
            } else if (i == 4) {
                queue = this.videoDownloadQueue;
            } else {
                queue = this.documentDownloadQueue;
            }
            for (int a = 0; a < objects.size(); a++) {
                DownloadObject downloadObject = objects.get(a);
                TLRPC.PhotoSize photoSize = null;
                if (downloadObject.object instanceof TLRPC.Document) {
                    path = FileLoader.getAttachFileName((TLRPC.Document) downloadObject.object);
                } else if (downloadObject.object instanceof TLRPC.Photo) {
                    photoSize = FileLoader.getClosestPhotoSizeWithSize(((TLRPC.Photo) downloadObject.object).sizes, AndroidUtilities.getPhotoSize());
                    path = FileLoader.getAttachFileName(photoSize);
                } else {
                    path = null;
                }
                if (path != null && !this.downloadQueueKeys.containsKey(path)) {
                    boolean added = true;
                    if (photoSize != null) {
                        TLRPC.Photo photo = (TLRPC.Photo) downloadObject.object;
                        if (downloadObject.secret) {
                            cacheType = 2;
                        } else if (downloadObject.forceCache != 0) {
                            cacheType = 1;
                        } else {
                            cacheType = 0;
                        }
                        getFileLoader().loadFile(ImageLocation.getForPhoto(photoSize, photo), downloadObject.parent, (String) null, 0, cacheType);
                    } else if (downloadObject.object instanceof TLRPC.Document) {
                        getFileLoader().loadFile((TLRPC.Document) downloadObject.object, downloadObject.parent, 0, downloadObject.secret ? 2 : 0);
                    } else {
                        added = false;
                    }
                    if (added) {
                        queue.add(downloadObject);
                        this.downloadQueueKeys.put(path, downloadObject);
                        this.downloadQueuePairs.put(new Pair(Long.valueOf(downloadObject.id), Integer.valueOf(downloadObject.type)), downloadObject);
                    }
                }
            }
            ArrayList<DownloadObject> arrayList = objects;
        }
    }

    /* access modifiers changed from: protected */
    public void newDownloadObjectsAvailable(int downloadMask) {
        int mask = getCurrentDownloadMask();
        if (!((mask & 1) == 0 || (downloadMask & 1) == 0 || !this.photoDownloadQueue.isEmpty())) {
            getMessagesStorage().getDownloadQueue(1);
        }
        if (!((mask & 2) == 0 || (downloadMask & 2) == 0 || !this.audioDownloadQueue.isEmpty())) {
            getMessagesStorage().getDownloadQueue(2);
        }
        if (!((mask & 4) == 0 || (downloadMask & 4) == 0 || !this.videoDownloadQueue.isEmpty())) {
            getMessagesStorage().getDownloadQueue(4);
        }
        if ((mask & 8) != 0 && (downloadMask & 8) != 0 && this.documentDownloadQueue.isEmpty()) {
            getMessagesStorage().getDownloadQueue(8);
        }
    }

    private void checkDownloadFinished(String fileName, int state) {
        DownloadObject downloadObject = this.downloadQueueKeys.get(fileName);
        if (downloadObject != null) {
            this.downloadQueueKeys.remove(fileName);
            this.downloadQueuePairs.remove(new Pair(Long.valueOf(downloadObject.id), Integer.valueOf(downloadObject.type)));
            if (state == 0 || state == 2) {
                getMessagesStorage().removeFromDownloadQueue(downloadObject.id, downloadObject.type, false);
            }
            if (downloadObject.type == 1) {
                this.photoDownloadQueue.remove(downloadObject);
                if (this.photoDownloadQueue.isEmpty()) {
                    newDownloadObjectsAvailable(1);
                }
            } else if (downloadObject.type == 2) {
                this.audioDownloadQueue.remove(downloadObject);
                if (this.audioDownloadQueue.isEmpty()) {
                    newDownloadObjectsAvailable(2);
                }
            } else if (downloadObject.type == 4) {
                this.videoDownloadQueue.remove(downloadObject);
                if (this.videoDownloadQueue.isEmpty()) {
                    newDownloadObjectsAvailable(4);
                }
            } else if (downloadObject.type == 8) {
                this.documentDownloadQueue.remove(downloadObject);
                if (this.documentDownloadQueue.isEmpty()) {
                    newDownloadObjectsAvailable(8);
                }
            }
        }
    }

    public int generateObserverTag() {
        int i = this.lastTag;
        this.lastTag = i + 1;
        return i;
    }

    public void addLoadingFileObserver(String fileName, FileDownloadProgressListener observer) {
        addLoadingFileObserver(fileName, (MessageObject) null, observer);
    }

    public void addLoadingFileObserver(String fileName, MessageObject messageObject, FileDownloadProgressListener observer) {
        if (this.listenerInProgress) {
            this.addLaterArray.put(fileName, observer);
            return;
        }
        removeLoadingFileObserver(observer);
        ArrayList<WeakReference<FileDownloadProgressListener>> arrayList = this.loadingFileObservers.get(fileName);
        if (arrayList == null) {
            arrayList = new ArrayList<>();
            this.loadingFileObservers.put(fileName, arrayList);
        }
        arrayList.add(new WeakReference(observer));
        if (messageObject != null) {
            ArrayList<MessageObject> messageObjects = this.loadingFileMessagesObservers.get(fileName);
            if (messageObjects == null) {
                messageObjects = new ArrayList<>();
                this.loadingFileMessagesObservers.put(fileName, messageObjects);
            }
            messageObjects.add(messageObject);
        }
        this.observersByTag.put(observer.getObserverTag(), fileName);
    }

    public void removeLoadingFileObserver(FileDownloadProgressListener observer) {
        if (this.listenerInProgress) {
            this.deleteLaterArray.add(observer);
            return;
        }
        String fileName = this.observersByTag.get(observer.getObserverTag());
        if (fileName != null) {
            ArrayList<WeakReference<FileDownloadProgressListener>> arrayList = this.loadingFileObservers.get(fileName);
            if (arrayList != null) {
                int a = 0;
                while (a < arrayList.size()) {
                    WeakReference<FileDownloadProgressListener> reference = arrayList.get(a);
                    if (reference.get() == null || reference.get() == observer) {
                        arrayList.remove(a);
                        a--;
                    }
                    a++;
                }
                if (arrayList.isEmpty() != 0) {
                    this.loadingFileObservers.remove(fileName);
                }
            }
            this.observersByTag.remove(observer.getObserverTag());
        }
    }

    private void processLaterArrays() {
        for (Map.Entry<String, FileDownloadProgressListener> listener : this.addLaterArray.entrySet()) {
            addLoadingFileObserver(listener.getKey(), listener.getValue());
        }
        this.addLaterArray.clear();
        Iterator<FileDownloadProgressListener> it = this.deleteLaterArray.iterator();
        while (it.hasNext()) {
            removeLoadingFileObserver(it.next());
        }
        this.deleteLaterArray.clear();
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        int a;
        int i = id;
        if (i == NotificationCenter.fileLoadFailed || i == NotificationCenter.httpFileDidFailedLoad) {
            String fileName = args[0];
            Integer canceled = args[1];
            this.listenerInProgress = true;
            ArrayList<WeakReference<FileDownloadProgressListener>> arrayList = this.loadingFileObservers.get(fileName);
            if (arrayList != null) {
                int size = arrayList.size();
                for (int a2 = 0; a2 < size; a2++) {
                    WeakReference<FileDownloadProgressListener> reference = arrayList.get(a2);
                    if (reference.get() != null) {
                        ((FileDownloadProgressListener) reference.get()).onFailedDownload(fileName, canceled.intValue() == 1);
                        if (canceled.intValue() != 1) {
                            this.observersByTag.remove(((FileDownloadProgressListener) reference.get()).getObserverTag());
                        }
                    }
                }
                if (canceled.intValue() != 1) {
                    this.loadingFileObservers.remove(fileName);
                }
            }
            this.listenerInProgress = false;
            processLaterArrays();
            checkDownloadFinished(fileName, canceled.intValue());
        } else if (i == NotificationCenter.fileLoaded || i == NotificationCenter.httpFileDidLoad) {
            this.listenerInProgress = true;
            String fileName2 = args[0];
            ArrayList<MessageObject> messageObjects = this.loadingFileMessagesObservers.get(fileName2);
            if (messageObjects != null) {
                int size2 = messageObjects.size();
                for (int a3 = 0; a3 < size2; a3++) {
                    messageObjects.get(a3).mediaExists = true;
                }
                this.loadingFileMessagesObservers.remove(fileName2);
            }
            ArrayList<WeakReference<FileDownloadProgressListener>> arrayList2 = this.loadingFileObservers.get(fileName2);
            if (arrayList2 != null) {
                int size3 = arrayList2.size();
                for (int a4 = 0; a4 < size3; a4++) {
                    WeakReference<FileDownloadProgressListener> reference2 = arrayList2.get(a4);
                    if (reference2.get() != null) {
                        ((FileDownloadProgressListener) reference2.get()).onSuccessDownload(fileName2);
                        this.observersByTag.remove(((FileDownloadProgressListener) reference2.get()).getObserverTag());
                    }
                }
                this.loadingFileObservers.remove(fileName2);
            }
            this.listenerInProgress = false;
            processLaterArrays();
            checkDownloadFinished(fileName2, 0);
        } else if (i == NotificationCenter.fileLoadProgressChanged) {
            this.listenerInProgress = true;
            String fileName3 = args[0];
            ArrayList<WeakReference<FileDownloadProgressListener>> arrayList3 = this.loadingFileObservers.get(fileName3);
            if (arrayList3 != null) {
                Long loadedSize = args[1];
                Long totalSize = args[2];
                int size4 = arrayList3.size();
                for (int a5 = 0; a5 < size4; a5++) {
                    WeakReference<FileDownloadProgressListener> reference3 = arrayList3.get(a5);
                    if (reference3.get() != null) {
                        ((FileDownloadProgressListener) reference3.get()).onProgressDownload(fileName3, loadedSize.longValue(), totalSize.longValue());
                    }
                }
            }
            this.listenerInProgress = false;
            processLaterArrays();
        } else if (i == NotificationCenter.fileUploadProgressChanged) {
            this.listenerInProgress = true;
            String fileName4 = args[0];
            ArrayList<WeakReference<FileDownloadProgressListener>> arrayList4 = this.loadingFileObservers.get(fileName4);
            if (arrayList4 != null) {
                Long loadedSize2 = args[1];
                Long totalSize2 = args[2];
                Boolean enc = args[3];
                int size5 = arrayList4.size();
                int a6 = 0;
                while (a6 < size5) {
                    WeakReference<FileDownloadProgressListener> reference4 = arrayList4.get(a6);
                    if (reference4.get() != null) {
                        a = a6;
                        ((FileDownloadProgressListener) reference4.get()).onProgressUpload(fileName4, loadedSize2.longValue(), totalSize2.longValue(), enc.booleanValue());
                    } else {
                        a = a6;
                    }
                    a6 = a + 1;
                }
                int i2 = a6;
            }
            this.listenerInProgress = false;
            processLaterArrays();
            try {
                ArrayList<SendMessagesHelper.DelayedMessage> delayedMessages = getSendMessagesHelper().getDelayedMessages(fileName4);
                if (delayedMessages != null) {
                    for (int a7 = 0; a7 < delayedMessages.size(); a7++) {
                        SendMessagesHelper.DelayedMessage delayedMessage = delayedMessages.get(a7);
                        if (delayedMessage.encryptedChat == null) {
                            long dialogId = delayedMessage.peer;
                            int topMessageId = delayedMessage.topMessageId;
                            Long lastTime = this.typingTimes.get(dialogId);
                            if (delayedMessage.type != 4) {
                                TLRPC.Document document = delayedMessage.obj.getDocument();
                                if (lastTime == null || lastTime.longValue() + 4000 < System.currentTimeMillis()) {
                                    if (delayedMessage.obj.isRoundVideo()) {
                                        getMessagesController().sendTyping(dialogId, topMessageId, 8, 0);
                                    } else if (delayedMessage.obj.isVideo()) {
                                        getMessagesController().sendTyping(dialogId, topMessageId, 5, 0);
                                    } else if (delayedMessage.obj.isVoice()) {
                                        getMessagesController().sendTyping(dialogId, topMessageId, 9, 0);
                                    } else if (delayedMessage.obj.getDocument() != null) {
                                        getMessagesController().sendTyping(dialogId, topMessageId, 3, 0);
                                    } else if (delayedMessage.photoSize != null) {
                                        getMessagesController().sendTyping(dialogId, topMessageId, 4, 0);
                                    }
                                    this.typingTimes.put(dialogId, Long.valueOf(System.currentTimeMillis()));
                                }
                            } else if (lastTime == null || lastTime.longValue() + 4000 < System.currentTimeMillis()) {
                                MessageObject messageObject = (MessageObject) delayedMessage.extraHashMap.get(fileName4 + "_i");
                                if (messageObject != null && messageObject.isVideo()) {
                                    getMessagesController().sendTyping(dialogId, topMessageId, 5, 0);
                                } else if (messageObject == null || messageObject.getDocument() == null) {
                                    getMessagesController().sendTyping(dialogId, topMessageId, 4, 0);
                                } else {
                                    getMessagesController().sendTyping(dialogId, topMessageId, 3, 0);
                                }
                                this.typingTimes.put(dialogId, Long.valueOf(System.currentTimeMillis()));
                            }
                        }
                    }
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    public static float getProgress(long[] progressSizes) {
        if (progressSizes == null || progressSizes.length < 2 || progressSizes[1] == 0) {
            return 0.0f;
        }
        return Math.min(1.0f, ((float) progressSizes[0]) / ((float) progressSizes[1]));
    }

    public void startDownloadFile(TLRPC.Document document, MessageObject parentObject) {
        if (parentObject.getDocument() != null) {
            AndroidUtilities.runOnUIThread(new DownloadController$$ExternalSyntheticLambda13(this, parentObject));
        }
    }

    /* renamed from: lambda$startDownloadFile$5$org-telegram-messenger-DownloadController  reason: not valid java name */
    public /* synthetic */ void m69x71var_eb7(MessageObject parentObject) {
        boolean contains = false;
        int i = 0;
        while (true) {
            if (i < this.recentDownloadingFiles.size()) {
                if (this.recentDownloadingFiles.get(i).getDocument() != null && this.recentDownloadingFiles.get(i).getDocument().id == parentObject.getDocument().id) {
                    contains = true;
                    break;
                }
                i++;
            } else {
                break;
            }
        }
        if (!contains) {
            int i2 = 0;
            while (true) {
                if (i2 < this.downloadingFiles.size()) {
                    if (this.downloadingFiles.get(i2).getDocument() != null && this.downloadingFiles.get(i2).getDocument().id == parentObject.getDocument().id) {
                        contains = true;
                        break;
                    }
                    i2++;
                } else {
                    break;
                }
            }
        }
        if (!contains) {
            this.downloadingFiles.add(parentObject);
            getMessagesStorage().getStorageQueue().postRunnable(new DownloadController$$ExternalSyntheticLambda12(this, parentObject));
        }
        getNotificationCenter().postNotificationName(NotificationCenter.onDownloadingFilesChanged, new Object[0]);
    }

    /* renamed from: lambda$startDownloadFile$4$org-telegram-messenger-DownloadController  reason: not valid java name */
    public /* synthetic */ void m68x8cb4eff6(MessageObject parentObject) {
        try {
            NativeByteBuffer data = new NativeByteBuffer(parentObject.messageOwner.getObjectSize());
            parentObject.messageOwner.serializeToStream(data);
            SQLitePreparedStatement state = getMessagesStorage().getDatabase().executeFast("REPLACE INTO downloading_documents VALUES(?, ?, ?, ?, ?)");
            state.bindByteBuffer(1, data);
            state.bindInteger(2, parentObject.getDocument().dc_id);
            state.bindLong(3, parentObject.getDocument().id);
            state.bindLong(4, System.currentTimeMillis());
            state.bindInteger(4, 0);
            state.step();
            state.dispose();
            data.reuse();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public void onDownloadComplete(MessageObject parentObject) {
        if (parentObject != null) {
            AndroidUtilities.runOnUIThread(new DownloadController$$ExternalSyntheticLambda10(this, parentObject));
        }
    }

    /* renamed from: lambda$onDownloadComplete$7$org-telegram-messenger-DownloadController  reason: not valid java name */
    public /* synthetic */ void m65x872e680d(MessageObject parentObject) {
        boolean removed = false;
        int i = 0;
        while (true) {
            if (i >= this.downloadingFiles.size()) {
                break;
            } else if (this.downloadingFiles.get(i).getDocument().id == parentObject.getDocument().id) {
                this.downloadingFiles.remove(i);
                removed = true;
                break;
            } else {
                i++;
            }
        }
        if (removed) {
            boolean contains = false;
            int i2 = 0;
            while (true) {
                if (i2 >= this.recentDownloadingFiles.size()) {
                    break;
                } else if (this.recentDownloadingFiles.get(i2).getDocument().id == parentObject.getDocument().id) {
                    contains = true;
                    break;
                } else {
                    i2++;
                }
            }
            if (!contains) {
                this.recentDownloadingFiles.add(0, parentObject);
                putToUnviewedDownloads(parentObject);
            }
            getNotificationCenter().postNotificationName(NotificationCenter.onDownloadingFilesChanged, new Object[0]);
            getMessagesStorage().getStorageQueue().postRunnable(new DownloadController$$ExternalSyntheticLambda9(this, parentObject));
        }
    }

    /* renamed from: lambda$onDownloadComplete$6$org-telegram-messenger-DownloadController  reason: not valid java name */
    public /* synthetic */ void m64xa1ecvar_c(MessageObject parentObject) {
        try {
            getMessagesStorage().getDatabase().executeFast(String.format(Locale.ENGLISH, "UPDATE downloading_documents SET state = 1, date = %d WHERE hash = %d AND id = %d", new Object[]{Long.valueOf(System.currentTimeMillis()), Integer.valueOf(parentObject.getDocument().dc_id), Long.valueOf(parentObject.getDocument().id)})).stepThis().dispose();
            SQLiteCursor cursor = getMessagesStorage().getDatabase().queryFinalized("SELECT COUNT(*) FROM downloading_documents WHERE state = 1", new Object[0]);
            int count = 0;
            if (cursor.next()) {
                count = cursor.intValue(0);
            }
            cursor.dispose();
            SQLiteCursor cursor2 = getMessagesStorage().getDatabase().queryFinalized("SELECT state FROM downloading_documents WHERE state = 1", new Object[0]);
            if (cursor2.next()) {
                cursor2.intValue(0);
            }
            cursor2.dispose();
            if (count > 100) {
                SQLiteDatabase database = getMessagesStorage().getDatabase();
                SQLiteCursor cursor3 = database.queryFinalized("SELECT hash, id FROM downloading_documents WHERE state = 1 ORDER BY date ASC LIMIT " + (100 - count), new Object[0]);
                ArrayList<DownloadingDocumentEntry> entriesToRemove = new ArrayList<>();
                while (cursor3.next()) {
                    DownloadingDocumentEntry entry = new DownloadingDocumentEntry();
                    entry.hash = cursor3.intValue(0);
                    entry.id = cursor3.longValue(1);
                    entriesToRemove.add(entry);
                }
                cursor3.dispose();
                SQLitePreparedStatement state = getMessagesStorage().getDatabase().executeFast("DELETE FROM downloading_documents WHERE hash = ? AND id = ?");
                for (int i = 0; i < entriesToRemove.size(); i++) {
                    state.requery();
                    state.bindInteger(1, entriesToRemove.get(i).hash);
                    state.bindLong(2, entriesToRemove.get(i).id);
                    state.step();
                }
                state.dispose();
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public void onDownloadFail(MessageObject parentObject, int reason) {
        if (parentObject != null) {
            AndroidUtilities.runOnUIThread(new DownloadController$$ExternalSyntheticLambda1(this, parentObject, reason));
            getMessagesStorage().getStorageQueue().postRunnable(new DownloadController$$ExternalSyntheticLambda11(this, parentObject));
        }
    }

    /* renamed from: lambda$onDownloadFail$8$org-telegram-messenger-DownloadController  reason: not valid java name */
    public /* synthetic */ void m66x867eb33(MessageObject parentObject, int reason) {
        boolean removed = false;
        int i = 0;
        while (true) {
            if (i >= this.downloadingFiles.size()) {
                break;
            } else if (this.downloadingFiles.get(i).getDocument().id == parentObject.getDocument().id) {
                this.downloadingFiles.remove(i);
                removed = true;
                break;
            } else {
                i++;
            }
        }
        if (removed) {
            getNotificationCenter().postNotificationName(NotificationCenter.onDownloadingFilesChanged, new Object[0]);
            if (reason == 0) {
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.showBulletin, 1, LocaleController.formatString("MessageNotFound", NUM, new Object[0]));
            }
        }
    }

    /* renamed from: lambda$onDownloadFail$9$org-telegram-messenger-DownloadController  reason: not valid java name */
    public /* synthetic */ void m67xeda959f4(MessageObject parentObject) {
        try {
            SQLitePreparedStatement state = getMessagesStorage().getDatabase().executeFast("DELETE FROM downloading_documents WHERE hash = ? AND id = ?");
            state.bindInteger(1, parentObject.getDocument().dc_id);
            state.bindLong(2, parentObject.getDocument().id);
            state.step();
            state.dispose();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    private void putToUnviewedDownloads(MessageObject parentObject) {
        this.unviewedDownloads.put(parentObject.getId(), parentObject);
        AndroidUtilities.cancelRunOnUIThread(this.clearUnviewedDownloadsRunnale);
        AndroidUtilities.runOnUIThread(this.clearUnviewedDownloadsRunnale, 60000);
    }

    public void clearUnviewedDownloads() {
        this.unviewedDownloads.clear();
    }

    public void checkUnviewedDownloads(int messageId, long dialogId) {
        MessageObject messageObject = this.unviewedDownloads.get(messageId);
        if (messageObject != null && messageObject.getDialogId() == dialogId) {
            this.unviewedDownloads.remove(messageId);
            if (this.unviewedDownloads.size() == 0) {
                getNotificationCenter().postNotificationName(NotificationCenter.onDownloadingFilesChanged, new Object[0]);
            }
        }
    }

    public boolean hasUnviewedDownloads() {
        return this.unviewedDownloads.size() > 0;
    }

    private class DownloadingDocumentEntry {
        int hash;
        long id;

        private DownloadingDocumentEntry() {
        }
    }

    public void loadDownloadingFiles() {
        getMessagesStorage().getStorageQueue().postRunnable(new DownloadController$$ExternalSyntheticLambda5(this));
    }

    /* renamed from: lambda$loadDownloadingFiles$11$org-telegram-messenger-DownloadController  reason: not valid java name */
    public /* synthetic */ void m62x5c0evar_() {
        ArrayList<MessageObject> downloadingMessages = new ArrayList<>();
        ArrayList<MessageObject> recentlyDownloadedMessages = new ArrayList<>();
        ArrayList<MessageObject> newMessages = new ArrayList<>();
        try {
            SQLiteCursor cursor2 = getMessagesStorage().getDatabase().queryFinalized("SELECT data, state FROM downloading_documents ORDER BY date DESC", new Object[0]);
            while (cursor2.next()) {
                NativeByteBuffer data = cursor2.byteBufferValue(0);
                int state = cursor2.intValue(1);
                if (data != null) {
                    TLRPC.Message message = TLRPC.Message.TLdeserialize(data, data.readInt32(false), false);
                    if (message != null) {
                        message.readAttachPath(data, UserConfig.getInstance(this.currentAccount).clientUserId);
                        MessageObject messageObject = new MessageObject(this.currentAccount, message, false, false);
                        newMessages.add(messageObject);
                        if (state == 0) {
                            downloadingMessages.add(messageObject);
                        } else {
                            recentlyDownloadedMessages.add(messageObject);
                        }
                    }
                    data.reuse();
                }
            }
            cursor2.dispose();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        getFileLoader().checkMediaExistance(downloadingMessages);
        getFileLoader().checkMediaExistance(recentlyDownloadedMessages);
        AndroidUtilities.runOnUIThread(new DownloadController$$ExternalSyntheticLambda8(this, downloadingMessages, recentlyDownloadedMessages));
    }

    /* renamed from: lambda$loadDownloadingFiles$10$org-telegram-messenger-DownloadController  reason: not valid java name */
    public /* synthetic */ void m61x207var_(ArrayList downloadingMessages, ArrayList recentlyDownloadedMessages) {
        this.downloadingFiles.clear();
        this.downloadingFiles.addAll(downloadingMessages);
        this.recentDownloadingFiles.clear();
        this.recentDownloadingFiles.addAll(recentlyDownloadedMessages);
    }

    public void clearRecentDownloadedFiles() {
        this.recentDownloadingFiles.clear();
        getNotificationCenter().postNotificationName(NotificationCenter.onDownloadingFilesChanged, new Object[0]);
        getMessagesStorage().getStorageQueue().postRunnable(new DownloadController$$ExternalSyntheticLambda0(this));
    }

    /* renamed from: lambda$clearRecentDownloadedFiles$12$org-telegram-messenger-DownloadController  reason: not valid java name */
    public /* synthetic */ void m57x5e3299d() {
        try {
            getMessagesStorage().getDatabase().executeFast("DELETE FROM downloading_documents WHERE state = 1").stepThis().dispose();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public void deleteRecentFiles(ArrayList<MessageObject> messageObjects) {
        for (int i = 0; i < messageObjects.size(); i++) {
            boolean found = false;
            int j = 0;
            while (true) {
                if (j < this.recentDownloadingFiles.size()) {
                    if (messageObjects.get(i).getId() == this.recentDownloadingFiles.get(j).getId() && this.recentDownloadingFiles.get(j).getDialogId() == messageObjects.get(i).getDialogId()) {
                        this.recentDownloadingFiles.remove(j);
                        found = true;
                        break;
                    }
                    j++;
                } else {
                    break;
                }
            }
            if (!found) {
                int j2 = 0;
                while (true) {
                    if (j2 < this.downloadingFiles.size()) {
                        if (messageObjects.get(i).getId() == this.downloadingFiles.get(j2).getId() && this.downloadingFiles.get(j2).getDialogId() == messageObjects.get(i).getDialogId()) {
                            this.downloadingFiles.remove(j2);
                            break;
                        }
                        j2++;
                    } else {
                        break;
                    }
                }
            }
            messageObjects.get(i).putInDownloadsStore = false;
            FileLoader.getInstance(this.currentAccount).loadFile(messageObjects.get(i).getDocument(), messageObjects.get(i), 0, 0);
            FileLoader.getInstance(this.currentAccount).cancelLoadFile(messageObjects.get(i).getDocument(), true);
        }
        getNotificationCenter().postNotificationName(NotificationCenter.onDownloadingFilesChanged, new Object[0]);
        getMessagesStorage().getStorageQueue().postRunnable(new DownloadController$$ExternalSyntheticLambda7(this, messageObjects));
    }

    /* renamed from: lambda$deleteRecentFiles$13$org-telegram-messenger-DownloadController  reason: not valid java name */
    public /* synthetic */ void m58x7bb1CLASSNAMEf(ArrayList messageObjects) {
        try {
            SQLitePreparedStatement state = getMessagesStorage().getDatabase().executeFast("DELETE FROM downloading_documents WHERE hash = ? AND id = ?");
            for (int i = 0; i < messageObjects.size(); i++) {
                state.requery();
                state.bindInteger(1, ((MessageObject) messageObjects.get(i)).getDocument().dc_id);
                state.bindLong(2, ((MessageObject) messageObjects.get(i)).getDocument().id);
                state.step();
                try {
                    FileLoader.getInstance(this.currentAccount).getPathToMessage(((MessageObject) messageObjects.get(i)).messageOwner).delete();
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
            }
            state.dispose();
        } catch (Exception e2) {
            FileLog.e((Throwable) e2);
        }
    }
}
