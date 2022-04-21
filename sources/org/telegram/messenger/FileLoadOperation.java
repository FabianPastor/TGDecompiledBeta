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
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.NativeByteBuffer;
import org.telegram.tgnet.QuickAckDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.WriteToSocketDelegate;

public class FileLoadOperation {
    private static final int bigFileSizeFrom = 1048576;
    private static final int cdnChunkCheckSize = 131072;
    private static final int downloadChunkSize = 32768;
    private static final int downloadChunkSizeBig = 131072;
    private static final int maxCdnParts = 16000;
    private static final int maxDownloadRequests = (BuildVars.DEBUG_PRIVATE_VERSION ? 8 : 4);
    private static final int maxDownloadRequestsBig;
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
    private int currentQueueType;
    private int currentType;
    private int datacenterId;
    private ArrayList<RequestInfo> delayedRequestInfos;
    private FileLoadOperationDelegate delegate;
    private int downloadedBytes;
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
    private int foundMoovSize;
    private int initialDatacenterId;
    private boolean isCdn;
    private boolean isForceRequest;
    private boolean isPreloadVideoOperation;
    private byte[] iv;
    private byte[] key;
    protected long lastProgressUpdateTime;
    protected TLRPC.InputFileLocation location;
    private int moovFound;
    private int nextAtomOffset;
    private boolean nextPartWasPreloaded;
    private int nextPreloadDownloadOffset;
    private ArrayList<Range> notCheckedCdnRanges;
    private ArrayList<Range> notLoadedBytesRanges;
    private volatile ArrayList<Range> notLoadedBytesRangesCopy;
    private ArrayList<Range> notRequestedBytesRanges;
    public Object parentObject;
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

        private Range(int s, int e) {
            this.start = s;
            this.end = e;
        }
    }

    private static class PreloadRange {
        /* access modifiers changed from: private */
        public int fileOffset;
        /* access modifiers changed from: private */
        public int length;

        private PreloadRange(int o, int l) {
            this.fileOffset = o;
            this.length = l;
        }
    }

    static {
        int i = 8;
        if (!BuildVars.DEBUG_PRIVATE_VERSION) {
            i = 4;
        }
        maxDownloadRequestsBig = i;
    }

    public FileLoadOperation(ImageLocation imageLocation, Object parent, String extension, int size) {
        this.preloadTempBuffer = new byte[16];
        boolean z = false;
        this.state = 0;
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
        this.preloadTempBuffer = new byte[16];
        this.state = 0;
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
        this.preloadTempBuffer = new byte[16];
        this.state = 0;
        this.currentAccount = instance;
        this.webFile = webDocument;
        this.webLocation = webDocument.location;
        this.totalBytesCount = webDocument.size;
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

    /* JADX WARNING: Removed duplicated region for block: B:32:0x00c2 A[Catch:{ Exception -> 0x0109 }] */
    /* JADX WARNING: Removed duplicated region for block: B:39:0x00e2 A[Catch:{ Exception -> 0x0109 }] */
    /* JADX WARNING: Removed duplicated region for block: B:40:0x00e7 A[Catch:{ Exception -> 0x0109 }] */
    /* JADX WARNING: Removed duplicated region for block: B:46:0x0100 A[Catch:{ Exception -> 0x0109 }] */
    /* JADX WARNING: Removed duplicated region for block: B:53:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public FileLoadOperation(org.telegram.tgnet.TLRPC.Document r8, java.lang.Object r9) {
        /*
            r7 = this;
            r7.<init>()
            r0 = 16
            byte[] r1 = new byte[r0]
            r7.preloadTempBuffer = r1
            r1 = 0
            r7.state = r1
            r2 = 1
            r7.parentObject = r9     // Catch:{ Exception -> 0x0109 }
            boolean r3 = r8 instanceof org.telegram.tgnet.TLRPC.TL_documentEncrypted     // Catch:{ Exception -> 0x0109 }
            java.lang.String r4 = ""
            if (r3 == 0) goto L_0x003f
            org.telegram.tgnet.TLRPC$TL_inputEncryptedFileLocation r3 = new org.telegram.tgnet.TLRPC$TL_inputEncryptedFileLocation     // Catch:{ Exception -> 0x0109 }
            r3.<init>()     // Catch:{ Exception -> 0x0109 }
            r7.location = r3     // Catch:{ Exception -> 0x0109 }
            long r5 = r8.id     // Catch:{ Exception -> 0x0109 }
            r3.id = r5     // Catch:{ Exception -> 0x0109 }
            org.telegram.tgnet.TLRPC$InputFileLocation r3 = r7.location     // Catch:{ Exception -> 0x0109 }
            long r5 = r8.access_hash     // Catch:{ Exception -> 0x0109 }
            r3.access_hash = r5     // Catch:{ Exception -> 0x0109 }
            int r3 = r8.dc_id     // Catch:{ Exception -> 0x0109 }
            r7.datacenterId = r3     // Catch:{ Exception -> 0x0109 }
            r7.initialDatacenterId = r3     // Catch:{ Exception -> 0x0109 }
            r3 = 32
            byte[] r3 = new byte[r3]     // Catch:{ Exception -> 0x0109 }
            r7.iv = r3     // Catch:{ Exception -> 0x0109 }
            byte[] r3 = r8.iv     // Catch:{ Exception -> 0x0109 }
            byte[] r5 = r7.iv     // Catch:{ Exception -> 0x0109 }
            int r6 = r5.length     // Catch:{ Exception -> 0x0109 }
            java.lang.System.arraycopy(r3, r1, r5, r1, r6)     // Catch:{ Exception -> 0x0109 }
            byte[] r3 = r8.key     // Catch:{ Exception -> 0x0109 }
            r7.key = r3     // Catch:{ Exception -> 0x0109 }
            goto L_0x008b
        L_0x003f:
            boolean r3 = r8 instanceof org.telegram.tgnet.TLRPC.TL_document     // Catch:{ Exception -> 0x0109 }
            if (r3 == 0) goto L_0x008b
            org.telegram.tgnet.TLRPC$TL_inputDocumentFileLocation r3 = new org.telegram.tgnet.TLRPC$TL_inputDocumentFileLocation     // Catch:{ Exception -> 0x0109 }
            r3.<init>()     // Catch:{ Exception -> 0x0109 }
            r7.location = r3     // Catch:{ Exception -> 0x0109 }
            long r5 = r8.id     // Catch:{ Exception -> 0x0109 }
            r3.id = r5     // Catch:{ Exception -> 0x0109 }
            org.telegram.tgnet.TLRPC$InputFileLocation r3 = r7.location     // Catch:{ Exception -> 0x0109 }
            long r5 = r8.access_hash     // Catch:{ Exception -> 0x0109 }
            r3.access_hash = r5     // Catch:{ Exception -> 0x0109 }
            org.telegram.tgnet.TLRPC$InputFileLocation r3 = r7.location     // Catch:{ Exception -> 0x0109 }
            byte[] r5 = r8.file_reference     // Catch:{ Exception -> 0x0109 }
            r3.file_reference = r5     // Catch:{ Exception -> 0x0109 }
            org.telegram.tgnet.TLRPC$InputFileLocation r3 = r7.location     // Catch:{ Exception -> 0x0109 }
            r3.thumb_size = r4     // Catch:{ Exception -> 0x0109 }
            org.telegram.tgnet.TLRPC$InputFileLocation r3 = r7.location     // Catch:{ Exception -> 0x0109 }
            byte[] r3 = r3.file_reference     // Catch:{ Exception -> 0x0109 }
            if (r3 != 0) goto L_0x006a
            org.telegram.tgnet.TLRPC$InputFileLocation r3 = r7.location     // Catch:{ Exception -> 0x0109 }
            byte[] r5 = new byte[r1]     // Catch:{ Exception -> 0x0109 }
            r3.file_reference = r5     // Catch:{ Exception -> 0x0109 }
        L_0x006a:
            int r3 = r8.dc_id     // Catch:{ Exception -> 0x0109 }
            r7.datacenterId = r3     // Catch:{ Exception -> 0x0109 }
            r7.initialDatacenterId = r3     // Catch:{ Exception -> 0x0109 }
            r7.allowDisordererFileSave = r2     // Catch:{ Exception -> 0x0109 }
            r3 = 0
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r5 = r8.attributes     // Catch:{ Exception -> 0x0109 }
            int r5 = r5.size()     // Catch:{ Exception -> 0x0109 }
        L_0x0079:
            if (r3 >= r5) goto L_0x008b
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r6 = r8.attributes     // Catch:{ Exception -> 0x0109 }
            java.lang.Object r6 = r6.get(r3)     // Catch:{ Exception -> 0x0109 }
            boolean r6 = r6 instanceof org.telegram.tgnet.TLRPC.TL_documentAttributeVideo     // Catch:{ Exception -> 0x0109 }
            if (r6 == 0) goto L_0x0088
            r7.supportsPreloading = r2     // Catch:{ Exception -> 0x0109 }
            goto L_0x008b
        L_0x0088:
            int r3 = r3 + 1
            goto L_0x0079
        L_0x008b:
            java.lang.String r3 = "application/x-tgsticker"
            java.lang.String r5 = r8.mime_type     // Catch:{ Exception -> 0x0109 }
            boolean r3 = r3.equals(r5)     // Catch:{ Exception -> 0x0109 }
            if (r3 != 0) goto L_0x00a2
            java.lang.String r3 = "application/x-tgwallpattern"
            java.lang.String r5 = r8.mime_type     // Catch:{ Exception -> 0x0109 }
            boolean r3 = r3.equals(r5)     // Catch:{ Exception -> 0x0109 }
            if (r3 == 0) goto L_0x00a0
            goto L_0x00a2
        L_0x00a0:
            r3 = 0
            goto L_0x00a3
        L_0x00a2:
            r3 = 1
        L_0x00a3:
            r7.ungzip = r3     // Catch:{ Exception -> 0x0109 }
            int r3 = r8.size     // Catch:{ Exception -> 0x0109 }
            r7.totalBytesCount = r3     // Catch:{ Exception -> 0x0109 }
            byte[] r5 = r7.key     // Catch:{ Exception -> 0x0109 }
            if (r5 == 0) goto L_0x00ba
            r5 = 0
            int r6 = r3 % 16
            if (r6 == 0) goto L_0x00ba
            int r6 = r3 % 16
            int r0 = r0 - r6
            r7.bytesCountPadding = r0     // Catch:{ Exception -> 0x0109 }
            int r3 = r3 + r0
            r7.totalBytesCount = r3     // Catch:{ Exception -> 0x0109 }
        L_0x00ba:
            java.lang.String r0 = org.telegram.messenger.FileLoader.getDocumentFileName(r8)     // Catch:{ Exception -> 0x0109 }
            r7.ext = r0     // Catch:{ Exception -> 0x0109 }
            if (r0 == 0) goto L_0x00d6
            r3 = 46
            int r0 = r0.lastIndexOf(r3)     // Catch:{ Exception -> 0x0109 }
            r3 = r0
            r5 = -1
            if (r0 != r5) goto L_0x00cd
            goto L_0x00d6
        L_0x00cd:
            java.lang.String r0 = r7.ext     // Catch:{ Exception -> 0x0109 }
            java.lang.String r0 = r0.substring(r3)     // Catch:{ Exception -> 0x0109 }
            r7.ext = r0     // Catch:{ Exception -> 0x0109 }
            goto L_0x00d8
        L_0x00d6:
            r7.ext = r4     // Catch:{ Exception -> 0x0109 }
        L_0x00d8:
            java.lang.String r0 = "audio/ogg"
            java.lang.String r3 = r8.mime_type     // Catch:{ Exception -> 0x0109 }
            boolean r0 = r0.equals(r3)     // Catch:{ Exception -> 0x0109 }
            if (r0 == 0) goto L_0x00e7
            r0 = 50331648(0x3000000, float:3.761582E-37)
            r7.currentType = r0     // Catch:{ Exception -> 0x0109 }
            goto L_0x00f8
        L_0x00e7:
            java.lang.String r0 = r8.mime_type     // Catch:{ Exception -> 0x0109 }
            boolean r0 = org.telegram.messenger.FileLoader.isVideoMimeType(r0)     // Catch:{ Exception -> 0x0109 }
            if (r0 == 0) goto L_0x00f4
            r0 = 33554432(0x2000000, float:9.403955E-38)
            r7.currentType = r0     // Catch:{ Exception -> 0x0109 }
            goto L_0x00f8
        L_0x00f4:
            r0 = 67108864(0x4000000, float:1.5046328E-36)
            r7.currentType = r0     // Catch:{ Exception -> 0x0109 }
        L_0x00f8:
            java.lang.String r0 = r7.ext     // Catch:{ Exception -> 0x0109 }
            int r0 = r0.length()     // Catch:{ Exception -> 0x0109 }
            if (r0 > r2) goto L_0x0108
            java.lang.String r0 = r8.mime_type     // Catch:{ Exception -> 0x0109 }
            java.lang.String r0 = org.telegram.messenger.FileLoader.getExtensionByMimeType(r0)     // Catch:{ Exception -> 0x0109 }
            r7.ext = r0     // Catch:{ Exception -> 0x0109 }
        L_0x0108:
            goto L_0x0110
        L_0x0109:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            r7.onFail(r2, r1)
        L_0x0110:
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

    public void setPaths(int instance, String name, int queueType, File store, File temp) {
        this.storePath = store;
        this.tempPath = temp;
        this.currentAccount = instance;
        this.fileName = name;
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

    private void removePart(ArrayList<Range> ranges, int start, int end) {
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
                    int unused = range.end = end;
                    modified = true;
                    break;
                } else if (end == range.start) {
                    int unused2 = range.start = start;
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
                    int unused3 = r1.end = r2.end;
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

    private void addPart(ArrayList<Range> ranges, int start, int end, boolean save) {
        if (ranges != null && end >= start) {
            boolean modified = false;
            int count = ranges.size();
            int a = 0;
            while (true) {
                if (a >= count) {
                    break;
                }
                Range range = ranges.get(a);
                if (start <= range.start) {
                    if (end >= range.end) {
                        ranges.remove(a);
                        modified = true;
                        break;
                    } else if (end > range.start) {
                        int unused = range.start = end;
                        modified = true;
                        break;
                    }
                } else if (end < range.end) {
                    ranges.add(0, new Range(range.start, start));
                    modified = true;
                    int unused2 = range.start = end;
                    break;
                } else if (start < range.end) {
                    int unused3 = range.end = start;
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
                        Range range2 = ranges.get(a2);
                        this.filePartsStream.writeInt(range2.start);
                        this.filePartsStream.writeInt(range2.end);
                    }
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
                ArrayList<FileLoadOperationStream> arrayList = this.streamListeners;
                if (arrayList != null) {
                    int count3 = arrayList.size();
                    for (int a3 = 0; a3 < count3; a3++) {
                        this.streamListeners.get(a3).newDataAvailable();
                    }
                }
            } else if (BuildVars.LOGS_ENABLED) {
                FileLog.e(this.cacheFileFinal + " downloaded duplicate file part " + start + " - " + end);
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
    public /* synthetic */ void m540lambda$getCurrentFile$1$orgtelegrammessengerFileLoadOperation(File[] result, CountDownLatch countDownLatch) {
        if (this.state == 3) {
            result[0] = this.cacheFileFinal;
        } else {
            result[0] = this.cacheFileTemp;
        }
        countDownLatch.countDown();
    }

    private int getDownloadedLengthFromOffsetInternal(ArrayList<Range> ranges, int offset, int length) {
        if (ranges == null || this.state == 3 || ranges.isEmpty()) {
            int count = this.downloadedBytes;
            if (count == 0) {
                return length;
            }
            return Math.min(length, Math.max(count - offset, 0));
        }
        int count2 = ranges.size();
        Range minRange = null;
        int availableLength = length;
        int a = 0;
        while (true) {
            if (a >= count2) {
                break;
            }
            Range range = ranges.get(a);
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
            return Math.min(length, minRange.start - offset);
        }
        return Math.min(length, Math.max(this.totalBytesCount - offset, 0));
    }

    /* access modifiers changed from: protected */
    public float getDownloadedLengthFromOffset(float progress) {
        ArrayList<Range> ranges = this.notLoadedBytesRangesCopy;
        int i = this.totalBytesCount;
        if (i == 0 || ranges == null) {
            return 0.0f;
        }
        return (((float) getDownloadedLengthFromOffsetInternal(ranges, (int) (((float) i) * progress), i)) / ((float) this.totalBytesCount)) + progress;
    }

    /* access modifiers changed from: protected */
    public int[] getDownloadedLengthFromOffset(int offset, int length) {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        int[] result = new int[2];
        Utilities.stageQueue.postRunnable(new FileLoadOperation$$ExternalSyntheticLambda13(this, result, offset, length, countDownLatch));
        try {
            countDownLatch.await();
        } catch (Exception e) {
        }
        return result;
    }

    /* renamed from: lambda$getDownloadedLengthFromOffset$2$org-telegram-messenger-FileLoadOperation  reason: not valid java name */
    public /* synthetic */ void m541xcb1e2f1b(int[] result, int offset, int length, CountDownLatch countDownLatch) {
        result[0] = getDownloadedLengthFromOffsetInternal(this.notLoadedBytesRanges, offset, length);
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
    public /* synthetic */ void m544xc3d0b3c8(FileLoadOperationStream operation) {
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

    /* JADX WARNING: Removed duplicated region for block: B:200:0x05df A[SYNTHETIC, Splitter:B:200:0x05df] */
    /* JADX WARNING: Removed duplicated region for block: B:210:0x0601  */
    /* JADX WARNING: Removed duplicated region for block: B:230:0x0672  */
    /* JADX WARNING: Removed duplicated region for block: B:233:0x067c  */
    /* JADX WARNING: Removed duplicated region for block: B:246:0x06d6  */
    /* JADX WARNING: Removed duplicated region for block: B:253:0x06fe  */
    /* JADX WARNING: Removed duplicated region for block: B:259:0x072a  */
    /* JADX WARNING: Removed duplicated region for block: B:264:0x0767  */
    /* JADX WARNING: Removed duplicated region for block: B:288:0x07cc A[Catch:{ Exception -> 0x07d1 }] */
    /* JADX WARNING: Removed duplicated region for block: B:293:0x07d9  */
    /* JADX WARNING: Removed duplicated region for block: B:295:0x07df  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean start(org.telegram.messenger.FileLoadOperationStream r32, int r33, boolean r34) {
        /*
            r31 = this;
            r7 = r31
            int r0 = r7.currentDownloadChunkSize
            if (r0 != 0) goto L_0x0027
            int r0 = r7.totalBytesCount
            r1 = 1048576(0x100000, float:1.469368E-39)
            if (r0 >= r1) goto L_0x0015
            boolean r2 = r7.forceBig
            if (r2 == 0) goto L_0x0011
            goto L_0x0015
        L_0x0011:
            r2 = 32768(0x8000, float:4.5918E-41)
            goto L_0x0017
        L_0x0015:
            r2 = 131072(0x20000, float:1.83671E-40)
        L_0x0017:
            r7.currentDownloadChunkSize = r2
            if (r0 >= r1) goto L_0x0023
            boolean r0 = r7.forceBig
            if (r0 == 0) goto L_0x0020
            goto L_0x0023
        L_0x0020:
            int r0 = maxDownloadRequests
            goto L_0x0025
        L_0x0023:
            int r0 = maxDownloadRequestsBig
        L_0x0025:
            r7.currentMaxDownloadRequests = r0
        L_0x0027:
            int r0 = r7.state
            r8 = 1
            r9 = 0
            if (r0 == 0) goto L_0x002f
            r0 = 1
            goto L_0x0030
        L_0x002f:
            r0 = 0
        L_0x0030:
            r10 = r0
            boolean r11 = r7.paused
            r7.paused = r9
            if (r32 == 0) goto L_0x004c
            org.telegram.messenger.DispatchQueue r0 = org.telegram.messenger.Utilities.stageQueue
            org.telegram.messenger.FileLoadOperation$$ExternalSyntheticLambda12 r12 = new org.telegram.messenger.FileLoadOperation$$ExternalSyntheticLambda12
            r1 = r12
            r2 = r31
            r3 = r34
            r4 = r33
            r5 = r32
            r6 = r10
            r1.<init>(r2, r3, r4, r5, r6)
            r0.postRunnable(r12)
            goto L_0x005a
        L_0x004c:
            if (r11 == 0) goto L_0x005a
            if (r10 == 0) goto L_0x005a
            org.telegram.messenger.DispatchQueue r0 = org.telegram.messenger.Utilities.stageQueue
            org.telegram.messenger.FileLoadOperation$$ExternalSyntheticLambda0 r1 = new org.telegram.messenger.FileLoadOperation$$ExternalSyntheticLambda0
            r1.<init>(r7)
            r0.postRunnable(r1)
        L_0x005a:
            if (r10 == 0) goto L_0x005d
            return r11
        L_0x005d:
            org.telegram.tgnet.TLRPC$InputFileLocation r0 = r7.location
            if (r0 != 0) goto L_0x0069
            org.telegram.tgnet.TLRPC$InputWebFileLocation r0 = r7.webLocation
            if (r0 != 0) goto L_0x0069
            r7.onFail(r8, r9)
            return r9
        L_0x0069:
            int r0 = r7.currentDownloadChunkSize
            int r1 = r33 / r0
            int r1 = r1 * r0
            r7.streamStartOffset = r1
            boolean r1 = r7.allowDisordererFileSave
            if (r1 == 0) goto L_0x0089
            int r1 = r7.totalBytesCount
            if (r1 <= 0) goto L_0x0089
            if (r1 <= r0) goto L_0x0089
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r7.notLoadedBytesRanges = r0
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r7.notRequestedBytesRanges = r0
        L_0x0089:
            r0 = 0
            r1 = 0
            r2 = 0
            org.telegram.tgnet.TLRPC$InputWebFileLocation r3 = r7.webLocation
            java.lang.String r4 = ".iv.enc"
            java.lang.String r5 = ".iv"
            java.lang.String r6 = ".enc"
            java.lang.String r12 = ".temp.enc"
            java.lang.String r13 = ".temp"
            java.lang.String r14 = "."
            r15 = 0
            if (r3 == 0) goto L_0x0122
            org.telegram.messenger.WebFile r3 = r7.webFile
            java.lang.String r3 = r3.url
            java.lang.String r3 = org.telegram.messenger.Utilities.MD5(r3)
            boolean r8 = r7.encryptFile
            if (r8 == 0) goto L_0x00e4
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            r5.append(r3)
            r5.append(r12)
            java.lang.String r5 = r5.toString()
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            r8.append(r3)
            r8.append(r14)
            java.lang.String r12 = r7.ext
            r8.append(r12)
            r8.append(r6)
            java.lang.String r6 = r8.toString()
            byte[] r8 = r7.key
            if (r8 == 0) goto L_0x011d
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            r8.append(r3)
            r8.append(r4)
            java.lang.String r2 = r8.toString()
            goto L_0x011d
        L_0x00e4:
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r3)
            r4.append(r13)
            java.lang.String r4 = r4.toString()
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            r6.append(r3)
            r6.append(r14)
            java.lang.String r8 = r7.ext
            r6.append(r8)
            java.lang.String r6 = r6.toString()
            byte[] r8 = r7.key
            if (r8 == 0) goto L_0x011c
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            r8.append(r3)
            r8.append(r5)
            java.lang.String r2 = r8.toString()
            r5 = r4
            goto L_0x011d
        L_0x011c:
            r5 = r4
        L_0x011d:
            r3 = r0
            r17 = r10
            goto L_0x0375
        L_0x0122:
            org.telegram.tgnet.TLRPC$InputFileLocation r3 = r7.location
            r17 = r10
            long r9 = r3.volume_id
            java.lang.String r3 = ".pt"
            java.lang.String r8 = ".preload"
            r19 = r0
            java.lang.String r0 = "_"
            int r20 = (r9 > r15 ? 1 : (r9 == r15 ? 0 : -1))
            if (r20 == 0) goto L_0x026a
            org.telegram.tgnet.TLRPC$InputFileLocation r9 = r7.location
            int r9 = r9.local_id
            if (r9 == 0) goto L_0x026a
            int r9 = r7.datacenterId
            r10 = -2147483648(0xfffffffvar_, float:-0.0)
            if (r9 == r10) goto L_0x0264
            org.telegram.tgnet.TLRPC$InputFileLocation r9 = r7.location
            long r9 = r9.volume_id
            r20 = -2147483648(0xfffffffvar_, double:NaN)
            int r22 = (r9 > r20 ? 1 : (r9 == r20 ? 0 : -1))
            if (r22 == 0) goto L_0x0264
            int r9 = r7.datacenterId
            if (r9 != 0) goto L_0x0151
            goto L_0x0264
        L_0x0151:
            boolean r9 = r7.encryptFile
            if (r9 == 0) goto L_0x01c0
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            org.telegram.tgnet.TLRPC$InputFileLocation r5 = r7.location
            long r8 = r5.volume_id
            r3.append(r8)
            r3.append(r0)
            org.telegram.tgnet.TLRPC$InputFileLocation r5 = r7.location
            int r5 = r5.local_id
            r3.append(r5)
            r3.append(r12)
            java.lang.String r5 = r3.toString()
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            org.telegram.tgnet.TLRPC$InputFileLocation r8 = r7.location
            long r8 = r8.volume_id
            r3.append(r8)
            r3.append(r0)
            org.telegram.tgnet.TLRPC$InputFileLocation r8 = r7.location
            int r8 = r8.local_id
            r3.append(r8)
            r3.append(r14)
            java.lang.String r8 = r7.ext
            r3.append(r8)
            r3.append(r6)
            java.lang.String r6 = r3.toString()
            byte[] r3 = r7.key
            if (r3 == 0) goto L_0x01bc
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            org.telegram.tgnet.TLRPC$InputFileLocation r8 = r7.location
            long r8 = r8.volume_id
            r3.append(r8)
            r3.append(r0)
            org.telegram.tgnet.TLRPC$InputFileLocation r0 = r7.location
            int r0 = r0.local_id
            r3.append(r0)
            r3.append(r4)
            java.lang.String r2 = r3.toString()
            r3 = r19
            goto L_0x0375
        L_0x01bc:
            r3 = r19
            goto L_0x0375
        L_0x01c0:
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            org.telegram.tgnet.TLRPC$InputFileLocation r6 = r7.location
            long r9 = r6.volume_id
            r4.append(r9)
            r4.append(r0)
            org.telegram.tgnet.TLRPC$InputFileLocation r6 = r7.location
            int r6 = r6.local_id
            r4.append(r6)
            r4.append(r13)
            java.lang.String r4 = r4.toString()
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            org.telegram.tgnet.TLRPC$InputFileLocation r9 = r7.location
            long r9 = r9.volume_id
            r6.append(r9)
            r6.append(r0)
            org.telegram.tgnet.TLRPC$InputFileLocation r9 = r7.location
            int r9 = r9.local_id
            r6.append(r9)
            r6.append(r14)
            java.lang.String r9 = r7.ext
            r6.append(r9)
            java.lang.String r6 = r6.toString()
            byte[] r9 = r7.key
            if (r9 == 0) goto L_0x0220
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            org.telegram.tgnet.TLRPC$InputFileLocation r10 = r7.location
            long r12 = r10.volume_id
            r9.append(r12)
            r9.append(r0)
            org.telegram.tgnet.TLRPC$InputFileLocation r10 = r7.location
            int r10 = r10.local_id
            r9.append(r10)
            r9.append(r5)
            java.lang.String r2 = r9.toString()
        L_0x0220:
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$Range> r5 = r7.notLoadedBytesRanges
            if (r5 == 0) goto L_0x0242
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            org.telegram.tgnet.TLRPC$InputFileLocation r9 = r7.location
            long r9 = r9.volume_id
            r5.append(r9)
            r5.append(r0)
            org.telegram.tgnet.TLRPC$InputFileLocation r9 = r7.location
            int r9 = r9.local_id
            r5.append(r9)
            r5.append(r3)
            java.lang.String r3 = r5.toString()
            goto L_0x0244
        L_0x0242:
            r3 = r19
        L_0x0244:
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            org.telegram.tgnet.TLRPC$InputFileLocation r9 = r7.location
            long r9 = r9.volume_id
            r5.append(r9)
            r5.append(r0)
            org.telegram.tgnet.TLRPC$InputFileLocation r0 = r7.location
            int r0 = r0.local_id
            r5.append(r0)
            r5.append(r8)
            java.lang.String r1 = r5.toString()
            r5 = r4
            goto L_0x0375
        L_0x0264:
            r3 = 1
            r4 = 0
            r7.onFail(r3, r4)
            return r4
        L_0x026a:
            int r10 = r7.datacenterId
            if (r10 == 0) goto L_0x0807
            org.telegram.tgnet.TLRPC$InputFileLocation r10 = r7.location
            long r9 = r10.id
            int r14 = (r9 > r15 ? 1 : (r9 == r15 ? 0 : -1))
            if (r14 != 0) goto L_0x027a
            r4 = 0
            r5 = 1
            goto L_0x0809
        L_0x027a:
            boolean r9 = r7.encryptFile
            if (r9 == 0) goto L_0x02e0
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            int r5 = r7.datacenterId
            r3.append(r5)
            r3.append(r0)
            org.telegram.tgnet.TLRPC$InputFileLocation r5 = r7.location
            long r8 = r5.id
            r3.append(r8)
            r3.append(r12)
            java.lang.String r5 = r3.toString()
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            int r8 = r7.datacenterId
            r3.append(r8)
            r3.append(r0)
            org.telegram.tgnet.TLRPC$InputFileLocation r8 = r7.location
            long r8 = r8.id
            r3.append(r8)
            java.lang.String r8 = r7.ext
            r3.append(r8)
            r3.append(r6)
            java.lang.String r6 = r3.toString()
            byte[] r3 = r7.key
            if (r3 == 0) goto L_0x02dc
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            int r8 = r7.datacenterId
            r3.append(r8)
            r3.append(r0)
            org.telegram.tgnet.TLRPC$InputFileLocation r0 = r7.location
            long r8 = r0.id
            r3.append(r8)
            r3.append(r4)
            java.lang.String r2 = r3.toString()
            r3 = r19
            goto L_0x0375
        L_0x02dc:
            r3 = r19
            goto L_0x0375
        L_0x02e0:
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            int r6 = r7.datacenterId
            r4.append(r6)
            r4.append(r0)
            org.telegram.tgnet.TLRPC$InputFileLocation r6 = r7.location
            long r9 = r6.id
            r4.append(r9)
            r4.append(r13)
            java.lang.String r4 = r4.toString()
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            int r9 = r7.datacenterId
            r6.append(r9)
            r6.append(r0)
            org.telegram.tgnet.TLRPC$InputFileLocation r9 = r7.location
            long r9 = r9.id
            r6.append(r9)
            java.lang.String r9 = r7.ext
            r6.append(r9)
            java.lang.String r6 = r6.toString()
            byte[] r9 = r7.key
            if (r9 == 0) goto L_0x0337
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            int r10 = r7.datacenterId
            r9.append(r10)
            r9.append(r0)
            org.telegram.tgnet.TLRPC$InputFileLocation r10 = r7.location
            long r12 = r10.id
            r9.append(r12)
            r9.append(r5)
            java.lang.String r2 = r9.toString()
        L_0x0337:
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$Range> r5 = r7.notLoadedBytesRanges
            if (r5 == 0) goto L_0x0357
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            int r9 = r7.datacenterId
            r5.append(r9)
            r5.append(r0)
            org.telegram.tgnet.TLRPC$InputFileLocation r9 = r7.location
            long r9 = r9.id
            r5.append(r9)
            r5.append(r3)
            java.lang.String r3 = r5.toString()
            goto L_0x0359
        L_0x0357:
            r3 = r19
        L_0x0359:
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            int r9 = r7.datacenterId
            r5.append(r9)
            r5.append(r0)
            org.telegram.tgnet.TLRPC$InputFileLocation r0 = r7.location
            long r9 = r0.id
            r5.append(r9)
            r5.append(r8)
            java.lang.String r1 = r5.toString()
            r5 = r4
        L_0x0375:
            java.util.ArrayList r0 = new java.util.ArrayList
            int r4 = r7.currentMaxDownloadRequests
            r0.<init>(r4)
            r7.requestInfos = r0
            java.util.ArrayList r0 = new java.util.ArrayList
            int r4 = r7.currentMaxDownloadRequests
            r8 = 1
            int r4 = r4 - r8
            r0.<init>(r4)
            r7.delayedRequestInfos = r0
            r7.state = r8
            java.lang.Object r0 = r7.parentObject
            boolean r4 = r0 instanceof org.telegram.tgnet.TLRPC.TL_theme
            if (r4 == 0) goto L_0x03b7
            org.telegram.tgnet.TLRPC$TL_theme r0 = (org.telegram.tgnet.TLRPC.TL_theme) r0
            java.io.File r4 = new java.io.File
            java.io.File r8 = org.telegram.messenger.ApplicationLoader.getFilesDirFixed()
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            java.lang.String r10 = "remote"
            r9.append(r10)
            long r12 = r0.id
            r9.append(r12)
            java.lang.String r10 = ".attheme"
            r9.append(r10)
            java.lang.String r9 = r9.toString()
            r4.<init>(r8, r9)
            r7.cacheFileFinal = r4
            goto L_0x03c0
        L_0x03b7:
            java.io.File r0 = new java.io.File
            java.io.File r4 = r7.storePath
            r0.<init>(r4, r6)
            r7.cacheFileFinal = r0
        L_0x03c0:
            java.io.File r0 = r7.cacheFileFinal
            boolean r0 = r0.exists()
            if (r0 == 0) goto L_0x03e5
            java.lang.Object r4 = r7.parentObject
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_theme
            if (r4 != 0) goto L_0x03dd
            int r4 = r7.totalBytesCount
            if (r4 == 0) goto L_0x03e5
            long r8 = (long) r4
            java.io.File r4 = r7.cacheFileFinal
            long r12 = r4.length()
            int r4 = (r8 > r12 ? 1 : (r8 == r12 ? 0 : -1))
            if (r4 == 0) goto L_0x03e5
        L_0x03dd:
            java.io.File r4 = r7.cacheFileFinal
            r4.delete()
            r0 = 0
            r4 = r0
            goto L_0x03e6
        L_0x03e5:
            r4 = r0
        L_0x03e6:
            if (r4 != 0) goto L_0x07f0
            java.io.File r0 = new java.io.File
            java.io.File r8 = r7.tempPath
            r0.<init>(r8, r5)
            r7.cacheFileTemp = r0
            boolean r0 = r7.ungzip
            if (r0 == 0) goto L_0x040f
            java.io.File r0 = new java.io.File
            java.io.File r8 = r7.tempPath
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            r9.append(r5)
            java.lang.String r10 = ".gz"
            r9.append(r10)
            java.lang.String r9 = r9.toString()
            r0.<init>(r8, r9)
            r7.cacheFileGzipTemp = r0
        L_0x040f:
            r9 = 0
            boolean r0 = r7.encryptFile
            r10 = 32
            java.lang.String r12 = "rws"
            if (r0 == 0) goto L_0x048c
            java.io.File r0 = new java.io.File
            java.io.File r8 = org.telegram.messenger.FileLoader.getInternalCacheDir()
            java.lang.StringBuilder r13 = new java.lang.StringBuilder
            r13.<init>()
            r13.append(r6)
            java.lang.String r14 = ".key"
            r13.append(r14)
            java.lang.String r13 = r13.toString()
            r0.<init>(r8, r13)
            r13 = r0
            java.io.RandomAccessFile r0 = new java.io.RandomAccessFile     // Catch:{ Exception -> 0x0488 }
            r0.<init>(r13, r12)     // Catch:{ Exception -> 0x0488 }
            r14 = r0
            long r19 = r13.length()     // Catch:{ Exception -> 0x0488 }
            byte[] r0 = new byte[r10]     // Catch:{ Exception -> 0x0488 }
            r7.encryptKey = r0     // Catch:{ Exception -> 0x0488 }
            r8 = 16
            byte[] r10 = new byte[r8]     // Catch:{ Exception -> 0x0488 }
            r7.encryptIv = r10     // Catch:{ Exception -> 0x0488 }
            int r10 = (r19 > r15 ? 1 : (r19 == r15 ? 0 : -1))
            if (r10 <= 0) goto L_0x045f
            r22 = 48
            long r22 = r19 % r22
            int r10 = (r22 > r15 ? 1 : (r22 == r15 ? 0 : -1))
            if (r10 != 0) goto L_0x045f
            r10 = 32
            r15 = 0
            r14.read(r0, r15, r10)     // Catch:{ Exception -> 0x0488 }
            byte[] r0 = r7.encryptIv     // Catch:{ Exception -> 0x0488 }
            r14.read(r0, r15, r8)     // Catch:{ Exception -> 0x0488 }
            goto L_0x0478
        L_0x045f:
            java.security.SecureRandom r0 = org.telegram.messenger.Utilities.random     // Catch:{ Exception -> 0x0488 }
            byte[] r10 = r7.encryptKey     // Catch:{ Exception -> 0x0488 }
            r0.nextBytes(r10)     // Catch:{ Exception -> 0x0488 }
            java.security.SecureRandom r0 = org.telegram.messenger.Utilities.random     // Catch:{ Exception -> 0x0488 }
            byte[] r10 = r7.encryptIv     // Catch:{ Exception -> 0x0488 }
            r0.nextBytes(r10)     // Catch:{ Exception -> 0x0488 }
            byte[] r0 = r7.encryptKey     // Catch:{ Exception -> 0x0488 }
            r14.write(r0)     // Catch:{ Exception -> 0x0488 }
            byte[] r0 = r7.encryptIv     // Catch:{ Exception -> 0x0488 }
            r14.write(r0)     // Catch:{ Exception -> 0x0488 }
            r9 = 1
        L_0x0478:
            java.nio.channels.FileChannel r0 = r14.getChannel()     // Catch:{ Exception -> 0x0480 }
            r0.close()     // Catch:{ Exception -> 0x0480 }
            goto L_0x0484
        L_0x0480:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ Exception -> 0x0488 }
        L_0x0484:
            r14.close()     // Catch:{ Exception -> 0x0488 }
            goto L_0x048c
        L_0x0488:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x048c:
            r10 = 1
            boolean[] r0 = new boolean[r10]
            r8 = 0
            r0[r8] = r8
            r10 = r0
            boolean r0 = r7.supportsPreloading
            r15 = 0
            if (r0 == 0) goto L_0x05f8
            if (r1 == 0) goto L_0x05f8
            java.io.File r0 = new java.io.File
            java.io.File r8 = r7.tempPath
            r0.<init>(r8, r1)
            r7.cacheFilePreload = r0
            r16 = 0
            java.io.RandomAccessFile r0 = new java.io.RandomAccessFile     // Catch:{ Exception -> 0x05c6 }
            java.io.File r8 = r7.cacheFilePreload     // Catch:{ Exception -> 0x05c6 }
            r0.<init>(r8, r12)     // Catch:{ Exception -> 0x05c6 }
            r7.preloadStream = r0     // Catch:{ Exception -> 0x05c6 }
            long r19 = r0.length()     // Catch:{ Exception -> 0x05c6 }
            r0 = 0
            r8 = 1
            r7.preloadStreamFileOffset = r8     // Catch:{ Exception -> 0x05c6 }
            long r13 = (long) r0     // Catch:{ Exception -> 0x05c6 }
            long r13 = r19 - r13
            r26 = 1
            int r8 = (r13 > r26 ? 1 : (r13 == r26 ? 0 : -1))
            if (r8 <= 0) goto L_0x05b4
            java.io.RandomAccessFile r8 = r7.preloadStream     // Catch:{ Exception -> 0x05c6 }
            byte r8 = r8.readByte()     // Catch:{ Exception -> 0x05c6 }
            if (r8 == 0) goto L_0x04c9
            r8 = 1
            goto L_0x04ca
        L_0x04c9:
            r8 = 0
        L_0x04ca:
            r13 = 0
            r10[r13] = r8     // Catch:{ Exception -> 0x05c6 }
            int r0 = r0 + 1
        L_0x04cf:
            long r13 = (long) r0     // Catch:{ Exception -> 0x05c6 }
            int r18 = (r13 > r19 ? 1 : (r13 == r19 ? 0 : -1))
            if (r18 >= 0) goto L_0x05ac
            long r13 = (long) r0     // Catch:{ Exception -> 0x05c6 }
            long r13 = r19 - r13
            r24 = 4
            int r18 = (r13 > r24 ? 1 : (r13 == r24 ? 0 : -1))
            if (r18 >= 0) goto L_0x04e6
            r26 = r4
            r28 = r5
            r14 = r9
            r27 = r10
            goto L_0x05bb
        L_0x04e6:
            java.io.RandomAccessFile r13 = r7.preloadStream     // Catch:{ Exception -> 0x05c6 }
            int r13 = r13.readInt()     // Catch:{ Exception -> 0x05c6 }
            int r0 = r0 + 4
            r14 = r9
            long r8 = (long) r0
            long r8 = r19 - r8
            r24 = 4
            int r26 = (r8 > r24 ? 1 : (r8 == r24 ? 0 : -1))
            if (r26 < 0) goto L_0x05a5
            if (r13 < 0) goto L_0x05a5
            int r8 = r7.totalBytesCount     // Catch:{ Exception -> 0x059d }
            if (r13 <= r8) goto L_0x0506
            r26 = r4
            r28 = r5
            r27 = r10
            goto L_0x05bb
        L_0x0506:
            java.io.RandomAccessFile r8 = r7.preloadStream     // Catch:{ Exception -> 0x059d }
            int r8 = r8.readInt()     // Catch:{ Exception -> 0x059d }
            int r0 = r0 + 4
            r26 = r4
            r9 = r5
            long r4 = (long) r0
            long r4 = r19 - r4
            r28 = r9
            r27 = r10
            long r9 = (long) r8
            int r29 = (r4 > r9 ? 1 : (r4 == r9 ? 0 : -1))
            if (r29 < 0) goto L_0x05bb
            int r4 = r7.currentDownloadChunkSize     // Catch:{ Exception -> 0x05c4 }
            if (r8 <= r4) goto L_0x0523
            goto L_0x05bb
        L_0x0523:
            org.telegram.messenger.FileLoadOperation$PreloadRange r4 = new org.telegram.messenger.FileLoadOperation$PreloadRange     // Catch:{ Exception -> 0x05c4 }
            r4.<init>(r0, r8)     // Catch:{ Exception -> 0x05c4 }
            int r0 = r0 + r8
            java.io.RandomAccessFile r5 = r7.preloadStream     // Catch:{ Exception -> 0x05c4 }
            long r9 = (long) r0     // Catch:{ Exception -> 0x05c4 }
            r5.seek(r9)     // Catch:{ Exception -> 0x05c4 }
            long r9 = (long) r0     // Catch:{ Exception -> 0x05c4 }
            long r9 = r19 - r9
            r29 = 12
            int r5 = (r9 > r29 ? 1 : (r9 == r29 ? 0 : -1))
            if (r5 >= 0) goto L_0x053a
            goto L_0x05bb
        L_0x053a:
            java.io.RandomAccessFile r5 = r7.preloadStream     // Catch:{ Exception -> 0x05c4 }
            int r5 = r5.readInt()     // Catch:{ Exception -> 0x05c4 }
            r7.foundMoovSize = r5     // Catch:{ Exception -> 0x05c4 }
            if (r5 == 0) goto L_0x0555
            int r9 = r7.nextPreloadDownloadOffset     // Catch:{ Exception -> 0x05c4 }
            int r10 = r7.totalBytesCount     // Catch:{ Exception -> 0x05c4 }
            r29 = 2
            int r10 = r10 / 2
            if (r9 <= r10) goto L_0x0550
            r9 = 2
            goto L_0x0551
        L_0x0550:
            r9 = 1
        L_0x0551:
            r7.moovFound = r9     // Catch:{ Exception -> 0x05c4 }
            r7.preloadNotRequestedBytesCount = r5     // Catch:{ Exception -> 0x05c4 }
        L_0x0555:
            java.io.RandomAccessFile r5 = r7.preloadStream     // Catch:{ Exception -> 0x05c4 }
            int r5 = r5.readInt()     // Catch:{ Exception -> 0x05c4 }
            r7.nextPreloadDownloadOffset = r5     // Catch:{ Exception -> 0x05c4 }
            java.io.RandomAccessFile r5 = r7.preloadStream     // Catch:{ Exception -> 0x05c4 }
            int r5 = r5.readInt()     // Catch:{ Exception -> 0x05c4 }
            r7.nextAtomOffset = r5     // Catch:{ Exception -> 0x05c4 }
            int r0 = r0 + 12
            android.util.SparseArray<org.telegram.messenger.FileLoadOperation$PreloadRange> r5 = r7.preloadedBytesRanges     // Catch:{ Exception -> 0x05c4 }
            if (r5 != 0) goto L_0x0572
            android.util.SparseArray r5 = new android.util.SparseArray     // Catch:{ Exception -> 0x05c4 }
            r5.<init>()     // Catch:{ Exception -> 0x05c4 }
            r7.preloadedBytesRanges = r5     // Catch:{ Exception -> 0x05c4 }
        L_0x0572:
            android.util.SparseIntArray r5 = r7.requestedPreloadedBytesRanges     // Catch:{ Exception -> 0x05c4 }
            if (r5 != 0) goto L_0x057d
            android.util.SparseIntArray r5 = new android.util.SparseIntArray     // Catch:{ Exception -> 0x05c4 }
            r5.<init>()     // Catch:{ Exception -> 0x05c4 }
            r7.requestedPreloadedBytesRanges = r5     // Catch:{ Exception -> 0x05c4 }
        L_0x057d:
            android.util.SparseArray<org.telegram.messenger.FileLoadOperation$PreloadRange> r5 = r7.preloadedBytesRanges     // Catch:{ Exception -> 0x05c4 }
            r5.put(r13, r4)     // Catch:{ Exception -> 0x05c4 }
            android.util.SparseIntArray r5 = r7.requestedPreloadedBytesRanges     // Catch:{ Exception -> 0x05c4 }
            r9 = 1
            r5.put(r13, r9)     // Catch:{ Exception -> 0x05c4 }
            int r5 = r7.totalPreloadedBytes     // Catch:{ Exception -> 0x05c4 }
            int r5 = r5 + r8
            r7.totalPreloadedBytes = r5     // Catch:{ Exception -> 0x05c4 }
            int r5 = r7.preloadStreamFileOffset     // Catch:{ Exception -> 0x05c4 }
            int r9 = r8 + 20
            int r5 = r5 + r9
            r7.preloadStreamFileOffset = r5     // Catch:{ Exception -> 0x05c4 }
            r9 = r14
            r4 = r26
            r10 = r27
            r5 = r28
            goto L_0x04cf
        L_0x059d:
            r0 = move-exception
            r26 = r4
            r28 = r5
            r27 = r10
            goto L_0x05ce
        L_0x05a5:
            r26 = r4
            r28 = r5
            r27 = r10
            goto L_0x05bb
        L_0x05ac:
            r26 = r4
            r28 = r5
            r14 = r9
            r27 = r10
            goto L_0x05bb
        L_0x05b4:
            r26 = r4
            r28 = r5
            r14 = r9
            r27 = r10
        L_0x05bb:
            java.io.RandomAccessFile r4 = r7.preloadStream     // Catch:{ Exception -> 0x05c4 }
            int r5 = r7.preloadStreamFileOffset     // Catch:{ Exception -> 0x05c4 }
            long r8 = (long) r5     // Catch:{ Exception -> 0x05c4 }
            r4.seek(r8)     // Catch:{ Exception -> 0x05c4 }
            goto L_0x05d1
        L_0x05c4:
            r0 = move-exception
            goto L_0x05ce
        L_0x05c6:
            r0 = move-exception
            r26 = r4
            r28 = r5
            r14 = r9
            r27 = r10
        L_0x05ce:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x05d1:
            boolean r0 = r7.isPreloadVideoOperation
            if (r0 != 0) goto L_0x05ff
            android.util.SparseArray<org.telegram.messenger.FileLoadOperation$PreloadRange> r0 = r7.preloadedBytesRanges
            if (r0 != 0) goto L_0x05ff
            r7.cacheFilePreload = r15
            java.io.RandomAccessFile r0 = r7.preloadStream     // Catch:{ Exception -> 0x05f3 }
            if (r0 == 0) goto L_0x05f2
            java.nio.channels.FileChannel r0 = r0.getChannel()     // Catch:{ Exception -> 0x05e7 }
            r0.close()     // Catch:{ Exception -> 0x05e7 }
            goto L_0x05eb
        L_0x05e7:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)     // Catch:{ Exception -> 0x05f3 }
        L_0x05eb:
            java.io.RandomAccessFile r0 = r7.preloadStream     // Catch:{ Exception -> 0x05f3 }
            r0.close()     // Catch:{ Exception -> 0x05f3 }
            r7.preloadStream = r15     // Catch:{ Exception -> 0x05f3 }
        L_0x05f2:
            goto L_0x05ff
        L_0x05f3:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x05ff
        L_0x05f8:
            r26 = r4
            r28 = r5
            r14 = r9
            r27 = r10
        L_0x05ff:
            if (r3 == 0) goto L_0x0672
            java.io.File r0 = new java.io.File
            java.io.File r4 = r7.tempPath
            r0.<init>(r4, r3)
            r7.cacheFileParts = r0
            java.io.RandomAccessFile r0 = new java.io.RandomAccessFile     // Catch:{ Exception -> 0x066b }
            java.io.File r4 = r7.cacheFileParts     // Catch:{ Exception -> 0x066b }
            r0.<init>(r4, r12)     // Catch:{ Exception -> 0x066b }
            r7.filePartsStream = r0     // Catch:{ Exception -> 0x066b }
            long r4 = r0.length()     // Catch:{ Exception -> 0x066b }
            r8 = 8
            long r8 = r4 % r8
            r19 = 4
            int r0 = (r8 > r19 ? 1 : (r8 == r19 ? 0 : -1))
            if (r0 != 0) goto L_0x0668
            long r4 = r4 - r19
            java.io.RandomAccessFile r0 = r7.filePartsStream     // Catch:{ Exception -> 0x066b }
            int r0 = r0.readInt()     // Catch:{ Exception -> 0x066b }
            long r8 = (long) r0     // Catch:{ Exception -> 0x066b }
            r19 = 2
            long r19 = r4 / r19
            int r10 = (r8 > r19 ? 1 : (r8 == r19 ? 0 : -1))
            if (r10 > 0) goto L_0x0665
            r8 = 0
        L_0x0633:
            if (r8 >= r0) goto L_0x0662
            java.io.RandomAccessFile r9 = r7.filePartsStream     // Catch:{ Exception -> 0x066b }
            int r9 = r9.readInt()     // Catch:{ Exception -> 0x066b }
            java.io.RandomAccessFile r10 = r7.filePartsStream     // Catch:{ Exception -> 0x066b }
            int r10 = r10.readInt()     // Catch:{ Exception -> 0x066b }
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$Range> r13 = r7.notLoadedBytesRanges     // Catch:{ Exception -> 0x066b }
            org.telegram.messenger.FileLoadOperation$Range r15 = new org.telegram.messenger.FileLoadOperation$Range     // Catch:{ Exception -> 0x066b }
            r19 = r1
            r1 = 0
            r15.<init>(r9, r10)     // Catch:{ Exception -> 0x0660 }
            r13.add(r15)     // Catch:{ Exception -> 0x0660 }
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$Range> r1 = r7.notRequestedBytesRanges     // Catch:{ Exception -> 0x0660 }
            org.telegram.messenger.FileLoadOperation$Range r13 = new org.telegram.messenger.FileLoadOperation$Range     // Catch:{ Exception -> 0x0660 }
            r15 = 0
            r13.<init>(r9, r10)     // Catch:{ Exception -> 0x0660 }
            r1.add(r13)     // Catch:{ Exception -> 0x0660 }
            int r8 = r8 + 1
            r1 = r19
            r15 = 0
            goto L_0x0633
        L_0x0660:
            r0 = move-exception
            goto L_0x066e
        L_0x0662:
            r19 = r1
            goto L_0x066a
        L_0x0665:
            r19 = r1
            goto L_0x066a
        L_0x0668:
            r19 = r1
        L_0x066a:
            goto L_0x0674
        L_0x066b:
            r0 = move-exception
            r19 = r1
        L_0x066e:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x0674
        L_0x0672:
            r19 = r1
        L_0x0674:
            java.io.File r0 = r7.cacheFileTemp
            boolean r0 = r0.exists()
            if (r0 == 0) goto L_0x06d6
            if (r14 == 0) goto L_0x0685
            java.io.File r0 = r7.cacheFileTemp
            r0.delete()
            goto L_0x06fa
        L_0x0685:
            java.io.File r0 = r7.cacheFileTemp
            long r0 = r0.length()
            if (r2 == 0) goto L_0x069e
            int r4 = r7.currentDownloadChunkSize
            long r4 = (long) r4
            long r4 = r0 % r4
            r8 = 0
            int r10 = (r4 > r8 ? 1 : (r4 == r8 ? 0 : -1))
            if (r10 == 0) goto L_0x069e
            r4 = 0
            r7.downloadedBytes = r4
            r7.requestedBytesCount = r4
            goto L_0x06ae
        L_0x069e:
            java.io.File r4 = r7.cacheFileTemp
            long r4 = r4.length()
            int r5 = (int) r4
            int r4 = r7.currentDownloadChunkSize
            int r5 = r5 / r4
            int r5 = r5 * r4
            r7.downloadedBytes = r5
            r7.requestedBytesCount = r5
        L_0x06ae:
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$Range> r4 = r7.notLoadedBytesRanges
            if (r4 == 0) goto L_0x06d5
            boolean r4 = r4.isEmpty()
            if (r4 == 0) goto L_0x06d5
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$Range> r4 = r7.notLoadedBytesRanges
            org.telegram.messenger.FileLoadOperation$Range r5 = new org.telegram.messenger.FileLoadOperation$Range
            int r9 = r7.downloadedBytes
            int r10 = r7.totalBytesCount
            r13 = 0
            r5.<init>(r9, r10)
            r4.add(r5)
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$Range> r4 = r7.notRequestedBytesRanges
            org.telegram.messenger.FileLoadOperation$Range r5 = new org.telegram.messenger.FileLoadOperation$Range
            int r9 = r7.downloadedBytes
            int r10 = r7.totalBytesCount
            r5.<init>(r9, r10)
            r4.add(r5)
        L_0x06d5:
            goto L_0x06fa
        L_0x06d6:
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$Range> r0 = r7.notLoadedBytesRanges
            if (r0 == 0) goto L_0x06fa
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L_0x06fa
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$Range> r0 = r7.notLoadedBytesRanges
            org.telegram.messenger.FileLoadOperation$Range r1 = new org.telegram.messenger.FileLoadOperation$Range
            int r4 = r7.totalBytesCount
            r5 = 0
            r8 = 0
            r1.<init>(r8, r4)
            r0.add(r1)
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$Range> r0 = r7.notRequestedBytesRanges
            org.telegram.messenger.FileLoadOperation$Range r1 = new org.telegram.messenger.FileLoadOperation$Range
            int r4 = r7.totalBytesCount
            r1.<init>(r8, r4)
            r0.add(r1)
        L_0x06fa:
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$Range> r0 = r7.notLoadedBytesRanges
            if (r0 == 0) goto L_0x0726
            int r1 = r7.totalBytesCount
            r7.downloadedBytes = r1
            int r0 = r0.size()
            r1 = 0
        L_0x0707:
            if (r1 >= r0) goto L_0x0722
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$Range> r4 = r7.notLoadedBytesRanges
            java.lang.Object r4 = r4.get(r1)
            org.telegram.messenger.FileLoadOperation$Range r4 = (org.telegram.messenger.FileLoadOperation.Range) r4
            int r5 = r7.downloadedBytes
            int r9 = r4.end
            int r10 = r4.start
            int r9 = r9 - r10
            int r5 = r5 - r9
            r7.downloadedBytes = r5
            int r1 = r1 + 1
            goto L_0x0707
        L_0x0722:
            int r1 = r7.downloadedBytes
            r7.requestedBytesCount = r1
        L_0x0726:
            boolean r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r0 == 0) goto L_0x0765
            boolean r0 = r7.isPreloadVideoOperation
            if (r0 == 0) goto L_0x0745
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "start preloading file to temp = "
            r0.append(r1)
            java.io.File r1 = r7.cacheFileTemp
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            org.telegram.messenger.FileLog.d(r0)
            goto L_0x0765
        L_0x0745:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "start loading file to temp = "
            r0.append(r1)
            java.io.File r1 = r7.cacheFileTemp
            r0.append(r1)
            java.lang.String r1 = " final = "
            r0.append(r1)
            java.io.File r1 = r7.cacheFileFinal
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            org.telegram.messenger.FileLog.d(r0)
        L_0x0765:
            if (r2 == 0) goto L_0x07ad
            java.io.File r0 = new java.io.File
            java.io.File r1 = r7.tempPath
            r0.<init>(r1, r2)
            r7.cacheIvTemp = r0
            java.io.RandomAccessFile r0 = new java.io.RandomAccessFile     // Catch:{ Exception -> 0x07a4 }
            java.io.File r1 = r7.cacheIvTemp     // Catch:{ Exception -> 0x07a4 }
            r0.<init>(r1, r12)     // Catch:{ Exception -> 0x07a4 }
            r7.fiv = r0     // Catch:{ Exception -> 0x07a4 }
            int r0 = r7.downloadedBytes     // Catch:{ Exception -> 0x07a4 }
            if (r0 == 0) goto L_0x07a3
            if (r14 != 0) goto L_0x07a3
            java.io.File r0 = r7.cacheIvTemp     // Catch:{ Exception -> 0x07a4 }
            long r0 = r0.length()     // Catch:{ Exception -> 0x07a4 }
            r4 = 0
            int r9 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1))
            if (r9 <= 0) goto L_0x079e
            r9 = 32
            long r9 = r0 % r9
            int r13 = (r9 > r4 ? 1 : (r9 == r4 ? 0 : -1))
            if (r13 != 0) goto L_0x079e
            java.io.RandomAccessFile r4 = r7.fiv     // Catch:{ Exception -> 0x07a4 }
            byte[] r5 = r7.iv     // Catch:{ Exception -> 0x07a4 }
            r8 = 32
            r9 = 0
            r4.read(r5, r9, r8)     // Catch:{ Exception -> 0x07a4 }
            goto L_0x07a3
        L_0x079e:
            r4 = 0
            r7.downloadedBytes = r4     // Catch:{ Exception -> 0x07a4 }
            r7.requestedBytesCount = r4     // Catch:{ Exception -> 0x07a4 }
        L_0x07a3:
            goto L_0x07ad
        L_0x07a4:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            r1 = 0
            r7.downloadedBytes = r1
            r7.requestedBytesCount = r1
        L_0x07ad:
            boolean r0 = r7.isPreloadVideoOperation
            if (r0 != 0) goto L_0x07bc
            int r0 = r7.downloadedBytes
            if (r0 == 0) goto L_0x07bc
            int r0 = r7.totalBytesCount
            if (r0 <= 0) goto L_0x07bc
            r31.copyNotLoadedRanges()
        L_0x07bc:
            r31.updateProgress()
            java.io.RandomAccessFile r0 = new java.io.RandomAccessFile     // Catch:{ Exception -> 0x07d1 }
            java.io.File r1 = r7.cacheFileTemp     // Catch:{ Exception -> 0x07d1 }
            r0.<init>(r1, r12)     // Catch:{ Exception -> 0x07d1 }
            r7.fileOutputStream = r0     // Catch:{ Exception -> 0x07d1 }
            int r1 = r7.downloadedBytes     // Catch:{ Exception -> 0x07d1 }
            if (r1 == 0) goto L_0x07d0
            long r4 = (long) r1     // Catch:{ Exception -> 0x07d1 }
            r0.seek(r4)     // Catch:{ Exception -> 0x07d1 }
        L_0x07d0:
            goto L_0x07d5
        L_0x07d1:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x07d5:
            java.io.RandomAccessFile r0 = r7.fileOutputStream
            if (r0 != 0) goto L_0x07df
            r1 = 1
            r4 = 0
            r7.onFail(r1, r4)
            return r4
        L_0x07df:
            r1 = 1
            r7.started = r1
            org.telegram.messenger.DispatchQueue r0 = org.telegram.messenger.Utilities.stageQueue
            org.telegram.messenger.FileLoadOperation$$ExternalSyntheticLambda1 r4 = new org.telegram.messenger.FileLoadOperation$$ExternalSyntheticLambda1
            r5 = r27
            r4.<init>(r7, r5)
            r0.postRunnable(r4)
            r5 = 1
            goto L_0x0806
        L_0x07f0:
            r19 = r1
            r26 = r4
            r28 = r5
            r1 = 1
            r7.started = r1
            r4 = 0
            r7.onFinishLoadingFile(r4)     // Catch:{ Exception -> 0x07ff }
            r5 = 1
            goto L_0x0806
        L_0x07ff:
            r0 = move-exception
            r1 = r0
            r0 = r1
            r5 = 1
            r7.onFail(r5, r4)
        L_0x0806:
            return r5
        L_0x0807:
            r4 = 0
            r5 = 1
        L_0x0809:
            r7.onFail(r5, r4)
            return r4
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.FileLoadOperation.start(org.telegram.messenger.FileLoadOperationStream, int, boolean):boolean");
    }

    /* renamed from: lambda$start$4$org-telegram-messenger-FileLoadOperation  reason: not valid java name */
    public /* synthetic */ void m547lambda$start$4$orgtelegrammessengerFileLoadOperation(boolean steamPriority, int streamOffset, FileLoadOperationStream stream, boolean alreadyStarted) {
        if (this.streamListeners == null) {
            this.streamListeners = new ArrayList<>();
        }
        if (steamPriority) {
            int i = this.currentDownloadChunkSize;
            int offset = (streamOffset / i) * i;
            RequestInfo requestInfo = this.priorityRequestInfo;
            if (!(requestInfo == null || requestInfo.offset == offset)) {
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
                this.streamPriorityStartOffset = offset;
            }
        } else {
            int i2 = this.currentDownloadChunkSize;
            this.streamStartOffset = (streamOffset / i2) * i2;
        }
        this.streamListeners.add(stream);
        if (alreadyStarted) {
            if (!(this.preloadedBytesRanges == null || getDownloadedLengthFromOffsetInternal(this.notLoadedBytesRanges, this.streamStartOffset, 1) != 0 || this.preloadedBytesRanges.get(this.streamStartOffset) == null)) {
                this.nextPartWasPreloaded = true;
            }
            startDownloadRequest();
            this.nextPartWasPreloaded = false;
        }
    }

    /* renamed from: lambda$start$5$org-telegram-messenger-FileLoadOperation  reason: not valid java name */
    public /* synthetic */ void m548lambda$start$5$orgtelegrammessengerFileLoadOperation(boolean[] preloaded) {
        int i = this.totalBytesCount;
        if (i == 0 || ((!this.isPreloadVideoOperation || !preloaded[0]) && this.downloadedBytes != i)) {
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
    public /* synthetic */ void m546xCLASSNAMEa74(boolean value) {
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
    public /* synthetic */ void m539lambda$cancel$7$orgtelegrammessengerFileLoadOperation(boolean deleteFiles) {
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
    public /* synthetic */ void m543x905dc9cf(boolean increment) {
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

    private int findNextPreloadDownloadOffset(int atomOffset, int partOffset, NativeByteBuffer partBuffer) {
        int partSize = partBuffer.limit();
        while (true) {
            if (atomOffset < partOffset - (this.preloadTempBuffer != null ? 16 : 0) || atomOffset >= partOffset + partSize) {
                return 0;
            }
            if (atomOffset >= (partOffset + partSize) - 16) {
                this.preloadTempBufferCount = (partOffset + partSize) - atomOffset;
                partBuffer.position(partBuffer.limit() - this.preloadTempBufferCount);
                partBuffer.readBytes(this.preloadTempBuffer, 0, this.preloadTempBufferCount, false);
                return partOffset + partSize;
            }
            if (this.preloadTempBufferCount != 0) {
                partBuffer.position(0);
                byte[] bArr = this.preloadTempBuffer;
                int i = this.preloadTempBufferCount;
                partBuffer.readBytes(bArr, i, 16 - i, false);
                this.preloadTempBufferCount = 0;
            } else {
                partBuffer.position(atomOffset - partOffset);
                partBuffer.readBytes(this.preloadTempBuffer, 0, 16, false);
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
                return -atomSize;
            }
            if (atomSize + atomOffset >= partOffset + partSize) {
                return atomSize + atomOffset;
            }
            atomOffset += atomSize;
        }
        return 0;
    }

    private void requestFileOffsets(int offset) {
        if (!this.requestingCdnOffsets) {
            this.requestingCdnOffsets = true;
            TLRPC.TL_upload_getCdnFileHashes req = new TLRPC.TL_upload_getCdnFileHashes();
            req.file_token = this.cdnToken;
            req.offset = offset;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new FileLoadOperation$$ExternalSyntheticLambda3(this), (QuickAckDelegate) null, (WriteToSocketDelegate) null, 0, this.datacenterId, 1, true);
        }
    }

    /* renamed from: lambda$requestFileOffsets$9$org-telegram-messenger-FileLoadOperation  reason: not valid java name */
    public /* synthetic */ void m545x6ac3var_(TLObject response, TLRPC.TL_error error) {
        if (error != null) {
            onFail(false, 0);
            return;
        }
        this.requestingCdnOffsets = false;
        TLRPC.Vector vector = (TLRPC.Vector) response;
        if (!vector.objects.isEmpty()) {
            if (this.cdnHashes == null) {
                this.cdnHashes = new SparseArray<>();
            }
            for (int a = 0; a < vector.objects.size(); a++) {
                TLRPC.TL_fileHash hash = (TLRPC.TL_fileHash) vector.objects.get(a);
                this.cdnHashes.put(hash.offset, hash);
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
    /* JADX WARNING: Removed duplicated region for block: B:74:0x01d9 A[Catch:{ Exception -> 0x04ee }] */
    /* JADX WARNING: Removed duplicated region for block: B:75:0x01e7 A[Catch:{ Exception -> 0x04ee }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean processRequestResult(org.telegram.messenger.FileLoadOperation.RequestInfo r29, org.telegram.tgnet.TLRPC.TL_error r30) {
        /*
            r28 = this;
            r7 = r28
            r8 = r30
            int r0 = r7.state
            java.lang.String r1 = " offset "
            r9 = 1
            r10 = 0
            if (r0 == r9) goto L_0x0031
            boolean r0 = org.telegram.messenger.BuildVars.DEBUG_VERSION
            if (r0 == 0) goto L_0x0030
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r2 = "trying to write to finished file "
            r0.append(r2)
            java.io.File r2 = r7.cacheFileFinal
            r0.append(r2)
            r0.append(r1)
            int r1 = r29.offset
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            org.telegram.messenger.FileLog.d(r0)
        L_0x0030:
            return r10
        L_0x0031:
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$RequestInfo> r0 = r7.requestInfos
            r11 = r29
            r0.remove(r11)
            java.lang.String r0 = " secret = "
            java.lang.String r2 = " volume_id = "
            java.lang.String r3 = " access_hash = "
            java.lang.String r4 = " local_id = "
            java.lang.String r6 = " id = "
            if (r8 != 0) goto L_0x04f9
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$Range> r12 = r7.notLoadedBytesRanges     // Catch:{ Exception -> 0x04ee }
            if (r12 != 0) goto L_0x0054
            int r12 = r7.downloadedBytes     // Catch:{ Exception -> 0x04ee }
            int r13 = r29.offset     // Catch:{ Exception -> 0x04ee }
            if (r12 == r13) goto L_0x0054
            r28.delayRequestInfo(r29)     // Catch:{ Exception -> 0x04ee }
            return r10
        L_0x0054:
            org.telegram.tgnet.TLRPC$TL_upload_file r12 = r29.response     // Catch:{ Exception -> 0x04ee }
            if (r12 == 0) goto L_0x0061
            org.telegram.tgnet.TLRPC$TL_upload_file r12 = r29.response     // Catch:{ Exception -> 0x04ee }
            org.telegram.tgnet.NativeByteBuffer r12 = r12.bytes     // Catch:{ Exception -> 0x04ee }
            goto L_0x007c
        L_0x0061:
            org.telegram.tgnet.TLRPC$TL_upload_webFile r12 = r29.responseWeb     // Catch:{ Exception -> 0x04ee }
            if (r12 == 0) goto L_0x006e
            org.telegram.tgnet.TLRPC$TL_upload_webFile r12 = r29.responseWeb     // Catch:{ Exception -> 0x04ee }
            org.telegram.tgnet.NativeByteBuffer r12 = r12.bytes     // Catch:{ Exception -> 0x04ee }
            goto L_0x007c
        L_0x006e:
            org.telegram.tgnet.TLRPC$TL_upload_cdnFile r12 = r29.responseCdn     // Catch:{ Exception -> 0x04ee }
            if (r12 == 0) goto L_0x007b
            org.telegram.tgnet.TLRPC$TL_upload_cdnFile r12 = r29.responseCdn     // Catch:{ Exception -> 0x04ee }
            org.telegram.tgnet.NativeByteBuffer r12 = r12.bytes     // Catch:{ Exception -> 0x04ee }
            goto L_0x007c
        L_0x007b:
            r12 = 0
        L_0x007c:
            if (r12 == 0) goto L_0x04e8
            int r13 = r12.limit()     // Catch:{ Exception -> 0x04ee }
            if (r13 != 0) goto L_0x0086
            goto L_0x04e8
        L_0x0086:
            int r13 = r12.limit()     // Catch:{ Exception -> 0x04ee }
            boolean r14 = r7.isCdn     // Catch:{ Exception -> 0x04ee }
            r5 = 131072(0x20000, float:1.83671E-40)
            if (r14 == 0) goto L_0x00ac
            int r14 = r29.offset     // Catch:{ Exception -> 0x04ee }
            int r14 = r14 / r5
            int r15 = r14 * r5
            android.util.SparseArray<org.telegram.tgnet.TLRPC$TL_fileHash> r5 = r7.cdnHashes     // Catch:{ Exception -> 0x04ee }
            if (r5 == 0) goto L_0x00a2
            java.lang.Object r5 = r5.get(r15)     // Catch:{ Exception -> 0x04ee }
            org.telegram.tgnet.TLRPC$TL_fileHash r5 = (org.telegram.tgnet.TLRPC.TL_fileHash) r5     // Catch:{ Exception -> 0x04ee }
            goto L_0x00a3
        L_0x00a2:
            r5 = 0
        L_0x00a3:
            if (r5 != 0) goto L_0x00ac
            r28.delayRequestInfo(r29)     // Catch:{ Exception -> 0x04ee }
            r7.requestFileOffsets(r15)     // Catch:{ Exception -> 0x04ee }
            return r9
        L_0x00ac:
            org.telegram.tgnet.TLRPC$TL_upload_cdnFile r5 = r29.responseCdn     // Catch:{ Exception -> 0x04ee }
            r14 = 13
            r15 = 14
            r18 = 15
            r19 = 12
            if (r5 == 0) goto L_0x00ec
            int r5 = r29.offset     // Catch:{ Exception -> 0x04ee }
            int r5 = r5 / 16
            byte[] r9 = r7.cdnIv     // Catch:{ Exception -> 0x04ee }
            r10 = r5 & 255(0xff, float:3.57E-43)
            byte r10 = (byte) r10     // Catch:{ Exception -> 0x04ee }
            r9[r18] = r10     // Catch:{ Exception -> 0x04ee }
            int r10 = r5 >> 8
            r10 = r10 & 255(0xff, float:3.57E-43)
            byte r10 = (byte) r10     // Catch:{ Exception -> 0x04ee }
            r9[r15] = r10     // Catch:{ Exception -> 0x04ee }
            int r10 = r5 >> 16
            r10 = r10 & 255(0xff, float:3.57E-43)
            byte r10 = (byte) r10     // Catch:{ Exception -> 0x04ee }
            r9[r14] = r10     // Catch:{ Exception -> 0x04ee }
            int r10 = r5 >> 24
            r10 = r10 & 255(0xff, float:3.57E-43)
            byte r10 = (byte) r10     // Catch:{ Exception -> 0x04ee }
            r9[r19] = r10     // Catch:{ Exception -> 0x04ee }
            java.nio.ByteBuffer r9 = r12.buffer     // Catch:{ Exception -> 0x04ee }
            byte[] r10 = r7.cdnKey     // Catch:{ Exception -> 0x04ee }
            byte[] r14 = r7.cdnIv     // Catch:{ Exception -> 0x04ee }
            int r15 = r12.limit()     // Catch:{ Exception -> 0x04ee }
            r20 = r5
            r5 = 0
            org.telegram.messenger.Utilities.aesCtrDecryption(r9, r10, r14, r5, r15)     // Catch:{ Exception -> 0x04ee }
        L_0x00ec:
            boolean r5 = r7.isPreloadVideoOperation     // Catch:{ Exception -> 0x04ee }
            if (r5 == 0) goto L_0x01f4
            java.io.RandomAccessFile r0 = r7.preloadStream     // Catch:{ Exception -> 0x04ee }
            int r2 = r29.offset     // Catch:{ Exception -> 0x04ee }
            r0.writeInt(r2)     // Catch:{ Exception -> 0x04ee }
            java.io.RandomAccessFile r0 = r7.preloadStream     // Catch:{ Exception -> 0x04ee }
            r0.writeInt(r13)     // Catch:{ Exception -> 0x04ee }
            int r0 = r7.preloadStreamFileOffset     // Catch:{ Exception -> 0x04ee }
            int r0 = r0 + 8
            r7.preloadStreamFileOffset = r0     // Catch:{ Exception -> 0x04ee }
            java.io.RandomAccessFile r0 = r7.preloadStream     // Catch:{ Exception -> 0x04ee }
            java.nio.channels.FileChannel r0 = r0.getChannel()     // Catch:{ Exception -> 0x04ee }
            java.nio.ByteBuffer r2 = r12.buffer     // Catch:{ Exception -> 0x04ee }
            r0.write(r2)     // Catch:{ Exception -> 0x04ee }
            boolean r2 = org.telegram.messenger.BuildVars.DEBUG_VERSION     // Catch:{ Exception -> 0x04ee }
            if (r2 == 0) goto L_0x013b
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x04ee }
            r2.<init>()     // Catch:{ Exception -> 0x04ee }
            java.lang.String r3 = "save preload file part "
            r2.append(r3)     // Catch:{ Exception -> 0x04ee }
            java.io.File r3 = r7.cacheFilePreload     // Catch:{ Exception -> 0x04ee }
            r2.append(r3)     // Catch:{ Exception -> 0x04ee }
            r2.append(r1)     // Catch:{ Exception -> 0x04ee }
            int r1 = r29.offset     // Catch:{ Exception -> 0x04ee }
            r2.append(r1)     // Catch:{ Exception -> 0x04ee }
            java.lang.String r1 = " size "
            r2.append(r1)     // Catch:{ Exception -> 0x04ee }
            r2.append(r13)     // Catch:{ Exception -> 0x04ee }
            java.lang.String r1 = r2.toString()     // Catch:{ Exception -> 0x04ee }
            org.telegram.messenger.FileLog.d(r1)     // Catch:{ Exception -> 0x04ee }
        L_0x013b:
            android.util.SparseArray<org.telegram.messenger.FileLoadOperation$PreloadRange> r1 = r7.preloadedBytesRanges     // Catch:{ Exception -> 0x04ee }
            if (r1 != 0) goto L_0x0146
            android.util.SparseArray r1 = new android.util.SparseArray     // Catch:{ Exception -> 0x04ee }
            r1.<init>()     // Catch:{ Exception -> 0x04ee }
            r7.preloadedBytesRanges = r1     // Catch:{ Exception -> 0x04ee }
        L_0x0146:
            android.util.SparseArray<org.telegram.messenger.FileLoadOperation$PreloadRange> r1 = r7.preloadedBytesRanges     // Catch:{ Exception -> 0x04ee }
            int r2 = r29.offset     // Catch:{ Exception -> 0x04ee }
            org.telegram.messenger.FileLoadOperation$PreloadRange r3 = new org.telegram.messenger.FileLoadOperation$PreloadRange     // Catch:{ Exception -> 0x04ee }
            int r4 = r7.preloadStreamFileOffset     // Catch:{ Exception -> 0x04ee }
            r5 = 0
            r3.<init>(r4, r13)     // Catch:{ Exception -> 0x04ee }
            r1.put(r2, r3)     // Catch:{ Exception -> 0x04ee }
            int r1 = r7.totalPreloadedBytes     // Catch:{ Exception -> 0x04ee }
            int r1 = r1 + r13
            r7.totalPreloadedBytes = r1     // Catch:{ Exception -> 0x04ee }
            int r1 = r7.preloadStreamFileOffset     // Catch:{ Exception -> 0x04ee }
            int r1 = r1 + r13
            r7.preloadStreamFileOffset = r1     // Catch:{ Exception -> 0x04ee }
            int r1 = r7.moovFound     // Catch:{ Exception -> 0x04ee }
            if (r1 != 0) goto L_0x01a2
            int r1 = r7.nextAtomOffset     // Catch:{ Exception -> 0x04ee }
            int r2 = r29.offset     // Catch:{ Exception -> 0x04ee }
            int r1 = r7.findNextPreloadDownloadOffset(r1, r2, r12)     // Catch:{ Exception -> 0x04ee }
            if (r1 >= 0) goto L_0x0198
            int r1 = r1 * -1
            int r2 = r7.nextPreloadDownloadOffset     // Catch:{ Exception -> 0x04ee }
            int r3 = r7.currentDownloadChunkSize     // Catch:{ Exception -> 0x04ee }
            int r2 = r2 + r3
            r7.nextPreloadDownloadOffset = r2     // Catch:{ Exception -> 0x04ee }
            int r3 = r7.totalBytesCount     // Catch:{ Exception -> 0x04ee }
            r4 = 2
            int r3 = r3 / r4
            if (r2 >= r3) goto L_0x018b
            r2 = 1048576(0x100000, float:1.469368E-39)
            int r2 = r2 + r1
            r7.foundMoovSize = r2     // Catch:{ Exception -> 0x04ee }
            r7.preloadNotRequestedBytesCount = r2     // Catch:{ Exception -> 0x04ee }
            r2 = 1
            r7.moovFound = r2     // Catch:{ Exception -> 0x04ee }
            goto L_0x0194
        L_0x018b:
            r2 = 2097152(0x200000, float:2.938736E-39)
            r7.foundMoovSize = r2     // Catch:{ Exception -> 0x04ee }
            r7.preloadNotRequestedBytesCount = r2     // Catch:{ Exception -> 0x04ee }
            r2 = 2
            r7.moovFound = r2     // Catch:{ Exception -> 0x04ee }
        L_0x0194:
            r2 = -1
            r7.nextPreloadDownloadOffset = r2     // Catch:{ Exception -> 0x04ee }
            goto L_0x01a0
        L_0x0198:
            int r2 = r7.currentDownloadChunkSize     // Catch:{ Exception -> 0x04ee }
            int r3 = r1 / r2
            int r3 = r3 * r2
            r7.nextPreloadDownloadOffset = r3     // Catch:{ Exception -> 0x04ee }
        L_0x01a0:
            r7.nextAtomOffset = r1     // Catch:{ Exception -> 0x04ee }
        L_0x01a2:
            java.io.RandomAccessFile r1 = r7.preloadStream     // Catch:{ Exception -> 0x04ee }
            int r2 = r7.foundMoovSize     // Catch:{ Exception -> 0x04ee }
            r1.writeInt(r2)     // Catch:{ Exception -> 0x04ee }
            java.io.RandomAccessFile r1 = r7.preloadStream     // Catch:{ Exception -> 0x04ee }
            int r2 = r7.nextPreloadDownloadOffset     // Catch:{ Exception -> 0x04ee }
            r1.writeInt(r2)     // Catch:{ Exception -> 0x04ee }
            java.io.RandomAccessFile r1 = r7.preloadStream     // Catch:{ Exception -> 0x04ee }
            int r2 = r7.nextAtomOffset     // Catch:{ Exception -> 0x04ee }
            r1.writeInt(r2)     // Catch:{ Exception -> 0x04ee }
            int r1 = r7.preloadStreamFileOffset     // Catch:{ Exception -> 0x04ee }
            int r1 = r1 + 12
            r7.preloadStreamFileOffset = r1     // Catch:{ Exception -> 0x04ee }
            int r1 = r7.nextPreloadDownloadOffset     // Catch:{ Exception -> 0x04ee }
            if (r1 == 0) goto L_0x01d6
            int r2 = r7.moovFound     // Catch:{ Exception -> 0x04ee }
            if (r2 == 0) goto L_0x01c9
            int r2 = r7.foundMoovSize     // Catch:{ Exception -> 0x04ee }
            if (r2 < 0) goto L_0x01d6
        L_0x01c9:
            int r2 = r7.totalPreloadedBytes     // Catch:{ Exception -> 0x04ee }
            r3 = 2097152(0x200000, float:2.938736E-39)
            if (r2 > r3) goto L_0x01d6
            int r2 = r7.totalBytesCount     // Catch:{ Exception -> 0x04ee }
            if (r1 < r2) goto L_0x01d4
            goto L_0x01d6
        L_0x01d4:
            r1 = 0
            goto L_0x01d7
        L_0x01d6:
            r1 = 1
        L_0x01d7:
            if (r1 == 0) goto L_0x01e7
            java.io.RandomAccessFile r2 = r7.preloadStream     // Catch:{ Exception -> 0x04ee }
            r3 = 0
            r2.seek(r3)     // Catch:{ Exception -> 0x04ee }
            java.io.RandomAccessFile r2 = r7.preloadStream     // Catch:{ Exception -> 0x04ee }
            r3 = 1
            r2.write(r3)     // Catch:{ Exception -> 0x04ee }
            goto L_0x01f2
        L_0x01e7:
            int r2 = r7.moovFound     // Catch:{ Exception -> 0x04ee }
            if (r2 == 0) goto L_0x01f2
            int r2 = r7.foundMoovSize     // Catch:{ Exception -> 0x04ee }
            int r3 = r7.currentDownloadChunkSize     // Catch:{ Exception -> 0x04ee }
            int r2 = r2 - r3
            r7.foundMoovSize = r2     // Catch:{ Exception -> 0x04ee }
        L_0x01f2:
            goto L_0x0472
        L_0x01f4:
            int r5 = r7.downloadedBytes     // Catch:{ Exception -> 0x04ee }
            int r5 = r5 + r13
            r7.downloadedBytes = r5     // Catch:{ Exception -> 0x04ee }
            int r9 = r7.totalBytesCount     // Catch:{ Exception -> 0x04ee }
            if (r9 <= 0) goto L_0x0204
            if (r5 < r9) goto L_0x0201
            r5 = 1
            goto L_0x0202
        L_0x0201:
            r5 = 0
        L_0x0202:
            r9 = r5
            goto L_0x0217
        L_0x0204:
            int r10 = r7.currentDownloadChunkSize     // Catch:{ Exception -> 0x04ee }
            if (r13 != r10) goto L_0x0215
            if (r9 == r5) goto L_0x020e
            int r10 = r5 % r10
            if (r10 == 0) goto L_0x0213
        L_0x020e:
            if (r9 <= 0) goto L_0x0215
            if (r9 > r5) goto L_0x0213
            goto L_0x0215
        L_0x0213:
            r5 = 0
            goto L_0x0216
        L_0x0215:
            r5 = 1
        L_0x0216:
            r9 = r5
        L_0x0217:
            byte[] r5 = r7.key     // Catch:{ Exception -> 0x04ee }
            if (r5 == 0) goto L_0x0244
            java.nio.ByteBuffer r5 = r12.buffer     // Catch:{ Exception -> 0x04ee }
            byte[] r10 = r7.key     // Catch:{ Exception -> 0x04ee }
            byte[] r14 = r7.iv     // Catch:{ Exception -> 0x04ee }
            r23 = 0
            r24 = 1
            r25 = 0
            int r26 = r12.limit()     // Catch:{ Exception -> 0x04ee }
            r20 = r5
            r21 = r10
            r22 = r14
            org.telegram.messenger.Utilities.aesIgeEncryption(r20, r21, r22, r23, r24, r25, r26)     // Catch:{ Exception -> 0x04ee }
            if (r9 == 0) goto L_0x0244
            int r5 = r7.bytesCountPadding     // Catch:{ Exception -> 0x04ee }
            if (r5 == 0) goto L_0x0244
            int r5 = r12.limit()     // Catch:{ Exception -> 0x04ee }
            int r10 = r7.bytesCountPadding     // Catch:{ Exception -> 0x04ee }
            int r5 = r5 - r10
            r12.limit(r5)     // Catch:{ Exception -> 0x04ee }
        L_0x0244:
            boolean r5 = r7.encryptFile     // Catch:{ Exception -> 0x04ee }
            if (r5 == 0) goto L_0x027e
            int r5 = r29.offset     // Catch:{ Exception -> 0x04ee }
            int r5 = r5 / 16
            byte[] r10 = r7.encryptIv     // Catch:{ Exception -> 0x04ee }
            r14 = r5 & 255(0xff, float:3.57E-43)
            byte r14 = (byte) r14     // Catch:{ Exception -> 0x04ee }
            r10[r18] = r14     // Catch:{ Exception -> 0x04ee }
            int r14 = r5 >> 8
            r14 = r14 & 255(0xff, float:3.57E-43)
            byte r14 = (byte) r14     // Catch:{ Exception -> 0x04ee }
            r15 = 14
            r10[r15] = r14     // Catch:{ Exception -> 0x04ee }
            int r14 = r5 >> 16
            r14 = r14 & 255(0xff, float:3.57E-43)
            byte r14 = (byte) r14     // Catch:{ Exception -> 0x04ee }
            r15 = 13
            r10[r15] = r14     // Catch:{ Exception -> 0x04ee }
            int r14 = r5 >> 24
            r14 = r14 & 255(0xff, float:3.57E-43)
            byte r14 = (byte) r14     // Catch:{ Exception -> 0x04ee }
            r10[r19] = r14     // Catch:{ Exception -> 0x04ee }
            java.nio.ByteBuffer r10 = r12.buffer     // Catch:{ Exception -> 0x04ee }
            byte[] r14 = r7.encryptKey     // Catch:{ Exception -> 0x04ee }
            byte[] r15 = r7.encryptIv     // Catch:{ Exception -> 0x04ee }
            r16 = r5
            int r5 = r12.limit()     // Catch:{ Exception -> 0x04ee }
            r11 = 0
            org.telegram.messenger.Utilities.aesCtrDecryption(r10, r14, r15, r11, r5)     // Catch:{ Exception -> 0x04ee }
        L_0x027e:
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$Range> r5 = r7.notLoadedBytesRanges     // Catch:{ Exception -> 0x04ee }
            if (r5 == 0) goto L_0x02b0
            java.io.RandomAccessFile r5 = r7.fileOutputStream     // Catch:{ Exception -> 0x04ee }
            int r10 = r29.offset     // Catch:{ Exception -> 0x04ee }
            long r10 = (long) r10     // Catch:{ Exception -> 0x04ee }
            r5.seek(r10)     // Catch:{ Exception -> 0x04ee }
            boolean r5 = org.telegram.messenger.BuildVars.DEBUG_VERSION     // Catch:{ Exception -> 0x04ee }
            if (r5 == 0) goto L_0x02b0
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x04ee }
            r5.<init>()     // Catch:{ Exception -> 0x04ee }
            java.lang.String r10 = "save file part "
            r5.append(r10)     // Catch:{ Exception -> 0x04ee }
            java.io.File r10 = r7.cacheFileFinal     // Catch:{ Exception -> 0x04ee }
            r5.append(r10)     // Catch:{ Exception -> 0x04ee }
            r5.append(r1)     // Catch:{ Exception -> 0x04ee }
            int r1 = r29.offset     // Catch:{ Exception -> 0x04ee }
            r5.append(r1)     // Catch:{ Exception -> 0x04ee }
            java.lang.String r1 = r5.toString()     // Catch:{ Exception -> 0x04ee }
            org.telegram.messenger.FileLog.d(r1)     // Catch:{ Exception -> 0x04ee }
        L_0x02b0:
            java.io.RandomAccessFile r1 = r7.fileOutputStream     // Catch:{ Exception -> 0x04ee }
            java.nio.channels.FileChannel r1 = r1.getChannel()     // Catch:{ Exception -> 0x04ee }
            r10 = r1
            java.nio.ByteBuffer r1 = r12.buffer     // Catch:{ Exception -> 0x04ee }
            r10.write(r1)     // Catch:{ Exception -> 0x04ee }
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$Range> r1 = r7.notLoadedBytesRanges     // Catch:{ Exception -> 0x04ee }
            int r5 = r29.offset     // Catch:{ Exception -> 0x04ee }
            int r11 = r29.offset     // Catch:{ Exception -> 0x04ee }
            int r11 = r11 + r13
            r14 = 1
            r7.addPart(r1, r5, r11, r14)     // Catch:{ Exception -> 0x04ee }
            boolean r1 = r7.isCdn     // Catch:{ Exception -> 0x04ee }
            if (r1 == 0) goto L_0x0443
            int r1 = r29.offset     // Catch:{ Exception -> 0x04ee }
            r5 = 131072(0x20000, float:1.83671E-40)
            int r1 = r1 / r5
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$Range> r5 = r7.notCheckedCdnRanges     // Catch:{ Exception -> 0x04ee }
            int r5 = r5.size()     // Catch:{ Exception -> 0x04ee }
            r11 = 1
            r14 = 0
        L_0x02de:
            if (r14 >= r5) goto L_0x02fd
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$Range> r15 = r7.notCheckedCdnRanges     // Catch:{ Exception -> 0x04ee }
            java.lang.Object r15 = r15.get(r14)     // Catch:{ Exception -> 0x04ee }
            org.telegram.messenger.FileLoadOperation$Range r15 = (org.telegram.messenger.FileLoadOperation.Range) r15     // Catch:{ Exception -> 0x04ee }
            r16 = r5
            int r5 = r15.start     // Catch:{ Exception -> 0x04ee }
            if (r5 > r1) goto L_0x02f8
            int r5 = r15.end     // Catch:{ Exception -> 0x04ee }
            if (r1 > r5) goto L_0x02f8
            r11 = 0
            goto L_0x02ff
        L_0x02f8:
            int r14 = r14 + 1
            r5 = r16
            goto L_0x02de
        L_0x02fd:
            r16 = r5
        L_0x02ff:
            if (r11 != 0) goto L_0x043c
            r5 = 131072(0x20000, float:1.83671E-40)
            int r14 = r1 * r5
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$Range> r15 = r7.notLoadedBytesRanges     // Catch:{ Exception -> 0x04ee }
            int r15 = r7.getDownloadedLengthFromOffsetInternal(r15, r14, r5)     // Catch:{ Exception -> 0x04ee }
            if (r15 == 0) goto L_0x0435
            if (r15 == r5) goto L_0x0325
            int r5 = r7.totalBytesCount     // Catch:{ Exception -> 0x04ee }
            if (r5 <= 0) goto L_0x031a
            r26 = r10
            int r10 = r5 - r14
            if (r15 == r10) goto L_0x0327
            goto L_0x031c
        L_0x031a:
            r26 = r10
        L_0x031c:
            if (r5 > 0) goto L_0x0321
            if (r9 == 0) goto L_0x0321
            goto L_0x0327
        L_0x0321:
            r17 = r9
            goto L_0x0447
        L_0x0325:
            r26 = r10
        L_0x0327:
            android.util.SparseArray<org.telegram.tgnet.TLRPC$TL_fileHash> r5 = r7.cdnHashes     // Catch:{ Exception -> 0x04ee }
            java.lang.Object r5 = r5.get(r14)     // Catch:{ Exception -> 0x04ee }
            org.telegram.tgnet.TLRPC$TL_fileHash r5 = (org.telegram.tgnet.TLRPC.TL_fileHash) r5     // Catch:{ Exception -> 0x04ee }
            java.io.RandomAccessFile r10 = r7.fileReadStream     // Catch:{ Exception -> 0x04ee }
            if (r10 != 0) goto L_0x0349
            r10 = 131072(0x20000, float:1.83671E-40)
            byte[] r10 = new byte[r10]     // Catch:{ Exception -> 0x04ee }
            r7.cdnCheckBytes = r10     // Catch:{ Exception -> 0x04ee }
            java.io.RandomAccessFile r10 = new java.io.RandomAccessFile     // Catch:{ Exception -> 0x04ee }
            r17 = r9
            java.io.File r9 = r7.cacheFileTemp     // Catch:{ Exception -> 0x04ee }
            r27 = r11
            java.lang.String r11 = "r"
            r10.<init>(r9, r11)     // Catch:{ Exception -> 0x04ee }
            r7.fileReadStream = r10     // Catch:{ Exception -> 0x04ee }
            goto L_0x034d
        L_0x0349:
            r17 = r9
            r27 = r11
        L_0x034d:
            java.io.RandomAccessFile r9 = r7.fileReadStream     // Catch:{ Exception -> 0x04ee }
            long r10 = (long) r14     // Catch:{ Exception -> 0x04ee }
            r9.seek(r10)     // Catch:{ Exception -> 0x04ee }
            java.io.RandomAccessFile r9 = r7.fileReadStream     // Catch:{ Exception -> 0x04ee }
            byte[] r10 = r7.cdnCheckBytes     // Catch:{ Exception -> 0x04ee }
            r11 = 0
            r9.readFully(r10, r11, r15)     // Catch:{ Exception -> 0x04ee }
            boolean r9 = r7.encryptFile     // Catch:{ Exception -> 0x04ee }
            if (r9 == 0) goto L_0x0396
            int r9 = r14 / 16
            byte[] r10 = r7.encryptIv     // Catch:{ Exception -> 0x04ee }
            r11 = r9 & 255(0xff, float:3.57E-43)
            byte r11 = (byte) r11     // Catch:{ Exception -> 0x04ee }
            r10[r18] = r11     // Catch:{ Exception -> 0x04ee }
            int r11 = r9 >> 8
            r11 = r11 & 255(0xff, float:3.57E-43)
            byte r11 = (byte) r11     // Catch:{ Exception -> 0x04ee }
            r18 = 14
            r10[r18] = r11     // Catch:{ Exception -> 0x04ee }
            int r11 = r9 >> 16
            r11 = r11 & 255(0xff, float:3.57E-43)
            byte r11 = (byte) r11     // Catch:{ Exception -> 0x04ee }
            r18 = 13
            r10[r18] = r11     // Catch:{ Exception -> 0x04ee }
            int r11 = r9 >> 24
            r11 = r11 & 255(0xff, float:3.57E-43)
            byte r11 = (byte) r11     // Catch:{ Exception -> 0x04ee }
            r10[r19] = r11     // Catch:{ Exception -> 0x04ee }
            byte[] r11 = r7.cdnCheckBytes     // Catch:{ Exception -> 0x04ee }
            r18 = r9
            byte[] r9 = r7.encryptKey     // Catch:{ Exception -> 0x04ee }
            r23 = 0
            r25 = 0
            r20 = r11
            r21 = r9
            r22 = r10
            r24 = r15
            org.telegram.messenger.Utilities.aesCtrDecryptionByteArray(r20, r21, r22, r23, r24, r25)     // Catch:{ Exception -> 0x04ee }
        L_0x0396:
            byte[] r9 = r7.cdnCheckBytes     // Catch:{ Exception -> 0x04ee }
            r10 = 0
            byte[] r9 = org.telegram.messenger.Utilities.computeSHA256(r9, r10, r15)     // Catch:{ Exception -> 0x04ee }
            byte[] r10 = r5.hash     // Catch:{ Exception -> 0x04ee }
            boolean r10 = java.util.Arrays.equals(r9, r10)     // Catch:{ Exception -> 0x04ee }
            if (r10 != 0) goto L_0x0426
            boolean r10 = org.telegram.messenger.BuildVars.LOGS_ENABLED     // Catch:{ Exception -> 0x04ee }
            if (r10 == 0) goto L_0x041b
            org.telegram.tgnet.TLRPC$InputFileLocation r10 = r7.location     // Catch:{ Exception -> 0x04ee }
            if (r10 == 0) goto L_0x03f7
            java.lang.StringBuilder r10 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x04ee }
            r10.<init>()     // Catch:{ Exception -> 0x04ee }
            java.lang.String r11 = "invalid cdn hash "
            r10.append(r11)     // Catch:{ Exception -> 0x04ee }
            org.telegram.tgnet.TLRPC$InputFileLocation r11 = r7.location     // Catch:{ Exception -> 0x04ee }
            r10.append(r11)     // Catch:{ Exception -> 0x04ee }
            r10.append(r6)     // Catch:{ Exception -> 0x04ee }
            org.telegram.tgnet.TLRPC$InputFileLocation r6 = r7.location     // Catch:{ Exception -> 0x04ee }
            r11 = r5
            long r5 = r6.id     // Catch:{ Exception -> 0x04ee }
            r10.append(r5)     // Catch:{ Exception -> 0x04ee }
            r10.append(r4)     // Catch:{ Exception -> 0x04ee }
            org.telegram.tgnet.TLRPC$InputFileLocation r4 = r7.location     // Catch:{ Exception -> 0x04ee }
            int r4 = r4.local_id     // Catch:{ Exception -> 0x04ee }
            r10.append(r4)     // Catch:{ Exception -> 0x04ee }
            r10.append(r3)     // Catch:{ Exception -> 0x04ee }
            org.telegram.tgnet.TLRPC$InputFileLocation r3 = r7.location     // Catch:{ Exception -> 0x04ee }
            long r3 = r3.access_hash     // Catch:{ Exception -> 0x04ee }
            r10.append(r3)     // Catch:{ Exception -> 0x04ee }
            r10.append(r2)     // Catch:{ Exception -> 0x04ee }
            org.telegram.tgnet.TLRPC$InputFileLocation r2 = r7.location     // Catch:{ Exception -> 0x04ee }
            long r2 = r2.volume_id     // Catch:{ Exception -> 0x04ee }
            r10.append(r2)     // Catch:{ Exception -> 0x04ee }
            r10.append(r0)     // Catch:{ Exception -> 0x04ee }
            org.telegram.tgnet.TLRPC$InputFileLocation r0 = r7.location     // Catch:{ Exception -> 0x04ee }
            long r2 = r0.secret     // Catch:{ Exception -> 0x04ee }
            r10.append(r2)     // Catch:{ Exception -> 0x04ee }
            java.lang.String r0 = r10.toString()     // Catch:{ Exception -> 0x04ee }
            org.telegram.messenger.FileLog.e((java.lang.String) r0)     // Catch:{ Exception -> 0x04ee }
            goto L_0x041c
        L_0x03f7:
            r11 = r5
            org.telegram.tgnet.TLRPC$InputWebFileLocation r0 = r7.webLocation     // Catch:{ Exception -> 0x04ee }
            if (r0 == 0) goto L_0x041c
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x04ee }
            r0.<init>()     // Catch:{ Exception -> 0x04ee }
            java.lang.String r2 = "invalid cdn hash  "
            r0.append(r2)     // Catch:{ Exception -> 0x04ee }
            org.telegram.tgnet.TLRPC$InputWebFileLocation r2 = r7.webLocation     // Catch:{ Exception -> 0x04ee }
            r0.append(r2)     // Catch:{ Exception -> 0x04ee }
            r0.append(r6)     // Catch:{ Exception -> 0x04ee }
            java.lang.String r2 = r7.fileName     // Catch:{ Exception -> 0x04ee }
            r0.append(r2)     // Catch:{ Exception -> 0x04ee }
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x04ee }
            org.telegram.messenger.FileLog.e((java.lang.String) r0)     // Catch:{ Exception -> 0x04ee }
            goto L_0x041c
        L_0x041b:
            r11 = r5
        L_0x041c:
            r2 = 0
            r7.onFail(r2, r2)     // Catch:{ Exception -> 0x04ee }
            java.io.File r0 = r7.cacheFileTemp     // Catch:{ Exception -> 0x04ee }
            r0.delete()     // Catch:{ Exception -> 0x04ee }
            return r2
        L_0x0426:
            r11 = r5
            android.util.SparseArray<org.telegram.tgnet.TLRPC$TL_fileHash> r0 = r7.cdnHashes     // Catch:{ Exception -> 0x04ee }
            r0.remove(r14)     // Catch:{ Exception -> 0x04ee }
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$Range> r0 = r7.notCheckedCdnRanges     // Catch:{ Exception -> 0x04ee }
            int r2 = r1 + 1
            r3 = 0
            r7.addPart(r0, r1, r2, r3)     // Catch:{ Exception -> 0x04ee }
            goto L_0x0447
        L_0x0435:
            r17 = r9
            r26 = r10
            r27 = r11
            goto L_0x0447
        L_0x043c:
            r17 = r9
            r26 = r10
            r27 = r11
            goto L_0x0447
        L_0x0443:
            r17 = r9
            r26 = r10
        L_0x0447:
            java.io.RandomAccessFile r0 = r7.fiv     // Catch:{ Exception -> 0x04ee }
            if (r0 == 0) goto L_0x0457
            r1 = 0
            r0.seek(r1)     // Catch:{ Exception -> 0x04ee }
            java.io.RandomAccessFile r0 = r7.fiv     // Catch:{ Exception -> 0x04ee }
            byte[] r1 = r7.iv     // Catch:{ Exception -> 0x04ee }
            r0.write(r1)     // Catch:{ Exception -> 0x04ee }
        L_0x0457:
            int r0 = r7.totalBytesCount     // Catch:{ Exception -> 0x04ee }
            if (r0 <= 0) goto L_0x0470
            int r0 = r7.state     // Catch:{ Exception -> 0x04ee }
            r1 = 1
            if (r0 != r1) goto L_0x0470
            r28.copyNotLoadedRanges()     // Catch:{ Exception -> 0x04ee }
            org.telegram.messenger.FileLoadOperation$FileLoadOperationDelegate r1 = r7.delegate     // Catch:{ Exception -> 0x04ee }
            int r0 = r7.downloadedBytes     // Catch:{ Exception -> 0x04ee }
            long r3 = (long) r0     // Catch:{ Exception -> 0x04ee }
            int r0 = r7.totalBytesCount     // Catch:{ Exception -> 0x04ee }
            long r5 = (long) r0     // Catch:{ Exception -> 0x04ee }
            r2 = r28
            r1.didChangedLoadProgress(r2, r3, r5)     // Catch:{ Exception -> 0x04ee }
        L_0x0470:
            r1 = r17
        L_0x0472:
            r0 = 0
        L_0x0473:
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$RequestInfo> r2 = r7.delayedRequestInfos     // Catch:{ Exception -> 0x04ee }
            int r2 = r2.size()     // Catch:{ Exception -> 0x04ee }
            if (r0 >= r2) goto L_0x04dd
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$RequestInfo> r2 = r7.delayedRequestInfos     // Catch:{ Exception -> 0x04ee }
            java.lang.Object r2 = r2.get(r0)     // Catch:{ Exception -> 0x04ee }
            org.telegram.messenger.FileLoadOperation$RequestInfo r2 = (org.telegram.messenger.FileLoadOperation.RequestInfo) r2     // Catch:{ Exception -> 0x04ee }
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$Range> r3 = r7.notLoadedBytesRanges     // Catch:{ Exception -> 0x04ee }
            if (r3 != 0) goto L_0x0493
            int r3 = r7.downloadedBytes     // Catch:{ Exception -> 0x04ee }
            int r4 = r2.offset     // Catch:{ Exception -> 0x04ee }
            if (r3 != r4) goto L_0x0490
            goto L_0x0493
        L_0x0490:
            int r0 = r0 + 1
            goto L_0x0473
        L_0x0493:
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$RequestInfo> r3 = r7.delayedRequestInfos     // Catch:{ Exception -> 0x04ee }
            r3.remove(r0)     // Catch:{ Exception -> 0x04ee }
            r3 = 0
            boolean r3 = r7.processRequestResult(r2, r3)     // Catch:{ Exception -> 0x04ee }
            if (r3 != 0) goto L_0x04dd
            org.telegram.tgnet.TLRPC$TL_upload_file r3 = r2.response     // Catch:{ Exception -> 0x04ee }
            if (r3 == 0) goto L_0x04b4
            org.telegram.tgnet.TLRPC$TL_upload_file r3 = r2.response     // Catch:{ Exception -> 0x04ee }
            r4 = 0
            r3.disableFree = r4     // Catch:{ Exception -> 0x04ee }
            org.telegram.tgnet.TLRPC$TL_upload_file r3 = r2.response     // Catch:{ Exception -> 0x04ee }
            r3.freeResources()     // Catch:{ Exception -> 0x04ee }
            goto L_0x04dd
        L_0x04b4:
            org.telegram.tgnet.TLRPC$TL_upload_webFile r3 = r2.responseWeb     // Catch:{ Exception -> 0x04ee }
            if (r3 == 0) goto L_0x04c9
            org.telegram.tgnet.TLRPC$TL_upload_webFile r3 = r2.responseWeb     // Catch:{ Exception -> 0x04ee }
            r4 = 0
            r3.disableFree = r4     // Catch:{ Exception -> 0x04ee }
            org.telegram.tgnet.TLRPC$TL_upload_webFile r3 = r2.responseWeb     // Catch:{ Exception -> 0x04ee }
            r3.freeResources()     // Catch:{ Exception -> 0x04ee }
            goto L_0x04dd
        L_0x04c9:
            org.telegram.tgnet.TLRPC$TL_upload_cdnFile r3 = r2.responseCdn     // Catch:{ Exception -> 0x04ee }
            if (r3 == 0) goto L_0x04dd
            org.telegram.tgnet.TLRPC$TL_upload_cdnFile r3 = r2.responseCdn     // Catch:{ Exception -> 0x04ee }
            r4 = 0
            r3.disableFree = r4     // Catch:{ Exception -> 0x04ee }
            org.telegram.tgnet.TLRPC$TL_upload_cdnFile r3 = r2.responseCdn     // Catch:{ Exception -> 0x04ee }
            r3.freeResources()     // Catch:{ Exception -> 0x04ee }
        L_0x04dd:
            if (r1 == 0) goto L_0x04e4
            r0 = 1
            r7.onFinishLoadingFile(r0)     // Catch:{ Exception -> 0x04ee }
            goto L_0x04f6
        L_0x04e4:
            r28.startDownloadRequest()     // Catch:{ Exception -> 0x04ee }
            goto L_0x04f6
        L_0x04e8:
            r0 = 1
            r7.onFinishLoadingFile(r0)     // Catch:{ Exception -> 0x04ee }
            r1 = 0
            return r1
        L_0x04ee:
            r0 = move-exception
            r1 = 0
            r7.onFail(r1, r1)
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x04f6:
            r1 = 0
            goto L_0x05f2
        L_0x04f9:
            java.lang.String r1 = r8.text
            java.lang.String r5 = "FILE_MIGRATE_"
            boolean r1 = r1.contains(r5)
            if (r1 == 0) goto L_0x0538
            java.lang.String r0 = r8.text
            java.lang.String r1 = ""
            java.lang.String r2 = r0.replace(r5, r1)
            java.util.Scanner r0 = new java.util.Scanner
            r0.<init>(r2)
            r3 = r0
            r3.useDelimiter(r1)
            int r0 = r3.nextInt()     // Catch:{ Exception -> 0x051d }
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)     // Catch:{ Exception -> 0x051d }
            goto L_0x0520
        L_0x051d:
            r0 = move-exception
            r1 = 0
            r0 = r1
        L_0x0520:
            if (r0 != 0) goto L_0x0527
            r1 = 0
            r7.onFail(r1, r1)
            goto L_0x0535
        L_0x0527:
            r1 = 0
            int r4 = r0.intValue()
            r7.datacenterId = r4
            r7.downloadedBytes = r1
            r7.requestedBytesCount = r1
            r28.startDownloadRequest()
        L_0x0535:
            r1 = 0
            goto L_0x05f2
        L_0x0538:
            java.lang.String r1 = r8.text
            java.lang.String r5 = "OFFSET_INVALID"
            boolean r1 = r1.contains(r5)
            if (r1 == 0) goto L_0x0562
            int r0 = r7.downloadedBytes
            int r1 = r7.currentDownloadChunkSize
            int r0 = r0 % r1
            if (r0 != 0) goto L_0x055c
            r0 = 1
            r7.onFinishLoadingFile(r0)     // Catch:{ Exception -> 0x0550 }
            r1 = 0
            goto L_0x05f2
        L_0x0550:
            r0 = move-exception
            r1 = r0
            r0 = r1
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            r1 = 0
            r7.onFail(r1, r1)
            goto L_0x05f2
        L_0x055c:
            r1 = 0
            r7.onFail(r1, r1)
            goto L_0x05f2
        L_0x0562:
            r1 = 0
            java.lang.String r5 = r8.text
            java.lang.String r9 = "RETRY_LIMIT"
            boolean r5 = r5.contains(r9)
            if (r5 == 0) goto L_0x0573
            r0 = 2
            r7.onFail(r1, r0)
            goto L_0x05f2
        L_0x0573:
            boolean r1 = org.telegram.messenger.BuildVars.LOGS_ENABLED
            if (r1 == 0) goto L_0x05ee
            org.telegram.tgnet.TLRPC$InputFileLocation r1 = r7.location
            java.lang.String r5 = " "
            if (r1 == 0) goto L_0x05c9
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r9 = r8.text
            r1.append(r9)
            r1.append(r5)
            org.telegram.tgnet.TLRPC$InputFileLocation r5 = r7.location
            r1.append(r5)
            r1.append(r6)
            org.telegram.tgnet.TLRPC$InputFileLocation r5 = r7.location
            long r5 = r5.id
            r1.append(r5)
            r1.append(r4)
            org.telegram.tgnet.TLRPC$InputFileLocation r4 = r7.location
            int r4 = r4.local_id
            r1.append(r4)
            r1.append(r3)
            org.telegram.tgnet.TLRPC$InputFileLocation r3 = r7.location
            long r3 = r3.access_hash
            r1.append(r3)
            r1.append(r2)
            org.telegram.tgnet.TLRPC$InputFileLocation r2 = r7.location
            long r2 = r2.volume_id
            r1.append(r2)
            r1.append(r0)
            org.telegram.tgnet.TLRPC$InputFileLocation r0 = r7.location
            long r2 = r0.secret
            r1.append(r2)
            java.lang.String r0 = r1.toString()
            org.telegram.messenger.FileLog.e((java.lang.String) r0)
            goto L_0x05ee
        L_0x05c9:
            org.telegram.tgnet.TLRPC$InputWebFileLocation r0 = r7.webLocation
            if (r0 == 0) goto L_0x05ee
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = r8.text
            r0.append(r1)
            r0.append(r5)
            org.telegram.tgnet.TLRPC$InputWebFileLocation r1 = r7.webLocation
            r0.append(r1)
            r0.append(r6)
            java.lang.String r1 = r7.fileName
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            org.telegram.messenger.FileLog.e((java.lang.String) r0)
        L_0x05ee:
            r1 = 0
            r7.onFail(r1, r1)
        L_0x05f2:
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
    public /* synthetic */ void m542lambda$onFail$10$orgtelegrammessengerFileLoadOperation(int reason) {
        this.delegate.didFailedLoadingFile(this, reason);
    }

    private void clearOperaion(RequestInfo currentInfo, boolean preloadChanged) {
        int minOffset = Integer.MAX_VALUE;
        for (int a = 0; a < this.requestInfos.size(); a++) {
            RequestInfo info = this.requestInfos.get(a);
            minOffset = Math.min(info.offset, minOffset);
            if (this.isPreloadVideoOperation) {
                this.requestedPreloadedBytesRanges.delete(info.offset);
            } else {
                removePart(this.notRequestedBytesRanges, info.offset, info.offset + this.currentDownloadChunkSize);
            }
            if (!(currentInfo == info || info.requestToken == 0)) {
                ConnectionsManager.getInstance(this.currentAccount).cancelRequest(info.requestToken, true);
            }
        }
        this.requestInfos.clear();
        for (int a2 = 0; a2 < this.delayedRequestInfos.size(); a2++) {
            RequestInfo info2 = this.delayedRequestInfos.get(a2);
            if (this.isPreloadVideoOperation) {
                this.requestedPreloadedBytesRanges.delete(info2.offset);
            } else {
                removePart(this.notRequestedBytesRanges, info2.offset, info2.offset + this.currentDownloadChunkSize);
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
            this.requestedBytesCount = this.totalPreloadedBytes;
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

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v0, resolved type: org.telegram.tgnet.TLRPC$TL_upload_getFile} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v3, resolved type: org.telegram.tgnet.TLRPC$TL_upload_getFile} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v6, resolved type: org.telegram.tgnet.TLRPC$TL_upload_getWebFile} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v6, resolved type: org.telegram.tgnet.TLRPC$TL_upload_getFile} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v8, resolved type: org.telegram.tgnet.TLRPC$TL_upload_getCdnFile} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v9, resolved type: org.telegram.tgnet.TLRPC$TL_upload_getFile} */
    /* access modifiers changed from: protected */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void startDownloadRequest() {
        /*
            r19 = this;
            r1 = r19
            boolean r0 = r1.paused
            if (r0 != 0) goto L_0x02b2
            boolean r0 = r1.reuploadingCdn
            if (r0 != 0) goto L_0x02b2
            int r0 = r1.state
            r2 = 1
            if (r0 != r2) goto L_0x02b2
            int r0 = r1.streamPriorityStartOffset
            r3 = 2097152(0x200000, float:2.938736E-39)
            if (r0 != 0) goto L_0x0040
            boolean r0 = r1.nextPartWasPreloaded
            if (r0 != 0) goto L_0x002a
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$RequestInfo> r0 = r1.requestInfos
            int r0 = r0.size()
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$RequestInfo> r4 = r1.delayedRequestInfos
            int r4 = r4.size()
            int r0 = r0 + r4
            int r4 = r1.currentMaxDownloadRequests
            if (r0 >= r4) goto L_0x02b2
        L_0x002a:
            boolean r0 = r1.isPreloadVideoOperation
            if (r0 == 0) goto L_0x0040
            int r0 = r1.requestedBytesCount
            if (r0 > r3) goto L_0x02b2
            int r0 = r1.moovFound
            if (r0 == 0) goto L_0x0040
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$RequestInfo> r0 = r1.requestInfos
            int r0 = r0.size()
            if (r0 <= 0) goto L_0x0040
            goto L_0x02b2
        L_0x0040:
            r0 = 1
            int r4 = r1.streamPriorityStartOffset
            r5 = 0
            if (r4 != 0) goto L_0x0065
            boolean r4 = r1.nextPartWasPreloaded
            if (r4 != 0) goto L_0x0065
            boolean r4 = r1.isPreloadVideoOperation
            if (r4 == 0) goto L_0x0052
            int r4 = r1.moovFound
            if (r4 == 0) goto L_0x0065
        L_0x0052:
            int r4 = r1.totalBytesCount
            if (r4 <= 0) goto L_0x0065
            int r4 = r1.currentMaxDownloadRequests
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$RequestInfo> r6 = r1.requestInfos
            int r6 = r6.size()
            int r4 = r4 - r6
            int r0 = java.lang.Math.max(r5, r4)
            r4 = r0
            goto L_0x0066
        L_0x0065:
            r4 = r0
        L_0x0066:
            r0 = 0
            r6 = r0
        L_0x0068:
            if (r6 >= r4) goto L_0x02b1
            boolean r0 = r1.isPreloadVideoOperation
            r7 = 2
            if (r0 == 0) goto L_0x0100
            int r0 = r1.moovFound
            if (r0 == 0) goto L_0x0078
            int r0 = r1.preloadNotRequestedBytesCount
            if (r0 > 0) goto L_0x0078
            return
        L_0x0078:
            int r0 = r1.nextPreloadDownloadOffset
            r8 = -1
            if (r0 != r8) goto L_0x00b8
            r0 = 0
            r8 = 0
            int r9 = r1.currentDownloadChunkSize
            int r9 = r3 / r9
            int r9 = r9 + r7
        L_0x0084:
            if (r9 == 0) goto L_0x00aa
            android.util.SparseIntArray r10 = r1.requestedPreloadedBytesRanges
            int r10 = r10.get(r0, r5)
            if (r10 != 0) goto L_0x0090
            r8 = 1
            goto L_0x00aa
        L_0x0090:
            int r10 = r1.currentDownloadChunkSize
            int r0 = r0 + r10
            int r11 = r1.totalBytesCount
            if (r0 <= r11) goto L_0x0098
            goto L_0x00aa
        L_0x0098:
            int r12 = r1.moovFound
            if (r12 != r7) goto L_0x00a7
            int r12 = r10 * 8
            if (r0 != r12) goto L_0x00a7
            r12 = 1048576(0x100000, float:1.469368E-39)
            int r11 = r11 - r12
            int r11 = r11 / r10
            int r11 = r11 * r10
            r0 = r11
        L_0x00a7:
            int r9 = r9 + -1
            goto L_0x0084
        L_0x00aa:
            if (r8 != 0) goto L_0x00b7
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$RequestInfo> r10 = r1.requestInfos
            boolean r10 = r10.isEmpty()
            if (r10 == 0) goto L_0x00b7
            r1.onFinishLoadingFile(r5)
        L_0x00b7:
            goto L_0x00ba
        L_0x00b8:
            int r0 = r1.nextPreloadDownloadOffset
        L_0x00ba:
            android.util.SparseIntArray r8 = r1.requestedPreloadedBytesRanges
            if (r8 != 0) goto L_0x00c5
            android.util.SparseIntArray r8 = new android.util.SparseIntArray
            r8.<init>()
            r1.requestedPreloadedBytesRanges = r8
        L_0x00c5:
            android.util.SparseIntArray r8 = r1.requestedPreloadedBytesRanges
            r8.put(r0, r2)
            boolean r8 = org.telegram.messenger.BuildVars.DEBUG_VERSION
            if (r8 == 0) goto L_0x00f6
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r8.<init>()
            java.lang.String r9 = "start next preload from "
            r8.append(r9)
            r8.append(r0)
            java.lang.String r9 = " size "
            r8.append(r9)
            int r9 = r1.totalBytesCount
            r8.append(r9)
            java.lang.String r9 = " for "
            r8.append(r9)
            java.io.File r9 = r1.cacheFilePreload
            r8.append(r9)
            java.lang.String r8 = r8.toString()
            org.telegram.messenger.FileLog.d(r8)
        L_0x00f6:
            int r8 = r1.preloadNotRequestedBytesCount
            int r9 = r1.currentDownloadChunkSize
            int r8 = r8 - r9
            r1.preloadNotRequestedBytesCount = r8
            r8 = r0
            goto L_0x015f
        L_0x0100:
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$Range> r0 = r1.notRequestedBytesRanges
            if (r0 == 0) goto L_0x015c
            int r8 = r1.streamPriorityStartOffset
            if (r8 == 0) goto L_0x0109
            goto L_0x010b
        L_0x0109:
            int r8 = r1.streamStartOffset
        L_0x010b:
            int r0 = r0.size()
            r9 = 2147483647(0x7fffffff, float:NaN)
            r10 = 2147483647(0x7fffffff, float:NaN)
            r11 = 0
        L_0x0116:
            if (r11 >= r0) goto L_0x014e
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$Range> r12 = r1.notRequestedBytesRanges
            java.lang.Object r12 = r12.get(r11)
            org.telegram.messenger.FileLoadOperation$Range r12 = (org.telegram.messenger.FileLoadOperation.Range) r12
            if (r8 == 0) goto L_0x0143
            int r13 = r12.start
            if (r13 > r8) goto L_0x0133
            int r13 = r12.end
            if (r13 <= r8) goto L_0x0133
            r10 = r8
            r9 = 2147483647(0x7fffffff, float:NaN)
            goto L_0x014e
        L_0x0133:
            int r13 = r12.start
            if (r8 >= r13) goto L_0x0143
            int r13 = r12.start
            if (r13 >= r10) goto L_0x0143
            int r10 = r12.start
        L_0x0143:
            int r13 = r12.start
            int r9 = java.lang.Math.min(r9, r13)
            int r11 = r11 + 1
            goto L_0x0116
        L_0x014e:
            r11 = 2147483647(0x7fffffff, float:NaN)
            if (r10 == r11) goto L_0x0156
            r11 = r10
            r0 = r11
            goto L_0x015a
        L_0x0156:
            if (r9 == r11) goto L_0x02b1
            r11 = r9
            r0 = r11
        L_0x015a:
            r8 = r0
            goto L_0x015f
        L_0x015c:
            int r0 = r1.requestedBytesCount
            r8 = r0
        L_0x015f:
            boolean r0 = r1.isPreloadVideoOperation
            if (r0 != 0) goto L_0x016d
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$Range> r0 = r1.notRequestedBytesRanges
            if (r0 == 0) goto L_0x016d
            int r9 = r1.currentDownloadChunkSize
            int r9 = r9 + r8
            r1.addPart(r0, r8, r9, r5)
        L_0x016d:
            int r0 = r1.totalBytesCount
            if (r0 <= 0) goto L_0x0175
            if (r8 < r0) goto L_0x0175
            goto L_0x02b1
        L_0x0175:
            if (r0 <= 0) goto L_0x0186
            int r9 = r4 + -1
            if (r6 == r9) goto L_0x0186
            if (r0 <= 0) goto L_0x0183
            int r9 = r1.currentDownloadChunkSize
            int r9 = r9 + r8
            if (r9 < r0) goto L_0x0183
            goto L_0x0186
        L_0x0183:
            r18 = 0
            goto L_0x0188
        L_0x0186:
            r18 = 1
        L_0x0188:
            int r0 = r1.requestsCount
            int r0 = r0 % r7
            if (r0 != 0) goto L_0x0190
            r17 = 2
            goto L_0x0196
        L_0x0190:
            r7 = 65538(0x10002, float:9.1838E-41)
            r17 = 65538(0x10002, float:9.1838E-41)
        L_0x0196:
            boolean r0 = r1.isForceRequest
            if (r0 == 0) goto L_0x019d
            r0 = 32
            goto L_0x019e
        L_0x019d:
            r0 = 0
        L_0x019e:
            boolean r7 = r1.isCdn
            if (r7 == 0) goto L_0x01b6
            org.telegram.tgnet.TLRPC$TL_upload_getCdnFile r7 = new org.telegram.tgnet.TLRPC$TL_upload_getCdnFile
            r7.<init>()
            byte[] r9 = r1.cdnToken
            r7.file_token = r9
            r7.offset = r8
            int r9 = r1.currentDownloadChunkSize
            r7.limit = r9
            r9 = r7
            r0 = r0 | 1
            r7 = r0
            goto L_0x01df
        L_0x01b6:
            org.telegram.tgnet.TLRPC$InputWebFileLocation r7 = r1.webLocation
            if (r7 == 0) goto L_0x01cc
            org.telegram.tgnet.TLRPC$TL_upload_getWebFile r7 = new org.telegram.tgnet.TLRPC$TL_upload_getWebFile
            r7.<init>()
            org.telegram.tgnet.TLRPC$InputWebFileLocation r9 = r1.webLocation
            r7.location = r9
            r7.offset = r8
            int r9 = r1.currentDownloadChunkSize
            r7.limit = r9
            r9 = r7
            r7 = r0
            goto L_0x01df
        L_0x01cc:
            org.telegram.tgnet.TLRPC$TL_upload_getFile r7 = new org.telegram.tgnet.TLRPC$TL_upload_getFile
            r7.<init>()
            org.telegram.tgnet.TLRPC$InputFileLocation r9 = r1.location
            r7.location = r9
            r7.offset = r8
            int r9 = r1.currentDownloadChunkSize
            r7.limit = r9
            r7.cdn_supported = r2
            r9 = r7
            r7 = r0
        L_0x01df:
            int r0 = r1.requestedBytesCount
            int r10 = r1.currentDownloadChunkSize
            int r0 = r0 + r10
            r1.requestedBytesCount = r0
            org.telegram.messenger.FileLoadOperation$RequestInfo r0 = new org.telegram.messenger.FileLoadOperation$RequestInfo
            r0.<init>()
            r15 = r0
            java.util.ArrayList<org.telegram.messenger.FileLoadOperation$RequestInfo> r0 = r1.requestInfos
            r0.add(r15)
            int unused = r15.offset = r8
            boolean r0 = r1.isPreloadVideoOperation
            if (r0 != 0) goto L_0x024e
            boolean r0 = r1.supportsPreloading
            if (r0 == 0) goto L_0x024e
            java.io.RandomAccessFile r0 = r1.preloadStream
            if (r0 == 0) goto L_0x024e
            android.util.SparseArray<org.telegram.messenger.FileLoadOperation$PreloadRange> r0 = r1.preloadedBytesRanges
            if (r0 == 0) goto L_0x024e
            int r10 = r15.offset
            java.lang.Object r0 = r0.get(r10)
            r10 = r0
            org.telegram.messenger.FileLoadOperation$PreloadRange r10 = (org.telegram.messenger.FileLoadOperation.PreloadRange) r10
            if (r10 == 0) goto L_0x024e
            org.telegram.tgnet.TLRPC$TL_upload_file r0 = new org.telegram.tgnet.TLRPC$TL_upload_file
            r0.<init>()
            org.telegram.tgnet.TLRPC.TL_upload_file unused = r15.response = r0
            org.telegram.tgnet.NativeByteBuffer r0 = new org.telegram.tgnet.NativeByteBuffer     // Catch:{ Exception -> 0x024d }
            int r11 = r10.length     // Catch:{ Exception -> 0x024d }
            r0.<init>((int) r11)     // Catch:{ Exception -> 0x024d }
            java.io.RandomAccessFile r11 = r1.preloadStream     // Catch:{ Exception -> 0x024d }
            int r12 = r10.fileOffset     // Catch:{ Exception -> 0x024d }
            long r12 = (long) r12     // Catch:{ Exception -> 0x024d }
            r11.seek(r12)     // Catch:{ Exception -> 0x024d }
            java.io.RandomAccessFile r11 = r1.preloadStream     // Catch:{ Exception -> 0x024d }
            java.nio.channels.FileChannel r11 = r11.getChannel()     // Catch:{ Exception -> 0x024d }
            java.nio.ByteBuffer r12 = r0.buffer     // Catch:{ Exception -> 0x024d }
            r11.read(r12)     // Catch:{ Exception -> 0x024d }
            java.nio.ByteBuffer r11 = r0.buffer     // Catch:{ Exception -> 0x024d }
            r11.position(r5)     // Catch:{ Exception -> 0x024d }
            org.telegram.tgnet.TLRPC$TL_upload_file r11 = r15.response     // Catch:{ Exception -> 0x024d }
            r11.bytes = r0     // Catch:{ Exception -> 0x024d }
            org.telegram.messenger.DispatchQueue r11 = org.telegram.messenger.Utilities.stageQueue     // Catch:{ Exception -> 0x024d }
            org.telegram.messenger.FileLoadOperation$$ExternalSyntheticLambda7 r12 = new org.telegram.messenger.FileLoadOperation$$ExternalSyntheticLambda7     // Catch:{ Exception -> 0x024d }
            r12.<init>(r1, r15)     // Catch:{ Exception -> 0x024d }
            r11.postRunnable(r12)     // Catch:{ Exception -> 0x024d }
            goto L_0x02ab
        L_0x024d:
            r0 = move-exception
        L_0x024e:
            int r0 = r1.streamPriorityStartOffset
            if (r0 == 0) goto L_0x0270
            boolean r0 = org.telegram.messenger.BuildVars.DEBUG_VERSION
            if (r0 == 0) goto L_0x026c
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r10 = "frame get offset = "
            r0.append(r10)
            int r10 = r1.streamPriorityStartOffset
            r0.append(r10)
            java.lang.String r0 = r0.toString()
            org.telegram.messenger.FileLog.d(r0)
        L_0x026c:
            r1.streamPriorityStartOffset = r5
            r1.priorityRequestInfo = r15
        L_0x0270:
            org.telegram.tgnet.TLRPC$InputFileLocation r0 = r1.location
            boolean r10 = r0 instanceof org.telegram.tgnet.TLRPC.TL_inputPeerPhotoFileLocation
            if (r10 == 0) goto L_0x0284
            org.telegram.tgnet.TLRPC$TL_inputPeerPhotoFileLocation r0 = (org.telegram.tgnet.TLRPC.TL_inputPeerPhotoFileLocation) r0
            long r10 = r0.photo_id
            r12 = 0
            int r14 = (r10 > r12 ? 1 : (r10 == r12 ? 0 : -1))
            if (r14 != 0) goto L_0x0284
            r1.requestReference(r15)
            goto L_0x02ab
        L_0x0284:
            int r0 = r1.currentAccount
            org.telegram.tgnet.ConnectionsManager r10 = org.telegram.tgnet.ConnectionsManager.getInstance(r0)
            org.telegram.messenger.FileLoadOperation$$ExternalSyntheticLambda5 r12 = new org.telegram.messenger.FileLoadOperation$$ExternalSyntheticLambda5
            r12.<init>(r1, r15, r9)
            r13 = 0
            r14 = 0
            boolean r0 = r1.isCdn
            if (r0 == 0) goto L_0x0298
            int r0 = r1.cdnDatacenterId
            goto L_0x029a
        L_0x0298:
            int r0 = r1.datacenterId
        L_0x029a:
            r16 = r0
            r11 = r9
            r3 = r15
            r15 = r7
            int r0 = r10.sendRequest(r11, r12, r13, r14, r15, r16, r17, r18)
            int unused = r3.requestToken = r0
            int r0 = r1.requestsCount
            int r0 = r0 + r2
            r1.requestsCount = r0
        L_0x02ab:
            int r6 = r6 + 1
            r3 = 2097152(0x200000, float:2.938736E-39)
            goto L_0x0068
        L_0x02b1:
            return
        L_0x02b2:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.FileLoadOperation.startDownloadRequest():void");
    }

    /* renamed from: lambda$startDownloadRequest$11$org-telegram-messenger-FileLoadOperation  reason: not valid java name */
    public /* synthetic */ void m549xe3dafa6a(RequestInfo requestInfo) {
        processRequestResult(requestInfo, (TLRPC.TL_error) null);
        requestInfo.response.freeResources();
    }

    /* renamed from: lambda$startDownloadRequest$13$org-telegram-messenger-FileLoadOperation  reason: not valid java name */
    public /* synthetic */ void m551xf2a564a8(RequestInfo requestInfo, TLObject request, TLObject response, TLRPC.TL_error error) {
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
                        this.cdnHashes = new SparseArray<>();
                    }
                    for (int a1 = 0; a1 < res.file_hashes.size(); a1++) {
                        TLRPC.TL_fileHash hash = (TLRPC.TL_fileHash) res.file_hashes.get(a1);
                        this.cdnHashes.put(hash.offset, hash);
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
                    arrayList.add(new Range(0, 16000));
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
                        this.totalBytesCount = requestInfo.responseWeb.size;
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
    public /* synthetic */ void m550xeb402var_(RequestInfo requestInfo, TLObject response1, TLRPC.TL_error error1) {
        this.reuploadingCdn = false;
        if (error1 == null) {
            TLRPC.Vector vector = (TLRPC.Vector) response1;
            if (!vector.objects.isEmpty()) {
                if (this.cdnHashes == null) {
                    this.cdnHashes = new SparseArray<>();
                }
                for (int a1 = 0; a1 < vector.objects.size(); a1++) {
                    TLRPC.TL_fileHash hash = (TLRPC.TL_fileHash) vector.objects.get(a1);
                    this.cdnHashes.put(hash.offset, hash);
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
