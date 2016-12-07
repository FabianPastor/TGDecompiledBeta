package org.telegram.messenger.exoplayer.dash;

import android.content.Context;
import java.io.IOException;
import java.util.List;
import org.telegram.messenger.exoplayer.chunk.VideoFormatSelectorUtil;
import org.telegram.messenger.exoplayer.dash.DashTrackSelector.Output;
import org.telegram.messenger.exoplayer.dash.mpd.AdaptationSet;
import org.telegram.messenger.exoplayer.dash.mpd.MediaPresentationDescription;
import org.telegram.messenger.exoplayer.dash.mpd.Period;
import org.telegram.messenger.exoplayer.util.Util;

public final class DefaultDashTrackSelector implements DashTrackSelector {
    private final int adaptationSetType;
    private final Context context;
    private final boolean filterProtectedHdContent;
    private final boolean filterVideoRepresentations;

    public static DefaultDashTrackSelector newVideoInstance(Context context, boolean filterVideoRepresentations, boolean filterProtectedHdContent) {
        return new DefaultDashTrackSelector(0, context, filterVideoRepresentations, filterProtectedHdContent);
    }

    public static DefaultDashTrackSelector newAudioInstance() {
        return new DefaultDashTrackSelector(1, null, false, false);
    }

    public static DefaultDashTrackSelector newTextInstance() {
        return new DefaultDashTrackSelector(2, null, false, false);
    }

    private DefaultDashTrackSelector(int adaptationSetType, Context context, boolean filterVideoRepresentations, boolean filterProtectedHdContent) {
        this.adaptationSetType = adaptationSetType;
        this.context = context;
        this.filterVideoRepresentations = filterVideoRepresentations;
        this.filterProtectedHdContent = filterProtectedHdContent;
    }

    public void selectTracks(MediaPresentationDescription manifest, int periodIndex, Output output) throws IOException {
        Period period = manifest.getPeriod(periodIndex);
        for (int i = 0; i < period.adaptationSets.size(); i++) {
            AdaptationSet adaptationSet = (AdaptationSet) period.adaptationSets.get(i);
            if (adaptationSet.type == this.adaptationSetType) {
                int j;
                if (this.adaptationSetType == 0) {
                    int[] representations;
                    if (this.filterVideoRepresentations) {
                        Context context = this.context;
                        List list = adaptationSet.representations;
                        boolean z = this.filterProtectedHdContent && adaptationSet.hasContentProtection();
                        representations = VideoFormatSelectorUtil.selectVideoFormatsForDefaultDisplay(context, list, null, z);
                    } else {
                        representations = Util.firstIntegersArray(adaptationSet.representations.size());
                    }
                    if (representationCount > 1) {
                        output.adaptiveTrack(manifest, periodIndex, i, representations);
                    }
                    for (int fixedTrack : representations) {
                        output.fixedTrack(manifest, periodIndex, i, fixedTrack);
                    }
                } else {
                    for (j = 0; j < adaptationSet.representations.size(); j++) {
                        output.fixedTrack(manifest, periodIndex, i, j);
                    }
                }
            }
        }
    }
}
