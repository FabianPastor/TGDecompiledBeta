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
import org.telegram.tgnet.TLRPC$InputFileLocation;
import org.telegram.tgnet.TLRPC$InputWebFileLocation;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_fileHash;
import org.telegram.tgnet.TLRPC$TL_fileLocationToBeDeprecated;
import org.telegram.tgnet.TLRPC$TL_inputDocumentFileLocation;
import org.telegram.tgnet.TLRPC$TL_inputEncryptedFileLocation;
import org.telegram.tgnet.TLRPC$TL_inputFileLocation;
import org.telegram.tgnet.TLRPC$TL_inputPeerPhotoFileLocation;
import org.telegram.tgnet.TLRPC$TL_inputPhotoFileLocation;
import org.telegram.tgnet.TLRPC$TL_inputSecureFileLocation;
import org.telegram.tgnet.TLRPC$TL_inputStickerSetThumb;
import org.telegram.tgnet.TLRPC$TL_secureFile;
import org.telegram.tgnet.TLRPC$TL_theme;
import org.telegram.tgnet.TLRPC$TL_upload_cdnFile;
import org.telegram.tgnet.TLRPC$TL_upload_cdnFileReuploadNeeded;
import org.telegram.tgnet.TLRPC$TL_upload_file;
import org.telegram.tgnet.TLRPC$TL_upload_fileCdnRedirect;
import org.telegram.tgnet.TLRPC$TL_upload_getCdnFile;
import org.telegram.tgnet.TLRPC$TL_upload_getCdnFileHashes;
import org.telegram.tgnet.TLRPC$TL_upload_reuploadCdnFile;
import org.telegram.tgnet.TLRPC$TL_upload_webFile;
import org.telegram.tgnet.TLRPC$Vector;
import org.telegram.tgnet.TLRPC$WebPage;
import org.telegram.tgnet.WriteToSocketDelegate;

public class FileLoadOperation {
    private static final int bigFileSizeFrom = 1048576;
    private static final int cdnChunkCheckSize = 131072;
    private static final int downloadChunkSize = 32768;
    private static final int downloadChunkSizeBig = 131072;
    private static final int maxCdnParts = 16000;
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
    private SparseArray<TLRPC$TL_fileHash> cdnHashes;
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
    protected TLRPC$InputFileLocation location;
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
    private TLRPC$InputWebFileLocation webLocation;

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
        public TLRPC$TL_upload_file response;
        /* access modifiers changed from: private */
        public TLRPC$TL_upload_cdnFile responseCdn;
        /* access modifiers changed from: private */
        public TLRPC$TL_upload_webFile responseWeb;

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
            TLRPC$TL_inputEncryptedFileLocation tLRPC$TL_inputEncryptedFileLocation = new TLRPC$TL_inputEncryptedFileLocation();
            this.location = tLRPC$TL_inputEncryptedFileLocation;
            TLRPC$TL_fileLocationToBeDeprecated tLRPC$TL_fileLocationToBeDeprecated = imageLocation.location;
            long j = tLRPC$TL_fileLocationToBeDeprecated.volume_id;
            tLRPC$TL_inputEncryptedFileLocation.id = j;
            tLRPC$TL_inputEncryptedFileLocation.volume_id = j;
            tLRPC$TL_inputEncryptedFileLocation.local_id = tLRPC$TL_fileLocationToBeDeprecated.local_id;
            tLRPC$TL_inputEncryptedFileLocation.access_hash = imageLocation.access_hash;
            byte[] bArr = new byte[32];
            this.iv = bArr;
            System.arraycopy(imageLocation.iv, 0, bArr, 0, bArr.length);
            this.key = imageLocation.key;
        } else if (imageLocation.photoPeer != null) {
            TLRPC$TL_inputPeerPhotoFileLocation tLRPC$TL_inputPeerPhotoFileLocation = new TLRPC$TL_inputPeerPhotoFileLocation();
            this.location = tLRPC$TL_inputPeerPhotoFileLocation;
            TLRPC$TL_fileLocationToBeDeprecated tLRPC$TL_fileLocationToBeDeprecated2 = imageLocation.location;
            long j2 = tLRPC$TL_fileLocationToBeDeprecated2.volume_id;
            tLRPC$TL_inputPeerPhotoFileLocation.id = j2;
            tLRPC$TL_inputPeerPhotoFileLocation.volume_id = j2;
            tLRPC$TL_inputPeerPhotoFileLocation.local_id = tLRPC$TL_fileLocationToBeDeprecated2.local_id;
            tLRPC$TL_inputPeerPhotoFileLocation.big = imageLocation.photoPeerBig;
            tLRPC$TL_inputPeerPhotoFileLocation.peer = imageLocation.photoPeer;
        } else if (imageLocation.stickerSet != null) {
            TLRPC$TL_inputStickerSetThumb tLRPC$TL_inputStickerSetThumb = new TLRPC$TL_inputStickerSetThumb();
            this.location = tLRPC$TL_inputStickerSetThumb;
            TLRPC$TL_fileLocationToBeDeprecated tLRPC$TL_fileLocationToBeDeprecated3 = imageLocation.location;
            long j3 = tLRPC$TL_fileLocationToBeDeprecated3.volume_id;
            tLRPC$TL_inputStickerSetThumb.id = j3;
            tLRPC$TL_inputStickerSetThumb.volume_id = j3;
            tLRPC$TL_inputStickerSetThumb.local_id = tLRPC$TL_fileLocationToBeDeprecated3.local_id;
            tLRPC$TL_inputStickerSetThumb.stickerset = imageLocation.stickerSet;
        } else if (imageLocation.thumbSize != null) {
            if (imageLocation.photoId != 0) {
                TLRPC$TL_inputPhotoFileLocation tLRPC$TL_inputPhotoFileLocation = new TLRPC$TL_inputPhotoFileLocation();
                this.location = tLRPC$TL_inputPhotoFileLocation;
                tLRPC$TL_inputPhotoFileLocation.id = imageLocation.photoId;
                TLRPC$TL_fileLocationToBeDeprecated tLRPC$TL_fileLocationToBeDeprecated4 = imageLocation.location;
                tLRPC$TL_inputPhotoFileLocation.volume_id = tLRPC$TL_fileLocationToBeDeprecated4.volume_id;
                tLRPC$TL_inputPhotoFileLocation.local_id = tLRPC$TL_fileLocationToBeDeprecated4.local_id;
                tLRPC$TL_inputPhotoFileLocation.access_hash = imageLocation.access_hash;
                tLRPC$TL_inputPhotoFileLocation.file_reference = imageLocation.file_reference;
                tLRPC$TL_inputPhotoFileLocation.thumb_size = imageLocation.thumbSize;
                if (imageLocation.imageType == 2) {
                    this.allowDisordererFileSave = true;
                }
            } else {
                TLRPC$TL_inputDocumentFileLocation tLRPC$TL_inputDocumentFileLocation = new TLRPC$TL_inputDocumentFileLocation();
                this.location = tLRPC$TL_inputDocumentFileLocation;
                tLRPC$TL_inputDocumentFileLocation.id = imageLocation.documentId;
                TLRPC$TL_fileLocationToBeDeprecated tLRPC$TL_fileLocationToBeDeprecated5 = imageLocation.location;
                tLRPC$TL_inputDocumentFileLocation.volume_id = tLRPC$TL_fileLocationToBeDeprecated5.volume_id;
                tLRPC$TL_inputDocumentFileLocation.local_id = tLRPC$TL_fileLocationToBeDeprecated5.local_id;
                tLRPC$TL_inputDocumentFileLocation.access_hash = imageLocation.access_hash;
                tLRPC$TL_inputDocumentFileLocation.file_reference = imageLocation.file_reference;
                tLRPC$TL_inputDocumentFileLocation.thumb_size = imageLocation.thumbSize;
            }
            TLRPC$InputFileLocation tLRPC$InputFileLocation = this.location;
            if (tLRPC$InputFileLocation.file_reference == null) {
                tLRPC$InputFileLocation.file_reference = new byte[0];
            }
        } else {
            TLRPC$TL_inputFileLocation tLRPC$TL_inputFileLocation = new TLRPC$TL_inputFileLocation();
            this.location = tLRPC$TL_inputFileLocation;
            TLRPC$TL_fileLocationToBeDeprecated tLRPC$TL_fileLocationToBeDeprecated6 = imageLocation.location;
            tLRPC$TL_inputFileLocation.volume_id = tLRPC$TL_fileLocationToBeDeprecated6.volume_id;
            tLRPC$TL_inputFileLocation.local_id = tLRPC$TL_fileLocationToBeDeprecated6.local_id;
            tLRPC$TL_inputFileLocation.secret = imageLocation.access_hash;
            byte[] bArr2 = imageLocation.file_reference;
            tLRPC$TL_inputFileLocation.file_reference = bArr2;
            if (bArr2 == null) {
                tLRPC$TL_inputFileLocation.file_reference = new byte[0];
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
        TLRPC$TL_inputSecureFileLocation tLRPC$TL_inputSecureFileLocation = new TLRPC$TL_inputSecureFileLocation();
        this.location = tLRPC$TL_inputSecureFileLocation;
        TLRPC$TL_secureFile tLRPC$TL_secureFile = secureDocument.secureFile;
        tLRPC$TL_inputSecureFileLocation.id = tLRPC$TL_secureFile.id;
        tLRPC$TL_inputSecureFileLocation.access_hash = tLRPC$TL_secureFile.access_hash;
        this.datacenterId = tLRPC$TL_secureFile.dc_id;
        this.totalBytesCount = tLRPC$TL_secureFile.size;
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
        int i2 = MessagesController.getInstance(i).webFileDatacenterId;
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

    /* JADX WARNING: Removed duplicated region for block: B:32:0x00b1 A[Catch:{ Exception -> 0x00f7 }] */
    /* JADX WARNING: Removed duplicated region for block: B:39:0x00d0 A[Catch:{ Exception -> 0x00f7 }] */
    /* JADX WARNING: Removed duplicated region for block: B:40:0x00d5 A[Catch:{ Exception -> 0x00f7 }] */
    /* JADX WARNING: Removed duplicated region for block: B:46:0x00ee A[Catch:{ Exception -> 0x00f7 }] */
    /* JADX WARNING: Removed duplicated region for block: B:52:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public FileLoadOperation(org.telegram.tgnet.TLRPC$Document r7, java.lang.Object r8) {
        /*
            r6 = this;
            r6.<init>()
            r0 = 16
            byte[] r1 = new byte[r0]
            r6.preloadTempBuffer = r1
            r1 = 0
            r6.state = r1
            r2 = 1
            r6.parentObject = r8     // Catch:{ Exception -> 0x00f7 }
            boolean r8 = r7 instanceof org.telegram.tgnet.TLRPC$TL_documentEncrypted     // Catch:{ Exception -> 0x00f7 }
            java.lang.String r3 = ""
            if (r8 == 0) goto L_0x003b
            org.telegram.tgnet.TLRPC$TL_inputEncryptedFileLocation r8 = new org.telegram.tgnet.TLRPC$TL_inputEncryptedFileLocation     // Catch:{ Exception -> 0x00f7 }
            r8.<init>()     // Catch:{ Exception -> 0x00f7 }
            r6.location = r8     // Catch:{ Exception -> 0x00f7 }
            long r4 = r7.id     // Catch:{ Exception -> 0x00f7 }
            r8.id = r4     // Catch:{ Exception -> 0x00f7 }
            long r4 = r7.access_hash     // Catch:{ Exception -> 0x00f7 }
            r8.access_hash = r4     // Catch:{ Exception -> 0x00f7 }
            int r8 = r7.dc_id     // Catch:{ Exception -> 0x00f7 }
            r6.datacenterId = r8     // Catch:{ Exception -> 0x00f7 }
            r6.initialDatacenterId = r8     // Catch:{ Exception -> 0x00f7 }
            r8 = 32
            byte[] r8 = new byte[r8]     // Catch:{ Exception -> 0x00f7 }
            r6.iv = r8     // Catch:{ Exception -> 0x00f7 }
            byte[] r4 = r7.iv     // Catch:{ Exception -> 0x00f7 }
            int r5 = r8.length     // Catch:{ Exception -> 0x00f7 }
            java.lang.System.arraycopy(r4, r1, r8, r1, r5)     // Catch:{ Exception -> 0x00f7 }
            byte[] r8 = r7.key     // Catch:{ Exception -> 0x00f7 }
            r6.key = r8     // Catch:{ Exception -> 0x00f7 }
            goto L_0x007b
        L_0x003b:
            boolean r8 = r7 instanceof org.telegram.tgnet.TLRPC$TL_document     // Catch:{ Exception -> 0x00f7 }
            if (r8 == 0) goto L_0x007b
            org.telegram.tgnet.TLRPC$TL_inputDocumentFileLocation r8 = new org.telegram.tgnet.TLRPC$TL_inputDocumentFileLocation     // Catch:{ Exception -> 0x00f7 }
            r8.<init>()     // Catch:{ Exception -> 0x00f7 }
            r6.location = r8     // Catch:{ Exception -> 0x00f7 }
            long r4 = r7.id     // Catch:{ Exception -> 0x00f7 }
            r8.id = r4     // Catch:{ Exception -> 0x00f7 }
            long r4 = r7.access_hash     // Catch:{ Exception -> 0x00f7 }
            r8.access_hash = r4     // Catch:{ Exception -> 0x00f7 }
            byte[] r4 = r7.file_reference     // Catch:{ Exception -> 0x00f7 }
            r8.file_reference = r4     // Catch:{ Exception -> 0x00f7 }
            r8.thumb_size = r3     // Catch:{ Exception -> 0x00f7 }
            if (r4 != 0) goto L_0x005a
            byte[] r4 = new byte[r1]     // Catch:{ Exception -> 0x00f7 }
            r8.file_reference = r4     // Catch:{ Exception -> 0x00f7 }
        L_0x005a:
            int r8 = r7.dc_id     // Catch:{ Exception -> 0x00f7 }
            r6.datacenterId = r8     // Catch:{ Exception -> 0x00f7 }
            r6.initialDatacenterId = r8     // Catch:{ Exception -> 0x00f7 }
            r6.allowDisordererFileSave = r2     // Catch:{ Exception -> 0x00f7 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r8 = r7.attributes     // Catch:{ Exception -> 0x00f7 }
            int r8 = r8.size()     // Catch:{ Exception -> 0x00f7 }
            r4 = 0
        L_0x0069:
            if (r4 >= r8) goto L_0x007b
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r5 = r7.attributes     // Catch:{ Exception -> 0x00f7 }
            java.lang.Object r5 = r5.get(r4)     // Catch:{ Exception -> 0x00f7 }
            boolean r5 = r5 instanceof org.telegram.tgnet.TLRPC$TL_documentAttributeVideo     // Catch:{ Exception -> 0x00f7 }
            if (r5 == 0) goto L_0x0078
            r6.supportsPreloading = r2     // Catch:{ Exception -> 0x00f7 }
            goto L_0x007b
        L_0x0078:
            int r4 = r4 + 1
            goto L_0x0069
        L_0x007b:
            java.lang.String r8 = "application/x-tgsticker"
            java.lang.String r4 = r7.mime_type     // Catch:{ Exception -> 0x00f7 }
            boolean r8 = r8.equals(r4)     // Catch:{ Exception -> 0x00f7 }
            if (r8 != 0) goto L_0x0092
            java.lang.String r8 = "application/x-tgwallpattern"
            java.lang.String r4 = r7.mime_type     // Catch:{ Exception -> 0x00f7 }
            boolean r8 = r8.equals(r4)     // Catch:{ Exception -> 0x00f7 }
            if (r8 == 0) goto L_0x0090
            goto L_0x0092
        L_0x0090:
            r8 = 0
            goto L_0x0093
        L_0x0092:
            r8 = 1
        L_0x0093:
            r6.ungzip = r8     // Catch:{ Exception -> 0x00f7 }
            int r8 = r7.size     // Catch:{ Exception -> 0x00f7 }
            r6.totalBytesCount = r8     // Catch:{ Exception -> 0x00f7 }
            byte[] r4 = r6.key     // Catch:{ Exception -> 0x00f7 }
            if (r4 == 0) goto L_0x00a9
            int r4 = r8 % 16
            if (r4 == 0) goto L_0x00a9
            int r4 = r8 % 16
            int r0 = r0 - r4
            r6.bytesCountPadding = r0     // Catch:{ Exception -> 0x00f7 }
            int r8 = r8 + r0
            r6.totalBytesCount = r8     // Catch:{ Exception -> 0x00f7 }
        L_0x00a9:
            java.lang.String r8 = org.telegram.messenger.FileLoader.getDocumentFileName(r7)     // Catch:{ Exception -> 0x00f7 }
            r6.ext = r8     // Catch:{ Exception -> 0x00f7 }
            if (r8 == 0) goto L_0x00c4
            r0 = 46
            int r8 = r8.lastIndexOf(r0)     // Catch:{ Exception -> 0x00f7 }
            r0 = -1
            if (r8 != r0) goto L_0x00bb
            goto L_0x00c4
        L_0x00bb:
            java.lang.String r0 = r6.ext     // Catch:{ Exception -> 0x00f7 }
            java.lang.String r8 = r0.substring(r8)     // Catch:{ Exception -> 0x00f7 }
            r6.ext = r8     // Catch:{ Exception -> 0x00f7 }
            goto L_0x00c6
        L_0x00c4:
            r6.ext = r3     // Catch:{ Exception -> 0x00f7 }
        L_0x00c6:
            java.lang.String r8 = "audio/ogg"
            java.lang.String r0 = r7.mime_type     // Catch:{ Exception -> 0x00f7 }
            boolean r8 = r8.equals(r0)     // Catch:{ Exception -> 0x00f7 }
            if (r8 == 0) goto L_0x00d5
            r8 = 50331648(0x3000000, float:3.761582E-37)
            r6.currentType = r8     // Catch:{ Exception -> 0x00f7 }
            goto L_0x00e6
        L_0x00d5:
            java.lang.String r8 = r7.mime_type     // Catch:{ Exception -> 0x00f7 }
            boolean r8 = org.telegram.messenger.FileLoader.isVideoMimeType(r8)     // Catch:{ Exception -> 0x00f7 }
            if (r8 == 0) goto L_0x00e2
            r8 = 33554432(0x2000000, float:9.403955E-38)
            r6.currentType = r8     // Catch:{ Exception -> 0x00f7 }
            goto L_0x00e6
        L_0x00e2:
            r8 = 67108864(0x4000000, float:1.5046328E-36)
            r6.currentType = r8     // Catch:{ Exception -> 0x00f7 }
        L_0x00e6:
            java.lang.String r8 = r6.ext     // Catch:{ Exception -> 0x00f7 }
            int r8 = r8.length()     // Catch:{ Exception -> 0x00f7 }
            if (r8 > r2) goto L_0x00fe
            java.lang.String r7 = r7.mime_type     // Catch:{ Exception -> 0x00f7 }
            java.lang.String r7 = org.telegram.messenger.FileLoader.getExtensionByMimeType(r7)     // Catch:{ Exception -> 0x00f7 }
            r6.ext = r7     // Catch:{ Exception -> 0x00f7 }
            goto L_0x00fe
        L_0x00f7:
            r7 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r7)
            r6.onFail(r2, r1)
        L_0x00fe:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.FileLoadOperation.<init>(org.telegram.tgnet.TLRPC$Document, java.lang.Object):void");
    }

    public void setEncryptFile(boolean z) {
        this.encryptFile = z;
        if (z) {
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
    public File getCacheFileFinal() {
        return this.cacheFileFinal;
    }

    /* access modifiers changed from: protected */
    public File getCurrentFile() {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        File[] fileArr = new File[1];
        Utilities.stageQueue.postRunnable(new Runnable(fileArr, countDownLatch) {
            public final /* synthetic */ File[] f$1;
            public final /* synthetic */ CountDownLatch f$2;

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
    public int[] getDownloadedLengthFromOffset(int i, int i2) {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        int[] iArr = new int[2];
        Utilities.stageQueue.postRunnable(new Runnable(iArr, i, i2, countDownLatch) {
            public final /* synthetic */ int[] f$1;
            public final /* synthetic */ int f$2;
            public final /* synthetic */ int f$3;
            public final /* synthetic */ CountDownLatch f$4;

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
        return iArr;
    }

    public /* synthetic */ void lambda$getDownloadedLengthFromOffset$2$FileLoadOperation(int[] iArr, int i, int i2, CountDownLatch countDownLatch) {
        iArr[0] = getDownloadedLengthFromOffsetInternal(this.notLoadedBytesRanges, i, i2);
        if (this.state == 3) {
            iArr[1] = 1;
        }
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
            public final /* synthetic */ FileLoadOperationStream f$1;

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
        if (this.notLoadedBytesRanges != null) {
            this.notLoadedBytesRangesCopy = new ArrayList<>(this.notLoadedBytesRanges);
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

    /* JADX WARNING: Removed duplicated region for block: B:103:0x03c0  */
    /* JADX WARNING: Removed duplicated region for block: B:131:0x0471 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:190:0x0577 A[SYNTHETIC, Splitter:B:190:0x0577] */
    /* JADX WARNING: Removed duplicated region for block: B:200:0x0597  */
    /* JADX WARNING: Removed duplicated region for block: B:214:0x05f9  */
    /* JADX WARNING: Removed duplicated region for block: B:227:0x0652  */
    /* JADX WARNING: Removed duplicated region for block: B:234:0x067a  */
    /* JADX WARNING: Removed duplicated region for block: B:240:0x06a6  */
    /* JADX WARNING: Removed duplicated region for block: B:245:0x06e3  */
    /* JADX WARNING: Removed duplicated region for block: B:269:0x0745 A[Catch:{ Exception -> 0x074c }] */
    /* JADX WARNING: Removed duplicated region for block: B:274:0x0754  */
    /* JADX WARNING: Removed duplicated region for block: B:276:0x075a  */
    /* JADX WARNING: Removed duplicated region for block: B:277:0x076a  */
    /* JADX WARNING: Removed duplicated region for block: B:91:0x036c  */
    /* JADX WARNING: Removed duplicated region for block: B:92:0x0392  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean start(org.telegram.messenger.FileLoadOperationStream r21, int r22, boolean r23) {
        /*
            r20 = this;
            r7 = r20
            int r0 = r7.currentDownloadChunkSize
            if (r0 != 0) goto L_0x0019
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
            r0 = 4
            r7.currentMaxDownloadRequests = r0
        L_0x0019:
            int r0 = r7.state
            r8 = 1
            r9 = 0
            if (r0 == 0) goto L_0x0021
            r0 = 1
            goto L_0x0022
        L_0x0021:
            r0 = 0
        L_0x0022:
            boolean r10 = r7.paused
            r7.paused = r9
            if (r21 == 0) goto L_0x003d
            org.telegram.messenger.DispatchQueue r11 = org.telegram.messenger.Utilities.stageQueue
            org.telegram.messenger.-$$Lambda$FileLoadOperation$ueOLhTDJvcCHBhZ6A3pTvCfLNBQ r12 = new org.telegram.messenger.-$$Lambda$FileLoadOperation$ueOLhTDJvcCHBhZ6A3pTvCfLNBQ
            r1 = r12
            r2 = r20
            r3 = r23
            r4 = r22
            r5 = r21
            r6 = r0
            r1.<init>(r3, r4, r5, r6)
            r11.postRunnable(r12)
            goto L_0x004b
        L_0x003d:
            if (r10 == 0) goto L_0x004b
            if (r0 == 0) goto L_0x004b
            org.telegram.messenger.DispatchQueue r1 = org.telegram.messenger.Utilities.stageQueue
            org.telegram.messenger.-$$Lambda$HfxdqGkDOxPIykHiS0VM1dK9UjM r2 = new org.telegram.messenger.-$$Lambda$HfxdqGkDOxPIykHiS0VM1dK9UjM
            r2.<init>()
            r1.postRunnable(r2)
        L_0x004b:
            if (r0 == 0) goto L_0x004e
            return r10
        L_0x004e:
            org.telegram.tgnet.TLRPC$InputFileLocation r0 = r7.location
            if (r0 != 0) goto L_0x005a
            org.telegram.tgnet.TLRPC$InputWebFileLocation r0 = r7.webLocation
            if (r0 != 0) goto L_0x005a
            r7.onFail(r8, r9)
            return r9
        L_0x005a:
            int r0 = r7.currentDownloadChunkSize
            int r1 = r22 / r0
            int r1 = r1 * r0
            r7.streamStartOffset = r1
            boolean r1 = r7.allowDisordererFileSave
            if (r1 == 0) goto L_0x007a
            int r1 = r7.totalBytesCount
            if (r1 <= 0) goto L_0x007a
            if (r1 <= r0) goto L_0x007a
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r7.notLoadedBytesRanges = r0
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r7.notRequestedBytesRanges = r0
        L_0x007a:
            org.telegram.tgnet.TLRPC$InputWebFileLocation r0 = r7.webLocation
            java.lang.String r1 = ".iv.enc"
            java.lang.String r2 = ".iv"
            java.lang.String r3 = ".enc"
            java.lang.String r4 = ".temp.enc"
            java.lang.String r5 = ".temp"
            java.lang.String r6 = "."
            r10 = 0
            if (r0 == 0) goto L_0x0112
            org.telegram.messenger.WebFile r0 = r7.webFile
            java.lang.String r0 = r0.url
            java.lang.String r0 = org.telegram.messenger.Utilities.MD5(r0)
            boolean r13 = r7.encryptFile
            if (r13 == 0) goto L_0x00d2
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
            if (r4 == 0) goto L_0x010b
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r0)
            r4.append(r1)
            java.lang.String r0 = r4.toString()
            goto L_0x010c
        L_0x00d2:
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
            if (r4 == 0) goto L_0x010a
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r0)
            r4.append(r2)
            java.lang.String r0 = r4.toString()
            r2 = r1
            goto L_0x010c
        L_0x010a:
            r2 = r1
        L_0x010b:
            r0 = 0
        L_0x010c:
            r1 = r0
            r0 = r2
        L_0x010e:
            r2 = 0
            r4 = 0
            goto L_0x0350
        L_0x0112:
            org.telegram.tgnet.TLRPC$InputFileLocation r0 = r7.location
            long r13 = r0.volume_id
            java.lang.String r15 = ".pt"
            java.lang.String r12 = ".preload"
            java.lang.String r8 = "_"
            int r16 = (r13 > r10 ? 1 : (r13 == r10 ? 0 : -1))
            if (r16 == 0) goto L_0x0247
            int r0 = r0.local_id
            if (r0 == 0) goto L_0x0247
            int r0 = r7.datacenterId
            r10 = -2147483648(0xfffffffvar_, float:-0.0)
            if (r0 == r10) goto L_0x0242
            r10 = -2147483648(0xfffffffvar_, double:NaN)
            int r16 = (r13 > r10 ? 1 : (r13 == r10 ? 0 : -1))
            if (r16 == 0) goto L_0x0242
            if (r0 != 0) goto L_0x0135
            goto L_0x0242
        L_0x0135:
            boolean r0 = r7.encryptFile
            if (r0 == 0) goto L_0x019e
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            org.telegram.tgnet.TLRPC$InputFileLocation r2 = r7.location
            long r10 = r2.volume_id
            r0.append(r10)
            r0.append(r8)
            org.telegram.tgnet.TLRPC$InputFileLocation r2 = r7.location
            int r2 = r2.local_id
            r0.append(r2)
            r0.append(r4)
            java.lang.String r2 = r0.toString()
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            org.telegram.tgnet.TLRPC$InputFileLocation r4 = r7.location
            long r4 = r4.volume_id
            r0.append(r4)
            r0.append(r8)
            org.telegram.tgnet.TLRPC$InputFileLocation r4 = r7.location
            int r4 = r4.local_id
            r0.append(r4)
            r0.append(r6)
            java.lang.String r4 = r7.ext
            r0.append(r4)
            r0.append(r3)
            java.lang.String r3 = r0.toString()
            byte[] r0 = r7.key
            if (r0 == 0) goto L_0x02b7
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            org.telegram.tgnet.TLRPC$InputFileLocation r4 = r7.location
            long r4 = r4.volume_id
            r0.append(r4)
            r0.append(r8)
            org.telegram.tgnet.TLRPC$InputFileLocation r4 = r7.location
            int r4 = r4.local_id
            r0.append(r4)
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            goto L_0x010c
        L_0x019e:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            org.telegram.tgnet.TLRPC$InputFileLocation r1 = r7.location
            long r3 = r1.volume_id
            r0.append(r3)
            r0.append(r8)
            org.telegram.tgnet.TLRPC$InputFileLocation r1 = r7.location
            int r1 = r1.local_id
            r0.append(r1)
            r0.append(r5)
            java.lang.String r0 = r0.toString()
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            org.telegram.tgnet.TLRPC$InputFileLocation r3 = r7.location
            long r3 = r3.volume_id
            r1.append(r3)
            r1.append(r8)
            org.telegram.tgnet.TLRPC$InputFileLocation r3 = r7.location
            int r3 = r3.local_id
            r1.append(r3)
            r1.append(r6)
            java.lang.String r3 = r7.ext
            r1.append(r3)
            java.lang.String r3 = r1.toString()
            byte[] r1 = r7.key
            if (r1 == 0) goto L_0x01ff
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            org.telegram.tgnet.TLRPC$InputFileLocation r4 = r7.location
            long r4 = r4.volume_id
            r1.append(r4)
            r1.append(r8)
            org.telegram.tgnet.TLRPC$InputFileLocation r4 = r7.location
            int r4 = r4.local_id
            r1.append(r4)
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            goto L_0x0200
        L_0x01ff:
            r1 = 0
        L_0x0200:
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$Range> r2 = r7.notLoadedBytesRanges
            if (r2 == 0) goto L_0x0222
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            org.telegram.tgnet.TLRPC$InputFileLocation r4 = r7.location
            long r4 = r4.volume_id
            r2.append(r4)
            r2.append(r8)
            org.telegram.tgnet.TLRPC$InputFileLocation r4 = r7.location
            int r4 = r4.local_id
            r2.append(r4)
            r2.append(r15)
            java.lang.String r2 = r2.toString()
            goto L_0x0223
        L_0x0222:
            r2 = 0
        L_0x0223:
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            org.telegram.tgnet.TLRPC$InputFileLocation r5 = r7.location
            long r5 = r5.volume_id
            r4.append(r5)
            r4.append(r8)
            org.telegram.tgnet.TLRPC$InputFileLocation r5 = r7.location
            int r5 = r5.local_id
            r4.append(r5)
            r4.append(r12)
            java.lang.String r4 = r4.toString()
            goto L_0x0350
        L_0x0242:
            r1 = 1
            r7.onFail(r1, r9)
            return r9
        L_0x0247:
            int r0 = r7.datacenterId
            if (r0 == 0) goto L_0x0776
            org.telegram.tgnet.TLRPC$InputFileLocation r0 = r7.location
            long r10 = r0.id
            r13 = 0
            int r0 = (r10 > r13 ? 1 : (r10 == r13 ? 0 : -1))
            if (r0 != 0) goto L_0x0257
            goto L_0x0776
        L_0x0257:
            boolean r0 = r7.encryptFile
            if (r0 == 0) goto L_0x02bb
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            int r2 = r7.datacenterId
            r0.append(r2)
            r0.append(r8)
            org.telegram.tgnet.TLRPC$InputFileLocation r2 = r7.location
            long r5 = r2.id
            r0.append(r5)
            r0.append(r4)
            java.lang.String r2 = r0.toString()
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            int r4 = r7.datacenterId
            r0.append(r4)
            r0.append(r8)
            org.telegram.tgnet.TLRPC$InputFileLocation r4 = r7.location
            long r4 = r4.id
            r0.append(r4)
            java.lang.String r4 = r7.ext
            r0.append(r4)
            r0.append(r3)
            java.lang.String r3 = r0.toString()
            byte[] r0 = r7.key
            if (r0 == 0) goto L_0x02b7
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            int r4 = r7.datacenterId
            r0.append(r4)
            r0.append(r8)
            org.telegram.tgnet.TLRPC$InputFileLocation r4 = r7.location
            long r4 = r4.id
            r0.append(r4)
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            goto L_0x010c
        L_0x02b7:
            r0 = r2
            r1 = 0
            goto L_0x010e
        L_0x02bb:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            int r1 = r7.datacenterId
            r0.append(r1)
            r0.append(r8)
            org.telegram.tgnet.TLRPC$InputFileLocation r1 = r7.location
            long r3 = r1.id
            r0.append(r3)
            r0.append(r5)
            java.lang.String r0 = r0.toString()
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            int r3 = r7.datacenterId
            r1.append(r3)
            r1.append(r8)
            org.telegram.tgnet.TLRPC$InputFileLocation r3 = r7.location
            long r3 = r3.id
            r1.append(r3)
            java.lang.String r3 = r7.ext
            r1.append(r3)
            java.lang.String r3 = r1.toString()
            byte[] r1 = r7.key
            if (r1 == 0) goto L_0x0313
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            int r4 = r7.datacenterId
            r1.append(r4)
            r1.append(r8)
            org.telegram.tgnet.TLRPC$InputFileLocation r4 = r7.location
            long r4 = r4.id
            r1.append(r4)
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            goto L_0x0314
        L_0x0313:
            r1 = 0
        L_0x0314:
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$Range> r2 = r7.notLoadedBytesRanges
            if (r2 == 0) goto L_0x0334
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            int r4 = r7.datacenterId
            r2.append(r4)
            r2.append(r8)
            org.telegram.tgnet.TLRPC$InputFileLocation r4 = r7.location
            long r4 = r4.id
            r2.append(r4)
            r2.append(r15)
            java.lang.String r2 = r2.toString()
            goto L_0x0335
        L_0x0334:
            r2 = 0
        L_0x0335:
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            int r5 = r7.datacenterId
            r4.append(r5)
            r4.append(r8)
            org.telegram.tgnet.TLRPC$InputFileLocation r5 = r7.location
            long r5 = r5.id
            r4.append(r5)
            r4.append(r12)
            java.lang.String r4 = r4.toString()
        L_0x0350:
            java.util.ArrayList r5 = new java.util.ArrayList
            int r6 = r7.currentMaxDownloadRequests
            r5.<init>(r6)
            r7.requestInfos = r5
            java.util.ArrayList r5 = new java.util.ArrayList
            int r6 = r7.currentMaxDownloadRequests
            r8 = 1
            int r6 = r6 - r8
            r5.<init>(r6)
            r7.delayedRequestInfos = r5
            r7.state = r8
            java.lang.Object r5 = r7.parentObject
            boolean r6 = r5 instanceof org.telegram.tgnet.TLRPC$TL_theme
            if (r6 == 0) goto L_0x0392
            org.telegram.tgnet.TLRPC$TL_theme r5 = (org.telegram.tgnet.TLRPC$TL_theme) r5
            java.io.File r6 = new java.io.File
            java.io.File r8 = org.telegram.messenger.ApplicationLoader.getFilesDirFixed()
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            java.lang.String r11 = "remote"
            r10.append(r11)
            long r11 = r5.id
            r10.append(r11)
            java.lang.String r5 = ".attheme"
            r10.append(r5)
            java.lang.String r5 = r10.toString()
            r6.<init>(r8, r5)
            r7.cacheFileFinal = r6
            goto L_0x039b
        L_0x0392:
            java.io.File r5 = new java.io.File
            java.io.File r6 = r7.storePath
            r5.<init>(r6, r3)
            r7.cacheFileFinal = r5
        L_0x039b:
            java.io.File r5 = r7.cacheFileFinal
            boolean r5 = r5.exists()
            if (r5 == 0) goto L_0x03be
            java.lang.Object r6 = r7.parentObject
            boolean r6 = r6 instanceof org.telegram.tgnet.TLRPC$TL_theme
            if (r6 != 0) goto L_0x03b8
            int r6 = r7.totalBytesCount
            if (r6 == 0) goto L_0x03be
            long r10 = (long) r6
            java.io.File r6 = r7.cacheFileFinal
            long r12 = r6.length()
            int r6 = (r10 > r12 ? 1 : (r10 == r12 ? 0 : -1))
            if (r6 == 0) goto L_0x03be
        L_0x03b8:
            java.io.File r5 = r7.cacheFileFinal
            r5.delete()
            r5 = 0
        L_0x03be:
            if (r5 != 0) goto L_0x076a
            java.io.File r5 = new java.io.File
            java.io.File r6 = r7.tempPath
            r5.<init>(r6, r0)
            r7.cacheFileTemp = r5
            boolean r5 = r7.ungzip
            if (r5 == 0) goto L_0x03e7
            java.io.File r5 = new java.io.File
            java.io.File r6 = r7.tempPath
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            r8.append(r0)
            java.lang.String r0 = ".gz"
            r8.append(r0)
            java.lang.String r0 = r8.toString()
            r5.<init>(r6, r0)
            r7.cacheFileGzipTemp = r5
        L_0x03e7:
            boolean r0 = r7.encryptFile
            r5 = 32
            java.lang.String r6 = "rws"
            if (r0 == 0) goto L_0x0465
            java.io.File r0 = new java.io.File
            java.io.File r8 = org.telegram.messenger.FileLoader.getInternalCacheDir()
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            r10.append(r3)
            java.lang.String r3 = ".key"
            r10.append(r3)
            java.lang.String r3 = r10.toString()
            r0.<init>(r8, r3)
            java.io.RandomAccessFile r3 = new java.io.RandomAccessFile     // Catch:{ Exception -> 0x045e }
            r3.<init>(r0, r6)     // Catch:{ Exception -> 0x045e }
            long r10 = r0.length()     // Catch:{ Exception -> 0x045e }
            byte[] r0 = new byte[r5]     // Catch:{ Exception -> 0x045e }
            r7.encryptKey = r0     // Catch:{ Exception -> 0x045e }
            r8 = 16
            byte[] r12 = new byte[r8]     // Catch:{ Exception -> 0x045e }
            r7.encryptIv = r12     // Catch:{ Exception -> 0x045e }
            r12 = 0
            int r14 = (r10 > r12 ? 1 : (r10 == r12 ? 0 : -1))
            if (r14 <= 0) goto L_0x0433
            r14 = 48
            long r10 = r10 % r14
            int r14 = (r10 > r12 ? 1 : (r10 == r12 ? 0 : -1))
            if (r14 != 0) goto L_0x0433
            r3.read(r0, r9, r5)     // Catch:{ Exception -> 0x045e }
            byte[] r0 = r7.encryptIv     // Catch:{ Exception -> 0x045e }
            r3.read(r0, r9, r8)     // Catch:{ Exception -> 0x045e }
            r8 = 0
            goto L_0x044c
        L_0x0433:
            java.security.SecureRandom r0 = org.telegram.messenger.Utilities.random     // Catch:{ Exception -> 0x045e }
            byte[] r8 = r7.encryptKey     // Catch:{ Exception -> 0x045e }
            r0.nextBytes(r8)     // Catch:{ Exception -> 0x045e }
            java.security.SecureRandom r0 = org.telegram.messenger.Utilities.random     // Catch:{ Exception -> 0x045e }
            byte[] r8 = r7.encryptIv     // Catch:{ Exception -> 0x045e }
            r0.nextBytes(r8)     // Catch:{ Exception -> 0x045e }
            byte[] r0 = r7.encryptKey     // Catch:{ Exception -> 0x045e }
            r3.write(r0)     // Catch:{ Exception -> 0x045e }
            byte[] r0 = r7.encryptIv     // Catch:{ Exception -> 0x045e }
            r3.write(r0)     // Catch:{ Exception -> 0x045e }
            r8 = 1
        L_0x044c:
            java.nio.channels.FileChannel r0 = r3.getChannel()     // Catch:{ Exception -> 0x0454 }
            r0.close()     // Catch:{ Exception -> 0x0454 }
            goto L_0x0458
        L_0x0454:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ Exception -> 0x045c }
        L_0x0458:
            r3.close()     // Catch:{ Exception -> 0x045c }
            goto L_0x0463
        L_0x045c:
            r0 = move-exception
            goto L_0x0460
        L_0x045e:
            r0 = move-exception
            r8 = 0
        L_0x0460:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0463:
            r3 = 1
            goto L_0x0467
        L_0x0465:
            r3 = 1
            r8 = 0
        L_0x0467:
            boolean[] r10 = new boolean[r3]
            r10[r9] = r9
            boolean r0 = r7.supportsPreloading
            r11 = 4
            if (r0 == 0) goto L_0x0593
            if (r4 == 0) goto L_0x0593
            java.io.File r0 = new java.io.File
            java.io.File r3 = r7.tempPath
            r0.<init>(r3, r4)
            r7.cacheFilePreload = r0
            java.io.RandomAccessFile r0 = new java.io.RandomAccessFile     // Catch:{ Exception -> 0x0562 }
            java.io.File r3 = r7.cacheFilePreload     // Catch:{ Exception -> 0x0562 }
            r0.<init>(r3, r6)     // Catch:{ Exception -> 0x0562 }
            r7.preloadStream = r0     // Catch:{ Exception -> 0x0562 }
            long r3 = r0.length()     // Catch:{ Exception -> 0x0562 }
            r13 = 1
            r7.preloadStreamFileOffset = r13     // Catch:{ Exception -> 0x0562 }
            long r13 = (long) r9     // Catch:{ Exception -> 0x0562 }
            long r13 = r3 - r13
            r16 = 1
            int r0 = (r13 > r16 ? 1 : (r13 == r16 ? 0 : -1))
            if (r0 <= 0) goto L_0x0555
            java.io.RandomAccessFile r0 = r7.preloadStream     // Catch:{ Exception -> 0x0562 }
            byte r0 = r0.readByte()     // Catch:{ Exception -> 0x0562 }
            if (r0 == 0) goto L_0x049f
            r0 = 1
            goto L_0x04a0
        L_0x049f:
            r0 = 0
        L_0x04a0:
            r10[r9] = r0     // Catch:{ Exception -> 0x0562 }
            r0 = 1
        L_0x04a3:
            long r13 = (long) r0     // Catch:{ Exception -> 0x0562 }
            int r15 = (r13 > r3 ? 1 : (r13 == r3 ? 0 : -1))
            if (r15 >= 0) goto L_0x0555
            long r13 = r3 - r13
            int r15 = (r13 > r11 ? 1 : (r13 == r11 ? 0 : -1))
            if (r15 >= 0) goto L_0x04b0
            goto L_0x0555
        L_0x04b0:
            java.io.RandomAccessFile r13 = r7.preloadStream     // Catch:{ Exception -> 0x0562 }
            int r13 = r13.readInt()     // Catch:{ Exception -> 0x0562 }
            int r0 = r0 + 4
            long r14 = (long) r0     // Catch:{ Exception -> 0x0562 }
            long r14 = r3 - r14
            int r16 = (r14 > r11 ? 1 : (r14 == r11 ? 0 : -1))
            if (r16 < 0) goto L_0x0555
            if (r13 < 0) goto L_0x0555
            int r14 = r7.totalBytesCount     // Catch:{ Exception -> 0x0562 }
            if (r13 <= r14) goto L_0x04c7
            goto L_0x0555
        L_0x04c7:
            java.io.RandomAccessFile r14 = r7.preloadStream     // Catch:{ Exception -> 0x0562 }
            int r14 = r14.readInt()     // Catch:{ Exception -> 0x0562 }
            int r0 = r0 + 4
            r16 = r10
            long r9 = (long) r0
            long r9 = r3 - r9
            long r11 = (long) r14
            int r18 = (r9 > r11 ? 1 : (r9 == r11 ? 0 : -1))
            if (r18 < 0) goto L_0x0557
            int r9 = r7.currentDownloadChunkSize     // Catch:{ Exception -> 0x0560 }
            if (r14 <= r9) goto L_0x04df
            goto L_0x0557
        L_0x04df:
            org.telegram.messenger.FileLoadOperation$PreloadRange r9 = new org.telegram.messenger.FileLoadOperation$PreloadRange     // Catch:{ Exception -> 0x0560 }
            r10 = 0
            r9.<init>(r0, r14)     // Catch:{ Exception -> 0x0560 }
            int r0 = r0 + r14
            java.io.RandomAccessFile r10 = r7.preloadStream     // Catch:{ Exception -> 0x0560 }
            long r11 = (long) r0     // Catch:{ Exception -> 0x0560 }
            r10.seek(r11)     // Catch:{ Exception -> 0x0560 }
            long r11 = r3 - r11
            r18 = 12
            int r10 = (r11 > r18 ? 1 : (r11 == r18 ? 0 : -1))
            if (r10 >= 0) goto L_0x04f5
            goto L_0x0557
        L_0x04f5:
            java.io.RandomAccessFile r10 = r7.preloadStream     // Catch:{ Exception -> 0x0560 }
            int r10 = r10.readInt()     // Catch:{ Exception -> 0x0560 }
            r7.foundMoovSize = r10     // Catch:{ Exception -> 0x0560 }
            if (r10 == 0) goto L_0x050f
            int r10 = r7.nextPreloadDownloadOffset     // Catch:{ Exception -> 0x0560 }
            int r11 = r7.totalBytesCount     // Catch:{ Exception -> 0x0560 }
            r12 = 2
            int r11 = r11 / r12
            if (r10 <= r11) goto L_0x0508
            goto L_0x0509
        L_0x0508:
            r12 = 1
        L_0x0509:
            r7.moovFound = r12     // Catch:{ Exception -> 0x0560 }
            int r10 = r7.foundMoovSize     // Catch:{ Exception -> 0x0560 }
            r7.preloadNotRequestedBytesCount = r10     // Catch:{ Exception -> 0x0560 }
        L_0x050f:
            java.io.RandomAccessFile r10 = r7.preloadStream     // Catch:{ Exception -> 0x0560 }
            int r10 = r10.readInt()     // Catch:{ Exception -> 0x0560 }
            r7.nextPreloadDownloadOffset = r10     // Catch:{ Exception -> 0x0560 }
            java.io.RandomAccessFile r10 = r7.preloadStream     // Catch:{ Exception -> 0x0560 }
            int r10 = r10.readInt()     // Catch:{ Exception -> 0x0560 }
            r7.nextAtomOffset = r10     // Catch:{ Exception -> 0x0560 }
            int r0 = r0 + 12
            android.util.SparseArray<org.telegram.messenger.FileLoadOperation$PreloadRange> r10 = r7.preloadedBytesRanges     // Catch:{ Exception -> 0x0560 }
            if (r10 != 0) goto L_0x052c
            android.util.SparseArray r10 = new android.util.SparseArray     // Catch:{ Exception -> 0x0560 }
            r10.<init>()     // Catch:{ Exception -> 0x0560 }
            r7.preloadedBytesRanges = r10     // Catch:{ Exception -> 0x0560 }
        L_0x052c:
            android.util.SparseIntArray r10 = r7.requestedPreloadedBytesRanges     // Catch:{ Exception -> 0x0560 }
            if (r10 != 0) goto L_0x0537
            android.util.SparseIntArray r10 = new android.util.SparseIntArray     // Catch:{ Exception -> 0x0560 }
            r10.<init>()     // Catch:{ Exception -> 0x0560 }
            r7.requestedPreloadedBytesRanges = r10     // Catch:{ Exception -> 0x0560 }
        L_0x0537:
            android.util.SparseArray<org.telegram.messenger.FileLoadOperation$PreloadRange> r10 = r7.preloadedBytesRanges     // Catch:{ Exception -> 0x0560 }
            r10.put(r13, r9)     // Catch:{ Exception -> 0x0560 }
            android.util.SparseIntArray r9 = r7.requestedPreloadedBytesRanges     // Catch:{ Exception -> 0x0560 }
            r10 = 1
            r9.put(r13, r10)     // Catch:{ Exception -> 0x0560 }
            int r9 = r7.totalPreloadedBytes     // Catch:{ Exception -> 0x0560 }
            int r9 = r9 + r14
            r7.totalPreloadedBytes = r9     // Catch:{ Exception -> 0x0560 }
            int r9 = r7.preloadStreamFileOffset     // Catch:{ Exception -> 0x0560 }
            int r14 = r14 + 20
            int r9 = r9 + r14
            r7.preloadStreamFileOffset = r9     // Catch:{ Exception -> 0x0560 }
            r10 = r16
            r9 = 0
            r11 = 4
            goto L_0x04a3
        L_0x0555:
            r16 = r10
        L_0x0557:
            java.io.RandomAccessFile r0 = r7.preloadStream     // Catch:{ Exception -> 0x0560 }
            int r3 = r7.preloadStreamFileOffset     // Catch:{ Exception -> 0x0560 }
            long r3 = (long) r3     // Catch:{ Exception -> 0x0560 }
            r0.seek(r3)     // Catch:{ Exception -> 0x0560 }
            goto L_0x0568
        L_0x0560:
            r0 = move-exception
            goto L_0x0565
        L_0x0562:
            r0 = move-exception
            r16 = r10
        L_0x0565:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0568:
            boolean r0 = r7.isPreloadVideoOperation
            if (r0 != 0) goto L_0x0595
            android.util.SparseArray<org.telegram.messenger.FileLoadOperation$PreloadRange> r0 = r7.preloadedBytesRanges
            if (r0 != 0) goto L_0x0595
            r3 = 0
            r7.cacheFilePreload = r3
            java.io.RandomAccessFile r0 = r7.preloadStream     // Catch:{ Exception -> 0x058e }
            if (r0 == 0) goto L_0x0595
            java.io.RandomAccessFile r0 = r7.preloadStream     // Catch:{ Exception -> 0x0581 }
            java.nio.channels.FileChannel r0 = r0.getChannel()     // Catch:{ Exception -> 0x0581 }
            r0.close()     // Catch:{ Exception -> 0x0581 }
            goto L_0x0585
        L_0x0581:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ Exception -> 0x058e }
        L_0x0585:
            java.io.RandomAccessFile r0 = r7.preloadStream     // Catch:{ Exception -> 0x058e }
            r0.close()     // Catch:{ Exception -> 0x058e }
            r3 = 0
            r7.preloadStream = r3     // Catch:{ Exception -> 0x058e }
            goto L_0x0595
        L_0x058e:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x0595
        L_0x0593:
            r16 = r10
        L_0x0595:
            if (r2 == 0) goto L_0x05f1
            java.io.File r0 = new java.io.File
            java.io.File r3 = r7.tempPath
            r0.<init>(r3, r2)
            r7.cacheFileParts = r0
            java.io.RandomAccessFile r0 = new java.io.RandomAccessFile     // Catch:{ Exception -> 0x05ed }
            java.io.File r2 = r7.cacheFileParts     // Catch:{ Exception -> 0x05ed }
            r0.<init>(r2, r6)     // Catch:{ Exception -> 0x05ed }
            r7.filePartsStream = r0     // Catch:{ Exception -> 0x05ed }
            long r2 = r0.length()     // Catch:{ Exception -> 0x05ed }
            r9 = 8
            long r9 = r2 % r9
            r11 = 4
            int r0 = (r9 > r11 ? 1 : (r9 == r11 ? 0 : -1))
            if (r0 != 0) goto L_0x05f1
            long r2 = r2 - r11
            java.io.RandomAccessFile r0 = r7.filePartsStream     // Catch:{ Exception -> 0x05ed }
            int r0 = r0.readInt()     // Catch:{ Exception -> 0x05ed }
            long r9 = (long) r0     // Catch:{ Exception -> 0x05ed }
            r11 = 2
            long r2 = r2 / r11
            int r4 = (r9 > r2 ? 1 : (r9 == r2 ? 0 : -1))
            if (r4 > 0) goto L_0x05f1
            r2 = 0
        L_0x05c7:
            if (r2 >= r0) goto L_0x05f1
            java.io.RandomAccessFile r3 = r7.filePartsStream     // Catch:{ Exception -> 0x05ed }
            int r3 = r3.readInt()     // Catch:{ Exception -> 0x05ed }
            java.io.RandomAccessFile r4 = r7.filePartsStream     // Catch:{ Exception -> 0x05ed }
            int r4 = r4.readInt()     // Catch:{ Exception -> 0x05ed }
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$Range> r9 = r7.notLoadedBytesRanges     // Catch:{ Exception -> 0x05ed }
            org.telegram.messenger.FileLoadOperation$Range r10 = new org.telegram.messenger.FileLoadOperation$Range     // Catch:{ Exception -> 0x05ed }
            r11 = 0
            r10.<init>(r3, r4)     // Catch:{ Exception -> 0x05ed }
            r9.add(r10)     // Catch:{ Exception -> 0x05ed }
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$Range> r9 = r7.notRequestedBytesRanges     // Catch:{ Exception -> 0x05ed }
            org.telegram.messenger.FileLoadOperation$Range r10 = new org.telegram.messenger.FileLoadOperation$Range     // Catch:{ Exception -> 0x05ed }
            r10.<init>(r3, r4)     // Catch:{ Exception -> 0x05ed }
            r9.add(r10)     // Catch:{ Exception -> 0x05ed }
            int r2 = r2 + 1
            goto L_0x05c7
        L_0x05ed:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x05f1:
            java.io.File r0 = r7.cacheFileTemp
            boolean r0 = r0.exists()
            if (r0 == 0) goto L_0x0652
            if (r8 == 0) goto L_0x0602
            java.io.File r0 = r7.cacheFileTemp
            r0.delete()
            goto L_0x0676
        L_0x0602:
            java.io.File r0 = r7.cacheFileTemp
            long r2 = r0.length()
            if (r1 == 0) goto L_0x061a
            int r0 = r7.currentDownloadChunkSize
            long r9 = (long) r0
            long r2 = r2 % r9
            r9 = 0
            int r0 = (r2 > r9 ? 1 : (r2 == r9 ? 0 : -1))
            if (r0 == 0) goto L_0x061a
            r2 = 0
            r7.downloadedBytes = r2
            r7.requestedBytesCount = r2
            goto L_0x062a
        L_0x061a:
            java.io.File r0 = r7.cacheFileTemp
            long r2 = r0.length()
            int r0 = (int) r2
            int r2 = r7.currentDownloadChunkSize
            int r0 = r0 / r2
            int r0 = r0 * r2
            r7.downloadedBytes = r0
            r7.requestedBytesCount = r0
        L_0x062a:
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$Range> r0 = r7.notLoadedBytesRanges
            if (r0 == 0) goto L_0x0676
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L_0x0676
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$Range> r0 = r7.notLoadedBytesRanges
            org.telegram.messenger.FileLoadOperation$Range r2 = new org.telegram.messenger.FileLoadOperation$Range
            int r3 = r7.downloadedBytes
            int r4 = r7.totalBytesCount
            r9 = 0
            r2.<init>(r3, r4)
            r0.add(r2)
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$Range> r0 = r7.notRequestedBytesRanges
            org.telegram.messenger.FileLoadOperation$Range r2 = new org.telegram.messenger.FileLoadOperation$Range
            int r3 = r7.downloadedBytes
            int r4 = r7.totalBytesCount
            r2.<init>(r3, r4)
            r0.add(r2)
            goto L_0x0676
        L_0x0652:
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$Range> r0 = r7.notLoadedBytesRanges
            if (r0 == 0) goto L_0x0676
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L_0x0676
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$Range> r0 = r7.notLoadedBytesRanges
            org.telegram.messenger.FileLoadOperation$Range r2 = new org.telegram.messenger.FileLoadOperation$Range
            int r3 = r7.totalBytesCount
            r4 = 0
            r9 = 0
            r2.<init>(r9, r3)
            r0.add(r2)
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$Range> r0 = r7.notRequestedBytesRanges
            org.telegram.messenger.FileLoadOperation$Range r2 = new org.telegram.messenger.FileLoadOperation$Range
            int r3 = r7.totalBytesCount
            r2.<init>(r9, r3)
            r0.add(r2)
        L_0x0676:
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$Range> r0 = r7.notLoadedBytesRanges
            if (r0 == 0) goto L_0x06a2
            int r2 = r7.totalBytesCount
            r7.downloadedBytes = r2
            int r0 = r0.size()
            r2 = 0
        L_0x0683:
            if (r2 >= r0) goto L_0x069e
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$Range> r3 = r7.notLoadedBytesRanges
            java.lang.Object r3 = r3.get(r2)
            org.telegram.messenger.FileLoadOperation$Range r3 = (org.telegram.messenger.FileLoadOperation.Range) r3
            int r4 = r7.downloadedBytes
            int r9 = r3.end
            int r3 = r3.start
            int r9 = r9 - r3
            int r4 = r4 - r9
            r7.downloadedBytes = r4
            int r2 = r2 + 1
            goto L_0x0683
        L_0x069e:
            int r0 = r7.downloadedBytes
            r7.requestedBytesCount = r0
        L_0x06a2:
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x06e1
            boolean r0 = r7.isPreloadVideoOperation
            if (r0 == 0) goto L_0x06c1
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r2 = "start preloading file to temp = "
            r0.append(r2)
            java.io.File r2 = r7.cacheFileTemp
            r0.append(r2)
            java.lang.String r0 = r0.toString()
            org.telegram.messenger.FileLog.d(r0)
            goto L_0x06e1
        L_0x06c1:
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
        L_0x06e1:
            if (r1 == 0) goto L_0x0726
            java.io.File r0 = new java.io.File
            java.io.File r2 = r7.tempPath
            r0.<init>(r2, r1)
            r7.cacheIvTemp = r0
            java.io.RandomAccessFile r0 = new java.io.RandomAccessFile     // Catch:{ Exception -> 0x071d }
            java.io.File r1 = r7.cacheIvTemp     // Catch:{ Exception -> 0x071d }
            r0.<init>(r1, r6)     // Catch:{ Exception -> 0x071d }
            r7.fiv = r0     // Catch:{ Exception -> 0x071d }
            int r0 = r7.downloadedBytes     // Catch:{ Exception -> 0x071d }
            if (r0 == 0) goto L_0x0726
            if (r8 != 0) goto L_0x0726
            java.io.File r0 = r7.cacheIvTemp     // Catch:{ Exception -> 0x071d }
            long r0 = r0.length()     // Catch:{ Exception -> 0x071d }
            r2 = 0
            int r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r4 <= 0) goto L_0x0717
            r8 = 32
            long r0 = r0 % r8
            int r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r4 != 0) goto L_0x0717
            java.io.RandomAccessFile r0 = r7.fiv     // Catch:{ Exception -> 0x071d }
            byte[] r1 = r7.iv     // Catch:{ Exception -> 0x071d }
            r2 = 0
            r0.read(r1, r2, r5)     // Catch:{ Exception -> 0x071d }
            goto L_0x0726
        L_0x0717:
            r1 = 0
            r7.downloadedBytes = r1     // Catch:{ Exception -> 0x071d }
            r7.requestedBytesCount = r1     // Catch:{ Exception -> 0x071d }
            goto L_0x0726
        L_0x071d:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            r1 = 0
            r7.downloadedBytes = r1
            r7.requestedBytesCount = r1
        L_0x0726:
            boolean r0 = r7.isPreloadVideoOperation
            if (r0 != 0) goto L_0x0735
            int r0 = r7.downloadedBytes
            if (r0 == 0) goto L_0x0735
            int r0 = r7.totalBytesCount
            if (r0 <= 0) goto L_0x0735
            r20.copyNotLoadedRanges()
        L_0x0735:
            r20.updateProgress()
            java.io.RandomAccessFile r0 = new java.io.RandomAccessFile     // Catch:{ Exception -> 0x074c }
            java.io.File r1 = r7.cacheFileTemp     // Catch:{ Exception -> 0x074c }
            r0.<init>(r1, r6)     // Catch:{ Exception -> 0x074c }
            r7.fileOutputStream = r0     // Catch:{ Exception -> 0x074c }
            int r1 = r7.downloadedBytes     // Catch:{ Exception -> 0x074c }
            if (r1 == 0) goto L_0x0750
            int r1 = r7.downloadedBytes     // Catch:{ Exception -> 0x074c }
            long r1 = (long) r1     // Catch:{ Exception -> 0x074c }
            r0.seek(r1)     // Catch:{ Exception -> 0x074c }
            goto L_0x0750
        L_0x074c:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0750:
            java.io.RandomAccessFile r0 = r7.fileOutputStream
            if (r0 != 0) goto L_0x075a
            r1 = 1
            r2 = 0
            r7.onFail(r1, r2)
            return r2
        L_0x075a:
            r1 = 1
            r7.started = r1
            org.telegram.messenger.DispatchQueue r0 = org.telegram.messenger.Utilities.stageQueue
            org.telegram.messenger.-$$Lambda$FileLoadOperation$pnBGbN7TnymWdMGyH-sNRrKnKro r2 = new org.telegram.messenger.-$$Lambda$FileLoadOperation$pnBGbN7TnymWdMGyH-sNRrKnKro
            r3 = r16
            r2.<init>(r3)
            r0.postRunnable(r2)
            goto L_0x0775
        L_0x076a:
            r1 = 1
            r2 = 0
            r7.started = r1
            r7.onFinishLoadingFile(r2)     // Catch:{ Exception -> 0x0772 }
            goto L_0x0775
        L_0x0772:
            r7.onFail(r1, r2)
        L_0x0775:
            return r1
        L_0x0776:
            r1 = 1
            r2 = 0
            r7.onFail(r1, r2)
            return r2
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

    public void updateProgress() {
        int i;
        int i2;
        FileLoadOperationDelegate fileLoadOperationDelegate = this.delegate;
        if (fileLoadOperationDelegate != null && (i = this.downloadedBytes) != (i2 = this.totalBytesCount) && i2 > 0) {
            fileLoadOperationDelegate.didChangedLoadProgress(this, (long) i, (long) i2);
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
                public final /* synthetic */ boolean f$1;

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
                if (this.cacheFileTemp != null) {
                    boolean z2 = false;
                    if (this.ungzip) {
                        try {
                            GZIPInputStream gZIPInputStream = new GZIPInputStream(new FileInputStream(this.cacheFileTemp));
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
                        if (this.parentObject instanceof TLRPC$TL_theme) {
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
                            int i = this.renameRetryCount + 1;
                            this.renameRetryCount = i;
                            if (i < 3) {
                                this.state = 1;
                                Utilities.stageQueue.postRunnable(new Runnable(z) {
                                    public final /* synthetic */ boolean f$1;

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
                    int i2 = this.currentType;
                    if (i2 == 50331648) {
                        StatsController.getInstance(this.currentAccount).incrementReceivedItemsCount(ApplicationLoader.getCurrentNetworkType(), 3, 1);
                    } else if (i2 == 33554432) {
                        StatsController.getInstance(this.currentAccount).incrementReceivedItemsCount(ApplicationLoader.getCurrentNetworkType(), 2, 1);
                    } else if (i2 == 16777216) {
                        StatsController.getInstance(this.currentAccount).incrementReceivedItemsCount(ApplicationLoader.getCurrentNetworkType(), 4, 1);
                    } else if (i2 == 67108864) {
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
            TLRPC$TL_upload_getCdnFileHashes tLRPC$TL_upload_getCdnFileHashes = new TLRPC$TL_upload_getCdnFileHashes();
            tLRPC$TL_upload_getCdnFileHashes.file_token = this.cdnToken;
            tLRPC$TL_upload_getCdnFileHashes.offset = i;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_upload_getCdnFileHashes, new RequestDelegate() {
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    FileLoadOperation.this.lambda$requestFileOffsets$9$FileLoadOperation(tLObject, tLRPC$TL_error);
                }
            }, (QuickAckDelegate) null, (WriteToSocketDelegate) null, 0, this.datacenterId, 1, true);
        }
    }

    public /* synthetic */ void lambda$requestFileOffsets$9$FileLoadOperation(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error != null) {
            onFail(false, 0);
            return;
        }
        this.requestingCdnOffsets = false;
        TLRPC$Vector tLRPC$Vector = (TLRPC$Vector) tLObject;
        if (!tLRPC$Vector.objects.isEmpty()) {
            if (this.cdnHashes == null) {
                this.cdnHashes = new SparseArray<>();
            }
            for (int i = 0; i < tLRPC$Vector.objects.size(); i++) {
                TLRPC$TL_fileHash tLRPC$TL_fileHash = (TLRPC$TL_fileHash) tLRPC$Vector.objects.get(i);
                this.cdnHashes.put(tLRPC$TL_fileHash.offset, tLRPC$TL_fileHash);
            }
        }
        int i2 = 0;
        while (i2 < this.delayedRequestInfos.size()) {
            RequestInfo requestInfo = this.delayedRequestInfos.get(i2);
            if (this.notLoadedBytesRanges != null || this.downloadedBytes == requestInfo.offset) {
                this.delayedRequestInfos.remove(i2);
                if (processRequestResult(requestInfo, (TLRPC$TL_error) null)) {
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
    /* JADX WARNING: Code restructure failed: missing block: B:166:0x0437, code lost:
        r7.delayedRequestInfos.remove(r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:167:0x0441, code lost:
        if (processRequestResult(r2, (org.telegram.tgnet.TLRPC$TL_error) null) != false) goto L_0x047e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:169:0x0447, code lost:
        if (org.telegram.messenger.FileLoadOperation.RequestInfo.access$400(r2) == null) goto L_0x0457;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:170:0x0449, code lost:
        org.telegram.messenger.FileLoadOperation.RequestInfo.access$400(r2).disableFree = false;
        org.telegram.messenger.FileLoadOperation.RequestInfo.access$400(r2).freeResources();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:172:0x045b, code lost:
        if (org.telegram.messenger.FileLoadOperation.RequestInfo.access$500(r2) == null) goto L_0x046b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:173:0x045d, code lost:
        org.telegram.messenger.FileLoadOperation.RequestInfo.access$500(r2).disableFree = false;
        org.telegram.messenger.FileLoadOperation.RequestInfo.access$500(r2).freeResources();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:175:0x046f, code lost:
        if (org.telegram.messenger.FileLoadOperation.RequestInfo.access$600(r2) == null) goto L_0x047e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:176:0x0471, code lost:
        org.telegram.messenger.FileLoadOperation.RequestInfo.access$600(r2).disableFree = false;
        org.telegram.messenger.FileLoadOperation.RequestInfo.access$600(r2).freeResources();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:81:0x0209, code lost:
        if (r8 >= r7.totalBytesCount) goto L_0x020b;
     */
    /* JADX WARNING: Removed duplicated region for block: B:104:0x0258 A[Catch:{ Exception -> 0x0490 }] */
    /* JADX WARNING: Removed duplicated region for block: B:107:0x0293 A[Catch:{ Exception -> 0x0490 }] */
    /* JADX WARNING: Removed duplicated region for block: B:112:0x02df A[Catch:{ Exception -> 0x0490 }] */
    /* JADX WARNING: Removed duplicated region for block: B:150:0x03ee A[Catch:{ Exception -> 0x0490 }] */
    /* JADX WARNING: Removed duplicated region for block: B:74:0x01e2 A[Catch:{ Exception -> 0x0490 }] */
    /* JADX WARNING: Removed duplicated region for block: B:75:0x01f1 A[Catch:{ Exception -> 0x0490 }] */
    /* JADX WARNING: Removed duplicated region for block: B:97:0x022b A[Catch:{ Exception -> 0x0490 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean processRequestResult(org.telegram.messenger.FileLoadOperation.RequestInfo r27, org.telegram.tgnet.TLRPC$TL_error r28) {
        /*
            r26 = this;
            r7 = r26
            r0 = r28
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
            int r1 = r27.offset
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            org.telegram.messenger.FileLog.d(r0)
        L_0x0030:
            return r9
        L_0x0031:
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$RequestInfo> r1 = r7.requestInfos
            r3 = r27
            r1.remove(r3)
            java.lang.String r1 = " secret = "
            java.lang.String r4 = " volume_id = "
            java.lang.String r5 = " access_hash = "
            java.lang.String r6 = " local_id = "
            java.lang.String r11 = " id = "
            r12 = 0
            if (r0 != 0) goto L_0x0499
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$Range> r0 = r7.notLoadedBytesRanges     // Catch:{ Exception -> 0x0490 }
            if (r0 != 0) goto L_0x0055
            int r0 = r7.downloadedBytes     // Catch:{ Exception -> 0x0490 }
            int r13 = r27.offset     // Catch:{ Exception -> 0x0490 }
            if (r0 == r13) goto L_0x0055
            r26.delayRequestInfo(r27)     // Catch:{ Exception -> 0x0490 }
            return r9
        L_0x0055:
            org.telegram.tgnet.TLRPC$TL_upload_file r0 = r27.response     // Catch:{ Exception -> 0x0490 }
            if (r0 == 0) goto L_0x0062
            org.telegram.tgnet.TLRPC$TL_upload_file r0 = r27.response     // Catch:{ Exception -> 0x0490 }
            org.telegram.tgnet.NativeByteBuffer r0 = r0.bytes     // Catch:{ Exception -> 0x0490 }
            goto L_0x007d
        L_0x0062:
            org.telegram.tgnet.TLRPC$TL_upload_webFile r0 = r27.responseWeb     // Catch:{ Exception -> 0x0490 }
            if (r0 == 0) goto L_0x006f
            org.telegram.tgnet.TLRPC$TL_upload_webFile r0 = r27.responseWeb     // Catch:{ Exception -> 0x0490 }
            org.telegram.tgnet.NativeByteBuffer r0 = r0.bytes     // Catch:{ Exception -> 0x0490 }
            goto L_0x007d
        L_0x006f:
            org.telegram.tgnet.TLRPC$TL_upload_cdnFile r0 = r27.responseCdn     // Catch:{ Exception -> 0x0490 }
            if (r0 == 0) goto L_0x007c
            org.telegram.tgnet.TLRPC$TL_upload_cdnFile r0 = r27.responseCdn     // Catch:{ Exception -> 0x0490 }
            org.telegram.tgnet.NativeByteBuffer r0 = r0.bytes     // Catch:{ Exception -> 0x0490 }
            goto L_0x007d
        L_0x007c:
            r0 = r12
        L_0x007d:
            if (r0 == 0) goto L_0x048b
            int r13 = r0.limit()     // Catch:{ Exception -> 0x0490 }
            if (r13 != 0) goto L_0x0087
            goto L_0x048b
        L_0x0087:
            int r13 = r0.limit()     // Catch:{ Exception -> 0x0490 }
            boolean r14 = r7.isCdn     // Catch:{ Exception -> 0x0490 }
            r15 = 131072(0x20000, float:1.83671E-40)
            if (r14 == 0) goto L_0x00af
            int r14 = r27.offset     // Catch:{ Exception -> 0x0490 }
            int r14 = r14 / r15
            int r14 = r14 * r15
            android.util.SparseArray<org.telegram.tgnet.TLRPC$TL_fileHash> r15 = r7.cdnHashes     // Catch:{ Exception -> 0x0490 }
            if (r15 == 0) goto L_0x00a5
            android.util.SparseArray<org.telegram.tgnet.TLRPC$TL_fileHash> r15 = r7.cdnHashes     // Catch:{ Exception -> 0x0490 }
            java.lang.Object r15 = r15.get(r14)     // Catch:{ Exception -> 0x0490 }
            org.telegram.tgnet.TLRPC$TL_fileHash r15 = (org.telegram.tgnet.TLRPC$TL_fileHash) r15     // Catch:{ Exception -> 0x0490 }
            goto L_0x00a6
        L_0x00a5:
            r15 = r12
        L_0x00a6:
            if (r15 != 0) goto L_0x00af
            r26.delayRequestInfo(r27)     // Catch:{ Exception -> 0x0490 }
            r7.requestFileOffsets(r14)     // Catch:{ Exception -> 0x0490 }
            return r8
        L_0x00af:
            org.telegram.tgnet.TLRPC$TL_upload_cdnFile r14 = r27.responseCdn     // Catch:{ Exception -> 0x0490 }
            r15 = 13
            r16 = 14
            r17 = 15
            r18 = 12
            if (r14 == 0) goto L_0x00f2
            int r14 = r27.offset     // Catch:{ Exception -> 0x0490 }
            int r14 = r14 / 16
            byte[] r8 = r7.cdnIv     // Catch:{ Exception -> 0x0490 }
            r10 = r14 & 255(0xff, float:3.57E-43)
            byte r10 = (byte) r10     // Catch:{ Exception -> 0x0490 }
            r8[r17] = r10     // Catch:{ Exception -> 0x0490 }
            byte[] r8 = r7.cdnIv     // Catch:{ Exception -> 0x0490 }
            int r10 = r14 >> 8
            r10 = r10 & 255(0xff, float:3.57E-43)
            byte r10 = (byte) r10     // Catch:{ Exception -> 0x0490 }
            r8[r16] = r10     // Catch:{ Exception -> 0x0490 }
            byte[] r8 = r7.cdnIv     // Catch:{ Exception -> 0x0490 }
            int r10 = r14 >> 16
            r10 = r10 & 255(0xff, float:3.57E-43)
            byte r10 = (byte) r10     // Catch:{ Exception -> 0x0490 }
            r8[r15] = r10     // Catch:{ Exception -> 0x0490 }
            byte[] r8 = r7.cdnIv     // Catch:{ Exception -> 0x0490 }
            int r10 = r14 >> 24
            r10 = r10 & 255(0xff, float:3.57E-43)
            byte r10 = (byte) r10     // Catch:{ Exception -> 0x0490 }
            r8[r18] = r10     // Catch:{ Exception -> 0x0490 }
            java.nio.ByteBuffer r8 = r0.buffer     // Catch:{ Exception -> 0x0490 }
            byte[] r10 = r7.cdnKey     // Catch:{ Exception -> 0x0490 }
            byte[] r14 = r7.cdnIv     // Catch:{ Exception -> 0x0490 }
            int r15 = r0.limit()     // Catch:{ Exception -> 0x0490 }
            org.telegram.messenger.Utilities.aesCtrDecryption(r8, r10, r14, r9, r15)     // Catch:{ Exception -> 0x0490 }
        L_0x00f2:
            boolean r8 = r7.isPreloadVideoOperation     // Catch:{ Exception -> 0x0490 }
            if (r8 == 0) goto L_0x01fe
            java.io.RandomAccessFile r1 = r7.preloadStream     // Catch:{ Exception -> 0x0490 }
            int r4 = r27.offset     // Catch:{ Exception -> 0x0490 }
            r1.writeInt(r4)     // Catch:{ Exception -> 0x0490 }
            java.io.RandomAccessFile r1 = r7.preloadStream     // Catch:{ Exception -> 0x0490 }
            r1.writeInt(r13)     // Catch:{ Exception -> 0x0490 }
            int r1 = r7.preloadStreamFileOffset     // Catch:{ Exception -> 0x0490 }
            int r1 = r1 + 8
            r7.preloadStreamFileOffset = r1     // Catch:{ Exception -> 0x0490 }
            java.io.RandomAccessFile r1 = r7.preloadStream     // Catch:{ Exception -> 0x0490 }
            java.nio.channels.FileChannel r1 = r1.getChannel()     // Catch:{ Exception -> 0x0490 }
            java.nio.ByteBuffer r4 = r0.buffer     // Catch:{ Exception -> 0x0490 }
            r1.write(r4)     // Catch:{ Exception -> 0x0490 }
            boolean r1 = org.telegram.messenger.BuildVars.DEBUG_VERSION     // Catch:{ Exception -> 0x0490 }
            if (r1 == 0) goto L_0x0141
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0490 }
            r1.<init>()     // Catch:{ Exception -> 0x0490 }
            java.lang.String r4 = "save preload file part "
            r1.append(r4)     // Catch:{ Exception -> 0x0490 }
            java.io.File r4 = r7.cacheFilePreload     // Catch:{ Exception -> 0x0490 }
            r1.append(r4)     // Catch:{ Exception -> 0x0490 }
            r1.append(r2)     // Catch:{ Exception -> 0x0490 }
            int r2 = r27.offset     // Catch:{ Exception -> 0x0490 }
            r1.append(r2)     // Catch:{ Exception -> 0x0490 }
            java.lang.String r2 = " size "
            r1.append(r2)     // Catch:{ Exception -> 0x0490 }
            r1.append(r13)     // Catch:{ Exception -> 0x0490 }
            java.lang.String r1 = r1.toString()     // Catch:{ Exception -> 0x0490 }
            org.telegram.messenger.FileLog.d(r1)     // Catch:{ Exception -> 0x0490 }
        L_0x0141:
            android.util.SparseArray<org.telegram.messenger.FileLoadOperation$PreloadRange> r1 = r7.preloadedBytesRanges     // Catch:{ Exception -> 0x0490 }
            if (r1 != 0) goto L_0x014c
            android.util.SparseArray r1 = new android.util.SparseArray     // Catch:{ Exception -> 0x0490 }
            r1.<init>()     // Catch:{ Exception -> 0x0490 }
            r7.preloadedBytesRanges = r1     // Catch:{ Exception -> 0x0490 }
        L_0x014c:
            android.util.SparseArray<org.telegram.messenger.FileLoadOperation$PreloadRange> r1 = r7.preloadedBytesRanges     // Catch:{ Exception -> 0x0490 }
            int r2 = r27.offset     // Catch:{ Exception -> 0x0490 }
            org.telegram.messenger.FileLoadOperation$PreloadRange r4 = new org.telegram.messenger.FileLoadOperation$PreloadRange     // Catch:{ Exception -> 0x0490 }
            int r5 = r7.preloadStreamFileOffset     // Catch:{ Exception -> 0x0490 }
            r4.<init>(r5, r13)     // Catch:{ Exception -> 0x0490 }
            r1.put(r2, r4)     // Catch:{ Exception -> 0x0490 }
            int r1 = r7.totalPreloadedBytes     // Catch:{ Exception -> 0x0490 }
            int r1 = r1 + r13
            r7.totalPreloadedBytes = r1     // Catch:{ Exception -> 0x0490 }
            int r1 = r7.preloadStreamFileOffset     // Catch:{ Exception -> 0x0490 }
            int r1 = r1 + r13
            r7.preloadStreamFileOffset = r1     // Catch:{ Exception -> 0x0490 }
            int r1 = r7.moovFound     // Catch:{ Exception -> 0x0490 }
            if (r1 != 0) goto L_0x01a9
            int r1 = r7.nextAtomOffset     // Catch:{ Exception -> 0x0490 }
            int r2 = r27.offset     // Catch:{ Exception -> 0x0490 }
            int r0 = r7.findNextPreloadDownloadOffset(r1, r2, r0)     // Catch:{ Exception -> 0x0490 }
            if (r0 >= 0) goto L_0x019d
            int r0 = r0 * -1
            int r1 = r7.nextPreloadDownloadOffset     // Catch:{ Exception -> 0x0490 }
            int r2 = r7.currentDownloadChunkSize     // Catch:{ Exception -> 0x0490 }
            int r1 = r1 + r2
            r7.nextPreloadDownloadOffset = r1     // Catch:{ Exception -> 0x0490 }
            int r2 = r7.totalBytesCount     // Catch:{ Exception -> 0x0490 }
            r3 = 2
            int r2 = r2 / r3
            if (r1 >= r2) goto L_0x0190
            r1 = 1048576(0x100000, float:1.469368E-39)
            int r1 = r1 + r0
            r7.foundMoovSize = r1     // Catch:{ Exception -> 0x0490 }
            r7.preloadNotRequestedBytesCount = r1     // Catch:{ Exception -> 0x0490 }
            r1 = 1
            r7.moovFound = r1     // Catch:{ Exception -> 0x0490 }
            goto L_0x0199
        L_0x0190:
            r1 = 2097152(0x200000, float:2.938736E-39)
            r7.foundMoovSize = r1     // Catch:{ Exception -> 0x0490 }
            r7.preloadNotRequestedBytesCount = r1     // Catch:{ Exception -> 0x0490 }
            r1 = 2
            r7.moovFound = r1     // Catch:{ Exception -> 0x0490 }
        L_0x0199:
            r1 = -1
            r7.nextPreloadDownloadOffset = r1     // Catch:{ Exception -> 0x0490 }
            goto L_0x01a7
        L_0x019d:
            int r1 = r7.currentDownloadChunkSize     // Catch:{ Exception -> 0x0490 }
            int r1 = r0 / r1
            int r2 = r7.currentDownloadChunkSize     // Catch:{ Exception -> 0x0490 }
            int r1 = r1 * r2
            r7.nextPreloadDownloadOffset = r1     // Catch:{ Exception -> 0x0490 }
        L_0x01a7:
            r7.nextAtomOffset = r0     // Catch:{ Exception -> 0x0490 }
        L_0x01a9:
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
            int r0 = r0 + 12
            r7.preloadStreamFileOffset = r0     // Catch:{ Exception -> 0x0490 }
            int r0 = r7.nextPreloadDownloadOffset     // Catch:{ Exception -> 0x0490 }
            if (r0 == 0) goto L_0x01df
            int r0 = r7.moovFound     // Catch:{ Exception -> 0x0490 }
            if (r0 == 0) goto L_0x01d0
            int r0 = r7.foundMoovSize     // Catch:{ Exception -> 0x0490 }
            if (r0 < 0) goto L_0x01df
        L_0x01d0:
            int r0 = r7.totalPreloadedBytes     // Catch:{ Exception -> 0x0490 }
            r1 = 2097152(0x200000, float:2.938736E-39)
            if (r0 > r1) goto L_0x01df
            int r0 = r7.nextPreloadDownloadOffset     // Catch:{ Exception -> 0x0490 }
            int r1 = r7.totalBytesCount     // Catch:{ Exception -> 0x0490 }
            if (r0 < r1) goto L_0x01dd
            goto L_0x01df
        L_0x01dd:
            r0 = 0
            goto L_0x01e0
        L_0x01df:
            r0 = 1
        L_0x01e0:
            if (r0 == 0) goto L_0x01f1
            java.io.RandomAccessFile r1 = r7.preloadStream     // Catch:{ Exception -> 0x0490 }
            r2 = 0
            r1.seek(r2)     // Catch:{ Exception -> 0x0490 }
            java.io.RandomAccessFile r1 = r7.preloadStream     // Catch:{ Exception -> 0x0490 }
            r2 = 1
            r1.write(r2)     // Catch:{ Exception -> 0x0490 }
            goto L_0x0416
        L_0x01f1:
            int r1 = r7.moovFound     // Catch:{ Exception -> 0x0490 }
            if (r1 == 0) goto L_0x0416
            int r1 = r7.foundMoovSize     // Catch:{ Exception -> 0x0490 }
            int r2 = r7.currentDownloadChunkSize     // Catch:{ Exception -> 0x0490 }
            int r1 = r1 - r2
            r7.foundMoovSize = r1     // Catch:{ Exception -> 0x0490 }
            goto L_0x0416
        L_0x01fe:
            int r8 = r7.downloadedBytes     // Catch:{ Exception -> 0x0490 }
            int r8 = r8 + r13
            r7.downloadedBytes = r8     // Catch:{ Exception -> 0x0490 }
            int r10 = r7.totalBytesCount     // Catch:{ Exception -> 0x0490 }
            if (r10 <= 0) goto L_0x020f
            int r10 = r7.totalBytesCount     // Catch:{ Exception -> 0x0490 }
            if (r8 < r10) goto L_0x020d
        L_0x020b:
            r8 = 1
            goto L_0x0227
        L_0x020d:
            r8 = 0
            goto L_0x0227
        L_0x020f:
            int r10 = r7.currentDownloadChunkSize     // Catch:{ Exception -> 0x0490 }
            if (r13 != r10) goto L_0x020b
            int r10 = r7.totalBytesCount     // Catch:{ Exception -> 0x0490 }
            if (r10 == r8) goto L_0x021c
            int r10 = r7.currentDownloadChunkSize     // Catch:{ Exception -> 0x0490 }
            int r8 = r8 % r10
            if (r8 == 0) goto L_0x020d
        L_0x021c:
            int r8 = r7.totalBytesCount     // Catch:{ Exception -> 0x0490 }
            if (r8 <= 0) goto L_0x020b
            int r8 = r7.totalBytesCount     // Catch:{ Exception -> 0x0490 }
            int r10 = r7.downloadedBytes     // Catch:{ Exception -> 0x0490 }
            if (r8 > r10) goto L_0x020d
            goto L_0x020b
        L_0x0227:
            byte[] r10 = r7.key     // Catch:{ Exception -> 0x0490 }
            if (r10 == 0) goto L_0x0254
            java.nio.ByteBuffer r10 = r0.buffer     // Catch:{ Exception -> 0x0490 }
            byte[] r14 = r7.key     // Catch:{ Exception -> 0x0490 }
            byte[] r15 = r7.iv     // Catch:{ Exception -> 0x0490 }
            r22 = 0
            r23 = 1
            r24 = 0
            int r25 = r0.limit()     // Catch:{ Exception -> 0x0490 }
            r19 = r10
            r20 = r14
            r21 = r15
            org.telegram.messenger.Utilities.aesIgeEncryption(r19, r20, r21, r22, r23, r24, r25)     // Catch:{ Exception -> 0x0490 }
            if (r8 == 0) goto L_0x0254
            int r10 = r7.bytesCountPadding     // Catch:{ Exception -> 0x0490 }
            if (r10 == 0) goto L_0x0254
            int r10 = r0.limit()     // Catch:{ Exception -> 0x0490 }
            int r14 = r7.bytesCountPadding     // Catch:{ Exception -> 0x0490 }
            int r10 = r10 - r14
            r0.limit(r10)     // Catch:{ Exception -> 0x0490 }
        L_0x0254:
            boolean r10 = r7.encryptFile     // Catch:{ Exception -> 0x0490 }
            if (r10 == 0) goto L_0x028f
            int r10 = r27.offset     // Catch:{ Exception -> 0x0490 }
            int r10 = r10 / 16
            byte[] r14 = r7.encryptIv     // Catch:{ Exception -> 0x0490 }
            r15 = r10 & 255(0xff, float:3.57E-43)
            byte r15 = (byte) r15     // Catch:{ Exception -> 0x0490 }
            r14[r17] = r15     // Catch:{ Exception -> 0x0490 }
            byte[] r14 = r7.encryptIv     // Catch:{ Exception -> 0x0490 }
            int r15 = r10 >> 8
            r15 = r15 & 255(0xff, float:3.57E-43)
            byte r15 = (byte) r15     // Catch:{ Exception -> 0x0490 }
            r14[r16] = r15     // Catch:{ Exception -> 0x0490 }
            byte[] r14 = r7.encryptIv     // Catch:{ Exception -> 0x0490 }
            int r15 = r10 >> 16
            r15 = r15 & 255(0xff, float:3.57E-43)
            byte r15 = (byte) r15     // Catch:{ Exception -> 0x0490 }
            r16 = 13
            r14[r16] = r15     // Catch:{ Exception -> 0x0490 }
            byte[] r14 = r7.encryptIv     // Catch:{ Exception -> 0x0490 }
            int r10 = r10 >> 24
            r10 = r10 & 255(0xff, float:3.57E-43)
            byte r10 = (byte) r10     // Catch:{ Exception -> 0x0490 }
            r14[r18] = r10     // Catch:{ Exception -> 0x0490 }
            java.nio.ByteBuffer r10 = r0.buffer     // Catch:{ Exception -> 0x0490 }
            byte[] r14 = r7.encryptKey     // Catch:{ Exception -> 0x0490 }
            byte[] r15 = r7.encryptIv     // Catch:{ Exception -> 0x0490 }
            int r12 = r0.limit()     // Catch:{ Exception -> 0x0490 }
            org.telegram.messenger.Utilities.aesCtrDecryption(r10, r14, r15, r9, r12)     // Catch:{ Exception -> 0x0490 }
        L_0x028f:
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$Range> r10 = r7.notLoadedBytesRanges     // Catch:{ Exception -> 0x0490 }
            if (r10 == 0) goto L_0x02c1
            java.io.RandomAccessFile r10 = r7.fileOutputStream     // Catch:{ Exception -> 0x0490 }
            int r12 = r27.offset     // Catch:{ Exception -> 0x0490 }
            long r14 = (long) r12     // Catch:{ Exception -> 0x0490 }
            r10.seek(r14)     // Catch:{ Exception -> 0x0490 }
            boolean r10 = org.telegram.messenger.BuildVars.DEBUG_VERSION     // Catch:{ Exception -> 0x0490 }
            if (r10 == 0) goto L_0x02c1
            java.lang.StringBuilder r10 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0490 }
            r10.<init>()     // Catch:{ Exception -> 0x0490 }
            java.lang.String r12 = "save file part "
            r10.append(r12)     // Catch:{ Exception -> 0x0490 }
            java.io.File r12 = r7.cacheFileFinal     // Catch:{ Exception -> 0x0490 }
            r10.append(r12)     // Catch:{ Exception -> 0x0490 }
            r10.append(r2)     // Catch:{ Exception -> 0x0490 }
            int r2 = r27.offset     // Catch:{ Exception -> 0x0490 }
            r10.append(r2)     // Catch:{ Exception -> 0x0490 }
            java.lang.String r2 = r10.toString()     // Catch:{ Exception -> 0x0490 }
            org.telegram.messenger.FileLog.d(r2)     // Catch:{ Exception -> 0x0490 }
        L_0x02c1:
            java.io.RandomAccessFile r2 = r7.fileOutputStream     // Catch:{ Exception -> 0x0490 }
            java.nio.channels.FileChannel r2 = r2.getChannel()     // Catch:{ Exception -> 0x0490 }
            java.nio.ByteBuffer r0 = r0.buffer     // Catch:{ Exception -> 0x0490 }
            r2.write(r0)     // Catch:{ Exception -> 0x0490 }
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$Range> r0 = r7.notLoadedBytesRanges     // Catch:{ Exception -> 0x0490 }
            int r2 = r27.offset     // Catch:{ Exception -> 0x0490 }
            int r10 = r27.offset     // Catch:{ Exception -> 0x0490 }
            int r10 = r10 + r13
            r12 = 1
            r7.addPart(r0, r2, r10, r12)     // Catch:{ Exception -> 0x0490 }
            boolean r0 = r7.isCdn     // Catch:{ Exception -> 0x0490 }
            if (r0 == 0) goto L_0x03ea
            int r0 = r27.offset     // Catch:{ Exception -> 0x0490 }
            r2 = 131072(0x20000, float:1.83671E-40)
            int r0 = r0 / r2
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$Range> r2 = r7.notCheckedCdnRanges     // Catch:{ Exception -> 0x0490 }
            int r2 = r2.size()     // Catch:{ Exception -> 0x0490 }
            r3 = 0
        L_0x02ed:
            if (r3 >= r2) goto L_0x0308
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$Range> r10 = r7.notCheckedCdnRanges     // Catch:{ Exception -> 0x0490 }
            java.lang.Object r10 = r10.get(r3)     // Catch:{ Exception -> 0x0490 }
            org.telegram.messenger.FileLoadOperation$Range r10 = (org.telegram.messenger.FileLoadOperation.Range) r10     // Catch:{ Exception -> 0x0490 }
            int r12 = r10.start     // Catch:{ Exception -> 0x0490 }
            if (r12 > r0) goto L_0x0305
            int r10 = r10.end     // Catch:{ Exception -> 0x0490 }
            if (r0 > r10) goto L_0x0305
            r2 = 0
            goto L_0x0309
        L_0x0305:
            int r3 = r3 + 1
            goto L_0x02ed
        L_0x0308:
            r2 = 1
        L_0x0309:
            if (r2 != 0) goto L_0x03ea
            r2 = 131072(0x20000, float:1.83671E-40)
            int r15 = r0 * r2
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$Range> r3 = r7.notLoadedBytesRanges     // Catch:{ Exception -> 0x0490 }
            int r3 = r7.getDownloadedLengthFromOffsetInternal(r3, r15, r2)     // Catch:{ Exception -> 0x0490 }
            if (r3 == 0) goto L_0x03ea
            if (r3 == r2) goto L_0x0328
            int r2 = r7.totalBytesCount     // Catch:{ Exception -> 0x0490 }
            if (r2 <= 0) goto L_0x0322
            int r2 = r7.totalBytesCount     // Catch:{ Exception -> 0x0490 }
            int r2 = r2 - r15
            if (r3 == r2) goto L_0x0328
        L_0x0322:
            int r2 = r7.totalBytesCount     // Catch:{ Exception -> 0x0490 }
            if (r2 > 0) goto L_0x03ea
            if (r8 == 0) goto L_0x03ea
        L_0x0328:
            android.util.SparseArray<org.telegram.tgnet.TLRPC$TL_fileHash> r2 = r7.cdnHashes     // Catch:{ Exception -> 0x0490 }
            java.lang.Object r2 = r2.get(r15)     // Catch:{ Exception -> 0x0490 }
            org.telegram.tgnet.TLRPC$TL_fileHash r2 = (org.telegram.tgnet.TLRPC$TL_fileHash) r2     // Catch:{ Exception -> 0x0490 }
            java.io.RandomAccessFile r10 = r7.fileReadStream     // Catch:{ Exception -> 0x0490 }
            if (r10 != 0) goto L_0x0345
            r10 = 131072(0x20000, float:1.83671E-40)
            byte[] r10 = new byte[r10]     // Catch:{ Exception -> 0x0490 }
            r7.cdnCheckBytes = r10     // Catch:{ Exception -> 0x0490 }
            java.io.RandomAccessFile r10 = new java.io.RandomAccessFile     // Catch:{ Exception -> 0x0490 }
            java.io.File r12 = r7.cacheFileTemp     // Catch:{ Exception -> 0x0490 }
            java.lang.String r13 = "r"
            r10.<init>(r12, r13)     // Catch:{ Exception -> 0x0490 }
            r7.fileReadStream = r10     // Catch:{ Exception -> 0x0490 }
        L_0x0345:
            java.io.RandomAccessFile r10 = r7.fileReadStream     // Catch:{ Exception -> 0x0490 }
            long r12 = (long) r15     // Catch:{ Exception -> 0x0490 }
            r10.seek(r12)     // Catch:{ Exception -> 0x0490 }
            java.io.RandomAccessFile r10 = r7.fileReadStream     // Catch:{ Exception -> 0x0490 }
            byte[] r12 = r7.cdnCheckBytes     // Catch:{ Exception -> 0x0490 }
            r10.readFully(r12, r9, r3)     // Catch:{ Exception -> 0x0490 }
            byte[] r10 = r7.cdnCheckBytes     // Catch:{ Exception -> 0x0490 }
            byte[] r3 = org.telegram.messenger.Utilities.computeSHA256(r10, r9, r3)     // Catch:{ Exception -> 0x0490 }
            byte[] r2 = r2.hash     // Catch:{ Exception -> 0x0490 }
            boolean r2 = java.util.Arrays.equals(r3, r2)     // Catch:{ Exception -> 0x0490 }
            if (r2 != 0) goto L_0x03de
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x0490 }
            if (r0 == 0) goto L_0x03d5
            org.telegram.tgnet.TLRPC$InputFileLocation r0 = r7.location     // Catch:{ Exception -> 0x0490 }
            if (r0 == 0) goto L_0x03b1
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0490 }
            r0.<init>()     // Catch:{ Exception -> 0x0490 }
            java.lang.String r2 = "invalid cdn hash "
            r0.append(r2)     // Catch:{ Exception -> 0x0490 }
            org.telegram.tgnet.TLRPC$InputFileLocation r2 = r7.location     // Catch:{ Exception -> 0x0490 }
            r0.append(r2)     // Catch:{ Exception -> 0x0490 }
            r0.append(r11)     // Catch:{ Exception -> 0x0490 }
            org.telegram.tgnet.TLRPC$InputFileLocation r2 = r7.location     // Catch:{ Exception -> 0x0490 }
            long r2 = r2.id     // Catch:{ Exception -> 0x0490 }
            r0.append(r2)     // Catch:{ Exception -> 0x0490 }
            r0.append(r6)     // Catch:{ Exception -> 0x0490 }
            org.telegram.tgnet.TLRPC$InputFileLocation r2 = r7.location     // Catch:{ Exception -> 0x0490 }
            int r2 = r2.local_id     // Catch:{ Exception -> 0x0490 }
            r0.append(r2)     // Catch:{ Exception -> 0x0490 }
            r0.append(r5)     // Catch:{ Exception -> 0x0490 }
            org.telegram.tgnet.TLRPC$InputFileLocation r2 = r7.location     // Catch:{ Exception -> 0x0490 }
            long r2 = r2.access_hash     // Catch:{ Exception -> 0x0490 }
            r0.append(r2)     // Catch:{ Exception -> 0x0490 }
            r0.append(r4)     // Catch:{ Exception -> 0x0490 }
            org.telegram.tgnet.TLRPC$InputFileLocation r2 = r7.location     // Catch:{ Exception -> 0x0490 }
            long r2 = r2.volume_id     // Catch:{ Exception -> 0x0490 }
            r0.append(r2)     // Catch:{ Exception -> 0x0490 }
            r0.append(r1)     // Catch:{ Exception -> 0x0490 }
            org.telegram.tgnet.TLRPC$InputFileLocation r1 = r7.location     // Catch:{ Exception -> 0x0490 }
            long r1 = r1.secret     // Catch:{ Exception -> 0x0490 }
            r0.append(r1)     // Catch:{ Exception -> 0x0490 }
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x0490 }
            org.telegram.messenger.FileLog.e((java.lang.String) r0)     // Catch:{ Exception -> 0x0490 }
            goto L_0x03d5
        L_0x03b1:
            org.telegram.tgnet.TLRPC$InputWebFileLocation r0 = r7.webLocation     // Catch:{ Exception -> 0x0490 }
            if (r0 == 0) goto L_0x03d5
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0490 }
            r0.<init>()     // Catch:{ Exception -> 0x0490 }
            java.lang.String r1 = "invalid cdn hash  "
            r0.append(r1)     // Catch:{ Exception -> 0x0490 }
            org.telegram.tgnet.TLRPC$InputWebFileLocation r1 = r7.webLocation     // Catch:{ Exception -> 0x0490 }
            r0.append(r1)     // Catch:{ Exception -> 0x0490 }
            r0.append(r11)     // Catch:{ Exception -> 0x0490 }
            java.lang.String r1 = r26.getFileName()     // Catch:{ Exception -> 0x0490 }
            r0.append(r1)     // Catch:{ Exception -> 0x0490 }
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x0490 }
            org.telegram.messenger.FileLog.e((java.lang.String) r0)     // Catch:{ Exception -> 0x0490 }
        L_0x03d5:
            r7.onFail(r9, r9)     // Catch:{ Exception -> 0x0490 }
            java.io.File r0 = r7.cacheFileTemp     // Catch:{ Exception -> 0x0490 }
            r0.delete()     // Catch:{ Exception -> 0x0490 }
            return r9
        L_0x03de:
            android.util.SparseArray<org.telegram.tgnet.TLRPC$TL_fileHash> r1 = r7.cdnHashes     // Catch:{ Exception -> 0x0490 }
            r1.remove(r15)     // Catch:{ Exception -> 0x0490 }
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$Range> r1 = r7.notCheckedCdnRanges     // Catch:{ Exception -> 0x0490 }
            int r2 = r0 + 1
            r7.addPart(r1, r0, r2, r9)     // Catch:{ Exception -> 0x0490 }
        L_0x03ea:
            java.io.RandomAccessFile r0 = r7.fiv     // Catch:{ Exception -> 0x0490 }
            if (r0 == 0) goto L_0x03fc
            java.io.RandomAccessFile r0 = r7.fiv     // Catch:{ Exception -> 0x0490 }
            r1 = 0
            r0.seek(r1)     // Catch:{ Exception -> 0x0490 }
            java.io.RandomAccessFile r0 = r7.fiv     // Catch:{ Exception -> 0x0490 }
            byte[] r1 = r7.iv     // Catch:{ Exception -> 0x0490 }
            r0.write(r1)     // Catch:{ Exception -> 0x0490 }
        L_0x03fc:
            int r0 = r7.totalBytesCount     // Catch:{ Exception -> 0x0490 }
            if (r0 <= 0) goto L_0x0415
            int r0 = r7.state     // Catch:{ Exception -> 0x0490 }
            r1 = 1
            if (r0 != r1) goto L_0x0415
            r26.copyNotLoadedRanges()     // Catch:{ Exception -> 0x0490 }
            org.telegram.messenger.FileLoadOperation$FileLoadOperationDelegate r1 = r7.delegate     // Catch:{ Exception -> 0x0490 }
            int r0 = r7.downloadedBytes     // Catch:{ Exception -> 0x0490 }
            long r3 = (long) r0     // Catch:{ Exception -> 0x0490 }
            int r0 = r7.totalBytesCount     // Catch:{ Exception -> 0x0490 }
            long r5 = (long) r0     // Catch:{ Exception -> 0x0490 }
            r2 = r26
            r1.didChangedLoadProgress(r2, r3, r5)     // Catch:{ Exception -> 0x0490 }
        L_0x0415:
            r0 = r8
        L_0x0416:
            r1 = 0
        L_0x0417:
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$RequestInfo> r2 = r7.delayedRequestInfos     // Catch:{ Exception -> 0x0490 }
            int r2 = r2.size()     // Catch:{ Exception -> 0x0490 }
            if (r1 >= r2) goto L_0x047e
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$RequestInfo> r2 = r7.delayedRequestInfos     // Catch:{ Exception -> 0x0490 }
            java.lang.Object r2 = r2.get(r1)     // Catch:{ Exception -> 0x0490 }
            org.telegram.messenger.FileLoadOperation$RequestInfo r2 = (org.telegram.messenger.FileLoadOperation.RequestInfo) r2     // Catch:{ Exception -> 0x0490 }
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$Range> r3 = r7.notLoadedBytesRanges     // Catch:{ Exception -> 0x0490 }
            if (r3 != 0) goto L_0x0437
            int r3 = r7.downloadedBytes     // Catch:{ Exception -> 0x0490 }
            int r4 = r2.offset     // Catch:{ Exception -> 0x0490 }
            if (r3 != r4) goto L_0x0434
            goto L_0x0437
        L_0x0434:
            int r1 = r1 + 1
            goto L_0x0417
        L_0x0437:
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$RequestInfo> r3 = r7.delayedRequestInfos     // Catch:{ Exception -> 0x0490 }
            r3.remove(r1)     // Catch:{ Exception -> 0x0490 }
            r3 = 0
            boolean r1 = r7.processRequestResult(r2, r3)     // Catch:{ Exception -> 0x0490 }
            if (r1 != 0) goto L_0x047e
            org.telegram.tgnet.TLRPC$TL_upload_file r1 = r2.response     // Catch:{ Exception -> 0x0490 }
            if (r1 == 0) goto L_0x0457
            org.telegram.tgnet.TLRPC$TL_upload_file r1 = r2.response     // Catch:{ Exception -> 0x0490 }
            r1.disableFree = r9     // Catch:{ Exception -> 0x0490 }
            org.telegram.tgnet.TLRPC$TL_upload_file r1 = r2.response     // Catch:{ Exception -> 0x0490 }
            r1.freeResources()     // Catch:{ Exception -> 0x0490 }
            goto L_0x047e
        L_0x0457:
            org.telegram.tgnet.TLRPC$TL_upload_webFile r1 = r2.responseWeb     // Catch:{ Exception -> 0x0490 }
            if (r1 == 0) goto L_0x046b
            org.telegram.tgnet.TLRPC$TL_upload_webFile r1 = r2.responseWeb     // Catch:{ Exception -> 0x0490 }
            r1.disableFree = r9     // Catch:{ Exception -> 0x0490 }
            org.telegram.tgnet.TLRPC$TL_upload_webFile r1 = r2.responseWeb     // Catch:{ Exception -> 0x0490 }
            r1.freeResources()     // Catch:{ Exception -> 0x0490 }
            goto L_0x047e
        L_0x046b:
            org.telegram.tgnet.TLRPC$TL_upload_cdnFile r1 = r2.responseCdn     // Catch:{ Exception -> 0x0490 }
            if (r1 == 0) goto L_0x047e
            org.telegram.tgnet.TLRPC$TL_upload_cdnFile r1 = r2.responseCdn     // Catch:{ Exception -> 0x0490 }
            r1.disableFree = r9     // Catch:{ Exception -> 0x0490 }
            org.telegram.tgnet.TLRPC$TL_upload_cdnFile r1 = r2.responseCdn     // Catch:{ Exception -> 0x0490 }
            r1.freeResources()     // Catch:{ Exception -> 0x0490 }
        L_0x047e:
            if (r0 == 0) goto L_0x0486
            r0 = 1
            r7.onFinishLoadingFile(r0)     // Catch:{ Exception -> 0x0490 }
            goto L_0x058a
        L_0x0486:
            r26.startDownloadRequest()     // Catch:{ Exception -> 0x0490 }
            goto L_0x058a
        L_0x048b:
            r0 = 1
            r7.onFinishLoadingFile(r0)     // Catch:{ Exception -> 0x0490 }
            return r9
        L_0x0490:
            r0 = move-exception
            r7.onFail(r9, r9)
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x058a
        L_0x0499:
            r3 = r12
            java.lang.String r2 = r0.text
            java.lang.String r8 = "FILE_MIGRATE_"
            boolean r2 = r2.contains(r8)
            if (r2 == 0) goto L_0x04d4
            java.lang.String r0 = r0.text
            java.lang.String r1 = ""
            java.lang.String r0 = r0.replace(r8, r1)
            java.util.Scanner r2 = new java.util.Scanner
            r2.<init>(r0)
            r2.useDelimiter(r1)
            int r0 = r2.nextInt()     // Catch:{ Exception -> 0x04bd }
            java.lang.Integer r12 = java.lang.Integer.valueOf(r0)     // Catch:{ Exception -> 0x04bd }
            goto L_0x04be
        L_0x04bd:
            r12 = r3
        L_0x04be:
            if (r12 != 0) goto L_0x04c5
            r7.onFail(r9, r9)
            goto L_0x058a
        L_0x04c5:
            int r0 = r12.intValue()
            r7.datacenterId = r0
            r7.downloadedBytes = r9
            r7.requestedBytesCount = r9
            r26.startDownloadRequest()
            goto L_0x058a
        L_0x04d4:
            java.lang.String r2 = r0.text
            java.lang.String r3 = "OFFSET_INVALID"
            boolean r2 = r2.contains(r3)
            if (r2 == 0) goto L_0x04fa
            int r0 = r7.downloadedBytes
            int r1 = r7.currentDownloadChunkSize
            int r0 = r0 % r1
            if (r0 != 0) goto L_0x04f5
            r0 = 1
            r7.onFinishLoadingFile(r0)     // Catch:{ Exception -> 0x04eb }
            goto L_0x058a
        L_0x04eb:
            r0 = move-exception
            r1 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)
            r7.onFail(r9, r9)
            goto L_0x058a
        L_0x04f5:
            r7.onFail(r9, r9)
            goto L_0x058a
        L_0x04fa:
            java.lang.String r2 = r0.text
            java.lang.String r3 = "RETRY_LIMIT"
            boolean r2 = r2.contains(r3)
            if (r2 == 0) goto L_0x050a
            r2 = 2
            r7.onFail(r9, r2)
            goto L_0x058a
        L_0x050a:
            boolean r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r2 == 0) goto L_0x0587
            org.telegram.tgnet.TLRPC$InputFileLocation r2 = r7.location
            java.lang.String r3 = " "
            if (r2 == 0) goto L_0x0560
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r0 = r0.text
            r2.append(r0)
            r2.append(r3)
            org.telegram.tgnet.TLRPC$InputFileLocation r0 = r7.location
            r2.append(r0)
            r2.append(r11)
            org.telegram.tgnet.TLRPC$InputFileLocation r0 = r7.location
            long r10 = r0.id
            r2.append(r10)
            r2.append(r6)
            org.telegram.tgnet.TLRPC$InputFileLocation r0 = r7.location
            int r0 = r0.local_id
            r2.append(r0)
            r2.append(r5)
            org.telegram.tgnet.TLRPC$InputFileLocation r0 = r7.location
            long r5 = r0.access_hash
            r2.append(r5)
            r2.append(r4)
            org.telegram.tgnet.TLRPC$InputFileLocation r0 = r7.location
            long r3 = r0.volume_id
            r2.append(r3)
            r2.append(r1)
            org.telegram.tgnet.TLRPC$InputFileLocation r0 = r7.location
            long r0 = r0.secret
            r2.append(r0)
            java.lang.String r0 = r2.toString()
            org.telegram.messenger.FileLog.e((java.lang.String) r0)
            goto L_0x0587
        L_0x0560:
            org.telegram.tgnet.TLRPC$InputWebFileLocation r1 = r7.webLocation
            if (r1 == 0) goto L_0x0587
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r0 = r0.text
            r1.append(r0)
            r1.append(r3)
            org.telegram.tgnet.TLRPC$InputWebFileLocation r0 = r7.webLocation
            r1.append(r0)
            r1.append(r11)
            java.lang.String r0 = r26.getFileName()
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            org.telegram.messenger.FileLog.e((java.lang.String) r0)
        L_0x0587:
            r7.onFail(r9, r9)
        L_0x058a:
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
                public final /* synthetic */ int f$1;

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
        TLRPC$WebPage tLRPC$WebPage;
        if (!this.requestingReference) {
            clearOperaion(requestInfo, false);
            this.requestingReference = true;
            Object obj = this.parentObject;
            if (obj instanceof MessageObject) {
                MessageObject messageObject = (MessageObject) obj;
                if (messageObject.getId() < 0 && (tLRPC$WebPage = messageObject.messageOwner.media.webpage) != null) {
                    this.parentObject = tLRPC$WebPage;
                }
            }
            FileRefController.getInstance(this.currentAccount).requestReference(this.parentObject, this.location, this, requestInfo);
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v8, resolved type: org.telegram.tgnet.TLRPC$TL_upload_getWebFile} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v9, resolved type: org.telegram.tgnet.TLRPC$TL_upload_getFile} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v18, resolved type: org.telegram.tgnet.TLRPC$TL_upload_getCdnFile} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v43, resolved type: org.telegram.tgnet.TLRPC$TL_upload_getFile} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v44, resolved type: org.telegram.tgnet.TLRPC$TL_upload_getFile} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v45, resolved type: org.telegram.tgnet.TLRPC$TL_upload_getFile} */
    /* access modifiers changed from: protected */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void startDownloadRequest() {
        /*
            r19 = this;
            r0 = r19
            boolean r1 = r0.paused
            if (r1 != 0) goto L_0x028c
            boolean r1 = r0.reuploadingCdn
            if (r1 != 0) goto L_0x028c
            int r1 = r0.state
            r2 = 1
            if (r1 != r2) goto L_0x028c
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
            if (r1 >= r4) goto L_0x028c
        L_0x002a:
            boolean r1 = r0.isPreloadVideoOperation
            if (r1 == 0) goto L_0x0040
            int r1 = r0.requestedBytesCount
            if (r1 > r3) goto L_0x028c
            int r1 = r0.moovFound
            if (r1 == 0) goto L_0x0040
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$RequestInfo> r1 = r0.requestInfos
            int r1 = r1.size()
            if (r1 <= 0) goto L_0x0040
            goto L_0x028c
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
            if (r5 >= r1) goto L_0x028c
            boolean r6 = r0.isPreloadVideoOperation
            r7 = 2
            if (r6 == 0) goto L_0x00fa
            int r6 = r0.moovFound
            if (r6 == 0) goto L_0x0075
            int r6 = r0.preloadNotRequestedBytesCount
            if (r6 > 0) goto L_0x0075
            return
        L_0x0075:
            int r6 = r0.nextPreloadDownloadOffset
            r8 = -1
            if (r6 != r8) goto L_0x00b6
            int r6 = r0.currentDownloadChunkSize
            int r6 = r3 / r6
            int r6 = r6 + r7
            r8 = 0
        L_0x0080:
            if (r6 == 0) goto L_0x00a7
            android.util.SparseIntArray r9 = r0.requestedPreloadedBytesRanges
            int r9 = r9.get(r8, r4)
            if (r9 != 0) goto L_0x008d
            r6 = r8
            r8 = 1
            goto L_0x00a9
        L_0x008d:
            int r9 = r0.currentDownloadChunkSize
            int r8 = r8 + r9
            int r10 = r0.totalBytesCount
            if (r8 <= r10) goto L_0x0095
            goto L_0x00a7
        L_0x0095:
            int r11 = r0.moovFound
            if (r11 != r7) goto L_0x00a4
            int r11 = r9 * 8
            if (r8 != r11) goto L_0x00a4
            r8 = 1048576(0x100000, float:1.469368E-39)
            int r10 = r10 - r8
            int r10 = r10 / r9
            int r10 = r10 * r9
            r8 = r10
        L_0x00a4:
            int r6 = r6 + -1
            goto L_0x0080
        L_0x00a7:
            r6 = r8
            r8 = 0
        L_0x00a9:
            if (r8 != 0) goto L_0x00b6
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$RequestInfo> r8 = r0.requestInfos
            boolean r8 = r8.isEmpty()
            if (r8 == 0) goto L_0x00b6
            r0.onFinishLoadingFile(r4)
        L_0x00b6:
            android.util.SparseIntArray r8 = r0.requestedPreloadedBytesRanges
            if (r8 != 0) goto L_0x00c1
            android.util.SparseIntArray r8 = new android.util.SparseIntArray
            r8.<init>()
            r0.requestedPreloadedBytesRanges = r8
        L_0x00c1:
            android.util.SparseIntArray r8 = r0.requestedPreloadedBytesRanges
            r8.put(r6, r2)
            boolean r8 = org.telegram.messenger.BuildVars.DEBUG_VERSION
            if (r8 == 0) goto L_0x00f2
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
        L_0x00f2:
            int r8 = r0.preloadNotRequestedBytesCount
            int r9 = r0.currentDownloadChunkSize
            int r8 = r8 - r9
            r0.preloadNotRequestedBytesCount = r8
            goto L_0x0156
        L_0x00fa:
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$Range> r6 = r0.notRequestedBytesRanges
            if (r6 == 0) goto L_0x0154
            int r6 = r0.streamPriorityStartOffset
            if (r6 == 0) goto L_0x0103
            goto L_0x0105
        L_0x0103:
            int r6 = r0.streamStartOffset
        L_0x0105:
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$Range> r8 = r0.notRequestedBytesRanges
            int r8 = r8.size()
            r9 = 2147483647(0x7fffffff, float:NaN)
            r10 = 0
            r11 = 2147483647(0x7fffffff, float:NaN)
            r12 = 2147483647(0x7fffffff, float:NaN)
        L_0x0115:
            if (r10 >= r8) goto L_0x014c
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$Range> r13 = r0.notRequestedBytesRanges
            java.lang.Object r13 = r13.get(r10)
            org.telegram.messenger.FileLoadOperation$Range r13 = (org.telegram.messenger.FileLoadOperation.Range) r13
            if (r6 == 0) goto L_0x0141
            int r14 = r13.start
            if (r14 > r6) goto L_0x0131
            int r14 = r13.end
            if (r14 <= r6) goto L_0x0131
            r12 = 2147483647(0x7fffffff, float:NaN)
            goto L_0x014d
        L_0x0131:
            int r14 = r13.start
            if (r6 >= r14) goto L_0x0141
            int r14 = r13.start
            if (r14 >= r11) goto L_0x0141
            int r11 = r13.start
        L_0x0141:
            int r13 = r13.start
            int r12 = java.lang.Math.min(r12, r13)
            int r10 = r10 + 1
            goto L_0x0115
        L_0x014c:
            r6 = r11
        L_0x014d:
            if (r6 == r9) goto L_0x0150
            goto L_0x0156
        L_0x0150:
            if (r12 == r9) goto L_0x028c
            r6 = r12
            goto L_0x0156
        L_0x0154:
            int r6 = r0.requestedBytesCount
        L_0x0156:
            boolean r8 = r0.isPreloadVideoOperation
            if (r8 != 0) goto L_0x0164
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$Range> r8 = r0.notRequestedBytesRanges
            if (r8 == 0) goto L_0x0164
            int r9 = r0.currentDownloadChunkSize
            int r9 = r9 + r6
            r0.addPart(r8, r6, r9, r4)
        L_0x0164:
            int r8 = r0.totalBytesCount
            if (r8 <= 0) goto L_0x016c
            if (r6 < r8) goto L_0x016c
            goto L_0x028c
        L_0x016c:
            int r8 = r0.totalBytesCount
            if (r8 <= 0) goto L_0x017f
            int r9 = r1 + -1
            if (r5 == r9) goto L_0x017f
            if (r8 <= 0) goto L_0x017c
            int r9 = r0.currentDownloadChunkSize
            int r9 = r9 + r6
            if (r9 < r8) goto L_0x017c
            goto L_0x017f
        L_0x017c:
            r18 = 0
            goto L_0x0181
        L_0x017f:
            r18 = 1
        L_0x0181:
            int r8 = r0.requestsCount
            int r8 = r8 % r7
            if (r8 != 0) goto L_0x0189
            r17 = 2
            goto L_0x018f
        L_0x0189:
            r7 = 65538(0x10002, float:9.1838E-41)
            r17 = 65538(0x10002, float:9.1838E-41)
        L_0x018f:
            boolean r7 = r0.isForceRequest
            if (r7 == 0) goto L_0x0196
            r7 = 32
            goto L_0x0197
        L_0x0196:
            r7 = 0
        L_0x0197:
            boolean r8 = r0.isCdn
            if (r8 == 0) goto L_0x01af
            org.telegram.tgnet.TLRPC$TL_upload_getCdnFile r8 = new org.telegram.tgnet.TLRPC$TL_upload_getCdnFile
            r8.<init>()
            byte[] r9 = r0.cdnToken
            r8.file_token = r9
            r8.offset = r6
            int r9 = r0.currentDownloadChunkSize
            r8.limit = r9
            r7 = r7 | 1
        L_0x01ac:
            r15 = r7
            r11 = r8
            goto L_0x01d5
        L_0x01af:
            org.telegram.tgnet.TLRPC$InputWebFileLocation r8 = r0.webLocation
            if (r8 == 0) goto L_0x01c3
            org.telegram.tgnet.TLRPC$TL_upload_getWebFile r8 = new org.telegram.tgnet.TLRPC$TL_upload_getWebFile
            r8.<init>()
            org.telegram.tgnet.TLRPC$InputWebFileLocation r9 = r0.webLocation
            r8.location = r9
            r8.offset = r6
            int r9 = r0.currentDownloadChunkSize
            r8.limit = r9
            goto L_0x01ac
        L_0x01c3:
            org.telegram.tgnet.TLRPC$TL_upload_getFile r8 = new org.telegram.tgnet.TLRPC$TL_upload_getFile
            r8.<init>()
            org.telegram.tgnet.TLRPC$InputFileLocation r9 = r0.location
            r8.location = r9
            r8.offset = r6
            int r9 = r0.currentDownloadChunkSize
            r8.limit = r9
            r8.cdn_supported = r2
            goto L_0x01ac
        L_0x01d5:
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
            if (r6 != 0) goto L_0x0242
            boolean r6 = r0.supportsPreloading
            if (r6 == 0) goto L_0x0242
            java.io.RandomAccessFile r6 = r0.preloadStream
            if (r6 == 0) goto L_0x0242
            android.util.SparseArray<org.telegram.messenger.FileLoadOperation$PreloadRange> r6 = r0.preloadedBytesRanges
            if (r6 == 0) goto L_0x0242
            int r8 = r7.offset
            java.lang.Object r6 = r6.get(r8)
            org.telegram.messenger.FileLoadOperation$PreloadRange r6 = (org.telegram.messenger.FileLoadOperation.PreloadRange) r6
            if (r6 == 0) goto L_0x0242
            org.telegram.tgnet.TLRPC$TL_upload_file r8 = new org.telegram.tgnet.TLRPC$TL_upload_file
            r8.<init>()
            org.telegram.tgnet.TLRPC$TL_upload_file unused = r7.response = r8
            org.telegram.tgnet.NativeByteBuffer r8 = new org.telegram.tgnet.NativeByteBuffer     // Catch:{ Exception -> 0x0241 }
            int r9 = r6.length     // Catch:{ Exception -> 0x0241 }
            r8.<init>((int) r9)     // Catch:{ Exception -> 0x0241 }
            java.io.RandomAccessFile r9 = r0.preloadStream     // Catch:{ Exception -> 0x0241 }
            int r6 = r6.fileOffset     // Catch:{ Exception -> 0x0241 }
            long r12 = (long) r6     // Catch:{ Exception -> 0x0241 }
            r9.seek(r12)     // Catch:{ Exception -> 0x0241 }
            java.io.RandomAccessFile r6 = r0.preloadStream     // Catch:{ Exception -> 0x0241 }
            java.nio.channels.FileChannel r6 = r6.getChannel()     // Catch:{ Exception -> 0x0241 }
            java.nio.ByteBuffer r9 = r8.buffer     // Catch:{ Exception -> 0x0241 }
            r6.read(r9)     // Catch:{ Exception -> 0x0241 }
            java.nio.ByteBuffer r6 = r8.buffer     // Catch:{ Exception -> 0x0241 }
            r6.position(r4)     // Catch:{ Exception -> 0x0241 }
            org.telegram.tgnet.TLRPC$TL_upload_file r6 = r7.response     // Catch:{ Exception -> 0x0241 }
            r6.bytes = r8     // Catch:{ Exception -> 0x0241 }
            org.telegram.messenger.DispatchQueue r6 = org.telegram.messenger.Utilities.stageQueue     // Catch:{ Exception -> 0x0241 }
            org.telegram.messenger.-$$Lambda$FileLoadOperation$_a8OwTWoM7783M1Mt0pXbFF_RXY r8 = new org.telegram.messenger.-$$Lambda$FileLoadOperation$_a8OwTWoM7783M1Mt0pXbFF_RXY     // Catch:{ Exception -> 0x0241 }
            r8.<init>(r7)     // Catch:{ Exception -> 0x0241 }
            r6.postRunnable(r8)     // Catch:{ Exception -> 0x0241 }
            goto L_0x0288
        L_0x0241:
        L_0x0242:
            int r6 = r0.streamPriorityStartOffset
            if (r6 == 0) goto L_0x0264
            boolean r6 = org.telegram.messenger.BuildVars.DEBUG_VERSION
            if (r6 == 0) goto L_0x0260
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            java.lang.String r8 = "frame get offset = "
            r6.append(r8)
            int r8 = r0.streamPriorityStartOffset
            r6.append(r8)
            java.lang.String r6 = r6.toString()
            org.telegram.messenger.FileLog.d(r6)
        L_0x0260:
            r0.streamPriorityStartOffset = r4
            r0.priorityRequestInfo = r7
        L_0x0264:
            int r6 = r0.currentAccount
            org.telegram.tgnet.ConnectionsManager r10 = org.telegram.tgnet.ConnectionsManager.getInstance(r6)
            org.telegram.messenger.-$$Lambda$FileLoadOperation$q0OPLp-rp7G0uazoY5pveJXhQSk r12 = new org.telegram.messenger.-$$Lambda$FileLoadOperation$q0OPLp-rp7G0uazoY5pveJXhQSk
            r12.<init>(r7, r11)
            r13 = 0
            r14 = 0
            boolean r6 = r0.isCdn
            if (r6 == 0) goto L_0x0278
            int r6 = r0.cdnDatacenterId
            goto L_0x027a
        L_0x0278:
            int r6 = r0.datacenterId
        L_0x027a:
            r16 = r6
            int r6 = r10.sendRequest(r11, r12, r13, r14, r15, r16, r17, r18)
            int unused = r7.requestToken = r6
            int r6 = r0.requestsCount
            int r6 = r6 + r2
            r0.requestsCount = r6
        L_0x0288:
            int r5 = r5 + 1
            goto L_0x0065
        L_0x028c:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.FileLoadOperation.startDownloadRequest():void");
    }

    public /* synthetic */ void lambda$startDownloadRequest$11$FileLoadOperation(RequestInfo requestInfo) {
        processRequestResult(requestInfo, (TLRPC$TL_error) null);
        requestInfo.response.freeResources();
    }

    public /* synthetic */ void lambda$startDownloadRequest$13$FileLoadOperation(RequestInfo requestInfo, TLObject tLObject, TLObject tLObject2, TLRPC$TL_error tLRPC$TL_error) {
        byte[] bArr;
        if (this.requestInfos.contains(requestInfo)) {
            if (requestInfo == this.priorityRequestInfo) {
                if (BuildVars.DEBUG_VERSION) {
                    FileLog.d("frame get request completed " + this.priorityRequestInfo.offset);
                }
                this.priorityRequestInfo = null;
            }
            if (tLRPC$TL_error != null) {
                if (FileRefController.isFileRefError(tLRPC$TL_error.text)) {
                    requestReference(requestInfo);
                    return;
                } else if ((tLObject instanceof TLRPC$TL_upload_getCdnFile) && tLRPC$TL_error.text.equals("FILE_TOKEN_INVALID")) {
                    this.isCdn = false;
                    clearOperaion(requestInfo, false);
                    startDownloadRequest();
                    return;
                }
            }
            if (tLObject2 instanceof TLRPC$TL_upload_fileCdnRedirect) {
                TLRPC$TL_upload_fileCdnRedirect tLRPC$TL_upload_fileCdnRedirect = (TLRPC$TL_upload_fileCdnRedirect) tLObject2;
                if (!tLRPC$TL_upload_fileCdnRedirect.file_hashes.isEmpty()) {
                    if (this.cdnHashes == null) {
                        this.cdnHashes = new SparseArray<>();
                    }
                    for (int i = 0; i < tLRPC$TL_upload_fileCdnRedirect.file_hashes.size(); i++) {
                        TLRPC$TL_fileHash tLRPC$TL_fileHash = tLRPC$TL_upload_fileCdnRedirect.file_hashes.get(i);
                        this.cdnHashes.put(tLRPC$TL_fileHash.offset, tLRPC$TL_fileHash);
                    }
                }
                byte[] bArr2 = tLRPC$TL_upload_fileCdnRedirect.encryption_iv;
                if (bArr2 == null || (bArr = tLRPC$TL_upload_fileCdnRedirect.encryption_key) == null || bArr2.length != 16 || bArr.length != 32) {
                    TLRPC$TL_error tLRPC$TL_error2 = new TLRPC$TL_error();
                    tLRPC$TL_error2.text = "bad redirect response";
                    tLRPC$TL_error2.code = 400;
                    processRequestResult(requestInfo, tLRPC$TL_error2);
                    return;
                }
                this.isCdn = true;
                if (this.notCheckedCdnRanges == null) {
                    ArrayList<Range> arrayList = new ArrayList<>();
                    this.notCheckedCdnRanges = arrayList;
                    arrayList.add(new Range(0, 16000));
                }
                this.cdnDatacenterId = tLRPC$TL_upload_fileCdnRedirect.dc_id;
                this.cdnIv = tLRPC$TL_upload_fileCdnRedirect.encryption_iv;
                this.cdnKey = tLRPC$TL_upload_fileCdnRedirect.encryption_key;
                this.cdnToken = tLRPC$TL_upload_fileCdnRedirect.file_token;
                clearOperaion(requestInfo, false);
                startDownloadRequest();
            } else if (!(tLObject2 instanceof TLRPC$TL_upload_cdnFileReuploadNeeded)) {
                if (tLObject2 instanceof TLRPC$TL_upload_file) {
                    TLRPC$TL_upload_file unused = requestInfo.response = (TLRPC$TL_upload_file) tLObject2;
                } else if (tLObject2 instanceof TLRPC$TL_upload_webFile) {
                    TLRPC$TL_upload_webFile unused2 = requestInfo.responseWeb = (TLRPC$TL_upload_webFile) tLObject2;
                    if (this.totalBytesCount == 0 && requestInfo.responseWeb.size != 0) {
                        this.totalBytesCount = requestInfo.responseWeb.size;
                    }
                } else {
                    TLRPC$TL_upload_cdnFile unused3 = requestInfo.responseCdn = (TLRPC$TL_upload_cdnFile) tLObject2;
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
                processRequestResult(requestInfo, tLRPC$TL_error);
            } else if (!this.reuploadingCdn) {
                clearOperaion(requestInfo, false);
                this.reuploadingCdn = true;
                TLRPC$TL_upload_reuploadCdnFile tLRPC$TL_upload_reuploadCdnFile = new TLRPC$TL_upload_reuploadCdnFile();
                tLRPC$TL_upload_reuploadCdnFile.file_token = this.cdnToken;
                tLRPC$TL_upload_reuploadCdnFile.request_token = ((TLRPC$TL_upload_cdnFileReuploadNeeded) tLObject2).request_token;
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_upload_reuploadCdnFile, new RequestDelegate(requestInfo) {
                    public final /* synthetic */ FileLoadOperation.RequestInfo f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                        FileLoadOperation.this.lambda$null$12$FileLoadOperation(this.f$1, tLObject, tLRPC$TL_error);
                    }
                }, (QuickAckDelegate) null, (WriteToSocketDelegate) null, 0, this.datacenterId, 1, true);
            }
        }
    }

    public /* synthetic */ void lambda$null$12$FileLoadOperation(RequestInfo requestInfo, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.reuploadingCdn = false;
        if (tLRPC$TL_error == null) {
            TLRPC$Vector tLRPC$Vector = (TLRPC$Vector) tLObject;
            if (!tLRPC$Vector.objects.isEmpty()) {
                if (this.cdnHashes == null) {
                    this.cdnHashes = new SparseArray<>();
                }
                for (int i = 0; i < tLRPC$Vector.objects.size(); i++) {
                    TLRPC$TL_fileHash tLRPC$TL_fileHash = (TLRPC$TL_fileHash) tLRPC$Vector.objects.get(i);
                    this.cdnHashes.put(tLRPC$TL_fileHash.offset, tLRPC$TL_fileHash);
                }
            }
            startDownloadRequest();
        } else if (tLRPC$TL_error.text.equals("FILE_TOKEN_INVALID") || tLRPC$TL_error.text.equals("REQUEST_TOKEN_INVALID")) {
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
