package org.telegram.messenger;

import android.content.SharedPreferences;
import android.util.SparseArray;
import android.util.SparseIntArray;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$InputEncryptedFile;
import org.telegram.tgnet.TLRPC$InputFile;
import org.telegram.tgnet.TLRPC$TL_boolTrue;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_inputEncryptedFileBigUploaded;
import org.telegram.tgnet.TLRPC$TL_inputEncryptedFileUploaded;
import org.telegram.tgnet.TLRPC$TL_inputFile;
import org.telegram.tgnet.TLRPC$TL_inputFileBig;

public class FileUploadOperation {
    private static final int initialRequestsCount = 8;
    private static final int initialRequestsSlowNetworkCount = 1;
    private static final int maxUploadParts = 4000;
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
    private int estimatedSize;
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

        void didFinishUploadingFile(FileUploadOperation fileUploadOperation, TLRPC$InputFile tLRPC$InputFile, TLRPC$InputEncryptedFile tLRPC$InputEncryptedFile, byte[] bArr, byte[] bArr2);
    }

    private static class UploadCachedResult {
        /* access modifiers changed from: private */
        public long bytesOffset;
        /* access modifiers changed from: private */
        public byte[] iv;

        private UploadCachedResult() {
        }
    }

    public FileUploadOperation(int i, String str, boolean z, int i2, int i3) {
        this.currentAccount = i;
        this.uploadingFilePath = str;
        this.isEncrypted = z;
        this.estimatedSize = i2;
        this.currentType = i3;
        this.uploadFirstPartLater = i2 != 0 && !z;
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
            Utilities.stageQueue.postRunnable(new FileUploadOperation$$ExternalSyntheticLambda2(this));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$start$0() {
        this.preferences = ApplicationLoader.applicationContext.getSharedPreferences("uploadinfo", 0);
        this.slowNetwork = ApplicationLoader.isConnectionSlow();
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("start upload on slow network = " + this.slowNetwork);
        }
        int i = this.slowNetwork ? 1 : 8;
        for (int i2 = 0; i2 < i; i2++) {
            startUploadRequest();
        }
    }

    /* access modifiers changed from: protected */
    public void onNetworkChanged(boolean z) {
        if (this.state == 1) {
            Utilities.stageQueue.postRunnable(new FileUploadOperation$$ExternalSyntheticLambda4(this, z));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onNetworkChanged$1(boolean z) {
        int i;
        if (this.slowNetwork != z) {
            this.slowNetwork = z;
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("network changed to slow = " + this.slowNetwork);
            }
            int i2 = 0;
            while (true) {
                i = 1;
                if (i2 >= this.requestTokens.size()) {
                    break;
                }
                ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.requestTokens.valueAt(i2), true);
                i2++;
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
            for (int i3 = 0; i3 < i; i3++) {
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

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$cancel$2() {
        for (int i = 0; i < this.requestTokens.size(); i++) {
            ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.requestTokens.valueAt(i), true);
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
    public void checkNewDataAvailable(long j, long j2) {
        Utilities.stageQueue.postRunnable(new FileUploadOperation$$ExternalSyntheticLambda3(this, j2, j));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$checkNewDataAvailable$3(long j, long j2) {
        if (!(this.estimatedSize == 0 || j == 0)) {
            this.estimatedSize = 0;
            this.totalFileSize = j;
            calcTotalPartsCount();
            if (!this.uploadFirstPartLater && this.started) {
                storeFileUploadInfo();
            }
        }
        if (j <= 0) {
            j = j2;
        }
        this.availableSize = j;
        if (this.currentUploadRequetsCount < this.maxRequestsCount) {
            startUploadRequest();
        }
    }

    private void storeFileUploadInfo() {
        SharedPreferences.Editor edit = this.preferences.edit();
        edit.putInt(this.fileKey + "_time", this.uploadStartTime);
        edit.putLong(this.fileKey + "_size", this.totalFileSize);
        edit.putLong(this.fileKey + "_id", this.currentFileId);
        edit.remove(this.fileKey + "_uploaded");
        if (this.isEncrypted) {
            edit.putString(this.fileKey + "_iv", Utilities.bytesToHex(this.iv));
            edit.putString(this.fileKey + "_ivc", Utilities.bytesToHex(this.ivChange));
            edit.putString(this.fileKey + "_key", Utilities.bytesToHex(this.key));
        }
        edit.commit();
    }

    private void calcTotalPartsCount() {
        if (!this.uploadFirstPartLater) {
            long j = this.totalFileSize;
            int i = this.uploadChunkSize;
            this.totalPartsCount = ((int) ((j + ((long) i)) - 1)) / i;
        } else if (this.isBigFile) {
            long j2 = this.totalFileSize;
            int i2 = this.uploadChunkSize;
            this.totalPartsCount = (((int) (((j2 - ((long) i2)) + ((long) i2)) - 1)) / i2) + 1;
        } else {
            int i3 = this.uploadChunkSize;
            this.totalPartsCount = (((int) (((this.totalFileSize - 1024) + ((long) i3)) - 1)) / i3) + 1;
        }
    }

    public void setForceSmallFile() {
        this.forceSmallFile = true;
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v0, resolved type: org.telegram.tgnet.TLRPC$TL_upload_saveFilePart} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v1, resolved type: org.telegram.tgnet.TLRPC$TL_upload_saveFilePart} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v11, resolved type: org.telegram.tgnet.TLRPC$TL_upload_saveBigFilePart} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v2, resolved type: org.telegram.tgnet.TLRPC$TL_upload_saveFilePart} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:125:0x02bc A[Catch:{ Exception -> 0x04ba }] */
    /* JADX WARNING: Removed duplicated region for block: B:137:0x02fa  */
    /* JADX WARNING: Removed duplicated region for block: B:148:0x0337 A[Catch:{ Exception -> 0x04ba }] */
    /* JADX WARNING: Removed duplicated region for block: B:88:0x0205 A[Catch:{ Exception -> 0x04ba }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void startUploadRequest() {
        /*
            r26 = this;
            r11 = r26
            int r0 = r11.state
            r1 = 1
            if (r0 == r1) goto L_0x0008
            return
        L_0x0008:
            r12 = 4
            r11.started = r1     // Catch:{ Exception -> 0x04ba }
            java.io.RandomAccessFile r0 = r11.stream     // Catch:{ Exception -> 0x04ba }
            r2 = 1024(0x400, float:1.435E-42)
            r3 = 0
            r4 = 0
            r6 = 32
            r13 = 0
            if (r0 != 0) goto L_0x0361
            java.io.File r7 = new java.io.File     // Catch:{ Exception -> 0x04ba }
            java.lang.String r0 = r11.uploadingFilePath     // Catch:{ Exception -> 0x04ba }
            r7.<init>(r0)     // Catch:{ Exception -> 0x04ba }
            android.net.Uri r0 = android.net.Uri.fromFile(r7)     // Catch:{ Exception -> 0x04ba }
            boolean r0 = org.telegram.messenger.AndroidUtilities.isInternalUri((android.net.Uri) r0)     // Catch:{ Exception -> 0x04ba }
            java.lang.String r8 = "trying to upload internal file"
            if (r0 != 0) goto L_0x035b
            java.io.RandomAccessFile r0 = new java.io.RandomAccessFile     // Catch:{ Exception -> 0x04ba }
            java.lang.String r9 = "r"
            r0.<init>(r7, r9)     // Catch:{ Exception -> 0x04ba }
            r11.stream = r0     // Catch:{ Exception -> 0x04ba }
            java.lang.Class<java.io.FileDescriptor> r0 = java.io.FileDescriptor.class
            java.lang.String r9 = "getInt$"
            java.lang.Class[] r10 = new java.lang.Class[r13]     // Catch:{ all -> 0x0054 }
            java.lang.reflect.Method r0 = r0.getDeclaredMethod(r9, r10)     // Catch:{ all -> 0x0054 }
            java.io.RandomAccessFile r9 = r11.stream     // Catch:{ all -> 0x0054 }
            java.io.FileDescriptor r9 = r9.getFD()     // Catch:{ all -> 0x0054 }
            java.lang.Object[] r10 = new java.lang.Object[r13]     // Catch:{ all -> 0x0054 }
            java.lang.Object r0 = r0.invoke(r9, r10)     // Catch:{ all -> 0x0054 }
            java.lang.Integer r0 = (java.lang.Integer) r0     // Catch:{ all -> 0x0054 }
            int r0 = r0.intValue()     // Catch:{ all -> 0x0054 }
            boolean r0 = org.telegram.messenger.AndroidUtilities.isInternalUri((int) r0)     // Catch:{ all -> 0x0054 }
            goto L_0x0059
        L_0x0054:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ Exception -> 0x04ba }
            r0 = 0
        L_0x0059:
            if (r0 != 0) goto L_0x0355
            int r0 = r11.estimatedSize     // Catch:{ Exception -> 0x04ba }
            if (r0 == 0) goto L_0x0063
            long r7 = (long) r0     // Catch:{ Exception -> 0x04ba }
            r11.totalFileSize = r7     // Catch:{ Exception -> 0x04ba }
            goto L_0x0069
        L_0x0063:
            long r7 = r7.length()     // Catch:{ Exception -> 0x04ba }
            r11.totalFileSize = r7     // Catch:{ Exception -> 0x04ba }
        L_0x0069:
            boolean r0 = r11.forceSmallFile     // Catch:{ Exception -> 0x04ba }
            if (r0 != 0) goto L_0x0078
            long r7 = r11.totalFileSize     // Catch:{ Exception -> 0x04ba }
            r9 = 10485760(0xa00000, double:5.180654E-317)
            int r0 = (r7 > r9 ? 1 : (r7 == r9 ? 0 : -1))
            if (r0 <= 0) goto L_0x0078
            r11.isBigFile = r1     // Catch:{ Exception -> 0x04ba }
        L_0x0078:
            boolean r0 = r11.slowNetwork     // Catch:{ Exception -> 0x04ba }
            if (r0 == 0) goto L_0x007f
            r7 = 32
            goto L_0x0081
        L_0x007f:
            r7 = 128(0x80, double:6.32E-322)
        L_0x0081:
            long r9 = r11.totalFileSize     // Catch:{ Exception -> 0x04ba }
            r14 = 4096000(0x3e8000, double:2.023693E-317)
            long r9 = r9 + r14
            r16 = 1
            long r9 = r9 - r16
            long r9 = r9 / r14
            long r7 = java.lang.Math.max(r7, r9)     // Catch:{ Exception -> 0x04ba }
            int r0 = (int) r7     // Catch:{ Exception -> 0x04ba }
            r11.uploadChunkSize = r0     // Catch:{ Exception -> 0x04ba }
            int r0 = r2 % r0
            r7 = 64
            if (r0 == 0) goto L_0x00a4
            r0 = 64
        L_0x009b:
            int r8 = r11.uploadChunkSize     // Catch:{ Exception -> 0x04ba }
            if (r8 <= r0) goto L_0x00a2
            int r0 = r0 * 2
            goto L_0x009b
        L_0x00a2:
            r11.uploadChunkSize = r0     // Catch:{ Exception -> 0x04ba }
        L_0x00a4:
            boolean r0 = r11.slowNetwork     // Catch:{ Exception -> 0x04ba }
            if (r0 == 0) goto L_0x00ab
            r0 = 32
            goto L_0x00ad
        L_0x00ab:
            r0 = 2048(0x800, float:2.87E-42)
        L_0x00ad:
            int r8 = r11.uploadChunkSize     // Catch:{ Exception -> 0x04ba }
            int r0 = r0 / r8
            int r0 = java.lang.Math.max(r1, r0)     // Catch:{ Exception -> 0x04ba }
            r11.maxRequestsCount = r0     // Catch:{ Exception -> 0x04ba }
            boolean r0 = r11.isEncrypted     // Catch:{ Exception -> 0x04ba }
            if (r0 == 0) goto L_0x00d2
            java.util.ArrayList r0 = new java.util.ArrayList     // Catch:{ Exception -> 0x04ba }
            int r8 = r11.maxRequestsCount     // Catch:{ Exception -> 0x04ba }
            r0.<init>(r8)     // Catch:{ Exception -> 0x04ba }
            r11.freeRequestIvs = r0     // Catch:{ Exception -> 0x04ba }
            r0 = 0
        L_0x00c4:
            int r8 = r11.maxRequestsCount     // Catch:{ Exception -> 0x04ba }
            if (r0 >= r8) goto L_0x00d2
            java.util.ArrayList<byte[]> r8 = r11.freeRequestIvs     // Catch:{ Exception -> 0x04ba }
            byte[] r9 = new byte[r6]     // Catch:{ Exception -> 0x04ba }
            r8.add(r9)     // Catch:{ Exception -> 0x04ba }
            int r0 = r0 + 1
            goto L_0x00c4
        L_0x00d2:
            int r0 = r11.uploadChunkSize     // Catch:{ Exception -> 0x04ba }
            int r0 = r0 * 1024
            r11.uploadChunkSize = r0     // Catch:{ Exception -> 0x04ba }
            r26.calcTotalPartsCount()     // Catch:{ Exception -> 0x04ba }
            int r0 = r11.uploadChunkSize     // Catch:{ Exception -> 0x04ba }
            byte[] r0 = new byte[r0]     // Catch:{ Exception -> 0x04ba }
            r11.readBuffer = r0     // Catch:{ Exception -> 0x04ba }
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x04ba }
            r0.<init>()     // Catch:{ Exception -> 0x04ba }
            java.lang.String r8 = r11.uploadingFilePath     // Catch:{ Exception -> 0x04ba }
            r0.append(r8)     // Catch:{ Exception -> 0x04ba }
            boolean r8 = r11.isEncrypted     // Catch:{ Exception -> 0x04ba }
            if (r8 == 0) goto L_0x00f2
            java.lang.String r8 = "enc"
            goto L_0x00f4
        L_0x00f2:
            java.lang.String r8 = ""
        L_0x00f4:
            r0.append(r8)     // Catch:{ Exception -> 0x04ba }
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x04ba }
            java.lang.String r0 = org.telegram.messenger.Utilities.MD5(r0)     // Catch:{ Exception -> 0x04ba }
            r11.fileKey = r0     // Catch:{ Exception -> 0x04ba }
            android.content.SharedPreferences r0 = r11.preferences     // Catch:{ Exception -> 0x04ba }
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x04ba }
            r8.<init>()     // Catch:{ Exception -> 0x04ba }
            java.lang.String r9 = r11.fileKey     // Catch:{ Exception -> 0x04ba }
            r8.append(r9)     // Catch:{ Exception -> 0x04ba }
            java.lang.String r9 = "_size"
            r8.append(r9)     // Catch:{ Exception -> 0x04ba }
            java.lang.String r8 = r8.toString()     // Catch:{ Exception -> 0x04ba }
            long r8 = r0.getLong(r8, r4)     // Catch:{ Exception -> 0x04ba }
            long r14 = java.lang.System.currentTimeMillis()     // Catch:{ Exception -> 0x04ba }
            r16 = 1000(0x3e8, double:4.94E-321)
            long r14 = r14 / r16
            int r0 = (int) r14     // Catch:{ Exception -> 0x04ba }
            r11.uploadStartTime = r0     // Catch:{ Exception -> 0x04ba }
            boolean r0 = r11.uploadFirstPartLater     // Catch:{ Exception -> 0x04ba }
            if (r0 != 0) goto L_0x02b9
            boolean r0 = r11.nextPartFirst     // Catch:{ Exception -> 0x04ba }
            if (r0 != 0) goto L_0x02b9
            int r0 = r11.estimatedSize     // Catch:{ Exception -> 0x04ba }
            if (r0 != 0) goto L_0x02b9
            long r14 = r11.totalFileSize     // Catch:{ Exception -> 0x04ba }
            int r0 = (r8 > r14 ? 1 : (r8 == r14 ? 0 : -1))
            if (r0 != 0) goto L_0x02b9
            android.content.SharedPreferences r0 = r11.preferences     // Catch:{ Exception -> 0x04ba }
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x04ba }
            r8.<init>()     // Catch:{ Exception -> 0x04ba }
            java.lang.String r9 = r11.fileKey     // Catch:{ Exception -> 0x04ba }
            r8.append(r9)     // Catch:{ Exception -> 0x04ba }
            java.lang.String r9 = "_id"
            r8.append(r9)     // Catch:{ Exception -> 0x04ba }
            java.lang.String r8 = r8.toString()     // Catch:{ Exception -> 0x04ba }
            long r8 = r0.getLong(r8, r4)     // Catch:{ Exception -> 0x04ba }
            r11.currentFileId = r8     // Catch:{ Exception -> 0x04ba }
            android.content.SharedPreferences r0 = r11.preferences     // Catch:{ Exception -> 0x04ba }
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x04ba }
            r8.<init>()     // Catch:{ Exception -> 0x04ba }
            java.lang.String r9 = r11.fileKey     // Catch:{ Exception -> 0x04ba }
            r8.append(r9)     // Catch:{ Exception -> 0x04ba }
            java.lang.String r9 = "_time"
            r8.append(r9)     // Catch:{ Exception -> 0x04ba }
            java.lang.String r8 = r8.toString()     // Catch:{ Exception -> 0x04ba }
            int r0 = r0.getInt(r8, r13)     // Catch:{ Exception -> 0x04ba }
            android.content.SharedPreferences r8 = r11.preferences     // Catch:{ Exception -> 0x04ba }
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x04ba }
            r9.<init>()     // Catch:{ Exception -> 0x04ba }
            java.lang.String r10 = r11.fileKey     // Catch:{ Exception -> 0x04ba }
            r9.append(r10)     // Catch:{ Exception -> 0x04ba }
            java.lang.String r10 = "_uploaded"
            r9.append(r10)     // Catch:{ Exception -> 0x04ba }
            java.lang.String r9 = r9.toString()     // Catch:{ Exception -> 0x04ba }
            long r8 = r8.getLong(r9, r4)     // Catch:{ Exception -> 0x04ba }
            boolean r10 = r11.isEncrypted     // Catch:{ Exception -> 0x04ba }
            if (r10 == 0) goto L_0x01e0
            android.content.SharedPreferences r10 = r11.preferences     // Catch:{ Exception -> 0x04ba }
            java.lang.StringBuilder r14 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x04ba }
            r14.<init>()     // Catch:{ Exception -> 0x04ba }
            java.lang.String r15 = r11.fileKey     // Catch:{ Exception -> 0x04ba }
            r14.append(r15)     // Catch:{ Exception -> 0x04ba }
            java.lang.String r15 = "_iv"
            r14.append(r15)     // Catch:{ Exception -> 0x04ba }
            java.lang.String r14 = r14.toString()     // Catch:{ Exception -> 0x04ba }
            java.lang.String r10 = r10.getString(r14, r3)     // Catch:{ Exception -> 0x04ba }
            android.content.SharedPreferences r14 = r11.preferences     // Catch:{ Exception -> 0x04ba }
            java.lang.StringBuilder r15 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x04ba }
            r15.<init>()     // Catch:{ Exception -> 0x04ba }
            java.lang.String r2 = r11.fileKey     // Catch:{ Exception -> 0x04ba }
            r15.append(r2)     // Catch:{ Exception -> 0x04ba }
            java.lang.String r2 = "_key"
            r15.append(r2)     // Catch:{ Exception -> 0x04ba }
            java.lang.String r2 = r15.toString()     // Catch:{ Exception -> 0x04ba }
            java.lang.String r2 = r14.getString(r2, r3)     // Catch:{ Exception -> 0x04ba }
            if (r10 == 0) goto L_0x01de
            if (r2 == 0) goto L_0x01de
            byte[] r2 = org.telegram.messenger.Utilities.hexToBytes(r2)     // Catch:{ Exception -> 0x04ba }
            r11.key = r2     // Catch:{ Exception -> 0x04ba }
            byte[] r2 = org.telegram.messenger.Utilities.hexToBytes(r10)     // Catch:{ Exception -> 0x04ba }
            r11.iv = r2     // Catch:{ Exception -> 0x04ba }
            byte[] r10 = r11.key     // Catch:{ Exception -> 0x04ba }
            if (r10 == 0) goto L_0x01de
            if (r2 == 0) goto L_0x01de
            int r10 = r10.length     // Catch:{ Exception -> 0x04ba }
            if (r10 != r6) goto L_0x01de
            int r10 = r2.length     // Catch:{ Exception -> 0x04ba }
            if (r10 != r6) goto L_0x01de
            byte[] r10 = new byte[r6]     // Catch:{ Exception -> 0x04ba }
            r11.ivChange = r10     // Catch:{ Exception -> 0x04ba }
            java.lang.System.arraycopy(r2, r13, r10, r13, r6)     // Catch:{ Exception -> 0x04ba }
            goto L_0x01e0
        L_0x01de:
            r2 = 1
            goto L_0x01e1
        L_0x01e0:
            r2 = 0
        L_0x01e1:
            if (r2 != 0) goto L_0x02b9
            if (r0 == 0) goto L_0x02b9
            boolean r10 = r11.isBigFile     // Catch:{ Exception -> 0x04ba }
            if (r10 == 0) goto L_0x01f3
            int r14 = r11.uploadStartTime     // Catch:{ Exception -> 0x04ba }
            r15 = 86400(0x15180, float:1.21072E-40)
            int r14 = r14 - r15
            if (r0 >= r14) goto L_0x01f3
        L_0x01f1:
            r0 = 0
            goto L_0x0203
        L_0x01f3:
            if (r10 != 0) goto L_0x0203
            float r14 = (float) r0     // Catch:{ Exception -> 0x04ba }
            int r15 = r11.uploadStartTime     // Catch:{ Exception -> 0x04ba }
            float r15 = (float) r15     // Catch:{ Exception -> 0x04ba }
            r17 = 1168687104(0x45a8CLASSNAME, float:5400.0)
            float r15 = r15 - r17
            int r14 = (r14 > r15 ? 1 : (r14 == r15 ? 0 : -1))
            if (r14 >= 0) goto L_0x0203
            goto L_0x01f1
        L_0x0203:
            if (r0 == 0) goto L_0x02ba
            int r0 = (r8 > r4 ? 1 : (r8 == r4 ? 0 : -1))
            if (r0 <= 0) goto L_0x02b9
            r11.readBytesCount = r8     // Catch:{ Exception -> 0x04ba }
            int r0 = r11.uploadChunkSize     // Catch:{ Exception -> 0x04ba }
            long r14 = (long) r0     // Catch:{ Exception -> 0x04ba }
            long r14 = r8 / r14
            int r0 = (int) r14     // Catch:{ Exception -> 0x04ba }
            r11.currentPartNum = r0     // Catch:{ Exception -> 0x04ba }
            if (r10 != 0) goto L_0x027d
            r0 = 0
        L_0x0216:
            long r8 = (long) r0     // Catch:{ Exception -> 0x04ba }
            long r14 = r11.readBytesCount     // Catch:{ Exception -> 0x04ba }
            int r10 = r11.uploadChunkSize     // Catch:{ Exception -> 0x04ba }
            long r4 = (long) r10     // Catch:{ Exception -> 0x04ba }
            long r14 = r14 / r4
            int r4 = (r8 > r14 ? 1 : (r8 == r14 ? 0 : -1))
            if (r4 >= 0) goto L_0x02ba
            java.io.RandomAccessFile r4 = r11.stream     // Catch:{ Exception -> 0x04ba }
            byte[] r5 = r11.readBuffer     // Catch:{ Exception -> 0x04ba }
            int r4 = r4.read(r5)     // Catch:{ Exception -> 0x04ba }
            boolean r5 = r11.isEncrypted     // Catch:{ Exception -> 0x04ba }
            if (r5 == 0) goto L_0x0237
            int r5 = r4 % 16
            if (r5 == 0) goto L_0x0237
            int r5 = r4 % 16
            int r5 = 16 - r5
            int r5 = r5 + r13
            goto L_0x0238
        L_0x0237:
            r5 = 0
        L_0x0238:
            org.telegram.tgnet.NativeByteBuffer r8 = new org.telegram.tgnet.NativeByteBuffer     // Catch:{ Exception -> 0x04ba }
            int r9 = r4 + r5
            r8.<init>((int) r9)     // Catch:{ Exception -> 0x04ba }
            int r10 = r11.uploadChunkSize     // Catch:{ Exception -> 0x04ba }
            if (r4 != r10) goto L_0x024a
            int r10 = r11.totalPartsCount     // Catch:{ Exception -> 0x04ba }
            int r14 = r11.currentPartNum     // Catch:{ Exception -> 0x04ba }
            int r14 = r14 + r1
            if (r10 != r14) goto L_0x024c
        L_0x024a:
            r11.isLastPart = r1     // Catch:{ Exception -> 0x04ba }
        L_0x024c:
            byte[] r10 = r11.readBuffer     // Catch:{ Exception -> 0x04ba }
            r8.writeBytes(r10, r13, r4)     // Catch:{ Exception -> 0x04ba }
            boolean r4 = r11.isEncrypted     // Catch:{ Exception -> 0x04ba }
            if (r4 == 0) goto L_0x0275
            r4 = 0
        L_0x0256:
            if (r4 >= r5) goto L_0x025e
            r8.writeByte((int) r13)     // Catch:{ Exception -> 0x04ba }
            int r4 = r4 + 1
            goto L_0x0256
        L_0x025e:
            java.nio.ByteBuffer r4 = r8.buffer     // Catch:{ Exception -> 0x04ba }
            byte[] r5 = r11.key     // Catch:{ Exception -> 0x04ba }
            byte[] r10 = r11.ivChange     // Catch:{ Exception -> 0x04ba }
            r22 = 1
            r23 = 1
            r24 = 0
            r19 = r4
            r20 = r5
            r21 = r10
            r25 = r9
            org.telegram.messenger.Utilities.aesIgeEncryption(r19, r20, r21, r22, r23, r24, r25)     // Catch:{ Exception -> 0x04ba }
        L_0x0275:
            r8.reuse()     // Catch:{ Exception -> 0x04ba }
            int r0 = r0 + 1
            r4 = 0
            goto L_0x0216
        L_0x027d:
            java.io.RandomAccessFile r0 = r11.stream     // Catch:{ Exception -> 0x04ba }
            r0.seek(r8)     // Catch:{ Exception -> 0x04ba }
            boolean r0 = r11.isEncrypted     // Catch:{ Exception -> 0x04ba }
            if (r0 == 0) goto L_0x02ba
            android.content.SharedPreferences r0 = r11.preferences     // Catch:{ Exception -> 0x04ba }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x04ba }
            r4.<init>()     // Catch:{ Exception -> 0x04ba }
            java.lang.String r5 = r11.fileKey     // Catch:{ Exception -> 0x04ba }
            r4.append(r5)     // Catch:{ Exception -> 0x04ba }
            java.lang.String r5 = "_ivc"
            r4.append(r5)     // Catch:{ Exception -> 0x04ba }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x04ba }
            java.lang.String r0 = r0.getString(r4, r3)     // Catch:{ Exception -> 0x04ba }
            if (r0 == 0) goto L_0x02b3
            byte[] r0 = org.telegram.messenger.Utilities.hexToBytes(r0)     // Catch:{ Exception -> 0x04ba }
            r11.ivChange = r0     // Catch:{ Exception -> 0x04ba }
            if (r0 == 0) goto L_0x02ac
            int r0 = r0.length     // Catch:{ Exception -> 0x04ba }
            if (r0 == r6) goto L_0x02ba
        L_0x02ac:
            r4 = 0
            r11.readBytesCount = r4     // Catch:{ Exception -> 0x04ba }
            r11.currentPartNum = r13     // Catch:{ Exception -> 0x04ba }
            goto L_0x02b9
        L_0x02b3:
            r4 = 0
            r11.readBytesCount = r4     // Catch:{ Exception -> 0x04ba }
            r11.currentPartNum = r13     // Catch:{ Exception -> 0x04ba }
        L_0x02b9:
            r2 = 1
        L_0x02ba:
            if (r2 == 0) goto L_0x02f6
            boolean r0 = r11.isEncrypted     // Catch:{ Exception -> 0x04ba }
            if (r0 == 0) goto L_0x02df
            byte[] r0 = new byte[r6]     // Catch:{ Exception -> 0x04ba }
            r11.iv = r0     // Catch:{ Exception -> 0x04ba }
            byte[] r2 = new byte[r6]     // Catch:{ Exception -> 0x04ba }
            r11.key = r2     // Catch:{ Exception -> 0x04ba }
            byte[] r2 = new byte[r6]     // Catch:{ Exception -> 0x04ba }
            r11.ivChange = r2     // Catch:{ Exception -> 0x04ba }
            java.security.SecureRandom r2 = org.telegram.messenger.Utilities.random     // Catch:{ Exception -> 0x04ba }
            r2.nextBytes(r0)     // Catch:{ Exception -> 0x04ba }
            java.security.SecureRandom r0 = org.telegram.messenger.Utilities.random     // Catch:{ Exception -> 0x04ba }
            byte[] r2 = r11.key     // Catch:{ Exception -> 0x04ba }
            r0.nextBytes(r2)     // Catch:{ Exception -> 0x04ba }
            byte[] r0 = r11.iv     // Catch:{ Exception -> 0x04ba }
            byte[] r2 = r11.ivChange     // Catch:{ Exception -> 0x04ba }
            java.lang.System.arraycopy(r0, r13, r2, r13, r6)     // Catch:{ Exception -> 0x04ba }
        L_0x02df:
            java.security.SecureRandom r0 = org.telegram.messenger.Utilities.random     // Catch:{ Exception -> 0x04ba }
            long r4 = r0.nextLong()     // Catch:{ Exception -> 0x04ba }
            r11.currentFileId = r4     // Catch:{ Exception -> 0x04ba }
            boolean r0 = r11.nextPartFirst     // Catch:{ Exception -> 0x04ba }
            if (r0 != 0) goto L_0x02f6
            boolean r0 = r11.uploadFirstPartLater     // Catch:{ Exception -> 0x04ba }
            if (r0 != 0) goto L_0x02f6
            int r0 = r11.estimatedSize     // Catch:{ Exception -> 0x04ba }
            if (r0 != 0) goto L_0x02f6
            r26.storeFileUploadInfo()     // Catch:{ Exception -> 0x04ba }
        L_0x02f6:
            boolean r0 = r11.isEncrypted     // Catch:{ Exception -> 0x04ba }
            if (r0 == 0) goto L_0x032b
            java.lang.String r0 = "MD5"
            java.security.MessageDigest r0 = java.security.MessageDigest.getInstance(r0)     // Catch:{ Exception -> 0x0327 }
            byte[] r2 = new byte[r7]     // Catch:{ Exception -> 0x0327 }
            byte[] r4 = r11.key     // Catch:{ Exception -> 0x0327 }
            java.lang.System.arraycopy(r4, r13, r2, r13, r6)     // Catch:{ Exception -> 0x0327 }
            byte[] r4 = r11.iv     // Catch:{ Exception -> 0x0327 }
            java.lang.System.arraycopy(r4, r13, r2, r6, r6)     // Catch:{ Exception -> 0x0327 }
            byte[] r0 = r0.digest(r2)     // Catch:{ Exception -> 0x0327 }
            r2 = 0
        L_0x0311:
            if (r2 >= r12) goto L_0x032b
            int r4 = r11.fingerprint     // Catch:{ Exception -> 0x0327 }
            byte r5 = r0[r2]     // Catch:{ Exception -> 0x0327 }
            int r7 = r2 + 4
            byte r7 = r0[r7]     // Catch:{ Exception -> 0x0327 }
            r5 = r5 ^ r7
            r5 = r5 & 255(0xff, float:3.57E-43)
            int r7 = r2 * 8
            int r5 = r5 << r7
            r4 = r4 | r5
            r11.fingerprint = r4     // Catch:{ Exception -> 0x0327 }
            int r2 = r2 + 1
            goto L_0x0311
        L_0x0327:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ Exception -> 0x04ba }
        L_0x032b:
            long r4 = r11.readBytesCount     // Catch:{ Exception -> 0x04ba }
            r11.uploadedBytesCount = r4     // Catch:{ Exception -> 0x04ba }
            int r0 = r11.currentPartNum     // Catch:{ Exception -> 0x04ba }
            r11.lastSavedPartNum = r0     // Catch:{ Exception -> 0x04ba }
            boolean r0 = r11.uploadFirstPartLater     // Catch:{ Exception -> 0x04ba }
            if (r0 == 0) goto L_0x0361
            boolean r0 = r11.isBigFile     // Catch:{ Exception -> 0x04ba }
            if (r0 == 0) goto L_0x0349
            java.io.RandomAccessFile r0 = r11.stream     // Catch:{ Exception -> 0x04ba }
            int r2 = r11.uploadChunkSize     // Catch:{ Exception -> 0x04ba }
            long r4 = (long) r2     // Catch:{ Exception -> 0x04ba }
            r0.seek(r4)     // Catch:{ Exception -> 0x04ba }
            int r0 = r11.uploadChunkSize     // Catch:{ Exception -> 0x04ba }
            long r4 = (long) r0     // Catch:{ Exception -> 0x04ba }
            r11.readBytesCount = r4     // Catch:{ Exception -> 0x04ba }
            goto L_0x0352
        L_0x0349:
            java.io.RandomAccessFile r0 = r11.stream     // Catch:{ Exception -> 0x04ba }
            r4 = 1024(0x400, double:5.06E-321)
            r0.seek(r4)     // Catch:{ Exception -> 0x04ba }
            r11.readBytesCount = r4     // Catch:{ Exception -> 0x04ba }
        L_0x0352:
            r11.currentPartNum = r1     // Catch:{ Exception -> 0x04ba }
            goto L_0x0361
        L_0x0355:
            java.lang.Exception r0 = new java.lang.Exception     // Catch:{ Exception -> 0x04ba }
            r0.<init>(r8)     // Catch:{ Exception -> 0x04ba }
            throw r0     // Catch:{ Exception -> 0x04ba }
        L_0x035b:
            java.lang.Exception r0 = new java.lang.Exception     // Catch:{ Exception -> 0x04ba }
            r0.<init>(r8)     // Catch:{ Exception -> 0x04ba }
            throw r0     // Catch:{ Exception -> 0x04ba }
        L_0x0361:
            int r0 = r11.estimatedSize     // Catch:{ Exception -> 0x04ba }
            if (r0 == 0) goto L_0x0372
            long r4 = r11.readBytesCount     // Catch:{ Exception -> 0x04ba }
            int r0 = r11.uploadChunkSize     // Catch:{ Exception -> 0x04ba }
            long r7 = (long) r0     // Catch:{ Exception -> 0x04ba }
            long r4 = r4 + r7
            long r7 = r11.availableSize     // Catch:{ Exception -> 0x04ba }
            int r0 = (r4 > r7 ? 1 : (r4 == r7 ? 0 : -1))
            if (r0 <= 0) goto L_0x0372
            return
        L_0x0372:
            boolean r0 = r11.nextPartFirst     // Catch:{ Exception -> 0x04ba }
            if (r0 == 0) goto L_0x0397
            java.io.RandomAccessFile r0 = r11.stream     // Catch:{ Exception -> 0x04ba }
            r4 = 0
            r0.seek(r4)     // Catch:{ Exception -> 0x04ba }
            boolean r0 = r11.isBigFile     // Catch:{ Exception -> 0x04ba }
            if (r0 == 0) goto L_0x038a
            java.io.RandomAccessFile r0 = r11.stream     // Catch:{ Exception -> 0x04ba }
            byte[] r2 = r11.readBuffer     // Catch:{ Exception -> 0x04ba }
            int r0 = r0.read(r2)     // Catch:{ Exception -> 0x04ba }
            goto L_0x0394
        L_0x038a:
            java.io.RandomAccessFile r0 = r11.stream     // Catch:{ Exception -> 0x04ba }
            byte[] r2 = r11.readBuffer     // Catch:{ Exception -> 0x04ba }
            r4 = 1024(0x400, float:1.435E-42)
            int r0 = r0.read(r2, r13, r4)     // Catch:{ Exception -> 0x04ba }
        L_0x0394:
            r11.currentPartNum = r13     // Catch:{ Exception -> 0x04ba }
            goto L_0x039f
        L_0x0397:
            java.io.RandomAccessFile r0 = r11.stream     // Catch:{ Exception -> 0x04ba }
            byte[] r2 = r11.readBuffer     // Catch:{ Exception -> 0x04ba }
            int r0 = r0.read(r2)     // Catch:{ Exception -> 0x04ba }
        L_0x039f:
            r7 = r0
            r0 = -1
            if (r7 != r0) goto L_0x03a4
            return
        L_0x03a4:
            boolean r2 = r11.isEncrypted     // Catch:{ Exception -> 0x04ba }
            if (r2 == 0) goto L_0x03b2
            int r2 = r7 % 16
            if (r2 == 0) goto L_0x03b2
            int r2 = r7 % 16
            int r2 = 16 - r2
            int r2 = r2 + r13
            goto L_0x03b3
        L_0x03b2:
            r2 = 0
        L_0x03b3:
            org.telegram.tgnet.NativeByteBuffer r4 = new org.telegram.tgnet.NativeByteBuffer     // Catch:{ Exception -> 0x04ba }
            int r5 = r7 + r2
            r4.<init>((int) r5)     // Catch:{ Exception -> 0x04ba }
            boolean r8 = r11.nextPartFirst     // Catch:{ Exception -> 0x04ba }
            if (r8 != 0) goto L_0x03cd
            int r8 = r11.uploadChunkSize     // Catch:{ Exception -> 0x04ba }
            if (r7 != r8) goto L_0x03cd
            int r8 = r11.estimatedSize     // Catch:{ Exception -> 0x04ba }
            if (r8 != 0) goto L_0x03d8
            int r8 = r11.totalPartsCount     // Catch:{ Exception -> 0x04ba }
            int r9 = r11.currentPartNum     // Catch:{ Exception -> 0x04ba }
            int r9 = r9 + r1
            if (r8 != r9) goto L_0x03d8
        L_0x03cd:
            boolean r8 = r11.uploadFirstPartLater     // Catch:{ Exception -> 0x04ba }
            if (r8 == 0) goto L_0x03d6
            r11.nextPartFirst = r1     // Catch:{ Exception -> 0x04ba }
            r11.uploadFirstPartLater = r13     // Catch:{ Exception -> 0x04ba }
            goto L_0x03d8
        L_0x03d6:
            r11.isLastPart = r1     // Catch:{ Exception -> 0x04ba }
        L_0x03d8:
            byte[] r8 = r11.readBuffer     // Catch:{ Exception -> 0x04ba }
            r4.writeBytes(r8, r13, r7)     // Catch:{ Exception -> 0x04ba }
            boolean r8 = r11.isEncrypted     // Catch:{ Exception -> 0x04ba }
            if (r8 == 0) goto L_0x0411
            r3 = 0
        L_0x03e2:
            if (r3 >= r2) goto L_0x03ea
            r4.writeByte((int) r13)     // Catch:{ Exception -> 0x04ba }
            int r3 = r3 + 1
            goto L_0x03e2
        L_0x03ea:
            java.nio.ByteBuffer r14 = r4.buffer     // Catch:{ Exception -> 0x04ba }
            byte[] r15 = r11.key     // Catch:{ Exception -> 0x04ba }
            byte[] r2 = r11.ivChange     // Catch:{ Exception -> 0x04ba }
            r17 = 1
            r18 = 1
            r19 = 0
            r16 = r2
            r20 = r5
            org.telegram.messenger.Utilities.aesIgeEncryption(r14, r15, r16, r17, r18, r19, r20)     // Catch:{ Exception -> 0x04ba }
            java.util.ArrayList<byte[]> r2 = r11.freeRequestIvs     // Catch:{ Exception -> 0x04ba }
            java.lang.Object r2 = r2.get(r13)     // Catch:{ Exception -> 0x04ba }
            byte[] r2 = (byte[]) r2     // Catch:{ Exception -> 0x04ba }
            byte[] r3 = r11.ivChange     // Catch:{ Exception -> 0x04ba }
            java.lang.System.arraycopy(r3, r13, r2, r13, r6)     // Catch:{ Exception -> 0x04ba }
            java.util.ArrayList<byte[]> r3 = r11.freeRequestIvs     // Catch:{ Exception -> 0x04ba }
            r3.remove(r13)     // Catch:{ Exception -> 0x04ba }
            r5 = r2
            goto L_0x0412
        L_0x0411:
            r5 = r3
        L_0x0412:
            boolean r2 = r11.isBigFile     // Catch:{ Exception -> 0x04ba }
            if (r2 == 0) goto L_0x0433
            org.telegram.tgnet.TLRPC$TL_upload_saveBigFilePart r2 = new org.telegram.tgnet.TLRPC$TL_upload_saveBigFilePart     // Catch:{ Exception -> 0x04ba }
            r2.<init>()     // Catch:{ Exception -> 0x04ba }
            int r3 = r11.currentPartNum     // Catch:{ Exception -> 0x04ba }
            r2.file_part = r3     // Catch:{ Exception -> 0x04ba }
            long r8 = r11.currentFileId     // Catch:{ Exception -> 0x04ba }
            r2.file_id = r8     // Catch:{ Exception -> 0x04ba }
            int r6 = r11.estimatedSize     // Catch:{ Exception -> 0x04ba }
            if (r6 == 0) goto L_0x042a
            r2.file_total_parts = r0     // Catch:{ Exception -> 0x04ba }
            goto L_0x042e
        L_0x042a:
            int r0 = r11.totalPartsCount     // Catch:{ Exception -> 0x04ba }
            r2.file_total_parts = r0     // Catch:{ Exception -> 0x04ba }
        L_0x042e:
            r2.bytes = r4     // Catch:{ Exception -> 0x04ba }
            r15 = r2
            r8 = r3
            goto L_0x0444
        L_0x0433:
            org.telegram.tgnet.TLRPC$TL_upload_saveFilePart r2 = new org.telegram.tgnet.TLRPC$TL_upload_saveFilePart     // Catch:{ Exception -> 0x04ba }
            r2.<init>()     // Catch:{ Exception -> 0x04ba }
            int r0 = r11.currentPartNum     // Catch:{ Exception -> 0x04ba }
            r2.file_part = r0     // Catch:{ Exception -> 0x04ba }
            long r8 = r11.currentFileId     // Catch:{ Exception -> 0x04ba }
            r2.file_id = r8     // Catch:{ Exception -> 0x04ba }
            r2.bytes = r4     // Catch:{ Exception -> 0x04ba }
            r8 = r0
            r15 = r2
        L_0x0444:
            boolean r0 = r11.isLastPart     // Catch:{ Exception -> 0x04ba }
            if (r0 == 0) goto L_0x045a
            boolean r0 = r11.nextPartFirst     // Catch:{ Exception -> 0x04ba }
            if (r0 == 0) goto L_0x045a
            r11.nextPartFirst = r13     // Catch:{ Exception -> 0x04ba }
            int r0 = r11.totalPartsCount     // Catch:{ Exception -> 0x04ba }
            int r0 = r0 - r1
            r11.currentPartNum = r0     // Catch:{ Exception -> 0x04ba }
            java.io.RandomAccessFile r0 = r11.stream     // Catch:{ Exception -> 0x04ba }
            long r2 = r11.totalFileSize     // Catch:{ Exception -> 0x04ba }
            r0.seek(r2)     // Catch:{ Exception -> 0x04ba }
        L_0x045a:
            long r2 = r11.readBytesCount     // Catch:{ Exception -> 0x04ba }
            long r9 = (long) r7     // Catch:{ Exception -> 0x04ba }
            long r2 = r2 + r9
            r11.readBytesCount = r2     // Catch:{ Exception -> 0x04ba }
            int r0 = r11.currentPartNum
            int r0 = r0 + r1
            r11.currentPartNum = r0
            int r0 = r11.currentUploadRequetsCount
            int r0 = r0 + r1
            r11.currentUploadRequetsCount = r0
            int r0 = r11.requestNum
            int r1 = r0 + 1
            r11.requestNum = r1
            int r1 = r8 + r7
            long r9 = (long) r1
            int r1 = r15.getObjectSize()
            int r4 = r1 + 4
            int r3 = r11.operationGuid
            boolean r1 = r11.slowNetwork
            if (r1 == 0) goto L_0x0482
            r21 = 4
            goto L_0x0489
        L_0x0482:
            int r1 = r0 % 4
            int r1 = r1 << 16
            r1 = r1 | r12
            r21 = r1
        L_0x0489:
            int r1 = r11.currentAccount
            org.telegram.tgnet.ConnectionsManager r14 = org.telegram.tgnet.ConnectionsManager.getInstance(r1)
            org.telegram.messenger.FileUploadOperation$$ExternalSyntheticLambda5 r16 = new org.telegram.messenger.FileUploadOperation$$ExternalSyntheticLambda5
            r1 = r16
            r2 = r26
            r6 = r0
            r1.<init>(r2, r3, r4, r5, r6, r7, r8, r9)
            r17 = 0
            org.telegram.messenger.FileUploadOperation$$ExternalSyntheticLambda6 r1 = new org.telegram.messenger.FileUploadOperation$$ExternalSyntheticLambda6
            r1.<init>(r11)
            boolean r2 = r11.forceSmallFile
            if (r2 == 0) goto L_0x04a7
            r19 = 4
            goto L_0x04a9
        L_0x04a7:
            r19 = 0
        L_0x04a9:
            r20 = 2147483647(0x7fffffff, float:NaN)
            r22 = 1
            r18 = r1
            int r1 = r14.sendRequest(r15, r16, r17, r18, r19, r20, r21, r22)
            android.util.SparseIntArray r2 = r11.requestTokens
            r2.put(r0, r1)
            return
        L_0x04ba:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            r11.state = r12
            org.telegram.messenger.FileUploadOperation$FileUploadOperationDelegate r0 = r11.delegate
            r0.didFailedUploadingFile(r11)
            r26.cleanup()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.FileUploadOperation.startUploadRequest():void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$startUploadRequest$4(int i, int i2, byte[] bArr, int i3, int i4, int i5, long j, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        long j2;
        TLRPC$InputEncryptedFile tLRPC$InputEncryptedFile;
        TLRPC$InputFile tLRPC$InputFile;
        int i6 = i2;
        byte[] bArr2 = bArr;
        int i7 = i5;
        TLObject tLObject2 = tLObject;
        if (i == this.operationGuid) {
            int currentNetworkType = tLObject2 != null ? tLObject2.networkType : ApplicationLoader.getCurrentNetworkType();
            int i8 = this.currentType;
            if (i8 == 50331648) {
                StatsController.getInstance(this.currentAccount).incrementSentBytesCount(currentNetworkType, 3, (long) i6);
            } else if (i8 == 33554432) {
                StatsController.getInstance(this.currentAccount).incrementSentBytesCount(currentNetworkType, 2, (long) i6);
            } else if (i8 == 16777216) {
                StatsController.getInstance(this.currentAccount).incrementSentBytesCount(currentNetworkType, 4, (long) i6);
            } else if (i8 == 67108864) {
                StatsController.getInstance(this.currentAccount).incrementSentBytesCount(currentNetworkType, 5, (long) i6);
            }
            if (bArr2 != null) {
                this.freeRequestIvs.add(bArr2);
            }
            this.requestTokens.delete(i3);
            if (!(tLObject2 instanceof TLRPC$TL_boolTrue)) {
                this.state = 4;
                this.delegate.didFailedUploadingFile(this);
                cleanup();
            } else if (this.state == 1) {
                this.uploadedBytesCount += (long) i4;
                int i9 = this.estimatedSize;
                if (i9 != 0) {
                    j2 = Math.max(this.availableSize, (long) i9);
                } else {
                    j2 = this.totalFileSize;
                }
                this.delegate.didChangedUploadProgress(this, this.uploadedBytesCount, j2);
                int i10 = this.currentUploadRequetsCount - 1;
                this.currentUploadRequetsCount = i10;
                if (this.isLastPart && i10 == 0 && this.state == 1) {
                    this.state = 3;
                    if (this.key == null) {
                        if (this.isBigFile) {
                            tLRPC$InputFile = new TLRPC$TL_inputFileBig();
                        } else {
                            tLRPC$InputFile = new TLRPC$TL_inputFile();
                            tLRPC$InputFile.md5_checksum = "";
                        }
                        tLRPC$InputFile.parts = this.currentPartNum;
                        tLRPC$InputFile.id = this.currentFileId;
                        String str = this.uploadingFilePath;
                        tLRPC$InputFile.name = str.substring(str.lastIndexOf("/") + 1);
                        this.delegate.didFinishUploadingFile(this, tLRPC$InputFile, (TLRPC$InputEncryptedFile) null, (byte[]) null, (byte[]) null);
                        cleanup();
                    } else {
                        if (this.isBigFile) {
                            tLRPC$InputEncryptedFile = new TLRPC$TL_inputEncryptedFileBigUploaded();
                        } else {
                            tLRPC$InputEncryptedFile = new TLRPC$TL_inputEncryptedFileUploaded();
                            tLRPC$InputEncryptedFile.md5_checksum = "";
                        }
                        tLRPC$InputEncryptedFile.parts = this.currentPartNum;
                        tLRPC$InputEncryptedFile.id = this.currentFileId;
                        tLRPC$InputEncryptedFile.key_fingerprint = this.fingerprint;
                        this.delegate.didFinishUploadingFile(this, (TLRPC$InputFile) null, tLRPC$InputEncryptedFile, this.key, this.iv);
                        cleanup();
                    }
                    int i11 = this.currentType;
                    if (i11 == 50331648) {
                        StatsController.getInstance(this.currentAccount).incrementSentItemsCount(ApplicationLoader.getCurrentNetworkType(), 3, 1);
                    } else if (i11 == 33554432) {
                        StatsController.getInstance(this.currentAccount).incrementSentItemsCount(ApplicationLoader.getCurrentNetworkType(), 2, 1);
                    } else if (i11 == 16777216) {
                        StatsController.getInstance(this.currentAccount).incrementSentItemsCount(ApplicationLoader.getCurrentNetworkType(), 4, 1);
                    } else if (i11 == 67108864) {
                        StatsController.getInstance(this.currentAccount).incrementSentItemsCount(ApplicationLoader.getCurrentNetworkType(), 5, 1);
                    }
                } else if (i10 < this.maxRequestsCount) {
                    if (this.estimatedSize == 0 && !this.uploadFirstPartLater && !this.nextPartFirst) {
                        if (this.saveInfoTimes >= 4) {
                            this.saveInfoTimes = 0;
                        }
                        int i12 = this.lastSavedPartNum;
                        if (i7 == i12) {
                            this.lastSavedPartNum = i12 + 1;
                            long j3 = j;
                            while (true) {
                                UploadCachedResult uploadCachedResult = this.cachedResults.get(this.lastSavedPartNum);
                                if (uploadCachedResult == null) {
                                    break;
                                }
                                j3 = uploadCachedResult.bytesOffset;
                                bArr2 = uploadCachedResult.iv;
                                this.cachedResults.remove(this.lastSavedPartNum);
                                this.lastSavedPartNum++;
                            }
                            boolean z = this.isBigFile;
                            if ((z && j3 % 1048576 == 0) || (!z && this.saveInfoTimes == 0)) {
                                SharedPreferences.Editor edit = this.preferences.edit();
                                edit.putLong(this.fileKey + "_uploaded", j3);
                                if (this.isEncrypted) {
                                    edit.putString(this.fileKey + "_ivc", Utilities.bytesToHex(bArr2));
                                }
                                edit.commit();
                            }
                        } else {
                            UploadCachedResult uploadCachedResult2 = new UploadCachedResult();
                            long unused = uploadCachedResult2.bytesOffset = j;
                            if (bArr2 != null) {
                                byte[] unused2 = uploadCachedResult2.iv = new byte[32];
                                System.arraycopy(bArr2, 0, uploadCachedResult2.iv, 0, 32);
                            }
                            this.cachedResults.put(i7, uploadCachedResult2);
                        }
                        this.saveInfoTimes++;
                    }
                    startUploadRequest();
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$startUploadRequest$6() {
        Utilities.stageQueue.postRunnable(new FileUploadOperation$$ExternalSyntheticLambda1(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$startUploadRequest$5() {
        if (this.currentUploadRequetsCount < this.maxRequestsCount) {
            startUploadRequest();
        }
    }
}
