package org.telegram.messenger.exoplayer2.offline;

import android.net.Uri;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

public abstract class DownloadAction {
    public final byte[] data;
    public final boolean isRemoveAction;
    public final String type;
    public final Uri uri;
    public final int version;

    public static abstract class Deserializer {
        public final String type;
        public final int version;

        public abstract DownloadAction readFromStream(int i, DataInputStream dataInputStream) throws IOException;

        public Deserializer(String type, int version) {
            this.type = type;
            this.version = version;
        }
    }

    protected abstract Downloader createDownloader(DownloaderConstructorHelper downloaderConstructorHelper);

    protected abstract void writeToStream(DataOutputStream dataOutputStream) throws IOException;

    public static DownloadAction deserializeFromStream(Deserializer[] deserializers, InputStream input) throws IOException {
        DataInputStream dataInputStream = new DataInputStream(input);
        String type = dataInputStream.readUTF();
        int version = dataInputStream.readInt();
        for (Deserializer deserializer : deserializers) {
            if (type.equals(deserializer.type) && deserializer.version >= version) {
                return deserializer.readFromStream(version, dataInputStream);
            }
        }
        throw new DownloadException("No deserializer found for:" + type + ", " + version);
    }

    public static void serializeToStream(DownloadAction action, OutputStream output) throws IOException {
        DataOutputStream dataOutputStream = new DataOutputStream(output);
        dataOutputStream.writeUTF(action.type);
        dataOutputStream.writeInt(action.version);
        action.writeToStream(dataOutputStream);
        dataOutputStream.flush();
    }

    protected DownloadAction(String type, int version, Uri uri, boolean isRemoveAction, byte[] data) {
        this.type = type;
        this.version = version;
        this.uri = uri;
        this.isRemoveAction = isRemoveAction;
        if (data == null) {
            data = new byte[0];
        }
        this.data = data;
    }

    public final byte[] toByteArray() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            serializeToStream(this, output);
            return output.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException();
        }
    }

    public boolean isSameMedia(DownloadAction other) {
        return this.uri.equals(other.uri);
    }

    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DownloadAction that = (DownloadAction) o;
        if (this.type.equals(that.type) && this.version == that.version && this.uri.equals(that.uri) && this.isRemoveAction == that.isRemoveAction && Arrays.equals(this.data, that.data)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return (((this.uri.hashCode() * 31) + (this.isRemoveAction ? 1 : 0)) * 31) + Arrays.hashCode(this.data);
    }
}
