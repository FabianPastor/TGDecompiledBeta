package org.telegram.messenger.exoplayer2.offline;

import android.net.Uri;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.telegram.messenger.exoplayer2.offline.DownloadAction.Deserializer;
import org.telegram.messenger.exoplayer2.util.Assertions;

public abstract class SegmentDownloadAction<K extends Comparable<K>> extends DownloadAction {
    public final List<K> keys;

    protected static abstract class SegmentDownloadActionDeserializer<K> extends Deserializer {
        protected abstract DownloadAction createDownloadAction(Uri uri, boolean z, byte[] bArr, List<K> list);

        protected abstract K readKey(DataInputStream dataInputStream) throws IOException;

        public SegmentDownloadActionDeserializer(String type, int version) {
            super(type, version);
        }

        public final DownloadAction readFromStream(int version, DataInputStream input) throws IOException {
            Uri uri = Uri.parse(input.readUTF());
            boolean isRemoveAction = input.readBoolean();
            byte[] data = new byte[input.readInt()];
            input.readFully(data);
            int keyCount = input.readInt();
            List<K> keys = new ArrayList();
            for (int i = 0; i < keyCount; i++) {
                keys.add(readKey(input));
            }
            return createDownloadAction(uri, isRemoveAction, data, keys);
        }
    }

    protected abstract void writeKey(DataOutputStream dataOutputStream, K k) throws IOException;

    protected SegmentDownloadAction(String type, int version, Uri uri, boolean isRemoveAction, byte[] data, List<K> keys) {
        super(type, version, uri, isRemoveAction, data);
        if (isRemoveAction) {
            Assertions.checkArgument(keys.isEmpty());
            this.keys = Collections.emptyList();
            return;
        }
        ArrayList<K> mutableKeys = new ArrayList(keys);
        Collections.sort(mutableKeys);
        this.keys = Collections.unmodifiableList(mutableKeys);
    }

    public final void writeToStream(DataOutputStream output) throws IOException {
        output.writeUTF(this.uri.toString());
        output.writeBoolean(this.isRemoveAction);
        output.writeInt(this.data.length);
        output.write(this.data);
        output.writeInt(this.keys.size());
        for (int i = 0; i < this.keys.size(); i++) {
            writeKey(output, (Comparable) this.keys.get(i));
        }
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!super.equals(o)) {
            return false;
        }
        return this.keys.equals(((SegmentDownloadAction) o).keys);
    }

    public int hashCode() {
        return (super.hashCode() * 31) + this.keys.hashCode();
    }
}
