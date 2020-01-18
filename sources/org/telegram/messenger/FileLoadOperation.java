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
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.InputFileLocation;
import org.telegram.tgnet.TLRPC.InputWebFileLocation;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_fileHash;
import org.telegram.tgnet.TLRPC.TL_fileLocationToBeDeprecated;
import org.telegram.tgnet.TLRPC.TL_inputDocumentFileLocation;
import org.telegram.tgnet.TLRPC.TL_inputEncryptedFileLocation;
import org.telegram.tgnet.TLRPC.TL_inputFileLocation;
import org.telegram.tgnet.TLRPC.TL_inputPeerPhotoFileLocation;
import org.telegram.tgnet.TLRPC.TL_inputPhotoFileLocation;
import org.telegram.tgnet.TLRPC.TL_inputSecureFileLocation;
import org.telegram.tgnet.TLRPC.TL_inputStickerSetThumb;
import org.telegram.tgnet.TLRPC.TL_secureFile;
import org.telegram.tgnet.TLRPC.TL_theme;
import org.telegram.tgnet.TLRPC.TL_upload_cdnFile;
import org.telegram.tgnet.TLRPC.TL_upload_cdnFileReuploadNeeded;
import org.telegram.tgnet.TLRPC.TL_upload_file;
import org.telegram.tgnet.TLRPC.TL_upload_fileCdnRedirect;
import org.telegram.tgnet.TLRPC.TL_upload_getCdnFile;
import org.telegram.tgnet.TLRPC.TL_upload_getCdnFileHashes;
import org.telegram.tgnet.TLRPC.TL_upload_getFile;
import org.telegram.tgnet.TLRPC.TL_upload_getWebFile;
import org.telegram.tgnet.TLRPC.TL_upload_reuploadCdnFile;
import org.telegram.tgnet.TLRPC.TL_upload_webFile;
import org.telegram.tgnet.TLRPC.Vector;
import org.telegram.tgnet.TLRPC.WebPage;

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
    private SparseArray<TL_fileHash> cdnHashes;
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
    protected InputFileLocation location;
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
    private InputWebFileLocation webLocation;

    public interface FileLoadOperationDelegate {
        void didChangedLoadProgress(FileLoadOperation fileLoadOperation, long j, long j2);

        void didFailedLoadingFile(FileLoadOperation fileLoadOperation, int i);

        void didFinishLoadingFile(FileLoadOperation fileLoadOperation, File file);
    }

    private static class PreloadRange {
        private int fileOffset;
        private int length;

        private PreloadRange(int i, int i2) {
            this.fileOffset = i;
            this.length = i2;
        }
    }

    public static class Range {
        private int end;
        private int start;

        private Range(int i, int i2) {
            this.start = i;
            this.end = i2;
        }
    }

    protected static class RequestInfo {
        private int offset;
        private int requestToken;
        private TL_upload_file response;
        private TL_upload_cdnFile responseCdn;
        private TL_upload_webFile responseWeb;

        protected RequestInfo() {
        }
    }

    public FileLoadOperation(ImageLocation imageLocation, Object obj, String str, int i) {
        this.preloadTempBuffer = new byte[16];
        boolean z = false;
        this.state = 0;
        this.parentObject = obj;
        InputFileLocation inputFileLocation;
        TL_fileLocationToBeDeprecated tL_fileLocationToBeDeprecated;
        long j;
        if (imageLocation.isEncrypted()) {
            this.location = new TL_inputEncryptedFileLocation();
            inputFileLocation = this.location;
            tL_fileLocationToBeDeprecated = imageLocation.location;
            j = tL_fileLocationToBeDeprecated.volume_id;
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
            this.location = new TL_inputPeerPhotoFileLocation();
            inputFileLocation = this.location;
            tL_fileLocationToBeDeprecated = imageLocation.location;
            j = tL_fileLocationToBeDeprecated.volume_id;
            inputFileLocation.id = j;
            inputFileLocation.volume_id = j;
            inputFileLocation.local_id = tL_fileLocationToBeDeprecated.local_id;
            inputFileLocation.big = imageLocation.photoPeerBig;
            inputFileLocation.peer = imageLocation.photoPeer;
        } else if (imageLocation.stickerSet != null) {
            this.location = new TL_inputStickerSetThumb();
            inputFileLocation = this.location;
            tL_fileLocationToBeDeprecated = imageLocation.location;
            j = tL_fileLocationToBeDeprecated.volume_id;
            inputFileLocation.id = j;
            inputFileLocation.volume_id = j;
            inputFileLocation.local_id = tL_fileLocationToBeDeprecated.local_id;
            inputFileLocation.stickerset = imageLocation.stickerSet;
        } else if (imageLocation.thumbSize != null) {
            if (imageLocation.photoId != 0) {
                this.location = new TL_inputPhotoFileLocation();
                inputFileLocation = this.location;
                inputFileLocation.id = imageLocation.photoId;
                tL_fileLocationToBeDeprecated = imageLocation.location;
                inputFileLocation.volume_id = tL_fileLocationToBeDeprecated.volume_id;
                inputFileLocation.local_id = tL_fileLocationToBeDeprecated.local_id;
                inputFileLocation.access_hash = imageLocation.access_hash;
                inputFileLocation.file_reference = imageLocation.file_reference;
                inputFileLocation.thumb_size = imageLocation.thumbSize;
            } else {
                this.location = new TL_inputDocumentFileLocation();
                inputFileLocation = this.location;
                inputFileLocation.id = imageLocation.documentId;
                tL_fileLocationToBeDeprecated = imageLocation.location;
                inputFileLocation.volume_id = tL_fileLocationToBeDeprecated.volume_id;
                inputFileLocation.local_id = tL_fileLocationToBeDeprecated.local_id;
                inputFileLocation.access_hash = imageLocation.access_hash;
                inputFileLocation.file_reference = imageLocation.file_reference;
                inputFileLocation.thumb_size = imageLocation.thumbSize;
            }
            inputFileLocation = this.location;
            if (inputFileLocation.file_reference == null) {
                inputFileLocation.file_reference = new byte[0];
            }
        } else {
            this.location = new TL_inputFileLocation();
            inputFileLocation = this.location;
            tL_fileLocationToBeDeprecated = imageLocation.location;
            inputFileLocation.volume_id = tL_fileLocationToBeDeprecated.volume_id;
            inputFileLocation.local_id = tL_fileLocationToBeDeprecated.local_id;
            inputFileLocation.secret = imageLocation.access_hash;
            inputFileLocation.file_reference = imageLocation.file_reference;
            if (inputFileLocation.file_reference == null) {
                inputFileLocation.file_reference = new byte[0];
            }
            this.allowDisordererFileSave = true;
        }
        int i2 = imageLocation.imageType;
        if (i2 == 1 || i2 == 3) {
            z = true;
        }
        this.ungzip = z;
        int i3 = imageLocation.dc_id;
        this.datacenterId = i3;
        this.initialDatacenterId = i3;
        this.currentType = 16777216;
        this.totalBytesCount = i;
        if (str == null) {
            str = "jpg";
        }
        this.ext = str;
    }

    public FileLoadOperation(SecureDocument secureDocument) {
        this.preloadTempBuffer = new byte[16];
        this.state = 0;
        this.location = new TL_inputSecureFileLocation();
        InputFileLocation inputFileLocation = this.location;
        TL_secureFile tL_secureFile = secureDocument.secureFile;
        inputFileLocation.id = tL_secureFile.id;
        inputFileLocation.access_hash = tL_secureFile.access_hash;
        this.datacenterId = tL_secureFile.dc_id;
        this.totalBytesCount = tL_secureFile.size;
        this.allowDisordererFileSave = true;
        this.currentType = 67108864;
        this.ext = ".jpg";
    }

    public FileLoadOperation(int i, WebFile webFile) {
        this.preloadTempBuffer = new byte[16];
        this.state = 0;
        this.currentAccount = i;
        this.webFile = webFile;
        this.webLocation = webFile.location;
        this.totalBytesCount = webFile.size;
        i = MessagesController.getInstance(this.currentAccount).webFileDatacenterId;
        this.datacenterId = i;
        this.initialDatacenterId = i;
        String mimeTypePart = FileLoader.getMimeTypePart(webFile.mime_type);
        if (webFile.mime_type.startsWith("image/")) {
            this.currentType = 16777216;
        } else if (webFile.mime_type.equals("audio/ogg")) {
            this.currentType = 50331648;
        } else if (webFile.mime_type.startsWith("video/")) {
            this.currentType = 33554432;
        } else {
            this.currentType = 67108864;
        }
        this.allowDisordererFileSave = true;
        this.ext = ImageLoader.getHttpUrlExtension(webFile.url, mimeTypePart);
    }

    /* JADX WARNING: Removed duplicated region for block: B:40:0x00f5 A:{Catch:{ Exception -> 0x0117 }} */
    /* JADX WARNING: Removed duplicated region for block: B:39:0x00f0 A:{Catch:{ Exception -> 0x0117 }} */
    /* JADX WARNING: Removed duplicated region for block: B:52:? A:{SYNTHETIC, RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:46:0x010e A:{Catch:{ Exception -> 0x0117 }} */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x00cf A:{Catch:{ Exception -> 0x0117 }} */
    /* JADX WARNING: Removed duplicated region for block: B:39:0x00f0 A:{Catch:{ Exception -> 0x0117 }} */
    /* JADX WARNING: Removed duplicated region for block: B:40:0x00f5 A:{Catch:{ Exception -> 0x0117 }} */
    /* JADX WARNING: Removed duplicated region for block: B:46:0x010e A:{Catch:{ Exception -> 0x0117 }} */
    /* JADX WARNING: Removed duplicated region for block: B:52:? A:{SYNTHETIC, RETURN} */
    public FileLoadOperation(org.telegram.tgnet.TLRPC.Document r7, java.lang.Object r8) {
        /*
        r6 = this;
        r6.<init>();
        r0 = 16;
        r1 = new byte[r0];
        r6.preloadTempBuffer = r1;
        r1 = 0;
        r6.state = r1;
        r2 = 1;
        r6.parentObject = r8;	 Catch:{ Exception -> 0x0117 }
        r8 = r7 instanceof org.telegram.tgnet.TLRPC.TL_documentEncrypted;	 Catch:{ Exception -> 0x0117 }
        r3 = "";
        if (r8 == 0) goto L_0x0043;
    L_0x0015:
        r8 = new org.telegram.tgnet.TLRPC$TL_inputEncryptedFileLocation;	 Catch:{ Exception -> 0x0117 }
        r8.<init>();	 Catch:{ Exception -> 0x0117 }
        r6.location = r8;	 Catch:{ Exception -> 0x0117 }
        r8 = r6.location;	 Catch:{ Exception -> 0x0117 }
        r4 = r7.id;	 Catch:{ Exception -> 0x0117 }
        r8.id = r4;	 Catch:{ Exception -> 0x0117 }
        r8 = r6.location;	 Catch:{ Exception -> 0x0117 }
        r4 = r7.access_hash;	 Catch:{ Exception -> 0x0117 }
        r8.access_hash = r4;	 Catch:{ Exception -> 0x0117 }
        r8 = r7.dc_id;	 Catch:{ Exception -> 0x0117 }
        r6.datacenterId = r8;	 Catch:{ Exception -> 0x0117 }
        r6.initialDatacenterId = r8;	 Catch:{ Exception -> 0x0117 }
        r8 = 32;
        r8 = new byte[r8];	 Catch:{ Exception -> 0x0117 }
        r6.iv = r8;	 Catch:{ Exception -> 0x0117 }
        r8 = r7.iv;	 Catch:{ Exception -> 0x0117 }
        r4 = r6.iv;	 Catch:{ Exception -> 0x0117 }
        r5 = r6.iv;	 Catch:{ Exception -> 0x0117 }
        r5 = r5.length;	 Catch:{ Exception -> 0x0117 }
        java.lang.System.arraycopy(r8, r1, r4, r1, r5);	 Catch:{ Exception -> 0x0117 }
        r8 = r7.key;	 Catch:{ Exception -> 0x0117 }
        r6.key = r8;	 Catch:{ Exception -> 0x0117 }
        goto L_0x0091;
    L_0x0043:
        r8 = r7 instanceof org.telegram.tgnet.TLRPC.TL_document;	 Catch:{ Exception -> 0x0117 }
        if (r8 == 0) goto L_0x0091;
    L_0x0047:
        r8 = new org.telegram.tgnet.TLRPC$TL_inputDocumentFileLocation;	 Catch:{ Exception -> 0x0117 }
        r8.<init>();	 Catch:{ Exception -> 0x0117 }
        r6.location = r8;	 Catch:{ Exception -> 0x0117 }
        r8 = r6.location;	 Catch:{ Exception -> 0x0117 }
        r4 = r7.id;	 Catch:{ Exception -> 0x0117 }
        r8.id = r4;	 Catch:{ Exception -> 0x0117 }
        r8 = r6.location;	 Catch:{ Exception -> 0x0117 }
        r4 = r7.access_hash;	 Catch:{ Exception -> 0x0117 }
        r8.access_hash = r4;	 Catch:{ Exception -> 0x0117 }
        r8 = r6.location;	 Catch:{ Exception -> 0x0117 }
        r4 = r7.file_reference;	 Catch:{ Exception -> 0x0117 }
        r8.file_reference = r4;	 Catch:{ Exception -> 0x0117 }
        r8 = r6.location;	 Catch:{ Exception -> 0x0117 }
        r8.thumb_size = r3;	 Catch:{ Exception -> 0x0117 }
        r8 = r6.location;	 Catch:{ Exception -> 0x0117 }
        r8 = r8.file_reference;	 Catch:{ Exception -> 0x0117 }
        if (r8 != 0) goto L_0x0070;
    L_0x006a:
        r8 = r6.location;	 Catch:{ Exception -> 0x0117 }
        r4 = new byte[r1];	 Catch:{ Exception -> 0x0117 }
        r8.file_reference = r4;	 Catch:{ Exception -> 0x0117 }
    L_0x0070:
        r8 = r7.dc_id;	 Catch:{ Exception -> 0x0117 }
        r6.datacenterId = r8;	 Catch:{ Exception -> 0x0117 }
        r6.initialDatacenterId = r8;	 Catch:{ Exception -> 0x0117 }
        r6.allowDisordererFileSave = r2;	 Catch:{ Exception -> 0x0117 }
        r8 = r7.attributes;	 Catch:{ Exception -> 0x0117 }
        r8 = r8.size();	 Catch:{ Exception -> 0x0117 }
        r4 = 0;
    L_0x007f:
        if (r4 >= r8) goto L_0x0091;
    L_0x0081:
        r5 = r7.attributes;	 Catch:{ Exception -> 0x0117 }
        r5 = r5.get(r4);	 Catch:{ Exception -> 0x0117 }
        r5 = r5 instanceof org.telegram.tgnet.TLRPC.TL_documentAttributeVideo;	 Catch:{ Exception -> 0x0117 }
        if (r5 == 0) goto L_0x008e;
    L_0x008b:
        r6.supportsPreloading = r2;	 Catch:{ Exception -> 0x0117 }
        goto L_0x0091;
    L_0x008e:
        r4 = r4 + 1;
        goto L_0x007f;
    L_0x0091:
        r8 = "application/x-tgsticker";
        r4 = r7.mime_type;	 Catch:{ Exception -> 0x0117 }
        r8 = r8.equals(r4);	 Catch:{ Exception -> 0x0117 }
        if (r8 != 0) goto L_0x00a8;
    L_0x009b:
        r8 = "application/x-tgwallpattern";
        r4 = r7.mime_type;	 Catch:{ Exception -> 0x0117 }
        r8 = r8.equals(r4);	 Catch:{ Exception -> 0x0117 }
        if (r8 == 0) goto L_0x00a6;
    L_0x00a5:
        goto L_0x00a8;
    L_0x00a6:
        r8 = 0;
        goto L_0x00a9;
    L_0x00a8:
        r8 = 1;
    L_0x00a9:
        r6.ungzip = r8;	 Catch:{ Exception -> 0x0117 }
        r8 = r7.size;	 Catch:{ Exception -> 0x0117 }
        r6.totalBytesCount = r8;	 Catch:{ Exception -> 0x0117 }
        r8 = r6.key;	 Catch:{ Exception -> 0x0117 }
        if (r8 == 0) goto L_0x00c5;
    L_0x00b3:
        r8 = r6.totalBytesCount;	 Catch:{ Exception -> 0x0117 }
        r8 = r8 % r0;
        if (r8 == 0) goto L_0x00c5;
    L_0x00b8:
        r8 = r6.totalBytesCount;	 Catch:{ Exception -> 0x0117 }
        r8 = r8 % r0;
        r0 = r0 - r8;
        r6.bytesCountPadding = r0;	 Catch:{ Exception -> 0x0117 }
        r8 = r6.totalBytesCount;	 Catch:{ Exception -> 0x0117 }
        r0 = r6.bytesCountPadding;	 Catch:{ Exception -> 0x0117 }
        r8 = r8 + r0;
        r6.totalBytesCount = r8;	 Catch:{ Exception -> 0x0117 }
    L_0x00c5:
        r8 = org.telegram.messenger.FileLoader.getDocumentFileName(r7);	 Catch:{ Exception -> 0x0117 }
        r6.ext = r8;	 Catch:{ Exception -> 0x0117 }
        r8 = r6.ext;	 Catch:{ Exception -> 0x0117 }
        if (r8 == 0) goto L_0x00e4;
    L_0x00cf:
        r8 = r6.ext;	 Catch:{ Exception -> 0x0117 }
        r0 = 46;
        r8 = r8.lastIndexOf(r0);	 Catch:{ Exception -> 0x0117 }
        r0 = -1;
        if (r8 != r0) goto L_0x00db;
    L_0x00da:
        goto L_0x00e4;
    L_0x00db:
        r0 = r6.ext;	 Catch:{ Exception -> 0x0117 }
        r8 = r0.substring(r8);	 Catch:{ Exception -> 0x0117 }
        r6.ext = r8;	 Catch:{ Exception -> 0x0117 }
        goto L_0x00e6;
    L_0x00e4:
        r6.ext = r3;	 Catch:{ Exception -> 0x0117 }
    L_0x00e6:
        r8 = "audio/ogg";
        r0 = r7.mime_type;	 Catch:{ Exception -> 0x0117 }
        r8 = r8.equals(r0);	 Catch:{ Exception -> 0x0117 }
        if (r8 == 0) goto L_0x00f5;
    L_0x00f0:
        r8 = 50331648; // 0x3000000 float:3.761582E-37 double:2.4867138E-316;
        r6.currentType = r8;	 Catch:{ Exception -> 0x0117 }
        goto L_0x0106;
    L_0x00f5:
        r8 = r7.mime_type;	 Catch:{ Exception -> 0x0117 }
        r8 = org.telegram.messenger.FileLoader.isVideoMimeType(r8);	 Catch:{ Exception -> 0x0117 }
        if (r8 == 0) goto L_0x0102;
    L_0x00fd:
        r8 = 33554432; // 0x2000000 float:9.403955E-38 double:1.6578092E-316;
        r6.currentType = r8;	 Catch:{ Exception -> 0x0117 }
        goto L_0x0106;
    L_0x0102:
        r8 = 67108864; // 0x4000000 float:1.5046328E-36 double:3.31561842E-316;
        r6.currentType = r8;	 Catch:{ Exception -> 0x0117 }
    L_0x0106:
        r8 = r6.ext;	 Catch:{ Exception -> 0x0117 }
        r8 = r8.length();	 Catch:{ Exception -> 0x0117 }
        if (r8 > r2) goto L_0x011e;
    L_0x010e:
        r7 = r7.mime_type;	 Catch:{ Exception -> 0x0117 }
        r7 = org.telegram.messenger.FileLoader.getExtensionByMimeType(r7);	 Catch:{ Exception -> 0x0117 }
        r6.ext = r7;	 Catch:{ Exception -> 0x0117 }
        goto L_0x011e;
    L_0x0117:
        r7 = move-exception;
        org.telegram.messenger.FileLog.e(r7);
        r6.onFail(r2, r1);
    L_0x011e:
        return;
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
        if (arrayList != null && i2 >= i) {
            Object obj;
            int size = arrayList.size();
            int i3 = 0;
            int i4 = 0;
            while (i4 < size) {
                Range range = (Range) arrayList.get(i4);
                if (i == range.end) {
                    range.end = i2;
                } else if (i2 == range.start) {
                    range.start = i;
                } else {
                    i4++;
                }
                obj = 1;
            }
            obj = null;
            Collections.sort(arrayList, -$$Lambda$FileLoadOperation$o7auaauybhlDahYpH2ZCMglGb9Q.INSTANCE);
            while (i3 < arrayList.size() - 1) {
                Range range2 = (Range) arrayList.get(i3);
                int i5 = i3 + 1;
                Range range3 = (Range) arrayList.get(i5);
                if (range2.end == range3.start) {
                    range2.end = range3.end;
                    arrayList.remove(i5);
                    i3--;
                }
                i3++;
            }
            if (obj == null) {
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
        if (arrayList != null && i2 >= i) {
            Object obj;
            int size = arrayList.size();
            int i3 = 0;
            int i4 = 0;
            while (true) {
                obj = 1;
                if (i4 >= size) {
                    obj = null;
                    break;
                }
                Range range = (Range) arrayList.get(i4);
                if (i <= range.start) {
                    if (i2 >= range.end) {
                        arrayList.remove(i4);
                        break;
                    } else if (i2 > range.start) {
                        range.start = i2;
                        break;
                    }
                } else if (i2 < range.end) {
                    arrayList.add(0, new Range(range.start, i));
                    range.start = i2;
                    break;
                } else if (i < range.end) {
                    range.end = i;
                    break;
                }
                i4++;
            }
            if (!z) {
                return;
            }
            if (obj != null) {
                try {
                    this.filePartsStream.seek(0);
                    i = arrayList.size();
                    this.filePartsStream.writeInt(i);
                    for (i2 = 0; i2 < i; i2++) {
                        Range range2 = (Range) arrayList.get(i2);
                        this.filePartsStream.writeInt(range2.start);
                        this.filePartsStream.writeInt(range2.end);
                    }
                } catch (Exception e) {
                    FileLog.e(e);
                }
                ArrayList arrayList2 = this.streamListeners;
                if (arrayList2 != null) {
                    int size2 = arrayList2.size();
                    while (i3 < size2) {
                        ((FileLoadOperationStream) this.streamListeners.get(i3)).newDataAvailable();
                        i3++;
                    }
                }
            } else if (BuildVars.LOGS_ENABLED) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(this.cacheFileFinal);
                stringBuilder.append(" downloaded duplicate file part ");
                stringBuilder.append(i);
                stringBuilder.append(" - ");
                stringBuilder.append(i2);
                FileLog.e(stringBuilder.toString());
            }
        }
    }

    /* Access modifiers changed, original: protected */
    public File getCurrentFile() {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        File[] fileArr = new File[1];
        Utilities.stageQueue.postRunnable(new -$$Lambda$FileLoadOperation$wamhg3zXe9K1fjSuz0X9oOjrnnI(this, fileArr, countDownLatch));
        try {
            countDownLatch.await();
        } catch (Exception e) {
            FileLog.e(e);
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
            i3 = this.downloadedBytes;
            if (i3 == 0) {
                return i2;
            }
            return Math.min(i2, Math.max(i3 - i, 0));
        }
        int size = arrayList.size();
        Range range = null;
        for (int i4 = 0; i4 < size; i4++) {
            Range range2 = (Range) arrayList.get(i4);
            if (i <= range2.start && (r3 == null || range2.start < r3.start)) {
                range = range2;
            }
            if (range2.start <= i && range2.end > i) {
                i3 = 0;
                break;
            }
        }
        i3 = i2;
        if (i3 == 0) {
            return 0;
        }
        if (range != null) {
            return Math.min(i2, range.start - i);
        }
        return Math.min(i2, Math.max(this.totalBytesCount - i, 0));
    }

    /* Access modifiers changed, original: protected */
    public float getDownloadedLengthFromOffset(float f) {
        ArrayList arrayList = this.notLoadedBytesRangesCopy;
        int i = this.totalBytesCount;
        return (i == 0 || arrayList == null) ? 0.0f : f + (((float) getDownloadedLengthFromOffsetInternal(arrayList, (int) (((float) i) * f), i)) / ((float) this.totalBytesCount));
    }

    /* Access modifiers changed, original: protected */
    public int getDownloadedLengthFromOffset(int i, int i2) {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        int[] iArr = new int[1];
        Utilities.stageQueue.postRunnable(new -$$Lambda$FileLoadOperation$8QL_G37hjhL6ox5f8VqjmOQNcPI(this, iArr, i, i2, countDownLatch));
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
        String str = ".";
        StringBuilder stringBuilder;
        if (this.location != null) {
            stringBuilder = new StringBuilder();
            stringBuilder.append(this.location.volume_id);
            stringBuilder.append("_");
            stringBuilder.append(this.location.local_id);
            stringBuilder.append(str);
            stringBuilder.append(this.ext);
            return stringBuilder.toString();
        }
        stringBuilder = new StringBuilder();
        stringBuilder.append(Utilities.MD5(this.webFile.url));
        stringBuilder.append(str);
        stringBuilder.append(this.ext);
        return stringBuilder.toString();
    }

    /* Access modifiers changed, original: protected */
    public void removeStreamListener(FileLoadOperationStream fileLoadOperationStream) {
        Utilities.stageQueue.postRunnable(new -$$Lambda$FileLoadOperation$gpU_9Y3L-u2SbrKXr1zQ9VNewHA(this, fileLoadOperationStream));
    }

    public /* synthetic */ void lambda$removeStreamListener$3$FileLoadOperation(FileLoadOperationStream fileLoadOperationStream) {
        ArrayList arrayList = this.streamListeners;
        if (arrayList != null) {
            arrayList.remove(fileLoadOperationStream);
        }
    }

    private void copyNotLoadedRanges() {
        ArrayList arrayList = this.notLoadedBytesRanges;
        if (arrayList != null) {
            this.notLoadedBytesRangesCopy = new ArrayList(arrayList);
        }
    }

    public void pause() {
        if (this.state == 1) {
            this.paused = true;
        }
    }

    public boolean start() {
        return start(null, 0, false);
    }

    /* JADX WARNING: Removed duplicated region for block: B:201:0x05b0  */
    /* JADX WARNING: Removed duplicated region for block: B:228:0x066b  */
    /* JADX WARNING: Removed duplicated region for block: B:215:0x0614  */
    /* JADX WARNING: Removed duplicated region for block: B:235:0x0692  */
    /* JADX WARNING: Removed duplicated region for block: B:241:0x06be  */
    /* JADX WARNING: Removed duplicated region for block: B:246:0x06fb  */
    /* JADX WARNING: Removed duplicated region for block: B:269:0x0766 A:{Catch:{ Exception -> 0x076f }} */
    /* JADX WARNING: Removed duplicated region for block: B:276:0x077b  */
    /* JADX WARNING: Removed duplicated region for block: B:274:0x0777  */
    /* JADX WARNING: Removed duplicated region for block: B:132:0x047e A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:201:0x05b0  */
    /* JADX WARNING: Removed duplicated region for block: B:215:0x0614  */
    /* JADX WARNING: Removed duplicated region for block: B:228:0x066b  */
    /* JADX WARNING: Removed duplicated region for block: B:235:0x0692  */
    /* JADX WARNING: Removed duplicated region for block: B:241:0x06be  */
    /* JADX WARNING: Removed duplicated region for block: B:246:0x06fb  */
    /* JADX WARNING: Removed duplicated region for block: B:269:0x0766 A:{Catch:{ Exception -> 0x076f }} */
    /* JADX WARNING: Removed duplicated region for block: B:274:0x0777  */
    /* JADX WARNING: Removed duplicated region for block: B:276:0x077b  */
    /* JADX WARNING: Removed duplicated region for block: B:191:0x058e A:{SYNTHETIC, Splitter:B:191:0x058e} */
    /* JADX WARNING: Removed duplicated region for block: B:201:0x05b0  */
    /* JADX WARNING: Removed duplicated region for block: B:228:0x066b  */
    /* JADX WARNING: Removed duplicated region for block: B:215:0x0614  */
    /* JADX WARNING: Removed duplicated region for block: B:235:0x0692  */
    /* JADX WARNING: Removed duplicated region for block: B:241:0x06be  */
    /* JADX WARNING: Removed duplicated region for block: B:246:0x06fb  */
    /* JADX WARNING: Removed duplicated region for block: B:269:0x0766 A:{Catch:{ Exception -> 0x076f }} */
    /* JADX WARNING: Removed duplicated region for block: B:276:0x077b  */
    /* JADX WARNING: Removed duplicated region for block: B:274:0x0777  */
    /* JADX WARNING: Removed duplicated region for block: B:191:0x058e A:{SYNTHETIC, Splitter:B:191:0x058e} */
    /* JADX WARNING: Removed duplicated region for block: B:201:0x05b0  */
    /* JADX WARNING: Removed duplicated region for block: B:215:0x0614  */
    /* JADX WARNING: Removed duplicated region for block: B:228:0x066b  */
    /* JADX WARNING: Removed duplicated region for block: B:235:0x0692  */
    /* JADX WARNING: Removed duplicated region for block: B:241:0x06be  */
    /* JADX WARNING: Removed duplicated region for block: B:246:0x06fb  */
    /* JADX WARNING: Removed duplicated region for block: B:269:0x0766 A:{Catch:{ Exception -> 0x076f }} */
    /* JADX WARNING: Removed duplicated region for block: B:274:0x0777  */
    /* JADX WARNING: Removed duplicated region for block: B:276:0x077b  */
    /* JADX WARNING: Missing block: B:102:0x03c3, code skipped:
            if (((long) r5) != r7.cacheFileFinal.length()) goto L_0x03c5;
     */
    public boolean start(org.telegram.messenger.FileLoadOperationStream r23, int r24, boolean r25) {
        /*
        r22 = this;
        r7 = r22;
        r0 = r7.currentDownloadChunkSize;
        if (r0 != 0) goto L_0x001b;
    L_0x0006:
        r0 = r7.totalBytesCount;
        r1 = 1048576; // 0x100000 float:1.469368E-39 double:5.180654E-318;
        if (r0 < r1) goto L_0x000f;
    L_0x000c:
        r0 = 131072; // 0x20000 float:1.83671E-40 double:6.47582E-319;
        goto L_0x0012;
    L_0x000f:
        r0 = 32768; // 0x8000 float:4.5918E-41 double:1.61895E-319;
    L_0x0012:
        r7.currentDownloadChunkSize = r0;
        r0 = r7.totalBytesCount;
        r1 = 1048576; // 0x100000 float:1.469368E-39 double:5.180654E-318;
        r0 = 4;
        r7.currentMaxDownloadRequests = r0;
    L_0x001b:
        r0 = r7.state;
        r8 = 1;
        r9 = 0;
        if (r0 == 0) goto L_0x0023;
    L_0x0021:
        r0 = 1;
        goto L_0x0024;
    L_0x0023:
        r0 = 0;
    L_0x0024:
        r10 = r7.paused;
        r7.paused = r9;
        if (r23 == 0) goto L_0x003f;
    L_0x002a:
        r11 = org.telegram.messenger.Utilities.stageQueue;
        r12 = new org.telegram.messenger.-$$Lambda$FileLoadOperation$ueOLhTDJvcCHBhZ6A3pTvCfLNBQ;
        r1 = r12;
        r2 = r22;
        r3 = r25;
        r4 = r24;
        r5 = r23;
        r6 = r0;
        r1.<init>(r2, r3, r4, r5, r6);
        r11.postRunnable(r12);
        goto L_0x004d;
    L_0x003f:
        if (r10 == 0) goto L_0x004d;
    L_0x0041:
        if (r0 == 0) goto L_0x004d;
    L_0x0043:
        r1 = org.telegram.messenger.Utilities.stageQueue;
        r2 = new org.telegram.messenger.-$$Lambda$HfxdqGkDOxPIykHiS0VM1dK9UjM;
        r2.<init>(r7);
        r1.postRunnable(r2);
    L_0x004d:
        if (r0 == 0) goto L_0x0050;
    L_0x004f:
        return r10;
    L_0x0050:
        r0 = r7.location;
        if (r0 != 0) goto L_0x005c;
    L_0x0054:
        r0 = r7.webLocation;
        if (r0 != 0) goto L_0x005c;
    L_0x0058:
        r7.onFail(r8, r9);
        return r9;
    L_0x005c:
        r0 = r7.currentDownloadChunkSize;
        r1 = r24 / r0;
        r1 = r1 * r0;
        r7.streamStartOffset = r1;
        r1 = r7.allowDisordererFileSave;
        if (r1 == 0) goto L_0x007c;
    L_0x0068:
        r1 = r7.totalBytesCount;
        if (r1 <= 0) goto L_0x007c;
    L_0x006c:
        if (r1 <= r0) goto L_0x007c;
    L_0x006e:
        r0 = new java.util.ArrayList;
        r0.<init>();
        r7.notLoadedBytesRanges = r0;
        r0 = new java.util.ArrayList;
        r0.<init>();
        r7.notRequestedBytesRanges = r0;
    L_0x007c:
        r0 = r7.webLocation;
        r1 = ".iv.enc";
        r2 = ".iv";
        r3 = ".enc";
        r4 = ".temp.enc";
        r5 = ".temp";
        r6 = ".";
        r10 = 0;
        if (r0 == 0) goto L_0x0118;
    L_0x008e:
        r0 = r7.webFile;
        r0 = r0.url;
        r0 = org.telegram.messenger.Utilities.MD5(r0);
        r13 = r7.encryptFile;
        if (r13 == 0) goto L_0x00d7;
    L_0x009a:
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r2.append(r0);
        r2.append(r4);
        r2 = r2.toString();
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r4.append(r0);
        r4.append(r6);
        r5 = r7.ext;
        r4.append(r5);
        r4.append(r3);
        r3 = r4.toString();
        r4 = r7.key;
        if (r4 == 0) goto L_0x00d5;
    L_0x00c4:
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r4.append(r0);
        r4.append(r1);
        r0 = r4.toString();
        r1 = r2;
        goto L_0x010f;
    L_0x00d5:
        r1 = r2;
        goto L_0x010e;
    L_0x00d7:
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r1.append(r0);
        r1.append(r5);
        r1 = r1.toString();
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r3.append(r0);
        r3.append(r6);
        r4 = r7.ext;
        r3.append(r4);
        r3 = r3.toString();
        r4 = r7.key;
        if (r4 == 0) goto L_0x010e;
    L_0x00fe:
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r4.append(r0);
        r4.append(r2);
        r0 = r4.toString();
        goto L_0x010f;
    L_0x010e:
        r0 = 0;
    L_0x010f:
        r2 = 0;
        r12 = 0;
        r21 = r1;
        r1 = r0;
        r0 = r21;
        goto L_0x035e;
    L_0x0118:
        r0 = r7.location;
        r13 = r0.volume_id;
        r15 = "_";
        r16 = (r13 > r10 ? 1 : (r13 == r10 ? 0 : -1));
        if (r16 == 0) goto L_0x024c;
    L_0x0122:
        r0 = r0.local_id;
        if (r0 == 0) goto L_0x024c;
    L_0x0126:
        r0 = r7.datacenterId;
        r12 = -NUM; // 0xfffffffvar_ float:-0.0 double:NaN;
        if (r0 == r12) goto L_0x0248;
    L_0x012c:
        r16 = -NUM; // 0xfffffffvar_ float:-0.0 double:NaN;
        r12 = (r13 > r16 ? 1 : (r13 == r16 ? 0 : -1));
        if (r12 == 0) goto L_0x0248;
    L_0x0133:
        if (r0 != 0) goto L_0x0137;
    L_0x0135:
        goto L_0x0248;
    L_0x0137:
        r0 = r7.encryptFile;
        if (r0 == 0) goto L_0x01a0;
    L_0x013b:
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r2 = r7.location;
        r12 = r2.volume_id;
        r0.append(r12);
        r0.append(r15);
        r2 = r7.location;
        r2 = r2.local_id;
        r0.append(r2);
        r0.append(r4);
        r0 = r0.toString();
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r4 = r7.location;
        r4 = r4.volume_id;
        r2.append(r4);
        r2.append(r15);
        r4 = r7.location;
        r4 = r4.local_id;
        r2.append(r4);
        r2.append(r6);
        r4 = r7.ext;
        r2.append(r4);
        r2.append(r3);
        r3 = r2.toString();
        r2 = r7.key;
        if (r2 == 0) goto L_0x02ba;
    L_0x0181:
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r4 = r7.location;
        r4 = r4.volume_id;
        r2.append(r4);
        r2.append(r15);
        r4 = r7.location;
        r4 = r4.local_id;
        r2.append(r4);
        r2.append(r1);
        r12 = r2.toString();
        goto L_0x02b8;
    L_0x01a0:
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r1 = r7.location;
        r3 = r1.volume_id;
        r0.append(r3);
        r0.append(r15);
        r1 = r7.location;
        r1 = r1.local_id;
        r0.append(r1);
        r0.append(r5);
        r1 = r0.toString();
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r3 = r7.location;
        r3 = r3.volume_id;
        r0.append(r3);
        r0.append(r15);
        r3 = r7.location;
        r3 = r3.local_id;
        r0.append(r3);
        r0.append(r6);
        r3 = r7.ext;
        r0.append(r3);
        r3 = r0.toString();
        r0 = r7.key;
        if (r0 == 0) goto L_0x0201;
    L_0x01e3:
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r4 = r7.location;
        r4 = r4.volume_id;
        r0.append(r4);
        r0.append(r15);
        r4 = r7.location;
        r4 = r4.local_id;
        r0.append(r4);
        r0.append(r2);
        r12 = r0.toString();
        goto L_0x0202;
    L_0x0201:
        r12 = 0;
    L_0x0202:
        r0 = r7.notLoadedBytesRanges;
        if (r0 == 0) goto L_0x0226;
    L_0x0206:
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r2 = r7.location;
        r4 = r2.volume_id;
        r0.append(r4);
        r0.append(r15);
        r2 = r7.location;
        r2 = r2.local_id;
        r0.append(r2);
        r2 = ".pt";
        r0.append(r2);
        r0 = r0.toString();
        goto L_0x0227;
    L_0x0226:
        r0 = 0;
    L_0x0227:
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r4 = r7.location;
        r4 = r4.volume_id;
        r2.append(r4);
        r2.append(r15);
        r4 = r7.location;
        r4 = r4.local_id;
        r2.append(r4);
        r4 = ".preload";
        r2.append(r4);
        r2 = r2.toString();
        goto L_0x0358;
    L_0x0248:
        r7.onFail(r8, r9);
        return r9;
    L_0x024c:
        r0 = r7.datacenterId;
        if (r0 == 0) goto L_0x0792;
    L_0x0250:
        r0 = r7.location;
        r12 = r0.id;
        r0 = (r12 > r10 ? 1 : (r12 == r10 ? 0 : -1));
        if (r0 != 0) goto L_0x025a;
    L_0x0258:
        goto L_0x0792;
    L_0x025a:
        r0 = r7.encryptFile;
        if (r0 == 0) goto L_0x02bf;
    L_0x025e:
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r2 = r7.datacenterId;
        r0.append(r2);
        r0.append(r15);
        r2 = r7.location;
        r5 = r2.id;
        r0.append(r5);
        r0.append(r4);
        r0 = r0.toString();
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r4 = r7.datacenterId;
        r2.append(r4);
        r2.append(r15);
        r4 = r7.location;
        r4 = r4.id;
        r2.append(r4);
        r4 = r7.ext;
        r2.append(r4);
        r2.append(r3);
        r3 = r2.toString();
        r2 = r7.key;
        if (r2 == 0) goto L_0x02ba;
    L_0x029d:
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r4 = r7.datacenterId;
        r2.append(r4);
        r2.append(r15);
        r4 = r7.location;
        r4 = r4.id;
        r2.append(r4);
        r2.append(r1);
        r12 = r2.toString();
    L_0x02b8:
        r1 = r12;
        goto L_0x02bb;
    L_0x02ba:
        r1 = 0;
    L_0x02bb:
        r2 = 0;
        r12 = 0;
        goto L_0x035e;
    L_0x02bf:
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r1 = r7.datacenterId;
        r0.append(r1);
        r0.append(r15);
        r1 = r7.location;
        r3 = r1.id;
        r0.append(r3);
        r0.append(r5);
        r1 = r0.toString();
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r3 = r7.datacenterId;
        r0.append(r3);
        r0.append(r15);
        r3 = r7.location;
        r3 = r3.id;
        r0.append(r3);
        r3 = r7.ext;
        r0.append(r3);
        r3 = r0.toString();
        r0 = r7.key;
        if (r0 == 0) goto L_0x0317;
    L_0x02fb:
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r4 = r7.datacenterId;
        r0.append(r4);
        r0.append(r15);
        r4 = r7.location;
        r4 = r4.id;
        r0.append(r4);
        r0.append(r2);
        r12 = r0.toString();
        goto L_0x0318;
    L_0x0317:
        r12 = 0;
    L_0x0318:
        r0 = r7.notLoadedBytesRanges;
        if (r0 == 0) goto L_0x033a;
    L_0x031c:
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r2 = r7.datacenterId;
        r0.append(r2);
        r0.append(r15);
        r2 = r7.location;
        r4 = r2.id;
        r0.append(r4);
        r2 = ".pt";
        r0.append(r2);
        r0 = r0.toString();
        goto L_0x033b;
    L_0x033a:
        r0 = 0;
    L_0x033b:
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r4 = r7.datacenterId;
        r2.append(r4);
        r2.append(r15);
        r4 = r7.location;
        r4 = r4.id;
        r2.append(r4);
        r4 = ".preload";
        r2.append(r4);
        r2 = r2.toString();
    L_0x0358:
        r21 = r12;
        r12 = r0;
        r0 = r1;
        r1 = r21;
    L_0x035e:
        r4 = new java.util.ArrayList;
        r5 = r7.currentMaxDownloadRequests;
        r4.<init>(r5);
        r7.requestInfos = r4;
        r4 = new java.util.ArrayList;
        r5 = r7.currentMaxDownloadRequests;
        r5 = r5 - r8;
        r4.<init>(r5);
        r7.delayedRequestInfos = r4;
        r7.state = r8;
        r4 = r7.parentObject;
        r5 = r4 instanceof org.telegram.tgnet.TLRPC.TL_theme;
        if (r5 == 0) goto L_0x039f;
    L_0x0379:
        r4 = (org.telegram.tgnet.TLRPC.TL_theme) r4;
        r5 = new java.io.File;
        r6 = org.telegram.messenger.ApplicationLoader.getFilesDirFixed();
        r13 = new java.lang.StringBuilder;
        r13.<init>();
        r14 = "remote";
        r13.append(r14);
        r14 = r4.id;
        r13.append(r14);
        r4 = ".attheme";
        r13.append(r4);
        r4 = r13.toString();
        r5.<init>(r6, r4);
        r7.cacheFileFinal = r5;
        goto L_0x03a8;
    L_0x039f:
        r4 = new java.io.File;
        r5 = r7.storePath;
        r4.<init>(r5, r3);
        r7.cacheFileFinal = r4;
    L_0x03a8:
        r4 = r7.cacheFileFinal;
        r4 = r4.exists();
        if (r4 == 0) goto L_0x03cb;
    L_0x03b0:
        r5 = r7.parentObject;
        r5 = r5 instanceof org.telegram.tgnet.TLRPC.TL_theme;
        if (r5 != 0) goto L_0x03c5;
    L_0x03b6:
        r5 = r7.totalBytesCount;
        if (r5 == 0) goto L_0x03cb;
    L_0x03ba:
        r5 = (long) r5;
        r13 = r7.cacheFileFinal;
        r13 = r13.length();
        r15 = (r5 > r13 ? 1 : (r5 == r13 ? 0 : -1));
        if (r15 == 0) goto L_0x03cb;
    L_0x03c5:
        r4 = r7.cacheFileFinal;
        r4.delete();
        r4 = 0;
    L_0x03cb:
        if (r4 != 0) goto L_0x0788;
    L_0x03cd:
        r4 = new java.io.File;
        r5 = r7.tempPath;
        r4.<init>(r5, r0);
        r7.cacheFileTemp = r4;
        r4 = r7.ungzip;
        if (r4 == 0) goto L_0x03f4;
    L_0x03da:
        r4 = new java.io.File;
        r5 = r7.tempPath;
        r6 = new java.lang.StringBuilder;
        r6.<init>();
        r6.append(r0);
        r0 = ".gz";
        r6.append(r0);
        r0 = r6.toString();
        r4.<init>(r5, r0);
        r7.cacheFileGzipTemp = r4;
    L_0x03f4:
        r0 = r7.encryptFile;
        r4 = 32;
        r13 = "rws";
        if (r0 == 0) goto L_0x0473;
    L_0x03fc:
        r0 = new java.io.File;
        r5 = org.telegram.messenger.FileLoader.getInternalCacheDir();
        r6 = new java.lang.StringBuilder;
        r6.<init>();
        r6.append(r3);
        r3 = ".key";
        r6.append(r3);
        r3 = r6.toString();
        r0.<init>(r5, r3);
        r3 = new java.io.RandomAccessFile;	 Catch:{ Exception -> 0x046d }
        r3.<init>(r0, r13);	 Catch:{ Exception -> 0x046d }
        r5 = r0.length();	 Catch:{ Exception -> 0x046d }
        r0 = new byte[r4];	 Catch:{ Exception -> 0x046d }
        r7.encryptKey = r0;	 Catch:{ Exception -> 0x046d }
        r0 = 16;
        r0 = new byte[r0];	 Catch:{ Exception -> 0x046d }
        r7.encryptIv = r0;	 Catch:{ Exception -> 0x046d }
        r0 = (r5 > r10 ? 1 : (r5 == r10 ? 0 : -1));
        if (r0 <= 0) goto L_0x0442;
    L_0x042d:
        r14 = 48;
        r5 = r5 % r14;
        r0 = (r5 > r10 ? 1 : (r5 == r10 ? 0 : -1));
        if (r0 != 0) goto L_0x0442;
    L_0x0434:
        r0 = r7.encryptKey;	 Catch:{ Exception -> 0x046d }
        r3.read(r0, r9, r4);	 Catch:{ Exception -> 0x046d }
        r0 = r7.encryptIv;	 Catch:{ Exception -> 0x046d }
        r5 = 16;
        r3.read(r0, r9, r5);	 Catch:{ Exception -> 0x046d }
        r5 = 0;
        goto L_0x045b;
    L_0x0442:
        r0 = org.telegram.messenger.Utilities.random;	 Catch:{ Exception -> 0x046d }
        r5 = r7.encryptKey;	 Catch:{ Exception -> 0x046d }
        r0.nextBytes(r5);	 Catch:{ Exception -> 0x046d }
        r0 = org.telegram.messenger.Utilities.random;	 Catch:{ Exception -> 0x046d }
        r5 = r7.encryptIv;	 Catch:{ Exception -> 0x046d }
        r0.nextBytes(r5);	 Catch:{ Exception -> 0x046d }
        r0 = r7.encryptKey;	 Catch:{ Exception -> 0x046d }
        r3.write(r0);	 Catch:{ Exception -> 0x046d }
        r0 = r7.encryptIv;	 Catch:{ Exception -> 0x046d }
        r3.write(r0);	 Catch:{ Exception -> 0x046d }
        r5 = 1;
    L_0x045b:
        r0 = r3.getChannel();	 Catch:{ Exception -> 0x0463 }
        r0.close();	 Catch:{ Exception -> 0x0463 }
        goto L_0x0467;
    L_0x0463:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);	 Catch:{ Exception -> 0x046b }
    L_0x0467:
        r3.close();	 Catch:{ Exception -> 0x046b }
        goto L_0x0474;
    L_0x046b:
        r0 = move-exception;
        goto L_0x046f;
    L_0x046d:
        r0 = move-exception;
        r5 = 0;
    L_0x046f:
        org.telegram.messenger.FileLog.e(r0);
        goto L_0x0474;
    L_0x0473:
        r5 = 0;
    L_0x0474:
        r14 = new boolean[r8];
        r14[r9] = r9;
        r0 = r7.supportsPreloading;
        r15 = 4;
        if (r0 == 0) goto L_0x05aa;
    L_0x047e:
        if (r2 == 0) goto L_0x05aa;
    L_0x0480:
        r0 = new java.io.File;
        r3 = r7.tempPath;
        r0.<init>(r3, r2);
        r7.cacheFilePreload = r0;
        r0 = new java.io.RandomAccessFile;	 Catch:{ Exception -> 0x0577 }
        r2 = r7.cacheFilePreload;	 Catch:{ Exception -> 0x0577 }
        r0.<init>(r2, r13);	 Catch:{ Exception -> 0x0577 }
        r7.preloadStream = r0;	 Catch:{ Exception -> 0x0577 }
        r0 = r7.preloadStream;	 Catch:{ Exception -> 0x0577 }
        r2 = r0.length();	 Catch:{ Exception -> 0x0577 }
        r7.preloadStreamFileOffset = r8;	 Catch:{ Exception -> 0x0577 }
        r10 = (long) r9;	 Catch:{ Exception -> 0x0577 }
        r10 = r2 - r10;
        r17 = 1;
        r0 = (r10 > r17 ? 1 : (r10 == r17 ? 0 : -1));
        if (r0 <= 0) goto L_0x0568;
    L_0x04a3:
        r0 = r7.preloadStream;	 Catch:{ Exception -> 0x0577 }
        r0 = r0.readByte();	 Catch:{ Exception -> 0x0577 }
        if (r0 == 0) goto L_0x04ad;
    L_0x04ab:
        r0 = 1;
        goto L_0x04ae;
    L_0x04ad:
        r0 = 0;
    L_0x04ae:
        r14[r9] = r0;	 Catch:{ Exception -> 0x0577 }
        r0 = 1;
    L_0x04b1:
        r10 = (long) r0;	 Catch:{ Exception -> 0x0577 }
        r6 = (r10 > r2 ? 1 : (r10 == r2 ? 0 : -1));
        if (r6 >= 0) goto L_0x0568;
    L_0x04b6:
        r10 = r2 - r10;
        r6 = (r10 > r15 ? 1 : (r10 == r15 ? 0 : -1));
        if (r6 >= 0) goto L_0x04be;
    L_0x04bc:
        goto L_0x0568;
    L_0x04be:
        r6 = r7.preloadStream;	 Catch:{ Exception -> 0x0577 }
        r6 = r6.readInt();	 Catch:{ Exception -> 0x0577 }
        r0 = r0 + 4;
        r10 = (long) r0;	 Catch:{ Exception -> 0x0577 }
        r10 = r2 - r10;
        r17 = (r10 > r15 ? 1 : (r10 == r15 ? 0 : -1));
        if (r17 < 0) goto L_0x0568;
    L_0x04cd:
        if (r6 < 0) goto L_0x0568;
    L_0x04cf:
        r10 = r7.totalBytesCount;	 Catch:{ Exception -> 0x0577 }
        if (r6 <= r10) goto L_0x04d5;
    L_0x04d3:
        goto L_0x0568;
    L_0x04d5:
        r10 = r7.preloadStream;	 Catch:{ Exception -> 0x0577 }
        r10 = r10.readInt();	 Catch:{ Exception -> 0x0577 }
        r0 = r0 + 4;
        r17 = r5;
        r4 = (long) r0;
        r4 = r2 - r4;
        r18 = r12;
        r11 = (long) r10;
        r19 = (r4 > r11 ? 1 : (r4 == r11 ? 0 : -1));
        if (r19 < 0) goto L_0x056c;
    L_0x04e9:
        r4 = r7.currentDownloadChunkSize;	 Catch:{ Exception -> 0x0575 }
        if (r10 <= r4) goto L_0x04ef;
    L_0x04ed:
        goto L_0x056c;
    L_0x04ef:
        r4 = new org.telegram.messenger.FileLoadOperation$PreloadRange;	 Catch:{ Exception -> 0x0575 }
        r5 = 0;
        r4.<init>(r0, r10);	 Catch:{ Exception -> 0x0575 }
        r0 = r0 + r10;
        r5 = r7.preloadStream;	 Catch:{ Exception -> 0x0575 }
        r11 = (long) r0;	 Catch:{ Exception -> 0x0575 }
        r5.seek(r11);	 Catch:{ Exception -> 0x0575 }
        r11 = r2 - r11;
        r19 = 12;
        r5 = (r11 > r19 ? 1 : (r11 == r19 ? 0 : -1));
        if (r5 >= 0) goto L_0x0505;
    L_0x0504:
        goto L_0x056c;
    L_0x0505:
        r5 = r7.preloadStream;	 Catch:{ Exception -> 0x0575 }
        r5 = r5.readInt();	 Catch:{ Exception -> 0x0575 }
        r7.foundMoovSize = r5;	 Catch:{ Exception -> 0x0575 }
        r5 = r7.foundMoovSize;	 Catch:{ Exception -> 0x0575 }
        if (r5 == 0) goto L_0x0522;
    L_0x0511:
        r5 = r7.nextPreloadDownloadOffset;	 Catch:{ Exception -> 0x0575 }
        r11 = r7.totalBytesCount;	 Catch:{ Exception -> 0x0575 }
        r11 = r11 / 2;
        if (r5 <= r11) goto L_0x051b;
    L_0x0519:
        r5 = 2;
        goto L_0x051c;
    L_0x051b:
        r5 = 1;
    L_0x051c:
        r7.moovFound = r5;	 Catch:{ Exception -> 0x0575 }
        r5 = r7.foundMoovSize;	 Catch:{ Exception -> 0x0575 }
        r7.preloadNotRequestedBytesCount = r5;	 Catch:{ Exception -> 0x0575 }
    L_0x0522:
        r5 = r7.preloadStream;	 Catch:{ Exception -> 0x0575 }
        r5 = r5.readInt();	 Catch:{ Exception -> 0x0575 }
        r7.nextPreloadDownloadOffset = r5;	 Catch:{ Exception -> 0x0575 }
        r5 = r7.preloadStream;	 Catch:{ Exception -> 0x0575 }
        r5 = r5.readInt();	 Catch:{ Exception -> 0x0575 }
        r7.nextAtomOffset = r5;	 Catch:{ Exception -> 0x0575 }
        r0 = r0 + 12;
        r5 = r7.preloadedBytesRanges;	 Catch:{ Exception -> 0x0575 }
        if (r5 != 0) goto L_0x053f;
    L_0x0538:
        r5 = new android.util.SparseArray;	 Catch:{ Exception -> 0x0575 }
        r5.<init>();	 Catch:{ Exception -> 0x0575 }
        r7.preloadedBytesRanges = r5;	 Catch:{ Exception -> 0x0575 }
    L_0x053f:
        r5 = r7.requestedPreloadedBytesRanges;	 Catch:{ Exception -> 0x0575 }
        if (r5 != 0) goto L_0x054a;
    L_0x0543:
        r5 = new android.util.SparseIntArray;	 Catch:{ Exception -> 0x0575 }
        r5.<init>();	 Catch:{ Exception -> 0x0575 }
        r7.requestedPreloadedBytesRanges = r5;	 Catch:{ Exception -> 0x0575 }
    L_0x054a:
        r5 = r7.preloadedBytesRanges;	 Catch:{ Exception -> 0x0575 }
        r5.put(r6, r4);	 Catch:{ Exception -> 0x0575 }
        r4 = r7.requestedPreloadedBytesRanges;	 Catch:{ Exception -> 0x0575 }
        r4.put(r6, r8);	 Catch:{ Exception -> 0x0575 }
        r4 = r7.totalPreloadedBytes;	 Catch:{ Exception -> 0x0575 }
        r4 = r4 + r10;
        r7.totalPreloadedBytes = r4;	 Catch:{ Exception -> 0x0575 }
        r4 = r7.preloadStreamFileOffset;	 Catch:{ Exception -> 0x0575 }
        r10 = r10 + 20;
        r4 = r4 + r10;
        r7.preloadStreamFileOffset = r4;	 Catch:{ Exception -> 0x0575 }
        r5 = r17;
        r12 = r18;
        r4 = 32;
        goto L_0x04b1;
    L_0x0568:
        r17 = r5;
        r18 = r12;
    L_0x056c:
        r0 = r7.preloadStream;	 Catch:{ Exception -> 0x0575 }
        r2 = r7.preloadStreamFileOffset;	 Catch:{ Exception -> 0x0575 }
        r2 = (long) r2;	 Catch:{ Exception -> 0x0575 }
        r0.seek(r2);	 Catch:{ Exception -> 0x0575 }
        goto L_0x057f;
    L_0x0575:
        r0 = move-exception;
        goto L_0x057c;
    L_0x0577:
        r0 = move-exception;
        r17 = r5;
        r18 = r12;
    L_0x057c:
        org.telegram.messenger.FileLog.e(r0);
    L_0x057f:
        r0 = r7.isPreloadVideoOperation;
        if (r0 != 0) goto L_0x05ae;
    L_0x0583:
        r0 = r7.preloadedBytesRanges;
        if (r0 != 0) goto L_0x05ae;
    L_0x0587:
        r2 = 0;
        r7.cacheFilePreload = r2;
        r0 = r7.preloadStream;	 Catch:{ Exception -> 0x05a5 }
        if (r0 == 0) goto L_0x05ae;
    L_0x058e:
        r0 = r7.preloadStream;	 Catch:{ Exception -> 0x0598 }
        r0 = r0.getChannel();	 Catch:{ Exception -> 0x0598 }
        r0.close();	 Catch:{ Exception -> 0x0598 }
        goto L_0x059c;
    L_0x0598:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);	 Catch:{ Exception -> 0x05a5 }
    L_0x059c:
        r0 = r7.preloadStream;	 Catch:{ Exception -> 0x05a5 }
        r0.close();	 Catch:{ Exception -> 0x05a5 }
        r2 = 0;
        r7.preloadStream = r2;	 Catch:{ Exception -> 0x05a5 }
        goto L_0x05ae;
    L_0x05a5:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
        goto L_0x05ae;
    L_0x05aa:
        r17 = r5;
        r18 = r12;
    L_0x05ae:
        if (r18 == 0) goto L_0x060c;
    L_0x05b0:
        r0 = new java.io.File;
        r2 = r7.tempPath;
        r12 = r18;
        r0.<init>(r2, r12);
        r7.cacheFileParts = r0;
        r0 = new java.io.RandomAccessFile;	 Catch:{ Exception -> 0x0608 }
        r2 = r7.cacheFileParts;	 Catch:{ Exception -> 0x0608 }
        r0.<init>(r2, r13);	 Catch:{ Exception -> 0x0608 }
        r7.filePartsStream = r0;	 Catch:{ Exception -> 0x0608 }
        r0 = r7.filePartsStream;	 Catch:{ Exception -> 0x0608 }
        r2 = r0.length();	 Catch:{ Exception -> 0x0608 }
        r4 = 8;
        r4 = r2 % r4;
        r0 = (r4 > r15 ? 1 : (r4 == r15 ? 0 : -1));
        if (r0 != 0) goto L_0x060c;
    L_0x05d2:
        r2 = r2 - r15;
        r0 = r7.filePartsStream;	 Catch:{ Exception -> 0x0608 }
        r0 = r0.readInt();	 Catch:{ Exception -> 0x0608 }
        r4 = (long) r0;	 Catch:{ Exception -> 0x0608 }
        r10 = 2;
        r2 = r2 / r10;
        r6 = (r4 > r2 ? 1 : (r4 == r2 ? 0 : -1));
        if (r6 > 0) goto L_0x060c;
    L_0x05e1:
        r2 = 0;
    L_0x05e2:
        if (r2 >= r0) goto L_0x060c;
    L_0x05e4:
        r3 = r7.filePartsStream;	 Catch:{ Exception -> 0x0608 }
        r3 = r3.readInt();	 Catch:{ Exception -> 0x0608 }
        r4 = r7.filePartsStream;	 Catch:{ Exception -> 0x0608 }
        r4 = r4.readInt();	 Catch:{ Exception -> 0x0608 }
        r5 = r7.notLoadedBytesRanges;	 Catch:{ Exception -> 0x0608 }
        r6 = new org.telegram.messenger.FileLoadOperation$Range;	 Catch:{ Exception -> 0x0608 }
        r10 = 0;
        r6.<init>(r3, r4);	 Catch:{ Exception -> 0x0608 }
        r5.add(r6);	 Catch:{ Exception -> 0x0608 }
        r5 = r7.notRequestedBytesRanges;	 Catch:{ Exception -> 0x0608 }
        r6 = new org.telegram.messenger.FileLoadOperation$Range;	 Catch:{ Exception -> 0x0608 }
        r6.<init>(r3, r4);	 Catch:{ Exception -> 0x0608 }
        r5.add(r6);	 Catch:{ Exception -> 0x0608 }
        r2 = r2 + 1;
        goto L_0x05e2;
    L_0x0608:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x060c:
        r0 = r7.cacheFileTemp;
        r0 = r0.exists();
        if (r0 == 0) goto L_0x066b;
    L_0x0614:
        if (r17 == 0) goto L_0x061c;
    L_0x0616:
        r0 = r7.cacheFileTemp;
        r0.delete();
        goto L_0x068e;
    L_0x061c:
        r0 = r7.cacheFileTemp;
        r2 = r0.length();
        if (r1 == 0) goto L_0x0633;
    L_0x0624:
        r0 = r7.currentDownloadChunkSize;
        r4 = (long) r0;
        r2 = r2 % r4;
        r4 = 0;
        r0 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1));
        if (r0 == 0) goto L_0x0633;
    L_0x062e:
        r7.downloadedBytes = r9;
        r7.requestedBytesCount = r9;
        goto L_0x0643;
    L_0x0633:
        r0 = r7.cacheFileTemp;
        r2 = r0.length();
        r0 = (int) r2;
        r2 = r7.currentDownloadChunkSize;
        r0 = r0 / r2;
        r0 = r0 * r2;
        r7.downloadedBytes = r0;
        r7.requestedBytesCount = r0;
    L_0x0643:
        r0 = r7.notLoadedBytesRanges;
        if (r0 == 0) goto L_0x068e;
    L_0x0647:
        r0 = r0.isEmpty();
        if (r0 == 0) goto L_0x068e;
    L_0x064d:
        r0 = r7.notLoadedBytesRanges;
        r2 = new org.telegram.messenger.FileLoadOperation$Range;
        r3 = r7.downloadedBytes;
        r4 = r7.totalBytesCount;
        r5 = 0;
        r2.<init>(r3, r4);
        r0.add(r2);
        r0 = r7.notRequestedBytesRanges;
        r2 = new org.telegram.messenger.FileLoadOperation$Range;
        r3 = r7.downloadedBytes;
        r4 = r7.totalBytesCount;
        r2.<init>(r3, r4);
        r0.add(r2);
        goto L_0x068e;
    L_0x066b:
        r0 = r7.notLoadedBytesRanges;
        if (r0 == 0) goto L_0x068e;
    L_0x066f:
        r0 = r0.isEmpty();
        if (r0 == 0) goto L_0x068e;
    L_0x0675:
        r0 = r7.notLoadedBytesRanges;
        r2 = new org.telegram.messenger.FileLoadOperation$Range;
        r3 = r7.totalBytesCount;
        r4 = 0;
        r2.<init>(r9, r3);
        r0.add(r2);
        r0 = r7.notRequestedBytesRanges;
        r2 = new org.telegram.messenger.FileLoadOperation$Range;
        r3 = r7.totalBytesCount;
        r2.<init>(r9, r3);
        r0.add(r2);
    L_0x068e:
        r0 = r7.notLoadedBytesRanges;
        if (r0 == 0) goto L_0x06ba;
    L_0x0692:
        r2 = r7.totalBytesCount;
        r7.downloadedBytes = r2;
        r0 = r0.size();
        r2 = 0;
    L_0x069b:
        if (r2 >= r0) goto L_0x06b6;
    L_0x069d:
        r3 = r7.notLoadedBytesRanges;
        r3 = r3.get(r2);
        r3 = (org.telegram.messenger.FileLoadOperation.Range) r3;
        r4 = r7.downloadedBytes;
        r5 = r3.end;
        r3 = r3.start;
        r5 = r5 - r3;
        r4 = r4 - r5;
        r7.downloadedBytes = r4;
        r2 = r2 + 1;
        goto L_0x069b;
    L_0x06b6:
        r0 = r7.downloadedBytes;
        r7.requestedBytesCount = r0;
    L_0x06ba:
        r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r0 == 0) goto L_0x06f9;
    L_0x06be:
        r0 = r7.isPreloadVideoOperation;
        if (r0 == 0) goto L_0x06d9;
    L_0x06c2:
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r2 = "start preloading file to temp = ";
        r0.append(r2);
        r2 = r7.cacheFileTemp;
        r0.append(r2);
        r0 = r0.toString();
        org.telegram.messenger.FileLog.d(r0);
        goto L_0x06f9;
    L_0x06d9:
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r2 = "start loading file to temp = ";
        r0.append(r2);
        r2 = r7.cacheFileTemp;
        r0.append(r2);
        r2 = " final = ";
        r0.append(r2);
        r2 = r7.cacheFileFinal;
        r0.append(r2);
        r0 = r0.toString();
        org.telegram.messenger.FileLog.d(r0);
    L_0x06f9:
        if (r1 == 0) goto L_0x073d;
    L_0x06fb:
        r0 = new java.io.File;
        r2 = r7.tempPath;
        r0.<init>(r2, r1);
        r7.cacheIvTemp = r0;
        r0 = new java.io.RandomAccessFile;	 Catch:{ Exception -> 0x0735 }
        r1 = r7.cacheIvTemp;	 Catch:{ Exception -> 0x0735 }
        r0.<init>(r1, r13);	 Catch:{ Exception -> 0x0735 }
        r7.fiv = r0;	 Catch:{ Exception -> 0x0735 }
        r0 = r7.downloadedBytes;	 Catch:{ Exception -> 0x0735 }
        if (r0 == 0) goto L_0x073d;
    L_0x0711:
        if (r17 != 0) goto L_0x073d;
    L_0x0713:
        r0 = r7.cacheIvTemp;	 Catch:{ Exception -> 0x0735 }
        r0 = r0.length();	 Catch:{ Exception -> 0x0735 }
        r2 = 0;
        r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1));
        if (r4 <= 0) goto L_0x0730;
    L_0x071f:
        r4 = 32;
        r0 = r0 % r4;
        r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1));
        if (r4 != 0) goto L_0x0730;
    L_0x0726:
        r0 = r7.fiv;	 Catch:{ Exception -> 0x0735 }
        r1 = r7.iv;	 Catch:{ Exception -> 0x0735 }
        r2 = 32;
        r0.read(r1, r9, r2);	 Catch:{ Exception -> 0x0735 }
        goto L_0x073d;
    L_0x0730:
        r7.downloadedBytes = r9;	 Catch:{ Exception -> 0x0735 }
        r7.requestedBytesCount = r9;	 Catch:{ Exception -> 0x0735 }
        goto L_0x073d;
    L_0x0735:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
        r7.downloadedBytes = r9;
        r7.requestedBytesCount = r9;
    L_0x073d:
        r0 = r7.isPreloadVideoOperation;
        if (r0 != 0) goto L_0x0759;
    L_0x0741:
        r0 = r7.downloadedBytes;
        if (r0 == 0) goto L_0x0759;
    L_0x0745:
        r0 = r7.totalBytesCount;
        if (r0 <= 0) goto L_0x0759;
    L_0x0749:
        r22.copyNotLoadedRanges();
        r1 = r7.delegate;
        r0 = r7.downloadedBytes;
        r3 = (long) r0;
        r0 = r7.totalBytesCount;
        r5 = (long) r0;
        r2 = r22;
        r1.didChangedLoadProgress(r2, r3, r5);
    L_0x0759:
        r0 = new java.io.RandomAccessFile;	 Catch:{ Exception -> 0x076f }
        r1 = r7.cacheFileTemp;	 Catch:{ Exception -> 0x076f }
        r0.<init>(r1, r13);	 Catch:{ Exception -> 0x076f }
        r7.fileOutputStream = r0;	 Catch:{ Exception -> 0x076f }
        r0 = r7.downloadedBytes;	 Catch:{ Exception -> 0x076f }
        if (r0 == 0) goto L_0x0773;
    L_0x0766:
        r0 = r7.fileOutputStream;	 Catch:{ Exception -> 0x076f }
        r1 = r7.downloadedBytes;	 Catch:{ Exception -> 0x076f }
        r1 = (long) r1;	 Catch:{ Exception -> 0x076f }
        r0.seek(r1);	 Catch:{ Exception -> 0x076f }
        goto L_0x0773;
    L_0x076f:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x0773:
        r0 = r7.fileOutputStream;
        if (r0 != 0) goto L_0x077b;
    L_0x0777:
        r7.onFail(r8, r9);
        return r9;
    L_0x077b:
        r7.started = r8;
        r0 = org.telegram.messenger.Utilities.stageQueue;
        r1 = new org.telegram.messenger.-$$Lambda$FileLoadOperation$pnBGbN7TnymWdMGyH-sNRrKnKro;
        r1.<init>(r7, r14);
        r0.postRunnable(r1);
        goto L_0x0791;
    L_0x0788:
        r7.started = r8;
        r7.onFinishLoadingFile(r9);	 Catch:{ Exception -> 0x078e }
        goto L_0x0791;
    L_0x078e:
        r7.onFail(r8, r9);
    L_0x0791:
        return r8;
    L_0x0792:
        r7.onFail(r8, r9);
        return r9;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.FileLoadOperation.start(org.telegram.messenger.FileLoadOperationStream, int, boolean):boolean");
    }

    public /* synthetic */ void lambda$start$4$FileLoadOperation(boolean z, int i, FileLoadOperationStream fileLoadOperationStream, boolean z2) {
        if (this.streamListeners == null) {
            this.streamListeners = new ArrayList();
        }
        int i2;
        if (z) {
            i2 = this.currentDownloadChunkSize;
            i = (i / i2) * i2;
            RequestInfo requestInfo = this.priorityRequestInfo;
            if (!(requestInfo == null || requestInfo.offset == i)) {
                this.requestInfos.remove(this.priorityRequestInfo);
                this.requestedBytesCount -= this.currentDownloadChunkSize;
                removePart(this.notRequestedBytesRanges, this.priorityRequestInfo.offset, this.priorityRequestInfo.offset + this.currentDownloadChunkSize);
                if (this.priorityRequestInfo.requestToken != 0) {
                    ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.priorityRequestInfo.requestToken, true);
                    this.requestsCount--;
                }
                if (BuildVars.DEBUG_VERSION) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("frame get cancel request at offset ");
                    stringBuilder.append(this.priorityRequestInfo.offset);
                    FileLog.d(stringBuilder.toString());
                }
                this.priorityRequestInfo = null;
            }
            if (this.priorityRequestInfo == null) {
                this.streamPriorityStartOffset = i;
            }
        } else {
            i2 = this.currentDownloadChunkSize;
            this.streamStartOffset = (i / i2) * i2;
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
        if (this.totalBytesCount == 0 || !((this.isPreloadVideoOperation && zArr[0]) || this.downloadedBytes == this.totalBytesCount)) {
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
            Utilities.stageQueue.postRunnable(new -$$Lambda$FileLoadOperation$nc5Q9zp5V-Wm8rlOOmtozGXmKXw(this, z));
        } else {
            this.isPreloadVideoOperation = z;
        }
    }

    public /* synthetic */ void lambda$setIsPreloadVideoOperation$6$FileLoadOperation(boolean z) {
        this.requestedBytesCount = 0;
        clearOperaion(null, true);
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
        Utilities.stageQueue.postRunnable(new -$$Lambda$FileLoadOperation$yUSq18W9rnsNzIHP9ZjuCLK1pQs(this));
    }

    public /* synthetic */ void lambda$cancel$7$FileLoadOperation() {
        if (this.state != 3 && this.state != 2) {
            if (this.requestInfos != null) {
                for (int i = 0; i < this.requestInfos.size(); i++) {
                    RequestInfo requestInfo = (RequestInfo) this.requestInfos.get(i);
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
                    FileLog.e(e);
                }
                this.fileOutputStream.close();
                this.fileOutputStream = null;
            }
        } catch (Exception e2) {
            FileLog.e(e2);
        }
        try {
            if (this.preloadStream != null) {
                try {
                    this.preloadStream.getChannel().close();
                } catch (Exception e22) {
                    FileLog.e(e22);
                }
                this.preloadStream.close();
                this.preloadStream = null;
            }
        } catch (Exception e222) {
            FileLog.e(e222);
        }
        try {
            if (this.fileReadStream != null) {
                try {
                    this.fileReadStream.getChannel().close();
                } catch (Exception e2222) {
                    FileLog.e(e2222);
                }
                this.fileReadStream.close();
                this.fileReadStream = null;
            }
        } catch (Exception e22222) {
            FileLog.e(e22222);
        }
        try {
            if (this.filePartsStream != null) {
                try {
                    this.filePartsStream.getChannel().close();
                } catch (Exception e222222) {
                    FileLog.e(e222222);
                }
                this.filePartsStream.close();
                this.filePartsStream = null;
            }
        } catch (Exception e2222222) {
            FileLog.e(e2222222);
        }
        try {
            if (this.fiv != null) {
                this.fiv.close();
                this.fiv = null;
            }
        } catch (Exception e3) {
            FileLog.e(e3);
        }
        if (this.delayedRequestInfos != null) {
            for (int i = 0; i < this.delayedRequestInfos.size(); i++) {
                RequestInfo requestInfo = (RequestInfo) this.delayedRequestInfos.get(i);
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
        StringBuilder stringBuilder;
        if (this.state == 1) {
            this.state = 3;
            cleanup();
            if (this.isPreloadVideoOperation) {
                this.preloadFinished = true;
                if (BuildVars.DEBUG_VERSION) {
                    StringBuilder stringBuilder2 = new StringBuilder();
                    stringBuilder2.append("finished preloading file to ");
                    stringBuilder2.append(this.cacheFileTemp);
                    stringBuilder2.append(" loaded ");
                    stringBuilder2.append(this.totalPreloadedBytes);
                    stringBuilder2.append(" of ");
                    stringBuilder2.append(this.totalBytesCount);
                    FileLog.d(stringBuilder2.toString());
                }
            } else {
                File file = this.cacheIvTemp;
                if (file != null) {
                    file.delete();
                    this.cacheIvTemp = null;
                }
                file = this.cacheFileParts;
                if (file != null) {
                    file.delete();
                    this.cacheFileParts = null;
                }
                file = this.cacheFilePreload;
                if (file != null) {
                    file.delete();
                    this.cacheFilePreload = null;
                }
                file = this.cacheFileTemp;
                if (file != null) {
                    String str = " to final = ";
                    boolean z2 = false;
                    if (this.ungzip) {
                        try {
                            GZIPInputStream gZIPInputStream = new GZIPInputStream(new FileInputStream(file));
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
                                stringBuilder = new StringBuilder();
                                stringBuilder.append("unable to ungzip temp = ");
                                stringBuilder.append(this.cacheFileTemp);
                                stringBuilder.append(str);
                                stringBuilder.append(this.cacheFileFinal);
                                FileLog.e(stringBuilder.toString());
                            }
                        }
                    }
                    if (this.ungzip) {
                        onFail(false, 0);
                        return;
                    }
                    if (this.parentObject instanceof TL_theme) {
                        try {
                            z2 = AndroidUtilities.copyFile(this.cacheFileTemp, this.cacheFileFinal);
                        } catch (Exception th2) {
                            FileLog.e(th2);
                        }
                    } else {
                        z2 = this.cacheFileTemp.renameTo(this.cacheFileFinal);
                    }
                    if (!z2) {
                        if (BuildVars.LOGS_ENABLED) {
                            stringBuilder = new StringBuilder();
                            stringBuilder.append("unable to rename temp = ");
                            stringBuilder.append(this.cacheFileTemp);
                            stringBuilder.append(str);
                            stringBuilder.append(this.cacheFileFinal);
                            stringBuilder.append(" retry = ");
                            stringBuilder.append(this.renameRetryCount);
                            FileLog.e(stringBuilder.toString());
                        }
                        this.renameRetryCount++;
                        if (this.renameRetryCount < 3) {
                            this.state = 1;
                            Utilities.stageQueue.postRunnable(new -$$Lambda$FileLoadOperation$OSevL5HT97P9D6Q-MYEqFdlPu-o(this, z), 200);
                            return;
                        }
                        this.cacheFileFinal = this.cacheFileTemp;
                    }
                }
                if (BuildVars.LOGS_ENABLED) {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("finished downloading file to ");
                    stringBuilder.append(this.cacheFileFinal);
                    FileLog.d(stringBuilder.toString());
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
        int limit = nativeByteBuffer.limit();
        int i3;
        do {
            if (i >= i2 - (this.preloadTempBuffer != null ? 16 : 0)) {
                i3 = i2 + limit;
                if (i < i3) {
                    if (i >= i3 - 16) {
                        this.preloadTempBufferCount = i3 - i;
                        nativeByteBuffer.position(nativeByteBuffer.limit() - this.preloadTempBufferCount);
                        nativeByteBuffer.readBytes(this.preloadTempBuffer, 0, this.preloadTempBufferCount, false);
                        return i3;
                    }
                    byte[] bArr;
                    int i4;
                    if (this.preloadTempBufferCount != 0) {
                        nativeByteBuffer.position(0);
                        bArr = this.preloadTempBuffer;
                        i4 = this.preloadTempBufferCount;
                        nativeByteBuffer.readBytes(bArr, i4, 16 - i4, false);
                        this.preloadTempBufferCount = 0;
                    } else {
                        nativeByteBuffer.position(i - i2);
                        nativeByteBuffer.readBytes(this.preloadTempBuffer, 0, 16, false);
                    }
                    bArr = this.preloadTempBuffer;
                    i4 = ((((bArr[0] & 255) << 24) + ((bArr[1] & 255) << 16)) + ((bArr[2] & 255) << 8)) + (bArr[3] & 255);
                    if (i4 == 0) {
                        return 0;
                    }
                    if (i4 == 1) {
                        i4 = ((((bArr[12] & 255) << 24) + ((bArr[13] & 255) << 16)) + ((bArr[14] & 255) << 8)) + (bArr[15] & 255);
                    }
                    byte[] bArr2 = this.preloadTempBuffer;
                    if (bArr2[4] == (byte) 109 && bArr2[5] == (byte) 111 && bArr2[6] == (byte) 111 && bArr2[7] == (byte) 118) {
                        return -i4;
                    }
                    i += i4;
                }
            }
            return 0;
        } while (i < i3);
        return i;
    }

    private void requestFileOffsets(int i) {
        if (!this.requestingCdnOffsets) {
            this.requestingCdnOffsets = true;
            TL_upload_getCdnFileHashes tL_upload_getCdnFileHashes = new TL_upload_getCdnFileHashes();
            tL_upload_getCdnFileHashes.file_token = this.cdnToken;
            tL_upload_getCdnFileHashes.offset = i;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_upload_getCdnFileHashes, new -$$Lambda$FileLoadOperation$nd2MBtmX1swxMm7OU6PKqeg0bEQ(this), null, null, 0, this.datacenterId, 1, true);
        }
    }

    public /* synthetic */ void lambda$requestFileOffsets$9$FileLoadOperation(TLObject tLObject, TL_error tL_error) {
        if (tL_error != null) {
            onFail(false, 0);
            return;
        }
        this.requestingCdnOffsets = false;
        Vector vector = (Vector) tLObject;
        if (!vector.objects.isEmpty()) {
            if (this.cdnHashes == null) {
                this.cdnHashes = new SparseArray();
            }
            for (int i = 0; i < vector.objects.size(); i++) {
                TL_fileHash tL_fileHash = (TL_fileHash) vector.objects.get(i);
                this.cdnHashes.put(tL_fileHash.offset, tL_fileHash);
            }
        }
        for (int i2 = 0; i2 < this.delayedRequestInfos.size(); i2++) {
            RequestInfo requestInfo = (RequestInfo) this.delayedRequestInfos.get(i2);
            if (this.notLoadedBytesRanges != null || this.downloadedBytes == requestInfo.offset) {
                this.delayedRequestInfos.remove(i2);
                if (!processRequestResult(requestInfo, null)) {
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
                }
                return;
            }
        }
    }

    /* Access modifiers changed, original: protected */
    /* JADX WARNING: Removed duplicated region for block: B:75:0x01e9 A:{Catch:{ Exception -> 0x0490 }} */
    /* JADX WARNING: Removed duplicated region for block: B:74:0x01db A:{Catch:{ Exception -> 0x0490 }} */
    /* JADX WARNING: Removed duplicated region for block: B:98:0x022a A:{Catch:{ Exception -> 0x0490 }} */
    /* JADX WARNING: Removed duplicated region for block: B:105:0x0253 A:{Catch:{ Exception -> 0x0490 }} */
    /* JADX WARNING: Removed duplicated region for block: B:108:0x0292 A:{Catch:{ Exception -> 0x0490 }} */
    /* JADX WARNING: Removed duplicated region for block: B:113:0x02dd A:{Catch:{ Exception -> 0x0490 }} */
    /* JADX WARNING: Removed duplicated region for block: B:151:0x03f2 A:{Catch:{ Exception -> 0x0490 }} */
    /* JADX WARNING: Removed duplicated region for block: B:98:0x022a A:{Catch:{ Exception -> 0x0490 }} */
    /* JADX WARNING: Removed duplicated region for block: B:105:0x0253 A:{Catch:{ Exception -> 0x0490 }} */
    /* JADX WARNING: Removed duplicated region for block: B:108:0x0292 A:{Catch:{ Exception -> 0x0490 }} */
    /* JADX WARNING: Removed duplicated region for block: B:113:0x02dd A:{Catch:{ Exception -> 0x0490 }} */
    /* JADX WARNING: Removed duplicated region for block: B:151:0x03f2 A:{Catch:{ Exception -> 0x0490 }} */
    /* JADX WARNING: Removed duplicated region for block: B:180:0x0487 A:{Catch:{ Exception -> 0x0490 }} */
    /* JADX WARNING: Removed duplicated region for block: B:179:0x0482 A:{Catch:{ Exception -> 0x0490 }} */
    public boolean processRequestResult(org.telegram.messenger.FileLoadOperation.RequestInfo r22, org.telegram.tgnet.TLRPC.TL_error r23) {
        /*
        r21 = this;
        r7 = r21;
        r0 = r23;
        r1 = r7.state;
        r2 = " offset ";
        r8 = 1;
        r9 = 0;
        if (r1 == r8) goto L_0x0031;
    L_0x000c:
        r0 = org.telegram.messenger.BuildVars.DEBUG_VERSION;
        if (r0 == 0) goto L_0x0030;
    L_0x0010:
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r1 = "trying to write to finished file ";
        r0.append(r1);
        r1 = r7.cacheFileFinal;
        r0.append(r1);
        r0.append(r2);
        r1 = r22.offset;
        r0.append(r1);
        r0 = r0.toString();
        org.telegram.messenger.FileLog.d(r0);
    L_0x0030:
        return r9;
    L_0x0031:
        r1 = r7.requestInfos;
        r3 = r22;
        r1.remove(r3);
        r1 = " local_id = ";
        r4 = 2;
        r5 = " id = ";
        r10 = 0;
        if (r0 != 0) goto L_0x0499;
    L_0x0040:
        r0 = r7.notLoadedBytesRanges;	 Catch:{ Exception -> 0x0490 }
        if (r0 != 0) goto L_0x0050;
    L_0x0044:
        r0 = r7.downloadedBytes;	 Catch:{ Exception -> 0x0490 }
        r6 = r22.offset;	 Catch:{ Exception -> 0x0490 }
        if (r0 == r6) goto L_0x0050;
    L_0x004c:
        r21.delayRequestInfo(r22);	 Catch:{ Exception -> 0x0490 }
        return r9;
    L_0x0050:
        r0 = r22.response;	 Catch:{ Exception -> 0x0490 }
        if (r0 == 0) goto L_0x005d;
    L_0x0056:
        r0 = r22.response;	 Catch:{ Exception -> 0x0490 }
        r0 = r0.bytes;	 Catch:{ Exception -> 0x0490 }
        goto L_0x0078;
    L_0x005d:
        r0 = r22.responseWeb;	 Catch:{ Exception -> 0x0490 }
        if (r0 == 0) goto L_0x006a;
    L_0x0063:
        r0 = r22.responseWeb;	 Catch:{ Exception -> 0x0490 }
        r0 = r0.bytes;	 Catch:{ Exception -> 0x0490 }
        goto L_0x0078;
    L_0x006a:
        r0 = r22.responseCdn;	 Catch:{ Exception -> 0x0490 }
        if (r0 == 0) goto L_0x0077;
    L_0x0070:
        r0 = r22.responseCdn;	 Catch:{ Exception -> 0x0490 }
        r0 = r0.bytes;	 Catch:{ Exception -> 0x0490 }
        goto L_0x0078;
    L_0x0077:
        r0 = r10;
    L_0x0078:
        if (r0 == 0) goto L_0x048c;
    L_0x007a:
        r6 = r0.limit();	 Catch:{ Exception -> 0x0490 }
        if (r6 != 0) goto L_0x0082;
    L_0x0080:
        goto L_0x048c;
    L_0x0082:
        r6 = r0.limit();	 Catch:{ Exception -> 0x0490 }
        r11 = r7.isCdn;	 Catch:{ Exception -> 0x0490 }
        r12 = 131072; // 0x20000 float:1.83671E-40 double:6.47582E-319;
        if (r11 == 0) goto L_0x00aa;
    L_0x008c:
        r11 = r22.offset;	 Catch:{ Exception -> 0x0490 }
        r11 = r11 / r12;
        r11 = r11 * r12;
        r13 = r7.cdnHashes;	 Catch:{ Exception -> 0x0490 }
        if (r13 == 0) goto L_0x00a0;
    L_0x0097:
        r13 = r7.cdnHashes;	 Catch:{ Exception -> 0x0490 }
        r13 = r13.get(r11);	 Catch:{ Exception -> 0x0490 }
        r13 = (org.telegram.tgnet.TLRPC.TL_fileHash) r13;	 Catch:{ Exception -> 0x0490 }
        goto L_0x00a1;
    L_0x00a0:
        r13 = r10;
    L_0x00a1:
        if (r13 != 0) goto L_0x00aa;
    L_0x00a3:
        r21.delayRequestInfo(r22);	 Catch:{ Exception -> 0x0490 }
        r7.requestFileOffsets(r11);	 Catch:{ Exception -> 0x0490 }
        return r8;
    L_0x00aa:
        r11 = r22.responseCdn;	 Catch:{ Exception -> 0x0490 }
        r13 = 12;
        if (r11 == 0) goto L_0x00ed;
    L_0x00b2:
        r11 = r22.offset;	 Catch:{ Exception -> 0x0490 }
        r11 = r11 / 16;
        r14 = r7.cdnIv;	 Catch:{ Exception -> 0x0490 }
        r15 = 15;
        r12 = r11 & 255;
        r12 = (byte) r12;	 Catch:{ Exception -> 0x0490 }
        r14[r15] = r12;	 Catch:{ Exception -> 0x0490 }
        r12 = r7.cdnIv;	 Catch:{ Exception -> 0x0490 }
        r14 = 14;
        r15 = r11 >> 8;
        r15 = r15 & 255;
        r15 = (byte) r15;	 Catch:{ Exception -> 0x0490 }
        r12[r14] = r15;	 Catch:{ Exception -> 0x0490 }
        r12 = r7.cdnIv;	 Catch:{ Exception -> 0x0490 }
        r14 = 13;
        r15 = r11 >> 16;
        r15 = r15 & 255;
        r15 = (byte) r15;	 Catch:{ Exception -> 0x0490 }
        r12[r14] = r15;	 Catch:{ Exception -> 0x0490 }
        r12 = r7.cdnIv;	 Catch:{ Exception -> 0x0490 }
        r11 = r11 >> 24;
        r11 = r11 & 255;
        r11 = (byte) r11;	 Catch:{ Exception -> 0x0490 }
        r12[r13] = r11;	 Catch:{ Exception -> 0x0490 }
        r11 = r0.buffer;	 Catch:{ Exception -> 0x0490 }
        r12 = r7.cdnKey;	 Catch:{ Exception -> 0x0490 }
        r14 = r7.cdnIv;	 Catch:{ Exception -> 0x0490 }
        r15 = r0.limit();	 Catch:{ Exception -> 0x0490 }
        org.telegram.messenger.Utilities.aesCtrDecryption(r11, r12, r14, r9, r15);	 Catch:{ Exception -> 0x0490 }
    L_0x00ed:
        r11 = r7.isPreloadVideoOperation;	 Catch:{ Exception -> 0x0490 }
        if (r11 == 0) goto L_0x01f6;
    L_0x00f1:
        r1 = r7.preloadStream;	 Catch:{ Exception -> 0x0490 }
        r5 = r22.offset;	 Catch:{ Exception -> 0x0490 }
        r1.writeInt(r5);	 Catch:{ Exception -> 0x0490 }
        r1 = r7.preloadStream;	 Catch:{ Exception -> 0x0490 }
        r1.writeInt(r6);	 Catch:{ Exception -> 0x0490 }
        r1 = r7.preloadStreamFileOffset;	 Catch:{ Exception -> 0x0490 }
        r1 = r1 + 8;
        r7.preloadStreamFileOffset = r1;	 Catch:{ Exception -> 0x0490 }
        r1 = r7.preloadStream;	 Catch:{ Exception -> 0x0490 }
        r1 = r1.getChannel();	 Catch:{ Exception -> 0x0490 }
        r5 = r0.buffer;	 Catch:{ Exception -> 0x0490 }
        r1.write(r5);	 Catch:{ Exception -> 0x0490 }
        r1 = org.telegram.messenger.BuildVars.DEBUG_VERSION;	 Catch:{ Exception -> 0x0490 }
        if (r1 == 0) goto L_0x013c;
    L_0x0114:
        r1 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0490 }
        r1.<init>();	 Catch:{ Exception -> 0x0490 }
        r5 = "save preload file part ";
        r1.append(r5);	 Catch:{ Exception -> 0x0490 }
        r5 = r7.cacheFilePreload;	 Catch:{ Exception -> 0x0490 }
        r1.append(r5);	 Catch:{ Exception -> 0x0490 }
        r1.append(r2);	 Catch:{ Exception -> 0x0490 }
        r2 = r22.offset;	 Catch:{ Exception -> 0x0490 }
        r1.append(r2);	 Catch:{ Exception -> 0x0490 }
        r2 = " size ";
        r1.append(r2);	 Catch:{ Exception -> 0x0490 }
        r1.append(r6);	 Catch:{ Exception -> 0x0490 }
        r1 = r1.toString();	 Catch:{ Exception -> 0x0490 }
        org.telegram.messenger.FileLog.d(r1);	 Catch:{ Exception -> 0x0490 }
    L_0x013c:
        r1 = r7.preloadedBytesRanges;	 Catch:{ Exception -> 0x0490 }
        if (r1 != 0) goto L_0x0147;
    L_0x0140:
        r1 = new android.util.SparseArray;	 Catch:{ Exception -> 0x0490 }
        r1.<init>();	 Catch:{ Exception -> 0x0490 }
        r7.preloadedBytesRanges = r1;	 Catch:{ Exception -> 0x0490 }
    L_0x0147:
        r1 = r7.preloadedBytesRanges;	 Catch:{ Exception -> 0x0490 }
        r2 = r22.offset;	 Catch:{ Exception -> 0x0490 }
        r5 = new org.telegram.messenger.FileLoadOperation$PreloadRange;	 Catch:{ Exception -> 0x0490 }
        r11 = r7.preloadStreamFileOffset;	 Catch:{ Exception -> 0x0490 }
        r5.<init>(r11, r6);	 Catch:{ Exception -> 0x0490 }
        r1.put(r2, r5);	 Catch:{ Exception -> 0x0490 }
        r1 = r7.totalPreloadedBytes;	 Catch:{ Exception -> 0x0490 }
        r1 = r1 + r6;
        r7.totalPreloadedBytes = r1;	 Catch:{ Exception -> 0x0490 }
        r1 = r7.preloadStreamFileOffset;	 Catch:{ Exception -> 0x0490 }
        r1 = r1 + r6;
        r7.preloadStreamFileOffset = r1;	 Catch:{ Exception -> 0x0490 }
        r1 = r7.moovFound;	 Catch:{ Exception -> 0x0490 }
        if (r1 != 0) goto L_0x01a3;
    L_0x0165:
        r1 = r7.nextAtomOffset;	 Catch:{ Exception -> 0x0490 }
        r2 = r22.offset;	 Catch:{ Exception -> 0x0490 }
        r0 = r7.findNextPreloadDownloadOffset(r1, r2, r0);	 Catch:{ Exception -> 0x0490 }
        if (r0 >= 0) goto L_0x0197;
    L_0x0171:
        r0 = r0 * -1;
        r1 = r7.nextPreloadDownloadOffset;	 Catch:{ Exception -> 0x0490 }
        r2 = r7.currentDownloadChunkSize;	 Catch:{ Exception -> 0x0490 }
        r1 = r1 + r2;
        r7.nextPreloadDownloadOffset = r1;	 Catch:{ Exception -> 0x0490 }
        r1 = r7.nextPreloadDownloadOffset;	 Catch:{ Exception -> 0x0490 }
        r2 = r7.totalBytesCount;	 Catch:{ Exception -> 0x0490 }
        r2 = r2 / r4;
        if (r1 >= r2) goto L_0x018b;
    L_0x0181:
        r1 = 1048576; // 0x100000 float:1.469368E-39 double:5.180654E-318;
        r1 = r1 + r0;
        r7.foundMoovSize = r1;	 Catch:{ Exception -> 0x0490 }
        r7.preloadNotRequestedBytesCount = r1;	 Catch:{ Exception -> 0x0490 }
        r7.moovFound = r8;	 Catch:{ Exception -> 0x0490 }
        goto L_0x0193;
    L_0x018b:
        r1 = 2097152; // 0x200000 float:2.938736E-39 double:1.0361308E-317;
        r7.foundMoovSize = r1;	 Catch:{ Exception -> 0x0490 }
        r7.preloadNotRequestedBytesCount = r1;	 Catch:{ Exception -> 0x0490 }
        r7.moovFound = r4;	 Catch:{ Exception -> 0x0490 }
    L_0x0193:
        r1 = -1;
        r7.nextPreloadDownloadOffset = r1;	 Catch:{ Exception -> 0x0490 }
        goto L_0x01a1;
    L_0x0197:
        r1 = r7.currentDownloadChunkSize;	 Catch:{ Exception -> 0x0490 }
        r1 = r0 / r1;
        r2 = r7.currentDownloadChunkSize;	 Catch:{ Exception -> 0x0490 }
        r1 = r1 * r2;
        r7.nextPreloadDownloadOffset = r1;	 Catch:{ Exception -> 0x0490 }
    L_0x01a1:
        r7.nextAtomOffset = r0;	 Catch:{ Exception -> 0x0490 }
    L_0x01a3:
        r0 = r7.preloadStream;	 Catch:{ Exception -> 0x0490 }
        r1 = r7.foundMoovSize;	 Catch:{ Exception -> 0x0490 }
        r0.writeInt(r1);	 Catch:{ Exception -> 0x0490 }
        r0 = r7.preloadStream;	 Catch:{ Exception -> 0x0490 }
        r1 = r7.nextPreloadDownloadOffset;	 Catch:{ Exception -> 0x0490 }
        r0.writeInt(r1);	 Catch:{ Exception -> 0x0490 }
        r0 = r7.preloadStream;	 Catch:{ Exception -> 0x0490 }
        r1 = r7.nextAtomOffset;	 Catch:{ Exception -> 0x0490 }
        r0.writeInt(r1);	 Catch:{ Exception -> 0x0490 }
        r0 = r7.preloadStreamFileOffset;	 Catch:{ Exception -> 0x0490 }
        r0 = r0 + r13;
        r7.preloadStreamFileOffset = r0;	 Catch:{ Exception -> 0x0490 }
        r0 = r7.nextPreloadDownloadOffset;	 Catch:{ Exception -> 0x0490 }
        if (r0 == 0) goto L_0x01d8;
    L_0x01c1:
        r0 = r7.moovFound;	 Catch:{ Exception -> 0x0490 }
        if (r0 == 0) goto L_0x01c9;
    L_0x01c5:
        r0 = r7.foundMoovSize;	 Catch:{ Exception -> 0x0490 }
        if (r0 < 0) goto L_0x01d8;
    L_0x01c9:
        r0 = r7.totalPreloadedBytes;	 Catch:{ Exception -> 0x0490 }
        r1 = 2097152; // 0x200000 float:2.938736E-39 double:1.0361308E-317;
        if (r0 > r1) goto L_0x01d8;
    L_0x01cf:
        r0 = r7.nextPreloadDownloadOffset;	 Catch:{ Exception -> 0x0490 }
        r1 = r7.totalBytesCount;	 Catch:{ Exception -> 0x0490 }
        if (r0 < r1) goto L_0x01d6;
    L_0x01d5:
        goto L_0x01d8;
    L_0x01d6:
        r0 = 0;
        goto L_0x01d9;
    L_0x01d8:
        r0 = 1;
    L_0x01d9:
        if (r0 == 0) goto L_0x01e9;
    L_0x01db:
        r1 = r7.preloadStream;	 Catch:{ Exception -> 0x0490 }
        r2 = 0;
        r1.seek(r2);	 Catch:{ Exception -> 0x0490 }
        r1 = r7.preloadStream;	 Catch:{ Exception -> 0x0490 }
        r1.write(r8);	 Catch:{ Exception -> 0x0490 }
        goto L_0x0419;
    L_0x01e9:
        r1 = r7.moovFound;	 Catch:{ Exception -> 0x0490 }
        if (r1 == 0) goto L_0x0419;
    L_0x01ed:
        r1 = r7.foundMoovSize;	 Catch:{ Exception -> 0x0490 }
        r2 = r7.currentDownloadChunkSize;	 Catch:{ Exception -> 0x0490 }
        r1 = r1 - r2;
        r7.foundMoovSize = r1;	 Catch:{ Exception -> 0x0490 }
        goto L_0x0419;
    L_0x01f6:
        r4 = r7.downloadedBytes;	 Catch:{ Exception -> 0x0490 }
        r4 = r4 + r6;
        r7.downloadedBytes = r4;	 Catch:{ Exception -> 0x0490 }
        r4 = r7.totalBytesCount;	 Catch:{ Exception -> 0x0490 }
        if (r4 <= 0) goto L_0x0206;
    L_0x01ff:
        r4 = r7.downloadedBytes;	 Catch:{ Exception -> 0x0490 }
        r11 = r7.totalBytesCount;	 Catch:{ Exception -> 0x0490 }
        if (r4 < r11) goto L_0x0222;
    L_0x0205:
        goto L_0x0224;
    L_0x0206:
        r4 = r7.currentDownloadChunkSize;	 Catch:{ Exception -> 0x0490 }
        if (r6 != r4) goto L_0x0224;
    L_0x020a:
        r4 = r7.totalBytesCount;	 Catch:{ Exception -> 0x0490 }
        r11 = r7.downloadedBytes;	 Catch:{ Exception -> 0x0490 }
        if (r4 == r11) goto L_0x0217;
    L_0x0210:
        r4 = r7.downloadedBytes;	 Catch:{ Exception -> 0x0490 }
        r11 = r7.currentDownloadChunkSize;	 Catch:{ Exception -> 0x0490 }
        r4 = r4 % r11;
        if (r4 == 0) goto L_0x0222;
    L_0x0217:
        r4 = r7.totalBytesCount;	 Catch:{ Exception -> 0x0490 }
        if (r4 <= 0) goto L_0x0224;
    L_0x021b:
        r4 = r7.totalBytesCount;	 Catch:{ Exception -> 0x0490 }
        r11 = r7.downloadedBytes;	 Catch:{ Exception -> 0x0490 }
        if (r4 > r11) goto L_0x0222;
    L_0x0221:
        goto L_0x0224;
    L_0x0222:
        r4 = 0;
        goto L_0x0225;
    L_0x0224:
        r4 = 1;
    L_0x0225:
        r11 = r4;
        r4 = r7.key;	 Catch:{ Exception -> 0x0490 }
        if (r4 == 0) goto L_0x024f;
    L_0x022a:
        r14 = r0.buffer;	 Catch:{ Exception -> 0x0490 }
        r15 = r7.key;	 Catch:{ Exception -> 0x0490 }
        r4 = r7.iv;	 Catch:{ Exception -> 0x0490 }
        r17 = 0;
        r18 = 1;
        r19 = 0;
        r20 = r0.limit();	 Catch:{ Exception -> 0x0490 }
        r16 = r4;
        org.telegram.messenger.Utilities.aesIgeEncryption(r14, r15, r16, r17, r18, r19, r20);	 Catch:{ Exception -> 0x0490 }
        if (r11 == 0) goto L_0x024f;
    L_0x0241:
        r4 = r7.bytesCountPadding;	 Catch:{ Exception -> 0x0490 }
        if (r4 == 0) goto L_0x024f;
    L_0x0245:
        r4 = r0.limit();	 Catch:{ Exception -> 0x0490 }
        r12 = r7.bytesCountPadding;	 Catch:{ Exception -> 0x0490 }
        r4 = r4 - r12;
        r0.limit(r4);	 Catch:{ Exception -> 0x0490 }
    L_0x024f:
        r4 = r7.encryptFile;	 Catch:{ Exception -> 0x0490 }
        if (r4 == 0) goto L_0x028e;
    L_0x0253:
        r4 = r22.offset;	 Catch:{ Exception -> 0x0490 }
        r4 = r4 / 16;
        r12 = r7.encryptIv;	 Catch:{ Exception -> 0x0490 }
        r14 = 15;
        r15 = r4 & 255;
        r15 = (byte) r15;	 Catch:{ Exception -> 0x0490 }
        r12[r14] = r15;	 Catch:{ Exception -> 0x0490 }
        r12 = r7.encryptIv;	 Catch:{ Exception -> 0x0490 }
        r14 = 14;
        r15 = r4 >> 8;
        r15 = r15 & 255;
        r15 = (byte) r15;	 Catch:{ Exception -> 0x0490 }
        r12[r14] = r15;	 Catch:{ Exception -> 0x0490 }
        r12 = r7.encryptIv;	 Catch:{ Exception -> 0x0490 }
        r14 = 13;
        r15 = r4 >> 16;
        r15 = r15 & 255;
        r15 = (byte) r15;	 Catch:{ Exception -> 0x0490 }
        r12[r14] = r15;	 Catch:{ Exception -> 0x0490 }
        r12 = r7.encryptIv;	 Catch:{ Exception -> 0x0490 }
        r4 = r4 >> 24;
        r4 = r4 & 255;
        r4 = (byte) r4;	 Catch:{ Exception -> 0x0490 }
        r12[r13] = r4;	 Catch:{ Exception -> 0x0490 }
        r4 = r0.buffer;	 Catch:{ Exception -> 0x0490 }
        r12 = r7.encryptKey;	 Catch:{ Exception -> 0x0490 }
        r13 = r7.encryptIv;	 Catch:{ Exception -> 0x0490 }
        r14 = r0.limit();	 Catch:{ Exception -> 0x0490 }
        org.telegram.messenger.Utilities.aesCtrDecryption(r4, r12, r13, r9, r14);	 Catch:{ Exception -> 0x0490 }
    L_0x028e:
        r4 = r7.notLoadedBytesRanges;	 Catch:{ Exception -> 0x0490 }
        if (r4 == 0) goto L_0x02c0;
    L_0x0292:
        r4 = r7.fileOutputStream;	 Catch:{ Exception -> 0x0490 }
        r12 = r22.offset;	 Catch:{ Exception -> 0x0490 }
        r12 = (long) r12;	 Catch:{ Exception -> 0x0490 }
        r4.seek(r12);	 Catch:{ Exception -> 0x0490 }
        r4 = org.telegram.messenger.BuildVars.DEBUG_VERSION;	 Catch:{ Exception -> 0x0490 }
        if (r4 == 0) goto L_0x02c0;
    L_0x02a0:
        r4 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0490 }
        r4.<init>();	 Catch:{ Exception -> 0x0490 }
        r12 = "save file part ";
        r4.append(r12);	 Catch:{ Exception -> 0x0490 }
        r12 = r7.cacheFileFinal;	 Catch:{ Exception -> 0x0490 }
        r4.append(r12);	 Catch:{ Exception -> 0x0490 }
        r4.append(r2);	 Catch:{ Exception -> 0x0490 }
        r2 = r22.offset;	 Catch:{ Exception -> 0x0490 }
        r4.append(r2);	 Catch:{ Exception -> 0x0490 }
        r2 = r4.toString();	 Catch:{ Exception -> 0x0490 }
        org.telegram.messenger.FileLog.d(r2);	 Catch:{ Exception -> 0x0490 }
    L_0x02c0:
        r2 = r7.fileOutputStream;	 Catch:{ Exception -> 0x0490 }
        r2 = r2.getChannel();	 Catch:{ Exception -> 0x0490 }
        r0 = r0.buffer;	 Catch:{ Exception -> 0x0490 }
        r2.write(r0);	 Catch:{ Exception -> 0x0490 }
        r0 = r7.notLoadedBytesRanges;	 Catch:{ Exception -> 0x0490 }
        r2 = r22.offset;	 Catch:{ Exception -> 0x0490 }
        r4 = r22.offset;	 Catch:{ Exception -> 0x0490 }
        r4 = r4 + r6;
        r7.addPart(r0, r2, r4, r8);	 Catch:{ Exception -> 0x0490 }
        r0 = r7.isCdn;	 Catch:{ Exception -> 0x0490 }
        if (r0 == 0) goto L_0x03ee;
    L_0x02dd:
        r0 = r22.offset;	 Catch:{ Exception -> 0x0490 }
        r2 = 131072; // 0x20000 float:1.83671E-40 double:6.47582E-319;
        r0 = r0 / r2;
        r2 = r7.notCheckedCdnRanges;	 Catch:{ Exception -> 0x0490 }
        r2 = r2.size();	 Catch:{ Exception -> 0x0490 }
        r3 = 0;
    L_0x02eb:
        if (r3 >= r2) goto L_0x0306;
    L_0x02ed:
        r4 = r7.notCheckedCdnRanges;	 Catch:{ Exception -> 0x0490 }
        r4 = r4.get(r3);	 Catch:{ Exception -> 0x0490 }
        r4 = (org.telegram.messenger.FileLoadOperation.Range) r4;	 Catch:{ Exception -> 0x0490 }
        r6 = r4.start;	 Catch:{ Exception -> 0x0490 }
        if (r6 > r0) goto L_0x0303;
    L_0x02fb:
        r4 = r4.end;	 Catch:{ Exception -> 0x0490 }
        if (r0 > r4) goto L_0x0303;
    L_0x0301:
        r2 = 0;
        goto L_0x0307;
    L_0x0303:
        r3 = r3 + 1;
        goto L_0x02eb;
    L_0x0306:
        r2 = 1;
    L_0x0307:
        if (r2 != 0) goto L_0x03ee;
    L_0x0309:
        r2 = 131072; // 0x20000 float:1.83671E-40 double:6.47582E-319;
        r12 = r0 * r2;
        r3 = r7.notLoadedBytesRanges;	 Catch:{ Exception -> 0x0490 }
        r3 = r7.getDownloadedLengthFromOffsetInternal(r3, r12, r2);	 Catch:{ Exception -> 0x0490 }
        if (r3 == 0) goto L_0x03ee;
    L_0x0315:
        if (r3 == r2) goto L_0x0326;
    L_0x0317:
        r2 = r7.totalBytesCount;	 Catch:{ Exception -> 0x0490 }
        if (r2 <= 0) goto L_0x0320;
    L_0x031b:
        r2 = r7.totalBytesCount;	 Catch:{ Exception -> 0x0490 }
        r2 = r2 - r12;
        if (r3 == r2) goto L_0x0326;
    L_0x0320:
        r2 = r7.totalBytesCount;	 Catch:{ Exception -> 0x0490 }
        if (r2 > 0) goto L_0x03ee;
    L_0x0324:
        if (r11 == 0) goto L_0x03ee;
    L_0x0326:
        r2 = r7.cdnHashes;	 Catch:{ Exception -> 0x0490 }
        r2 = r2.get(r12);	 Catch:{ Exception -> 0x0490 }
        r2 = (org.telegram.tgnet.TLRPC.TL_fileHash) r2;	 Catch:{ Exception -> 0x0490 }
        r4 = r7.fileReadStream;	 Catch:{ Exception -> 0x0490 }
        if (r4 != 0) goto L_0x0343;
    L_0x0332:
        r4 = 131072; // 0x20000 float:1.83671E-40 double:6.47582E-319;
        r4 = new byte[r4];	 Catch:{ Exception -> 0x0490 }
        r7.cdnCheckBytes = r4;	 Catch:{ Exception -> 0x0490 }
        r4 = new java.io.RandomAccessFile;	 Catch:{ Exception -> 0x0490 }
        r6 = r7.cacheFileTemp;	 Catch:{ Exception -> 0x0490 }
        r13 = "r";
        r4.<init>(r6, r13);	 Catch:{ Exception -> 0x0490 }
        r7.fileReadStream = r4;	 Catch:{ Exception -> 0x0490 }
    L_0x0343:
        r4 = r7.fileReadStream;	 Catch:{ Exception -> 0x0490 }
        r13 = (long) r12;	 Catch:{ Exception -> 0x0490 }
        r4.seek(r13);	 Catch:{ Exception -> 0x0490 }
        r4 = r7.fileReadStream;	 Catch:{ Exception -> 0x0490 }
        r6 = r7.cdnCheckBytes;	 Catch:{ Exception -> 0x0490 }
        r4.readFully(r6, r9, r3);	 Catch:{ Exception -> 0x0490 }
        r4 = r7.cdnCheckBytes;	 Catch:{ Exception -> 0x0490 }
        r3 = org.telegram.messenger.Utilities.computeSHA256(r4, r9, r3);	 Catch:{ Exception -> 0x0490 }
        r2 = r2.hash;	 Catch:{ Exception -> 0x0490 }
        r2 = java.util.Arrays.equals(r3, r2);	 Catch:{ Exception -> 0x0490 }
        if (r2 != 0) goto L_0x03e2;
    L_0x035e:
        r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Exception -> 0x0490 }
        if (r0 == 0) goto L_0x03d9;
    L_0x0362:
        r0 = r7.location;	 Catch:{ Exception -> 0x0490 }
        if (r0 == 0) goto L_0x03b5;
    L_0x0366:
        r0 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0490 }
        r0.<init>();	 Catch:{ Exception -> 0x0490 }
        r2 = "invalid cdn hash ";
        r0.append(r2);	 Catch:{ Exception -> 0x0490 }
        r2 = r7.location;	 Catch:{ Exception -> 0x0490 }
        r0.append(r2);	 Catch:{ Exception -> 0x0490 }
        r0.append(r5);	 Catch:{ Exception -> 0x0490 }
        r2 = r7.location;	 Catch:{ Exception -> 0x0490 }
        r2 = r2.id;	 Catch:{ Exception -> 0x0490 }
        r0.append(r2);	 Catch:{ Exception -> 0x0490 }
        r0.append(r1);	 Catch:{ Exception -> 0x0490 }
        r1 = r7.location;	 Catch:{ Exception -> 0x0490 }
        r1 = r1.local_id;	 Catch:{ Exception -> 0x0490 }
        r0.append(r1);	 Catch:{ Exception -> 0x0490 }
        r1 = " access_hash = ";
        r0.append(r1);	 Catch:{ Exception -> 0x0490 }
        r1 = r7.location;	 Catch:{ Exception -> 0x0490 }
        r1 = r1.access_hash;	 Catch:{ Exception -> 0x0490 }
        r0.append(r1);	 Catch:{ Exception -> 0x0490 }
        r1 = " volume_id = ";
        r0.append(r1);	 Catch:{ Exception -> 0x0490 }
        r1 = r7.location;	 Catch:{ Exception -> 0x0490 }
        r1 = r1.volume_id;	 Catch:{ Exception -> 0x0490 }
        r0.append(r1);	 Catch:{ Exception -> 0x0490 }
        r1 = " secret = ";
        r0.append(r1);	 Catch:{ Exception -> 0x0490 }
        r1 = r7.location;	 Catch:{ Exception -> 0x0490 }
        r1 = r1.secret;	 Catch:{ Exception -> 0x0490 }
        r0.append(r1);	 Catch:{ Exception -> 0x0490 }
        r0 = r0.toString();	 Catch:{ Exception -> 0x0490 }
        org.telegram.messenger.FileLog.e(r0);	 Catch:{ Exception -> 0x0490 }
        goto L_0x03d9;
    L_0x03b5:
        r0 = r7.webLocation;	 Catch:{ Exception -> 0x0490 }
        if (r0 == 0) goto L_0x03d9;
    L_0x03b9:
        r0 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0490 }
        r0.<init>();	 Catch:{ Exception -> 0x0490 }
        r1 = "invalid cdn hash  ";
        r0.append(r1);	 Catch:{ Exception -> 0x0490 }
        r1 = r7.webLocation;	 Catch:{ Exception -> 0x0490 }
        r0.append(r1);	 Catch:{ Exception -> 0x0490 }
        r0.append(r5);	 Catch:{ Exception -> 0x0490 }
        r1 = r21.getFileName();	 Catch:{ Exception -> 0x0490 }
        r0.append(r1);	 Catch:{ Exception -> 0x0490 }
        r0 = r0.toString();	 Catch:{ Exception -> 0x0490 }
        org.telegram.messenger.FileLog.e(r0);	 Catch:{ Exception -> 0x0490 }
    L_0x03d9:
        r7.onFail(r9, r9);	 Catch:{ Exception -> 0x0490 }
        r0 = r7.cacheFileTemp;	 Catch:{ Exception -> 0x0490 }
        r0.delete();	 Catch:{ Exception -> 0x0490 }
        return r9;
    L_0x03e2:
        r1 = r7.cdnHashes;	 Catch:{ Exception -> 0x0490 }
        r1.remove(r12);	 Catch:{ Exception -> 0x0490 }
        r1 = r7.notCheckedCdnRanges;	 Catch:{ Exception -> 0x0490 }
        r2 = r0 + 1;
        r7.addPart(r1, r0, r2, r9);	 Catch:{ Exception -> 0x0490 }
    L_0x03ee:
        r0 = r7.fiv;	 Catch:{ Exception -> 0x0490 }
        if (r0 == 0) goto L_0x0400;
    L_0x03f2:
        r0 = r7.fiv;	 Catch:{ Exception -> 0x0490 }
        r1 = 0;
        r0.seek(r1);	 Catch:{ Exception -> 0x0490 }
        r0 = r7.fiv;	 Catch:{ Exception -> 0x0490 }
        r1 = r7.iv;	 Catch:{ Exception -> 0x0490 }
        r0.write(r1);	 Catch:{ Exception -> 0x0490 }
    L_0x0400:
        r0 = r7.totalBytesCount;	 Catch:{ Exception -> 0x0490 }
        if (r0 <= 0) goto L_0x0418;
    L_0x0404:
        r0 = r7.state;	 Catch:{ Exception -> 0x0490 }
        if (r0 != r8) goto L_0x0418;
    L_0x0408:
        r21.copyNotLoadedRanges();	 Catch:{ Exception -> 0x0490 }
        r1 = r7.delegate;	 Catch:{ Exception -> 0x0490 }
        r0 = r7.downloadedBytes;	 Catch:{ Exception -> 0x0490 }
        r3 = (long) r0;	 Catch:{ Exception -> 0x0490 }
        r0 = r7.totalBytesCount;	 Catch:{ Exception -> 0x0490 }
        r5 = (long) r0;	 Catch:{ Exception -> 0x0490 }
        r2 = r21;
        r1.didChangedLoadProgress(r2, r3, r5);	 Catch:{ Exception -> 0x0490 }
    L_0x0418:
        r0 = r11;
    L_0x0419:
        r1 = 0;
    L_0x041a:
        r2 = r7.delayedRequestInfos;	 Catch:{ Exception -> 0x0490 }
        r2 = r2.size();	 Catch:{ Exception -> 0x0490 }
        if (r1 >= r2) goto L_0x0480;
    L_0x0422:
        r2 = r7.delayedRequestInfos;	 Catch:{ Exception -> 0x0490 }
        r2 = r2.get(r1);	 Catch:{ Exception -> 0x0490 }
        r2 = (org.telegram.messenger.FileLoadOperation.RequestInfo) r2;	 Catch:{ Exception -> 0x0490 }
        r3 = r7.notLoadedBytesRanges;	 Catch:{ Exception -> 0x0490 }
        if (r3 != 0) goto L_0x043a;
    L_0x042e:
        r3 = r7.downloadedBytes;	 Catch:{ Exception -> 0x0490 }
        r4 = r2.offset;	 Catch:{ Exception -> 0x0490 }
        if (r3 != r4) goto L_0x0437;
    L_0x0436:
        goto L_0x043a;
    L_0x0437:
        r1 = r1 + 1;
        goto L_0x041a;
    L_0x043a:
        r3 = r7.delayedRequestInfos;	 Catch:{ Exception -> 0x0490 }
        r3.remove(r1);	 Catch:{ Exception -> 0x0490 }
        r1 = r7.processRequestResult(r2, r10);	 Catch:{ Exception -> 0x0490 }
        if (r1 != 0) goto L_0x0480;
    L_0x0445:
        r1 = r2.response;	 Catch:{ Exception -> 0x0490 }
        if (r1 == 0) goto L_0x0459;
    L_0x044b:
        r1 = r2.response;	 Catch:{ Exception -> 0x0490 }
        r1.disableFree = r9;	 Catch:{ Exception -> 0x0490 }
        r1 = r2.response;	 Catch:{ Exception -> 0x0490 }
        r1.freeResources();	 Catch:{ Exception -> 0x0490 }
        goto L_0x0480;
    L_0x0459:
        r1 = r2.responseWeb;	 Catch:{ Exception -> 0x0490 }
        if (r1 == 0) goto L_0x046d;
    L_0x045f:
        r1 = r2.responseWeb;	 Catch:{ Exception -> 0x0490 }
        r1.disableFree = r9;	 Catch:{ Exception -> 0x0490 }
        r1 = r2.responseWeb;	 Catch:{ Exception -> 0x0490 }
        r1.freeResources();	 Catch:{ Exception -> 0x0490 }
        goto L_0x0480;
    L_0x046d:
        r1 = r2.responseCdn;	 Catch:{ Exception -> 0x0490 }
        if (r1 == 0) goto L_0x0480;
    L_0x0473:
        r1 = r2.responseCdn;	 Catch:{ Exception -> 0x0490 }
        r1.disableFree = r9;	 Catch:{ Exception -> 0x0490 }
        r1 = r2.responseCdn;	 Catch:{ Exception -> 0x0490 }
        r1.freeResources();	 Catch:{ Exception -> 0x0490 }
    L_0x0480:
        if (r0 == 0) goto L_0x0487;
    L_0x0482:
        r7.onFinishLoadingFile(r8);	 Catch:{ Exception -> 0x0490 }
        goto L_0x058d;
    L_0x0487:
        r21.startDownloadRequest();	 Catch:{ Exception -> 0x0490 }
        goto L_0x058d;
    L_0x048c:
        r7.onFinishLoadingFile(r8);	 Catch:{ Exception -> 0x0490 }
        return r9;
    L_0x0490:
        r0 = move-exception;
        r7.onFail(r9, r9);
        org.telegram.messenger.FileLog.e(r0);
        goto L_0x058d;
    L_0x0499:
        r2 = r0.text;
        r3 = "FILE_MIGRATE_";
        r2 = r2.contains(r3);
        if (r2 == 0) goto L_0x04d3;
    L_0x04a3:
        r0 = r0.text;
        r1 = "";
        r0 = r0.replace(r3, r1);
        r2 = new java.util.Scanner;
        r2.<init>(r0);
        r2.useDelimiter(r1);
        r0 = r2.nextInt();	 Catch:{ Exception -> 0x04bc }
        r10 = java.lang.Integer.valueOf(r0);	 Catch:{ Exception -> 0x04bc }
        goto L_0x04bd;
    L_0x04bd:
        if (r10 != 0) goto L_0x04c4;
    L_0x04bf:
        r7.onFail(r9, r9);
        goto L_0x058d;
    L_0x04c4:
        r0 = r10.intValue();
        r7.datacenterId = r0;
        r7.downloadedBytes = r9;
        r7.requestedBytesCount = r9;
        r21.startDownloadRequest();
        goto L_0x058d;
    L_0x04d3:
        r2 = r0.text;
        r3 = "OFFSET_INVALID";
        r2 = r2.contains(r3);
        if (r2 == 0) goto L_0x04f8;
    L_0x04dd:
        r0 = r7.downloadedBytes;
        r1 = r7.currentDownloadChunkSize;
        r0 = r0 % r1;
        if (r0 != 0) goto L_0x04f3;
    L_0x04e4:
        r7.onFinishLoadingFile(r8);	 Catch:{ Exception -> 0x04e9 }
        goto L_0x058d;
    L_0x04e9:
        r0 = move-exception;
        r1 = r0;
        org.telegram.messenger.FileLog.e(r1);
        r7.onFail(r9, r9);
        goto L_0x058d;
    L_0x04f3:
        r7.onFail(r9, r9);
        goto L_0x058d;
    L_0x04f8:
        r2 = r0.text;
        r3 = "RETRY_LIMIT";
        r2 = r2.contains(r3);
        if (r2 == 0) goto L_0x0507;
    L_0x0502:
        r7.onFail(r9, r4);
        goto L_0x058d;
    L_0x0507:
        r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r2 == 0) goto L_0x058a;
    L_0x050b:
        r2 = r7.location;
        r3 = " ";
        if (r2 == 0) goto L_0x0563;
    L_0x0511:
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r0 = r0.text;
        r2.append(r0);
        r2.append(r3);
        r0 = r7.location;
        r2.append(r0);
        r2.append(r5);
        r0 = r7.location;
        r3 = r0.id;
        r2.append(r3);
        r2.append(r1);
        r0 = r7.location;
        r0 = r0.local_id;
        r2.append(r0);
        r0 = " access_hash = ";
        r2.append(r0);
        r0 = r7.location;
        r0 = r0.access_hash;
        r2.append(r0);
        r0 = " volume_id = ";
        r2.append(r0);
        r0 = r7.location;
        r0 = r0.volume_id;
        r2.append(r0);
        r0 = " secret = ";
        r2.append(r0);
        r0 = r7.location;
        r0 = r0.secret;
        r2.append(r0);
        r0 = r2.toString();
        org.telegram.messenger.FileLog.e(r0);
        goto L_0x058a;
    L_0x0563:
        r1 = r7.webLocation;
        if (r1 == 0) goto L_0x058a;
    L_0x0567:
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r0 = r0.text;
        r1.append(r0);
        r1.append(r3);
        r0 = r7.webLocation;
        r1.append(r0);
        r1.append(r5);
        r0 = r21.getFileName();
        r1.append(r0);
        r0 = r1.toString();
        org.telegram.messenger.FileLog.e(r0);
    L_0x058a:
        r7.onFail(r9, r9);
    L_0x058d:
        return r9;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.FileLoadOperation.processRequestResult(org.telegram.messenger.FileLoadOperation$RequestInfo, org.telegram.tgnet.TLRPC$TL_error):boolean");
    }

    /* Access modifiers changed, original: protected */
    public void onFail(boolean z, int i) {
        cleanup();
        this.state = 2;
        if (z) {
            Utilities.stageQueue.postRunnable(new -$$Lambda$FileLoadOperation$pXgjdvEV2cEy0KoNhcKFGIwQAyM(this, i));
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
            RequestInfo requestInfo2 = (RequestInfo) this.requestInfos.get(i2);
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
            RequestInfo requestInfo3 = (RequestInfo) this.delayedRequestInfos.get(i3);
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
        if (!this.requestingReference) {
            clearOperaion(requestInfo, false);
            this.requestingReference = true;
            Object obj = this.parentObject;
            if (obj instanceof MessageObject) {
                MessageObject messageObject = (MessageObject) obj;
                if (messageObject.getId() < 0) {
                    WebPage webPage = messageObject.messageOwner.media.webpage;
                    if (webPage != null) {
                        this.parentObject = webPage;
                    }
                }
            }
            FileRefController.getInstance(this.currentAccount).requestReference(this.parentObject, this.location, this, requestInfo);
        }
    }

    /* Access modifiers changed, original: protected */
    public void startDownloadRequest() {
        if (!this.paused && !this.reuploadingCdn && this.state == 1 && (this.streamPriorityStartOffset != 0 || ((this.nextPartWasPreloaded || this.requestInfos.size() + this.delayedRequestInfos.size() < this.currentMaxDownloadRequests) && (!this.isPreloadVideoOperation || (this.requestedBytesCount <= 2097152 && (this.moovFound == 0 || this.requestInfos.size() <= 0)))))) {
            int max = (this.streamPriorityStartOffset != 0 || this.nextPartWasPreloaded || ((this.isPreloadVideoOperation && this.moovFound == 0) || this.totalBytesCount <= 0)) ? 1 : Math.max(0, this.currentMaxDownloadRequests - this.requestInfos.size());
            int i = 0;
            while (i < max) {
                int i2;
                int i3;
                int i4;
                if (this.isPreloadVideoOperation) {
                    if (this.moovFound == 0 || this.preloadNotRequestedBytesCount > 0) {
                        i2 = this.nextPreloadDownloadOffset;
                        if (i2 == -1) {
                            Object obj;
                            i3 = 0;
                            for (i2 = (2097152 / this.currentDownloadChunkSize) + 2; i2 != 0; i2--) {
                                if (this.requestedPreloadedBytesRanges.get(i3, 0) == 0) {
                                    obj = 1;
                                    break;
                                }
                                int i5 = this.currentDownloadChunkSize;
                                i3 += i5;
                                i4 = this.totalBytesCount;
                                if (i3 > i4) {
                                    break;
                                }
                                if (this.moovFound == 2 && i3 == i5 * 8) {
                                    i3 = ((i4 - 1048576) / i5) * i5;
                                }
                            }
                            obj = null;
                            if (obj == null && this.requestInfos.isEmpty()) {
                                onFinishLoadingFile(false);
                            }
                            i2 = i3;
                        }
                        if (this.requestedPreloadedBytesRanges == null) {
                            this.requestedPreloadedBytesRanges = new SparseIntArray();
                        }
                        this.requestedPreloadedBytesRanges.put(i2, 1);
                        if (BuildVars.DEBUG_VERSION) {
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append("start next preload from ");
                            stringBuilder.append(i2);
                            stringBuilder.append(" size ");
                            stringBuilder.append(this.totalBytesCount);
                            stringBuilder.append(" for ");
                            stringBuilder.append(this.cacheFilePreload);
                            FileLog.d(stringBuilder.toString());
                        }
                        this.preloadNotRequestedBytesCount -= this.currentDownloadChunkSize;
                    } else {
                        return;
                    }
                } else if (this.notRequestedBytesRanges != null) {
                    i2 = this.streamPriorityStartOffset;
                    if (i2 == 0) {
                        i2 = this.streamStartOffset;
                    }
                    i3 = this.notRequestedBytesRanges.size();
                    int i6 = Integer.MAX_VALUE;
                    int i7 = Integer.MAX_VALUE;
                    for (i4 = 0; i4 < i3; i4++) {
                        Range range = (Range) this.notRequestedBytesRanges.get(i4);
                        if (i2 != 0) {
                            if (range.start <= i2 && range.end > i2) {
                                i7 = Integer.MAX_VALUE;
                                break;
                            } else if (i2 < range.start && range.start < i6) {
                                i6 = range.start;
                            }
                        }
                        i7 = Math.min(i7, range.start);
                    }
                    i2 = i6;
                    if (i2 == Integer.MAX_VALUE) {
                        if (i7 == Integer.MAX_VALUE) {
                            break;
                        }
                        i2 = i7;
                    }
                } else {
                    i2 = this.requestedBytesCount;
                }
                if (!this.isPreloadVideoOperation) {
                    ArrayList arrayList = this.notRequestedBytesRanges;
                    if (arrayList != null) {
                        addPart(arrayList, i2, this.currentDownloadChunkSize + i2, false);
                    }
                }
                i3 = this.totalBytesCount;
                if (i3 > 0 && i2 >= i3) {
                    break;
                }
                TLObject tL_upload_getCdnFile;
                i3 = this.totalBytesCount;
                boolean z = i3 <= 0 || i == max - 1 || (i3 > 0 && this.currentDownloadChunkSize + i2 >= i3);
                int i8 = this.requestsCount % 2 == 0 ? 2 : 65538;
                int i9 = this.isForceRequest ? 32 : 0;
                if (this.isCdn) {
                    tL_upload_getCdnFile = new TL_upload_getCdnFile();
                    tL_upload_getCdnFile.file_token = this.cdnToken;
                    tL_upload_getCdnFile.offset = i2;
                    tL_upload_getCdnFile.limit = this.currentDownloadChunkSize;
                    i9 |= 1;
                } else if (this.webLocation != null) {
                    tL_upload_getCdnFile = new TL_upload_getWebFile();
                    tL_upload_getCdnFile.location = this.webLocation;
                    tL_upload_getCdnFile.offset = i2;
                    tL_upload_getCdnFile.limit = this.currentDownloadChunkSize;
                } else {
                    tL_upload_getCdnFile = new TL_upload_getFile();
                    tL_upload_getCdnFile.location = this.location;
                    tL_upload_getCdnFile.offset = i2;
                    tL_upload_getCdnFile.limit = this.currentDownloadChunkSize;
                    tL_upload_getCdnFile.cdn_supported = true;
                }
                int i10 = i9;
                TLObject tLObject = tL_upload_getCdnFile;
                this.requestedBytesCount += this.currentDownloadChunkSize;
                RequestInfo requestInfo = new RequestInfo();
                this.requestInfos.add(requestInfo);
                requestInfo.offset = i2;
                if (!(this.isPreloadVideoOperation || !this.supportsPreloading || this.preloadStream == null)) {
                    SparseArray sparseArray = this.preloadedBytesRanges;
                    if (sparseArray != null) {
                        PreloadRange preloadRange = (PreloadRange) sparseArray.get(requestInfo.offset);
                        if (preloadRange != null) {
                            requestInfo.response = new TL_upload_file();
                            try {
                                NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(preloadRange.length);
                                this.preloadStream.seek((long) preloadRange.fileOffset);
                                this.preloadStream.getChannel().read(nativeByteBuffer.buffer);
                                nativeByteBuffer.buffer.position(0);
                                requestInfo.response.bytes = nativeByteBuffer;
                                Utilities.stageQueue.postRunnable(new -$$Lambda$FileLoadOperation$_a8OwTWoM7783M1Mt0pXbFF_RXY(this, requestInfo));
                            } catch (Exception unused) {
                            }
                            i++;
                        }
                    }
                }
                if (this.streamPriorityStartOffset != 0) {
                    if (BuildVars.DEBUG_VERSION) {
                        StringBuilder stringBuilder2 = new StringBuilder();
                        stringBuilder2.append("frame get offset = ");
                        stringBuilder2.append(this.streamPriorityStartOffset);
                        FileLog.d(stringBuilder2.toString());
                    }
                    this.streamPriorityStartOffset = 0;
                    this.priorityRequestInfo = requestInfo;
                }
                requestInfo.requestToken = ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLObject, new -$$Lambda$FileLoadOperation$q0OPLp-rp7G0uazoY5pveJXhQSk(this, requestInfo, tLObject), null, null, i10, this.isCdn ? this.cdnDatacenterId : this.datacenterId, i8, z);
                this.requestsCount++;
                i++;
            }
        }
    }

    public /* synthetic */ void lambda$startDownloadRequest$11$FileLoadOperation(RequestInfo requestInfo) {
        processRequestResult(requestInfo, null);
        requestInfo.response.freeResources();
    }

    public /* synthetic */ void lambda$startDownloadRequest$13$FileLoadOperation(RequestInfo requestInfo, TLObject tLObject, TLObject tLObject2, TL_error tL_error) {
        if (this.requestInfos.contains(requestInfo)) {
            if (requestInfo == this.priorityRequestInfo) {
                if (BuildVars.DEBUG_VERSION) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("frame get request completed ");
                    stringBuilder.append(this.priorityRequestInfo.offset);
                    FileLog.d(stringBuilder.toString());
                }
                this.priorityRequestInfo = null;
            }
            if (tL_error != null) {
                if (FileRefController.isFileRefError(tL_error.text)) {
                    requestReference(requestInfo);
                    return;
                } else if ((tLObject instanceof TL_upload_getCdnFile) && tL_error.text.equals("FILE_TOKEN_INVALID")) {
                    this.isCdn = false;
                    clearOperaion(requestInfo, false);
                    startDownloadRequest();
                    return;
                }
            }
            int i;
            if (tLObject2 instanceof TL_upload_fileCdnRedirect) {
                TL_upload_fileCdnRedirect tL_upload_fileCdnRedirect = (TL_upload_fileCdnRedirect) tLObject2;
                if (!tL_upload_fileCdnRedirect.file_hashes.isEmpty()) {
                    if (this.cdnHashes == null) {
                        this.cdnHashes = new SparseArray();
                    }
                    for (i = 0; i < tL_upload_fileCdnRedirect.file_hashes.size(); i++) {
                        TL_fileHash tL_fileHash = (TL_fileHash) tL_upload_fileCdnRedirect.file_hashes.get(i);
                        this.cdnHashes.put(tL_fileHash.offset, tL_fileHash);
                    }
                }
                byte[] bArr = tL_upload_fileCdnRedirect.encryption_iv;
                if (bArr != null) {
                    byte[] bArr2 = tL_upload_fileCdnRedirect.encryption_key;
                    if (bArr2 != null && bArr.length == 16 && bArr2.length == 32) {
                        this.isCdn = true;
                        if (this.notCheckedCdnRanges == null) {
                            this.notCheckedCdnRanges = new ArrayList();
                            this.notCheckedCdnRanges.add(new Range(0, 12288));
                        }
                        this.cdnDatacenterId = tL_upload_fileCdnRedirect.dc_id;
                        this.cdnIv = tL_upload_fileCdnRedirect.encryption_iv;
                        this.cdnKey = tL_upload_fileCdnRedirect.encryption_key;
                        this.cdnToken = tL_upload_fileCdnRedirect.file_token;
                        clearOperaion(requestInfo, false);
                        startDownloadRequest();
                    }
                }
                TL_error tL_error2 = new TL_error();
                tL_error2.text = "bad redirect response";
                tL_error2.code = 400;
                processRequestResult(requestInfo, tL_error2);
            } else if (!(tLObject2 instanceof TL_upload_cdnFileReuploadNeeded)) {
                if (tLObject2 instanceof TL_upload_file) {
                    requestInfo.response = (TL_upload_file) tLObject2;
                } else if (tLObject2 instanceof TL_upload_webFile) {
                    requestInfo.responseWeb = (TL_upload_webFile) tLObject2;
                    if (this.totalBytesCount == 0 && requestInfo.responseWeb.size != 0) {
                        this.totalBytesCount = requestInfo.responseWeb.size;
                    }
                } else {
                    requestInfo.responseCdn = (TL_upload_cdnFile) tLObject2;
                }
                if (tLObject2 != null) {
                    i = this.currentType;
                    if (i == 50331648) {
                        StatsController.getInstance(this.currentAccount).incrementReceivedBytesCount(tLObject2.networkType, 3, (long) (tLObject2.getObjectSize() + 4));
                    } else if (i == 33554432) {
                        StatsController.getInstance(this.currentAccount).incrementReceivedBytesCount(tLObject2.networkType, 2, (long) (tLObject2.getObjectSize() + 4));
                    } else if (i == 16777216) {
                        StatsController.getInstance(this.currentAccount).incrementReceivedBytesCount(tLObject2.networkType, 4, (long) (tLObject2.getObjectSize() + 4));
                    } else if (i == 67108864) {
                        StatsController.getInstance(this.currentAccount).incrementReceivedBytesCount(tLObject2.networkType, 5, (long) (tLObject2.getObjectSize() + 4));
                    }
                }
                processRequestResult(requestInfo, tL_error);
            } else if (!this.reuploadingCdn) {
                clearOperaion(requestInfo, false);
                this.reuploadingCdn = true;
                TL_upload_cdnFileReuploadNeeded tL_upload_cdnFileReuploadNeeded = (TL_upload_cdnFileReuploadNeeded) tLObject2;
                TL_upload_reuploadCdnFile tL_upload_reuploadCdnFile = new TL_upload_reuploadCdnFile();
                tL_upload_reuploadCdnFile.file_token = this.cdnToken;
                tL_upload_reuploadCdnFile.request_token = tL_upload_cdnFileReuploadNeeded.request_token;
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_upload_reuploadCdnFile, new -$$Lambda$FileLoadOperation$EDtaFymnkH324onISgCX13bWiFE(this, requestInfo), null, null, 0, this.datacenterId, 1, true);
            }
        }
    }

    public /* synthetic */ void lambda$null$12$FileLoadOperation(RequestInfo requestInfo, TLObject tLObject, TL_error tL_error) {
        int i = 0;
        this.reuploadingCdn = false;
        if (tL_error == null) {
            Vector vector = (Vector) tLObject;
            if (!vector.objects.isEmpty()) {
                if (this.cdnHashes == null) {
                    this.cdnHashes = new SparseArray();
                }
                while (i < vector.objects.size()) {
                    TL_fileHash tL_fileHash = (TL_fileHash) vector.objects.get(i);
                    this.cdnHashes.put(tL_fileHash.offset, tL_fileHash);
                    i++;
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
