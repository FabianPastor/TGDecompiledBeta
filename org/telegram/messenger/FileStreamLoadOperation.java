package org.telegram.messenger;

import android.net.Uri;
import java.io.EOFException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.concurrent.CountDownLatch;
import org.telegram.messenger.exoplayer2.upstream.DataSource;
import org.telegram.messenger.exoplayer2.upstream.DataSpec;
import org.telegram.messenger.exoplayer2.upstream.TransferListener;
import org.telegram.messenger.exoplayer2.util.MimeTypes;
import org.telegram.tgnet.TLRPC.TL_document;
import org.telegram.tgnet.TLRPC.TL_documentAttributeAudio;
import org.telegram.tgnet.TLRPC.TL_documentAttributeFilename;
import org.telegram.tgnet.TLRPC.TL_documentAttributeVideo;

public class FileStreamLoadOperation implements DataSource {
    private long bytesRemaining;
    private CountDownLatch countDownLatch;
    private int currentOffset;
    private RandomAccessFile file;
    private final TransferListener<? super FileStreamLoadOperation> listener;
    private FileLoadOperation loadOperation;
    private boolean opened;
    private Uri uri;

    public FileStreamLoadOperation() {
        this(null);
    }

    public FileStreamLoadOperation(TransferListener<? super FileStreamLoadOperation> listener) {
        this.listener = listener;
    }

    public long open(DataSpec dataSpec) throws IOException {
        this.uri = dataSpec.uri;
        int currentAccount = Utilities.parseInt(this.uri.getQueryParameter("account")).intValue();
        TL_document document = new TL_document();
        document.access_hash = Utilities.parseLong(this.uri.getQueryParameter("hash")).longValue();
        document.id = Utilities.parseLong(this.uri.getQueryParameter(TtmlNode.ATTR_ID)).longValue();
        document.size = Utilities.parseInt(this.uri.getQueryParameter("size")).intValue();
        document.dc_id = Utilities.parseInt(this.uri.getQueryParameter("dc")).intValue();
        document.mime_type = this.uri.getQueryParameter("mime");
        TL_documentAttributeFilename filename = new TL_documentAttributeFilename();
        filename.file_name = this.uri.getQueryParameter("name");
        document.attributes.add(filename);
        if (document.mime_type.startsWith(MimeTypes.BASE_TYPE_VIDEO)) {
            document.attributes.add(new TL_documentAttributeVideo());
        } else if (document.mime_type.startsWith(MimeTypes.BASE_TYPE_AUDIO)) {
            document.attributes.add(new TL_documentAttributeAudio());
        }
        FileLoader instance = FileLoader.getInstance(currentAccount);
        int i = (int) dataSpec.position;
        this.currentOffset = i;
        this.loadOperation = instance.loadStreamFile(this, document, i);
        this.bytesRemaining = dataSpec.length == -1 ? ((long) document.size) - dataSpec.position : dataSpec.length;
        if (this.bytesRemaining < 0) {
            throw new EOFException();
        }
        this.opened = true;
        if (this.listener != null) {
            this.listener.onTransferStart(this, dataSpec);
        }
        this.file = new RandomAccessFile(this.loadOperation.getCurrentFile(), "r");
        this.file.seek((long) this.currentOffset);
        return this.bytesRemaining;
    }

    public int read(byte[] buffer, int offset, int readLength) throws IOException {
        if (readLength == 0) {
            return 0;
        }
        if (this.bytesRemaining == 0) {
            return -1;
        }
        int availableLength = 0;
        try {
            if (this.bytesRemaining < ((long) readLength)) {
                readLength = (int) this.bytesRemaining;
            }
            while (availableLength == 0) {
                availableLength = this.loadOperation.getDownloadedLengthFromOffset(this.currentOffset, readLength);
                if (availableLength == 0) {
                    this.countDownLatch = new CountDownLatch(1);
                    this.countDownLatch.await();
                }
            }
            this.file.readFully(buffer, offset, availableLength);
            this.currentOffset += availableLength;
            this.bytesRemaining -= (long) availableLength;
            if (this.listener == null) {
                return availableLength;
            }
            this.listener.onBytesTransferred(this, availableLength);
            return availableLength;
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    public Uri getUri() {
        return this.uri;
    }

    public void close() {
        if (this.loadOperation != null) {
            this.loadOperation.removeStreamListener(this);
        }
        if (this.countDownLatch != null) {
            this.countDownLatch.countDown();
        }
        if (this.file != null) {
            try {
                this.file.close();
            } catch (Throwable e) {
                FileLog.e(e);
            }
            this.file = null;
        }
        this.uri = null;
        if (this.opened) {
            this.opened = false;
            if (this.listener != null) {
                this.listener.onTransferEnd(this);
            }
        }
    }

    protected void newDataAvailable() {
        if (this.countDownLatch != null) {
            this.countDownLatch.countDown();
        }
    }
}
