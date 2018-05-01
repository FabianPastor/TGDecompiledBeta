package org.telegram.messenger;

import android.util.SparseArray;
import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import org.telegram.messenger.exoplayer2.util.MimeTypes;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.InputFileLocation;
import org.telegram.tgnet.TLRPC.TL_document;
import org.telegram.tgnet.TLRPC.TL_documentEncrypted;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_fileEncryptedLocation;
import org.telegram.tgnet.TLRPC.TL_fileHash;
import org.telegram.tgnet.TLRPC.TL_fileLocation;
import org.telegram.tgnet.TLRPC.TL_inputDocumentFileLocation;
import org.telegram.tgnet.TLRPC.TL_inputEncryptedFileLocation;
import org.telegram.tgnet.TLRPC.TL_inputFileLocation;
import org.telegram.tgnet.TLRPC.TL_inputWebFileLocation;
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
import org.telegram.tgnet.TLRPC.TL_webDocument;
import org.telegram.tgnet.TLRPC.Vector;

public class FileLoadOperation {
    private static final int bigFileSizeFrom = 1048576;
    private static final int cdnChunkCheckSize = 131072;
    private static final int downloadChunkSize = 32768;
    private static final int downloadChunkSizeBig = 131072;
    private static final int maxCdnParts = 12288;
    private static final int maxDownloadRequests = 4;
    private static final int maxDownloadRequestsBig = 4;
    private static final int stateDownloading = 1;
    private static final int stateFailed = 2;
    private static final int stateFinished = 3;
    private static final int stateIdle = 0;
    private boolean allowDisordererFileSave;
    private int bytesCountPadding;
    private File cacheFileFinal;
    private File cacheFileParts;
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
    private int datacenter_id;
    private ArrayList<RequestInfo> delayedRequestInfos;
    private FileLoadOperationDelegate delegate;
    private volatile int downloadedBytes;
    private boolean encryptFile;
    private byte[] encryptIv;
    private byte[] encryptKey;
    private String ext;
    private RandomAccessFile fileOutputStream;
    private RandomAccessFile filePartsStream;
    private RandomAccessFile fileReadStream;
    private RandomAccessFile fiv;
    private boolean isCdn;
    private boolean isForceRequest;
    private byte[] iv;
    private byte[] key;
    private InputFileLocation location;
    private ArrayList<Range> notCheckedCdnRanges;
    private ArrayList<Range> notLoadedBytesRanges;
    private volatile ArrayList<Range> notLoadedBytesRangesCopy;
    private ArrayList<Range> notRequestedBytesRanges;
    private volatile boolean paused;
    private int renameRetryCount;
    private ArrayList<RequestInfo> requestInfos;
    private int requestedBytesCount;
    private boolean requestingCdnOffsets;
    private int requestsCount;
    private boolean reuploadingCdn;
    private boolean started;
    private volatile int state;
    private File storePath;
    private ArrayList<FileStreamLoadOperation> streamListeners;
    private int streamStartOffset;
    private File tempPath;
    private int totalBytesCount;
    private TL_inputWebFileLocation webLocation;

    /* renamed from: org.telegram.messenger.FileLoadOperation$4 */
    class C01534 implements Runnable {
        C01534() {
        }

        public void run() {
            FileLoadOperation.this.paused = true;
        }
    }

    /* renamed from: org.telegram.messenger.FileLoadOperation$6 */
    class C01556 implements Runnable {
        C01556() {
        }

        public void run() {
            FileLoadOperation.this.startDownloadRequest();
        }
    }

    /* renamed from: org.telegram.messenger.FileLoadOperation$7 */
    class C01567 implements Runnable {
        C01567() {
        }

        public void run() {
            /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
            /*
            r3 = this;
            r0 = org.telegram.messenger.FileLoadOperation.this;
            r0 = r0.totalBytesCount;
            if (r0 == 0) goto L_0x0024;
        L_0x0008:
            r0 = org.telegram.messenger.FileLoadOperation.this;
            r0 = r0.downloadedBytes;
            r1 = org.telegram.messenger.FileLoadOperation.this;
            r1 = r1.totalBytesCount;
            if (r0 != r1) goto L_0x0024;
        L_0x0016:
            r0 = 0;
            r1 = org.telegram.messenger.FileLoadOperation.this;	 Catch:{ Exception -> 0x001d }
            r1.onFinishLoadingFile(r0);	 Catch:{ Exception -> 0x001d }
            goto L_0x0029;
        L_0x001d:
            r1 = org.telegram.messenger.FileLoadOperation.this;
            r2 = 1;
            r1.onFail(r2, r0);
            goto L_0x0029;
        L_0x0024:
            r0 = org.telegram.messenger.FileLoadOperation.this;
            r0.startDownloadRequest();
        L_0x0029:
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.FileLoadOperation.7.run():void");
        }
    }

    /* renamed from: org.telegram.messenger.FileLoadOperation$8 */
    class C01578 implements Runnable {
        C01578() {
        }

        public void run() {
            if (FileLoadOperation.this.state != 3) {
                if (FileLoadOperation.this.state != 2) {
                    if (FileLoadOperation.this.requestInfos != null) {
                        for (int i = 0; i < FileLoadOperation.this.requestInfos.size(); i++) {
                            RequestInfo requestInfo = (RequestInfo) FileLoadOperation.this.requestInfos.get(i);
                            if (requestInfo.requestToken != 0) {
                                ConnectionsManager.getInstance(FileLoadOperation.this.currentAccount).cancelRequest(requestInfo.requestToken, true);
                            }
                        }
                    }
                    FileLoadOperation.this.onFail(false, 1);
                }
            }
        }
    }

    public interface FileLoadOperationDelegate {
        void didChangedLoadProgress(FileLoadOperation fileLoadOperation, float f);

        void didFailedLoadingFile(FileLoadOperation fileLoadOperation, int i);

        void didFinishLoadingFile(FileLoadOperation fileLoadOperation, File file);
    }

    public static class Range {
        private int end;
        private int start;

        private Range(int i, int i2) {
            this.start = i;
            this.end = i2;
        }
    }

    private static class RequestInfo {
        private int offset;
        private int requestToken;
        private TL_upload_file response;
        private TL_upload_cdnFile responseCdn;
        private TL_upload_webFile responseWeb;

        private RequestInfo() {
        }
    }

    public FileLoadOperation(FileLocation fileLocation, String str, int i) {
        this.state = 0;
        if (fileLocation instanceof TL_fileEncryptedLocation) {
            this.location = new TL_inputEncryptedFileLocation();
            this.location.id = fileLocation.volume_id;
            this.location.volume_id = fileLocation.volume_id;
            this.location.access_hash = fileLocation.secret;
            this.location.local_id = fileLocation.local_id;
            this.iv = new byte[32];
            System.arraycopy(fileLocation.iv, 0, this.iv, 0, this.iv.length);
            this.key = fileLocation.key;
            this.datacenter_id = fileLocation.dc_id;
        } else if (fileLocation instanceof TL_fileLocation) {
            this.location = new TL_inputFileLocation();
            this.location.volume_id = fileLocation.volume_id;
            this.location.secret = fileLocation.secret;
            this.location.local_id = fileLocation.local_id;
            this.datacenter_id = fileLocation.dc_id;
            this.allowDisordererFileSave = true;
        }
        this.currentType = 16777216;
        this.totalBytesCount = i;
        if (str == null) {
            str = "jpg";
        }
        this.ext = str;
    }

    public FileLoadOperation(TL_webDocument tL_webDocument) {
        this.state = 0;
        this.webLocation = new TL_inputWebFileLocation();
        this.webLocation.url = tL_webDocument.url;
        this.webLocation.access_hash = tL_webDocument.access_hash;
        this.totalBytesCount = tL_webDocument.size;
        this.datacenter_id = tL_webDocument.dc_id;
        String extensionByMime = FileLoader.getExtensionByMime(tL_webDocument.mime_type);
        if (tL_webDocument.mime_type.startsWith("image/")) {
            this.currentType = 16777216;
        } else if (tL_webDocument.mime_type.equals("audio/ogg")) {
            this.currentType = ConnectionsManager.FileTypeAudio;
        } else if (tL_webDocument.mime_type.startsWith("video/")) {
            this.currentType = ConnectionsManager.FileTypeVideo;
        } else {
            this.currentType = ConnectionsManager.FileTypeFile;
        }
        this.allowDisordererFileSave = true;
        this.ext = ImageLoader.getHttpUrlExtension(tL_webDocument.url, extensionByMime);
    }

    public FileLoadOperation(Document document) {
        this.state = 0;
        try {
            int lastIndexOf;
            if (document instanceof TL_documentEncrypted) {
                this.location = new TL_inputEncryptedFileLocation();
                this.location.id = document.id;
                this.location.access_hash = document.access_hash;
                this.datacenter_id = document.dc_id;
                this.iv = new byte[32];
                System.arraycopy(document.iv, 0, this.iv, 0, this.iv.length);
                this.key = document.key;
            } else if (document instanceof TL_document) {
                this.location = new TL_inputDocumentFileLocation();
                this.location.id = document.id;
                this.location.access_hash = document.access_hash;
                this.datacenter_id = document.dc_id;
                this.allowDisordererFileSave = true;
            }
            this.totalBytesCount = document.size;
            if (!(this.key == null || this.totalBytesCount % 16 == 0)) {
                this.bytesCountPadding = 16 - (this.totalBytesCount % 16);
                this.totalBytesCount += this.bytesCountPadding;
            }
            this.ext = FileLoader.getDocumentFileName(document);
            int i = -1;
            if (this.ext != null) {
                lastIndexOf = this.ext.lastIndexOf(46);
                if (lastIndexOf != -1) {
                    this.ext = this.ext.substring(lastIndexOf);
                    if ("audio/ogg".equals(document.mime_type)) {
                        this.currentType = ConnectionsManager.FileTypeAudio;
                    } else if (MimeTypes.VIDEO_MP4.equals(document.mime_type)) {
                        this.currentType = ConnectionsManager.FileTypeFile;
                    } else {
                        this.currentType = ConnectionsManager.FileTypeVideo;
                    }
                    if (this.ext.length() > 1) {
                    }
                    if (document.mime_type == null) {
                        document = document.mime_type;
                        lastIndexOf = document.hashCode();
                        if (lastIndexOf == 187091926) {
                            if (lastIndexOf != NUM) {
                                if (document.equals(MimeTypes.VIDEO_MP4) != null) {
                                    i = 0;
                                }
                            }
                        } else if (document.equals("audio/ogg") != null) {
                            i = true;
                        }
                        switch (i) {
                            case 0:
                                this.ext = ".mp4";
                                return;
                            case 1:
                                this.ext = ".ogg";
                                return;
                            default:
                                this.ext = TtmlNode.ANONYMOUS_REGION_ID;
                                return;
                        }
                    }
                    this.ext = TtmlNode.ANONYMOUS_REGION_ID;
                    return;
                }
            }
            this.ext = TtmlNode.ANONYMOUS_REGION_ID;
            if ("audio/ogg".equals(document.mime_type)) {
                this.currentType = ConnectionsManager.FileTypeAudio;
            } else if (MimeTypes.VIDEO_MP4.equals(document.mime_type)) {
                this.currentType = ConnectionsManager.FileTypeFile;
            } else {
                this.currentType = ConnectionsManager.FileTypeVideo;
            }
            if (this.ext.length() > 1) {
                if (document.mime_type == null) {
                    this.ext = TtmlNode.ANONYMOUS_REGION_ID;
                    return;
                }
                document = document.mime_type;
                lastIndexOf = document.hashCode();
                if (lastIndexOf == 187091926) {
                    if (document.equals("audio/ogg") != null) {
                        i = true;
                    }
                } else if (lastIndexOf != NUM) {
                    if (document.equals(MimeTypes.VIDEO_MP4) != null) {
                        i = 0;
                    }
                }
                switch (i) {
                    case 0:
                        this.ext = ".mp4";
                        return;
                    case 1:
                        this.ext = ".ogg";
                        return;
                    default:
                        this.ext = TtmlNode.ANONYMOUS_REGION_ID;
                        return;
                }
            }
        } catch (Throwable e) {
            FileLog.m3e(e);
            onFail(true, 0);
        }
    }

    public void setEncryptFile(boolean z) {
        this.encryptFile = z;
        if (this.encryptFile) {
            this.allowDisordererFileSave = false;
        }
    }

    public void setForceRequest(boolean z) {
        this.isForceRequest = z;
    }

    public boolean isForceRequest() {
        return this.isForceRequest;
    }

    public void setPaths(int i, File file, File file2) {
        this.storePath = file;
        this.tempPath = file2;
        this.currentAccount = i;
    }

    public boolean wasStarted() {
        return this.started;
    }

    public int getCurrentType() {
        return this.currentType;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void removePart(ArrayList<Range> arrayList, int i, int i2) {
        if (arrayList != null) {
            if (i2 >= i) {
                Object obj;
                int size = arrayList.size();
                int i3 = 0;
                while (true) {
                    obj = 1;
                    if (i3 >= size) {
                        break;
                    }
                    Range range = (Range) arrayList.get(i3);
                    if (i == range.end) {
                        break;
                    } else if (i2 == range.start) {
                        break;
                    } else {
                        i3++;
                    }
                    if (obj == null) {
                        arrayList.add(new Range(i, i2));
                    }
                }
                obj = null;
                if (obj == null) {
                    arrayList.add(new Range(i, i2));
                }
            }
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void addPart(ArrayList<Range> arrayList, int i, int i2, boolean z) {
        if (arrayList != null) {
            if (i2 >= i) {
                int i3;
                Range range;
                int size = arrayList.size();
                int i4 = 0;
                int i5 = 0;
                while (true) {
                    i3 = 1;
                    if (i5 >= size) {
                        break;
                    }
                    Range range2 = (Range) arrayList.get(i5);
                    if (i <= range2.start) {
                        if (i2 >= range2.end) {
                            break;
                        } else if (i2 > range2.start) {
                            break;
                        }
                    } else if (i2 < range2.end) {
                        break;
                    } else if (i < range2.end) {
                        break;
                    }
                    i5++;
                    if (z) {
                        if (i3 != 0) {
                            try {
                                this.filePartsStream.seek(0);
                                i = arrayList.size();
                                this.filePartsStream.writeInt(i);
                                for (i2 = 0; i2 < i; i2++) {
                                    range = (Range) arrayList.get(i2);
                                    this.filePartsStream.writeInt(range.start);
                                    this.filePartsStream.writeInt(range.end);
                                }
                            } catch (Throwable e) {
                                FileLog.m3e(e);
                            }
                            if (this.streamListeners != null) {
                                arrayList = this.streamListeners.size();
                                while (i4 < arrayList) {
                                    ((FileStreamLoadOperation) this.streamListeners.get(i4)).newDataAvailable();
                                    i4++;
                                }
                            }
                        } else if (BuildVars.LOGS_ENABLED != null) {
                            arrayList = new StringBuilder();
                            arrayList.append(this.cacheFileFinal);
                            arrayList.append(" downloaded duplicate file part ");
                            arrayList.append(i);
                            arrayList.append(" - ");
                            arrayList.append(i2);
                            FileLog.m1e(arrayList.toString());
                        }
                    }
                }
                arrayList.remove(i5);
                if (z) {
                    if (i3 != 0) {
                        this.filePartsStream.seek(0);
                        i = arrayList.size();
                        this.filePartsStream.writeInt(i);
                        for (i2 = 0; i2 < i; i2++) {
                            range = (Range) arrayList.get(i2);
                            this.filePartsStream.writeInt(range.start);
                            this.filePartsStream.writeInt(range.end);
                        }
                        if (this.streamListeners != null) {
                            arrayList = this.streamListeners.size();
                            while (i4 < arrayList) {
                                ((FileStreamLoadOperation) this.streamListeners.get(i4)).newDataAvailable();
                                i4++;
                            }
                        }
                    } else if (BuildVars.LOGS_ENABLED != null) {
                        arrayList = new StringBuilder();
                        arrayList.append(this.cacheFileFinal);
                        arrayList.append(" downloaded duplicate file part ");
                        arrayList.append(i);
                        arrayList.append(" - ");
                        arrayList.append(i2);
                        FileLog.m1e(arrayList.toString());
                    }
                }
            }
        }
    }

    protected File getCurrentFile() {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final File[] fileArr = new File[1];
        Utilities.stageQueue.postRunnable(new Runnable() {
            public void run() {
                if (FileLoadOperation.this.state == 3) {
                    fileArr[0] = FileLoadOperation.this.cacheFileFinal;
                } else {
                    fileArr[0] = FileLoadOperation.this.cacheFileTemp;
                }
                countDownLatch.countDown();
            }
        });
        try {
            countDownLatch.await();
        } catch (Throwable e) {
            FileLog.m3e(e);
        }
        return fileArr[0];
    }

    private int getDownloadedLengthFromOffsetInternal(ArrayList<Range> arrayList, int i, int i2) {
        if (!(arrayList == null || this.state == 3)) {
            if (!arrayList.isEmpty()) {
                int size = arrayList.size();
                int i3 = i2;
                Range range = null;
                for (int i4 = 0; i4 < size; i4++) {
                    Range range2 = (Range) arrayList.get(i4);
                    if (i <= range2.start && (range == null || range2.start < range.start)) {
                        range = range2;
                    }
                    if (range2.start <= i && range2.end > i) {
                        i3 = 0;
                    }
                }
                if (i3 == 0) {
                    return 0;
                }
                if (range != null) {
                    return Math.min(i2, range.start - i);
                }
                return Math.min(i2, Math.max(this.totalBytesCount - i, 0));
            }
        }
        if (this.downloadedBytes == null) {
            return i2;
        }
        return Math.min(i2, Math.max(this.downloadedBytes - i, 0));
    }

    protected float getDownloadedLengthFromOffset(float f) {
        ArrayList arrayList = this.notLoadedBytesRangesCopy;
        if (this.totalBytesCount != 0) {
            if (arrayList != null) {
                return f + (((float) getDownloadedLengthFromOffsetInternal(arrayList, (int) (((float) this.totalBytesCount) * f), this.totalBytesCount)) / ((float) this.totalBytesCount));
            }
        }
        return 0.0f;
    }

    protected int getDownloadedLengthFromOffset(int i, int i2) {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        int[] iArr = new int[1];
        final int[] iArr2 = iArr;
        final int i3 = i;
        final int i4 = i2;
        final CountDownLatch countDownLatch2 = countDownLatch;
        Utilities.stageQueue.postRunnable(new Runnable() {
            public void run() {
                iArr2[0] = FileLoadOperation.this.getDownloadedLengthFromOffsetInternal(FileLoadOperation.this.notLoadedBytesRanges, i3, i4);
                countDownLatch2.countDown();
            }
        });
        try {
            countDownLatch.await();
        } catch (Throwable e) {
            FileLog.m3e(e);
        }
        return iArr[0];
    }

    public String getFileName() {
        if (this.location != null) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(this.location.volume_id);
            stringBuilder.append("_");
            stringBuilder.append(this.location.local_id);
            stringBuilder.append(".");
            stringBuilder.append(this.ext);
            return stringBuilder.toString();
        }
        stringBuilder = new StringBuilder();
        stringBuilder.append(Utilities.MD5(this.webLocation.url));
        stringBuilder.append(".");
        stringBuilder.append(this.ext);
        return stringBuilder.toString();
    }

    protected void removeStreamListener(final FileStreamLoadOperation fileStreamLoadOperation) {
        Utilities.stageQueue.postRunnable(new Runnable() {
            public void run() {
                if (FileLoadOperation.this.streamListeners != null) {
                    FileLoadOperation.this.streamListeners.remove(fileStreamLoadOperation);
                }
            }
        });
    }

    private void copytNotLoadedRanges() {
        if (this.notLoadedBytesRanges != null) {
            this.notLoadedBytesRangesCopy = new ArrayList(this.notLoadedBytesRanges);
        }
    }

    public void pause() {
        if (this.state == 1) {
            Utilities.stageQueue.postRunnable(new C01534());
        }
    }

    public boolean start() {
        return start(null, 0);
    }

    public boolean start(org.telegram.messenger.FileStreamLoadOperation r19, int r20) {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
        /*
        r18 = this;
        r1 = r18;
        r2 = r19;
        r3 = r20;
        r4 = r1.currentDownloadChunkSize;
        if (r4 != 0) goto L_0x001d;
    L_0x000a:
        r4 = r1.totalBytesCount;
        r5 = 1048576; // 0x100000 float:1.469368E-39 double:5.180654E-318;
        if (r4 < r5) goto L_0x0013;
    L_0x0010:
        r4 = 131072; // 0x20000 float:1.83671E-40 double:6.47582E-319;
        goto L_0x0016;
    L_0x0013:
        r4 = 32768; // 0x8000 float:4.5918E-41 double:1.61895E-319;
    L_0x0016:
        r1.currentDownloadChunkSize = r4;
        r4 = r1.totalBytesCount;
        r4 = 4;
        r1.currentMaxDownloadRequests = r4;
    L_0x001d:
        r4 = r1.state;
        r5 = 1;
        r6 = 0;
        if (r4 == 0) goto L_0x0025;
    L_0x0023:
        r4 = r5;
        goto L_0x0026;
    L_0x0025:
        r4 = r6;
    L_0x0026:
        r7 = r1.paused;
        r1.paused = r6;
        if (r2 == 0) goto L_0x0037;
    L_0x002c:
        r8 = org.telegram.messenger.Utilities.stageQueue;
        r9 = new org.telegram.messenger.FileLoadOperation$5;
        r9.<init>(r3, r2, r4);
        r8.postRunnable(r9);
        goto L_0x0045;
    L_0x0037:
        if (r7 == 0) goto L_0x0045;
    L_0x0039:
        if (r4 == 0) goto L_0x0045;
    L_0x003b:
        r2 = org.telegram.messenger.Utilities.stageQueue;
        r8 = new org.telegram.messenger.FileLoadOperation$6;
        r8.<init>();
        r2.postRunnable(r8);
    L_0x0045:
        if (r4 == 0) goto L_0x0048;
    L_0x0047:
        return r7;
    L_0x0048:
        r2 = r1.location;
        if (r2 != 0) goto L_0x0054;
    L_0x004c:
        r2 = r1.webLocation;
        if (r2 != 0) goto L_0x0054;
    L_0x0050:
        r1.onFail(r5, r6);
        return r6;
    L_0x0054:
        r2 = r1.currentDownloadChunkSize;
        r2 = r3 / r2;
        r3 = r1.currentDownloadChunkSize;
        r2 = r2 * r3;
        r1.streamStartOffset = r2;
        r2 = r1.allowDisordererFileSave;
        if (r2 == 0) goto L_0x0079;
    L_0x0061:
        r2 = r1.totalBytesCount;
        if (r2 <= 0) goto L_0x0079;
    L_0x0065:
        r2 = r1.totalBytesCount;
        r3 = r1.currentDownloadChunkSize;
        if (r2 <= r3) goto L_0x0079;
    L_0x006b:
        r2 = new java.util.ArrayList;
        r2.<init>();
        r1.notLoadedBytesRanges = r2;
        r2 = new java.util.ArrayList;
        r2.<init>();
        r1.notRequestedBytesRanges = r2;
    L_0x0079:
        r2 = r1.webLocation;
        r3 = 0;
        r7 = 0;
        if (r2 == 0) goto L_0x010f;
    L_0x0080:
        r2 = r1.webLocation;
        r2 = r2.url;
        r2 = org.telegram.messenger.Utilities.MD5(r2);
        r8 = r1.encryptFile;
        if (r8 == 0) goto L_0x00d0;
    L_0x008c:
        r8 = new java.lang.StringBuilder;
        r8.<init>();
        r8.append(r2);
        r9 = ".temp.enc";
        r8.append(r9);
        r8 = r8.toString();
        r9 = new java.lang.StringBuilder;
        r9.<init>();
        r9.append(r2);
        r10 = ".";
        r9.append(r10);
        r10 = r1.ext;
        r9.append(r10);
        r10 = ".enc";
        r9.append(r10);
        r9 = r9.toString();
        r10 = r1.key;
        if (r10 == 0) goto L_0x00ce;
    L_0x00bc:
        r10 = new java.lang.StringBuilder;
        r10.<init>();
        r10.append(r2);
        r2 = ".iv.enc";
        r10.append(r2);
        r2 = r10.toString();
        goto L_0x010c;
    L_0x00ce:
        r2 = r7;
        goto L_0x010c;
    L_0x00d0:
        r8 = new java.lang.StringBuilder;
        r8.<init>();
        r8.append(r2);
        r9 = ".temp";
        r8.append(r9);
        r8 = r8.toString();
        r9 = new java.lang.StringBuilder;
        r9.<init>();
        r9.append(r2);
        r10 = ".";
        r9.append(r10);
        r10 = r1.ext;
        r9.append(r10);
        r9 = r9.toString();
        r10 = r1.key;
        if (r10 == 0) goto L_0x00ce;
    L_0x00fb:
        r10 = new java.lang.StringBuilder;
        r10.<init>();
        r10.append(r2);
        r2 = ".iv";
        r10.append(r2);
        r2 = r10.toString();
    L_0x010c:
        r10 = r7;
        goto L_0x0348;
    L_0x010f:
        r2 = r1.location;
        r8 = r2.volume_id;
        r2 = (r8 > r3 ? 1 : (r8 == r3 ? 0 : -1));
        if (r2 == 0) goto L_0x0244;
    L_0x0117:
        r2 = r1.location;
        r2 = r2.local_id;
        if (r2 == 0) goto L_0x0244;
    L_0x011d:
        r2 = r1.datacenter_id;
        r8 = -NUM; // 0xffffffff80000000 float:-0.0 double:NaN;
        if (r2 == r8) goto L_0x0240;
    L_0x0123:
        r2 = r1.location;
        r8 = r2.volume_id;
        r10 = -NUM; // 0xffffffff80000000 float:-0.0 double:NaN;
        r2 = (r8 > r10 ? 1 : (r8 == r10 ? 0 : -1));
        if (r2 == 0) goto L_0x0240;
    L_0x012e:
        r2 = r1.datacenter_id;
        if (r2 != 0) goto L_0x0134;
    L_0x0132:
        goto L_0x0240;
    L_0x0134:
        r2 = r1.encryptFile;
        if (r2 == 0) goto L_0x01ab;
    L_0x0138:
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r8 = r1.location;
        r8 = r8.volume_id;
        r2.append(r8);
        r8 = "_";
        r2.append(r8);
        r8 = r1.location;
        r8 = r8.local_id;
        r2.append(r8);
        r8 = ".temp.enc";
        r2.append(r8);
        r8 = r2.toString();
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r9 = r1.location;
        r9 = r9.volume_id;
        r2.append(r9);
        r9 = "_";
        r2.append(r9);
        r9 = r1.location;
        r9 = r9.local_id;
        r2.append(r9);
        r9 = ".";
        r2.append(r9);
        r9 = r1.ext;
        r2.append(r9);
        r9 = ".enc";
        r2.append(r9);
        r9 = r2.toString();
        r2 = r1.key;
        if (r2 == 0) goto L_0x02be;
    L_0x0188:
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r10 = r1.location;
        r10 = r10.volume_id;
        r2.append(r10);
        r10 = "_";
        r2.append(r10);
        r10 = r1.location;
        r10 = r10.local_id;
        r2.append(r10);
        r10 = ".iv.enc";
        r2.append(r10);
        r2 = r2.toString();
        goto L_0x010c;
    L_0x01ab:
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r8 = r1.location;
        r8 = r8.volume_id;
        r2.append(r8);
        r8 = "_";
        r2.append(r8);
        r8 = r1.location;
        r8 = r8.local_id;
        r2.append(r8);
        r8 = ".temp";
        r2.append(r8);
        r8 = r2.toString();
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r9 = r1.location;
        r9 = r9.volume_id;
        r2.append(r9);
        r9 = "_";
        r2.append(r9);
        r9 = r1.location;
        r9 = r9.local_id;
        r2.append(r9);
        r9 = ".";
        r2.append(r9);
        r9 = r1.ext;
        r2.append(r9);
        r9 = r2.toString();
        r2 = r1.key;
        if (r2 == 0) goto L_0x0218;
    L_0x01f6:
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r10 = r1.location;
        r10 = r10.volume_id;
        r2.append(r10);
        r10 = "_";
        r2.append(r10);
        r10 = r1.location;
        r10 = r10.local_id;
        r2.append(r10);
        r10 = ".iv";
        r2.append(r10);
        r2 = r2.toString();
        goto L_0x0219;
    L_0x0218:
        r2 = r7;
    L_0x0219:
        r10 = r1.notLoadedBytesRanges;
        if (r10 == 0) goto L_0x010c;
    L_0x021d:
        r10 = new java.lang.StringBuilder;
        r10.<init>();
        r11 = r1.location;
        r11 = r11.volume_id;
        r10.append(r11);
        r11 = "_";
        r10.append(r11);
        r11 = r1.location;
        r11 = r11.local_id;
        r10.append(r11);
        r11 = ".pt";
        r10.append(r11);
        r10 = r10.toString();
        goto L_0x0348;
    L_0x0240:
        r1.onFail(r5, r6);
        return r6;
    L_0x0244:
        r2 = r1.datacenter_id;
        if (r2 == 0) goto L_0x05e3;
    L_0x0248:
        r2 = r1.location;
        r8 = r2.id;
        r2 = (r8 > r3 ? 1 : (r8 == r3 ? 0 : -1));
        if (r2 != 0) goto L_0x0252;
    L_0x0250:
        goto L_0x05e3;
    L_0x0252:
        r2 = r1.encryptFile;
        if (r2 == 0) goto L_0x02c2;
    L_0x0256:
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r8 = r1.datacenter_id;
        r2.append(r8);
        r8 = "_";
        r2.append(r8);
        r8 = r1.location;
        r8 = r8.id;
        r2.append(r8);
        r8 = ".temp.enc";
        r2.append(r8);
        r8 = r2.toString();
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r9 = r1.datacenter_id;
        r2.append(r9);
        r9 = "_";
        r2.append(r9);
        r9 = r1.location;
        r9 = r9.id;
        r2.append(r9);
        r9 = r1.ext;
        r2.append(r9);
        r9 = ".enc";
        r2.append(r9);
        r9 = r2.toString();
        r2 = r1.key;
        if (r2 == 0) goto L_0x02be;
    L_0x029d:
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r10 = r1.datacenter_id;
        r2.append(r10);
        r10 = "_";
        r2.append(r10);
        r10 = r1.location;
        r10 = r10.id;
        r2.append(r10);
        r10 = ".iv.enc";
        r2.append(r10);
        r2 = r2.toString();
        goto L_0x010c;
    L_0x02be:
        r2 = r7;
        r10 = r2;
        goto L_0x0348;
    L_0x02c2:
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r8 = r1.datacenter_id;
        r2.append(r8);
        r8 = "_";
        r2.append(r8);
        r8 = r1.location;
        r8 = r8.id;
        r2.append(r8);
        r8 = ".temp";
        r2.append(r8);
        r8 = r2.toString();
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r9 = r1.datacenter_id;
        r2.append(r9);
        r9 = "_";
        r2.append(r9);
        r9 = r1.location;
        r9 = r9.id;
        r2.append(r9);
        r9 = r1.ext;
        r2.append(r9);
        r9 = r2.toString();
        r2 = r1.key;
        if (r2 == 0) goto L_0x0324;
    L_0x0304:
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r10 = r1.datacenter_id;
        r2.append(r10);
        r10 = "_";
        r2.append(r10);
        r10 = r1.location;
        r10 = r10.id;
        r2.append(r10);
        r10 = ".iv";
        r2.append(r10);
        r2 = r2.toString();
        goto L_0x0325;
    L_0x0324:
        r2 = r7;
    L_0x0325:
        r10 = r1.notLoadedBytesRanges;
        if (r10 == 0) goto L_0x010c;
    L_0x0329:
        r10 = new java.lang.StringBuilder;
        r10.<init>();
        r11 = r1.datacenter_id;
        r10.append(r11);
        r11 = "_";
        r10.append(r11);
        r11 = r1.location;
        r11 = r11.id;
        r10.append(r11);
        r11 = ".pt";
        r10.append(r11);
        r10 = r10.toString();
    L_0x0348:
        r11 = new java.util.ArrayList;
        r12 = r1.currentMaxDownloadRequests;
        r11.<init>(r12);
        r1.requestInfos = r11;
        r11 = new java.util.ArrayList;
        r12 = r1.currentMaxDownloadRequests;
        r12 = r12 - r5;
        r11.<init>(r12);
        r1.delayedRequestInfos = r11;
        r1.state = r5;
        r11 = new java.io.File;
        r12 = r1.storePath;
        r11.<init>(r12, r9);
        r1.cacheFileFinal = r11;
        r11 = r1.cacheFileFinal;
        r11 = r11.exists();
        if (r11 == 0) goto L_0x0385;
    L_0x036e:
        r12 = r1.totalBytesCount;
        if (r12 == 0) goto L_0x0385;
    L_0x0372:
        r12 = r1.totalBytesCount;
        r12 = (long) r12;
        r14 = r1.cacheFileFinal;
        r14 = r14.length();
        r16 = (r12 > r14 ? 1 : (r12 == r14 ? 0 : -1));
        if (r16 == 0) goto L_0x0385;
    L_0x037f:
        r11 = r1.cacheFileFinal;
        r11.delete();
        r11 = r6;
    L_0x0385:
        if (r11 != 0) goto L_0x05d9;
    L_0x0387:
        r11 = new java.io.File;
        r12 = r1.tempPath;
        r11.<init>(r12, r8);
        r1.cacheFileTemp = r11;
        r8 = r1.encryptFile;
        r11 = 32;
        if (r8 == 0) goto L_0x0412;
    L_0x0396:
        r8 = new java.io.File;
        r12 = org.telegram.messenger.FileLoader.getInternalCacheDir();
        r13 = new java.lang.StringBuilder;
        r13.<init>();
        r13.append(r9);
        r9 = ".key";
        r13.append(r9);
        r9 = r13.toString();
        r8.<init>(r12, r9);
        r9 = new java.io.RandomAccessFile;	 Catch:{ Exception -> 0x040a }
        r12 = "rws";	 Catch:{ Exception -> 0x040a }
        r9.<init>(r8, r12);	 Catch:{ Exception -> 0x040a }
        r12 = r8.length();	 Catch:{ Exception -> 0x040a }
        r8 = new byte[r11];	 Catch:{ Exception -> 0x040a }
        r1.encryptKey = r8;	 Catch:{ Exception -> 0x040a }
        r8 = 16;	 Catch:{ Exception -> 0x040a }
        r14 = new byte[r8];	 Catch:{ Exception -> 0x040a }
        r1.encryptIv = r14;	 Catch:{ Exception -> 0x040a }
        r14 = (r12 > r3 ? 1 : (r12 == r3 ? 0 : -1));	 Catch:{ Exception -> 0x040a }
        if (r14 <= 0) goto L_0x03dc;	 Catch:{ Exception -> 0x040a }
    L_0x03c9:
        r14 = 48;	 Catch:{ Exception -> 0x040a }
        r12 = r12 % r14;	 Catch:{ Exception -> 0x040a }
        r14 = (r12 > r3 ? 1 : (r12 == r3 ? 0 : -1));	 Catch:{ Exception -> 0x040a }
        if (r14 != 0) goto L_0x03dc;	 Catch:{ Exception -> 0x040a }
    L_0x03d0:
        r12 = r1.encryptKey;	 Catch:{ Exception -> 0x040a }
        r9.read(r12, r6, r11);	 Catch:{ Exception -> 0x040a }
        r12 = r1.encryptIv;	 Catch:{ Exception -> 0x040a }
        r9.read(r12, r6, r8);	 Catch:{ Exception -> 0x040a }
        r8 = r6;	 Catch:{ Exception -> 0x040a }
        goto L_0x03f5;	 Catch:{ Exception -> 0x040a }
    L_0x03dc:
        r8 = org.telegram.messenger.Utilities.random;	 Catch:{ Exception -> 0x040a }
        r12 = r1.encryptKey;	 Catch:{ Exception -> 0x040a }
        r8.nextBytes(r12);	 Catch:{ Exception -> 0x040a }
        r8 = org.telegram.messenger.Utilities.random;	 Catch:{ Exception -> 0x040a }
        r12 = r1.encryptIv;	 Catch:{ Exception -> 0x040a }
        r8.nextBytes(r12);	 Catch:{ Exception -> 0x040a }
        r8 = r1.encryptKey;	 Catch:{ Exception -> 0x040a }
        r9.write(r8);	 Catch:{ Exception -> 0x040a }
        r8 = r1.encryptIv;	 Catch:{ Exception -> 0x040a }
        r9.write(r8);	 Catch:{ Exception -> 0x040a }
        r8 = r5;
    L_0x03f5:
        r12 = r9.getChannel();	 Catch:{ Exception -> 0x03fd }
        r12.close();	 Catch:{ Exception -> 0x03fd }
        goto L_0x0402;
    L_0x03fd:
        r0 = move-exception;
        r12 = r0;
        org.telegram.messenger.FileLog.m3e(r12);	 Catch:{ Exception -> 0x0406 }
    L_0x0402:
        r9.close();	 Catch:{ Exception -> 0x0406 }
        goto L_0x0413;
    L_0x0406:
        r0 = move-exception;
        r9 = r8;
        r8 = r0;
        goto L_0x040d;
    L_0x040a:
        r0 = move-exception;
        r8 = r0;
        r9 = r6;
    L_0x040d:
        org.telegram.messenger.FileLog.m3e(r8);
        r8 = r9;
        goto L_0x0413;
    L_0x0412:
        r8 = r6;
    L_0x0413:
        if (r10 == 0) goto L_0x0475;
    L_0x0415:
        r9 = new java.io.File;
        r12 = r1.tempPath;
        r9.<init>(r12, r10);
        r1.cacheFileParts = r9;
        r9 = new java.io.RandomAccessFile;	 Catch:{ Exception -> 0x0470 }
        r10 = r1.cacheFileParts;	 Catch:{ Exception -> 0x0470 }
        r12 = "rws";	 Catch:{ Exception -> 0x0470 }
        r9.<init>(r10, r12);	 Catch:{ Exception -> 0x0470 }
        r1.filePartsStream = r9;	 Catch:{ Exception -> 0x0470 }
        r9 = r1.filePartsStream;	 Catch:{ Exception -> 0x0470 }
        r9 = r9.length();	 Catch:{ Exception -> 0x0470 }
        r12 = 8;	 Catch:{ Exception -> 0x0470 }
        r12 = r9 % r12;	 Catch:{ Exception -> 0x0470 }
        r14 = 4;	 Catch:{ Exception -> 0x0470 }
        r16 = (r12 > r14 ? 1 : (r12 == r14 ? 0 : -1));	 Catch:{ Exception -> 0x0470 }
        if (r16 != 0) goto L_0x0475;	 Catch:{ Exception -> 0x0470 }
    L_0x0439:
        r12 = r9 - r14;	 Catch:{ Exception -> 0x0470 }
        r9 = r1.filePartsStream;	 Catch:{ Exception -> 0x0470 }
        r9 = r9.readInt();	 Catch:{ Exception -> 0x0470 }
        r14 = (long) r9;	 Catch:{ Exception -> 0x0470 }
        r16 = 2;	 Catch:{ Exception -> 0x0470 }
        r12 = r12 / r16;	 Catch:{ Exception -> 0x0470 }
        r10 = (r14 > r12 ? 1 : (r14 == r12 ? 0 : -1));	 Catch:{ Exception -> 0x0470 }
        if (r10 > 0) goto L_0x0475;	 Catch:{ Exception -> 0x0470 }
    L_0x044a:
        r10 = r6;	 Catch:{ Exception -> 0x0470 }
    L_0x044b:
        if (r10 >= r9) goto L_0x0475;	 Catch:{ Exception -> 0x0470 }
    L_0x044d:
        r12 = r1.filePartsStream;	 Catch:{ Exception -> 0x0470 }
        r12 = r12.readInt();	 Catch:{ Exception -> 0x0470 }
        r13 = r1.filePartsStream;	 Catch:{ Exception -> 0x0470 }
        r13 = r13.readInt();	 Catch:{ Exception -> 0x0470 }
        r14 = r1.notLoadedBytesRanges;	 Catch:{ Exception -> 0x0470 }
        r15 = new org.telegram.messenger.FileLoadOperation$Range;	 Catch:{ Exception -> 0x0470 }
        r15.<init>(r12, r13);	 Catch:{ Exception -> 0x0470 }
        r14.add(r15);	 Catch:{ Exception -> 0x0470 }
        r14 = r1.notRequestedBytesRanges;	 Catch:{ Exception -> 0x0470 }
        r15 = new org.telegram.messenger.FileLoadOperation$Range;	 Catch:{ Exception -> 0x0470 }
        r15.<init>(r12, r13);	 Catch:{ Exception -> 0x0470 }
        r14.add(r15);	 Catch:{ Exception -> 0x0470 }
        r10 = r10 + 1;
        goto L_0x044b;
    L_0x0470:
        r0 = move-exception;
        r9 = r0;
        org.telegram.messenger.FileLog.m3e(r9);
    L_0x0475:
        r9 = r1.cacheFileTemp;
        r9 = r9.exists();
        if (r9 == 0) goto L_0x04d5;
    L_0x047d:
        if (r8 == 0) goto L_0x0486;
    L_0x047f:
        r7 = r1.cacheFileTemp;
        r7.delete();
        goto L_0x04f9;
    L_0x0486:
        r9 = r1.cacheFileTemp;
        r9 = r9.length();
        if (r2 == 0) goto L_0x049b;
    L_0x048e:
        r12 = r1.currentDownloadChunkSize;
        r12 = (long) r12;
        r9 = r9 % r12;
        r12 = (r9 > r3 ? 1 : (r9 == r3 ? 0 : -1));
        if (r12 == 0) goto L_0x049b;
    L_0x0496:
        r1.downloadedBytes = r6;
        r1.requestedBytesCount = r6;
        goto L_0x04ac;
    L_0x049b:
        r9 = r1.cacheFileTemp;
        r9 = r9.length();
        r9 = (int) r9;
        r10 = r1.currentDownloadChunkSize;
        r9 = r9 / r10;
        r10 = r1.currentDownloadChunkSize;
        r9 = r9 * r10;
        r1.downloadedBytes = r9;
        r1.requestedBytesCount = r9;
    L_0x04ac:
        r9 = r1.notLoadedBytesRanges;
        if (r9 == 0) goto L_0x04f9;
    L_0x04b0:
        r9 = r1.notLoadedBytesRanges;
        r9 = r9.isEmpty();
        if (r9 == 0) goto L_0x04f9;
    L_0x04b8:
        r9 = r1.notLoadedBytesRanges;
        r10 = new org.telegram.messenger.FileLoadOperation$Range;
        r12 = r1.downloadedBytes;
        r13 = r1.totalBytesCount;
        r10.<init>(r12, r13);
        r9.add(r10);
        r9 = r1.notRequestedBytesRanges;
        r10 = new org.telegram.messenger.FileLoadOperation$Range;
        r12 = r1.downloadedBytes;
        r13 = r1.totalBytesCount;
        r10.<init>(r12, r13);
        r9.add(r10);
        goto L_0x04f9;
    L_0x04d5:
        r9 = r1.notLoadedBytesRanges;
        if (r9 == 0) goto L_0x04f9;
    L_0x04d9:
        r9 = r1.notLoadedBytesRanges;
        r9 = r9.isEmpty();
        if (r9 == 0) goto L_0x04f9;
    L_0x04e1:
        r9 = r1.notLoadedBytesRanges;
        r10 = new org.telegram.messenger.FileLoadOperation$Range;
        r12 = r1.totalBytesCount;
        r10.<init>(r6, r12);
        r9.add(r10);
        r9 = r1.notRequestedBytesRanges;
        r10 = new org.telegram.messenger.FileLoadOperation$Range;
        r12 = r1.totalBytesCount;
        r10.<init>(r6, r12);
        r9.add(r10);
    L_0x04f9:
        r7 = r1.notLoadedBytesRanges;
        if (r7 == 0) goto L_0x0523;
    L_0x04fd:
        r7 = r1.totalBytesCount;
        r1.downloadedBytes = r7;
        r7 = r1.notLoadedBytesRanges;
        r7 = r7.size();
        r9 = r6;
    L_0x0508:
        if (r9 >= r7) goto L_0x0523;
    L_0x050a:
        r10 = r1.notLoadedBytesRanges;
        r10 = r10.get(r9);
        r10 = (org.telegram.messenger.FileLoadOperation.Range) r10;
        r12 = r1.downloadedBytes;
        r13 = r10.end;
        r10 = r10.start;
        r13 = r13 - r10;
        r12 = r12 - r13;
        r1.downloadedBytes = r12;
        r9 = r9 + 1;
        goto L_0x0508;
    L_0x0523:
        r7 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r7 == 0) goto L_0x0547;
    L_0x0527:
        r7 = new java.lang.StringBuilder;
        r7.<init>();
        r9 = "start loading file to temp = ";
        r7.append(r9);
        r9 = r1.cacheFileTemp;
        r7.append(r9);
        r9 = " final = ";
        r7.append(r9);
        r9 = r1.cacheFileFinal;
        r7.append(r9);
        r7 = r7.toString();
        org.telegram.messenger.FileLog.m0d(r7);
    L_0x0547:
        if (r2 == 0) goto L_0x058a;
    L_0x0549:
        r7 = new java.io.File;
        r9 = r1.tempPath;
        r7.<init>(r9, r2);
        r1.cacheIvTemp = r7;
        r2 = new java.io.RandomAccessFile;	 Catch:{ Exception -> 0x0581 }
        r7 = r1.cacheIvTemp;	 Catch:{ Exception -> 0x0581 }
        r9 = "rws";	 Catch:{ Exception -> 0x0581 }
        r2.<init>(r7, r9);	 Catch:{ Exception -> 0x0581 }
        r1.fiv = r2;	 Catch:{ Exception -> 0x0581 }
        r2 = r1.downloadedBytes;	 Catch:{ Exception -> 0x0581 }
        if (r2 == 0) goto L_0x058a;	 Catch:{ Exception -> 0x0581 }
    L_0x0561:
        if (r8 != 0) goto L_0x058a;	 Catch:{ Exception -> 0x0581 }
    L_0x0563:
        r2 = r1.cacheIvTemp;	 Catch:{ Exception -> 0x0581 }
        r7 = r2.length();	 Catch:{ Exception -> 0x0581 }
        r2 = (r7 > r3 ? 1 : (r7 == r3 ? 0 : -1));	 Catch:{ Exception -> 0x0581 }
        if (r2 <= 0) goto L_0x057c;	 Catch:{ Exception -> 0x0581 }
    L_0x056d:
        r9 = 32;	 Catch:{ Exception -> 0x0581 }
        r7 = r7 % r9;	 Catch:{ Exception -> 0x0581 }
        r2 = (r7 > r3 ? 1 : (r7 == r3 ? 0 : -1));	 Catch:{ Exception -> 0x0581 }
        if (r2 != 0) goto L_0x057c;	 Catch:{ Exception -> 0x0581 }
    L_0x0574:
        r2 = r1.fiv;	 Catch:{ Exception -> 0x0581 }
        r3 = r1.iv;	 Catch:{ Exception -> 0x0581 }
        r2.read(r3, r6, r11);	 Catch:{ Exception -> 0x0581 }
        goto L_0x058a;	 Catch:{ Exception -> 0x0581 }
    L_0x057c:
        r1.downloadedBytes = r6;	 Catch:{ Exception -> 0x0581 }
        r1.requestedBytesCount = r6;	 Catch:{ Exception -> 0x0581 }
        goto L_0x058a;
    L_0x0581:
        r0 = move-exception;
        r2 = r0;
        org.telegram.messenger.FileLog.m3e(r2);
        r1.downloadedBytes = r6;
        r1.requestedBytesCount = r6;
    L_0x058a:
        r2 = r1.downloadedBytes;
        if (r2 == 0) goto L_0x05a7;
    L_0x058e:
        r2 = r1.totalBytesCount;
        if (r2 <= 0) goto L_0x05a7;
    L_0x0592:
        r18.copytNotLoadedRanges();
        r2 = r1.delegate;
        r3 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;
        r4 = r1.downloadedBytes;
        r4 = (float) r4;
        r7 = r1.totalBytesCount;
        r7 = (float) r7;
        r4 = r4 / r7;
        r3 = java.lang.Math.min(r3, r4);
        r2.didChangedLoadProgress(r1, r3);
    L_0x05a7:
        r2 = new java.io.RandomAccessFile;	 Catch:{ Exception -> 0x05bf }
        r3 = r1.cacheFileTemp;	 Catch:{ Exception -> 0x05bf }
        r4 = "rws";	 Catch:{ Exception -> 0x05bf }
        r2.<init>(r3, r4);	 Catch:{ Exception -> 0x05bf }
        r1.fileOutputStream = r2;	 Catch:{ Exception -> 0x05bf }
        r2 = r1.downloadedBytes;	 Catch:{ Exception -> 0x05bf }
        if (r2 == 0) goto L_0x05c4;	 Catch:{ Exception -> 0x05bf }
    L_0x05b6:
        r2 = r1.fileOutputStream;	 Catch:{ Exception -> 0x05bf }
        r3 = r1.downloadedBytes;	 Catch:{ Exception -> 0x05bf }
        r3 = (long) r3;	 Catch:{ Exception -> 0x05bf }
        r2.seek(r3);	 Catch:{ Exception -> 0x05bf }
        goto L_0x05c4;
    L_0x05bf:
        r0 = move-exception;
        r2 = r0;
        org.telegram.messenger.FileLog.m3e(r2);
    L_0x05c4:
        r2 = r1.fileOutputStream;
        if (r2 != 0) goto L_0x05cc;
    L_0x05c8:
        r1.onFail(r5, r6);
        return r6;
    L_0x05cc:
        r1.started = r5;
        r2 = org.telegram.messenger.Utilities.stageQueue;
        r3 = new org.telegram.messenger.FileLoadOperation$7;
        r3.<init>();
        r2.postRunnable(r3);
        goto L_0x05e2;
    L_0x05d9:
        r1.started = r5;
        r1.onFinishLoadingFile(r6);	 Catch:{ Exception -> 0x05df }
        goto L_0x05e2;
    L_0x05df:
        r1.onFail(r5, r6);
    L_0x05e2:
        return r5;
    L_0x05e3:
        r1.onFail(r5, r6);
        return r6;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.FileLoadOperation.start(org.telegram.messenger.FileStreamLoadOperation, int):boolean");
    }

    public boolean isPaused() {
        return this.paused;
    }

    public void cancel() {
        Utilities.stageQueue.postRunnable(new C01578());
    }

    private void cleanup() {
        try {
            if (this.fileOutputStream != null) {
                try {
                    this.fileOutputStream.getChannel().close();
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
                this.fileOutputStream.close();
                this.fileOutputStream = null;
            }
        } catch (Throwable e2) {
            FileLog.m3e(e2);
        }
        try {
            if (this.fileReadStream != null) {
                try {
                    this.fileReadStream.getChannel().close();
                } catch (Throwable e22) {
                    FileLog.m3e(e22);
                }
                this.fileReadStream.close();
                this.fileReadStream = null;
            }
        } catch (Throwable e222) {
            FileLog.m3e(e222);
        }
        try {
            if (this.filePartsStream != null) {
                try {
                    this.filePartsStream.getChannel().close();
                } catch (Throwable e2222) {
                    FileLog.m3e(e2222);
                }
                this.filePartsStream.close();
                this.filePartsStream = null;
            }
        } catch (Throwable e22222) {
            FileLog.m3e(e22222);
        }
        try {
            if (this.fiv != null) {
                this.fiv.close();
                this.fiv = null;
            }
        } catch (Throwable e3) {
            FileLog.m3e(e3);
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

    private void onFinishLoadingFile(final boolean z) throws Exception {
        if (this.state == 1) {
            StringBuilder stringBuilder;
            this.state = 3;
            cleanup();
            if (this.cacheIvTemp != null) {
                this.cacheIvTemp.delete();
                this.cacheIvTemp = null;
            }
            if (this.cacheFileParts != null) {
                this.cacheFileParts.delete();
                this.cacheFileParts = null;
            }
            if (!(this.cacheFileTemp == null || this.cacheFileTemp.renameTo(this.cacheFileFinal))) {
                if (BuildVars.LOGS_ENABLED) {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("unable to rename temp = ");
                    stringBuilder.append(this.cacheFileTemp);
                    stringBuilder.append(" to final = ");
                    stringBuilder.append(this.cacheFileFinal);
                    stringBuilder.append(" retry = ");
                    stringBuilder.append(this.renameRetryCount);
                    FileLog.m1e(stringBuilder.toString());
                }
                this.renameRetryCount++;
                if (this.renameRetryCount < 3) {
                    this.state = 1;
                    Utilities.stageQueue.postRunnable(new Runnable() {
                        public void run() {
                            /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
                            /*
                            r2 = this;
                            r0 = org.telegram.messenger.FileLoadOperation.this;	 Catch:{ Exception -> 0x0008 }
                            r1 = r5;	 Catch:{ Exception -> 0x0008 }
                            r0.onFinishLoadingFile(r1);	 Catch:{ Exception -> 0x0008 }
                            goto L_0x000e;
                        L_0x0008:
                            r0 = org.telegram.messenger.FileLoadOperation.this;
                            r1 = 0;
                            r0.onFail(r1, r1);
                        L_0x000e:
                            return;
                            */
                            throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.FileLoadOperation.9.run():void");
                        }
                    }, 200);
                    return;
                }
                this.cacheFileFinal = this.cacheFileTemp;
            }
            if (BuildVars.LOGS_ENABLED) {
                stringBuilder = new StringBuilder();
                stringBuilder.append("finished downloading file to ");
                stringBuilder.append(this.cacheFileFinal);
                FileLog.m0d(stringBuilder.toString());
            }
            this.delegate.didFinishLoadingFile(this, this.cacheFileFinal);
            if (z) {
                if (this.currentType) {
                    StatsController.getInstance(this.currentAccount).incrementReceivedItemsCount(ConnectionsManager.getCurrentNetworkType(), 3, 1);
                } else if (this.currentType) {
                    StatsController.getInstance(this.currentAccount).incrementReceivedItemsCount(ConnectionsManager.getCurrentNetworkType(), 2, 1);
                } else if (this.currentType) {
                    StatsController.getInstance(this.currentAccount).incrementReceivedItemsCount(ConnectionsManager.getCurrentNetworkType(), 4, 1);
                } else if (this.currentType) {
                    StatsController.getInstance(this.currentAccount).incrementReceivedItemsCount(ConnectionsManager.getCurrentNetworkType(), 5, 1);
                }
            }
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

    private void requestFileOffsets(int i) {
        if (!this.requestingCdnOffsets) {
            this.requestingCdnOffsets = true;
            TLObject tL_upload_getCdnFileHashes = new TL_upload_getCdnFileHashes();
            tL_upload_getCdnFileHashes.file_token = this.cdnToken;
            tL_upload_getCdnFileHashes.offset = i;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_upload_getCdnFileHashes, new RequestDelegate() {
                public void run(TLObject tLObject, TL_error tL_error) {
                    if (tL_error != null) {
                        FileLoadOperation.this.onFail(false, 0);
                        return;
                    }
                    FileLoadOperation.this.requestingCdnOffsets = false;
                    Vector vector = (Vector) tLObject;
                    if (vector.objects.isEmpty() == null) {
                        if (FileLoadOperation.this.cdnHashes == null) {
                            FileLoadOperation.this.cdnHashes = new SparseArray();
                        }
                        for (tL_error = null; tL_error < vector.objects.size(); tL_error++) {
                            TL_fileHash tL_fileHash = (TL_fileHash) vector.objects.get(tL_error);
                            FileLoadOperation.this.cdnHashes.put(tL_fileHash.offset, tL_fileHash);
                        }
                    }
                    tLObject = null;
                    while (tLObject < FileLoadOperation.this.delayedRequestInfos.size()) {
                        RequestInfo requestInfo = (RequestInfo) FileLoadOperation.this.delayedRequestInfos.get(tLObject);
                        if (FileLoadOperation.this.notLoadedBytesRanges == null) {
                            if (FileLoadOperation.this.downloadedBytes != requestInfo.offset) {
                                tLObject++;
                            }
                        }
                        FileLoadOperation.this.delayedRequestInfos.remove(tLObject);
                        if (FileLoadOperation.this.processRequestResult(requestInfo, null) != null) {
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
                    }
                }
            }, null, null, 0, this.datacenter_id, 1, true);
        }
    }

    private boolean processRequestResult(org.telegram.messenger.FileLoadOperation.RequestInfo r24, org.telegram.tgnet.TLRPC.TL_error r25) {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
        /*
        r23 = this;
        r1 = r23;
        r2 = r25;
        r3 = r1.state;
        r4 = 1;
        r5 = 0;
        if (r3 == r4) goto L_0x000b;
    L_0x000a:
        return r5;
    L_0x000b:
        r3 = r1.requestInfos;
        r6 = r24;
        r3.remove(r6);
        if (r2 != 0) goto L_0x034e;
    L_0x0014:
        r2 = r1.notLoadedBytesRanges;	 Catch:{ Exception -> 0x0344 }
        if (r2 != 0) goto L_0x0024;	 Catch:{ Exception -> 0x0344 }
    L_0x0018:
        r2 = r1.downloadedBytes;	 Catch:{ Exception -> 0x0344 }
        r7 = r24.offset;	 Catch:{ Exception -> 0x0344 }
        if (r2 == r7) goto L_0x0024;	 Catch:{ Exception -> 0x0344 }
    L_0x0020:
        r23.delayRequestInfo(r24);	 Catch:{ Exception -> 0x0344 }
        return r5;	 Catch:{ Exception -> 0x0344 }
    L_0x0024:
        r2 = r24.response;	 Catch:{ Exception -> 0x0344 }
        if (r2 == 0) goto L_0x0031;	 Catch:{ Exception -> 0x0344 }
    L_0x002a:
        r2 = r24.response;	 Catch:{ Exception -> 0x0344 }
        r2 = r2.bytes;	 Catch:{ Exception -> 0x0344 }
        goto L_0x004c;	 Catch:{ Exception -> 0x0344 }
    L_0x0031:
        r2 = r24.responseWeb;	 Catch:{ Exception -> 0x0344 }
        if (r2 == 0) goto L_0x003e;	 Catch:{ Exception -> 0x0344 }
    L_0x0037:
        r2 = r24.responseWeb;	 Catch:{ Exception -> 0x0344 }
        r2 = r2.bytes;	 Catch:{ Exception -> 0x0344 }
        goto L_0x004c;	 Catch:{ Exception -> 0x0344 }
    L_0x003e:
        r2 = r24.responseCdn;	 Catch:{ Exception -> 0x0344 }
        if (r2 == 0) goto L_0x004b;	 Catch:{ Exception -> 0x0344 }
    L_0x0044:
        r2 = r24.responseCdn;	 Catch:{ Exception -> 0x0344 }
        r2 = r2.bytes;	 Catch:{ Exception -> 0x0344 }
        goto L_0x004c;	 Catch:{ Exception -> 0x0344 }
    L_0x004b:
        r2 = 0;	 Catch:{ Exception -> 0x0344 }
    L_0x004c:
        if (r2 == 0) goto L_0x0340;	 Catch:{ Exception -> 0x0344 }
    L_0x004e:
        r7 = r2.limit();	 Catch:{ Exception -> 0x0344 }
        if (r7 != 0) goto L_0x0056;	 Catch:{ Exception -> 0x0344 }
    L_0x0054:
        goto L_0x0340;	 Catch:{ Exception -> 0x0344 }
    L_0x0056:
        r7 = r2.limit();	 Catch:{ Exception -> 0x0344 }
        r8 = r1.isCdn;	 Catch:{ Exception -> 0x0344 }
        r9 = 131072; // 0x20000 float:1.83671E-40 double:6.47582E-319;	 Catch:{ Exception -> 0x0344 }
        if (r8 == 0) goto L_0x007d;	 Catch:{ Exception -> 0x0344 }
    L_0x0060:
        r8 = r24.offset;	 Catch:{ Exception -> 0x0344 }
        r8 = r8 / r9;	 Catch:{ Exception -> 0x0344 }
        r8 = r8 * r9;	 Catch:{ Exception -> 0x0344 }
        r10 = r1.cdnHashes;	 Catch:{ Exception -> 0x0344 }
        if (r10 == 0) goto L_0x0073;	 Catch:{ Exception -> 0x0344 }
    L_0x006a:
        r10 = r1.cdnHashes;	 Catch:{ Exception -> 0x0344 }
        r10 = r10.get(r8);	 Catch:{ Exception -> 0x0344 }
        r10 = (org.telegram.tgnet.TLRPC.TL_fileHash) r10;	 Catch:{ Exception -> 0x0344 }
        goto L_0x0074;	 Catch:{ Exception -> 0x0344 }
    L_0x0073:
        r10 = 0;	 Catch:{ Exception -> 0x0344 }
    L_0x0074:
        if (r10 != 0) goto L_0x007d;	 Catch:{ Exception -> 0x0344 }
    L_0x0076:
        r23.delayRequestInfo(r24);	 Catch:{ Exception -> 0x0344 }
        r1.requestFileOffsets(r8);	 Catch:{ Exception -> 0x0344 }
        return r4;	 Catch:{ Exception -> 0x0344 }
    L_0x007d:
        r8 = r24.responseCdn;	 Catch:{ Exception -> 0x0344 }
        r10 = 12;	 Catch:{ Exception -> 0x0344 }
        r11 = 13;	 Catch:{ Exception -> 0x0344 }
        r12 = 14;	 Catch:{ Exception -> 0x0344 }
        r13 = 15;	 Catch:{ Exception -> 0x0344 }
        if (r8 == 0) goto L_0x00c0;	 Catch:{ Exception -> 0x0344 }
    L_0x008b:
        r8 = r24.offset;	 Catch:{ Exception -> 0x0344 }
        r8 = r8 / 16;	 Catch:{ Exception -> 0x0344 }
        r14 = r1.cdnIv;	 Catch:{ Exception -> 0x0344 }
        r15 = r8 & 255;	 Catch:{ Exception -> 0x0344 }
        r15 = (byte) r15;	 Catch:{ Exception -> 0x0344 }
        r14[r13] = r15;	 Catch:{ Exception -> 0x0344 }
        r14 = r1.cdnIv;	 Catch:{ Exception -> 0x0344 }
        r15 = r8 >> 8;	 Catch:{ Exception -> 0x0344 }
        r15 = r15 & 255;	 Catch:{ Exception -> 0x0344 }
        r15 = (byte) r15;	 Catch:{ Exception -> 0x0344 }
        r14[r12] = r15;	 Catch:{ Exception -> 0x0344 }
        r14 = r1.cdnIv;	 Catch:{ Exception -> 0x0344 }
        r15 = r8 >> 16;	 Catch:{ Exception -> 0x0344 }
        r15 = r15 & 255;	 Catch:{ Exception -> 0x0344 }
        r15 = (byte) r15;	 Catch:{ Exception -> 0x0344 }
        r14[r11] = r15;	 Catch:{ Exception -> 0x0344 }
        r14 = r1.cdnIv;	 Catch:{ Exception -> 0x0344 }
        r8 = r8 >> 24;	 Catch:{ Exception -> 0x0344 }
        r8 = r8 & 255;	 Catch:{ Exception -> 0x0344 }
        r8 = (byte) r8;	 Catch:{ Exception -> 0x0344 }
        r14[r10] = r8;	 Catch:{ Exception -> 0x0344 }
        r8 = r2.buffer;	 Catch:{ Exception -> 0x0344 }
        r14 = r1.cdnKey;	 Catch:{ Exception -> 0x0344 }
        r15 = r1.cdnIv;	 Catch:{ Exception -> 0x0344 }
        r3 = r2.limit();	 Catch:{ Exception -> 0x0344 }
        org.telegram.messenger.Utilities.aesCtrDecryption(r8, r14, r15, r5, r3);	 Catch:{ Exception -> 0x0344 }
    L_0x00c0:
        r3 = r1.downloadedBytes;	 Catch:{ Exception -> 0x0344 }
        r3 = r3 + r7;	 Catch:{ Exception -> 0x0344 }
        r1.downloadedBytes = r3;	 Catch:{ Exception -> 0x0344 }
        r3 = r1.totalBytesCount;	 Catch:{ Exception -> 0x0344 }
        if (r3 <= 0) goto L_0x00d3;	 Catch:{ Exception -> 0x0344 }
    L_0x00c9:
        r3 = r1.downloadedBytes;	 Catch:{ Exception -> 0x0344 }
        r8 = r1.totalBytesCount;	 Catch:{ Exception -> 0x0344 }
        if (r3 < r8) goto L_0x00d1;	 Catch:{ Exception -> 0x0344 }
    L_0x00cf:
        r3 = r4;	 Catch:{ Exception -> 0x0344 }
        goto L_0x00ef;	 Catch:{ Exception -> 0x0344 }
    L_0x00d1:
        r3 = r5;	 Catch:{ Exception -> 0x0344 }
        goto L_0x00ef;	 Catch:{ Exception -> 0x0344 }
    L_0x00d3:
        r3 = r1.currentDownloadChunkSize;	 Catch:{ Exception -> 0x0344 }
        if (r7 != r3) goto L_0x00cf;	 Catch:{ Exception -> 0x0344 }
    L_0x00d7:
        r3 = r1.totalBytesCount;	 Catch:{ Exception -> 0x0344 }
        r8 = r1.downloadedBytes;	 Catch:{ Exception -> 0x0344 }
        if (r3 == r8) goto L_0x00e4;	 Catch:{ Exception -> 0x0344 }
    L_0x00dd:
        r3 = r1.downloadedBytes;	 Catch:{ Exception -> 0x0344 }
        r8 = r1.currentDownloadChunkSize;	 Catch:{ Exception -> 0x0344 }
        r3 = r3 % r8;	 Catch:{ Exception -> 0x0344 }
        if (r3 == 0) goto L_0x00d1;	 Catch:{ Exception -> 0x0344 }
    L_0x00e4:
        r3 = r1.totalBytesCount;	 Catch:{ Exception -> 0x0344 }
        if (r3 <= 0) goto L_0x00cf;	 Catch:{ Exception -> 0x0344 }
    L_0x00e8:
        r3 = r1.totalBytesCount;	 Catch:{ Exception -> 0x0344 }
        r8 = r1.downloadedBytes;	 Catch:{ Exception -> 0x0344 }
        if (r3 > r8) goto L_0x00d1;	 Catch:{ Exception -> 0x0344 }
    L_0x00ee:
        goto L_0x00cf;	 Catch:{ Exception -> 0x0344 }
    L_0x00ef:
        r8 = r1.key;	 Catch:{ Exception -> 0x0344 }
        if (r8 == 0) goto L_0x011c;	 Catch:{ Exception -> 0x0344 }
    L_0x00f3:
        r8 = r2.buffer;	 Catch:{ Exception -> 0x0344 }
        r14 = r1.key;	 Catch:{ Exception -> 0x0344 }
        r15 = r1.iv;	 Catch:{ Exception -> 0x0344 }
        r19 = 0;	 Catch:{ Exception -> 0x0344 }
        r20 = 1;	 Catch:{ Exception -> 0x0344 }
        r21 = 0;	 Catch:{ Exception -> 0x0344 }
        r22 = r2.limit();	 Catch:{ Exception -> 0x0344 }
        r16 = r8;	 Catch:{ Exception -> 0x0344 }
        r17 = r14;	 Catch:{ Exception -> 0x0344 }
        r18 = r15;	 Catch:{ Exception -> 0x0344 }
        org.telegram.messenger.Utilities.aesIgeEncryption(r16, r17, r18, r19, r20, r21, r22);	 Catch:{ Exception -> 0x0344 }
        if (r3 == 0) goto L_0x011c;	 Catch:{ Exception -> 0x0344 }
    L_0x010e:
        r8 = r1.bytesCountPadding;	 Catch:{ Exception -> 0x0344 }
        if (r8 == 0) goto L_0x011c;	 Catch:{ Exception -> 0x0344 }
    L_0x0112:
        r8 = r2.limit();	 Catch:{ Exception -> 0x0344 }
        r14 = r1.bytesCountPadding;	 Catch:{ Exception -> 0x0344 }
        r8 = r8 - r14;	 Catch:{ Exception -> 0x0344 }
        r2.limit(r8);	 Catch:{ Exception -> 0x0344 }
    L_0x011c:
        r8 = r1.encryptFile;	 Catch:{ Exception -> 0x0344 }
        if (r8 == 0) goto L_0x0155;	 Catch:{ Exception -> 0x0344 }
    L_0x0120:
        r8 = r24.offset;	 Catch:{ Exception -> 0x0344 }
        r8 = r8 / 16;	 Catch:{ Exception -> 0x0344 }
        r14 = r1.encryptIv;	 Catch:{ Exception -> 0x0344 }
        r15 = r8 & 255;	 Catch:{ Exception -> 0x0344 }
        r15 = (byte) r15;	 Catch:{ Exception -> 0x0344 }
        r14[r13] = r15;	 Catch:{ Exception -> 0x0344 }
        r13 = r1.encryptIv;	 Catch:{ Exception -> 0x0344 }
        r14 = r8 >> 8;	 Catch:{ Exception -> 0x0344 }
        r14 = r14 & 255;	 Catch:{ Exception -> 0x0344 }
        r14 = (byte) r14;	 Catch:{ Exception -> 0x0344 }
        r13[r12] = r14;	 Catch:{ Exception -> 0x0344 }
        r12 = r1.encryptIv;	 Catch:{ Exception -> 0x0344 }
        r13 = r8 >> 16;	 Catch:{ Exception -> 0x0344 }
        r13 = r13 & 255;	 Catch:{ Exception -> 0x0344 }
        r13 = (byte) r13;	 Catch:{ Exception -> 0x0344 }
        r12[r11] = r13;	 Catch:{ Exception -> 0x0344 }
        r11 = r1.encryptIv;	 Catch:{ Exception -> 0x0344 }
        r8 = r8 >> 24;	 Catch:{ Exception -> 0x0344 }
        r8 = r8 & 255;	 Catch:{ Exception -> 0x0344 }
        r8 = (byte) r8;	 Catch:{ Exception -> 0x0344 }
        r11[r10] = r8;	 Catch:{ Exception -> 0x0344 }
        r8 = r2.buffer;	 Catch:{ Exception -> 0x0344 }
        r10 = r1.encryptKey;	 Catch:{ Exception -> 0x0344 }
        r11 = r1.encryptIv;	 Catch:{ Exception -> 0x0344 }
        r12 = r2.limit();	 Catch:{ Exception -> 0x0344 }
        org.telegram.messenger.Utilities.aesCtrDecryption(r8, r10, r11, r5, r12);	 Catch:{ Exception -> 0x0344 }
    L_0x0155:
        r8 = r1.notLoadedBytesRanges;	 Catch:{ Exception -> 0x0344 }
        if (r8 == 0) goto L_0x0163;	 Catch:{ Exception -> 0x0344 }
    L_0x0159:
        r8 = r1.fileOutputStream;	 Catch:{ Exception -> 0x0344 }
        r10 = r24.offset;	 Catch:{ Exception -> 0x0344 }
        r10 = (long) r10;	 Catch:{ Exception -> 0x0344 }
        r8.seek(r10);	 Catch:{ Exception -> 0x0344 }
    L_0x0163:
        r8 = r1.fileOutputStream;	 Catch:{ Exception -> 0x0344 }
        r8 = r8.getChannel();	 Catch:{ Exception -> 0x0344 }
        r2 = r2.buffer;	 Catch:{ Exception -> 0x0344 }
        r8.write(r2);	 Catch:{ Exception -> 0x0344 }
        r2 = r1.notLoadedBytesRanges;	 Catch:{ Exception -> 0x0344 }
        r8 = r24.offset;	 Catch:{ Exception -> 0x0344 }
        r10 = r24.offset;	 Catch:{ Exception -> 0x0344 }
        r10 = r10 + r7;	 Catch:{ Exception -> 0x0344 }
        r1.addPart(r2, r8, r10, r4);	 Catch:{ Exception -> 0x0344 }
        r2 = r1.isCdn;	 Catch:{ Exception -> 0x0344 }
        if (r2 == 0) goto L_0x029d;	 Catch:{ Exception -> 0x0344 }
    L_0x0180:
        r2 = r24.offset;	 Catch:{ Exception -> 0x0344 }
        r2 = r2 / r9;	 Catch:{ Exception -> 0x0344 }
        r6 = r1.notCheckedCdnRanges;	 Catch:{ Exception -> 0x0344 }
        r6 = r6.size();	 Catch:{ Exception -> 0x0344 }
        r7 = r5;	 Catch:{ Exception -> 0x0344 }
    L_0x018c:
        if (r7 >= r6) goto L_0x01a7;	 Catch:{ Exception -> 0x0344 }
    L_0x018e:
        r8 = r1.notCheckedCdnRanges;	 Catch:{ Exception -> 0x0344 }
        r8 = r8.get(r7);	 Catch:{ Exception -> 0x0344 }
        r8 = (org.telegram.messenger.FileLoadOperation.Range) r8;	 Catch:{ Exception -> 0x0344 }
        r10 = r8.start;	 Catch:{ Exception -> 0x0344 }
        if (r10 > r2) goto L_0x01a4;	 Catch:{ Exception -> 0x0344 }
    L_0x019c:
        r8 = r8.end;	 Catch:{ Exception -> 0x0344 }
        if (r2 > r8) goto L_0x01a4;	 Catch:{ Exception -> 0x0344 }
    L_0x01a2:
        r6 = r5;	 Catch:{ Exception -> 0x0344 }
        goto L_0x01a8;	 Catch:{ Exception -> 0x0344 }
    L_0x01a4:
        r7 = r7 + 1;	 Catch:{ Exception -> 0x0344 }
        goto L_0x018c;	 Catch:{ Exception -> 0x0344 }
    L_0x01a7:
        r6 = r4;	 Catch:{ Exception -> 0x0344 }
    L_0x01a8:
        if (r6 != 0) goto L_0x029d;	 Catch:{ Exception -> 0x0344 }
    L_0x01aa:
        r6 = r2 * r9;	 Catch:{ Exception -> 0x0344 }
        r7 = r1.notLoadedBytesRanges;	 Catch:{ Exception -> 0x0344 }
        r7 = r1.getDownloadedLengthFromOffsetInternal(r7, r6, r9);	 Catch:{ Exception -> 0x0344 }
        if (r7 == 0) goto L_0x029d;	 Catch:{ Exception -> 0x0344 }
    L_0x01b4:
        if (r7 == r9) goto L_0x01c5;	 Catch:{ Exception -> 0x0344 }
    L_0x01b6:
        r8 = r1.totalBytesCount;	 Catch:{ Exception -> 0x0344 }
        if (r8 <= 0) goto L_0x01bf;	 Catch:{ Exception -> 0x0344 }
    L_0x01ba:
        r8 = r1.totalBytesCount;	 Catch:{ Exception -> 0x0344 }
        r8 = r8 - r6;	 Catch:{ Exception -> 0x0344 }
        if (r7 == r8) goto L_0x01c5;	 Catch:{ Exception -> 0x0344 }
    L_0x01bf:
        r8 = r1.totalBytesCount;	 Catch:{ Exception -> 0x0344 }
        if (r8 > 0) goto L_0x029d;	 Catch:{ Exception -> 0x0344 }
    L_0x01c3:
        if (r3 == 0) goto L_0x029d;	 Catch:{ Exception -> 0x0344 }
    L_0x01c5:
        r8 = r1.cdnHashes;	 Catch:{ Exception -> 0x0344 }
        r8 = r8.get(r6);	 Catch:{ Exception -> 0x0344 }
        r8 = (org.telegram.tgnet.TLRPC.TL_fileHash) r8;	 Catch:{ Exception -> 0x0344 }
        r10 = r1.fileReadStream;	 Catch:{ Exception -> 0x0344 }
        if (r10 != 0) goto L_0x01e0;	 Catch:{ Exception -> 0x0344 }
    L_0x01d1:
        r9 = new byte[r9];	 Catch:{ Exception -> 0x0344 }
        r1.cdnCheckBytes = r9;	 Catch:{ Exception -> 0x0344 }
        r9 = new java.io.RandomAccessFile;	 Catch:{ Exception -> 0x0344 }
        r10 = r1.cacheFileTemp;	 Catch:{ Exception -> 0x0344 }
        r11 = "r";	 Catch:{ Exception -> 0x0344 }
        r9.<init>(r10, r11);	 Catch:{ Exception -> 0x0344 }
        r1.fileReadStream = r9;	 Catch:{ Exception -> 0x0344 }
    L_0x01e0:
        r9 = r1.fileReadStream;	 Catch:{ Exception -> 0x0344 }
        r10 = (long) r6;	 Catch:{ Exception -> 0x0344 }
        r9.seek(r10);	 Catch:{ Exception -> 0x0344 }
        r9 = r1.fileReadStream;	 Catch:{ Exception -> 0x0344 }
        r10 = r1.cdnCheckBytes;	 Catch:{ Exception -> 0x0344 }
        r9.readFully(r10, r5, r7);	 Catch:{ Exception -> 0x0344 }
        r9 = r1.cdnCheckBytes;	 Catch:{ Exception -> 0x0344 }
        r7 = org.telegram.messenger.Utilities.computeSHA256(r9, r5, r7);	 Catch:{ Exception -> 0x0344 }
        r8 = r8.hash;	 Catch:{ Exception -> 0x0344 }
        r7 = java.util.Arrays.equals(r7, r8);	 Catch:{ Exception -> 0x0344 }
        if (r7 != 0) goto L_0x0291;	 Catch:{ Exception -> 0x0344 }
    L_0x01fb:
        r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED;	 Catch:{ Exception -> 0x0344 }
        if (r2 == 0) goto L_0x0288;	 Catch:{ Exception -> 0x0344 }
    L_0x01ff:
        r2 = r1.location;	 Catch:{ Exception -> 0x0344 }
        if (r2 == 0) goto L_0x0256;	 Catch:{ Exception -> 0x0344 }
    L_0x0203:
        r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0344 }
        r2.<init>();	 Catch:{ Exception -> 0x0344 }
        r3 = "invalid cdn hash ";	 Catch:{ Exception -> 0x0344 }
        r2.append(r3);	 Catch:{ Exception -> 0x0344 }
        r3 = r1.location;	 Catch:{ Exception -> 0x0344 }
        r2.append(r3);	 Catch:{ Exception -> 0x0344 }
        r3 = " id = ";	 Catch:{ Exception -> 0x0344 }
        r2.append(r3);	 Catch:{ Exception -> 0x0344 }
        r3 = r1.location;	 Catch:{ Exception -> 0x0344 }
        r3 = r3.id;	 Catch:{ Exception -> 0x0344 }
        r2.append(r3);	 Catch:{ Exception -> 0x0344 }
        r3 = " local_id = ";	 Catch:{ Exception -> 0x0344 }
        r2.append(r3);	 Catch:{ Exception -> 0x0344 }
        r3 = r1.location;	 Catch:{ Exception -> 0x0344 }
        r3 = r3.local_id;	 Catch:{ Exception -> 0x0344 }
        r2.append(r3);	 Catch:{ Exception -> 0x0344 }
        r3 = " access_hash = ";	 Catch:{ Exception -> 0x0344 }
        r2.append(r3);	 Catch:{ Exception -> 0x0344 }
        r3 = r1.location;	 Catch:{ Exception -> 0x0344 }
        r3 = r3.access_hash;	 Catch:{ Exception -> 0x0344 }
        r2.append(r3);	 Catch:{ Exception -> 0x0344 }
        r3 = " volume_id = ";	 Catch:{ Exception -> 0x0344 }
        r2.append(r3);	 Catch:{ Exception -> 0x0344 }
        r3 = r1.location;	 Catch:{ Exception -> 0x0344 }
        r3 = r3.volume_id;	 Catch:{ Exception -> 0x0344 }
        r2.append(r3);	 Catch:{ Exception -> 0x0344 }
        r3 = " secret = ";	 Catch:{ Exception -> 0x0344 }
        r2.append(r3);	 Catch:{ Exception -> 0x0344 }
        r3 = r1.location;	 Catch:{ Exception -> 0x0344 }
        r3 = r3.secret;	 Catch:{ Exception -> 0x0344 }
        r2.append(r3);	 Catch:{ Exception -> 0x0344 }
        r2 = r2.toString();	 Catch:{ Exception -> 0x0344 }
        org.telegram.messenger.FileLog.m1e(r2);	 Catch:{ Exception -> 0x0344 }
        goto L_0x0288;	 Catch:{ Exception -> 0x0344 }
    L_0x0256:
        r2 = r1.webLocation;	 Catch:{ Exception -> 0x0344 }
        if (r2 == 0) goto L_0x0288;	 Catch:{ Exception -> 0x0344 }
    L_0x025a:
        r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0344 }
        r2.<init>();	 Catch:{ Exception -> 0x0344 }
        r3 = "invalid cdn hash  ";	 Catch:{ Exception -> 0x0344 }
        r2.append(r3);	 Catch:{ Exception -> 0x0344 }
        r3 = r1.webLocation;	 Catch:{ Exception -> 0x0344 }
        r2.append(r3);	 Catch:{ Exception -> 0x0344 }
        r3 = " id = ";	 Catch:{ Exception -> 0x0344 }
        r2.append(r3);	 Catch:{ Exception -> 0x0344 }
        r3 = r1.webLocation;	 Catch:{ Exception -> 0x0344 }
        r3 = r3.url;	 Catch:{ Exception -> 0x0344 }
        r2.append(r3);	 Catch:{ Exception -> 0x0344 }
        r3 = " access_hash = ";	 Catch:{ Exception -> 0x0344 }
        r2.append(r3);	 Catch:{ Exception -> 0x0344 }
        r3 = r1.webLocation;	 Catch:{ Exception -> 0x0344 }
        r3 = r3.access_hash;	 Catch:{ Exception -> 0x0344 }
        r2.append(r3);	 Catch:{ Exception -> 0x0344 }
        r2 = r2.toString();	 Catch:{ Exception -> 0x0344 }
        org.telegram.messenger.FileLog.m1e(r2);	 Catch:{ Exception -> 0x0344 }
    L_0x0288:
        r1.onFail(r5, r5);	 Catch:{ Exception -> 0x0344 }
        r2 = r1.cacheFileTemp;	 Catch:{ Exception -> 0x0344 }
        r2.delete();	 Catch:{ Exception -> 0x0344 }
        return r5;	 Catch:{ Exception -> 0x0344 }
    L_0x0291:
        r7 = r1.cdnHashes;	 Catch:{ Exception -> 0x0344 }
        r7.remove(r6);	 Catch:{ Exception -> 0x0344 }
        r6 = r1.notCheckedCdnRanges;	 Catch:{ Exception -> 0x0344 }
        r7 = r2 + 1;	 Catch:{ Exception -> 0x0344 }
        r1.addPart(r6, r2, r7, r5);	 Catch:{ Exception -> 0x0344 }
    L_0x029d:
        r2 = r1.fiv;	 Catch:{ Exception -> 0x0344 }
        if (r2 == 0) goto L_0x02af;	 Catch:{ Exception -> 0x0344 }
    L_0x02a1:
        r2 = r1.fiv;	 Catch:{ Exception -> 0x0344 }
        r6 = 0;	 Catch:{ Exception -> 0x0344 }
        r2.seek(r6);	 Catch:{ Exception -> 0x0344 }
        r2 = r1.fiv;	 Catch:{ Exception -> 0x0344 }
        r6 = r1.iv;	 Catch:{ Exception -> 0x0344 }
        r2.write(r6);	 Catch:{ Exception -> 0x0344 }
    L_0x02af:
        r2 = r1.totalBytesCount;	 Catch:{ Exception -> 0x0344 }
        if (r2 <= 0) goto L_0x02cc;	 Catch:{ Exception -> 0x0344 }
    L_0x02b3:
        r2 = r1.state;	 Catch:{ Exception -> 0x0344 }
        if (r2 != r4) goto L_0x02cc;	 Catch:{ Exception -> 0x0344 }
    L_0x02b7:
        r23.copytNotLoadedRanges();	 Catch:{ Exception -> 0x0344 }
        r2 = r1.delegate;	 Catch:{ Exception -> 0x0344 }
        r6 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;	 Catch:{ Exception -> 0x0344 }
        r7 = r1.downloadedBytes;	 Catch:{ Exception -> 0x0344 }
        r7 = (float) r7;	 Catch:{ Exception -> 0x0344 }
        r8 = r1.totalBytesCount;	 Catch:{ Exception -> 0x0344 }
        r8 = (float) r8;	 Catch:{ Exception -> 0x0344 }
        r7 = r7 / r8;	 Catch:{ Exception -> 0x0344 }
        r6 = java.lang.Math.min(r6, r7);	 Catch:{ Exception -> 0x0344 }
        r2.didChangedLoadProgress(r1, r6);	 Catch:{ Exception -> 0x0344 }
    L_0x02cc:
        r2 = r5;	 Catch:{ Exception -> 0x0344 }
    L_0x02cd:
        r6 = r1.delayedRequestInfos;	 Catch:{ Exception -> 0x0344 }
        r6 = r6.size();	 Catch:{ Exception -> 0x0344 }
        if (r2 >= r6) goto L_0x0334;	 Catch:{ Exception -> 0x0344 }
    L_0x02d5:
        r6 = r1.delayedRequestInfos;	 Catch:{ Exception -> 0x0344 }
        r6 = r6.get(r2);	 Catch:{ Exception -> 0x0344 }
        r6 = (org.telegram.messenger.FileLoadOperation.RequestInfo) r6;	 Catch:{ Exception -> 0x0344 }
        r7 = r1.notLoadedBytesRanges;	 Catch:{ Exception -> 0x0344 }
        if (r7 != 0) goto L_0x02ed;	 Catch:{ Exception -> 0x0344 }
    L_0x02e1:
        r7 = r1.downloadedBytes;	 Catch:{ Exception -> 0x0344 }
        r8 = r6.offset;	 Catch:{ Exception -> 0x0344 }
        if (r7 != r8) goto L_0x02ea;	 Catch:{ Exception -> 0x0344 }
    L_0x02e9:
        goto L_0x02ed;	 Catch:{ Exception -> 0x0344 }
    L_0x02ea:
        r2 = r2 + 1;	 Catch:{ Exception -> 0x0344 }
        goto L_0x02cd;	 Catch:{ Exception -> 0x0344 }
    L_0x02ed:
        r7 = r1.delayedRequestInfos;	 Catch:{ Exception -> 0x0344 }
        r7.remove(r2);	 Catch:{ Exception -> 0x0344 }
        r7 = 0;	 Catch:{ Exception -> 0x0344 }
        r2 = r1.processRequestResult(r6, r7);	 Catch:{ Exception -> 0x0344 }
        if (r2 != 0) goto L_0x0334;	 Catch:{ Exception -> 0x0344 }
    L_0x02f9:
        r2 = r6.response;	 Catch:{ Exception -> 0x0344 }
        if (r2 == 0) goto L_0x030d;	 Catch:{ Exception -> 0x0344 }
    L_0x02ff:
        r2 = r6.response;	 Catch:{ Exception -> 0x0344 }
        r2.disableFree = r5;	 Catch:{ Exception -> 0x0344 }
        r2 = r6.response;	 Catch:{ Exception -> 0x0344 }
        r2.freeResources();	 Catch:{ Exception -> 0x0344 }
        goto L_0x0334;	 Catch:{ Exception -> 0x0344 }
    L_0x030d:
        r2 = r6.responseWeb;	 Catch:{ Exception -> 0x0344 }
        if (r2 == 0) goto L_0x0321;	 Catch:{ Exception -> 0x0344 }
    L_0x0313:
        r2 = r6.responseWeb;	 Catch:{ Exception -> 0x0344 }
        r2.disableFree = r5;	 Catch:{ Exception -> 0x0344 }
        r2 = r6.responseWeb;	 Catch:{ Exception -> 0x0344 }
        r2.freeResources();	 Catch:{ Exception -> 0x0344 }
        goto L_0x0334;	 Catch:{ Exception -> 0x0344 }
    L_0x0321:
        r2 = r6.responseCdn;	 Catch:{ Exception -> 0x0344 }
        if (r2 == 0) goto L_0x0334;	 Catch:{ Exception -> 0x0344 }
    L_0x0327:
        r2 = r6.responseCdn;	 Catch:{ Exception -> 0x0344 }
        r2.disableFree = r5;	 Catch:{ Exception -> 0x0344 }
        r2 = r6.responseCdn;	 Catch:{ Exception -> 0x0344 }
        r2.freeResources();	 Catch:{ Exception -> 0x0344 }
    L_0x0334:
        if (r3 == 0) goto L_0x033b;	 Catch:{ Exception -> 0x0344 }
    L_0x0336:
        r1.onFinishLoadingFile(r4);	 Catch:{ Exception -> 0x0344 }
        goto L_0x0451;	 Catch:{ Exception -> 0x0344 }
    L_0x033b:
        r23.startDownloadRequest();	 Catch:{ Exception -> 0x0344 }
        goto L_0x0451;	 Catch:{ Exception -> 0x0344 }
    L_0x0340:
        r1.onFinishLoadingFile(r4);	 Catch:{ Exception -> 0x0344 }
        return r5;
    L_0x0344:
        r0 = move-exception;
        r2 = r0;
        r1.onFail(r5, r5);
        org.telegram.messenger.FileLog.m3e(r2);
        goto L_0x0451;
    L_0x034e:
        r7 = 0;
        r3 = r2.text;
        r6 = "FILE_MIGRATE_";
        r3 = r3.contains(r6);
        if (r3 == 0) goto L_0x038d;
    L_0x0359:
        r2 = r2.text;
        r3 = "FILE_MIGRATE_";
        r4 = "";
        r2 = r2.replace(r3, r4);
        r3 = new java.util.Scanner;
        r3.<init>(r2);
        r2 = "";
        r3.useDelimiter(r2);
        r2 = r3.nextInt();	 Catch:{ Exception -> 0x0376 }
        r3 = java.lang.Integer.valueOf(r2);	 Catch:{ Exception -> 0x0376 }
        goto L_0x0377;
    L_0x0376:
        r3 = r7;
    L_0x0377:
        if (r3 != 0) goto L_0x037e;
    L_0x0379:
        r1.onFail(r5, r5);
        goto L_0x0451;
    L_0x037e:
        r2 = r3.intValue();
        r1.datacenter_id = r2;
        r1.downloadedBytes = r5;
        r1.requestedBytesCount = r5;
        r23.startDownloadRequest();
        goto L_0x0451;
    L_0x038d:
        r3 = r2.text;
        r6 = "OFFSET_INVALID";
        r3 = r3.contains(r6);
        if (r3 == 0) goto L_0x03b1;
    L_0x0397:
        r2 = r1.downloadedBytes;
        r3 = r1.currentDownloadChunkSize;
        r2 = r2 % r3;
        if (r2 != 0) goto L_0x03ac;
    L_0x039e:
        r1.onFinishLoadingFile(r4);	 Catch:{ Exception -> 0x03a3 }
        goto L_0x0451;
    L_0x03a3:
        r0 = move-exception;
        org.telegram.messenger.FileLog.m3e(r0);
        r1.onFail(r5, r5);
        goto L_0x0451;
    L_0x03ac:
        r1.onFail(r5, r5);
        goto L_0x0451;
    L_0x03b1:
        r2 = r2.text;
        r3 = "RETRY_LIMIT";
        r2 = r2.contains(r3);
        if (r2 == 0) goto L_0x03c1;
    L_0x03bb:
        r2 = 2;
        r1.onFail(r5, r2);
        goto L_0x0451;
    L_0x03c1:
        r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r2 == 0) goto L_0x044e;
    L_0x03c5:
        r2 = r1.location;
        if (r2 == 0) goto L_0x041c;
    L_0x03c9:
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r3 = "";
        r2.append(r3);
        r3 = r1.location;
        r2.append(r3);
        r3 = " id = ";
        r2.append(r3);
        r3 = r1.location;
        r3 = r3.id;
        r2.append(r3);
        r3 = " local_id = ";
        r2.append(r3);
        r3 = r1.location;
        r3 = r3.local_id;
        r2.append(r3);
        r3 = " access_hash = ";
        r2.append(r3);
        r3 = r1.location;
        r3 = r3.access_hash;
        r2.append(r3);
        r3 = " volume_id = ";
        r2.append(r3);
        r3 = r1.location;
        r3 = r3.volume_id;
        r2.append(r3);
        r3 = " secret = ";
        r2.append(r3);
        r3 = r1.location;
        r3 = r3.secret;
        r2.append(r3);
        r2 = r2.toString();
        org.telegram.messenger.FileLog.m1e(r2);
        goto L_0x044e;
    L_0x041c:
        r2 = r1.webLocation;
        if (r2 == 0) goto L_0x044e;
    L_0x0420:
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r3 = "";
        r2.append(r3);
        r3 = r1.webLocation;
        r2.append(r3);
        r3 = " id = ";
        r2.append(r3);
        r3 = r1.webLocation;
        r3 = r3.url;
        r2.append(r3);
        r3 = " access_hash = ";
        r2.append(r3);
        r3 = r1.webLocation;
        r3 = r3.access_hash;
        r2.append(r3);
        r2 = r2.toString();
        org.telegram.messenger.FileLog.m1e(r2);
    L_0x044e:
        r1.onFail(r5, r5);
    L_0x0451:
        return r5;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.FileLoadOperation.processRequestResult(org.telegram.messenger.FileLoadOperation$RequestInfo, org.telegram.tgnet.TLRPC$TL_error):boolean");
    }

    private void onFail(boolean z, final int i) {
        cleanup();
        this.state = 2;
        if (z) {
            Utilities.stageQueue.postRunnable(new Runnable() {
                public void run() {
                    FileLoadOperation.this.delegate.didFailedLoadingFile(FileLoadOperation.this, i);
                }
            });
        } else {
            this.delegate.didFailedLoadingFile(this, i);
        }
    }

    private void clearOperaion(RequestInfo requestInfo) {
        int i = ConnectionsManager.DEFAULT_DATACENTER_ID;
        for (int i2 = 0; i2 < this.requestInfos.size(); i2++) {
            RequestInfo requestInfo2 = (RequestInfo) this.requestInfos.get(i2);
            i = Math.min(requestInfo2.offset, i);
            removePart(this.notRequestedBytesRanges, requestInfo2.offset, requestInfo2.offset + this.currentDownloadChunkSize);
            if (requestInfo != requestInfo2) {
                if (requestInfo2.requestToken != 0) {
                    ConnectionsManager.getInstance(this.currentAccount).cancelRequest(requestInfo2.requestToken, true);
                }
            }
        }
        this.requestInfos.clear();
        for (requestInfo = null; requestInfo < this.delayedRequestInfos.size(); requestInfo++) {
            RequestInfo requestInfo3 = (RequestInfo) this.delayedRequestInfos.get(requestInfo);
            removePart(this.notRequestedBytesRanges, requestInfo3.offset, requestInfo3.offset + this.currentDownloadChunkSize);
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
        if (this.notLoadedBytesRanges == null) {
            this.downloadedBytes = i;
            this.requestedBytesCount = i;
        }
    }

    private void startDownloadRequest() {
        if (!this.paused && r0.state == 1) {
            if (r0.requestInfos.size() + r0.delayedRequestInfos.size() < r0.currentMaxDownloadRequests) {
                int max = r0.totalBytesCount > 0 ? Math.max(0, r0.currentMaxDownloadRequests - r0.requestInfos.size()) : 1;
                int i = 0;
                while (i < max) {
                    int size;
                    int i2;
                    if (r0.notRequestedBytesRanges != null) {
                        size = r0.notRequestedBytesRanges.size();
                        i2 = ConnectionsManager.DEFAULT_DATACENTER_ID;
                        int i3 = i2;
                        for (int i4 = 0; i4 < size; i4++) {
                            Range range = (Range) r0.notRequestedBytesRanges.get(i4);
                            if (r0.streamStartOffset != 0) {
                                if (range.start <= r0.streamStartOffset && range.end > r0.streamStartOffset) {
                                    i3 = r0.streamStartOffset;
                                    i2 = ConnectionsManager.DEFAULT_DATACENTER_ID;
                                    break;
                                } else if (r0.streamStartOffset < range.start && range.start < r9) {
                                    i3 = range.start;
                                }
                            }
                            i2 = Math.min(i2, range.start);
                        }
                        if (i3 == ConnectionsManager.DEFAULT_DATACENTER_ID) {
                            if (i2 == ConnectionsManager.DEFAULT_DATACENTER_ID) {
                                break;
                            }
                        } else {
                            i2 = i3;
                        }
                    } else {
                        i2 = r0.requestedBytesCount;
                    }
                    if (r0.notRequestedBytesRanges != null) {
                        addPart(r0.notRequestedBytesRanges, i2, r0.currentDownloadChunkSize + i2, false);
                    }
                    if (r0.totalBytesCount > 0 && i2 >= r0.totalBytesCount) {
                        break;
                    }
                    boolean z;
                    int i5;
                    TLObject tL_upload_getCdnFile;
                    int i6;
                    final TLObject tLObject;
                    final RequestInfo requestInfo;
                    if (r0.totalBytesCount > 0 && i != max - 1) {
                        if (r0.totalBytesCount <= 0 || r0.currentDownloadChunkSize + i2 < r0.totalBytesCount) {
                            z = false;
                            i5 = r0.requestsCount % 2 != 0 ? 2 : ConnectionsManager.ConnectionTypeDownload2;
                            size = (r0.isForceRequest ? 32 : 0) | 2;
                            if (r0.isCdn) {
                                tL_upload_getCdnFile = new TL_upload_getCdnFile();
                                tL_upload_getCdnFile.file_token = r0.cdnToken;
                                tL_upload_getCdnFile.offset = i2;
                                tL_upload_getCdnFile.limit = r0.currentDownloadChunkSize;
                                size |= 1;
                            } else if (r0.webLocation == null) {
                                tL_upload_getCdnFile = new TL_upload_getWebFile();
                                tL_upload_getCdnFile.location = r0.webLocation;
                                tL_upload_getCdnFile.offset = i2;
                                tL_upload_getCdnFile.limit = r0.currentDownloadChunkSize;
                            } else {
                                tL_upload_getCdnFile = new TL_upload_getFile();
                                tL_upload_getCdnFile.location = r0.location;
                                tL_upload_getCdnFile.offset = i2;
                                tL_upload_getCdnFile.limit = r0.currentDownloadChunkSize;
                            }
                            i6 = size;
                            tLObject = tL_upload_getCdnFile;
                            r0.requestedBytesCount += r0.currentDownloadChunkSize;
                            requestInfo = new RequestInfo();
                            r0.requestInfos.add(requestInfo);
                            requestInfo.offset = i2;
                            requestInfo.requestToken = ConnectionsManager.getInstance(r0.currentAccount).sendRequest(tLObject, new RequestDelegate() {

                                /* renamed from: org.telegram.messenger.FileLoadOperation$12$1 */
                                class C18001 implements RequestDelegate {
                                    C18001() {
                                    }

                                    public void run(TLObject tLObject, TL_error tL_error) {
                                        int i = 0;
                                        FileLoadOperation.this.reuploadingCdn = false;
                                        if (tL_error == null) {
                                            Vector vector = (Vector) tLObject;
                                            if (vector.objects.isEmpty() == null) {
                                                if (FileLoadOperation.this.cdnHashes == null) {
                                                    FileLoadOperation.this.cdnHashes = new SparseArray();
                                                }
                                                while (i < vector.objects.size()) {
                                                    TL_fileHash tL_fileHash = (TL_fileHash) vector.objects.get(i);
                                                    FileLoadOperation.this.cdnHashes.put(tL_fileHash.offset, tL_fileHash);
                                                    i++;
                                                }
                                            }
                                            FileLoadOperation.this.startDownloadRequest();
                                            return;
                                        }
                                        if (tL_error.text.equals("FILE_TOKEN_INVALID") == null) {
                                            if (tL_error.text.equals("REQUEST_TOKEN_INVALID") == null) {
                                                FileLoadOperation.this.onFail(false, 0);
                                                return;
                                            }
                                        }
                                        FileLoadOperation.this.isCdn = false;
                                        FileLoadOperation.this.clearOperaion(requestInfo);
                                        FileLoadOperation.this.startDownloadRequest();
                                    }
                                }

                                public void run(TLObject tLObject, TL_error tL_error) {
                                    if (!FileLoadOperation.this.requestInfos.contains(requestInfo)) {
                                        return;
                                    }
                                    if (tL_error != null && (tLObject instanceof TL_upload_getCdnFile) && tL_error.text.equals("FILE_TOKEN_INVALID")) {
                                        FileLoadOperation.this.isCdn = false;
                                        FileLoadOperation.this.clearOperaion(requestInfo);
                                        FileLoadOperation.this.startDownloadRequest();
                                        return;
                                    }
                                    if (tLObject instanceof TL_upload_fileCdnRedirect) {
                                        TL_upload_fileCdnRedirect tL_upload_fileCdnRedirect = (TL_upload_fileCdnRedirect) tLObject;
                                        if (tL_upload_fileCdnRedirect.file_hashes.isEmpty() == null) {
                                            if (FileLoadOperation.this.cdnHashes == null) {
                                                FileLoadOperation.this.cdnHashes = new SparseArray();
                                            }
                                            for (tL_error = null; tL_error < tL_upload_fileCdnRedirect.file_hashes.size(); tL_error++) {
                                                TL_fileHash tL_fileHash = (TL_fileHash) tL_upload_fileCdnRedirect.file_hashes.get(tL_error);
                                                FileLoadOperation.this.cdnHashes.put(tL_fileHash.offset, tL_fileHash);
                                            }
                                        }
                                        if (!(tL_upload_fileCdnRedirect.encryption_iv == null || tL_upload_fileCdnRedirect.encryption_key == null || tL_upload_fileCdnRedirect.encryption_iv.length != 16)) {
                                            if (tL_upload_fileCdnRedirect.encryption_key.length == 32) {
                                                FileLoadOperation.this.isCdn = true;
                                                if (FileLoadOperation.this.notCheckedCdnRanges == null) {
                                                    FileLoadOperation.this.notCheckedCdnRanges = new ArrayList();
                                                    FileLoadOperation.this.notCheckedCdnRanges.add(new Range(0, FileLoadOperation.maxCdnParts));
                                                }
                                                FileLoadOperation.this.cdnDatacenterId = tL_upload_fileCdnRedirect.dc_id;
                                                FileLoadOperation.this.cdnIv = tL_upload_fileCdnRedirect.encryption_iv;
                                                FileLoadOperation.this.cdnKey = tL_upload_fileCdnRedirect.encryption_key;
                                                FileLoadOperation.this.cdnToken = tL_upload_fileCdnRedirect.file_token;
                                                FileLoadOperation.this.clearOperaion(requestInfo);
                                                FileLoadOperation.this.startDownloadRequest();
                                            }
                                        }
                                        tLObject = new TL_error();
                                        tLObject.text = "bad redirect response";
                                        tLObject.code = 400;
                                        FileLoadOperation.this.processRequestResult(requestInfo, tLObject);
                                    } else if (!(tLObject instanceof TL_upload_cdnFileReuploadNeeded)) {
                                        if (tLObject instanceof TL_upload_file) {
                                            requestInfo.response = (TL_upload_file) tLObject;
                                        } else if (tLObject instanceof TL_upload_webFile) {
                                            requestInfo.responseWeb = (TL_upload_webFile) tLObject;
                                            if (FileLoadOperation.this.totalBytesCount == 0 && requestInfo.responseWeb.size != 0) {
                                                FileLoadOperation.this.totalBytesCount = requestInfo.responseWeb.size;
                                            }
                                        } else {
                                            requestInfo.responseCdn = (TL_upload_cdnFile) tLObject;
                                        }
                                        if (tLObject != null) {
                                            if (FileLoadOperation.this.currentType == ConnectionsManager.FileTypeAudio) {
                                                StatsController.getInstance(FileLoadOperation.this.currentAccount).incrementReceivedBytesCount(tLObject.networkType, 3, (long) (tLObject.getObjectSize() + 4));
                                            } else if (FileLoadOperation.this.currentType == ConnectionsManager.FileTypeVideo) {
                                                StatsController.getInstance(FileLoadOperation.this.currentAccount).incrementReceivedBytesCount(tLObject.networkType, 2, (long) (tLObject.getObjectSize() + 4));
                                            } else if (FileLoadOperation.this.currentType == 16777216) {
                                                StatsController.getInstance(FileLoadOperation.this.currentAccount).incrementReceivedBytesCount(tLObject.networkType, 4, (long) (tLObject.getObjectSize() + 4));
                                            } else if (FileLoadOperation.this.currentType == ConnectionsManager.FileTypeFile) {
                                                StatsController.getInstance(FileLoadOperation.this.currentAccount).incrementReceivedBytesCount(tLObject.networkType, 5, (long) (tLObject.getObjectSize() + 4));
                                            }
                                        }
                                        FileLoadOperation.this.processRequestResult(requestInfo, tL_error);
                                    } else if (FileLoadOperation.this.reuploadingCdn == null) {
                                        FileLoadOperation.this.clearOperaion(requestInfo);
                                        FileLoadOperation.this.reuploadingCdn = true;
                                        TL_upload_cdnFileReuploadNeeded tL_upload_cdnFileReuploadNeeded = (TL_upload_cdnFileReuploadNeeded) tLObject;
                                        TLObject tL_upload_reuploadCdnFile = new TL_upload_reuploadCdnFile();
                                        tL_upload_reuploadCdnFile.file_token = FileLoadOperation.this.cdnToken;
                                        tL_upload_reuploadCdnFile.request_token = tL_upload_cdnFileReuploadNeeded.request_token;
                                        ConnectionsManager.getInstance(FileLoadOperation.this.currentAccount).sendRequest(tL_upload_reuploadCdnFile, new C18001(), null, null, 0, FileLoadOperation.this.datacenter_id, 1, true);
                                    }
                                }
                            }, null, null, i6, r0.isCdn ? r0.cdnDatacenterId : r0.datacenter_id, i5, z);
                            r0.requestsCount++;
                            i++;
                        }
                    }
                    z = true;
                    if (r0.requestsCount % 2 != 0) {
                    }
                    if (r0.isForceRequest) {
                    }
                    size = (r0.isForceRequest ? 32 : 0) | 2;
                    if (r0.isCdn) {
                        tL_upload_getCdnFile = new TL_upload_getCdnFile();
                        tL_upload_getCdnFile.file_token = r0.cdnToken;
                        tL_upload_getCdnFile.offset = i2;
                        tL_upload_getCdnFile.limit = r0.currentDownloadChunkSize;
                        size |= 1;
                    } else if (r0.webLocation == null) {
                        tL_upload_getCdnFile = new TL_upload_getFile();
                        tL_upload_getCdnFile.location = r0.location;
                        tL_upload_getCdnFile.offset = i2;
                        tL_upload_getCdnFile.limit = r0.currentDownloadChunkSize;
                    } else {
                        tL_upload_getCdnFile = new TL_upload_getWebFile();
                        tL_upload_getCdnFile.location = r0.webLocation;
                        tL_upload_getCdnFile.offset = i2;
                        tL_upload_getCdnFile.limit = r0.currentDownloadChunkSize;
                    }
                    i6 = size;
                    tLObject = tL_upload_getCdnFile;
                    r0.requestedBytesCount += r0.currentDownloadChunkSize;
                    requestInfo = new RequestInfo();
                    r0.requestInfos.add(requestInfo);
                    requestInfo.offset = i2;
                    if (r0.isCdn) {
                    }
                    requestInfo.requestToken = ConnectionsManager.getInstance(r0.currentAccount).sendRequest(tLObject, /* anonymous class already generated */, null, null, i6, r0.isCdn ? r0.cdnDatacenterId : r0.datacenter_id, i5, z);
                    r0.requestsCount++;
                    i++;
                }
            }
        }
    }

    public void setDelegate(FileLoadOperationDelegate fileLoadOperationDelegate) {
        this.delegate = fileLoadOperationDelegate;
    }
}
