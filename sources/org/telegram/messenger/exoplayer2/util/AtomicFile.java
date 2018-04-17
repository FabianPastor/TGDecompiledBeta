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
                } catch (IOException e) {
                    Log.w(AtomicFile.TAG, "Failed to sync file descriptor:", e);
                }
                this.fileOutputStream.close();
            }
        }

        public void flush() throws IOException {
            this.fileOutputStream.flush();
        }

        public void write(int b) throws IOException {
            this.fileOutputStream.write(b);
        }

        public void write(byte[] b) throws IOException {
            this.fileOutputStream.write(b);
        }

        public void write(byte[] b, int off, int len) throws IOException {
            this.fileOutputStream.write(b, off, len);
        }
    }

    public AtomicFile(File baseName) {
        this.baseName = baseName;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(baseName.getPath());
        stringBuilder.append(".bak");
        this.backupName = new File(stringBuilder.toString());
    }

    public void delete() {
        this.baseName.delete();
        this.backupName.delete();
    }

    public OutputStream startWrite() throws IOException {
        if (this.baseName.exists()) {
            if (this.backupName.exists()) {
                this.baseName.delete();
            } else if (!this.baseName.renameTo(this.backupName)) {
                String str = TAG;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Couldn't rename file ");
                stringBuilder.append(this.baseName);
                stringBuilder.append(" to backup file ");
                stringBuilder.append(this.backupName);
                Log.w(str, stringBuilder.toString());
            }
        }
        try {
            return new AtomicFileOutputStream(this.baseName);
        } catch (FileNotFoundException e) {
            if (this.baseName.getParentFile().mkdirs()) {
                try {
                    return new AtomicFileOutputStream(this.baseName);
                } catch (FileNotFoundException e2) {
                    StringBuilder stringBuilder2 = new StringBuilder();
                    stringBuilder2.append("Couldn't create ");
                    stringBuilder2.append(this.baseName);
                    throw new IOException(stringBuilder2.toString(), e2);
                }
            }
            StringBuilder stringBuilder3 = new StringBuilder();
            stringBuilder3.append("Couldn't create directory ");
            stringBuilder3.append(this.baseName);
            throw new IOException(stringBuilder3.toString(), e);
        }
    }

    public void endWrite(OutputStream str) throws IOException {
        str.close();
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
