package org.telegram.messenger.exoplayer2.trackselection;

import android.util.SparseArray;
import android.util.SparseBooleanArray;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.telegram.messenger.exoplayer2.ExoPlaybackException;
import org.telegram.messenger.exoplayer2.RendererCapabilities;
import org.telegram.messenger.exoplayer2.RendererConfiguration;
import org.telegram.messenger.exoplayer2.source.TrackGroup;
import org.telegram.messenger.exoplayer2.source.TrackGroupArray;
import org.telegram.messenger.exoplayer2.trackselection.TrackSelection.Factory;
import org.telegram.messenger.exoplayer2.util.Util;

public abstract class MappingTrackSelector extends TrackSelector {
    private MappedTrackInfo currentMappedTrackInfo;
    private final SparseBooleanArray rendererDisabledFlags = new SparseBooleanArray();
    private final SparseArray<Map<TrackGroupArray, SelectionOverride>> selectionOverrides = new SparseArray();
    private int tunnelingAudioSessionId = 0;

    public static final class MappedTrackInfo {
        public static final int RENDERER_SUPPORT_EXCEEDS_CAPABILITIES_TRACKS = 2;
        public static final int RENDERER_SUPPORT_NO_TRACKS = 0;
        public static final int RENDERER_SUPPORT_PLAYABLE_TRACKS = 3;
        public static final int RENDERER_SUPPORT_UNSUPPORTED_TRACKS = 1;
        private final int[][][] formatSupport;
        public final int length;
        private final int[] mixedMimeTypeAdaptiveSupport;
        private final int[] rendererTrackTypes;
        private final TrackGroupArray[] trackGroups;
        private final TrackGroupArray unassociatedTrackGroups;

        MappedTrackInfo(int[] rendererTrackTypes, TrackGroupArray[] trackGroups, int[] mixedMimeTypeAdaptiveSupport, int[][][] formatSupport, TrackGroupArray unassociatedTrackGroups) {
            this.rendererTrackTypes = rendererTrackTypes;
            this.trackGroups = trackGroups;
            this.formatSupport = formatSupport;
            this.mixedMimeTypeAdaptiveSupport = mixedMimeTypeAdaptiveSupport;
            this.unassociatedTrackGroups = unassociatedTrackGroups;
            this.length = trackGroups.length;
        }

        public TrackGroupArray getTrackGroups(int rendererIndex) {
            return this.trackGroups[rendererIndex];
        }

        public int getRendererSupport(int rendererIndex) {
            int[][] rendererFormatSupport = this.formatSupport[rendererIndex];
            int bestRendererSupport = 0;
            int i = 0;
            while (i < rendererFormatSupport.length) {
                int bestRendererSupport2 = bestRendererSupport;
                for (int i2 : rendererFormatSupport[i]) {
                    int i22;
                    switch (i22 & 7) {
                        case 3:
                            i22 = 2;
                            break;
                        case 4:
                            return 3;
                        default:
                            i22 = 1;
                            break;
                    }
                    bestRendererSupport2 = Math.max(bestRendererSupport2, i22);
                }
                i++;
                bestRendererSupport = bestRendererSupport2;
            }
            return bestRendererSupport;
        }

        public int getTrackTypeRendererSupport(int trackType) {
            int bestRendererSupport = 0;
            for (int i = 0; i < this.length; i++) {
                if (this.rendererTrackTypes[i] == trackType) {
                    bestRendererSupport = Math.max(bestRendererSupport, getRendererSupport(i));
                }
            }
            return bestRendererSupport;
        }

        public int getTrackFormatSupport(int rendererIndex, int groupIndex, int trackIndex) {
            return this.formatSupport[rendererIndex][groupIndex][trackIndex] & 7;
        }

        public int getAdaptiveSupport(int rendererIndex, int groupIndex, boolean includeCapabilitiesExceededTracks) {
            int trackCount = this.trackGroups[rendererIndex].get(groupIndex).length;
            int[] trackIndices = new int[trackCount];
            int trackIndexCount = 0;
            for (int i = 0; i < trackCount; i++) {
                int fixedSupport = getTrackFormatSupport(rendererIndex, groupIndex, i);
                if (fixedSupport == 4 || (includeCapabilitiesExceededTracks && fixedSupport == 3)) {
                    int trackIndexCount2 = trackIndexCount + 1;
                    trackIndices[trackIndexCount] = i;
                    trackIndexCount = trackIndexCount2;
                }
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
                String sampleMimeType = this.trackGroups[rendererIndex].get(groupIndex).getFormat(trackIndices[i]).sampleMimeType;
                int handledTrackCount2 = handledTrackCount + 1;
                if (handledTrackCount == 0) {
                    firstSampleMimeType = sampleMimeType;
                } else {
                    multipleMimeTypes = (Util.areEqual(firstSampleMimeType, sampleMimeType) ^ 1) | multipleMimeTypes;
                }
                adaptiveSupport = Math.min(adaptiveSupport, this.formatSupport[rendererIndex][groupIndex][i] & 24);
                i++;
                handledTrackCount = handledTrackCount2;
            }
            if (multipleMimeTypes) {
                return Math.min(adaptiveSupport, this.mixedMimeTypeAdaptiveSupport[rendererIndex]);
            }
            return adaptiveSupport;
        }

        public TrackGroupArray getUnassociatedTrackGroups() {
            return this.unassociatedTrackGroups;
        }
    }

    public static final class SelectionOverride {
        public final Factory factory;
        public final int groupIndex;
        public final int length;
        public final int[] tracks;

        public SelectionOverride(Factory factory, int groupIndex, int... tracks) {
            this.factory = factory;
            this.groupIndex = groupIndex;
            this.tracks = tracks;
            this.length = tracks.length;
        }

        public TrackSelection createTrackSelection(TrackGroupArray groups) {
            return this.factory.createTrackSelection(groups.get(this.groupIndex), this.tracks);
        }

        public boolean containsTrack(int track) {
            for (int overrideTrack : this.tracks) {
                if (overrideTrack == track) {
                    return true;
                }
            }
            return false;
        }
    }

    protected abstract TrackSelection[] selectTracks(RendererCapabilities[] rendererCapabilitiesArr, TrackGroupArray[] trackGroupArrayArr, int[][][] iArr) throws ExoPlaybackException;

    public final MappedTrackInfo getCurrentMappedTrackInfo() {
        return this.currentMappedTrackInfo;
    }

    public final void setRendererDisabled(int rendererIndex, boolean disabled) {
        if (this.rendererDisabledFlags.get(rendererIndex) != disabled) {
            this.rendererDisabledFlags.put(rendererIndex, disabled);
            invalidate();
        }
    }

    public final boolean getRendererDisabled(int rendererIndex) {
        return this.rendererDisabledFlags.get(rendererIndex);
    }

    public final void setSelectionOverride(int rendererIndex, TrackGroupArray groups, SelectionOverride override) {
        Map<TrackGroupArray, SelectionOverride> overrides = (Map) this.selectionOverrides.get(rendererIndex);
        if (overrides == null) {
            overrides = new HashMap();
            this.selectionOverrides.put(rendererIndex, overrides);
        }
        if (!overrides.containsKey(groups) || !Util.areEqual(overrides.get(groups), override)) {
            overrides.put(groups, override);
            invalidate();
        }
    }

    public final boolean hasSelectionOverride(int rendererIndex, TrackGroupArray groups) {
        Map<TrackGroupArray, SelectionOverride> overrides = (Map) this.selectionOverrides.get(rendererIndex);
        return overrides != null && overrides.containsKey(groups);
    }

    public final SelectionOverride getSelectionOverride(int rendererIndex, TrackGroupArray groups) {
        Map<TrackGroupArray, SelectionOverride> overrides = (Map) this.selectionOverrides.get(rendererIndex);
        return overrides != null ? (SelectionOverride) overrides.get(groups) : null;
    }

    public final void clearSelectionOverride(int rendererIndex, TrackGroupArray groups) {
        Map<TrackGroupArray, SelectionOverride> overrides = (Map) this.selectionOverrides.get(rendererIndex);
        if (overrides != null) {
            if (overrides.containsKey(groups)) {
                overrides.remove(groups);
                if (overrides.isEmpty()) {
                    this.selectionOverrides.remove(rendererIndex);
                }
                invalidate();
            }
        }
    }

    public final void clearSelectionOverrides(int rendererIndex) {
        Map<TrackGroupArray, ?> overrides = (Map) this.selectionOverrides.get(rendererIndex);
        if (overrides != null) {
            if (!overrides.isEmpty()) {
                this.selectionOverrides.remove(rendererIndex);
                invalidate();
            }
        }
    }

    public final void clearSelectionOverrides() {
        if (this.selectionOverrides.size() != 0) {
            this.selectionOverrides.clear();
            invalidate();
        }
    }

    public void setTunnelingAudioSessionId(int tunnelingAudioSessionId) {
        if (this.tunnelingAudioSessionId != tunnelingAudioSessionId) {
            this.tunnelingAudioSessionId = tunnelingAudioSessionId;
            invalidate();
        }
    }

    public final TrackSelectorResult selectTracks(RendererCapabilities[] rendererCapabilities, TrackGroupArray trackGroups) throws ExoPlaybackException {
        int i;
        MappingTrackSelector mappingTrackSelector = this;
        RendererCapabilities[] rendererCapabilitiesArr = rendererCapabilities;
        TrackGroupArray trackGroupArray = trackGroups;
        int i2 = 0;
        int[] rendererTrackGroupCounts = new int[(rendererCapabilitiesArr.length + 1)];
        TrackGroup[][] rendererTrackGroups = new TrackGroup[(rendererCapabilitiesArr.length + 1)][];
        int[][][] rendererFormatSupports = new int[(rendererCapabilitiesArr.length + 1)][][];
        for (i = 0; i < rendererTrackGroups.length; i++) {
            rendererTrackGroups[i] = new TrackGroup[trackGroupArray.length];
            rendererFormatSupports[i] = new int[trackGroupArray.length][];
        }
        int[] mixedMimeTypeAdaptationSupport = getMixedMimeTypeAdaptationSupport(rendererCapabilities);
        for (i = 0; i < trackGroupArray.length; i++) {
            int[] rendererFormatSupport;
            TrackGroup group = trackGroupArray.get(i);
            int rendererIndex = findRenderer(rendererCapabilitiesArr, group);
            if (rendererIndex == rendererCapabilitiesArr.length) {
                rendererFormatSupport = new int[group.length];
            } else {
                rendererFormatSupport = getFormatSupport(rendererCapabilitiesArr[rendererIndex], group);
            }
            int rendererTrackGroupCount = rendererTrackGroupCounts[rendererIndex];
            rendererTrackGroups[rendererIndex][rendererTrackGroupCount] = group;
            rendererFormatSupports[rendererIndex][rendererTrackGroupCount] = rendererFormatSupport;
            rendererTrackGroupCounts[rendererIndex] = rendererTrackGroupCounts[rendererIndex] + 1;
        }
        TrackGroupArray[] rendererTrackGroupArrays = new TrackGroupArray[rendererCapabilitiesArr.length];
        int[] rendererTrackTypes = new int[rendererCapabilitiesArr.length];
        for (i = 0; i < rendererCapabilitiesArr.length; i++) {
            int rendererTrackGroupCount2 = rendererTrackGroupCounts[i];
            rendererTrackGroupArrays[i] = new TrackGroupArray((TrackGroup[]) Arrays.copyOf(rendererTrackGroups[i], rendererTrackGroupCount2));
            rendererFormatSupports[i] = (int[][]) Arrays.copyOf(rendererFormatSupports[i], rendererTrackGroupCount2);
            rendererTrackTypes[i] = rendererCapabilitiesArr[i].getTrackType();
        }
        int unassociatedTrackGroupCount = rendererTrackGroupCounts[rendererCapabilitiesArr.length];
        TrackGroupArray unassociatedTrackGroupArray = new TrackGroupArray((TrackGroup[]) Arrays.copyOf(rendererTrackGroups[rendererCapabilitiesArr.length], unassociatedTrackGroupCount));
        TrackSelection[] trackSelections = selectTracks(rendererCapabilitiesArr, rendererTrackGroupArrays, rendererFormatSupports);
        i = 0;
        while (true) {
            TrackSelection trackSelection = null;
            if (i >= rendererCapabilitiesArr.length) {
                break;
            }
            if (mappingTrackSelector.rendererDisabledFlags.get(i)) {
                trackSelections[i] = null;
            } else {
                TrackGroupArray rendererTrackGroup = rendererTrackGroupArrays[i];
                if (hasSelectionOverride(i, rendererTrackGroup)) {
                    SelectionOverride override = (SelectionOverride) ((Map) mappingTrackSelector.selectionOverrides.get(i)).get(rendererTrackGroup);
                    if (override != null) {
                        trackSelection = override.createTrackSelection(rendererTrackGroup);
                    }
                    trackSelections[i] = trackSelection;
                }
            }
            i++;
        }
        boolean[] rendererEnabled = determineEnabledRenderers(rendererCapabilitiesArr, trackSelections);
        MappedTrackInfo mappedTrackInfo = new MappedTrackInfo(rendererTrackTypes, rendererTrackGroupArrays, mixedMimeTypeAdaptationSupport, rendererFormatSupports, unassociatedTrackGroupArray);
        RendererConfiguration[] rendererConfigurations = new RendererConfiguration[rendererCapabilitiesArr.length];
        while (i2 < rendererCapabilitiesArr.length) {
            rendererConfigurations[i2] = rendererEnabled[i2] ? RendererConfiguration.DEFAULT : null;
            i2++;
        }
        int i3 = mappingTrackSelector.tunnelingAudioSessionId;
        RendererConfiguration[] rendererConfigurations2 = rendererConfigurations;
        TrackSelection[] trackSelections2 = trackSelections;
        maybeConfigureRenderersForTunneling(rendererCapabilitiesArr, rendererTrackGroupArrays, rendererFormatSupports, rendererConfigurations, trackSelections, i3);
        TrackSelectionArray trackSelectionArray = new TrackSelectionArray(trackSelections2);
        return new TrackSelectorResult(trackGroupArray, rendererEnabled, trackSelectionArray, mappedTrackInfo, rendererConfigurations2);
    }

    private boolean[] determineEnabledRenderers(RendererCapabilities[] rendererCapabilities, TrackSelection[] trackSelections) {
        boolean[] rendererEnabled = new boolean[trackSelections.length];
        for (int i = 0; i < rendererEnabled.length; i++) {
            boolean z = !this.rendererDisabledFlags.get(i) && (rendererCapabilities[i].getTrackType() == 5 || trackSelections[i] != null);
            rendererEnabled[i] = z;
        }
        return rendererEnabled;
    }

    public final void onSelectionActivated(Object info) {
        this.currentMappedTrackInfo = (MappedTrackInfo) info;
    }

    private static int findRenderer(RendererCapabilities[] rendererCapabilities, TrackGroup group) throws ExoPlaybackException {
        int bestFormatSupportLevel = 0;
        int bestRendererIndex = rendererCapabilities.length;
        int rendererIndex = 0;
        while (rendererIndex < rendererCapabilities.length) {
            RendererCapabilities rendererCapability = rendererCapabilities[rendererIndex];
            int bestRendererIndex2 = bestRendererIndex;
            for (bestRendererIndex = 0; bestRendererIndex < group.length; bestRendererIndex++) {
                int formatSupportLevel = rendererCapability.supportsFormat(group.getFormat(bestRendererIndex)) & 7;
                if (formatSupportLevel > bestFormatSupportLevel) {
                    bestRendererIndex2 = rendererIndex;
                    bestFormatSupportLevel = formatSupportLevel;
                    if (bestFormatSupportLevel == 4) {
                        return bestRendererIndex2;
                    }
                }
            }
            rendererIndex++;
            bestRendererIndex = bestRendererIndex2;
        }
        return bestRendererIndex;
    }

    private static int[] getFormatSupport(RendererCapabilities rendererCapabilities, TrackGroup group) throws ExoPlaybackException {
        int[] formatSupport = new int[group.length];
        for (int i = 0; i < group.length; i++) {
            formatSupport[i] = rendererCapabilities.supportsFormat(group.getFormat(i));
        }
        return formatSupport;
    }

    private static int[] getMixedMimeTypeAdaptationSupport(RendererCapabilities[] rendererCapabilities) throws ExoPlaybackException {
        int[] mixedMimeTypeAdaptationSupport = new int[rendererCapabilities.length];
        for (int i = 0; i < mixedMimeTypeAdaptationSupport.length; i++) {
            mixedMimeTypeAdaptationSupport[i] = rendererCapabilities[i].supportsMixedMimeTypeAdaptation();
        }
        return mixedMimeTypeAdaptationSupport;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static void maybeConfigureRenderersForTunneling(RendererCapabilities[] rendererCapabilities, TrackGroupArray[] rendererTrackGroupArrays, int[][][] rendererFormatSupports, RendererConfiguration[] rendererConfigurations, TrackSelection[] trackSelections, int tunnelingAudioSessionId) {
        RendererCapabilities[] rendererCapabilitiesArr = rendererCapabilities;
        int i = tunnelingAudioSessionId;
        if (i != 0) {
            int i2;
            boolean enableTunneling = true;
            int tunnelingVideoRendererIndex = -1;
            int tunnelingAudioRendererIndex = -1;
            int i3 = 0;
            while (true) {
                i2 = 1;
                if (i3 >= rendererCapabilitiesArr.length) {
                    break;
                }
                int rendererType = rendererCapabilitiesArr[i3].getTrackType();
                TrackSelection trackSelection = trackSelections[i3];
                if ((rendererType == 1 || rendererType == 2) && trackSelection != null && rendererSupportsTunneling(rendererFormatSupports[i3], rendererTrackGroupArrays[i3], trackSelection)) {
                    if (rendererType == 1) {
                        if (tunnelingAudioRendererIndex != -1) {
                            break;
                        }
                        tunnelingAudioRendererIndex = i3;
                    } else if (tunnelingVideoRendererIndex != -1) {
                        break;
                    } else {
                        tunnelingVideoRendererIndex = i3;
                    }
                }
                i3++;
            }
            enableTunneling = false;
            if (tunnelingAudioRendererIndex == -1 || tunnelingVideoRendererIndex == -1) {
                i2 = 0;
            }
            if (enableTunneling & i2) {
                RendererConfiguration tunnelingRendererConfiguration = new RendererConfiguration(i);
                rendererConfigurations[tunnelingAudioRendererIndex] = tunnelingRendererConfiguration;
                rendererConfigurations[tunnelingVideoRendererIndex] = tunnelingRendererConfiguration;
            }
        }
    }

    private static boolean rendererSupportsTunneling(int[][] formatSupport, TrackGroupArray trackGroups, TrackSelection selection) {
        if (selection == null) {
            return false;
        }
        int trackGroupIndex = trackGroups.indexOf(selection.getTrackGroup());
        for (int i = 0; i < selection.length(); i++) {
            if ((formatSupport[trackGroupIndex][selection.getIndexInTrackGroup(i)] & 32) != 32) {
                return false;
            }
        }
        return true;
    }
}
