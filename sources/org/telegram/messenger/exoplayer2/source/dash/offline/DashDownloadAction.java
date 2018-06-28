package org.telegram.messenger.exoplayer2.source.dash.offline;

import android.net.Uri;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;
import org.telegram.messenger.exoplayer2.offline.DownloadAction;
import org.telegram.messenger.exoplayer2.offline.DownloadAction.Deserializer;
import org.telegram.messenger.exoplayer2.offline.DownloaderConstructorHelper;
import org.telegram.messenger.exoplayer2.offline.SegmentDownloadAction;
import org.telegram.messenger.exoplayer2.source.dash.manifest.RepresentationKey;

public final class DashDownloadAction extends SegmentDownloadAction<RepresentationKey> {
    public static final Deserializer DESERIALIZER = new SegmentDownloadActionDeserializer<RepresentationKey>(TYPE, 0) {
        protected RepresentationKey readKey(DataInputStream input) throws IOException {
            return new RepresentationKey(input.readInt(), input.readInt(), input.readInt());
        }

        protected DownloadAction createDownloadAction(Uri uri, boolean isRemoveAction, byte[] data, List<RepresentationKey> keys) {
            return new DashDownloadAction(uri, isRemoveAction, data, keys);
        }
    };
    private static final String TYPE = "dash";
    private static final int VERSION = 0;

    public DashDownloadAction(Uri uri, boolean isRemoveAction, byte[] data, List<RepresentationKey> keys) {
        super(TYPE, 0, uri, isRemoveAction, data, keys);
    }

    protected DashDownloader createDownloader(DownloaderConstructorHelper constructorHelper) {
        return new DashDownloader(this.uri, this.keys, constructorHelper);
    }

    protected void writeKey(DataOutputStream output, RepresentationKey key) throws IOException {
        output.writeInt(key.periodIndex);
        output.writeInt(key.adaptationSetIndex);
        output.writeInt(key.representationIndex);
    }
}
