package org.telegram.messenger;

import android.util.SparseArray;
import android.util.SparseIntArray;
import java.io.File;
import java.io.FileInputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
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
import org.telegram.tgnet.TLRPC.TL_inputWebFileGeoPointLocation;
import org.telegram.tgnet.TLRPC.TL_secureFile;
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
    private int streamStartOffset;
    private boolean supportsPreloading;
    private File tempPath;
    private int totalBytesCount;
    private int totalPreloadedBytes;
    private boolean ungzip;
    private WebFile webFile;
    private InputWebFileLocation webLocation;

    public interface FileLoadOperationDelegate {
        void didChangedLoadProgress(FileLoadOperation fileLoadOperation, float f);

        void didFailedLoadingFile(FileLoadOperation fileLoadOperation, int i);

        void didFinishLoadingFile(FileLoadOperation fileLoadOperation, File file);
    }

    private static class PreloadRange {
        private int fileOffset;
        private int length;
        private int start;

        private PreloadRange(int i, int i2, int i3) {
            this.fileOffset = i;
            this.start = i2;
            this.length = i3;
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
        this.state = 0;
        this.parentObject = obj;
        InputFileLocation inputFileLocation;
        TL_fileLocationToBeDeprecated tL_fileLocationToBeDeprecated;
        TL_fileLocationToBeDeprecated tL_fileLocationToBeDeprecated2;
        long j;
        if (imageLocation.isEncrypted()) {
            this.location = new TL_inputEncryptedFileLocation();
            inputFileLocation = this.location;
            tL_fileLocationToBeDeprecated = imageLocation.location;
            long j2 = tL_fileLocationToBeDeprecated.volume_id;
            inputFileLocation.id = j2;
            inputFileLocation.volume_id = j2;
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
            tL_fileLocationToBeDeprecated2 = imageLocation.location;
            j = tL_fileLocationToBeDeprecated2.volume_id;
            inputFileLocation.id = j;
            inputFileLocation.volume_id = j;
            inputFileLocation.local_id = tL_fileLocationToBeDeprecated2.local_id;
            inputFileLocation.big = imageLocation.photoPeerBig;
            inputFileLocation.peer = imageLocation.photoPeer;
        } else if (imageLocation.stickerSet != null) {
            this.location = new TL_inputStickerSetThumb();
            inputFileLocation = this.location;
            tL_fileLocationToBeDeprecated2 = imageLocation.location;
            j = tL_fileLocationToBeDeprecated2.volume_id;
            inputFileLocation.id = j;
            inputFileLocation.volume_id = j;
            inputFileLocation.local_id = tL_fileLocationToBeDeprecated2.local_id;
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
        this.ungzip = imageLocation.lottieAnimation;
        int i2 = imageLocation.dc_id;
        this.datacenterId = i2;
        this.initialDatacenterId = i2;
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

    /* JADX WARNING: Removed duplicated region for block: B:33:0x00e5 A:{Catch:{ Exception -> 0x0107 }} */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x00e0 A:{Catch:{ Exception -> 0x0107 }} */
    /* JADX WARNING: Removed duplicated region for block: B:45:? A:{SYNTHETIC, RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:39:0x00fe A:{Catch:{ Exception -> 0x0107 }} */
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
        r6.parentObject = r8;	 Catch:{ Exception -> 0x0107 }
        r8 = r7 instanceof org.telegram.tgnet.TLRPC.TL_documentEncrypted;	 Catch:{ Exception -> 0x0107 }
        r3 = "";
        if (r8 == 0) goto L_0x0043;
    L_0x0015:
        r8 = new org.telegram.tgnet.TLRPC$TL_inputEncryptedFileLocation;	 Catch:{ Exception -> 0x0107 }
        r8.<init>();	 Catch:{ Exception -> 0x0107 }
        r6.location = r8;	 Catch:{ Exception -> 0x0107 }
        r8 = r6.location;	 Catch:{ Exception -> 0x0107 }
        r4 = r7.id;	 Catch:{ Exception -> 0x0107 }
        r8.id = r4;	 Catch:{ Exception -> 0x0107 }
        r8 = r6.location;	 Catch:{ Exception -> 0x0107 }
        r4 = r7.access_hash;	 Catch:{ Exception -> 0x0107 }
        r8.access_hash = r4;	 Catch:{ Exception -> 0x0107 }
        r8 = r7.dc_id;	 Catch:{ Exception -> 0x0107 }
        r6.datacenterId = r8;	 Catch:{ Exception -> 0x0107 }
        r6.initialDatacenterId = r8;	 Catch:{ Exception -> 0x0107 }
        r8 = 32;
        r8 = new byte[r8];	 Catch:{ Exception -> 0x0107 }
        r6.iv = r8;	 Catch:{ Exception -> 0x0107 }
        r8 = r7.iv;	 Catch:{ Exception -> 0x0107 }
        r4 = r6.iv;	 Catch:{ Exception -> 0x0107 }
        r5 = r6.iv;	 Catch:{ Exception -> 0x0107 }
        r5 = r5.length;	 Catch:{ Exception -> 0x0107 }
        java.lang.System.arraycopy(r8, r1, r4, r1, r5);	 Catch:{ Exception -> 0x0107 }
        r8 = r7.key;	 Catch:{ Exception -> 0x0107 }
        r6.key = r8;	 Catch:{ Exception -> 0x0107 }
        goto L_0x0091;
    L_0x0043:
        r8 = r7 instanceof org.telegram.tgnet.TLRPC.TL_document;	 Catch:{ Exception -> 0x0107 }
        if (r8 == 0) goto L_0x0091;
    L_0x0047:
        r8 = new org.telegram.tgnet.TLRPC$TL_inputDocumentFileLocation;	 Catch:{ Exception -> 0x0107 }
        r8.<init>();	 Catch:{ Exception -> 0x0107 }
        r6.location = r8;	 Catch:{ Exception -> 0x0107 }
        r8 = r6.location;	 Catch:{ Exception -> 0x0107 }
        r4 = r7.id;	 Catch:{ Exception -> 0x0107 }
        r8.id = r4;	 Catch:{ Exception -> 0x0107 }
        r8 = r6.location;	 Catch:{ Exception -> 0x0107 }
        r4 = r7.access_hash;	 Catch:{ Exception -> 0x0107 }
        r8.access_hash = r4;	 Catch:{ Exception -> 0x0107 }
        r8 = r6.location;	 Catch:{ Exception -> 0x0107 }
        r4 = r7.file_reference;	 Catch:{ Exception -> 0x0107 }
        r8.file_reference = r4;	 Catch:{ Exception -> 0x0107 }
        r8 = r6.location;	 Catch:{ Exception -> 0x0107 }
        r8.thumb_size = r3;	 Catch:{ Exception -> 0x0107 }
        r8 = r6.location;	 Catch:{ Exception -> 0x0107 }
        r8 = r8.file_reference;	 Catch:{ Exception -> 0x0107 }
        if (r8 != 0) goto L_0x0070;
    L_0x006a:
        r8 = r6.location;	 Catch:{ Exception -> 0x0107 }
        r4 = new byte[r1];	 Catch:{ Exception -> 0x0107 }
        r8.file_reference = r4;	 Catch:{ Exception -> 0x0107 }
    L_0x0070:
        r8 = r7.dc_id;	 Catch:{ Exception -> 0x0107 }
        r6.datacenterId = r8;	 Catch:{ Exception -> 0x0107 }
        r6.initialDatacenterId = r8;	 Catch:{ Exception -> 0x0107 }
        r6.allowDisordererFileSave = r2;	 Catch:{ Exception -> 0x0107 }
        r8 = r7.attributes;	 Catch:{ Exception -> 0x0107 }
        r8 = r8.size();	 Catch:{ Exception -> 0x0107 }
        r4 = 0;
    L_0x007f:
        if (r4 >= r8) goto L_0x0091;
    L_0x0081:
        r5 = r7.attributes;	 Catch:{ Exception -> 0x0107 }
        r5 = r5.get(r4);	 Catch:{ Exception -> 0x0107 }
        r5 = r5 instanceof org.telegram.tgnet.TLRPC.TL_documentAttributeVideo;	 Catch:{ Exception -> 0x0107 }
        if (r5 == 0) goto L_0x008e;
    L_0x008b:
        r6.supportsPreloading = r2;	 Catch:{ Exception -> 0x0107 }
        goto L_0x0091;
    L_0x008e:
        r4 = r4 + 1;
        goto L_0x007f;
    L_0x0091:
        r8 = "application/x-tgsticker";
        r4 = r7.mime_type;	 Catch:{ Exception -> 0x0107 }
        r8 = r8.equals(r4);	 Catch:{ Exception -> 0x0107 }
        r6.ungzip = r8;	 Catch:{ Exception -> 0x0107 }
        r8 = r7.size;	 Catch:{ Exception -> 0x0107 }
        r6.totalBytesCount = r8;	 Catch:{ Exception -> 0x0107 }
        r8 = r6.key;	 Catch:{ Exception -> 0x0107 }
        if (r8 == 0) goto L_0x00b5;
    L_0x00a3:
        r8 = r6.totalBytesCount;	 Catch:{ Exception -> 0x0107 }
        r8 = r8 % r0;
        if (r8 == 0) goto L_0x00b5;
    L_0x00a8:
        r8 = r6.totalBytesCount;	 Catch:{ Exception -> 0x0107 }
        r8 = r8 % r0;
        r0 = r0 - r8;
        r6.bytesCountPadding = r0;	 Catch:{ Exception -> 0x0107 }
        r8 = r6.totalBytesCount;	 Catch:{ Exception -> 0x0107 }
        r0 = r6.bytesCountPadding;	 Catch:{ Exception -> 0x0107 }
        r8 = r8 + r0;
        r6.totalBytesCount = r8;	 Catch:{ Exception -> 0x0107 }
    L_0x00b5:
        r8 = org.telegram.messenger.FileLoader.getDocumentFileName(r7);	 Catch:{ Exception -> 0x0107 }
        r6.ext = r8;	 Catch:{ Exception -> 0x0107 }
        r8 = r6.ext;	 Catch:{ Exception -> 0x0107 }
        if (r8 == 0) goto L_0x00d4;
    L_0x00bf:
        r8 = r6.ext;	 Catch:{ Exception -> 0x0107 }
        r0 = 46;
        r8 = r8.lastIndexOf(r0);	 Catch:{ Exception -> 0x0107 }
        r0 = -1;
        if (r8 != r0) goto L_0x00cb;
    L_0x00ca:
        goto L_0x00d4;
    L_0x00cb:
        r0 = r6.ext;	 Catch:{ Exception -> 0x0107 }
        r8 = r0.substring(r8);	 Catch:{ Exception -> 0x0107 }
        r6.ext = r8;	 Catch:{ Exception -> 0x0107 }
        goto L_0x00d6;
    L_0x00d4:
        r6.ext = r3;	 Catch:{ Exception -> 0x0107 }
    L_0x00d6:
        r8 = "audio/ogg";
        r0 = r7.mime_type;	 Catch:{ Exception -> 0x0107 }
        r8 = r8.equals(r0);	 Catch:{ Exception -> 0x0107 }
        if (r8 == 0) goto L_0x00e5;
    L_0x00e0:
        r8 = 50331648; // 0x3000000 float:3.761582E-37 double:2.4867138E-316;
        r6.currentType = r8;	 Catch:{ Exception -> 0x0107 }
        goto L_0x00f6;
    L_0x00e5:
        r8 = r7.mime_type;	 Catch:{ Exception -> 0x0107 }
        r8 = org.telegram.messenger.FileLoader.isVideoMimeType(r8);	 Catch:{ Exception -> 0x0107 }
        if (r8 == 0) goto L_0x00f2;
    L_0x00ed:
        r8 = 33554432; // 0x2000000 float:9.403955E-38 double:1.6578092E-316;
        r6.currentType = r8;	 Catch:{ Exception -> 0x0107 }
        goto L_0x00f6;
    L_0x00f2:
        r8 = 67108864; // 0x4000000 float:1.5046328E-36 double:3.31561842E-316;
        r6.currentType = r8;	 Catch:{ Exception -> 0x0107 }
    L_0x00f6:
        r8 = r6.ext;	 Catch:{ Exception -> 0x0107 }
        r8 = r8.length();	 Catch:{ Exception -> 0x0107 }
        if (r8 > r2) goto L_0x010e;
    L_0x00fe:
        r7 = r7.mime_type;	 Catch:{ Exception -> 0x0107 }
        r7 = org.telegram.messenger.FileLoader.getExtensionByMimeType(r7);	 Catch:{ Exception -> 0x0107 }
        r6.ext = r7;	 Catch:{ Exception -> 0x0107 }
        goto L_0x010e;
    L_0x0107:
        r7 = move-exception;
        org.telegram.messenger.FileLog.e(r7);
        r6.onFail(r2, r1);
    L_0x010e:
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
            while (true) {
                obj = 1;
                if (i3 >= size) {
                    obj = null;
                    break;
                }
                Range range = (Range) arrayList.get(i3);
                if (i == range.end) {
                    range.end = i2;
                    break;
                } else if (i2 == range.start) {
                    range.start = i;
                    break;
                } else {
                    i3++;
                }
            }
            if (obj == null) {
                arrayList.add(new Range(i, i2));
            }
        }
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
        Utilities.stageQueue.postRunnable(new -$$Lambda$FileLoadOperation$sszlpx-7B35gY8yLDFGHhHn8Nio(this, fileArr, countDownLatch));
        try {
            countDownLatch.await();
        } catch (Exception e) {
            FileLog.e(e);
        }
        return fileArr[0];
    }

    public /* synthetic */ void lambda$getCurrentFile$0$FileLoadOperation(File[] fileArr, CountDownLatch countDownLatch) {
        if (this.state == 3) {
            fileArr[0] = this.cacheFileFinal;
        } else {
            fileArr[0] = this.cacheFileTemp;
        }
        countDownLatch.countDown();
    }

    private int getDownloadedLengthFromOffsetInternal(ArrayList<Range> arrayList, int i, int i2) {
        if (arrayList == null || this.state == 3 || arrayList.isEmpty()) {
            int i3 = this.downloadedBytes;
            if (i3 == 0) {
                return i2;
            }
            return Math.min(i2, Math.max(i3 - i, 0));
        }
        int size = arrayList.size();
        int i4 = i2;
        Range range = null;
        for (int i5 = 0; i5 < size; i5++) {
            Range range2 = (Range) arrayList.get(i5);
            if (i <= range2.start && (range == null || range2.start < range.start)) {
                range = range2;
            }
            if (range2.start <= i && range2.end > i) {
                i4 = 0;
            }
        }
        if (i4 == 0) {
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
        Utilities.stageQueue.postRunnable(new -$$Lambda$FileLoadOperation$7ur7HYEmrvSDh4lwc7_mdrw1G5A(this, iArr, i, i2, countDownLatch));
        try {
            countDownLatch.await();
        } catch (Exception unused) {
        }
        return iArr[0];
    }

    public /* synthetic */ void lambda$getDownloadedLengthFromOffset$1$FileLoadOperation(int[] iArr, int i, int i2, CountDownLatch countDownLatch) {
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
    public void removeStreamListener(FileStreamLoadOperation fileStreamLoadOperation) {
        Utilities.stageQueue.postRunnable(new -$$Lambda$FileLoadOperation$pRXavC_xR0CSUF8u1ykitHqvWcY(this, fileStreamLoadOperation));
    }

    public /* synthetic */ void lambda$removeStreamListener$2$FileLoadOperation(FileStreamLoadOperation fileStreamLoadOperation) {
        ArrayList arrayList = this.streamListeners;
        if (arrayList != null) {
            arrayList.remove(fileStreamLoadOperation);
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
        return start(null, 0);
    }

    /* JADX WARNING: Removed duplicated region for block: B:182:0x0530 A:{SYNTHETIC, Splitter:B:182:0x0530} */
    /* JADX WARNING: Removed duplicated region for block: B:192:0x0550  */
    /* JADX WARNING: Removed duplicated region for block: B:219:0x060d  */
    /* JADX WARNING: Removed duplicated region for block: B:206:0x05b4  */
    /* JADX WARNING: Removed duplicated region for block: B:226:0x0635  */
    /* JADX WARNING: Removed duplicated region for block: B:232:0x0661  */
    /* JADX WARNING: Removed duplicated region for block: B:237:0x069e  */
    /* JADX WARNING: Removed duplicated region for block: B:260:0x070f A:{Catch:{ Exception -> 0x0718 }} */
    /* JADX WARNING: Removed duplicated region for block: B:267:0x0725  */
    /* JADX WARNING: Removed duplicated region for block: B:265:0x0720  */
    /* JADX WARNING: Removed duplicated region for block: B:182:0x0530 A:{SYNTHETIC, Splitter:B:182:0x0530} */
    /* JADX WARNING: Removed duplicated region for block: B:192:0x0550  */
    /* JADX WARNING: Removed duplicated region for block: B:206:0x05b4  */
    /* JADX WARNING: Removed duplicated region for block: B:219:0x060d  */
    /* JADX WARNING: Removed duplicated region for block: B:226:0x0635  */
    /* JADX WARNING: Removed duplicated region for block: B:232:0x0661  */
    /* JADX WARNING: Removed duplicated region for block: B:237:0x069e  */
    /* JADX WARNING: Removed duplicated region for block: B:260:0x070f A:{Catch:{ Exception -> 0x0718 }} */
    /* JADX WARNING: Removed duplicated region for block: B:265:0x0720  */
    /* JADX WARNING: Removed duplicated region for block: B:267:0x0725  */
    /* JADX WARNING: Removed duplicated region for block: B:192:0x0550  */
    /* JADX WARNING: Removed duplicated region for block: B:219:0x060d  */
    /* JADX WARNING: Removed duplicated region for block: B:206:0x05b4  */
    /* JADX WARNING: Removed duplicated region for block: B:226:0x0635  */
    /* JADX WARNING: Removed duplicated region for block: B:232:0x0661  */
    /* JADX WARNING: Removed duplicated region for block: B:237:0x069e  */
    /* JADX WARNING: Removed duplicated region for block: B:260:0x070f A:{Catch:{ Exception -> 0x0718 }} */
    /* JADX WARNING: Removed duplicated region for block: B:267:0x0725  */
    /* JADX WARNING: Removed duplicated region for block: B:265:0x0720  */
    /* JADX WARNING: Removed duplicated region for block: B:123:0x0427 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:192:0x0550  */
    /* JADX WARNING: Removed duplicated region for block: B:206:0x05b4  */
    /* JADX WARNING: Removed duplicated region for block: B:219:0x060d  */
    /* JADX WARNING: Removed duplicated region for block: B:226:0x0635  */
    /* JADX WARNING: Removed duplicated region for block: B:232:0x0661  */
    /* JADX WARNING: Removed duplicated region for block: B:237:0x069e  */
    /* JADX WARNING: Removed duplicated region for block: B:260:0x070f A:{Catch:{ Exception -> 0x0718 }} */
    /* JADX WARNING: Removed duplicated region for block: B:265:0x0720  */
    /* JADX WARNING: Removed duplicated region for block: B:267:0x0725  */
    public boolean start(org.telegram.messenger.FileLoadOperationStream r24, int r25) {
        /*
        r23 = this;
        r1 = r23;
        r0 = r24;
        r2 = r25;
        r3 = r1.currentDownloadChunkSize;
        if (r3 != 0) goto L_0x001f;
    L_0x000a:
        r3 = r1.totalBytesCount;
        r4 = 1048576; // 0x100000 float:1.469368E-39 double:5.180654E-318;
        if (r3 < r4) goto L_0x0013;
    L_0x0010:
        r3 = 131072; // 0x20000 float:1.83671E-40 double:6.47582E-319;
        goto L_0x0016;
    L_0x0013:
        r3 = 32768; // 0x8000 float:4.5918E-41 double:1.61895E-319;
    L_0x0016:
        r1.currentDownloadChunkSize = r3;
        r3 = r1.totalBytesCount;
        r4 = 1048576; // 0x100000 float:1.469368E-39 double:5.180654E-318;
        r3 = 4;
        r1.currentMaxDownloadRequests = r3;
    L_0x001f:
        r3 = r1.state;
        r4 = 1;
        r5 = 0;
        if (r3 == 0) goto L_0x0027;
    L_0x0025:
        r3 = 1;
        goto L_0x0028;
    L_0x0027:
        r3 = 0;
    L_0x0028:
        r6 = r1.paused;
        r1.paused = r5;
        if (r0 == 0) goto L_0x0039;
    L_0x002e:
        r7 = org.telegram.messenger.Utilities.stageQueue;
        r8 = new org.telegram.messenger.-$$Lambda$FileLoadOperation$LZcjmIEbWfyJMJubpJYFWtYZ3uM;
        r8.<init>(r1, r2, r0, r3);
        r7.postRunnable(r8);
        goto L_0x0047;
    L_0x0039:
        if (r6 == 0) goto L_0x0047;
    L_0x003b:
        if (r3 == 0) goto L_0x0047;
    L_0x003d:
        r0 = org.telegram.messenger.Utilities.stageQueue;
        r7 = new org.telegram.messenger.-$$Lambda$HfxdqGkDOxPIykHiS0VM1dK9UjM;
        r7.<init>(r1);
        r0.postRunnable(r7);
    L_0x0047:
        if (r3 == 0) goto L_0x004a;
    L_0x0049:
        return r6;
    L_0x004a:
        r0 = r1.location;
        if (r0 != 0) goto L_0x0056;
    L_0x004e:
        r0 = r1.webLocation;
        if (r0 != 0) goto L_0x0056;
    L_0x0052:
        r1.onFail(r4, r5);
        return r5;
    L_0x0056:
        r0 = r1.currentDownloadChunkSize;
        r2 = r2 / r0;
        r2 = r2 * r0;
        r1.streamStartOffset = r2;
        r2 = r1.allowDisordererFileSave;
        if (r2 == 0) goto L_0x0075;
    L_0x0061:
        r2 = r1.totalBytesCount;
        if (r2 <= 0) goto L_0x0075;
    L_0x0065:
        if (r2 <= r0) goto L_0x0075;
    L_0x0067:
        r0 = new java.util.ArrayList;
        r0.<init>();
        r1.notLoadedBytesRanges = r0;
        r0 = new java.util.ArrayList;
        r0.<init>();
        r1.notRequestedBytesRanges = r0;
    L_0x0075:
        r0 = r1.webLocation;
        r2 = ".iv.enc";
        r3 = ".iv";
        r6 = ".enc";
        r7 = ".temp.enc";
        r8 = ".temp";
        r9 = ".";
        r10 = 0;
        if (r0 == 0) goto L_0x0111;
    L_0x0087:
        r0 = r1.webFile;
        r0 = r0.url;
        r0 = org.telegram.messenger.Utilities.MD5(r0);
        r13 = r1.encryptFile;
        if (r13 == 0) goto L_0x00d0;
    L_0x0093:
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r3.append(r0);
        r3.append(r7);
        r3 = r3.toString();
        r7 = new java.lang.StringBuilder;
        r7.<init>();
        r7.append(r0);
        r7.append(r9);
        r8 = r1.ext;
        r7.append(r8);
        r7.append(r6);
        r6 = r7.toString();
        r7 = r1.key;
        if (r7 == 0) goto L_0x00ce;
    L_0x00bd:
        r7 = new java.lang.StringBuilder;
        r7.<init>();
        r7.append(r0);
        r7.append(r2);
        r0 = r7.toString();
        r2 = r3;
        goto L_0x0108;
    L_0x00ce:
        r2 = r3;
        goto L_0x0107;
    L_0x00d0:
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r2.append(r0);
        r2.append(r8);
        r2 = r2.toString();
        r6 = new java.lang.StringBuilder;
        r6.<init>();
        r6.append(r0);
        r6.append(r9);
        r7 = r1.ext;
        r6.append(r7);
        r6 = r6.toString();
        r7 = r1.key;
        if (r7 == 0) goto L_0x0107;
    L_0x00f7:
        r7 = new java.lang.StringBuilder;
        r7.<init>();
        r7.append(r0);
        r7.append(r3);
        r0 = r7.toString();
        goto L_0x0108;
    L_0x0107:
        r0 = 0;
    L_0x0108:
        r3 = 0;
        r12 = 0;
        r22 = r2;
        r2 = r0;
        r0 = r22;
        goto L_0x0357;
    L_0x0111:
        r0 = r1.location;
        r13 = r0.volume_id;
        r15 = "_";
        r16 = (r13 > r10 ? 1 : (r13 == r10 ? 0 : -1));
        if (r16 == 0) goto L_0x0245;
    L_0x011b:
        r0 = r0.local_id;
        if (r0 == 0) goto L_0x0245;
    L_0x011f:
        r0 = r1.datacenterId;
        r12 = -NUM; // 0xfffffffvar_ float:-0.0 double:NaN;
        if (r0 == r12) goto L_0x0241;
    L_0x0125:
        r16 = -NUM; // 0xfffffffvar_ float:-0.0 double:NaN;
        r12 = (r13 > r16 ? 1 : (r13 == r16 ? 0 : -1));
        if (r12 == 0) goto L_0x0241;
    L_0x012c:
        if (r0 != 0) goto L_0x0130;
    L_0x012e:
        goto L_0x0241;
    L_0x0130:
        r0 = r1.encryptFile;
        if (r0 == 0) goto L_0x0199;
    L_0x0134:
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r3 = r1.location;
        r12 = r3.volume_id;
        r0.append(r12);
        r0.append(r15);
        r3 = r1.location;
        r3 = r3.local_id;
        r0.append(r3);
        r0.append(r7);
        r0 = r0.toString();
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r7 = r1.location;
        r7 = r7.volume_id;
        r3.append(r7);
        r3.append(r15);
        r7 = r1.location;
        r7 = r7.local_id;
        r3.append(r7);
        r3.append(r9);
        r7 = r1.ext;
        r3.append(r7);
        r3.append(r6);
        r6 = r3.toString();
        r3 = r1.key;
        if (r3 == 0) goto L_0x02b3;
    L_0x017a:
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r7 = r1.location;
        r7 = r7.volume_id;
        r3.append(r7);
        r3.append(r15);
        r7 = r1.location;
        r7 = r7.local_id;
        r3.append(r7);
        r3.append(r2);
        r12 = r3.toString();
        goto L_0x02b1;
    L_0x0199:
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r2 = r1.location;
        r6 = r2.volume_id;
        r0.append(r6);
        r0.append(r15);
        r2 = r1.location;
        r2 = r2.local_id;
        r0.append(r2);
        r0.append(r8);
        r2 = r0.toString();
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r6 = r1.location;
        r6 = r6.volume_id;
        r0.append(r6);
        r0.append(r15);
        r6 = r1.location;
        r6 = r6.local_id;
        r0.append(r6);
        r0.append(r9);
        r6 = r1.ext;
        r0.append(r6);
        r6 = r0.toString();
        r0 = r1.key;
        if (r0 == 0) goto L_0x01fa;
    L_0x01dc:
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r7 = r1.location;
        r7 = r7.volume_id;
        r0.append(r7);
        r0.append(r15);
        r7 = r1.location;
        r7 = r7.local_id;
        r0.append(r7);
        r0.append(r3);
        r12 = r0.toString();
        goto L_0x01fb;
    L_0x01fa:
        r12 = 0;
    L_0x01fb:
        r0 = r1.notLoadedBytesRanges;
        if (r0 == 0) goto L_0x021f;
    L_0x01ff:
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r3 = r1.location;
        r7 = r3.volume_id;
        r0.append(r7);
        r0.append(r15);
        r3 = r1.location;
        r3 = r3.local_id;
        r0.append(r3);
        r3 = ".pt";
        r0.append(r3);
        r0 = r0.toString();
        goto L_0x0220;
    L_0x021f:
        r0 = 0;
    L_0x0220:
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r7 = r1.location;
        r7 = r7.volume_id;
        r3.append(r7);
        r3.append(r15);
        r7 = r1.location;
        r7 = r7.local_id;
        r3.append(r7);
        r7 = ".preload";
        r3.append(r7);
        r3 = r3.toString();
        goto L_0x0351;
    L_0x0241:
        r1.onFail(r4, r5);
        return r5;
    L_0x0245:
        r0 = r1.datacenterId;
        if (r0 == 0) goto L_0x073f;
    L_0x0249:
        r0 = r1.location;
        r12 = r0.id;
        r0 = (r12 > r10 ? 1 : (r12 == r10 ? 0 : -1));
        if (r0 != 0) goto L_0x0253;
    L_0x0251:
        goto L_0x073f;
    L_0x0253:
        r0 = r1.encryptFile;
        if (r0 == 0) goto L_0x02b8;
    L_0x0257:
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r3 = r1.datacenterId;
        r0.append(r3);
        r0.append(r15);
        r3 = r1.location;
        r8 = r3.id;
        r0.append(r8);
        r0.append(r7);
        r0 = r0.toString();
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r7 = r1.datacenterId;
        r3.append(r7);
        r3.append(r15);
        r7 = r1.location;
        r7 = r7.id;
        r3.append(r7);
        r7 = r1.ext;
        r3.append(r7);
        r3.append(r6);
        r6 = r3.toString();
        r3 = r1.key;
        if (r3 == 0) goto L_0x02b3;
    L_0x0296:
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r7 = r1.datacenterId;
        r3.append(r7);
        r3.append(r15);
        r7 = r1.location;
        r7 = r7.id;
        r3.append(r7);
        r3.append(r2);
        r12 = r3.toString();
    L_0x02b1:
        r2 = r12;
        goto L_0x02b4;
    L_0x02b3:
        r2 = 0;
    L_0x02b4:
        r3 = 0;
        r12 = 0;
        goto L_0x0357;
    L_0x02b8:
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r2 = r1.datacenterId;
        r0.append(r2);
        r0.append(r15);
        r2 = r1.location;
        r6 = r2.id;
        r0.append(r6);
        r0.append(r8);
        r2 = r0.toString();
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r6 = r1.datacenterId;
        r0.append(r6);
        r0.append(r15);
        r6 = r1.location;
        r6 = r6.id;
        r0.append(r6);
        r6 = r1.ext;
        r0.append(r6);
        r6 = r0.toString();
        r0 = r1.key;
        if (r0 == 0) goto L_0x0310;
    L_0x02f4:
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r7 = r1.datacenterId;
        r0.append(r7);
        r0.append(r15);
        r7 = r1.location;
        r7 = r7.id;
        r0.append(r7);
        r0.append(r3);
        r12 = r0.toString();
        goto L_0x0311;
    L_0x0310:
        r12 = 0;
    L_0x0311:
        r0 = r1.notLoadedBytesRanges;
        if (r0 == 0) goto L_0x0333;
    L_0x0315:
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r3 = r1.datacenterId;
        r0.append(r3);
        r0.append(r15);
        r3 = r1.location;
        r7 = r3.id;
        r0.append(r7);
        r3 = ".pt";
        r0.append(r3);
        r0 = r0.toString();
        goto L_0x0334;
    L_0x0333:
        r0 = 0;
    L_0x0334:
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r7 = r1.datacenterId;
        r3.append(r7);
        r3.append(r15);
        r7 = r1.location;
        r7 = r7.id;
        r3.append(r7);
        r7 = ".preload";
        r3.append(r7);
        r3 = r3.toString();
    L_0x0351:
        r22 = r12;
        r12 = r0;
        r0 = r2;
        r2 = r22;
    L_0x0357:
        r7 = new java.util.ArrayList;
        r8 = r1.currentMaxDownloadRequests;
        r7.<init>(r8);
        r1.requestInfos = r7;
        r7 = new java.util.ArrayList;
        r8 = r1.currentMaxDownloadRequests;
        r8 = r8 - r4;
        r7.<init>(r8);
        r1.delayedRequestInfos = r7;
        r1.state = r4;
        r7 = new java.io.File;
        r8 = r1.storePath;
        r7.<init>(r8, r6);
        r1.cacheFileFinal = r7;
        r7 = r1.cacheFileFinal;
        r7 = r7.exists();
        if (r7 == 0) goto L_0x0392;
    L_0x037d:
        r8 = r1.totalBytesCount;
        if (r8 == 0) goto L_0x0392;
    L_0x0381:
        r8 = (long) r8;
        r13 = r1.cacheFileFinal;
        r13 = r13.length();
        r15 = (r8 > r13 ? 1 : (r8 == r13 ? 0 : -1));
        if (r15 == 0) goto L_0x0392;
    L_0x038c:
        r7 = r1.cacheFileFinal;
        r7.delete();
        r7 = 0;
    L_0x0392:
        if (r7 != 0) goto L_0x0734;
    L_0x0394:
        r7 = new java.io.File;
        r8 = r1.tempPath;
        r7.<init>(r8, r0);
        r1.cacheFileTemp = r7;
        r0 = r1.encryptFile;
        r7 = 32;
        r8 = "rws";
        if (r0 == 0) goto L_0x041c;
    L_0x03a5:
        r0 = new java.io.File;
        r9 = org.telegram.messenger.FileLoader.getInternalCacheDir();
        r13 = new java.lang.StringBuilder;
        r13.<init>();
        r13.append(r6);
        r6 = ".key";
        r13.append(r6);
        r6 = r13.toString();
        r0.<init>(r9, r6);
        r6 = new java.io.RandomAccessFile;	 Catch:{ Exception -> 0x0416 }
        r6.<init>(r0, r8);	 Catch:{ Exception -> 0x0416 }
        r13 = r0.length();	 Catch:{ Exception -> 0x0416 }
        r0 = new byte[r7];	 Catch:{ Exception -> 0x0416 }
        r1.encryptKey = r0;	 Catch:{ Exception -> 0x0416 }
        r0 = 16;
        r0 = new byte[r0];	 Catch:{ Exception -> 0x0416 }
        r1.encryptIv = r0;	 Catch:{ Exception -> 0x0416 }
        r0 = (r13 > r10 ? 1 : (r13 == r10 ? 0 : -1));
        if (r0 <= 0) goto L_0x03eb;
    L_0x03d6:
        r15 = 48;
        r13 = r13 % r15;
        r0 = (r13 > r10 ? 1 : (r13 == r10 ? 0 : -1));
        if (r0 != 0) goto L_0x03eb;
    L_0x03dd:
        r0 = r1.encryptKey;	 Catch:{ Exception -> 0x0416 }
        r6.read(r0, r5, r7);	 Catch:{ Exception -> 0x0416 }
        r0 = r1.encryptIv;	 Catch:{ Exception -> 0x0416 }
        r9 = 16;
        r6.read(r0, r5, r9);	 Catch:{ Exception -> 0x0416 }
        r9 = 0;
        goto L_0x0404;
    L_0x03eb:
        r0 = org.telegram.messenger.Utilities.random;	 Catch:{ Exception -> 0x0416 }
        r9 = r1.encryptKey;	 Catch:{ Exception -> 0x0416 }
        r0.nextBytes(r9);	 Catch:{ Exception -> 0x0416 }
        r0 = org.telegram.messenger.Utilities.random;	 Catch:{ Exception -> 0x0416 }
        r9 = r1.encryptIv;	 Catch:{ Exception -> 0x0416 }
        r0.nextBytes(r9);	 Catch:{ Exception -> 0x0416 }
        r0 = r1.encryptKey;	 Catch:{ Exception -> 0x0416 }
        r6.write(r0);	 Catch:{ Exception -> 0x0416 }
        r0 = r1.encryptIv;	 Catch:{ Exception -> 0x0416 }
        r6.write(r0);	 Catch:{ Exception -> 0x0416 }
        r9 = 1;
    L_0x0404:
        r0 = r6.getChannel();	 Catch:{ Exception -> 0x040c }
        r0.close();	 Catch:{ Exception -> 0x040c }
        goto L_0x0410;
    L_0x040c:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);	 Catch:{ Exception -> 0x0414 }
    L_0x0410:
        r6.close();	 Catch:{ Exception -> 0x0414 }
        goto L_0x041d;
    L_0x0414:
        r0 = move-exception;
        goto L_0x0418;
    L_0x0416:
        r0 = move-exception;
        r9 = 0;
    L_0x0418:
        org.telegram.messenger.FileLog.e(r0);
        goto L_0x041d;
    L_0x041c:
        r9 = 0;
    L_0x041d:
        r6 = new boolean[r4];
        r6[r5] = r5;
        r0 = r1.supportsPreloading;
        r13 = 4;
        if (r0 == 0) goto L_0x054c;
    L_0x0427:
        if (r3 == 0) goto L_0x054c;
    L_0x0429:
        r0 = new java.io.File;
        r15 = r1.tempPath;
        r0.<init>(r15, r3);
        r1.cacheFilePreload = r0;
        r0 = new java.io.RandomAccessFile;	 Catch:{ Exception -> 0x051b }
        r3 = r1.cacheFilePreload;	 Catch:{ Exception -> 0x051b }
        r0.<init>(r3, r8);	 Catch:{ Exception -> 0x051b }
        r1.preloadStream = r0;	 Catch:{ Exception -> 0x051b }
        r0 = r1.preloadStream;	 Catch:{ Exception -> 0x051b }
        r15 = r0.length();	 Catch:{ Exception -> 0x051b }
        r1.preloadStreamFileOffset = r4;	 Catch:{ Exception -> 0x051b }
        r10 = (long) r5;	 Catch:{ Exception -> 0x051b }
        r10 = r15 - r10;
        r18 = 1;
        r0 = (r10 > r18 ? 1 : (r10 == r18 ? 0 : -1));
        if (r0 <= 0) goto L_0x050e;
    L_0x044c:
        r0 = r1.preloadStream;	 Catch:{ Exception -> 0x051b }
        r0 = r0.readByte();	 Catch:{ Exception -> 0x051b }
        if (r0 == 0) goto L_0x0456;
    L_0x0454:
        r0 = 1;
        goto L_0x0457;
    L_0x0456:
        r0 = 0;
    L_0x0457:
        r6[r5] = r0;	 Catch:{ Exception -> 0x051b }
        r0 = 1;
    L_0x045a:
        r10 = (long) r0;	 Catch:{ Exception -> 0x051b }
        r3 = (r10 > r15 ? 1 : (r10 == r15 ? 0 : -1));
        if (r3 >= 0) goto L_0x050e;
    L_0x045f:
        r10 = r15 - r10;
        r3 = (r10 > r13 ? 1 : (r10 == r13 ? 0 : -1));
        if (r3 >= 0) goto L_0x0467;
    L_0x0465:
        goto L_0x050e;
    L_0x0467:
        r3 = r1.preloadStream;	 Catch:{ Exception -> 0x051b }
        r3 = r3.readInt();	 Catch:{ Exception -> 0x051b }
        r0 = r0 + 4;
        r10 = (long) r0;	 Catch:{ Exception -> 0x051b }
        r10 = r15 - r10;
        r18 = (r10 > r13 ? 1 : (r10 == r13 ? 0 : -1));
        if (r18 < 0) goto L_0x050e;
    L_0x0476:
        if (r3 < 0) goto L_0x050e;
    L_0x0478:
        r10 = r1.totalBytesCount;	 Catch:{ Exception -> 0x051b }
        if (r3 <= r10) goto L_0x047e;
    L_0x047c:
        goto L_0x050e;
    L_0x047e:
        r10 = r1.preloadStream;	 Catch:{ Exception -> 0x051b }
        r10 = r10.readInt();	 Catch:{ Exception -> 0x051b }
        r0 = r0 + 4;
        r18 = r6;
        r5 = (long) r0;
        r5 = r15 - r5;
        r13 = (long) r10;
        r20 = (r5 > r13 ? 1 : (r5 == r13 ? 0 : -1));
        if (r20 < 0) goto L_0x0510;
    L_0x0490:
        r5 = r1.currentDownloadChunkSize;	 Catch:{ Exception -> 0x0519 }
        if (r10 <= r5) goto L_0x0496;
    L_0x0494:
        goto L_0x0510;
    L_0x0496:
        r5 = new org.telegram.messenger.FileLoadOperation$PreloadRange;	 Catch:{ Exception -> 0x0519 }
        r6 = 0;
        r5.<init>(r0, r3, r10);	 Catch:{ Exception -> 0x0519 }
        r0 = r0 + r10;
        r6 = r1.preloadStream;	 Catch:{ Exception -> 0x0519 }
        r13 = (long) r0;	 Catch:{ Exception -> 0x0519 }
        r6.seek(r13);	 Catch:{ Exception -> 0x0519 }
        r13 = r15 - r13;
        r20 = 12;
        r6 = (r13 > r20 ? 1 : (r13 == r20 ? 0 : -1));
        if (r6 >= 0) goto L_0x04ac;
    L_0x04ab:
        goto L_0x0510;
    L_0x04ac:
        r6 = r1.preloadStream;	 Catch:{ Exception -> 0x0519 }
        r6 = r6.readInt();	 Catch:{ Exception -> 0x0519 }
        r1.foundMoovSize = r6;	 Catch:{ Exception -> 0x0519 }
        r6 = r1.foundMoovSize;	 Catch:{ Exception -> 0x0519 }
        if (r6 == 0) goto L_0x04c9;
    L_0x04b8:
        r6 = r1.nextPreloadDownloadOffset;	 Catch:{ Exception -> 0x0519 }
        r13 = r1.totalBytesCount;	 Catch:{ Exception -> 0x0519 }
        r13 = r13 / 2;
        if (r6 <= r13) goto L_0x04c2;
    L_0x04c0:
        r6 = 2;
        goto L_0x04c3;
    L_0x04c2:
        r6 = 1;
    L_0x04c3:
        r1.moovFound = r6;	 Catch:{ Exception -> 0x0519 }
        r6 = r1.foundMoovSize;	 Catch:{ Exception -> 0x0519 }
        r1.preloadNotRequestedBytesCount = r6;	 Catch:{ Exception -> 0x0519 }
    L_0x04c9:
        r6 = r1.preloadStream;	 Catch:{ Exception -> 0x0519 }
        r6 = r6.readInt();	 Catch:{ Exception -> 0x0519 }
        r1.nextPreloadDownloadOffset = r6;	 Catch:{ Exception -> 0x0519 }
        r6 = r1.preloadStream;	 Catch:{ Exception -> 0x0519 }
        r6 = r6.readInt();	 Catch:{ Exception -> 0x0519 }
        r1.nextAtomOffset = r6;	 Catch:{ Exception -> 0x0519 }
        r0 = r0 + 12;
        r6 = r1.preloadedBytesRanges;	 Catch:{ Exception -> 0x0519 }
        if (r6 != 0) goto L_0x04e6;
    L_0x04df:
        r6 = new android.util.SparseArray;	 Catch:{ Exception -> 0x0519 }
        r6.<init>();	 Catch:{ Exception -> 0x0519 }
        r1.preloadedBytesRanges = r6;	 Catch:{ Exception -> 0x0519 }
    L_0x04e6:
        r6 = r1.requestedPreloadedBytesRanges;	 Catch:{ Exception -> 0x0519 }
        if (r6 != 0) goto L_0x04f1;
    L_0x04ea:
        r6 = new android.util.SparseIntArray;	 Catch:{ Exception -> 0x0519 }
        r6.<init>();	 Catch:{ Exception -> 0x0519 }
        r1.requestedPreloadedBytesRanges = r6;	 Catch:{ Exception -> 0x0519 }
    L_0x04f1:
        r6 = r1.preloadedBytesRanges;	 Catch:{ Exception -> 0x0519 }
        r6.put(r3, r5);	 Catch:{ Exception -> 0x0519 }
        r5 = r1.requestedPreloadedBytesRanges;	 Catch:{ Exception -> 0x0519 }
        r5.put(r3, r4);	 Catch:{ Exception -> 0x0519 }
        r3 = r1.totalPreloadedBytes;	 Catch:{ Exception -> 0x0519 }
        r3 = r3 + r10;
        r1.totalPreloadedBytes = r3;	 Catch:{ Exception -> 0x0519 }
        r3 = r1.preloadStreamFileOffset;	 Catch:{ Exception -> 0x0519 }
        r10 = r10 + 20;
        r3 = r3 + r10;
        r1.preloadStreamFileOffset = r3;	 Catch:{ Exception -> 0x0519 }
        r6 = r18;
        r5 = 0;
        r13 = 4;
        goto L_0x045a;
    L_0x050e:
        r18 = r6;
    L_0x0510:
        r0 = r1.preloadStream;	 Catch:{ Exception -> 0x0519 }
        r3 = r1.preloadStreamFileOffset;	 Catch:{ Exception -> 0x0519 }
        r5 = (long) r3;	 Catch:{ Exception -> 0x0519 }
        r0.seek(r5);	 Catch:{ Exception -> 0x0519 }
        goto L_0x0521;
    L_0x0519:
        r0 = move-exception;
        goto L_0x051e;
    L_0x051b:
        r0 = move-exception;
        r18 = r6;
    L_0x051e:
        org.telegram.messenger.FileLog.e(r0);
    L_0x0521:
        r0 = r1.isPreloadVideoOperation;
        if (r0 != 0) goto L_0x054e;
    L_0x0525:
        r0 = r1.preloadedBytesRanges;
        if (r0 != 0) goto L_0x054e;
    L_0x0529:
        r3 = 0;
        r1.cacheFilePreload = r3;
        r0 = r1.preloadStream;	 Catch:{ Exception -> 0x0547 }
        if (r0 == 0) goto L_0x054e;
    L_0x0530:
        r0 = r1.preloadStream;	 Catch:{ Exception -> 0x053a }
        r0 = r0.getChannel();	 Catch:{ Exception -> 0x053a }
        r0.close();	 Catch:{ Exception -> 0x053a }
        goto L_0x053e;
    L_0x053a:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);	 Catch:{ Exception -> 0x0547 }
    L_0x053e:
        r0 = r1.preloadStream;	 Catch:{ Exception -> 0x0547 }
        r0.close();	 Catch:{ Exception -> 0x0547 }
        r3 = 0;
        r1.preloadStream = r3;	 Catch:{ Exception -> 0x0547 }
        goto L_0x054e;
    L_0x0547:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
        goto L_0x054e;
    L_0x054c:
        r18 = r6;
    L_0x054e:
        if (r12 == 0) goto L_0x05ac;
    L_0x0550:
        r0 = new java.io.File;
        r3 = r1.tempPath;
        r0.<init>(r3, r12);
        r1.cacheFileParts = r0;
        r0 = new java.io.RandomAccessFile;	 Catch:{ Exception -> 0x05a8 }
        r3 = r1.cacheFileParts;	 Catch:{ Exception -> 0x05a8 }
        r0.<init>(r3, r8);	 Catch:{ Exception -> 0x05a8 }
        r1.filePartsStream = r0;	 Catch:{ Exception -> 0x05a8 }
        r0 = r1.filePartsStream;	 Catch:{ Exception -> 0x05a8 }
        r5 = r0.length();	 Catch:{ Exception -> 0x05a8 }
        r12 = 8;
        r12 = r5 % r12;
        r14 = 4;
        r0 = (r12 > r14 ? 1 : (r12 == r14 ? 0 : -1));
        if (r0 != 0) goto L_0x05ac;
    L_0x0572:
        r5 = r5 - r14;
        r0 = r1.filePartsStream;	 Catch:{ Exception -> 0x05a8 }
        r0 = r0.readInt();	 Catch:{ Exception -> 0x05a8 }
        r12 = (long) r0;	 Catch:{ Exception -> 0x05a8 }
        r14 = 2;
        r5 = r5 / r14;
        r3 = (r12 > r5 ? 1 : (r12 == r5 ? 0 : -1));
        if (r3 > 0) goto L_0x05ac;
    L_0x0581:
        r3 = 0;
    L_0x0582:
        if (r3 >= r0) goto L_0x05ac;
    L_0x0584:
        r5 = r1.filePartsStream;	 Catch:{ Exception -> 0x05a8 }
        r5 = r5.readInt();	 Catch:{ Exception -> 0x05a8 }
        r6 = r1.filePartsStream;	 Catch:{ Exception -> 0x05a8 }
        r6 = r6.readInt();	 Catch:{ Exception -> 0x05a8 }
        r10 = r1.notLoadedBytesRanges;	 Catch:{ Exception -> 0x05a8 }
        r12 = new org.telegram.messenger.FileLoadOperation$Range;	 Catch:{ Exception -> 0x05a8 }
        r13 = 0;
        r12.<init>(r5, r6);	 Catch:{ Exception -> 0x05a8 }
        r10.add(r12);	 Catch:{ Exception -> 0x05a8 }
        r10 = r1.notRequestedBytesRanges;	 Catch:{ Exception -> 0x05a8 }
        r12 = new org.telegram.messenger.FileLoadOperation$Range;	 Catch:{ Exception -> 0x05a8 }
        r12.<init>(r5, r6);	 Catch:{ Exception -> 0x05a8 }
        r10.add(r12);	 Catch:{ Exception -> 0x05a8 }
        r3 = r3 + 1;
        goto L_0x0582;
    L_0x05a8:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x05ac:
        r0 = r1.cacheFileTemp;
        r0 = r0.exists();
        if (r0 == 0) goto L_0x060d;
    L_0x05b4:
        if (r9 == 0) goto L_0x05bd;
    L_0x05b6:
        r0 = r1.cacheFileTemp;
        r0.delete();
        goto L_0x0631;
    L_0x05bd:
        r0 = r1.cacheFileTemp;
        r5 = r0.length();
        if (r2 == 0) goto L_0x05d5;
    L_0x05c5:
        r0 = r1.currentDownloadChunkSize;
        r12 = (long) r0;
        r5 = r5 % r12;
        r12 = 0;
        r0 = (r5 > r12 ? 1 : (r5 == r12 ? 0 : -1));
        if (r0 == 0) goto L_0x05d5;
    L_0x05cf:
        r3 = 0;
        r1.downloadedBytes = r3;
        r1.requestedBytesCount = r3;
        goto L_0x05e5;
    L_0x05d5:
        r0 = r1.cacheFileTemp;
        r5 = r0.length();
        r0 = (int) r5;
        r3 = r1.currentDownloadChunkSize;
        r0 = r0 / r3;
        r0 = r0 * r3;
        r1.downloadedBytes = r0;
        r1.requestedBytesCount = r0;
    L_0x05e5:
        r0 = r1.notLoadedBytesRanges;
        if (r0 == 0) goto L_0x0631;
    L_0x05e9:
        r0 = r0.isEmpty();
        if (r0 == 0) goto L_0x0631;
    L_0x05ef:
        r0 = r1.notLoadedBytesRanges;
        r3 = new org.telegram.messenger.FileLoadOperation$Range;
        r5 = r1.downloadedBytes;
        r6 = r1.totalBytesCount;
        r10 = 0;
        r3.<init>(r5, r6);
        r0.add(r3);
        r0 = r1.notRequestedBytesRanges;
        r3 = new org.telegram.messenger.FileLoadOperation$Range;
        r5 = r1.downloadedBytes;
        r6 = r1.totalBytesCount;
        r3.<init>(r5, r6);
        r0.add(r3);
        goto L_0x0631;
    L_0x060d:
        r0 = r1.notLoadedBytesRanges;
        if (r0 == 0) goto L_0x0631;
    L_0x0611:
        r0 = r0.isEmpty();
        if (r0 == 0) goto L_0x0631;
    L_0x0617:
        r0 = r1.notLoadedBytesRanges;
        r3 = new org.telegram.messenger.FileLoadOperation$Range;
        r5 = r1.totalBytesCount;
        r6 = 0;
        r10 = 0;
        r3.<init>(r10, r5);
        r0.add(r3);
        r0 = r1.notRequestedBytesRanges;
        r3 = new org.telegram.messenger.FileLoadOperation$Range;
        r5 = r1.totalBytesCount;
        r3.<init>(r10, r5);
        r0.add(r3);
    L_0x0631:
        r0 = r1.notLoadedBytesRanges;
        if (r0 == 0) goto L_0x065d;
    L_0x0635:
        r3 = r1.totalBytesCount;
        r1.downloadedBytes = r3;
        r0 = r0.size();
        r3 = 0;
    L_0x063e:
        if (r3 >= r0) goto L_0x0659;
    L_0x0640:
        r5 = r1.notLoadedBytesRanges;
        r5 = r5.get(r3);
        r5 = (org.telegram.messenger.FileLoadOperation.Range) r5;
        r6 = r1.downloadedBytes;
        r10 = r5.end;
        r5 = r5.start;
        r10 = r10 - r5;
        r6 = r6 - r10;
        r1.downloadedBytes = r6;
        r3 = r3 + 1;
        goto L_0x063e;
    L_0x0659:
        r0 = r1.downloadedBytes;
        r1.requestedBytesCount = r0;
    L_0x065d:
        r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r0 == 0) goto L_0x069c;
    L_0x0661:
        r0 = r1.isPreloadVideoOperation;
        if (r0 == 0) goto L_0x067c;
    L_0x0665:
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r3 = "start preloading file to temp = ";
        r0.append(r3);
        r3 = r1.cacheFileTemp;
        r0.append(r3);
        r0 = r0.toString();
        org.telegram.messenger.FileLog.d(r0);
        goto L_0x069c;
    L_0x067c:
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r3 = "start loading file to temp = ";
        r0.append(r3);
        r3 = r1.cacheFileTemp;
        r0.append(r3);
        r3 = " final = ";
        r0.append(r3);
        r3 = r1.cacheFileFinal;
        r0.append(r3);
        r0 = r0.toString();
        org.telegram.messenger.FileLog.d(r0);
    L_0x069c:
        if (r2 == 0) goto L_0x06e1;
    L_0x069e:
        r0 = new java.io.File;
        r3 = r1.tempPath;
        r0.<init>(r3, r2);
        r1.cacheIvTemp = r0;
        r0 = new java.io.RandomAccessFile;	 Catch:{ Exception -> 0x06d8 }
        r2 = r1.cacheIvTemp;	 Catch:{ Exception -> 0x06d8 }
        r0.<init>(r2, r8);	 Catch:{ Exception -> 0x06d8 }
        r1.fiv = r0;	 Catch:{ Exception -> 0x06d8 }
        r0 = r1.downloadedBytes;	 Catch:{ Exception -> 0x06d8 }
        if (r0 == 0) goto L_0x06e1;
    L_0x06b4:
        if (r9 != 0) goto L_0x06e1;
    L_0x06b6:
        r0 = r1.cacheIvTemp;	 Catch:{ Exception -> 0x06d8 }
        r2 = r0.length();	 Catch:{ Exception -> 0x06d8 }
        r5 = 0;
        r0 = (r2 > r5 ? 1 : (r2 == r5 ? 0 : -1));
        if (r0 <= 0) goto L_0x06d2;
    L_0x06c2:
        r9 = 32;
        r2 = r2 % r9;
        r0 = (r2 > r5 ? 1 : (r2 == r5 ? 0 : -1));
        if (r0 != 0) goto L_0x06d2;
    L_0x06c9:
        r0 = r1.fiv;	 Catch:{ Exception -> 0x06d8 }
        r2 = r1.iv;	 Catch:{ Exception -> 0x06d8 }
        r3 = 0;
        r0.read(r2, r3, r7);	 Catch:{ Exception -> 0x06d8 }
        goto L_0x06e1;
    L_0x06d2:
        r2 = 0;
        r1.downloadedBytes = r2;	 Catch:{ Exception -> 0x06d8 }
        r1.requestedBytesCount = r2;	 Catch:{ Exception -> 0x06d8 }
        goto L_0x06e1;
    L_0x06d8:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
        r2 = 0;
        r1.downloadedBytes = r2;
        r1.requestedBytesCount = r2;
    L_0x06e1:
        r0 = r1.isPreloadVideoOperation;
        if (r0 != 0) goto L_0x0702;
    L_0x06e5:
        r0 = r1.downloadedBytes;
        if (r0 == 0) goto L_0x0702;
    L_0x06e9:
        r0 = r1.totalBytesCount;
        if (r0 <= 0) goto L_0x0702;
    L_0x06ed:
        r23.copyNotLoadedRanges();
        r0 = r1.delegate;
        r2 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r3 = r1.downloadedBytes;
        r3 = (float) r3;
        r5 = r1.totalBytesCount;
        r5 = (float) r5;
        r3 = r3 / r5;
        r2 = java.lang.Math.min(r2, r3);
        r0.didChangedLoadProgress(r1, r2);
    L_0x0702:
        r0 = new java.io.RandomAccessFile;	 Catch:{ Exception -> 0x0718 }
        r2 = r1.cacheFileTemp;	 Catch:{ Exception -> 0x0718 }
        r0.<init>(r2, r8);	 Catch:{ Exception -> 0x0718 }
        r1.fileOutputStream = r0;	 Catch:{ Exception -> 0x0718 }
        r0 = r1.downloadedBytes;	 Catch:{ Exception -> 0x0718 }
        if (r0 == 0) goto L_0x071c;
    L_0x070f:
        r0 = r1.fileOutputStream;	 Catch:{ Exception -> 0x0718 }
        r2 = r1.downloadedBytes;	 Catch:{ Exception -> 0x0718 }
        r2 = (long) r2;	 Catch:{ Exception -> 0x0718 }
        r0.seek(r2);	 Catch:{ Exception -> 0x0718 }
        goto L_0x071c;
    L_0x0718:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x071c:
        r0 = r1.fileOutputStream;
        if (r0 != 0) goto L_0x0725;
    L_0x0720:
        r2 = 0;
        r1.onFail(r4, r2);
        return r2;
    L_0x0725:
        r1.started = r4;
        r0 = org.telegram.messenger.Utilities.stageQueue;
        r2 = new org.telegram.messenger.-$$Lambda$FileLoadOperation$zWcnXiI1arbfnF_-ZxDNUNllncs;
        r3 = r18;
        r2.<init>(r1, r3);
        r0.postRunnable(r2);
        goto L_0x073e;
    L_0x0734:
        r2 = 0;
        r1.started = r4;
        r1.onFinishLoadingFile(r2);	 Catch:{ Exception -> 0x073b }
        goto L_0x073e;
    L_0x073b:
        r1.onFail(r4, r2);
    L_0x073e:
        return r4;
    L_0x073f:
        r2 = 0;
        r1.onFail(r4, r2);
        return r2;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.FileLoadOperation.start(org.telegram.messenger.FileLoadOperationStream, int):boolean");
    }

    public /* synthetic */ void lambda$start$3$FileLoadOperation(int i, FileLoadOperationStream fileLoadOperationStream, boolean z) {
        if (this.streamListeners == null) {
            this.streamListeners = new ArrayList();
        }
        int i2 = this.currentDownloadChunkSize;
        this.streamStartOffset = (i / i2) * i2;
        this.streamListeners.add(fileLoadOperationStream);
        if (z) {
            if (!(this.preloadedBytesRanges == null || getDownloadedLengthFromOffsetInternal(this.notLoadedBytesRanges, this.streamStartOffset, 1) != 0 || this.preloadedBytesRanges.get(this.streamStartOffset) == null)) {
                this.nextPartWasPreloaded = true;
            }
            startDownloadRequest();
            this.nextPartWasPreloaded = false;
        }
    }

    public /* synthetic */ void lambda$start$4$FileLoadOperation(boolean[] zArr) {
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
            Utilities.stageQueue.postRunnable(new -$$Lambda$FileLoadOperation$dCUC8Z2YsvYYD1fE6icyc8raIws(this, z));
        } else {
            this.isPreloadVideoOperation = z;
        }
    }

    public /* synthetic */ void lambda$setIsPreloadVideoOperation$5$FileLoadOperation(boolean z) {
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
        Utilities.stageQueue.postRunnable(new -$$Lambda$FileLoadOperation$J6yoj3V6lUrD2GOh_IqPARM2LhA(this));
    }

    public /* synthetic */ void lambda$cancel$6$FileLoadOperation() {
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
                    if (this.ungzip) {
                        try {
                            GZIPInputStream gZIPInputStream = new GZIPInputStream(new FileInputStream(file));
                            FileLoader.copyFile(gZIPInputStream, this.cacheFileFinal);
                            gZIPInputStream.close();
                            this.cacheFileTemp.delete();
                        } catch (ZipException unused) {
                            this.ungzip = false;
                        } catch (Throwable th) {
                            FileLog.e(th);
                            stringBuilder = new StringBuilder();
                            stringBuilder.append("unable to ungzip temp = ");
                            stringBuilder.append(this.cacheFileTemp);
                            stringBuilder.append(str);
                            stringBuilder.append(this.cacheFileFinal);
                            FileLog.e(stringBuilder.toString());
                        }
                    }
                    if (!(this.ungzip || this.cacheFileTemp.renameTo(this.cacheFileFinal))) {
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
                            Utilities.stageQueue.postRunnable(new -$$Lambda$FileLoadOperation$Pg1188DV6hLAQ13wzfrMjXc2Ie0(this, z), 200);
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

    public /* synthetic */ void lambda$onFinishLoadingFile$7$FileLoadOperation(boolean z) {
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
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_upload_getCdnFileHashes, new -$$Lambda$FileLoadOperation$AloCOvGHlndklEjA6lccwgvlez8(this), null, null, 0, this.datacenterId, 1, true);
        }
    }

    public /* synthetic */ void lambda$requestFileOffsets$8$FileLoadOperation(TLObject tLObject, TL_error tL_error) {
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
    /* JADX WARNING: Removed duplicated region for block: B:75:0x01ed A:{Catch:{ Exception -> 0x0498 }} */
    /* JADX WARNING: Removed duplicated region for block: B:74:0x01df A:{Catch:{ Exception -> 0x0498 }} */
    /* JADX WARNING: Removed duplicated region for block: B:97:0x022d A:{Catch:{ Exception -> 0x0498 }} */
    /* JADX WARNING: Removed duplicated region for block: B:104:0x0256 A:{Catch:{ Exception -> 0x0498 }} */
    /* JADX WARNING: Removed duplicated region for block: B:107:0x0295 A:{Catch:{ Exception -> 0x0498 }} */
    /* JADX WARNING: Removed duplicated region for block: B:112:0x02e0 A:{Catch:{ Exception -> 0x0498 }} */
    /* JADX WARNING: Removed duplicated region for block: B:150:0x03f5 A:{Catch:{ Exception -> 0x0498 }} */
    /* JADX WARNING: Removed duplicated region for block: B:179:0x048f A:{Catch:{ Exception -> 0x0498 }} */
    /* JADX WARNING: Removed duplicated region for block: B:178:0x048a A:{Catch:{ Exception -> 0x0498 }} */
    /* JADX WARNING: Missing block: B:81:0x0207, code skipped:
            if (r1.downloadedBytes >= r1.totalBytesCount) goto L_0x0209;
     */
    public boolean processRequestResult(org.telegram.messenger.FileLoadOperation.RequestInfo r22, org.telegram.tgnet.TLRPC.TL_error r23) {
        /*
        r21 = this;
        r1 = r21;
        r0 = r23;
        r2 = r1.state;
        r3 = " offset ";
        r4 = 1;
        r5 = 0;
        if (r2 == r4) goto L_0x0031;
    L_0x000c:
        r0 = org.telegram.messenger.BuildVars.DEBUG_VERSION;
        if (r0 == 0) goto L_0x0030;
    L_0x0010:
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r2 = "trying to write to finished file ";
        r0.append(r2);
        r2 = r1.cacheFileFinal;
        r0.append(r2);
        r0.append(r3);
        r2 = r22.offset;
        r0.append(r2);
        r0 = r0.toString();
        org.telegram.messenger.FileLog.d(r0);
    L_0x0030:
        return r5;
    L_0x0031:
        r2 = r1.requestInfos;
        r6 = r22;
        r2.remove(r6);
        r2 = " local_id = ";
        r7 = 2;
        r8 = " id = ";
        r9 = 0;
        if (r0 != 0) goto L_0x04a1;
    L_0x0040:
        r0 = r1.notLoadedBytesRanges;	 Catch:{ Exception -> 0x0498 }
        if (r0 != 0) goto L_0x0050;
    L_0x0044:
        r0 = r1.downloadedBytes;	 Catch:{ Exception -> 0x0498 }
        r10 = r22.offset;	 Catch:{ Exception -> 0x0498 }
        if (r0 == r10) goto L_0x0050;
    L_0x004c:
        r21.delayRequestInfo(r22);	 Catch:{ Exception -> 0x0498 }
        return r5;
    L_0x0050:
        r0 = r22.response;	 Catch:{ Exception -> 0x0498 }
        if (r0 == 0) goto L_0x005d;
    L_0x0056:
        r0 = r22.response;	 Catch:{ Exception -> 0x0498 }
        r0 = r0.bytes;	 Catch:{ Exception -> 0x0498 }
        goto L_0x0078;
    L_0x005d:
        r0 = r22.responseWeb;	 Catch:{ Exception -> 0x0498 }
        if (r0 == 0) goto L_0x006a;
    L_0x0063:
        r0 = r22.responseWeb;	 Catch:{ Exception -> 0x0498 }
        r0 = r0.bytes;	 Catch:{ Exception -> 0x0498 }
        goto L_0x0078;
    L_0x006a:
        r0 = r22.responseCdn;	 Catch:{ Exception -> 0x0498 }
        if (r0 == 0) goto L_0x0077;
    L_0x0070:
        r0 = r22.responseCdn;	 Catch:{ Exception -> 0x0498 }
        r0 = r0.bytes;	 Catch:{ Exception -> 0x0498 }
        goto L_0x0078;
    L_0x0077:
        r0 = r9;
    L_0x0078:
        if (r0 == 0) goto L_0x0494;
    L_0x007a:
        r10 = r0.limit();	 Catch:{ Exception -> 0x0498 }
        if (r10 != 0) goto L_0x0082;
    L_0x0080:
        goto L_0x0494;
    L_0x0082:
        r10 = r0.limit();	 Catch:{ Exception -> 0x0498 }
        r11 = r1.isCdn;	 Catch:{ Exception -> 0x0498 }
        r12 = 131072; // 0x20000 float:1.83671E-40 double:6.47582E-319;
        if (r11 == 0) goto L_0x00aa;
    L_0x008c:
        r11 = r22.offset;	 Catch:{ Exception -> 0x0498 }
        r11 = r11 / r12;
        r11 = r11 * r12;
        r13 = r1.cdnHashes;	 Catch:{ Exception -> 0x0498 }
        if (r13 == 0) goto L_0x00a0;
    L_0x0097:
        r13 = r1.cdnHashes;	 Catch:{ Exception -> 0x0498 }
        r13 = r13.get(r11);	 Catch:{ Exception -> 0x0498 }
        r13 = (org.telegram.tgnet.TLRPC.TL_fileHash) r13;	 Catch:{ Exception -> 0x0498 }
        goto L_0x00a1;
    L_0x00a0:
        r13 = r9;
    L_0x00a1:
        if (r13 != 0) goto L_0x00aa;
    L_0x00a3:
        r21.delayRequestInfo(r22);	 Catch:{ Exception -> 0x0498 }
        r1.requestFileOffsets(r11);	 Catch:{ Exception -> 0x0498 }
        return r4;
    L_0x00aa:
        r11 = r22.responseCdn;	 Catch:{ Exception -> 0x0498 }
        r13 = 12;
        if (r11 == 0) goto L_0x00ed;
    L_0x00b2:
        r11 = r22.offset;	 Catch:{ Exception -> 0x0498 }
        r11 = r11 / 16;
        r14 = r1.cdnIv;	 Catch:{ Exception -> 0x0498 }
        r15 = 15;
        r12 = r11 & 255;
        r12 = (byte) r12;	 Catch:{ Exception -> 0x0498 }
        r14[r15] = r12;	 Catch:{ Exception -> 0x0498 }
        r12 = r1.cdnIv;	 Catch:{ Exception -> 0x0498 }
        r14 = 14;
        r15 = r11 >> 8;
        r15 = r15 & 255;
        r15 = (byte) r15;	 Catch:{ Exception -> 0x0498 }
        r12[r14] = r15;	 Catch:{ Exception -> 0x0498 }
        r12 = r1.cdnIv;	 Catch:{ Exception -> 0x0498 }
        r14 = 13;
        r15 = r11 >> 16;
        r15 = r15 & 255;
        r15 = (byte) r15;	 Catch:{ Exception -> 0x0498 }
        r12[r14] = r15;	 Catch:{ Exception -> 0x0498 }
        r12 = r1.cdnIv;	 Catch:{ Exception -> 0x0498 }
        r11 = r11 >> 24;
        r11 = r11 & 255;
        r11 = (byte) r11;	 Catch:{ Exception -> 0x0498 }
        r12[r13] = r11;	 Catch:{ Exception -> 0x0498 }
        r11 = r0.buffer;	 Catch:{ Exception -> 0x0498 }
        r12 = r1.cdnKey;	 Catch:{ Exception -> 0x0498 }
        r14 = r1.cdnIv;	 Catch:{ Exception -> 0x0498 }
        r15 = r0.limit();	 Catch:{ Exception -> 0x0498 }
        org.telegram.messenger.Utilities.aesCtrDecryption(r11, r12, r14, r5, r15);	 Catch:{ Exception -> 0x0498 }
    L_0x00ed:
        r11 = r1.isPreloadVideoOperation;	 Catch:{ Exception -> 0x0498 }
        if (r11 == 0) goto L_0x01fa;
    L_0x00f1:
        r2 = r1.preloadStream;	 Catch:{ Exception -> 0x0498 }
        r8 = r22.offset;	 Catch:{ Exception -> 0x0498 }
        r2.writeInt(r8);	 Catch:{ Exception -> 0x0498 }
        r2 = r1.preloadStream;	 Catch:{ Exception -> 0x0498 }
        r2.writeInt(r10);	 Catch:{ Exception -> 0x0498 }
        r2 = r1.preloadStreamFileOffset;	 Catch:{ Exception -> 0x0498 }
        r2 = r2 + 8;
        r1.preloadStreamFileOffset = r2;	 Catch:{ Exception -> 0x0498 }
        r2 = r1.preloadStream;	 Catch:{ Exception -> 0x0498 }
        r2 = r2.getChannel();	 Catch:{ Exception -> 0x0498 }
        r8 = r0.buffer;	 Catch:{ Exception -> 0x0498 }
        r2.write(r8);	 Catch:{ Exception -> 0x0498 }
        r2 = org.telegram.messenger.BuildVars.DEBUG_VERSION;	 Catch:{ Exception -> 0x0498 }
        if (r2 == 0) goto L_0x013c;
    L_0x0114:
        r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0498 }
        r2.<init>();	 Catch:{ Exception -> 0x0498 }
        r8 = "save preload file part ";
        r2.append(r8);	 Catch:{ Exception -> 0x0498 }
        r8 = r1.cacheFilePreload;	 Catch:{ Exception -> 0x0498 }
        r2.append(r8);	 Catch:{ Exception -> 0x0498 }
        r2.append(r3);	 Catch:{ Exception -> 0x0498 }
        r3 = r22.offset;	 Catch:{ Exception -> 0x0498 }
        r2.append(r3);	 Catch:{ Exception -> 0x0498 }
        r3 = " size ";
        r2.append(r3);	 Catch:{ Exception -> 0x0498 }
        r2.append(r10);	 Catch:{ Exception -> 0x0498 }
        r2 = r2.toString();	 Catch:{ Exception -> 0x0498 }
        org.telegram.messenger.FileLog.d(r2);	 Catch:{ Exception -> 0x0498 }
    L_0x013c:
        r2 = r1.preloadedBytesRanges;	 Catch:{ Exception -> 0x0498 }
        if (r2 != 0) goto L_0x0147;
    L_0x0140:
        r2 = new android.util.SparseArray;	 Catch:{ Exception -> 0x0498 }
        r2.<init>();	 Catch:{ Exception -> 0x0498 }
        r1.preloadedBytesRanges = r2;	 Catch:{ Exception -> 0x0498 }
    L_0x0147:
        r2 = r1.preloadedBytesRanges;	 Catch:{ Exception -> 0x0498 }
        r3 = r22.offset;	 Catch:{ Exception -> 0x0498 }
        r8 = new org.telegram.messenger.FileLoadOperation$PreloadRange;	 Catch:{ Exception -> 0x0498 }
        r11 = r1.preloadStreamFileOffset;	 Catch:{ Exception -> 0x0498 }
        r12 = r22.offset;	 Catch:{ Exception -> 0x0498 }
        r8.<init>(r11, r12, r10);	 Catch:{ Exception -> 0x0498 }
        r2.put(r3, r8);	 Catch:{ Exception -> 0x0498 }
        r2 = r1.totalPreloadedBytes;	 Catch:{ Exception -> 0x0498 }
        r2 = r2 + r10;
        r1.totalPreloadedBytes = r2;	 Catch:{ Exception -> 0x0498 }
        r2 = r1.preloadStreamFileOffset;	 Catch:{ Exception -> 0x0498 }
        r2 = r2 + r10;
        r1.preloadStreamFileOffset = r2;	 Catch:{ Exception -> 0x0498 }
        r2 = r1.moovFound;	 Catch:{ Exception -> 0x0498 }
        if (r2 != 0) goto L_0x01a7;
    L_0x0169:
        r2 = r1.nextAtomOffset;	 Catch:{ Exception -> 0x0498 }
        r3 = r22.offset;	 Catch:{ Exception -> 0x0498 }
        r0 = r1.findNextPreloadDownloadOffset(r2, r3, r0);	 Catch:{ Exception -> 0x0498 }
        if (r0 >= 0) goto L_0x019b;
    L_0x0175:
        r0 = r0 * -1;
        r2 = r1.nextPreloadDownloadOffset;	 Catch:{ Exception -> 0x0498 }
        r3 = r1.currentDownloadChunkSize;	 Catch:{ Exception -> 0x0498 }
        r2 = r2 + r3;
        r1.nextPreloadDownloadOffset = r2;	 Catch:{ Exception -> 0x0498 }
        r2 = r1.nextPreloadDownloadOffset;	 Catch:{ Exception -> 0x0498 }
        r3 = r1.totalBytesCount;	 Catch:{ Exception -> 0x0498 }
        r3 = r3 / r7;
        if (r2 >= r3) goto L_0x018f;
    L_0x0185:
        r2 = 1048576; // 0x100000 float:1.469368E-39 double:5.180654E-318;
        r2 = r2 + r0;
        r1.foundMoovSize = r2;	 Catch:{ Exception -> 0x0498 }
        r1.preloadNotRequestedBytesCount = r2;	 Catch:{ Exception -> 0x0498 }
        r1.moovFound = r4;	 Catch:{ Exception -> 0x0498 }
        goto L_0x0197;
    L_0x018f:
        r2 = 2097152; // 0x200000 float:2.938736E-39 double:1.0361308E-317;
        r1.foundMoovSize = r2;	 Catch:{ Exception -> 0x0498 }
        r1.preloadNotRequestedBytesCount = r2;	 Catch:{ Exception -> 0x0498 }
        r1.moovFound = r7;	 Catch:{ Exception -> 0x0498 }
    L_0x0197:
        r2 = -1;
        r1.nextPreloadDownloadOffset = r2;	 Catch:{ Exception -> 0x0498 }
        goto L_0x01a5;
    L_0x019b:
        r2 = r1.currentDownloadChunkSize;	 Catch:{ Exception -> 0x0498 }
        r2 = r0 / r2;
        r3 = r1.currentDownloadChunkSize;	 Catch:{ Exception -> 0x0498 }
        r2 = r2 * r3;
        r1.nextPreloadDownloadOffset = r2;	 Catch:{ Exception -> 0x0498 }
    L_0x01a5:
        r1.nextAtomOffset = r0;	 Catch:{ Exception -> 0x0498 }
    L_0x01a7:
        r0 = r1.preloadStream;	 Catch:{ Exception -> 0x0498 }
        r2 = r1.foundMoovSize;	 Catch:{ Exception -> 0x0498 }
        r0.writeInt(r2);	 Catch:{ Exception -> 0x0498 }
        r0 = r1.preloadStream;	 Catch:{ Exception -> 0x0498 }
        r2 = r1.nextPreloadDownloadOffset;	 Catch:{ Exception -> 0x0498 }
        r0.writeInt(r2);	 Catch:{ Exception -> 0x0498 }
        r0 = r1.preloadStream;	 Catch:{ Exception -> 0x0498 }
        r2 = r1.nextAtomOffset;	 Catch:{ Exception -> 0x0498 }
        r0.writeInt(r2);	 Catch:{ Exception -> 0x0498 }
        r0 = r1.preloadStreamFileOffset;	 Catch:{ Exception -> 0x0498 }
        r0 = r0 + r13;
        r1.preloadStreamFileOffset = r0;	 Catch:{ Exception -> 0x0498 }
        r0 = r1.nextPreloadDownloadOffset;	 Catch:{ Exception -> 0x0498 }
        if (r0 == 0) goto L_0x01dc;
    L_0x01c5:
        r0 = r1.moovFound;	 Catch:{ Exception -> 0x0498 }
        if (r0 == 0) goto L_0x01cd;
    L_0x01c9:
        r0 = r1.foundMoovSize;	 Catch:{ Exception -> 0x0498 }
        if (r0 < 0) goto L_0x01dc;
    L_0x01cd:
        r0 = r1.totalPreloadedBytes;	 Catch:{ Exception -> 0x0498 }
        r2 = 2097152; // 0x200000 float:2.938736E-39 double:1.0361308E-317;
        if (r0 > r2) goto L_0x01dc;
    L_0x01d3:
        r0 = r1.nextPreloadDownloadOffset;	 Catch:{ Exception -> 0x0498 }
        r2 = r1.totalBytesCount;	 Catch:{ Exception -> 0x0498 }
        if (r0 < r2) goto L_0x01da;
    L_0x01d9:
        goto L_0x01dc;
    L_0x01da:
        r0 = 0;
        goto L_0x01dd;
    L_0x01dc:
        r0 = 1;
    L_0x01dd:
        if (r0 == 0) goto L_0x01ed;
    L_0x01df:
        r2 = r1.preloadStream;	 Catch:{ Exception -> 0x0498 }
        r6 = 0;
        r2.seek(r6);	 Catch:{ Exception -> 0x0498 }
        r2 = r1.preloadStream;	 Catch:{ Exception -> 0x0498 }
        r2.write(r4);	 Catch:{ Exception -> 0x0498 }
        goto L_0x0421;
    L_0x01ed:
        r2 = r1.moovFound;	 Catch:{ Exception -> 0x0498 }
        if (r2 == 0) goto L_0x0421;
    L_0x01f1:
        r2 = r1.foundMoovSize;	 Catch:{ Exception -> 0x0498 }
        r3 = r1.currentDownloadChunkSize;	 Catch:{ Exception -> 0x0498 }
        r2 = r2 - r3;
        r1.foundMoovSize = r2;	 Catch:{ Exception -> 0x0498 }
        goto L_0x0421;
    L_0x01fa:
        r7 = r1.downloadedBytes;	 Catch:{ Exception -> 0x0498 }
        r7 = r7 + r10;
        r1.downloadedBytes = r7;	 Catch:{ Exception -> 0x0498 }
        r7 = r1.totalBytesCount;	 Catch:{ Exception -> 0x0498 }
        if (r7 <= 0) goto L_0x020d;
    L_0x0203:
        r7 = r1.downloadedBytes;	 Catch:{ Exception -> 0x0498 }
        r11 = r1.totalBytesCount;	 Catch:{ Exception -> 0x0498 }
        if (r7 < r11) goto L_0x020b;
    L_0x0209:
        r7 = 1;
        goto L_0x0229;
    L_0x020b:
        r7 = 0;
        goto L_0x0229;
    L_0x020d:
        r7 = r1.currentDownloadChunkSize;	 Catch:{ Exception -> 0x0498 }
        if (r10 != r7) goto L_0x0209;
    L_0x0211:
        r7 = r1.totalBytesCount;	 Catch:{ Exception -> 0x0498 }
        r11 = r1.downloadedBytes;	 Catch:{ Exception -> 0x0498 }
        if (r7 == r11) goto L_0x021e;
    L_0x0217:
        r7 = r1.downloadedBytes;	 Catch:{ Exception -> 0x0498 }
        r11 = r1.currentDownloadChunkSize;	 Catch:{ Exception -> 0x0498 }
        r7 = r7 % r11;
        if (r7 == 0) goto L_0x020b;
    L_0x021e:
        r7 = r1.totalBytesCount;	 Catch:{ Exception -> 0x0498 }
        if (r7 <= 0) goto L_0x0209;
    L_0x0222:
        r7 = r1.totalBytesCount;	 Catch:{ Exception -> 0x0498 }
        r11 = r1.downloadedBytes;	 Catch:{ Exception -> 0x0498 }
        if (r7 > r11) goto L_0x020b;
    L_0x0228:
        goto L_0x0209;
    L_0x0229:
        r11 = r1.key;	 Catch:{ Exception -> 0x0498 }
        if (r11 == 0) goto L_0x0252;
    L_0x022d:
        r14 = r0.buffer;	 Catch:{ Exception -> 0x0498 }
        r15 = r1.key;	 Catch:{ Exception -> 0x0498 }
        r11 = r1.iv;	 Catch:{ Exception -> 0x0498 }
        r17 = 0;
        r18 = 1;
        r19 = 0;
        r20 = r0.limit();	 Catch:{ Exception -> 0x0498 }
        r16 = r11;
        org.telegram.messenger.Utilities.aesIgeEncryption(r14, r15, r16, r17, r18, r19, r20);	 Catch:{ Exception -> 0x0498 }
        if (r7 == 0) goto L_0x0252;
    L_0x0244:
        r11 = r1.bytesCountPadding;	 Catch:{ Exception -> 0x0498 }
        if (r11 == 0) goto L_0x0252;
    L_0x0248:
        r11 = r0.limit();	 Catch:{ Exception -> 0x0498 }
        r12 = r1.bytesCountPadding;	 Catch:{ Exception -> 0x0498 }
        r11 = r11 - r12;
        r0.limit(r11);	 Catch:{ Exception -> 0x0498 }
    L_0x0252:
        r11 = r1.encryptFile;	 Catch:{ Exception -> 0x0498 }
        if (r11 == 0) goto L_0x0291;
    L_0x0256:
        r11 = r22.offset;	 Catch:{ Exception -> 0x0498 }
        r11 = r11 / 16;
        r12 = r1.encryptIv;	 Catch:{ Exception -> 0x0498 }
        r14 = 15;
        r15 = r11 & 255;
        r15 = (byte) r15;	 Catch:{ Exception -> 0x0498 }
        r12[r14] = r15;	 Catch:{ Exception -> 0x0498 }
        r12 = r1.encryptIv;	 Catch:{ Exception -> 0x0498 }
        r14 = 14;
        r15 = r11 >> 8;
        r15 = r15 & 255;
        r15 = (byte) r15;	 Catch:{ Exception -> 0x0498 }
        r12[r14] = r15;	 Catch:{ Exception -> 0x0498 }
        r12 = r1.encryptIv;	 Catch:{ Exception -> 0x0498 }
        r14 = 13;
        r15 = r11 >> 16;
        r15 = r15 & 255;
        r15 = (byte) r15;	 Catch:{ Exception -> 0x0498 }
        r12[r14] = r15;	 Catch:{ Exception -> 0x0498 }
        r12 = r1.encryptIv;	 Catch:{ Exception -> 0x0498 }
        r11 = r11 >> 24;
        r11 = r11 & 255;
        r11 = (byte) r11;	 Catch:{ Exception -> 0x0498 }
        r12[r13] = r11;	 Catch:{ Exception -> 0x0498 }
        r11 = r0.buffer;	 Catch:{ Exception -> 0x0498 }
        r12 = r1.encryptKey;	 Catch:{ Exception -> 0x0498 }
        r13 = r1.encryptIv;	 Catch:{ Exception -> 0x0498 }
        r14 = r0.limit();	 Catch:{ Exception -> 0x0498 }
        org.telegram.messenger.Utilities.aesCtrDecryption(r11, r12, r13, r5, r14);	 Catch:{ Exception -> 0x0498 }
    L_0x0291:
        r11 = r1.notLoadedBytesRanges;	 Catch:{ Exception -> 0x0498 }
        if (r11 == 0) goto L_0x02c3;
    L_0x0295:
        r11 = r1.fileOutputStream;	 Catch:{ Exception -> 0x0498 }
        r12 = r22.offset;	 Catch:{ Exception -> 0x0498 }
        r12 = (long) r12;	 Catch:{ Exception -> 0x0498 }
        r11.seek(r12);	 Catch:{ Exception -> 0x0498 }
        r11 = org.telegram.messenger.BuildVars.DEBUG_VERSION;	 Catch:{ Exception -> 0x0498 }
        if (r11 == 0) goto L_0x02c3;
    L_0x02a3:
        r11 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0498 }
        r11.<init>();	 Catch:{ Exception -> 0x0498 }
        r12 = "save file part ";
        r11.append(r12);	 Catch:{ Exception -> 0x0498 }
        r12 = r1.cacheFileFinal;	 Catch:{ Exception -> 0x0498 }
        r11.append(r12);	 Catch:{ Exception -> 0x0498 }
        r11.append(r3);	 Catch:{ Exception -> 0x0498 }
        r3 = r22.offset;	 Catch:{ Exception -> 0x0498 }
        r11.append(r3);	 Catch:{ Exception -> 0x0498 }
        r3 = r11.toString();	 Catch:{ Exception -> 0x0498 }
        org.telegram.messenger.FileLog.d(r3);	 Catch:{ Exception -> 0x0498 }
    L_0x02c3:
        r3 = r1.fileOutputStream;	 Catch:{ Exception -> 0x0498 }
        r3 = r3.getChannel();	 Catch:{ Exception -> 0x0498 }
        r0 = r0.buffer;	 Catch:{ Exception -> 0x0498 }
        r3.write(r0);	 Catch:{ Exception -> 0x0498 }
        r0 = r1.notLoadedBytesRanges;	 Catch:{ Exception -> 0x0498 }
        r3 = r22.offset;	 Catch:{ Exception -> 0x0498 }
        r11 = r22.offset;	 Catch:{ Exception -> 0x0498 }
        r11 = r11 + r10;
        r1.addPart(r0, r3, r11, r4);	 Catch:{ Exception -> 0x0498 }
        r0 = r1.isCdn;	 Catch:{ Exception -> 0x0498 }
        if (r0 == 0) goto L_0x03f1;
    L_0x02e0:
        r0 = r22.offset;	 Catch:{ Exception -> 0x0498 }
        r3 = 131072; // 0x20000 float:1.83671E-40 double:6.47582E-319;
        r0 = r0 / r3;
        r3 = r1.notCheckedCdnRanges;	 Catch:{ Exception -> 0x0498 }
        r3 = r3.size();	 Catch:{ Exception -> 0x0498 }
        r6 = 0;
    L_0x02ee:
        if (r6 >= r3) goto L_0x0309;
    L_0x02f0:
        r10 = r1.notCheckedCdnRanges;	 Catch:{ Exception -> 0x0498 }
        r10 = r10.get(r6);	 Catch:{ Exception -> 0x0498 }
        r10 = (org.telegram.messenger.FileLoadOperation.Range) r10;	 Catch:{ Exception -> 0x0498 }
        r11 = r10.start;	 Catch:{ Exception -> 0x0498 }
        if (r11 > r0) goto L_0x0306;
    L_0x02fe:
        r10 = r10.end;	 Catch:{ Exception -> 0x0498 }
        if (r0 > r10) goto L_0x0306;
    L_0x0304:
        r3 = 0;
        goto L_0x030a;
    L_0x0306:
        r6 = r6 + 1;
        goto L_0x02ee;
    L_0x0309:
        r3 = 1;
    L_0x030a:
        if (r3 != 0) goto L_0x03f1;
    L_0x030c:
        r3 = 131072; // 0x20000 float:1.83671E-40 double:6.47582E-319;
        r12 = r0 * r3;
        r6 = r1.notLoadedBytesRanges;	 Catch:{ Exception -> 0x0498 }
        r6 = r1.getDownloadedLengthFromOffsetInternal(r6, r12, r3);	 Catch:{ Exception -> 0x0498 }
        if (r6 == 0) goto L_0x03f1;
    L_0x0318:
        if (r6 == r3) goto L_0x0329;
    L_0x031a:
        r3 = r1.totalBytesCount;	 Catch:{ Exception -> 0x0498 }
        if (r3 <= 0) goto L_0x0323;
    L_0x031e:
        r3 = r1.totalBytesCount;	 Catch:{ Exception -> 0x0498 }
        r3 = r3 - r12;
        if (r6 == r3) goto L_0x0329;
    L_0x0323:
        r3 = r1.totalBytesCount;	 Catch:{ Exception -> 0x0498 }
        if (r3 > 0) goto L_0x03f1;
    L_0x0327:
        if (r7 == 0) goto L_0x03f1;
    L_0x0329:
        r3 = r1.cdnHashes;	 Catch:{ Exception -> 0x0498 }
        r3 = r3.get(r12);	 Catch:{ Exception -> 0x0498 }
        r3 = (org.telegram.tgnet.TLRPC.TL_fileHash) r3;	 Catch:{ Exception -> 0x0498 }
        r10 = r1.fileReadStream;	 Catch:{ Exception -> 0x0498 }
        if (r10 != 0) goto L_0x0346;
    L_0x0335:
        r10 = 131072; // 0x20000 float:1.83671E-40 double:6.47582E-319;
        r10 = new byte[r10];	 Catch:{ Exception -> 0x0498 }
        r1.cdnCheckBytes = r10;	 Catch:{ Exception -> 0x0498 }
        r10 = new java.io.RandomAccessFile;	 Catch:{ Exception -> 0x0498 }
        r11 = r1.cacheFileTemp;	 Catch:{ Exception -> 0x0498 }
        r13 = "r";
        r10.<init>(r11, r13);	 Catch:{ Exception -> 0x0498 }
        r1.fileReadStream = r10;	 Catch:{ Exception -> 0x0498 }
    L_0x0346:
        r10 = r1.fileReadStream;	 Catch:{ Exception -> 0x0498 }
        r13 = (long) r12;	 Catch:{ Exception -> 0x0498 }
        r10.seek(r13);	 Catch:{ Exception -> 0x0498 }
        r10 = r1.fileReadStream;	 Catch:{ Exception -> 0x0498 }
        r11 = r1.cdnCheckBytes;	 Catch:{ Exception -> 0x0498 }
        r10.readFully(r11, r5, r6);	 Catch:{ Exception -> 0x0498 }
        r10 = r1.cdnCheckBytes;	 Catch:{ Exception -> 0x0498 }
        r6 = org.telegram.messenger.Utilities.computeSHA256(r10, r5, r6);	 Catch:{ Exception -> 0x0498 }
        r3 = r3.hash;	 Catch:{ Exception -> 0x0498 }
        r3 = java.util.Arrays.equals(r6, r3);	 Catch:{ Exception -> 0x0498 }
        if (r3 != 0) goto L_0x03e5;
    L_0x0361:
        r0 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Exception -> 0x0498 }
        if (r0 == 0) goto L_0x03dc;
    L_0x0365:
        r0 = r1.location;	 Catch:{ Exception -> 0x0498 }
        if (r0 == 0) goto L_0x03b8;
    L_0x0369:
        r0 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0498 }
        r0.<init>();	 Catch:{ Exception -> 0x0498 }
        r3 = "invalid cdn hash ";
        r0.append(r3);	 Catch:{ Exception -> 0x0498 }
        r3 = r1.location;	 Catch:{ Exception -> 0x0498 }
        r0.append(r3);	 Catch:{ Exception -> 0x0498 }
        r0.append(r8);	 Catch:{ Exception -> 0x0498 }
        r3 = r1.location;	 Catch:{ Exception -> 0x0498 }
        r3 = r3.id;	 Catch:{ Exception -> 0x0498 }
        r0.append(r3);	 Catch:{ Exception -> 0x0498 }
        r0.append(r2);	 Catch:{ Exception -> 0x0498 }
        r2 = r1.location;	 Catch:{ Exception -> 0x0498 }
        r2 = r2.local_id;	 Catch:{ Exception -> 0x0498 }
        r0.append(r2);	 Catch:{ Exception -> 0x0498 }
        r2 = " access_hash = ";
        r0.append(r2);	 Catch:{ Exception -> 0x0498 }
        r2 = r1.location;	 Catch:{ Exception -> 0x0498 }
        r2 = r2.access_hash;	 Catch:{ Exception -> 0x0498 }
        r0.append(r2);	 Catch:{ Exception -> 0x0498 }
        r2 = " volume_id = ";
        r0.append(r2);	 Catch:{ Exception -> 0x0498 }
        r2 = r1.location;	 Catch:{ Exception -> 0x0498 }
        r2 = r2.volume_id;	 Catch:{ Exception -> 0x0498 }
        r0.append(r2);	 Catch:{ Exception -> 0x0498 }
        r2 = " secret = ";
        r0.append(r2);	 Catch:{ Exception -> 0x0498 }
        r2 = r1.location;	 Catch:{ Exception -> 0x0498 }
        r2 = r2.secret;	 Catch:{ Exception -> 0x0498 }
        r0.append(r2);	 Catch:{ Exception -> 0x0498 }
        r0 = r0.toString();	 Catch:{ Exception -> 0x0498 }
        org.telegram.messenger.FileLog.e(r0);	 Catch:{ Exception -> 0x0498 }
        goto L_0x03dc;
    L_0x03b8:
        r0 = r1.webLocation;	 Catch:{ Exception -> 0x0498 }
        if (r0 == 0) goto L_0x03dc;
    L_0x03bc:
        r0 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0498 }
        r0.<init>();	 Catch:{ Exception -> 0x0498 }
        r2 = "invalid cdn hash  ";
        r0.append(r2);	 Catch:{ Exception -> 0x0498 }
        r2 = r1.webLocation;	 Catch:{ Exception -> 0x0498 }
        r0.append(r2);	 Catch:{ Exception -> 0x0498 }
        r0.append(r8);	 Catch:{ Exception -> 0x0498 }
        r2 = r21.getFileName();	 Catch:{ Exception -> 0x0498 }
        r0.append(r2);	 Catch:{ Exception -> 0x0498 }
        r0 = r0.toString();	 Catch:{ Exception -> 0x0498 }
        org.telegram.messenger.FileLog.e(r0);	 Catch:{ Exception -> 0x0498 }
    L_0x03dc:
        r1.onFail(r5, r5);	 Catch:{ Exception -> 0x0498 }
        r0 = r1.cacheFileTemp;	 Catch:{ Exception -> 0x0498 }
        r0.delete();	 Catch:{ Exception -> 0x0498 }
        return r5;
    L_0x03e5:
        r2 = r1.cdnHashes;	 Catch:{ Exception -> 0x0498 }
        r2.remove(r12);	 Catch:{ Exception -> 0x0498 }
        r2 = r1.notCheckedCdnRanges;	 Catch:{ Exception -> 0x0498 }
        r3 = r0 + 1;
        r1.addPart(r2, r0, r3, r5);	 Catch:{ Exception -> 0x0498 }
    L_0x03f1:
        r0 = r1.fiv;	 Catch:{ Exception -> 0x0498 }
        if (r0 == 0) goto L_0x0403;
    L_0x03f5:
        r0 = r1.fiv;	 Catch:{ Exception -> 0x0498 }
        r2 = 0;
        r0.seek(r2);	 Catch:{ Exception -> 0x0498 }
        r0 = r1.fiv;	 Catch:{ Exception -> 0x0498 }
        r2 = r1.iv;	 Catch:{ Exception -> 0x0498 }
        r0.write(r2);	 Catch:{ Exception -> 0x0498 }
    L_0x0403:
        r0 = r1.totalBytesCount;	 Catch:{ Exception -> 0x0498 }
        if (r0 <= 0) goto L_0x0420;
    L_0x0407:
        r0 = r1.state;	 Catch:{ Exception -> 0x0498 }
        if (r0 != r4) goto L_0x0420;
    L_0x040b:
        r21.copyNotLoadedRanges();	 Catch:{ Exception -> 0x0498 }
        r0 = r1.delegate;	 Catch:{ Exception -> 0x0498 }
        r2 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r3 = r1.downloadedBytes;	 Catch:{ Exception -> 0x0498 }
        r3 = (float) r3;	 Catch:{ Exception -> 0x0498 }
        r6 = r1.totalBytesCount;	 Catch:{ Exception -> 0x0498 }
        r6 = (float) r6;	 Catch:{ Exception -> 0x0498 }
        r3 = r3 / r6;
        r2 = java.lang.Math.min(r2, r3);	 Catch:{ Exception -> 0x0498 }
        r0.didChangedLoadProgress(r1, r2);	 Catch:{ Exception -> 0x0498 }
    L_0x0420:
        r0 = r7;
    L_0x0421:
        r2 = 0;
    L_0x0422:
        r3 = r1.delayedRequestInfos;	 Catch:{ Exception -> 0x0498 }
        r3 = r3.size();	 Catch:{ Exception -> 0x0498 }
        if (r2 >= r3) goto L_0x0488;
    L_0x042a:
        r3 = r1.delayedRequestInfos;	 Catch:{ Exception -> 0x0498 }
        r3 = r3.get(r2);	 Catch:{ Exception -> 0x0498 }
        r3 = (org.telegram.messenger.FileLoadOperation.RequestInfo) r3;	 Catch:{ Exception -> 0x0498 }
        r6 = r1.notLoadedBytesRanges;	 Catch:{ Exception -> 0x0498 }
        if (r6 != 0) goto L_0x0442;
    L_0x0436:
        r6 = r1.downloadedBytes;	 Catch:{ Exception -> 0x0498 }
        r7 = r3.offset;	 Catch:{ Exception -> 0x0498 }
        if (r6 != r7) goto L_0x043f;
    L_0x043e:
        goto L_0x0442;
    L_0x043f:
        r2 = r2 + 1;
        goto L_0x0422;
    L_0x0442:
        r6 = r1.delayedRequestInfos;	 Catch:{ Exception -> 0x0498 }
        r6.remove(r2);	 Catch:{ Exception -> 0x0498 }
        r2 = r1.processRequestResult(r3, r9);	 Catch:{ Exception -> 0x0498 }
        if (r2 != 0) goto L_0x0488;
    L_0x044d:
        r2 = r3.response;	 Catch:{ Exception -> 0x0498 }
        if (r2 == 0) goto L_0x0461;
    L_0x0453:
        r2 = r3.response;	 Catch:{ Exception -> 0x0498 }
        r2.disableFree = r5;	 Catch:{ Exception -> 0x0498 }
        r2 = r3.response;	 Catch:{ Exception -> 0x0498 }
        r2.freeResources();	 Catch:{ Exception -> 0x0498 }
        goto L_0x0488;
    L_0x0461:
        r2 = r3.responseWeb;	 Catch:{ Exception -> 0x0498 }
        if (r2 == 0) goto L_0x0475;
    L_0x0467:
        r2 = r3.responseWeb;	 Catch:{ Exception -> 0x0498 }
        r2.disableFree = r5;	 Catch:{ Exception -> 0x0498 }
        r2 = r3.responseWeb;	 Catch:{ Exception -> 0x0498 }
        r2.freeResources();	 Catch:{ Exception -> 0x0498 }
        goto L_0x0488;
    L_0x0475:
        r2 = r3.responseCdn;	 Catch:{ Exception -> 0x0498 }
        if (r2 == 0) goto L_0x0488;
    L_0x047b:
        r2 = r3.responseCdn;	 Catch:{ Exception -> 0x0498 }
        r2.disableFree = r5;	 Catch:{ Exception -> 0x0498 }
        r2 = r3.responseCdn;	 Catch:{ Exception -> 0x0498 }
        r2.freeResources();	 Catch:{ Exception -> 0x0498 }
    L_0x0488:
        if (r0 == 0) goto L_0x048f;
    L_0x048a:
        r1.onFinishLoadingFile(r4);	 Catch:{ Exception -> 0x0498 }
        goto L_0x0593;
    L_0x048f:
        r21.startDownloadRequest();	 Catch:{ Exception -> 0x0498 }
        goto L_0x0593;
    L_0x0494:
        r1.onFinishLoadingFile(r4);	 Catch:{ Exception -> 0x0498 }
        return r5;
    L_0x0498:
        r0 = move-exception;
        r1.onFail(r5, r5);
        org.telegram.messenger.FileLog.e(r0);
        goto L_0x0593;
    L_0x04a1:
        r3 = r0.text;
        r6 = "FILE_MIGRATE_";
        r3 = r3.contains(r6);
        if (r3 == 0) goto L_0x04d9;
    L_0x04ab:
        r0 = r0.text;
        r2 = "";
        r0 = r0.replace(r6, r2);
        r3 = new java.util.Scanner;
        r3.<init>(r0);
        r3.useDelimiter(r2);
        r0 = r3.nextInt();	 Catch:{ Exception -> 0x04c3 }
        r9 = java.lang.Integer.valueOf(r0);	 Catch:{ Exception -> 0x04c3 }
    L_0x04c3:
        if (r9 != 0) goto L_0x04ca;
    L_0x04c5:
        r1.onFail(r5, r5);
        goto L_0x0593;
    L_0x04ca:
        r0 = r9.intValue();
        r1.datacenterId = r0;
        r1.downloadedBytes = r5;
        r1.requestedBytesCount = r5;
        r21.startDownloadRequest();
        goto L_0x0593;
    L_0x04d9:
        r3 = r0.text;
        r6 = "OFFSET_INVALID";
        r3 = r3.contains(r6);
        if (r3 == 0) goto L_0x04fe;
    L_0x04e3:
        r0 = r1.downloadedBytes;
        r2 = r1.currentDownloadChunkSize;
        r0 = r0 % r2;
        if (r0 != 0) goto L_0x04f9;
    L_0x04ea:
        r1.onFinishLoadingFile(r4);	 Catch:{ Exception -> 0x04ef }
        goto L_0x0593;
    L_0x04ef:
        r0 = move-exception;
        r2 = r0;
        org.telegram.messenger.FileLog.e(r2);
        r1.onFail(r5, r5);
        goto L_0x0593;
    L_0x04f9:
        r1.onFail(r5, r5);
        goto L_0x0593;
    L_0x04fe:
        r3 = r0.text;
        r4 = "RETRY_LIMIT";
        r3 = r3.contains(r4);
        if (r3 == 0) goto L_0x050d;
    L_0x0508:
        r1.onFail(r5, r7);
        goto L_0x0593;
    L_0x050d:
        r3 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r3 == 0) goto L_0x0590;
    L_0x0511:
        r3 = r1.location;
        r4 = " ";
        if (r3 == 0) goto L_0x0569;
    L_0x0517:
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r0 = r0.text;
        r3.append(r0);
        r3.append(r4);
        r0 = r1.location;
        r3.append(r0);
        r3.append(r8);
        r0 = r1.location;
        r6 = r0.id;
        r3.append(r6);
        r3.append(r2);
        r0 = r1.location;
        r0 = r0.local_id;
        r3.append(r0);
        r0 = " access_hash = ";
        r3.append(r0);
        r0 = r1.location;
        r6 = r0.access_hash;
        r3.append(r6);
        r0 = " volume_id = ";
        r3.append(r0);
        r0 = r1.location;
        r6 = r0.volume_id;
        r3.append(r6);
        r0 = " secret = ";
        r3.append(r0);
        r0 = r1.location;
        r6 = r0.secret;
        r3.append(r6);
        r0 = r3.toString();
        org.telegram.messenger.FileLog.e(r0);
        goto L_0x0590;
    L_0x0569:
        r2 = r1.webLocation;
        if (r2 == 0) goto L_0x0590;
    L_0x056d:
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r0 = r0.text;
        r2.append(r0);
        r2.append(r4);
        r0 = r1.webLocation;
        r2.append(r0);
        r2.append(r8);
        r0 = r21.getFileName();
        r2.append(r0);
        r0 = r2.toString();
        org.telegram.messenger.FileLog.e(r0);
    L_0x0590:
        r1.onFail(r5, r5);
    L_0x0593:
        return r5;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.FileLoadOperation.processRequestResult(org.telegram.messenger.FileLoadOperation$RequestInfo, org.telegram.tgnet.TLRPC$TL_error):boolean");
    }

    /* Access modifiers changed, original: protected */
    public void onFail(boolean z, int i) {
        cleanup();
        this.state = 2;
        if (z) {
            Utilities.stageQueue.postRunnable(new -$$Lambda$FileLoadOperation$9spswKeKfAcOLWXDEy_zgCWBTtA(this, i));
        } else {
            this.delegate.didFailedLoadingFile(this, i);
        }
    }

    public /* synthetic */ void lambda$onFail$9$FileLoadOperation(int i) {
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
        if (!this.paused && this.state == 1 && ((this.nextPartWasPreloaded || this.requestInfos.size() + this.delayedRequestInfos.size() < this.currentMaxDownloadRequests) && (!this.isPreloadVideoOperation || (this.requestedBytesCount <= 2097152 && (this.moovFound == 0 || this.requestInfos.size() <= 0))))) {
            int max = (this.nextPartWasPreloaded || ((this.isPreloadVideoOperation && this.moovFound == 0) || this.totalBytesCount <= 0)) ? 1 : Math.max(0, this.currentMaxDownloadRequests - this.requestInfos.size());
            int i = 0;
            while (i < max) {
                int size;
                int i2;
                int i3;
                int i4;
                if (!this.isPreloadVideoOperation) {
                    ArrayList arrayList = this.notRequestedBytesRanges;
                    if (arrayList != null) {
                        size = arrayList.size();
                        i3 = Integer.MAX_VALUE;
                        int i5 = Integer.MAX_VALUE;
                        for (i4 = 0; i4 < size; i4++) {
                            Range range = (Range) this.notRequestedBytesRanges.get(i4);
                            if (this.streamStartOffset != 0) {
                                if (range.start <= this.streamStartOffset) {
                                    int access$000 = range.end;
                                    int i6 = this.streamStartOffset;
                                    if (access$000 > i6) {
                                        i3 = i6;
                                        i5 = Integer.MAX_VALUE;
                                        break;
                                    }
                                }
                                if (this.streamStartOffset < range.start && range.start < r10) {
                                    i3 = range.start;
                                }
                            }
                            i5 = Math.min(i5, range.start);
                        }
                        if (i3 == Integer.MAX_VALUE) {
                            if (i5 == Integer.MAX_VALUE) {
                                break;
                            }
                            size = i5;
                        } else {
                            size = i3;
                        }
                    } else {
                        size = this.requestedBytesCount;
                    }
                } else if (this.moovFound == 0 || this.preloadNotRequestedBytesCount > 0) {
                    size = this.nextPreloadDownloadOffset;
                    if (size == -1) {
                        Object obj;
                        i2 = 0;
                        for (size = (2097152 / this.currentDownloadChunkSize) + 2; size != 0; size--) {
                            if (this.requestedPreloadedBytesRanges.get(i2, 0) == 0) {
                                obj = 1;
                                break;
                            }
                            i4 = this.currentDownloadChunkSize;
                            i2 += i4;
                            i3 = this.totalBytesCount;
                            if (i2 > i3) {
                                break;
                            }
                            if (this.moovFound == 2 && i2 == i4 * 8) {
                                i2 = ((i3 - 1048576) / i4) * i4;
                            }
                        }
                        obj = null;
                        if (obj == null && this.requestInfos.isEmpty()) {
                            onFinishLoadingFile(false);
                        }
                        size = i2;
                    }
                    if (this.requestedPreloadedBytesRanges == null) {
                        this.requestedPreloadedBytesRanges = new SparseIntArray();
                    }
                    this.requestedPreloadedBytesRanges.put(size, 1);
                    if (BuildVars.DEBUG_VERSION) {
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("start next preload from ");
                        stringBuilder.append(size);
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
                if (!this.isPreloadVideoOperation) {
                    ArrayList arrayList2 = this.notRequestedBytesRanges;
                    if (arrayList2 != null) {
                        addPart(arrayList2, size, this.currentDownloadChunkSize + size, false);
                    }
                }
                i2 = this.totalBytesCount;
                if (i2 > 0 && size >= i2) {
                    break;
                }
                TLObject tL_upload_getCdnFile;
                i2 = this.totalBytesCount;
                boolean z = i2 <= 0 || i == max - 1 || (i2 > 0 && this.currentDownloadChunkSize + size >= i2);
                int i7 = this.requestsCount % 2 == 0 ? 2 : 65538;
                int i8 = this.isForceRequest ? 32 : 0;
                if (!(this.webLocation instanceof TL_inputWebFileGeoPointLocation)) {
                    i8 |= 2;
                }
                if (this.isCdn) {
                    tL_upload_getCdnFile = new TL_upload_getCdnFile();
                    tL_upload_getCdnFile.file_token = this.cdnToken;
                    tL_upload_getCdnFile.offset = size;
                    tL_upload_getCdnFile.limit = this.currentDownloadChunkSize;
                    i8 |= 1;
                } else if (this.webLocation != null) {
                    tL_upload_getCdnFile = new TL_upload_getWebFile();
                    tL_upload_getCdnFile.location = this.webLocation;
                    tL_upload_getCdnFile.offset = size;
                    tL_upload_getCdnFile.limit = this.currentDownloadChunkSize;
                } else {
                    tL_upload_getCdnFile = new TL_upload_getFile();
                    tL_upload_getCdnFile.location = this.location;
                    tL_upload_getCdnFile.offset = size;
                    tL_upload_getCdnFile.limit = this.currentDownloadChunkSize;
                }
                int i9 = i8;
                TLObject tLObject = tL_upload_getCdnFile;
                this.requestedBytesCount += this.currentDownloadChunkSize;
                RequestInfo requestInfo = new RequestInfo();
                this.requestInfos.add(requestInfo);
                requestInfo.offset = size;
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
                                Utilities.stageQueue.postRunnable(new -$$Lambda$FileLoadOperation$CkXyQ6ScRYNY6TMd6qRPWNLnxAA(this, requestInfo));
                            } catch (Exception unused) {
                            }
                            i++;
                        }
                    }
                }
                requestInfo.requestToken = ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLObject, new -$$Lambda$FileLoadOperation$pLwgbN71WnWYsTs7i_Ie2IK-Sjs(this, requestInfo, tLObject), null, null, i9, this.isCdn ? this.cdnDatacenterId : this.datacenterId, i7, z);
                this.requestsCount++;
                i++;
            }
        }
    }

    public /* synthetic */ void lambda$startDownloadRequest$10$FileLoadOperation(RequestInfo requestInfo) {
        processRequestResult(requestInfo, null);
        requestInfo.response.freeResources();
    }

    public /* synthetic */ void lambda$startDownloadRequest$12$FileLoadOperation(RequestInfo requestInfo, TLObject tLObject, TLObject tLObject2, TL_error tL_error) {
        if (this.requestInfos.contains(requestInfo)) {
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
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_upload_reuploadCdnFile, new -$$Lambda$FileLoadOperation$_RANhbuGWnPnpbUnhksdq4cjW2c(this, requestInfo), null, null, 0, this.datacenterId, 1, true);
            }
        }
    }

    public /* synthetic */ void lambda$null$11$FileLoadOperation(RequestInfo requestInfo, TLObject tLObject, TL_error tL_error) {
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
