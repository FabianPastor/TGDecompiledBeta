package org.telegram.messenger.exoplayer2.source.hls.offline;

import android.net.Uri;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;
import org.telegram.messenger.exoplayer2.offline.DownloadAction;
import org.telegram.messenger.exoplayer2.offline.DownloadAction.Deserializer;
import org.telegram.messenger.exoplayer2.offline.DownloaderConstructorHelper;
import org.telegram.messenger.exoplayer2.offline.SegmentDownloadAction;
import org.telegram.messenger.exoplayer2.source.hls.playlist.RenditionKey;

public final class HlsDownloadAction extends SegmentDownloadAction<RenditionKey> {
    public static final Deserializer DESERIALIZER = new SegmentDownloadActionDeserializer<RenditionKey>(TYPE, 0) {
        protected RenditionKey readKey(DataInputStream input) throws IOException {
            return new RenditionKey(input.readInt(), input.readInt());
        }

        protected DownloadAction createDownloadAction(Uri uri, boolean isRemoveAction, byte[] data, List<RenditionKey> keys) {
            return new HlsDownloadAction(uri, isRemoveAction, data, keys);
        }
    };
    private static final String TYPE = "hls";
    private static final int VERSION = 0;

    public HlsDownloadAction(Uri uri, boolean isRemoveAction, byte[] data, List<RenditionKey> keys) {
        super(TYPE, 0, uri, isRemoveAction, data, keys);
    }

    protected HlsDownloader createDownloader(DownloaderConstructorHelper constructorHelper) {
        return new HlsDownloader(this.uri, this.keys, constructorHelper);
    }

    protected void writeKey(DataOutputStream output, RenditionKey key) throws IOException {
        output.writeInt(key.type);
        output.writeInt(key.trackIndex);
    }
}
