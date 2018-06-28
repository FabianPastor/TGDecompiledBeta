package org.telegram.messenger.exoplayer2.offline;

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import org.telegram.messenger.exoplayer2.offline.DownloadAction.Deserializer;
import org.telegram.messenger.exoplayer2.util.AtomicFile;
import org.telegram.messenger.exoplayer2.util.Util;

public final class ActionFile {
    static final int VERSION = 0;
    private final File actionFile;
    private final AtomicFile atomicFile;

    public ActionFile(File actionFile) {
        this.actionFile = actionFile;
        this.atomicFile = new AtomicFile(actionFile);
    }

    public DownloadAction[] load(Deserializer... deserializers) throws IOException {
        if (!this.actionFile.exists()) {
            return new DownloadAction[0];
        }
        Closeable inputStream = null;
        try {
            inputStream = this.atomicFile.openRead();
            DataInputStream dataInputStream = new DataInputStream(inputStream);
            int version = dataInputStream.readInt();
            if (version > 0) {
                throw new IOException("Unsupported action file version: " + version);
            }
            int actionCount = dataInputStream.readInt();
            DownloadAction[] actions = new DownloadAction[actionCount];
            for (int i = 0; i < actionCount; i++) {
                actions[i] = DownloadAction.deserializeFromStream(deserializers, dataInputStream);
            }
            return actions;
        } finally {
            Util.closeQuietly(inputStream);
        }
    }

    public void store(DownloadAction... downloadActions) throws IOException {
        Throwable th;
        Closeable output = null;
        try {
            DataOutputStream output2 = new DataOutputStream(this.atomicFile.startWrite());
            try {
                output2.writeInt(0);
                output2.writeInt(downloadActions.length);
                for (DownloadAction action : downloadActions) {
                    DownloadAction.serializeToStream(action, output2);
                }
                this.atomicFile.endWrite(output2);
                Util.closeQuietly((Closeable) null);
            } catch (Throwable th2) {
                th = th2;
                Object output3 = output2;
                Util.closeQuietly(output);
                throw th;
            }
        } catch (Throwable th3) {
            th = th3;
            Util.closeQuietly(output);
            throw th;
        }
    }
}
