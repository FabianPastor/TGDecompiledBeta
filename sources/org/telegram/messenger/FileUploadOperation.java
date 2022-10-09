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
import org.telegram.tgnet.TLRPC$TL_inputFile;
import org.telegram.tgnet.TLRPC$TL_inputFileBig;
/* loaded from: classes.dex */
public class FileUploadOperation {
    private static final int initialRequestsCount = 8;
    private static final int initialRequestsSlowNetworkCount = 1;
    private static final int maxUploadingKBytes = 2048;
    private static final int maxUploadingSlowNetworkKBytes = 32;
    private static final int minUploadChunkSize = 128;
    private static final int minUploadChunkSlowNetworkSize = 32;
    private long availableSize;
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
    private int saveInfoTimes;
    private boolean slowNetwork;
    private boolean started;
    private int state;
    private RandomAccessFile stream;
    private long totalFileSize;
    private int totalPartsCount;
    private boolean uploadFirstPartLater;
    private int uploadStartTime;
    private long uploadedBytesCount;
    private String uploadingFilePath;
    private int uploadChunkSize = 65536;
    private SparseIntArray requestTokens = new SparseIntArray();
    private SparseArray<UploadCachedResult> cachedResults = new SparseArray<>();

    /* loaded from: classes.dex */
    public interface FileUploadOperationDelegate {
        void didChangedUploadProgress(FileUploadOperation fileUploadOperation, long j, long j2);

        void didFailedUploadingFile(FileUploadOperation fileUploadOperation);

        void didFinishUploadingFile(FileUploadOperation fileUploadOperation, TLRPC$InputFile tLRPC$InputFile, TLRPC$InputEncryptedFile tLRPC$InputEncryptedFile, byte[] bArr, byte[] bArr2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class UploadCachedResult {
        private long bytesOffset;
        private byte[] iv;

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
        if (this.state != 0) {
            return;
        }
        this.state = 1;
        Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.FileUploadOperation$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                FileUploadOperation.this.lambda$start$0();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
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

    /* JADX INFO: Access modifiers changed from: protected */
    public void onNetworkChanged(final boolean z) {
        if (this.state != 1) {
            return;
        }
        Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.FileUploadOperation$$ExternalSyntheticLambda4
            @Override // java.lang.Runnable
            public final void run() {
                FileUploadOperation.this.lambda$onNetworkChanged$1(z);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
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
            this.readBytesCount = 0L;
            this.uploadedBytesCount = 0L;
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
        if (this.state == 3) {
            return;
        }
        this.state = 2;
        Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.FileUploadOperation$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                FileUploadOperation.this.lambda$cancel$2();
            }
        });
        this.delegate.didFailedUploadingFile(this);
        cleanup();
    }

    /* JADX INFO: Access modifiers changed from: private */
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
            if (randomAccessFile == null) {
                return;
            }
            randomAccessFile.close();
            this.stream = null;
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void checkNewDataAvailable(final long j, final long j2) {
        Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.FileUploadOperation$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                FileUploadOperation.this.lambda$checkNewDataAvailable$3(j2, j);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$checkNewDataAvailable$3(long j, long j2) {
        if (this.estimatedSize != 0 && j != 0) {
            this.estimatedSize = 0L;
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
        if (this.uploadFirstPartLater) {
            if (this.isBigFile) {
                long j = this.totalFileSize;
                int i = this.uploadChunkSize;
                this.totalPartsCount = ((int) ((((j - i) + i) - 1) / i)) + 1;
                return;
            }
            int i2 = this.uploadChunkSize;
            this.totalPartsCount = ((int) ((((this.totalFileSize - 1024) + i2) - 1) / i2)) + 1;
            return;
        }
        long j2 = this.totalFileSize;
        int i3 = this.uploadChunkSize;
        this.totalPartsCount = (int) (((j2 + i3) - 1) / i3);
    }

    public void setForceSmallFile() {
        this.forceSmallFile = true;
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:127:0x02ed A[Catch: Exception -> 0x0508, TryCatch #2 {Exception -> 0x0508, blocks: (B:5:0x0008, B:7:0x0016, B:10:0x0029, B:16:0x005a, B:18:0x0060, B:20:0x0069, B:22:0x006d, B:24:0x0076, B:25:0x0078, B:27:0x0091, B:29:0x009a, B:30:0x00a3, B:34:0x00ac, B:37:0x00c7, B:39:0x00cb, B:40:0x00ce, B:41:0x00d0, B:45:0x00d9, B:47:0x00e6, B:48:0x00f0, B:50:0x00f4, B:51:0x00fe, B:55:0x0120, B:57:0x0155, B:59:0x0159, B:61:0x015f, B:63:0x0165, B:65:0x01b7, B:68:0x01ed, B:71:0x01ff, B:73:0x0202, B:75:0x0205, B:80:0x0215, B:82:0x0219, B:92:0x0239, B:95:0x0246, B:97:0x0251, B:99:0x025d, B:101:0x0261, B:103:0x0269, B:105:0x0274, B:108:0x027d, B:112:0x028a, B:113:0x0291, B:114:0x02a8, B:107:0x027b, B:115:0x02b0, B:117:0x02b9, B:119:0x02d4, B:121:0x02dc, B:123:0x02df, B:124:0x02e5, B:127:0x02ed, B:129:0x02f1, B:130:0x0311, B:132:0x031d, B:134:0x0321, B:136:0x0327, B:137:0x032a, B:146:0x0361, B:148:0x036d, B:150:0x0371, B:152:0x0388, B:151:0x037f, B:145:0x035e, B:86:0x0225, B:19:0x0063, B:153:0x038b, B:154:0x0390, B:155:0x0391, B:156:0x0396, B:157:0x0397, B:159:0x039d, B:162:0x03aa, B:164:0x03ae, B:166:0x03b7, B:168:0x03cc, B:173:0x03dc, B:175:0x03e0, B:177:0x03e4, B:179:0x03ec, B:181:0x03f7, B:183:0x03fb, B:185:0x0401, B:191:0x0414, B:195:0x0421, B:196:0x0428, B:198:0x0455, B:200:0x0459, B:202:0x046c, B:204:0x0473, B:206:0x048b, B:208:0x048f, B:210:0x0493, B:212:0x04a4, B:203:0x046f, B:205:0x0479, B:187:0x0408, B:189:0x040c, B:190:0x0412, B:167:0x03c1, B:169:0x03cf, B:139:0x032e, B:142:0x0349, B:11:0x0032), top: B:228:0x0008, inners: #0 }] */
    /* JADX WARN: Removed duplicated region for block: B:148:0x036d A[Catch: Exception -> 0x0508, TryCatch #2 {Exception -> 0x0508, blocks: (B:5:0x0008, B:7:0x0016, B:10:0x0029, B:16:0x005a, B:18:0x0060, B:20:0x0069, B:22:0x006d, B:24:0x0076, B:25:0x0078, B:27:0x0091, B:29:0x009a, B:30:0x00a3, B:34:0x00ac, B:37:0x00c7, B:39:0x00cb, B:40:0x00ce, B:41:0x00d0, B:45:0x00d9, B:47:0x00e6, B:48:0x00f0, B:50:0x00f4, B:51:0x00fe, B:55:0x0120, B:57:0x0155, B:59:0x0159, B:61:0x015f, B:63:0x0165, B:65:0x01b7, B:68:0x01ed, B:71:0x01ff, B:73:0x0202, B:75:0x0205, B:80:0x0215, B:82:0x0219, B:92:0x0239, B:95:0x0246, B:97:0x0251, B:99:0x025d, B:101:0x0261, B:103:0x0269, B:105:0x0274, B:108:0x027d, B:112:0x028a, B:113:0x0291, B:114:0x02a8, B:107:0x027b, B:115:0x02b0, B:117:0x02b9, B:119:0x02d4, B:121:0x02dc, B:123:0x02df, B:124:0x02e5, B:127:0x02ed, B:129:0x02f1, B:130:0x0311, B:132:0x031d, B:134:0x0321, B:136:0x0327, B:137:0x032a, B:146:0x0361, B:148:0x036d, B:150:0x0371, B:152:0x0388, B:151:0x037f, B:145:0x035e, B:86:0x0225, B:19:0x0063, B:153:0x038b, B:154:0x0390, B:155:0x0391, B:156:0x0396, B:157:0x0397, B:159:0x039d, B:162:0x03aa, B:164:0x03ae, B:166:0x03b7, B:168:0x03cc, B:173:0x03dc, B:175:0x03e0, B:177:0x03e4, B:179:0x03ec, B:181:0x03f7, B:183:0x03fb, B:185:0x0401, B:191:0x0414, B:195:0x0421, B:196:0x0428, B:198:0x0455, B:200:0x0459, B:202:0x046c, B:204:0x0473, B:206:0x048b, B:208:0x048f, B:210:0x0493, B:212:0x04a4, B:203:0x046f, B:205:0x0479, B:187:0x0408, B:189:0x040c, B:190:0x0412, B:167:0x03c1, B:169:0x03cf, B:139:0x032e, B:142:0x0349, B:11:0x0032), top: B:228:0x0008, inners: #0 }] */
    /* JADX WARN: Removed duplicated region for block: B:226:0x032e A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:90:0x0235  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void startUploadRequest() {
        /*
            Method dump skipped, instructions count: 1304
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.FileUploadOperation.startUploadRequest():void");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$startUploadRequest$4(int i, int i2, byte[] bArr, int i3, int i4, int i5, long j, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        long j2;
        TLRPC$InputEncryptedFile tLRPC$InputEncryptedFile;
        TLRPC$InputFile tLRPC$TL_inputFile;
        byte[] bArr2 = bArr;
        if (i != this.operationGuid) {
            return;
        }
        int currentNetworkType = tLObject != null ? tLObject.networkType : ApplicationLoader.getCurrentNetworkType();
        int i6 = this.currentType;
        if (i6 == 50331648) {
            StatsController.getInstance(this.currentAccount).incrementSentBytesCount(currentNetworkType, 3, i2);
        } else if (i6 == 33554432) {
            StatsController.getInstance(this.currentAccount).incrementSentBytesCount(currentNetworkType, 2, i2);
        } else if (i6 == 16777216) {
            StatsController.getInstance(this.currentAccount).incrementSentBytesCount(currentNetworkType, 4, i2);
        } else if (i6 == 67108864) {
            StatsController.getInstance(this.currentAccount).incrementSentBytesCount(currentNetworkType, 5, i2);
        }
        if (bArr2 != null) {
            this.freeRequestIvs.add(bArr2);
        }
        this.requestTokens.delete(i3);
        if (tLObject instanceof TLRPC$TL_boolTrue) {
            if (this.state != 1) {
                return;
            }
            this.uploadedBytesCount += i4;
            long j3 = this.estimatedSize;
            if (j3 != 0) {
                j2 = Math.max(this.availableSize, j3);
            } else {
                j2 = this.totalFileSize;
            }
            this.delegate.didChangedUploadProgress(this, this.uploadedBytesCount, j2);
            int i7 = this.currentUploadRequetsCount - 1;
            this.currentUploadRequetsCount = i7;
            if (this.isLastPart && i7 == 0 && this.state == 1) {
                this.state = 3;
                if (this.key == null) {
                    if (this.isBigFile) {
                        tLRPC$TL_inputFile = new TLRPC$TL_inputFileBig();
                    } else {
                        tLRPC$TL_inputFile = new TLRPC$TL_inputFile();
                        tLRPC$TL_inputFile.md5_checksum = "";
                    }
                    tLRPC$TL_inputFile.parts = this.currentPartNum;
                    tLRPC$TL_inputFile.id = this.currentFileId;
                    String str = this.uploadingFilePath;
                    tLRPC$TL_inputFile.name = str.substring(str.lastIndexOf("/") + 1);
                    this.delegate.didFinishUploadingFile(this, tLRPC$TL_inputFile, null, null, null);
                    cleanup();
                } else {
                    if (this.isBigFile) {
                        tLRPC$InputEncryptedFile = new TLRPC$InputEncryptedFile() { // from class: org.telegram.tgnet.TLRPC$TL_inputEncryptedFileBigUploaded
                            public static int constructor = NUM;

                            @Override // org.telegram.tgnet.TLObject
                            public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
                                this.id = abstractSerializedData.readInt64(z);
                                this.parts = abstractSerializedData.readInt32(z);
                                this.key_fingerprint = abstractSerializedData.readInt32(z);
                            }

                            @Override // org.telegram.tgnet.TLObject
                            public void serializeToStream(AbstractSerializedData abstractSerializedData) {
                                abstractSerializedData.writeInt32(constructor);
                                abstractSerializedData.writeInt64(this.id);
                                abstractSerializedData.writeInt32(this.parts);
                                abstractSerializedData.writeInt32(this.key_fingerprint);
                            }
                        };
                    } else {
                        tLRPC$InputEncryptedFile = new TLRPC$InputEncryptedFile() { // from class: org.telegram.tgnet.TLRPC$TL_inputEncryptedFileUploaded
                            public static int constructor = NUM;

                            @Override // org.telegram.tgnet.TLObject
                            public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
                                this.id = abstractSerializedData.readInt64(z);
                                this.parts = abstractSerializedData.readInt32(z);
                                this.md5_checksum = abstractSerializedData.readString(z);
                                this.key_fingerprint = abstractSerializedData.readInt32(z);
                            }

                            @Override // org.telegram.tgnet.TLObject
                            public void serializeToStream(AbstractSerializedData abstractSerializedData) {
                                abstractSerializedData.writeInt32(constructor);
                                abstractSerializedData.writeInt64(this.id);
                                abstractSerializedData.writeInt32(this.parts);
                                abstractSerializedData.writeString(this.md5_checksum);
                                abstractSerializedData.writeInt32(this.key_fingerprint);
                            }
                        };
                        tLRPC$InputEncryptedFile.md5_checksum = "";
                    }
                    tLRPC$InputEncryptedFile.parts = this.currentPartNum;
                    tLRPC$InputEncryptedFile.id = this.currentFileId;
                    tLRPC$InputEncryptedFile.key_fingerprint = this.fingerprint;
                    this.delegate.didFinishUploadingFile(this, null, tLRPC$InputEncryptedFile, this.key, this.iv);
                    cleanup();
                }
                int i8 = this.currentType;
                if (i8 == 50331648) {
                    StatsController.getInstance(this.currentAccount).incrementSentItemsCount(ApplicationLoader.getCurrentNetworkType(), 3, 1);
                    return;
                } else if (i8 == 33554432) {
                    StatsController.getInstance(this.currentAccount).incrementSentItemsCount(ApplicationLoader.getCurrentNetworkType(), 2, 1);
                    return;
                } else if (i8 == 16777216) {
                    StatsController.getInstance(this.currentAccount).incrementSentItemsCount(ApplicationLoader.getCurrentNetworkType(), 4, 1);
                    return;
                } else if (i8 != 67108864) {
                    return;
                } else {
                    StatsController.getInstance(this.currentAccount).incrementSentItemsCount(ApplicationLoader.getCurrentNetworkType(), 5, 1);
                    return;
                }
            } else if (i7 >= this.maxRequestsCount) {
                return;
            } else {
                if (this.estimatedSize == 0 && !this.uploadFirstPartLater && !this.nextPartFirst) {
                    if (this.saveInfoTimes >= 4) {
                        this.saveInfoTimes = 0;
                    }
                    int i9 = this.lastSavedPartNum;
                    if (i5 == i9) {
                        this.lastSavedPartNum = i9 + 1;
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
                        uploadCachedResult2.bytesOffset = j;
                        if (bArr2 != null) {
                            uploadCachedResult2.iv = new byte[32];
                            System.arraycopy(bArr2, 0, uploadCachedResult2.iv, 0, 32);
                        }
                        this.cachedResults.put(i5, uploadCachedResult2);
                    }
                    this.saveInfoTimes++;
                }
                startUploadRequest();
                return;
            }
        }
        this.state = 4;
        this.delegate.didFailedUploadingFile(this);
        cleanup();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$startUploadRequest$6() {
        Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.FileUploadOperation$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                FileUploadOperation.this.lambda$startUploadRequest$5();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$startUploadRequest$5() {
        if (this.currentUploadRequetsCount < this.maxRequestsCount) {
            startUploadRequest();
        }
    }
}
