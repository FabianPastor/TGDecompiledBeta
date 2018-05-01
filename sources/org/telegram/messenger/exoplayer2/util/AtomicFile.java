package org.telegram.messenger.exoplayer2.util;

import android.util.Log;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class AtomicFile {
    private static final String TAG = "AtomicFile";
    private final File backupName;
    private final File baseName;

    private static final class AtomicFileOutputStream extends OutputStream {
        private boolean closed = false;
        private final FileOutputStream fileOutputStream;

        public AtomicFileOutputStream(File file) throws FileNotFoundException {
            this.fileOutputStream = new FileOutputStream(file);
        }

        public void close() throws IOException {
            if (!this.closed) {
                this.closed = true;
                flush();
                try {
                    this.fileOutputStream.getFD().sync();
                } catch (Throwable e) {
                    Log.w(AtomicFile.TAG, "Failed to sync file descriptor:", e);
                }
                this.fileOutputStream.close();
            }
        }

        public void flush() throws IOException {
            this.fileOutputStream.flush();
        }

        public void write(int i) throws IOException {
            this.fileOutputStream.write(i);
        }

        public void write(byte[] bArr) throws IOException {
            this.fileOutputStream.write(bArr);
        }

        public void write(byte[] bArr, int i, int i2) throws IOException {
            this.fileOutputStream.write(bArr, i, i2);
        }
    }

    public AtomicFile(File file) {
        this.baseName = file;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(file.getPath());
        stringBuilder.append(".bak");
        this.backupName = new File(stringBuilder.toString());
    }

    public void delete() {
        this.baseName.delete();
        this.backupName.delete();
    }

    public OutputStream startWrite() throws IOException {
        StringBuilder stringBuilder;
        if (this.baseName.exists()) {
            if (this.backupName.exists()) {
                this.baseName.delete();
            } else if (!this.baseName.renameTo(this.backupName)) {
                String str = TAG;
                StringBuilder stringBuilder2 = new StringBuilder();
                stringBuilder2.append("Couldn't rename file ");
                stringBuilder2.append(this.baseName);
                stringBuilder2.append(" to backup file ");
                stringBuilder2.append(this.backupName);
                Log.w(str, stringBuilder2.toString());
            }
        }
        try {
            return new AtomicFileOutputStream(this.baseName);
        } catch (Throwable e) {
            if (this.baseName.getParentFile().mkdirs()) {
                try {
                    return new AtomicFileOutputStream(this.baseName);
                } catch (Throwable e2) {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("Couldn't create ");
                    stringBuilder.append(this.baseName);
                    throw new IOException(stringBuilder.toString(), e2);
                }
            }
            stringBuilder = new StringBuilder();
            stringBuilder.append("Couldn't create directory ");
            stringBuilder.append(this.baseName);
            throw new IOException(stringBuilder.toString(), e2);
        }
    }

    public void endWrite(OutputStream outputStream) throws IOException {
        outputStream.close();
        this.backupName.delete();
    }

    public InputStream openRead() throws FileNotFoundException {
        restoreBackup();
        return new FileInputStream(this.baseName);
    }

    private void restoreBackup() {
        if (this.backupName.exists()) {
            this.baseName.delete();
            this.backupName.renameTo(this.baseName);
        }
    }
}
