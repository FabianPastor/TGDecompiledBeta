package org.telegram.messenger.exoplayer.smoothstreaming;

import android.content.Context;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.telegram.messenger.exoplayer.chunk.VideoFormatSelectorUtil;
import org.telegram.messenger.exoplayer.smoothstreaming.SmoothStreamingManifest.TrackElement;
import org.telegram.messenger.exoplayer.smoothstreaming.SmoothStreamingTrackSelector.Output;
import org.telegram.messenger.exoplayer.util.Util;

public final class DefaultSmoothStreamingTrackSelector implements SmoothStreamingTrackSelector {
    private final Context context;
    private final boolean filterProtectedHdContent;
    private final boolean filterVideoRepresentations;
    private final int streamElementType;

    public static DefaultSmoothStreamingTrackSelector newVideoInstance(Context context, boolean filterVideoRepresentations, boolean filterProtectedHdContent) {
        return new DefaultSmoothStreamingTrackSelector(1, context, filterVideoRepresentations, filterProtectedHdContent);
    }

    public static DefaultSmoothStreamingTrackSelector newAudioInstance() {
        return new DefaultSmoothStreamingTrackSelector(0, null, false, false);
    }

    public static DefaultSmoothStreamingTrackSelector newTextInstance() {
        return new DefaultSmoothStreamingTrackSelector(2, null, false, false);
    }

    private DefaultSmoothStreamingTrackSelector(int streamElementType, Context context, boolean filterVideoRepresentations, boolean filterProtectedHdContent) {
        this.context = context;
        this.streamElementType = streamElementType;
        this.filterVideoRepresentations = filterVideoRepresentations;
        this.filterProtectedHdContent = filterProtectedHdContent;
    }

    public void selectTracks(SmoothStreamingManifest manifest, Output output) throws IOException {
        for (int i = 0; i < manifest.streamElements.length; i++) {
            TrackElement[] tracks = manifest.streamElements[i].tracks;
            if (manifest.streamElements[i].type == this.streamElementType) {
                int j;
                if (this.streamElementType == 1) {
                    int[] trackIndices;
                    if (this.filterVideoRepresentations) {
                        Context context = this.context;
                        List asList = Arrays.asList(tracks);
                        boolean z = this.filterProtectedHdContent && manifest.protectionElement != null;
                        trackIndices = VideoFormatSelectorUtil.selectVideoFormatsForDefaultDisplay(context, asList, null, z);
                    } else {
                        trackIndices = Util.firstIntegersArray(tracks.length);
                    }
                    if (trackCount > 1) {
                        output.adaptiveTrack(manifest, i, trackIndices);
                    }
                    for (int fixedTrack : trackIndices) {
                        output.fixedTrack(manifest, i, fixedTrack);
                    }
                } else {
                    for (j = 0; j < tracks.length; j++) {
                        output.fixedTrack(manifest, i, j);
                    }
                }
            }
        }
    }
}
