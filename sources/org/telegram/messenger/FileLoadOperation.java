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
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.WriteToSocketDelegate;

public class FileLoadOperation {
    private static final int preloadMaxBytes = 2097152;
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
    private HashMap<Long, TLRPC.TL_fileHash> cdnHashes;
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
    private boolean forceBig;
    private long foundMoovSize;
    private int initialDatacenterId;
    private boolean isCdn;
    private boolean isForceRequest;
    private boolean isPreloadVideoOperation;
    private byte[] iv;
    private byte[] key;
    protected long lastProgressUpdateTime;
    protected TLRPC.InputFileLocation location;
    private int maxCdnParts;
    private int maxDownloadRequests;
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
    private TLRPC.InputWebFileLocation webLocation;

    public interface FileLoadOperationDelegate {
        void didChangedLoadProgress(FileLoadOperation fileLoadOperation, long j, long j2);

        void didFailedLoadingFile(FileLoadOperation fileLoadOperation, int i);

        void didFinishLoadingFile(FileLoadOperation fileLoadOperation, File file);

        void saveFilePath(FilePathDatabase.PathData pathData, File file);
    }

    protected static class RequestInfo {
        /* access modifiers changed from: private */
        public long offset;
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
        public long end;
        /* access modifiers changed from: private */
        public long start;

        private Range(long s, long e) {
            this.start = s;
            this.end = e;
        }
    }

    private static class PreloadRange {
        /* access modifiers changed from: private */
        public long fileOffset;
        /* access modifiers changed from: private */
        public long length;

        private PreloadRange(long o, long l) {
            this.fileOffset = o;
            this.length = l;
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

    public FileLoadOperation(ImageLocation imageLocation, Object parent, String extension, long size) {
        this.downloadChunkSize = 32768;
        this.downloadChunkSizeBig = 131072;
        this.cdnChunkCheckSize = 131072;
        this.maxDownloadRequests = 4;
        this.maxDownloadRequestsBig = 4;
        this.bigFileSizeFrom = 1048576;
        this.maxCdnParts = (int) (NUM / ((long) 131072));
        this.preloadTempBuffer = new byte[24];
        boolean z = false;
        this.state = 0;
        updateParams();
        this.parentObject = parent;
        this.forceBig = imageLocation.imageType == 2;
        if (imageLocation.isEncrypted()) {
            TLRPC.TL_inputEncryptedFileLocation tL_inputEncryptedFileLocation = new TLRPC.TL_inputEncryptedFileLocation();
            this.location = tL_inputEncryptedFileLocation;
            tL_inputEncryptedFileLocation.id = imageLocation.location.volume_id;
            this.location.volume_id = imageLocation.location.volume_id;
            this.location.local_id = imageLocation.location.local_id;
            this.location.access_hash = imageLocation.access_hash;
            this.iv = new byte[32];
            byte[] bArr = imageLocation.iv;
            byte[] bArr2 = this.iv;
            System.arraycopy(bArr, 0, bArr2, 0, bArr2.length);
            this.key = imageLocation.key;
        } else if (imageLocation.photoPeer != null) {
            TLRPC.TL_inputPeerPhotoFileLocation inputPeerPhotoFileLocation = new TLRPC.TL_inputPeerPhotoFileLocation();
            inputPeerPhotoFileLocation.id = imageLocation.location.volume_id;
            inputPeerPhotoFileLocation.volume_id = imageLocation.location.volume_id;
            inputPeerPhotoFileLocation.local_id = imageLocation.location.local_id;
            inputPeerPhotoFileLocation.photo_id = imageLocation.photoId;
            inputPeerPhotoFileLocation.big = imageLocation.photoPeerType == 0;
            inputPeerPhotoFileLocation.peer = imageLocation.photoPeer;
            this.location = inputPeerPhotoFileLocation;
        } else if (imageLocation.stickerSet != null) {
            TLRPC.TL_inputStickerSetThumb inputStickerSetThumb = new TLRPC.TL_inputStickerSetThumb();
            inputStickerSetThumb.id = imageLocation.location.volume_id;
            inputStickerSetThumb.volume_id = imageLocation.location.volume_id;
            inputStickerSetThumb.local_id = imageLocation.location.local_id;
            inputStickerSetThumb.thumb_version = imageLocation.thumbVersion;
            inputStickerSetThumb.stickerset = imageLocation.stickerSet;
            this.location = inputStickerSetThumb;
        } else if (imageLocation.thumbSize != null) {
            if (imageLocation.photoId != 0) {
                TLRPC.TL_inputPhotoFileLocation tL_inputPhotoFileLocation = new TLRPC.TL_inputPhotoFileLocation();
                this.location = tL_inputPhotoFileLocation;
                tL_inputPhotoFileLocation.id = imageLocation.photoId;
                this.location.volume_id = imageLocation.location.volume_id;
                this.location.local_id = imageLocation.location.local_id;
                this.location.access_hash = imageLocation.access_hash;
                this.location.file_reference = imageLocation.file_reference;
                this.location.thumb_size = imageLocation.thumbSize;
                if (imageLocation.imageType == 2) {
                    this.allowDisordererFileSave = true;
                }
            } else {
                TLRPC.TL_inputDocumentFileLocation tL_inputDocumentFileLocation = new TLRPC.TL_inputDocumentFileLocation();
                this.location = tL_inputDocumentFileLocation;
                tL_inputDocumentFileLocation.id = imageLocation.documentId;
                this.location.volume_id = imageLocation.location.volume_id;
                this.location.local_id = imageLocation.location.local_id;
                this.location.access_hash = imageLocation.access_hash;
                this.location.file_reference = imageLocation.file_reference;
                this.location.thumb_size = imageLocation.thumbSize;
            }
            if (this.location.file_reference == null) {
                this.location.file_reference = new byte[0];
            }
        } else {
            TLRPC.TL_inputFileLocation tL_inputFileLocation = new TLRPC.TL_inputFileLocation();
            this.location = tL_inputFileLocation;
            tL_inputFileLocation.volume_id = imageLocation.location.volume_id;
            this.location.local_id = imageLocation.location.local_id;
            this.location.secret = imageLocation.access_hash;
            this.location.file_reference = imageLocation.file_reference;
            if (this.location.file_reference == null) {
                this.location.file_reference = new byte[0];
            }
            this.allowDisordererFileSave = true;
        }
        this.ungzip = (imageLocation.imageType == 1 || imageLocation.imageType == 3) ? true : z;
        int i = imageLocation.dc_id;
        this.datacenterId = i;
        this.initialDatacenterId = i;
        this.currentType = 16777216;
        this.totalBytesCount = size;
        this.ext = extension != null ? extension : "jpg";
    }

    public FileLoadOperation(SecureDocument secureDocument) {
        this.downloadChunkSize = 32768;
        this.downloadChunkSizeBig = 131072;
        this.cdnChunkCheckSize = 131072;
        this.maxDownloadRequests = 4;
        this.maxDownloadRequestsBig = 4;
        this.bigFileSizeFrom = 1048576;
        this.maxCdnParts = (int) (NUM / ((long) 131072));
        this.preloadTempBuffer = new byte[24];
        this.state = 0;
        updateParams();
        TLRPC.TL_inputSecureFileLocation tL_inputSecureFileLocation = new TLRPC.TL_inputSecureFileLocation();
        this.location = tL_inputSecureFileLocation;
        tL_inputSecureFileLocation.id = secureDocument.secureFile.id;
        this.location.access_hash = secureDocument.secureFile.access_hash;
        this.datacenterId = secureDocument.secureFile.dc_id;
        this.totalBytesCount = secureDocument.secureFile.size;
        this.allowDisordererFileSave = true;
        this.currentType = 67108864;
        this.ext = ".jpg";
    }

    public FileLoadOperation(int instance, WebFile webDocument) {
        this.downloadChunkSize = 32768;
        this.downloadChunkSizeBig = 131072;
        this.cdnChunkCheckSize = 131072;
        this.maxDownloadRequests = 4;
        this.maxDownloadRequestsBig = 4;
        this.bigFileSizeFrom = 1048576;
        this.maxCdnParts = (int) (NUM / ((long) 131072));
        this.preloadTempBuffer = new byte[24];
        this.state = 0;
        updateParams();
        this.currentAccount = instance;
        this.webFile = webDocument;
        this.webLocation = webDocument.location;
        this.totalBytesCount = (long) webDocument.size;
        int i = MessagesController.getInstance(this.currentAccount).webFileDatacenterId;
        this.datacenterId = i;
        this.initialDatacenterId = i;
        String defaultExt = FileLoader.getMimeTypePart(webDocument.mime_type);
        if (webDocument.mime_type.startsWith("image/")) {
            this.currentType = 16777216;
        } else if (webDocument.mime_type.equals("audio/ogg")) {
            this.currentType = 50331648;
        } else if (webDocument.mime_type.startsWith("video/")) {
            this.currentType = 33554432;
        } else {
            this.currentType = 67108864;
        }
        this.allowDisordererFileSave = true;
        this.ext = ImageLoader.getHttpUrlExtension(webDocument.url, defaultExt);
    }

    /* JADX WARNING: Removed duplicated region for block: B:32:0x00e7 A[Catch:{ Exception -> 0x012e }] */
    /* JADX WARNING: Removed duplicated region for block: B:39:0x0107 A[Catch:{ Exception -> 0x012e }] */
    /* JADX WARNING: Removed duplicated region for block: B:40:0x010c A[Catch:{ Exception -> 0x012e }] */
    /* JADX WARNING: Removed duplicated region for block: B:46:0x0125 A[Catch:{ Exception -> 0x012e }] */
    /* JADX WARNING: Removed duplicated region for block: B:53:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public FileLoadOperation(org.telegram.tgnet.TLRPC.Document r14, java.lang.Object r15) {
        /*
            r13 = this;
            r13.<init>()
            r0 = 32768(0x8000, float:4.5918E-41)
            r13.downloadChunkSize = r0
            r0 = 131072(0x20000, float:1.83671E-40)
            r13.downloadChunkSizeBig = r0
            r13.cdnChunkCheckSize = r0
            r1 = 4
            r13.maxDownloadRequests = r1
            r13.maxDownloadRequestsBig = r1
            r1 = 1048576(0x100000, float:1.469368E-39)
            r13.bigFileSizeFrom = r1
            long r0 = (long) r0
            r2 = 2097152000(0x7d000000, double:1.0361307573E-314)
            long r2 = r2 / r0
            int r0 = (int) r2
            r13.maxCdnParts = r0
            r0 = 24
            byte[] r0 = new byte[r0]
            r13.preloadTempBuffer = r0
            r0 = 0
            r13.state = r0
            r13.updateParams()
            r1 = 1
            r13.parentObject = r15     // Catch:{ Exception -> 0x012e }
            boolean r2 = r14 instanceof org.telegram.tgnet.TLRPC.TL_documentEncrypted     // Catch:{ Exception -> 0x012e }
            java.lang.String r3 = ""
            if (r2 == 0) goto L_0x005e
            org.telegram.tgnet.TLRPC$TL_inputEncryptedFileLocation r2 = new org.telegram.tgnet.TLRPC$TL_inputEncryptedFileLocation     // Catch:{ Exception -> 0x012e }
            r2.<init>()     // Catch:{ Exception -> 0x012e }
            r13.location = r2     // Catch:{ Exception -> 0x012e }
            long r4 = r14.id     // Catch:{ Exception -> 0x012e }
            r2.id = r4     // Catch:{ Exception -> 0x012e }
            org.telegram.tgnet.TLRPC$InputFileLocation r2 = r13.location     // Catch:{ Exception -> 0x012e }
            long r4 = r14.access_hash     // Catch:{ Exception -> 0x012e }
            r2.access_hash = r4     // Catch:{ Exception -> 0x012e }
            int r2 = r14.dc_id     // Catch:{ Exception -> 0x012e }
            r13.datacenterId = r2     // Catch:{ Exception -> 0x012e }
            r13.initialDatacenterId = r2     // Catch:{ Exception -> 0x012e }
            r2 = 32
            byte[] r2 = new byte[r2]     // Catch:{ Exception -> 0x012e }
            r13.iv = r2     // Catch:{ Exception -> 0x012e }
            byte[] r2 = r14.iv     // Catch:{ Exception -> 0x012e }
            byte[] r4 = r13.iv     // Catch:{ Exception -> 0x012e }
            int r5 = r4.length     // Catch:{ Exception -> 0x012e }
            java.lang.System.arraycopy(r2, r0, r4, r0, r5)     // Catch:{ Exception -> 0x012e }
            byte[] r2 = r14.key     // Catch:{ Exception -> 0x012e }
            r13.key = r2     // Catch:{ Exception -> 0x012e }
            goto L_0x00aa
        L_0x005e:
            boolean r2 = r14 instanceof org.telegram.tgnet.TLRPC.TL_document     // Catch:{ Exception -> 0x012e }
            if (r2 == 0) goto L_0x00aa
            org.telegram.tgnet.TLRPC$TL_inputDocumentFileLocation r2 = new org.telegram.tgnet.TLRPC$TL_inputDocumentFileLocation     // Catch:{ Exception -> 0x012e }
            r2.<init>()     // Catch:{ Exception -> 0x012e }
            r13.location = r2     // Catch:{ Exception -> 0x012e }
            long r4 = r14.id     // Catch:{ Exception -> 0x012e }
            r2.id = r4     // Catch:{ Exception -> 0x012e }
            org.telegram.tgnet.TLRPC$InputFileLocation r2 = r13.location     // Catch:{ Exception -> 0x012e }
            long r4 = r14.access_hash     // Catch:{ Exception -> 0x012e }
            r2.access_hash = r4     // Catch:{ Exception -> 0x012e }
            org.telegram.tgnet.TLRPC$InputFileLocation r2 = r13.location     // Catch:{ Exception -> 0x012e }
            byte[] r4 = r14.file_reference     // Catch:{ Exception -> 0x012e }
            r2.file_reference = r4     // Catch:{ Exception -> 0x012e }
            org.telegram.tgnet.TLRPC$InputFileLocation r2 = r13.location     // Catch:{ Exception -> 0x012e }
            r2.thumb_size = r3     // Catch:{ Exception -> 0x012e }
            org.telegram.tgnet.TLRPC$InputFileLocation r2 = r13.location     // Catch:{ Exception -> 0x012e }
            byte[] r2 = r2.file_reference     // Catch:{ Exception -> 0x012e }
            if (r2 != 0) goto L_0x0089
            org.telegram.tgnet.TLRPC$InputFileLocation r2 = r13.location     // Catch:{ Exception -> 0x012e }
            byte[] r4 = new byte[r0]     // Catch:{ Exception -> 0x012e }
            r2.file_reference = r4     // Catch:{ Exception -> 0x012e }
        L_0x0089:
            int r2 = r14.dc_id     // Catch:{ Exception -> 0x012e }
            r13.datacenterId = r2     // Catch:{ Exception -> 0x012e }
            r13.initialDatacenterId = r2     // Catch:{ Exception -> 0x012e }
            r13.allowDisordererFileSave = r1     // Catch:{ Exception -> 0x012e }
            r2 = 0
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r4 = r14.attributes     // Catch:{ Exception -> 0x012e }
            int r4 = r4.size()     // Catch:{ Exception -> 0x012e }
        L_0x0098:
            if (r2 >= r4) goto L_0x00aa
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r5 = r14.attributes     // Catch:{ Exception -> 0x012e }
            java.lang.Object r5 = r5.get(r2)     // Catch:{ Exception -> 0x012e }
            boolean r5 = r5 instanceof org.telegram.tgnet.TLRPC.TL_documentAttributeVideo     // Catch:{ Exception -> 0x012e }
            if (r5 == 0) goto L_0x00a7
            r13.supportsPreloading = r1     // Catch:{ Exception -> 0x012e }
            goto L_0x00aa
        L_0x00a7:
            int r2 = r2 + 1
            goto L_0x0098
        L_0x00aa:
            java.lang.String r2 = "application/x-tgsticker"
            java.lang.String r4 = r14.mime_type     // Catch:{ Exception -> 0x012e }
            boolean r2 = r2.equals(r4)     // Catch:{ Exception -> 0x012e }
            if (r2 != 0) goto L_0x00c1
            java.lang.String r2 = "application/x-tgwallpattern"
            java.lang.String r4 = r14.mime_type     // Catch:{ Exception -> 0x012e }
            boolean r2 = r2.equals(r4)     // Catch:{ Exception -> 0x012e }
            if (r2 == 0) goto L_0x00bf
            goto L_0x00c1
        L_0x00bf:
            r2 = 0
            goto L_0x00c2
        L_0x00c1:
            r2 = 1
        L_0x00c2:
            r13.ungzip = r2     // Catch:{ Exception -> 0x012e }
            long r4 = r14.size     // Catch:{ Exception -> 0x012e }
            r13.totalBytesCount = r4     // Catch:{ Exception -> 0x012e }
            byte[] r2 = r13.key     // Catch:{ Exception -> 0x012e }
            if (r2 == 0) goto L_0x00df
            r2 = 0
            r6 = 16
            long r8 = r4 % r6
            r10 = 0
            int r12 = (r8 > r10 ? 1 : (r8 == r10 ? 0 : -1))
            if (r12 == 0) goto L_0x00df
            long r8 = r4 % r6
            long r6 = r6 - r8
            r13.bytesCountPadding = r6     // Catch:{ Exception -> 0x012e }
            long r4 = r4 + r6
            r13.totalBytesCount = r4     // Catch:{ Exception -> 0x012e }
        L_0x00df:
            java.lang.String r2 = org.telegram.messenger.FileLoader.getDocumentFileName(r14)     // Catch:{ Exception -> 0x012e }
            r13.ext = r2     // Catch:{ Exception -> 0x012e }
            if (r2 == 0) goto L_0x00fb
            r4 = 46
            int r2 = r2.lastIndexOf(r4)     // Catch:{ Exception -> 0x012e }
            r4 = r2
            r5 = -1
            if (r2 != r5) goto L_0x00f2
            goto L_0x00fb
        L_0x00f2:
            java.lang.String r2 = r13.ext     // Catch:{ Exception -> 0x012e }
            java.lang.String r2 = r2.substring(r4)     // Catch:{ Exception -> 0x012e }
            r13.ext = r2     // Catch:{ Exception -> 0x012e }
            goto L_0x00fd
        L_0x00fb:
            r13.ext = r3     // Catch:{ Exception -> 0x012e }
        L_0x00fd:
            java.lang.String r2 = "audio/ogg"
            java.lang.String r3 = r14.mime_type     // Catch:{ Exception -> 0x012e }
            boolean r2 = r2.equals(r3)     // Catch:{ Exception -> 0x012e }
            if (r2 == 0) goto L_0x010c
            r2 = 50331648(0x3000000, float:3.761582E-37)
            r13.currentType = r2     // Catch:{ Exception -> 0x012e }
            goto L_0x011d
        L_0x010c:
            java.lang.String r2 = r14.mime_type     // Catch:{ Exception -> 0x012e }
            boolean r2 = org.telegram.messenger.FileLoader.isVideoMimeType(r2)     // Catch:{ Exception -> 0x012e }
            if (r2 == 0) goto L_0x0119
            r2 = 33554432(0x2000000, float:9.403955E-38)
            r13.currentType = r2     // Catch:{ Exception -> 0x012e }
            goto L_0x011d
        L_0x0119:
            r2 = 67108864(0x4000000, float:1.5046328E-36)
            r13.currentType = r2     // Catch:{ Exception -> 0x012e }
        L_0x011d:
            java.lang.String r2 = r13.ext     // Catch:{ Exception -> 0x012e }
            int r2 = r2.length()     // Catch:{ Exception -> 0x012e }
            if (r2 > r1) goto L_0x012d
            java.lang.String r2 = r14.mime_type     // Catch:{ Exception -> 0x012e }
            java.lang.String r2 = org.telegram.messenger.FileLoader.getExtensionByMimeType(r2)     // Catch:{ Exception -> 0x012e }
            r13.ext = r2     // Catch:{ Exception -> 0x012e }
        L_0x012d:
            goto L_0x0135
        L_0x012e:
            r2 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)
            r13.onFail(r1, r0)
        L_0x0135:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.FileLoadOperation.<init>(org.telegram.tgnet.TLRPC$Document, java.lang.Object):void");
    }

    public void setEncryptFile(boolean value) {
        this.encryptFile = value;
        if (value) {
            this.allowDisordererFileSave = false;
        }
    }

    public int getDatacenterId() {
        return this.initialDatacenterId;
    }

    public void setForceRequest(boolean forceRequest) {
        this.isForceRequest = forceRequest;
    }

    public boolean isForceRequest() {
        return this.isForceRequest;
    }

    public void setPriority(int value) {
        this.priority = value;
    }

    public int getPriority() {
        return this.priority;
    }

    public void setPaths(int instance, String name, int queueType, File store, File temp, String finalName) {
        this.storePath = store;
        this.tempPath = temp;
        this.currentAccount = instance;
        this.fileName = name;
        this.storeFileName = finalName;
        this.currentQueueType = queueType;
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

    private void removePart(ArrayList<Range> ranges, long start, long end) {
        if (ranges != null && end >= start) {
            int count = ranges.size();
            boolean modified = false;
            int a = 0;
            while (true) {
                if (a >= count) {
                    break;
                }
                Range range = ranges.get(a);
                if (start == range.end) {
                    long unused = range.end = end;
                    modified = true;
                    break;
                } else if (end == range.start) {
                    long unused2 = range.start = start;
                    modified = true;
                    break;
                } else {
                    a++;
                }
            }
            Collections.sort(ranges, FileLoadOperation$$ExternalSyntheticLambda2.INSTANCE);
            int a2 = 0;
            while (a2 < ranges.size() - 1) {
                Range r1 = ranges.get(a2);
                Range r2 = ranges.get(a2 + 1);
                if (r1.end == r2.start) {
                    long unused3 = r1.end = r2.end;
                    ranges.remove(a2 + 1);
                    a2--;
                }
                a2++;
            }
            if (!modified) {
                ranges.add(new Range(start, end));
            }
        }
    }

    static /* synthetic */ int lambda$removePart$0(Range o1, Range o2) {
        if (o1.start > o2.start) {
            return 1;
        }
        if (o1.start < o2.start) {
            return -1;
        }
        return 0;
    }

    private void addPart(ArrayList<Range> ranges, long start, long end, boolean save) {
        boolean modified;
        ArrayList<Range> arrayList = ranges;
        long j = start;
        long j2 = end;
        if (arrayList != null && j2 >= j) {
            int count = ranges.size();
            int a = 0;
            while (true) {
                if (a >= count) {
                    modified = false;
                    break;
                }
                Range range = arrayList.get(a);
                if (j <= range.start) {
                    if (j2 >= range.end) {
                        arrayList.remove(a);
                        modified = true;
                        break;
                    } else if (j2 > range.start) {
                        long unused = range.start = j2;
                        modified = true;
                        break;
                    }
                } else if (j2 < range.end) {
                    arrayList.add(0, new Range(range.start, start));
                    long unused2 = range.start = j2;
                    modified = true;
                    break;
                } else if (j < range.end) {
                    long unused3 = range.end = j;
                    modified = true;
                    break;
                }
                a++;
            }
            if (!save) {
                return;
            }
            if (modified) {
                try {
                    this.filePartsStream.seek(0);
                    int count2 = ranges.size();
                    this.filePartsStream.writeInt(count2);
                    for (int a2 = 0; a2 < count2; a2++) {
                        Range range2 = arrayList.get(a2);
                        this.filePartsStream.writeLong(range2.start);
                        this.filePartsStream.writeLong(range2.end);
                    }
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
                notifyStreamListeners();
            } else if (BuildVars.LOGS_ENABLED) {
                FileLog.e(this.cacheFileFinal + " downloaded duplicate file part " + j + " - " + j2);
            }
        }
    }

    private void notifyStreamListeners() {
        ArrayList<FileLoadOperationStream> arrayList = this.streamListeners;
        if (arrayList != null) {
            int count = arrayList.size();
            for (int a = 0; a < count; a++) {
                this.streamListeners.get(a).newDataAvailable();
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
        File[] result = new File[1];
        Utilities.stageQueue.postRunnable(new FileLoadOperation$$ExternalSyntheticLambda14(this, result, countDownLatch));
        try {
            countDownLatch.await();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        return result[0];
    }

    /* renamed from: lambda$getCurrentFile$1$org-telegram-messenger-FileLoadOperation  reason: not valid java name */
    public /* synthetic */ void m1804lambda$getCurrentFile$1$orgtelegrammessengerFileLoadOperation(File[] result, CountDownLatch countDownLatch) {
        if (this.state == 3) {
            result[0] = this.cacheFileFinal;
        } else {
            result[0] = this.cacheFileTemp;
        }
        countDownLatch.countDown();
    }

    private long getDownloadedLengthFromOffsetInternal(ArrayList<Range> ranges, long offset, long length) {
        ArrayList<Range> arrayList = ranges;
        long j = length;
        if (arrayList != null && this.state != 3 && !ranges.isEmpty()) {
            int count = ranges.size();
            Range minRange = null;
            long availableLength = length;
            int a = 0;
            while (true) {
                if (a >= count) {
                    break;
                }
                Range range = arrayList.get(a);
                if (offset <= range.start && (minRange == null || range.start < minRange.start)) {
                    minRange = range;
                }
                if (range.start <= offset && range.end > offset) {
                    availableLength = 0;
                    break;
                }
                a++;
            }
            if (availableLength == 0) {
                return 0;
            }
            if (minRange != null) {
                return Math.min(j, minRange.start - offset);
            }
            return Math.min(j, Math.max(this.totalBytesCount - offset, 0));
        } else if (this.state == 3) {
            return j;
        } else {
            long j2 = this.downloadedBytes;
            if (j2 == 0) {
                return 0;
            }
            return Math.min(j, Math.max(j2 - offset, 0));
        }
    }

    /* access modifiers changed from: protected */
    public float getDownloadedLengthFromOffset(float progress) {
        ArrayList<Range> ranges = this.notLoadedBytesRangesCopy;
        long j = this.totalBytesCount;
        if (j == 0 || ranges == null) {
            return 0.0f;
        }
        return (((float) getDownloadedLengthFromOffsetInternal(ranges, (long) ((int) (((float) j) * progress)), j)) / ((float) this.totalBytesCount)) + progress;
    }

    /* access modifiers changed from: protected */
    public long[] getDownloadedLengthFromOffset(int offset, long length) {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        long[] result = new long[2];
        Utilities.stageQueue.postRunnable(new FileLoadOperation$$ExternalSyntheticLambda13(this, result, offset, length, countDownLatch));
        try {
            countDownLatch.await();
        } catch (Exception e) {
        }
        return result;
    }

    /* renamed from: lambda$getDownloadedLengthFromOffset$2$org-telegram-messenger-FileLoadOperation  reason: not valid java name */
    public /* synthetic */ void m1805xcb1e2f1b(long[] result, int offset, long length, CountDownLatch countDownLatch) {
        result[0] = getDownloadedLengthFromOffsetInternal(this.notLoadedBytesRanges, (long) offset, length);
        if (this.state == 3) {
            result[1] = 1;
        }
        countDownLatch.countDown();
    }

    public String getFileName() {
        return this.fileName;
    }

    /* access modifiers changed from: protected */
    public void removeStreamListener(FileLoadOperationStream operation) {
        Utilities.stageQueue.postRunnable(new FileLoadOperation$$ExternalSyntheticLambda8(this, operation));
    }

    /* renamed from: lambda$removeStreamListener$3$org-telegram-messenger-FileLoadOperation  reason: not valid java name */
    public /* synthetic */ void m1808xc3d0b3c8(FileLoadOperationStream operation) {
        ArrayList<FileLoadOperationStream> arrayList = this.streamListeners;
        if (arrayList != null) {
            arrayList.remove(operation);
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

    /* JADX WARNING: Code restructure failed: missing block: B:115:0x0415, code lost:
        if (r5 != r8.cacheFileFinal.length()) goto L_0x0417;
     */
    /* JADX WARNING: Removed duplicated region for block: B:119:0x0422  */
    /* JADX WARNING: Removed duplicated region for block: B:237:0x0716 A[SYNTHETIC, Splitter:B:237:0x0716] */
    /* JADX WARNING: Removed duplicated region for block: B:247:0x0746  */
    /* JADX WARNING: Removed duplicated region for block: B:264:0x07b1  */
    /* JADX WARNING: Removed duplicated region for block: B:267:0x07bb  */
    /* JADX WARNING: Removed duplicated region for block: B:280:0x0815  */
    /* JADX WARNING: Removed duplicated region for block: B:287:0x0840  */
    /* JADX WARNING: Removed duplicated region for block: B:293:0x086c  */
    /* JADX WARNING: Removed duplicated region for block: B:298:0x08a9  */
    /* JADX WARNING: Removed duplicated region for block: B:312:0x08fa  */
    /* JADX WARNING: Removed duplicated region for block: B:324:0x0925 A[Catch:{ Exception -> 0x092a }] */
    /* JADX WARNING: Removed duplicated region for block: B:330:0x0933  */
    /* JADX WARNING: Removed duplicated region for block: B:332:0x0938  */
    /* JADX WARNING: Removed duplicated region for block: B:333:0x0948  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean start(org.telegram.messenger.FileLoadOperationStream r40, long r41, boolean r43) {
        /*
            r39 = this;
            r8 = r39
            r39.updateParams()
            int r0 = r8.currentDownloadChunkSize
            if (r0 != 0) goto L_0x002f
            long r0 = r8.totalBytesCount
            int r2 = r8.bigFileSizeFrom
            long r3 = (long) r2
            int r5 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1))
            if (r5 >= 0) goto L_0x001a
            boolean r3 = r8.forceBig
            if (r3 == 0) goto L_0x0017
            goto L_0x001a
        L_0x0017:
            int r3 = r8.downloadChunkSize
            goto L_0x001c
        L_0x001a:
            int r3 = r8.downloadChunkSizeBig
        L_0x001c:
            r8.currentDownloadChunkSize = r3
            long r2 = (long) r2
            int r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r4 >= 0) goto L_0x002b
            boolean r0 = r8.forceBig
            if (r0 == 0) goto L_0x0028
            goto L_0x002b
        L_0x0028:
            int r0 = r8.maxDownloadRequests
            goto L_0x002d
        L_0x002b:
            int r0 = r8.maxDownloadRequestsBig
        L_0x002d:
            r8.currentMaxDownloadRequests = r0
        L_0x002f:
            int r0 = r8.state
            r9 = 1
            r10 = 0
            if (r0 == 0) goto L_0x0037
            r0 = 1
            goto L_0x0038
        L_0x0037:
            r0 = 0
        L_0x0038:
            r11 = r0
            boolean r12 = r8.paused
            r8.paused = r10
            if (r40 == 0) goto L_0x0054
            org.telegram.messenger.DispatchQueue r0 = org.telegram.messenger.Utilities.stageQueue
            org.telegram.messenger.FileLoadOperation$$ExternalSyntheticLambda12 r13 = new org.telegram.messenger.FileLoadOperation$$ExternalSyntheticLambda12
            r1 = r13
            r2 = r39
            r3 = r43
            r4 = r41
            r6 = r40
            r7 = r11
            r1.<init>(r2, r3, r4, r6, r7)
            r0.postRunnable(r13)
            goto L_0x0062
        L_0x0054:
            if (r12 == 0) goto L_0x0062
            if (r11 == 0) goto L_0x0062
            org.telegram.messenger.DispatchQueue r0 = org.telegram.messenger.Utilities.stageQueue
            org.telegram.messenger.FileLoadOperation$$ExternalSyntheticLambda0 r1 = new org.telegram.messenger.FileLoadOperation$$ExternalSyntheticLambda0
            r1.<init>(r8)
            r0.postRunnable(r1)
        L_0x0062:
            if (r11 == 0) goto L_0x0065
            return r12
        L_0x0065:
            org.telegram.tgnet.TLRPC$InputFileLocation r0 = r8.location
            if (r0 != 0) goto L_0x0071
            org.telegram.tgnet.TLRPC$InputWebFileLocation r0 = r8.webLocation
            if (r0 != 0) goto L_0x0071
            r8.onFail(r9, r10)
            return r10
        L_0x0071:
            int r0 = r8.currentDownloadChunkSize
            long r1 = (long) r0
            long r1 = r41 / r1
            long r3 = (long) r0
            long r1 = r1 * r3
            r8.streamStartOffset = r1
            boolean r1 = r8.allowDisordererFileSave
            r2 = 0
            if (r1 == 0) goto L_0x009a
            long r4 = r8.totalBytesCount
            int r1 = (r4 > r2 ? 1 : (r4 == r2 ? 0 : -1))
            if (r1 <= 0) goto L_0x009a
            long r0 = (long) r0
            int r6 = (r4 > r0 ? 1 : (r4 == r0 ? 0 : -1))
            if (r6 <= 0) goto L_0x009a
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r8.notLoadedBytesRanges = r0
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r8.notRequestedBytesRanges = r0
        L_0x009a:
            r0 = 0
            r1 = 0
            r4 = 0
            org.telegram.tgnet.TLRPC$InputWebFileLocation r5 = r8.webLocation
            java.lang.String r6 = "_64.iv.enc"
            java.lang.String r7 = "_64.iv"
            java.lang.String r13 = ".enc"
            java.lang.String r14 = ".temp.enc"
            java.lang.String r15 = ".temp"
            java.lang.String r9 = "."
            if (r5 == 0) goto L_0x012f
            org.telegram.messenger.WebFile r5 = r8.webFile
            java.lang.String r5 = r5.url
            java.lang.String r5 = org.telegram.messenger.Utilities.MD5(r5)
            boolean r10 = r8.encryptFile
            if (r10 == 0) goto L_0x00f3
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            r7.append(r5)
            r7.append(r14)
            java.lang.String r7 = r7.toString()
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            r10.append(r5)
            r10.append(r9)
            java.lang.String r9 = r8.ext
            r10.append(r9)
            r10.append(r13)
            java.lang.String r9 = r10.toString()
            byte[] r10 = r8.key
            if (r10 == 0) goto L_0x012c
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            r10.append(r5)
            r10.append(r6)
            java.lang.String r4 = r10.toString()
            goto L_0x012c
        L_0x00f3:
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            r6.append(r5)
            r6.append(r15)
            java.lang.String r6 = r6.toString()
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            r10.append(r5)
            r10.append(r9)
            java.lang.String r9 = r8.ext
            r10.append(r9)
            java.lang.String r9 = r10.toString()
            byte[] r10 = r8.key
            if (r10 == 0) goto L_0x012b
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            r10.append(r5)
            r10.append(r7)
            java.lang.String r4 = r10.toString()
            r7 = r6
            goto L_0x012c
        L_0x012b:
            r7 = r6
        L_0x012c:
            r3 = r0
            goto L_0x039c
        L_0x012f:
            org.telegram.tgnet.TLRPC$InputFileLocation r5 = r8.location
            long r2 = r5.volume_id
            java.lang.String r5 = "_64.pt"
            java.lang.String r10 = "_64.preload"
            r18 = r0
            java.lang.String r0 = "_"
            r16 = 0
            int r19 = (r2 > r16 ? 1 : (r2 == r16 ? 0 : -1))
            if (r19 == 0) goto L_0x0284
            org.telegram.tgnet.TLRPC$InputFileLocation r2 = r8.location
            int r2 = r2.local_id
            if (r2 == 0) goto L_0x0284
            int r2 = r8.datacenterId
            r3 = -2147483648(0xfffffffvar_, float:-0.0)
            if (r2 == r3) goto L_0x027c
            org.telegram.tgnet.TLRPC$InputFileLocation r2 = r8.location
            long r2 = r2.volume_id
            r19 = -2147483648(0xfffffffvar_, double:NaN)
            int r21 = (r2 > r19 ? 1 : (r2 == r19 ? 0 : -1))
            if (r21 == 0) goto L_0x027c
            int r2 = r8.datacenterId
            if (r2 != 0) goto L_0x0160
            r19 = r4
            goto L_0x027e
        L_0x0160:
            boolean r2 = r8.encryptFile
            if (r2 == 0) goto L_0x01d3
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            org.telegram.tgnet.TLRPC$InputFileLocation r3 = r8.location
            r19 = r4
            long r3 = r3.volume_id
            r2.append(r3)
            r2.append(r0)
            org.telegram.tgnet.TLRPC$InputFileLocation r3 = r8.location
            int r3 = r3.local_id
            r2.append(r3)
            r2.append(r14)
            java.lang.String r7 = r2.toString()
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            org.telegram.tgnet.TLRPC$InputFileLocation r3 = r8.location
            long r3 = r3.volume_id
            r2.append(r3)
            r2.append(r0)
            org.telegram.tgnet.TLRPC$InputFileLocation r3 = r8.location
            int r3 = r3.local_id
            r2.append(r3)
            r2.append(r9)
            java.lang.String r3 = r8.ext
            r2.append(r3)
            r2.append(r13)
            java.lang.String r9 = r2.toString()
            byte[] r2 = r8.key
            if (r2 == 0) goto L_0x01cd
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            org.telegram.tgnet.TLRPC$InputFileLocation r3 = r8.location
            long r3 = r3.volume_id
            r2.append(r3)
            r2.append(r0)
            org.telegram.tgnet.TLRPC$InputFileLocation r0 = r8.location
            int r0 = r0.local_id
            r2.append(r0)
            r2.append(r6)
            java.lang.String r4 = r2.toString()
            r3 = r18
            goto L_0x039c
        L_0x01cd:
            r3 = r18
            r4 = r19
            goto L_0x039c
        L_0x01d3:
            r19 = r4
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            org.telegram.tgnet.TLRPC$InputFileLocation r3 = r8.location
            long r3 = r3.volume_id
            r2.append(r3)
            r2.append(r0)
            org.telegram.tgnet.TLRPC$InputFileLocation r3 = r8.location
            int r3 = r3.local_id
            r2.append(r3)
            r2.append(r15)
            java.lang.String r2 = r2.toString()
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            org.telegram.tgnet.TLRPC$InputFileLocation r4 = r8.location
            long r13 = r4.volume_id
            r3.append(r13)
            r3.append(r0)
            org.telegram.tgnet.TLRPC$InputFileLocation r4 = r8.location
            int r4 = r4.local_id
            r3.append(r4)
            r3.append(r9)
            java.lang.String r4 = r8.ext
            r3.append(r4)
            java.lang.String r9 = r3.toString()
            byte[] r3 = r8.key
            if (r3 == 0) goto L_0x0236
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            org.telegram.tgnet.TLRPC$InputFileLocation r4 = r8.location
            long r13 = r4.volume_id
            r3.append(r13)
            r3.append(r0)
            org.telegram.tgnet.TLRPC$InputFileLocation r4 = r8.location
            int r4 = r4.local_id
            r3.append(r4)
            r3.append(r7)
            java.lang.String r4 = r3.toString()
            goto L_0x0238
        L_0x0236:
            r4 = r19
        L_0x0238:
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$Range> r3 = r8.notLoadedBytesRanges
            if (r3 == 0) goto L_0x025a
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            org.telegram.tgnet.TLRPC$InputFileLocation r6 = r8.location
            long r6 = r6.volume_id
            r3.append(r6)
            r3.append(r0)
            org.telegram.tgnet.TLRPC$InputFileLocation r6 = r8.location
            int r6 = r6.local_id
            r3.append(r6)
            r3.append(r5)
            java.lang.String r3 = r3.toString()
            goto L_0x025c
        L_0x025a:
            r3 = r18
        L_0x025c:
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            org.telegram.tgnet.TLRPC$InputFileLocation r6 = r8.location
            long r6 = r6.volume_id
            r5.append(r6)
            r5.append(r0)
            org.telegram.tgnet.TLRPC$InputFileLocation r0 = r8.location
            int r0 = r0.local_id
            r5.append(r0)
            r5.append(r10)
            java.lang.String r1 = r5.toString()
            r7 = r2
            goto L_0x039c
        L_0x027c:
            r19 = r4
        L_0x027e:
            r2 = 1
            r3 = 0
            r8.onFail(r2, r3)
            return r3
        L_0x0284:
            r19 = r4
            int r2 = r8.datacenterId
            if (r2 == 0) goto L_0x096e
            org.telegram.tgnet.TLRPC$InputFileLocation r2 = r8.location
            long r2 = r2.id
            r16 = 0
            int r4 = (r2 > r16 ? 1 : (r2 == r16 ? 0 : -1))
            if (r4 != 0) goto L_0x029c
            r26 = r11
            r27 = r12
            r2 = 1
            r5 = 0
            goto L_0x0974
        L_0x029c:
            boolean r2 = r8.encryptFile
            if (r2 == 0) goto L_0x0304
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            int r3 = r8.datacenterId
            r2.append(r3)
            r2.append(r0)
            org.telegram.tgnet.TLRPC$InputFileLocation r3 = r8.location
            long r3 = r3.id
            r2.append(r3)
            r2.append(r14)
            java.lang.String r7 = r2.toString()
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            int r3 = r8.datacenterId
            r2.append(r3)
            r2.append(r0)
            org.telegram.tgnet.TLRPC$InputFileLocation r3 = r8.location
            long r3 = r3.id
            r2.append(r3)
            java.lang.String r3 = r8.ext
            r2.append(r3)
            r2.append(r13)
            java.lang.String r9 = r2.toString()
            byte[] r2 = r8.key
            if (r2 == 0) goto L_0x02fe
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            int r3 = r8.datacenterId
            r2.append(r3)
            r2.append(r0)
            org.telegram.tgnet.TLRPC$InputFileLocation r0 = r8.location
            long r3 = r0.id
            r2.append(r3)
            r2.append(r6)
            java.lang.String r4 = r2.toString()
            r3 = r18
            goto L_0x039c
        L_0x02fe:
            r3 = r18
            r4 = r19
            goto L_0x039c
        L_0x0304:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            int r3 = r8.datacenterId
            r2.append(r3)
            r2.append(r0)
            org.telegram.tgnet.TLRPC$InputFileLocation r3 = r8.location
            long r3 = r3.id
            r2.append(r3)
            r2.append(r15)
            java.lang.String r2 = r2.toString()
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            int r4 = r8.datacenterId
            r3.append(r4)
            r3.append(r0)
            org.telegram.tgnet.TLRPC$InputFileLocation r4 = r8.location
            long r13 = r4.id
            r3.append(r13)
            java.lang.String r4 = r8.ext
            r3.append(r4)
            java.lang.String r9 = r3.toString()
            byte[] r3 = r8.key
            if (r3 == 0) goto L_0x035c
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            int r4 = r8.datacenterId
            r3.append(r4)
            r3.append(r0)
            org.telegram.tgnet.TLRPC$InputFileLocation r4 = r8.location
            long r13 = r4.id
            r3.append(r13)
            r3.append(r7)
            java.lang.String r4 = r3.toString()
            goto L_0x035e
        L_0x035c:
            r4 = r19
        L_0x035e:
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$Range> r3 = r8.notLoadedBytesRanges
            if (r3 == 0) goto L_0x037e
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            int r6 = r8.datacenterId
            r3.append(r6)
            r3.append(r0)
            org.telegram.tgnet.TLRPC$InputFileLocation r6 = r8.location
            long r6 = r6.id
            r3.append(r6)
            r3.append(r5)
            java.lang.String r3 = r3.toString()
            goto L_0x0380
        L_0x037e:
            r3 = r18
        L_0x0380:
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            int r6 = r8.datacenterId
            r5.append(r6)
            r5.append(r0)
            org.telegram.tgnet.TLRPC$InputFileLocation r0 = r8.location
            long r6 = r0.id
            r5.append(r6)
            r5.append(r10)
            java.lang.String r1 = r5.toString()
            r7 = r2
        L_0x039c:
            java.util.ArrayList r0 = new java.util.ArrayList
            int r2 = r8.currentMaxDownloadRequests
            r0.<init>(r2)
            r8.requestInfos = r0
            java.util.ArrayList r0 = new java.util.ArrayList
            int r2 = r8.currentMaxDownloadRequests
            r5 = 1
            int r2 = r2 - r5
            r0.<init>(r2)
            r8.delayedRequestInfos = r0
            r8.state = r5
            java.lang.Object r0 = r8.parentObject
            boolean r2 = r0 instanceof org.telegram.tgnet.TLRPC.TL_theme
            if (r2 == 0) goto L_0x03de
            org.telegram.tgnet.TLRPC$TL_theme r0 = (org.telegram.tgnet.TLRPC.TL_theme) r0
            java.io.File r2 = new java.io.File
            java.io.File r5 = org.telegram.messenger.ApplicationLoader.getFilesDirFixed()
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            java.lang.String r10 = "remote"
            r6.append(r10)
            long r13 = r0.id
            r6.append(r13)
            java.lang.String r10 = ".attheme"
            r6.append(r10)
            java.lang.String r6 = r6.toString()
            r2.<init>(r5, r6)
            r8.cacheFileFinal = r2
            goto L_0x03f7
        L_0x03de:
            boolean r0 = r8.encryptFile
            if (r0 != 0) goto L_0x03ee
            java.io.File r0 = new java.io.File
            java.io.File r2 = r8.storePath
            java.lang.String r5 = r8.storeFileName
            r0.<init>(r2, r5)
            r8.cacheFileFinal = r0
            goto L_0x03f7
        L_0x03ee:
            java.io.File r0 = new java.io.File
            java.io.File r2 = r8.storePath
            r0.<init>(r2, r9)
            r8.cacheFileFinal = r0
        L_0x03f7:
            java.io.File r0 = r8.cacheFileFinal
            boolean r0 = r0.exists()
            if (r0 == 0) goto L_0x041f
            java.lang.Object r2 = r8.parentObject
            boolean r2 = r2 instanceof org.telegram.tgnet.TLRPC.TL_theme
            if (r2 != 0) goto L_0x0417
            long r5 = r8.totalBytesCount
            r13 = 0
            int r2 = (r5 > r13 ? 1 : (r5 == r13 ? 0 : -1))
            if (r2 == 0) goto L_0x041f
            java.io.File r2 = r8.cacheFileFinal
            long r13 = r2.length()
            int r2 = (r5 > r13 ? 1 : (r5 == r13 ? 0 : -1))
            if (r2 == 0) goto L_0x041f
        L_0x0417:
            java.io.File r2 = r8.cacheFileFinal
            r2.delete()
            r0 = 0
            r2 = r0
            goto L_0x0420
        L_0x041f:
            r2 = r0
        L_0x0420:
            if (r2 != 0) goto L_0x0948
            java.io.File r0 = new java.io.File
            java.io.File r6 = r8.tempPath
            r0.<init>(r6, r7)
            r8.cacheFileTemp = r0
            boolean r0 = r8.ungzip
            if (r0 == 0) goto L_0x0449
            java.io.File r0 = new java.io.File
            java.io.File r6 = r8.tempPath
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            r10.append(r7)
            java.lang.String r13 = ".gz"
            r10.append(r13)
            java.lang.String r10 = r10.toString()
            r0.<init>(r6, r10)
            r8.cacheFileGzipTemp = r0
        L_0x0449:
            r6 = 0
            boolean r0 = r8.encryptFile
            java.lang.String r10 = "rws"
            if (r0 == 0) goto L_0x04ca
            java.io.File r0 = new java.io.File
            java.io.File r13 = org.telegram.messenger.FileLoader.getInternalCacheDir()
            java.lang.StringBuilder r14 = new java.lang.StringBuilder
            r14.<init>()
            r14.append(r9)
            java.lang.String r15 = ".key"
            r14.append(r15)
            java.lang.String r14 = r14.toString()
            r0.<init>(r13, r14)
            r13 = r0
            java.io.RandomAccessFile r0 = new java.io.RandomAccessFile     // Catch:{ Exception -> 0x04c6 }
            r0.<init>(r13, r10)     // Catch:{ Exception -> 0x04c6 }
            r14 = r0
            long r18 = r13.length()     // Catch:{ Exception -> 0x04c6 }
            r0 = 32
            byte[] r15 = new byte[r0]     // Catch:{ Exception -> 0x04c6 }
            r8.encryptKey = r15     // Catch:{ Exception -> 0x04c6 }
            r5 = 16
            byte[] r0 = new byte[r5]     // Catch:{ Exception -> 0x04c6 }
            r8.encryptIv = r0     // Catch:{ Exception -> 0x04c6 }
            r16 = 0
            int r0 = (r18 > r16 ? 1 : (r18 == r16 ? 0 : -1))
            if (r0 <= 0) goto L_0x049d
            r22 = 48
            long r22 = r18 % r22
            int r0 = (r22 > r16 ? 1 : (r22 == r16 ? 0 : -1))
            if (r0 != 0) goto L_0x049d
            r0 = 32
            r5 = 0
            r14.read(r15, r5, r0)     // Catch:{ Exception -> 0x04c6 }
            byte[] r0 = r8.encryptIv     // Catch:{ Exception -> 0x04c6 }
            r15 = 16
            r14.read(r0, r5, r15)     // Catch:{ Exception -> 0x04c6 }
            goto L_0x04b6
        L_0x049d:
            java.security.SecureRandom r0 = org.telegram.messenger.Utilities.random     // Catch:{ Exception -> 0x04c6 }
            byte[] r5 = r8.encryptKey     // Catch:{ Exception -> 0x04c6 }
            r0.nextBytes(r5)     // Catch:{ Exception -> 0x04c6 }
            java.security.SecureRandom r0 = org.telegram.messenger.Utilities.random     // Catch:{ Exception -> 0x04c6 }
            byte[] r5 = r8.encryptIv     // Catch:{ Exception -> 0x04c6 }
            r0.nextBytes(r5)     // Catch:{ Exception -> 0x04c6 }
            byte[] r0 = r8.encryptKey     // Catch:{ Exception -> 0x04c6 }
            r14.write(r0)     // Catch:{ Exception -> 0x04c6 }
            byte[] r0 = r8.encryptIv     // Catch:{ Exception -> 0x04c6 }
            r14.write(r0)     // Catch:{ Exception -> 0x04c6 }
            r6 = 1
        L_0x04b6:
            java.nio.channels.FileChannel r0 = r14.getChannel()     // Catch:{ Exception -> 0x04be }
            r0.close()     // Catch:{ Exception -> 0x04be }
            goto L_0x04c2
        L_0x04be:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ Exception -> 0x04c6 }
        L_0x04c2:
            r14.close()     // Catch:{ Exception -> 0x04c6 }
            goto L_0x04ca
        L_0x04c6:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x04ca:
            r5 = 1
            boolean[] r0 = new boolean[r5]
            r5 = 0
            r0[r5] = r5
            r5 = r0
            boolean r0 = r8.supportsPreloading
            r18 = 8
            if (r0 == 0) goto L_0x0730
            if (r1 == 0) goto L_0x0730
            java.io.File r0 = new java.io.File
            java.io.File r15 = r8.tempPath
            r0.<init>(r15, r1)
            r8.cacheFilePreload = r0
            r15 = 0
            java.io.RandomAccessFile r0 = new java.io.RandomAccessFile     // Catch:{ Exception -> 0x06ef }
            java.io.File r13 = r8.cacheFilePreload     // Catch:{ Exception -> 0x06ef }
            r0.<init>(r13, r10)     // Catch:{ Exception -> 0x06ef }
            r8.preloadStream = r0     // Catch:{ Exception -> 0x06ef }
            long r13 = r0.length()     // Catch:{ Exception -> 0x06ef }
            r23 = 0
            r25 = r1
            r1 = 1
            r8.preloadStreamFileOffset = r1     // Catch:{ Exception -> 0x06db }
            long r0 = r13 - r23
            r26 = 1
            int r28 = (r0 > r26 ? 1 : (r0 == r26 ? 0 : -1))
            if (r28 <= 0) goto L_0x06bc
            java.io.RandomAccessFile r0 = r8.preloadStream     // Catch:{ Exception -> 0x06db }
            byte r0 = r0.readByte()     // Catch:{ Exception -> 0x06db }
            if (r0 == 0) goto L_0x0509
            r0 = 1
            goto L_0x050a
        L_0x0509:
            r0 = 0
        L_0x050a:
            r1 = 0
            r5[r1] = r0     // Catch:{ Exception -> 0x06db }
            long r23 = r23 + r26
        L_0x050f:
            int r0 = (r23 > r13 ? 1 : (r23 == r13 ? 0 : -1))
            if (r0 >= 0) goto L_0x06a7
            long r0 = r13 - r23
            int r26 = (r0 > r18 ? 1 : (r0 == r18 ? 0 : -1))
            if (r26 >= 0) goto L_0x052f
            r34 = r2
            r35 = r4
            r36 = r5
            r33 = r6
            r28 = r7
            r37 = r9
            r38 = r10
            r26 = r11
            r27 = r12
            r31 = r13
            goto L_0x06d0
        L_0x052f:
            java.io.RandomAccessFile r0 = r8.preloadStream     // Catch:{ Exception -> 0x06db }
            long r0 = r0.readLong()     // Catch:{ Exception -> 0x06db }
            long r23 = r23 + r18
            long r26 = r13 - r23
            int r28 = (r26 > r18 ? 1 : (r26 == r18 ? 0 : -1))
            if (r28 < 0) goto L_0x0692
            r16 = 0
            int r26 = (r0 > r16 ? 1 : (r0 == r16 ? 0 : -1))
            if (r26 < 0) goto L_0x0692
            r26 = r11
            r27 = r12
            long r11 = r8.totalBytesCount     // Catch:{ Exception -> 0x0681 }
            int r28 = (r0 > r11 ? 1 : (r0 == r11 ? 0 : -1))
            if (r28 <= 0) goto L_0x055f
            r34 = r2
            r35 = r4
            r36 = r5
            r33 = r6
            r28 = r7
            r37 = r9
            r38 = r10
            r31 = r13
            goto L_0x06d0
        L_0x055f:
            java.io.RandomAccessFile r11 = r8.preloadStream     // Catch:{ Exception -> 0x0681 }
            long r11 = r11.readLong()     // Catch:{ Exception -> 0x0681 }
            long r23 = r23 + r18
            long r28 = r13 - r23
            int r30 = (r28 > r11 ? 1 : (r28 == r11 ? 0 : -1))
            if (r30 < 0) goto L_0x0670
            r34 = r2
            int r2 = r8.currentDownloadChunkSize     // Catch:{ Exception -> 0x0661 }
            r35 = r4
            r36 = r5
            long r4 = (long) r2
            int r2 = (r11 > r4 ? 1 : (r11 == r4 ? 0 : -1))
            if (r2 <= 0) goto L_0x0586
            r33 = r6
            r28 = r7
            r37 = r9
            r38 = r10
            r31 = r13
            goto L_0x06d0
        L_0x0586:
            org.telegram.messenger.FileLoadOperation$PreloadRange r2 = new org.telegram.messenger.FileLoadOperation$PreloadRange     // Catch:{ Exception -> 0x0656 }
            r33 = 0
            r28 = r2
            r29 = r23
            r31 = r11
            r28.<init>(r29, r31)     // Catch:{ Exception -> 0x0656 }
            long r4 = r23 + r11
            r28 = r7
            java.io.RandomAccessFile r7 = r8.preloadStream     // Catch:{ Exception -> 0x064d }
            r7.seek(r4)     // Catch:{ Exception -> 0x064d }
            long r23 = r13 - r4
            r29 = 24
            int r7 = (r23 > r29 ? 1 : (r23 == r29 ? 0 : -1))
            if (r7 >= 0) goto L_0x05b0
            r23 = r4
            r33 = r6
            r37 = r9
            r38 = r10
            r31 = r13
            goto L_0x06d0
        L_0x05b0:
            java.io.RandomAccessFile r7 = r8.preloadStream     // Catch:{ Exception -> 0x064d }
            r31 = r13
            long r13 = r7.readLong()     // Catch:{ Exception -> 0x064d }
            r8.foundMoovSize = r13     // Catch:{ Exception -> 0x064d }
            r16 = 0
            int r7 = (r13 > r16 ? 1 : (r13 == r16 ? 0 : -1))
            if (r7 == 0) goto L_0x05e1
            r33 = r6
            long r6 = r8.nextPreloadDownloadOffset     // Catch:{ Exception -> 0x05da }
            r37 = r9
            r38 = r10
            long r9 = r8.totalBytesCount     // Catch:{ Exception -> 0x06d9 }
            r21 = 2
            long r9 = r9 / r21
            int r23 = (r6 > r9 ? 1 : (r6 == r9 ? 0 : -1))
            if (r23 <= 0) goto L_0x05d4
            r6 = 2
            goto L_0x05d5
        L_0x05d4:
            r6 = 1
        L_0x05d5:
            r8.moovFound = r6     // Catch:{ Exception -> 0x06d9 }
            r8.preloadNotRequestedBytesCount = r13     // Catch:{ Exception -> 0x06d9 }
            goto L_0x05e7
        L_0x05da:
            r0 = move-exception
            r37 = r9
            r38 = r10
            goto L_0x0704
        L_0x05e1:
            r33 = r6
            r37 = r9
            r38 = r10
        L_0x05e7:
            java.io.RandomAccessFile r6 = r8.preloadStream     // Catch:{ Exception -> 0x06d9 }
            long r6 = r6.readLong()     // Catch:{ Exception -> 0x06d9 }
            r8.nextPreloadDownloadOffset = r6     // Catch:{ Exception -> 0x06d9 }
            java.io.RandomAccessFile r6 = r8.preloadStream     // Catch:{ Exception -> 0x06d9 }
            long r6 = r6.readLong()     // Catch:{ Exception -> 0x06d9 }
            r8.nextAtomOffset = r6     // Catch:{ Exception -> 0x06d9 }
            long r23 = r4 + r29
            java.util.HashMap<java.lang.Long, org.telegram.messenger.FileLoadOperation$PreloadRange> r4 = r8.preloadedBytesRanges     // Catch:{ Exception -> 0x06d9 }
            if (r4 != 0) goto L_0x0604
            java.util.HashMap r4 = new java.util.HashMap     // Catch:{ Exception -> 0x06d9 }
            r4.<init>()     // Catch:{ Exception -> 0x06d9 }
            r8.preloadedBytesRanges = r4     // Catch:{ Exception -> 0x06d9 }
        L_0x0604:
            java.util.HashMap<java.lang.Long, java.lang.Integer> r4 = r8.requestedPreloadedBytesRanges     // Catch:{ Exception -> 0x06d9 }
            if (r4 != 0) goto L_0x060f
            java.util.HashMap r4 = new java.util.HashMap     // Catch:{ Exception -> 0x06d9 }
            r4.<init>()     // Catch:{ Exception -> 0x06d9 }
            r8.requestedPreloadedBytesRanges = r4     // Catch:{ Exception -> 0x06d9 }
        L_0x060f:
            java.util.HashMap<java.lang.Long, org.telegram.messenger.FileLoadOperation$PreloadRange> r4 = r8.preloadedBytesRanges     // Catch:{ Exception -> 0x06d9 }
            java.lang.Long r5 = java.lang.Long.valueOf(r0)     // Catch:{ Exception -> 0x06d9 }
            r4.put(r5, r2)     // Catch:{ Exception -> 0x06d9 }
            java.util.HashMap<java.lang.Long, java.lang.Integer> r4 = r8.requestedPreloadedBytesRanges     // Catch:{ Exception -> 0x06d9 }
            java.lang.Long r5 = java.lang.Long.valueOf(r0)     // Catch:{ Exception -> 0x06d9 }
            r6 = 1
            java.lang.Integer r7 = java.lang.Integer.valueOf(r6)     // Catch:{ Exception -> 0x06d9 }
            r4.put(r5, r7)     // Catch:{ Exception -> 0x06d9 }
            int r4 = r8.totalPreloadedBytes     // Catch:{ Exception -> 0x06d9 }
            long r4 = (long) r4     // Catch:{ Exception -> 0x06d9 }
            long r4 = r4 + r11
            int r5 = (int) r4     // Catch:{ Exception -> 0x06d9 }
            r8.totalPreloadedBytes = r5     // Catch:{ Exception -> 0x06d9 }
            int r4 = r8.preloadStreamFileOffset     // Catch:{ Exception -> 0x06d9 }
            long r4 = (long) r4     // Catch:{ Exception -> 0x06d9 }
            r6 = 36
            long r6 = r6 + r11
            long r4 = r4 + r6
            int r5 = (int) r4     // Catch:{ Exception -> 0x06d9 }
            r8.preloadStreamFileOffset = r5     // Catch:{ Exception -> 0x06d9 }
            r11 = r26
            r12 = r27
            r7 = r28
            r13 = r31
            r6 = r33
            r2 = r34
            r4 = r35
            r5 = r36
            r9 = r37
            r10 = r38
            goto L_0x050f
        L_0x064d:
            r0 = move-exception
            r33 = r6
            r37 = r9
            r38 = r10
            goto L_0x0704
        L_0x0656:
            r0 = move-exception
            r33 = r6
            r28 = r7
            r37 = r9
            r38 = r10
            goto L_0x0704
        L_0x0661:
            r0 = move-exception
            r35 = r4
            r36 = r5
            r33 = r6
            r28 = r7
            r37 = r9
            r38 = r10
            goto L_0x0704
        L_0x0670:
            r34 = r2
            r35 = r4
            r36 = r5
            r33 = r6
            r28 = r7
            r37 = r9
            r38 = r10
            r31 = r13
            goto L_0x06d0
        L_0x0681:
            r0 = move-exception
            r34 = r2
            r35 = r4
            r36 = r5
            r33 = r6
            r28 = r7
            r37 = r9
            r38 = r10
            goto L_0x0704
        L_0x0692:
            r34 = r2
            r35 = r4
            r36 = r5
            r33 = r6
            r28 = r7
            r37 = r9
            r38 = r10
            r26 = r11
            r27 = r12
            r31 = r13
            goto L_0x06d0
        L_0x06a7:
            r34 = r2
            r35 = r4
            r36 = r5
            r33 = r6
            r28 = r7
            r37 = r9
            r38 = r10
            r26 = r11
            r27 = r12
            r31 = r13
            goto L_0x06d0
        L_0x06bc:
            r34 = r2
            r35 = r4
            r36 = r5
            r33 = r6
            r28 = r7
            r37 = r9
            r38 = r10
            r26 = r11
            r27 = r12
            r31 = r13
        L_0x06d0:
            java.io.RandomAccessFile r0 = r8.preloadStream     // Catch:{ Exception -> 0x06d9 }
            int r1 = r8.preloadStreamFileOffset     // Catch:{ Exception -> 0x06d9 }
            long r1 = (long) r1     // Catch:{ Exception -> 0x06d9 }
            r0.seek(r1)     // Catch:{ Exception -> 0x06d9 }
            goto L_0x0707
        L_0x06d9:
            r0 = move-exception
            goto L_0x0704
        L_0x06db:
            r0 = move-exception
            r34 = r2
            r35 = r4
            r36 = r5
            r33 = r6
            r28 = r7
            r37 = r9
            r38 = r10
            r26 = r11
            r27 = r12
            goto L_0x0704
        L_0x06ef:
            r0 = move-exception
            r25 = r1
            r34 = r2
            r35 = r4
            r36 = r5
            r33 = r6
            r28 = r7
            r37 = r9
            r38 = r10
            r26 = r11
            r27 = r12
        L_0x0704:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0707:
            boolean r0 = r8.isPreloadVideoOperation
            if (r0 != 0) goto L_0x0744
            java.util.HashMap<java.lang.Long, org.telegram.messenger.FileLoadOperation$PreloadRange> r0 = r8.preloadedBytesRanges
            if (r0 != 0) goto L_0x0744
            r1 = 0
            r8.cacheFilePreload = r1
            java.io.RandomAccessFile r0 = r8.preloadStream     // Catch:{ Exception -> 0x072b }
            if (r0 == 0) goto L_0x072a
            java.nio.channels.FileChannel r0 = r0.getChannel()     // Catch:{ Exception -> 0x071e }
            r0.close()     // Catch:{ Exception -> 0x071e }
            goto L_0x0722
        L_0x071e:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ Exception -> 0x072b }
        L_0x0722:
            java.io.RandomAccessFile r0 = r8.preloadStream     // Catch:{ Exception -> 0x072b }
            r0.close()     // Catch:{ Exception -> 0x072b }
            r1 = 0
            r8.preloadStream = r1     // Catch:{ Exception -> 0x072b }
        L_0x072a:
            goto L_0x0744
        L_0x072b:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x0744
        L_0x0730:
            r25 = r1
            r34 = r2
            r35 = r4
            r36 = r5
            r33 = r6
            r28 = r7
            r37 = r9
            r38 = r10
            r26 = r11
            r27 = r12
        L_0x0744:
            if (r3 == 0) goto L_0x07b1
            java.io.File r0 = new java.io.File
            java.io.File r1 = r8.tempPath
            r0.<init>(r1, r3)
            r8.cacheFileParts = r0
            java.io.RandomAccessFile r0 = new java.io.RandomAccessFile     // Catch:{ Exception -> 0x07aa }
            java.io.File r1 = r8.cacheFileParts     // Catch:{ Exception -> 0x07aa }
            r2 = r38
            r0.<init>(r1, r2)     // Catch:{ Exception -> 0x07a8 }
            r8.filePartsStream = r0     // Catch:{ Exception -> 0x07a8 }
            long r0 = r0.length()     // Catch:{ Exception -> 0x07a8 }
            long r4 = r0 % r18
            r6 = 4
            int r9 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r9 != 0) goto L_0x07a7
            long r0 = r0 - r6
            java.io.RandomAccessFile r4 = r8.filePartsStream     // Catch:{ Exception -> 0x07a8 }
            int r4 = r4.readInt()     // Catch:{ Exception -> 0x07a8 }
            long r5 = (long) r4     // Catch:{ Exception -> 0x07a8 }
            r9 = 2
            long r9 = r0 / r9
            int r7 = (r5 > r9 ? 1 : (r5 == r9 ? 0 : -1))
            if (r7 > 0) goto L_0x07a7
            r5 = 0
        L_0x0777:
            if (r5 >= r4) goto L_0x07a7
            java.io.RandomAccessFile r6 = r8.filePartsStream     // Catch:{ Exception -> 0x07a8 }
            long r10 = r6.readLong()     // Catch:{ Exception -> 0x07a8 }
            java.io.RandomAccessFile r6 = r8.filePartsStream     // Catch:{ Exception -> 0x07a8 }
            long r12 = r6.readLong()     // Catch:{ Exception -> 0x07a8 }
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$Range> r6 = r8.notLoadedBytesRanges     // Catch:{ Exception -> 0x07a8 }
            org.telegram.messenger.FileLoadOperation$Range r7 = new org.telegram.messenger.FileLoadOperation$Range     // Catch:{ Exception -> 0x07a8 }
            r14 = 0
            r9 = r7
            r9.<init>(r10, r12)     // Catch:{ Exception -> 0x07a8 }
            r6.add(r7)     // Catch:{ Exception -> 0x07a8 }
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$Range> r6 = r8.notRequestedBytesRanges     // Catch:{ Exception -> 0x07a8 }
            org.telegram.messenger.FileLoadOperation$Range r7 = new org.telegram.messenger.FileLoadOperation$Range     // Catch:{ Exception -> 0x07a8 }
            r23 = 0
            r18 = r7
            r19 = r10
            r21 = r12
            r18.<init>(r19, r21)     // Catch:{ Exception -> 0x07a8 }
            r6.add(r7)     // Catch:{ Exception -> 0x07a8 }
            int r5 = r5 + 1
            goto L_0x0777
        L_0x07a7:
            goto L_0x07b3
        L_0x07a8:
            r0 = move-exception
            goto L_0x07ad
        L_0x07aa:
            r0 = move-exception
            r2 = r38
        L_0x07ad:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x07b3
        L_0x07b1:
            r2 = r38
        L_0x07b3:
            java.io.File r0 = r8.cacheFileTemp
            boolean r0 = r0.exists()
            if (r0 == 0) goto L_0x0815
            if (r33 == 0) goto L_0x07c4
            java.io.File r0 = r8.cacheFileTemp
            r0.delete()
            goto L_0x083c
        L_0x07c4:
            java.io.File r0 = r8.cacheFileTemp
            long r0 = r0.length()
            if (r35 == 0) goto L_0x07da
            int r4 = r8.currentDownloadChunkSize
            long r4 = (long) r4
            long r4 = r0 % r4
            r6 = 0
            int r9 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r9 == 0) goto L_0x07da
            r8.requestedBytesCount = r6
            goto L_0x07eb
        L_0x07da:
            java.io.File r4 = r8.cacheFileTemp
            long r4 = r4.length()
            int r6 = r8.currentDownloadChunkSize
            long r9 = (long) r6
            long r4 = r4 / r9
            long r6 = (long) r6
            long r4 = r4 * r6
            r8.downloadedBytes = r4
            r8.requestedBytesCount = r4
        L_0x07eb:
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$Range> r4 = r8.notLoadedBytesRanges
            if (r4 == 0) goto L_0x0814
            boolean r4 = r4.isEmpty()
            if (r4 == 0) goto L_0x0814
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$Range> r4 = r8.notLoadedBytesRanges
            org.telegram.messenger.FileLoadOperation$Range r5 = new org.telegram.messenger.FileLoadOperation$Range
            long r10 = r8.downloadedBytes
            long r12 = r8.totalBytesCount
            r14 = 0
            r9 = r5
            r9.<init>(r10, r12)
            r4.add(r5)
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$Range> r4 = r8.notRequestedBytesRanges
            org.telegram.messenger.FileLoadOperation$Range r5 = new org.telegram.messenger.FileLoadOperation$Range
            long r10 = r8.downloadedBytes
            long r12 = r8.totalBytesCount
            r9 = r5
            r9.<init>(r10, r12)
            r4.add(r5)
        L_0x0814:
            goto L_0x083c
        L_0x0815:
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$Range> r0 = r8.notLoadedBytesRanges
            if (r0 == 0) goto L_0x083c
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L_0x083c
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$Range> r0 = r8.notLoadedBytesRanges
            org.telegram.messenger.FileLoadOperation$Range r1 = new org.telegram.messenger.FileLoadOperation$Range
            r10 = 0
            long r12 = r8.totalBytesCount
            r14 = 0
            r9 = r1
            r9.<init>(r10, r12)
            r0.add(r1)
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$Range> r0 = r8.notRequestedBytesRanges
            org.telegram.messenger.FileLoadOperation$Range r1 = new org.telegram.messenger.FileLoadOperation$Range
            long r12 = r8.totalBytesCount
            r9 = r1
            r9.<init>(r10, r12)
            r0.add(r1)
        L_0x083c:
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$Range> r0 = r8.notLoadedBytesRanges
            if (r0 == 0) goto L_0x0868
            long r4 = r8.totalBytesCount
            r8.downloadedBytes = r4
            int r0 = r0.size()
            r1 = 0
        L_0x0849:
            if (r1 >= r0) goto L_0x0864
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$Range> r4 = r8.notLoadedBytesRanges
            java.lang.Object r4 = r4.get(r1)
            org.telegram.messenger.FileLoadOperation$Range r4 = (org.telegram.messenger.FileLoadOperation.Range) r4
            long r5 = r8.downloadedBytes
            long r9 = r4.end
            long r11 = r4.start
            long r9 = r9 - r11
            long r5 = r5 - r9
            r8.downloadedBytes = r5
            int r1 = r1 + 1
            goto L_0x0849
        L_0x0864:
            long r4 = r8.downloadedBytes
            r8.requestedBytesCount = r4
        L_0x0868:
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x08a7
            boolean r0 = r8.isPreloadVideoOperation
            if (r0 == 0) goto L_0x0887
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "start preloading file to temp = "
            r0.append(r1)
            java.io.File r1 = r8.cacheFileTemp
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            org.telegram.messenger.FileLog.d(r0)
            goto L_0x08a7
        L_0x0887:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "start loading file to temp = "
            r0.append(r1)
            java.io.File r1 = r8.cacheFileTemp
            r0.append(r1)
            java.lang.String r1 = " final = "
            r0.append(r1)
            java.io.File r1 = r8.cacheFileFinal
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            org.telegram.messenger.FileLog.d(r0)
        L_0x08a7:
            if (r35 == 0) goto L_0x08fa
            java.io.File r0 = new java.io.File
            java.io.File r1 = r8.tempPath
            r4 = r35
            r0.<init>(r1, r4)
            r8.cacheIvTemp = r0
            java.io.RandomAccessFile r0 = new java.io.RandomAccessFile     // Catch:{ Exception -> 0x08ef }
            java.io.File r1 = r8.cacheIvTemp     // Catch:{ Exception -> 0x08ef }
            r0.<init>(r1, r2)     // Catch:{ Exception -> 0x08ef }
            r8.fiv = r0     // Catch:{ Exception -> 0x08ef }
            long r0 = r8.downloadedBytes     // Catch:{ Exception -> 0x08ef }
            r5 = 0
            int r7 = (r0 > r5 ? 1 : (r0 == r5 ? 0 : -1))
            if (r7 == 0) goto L_0x08ec
            if (r33 != 0) goto L_0x08ec
            java.io.File r0 = r8.cacheIvTemp     // Catch:{ Exception -> 0x08ef }
            long r0 = r0.length()     // Catch:{ Exception -> 0x08ef }
            r5 = 0
            int r7 = (r0 > r5 ? 1 : (r0 == r5 ? 0 : -1))
            if (r7 <= 0) goto L_0x08e6
            r9 = 64
            long r9 = r0 % r9
            int r7 = (r9 > r5 ? 1 : (r9 == r5 ? 0 : -1))
            if (r7 != 0) goto L_0x08e6
            java.io.RandomAccessFile r5 = r8.fiv     // Catch:{ Exception -> 0x08ef }
            byte[] r6 = r8.iv     // Catch:{ Exception -> 0x08ef }
            r7 = 64
            r9 = 0
            r5.read(r6, r9, r7)     // Catch:{ Exception -> 0x08ef }
            goto L_0x08ec
        L_0x08e6:
            r5 = 0
            r8.downloadedBytes = r5     // Catch:{ Exception -> 0x08ef }
            r8.requestedBytesCount = r5     // Catch:{ Exception -> 0x08ef }
        L_0x08ec:
            r5 = 0
            goto L_0x08fe
        L_0x08ef:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            r5 = 0
            r8.downloadedBytes = r5
            r8.requestedBytesCount = r5
            goto L_0x08fe
        L_0x08fa:
            r4 = r35
            r5 = 0
        L_0x08fe:
            boolean r0 = r8.isPreloadVideoOperation
            if (r0 != 0) goto L_0x0911
            long r0 = r8.downloadedBytes
            int r7 = (r0 > r5 ? 1 : (r0 == r5 ? 0 : -1))
            if (r7 == 0) goto L_0x0911
            long r0 = r8.totalBytesCount
            int r7 = (r0 > r5 ? 1 : (r0 == r5 ? 0 : -1))
            if (r7 <= 0) goto L_0x0911
            r39.copyNotLoadedRanges()
        L_0x0911:
            r39.updateProgress()
            java.io.RandomAccessFile r0 = new java.io.RandomAccessFile     // Catch:{ Exception -> 0x092a }
            java.io.File r1 = r8.cacheFileTemp     // Catch:{ Exception -> 0x092a }
            r0.<init>(r1, r2)     // Catch:{ Exception -> 0x092a }
            r8.fileOutputStream = r0     // Catch:{ Exception -> 0x092a }
            long r1 = r8.downloadedBytes     // Catch:{ Exception -> 0x092a }
            r5 = 0
            int r7 = (r1 > r5 ? 1 : (r1 == r5 ? 0 : -1))
            if (r7 == 0) goto L_0x0928
            r0.seek(r1)     // Catch:{ Exception -> 0x092a }
        L_0x0928:
            r1 = 0
            goto L_0x092f
        L_0x092a:
            r0 = move-exception
            r1 = 0
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0, (boolean) r1)
        L_0x092f:
            java.io.RandomAccessFile r0 = r8.fileOutputStream
            if (r0 != 0) goto L_0x0938
            r2 = 1
            r8.onFail(r2, r1)
            return r1
        L_0x0938:
            r2 = 1
            r8.started = r2
            org.telegram.messenger.DispatchQueue r0 = org.telegram.messenger.Utilities.stageQueue
            org.telegram.messenger.FileLoadOperation$$ExternalSyntheticLambda1 r1 = new org.telegram.messenger.FileLoadOperation$$ExternalSyntheticLambda1
            r5 = r36
            r1.<init>(r8, r5)
            r0.postRunnable(r1)
            goto L_0x096d
        L_0x0948:
            r25 = r1
            r34 = r2
            r28 = r7
            r37 = r9
            r26 = r11
            r27 = r12
            r2 = 1
            r8.started = r2
            r1 = 0
            r8.onFinishLoadingFile(r1)     // Catch:{ Exception -> 0x0967 }
            org.telegram.messenger.FilePathDatabase$PathData r0 = r8.pathSaveData     // Catch:{ Exception -> 0x0967 }
            if (r0 == 0) goto L_0x0965
            org.telegram.messenger.FileLoadOperation$FileLoadOperationDelegate r1 = r8.delegate     // Catch:{ Exception -> 0x0967 }
            r2 = 0
            r1.saveFilePath(r0, r2)     // Catch:{ Exception -> 0x0967 }
        L_0x0965:
            r2 = 1
            goto L_0x096d
        L_0x0967:
            r0 = move-exception
            r2 = 1
            r5 = 0
            r8.onFail(r2, r5)
        L_0x096d:
            return r2
        L_0x096e:
            r26 = r11
            r27 = r12
            r2 = 1
            r5 = 0
        L_0x0974:
            r8.onFail(r2, r5)
            return r5
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.FileLoadOperation.start(org.telegram.messenger.FileLoadOperationStream, long, boolean):boolean");
    }

    /* renamed from: lambda$start$4$org-telegram-messenger-FileLoadOperation  reason: not valid java name */
    public /* synthetic */ void m1811lambda$start$4$orgtelegrammessengerFileLoadOperation(boolean steamPriority, long streamOffset, FileLoadOperationStream stream, boolean alreadyStarted) {
        if (this.streamListeners == null) {
            this.streamListeners = new ArrayList<>();
        }
        if (steamPriority) {
            int i = this.currentDownloadChunkSize;
            long offset = (streamOffset / ((long) i)) * ((long) i);
            RequestInfo requestInfo = this.priorityRequestInfo;
            if (!(requestInfo == null || requestInfo.offset == offset)) {
                this.requestInfos.remove(this.priorityRequestInfo);
                this.requestedBytesCount -= (long) this.currentDownloadChunkSize;
                removePart(this.notRequestedBytesRanges, this.priorityRequestInfo.offset, this.priorityRequestInfo.offset + ((long) this.currentDownloadChunkSize));
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
                this.streamPriorityStartOffset = offset;
            }
        } else {
            int i2 = this.currentDownloadChunkSize;
            this.streamStartOffset = (streamOffset / ((long) i2)) * ((long) i2);
        }
        this.streamListeners.add(stream);
        if (alreadyStarted) {
            if (this.preloadedBytesRanges != null) {
                if (getDownloadedLengthFromOffsetInternal(this.notLoadedBytesRanges, this.streamStartOffset, 1) == 0 && this.preloadedBytesRanges.get(Long.valueOf(this.streamStartOffset)) != null) {
                    this.nextPartWasPreloaded = true;
                }
            }
            startDownloadRequest();
            this.nextPartWasPreloaded = false;
        }
    }

    /* renamed from: lambda$start$5$org-telegram-messenger-FileLoadOperation  reason: not valid java name */
    public /* synthetic */ void m1812lambda$start$5$orgtelegrammessengerFileLoadOperation(boolean[] preloaded) {
        long j = this.totalBytesCount;
        if (j == 0 || ((!this.isPreloadVideoOperation || !preloaded[0]) && this.downloadedBytes != j)) {
            startDownloadRequest();
            return;
        }
        try {
            onFinishLoadingFile(false);
        } catch (Exception e) {
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

    public void setIsPreloadVideoOperation(boolean value) {
        boolean z = this.isPreloadVideoOperation;
        if (z == value) {
            return;
        }
        if (value && this.totalBytesCount <= 2097152) {
            return;
        }
        if (value || !z) {
            this.isPreloadVideoOperation = value;
        } else if (this.state == 3) {
            this.isPreloadVideoOperation = value;
            this.state = 0;
            this.preloadFinished = false;
            start();
        } else if (this.state == 1) {
            Utilities.stageQueue.postRunnable(new FileLoadOperation$$ExternalSyntheticLambda11(this, value));
        } else {
            this.isPreloadVideoOperation = value;
        }
    }

    /* renamed from: lambda$setIsPreloadVideoOperation$6$org-telegram-messenger-FileLoadOperation  reason: not valid java name */
    public /* synthetic */ void m1810xCLASSNAMEa74(boolean value) {
        this.requestedBytesCount = 0;
        clearOperaion((RequestInfo) null, true);
        this.isPreloadVideoOperation = value;
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

    public void cancel(boolean deleteFiles) {
        Utilities.stageQueue.postRunnable(new FileLoadOperation$$ExternalSyntheticLambda9(this, deleteFiles));
    }

    /* renamed from: lambda$cancel$7$org-telegram-messenger-FileLoadOperation  reason: not valid java name */
    public /* synthetic */ void m1803lambda$cancel$7$orgtelegrammessengerFileLoadOperation(boolean deleteFiles) {
        if (!(this.state == 3 || this.state == 2)) {
            if (this.requestInfos != null) {
                for (int a = 0; a < this.requestInfos.size(); a++) {
                    RequestInfo requestInfo = this.requestInfos.get(a);
                    if (requestInfo.requestToken != 0) {
                        ConnectionsManager.getInstance(this.currentAccount).cancelRequest(requestInfo.requestToken, true);
                    }
                }
            }
            onFail(false, 1);
        }
        if (deleteFiles) {
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
            for (int a = 0; a < this.delayedRequestInfos.size(); a++) {
                RequestInfo requestInfo = this.delayedRequestInfos.get(a);
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

    private void onFinishLoadingFile(boolean increment) {
        boolean renameResult;
        String newFileName;
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
                    if (this.ungzip) {
                        try {
                            GZIPInputStream gzipInputStream = new GZIPInputStream(new FileInputStream(this.cacheFileTemp));
                            FileLoader.copyFile(gzipInputStream, this.cacheFileGzipTemp, 2097152);
                            gzipInputStream.close();
                            this.cacheFileTemp.delete();
                            this.cacheFileTemp = this.cacheFileGzipTemp;
                            this.ungzip = false;
                        } catch (ZipException e) {
                            this.ungzip = false;
                        } catch (Throwable e2) {
                            FileLog.e(e2);
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.e("unable to ungzip temp = " + this.cacheFileTemp + " to final = " + this.cacheFileFinal);
                            }
                        }
                    }
                    if (!this.ungzip) {
                        if (this.parentObject instanceof TLRPC.TL_theme) {
                            try {
                                renameResult = AndroidUtilities.copyFile(this.cacheFileTemp, this.cacheFileFinal);
                            } catch (Exception e3) {
                                FileLog.e((Throwable) e3);
                                renameResult = false;
                            }
                        } else {
                            try {
                                if (this.pathSaveData != null) {
                                    this.cacheFileFinal = new File(this.storePath, this.storeFileName);
                                    int count = 1;
                                    while (this.cacheFileFinal.exists()) {
                                        int lastDotIndex = this.storeFileName.lastIndexOf(46);
                                        if (lastDotIndex > 0) {
                                            newFileName = this.storeFileName.substring(0, lastDotIndex) + " (" + count + ")" + this.storeFileName.substring(lastDotIndex);
                                        } else {
                                            newFileName = this.storeFileName + " (" + count + ")";
                                        }
                                        this.cacheFileFinal = new File(this.storePath, newFileName);
                                        count++;
                                    }
                                }
                                renameResult = this.cacheFileTemp.renameTo(this.cacheFileFinal);
                            } catch (Exception e4) {
                                FileLog.e((Throwable) e4);
                                renameResult = false;
                            }
                        }
                        if (!renameResult) {
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.e("unable to rename temp = " + this.cacheFileTemp + " to final = " + this.cacheFileFinal + " retry = " + this.renameRetryCount);
                            }
                            int i = this.renameRetryCount + 1;
                            this.renameRetryCount = i;
                            if (i < 3) {
                                this.state = 1;
                                Utilities.stageQueue.postRunnable(new FileLoadOperation$$ExternalSyntheticLambda10(this, increment), 200);
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
                    FileLog.d("finished downloading file to " + this.cacheFileFinal);
                }
                if (increment) {
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

    /* renamed from: lambda$onFinishLoadingFile$8$org-telegram-messenger-FileLoadOperation  reason: not valid java name */
    public /* synthetic */ void m1807x905dc9cf(boolean increment) {
        try {
            onFinishLoadingFile(increment);
        } catch (Exception e) {
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

    private long findNextPreloadDownloadOffset(long atomOffset, long partOffset, NativeByteBuffer partBuffer) {
        NativeByteBuffer nativeByteBuffer = partBuffer;
        int partSize = partBuffer.limit();
        long atomOffset2 = atomOffset;
        while (true) {
            if (atomOffset2 < partOffset - ((long) (this.preloadTempBuffer != null ? 16 : 0)) || atomOffset2 >= partOffset + ((long) partSize)) {
                return 0;
            }
            if (atomOffset2 >= (partOffset + ((long) partSize)) - 16) {
                long count = (partOffset + ((long) partSize)) - atomOffset2;
                if (count <= 2147483647L) {
                    this.preloadTempBufferCount = (int) count;
                    nativeByteBuffer.position((int) ((long) (partBuffer.limit() - this.preloadTempBufferCount)));
                    nativeByteBuffer.readBytes(this.preloadTempBuffer, 0, this.preloadTempBufferCount, false);
                    return partOffset + ((long) partSize);
                }
                throw new RuntimeException("!!!");
            }
            if (this.preloadTempBufferCount != 0) {
                nativeByteBuffer.position(0);
                byte[] bArr = this.preloadTempBuffer;
                int i = this.preloadTempBufferCount;
                nativeByteBuffer.readBytes(bArr, i, 16 - i, false);
                this.preloadTempBufferCount = 0;
            } else {
                long count2 = atomOffset2 - partOffset;
                if (count2 <= 2147483647L) {
                    nativeByteBuffer.position((int) count2);
                    nativeByteBuffer.readBytes(this.preloadTempBuffer, 0, 16, false);
                } else {
                    throw new RuntimeException("!!!");
                }
            }
            byte[] bArr2 = this.preloadTempBuffer;
            int atomSize = ((bArr2[0] & 255) << 24) + ((bArr2[1] & 255) << 16) + ((bArr2[2] & 255) << 8) + (bArr2[3] & 255);
            if (atomSize == 0) {
                return 0;
            }
            if (atomSize == 1) {
                atomSize = ((bArr2[12] & 255) << 24) + ((bArr2[13] & 255) << 16) + ((bArr2[14] & 255) << 8) + (bArr2[15] & 255);
            }
            if (bArr2[4] == 109 && bArr2[5] == 111 && bArr2[6] == 111 && bArr2[7] == 118) {
                return (long) (-atomSize);
            }
            if (((long) atomSize) + atomOffset2 >= partOffset + ((long) partSize)) {
                return ((long) atomSize) + atomOffset2;
            }
            atomOffset2 += (long) atomSize;
        }
        return 0;
    }

    private void requestFileOffsets(long offset) {
        if (!this.requestingCdnOffsets) {
            this.requestingCdnOffsets = true;
            TLRPC.TL_upload_getCdnFileHashes req = new TLRPC.TL_upload_getCdnFileHashes();
            req.file_token = this.cdnToken;
            req.offset = offset;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new FileLoadOperation$$ExternalSyntheticLambda3(this), (QuickAckDelegate) null, (WriteToSocketDelegate) null, 0, this.datacenterId, 1, true);
        }
    }

    /* renamed from: lambda$requestFileOffsets$9$org-telegram-messenger-FileLoadOperation  reason: not valid java name */
    public /* synthetic */ void m1809x6ac3var_(TLObject response, TLRPC.TL_error error) {
        if (error != null) {
            onFail(false, 0);
            return;
        }
        this.requestingCdnOffsets = false;
        TLRPC.Vector vector = (TLRPC.Vector) response;
        if (!vector.objects.isEmpty()) {
            if (this.cdnHashes == null) {
                this.cdnHashes = new HashMap<>();
            }
            for (int a = 0; a < vector.objects.size(); a++) {
                TLRPC.TL_fileHash hash = (TLRPC.TL_fileHash) vector.objects.get(a);
                this.cdnHashes.put(Long.valueOf(hash.offset), hash);
            }
        }
        int a2 = 0;
        while (a2 < this.delayedRequestInfos.size()) {
            RequestInfo delayedRequestInfo = this.delayedRequestInfos.get(a2);
            if (this.notLoadedBytesRanges != null || this.downloadedBytes == delayedRequestInfo.offset) {
                this.delayedRequestInfos.remove(a2);
                if (processRequestResult(delayedRequestInfo, (TLRPC.TL_error) null)) {
                    return;
                }
                if (delayedRequestInfo.response != null) {
                    delayedRequestInfo.response.disableFree = false;
                    delayedRequestInfo.response.freeResources();
                    return;
                } else if (delayedRequestInfo.responseWeb != null) {
                    delayedRequestInfo.responseWeb.disableFree = false;
                    delayedRequestInfo.responseWeb.freeResources();
                    return;
                } else if (delayedRequestInfo.responseCdn != null) {
                    delayedRequestInfo.responseCdn.disableFree = false;
                    delayedRequestInfo.responseCdn.freeResources();
                    return;
                } else {
                    return;
                }
            } else {
                a2++;
            }
        }
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Code restructure failed: missing block: B:204:0x0599, code lost:
        r8.delayedRequestInfos.remove(r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:205:0x05a3, code lost:
        if (processRequestResult(r2, (org.telegram.tgnet.TLRPC.TL_error) null) != false) goto L_0x05e3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:207:0x05a9, code lost:
        if (org.telegram.messenger.FileLoadOperation.RequestInfo.access$400(r2) == null) goto L_0x05ba;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:208:0x05ab, code lost:
        org.telegram.messenger.FileLoadOperation.RequestInfo.access$400(r2).disableFree = false;
        org.telegram.messenger.FileLoadOperation.RequestInfo.access$400(r2).freeResources();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:210:0x05be, code lost:
        if (org.telegram.messenger.FileLoadOperation.RequestInfo.access$500(r2) == null) goto L_0x05cf;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:211:0x05c0, code lost:
        org.telegram.messenger.FileLoadOperation.RequestInfo.access$500(r2).disableFree = false;
        org.telegram.messenger.FileLoadOperation.RequestInfo.access$500(r2).freeResources();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:213:0x05d3, code lost:
        if (org.telegram.messenger.FileLoadOperation.RequestInfo.access$600(r2) == null) goto L_0x05e3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:214:0x05d5, code lost:
        org.telegram.messenger.FileLoadOperation.RequestInfo.access$600(r2).disableFree = false;
        org.telegram.messenger.FileLoadOperation.RequestInfo.access$600(r2).freeResources();
     */
    /* JADX WARNING: Removed duplicated region for block: B:75:0x021c A[Catch:{ Exception -> 0x05f6 }] */
    /* JADX WARNING: Removed duplicated region for block: B:76:0x0228 A[Catch:{ Exception -> 0x05f6 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean processRequestResult(org.telegram.messenger.FileLoadOperation.RequestInfo r46, org.telegram.tgnet.TLRPC.TL_error r47) {
        /*
            r45 = this;
            r8 = r45
            r9 = r47
            int r0 = r8.state
            java.lang.String r1 = " offset "
            r10 = 1
            r11 = 0
            if (r0 == r10) goto L_0x0031
            boolean r0 = org.telegram.messenger.BuildVars.DEBUG_VERSION
            if (r0 == 0) goto L_0x0030
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r2 = "trying to write to finished file "
            r0.append(r2)
            java.io.File r2 = r8.cacheFileFinal
            r0.append(r2)
            r0.append(r1)
            long r1 = r46.offset
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            org.telegram.messenger.FileLog.d(r0)
        L_0x0030:
            return r11
        L_0x0031:
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$RequestInfo> r0 = r8.requestInfos
            r12 = r46
            r0.remove(r12)
            java.lang.String r0 = " secret = "
            java.lang.String r13 = " volume_id = "
            java.lang.String r14 = " access_hash = "
            java.lang.String r15 = " local_id = "
            java.lang.String r5 = " id = "
            if (r9 != 0) goto L_0x0601
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$Range> r4 = r8.notLoadedBytesRanges     // Catch:{ Exception -> 0x05f6 }
            if (r4 != 0) goto L_0x0056
            long r2 = r8.downloadedBytes     // Catch:{ Exception -> 0x05f6 }
            long r18 = r46.offset     // Catch:{ Exception -> 0x05f6 }
            int r4 = (r2 > r18 ? 1 : (r2 == r18 ? 0 : -1))
            if (r4 == 0) goto L_0x0056
            r45.delayRequestInfo(r46)     // Catch:{ Exception -> 0x05f6 }
            return r11
        L_0x0056:
            org.telegram.tgnet.TLRPC$TL_upload_file r2 = r46.response     // Catch:{ Exception -> 0x05f6 }
            if (r2 == 0) goto L_0x0064
            org.telegram.tgnet.TLRPC$TL_upload_file r2 = r46.response     // Catch:{ Exception -> 0x05f6 }
            org.telegram.tgnet.NativeByteBuffer r2 = r2.bytes     // Catch:{ Exception -> 0x05f6 }
            r6 = r2
            goto L_0x0082
        L_0x0064:
            org.telegram.tgnet.TLRPC$TL_upload_webFile r2 = r46.responseWeb     // Catch:{ Exception -> 0x05f6 }
            if (r2 == 0) goto L_0x0072
            org.telegram.tgnet.TLRPC$TL_upload_webFile r2 = r46.responseWeb     // Catch:{ Exception -> 0x05f6 }
            org.telegram.tgnet.NativeByteBuffer r2 = r2.bytes     // Catch:{ Exception -> 0x05f6 }
            r6 = r2
            goto L_0x0082
        L_0x0072:
            org.telegram.tgnet.TLRPC$TL_upload_cdnFile r2 = r46.responseCdn     // Catch:{ Exception -> 0x05f6 }
            if (r2 == 0) goto L_0x0080
            org.telegram.tgnet.TLRPC$TL_upload_cdnFile r2 = r46.responseCdn     // Catch:{ Exception -> 0x05f6 }
            org.telegram.tgnet.NativeByteBuffer r2 = r2.bytes     // Catch:{ Exception -> 0x05f6 }
            r6 = r2
            goto L_0x0082
        L_0x0080:
            r2 = 0
            r6 = r2
        L_0x0082:
            if (r6 == 0) goto L_0x05ee
            int r2 = r6.limit()     // Catch:{ Exception -> 0x05f6 }
            if (r2 != 0) goto L_0x008e
            r18 = r6
            goto L_0x05f0
        L_0x008e:
            int r2 = r6.limit()     // Catch:{ Exception -> 0x05f6 }
            r4 = r2
            boolean r2 = r8.isCdn     // Catch:{ Exception -> 0x05f6 }
            if (r2 == 0) goto L_0x00bc
            long r2 = r46.offset     // Catch:{ Exception -> 0x05f6 }
            int r7 = r8.cdnChunkCheckSize     // Catch:{ Exception -> 0x05f6 }
            long r11 = (long) r7     // Catch:{ Exception -> 0x05f6 }
            long r2 = r2 / r11
            long r11 = (long) r7     // Catch:{ Exception -> 0x05f6 }
            long r11 = r11 * r2
            java.util.HashMap<java.lang.Long, org.telegram.tgnet.TLRPC$TL_fileHash> r7 = r8.cdnHashes     // Catch:{ Exception -> 0x05f6 }
            if (r7 == 0) goto L_0x00b1
            java.lang.Long r10 = java.lang.Long.valueOf(r11)     // Catch:{ Exception -> 0x05f6 }
            java.lang.Object r7 = r7.get(r10)     // Catch:{ Exception -> 0x05f6 }
            org.telegram.tgnet.TLRPC$TL_fileHash r7 = (org.telegram.tgnet.TLRPC.TL_fileHash) r7     // Catch:{ Exception -> 0x05f6 }
            goto L_0x00b2
        L_0x00b1:
            r7 = 0
        L_0x00b2:
            if (r7 != 0) goto L_0x00bc
            r45.delayRequestInfo(r46)     // Catch:{ Exception -> 0x05f6 }
            r8.requestFileOffsets(r11)     // Catch:{ Exception -> 0x05f6 }
            r0 = 1
            return r0
        L_0x00bc:
            org.telegram.tgnet.TLRPC$TL_upload_cdnFile r2 = r46.responseCdn     // Catch:{ Exception -> 0x05f6 }
            r12 = 8
            r20 = 14
            r21 = 15
            r22 = 16
            r24 = 24
            r25 = 16
            r26 = 255(0xff, double:1.26E-321)
            if (r2 == 0) goto L_0x010a
            long r2 = r46.offset     // Catch:{ Exception -> 0x05f6 }
            long r2 = r2 / r22
            byte[] r7 = r8.cdnIv     // Catch:{ Exception -> 0x05f6 }
            long r10 = r2 & r26
            int r11 = (int) r10     // Catch:{ Exception -> 0x05f6 }
            byte r10 = (byte) r11     // Catch:{ Exception -> 0x05f6 }
            r7[r21] = r10     // Catch:{ Exception -> 0x05f6 }
            long r10 = r2 >> r12
            long r10 = r10 & r26
            int r11 = (int) r10     // Catch:{ Exception -> 0x05f6 }
            byte r10 = (byte) r11     // Catch:{ Exception -> 0x05f6 }
            r7[r20] = r10     // Catch:{ Exception -> 0x05f6 }
            long r10 = r2 >> r25
            long r10 = r10 & r26
            int r11 = (int) r10     // Catch:{ Exception -> 0x05f6 }
            byte r10 = (byte) r11     // Catch:{ Exception -> 0x05f6 }
            r11 = 13
            r7[r11] = r10     // Catch:{ Exception -> 0x05f6 }
            long r10 = r2 >> r24
            long r10 = r10 & r26
            int r11 = (int) r10     // Catch:{ Exception -> 0x05f6 }
            byte r10 = (byte) r11     // Catch:{ Exception -> 0x05f6 }
            r11 = 12
            r7[r11] = r10     // Catch:{ Exception -> 0x05f6 }
            java.nio.ByteBuffer r7 = r6.buffer     // Catch:{ Exception -> 0x05f6 }
            byte[] r10 = r8.cdnKey     // Catch:{ Exception -> 0x05f6 }
            byte[] r11 = r8.cdnIv     // Catch:{ Exception -> 0x05f6 }
            int r12 = r6.limit()     // Catch:{ Exception -> 0x05f6 }
            r28 = r2
            r2 = 0
            org.telegram.messenger.Utilities.aesCtrDecryption(r7, r10, r11, r2, r12)     // Catch:{ Exception -> 0x05f6 }
        L_0x010a:
            boolean r2 = r8.isPreloadVideoOperation     // Catch:{ Exception -> 0x05f6 }
            if (r2 == 0) goto L_0x023a
            java.io.RandomAccessFile r0 = r8.preloadStream     // Catch:{ Exception -> 0x05f6 }
            long r2 = r46.offset     // Catch:{ Exception -> 0x05f6 }
            r0.writeLong(r2)     // Catch:{ Exception -> 0x05f6 }
            java.io.RandomAccessFile r0 = r8.preloadStream     // Catch:{ Exception -> 0x05f6 }
            long r2 = (long) r4     // Catch:{ Exception -> 0x05f6 }
            r0.writeLong(r2)     // Catch:{ Exception -> 0x05f6 }
            int r0 = r8.preloadStreamFileOffset     // Catch:{ Exception -> 0x05f6 }
            int r0 = r0 + 16
            r8.preloadStreamFileOffset = r0     // Catch:{ Exception -> 0x05f6 }
            java.io.RandomAccessFile r0 = r8.preloadStream     // Catch:{ Exception -> 0x05f6 }
            java.nio.channels.FileChannel r0 = r0.getChannel()     // Catch:{ Exception -> 0x05f6 }
            java.nio.ByteBuffer r2 = r6.buffer     // Catch:{ Exception -> 0x05f6 }
            r0.write(r2)     // Catch:{ Exception -> 0x05f6 }
            boolean r2 = org.telegram.messenger.BuildVars.DEBUG_VERSION     // Catch:{ Exception -> 0x05f6 }
            if (r2 == 0) goto L_0x015a
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x05f6 }
            r2.<init>()     // Catch:{ Exception -> 0x05f6 }
            java.lang.String r3 = "save preload file part "
            r2.append(r3)     // Catch:{ Exception -> 0x05f6 }
            java.io.File r3 = r8.cacheFilePreload     // Catch:{ Exception -> 0x05f6 }
            r2.append(r3)     // Catch:{ Exception -> 0x05f6 }
            r2.append(r1)     // Catch:{ Exception -> 0x05f6 }
            long r10 = r46.offset     // Catch:{ Exception -> 0x05f6 }
            r2.append(r10)     // Catch:{ Exception -> 0x05f6 }
            java.lang.String r1 = " size "
            r2.append(r1)     // Catch:{ Exception -> 0x05f6 }
            r2.append(r4)     // Catch:{ Exception -> 0x05f6 }
            java.lang.String r1 = r2.toString()     // Catch:{ Exception -> 0x05f6 }
            org.telegram.messenger.FileLog.d(r1)     // Catch:{ Exception -> 0x05f6 }
        L_0x015a:
            java.util.HashMap<java.lang.Long, org.telegram.messenger.FileLoadOperation$PreloadRange> r1 = r8.preloadedBytesRanges     // Catch:{ Exception -> 0x05f6 }
            if (r1 != 0) goto L_0x0165
            java.util.HashMap r1 = new java.util.HashMap     // Catch:{ Exception -> 0x05f6 }
            r1.<init>()     // Catch:{ Exception -> 0x05f6 }
            r8.preloadedBytesRanges = r1     // Catch:{ Exception -> 0x05f6 }
        L_0x0165:
            java.util.HashMap<java.lang.Long, org.telegram.messenger.FileLoadOperation$PreloadRange> r1 = r8.preloadedBytesRanges     // Catch:{ Exception -> 0x05f6 }
            long r2 = r46.offset     // Catch:{ Exception -> 0x05f6 }
            java.lang.Long r2 = java.lang.Long.valueOf(r2)     // Catch:{ Exception -> 0x05f6 }
            org.telegram.messenger.FileLoadOperation$PreloadRange r3 = new org.telegram.messenger.FileLoadOperation$PreloadRange     // Catch:{ Exception -> 0x05f6 }
            int r5 = r8.preloadStreamFileOffset     // Catch:{ Exception -> 0x05f6 }
            long r11 = (long) r5     // Catch:{ Exception -> 0x05f6 }
            long r13 = (long) r4     // Catch:{ Exception -> 0x05f6 }
            r15 = 0
            r10 = r3
            r10.<init>(r11, r13)     // Catch:{ Exception -> 0x05f6 }
            r1.put(r2, r3)     // Catch:{ Exception -> 0x05f6 }
            int r1 = r8.totalPreloadedBytes     // Catch:{ Exception -> 0x05f6 }
            int r1 = r1 + r4
            r8.totalPreloadedBytes = r1     // Catch:{ Exception -> 0x05f6 }
            int r1 = r8.preloadStreamFileOffset     // Catch:{ Exception -> 0x05f6 }
            int r1 = r1 + r4
            r8.preloadStreamFileOffset = r1     // Catch:{ Exception -> 0x05f6 }
            int r1 = r8.moovFound     // Catch:{ Exception -> 0x05f6 }
            if (r1 != 0) goto L_0x01db
            long r2 = r8.nextAtomOffset     // Catch:{ Exception -> 0x05f6 }
            long r10 = r46.offset     // Catch:{ Exception -> 0x05f6 }
            r1 = r45
            r12 = 0
            r7 = r4
            r4 = r10
            r10 = r6
            long r1 = r1.findNextPreloadDownloadOffset(r2, r4, r6)     // Catch:{ Exception -> 0x05f6 }
            int r3 = (r1 > r12 ? 1 : (r1 == r12 ? 0 : -1))
            if (r3 >= 0) goto L_0x01d0
            r3 = -1
            long r1 = r1 * r3
            long r3 = r8.nextPreloadDownloadOffset     // Catch:{ Exception -> 0x05f6 }
            int r5 = r8.currentDownloadChunkSize     // Catch:{ Exception -> 0x05f6 }
            long r5 = (long) r5     // Catch:{ Exception -> 0x05f6 }
            long r3 = r3 + r5
            r8.nextPreloadDownloadOffset = r3     // Catch:{ Exception -> 0x05f6 }
            long r5 = r8.totalBytesCount     // Catch:{ Exception -> 0x05f6 }
            r14 = 2
            long r5 = r5 / r14
            int r11 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r11 >= 0) goto L_0x01c1
            r3 = 1048576(0x100000, double:5.180654E-318)
            long r3 = r3 + r1
            r8.foundMoovSize = r3     // Catch:{ Exception -> 0x05f6 }
            r8.preloadNotRequestedBytesCount = r3     // Catch:{ Exception -> 0x05f6 }
            r3 = 1
            r8.moovFound = r3     // Catch:{ Exception -> 0x05f6 }
            goto L_0x01cb
        L_0x01c1:
            r3 = 2097152(0x200000, double:1.0361308E-317)
            r8.foundMoovSize = r3     // Catch:{ Exception -> 0x05f6 }
            r8.preloadNotRequestedBytesCount = r3     // Catch:{ Exception -> 0x05f6 }
            r3 = 2
            r8.moovFound = r3     // Catch:{ Exception -> 0x05f6 }
        L_0x01cb:
            r3 = -1
            r8.nextPreloadDownloadOffset = r3     // Catch:{ Exception -> 0x05f6 }
            goto L_0x01d8
        L_0x01d0:
            long r3 = r8.nextPreloadDownloadOffset     // Catch:{ Exception -> 0x05f6 }
            int r5 = r8.currentDownloadChunkSize     // Catch:{ Exception -> 0x05f6 }
            long r5 = (long) r5     // Catch:{ Exception -> 0x05f6 }
            long r3 = r3 + r5
            r8.nextPreloadDownloadOffset = r3     // Catch:{ Exception -> 0x05f6 }
        L_0x01d8:
            r8.nextAtomOffset = r1     // Catch:{ Exception -> 0x05f6 }
            goto L_0x01df
        L_0x01db:
            r7 = r4
            r10 = r6
            r12 = 0
        L_0x01df:
            java.io.RandomAccessFile r1 = r8.preloadStream     // Catch:{ Exception -> 0x05f6 }
            long r2 = r8.foundMoovSize     // Catch:{ Exception -> 0x05f6 }
            r1.writeLong(r2)     // Catch:{ Exception -> 0x05f6 }
            java.io.RandomAccessFile r1 = r8.preloadStream     // Catch:{ Exception -> 0x05f6 }
            long r2 = r8.nextPreloadDownloadOffset     // Catch:{ Exception -> 0x05f6 }
            r1.writeLong(r2)     // Catch:{ Exception -> 0x05f6 }
            java.io.RandomAccessFile r1 = r8.preloadStream     // Catch:{ Exception -> 0x05f6 }
            long r2 = r8.nextAtomOffset     // Catch:{ Exception -> 0x05f6 }
            r1.writeLong(r2)     // Catch:{ Exception -> 0x05f6 }
            int r1 = r8.preloadStreamFileOffset     // Catch:{ Exception -> 0x05f6 }
            int r1 = r1 + 24
            r8.preloadStreamFileOffset = r1     // Catch:{ Exception -> 0x05f6 }
            long r1 = r8.nextPreloadDownloadOffset     // Catch:{ Exception -> 0x05f6 }
            int r3 = (r1 > r12 ? 1 : (r1 == r12 ? 0 : -1))
            if (r3 == 0) goto L_0x0219
            int r3 = r8.moovFound     // Catch:{ Exception -> 0x05f6 }
            if (r3 == 0) goto L_0x020a
            long r3 = r8.foundMoovSize     // Catch:{ Exception -> 0x05f6 }
            int r5 = (r3 > r12 ? 1 : (r3 == r12 ? 0 : -1))
            if (r5 < 0) goto L_0x0219
        L_0x020a:
            int r3 = r8.totalPreloadedBytes     // Catch:{ Exception -> 0x05f6 }
            r4 = 2097152(0x200000, float:2.938736E-39)
            if (r3 > r4) goto L_0x0219
            long r3 = r8.totalBytesCount     // Catch:{ Exception -> 0x05f6 }
            int r5 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r5 < 0) goto L_0x0217
            goto L_0x0219
        L_0x0217:
            r1 = 0
            goto L_0x021a
        L_0x0219:
            r1 = 1
        L_0x021a:
            if (r1 == 0) goto L_0x0228
            java.io.RandomAccessFile r2 = r8.preloadStream     // Catch:{ Exception -> 0x05f6 }
            r2.seek(r12)     // Catch:{ Exception -> 0x05f6 }
            java.io.RandomAccessFile r2 = r8.preloadStream     // Catch:{ Exception -> 0x05f6 }
            r3 = 1
            r2.write(r3)     // Catch:{ Exception -> 0x05f6 }
            goto L_0x0234
        L_0x0228:
            int r2 = r8.moovFound     // Catch:{ Exception -> 0x05f6 }
            if (r2 == 0) goto L_0x0234
            long r2 = r8.foundMoovSize     // Catch:{ Exception -> 0x05f6 }
            int r4 = r8.currentDownloadChunkSize     // Catch:{ Exception -> 0x05f6 }
            long r4 = (long) r4     // Catch:{ Exception -> 0x05f6 }
            long r2 = r2 - r4
            r8.foundMoovSize = r2     // Catch:{ Exception -> 0x05f6 }
        L_0x0234:
            r29 = r7
            r18 = r10
            goto L_0x0576
        L_0x023a:
            r7 = r4
            r10 = r6
            r11 = 0
            long r2 = r8.downloadedBytes     // Catch:{ Exception -> 0x05f6 }
            long r11 = (long) r7     // Catch:{ Exception -> 0x05f6 }
            long r2 = r2 + r11
            r8.downloadedBytes = r2     // Catch:{ Exception -> 0x05f6 }
            long r11 = r8.totalBytesCount     // Catch:{ Exception -> 0x05f6 }
            r16 = 0
            int r4 = (r11 > r16 ? 1 : (r11 == r16 ? 0 : -1))
            if (r4 <= 0) goto L_0x0256
            int r4 = (r2 > r11 ? 1 : (r2 == r11 ? 0 : -1))
            if (r4 < 0) goto L_0x0252
            r2 = 1
            goto L_0x0253
        L_0x0252:
            r2 = 0
        L_0x0253:
            r11 = r2
            r6 = r5
            goto L_0x027a
        L_0x0256:
            int r4 = r8.currentDownloadChunkSize     // Catch:{ Exception -> 0x05f6 }
            if (r7 != r4) goto L_0x0277
            int r6 = (r11 > r2 ? 1 : (r11 == r2 ? 0 : -1))
            if (r6 == 0) goto L_0x0269
            r6 = r5
            long r4 = (long) r4     // Catch:{ Exception -> 0x05f6 }
            long r4 = r2 % r4
            r16 = 0
            int r18 = (r4 > r16 ? 1 : (r4 == r16 ? 0 : -1))
            if (r18 == 0) goto L_0x0275
            goto L_0x026a
        L_0x0269:
            r6 = r5
        L_0x026a:
            r4 = 0
            int r18 = (r11 > r4 ? 1 : (r11 == r4 ? 0 : -1))
            if (r18 <= 0) goto L_0x0278
            int r4 = (r11 > r2 ? 1 : (r11 == r2 ? 0 : -1))
            if (r4 > 0) goto L_0x0275
            goto L_0x0278
        L_0x0275:
            r2 = 0
            goto L_0x0279
        L_0x0277:
            r6 = r5
        L_0x0278:
            r2 = 1
        L_0x0279:
            r11 = r2
        L_0x027a:
            byte[] r2 = r8.key     // Catch:{ Exception -> 0x05f6 }
            if (r2 == 0) goto L_0x02d0
            java.nio.ByteBuffer r2 = r10.buffer     // Catch:{ Exception -> 0x05f6 }
            byte[] r3 = r8.key     // Catch:{ Exception -> 0x05f6 }
            byte[] r4 = r8.iv     // Catch:{ Exception -> 0x05f6 }
            r31 = 0
            r32 = 1
            r33 = 0
            int r34 = r10.limit()     // Catch:{ Exception -> 0x05f6 }
            r28 = r2
            r29 = r3
            r30 = r4
            org.telegram.messenger.Utilities.aesIgeEncryption(r28, r29, r30, r31, r32, r33, r34)     // Catch:{ Exception -> 0x05f6 }
            if (r11 == 0) goto L_0x02d0
            long r2 = r8.bytesCountPadding     // Catch:{ Exception -> 0x05f6 }
            r4 = 0
            int r12 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r12 == 0) goto L_0x02d0
            int r2 = r10.limit()     // Catch:{ Exception -> 0x05f6 }
            long r2 = (long) r2     // Catch:{ Exception -> 0x05f6 }
            long r4 = r8.bytesCountPadding     // Catch:{ Exception -> 0x05f6 }
            long r2 = r2 - r4
            boolean r4 = org.telegram.messenger.BuildVars.DEBUG_VERSION     // Catch:{ Exception -> 0x05f6 }
            if (r4 == 0) goto L_0x02cc
            r4 = 2147483647(0x7fffffff, double:1.060997895E-314)
            int r12 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r12 > 0) goto L_0x02b5
            goto L_0x02cc
        L_0x02b5:
            java.lang.RuntimeException r0 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x05f6 }
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x05f6 }
            r1.<init>()     // Catch:{ Exception -> 0x05f6 }
            java.lang.String r4 = "Out of limit"
            r1.append(r4)     // Catch:{ Exception -> 0x05f6 }
            r1.append(r2)     // Catch:{ Exception -> 0x05f6 }
            java.lang.String r1 = r1.toString()     // Catch:{ Exception -> 0x05f6 }
            r0.<init>(r1)     // Catch:{ Exception -> 0x05f6 }
            throw r0     // Catch:{ Exception -> 0x05f6 }
        L_0x02cc:
            int r4 = (int) r2     // Catch:{ Exception -> 0x05f6 }
            r10.limit(r4)     // Catch:{ Exception -> 0x05f6 }
        L_0x02d0:
            boolean r2 = r8.encryptFile     // Catch:{ Exception -> 0x05f6 }
            if (r2 == 0) goto L_0x0312
            long r2 = r46.offset     // Catch:{ Exception -> 0x05f6 }
            long r2 = r2 / r22
            byte[] r4 = r8.encryptIv     // Catch:{ Exception -> 0x05f6 }
            r12 = r6
            long r5 = r2 & r26
            int r6 = (int) r5     // Catch:{ Exception -> 0x05f6 }
            byte r5 = (byte) r6     // Catch:{ Exception -> 0x05f6 }
            r4[r21] = r5     // Catch:{ Exception -> 0x05f6 }
            r5 = 8
            long r28 = r2 >> r5
            long r5 = r28 & r26
            int r6 = (int) r5     // Catch:{ Exception -> 0x05f6 }
            byte r5 = (byte) r6     // Catch:{ Exception -> 0x05f6 }
            r4[r20] = r5     // Catch:{ Exception -> 0x05f6 }
            long r5 = r2 >> r25
            long r5 = r5 & r26
            int r6 = (int) r5     // Catch:{ Exception -> 0x05f6 }
            byte r5 = (byte) r6     // Catch:{ Exception -> 0x05f6 }
            r6 = 13
            r4[r6] = r5     // Catch:{ Exception -> 0x05f6 }
            long r5 = r2 >> r24
            long r5 = r5 & r26
            int r6 = (int) r5     // Catch:{ Exception -> 0x05f6 }
            byte r5 = (byte) r6     // Catch:{ Exception -> 0x05f6 }
            r6 = 12
            r4[r6] = r5     // Catch:{ Exception -> 0x05f6 }
            java.nio.ByteBuffer r4 = r10.buffer     // Catch:{ Exception -> 0x05f6 }
            byte[] r5 = r8.encryptKey     // Catch:{ Exception -> 0x05f6 }
            byte[] r6 = r8.encryptIv     // Catch:{ Exception -> 0x05f6 }
            r28 = r2
            int r2 = r10.limit()     // Catch:{ Exception -> 0x05f6 }
            r3 = 0
            org.telegram.messenger.Utilities.aesCtrDecryption(r4, r5, r6, r3, r2)     // Catch:{ Exception -> 0x05f6 }
            goto L_0x0313
        L_0x0312:
            r12 = r6
        L_0x0313:
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$Range> r2 = r8.notLoadedBytesRanges     // Catch:{ Exception -> 0x05f6 }
            if (r2 == 0) goto L_0x0344
            java.io.RandomAccessFile r2 = r8.fileOutputStream     // Catch:{ Exception -> 0x05f6 }
            long r3 = r46.offset     // Catch:{ Exception -> 0x05f6 }
            r2.seek(r3)     // Catch:{ Exception -> 0x05f6 }
            boolean r2 = org.telegram.messenger.BuildVars.DEBUG_VERSION     // Catch:{ Exception -> 0x05f6 }
            if (r2 == 0) goto L_0x0344
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x05f6 }
            r2.<init>()     // Catch:{ Exception -> 0x05f6 }
            java.lang.String r3 = "save file part "
            r2.append(r3)     // Catch:{ Exception -> 0x05f6 }
            java.io.File r3 = r8.cacheFileFinal     // Catch:{ Exception -> 0x05f6 }
            r2.append(r3)     // Catch:{ Exception -> 0x05f6 }
            r2.append(r1)     // Catch:{ Exception -> 0x05f6 }
            long r3 = r46.offset     // Catch:{ Exception -> 0x05f6 }
            r2.append(r3)     // Catch:{ Exception -> 0x05f6 }
            java.lang.String r1 = r2.toString()     // Catch:{ Exception -> 0x05f6 }
            org.telegram.messenger.FileLog.d(r1)     // Catch:{ Exception -> 0x05f6 }
        L_0x0344:
            java.io.RandomAccessFile r1 = r8.fileOutputStream     // Catch:{ Exception -> 0x05f6 }
            java.nio.channels.FileChannel r1 = r1.getChannel()     // Catch:{ Exception -> 0x05f6 }
            r5 = r1
            java.nio.ByteBuffer r1 = r10.buffer     // Catch:{ Exception -> 0x05f6 }
            r5.write(r1)     // Catch:{ Exception -> 0x05f6 }
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$Range> r2 = r8.notLoadedBytesRanges     // Catch:{ Exception -> 0x05f6 }
            long r3 = r46.offset     // Catch:{ Exception -> 0x05f6 }
            long r28 = r46.offset     // Catch:{ Exception -> 0x05f6 }
            r18 = r5
            long r5 = (long) r7     // Catch:{ Exception -> 0x05f6 }
            long r5 = r28 + r5
            r28 = 1
            r1 = r45
            r44 = r18
            r18 = r10
            r10 = r12
            r12 = r44
            r29 = r7
            r7 = r28
            r1.addPart(r2, r3, r5, r7)     // Catch:{ Exception -> 0x05f6 }
            boolean r1 = r8.isCdn     // Catch:{ Exception -> 0x05f6 }
            if (r1 == 0) goto L_0x0545
            long r1 = r46.offset     // Catch:{ Exception -> 0x05f6 }
            int r3 = r8.cdnChunkCheckSize     // Catch:{ Exception -> 0x05f6 }
            long r3 = (long) r3     // Catch:{ Exception -> 0x05f6 }
            long r1 = r1 / r3
            r30 = r1
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$Range> r1 = r8.notCheckedCdnRanges     // Catch:{ Exception -> 0x05f6 }
            int r1 = r1.size()     // Catch:{ Exception -> 0x05f6 }
            r7 = r1
            r1 = 1
            r2 = 0
        L_0x0388:
            if (r2 >= r7) goto L_0x03a9
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$Range> r3 = r8.notCheckedCdnRanges     // Catch:{ Exception -> 0x05f6 }
            java.lang.Object r3 = r3.get(r2)     // Catch:{ Exception -> 0x05f6 }
            org.telegram.messenger.FileLoadOperation$Range r3 = (org.telegram.messenger.FileLoadOperation.Range) r3     // Catch:{ Exception -> 0x05f6 }
            long r4 = r3.start     // Catch:{ Exception -> 0x05f6 }
            int r6 = (r4 > r30 ? 1 : (r4 == r30 ? 0 : -1))
            if (r6 > 0) goto L_0x03a6
            long r4 = r3.end     // Catch:{ Exception -> 0x05f6 }
            int r6 = (r30 > r4 ? 1 : (r30 == r4 ? 0 : -1))
            if (r6 > 0) goto L_0x03a6
            r1 = 0
            r28 = r1
            goto L_0x03ab
        L_0x03a6:
            int r2 = r2 + 1
            goto L_0x0388
        L_0x03a9:
            r28 = r1
        L_0x03ab:
            if (r28 != 0) goto L_0x053f
            int r1 = r8.cdnChunkCheckSize     // Catch:{ Exception -> 0x05f6 }
            long r2 = (long) r1     // Catch:{ Exception -> 0x05f6 }
            long r5 = r30 * r2
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$Range> r2 = r8.notLoadedBytesRanges     // Catch:{ Exception -> 0x05f6 }
            long r3 = (long) r1     // Catch:{ Exception -> 0x05f6 }
            r1 = r45
            r32 = r3
            r3 = r5
            r35 = r5
            r5 = r32
            long r1 = r1.getDownloadedLengthFromOffsetInternal(r2, r3, r5)     // Catch:{ Exception -> 0x05f6 }
            r5 = r1
            r1 = 0
            int r3 = (r5 > r1 ? 1 : (r5 == r1 ? 0 : -1))
            if (r3 == 0) goto L_0x0535
            int r1 = r8.cdnChunkCheckSize     // Catch:{ Exception -> 0x05f6 }
            long r1 = (long) r1     // Catch:{ Exception -> 0x05f6 }
            int r3 = (r5 > r1 ? 1 : (r5 == r1 ? 0 : -1))
            if (r3 == 0) goto L_0x03f2
            long r1 = r8.totalBytesCount     // Catch:{ Exception -> 0x05f6 }
            r3 = 0
            int r32 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r32 <= 0) goto L_0x03e1
            r3 = r35
            long r32 = r1 - r3
            int r34 = (r5 > r32 ? 1 : (r5 == r32 ? 0 : -1))
            if (r34 == 0) goto L_0x03f4
            goto L_0x03e3
        L_0x03e1:
            r3 = r35
        L_0x03e3:
            r16 = 0
            int r32 = (r1 > r16 ? 1 : (r1 == r16 ? 0 : -1))
            if (r32 > 0) goto L_0x03ec
            if (r11 == 0) goto L_0x03ec
            goto L_0x03f4
        L_0x03ec:
            r33 = r11
            r34 = r12
            goto L_0x0549
        L_0x03f2:
            r3 = r35
        L_0x03f4:
            java.util.HashMap<java.lang.Long, org.telegram.tgnet.TLRPC$TL_fileHash> r1 = r8.cdnHashes     // Catch:{ Exception -> 0x05f6 }
            java.lang.Long r2 = java.lang.Long.valueOf(r3)     // Catch:{ Exception -> 0x05f6 }
            java.lang.Object r1 = r1.get(r2)     // Catch:{ Exception -> 0x05f6 }
            org.telegram.tgnet.TLRPC$TL_fileHash r1 = (org.telegram.tgnet.TLRPC.TL_fileHash) r1     // Catch:{ Exception -> 0x05f6 }
            r2 = r1
            java.io.RandomAccessFile r1 = r8.fileReadStream     // Catch:{ Exception -> 0x05f6 }
            if (r1 != 0) goto L_0x041b
            int r1 = r8.cdnChunkCheckSize     // Catch:{ Exception -> 0x05f6 }
            byte[] r1 = new byte[r1]     // Catch:{ Exception -> 0x05f6 }
            r8.cdnCheckBytes = r1     // Catch:{ Exception -> 0x05f6 }
            java.io.RandomAccessFile r1 = new java.io.RandomAccessFile     // Catch:{ Exception -> 0x05f6 }
            r32 = r7
            java.io.File r7 = r8.cacheFileTemp     // Catch:{ Exception -> 0x05f6 }
            r33 = r11
            java.lang.String r11 = "r"
            r1.<init>(r7, r11)     // Catch:{ Exception -> 0x05f6 }
            r8.fileReadStream = r1     // Catch:{ Exception -> 0x05f6 }
            goto L_0x041f
        L_0x041b:
            r32 = r7
            r33 = r11
        L_0x041f:
            java.io.RandomAccessFile r1 = r8.fileReadStream     // Catch:{ Exception -> 0x05f6 }
            r1.seek(r3)     // Catch:{ Exception -> 0x05f6 }
            boolean r1 = org.telegram.messenger.BuildVars.DEBUG_VERSION     // Catch:{ Exception -> 0x05f6 }
            if (r1 == 0) goto L_0x0438
            r34 = 2147483647(0x7fffffff, double:1.060997895E-314)
            int r1 = (r5 > r34 ? 1 : (r5 == r34 ? 0 : -1))
            if (r1 > 0) goto L_0x0430
            goto L_0x0438
        L_0x0430:
            java.lang.RuntimeException r0 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x05f6 }
            java.lang.String r1 = "!!!"
            r0.<init>(r1)     // Catch:{ Exception -> 0x05f6 }
            throw r0     // Catch:{ Exception -> 0x05f6 }
        L_0x0438:
            java.io.RandomAccessFile r1 = r8.fileReadStream     // Catch:{ Exception -> 0x05f6 }
            byte[] r7 = r8.cdnCheckBytes     // Catch:{ Exception -> 0x05f6 }
            int r11 = (int) r5     // Catch:{ Exception -> 0x05f6 }
            r34 = r12
            r12 = 0
            r1.readFully(r7, r12, r11)     // Catch:{ Exception -> 0x05f6 }
            boolean r1 = r8.encryptFile     // Catch:{ Exception -> 0x05f6 }
            if (r1 == 0) goto L_0x0485
            long r11 = r3 / r22
            byte[] r1 = r8.encryptIv     // Catch:{ Exception -> 0x05f6 }
            r35 = r3
            long r3 = r11 & r26
            int r4 = (int) r3     // Catch:{ Exception -> 0x05f6 }
            byte r3 = (byte) r4     // Catch:{ Exception -> 0x05f6 }
            r1[r21] = r3     // Catch:{ Exception -> 0x05f6 }
            r3 = 8
            long r3 = r11 >> r3
            long r3 = r3 & r26
            int r4 = (int) r3     // Catch:{ Exception -> 0x05f6 }
            byte r3 = (byte) r4     // Catch:{ Exception -> 0x05f6 }
            r1[r20] = r3     // Catch:{ Exception -> 0x05f6 }
            long r3 = r11 >> r25
            long r3 = r3 & r26
            int r4 = (int) r3     // Catch:{ Exception -> 0x05f6 }
            byte r3 = (byte) r4     // Catch:{ Exception -> 0x05f6 }
            r4 = 13
            r1[r4] = r3     // Catch:{ Exception -> 0x05f6 }
            long r3 = r11 >> r24
            long r3 = r3 & r26
            int r4 = (int) r3     // Catch:{ Exception -> 0x05f6 }
            byte r3 = (byte) r4     // Catch:{ Exception -> 0x05f6 }
            r4 = 12
            r1[r4] = r3     // Catch:{ Exception -> 0x05f6 }
            byte[] r3 = r8.cdnCheckBytes     // Catch:{ Exception -> 0x05f6 }
            byte[] r4 = r8.encryptKey     // Catch:{ Exception -> 0x05f6 }
            r40 = 0
            r43 = 0
            r37 = r3
            r38 = r4
            r39 = r1
            r41 = r5
            org.telegram.messenger.Utilities.aesCtrDecryptionByteArray(r37, r38, r39, r40, r41, r43)     // Catch:{ Exception -> 0x05f6 }
            goto L_0x0487
        L_0x0485:
            r35 = r3
        L_0x0487:
            byte[] r1 = r8.cdnCheckBytes     // Catch:{ Exception -> 0x05f6 }
            r3 = 0
            byte[] r1 = org.telegram.messenger.Utilities.computeSHA256(r1, r3, r5)     // Catch:{ Exception -> 0x05f6 }
            r11 = r1
            byte[] r1 = r2.hash     // Catch:{ Exception -> 0x05f6 }
            boolean r1 = java.util.Arrays.equals(r11, r1)     // Catch:{ Exception -> 0x05f6 }
            if (r1 != 0) goto L_0x0514
            boolean r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x05f6 }
            if (r1 == 0) goto L_0x050a
            org.telegram.tgnet.TLRPC$InputFileLocation r1 = r8.location     // Catch:{ Exception -> 0x05f6 }
            if (r1 == 0) goto L_0x04e8
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x05f6 }
            r1.<init>()     // Catch:{ Exception -> 0x05f6 }
            java.lang.String r3 = "invalid cdn hash "
            r1.append(r3)     // Catch:{ Exception -> 0x05f6 }
            org.telegram.tgnet.TLRPC$InputFileLocation r3 = r8.location     // Catch:{ Exception -> 0x05f6 }
            r1.append(r3)     // Catch:{ Exception -> 0x05f6 }
            r1.append(r10)     // Catch:{ Exception -> 0x05f6 }
            org.telegram.tgnet.TLRPC$InputFileLocation r3 = r8.location     // Catch:{ Exception -> 0x05f6 }
            long r3 = r3.id     // Catch:{ Exception -> 0x05f6 }
            r1.append(r3)     // Catch:{ Exception -> 0x05f6 }
            r1.append(r15)     // Catch:{ Exception -> 0x05f6 }
            org.telegram.tgnet.TLRPC$InputFileLocation r3 = r8.location     // Catch:{ Exception -> 0x05f6 }
            int r3 = r3.local_id     // Catch:{ Exception -> 0x05f6 }
            r1.append(r3)     // Catch:{ Exception -> 0x05f6 }
            r1.append(r14)     // Catch:{ Exception -> 0x05f6 }
            org.telegram.tgnet.TLRPC$InputFileLocation r3 = r8.location     // Catch:{ Exception -> 0x05f6 }
            long r3 = r3.access_hash     // Catch:{ Exception -> 0x05f6 }
            r1.append(r3)     // Catch:{ Exception -> 0x05f6 }
            r1.append(r13)     // Catch:{ Exception -> 0x05f6 }
            org.telegram.tgnet.TLRPC$InputFileLocation r3 = r8.location     // Catch:{ Exception -> 0x05f6 }
            long r3 = r3.volume_id     // Catch:{ Exception -> 0x05f6 }
            r1.append(r3)     // Catch:{ Exception -> 0x05f6 }
            r1.append(r0)     // Catch:{ Exception -> 0x05f6 }
            org.telegram.tgnet.TLRPC$InputFileLocation r0 = r8.location     // Catch:{ Exception -> 0x05f6 }
            long r3 = r0.secret     // Catch:{ Exception -> 0x05f6 }
            r1.append(r3)     // Catch:{ Exception -> 0x05f6 }
            java.lang.String r0 = r1.toString()     // Catch:{ Exception -> 0x05f6 }
            org.telegram.messenger.FileLog.e((java.lang.String) r0)     // Catch:{ Exception -> 0x05f6 }
            goto L_0x050a
        L_0x04e8:
            org.telegram.tgnet.TLRPC$InputWebFileLocation r0 = r8.webLocation     // Catch:{ Exception -> 0x05f6 }
            if (r0 == 0) goto L_0x050a
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x05f6 }
            r0.<init>()     // Catch:{ Exception -> 0x05f6 }
            java.lang.String r1 = "invalid cdn hash  "
            r0.append(r1)     // Catch:{ Exception -> 0x05f6 }
            org.telegram.tgnet.TLRPC$InputWebFileLocation r1 = r8.webLocation     // Catch:{ Exception -> 0x05f6 }
            r0.append(r1)     // Catch:{ Exception -> 0x05f6 }
            r0.append(r10)     // Catch:{ Exception -> 0x05f6 }
            java.lang.String r1 = r8.fileName     // Catch:{ Exception -> 0x05f6 }
            r0.append(r1)     // Catch:{ Exception -> 0x05f6 }
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x05f6 }
            org.telegram.messenger.FileLog.e((java.lang.String) r0)     // Catch:{ Exception -> 0x05f6 }
        L_0x050a:
            r1 = 0
            r8.onFail(r1, r1)     // Catch:{ Exception -> 0x05f6 }
            java.io.File r0 = r8.cacheFileTemp     // Catch:{ Exception -> 0x05f6 }
            r0.delete()     // Catch:{ Exception -> 0x05f6 }
            return r1
        L_0x0514:
            java.util.HashMap<java.lang.Long, org.telegram.tgnet.TLRPC$TL_fileHash> r0 = r8.cdnHashes     // Catch:{ Exception -> 0x05f6 }
            java.lang.Long r1 = java.lang.Long.valueOf(r35)     // Catch:{ Exception -> 0x05f6 }
            r0.remove(r1)     // Catch:{ Exception -> 0x05f6 }
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$Range> r0 = r8.notCheckedCdnRanges     // Catch:{ Exception -> 0x05f6 }
            r3 = 1
            long r12 = r30 + r3
            r7 = 0
            r1 = r45
            r10 = r2
            r2 = r0
            r14 = r35
            r3 = r30
            r20 = r5
            r5 = r12
            r0 = r32
            r1.addPart(r2, r3, r5, r7)     // Catch:{ Exception -> 0x05f6 }
            goto L_0x0549
        L_0x0535:
            r20 = r5
            r0 = r7
            r33 = r11
            r34 = r12
            r14 = r35
            goto L_0x0549
        L_0x053f:
            r0 = r7
            r33 = r11
            r34 = r12
            goto L_0x0549
        L_0x0545:
            r33 = r11
            r34 = r12
        L_0x0549:
            java.io.RandomAccessFile r0 = r8.fiv     // Catch:{ Exception -> 0x05f6 }
            if (r0 == 0) goto L_0x0559
            r1 = 0
            r0.seek(r1)     // Catch:{ Exception -> 0x05f6 }
            java.io.RandomAccessFile r0 = r8.fiv     // Catch:{ Exception -> 0x05f6 }
            byte[] r1 = r8.iv     // Catch:{ Exception -> 0x05f6 }
            r0.write(r1)     // Catch:{ Exception -> 0x05f6 }
        L_0x0559:
            long r0 = r8.totalBytesCount     // Catch:{ Exception -> 0x05f6 }
            r2 = 0
            int r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r4 <= 0) goto L_0x0574
            int r0 = r8.state     // Catch:{ Exception -> 0x05f6 }
            r1 = 1
            if (r0 != r1) goto L_0x0574
            r45.copyNotLoadedRanges()     // Catch:{ Exception -> 0x05f6 }
            org.telegram.messenger.FileLoadOperation$FileLoadOperationDelegate r1 = r8.delegate     // Catch:{ Exception -> 0x05f6 }
            long r3 = r8.downloadedBytes     // Catch:{ Exception -> 0x05f6 }
            long r5 = r8.totalBytesCount     // Catch:{ Exception -> 0x05f6 }
            r2 = r45
            r1.didChangedLoadProgress(r2, r3, r5)     // Catch:{ Exception -> 0x05f6 }
        L_0x0574:
            r1 = r33
        L_0x0576:
            r0 = 0
        L_0x0577:
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$RequestInfo> r2 = r8.delayedRequestInfos     // Catch:{ Exception -> 0x05f6 }
            int r2 = r2.size()     // Catch:{ Exception -> 0x05f6 }
            if (r0 >= r2) goto L_0x05e3
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$RequestInfo> r2 = r8.delayedRequestInfos     // Catch:{ Exception -> 0x05f6 }
            java.lang.Object r2 = r2.get(r0)     // Catch:{ Exception -> 0x05f6 }
            org.telegram.messenger.FileLoadOperation$RequestInfo r2 = (org.telegram.messenger.FileLoadOperation.RequestInfo) r2     // Catch:{ Exception -> 0x05f6 }
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$Range> r3 = r8.notLoadedBytesRanges     // Catch:{ Exception -> 0x05f6 }
            if (r3 != 0) goto L_0x0599
            long r3 = r8.downloadedBytes     // Catch:{ Exception -> 0x05f6 }
            long r5 = r2.offset     // Catch:{ Exception -> 0x05f6 }
            int r7 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r7 != 0) goto L_0x0596
            goto L_0x0599
        L_0x0596:
            int r0 = r0 + 1
            goto L_0x0577
        L_0x0599:
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$RequestInfo> r3 = r8.delayedRequestInfos     // Catch:{ Exception -> 0x05f6 }
            r3.remove(r0)     // Catch:{ Exception -> 0x05f6 }
            r3 = 0
            boolean r3 = r8.processRequestResult(r2, r3)     // Catch:{ Exception -> 0x05f6 }
            if (r3 != 0) goto L_0x05e3
            org.telegram.tgnet.TLRPC$TL_upload_file r3 = r2.response     // Catch:{ Exception -> 0x05f6 }
            if (r3 == 0) goto L_0x05ba
            org.telegram.tgnet.TLRPC$TL_upload_file r3 = r2.response     // Catch:{ Exception -> 0x05f6 }
            r4 = 0
            r3.disableFree = r4     // Catch:{ Exception -> 0x05f6 }
            org.telegram.tgnet.TLRPC$TL_upload_file r3 = r2.response     // Catch:{ Exception -> 0x05f6 }
            r3.freeResources()     // Catch:{ Exception -> 0x05f6 }
            goto L_0x05e3
        L_0x05ba:
            org.telegram.tgnet.TLRPC$TL_upload_webFile r3 = r2.responseWeb     // Catch:{ Exception -> 0x05f6 }
            if (r3 == 0) goto L_0x05cf
            org.telegram.tgnet.TLRPC$TL_upload_webFile r3 = r2.responseWeb     // Catch:{ Exception -> 0x05f6 }
            r4 = 0
            r3.disableFree = r4     // Catch:{ Exception -> 0x05f6 }
            org.telegram.tgnet.TLRPC$TL_upload_webFile r3 = r2.responseWeb     // Catch:{ Exception -> 0x05f6 }
            r3.freeResources()     // Catch:{ Exception -> 0x05f6 }
            goto L_0x05e3
        L_0x05cf:
            org.telegram.tgnet.TLRPC$TL_upload_cdnFile r3 = r2.responseCdn     // Catch:{ Exception -> 0x05f6 }
            if (r3 == 0) goto L_0x05e3
            org.telegram.tgnet.TLRPC$TL_upload_cdnFile r3 = r2.responseCdn     // Catch:{ Exception -> 0x05f6 }
            r4 = 0
            r3.disableFree = r4     // Catch:{ Exception -> 0x05f6 }
            org.telegram.tgnet.TLRPC$TL_upload_cdnFile r3 = r2.responseCdn     // Catch:{ Exception -> 0x05f6 }
            r3.freeResources()     // Catch:{ Exception -> 0x05f6 }
        L_0x05e3:
            if (r1 == 0) goto L_0x05ea
            r0 = 1
            r8.onFinishLoadingFile(r0)     // Catch:{ Exception -> 0x05f6 }
            goto L_0x05fe
        L_0x05ea:
            r45.startDownloadRequest()     // Catch:{ Exception -> 0x05f6 }
            goto L_0x05fe
        L_0x05ee:
            r18 = r6
        L_0x05f0:
            r0 = 1
            r8.onFinishLoadingFile(r0)     // Catch:{ Exception -> 0x05f6 }
            r1 = 0
            return r1
        L_0x05f6:
            r0 = move-exception
            r1 = 0
            r8.onFail(r1, r1)
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x05fe:
            r1 = 0
            goto L_0x0701
        L_0x0601:
            r10 = r5
            java.lang.String r1 = r9.text
            java.lang.String r2 = "FILE_MIGRATE_"
            boolean r1 = r1.contains(r2)
            if (r1 == 0) goto L_0x0642
            java.lang.String r0 = r9.text
            java.lang.String r1 = ""
            java.lang.String r2 = r0.replace(r2, r1)
            java.util.Scanner r0 = new java.util.Scanner
            r0.<init>(r2)
            r3 = r0
            r3.useDelimiter(r1)
            int r0 = r3.nextInt()     // Catch:{ Exception -> 0x0626 }
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)     // Catch:{ Exception -> 0x0626 }
            goto L_0x0629
        L_0x0626:
            r0 = move-exception
            r1 = 0
            r0 = r1
        L_0x0629:
            if (r0 != 0) goto L_0x0630
            r1 = 0
            r8.onFail(r1, r1)
            goto L_0x063f
        L_0x0630:
            int r1 = r0.intValue()
            r8.datacenterId = r1
            r4 = 0
            r8.downloadedBytes = r4
            r8.requestedBytesCount = r4
            r45.startDownloadRequest()
        L_0x063f:
            r1 = 0
            goto L_0x0701
        L_0x0642:
            java.lang.String r1 = r9.text
            java.lang.String r2 = "OFFSET_INVALID"
            boolean r1 = r1.contains(r2)
            if (r1 == 0) goto L_0x0671
            long r0 = r8.downloadedBytes
            int r2 = r8.currentDownloadChunkSize
            long r2 = (long) r2
            long r0 = r0 % r2
            r2 = 0
            int r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r4 != 0) goto L_0x066b
            r0 = 1
            r8.onFinishLoadingFile(r0)     // Catch:{ Exception -> 0x065f }
            r1 = 0
            goto L_0x0701
        L_0x065f:
            r0 = move-exception
            r1 = r0
            r0 = r1
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            r1 = 0
            r8.onFail(r1, r1)
            goto L_0x0701
        L_0x066b:
            r1 = 0
            r8.onFail(r1, r1)
            goto L_0x0701
        L_0x0671:
            r1 = 0
            java.lang.String r2 = r9.text
            java.lang.String r3 = "RETRY_LIMIT"
            boolean r2 = r2.contains(r3)
            if (r2 == 0) goto L_0x0682
            r0 = 2
            r8.onFail(r1, r0)
            goto L_0x0701
        L_0x0682:
            boolean r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r1 == 0) goto L_0x06fd
            org.telegram.tgnet.TLRPC$InputFileLocation r1 = r8.location
            java.lang.String r2 = " "
            if (r1 == 0) goto L_0x06d8
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r3 = r9.text
            r1.append(r3)
            r1.append(r2)
            org.telegram.tgnet.TLRPC$InputFileLocation r2 = r8.location
            r1.append(r2)
            r1.append(r10)
            org.telegram.tgnet.TLRPC$InputFileLocation r2 = r8.location
            long r2 = r2.id
            r1.append(r2)
            r1.append(r15)
            org.telegram.tgnet.TLRPC$InputFileLocation r2 = r8.location
            int r2 = r2.local_id
            r1.append(r2)
            r1.append(r14)
            org.telegram.tgnet.TLRPC$InputFileLocation r2 = r8.location
            long r2 = r2.access_hash
            r1.append(r2)
            r1.append(r13)
            org.telegram.tgnet.TLRPC$InputFileLocation r2 = r8.location
            long r2 = r2.volume_id
            r1.append(r2)
            r1.append(r0)
            org.telegram.tgnet.TLRPC$InputFileLocation r0 = r8.location
            long r2 = r0.secret
            r1.append(r2)
            java.lang.String r0 = r1.toString()
            org.telegram.messenger.FileLog.e((java.lang.String) r0)
            goto L_0x06fd
        L_0x06d8:
            org.telegram.tgnet.TLRPC$InputWebFileLocation r0 = r8.webLocation
            if (r0 == 0) goto L_0x06fd
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = r9.text
            r0.append(r1)
            r0.append(r2)
            org.telegram.tgnet.TLRPC$InputWebFileLocation r1 = r8.webLocation
            r0.append(r1)
            r0.append(r10)
            java.lang.String r1 = r8.fileName
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            org.telegram.messenger.FileLog.e((java.lang.String) r0)
        L_0x06fd:
            r1 = 0
            r8.onFail(r1, r1)
        L_0x0701:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.FileLoadOperation.processRequestResult(org.telegram.messenger.FileLoadOperation$RequestInfo, org.telegram.tgnet.TLRPC$TL_error):boolean");
    }

    /* access modifiers changed from: protected */
    public void onFail(boolean thread, int reason) {
        cleanup();
        this.state = 2;
        FileLoadOperationDelegate fileLoadOperationDelegate = this.delegate;
        if (fileLoadOperationDelegate == null) {
            return;
        }
        if (thread) {
            Utilities.stageQueue.postRunnable(new FileLoadOperation$$ExternalSyntheticLambda6(this, reason));
        } else {
            fileLoadOperationDelegate.didFailedLoadingFile(this, reason);
        }
    }

    /* renamed from: lambda$onFail$10$org-telegram-messenger-FileLoadOperation  reason: not valid java name */
    public /* synthetic */ void m1806lambda$onFail$10$orgtelegrammessengerFileLoadOperation(int reason) {
        this.delegate.didFailedLoadingFile(this, reason);
    }

    private void clearOperaion(RequestInfo currentInfo, boolean preloadChanged) {
        long minOffset = Long.MAX_VALUE;
        for (int a = 0; a < this.requestInfos.size(); a++) {
            RequestInfo info = this.requestInfos.get(a);
            minOffset = Math.min(info.offset, minOffset);
            if (this.isPreloadVideoOperation) {
                this.requestedPreloadedBytesRanges.remove(Long.valueOf(info.offset));
            } else {
                removePart(this.notRequestedBytesRanges, info.offset, ((long) this.currentDownloadChunkSize) + info.offset);
            }
            if (!(currentInfo == info || info.requestToken == 0)) {
                ConnectionsManager.getInstance(this.currentAccount).cancelRequest(info.requestToken, true);
            }
        }
        this.requestInfos.clear();
        for (int a2 = 0; a2 < this.delayedRequestInfos.size(); a2++) {
            RequestInfo info2 = this.delayedRequestInfos.get(a2);
            if (this.isPreloadVideoOperation) {
                this.requestedPreloadedBytesRanges.remove(Long.valueOf(info2.offset));
            } else {
                removePart(this.notRequestedBytesRanges, info2.offset, ((long) this.currentDownloadChunkSize) + info2.offset);
            }
            if (info2.response != null) {
                info2.response.disableFree = false;
                info2.response.freeResources();
            } else if (info2.responseWeb != null) {
                info2.responseWeb.disableFree = false;
                info2.responseWeb.freeResources();
            } else if (info2.responseCdn != null) {
                info2.responseCdn.disableFree = false;
                info2.responseCdn.freeResources();
            }
            minOffset = Math.min(info2.offset, minOffset);
        }
        this.delayedRequestInfos.clear();
        this.requestsCount = 0;
        if (!preloadChanged && this.isPreloadVideoOperation) {
            this.requestedBytesCount = (long) this.totalPreloadedBytes;
        } else if (this.notLoadedBytesRanges == null) {
            this.downloadedBytes = minOffset;
            this.requestedBytesCount = minOffset;
        }
    }

    private void requestReference(RequestInfo requestInfo) {
        if (!this.requestingReference) {
            clearOperaion(requestInfo, false);
            this.requestingReference = true;
            Object obj = this.parentObject;
            if (obj instanceof MessageObject) {
                MessageObject messageObject = (MessageObject) obj;
                if (messageObject.getId() < 0 && messageObject.messageOwner.media.webpage != null) {
                    this.parentObject = messageObject.messageOwner.media.webpage;
                }
            }
            FileRefController.getInstance(this.currentAccount).requestReference(this.parentObject, this.location, this, requestInfo);
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v3, resolved type: org.telegram.tgnet.TLRPC$TL_upload_getFile} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v6, resolved type: org.telegram.tgnet.TLRPC$TL_upload_getFile} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v6, resolved type: org.telegram.tgnet.TLRPC$TL_upload_getWebFile} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v10, resolved type: org.telegram.tgnet.TLRPC$TL_upload_getFile} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v8, resolved type: org.telegram.tgnet.TLRPC$TL_upload_getCdnFile} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v13, resolved type: org.telegram.tgnet.TLRPC$TL_upload_getFile} */
    /* access modifiers changed from: protected */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void startDownloadRequest() {
        /*
            r28 = this;
            r8 = r28
            boolean r0 = r8.paused
            if (r0 != 0) goto L_0x0353
            boolean r0 = r8.reuploadingCdn
            if (r0 != 0) goto L_0x0353
            int r0 = r8.state
            r9 = 1
            if (r0 != r9) goto L_0x0353
            long r0 = r8.streamPriorityStartOffset
            r10 = 0
            int r2 = (r0 > r10 ? 1 : (r0 == r10 ? 0 : -1))
            if (r2 != 0) goto L_0x0047
            boolean r0 = r8.nextPartWasPreloaded
            if (r0 != 0) goto L_0x002c
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$RequestInfo> r0 = r8.requestInfos
            int r0 = r0.size()
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$RequestInfo> r1 = r8.delayedRequestInfos
            int r1 = r1.size()
            int r0 = r0 + r1
            int r1 = r8.currentMaxDownloadRequests
            if (r0 >= r1) goto L_0x0353
        L_0x002c:
            boolean r0 = r8.isPreloadVideoOperation
            if (r0 == 0) goto L_0x0047
            long r0 = r8.requestedBytesCount
            r2 = 2097152(0x200000, double:1.0361308E-317)
            int r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r4 > 0) goto L_0x0353
            int r0 = r8.moovFound
            if (r0 == 0) goto L_0x0047
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$RequestInfo> r0 = r8.requestInfos
            int r0 = r0.size()
            if (r0 <= 0) goto L_0x0047
            goto L_0x0353
        L_0x0047:
            r0 = 1
            long r1 = r8.streamPriorityStartOffset
            r12 = 0
            int r3 = (r1 > r10 ? 1 : (r1 == r10 ? 0 : -1))
            if (r3 != 0) goto L_0x0070
            boolean r1 = r8.nextPartWasPreloaded
            if (r1 != 0) goto L_0x0070
            boolean r1 = r8.isPreloadVideoOperation
            if (r1 == 0) goto L_0x005b
            int r1 = r8.moovFound
            if (r1 == 0) goto L_0x0070
        L_0x005b:
            long r1 = r8.totalBytesCount
            int r3 = (r1 > r10 ? 1 : (r1 == r10 ? 0 : -1))
            if (r3 <= 0) goto L_0x0070
            int r1 = r8.currentMaxDownloadRequests
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$RequestInfo> r2 = r8.requestInfos
            int r2 = r2.size()
            int r1 = r1 - r2
            int r0 = java.lang.Math.max(r12, r1)
            r13 = r0
            goto L_0x0071
        L_0x0070:
            r13 = r0
        L_0x0071:
            r0 = 0
            r14 = r0
        L_0x0073:
            if (r14 >= r13) goto L_0x0350
            boolean r0 = r8.isPreloadVideoOperation
            r15 = 2
            if (r0 == 0) goto L_0x012c
            int r0 = r8.moovFound
            if (r0 == 0) goto L_0x0085
            long r0 = r8.preloadNotRequestedBytesCount
            int r2 = (r0 > r10 ? 1 : (r0 == r10 ? 0 : -1))
            if (r2 > 0) goto L_0x0085
            return
        L_0x0085:
            long r0 = r8.nextPreloadDownloadOffset
            r2 = -1
            int r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r4 != 0) goto L_0x00d9
            r0 = 0
            r2 = 0
            r3 = 2097152(0x200000, float:2.938736E-39)
            int r4 = r8.currentDownloadChunkSize
            int r3 = r3 / r4
            int r3 = r3 + r15
        L_0x0096:
            if (r3 == 0) goto L_0x00cb
            java.util.HashMap<java.lang.Long, java.lang.Integer> r4 = r8.requestedPreloadedBytesRanges
            java.lang.Long r5 = java.lang.Long.valueOf(r0)
            boolean r4 = r4.containsKey(r5)
            if (r4 != 0) goto L_0x00a6
            r2 = 1
            goto L_0x00cb
        L_0x00a6:
            int r4 = r8.currentDownloadChunkSize
            long r5 = (long) r4
            long r0 = r0 + r5
            long r5 = r8.totalBytesCount
            int r7 = (r0 > r5 ? 1 : (r0 == r5 ? 0 : -1))
            if (r7 <= 0) goto L_0x00b1
            goto L_0x00cb
        L_0x00b1:
            int r7 = r8.moovFound
            if (r7 != r15) goto L_0x00c6
            int r7 = r4 * 8
            long r10 = (long) r7
            int r7 = (r0 > r10 ? 1 : (r0 == r10 ? 0 : -1))
            if (r7 != 0) goto L_0x00c6
            r10 = 1048576(0x100000, double:5.180654E-318)
            long r5 = r5 - r10
            long r10 = (long) r4
            long r5 = r5 / r10
            long r10 = (long) r4
            long r5 = r5 * r10
            r0 = r5
        L_0x00c6:
            int r3 = r3 + -1
            r10 = 0
            goto L_0x0096
        L_0x00cb:
            if (r2 != 0) goto L_0x00d8
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$RequestInfo> r4 = r8.requestInfos
            boolean r4 = r4.isEmpty()
            if (r4 == 0) goto L_0x00d8
            r8.onFinishLoadingFile(r12)
        L_0x00d8:
            goto L_0x00db
        L_0x00d9:
            long r0 = r8.nextPreloadDownloadOffset
        L_0x00db:
            java.util.HashMap<java.lang.Long, java.lang.Integer> r2 = r8.requestedPreloadedBytesRanges
            if (r2 != 0) goto L_0x00e6
            java.util.HashMap r2 = new java.util.HashMap
            r2.<init>()
            r8.requestedPreloadedBytesRanges = r2
        L_0x00e6:
            java.util.HashMap<java.lang.Long, java.lang.Integer> r2 = r8.requestedPreloadedBytesRanges
            java.lang.Long r3 = java.lang.Long.valueOf(r0)
            java.lang.Integer r4 = java.lang.Integer.valueOf(r9)
            r2.put(r3, r4)
            boolean r2 = org.telegram.messenger.BuildVars.DEBUG_VERSION
            if (r2 == 0) goto L_0x011f
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "start next preload from "
            r2.append(r3)
            r2.append(r0)
            java.lang.String r3 = " size "
            r2.append(r3)
            long r3 = r8.totalBytesCount
            r2.append(r3)
            java.lang.String r3 = " for "
            r2.append(r3)
            java.io.File r3 = r8.cacheFilePreload
            r2.append(r3)
            java.lang.String r2 = r2.toString()
            org.telegram.messenger.FileLog.d(r2)
        L_0x011f:
            long r2 = r8.preloadNotRequestedBytesCount
            int r4 = r8.currentDownloadChunkSize
            long r4 = (long) r4
            long r2 = r2 - r4
            r8.preloadNotRequestedBytesCount = r2
            r18 = r13
            r12 = r0
            goto L_0x01b2
        L_0x012c:
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$Range> r0 = r8.notRequestedBytesRanges
            if (r0 == 0) goto L_0x01ad
            long r1 = r8.streamPriorityStartOffset
            r3 = 0
            int r5 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r5 == 0) goto L_0x0139
            goto L_0x013b
        L_0x0139:
            long r1 = r8.streamStartOffset
        L_0x013b:
            int r0 = r0.size()
            r3 = 9223372036854775807(0x7fffffffffffffff, double:NaN)
            r5 = 9223372036854775807(0x7fffffffffffffff, double:NaN)
            r7 = 0
        L_0x014a:
            if (r7 >= r0) goto L_0x0197
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$Range> r10 = r8.notRequestedBytesRanges
            java.lang.Object r10 = r10.get(r7)
            org.telegram.messenger.FileLoadOperation$Range r10 = (org.telegram.messenger.FileLoadOperation.Range) r10
            r16 = 0
            int r11 = (r1 > r16 ? 1 : (r1 == r16 ? 0 : -1))
            if (r11 == 0) goto L_0x0187
            long r18 = r10.start
            int r11 = (r18 > r1 ? 1 : (r18 == r1 ? 0 : -1))
            if (r11 > 0) goto L_0x0173
            long r18 = r10.end
            int r11 = (r18 > r1 ? 1 : (r18 == r1 ? 0 : -1))
            if (r11 <= 0) goto L_0x0173
            r5 = r1
            r3 = 9223372036854775807(0x7fffffffffffffff, double:NaN)
            r18 = r13
            goto L_0x0199
        L_0x0173:
            long r18 = r10.start
            int r11 = (r1 > r18 ? 1 : (r1 == r18 ? 0 : -1))
            if (r11 >= 0) goto L_0x0187
            long r18 = r10.start
            int r11 = (r18 > r5 ? 1 : (r18 == r5 ? 0 : -1))
            if (r11 >= 0) goto L_0x0187
            long r5 = r10.start
        L_0x0187:
            r18 = r13
            long r12 = r10.start
            long r3 = java.lang.Math.min(r3, r12)
            int r7 = r7 + 1
            r13 = r18
            r12 = 0
            goto L_0x014a
        L_0x0197:
            r18 = r13
        L_0x0199:
            r12 = 9223372036854775807(0x7fffffffffffffff, double:NaN)
            int r7 = (r5 > r12 ? 1 : (r5 == r12 ? 0 : -1))
            if (r7 == 0) goto L_0x01a5
            r12 = r5
            r0 = r12
            goto L_0x01ab
        L_0x01a5:
            int r7 = (r3 > r12 ? 1 : (r3 == r12 ? 0 : -1))
            if (r7 == 0) goto L_0x0352
            r12 = r3
            r0 = r12
        L_0x01ab:
            r12 = r0
            goto L_0x01b2
        L_0x01ad:
            r18 = r13
            long r0 = r8.requestedBytesCount
            r12 = r0
        L_0x01b2:
            boolean r0 = r8.isPreloadVideoOperation
            if (r0 != 0) goto L_0x01c6
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$Range> r2 = r8.notRequestedBytesRanges
            if (r2 == 0) goto L_0x01c6
            int r0 = r8.currentDownloadChunkSize
            long r0 = (long) r0
            long r5 = r12 + r0
            r7 = 0
            r1 = r28
            r3 = r12
            r1.addPart(r2, r3, r5, r7)
        L_0x01c6:
            long r0 = r8.totalBytesCount
            r2 = 0
            int r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r4 <= 0) goto L_0x01d4
            int r2 = (r12 > r0 ? 1 : (r12 == r0 ? 0 : -1))
            if (r2 < 0) goto L_0x01d4
            goto L_0x0352
        L_0x01d4:
            r2 = 0
            int r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r4 <= 0) goto L_0x01ee
            int r4 = r18 + -1
            if (r14 == r4) goto L_0x01ee
            int r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r4 <= 0) goto L_0x01eb
            int r2 = r8.currentDownloadChunkSize
            long r2 = (long) r2
            long r2 = r2 + r12
            int r4 = (r2 > r0 ? 1 : (r2 == r0 ? 0 : -1))
            if (r4 < 0) goto L_0x01eb
            goto L_0x01ee
        L_0x01eb:
            r27 = 0
            goto L_0x01f0
        L_0x01ee:
            r27 = 1
        L_0x01f0:
            int r0 = r8.requestsCount
            int r0 = r0 % r15
            if (r0 != 0) goto L_0x01f8
            r26 = 2
            goto L_0x01fe
        L_0x01f8:
            r15 = 65538(0x10002, float:9.1838E-41)
            r26 = 65538(0x10002, float:9.1838E-41)
        L_0x01fe:
            boolean r0 = r8.isForceRequest
            if (r0 == 0) goto L_0x0205
            r0 = 32
            goto L_0x0206
        L_0x0205:
            r0 = 0
        L_0x0206:
            boolean r1 = r8.isCdn
            if (r1 == 0) goto L_0x021e
            org.telegram.tgnet.TLRPC$TL_upload_getCdnFile r1 = new org.telegram.tgnet.TLRPC$TL_upload_getCdnFile
            r1.<init>()
            byte[] r2 = r8.cdnToken
            r1.file_token = r2
            r1.offset = r12
            int r2 = r8.currentDownloadChunkSize
            r1.limit = r2
            r2 = r1
            r0 = r0 | 1
            r1 = r0
            goto L_0x0248
        L_0x021e:
            org.telegram.tgnet.TLRPC$InputWebFileLocation r1 = r8.webLocation
            if (r1 == 0) goto L_0x0235
            org.telegram.tgnet.TLRPC$TL_upload_getWebFile r1 = new org.telegram.tgnet.TLRPC$TL_upload_getWebFile
            r1.<init>()
            org.telegram.tgnet.TLRPC$InputWebFileLocation r2 = r8.webLocation
            r1.location = r2
            int r2 = (int) r12
            r1.offset = r2
            int r2 = r8.currentDownloadChunkSize
            r1.limit = r2
            r2 = r1
            r1 = r0
            goto L_0x0248
        L_0x0235:
            org.telegram.tgnet.TLRPC$TL_upload_getFile r1 = new org.telegram.tgnet.TLRPC$TL_upload_getFile
            r1.<init>()
            org.telegram.tgnet.TLRPC$InputFileLocation r2 = r8.location
            r1.location = r2
            r1.offset = r12
            int r2 = r8.currentDownloadChunkSize
            r1.limit = r2
            r1.cdn_supported = r9
            r2 = r1
            r1 = r0
        L_0x0248:
            long r3 = r8.requestedBytesCount
            int r0 = r8.currentDownloadChunkSize
            long r5 = (long) r0
            long r3 = r3 + r5
            r8.requestedBytesCount = r3
            org.telegram.messenger.FileLoadOperation$RequestInfo r0 = new org.telegram.messenger.FileLoadOperation$RequestInfo
            r0.<init>()
            r3 = r0
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$RequestInfo> r0 = r8.requestInfos
            r0.add(r3)
            long unused = r3.offset = r12
            boolean r0 = r8.isPreloadVideoOperation
            if (r0 != 0) goto L_0x02de
            boolean r0 = r8.supportsPreloading
            if (r0 == 0) goto L_0x02de
            java.io.RandomAccessFile r0 = r8.preloadStream
            if (r0 == 0) goto L_0x02de
            java.util.HashMap<java.lang.Long, org.telegram.messenger.FileLoadOperation$PreloadRange> r0 = r8.preloadedBytesRanges
            if (r0 == 0) goto L_0x02de
            long r4 = r3.offset
            java.lang.Long r4 = java.lang.Long.valueOf(r4)
            java.lang.Object r0 = r0.get(r4)
            r4 = r0
            org.telegram.messenger.FileLoadOperation$PreloadRange r4 = (org.telegram.messenger.FileLoadOperation.PreloadRange) r4
            if (r4 == 0) goto L_0x02dc
            org.telegram.tgnet.TLRPC$TL_upload_file r0 = new org.telegram.tgnet.TLRPC$TL_upload_file
            r0.<init>()
            org.telegram.tgnet.TLRPC.TL_upload_file unused = r3.response = r0
            boolean r0 = org.telegram.messenger.BuildVars.DEBUG_VERSION     // Catch:{ Exception -> 0x02d9 }
            if (r0 == 0) goto L_0x029f
            long r5 = r4.length     // Catch:{ Exception -> 0x02d9 }
            r19 = 2147483647(0x7fffffff, double:1.060997895E-314)
            int r0 = (r5 > r19 ? 1 : (r5 == r19 ? 0 : -1))
            if (r0 > 0) goto L_0x0297
            goto L_0x029f
        L_0x0297:
            java.lang.RuntimeException r0 = new java.lang.RuntimeException     // Catch:{ Exception -> 0x02d9 }
            java.lang.String r5 = "cast long to integer"
            r0.<init>(r5)     // Catch:{ Exception -> 0x02d9 }
            throw r0     // Catch:{ Exception -> 0x02d9 }
        L_0x029f:
            org.telegram.tgnet.NativeByteBuffer r0 = new org.telegram.tgnet.NativeByteBuffer     // Catch:{ Exception -> 0x02d9 }
            long r5 = r4.length     // Catch:{ Exception -> 0x02d9 }
            int r6 = (int) r5     // Catch:{ Exception -> 0x02d9 }
            r0.<init>((int) r6)     // Catch:{ Exception -> 0x02d9 }
            java.io.RandomAccessFile r5 = r8.preloadStream     // Catch:{ Exception -> 0x02d9 }
            long r6 = r4.fileOffset     // Catch:{ Exception -> 0x02d9 }
            r5.seek(r6)     // Catch:{ Exception -> 0x02d9 }
            java.io.RandomAccessFile r5 = r8.preloadStream     // Catch:{ Exception -> 0x02d9 }
            java.nio.channels.FileChannel r5 = r5.getChannel()     // Catch:{ Exception -> 0x02d9 }
            java.nio.ByteBuffer r6 = r0.buffer     // Catch:{ Exception -> 0x02d9 }
            r5.read(r6)     // Catch:{ Exception -> 0x02d9 }
            java.nio.ByteBuffer r5 = r0.buffer     // Catch:{ Exception -> 0x02d9 }
            r6 = 0
            r5.position(r6)     // Catch:{ Exception -> 0x02d7 }
            org.telegram.tgnet.TLRPC$TL_upload_file r5 = r3.response     // Catch:{ Exception -> 0x02d7 }
            r5.bytes = r0     // Catch:{ Exception -> 0x02d7 }
            org.telegram.messenger.DispatchQueue r5 = org.telegram.messenger.Utilities.stageQueue     // Catch:{ Exception -> 0x02d7 }
            org.telegram.messenger.FileLoadOperation$$ExternalSyntheticLambda7 r7 = new org.telegram.messenger.FileLoadOperation$$ExternalSyntheticLambda7     // Catch:{ Exception -> 0x02d7 }
            r7.<init>(r8, r3)     // Catch:{ Exception -> 0x02d7 }
            r5.postRunnable(r7)     // Catch:{ Exception -> 0x02d7 }
            r10 = 0
            goto L_0x0349
        L_0x02d7:
            r0 = move-exception
            goto L_0x02df
        L_0x02d9:
            r0 = move-exception
            r6 = 0
            goto L_0x02df
        L_0x02dc:
            r6 = 0
            goto L_0x02df
        L_0x02de:
            r6 = 0
        L_0x02df:
            long r4 = r8.streamPriorityStartOffset
            r10 = 0
            int r0 = (r4 > r10 ? 1 : (r4 == r10 ? 0 : -1))
            if (r0 == 0) goto L_0x0307
            boolean r0 = org.telegram.messenger.BuildVars.DEBUG_VERSION
            if (r0 == 0) goto L_0x0301
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r4 = "frame get offset = "
            r0.append(r4)
            long r4 = r8.streamPriorityStartOffset
            r0.append(r4)
            java.lang.String r0 = r0.toString()
            org.telegram.messenger.FileLog.d(r0)
        L_0x0301:
            r4 = 0
            r8.streamPriorityStartOffset = r4
            r8.priorityRequestInfo = r3
        L_0x0307:
            org.telegram.tgnet.TLRPC$InputFileLocation r0 = r8.location
            boolean r4 = r0 instanceof org.telegram.tgnet.TLRPC.TL_inputPeerPhotoFileLocation
            if (r4 == 0) goto L_0x031b
            org.telegram.tgnet.TLRPC$TL_inputPeerPhotoFileLocation r0 = (org.telegram.tgnet.TLRPC.TL_inputPeerPhotoFileLocation) r0
            long r4 = r0.photo_id
            r10 = 0
            int r7 = (r4 > r10 ? 1 : (r4 == r10 ? 0 : -1))
            if (r7 != 0) goto L_0x031d
            r8.requestReference(r3)
            goto L_0x0349
        L_0x031b:
            r10 = 0
        L_0x031d:
            int r0 = r8.currentAccount
            org.telegram.tgnet.ConnectionsManager r19 = org.telegram.tgnet.ConnectionsManager.getInstance(r0)
            org.telegram.messenger.FileLoadOperation$$ExternalSyntheticLambda5 r0 = new org.telegram.messenger.FileLoadOperation$$ExternalSyntheticLambda5
            r0.<init>(r8, r3, r2)
            r22 = 0
            r23 = 0
            boolean r4 = r8.isCdn
            if (r4 == 0) goto L_0x0333
            int r4 = r8.cdnDatacenterId
            goto L_0x0335
        L_0x0333:
            int r4 = r8.datacenterId
        L_0x0335:
            r25 = r4
            r20 = r2
            r21 = r0
            r24 = r1
            int r0 = r19.sendRequest(r20, r21, r22, r23, r24, r25, r26, r27)
            int unused = r3.requestToken = r0
            int r0 = r8.requestsCount
            int r0 = r0 + r9
            r8.requestsCount = r0
        L_0x0349:
            int r14 = r14 + 1
            r13 = r18
            r12 = 0
            goto L_0x0073
        L_0x0350:
            r18 = r13
        L_0x0352:
            return
        L_0x0353:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.FileLoadOperation.startDownloadRequest():void");
    }

    /* renamed from: lambda$startDownloadRequest$11$org-telegram-messenger-FileLoadOperation  reason: not valid java name */
    public /* synthetic */ void m1813xe3dafa6a(RequestInfo requestInfo) {
        processRequestResult(requestInfo, (TLRPC.TL_error) null);
        requestInfo.response.freeResources();
    }

    /* renamed from: lambda$startDownloadRequest$13$org-telegram-messenger-FileLoadOperation  reason: not valid java name */
    public /* synthetic */ void m1815xf2a564a8(RequestInfo requestInfo, TLObject request, TLObject response, TLRPC.TL_error error) {
        if (this.requestInfos.contains(requestInfo)) {
            if (requestInfo == this.priorityRequestInfo) {
                if (BuildVars.DEBUG_VERSION) {
                    FileLog.d("frame get request completed " + this.priorityRequestInfo.offset);
                }
                this.priorityRequestInfo = null;
            }
            if (error != null) {
                if (FileRefController.isFileRefError(error.text)) {
                    requestReference(requestInfo);
                    return;
                } else if ((request instanceof TLRPC.TL_upload_getCdnFile) && error.text.equals("FILE_TOKEN_INVALID")) {
                    this.isCdn = false;
                    clearOperaion(requestInfo, false);
                    startDownloadRequest();
                    return;
                }
            }
            if (response instanceof TLRPC.TL_upload_fileCdnRedirect) {
                TLRPC.TL_upload_fileCdnRedirect res = (TLRPC.TL_upload_fileCdnRedirect) response;
                if (!res.file_hashes.isEmpty()) {
                    if (this.cdnHashes == null) {
                        this.cdnHashes = new HashMap<>();
                    }
                    for (int a1 = 0; a1 < res.file_hashes.size(); a1++) {
                        TLRPC.TL_fileHash hash = (TLRPC.TL_fileHash) res.file_hashes.get(a1);
                        this.cdnHashes.put(Long.valueOf(hash.offset), hash);
                    }
                }
                if (res.encryption_iv == null || res.encryption_key == null || res.encryption_iv.length != 16 || res.encryption_key.length != 32) {
                    TLRPC.TL_error error2 = new TLRPC.TL_error();
                    error2.text = "bad redirect response";
                    error2.code = 400;
                    processRequestResult(requestInfo, error2);
                    return;
                }
                this.isCdn = true;
                if (this.notCheckedCdnRanges == null) {
                    ArrayList<Range> arrayList = new ArrayList<>();
                    this.notCheckedCdnRanges = arrayList;
                    arrayList.add(new Range(0, (long) this.maxCdnParts));
                }
                this.cdnDatacenterId = res.dc_id;
                this.cdnIv = res.encryption_iv;
                this.cdnKey = res.encryption_key;
                this.cdnToken = res.file_token;
                clearOperaion(requestInfo, false);
                startDownloadRequest();
            } else if (!(response instanceof TLRPC.TL_upload_cdnFileReuploadNeeded)) {
                if (response instanceof TLRPC.TL_upload_file) {
                    TLRPC.TL_upload_file unused = requestInfo.response = (TLRPC.TL_upload_file) response;
                } else if (response instanceof TLRPC.TL_upload_webFile) {
                    TLRPC.TL_upload_webFile unused2 = requestInfo.responseWeb = (TLRPC.TL_upload_webFile) response;
                    if (this.totalBytesCount == 0 && requestInfo.responseWeb.size != 0) {
                        this.totalBytesCount = (long) requestInfo.responseWeb.size;
                    }
                } else {
                    TLRPC.TL_upload_cdnFile unused3 = requestInfo.responseCdn = (TLRPC.TL_upload_cdnFile) response;
                }
                if (response != null) {
                    int i = this.currentType;
                    if (i == 50331648) {
                        StatsController.getInstance(this.currentAccount).incrementReceivedBytesCount(response.networkType, 3, (long) (response.getObjectSize() + 4));
                    } else if (i == 33554432) {
                        StatsController.getInstance(this.currentAccount).incrementReceivedBytesCount(response.networkType, 2, (long) (response.getObjectSize() + 4));
                    } else if (i == 16777216) {
                        StatsController.getInstance(this.currentAccount).incrementReceivedBytesCount(response.networkType, 4, (long) (response.getObjectSize() + 4));
                    } else if (i == 67108864) {
                        StatsController.getInstance(this.currentAccount).incrementReceivedBytesCount(response.networkType, 5, (long) (response.getObjectSize() + 4));
                    }
                }
                processRequestResult(requestInfo, error);
            } else if (!this.reuploadingCdn) {
                clearOperaion(requestInfo, false);
                this.reuploadingCdn = true;
                TLRPC.TL_upload_reuploadCdnFile req = new TLRPC.TL_upload_reuploadCdnFile();
                req.file_token = this.cdnToken;
                req.request_token = ((TLRPC.TL_upload_cdnFileReuploadNeeded) response).request_token;
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new FileLoadOperation$$ExternalSyntheticLambda4(this, requestInfo), (QuickAckDelegate) null, (WriteToSocketDelegate) null, 0, this.datacenterId, 1, true);
            }
        }
    }

    /* renamed from: lambda$startDownloadRequest$12$org-telegram-messenger-FileLoadOperation  reason: not valid java name */
    public /* synthetic */ void m1814xeb402var_(RequestInfo requestInfo, TLObject response1, TLRPC.TL_error error1) {
        this.reuploadingCdn = false;
        if (error1 == null) {
            TLRPC.Vector vector = (TLRPC.Vector) response1;
            if (!vector.objects.isEmpty()) {
                if (this.cdnHashes == null) {
                    this.cdnHashes = new HashMap<>();
                }
                for (int a1 = 0; a1 < vector.objects.size(); a1++) {
                    TLRPC.TL_fileHash hash = (TLRPC.TL_fileHash) vector.objects.get(a1);
                    this.cdnHashes.put(Long.valueOf(hash.offset), hash);
                }
            }
            startDownloadRequest();
        } else if (error1.text.equals("FILE_TOKEN_INVALID") || error1.text.equals("REQUEST_TOKEN_INVALID")) {
            this.isCdn = false;
            clearOperaion(requestInfo, false);
            startDownloadRequest();
        } else {
            onFail(false, 0);
        }
    }

    public void setDelegate(FileLoadOperationDelegate delegate2) {
        this.delegate = delegate2;
    }
}
