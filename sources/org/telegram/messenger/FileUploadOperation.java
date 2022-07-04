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

    public FileUploadOperation(int i, String str, boolean z, long j, int i2) {
        this.currentAccount = i;
        this.uploadingFilePath = str;
        this.isEncrypted = z;
        this.estimatedSize = j;
        this.currentType = i2;
        this.uploadFirstPartLater = j != 0 && !z;
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

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v0, resolved type: org.telegram.tgnet.TLRPC$TL_upload_saveFilePart} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v1, resolved type: org.telegram.tgnet.TLRPC$TL_upload_saveFilePart} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v11, resolved type: org.telegram.tgnet.TLRPC$TL_upload_saveBigFilePart} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r18v2, resolved type: org.telegram.tgnet.TLRPC$TL_upload_saveFilePart} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:129:0x02ed A[Catch:{ Exception -> 0x0508 }] */
    /* JADX WARNING: Removed duplicated region for block: B:141:0x032e  */
    /* JADX WARNING: Removed duplicated region for block: B:153:0x036d A[Catch:{ Exception -> 0x0508 }] */
    /* JADX WARNING: Removed duplicated region for block: B:92:0x0235 A[Catch:{ Exception -> 0x0508 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void startUploadRequest() {
        /*
            r27 = this;
            r11 = r27
            int r0 = r11.state
            r1 = 1
            if (r0 == r1) goto L_0x0008
            return
        L_0x0008:
            r11.started = r1     // Catch:{ Exception -> 0x0508 }
            java.io.RandomAccessFile r0 = r11.stream     // Catch:{ Exception -> 0x0508 }
            r2 = 1024(0x400, float:1.435E-42)
            r3 = 0
            r4 = 0
            r6 = 32
            r13 = 0
            if (r0 != 0) goto L_0x0397
            java.io.File r7 = new java.io.File     // Catch:{ Exception -> 0x0508 }
            java.lang.String r0 = r11.uploadingFilePath     // Catch:{ Exception -> 0x0508 }
            r7.<init>(r0)     // Catch:{ Exception -> 0x0508 }
            android.net.Uri r0 = android.net.Uri.fromFile(r7)     // Catch:{ Exception -> 0x0508 }
            boolean r0 = org.telegram.messenger.AndroidUtilities.isInternalUri((android.net.Uri) r0)     // Catch:{ Exception -> 0x0508 }
            java.lang.String r8 = "trying to upload internal file"
            if (r0 != 0) goto L_0x0391
            java.io.RandomAccessFile r0 = new java.io.RandomAccessFile     // Catch:{ Exception -> 0x0508 }
            java.lang.String r9 = "r"
            r0.<init>(r7, r9)     // Catch:{ Exception -> 0x0508 }
            r11.stream = r0     // Catch:{ Exception -> 0x0508 }
            java.lang.Class<java.io.FileDescriptor> r0 = java.io.FileDescriptor.class
            java.lang.String r9 = "getInt$"
            java.lang.Class[] r10 = new java.lang.Class[r13]     // Catch:{ all -> 0x0053 }
            java.lang.reflect.Method r0 = r0.getDeclaredMethod(r9, r10)     // Catch:{ all -> 0x0053 }
            java.io.RandomAccessFile r9 = r11.stream     // Catch:{ all -> 0x0053 }
            java.io.FileDescriptor r9 = r9.getFD()     // Catch:{ all -> 0x0053 }
            java.lang.Object[] r10 = new java.lang.Object[r13]     // Catch:{ all -> 0x0053 }
            java.lang.Object r0 = r0.invoke(r9, r10)     // Catch:{ all -> 0x0053 }
            java.lang.Integer r0 = (java.lang.Integer) r0     // Catch:{ all -> 0x0053 }
            int r0 = r0.intValue()     // Catch:{ all -> 0x0053 }
            boolean r0 = org.telegram.messenger.AndroidUtilities.isInternalUri((int) r0)     // Catch:{ all -> 0x0053 }
            goto L_0x0058
        L_0x0053:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ Exception -> 0x0508 }
            r0 = 0
        L_0x0058:
            if (r0 != 0) goto L_0x038b
            long r8 = r11.estimatedSize     // Catch:{ Exception -> 0x0508 }
            int r0 = (r8 > r4 ? 1 : (r8 == r4 ? 0 : -1))
            if (r0 == 0) goto L_0x0063
            r11.totalFileSize = r8     // Catch:{ Exception -> 0x0508 }
            goto L_0x0069
        L_0x0063:
            long r7 = r7.length()     // Catch:{ Exception -> 0x0508 }
            r11.totalFileSize = r7     // Catch:{ Exception -> 0x0508 }
        L_0x0069:
            boolean r0 = r11.forceSmallFile     // Catch:{ Exception -> 0x0508 }
            if (r0 != 0) goto L_0x0078
            long r7 = r11.totalFileSize     // Catch:{ Exception -> 0x0508 }
            r9 = 10485760(0xa00000, double:5.180654E-317)
            int r0 = (r7 > r9 ? 1 : (r7 == r9 ? 0 : -1))
            if (r0 <= 0) goto L_0x0078
            r11.isBigFile = r1     // Catch:{ Exception -> 0x0508 }
        L_0x0078:
            int r0 = r11.currentAccount     // Catch:{ Exception -> 0x0508 }
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)     // Catch:{ Exception -> 0x0508 }
            int r0 = r0.uploadMaxFileParts     // Catch:{ Exception -> 0x0508 }
            long r7 = (long) r0     // Catch:{ Exception -> 0x0508 }
            int r0 = r11.currentAccount     // Catch:{ Exception -> 0x0508 }
            org.telegram.messenger.AccountInstance r0 = org.telegram.messenger.AccountInstance.getInstance(r0)     // Catch:{ Exception -> 0x0508 }
            org.telegram.messenger.UserConfig r0 = r0.getUserConfig()     // Catch:{ Exception -> 0x0508 }
            boolean r0 = r0.isPremium()     // Catch:{ Exception -> 0x0508 }
            if (r0 == 0) goto L_0x00a3
            long r9 = r11.totalFileSize     // Catch:{ Exception -> 0x0508 }
            r14 = 2097152000(0x7d000000, double:1.0361307573E-314)
            int r0 = (r9 > r14 ? 1 : (r9 == r14 ? 0 : -1))
            if (r0 <= 0) goto L_0x00a3
            int r0 = r11.currentAccount     // Catch:{ Exception -> 0x0508 }
            org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)     // Catch:{ Exception -> 0x0508 }
            int r0 = r0.uploadMaxFilePartsPremium     // Catch:{ Exception -> 0x0508 }
            long r7 = (long) r0     // Catch:{ Exception -> 0x0508 }
        L_0x00a3:
            boolean r0 = r11.slowNetwork     // Catch:{ Exception -> 0x0508 }
            if (r0 == 0) goto L_0x00aa
            r9 = 32
            goto L_0x00ac
        L_0x00aa:
            r9 = 128(0x80, double:6.32E-322)
        L_0x00ac:
            long r14 = r11.totalFileSize     // Catch:{ Exception -> 0x0508 }
            r12 = 1024(0x400, double:5.06E-321)
            long r7 = r7 * r12
            long r14 = r14 + r7
            r17 = 1
            long r14 = r14 - r17
            long r14 = r14 / r7
            long r7 = java.lang.Math.max(r9, r14)     // Catch:{ Exception -> 0x0508 }
            int r0 = (int) r7     // Catch:{ Exception -> 0x0508 }
            r11.uploadChunkSize = r0     // Catch:{ Exception -> 0x0508 }
            int r0 = r2 % r0
            r7 = 64
            if (r0 == 0) goto L_0x00d0
            r0 = 64
        L_0x00c7:
            int r8 = r11.uploadChunkSize     // Catch:{ Exception -> 0x0508 }
            if (r8 <= r0) goto L_0x00ce
            int r0 = r0 * 2
            goto L_0x00c7
        L_0x00ce:
            r11.uploadChunkSize = r0     // Catch:{ Exception -> 0x0508 }
        L_0x00d0:
            boolean r0 = r11.slowNetwork     // Catch:{ Exception -> 0x0508 }
            if (r0 == 0) goto L_0x00d7
            r0 = 32
            goto L_0x00d9
        L_0x00d7:
            r0 = 2048(0x800, float:2.87E-42)
        L_0x00d9:
            int r8 = r11.uploadChunkSize     // Catch:{ Exception -> 0x0508 }
            int r0 = r0 / r8
            int r0 = java.lang.Math.max(r1, r0)     // Catch:{ Exception -> 0x0508 }
            r11.maxRequestsCount = r0     // Catch:{ Exception -> 0x0508 }
            boolean r0 = r11.isEncrypted     // Catch:{ Exception -> 0x0508 }
            if (r0 == 0) goto L_0x00fe
            java.util.ArrayList r0 = new java.util.ArrayList     // Catch:{ Exception -> 0x0508 }
            int r8 = r11.maxRequestsCount     // Catch:{ Exception -> 0x0508 }
            r0.<init>(r8)     // Catch:{ Exception -> 0x0508 }
            r11.freeRequestIvs = r0     // Catch:{ Exception -> 0x0508 }
            r0 = 0
        L_0x00f0:
            int r8 = r11.maxRequestsCount     // Catch:{ Exception -> 0x0508 }
            if (r0 >= r8) goto L_0x00fe
            java.util.ArrayList<byte[]> r8 = r11.freeRequestIvs     // Catch:{ Exception -> 0x0508 }
            byte[] r9 = new byte[r6]     // Catch:{ Exception -> 0x0508 }
            r8.add(r9)     // Catch:{ Exception -> 0x0508 }
            int r0 = r0 + 1
            goto L_0x00f0
        L_0x00fe:
            int r0 = r11.uploadChunkSize     // Catch:{ Exception -> 0x0508 }
            int r0 = r0 * 1024
            r11.uploadChunkSize = r0     // Catch:{ Exception -> 0x0508 }
            r27.calcTotalPartsCount()     // Catch:{ Exception -> 0x0508 }
            int r0 = r11.uploadChunkSize     // Catch:{ Exception -> 0x0508 }
            byte[] r0 = new byte[r0]     // Catch:{ Exception -> 0x0508 }
            r11.readBuffer = r0     // Catch:{ Exception -> 0x0508 }
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0508 }
            r0.<init>()     // Catch:{ Exception -> 0x0508 }
            java.lang.String r8 = r11.uploadingFilePath     // Catch:{ Exception -> 0x0508 }
            r0.append(r8)     // Catch:{ Exception -> 0x0508 }
            boolean r8 = r11.isEncrypted     // Catch:{ Exception -> 0x0508 }
            if (r8 == 0) goto L_0x011e
            java.lang.String r8 = "enc"
            goto L_0x0120
        L_0x011e:
            java.lang.String r8 = ""
        L_0x0120:
            r0.append(r8)     // Catch:{ Exception -> 0x0508 }
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x0508 }
            java.lang.String r0 = org.telegram.messenger.Utilities.MD5(r0)     // Catch:{ Exception -> 0x0508 }
            r11.fileKey = r0     // Catch:{ Exception -> 0x0508 }
            android.content.SharedPreferences r0 = r11.preferences     // Catch:{ Exception -> 0x0508 }
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0508 }
            r8.<init>()     // Catch:{ Exception -> 0x0508 }
            java.lang.String r9 = r11.fileKey     // Catch:{ Exception -> 0x0508 }
            r8.append(r9)     // Catch:{ Exception -> 0x0508 }
            java.lang.String r9 = "_size"
            r8.append(r9)     // Catch:{ Exception -> 0x0508 }
            java.lang.String r8 = r8.toString()     // Catch:{ Exception -> 0x0508 }
            long r8 = r0.getLong(r8, r4)     // Catch:{ Exception -> 0x0508 }
            long r14 = java.lang.System.currentTimeMillis()     // Catch:{ Exception -> 0x0508 }
            r17 = 1000(0x3e8, double:4.94E-321)
            long r14 = r14 / r17
            int r0 = (int) r14     // Catch:{ Exception -> 0x0508 }
            r11.uploadStartTime = r0     // Catch:{ Exception -> 0x0508 }
            boolean r0 = r11.uploadFirstPartLater     // Catch:{ Exception -> 0x0508 }
            if (r0 != 0) goto L_0x02ea
            boolean r0 = r11.nextPartFirst     // Catch:{ Exception -> 0x0508 }
            if (r0 != 0) goto L_0x02ea
            long r14 = r11.estimatedSize     // Catch:{ Exception -> 0x0508 }
            int r0 = (r14 > r4 ? 1 : (r14 == r4 ? 0 : -1))
            if (r0 != 0) goto L_0x02ea
            long r14 = r11.totalFileSize     // Catch:{ Exception -> 0x0508 }
            int r0 = (r8 > r14 ? 1 : (r8 == r14 ? 0 : -1))
            if (r0 != 0) goto L_0x02ea
            android.content.SharedPreferences r0 = r11.preferences     // Catch:{ Exception -> 0x0508 }
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0508 }
            r8.<init>()     // Catch:{ Exception -> 0x0508 }
            java.lang.String r9 = r11.fileKey     // Catch:{ Exception -> 0x0508 }
            r8.append(r9)     // Catch:{ Exception -> 0x0508 }
            java.lang.String r9 = "_id"
            r8.append(r9)     // Catch:{ Exception -> 0x0508 }
            java.lang.String r8 = r8.toString()     // Catch:{ Exception -> 0x0508 }
            long r8 = r0.getLong(r8, r4)     // Catch:{ Exception -> 0x0508 }
            r11.currentFileId = r8     // Catch:{ Exception -> 0x0508 }
            android.content.SharedPreferences r0 = r11.preferences     // Catch:{ Exception -> 0x0508 }
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0508 }
            r8.<init>()     // Catch:{ Exception -> 0x0508 }
            java.lang.String r9 = r11.fileKey     // Catch:{ Exception -> 0x0508 }
            r8.append(r9)     // Catch:{ Exception -> 0x0508 }
            java.lang.String r9 = "_time"
            r8.append(r9)     // Catch:{ Exception -> 0x0508 }
            java.lang.String r8 = r8.toString()     // Catch:{ Exception -> 0x0508 }
            r9 = 0
            int r0 = r0.getInt(r8, r9)     // Catch:{ Exception -> 0x0508 }
            android.content.SharedPreferences r8 = r11.preferences     // Catch:{ Exception -> 0x0508 }
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0508 }
            r9.<init>()     // Catch:{ Exception -> 0x0508 }
            java.lang.String r10 = r11.fileKey     // Catch:{ Exception -> 0x0508 }
            r9.append(r10)     // Catch:{ Exception -> 0x0508 }
            java.lang.String r10 = "_uploaded"
            r9.append(r10)     // Catch:{ Exception -> 0x0508 }
            java.lang.String r9 = r9.toString()     // Catch:{ Exception -> 0x0508 }
            long r8 = r8.getLong(r9, r4)     // Catch:{ Exception -> 0x0508 }
            boolean r10 = r11.isEncrypted     // Catch:{ Exception -> 0x0508 }
            if (r10 == 0) goto L_0x0210
            android.content.SharedPreferences r10 = r11.preferences     // Catch:{ Exception -> 0x0508 }
            java.lang.StringBuilder r14 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0508 }
            r14.<init>()     // Catch:{ Exception -> 0x0508 }
            java.lang.String r15 = r11.fileKey     // Catch:{ Exception -> 0x0508 }
            r14.append(r15)     // Catch:{ Exception -> 0x0508 }
            java.lang.String r15 = "_iv"
            r14.append(r15)     // Catch:{ Exception -> 0x0508 }
            java.lang.String r14 = r14.toString()     // Catch:{ Exception -> 0x0508 }
            java.lang.String r10 = r10.getString(r14, r3)     // Catch:{ Exception -> 0x0508 }
            android.content.SharedPreferences r14 = r11.preferences     // Catch:{ Exception -> 0x0508 }
            java.lang.StringBuilder r15 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0508 }
            r15.<init>()     // Catch:{ Exception -> 0x0508 }
            java.lang.String r2 = r11.fileKey     // Catch:{ Exception -> 0x0508 }
            r15.append(r2)     // Catch:{ Exception -> 0x0508 }
            java.lang.String r2 = "_key"
            r15.append(r2)     // Catch:{ Exception -> 0x0508 }
            java.lang.String r2 = r15.toString()     // Catch:{ Exception -> 0x0508 }
            java.lang.String r2 = r14.getString(r2, r3)     // Catch:{ Exception -> 0x0508 }
            if (r10 == 0) goto L_0x020e
            if (r2 == 0) goto L_0x020e
            byte[] r2 = org.telegram.messenger.Utilities.hexToBytes(r2)     // Catch:{ Exception -> 0x0508 }
            r11.key = r2     // Catch:{ Exception -> 0x0508 }
            byte[] r2 = org.telegram.messenger.Utilities.hexToBytes(r10)     // Catch:{ Exception -> 0x0508 }
            r11.iv = r2     // Catch:{ Exception -> 0x0508 }
            byte[] r10 = r11.key     // Catch:{ Exception -> 0x0508 }
            if (r10 == 0) goto L_0x020e
            if (r2 == 0) goto L_0x020e
            int r10 = r10.length     // Catch:{ Exception -> 0x0508 }
            if (r10 != r6) goto L_0x020e
            int r10 = r2.length     // Catch:{ Exception -> 0x0508 }
            if (r10 != r6) goto L_0x020e
            byte[] r10 = new byte[r6]     // Catch:{ Exception -> 0x0508 }
            r11.ivChange = r10     // Catch:{ Exception -> 0x0508 }
            r14 = 0
            java.lang.System.arraycopy(r2, r14, r10, r14, r6)     // Catch:{ Exception -> 0x0508 }
            goto L_0x0210
        L_0x020e:
            r2 = 1
            goto L_0x0211
        L_0x0210:
            r2 = 0
        L_0x0211:
            if (r2 != 0) goto L_0x02ea
            if (r0 == 0) goto L_0x02ea
            boolean r10 = r11.isBigFile     // Catch:{ Exception -> 0x0508 }
            if (r10 == 0) goto L_0x0223
            int r14 = r11.uploadStartTime     // Catch:{ Exception -> 0x0508 }
            r15 = 86400(0x15180, float:1.21072E-40)
            int r14 = r14 - r15
            if (r0 >= r14) goto L_0x0223
        L_0x0221:
            r0 = 0
            goto L_0x0233
        L_0x0223:
            if (r10 != 0) goto L_0x0233
            float r14 = (float) r0     // Catch:{ Exception -> 0x0508 }
            int r15 = r11.uploadStartTime     // Catch:{ Exception -> 0x0508 }
            float r15 = (float) r15     // Catch:{ Exception -> 0x0508 }
            r18 = 1168687104(0x45a8CLASSNAME, float:5400.0)
            float r15 = r15 - r18
            int r14 = (r14 > r15 ? 1 : (r14 == r15 ? 0 : -1))
            if (r14 >= 0) goto L_0x0233
            goto L_0x0221
        L_0x0233:
            if (r0 == 0) goto L_0x02eb
            int r0 = (r8 > r4 ? 1 : (r8 == r4 ? 0 : -1))
            if (r0 <= 0) goto L_0x02ea
            r11.readBytesCount = r8     // Catch:{ Exception -> 0x0508 }
            int r0 = r11.uploadChunkSize     // Catch:{ Exception -> 0x0508 }
            long r14 = (long) r0     // Catch:{ Exception -> 0x0508 }
            long r14 = r8 / r14
            int r0 = (int) r14     // Catch:{ Exception -> 0x0508 }
            r11.currentPartNum = r0     // Catch:{ Exception -> 0x0508 }
            if (r10 != 0) goto L_0x02b0
            r0 = 0
        L_0x0246:
            long r8 = (long) r0     // Catch:{ Exception -> 0x0508 }
            long r14 = r11.readBytesCount     // Catch:{ Exception -> 0x0508 }
            int r10 = r11.uploadChunkSize     // Catch:{ Exception -> 0x0508 }
            long r12 = (long) r10     // Catch:{ Exception -> 0x0508 }
            long r14 = r14 / r12
            int r10 = (r8 > r14 ? 1 : (r8 == r14 ? 0 : -1))
            if (r10 >= 0) goto L_0x02eb
            java.io.RandomAccessFile r8 = r11.stream     // Catch:{ Exception -> 0x0508 }
            byte[] r9 = r11.readBuffer     // Catch:{ Exception -> 0x0508 }
            int r8 = r8.read(r9)     // Catch:{ Exception -> 0x0508 }
            boolean r9 = r11.isEncrypted     // Catch:{ Exception -> 0x0508 }
            if (r9 == 0) goto L_0x0268
            int r9 = r8 % 16
            if (r9 == 0) goto L_0x0268
            int r9 = r8 % 16
            int r9 = 16 - r9
            r10 = 0
            int r9 = r9 + r10
            goto L_0x0269
        L_0x0268:
            r9 = 0
        L_0x0269:
            org.telegram.tgnet.NativeByteBuffer r10 = new org.telegram.tgnet.NativeByteBuffer     // Catch:{ Exception -> 0x0508 }
            int r12 = r8 + r9
            r10.<init>((int) r12)     // Catch:{ Exception -> 0x0508 }
            int r13 = r11.uploadChunkSize     // Catch:{ Exception -> 0x0508 }
            if (r8 != r13) goto L_0x027b
            int r13 = r11.totalPartsCount     // Catch:{ Exception -> 0x0508 }
            int r14 = r11.currentPartNum     // Catch:{ Exception -> 0x0508 }
            int r14 = r14 + r1
            if (r13 != r14) goto L_0x027d
        L_0x027b:
            r11.isLastPart = r1     // Catch:{ Exception -> 0x0508 }
        L_0x027d:
            byte[] r13 = r11.readBuffer     // Catch:{ Exception -> 0x0508 }
            r14 = 0
            r10.writeBytes(r13, r14, r8)     // Catch:{ Exception -> 0x0508 }
            boolean r8 = r11.isEncrypted     // Catch:{ Exception -> 0x0508 }
            if (r8 == 0) goto L_0x02a8
            r8 = 0
        L_0x0288:
            if (r8 >= r9) goto L_0x0291
            r10.writeByte((int) r14)     // Catch:{ Exception -> 0x0508 }
            int r8 = r8 + 1
            r14 = 0
            goto L_0x0288
        L_0x0291:
            java.nio.ByteBuffer r8 = r10.buffer     // Catch:{ Exception -> 0x0508 }
            byte[] r9 = r11.key     // Catch:{ Exception -> 0x0508 }
            byte[] r13 = r11.ivChange     // Catch:{ Exception -> 0x0508 }
            r23 = 1
            r24 = 1
            r25 = 0
            r20 = r8
            r21 = r9
            r22 = r13
            r26 = r12
            org.telegram.messenger.Utilities.aesIgeEncryption(r20, r21, r22, r23, r24, r25, r26)     // Catch:{ Exception -> 0x0508 }
        L_0x02a8:
            r10.reuse()     // Catch:{ Exception -> 0x0508 }
            int r0 = r0 + 1
            r12 = 1024(0x400, double:5.06E-321)
            goto L_0x0246
        L_0x02b0:
            java.io.RandomAccessFile r0 = r11.stream     // Catch:{ Exception -> 0x0508 }
            r0.seek(r8)     // Catch:{ Exception -> 0x0508 }
            boolean r0 = r11.isEncrypted     // Catch:{ Exception -> 0x0508 }
            if (r0 == 0) goto L_0x02eb
            android.content.SharedPreferences r0 = r11.preferences     // Catch:{ Exception -> 0x0508 }
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0508 }
            r8.<init>()     // Catch:{ Exception -> 0x0508 }
            java.lang.String r9 = r11.fileKey     // Catch:{ Exception -> 0x0508 }
            r8.append(r9)     // Catch:{ Exception -> 0x0508 }
            java.lang.String r9 = "_ivc"
            r8.append(r9)     // Catch:{ Exception -> 0x0508 }
            java.lang.String r8 = r8.toString()     // Catch:{ Exception -> 0x0508 }
            java.lang.String r0 = r0.getString(r8, r3)     // Catch:{ Exception -> 0x0508 }
            if (r0 == 0) goto L_0x02e5
            byte[] r0 = org.telegram.messenger.Utilities.hexToBytes(r0)     // Catch:{ Exception -> 0x0508 }
            r11.ivChange = r0     // Catch:{ Exception -> 0x0508 }
            if (r0 == 0) goto L_0x02df
            int r0 = r0.length     // Catch:{ Exception -> 0x0508 }
            if (r0 == r6) goto L_0x02eb
        L_0x02df:
            r11.readBytesCount = r4     // Catch:{ Exception -> 0x0508 }
            r2 = 0
            r11.currentPartNum = r2     // Catch:{ Exception -> 0x0508 }
            goto L_0x02ea
        L_0x02e5:
            r11.readBytesCount = r4     // Catch:{ Exception -> 0x0508 }
            r2 = 0
            r11.currentPartNum = r2     // Catch:{ Exception -> 0x0508 }
        L_0x02ea:
            r2 = 1
        L_0x02eb:
            if (r2 == 0) goto L_0x032a
            boolean r0 = r11.isEncrypted     // Catch:{ Exception -> 0x0508 }
            if (r0 == 0) goto L_0x0311
            byte[] r0 = new byte[r6]     // Catch:{ Exception -> 0x0508 }
            r11.iv = r0     // Catch:{ Exception -> 0x0508 }
            byte[] r2 = new byte[r6]     // Catch:{ Exception -> 0x0508 }
            r11.key = r2     // Catch:{ Exception -> 0x0508 }
            byte[] r2 = new byte[r6]     // Catch:{ Exception -> 0x0508 }
            r11.ivChange = r2     // Catch:{ Exception -> 0x0508 }
            java.security.SecureRandom r2 = org.telegram.messenger.Utilities.random     // Catch:{ Exception -> 0x0508 }
            r2.nextBytes(r0)     // Catch:{ Exception -> 0x0508 }
            java.security.SecureRandom r0 = org.telegram.messenger.Utilities.random     // Catch:{ Exception -> 0x0508 }
            byte[] r2 = r11.key     // Catch:{ Exception -> 0x0508 }
            r0.nextBytes(r2)     // Catch:{ Exception -> 0x0508 }
            byte[] r0 = r11.iv     // Catch:{ Exception -> 0x0508 }
            byte[] r2 = r11.ivChange     // Catch:{ Exception -> 0x0508 }
            r8 = 0
            java.lang.System.arraycopy(r0, r8, r2, r8, r6)     // Catch:{ Exception -> 0x0508 }
        L_0x0311:
            java.security.SecureRandom r0 = org.telegram.messenger.Utilities.random     // Catch:{ Exception -> 0x0508 }
            long r8 = r0.nextLong()     // Catch:{ Exception -> 0x0508 }
            r11.currentFileId = r8     // Catch:{ Exception -> 0x0508 }
            boolean r0 = r11.nextPartFirst     // Catch:{ Exception -> 0x0508 }
            if (r0 != 0) goto L_0x032a
            boolean r0 = r11.uploadFirstPartLater     // Catch:{ Exception -> 0x0508 }
            if (r0 != 0) goto L_0x032a
            long r8 = r11.estimatedSize     // Catch:{ Exception -> 0x0508 }
            int r0 = (r8 > r4 ? 1 : (r8 == r4 ? 0 : -1))
            if (r0 != 0) goto L_0x032a
            r27.storeFileUploadInfo()     // Catch:{ Exception -> 0x0508 }
        L_0x032a:
            boolean r0 = r11.isEncrypted     // Catch:{ Exception -> 0x0508 }
            if (r0 == 0) goto L_0x0361
            java.lang.String r0 = "MD5"
            java.security.MessageDigest r0 = java.security.MessageDigest.getInstance(r0)     // Catch:{ Exception -> 0x035d }
            byte[] r2 = new byte[r7]     // Catch:{ Exception -> 0x035d }
            byte[] r7 = r11.key     // Catch:{ Exception -> 0x035d }
            r8 = 0
            java.lang.System.arraycopy(r7, r8, r2, r8, r6)     // Catch:{ Exception -> 0x035d }
            byte[] r7 = r11.iv     // Catch:{ Exception -> 0x035d }
            java.lang.System.arraycopy(r7, r8, r2, r6, r6)     // Catch:{ Exception -> 0x035d }
            byte[] r0 = r0.digest(r2)     // Catch:{ Exception -> 0x035d }
            r2 = 0
        L_0x0346:
            r7 = 4
            if (r2 >= r7) goto L_0x0361
            int r7 = r11.fingerprint     // Catch:{ Exception -> 0x035d }
            byte r8 = r0[r2]     // Catch:{ Exception -> 0x035d }
            int r9 = r2 + 4
            byte r9 = r0[r9]     // Catch:{ Exception -> 0x035d }
            r8 = r8 ^ r9
            r8 = r8 & 255(0xff, float:3.57E-43)
            int r9 = r2 * 8
            int r8 = r8 << r9
            r7 = r7 | r8
            r11.fingerprint = r7     // Catch:{ Exception -> 0x035d }
            int r2 = r2 + 1
            goto L_0x0346
        L_0x035d:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ Exception -> 0x0508 }
        L_0x0361:
            long r7 = r11.readBytesCount     // Catch:{ Exception -> 0x0508 }
            r11.uploadedBytesCount = r7     // Catch:{ Exception -> 0x0508 }
            int r0 = r11.currentPartNum     // Catch:{ Exception -> 0x0508 }
            r11.lastSavedPartNum = r0     // Catch:{ Exception -> 0x0508 }
            boolean r0 = r11.uploadFirstPartLater     // Catch:{ Exception -> 0x0508 }
            if (r0 == 0) goto L_0x0397
            boolean r0 = r11.isBigFile     // Catch:{ Exception -> 0x0508 }
            if (r0 == 0) goto L_0x037f
            java.io.RandomAccessFile r0 = r11.stream     // Catch:{ Exception -> 0x0508 }
            int r2 = r11.uploadChunkSize     // Catch:{ Exception -> 0x0508 }
            long r7 = (long) r2     // Catch:{ Exception -> 0x0508 }
            r0.seek(r7)     // Catch:{ Exception -> 0x0508 }
            int r0 = r11.uploadChunkSize     // Catch:{ Exception -> 0x0508 }
            long r7 = (long) r0     // Catch:{ Exception -> 0x0508 }
            r11.readBytesCount = r7     // Catch:{ Exception -> 0x0508 }
            goto L_0x0388
        L_0x037f:
            java.io.RandomAccessFile r0 = r11.stream     // Catch:{ Exception -> 0x0508 }
            r7 = 1024(0x400, double:5.06E-321)
            r0.seek(r7)     // Catch:{ Exception -> 0x0508 }
            r11.readBytesCount = r7     // Catch:{ Exception -> 0x0508 }
        L_0x0388:
            r11.currentPartNum = r1     // Catch:{ Exception -> 0x0508 }
            goto L_0x0397
        L_0x038b:
            java.lang.Exception r0 = new java.lang.Exception     // Catch:{ Exception -> 0x0508 }
            r0.<init>(r8)     // Catch:{ Exception -> 0x0508 }
            throw r0     // Catch:{ Exception -> 0x0508 }
        L_0x0391:
            java.lang.Exception r0 = new java.lang.Exception     // Catch:{ Exception -> 0x0508 }
            r0.<init>(r8)     // Catch:{ Exception -> 0x0508 }
            throw r0     // Catch:{ Exception -> 0x0508 }
        L_0x0397:
            long r7 = r11.estimatedSize     // Catch:{ Exception -> 0x0508 }
            int r0 = (r7 > r4 ? 1 : (r7 == r4 ? 0 : -1))
            if (r0 == 0) goto L_0x03aa
            long r7 = r11.readBytesCount     // Catch:{ Exception -> 0x0508 }
            int r0 = r11.uploadChunkSize     // Catch:{ Exception -> 0x0508 }
            long r9 = (long) r0     // Catch:{ Exception -> 0x0508 }
            long r7 = r7 + r9
            long r9 = r11.availableSize     // Catch:{ Exception -> 0x0508 }
            int r0 = (r7 > r9 ? 1 : (r7 == r9 ? 0 : -1))
            if (r0 <= 0) goto L_0x03aa
            return
        L_0x03aa:
            boolean r0 = r11.nextPartFirst     // Catch:{ Exception -> 0x0508 }
            if (r0 == 0) goto L_0x03cf
            java.io.RandomAccessFile r0 = r11.stream     // Catch:{ Exception -> 0x0508 }
            r0.seek(r4)     // Catch:{ Exception -> 0x0508 }
            boolean r0 = r11.isBigFile     // Catch:{ Exception -> 0x0508 }
            if (r0 == 0) goto L_0x03c1
            java.io.RandomAccessFile r0 = r11.stream     // Catch:{ Exception -> 0x0508 }
            byte[] r2 = r11.readBuffer     // Catch:{ Exception -> 0x0508 }
            int r0 = r0.read(r2)     // Catch:{ Exception -> 0x0508 }
            r8 = 0
            goto L_0x03cc
        L_0x03c1:
            java.io.RandomAccessFile r0 = r11.stream     // Catch:{ Exception -> 0x0508 }
            byte[] r2 = r11.readBuffer     // Catch:{ Exception -> 0x0508 }
            r7 = 1024(0x400, float:1.435E-42)
            r8 = 0
            int r0 = r0.read(r2, r8, r7)     // Catch:{ Exception -> 0x0508 }
        L_0x03cc:
            r11.currentPartNum = r8     // Catch:{ Exception -> 0x0508 }
            goto L_0x03d7
        L_0x03cf:
            java.io.RandomAccessFile r0 = r11.stream     // Catch:{ Exception -> 0x0508 }
            byte[] r2 = r11.readBuffer     // Catch:{ Exception -> 0x0508 }
            int r0 = r0.read(r2)     // Catch:{ Exception -> 0x0508 }
        L_0x03d7:
            r7 = r0
            r0 = -1
            if (r7 != r0) goto L_0x03dc
            return
        L_0x03dc:
            boolean r2 = r11.isEncrypted     // Catch:{ Exception -> 0x0508 }
            if (r2 == 0) goto L_0x03eb
            int r2 = r7 % 16
            if (r2 == 0) goto L_0x03eb
            int r2 = r7 % 16
            int r2 = 16 - r2
            r8 = 0
            int r2 = r2 + r8
            goto L_0x03ec
        L_0x03eb:
            r2 = 0
        L_0x03ec:
            org.telegram.tgnet.NativeByteBuffer r8 = new org.telegram.tgnet.NativeByteBuffer     // Catch:{ Exception -> 0x0508 }
            int r9 = r7 + r2
            r8.<init>((int) r9)     // Catch:{ Exception -> 0x0508 }
            boolean r10 = r11.nextPartFirst     // Catch:{ Exception -> 0x0508 }
            if (r10 != 0) goto L_0x0408
            int r10 = r11.uploadChunkSize     // Catch:{ Exception -> 0x0508 }
            if (r7 != r10) goto L_0x0408
            long r12 = r11.estimatedSize     // Catch:{ Exception -> 0x0508 }
            int r10 = (r12 > r4 ? 1 : (r12 == r4 ? 0 : -1))
            if (r10 != 0) goto L_0x0414
            int r10 = r11.totalPartsCount     // Catch:{ Exception -> 0x0508 }
            int r12 = r11.currentPartNum     // Catch:{ Exception -> 0x0508 }
            int r12 = r12 + r1
            if (r10 != r12) goto L_0x0414
        L_0x0408:
            boolean r10 = r11.uploadFirstPartLater     // Catch:{ Exception -> 0x0508 }
            if (r10 == 0) goto L_0x0412
            r11.nextPartFirst = r1     // Catch:{ Exception -> 0x0508 }
            r10 = 0
            r11.uploadFirstPartLater = r10     // Catch:{ Exception -> 0x0508 }
            goto L_0x0414
        L_0x0412:
            r11.isLastPart = r1     // Catch:{ Exception -> 0x0508 }
        L_0x0414:
            byte[] r10 = r11.readBuffer     // Catch:{ Exception -> 0x0508 }
            r12 = 0
            r8.writeBytes(r10, r12, r7)     // Catch:{ Exception -> 0x0508 }
            boolean r10 = r11.isEncrypted     // Catch:{ Exception -> 0x0508 }
            if (r10 == 0) goto L_0x0454
            r3 = 0
        L_0x041f:
            if (r3 >= r2) goto L_0x0428
            r8.writeByte((int) r12)     // Catch:{ Exception -> 0x0508 }
            int r3 = r3 + 1
            r12 = 0
            goto L_0x041f
        L_0x0428:
            java.nio.ByteBuffer r2 = r8.buffer     // Catch:{ Exception -> 0x0508 }
            byte[] r3 = r11.key     // Catch:{ Exception -> 0x0508 }
            byte[] r10 = r11.ivChange     // Catch:{ Exception -> 0x0508 }
            r20 = 1
            r21 = 1
            r22 = 0
            r17 = r2
            r18 = r3
            r19 = r10
            r23 = r9
            org.telegram.messenger.Utilities.aesIgeEncryption(r17, r18, r19, r20, r21, r22, r23)     // Catch:{ Exception -> 0x0508 }
            java.util.ArrayList<byte[]> r2 = r11.freeRequestIvs     // Catch:{ Exception -> 0x0508 }
            r3 = 0
            java.lang.Object r2 = r2.get(r3)     // Catch:{ Exception -> 0x0508 }
            byte[] r2 = (byte[]) r2     // Catch:{ Exception -> 0x0508 }
            byte[] r9 = r11.ivChange     // Catch:{ Exception -> 0x0508 }
            java.lang.System.arraycopy(r9, r3, r2, r3, r6)     // Catch:{ Exception -> 0x0508 }
            java.util.ArrayList<byte[]> r6 = r11.freeRequestIvs     // Catch:{ Exception -> 0x0508 }
            r6.remove(r3)     // Catch:{ Exception -> 0x0508 }
            r6 = r2
            goto L_0x0455
        L_0x0454:
            r6 = r3
        L_0x0455:
            boolean r2 = r11.isBigFile     // Catch:{ Exception -> 0x0508 }
            if (r2 == 0) goto L_0x0479
            org.telegram.tgnet.TLRPC$TL_upload_saveBigFilePart r2 = new org.telegram.tgnet.TLRPC$TL_upload_saveBigFilePart     // Catch:{ Exception -> 0x0508 }
            r2.<init>()     // Catch:{ Exception -> 0x0508 }
            int r3 = r11.currentPartNum     // Catch:{ Exception -> 0x0508 }
            r2.file_part = r3     // Catch:{ Exception -> 0x0508 }
            long r9 = r11.currentFileId     // Catch:{ Exception -> 0x0508 }
            r2.file_id = r9     // Catch:{ Exception -> 0x0508 }
            long r9 = r11.estimatedSize     // Catch:{ Exception -> 0x0508 }
            int r12 = (r9 > r4 ? 1 : (r9 == r4 ? 0 : -1))
            if (r12 == 0) goto L_0x046f
            r2.file_total_parts = r0     // Catch:{ Exception -> 0x0508 }
            goto L_0x0473
        L_0x046f:
            int r0 = r11.totalPartsCount     // Catch:{ Exception -> 0x0508 }
            r2.file_total_parts = r0     // Catch:{ Exception -> 0x0508 }
        L_0x0473:
            r2.bytes = r8     // Catch:{ Exception -> 0x0508 }
            r18 = r2
            r8 = r3
            goto L_0x048b
        L_0x0479:
            org.telegram.tgnet.TLRPC$TL_upload_saveFilePart r2 = new org.telegram.tgnet.TLRPC$TL_upload_saveFilePart     // Catch:{ Exception -> 0x0508 }
            r2.<init>()     // Catch:{ Exception -> 0x0508 }
            int r0 = r11.currentPartNum     // Catch:{ Exception -> 0x0508 }
            r2.file_part = r0     // Catch:{ Exception -> 0x0508 }
            long r3 = r11.currentFileId     // Catch:{ Exception -> 0x0508 }
            r2.file_id = r3     // Catch:{ Exception -> 0x0508 }
            r2.bytes = r8     // Catch:{ Exception -> 0x0508 }
            r8 = r0
            r18 = r2
        L_0x048b:
            boolean r0 = r11.isLastPart     // Catch:{ Exception -> 0x0508 }
            if (r0 == 0) goto L_0x04a3
            boolean r0 = r11.nextPartFirst     // Catch:{ Exception -> 0x0508 }
            if (r0 == 0) goto L_0x04a3
            r12 = 0
            r11.nextPartFirst = r12     // Catch:{ Exception -> 0x0508 }
            int r0 = r11.totalPartsCount     // Catch:{ Exception -> 0x0508 }
            int r0 = r0 - r1
            r11.currentPartNum = r0     // Catch:{ Exception -> 0x0508 }
            java.io.RandomAccessFile r0 = r11.stream     // Catch:{ Exception -> 0x0508 }
            long r2 = r11.totalFileSize     // Catch:{ Exception -> 0x0508 }
            r0.seek(r2)     // Catch:{ Exception -> 0x0508 }
            goto L_0x04a4
        L_0x04a3:
            r12 = 0
        L_0x04a4:
            long r2 = r11.readBytesCount     // Catch:{ Exception -> 0x0508 }
            long r4 = (long) r7     // Catch:{ Exception -> 0x0508 }
            long r2 = r2 + r4
            r11.readBytesCount = r2     // Catch:{ Exception -> 0x0508 }
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
            int r1 = r18.getObjectSize()
            r16 = 4
            int r4 = r1 + 4
            int r3 = r11.operationGuid
            boolean r1 = r11.slowNetwork
            if (r1 == 0) goto L_0x04ce
            r24 = 4
            goto L_0x04d6
        L_0x04ce:
            int r1 = r0 % 4
            int r1 = r1 << 16
            r1 = r1 | 4
            r24 = r1
        L_0x04d6:
            int r1 = r11.currentAccount
            org.telegram.tgnet.ConnectionsManager r17 = org.telegram.tgnet.ConnectionsManager.getInstance(r1)
            org.telegram.messenger.FileUploadOperation$$ExternalSyntheticLambda5 r19 = new org.telegram.messenger.FileUploadOperation$$ExternalSyntheticLambda5
            r1 = r19
            r2 = r27
            r5 = r6
            r6 = r0
            r1.<init>(r2, r3, r4, r5, r6, r7, r8, r9)
            r20 = 0
            org.telegram.messenger.FileUploadOperation$$ExternalSyntheticLambda6 r1 = new org.telegram.messenger.FileUploadOperation$$ExternalSyntheticLambda6
            r1.<init>(r11)
            boolean r2 = r11.forceSmallFile
            if (r2 == 0) goto L_0x04f5
            r22 = 4
            goto L_0x04f7
        L_0x04f5:
            r22 = 0
        L_0x04f7:
            r23 = 2147483647(0x7fffffff, float:NaN)
            r25 = 1
            r21 = r1
            int r1 = r17.sendRequest(r18, r19, r20, r21, r22, r23, r24, r25)
            android.util.SparseIntArray r2 = r11.requestTokens
            r2.put(r0, r1)
            return
        L_0x0508:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            r1 = 4
            r11.state = r1
            org.telegram.messenger.FileUploadOperation$FileUploadOperationDelegate r0 = r11.delegate
            r0.didFailedUploadingFile(r11)
            r27.cleanup()
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
                long j3 = this.estimatedSize;
                if (j3 != 0) {
                    j2 = Math.max(this.availableSize, j3);
                } else {
                    j2 = this.totalFileSize;
                }
                this.delegate.didChangedUploadProgress(this, this.uploadedBytesCount, j2);
                int i9 = this.currentUploadRequetsCount - 1;
                this.currentUploadRequetsCount = i9;
                if (this.isLastPart && i9 == 0 && this.state == 1) {
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
                    int i10 = this.currentType;
                    if (i10 == 50331648) {
                        StatsController.getInstance(this.currentAccount).incrementSentItemsCount(ApplicationLoader.getCurrentNetworkType(), 3, 1);
                    } else if (i10 == 33554432) {
                        StatsController.getInstance(this.currentAccount).incrementSentItemsCount(ApplicationLoader.getCurrentNetworkType(), 2, 1);
                    } else if (i10 == 16777216) {
                        StatsController.getInstance(this.currentAccount).incrementSentItemsCount(ApplicationLoader.getCurrentNetworkType(), 4, 1);
                    } else if (i10 == 67108864) {
                        StatsController.getInstance(this.currentAccount).incrementSentItemsCount(ApplicationLoader.getCurrentNetworkType(), 5, 1);
                    }
                } else if (i9 < this.maxRequestsCount) {
                    if (this.estimatedSize == 0 && !this.uploadFirstPartLater && !this.nextPartFirst) {
                        if (this.saveInfoTimes >= 4) {
                            this.saveInfoTimes = 0;
                        }
                        int i11 = this.lastSavedPartNum;
                        if (i7 == i11) {
                            this.lastSavedPartNum = i11 + 1;
                            long j4 = j;
                            while (true) {
                                UploadCachedResult uploadCachedResult = this.cachedResults.get(this.lastSavedPartNum);
                                if (uploadCachedResult == null) {
                                    break;
                                }
                                j4 = uploadCachedResult.bytesOffset;
                                bArr2 = uploadCachedResult.iv;
                                this.cachedResults.remove(this.lastSavedPartNum);
                                this.lastSavedPartNum++;
                            }
                            boolean z = this.isBigFile;
                            if ((z && j4 % 1048576 == 0) || (!z && this.saveInfoTimes == 0)) {
                                SharedPreferences.Editor edit = this.preferences.edit();
                                edit.putLong(this.fileKey + "_uploaded", j4);
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
