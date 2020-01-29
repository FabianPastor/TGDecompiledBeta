package org.telegram.messenger;

import android.util.SparseArray;
import android.util.SparseIntArray;
import java.io.File;
import java.io.FileInputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.CountDownLatch;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipException;
import org.telegram.messenger.FileLoadOperation;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.NativeByteBuffer;
import org.telegram.tgnet.QuickAckDelegate;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.WriteToSocketDelegate;

public class FileLoadOperation {
    private static final int bigFileSizeFrom = 1048576;
    private static final int cdnChunkCheckSize = 131072;
    private static final int downloadChunkSize = 32768;
    private static final int downloadChunkSizeBig = 131072;
    private static final int maxCdnParts = 12288;
    private static final int maxDownloadRequests = 4;
    private static final int maxDownloadRequestsBig = 4;
    private static final int preloadMaxBytes = 2097152;
    private static final int stateDownloading = 1;
    private static final int stateFailed = 2;
    private static final int stateFinished = 3;
    private static final int stateIdle = 0;
    private boolean allowDisordererFileSave;
    private int bytesCountPadding;
    private File cacheFileFinal;
    private File cacheFileGzipTemp;
    private File cacheFileParts;
    private File cacheFilePreload;
    private File cacheFileTemp;
    private File cacheIvTemp;
    private byte[] cdnCheckBytes;
    private int cdnDatacenterId;
    private SparseArray<TLRPC.TL_fileHash> cdnHashes;
    private byte[] cdnIv;
    private byte[] cdnKey;
    private byte[] cdnToken;
    private int currentAccount;
    private int currentDownloadChunkSize;
    private int currentMaxDownloadRequests;
    private int currentType;
    private int datacenterId;
    private ArrayList<RequestInfo> delayedRequestInfos;
    private FileLoadOperationDelegate delegate;
    private int downloadedBytes;
    private boolean encryptFile;
    private byte[] encryptIv;
    private byte[] encryptKey;
    private String ext;
    private RandomAccessFile fileOutputStream;
    private RandomAccessFile filePartsStream;
    private RandomAccessFile fileReadStream;
    private RandomAccessFile fiv;
    private int foundMoovSize;
    private int initialDatacenterId;
    private boolean isCdn;
    private boolean isForceRequest;
    private boolean isPreloadVideoOperation;
    private byte[] iv;
    private byte[] key;
    protected TLRPC.InputFileLocation location;
    private int moovFound;
    private int nextAtomOffset;
    private boolean nextPartWasPreloaded;
    private int nextPreloadDownloadOffset;
    private ArrayList<Range> notCheckedCdnRanges;
    private ArrayList<Range> notLoadedBytesRanges;
    private volatile ArrayList<Range> notLoadedBytesRangesCopy;
    private ArrayList<Range> notRequestedBytesRanges;
    private Object parentObject;
    private volatile boolean paused;
    private boolean preloadFinished;
    private int preloadNotRequestedBytesCount;
    private RandomAccessFile preloadStream;
    private int preloadStreamFileOffset;
    private byte[] preloadTempBuffer;
    private int preloadTempBufferCount;
    private SparseArray<PreloadRange> preloadedBytesRanges;
    private int priority;
    private RequestInfo priorityRequestInfo;
    private int renameRetryCount;
    private ArrayList<RequestInfo> requestInfos;
    private int requestedBytesCount;
    private SparseIntArray requestedPreloadedBytesRanges;
    private boolean requestingCdnOffsets;
    protected boolean requestingReference;
    private int requestsCount;
    private boolean reuploadingCdn;
    private boolean started;
    private volatile int state;
    private File storePath;
    private ArrayList<FileLoadOperationStream> streamListeners;
    private int streamPriorityStartOffset;
    private int streamStartOffset;
    private boolean supportsPreloading;
    private File tempPath;
    private int totalBytesCount;
    private int totalPreloadedBytes;
    private boolean ungzip;
    private WebFile webFile;
    private TLRPC.InputWebFileLocation webLocation;

    public interface FileLoadOperationDelegate {
        void didChangedLoadProgress(FileLoadOperation fileLoadOperation, long j, long j2);

        void didFailedLoadingFile(FileLoadOperation fileLoadOperation, int i);

        void didFinishLoadingFile(FileLoadOperation fileLoadOperation, File file);
    }

    protected static class RequestInfo {
        /* access modifiers changed from: private */
        public int offset;
        /* access modifiers changed from: private */
        public int requestToken;
        /* access modifiers changed from: private */
        public TLRPC.TL_upload_file response;
        /* access modifiers changed from: private */
        public TLRPC.TL_upload_cdnFile responseCdn;
        /* access modifiers changed from: private */
        public TLRPC.TL_upload_webFile responseWeb;

        protected RequestInfo() {
        }
    }

    public static class Range {
        /* access modifiers changed from: private */
        public int end;
        /* access modifiers changed from: private */
        public int start;

        private Range(int i, int i2) {
            this.start = i;
            this.end = i2;
        }
    }

    private static class PreloadRange {
        /* access modifiers changed from: private */
        public int fileOffset;
        /* access modifiers changed from: private */
        public int length;

        private PreloadRange(int i, int i2) {
            this.fileOffset = i;
            this.length = i2;
        }
    }

    public FileLoadOperation(ImageLocation imageLocation, Object obj, String str, int i) {
        this.preloadTempBuffer = new byte[16];
        boolean z = false;
        this.state = 0;
        this.parentObject = obj;
        if (imageLocation.isEncrypted()) {
            this.location = new TLRPC.TL_inputEncryptedFileLocation();
            TLRPC.InputFileLocation inputFileLocation = this.location;
            TLRPC.TL_fileLocationToBeDeprecated tL_fileLocationToBeDeprecated = imageLocation.location;
            long j = tL_fileLocationToBeDeprecated.volume_id;
            inputFileLocation.id = j;
            inputFileLocation.volume_id = j;
            inputFileLocation.local_id = tL_fileLocationToBeDeprecated.local_id;
            inputFileLocation.access_hash = imageLocation.access_hash;
            this.iv = new byte[32];
            byte[] bArr = imageLocation.iv;
            byte[] bArr2 = this.iv;
            System.arraycopy(bArr, 0, bArr2, 0, bArr2.length);
            this.key = imageLocation.key;
        } else if (imageLocation.photoPeer != null) {
            this.location = new TLRPC.TL_inputPeerPhotoFileLocation();
            TLRPC.InputFileLocation inputFileLocation2 = this.location;
            TLRPC.TL_fileLocationToBeDeprecated tL_fileLocationToBeDeprecated2 = imageLocation.location;
            long j2 = tL_fileLocationToBeDeprecated2.volume_id;
            inputFileLocation2.id = j2;
            inputFileLocation2.volume_id = j2;
            inputFileLocation2.local_id = tL_fileLocationToBeDeprecated2.local_id;
            inputFileLocation2.big = imageLocation.photoPeerBig;
            inputFileLocation2.peer = imageLocation.photoPeer;
        } else if (imageLocation.stickerSet != null) {
            this.location = new TLRPC.TL_inputStickerSetThumb();
            TLRPC.InputFileLocation inputFileLocation3 = this.location;
            TLRPC.TL_fileLocationToBeDeprecated tL_fileLocationToBeDeprecated3 = imageLocation.location;
            long j3 = tL_fileLocationToBeDeprecated3.volume_id;
            inputFileLocation3.id = j3;
            inputFileLocation3.volume_id = j3;
            inputFileLocation3.local_id = tL_fileLocationToBeDeprecated3.local_id;
            inputFileLocation3.stickerset = imageLocation.stickerSet;
        } else if (imageLocation.thumbSize != null) {
            if (imageLocation.photoId != 0) {
                this.location = new TLRPC.TL_inputPhotoFileLocation();
                TLRPC.InputFileLocation inputFileLocation4 = this.location;
                inputFileLocation4.id = imageLocation.photoId;
                TLRPC.TL_fileLocationToBeDeprecated tL_fileLocationToBeDeprecated4 = imageLocation.location;
                inputFileLocation4.volume_id = tL_fileLocationToBeDeprecated4.volume_id;
                inputFileLocation4.local_id = tL_fileLocationToBeDeprecated4.local_id;
                inputFileLocation4.access_hash = imageLocation.access_hash;
                inputFileLocation4.file_reference = imageLocation.file_reference;
                inputFileLocation4.thumb_size = imageLocation.thumbSize;
            } else {
                this.location = new TLRPC.TL_inputDocumentFileLocation();
                TLRPC.InputFileLocation inputFileLocation5 = this.location;
                inputFileLocation5.id = imageLocation.documentId;
                TLRPC.TL_fileLocationToBeDeprecated tL_fileLocationToBeDeprecated5 = imageLocation.location;
                inputFileLocation5.volume_id = tL_fileLocationToBeDeprecated5.volume_id;
                inputFileLocation5.local_id = tL_fileLocationToBeDeprecated5.local_id;
                inputFileLocation5.access_hash = imageLocation.access_hash;
                inputFileLocation5.file_reference = imageLocation.file_reference;
                inputFileLocation5.thumb_size = imageLocation.thumbSize;
            }
            TLRPC.InputFileLocation inputFileLocation6 = this.location;
            if (inputFileLocation6.file_reference == null) {
                inputFileLocation6.file_reference = new byte[0];
            }
        } else {
            this.location = new TLRPC.TL_inputFileLocation();
            TLRPC.InputFileLocation inputFileLocation7 = this.location;
            TLRPC.TL_fileLocationToBeDeprecated tL_fileLocationToBeDeprecated6 = imageLocation.location;
            inputFileLocation7.volume_id = tL_fileLocationToBeDeprecated6.volume_id;
            inputFileLocation7.local_id = tL_fileLocationToBeDeprecated6.local_id;
            inputFileLocation7.secret = imageLocation.access_hash;
            inputFileLocation7.file_reference = imageLocation.file_reference;
            if (inputFileLocation7.file_reference == null) {
                inputFileLocation7.file_reference = new byte[0];
            }
            this.allowDisordererFileSave = true;
        }
        int i2 = imageLocation.imageType;
        this.ungzip = (i2 == 1 || i2 == 3) ? true : z;
        int i3 = imageLocation.dc_id;
        this.datacenterId = i3;
        this.initialDatacenterId = i3;
        this.currentType = 16777216;
        this.totalBytesCount = i;
        this.ext = str == null ? "jpg" : str;
    }

    public FileLoadOperation(SecureDocument secureDocument) {
        this.preloadTempBuffer = new byte[16];
        this.state = 0;
        this.location = new TLRPC.TL_inputSecureFileLocation();
        TLRPC.InputFileLocation inputFileLocation = this.location;
        TLRPC.TL_secureFile tL_secureFile = secureDocument.secureFile;
        inputFileLocation.id = tL_secureFile.id;
        inputFileLocation.access_hash = tL_secureFile.access_hash;
        this.datacenterId = tL_secureFile.dc_id;
        this.totalBytesCount = tL_secureFile.size;
        this.allowDisordererFileSave = true;
        this.currentType = 67108864;
        this.ext = ".jpg";
    }

    public FileLoadOperation(int i, WebFile webFile2) {
        this.preloadTempBuffer = new byte[16];
        this.state = 0;
        this.currentAccount = i;
        this.webFile = webFile2;
        this.webLocation = webFile2.location;
        this.totalBytesCount = webFile2.size;
        int i2 = MessagesController.getInstance(this.currentAccount).webFileDatacenterId;
        this.datacenterId = i2;
        this.initialDatacenterId = i2;
        String mimeTypePart = FileLoader.getMimeTypePart(webFile2.mime_type);
        if (webFile2.mime_type.startsWith("image/")) {
            this.currentType = 16777216;
        } else if (webFile2.mime_type.equals("audio/ogg")) {
            this.currentType = 50331648;
        } else if (webFile2.mime_type.startsWith("video/")) {
            this.currentType = 33554432;
        } else {
            this.currentType = 67108864;
        }
        this.allowDisordererFileSave = true;
        this.ext = ImageLoader.getHttpUrlExtension(webFile2.url, mimeTypePart);
    }

    /* JADX WARNING: Removed duplicated region for block: B:32:0x00cf A[Catch:{ Exception -> 0x0117 }] */
    /* JADX WARNING: Removed duplicated region for block: B:39:0x00f0 A[Catch:{ Exception -> 0x0117 }] */
    /* JADX WARNING: Removed duplicated region for block: B:40:0x00f5 A[Catch:{ Exception -> 0x0117 }] */
    /* JADX WARNING: Removed duplicated region for block: B:46:0x010e A[Catch:{ Exception -> 0x0117 }] */
    /* JADX WARNING: Removed duplicated region for block: B:52:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public FileLoadOperation(org.telegram.tgnet.TLRPC.Document r7, java.lang.Object r8) {
        /*
            r6 = this;
            r6.<init>()
            r0 = 16
            byte[] r1 = new byte[r0]
            r6.preloadTempBuffer = r1
            r1 = 0
            r6.state = r1
            r2 = 1
            r6.parentObject = r8     // Catch:{ Exception -> 0x0117 }
            boolean r8 = r7 instanceof org.telegram.tgnet.TLRPC.TL_documentEncrypted     // Catch:{ Exception -> 0x0117 }
            java.lang.String r3 = ""
            if (r8 == 0) goto L_0x0043
            org.telegram.tgnet.TLRPC$TL_inputEncryptedFileLocation r8 = new org.telegram.tgnet.TLRPC$TL_inputEncryptedFileLocation     // Catch:{ Exception -> 0x0117 }
            r8.<init>()     // Catch:{ Exception -> 0x0117 }
            r6.location = r8     // Catch:{ Exception -> 0x0117 }
            org.telegram.tgnet.TLRPC$InputFileLocation r8 = r6.location     // Catch:{ Exception -> 0x0117 }
            long r4 = r7.id     // Catch:{ Exception -> 0x0117 }
            r8.id = r4     // Catch:{ Exception -> 0x0117 }
            org.telegram.tgnet.TLRPC$InputFileLocation r8 = r6.location     // Catch:{ Exception -> 0x0117 }
            long r4 = r7.access_hash     // Catch:{ Exception -> 0x0117 }
            r8.access_hash = r4     // Catch:{ Exception -> 0x0117 }
            int r8 = r7.dc_id     // Catch:{ Exception -> 0x0117 }
            r6.datacenterId = r8     // Catch:{ Exception -> 0x0117 }
            r6.initialDatacenterId = r8     // Catch:{ Exception -> 0x0117 }
            r8 = 32
            byte[] r8 = new byte[r8]     // Catch:{ Exception -> 0x0117 }
            r6.iv = r8     // Catch:{ Exception -> 0x0117 }
            byte[] r8 = r7.iv     // Catch:{ Exception -> 0x0117 }
            byte[] r4 = r6.iv     // Catch:{ Exception -> 0x0117 }
            byte[] r5 = r6.iv     // Catch:{ Exception -> 0x0117 }
            int r5 = r5.length     // Catch:{ Exception -> 0x0117 }
            java.lang.System.arraycopy(r8, r1, r4, r1, r5)     // Catch:{ Exception -> 0x0117 }
            byte[] r8 = r7.key     // Catch:{ Exception -> 0x0117 }
            r6.key = r8     // Catch:{ Exception -> 0x0117 }
            goto L_0x0091
        L_0x0043:
            boolean r8 = r7 instanceof org.telegram.tgnet.TLRPC.TL_document     // Catch:{ Exception -> 0x0117 }
            if (r8 == 0) goto L_0x0091
            org.telegram.tgnet.TLRPC$TL_inputDocumentFileLocation r8 = new org.telegram.tgnet.TLRPC$TL_inputDocumentFileLocation     // Catch:{ Exception -> 0x0117 }
            r8.<init>()     // Catch:{ Exception -> 0x0117 }
            r6.location = r8     // Catch:{ Exception -> 0x0117 }
            org.telegram.tgnet.TLRPC$InputFileLocation r8 = r6.location     // Catch:{ Exception -> 0x0117 }
            long r4 = r7.id     // Catch:{ Exception -> 0x0117 }
            r8.id = r4     // Catch:{ Exception -> 0x0117 }
            org.telegram.tgnet.TLRPC$InputFileLocation r8 = r6.location     // Catch:{ Exception -> 0x0117 }
            long r4 = r7.access_hash     // Catch:{ Exception -> 0x0117 }
            r8.access_hash = r4     // Catch:{ Exception -> 0x0117 }
            org.telegram.tgnet.TLRPC$InputFileLocation r8 = r6.location     // Catch:{ Exception -> 0x0117 }
            byte[] r4 = r7.file_reference     // Catch:{ Exception -> 0x0117 }
            r8.file_reference = r4     // Catch:{ Exception -> 0x0117 }
            org.telegram.tgnet.TLRPC$InputFileLocation r8 = r6.location     // Catch:{ Exception -> 0x0117 }
            r8.thumb_size = r3     // Catch:{ Exception -> 0x0117 }
            org.telegram.tgnet.TLRPC$InputFileLocation r8 = r6.location     // Catch:{ Exception -> 0x0117 }
            byte[] r8 = r8.file_reference     // Catch:{ Exception -> 0x0117 }
            if (r8 != 0) goto L_0x0070
            org.telegram.tgnet.TLRPC$InputFileLocation r8 = r6.location     // Catch:{ Exception -> 0x0117 }
            byte[] r4 = new byte[r1]     // Catch:{ Exception -> 0x0117 }
            r8.file_reference = r4     // Catch:{ Exception -> 0x0117 }
        L_0x0070:
            int r8 = r7.dc_id     // Catch:{ Exception -> 0x0117 }
            r6.datacenterId = r8     // Catch:{ Exception -> 0x0117 }
            r6.initialDatacenterId = r8     // Catch:{ Exception -> 0x0117 }
            r6.allowDisordererFileSave = r2     // Catch:{ Exception -> 0x0117 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r8 = r7.attributes     // Catch:{ Exception -> 0x0117 }
            int r8 = r8.size()     // Catch:{ Exception -> 0x0117 }
            r4 = 0
        L_0x007f:
            if (r4 >= r8) goto L_0x0091
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r5 = r7.attributes     // Catch:{ Exception -> 0x0117 }
            java.lang.Object r5 = r5.get(r4)     // Catch:{ Exception -> 0x0117 }
            boolean r5 = r5 instanceof org.telegram.tgnet.TLRPC.TL_documentAttributeVideo     // Catch:{ Exception -> 0x0117 }
            if (r5 == 0) goto L_0x008e
            r6.supportsPreloading = r2     // Catch:{ Exception -> 0x0117 }
            goto L_0x0091
        L_0x008e:
            int r4 = r4 + 1
            goto L_0x007f
        L_0x0091:
            java.lang.String r8 = "application/x-tgsticker"
            java.lang.String r4 = r7.mime_type     // Catch:{ Exception -> 0x0117 }
            boolean r8 = r8.equals(r4)     // Catch:{ Exception -> 0x0117 }
            if (r8 != 0) goto L_0x00a8
            java.lang.String r8 = "application/x-tgwallpattern"
            java.lang.String r4 = r7.mime_type     // Catch:{ Exception -> 0x0117 }
            boolean r8 = r8.equals(r4)     // Catch:{ Exception -> 0x0117 }
            if (r8 == 0) goto L_0x00a6
            goto L_0x00a8
        L_0x00a6:
            r8 = 0
            goto L_0x00a9
        L_0x00a8:
            r8 = 1
        L_0x00a9:
            r6.ungzip = r8     // Catch:{ Exception -> 0x0117 }
            int r8 = r7.size     // Catch:{ Exception -> 0x0117 }
            r6.totalBytesCount = r8     // Catch:{ Exception -> 0x0117 }
            byte[] r8 = r6.key     // Catch:{ Exception -> 0x0117 }
            if (r8 == 0) goto L_0x00c5
            int r8 = r6.totalBytesCount     // Catch:{ Exception -> 0x0117 }
            int r8 = r8 % r0
            if (r8 == 0) goto L_0x00c5
            int r8 = r6.totalBytesCount     // Catch:{ Exception -> 0x0117 }
            int r8 = r8 % r0
            int r0 = r0 - r8
            r6.bytesCountPadding = r0     // Catch:{ Exception -> 0x0117 }
            int r8 = r6.totalBytesCount     // Catch:{ Exception -> 0x0117 }
            int r0 = r6.bytesCountPadding     // Catch:{ Exception -> 0x0117 }
            int r8 = r8 + r0
            r6.totalBytesCount = r8     // Catch:{ Exception -> 0x0117 }
        L_0x00c5:
            java.lang.String r8 = org.telegram.messenger.FileLoader.getDocumentFileName(r7)     // Catch:{ Exception -> 0x0117 }
            r6.ext = r8     // Catch:{ Exception -> 0x0117 }
            java.lang.String r8 = r6.ext     // Catch:{ Exception -> 0x0117 }
            if (r8 == 0) goto L_0x00e4
            java.lang.String r8 = r6.ext     // Catch:{ Exception -> 0x0117 }
            r0 = 46
            int r8 = r8.lastIndexOf(r0)     // Catch:{ Exception -> 0x0117 }
            r0 = -1
            if (r8 != r0) goto L_0x00db
            goto L_0x00e4
        L_0x00db:
            java.lang.String r0 = r6.ext     // Catch:{ Exception -> 0x0117 }
            java.lang.String r8 = r0.substring(r8)     // Catch:{ Exception -> 0x0117 }
            r6.ext = r8     // Catch:{ Exception -> 0x0117 }
            goto L_0x00e6
        L_0x00e4:
            r6.ext = r3     // Catch:{ Exception -> 0x0117 }
        L_0x00e6:
            java.lang.String r8 = "audio/ogg"
            java.lang.String r0 = r7.mime_type     // Catch:{ Exception -> 0x0117 }
            boolean r8 = r8.equals(r0)     // Catch:{ Exception -> 0x0117 }
            if (r8 == 0) goto L_0x00f5
            r8 = 50331648(0x3000000, float:3.761582E-37)
            r6.currentType = r8     // Catch:{ Exception -> 0x0117 }
            goto L_0x0106
        L_0x00f5:
            java.lang.String r8 = r7.mime_type     // Catch:{ Exception -> 0x0117 }
            boolean r8 = org.telegram.messenger.FileLoader.isVideoMimeType(r8)     // Catch:{ Exception -> 0x0117 }
            if (r8 == 0) goto L_0x0102
            r8 = 33554432(0x2000000, float:9.403955E-38)
            r6.currentType = r8     // Catch:{ Exception -> 0x0117 }
            goto L_0x0106
        L_0x0102:
            r8 = 67108864(0x4000000, float:1.5046328E-36)
            r6.currentType = r8     // Catch:{ Exception -> 0x0117 }
        L_0x0106:
            java.lang.String r8 = r6.ext     // Catch:{ Exception -> 0x0117 }
            int r8 = r8.length()     // Catch:{ Exception -> 0x0117 }
            if (r8 > r2) goto L_0x011e
            java.lang.String r7 = r7.mime_type     // Catch:{ Exception -> 0x0117 }
            java.lang.String r7 = org.telegram.messenger.FileLoader.getExtensionByMimeType(r7)     // Catch:{ Exception -> 0x0117 }
            r6.ext = r7     // Catch:{ Exception -> 0x0117 }
            goto L_0x011e
        L_0x0117:
            r7 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r7)
            r6.onFail(r2, r1)
        L_0x011e:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.FileLoadOperation.<init>(org.telegram.tgnet.TLRPC$Document, java.lang.Object):void");
    }

    public void setEncryptFile(boolean z) {
        this.encryptFile = z;
        if (this.encryptFile) {
            this.allowDisordererFileSave = false;
        }
    }

    public int getDatacenterId() {
        return this.initialDatacenterId;
    }

    public void setForceRequest(boolean z) {
        this.isForceRequest = z;
    }

    public boolean isForceRequest() {
        return this.isForceRequest;
    }

    public void setPriority(int i) {
        this.priority = i;
    }

    public int getPriority() {
        return this.priority;
    }

    public void setPaths(int i, File file, File file2) {
        this.storePath = file;
        this.tempPath = file2;
        this.currentAccount = i;
    }

    public boolean wasStarted() {
        return this.started && !this.paused;
    }

    public int getCurrentType() {
        return this.currentType;
    }

    private void removePart(ArrayList<Range> arrayList, int i, int i2) {
        boolean z;
        if (arrayList != null && i2 >= i) {
            int size = arrayList.size();
            int i3 = 0;
            int i4 = 0;
            while (i4 < size) {
                Range range = arrayList.get(i4);
                if (i == range.end) {
                    int unused = range.end = i2;
                } else if (i2 == range.start) {
                    int unused2 = range.start = i;
                } else {
                    i4++;
                }
                z = true;
            }
            z = false;
            Collections.sort(arrayList, $$Lambda$FileLoadOperation$o7auaauybhlDahYpH2ZCMglGb9Q.INSTANCE);
            while (i3 < arrayList.size() - 1) {
                Range range2 = arrayList.get(i3);
                int i5 = i3 + 1;
                Range range3 = arrayList.get(i5);
                if (range2.end == range3.start) {
                    int unused3 = range2.end = range3.end;
                    arrayList.remove(i5);
                    i3--;
                }
                i3++;
            }
            if (!z) {
                arrayList.add(new Range(i, i2));
            }
        }
    }

    static /* synthetic */ int lambda$removePart$0(Range range, Range range2) {
        if (range.start > range2.start) {
            return 1;
        }
        return range.start < range2.start ? -1 : 0;
    }

    private void addPart(ArrayList<Range> arrayList, int i, int i2, boolean z) {
        boolean z2;
        if (arrayList != null && i2 >= i) {
            int size = arrayList.size();
            int i3 = 0;
            while (true) {
                z2 = true;
                if (i3 >= size) {
                    z2 = false;
                    break;
                }
                Range range = arrayList.get(i3);
                if (i <= range.start) {
                    if (i2 >= range.end) {
                        arrayList.remove(i3);
                        break;
                    } else if (i2 > range.start) {
                        int unused = range.start = i2;
                        break;
                    }
                } else if (i2 < range.end) {
                    arrayList.add(0, new Range(range.start, i));
                    int unused2 = range.start = i2;
                    break;
                } else if (i < range.end) {
                    int unused3 = range.end = i;
                    break;
                }
                i3++;
            }
            if (!z) {
                return;
            }
            if (z2) {
                try {
                    this.filePartsStream.seek(0);
                    int size2 = arrayList.size();
                    this.filePartsStream.writeInt(size2);
                    for (int i4 = 0; i4 < size2; i4++) {
                        Range range2 = arrayList.get(i4);
                        this.filePartsStream.writeInt(range2.start);
                        this.filePartsStream.writeInt(range2.end);
                    }
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
                ArrayList<FileLoadOperationStream> arrayList2 = this.streamListeners;
                if (arrayList2 != null) {
                    int size3 = arrayList2.size();
                    for (int i5 = 0; i5 < size3; i5++) {
                        this.streamListeners.get(i5).newDataAvailable();
                    }
                }
            } else if (BuildVars.LOGS_ENABLED) {
                FileLog.e(this.cacheFileFinal + " downloaded duplicate file part " + i + " - " + i2);
            }
        }
    }

    /* access modifiers changed from: protected */
    public File getCurrentFile() {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        File[] fileArr = new File[1];
        Utilities.stageQueue.postRunnable(new Runnable(fileArr, countDownLatch) {
            private final /* synthetic */ File[] f$1;
            private final /* synthetic */ CountDownLatch f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                FileLoadOperation.this.lambda$getCurrentFile$1$FileLoadOperation(this.f$1, this.f$2);
            }
        });
        try {
            countDownLatch.await();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        return fileArr[0];
    }

    public /* synthetic */ void lambda$getCurrentFile$1$FileLoadOperation(File[] fileArr, CountDownLatch countDownLatch) {
        if (this.state == 3) {
            fileArr[0] = this.cacheFileFinal;
        } else {
            fileArr[0] = this.cacheFileTemp;
        }
        countDownLatch.countDown();
    }

    private int getDownloadedLengthFromOffsetInternal(ArrayList<Range> arrayList, int i, int i2) {
        int i3;
        if (arrayList == null || this.state == 3 || arrayList.isEmpty()) {
            int i4 = this.downloadedBytes;
            if (i4 == 0) {
                return i2;
            }
            return Math.min(i2, Math.max(i4 - i, 0));
        }
        int size = arrayList.size();
        Range range = null;
        int i5 = 0;
        while (true) {
            if (i5 >= size) {
                i3 = i2;
                break;
            }
            Range range2 = arrayList.get(i5);
            if (i <= range2.start && (range == null || range2.start < range.start)) {
                range = range2;
            }
            if (range2.start <= i && range2.end > i) {
                i3 = 0;
                break;
            }
            i5++;
        }
        if (i3 == 0) {
            return 0;
        }
        if (range != null) {
            return Math.min(i2, range.start - i);
        }
        return Math.min(i2, Math.max(this.totalBytesCount - i, 0));
    }

    /* access modifiers changed from: protected */
    public float getDownloadedLengthFromOffset(float f) {
        ArrayList<Range> arrayList = this.notLoadedBytesRangesCopy;
        int i = this.totalBytesCount;
        if (i == 0 || arrayList == null) {
            return 0.0f;
        }
        return f + (((float) getDownloadedLengthFromOffsetInternal(arrayList, (int) (((float) i) * f), i)) / ((float) this.totalBytesCount));
    }

    /* access modifiers changed from: protected */
    public int getDownloadedLengthFromOffset(int i, int i2) {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        int[] iArr = new int[1];
        Utilities.stageQueue.postRunnable(new Runnable(iArr, i, i2, countDownLatch) {
            private final /* synthetic */ int[] f$1;
            private final /* synthetic */ int f$2;
            private final /* synthetic */ int f$3;
            private final /* synthetic */ CountDownLatch f$4;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
            }

            public final void run() {
                FileLoadOperation.this.lambda$getDownloadedLengthFromOffset$2$FileLoadOperation(this.f$1, this.f$2, this.f$3, this.f$4);
            }
        });
        try {
            countDownLatch.await();
        } catch (Exception unused) {
        }
        return iArr[0];
    }

    public /* synthetic */ void lambda$getDownloadedLengthFromOffset$2$FileLoadOperation(int[] iArr, int i, int i2, CountDownLatch countDownLatch) {
        iArr[0] = getDownloadedLengthFromOffsetInternal(this.notLoadedBytesRanges, i, i2);
        countDownLatch.countDown();
    }

    public String getFileName() {
        if (this.location != null) {
            return this.location.volume_id + "_" + this.location.local_id + "." + this.ext;
        }
        return Utilities.MD5(this.webFile.url) + "." + this.ext;
    }

    /* access modifiers changed from: protected */
    public void removeStreamListener(FileLoadOperationStream fileLoadOperationStream) {
        Utilities.stageQueue.postRunnable(new Runnable(fileLoadOperationStream) {
            private final /* synthetic */ FileLoadOperationStream f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                FileLoadOperation.this.lambda$removeStreamListener$3$FileLoadOperation(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$removeStreamListener$3$FileLoadOperation(FileLoadOperationStream fileLoadOperationStream) {
        ArrayList<FileLoadOperationStream> arrayList = this.streamListeners;
        if (arrayList != null) {
            arrayList.remove(fileLoadOperationStream);
        }
    }

    private void copyNotLoadedRanges() {
        ArrayList<Range> arrayList = this.notLoadedBytesRanges;
        if (arrayList != null) {
            this.notLoadedBytesRangesCopy = new ArrayList<>(arrayList);
        }
    }

    public void pause() {
        if (this.state == 1) {
            this.paused = true;
        }
    }

    public boolean start() {
        return start((FileLoadOperationStream) null, 0, false);
    }

    /* JADX WARNING: Removed duplicated region for block: B:132:0x047e A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:191:0x058e A[SYNTHETIC, Splitter:B:191:0x058e] */
    /* JADX WARNING: Removed duplicated region for block: B:201:0x05b0  */
    /* JADX WARNING: Removed duplicated region for block: B:215:0x0614  */
    /* JADX WARNING: Removed duplicated region for block: B:228:0x066b  */
    /* JADX WARNING: Removed duplicated region for block: B:235:0x0692  */
    /* JADX WARNING: Removed duplicated region for block: B:241:0x06be  */
    /* JADX WARNING: Removed duplicated region for block: B:246:0x06fb  */
    /* JADX WARNING: Removed duplicated region for block: B:269:0x0766 A[Catch:{ Exception -> 0x076f }] */
    /* JADX WARNING: Removed duplicated region for block: B:274:0x0777  */
    /* JADX WARNING: Removed duplicated region for block: B:276:0x077b  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean start(org.telegram.messenger.FileLoadOperationStream r23, int r24, boolean r25) {
        /*
            r22 = this;
            r7 = r22
            int r0 = r7.currentDownloadChunkSize
            if (r0 != 0) goto L_0x001b
            int r0 = r7.totalBytesCount
            r1 = 1048576(0x100000, float:1.469368E-39)
            if (r0 < r1) goto L_0x000f
            r0 = 131072(0x20000, float:1.83671E-40)
            goto L_0x0012
        L_0x000f:
            r0 = 32768(0x8000, float:4.5918E-41)
        L_0x0012:
            r7.currentDownloadChunkSize = r0
            int r0 = r7.totalBytesCount
            r1 = 1048576(0x100000, float:1.469368E-39)
            r0 = 4
            r7.currentMaxDownloadRequests = r0
        L_0x001b:
            int r0 = r7.state
            r8 = 1
            r9 = 0
            if (r0 == 0) goto L_0x0023
            r0 = 1
            goto L_0x0024
        L_0x0023:
            r0 = 0
        L_0x0024:
            boolean r10 = r7.paused
            r7.paused = r9
            if (r23 == 0) goto L_0x003f
            org.telegram.messenger.DispatchQueue r11 = org.telegram.messenger.Utilities.stageQueue
            org.telegram.messenger.-$$Lambda$FileLoadOperation$ueOLhTDJvcCHBhZ6A3pTvCfLNBQ r12 = new org.telegram.messenger.-$$Lambda$FileLoadOperation$ueOLhTDJvcCHBhZ6A3pTvCfLNBQ
            r1 = r12
            r2 = r22
            r3 = r25
            r4 = r24
            r5 = r23
            r6 = r0
            r1.<init>(r3, r4, r5, r6)
            r11.postRunnable(r12)
            goto L_0x004d
        L_0x003f:
            if (r10 == 0) goto L_0x004d
            if (r0 == 0) goto L_0x004d
            org.telegram.messenger.DispatchQueue r1 = org.telegram.messenger.Utilities.stageQueue
            org.telegram.messenger.-$$Lambda$HfxdqGkDOxPIykHiS0VM1dK9UjM r2 = new org.telegram.messenger.-$$Lambda$HfxdqGkDOxPIykHiS0VM1dK9UjM
            r2.<init>()
            r1.postRunnable(r2)
        L_0x004d:
            if (r0 == 0) goto L_0x0050
            return r10
        L_0x0050:
            org.telegram.tgnet.TLRPC$InputFileLocation r0 = r7.location
            if (r0 != 0) goto L_0x005c
            org.telegram.tgnet.TLRPC$InputWebFileLocation r0 = r7.webLocation
            if (r0 != 0) goto L_0x005c
            r7.onFail(r8, r9)
            return r9
        L_0x005c:
            int r0 = r7.currentDownloadChunkSize
            int r1 = r24 / r0
            int r1 = r1 * r0
            r7.streamStartOffset = r1
            boolean r1 = r7.allowDisordererFileSave
            if (r1 == 0) goto L_0x007c
            int r1 = r7.totalBytesCount
            if (r1 <= 0) goto L_0x007c
            if (r1 <= r0) goto L_0x007c
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r7.notLoadedBytesRanges = r0
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r7.notRequestedBytesRanges = r0
        L_0x007c:
            org.telegram.tgnet.TLRPC$InputWebFileLocation r0 = r7.webLocation
            java.lang.String r1 = ".iv.enc"
            java.lang.String r2 = ".iv"
            java.lang.String r3 = ".enc"
            java.lang.String r4 = ".temp.enc"
            java.lang.String r5 = ".temp"
            java.lang.String r6 = "."
            r10 = 0
            if (r0 == 0) goto L_0x0118
            org.telegram.messenger.WebFile r0 = r7.webFile
            java.lang.String r0 = r0.url
            java.lang.String r0 = org.telegram.messenger.Utilities.MD5(r0)
            boolean r13 = r7.encryptFile
            if (r13 == 0) goto L_0x00d7
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r0)
            r2.append(r4)
            java.lang.String r2 = r2.toString()
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r0)
            r4.append(r6)
            java.lang.String r5 = r7.ext
            r4.append(r5)
            r4.append(r3)
            java.lang.String r3 = r4.toString()
            byte[] r4 = r7.key
            if (r4 == 0) goto L_0x00d5
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r0)
            r4.append(r1)
            java.lang.String r0 = r4.toString()
            r1 = r2
            goto L_0x010f
        L_0x00d5:
            r1 = r2
            goto L_0x010e
        L_0x00d7:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r0)
            r1.append(r5)
            java.lang.String r1 = r1.toString()
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r0)
            r3.append(r6)
            java.lang.String r4 = r7.ext
            r3.append(r4)
            java.lang.String r3 = r3.toString()
            byte[] r4 = r7.key
            if (r4 == 0) goto L_0x010e
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r0)
            r4.append(r2)
            java.lang.String r0 = r4.toString()
            goto L_0x010f
        L_0x010e:
            r0 = 0
        L_0x010f:
            r2 = 0
            r12 = 0
            r21 = r1
            r1 = r0
            r0 = r21
            goto L_0x035e
        L_0x0118:
            org.telegram.tgnet.TLRPC$InputFileLocation r0 = r7.location
            long r13 = r0.volume_id
            java.lang.String r15 = "_"
            int r16 = (r13 > r10 ? 1 : (r13 == r10 ? 0 : -1))
            if (r16 == 0) goto L_0x024c
            int r0 = r0.local_id
            if (r0 == 0) goto L_0x024c
            int r0 = r7.datacenterId
            r12 = -2147483648(0xfffffffvar_, float:-0.0)
            if (r0 == r12) goto L_0x0248
            r16 = -2147483648(0xfffffffvar_, double:NaN)
            int r12 = (r13 > r16 ? 1 : (r13 == r16 ? 0 : -1))
            if (r12 == 0) goto L_0x0248
            if (r0 != 0) goto L_0x0137
            goto L_0x0248
        L_0x0137:
            boolean r0 = r7.encryptFile
            if (r0 == 0) goto L_0x01a0
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            org.telegram.tgnet.TLRPC$InputFileLocation r2 = r7.location
            long r12 = r2.volume_id
            r0.append(r12)
            r0.append(r15)
            org.telegram.tgnet.TLRPC$InputFileLocation r2 = r7.location
            int r2 = r2.local_id
            r0.append(r2)
            r0.append(r4)
            java.lang.String r0 = r0.toString()
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            org.telegram.tgnet.TLRPC$InputFileLocation r4 = r7.location
            long r4 = r4.volume_id
            r2.append(r4)
            r2.append(r15)
            org.telegram.tgnet.TLRPC$InputFileLocation r4 = r7.location
            int r4 = r4.local_id
            r2.append(r4)
            r2.append(r6)
            java.lang.String r4 = r7.ext
            r2.append(r4)
            r2.append(r3)
            java.lang.String r3 = r2.toString()
            byte[] r2 = r7.key
            if (r2 == 0) goto L_0x02ba
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            org.telegram.tgnet.TLRPC$InputFileLocation r4 = r7.location
            long r4 = r4.volume_id
            r2.append(r4)
            r2.append(r15)
            org.telegram.tgnet.TLRPC$InputFileLocation r4 = r7.location
            int r4 = r4.local_id
            r2.append(r4)
            r2.append(r1)
            java.lang.String r12 = r2.toString()
            goto L_0x02b8
        L_0x01a0:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            org.telegram.tgnet.TLRPC$InputFileLocation r1 = r7.location
            long r3 = r1.volume_id
            r0.append(r3)
            r0.append(r15)
            org.telegram.tgnet.TLRPC$InputFileLocation r1 = r7.location
            int r1 = r1.local_id
            r0.append(r1)
            r0.append(r5)
            java.lang.String r1 = r0.toString()
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            org.telegram.tgnet.TLRPC$InputFileLocation r3 = r7.location
            long r3 = r3.volume_id
            r0.append(r3)
            r0.append(r15)
            org.telegram.tgnet.TLRPC$InputFileLocation r3 = r7.location
            int r3 = r3.local_id
            r0.append(r3)
            r0.append(r6)
            java.lang.String r3 = r7.ext
            r0.append(r3)
            java.lang.String r3 = r0.toString()
            byte[] r0 = r7.key
            if (r0 == 0) goto L_0x0201
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            org.telegram.tgnet.TLRPC$InputFileLocation r4 = r7.location
            long r4 = r4.volume_id
            r0.append(r4)
            r0.append(r15)
            org.telegram.tgnet.TLRPC$InputFileLocation r4 = r7.location
            int r4 = r4.local_id
            r0.append(r4)
            r0.append(r2)
            java.lang.String r12 = r0.toString()
            goto L_0x0202
        L_0x0201:
            r12 = 0
        L_0x0202:
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$Range> r0 = r7.notLoadedBytesRanges
            if (r0 == 0) goto L_0x0226
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            org.telegram.tgnet.TLRPC$InputFileLocation r2 = r7.location
            long r4 = r2.volume_id
            r0.append(r4)
            r0.append(r15)
            org.telegram.tgnet.TLRPC$InputFileLocation r2 = r7.location
            int r2 = r2.local_id
            r0.append(r2)
            java.lang.String r2 = ".pt"
            r0.append(r2)
            java.lang.String r0 = r0.toString()
            goto L_0x0227
        L_0x0226:
            r0 = 0
        L_0x0227:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            org.telegram.tgnet.TLRPC$InputFileLocation r4 = r7.location
            long r4 = r4.volume_id
            r2.append(r4)
            r2.append(r15)
            org.telegram.tgnet.TLRPC$InputFileLocation r4 = r7.location
            int r4 = r4.local_id
            r2.append(r4)
            java.lang.String r4 = ".preload"
            r2.append(r4)
            java.lang.String r2 = r2.toString()
            goto L_0x0358
        L_0x0248:
            r7.onFail(r8, r9)
            return r9
        L_0x024c:
            int r0 = r7.datacenterId
            if (r0 == 0) goto L_0x0792
            org.telegram.tgnet.TLRPC$InputFileLocation r0 = r7.location
            long r12 = r0.id
            int r0 = (r12 > r10 ? 1 : (r12 == r10 ? 0 : -1))
            if (r0 != 0) goto L_0x025a
            goto L_0x0792
        L_0x025a:
            boolean r0 = r7.encryptFile
            if (r0 == 0) goto L_0x02bf
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            int r2 = r7.datacenterId
            r0.append(r2)
            r0.append(r15)
            org.telegram.tgnet.TLRPC$InputFileLocation r2 = r7.location
            long r5 = r2.id
            r0.append(r5)
            r0.append(r4)
            java.lang.String r0 = r0.toString()
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            int r4 = r7.datacenterId
            r2.append(r4)
            r2.append(r15)
            org.telegram.tgnet.TLRPC$InputFileLocation r4 = r7.location
            long r4 = r4.id
            r2.append(r4)
            java.lang.String r4 = r7.ext
            r2.append(r4)
            r2.append(r3)
            java.lang.String r3 = r2.toString()
            byte[] r2 = r7.key
            if (r2 == 0) goto L_0x02ba
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            int r4 = r7.datacenterId
            r2.append(r4)
            r2.append(r15)
            org.telegram.tgnet.TLRPC$InputFileLocation r4 = r7.location
            long r4 = r4.id
            r2.append(r4)
            r2.append(r1)
            java.lang.String r12 = r2.toString()
        L_0x02b8:
            r1 = r12
            goto L_0x02bb
        L_0x02ba:
            r1 = 0
        L_0x02bb:
            r2 = 0
            r12 = 0
            goto L_0x035e
        L_0x02bf:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            int r1 = r7.datacenterId
            r0.append(r1)
            r0.append(r15)
            org.telegram.tgnet.TLRPC$InputFileLocation r1 = r7.location
            long r3 = r1.id
            r0.append(r3)
            r0.append(r5)
            java.lang.String r1 = r0.toString()
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            int r3 = r7.datacenterId
            r0.append(r3)
            r0.append(r15)
            org.telegram.tgnet.TLRPC$InputFileLocation r3 = r7.location
            long r3 = r3.id
            r0.append(r3)
            java.lang.String r3 = r7.ext
            r0.append(r3)
            java.lang.String r3 = r0.toString()
            byte[] r0 = r7.key
            if (r0 == 0) goto L_0x0317
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            int r4 = r7.datacenterId
            r0.append(r4)
            r0.append(r15)
            org.telegram.tgnet.TLRPC$InputFileLocation r4 = r7.location
            long r4 = r4.id
            r0.append(r4)
            r0.append(r2)
            java.lang.String r12 = r0.toString()
            goto L_0x0318
        L_0x0317:
            r12 = 0
        L_0x0318:
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$Range> r0 = r7.notLoadedBytesRanges
            if (r0 == 0) goto L_0x033a
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            int r2 = r7.datacenterId
            r0.append(r2)
            r0.append(r15)
            org.telegram.tgnet.TLRPC$InputFileLocation r2 = r7.location
            long r4 = r2.id
            r0.append(r4)
            java.lang.String r2 = ".pt"
            r0.append(r2)
            java.lang.String r0 = r0.toString()
            goto L_0x033b
        L_0x033a:
            r0 = 0
        L_0x033b:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            int r4 = r7.datacenterId
            r2.append(r4)
            r2.append(r15)
            org.telegram.tgnet.TLRPC$InputFileLocation r4 = r7.location
            long r4 = r4.id
            r2.append(r4)
            java.lang.String r4 = ".preload"
            r2.append(r4)
            java.lang.String r2 = r2.toString()
        L_0x0358:
            r21 = r12
            r12 = r0
            r0 = r1
            r1 = r21
        L_0x035e:
            java.util.ArrayList r4 = new java.util.ArrayList
            int r5 = r7.currentMaxDownloadRequests
            r4.<init>(r5)
            r7.requestInfos = r4
            java.util.ArrayList r4 = new java.util.ArrayList
            int r5 = r7.currentMaxDownloadRequests
            int r5 = r5 - r8
            r4.<init>(r5)
            r7.delayedRequestInfos = r4
            r7.state = r8
            java.lang.Object r4 = r7.parentObject
            boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC.TL_theme
            if (r5 == 0) goto L_0x039f
            org.telegram.tgnet.TLRPC$TL_theme r4 = (org.telegram.tgnet.TLRPC.TL_theme) r4
            java.io.File r5 = new java.io.File
            java.io.File r6 = org.telegram.messenger.ApplicationLoader.getFilesDirFixed()
            java.lang.StringBuilder r13 = new java.lang.StringBuilder
            r13.<init>()
            java.lang.String r14 = "remote"
            r13.append(r14)
            long r14 = r4.id
            r13.append(r14)
            java.lang.String r4 = ".attheme"
            r13.append(r4)
            java.lang.String r4 = r13.toString()
            r5.<init>(r6, r4)
            r7.cacheFileFinal = r5
            goto L_0x03a8
        L_0x039f:
            java.io.File r4 = new java.io.File
            java.io.File r5 = r7.storePath
            r4.<init>(r5, r3)
            r7.cacheFileFinal = r4
        L_0x03a8:
            java.io.File r4 = r7.cacheFileFinal
            boolean r4 = r4.exists()
            if (r4 == 0) goto L_0x03cb
            java.lang.Object r5 = r7.parentObject
            boolean r5 = r5 instanceof org.telegram.tgnet.TLRPC.TL_theme
            if (r5 != 0) goto L_0x03c5
            int r5 = r7.totalBytesCount
            if (r5 == 0) goto L_0x03cb
            long r5 = (long) r5
            java.io.File r13 = r7.cacheFileFinal
            long r13 = r13.length()
            int r15 = (r5 > r13 ? 1 : (r5 == r13 ? 0 : -1))
            if (r15 == 0) goto L_0x03cb
        L_0x03c5:
            java.io.File r4 = r7.cacheFileFinal
            r4.delete()
            r4 = 0
        L_0x03cb:
            if (r4 != 0) goto L_0x0788
            java.io.File r4 = new java.io.File
            java.io.File r5 = r7.tempPath
            r4.<init>(r5, r0)
            r7.cacheFileTemp = r4
            boolean r4 = r7.ungzip
            if (r4 == 0) goto L_0x03f4
            java.io.File r4 = new java.io.File
            java.io.File r5 = r7.tempPath
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            r6.append(r0)
            java.lang.String r0 = ".gz"
            r6.append(r0)
            java.lang.String r0 = r6.toString()
            r4.<init>(r5, r0)
            r7.cacheFileGzipTemp = r4
        L_0x03f4:
            boolean r0 = r7.encryptFile
            r4 = 32
            java.lang.String r13 = "rws"
            if (r0 == 0) goto L_0x0473
            java.io.File r0 = new java.io.File
            java.io.File r5 = org.telegram.messenger.FileLoader.getInternalCacheDir()
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            r6.append(r3)
            java.lang.String r3 = ".key"
            r6.append(r3)
            java.lang.String r3 = r6.toString()
            r0.<init>(r5, r3)
            java.io.RandomAccessFile r3 = new java.io.RandomAccessFile     // Catch:{ Exception -> 0x046d }
            r3.<init>(r0, r13)     // Catch:{ Exception -> 0x046d }
            long r5 = r0.length()     // Catch:{ Exception -> 0x046d }
            byte[] r0 = new byte[r4]     // Catch:{ Exception -> 0x046d }
            r7.encryptKey = r0     // Catch:{ Exception -> 0x046d }
            r0 = 16
            byte[] r0 = new byte[r0]     // Catch:{ Exception -> 0x046d }
            r7.encryptIv = r0     // Catch:{ Exception -> 0x046d }
            int r0 = (r5 > r10 ? 1 : (r5 == r10 ? 0 : -1))
            if (r0 <= 0) goto L_0x0442
            r14 = 48
            long r5 = r5 % r14
            int r0 = (r5 > r10 ? 1 : (r5 == r10 ? 0 : -1))
            if (r0 != 0) goto L_0x0442
            byte[] r0 = r7.encryptKey     // Catch:{ Exception -> 0x046d }
            r3.read(r0, r9, r4)     // Catch:{ Exception -> 0x046d }
            byte[] r0 = r7.encryptIv     // Catch:{ Exception -> 0x046d }
            r5 = 16
            r3.read(r0, r9, r5)     // Catch:{ Exception -> 0x046d }
            r5 = 0
            goto L_0x045b
        L_0x0442:
            java.security.SecureRandom r0 = org.telegram.messenger.Utilities.random     // Catch:{ Exception -> 0x046d }
            byte[] r5 = r7.encryptKey     // Catch:{ Exception -> 0x046d }
            r0.nextBytes(r5)     // Catch:{ Exception -> 0x046d }
            java.security.SecureRandom r0 = org.telegram.messenger.Utilities.random     // Catch:{ Exception -> 0x046d }
            byte[] r5 = r7.encryptIv     // Catch:{ Exception -> 0x046d }
            r0.nextBytes(r5)     // Catch:{ Exception -> 0x046d }
            byte[] r0 = r7.encryptKey     // Catch:{ Exception -> 0x046d }
            r3.write(r0)     // Catch:{ Exception -> 0x046d }
            byte[] r0 = r7.encryptIv     // Catch:{ Exception -> 0x046d }
            r3.write(r0)     // Catch:{ Exception -> 0x046d }
            r5 = 1
        L_0x045b:
            java.nio.channels.FileChannel r0 = r3.getChannel()     // Catch:{ Exception -> 0x0463 }
            r0.close()     // Catch:{ Exception -> 0x0463 }
            goto L_0x0467
        L_0x0463:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ Exception -> 0x046b }
        L_0x0467:
            r3.close()     // Catch:{ Exception -> 0x046b }
            goto L_0x0474
        L_0x046b:
            r0 = move-exception
            goto L_0x046f
        L_0x046d:
            r0 = move-exception
            r5 = 0
        L_0x046f:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x0474
        L_0x0473:
            r5 = 0
        L_0x0474:
            boolean[] r14 = new boolean[r8]
            r14[r9] = r9
            boolean r0 = r7.supportsPreloading
            r15 = 4
            if (r0 == 0) goto L_0x05aa
            if (r2 == 0) goto L_0x05aa
            java.io.File r0 = new java.io.File
            java.io.File r3 = r7.tempPath
            r0.<init>(r3, r2)
            r7.cacheFilePreload = r0
            java.io.RandomAccessFile r0 = new java.io.RandomAccessFile     // Catch:{ Exception -> 0x0577 }
            java.io.File r2 = r7.cacheFilePreload     // Catch:{ Exception -> 0x0577 }
            r0.<init>(r2, r13)     // Catch:{ Exception -> 0x0577 }
            r7.preloadStream = r0     // Catch:{ Exception -> 0x0577 }
            java.io.RandomAccessFile r0 = r7.preloadStream     // Catch:{ Exception -> 0x0577 }
            long r2 = r0.length()     // Catch:{ Exception -> 0x0577 }
            r7.preloadStreamFileOffset = r8     // Catch:{ Exception -> 0x0577 }
            long r10 = (long) r9     // Catch:{ Exception -> 0x0577 }
            long r10 = r2 - r10
            r17 = 1
            int r0 = (r10 > r17 ? 1 : (r10 == r17 ? 0 : -1))
            if (r0 <= 0) goto L_0x0568
            java.io.RandomAccessFile r0 = r7.preloadStream     // Catch:{ Exception -> 0x0577 }
            byte r0 = r0.readByte()     // Catch:{ Exception -> 0x0577 }
            if (r0 == 0) goto L_0x04ad
            r0 = 1
            goto L_0x04ae
        L_0x04ad:
            r0 = 0
        L_0x04ae:
            r14[r9] = r0     // Catch:{ Exception -> 0x0577 }
            r0 = 1
        L_0x04b1:
            long r10 = (long) r0     // Catch:{ Exception -> 0x0577 }
            int r6 = (r10 > r2 ? 1 : (r10 == r2 ? 0 : -1))
            if (r6 >= 0) goto L_0x0568
            long r10 = r2 - r10
            int r6 = (r10 > r15 ? 1 : (r10 == r15 ? 0 : -1))
            if (r6 >= 0) goto L_0x04be
            goto L_0x0568
        L_0x04be:
            java.io.RandomAccessFile r6 = r7.preloadStream     // Catch:{ Exception -> 0x0577 }
            int r6 = r6.readInt()     // Catch:{ Exception -> 0x0577 }
            int r0 = r0 + 4
            long r10 = (long) r0     // Catch:{ Exception -> 0x0577 }
            long r10 = r2 - r10
            int r17 = (r10 > r15 ? 1 : (r10 == r15 ? 0 : -1))
            if (r17 < 0) goto L_0x0568
            if (r6 < 0) goto L_0x0568
            int r10 = r7.totalBytesCount     // Catch:{ Exception -> 0x0577 }
            if (r6 <= r10) goto L_0x04d5
            goto L_0x0568
        L_0x04d5:
            java.io.RandomAccessFile r10 = r7.preloadStream     // Catch:{ Exception -> 0x0577 }
            int r10 = r10.readInt()     // Catch:{ Exception -> 0x0577 }
            int r0 = r0 + 4
            r17 = r5
            long r4 = (long) r0
            long r4 = r2 - r4
            r18 = r12
            long r11 = (long) r10
            int r19 = (r4 > r11 ? 1 : (r4 == r11 ? 0 : -1))
            if (r19 < 0) goto L_0x056c
            int r4 = r7.currentDownloadChunkSize     // Catch:{ Exception -> 0x0575 }
            if (r10 <= r4) goto L_0x04ef
            goto L_0x056c
        L_0x04ef:
            org.telegram.messenger.FileLoadOperation$PreloadRange r4 = new org.telegram.messenger.FileLoadOperation$PreloadRange     // Catch:{ Exception -> 0x0575 }
            r5 = 0
            r4.<init>(r0, r10)     // Catch:{ Exception -> 0x0575 }
            int r0 = r0 + r10
            java.io.RandomAccessFile r5 = r7.preloadStream     // Catch:{ Exception -> 0x0575 }
            long r11 = (long) r0     // Catch:{ Exception -> 0x0575 }
            r5.seek(r11)     // Catch:{ Exception -> 0x0575 }
            long r11 = r2 - r11
            r19 = 12
            int r5 = (r11 > r19 ? 1 : (r11 == r19 ? 0 : -1))
            if (r5 >= 0) goto L_0x0505
            goto L_0x056c
        L_0x0505:
            java.io.RandomAccessFile r5 = r7.preloadStream     // Catch:{ Exception -> 0x0575 }
            int r5 = r5.readInt()     // Catch:{ Exception -> 0x0575 }
            r7.foundMoovSize = r5     // Catch:{ Exception -> 0x0575 }
            int r5 = r7.foundMoovSize     // Catch:{ Exception -> 0x0575 }
            if (r5 == 0) goto L_0x0522
            int r5 = r7.nextPreloadDownloadOffset     // Catch:{ Exception -> 0x0575 }
            int r11 = r7.totalBytesCount     // Catch:{ Exception -> 0x0575 }
            int r11 = r11 / 2
            if (r5 <= r11) goto L_0x051b
            r5 = 2
            goto L_0x051c
        L_0x051b:
            r5 = 1
        L_0x051c:
            r7.moovFound = r5     // Catch:{ Exception -> 0x0575 }
            int r5 = r7.foundMoovSize     // Catch:{ Exception -> 0x0575 }
            r7.preloadNotRequestedBytesCount = r5     // Catch:{ Exception -> 0x0575 }
        L_0x0522:
            java.io.RandomAccessFile r5 = r7.preloadStream     // Catch:{ Exception -> 0x0575 }
            int r5 = r5.readInt()     // Catch:{ Exception -> 0x0575 }
            r7.nextPreloadDownloadOffset = r5     // Catch:{ Exception -> 0x0575 }
            java.io.RandomAccessFile r5 = r7.preloadStream     // Catch:{ Exception -> 0x0575 }
            int r5 = r5.readInt()     // Catch:{ Exception -> 0x0575 }
            r7.nextAtomOffset = r5     // Catch:{ Exception -> 0x0575 }
            int r0 = r0 + 12
            android.util.SparseArray<org.telegram.messenger.FileLoadOperation$PreloadRange> r5 = r7.preloadedBytesRanges     // Catch:{ Exception -> 0x0575 }
            if (r5 != 0) goto L_0x053f
            android.util.SparseArray r5 = new android.util.SparseArray     // Catch:{ Exception -> 0x0575 }
            r5.<init>()     // Catch:{ Exception -> 0x0575 }
            r7.preloadedBytesRanges = r5     // Catch:{ Exception -> 0x0575 }
        L_0x053f:
            android.util.SparseIntArray r5 = r7.requestedPreloadedBytesRanges     // Catch:{ Exception -> 0x0575 }
            if (r5 != 0) goto L_0x054a
            android.util.SparseIntArray r5 = new android.util.SparseIntArray     // Catch:{ Exception -> 0x0575 }
            r5.<init>()     // Catch:{ Exception -> 0x0575 }
            r7.requestedPreloadedBytesRanges = r5     // Catch:{ Exception -> 0x0575 }
        L_0x054a:
            android.util.SparseArray<org.telegram.messenger.FileLoadOperation$PreloadRange> r5 = r7.preloadedBytesRanges     // Catch:{ Exception -> 0x0575 }
            r5.put(r6, r4)     // Catch:{ Exception -> 0x0575 }
            android.util.SparseIntArray r4 = r7.requestedPreloadedBytesRanges     // Catch:{ Exception -> 0x0575 }
            r4.put(r6, r8)     // Catch:{ Exception -> 0x0575 }
            int r4 = r7.totalPreloadedBytes     // Catch:{ Exception -> 0x0575 }
            int r4 = r4 + r10
            r7.totalPreloadedBytes = r4     // Catch:{ Exception -> 0x0575 }
            int r4 = r7.preloadStreamFileOffset     // Catch:{ Exception -> 0x0575 }
            int r10 = r10 + 20
            int r4 = r4 + r10
            r7.preloadStreamFileOffset = r4     // Catch:{ Exception -> 0x0575 }
            r5 = r17
            r12 = r18
            r4 = 32
            goto L_0x04b1
        L_0x0568:
            r17 = r5
            r18 = r12
        L_0x056c:
            java.io.RandomAccessFile r0 = r7.preloadStream     // Catch:{ Exception -> 0x0575 }
            int r2 = r7.preloadStreamFileOffset     // Catch:{ Exception -> 0x0575 }
            long r2 = (long) r2     // Catch:{ Exception -> 0x0575 }
            r0.seek(r2)     // Catch:{ Exception -> 0x0575 }
            goto L_0x057f
        L_0x0575:
            r0 = move-exception
            goto L_0x057c
        L_0x0577:
            r0 = move-exception
            r17 = r5
            r18 = r12
        L_0x057c:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x057f:
            boolean r0 = r7.isPreloadVideoOperation
            if (r0 != 0) goto L_0x05ae
            android.util.SparseArray<org.telegram.messenger.FileLoadOperation$PreloadRange> r0 = r7.preloadedBytesRanges
            if (r0 != 0) goto L_0x05ae
            r2 = 0
            r7.cacheFilePreload = r2
            java.io.RandomAccessFile r0 = r7.preloadStream     // Catch:{ Exception -> 0x05a5 }
            if (r0 == 0) goto L_0x05ae
            java.io.RandomAccessFile r0 = r7.preloadStream     // Catch:{ Exception -> 0x0598 }
            java.nio.channels.FileChannel r0 = r0.getChannel()     // Catch:{ Exception -> 0x0598 }
            r0.close()     // Catch:{ Exception -> 0x0598 }
            goto L_0x059c
        L_0x0598:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ Exception -> 0x05a5 }
        L_0x059c:
            java.io.RandomAccessFile r0 = r7.preloadStream     // Catch:{ Exception -> 0x05a5 }
            r0.close()     // Catch:{ Exception -> 0x05a5 }
            r2 = 0
            r7.preloadStream = r2     // Catch:{ Exception -> 0x05a5 }
            goto L_0x05ae
        L_0x05a5:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x05ae
        L_0x05aa:
            r17 = r5
            r18 = r12
        L_0x05ae:
            if (r18 == 0) goto L_0x060c
            java.io.File r0 = new java.io.File
            java.io.File r2 = r7.tempPath
            r12 = r18
            r0.<init>(r2, r12)
            r7.cacheFileParts = r0
            java.io.RandomAccessFile r0 = new java.io.RandomAccessFile     // Catch:{ Exception -> 0x0608 }
            java.io.File r2 = r7.cacheFileParts     // Catch:{ Exception -> 0x0608 }
            r0.<init>(r2, r13)     // Catch:{ Exception -> 0x0608 }
            r7.filePartsStream = r0     // Catch:{ Exception -> 0x0608 }
            java.io.RandomAccessFile r0 = r7.filePartsStream     // Catch:{ Exception -> 0x0608 }
            long r2 = r0.length()     // Catch:{ Exception -> 0x0608 }
            r4 = 8
            long r4 = r2 % r4
            int r0 = (r4 > r15 ? 1 : (r4 == r15 ? 0 : -1))
            if (r0 != 0) goto L_0x060c
            long r2 = r2 - r15
            java.io.RandomAccessFile r0 = r7.filePartsStream     // Catch:{ Exception -> 0x0608 }
            int r0 = r0.readInt()     // Catch:{ Exception -> 0x0608 }
            long r4 = (long) r0     // Catch:{ Exception -> 0x0608 }
            r10 = 2
            long r2 = r2 / r10
            int r6 = (r4 > r2 ? 1 : (r4 == r2 ? 0 : -1))
            if (r6 > 0) goto L_0x060c
            r2 = 0
        L_0x05e2:
            if (r2 >= r0) goto L_0x060c
            java.io.RandomAccessFile r3 = r7.filePartsStream     // Catch:{ Exception -> 0x0608 }
            int r3 = r3.readInt()     // Catch:{ Exception -> 0x0608 }
            java.io.RandomAccessFile r4 = r7.filePartsStream     // Catch:{ Exception -> 0x0608 }
            int r4 = r4.readInt()     // Catch:{ Exception -> 0x0608 }
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$Range> r5 = r7.notLoadedBytesRanges     // Catch:{ Exception -> 0x0608 }
            org.telegram.messenger.FileLoadOperation$Range r6 = new org.telegram.messenger.FileLoadOperation$Range     // Catch:{ Exception -> 0x0608 }
            r10 = 0
            r6.<init>(r3, r4)     // Catch:{ Exception -> 0x0608 }
            r5.add(r6)     // Catch:{ Exception -> 0x0608 }
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$Range> r5 = r7.notRequestedBytesRanges     // Catch:{ Exception -> 0x0608 }
            org.telegram.messenger.FileLoadOperation$Range r6 = new org.telegram.messenger.FileLoadOperation$Range     // Catch:{ Exception -> 0x0608 }
            r6.<init>(r3, r4)     // Catch:{ Exception -> 0x0608 }
            r5.add(r6)     // Catch:{ Exception -> 0x0608 }
            int r2 = r2 + 1
            goto L_0x05e2
        L_0x0608:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x060c:
            java.io.File r0 = r7.cacheFileTemp
            boolean r0 = r0.exists()
            if (r0 == 0) goto L_0x066b
            if (r17 == 0) goto L_0x061c
            java.io.File r0 = r7.cacheFileTemp
            r0.delete()
            goto L_0x068e
        L_0x061c:
            java.io.File r0 = r7.cacheFileTemp
            long r2 = r0.length()
            if (r1 == 0) goto L_0x0633
            int r0 = r7.currentDownloadChunkSize
            long r4 = (long) r0
            long r2 = r2 % r4
            r4 = 0
            int r0 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r0 == 0) goto L_0x0633
            r7.downloadedBytes = r9
            r7.requestedBytesCount = r9
            goto L_0x0643
        L_0x0633:
            java.io.File r0 = r7.cacheFileTemp
            long r2 = r0.length()
            int r0 = (int) r2
            int r2 = r7.currentDownloadChunkSize
            int r0 = r0 / r2
            int r0 = r0 * r2
            r7.downloadedBytes = r0
            r7.requestedBytesCount = r0
        L_0x0643:
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$Range> r0 = r7.notLoadedBytesRanges
            if (r0 == 0) goto L_0x068e
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L_0x068e
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$Range> r0 = r7.notLoadedBytesRanges
            org.telegram.messenger.FileLoadOperation$Range r2 = new org.telegram.messenger.FileLoadOperation$Range
            int r3 = r7.downloadedBytes
            int r4 = r7.totalBytesCount
            r5 = 0
            r2.<init>(r3, r4)
            r0.add(r2)
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$Range> r0 = r7.notRequestedBytesRanges
            org.telegram.messenger.FileLoadOperation$Range r2 = new org.telegram.messenger.FileLoadOperation$Range
            int r3 = r7.downloadedBytes
            int r4 = r7.totalBytesCount
            r2.<init>(r3, r4)
            r0.add(r2)
            goto L_0x068e
        L_0x066b:
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$Range> r0 = r7.notLoadedBytesRanges
            if (r0 == 0) goto L_0x068e
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L_0x068e
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$Range> r0 = r7.notLoadedBytesRanges
            org.telegram.messenger.FileLoadOperation$Range r2 = new org.telegram.messenger.FileLoadOperation$Range
            int r3 = r7.totalBytesCount
            r4 = 0
            r2.<init>(r9, r3)
            r0.add(r2)
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$Range> r0 = r7.notRequestedBytesRanges
            org.telegram.messenger.FileLoadOperation$Range r2 = new org.telegram.messenger.FileLoadOperation$Range
            int r3 = r7.totalBytesCount
            r2.<init>(r9, r3)
            r0.add(r2)
        L_0x068e:
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$Range> r0 = r7.notLoadedBytesRanges
            if (r0 == 0) goto L_0x06ba
            int r2 = r7.totalBytesCount
            r7.downloadedBytes = r2
            int r0 = r0.size()
            r2 = 0
        L_0x069b:
            if (r2 >= r0) goto L_0x06b6
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$Range> r3 = r7.notLoadedBytesRanges
            java.lang.Object r3 = r3.get(r2)
            org.telegram.messenger.FileLoadOperation$Range r3 = (org.telegram.messenger.FileLoadOperation.Range) r3
            int r4 = r7.downloadedBytes
            int r5 = r3.end
            int r3 = r3.start
            int r5 = r5 - r3
            int r4 = r4 - r5
            r7.downloadedBytes = r4
            int r2 = r2 + 1
            goto L_0x069b
        L_0x06b6:
            int r0 = r7.downloadedBytes
            r7.requestedBytesCount = r0
        L_0x06ba:
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x06f9
            boolean r0 = r7.isPreloadVideoOperation
            if (r0 == 0) goto L_0x06d9
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r2 = "start preloading file to temp = "
            r0.append(r2)
            java.io.File r2 = r7.cacheFileTemp
            r0.append(r2)
            java.lang.String r0 = r0.toString()
            org.telegram.messenger.FileLog.d(r0)
            goto L_0x06f9
        L_0x06d9:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r2 = "start loading file to temp = "
            r0.append(r2)
            java.io.File r2 = r7.cacheFileTemp
            r0.append(r2)
            java.lang.String r2 = " final = "
            r0.append(r2)
            java.io.File r2 = r7.cacheFileFinal
            r0.append(r2)
            java.lang.String r0 = r0.toString()
            org.telegram.messenger.FileLog.d(r0)
        L_0x06f9:
            if (r1 == 0) goto L_0x073d
            java.io.File r0 = new java.io.File
            java.io.File r2 = r7.tempPath
            r0.<init>(r2, r1)
            r7.cacheIvTemp = r0
            java.io.RandomAccessFile r0 = new java.io.RandomAccessFile     // Catch:{ Exception -> 0x0735 }
            java.io.File r1 = r7.cacheIvTemp     // Catch:{ Exception -> 0x0735 }
            r0.<init>(r1, r13)     // Catch:{ Exception -> 0x0735 }
            r7.fiv = r0     // Catch:{ Exception -> 0x0735 }
            int r0 = r7.downloadedBytes     // Catch:{ Exception -> 0x0735 }
            if (r0 == 0) goto L_0x073d
            if (r17 != 0) goto L_0x073d
            java.io.File r0 = r7.cacheIvTemp     // Catch:{ Exception -> 0x0735 }
            long r0 = r0.length()     // Catch:{ Exception -> 0x0735 }
            r2 = 0
            int r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r4 <= 0) goto L_0x0730
            r4 = 32
            long r0 = r0 % r4
            int r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r4 != 0) goto L_0x0730
            java.io.RandomAccessFile r0 = r7.fiv     // Catch:{ Exception -> 0x0735 }
            byte[] r1 = r7.iv     // Catch:{ Exception -> 0x0735 }
            r2 = 32
            r0.read(r1, r9, r2)     // Catch:{ Exception -> 0x0735 }
            goto L_0x073d
        L_0x0730:
            r7.downloadedBytes = r9     // Catch:{ Exception -> 0x0735 }
            r7.requestedBytesCount = r9     // Catch:{ Exception -> 0x0735 }
            goto L_0x073d
        L_0x0735:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            r7.downloadedBytes = r9
            r7.requestedBytesCount = r9
        L_0x073d:
            boolean r0 = r7.isPreloadVideoOperation
            if (r0 != 0) goto L_0x0759
            int r0 = r7.downloadedBytes
            if (r0 == 0) goto L_0x0759
            int r0 = r7.totalBytesCount
            if (r0 <= 0) goto L_0x0759
            r22.copyNotLoadedRanges()
            org.telegram.messenger.FileLoadOperation$FileLoadOperationDelegate r1 = r7.delegate
            int r0 = r7.downloadedBytes
            long r3 = (long) r0
            int r0 = r7.totalBytesCount
            long r5 = (long) r0
            r2 = r22
            r1.didChangedLoadProgress(r2, r3, r5)
        L_0x0759:
            java.io.RandomAccessFile r0 = new java.io.RandomAccessFile     // Catch:{ Exception -> 0x076f }
            java.io.File r1 = r7.cacheFileTemp     // Catch:{ Exception -> 0x076f }
            r0.<init>(r1, r13)     // Catch:{ Exception -> 0x076f }
            r7.fileOutputStream = r0     // Catch:{ Exception -> 0x076f }
            int r0 = r7.downloadedBytes     // Catch:{ Exception -> 0x076f }
            if (r0 == 0) goto L_0x0773
            java.io.RandomAccessFile r0 = r7.fileOutputStream     // Catch:{ Exception -> 0x076f }
            int r1 = r7.downloadedBytes     // Catch:{ Exception -> 0x076f }
            long r1 = (long) r1     // Catch:{ Exception -> 0x076f }
            r0.seek(r1)     // Catch:{ Exception -> 0x076f }
            goto L_0x0773
        L_0x076f:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0773:
            java.io.RandomAccessFile r0 = r7.fileOutputStream
            if (r0 != 0) goto L_0x077b
            r7.onFail(r8, r9)
            return r9
        L_0x077b:
            r7.started = r8
            org.telegram.messenger.DispatchQueue r0 = org.telegram.messenger.Utilities.stageQueue
            org.telegram.messenger.-$$Lambda$FileLoadOperation$pnBGbN7TnymWdMGyH-sNRrKnKro r1 = new org.telegram.messenger.-$$Lambda$FileLoadOperation$pnBGbN7TnymWdMGyH-sNRrKnKro
            r1.<init>(r14)
            r0.postRunnable(r1)
            goto L_0x0791
        L_0x0788:
            r7.started = r8
            r7.onFinishLoadingFile(r9)     // Catch:{ Exception -> 0x078e }
            goto L_0x0791
        L_0x078e:
            r7.onFail(r8, r9)
        L_0x0791:
            return r8
        L_0x0792:
            r7.onFail(r8, r9)
            return r9
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.FileLoadOperation.start(org.telegram.messenger.FileLoadOperationStream, int, boolean):boolean");
    }

    public /* synthetic */ void lambda$start$4$FileLoadOperation(boolean z, int i, FileLoadOperationStream fileLoadOperationStream, boolean z2) {
        if (this.streamListeners == null) {
            this.streamListeners = new ArrayList<>();
        }
        if (z) {
            int i2 = this.currentDownloadChunkSize;
            int i3 = (i / i2) * i2;
            RequestInfo requestInfo = this.priorityRequestInfo;
            if (!(requestInfo == null || requestInfo.offset == i3)) {
                this.requestInfos.remove(this.priorityRequestInfo);
                this.requestedBytesCount -= this.currentDownloadChunkSize;
                removePart(this.notRequestedBytesRanges, this.priorityRequestInfo.offset, this.priorityRequestInfo.offset + this.currentDownloadChunkSize);
                if (this.priorityRequestInfo.requestToken != 0) {
                    ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.priorityRequestInfo.requestToken, true);
                    this.requestsCount--;
                }
                if (BuildVars.DEBUG_VERSION) {
                    FileLog.d("frame get cancel request at offset " + this.priorityRequestInfo.offset);
                }
                this.priorityRequestInfo = null;
            }
            if (this.priorityRequestInfo == null) {
                this.streamPriorityStartOffset = i3;
            }
        } else {
            int i4 = this.currentDownloadChunkSize;
            this.streamStartOffset = (i / i4) * i4;
        }
        this.streamListeners.add(fileLoadOperationStream);
        if (z2) {
            if (!(this.preloadedBytesRanges == null || getDownloadedLengthFromOffsetInternal(this.notLoadedBytesRanges, this.streamStartOffset, 1) != 0 || this.preloadedBytesRanges.get(this.streamStartOffset) == null)) {
                this.nextPartWasPreloaded = true;
            }
            startDownloadRequest();
            this.nextPartWasPreloaded = false;
        }
    }

    public /* synthetic */ void lambda$start$5$FileLoadOperation(boolean[] zArr) {
        if (this.totalBytesCount == 0 || ((!this.isPreloadVideoOperation || !zArr[0]) && this.downloadedBytes != this.totalBytesCount)) {
            startDownloadRequest();
            return;
        }
        try {
            onFinishLoadingFile(false);
        } catch (Exception unused) {
            onFail(true, 0);
        }
    }

    public boolean isPaused() {
        return this.paused;
    }

    public void setIsPreloadVideoOperation(boolean z) {
        if (this.isPreloadVideoOperation == z) {
            return;
        }
        if (z && this.totalBytesCount <= 2097152) {
            return;
        }
        if (z || !this.isPreloadVideoOperation) {
            this.isPreloadVideoOperation = z;
        } else if (this.state == 3) {
            this.isPreloadVideoOperation = z;
            this.state = 0;
            this.preloadFinished = false;
            start();
        } else if (this.state == 1) {
            Utilities.stageQueue.postRunnable(new Runnable(z) {
                private final /* synthetic */ boolean f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    FileLoadOperation.this.lambda$setIsPreloadVideoOperation$6$FileLoadOperation(this.f$1);
                }
            });
        } else {
            this.isPreloadVideoOperation = z;
        }
    }

    public /* synthetic */ void lambda$setIsPreloadVideoOperation$6$FileLoadOperation(boolean z) {
        this.requestedBytesCount = 0;
        clearOperaion((RequestInfo) null, true);
        this.isPreloadVideoOperation = z;
        startDownloadRequest();
    }

    public boolean isPreloadVideoOperation() {
        return this.isPreloadVideoOperation;
    }

    public boolean isPreloadFinished() {
        return this.preloadFinished;
    }

    public void cancel() {
        Utilities.stageQueue.postRunnable(new Runnable() {
            public final void run() {
                FileLoadOperation.this.lambda$cancel$7$FileLoadOperation();
            }
        });
    }

    public /* synthetic */ void lambda$cancel$7$FileLoadOperation() {
        if (this.state != 3 && this.state != 2) {
            if (this.requestInfos != null) {
                for (int i = 0; i < this.requestInfos.size(); i++) {
                    RequestInfo requestInfo = this.requestInfos.get(i);
                    if (requestInfo.requestToken != 0) {
                        ConnectionsManager.getInstance(this.currentAccount).cancelRequest(requestInfo.requestToken, true);
                    }
                }
            }
            onFail(false, 1);
        }
    }

    private void cleanup() {
        try {
            if (this.fileOutputStream != null) {
                try {
                    this.fileOutputStream.getChannel().close();
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
                this.fileOutputStream.close();
                this.fileOutputStream = null;
            }
        } catch (Exception e2) {
            FileLog.e((Throwable) e2);
        }
        try {
            if (this.preloadStream != null) {
                try {
                    this.preloadStream.getChannel().close();
                } catch (Exception e3) {
                    FileLog.e((Throwable) e3);
                }
                this.preloadStream.close();
                this.preloadStream = null;
            }
        } catch (Exception e4) {
            FileLog.e((Throwable) e4);
        }
        try {
            if (this.fileReadStream != null) {
                try {
                    this.fileReadStream.getChannel().close();
                } catch (Exception e5) {
                    FileLog.e((Throwable) e5);
                }
                this.fileReadStream.close();
                this.fileReadStream = null;
            }
        } catch (Exception e6) {
            FileLog.e((Throwable) e6);
        }
        try {
            if (this.filePartsStream != null) {
                try {
                    this.filePartsStream.getChannel().close();
                } catch (Exception e7) {
                    FileLog.e((Throwable) e7);
                }
                this.filePartsStream.close();
                this.filePartsStream = null;
            }
        } catch (Exception e8) {
            FileLog.e((Throwable) e8);
        }
        try {
            if (this.fiv != null) {
                this.fiv.close();
                this.fiv = null;
            }
        } catch (Exception e9) {
            FileLog.e((Throwable) e9);
        }
        if (this.delayedRequestInfos != null) {
            for (int i = 0; i < this.delayedRequestInfos.size(); i++) {
                RequestInfo requestInfo = this.delayedRequestInfos.get(i);
                if (requestInfo.response != null) {
                    requestInfo.response.disableFree = false;
                    requestInfo.response.freeResources();
                } else if (requestInfo.responseWeb != null) {
                    requestInfo.responseWeb.disableFree = false;
                    requestInfo.responseWeb.freeResources();
                } else if (requestInfo.responseCdn != null) {
                    requestInfo.responseCdn.disableFree = false;
                    requestInfo.responseCdn.freeResources();
                }
            }
            this.delayedRequestInfos.clear();
        }
    }

    private void onFinishLoadingFile(boolean z) {
        if (this.state == 1) {
            this.state = 3;
            cleanup();
            if (this.isPreloadVideoOperation) {
                this.preloadFinished = true;
                if (BuildVars.DEBUG_VERSION) {
                    FileLog.d("finished preloading file to " + this.cacheFileTemp + " loaded " + this.totalPreloadedBytes + " of " + this.totalBytesCount);
                }
            } else {
                File file = this.cacheIvTemp;
                if (file != null) {
                    file.delete();
                    this.cacheIvTemp = null;
                }
                File file2 = this.cacheFileParts;
                if (file2 != null) {
                    file2.delete();
                    this.cacheFileParts = null;
                }
                File file3 = this.cacheFilePreload;
                if (file3 != null) {
                    file3.delete();
                    this.cacheFilePreload = null;
                }
                File file4 = this.cacheFileTemp;
                if (file4 != null) {
                    boolean z2 = false;
                    if (this.ungzip) {
                        try {
                            GZIPInputStream gZIPInputStream = new GZIPInputStream(new FileInputStream(file4));
                            FileLoader.copyFile(gZIPInputStream, this.cacheFileGzipTemp, 2097152);
                            gZIPInputStream.close();
                            this.cacheFileTemp.delete();
                            this.cacheFileTemp = this.cacheFileGzipTemp;
                            this.ungzip = false;
                        } catch (ZipException unused) {
                            this.ungzip = false;
                        } catch (Throwable th) {
                            FileLog.e(th);
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.e("unable to ungzip temp = " + this.cacheFileTemp + " to final = " + this.cacheFileFinal);
                            }
                        }
                    }
                    if (!this.ungzip) {
                        if (this.parentObject instanceof TLRPC.TL_theme) {
                            try {
                                z2 = AndroidUtilities.copyFile(this.cacheFileTemp, this.cacheFileFinal);
                            } catch (Exception e) {
                                FileLog.e((Throwable) e);
                            }
                        } else {
                            z2 = this.cacheFileTemp.renameTo(this.cacheFileFinal);
                        }
                        if (!z2) {
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.e("unable to rename temp = " + this.cacheFileTemp + " to final = " + this.cacheFileFinal + " retry = " + this.renameRetryCount);
                            }
                            this.renameRetryCount++;
                            if (this.renameRetryCount < 3) {
                                this.state = 1;
                                Utilities.stageQueue.postRunnable(new Runnable(z) {
                                    private final /* synthetic */ boolean f$1;

                                    {
                                        this.f$1 = r2;
                                    }

                                    public final void run() {
                                        FileLoadOperation.this.lambda$onFinishLoadingFile$8$FileLoadOperation(this.f$1);
                                    }
                                }, 200);
                                return;
                            }
                            this.cacheFileFinal = this.cacheFileTemp;
                        }
                    } else {
                        onFail(false, 0);
                        return;
                    }
                }
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("finished downloading file to " + this.cacheFileFinal);
                }
                if (z) {
                    int i = this.currentType;
                    if (i == 50331648) {
                        StatsController.getInstance(this.currentAccount).incrementReceivedItemsCount(ApplicationLoader.getCurrentNetworkType(), 3, 1);
                    } else if (i == 33554432) {
                        StatsController.getInstance(this.currentAccount).incrementReceivedItemsCount(ApplicationLoader.getCurrentNetworkType(), 2, 1);
                    } else if (i == 16777216) {
                        StatsController.getInstance(this.currentAccount).incrementReceivedItemsCount(ApplicationLoader.getCurrentNetworkType(), 4, 1);
                    } else if (i == 67108864) {
                        StatsController.getInstance(this.currentAccount).incrementReceivedItemsCount(ApplicationLoader.getCurrentNetworkType(), 5, 1);
                    }
                }
            }
            this.delegate.didFinishLoadingFile(this, this.cacheFileFinal);
        }
    }

    public /* synthetic */ void lambda$onFinishLoadingFile$8$FileLoadOperation(boolean z) {
        try {
            onFinishLoadingFile(z);
        } catch (Exception unused) {
            onFail(false, 0);
        }
    }

    private void delayRequestInfo(RequestInfo requestInfo) {
        this.delayedRequestInfos.add(requestInfo);
        if (requestInfo.response != null) {
            requestInfo.response.disableFree = true;
        } else if (requestInfo.responseWeb != null) {
            requestInfo.responseWeb.disableFree = true;
        } else if (requestInfo.responseCdn != null) {
            requestInfo.responseCdn.disableFree = true;
        }
    }

    private int findNextPreloadDownloadOffset(int i, int i2, NativeByteBuffer nativeByteBuffer) {
        int i3;
        int limit = nativeByteBuffer.limit();
        do {
            if (i < i2 - (this.preloadTempBuffer != null ? 16 : 0) || i >= (i3 = i2 + limit)) {
                return 0;
            }
            if (i >= i3 - 16) {
                this.preloadTempBufferCount = i3 - i;
                nativeByteBuffer.position(nativeByteBuffer.limit() - this.preloadTempBufferCount);
                nativeByteBuffer.readBytes(this.preloadTempBuffer, 0, this.preloadTempBufferCount, false);
                return i3;
            }
            if (this.preloadTempBufferCount != 0) {
                nativeByteBuffer.position(0);
                byte[] bArr = this.preloadTempBuffer;
                int i4 = this.preloadTempBufferCount;
                nativeByteBuffer.readBytes(bArr, i4, 16 - i4, false);
                this.preloadTempBufferCount = 0;
            } else {
                nativeByteBuffer.position(i - i2);
                nativeByteBuffer.readBytes(this.preloadTempBuffer, 0, 16, false);
            }
            byte[] bArr2 = this.preloadTempBuffer;
            int i5 = ((bArr2[0] & 255) << 24) + ((bArr2[1] & 255) << 16) + ((bArr2[2] & 255) << 8) + (bArr2[3] & 255);
            if (i5 == 0) {
                return 0;
            }
            if (i5 == 1) {
                i5 = ((bArr2[12] & 255) << 24) + ((bArr2[13] & 255) << 16) + ((bArr2[14] & 255) << 8) + (bArr2[15] & 255);
            }
            byte[] bArr3 = this.preloadTempBuffer;
            if (bArr3[4] == 109 && bArr3[5] == 111 && bArr3[6] == 111 && bArr3[7] == 118) {
                return -i5;
            }
            i += i5;
        } while (i < i3);
        return i;
    }

    private void requestFileOffsets(int i) {
        if (!this.requestingCdnOffsets) {
            this.requestingCdnOffsets = true;
            TLRPC.TL_upload_getCdnFileHashes tL_upload_getCdnFileHashes = new TLRPC.TL_upload_getCdnFileHashes();
            tL_upload_getCdnFileHashes.file_token = this.cdnToken;
            tL_upload_getCdnFileHashes.offset = i;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_upload_getCdnFileHashes, new RequestDelegate() {
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    FileLoadOperation.this.lambda$requestFileOffsets$9$FileLoadOperation(tLObject, tL_error);
                }
            }, (QuickAckDelegate) null, (WriteToSocketDelegate) null, 0, this.datacenterId, 1, true);
        }
    }

    public /* synthetic */ void lambda$requestFileOffsets$9$FileLoadOperation(TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tL_error != null) {
            onFail(false, 0);
            return;
        }
        this.requestingCdnOffsets = false;
        TLRPC.Vector vector = (TLRPC.Vector) tLObject;
        if (!vector.objects.isEmpty()) {
            if (this.cdnHashes == null) {
                this.cdnHashes = new SparseArray<>();
            }
            for (int i = 0; i < vector.objects.size(); i++) {
                TLRPC.TL_fileHash tL_fileHash = (TLRPC.TL_fileHash) vector.objects.get(i);
                this.cdnHashes.put(tL_fileHash.offset, tL_fileHash);
            }
        }
        int i2 = 0;
        while (i2 < this.delayedRequestInfos.size()) {
            RequestInfo requestInfo = this.delayedRequestInfos.get(i2);
            if (this.notLoadedBytesRanges != null || this.downloadedBytes == requestInfo.offset) {
                this.delayedRequestInfos.remove(i2);
                if (processRequestResult(requestInfo, (TLRPC.TL_error) null)) {
                    return;
                }
                if (requestInfo.response != null) {
                    requestInfo.response.disableFree = false;
                    requestInfo.response.freeResources();
                    return;
                } else if (requestInfo.responseWeb != null) {
                    requestInfo.responseWeb.disableFree = false;
                    requestInfo.responseWeb.freeResources();
                    return;
                } else if (requestInfo.responseCdn != null) {
                    requestInfo.responseCdn.disableFree = false;
                    requestInfo.responseCdn.freeResources();
                    return;
                } else {
                    return;
                }
            } else {
                i2++;
            }
        }
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:105:0x0253 A[Catch:{ Exception -> 0x0490 }] */
    /* JADX WARNING: Removed duplicated region for block: B:108:0x0292 A[Catch:{ Exception -> 0x0490 }] */
    /* JADX WARNING: Removed duplicated region for block: B:113:0x02dd A[Catch:{ Exception -> 0x0490 }] */
    /* JADX WARNING: Removed duplicated region for block: B:151:0x03f2 A[Catch:{ Exception -> 0x0490 }] */
    /* JADX WARNING: Removed duplicated region for block: B:74:0x01db A[Catch:{ Exception -> 0x0490 }] */
    /* JADX WARNING: Removed duplicated region for block: B:75:0x01e9 A[Catch:{ Exception -> 0x0490 }] */
    /* JADX WARNING: Removed duplicated region for block: B:98:0x022a A[Catch:{ Exception -> 0x0490 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean processRequestResult(org.telegram.messenger.FileLoadOperation.RequestInfo r22, org.telegram.tgnet.TLRPC.TL_error r23) {
        /*
            r21 = this;
            r7 = r21
            r0 = r23
            int r1 = r7.state
            java.lang.String r2 = " offset "
            r8 = 1
            r9 = 0
            if (r1 == r8) goto L_0x0031
            boolean r0 = org.telegram.messenger.BuildVars.DEBUG_VERSION
            if (r0 == 0) goto L_0x0030
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "trying to write to finished file "
            r0.append(r1)
            java.io.File r1 = r7.cacheFileFinal
            r0.append(r1)
            r0.append(r2)
            int r1 = r22.offset
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            org.telegram.messenger.FileLog.d(r0)
        L_0x0030:
            return r9
        L_0x0031:
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$RequestInfo> r1 = r7.requestInfos
            r3 = r22
            r1.remove(r3)
            java.lang.String r1 = " local_id = "
            r4 = 2
            java.lang.String r5 = " id = "
            r10 = 0
            if (r0 != 0) goto L_0x0499
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$Range> r0 = r7.notLoadedBytesRanges     // Catch:{ Exception -> 0x0490 }
            if (r0 != 0) goto L_0x0050
            int r0 = r7.downloadedBytes     // Catch:{ Exception -> 0x0490 }
            int r6 = r22.offset     // Catch:{ Exception -> 0x0490 }
            if (r0 == r6) goto L_0x0050
            r21.delayRequestInfo(r22)     // Catch:{ Exception -> 0x0490 }
            return r9
        L_0x0050:
            org.telegram.tgnet.TLRPC$TL_upload_file r0 = r22.response     // Catch:{ Exception -> 0x0490 }
            if (r0 == 0) goto L_0x005d
            org.telegram.tgnet.TLRPC$TL_upload_file r0 = r22.response     // Catch:{ Exception -> 0x0490 }
            org.telegram.tgnet.NativeByteBuffer r0 = r0.bytes     // Catch:{ Exception -> 0x0490 }
            goto L_0x0078
        L_0x005d:
            org.telegram.tgnet.TLRPC$TL_upload_webFile r0 = r22.responseWeb     // Catch:{ Exception -> 0x0490 }
            if (r0 == 0) goto L_0x006a
            org.telegram.tgnet.TLRPC$TL_upload_webFile r0 = r22.responseWeb     // Catch:{ Exception -> 0x0490 }
            org.telegram.tgnet.NativeByteBuffer r0 = r0.bytes     // Catch:{ Exception -> 0x0490 }
            goto L_0x0078
        L_0x006a:
            org.telegram.tgnet.TLRPC$TL_upload_cdnFile r0 = r22.responseCdn     // Catch:{ Exception -> 0x0490 }
            if (r0 == 0) goto L_0x0077
            org.telegram.tgnet.TLRPC$TL_upload_cdnFile r0 = r22.responseCdn     // Catch:{ Exception -> 0x0490 }
            org.telegram.tgnet.NativeByteBuffer r0 = r0.bytes     // Catch:{ Exception -> 0x0490 }
            goto L_0x0078
        L_0x0077:
            r0 = r10
        L_0x0078:
            if (r0 == 0) goto L_0x048c
            int r6 = r0.limit()     // Catch:{ Exception -> 0x0490 }
            if (r6 != 0) goto L_0x0082
            goto L_0x048c
        L_0x0082:
            int r6 = r0.limit()     // Catch:{ Exception -> 0x0490 }
            boolean r11 = r7.isCdn     // Catch:{ Exception -> 0x0490 }
            r12 = 131072(0x20000, float:1.83671E-40)
            if (r11 == 0) goto L_0x00aa
            int r11 = r22.offset     // Catch:{ Exception -> 0x0490 }
            int r11 = r11 / r12
            int r11 = r11 * r12
            android.util.SparseArray<org.telegram.tgnet.TLRPC$TL_fileHash> r13 = r7.cdnHashes     // Catch:{ Exception -> 0x0490 }
            if (r13 == 0) goto L_0x00a0
            android.util.SparseArray<org.telegram.tgnet.TLRPC$TL_fileHash> r13 = r7.cdnHashes     // Catch:{ Exception -> 0x0490 }
            java.lang.Object r13 = r13.get(r11)     // Catch:{ Exception -> 0x0490 }
            org.telegram.tgnet.TLRPC$TL_fileHash r13 = (org.telegram.tgnet.TLRPC.TL_fileHash) r13     // Catch:{ Exception -> 0x0490 }
            goto L_0x00a1
        L_0x00a0:
            r13 = r10
        L_0x00a1:
            if (r13 != 0) goto L_0x00aa
            r21.delayRequestInfo(r22)     // Catch:{ Exception -> 0x0490 }
            r7.requestFileOffsets(r11)     // Catch:{ Exception -> 0x0490 }
            return r8
        L_0x00aa:
            org.telegram.tgnet.TLRPC$TL_upload_cdnFile r11 = r22.responseCdn     // Catch:{ Exception -> 0x0490 }
            r13 = 12
            if (r11 == 0) goto L_0x00ed
            int r11 = r22.offset     // Catch:{ Exception -> 0x0490 }
            int r11 = r11 / 16
            byte[] r14 = r7.cdnIv     // Catch:{ Exception -> 0x0490 }
            r15 = 15
            r12 = r11 & 255(0xff, float:3.57E-43)
            byte r12 = (byte) r12     // Catch:{ Exception -> 0x0490 }
            r14[r15] = r12     // Catch:{ Exception -> 0x0490 }
            byte[] r12 = r7.cdnIv     // Catch:{ Exception -> 0x0490 }
            r14 = 14
            int r15 = r11 >> 8
            r15 = r15 & 255(0xff, float:3.57E-43)
            byte r15 = (byte) r15     // Catch:{ Exception -> 0x0490 }
            r12[r14] = r15     // Catch:{ Exception -> 0x0490 }
            byte[] r12 = r7.cdnIv     // Catch:{ Exception -> 0x0490 }
            r14 = 13
            int r15 = r11 >> 16
            r15 = r15 & 255(0xff, float:3.57E-43)
            byte r15 = (byte) r15     // Catch:{ Exception -> 0x0490 }
            r12[r14] = r15     // Catch:{ Exception -> 0x0490 }
            byte[] r12 = r7.cdnIv     // Catch:{ Exception -> 0x0490 }
            int r11 = r11 >> 24
            r11 = r11 & 255(0xff, float:3.57E-43)
            byte r11 = (byte) r11     // Catch:{ Exception -> 0x0490 }
            r12[r13] = r11     // Catch:{ Exception -> 0x0490 }
            java.nio.ByteBuffer r11 = r0.buffer     // Catch:{ Exception -> 0x0490 }
            byte[] r12 = r7.cdnKey     // Catch:{ Exception -> 0x0490 }
            byte[] r14 = r7.cdnIv     // Catch:{ Exception -> 0x0490 }
            int r15 = r0.limit()     // Catch:{ Exception -> 0x0490 }
            org.telegram.messenger.Utilities.aesCtrDecryption(r11, r12, r14, r9, r15)     // Catch:{ Exception -> 0x0490 }
        L_0x00ed:
            boolean r11 = r7.isPreloadVideoOperation     // Catch:{ Exception -> 0x0490 }
            if (r11 == 0) goto L_0x01f6
            java.io.RandomAccessFile r1 = r7.preloadStream     // Catch:{ Exception -> 0x0490 }
            int r5 = r22.offset     // Catch:{ Exception -> 0x0490 }
            r1.writeInt(r5)     // Catch:{ Exception -> 0x0490 }
            java.io.RandomAccessFile r1 = r7.preloadStream     // Catch:{ Exception -> 0x0490 }
            r1.writeInt(r6)     // Catch:{ Exception -> 0x0490 }
            int r1 = r7.preloadStreamFileOffset     // Catch:{ Exception -> 0x0490 }
            int r1 = r1 + 8
            r7.preloadStreamFileOffset = r1     // Catch:{ Exception -> 0x0490 }
            java.io.RandomAccessFile r1 = r7.preloadStream     // Catch:{ Exception -> 0x0490 }
            java.nio.channels.FileChannel r1 = r1.getChannel()     // Catch:{ Exception -> 0x0490 }
            java.nio.ByteBuffer r5 = r0.buffer     // Catch:{ Exception -> 0x0490 }
            r1.write(r5)     // Catch:{ Exception -> 0x0490 }
            boolean r1 = org.telegram.messenger.BuildVars.DEBUG_VERSION     // Catch:{ Exception -> 0x0490 }
            if (r1 == 0) goto L_0x013c
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0490 }
            r1.<init>()     // Catch:{ Exception -> 0x0490 }
            java.lang.String r5 = "save preload file part "
            r1.append(r5)     // Catch:{ Exception -> 0x0490 }
            java.io.File r5 = r7.cacheFilePreload     // Catch:{ Exception -> 0x0490 }
            r1.append(r5)     // Catch:{ Exception -> 0x0490 }
            r1.append(r2)     // Catch:{ Exception -> 0x0490 }
            int r2 = r22.offset     // Catch:{ Exception -> 0x0490 }
            r1.append(r2)     // Catch:{ Exception -> 0x0490 }
            java.lang.String r2 = " size "
            r1.append(r2)     // Catch:{ Exception -> 0x0490 }
            r1.append(r6)     // Catch:{ Exception -> 0x0490 }
            java.lang.String r1 = r1.toString()     // Catch:{ Exception -> 0x0490 }
            org.telegram.messenger.FileLog.d(r1)     // Catch:{ Exception -> 0x0490 }
        L_0x013c:
            android.util.SparseArray<org.telegram.messenger.FileLoadOperation$PreloadRange> r1 = r7.preloadedBytesRanges     // Catch:{ Exception -> 0x0490 }
            if (r1 != 0) goto L_0x0147
            android.util.SparseArray r1 = new android.util.SparseArray     // Catch:{ Exception -> 0x0490 }
            r1.<init>()     // Catch:{ Exception -> 0x0490 }
            r7.preloadedBytesRanges = r1     // Catch:{ Exception -> 0x0490 }
        L_0x0147:
            android.util.SparseArray<org.telegram.messenger.FileLoadOperation$PreloadRange> r1 = r7.preloadedBytesRanges     // Catch:{ Exception -> 0x0490 }
            int r2 = r22.offset     // Catch:{ Exception -> 0x0490 }
            org.telegram.messenger.FileLoadOperation$PreloadRange r5 = new org.telegram.messenger.FileLoadOperation$PreloadRange     // Catch:{ Exception -> 0x0490 }
            int r11 = r7.preloadStreamFileOffset     // Catch:{ Exception -> 0x0490 }
            r5.<init>(r11, r6)     // Catch:{ Exception -> 0x0490 }
            r1.put(r2, r5)     // Catch:{ Exception -> 0x0490 }
            int r1 = r7.totalPreloadedBytes     // Catch:{ Exception -> 0x0490 }
            int r1 = r1 + r6
            r7.totalPreloadedBytes = r1     // Catch:{ Exception -> 0x0490 }
            int r1 = r7.preloadStreamFileOffset     // Catch:{ Exception -> 0x0490 }
            int r1 = r1 + r6
            r7.preloadStreamFileOffset = r1     // Catch:{ Exception -> 0x0490 }
            int r1 = r7.moovFound     // Catch:{ Exception -> 0x0490 }
            if (r1 != 0) goto L_0x01a3
            int r1 = r7.nextAtomOffset     // Catch:{ Exception -> 0x0490 }
            int r2 = r22.offset     // Catch:{ Exception -> 0x0490 }
            int r0 = r7.findNextPreloadDownloadOffset(r1, r2, r0)     // Catch:{ Exception -> 0x0490 }
            if (r0 >= 0) goto L_0x0197
            int r0 = r0 * -1
            int r1 = r7.nextPreloadDownloadOffset     // Catch:{ Exception -> 0x0490 }
            int r2 = r7.currentDownloadChunkSize     // Catch:{ Exception -> 0x0490 }
            int r1 = r1 + r2
            r7.nextPreloadDownloadOffset = r1     // Catch:{ Exception -> 0x0490 }
            int r1 = r7.nextPreloadDownloadOffset     // Catch:{ Exception -> 0x0490 }
            int r2 = r7.totalBytesCount     // Catch:{ Exception -> 0x0490 }
            int r2 = r2 / r4
            if (r1 >= r2) goto L_0x018b
            r1 = 1048576(0x100000, float:1.469368E-39)
            int r1 = r1 + r0
            r7.foundMoovSize = r1     // Catch:{ Exception -> 0x0490 }
            r7.preloadNotRequestedBytesCount = r1     // Catch:{ Exception -> 0x0490 }
            r7.moovFound = r8     // Catch:{ Exception -> 0x0490 }
            goto L_0x0193
        L_0x018b:
            r1 = 2097152(0x200000, float:2.938736E-39)
            r7.foundMoovSize = r1     // Catch:{ Exception -> 0x0490 }
            r7.preloadNotRequestedBytesCount = r1     // Catch:{ Exception -> 0x0490 }
            r7.moovFound = r4     // Catch:{ Exception -> 0x0490 }
        L_0x0193:
            r1 = -1
            r7.nextPreloadDownloadOffset = r1     // Catch:{ Exception -> 0x0490 }
            goto L_0x01a1
        L_0x0197:
            int r1 = r7.currentDownloadChunkSize     // Catch:{ Exception -> 0x0490 }
            int r1 = r0 / r1
            int r2 = r7.currentDownloadChunkSize     // Catch:{ Exception -> 0x0490 }
            int r1 = r1 * r2
            r7.nextPreloadDownloadOffset = r1     // Catch:{ Exception -> 0x0490 }
        L_0x01a1:
            r7.nextAtomOffset = r0     // Catch:{ Exception -> 0x0490 }
        L_0x01a3:
            java.io.RandomAccessFile r0 = r7.preloadStream     // Catch:{ Exception -> 0x0490 }
            int r1 = r7.foundMoovSize     // Catch:{ Exception -> 0x0490 }
            r0.writeInt(r1)     // Catch:{ Exception -> 0x0490 }
            java.io.RandomAccessFile r0 = r7.preloadStream     // Catch:{ Exception -> 0x0490 }
            int r1 = r7.nextPreloadDownloadOffset     // Catch:{ Exception -> 0x0490 }
            r0.writeInt(r1)     // Catch:{ Exception -> 0x0490 }
            java.io.RandomAccessFile r0 = r7.preloadStream     // Catch:{ Exception -> 0x0490 }
            int r1 = r7.nextAtomOffset     // Catch:{ Exception -> 0x0490 }
            r0.writeInt(r1)     // Catch:{ Exception -> 0x0490 }
            int r0 = r7.preloadStreamFileOffset     // Catch:{ Exception -> 0x0490 }
            int r0 = r0 + r13
            r7.preloadStreamFileOffset = r0     // Catch:{ Exception -> 0x0490 }
            int r0 = r7.nextPreloadDownloadOffset     // Catch:{ Exception -> 0x0490 }
            if (r0 == 0) goto L_0x01d8
            int r0 = r7.moovFound     // Catch:{ Exception -> 0x0490 }
            if (r0 == 0) goto L_0x01c9
            int r0 = r7.foundMoovSize     // Catch:{ Exception -> 0x0490 }
            if (r0 < 0) goto L_0x01d8
        L_0x01c9:
            int r0 = r7.totalPreloadedBytes     // Catch:{ Exception -> 0x0490 }
            r1 = 2097152(0x200000, float:2.938736E-39)
            if (r0 > r1) goto L_0x01d8
            int r0 = r7.nextPreloadDownloadOffset     // Catch:{ Exception -> 0x0490 }
            int r1 = r7.totalBytesCount     // Catch:{ Exception -> 0x0490 }
            if (r0 < r1) goto L_0x01d6
            goto L_0x01d8
        L_0x01d6:
            r0 = 0
            goto L_0x01d9
        L_0x01d8:
            r0 = 1
        L_0x01d9:
            if (r0 == 0) goto L_0x01e9
            java.io.RandomAccessFile r1 = r7.preloadStream     // Catch:{ Exception -> 0x0490 }
            r2 = 0
            r1.seek(r2)     // Catch:{ Exception -> 0x0490 }
            java.io.RandomAccessFile r1 = r7.preloadStream     // Catch:{ Exception -> 0x0490 }
            r1.write(r8)     // Catch:{ Exception -> 0x0490 }
            goto L_0x0419
        L_0x01e9:
            int r1 = r7.moovFound     // Catch:{ Exception -> 0x0490 }
            if (r1 == 0) goto L_0x0419
            int r1 = r7.foundMoovSize     // Catch:{ Exception -> 0x0490 }
            int r2 = r7.currentDownloadChunkSize     // Catch:{ Exception -> 0x0490 }
            int r1 = r1 - r2
            r7.foundMoovSize = r1     // Catch:{ Exception -> 0x0490 }
            goto L_0x0419
        L_0x01f6:
            int r4 = r7.downloadedBytes     // Catch:{ Exception -> 0x0490 }
            int r4 = r4 + r6
            r7.downloadedBytes = r4     // Catch:{ Exception -> 0x0490 }
            int r4 = r7.totalBytesCount     // Catch:{ Exception -> 0x0490 }
            if (r4 <= 0) goto L_0x0206
            int r4 = r7.downloadedBytes     // Catch:{ Exception -> 0x0490 }
            int r11 = r7.totalBytesCount     // Catch:{ Exception -> 0x0490 }
            if (r4 < r11) goto L_0x0222
            goto L_0x0224
        L_0x0206:
            int r4 = r7.currentDownloadChunkSize     // Catch:{ Exception -> 0x0490 }
            if (r6 != r4) goto L_0x0224
            int r4 = r7.totalBytesCount     // Catch:{ Exception -> 0x0490 }
            int r11 = r7.downloadedBytes     // Catch:{ Exception -> 0x0490 }
            if (r4 == r11) goto L_0x0217
            int r4 = r7.downloadedBytes     // Catch:{ Exception -> 0x0490 }
            int r11 = r7.currentDownloadChunkSize     // Catch:{ Exception -> 0x0490 }
            int r4 = r4 % r11
            if (r4 == 0) goto L_0x0222
        L_0x0217:
            int r4 = r7.totalBytesCount     // Catch:{ Exception -> 0x0490 }
            if (r4 <= 0) goto L_0x0224
            int r4 = r7.totalBytesCount     // Catch:{ Exception -> 0x0490 }
            int r11 = r7.downloadedBytes     // Catch:{ Exception -> 0x0490 }
            if (r4 > r11) goto L_0x0222
            goto L_0x0224
        L_0x0222:
            r4 = 0
            goto L_0x0225
        L_0x0224:
            r4 = 1
        L_0x0225:
            r11 = r4
            byte[] r4 = r7.key     // Catch:{ Exception -> 0x0490 }
            if (r4 == 0) goto L_0x024f
            java.nio.ByteBuffer r14 = r0.buffer     // Catch:{ Exception -> 0x0490 }
            byte[] r15 = r7.key     // Catch:{ Exception -> 0x0490 }
            byte[] r4 = r7.iv     // Catch:{ Exception -> 0x0490 }
            r17 = 0
            r18 = 1
            r19 = 0
            int r20 = r0.limit()     // Catch:{ Exception -> 0x0490 }
            r16 = r4
            org.telegram.messenger.Utilities.aesIgeEncryption(r14, r15, r16, r17, r18, r19, r20)     // Catch:{ Exception -> 0x0490 }
            if (r11 == 0) goto L_0x024f
            int r4 = r7.bytesCountPadding     // Catch:{ Exception -> 0x0490 }
            if (r4 == 0) goto L_0x024f
            int r4 = r0.limit()     // Catch:{ Exception -> 0x0490 }
            int r12 = r7.bytesCountPadding     // Catch:{ Exception -> 0x0490 }
            int r4 = r4 - r12
            r0.limit(r4)     // Catch:{ Exception -> 0x0490 }
        L_0x024f:
            boolean r4 = r7.encryptFile     // Catch:{ Exception -> 0x0490 }
            if (r4 == 0) goto L_0x028e
            int r4 = r22.offset     // Catch:{ Exception -> 0x0490 }
            int r4 = r4 / 16
            byte[] r12 = r7.encryptIv     // Catch:{ Exception -> 0x0490 }
            r14 = 15
            r15 = r4 & 255(0xff, float:3.57E-43)
            byte r15 = (byte) r15     // Catch:{ Exception -> 0x0490 }
            r12[r14] = r15     // Catch:{ Exception -> 0x0490 }
            byte[] r12 = r7.encryptIv     // Catch:{ Exception -> 0x0490 }
            r14 = 14
            int r15 = r4 >> 8
            r15 = r15 & 255(0xff, float:3.57E-43)
            byte r15 = (byte) r15     // Catch:{ Exception -> 0x0490 }
            r12[r14] = r15     // Catch:{ Exception -> 0x0490 }
            byte[] r12 = r7.encryptIv     // Catch:{ Exception -> 0x0490 }
            r14 = 13
            int r15 = r4 >> 16
            r15 = r15 & 255(0xff, float:3.57E-43)
            byte r15 = (byte) r15     // Catch:{ Exception -> 0x0490 }
            r12[r14] = r15     // Catch:{ Exception -> 0x0490 }
            byte[] r12 = r7.encryptIv     // Catch:{ Exception -> 0x0490 }
            int r4 = r4 >> 24
            r4 = r4 & 255(0xff, float:3.57E-43)
            byte r4 = (byte) r4     // Catch:{ Exception -> 0x0490 }
            r12[r13] = r4     // Catch:{ Exception -> 0x0490 }
            java.nio.ByteBuffer r4 = r0.buffer     // Catch:{ Exception -> 0x0490 }
            byte[] r12 = r7.encryptKey     // Catch:{ Exception -> 0x0490 }
            byte[] r13 = r7.encryptIv     // Catch:{ Exception -> 0x0490 }
            int r14 = r0.limit()     // Catch:{ Exception -> 0x0490 }
            org.telegram.messenger.Utilities.aesCtrDecryption(r4, r12, r13, r9, r14)     // Catch:{ Exception -> 0x0490 }
        L_0x028e:
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$Range> r4 = r7.notLoadedBytesRanges     // Catch:{ Exception -> 0x0490 }
            if (r4 == 0) goto L_0x02c0
            java.io.RandomAccessFile r4 = r7.fileOutputStream     // Catch:{ Exception -> 0x0490 }
            int r12 = r22.offset     // Catch:{ Exception -> 0x0490 }
            long r12 = (long) r12     // Catch:{ Exception -> 0x0490 }
            r4.seek(r12)     // Catch:{ Exception -> 0x0490 }
            boolean r4 = org.telegram.messenger.BuildVars.DEBUG_VERSION     // Catch:{ Exception -> 0x0490 }
            if (r4 == 0) goto L_0x02c0
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0490 }
            r4.<init>()     // Catch:{ Exception -> 0x0490 }
            java.lang.String r12 = "save file part "
            r4.append(r12)     // Catch:{ Exception -> 0x0490 }
            java.io.File r12 = r7.cacheFileFinal     // Catch:{ Exception -> 0x0490 }
            r4.append(r12)     // Catch:{ Exception -> 0x0490 }
            r4.append(r2)     // Catch:{ Exception -> 0x0490 }
            int r2 = r22.offset     // Catch:{ Exception -> 0x0490 }
            r4.append(r2)     // Catch:{ Exception -> 0x0490 }
            java.lang.String r2 = r4.toString()     // Catch:{ Exception -> 0x0490 }
            org.telegram.messenger.FileLog.d(r2)     // Catch:{ Exception -> 0x0490 }
        L_0x02c0:
            java.io.RandomAccessFile r2 = r7.fileOutputStream     // Catch:{ Exception -> 0x0490 }
            java.nio.channels.FileChannel r2 = r2.getChannel()     // Catch:{ Exception -> 0x0490 }
            java.nio.ByteBuffer r0 = r0.buffer     // Catch:{ Exception -> 0x0490 }
            r2.write(r0)     // Catch:{ Exception -> 0x0490 }
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$Range> r0 = r7.notLoadedBytesRanges     // Catch:{ Exception -> 0x0490 }
            int r2 = r22.offset     // Catch:{ Exception -> 0x0490 }
            int r4 = r22.offset     // Catch:{ Exception -> 0x0490 }
            int r4 = r4 + r6
            r7.addPart(r0, r2, r4, r8)     // Catch:{ Exception -> 0x0490 }
            boolean r0 = r7.isCdn     // Catch:{ Exception -> 0x0490 }
            if (r0 == 0) goto L_0x03ee
            int r0 = r22.offset     // Catch:{ Exception -> 0x0490 }
            r2 = 131072(0x20000, float:1.83671E-40)
            int r0 = r0 / r2
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$Range> r2 = r7.notCheckedCdnRanges     // Catch:{ Exception -> 0x0490 }
            int r2 = r2.size()     // Catch:{ Exception -> 0x0490 }
            r3 = 0
        L_0x02eb:
            if (r3 >= r2) goto L_0x0306
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$Range> r4 = r7.notCheckedCdnRanges     // Catch:{ Exception -> 0x0490 }
            java.lang.Object r4 = r4.get(r3)     // Catch:{ Exception -> 0x0490 }
            org.telegram.messenger.FileLoadOperation$Range r4 = (org.telegram.messenger.FileLoadOperation.Range) r4     // Catch:{ Exception -> 0x0490 }
            int r6 = r4.start     // Catch:{ Exception -> 0x0490 }
            if (r6 > r0) goto L_0x0303
            int r4 = r4.end     // Catch:{ Exception -> 0x0490 }
            if (r0 > r4) goto L_0x0303
            r2 = 0
            goto L_0x0307
        L_0x0303:
            int r3 = r3 + 1
            goto L_0x02eb
        L_0x0306:
            r2 = 1
        L_0x0307:
            if (r2 != 0) goto L_0x03ee
            r2 = 131072(0x20000, float:1.83671E-40)
            int r12 = r0 * r2
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$Range> r3 = r7.notLoadedBytesRanges     // Catch:{ Exception -> 0x0490 }
            int r3 = r7.getDownloadedLengthFromOffsetInternal(r3, r12, r2)     // Catch:{ Exception -> 0x0490 }
            if (r3 == 0) goto L_0x03ee
            if (r3 == r2) goto L_0x0326
            int r2 = r7.totalBytesCount     // Catch:{ Exception -> 0x0490 }
            if (r2 <= 0) goto L_0x0320
            int r2 = r7.totalBytesCount     // Catch:{ Exception -> 0x0490 }
            int r2 = r2 - r12
            if (r3 == r2) goto L_0x0326
        L_0x0320:
            int r2 = r7.totalBytesCount     // Catch:{ Exception -> 0x0490 }
            if (r2 > 0) goto L_0x03ee
            if (r11 == 0) goto L_0x03ee
        L_0x0326:
            android.util.SparseArray<org.telegram.tgnet.TLRPC$TL_fileHash> r2 = r7.cdnHashes     // Catch:{ Exception -> 0x0490 }
            java.lang.Object r2 = r2.get(r12)     // Catch:{ Exception -> 0x0490 }
            org.telegram.tgnet.TLRPC$TL_fileHash r2 = (org.telegram.tgnet.TLRPC.TL_fileHash) r2     // Catch:{ Exception -> 0x0490 }
            java.io.RandomAccessFile r4 = r7.fileReadStream     // Catch:{ Exception -> 0x0490 }
            if (r4 != 0) goto L_0x0343
            r4 = 131072(0x20000, float:1.83671E-40)
            byte[] r4 = new byte[r4]     // Catch:{ Exception -> 0x0490 }
            r7.cdnCheckBytes = r4     // Catch:{ Exception -> 0x0490 }
            java.io.RandomAccessFile r4 = new java.io.RandomAccessFile     // Catch:{ Exception -> 0x0490 }
            java.io.File r6 = r7.cacheFileTemp     // Catch:{ Exception -> 0x0490 }
            java.lang.String r13 = "r"
            r4.<init>(r6, r13)     // Catch:{ Exception -> 0x0490 }
            r7.fileReadStream = r4     // Catch:{ Exception -> 0x0490 }
        L_0x0343:
            java.io.RandomAccessFile r4 = r7.fileReadStream     // Catch:{ Exception -> 0x0490 }
            long r13 = (long) r12     // Catch:{ Exception -> 0x0490 }
            r4.seek(r13)     // Catch:{ Exception -> 0x0490 }
            java.io.RandomAccessFile r4 = r7.fileReadStream     // Catch:{ Exception -> 0x0490 }
            byte[] r6 = r7.cdnCheckBytes     // Catch:{ Exception -> 0x0490 }
            r4.readFully(r6, r9, r3)     // Catch:{ Exception -> 0x0490 }
            byte[] r4 = r7.cdnCheckBytes     // Catch:{ Exception -> 0x0490 }
            byte[] r3 = org.telegram.messenger.Utilities.computeSHA256(r4, r9, r3)     // Catch:{ Exception -> 0x0490 }
            byte[] r2 = r2.hash     // Catch:{ Exception -> 0x0490 }
            boolean r2 = java.util.Arrays.equals(r3, r2)     // Catch:{ Exception -> 0x0490 }
            if (r2 != 0) goto L_0x03e2
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x0490 }
            if (r0 == 0) goto L_0x03d9
            org.telegram.tgnet.TLRPC$InputFileLocation r0 = r7.location     // Catch:{ Exception -> 0x0490 }
            if (r0 == 0) goto L_0x03b5
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0490 }
            r0.<init>()     // Catch:{ Exception -> 0x0490 }
            java.lang.String r2 = "invalid cdn hash "
            r0.append(r2)     // Catch:{ Exception -> 0x0490 }
            org.telegram.tgnet.TLRPC$InputFileLocation r2 = r7.location     // Catch:{ Exception -> 0x0490 }
            r0.append(r2)     // Catch:{ Exception -> 0x0490 }
            r0.append(r5)     // Catch:{ Exception -> 0x0490 }
            org.telegram.tgnet.TLRPC$InputFileLocation r2 = r7.location     // Catch:{ Exception -> 0x0490 }
            long r2 = r2.id     // Catch:{ Exception -> 0x0490 }
            r0.append(r2)     // Catch:{ Exception -> 0x0490 }
            r0.append(r1)     // Catch:{ Exception -> 0x0490 }
            org.telegram.tgnet.TLRPC$InputFileLocation r1 = r7.location     // Catch:{ Exception -> 0x0490 }
            int r1 = r1.local_id     // Catch:{ Exception -> 0x0490 }
            r0.append(r1)     // Catch:{ Exception -> 0x0490 }
            java.lang.String r1 = " access_hash = "
            r0.append(r1)     // Catch:{ Exception -> 0x0490 }
            org.telegram.tgnet.TLRPC$InputFileLocation r1 = r7.location     // Catch:{ Exception -> 0x0490 }
            long r1 = r1.access_hash     // Catch:{ Exception -> 0x0490 }
            r0.append(r1)     // Catch:{ Exception -> 0x0490 }
            java.lang.String r1 = " volume_id = "
            r0.append(r1)     // Catch:{ Exception -> 0x0490 }
            org.telegram.tgnet.TLRPC$InputFileLocation r1 = r7.location     // Catch:{ Exception -> 0x0490 }
            long r1 = r1.volume_id     // Catch:{ Exception -> 0x0490 }
            r0.append(r1)     // Catch:{ Exception -> 0x0490 }
            java.lang.String r1 = " secret = "
            r0.append(r1)     // Catch:{ Exception -> 0x0490 }
            org.telegram.tgnet.TLRPC$InputFileLocation r1 = r7.location     // Catch:{ Exception -> 0x0490 }
            long r1 = r1.secret     // Catch:{ Exception -> 0x0490 }
            r0.append(r1)     // Catch:{ Exception -> 0x0490 }
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x0490 }
            org.telegram.messenger.FileLog.e((java.lang.String) r0)     // Catch:{ Exception -> 0x0490 }
            goto L_0x03d9
        L_0x03b5:
            org.telegram.tgnet.TLRPC$InputWebFileLocation r0 = r7.webLocation     // Catch:{ Exception -> 0x0490 }
            if (r0 == 0) goto L_0x03d9
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0490 }
            r0.<init>()     // Catch:{ Exception -> 0x0490 }
            java.lang.String r1 = "invalid cdn hash  "
            r0.append(r1)     // Catch:{ Exception -> 0x0490 }
            org.telegram.tgnet.TLRPC$InputWebFileLocation r1 = r7.webLocation     // Catch:{ Exception -> 0x0490 }
            r0.append(r1)     // Catch:{ Exception -> 0x0490 }
            r0.append(r5)     // Catch:{ Exception -> 0x0490 }
            java.lang.String r1 = r21.getFileName()     // Catch:{ Exception -> 0x0490 }
            r0.append(r1)     // Catch:{ Exception -> 0x0490 }
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x0490 }
            org.telegram.messenger.FileLog.e((java.lang.String) r0)     // Catch:{ Exception -> 0x0490 }
        L_0x03d9:
            r7.onFail(r9, r9)     // Catch:{ Exception -> 0x0490 }
            java.io.File r0 = r7.cacheFileTemp     // Catch:{ Exception -> 0x0490 }
            r0.delete()     // Catch:{ Exception -> 0x0490 }
            return r9
        L_0x03e2:
            android.util.SparseArray<org.telegram.tgnet.TLRPC$TL_fileHash> r1 = r7.cdnHashes     // Catch:{ Exception -> 0x0490 }
            r1.remove(r12)     // Catch:{ Exception -> 0x0490 }
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$Range> r1 = r7.notCheckedCdnRanges     // Catch:{ Exception -> 0x0490 }
            int r2 = r0 + 1
            r7.addPart(r1, r0, r2, r9)     // Catch:{ Exception -> 0x0490 }
        L_0x03ee:
            java.io.RandomAccessFile r0 = r7.fiv     // Catch:{ Exception -> 0x0490 }
            if (r0 == 0) goto L_0x0400
            java.io.RandomAccessFile r0 = r7.fiv     // Catch:{ Exception -> 0x0490 }
            r1 = 0
            r0.seek(r1)     // Catch:{ Exception -> 0x0490 }
            java.io.RandomAccessFile r0 = r7.fiv     // Catch:{ Exception -> 0x0490 }
            byte[] r1 = r7.iv     // Catch:{ Exception -> 0x0490 }
            r0.write(r1)     // Catch:{ Exception -> 0x0490 }
        L_0x0400:
            int r0 = r7.totalBytesCount     // Catch:{ Exception -> 0x0490 }
            if (r0 <= 0) goto L_0x0418
            int r0 = r7.state     // Catch:{ Exception -> 0x0490 }
            if (r0 != r8) goto L_0x0418
            r21.copyNotLoadedRanges()     // Catch:{ Exception -> 0x0490 }
            org.telegram.messenger.FileLoadOperation$FileLoadOperationDelegate r1 = r7.delegate     // Catch:{ Exception -> 0x0490 }
            int r0 = r7.downloadedBytes     // Catch:{ Exception -> 0x0490 }
            long r3 = (long) r0     // Catch:{ Exception -> 0x0490 }
            int r0 = r7.totalBytesCount     // Catch:{ Exception -> 0x0490 }
            long r5 = (long) r0     // Catch:{ Exception -> 0x0490 }
            r2 = r21
            r1.didChangedLoadProgress(r2, r3, r5)     // Catch:{ Exception -> 0x0490 }
        L_0x0418:
            r0 = r11
        L_0x0419:
            r1 = 0
        L_0x041a:
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$RequestInfo> r2 = r7.delayedRequestInfos     // Catch:{ Exception -> 0x0490 }
            int r2 = r2.size()     // Catch:{ Exception -> 0x0490 }
            if (r1 >= r2) goto L_0x0480
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$RequestInfo> r2 = r7.delayedRequestInfos     // Catch:{ Exception -> 0x0490 }
            java.lang.Object r2 = r2.get(r1)     // Catch:{ Exception -> 0x0490 }
            org.telegram.messenger.FileLoadOperation$RequestInfo r2 = (org.telegram.messenger.FileLoadOperation.RequestInfo) r2     // Catch:{ Exception -> 0x0490 }
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$Range> r3 = r7.notLoadedBytesRanges     // Catch:{ Exception -> 0x0490 }
            if (r3 != 0) goto L_0x043a
            int r3 = r7.downloadedBytes     // Catch:{ Exception -> 0x0490 }
            int r4 = r2.offset     // Catch:{ Exception -> 0x0490 }
            if (r3 != r4) goto L_0x0437
            goto L_0x043a
        L_0x0437:
            int r1 = r1 + 1
            goto L_0x041a
        L_0x043a:
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$RequestInfo> r3 = r7.delayedRequestInfos     // Catch:{ Exception -> 0x0490 }
            r3.remove(r1)     // Catch:{ Exception -> 0x0490 }
            boolean r1 = r7.processRequestResult(r2, r10)     // Catch:{ Exception -> 0x0490 }
            if (r1 != 0) goto L_0x0480
            org.telegram.tgnet.TLRPC$TL_upload_file r1 = r2.response     // Catch:{ Exception -> 0x0490 }
            if (r1 == 0) goto L_0x0459
            org.telegram.tgnet.TLRPC$TL_upload_file r1 = r2.response     // Catch:{ Exception -> 0x0490 }
            r1.disableFree = r9     // Catch:{ Exception -> 0x0490 }
            org.telegram.tgnet.TLRPC$TL_upload_file r1 = r2.response     // Catch:{ Exception -> 0x0490 }
            r1.freeResources()     // Catch:{ Exception -> 0x0490 }
            goto L_0x0480
        L_0x0459:
            org.telegram.tgnet.TLRPC$TL_upload_webFile r1 = r2.responseWeb     // Catch:{ Exception -> 0x0490 }
            if (r1 == 0) goto L_0x046d
            org.telegram.tgnet.TLRPC$TL_upload_webFile r1 = r2.responseWeb     // Catch:{ Exception -> 0x0490 }
            r1.disableFree = r9     // Catch:{ Exception -> 0x0490 }
            org.telegram.tgnet.TLRPC$TL_upload_webFile r1 = r2.responseWeb     // Catch:{ Exception -> 0x0490 }
            r1.freeResources()     // Catch:{ Exception -> 0x0490 }
            goto L_0x0480
        L_0x046d:
            org.telegram.tgnet.TLRPC$TL_upload_cdnFile r1 = r2.responseCdn     // Catch:{ Exception -> 0x0490 }
            if (r1 == 0) goto L_0x0480
            org.telegram.tgnet.TLRPC$TL_upload_cdnFile r1 = r2.responseCdn     // Catch:{ Exception -> 0x0490 }
            r1.disableFree = r9     // Catch:{ Exception -> 0x0490 }
            org.telegram.tgnet.TLRPC$TL_upload_cdnFile r1 = r2.responseCdn     // Catch:{ Exception -> 0x0490 }
            r1.freeResources()     // Catch:{ Exception -> 0x0490 }
        L_0x0480:
            if (r0 == 0) goto L_0x0487
            r7.onFinishLoadingFile(r8)     // Catch:{ Exception -> 0x0490 }
            goto L_0x058d
        L_0x0487:
            r21.startDownloadRequest()     // Catch:{ Exception -> 0x0490 }
            goto L_0x058d
        L_0x048c:
            r7.onFinishLoadingFile(r8)     // Catch:{ Exception -> 0x0490 }
            return r9
        L_0x0490:
            r0 = move-exception
            r7.onFail(r9, r9)
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x058d
        L_0x0499:
            java.lang.String r2 = r0.text
            java.lang.String r3 = "FILE_MIGRATE_"
            boolean r2 = r2.contains(r3)
            if (r2 == 0) goto L_0x04d3
            java.lang.String r0 = r0.text
            java.lang.String r1 = ""
            java.lang.String r0 = r0.replace(r3, r1)
            java.util.Scanner r2 = new java.util.Scanner
            r2.<init>(r0)
            r2.useDelimiter(r1)
            int r0 = r2.nextInt()     // Catch:{ Exception -> 0x04bc }
            java.lang.Integer r10 = java.lang.Integer.valueOf(r0)     // Catch:{ Exception -> 0x04bc }
            goto L_0x04bd
        L_0x04bc:
        L_0x04bd:
            if (r10 != 0) goto L_0x04c4
            r7.onFail(r9, r9)
            goto L_0x058d
        L_0x04c4:
            int r0 = r10.intValue()
            r7.datacenterId = r0
            r7.downloadedBytes = r9
            r7.requestedBytesCount = r9
            r21.startDownloadRequest()
            goto L_0x058d
        L_0x04d3:
            java.lang.String r2 = r0.text
            java.lang.String r3 = "OFFSET_INVALID"
            boolean r2 = r2.contains(r3)
            if (r2 == 0) goto L_0x04f8
            int r0 = r7.downloadedBytes
            int r1 = r7.currentDownloadChunkSize
            int r0 = r0 % r1
            if (r0 != 0) goto L_0x04f3
            r7.onFinishLoadingFile(r8)     // Catch:{ Exception -> 0x04e9 }
            goto L_0x058d
        L_0x04e9:
            r0 = move-exception
            r1 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)
            r7.onFail(r9, r9)
            goto L_0x058d
        L_0x04f3:
            r7.onFail(r9, r9)
            goto L_0x058d
        L_0x04f8:
            java.lang.String r2 = r0.text
            java.lang.String r3 = "RETRY_LIMIT"
            boolean r2 = r2.contains(r3)
            if (r2 == 0) goto L_0x0507
            r7.onFail(r9, r4)
            goto L_0x058d
        L_0x0507:
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r2 == 0) goto L_0x058a
            org.telegram.tgnet.TLRPC$InputFileLocation r2 = r7.location
            java.lang.String r3 = " "
            if (r2 == 0) goto L_0x0563
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r0 = r0.text
            r2.append(r0)
            r2.append(r3)
            org.telegram.tgnet.TLRPC$InputFileLocation r0 = r7.location
            r2.append(r0)
            r2.append(r5)
            org.telegram.tgnet.TLRPC$InputFileLocation r0 = r7.location
            long r3 = r0.id
            r2.append(r3)
            r2.append(r1)
            org.telegram.tgnet.TLRPC$InputFileLocation r0 = r7.location
            int r0 = r0.local_id
            r2.append(r0)
            java.lang.String r0 = " access_hash = "
            r2.append(r0)
            org.telegram.tgnet.TLRPC$InputFileLocation r0 = r7.location
            long r0 = r0.access_hash
            r2.append(r0)
            java.lang.String r0 = " volume_id = "
            r2.append(r0)
            org.telegram.tgnet.TLRPC$InputFileLocation r0 = r7.location
            long r0 = r0.volume_id
            r2.append(r0)
            java.lang.String r0 = " secret = "
            r2.append(r0)
            org.telegram.tgnet.TLRPC$InputFileLocation r0 = r7.location
            long r0 = r0.secret
            r2.append(r0)
            java.lang.String r0 = r2.toString()
            org.telegram.messenger.FileLog.e((java.lang.String) r0)
            goto L_0x058a
        L_0x0563:
            org.telegram.tgnet.TLRPC$InputWebFileLocation r1 = r7.webLocation
            if (r1 == 0) goto L_0x058a
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r0 = r0.text
            r1.append(r0)
            r1.append(r3)
            org.telegram.tgnet.TLRPC$InputWebFileLocation r0 = r7.webLocation
            r1.append(r0)
            r1.append(r5)
            java.lang.String r0 = r21.getFileName()
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            org.telegram.messenger.FileLog.e((java.lang.String) r0)
        L_0x058a:
            r7.onFail(r9, r9)
        L_0x058d:
            return r9
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.FileLoadOperation.processRequestResult(org.telegram.messenger.FileLoadOperation$RequestInfo, org.telegram.tgnet.TLRPC$TL_error):boolean");
    }

    /* access modifiers changed from: protected */
    public void onFail(boolean z, int i) {
        cleanup();
        this.state = 2;
        if (z) {
            Utilities.stageQueue.postRunnable(new Runnable(i) {
                private final /* synthetic */ int f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    FileLoadOperation.this.lambda$onFail$10$FileLoadOperation(this.f$1);
                }
            });
        } else {
            this.delegate.didFailedLoadingFile(this, i);
        }
    }

    public /* synthetic */ void lambda$onFail$10$FileLoadOperation(int i) {
        this.delegate.didFailedLoadingFile(this, i);
    }

    private void clearOperaion(RequestInfo requestInfo, boolean z) {
        int i = Integer.MAX_VALUE;
        for (int i2 = 0; i2 < this.requestInfos.size(); i2++) {
            RequestInfo requestInfo2 = this.requestInfos.get(i2);
            i = Math.min(requestInfo2.offset, i);
            if (this.isPreloadVideoOperation) {
                this.requestedPreloadedBytesRanges.delete(requestInfo2.offset);
            } else {
                removePart(this.notRequestedBytesRanges, requestInfo2.offset, requestInfo2.offset + this.currentDownloadChunkSize);
            }
            if (!(requestInfo == requestInfo2 || requestInfo2.requestToken == 0)) {
                ConnectionsManager.getInstance(this.currentAccount).cancelRequest(requestInfo2.requestToken, true);
            }
        }
        this.requestInfos.clear();
        for (int i3 = 0; i3 < this.delayedRequestInfos.size(); i3++) {
            RequestInfo requestInfo3 = this.delayedRequestInfos.get(i3);
            if (this.isPreloadVideoOperation) {
                this.requestedPreloadedBytesRanges.delete(requestInfo3.offset);
            } else {
                removePart(this.notRequestedBytesRanges, requestInfo3.offset, requestInfo3.offset + this.currentDownloadChunkSize);
            }
            if (requestInfo3.response != null) {
                requestInfo3.response.disableFree = false;
                requestInfo3.response.freeResources();
            } else if (requestInfo3.responseWeb != null) {
                requestInfo3.responseWeb.disableFree = false;
                requestInfo3.responseWeb.freeResources();
            } else if (requestInfo3.responseCdn != null) {
                requestInfo3.responseCdn.disableFree = false;
                requestInfo3.responseCdn.freeResources();
            }
            i = Math.min(requestInfo3.offset, i);
        }
        this.delayedRequestInfos.clear();
        this.requestsCount = 0;
        if (!z && this.isPreloadVideoOperation) {
            this.requestedBytesCount = this.totalPreloadedBytes;
        } else if (this.notLoadedBytesRanges == null) {
            this.downloadedBytes = i;
            this.requestedBytesCount = i;
        }
    }

    private void requestReference(RequestInfo requestInfo) {
        TLRPC.WebPage webPage;
        if (!this.requestingReference) {
            clearOperaion(requestInfo, false);
            this.requestingReference = true;
            Object obj = this.parentObject;
            if (obj instanceof MessageObject) {
                MessageObject messageObject = (MessageObject) obj;
                if (messageObject.getId() < 0 && (webPage = messageObject.messageOwner.media.webpage) != null) {
                    this.parentObject = webPage;
                }
            }
            FileRefController.getInstance(this.currentAccount).requestReference(this.parentObject, this.location, this, requestInfo);
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v8, resolved type: org.telegram.tgnet.TLRPC$TL_upload_getWebFile} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v9, resolved type: org.telegram.tgnet.TLRPC$TL_upload_getFile} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v18, resolved type: org.telegram.tgnet.TLRPC$TL_upload_getCdnFile} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v39, resolved type: org.telegram.tgnet.TLRPC$TL_upload_getFile} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v40, resolved type: org.telegram.tgnet.TLRPC$TL_upload_getFile} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v41, resolved type: org.telegram.tgnet.TLRPC$TL_upload_getFile} */
    /* access modifiers changed from: protected */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void startDownloadRequest() {
        /*
            r19 = this;
            r0 = r19
            boolean r1 = r0.paused
            if (r1 != 0) goto L_0x028b
            boolean r1 = r0.reuploadingCdn
            if (r1 != 0) goto L_0x028b
            int r1 = r0.state
            r2 = 1
            if (r1 != r2) goto L_0x028b
            int r1 = r0.streamPriorityStartOffset
            r3 = 2097152(0x200000, float:2.938736E-39)
            if (r1 != 0) goto L_0x0040
            boolean r1 = r0.nextPartWasPreloaded
            if (r1 != 0) goto L_0x002a
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$RequestInfo> r1 = r0.requestInfos
            int r1 = r1.size()
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$RequestInfo> r4 = r0.delayedRequestInfos
            int r4 = r4.size()
            int r1 = r1 + r4
            int r4 = r0.currentMaxDownloadRequests
            if (r1 >= r4) goto L_0x028b
        L_0x002a:
            boolean r1 = r0.isPreloadVideoOperation
            if (r1 == 0) goto L_0x0040
            int r1 = r0.requestedBytesCount
            if (r1 > r3) goto L_0x028b
            int r1 = r0.moovFound
            if (r1 == 0) goto L_0x0040
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$RequestInfo> r1 = r0.requestInfos
            int r1 = r1.size()
            if (r1 <= 0) goto L_0x0040
            goto L_0x028b
        L_0x0040:
            int r1 = r0.streamPriorityStartOffset
            r4 = 0
            if (r1 != 0) goto L_0x0063
            boolean r1 = r0.nextPartWasPreloaded
            if (r1 != 0) goto L_0x0063
            boolean r1 = r0.isPreloadVideoOperation
            if (r1 == 0) goto L_0x0051
            int r1 = r0.moovFound
            if (r1 == 0) goto L_0x0063
        L_0x0051:
            int r1 = r0.totalBytesCount
            if (r1 <= 0) goto L_0x0063
            int r1 = r0.currentMaxDownloadRequests
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$RequestInfo> r5 = r0.requestInfos
            int r5 = r5.size()
            int r1 = r1 - r5
            int r1 = java.lang.Math.max(r4, r1)
            goto L_0x0064
        L_0x0063:
            r1 = 1
        L_0x0064:
            r5 = 0
        L_0x0065:
            if (r5 >= r1) goto L_0x028b
            boolean r6 = r0.isPreloadVideoOperation
            r7 = 2
            if (r6 == 0) goto L_0x00f9
            int r6 = r0.moovFound
            if (r6 == 0) goto L_0x0075
            int r6 = r0.preloadNotRequestedBytesCount
            if (r6 > 0) goto L_0x0075
            return
        L_0x0075:
            int r6 = r0.nextPreloadDownloadOffset
            r8 = -1
            if (r6 != r8) goto L_0x00b5
            int r6 = r0.currentDownloadChunkSize
            int r6 = r3 / r6
            int r6 = r6 + r7
            r8 = 0
        L_0x0080:
            if (r6 == 0) goto L_0x00a6
            android.util.SparseIntArray r9 = r0.requestedPreloadedBytesRanges
            int r9 = r9.get(r8, r4)
            if (r9 != 0) goto L_0x008c
            r6 = 1
            goto L_0x00a7
        L_0x008c:
            int r9 = r0.currentDownloadChunkSize
            int r8 = r8 + r9
            int r10 = r0.totalBytesCount
            if (r8 <= r10) goto L_0x0094
            goto L_0x00a6
        L_0x0094:
            int r11 = r0.moovFound
            if (r11 != r7) goto L_0x00a3
            int r11 = r9 * 8
            if (r8 != r11) goto L_0x00a3
            r8 = 1048576(0x100000, float:1.469368E-39)
            int r10 = r10 - r8
            int r10 = r10 / r9
            int r10 = r10 * r9
            r8 = r10
        L_0x00a3:
            int r6 = r6 + -1
            goto L_0x0080
        L_0x00a6:
            r6 = 0
        L_0x00a7:
            if (r6 != 0) goto L_0x00b4
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$RequestInfo> r6 = r0.requestInfos
            boolean r6 = r6.isEmpty()
            if (r6 == 0) goto L_0x00b4
            r0.onFinishLoadingFile(r4)
        L_0x00b4:
            r6 = r8
        L_0x00b5:
            android.util.SparseIntArray r8 = r0.requestedPreloadedBytesRanges
            if (r8 != 0) goto L_0x00c0
            android.util.SparseIntArray r8 = new android.util.SparseIntArray
            r8.<init>()
            r0.requestedPreloadedBytesRanges = r8
        L_0x00c0:
            android.util.SparseIntArray r8 = r0.requestedPreloadedBytesRanges
            r8.put(r6, r2)
            boolean r8 = org.telegram.messenger.BuildVars.DEBUG_VERSION
            if (r8 == 0) goto L_0x00f1
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            java.lang.String r9 = "start next preload from "
            r8.append(r9)
            r8.append(r6)
            java.lang.String r9 = " size "
            r8.append(r9)
            int r9 = r0.totalBytesCount
            r8.append(r9)
            java.lang.String r9 = " for "
            r8.append(r9)
            java.io.File r9 = r0.cacheFilePreload
            r8.append(r9)
            java.lang.String r8 = r8.toString()
            org.telegram.messenger.FileLog.d(r8)
        L_0x00f1:
            int r8 = r0.preloadNotRequestedBytesCount
            int r9 = r0.currentDownloadChunkSize
            int r8 = r8 - r9
            r0.preloadNotRequestedBytesCount = r8
            goto L_0x0155
        L_0x00f9:
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$Range> r6 = r0.notRequestedBytesRanges
            if (r6 == 0) goto L_0x0153
            int r6 = r0.streamPriorityStartOffset
            if (r6 == 0) goto L_0x0102
            goto L_0x0104
        L_0x0102:
            int r6 = r0.streamStartOffset
        L_0x0104:
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$Range> r8 = r0.notRequestedBytesRanges
            int r8 = r8.size()
            r9 = 2147483647(0x7fffffff, float:NaN)
            r10 = 0
            r11 = 2147483647(0x7fffffff, float:NaN)
            r12 = 2147483647(0x7fffffff, float:NaN)
        L_0x0114:
            if (r10 >= r8) goto L_0x014b
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$Range> r13 = r0.notRequestedBytesRanges
            java.lang.Object r13 = r13.get(r10)
            org.telegram.messenger.FileLoadOperation$Range r13 = (org.telegram.messenger.FileLoadOperation.Range) r13
            if (r6 == 0) goto L_0x0140
            int r14 = r13.start
            if (r14 > r6) goto L_0x0130
            int r14 = r13.end
            if (r14 <= r6) goto L_0x0130
            r12 = 2147483647(0x7fffffff, float:NaN)
            goto L_0x014c
        L_0x0130:
            int r14 = r13.start
            if (r6 >= r14) goto L_0x0140
            int r14 = r13.start
            if (r14 >= r11) goto L_0x0140
            int r11 = r13.start
        L_0x0140:
            int r13 = r13.start
            int r12 = java.lang.Math.min(r12, r13)
            int r10 = r10 + 1
            goto L_0x0114
        L_0x014b:
            r6 = r11
        L_0x014c:
            if (r6 == r9) goto L_0x014f
            goto L_0x0155
        L_0x014f:
            if (r12 == r9) goto L_0x028b
            r6 = r12
            goto L_0x0155
        L_0x0153:
            int r6 = r0.requestedBytesCount
        L_0x0155:
            boolean r8 = r0.isPreloadVideoOperation
            if (r8 != 0) goto L_0x0163
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$Range> r8 = r0.notRequestedBytesRanges
            if (r8 == 0) goto L_0x0163
            int r9 = r0.currentDownloadChunkSize
            int r9 = r9 + r6
            r0.addPart(r8, r6, r9, r4)
        L_0x0163:
            int r8 = r0.totalBytesCount
            if (r8 <= 0) goto L_0x016b
            if (r6 < r8) goto L_0x016b
            goto L_0x028b
        L_0x016b:
            int r8 = r0.totalBytesCount
            if (r8 <= 0) goto L_0x017e
            int r9 = r1 + -1
            if (r5 == r9) goto L_0x017e
            if (r8 <= 0) goto L_0x017b
            int r9 = r0.currentDownloadChunkSize
            int r9 = r9 + r6
            if (r9 < r8) goto L_0x017b
            goto L_0x017e
        L_0x017b:
            r18 = 0
            goto L_0x0180
        L_0x017e:
            r18 = 1
        L_0x0180:
            int r8 = r0.requestsCount
            int r8 = r8 % r7
            if (r8 != 0) goto L_0x0188
            r17 = 2
            goto L_0x018e
        L_0x0188:
            r7 = 65538(0x10002, float:9.1838E-41)
            r17 = 65538(0x10002, float:9.1838E-41)
        L_0x018e:
            boolean r7 = r0.isForceRequest
            if (r7 == 0) goto L_0x0195
            r7 = 32
            goto L_0x0196
        L_0x0195:
            r7 = 0
        L_0x0196:
            boolean r8 = r0.isCdn
            if (r8 == 0) goto L_0x01ae
            org.telegram.tgnet.TLRPC$TL_upload_getCdnFile r8 = new org.telegram.tgnet.TLRPC$TL_upload_getCdnFile
            r8.<init>()
            byte[] r9 = r0.cdnToken
            r8.file_token = r9
            r8.offset = r6
            int r9 = r0.currentDownloadChunkSize
            r8.limit = r9
            r7 = r7 | 1
        L_0x01ab:
            r15 = r7
            r11 = r8
            goto L_0x01d4
        L_0x01ae:
            org.telegram.tgnet.TLRPC$InputWebFileLocation r8 = r0.webLocation
            if (r8 == 0) goto L_0x01c2
            org.telegram.tgnet.TLRPC$TL_upload_getWebFile r8 = new org.telegram.tgnet.TLRPC$TL_upload_getWebFile
            r8.<init>()
            org.telegram.tgnet.TLRPC$InputWebFileLocation r9 = r0.webLocation
            r8.location = r9
            r8.offset = r6
            int r9 = r0.currentDownloadChunkSize
            r8.limit = r9
            goto L_0x01ab
        L_0x01c2:
            org.telegram.tgnet.TLRPC$TL_upload_getFile r8 = new org.telegram.tgnet.TLRPC$TL_upload_getFile
            r8.<init>()
            org.telegram.tgnet.TLRPC$InputFileLocation r9 = r0.location
            r8.location = r9
            r8.offset = r6
            int r9 = r0.currentDownloadChunkSize
            r8.limit = r9
            r8.cdn_supported = r2
            goto L_0x01ab
        L_0x01d4:
            int r7 = r0.requestedBytesCount
            int r8 = r0.currentDownloadChunkSize
            int r7 = r7 + r8
            r0.requestedBytesCount = r7
            org.telegram.messenger.FileLoadOperation$RequestInfo r7 = new org.telegram.messenger.FileLoadOperation$RequestInfo
            r7.<init>()
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$RequestInfo> r8 = r0.requestInfos
            r8.add(r7)
            int unused = r7.offset = r6
            boolean r6 = r0.isPreloadVideoOperation
            if (r6 != 0) goto L_0x0241
            boolean r6 = r0.supportsPreloading
            if (r6 == 0) goto L_0x0241
            java.io.RandomAccessFile r6 = r0.preloadStream
            if (r6 == 0) goto L_0x0241
            android.util.SparseArray<org.telegram.messenger.FileLoadOperation$PreloadRange> r6 = r0.preloadedBytesRanges
            if (r6 == 0) goto L_0x0241
            int r8 = r7.offset
            java.lang.Object r6 = r6.get(r8)
            org.telegram.messenger.FileLoadOperation$PreloadRange r6 = (org.telegram.messenger.FileLoadOperation.PreloadRange) r6
            if (r6 == 0) goto L_0x0241
            org.telegram.tgnet.TLRPC$TL_upload_file r8 = new org.telegram.tgnet.TLRPC$TL_upload_file
            r8.<init>()
            org.telegram.tgnet.TLRPC.TL_upload_file unused = r7.response = r8
            org.telegram.tgnet.NativeByteBuffer r8 = new org.telegram.tgnet.NativeByteBuffer     // Catch:{ Exception -> 0x0240 }
            int r9 = r6.length     // Catch:{ Exception -> 0x0240 }
            r8.<init>((int) r9)     // Catch:{ Exception -> 0x0240 }
            java.io.RandomAccessFile r9 = r0.preloadStream     // Catch:{ Exception -> 0x0240 }
            int r6 = r6.fileOffset     // Catch:{ Exception -> 0x0240 }
            long r12 = (long) r6     // Catch:{ Exception -> 0x0240 }
            r9.seek(r12)     // Catch:{ Exception -> 0x0240 }
            java.io.RandomAccessFile r6 = r0.preloadStream     // Catch:{ Exception -> 0x0240 }
            java.nio.channels.FileChannel r6 = r6.getChannel()     // Catch:{ Exception -> 0x0240 }
            java.nio.ByteBuffer r9 = r8.buffer     // Catch:{ Exception -> 0x0240 }
            r6.read(r9)     // Catch:{ Exception -> 0x0240 }
            java.nio.ByteBuffer r6 = r8.buffer     // Catch:{ Exception -> 0x0240 }
            r6.position(r4)     // Catch:{ Exception -> 0x0240 }
            org.telegram.tgnet.TLRPC$TL_upload_file r6 = r7.response     // Catch:{ Exception -> 0x0240 }
            r6.bytes = r8     // Catch:{ Exception -> 0x0240 }
            org.telegram.messenger.DispatchQueue r6 = org.telegram.messenger.Utilities.stageQueue     // Catch:{ Exception -> 0x0240 }
            org.telegram.messenger.-$$Lambda$FileLoadOperation$_a8OwTWoM7783M1Mt0pXbFF_RXY r8 = new org.telegram.messenger.-$$Lambda$FileLoadOperation$_a8OwTWoM7783M1Mt0pXbFF_RXY     // Catch:{ Exception -> 0x0240 }
            r8.<init>(r7)     // Catch:{ Exception -> 0x0240 }
            r6.postRunnable(r8)     // Catch:{ Exception -> 0x0240 }
            goto L_0x0287
        L_0x0240:
        L_0x0241:
            int r6 = r0.streamPriorityStartOffset
            if (r6 == 0) goto L_0x0263
            boolean r6 = org.telegram.messenger.BuildVars.DEBUG_VERSION
            if (r6 == 0) goto L_0x025f
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            java.lang.String r8 = "frame get offset = "
            r6.append(r8)
            int r8 = r0.streamPriorityStartOffset
            r6.append(r8)
            java.lang.String r6 = r6.toString()
            org.telegram.messenger.FileLog.d(r6)
        L_0x025f:
            r0.streamPriorityStartOffset = r4
            r0.priorityRequestInfo = r7
        L_0x0263:
            int r6 = r0.currentAccount
            org.telegram.tgnet.ConnectionsManager r10 = org.telegram.tgnet.ConnectionsManager.getInstance(r6)
            org.telegram.messenger.-$$Lambda$FileLoadOperation$q0OPLp-rp7G0uazoY5pveJXhQSk r12 = new org.telegram.messenger.-$$Lambda$FileLoadOperation$q0OPLp-rp7G0uazoY5pveJXhQSk
            r12.<init>(r7, r11)
            r13 = 0
            r14 = 0
            boolean r6 = r0.isCdn
            if (r6 == 0) goto L_0x0277
            int r6 = r0.cdnDatacenterId
            goto L_0x0279
        L_0x0277:
            int r6 = r0.datacenterId
        L_0x0279:
            r16 = r6
            int r6 = r10.sendRequest(r11, r12, r13, r14, r15, r16, r17, r18)
            int unused = r7.requestToken = r6
            int r6 = r0.requestsCount
            int r6 = r6 + r2
            r0.requestsCount = r6
        L_0x0287:
            int r5 = r5 + 1
            goto L_0x0065
        L_0x028b:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.FileLoadOperation.startDownloadRequest():void");
    }

    public /* synthetic */ void lambda$startDownloadRequest$11$FileLoadOperation(RequestInfo requestInfo) {
        processRequestResult(requestInfo, (TLRPC.TL_error) null);
        requestInfo.response.freeResources();
    }

    public /* synthetic */ void lambda$startDownloadRequest$13$FileLoadOperation(RequestInfo requestInfo, TLObject tLObject, TLObject tLObject2, TLRPC.TL_error tL_error) {
        byte[] bArr;
        if (this.requestInfos.contains(requestInfo)) {
            if (requestInfo == this.priorityRequestInfo) {
                if (BuildVars.DEBUG_VERSION) {
                    FileLog.d("frame get request completed " + this.priorityRequestInfo.offset);
                }
                this.priorityRequestInfo = null;
            }
            if (tL_error != null) {
                if (FileRefController.isFileRefError(tL_error.text)) {
                    requestReference(requestInfo);
                    return;
                } else if ((tLObject instanceof TLRPC.TL_upload_getCdnFile) && tL_error.text.equals("FILE_TOKEN_INVALID")) {
                    this.isCdn = false;
                    clearOperaion(requestInfo, false);
                    startDownloadRequest();
                    return;
                }
            }
            if (tLObject2 instanceof TLRPC.TL_upload_fileCdnRedirect) {
                TLRPC.TL_upload_fileCdnRedirect tL_upload_fileCdnRedirect = (TLRPC.TL_upload_fileCdnRedirect) tLObject2;
                if (!tL_upload_fileCdnRedirect.file_hashes.isEmpty()) {
                    if (this.cdnHashes == null) {
                        this.cdnHashes = new SparseArray<>();
                    }
                    for (int i = 0; i < tL_upload_fileCdnRedirect.file_hashes.size(); i++) {
                        TLRPC.TL_fileHash tL_fileHash = tL_upload_fileCdnRedirect.file_hashes.get(i);
                        this.cdnHashes.put(tL_fileHash.offset, tL_fileHash);
                    }
                }
                byte[] bArr2 = tL_upload_fileCdnRedirect.encryption_iv;
                if (bArr2 == null || (bArr = tL_upload_fileCdnRedirect.encryption_key) == null || bArr2.length != 16 || bArr.length != 32) {
                    TLRPC.TL_error tL_error2 = new TLRPC.TL_error();
                    tL_error2.text = "bad redirect response";
                    tL_error2.code = 400;
                    processRequestResult(requestInfo, tL_error2);
                    return;
                }
                this.isCdn = true;
                if (this.notCheckedCdnRanges == null) {
                    this.notCheckedCdnRanges = new ArrayList<>();
                    this.notCheckedCdnRanges.add(new Range(0, 12288));
                }
                this.cdnDatacenterId = tL_upload_fileCdnRedirect.dc_id;
                this.cdnIv = tL_upload_fileCdnRedirect.encryption_iv;
                this.cdnKey = tL_upload_fileCdnRedirect.encryption_key;
                this.cdnToken = tL_upload_fileCdnRedirect.file_token;
                clearOperaion(requestInfo, false);
                startDownloadRequest();
            } else if (!(tLObject2 instanceof TLRPC.TL_upload_cdnFileReuploadNeeded)) {
                if (tLObject2 instanceof TLRPC.TL_upload_file) {
                    TLRPC.TL_upload_file unused = requestInfo.response = (TLRPC.TL_upload_file) tLObject2;
                } else if (tLObject2 instanceof TLRPC.TL_upload_webFile) {
                    TLRPC.TL_upload_webFile unused2 = requestInfo.responseWeb = (TLRPC.TL_upload_webFile) tLObject2;
                    if (this.totalBytesCount == 0 && requestInfo.responseWeb.size != 0) {
                        this.totalBytesCount = requestInfo.responseWeb.size;
                    }
                } else {
                    TLRPC.TL_upload_cdnFile unused3 = requestInfo.responseCdn = (TLRPC.TL_upload_cdnFile) tLObject2;
                }
                if (tLObject2 != null) {
                    int i2 = this.currentType;
                    if (i2 == 50331648) {
                        StatsController.getInstance(this.currentAccount).incrementReceivedBytesCount(tLObject2.networkType, 3, (long) (tLObject2.getObjectSize() + 4));
                    } else if (i2 == 33554432) {
                        StatsController.getInstance(this.currentAccount).incrementReceivedBytesCount(tLObject2.networkType, 2, (long) (tLObject2.getObjectSize() + 4));
                    } else if (i2 == 16777216) {
                        StatsController.getInstance(this.currentAccount).incrementReceivedBytesCount(tLObject2.networkType, 4, (long) (tLObject2.getObjectSize() + 4));
                    } else if (i2 == 67108864) {
                        StatsController.getInstance(this.currentAccount).incrementReceivedBytesCount(tLObject2.networkType, 5, (long) (tLObject2.getObjectSize() + 4));
                    }
                }
                processRequestResult(requestInfo, tL_error);
            } else if (!this.reuploadingCdn) {
                clearOperaion(requestInfo, false);
                this.reuploadingCdn = true;
                TLRPC.TL_upload_reuploadCdnFile tL_upload_reuploadCdnFile = new TLRPC.TL_upload_reuploadCdnFile();
                tL_upload_reuploadCdnFile.file_token = this.cdnToken;
                tL_upload_reuploadCdnFile.request_token = ((TLRPC.TL_upload_cdnFileReuploadNeeded) tLObject2).request_token;
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_upload_reuploadCdnFile, new RequestDelegate(requestInfo) {
                    private final /* synthetic */ FileLoadOperation.RequestInfo f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        FileLoadOperation.this.lambda$null$12$FileLoadOperation(this.f$1, tLObject, tL_error);
                    }
                }, (QuickAckDelegate) null, (WriteToSocketDelegate) null, 0, this.datacenterId, 1, true);
            }
        }
    }

    public /* synthetic */ void lambda$null$12$FileLoadOperation(RequestInfo requestInfo, TLObject tLObject, TLRPC.TL_error tL_error) {
        this.reuploadingCdn = false;
        if (tL_error == null) {
            TLRPC.Vector vector = (TLRPC.Vector) tLObject;
            if (!vector.objects.isEmpty()) {
                if (this.cdnHashes == null) {
                    this.cdnHashes = new SparseArray<>();
                }
                for (int i = 0; i < vector.objects.size(); i++) {
                    TLRPC.TL_fileHash tL_fileHash = (TLRPC.TL_fileHash) vector.objects.get(i);
                    this.cdnHashes.put(tL_fileHash.offset, tL_fileHash);
                }
            }
            startDownloadRequest();
        } else if (tL_error.text.equals("FILE_TOKEN_INVALID") || tL_error.text.equals("REQUEST_TOKEN_INVALID")) {
            this.isCdn = false;
            clearOperaion(requestInfo, false);
            startDownloadRequest();
        } else {
            onFail(false, 0);
        }
    }

    public void setDelegate(FileLoadOperationDelegate fileLoadOperationDelegate) {
        this.delegate = fileLoadOperationDelegate;
    }
}
