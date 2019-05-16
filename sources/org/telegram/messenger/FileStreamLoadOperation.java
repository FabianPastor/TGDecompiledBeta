package org.telegram.messenger;

import android.net.Uri;
import com.google.android.exoplayer2.upstream.BaseDataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.TransferListener;
import java.io.EOFException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.concurrent.CountDownLatch;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.TL_document;
import org.telegram.tgnet.TLRPC.TL_documentAttributeAudio;
import org.telegram.tgnet.TLRPC.TL_documentAttributeFilename;
import org.telegram.tgnet.TLRPC.TL_documentAttributeVideo;

public class FileStreamLoadOperation extends BaseDataSource implements FileLoadOperationStream {
    private long bytesRemaining;
    private CountDownLatch countDownLatch;
    private int currentAccount;
    private int currentOffset;
    private Document document;
    private RandomAccessFile file;
    private FileLoadOperation loadOperation;
    private boolean opened;
    private Object parentObject;
    private Uri uri;

    /*  JADX ERROR: JadxRuntimeException in pass: BlockProcessor
        jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:20:0x006c in {2, 6, 9, 14, 16, 19} preds:[]
        	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:242)
        	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:52)
        	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:42)
        	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:27)
        	at jadx.core.dex.visitors.DepthTraversal.lambda$visit$1(DepthTraversal.java:14)
        	at java.util.ArrayList.forEach(ArrayList.java:1257)
        	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
        	at jadx.core.ProcessClass.process(ProcessClass.java:32)
        	at jadx.core.ProcessClass.lambda$processDependencies$0(ProcessClass.java:51)
        	at java.lang.Iterable.forEach(Iterable.java:75)
        	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:51)
        	at jadx.core.ProcessClass.process(ProcessClass.java:37)
        	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:292)
        	at jadx.api.JavaClass.decompile(JavaClass.java:62)
        	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
        */
    public int read(byte[] r7, int r8, int r9) throws java.io.IOException {
        /*
        r6 = this;
        r0 = 0;
        if (r9 != 0) goto L_0x0004;
        return r0;
        r1 = r6.bytesRemaining;
        r3 = 0;
        r5 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1));
        if (r5 != 0) goto L_0x000e;
        r7 = -1;
        return r7;
        r3 = (long) r9;
        r5 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1));
        if (r5 >= 0) goto L_0x0014;
        r9 = (int) r1;
        if (r0 != 0) goto L_0x0051;
        r0 = r6.loadOperation;	 Catch:{ Exception -> 0x0065 }
        r1 = r6.currentOffset;	 Catch:{ Exception -> 0x0065 }
        r0 = r0.getDownloadedLengthFromOffset(r1, r9);	 Catch:{ Exception -> 0x0065 }
        if (r0 != 0) goto L_0x0014;	 Catch:{ Exception -> 0x0065 }
        r1 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0065 }
        r1.<init>();	 Catch:{ Exception -> 0x0065 }
        r2 = "not found bytes ";	 Catch:{ Exception -> 0x0065 }
        r1.append(r2);	 Catch:{ Exception -> 0x0065 }
        r1.append(r8);	 Catch:{ Exception -> 0x0065 }
        r1 = r1.toString();	 Catch:{ Exception -> 0x0065 }
        org.telegram.messenger.FileLog.d(r1);	 Catch:{ Exception -> 0x0065 }
        r1 = r6.currentAccount;	 Catch:{ Exception -> 0x0065 }
        r1 = org.telegram.messenger.FileLoader.getInstance(r1);	 Catch:{ Exception -> 0x0065 }
        r2 = r6.document;	 Catch:{ Exception -> 0x0065 }
        r3 = r6.parentObject;	 Catch:{ Exception -> 0x0065 }
        r4 = r6.currentOffset;	 Catch:{ Exception -> 0x0065 }
        r1.loadStreamFile(r6, r2, r3, r4);	 Catch:{ Exception -> 0x0065 }
        r1 = new java.util.concurrent.CountDownLatch;	 Catch:{ Exception -> 0x0065 }
        r2 = 1;	 Catch:{ Exception -> 0x0065 }
        r1.<init>(r2);	 Catch:{ Exception -> 0x0065 }
        r6.countDownLatch = r1;	 Catch:{ Exception -> 0x0065 }
        r1 = r6.countDownLatch;	 Catch:{ Exception -> 0x0065 }
        r1.await();	 Catch:{ Exception -> 0x0065 }
        goto L_0x0014;	 Catch:{ Exception -> 0x0065 }
        r9 = r6.file;	 Catch:{ Exception -> 0x0065 }
        r9.readFully(r7, r8, r0);	 Catch:{ Exception -> 0x0065 }
        r7 = r6.currentOffset;	 Catch:{ Exception -> 0x0065 }
        r7 = r7 + r0;	 Catch:{ Exception -> 0x0065 }
        r6.currentOffset = r7;	 Catch:{ Exception -> 0x0065 }
        r7 = r6.bytesRemaining;	 Catch:{ Exception -> 0x0065 }
        r1 = (long) r0;	 Catch:{ Exception -> 0x0065 }
        r7 = r7 - r1;	 Catch:{ Exception -> 0x0065 }
        r6.bytesRemaining = r7;	 Catch:{ Exception -> 0x0065 }
        r6.bytesTransferred(r0);	 Catch:{ Exception -> 0x0065 }
        return r0;
        r7 = move-exception;
        r8 = new java.io.IOException;
        r8.<init>(r7);
        throw r8;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.FileStreamLoadOperation.read(byte[], int, int):int");
    }

    public FileStreamLoadOperation() {
        super(false);
    }

    @Deprecated
    public FileStreamLoadOperation(TransferListener transferListener) {
        this();
        if (transferListener != null) {
            addTransferListener(transferListener);
        }
    }

    public long open(DataSpec dataSpec) throws IOException {
        this.uri = dataSpec.uri;
        this.currentAccount = Utilities.parseInt(this.uri.getQueryParameter("account")).intValue();
        this.parentObject = FileLoader.getInstance(this.currentAccount).getParentObject(Utilities.parseInt(this.uri.getQueryParameter("rid")).intValue());
        this.document = new TL_document();
        this.document.access_hash = Utilities.parseLong(this.uri.getQueryParameter("hash")).longValue();
        this.document.id = Utilities.parseLong(this.uri.getQueryParameter("id")).longValue();
        this.document.size = Utilities.parseInt(this.uri.getQueryParameter("size")).intValue();
        this.document.dc_id = Utilities.parseInt(this.uri.getQueryParameter("dc")).intValue();
        this.document.mime_type = this.uri.getQueryParameter("mime");
        this.document.file_reference = Utilities.hexToBytes(this.uri.getQueryParameter("reference"));
        TL_documentAttributeFilename tL_documentAttributeFilename = new TL_documentAttributeFilename();
        tL_documentAttributeFilename.file_name = this.uri.getQueryParameter("name");
        this.document.attributes.add(tL_documentAttributeFilename);
        if (this.document.mime_type.startsWith("video")) {
            this.document.attributes.add(new TL_documentAttributeVideo());
        } else if (this.document.mime_type.startsWith("audio")) {
            this.document.attributes.add(new TL_documentAttributeAudio());
        }
        FileLoader instance = FileLoader.getInstance(this.currentAccount);
        Document document = this.document;
        Object obj = this.parentObject;
        int i = (int) dataSpec.position;
        this.currentOffset = i;
        this.loadOperation = instance.loadStreamFile(this, document, obj, i);
        long j = dataSpec.length;
        if (j == -1) {
            j = ((long) this.document.size) - dataSpec.position;
        }
        this.bytesRemaining = j;
        if (this.bytesRemaining >= 0) {
            this.opened = true;
            transferStarted(dataSpec);
            FileLoadOperation fileLoadOperation = this.loadOperation;
            if (fileLoadOperation != null) {
                this.file = new RandomAccessFile(fileLoadOperation.getCurrentFile(), "r");
                this.file.seek((long) this.currentOffset);
            }
            return this.bytesRemaining;
        }
        throw new EOFException();
    }

    public Uri getUri() {
        return this.uri;
    }

    public void close() {
        FileLoadOperation fileLoadOperation = this.loadOperation;
        if (fileLoadOperation != null) {
            fileLoadOperation.removeStreamListener(this);
        }
        CountDownLatch countDownLatch = this.countDownLatch;
        if (countDownLatch != null) {
            countDownLatch.countDown();
        }
        RandomAccessFile randomAccessFile = this.file;
        if (randomAccessFile != null) {
            try {
                randomAccessFile.close();
            } catch (Exception e) {
                FileLog.e(e);
            }
            this.file = null;
        }
        this.uri = null;
        if (this.opened) {
            this.opened = false;
            transferEnded();
        }
    }

    public void newDataAvailable() {
        CountDownLatch countDownLatch = this.countDownLatch;
        if (countDownLatch != null) {
            countDownLatch.countDown();
        }
    }
}
