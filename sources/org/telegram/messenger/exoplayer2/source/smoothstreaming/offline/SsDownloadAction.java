package org.telegram.messenger.exoplayer2.source.smoothstreaming.offline;

import android.net.Uri;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;
import org.telegram.messenger.exoplayer2.offline.DownloadAction;
import org.telegram.messenger.exoplayer2.offline.DownloadAction.Deserializer;
import org.telegram.messenger.exoplayer2.offline.DownloaderConstructorHelper;
import org.telegram.messenger.exoplayer2.offline.SegmentDownloadAction;
import org.telegram.messenger.exoplayer2.source.smoothstreaming.manifest.StreamKey;

public final class SsDownloadAction extends SegmentDownloadAction<StreamKey> {
    public static final Deserializer DESERIALIZER = new SegmentDownloadActionDeserializer<StreamKey>(TYPE, 0) {
        protected StreamKey readKey(DataInputStream input) throws IOException {
            return new StreamKey(input.readInt(), input.readInt());
        }

        protected DownloadAction createDownloadAction(Uri uri, boolean isRemoveAction, byte[] data, List<StreamKey> keys) {
            return new SsDownloadAction(uri, isRemoveAction, data, keys);
        }
    };
    private static final String TYPE = "ss";
    private static final int VERSION = 0;

    public SsDownloadAction(Uri uri, boolean isRemoveAction, byte[] data, List<StreamKey> keys) {
        super(TYPE, 0, uri, isRemoveAction, data, keys);
    }

    protected SsDownloader createDownloader(DownloaderConstructorHelper constructorHelper) {
        return new SsDownloader(this.uri, this.keys, constructorHelper);
    }

    protected void writeKey(DataOutputStream output, StreamKey key) throws IOException {
        output.writeInt(key.streamElementIndex);
        output.writeInt(key.trackIndex);
    }
}
