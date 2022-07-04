package org.telegram.messenger;

import android.content.SharedPreferences;
import android.util.SparseArray;
import android.util.SparseIntArray;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public class FileUploadOperation {
    private static final int initialRequestsCount = 8;
    private static final int initialRequestsSlowNetworkCount = 1;
    private static final int maxUploadingKBytes = 2048;
    private static final int maxUploadingSlowNetworkKBytes = 32;
    private static final int minUploadChunkSize = 128;
    private static final int minUploadChunkSlowNetworkSize = 32;
    private long availableSize;
    private SparseArray<UploadCachedResult> cachedResults = new SparseArray<>();
    private int currentAccount;
    private long currentFileId;
    private int currentPartNum;
    private int currentType;
    private int currentUploadRequetsCount;
    private FileUploadOperationDelegate delegate;
    private long estimatedSize;
    private String fileKey;
    private int fingerprint;
    private boolean forceSmallFile;
    private ArrayList<byte[]> freeRequestIvs;
    private boolean isBigFile;
    private boolean isEncrypted;
    private boolean isLastPart;
    private byte[] iv;
    private byte[] ivChange;
    private byte[] key;
    protected long lastProgressUpdateTime;
    private int lastSavedPartNum;
    private int maxRequestsCount;
    private boolean nextPartFirst;
    private int operationGuid;
    private SharedPreferences preferences;
    private byte[] readBuffer;
    private long readBytesCount;
    private int requestNum;
    private SparseIntArray requestTokens = new SparseIntArray();
    private int saveInfoTimes;
    private boolean slowNetwork;
    private boolean started;
    private int state;
    private RandomAccessFile stream;
    private long totalFileSize;
    private int totalPartsCount;
    private int uploadChunkSize = 65536;
    private boolean uploadFirstPartLater;
    private int uploadStartTime;
    private long uploadedBytesCount;
    private String uploadingFilePath;

    public interface FileUploadOperationDelegate {
        void didChangedUploadProgress(FileUploadOperation fileUploadOperation, long j, long j2);

        void didFailedUploadingFile(FileUploadOperation fileUploadOperation);

        void didFinishUploadingFile(FileUploadOperation fileUploadOperation, TLRPC.InputFile inputFile, TLRPC.InputEncryptedFile inputEncryptedFile, byte[] bArr, byte[] bArr2);
    }

    private static class UploadCachedResult {
        /* access modifiers changed from: private */
        public long bytesOffset;
        /* access modifiers changed from: private */
        public byte[] iv;

        private UploadCachedResult() {
        }
    }

    public FileUploadOperation(int instance, String location, boolean encrypted, long estimated, int type) {
        this.currentAccount = instance;
        this.uploadingFilePath = location;
        this.isEncrypted = encrypted;
        this.estimatedSize = estimated;
        this.currentType = type;
        this.uploadFirstPartLater = estimated != 0 && !encrypted;
    }

    public long getTotalFileSize() {
        return this.totalFileSize;
    }

    public void setDelegate(FileUploadOperationDelegate fileUploadOperationDelegate) {
        this.delegate = fileUploadOperationDelegate;
    }

    public void start() {
        if (this.state == 0) {
            this.state = 1;
            Utilities.stageQueue.postRunnable(new FileUploadOperation$$ExternalSyntheticLambda1(this));
        }
    }

    /* renamed from: lambda$start$0$org-telegram-messenger-FileUploadOperation  reason: not valid java name */
    public /* synthetic */ void m1870lambda$start$0$orgtelegrammessengerFileUploadOperation() {
        this.preferences = ApplicationLoader.applicationContext.getSharedPreferences("uploadinfo", 0);
        this.slowNetwork = ApplicationLoader.isConnectionSlow();
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("start upload on slow network = " + this.slowNetwork);
        }
        int count = this.slowNetwork ? 1 : 8;
        for (int a = 0; a < count; a++) {
            startUploadRequest();
        }
    }

    /* access modifiers changed from: protected */
    public void onNetworkChanged(boolean slow) {
        if (this.state == 1) {
            Utilities.stageQueue.postRunnable(new FileUploadOperation$$ExternalSyntheticLambda4(this, slow));
        }
    }

    /* renamed from: lambda$onNetworkChanged$1$org-telegram-messenger-FileUploadOperation  reason: not valid java name */
    public /* synthetic */ void m1869x2bbbbcc2(boolean slow) {
        int i;
        if (this.slowNetwork != slow) {
            this.slowNetwork = slow;
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("network changed to slow = " + this.slowNetwork);
            }
            int a = 0;
            while (true) {
                i = 1;
                if (a >= this.requestTokens.size()) {
                    break;
                }
                ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.requestTokens.valueAt(a), true);
                a++;
            }
            this.requestTokens.clear();
            cleanup();
            this.isLastPart = false;
            this.nextPartFirst = false;
            this.requestNum = 0;
            this.currentPartNum = 0;
            this.readBytesCount = 0;
            this.uploadedBytesCount = 0;
            this.saveInfoTimes = 0;
            this.key = null;
            this.iv = null;
            this.ivChange = null;
            this.currentUploadRequetsCount = 0;
            this.lastSavedPartNum = 0;
            this.uploadFirstPartLater = false;
            this.cachedResults.clear();
            this.operationGuid++;
            if (!this.slowNetwork) {
                i = 8;
            }
            int count = i;
            for (int a2 = 0; a2 < count; a2++) {
                startUploadRequest();
            }
        }
    }

    public void cancel() {
        if (this.state != 3) {
            this.state = 2;
            Utilities.stageQueue.postRunnable(new FileUploadOperation$$ExternalSyntheticLambda0(this));
            this.delegate.didFailedUploadingFile(this);
            cleanup();
        }
    }

    /* renamed from: lambda$cancel$2$org-telegram-messenger-FileUploadOperation  reason: not valid java name */
    public /* synthetic */ void m1867lambda$cancel$2$orgtelegrammessengerFileUploadOperation() {
        for (int a = 0; a < this.requestTokens.size(); a++) {
            ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.requestTokens.valueAt(a), true);
        }
    }

    private void cleanup() {
        if (this.preferences == null) {
            this.preferences = ApplicationLoader.applicationContext.getSharedPreferences("uploadinfo", 0);
        }
        SharedPreferences.Editor edit = this.preferences.edit();
        SharedPreferences.Editor remove = edit.remove(this.fileKey + "_time");
        SharedPreferences.Editor remove2 = remove.remove(this.fileKey + "_size");
        SharedPreferences.Editor remove3 = remove2.remove(this.fileKey + "_uploaded");
        SharedPreferences.Editor remove4 = remove3.remove(this.fileKey + "_id");
        SharedPreferences.Editor remove5 = remove4.remove(this.fileKey + "_iv");
        SharedPreferences.Editor remove6 = remove5.remove(this.fileKey + "_key");
        remove6.remove(this.fileKey + "_ivc").commit();
        try {
            RandomAccessFile randomAccessFile = this.stream;
            if (randomAccessFile != null) {
                randomAccessFile.close();
                this.stream = null;
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    /* access modifiers changed from: protected */
    public void checkNewDataAvailable(long newAvailableSize, long finalSize) {
        Utilities.stageQueue.postRunnable(new FileUploadOperation$$ExternalSyntheticLambda3(this, finalSize, newAvailableSize));
    }

    /* renamed from: lambda$checkNewDataAvailable$3$org-telegram-messenger-FileUploadOperation  reason: not valid java name */
    public /* synthetic */ void m1868x53f1b98(long finalSize, long newAvailableSize) {
        if (!(this.estimatedSize == 0 || finalSize == 0)) {
            this.estimatedSize = 0;
            this.totalFileSize = finalSize;
            calcTotalPartsCount();
            if (!this.uploadFirstPartLater && this.started) {
                storeFileUploadInfo();
            }
        }
        this.availableSize = finalSize > 0 ? finalSize : newAvailableSize;
        if (this.currentUploadRequetsCount < this.maxRequestsCount) {
            startUploadRequest();
        }
    }

    private void storeFileUploadInfo() {
        SharedPreferences.Editor editor = this.preferences.edit();
        editor.putInt(this.fileKey + "_time", this.uploadStartTime);
        editor.putLong(this.fileKey + "_size", this.totalFileSize);
        editor.putLong(this.fileKey + "_id", this.currentFileId);
        editor.remove(this.fileKey + "_uploaded");
        if (this.isEncrypted) {
            editor.putString(this.fileKey + "_iv", Utilities.bytesToHex(this.iv));
            editor.putString(this.fileKey + "_ivc", Utilities.bytesToHex(this.ivChange));
            editor.putString(this.fileKey + "_key", Utilities.bytesToHex(this.key));
        }
        editor.commit();
    }

    private void calcTotalPartsCount() {
        if (!this.uploadFirstPartLater) {
            long j = this.totalFileSize;
            int i = this.uploadChunkSize;
            this.totalPartsCount = (int) (((j + ((long) i)) - 1) / ((long) i));
        } else if (this.isBigFile) {
            long j2 = this.totalFileSize;
            int i2 = this.uploadChunkSize;
            this.totalPartsCount = ((int) ((((j2 - ((long) i2)) + ((long) i2)) - 1) / ((long) i2))) + 1;
        } else {
            int i3 = this.uploadChunkSize;
            this.totalPartsCount = ((int) ((((this.totalFileSize - 1024) + ((long) i3)) - 1) / ((long) i3))) + 1;
        }
    }

    public void setForceSmallFile() {
        this.forceSmallFile = true;
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v9, resolved type: org.telegram.tgnet.TLRPC$TL_upload_saveBigFilePart} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v27, resolved type: org.telegram.tgnet.TLRPC$TL_upload_saveFilePart} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void startUploadRequest() {
        /*
            r38 = this;
            r11 = r38
            int r0 = r11.state
            r1 = 1
            if (r0 == r1) goto L_0x0008
            return
        L_0x0008:
            r11.started = r1     // Catch:{ Exception -> 0x0583 }
            java.io.RandomAccessFile r0 = r11.stream     // Catch:{ Exception -> 0x0583 }
            r2 = 1024(0x400, float:1.435E-42)
            r3 = 0
            r5 = 32
            r13 = 0
            if (r0 != 0) goto L_0x03ef
            java.io.File r0 = new java.io.File     // Catch:{ Exception -> 0x0583 }
            java.lang.String r6 = r11.uploadingFilePath     // Catch:{ Exception -> 0x0583 }
            r0.<init>(r6)     // Catch:{ Exception -> 0x0583 }
            r6 = r0
            android.net.Uri r0 = android.net.Uri.fromFile(r6)     // Catch:{ Exception -> 0x0583 }
            boolean r0 = org.telegram.messenger.AndroidUtilities.isInternalUri((android.net.Uri) r0)     // Catch:{ Exception -> 0x0583 }
            java.lang.String r7 = "trying to upload internal file"
            if (r0 != 0) goto L_0x03e7
            java.io.RandomAccessFile r0 = new java.io.RandomAccessFile     // Catch:{ Exception -> 0x0583 }
            java.lang.String r8 = "r"
            r0.<init>(r6, r8)     // Catch:{ Exception -> 0x0583 }
            r11.stream = r0     // Catch:{ Exception -> 0x0583 }
            r8 = 0
            java.lang.Class<java.io.FileDescriptor> r0 = java.io.FileDescriptor.class
            java.lang.String r9 = "getInt$"
            java.lang.Class[] r10 = new java.lang.Class[r13]     // Catch:{ all -> 0x0057 }
            java.lang.reflect.Method r0 = r0.getDeclaredMethod(r9, r10)     // Catch:{ all -> 0x0057 }
            java.io.RandomAccessFile r9 = r11.stream     // Catch:{ all -> 0x0057 }
            java.io.FileDescriptor r9 = r9.getFD()     // Catch:{ all -> 0x0057 }
            java.lang.Object[] r10 = new java.lang.Object[r13]     // Catch:{ all -> 0x0057 }
            java.lang.Object r9 = r0.invoke(r9, r10)     // Catch:{ all -> 0x0057 }
            java.lang.Integer r9 = (java.lang.Integer) r9     // Catch:{ all -> 0x0057 }
            int r9 = r9.intValue()     // Catch:{ all -> 0x0057 }
            boolean r10 = org.telegram.messenger.AndroidUtilities.isInternalUri((int) r9)     // Catch:{ all -> 0x0057 }
            if (r10 == 0) goto L_0x0056
            r8 = 1
        L_0x0056:
            goto L_0x005b
        L_0x0057:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ Exception -> 0x0583 }
        L_0x005b:
            if (r8 != 0) goto L_0x03de
            long r9 = r11.estimatedSize     // Catch:{ Exception -> 0x0583 }
            int r0 = (r9 > r3 ? 1 : (r9 == r3 ? 0 : -1))
            if (r0 == 0) goto L_0x0066
            r11.totalFileSize = r9     // Catch:{ Exception -> 0x0583 }
            goto L_0x006c
        L_0x0066:
            long r9 = r6.length()     // Catch:{ Exception -> 0x0583 }
            r11.totalFileSize = r9     // Catch:{ Exception -> 0x0583 }
        L_0x006c:
            boolean r0 = r11.forceSmallFile     // Catch:{ Exception -> 0x0583 }
            if (r0 != 0) goto L_0x007b
            long r9 = r11.totalFileSize     // Catch:{ Exception -> 0x0583 }
            r14 = 10485760(0xa00000, double:5.180654E-317)
            int r0 = (r9 > r14 ? 1 : (r9 == r14 ? 0 : -1))
            if (r0 <= 0) goto L_0x007b
            r11.isBigFile = r1     // Catch:{ Exception -> 0x0583 }
        L_0x007b:
            int r0 = r11.currentAccount     // Catch:{ Exception -> 0x0583 }
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)     // Catch:{ Exception -> 0x0583 }
            int r0 = r0.uploadMaxFileParts     // Catch:{ Exception -> 0x0583 }
            long r9 = (long) r0     // Catch:{ Exception -> 0x0583 }
            int r0 = r11.currentAccount     // Catch:{ Exception -> 0x0583 }
            org.telegram.messenger.AccountInstance r0 = org.telegram.messenger.AccountInstance.getInstance(r0)     // Catch:{ Exception -> 0x0583 }
            org.telegram.messenger.UserConfig r0 = r0.getUserConfig()     // Catch:{ Exception -> 0x0583 }
            boolean r0 = r0.isPremium()     // Catch:{ Exception -> 0x0583 }
            if (r0 == 0) goto L_0x00a6
            long r14 = r11.totalFileSize     // Catch:{ Exception -> 0x0583 }
            r16 = 2097152000(0x7d000000, double:1.0361307573E-314)
            int r0 = (r14 > r16 ? 1 : (r14 == r16 ? 0 : -1))
            if (r0 <= 0) goto L_0x00a6
            int r0 = r11.currentAccount     // Catch:{ Exception -> 0x0583 }
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)     // Catch:{ Exception -> 0x0583 }
            int r0 = r0.uploadMaxFilePartsPremium     // Catch:{ Exception -> 0x0583 }
            long r9 = (long) r0     // Catch:{ Exception -> 0x0583 }
        L_0x00a6:
            boolean r0 = r11.slowNetwork     // Catch:{ Exception -> 0x0583 }
            if (r0 == 0) goto L_0x00ad
            r14 = 32
            goto L_0x00af
        L_0x00ad:
            r14 = 128(0x80, double:6.32E-322)
        L_0x00af:
            long r12 = r11.totalFileSize     // Catch:{ Exception -> 0x0583 }
            r3 = 1024(0x400, double:5.06E-321)
            long r20 = r9 * r3
            long r12 = r12 + r20
            r20 = 1
            long r12 = r12 - r20
            long r20 = r9 * r3
            long r12 = r12 / r20
            long r12 = java.lang.Math.max(r14, r12)     // Catch:{ Exception -> 0x0583 }
            int r0 = (int) r12     // Catch:{ Exception -> 0x0583 }
            r11.uploadChunkSize = r0     // Catch:{ Exception -> 0x0583 }
            int r0 = r2 % r0
            if (r0 == 0) goto L_0x00d5
            r0 = 64
        L_0x00cc:
            int r7 = r11.uploadChunkSize     // Catch:{ Exception -> 0x0583 }
            if (r7 <= r0) goto L_0x00d3
            int r0 = r0 * 2
            goto L_0x00cc
        L_0x00d3:
            r11.uploadChunkSize = r0     // Catch:{ Exception -> 0x0583 }
        L_0x00d5:
            boolean r0 = r11.slowNetwork     // Catch:{ Exception -> 0x0583 }
            if (r0 == 0) goto L_0x00dc
            r0 = 32
            goto L_0x00de
        L_0x00dc:
            r0 = 2048(0x800, float:2.87E-42)
        L_0x00de:
            int r7 = r11.uploadChunkSize     // Catch:{ Exception -> 0x0583 }
            int r0 = r0 / r7
            int r0 = java.lang.Math.max(r1, r0)     // Catch:{ Exception -> 0x0583 }
            r11.maxRequestsCount = r0     // Catch:{ Exception -> 0x0583 }
            boolean r0 = r11.isEncrypted     // Catch:{ Exception -> 0x0583 }
            if (r0 == 0) goto L_0x0103
            java.util.ArrayList r0 = new java.util.ArrayList     // Catch:{ Exception -> 0x0583 }
            int r7 = r11.maxRequestsCount     // Catch:{ Exception -> 0x0583 }
            r0.<init>(r7)     // Catch:{ Exception -> 0x0583 }
            r11.freeRequestIvs = r0     // Catch:{ Exception -> 0x0583 }
            r0 = 0
        L_0x00f5:
            int r7 = r11.maxRequestsCount     // Catch:{ Exception -> 0x0583 }
            if (r0 >= r7) goto L_0x0103
            java.util.ArrayList<byte[]> r7 = r11.freeRequestIvs     // Catch:{ Exception -> 0x0583 }
            byte[] r12 = new byte[r5]     // Catch:{ Exception -> 0x0583 }
            r7.add(r12)     // Catch:{ Exception -> 0x0583 }
            int r0 = r0 + 1
            goto L_0x00f5
        L_0x0103:
            int r0 = r11.uploadChunkSize     // Catch:{ Exception -> 0x0583 }
            int r0 = r0 * 1024
            r11.uploadChunkSize = r0     // Catch:{ Exception -> 0x0583 }
            r38.calcTotalPartsCount()     // Catch:{ Exception -> 0x0583 }
            int r0 = r11.uploadChunkSize     // Catch:{ Exception -> 0x0583 }
            byte[] r0 = new byte[r0]     // Catch:{ Exception -> 0x0583 }
            r11.readBuffer = r0     // Catch:{ Exception -> 0x0583 }
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0583 }
            r0.<init>()     // Catch:{ Exception -> 0x0583 }
            java.lang.String r7 = r11.uploadingFilePath     // Catch:{ Exception -> 0x0583 }
            r0.append(r7)     // Catch:{ Exception -> 0x0583 }
            boolean r7 = r11.isEncrypted     // Catch:{ Exception -> 0x0583 }
            if (r7 == 0) goto L_0x0123
            java.lang.String r7 = "enc"
            goto L_0x0125
        L_0x0123:
            java.lang.String r7 = ""
        L_0x0125:
            r0.append(r7)     // Catch:{ Exception -> 0x0583 }
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x0583 }
            java.lang.String r0 = org.telegram.messenger.Utilities.MD5(r0)     // Catch:{ Exception -> 0x0583 }
            r11.fileKey = r0     // Catch:{ Exception -> 0x0583 }
            android.content.SharedPreferences r0 = r11.preferences     // Catch:{ Exception -> 0x0583 }
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0583 }
            r7.<init>()     // Catch:{ Exception -> 0x0583 }
            java.lang.String r12 = r11.fileKey     // Catch:{ Exception -> 0x0583 }
            r7.append(r12)     // Catch:{ Exception -> 0x0583 }
            java.lang.String r12 = "_size"
            r7.append(r12)     // Catch:{ Exception -> 0x0583 }
            java.lang.String r7 = r7.toString()     // Catch:{ Exception -> 0x0583 }
            r12 = 0
            long r14 = r0.getLong(r7, r12)     // Catch:{ Exception -> 0x0583 }
            r12 = r14
            long r14 = java.lang.System.currentTimeMillis()     // Catch:{ Exception -> 0x0583 }
            r20 = 1000(0x3e8, double:4.94E-321)
            long r14 = r14 / r20
            int r0 = (int) r14     // Catch:{ Exception -> 0x0583 }
            r11.uploadStartTime = r0     // Catch:{ Exception -> 0x0583 }
            r0 = 0
            boolean r7 = r11.uploadFirstPartLater     // Catch:{ Exception -> 0x0583 }
            if (r7 != 0) goto L_0x032b
            boolean r7 = r11.nextPartFirst     // Catch:{ Exception -> 0x0583 }
            if (r7 != 0) goto L_0x032b
            long r14 = r11.estimatedSize     // Catch:{ Exception -> 0x0583 }
            r18 = 0
            int r7 = (r14 > r18 ? 1 : (r14 == r18 ? 0 : -1))
            if (r7 != 0) goto L_0x032b
            long r14 = r11.totalFileSize     // Catch:{ Exception -> 0x0583 }
            int r7 = (r12 > r14 ? 1 : (r12 == r14 ? 0 : -1))
            if (r7 != 0) goto L_0x032b
            android.content.SharedPreferences r7 = r11.preferences     // Catch:{ Exception -> 0x0583 }
            java.lang.StringBuilder r14 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0583 }
            r14.<init>()     // Catch:{ Exception -> 0x0583 }
            java.lang.String r15 = r11.fileKey     // Catch:{ Exception -> 0x0583 }
            r14.append(r15)     // Catch:{ Exception -> 0x0583 }
            java.lang.String r15 = "_id"
            r14.append(r15)     // Catch:{ Exception -> 0x0583 }
            java.lang.String r14 = r14.toString()     // Catch:{ Exception -> 0x0583 }
            r22 = r6
            r2 = 0
            long r5 = r7.getLong(r14, r2)     // Catch:{ Exception -> 0x0583 }
            r11.currentFileId = r5     // Catch:{ Exception -> 0x0583 }
            android.content.SharedPreferences r2 = r11.preferences     // Catch:{ Exception -> 0x0583 }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0583 }
            r3.<init>()     // Catch:{ Exception -> 0x0583 }
            java.lang.String r5 = r11.fileKey     // Catch:{ Exception -> 0x0583 }
            r3.append(r5)     // Catch:{ Exception -> 0x0583 }
            java.lang.String r5 = "_time"
            r3.append(r5)     // Catch:{ Exception -> 0x0583 }
            java.lang.String r3 = r3.toString()     // Catch:{ Exception -> 0x0583 }
            r5 = 0
            int r2 = r2.getInt(r3, r5)     // Catch:{ Exception -> 0x0583 }
            android.content.SharedPreferences r3 = r11.preferences     // Catch:{ Exception -> 0x0583 }
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0583 }
            r5.<init>()     // Catch:{ Exception -> 0x0583 }
            java.lang.String r6 = r11.fileKey     // Catch:{ Exception -> 0x0583 }
            r5.append(r6)     // Catch:{ Exception -> 0x0583 }
            java.lang.String r6 = "_uploaded"
            r5.append(r6)     // Catch:{ Exception -> 0x0583 }
            java.lang.String r5 = r5.toString()     // Catch:{ Exception -> 0x0583 }
            r6 = 0
            long r23 = r3.getLong(r5, r6)     // Catch:{ Exception -> 0x0583 }
            r5 = r23
            boolean r3 = r11.isEncrypted     // Catch:{ Exception -> 0x0583 }
            r7 = 0
            if (r3 == 0) goto L_0x0228
            android.content.SharedPreferences r3 = r11.preferences     // Catch:{ Exception -> 0x0583 }
            java.lang.StringBuilder r14 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0583 }
            r14.<init>()     // Catch:{ Exception -> 0x0583 }
            java.lang.String r4 = r11.fileKey     // Catch:{ Exception -> 0x0583 }
            r14.append(r4)     // Catch:{ Exception -> 0x0583 }
            java.lang.String r4 = "_iv"
            r14.append(r4)     // Catch:{ Exception -> 0x0583 }
            java.lang.String r4 = r14.toString()     // Catch:{ Exception -> 0x0583 }
            java.lang.String r3 = r3.getString(r4, r7)     // Catch:{ Exception -> 0x0583 }
            android.content.SharedPreferences r4 = r11.preferences     // Catch:{ Exception -> 0x0583 }
            java.lang.StringBuilder r14 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0583 }
            r14.<init>()     // Catch:{ Exception -> 0x0583 }
            java.lang.String r15 = r11.fileKey     // Catch:{ Exception -> 0x0583 }
            r14.append(r15)     // Catch:{ Exception -> 0x0583 }
            java.lang.String r15 = "_key"
            r14.append(r15)     // Catch:{ Exception -> 0x0583 }
            java.lang.String r14 = r14.toString()     // Catch:{ Exception -> 0x0583 }
            java.lang.String r4 = r4.getString(r14, r7)     // Catch:{ Exception -> 0x0583 }
            r14 = r4
            if (r3 == 0) goto L_0x0227
            if (r14 == 0) goto L_0x0227
            byte[] r4 = org.telegram.messenger.Utilities.hexToBytes(r14)     // Catch:{ Exception -> 0x0583 }
            r11.key = r4     // Catch:{ Exception -> 0x0583 }
            byte[] r4 = org.telegram.messenger.Utilities.hexToBytes(r3)     // Catch:{ Exception -> 0x0583 }
            r11.iv = r4     // Catch:{ Exception -> 0x0583 }
            byte[] r15 = r11.key     // Catch:{ Exception -> 0x0583 }
            if (r15 == 0) goto L_0x0225
            if (r4 == 0) goto L_0x0225
            int r15 = r15.length     // Catch:{ Exception -> 0x0583 }
            r7 = 32
            if (r15 != r7) goto L_0x0225
            int r15 = r4.length     // Catch:{ Exception -> 0x0583 }
            if (r15 != r7) goto L_0x0225
            byte[] r15 = new byte[r7]     // Catch:{ Exception -> 0x0583 }
            r11.ivChange = r15     // Catch:{ Exception -> 0x0583 }
            r1 = 0
            java.lang.System.arraycopy(r4, r1, r15, r1, r7)     // Catch:{ Exception -> 0x0583 }
            goto L_0x0228
        L_0x0225:
            r0 = 1
            goto L_0x0228
        L_0x0227:
            r0 = 1
        L_0x0228:
            if (r0 != 0) goto L_0x0323
            if (r2 == 0) goto L_0x0323
            boolean r1 = r11.isBigFile     // Catch:{ Exception -> 0x0583 }
            if (r1 == 0) goto L_0x023a
            int r3 = r11.uploadStartTime     // Catch:{ Exception -> 0x0583 }
            r7 = 86400(0x15180, float:1.21072E-40)
            int r3 = r3 - r7
            if (r2 >= r3) goto L_0x023a
            r2 = 0
            goto L_0x0249
        L_0x023a:
            if (r1 != 0) goto L_0x0249
            float r3 = (float) r2     // Catch:{ Exception -> 0x0583 }
            int r7 = r11.uploadStartTime     // Catch:{ Exception -> 0x0583 }
            float r7 = (float) r7     // Catch:{ Exception -> 0x0583 }
            r14 = 1168687104(0x45a8CLASSNAME, float:5400.0)
            float r7 = r7 - r14
            int r3 = (r3 > r7 ? 1 : (r3 == r7 ? 0 : -1))
            if (r3 >= 0) goto L_0x0249
            r2 = 0
        L_0x0249:
            if (r2 == 0) goto L_0x031d
            r14 = 0
            int r3 = (r5 > r14 ? 1 : (r5 == r14 ? 0 : -1))
            if (r3 <= 0) goto L_0x0316
            r11.readBytesCount = r5     // Catch:{ Exception -> 0x0583 }
            int r3 = r11.uploadChunkSize     // Catch:{ Exception -> 0x0583 }
            long r14 = (long) r3     // Catch:{ Exception -> 0x0583 }
            long r14 = r5 / r14
            int r3 = (int) r14     // Catch:{ Exception -> 0x0583 }
            r11.currentPartNum = r3     // Catch:{ Exception -> 0x0583 }
            if (r1 != 0) goto L_0x02cd
            r1 = 0
        L_0x025e:
            long r14 = (long) r1     // Catch:{ Exception -> 0x0583 }
            r26 = r5
            long r4 = r11.readBytesCount     // Catch:{ Exception -> 0x0583 }
            int r3 = r11.uploadChunkSize     // Catch:{ Exception -> 0x0583 }
            r6 = r8
            long r7 = (long) r3     // Catch:{ Exception -> 0x0583 }
            long r4 = r4 / r7
            int r3 = (r14 > r4 ? 1 : (r14 == r4 ? 0 : -1))
            if (r3 >= 0) goto L_0x02cc
            java.io.RandomAccessFile r3 = r11.stream     // Catch:{ Exception -> 0x0583 }
            byte[] r4 = r11.readBuffer     // Catch:{ Exception -> 0x0583 }
            int r3 = r3.read(r4)     // Catch:{ Exception -> 0x0583 }
            r4 = 0
            boolean r5 = r11.isEncrypted     // Catch:{ Exception -> 0x0583 }
            if (r5 == 0) goto L_0x0282
            int r5 = r3 % 16
            if (r5 == 0) goto L_0x0282
            int r5 = r3 % 16
            int r5 = 16 - r5
            int r4 = r4 + r5
        L_0x0282:
            org.telegram.tgnet.NativeByteBuffer r5 = new org.telegram.tgnet.NativeByteBuffer     // Catch:{ Exception -> 0x0583 }
            int r7 = r3 + r4
            r5.<init>((int) r7)     // Catch:{ Exception -> 0x0583 }
            int r7 = r11.uploadChunkSize     // Catch:{ Exception -> 0x0583 }
            if (r3 != r7) goto L_0x0295
            int r7 = r11.totalPartsCount     // Catch:{ Exception -> 0x0583 }
            int r8 = r11.currentPartNum     // Catch:{ Exception -> 0x0583 }
            r14 = 1
            int r8 = r8 + r14
            if (r7 != r8) goto L_0x0298
        L_0x0295:
            r7 = 1
            r11.isLastPart = r7     // Catch:{ Exception -> 0x0583 }
        L_0x0298:
            byte[] r7 = r11.readBuffer     // Catch:{ Exception -> 0x0583 }
            r8 = 0
            r5.writeBytes(r7, r8, r3)     // Catch:{ Exception -> 0x0583 }
            boolean r7 = r11.isEncrypted     // Catch:{ Exception -> 0x0583 }
            if (r7 == 0) goto L_0x02c3
            r7 = 0
        L_0x02a3:
            if (r7 >= r4) goto L_0x02ac
            r8 = 0
            r5.writeByte((int) r8)     // Catch:{ Exception -> 0x0583 }
            int r7 = r7 + 1
            goto L_0x02a3
        L_0x02ac:
            java.nio.ByteBuffer r7 = r5.buffer     // Catch:{ Exception -> 0x0583 }
            byte[] r8 = r11.key     // Catch:{ Exception -> 0x0583 }
            byte[] r14 = r11.ivChange     // Catch:{ Exception -> 0x0583 }
            r32 = 1
            r33 = 1
            r34 = 0
            int r35 = r3 + r4
            r29 = r7
            r30 = r8
            r31 = r14
            org.telegram.messenger.Utilities.aesIgeEncryption(r29, r30, r31, r32, r33, r34, r35)     // Catch:{ Exception -> 0x0583 }
        L_0x02c3:
            r5.reuse()     // Catch:{ Exception -> 0x0583 }
            int r1 = r1 + 1
            r8 = r6
            r5 = r26
            goto L_0x025e
        L_0x02cc:
            goto L_0x0329
        L_0x02cd:
            r26 = r5
            r6 = r8
            java.io.RandomAccessFile r1 = r11.stream     // Catch:{ Exception -> 0x0583 }
            r7 = r26
            r1.seek(r7)     // Catch:{ Exception -> 0x0583 }
            boolean r1 = r11.isEncrypted     // Catch:{ Exception -> 0x0583 }
            if (r1 == 0) goto L_0x0329
            android.content.SharedPreferences r1 = r11.preferences     // Catch:{ Exception -> 0x0583 }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0583 }
            r3.<init>()     // Catch:{ Exception -> 0x0583 }
            java.lang.String r4 = r11.fileKey     // Catch:{ Exception -> 0x0583 }
            r3.append(r4)     // Catch:{ Exception -> 0x0583 }
            java.lang.String r4 = "_ivc"
            r3.append(r4)     // Catch:{ Exception -> 0x0583 }
            java.lang.String r3 = r3.toString()     // Catch:{ Exception -> 0x0583 }
            r4 = 0
            java.lang.String r1 = r1.getString(r3, r4)     // Catch:{ Exception -> 0x0583 }
            if (r1 == 0) goto L_0x030d
            byte[] r3 = org.telegram.messenger.Utilities.hexToBytes(r1)     // Catch:{ Exception -> 0x0583 }
            r11.ivChange = r3     // Catch:{ Exception -> 0x0583 }
            if (r3 == 0) goto L_0x0304
            int r3 = r3.length     // Catch:{ Exception -> 0x0583 }
            r4 = 32
            if (r3 == r4) goto L_0x0315
        L_0x0304:
            r0 = 1
            r14 = 0
            r11.readBytesCount = r14     // Catch:{ Exception -> 0x0583 }
            r3 = 0
            r11.currentPartNum = r3     // Catch:{ Exception -> 0x0583 }
            goto L_0x0315
        L_0x030d:
            r0 = 1
            r14 = 0
            r11.readBytesCount = r14     // Catch:{ Exception -> 0x0583 }
            r3 = 0
            r11.currentPartNum = r3     // Catch:{ Exception -> 0x0583 }
        L_0x0315:
            goto L_0x0329
        L_0x0316:
            r36 = r5
            r6 = r8
            r7 = r36
            r0 = 1
            goto L_0x0329
        L_0x031d:
            r36 = r5
            r6 = r8
            r7 = r36
            goto L_0x0329
        L_0x0323:
            r36 = r5
            r6 = r8
            r7 = r36
            r0 = 1
        L_0x0329:
            r1 = r0
            goto L_0x0330
        L_0x032b:
            r22 = r6
            r6 = r8
            r0 = 1
            r1 = r0
        L_0x0330:
            if (r1 == 0) goto L_0x0377
            boolean r0 = r11.isEncrypted     // Catch:{ Exception -> 0x0583 }
            if (r0 == 0) goto L_0x035c
            r2 = 32
            byte[] r0 = new byte[r2]     // Catch:{ Exception -> 0x0583 }
            r11.iv = r0     // Catch:{ Exception -> 0x0583 }
            byte[] r0 = new byte[r2]     // Catch:{ Exception -> 0x0583 }
            r11.key = r0     // Catch:{ Exception -> 0x0583 }
            byte[] r0 = new byte[r2]     // Catch:{ Exception -> 0x0583 }
            r11.ivChange = r0     // Catch:{ Exception -> 0x0583 }
            java.security.SecureRandom r0 = org.telegram.messenger.Utilities.random     // Catch:{ Exception -> 0x0583 }
            byte[] r2 = r11.iv     // Catch:{ Exception -> 0x0583 }
            r0.nextBytes(r2)     // Catch:{ Exception -> 0x0583 }
            java.security.SecureRandom r0 = org.telegram.messenger.Utilities.random     // Catch:{ Exception -> 0x0583 }
            byte[] r2 = r11.key     // Catch:{ Exception -> 0x0583 }
            r0.nextBytes(r2)     // Catch:{ Exception -> 0x0583 }
            byte[] r0 = r11.iv     // Catch:{ Exception -> 0x0583 }
            byte[] r2 = r11.ivChange     // Catch:{ Exception -> 0x0583 }
            r3 = 32
            r4 = 0
            java.lang.System.arraycopy(r0, r4, r2, r4, r3)     // Catch:{ Exception -> 0x0583 }
        L_0x035c:
            java.security.SecureRandom r0 = org.telegram.messenger.Utilities.random     // Catch:{ Exception -> 0x0583 }
            long r2 = r0.nextLong()     // Catch:{ Exception -> 0x0583 }
            r11.currentFileId = r2     // Catch:{ Exception -> 0x0583 }
            boolean r0 = r11.nextPartFirst     // Catch:{ Exception -> 0x0583 }
            if (r0 != 0) goto L_0x0377
            boolean r0 = r11.uploadFirstPartLater     // Catch:{ Exception -> 0x0583 }
            if (r0 != 0) goto L_0x0377
            long r2 = r11.estimatedSize     // Catch:{ Exception -> 0x0583 }
            r7 = 0
            int r0 = (r2 > r7 ? 1 : (r2 == r7 ? 0 : -1))
            if (r0 != 0) goto L_0x0377
            r38.storeFileUploadInfo()     // Catch:{ Exception -> 0x0583 }
        L_0x0377:
            boolean r0 = r11.isEncrypted     // Catch:{ Exception -> 0x0583 }
            if (r0 == 0) goto L_0x03b3
            java.lang.String r0 = "MD5"
            java.security.MessageDigest r0 = java.security.MessageDigest.getInstance(r0)     // Catch:{ Exception -> 0x03af }
            r2 = 64
            byte[] r2 = new byte[r2]     // Catch:{ Exception -> 0x03af }
            byte[] r3 = r11.key     // Catch:{ Exception -> 0x03af }
            r4 = 32
            r5 = 0
            java.lang.System.arraycopy(r3, r5, r2, r5, r4)     // Catch:{ Exception -> 0x03af }
            byte[] r3 = r11.iv     // Catch:{ Exception -> 0x03af }
            java.lang.System.arraycopy(r3, r5, r2, r4, r4)     // Catch:{ Exception -> 0x03af }
            byte[] r3 = r0.digest(r2)     // Catch:{ Exception -> 0x03af }
            r5 = 0
        L_0x0397:
            r7 = 4
            if (r5 >= r7) goto L_0x03ae
            int r7 = r11.fingerprint     // Catch:{ Exception -> 0x03af }
            byte r8 = r3[r5]     // Catch:{ Exception -> 0x03af }
            int r14 = r5 + 4
            byte r14 = r3[r14]     // Catch:{ Exception -> 0x03af }
            r8 = r8 ^ r14
            r8 = r8 & 255(0xff, float:3.57E-43)
            int r14 = r5 * 8
            int r8 = r8 << r14
            r7 = r7 | r8
            r11.fingerprint = r7     // Catch:{ Exception -> 0x03af }
            int r5 = r5 + 1
            goto L_0x0397
        L_0x03ae:
            goto L_0x03b3
        L_0x03af:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ Exception -> 0x0583 }
        L_0x03b3:
            long r2 = r11.readBytesCount     // Catch:{ Exception -> 0x0583 }
            r11.uploadedBytesCount = r2     // Catch:{ Exception -> 0x0583 }
            int r0 = r11.currentPartNum     // Catch:{ Exception -> 0x0583 }
            r11.lastSavedPartNum = r0     // Catch:{ Exception -> 0x0583 }
            boolean r0 = r11.uploadFirstPartLater     // Catch:{ Exception -> 0x0583 }
            if (r0 == 0) goto L_0x03ef
            boolean r0 = r11.isBigFile     // Catch:{ Exception -> 0x0583 }
            if (r0 == 0) goto L_0x03d1
            java.io.RandomAccessFile r0 = r11.stream     // Catch:{ Exception -> 0x0583 }
            int r2 = r11.uploadChunkSize     // Catch:{ Exception -> 0x0583 }
            long r2 = (long) r2     // Catch:{ Exception -> 0x0583 }
            r0.seek(r2)     // Catch:{ Exception -> 0x0583 }
            int r0 = r11.uploadChunkSize     // Catch:{ Exception -> 0x0583 }
            long r2 = (long) r0     // Catch:{ Exception -> 0x0583 }
            r11.readBytesCount = r2     // Catch:{ Exception -> 0x0583 }
            goto L_0x03da
        L_0x03d1:
            java.io.RandomAccessFile r0 = r11.stream     // Catch:{ Exception -> 0x0583 }
            r2 = 1024(0x400, double:5.06E-321)
            r0.seek(r2)     // Catch:{ Exception -> 0x0583 }
            r11.readBytesCount = r2     // Catch:{ Exception -> 0x0583 }
        L_0x03da:
            r2 = 1
            r11.currentPartNum = r2     // Catch:{ Exception -> 0x0583 }
            goto L_0x03ef
        L_0x03de:
            r22 = r6
            r6 = r8
            java.lang.Exception r0 = new java.lang.Exception     // Catch:{ Exception -> 0x0583 }
            r0.<init>(r7)     // Catch:{ Exception -> 0x0583 }
            throw r0     // Catch:{ Exception -> 0x0583 }
        L_0x03e7:
            r22 = r6
            java.lang.Exception r0 = new java.lang.Exception     // Catch:{ Exception -> 0x0583 }
            r0.<init>(r7)     // Catch:{ Exception -> 0x0583 }
            throw r0     // Catch:{ Exception -> 0x0583 }
        L_0x03ef:
            long r0 = r11.estimatedSize     // Catch:{ Exception -> 0x0583 }
            r2 = 0
            int r5 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r5 == 0) goto L_0x0404
            long r0 = r11.readBytesCount     // Catch:{ Exception -> 0x0583 }
            int r2 = r11.uploadChunkSize     // Catch:{ Exception -> 0x0583 }
            long r2 = (long) r2     // Catch:{ Exception -> 0x0583 }
            long r0 = r0 + r2
            long r2 = r11.availableSize     // Catch:{ Exception -> 0x0583 }
            int r5 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r5 <= 0) goto L_0x0404
            return
        L_0x0404:
            boolean r0 = r11.nextPartFirst     // Catch:{ Exception -> 0x0583 }
            if (r0 == 0) goto L_0x042b
            java.io.RandomAccessFile r0 = r11.stream     // Catch:{ Exception -> 0x0583 }
            r1 = 0
            r0.seek(r1)     // Catch:{ Exception -> 0x0583 }
            boolean r0 = r11.isBigFile     // Catch:{ Exception -> 0x0583 }
            if (r0 == 0) goto L_0x041d
            java.io.RandomAccessFile r0 = r11.stream     // Catch:{ Exception -> 0x0583 }
            byte[] r1 = r11.readBuffer     // Catch:{ Exception -> 0x0583 }
            int r0 = r0.read(r1)     // Catch:{ Exception -> 0x0583 }
            r3 = 0
            goto L_0x0428
        L_0x041d:
            java.io.RandomAccessFile r0 = r11.stream     // Catch:{ Exception -> 0x0583 }
            byte[] r1 = r11.readBuffer     // Catch:{ Exception -> 0x0583 }
            r2 = 1024(0x400, float:1.435E-42)
            r3 = 0
            int r0 = r0.read(r1, r3, r2)     // Catch:{ Exception -> 0x0583 }
        L_0x0428:
            r11.currentPartNum = r3     // Catch:{ Exception -> 0x0583 }
            goto L_0x0433
        L_0x042b:
            java.io.RandomAccessFile r0 = r11.stream     // Catch:{ Exception -> 0x0583 }
            byte[] r1 = r11.readBuffer     // Catch:{ Exception -> 0x0583 }
            int r0 = r0.read(r1)     // Catch:{ Exception -> 0x0583 }
        L_0x0433:
            r1 = -1
            if (r0 != r1) goto L_0x0437
            return
        L_0x0437:
            r2 = 0
            boolean r3 = r11.isEncrypted     // Catch:{ Exception -> 0x0583 }
            if (r3 == 0) goto L_0x0445
            int r3 = r0 % 16
            if (r3 == 0) goto L_0x0445
            int r3 = r0 % 16
            int r3 = 16 - r3
            int r2 = r2 + r3
        L_0x0445:
            org.telegram.tgnet.NativeByteBuffer r3 = new org.telegram.tgnet.NativeByteBuffer     // Catch:{ Exception -> 0x0583 }
            int r5 = r0 + r2
            r3.<init>((int) r5)     // Catch:{ Exception -> 0x0583 }
            boolean r5 = r11.nextPartFirst     // Catch:{ Exception -> 0x0583 }
            if (r5 != 0) goto L_0x0464
            int r5 = r11.uploadChunkSize     // Catch:{ Exception -> 0x0583 }
            if (r0 != r5) goto L_0x0464
            long r5 = r11.estimatedSize     // Catch:{ Exception -> 0x0583 }
            r7 = 0
            int r9 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1))
            if (r9 != 0) goto L_0x0472
            int r5 = r11.totalPartsCount     // Catch:{ Exception -> 0x0583 }
            int r6 = r11.currentPartNum     // Catch:{ Exception -> 0x0583 }
            r7 = 1
            int r6 = r6 + r7
            if (r5 != r6) goto L_0x0472
        L_0x0464:
            boolean r5 = r11.uploadFirstPartLater     // Catch:{ Exception -> 0x0583 }
            if (r5 == 0) goto L_0x046f
            r5 = 1
            r11.nextPartFirst = r5     // Catch:{ Exception -> 0x0583 }
            r5 = 0
            r11.uploadFirstPartLater = r5     // Catch:{ Exception -> 0x0583 }
            goto L_0x0472
        L_0x046f:
            r5 = 1
            r11.isLastPart = r5     // Catch:{ Exception -> 0x0583 }
        L_0x0472:
            byte[] r5 = r11.readBuffer     // Catch:{ Exception -> 0x0583 }
            r6 = 0
            r3.writeBytes(r5, r6, r0)     // Catch:{ Exception -> 0x0583 }
            boolean r5 = r11.isEncrypted     // Catch:{ Exception -> 0x0583 }
            if (r5 == 0) goto L_0x04b4
            r5 = 0
        L_0x047d:
            if (r5 >= r2) goto L_0x0486
            r6 = 0
            r3.writeByte((int) r6)     // Catch:{ Exception -> 0x0583 }
            int r5 = r5 + 1
            goto L_0x047d
        L_0x0486:
            java.nio.ByteBuffer r5 = r3.buffer     // Catch:{ Exception -> 0x0583 }
            byte[] r6 = r11.key     // Catch:{ Exception -> 0x0583 }
            byte[] r7 = r11.ivChange     // Catch:{ Exception -> 0x0583 }
            r27 = 1
            r28 = 1
            r29 = 0
            int r30 = r0 + r2
            r24 = r5
            r25 = r6
            r26 = r7
            org.telegram.messenger.Utilities.aesIgeEncryption(r24, r25, r26, r27, r28, r29, r30)     // Catch:{ Exception -> 0x0583 }
            java.util.ArrayList<byte[]> r5 = r11.freeRequestIvs     // Catch:{ Exception -> 0x0583 }
            r6 = 0
            java.lang.Object r5 = r5.get(r6)     // Catch:{ Exception -> 0x0583 }
            byte[] r5 = (byte[]) r5     // Catch:{ Exception -> 0x0583 }
            byte[] r7 = r11.ivChange     // Catch:{ Exception -> 0x0583 }
            r4 = 32
            java.lang.System.arraycopy(r7, r6, r5, r6, r4)     // Catch:{ Exception -> 0x0583 }
            java.util.ArrayList<byte[]> r4 = r11.freeRequestIvs     // Catch:{ Exception -> 0x0583 }
            r4.remove(r6)     // Catch:{ Exception -> 0x0583 }
            r12 = r5
            goto L_0x04b7
        L_0x04b4:
            r4 = 0
            r5 = r4
            r12 = r5
        L_0x04b7:
            boolean r4 = r11.isBigFile     // Catch:{ Exception -> 0x0583 }
            if (r4 == 0) goto L_0x04de
            org.telegram.tgnet.TLRPC$TL_upload_saveBigFilePart r4 = new org.telegram.tgnet.TLRPC$TL_upload_saveBigFilePart     // Catch:{ Exception -> 0x0583 }
            r4.<init>()     // Catch:{ Exception -> 0x0583 }
            int r5 = r11.currentPartNum     // Catch:{ Exception -> 0x0583 }
            r6 = r5
            r4.file_part = r5     // Catch:{ Exception -> 0x0583 }
            long r7 = r11.currentFileId     // Catch:{ Exception -> 0x0583 }
            r4.file_id = r7     // Catch:{ Exception -> 0x0583 }
            long r7 = r11.estimatedSize     // Catch:{ Exception -> 0x0583 }
            r9 = 0
            int r5 = (r7 > r9 ? 1 : (r7 == r9 ? 0 : -1))
            if (r5 == 0) goto L_0x04d4
            r4.file_total_parts = r1     // Catch:{ Exception -> 0x0583 }
            goto L_0x04d8
        L_0x04d4:
            int r1 = r11.totalPartsCount     // Catch:{ Exception -> 0x0583 }
            r4.file_total_parts = r1     // Catch:{ Exception -> 0x0583 }
        L_0x04d8:
            r4.bytes = r3     // Catch:{ Exception -> 0x0583 }
            r1 = r4
            r13 = r1
            r14 = r6
            goto L_0x04f2
        L_0x04de:
            org.telegram.tgnet.TLRPC$TL_upload_saveFilePart r1 = new org.telegram.tgnet.TLRPC$TL_upload_saveFilePart     // Catch:{ Exception -> 0x0583 }
            r1.<init>()     // Catch:{ Exception -> 0x0583 }
            int r4 = r11.currentPartNum     // Catch:{ Exception -> 0x0583 }
            r6 = r4
            r1.file_part = r4     // Catch:{ Exception -> 0x0583 }
            long r4 = r11.currentFileId     // Catch:{ Exception -> 0x0583 }
            r1.file_id = r4     // Catch:{ Exception -> 0x0583 }
            r1.bytes = r3     // Catch:{ Exception -> 0x0583 }
            r4 = r1
            r1 = r4
            r13 = r1
            r14 = r6
        L_0x04f2:
            boolean r1 = r11.isLastPart     // Catch:{ Exception -> 0x0583 }
            if (r1 == 0) goto L_0x050b
            boolean r1 = r11.nextPartFirst     // Catch:{ Exception -> 0x0583 }
            if (r1 == 0) goto L_0x050b
            r15 = 0
            r11.nextPartFirst = r15     // Catch:{ Exception -> 0x0583 }
            int r1 = r11.totalPartsCount     // Catch:{ Exception -> 0x0583 }
            r4 = 1
            int r1 = r1 - r4
            r11.currentPartNum = r1     // Catch:{ Exception -> 0x0583 }
            java.io.RandomAccessFile r1 = r11.stream     // Catch:{ Exception -> 0x0583 }
            long r4 = r11.totalFileSize     // Catch:{ Exception -> 0x0583 }
            r1.seek(r4)     // Catch:{ Exception -> 0x0583 }
            goto L_0x050c
        L_0x050b:
            r15 = 0
        L_0x050c:
            long r4 = r11.readBytesCount     // Catch:{ Exception -> 0x0583 }
            long r6 = (long) r0     // Catch:{ Exception -> 0x0583 }
            long r4 = r4 + r6
            r11.readBytesCount = r4     // Catch:{ Exception -> 0x0583 }
            int r1 = r11.currentPartNum
            r2 = 1
            int r1 = r1 + r2
            r11.currentPartNum = r1
            int r1 = r11.currentUploadRequetsCount
            int r1 = r1 + r2
            r11.currentUploadRequetsCount = r1
            int r1 = r11.requestNum
            int r2 = r1 + 1
            r11.requestNum = r2
            r9 = r1
            int r1 = r14 + r0
            long r7 = (long) r1
            int r1 = r13.getObjectSize()
            r2 = 4
            int r17 = r1 + 4
            int r10 = r11.operationGuid
            boolean r1 = r11.slowNetwork
            if (r1 == 0) goto L_0x0539
            r1 = 4
            r18 = r1
            goto L_0x0541
        L_0x0539:
            int r1 = r9 % 4
            int r1 = r1 << 16
            r2 = 4
            r1 = r1 | r2
            r18 = r1
        L_0x0541:
            int r1 = r11.currentAccount
            org.telegram.tgnet.ConnectionsManager r24 = org.telegram.tgnet.ConnectionsManager.getInstance(r1)
            org.telegram.messenger.FileUploadOperation$$ExternalSyntheticLambda5 r26 = new org.telegram.messenger.FileUploadOperation$$ExternalSyntheticLambda5
            r1 = r26
            r2 = r38
            r3 = r10
            r4 = r17
            r5 = r12
            r6 = r9
            r19 = r7
            r7 = r0
            r8 = r14
            r15 = r9
            r22 = r10
            r9 = r19
            r1.<init>(r2, r3, r4, r5, r6, r7, r8, r9)
            r27 = 0
            org.telegram.messenger.FileUploadOperation$$ExternalSyntheticLambda6 r1 = new org.telegram.messenger.FileUploadOperation$$ExternalSyntheticLambda6
            r1.<init>(r11)
            boolean r2 = r11.forceSmallFile
            if (r2 == 0) goto L_0x056c
            r29 = 4
            goto L_0x056e
        L_0x056c:
            r29 = 0
        L_0x056e:
            r30 = 2147483647(0x7fffffff, float:NaN)
            r32 = 1
            r25 = r13
            r28 = r1
            r31 = r18
            int r1 = r24.sendRequest(r25, r26, r27, r28, r29, r30, r31, r32)
            android.util.SparseIntArray r2 = r11.requestTokens
            r2.put(r15, r1)
            return
        L_0x0583:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            r1 = 4
            r11.state = r1
            org.telegram.messenger.FileUploadOperation$FileUploadOperationDelegate r1 = r11.delegate
            r1.didFailedUploadingFile(r11)
            r38.cleanup()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.FileUploadOperation.startUploadRequest():void");
    }

    /* renamed from: lambda$startUploadRequest$4$org-telegram-messenger-FileUploadOperation  reason: not valid java name */
    public /* synthetic */ void m1871xfe4e3d8(int currentOperationGuid, int requestSize, byte[] currentRequestIv, int requestNumFinal, int currentRequestBytes, int currentRequestPartNum, long currentRequestBytesOffset, TLObject response, TLRPC.TL_error error) {
        long size;
        int i;
        TLRPC.InputEncryptedFile result;
        TLRPC.InputFile result2;
        int i2 = requestSize;
        byte[] bArr = currentRequestIv;
        int i3 = currentRequestPartNum;
        TLObject tLObject = response;
        if (currentOperationGuid == this.operationGuid) {
            int networkType = tLObject != null ? tLObject.networkType : ApplicationLoader.getCurrentNetworkType();
            int i4 = this.currentType;
            if (i4 == 50331648) {
                StatsController.getInstance(this.currentAccount).incrementSentBytesCount(networkType, 3, (long) i2);
            } else if (i4 == 33554432) {
                StatsController.getInstance(this.currentAccount).incrementSentBytesCount(networkType, 2, (long) i2);
            } else if (i4 == 16777216) {
                StatsController.getInstance(this.currentAccount).incrementSentBytesCount(networkType, 4, (long) i2);
            } else if (i4 == 67108864) {
                StatsController.getInstance(this.currentAccount).incrementSentBytesCount(networkType, 5, (long) i2);
            }
            if (bArr != null) {
                this.freeRequestIvs.add(bArr);
            }
            this.requestTokens.delete(requestNumFinal);
            if (!(tLObject instanceof TLRPC.TL_boolTrue)) {
                int i5 = currentRequestBytes;
                long j = currentRequestBytesOffset;
                this.state = 4;
                this.delegate.didFailedUploadingFile(this);
                cleanup();
            } else if (this.state == 1) {
                this.uploadedBytesCount += (long) currentRequestBytes;
                long j2 = this.estimatedSize;
                if (j2 != 0) {
                    size = Math.max(this.availableSize, j2);
                } else {
                    size = this.totalFileSize;
                }
                this.delegate.didChangedUploadProgress(this, this.uploadedBytesCount, size);
                int i6 = this.currentUploadRequetsCount - 1;
                this.currentUploadRequetsCount = i6;
                if (this.isLastPart && i6 == 0 && this.state == 1) {
                    this.state = 3;
                    if (this.key == null) {
                        if (this.isBigFile) {
                            result2 = new TLRPC.TL_inputFileBig();
                        } else {
                            TLRPC.InputFile tL_inputFile = new TLRPC.TL_inputFile();
                            tL_inputFile.md5_checksum = "";
                            result2 = tL_inputFile;
                        }
                        result2.parts = this.currentPartNum;
                        result2.id = this.currentFileId;
                        String str = this.uploadingFilePath;
                        result2.name = str.substring(str.lastIndexOf("/") + 1);
                        TLRPC.InputFile inputFile = result2;
                        i = 3;
                        this.delegate.didFinishUploadingFile(this, result2, (TLRPC.InputEncryptedFile) null, (byte[]) null, (byte[]) null);
                        cleanup();
                    } else {
                        i = 3;
                        if (this.isBigFile) {
                            result = new TLRPC.TL_inputEncryptedFileBigUploaded();
                        } else {
                            TLRPC.InputEncryptedFile tL_inputEncryptedFileUploaded = new TLRPC.TL_inputEncryptedFileUploaded();
                            tL_inputEncryptedFileUploaded.md5_checksum = "";
                            result = tL_inputEncryptedFileUploaded;
                        }
                        result.parts = this.currentPartNum;
                        result.id = this.currentFileId;
                        result.key_fingerprint = this.fingerprint;
                        TLRPC.InputEncryptedFile inputEncryptedFile = result;
                        this.delegate.didFinishUploadingFile(this, (TLRPC.InputFile) null, result, this.key, this.iv);
                        cleanup();
                    }
                    int i7 = this.currentType;
                    if (i7 == 50331648) {
                        StatsController.getInstance(this.currentAccount).incrementSentItemsCount(ApplicationLoader.getCurrentNetworkType(), i, 1);
                        long j3 = currentRequestBytesOffset;
                    } else if (i7 == 33554432) {
                        StatsController.getInstance(this.currentAccount).incrementSentItemsCount(ApplicationLoader.getCurrentNetworkType(), 2, 1);
                        long j4 = currentRequestBytesOffset;
                    } else if (i7 == 16777216) {
                        StatsController.getInstance(this.currentAccount).incrementSentItemsCount(ApplicationLoader.getCurrentNetworkType(), 4, 1);
                        long j5 = currentRequestBytesOffset;
                    } else if (i7 == 67108864) {
                        StatsController.getInstance(this.currentAccount).incrementSentItemsCount(ApplicationLoader.getCurrentNetworkType(), 5, 1);
                        long j6 = currentRequestBytesOffset;
                    } else {
                        long j7 = currentRequestBytesOffset;
                    }
                } else if (i6 < this.maxRequestsCount) {
                    if (this.estimatedSize != 0 || this.uploadFirstPartLater || this.nextPartFirst) {
                        long j8 = currentRequestBytesOffset;
                    } else {
                        if (this.saveInfoTimes >= 4) {
                            this.saveInfoTimes = 0;
                        }
                        int i8 = this.lastSavedPartNum;
                        if (i3 == i8) {
                            this.lastSavedPartNum = i8 + 1;
                            long offsetToSave = currentRequestBytesOffset;
                            byte[] ivToSave = currentRequestIv;
                            while (true) {
                                UploadCachedResult uploadCachedResult = this.cachedResults.get(this.lastSavedPartNum);
                                UploadCachedResult result3 = uploadCachedResult;
                                if (uploadCachedResult == null) {
                                    break;
                                }
                                offsetToSave = result3.bytesOffset;
                                ivToSave = result3.iv;
                                this.cachedResults.remove(this.lastSavedPartNum);
                                this.lastSavedPartNum++;
                            }
                            boolean z = this.isBigFile;
                            if ((z && offsetToSave % 1048576 == 0) || (!z && this.saveInfoTimes == 0)) {
                                SharedPreferences.Editor editor = this.preferences.edit();
                                editor.putLong(this.fileKey + "_uploaded", offsetToSave);
                                if (this.isEncrypted) {
                                    editor.putString(this.fileKey + "_ivc", Utilities.bytesToHex(ivToSave));
                                }
                                editor.commit();
                            }
                            long j9 = currentRequestBytesOffset;
                        } else {
                            UploadCachedResult result4 = new UploadCachedResult();
                            long unused = result4.bytesOffset = currentRequestBytesOffset;
                            if (bArr != null) {
                                byte[] unused2 = result4.iv = new byte[32];
                                System.arraycopy(bArr, 0, result4.iv, 0, 32);
                            }
                            this.cachedResults.put(i3, result4);
                        }
                        this.saveInfoTimes++;
                    }
                    startUploadRequest();
                } else {
                    long j10 = currentRequestBytesOffset;
                }
            }
        }
    }

    /* renamed from: lambda$startUploadRequest$6$org-telegram-messenger-FileUploadOperation  reason: not valid java name */
    public /* synthetic */ void m1873x95bdb696() {
        Utilities.stageQueue.postRunnable(new FileUploadOperation$$ExternalSyntheticLambda2(this));
    }

    /* renamed from: lambda$startUploadRequest$5$org-telegram-messenger-FileUploadOperation  reason: not valid java name */
    public /* synthetic */ void m1872xd2d14d37() {
        if (this.currentUploadRequetsCount < this.maxRequestsCount) {
            startUploadRequest();
        }
    }
}
