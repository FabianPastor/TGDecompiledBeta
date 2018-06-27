package org.telegram.messenger.exoplayer2.trackselection;

import android.util.Pair;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;
import org.telegram.messenger.exoplayer2.ExoPlaybackException;
import org.telegram.messenger.exoplayer2.RendererCapabilities;
import org.telegram.messenger.exoplayer2.RendererConfiguration;
import org.telegram.messenger.exoplayer2.source.TrackGroup;
import org.telegram.messenger.exoplayer2.source.TrackGroupArray;
import org.telegram.messenger.exoplayer2.util.Util;

public abstract class MappingTrackSelector extends TrackSelector {
    private MappedTrackInfo currentMappedTrackInfo;

    public static final class MappedTrackInfo {
        public static final int RENDERER_SUPPORT_EXCEEDS_CAPABILITIES_TRACKS = 2;
        public static final int RENDERER_SUPPORT_NO_TRACKS = 0;
        public static final int RENDERER_SUPPORT_PLAYABLE_TRACKS = 3;
        public static final int RENDERER_SUPPORT_UNSUPPORTED_TRACKS = 1;
        @Deprecated
        public final int length = this.rendererCount;
        private final int rendererCount;
        private final int[][][] rendererFormatSupports;
        private final int[] rendererMixedMimeTypeAdaptiveSupports;
        private final TrackGroupArray[] rendererTrackGroups;
        private final int[] rendererTrackTypes;
        private final TrackGroupArray unmappedTrackGroups;

        @Retention(RetentionPolicy.SOURCE)
        @interface RendererSupport {
        }

        MappedTrackInfo(int[] rendererTrackTypes, TrackGroupArray[] rendererTrackGroups, int[] rendererMixedMimeTypeAdaptiveSupports, int[][][] rendererFormatSupports, TrackGroupArray unmappedTrackGroups) {
            this.rendererTrackTypes = rendererTrackTypes;
            this.rendererTrackGroups = rendererTrackGroups;
            this.rendererFormatSupports = rendererFormatSupports;
            this.rendererMixedMimeTypeAdaptiveSupports = rendererMixedMimeTypeAdaptiveSupports;
            this.unmappedTrackGroups = unmappedTrackGroups;
            this.rendererCount = rendererTrackTypes.length;
        }

        public int getRendererCount() {
            return this.rendererCount;
        }

        public int getRendererType(int rendererIndex) {
            return this.rendererTrackTypes[rendererIndex];
        }

        public TrackGroupArray getTrackGroups(int rendererIndex) {
            return this.rendererTrackGroups[rendererIndex];
        }

        public int getRendererSupport(int rendererIndex) {
            int bestRendererSupport = 0;
            int[][] rendererFormatSupport = this.rendererFormatSupports[rendererIndex];
            for (int i = 0; i < rendererFormatSupport.length; i++) {
                for (int i2 : rendererFormatSupport[i]) {
                    int trackRendererSupport;
                    switch (i2 & 7) {
                        case 3:
                            trackRendererSupport = 2;
                            break;
                        case 4:
                            return 3;
                        default:
                            trackRendererSupport = 1;
                            break;
                    }
                    bestRendererSupport = Math.max(bestRendererSupport, trackRendererSupport);
                }
            }
            int i3 = bestRendererSupport;
            return bestRendererSupport;
        }

        @Deprecated
        public int getTrackTypeRendererSupport(int trackType) {
            return getTypeSupport(trackType);
        }

        public int getTypeSupport(int trackType) {
            int bestRendererSupport = 0;
            for (int i = 0; i < this.rendererCount; i++) {
                if (this.rendererTrackTypes[i] == trackType) {
                    bestRendererSupport = Math.max(bestRendererSupport, getRendererSupport(i));
                }
            }
            return bestRendererSupport;
        }

        @Deprecated
        public int getTrackFormatSupport(int rendererIndex, int groupIndex, int trackIndex) {
            return getTrackSupport(rendererIndex, groupIndex, trackIndex);
        }

        public int getTrackSupport(int rendererIndex, int groupIndex, int trackIndex) {
            return this.rendererFormatSupports[rendererIndex][groupIndex][trackIndex] & 7;
        }

        public int getAdaptiveSupport(int rendererIndex, int groupIndex, boolean includeCapabilitiesExceededTracks) {
            int trackCount = this.rendererTrackGroups[rendererIndex].get(groupIndex).length;
            int[] trackIndices = new int[trackCount];
            int i = 0;
            int trackIndexCount = 0;
            while (i < trackCount) {
                int trackIndexCount2;
                int fixedSupport = getTrackSupport(rendererIndex, groupIndex, i);
                if (fixedSupport == 4 || (includeCapabilitiesExceededTracks && fixedSupport == 3)) {
                    trackIndexCount2 = trackIndexCount + 1;
                    trackIndices[trackIndexCount] = i;
                } else {
                    trackIndexCount2 = trackIndexCount;
                }
                i++;
                trackIndexCount = trackIndexCount2;
            }
            return getAdaptiveSupport(rendererIndex, groupIndex, Arrays.copyOf(trackIndices, trackIndexCount));
        }

        public int getAdaptiveSupport(int rendererIndex, int groupIndex, int[] trackIndices) {
            int handledTrackCount = 0;
            int adaptiveSupport = 16;
            boolean multipleMimeTypes = false;
            String firstSampleMimeType = null;
            int i = 0;
            while (i < trackIndices.length) {
                String sampleMimeType = this.rendererTrackGroups[rendererIndex].get(groupIndex).getFormat(trackIndices[i]).sampleMimeType;
                int handledTrackCount2 = handledTrackCount + 1;
                if (handledTrackCount == 0) {
                    firstSampleMimeType = sampleMimeType;
                } else {
                    multipleMimeTypes |= !Util.areEqual(firstSampleMimeType, sampleMimeType) ? 1 : 0;
                }
                adaptiveSupport = Math.min(adaptiveSupport, this.rendererFormatSupports[rendererIndex][groupIndex][i] & 24);
                i++;
                handledTrackCount = handledTrackCount2;
            }
            if (multipleMimeTypes) {
                return Math.min(adaptiveSupport, this.rendererMixedMimeTypeAdaptiveSupports[rendererIndex]);
            }
            return adaptiveSupport;
        }

        @Deprecated
        public TrackGroupArray getUnassociatedTrackGroups() {
            return getUnmappedTrackGroups();
        }

        public TrackGroupArray getUnmappedTrackGroups() {
            return this.unmappedTrackGroups;
        }
    }

    protected abstract Pair<RendererConfiguration[], TrackSelection[]> selectTracks(MappedTrackInfo mappedTrackInfo, int[][][] iArr, int[] iArr2) throws ExoPlaybackException;

    public final MappedTrackInfo getCurrentMappedTrackInfo() {
        return this.currentMappedTrackInfo;
    }

    public final void onSelectionActivated(Object info) {
        this.currentMappedTrackInfo = (MappedTrackInfo) info;
    }

    public final TrackSelectorResult selectTracks(RendererCapabilities[] rendererCapabilities, TrackGroupArray trackGroups) throws ExoPlaybackException {
        int i;
        int[] rendererTrackGroupCounts = new int[(rendererCapabilities.length + 1)];
        TrackGroup[][] rendererTrackGroups = new TrackGroup[(rendererCapabilities.length + 1)][];
        int[][][] rendererFormatSupports = new int[(rendererCapabilities.length + 1)][][];
        for (i = 0; i < rendererTrackGroups.length; i++) {
            rendererTrackGroups[i] = new TrackGroup[trackGroups.length];
            rendererFormatSupports[i] = new int[trackGroups.length][];
        }
        int[] rendererMixedMimeTypeAdaptationSupports = getMixedMimeTypeAdaptationSupports(rendererCapabilities);
        for (int groupIndex = 0; groupIndex < trackGroups.length; groupIndex++) {
            int[] rendererFormatSupport;
            TrackGroup group = trackGroups.get(groupIndex);
            int rendererIndex = findRenderer(rendererCapabilities, group);
            if (rendererIndex == rendererCapabilities.length) {
                rendererFormatSupport = new int[group.length];
            } else {
                rendererFormatSupport = getFormatSupport(rendererCapabilities[rendererIndex], group);
            }
            int rendererTrackGroupCount = rendererTrackGroupCounts[rendererIndex];
            rendererTrackGroups[rendererIndex][rendererTrackGroupCount] = group;
            rendererFormatSupports[rendererIndex][rendererTrackGroupCount] = rendererFormatSupport;
            rendererTrackGroupCounts[rendererIndex] = rendererTrackGroupCounts[rendererIndex] + 1;
        }
        TrackGroupArray[] rendererTrackGroupArrays = new TrackGroupArray[rendererCapabilities.length];
        int[] rendererTrackTypes = new int[rendererCapabilities.length];
        for (i = 0; i < rendererCapabilities.length; i++) {
            rendererTrackGroupCount = rendererTrackGroupCounts[i];
            rendererTrackGroupArrays[i] = new TrackGroupArray((TrackGroup[]) Arrays.copyOf(rendererTrackGroups[i], rendererTrackGroupCount));
            rendererFormatSupports[i] = (int[][]) Arrays.copyOf(rendererFormatSupports[i], rendererTrackGroupCount);
            rendererTrackTypes[i] = rendererCapabilities[i].getTrackType();
        }
        MappedTrackInfo mappedTrackInfo = new MappedTrackInfo(rendererTrackTypes, rendererTrackGroupArrays, rendererMixedMimeTypeAdaptationSupports, rendererFormatSupports, new TrackGroupArray((TrackGroup[]) Arrays.copyOf(rendererTrackGroups[rendererCapabilities.length], rendererTrackGroupCounts[rendererCapabilities.length])));
        Pair<RendererConfiguration[], TrackSelection[]> result = selectTracks(mappedTrackInfo, rendererFormatSupports, rendererMixedMimeTypeAdaptationSupports);
        return new TrackSelectorResult((RendererConfiguration[]) result.first, (TrackSelection[]) result.second, mappedTrackInfo);
    }

    private static int findRenderer(RendererCapabilities[] rendererCapabilities, TrackGroup group) throws ExoPlaybackException {
        int bestRendererIndex = rendererCapabilities.length;
        int bestFormatSupportLevel = 0;
        for (int rendererIndex = 0; rendererIndex < rendererCapabilities.length; rendererIndex++) {
            RendererCapabilities rendererCapability = rendererCapabilities[rendererIndex];
            for (int trackIndex = 0; trackIndex < group.length; trackIndex++) {
                int formatSupportLevel = rendererCapability.supportsFormat(group.getFormat(trackIndex)) & 7;
                if (formatSupportLevel > bestFormatSupportLevel) {
                    bestRendererIndex = rendererIndex;
                    bestFormatSupportLevel = formatSupportLevel;
                    if (bestFormatSupportLevel == 4) {
                        return bestRendererIndex;
                    }
                }
            }
        }
        int i = bestRendererIndex;
        return bestRendererIndex;
    }

    private static int[] getFormatSupport(RendererCapabilities rendererCapabilities, TrackGroup group) throws ExoPlaybackException {
        int[] formatSupport = new int[group.length];
        for (int i = 0; i < group.length; i++) {
            formatSupport[i] = rendererCapabilities.supportsFormat(group.getFormat(i));
        }
        return formatSupport;
    }

    private static int[] getMixedMimeTypeAdaptationSupports(RendererCapabilities[] rendererCapabilities) throws ExoPlaybackException {
        int[] mixedMimeTypeAdaptationSupport = new int[rendererCapabilities.length];
        for (int i = 0; i < mixedMimeTypeAdaptationSupport.length; i++) {
            mixedMimeTypeAdaptationSupport[i] = rendererCapabilities[i].supportsMixedMimeTypeAdaptation();
        }
        return mixedMimeTypeAdaptationSupport;
    }
}
