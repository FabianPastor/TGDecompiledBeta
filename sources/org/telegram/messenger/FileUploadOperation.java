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
                public final /* synthetic */ boolean f$1;

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
            public final /* synthetic */ long f$1;
            public final /* synthetic */ long f$2;

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
    /* JADX WARNING: Removed duplicated region for block: B:123:0x02bf A[Catch:{ Exception -> 0x04c7 }] */
    /* JADX WARNING: Removed duplicated region for block: B:126:0x02c5 A[Catch:{ Exception -> 0x04c7 }] */
    /* JADX WARNING: Removed duplicated region for block: B:138:0x0303  */
    /* JADX WARNING: Removed duplicated region for block: B:150:0x0341 A[Catch:{ Exception -> 0x04c7 }] */
    /* JADX WARNING: Removed duplicated region for block: B:86:0x0204 A[Catch:{ Exception -> 0x04c7 }] */
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
            r12.started = r1     // Catch:{ Exception -> 0x04c7 }
            java.io.RandomAccessFile r0 = r12.stream     // Catch:{ Exception -> 0x04c7 }
            r3 = 1024(0x400, float:1.435E-42)
            r4 = 0
            r5 = 0
            r7 = 32
            r8 = 0
            if (r0 != 0) goto L_0x036b
            java.io.File r9 = new java.io.File     // Catch:{ Exception -> 0x04c7 }
            java.lang.String r0 = r12.uploadingFilePath     // Catch:{ Exception -> 0x04c7 }
            r9.<init>(r0)     // Catch:{ Exception -> 0x04c7 }
            android.net.Uri r0 = android.net.Uri.fromFile(r9)     // Catch:{ Exception -> 0x04c7 }
            boolean r0 = org.telegram.messenger.AndroidUtilities.isInternalUri((android.net.Uri) r0)     // Catch:{ Exception -> 0x04c7 }
            java.lang.String r10 = "trying to upload internal file"
            if (r0 != 0) goto L_0x0365
            java.io.RandomAccessFile r0 = new java.io.RandomAccessFile     // Catch:{ Exception -> 0x04c7 }
            java.lang.String r11 = "r"
            r0.<init>(r9, r11)     // Catch:{ Exception -> 0x04c7 }
            r12.stream = r0     // Catch:{ Exception -> 0x04c7 }
            java.lang.Class<java.io.FileDescriptor> r0 = java.io.FileDescriptor.class
            java.lang.String r11 = "getInt$"
            java.lang.Class[] r13 = new java.lang.Class[r8]     // Catch:{ all -> 0x0053 }
            java.lang.reflect.Method r0 = r0.getDeclaredMethod(r11, r13)     // Catch:{ all -> 0x0053 }
            java.io.RandomAccessFile r11 = r12.stream     // Catch:{ all -> 0x0053 }
            java.io.FileDescriptor r11 = r11.getFD()     // Catch:{ all -> 0x0053 }
            java.lang.Object[] r13 = new java.lang.Object[r8]     // Catch:{ all -> 0x0053 }
            java.lang.Object r0 = r0.invoke(r11, r13)     // Catch:{ all -> 0x0053 }
            java.lang.Integer r0 = (java.lang.Integer) r0     // Catch:{ all -> 0x0053 }
            int r0 = r0.intValue()     // Catch:{ all -> 0x0053 }
            boolean r0 = org.telegram.messenger.AndroidUtilities.isInternalUri((int) r0)     // Catch:{ all -> 0x0053 }
            goto L_0x0058
        L_0x0053:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ Exception -> 0x04c7 }
            r0 = 0
        L_0x0058:
            if (r0 != 0) goto L_0x035f
            int r0 = r12.estimatedSize     // Catch:{ Exception -> 0x04c7 }
            if (r0 == 0) goto L_0x0064
            int r0 = r12.estimatedSize     // Catch:{ Exception -> 0x04c7 }
            long r9 = (long) r0     // Catch:{ Exception -> 0x04c7 }
            r12.totalFileSize = r9     // Catch:{ Exception -> 0x04c7 }
            goto L_0x006a
        L_0x0064:
            long r9 = r9.length()     // Catch:{ Exception -> 0x04c7 }
            r12.totalFileSize = r9     // Catch:{ Exception -> 0x04c7 }
        L_0x006a:
            long r9 = r12.totalFileSize     // Catch:{ Exception -> 0x04c7 }
            r13 = 10485760(0xa00000, double:5.180654E-317)
            int r0 = (r9 > r13 ? 1 : (r9 == r13 ? 0 : -1))
            if (r0 <= 0) goto L_0x0075
            r12.isBigFile = r1     // Catch:{ Exception -> 0x04c7 }
        L_0x0075:
            boolean r0 = r12.slowNetwork     // Catch:{ Exception -> 0x04c7 }
            if (r0 == 0) goto L_0x007c
            r9 = 32
            goto L_0x007e
        L_0x007c:
            r9 = 128(0x80, double:6.32E-322)
        L_0x007e:
            long r13 = r12.totalFileSize     // Catch:{ Exception -> 0x04c7 }
            r15 = 4096000(0x3e8000, double:2.023693E-317)
            long r13 = r13 + r15
            r17 = 1
            long r13 = r13 - r17
            long r13 = r13 / r15
            long r9 = java.lang.Math.max(r9, r13)     // Catch:{ Exception -> 0x04c7 }
            int r0 = (int) r9     // Catch:{ Exception -> 0x04c7 }
            r12.uploadChunkSize = r0     // Catch:{ Exception -> 0x04c7 }
            int r0 = r3 % r0
            r9 = 64
            if (r0 == 0) goto L_0x00a1
            r0 = 64
        L_0x0098:
            int r10 = r12.uploadChunkSize     // Catch:{ Exception -> 0x04c7 }
            if (r10 <= r0) goto L_0x009f
            int r0 = r0 * 2
            goto L_0x0098
        L_0x009f:
            r12.uploadChunkSize = r0     // Catch:{ Exception -> 0x04c7 }
        L_0x00a1:
            boolean r0 = r12.slowNetwork     // Catch:{ Exception -> 0x04c7 }
            if (r0 == 0) goto L_0x00a8
            r0 = 32
            goto L_0x00aa
        L_0x00a8:
            r0 = 2048(0x800, float:2.87E-42)
        L_0x00aa:
            int r10 = r12.uploadChunkSize     // Catch:{ Exception -> 0x04c7 }
            int r0 = r0 / r10
            int r0 = java.lang.Math.max(r1, r0)     // Catch:{ Exception -> 0x04c7 }
            r12.maxRequestsCount = r0     // Catch:{ Exception -> 0x04c7 }
            boolean r0 = r12.isEncrypted     // Catch:{ Exception -> 0x04c7 }
            if (r0 == 0) goto L_0x00cf
            java.util.ArrayList r0 = new java.util.ArrayList     // Catch:{ Exception -> 0x04c7 }
            int r10 = r12.maxRequestsCount     // Catch:{ Exception -> 0x04c7 }
            r0.<init>(r10)     // Catch:{ Exception -> 0x04c7 }
            r12.freeRequestIvs = r0     // Catch:{ Exception -> 0x04c7 }
            r0 = 0
        L_0x00c1:
            int r10 = r12.maxRequestsCount     // Catch:{ Exception -> 0x04c7 }
            if (r0 >= r10) goto L_0x00cf
            java.util.ArrayList<byte[]> r10 = r12.freeRequestIvs     // Catch:{ Exception -> 0x04c7 }
            byte[] r11 = new byte[r7]     // Catch:{ Exception -> 0x04c7 }
            r10.add(r11)     // Catch:{ Exception -> 0x04c7 }
            int r0 = r0 + 1
            goto L_0x00c1
        L_0x00cf:
            int r0 = r12.uploadChunkSize     // Catch:{ Exception -> 0x04c7 }
            int r0 = r0 * 1024
            r12.uploadChunkSize = r0     // Catch:{ Exception -> 0x04c7 }
            r27.calcTotalPartsCount()     // Catch:{ Exception -> 0x04c7 }
            int r0 = r12.uploadChunkSize     // Catch:{ Exception -> 0x04c7 }
            byte[] r0 = new byte[r0]     // Catch:{ Exception -> 0x04c7 }
            r12.readBuffer = r0     // Catch:{ Exception -> 0x04c7 }
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x04c7 }
            r0.<init>()     // Catch:{ Exception -> 0x04c7 }
            java.lang.String r10 = r12.uploadingFilePath     // Catch:{ Exception -> 0x04c7 }
            r0.append(r10)     // Catch:{ Exception -> 0x04c7 }
            boolean r10 = r12.isEncrypted     // Catch:{ Exception -> 0x04c7 }
            if (r10 == 0) goto L_0x00ef
            java.lang.String r10 = "enc"
            goto L_0x00f1
        L_0x00ef:
            java.lang.String r10 = ""
        L_0x00f1:
            r0.append(r10)     // Catch:{ Exception -> 0x04c7 }
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x04c7 }
            java.lang.String r0 = org.telegram.messenger.Utilities.MD5(r0)     // Catch:{ Exception -> 0x04c7 }
            r12.fileKey = r0     // Catch:{ Exception -> 0x04c7 }
            android.content.SharedPreferences r0 = r12.preferences     // Catch:{ Exception -> 0x04c7 }
            java.lang.StringBuilder r10 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x04c7 }
            r10.<init>()     // Catch:{ Exception -> 0x04c7 }
            java.lang.String r11 = r12.fileKey     // Catch:{ Exception -> 0x04c7 }
            r10.append(r11)     // Catch:{ Exception -> 0x04c7 }
            java.lang.String r11 = "_size"
            r10.append(r11)     // Catch:{ Exception -> 0x04c7 }
            java.lang.String r10 = r10.toString()     // Catch:{ Exception -> 0x04c7 }
            long r10 = r0.getLong(r10, r5)     // Catch:{ Exception -> 0x04c7 }
            long r13 = java.lang.System.currentTimeMillis()     // Catch:{ Exception -> 0x04c7 }
            r15 = 1000(0x3e8, double:4.94E-321)
            long r13 = r13 / r15
            int r0 = (int) r13     // Catch:{ Exception -> 0x04c7 }
            r12.uploadStartTime = r0     // Catch:{ Exception -> 0x04c7 }
            boolean r0 = r12.uploadFirstPartLater     // Catch:{ Exception -> 0x04c7 }
            if (r0 != 0) goto L_0x02c2
            boolean r0 = r12.nextPartFirst     // Catch:{ Exception -> 0x04c7 }
            if (r0 != 0) goto L_0x02c2
            int r0 = r12.estimatedSize     // Catch:{ Exception -> 0x04c7 }
            if (r0 != 0) goto L_0x02c2
            long r13 = r12.totalFileSize     // Catch:{ Exception -> 0x04c7 }
            int r0 = (r10 > r13 ? 1 : (r10 == r13 ? 0 : -1))
            if (r0 != 0) goto L_0x02c2
            android.content.SharedPreferences r0 = r12.preferences     // Catch:{ Exception -> 0x04c7 }
            java.lang.StringBuilder r10 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x04c7 }
            r10.<init>()     // Catch:{ Exception -> 0x04c7 }
            java.lang.String r11 = r12.fileKey     // Catch:{ Exception -> 0x04c7 }
            r10.append(r11)     // Catch:{ Exception -> 0x04c7 }
            java.lang.String r11 = "_id"
            r10.append(r11)     // Catch:{ Exception -> 0x04c7 }
            java.lang.String r10 = r10.toString()     // Catch:{ Exception -> 0x04c7 }
            long r10 = r0.getLong(r10, r5)     // Catch:{ Exception -> 0x04c7 }
            r12.currentFileId = r10     // Catch:{ Exception -> 0x04c7 }
            android.content.SharedPreferences r0 = r12.preferences     // Catch:{ Exception -> 0x04c7 }
            java.lang.StringBuilder r10 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x04c7 }
            r10.<init>()     // Catch:{ Exception -> 0x04c7 }
            java.lang.String r11 = r12.fileKey     // Catch:{ Exception -> 0x04c7 }
            r10.append(r11)     // Catch:{ Exception -> 0x04c7 }
            java.lang.String r11 = "_time"
            r10.append(r11)     // Catch:{ Exception -> 0x04c7 }
            java.lang.String r10 = r10.toString()     // Catch:{ Exception -> 0x04c7 }
            int r0 = r0.getInt(r10, r8)     // Catch:{ Exception -> 0x04c7 }
            android.content.SharedPreferences r10 = r12.preferences     // Catch:{ Exception -> 0x04c7 }
            java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x04c7 }
            r11.<init>()     // Catch:{ Exception -> 0x04c7 }
            java.lang.String r13 = r12.fileKey     // Catch:{ Exception -> 0x04c7 }
            r11.append(r13)     // Catch:{ Exception -> 0x04c7 }
            java.lang.String r13 = "_uploaded"
            r11.append(r13)     // Catch:{ Exception -> 0x04c7 }
            java.lang.String r11 = r11.toString()     // Catch:{ Exception -> 0x04c7 }
            long r10 = r10.getLong(r11, r5)     // Catch:{ Exception -> 0x04c7 }
            boolean r13 = r12.isEncrypted     // Catch:{ Exception -> 0x04c7 }
            if (r13 == 0) goto L_0x01de
            android.content.SharedPreferences r13 = r12.preferences     // Catch:{ Exception -> 0x04c7 }
            java.lang.StringBuilder r14 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x04c7 }
            r14.<init>()     // Catch:{ Exception -> 0x04c7 }
            java.lang.String r15 = r12.fileKey     // Catch:{ Exception -> 0x04c7 }
            r14.append(r15)     // Catch:{ Exception -> 0x04c7 }
            java.lang.String r15 = "_iv"
            r14.append(r15)     // Catch:{ Exception -> 0x04c7 }
            java.lang.String r14 = r14.toString()     // Catch:{ Exception -> 0x04c7 }
            java.lang.String r13 = r13.getString(r14, r4)     // Catch:{ Exception -> 0x04c7 }
            android.content.SharedPreferences r14 = r12.preferences     // Catch:{ Exception -> 0x04c7 }
            java.lang.StringBuilder r15 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x04c7 }
            r15.<init>()     // Catch:{ Exception -> 0x04c7 }
            java.lang.String r3 = r12.fileKey     // Catch:{ Exception -> 0x04c7 }
            r15.append(r3)     // Catch:{ Exception -> 0x04c7 }
            java.lang.String r3 = "_key"
            r15.append(r3)     // Catch:{ Exception -> 0x04c7 }
            java.lang.String r3 = r15.toString()     // Catch:{ Exception -> 0x04c7 }
            java.lang.String r3 = r14.getString(r3, r4)     // Catch:{ Exception -> 0x04c7 }
            if (r13 == 0) goto L_0x01dc
            if (r3 == 0) goto L_0x01dc
            byte[] r3 = org.telegram.messenger.Utilities.hexToBytes(r3)     // Catch:{ Exception -> 0x04c7 }
            r12.key = r3     // Catch:{ Exception -> 0x04c7 }
            byte[] r3 = org.telegram.messenger.Utilities.hexToBytes(r13)     // Catch:{ Exception -> 0x04c7 }
            r12.iv = r3     // Catch:{ Exception -> 0x04c7 }
            byte[] r13 = r12.key     // Catch:{ Exception -> 0x04c7 }
            if (r13 == 0) goto L_0x01dc
            if (r3 == 0) goto L_0x01dc
            byte[] r13 = r12.key     // Catch:{ Exception -> 0x04c7 }
            int r13 = r13.length     // Catch:{ Exception -> 0x04c7 }
            if (r13 != r7) goto L_0x01dc
            int r13 = r3.length     // Catch:{ Exception -> 0x04c7 }
            if (r13 != r7) goto L_0x01dc
            byte[] r13 = new byte[r7]     // Catch:{ Exception -> 0x04c7 }
            r12.ivChange = r13     // Catch:{ Exception -> 0x04c7 }
            java.lang.System.arraycopy(r3, r8, r13, r8, r7)     // Catch:{ Exception -> 0x04c7 }
            goto L_0x01de
        L_0x01dc:
            r3 = 1
            goto L_0x01df
        L_0x01de:
            r3 = 0
        L_0x01df:
            if (r3 != 0) goto L_0x02c2
            if (r0 == 0) goto L_0x02c2
            boolean r13 = r12.isBigFile     // Catch:{ Exception -> 0x04c7 }
            if (r13 == 0) goto L_0x01f1
            int r13 = r12.uploadStartTime     // Catch:{ Exception -> 0x04c7 }
            r14 = 86400(0x15180, float:1.21072E-40)
            int r13 = r13 - r14
            if (r0 >= r13) goto L_0x01f1
        L_0x01ef:
            r0 = 0
            goto L_0x0202
        L_0x01f1:
            boolean r13 = r12.isBigFile     // Catch:{ Exception -> 0x04c7 }
            if (r13 != 0) goto L_0x0202
            float r13 = (float) r0     // Catch:{ Exception -> 0x04c7 }
            int r14 = r12.uploadStartTime     // Catch:{ Exception -> 0x04c7 }
            float r14 = (float) r14     // Catch:{ Exception -> 0x04c7 }
            r15 = 1168687104(0x45a8CLASSNAME, float:5400.0)
            float r14 = r14 - r15
            int r13 = (r13 > r14 ? 1 : (r13 == r14 ? 0 : -1))
            if (r13 >= 0) goto L_0x0202
            goto L_0x01ef
        L_0x0202:
            if (r0 == 0) goto L_0x02bf
            int r0 = (r10 > r5 ? 1 : (r10 == r5 ? 0 : -1))
            if (r0 <= 0) goto L_0x02c2
            r12.readBytesCount = r10     // Catch:{ Exception -> 0x04c7 }
            int r0 = r12.uploadChunkSize     // Catch:{ Exception -> 0x04c7 }
            long r13 = (long) r0     // Catch:{ Exception -> 0x04c7 }
            long r13 = r10 / r13
            int r0 = (int) r13     // Catch:{ Exception -> 0x04c7 }
            r12.currentPartNum = r0     // Catch:{ Exception -> 0x04c7 }
            boolean r0 = r12.isBigFile     // Catch:{ Exception -> 0x04c7 }
            if (r0 != 0) goto L_0x0280
            r0 = 0
        L_0x0217:
            long r10 = (long) r0     // Catch:{ Exception -> 0x04c7 }
            long r13 = r12.readBytesCount     // Catch:{ Exception -> 0x04c7 }
            int r15 = r12.uploadChunkSize     // Catch:{ Exception -> 0x04c7 }
            r18 = r3
            long r2 = (long) r15     // Catch:{ Exception -> 0x04c7 }
            long r13 = r13 / r2
            int r2 = (r10 > r13 ? 1 : (r10 == r13 ? 0 : -1))
            if (r2 >= 0) goto L_0x02b2
            java.io.RandomAccessFile r2 = r12.stream     // Catch:{ Exception -> 0x04c7 }
            byte[] r3 = r12.readBuffer     // Catch:{ Exception -> 0x04c7 }
            int r2 = r2.read(r3)     // Catch:{ Exception -> 0x04c7 }
            boolean r3 = r12.isEncrypted     // Catch:{ Exception -> 0x04c7 }
            if (r3 == 0) goto L_0x023a
            int r3 = r2 % 16
            if (r3 == 0) goto L_0x023a
            int r3 = r2 % 16
            int r3 = 16 - r3
            int r3 = r3 + r8
            goto L_0x023b
        L_0x023a:
            r3 = 0
        L_0x023b:
            org.telegram.tgnet.NativeByteBuffer r10 = new org.telegram.tgnet.NativeByteBuffer     // Catch:{ Exception -> 0x04c7 }
            int r11 = r2 + r3
            r10.<init>((int) r11)     // Catch:{ Exception -> 0x04c7 }
            int r13 = r12.uploadChunkSize     // Catch:{ Exception -> 0x04c7 }
            if (r2 != r13) goto L_0x024d
            int r13 = r12.totalPartsCount     // Catch:{ Exception -> 0x04c7 }
            int r14 = r12.currentPartNum     // Catch:{ Exception -> 0x04c7 }
            int r14 = r14 + r1
            if (r13 != r14) goto L_0x024f
        L_0x024d:
            r12.isLastPart = r1     // Catch:{ Exception -> 0x04c7 }
        L_0x024f:
            byte[] r13 = r12.readBuffer     // Catch:{ Exception -> 0x04c7 }
            r10.writeBytes(r13, r8, r2)     // Catch:{ Exception -> 0x04c7 }
            boolean r2 = r12.isEncrypted     // Catch:{ Exception -> 0x04c7 }
            if (r2 == 0) goto L_0x0278
            r2 = 0
        L_0x0259:
            if (r2 >= r3) goto L_0x0261
            r10.writeByte((int) r8)     // Catch:{ Exception -> 0x04c7 }
            int r2 = r2 + 1
            goto L_0x0259
        L_0x0261:
            java.nio.ByteBuffer r2 = r10.buffer     // Catch:{ Exception -> 0x04c7 }
            byte[] r3 = r12.key     // Catch:{ Exception -> 0x04c7 }
            byte[] r13 = r12.ivChange     // Catch:{ Exception -> 0x04c7 }
            r22 = 1
            r23 = 1
            r24 = 0
            r19 = r2
            r20 = r3
            r21 = r13
            r25 = r11
            org.telegram.messenger.Utilities.aesIgeEncryption(r19, r20, r21, r22, r23, r24, r25)     // Catch:{ Exception -> 0x04c7 }
        L_0x0278:
            r10.reuse()     // Catch:{ Exception -> 0x04c7 }
            int r0 = r0 + 1
            r3 = r18
            goto L_0x0217
        L_0x0280:
            r18 = r3
            java.io.RandomAccessFile r0 = r12.stream     // Catch:{ Exception -> 0x04c7 }
            r0.seek(r10)     // Catch:{ Exception -> 0x04c7 }
            boolean r0 = r12.isEncrypted     // Catch:{ Exception -> 0x04c7 }
            if (r0 == 0) goto L_0x02b2
            android.content.SharedPreferences r0 = r12.preferences     // Catch:{ Exception -> 0x04c7 }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x04c7 }
            r2.<init>()     // Catch:{ Exception -> 0x04c7 }
            java.lang.String r3 = r12.fileKey     // Catch:{ Exception -> 0x04c7 }
            r2.append(r3)     // Catch:{ Exception -> 0x04c7 }
            java.lang.String r3 = "_ivc"
            r2.append(r3)     // Catch:{ Exception -> 0x04c7 }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x04c7 }
            java.lang.String r0 = r0.getString(r2, r4)     // Catch:{ Exception -> 0x04c7 }
            if (r0 == 0) goto L_0x02ba
            byte[] r0 = org.telegram.messenger.Utilities.hexToBytes(r0)     // Catch:{ Exception -> 0x04c7 }
            r12.ivChange = r0     // Catch:{ Exception -> 0x04c7 }
            if (r0 == 0) goto L_0x02b5
            int r0 = r0.length     // Catch:{ Exception -> 0x04c7 }
            if (r0 == r7) goto L_0x02b2
            goto L_0x02b5
        L_0x02b2:
            r3 = r18
            goto L_0x02c3
        L_0x02b5:
            r12.readBytesCount = r5     // Catch:{ Exception -> 0x04c7 }
            r12.currentPartNum = r8     // Catch:{ Exception -> 0x04c7 }
            goto L_0x02c2
        L_0x02ba:
            r12.readBytesCount = r5     // Catch:{ Exception -> 0x04c7 }
            r12.currentPartNum = r8     // Catch:{ Exception -> 0x04c7 }
            goto L_0x02c2
        L_0x02bf:
            r18 = r3
            goto L_0x02b2
        L_0x02c2:
            r3 = 1
        L_0x02c3:
            if (r3 == 0) goto L_0x02ff
            boolean r0 = r12.isEncrypted     // Catch:{ Exception -> 0x04c7 }
            if (r0 == 0) goto L_0x02e8
            byte[] r0 = new byte[r7]     // Catch:{ Exception -> 0x04c7 }
            r12.iv = r0     // Catch:{ Exception -> 0x04c7 }
            byte[] r2 = new byte[r7]     // Catch:{ Exception -> 0x04c7 }
            r12.key = r2     // Catch:{ Exception -> 0x04c7 }
            byte[] r2 = new byte[r7]     // Catch:{ Exception -> 0x04c7 }
            r12.ivChange = r2     // Catch:{ Exception -> 0x04c7 }
            java.security.SecureRandom r2 = org.telegram.messenger.Utilities.random     // Catch:{ Exception -> 0x04c7 }
            r2.nextBytes(r0)     // Catch:{ Exception -> 0x04c7 }
            java.security.SecureRandom r0 = org.telegram.messenger.Utilities.random     // Catch:{ Exception -> 0x04c7 }
            byte[] r2 = r12.key     // Catch:{ Exception -> 0x04c7 }
            r0.nextBytes(r2)     // Catch:{ Exception -> 0x04c7 }
            byte[] r0 = r12.iv     // Catch:{ Exception -> 0x04c7 }
            byte[] r2 = r12.ivChange     // Catch:{ Exception -> 0x04c7 }
            java.lang.System.arraycopy(r0, r8, r2, r8, r7)     // Catch:{ Exception -> 0x04c7 }
        L_0x02e8:
            java.security.SecureRandom r0 = org.telegram.messenger.Utilities.random     // Catch:{ Exception -> 0x04c7 }
            long r2 = r0.nextLong()     // Catch:{ Exception -> 0x04c7 }
            r12.currentFileId = r2     // Catch:{ Exception -> 0x04c7 }
            boolean r0 = r12.nextPartFirst     // Catch:{ Exception -> 0x04c7 }
            if (r0 != 0) goto L_0x02ff
            boolean r0 = r12.uploadFirstPartLater     // Catch:{ Exception -> 0x04c7 }
            if (r0 != 0) goto L_0x02ff
            int r0 = r12.estimatedSize     // Catch:{ Exception -> 0x04c7 }
            if (r0 != 0) goto L_0x02ff
            r27.storeFileUploadInfo()     // Catch:{ Exception -> 0x04c7 }
        L_0x02ff:
            boolean r0 = r12.isEncrypted     // Catch:{ Exception -> 0x04c7 }
            if (r0 == 0) goto L_0x0335
            java.lang.String r0 = "MD5"
            java.security.MessageDigest r0 = java.security.MessageDigest.getInstance(r0)     // Catch:{ Exception -> 0x0331 }
            byte[] r2 = new byte[r9]     // Catch:{ Exception -> 0x0331 }
            byte[] r3 = r12.key     // Catch:{ Exception -> 0x0331 }
            java.lang.System.arraycopy(r3, r8, r2, r8, r7)     // Catch:{ Exception -> 0x0331 }
            byte[] r3 = r12.iv     // Catch:{ Exception -> 0x0331 }
            java.lang.System.arraycopy(r3, r8, r2, r7, r7)     // Catch:{ Exception -> 0x0331 }
            byte[] r0 = r0.digest(r2)     // Catch:{ Exception -> 0x0331 }
            r2 = 0
        L_0x031a:
            r3 = 4
            if (r2 >= r3) goto L_0x0335
            int r3 = r12.fingerprint     // Catch:{ Exception -> 0x0331 }
            byte r9 = r0[r2]     // Catch:{ Exception -> 0x0331 }
            int r10 = r2 + 4
            byte r10 = r0[r10]     // Catch:{ Exception -> 0x0331 }
            r9 = r9 ^ r10
            r9 = r9 & 255(0xff, float:3.57E-43)
            int r10 = r2 * 8
            int r9 = r9 << r10
            r3 = r3 | r9
            r12.fingerprint = r3     // Catch:{ Exception -> 0x0331 }
            int r2 = r2 + 1
            goto L_0x031a
        L_0x0331:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ Exception -> 0x04c7 }
        L_0x0335:
            long r2 = r12.readBytesCount     // Catch:{ Exception -> 0x04c7 }
            r12.uploadedBytesCount = r2     // Catch:{ Exception -> 0x04c7 }
            int r0 = r12.currentPartNum     // Catch:{ Exception -> 0x04c7 }
            r12.lastSavedPartNum = r0     // Catch:{ Exception -> 0x04c7 }
            boolean r0 = r12.uploadFirstPartLater     // Catch:{ Exception -> 0x04c7 }
            if (r0 == 0) goto L_0x036b
            boolean r0 = r12.isBigFile     // Catch:{ Exception -> 0x04c7 }
            if (r0 == 0) goto L_0x0353
            java.io.RandomAccessFile r0 = r12.stream     // Catch:{ Exception -> 0x04c7 }
            int r2 = r12.uploadChunkSize     // Catch:{ Exception -> 0x04c7 }
            long r2 = (long) r2     // Catch:{ Exception -> 0x04c7 }
            r0.seek(r2)     // Catch:{ Exception -> 0x04c7 }
            int r0 = r12.uploadChunkSize     // Catch:{ Exception -> 0x04c7 }
            long r2 = (long) r0     // Catch:{ Exception -> 0x04c7 }
            r12.readBytesCount = r2     // Catch:{ Exception -> 0x04c7 }
            goto L_0x035c
        L_0x0353:
            java.io.RandomAccessFile r0 = r12.stream     // Catch:{ Exception -> 0x04c7 }
            r2 = 1024(0x400, double:5.06E-321)
            r0.seek(r2)     // Catch:{ Exception -> 0x04c7 }
            r12.readBytesCount = r2     // Catch:{ Exception -> 0x04c7 }
        L_0x035c:
            r12.currentPartNum = r1     // Catch:{ Exception -> 0x04c7 }
            goto L_0x036b
        L_0x035f:
            java.lang.Exception r0 = new java.lang.Exception     // Catch:{ Exception -> 0x04c7 }
            r0.<init>(r10)     // Catch:{ Exception -> 0x04c7 }
            throw r0     // Catch:{ Exception -> 0x04c7 }
        L_0x0365:
            java.lang.Exception r0 = new java.lang.Exception     // Catch:{ Exception -> 0x04c7 }
            r0.<init>(r10)     // Catch:{ Exception -> 0x04c7 }
            throw r0     // Catch:{ Exception -> 0x04c7 }
        L_0x036b:
            int r0 = r12.estimatedSize     // Catch:{ Exception -> 0x04c7 }
            if (r0 == 0) goto L_0x037c
            long r2 = r12.readBytesCount     // Catch:{ Exception -> 0x04c7 }
            int r0 = r12.uploadChunkSize     // Catch:{ Exception -> 0x04c7 }
            long r9 = (long) r0     // Catch:{ Exception -> 0x04c7 }
            long r2 = r2 + r9
            long r9 = r12.availableSize     // Catch:{ Exception -> 0x04c7 }
            int r0 = (r2 > r9 ? 1 : (r2 == r9 ? 0 : -1))
            if (r0 <= 0) goto L_0x037c
            return
        L_0x037c:
            boolean r0 = r12.nextPartFirst     // Catch:{ Exception -> 0x04c7 }
            if (r0 == 0) goto L_0x039f
            java.io.RandomAccessFile r0 = r12.stream     // Catch:{ Exception -> 0x04c7 }
            r0.seek(r5)     // Catch:{ Exception -> 0x04c7 }
            boolean r0 = r12.isBigFile     // Catch:{ Exception -> 0x04c7 }
            if (r0 == 0) goto L_0x0392
            java.io.RandomAccessFile r0 = r12.stream     // Catch:{ Exception -> 0x04c7 }
            byte[] r2 = r12.readBuffer     // Catch:{ Exception -> 0x04c7 }
            int r0 = r0.read(r2)     // Catch:{ Exception -> 0x04c7 }
            goto L_0x039c
        L_0x0392:
            java.io.RandomAccessFile r0 = r12.stream     // Catch:{ Exception -> 0x04c7 }
            byte[] r2 = r12.readBuffer     // Catch:{ Exception -> 0x04c7 }
            r3 = 1024(0x400, float:1.435E-42)
            int r0 = r0.read(r2, r8, r3)     // Catch:{ Exception -> 0x04c7 }
        L_0x039c:
            r12.currentPartNum = r8     // Catch:{ Exception -> 0x04c7 }
            goto L_0x03a7
        L_0x039f:
            java.io.RandomAccessFile r0 = r12.stream     // Catch:{ Exception -> 0x04c7 }
            byte[] r2 = r12.readBuffer     // Catch:{ Exception -> 0x04c7 }
            int r0 = r0.read(r2)     // Catch:{ Exception -> 0x04c7 }
        L_0x03a7:
            r2 = -1
            if (r0 != r2) goto L_0x03ab
            return
        L_0x03ab:
            boolean r3 = r12.isEncrypted     // Catch:{ Exception -> 0x04c7 }
            if (r3 == 0) goto L_0x03b9
            int r3 = r0 % 16
            if (r3 == 0) goto L_0x03b9
            int r3 = r0 % 16
            int r3 = 16 - r3
            int r3 = r3 + r8
            goto L_0x03ba
        L_0x03b9:
            r3 = 0
        L_0x03ba:
            org.telegram.tgnet.NativeByteBuffer r5 = new org.telegram.tgnet.NativeByteBuffer     // Catch:{ Exception -> 0x04c7 }
            int r6 = r0 + r3
            r5.<init>((int) r6)     // Catch:{ Exception -> 0x04c7 }
            boolean r9 = r12.nextPartFirst     // Catch:{ Exception -> 0x04c7 }
            if (r9 != 0) goto L_0x03d4
            int r9 = r12.uploadChunkSize     // Catch:{ Exception -> 0x04c7 }
            if (r0 != r9) goto L_0x03d4
            int r9 = r12.estimatedSize     // Catch:{ Exception -> 0x04c7 }
            if (r9 != 0) goto L_0x03df
            int r9 = r12.totalPartsCount     // Catch:{ Exception -> 0x04c7 }
            int r10 = r12.currentPartNum     // Catch:{ Exception -> 0x04c7 }
            int r10 = r10 + r1
            if (r9 != r10) goto L_0x03df
        L_0x03d4:
            boolean r9 = r12.uploadFirstPartLater     // Catch:{ Exception -> 0x04c7 }
            if (r9 == 0) goto L_0x03dd
            r12.nextPartFirst = r1     // Catch:{ Exception -> 0x04c7 }
            r12.uploadFirstPartLater = r8     // Catch:{ Exception -> 0x04c7 }
            goto L_0x03df
        L_0x03dd:
            r12.isLastPart = r1     // Catch:{ Exception -> 0x04c7 }
        L_0x03df:
            byte[] r9 = r12.readBuffer     // Catch:{ Exception -> 0x04c7 }
            r5.writeBytes(r9, r8, r0)     // Catch:{ Exception -> 0x04c7 }
            boolean r9 = r12.isEncrypted     // Catch:{ Exception -> 0x04c7 }
            if (r9 == 0) goto L_0x041c
            r4 = 0
        L_0x03e9:
            if (r4 >= r3) goto L_0x03f1
            r5.writeByte((int) r8)     // Catch:{ Exception -> 0x04c7 }
            int r4 = r4 + 1
            goto L_0x03e9
        L_0x03f1:
            java.nio.ByteBuffer r3 = r5.buffer     // Catch:{ Exception -> 0x04c7 }
            byte[] r4 = r12.key     // Catch:{ Exception -> 0x04c7 }
            byte[] r9 = r12.ivChange     // Catch:{ Exception -> 0x04c7 }
            r21 = 1
            r22 = 1
            r23 = 0
            r18 = r3
            r19 = r4
            r20 = r9
            r24 = r6
            org.telegram.messenger.Utilities.aesIgeEncryption(r18, r19, r20, r21, r22, r23, r24)     // Catch:{ Exception -> 0x04c7 }
            java.util.ArrayList<byte[]> r3 = r12.freeRequestIvs     // Catch:{ Exception -> 0x04c7 }
            java.lang.Object r3 = r3.get(r8)     // Catch:{ Exception -> 0x04c7 }
            byte[] r3 = (byte[]) r3     // Catch:{ Exception -> 0x04c7 }
            byte[] r4 = r12.ivChange     // Catch:{ Exception -> 0x04c7 }
            java.lang.System.arraycopy(r4, r8, r3, r8, r7)     // Catch:{ Exception -> 0x04c7 }
            java.util.ArrayList<byte[]> r4 = r12.freeRequestIvs     // Catch:{ Exception -> 0x04c7 }
            r4.remove(r8)     // Catch:{ Exception -> 0x04c7 }
            r6 = r3
            goto L_0x041d
        L_0x041c:
            r6 = r4
        L_0x041d:
            boolean r3 = r12.isBigFile     // Catch:{ Exception -> 0x04c7 }
            if (r3 == 0) goto L_0x043f
            org.telegram.tgnet.TLRPC$TL_upload_saveBigFilePart r3 = new org.telegram.tgnet.TLRPC$TL_upload_saveBigFilePart     // Catch:{ Exception -> 0x04c7 }
            r3.<init>()     // Catch:{ Exception -> 0x04c7 }
            int r4 = r12.currentPartNum     // Catch:{ Exception -> 0x04c7 }
            r3.file_part = r4     // Catch:{ Exception -> 0x04c7 }
            long r9 = r12.currentFileId     // Catch:{ Exception -> 0x04c7 }
            r3.file_id = r9     // Catch:{ Exception -> 0x04c7 }
            int r7 = r12.estimatedSize     // Catch:{ Exception -> 0x04c7 }
            if (r7 == 0) goto L_0x0435
            r3.file_total_parts = r2     // Catch:{ Exception -> 0x04c7 }
            goto L_0x0439
        L_0x0435:
            int r2 = r12.totalPartsCount     // Catch:{ Exception -> 0x04c7 }
            r3.file_total_parts = r2     // Catch:{ Exception -> 0x04c7 }
        L_0x0439:
            r3.bytes = r5     // Catch:{ Exception -> 0x04c7 }
            r19 = r3
            r9 = r4
            goto L_0x0451
        L_0x043f:
            org.telegram.tgnet.TLRPC$TL_upload_saveFilePart r3 = new org.telegram.tgnet.TLRPC$TL_upload_saveFilePart     // Catch:{ Exception -> 0x04c7 }
            r3.<init>()     // Catch:{ Exception -> 0x04c7 }
            int r2 = r12.currentPartNum     // Catch:{ Exception -> 0x04c7 }
            r3.file_part = r2     // Catch:{ Exception -> 0x04c7 }
            long r9 = r12.currentFileId     // Catch:{ Exception -> 0x04c7 }
            r3.file_id = r9     // Catch:{ Exception -> 0x04c7 }
            r3.bytes = r5     // Catch:{ Exception -> 0x04c7 }
            r9 = r2
            r19 = r3
        L_0x0451:
            boolean r2 = r12.isLastPart     // Catch:{ Exception -> 0x04c7 }
            if (r2 == 0) goto L_0x0467
            boolean r2 = r12.nextPartFirst     // Catch:{ Exception -> 0x04c7 }
            if (r2 == 0) goto L_0x0467
            r12.nextPartFirst = r8     // Catch:{ Exception -> 0x04c7 }
            int r2 = r12.totalPartsCount     // Catch:{ Exception -> 0x04c7 }
            int r2 = r2 - r1
            r12.currentPartNum = r2     // Catch:{ Exception -> 0x04c7 }
            java.io.RandomAccessFile r2 = r12.stream     // Catch:{ Exception -> 0x04c7 }
            long r3 = r12.totalFileSize     // Catch:{ Exception -> 0x04c7 }
            r2.seek(r3)     // Catch:{ Exception -> 0x04c7 }
        L_0x0467:
            long r2 = r12.readBytesCount     // Catch:{ Exception -> 0x04c7 }
            long r4 = (long) r0     // Catch:{ Exception -> 0x04c7 }
            long r2 = r2 + r4
            r12.readBytesCount = r2     // Catch:{ Exception -> 0x04c7 }
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
            r2 = 4
            int r4 = r1 + 4
            int r3 = r12.operationGuid
            boolean r1 = r12.slowNetwork
            if (r1 == 0) goto L_0x0490
            r25 = 4
            goto L_0x0497
        L_0x0490:
            int r1 = r13 % 4
            int r1 = r1 << 16
            r2 = r2 | r1
            r25 = r2
        L_0x0497:
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
        L_0x04c7:
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

    public /* synthetic */ void lambda$startUploadRequest$4$FileUploadOperation(int i, int i2, byte[] bArr, int i3, int i4, int i5, long j, TLObject tLObject, TLObject tLObject2, TLRPC$TL_error tLRPC$TL_error) {
        long j2;
        TLRPC$InputEncryptedFile tLRPC$InputEncryptedFile;
        TLRPC$InputFile tLRPC$InputFile;
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
            if (!(tLObject3 instanceof TLRPC$TL_boolTrue)) {
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
                } else if (this.currentUploadRequetsCount < this.maxRequestsCount) {
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
