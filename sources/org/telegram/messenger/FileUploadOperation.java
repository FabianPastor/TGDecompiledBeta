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
    private int estimatedSize;
    private String fileKey;
    private int fingerprint;
    private ArrayList<byte[]> freeRequestIvs;
    private boolean isBigFile;
    private boolean isEncrypted;
    private boolean isLastPart;
    private byte[] iv;
    private byte[] ivChange;
    private byte[] key;
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

    private class UploadCachedResult {
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
        this.uploadFirstPartLater = i2 != 0 && !this.isEncrypted;
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
            Utilities.stageQueue.postRunnable(new Runnable() {
                public final void run() {
                    FileUploadOperation.this.lambda$start$0$FileUploadOperation();
                }
            });
        }
    }

    public /* synthetic */ void lambda$start$0$FileUploadOperation() {
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
            Utilities.stageQueue.postRunnable(new Runnable(z) {
                private final /* synthetic */ boolean f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    FileUploadOperation.this.lambda$onNetworkChanged$1$FileUploadOperation(this.f$1);
                }
            });
        }
    }

    public /* synthetic */ void lambda$onNetworkChanged$1$FileUploadOperation(boolean z) {
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
            Utilities.stageQueue.postRunnable(new Runnable() {
                public final void run() {
                    FileUploadOperation.this.lambda$cancel$2$FileUploadOperation();
                }
            });
            this.delegate.didFailedUploadingFile(this);
            cleanup();
        }
    }

    public /* synthetic */ void lambda$cancel$2$FileUploadOperation() {
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
            if (this.stream != null) {
                this.stream.close();
                this.stream = null;
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    /* access modifiers changed from: protected */
    public void checkNewDataAvailable(long j, long j2) {
        Utilities.stageQueue.postRunnable(new Runnable(j2, j) {
            private final /* synthetic */ long f$1;
            private final /* synthetic */ long f$2;

            {
                this.f$1 = r2;
                this.f$2 = r4;
            }

            public final void run() {
                FileUploadOperation.this.lambda$checkNewDataAvailable$3$FileUploadOperation(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$checkNewDataAvailable$3$FileUploadOperation(long j, long j2) {
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

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v0, resolved type: org.telegram.tgnet.TLRPC$TL_upload_saveFilePart} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v1, resolved type: org.telegram.tgnet.TLRPC$TL_upload_saveFilePart} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v7, resolved type: org.telegram.tgnet.TLRPC$TL_upload_saveBigFilePart} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v2, resolved type: org.telegram.tgnet.TLRPC$TL_upload_saveFilePart} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:112:0x029f A[Catch:{ Exception -> 0x04a8 }] */
    /* JADX WARNING: Removed duplicated region for block: B:115:0x02a6 A[Catch:{ Exception -> 0x04a8 }] */
    /* JADX WARNING: Removed duplicated region for block: B:127:0x02e6  */
    /* JADX WARNING: Removed duplicated region for block: B:139:0x0324 A[Catch:{ Exception -> 0x04a8 }] */
    /* JADX WARNING: Removed duplicated region for block: B:77:0x01e4 A[Catch:{ Exception -> 0x04a8 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void startUploadRequest() {
        /*
            r27 = this;
            r12 = r27
            int r0 = r12.state
            r1 = 1
            if (r0 == r1) goto L_0x0008
            return
        L_0x0008:
            r12.started = r1     // Catch:{ Exception -> 0x04a8 }
            java.io.RandomAccessFile r0 = r12.stream     // Catch:{ Exception -> 0x04a8 }
            r3 = 1024(0x400, float:1.435E-42)
            r4 = 0
            r5 = 0
            r7 = 32
            r8 = 0
            if (r0 != 0) goto L_0x034a
            java.io.File r0 = new java.io.File     // Catch:{ Exception -> 0x04a8 }
            java.lang.String r9 = r12.uploadingFilePath     // Catch:{ Exception -> 0x04a8 }
            r0.<init>(r9)     // Catch:{ Exception -> 0x04a8 }
            android.net.Uri r9 = android.net.Uri.fromFile(r0)     // Catch:{ Exception -> 0x04a8 }
            boolean r9 = org.telegram.messenger.AndroidUtilities.isInternalUri(r9)     // Catch:{ Exception -> 0x04a8 }
            if (r9 != 0) goto L_0x0342
            java.io.RandomAccessFile r9 = new java.io.RandomAccessFile     // Catch:{ Exception -> 0x04a8 }
            java.lang.String r10 = "r"
            r9.<init>(r0, r10)     // Catch:{ Exception -> 0x04a8 }
            r12.stream = r9     // Catch:{ Exception -> 0x04a8 }
            int r9 = r12.estimatedSize     // Catch:{ Exception -> 0x04a8 }
            if (r9 == 0) goto L_0x003a
            int r0 = r12.estimatedSize     // Catch:{ Exception -> 0x04a8 }
            long r9 = (long) r0     // Catch:{ Exception -> 0x04a8 }
            r12.totalFileSize = r9     // Catch:{ Exception -> 0x04a8 }
            goto L_0x0040
        L_0x003a:
            long r9 = r0.length()     // Catch:{ Exception -> 0x04a8 }
            r12.totalFileSize = r9     // Catch:{ Exception -> 0x04a8 }
        L_0x0040:
            long r9 = r12.totalFileSize     // Catch:{ Exception -> 0x04a8 }
            r13 = 10485760(0xa00000, double:5.180654E-317)
            int r0 = (r9 > r13 ? 1 : (r9 == r13 ? 0 : -1))
            if (r0 <= 0) goto L_0x004b
            r12.isBigFile = r1     // Catch:{ Exception -> 0x04a8 }
        L_0x004b:
            boolean r0 = r12.slowNetwork     // Catch:{ Exception -> 0x04a8 }
            if (r0 == 0) goto L_0x0052
            r9 = 32
            goto L_0x0054
        L_0x0052:
            r9 = 128(0x80, double:6.32E-322)
        L_0x0054:
            long r13 = r12.totalFileSize     // Catch:{ Exception -> 0x04a8 }
            r15 = 3072000(0x2ee000, double:1.5177697E-317)
            long r13 = r13 + r15
            r17 = 1
            long r13 = r13 - r17
            long r13 = r13 / r15
            long r9 = java.lang.Math.max(r9, r13)     // Catch:{ Exception -> 0x04a8 }
            int r0 = (int) r9     // Catch:{ Exception -> 0x04a8 }
            r12.uploadChunkSize = r0     // Catch:{ Exception -> 0x04a8 }
            int r0 = r12.uploadChunkSize     // Catch:{ Exception -> 0x04a8 }
            int r0 = r3 % r0
            r9 = 64
            if (r0 == 0) goto L_0x0079
            r0 = 64
        L_0x0070:
            int r10 = r12.uploadChunkSize     // Catch:{ Exception -> 0x04a8 }
            if (r10 <= r0) goto L_0x0077
            int r0 = r0 * 2
            goto L_0x0070
        L_0x0077:
            r12.uploadChunkSize = r0     // Catch:{ Exception -> 0x04a8 }
        L_0x0079:
            boolean r0 = r12.slowNetwork     // Catch:{ Exception -> 0x04a8 }
            if (r0 == 0) goto L_0x0080
            r0 = 32
            goto L_0x0082
        L_0x0080:
            r0 = 2048(0x800, float:2.87E-42)
        L_0x0082:
            int r10 = r12.uploadChunkSize     // Catch:{ Exception -> 0x04a8 }
            int r0 = r0 / r10
            int r0 = java.lang.Math.max(r1, r0)     // Catch:{ Exception -> 0x04a8 }
            r12.maxRequestsCount = r0     // Catch:{ Exception -> 0x04a8 }
            boolean r0 = r12.isEncrypted     // Catch:{ Exception -> 0x04a8 }
            if (r0 == 0) goto L_0x00a7
            java.util.ArrayList r0 = new java.util.ArrayList     // Catch:{ Exception -> 0x04a8 }
            int r10 = r12.maxRequestsCount     // Catch:{ Exception -> 0x04a8 }
            r0.<init>(r10)     // Catch:{ Exception -> 0x04a8 }
            r12.freeRequestIvs = r0     // Catch:{ Exception -> 0x04a8 }
            r0 = 0
        L_0x0099:
            int r10 = r12.maxRequestsCount     // Catch:{ Exception -> 0x04a8 }
            if (r0 >= r10) goto L_0x00a7
            java.util.ArrayList<byte[]> r10 = r12.freeRequestIvs     // Catch:{ Exception -> 0x04a8 }
            byte[] r11 = new byte[r7]     // Catch:{ Exception -> 0x04a8 }
            r10.add(r11)     // Catch:{ Exception -> 0x04a8 }
            int r0 = r0 + 1
            goto L_0x0099
        L_0x00a7:
            int r0 = r12.uploadChunkSize     // Catch:{ Exception -> 0x04a8 }
            int r0 = r0 * 1024
            r12.uploadChunkSize = r0     // Catch:{ Exception -> 0x04a8 }
            r27.calcTotalPartsCount()     // Catch:{ Exception -> 0x04a8 }
            int r0 = r12.uploadChunkSize     // Catch:{ Exception -> 0x04a8 }
            byte[] r0 = new byte[r0]     // Catch:{ Exception -> 0x04a8 }
            r12.readBuffer = r0     // Catch:{ Exception -> 0x04a8 }
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x04a8 }
            r0.<init>()     // Catch:{ Exception -> 0x04a8 }
            java.lang.String r10 = r12.uploadingFilePath     // Catch:{ Exception -> 0x04a8 }
            r0.append(r10)     // Catch:{ Exception -> 0x04a8 }
            boolean r10 = r12.isEncrypted     // Catch:{ Exception -> 0x04a8 }
            if (r10 == 0) goto L_0x00c7
            java.lang.String r10 = "enc"
            goto L_0x00c9
        L_0x00c7:
            java.lang.String r10 = ""
        L_0x00c9:
            r0.append(r10)     // Catch:{ Exception -> 0x04a8 }
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x04a8 }
            java.lang.String r0 = org.telegram.messenger.Utilities.MD5(r0)     // Catch:{ Exception -> 0x04a8 }
            r12.fileKey = r0     // Catch:{ Exception -> 0x04a8 }
            android.content.SharedPreferences r0 = r12.preferences     // Catch:{ Exception -> 0x04a8 }
            java.lang.StringBuilder r10 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x04a8 }
            r10.<init>()     // Catch:{ Exception -> 0x04a8 }
            java.lang.String r11 = r12.fileKey     // Catch:{ Exception -> 0x04a8 }
            r10.append(r11)     // Catch:{ Exception -> 0x04a8 }
            java.lang.String r11 = "_size"
            r10.append(r11)     // Catch:{ Exception -> 0x04a8 }
            java.lang.String r10 = r10.toString()     // Catch:{ Exception -> 0x04a8 }
            long r10 = r0.getLong(r10, r5)     // Catch:{ Exception -> 0x04a8 }
            long r13 = java.lang.System.currentTimeMillis()     // Catch:{ Exception -> 0x04a8 }
            r15 = 1000(0x3e8, double:4.94E-321)
            long r13 = r13 / r15
            int r0 = (int) r13     // Catch:{ Exception -> 0x04a8 }
            r12.uploadStartTime = r0     // Catch:{ Exception -> 0x04a8 }
            boolean r0 = r12.uploadFirstPartLater     // Catch:{ Exception -> 0x04a8 }
            if (r0 != 0) goto L_0x02a2
            boolean r0 = r12.nextPartFirst     // Catch:{ Exception -> 0x04a8 }
            if (r0 != 0) goto L_0x02a2
            int r0 = r12.estimatedSize     // Catch:{ Exception -> 0x04a8 }
            if (r0 != 0) goto L_0x02a2
            long r13 = r12.totalFileSize     // Catch:{ Exception -> 0x04a8 }
            int r0 = (r10 > r13 ? 1 : (r10 == r13 ? 0 : -1))
            if (r0 != 0) goto L_0x02a2
            android.content.SharedPreferences r0 = r12.preferences     // Catch:{ Exception -> 0x04a8 }
            java.lang.StringBuilder r10 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x04a8 }
            r10.<init>()     // Catch:{ Exception -> 0x04a8 }
            java.lang.String r11 = r12.fileKey     // Catch:{ Exception -> 0x04a8 }
            r10.append(r11)     // Catch:{ Exception -> 0x04a8 }
            java.lang.String r11 = "_id"
            r10.append(r11)     // Catch:{ Exception -> 0x04a8 }
            java.lang.String r10 = r10.toString()     // Catch:{ Exception -> 0x04a8 }
            long r10 = r0.getLong(r10, r5)     // Catch:{ Exception -> 0x04a8 }
            r12.currentFileId = r10     // Catch:{ Exception -> 0x04a8 }
            android.content.SharedPreferences r0 = r12.preferences     // Catch:{ Exception -> 0x04a8 }
            java.lang.StringBuilder r10 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x04a8 }
            r10.<init>()     // Catch:{ Exception -> 0x04a8 }
            java.lang.String r11 = r12.fileKey     // Catch:{ Exception -> 0x04a8 }
            r10.append(r11)     // Catch:{ Exception -> 0x04a8 }
            java.lang.String r11 = "_time"
            r10.append(r11)     // Catch:{ Exception -> 0x04a8 }
            java.lang.String r10 = r10.toString()     // Catch:{ Exception -> 0x04a8 }
            int r0 = r0.getInt(r10, r8)     // Catch:{ Exception -> 0x04a8 }
            android.content.SharedPreferences r10 = r12.preferences     // Catch:{ Exception -> 0x04a8 }
            java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x04a8 }
            r11.<init>()     // Catch:{ Exception -> 0x04a8 }
            java.lang.String r13 = r12.fileKey     // Catch:{ Exception -> 0x04a8 }
            r11.append(r13)     // Catch:{ Exception -> 0x04a8 }
            java.lang.String r13 = "_uploaded"
            r11.append(r13)     // Catch:{ Exception -> 0x04a8 }
            java.lang.String r11 = r11.toString()     // Catch:{ Exception -> 0x04a8 }
            long r10 = r10.getLong(r11, r5)     // Catch:{ Exception -> 0x04a8 }
            boolean r13 = r12.isEncrypted     // Catch:{ Exception -> 0x04a8 }
            if (r13 == 0) goto L_0x01be
            android.content.SharedPreferences r13 = r12.preferences     // Catch:{ Exception -> 0x04a8 }
            java.lang.StringBuilder r14 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x04a8 }
            r14.<init>()     // Catch:{ Exception -> 0x04a8 }
            java.lang.String r15 = r12.fileKey     // Catch:{ Exception -> 0x04a8 }
            r14.append(r15)     // Catch:{ Exception -> 0x04a8 }
            java.lang.String r15 = "_iv"
            r14.append(r15)     // Catch:{ Exception -> 0x04a8 }
            java.lang.String r14 = r14.toString()     // Catch:{ Exception -> 0x04a8 }
            java.lang.String r13 = r13.getString(r14, r4)     // Catch:{ Exception -> 0x04a8 }
            android.content.SharedPreferences r14 = r12.preferences     // Catch:{ Exception -> 0x04a8 }
            java.lang.StringBuilder r15 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x04a8 }
            r15.<init>()     // Catch:{ Exception -> 0x04a8 }
            java.lang.String r3 = r12.fileKey     // Catch:{ Exception -> 0x04a8 }
            r15.append(r3)     // Catch:{ Exception -> 0x04a8 }
            java.lang.String r3 = "_key"
            r15.append(r3)     // Catch:{ Exception -> 0x04a8 }
            java.lang.String r3 = r15.toString()     // Catch:{ Exception -> 0x04a8 }
            java.lang.String r3 = r14.getString(r3, r4)     // Catch:{ Exception -> 0x04a8 }
            if (r13 == 0) goto L_0x01bc
            if (r3 == 0) goto L_0x01bc
            byte[] r3 = org.telegram.messenger.Utilities.hexToBytes(r3)     // Catch:{ Exception -> 0x04a8 }
            r12.key = r3     // Catch:{ Exception -> 0x04a8 }
            byte[] r3 = org.telegram.messenger.Utilities.hexToBytes(r13)     // Catch:{ Exception -> 0x04a8 }
            r12.iv = r3     // Catch:{ Exception -> 0x04a8 }
            byte[] r3 = r12.key     // Catch:{ Exception -> 0x04a8 }
            if (r3 == 0) goto L_0x01bc
            byte[] r3 = r12.iv     // Catch:{ Exception -> 0x04a8 }
            if (r3 == 0) goto L_0x01bc
            byte[] r3 = r12.key     // Catch:{ Exception -> 0x04a8 }
            int r3 = r3.length     // Catch:{ Exception -> 0x04a8 }
            if (r3 != r7) goto L_0x01bc
            byte[] r3 = r12.iv     // Catch:{ Exception -> 0x04a8 }
            int r3 = r3.length     // Catch:{ Exception -> 0x04a8 }
            if (r3 != r7) goto L_0x01bc
            byte[] r3 = new byte[r7]     // Catch:{ Exception -> 0x04a8 }
            r12.ivChange = r3     // Catch:{ Exception -> 0x04a8 }
            byte[] r3 = r12.iv     // Catch:{ Exception -> 0x04a8 }
            byte[] r13 = r12.ivChange     // Catch:{ Exception -> 0x04a8 }
            java.lang.System.arraycopy(r3, r8, r13, r8, r7)     // Catch:{ Exception -> 0x04a8 }
            goto L_0x01be
        L_0x01bc:
            r3 = 1
            goto L_0x01bf
        L_0x01be:
            r3 = 0
        L_0x01bf:
            if (r3 != 0) goto L_0x02a2
            if (r0 == 0) goto L_0x02a2
            boolean r13 = r12.isBigFile     // Catch:{ Exception -> 0x04a8 }
            if (r13 == 0) goto L_0x01d1
            int r13 = r12.uploadStartTime     // Catch:{ Exception -> 0x04a8 }
            r14 = 86400(0x15180, float:1.21072E-40)
            int r13 = r13 - r14
            if (r0 >= r13) goto L_0x01d1
        L_0x01cf:
            r0 = 0
            goto L_0x01e2
        L_0x01d1:
            boolean r13 = r12.isBigFile     // Catch:{ Exception -> 0x04a8 }
            if (r13 != 0) goto L_0x01e2
            float r13 = (float) r0     // Catch:{ Exception -> 0x04a8 }
            int r14 = r12.uploadStartTime     // Catch:{ Exception -> 0x04a8 }
            float r14 = (float) r14     // Catch:{ Exception -> 0x04a8 }
            r15 = 1168687104(0x45a8CLASSNAME, float:5400.0)
            float r14 = r14 - r15
            int r13 = (r13 > r14 ? 1 : (r13 == r14 ? 0 : -1))
            if (r13 >= 0) goto L_0x01e2
            goto L_0x01cf
        L_0x01e2:
            if (r0 == 0) goto L_0x029f
            int r0 = (r10 > r5 ? 1 : (r10 == r5 ? 0 : -1))
            if (r0 <= 0) goto L_0x02a2
            r12.readBytesCount = r10     // Catch:{ Exception -> 0x04a8 }
            int r0 = r12.uploadChunkSize     // Catch:{ Exception -> 0x04a8 }
            long r13 = (long) r0     // Catch:{ Exception -> 0x04a8 }
            long r13 = r10 / r13
            int r0 = (int) r13     // Catch:{ Exception -> 0x04a8 }
            r12.currentPartNum = r0     // Catch:{ Exception -> 0x04a8 }
            boolean r0 = r12.isBigFile     // Catch:{ Exception -> 0x04a8 }
            if (r0 != 0) goto L_0x0260
            r0 = 0
        L_0x01f7:
            long r10 = (long) r0     // Catch:{ Exception -> 0x04a8 }
            long r13 = r12.readBytesCount     // Catch:{ Exception -> 0x04a8 }
            int r15 = r12.uploadChunkSize     // Catch:{ Exception -> 0x04a8 }
            r18 = r3
            long r2 = (long) r15     // Catch:{ Exception -> 0x04a8 }
            long r13 = r13 / r2
            int r2 = (r10 > r13 ? 1 : (r10 == r13 ? 0 : -1))
            if (r2 >= 0) goto L_0x02a4
            java.io.RandomAccessFile r2 = r12.stream     // Catch:{ Exception -> 0x04a8 }
            byte[] r3 = r12.readBuffer     // Catch:{ Exception -> 0x04a8 }
            int r2 = r2.read(r3)     // Catch:{ Exception -> 0x04a8 }
            boolean r3 = r12.isEncrypted     // Catch:{ Exception -> 0x04a8 }
            if (r3 == 0) goto L_0x021a
            int r3 = r2 % 16
            if (r3 == 0) goto L_0x021a
            int r3 = r2 % 16
            int r3 = 16 - r3
            int r3 = r3 + r8
            goto L_0x021b
        L_0x021a:
            r3 = 0
        L_0x021b:
            org.telegram.tgnet.NativeByteBuffer r10 = new org.telegram.tgnet.NativeByteBuffer     // Catch:{ Exception -> 0x04a8 }
            int r11 = r2 + r3
            r10.<init>((int) r11)     // Catch:{ Exception -> 0x04a8 }
            int r13 = r12.uploadChunkSize     // Catch:{ Exception -> 0x04a8 }
            if (r2 != r13) goto L_0x022d
            int r13 = r12.totalPartsCount     // Catch:{ Exception -> 0x04a8 }
            int r14 = r12.currentPartNum     // Catch:{ Exception -> 0x04a8 }
            int r14 = r14 + r1
            if (r13 != r14) goto L_0x022f
        L_0x022d:
            r12.isLastPart = r1     // Catch:{ Exception -> 0x04a8 }
        L_0x022f:
            byte[] r13 = r12.readBuffer     // Catch:{ Exception -> 0x04a8 }
            r10.writeBytes(r13, r8, r2)     // Catch:{ Exception -> 0x04a8 }
            boolean r2 = r12.isEncrypted     // Catch:{ Exception -> 0x04a8 }
            if (r2 == 0) goto L_0x0258
            r2 = 0
        L_0x0239:
            if (r2 >= r3) goto L_0x0241
            r10.writeByte((int) r8)     // Catch:{ Exception -> 0x04a8 }
            int r2 = r2 + 1
            goto L_0x0239
        L_0x0241:
            java.nio.ByteBuffer r2 = r10.buffer     // Catch:{ Exception -> 0x04a8 }
            byte[] r3 = r12.key     // Catch:{ Exception -> 0x04a8 }
            byte[] r13 = r12.ivChange     // Catch:{ Exception -> 0x04a8 }
            r22 = 1
            r23 = 1
            r24 = 0
            r19 = r2
            r20 = r3
            r21 = r13
            r25 = r11
            org.telegram.messenger.Utilities.aesIgeEncryption(r19, r20, r21, r22, r23, r24, r25)     // Catch:{ Exception -> 0x04a8 }
        L_0x0258:
            r10.reuse()     // Catch:{ Exception -> 0x04a8 }
            int r0 = r0 + 1
            r3 = r18
            goto L_0x01f7
        L_0x0260:
            r18 = r3
            java.io.RandomAccessFile r0 = r12.stream     // Catch:{ Exception -> 0x04a8 }
            r0.seek(r10)     // Catch:{ Exception -> 0x04a8 }
            boolean r0 = r12.isEncrypted     // Catch:{ Exception -> 0x04a8 }
            if (r0 == 0) goto L_0x02a4
            android.content.SharedPreferences r0 = r12.preferences     // Catch:{ Exception -> 0x04a8 }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x04a8 }
            r2.<init>()     // Catch:{ Exception -> 0x04a8 }
            java.lang.String r3 = r12.fileKey     // Catch:{ Exception -> 0x04a8 }
            r2.append(r3)     // Catch:{ Exception -> 0x04a8 }
            java.lang.String r3 = "_ivc"
            r2.append(r3)     // Catch:{ Exception -> 0x04a8 }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x04a8 }
            java.lang.String r0 = r0.getString(r2, r4)     // Catch:{ Exception -> 0x04a8 }
            if (r0 == 0) goto L_0x029a
            byte[] r0 = org.telegram.messenger.Utilities.hexToBytes(r0)     // Catch:{ Exception -> 0x04a8 }
            r12.ivChange = r0     // Catch:{ Exception -> 0x04a8 }
            byte[] r0 = r12.ivChange     // Catch:{ Exception -> 0x04a8 }
            if (r0 == 0) goto L_0x0295
            byte[] r0 = r12.ivChange     // Catch:{ Exception -> 0x04a8 }
            int r0 = r0.length     // Catch:{ Exception -> 0x04a8 }
            if (r0 == r7) goto L_0x02a4
        L_0x0295:
            r12.readBytesCount = r5     // Catch:{ Exception -> 0x04a8 }
            r12.currentPartNum = r8     // Catch:{ Exception -> 0x04a8 }
            goto L_0x02a2
        L_0x029a:
            r12.readBytesCount = r5     // Catch:{ Exception -> 0x04a8 }
            r12.currentPartNum = r8     // Catch:{ Exception -> 0x04a8 }
            goto L_0x02a2
        L_0x029f:
            r18 = r3
            goto L_0x02a4
        L_0x02a2:
            r18 = 1
        L_0x02a4:
            if (r18 == 0) goto L_0x02e2
            boolean r0 = r12.isEncrypted     // Catch:{ Exception -> 0x04a8 }
            if (r0 == 0) goto L_0x02cb
            byte[] r0 = new byte[r7]     // Catch:{ Exception -> 0x04a8 }
            r12.iv = r0     // Catch:{ Exception -> 0x04a8 }
            byte[] r0 = new byte[r7]     // Catch:{ Exception -> 0x04a8 }
            r12.key = r0     // Catch:{ Exception -> 0x04a8 }
            byte[] r0 = new byte[r7]     // Catch:{ Exception -> 0x04a8 }
            r12.ivChange = r0     // Catch:{ Exception -> 0x04a8 }
            java.security.SecureRandom r0 = org.telegram.messenger.Utilities.random     // Catch:{ Exception -> 0x04a8 }
            byte[] r2 = r12.iv     // Catch:{ Exception -> 0x04a8 }
            r0.nextBytes(r2)     // Catch:{ Exception -> 0x04a8 }
            java.security.SecureRandom r0 = org.telegram.messenger.Utilities.random     // Catch:{ Exception -> 0x04a8 }
            byte[] r2 = r12.key     // Catch:{ Exception -> 0x04a8 }
            r0.nextBytes(r2)     // Catch:{ Exception -> 0x04a8 }
            byte[] r0 = r12.iv     // Catch:{ Exception -> 0x04a8 }
            byte[] r2 = r12.ivChange     // Catch:{ Exception -> 0x04a8 }
            java.lang.System.arraycopy(r0, r8, r2, r8, r7)     // Catch:{ Exception -> 0x04a8 }
        L_0x02cb:
            java.security.SecureRandom r0 = org.telegram.messenger.Utilities.random     // Catch:{ Exception -> 0x04a8 }
            long r2 = r0.nextLong()     // Catch:{ Exception -> 0x04a8 }
            r12.currentFileId = r2     // Catch:{ Exception -> 0x04a8 }
            boolean r0 = r12.nextPartFirst     // Catch:{ Exception -> 0x04a8 }
            if (r0 != 0) goto L_0x02e2
            boolean r0 = r12.uploadFirstPartLater     // Catch:{ Exception -> 0x04a8 }
            if (r0 != 0) goto L_0x02e2
            int r0 = r12.estimatedSize     // Catch:{ Exception -> 0x04a8 }
            if (r0 != 0) goto L_0x02e2
            r27.storeFileUploadInfo()     // Catch:{ Exception -> 0x04a8 }
        L_0x02e2:
            boolean r0 = r12.isEncrypted     // Catch:{ Exception -> 0x04a8 }
            if (r0 == 0) goto L_0x0318
            java.lang.String r0 = "MD5"
            java.security.MessageDigest r0 = java.security.MessageDigest.getInstance(r0)     // Catch:{ Exception -> 0x0314 }
            byte[] r2 = new byte[r9]     // Catch:{ Exception -> 0x0314 }
            byte[] r3 = r12.key     // Catch:{ Exception -> 0x0314 }
            java.lang.System.arraycopy(r3, r8, r2, r8, r7)     // Catch:{ Exception -> 0x0314 }
            byte[] r3 = r12.iv     // Catch:{ Exception -> 0x0314 }
            java.lang.System.arraycopy(r3, r8, r2, r7, r7)     // Catch:{ Exception -> 0x0314 }
            byte[] r0 = r0.digest(r2)     // Catch:{ Exception -> 0x0314 }
            r2 = 0
        L_0x02fd:
            r3 = 4
            if (r2 >= r3) goto L_0x0318
            int r3 = r12.fingerprint     // Catch:{ Exception -> 0x0314 }
            byte r9 = r0[r2]     // Catch:{ Exception -> 0x0314 }
            int r10 = r2 + 4
            byte r10 = r0[r10]     // Catch:{ Exception -> 0x0314 }
            r9 = r9 ^ r10
            r9 = r9 & 255(0xff, float:3.57E-43)
            int r10 = r2 * 8
            int r9 = r9 << r10
            r3 = r3 | r9
            r12.fingerprint = r3     // Catch:{ Exception -> 0x0314 }
            int r2 = r2 + 1
            goto L_0x02fd
        L_0x0314:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ Exception -> 0x04a8 }
        L_0x0318:
            long r2 = r12.readBytesCount     // Catch:{ Exception -> 0x04a8 }
            r12.uploadedBytesCount = r2     // Catch:{ Exception -> 0x04a8 }
            int r0 = r12.currentPartNum     // Catch:{ Exception -> 0x04a8 }
            r12.lastSavedPartNum = r0     // Catch:{ Exception -> 0x04a8 }
            boolean r0 = r12.uploadFirstPartLater     // Catch:{ Exception -> 0x04a8 }
            if (r0 == 0) goto L_0x034a
            boolean r0 = r12.isBigFile     // Catch:{ Exception -> 0x04a8 }
            if (r0 == 0) goto L_0x0336
            java.io.RandomAccessFile r0 = r12.stream     // Catch:{ Exception -> 0x04a8 }
            int r2 = r12.uploadChunkSize     // Catch:{ Exception -> 0x04a8 }
            long r2 = (long) r2     // Catch:{ Exception -> 0x04a8 }
            r0.seek(r2)     // Catch:{ Exception -> 0x04a8 }
            int r0 = r12.uploadChunkSize     // Catch:{ Exception -> 0x04a8 }
            long r2 = (long) r0     // Catch:{ Exception -> 0x04a8 }
            r12.readBytesCount = r2     // Catch:{ Exception -> 0x04a8 }
            goto L_0x033f
        L_0x0336:
            java.io.RandomAccessFile r0 = r12.stream     // Catch:{ Exception -> 0x04a8 }
            r2 = 1024(0x400, double:5.06E-321)
            r0.seek(r2)     // Catch:{ Exception -> 0x04a8 }
            r12.readBytesCount = r2     // Catch:{ Exception -> 0x04a8 }
        L_0x033f:
            r12.currentPartNum = r1     // Catch:{ Exception -> 0x04a8 }
            goto L_0x034a
        L_0x0342:
            java.lang.Exception r0 = new java.lang.Exception     // Catch:{ Exception -> 0x04a8 }
            java.lang.String r1 = "trying to upload internal file"
            r0.<init>(r1)     // Catch:{ Exception -> 0x04a8 }
            throw r0     // Catch:{ Exception -> 0x04a8 }
        L_0x034a:
            int r0 = r12.estimatedSize     // Catch:{ Exception -> 0x04a8 }
            if (r0 == 0) goto L_0x035b
            long r2 = r12.readBytesCount     // Catch:{ Exception -> 0x04a8 }
            int r0 = r12.uploadChunkSize     // Catch:{ Exception -> 0x04a8 }
            long r9 = (long) r0     // Catch:{ Exception -> 0x04a8 }
            long r2 = r2 + r9
            long r9 = r12.availableSize     // Catch:{ Exception -> 0x04a8 }
            int r0 = (r2 > r9 ? 1 : (r2 == r9 ? 0 : -1))
            if (r0 <= 0) goto L_0x035b
            return
        L_0x035b:
            boolean r0 = r12.nextPartFirst     // Catch:{ Exception -> 0x04a8 }
            if (r0 == 0) goto L_0x037e
            java.io.RandomAccessFile r0 = r12.stream     // Catch:{ Exception -> 0x04a8 }
            r0.seek(r5)     // Catch:{ Exception -> 0x04a8 }
            boolean r0 = r12.isBigFile     // Catch:{ Exception -> 0x04a8 }
            if (r0 == 0) goto L_0x0371
            java.io.RandomAccessFile r0 = r12.stream     // Catch:{ Exception -> 0x04a8 }
            byte[] r2 = r12.readBuffer     // Catch:{ Exception -> 0x04a8 }
            int r0 = r0.read(r2)     // Catch:{ Exception -> 0x04a8 }
            goto L_0x037b
        L_0x0371:
            java.io.RandomAccessFile r0 = r12.stream     // Catch:{ Exception -> 0x04a8 }
            byte[] r2 = r12.readBuffer     // Catch:{ Exception -> 0x04a8 }
            r3 = 1024(0x400, float:1.435E-42)
            int r0 = r0.read(r2, r8, r3)     // Catch:{ Exception -> 0x04a8 }
        L_0x037b:
            r12.currentPartNum = r8     // Catch:{ Exception -> 0x04a8 }
            goto L_0x0386
        L_0x037e:
            java.io.RandomAccessFile r0 = r12.stream     // Catch:{ Exception -> 0x04a8 }
            byte[] r2 = r12.readBuffer     // Catch:{ Exception -> 0x04a8 }
            int r0 = r0.read(r2)     // Catch:{ Exception -> 0x04a8 }
        L_0x0386:
            r2 = -1
            if (r0 != r2) goto L_0x038a
            return
        L_0x038a:
            boolean r3 = r12.isEncrypted     // Catch:{ Exception -> 0x04a8 }
            if (r3 == 0) goto L_0x0398
            int r3 = r0 % 16
            if (r3 == 0) goto L_0x0398
            int r3 = r0 % 16
            int r3 = 16 - r3
            int r3 = r3 + r8
            goto L_0x0399
        L_0x0398:
            r3 = 0
        L_0x0399:
            org.telegram.tgnet.NativeByteBuffer r5 = new org.telegram.tgnet.NativeByteBuffer     // Catch:{ Exception -> 0x04a8 }
            int r6 = r0 + r3
            r5.<init>((int) r6)     // Catch:{ Exception -> 0x04a8 }
            boolean r9 = r12.nextPartFirst     // Catch:{ Exception -> 0x04a8 }
            if (r9 != 0) goto L_0x03b3
            int r9 = r12.uploadChunkSize     // Catch:{ Exception -> 0x04a8 }
            if (r0 != r9) goto L_0x03b3
            int r9 = r12.estimatedSize     // Catch:{ Exception -> 0x04a8 }
            if (r9 != 0) goto L_0x03be
            int r9 = r12.totalPartsCount     // Catch:{ Exception -> 0x04a8 }
            int r10 = r12.currentPartNum     // Catch:{ Exception -> 0x04a8 }
            int r10 = r10 + r1
            if (r9 != r10) goto L_0x03be
        L_0x03b3:
            boolean r9 = r12.uploadFirstPartLater     // Catch:{ Exception -> 0x04a8 }
            if (r9 == 0) goto L_0x03bc
            r12.nextPartFirst = r1     // Catch:{ Exception -> 0x04a8 }
            r12.uploadFirstPartLater = r8     // Catch:{ Exception -> 0x04a8 }
            goto L_0x03be
        L_0x03bc:
            r12.isLastPart = r1     // Catch:{ Exception -> 0x04a8 }
        L_0x03be:
            byte[] r9 = r12.readBuffer     // Catch:{ Exception -> 0x04a8 }
            r5.writeBytes(r9, r8, r0)     // Catch:{ Exception -> 0x04a8 }
            boolean r9 = r12.isEncrypted     // Catch:{ Exception -> 0x04a8 }
            if (r9 == 0) goto L_0x03fb
            r4 = 0
        L_0x03c8:
            if (r4 >= r3) goto L_0x03d0
            r5.writeByte((int) r8)     // Catch:{ Exception -> 0x04a8 }
            int r4 = r4 + 1
            goto L_0x03c8
        L_0x03d0:
            java.nio.ByteBuffer r3 = r5.buffer     // Catch:{ Exception -> 0x04a8 }
            byte[] r4 = r12.key     // Catch:{ Exception -> 0x04a8 }
            byte[] r9 = r12.ivChange     // Catch:{ Exception -> 0x04a8 }
            r21 = 1
            r22 = 1
            r23 = 0
            r18 = r3
            r19 = r4
            r20 = r9
            r24 = r6
            org.telegram.messenger.Utilities.aesIgeEncryption(r18, r19, r20, r21, r22, r23, r24)     // Catch:{ Exception -> 0x04a8 }
            java.util.ArrayList<byte[]> r3 = r12.freeRequestIvs     // Catch:{ Exception -> 0x04a8 }
            java.lang.Object r3 = r3.get(r8)     // Catch:{ Exception -> 0x04a8 }
            byte[] r3 = (byte[]) r3     // Catch:{ Exception -> 0x04a8 }
            byte[] r4 = r12.ivChange     // Catch:{ Exception -> 0x04a8 }
            java.lang.System.arraycopy(r4, r8, r3, r8, r7)     // Catch:{ Exception -> 0x04a8 }
            java.util.ArrayList<byte[]> r4 = r12.freeRequestIvs     // Catch:{ Exception -> 0x04a8 }
            r4.remove(r8)     // Catch:{ Exception -> 0x04a8 }
            r6 = r3
            goto L_0x03fc
        L_0x03fb:
            r6 = r4
        L_0x03fc:
            boolean r3 = r12.isBigFile     // Catch:{ Exception -> 0x04a8 }
            if (r3 == 0) goto L_0x041e
            org.telegram.tgnet.TLRPC$TL_upload_saveBigFilePart r3 = new org.telegram.tgnet.TLRPC$TL_upload_saveBigFilePart     // Catch:{ Exception -> 0x04a8 }
            r3.<init>()     // Catch:{ Exception -> 0x04a8 }
            int r4 = r12.currentPartNum     // Catch:{ Exception -> 0x04a8 }
            r3.file_part = r4     // Catch:{ Exception -> 0x04a8 }
            long r9 = r12.currentFileId     // Catch:{ Exception -> 0x04a8 }
            r3.file_id = r9     // Catch:{ Exception -> 0x04a8 }
            int r7 = r12.estimatedSize     // Catch:{ Exception -> 0x04a8 }
            if (r7 == 0) goto L_0x0414
            r3.file_total_parts = r2     // Catch:{ Exception -> 0x04a8 }
            goto L_0x0418
        L_0x0414:
            int r2 = r12.totalPartsCount     // Catch:{ Exception -> 0x04a8 }
            r3.file_total_parts = r2     // Catch:{ Exception -> 0x04a8 }
        L_0x0418:
            r3.bytes = r5     // Catch:{ Exception -> 0x04a8 }
            r19 = r3
            r9 = r4
            goto L_0x0430
        L_0x041e:
            org.telegram.tgnet.TLRPC$TL_upload_saveFilePart r2 = new org.telegram.tgnet.TLRPC$TL_upload_saveFilePart     // Catch:{ Exception -> 0x04a8 }
            r2.<init>()     // Catch:{ Exception -> 0x04a8 }
            int r3 = r12.currentPartNum     // Catch:{ Exception -> 0x04a8 }
            r2.file_part = r3     // Catch:{ Exception -> 0x04a8 }
            long r9 = r12.currentFileId     // Catch:{ Exception -> 0x04a8 }
            r2.file_id = r9     // Catch:{ Exception -> 0x04a8 }
            r2.bytes = r5     // Catch:{ Exception -> 0x04a8 }
            r19 = r2
            r9 = r3
        L_0x0430:
            boolean r2 = r12.isLastPart     // Catch:{ Exception -> 0x04a8 }
            if (r2 == 0) goto L_0x0446
            boolean r2 = r12.nextPartFirst     // Catch:{ Exception -> 0x04a8 }
            if (r2 == 0) goto L_0x0446
            r12.nextPartFirst = r8     // Catch:{ Exception -> 0x04a8 }
            int r2 = r12.totalPartsCount     // Catch:{ Exception -> 0x04a8 }
            int r2 = r2 - r1
            r12.currentPartNum = r2     // Catch:{ Exception -> 0x04a8 }
            java.io.RandomAccessFile r2 = r12.stream     // Catch:{ Exception -> 0x04a8 }
            long r3 = r12.totalFileSize     // Catch:{ Exception -> 0x04a8 }
            r2.seek(r3)     // Catch:{ Exception -> 0x04a8 }
        L_0x0446:
            long r2 = r12.readBytesCount     // Catch:{ Exception -> 0x04a8 }
            long r4 = (long) r0     // Catch:{ Exception -> 0x04a8 }
            long r2 = r2 + r4
            r12.readBytesCount = r2     // Catch:{ Exception -> 0x04a8 }
            int r2 = r12.currentPartNum
            int r2 = r2 + r1
            r12.currentPartNum = r2
            int r2 = r12.currentUploadRequetsCount
            int r2 = r2 + r1
            r12.currentUploadRequetsCount = r2
            int r13 = r12.requestNum
            int r1 = r13 + 1
            r12.requestNum = r1
            int r1 = r9 + r0
            long r10 = (long) r1
            int r1 = r19.getObjectSize()
            r17 = 4
            int r4 = r1 + 4
            int r3 = r12.operationGuid
            boolean r1 = r12.slowNetwork
            if (r1 == 0) goto L_0x0470
            r25 = 4
            goto L_0x0478
        L_0x0470:
            int r1 = r13 % 4
            int r1 = r1 << 16
            r2 = r1 | 4
            r25 = r2
        L_0x0478:
            int r1 = r12.currentAccount
            org.telegram.tgnet.ConnectionsManager r18 = org.telegram.tgnet.ConnectionsManager.getInstance(r1)
            org.telegram.messenger.-$$Lambda$FileUploadOperation$XMcVvcrqfWd56m49RmvLW8_t4Os r20 = new org.telegram.messenger.-$$Lambda$FileUploadOperation$XMcVvcrqfWd56m49RmvLW8_t4Os
            r1 = r20
            r2 = r27
            r5 = r6
            r6 = r13
            r7 = r0
            r8 = r9
            r9 = r10
            r11 = r19
            r1.<init>(r3, r4, r5, r6, r7, r8, r9, r11)
            r21 = 0
            org.telegram.messenger.-$$Lambda$FileUploadOperation$H-o0ouVev-JFhE9lBzpUHg6WYPI r0 = new org.telegram.messenger.-$$Lambda$FileUploadOperation$H-o0ouVev-JFhE9lBzpUHg6WYPI
            r0.<init>()
            r23 = 0
            r24 = 2147483647(0x7fffffff, float:NaN)
            r26 = 1
            r22 = r0
            int r0 = r18.sendRequest(r19, r20, r21, r22, r23, r24, r25, r26)
            android.util.SparseIntArray r1 = r12.requestTokens
            r1.put(r13, r0)
            return
        L_0x04a8:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            r1 = 4
            r12.state = r1
            org.telegram.messenger.FileUploadOperation$FileUploadOperationDelegate r0 = r12.delegate
            r0.didFailedUploadingFile(r12)
            r27.cleanup()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.FileUploadOperation.startUploadRequest():void");
    }

    public /* synthetic */ void lambda$startUploadRequest$4$FileUploadOperation(int i, int i2, byte[] bArr, int i3, int i4, int i5, long j, TLObject tLObject, TLObject tLObject2, TLRPC.TL_error tL_error) {
        long j2;
        TLRPC.InputEncryptedFile inputEncryptedFile;
        TLRPC.InputFile inputFile;
        int i6 = i2;
        byte[] bArr2 = bArr;
        int i7 = i5;
        TLObject tLObject3 = tLObject2;
        if (i == this.operationGuid) {
            int currentNetworkType = tLObject3 != null ? tLObject3.networkType : ApplicationLoader.getCurrentNetworkType();
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
            if (!(tLObject3 instanceof TLRPC.TL_boolTrue)) {
                if (tLObject != null) {
                    FileLog.e("23123");
                }
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
                this.currentUploadRequetsCount--;
                if (this.isLastPart && this.currentUploadRequetsCount == 0 && this.state == 1) {
                    this.state = 3;
                    if (this.key == null) {
                        if (this.isBigFile) {
                            inputFile = new TLRPC.TL_inputFileBig();
                        } else {
                            inputFile = new TLRPC.TL_inputFile();
                            inputFile.md5_checksum = "";
                        }
                        inputFile.parts = this.currentPartNum;
                        inputFile.id = this.currentFileId;
                        String str = this.uploadingFilePath;
                        inputFile.name = str.substring(str.lastIndexOf("/") + 1);
                        this.delegate.didFinishUploadingFile(this, inputFile, (TLRPC.InputEncryptedFile) null, (byte[]) null, (byte[]) null);
                        cleanup();
                    } else {
                        if (this.isBigFile) {
                            inputEncryptedFile = new TLRPC.TL_inputEncryptedFileBigUploaded();
                        } else {
                            inputEncryptedFile = new TLRPC.TL_inputEncryptedFileUploaded();
                            inputEncryptedFile.md5_checksum = "";
                        }
                        inputEncryptedFile.parts = this.currentPartNum;
                        inputEncryptedFile.id = this.currentFileId;
                        inputEncryptedFile.key_fingerprint = this.fingerprint;
                        this.delegate.didFinishUploadingFile(this, (TLRPC.InputFile) null, inputEncryptedFile, this.key, this.iv);
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
                } else if (this.currentUploadRequetsCount < this.maxRequestsCount) {
                    if (this.estimatedSize == 0 && !this.uploadFirstPartLater && !this.nextPartFirst) {
                        if (this.saveInfoTimes >= 4) {
                            this.saveInfoTimes = 0;
                        }
                        int i11 = this.lastSavedPartNum;
                        if (i7 == i11) {
                            this.lastSavedPartNum = i11 + 1;
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
                            if ((this.isBigFile && j3 % 1048576 == 0) || (!this.isBigFile && this.saveInfoTimes == 0)) {
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

    public /* synthetic */ void lambda$startUploadRequest$6$FileUploadOperation() {
        Utilities.stageQueue.postRunnable(new Runnable() {
            public final void run() {
                FileUploadOperation.this.lambda$null$5$FileUploadOperation();
            }
        });
    }

    public /* synthetic */ void lambda$null$5$FileUploadOperation() {
        if (this.currentUploadRequetsCount < this.maxRequestsCount) {
            startUploadRequest();
        }
    }
}
