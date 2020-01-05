package org.telegram.messenger;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.SparseArray;
import android.util.SparseIntArray;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.InputEncryptedFile;
import org.telegram.tgnet.TLRPC.InputFile;
import org.telegram.tgnet.TLRPC.TL_boolTrue;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_inputEncryptedFileBigUploaded;
import org.telegram.tgnet.TLRPC.TL_inputEncryptedFileUploaded;
import org.telegram.tgnet.TLRPC.TL_inputFile;
import org.telegram.tgnet.TLRPC.TL_inputFileBig;

public class FileUploadOperation {
    private static final int initialRequestsCount = 8;
    private static final int initialRequestsSlowNetworkCount = 1;
    private static final int maxUploadingKBytes = 2048;
    private static final int maxUploadingSlowNetworkKBytes = 32;
    private static final int minUploadChunkSize = 128;
    private static final int minUploadChunkSlowNetworkSize = 32;
    private long availableSize;
    private SparseArray<UploadCachedResult> cachedResults = new SparseArray();
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
        void didChangedUploadProgress(FileUploadOperation fileUploadOperation, float f);

        void didFailedUploadingFile(FileUploadOperation fileUploadOperation);

        void didFinishUploadingFile(FileUploadOperation fileUploadOperation, InputFile inputFile, InputEncryptedFile inputEncryptedFile, byte[] bArr, byte[] bArr2);
    }

    private class UploadCachedResult {
        private long bytesOffset;
        private byte[] iv;

        private UploadCachedResult() {
        }
    }

    public FileUploadOperation(int i, String str, boolean z, int i2, int i3) {
        this.currentAccount = i;
        this.uploadingFilePath = str;
        this.isEncrypted = z;
        this.estimatedSize = i2;
        this.currentType = i3;
        boolean z2 = (i2 == 0 || this.isEncrypted) ? false : true;
        this.uploadFirstPartLater = z2;
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
            Utilities.stageQueue.postRunnable(new -$$Lambda$FileUploadOperation$UJ53jdSVrDp9jmxTaqaNFaiq47E(this));
        }
    }

    public /* synthetic */ void lambda$start$0$FileUploadOperation() {
        int i = 0;
        this.preferences = ApplicationLoader.applicationContext.getSharedPreferences("uploadinfo", 0);
        this.slowNetwork = ApplicationLoader.isConnectionSlow();
        if (BuildVars.LOGS_ENABLED) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("start upload on slow network = ");
            stringBuilder.append(this.slowNetwork);
            FileLog.d(stringBuilder.toString());
        }
        int i2 = this.slowNetwork ? 1 : 8;
        while (i < i2) {
            startUploadRequest();
            i++;
        }
    }

    /* Access modifiers changed, original: protected */
    public void onNetworkChanged(boolean z) {
        if (this.state == 1) {
            Utilities.stageQueue.postRunnable(new -$$Lambda$FileUploadOperation$AsRwZThcGZTT5evyde1j2fU_PqE(this, z));
        }
    }

    public /* synthetic */ void lambda$onNetworkChanged$1$FileUploadOperation(boolean z) {
        if (this.slowNetwork != z) {
            boolean z2;
            this.slowNetwork = z;
            if (BuildVars.LOGS_ENABLED) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("network changed to slow = ");
                stringBuilder.append(this.slowNetwork);
                FileLog.d(stringBuilder.toString());
            }
            z = false;
            int i = 0;
            while (true) {
                z2 = true;
                if (i >= this.requestTokens.size()) {
                    break;
                }
                ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.requestTokens.valueAt(i), true);
                i++;
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
                z2 = true;
            }
            while (z < z2) {
                startUploadRequest();
                z++;
            }
        }
    }

    public void cancel() {
        if (this.state != 3) {
            this.state = 2;
            Utilities.stageQueue.postRunnable(new -$$Lambda$FileUploadOperation$1Av0AtRL3UNZuURFidi06RFneIU(this));
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
        Editor edit = this.preferences.edit();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.fileKey);
        stringBuilder.append("_time");
        edit = edit.remove(stringBuilder.toString());
        stringBuilder = new StringBuilder();
        stringBuilder.append(this.fileKey);
        stringBuilder.append("_size");
        edit = edit.remove(stringBuilder.toString());
        stringBuilder = new StringBuilder();
        stringBuilder.append(this.fileKey);
        stringBuilder.append("_uploaded");
        edit = edit.remove(stringBuilder.toString());
        stringBuilder = new StringBuilder();
        stringBuilder.append(this.fileKey);
        stringBuilder.append("_id");
        edit = edit.remove(stringBuilder.toString());
        stringBuilder = new StringBuilder();
        stringBuilder.append(this.fileKey);
        stringBuilder.append("_iv");
        edit = edit.remove(stringBuilder.toString());
        stringBuilder = new StringBuilder();
        stringBuilder.append(this.fileKey);
        stringBuilder.append("_key");
        edit = edit.remove(stringBuilder.toString());
        stringBuilder = new StringBuilder();
        stringBuilder.append(this.fileKey);
        stringBuilder.append("_ivc");
        edit.remove(stringBuilder.toString()).commit();
        try {
            if (this.stream != null) {
                this.stream.close();
                this.stream = null;
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    /* Access modifiers changed, original: protected */
    public void checkNewDataAvailable(long j, long j2) {
        Utilities.stageQueue.postRunnable(new -$$Lambda$FileUploadOperation$gwhBTZdvm3NdxGlA-jj4tE_lywA(this, j2, j));
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
        Editor edit = this.preferences.edit();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.fileKey);
        stringBuilder.append("_time");
        edit.putInt(stringBuilder.toString(), this.uploadStartTime);
        stringBuilder = new StringBuilder();
        stringBuilder.append(this.fileKey);
        stringBuilder.append("_size");
        edit.putLong(stringBuilder.toString(), this.totalFileSize);
        stringBuilder = new StringBuilder();
        stringBuilder.append(this.fileKey);
        stringBuilder.append("_id");
        edit.putLong(stringBuilder.toString(), this.currentFileId);
        stringBuilder = new StringBuilder();
        stringBuilder.append(this.fileKey);
        stringBuilder.append("_uploaded");
        edit.remove(stringBuilder.toString());
        if (this.isEncrypted) {
            stringBuilder = new StringBuilder();
            stringBuilder.append(this.fileKey);
            stringBuilder.append("_iv");
            edit.putString(stringBuilder.toString(), Utilities.bytesToHex(this.iv));
            stringBuilder = new StringBuilder();
            stringBuilder.append(this.fileKey);
            stringBuilder.append("_ivc");
            edit.putString(stringBuilder.toString(), Utilities.bytesToHex(this.ivChange));
            stringBuilder = new StringBuilder();
            stringBuilder.append(this.fileKey);
            stringBuilder.append("_key");
            edit.putString(stringBuilder.toString(), Utilities.bytesToHex(this.key));
        }
        edit.commit();
    }

    private void calcTotalPartsCount() {
        long j;
        int i;
        if (!this.uploadFirstPartLater) {
            j = this.totalFileSize;
            i = this.uploadChunkSize;
            this.totalPartsCount = ((int) ((j + ((long) i)) - 1)) / i;
        } else if (this.isBigFile) {
            j = this.totalFileSize;
            i = this.uploadChunkSize;
            this.totalPartsCount = (((int) (((j - ((long) i)) + ((long) i)) - 1)) / i) + 1;
        } else {
            j = this.totalFileSize - 1024;
            i = this.uploadChunkSize;
            this.totalPartsCount = (((int) ((j + ((long) i)) - 1)) / i) + 1;
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:112:0x029f A:{Catch:{ Exception -> 0x04a9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:77:0x01e4 A:{Catch:{ Exception -> 0x04a9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:115:0x02a6 A:{Catch:{ Exception -> 0x04a9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:127:0x02e6 A:{SYNTHETIC, Splitter:B:127:0x02e6} */
    /* JADX WARNING: Removed duplicated region for block: B:138:0x0324 A:{Catch:{ Exception -> 0x04a9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:77:0x01e4 A:{Catch:{ Exception -> 0x04a9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:112:0x029f A:{Catch:{ Exception -> 0x04a9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:115:0x02a6 A:{Catch:{ Exception -> 0x04a9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:127:0x02e6 A:{SYNTHETIC, Splitter:B:127:0x02e6} */
    /* JADX WARNING: Removed duplicated region for block: B:138:0x0324 A:{Catch:{ Exception -> 0x04a9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:115:0x02a6 A:{Catch:{ Exception -> 0x04a9 }} */
    /* JADX WARNING: Removed duplicated region for block: B:127:0x02e6 A:{SYNTHETIC, Splitter:B:127:0x02e6} */
    /* JADX WARNING: Removed duplicated region for block: B:138:0x0324 A:{Catch:{ Exception -> 0x04a9 }} */
    private void startUploadRequest() {
        /*
        r27 = this;
        r12 = r27;
        r0 = r12.state;
        r1 = 1;
        if (r0 == r1) goto L_0x0008;
    L_0x0007:
        return;
    L_0x0008:
        r12.started = r1;	 Catch:{ Exception -> 0x04a9 }
        r0 = r12.stream;	 Catch:{ Exception -> 0x04a9 }
        r3 = 1024; // 0x400 float:1.435E-42 double:5.06E-321;
        r4 = 0;
        r5 = 0;
        r7 = 32;
        r8 = 0;
        if (r0 != 0) goto L_0x034b;
    L_0x0016:
        r0 = new java.io.File;	 Catch:{ Exception -> 0x04a9 }
        r9 = r12.uploadingFilePath;	 Catch:{ Exception -> 0x04a9 }
        r0.<init>(r9);	 Catch:{ Exception -> 0x04a9 }
        r9 = android.net.Uri.fromFile(r0);	 Catch:{ Exception -> 0x04a9 }
        r9 = org.telegram.messenger.AndroidUtilities.isInternalUri(r9);	 Catch:{ Exception -> 0x04a9 }
        if (r9 != 0) goto L_0x0342;
    L_0x0027:
        r9 = new java.io.RandomAccessFile;	 Catch:{ Exception -> 0x04a9 }
        r10 = "r";
        r9.<init>(r0, r10);	 Catch:{ Exception -> 0x04a9 }
        r12.stream = r9;	 Catch:{ Exception -> 0x04a9 }
        r9 = r12.estimatedSize;	 Catch:{ Exception -> 0x04a9 }
        if (r9 == 0) goto L_0x003a;
    L_0x0034:
        r0 = r12.estimatedSize;	 Catch:{ Exception -> 0x04a9 }
        r9 = (long) r0;	 Catch:{ Exception -> 0x04a9 }
        r12.totalFileSize = r9;	 Catch:{ Exception -> 0x04a9 }
        goto L_0x0040;
    L_0x003a:
        r9 = r0.length();	 Catch:{ Exception -> 0x04a9 }
        r12.totalFileSize = r9;	 Catch:{ Exception -> 0x04a9 }
    L_0x0040:
        r9 = r12.totalFileSize;	 Catch:{ Exception -> 0x04a9 }
        r13 = 10485760; // 0xa00000 float:1.469368E-38 double:5.180654E-317;
        r0 = (r9 > r13 ? 1 : (r9 == r13 ? 0 : -1));
        if (r0 <= 0) goto L_0x004b;
    L_0x0049:
        r12.isBigFile = r1;	 Catch:{ Exception -> 0x04a9 }
    L_0x004b:
        r0 = r12.slowNetwork;	 Catch:{ Exception -> 0x04a9 }
        if (r0 == 0) goto L_0x0052;
    L_0x004f:
        r9 = 32;
        goto L_0x0054;
    L_0x0052:
        r9 = 128; // 0x80 float:1.794E-43 double:6.32E-322;
    L_0x0054:
        r13 = r12.totalFileSize;	 Catch:{ Exception -> 0x04a9 }
        r15 = 3072000; // 0x2ee000 float:4.304789E-39 double:1.5177697E-317;
        r13 = r13 + r15;
        r17 = 1;
        r13 = r13 - r17;
        r13 = r13 / r15;
        r9 = java.lang.Math.max(r9, r13);	 Catch:{ Exception -> 0x04a9 }
        r0 = (int) r9;	 Catch:{ Exception -> 0x04a9 }
        r12.uploadChunkSize = r0;	 Catch:{ Exception -> 0x04a9 }
        r0 = r12.uploadChunkSize;	 Catch:{ Exception -> 0x04a9 }
        r0 = r3 % r0;
        r9 = 64;
        if (r0 == 0) goto L_0x0079;
    L_0x006e:
        r0 = 64;
    L_0x0070:
        r10 = r12.uploadChunkSize;	 Catch:{ Exception -> 0x04a9 }
        if (r10 <= r0) goto L_0x0077;
    L_0x0074:
        r0 = r0 * 2;
        goto L_0x0070;
    L_0x0077:
        r12.uploadChunkSize = r0;	 Catch:{ Exception -> 0x04a9 }
    L_0x0079:
        r0 = r12.slowNetwork;	 Catch:{ Exception -> 0x04a9 }
        if (r0 == 0) goto L_0x0080;
    L_0x007d:
        r0 = 32;
        goto L_0x0082;
    L_0x0080:
        r0 = 2048; // 0x800 float:2.87E-42 double:1.0118E-320;
    L_0x0082:
        r10 = r12.uploadChunkSize;	 Catch:{ Exception -> 0x04a9 }
        r0 = r0 / r10;
        r0 = java.lang.Math.max(r1, r0);	 Catch:{ Exception -> 0x04a9 }
        r12.maxRequestsCount = r0;	 Catch:{ Exception -> 0x04a9 }
        r0 = r12.isEncrypted;	 Catch:{ Exception -> 0x04a9 }
        if (r0 == 0) goto L_0x00a7;
    L_0x008f:
        r0 = new java.util.ArrayList;	 Catch:{ Exception -> 0x04a9 }
        r10 = r12.maxRequestsCount;	 Catch:{ Exception -> 0x04a9 }
        r0.<init>(r10);	 Catch:{ Exception -> 0x04a9 }
        r12.freeRequestIvs = r0;	 Catch:{ Exception -> 0x04a9 }
        r0 = 0;
    L_0x0099:
        r10 = r12.maxRequestsCount;	 Catch:{ Exception -> 0x04a9 }
        if (r0 >= r10) goto L_0x00a7;
    L_0x009d:
        r10 = r12.freeRequestIvs;	 Catch:{ Exception -> 0x04a9 }
        r11 = new byte[r7];	 Catch:{ Exception -> 0x04a9 }
        r10.add(r11);	 Catch:{ Exception -> 0x04a9 }
        r0 = r0 + 1;
        goto L_0x0099;
    L_0x00a7:
        r0 = r12.uploadChunkSize;	 Catch:{ Exception -> 0x04a9 }
        r0 = r0 * 1024;
        r12.uploadChunkSize = r0;	 Catch:{ Exception -> 0x04a9 }
        r27.calcTotalPartsCount();	 Catch:{ Exception -> 0x04a9 }
        r0 = r12.uploadChunkSize;	 Catch:{ Exception -> 0x04a9 }
        r0 = new byte[r0];	 Catch:{ Exception -> 0x04a9 }
        r12.readBuffer = r0;	 Catch:{ Exception -> 0x04a9 }
        r0 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x04a9 }
        r0.<init>();	 Catch:{ Exception -> 0x04a9 }
        r10 = r12.uploadingFilePath;	 Catch:{ Exception -> 0x04a9 }
        r0.append(r10);	 Catch:{ Exception -> 0x04a9 }
        r10 = r12.isEncrypted;	 Catch:{ Exception -> 0x04a9 }
        if (r10 == 0) goto L_0x00c7;
    L_0x00c4:
        r10 = "enc";
        goto L_0x00c9;
    L_0x00c7:
        r10 = "";
    L_0x00c9:
        r0.append(r10);	 Catch:{ Exception -> 0x04a9 }
        r0 = r0.toString();	 Catch:{ Exception -> 0x04a9 }
        r0 = org.telegram.messenger.Utilities.MD5(r0);	 Catch:{ Exception -> 0x04a9 }
        r12.fileKey = r0;	 Catch:{ Exception -> 0x04a9 }
        r0 = r12.preferences;	 Catch:{ Exception -> 0x04a9 }
        r10 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x04a9 }
        r10.<init>();	 Catch:{ Exception -> 0x04a9 }
        r11 = r12.fileKey;	 Catch:{ Exception -> 0x04a9 }
        r10.append(r11);	 Catch:{ Exception -> 0x04a9 }
        r11 = "_size";
        r10.append(r11);	 Catch:{ Exception -> 0x04a9 }
        r10 = r10.toString();	 Catch:{ Exception -> 0x04a9 }
        r10 = r0.getLong(r10, r5);	 Catch:{ Exception -> 0x04a9 }
        r13 = java.lang.System.currentTimeMillis();	 Catch:{ Exception -> 0x04a9 }
        r15 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r13 = r13 / r15;
        r0 = (int) r13;	 Catch:{ Exception -> 0x04a9 }
        r12.uploadStartTime = r0;	 Catch:{ Exception -> 0x04a9 }
        r0 = r12.uploadFirstPartLater;	 Catch:{ Exception -> 0x04a9 }
        if (r0 != 0) goto L_0x02a2;
    L_0x00fd:
        r0 = r12.nextPartFirst;	 Catch:{ Exception -> 0x04a9 }
        if (r0 != 0) goto L_0x02a2;
    L_0x0101:
        r0 = r12.estimatedSize;	 Catch:{ Exception -> 0x04a9 }
        if (r0 != 0) goto L_0x02a2;
    L_0x0105:
        r13 = r12.totalFileSize;	 Catch:{ Exception -> 0x04a9 }
        r0 = (r10 > r13 ? 1 : (r10 == r13 ? 0 : -1));
        if (r0 != 0) goto L_0x02a2;
    L_0x010b:
        r0 = r12.preferences;	 Catch:{ Exception -> 0x04a9 }
        r10 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x04a9 }
        r10.<init>();	 Catch:{ Exception -> 0x04a9 }
        r11 = r12.fileKey;	 Catch:{ Exception -> 0x04a9 }
        r10.append(r11);	 Catch:{ Exception -> 0x04a9 }
        r11 = "_id";
        r10.append(r11);	 Catch:{ Exception -> 0x04a9 }
        r10 = r10.toString();	 Catch:{ Exception -> 0x04a9 }
        r10 = r0.getLong(r10, r5);	 Catch:{ Exception -> 0x04a9 }
        r12.currentFileId = r10;	 Catch:{ Exception -> 0x04a9 }
        r0 = r12.preferences;	 Catch:{ Exception -> 0x04a9 }
        r10 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x04a9 }
        r10.<init>();	 Catch:{ Exception -> 0x04a9 }
        r11 = r12.fileKey;	 Catch:{ Exception -> 0x04a9 }
        r10.append(r11);	 Catch:{ Exception -> 0x04a9 }
        r11 = "_time";
        r10.append(r11);	 Catch:{ Exception -> 0x04a9 }
        r10 = r10.toString();	 Catch:{ Exception -> 0x04a9 }
        r0 = r0.getInt(r10, r8);	 Catch:{ Exception -> 0x04a9 }
        r10 = r12.preferences;	 Catch:{ Exception -> 0x04a9 }
        r11 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x04a9 }
        r11.<init>();	 Catch:{ Exception -> 0x04a9 }
        r13 = r12.fileKey;	 Catch:{ Exception -> 0x04a9 }
        r11.append(r13);	 Catch:{ Exception -> 0x04a9 }
        r13 = "_uploaded";
        r11.append(r13);	 Catch:{ Exception -> 0x04a9 }
        r11 = r11.toString();	 Catch:{ Exception -> 0x04a9 }
        r10 = r10.getLong(r11, r5);	 Catch:{ Exception -> 0x04a9 }
        r13 = r12.isEncrypted;	 Catch:{ Exception -> 0x04a9 }
        if (r13 == 0) goto L_0x01be;
    L_0x015c:
        r13 = r12.preferences;	 Catch:{ Exception -> 0x04a9 }
        r14 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x04a9 }
        r14.<init>();	 Catch:{ Exception -> 0x04a9 }
        r15 = r12.fileKey;	 Catch:{ Exception -> 0x04a9 }
        r14.append(r15);	 Catch:{ Exception -> 0x04a9 }
        r15 = "_iv";
        r14.append(r15);	 Catch:{ Exception -> 0x04a9 }
        r14 = r14.toString();	 Catch:{ Exception -> 0x04a9 }
        r13 = r13.getString(r14, r4);	 Catch:{ Exception -> 0x04a9 }
        r14 = r12.preferences;	 Catch:{ Exception -> 0x04a9 }
        r15 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x04a9 }
        r15.<init>();	 Catch:{ Exception -> 0x04a9 }
        r3 = r12.fileKey;	 Catch:{ Exception -> 0x04a9 }
        r15.append(r3);	 Catch:{ Exception -> 0x04a9 }
        r3 = "_key";
        r15.append(r3);	 Catch:{ Exception -> 0x04a9 }
        r3 = r15.toString();	 Catch:{ Exception -> 0x04a9 }
        r3 = r14.getString(r3, r4);	 Catch:{ Exception -> 0x04a9 }
        if (r13 == 0) goto L_0x01bc;
    L_0x0190:
        if (r3 == 0) goto L_0x01bc;
    L_0x0192:
        r3 = org.telegram.messenger.Utilities.hexToBytes(r3);	 Catch:{ Exception -> 0x04a9 }
        r12.key = r3;	 Catch:{ Exception -> 0x04a9 }
        r3 = org.telegram.messenger.Utilities.hexToBytes(r13);	 Catch:{ Exception -> 0x04a9 }
        r12.iv = r3;	 Catch:{ Exception -> 0x04a9 }
        r3 = r12.key;	 Catch:{ Exception -> 0x04a9 }
        if (r3 == 0) goto L_0x01bc;
    L_0x01a2:
        r3 = r12.iv;	 Catch:{ Exception -> 0x04a9 }
        if (r3 == 0) goto L_0x01bc;
    L_0x01a6:
        r3 = r12.key;	 Catch:{ Exception -> 0x04a9 }
        r3 = r3.length;	 Catch:{ Exception -> 0x04a9 }
        if (r3 != r7) goto L_0x01bc;
    L_0x01ab:
        r3 = r12.iv;	 Catch:{ Exception -> 0x04a9 }
        r3 = r3.length;	 Catch:{ Exception -> 0x04a9 }
        if (r3 != r7) goto L_0x01bc;
    L_0x01b0:
        r3 = new byte[r7];	 Catch:{ Exception -> 0x04a9 }
        r12.ivChange = r3;	 Catch:{ Exception -> 0x04a9 }
        r3 = r12.iv;	 Catch:{ Exception -> 0x04a9 }
        r13 = r12.ivChange;	 Catch:{ Exception -> 0x04a9 }
        java.lang.System.arraycopy(r3, r8, r13, r8, r7);	 Catch:{ Exception -> 0x04a9 }
        goto L_0x01be;
    L_0x01bc:
        r3 = 1;
        goto L_0x01bf;
    L_0x01be:
        r3 = 0;
    L_0x01bf:
        if (r3 != 0) goto L_0x02a2;
    L_0x01c1:
        if (r0 == 0) goto L_0x02a2;
    L_0x01c3:
        r13 = r12.isBigFile;	 Catch:{ Exception -> 0x04a9 }
        if (r13 == 0) goto L_0x01d1;
    L_0x01c7:
        r13 = r12.uploadStartTime;	 Catch:{ Exception -> 0x04a9 }
        r14 = 86400; // 0x15180 float:1.21072E-40 double:4.26873E-319;
        r13 = r13 - r14;
        if (r0 >= r13) goto L_0x01d1;
    L_0x01cf:
        r0 = 0;
        goto L_0x01e2;
    L_0x01d1:
        r13 = r12.isBigFile;	 Catch:{ Exception -> 0x04a9 }
        if (r13 != 0) goto L_0x01e2;
    L_0x01d5:
        r13 = (float) r0;	 Catch:{ Exception -> 0x04a9 }
        r14 = r12.uploadStartTime;	 Catch:{ Exception -> 0x04a9 }
        r14 = (float) r14;	 Catch:{ Exception -> 0x04a9 }
        r15 = NUM; // 0x45a8CLASSNAME float:5400.0 double:5.77408149E-315;
        r14 = r14 - r15;
        r13 = (r13 > r14 ? 1 : (r13 == r14 ? 0 : -1));
        if (r13 >= 0) goto L_0x01e2;
    L_0x01e1:
        goto L_0x01cf;
    L_0x01e2:
        if (r0 == 0) goto L_0x029f;
    L_0x01e4:
        r0 = (r10 > r5 ? 1 : (r10 == r5 ? 0 : -1));
        if (r0 <= 0) goto L_0x02a2;
    L_0x01e8:
        r12.readBytesCount = r10;	 Catch:{ Exception -> 0x04a9 }
        r0 = r12.uploadChunkSize;	 Catch:{ Exception -> 0x04a9 }
        r13 = (long) r0;	 Catch:{ Exception -> 0x04a9 }
        r13 = r10 / r13;
        r0 = (int) r13;	 Catch:{ Exception -> 0x04a9 }
        r12.currentPartNum = r0;	 Catch:{ Exception -> 0x04a9 }
        r0 = r12.isBigFile;	 Catch:{ Exception -> 0x04a9 }
        if (r0 != 0) goto L_0x0260;
    L_0x01f6:
        r0 = 0;
    L_0x01f7:
        r10 = (long) r0;	 Catch:{ Exception -> 0x04a9 }
        r13 = r12.readBytesCount;	 Catch:{ Exception -> 0x04a9 }
        r15 = r12.uploadChunkSize;	 Catch:{ Exception -> 0x04a9 }
        r18 = r3;
        r2 = (long) r15;	 Catch:{ Exception -> 0x04a9 }
        r13 = r13 / r2;
        r2 = (r10 > r13 ? 1 : (r10 == r13 ? 0 : -1));
        if (r2 >= 0) goto L_0x02a4;
    L_0x0204:
        r2 = r12.stream;	 Catch:{ Exception -> 0x04a9 }
        r3 = r12.readBuffer;	 Catch:{ Exception -> 0x04a9 }
        r2 = r2.read(r3);	 Catch:{ Exception -> 0x04a9 }
        r3 = r12.isEncrypted;	 Catch:{ Exception -> 0x04a9 }
        if (r3 == 0) goto L_0x021a;
    L_0x0210:
        r3 = r2 % 16;
        if (r3 == 0) goto L_0x021a;
    L_0x0214:
        r3 = r2 % 16;
        r3 = 16 - r3;
        r3 = r3 + r8;
        goto L_0x021b;
    L_0x021a:
        r3 = 0;
    L_0x021b:
        r10 = new org.telegram.tgnet.NativeByteBuffer;	 Catch:{ Exception -> 0x04a9 }
        r11 = r2 + r3;
        r10.<init>(r11);	 Catch:{ Exception -> 0x04a9 }
        r13 = r12.uploadChunkSize;	 Catch:{ Exception -> 0x04a9 }
        if (r2 != r13) goto L_0x022d;
    L_0x0226:
        r13 = r12.totalPartsCount;	 Catch:{ Exception -> 0x04a9 }
        r14 = r12.currentPartNum;	 Catch:{ Exception -> 0x04a9 }
        r14 = r14 + r1;
        if (r13 != r14) goto L_0x022f;
    L_0x022d:
        r12.isLastPart = r1;	 Catch:{ Exception -> 0x04a9 }
    L_0x022f:
        r13 = r12.readBuffer;	 Catch:{ Exception -> 0x04a9 }
        r10.writeBytes(r13, r8, r2);	 Catch:{ Exception -> 0x04a9 }
        r2 = r12.isEncrypted;	 Catch:{ Exception -> 0x04a9 }
        if (r2 == 0) goto L_0x0258;
    L_0x0238:
        r2 = 0;
    L_0x0239:
        if (r2 >= r3) goto L_0x0241;
    L_0x023b:
        r10.writeByte(r8);	 Catch:{ Exception -> 0x04a9 }
        r2 = r2 + 1;
        goto L_0x0239;
    L_0x0241:
        r2 = r10.buffer;	 Catch:{ Exception -> 0x04a9 }
        r3 = r12.key;	 Catch:{ Exception -> 0x04a9 }
        r13 = r12.ivChange;	 Catch:{ Exception -> 0x04a9 }
        r22 = 1;
        r23 = 1;
        r24 = 0;
        r19 = r2;
        r20 = r3;
        r21 = r13;
        r25 = r11;
        org.telegram.messenger.Utilities.aesIgeEncryption(r19, r20, r21, r22, r23, r24, r25);	 Catch:{ Exception -> 0x04a9 }
    L_0x0258:
        r10.reuse();	 Catch:{ Exception -> 0x04a9 }
        r0 = r0 + 1;
        r3 = r18;
        goto L_0x01f7;
    L_0x0260:
        r18 = r3;
        r0 = r12.stream;	 Catch:{ Exception -> 0x04a9 }
        r0.seek(r10);	 Catch:{ Exception -> 0x04a9 }
        r0 = r12.isEncrypted;	 Catch:{ Exception -> 0x04a9 }
        if (r0 == 0) goto L_0x02a4;
    L_0x026b:
        r0 = r12.preferences;	 Catch:{ Exception -> 0x04a9 }
        r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x04a9 }
        r2.<init>();	 Catch:{ Exception -> 0x04a9 }
        r3 = r12.fileKey;	 Catch:{ Exception -> 0x04a9 }
        r2.append(r3);	 Catch:{ Exception -> 0x04a9 }
        r3 = "_ivc";
        r2.append(r3);	 Catch:{ Exception -> 0x04a9 }
        r2 = r2.toString();	 Catch:{ Exception -> 0x04a9 }
        r0 = r0.getString(r2, r4);	 Catch:{ Exception -> 0x04a9 }
        if (r0 == 0) goto L_0x029a;
    L_0x0286:
        r0 = org.telegram.messenger.Utilities.hexToBytes(r0);	 Catch:{ Exception -> 0x04a9 }
        r12.ivChange = r0;	 Catch:{ Exception -> 0x04a9 }
        r0 = r12.ivChange;	 Catch:{ Exception -> 0x04a9 }
        if (r0 == 0) goto L_0x0295;
    L_0x0290:
        r0 = r12.ivChange;	 Catch:{ Exception -> 0x04a9 }
        r0 = r0.length;	 Catch:{ Exception -> 0x04a9 }
        if (r0 == r7) goto L_0x02a4;
    L_0x0295:
        r12.readBytesCount = r5;	 Catch:{ Exception -> 0x04a9 }
        r12.currentPartNum = r8;	 Catch:{ Exception -> 0x04a9 }
        goto L_0x02a2;
    L_0x029a:
        r12.readBytesCount = r5;	 Catch:{ Exception -> 0x04a9 }
        r12.currentPartNum = r8;	 Catch:{ Exception -> 0x04a9 }
        goto L_0x02a2;
    L_0x029f:
        r18 = r3;
        goto L_0x02a4;
    L_0x02a2:
        r18 = 1;
    L_0x02a4:
        if (r18 == 0) goto L_0x02e2;
    L_0x02a6:
        r0 = r12.isEncrypted;	 Catch:{ Exception -> 0x04a9 }
        if (r0 == 0) goto L_0x02cb;
    L_0x02aa:
        r0 = new byte[r7];	 Catch:{ Exception -> 0x04a9 }
        r12.iv = r0;	 Catch:{ Exception -> 0x04a9 }
        r0 = new byte[r7];	 Catch:{ Exception -> 0x04a9 }
        r12.key = r0;	 Catch:{ Exception -> 0x04a9 }
        r0 = new byte[r7];	 Catch:{ Exception -> 0x04a9 }
        r12.ivChange = r0;	 Catch:{ Exception -> 0x04a9 }
        r0 = org.telegram.messenger.Utilities.random;	 Catch:{ Exception -> 0x04a9 }
        r2 = r12.iv;	 Catch:{ Exception -> 0x04a9 }
        r0.nextBytes(r2);	 Catch:{ Exception -> 0x04a9 }
        r0 = org.telegram.messenger.Utilities.random;	 Catch:{ Exception -> 0x04a9 }
        r2 = r12.key;	 Catch:{ Exception -> 0x04a9 }
        r0.nextBytes(r2);	 Catch:{ Exception -> 0x04a9 }
        r0 = r12.iv;	 Catch:{ Exception -> 0x04a9 }
        r2 = r12.ivChange;	 Catch:{ Exception -> 0x04a9 }
        java.lang.System.arraycopy(r0, r8, r2, r8, r7);	 Catch:{ Exception -> 0x04a9 }
    L_0x02cb:
        r0 = org.telegram.messenger.Utilities.random;	 Catch:{ Exception -> 0x04a9 }
        r2 = r0.nextLong();	 Catch:{ Exception -> 0x04a9 }
        r12.currentFileId = r2;	 Catch:{ Exception -> 0x04a9 }
        r0 = r12.nextPartFirst;	 Catch:{ Exception -> 0x04a9 }
        if (r0 != 0) goto L_0x02e2;
    L_0x02d7:
        r0 = r12.uploadFirstPartLater;	 Catch:{ Exception -> 0x04a9 }
        if (r0 != 0) goto L_0x02e2;
    L_0x02db:
        r0 = r12.estimatedSize;	 Catch:{ Exception -> 0x04a9 }
        if (r0 != 0) goto L_0x02e2;
    L_0x02df:
        r27.storeFileUploadInfo();	 Catch:{ Exception -> 0x04a9 }
    L_0x02e2:
        r0 = r12.isEncrypted;	 Catch:{ Exception -> 0x04a9 }
        if (r0 == 0) goto L_0x0318;
    L_0x02e6:
        r0 = "MD5";
        r0 = java.security.MessageDigest.getInstance(r0);	 Catch:{ Exception -> 0x0314 }
        r2 = new byte[r9];	 Catch:{ Exception -> 0x0314 }
        r3 = r12.key;	 Catch:{ Exception -> 0x0314 }
        java.lang.System.arraycopy(r3, r8, r2, r8, r7);	 Catch:{ Exception -> 0x0314 }
        r3 = r12.iv;	 Catch:{ Exception -> 0x0314 }
        java.lang.System.arraycopy(r3, r8, r2, r7, r7);	 Catch:{ Exception -> 0x0314 }
        r0 = r0.digest(r2);	 Catch:{ Exception -> 0x0314 }
        r2 = 0;
    L_0x02fd:
        r3 = 4;
        if (r2 >= r3) goto L_0x0318;
    L_0x0300:
        r3 = r12.fingerprint;	 Catch:{ Exception -> 0x0314 }
        r9 = r0[r2];	 Catch:{ Exception -> 0x0314 }
        r10 = r2 + 4;
        r10 = r0[r10];	 Catch:{ Exception -> 0x0314 }
        r9 = r9 ^ r10;
        r9 = r9 & 255;
        r10 = r2 * 8;
        r9 = r9 << r10;
        r3 = r3 | r9;
        r12.fingerprint = r3;	 Catch:{ Exception -> 0x0314 }
        r2 = r2 + 1;
        goto L_0x02fd;
    L_0x0314:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);	 Catch:{ Exception -> 0x04a9 }
    L_0x0318:
        r2 = r12.readBytesCount;	 Catch:{ Exception -> 0x04a9 }
        r12.uploadedBytesCount = r2;	 Catch:{ Exception -> 0x04a9 }
        r0 = r12.currentPartNum;	 Catch:{ Exception -> 0x04a9 }
        r12.lastSavedPartNum = r0;	 Catch:{ Exception -> 0x04a9 }
        r0 = r12.uploadFirstPartLater;	 Catch:{ Exception -> 0x04a9 }
        if (r0 == 0) goto L_0x034b;
    L_0x0324:
        r0 = r12.isBigFile;	 Catch:{ Exception -> 0x04a9 }
        if (r0 == 0) goto L_0x0336;
    L_0x0328:
        r0 = r12.stream;	 Catch:{ Exception -> 0x04a9 }
        r2 = r12.uploadChunkSize;	 Catch:{ Exception -> 0x04a9 }
        r2 = (long) r2;	 Catch:{ Exception -> 0x04a9 }
        r0.seek(r2);	 Catch:{ Exception -> 0x04a9 }
        r0 = r12.uploadChunkSize;	 Catch:{ Exception -> 0x04a9 }
        r2 = (long) r0;	 Catch:{ Exception -> 0x04a9 }
        r12.readBytesCount = r2;	 Catch:{ Exception -> 0x04a9 }
        goto L_0x033f;
    L_0x0336:
        r0 = r12.stream;	 Catch:{ Exception -> 0x04a9 }
        r2 = 1024; // 0x400 float:1.435E-42 double:5.06E-321;
        r0.seek(r2);	 Catch:{ Exception -> 0x04a9 }
        r12.readBytesCount = r2;	 Catch:{ Exception -> 0x04a9 }
    L_0x033f:
        r12.currentPartNum = r1;	 Catch:{ Exception -> 0x04a9 }
        goto L_0x034b;
    L_0x0342:
        r0 = new java.lang.Exception;	 Catch:{ Exception -> 0x04a9 }
        r1 = "trying to upload internal file";
        r0.<init>(r1);	 Catch:{ Exception -> 0x04a9 }
        throw r0;	 Catch:{ Exception -> 0x04a9 }
    L_0x034b:
        r0 = r12.estimatedSize;	 Catch:{ Exception -> 0x04a9 }
        if (r0 == 0) goto L_0x035c;
    L_0x034f:
        r2 = r12.readBytesCount;	 Catch:{ Exception -> 0x04a9 }
        r0 = r12.uploadChunkSize;	 Catch:{ Exception -> 0x04a9 }
        r9 = (long) r0;	 Catch:{ Exception -> 0x04a9 }
        r2 = r2 + r9;
        r9 = r12.availableSize;	 Catch:{ Exception -> 0x04a9 }
        r0 = (r2 > r9 ? 1 : (r2 == r9 ? 0 : -1));
        if (r0 <= 0) goto L_0x035c;
    L_0x035b:
        return;
    L_0x035c:
        r0 = r12.nextPartFirst;	 Catch:{ Exception -> 0x04a9 }
        if (r0 == 0) goto L_0x037f;
    L_0x0360:
        r0 = r12.stream;	 Catch:{ Exception -> 0x04a9 }
        r0.seek(r5);	 Catch:{ Exception -> 0x04a9 }
        r0 = r12.isBigFile;	 Catch:{ Exception -> 0x04a9 }
        if (r0 == 0) goto L_0x0372;
    L_0x0369:
        r0 = r12.stream;	 Catch:{ Exception -> 0x04a9 }
        r2 = r12.readBuffer;	 Catch:{ Exception -> 0x04a9 }
        r0 = r0.read(r2);	 Catch:{ Exception -> 0x04a9 }
        goto L_0x037c;
    L_0x0372:
        r0 = r12.stream;	 Catch:{ Exception -> 0x04a9 }
        r2 = r12.readBuffer;	 Catch:{ Exception -> 0x04a9 }
        r3 = 1024; // 0x400 float:1.435E-42 double:5.06E-321;
        r0 = r0.read(r2, r8, r3);	 Catch:{ Exception -> 0x04a9 }
    L_0x037c:
        r12.currentPartNum = r8;	 Catch:{ Exception -> 0x04a9 }
        goto L_0x0387;
    L_0x037f:
        r0 = r12.stream;	 Catch:{ Exception -> 0x04a9 }
        r2 = r12.readBuffer;	 Catch:{ Exception -> 0x04a9 }
        r0 = r0.read(r2);	 Catch:{ Exception -> 0x04a9 }
    L_0x0387:
        r2 = -1;
        if (r0 != r2) goto L_0x038b;
    L_0x038a:
        return;
    L_0x038b:
        r3 = r12.isEncrypted;	 Catch:{ Exception -> 0x04a9 }
        if (r3 == 0) goto L_0x0399;
    L_0x038f:
        r3 = r0 % 16;
        if (r3 == 0) goto L_0x0399;
    L_0x0393:
        r3 = r0 % 16;
        r3 = 16 - r3;
        r3 = r3 + r8;
        goto L_0x039a;
    L_0x0399:
        r3 = 0;
    L_0x039a:
        r5 = new org.telegram.tgnet.NativeByteBuffer;	 Catch:{ Exception -> 0x04a9 }
        r6 = r0 + r3;
        r5.<init>(r6);	 Catch:{ Exception -> 0x04a9 }
        r9 = r12.nextPartFirst;	 Catch:{ Exception -> 0x04a9 }
        if (r9 != 0) goto L_0x03b4;
    L_0x03a5:
        r9 = r12.uploadChunkSize;	 Catch:{ Exception -> 0x04a9 }
        if (r0 != r9) goto L_0x03b4;
    L_0x03a9:
        r9 = r12.estimatedSize;	 Catch:{ Exception -> 0x04a9 }
        if (r9 != 0) goto L_0x03bf;
    L_0x03ad:
        r9 = r12.totalPartsCount;	 Catch:{ Exception -> 0x04a9 }
        r10 = r12.currentPartNum;	 Catch:{ Exception -> 0x04a9 }
        r10 = r10 + r1;
        if (r9 != r10) goto L_0x03bf;
    L_0x03b4:
        r9 = r12.uploadFirstPartLater;	 Catch:{ Exception -> 0x04a9 }
        if (r9 == 0) goto L_0x03bd;
    L_0x03b8:
        r12.nextPartFirst = r1;	 Catch:{ Exception -> 0x04a9 }
        r12.uploadFirstPartLater = r8;	 Catch:{ Exception -> 0x04a9 }
        goto L_0x03bf;
    L_0x03bd:
        r12.isLastPart = r1;	 Catch:{ Exception -> 0x04a9 }
    L_0x03bf:
        r9 = r12.readBuffer;	 Catch:{ Exception -> 0x04a9 }
        r5.writeBytes(r9, r8, r0);	 Catch:{ Exception -> 0x04a9 }
        r9 = r12.isEncrypted;	 Catch:{ Exception -> 0x04a9 }
        if (r9 == 0) goto L_0x03fc;
    L_0x03c8:
        r4 = 0;
    L_0x03c9:
        if (r4 >= r3) goto L_0x03d1;
    L_0x03cb:
        r5.writeByte(r8);	 Catch:{ Exception -> 0x04a9 }
        r4 = r4 + 1;
        goto L_0x03c9;
    L_0x03d1:
        r3 = r5.buffer;	 Catch:{ Exception -> 0x04a9 }
        r4 = r12.key;	 Catch:{ Exception -> 0x04a9 }
        r9 = r12.ivChange;	 Catch:{ Exception -> 0x04a9 }
        r21 = 1;
        r22 = 1;
        r23 = 0;
        r18 = r3;
        r19 = r4;
        r20 = r9;
        r24 = r6;
        org.telegram.messenger.Utilities.aesIgeEncryption(r18, r19, r20, r21, r22, r23, r24);	 Catch:{ Exception -> 0x04a9 }
        r3 = r12.freeRequestIvs;	 Catch:{ Exception -> 0x04a9 }
        r3 = r3.get(r8);	 Catch:{ Exception -> 0x04a9 }
        r3 = (byte[]) r3;	 Catch:{ Exception -> 0x04a9 }
        r4 = r12.ivChange;	 Catch:{ Exception -> 0x04a9 }
        java.lang.System.arraycopy(r4, r8, r3, r8, r7);	 Catch:{ Exception -> 0x04a9 }
        r4 = r12.freeRequestIvs;	 Catch:{ Exception -> 0x04a9 }
        r4.remove(r8);	 Catch:{ Exception -> 0x04a9 }
        r6 = r3;
        goto L_0x03fd;
    L_0x03fc:
        r6 = r4;
    L_0x03fd:
        r3 = r12.isBigFile;	 Catch:{ Exception -> 0x04a9 }
        if (r3 == 0) goto L_0x041f;
    L_0x0401:
        r3 = new org.telegram.tgnet.TLRPC$TL_upload_saveBigFilePart;	 Catch:{ Exception -> 0x04a9 }
        r3.<init>();	 Catch:{ Exception -> 0x04a9 }
        r4 = r12.currentPartNum;	 Catch:{ Exception -> 0x04a9 }
        r3.file_part = r4;	 Catch:{ Exception -> 0x04a9 }
        r9 = r12.currentFileId;	 Catch:{ Exception -> 0x04a9 }
        r3.file_id = r9;	 Catch:{ Exception -> 0x04a9 }
        r7 = r12.estimatedSize;	 Catch:{ Exception -> 0x04a9 }
        if (r7 == 0) goto L_0x0415;
    L_0x0412:
        r3.file_total_parts = r2;	 Catch:{ Exception -> 0x04a9 }
        goto L_0x0419;
    L_0x0415:
        r2 = r12.totalPartsCount;	 Catch:{ Exception -> 0x04a9 }
        r3.file_total_parts = r2;	 Catch:{ Exception -> 0x04a9 }
    L_0x0419:
        r3.bytes = r5;	 Catch:{ Exception -> 0x04a9 }
        r19 = r3;
        r9 = r4;
        goto L_0x0431;
    L_0x041f:
        r2 = new org.telegram.tgnet.TLRPC$TL_upload_saveFilePart;	 Catch:{ Exception -> 0x04a9 }
        r2.<init>();	 Catch:{ Exception -> 0x04a9 }
        r3 = r12.currentPartNum;	 Catch:{ Exception -> 0x04a9 }
        r2.file_part = r3;	 Catch:{ Exception -> 0x04a9 }
        r9 = r12.currentFileId;	 Catch:{ Exception -> 0x04a9 }
        r2.file_id = r9;	 Catch:{ Exception -> 0x04a9 }
        r2.bytes = r5;	 Catch:{ Exception -> 0x04a9 }
        r19 = r2;
        r9 = r3;
    L_0x0431:
        r2 = r12.isLastPart;	 Catch:{ Exception -> 0x04a9 }
        if (r2 == 0) goto L_0x0447;
    L_0x0435:
        r2 = r12.nextPartFirst;	 Catch:{ Exception -> 0x04a9 }
        if (r2 == 0) goto L_0x0447;
    L_0x0439:
        r12.nextPartFirst = r8;	 Catch:{ Exception -> 0x04a9 }
        r2 = r12.totalPartsCount;	 Catch:{ Exception -> 0x04a9 }
        r2 = r2 - r1;
        r12.currentPartNum = r2;	 Catch:{ Exception -> 0x04a9 }
        r2 = r12.stream;	 Catch:{ Exception -> 0x04a9 }
        r3 = r12.totalFileSize;	 Catch:{ Exception -> 0x04a9 }
        r2.seek(r3);	 Catch:{ Exception -> 0x04a9 }
    L_0x0447:
        r2 = r12.readBytesCount;	 Catch:{ Exception -> 0x04a9 }
        r4 = (long) r0;	 Catch:{ Exception -> 0x04a9 }
        r2 = r2 + r4;
        r12.readBytesCount = r2;	 Catch:{ Exception -> 0x04a9 }
        r2 = r12.currentPartNum;
        r2 = r2 + r1;
        r12.currentPartNum = r2;
        r2 = r12.currentUploadRequetsCount;
        r2 = r2 + r1;
        r12.currentUploadRequetsCount = r2;
        r13 = r12.requestNum;
        r1 = r13 + 1;
        r12.requestNum = r1;
        r1 = r9 + r0;
        r10 = (long) r1;
        r1 = r19.getObjectSize();
        r17 = 4;
        r4 = r1 + 4;
        r3 = r12.operationGuid;
        r1 = r12.slowNetwork;
        if (r1 == 0) goto L_0x0471;
    L_0x046e:
        r25 = 4;
        goto L_0x0479;
    L_0x0471:
        r1 = r13 % 4;
        r1 = r1 << 16;
        r2 = r1 | 4;
        r25 = r2;
    L_0x0479:
        r1 = r12.currentAccount;
        r18 = org.telegram.tgnet.ConnectionsManager.getInstance(r1);
        r20 = new org.telegram.messenger.-$$Lambda$FileUploadOperation$XMcVvcrqfWd56m49RmvLW8_t4Os;
        r1 = r20;
        r2 = r27;
        r5 = r6;
        r6 = r13;
        r7 = r0;
        r8 = r9;
        r9 = r10;
        r11 = r19;
        r1.<init>(r2, r3, r4, r5, r6, r7, r8, r9, r11);
        r21 = 0;
        r0 = new org.telegram.messenger.-$$Lambda$FileUploadOperation$H-o0ouVev-JFhE9lBzpUHg6WYPI;
        r0.<init>(r12);
        r23 = 0;
        r24 = NUM; // 0x7fffffff float:NaN double:1.060997895E-314;
        r26 = 1;
        r22 = r0;
        r0 = r18.sendRequest(r19, r20, r21, r22, r23, r24, r25, r26);
        r1 = r12.requestTokens;
        r1.put(r13, r0);
        return;
    L_0x04a9:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
        r1 = 4;
        r12.state = r1;
        r0 = r12.delegate;
        r0.didFailedUploadingFile(r12);
        r27.cleanup();
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.FileUploadOperation.startUploadRequest():void");
    }

    public /* synthetic */ void lambda$startUploadRequest$4$FileUploadOperation(int i, int i2, byte[] bArr, int i3, int i4, int i5, long j, TLObject tLObject, TLObject tLObject2, TL_error tL_error) {
        int i6 = i2;
        Object obj = bArr;
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
            if (obj != null) {
                this.freeRequestIvs.add(obj);
            }
            this.requestTokens.delete(i3);
            if (!(tLObject3 instanceof TL_boolTrue)) {
                if (tLObject != null) {
                    FileLog.e("23123");
                }
                this.state = 4;
                this.delegate.didFailedUploadingFile(this);
                cleanup();
            } else if (this.state == 1) {
                long max;
                this.uploadedBytesCount += (long) i4;
                i6 = this.estimatedSize;
                if (i6 != 0) {
                    max = Math.max(this.availableSize, (long) i6);
                } else {
                    max = this.totalFileSize;
                }
                this.delegate.didChangedUploadProgress(this, ((float) this.uploadedBytesCount) / ((float) max));
                this.currentUploadRequetsCount--;
                if (this.isLastPart && this.currentUploadRequetsCount == 0 && this.state == 1) {
                    this.state = 3;
                    String str = "";
                    if (this.key == null) {
                        InputFile tL_inputFileBig;
                        if (this.isBigFile) {
                            tL_inputFileBig = new TL_inputFileBig();
                        } else {
                            tL_inputFileBig = new TL_inputFile();
                            tL_inputFileBig.md5_checksum = str;
                        }
                        tL_inputFileBig.parts = this.currentPartNum;
                        tL_inputFileBig.id = this.currentFileId;
                        str = this.uploadingFilePath;
                        tL_inputFileBig.name = str.substring(str.lastIndexOf("/") + 1);
                        this.delegate.didFinishUploadingFile(this, tL_inputFileBig, null, null, null);
                        cleanup();
                    } else {
                        InputEncryptedFile tL_inputEncryptedFileBigUploaded;
                        if (this.isBigFile) {
                            tL_inputEncryptedFileBigUploaded = new TL_inputEncryptedFileBigUploaded();
                        } else {
                            tL_inputEncryptedFileBigUploaded = new TL_inputEncryptedFileUploaded();
                            tL_inputEncryptedFileBigUploaded.md5_checksum = str;
                        }
                        tL_inputEncryptedFileBigUploaded.parts = this.currentPartNum;
                        tL_inputEncryptedFileBigUploaded.id = this.currentFileId;
                        tL_inputEncryptedFileBigUploaded.key_fingerprint = this.fingerprint;
                        this.delegate.didFinishUploadingFile(this, null, tL_inputEncryptedFileBigUploaded, this.key, this.iv);
                        cleanup();
                    }
                    i6 = this.currentType;
                    if (i6 == 50331648) {
                        StatsController.getInstance(this.currentAccount).incrementSentItemsCount(ApplicationLoader.getCurrentNetworkType(), 3, 1);
                    } else if (i6 == 33554432) {
                        StatsController.getInstance(this.currentAccount).incrementSentItemsCount(ApplicationLoader.getCurrentNetworkType(), 2, 1);
                    } else if (i6 == 16777216) {
                        StatsController.getInstance(this.currentAccount).incrementSentItemsCount(ApplicationLoader.getCurrentNetworkType(), 4, 1);
                    } else if (i6 == 67108864) {
                        StatsController.getInstance(this.currentAccount).incrementSentItemsCount(ApplicationLoader.getCurrentNetworkType(), 5, 1);
                    }
                } else if (this.currentUploadRequetsCount < this.maxRequestsCount) {
                    if (!(this.estimatedSize != 0 || this.uploadFirstPartLater || this.nextPartFirst)) {
                        if (this.saveInfoTimes >= 4) {
                            this.saveInfoTimes = 0;
                        }
                        i6 = this.lastSavedPartNum;
                        if (i7 == i6) {
                            this.lastSavedPartNum = i6 + 1;
                            byte[] bArr2 = obj;
                            long j2 = j;
                            while (true) {
                                UploadCachedResult uploadCachedResult = (UploadCachedResult) this.cachedResults.get(this.lastSavedPartNum);
                                if (uploadCachedResult == null) {
                                    break;
                                }
                                j2 = uploadCachedResult.bytesOffset;
                                bArr2 = uploadCachedResult.iv;
                                this.cachedResults.remove(this.lastSavedPartNum);
                                this.lastSavedPartNum++;
                            }
                            if ((this.isBigFile && j2 % 1048576 == 0) || (!this.isBigFile && this.saveInfoTimes == 0)) {
                                Editor edit = this.preferences.edit();
                                StringBuilder stringBuilder = new StringBuilder();
                                stringBuilder.append(this.fileKey);
                                stringBuilder.append("_uploaded");
                                edit.putLong(stringBuilder.toString(), j2);
                                if (this.isEncrypted) {
                                    StringBuilder stringBuilder2 = new StringBuilder();
                                    stringBuilder2.append(this.fileKey);
                                    stringBuilder2.append("_ivc");
                                    edit.putString(stringBuilder2.toString(), Utilities.bytesToHex(bArr2));
                                }
                                edit.commit();
                            }
                        } else {
                            UploadCachedResult uploadCachedResult2 = new UploadCachedResult();
                            uploadCachedResult2.bytesOffset = j;
                            if (obj != null) {
                                uploadCachedResult2.iv = new byte[32];
                                System.arraycopy(obj, 0, uploadCachedResult2.iv, 0, 32);
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
        Utilities.stageQueue.postRunnable(new -$$Lambda$FileUploadOperation$gfXB7X6eyjMCnkoAcAZ52SVcCAA(this));
    }

    public /* synthetic */ void lambda$null$5$FileUploadOperation() {
        if (this.currentUploadRequetsCount < this.maxRequestsCount) {
            startUploadRequest();
        }
    }
}
