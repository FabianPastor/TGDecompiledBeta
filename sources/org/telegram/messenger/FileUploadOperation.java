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
            int i = 0;
            FileUploadOperation.this.preferences = ApplicationLoader.applicationContext.getSharedPreferences("uploadinfo", 0);
            while (i < 8) {
                FileUploadOperation.this.startUploadRequest();
                i++;
            }
        }
    }

    /* renamed from: org.telegram.messenger.FileUploadOperation$2 */
    class C01752 implements Runnable {
        C01752() {
        }

        public void run() {
            for (int i = 0; i < FileUploadOperation.this.requestTokens.size(); i++) {
                ConnectionsManager.getInstance(FileUploadOperation.this.currentAccount).cancelRequest(FileUploadOperation.this.requestTokens.valueAt(i), true);
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

    public FileUploadOperation(int i, String str, boolean z, int i2, int i3) {
        this.currentAccount = i;
        this.uploadingFilePath = str;
        this.isEncrypted = z;
        this.estimatedSize = i2;
        this.currentType = i3;
        i = (i2 == 0 || this.isEncrypted != 0) ? 0 : 1;
        this.uploadFirstPartLater = i;
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

    protected void checkNewDataAvailable(long j, long j2) {
        final long j3 = j2;
        final long j4 = j;
        Utilities.stageQueue.postRunnable(new Runnable() {
            public void run() {
                if (!(FileUploadOperation.this.estimatedSize == 0 || j3 == 0)) {
                    FileUploadOperation.this.estimatedSize = 0;
                    FileUploadOperation.this.totalFileSize = j3;
                    FileUploadOperation.this.calcTotalPartsCount();
                    if (!FileUploadOperation.this.uploadFirstPartLater && FileUploadOperation.this.started) {
                        FileUploadOperation.this.storeFileUploadInfo();
                    }
                }
                FileUploadOperation.this.availableSize = j4;
                if (FileUploadOperation.this.currentUploadRequetsCount < FileUploadOperation.this.maxRequestsCount) {
                    FileUploadOperation.this.startUploadRequest();
                }
            }
        });
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
                int i;
                int i2;
                int i3;
                r11.started = true;
                String str = null;
                if (r11.stream == null) {
                    File file = new File(r11.uploadingFilePath);
                    if (AndroidUtilities.isInternalUri(Uri.fromFile(file))) {
                        throw new Exception("trying to upload internal file");
                    }
                    boolean z;
                    MessageDigest instance;
                    Object obj;
                    byte[] digest;
                    r11.stream = new RandomAccessFile(file, "r");
                    if (r11.estimatedSize != 0) {
                        r11.totalFileSize = (long) r11.estimatedSize;
                    } else {
                        r11.totalFileSize = file.length();
                    }
                    if (r11.totalFileSize > 10485760) {
                        r11.isBigFile = true;
                    }
                    r11.uploadChunkSize = (int) Math.max(128, ((r11.totalFileSize + 3072000) - 1) / 3072000);
                    if (1024 % r11.uploadChunkSize != 0) {
                        i = 64;
                        while (r11.uploadChunkSize > i) {
                            i *= 2;
                        }
                        r11.uploadChunkSize = i;
                    }
                    r11.maxRequestsCount = 2048 / r11.uploadChunkSize;
                    if (r11.isEncrypted) {
                        r11.freeRequestIvs = new ArrayList(r11.maxRequestsCount);
                        for (i = 0; i < r11.maxRequestsCount; i++) {
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
                    long j = sharedPreferences.getLong(stringBuilder2.toString(), 0);
                    r11.uploadStartTime = (int) (System.currentTimeMillis() / 1000);
                    if (!r11.uploadFirstPartLater && !r11.nextPartFirst && r11.estimatedSize == 0 && j == r11.totalFileSize) {
                        StringBuilder stringBuilder3;
                        String string;
                        int read;
                        NativeByteBuffer nativeByteBuffer;
                        sharedPreferences = r11.preferences;
                        stringBuilder2 = new StringBuilder();
                        stringBuilder2.append(r11.fileKey);
                        stringBuilder2.append("_id");
                        r11.currentFileId = sharedPreferences.getLong(stringBuilder2.toString(), 0);
                        sharedPreferences = r11.preferences;
                        stringBuilder2 = new StringBuilder();
                        stringBuilder2.append(r11.fileKey);
                        stringBuilder2.append("_time");
                        i = sharedPreferences.getInt(stringBuilder2.toString(), 0);
                        SharedPreferences sharedPreferences2 = r11.preferences;
                        StringBuilder stringBuilder4 = new StringBuilder();
                        stringBuilder4.append(r11.fileKey);
                        stringBuilder4.append("_uploaded");
                        j = sharedPreferences2.getLong(stringBuilder4.toString(), 0);
                        if (r11.isEncrypted) {
                            SharedPreferences sharedPreferences3 = r11.preferences;
                            StringBuilder stringBuilder5 = new StringBuilder();
                            stringBuilder5.append(r11.fileKey);
                            stringBuilder5.append("_iv");
                            String string2 = sharedPreferences3.getString(stringBuilder5.toString(), null);
                            sharedPreferences3 = r11.preferences;
                            stringBuilder5 = new StringBuilder();
                            stringBuilder5.append(r11.fileKey);
                            stringBuilder5.append("_key");
                            String string3 = sharedPreferences3.getString(stringBuilder5.toString(), null);
                            if (!(string2 == null || string3 == null)) {
                                r11.key = Utilities.hexToBytes(string3);
                                r11.iv = Utilities.hexToBytes(string2);
                                if (r11.key != null && r11.iv != null && r11.key.length == 32 && r11.iv.length == 32) {
                                    r11.ivChange = new byte[32];
                                    System.arraycopy(r11.iv, 0, r11.ivChange, 0, 32);
                                }
                            }
                            z = true;
                            if (!(z || i == 0)) {
                                if (!r11.isBigFile || i >= r11.uploadStartTime - 86400) {
                                    if (!r11.isBigFile && ((float) i) < ((float) r11.uploadStartTime) - 5400.0f) {
                                    }
                                    if (i != 0) {
                                        if (j > 0) {
                                            r11.readBytesCount = j;
                                            r11.currentPartNum = (int) (j / ((long) r11.uploadChunkSize));
                                            if (r11.isBigFile) {
                                                r11.stream.seek(j);
                                                if (r11.isEncrypted) {
                                                    sharedPreferences = r11.preferences;
                                                    stringBuilder3 = new StringBuilder();
                                                    stringBuilder3.append(r11.fileKey);
                                                    stringBuilder3.append("_ivc");
                                                    string = sharedPreferences.getString(stringBuilder3.toString(), null);
                                                    if (string != null) {
                                                        r11.ivChange = Utilities.hexToBytes(string);
                                                        if (r11.ivChange == null || r11.ivChange.length != 32) {
                                                            r11.readBytesCount = 0;
                                                            r11.currentPartNum = 0;
                                                        }
                                                    } else {
                                                        r11.readBytesCount = 0;
                                                        r11.currentPartNum = 0;
                                                    }
                                                }
                                            } else {
                                                for (i = 0; ((long) i) < r11.readBytesCount / ((long) r11.uploadChunkSize); i++) {
                                                    read = r11.stream.read(r11.readBuffer);
                                                    i2 = (r11.isEncrypted || read % 16 == 0) ? 0 : (16 - (read % 16)) + 0;
                                                    i3 = read + i2;
                                                    nativeByteBuffer = new NativeByteBuffer(i3);
                                                    if (read != r11.uploadChunkSize || r11.totalPartsCount == r11.currentPartNum + 1) {
                                                        r11.isLastPart = true;
                                                    }
                                                    nativeByteBuffer.writeBytes(r11.readBuffer, 0, read);
                                                    if (!r11.isEncrypted) {
                                                        for (read = 0; read < i2; read++) {
                                                            nativeByteBuffer.writeByte(0);
                                                        }
                                                        Utilities.aesIgeEncryption(nativeByteBuffer.buffer, r11.key, r11.ivChange, true, true, 0, i3);
                                                    }
                                                    nativeByteBuffer.reuse();
                                                }
                                            }
                                        }
                                    }
                                    if (z) {
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
                                            instance = MessageDigest.getInstance("MD5");
                                            obj = new byte[64];
                                            System.arraycopy(r11.key, 0, obj, 0, 32);
                                            System.arraycopy(r11.iv, 0, obj, 32, 32);
                                            digest = instance.digest(obj);
                                            for (i = 0; i < 4; i++) {
                                                r11.fingerprint |= ((digest[i] ^ digest[i + 4]) & 255) << (i * 8);
                                            }
                                        } catch (Throwable e) {
                                            FileLog.m3e(e);
                                        }
                                    }
                                    r11.uploadedBytesCount = r11.readBytesCount;
                                    r11.lastSavedPartNum = r11.currentPartNum;
                                    if (r11.uploadFirstPartLater) {
                                        if (r11.isBigFile) {
                                            r11.stream.seek(1024);
                                            r11.readBytesCount = 1024;
                                        } else {
                                            r11.stream.seek((long) r11.uploadChunkSize);
                                            r11.readBytesCount = (long) r11.uploadChunkSize;
                                        }
                                        r11.currentPartNum = 1;
                                    }
                                }
                                i = 0;
                                if (i != 0) {
                                    if (j > 0) {
                                        r11.readBytesCount = j;
                                        r11.currentPartNum = (int) (j / ((long) r11.uploadChunkSize));
                                        if (r11.isBigFile) {
                                            r11.stream.seek(j);
                                            if (r11.isEncrypted) {
                                                sharedPreferences = r11.preferences;
                                                stringBuilder3 = new StringBuilder();
                                                stringBuilder3.append(r11.fileKey);
                                                stringBuilder3.append("_ivc");
                                                string = sharedPreferences.getString(stringBuilder3.toString(), null);
                                                if (string != null) {
                                                    r11.readBytesCount = 0;
                                                    r11.currentPartNum = 0;
                                                } else {
                                                    r11.ivChange = Utilities.hexToBytes(string);
                                                    r11.readBytesCount = 0;
                                                    r11.currentPartNum = 0;
                                                }
                                            }
                                        } else {
                                            for (i = 0; ((long) i) < r11.readBytesCount / ((long) r11.uploadChunkSize); i++) {
                                                read = r11.stream.read(r11.readBuffer);
                                                if (r11.isEncrypted) {
                                                }
                                                i3 = read + i2;
                                                nativeByteBuffer = new NativeByteBuffer(i3);
                                                r11.isLastPart = true;
                                                nativeByteBuffer.writeBytes(r11.readBuffer, 0, read);
                                                if (!r11.isEncrypted) {
                                                    for (read = 0; read < i2; read++) {
                                                        nativeByteBuffer.writeByte(0);
                                                    }
                                                    Utilities.aesIgeEncryption(nativeByteBuffer.buffer, r11.key, r11.ivChange, true, true, 0, i3);
                                                }
                                                nativeByteBuffer.reuse();
                                            }
                                        }
                                    }
                                }
                                if (z) {
                                    if (r11.isEncrypted) {
                                        r11.iv = new byte[32];
                                        r11.key = new byte[32];
                                        r11.ivChange = new byte[32];
                                        Utilities.random.nextBytes(r11.iv);
                                        Utilities.random.nextBytes(r11.key);
                                        System.arraycopy(r11.iv, 0, r11.ivChange, 0, 32);
                                    }
                                    r11.currentFileId = Utilities.random.nextLong();
                                    storeFileUploadInfo();
                                }
                                if (r11.isEncrypted) {
                                    instance = MessageDigest.getInstance("MD5");
                                    obj = new byte[64];
                                    System.arraycopy(r11.key, 0, obj, 0, 32);
                                    System.arraycopy(r11.iv, 0, obj, 32, 32);
                                    digest = instance.digest(obj);
                                    for (i = 0; i < 4; i++) {
                                        r11.fingerprint |= ((digest[i] ^ digest[i + 4]) & 255) << (i * 8);
                                    }
                                }
                                r11.uploadedBytesCount = r11.readBytesCount;
                                r11.lastSavedPartNum = r11.currentPartNum;
                                if (r11.uploadFirstPartLater) {
                                    if (r11.isBigFile) {
                                        r11.stream.seek(1024);
                                        r11.readBytesCount = 1024;
                                    } else {
                                        r11.stream.seek((long) r11.uploadChunkSize);
                                        r11.readBytesCount = (long) r11.uploadChunkSize;
                                    }
                                    r11.currentPartNum = 1;
                                }
                            }
                        }
                        z = false;
                        i = 0;
                        if (i != 0) {
                            if (j > 0) {
                                r11.readBytesCount = j;
                                r11.currentPartNum = (int) (j / ((long) r11.uploadChunkSize));
                                if (r11.isBigFile) {
                                    for (i = 0; ((long) i) < r11.readBytesCount / ((long) r11.uploadChunkSize); i++) {
                                        read = r11.stream.read(r11.readBuffer);
                                        if (r11.isEncrypted) {
                                        }
                                        i3 = read + i2;
                                        nativeByteBuffer = new NativeByteBuffer(i3);
                                        r11.isLastPart = true;
                                        nativeByteBuffer.writeBytes(r11.readBuffer, 0, read);
                                        if (!r11.isEncrypted) {
                                            for (read = 0; read < i2; read++) {
                                                nativeByteBuffer.writeByte(0);
                                            }
                                            Utilities.aesIgeEncryption(nativeByteBuffer.buffer, r11.key, r11.ivChange, true, true, 0, i3);
                                        }
                                        nativeByteBuffer.reuse();
                                    }
                                } else {
                                    r11.stream.seek(j);
                                    if (r11.isEncrypted) {
                                        sharedPreferences = r11.preferences;
                                        stringBuilder3 = new StringBuilder();
                                        stringBuilder3.append(r11.fileKey);
                                        stringBuilder3.append("_ivc");
                                        string = sharedPreferences.getString(stringBuilder3.toString(), null);
                                        if (string != null) {
                                            r11.ivChange = Utilities.hexToBytes(string);
                                            r11.readBytesCount = 0;
                                            r11.currentPartNum = 0;
                                        } else {
                                            r11.readBytesCount = 0;
                                            r11.currentPartNum = 0;
                                        }
                                    }
                                }
                            }
                        }
                        if (z) {
                            if (r11.isEncrypted) {
                                r11.iv = new byte[32];
                                r11.key = new byte[32];
                                r11.ivChange = new byte[32];
                                Utilities.random.nextBytes(r11.iv);
                                Utilities.random.nextBytes(r11.key);
                                System.arraycopy(r11.iv, 0, r11.ivChange, 0, 32);
                            }
                            r11.currentFileId = Utilities.random.nextLong();
                            storeFileUploadInfo();
                        }
                        if (r11.isEncrypted) {
                            instance = MessageDigest.getInstance("MD5");
                            obj = new byte[64];
                            System.arraycopy(r11.key, 0, obj, 0, 32);
                            System.arraycopy(r11.iv, 0, obj, 32, 32);
                            digest = instance.digest(obj);
                            for (i = 0; i < 4; i++) {
                                r11.fingerprint |= ((digest[i] ^ digest[i + 4]) & 255) << (i * 8);
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
                    }
                    z = true;
                    if (z) {
                        if (r11.isEncrypted) {
                            r11.iv = new byte[32];
                            r11.key = new byte[32];
                            r11.ivChange = new byte[32];
                            Utilities.random.nextBytes(r11.iv);
                            Utilities.random.nextBytes(r11.key);
                            System.arraycopy(r11.iv, 0, r11.ivChange, 0, 32);
                        }
                        r11.currentFileId = Utilities.random.nextLong();
                        storeFileUploadInfo();
                    }
                    if (r11.isEncrypted) {
                        instance = MessageDigest.getInstance("MD5");
                        obj = new byte[64];
                        System.arraycopy(r11.key, 0, obj, 0, 32);
                        System.arraycopy(r11.iv, 0, obj, 32, 32);
                        digest = instance.digest(obj);
                        for (i = 0; i < 4; i++) {
                            r11.fingerprint |= ((digest[i] ^ digest[i + 4]) & 255) << (i * 8);
                        }
                    }
                    r11.uploadedBytesCount = r11.readBytesCount;
                    r11.lastSavedPartNum = r11.currentPartNum;
                    if (r11.uploadFirstPartLater) {
                        if (r11.isBigFile) {
                            r11.stream.seek(1024);
                            r11.readBytesCount = 1024;
                        } else {
                            r11.stream.seek((long) r11.uploadChunkSize);
                            r11.readBytesCount = (long) r11.uploadChunkSize;
                        }
                        r11.currentPartNum = 1;
                    }
                }
                if (r11.estimatedSize == 0 || r11.readBytesCount + ((long) r11.uploadChunkSize) <= r11.availableSize) {
                    int read2;
                    if (r11.nextPartFirst) {
                        r11.stream.seek(0);
                        if (r11.isBigFile) {
                            read2 = r11.stream.read(r11.readBuffer);
                        } else {
                            read2 = r11.stream.read(r11.readBuffer, 0, 1024);
                        }
                        r11.currentPartNum = 0;
                    } else {
                        read2 = r11.stream.read(r11.readBuffer);
                    }
                    i2 = read2;
                    if (i2 != -1) {
                        int i4;
                        TLObject tLObject;
                        i = (!r11.isEncrypted || i2 % 16 == 0) ? 0 : (16 - (i2 % 16)) + 0;
                        int i5 = i2 + i;
                        NativeByteBuffer nativeByteBuffer2 = new NativeByteBuffer(i5);
                        if (r11.nextPartFirst || i2 != r11.uploadChunkSize || (r11.estimatedSize == 0 && r11.totalPartsCount == r11.currentPartNum + 1)) {
                            if (r11.uploadFirstPartLater) {
                                r11.nextPartFirst = true;
                                r11.uploadFirstPartLater = false;
                            } else {
                                r11.isLastPart = true;
                            }
                        }
                        nativeByteBuffer2.writeBytes(r11.readBuffer, 0, i2);
                        if (r11.isEncrypted) {
                            for (i4 = 0; i4 < i; i4++) {
                                nativeByteBuffer2.writeByte(0);
                            }
                            Utilities.aesIgeEncryption(nativeByteBuffer2.buffer, r11.key, r11.ivChange, true, true, 0, i5);
                            byte[] bArr = (byte[]) r11.freeRequestIvs.get(0);
                            System.arraycopy(r11.ivChange, 0, bArr, 0, 32);
                            r11.freeRequestIvs.remove(0);
                            str = bArr;
                        }
                        if (r11.isBigFile) {
                            TLObject tL_upload_saveBigFilePart = new TL_upload_saveBigFilePart();
                            i5 = r11.currentPartNum;
                            tL_upload_saveBigFilePart.file_part = i5;
                            tL_upload_saveBigFilePart.file_id = r11.currentFileId;
                            if (r11.estimatedSize != 0) {
                                tL_upload_saveBigFilePart.file_total_parts = -1;
                            } else {
                                tL_upload_saveBigFilePart.file_total_parts = r11.totalPartsCount;
                            }
                            tL_upload_saveBigFilePart.bytes = nativeByteBuffer2;
                            tLObject = tL_upload_saveBigFilePart;
                        } else {
                            TLObject tL_upload_saveFilePart = new TL_upload_saveFilePart();
                            i = r11.currentPartNum;
                            tL_upload_saveFilePart.file_part = i;
                            tL_upload_saveFilePart.file_id = r11.currentFileId;
                            tL_upload_saveFilePart.bytes = nativeByteBuffer2;
                            tLObject = tL_upload_saveFilePart;
                            i5 = i;
                        }
                        if (r11.isLastPart && r11.nextPartFirst) {
                            r11.nextPartFirst = false;
                            r11.currentPartNum = r11.totalPartsCount - 1;
                            r11.stream.seek(r11.totalFileSize);
                        }
                        r11.readBytesCount += (long) i2;
                        r11.currentPartNum++;
                        r11.currentUploadRequetsCount++;
                        i3 = r11.requestNum;
                        r11.requestNum = i3 + 1;
                        final long j2 = (long) (i5 + i2);
                        i = tLObject.getObjectSize() + 4;
                        final byte[] bArr2 = str;
                        i4 = i3;
                        final TLObject tLObject2 = tLObject;
                        r11.requestTokens.put(i3, ConnectionsManager.getInstance(r11.currentAccount).sendRequest(tLObject, new RequestDelegate() {
                            public void run(TLObject tLObject, TL_error tL_error) {
                                C18034 c18034 = this;
                                TLObject tLObject2 = tLObject;
                                int currentNetworkType = tLObject2 != null ? tLObject2.networkType : ConnectionsManager.getCurrentNetworkType();
                                if (FileUploadOperation.this.currentType == ConnectionsManager.FileTypeAudio) {
                                    StatsController.getInstance(FileUploadOperation.this.currentAccount).incrementSentBytesCount(currentNetworkType, 3, (long) i);
                                } else if (FileUploadOperation.this.currentType == ConnectionsManager.FileTypeVideo) {
                                    StatsController.getInstance(FileUploadOperation.this.currentAccount).incrementSentBytesCount(currentNetworkType, 2, (long) i);
                                } else if (FileUploadOperation.this.currentType == 16777216) {
                                    StatsController.getInstance(FileUploadOperation.this.currentAccount).incrementSentBytesCount(currentNetworkType, 4, (long) i);
                                } else if (FileUploadOperation.this.currentType == ConnectionsManager.FileTypeFile) {
                                    StatsController.getInstance(FileUploadOperation.this.currentAccount).incrementSentBytesCount(currentNetworkType, 5, (long) i);
                                }
                                if (bArr2 != null) {
                                    FileUploadOperation.this.freeRequestIvs.add(bArr2);
                                }
                                FileUploadOperation.this.requestTokens.delete(i4);
                                if (!(tLObject2 instanceof TL_boolTrue)) {
                                    if (tLObject2 != null) {
                                        FileLog.m1e("23123");
                                    }
                                    FileUploadOperation.this.state = 4;
                                    FileUploadOperation.this.delegate.didFailedUploadingFile(FileUploadOperation.this);
                                    FileUploadOperation.this.cleanup();
                                } else if (FileUploadOperation.this.state == 1) {
                                    long max;
                                    FileUploadOperation.this.uploadedBytesCount = FileUploadOperation.this.uploadedBytesCount + ((long) i2);
                                    if (FileUploadOperation.this.estimatedSize != 0) {
                                        max = Math.max(FileUploadOperation.this.availableSize, (long) FileUploadOperation.this.estimatedSize);
                                    } else {
                                        max = FileUploadOperation.this.totalFileSize;
                                    }
                                    FileUploadOperation.this.delegate.didChangedUploadProgress(FileUploadOperation.this, ((float) FileUploadOperation.this.uploadedBytesCount) / ((float) max));
                                    FileUploadOperation.this.currentUploadRequetsCount = FileUploadOperation.this.currentUploadRequetsCount - 1;
                                    if (FileUploadOperation.this.isLastPart && FileUploadOperation.this.currentUploadRequetsCount == 0 && FileUploadOperation.this.state == 1) {
                                        FileUploadOperation.this.state = 3;
                                        if (FileUploadOperation.this.key == null) {
                                            InputFile tL_inputFileBig;
                                            if (FileUploadOperation.this.isBigFile) {
                                                tL_inputFileBig = new TL_inputFileBig();
                                            } else {
                                                tL_inputFileBig = new TL_inputFile();
                                                tL_inputFileBig.md5_checksum = TtmlNode.ANONYMOUS_REGION_ID;
                                            }
                                            tL_inputFileBig.parts = FileUploadOperation.this.currentPartNum;
                                            tL_inputFileBig.id = FileUploadOperation.this.currentFileId;
                                            tL_inputFileBig.name = FileUploadOperation.this.uploadingFilePath.substring(FileUploadOperation.this.uploadingFilePath.lastIndexOf("/") + 1);
                                            FileUploadOperation.this.delegate.didFinishUploadingFile(FileUploadOperation.this, tL_inputFileBig, null, null, null);
                                            FileUploadOperation.this.cleanup();
                                        } else {
                                            InputEncryptedFile tL_inputEncryptedFileBigUploaded;
                                            if (FileUploadOperation.this.isBigFile) {
                                                tL_inputEncryptedFileBigUploaded = new TL_inputEncryptedFileBigUploaded();
                                            } else {
                                                tL_inputEncryptedFileBigUploaded = new TL_inputEncryptedFileUploaded();
                                                tL_inputEncryptedFileBigUploaded.md5_checksum = TtmlNode.ANONYMOUS_REGION_ID;
                                            }
                                            tL_inputEncryptedFileBigUploaded.parts = FileUploadOperation.this.currentPartNum;
                                            tL_inputEncryptedFileBigUploaded.id = FileUploadOperation.this.currentFileId;
                                            tL_inputEncryptedFileBigUploaded.key_fingerprint = FileUploadOperation.this.fingerprint;
                                            FileUploadOperation.this.delegate.didFinishUploadingFile(FileUploadOperation.this, null, tL_inputEncryptedFileBigUploaded, FileUploadOperation.this.key, FileUploadOperation.this.iv);
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
                                            if (i5 == FileUploadOperation.this.lastSavedPartNum) {
                                                FileUploadOperation.this.lastSavedPartNum = FileUploadOperation.this.lastSavedPartNum + 1;
                                                long j = j2;
                                                byte[] bArr = bArr2;
                                                while (true) {
                                                    UploadCachedResult uploadCachedResult = (UploadCachedResult) FileUploadOperation.this.cachedResults.get(FileUploadOperation.this.lastSavedPartNum);
                                                    if (uploadCachedResult == null) {
                                                        break;
                                                    }
                                                    j = uploadCachedResult.bytesOffset;
                                                    bArr = uploadCachedResult.iv;
                                                    FileUploadOperation.this.cachedResults.remove(FileUploadOperation.this.lastSavedPartNum);
                                                    FileUploadOperation.this.lastSavedPartNum = FileUploadOperation.this.lastSavedPartNum + 1;
                                                }
                                                if ((FileUploadOperation.this.isBigFile && j % 1048576 == 0) || (!FileUploadOperation.this.isBigFile && FileUploadOperation.this.saveInfoTimes == 0)) {
                                                    Editor edit = FileUploadOperation.this.preferences.edit();
                                                    StringBuilder stringBuilder = new StringBuilder();
                                                    stringBuilder.append(FileUploadOperation.this.fileKey);
                                                    stringBuilder.append("_uploaded");
                                                    edit.putLong(stringBuilder.toString(), j);
                                                    if (FileUploadOperation.this.isEncrypted) {
                                                        StringBuilder stringBuilder2 = new StringBuilder();
                                                        stringBuilder2.append(FileUploadOperation.this.fileKey);
                                                        stringBuilder2.append("_ivc");
                                                        edit.putString(stringBuilder2.toString(), Utilities.bytesToHex(bArr));
                                                    }
                                                    edit.commit();
                                                }
                                            } else {
                                                UploadCachedResult uploadCachedResult2 = new UploadCachedResult();
                                                uploadCachedResult2.bytesOffset = j2;
                                                if (bArr2 != null) {
                                                    uploadCachedResult2.iv = new byte[32];
                                                    System.arraycopy(bArr2, 0, uploadCachedResult2.iv, 0, 32);
                                                }
                                                FileUploadOperation.this.cachedResults.put(i5, uploadCachedResult2);
                                            }
                                            FileUploadOperation.this.saveInfoTimes = FileUploadOperation.this.saveInfoTimes + 1;
                                        }
                                        FileUploadOperation.this.startUploadRequest();
                                    }
                                }
                            }
                        }, null, new C18045(), 0, ConnectionsManager.DEFAULT_DATACENTER_ID, 4 | ((i3 % 4) << 16), true));
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
