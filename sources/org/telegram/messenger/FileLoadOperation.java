package org.telegram.messenger;

import java.io.File;
import java.io.FileInputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipException;
import org.telegram.messenger.FilePathDatabase;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.NativeByteBuffer;
import org.telegram.tgnet.QuickAckDelegate;
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
    private static final Object lockObject = new Object();
    private static final int preloadMaxBytes = 2097152;
    private static final int stateCanceled = 4;
    private static final int stateDownloading = 1;
    private static final int stateFailed = 2;
    private static final int stateFinished = 3;
    private static final int stateIdle = 0;
    private boolean allowDisordererFileSave;
    private int bigFileSizeFrom;
    private long bytesCountPadding;
    private File cacheFileFinal;
    private File cacheFileGzipTemp;
    private File cacheFileParts;
    private File cacheFilePreload;
    private File cacheFileTemp;
    private File cacheIvTemp;
    private byte[] cdnCheckBytes;
    private int cdnChunkCheckSize;
    private int cdnDatacenterId;
    private HashMap<Long, TLRPC$TL_fileHash> cdnHashes;
    private byte[] cdnIv;
    private byte[] cdnKey;
    private byte[] cdnToken;
    private int currentAccount;
    private int currentDownloadChunkSize;
    private int currentMaxDownloadRequests;
    private int currentQueueType;
    private int currentType;
    private int datacenterId;
    private ArrayList<RequestInfo> delayedRequestInfos;
    private FileLoadOperationDelegate delegate;
    private int downloadChunkSize;
    private int downloadChunkSizeAnimation;
    private int downloadChunkSizeBig;
    private long downloadedBytes;
    private boolean encryptFile;
    private byte[] encryptIv;
    private byte[] encryptKey;
    private String ext;
    private String fileName;
    private RandomAccessFile fileOutputStream;
    private RandomAccessFile filePartsStream;
    private RandomAccessFile fileReadStream;
    private RandomAccessFile fiv;
    private long foundMoovSize;
    private int initialDatacenterId;
    private boolean isCdn;
    private boolean isForceRequest;
    private boolean isPreloadVideoOperation;
    private boolean isStream;
    private byte[] iv;
    private byte[] key;
    protected long lastProgressUpdateTime;
    protected TLRPC$InputFileLocation location;
    private int maxCdnParts;
    private int maxDownloadRequests;
    private int maxDownloadRequestsAnimation;
    private int maxDownloadRequestsBig;
    private int moovFound;
    private long nextAtomOffset;
    private boolean nextPartWasPreloaded;
    private long nextPreloadDownloadOffset;
    private ArrayList<Range> notCheckedCdnRanges;
    private ArrayList<Range> notLoadedBytesRanges;
    private volatile ArrayList<Range> notLoadedBytesRangesCopy;
    private ArrayList<Range> notRequestedBytesRanges;
    public Object parentObject;
    public FilePathDatabase.PathData pathSaveData;
    private volatile boolean paused;
    private boolean preloadFinished;
    private long preloadNotRequestedBytesCount;
    private RandomAccessFile preloadStream;
    private int preloadStreamFileOffset;
    private byte[] preloadTempBuffer;
    private int preloadTempBufferCount;
    private HashMap<Long, PreloadRange> preloadedBytesRanges;
    private int priority;
    private RequestInfo priorityRequestInfo;
    private int renameRetryCount;
    private ArrayList<RequestInfo> requestInfos;
    private long requestedBytesCount;
    private HashMap<Long, Integer> requestedPreloadedBytesRanges;
    private boolean requestingCdnOffsets;
    protected boolean requestingReference;
    private int requestsCount;
    private boolean reuploadingCdn;
    private long startTime;
    private boolean started;
    private volatile int state;
    private String storeFileName;
    private File storePath;
    private ArrayList<FileLoadOperationStream> streamListeners;
    private long streamPriorityStartOffset;
    private long streamStartOffset;
    private boolean supportsPreloading;
    private File tempPath;
    private long totalBytesCount;
    private int totalPreloadedBytes;
    private boolean ungzip;
    private WebFile webFile;
    private TLRPC$InputWebFileLocation webLocation;

    public interface FileLoadOperationDelegate {
        void didChangedLoadProgress(FileLoadOperation fileLoadOperation, long j, long j2);

        void didFailedLoadingFile(FileLoadOperation fileLoadOperation, int i);

        void didFinishLoadingFile(FileLoadOperation fileLoadOperation, File file);

        boolean hasAnotherRefOnFile(String str);

        void saveFilePath(FilePathDatabase.PathData pathData, File file);
    }

    protected static class RequestInfo {
        /* access modifiers changed from: private */
        public long offset;
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
        public long end;
        /* access modifiers changed from: private */
        public long start;

        private Range(long j, long j2) {
            this.start = j;
            this.end = j2;
        }
    }

    private static class PreloadRange {
        /* access modifiers changed from: private */
        public long fileOffset;
        /* access modifiers changed from: private */
        public long length;

        private PreloadRange(long j, long j2) {
            this.fileOffset = j;
            this.length = j2;
        }
    }

    private void updateParams() {
        if (MessagesController.getInstance(this.currentAccount).getfileExperimentalParams) {
            this.downloadChunkSizeBig = 524288;
            this.maxDownloadRequests = 8;
            this.maxDownloadRequestsBig = 8;
        } else {
            this.downloadChunkSizeBig = 131072;
            this.maxDownloadRequests = 4;
            this.maxDownloadRequestsBig = 4;
        }
        this.maxCdnParts = (int) (NUM / ((long) this.downloadChunkSizeBig));
    }

    public FileLoadOperation(ImageLocation imageLocation, Object obj, String str, long j) {
        this.downloadChunkSize = 32768;
        this.downloadChunkSizeBig = 131072;
        this.cdnChunkCheckSize = 131072;
        this.maxDownloadRequests = 4;
        this.maxDownloadRequestsBig = 4;
        this.bigFileSizeFrom = 10485760;
        this.maxCdnParts = (int) (NUM / ((long) 131072));
        this.downloadChunkSizeAnimation = 131072;
        this.maxDownloadRequestsAnimation = 4;
        this.preloadTempBuffer = new byte[24];
        boolean z = false;
        this.state = 0;
        updateParams();
        this.parentObject = obj;
        this.isStream = imageLocation.imageType == 2;
        if (imageLocation.isEncrypted()) {
            TLRPC$TL_inputEncryptedFileLocation tLRPC$TL_inputEncryptedFileLocation = new TLRPC$TL_inputEncryptedFileLocation();
            this.location = tLRPC$TL_inputEncryptedFileLocation;
            TLRPC$TL_fileLocationToBeDeprecated tLRPC$TL_fileLocationToBeDeprecated = imageLocation.location;
            long j2 = tLRPC$TL_fileLocationToBeDeprecated.volume_id;
            tLRPC$TL_inputEncryptedFileLocation.id = j2;
            tLRPC$TL_inputEncryptedFileLocation.volume_id = j2;
            tLRPC$TL_inputEncryptedFileLocation.local_id = tLRPC$TL_fileLocationToBeDeprecated.local_id;
            tLRPC$TL_inputEncryptedFileLocation.access_hash = imageLocation.access_hash;
            byte[] bArr = new byte[32];
            this.iv = bArr;
            System.arraycopy(imageLocation.iv, 0, bArr, 0, bArr.length);
            this.key = imageLocation.key;
        } else if (imageLocation.photoPeer != null) {
            TLRPC$TL_inputPeerPhotoFileLocation tLRPC$TL_inputPeerPhotoFileLocation = new TLRPC$TL_inputPeerPhotoFileLocation();
            TLRPC$TL_fileLocationToBeDeprecated tLRPC$TL_fileLocationToBeDeprecated2 = imageLocation.location;
            long j3 = tLRPC$TL_fileLocationToBeDeprecated2.volume_id;
            tLRPC$TL_inputPeerPhotoFileLocation.id = j3;
            tLRPC$TL_inputPeerPhotoFileLocation.volume_id = j3;
            tLRPC$TL_inputPeerPhotoFileLocation.local_id = tLRPC$TL_fileLocationToBeDeprecated2.local_id;
            tLRPC$TL_inputPeerPhotoFileLocation.photo_id = imageLocation.photoId;
            tLRPC$TL_inputPeerPhotoFileLocation.big = imageLocation.photoPeerType == 0;
            tLRPC$TL_inputPeerPhotoFileLocation.peer = imageLocation.photoPeer;
            this.location = tLRPC$TL_inputPeerPhotoFileLocation;
        } else if (imageLocation.stickerSet != null) {
            TLRPC$TL_inputStickerSetThumb tLRPC$TL_inputStickerSetThumb = new TLRPC$TL_inputStickerSetThumb();
            TLRPC$TL_fileLocationToBeDeprecated tLRPC$TL_fileLocationToBeDeprecated3 = imageLocation.location;
            long j4 = tLRPC$TL_fileLocationToBeDeprecated3.volume_id;
            tLRPC$TL_inputStickerSetThumb.id = j4;
            tLRPC$TL_inputStickerSetThumb.volume_id = j4;
            tLRPC$TL_inputStickerSetThumb.local_id = tLRPC$TL_fileLocationToBeDeprecated3.local_id;
            tLRPC$TL_inputStickerSetThumb.thumb_version = imageLocation.thumbVersion;
            tLRPC$TL_inputStickerSetThumb.stickerset = imageLocation.stickerSet;
            this.location = tLRPC$TL_inputStickerSetThumb;
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
        int i = imageLocation.imageType;
        this.ungzip = (i == 1 || i == 3) ? true : z;
        int i2 = imageLocation.dc_id;
        this.datacenterId = i2;
        this.initialDatacenterId = i2;
        this.currentType = 16777216;
        this.totalBytesCount = j;
        this.ext = str == null ? "jpg" : str;
    }

    public FileLoadOperation(SecureDocument secureDocument) {
        this.downloadChunkSize = 32768;
        this.downloadChunkSizeBig = 131072;
        this.cdnChunkCheckSize = 131072;
        this.maxDownloadRequests = 4;
        this.maxDownloadRequestsBig = 4;
        this.bigFileSizeFrom = 10485760;
        this.maxCdnParts = (int) (NUM / ((long) 131072));
        this.downloadChunkSizeAnimation = 131072;
        this.maxDownloadRequestsAnimation = 4;
        this.preloadTempBuffer = new byte[24];
        this.state = 0;
        updateParams();
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
        this.downloadChunkSize = 32768;
        this.downloadChunkSizeBig = 131072;
        this.cdnChunkCheckSize = 131072;
        this.maxDownloadRequests = 4;
        this.maxDownloadRequestsBig = 4;
        this.bigFileSizeFrom = 10485760;
        this.maxCdnParts = (int) (NUM / ((long) 131072));
        this.downloadChunkSizeAnimation = 131072;
        this.maxDownloadRequestsAnimation = 4;
        this.preloadTempBuffer = new byte[24];
        this.state = 0;
        updateParams();
        this.currentAccount = i;
        this.webFile = webFile2;
        this.webLocation = webFile2.location;
        this.totalBytesCount = (long) webFile2.size;
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

    /* JADX WARNING: Removed duplicated region for block: B:32:0x00da A[Catch:{ Exception -> 0x0120 }] */
    /* JADX WARNING: Removed duplicated region for block: B:39:0x00f9 A[Catch:{ Exception -> 0x0120 }] */
    /* JADX WARNING: Removed duplicated region for block: B:40:0x00fe A[Catch:{ Exception -> 0x0120 }] */
    /* JADX WARNING: Removed duplicated region for block: B:46:0x0117 A[Catch:{ Exception -> 0x0120 }] */
    /* JADX WARNING: Removed duplicated region for block: B:52:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public FileLoadOperation(org.telegram.tgnet.TLRPC$Document r12, java.lang.Object r13) {
        /*
            r11 = this;
            r11.<init>()
            r0 = 32768(0x8000, float:4.5918E-41)
            r11.downloadChunkSize = r0
            r0 = 131072(0x20000, float:1.83671E-40)
            r11.downloadChunkSizeBig = r0
            r11.cdnChunkCheckSize = r0
            r1 = 4
            r11.maxDownloadRequests = r1
            r11.maxDownloadRequestsBig = r1
            r2 = 10485760(0xa00000, float:1.469368E-38)
            r11.bigFileSizeFrom = r2
            long r2 = (long) r0
            r4 = 2097152000(0x7d000000, double:1.0361307573E-314)
            long r4 = r4 / r2
            int r2 = (int) r4
            r11.maxCdnParts = r2
            r11.downloadChunkSizeAnimation = r0
            r11.maxDownloadRequestsAnimation = r1
            r0 = 24
            byte[] r0 = new byte[r0]
            r11.preloadTempBuffer = r0
            r0 = 0
            r11.state = r0
            r11.updateParams()
            r1 = 1
            r11.parentObject = r13     // Catch:{ Exception -> 0x0120 }
            boolean r13 = r12 instanceof org.telegram.tgnet.TLRPC$TL_documentEncrypted     // Catch:{ Exception -> 0x0120 }
            java.lang.String r2 = ""
            if (r13 == 0) goto L_0x005e
            org.telegram.tgnet.TLRPC$TL_inputEncryptedFileLocation r13 = new org.telegram.tgnet.TLRPC$TL_inputEncryptedFileLocation     // Catch:{ Exception -> 0x0120 }
            r13.<init>()     // Catch:{ Exception -> 0x0120 }
            r11.location = r13     // Catch:{ Exception -> 0x0120 }
            long r3 = r12.id     // Catch:{ Exception -> 0x0120 }
            r13.id = r3     // Catch:{ Exception -> 0x0120 }
            long r3 = r12.access_hash     // Catch:{ Exception -> 0x0120 }
            r13.access_hash = r3     // Catch:{ Exception -> 0x0120 }
            int r13 = r12.dc_id     // Catch:{ Exception -> 0x0120 }
            r11.datacenterId = r13     // Catch:{ Exception -> 0x0120 }
            r11.initialDatacenterId = r13     // Catch:{ Exception -> 0x0120 }
            r13 = 32
            byte[] r13 = new byte[r13]     // Catch:{ Exception -> 0x0120 }
            r11.iv = r13     // Catch:{ Exception -> 0x0120 }
            byte[] r3 = r12.iv     // Catch:{ Exception -> 0x0120 }
            int r4 = r13.length     // Catch:{ Exception -> 0x0120 }
            java.lang.System.arraycopy(r3, r0, r13, r0, r4)     // Catch:{ Exception -> 0x0120 }
            byte[] r13 = r12.key     // Catch:{ Exception -> 0x0120 }
            r11.key = r13     // Catch:{ Exception -> 0x0120 }
            goto L_0x009e
        L_0x005e:
            boolean r13 = r12 instanceof org.telegram.tgnet.TLRPC$TL_document     // Catch:{ Exception -> 0x0120 }
            if (r13 == 0) goto L_0x009e
            org.telegram.tgnet.TLRPC$TL_inputDocumentFileLocation r13 = new org.telegram.tgnet.TLRPC$TL_inputDocumentFileLocation     // Catch:{ Exception -> 0x0120 }
            r13.<init>()     // Catch:{ Exception -> 0x0120 }
            r11.location = r13     // Catch:{ Exception -> 0x0120 }
            long r3 = r12.id     // Catch:{ Exception -> 0x0120 }
            r13.id = r3     // Catch:{ Exception -> 0x0120 }
            long r3 = r12.access_hash     // Catch:{ Exception -> 0x0120 }
            r13.access_hash = r3     // Catch:{ Exception -> 0x0120 }
            byte[] r3 = r12.file_reference     // Catch:{ Exception -> 0x0120 }
            r13.file_reference = r3     // Catch:{ Exception -> 0x0120 }
            r13.thumb_size = r2     // Catch:{ Exception -> 0x0120 }
            if (r3 != 0) goto L_0x007d
            byte[] r3 = new byte[r0]     // Catch:{ Exception -> 0x0120 }
            r13.file_reference = r3     // Catch:{ Exception -> 0x0120 }
        L_0x007d:
            int r13 = r12.dc_id     // Catch:{ Exception -> 0x0120 }
            r11.datacenterId = r13     // Catch:{ Exception -> 0x0120 }
            r11.initialDatacenterId = r13     // Catch:{ Exception -> 0x0120 }
            r11.allowDisordererFileSave = r1     // Catch:{ Exception -> 0x0120 }
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r13 = r12.attributes     // Catch:{ Exception -> 0x0120 }
            int r13 = r13.size()     // Catch:{ Exception -> 0x0120 }
            r3 = 0
        L_0x008c:
            if (r3 >= r13) goto L_0x009e
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r4 = r12.attributes     // Catch:{ Exception -> 0x0120 }
            java.lang.Object r4 = r4.get(r3)     // Catch:{ Exception -> 0x0120 }
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC$TL_documentAttributeVideo     // Catch:{ Exception -> 0x0120 }
            if (r4 == 0) goto L_0x009b
            r11.supportsPreloading = r1     // Catch:{ Exception -> 0x0120 }
            goto L_0x009e
        L_0x009b:
            int r3 = r3 + 1
            goto L_0x008c
        L_0x009e:
            java.lang.String r13 = "application/x-tgsticker"
            java.lang.String r3 = r12.mime_type     // Catch:{ Exception -> 0x0120 }
            boolean r13 = r13.equals(r3)     // Catch:{ Exception -> 0x0120 }
            if (r13 != 0) goto L_0x00b5
            java.lang.String r13 = "application/x-tgwallpattern"
            java.lang.String r3 = r12.mime_type     // Catch:{ Exception -> 0x0120 }
            boolean r13 = r13.equals(r3)     // Catch:{ Exception -> 0x0120 }
            if (r13 == 0) goto L_0x00b3
            goto L_0x00b5
        L_0x00b3:
            r13 = 0
            goto L_0x00b6
        L_0x00b5:
            r13 = 1
        L_0x00b6:
            r11.ungzip = r13     // Catch:{ Exception -> 0x0120 }
            long r3 = r12.size     // Catch:{ Exception -> 0x0120 }
            r11.totalBytesCount = r3     // Catch:{ Exception -> 0x0120 }
            byte[] r13 = r11.key     // Catch:{ Exception -> 0x0120 }
            if (r13 == 0) goto L_0x00d2
            r5 = 16
            long r7 = r3 % r5
            r9 = 0
            int r13 = (r7 > r9 ? 1 : (r7 == r9 ? 0 : -1))
            if (r13 == 0) goto L_0x00d2
            long r7 = r3 % r5
            long r5 = r5 - r7
            r11.bytesCountPadding = r5     // Catch:{ Exception -> 0x0120 }
            long r3 = r3 + r5
            r11.totalBytesCount = r3     // Catch:{ Exception -> 0x0120 }
        L_0x00d2:
            java.lang.String r13 = org.telegram.messenger.FileLoader.getDocumentFileName(r12)     // Catch:{ Exception -> 0x0120 }
            r11.ext = r13     // Catch:{ Exception -> 0x0120 }
            if (r13 == 0) goto L_0x00ed
            r3 = 46
            int r13 = r13.lastIndexOf(r3)     // Catch:{ Exception -> 0x0120 }
            r3 = -1
            if (r13 != r3) goto L_0x00e4
            goto L_0x00ed
        L_0x00e4:
            java.lang.String r2 = r11.ext     // Catch:{ Exception -> 0x0120 }
            java.lang.String r13 = r2.substring(r13)     // Catch:{ Exception -> 0x0120 }
            r11.ext = r13     // Catch:{ Exception -> 0x0120 }
            goto L_0x00ef
        L_0x00ed:
            r11.ext = r2     // Catch:{ Exception -> 0x0120 }
        L_0x00ef:
            java.lang.String r13 = "audio/ogg"
            java.lang.String r2 = r12.mime_type     // Catch:{ Exception -> 0x0120 }
            boolean r13 = r13.equals(r2)     // Catch:{ Exception -> 0x0120 }
            if (r13 == 0) goto L_0x00fe
            r13 = 50331648(0x3000000, float:3.761582E-37)
            r11.currentType = r13     // Catch:{ Exception -> 0x0120 }
            goto L_0x010f
        L_0x00fe:
            java.lang.String r13 = r12.mime_type     // Catch:{ Exception -> 0x0120 }
            boolean r13 = org.telegram.messenger.FileLoader.isVideoMimeType(r13)     // Catch:{ Exception -> 0x0120 }
            if (r13 == 0) goto L_0x010b
            r13 = 33554432(0x2000000, float:9.403955E-38)
            r11.currentType = r13     // Catch:{ Exception -> 0x0120 }
            goto L_0x010f
        L_0x010b:
            r13 = 67108864(0x4000000, float:1.5046328E-36)
            r11.currentType = r13     // Catch:{ Exception -> 0x0120 }
        L_0x010f:
            java.lang.String r13 = r11.ext     // Catch:{ Exception -> 0x0120 }
            int r13 = r13.length()     // Catch:{ Exception -> 0x0120 }
            if (r13 > r1) goto L_0x0127
            java.lang.String r12 = r12.mime_type     // Catch:{ Exception -> 0x0120 }
            java.lang.String r12 = org.telegram.messenger.FileLoader.getExtensionByMimeType(r12)     // Catch:{ Exception -> 0x0120 }
            r11.ext = r12     // Catch:{ Exception -> 0x0120 }
            goto L_0x0127
        L_0x0120:
            r12 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r12)
            r11.onFail(r1, r0)
        L_0x0127:
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

    public void setPaths(int i, String str, int i2, File file, File file2, String str2) {
        this.storePath = file;
        this.tempPath = file2;
        this.currentAccount = i;
        this.fileName = str;
        this.storeFileName = str2;
        this.currentQueueType = i2;
    }

    public int getQueueType() {
        return this.currentQueueType;
    }

    public boolean wasStarted() {
        return this.started && !this.paused;
    }

    public int getCurrentType() {
        return this.currentType;
    }

    private void removePart(ArrayList<Range> arrayList, long j, long j2) {
        boolean z;
        ArrayList<Range> arrayList2 = arrayList;
        long j3 = j;
        long j4 = j2;
        if (arrayList2 != null && j4 >= j3) {
            int size = arrayList.size();
            int i = 0;
            int i2 = 0;
            while (i2 < size) {
                Range range = arrayList2.get(i2);
                if (j3 == range.end) {
                    long unused = range.end = j4;
                } else if (j4 == range.start) {
                    long unused2 = range.start = j3;
                } else {
                    i2++;
                }
                z = true;
            }
            z = false;
            Collections.sort(arrayList2, FileLoadOperation$$ExternalSyntheticLambda11.INSTANCE);
            while (i < arrayList.size() - 1) {
                Range range2 = arrayList2.get(i);
                int i3 = i + 1;
                Range range3 = arrayList2.get(i3);
                if (range2.end == range3.start) {
                    long unused3 = range2.end = range3.end;
                    arrayList2.remove(i3);
                    i--;
                }
                i++;
            }
            if (!z) {
                arrayList2.add(new Range(j, j2));
            }
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ int lambda$removePart$0(Range range, Range range2) {
        if (range.start > range2.start) {
            return 1;
        }
        return range.start < range2.start ? -1 : 0;
    }

    private void addPart(ArrayList<Range> arrayList, long j, long j2, boolean z) {
        boolean z2;
        ArrayList<Range> arrayList2 = arrayList;
        long j3 = j;
        long j4 = j2;
        if (arrayList2 != null && j4 >= j3) {
            int size = arrayList.size();
            int i = 0;
            while (true) {
                z2 = true;
                if (i >= size) {
                    z2 = false;
                    break;
                }
                Range range = arrayList2.get(i);
                if (j3 <= range.start) {
                    if (j4 >= range.end) {
                        arrayList2.remove(i);
                        break;
                    } else if (j4 > range.start) {
                        long unused = range.start = j4;
                        break;
                    }
                } else if (j4 < range.end) {
                    arrayList2.add(0, new Range(range.start, j));
                    long unused2 = range.start = j4;
                    break;
                } else if (j3 < range.end) {
                    long unused3 = range.end = j3;
                    break;
                }
                i++;
            }
            if (!z) {
                return;
            }
            if (z2) {
                try {
                    this.filePartsStream.seek(0);
                    int size2 = arrayList.size();
                    this.filePartsStream.writeInt(size2);
                    for (int i2 = 0; i2 < size2; i2++) {
                        Range range2 = arrayList2.get(i2);
                        this.filePartsStream.writeLong(range2.start);
                        this.filePartsStream.writeLong(range2.end);
                    }
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
                notifyStreamListeners();
            } else if (BuildVars.LOGS_ENABLED) {
                FileLog.e(this.cacheFileFinal + " downloaded duplicate file part " + j3 + " - " + j4);
            }
        }
    }

    private void notifyStreamListeners() {
        ArrayList<FileLoadOperationStream> arrayList = this.streamListeners;
        if (arrayList != null) {
            int size = arrayList.size();
            for (int i = 0; i < size; i++) {
                this.streamListeners.get(i).newDataAvailable();
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
        Utilities.stageQueue.postRunnable(new FileLoadOperation$$ExternalSyntheticLambda9(this, fileArr, countDownLatch));
        try {
            countDownLatch.await();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        return fileArr[0];
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$getCurrentFile$1(File[] fileArr, CountDownLatch countDownLatch) {
        if (this.state == 3) {
            fileArr[0] = this.cacheFileFinal;
        } else {
            fileArr[0] = this.cacheFileTemp;
        }
        countDownLatch.countDown();
    }

    private long getDownloadedLengthFromOffsetInternal(ArrayList<Range> arrayList, long j, long j2) {
        long j3;
        ArrayList<Range> arrayList2 = arrayList;
        long j4 = j2;
        if (arrayList2 != null && this.state != 3 && !arrayList.isEmpty()) {
            int size = arrayList.size();
            Range range = null;
            int i = 0;
            while (true) {
                if (i >= size) {
                    j3 = j4;
                    break;
                }
                Range range2 = arrayList2.get(i);
                if (j <= range2.start && (range == null || range2.start < range.start)) {
                    range = range2;
                }
                if (range2.start <= j && range2.end > j) {
                    j3 = 0;
                    break;
                }
                i++;
            }
            if (j3 == 0) {
                return 0;
            }
            if (range != null) {
                return Math.min(j4, range.start - j);
            }
            return Math.min(j4, Math.max(this.totalBytesCount - j, 0));
        } else if (this.state == 3) {
            return j4;
        } else {
            long j5 = this.downloadedBytes;
            if (j5 == 0) {
                return 0;
            }
            return Math.min(j4, Math.max(j5 - j, 0));
        }
    }

    /* access modifiers changed from: protected */
    public float getDownloadedLengthFromOffset(float f) {
        ArrayList<Range> arrayList = this.notLoadedBytesRangesCopy;
        long j = this.totalBytesCount;
        if (j == 0 || arrayList == null) {
            return 0.0f;
        }
        return f + (((float) getDownloadedLengthFromOffsetInternal(arrayList, (long) ((int) (((float) j) * f)), j)) / ((float) this.totalBytesCount));
    }

    /* access modifiers changed from: protected */
    public long[] getDownloadedLengthFromOffset(int i, long j) {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        long[] jArr = new long[2];
        Utilities.stageQueue.postRunnable(new FileLoadOperation$$ExternalSyntheticLambda8(this, jArr, i, j, countDownLatch));
        try {
            countDownLatch.await();
        } catch (Exception unused) {
        }
        return jArr;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$getDownloadedLengthFromOffset$2(long[] jArr, int i, long j, CountDownLatch countDownLatch) {
        jArr[0] = getDownloadedLengthFromOffsetInternal(this.notLoadedBytesRanges, (long) i, j);
        if (this.state == 3) {
            jArr[1] = 1;
        }
        countDownLatch.countDown();
    }

    public String getFileName() {
        return this.fileName;
    }

    /* access modifiers changed from: protected */
    public void removeStreamListener(FileLoadOperationStream fileLoadOperationStream) {
        Utilities.stageQueue.postRunnable(new FileLoadOperation$$ExternalSyntheticLambda3(this, fileLoadOperationStream));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$removeStreamListener$3(FileLoadOperationStream fileLoadOperationStream) {
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

    /* JADX WARNING: type inference failed for: r6v20 */
    /* JADX WARNING: type inference failed for: r6v22 */
    /* JADX WARNING: type inference failed for: r6v24 */
    /* JADX WARNING: Code restructure failed: missing block: B:114:0x03f1, code lost:
        if (r6 != r8.cacheFileFinal.length()) goto L_0x03f3;
     */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:102:0x0394  */
    /* JADX WARNING: Removed duplicated region for block: B:103:0x03ba  */
    /* JADX WARNING: Removed duplicated region for block: B:109:0x03db  */
    /* JADX WARNING: Removed duplicated region for block: B:120:0x0409  */
    /* JADX WARNING: Removed duplicated region for block: B:148:0x04b8 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:215:0x05f0 A[SYNTHETIC, Splitter:B:215:0x05f0] */
    /* JADX WARNING: Removed duplicated region for block: B:225:0x060f  */
    /* JADX WARNING: Removed duplicated region for block: B:242:0x0681  */
    /* JADX WARNING: Removed duplicated region for block: B:245:0x068b  */
    /* JADX WARNING: Removed duplicated region for block: B:258:0x06e4  */
    /* JADX WARNING: Removed duplicated region for block: B:265:0x070f  */
    /* JADX WARNING: Removed duplicated region for block: B:271:0x073b  */
    /* JADX WARNING: Removed duplicated region for block: B:276:0x0778  */
    /* JADX WARNING: Removed duplicated region for block: B:301:0x07eb A[Catch:{ Exception -> 0x07f0 }] */
    /* JADX WARNING: Removed duplicated region for block: B:307:0x07f9  */
    /* JADX WARNING: Removed duplicated region for block: B:309:0x07fe  */
    /* JADX WARNING: Removed duplicated region for block: B:310:0x080c  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean start(org.telegram.messenger.FileLoadOperationStream r31, long r32, boolean r34) {
        /*
            r30 = this;
            r8 = r30
            long r0 = java.lang.System.currentTimeMillis()
            r8.startTime = r0
            r30.updateParams()
            int r0 = r8.currentDownloadChunkSize
            if (r0 != 0) goto L_0x003d
            boolean r0 = r8.isStream
            if (r0 == 0) goto L_0x001b
            int r1 = r8.downloadChunkSizeAnimation
            r8.currentDownloadChunkSize = r1
            int r1 = r8.maxDownloadRequestsAnimation
            r8.currentMaxDownloadRequests = r1
        L_0x001b:
            long r1 = r8.totalBytesCount
            int r3 = r8.bigFileSizeFrom
            long r4 = (long) r3
            int r6 = (r1 > r4 ? 1 : (r1 == r4 ? 0 : -1))
            if (r6 >= 0) goto L_0x002a
            if (r0 == 0) goto L_0x0027
            goto L_0x002a
        L_0x0027:
            int r4 = r8.downloadChunkSize
            goto L_0x002c
        L_0x002a:
            int r4 = r8.downloadChunkSizeBig
        L_0x002c:
            r8.currentDownloadChunkSize = r4
            long r3 = (long) r3
            int r5 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r5 >= 0) goto L_0x0039
            if (r0 == 0) goto L_0x0036
            goto L_0x0039
        L_0x0036:
            int r0 = r8.maxDownloadRequests
            goto L_0x003b
        L_0x0039:
            int r0 = r8.maxDownloadRequestsBig
        L_0x003b:
            r8.currentMaxDownloadRequests = r0
        L_0x003d:
            int r0 = r8.state
            r9 = 1
            r10 = 0
            if (r0 == 0) goto L_0x0045
            r0 = 1
            goto L_0x0046
        L_0x0045:
            r0 = 0
        L_0x0046:
            boolean r11 = r8.paused
            r8.paused = r10
            if (r31 == 0) goto L_0x0061
            org.telegram.messenger.DispatchQueue r12 = org.telegram.messenger.Utilities.stageQueue
            org.telegram.messenger.FileLoadOperation$$ExternalSyntheticLambda7 r13 = new org.telegram.messenger.FileLoadOperation$$ExternalSyntheticLambda7
            r1 = r13
            r2 = r30
            r3 = r34
            r4 = r32
            r6 = r31
            r7 = r0
            r1.<init>(r2, r3, r4, r6, r7)
            r12.postRunnable(r13)
            goto L_0x006f
        L_0x0061:
            if (r11 == 0) goto L_0x006f
            if (r0 == 0) goto L_0x006f
            org.telegram.messenger.DispatchQueue r1 = org.telegram.messenger.Utilities.stageQueue
            org.telegram.messenger.FileLoadOperation$$ExternalSyntheticLambda0 r2 = new org.telegram.messenger.FileLoadOperation$$ExternalSyntheticLambda0
            r2.<init>(r8)
            r1.postRunnable(r2)
        L_0x006f:
            if (r0 == 0) goto L_0x0072
            return r11
        L_0x0072:
            org.telegram.tgnet.TLRPC$InputFileLocation r0 = r8.location
            if (r0 != 0) goto L_0x007e
            org.telegram.tgnet.TLRPC$InputWebFileLocation r0 = r8.webLocation
            if (r0 != 0) goto L_0x007e
            r8.onFail(r9, r10)
            return r10
        L_0x007e:
            int r0 = r8.currentDownloadChunkSize
            long r1 = (long) r0
            long r1 = r32 / r1
            long r3 = (long) r0
            long r1 = r1 * r3
            r8.streamStartOffset = r1
            boolean r1 = r8.allowDisordererFileSave
            r2 = 0
            if (r1 == 0) goto L_0x00a7
            long r4 = r8.totalBytesCount
            int r1 = (r4 > r2 ? 1 : (r4 == r2 ? 0 : -1))
            if (r1 <= 0) goto L_0x00a7
            long r0 = (long) r0
            int r6 = (r4 > r0 ? 1 : (r4 == r0 ? 0 : -1))
            if (r6 <= 0) goto L_0x00a7
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r8.notLoadedBytesRanges = r0
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r8.notRequestedBytesRanges = r0
        L_0x00a7:
            org.telegram.tgnet.TLRPC$InputWebFileLocation r0 = r8.webLocation
            java.lang.String r1 = "_64.iv.enc"
            java.lang.String r4 = "_64.iv"
            java.lang.String r5 = ".enc"
            java.lang.String r6 = ".temp.enc"
            java.lang.String r7 = ".temp"
            java.lang.String r11 = "."
            if (r0 == 0) goto L_0x013c
            org.telegram.messenger.WebFile r0 = r8.webFile
            java.lang.String r0 = r0.url
            java.lang.String r0 = org.telegram.messenger.Utilities.MD5(r0)
            boolean r13 = r8.encryptFile
            if (r13 == 0) goto L_0x00fd
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r0)
            r4.append(r6)
            java.lang.String r4 = r4.toString()
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            r6.append(r0)
            r6.append(r11)
            java.lang.String r7 = r8.ext
            r6.append(r7)
            r6.append(r5)
            java.lang.String r5 = r6.toString()
            byte[] r6 = r8.key
            if (r6 == 0) goto L_0x0136
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            r6.append(r0)
            r6.append(r1)
            java.lang.String r0 = r6.toString()
            goto L_0x0137
        L_0x00fd:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r1.append(r0)
            r1.append(r7)
            java.lang.String r1 = r1.toString()
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            r5.append(r0)
            r5.append(r11)
            java.lang.String r6 = r8.ext
            r5.append(r6)
            java.lang.String r5 = r5.toString()
            byte[] r6 = r8.key
            if (r6 == 0) goto L_0x0135
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            r6.append(r0)
            r6.append(r4)
            java.lang.String r0 = r6.toString()
            r4 = r1
            goto L_0x0137
        L_0x0135:
            r4 = r1
        L_0x0136:
            r0 = 0
        L_0x0137:
            r1 = r0
        L_0x0138:
            r2 = 0
            r3 = 0
            goto L_0x0378
        L_0x013c:
            org.telegram.tgnet.TLRPC$InputFileLocation r0 = r8.location
            long r13 = r0.volume_id
            java.lang.String r15 = "_64.pt"
            java.lang.String r12 = "_64.preload"
            java.lang.String r9 = "_"
            int r16 = (r13 > r2 ? 1 : (r13 == r2 ? 0 : -1))
            if (r16 == 0) goto L_0x0271
            int r2 = r0.local_id
            if (r2 == 0) goto L_0x0271
            int r0 = r8.datacenterId
            r2 = -2147483648(0xfffffffvar_, float:-0.0)
            if (r0 == r2) goto L_0x026c
            r2 = -2147483648(0xfffffffvar_, double:NaN)
            int r16 = (r13 > r2 ? 1 : (r13 == r2 ? 0 : -1))
            if (r16 == 0) goto L_0x026c
            if (r0 != 0) goto L_0x015f
            goto L_0x026c
        L_0x015f:
            boolean r0 = r8.encryptFile
            if (r0 == 0) goto L_0x01c8
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            org.telegram.tgnet.TLRPC$InputFileLocation r2 = r8.location
            long r2 = r2.volume_id
            r0.append(r2)
            r0.append(r9)
            org.telegram.tgnet.TLRPC$InputFileLocation r2 = r8.location
            int r2 = r2.local_id
            r0.append(r2)
            r0.append(r6)
            java.lang.String r4 = r0.toString()
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            org.telegram.tgnet.TLRPC$InputFileLocation r2 = r8.location
            long r2 = r2.volume_id
            r0.append(r2)
            r0.append(r9)
            org.telegram.tgnet.TLRPC$InputFileLocation r2 = r8.location
            int r2 = r2.local_id
            r0.append(r2)
            r0.append(r11)
            java.lang.String r2 = r8.ext
            r0.append(r2)
            r0.append(r5)
            java.lang.String r5 = r0.toString()
            byte[] r0 = r8.key
            if (r0 == 0) goto L_0x02df
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            org.telegram.tgnet.TLRPC$InputFileLocation r2 = r8.location
            long r2 = r2.volume_id
            r0.append(r2)
            r0.append(r9)
            org.telegram.tgnet.TLRPC$InputFileLocation r2 = r8.location
            int r2 = r2.local_id
            r0.append(r2)
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            goto L_0x0137
        L_0x01c8:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            org.telegram.tgnet.TLRPC$InputFileLocation r1 = r8.location
            long r1 = r1.volume_id
            r0.append(r1)
            r0.append(r9)
            org.telegram.tgnet.TLRPC$InputFileLocation r1 = r8.location
            int r1 = r1.local_id
            r0.append(r1)
            r0.append(r7)
            java.lang.String r0 = r0.toString()
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            org.telegram.tgnet.TLRPC$InputFileLocation r2 = r8.location
            long r2 = r2.volume_id
            r1.append(r2)
            r1.append(r9)
            org.telegram.tgnet.TLRPC$InputFileLocation r2 = r8.location
            int r2 = r2.local_id
            r1.append(r2)
            r1.append(r11)
            java.lang.String r2 = r8.ext
            r1.append(r2)
            java.lang.String r5 = r1.toString()
            byte[] r1 = r8.key
            if (r1 == 0) goto L_0x0229
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            org.telegram.tgnet.TLRPC$InputFileLocation r2 = r8.location
            long r2 = r2.volume_id
            r1.append(r2)
            r1.append(r9)
            org.telegram.tgnet.TLRPC$InputFileLocation r2 = r8.location
            int r2 = r2.local_id
            r1.append(r2)
            r1.append(r4)
            java.lang.String r1 = r1.toString()
            goto L_0x022a
        L_0x0229:
            r1 = 0
        L_0x022a:
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$Range> r2 = r8.notLoadedBytesRanges
            if (r2 == 0) goto L_0x024c
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            org.telegram.tgnet.TLRPC$InputFileLocation r3 = r8.location
            long r3 = r3.volume_id
            r2.append(r3)
            r2.append(r9)
            org.telegram.tgnet.TLRPC$InputFileLocation r3 = r8.location
            int r3 = r3.local_id
            r2.append(r3)
            r2.append(r15)
            java.lang.String r2 = r2.toString()
            goto L_0x024d
        L_0x024c:
            r2 = 0
        L_0x024d:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            org.telegram.tgnet.TLRPC$InputFileLocation r4 = r8.location
            long r6 = r4.volume_id
            r3.append(r6)
            r3.append(r9)
            org.telegram.tgnet.TLRPC$InputFileLocation r4 = r8.location
            int r4 = r4.local_id
            r3.append(r4)
            r3.append(r12)
            java.lang.String r3 = r3.toString()
            goto L_0x0377
        L_0x026c:
            r1 = 1
            r8.onFail(r1, r10)
            return r10
        L_0x0271:
            int r2 = r8.datacenterId
            if (r2 == 0) goto L_0x0825
            long r2 = r0.id
            r13 = 0
            int r0 = (r2 > r13 ? 1 : (r2 == r13 ? 0 : -1))
            if (r0 != 0) goto L_0x027f
            goto L_0x0825
        L_0x027f:
            boolean r0 = r8.encryptFile
            if (r0 == 0) goto L_0x02e2
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            int r2 = r8.datacenterId
            r0.append(r2)
            r0.append(r9)
            org.telegram.tgnet.TLRPC$InputFileLocation r2 = r8.location
            long r2 = r2.id
            r0.append(r2)
            r0.append(r6)
            java.lang.String r4 = r0.toString()
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            int r2 = r8.datacenterId
            r0.append(r2)
            r0.append(r9)
            org.telegram.tgnet.TLRPC$InputFileLocation r2 = r8.location
            long r2 = r2.id
            r0.append(r2)
            java.lang.String r2 = r8.ext
            r0.append(r2)
            r0.append(r5)
            java.lang.String r5 = r0.toString()
            byte[] r0 = r8.key
            if (r0 == 0) goto L_0x02df
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            int r2 = r8.datacenterId
            r0.append(r2)
            r0.append(r9)
            org.telegram.tgnet.TLRPC$InputFileLocation r2 = r8.location
            long r2 = r2.id
            r0.append(r2)
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            goto L_0x0137
        L_0x02df:
            r1 = 0
            goto L_0x0138
        L_0x02e2:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            int r1 = r8.datacenterId
            r0.append(r1)
            r0.append(r9)
            org.telegram.tgnet.TLRPC$InputFileLocation r1 = r8.location
            long r1 = r1.id
            r0.append(r1)
            r0.append(r7)
            java.lang.String r0 = r0.toString()
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            int r2 = r8.datacenterId
            r1.append(r2)
            r1.append(r9)
            org.telegram.tgnet.TLRPC$InputFileLocation r2 = r8.location
            long r2 = r2.id
            r1.append(r2)
            java.lang.String r2 = r8.ext
            r1.append(r2)
            java.lang.String r5 = r1.toString()
            byte[] r1 = r8.key
            if (r1 == 0) goto L_0x033a
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            int r2 = r8.datacenterId
            r1.append(r2)
            r1.append(r9)
            org.telegram.tgnet.TLRPC$InputFileLocation r2 = r8.location
            long r2 = r2.id
            r1.append(r2)
            r1.append(r4)
            java.lang.String r1 = r1.toString()
            goto L_0x033b
        L_0x033a:
            r1 = 0
        L_0x033b:
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$Range> r2 = r8.notLoadedBytesRanges
            if (r2 == 0) goto L_0x035b
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            int r3 = r8.datacenterId
            r2.append(r3)
            r2.append(r9)
            org.telegram.tgnet.TLRPC$InputFileLocation r3 = r8.location
            long r3 = r3.id
            r2.append(r3)
            r2.append(r15)
            java.lang.String r2 = r2.toString()
            goto L_0x035c
        L_0x035b:
            r2 = 0
        L_0x035c:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            int r4 = r8.datacenterId
            r3.append(r4)
            r3.append(r9)
            org.telegram.tgnet.TLRPC$InputFileLocation r4 = r8.location
            long r6 = r4.id
            r3.append(r6)
            r3.append(r12)
            java.lang.String r3 = r3.toString()
        L_0x0377:
            r4 = r0
        L_0x0378:
            java.util.ArrayList r0 = new java.util.ArrayList
            int r6 = r8.currentMaxDownloadRequests
            r0.<init>(r6)
            r8.requestInfos = r0
            java.util.ArrayList r0 = new java.util.ArrayList
            int r6 = r8.currentMaxDownloadRequests
            r7 = 1
            int r6 = r6 - r7
            r0.<init>(r6)
            r8.delayedRequestInfos = r0
            r8.state = r7
            java.lang.Object r0 = r8.parentObject
            boolean r6 = r0 instanceof org.telegram.tgnet.TLRPC$TL_theme
            if (r6 == 0) goto L_0x03ba
            org.telegram.tgnet.TLRPC$TL_theme r0 = (org.telegram.tgnet.TLRPC$TL_theme) r0
            java.io.File r6 = new java.io.File
            java.io.File r7 = org.telegram.messenger.ApplicationLoader.getFilesDirFixed()
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            java.lang.String r11 = "remote"
            r9.append(r11)
            long r11 = r0.id
            r9.append(r11)
            java.lang.String r0 = ".attheme"
            r9.append(r0)
            java.lang.String r0 = r9.toString()
            r6.<init>(r7, r0)
            r8.cacheFileFinal = r6
            goto L_0x03d3
        L_0x03ba:
            boolean r0 = r8.encryptFile
            if (r0 != 0) goto L_0x03ca
            java.io.File r0 = new java.io.File
            java.io.File r6 = r8.storePath
            java.lang.String r7 = r8.storeFileName
            r0.<init>(r6, r7)
            r8.cacheFileFinal = r0
            goto L_0x03d3
        L_0x03ca:
            java.io.File r0 = new java.io.File
            java.io.File r6 = r8.storePath
            r0.<init>(r6, r5)
            r8.cacheFileFinal = r0
        L_0x03d3:
            java.io.File r0 = r8.cacheFileFinal
            boolean r0 = r0.exists()
            if (r0 == 0) goto L_0x0407
            java.lang.Object r6 = r8.parentObject
            boolean r6 = r6 instanceof org.telegram.tgnet.TLRPC$TL_theme
            if (r6 != 0) goto L_0x03f3
            long r6 = r8.totalBytesCount
            r11 = 0
            int r9 = (r6 > r11 ? 1 : (r6 == r11 ? 0 : -1))
            if (r9 == 0) goto L_0x0407
            java.io.File r9 = r8.cacheFileFinal
            long r11 = r9.length()
            int r9 = (r6 > r11 ? 1 : (r6 == r11 ? 0 : -1))
            if (r9 == 0) goto L_0x0407
        L_0x03f3:
            org.telegram.messenger.FileLoadOperation$FileLoadOperationDelegate r0 = r8.delegate
            java.io.File r6 = r8.cacheFileFinal
            java.lang.String r6 = r6.toString()
            boolean r0 = r0.hasAnotherRefOnFile(r6)
            if (r0 != 0) goto L_0x0406
            java.io.File r0 = r8.cacheFileFinal
            r0.delete()
        L_0x0406:
            r0 = 0
        L_0x0407:
            if (r0 != 0) goto L_0x080c
            java.io.File r0 = new java.io.File
            java.io.File r6 = r8.tempPath
            r0.<init>(r6, r4)
            r8.cacheFileTemp = r0
            boolean r0 = r8.ungzip
            if (r0 == 0) goto L_0x0430
            java.io.File r0 = new java.io.File
            java.io.File r6 = r8.tempPath
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            r7.append(r4)
            java.lang.String r4 = ".gz"
            r7.append(r4)
            java.lang.String r4 = r7.toString()
            r0.<init>(r6, r4)
            r8.cacheFileGzipTemp = r0
        L_0x0430:
            boolean r0 = r8.encryptFile
            java.lang.String r4 = "rws"
            if (r0 == 0) goto L_0x04ac
            java.io.File r0 = new java.io.File
            java.io.File r6 = org.telegram.messenger.FileLoader.getInternalCacheDir()
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            r7.append(r5)
            java.lang.String r5 = ".key"
            r7.append(r5)
            java.lang.String r5 = r7.toString()
            r0.<init>(r6, r5)
            java.io.RandomAccessFile r5 = new java.io.RandomAccessFile     // Catch:{ Exception -> 0x04a5 }
            r5.<init>(r0, r4)     // Catch:{ Exception -> 0x04a5 }
            long r6 = r0.length()     // Catch:{ Exception -> 0x04a5 }
            r0 = 32
            byte[] r9 = new byte[r0]     // Catch:{ Exception -> 0x04a5 }
            r8.encryptKey = r9     // Catch:{ Exception -> 0x04a5 }
            r11 = 16
            byte[] r12 = new byte[r11]     // Catch:{ Exception -> 0x04a5 }
            r8.encryptIv = r12     // Catch:{ Exception -> 0x04a5 }
            r12 = 0
            int r14 = (r6 > r12 ? 1 : (r6 == r12 ? 0 : -1))
            if (r14 <= 0) goto L_0x047c
            r14 = 48
            long r6 = r6 % r14
            int r14 = (r6 > r12 ? 1 : (r6 == r12 ? 0 : -1))
            if (r14 != 0) goto L_0x047c
            r5.read(r9, r10, r0)     // Catch:{ Exception -> 0x04a5 }
            byte[] r0 = r8.encryptIv     // Catch:{ Exception -> 0x04a5 }
            r5.read(r0, r10, r11)     // Catch:{ Exception -> 0x04a5 }
            r6 = 0
            goto L_0x0493
        L_0x047c:
            java.security.SecureRandom r0 = org.telegram.messenger.Utilities.random     // Catch:{ Exception -> 0x04a5 }
            r0.nextBytes(r9)     // Catch:{ Exception -> 0x04a5 }
            java.security.SecureRandom r0 = org.telegram.messenger.Utilities.random     // Catch:{ Exception -> 0x04a5 }
            byte[] r6 = r8.encryptIv     // Catch:{ Exception -> 0x04a5 }
            r0.nextBytes(r6)     // Catch:{ Exception -> 0x04a5 }
            byte[] r0 = r8.encryptKey     // Catch:{ Exception -> 0x04a5 }
            r5.write(r0)     // Catch:{ Exception -> 0x04a5 }
            byte[] r0 = r8.encryptIv     // Catch:{ Exception -> 0x04a5 }
            r5.write(r0)     // Catch:{ Exception -> 0x04a5 }
            r6 = 1
        L_0x0493:
            java.nio.channels.FileChannel r0 = r5.getChannel()     // Catch:{ Exception -> 0x049b }
            r0.close()     // Catch:{ Exception -> 0x049b }
            goto L_0x049f
        L_0x049b:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ Exception -> 0x04a3 }
        L_0x049f:
            r5.close()     // Catch:{ Exception -> 0x04a3 }
            goto L_0x04aa
        L_0x04a3:
            r0 = move-exception
            goto L_0x04a7
        L_0x04a5:
            r0 = move-exception
            r6 = 0
        L_0x04a7:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x04aa:
            r5 = 1
            goto L_0x04ae
        L_0x04ac:
            r5 = 1
            r6 = 0
        L_0x04ae:
            boolean[] r7 = new boolean[r5]
            r7[r10] = r10
            boolean r0 = r8.supportsPreloading
            r13 = 8
            if (r0 == 0) goto L_0x060a
            if (r3 == 0) goto L_0x060a
            java.io.File r0 = new java.io.File
            java.io.File r5 = r8.tempPath
            r0.<init>(r5, r3)
            r8.cacheFilePreload = r0
            java.io.RandomAccessFile r0 = new java.io.RandomAccessFile     // Catch:{ Exception -> 0x05da }
            java.io.File r3 = r8.cacheFilePreload     // Catch:{ Exception -> 0x05da }
            r0.<init>(r3, r4)     // Catch:{ Exception -> 0x05da }
            r8.preloadStream = r0     // Catch:{ Exception -> 0x05da }
            long r16 = r0.length()     // Catch:{ Exception -> 0x05da }
            r3 = 1
            r8.preloadStreamFileOffset = r3     // Catch:{ Exception -> 0x05da }
            r18 = 0
            long r20 = r16 - r18
            r18 = 1
            int r0 = (r20 > r18 ? 1 : (r20 == r18 ? 0 : -1))
            if (r0 <= 0) goto L_0x05cc
            java.io.RandomAccessFile r0 = r8.preloadStream     // Catch:{ Exception -> 0x05da }
            byte r0 = r0.readByte()     // Catch:{ Exception -> 0x05da }
            if (r0 == 0) goto L_0x04e7
            r0 = 1
            goto L_0x04e8
        L_0x04e7:
            r0 = 0
        L_0x04e8:
            r7[r10] = r0     // Catch:{ Exception -> 0x05da }
        L_0x04ea:
            int r0 = (r18 > r16 ? 1 : (r18 == r16 ? 0 : -1))
            if (r0 >= 0) goto L_0x05cc
            long r20 = r16 - r18
            int r0 = (r20 > r13 ? 1 : (r20 == r13 ? 0 : -1))
            if (r0 >= 0) goto L_0x04f6
            goto L_0x05cc
        L_0x04f6:
            java.io.RandomAccessFile r0 = r8.preloadStream     // Catch:{ Exception -> 0x05da }
            long r20 = r0.readLong()     // Catch:{ Exception -> 0x05da }
            long r18 = r18 + r13
            long r22 = r16 - r18
            int r0 = (r22 > r13 ? 1 : (r22 == r13 ? 0 : -1))
            if (r0 < 0) goto L_0x05cc
            r22 = 0
            int r0 = (r20 > r22 ? 1 : (r20 == r22 ? 0 : -1))
            if (r0 < 0) goto L_0x05cc
            long r10 = r8.totalBytesCount     // Catch:{ Exception -> 0x05da }
            int r0 = (r20 > r10 ? 1 : (r20 == r10 ? 0 : -1))
            if (r0 <= 0) goto L_0x0512
            goto L_0x05cc
        L_0x0512:
            java.io.RandomAccessFile r0 = r8.preloadStream     // Catch:{ Exception -> 0x05da }
            long r9 = r0.readLong()     // Catch:{ Exception -> 0x05da }
            long r18 = r18 + r13
            long r11 = r16 - r18
            int r0 = (r11 > r9 ? 1 : (r11 == r9 ? 0 : -1))
            if (r0 < 0) goto L_0x05cc
            int r0 = r8.currentDownloadChunkSize     // Catch:{ Exception -> 0x05da }
            long r11 = (long) r0     // Catch:{ Exception -> 0x05da }
            int r0 = (r9 > r11 ? 1 : (r9 == r11 ? 0 : -1))
            if (r0 <= 0) goto L_0x0529
            goto L_0x05cc
        L_0x0529:
            org.telegram.messenger.FileLoadOperation$PreloadRange r0 = new org.telegram.messenger.FileLoadOperation$PreloadRange     // Catch:{ Exception -> 0x05da }
            r29 = 0
            r24 = r0
            r25 = r18
            r27 = r9
            r24.<init>(r25, r27)     // Catch:{ Exception -> 0x05da }
            long r11 = r18 + r9
            java.io.RandomAccessFile r5 = r8.preloadStream     // Catch:{ Exception -> 0x05da }
            r5.seek(r11)     // Catch:{ Exception -> 0x05da }
            long r18 = r16 - r11
            r24 = 24
            int r5 = (r18 > r24 ? 1 : (r18 == r24 ? 0 : -1))
            if (r5 >= 0) goto L_0x0547
            goto L_0x05cc
        L_0x0547:
            java.io.RandomAccessFile r5 = r8.preloadStream     // Catch:{ Exception -> 0x05da }
            long r13 = r5.readLong()     // Catch:{ Exception -> 0x05da }
            r8.foundMoovSize = r13     // Catch:{ Exception -> 0x05da }
            r26 = 0
            int r5 = (r13 > r26 ? 1 : (r13 == r26 ? 0 : -1))
            if (r5 == 0) goto L_0x056f
            r34 = r4
            long r3 = r8.nextPreloadDownloadOffset     // Catch:{ Exception -> 0x056c }
            r15 = r6
            long r5 = r8.totalBytesCount     // Catch:{ Exception -> 0x05d8 }
            r22 = 2
            long r5 = r5 / r22
            int r27 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r27 <= 0) goto L_0x0566
            r3 = 2
            goto L_0x0567
        L_0x0566:
            r3 = 1
        L_0x0567:
            r8.moovFound = r3     // Catch:{ Exception -> 0x05d8 }
            r8.preloadNotRequestedBytesCount = r13     // Catch:{ Exception -> 0x05d8 }
            goto L_0x0572
        L_0x056c:
            r0 = move-exception
            goto L_0x05dd
        L_0x056f:
            r34 = r4
            r15 = r6
        L_0x0572:
            java.io.RandomAccessFile r3 = r8.preloadStream     // Catch:{ Exception -> 0x05d8 }
            long r3 = r3.readLong()     // Catch:{ Exception -> 0x05d8 }
            r8.nextPreloadDownloadOffset = r3     // Catch:{ Exception -> 0x05d8 }
            java.io.RandomAccessFile r3 = r8.preloadStream     // Catch:{ Exception -> 0x05d8 }
            long r3 = r3.readLong()     // Catch:{ Exception -> 0x05d8 }
            r8.nextAtomOffset = r3     // Catch:{ Exception -> 0x05d8 }
            long r3 = r11 + r24
            java.util.HashMap<java.lang.Long, org.telegram.messenger.FileLoadOperation$PreloadRange> r5 = r8.preloadedBytesRanges     // Catch:{ Exception -> 0x05d8 }
            if (r5 != 0) goto L_0x058f
            java.util.HashMap r5 = new java.util.HashMap     // Catch:{ Exception -> 0x05d8 }
            r5.<init>()     // Catch:{ Exception -> 0x05d8 }
            r8.preloadedBytesRanges = r5     // Catch:{ Exception -> 0x05d8 }
        L_0x058f:
            java.util.HashMap<java.lang.Long, java.lang.Integer> r5 = r8.requestedPreloadedBytesRanges     // Catch:{ Exception -> 0x05d8 }
            if (r5 != 0) goto L_0x059a
            java.util.HashMap r5 = new java.util.HashMap     // Catch:{ Exception -> 0x05d8 }
            r5.<init>()     // Catch:{ Exception -> 0x05d8 }
            r8.requestedPreloadedBytesRanges = r5     // Catch:{ Exception -> 0x05d8 }
        L_0x059a:
            java.util.HashMap<java.lang.Long, org.telegram.messenger.FileLoadOperation$PreloadRange> r5 = r8.preloadedBytesRanges     // Catch:{ Exception -> 0x05d8 }
            java.lang.Long r6 = java.lang.Long.valueOf(r20)     // Catch:{ Exception -> 0x05d8 }
            r5.put(r6, r0)     // Catch:{ Exception -> 0x05d8 }
            java.util.HashMap<java.lang.Long, java.lang.Integer> r0 = r8.requestedPreloadedBytesRanges     // Catch:{ Exception -> 0x05d8 }
            java.lang.Long r5 = java.lang.Long.valueOf(r20)     // Catch:{ Exception -> 0x05d8 }
            r6 = 1
            java.lang.Integer r11 = java.lang.Integer.valueOf(r6)     // Catch:{ Exception -> 0x05d8 }
            r0.put(r5, r11)     // Catch:{ Exception -> 0x05d8 }
            int r0 = r8.totalPreloadedBytes     // Catch:{ Exception -> 0x05d8 }
            long r5 = (long) r0     // Catch:{ Exception -> 0x05d8 }
            long r5 = r5 + r9
            int r0 = (int) r5     // Catch:{ Exception -> 0x05d8 }
            r8.totalPreloadedBytes = r0     // Catch:{ Exception -> 0x05d8 }
            int r0 = r8.preloadStreamFileOffset     // Catch:{ Exception -> 0x05d8 }
            long r5 = (long) r0     // Catch:{ Exception -> 0x05d8 }
            r11 = 36
            long r9 = r9 + r11
            long r5 = r5 + r9
            int r0 = (int) r5     // Catch:{ Exception -> 0x05d8 }
            r8.preloadStreamFileOffset = r0     // Catch:{ Exception -> 0x05d8 }
            r18 = r3
            r6 = r15
            r10 = 0
            r13 = 8
            r4 = r34
            goto L_0x04ea
        L_0x05cc:
            r34 = r4
            r15 = r6
            java.io.RandomAccessFile r0 = r8.preloadStream     // Catch:{ Exception -> 0x05d8 }
            int r3 = r8.preloadStreamFileOffset     // Catch:{ Exception -> 0x05d8 }
            long r3 = (long) r3     // Catch:{ Exception -> 0x05d8 }
            r0.seek(r3)     // Catch:{ Exception -> 0x05d8 }
            goto L_0x05e1
        L_0x05d8:
            r0 = move-exception
            goto L_0x05de
        L_0x05da:
            r0 = move-exception
            r34 = r4
        L_0x05dd:
            r15 = r6
        L_0x05de:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x05e1:
            boolean r0 = r8.isPreloadVideoOperation
            if (r0 != 0) goto L_0x060d
            java.util.HashMap<java.lang.Long, org.telegram.messenger.FileLoadOperation$PreloadRange> r0 = r8.preloadedBytesRanges
            if (r0 != 0) goto L_0x060d
            r3 = 0
            r8.cacheFilePreload = r3
            java.io.RandomAccessFile r0 = r8.preloadStream     // Catch:{ Exception -> 0x0605 }
            if (r0 == 0) goto L_0x060d
            java.nio.channels.FileChannel r0 = r0.getChannel()     // Catch:{ Exception -> 0x05f8 }
            r0.close()     // Catch:{ Exception -> 0x05f8 }
            goto L_0x05fc
        L_0x05f8:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ Exception -> 0x0605 }
        L_0x05fc:
            java.io.RandomAccessFile r0 = r8.preloadStream     // Catch:{ Exception -> 0x0605 }
            r0.close()     // Catch:{ Exception -> 0x0605 }
            r3 = 0
            r8.preloadStream = r3     // Catch:{ Exception -> 0x0605 }
            goto L_0x060d
        L_0x0605:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x060d
        L_0x060a:
            r34 = r4
            r15 = r6
        L_0x060d:
            if (r2 == 0) goto L_0x0681
            java.io.File r0 = new java.io.File
            java.io.File r3 = r8.tempPath
            r0.<init>(r3, r2)
            r8.cacheFileParts = r0
            java.io.RandomAccessFile r0 = new java.io.RandomAccessFile     // Catch:{ Exception -> 0x067a }
            java.io.File r2 = r8.cacheFileParts     // Catch:{ Exception -> 0x067a }
            r4 = r34
            r0.<init>(r2, r4)     // Catch:{ Exception -> 0x0678 }
            r8.filePartsStream = r0     // Catch:{ Exception -> 0x0678 }
            long r2 = r0.length()     // Catch:{ Exception -> 0x0678 }
            r5 = 8
            long r5 = r2 % r5
            r9 = 4
            int r0 = (r5 > r9 ? 1 : (r5 == r9 ? 0 : -1))
            if (r0 != 0) goto L_0x0683
            long r2 = r2 - r9
            java.io.RandomAccessFile r0 = r8.filePartsStream     // Catch:{ Exception -> 0x0678 }
            int r0 = r0.readInt()     // Catch:{ Exception -> 0x0678 }
            long r5 = (long) r0     // Catch:{ Exception -> 0x0678 }
            r9 = 2
            long r2 = r2 / r9
            int r9 = (r5 > r2 ? 1 : (r5 == r2 ? 0 : -1))
            if (r9 > 0) goto L_0x0683
            r3 = 0
        L_0x0641:
            if (r3 >= r0) goto L_0x0683
            java.io.RandomAccessFile r2 = r8.filePartsStream     // Catch:{ Exception -> 0x0678 }
            long r5 = r2.readLong()     // Catch:{ Exception -> 0x0678 }
            java.io.RandomAccessFile r2 = r8.filePartsStream     // Catch:{ Exception -> 0x0678 }
            long r16 = r2.readLong()     // Catch:{ Exception -> 0x0678 }
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$Range> r2 = r8.notLoadedBytesRanges     // Catch:{ Exception -> 0x0678 }
            org.telegram.messenger.FileLoadOperation$Range r14 = new org.telegram.messenger.FileLoadOperation$Range     // Catch:{ Exception -> 0x0678 }
            r18 = 0
            r9 = r14
            r10 = r5
            r12 = r16
            r31 = r0
            r0 = r14
            r14 = r18
            r9.<init>(r10, r12)     // Catch:{ Exception -> 0x0678 }
            r2.add(r0)     // Catch:{ Exception -> 0x0678 }
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$Range> r0 = r8.notRequestedBytesRanges     // Catch:{ Exception -> 0x0678 }
            org.telegram.messenger.FileLoadOperation$Range r2 = new org.telegram.messenger.FileLoadOperation$Range     // Catch:{ Exception -> 0x0678 }
            r14 = 0
            r9 = r2
            r10 = r5
            r12 = r16
            r9.<init>(r10, r12)     // Catch:{ Exception -> 0x0678 }
            r0.add(r2)     // Catch:{ Exception -> 0x0678 }
            int r3 = r3 + 1
            r0 = r31
            goto L_0x0641
        L_0x0678:
            r0 = move-exception
            goto L_0x067d
        L_0x067a:
            r0 = move-exception
            r4 = r34
        L_0x067d:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x0683
        L_0x0681:
            r4 = r34
        L_0x0683:
            java.io.File r0 = r8.cacheFileTemp
            boolean r0 = r0.exists()
            if (r0 == 0) goto L_0x06e4
            if (r15 == 0) goto L_0x0694
            java.io.File r0 = r8.cacheFileTemp
            r0.delete()
            goto L_0x070b
        L_0x0694:
            java.io.File r0 = r8.cacheFileTemp
            long r2 = r0.length()
            if (r1 == 0) goto L_0x06a9
            int r0 = r8.currentDownloadChunkSize
            long r5 = (long) r0
            long r2 = r2 % r5
            r5 = 0
            int r0 = (r2 > r5 ? 1 : (r2 == r5 ? 0 : -1))
            if (r0 == 0) goto L_0x06a9
            r8.requestedBytesCount = r5
            goto L_0x06ba
        L_0x06a9:
            java.io.File r0 = r8.cacheFileTemp
            long r2 = r0.length()
            int r0 = r8.currentDownloadChunkSize
            long r5 = (long) r0
            long r2 = r2 / r5
            long r5 = (long) r0
            long r2 = r2 * r5
            r8.downloadedBytes = r2
            r8.requestedBytesCount = r2
        L_0x06ba:
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$Range> r0 = r8.notLoadedBytesRanges
            if (r0 == 0) goto L_0x070b
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L_0x070b
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$Range> r0 = r8.notLoadedBytesRanges
            org.telegram.messenger.FileLoadOperation$Range r2 = new org.telegram.messenger.FileLoadOperation$Range
            long r10 = r8.downloadedBytes
            long r12 = r8.totalBytesCount
            r14 = 0
            r9 = r2
            r9.<init>(r10, r12)
            r0.add(r2)
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$Range> r0 = r8.notRequestedBytesRanges
            org.telegram.messenger.FileLoadOperation$Range r2 = new org.telegram.messenger.FileLoadOperation$Range
            long r10 = r8.downloadedBytes
            long r12 = r8.totalBytesCount
            r9 = r2
            r9.<init>(r10, r12)
            r0.add(r2)
            goto L_0x070b
        L_0x06e4:
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$Range> r0 = r8.notLoadedBytesRanges
            if (r0 == 0) goto L_0x070b
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L_0x070b
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$Range> r0 = r8.notLoadedBytesRanges
            org.telegram.messenger.FileLoadOperation$Range r2 = new org.telegram.messenger.FileLoadOperation$Range
            r10 = 0
            long r12 = r8.totalBytesCount
            r14 = 0
            r9 = r2
            r9.<init>(r10, r12)
            r0.add(r2)
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$Range> r0 = r8.notRequestedBytesRanges
            org.telegram.messenger.FileLoadOperation$Range r2 = new org.telegram.messenger.FileLoadOperation$Range
            long r12 = r8.totalBytesCount
            r9 = r2
            r9.<init>(r10, r12)
            r0.add(r2)
        L_0x070b:
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$Range> r0 = r8.notLoadedBytesRanges
            if (r0 == 0) goto L_0x0737
            long r2 = r8.totalBytesCount
            r8.downloadedBytes = r2
            int r0 = r0.size()
            r3 = 0
        L_0x0718:
            if (r3 >= r0) goto L_0x0733
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$Range> r2 = r8.notLoadedBytesRanges
            java.lang.Object r2 = r2.get(r3)
            org.telegram.messenger.FileLoadOperation$Range r2 = (org.telegram.messenger.FileLoadOperation.Range) r2
            long r5 = r8.downloadedBytes
            long r9 = r2.end
            long r11 = r2.start
            long r9 = r9 - r11
            long r5 = r5 - r9
            r8.downloadedBytes = r5
            int r3 = r3 + 1
            goto L_0x0718
        L_0x0733:
            long r2 = r8.downloadedBytes
            r8.requestedBytesCount = r2
        L_0x0737:
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x0776
            boolean r0 = r8.isPreloadVideoOperation
            if (r0 == 0) goto L_0x0756
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r2 = "start preloading file to temp = "
            r0.append(r2)
            java.io.File r2 = r8.cacheFileTemp
            r0.append(r2)
            java.lang.String r0 = r0.toString()
            org.telegram.messenger.FileLog.d(r0)
            goto L_0x0776
        L_0x0756:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r2 = "start loading file to temp = "
            r0.append(r2)
            java.io.File r2 = r8.cacheFileTemp
            r0.append(r2)
            java.lang.String r2 = " final = "
            r0.append(r2)
            java.io.File r2 = r8.cacheFileFinal
            r0.append(r2)
            java.lang.String r0 = r0.toString()
            org.telegram.messenger.FileLog.d(r0)
        L_0x0776:
            if (r1 == 0) goto L_0x07c2
            java.io.File r0 = new java.io.File
            java.io.File r2 = r8.tempPath
            r0.<init>(r2, r1)
            r8.cacheIvTemp = r0
            java.io.RandomAccessFile r0 = new java.io.RandomAccessFile     // Catch:{ Exception -> 0x07b7 }
            java.io.File r1 = r8.cacheIvTemp     // Catch:{ Exception -> 0x07b7 }
            r0.<init>(r1, r4)     // Catch:{ Exception -> 0x07b7 }
            r8.fiv = r0     // Catch:{ Exception -> 0x07b7 }
            long r0 = r8.downloadedBytes     // Catch:{ Exception -> 0x07b7 }
            r2 = 0
            int r5 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r5 == 0) goto L_0x07c2
            if (r15 != 0) goto L_0x07c2
            java.io.File r0 = r8.cacheIvTemp     // Catch:{ Exception -> 0x07b7 }
            long r0 = r0.length()     // Catch:{ Exception -> 0x07b7 }
            int r5 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r5 <= 0) goto L_0x07b0
            r5 = 64
            long r0 = r0 % r5
            int r5 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r5 != 0) goto L_0x07b0
            java.io.RandomAccessFile r0 = r8.fiv     // Catch:{ Exception -> 0x07b7 }
            byte[] r1 = r8.iv     // Catch:{ Exception -> 0x07b7 }
            r2 = 64
            r3 = 0
            r0.read(r1, r3, r2)     // Catch:{ Exception -> 0x07b7 }
            goto L_0x07c2
        L_0x07b0:
            r1 = 0
            r8.downloadedBytes = r1     // Catch:{ Exception -> 0x07b7 }
            r8.requestedBytesCount = r1     // Catch:{ Exception -> 0x07b7 }
            goto L_0x07c2
        L_0x07b7:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            r1 = 0
            r8.downloadedBytes = r1
            r8.requestedBytesCount = r1
            goto L_0x07c4
        L_0x07c2:
            r1 = 0
        L_0x07c4:
            boolean r0 = r8.isPreloadVideoOperation
            if (r0 != 0) goto L_0x07d7
            long r5 = r8.downloadedBytes
            int r0 = (r5 > r1 ? 1 : (r5 == r1 ? 0 : -1))
            if (r0 == 0) goto L_0x07d7
            long r5 = r8.totalBytesCount
            int r0 = (r5 > r1 ? 1 : (r5 == r1 ? 0 : -1))
            if (r0 <= 0) goto L_0x07d7
            r30.copyNotLoadedRanges()
        L_0x07d7:
            r30.updateProgress()
            java.io.RandomAccessFile r0 = new java.io.RandomAccessFile     // Catch:{ Exception -> 0x07f0 }
            java.io.File r1 = r8.cacheFileTemp     // Catch:{ Exception -> 0x07f0 }
            r0.<init>(r1, r4)     // Catch:{ Exception -> 0x07f0 }
            r8.fileOutputStream = r0     // Catch:{ Exception -> 0x07f0 }
            long r1 = r8.downloadedBytes     // Catch:{ Exception -> 0x07f0 }
            r4 = 0
            int r6 = (r1 > r4 ? 1 : (r1 == r4 ? 0 : -1))
            if (r6 == 0) goto L_0x07ee
            r0.seek(r1)     // Catch:{ Exception -> 0x07f0 }
        L_0x07ee:
            r1 = 0
            goto L_0x07f5
        L_0x07f0:
            r0 = move-exception
            r1 = 0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0, (boolean) r1)
        L_0x07f5:
            java.io.RandomAccessFile r0 = r8.fileOutputStream
            if (r0 != 0) goto L_0x07fe
            r2 = 1
            r8.onFail(r2, r1)
            return r1
        L_0x07fe:
            r2 = 1
            r8.started = r2
            org.telegram.messenger.DispatchQueue r0 = org.telegram.messenger.Utilities.stageQueue
            org.telegram.messenger.FileLoadOperation$$ExternalSyntheticLambda10 r1 = new org.telegram.messenger.FileLoadOperation$$ExternalSyntheticLambda10
            r1.<init>(r8, r7)
            r0.postRunnable(r1)
            goto L_0x081d
        L_0x080c:
            r1 = 0
            r2 = 1
            r8.started = r2
            r8.onFinishLoadingFile(r1)     // Catch:{ Exception -> 0x081f }
            org.telegram.messenger.FilePathDatabase$PathData r0 = r8.pathSaveData     // Catch:{ Exception -> 0x081f }
            if (r0 == 0) goto L_0x081d
            org.telegram.messenger.FileLoadOperation$FileLoadOperationDelegate r1 = r8.delegate     // Catch:{ Exception -> 0x081f }
            r2 = 0
            r1.saveFilePath(r0, r2)     // Catch:{ Exception -> 0x081f }
        L_0x081d:
            r1 = 1
            goto L_0x0824
        L_0x081f:
            r1 = 1
            r2 = 0
            r8.onFail(r1, r2)
        L_0x0824:
            return r1
        L_0x0825:
            r1 = 1
            r2 = 0
            r8.onFail(r1, r2)
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.FileLoadOperation.start(org.telegram.messenger.FileLoadOperationStream, long, boolean):boolean");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$start$4(boolean z, long j, FileLoadOperationStream fileLoadOperationStream, boolean z2) {
        if (this.streamListeners == null) {
            this.streamListeners = new ArrayList<>();
        }
        if (z) {
            int i = this.currentDownloadChunkSize;
            long j2 = (j / ((long) i)) * ((long) i);
            RequestInfo requestInfo = this.priorityRequestInfo;
            if (!(requestInfo == null || requestInfo.offset == j2)) {
                this.requestInfos.remove(this.priorityRequestInfo);
                this.requestedBytesCount -= (long) this.currentDownloadChunkSize;
                removePart(this.notRequestedBytesRanges, this.priorityRequestInfo.offset, ((long) this.currentDownloadChunkSize) + this.priorityRequestInfo.offset);
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
                this.streamPriorityStartOffset = j2;
            }
        } else {
            int i2 = this.currentDownloadChunkSize;
            this.streamStartOffset = (j / ((long) i2)) * ((long) i2);
        }
        this.streamListeners.add(fileLoadOperationStream);
        if (z2) {
            if (this.preloadedBytesRanges != null) {
                if (getDownloadedLengthFromOffsetInternal(this.notLoadedBytesRanges, this.streamStartOffset, 1) == 0 && this.preloadedBytesRanges.get(Long.valueOf(this.streamStartOffset)) != null) {
                    this.nextPartWasPreloaded = true;
                }
            }
            startDownloadRequest();
            this.nextPartWasPreloaded = false;
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$start$5(boolean[] zArr) {
        long j = this.totalBytesCount;
        if (j == 0 || ((!this.isPreloadVideoOperation || !zArr[0]) && this.downloadedBytes != j)) {
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
        FileLoadOperationDelegate fileLoadOperationDelegate = this.delegate;
        if (fileLoadOperationDelegate != null) {
            long j = this.downloadedBytes;
            long j2 = this.totalBytesCount;
            if (j != j2 && j2 > 0) {
                fileLoadOperationDelegate.didChangedLoadProgress(this, j, j2);
            }
        }
    }

    public boolean isPaused() {
        return this.paused;
    }

    public void setIsPreloadVideoOperation(boolean z) {
        boolean z2 = this.isPreloadVideoOperation;
        if (z2 == z) {
            return;
        }
        if (z && this.totalBytesCount <= 2097152) {
            return;
        }
        if (z || !z2) {
            this.isPreloadVideoOperation = z;
        } else if (this.state == 3) {
            this.isPreloadVideoOperation = z;
            this.state = 0;
            this.preloadFinished = false;
            start();
        } else if (this.state == 1) {
            Utilities.stageQueue.postRunnable(new FileLoadOperation$$ExternalSyntheticLambda6(this, z));
        } else {
            this.isPreloadVideoOperation = z;
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$setIsPreloadVideoOperation$6(boolean z) {
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
        cancel(false);
    }

    public void cancel(boolean z) {
        Utilities.stageQueue.postRunnable(new FileLoadOperation$$ExternalSyntheticLambda5(this, z));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$cancel$7(boolean z) {
        if (!(this.state == 3 || this.state == 2)) {
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
        if (z) {
            File file = this.cacheFileFinal;
            if (file != null) {
                try {
                    if (!file.delete()) {
                        this.cacheFileFinal.deleteOnExit();
                    }
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
            }
            File file2 = this.cacheFileTemp;
            if (file2 != null) {
                try {
                    if (!file2.delete()) {
                        this.cacheFileTemp.deleteOnExit();
                    }
                } catch (Exception e2) {
                    FileLog.e((Throwable) e2);
                }
            }
            File file3 = this.cacheFileParts;
            if (file3 != null) {
                try {
                    if (!file3.delete()) {
                        this.cacheFileParts.deleteOnExit();
                    }
                } catch (Exception e3) {
                    FileLog.e((Throwable) e3);
                }
            }
            File file4 = this.cacheIvTemp;
            if (file4 != null) {
                try {
                    if (!file4.delete()) {
                        this.cacheIvTemp.deleteOnExit();
                    }
                } catch (Exception e4) {
                    FileLog.e((Throwable) e4);
                }
            }
            File file5 = this.cacheFilePreload;
            if (file5 != null) {
                try {
                    if (!file5.delete()) {
                        this.cacheFilePreload.deleteOnExit();
                    }
                } catch (Exception e5) {
                    FileLog.e((Throwable) e5);
                }
            }
        }
    }

    private void cleanup() {
        try {
            RandomAccessFile randomAccessFile = this.fileOutputStream;
            if (randomAccessFile != null) {
                try {
                    randomAccessFile.getChannel().close();
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
            RandomAccessFile randomAccessFile2 = this.preloadStream;
            if (randomAccessFile2 != null) {
                try {
                    randomAccessFile2.getChannel().close();
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
            RandomAccessFile randomAccessFile3 = this.fileReadStream;
            if (randomAccessFile3 != null) {
                try {
                    randomAccessFile3.getChannel().close();
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
            RandomAccessFile randomAccessFile4 = this.filePartsStream;
            if (randomAccessFile4 != null) {
                try {
                    randomAccessFile4.getChannel().close();
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
            RandomAccessFile randomAccessFile5 = this.fiv;
            if (randomAccessFile5 != null) {
                randomAccessFile5.close();
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
        String str;
        if (this.state == 1) {
            this.state = 3;
            notifyStreamListeners();
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
                            try {
                                if (this.pathSaveData != null) {
                                    synchronized (lockObject) {
                                        this.cacheFileFinal = new File(this.storePath, this.storeFileName);
                                        int i = 1;
                                        while (this.cacheFileFinal.exists()) {
                                            int lastIndexOf = this.storeFileName.lastIndexOf(46);
                                            if (lastIndexOf > 0) {
                                                str = this.storeFileName.substring(0, lastIndexOf) + " (" + i + ")" + this.storeFileName.substring(lastIndexOf);
                                            } else {
                                                str = this.storeFileName + " (" + i + ")";
                                            }
                                            this.cacheFileFinal = new File(this.storePath, str);
                                            i++;
                                        }
                                    }
                                }
                                z2 = this.cacheFileTemp.renameTo(this.cacheFileFinal);
                            } catch (Exception e2) {
                                FileLog.e((Throwable) e2);
                            }
                        }
                        if (!z2) {
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.e("unable to rename temp = " + this.cacheFileTemp + " to final = " + this.cacheFileFinal + " retry = " + this.renameRetryCount);
                            }
                            int i2 = this.renameRetryCount + 1;
                            this.renameRetryCount = i2;
                            if (i2 < 3) {
                                this.state = 1;
                                Utilities.stageQueue.postRunnable(new FileLoadOperation$$ExternalSyntheticLambda4(this, z), 200);
                                return;
                            }
                            this.cacheFileFinal = this.cacheFileTemp;
                        } else if (this.pathSaveData != null && this.cacheFileFinal.exists()) {
                            this.delegate.saveFilePath(this.pathSaveData, this.cacheFileFinal);
                        }
                    } else {
                        onFail(false, 0);
                        return;
                    }
                }
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("finished downloading file to " + this.cacheFileFinal + " time = " + (System.currentTimeMillis() - this.startTime));
                }
                if (z) {
                    int i3 = this.currentType;
                    if (i3 == 50331648) {
                        StatsController.getInstance(this.currentAccount).incrementReceivedItemsCount(ApplicationLoader.getCurrentNetworkType(), 3, 1);
                    } else if (i3 == 33554432) {
                        StatsController.getInstance(this.currentAccount).incrementReceivedItemsCount(ApplicationLoader.getCurrentNetworkType(), 2, 1);
                    } else if (i3 == 16777216) {
                        StatsController.getInstance(this.currentAccount).incrementReceivedItemsCount(ApplicationLoader.getCurrentNetworkType(), 4, 1);
                    } else if (i3 == 67108864) {
                        StatsController.getInstance(this.currentAccount).incrementReceivedItemsCount(ApplicationLoader.getCurrentNetworkType(), 5, 1);
                    }
                }
            }
            this.delegate.didFinishLoadingFile(this, this.cacheFileFinal);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onFinishLoadingFile$8(boolean z) {
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

    private long findNextPreloadDownloadOffset(long j, long j2, NativeByteBuffer nativeByteBuffer) {
        long j3;
        NativeByteBuffer nativeByteBuffer2 = nativeByteBuffer;
        int limit = nativeByteBuffer.limit();
        long j4 = j;
        do {
            if (j4 >= j2 - ((long) (this.preloadTempBuffer != null ? 16 : 0))) {
                j3 = j2 + ((long) limit);
                if (j4 < j3) {
                    if (j4 >= j3 - 16) {
                        long j5 = j3 - j4;
                        if (j5 <= 2147483647L) {
                            this.preloadTempBufferCount = (int) j5;
                            nativeByteBuffer2.position((int) ((long) (nativeByteBuffer.limit() - this.preloadTempBufferCount)));
                            nativeByteBuffer2.readBytes(this.preloadTempBuffer, 0, this.preloadTempBufferCount, false);
                            return j3;
                        }
                        throw new RuntimeException("!!!");
                    }
                    if (this.preloadTempBufferCount != 0) {
                        nativeByteBuffer2.position(0);
                        byte[] bArr = this.preloadTempBuffer;
                        int i = this.preloadTempBufferCount;
                        nativeByteBuffer2.readBytes(bArr, i, 16 - i, false);
                        this.preloadTempBufferCount = 0;
                    } else {
                        long j6 = j4 - j2;
                        if (j6 <= 2147483647L) {
                            nativeByteBuffer2.position((int) j6);
                            nativeByteBuffer2.readBytes(this.preloadTempBuffer, 0, 16, false);
                        } else {
                            throw new RuntimeException("!!!");
                        }
                    }
                    byte[] bArr2 = this.preloadTempBuffer;
                    int i2 = ((bArr2[0] & 255) << 24) + ((bArr2[1] & 255) << 16) + ((bArr2[2] & 255) << 8) + (bArr2[3] & 255);
                    if (i2 == 0) {
                        return 0;
                    }
                    if (i2 == 1) {
                        i2 = ((bArr2[12] & 255) << 24) + ((bArr2[13] & 255) << 16) + ((bArr2[14] & 255) << 8) + (bArr2[15] & 255);
                    }
                    if (bArr2[4] == 109 && bArr2[5] == 111 && bArr2[6] == 111 && bArr2[7] == 118) {
                        return (long) (-i2);
                    }
                    j4 += (long) i2;
                }
            }
            return 0;
        } while (j4 < j3);
        return j4;
    }

    private void requestFileOffsets(long j) {
        if (!this.requestingCdnOffsets) {
            this.requestingCdnOffsets = true;
            TLRPC$TL_upload_getCdnFileHashes tLRPC$TL_upload_getCdnFileHashes = new TLRPC$TL_upload_getCdnFileHashes();
            tLRPC$TL_upload_getCdnFileHashes.file_token = this.cdnToken;
            tLRPC$TL_upload_getCdnFileHashes.offset = j;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_upload_getCdnFileHashes, new FileLoadOperation$$ExternalSyntheticLambda12(this), (QuickAckDelegate) null, (WriteToSocketDelegate) null, 0, this.datacenterId, 1, true);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$requestFileOffsets$9(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error != null) {
            onFail(false, 0);
            return;
        }
        this.requestingCdnOffsets = false;
        TLRPC$Vector tLRPC$Vector = (TLRPC$Vector) tLObject;
        if (!tLRPC$Vector.objects.isEmpty()) {
            if (this.cdnHashes == null) {
                this.cdnHashes = new HashMap<>();
            }
            for (int i = 0; i < tLRPC$Vector.objects.size(); i++) {
                TLRPC$TL_fileHash tLRPC$TL_fileHash = (TLRPC$TL_fileHash) tLRPC$Vector.objects.get(i);
                this.cdnHashes.put(Long.valueOf(tLRPC$TL_fileHash.offset), tLRPC$TL_fileHash);
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
    /* JADX WARNING: Code restructure failed: missing block: B:142:0x03ca, code lost:
        if (r0 == (r4 - r10)) goto L_0x03d2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:145:0x03d0, code lost:
        if (r9 != false) goto L_0x03d2;
     */
    /* JADX WARNING: Removed duplicated region for block: B:103:0x027a A[Catch:{ Exception -> 0x05b2 }] */
    /* JADX WARNING: Removed duplicated region for block: B:117:0x02ce A[Catch:{ Exception -> 0x05b2 }] */
    /* JADX WARNING: Removed duplicated region for block: B:120:0x0304 A[Catch:{ Exception -> 0x05b2 }] */
    /* JADX WARNING: Removed duplicated region for block: B:125:0x036d A[Catch:{ Exception -> 0x05b2 }] */
    /* JADX WARNING: Removed duplicated region for block: B:180:0x0506 A[Catch:{ Exception -> 0x05b2 }] */
    /* JADX WARNING: Removed duplicated region for block: B:77:0x0226 A[Catch:{ Exception -> 0x05b2 }] */
    /* JADX WARNING: Removed duplicated region for block: B:78:0x0233 A[Catch:{ Exception -> 0x05b2 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean processRequestResult(org.telegram.messenger.FileLoadOperation.RequestInfo r42, org.telegram.tgnet.TLRPC$TL_error r43) {
        /*
            r41 = this;
            r8 = r41
            r0 = r43
            int r1 = r8.state
            java.lang.String r2 = " offset "
            java.lang.String r3 = " "
            r9 = 1
            r10 = 0
            if (r1 == r9) goto L_0x0045
            boolean r0 = org.telegram.messenger.BuildVars.DEBUG_VERSION
            if (r0 == 0) goto L_0x0044
            int r0 = r8.state
            r1 = 3
            if (r0 != r1) goto L_0x0044
            java.lang.Exception r0 = new java.lang.Exception
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r4 = "trying to write to finished file "
            r1.append(r4)
            java.lang.String r4 = r8.fileName
            r1.append(r4)
            r1.append(r2)
            long r4 = r42.offset
            r1.append(r4)
            r1.append(r3)
            long r2 = r8.totalBytesCount
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            r0.<init>(r1)
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0044:
            return r10
        L_0x0045:
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$RequestInfo> r1 = r8.requestInfos
            r11 = r42
            r1.remove(r11)
            java.lang.String r12 = " volume_id = "
            java.lang.String r13 = " access_hash = "
            java.lang.String r14 = " local_id = "
            r7 = 2
            java.lang.String r15 = " id = "
            if (r0 != 0) goto L_0x05bd
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$Range> r0 = r8.notLoadedBytesRanges     // Catch:{ Exception -> 0x05b2 }
            if (r0 != 0) goto L_0x0069
            long r0 = r8.downloadedBytes     // Catch:{ Exception -> 0x05b2 }
            long r16 = r42.offset     // Catch:{ Exception -> 0x05b2 }
            int r3 = (r0 > r16 ? 1 : (r0 == r16 ? 0 : -1))
            if (r3 == 0) goto L_0x0069
            r41.delayRequestInfo(r42)     // Catch:{ Exception -> 0x05b2 }
            return r10
        L_0x0069:
            org.telegram.tgnet.TLRPC$TL_upload_file r0 = r42.response     // Catch:{ Exception -> 0x05b2 }
            if (r0 == 0) goto L_0x0076
            org.telegram.tgnet.TLRPC$TL_upload_file r0 = r42.response     // Catch:{ Exception -> 0x05b2 }
            org.telegram.tgnet.NativeByteBuffer r0 = r0.bytes     // Catch:{ Exception -> 0x05b2 }
            goto L_0x0091
        L_0x0076:
            org.telegram.tgnet.TLRPC$TL_upload_webFile r0 = r42.responseWeb     // Catch:{ Exception -> 0x05b2 }
            if (r0 == 0) goto L_0x0083
            org.telegram.tgnet.TLRPC$TL_upload_webFile r0 = r42.responseWeb     // Catch:{ Exception -> 0x05b2 }
            org.telegram.tgnet.NativeByteBuffer r0 = r0.bytes     // Catch:{ Exception -> 0x05b2 }
            goto L_0x0091
        L_0x0083:
            org.telegram.tgnet.TLRPC$TL_upload_cdnFile r0 = r42.responseCdn     // Catch:{ Exception -> 0x05b2 }
            if (r0 == 0) goto L_0x0090
            org.telegram.tgnet.TLRPC$TL_upload_cdnFile r0 = r42.responseCdn     // Catch:{ Exception -> 0x05b2 }
            org.telegram.tgnet.NativeByteBuffer r0 = r0.bytes     // Catch:{ Exception -> 0x05b2 }
            goto L_0x0091
        L_0x0090:
            r0 = 0
        L_0x0091:
            if (r0 == 0) goto L_0x05ac
            int r1 = r0.limit()     // Catch:{ Exception -> 0x05b2 }
            if (r1 != 0) goto L_0x009b
            goto L_0x05ac
        L_0x009b:
            int r1 = r0.limit()     // Catch:{ Exception -> 0x05b2 }
            boolean r3 = r8.isCdn     // Catch:{ Exception -> 0x05b2 }
            if (r3 == 0) goto L_0x00c8
            long r16 = r42.offset     // Catch:{ Exception -> 0x05b2 }
            int r3 = r8.cdnChunkCheckSize     // Catch:{ Exception -> 0x05b2 }
            long r4 = (long) r3     // Catch:{ Exception -> 0x05b2 }
            long r16 = r16 / r4
            long r3 = (long) r3     // Catch:{ Exception -> 0x05b2 }
            long r3 = r3 * r16
            java.util.HashMap<java.lang.Long, org.telegram.tgnet.TLRPC$TL_fileHash> r5 = r8.cdnHashes     // Catch:{ Exception -> 0x05b2 }
            if (r5 == 0) goto L_0x00be
            java.lang.Long r6 = java.lang.Long.valueOf(r3)     // Catch:{ Exception -> 0x05b2 }
            java.lang.Object r5 = r5.get(r6)     // Catch:{ Exception -> 0x05b2 }
            org.telegram.tgnet.TLRPC$TL_fileHash r5 = (org.telegram.tgnet.TLRPC$TL_fileHash) r5     // Catch:{ Exception -> 0x05b2 }
            goto L_0x00bf
        L_0x00be:
            r5 = 0
        L_0x00bf:
            if (r5 != 0) goto L_0x00c8
            r41.delayRequestInfo(r42)     // Catch:{ Exception -> 0x05b2 }
            r8.requestFileOffsets(r3)     // Catch:{ Exception -> 0x05b2 }
            return r9
        L_0x00c8:
            org.telegram.tgnet.TLRPC$TL_upload_cdnFile r3 = r42.responseCdn     // Catch:{ Exception -> 0x05b2 }
            r17 = 12
            r20 = 13
            r21 = 8
            r22 = 14
            r23 = 15
            r24 = 16
            r26 = 24
            r27 = 16
            r28 = 255(0xff, double:1.26E-321)
            if (r3 == 0) goto L_0x0112
            long r3 = r42.offset     // Catch:{ Exception -> 0x05b2 }
            long r3 = r3 / r24
            byte[] r5 = r8.cdnIv     // Catch:{ Exception -> 0x05b2 }
            long r9 = r3 & r28
            int r6 = (int) r9     // Catch:{ Exception -> 0x05b2 }
            byte r6 = (byte) r6     // Catch:{ Exception -> 0x05b2 }
            r5[r23] = r6     // Catch:{ Exception -> 0x05b2 }
            long r9 = r3 >> r21
            long r9 = r9 & r28
            int r6 = (int) r9     // Catch:{ Exception -> 0x05b2 }
            byte r6 = (byte) r6     // Catch:{ Exception -> 0x05b2 }
            r5[r22] = r6     // Catch:{ Exception -> 0x05b2 }
            long r9 = r3 >> r27
            long r9 = r9 & r28
            int r6 = (int) r9     // Catch:{ Exception -> 0x05b2 }
            byte r6 = (byte) r6     // Catch:{ Exception -> 0x05b2 }
            r5[r20] = r6     // Catch:{ Exception -> 0x05b2 }
            long r3 = r3 >> r26
            long r3 = r3 & r28
            int r4 = (int) r3     // Catch:{ Exception -> 0x05b2 }
            byte r3 = (byte) r4     // Catch:{ Exception -> 0x05b2 }
            r5[r17] = r3     // Catch:{ Exception -> 0x05b2 }
            java.nio.ByteBuffer r3 = r0.buffer     // Catch:{ Exception -> 0x05b2 }
            byte[] r4 = r8.cdnKey     // Catch:{ Exception -> 0x05b2 }
            int r6 = r0.limit()     // Catch:{ Exception -> 0x05b2 }
            r9 = 0
            org.telegram.messenger.Utilities.aesCtrDecryption(r3, r4, r5, r9, r6)     // Catch:{ Exception -> 0x05b2 }
        L_0x0112:
            boolean r3 = r8.isPreloadVideoOperation     // Catch:{ Exception -> 0x05b2 }
            if (r3 == 0) goto L_0x0241
            java.io.RandomAccessFile r3 = r8.preloadStream     // Catch:{ Exception -> 0x05b2 }
            long r4 = r42.offset     // Catch:{ Exception -> 0x05b2 }
            r3.writeLong(r4)     // Catch:{ Exception -> 0x05b2 }
            java.io.RandomAccessFile r3 = r8.preloadStream     // Catch:{ Exception -> 0x05b2 }
            long r4 = (long) r1     // Catch:{ Exception -> 0x05b2 }
            r3.writeLong(r4)     // Catch:{ Exception -> 0x05b2 }
            int r3 = r8.preloadStreamFileOffset     // Catch:{ Exception -> 0x05b2 }
            int r3 = r3 + 16
            r8.preloadStreamFileOffset = r3     // Catch:{ Exception -> 0x05b2 }
            java.io.RandomAccessFile r3 = r8.preloadStream     // Catch:{ Exception -> 0x05b2 }
            java.nio.channels.FileChannel r3 = r3.getChannel()     // Catch:{ Exception -> 0x05b2 }
            java.nio.ByteBuffer r6 = r0.buffer     // Catch:{ Exception -> 0x05b2 }
            r3.write(r6)     // Catch:{ Exception -> 0x05b2 }
            boolean r3 = org.telegram.messenger.BuildVars.DEBUG_VERSION     // Catch:{ Exception -> 0x05b2 }
            if (r3 == 0) goto L_0x0162
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x05b2 }
            r3.<init>()     // Catch:{ Exception -> 0x05b2 }
            java.lang.String r6 = "save preload file part "
            r3.append(r6)     // Catch:{ Exception -> 0x05b2 }
            java.io.File r6 = r8.cacheFilePreload     // Catch:{ Exception -> 0x05b2 }
            r3.append(r6)     // Catch:{ Exception -> 0x05b2 }
            r3.append(r2)     // Catch:{ Exception -> 0x05b2 }
            long r9 = r42.offset     // Catch:{ Exception -> 0x05b2 }
            r3.append(r9)     // Catch:{ Exception -> 0x05b2 }
            java.lang.String r2 = " size "
            r3.append(r2)     // Catch:{ Exception -> 0x05b2 }
            r3.append(r1)     // Catch:{ Exception -> 0x05b2 }
            java.lang.String r2 = r3.toString()     // Catch:{ Exception -> 0x05b2 }
            org.telegram.messenger.FileLog.d(r2)     // Catch:{ Exception -> 0x05b2 }
        L_0x0162:
            java.util.HashMap<java.lang.Long, org.telegram.messenger.FileLoadOperation$PreloadRange> r2 = r8.preloadedBytesRanges     // Catch:{ Exception -> 0x05b2 }
            if (r2 != 0) goto L_0x016d
            java.util.HashMap r2 = new java.util.HashMap     // Catch:{ Exception -> 0x05b2 }
            r2.<init>()     // Catch:{ Exception -> 0x05b2 }
            r8.preloadedBytesRanges = r2     // Catch:{ Exception -> 0x05b2 }
        L_0x016d:
            java.util.HashMap<java.lang.Long, org.telegram.messenger.FileLoadOperation$PreloadRange> r2 = r8.preloadedBytesRanges     // Catch:{ Exception -> 0x05b2 }
            long r9 = r42.offset     // Catch:{ Exception -> 0x05b2 }
            java.lang.Long r3 = java.lang.Long.valueOf(r9)     // Catch:{ Exception -> 0x05b2 }
            org.telegram.messenger.FileLoadOperation$PreloadRange r6 = new org.telegram.messenger.FileLoadOperation$PreloadRange     // Catch:{ Exception -> 0x05b2 }
            int r9 = r8.preloadStreamFileOffset     // Catch:{ Exception -> 0x05b2 }
            long r9 = (long) r9     // Catch:{ Exception -> 0x05b2 }
            r25 = 0
            r20 = r6
            r21 = r9
            r23 = r4
            r20.<init>(r21, r23)     // Catch:{ Exception -> 0x05b2 }
            r2.put(r3, r6)     // Catch:{ Exception -> 0x05b2 }
            int r2 = r8.totalPreloadedBytes     // Catch:{ Exception -> 0x05b2 }
            int r2 = r2 + r1
            r8.totalPreloadedBytes = r2     // Catch:{ Exception -> 0x05b2 }
            int r2 = r8.preloadStreamFileOffset     // Catch:{ Exception -> 0x05b2 }
            int r2 = r2 + r1
            r8.preloadStreamFileOffset = r2     // Catch:{ Exception -> 0x05b2 }
            int r1 = r8.moovFound     // Catch:{ Exception -> 0x05b2 }
            if (r1 != 0) goto L_0x01e6
            long r2 = r8.nextAtomOffset     // Catch:{ Exception -> 0x05b2 }
            long r4 = r42.offset     // Catch:{ Exception -> 0x05b2 }
            r1 = r41
            r9 = 0
            r11 = 0
            r6 = r0
            long r0 = r1.findNextPreloadDownloadOffset(r2, r4, r6)     // Catch:{ Exception -> 0x05b2 }
            int r2 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1))
            if (r2 >= 0) goto L_0x01db
            r2 = -1
            long r0 = r0 * r2
            long r2 = r8.nextPreloadDownloadOffset     // Catch:{ Exception -> 0x05b2 }
            int r4 = r8.currentDownloadChunkSize     // Catch:{ Exception -> 0x05b2 }
            long r4 = (long) r4     // Catch:{ Exception -> 0x05b2 }
            long r2 = r2 + r4
            r8.nextPreloadDownloadOffset = r2     // Catch:{ Exception -> 0x05b2 }
            long r4 = r8.totalBytesCount     // Catch:{ Exception -> 0x05b2 }
            r12 = 2
            long r4 = r4 / r12
            int r6 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r6 >= 0) goto L_0x01cd
            r2 = 1048576(0x100000, double:5.180654E-318)
            long r2 = r2 + r0
            r8.foundMoovSize = r2     // Catch:{ Exception -> 0x05b2 }
            r8.preloadNotRequestedBytesCount = r2     // Catch:{ Exception -> 0x05b2 }
            r2 = 1
            r8.moovFound = r2     // Catch:{ Exception -> 0x05b2 }
            goto L_0x01d6
        L_0x01cd:
            r2 = 2097152(0x200000, double:1.0361308E-317)
            r8.foundMoovSize = r2     // Catch:{ Exception -> 0x05b2 }
            r8.preloadNotRequestedBytesCount = r2     // Catch:{ Exception -> 0x05b2 }
            r8.moovFound = r7     // Catch:{ Exception -> 0x05b2 }
        L_0x01d6:
            r2 = -1
            r8.nextPreloadDownloadOffset = r2     // Catch:{ Exception -> 0x05b2 }
            goto L_0x01e3
        L_0x01db:
            long r2 = r8.nextPreloadDownloadOffset     // Catch:{ Exception -> 0x05b2 }
            int r4 = r8.currentDownloadChunkSize     // Catch:{ Exception -> 0x05b2 }
            long r4 = (long) r4     // Catch:{ Exception -> 0x05b2 }
            long r2 = r2 + r4
            r8.nextPreloadDownloadOffset = r2     // Catch:{ Exception -> 0x05b2 }
        L_0x01e3:
            r8.nextAtomOffset = r0     // Catch:{ Exception -> 0x05b2 }
            goto L_0x01e9
        L_0x01e6:
            r9 = 0
            r11 = 0
        L_0x01e9:
            java.io.RandomAccessFile r0 = r8.preloadStream     // Catch:{ Exception -> 0x05b2 }
            long r1 = r8.foundMoovSize     // Catch:{ Exception -> 0x05b2 }
            r0.writeLong(r1)     // Catch:{ Exception -> 0x05b2 }
            java.io.RandomAccessFile r0 = r8.preloadStream     // Catch:{ Exception -> 0x05b2 }
            long r1 = r8.nextPreloadDownloadOffset     // Catch:{ Exception -> 0x05b2 }
            r0.writeLong(r1)     // Catch:{ Exception -> 0x05b2 }
            java.io.RandomAccessFile r0 = r8.preloadStream     // Catch:{ Exception -> 0x05b2 }
            long r1 = r8.nextAtomOffset     // Catch:{ Exception -> 0x05b2 }
            r0.writeLong(r1)     // Catch:{ Exception -> 0x05b2 }
            int r0 = r8.preloadStreamFileOffset     // Catch:{ Exception -> 0x05b2 }
            int r0 = r0 + 24
            r8.preloadStreamFileOffset = r0     // Catch:{ Exception -> 0x05b2 }
            long r0 = r8.nextPreloadDownloadOffset     // Catch:{ Exception -> 0x05b2 }
            int r2 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1))
            if (r2 == 0) goto L_0x0223
            int r2 = r8.moovFound     // Catch:{ Exception -> 0x05b2 }
            if (r2 == 0) goto L_0x0214
            long r2 = r8.foundMoovSize     // Catch:{ Exception -> 0x05b2 }
            int r4 = (r2 > r9 ? 1 : (r2 == r9 ? 0 : -1))
            if (r4 < 0) goto L_0x0223
        L_0x0214:
            int r2 = r8.totalPreloadedBytes     // Catch:{ Exception -> 0x05b2 }
            r3 = 2097152(0x200000, float:2.938736E-39)
            if (r2 > r3) goto L_0x0223
            long r2 = r8.totalBytesCount     // Catch:{ Exception -> 0x05b2 }
            int r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r4 < 0) goto L_0x0221
            goto L_0x0223
        L_0x0221:
            r0 = 0
            goto L_0x0224
        L_0x0223:
            r0 = 1
        L_0x0224:
            if (r0 == 0) goto L_0x0233
            java.io.RandomAccessFile r1 = r8.preloadStream     // Catch:{ Exception -> 0x05b2 }
            r1.seek(r9)     // Catch:{ Exception -> 0x05b2 }
            java.io.RandomAccessFile r1 = r8.preloadStream     // Catch:{ Exception -> 0x05b2 }
            r2 = 1
            r1.write(r2)     // Catch:{ Exception -> 0x05b2 }
            goto L_0x052f
        L_0x0233:
            int r1 = r8.moovFound     // Catch:{ Exception -> 0x05b2 }
            if (r1 == 0) goto L_0x052f
            long r1 = r8.foundMoovSize     // Catch:{ Exception -> 0x05b2 }
            int r3 = r8.currentDownloadChunkSize     // Catch:{ Exception -> 0x05b2 }
            long r3 = (long) r3     // Catch:{ Exception -> 0x05b2 }
            long r1 = r1 - r3
            r8.foundMoovSize = r1     // Catch:{ Exception -> 0x05b2 }
            goto L_0x052f
        L_0x0241:
            r7 = 0
            r9 = 0
            long r2 = r8.downloadedBytes     // Catch:{ Exception -> 0x05b2 }
            long r4 = (long) r1     // Catch:{ Exception -> 0x05b2 }
            long r2 = r2 + r4
            r8.downloadedBytes = r2     // Catch:{ Exception -> 0x05b2 }
            long r6 = r8.totalBytesCount     // Catch:{ Exception -> 0x05b2 }
            int r18 = (r6 > r9 ? 1 : (r6 == r9 ? 0 : -1))
            if (r18 <= 0) goto L_0x0255
            int r1 = (r2 > r6 ? 1 : (r2 == r6 ? 0 : -1))
            if (r1 < 0) goto L_0x0272
            goto L_0x0274
        L_0x0255:
            int r9 = r8.currentDownloadChunkSize     // Catch:{ Exception -> 0x05b2 }
            if (r1 != r9) goto L_0x0274
            int r1 = (r6 > r2 ? 1 : (r6 == r2 ? 0 : -1))
            if (r1 == 0) goto L_0x0267
            long r9 = (long) r9     // Catch:{ Exception -> 0x05b2 }
            long r9 = r2 % r9
            r18 = 0
            int r1 = (r9 > r18 ? 1 : (r9 == r18 ? 0 : -1))
            if (r1 == 0) goto L_0x0272
            goto L_0x0269
        L_0x0267:
            r18 = 0
        L_0x0269:
            int r1 = (r6 > r18 ? 1 : (r6 == r18 ? 0 : -1))
            if (r1 <= 0) goto L_0x0274
            int r1 = (r6 > r2 ? 1 : (r6 == r2 ? 0 : -1))
            if (r1 > 0) goto L_0x0272
            goto L_0x0274
        L_0x0272:
            r1 = 0
            goto L_0x0275
        L_0x0274:
            r1 = 1
        L_0x0275:
            r9 = r1
            byte[] r1 = r8.key     // Catch:{ Exception -> 0x05b2 }
            if (r1 == 0) goto L_0x02ca
            java.nio.ByteBuffer r2 = r0.buffer     // Catch:{ Exception -> 0x05b2 }
            byte[] r3 = r8.iv     // Catch:{ Exception -> 0x05b2 }
            r33 = 0
            r34 = 1
            r35 = 0
            int r36 = r0.limit()     // Catch:{ Exception -> 0x05b2 }
            r30 = r2
            r31 = r1
            r32 = r3
            org.telegram.messenger.Utilities.aesIgeEncryption(r30, r31, r32, r33, r34, r35, r36)     // Catch:{ Exception -> 0x05b2 }
            if (r9 == 0) goto L_0x02ca
            long r1 = r8.bytesCountPadding     // Catch:{ Exception -> 0x05b2 }
            r6 = 0
            int r3 = (r1 > r6 ? 1 : (r1 == r6 ? 0 : -1))
            if (r3 == 0) goto L_0x02ca
            int r1 = r0.limit()     // Catch:{ Exception -> 0x05b2 }
            long r1 = (long) r1     // Catch:{ Exception -> 0x05b2 }
            long r6 = r8.bytesCountPadding     // Catch:{ Exception -> 0x05b2 }
            long r1 = r1 - r6
            boolean r3 = org.telegram.messenger.BuildVars.DEBUG_VERSION     // Catch:{ Exception -> 0x05b2 }
            if (r3 == 0) goto L_0x02c6
            r6 = 2147483647(0x7fffffff, double:1.060997895E-314)
            int r3 = (r1 > r6 ? 1 : (r1 == r6 ? 0 : -1))
            if (r3 > 0) goto L_0x02af
            goto L_0x02c6
        L_0x02af:
            java.lang.RuntimeException r0 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x05b2 }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x05b2 }
            r3.<init>()     // Catch:{ Exception -> 0x05b2 }
            java.lang.String r4 = "Out of limit"
            r3.append(r4)     // Catch:{ Exception -> 0x05b2 }
            r3.append(r1)     // Catch:{ Exception -> 0x05b2 }
            java.lang.String r1 = r3.toString()     // Catch:{ Exception -> 0x05b2 }
            r0.<init>(r1)     // Catch:{ Exception -> 0x05b2 }
            throw r0     // Catch:{ Exception -> 0x05b2 }
        L_0x02c6:
            int r2 = (int) r1     // Catch:{ Exception -> 0x05b2 }
            r0.limit(r2)     // Catch:{ Exception -> 0x05b2 }
        L_0x02ca:
            boolean r1 = r8.encryptFile     // Catch:{ Exception -> 0x05b2 }
            if (r1 == 0) goto L_0x0300
            long r1 = r42.offset     // Catch:{ Exception -> 0x05b2 }
            long r1 = r1 / r24
            byte[] r3 = r8.encryptIv     // Catch:{ Exception -> 0x05b2 }
            long r6 = r1 & r28
            int r7 = (int) r6     // Catch:{ Exception -> 0x05b2 }
            byte r6 = (byte) r7     // Catch:{ Exception -> 0x05b2 }
            r3[r23] = r6     // Catch:{ Exception -> 0x05b2 }
            long r6 = r1 >> r21
            long r6 = r6 & r28
            int r7 = (int) r6     // Catch:{ Exception -> 0x05b2 }
            byte r6 = (byte) r7     // Catch:{ Exception -> 0x05b2 }
            r3[r22] = r6     // Catch:{ Exception -> 0x05b2 }
            long r6 = r1 >> r27
            long r6 = r6 & r28
            int r7 = (int) r6     // Catch:{ Exception -> 0x05b2 }
            byte r6 = (byte) r7     // Catch:{ Exception -> 0x05b2 }
            r3[r20] = r6     // Catch:{ Exception -> 0x05b2 }
            long r1 = r1 >> r26
            long r1 = r1 & r28
            int r2 = (int) r1     // Catch:{ Exception -> 0x05b2 }
            byte r1 = (byte) r2     // Catch:{ Exception -> 0x05b2 }
            r3[r17] = r1     // Catch:{ Exception -> 0x05b2 }
            java.nio.ByteBuffer r1 = r0.buffer     // Catch:{ Exception -> 0x05b2 }
            byte[] r2 = r8.encryptKey     // Catch:{ Exception -> 0x05b2 }
            int r6 = r0.limit()     // Catch:{ Exception -> 0x05b2 }
            r7 = 0
            org.telegram.messenger.Utilities.aesCtrDecryption(r1, r2, r3, r7, r6)     // Catch:{ Exception -> 0x05b2 }
        L_0x0300:
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$Range> r1 = r8.notLoadedBytesRanges     // Catch:{ Exception -> 0x05b2 }
            if (r1 == 0) goto L_0x0347
            java.io.RandomAccessFile r1 = r8.fileOutputStream     // Catch:{ Exception -> 0x05b2 }
            long r2 = r42.offset     // Catch:{ Exception -> 0x05b2 }
            r1.seek(r2)     // Catch:{ Exception -> 0x05b2 }
            boolean r1 = org.telegram.messenger.BuildVars.DEBUG_VERSION     // Catch:{ Exception -> 0x05b2 }
            if (r1 == 0) goto L_0x0347
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x05b2 }
            r1.<init>()     // Catch:{ Exception -> 0x05b2 }
            java.lang.String r2 = "save file part "
            r1.append(r2)     // Catch:{ Exception -> 0x05b2 }
            java.lang.String r2 = r8.fileName     // Catch:{ Exception -> 0x05b2 }
            r1.append(r2)     // Catch:{ Exception -> 0x05b2 }
            java.lang.String r2 = " offset="
            r1.append(r2)     // Catch:{ Exception -> 0x05b2 }
            long r2 = r42.offset     // Catch:{ Exception -> 0x05b2 }
            r1.append(r2)     // Catch:{ Exception -> 0x05b2 }
            java.lang.String r2 = " chunk_size="
            r1.append(r2)     // Catch:{ Exception -> 0x05b2 }
            int r2 = r8.currentDownloadChunkSize     // Catch:{ Exception -> 0x05b2 }
            r1.append(r2)     // Catch:{ Exception -> 0x05b2 }
            java.lang.String r2 = " isCdn="
            r1.append(r2)     // Catch:{ Exception -> 0x05b2 }
            boolean r2 = r8.isCdn     // Catch:{ Exception -> 0x05b2 }
            r1.append(r2)     // Catch:{ Exception -> 0x05b2 }
            java.lang.String r1 = r1.toString()     // Catch:{ Exception -> 0x05b2 }
            org.telegram.messenger.FileLog.d(r1)     // Catch:{ Exception -> 0x05b2 }
        L_0x0347:
            java.io.RandomAccessFile r1 = r8.fileOutputStream     // Catch:{ Exception -> 0x05b2 }
            java.nio.channels.FileChannel r1 = r1.getChannel()     // Catch:{ Exception -> 0x05b2 }
            java.nio.ByteBuffer r0 = r0.buffer     // Catch:{ Exception -> 0x05b2 }
            r1.write(r0)     // Catch:{ Exception -> 0x05b2 }
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$Range> r2 = r8.notLoadedBytesRanges     // Catch:{ Exception -> 0x05b2 }
            long r6 = r42.offset     // Catch:{ Exception -> 0x05b2 }
            long r0 = r42.offset     // Catch:{ Exception -> 0x05b2 }
            long r30 = r0 + r4
            r0 = 1
            r1 = r41
            r3 = r6
            r5 = r30
            r10 = 0
            r7 = r0
            r1.addPart(r2, r3, r5, r7)     // Catch:{ Exception -> 0x05b2 }
            boolean r0 = r8.isCdn     // Catch:{ Exception -> 0x05b2 }
            if (r0 == 0) goto L_0x0500
            long r0 = r42.offset     // Catch:{ Exception -> 0x05b2 }
            int r2 = r8.cdnChunkCheckSize     // Catch:{ Exception -> 0x05b2 }
            long r2 = (long) r2     // Catch:{ Exception -> 0x05b2 }
            long r30 = r0 / r2
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$Range> r0 = r8.notCheckedCdnRanges     // Catch:{ Exception -> 0x05b2 }
            int r0 = r0.size()     // Catch:{ Exception -> 0x05b2 }
            r1 = 0
        L_0x037d:
            if (r1 >= r0) goto L_0x039c
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$Range> r2 = r8.notCheckedCdnRanges     // Catch:{ Exception -> 0x05b2 }
            java.lang.Object r2 = r2.get(r1)     // Catch:{ Exception -> 0x05b2 }
            org.telegram.messenger.FileLoadOperation$Range r2 = (org.telegram.messenger.FileLoadOperation.Range) r2     // Catch:{ Exception -> 0x05b2 }
            long r3 = r2.start     // Catch:{ Exception -> 0x05b2 }
            int r5 = (r3 > r30 ? 1 : (r3 == r30 ? 0 : -1))
            if (r5 > 0) goto L_0x0399
            long r2 = r2.end     // Catch:{ Exception -> 0x05b2 }
            int r4 = (r30 > r2 ? 1 : (r30 == r2 ? 0 : -1))
            if (r4 > 0) goto L_0x0399
            r0 = 0
            goto L_0x039d
        L_0x0399:
            int r1 = r1 + 1
            goto L_0x037d
        L_0x039c:
            r0 = 1
        L_0x039d:
            if (r0 != 0) goto L_0x0500
            int r0 = r8.cdnChunkCheckSize     // Catch:{ Exception -> 0x05b2 }
            long r1 = (long) r0     // Catch:{ Exception -> 0x05b2 }
            long r5 = r30 * r1
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$Range> r2 = r8.notLoadedBytesRanges     // Catch:{ Exception -> 0x05b2 }
            long r3 = (long) r0     // Catch:{ Exception -> 0x05b2 }
            r1 = r41
            r32 = r3
            r3 = r5
            r10 = r5
            r5 = r32
            long r0 = r1.getDownloadedLengthFromOffsetInternal(r2, r3, r5)     // Catch:{ Exception -> 0x05b2 }
            r2 = 0
            int r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r4 == 0) goto L_0x0500
            int r4 = r8.cdnChunkCheckSize     // Catch:{ Exception -> 0x05b2 }
            long r4 = (long) r4     // Catch:{ Exception -> 0x05b2 }
            int r6 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1))
            if (r6 == 0) goto L_0x03d2
            long r4 = r8.totalBytesCount     // Catch:{ Exception -> 0x05b2 }
            int r6 = (r4 > r2 ? 1 : (r4 == r2 ? 0 : -1))
            if (r6 <= 0) goto L_0x03cc
            long r6 = r4 - r10
            int r18 = (r0 > r6 ? 1 : (r0 == r6 ? 0 : -1))
            if (r18 == 0) goto L_0x03d2
        L_0x03cc:
            int r6 = (r4 > r2 ? 1 : (r4 == r2 ? 0 : -1))
            if (r6 > 0) goto L_0x0500
            if (r9 == 0) goto L_0x0500
        L_0x03d2:
            java.util.HashMap<java.lang.Long, org.telegram.tgnet.TLRPC$TL_fileHash> r2 = r8.cdnHashes     // Catch:{ Exception -> 0x05b2 }
            java.lang.Long r3 = java.lang.Long.valueOf(r10)     // Catch:{ Exception -> 0x05b2 }
            java.lang.Object r2 = r2.get(r3)     // Catch:{ Exception -> 0x05b2 }
            org.telegram.tgnet.TLRPC$TL_fileHash r2 = (org.telegram.tgnet.TLRPC$TL_fileHash) r2     // Catch:{ Exception -> 0x05b2 }
            java.io.RandomAccessFile r3 = r8.fileReadStream     // Catch:{ Exception -> 0x05b2 }
            if (r3 != 0) goto L_0x03f3
            int r3 = r8.cdnChunkCheckSize     // Catch:{ Exception -> 0x05b2 }
            byte[] r3 = new byte[r3]     // Catch:{ Exception -> 0x05b2 }
            r8.cdnCheckBytes = r3     // Catch:{ Exception -> 0x05b2 }
            java.io.RandomAccessFile r3 = new java.io.RandomAccessFile     // Catch:{ Exception -> 0x05b2 }
            java.io.File r4 = r8.cacheFileTemp     // Catch:{ Exception -> 0x05b2 }
            java.lang.String r5 = "r"
            r3.<init>(r4, r5)     // Catch:{ Exception -> 0x05b2 }
            r8.fileReadStream = r3     // Catch:{ Exception -> 0x05b2 }
        L_0x03f3:
            java.io.RandomAccessFile r3 = r8.fileReadStream     // Catch:{ Exception -> 0x05b2 }
            r3.seek(r10)     // Catch:{ Exception -> 0x05b2 }
            boolean r3 = org.telegram.messenger.BuildVars.DEBUG_VERSION     // Catch:{ Exception -> 0x05b2 }
            if (r3 == 0) goto L_0x040c
            r3 = 2147483647(0x7fffffff, double:1.060997895E-314)
            int r5 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1))
            if (r5 > 0) goto L_0x0404
            goto L_0x040c
        L_0x0404:
            java.lang.RuntimeException r0 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x05b2 }
            java.lang.String r1 = "!!!"
            r0.<init>(r1)     // Catch:{ Exception -> 0x05b2 }
            throw r0     // Catch:{ Exception -> 0x05b2 }
        L_0x040c:
            java.io.RandomAccessFile r3 = r8.fileReadStream     // Catch:{ Exception -> 0x05b2 }
            byte[] r4 = r8.cdnCheckBytes     // Catch:{ Exception -> 0x05b2 }
            int r5 = (int) r0     // Catch:{ Exception -> 0x05b2 }
            r6 = 0
            r3.readFully(r4, r6, r5)     // Catch:{ Exception -> 0x05b2 }
            boolean r3 = r8.encryptFile     // Catch:{ Exception -> 0x05b2 }
            if (r3 == 0) goto L_0x0453
            long r5 = r10 / r24
            byte[] r3 = r8.encryptIv     // Catch:{ Exception -> 0x05b2 }
            r43 = r9
            r24 = r10
            long r9 = r5 & r28
            int r4 = (int) r9     // Catch:{ Exception -> 0x05b2 }
            byte r4 = (byte) r4     // Catch:{ Exception -> 0x05b2 }
            r3[r23] = r4     // Catch:{ Exception -> 0x05b2 }
            long r9 = r5 >> r21
            long r9 = r9 & r28
            int r4 = (int) r9     // Catch:{ Exception -> 0x05b2 }
            byte r4 = (byte) r4     // Catch:{ Exception -> 0x05b2 }
            r3[r22] = r4     // Catch:{ Exception -> 0x05b2 }
            long r9 = r5 >> r27
            long r9 = r9 & r28
            int r4 = (int) r9     // Catch:{ Exception -> 0x05b2 }
            byte r4 = (byte) r4     // Catch:{ Exception -> 0x05b2 }
            r3[r20] = r4     // Catch:{ Exception -> 0x05b2 }
            long r4 = r5 >> r26
            long r4 = r4 & r28
            int r5 = (int) r4     // Catch:{ Exception -> 0x05b2 }
            byte r4 = (byte) r5     // Catch:{ Exception -> 0x05b2 }
            r3[r17] = r4     // Catch:{ Exception -> 0x05b2 }
            byte[] r4 = r8.cdnCheckBytes     // Catch:{ Exception -> 0x05b2 }
            byte[] r5 = r8.encryptKey     // Catch:{ Exception -> 0x05b2 }
            r37 = 0
            r40 = 0
            r34 = r4
            r35 = r5
            r36 = r3
            r38 = r0
            org.telegram.messenger.Utilities.aesCtrDecryptionByteArray(r34, r35, r36, r37, r38, r40)     // Catch:{ Exception -> 0x05b2 }
            goto L_0x0457
        L_0x0453:
            r43 = r9
            r24 = r10
        L_0x0457:
            byte[] r3 = r8.cdnCheckBytes     // Catch:{ Exception -> 0x05b2 }
            r4 = 0
            byte[] r0 = org.telegram.messenger.Utilities.computeSHA256(r3, r4, r0)     // Catch:{ Exception -> 0x05b2 }
            byte[] r1 = r2.hash     // Catch:{ Exception -> 0x05b2 }
            boolean r0 = java.util.Arrays.equals(r0, r1)     // Catch:{ Exception -> 0x05b2 }
            if (r0 != 0) goto L_0x04e8
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x05b2 }
            if (r0 == 0) goto L_0x04db
            org.telegram.tgnet.TLRPC$InputFileLocation r0 = r8.location     // Catch:{ Exception -> 0x05b2 }
            if (r0 == 0) goto L_0x04b9
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x05b2 }
            r0.<init>()     // Catch:{ Exception -> 0x05b2 }
            java.lang.String r1 = "invalid cdn hash "
            r0.append(r1)     // Catch:{ Exception -> 0x05b2 }
            org.telegram.tgnet.TLRPC$InputFileLocation r1 = r8.location     // Catch:{ Exception -> 0x05b2 }
            r0.append(r1)     // Catch:{ Exception -> 0x05b2 }
            r0.append(r15)     // Catch:{ Exception -> 0x05b2 }
            org.telegram.tgnet.TLRPC$InputFileLocation r1 = r8.location     // Catch:{ Exception -> 0x05b2 }
            long r1 = r1.id     // Catch:{ Exception -> 0x05b2 }
            r0.append(r1)     // Catch:{ Exception -> 0x05b2 }
            r0.append(r14)     // Catch:{ Exception -> 0x05b2 }
            org.telegram.tgnet.TLRPC$InputFileLocation r1 = r8.location     // Catch:{ Exception -> 0x05b2 }
            int r1 = r1.local_id     // Catch:{ Exception -> 0x05b2 }
            r0.append(r1)     // Catch:{ Exception -> 0x05b2 }
            r0.append(r13)     // Catch:{ Exception -> 0x05b2 }
            org.telegram.tgnet.TLRPC$InputFileLocation r1 = r8.location     // Catch:{ Exception -> 0x05b2 }
            long r1 = r1.access_hash     // Catch:{ Exception -> 0x05b2 }
            r0.append(r1)     // Catch:{ Exception -> 0x05b2 }
            r0.append(r12)     // Catch:{ Exception -> 0x05b2 }
            org.telegram.tgnet.TLRPC$InputFileLocation r1 = r8.location     // Catch:{ Exception -> 0x05b2 }
            long r1 = r1.volume_id     // Catch:{ Exception -> 0x05b2 }
            r0.append(r1)     // Catch:{ Exception -> 0x05b2 }
            java.lang.String r1 = " secret = "
            r0.append(r1)     // Catch:{ Exception -> 0x05b2 }
            org.telegram.tgnet.TLRPC$InputFileLocation r1 = r8.location     // Catch:{ Exception -> 0x05b2 }
            long r1 = r1.secret     // Catch:{ Exception -> 0x05b2 }
            r0.append(r1)     // Catch:{ Exception -> 0x05b2 }
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x05b2 }
            org.telegram.messenger.FileLog.e((java.lang.String) r0)     // Catch:{ Exception -> 0x05b2 }
            goto L_0x04db
        L_0x04b9:
            org.telegram.tgnet.TLRPC$InputWebFileLocation r0 = r8.webLocation     // Catch:{ Exception -> 0x05b2 }
            if (r0 == 0) goto L_0x04db
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x05b2 }
            r0.<init>()     // Catch:{ Exception -> 0x05b2 }
            java.lang.String r1 = "invalid cdn hash  "
            r0.append(r1)     // Catch:{ Exception -> 0x05b2 }
            org.telegram.tgnet.TLRPC$InputWebFileLocation r1 = r8.webLocation     // Catch:{ Exception -> 0x05b2 }
            r0.append(r1)     // Catch:{ Exception -> 0x05b2 }
            r0.append(r15)     // Catch:{ Exception -> 0x05b2 }
            java.lang.String r1 = r8.fileName     // Catch:{ Exception -> 0x05b2 }
            r0.append(r1)     // Catch:{ Exception -> 0x05b2 }
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x05b2 }
            org.telegram.messenger.FileLog.e((java.lang.String) r0)     // Catch:{ Exception -> 0x05b2 }
        L_0x04db:
            r1 = 0
            r8.onFail(r1, r1)     // Catch:{ Exception -> 0x04e5 }
            java.io.File r0 = r8.cacheFileTemp     // Catch:{ Exception -> 0x04e5 }
            r0.delete()     // Catch:{ Exception -> 0x04e5 }
            return r1
        L_0x04e5:
            r0 = move-exception
            goto L_0x05b4
        L_0x04e8:
            java.util.HashMap<java.lang.Long, org.telegram.tgnet.TLRPC$TL_fileHash> r0 = r8.cdnHashes     // Catch:{ Exception -> 0x05b2 }
            java.lang.Long r1 = java.lang.Long.valueOf(r24)     // Catch:{ Exception -> 0x05b2 }
            r0.remove(r1)     // Catch:{ Exception -> 0x05b2 }
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$Range> r2 = r8.notCheckedCdnRanges     // Catch:{ Exception -> 0x05b2 }
            r0 = 1
            long r5 = r30 + r0
            r7 = 0
            r1 = r41
            r3 = r30
            r1.addPart(r2, r3, r5, r7)     // Catch:{ Exception -> 0x05b2 }
            goto L_0x0502
        L_0x0500:
            r43 = r9
        L_0x0502:
            java.io.RandomAccessFile r0 = r8.fiv     // Catch:{ Exception -> 0x05b2 }
            if (r0 == 0) goto L_0x0512
            r1 = 0
            r0.seek(r1)     // Catch:{ Exception -> 0x05b2 }
            java.io.RandomAccessFile r0 = r8.fiv     // Catch:{ Exception -> 0x05b2 }
            byte[] r1 = r8.iv     // Catch:{ Exception -> 0x05b2 }
            r0.write(r1)     // Catch:{ Exception -> 0x05b2 }
        L_0x0512:
            long r0 = r8.totalBytesCount     // Catch:{ Exception -> 0x05b2 }
            r2 = 0
            int r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r4 <= 0) goto L_0x052d
            int r0 = r8.state     // Catch:{ Exception -> 0x05b2 }
            r1 = 1
            if (r0 != r1) goto L_0x052d
            r41.copyNotLoadedRanges()     // Catch:{ Exception -> 0x05b2 }
            org.telegram.messenger.FileLoadOperation$FileLoadOperationDelegate r1 = r8.delegate     // Catch:{ Exception -> 0x05b2 }
            long r3 = r8.downloadedBytes     // Catch:{ Exception -> 0x05b2 }
            long r5 = r8.totalBytesCount     // Catch:{ Exception -> 0x05b2 }
            r2 = r41
            r1.didChangedLoadProgress(r2, r3, r5)     // Catch:{ Exception -> 0x05b2 }
        L_0x052d:
            r0 = r43
        L_0x052f:
            r1 = 0
        L_0x0530:
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$RequestInfo> r2 = r8.delayedRequestInfos     // Catch:{ Exception -> 0x05b2 }
            int r2 = r2.size()     // Catch:{ Exception -> 0x05b2 }
            if (r1 >= r2) goto L_0x059c
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$RequestInfo> r2 = r8.delayedRequestInfos     // Catch:{ Exception -> 0x05b2 }
            java.lang.Object r2 = r2.get(r1)     // Catch:{ Exception -> 0x05b2 }
            org.telegram.messenger.FileLoadOperation$RequestInfo r2 = (org.telegram.messenger.FileLoadOperation.RequestInfo) r2     // Catch:{ Exception -> 0x05b2 }
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$Range> r3 = r8.notLoadedBytesRanges     // Catch:{ Exception -> 0x05b2 }
            if (r3 != 0) goto L_0x0552
            long r3 = r8.downloadedBytes     // Catch:{ Exception -> 0x05b2 }
            long r5 = r2.offset     // Catch:{ Exception -> 0x05b2 }
            int r7 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r7 != 0) goto L_0x054f
            goto L_0x0552
        L_0x054f:
            int r1 = r1 + 1
            goto L_0x0530
        L_0x0552:
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$RequestInfo> r3 = r8.delayedRequestInfos     // Catch:{ Exception -> 0x05b2 }
            r3.remove(r1)     // Catch:{ Exception -> 0x05b2 }
            r1 = 0
            boolean r1 = r8.processRequestResult(r2, r1)     // Catch:{ Exception -> 0x05b2 }
            if (r1 != 0) goto L_0x059c
            org.telegram.tgnet.TLRPC$TL_upload_file r1 = r2.response     // Catch:{ Exception -> 0x05b2 }
            if (r1 == 0) goto L_0x0573
            org.telegram.tgnet.TLRPC$TL_upload_file r1 = r2.response     // Catch:{ Exception -> 0x05b2 }
            r3 = 0
            r1.disableFree = r3     // Catch:{ Exception -> 0x05b2 }
            org.telegram.tgnet.TLRPC$TL_upload_file r1 = r2.response     // Catch:{ Exception -> 0x05b2 }
            r1.freeResources()     // Catch:{ Exception -> 0x05b2 }
            goto L_0x059c
        L_0x0573:
            org.telegram.tgnet.TLRPC$TL_upload_webFile r1 = r2.responseWeb     // Catch:{ Exception -> 0x05b2 }
            if (r1 == 0) goto L_0x0588
            org.telegram.tgnet.TLRPC$TL_upload_webFile r1 = r2.responseWeb     // Catch:{ Exception -> 0x05b2 }
            r3 = 0
            r1.disableFree = r3     // Catch:{ Exception -> 0x05b2 }
            org.telegram.tgnet.TLRPC$TL_upload_webFile r1 = r2.responseWeb     // Catch:{ Exception -> 0x05b2 }
            r1.freeResources()     // Catch:{ Exception -> 0x05b2 }
            goto L_0x059c
        L_0x0588:
            org.telegram.tgnet.TLRPC$TL_upload_cdnFile r1 = r2.responseCdn     // Catch:{ Exception -> 0x05b2 }
            if (r1 == 0) goto L_0x059c
            org.telegram.tgnet.TLRPC$TL_upload_cdnFile r1 = r2.responseCdn     // Catch:{ Exception -> 0x05b2 }
            r3 = 0
            r1.disableFree = r3     // Catch:{ Exception -> 0x05b2 }
            org.telegram.tgnet.TLRPC$TL_upload_cdnFile r1 = r2.responseCdn     // Catch:{ Exception -> 0x05b2 }
            r1.freeResources()     // Catch:{ Exception -> 0x05b2 }
        L_0x059c:
            if (r0 == 0) goto L_0x05a3
            r0 = 1
            r8.onFinishLoadingFile(r0)     // Catch:{ Exception -> 0x05b2 }
            goto L_0x05ba
        L_0x05a3:
            int r0 = r8.state     // Catch:{ Exception -> 0x05b2 }
            r1 = 4
            if (r0 == r1) goto L_0x05ba
            r41.startDownloadRequest()     // Catch:{ Exception -> 0x05b2 }
            goto L_0x05ba
        L_0x05ac:
            r0 = 1
            r8.onFinishLoadingFile(r0)     // Catch:{ Exception -> 0x05b2 }
            r1 = 0
            return r1
        L_0x05b2:
            r0 = move-exception
            r1 = 0
        L_0x05b4:
            r8.onFail(r1, r1)
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x05ba:
            r1 = 0
            goto L_0x06b4
        L_0x05bd:
            r1 = 0
            java.lang.String r2 = r0.text
            java.lang.String r4 = "FILE_MIGRATE_"
            boolean r2 = r2.contains(r4)
            if (r2 == 0) goto L_0x05f9
            java.lang.String r0 = r0.text
            java.lang.String r2 = ""
            java.lang.String r0 = r0.replace(r4, r2)
            java.util.Scanner r3 = new java.util.Scanner
            r3.<init>(r0)
            r3.useDelimiter(r2)
            int r0 = r3.nextInt()     // Catch:{ Exception -> 0x05e1 }
            java.lang.Integer r6 = java.lang.Integer.valueOf(r0)     // Catch:{ Exception -> 0x05e1 }
            goto L_0x05e2
        L_0x05e1:
            r6 = r1
        L_0x05e2:
            if (r6 != 0) goto L_0x05e9
            r1 = 0
            r8.onFail(r1, r1)
            goto L_0x05ba
        L_0x05e9:
            int r0 = r6.intValue()
            r8.datacenterId = r0
            r0 = 0
            r8.downloadedBytes = r0
            r8.requestedBytesCount = r0
            r41.startDownloadRequest()
            goto L_0x05ba
        L_0x05f9:
            java.lang.String r1 = r0.text
            java.lang.String r2 = "OFFSET_INVALID"
            boolean r1 = r1.contains(r2)
            if (r1 == 0) goto L_0x0625
            long r0 = r8.downloadedBytes
            int r2 = r8.currentDownloadChunkSize
            long r2 = (long) r2
            long r0 = r0 % r2
            r2 = 0
            int r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r4 != 0) goto L_0x061f
            r0 = 1
            r8.onFinishLoadingFile(r0)     // Catch:{ Exception -> 0x0614 }
            goto L_0x05ba
        L_0x0614:
            r0 = move-exception
            r1 = r0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r1)
            r1 = 0
            r8.onFail(r1, r1)
            goto L_0x06b4
        L_0x061f:
            r1 = 0
            r8.onFail(r1, r1)
            goto L_0x06b4
        L_0x0625:
            r1 = 0
            java.lang.String r2 = r0.text
            java.lang.String r4 = "RETRY_LIMIT"
            boolean r2 = r2.contains(r4)
            if (r2 == 0) goto L_0x0635
            r8.onFail(r1, r7)
            goto L_0x06b4
        L_0x0635:
            boolean r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r1 == 0) goto L_0x06b0
            org.telegram.tgnet.TLRPC$InputFileLocation r1 = r8.location
            if (r1 == 0) goto L_0x068b
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r0 = r0.text
            r1.append(r0)
            r1.append(r3)
            org.telegram.tgnet.TLRPC$InputFileLocation r0 = r8.location
            r1.append(r0)
            r1.append(r15)
            org.telegram.tgnet.TLRPC$InputFileLocation r0 = r8.location
            long r2 = r0.id
            r1.append(r2)
            r1.append(r14)
            org.telegram.tgnet.TLRPC$InputFileLocation r0 = r8.location
            int r0 = r0.local_id
            r1.append(r0)
            r1.append(r13)
            org.telegram.tgnet.TLRPC$InputFileLocation r0 = r8.location
            long r2 = r0.access_hash
            r1.append(r2)
            r1.append(r12)
            org.telegram.tgnet.TLRPC$InputFileLocation r0 = r8.location
            long r2 = r0.volume_id
            r1.append(r2)
            java.lang.String r0 = " secret = "
            r1.append(r0)
            org.telegram.tgnet.TLRPC$InputFileLocation r0 = r8.location
            long r2 = r0.secret
            r1.append(r2)
            java.lang.String r0 = r1.toString()
            org.telegram.messenger.FileLog.e((java.lang.String) r0)
            goto L_0x06b0
        L_0x068b:
            org.telegram.tgnet.TLRPC$InputWebFileLocation r1 = r8.webLocation
            if (r1 == 0) goto L_0x06b0
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r0 = r0.text
            r1.append(r0)
            r1.append(r3)
            org.telegram.tgnet.TLRPC$InputWebFileLocation r0 = r8.webLocation
            r1.append(r0)
            r1.append(r15)
            java.lang.String r0 = r8.fileName
            r1.append(r0)
            java.lang.String r0 = r1.toString()
            org.telegram.messenger.FileLog.e((java.lang.String) r0)
        L_0x06b0:
            r1 = 0
            r8.onFail(r1, r1)
        L_0x06b4:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.FileLoadOperation.processRequestResult(org.telegram.messenger.FileLoadOperation$RequestInfo, org.telegram.tgnet.TLRPC$TL_error):boolean");
    }

    /* access modifiers changed from: protected */
    public void onFail(boolean z, int i) {
        cleanup();
        this.state = i == 1 ? 4 : 2;
        FileLoadOperationDelegate fileLoadOperationDelegate = this.delegate;
        if (fileLoadOperationDelegate == null) {
            return;
        }
        if (z) {
            Utilities.stageQueue.postRunnable(new FileLoadOperation$$ExternalSyntheticLambda1(this, i));
        } else {
            fileLoadOperationDelegate.didFailedLoadingFile(this, i);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onFail$10(int i) {
        this.delegate.didFailedLoadingFile(this, i);
    }

    private void clearOperaion(RequestInfo requestInfo, boolean z) {
        long j = Long.MAX_VALUE;
        for (int i = 0; i < this.requestInfos.size(); i++) {
            RequestInfo requestInfo2 = this.requestInfos.get(i);
            j = Math.min(requestInfo2.offset, j);
            if (this.isPreloadVideoOperation) {
                this.requestedPreloadedBytesRanges.remove(Long.valueOf(requestInfo2.offset));
            } else {
                removePart(this.notRequestedBytesRanges, requestInfo2.offset, ((long) this.currentDownloadChunkSize) + requestInfo2.offset);
            }
            if (!(requestInfo == requestInfo2 || requestInfo2.requestToken == 0)) {
                ConnectionsManager.getInstance(this.currentAccount).cancelRequest(requestInfo2.requestToken, true);
            }
        }
        this.requestInfos.clear();
        for (int i2 = 0; i2 < this.delayedRequestInfos.size(); i2++) {
            RequestInfo requestInfo3 = this.delayedRequestInfos.get(i2);
            if (this.isPreloadVideoOperation) {
                this.requestedPreloadedBytesRanges.remove(Long.valueOf(requestInfo3.offset));
            } else {
                removePart(this.notRequestedBytesRanges, requestInfo3.offset, ((long) this.currentDownloadChunkSize) + requestInfo3.offset);
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
            j = Math.min(requestInfo3.offset, j);
        }
        this.delayedRequestInfos.clear();
        this.requestsCount = 0;
        if (!z && this.isPreloadVideoOperation) {
            this.requestedBytesCount = (long) this.totalPreloadedBytes;
        } else if (this.notLoadedBytesRanges == null) {
            this.downloadedBytes = j;
            this.requestedBytesCount = j;
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

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v4, resolved type: org.telegram.tgnet.TLRPC$TL_upload_getWebFile} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v5, resolved type: org.telegram.tgnet.TLRPC$TL_upload_getFile} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v10, resolved type: org.telegram.tgnet.TLRPC$TL_upload_getCdnFile} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v32, resolved type: org.telegram.tgnet.TLRPC$TL_upload_getFile} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v33, resolved type: org.telegram.tgnet.TLRPC$TL_upload_getFile} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v34, resolved type: org.telegram.tgnet.TLRPC$TL_upload_getFile} */
    /* access modifiers changed from: protected */
    /* JADX WARNING: Code restructure failed: missing block: B:56:0x00c7, code lost:
        r0 = r1;
        r2 = false;
     */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void startDownloadRequest() {
        /*
            r29 = this;
            r7 = r29
            boolean r0 = r7.paused
            if (r0 != 0) goto L_0x032b
            boolean r0 = r7.reuploadingCdn
            if (r0 != 0) goto L_0x032b
            int r0 = r7.state
            r8 = 1
            if (r0 != r8) goto L_0x032b
            long r0 = r7.streamPriorityStartOffset
            r9 = 0
            int r2 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1))
            if (r2 != 0) goto L_0x0047
            boolean r0 = r7.nextPartWasPreloaded
            if (r0 != 0) goto L_0x002c
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$RequestInfo> r0 = r7.requestInfos
            int r0 = r0.size()
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$RequestInfo> r1 = r7.delayedRequestInfos
            int r1 = r1.size()
            int r0 = r0 + r1
            int r1 = r7.currentMaxDownloadRequests
            if (r0 >= r1) goto L_0x032b
        L_0x002c:
            boolean r0 = r7.isPreloadVideoOperation
            if (r0 == 0) goto L_0x0047
            long r0 = r7.requestedBytesCount
            r2 = 2097152(0x200000, double:1.0361308E-317)
            int r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r4 > 0) goto L_0x032b
            int r0 = r7.moovFound
            if (r0 == 0) goto L_0x0047
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$RequestInfo> r0 = r7.requestInfos
            int r0 = r0.size()
            if (r0 <= 0) goto L_0x0047
            goto L_0x032b
        L_0x0047:
            long r0 = r7.streamPriorityStartOffset
            r11 = 0
            int r2 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1))
            if (r2 != 0) goto L_0x006f
            boolean r0 = r7.nextPartWasPreloaded
            if (r0 != 0) goto L_0x006f
            boolean r0 = r7.isPreloadVideoOperation
            if (r0 == 0) goto L_0x005a
            int r0 = r7.moovFound
            if (r0 == 0) goto L_0x006f
        L_0x005a:
            long r0 = r7.totalBytesCount
            int r2 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1))
            if (r2 <= 0) goto L_0x006f
            int r0 = r7.currentMaxDownloadRequests
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$RequestInfo> r1 = r7.requestInfos
            int r1 = r1.size()
            int r0 = r0 - r1
            int r0 = java.lang.Math.max(r11, r0)
            r12 = r0
            goto L_0x0070
        L_0x006f:
            r12 = 1
        L_0x0070:
            r13 = 0
        L_0x0071:
            if (r13 >= r12) goto L_0x032b
            boolean r0 = r7.isPreloadVideoOperation
            r14 = 2
            if (r0 == 0) goto L_0x0127
            int r0 = r7.moovFound
            if (r0 == 0) goto L_0x0083
            long r0 = r7.preloadNotRequestedBytesCount
            int r2 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1))
            if (r2 > 0) goto L_0x0083
            return
        L_0x0083:
            long r0 = r7.nextPreloadDownloadOffset
            r2 = -1
            int r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r4 != 0) goto L_0x00d6
            r0 = 2097152(0x200000, float:2.938736E-39)
            int r1 = r7.currentDownloadChunkSize
            int r0 = r0 / r1
            int r0 = r0 + r14
            r1 = r9
        L_0x0092:
            if (r0 == 0) goto L_0x00c7
            java.util.HashMap<java.lang.Long, java.lang.Integer> r3 = r7.requestedPreloadedBytesRanges
            java.lang.Long r4 = java.lang.Long.valueOf(r1)
            boolean r3 = r3.containsKey(r4)
            if (r3 != 0) goto L_0x00a3
            r0 = r1
            r2 = 1
            goto L_0x00c9
        L_0x00a3:
            int r3 = r7.currentDownloadChunkSize
            long r4 = (long) r3
            long r1 = r1 + r4
            long r4 = r7.totalBytesCount
            int r6 = (r1 > r4 ? 1 : (r1 == r4 ? 0 : -1))
            if (r6 <= 0) goto L_0x00ae
            goto L_0x00c7
        L_0x00ae:
            int r6 = r7.moovFound
            if (r6 != r14) goto L_0x00c3
            int r6 = r3 * 8
            long r14 = (long) r6
            int r6 = (r1 > r14 ? 1 : (r1 == r14 ? 0 : -1))
            if (r6 != 0) goto L_0x00c3
            r1 = 1048576(0x100000, double:5.180654E-318)
            long r4 = r4 - r1
            long r1 = (long) r3
            long r4 = r4 / r1
            long r1 = (long) r3
            long r4 = r4 * r1
            r1 = r4
        L_0x00c3:
            int r0 = r0 + -1
            r14 = 2
            goto L_0x0092
        L_0x00c7:
            r0 = r1
            r2 = 0
        L_0x00c9:
            if (r2 != 0) goto L_0x00d6
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$RequestInfo> r2 = r7.requestInfos
            boolean r2 = r2.isEmpty()
            if (r2 == 0) goto L_0x00d6
            r7.onFinishLoadingFile(r11)
        L_0x00d6:
            java.util.HashMap<java.lang.Long, java.lang.Integer> r2 = r7.requestedPreloadedBytesRanges
            if (r2 != 0) goto L_0x00e1
            java.util.HashMap r2 = new java.util.HashMap
            r2.<init>()
            r7.requestedPreloadedBytesRanges = r2
        L_0x00e1:
            java.util.HashMap<java.lang.Long, java.lang.Integer> r2 = r7.requestedPreloadedBytesRanges
            java.lang.Long r3 = java.lang.Long.valueOf(r0)
            java.lang.Integer r4 = java.lang.Integer.valueOf(r8)
            r2.put(r3, r4)
            boolean r2 = org.telegram.messenger.BuildVars.DEBUG_VERSION
            if (r2 == 0) goto L_0x011a
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "start next preload from "
            r2.append(r3)
            r2.append(r0)
            java.lang.String r3 = " size "
            r2.append(r3)
            long r3 = r7.totalBytesCount
            r2.append(r3)
            java.lang.String r3 = " for "
            r2.append(r3)
            java.io.File r3 = r7.cacheFilePreload
            r2.append(r3)
            java.lang.String r2 = r2.toString()
            org.telegram.messenger.FileLog.d(r2)
        L_0x011a:
            long r2 = r7.preloadNotRequestedBytesCount
            int r4 = r7.currentDownloadChunkSize
            long r4 = (long) r4
            long r2 = r2 - r4
            r7.preloadNotRequestedBytesCount = r2
            r8 = r0
            r16 = r12
            goto L_0x0196
        L_0x0127:
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$Range> r0 = r7.notRequestedBytesRanges
            if (r0 == 0) goto L_0x0191
            long r1 = r7.streamPriorityStartOffset
            int r3 = (r1 > r9 ? 1 : (r1 == r9 ? 0 : -1))
            if (r3 == 0) goto L_0x0132
            goto L_0x0134
        L_0x0132:
            long r1 = r7.streamStartOffset
        L_0x0134:
            int r0 = r0.size()
            r3 = 9223372036854775807(0x7fffffffffffffff, double:NaN)
            r14 = r3
            r16 = r12
            r5 = 0
            r11 = r14
        L_0x0142:
            if (r5 >= r0) goto L_0x0184
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$Range> r6 = r7.notRequestedBytesRanges
            java.lang.Object r6 = r6.get(r5)
            org.telegram.messenger.FileLoadOperation$Range r6 = (org.telegram.messenger.FileLoadOperation.Range) r6
            int r17 = (r1 > r9 ? 1 : (r1 == r9 ? 0 : -1))
            if (r17 == 0) goto L_0x0176
            long r17 = r6.start
            int r19 = (r17 > r1 ? 1 : (r17 == r1 ? 0 : -1))
            if (r19 > 0) goto L_0x0162
            long r17 = r6.end
            int r19 = (r17 > r1 ? 1 : (r17 == r1 ? 0 : -1))
            if (r19 <= 0) goto L_0x0162
            r11 = r3
            goto L_0x0185
        L_0x0162:
            long r17 = r6.start
            int r19 = (r1 > r17 ? 1 : (r1 == r17 ? 0 : -1))
            if (r19 >= 0) goto L_0x0176
            long r17 = r6.start
            int r19 = (r17 > r14 ? 1 : (r17 == r14 ? 0 : -1))
            if (r19 >= 0) goto L_0x0176
            long r14 = r6.start
        L_0x0176:
            long r8 = r6.start
            long r11 = java.lang.Math.min(r11, r8)
            int r5 = r5 + 1
            r8 = 1
            r9 = 0
            goto L_0x0142
        L_0x0184:
            r1 = r14
        L_0x0185:
            int r0 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r0 == 0) goto L_0x018b
            r0 = r1
            goto L_0x0195
        L_0x018b:
            int r0 = (r11 > r3 ? 1 : (r11 == r3 ? 0 : -1))
            if (r0 == 0) goto L_0x032b
            r0 = r11
            goto L_0x0195
        L_0x0191:
            r16 = r12
            long r0 = r7.requestedBytesCount
        L_0x0195:
            r8 = r0
        L_0x0196:
            boolean r0 = r7.isPreloadVideoOperation
            if (r0 != 0) goto L_0x01aa
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$Range> r1 = r7.notRequestedBytesRanges
            if (r1 == 0) goto L_0x01aa
            int r0 = r7.currentDownloadChunkSize
            long r2 = (long) r0
            long r4 = r8 + r2
            r6 = 0
            r0 = r29
            r2 = r8
            r0.addPart(r1, r2, r4, r6)
        L_0x01aa:
            long r0 = r7.totalBytesCount
            r2 = 0
            int r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r4 <= 0) goto L_0x01b8
            int r4 = (r8 > r0 ? 1 : (r8 == r0 ? 0 : -1))
            if (r4 < 0) goto L_0x01b8
            goto L_0x032b
        L_0x01b8:
            int r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r4 <= 0) goto L_0x01d0
            int r12 = r16 + -1
            if (r13 == r12) goto L_0x01d0
            int r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r4 <= 0) goto L_0x01cd
            int r2 = r7.currentDownloadChunkSize
            long r2 = (long) r2
            long r2 = r2 + r8
            int r4 = (r2 > r0 ? 1 : (r2 == r0 ? 0 : -1))
            if (r4 < 0) goto L_0x01cd
            goto L_0x01d0
        L_0x01cd:
            r28 = 0
            goto L_0x01d2
        L_0x01d0:
            r28 = 1
        L_0x01d2:
            int r0 = r7.requestsCount
            r1 = 2
            int r0 = r0 % r1
            if (r0 != 0) goto L_0x01db
            r27 = 2
            goto L_0x01e1
        L_0x01db:
            r14 = 65538(0x10002, float:9.1838E-41)
            r27 = 65538(0x10002, float:9.1838E-41)
        L_0x01e1:
            boolean r0 = r7.isForceRequest
            if (r0 == 0) goto L_0x01e8
            r0 = 32
            goto L_0x01e9
        L_0x01e8:
            r0 = 0
        L_0x01e9:
            boolean r1 = r7.isCdn
            if (r1 == 0) goto L_0x0201
            org.telegram.tgnet.TLRPC$TL_upload_getCdnFile r1 = new org.telegram.tgnet.TLRPC$TL_upload_getCdnFile
            r1.<init>()
            byte[] r2 = r7.cdnToken
            r1.file_token = r2
            r1.offset = r8
            int r2 = r7.currentDownloadChunkSize
            r1.limit = r2
            r0 = r0 | 1
        L_0x01fe:
            r25 = r0
            goto L_0x0229
        L_0x0201:
            org.telegram.tgnet.TLRPC$InputWebFileLocation r1 = r7.webLocation
            if (r1 == 0) goto L_0x0216
            org.telegram.tgnet.TLRPC$TL_upload_getWebFile r1 = new org.telegram.tgnet.TLRPC$TL_upload_getWebFile
            r1.<init>()
            org.telegram.tgnet.TLRPC$InputWebFileLocation r2 = r7.webLocation
            r1.location = r2
            int r2 = (int) r8
            r1.offset = r2
            int r2 = r7.currentDownloadChunkSize
            r1.limit = r2
            goto L_0x01fe
        L_0x0216:
            org.telegram.tgnet.TLRPC$TL_upload_getFile r1 = new org.telegram.tgnet.TLRPC$TL_upload_getFile
            r1.<init>()
            org.telegram.tgnet.TLRPC$InputFileLocation r2 = r7.location
            r1.location = r2
            r1.offset = r8
            int r2 = r7.currentDownloadChunkSize
            r1.limit = r2
            r2 = 1
            r1.cdn_supported = r2
            goto L_0x01fe
        L_0x0229:
            long r2 = r7.requestedBytesCount
            int r0 = r7.currentDownloadChunkSize
            long r4 = (long) r0
            long r2 = r2 + r4
            r7.requestedBytesCount = r2
            org.telegram.messenger.FileLoadOperation$RequestInfo r0 = new org.telegram.messenger.FileLoadOperation$RequestInfo
            r0.<init>()
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$RequestInfo> r2 = r7.requestInfos
            r2.add(r0)
            long unused = r0.offset = r8
            boolean r2 = r7.isPreloadVideoOperation
            if (r2 != 0) goto L_0x02b8
            boolean r2 = r7.supportsPreloading
            if (r2 == 0) goto L_0x02b8
            java.io.RandomAccessFile r2 = r7.preloadStream
            if (r2 == 0) goto L_0x02b8
            java.util.HashMap<java.lang.Long, org.telegram.messenger.FileLoadOperation$PreloadRange> r2 = r7.preloadedBytesRanges
            if (r2 == 0) goto L_0x02b8
            long r3 = r0.offset
            java.lang.Long r3 = java.lang.Long.valueOf(r3)
            java.lang.Object r2 = r2.get(r3)
            org.telegram.messenger.FileLoadOperation$PreloadRange r2 = (org.telegram.messenger.FileLoadOperation.PreloadRange) r2
            if (r2 == 0) goto L_0x02b8
            org.telegram.tgnet.TLRPC$TL_upload_file r3 = new org.telegram.tgnet.TLRPC$TL_upload_file
            r3.<init>()
            org.telegram.tgnet.TLRPC$TL_upload_file unused = r0.response = r3
            boolean r3 = org.telegram.messenger.BuildVars.DEBUG_VERSION     // Catch:{ Exception -> 0x02b8 }
            if (r3 == 0) goto L_0x027e
            long r3 = r2.length     // Catch:{ Exception -> 0x02b8 }
            r5 = 2147483647(0x7fffffff, double:1.060997895E-314)
            int r8 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r8 > 0) goto L_0x0276
            goto L_0x027e
        L_0x0276:
            java.lang.RuntimeException r2 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x02b8 }
            java.lang.String r3 = "cast long to integer"
            r2.<init>(r3)     // Catch:{ Exception -> 0x02b8 }
            throw r2     // Catch:{ Exception -> 0x02b8 }
        L_0x027e:
            org.telegram.tgnet.NativeByteBuffer r3 = new org.telegram.tgnet.NativeByteBuffer     // Catch:{ Exception -> 0x02b8 }
            long r4 = r2.length     // Catch:{ Exception -> 0x02b8 }
            int r5 = (int) r4     // Catch:{ Exception -> 0x02b8 }
            r3.<init>((int) r5)     // Catch:{ Exception -> 0x02b8 }
            java.io.RandomAccessFile r4 = r7.preloadStream     // Catch:{ Exception -> 0x02b8 }
            long r5 = r2.fileOffset     // Catch:{ Exception -> 0x02b8 }
            r4.seek(r5)     // Catch:{ Exception -> 0x02b8 }
            java.io.RandomAccessFile r2 = r7.preloadStream     // Catch:{ Exception -> 0x02b8 }
            java.nio.channels.FileChannel r2 = r2.getChannel()     // Catch:{ Exception -> 0x02b8 }
            java.nio.ByteBuffer r4 = r3.buffer     // Catch:{ Exception -> 0x02b8 }
            r2.read(r4)     // Catch:{ Exception -> 0x02b8 }
            java.nio.ByteBuffer r2 = r3.buffer     // Catch:{ Exception -> 0x02b8 }
            r4 = 0
            r2.position(r4)     // Catch:{ Exception -> 0x02b6 }
            org.telegram.tgnet.TLRPC$TL_upload_file r2 = r0.response     // Catch:{ Exception -> 0x02b6 }
            r2.bytes = r3     // Catch:{ Exception -> 0x02b6 }
            org.telegram.messenger.DispatchQueue r2 = org.telegram.messenger.Utilities.stageQueue     // Catch:{ Exception -> 0x02b6 }
            org.telegram.messenger.FileLoadOperation$$ExternalSyntheticLambda2 r3 = new org.telegram.messenger.FileLoadOperation$$ExternalSyntheticLambda2     // Catch:{ Exception -> 0x02b6 }
            r3.<init>(r7, r0)     // Catch:{ Exception -> 0x02b6 }
            r2.postRunnable(r3)     // Catch:{ Exception -> 0x02b6 }
            r1 = 1
            r2 = 0
            goto L_0x0322
        L_0x02b6:
            goto L_0x02b9
        L_0x02b8:
            r4 = 0
        L_0x02b9:
            long r2 = r7.streamPriorityStartOffset
            r5 = 0
            int r8 = (r2 > r5 ? 1 : (r2 == r5 ? 0 : -1))
            if (r8 == 0) goto L_0x02e2
            boolean r2 = org.telegram.messenger.BuildVars.DEBUG_VERSION
            if (r2 == 0) goto L_0x02db
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "frame get offset = "
            r2.append(r3)
            long r5 = r7.streamPriorityStartOffset
            r2.append(r5)
            java.lang.String r2 = r2.toString()
            org.telegram.messenger.FileLog.d(r2)
        L_0x02db:
            r2 = 0
            r7.streamPriorityStartOffset = r2
            r7.priorityRequestInfo = r0
            goto L_0x02e4
        L_0x02e2:
            r2 = 0
        L_0x02e4:
            org.telegram.tgnet.TLRPC$InputFileLocation r5 = r7.location
            boolean r6 = r5 instanceof org.telegram.tgnet.TLRPC$TL_inputPeerPhotoFileLocation
            if (r6 == 0) goto L_0x02f7
            org.telegram.tgnet.TLRPC$TL_inputPeerPhotoFileLocation r5 = (org.telegram.tgnet.TLRPC$TL_inputPeerPhotoFileLocation) r5
            long r5 = r5.photo_id
            int r8 = (r5 > r2 ? 1 : (r5 == r2 ? 0 : -1))
            if (r8 != 0) goto L_0x02f7
            r7.requestReference(r0)
            r1 = 1
            goto L_0x0322
        L_0x02f7:
            int r5 = r7.currentAccount
            org.telegram.tgnet.ConnectionsManager r20 = org.telegram.tgnet.ConnectionsManager.getInstance(r5)
            org.telegram.messenger.FileLoadOperation$$ExternalSyntheticLambda14 r5 = new org.telegram.messenger.FileLoadOperation$$ExternalSyntheticLambda14
            r5.<init>(r7, r0, r1)
            r23 = 0
            r24 = 0
            boolean r6 = r7.isCdn
            if (r6 == 0) goto L_0x030d
            int r6 = r7.cdnDatacenterId
            goto L_0x030f
        L_0x030d:
            int r6 = r7.datacenterId
        L_0x030f:
            r26 = r6
            r21 = r1
            r22 = r5
            int r1 = r20.sendRequest(r21, r22, r23, r24, r25, r26, r27, r28)
            int unused = r0.requestToken = r1
            int r0 = r7.requestsCount
            r1 = 1
            int r0 = r0 + r1
            r7.requestsCount = r0
        L_0x0322:
            int r13 = r13 + 1
            r9 = r2
            r12 = r16
            r8 = 1
            r11 = 0
            goto L_0x0071
        L_0x032b:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.FileLoadOperation.startDownloadRequest():void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$startDownloadRequest$11(RequestInfo requestInfo) {
        processRequestResult(requestInfo, (TLRPC$TL_error) null);
        requestInfo.response.freeResources();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$startDownloadRequest$13(RequestInfo requestInfo, TLObject tLObject, TLObject tLObject2, TLRPC$TL_error tLRPC$TL_error) {
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
                        this.cdnHashes = new HashMap<>();
                    }
                    for (int i = 0; i < tLRPC$TL_upload_fileCdnRedirect.file_hashes.size(); i++) {
                        TLRPC$TL_fileHash tLRPC$TL_fileHash = tLRPC$TL_upload_fileCdnRedirect.file_hashes.get(i);
                        this.cdnHashes.put(Long.valueOf(tLRPC$TL_fileHash.offset), tLRPC$TL_fileHash);
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
                    arrayList.add(new Range(0, (long) this.maxCdnParts));
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
                        this.totalBytesCount = (long) requestInfo.responseWeb.size;
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
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_upload_reuploadCdnFile, new FileLoadOperation$$ExternalSyntheticLambda13(this, requestInfo), (QuickAckDelegate) null, (WriteToSocketDelegate) null, 0, this.datacenterId, 1, true);
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$startDownloadRequest$12(RequestInfo requestInfo, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.reuploadingCdn = false;
        if (tLRPC$TL_error == null) {
            TLRPC$Vector tLRPC$Vector = (TLRPC$Vector) tLObject;
            if (!tLRPC$Vector.objects.isEmpty()) {
                if (this.cdnHashes == null) {
                    this.cdnHashes = new HashMap<>();
                }
                for (int i = 0; i < tLRPC$Vector.objects.size(); i++) {
                    TLRPC$TL_fileHash tLRPC$TL_fileHash = (TLRPC$TL_fileHash) tLRPC$Vector.objects.get(i);
                    this.cdnHashes.put(Long.valueOf(tLRPC$TL_fileHash.offset), tLRPC$TL_fileHash);
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
