package org.telegram.messenger;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Scanner;
import org.telegram.messenger.exoplayer2.util.MimeTypes;
import org.telegram.messenger.volley.DefaultRetryPolicy;
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
import org.telegram.tgnet.TLRPC.TL_fileLocation;
import org.telegram.tgnet.TLRPC.TL_inputDocumentFileLocation;
import org.telegram.tgnet.TLRPC.TL_inputEncryptedFileLocation;
import org.telegram.tgnet.TLRPC.TL_inputFileLocation;
import org.telegram.tgnet.TLRPC.TL_upload_file;
import org.telegram.tgnet.TLRPC.TL_upload_getFile;

public class FileLoadOperation {
    private static final int bigFileSizeFrom = 1048576;
    private static final int downloadChunkSize = 32768;
    private static final int downloadChunkSizeBig = 131072;
    private static final int maxDownloadRequests = 4;
    private static final int maxDownloadRequestsBig = 2;
    private static final int stateDownloading = 1;
    private static final int stateFailed = 2;
    private static final int stateFinished = 3;
    private static final int stateIdle = 0;
    private int bytesCountPadding;
    private File cacheFileFinal;
    private File cacheFileTemp;
    private File cacheIvTemp;
    private int currentDownloadChunkSize;
    private int currentMaxDownloadRequests;
    private int currentType;
    private int datacenter_id;
    private ArrayList<RequestInfo> delayedRequestInfos;
    private FileLoadOperationDelegate delegate;
    private int downloadedBytes;
    private String ext;
    private RandomAccessFile fileOutputStream;
    private RandomAccessFile fiv;
    private boolean isForceRequest;
    private byte[] iv;
    private byte[] key;
    private InputFileLocation location;
    private int nextDownloadOffset;
    private int renameRetryCount;
    private ArrayList<RequestInfo> requestInfos;
    private int requestsCount;
    private boolean started;
    private volatile int state = 0;
    private File storePath;
    private File tempPath;
    private int totalBytesCount;

    public interface FileLoadOperationDelegate {
        void didChangedLoadProgress(FileLoadOperation fileLoadOperation, float f);

        void didFailedLoadingFile(FileLoadOperation fileLoadOperation, int i);

        void didFinishLoadingFile(FileLoadOperation fileLoadOperation, File file);
    }

    private static class RequestInfo {
        private int offset;
        private int requestToken;
        private TL_upload_file response;

        private RequestInfo() {
        }
    }

    public FileLoadOperation(FileLocation photoLocation, String extension, int size) {
        if (photoLocation instanceof TL_fileEncryptedLocation) {
            this.location = new TL_inputEncryptedFileLocation();
            this.location.id = photoLocation.volume_id;
            this.location.volume_id = photoLocation.volume_id;
            this.location.access_hash = photoLocation.secret;
            this.location.local_id = photoLocation.local_id;
            this.iv = new byte[32];
            System.arraycopy(photoLocation.iv, 0, this.iv, 0, this.iv.length);
            this.key = photoLocation.key;
            this.datacenter_id = photoLocation.dc_id;
        } else if (photoLocation instanceof TL_fileLocation) {
            this.location = new TL_inputFileLocation();
            this.location.volume_id = photoLocation.volume_id;
            this.location.secret = photoLocation.secret;
            this.location.local_id = photoLocation.local_id;
            this.datacenter_id = photoLocation.dc_id;
        }
        this.currentType = 16777216;
        this.totalBytesCount = size;
        if (extension == null) {
            extension = "jpg";
        }
        this.ext = extension;
    }

    public FileLoadOperation(Document documentLocation) {
        int i = -1;
        try {
            String str;
            boolean z;
            if (documentLocation instanceof TL_documentEncrypted) {
                this.location = new TL_inputEncryptedFileLocation();
                this.location.id = documentLocation.id;
                this.location.access_hash = documentLocation.access_hash;
                this.datacenter_id = documentLocation.dc_id;
                this.iv = new byte[32];
                System.arraycopy(documentLocation.iv, 0, this.iv, 0, this.iv.length);
                this.key = documentLocation.key;
            } else if (documentLocation instanceof TL_document) {
                this.location = new TL_inputDocumentFileLocation();
                this.location.id = documentLocation.id;
                this.location.access_hash = documentLocation.access_hash;
                this.datacenter_id = documentLocation.dc_id;
            }
            this.totalBytesCount = documentLocation.size;
            if (!(this.key == null || this.totalBytesCount % 16 == 0)) {
                this.bytesCountPadding = 16 - (this.totalBytesCount % 16);
                this.totalBytesCount += this.bytesCountPadding;
            }
            this.ext = FileLoader.getDocumentFileName(documentLocation);
            if (this.ext != null) {
                int idx = this.ext.lastIndexOf(46);
                if (idx != -1) {
                    this.ext = this.ext.substring(idx);
                    if ("audio/ogg".equals(documentLocation.mime_type)) {
                        this.currentType = ConnectionsManager.FileTypeAudio;
                    } else if (MimeTypes.VIDEO_MP4.equals(documentLocation.mime_type)) {
                        this.currentType = ConnectionsManager.FileTypeFile;
                    } else {
                        this.currentType = ConnectionsManager.FileTypeVideo;
                    }
                    if (this.ext.length() > 1) {
                    }
                    if (documentLocation.mime_type == null) {
                        str = documentLocation.mime_type;
                        switch (str.hashCode()) {
                            case 187091926:
                                if (str.equals("audio/ogg")) {
                                    z = true;
                                    break;
                                }
                                break;
                            case 1331848029:
                                if (str.equals(MimeTypes.VIDEO_MP4)) {
                                    i = 0;
                                    break;
                                }
                                break;
                        }
                        switch (i) {
                            case 0:
                                this.ext = ".mp4";
                                return;
                            case 1:
                                this.ext = ".ogg";
                                return;
                            default:
                                this.ext = "";
                                return;
                        }
                    }
                    this.ext = "";
                    return;
                }
            }
            this.ext = "";
            if ("audio/ogg".equals(documentLocation.mime_type)) {
                this.currentType = ConnectionsManager.FileTypeAudio;
            } else if (MimeTypes.VIDEO_MP4.equals(documentLocation.mime_type)) {
                this.currentType = ConnectionsManager.FileTypeFile;
            } else {
                this.currentType = ConnectionsManager.FileTypeVideo;
            }
            if (this.ext.length() > 1) {
                if (documentLocation.mime_type == null) {
                    this.ext = "";
                    return;
                }
                str = documentLocation.mime_type;
                switch (str.hashCode()) {
                    case 187091926:
                        if (str.equals("audio/ogg")) {
                            z = true;
                            break;
                        }
                        break;
                    case 1331848029:
                        if (str.equals(MimeTypes.VIDEO_MP4)) {
                            i = 0;
                            break;
                        }
                        break;
                }
                switch (i) {
                    case 0:
                        this.ext = ".mp4";
                        return;
                    case 1:
                        this.ext = ".ogg";
                        return;
                    default:
                        this.ext = "";
                        return;
                }
            }
        } catch (Throwable e) {
            FileLog.e("tmessages", e);
            onFail(true, 0);
        }
    }

    public void setForceRequest(boolean forceRequest) {
        this.isForceRequest = forceRequest;
    }

    public boolean isForceRequest() {
        return this.isForceRequest;
    }

    public void setPaths(File store, File temp) {
        this.storePath = store;
        this.tempPath = temp;
    }

    public boolean wasStarted() {
        return this.started;
    }

    public String getFileName() {
        return this.location.volume_id + "_" + this.location.local_id + "." + this.ext;
    }

    public boolean start() {
        if (this.state != 0) {
            return false;
        }
        if (this.location == null) {
            onFail(true, 0);
            return false;
        }
        String fileNameTemp;
        String fileNameFinal;
        String fileNameIv = null;
        if (this.location.volume_id == 0 || this.location.local_id == 0) {
            if (this.datacenter_id == 0 || this.location.id == 0) {
                onFail(true, 0);
                return false;
            }
            fileNameTemp = this.datacenter_id + "_" + this.location.id + ".temp";
            fileNameFinal = this.datacenter_id + "_" + this.location.id + this.ext;
            if (this.key != null) {
                fileNameIv = this.datacenter_id + "_" + this.location.id + ".iv";
            }
        } else if (this.datacenter_id == Integer.MIN_VALUE || this.location.volume_id == -2147483648L || this.datacenter_id == 0) {
            onFail(true, 0);
            return false;
        } else {
            fileNameTemp = this.location.volume_id + "_" + this.location.local_id + ".temp";
            fileNameFinal = this.location.volume_id + "_" + this.location.local_id + "." + this.ext;
            if (this.key != null) {
                fileNameIv = this.location.volume_id + "_" + this.location.local_id + ".iv";
            }
        }
        this.currentDownloadChunkSize = this.totalBytesCount >= 1048576 ? 131072 : 32768;
        this.currentMaxDownloadRequests = this.totalBytesCount >= 1048576 ? 2 : 4;
        this.requestInfos = new ArrayList(this.currentMaxDownloadRequests);
        this.delayedRequestInfos = new ArrayList(this.currentMaxDownloadRequests - 1);
        this.state = 1;
        this.cacheFileFinal = new File(this.storePath, fileNameFinal);
        if (!(!this.cacheFileFinal.exists() || this.totalBytesCount == 0 || ((long) this.totalBytesCount) == this.cacheFileFinal.length())) {
            this.cacheFileFinal.delete();
        }
        if (this.cacheFileFinal.exists()) {
            this.started = true;
            try {
                onFinishLoadingFile(false);
            } catch (Exception e) {
                onFail(true, 0);
            }
        } else {
            this.cacheFileTemp = new File(this.tempPath, fileNameTemp);
            if (this.cacheFileTemp.exists()) {
                this.downloadedBytes = (int) this.cacheFileTemp.length();
                int i = (this.downloadedBytes / this.currentDownloadChunkSize) * this.currentDownloadChunkSize;
                this.downloadedBytes = i;
                this.nextDownloadOffset = i;
            }
            if (BuildVars.DEBUG_VERSION) {
                FileLog.d("tmessages", "start loading file to temp = " + this.cacheFileTemp + " final = " + this.cacheFileFinal);
            }
            if (fileNameIv != null) {
                this.cacheIvTemp = new File(this.tempPath, fileNameIv);
                try {
                    this.fiv = new RandomAccessFile(this.cacheIvTemp, "rws");
                    long len = this.cacheIvTemp.length();
                    if (len <= 0 || len % 32 != 0) {
                        this.downloadedBytes = 0;
                    } else {
                        this.fiv.read(this.iv, 0, 32);
                    }
                } catch (Throwable e2) {
                    FileLog.e("tmessages", e2);
                    this.downloadedBytes = 0;
                }
            }
            try {
                this.fileOutputStream = new RandomAccessFile(this.cacheFileTemp, "rws");
                if (this.downloadedBytes != 0) {
                    this.fileOutputStream.seek((long) this.downloadedBytes);
                }
            } catch (Throwable e22) {
                FileLog.e("tmessages", e22);
            }
            if (this.fileOutputStream == null) {
                onFail(true, 0);
                return false;
            }
            this.started = true;
            Utilities.stageQueue.postRunnable(new Runnable() {
                public void run() {
                    if (FileLoadOperation.this.totalBytesCount == 0 || FileLoadOperation.this.downloadedBytes != FileLoadOperation.this.totalBytesCount) {
                        FileLoadOperation.this.startDownloadRequest();
                        return;
                    }
                    try {
                        FileLoadOperation.this.onFinishLoadingFile(false);
                    } catch (Exception e) {
                        FileLoadOperation.this.onFail(true, 0);
                    }
                }
            });
        }
        return true;
    }

    public void cancel() {
        Utilities.stageQueue.postRunnable(new Runnable() {
            public void run() {
                if (FileLoadOperation.this.state != 3 && FileLoadOperation.this.state != 2) {
                    if (FileLoadOperation.this.requestInfos != null) {
                        for (int a = 0; a < FileLoadOperation.this.requestInfos.size(); a++) {
                            RequestInfo requestInfo = (RequestInfo) FileLoadOperation.this.requestInfos.get(a);
                            if (requestInfo.requestToken != 0) {
                                ConnectionsManager.getInstance().cancelRequest(requestInfo.requestToken, true);
                            }
                        }
                    }
                    FileLoadOperation.this.onFail(false, 1);
                }
            }
        });
    }

    private void cleanup() {
        try {
            if (this.fileOutputStream != null) {
                try {
                    this.fileOutputStream.getChannel().close();
                } catch (Throwable e) {
                    FileLog.e("tmessages", e);
                }
                this.fileOutputStream.close();
                this.fileOutputStream = null;
            }
        } catch (Throwable e2) {
            FileLog.e("tmessages", e2);
        }
        try {
            if (this.fiv != null) {
                this.fiv.close();
                this.fiv = null;
            }
        } catch (Throwable e22) {
            FileLog.e("tmessages", e22);
        }
        if (this.delayedRequestInfos != null) {
            for (int a = 0; a < this.delayedRequestInfos.size(); a++) {
                RequestInfo requestInfo = (RequestInfo) this.delayedRequestInfos.get(a);
                if (requestInfo.response != null) {
                    requestInfo.response.disableFree = false;
                    requestInfo.response.freeResources();
                }
            }
            this.delayedRequestInfos.clear();
        }
    }

    private void onFinishLoadingFile(final boolean increment) throws Exception {
        if (this.state == 1) {
            this.state = 3;
            cleanup();
            if (this.cacheIvTemp != null) {
                this.cacheIvTemp.delete();
                this.cacheIvTemp = null;
            }
            if (!(this.cacheFileTemp == null || this.cacheFileTemp.renameTo(this.cacheFileFinal))) {
                if (BuildVars.DEBUG_VERSION) {
                    FileLog.e("tmessages", "unable to rename temp = " + this.cacheFileTemp + " to final = " + this.cacheFileFinal + " retry = " + this.renameRetryCount);
                }
                this.renameRetryCount++;
                if (this.renameRetryCount < 3) {
                    this.state = 1;
                    Utilities.stageQueue.postRunnable(new Runnable() {
                        public void run() {
                            try {
                                FileLoadOperation.this.onFinishLoadingFile(increment);
                            } catch (Exception e) {
                                FileLoadOperation.this.onFail(false, 0);
                            }
                        }
                    }, 200);
                    return;
                }
                this.cacheFileFinal = this.cacheFileTemp;
            }
            if (BuildVars.DEBUG_VERSION) {
                FileLog.e("tmessages", "finished downloading file to " + this.cacheFileFinal);
            }
            this.delegate.didFinishLoadingFile(this, this.cacheFileFinal);
            if (!increment) {
                return;
            }
            if (this.currentType == ConnectionsManager.FileTypeAudio) {
                StatsController.getInstance().incrementReceivedItemsCount(ConnectionsManager.getCurrentNetworkType(), 3, 1);
            } else if (this.currentType == ConnectionsManager.FileTypeVideo) {
                StatsController.getInstance().incrementReceivedItemsCount(ConnectionsManager.getCurrentNetworkType(), 2, 1);
            } else if (this.currentType == 16777216) {
                StatsController.getInstance().incrementReceivedItemsCount(ConnectionsManager.getCurrentNetworkType(), 4, 1);
            } else if (this.currentType == ConnectionsManager.FileTypeFile) {
                StatsController.getInstance().incrementReceivedItemsCount(ConnectionsManager.getCurrentNetworkType(), 5, 1);
            }
        }
    }

    private void processRequestResult(RequestInfo requestInfo, TL_error error) {
        this.requestInfos.remove(requestInfo);
        if (error == null) {
            try {
                if (this.downloadedBytes != requestInfo.offset) {
                    if (this.state == 1) {
                        this.delayedRequestInfos.add(requestInfo);
                        requestInfo.response.disableFree = true;
                    }
                } else if (requestInfo.response.bytes == null || requestInfo.response.bytes.limit() == 0) {
                    onFinishLoadingFile(true);
                } else {
                    int currentBytesSize = requestInfo.response.bytes.limit();
                    this.downloadedBytes += currentBytesSize;
                    boolean finishedDownloading = currentBytesSize != this.currentDownloadChunkSize || ((this.totalBytesCount == this.downloadedBytes || this.downloadedBytes % this.currentDownloadChunkSize != 0) && (this.totalBytesCount <= 0 || this.totalBytesCount <= this.downloadedBytes));
                    if (this.key != null) {
                        Utilities.aesIgeEncryption(requestInfo.response.bytes.buffer, this.key, this.iv, false, true, 0, requestInfo.response.bytes.limit());
                        if (finishedDownloading && this.bytesCountPadding != 0) {
                            requestInfo.response.bytes.limit(requestInfo.response.bytes.limit() - this.bytesCountPadding);
                        }
                    }
                    if (this.fileOutputStream != null) {
                        this.fileOutputStream.getChannel().write(requestInfo.response.bytes.buffer);
                    }
                    if (this.fiv != null) {
                        this.fiv.seek(0);
                        this.fiv.write(this.iv);
                    }
                    if (this.totalBytesCount > 0 && this.state == 1) {
                        this.delegate.didChangedLoadProgress(this, Math.min(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, ((float) this.downloadedBytes) / ((float) this.totalBytesCount)));
                    }
                    for (int a = 0; a < this.delayedRequestInfos.size(); a++) {
                        RequestInfo delayedRequestInfo = (RequestInfo) this.delayedRequestInfos.get(a);
                        if (this.downloadedBytes == delayedRequestInfo.offset) {
                            this.delayedRequestInfos.remove(a);
                            processRequestResult(delayedRequestInfo, null);
                            delayedRequestInfo.response.disableFree = false;
                            delayedRequestInfo.response.freeResources();
                            break;
                        }
                    }
                    if (finishedDownloading) {
                        onFinishLoadingFile(true);
                    } else {
                        startDownloadRequest();
                    }
                }
            } catch (Throwable e) {
                onFail(false, 0);
                FileLog.e("tmessages", e);
            }
        } else if (error.text.contains("FILE_MIGRATE_")) {
            Integer val;
            Scanner scanner = new Scanner(error.text.replace("FILE_MIGRATE_", ""));
            scanner.useDelimiter("");
            try {
                val = Integer.valueOf(scanner.nextInt());
            } catch (Exception e2) {
                val = null;
            }
            if (val == null) {
                onFail(false, 0);
                return;
            }
            this.datacenter_id = val.intValue();
            this.nextDownloadOffset = 0;
            startDownloadRequest();
        } else if (error.text.contains("OFFSET_INVALID")) {
            if (this.downloadedBytes % this.currentDownloadChunkSize == 0) {
                try {
                    onFinishLoadingFile(true);
                    return;
                } catch (Throwable e3) {
                    FileLog.e("tmessages", e3);
                    onFail(false, 0);
                    return;
                }
            }
            onFail(false, 0);
        } else if (error.text.contains("RETRY_LIMIT")) {
            onFail(false, 2);
        } else {
            if (this.location != null) {
                FileLog.e("tmessages", "" + this.location + " id = " + this.location.id + " local_id = " + this.location.local_id + " access_hash = " + this.location.access_hash + " volume_id = " + this.location.volume_id + " secret = " + this.location.secret);
            }
            onFail(false, 0);
        }
    }

    private void onFail(boolean thread, final int reason) {
        cleanup();
        this.state = 2;
        if (thread) {
            Utilities.stageQueue.postRunnable(new Runnable() {
                public void run() {
                    FileLoadOperation.this.delegate.didFailedLoadingFile(FileLoadOperation.this, reason);
                }
            });
        } else {
            this.delegate.didFailedLoadingFile(this, reason);
        }
    }

    private void startDownloadRequest() {
        if (this.state != 1) {
            return;
        }
        if ((this.totalBytesCount <= 0 || this.nextDownloadOffset < this.totalBytesCount) && this.requestInfos.size() + this.delayedRequestInfos.size() < this.currentMaxDownloadRequests) {
            int count = 1;
            if (this.totalBytesCount > 0) {
                count = Math.max(0, this.currentMaxDownloadRequests - this.requestInfos.size());
            }
            int a = 0;
            while (a < count) {
                if (this.totalBytesCount <= 0 || this.nextDownloadOffset < this.totalBytesCount) {
                    boolean isLast;
                    int i;
                    int i2;
                    if (this.totalBytesCount <= 0 || a == count - 1 || (this.totalBytesCount > 0 && this.nextDownloadOffset + this.currentDownloadChunkSize >= this.totalBytesCount)) {
                        isLast = true;
                    } else {
                        isLast = false;
                    }
                    TL_upload_getFile req = new TL_upload_getFile();
                    req.location = this.location;
                    req.offset = this.nextDownloadOffset;
                    req.limit = this.currentDownloadChunkSize;
                    this.nextDownloadOffset += this.currentDownloadChunkSize;
                    final RequestInfo requestInfo = new RequestInfo();
                    this.requestInfos.add(requestInfo);
                    requestInfo.offset = req.offset;
                    ConnectionsManager instance = ConnectionsManager.getInstance();
                    RequestDelegate anonymousClass5 = new RequestDelegate() {
                        public void run(TLObject response, TL_error error) {
                            requestInfo.response = (TL_upload_file) response;
                            if (FileLoadOperation.this.currentType == ConnectionsManager.FileTypeAudio) {
                                StatsController.getInstance().incrementReceivedBytesCount(ConnectionsManager.getCurrentNetworkType(), 3, (long) (response.getObjectSize() + 4));
                            } else if (FileLoadOperation.this.currentType == ConnectionsManager.FileTypeVideo) {
                                StatsController.getInstance().incrementReceivedBytesCount(ConnectionsManager.getCurrentNetworkType(), 2, (long) (response.getObjectSize() + 4));
                            } else if (FileLoadOperation.this.currentType == 16777216) {
                                StatsController.getInstance().incrementReceivedBytesCount(ConnectionsManager.getCurrentNetworkType(), 4, (long) (response.getObjectSize() + 4));
                            } else if (FileLoadOperation.this.currentType == ConnectionsManager.FileTypeFile) {
                                StatsController.getInstance().incrementReceivedBytesCount(ConnectionsManager.getCurrentNetworkType(), 5, (long) (response.getObjectSize() + 4));
                            }
                            FileLoadOperation.this.processRequestResult(requestInfo, error);
                        }
                    };
                    if (this.isForceRequest) {
                        i = 32;
                    } else {
                        i = 0;
                    }
                    i |= 2;
                    int i3 = this.datacenter_id;
                    if (this.requestsCount % 2 == 0) {
                        i2 = 2;
                    } else {
                        i2 = ConnectionsManager.ConnectionTypeDownload2;
                    }
                    requestInfo.requestToken = instance.sendRequest(req, anonymousClass5, null, i, i3, i2, isLast);
                    this.requestsCount++;
                    a++;
                } else {
                    return;
                }
            }
        }
    }

    public void setDelegate(FileLoadOperationDelegate delegate) {
        this.delegate = delegate;
    }
}
