package org.telegram.messenger;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.util.SparseArray;
import android.util.SparseIntArray;
import java.io.File;
import java.io.RandomAccessFile;
import java.security.MessageDigest;
import java.util.ArrayList;
import org.telegram.messenger.exoplayer2.C0542C;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.NativeByteBuffer;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.InputEncryptedFile;
import org.telegram.tgnet.TLRPC.InputFile;
import org.telegram.tgnet.TLRPC.TL_boolTrue;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_inputEncryptedFileBigUploaded;
import org.telegram.tgnet.TLRPC.TL_inputEncryptedFileUploaded;
import org.telegram.tgnet.TLRPC.TL_inputFile;
import org.telegram.tgnet.TLRPC.TL_inputFileBig;
import org.telegram.tgnet.TLRPC.TL_upload_saveBigFilePart;
import org.telegram.tgnet.TLRPC.TL_upload_saveFilePart;
import org.telegram.tgnet.WriteToSocketDelegate;

public class FileUploadOperation {
    private static final int initialRequestsCount = 8;
    private static final int maxUploadingKBytes = 2048;
    private static final int minUploadChunkSize = 128;
    private long availableSize;
    private SparseArray<UploadCachedResult> cachedResults = new SparseArray();
    private int currentAccount;
    private long currentFileId;
    private int currentPartNum;
    private int currentType;
    private int currentUploadRequetsCount;
    private int currentUploadingBytes;
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
    private SharedPreferences preferences;
    private byte[] readBuffer;
    private long readBytesCount;
    private int requestNum;
    private SparseIntArray requestTokens = new SparseIntArray();
    private int saveInfoTimes;
    private boolean started;
    private int state;
    private RandomAccessFile stream;
    private long totalFileSize;
    private int totalPartsCount;
    private int uploadChunkSize = C0542C.DEFAULT_BUFFER_SEGMENT_SIZE;
    private boolean uploadFirstPartLater;
    private int uploadStartTime;
    private long uploadedBytesCount;
    private String uploadingFilePath;

    /* renamed from: org.telegram.messenger.FileUploadOperation$1 */
    class C01741 implements Runnable {
        C01741() {
        }

        public void run() {
            int a = 0;
            FileUploadOperation.this.preferences = ApplicationLoader.applicationContext.getSharedPreferences("uploadinfo", 0);
            while (true) {
                int a2 = a;
                if (a2 < 8) {
                    FileUploadOperation.this.startUploadRequest();
                    a = a2 + 1;
                } else {
                    return;
                }
            }
        }
    }

    /* renamed from: org.telegram.messenger.FileUploadOperation$2 */
    class C01752 implements Runnable {
        C01752() {
        }

        public void run() {
            for (int a = 0; a < FileUploadOperation.this.requestTokens.size(); a++) {
                ConnectionsManager.getInstance(FileUploadOperation.this.currentAccount).cancelRequest(FileUploadOperation.this.requestTokens.valueAt(a), true);
            }
        }
    }

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

    /* renamed from: org.telegram.messenger.FileUploadOperation$5 */
    class C18045 implements WriteToSocketDelegate {

        /* renamed from: org.telegram.messenger.FileUploadOperation$5$1 */
        class C01771 implements Runnable {
            C01771() {
            }

            public void run() {
                if (FileUploadOperation.this.currentUploadRequetsCount < FileUploadOperation.this.maxRequestsCount) {
                    FileUploadOperation.this.startUploadRequest();
                }
            }
        }

        C18045() {
        }

        public void run() {
            Utilities.stageQueue.postRunnable(new C01771());
        }
    }

    public FileUploadOperation(int instance, String location, boolean encrypted, int estimated, int type) {
        this.currentAccount = instance;
        this.uploadingFilePath = location;
        this.isEncrypted = encrypted;
        this.estimatedSize = estimated;
        this.currentType = type;
        boolean z = (estimated == 0 || this.isEncrypted) ? false : true;
        this.uploadFirstPartLater = z;
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
            Utilities.stageQueue.postRunnable(new C01741());
        }
    }

    public void cancel() {
        if (this.state != 3) {
            this.state = 2;
            Utilities.stageQueue.postRunnable(new C01752());
            this.delegate.didFailedUploadingFile(this);
            cleanup();
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
        } catch (Throwable e) {
            FileLog.m3e(e);
        }
    }

    protected void checkNewDataAvailable(long newAvailableSize, long finalSize) {
        final long j = finalSize;
        final long j2 = newAvailableSize;
        Utilities.stageQueue.postRunnable(new Runnable() {
            public void run() {
                if (!(FileUploadOperation.this.estimatedSize == 0 || j == 0)) {
                    FileUploadOperation.this.estimatedSize = 0;
                    FileUploadOperation.this.totalFileSize = j;
                    FileUploadOperation.this.calcTotalPartsCount();
                    if (!FileUploadOperation.this.uploadFirstPartLater && FileUploadOperation.this.started) {
                        FileUploadOperation.this.storeFileUploadInfo();
                    }
                }
                FileUploadOperation.this.availableSize = j2;
                if (FileUploadOperation.this.currentUploadRequetsCount < FileUploadOperation.this.maxRequestsCount) {
                    FileUploadOperation.this.startUploadRequest();
                }
            }
        });
    }

    private void storeFileUploadInfo() {
        Editor editor = this.preferences.edit();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.fileKey);
        stringBuilder.append("_time");
        editor.putInt(stringBuilder.toString(), this.uploadStartTime);
        stringBuilder = new StringBuilder();
        stringBuilder.append(this.fileKey);
        stringBuilder.append("_size");
        editor.putLong(stringBuilder.toString(), this.totalFileSize);
        stringBuilder = new StringBuilder();
        stringBuilder.append(this.fileKey);
        stringBuilder.append("_id");
        editor.putLong(stringBuilder.toString(), this.currentFileId);
        stringBuilder = new StringBuilder();
        stringBuilder.append(this.fileKey);
        stringBuilder.append("_uploaded");
        editor.remove(stringBuilder.toString());
        if (this.isEncrypted) {
            stringBuilder = new StringBuilder();
            stringBuilder.append(this.fileKey);
            stringBuilder.append("_iv");
            editor.putString(stringBuilder.toString(), Utilities.bytesToHex(this.iv));
            stringBuilder = new StringBuilder();
            stringBuilder.append(this.fileKey);
            stringBuilder.append("_ivc");
            editor.putString(stringBuilder.toString(), Utilities.bytesToHex(this.ivChange));
            stringBuilder = new StringBuilder();
            stringBuilder.append(this.fileKey);
            stringBuilder.append("_key");
            editor.putString(stringBuilder.toString(), Utilities.bytesToHex(this.key));
        }
        editor.commit();
    }

    private void calcTotalPartsCount() {
        if (!this.uploadFirstPartLater) {
            this.totalPartsCount = ((int) ((this.totalFileSize + ((long) this.uploadChunkSize)) - 1)) / this.uploadChunkSize;
        } else if (this.isBigFile) {
            this.totalPartsCount = 1 + (((int) (((this.totalFileSize - ((long) this.uploadChunkSize)) + ((long) this.uploadChunkSize)) - 1)) / this.uploadChunkSize);
        } else {
            this.totalPartsCount = 1 + (((int) (((this.totalFileSize - 1024) + ((long) this.uploadChunkSize)) - 1)) / this.uploadChunkSize);
        }
    }

    private void startUploadRequest() {
        if (this.state == 1) {
            try {
                byte[] bArr;
                int b;
                int a;
                byte[] arr;
                byte[] digest;
                r11.started = true;
                if (r11.stream == null) {
                    File cacheFile = new File(r11.uploadingFilePath);
                    if (AndroidUtilities.isInternalUri(Uri.fromFile(cacheFile))) {
                        throw new Exception("trying to upload internal file");
                    }
                    int chunkSize;
                    r11.stream = new RandomAccessFile(cacheFile, "r");
                    if (r11.estimatedSize != 0) {
                        r11.totalFileSize = (long) r11.estimatedSize;
                    } else {
                        r11.totalFileSize = cacheFile.length();
                    }
                    if (r11.totalFileSize > 10485760) {
                        r11.isBigFile = true;
                    }
                    r11.uploadChunkSize = (int) Math.max(128, ((r11.totalFileSize + 3072000) - 1) / 3072000);
                    if (1024 % r11.uploadChunkSize != 0) {
                        chunkSize = 64;
                        while (r11.uploadChunkSize > chunkSize) {
                            chunkSize *= 2;
                        }
                        r11.uploadChunkSize = chunkSize;
                    }
                    r11.maxRequestsCount = 2048 / r11.uploadChunkSize;
                    if (r11.isEncrypted) {
                        r11.freeRequestIvs = new ArrayList(r11.maxRequestsCount);
                        for (chunkSize = 0; chunkSize < r11.maxRequestsCount; chunkSize++) {
                            r11.freeRequestIvs.add(new byte[32]);
                        }
                    }
                    r11.uploadChunkSize *= 1024;
                    calcTotalPartsCount();
                    r11.readBuffer = new byte[r11.uploadChunkSize];
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(r11.uploadingFilePath);
                    stringBuilder.append(r11.isEncrypted ? "enc" : TtmlNode.ANONYMOUS_REGION_ID);
                    r11.fileKey = Utilities.MD5(stringBuilder.toString());
                    SharedPreferences sharedPreferences = r11.preferences;
                    StringBuilder stringBuilder2 = new StringBuilder();
                    stringBuilder2.append(r11.fileKey);
                    stringBuilder2.append("_size");
                    long fileSize = sharedPreferences.getLong(stringBuilder2.toString(), 0);
                    r11.uploadStartTime = (int) (System.currentTimeMillis() / 1000);
                    boolean rewrite = false;
                    if (r11.uploadFirstPartLater || r11.nextPartFirst || r11.estimatedSize != 0 || fileSize != r11.totalFileSize) {
                        bArr = null;
                        rewrite = true;
                    } else {
                        SharedPreferences sharedPreferences2 = r11.preferences;
                        StringBuilder stringBuilder3 = new StringBuilder();
                        stringBuilder3.append(r11.fileKey);
                        stringBuilder3.append("_id");
                        r11.currentFileId = sharedPreferences2.getLong(stringBuilder3.toString(), 0);
                        int date = r11.preferences;
                        stringBuilder3 = new StringBuilder();
                        stringBuilder3.append(r11.fileKey);
                        stringBuilder3.append("_time");
                        date = date.getInt(stringBuilder3.toString(), 0);
                        long uploadedSize = r11.preferences;
                        StringBuilder stringBuilder4 = new StringBuilder();
                        stringBuilder4.append(r11.fileKey);
                        stringBuilder4.append("_uploaded");
                        uploadedSize = uploadedSize.getLong(stringBuilder4.toString(), 0);
                        if (r11.isEncrypted) {
                            String ivString = r11.preferences;
                            StringBuilder stringBuilder5 = new StringBuilder();
                            stringBuilder5.append(r11.fileKey);
                            stringBuilder5.append("_iv");
                            ivString = ivString.getString(stringBuilder5.toString(), null);
                            SharedPreferences sharedPreferences3 = r11.preferences;
                            stringBuilder5 = new StringBuilder();
                            stringBuilder5.append(r11.fileKey);
                            stringBuilder5.append("_key");
                            String keyString = sharedPreferences3.getString(stringBuilder5.toString(), null);
                            if (ivString == null || keyString == null) {
                                rewrite = true;
                            } else {
                                r11.key = Utilities.hexToBytes(keyString);
                                r11.iv = Utilities.hexToBytes(ivString);
                                if (r11.key == null || r11.iv == null || r11.key.length != 32 || r11.iv.length != 32) {
                                    rewrite = true;
                                } else {
                                    r11.ivChange = new byte[32];
                                    System.arraycopy(r11.iv, 0, r11.ivChange, 0, 32);
                                }
                            }
                        }
                        long j;
                        if (rewrite || date == 0) {
                            j = fileSize;
                            bArr = null;
                            rewrite = true;
                        } else {
                            boolean rewrite2;
                            if (r11.isBigFile && date < r11.uploadStartTime - 86400) {
                                date = 0;
                            } else if (!r11.isBigFile && ((float) date) < ((float) r11.uploadStartTime) - 5400.0f) {
                                date = 0;
                            }
                            if (date == 0) {
                                rewrite2 = rewrite;
                                j = fileSize;
                                bArr = null;
                            } else if (uploadedSize > 0) {
                                r11.readBytesCount = uploadedSize;
                                r11.currentPartNum = (int) (uploadedSize / ((long) r11.uploadChunkSize));
                                if (!r11.isBigFile) {
                                    b = 0;
                                    while (true) {
                                        rewrite2 = rewrite;
                                        date = date;
                                        j = fileSize;
                                        if (((long) b) >= r11.readBytesCount / ((long) r11.uploadChunkSize)) {
                                            break;
                                        }
                                        boolean z;
                                        rewrite = r11.stream.read(r11.readBuffer);
                                        date = 0;
                                        if (r11.isEncrypted && rewrite % 16 != 0) {
                                            date = 0 + (16 - (rewrite % 16));
                                        }
                                        NativeByteBuffer sendBuffer = new NativeByteBuffer(rewrite + date);
                                        if (rewrite == r11.uploadChunkSize) {
                                            z = true;
                                            if (r11.totalPartsCount == r11.currentPartNum + 1) {
                                            }
                                            sendBuffer.writeBytes(r11.readBuffer, 0, rewrite);
                                            if (r11.isEncrypted) {
                                                for (a = 0; a < date; a++) {
                                                    sendBuffer.writeByte(0);
                                                }
                                                Utilities.aesIgeEncryption(sendBuffer.buffer, r11.key, r11.ivChange, true, true, 0, rewrite + date);
                                            }
                                            sendBuffer.reuse();
                                            b++;
                                            rewrite = rewrite2;
                                            date = date;
                                            fileSize = j;
                                        } else {
                                            z = true;
                                        }
                                        r11.isLastPart = z;
                                        sendBuffer.writeBytes(r11.readBuffer, 0, rewrite);
                                        if (r11.isEncrypted) {
                                            for (a = 0; a < date; a++) {
                                                sendBuffer.writeByte(0);
                                            }
                                            Utilities.aesIgeEncryption(sendBuffer.buffer, r11.key, r11.ivChange, true, true, 0, rewrite + date);
                                        }
                                        sendBuffer.reuse();
                                        b++;
                                        rewrite = rewrite2;
                                        date = date;
                                        fileSize = j;
                                    }
                                } else {
                                    rewrite2 = rewrite;
                                    date = date;
                                    j = fileSize;
                                    r11.stream.seek(uploadedSize);
                                    if (r11.isEncrypted) {
                                        boolean date2;
                                        String ivcString = r11.preferences;
                                        StringBuilder stringBuilder6 = new StringBuilder();
                                        stringBuilder6.append(r11.fileKey);
                                        stringBuilder6.append("_ivc");
                                        bArr = null;
                                        ivcString = ivcString.getString(stringBuilder6.toString(), null);
                                        if (ivcString != null) {
                                            r11.ivChange = Utilities.hexToBytes(ivcString);
                                            if (r11.ivChange != null) {
                                                if (r11.ivChange.length == 32) {
                                                    rewrite = rewrite2;
                                                }
                                            }
                                            date2 = true;
                                            r11.readBytesCount = 0;
                                            r11.currentPartNum = 0;
                                        } else {
                                            date2 = true;
                                            r11.readBytesCount = 0;
                                            r11.currentPartNum = 0;
                                        }
                                        rewrite = date2;
                                    }
                                }
                                bArr = null;
                            } else {
                                date = date;
                                j = fileSize;
                                bArr = null;
                                rewrite = true;
                            }
                            rewrite = rewrite2;
                        }
                    }
                    if (rewrite) {
                        if (r11.isEncrypted) {
                            r11.iv = new byte[32];
                            r11.key = new byte[32];
                            r11.ivChange = new byte[32];
                            Utilities.random.nextBytes(r11.iv);
                            Utilities.random.nextBytes(r11.key);
                            System.arraycopy(r11.iv, 0, r11.ivChange, 0, 32);
                        }
                        r11.currentFileId = Utilities.random.nextLong();
                        if (!(r11.nextPartFirst || r11.uploadFirstPartLater || r11.estimatedSize != 0)) {
                            storeFileUploadInfo();
                        }
                    }
                    if (r11.isEncrypted) {
                        try {
                            MessageDigest md = MessageDigest.getInstance("MD5");
                            arr = new byte[64];
                            System.arraycopy(r11.key, 0, arr, 0, 32);
                            System.arraycopy(r11.iv, 0, arr, 32, 32);
                            digest = md.digest(arr);
                            for (b = 0; b < 4; b++) {
                                r11.fingerprint |= ((digest[b] ^ digest[b + 4]) & 255) << (b * 8);
                            }
                        } catch (Throwable e) {
                            FileLog.m3e(e);
                        }
                    }
                    r11.uploadedBytesCount = r11.readBytesCount;
                    r11.lastSavedPartNum = r11.currentPartNum;
                    if (r11.uploadFirstPartLater) {
                        if (r11.isBigFile) {
                            r11.stream.seek((long) r11.uploadChunkSize);
                            r11.readBytesCount = (long) r11.uploadChunkSize;
                        } else {
                            r11.stream.seek(1024);
                            r11.readBytesCount = 1024;
                        }
                        r11.currentPartNum = 1;
                    }
                } else {
                    bArr = null;
                }
                if (r11.estimatedSize == 0 || r11.readBytesCount + ((long) r11.uploadChunkSize) <= r11.availableSize) {
                    int read;
                    int i;
                    if (r11.nextPartFirst) {
                        r11.stream.seek(0);
                        if (r11.isBigFile) {
                            read = r11.stream.read(r11.readBuffer);
                            i = 0;
                        } else {
                            i = 0;
                            read = r11.stream.read(r11.readBuffer, 0, 1024);
                        }
                        r11.currentPartNum = i;
                    } else {
                        read = r11.stream.read(r11.readBuffer);
                    }
                    if (read != -1) {
                        int a2;
                        int i2;
                        TLObject finalRequest;
                        i = 0;
                        if (r11.isEncrypted && read % 16 != 0) {
                            i = 0 + (16 - (read % 16));
                        }
                        NativeByteBuffer sendBuffer2 = new NativeByteBuffer(read + i);
                        if (r11.nextPartFirst || read != r11.uploadChunkSize || (r11.estimatedSize == 0 && r11.totalPartsCount == r11.currentPartNum + 1)) {
                            if (r11.uploadFirstPartLater) {
                                r11.nextPartFirst = true;
                                r11.uploadFirstPartLater = false;
                            } else {
                                r11.isLastPart = true;
                            }
                        }
                        sendBuffer2.writeBytes(r11.readBuffer, 0, read);
                        if (r11.isEncrypted) {
                            for (a2 = 0; a2 < i; a2++) {
                                sendBuffer2.writeByte(0);
                            }
                            Utilities.aesIgeEncryption(sendBuffer2.buffer, r11.key, r11.ivChange, true, true, 0, read + i);
                            digest = (byte[]) r11.freeRequestIvs.get(0);
                            System.arraycopy(r11.ivChange, 0, digest, 0, 32);
                            r11.freeRequestIvs.remove(0);
                            bArr = digest;
                        }
                        digest = bArr;
                        if (r11.isBigFile) {
                            TLObject req = new TL_upload_saveBigFilePart();
                            i2 = r11.currentPartNum;
                            a = i2;
                            req.file_part = i2;
                            req.file_id = r11.currentFileId;
                            if (r11.estimatedSize != 0) {
                                req.file_total_parts = -1;
                            } else {
                                req.file_total_parts = r11.totalPartsCount;
                            }
                            req.bytes = sendBuffer2;
                            finalRequest = req;
                        } else {
                            finalRequest = new TL_upload_saveFilePart();
                            b = r11.currentPartNum;
                            a = b;
                            finalRequest.file_part = b;
                            finalRequest.file_id = r11.currentFileId;
                            finalRequest.bytes = sendBuffer2;
                        }
                        if (r11.isLastPart && r11.nextPartFirst) {
                            r11.nextPartFirst = false;
                            r11.currentPartNum = r11.totalPartsCount - 1;
                            r11.stream.seek(r11.totalFileSize);
                        }
                        r11.readBytesCount += (long) read;
                        TLObject finalRequest2 = finalRequest;
                        int currentRequestPartNum = a;
                        int currentRequestBytes = read;
                        arr = digest;
                        r11.currentPartNum++;
                        r11.currentUploadRequetsCount++;
                        read = r11.requestNum;
                        r11.requestNum = read + 1;
                        int requestNumFinal = read;
                        final long j2 = (long) (currentRequestPartNum + currentRequestBytes);
                        i = finalRequest2.getObjectSize() + 4;
                        a2 = requestNumFinal;
                        b = currentRequestBytes;
                        i2 = currentRequestPartNum;
                        long currentRequestBytesOffset = j2;
                        final TLObject tLObject = finalRequest2;
                        r11.requestTokens.put(requestNumFinal, ConnectionsManager.getInstance(r11.currentAccount).sendRequest(finalRequest2, new RequestDelegate() {
                            public void run(TLObject response, TL_error error) {
                                C18034 c18034 = this;
                                TLObject tLObject = response;
                                int networkType = tLObject != null ? tLObject.networkType : ConnectionsManager.getCurrentNetworkType();
                                if (FileUploadOperation.this.currentType == ConnectionsManager.FileTypeAudio) {
                                    StatsController.getInstance(FileUploadOperation.this.currentAccount).incrementSentBytesCount(networkType, 3, (long) i);
                                } else if (FileUploadOperation.this.currentType == ConnectionsManager.FileTypeVideo) {
                                    StatsController.getInstance(FileUploadOperation.this.currentAccount).incrementSentBytesCount(networkType, 2, (long) i);
                                } else if (FileUploadOperation.this.currentType == 16777216) {
                                    StatsController.getInstance(FileUploadOperation.this.currentAccount).incrementSentBytesCount(networkType, 4, (long) i);
                                } else if (FileUploadOperation.this.currentType == ConnectionsManager.FileTypeFile) {
                                    StatsController.getInstance(FileUploadOperation.this.currentAccount).incrementSentBytesCount(networkType, 5, (long) i);
                                }
                                if (arr != null) {
                                    FileUploadOperation.this.freeRequestIvs.add(arr);
                                }
                                FileUploadOperation.this.requestTokens.delete(a2);
                                if (!(tLObject instanceof TL_boolTrue)) {
                                    if (tLObject != null) {
                                        FileLog.m1e("23123");
                                    }
                                    FileUploadOperation.this.state = 4;
                                    FileUploadOperation.this.delegate.didFailedUploadingFile(FileUploadOperation.this);
                                    FileUploadOperation.this.cleanup();
                                } else if (FileUploadOperation.this.state == 1) {
                                    long size;
                                    FileUploadOperation.this.uploadedBytesCount = FileUploadOperation.this.uploadedBytesCount + ((long) b);
                                    if (FileUploadOperation.this.estimatedSize != 0) {
                                        size = Math.max(FileUploadOperation.this.availableSize, (long) FileUploadOperation.this.estimatedSize);
                                    } else {
                                        size = FileUploadOperation.this.totalFileSize;
                                    }
                                    FileUploadOperation.this.delegate.didChangedUploadProgress(FileUploadOperation.this, ((float) FileUploadOperation.this.uploadedBytesCount) / ((float) size));
                                    FileUploadOperation.this.currentUploadRequetsCount = FileUploadOperation.this.currentUploadRequetsCount - 1;
                                    if (FileUploadOperation.this.isLastPart && FileUploadOperation.this.currentUploadRequetsCount == 0 && FileUploadOperation.this.state == 1) {
                                        FileUploadOperation.this.state = 3;
                                        if (FileUploadOperation.this.key == null) {
                                            InputFile result;
                                            if (FileUploadOperation.this.isBigFile) {
                                                result = new TL_inputFileBig();
                                            } else {
                                                result = new TL_inputFile();
                                                result.md5_checksum = TtmlNode.ANONYMOUS_REGION_ID;
                                            }
                                            result.parts = FileUploadOperation.this.currentPartNum;
                                            result.id = FileUploadOperation.this.currentFileId;
                                            result.name = FileUploadOperation.this.uploadingFilePath.substring(FileUploadOperation.this.uploadingFilePath.lastIndexOf("/") + 1);
                                            FileUploadOperation.this.delegate.didFinishUploadingFile(FileUploadOperation.this, result, null, null, null);
                                            FileUploadOperation.this.cleanup();
                                        } else {
                                            InputEncryptedFile result2;
                                            if (FileUploadOperation.this.isBigFile) {
                                                result2 = new TL_inputEncryptedFileBigUploaded();
                                            } else {
                                                result2 = new TL_inputEncryptedFileUploaded();
                                                result2.md5_checksum = TtmlNode.ANONYMOUS_REGION_ID;
                                            }
                                            result2.parts = FileUploadOperation.this.currentPartNum;
                                            result2.id = FileUploadOperation.this.currentFileId;
                                            result2.key_fingerprint = FileUploadOperation.this.fingerprint;
                                            FileUploadOperation.this.delegate.didFinishUploadingFile(FileUploadOperation.this, null, result2, FileUploadOperation.this.key, FileUploadOperation.this.iv);
                                            FileUploadOperation.this.cleanup();
                                        }
                                        if (FileUploadOperation.this.currentType == ConnectionsManager.FileTypeAudio) {
                                            StatsController.getInstance(FileUploadOperation.this.currentAccount).incrementSentItemsCount(ConnectionsManager.getCurrentNetworkType(), 3, 1);
                                        } else if (FileUploadOperation.this.currentType == ConnectionsManager.FileTypeVideo) {
                                            StatsController.getInstance(FileUploadOperation.this.currentAccount).incrementSentItemsCount(ConnectionsManager.getCurrentNetworkType(), 2, 1);
                                        } else if (FileUploadOperation.this.currentType == 16777216) {
                                            StatsController.getInstance(FileUploadOperation.this.currentAccount).incrementSentItemsCount(ConnectionsManager.getCurrentNetworkType(), 4, 1);
                                        } else if (FileUploadOperation.this.currentType == ConnectionsManager.FileTypeFile) {
                                            StatsController.getInstance(FileUploadOperation.this.currentAccount).incrementSentItemsCount(ConnectionsManager.getCurrentNetworkType(), 5, 1);
                                        }
                                    } else if (FileUploadOperation.this.currentUploadRequetsCount < FileUploadOperation.this.maxRequestsCount) {
                                        if (!(FileUploadOperation.this.estimatedSize != 0 || FileUploadOperation.this.uploadFirstPartLater || FileUploadOperation.this.nextPartFirst)) {
                                            if (FileUploadOperation.this.saveInfoTimes >= 4) {
                                                FileUploadOperation.this.saveInfoTimes = 0;
                                            }
                                            if (i2 == FileUploadOperation.this.lastSavedPartNum) {
                                                FileUploadOperation.this.lastSavedPartNum = FileUploadOperation.this.lastSavedPartNum + 1;
                                                long offsetToSave = j2;
                                                byte[] ivToSave = arr;
                                                while (true) {
                                                    UploadCachedResult uploadCachedResult = (UploadCachedResult) FileUploadOperation.this.cachedResults.get(FileUploadOperation.this.lastSavedPartNum);
                                                    UploadCachedResult result3 = uploadCachedResult;
                                                    if (uploadCachedResult == null) {
                                                        break;
                                                    }
                                                    offsetToSave = result3.bytesOffset;
                                                    ivToSave = result3.iv;
                                                    FileUploadOperation.this.cachedResults.remove(FileUploadOperation.this.lastSavedPartNum);
                                                    FileUploadOperation.this.lastSavedPartNum = FileUploadOperation.this.lastSavedPartNum + 1;
                                                }
                                                if ((FileUploadOperation.this.isBigFile && offsetToSave % 1048576 == 0) || (!FileUploadOperation.this.isBigFile && FileUploadOperation.this.saveInfoTimes == 0)) {
                                                    Editor editor = FileUploadOperation.this.preferences.edit();
                                                    StringBuilder stringBuilder = new StringBuilder();
                                                    stringBuilder.append(FileUploadOperation.this.fileKey);
                                                    stringBuilder.append("_uploaded");
                                                    editor.putLong(stringBuilder.toString(), offsetToSave);
                                                    if (FileUploadOperation.this.isEncrypted) {
                                                        stringBuilder = new StringBuilder();
                                                        stringBuilder.append(FileUploadOperation.this.fileKey);
                                                        stringBuilder.append("_ivc");
                                                        editor.putString(stringBuilder.toString(), Utilities.bytesToHex(ivToSave));
                                                    }
                                                    editor.commit();
                                                }
                                            } else {
                                                UploadCachedResult result4 = new UploadCachedResult();
                                                result4.bytesOffset = j2;
                                                if (arr != null) {
                                                    result4.iv = new byte[32];
                                                    System.arraycopy(arr, 0, result4.iv, 0, 32);
                                                }
                                                FileUploadOperation.this.cachedResults.put(i2, result4);
                                            }
                                            FileUploadOperation.this.saveInfoTimes = FileUploadOperation.this.saveInfoTimes + 1;
                                        }
                                        FileUploadOperation.this.startUploadRequest();
                                    }
                                }
                            }
                        }, null, new C18045(), 0, ConnectionsManager.DEFAULT_DATACENTER_ID, 4 | ((requestNumFinal % 4) << 16), true));
                    }
                }
            } catch (Throwable e2) {
                FileLog.m3e(e2);
                r11.state = 4;
                r11.delegate.didFailedUploadingFile(r11);
                cleanup();
            }
        }
    }
}
