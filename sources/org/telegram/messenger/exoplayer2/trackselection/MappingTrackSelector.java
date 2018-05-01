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

        MappedTrackInfo(int[] iArr, TrackGroupArray[] trackGroupArrayArr, int[] iArr2, int[][][] iArr3, TrackGroupArray trackGroupArray) {
            this.rendererTrackTypes = iArr;
            this.trackGroups = trackGroupArrayArr;
            this.formatSupport = iArr3;
            this.mixedMimeTypeAdaptiveSupport = iArr2;
            this.unassociatedTrackGroups = trackGroupArray;
            this.length = trackGroupArrayArr.length;
        }

        public TrackGroupArray getTrackGroups(int i) {
            return this.trackGroups[i];
        }

        public int getRendererSupport(int i) {
            i = this.formatSupport[i];
            int i2 = 0;
            int i3 = i2;
            while (i2 < i.length) {
                int i4 = i3;
                for (int i5 : i[i2]) {
                    int i52;
                    switch (i52 & 7) {
                        case 3:
                            i52 = 2;
                            break;
                        case 4:
                            return 3;
                        default:
                            i52 = 1;
                            break;
                    }
                    i4 = Math.max(i4, i52);
                }
                i2++;
                i3 = i4;
            }
            return i3;
        }

        public int getTrackTypeRendererSupport(int i) {
            int i2 = 0;
            int i3 = 0;
            while (i2 < this.length) {
                if (this.rendererTrackTypes[i2] == i) {
                    i3 = Math.max(i3, getRendererSupport(i2));
                }
                i2++;
            }
            return i3;
        }

        public int getTrackFormatSupport(int i, int i2, int i3) {
            return this.formatSupport[i][i2][i3] & 7;
        }

        public int getAdaptiveSupport(int i, int i2, boolean z) {
            int i3 = this.trackGroups[i].get(i2).length;
            int[] iArr = new int[i3];
            int i4 = 0;
            int i5 = 0;
            while (i4 < i3) {
                int trackFormatSupport = getTrackFormatSupport(i, i2, i4);
                if (trackFormatSupport == 4 || (z && trackFormatSupport == 3)) {
                    trackFormatSupport = i5 + 1;
                    iArr[i5] = i4;
                    i5 = trackFormatSupport;
                }
                i4++;
            }
            return getAdaptiveSupport(i, i2, Arrays.copyOf(iArr, i5));
        }

        public int getAdaptiveSupport(int i, int i2, int[] iArr) {
            int i3 = 0;
            int i4 = 16;
            Object obj = null;
            int i5 = 0;
            int i6 = i5;
            while (i3 < iArr.length) {
                String str = this.trackGroups[i].get(i2).getFormat(iArr[i3]).sampleMimeType;
                int i7 = i5 + 1;
                if (i5 == 0) {
                    obj = str;
                } else {
                    i6 = (Util.areEqual(obj, str) ^ 1) | i6;
                }
                i4 = Math.min(i4, this.formatSupport[i][i2][i3] & 24);
                i3++;
                i5 = i7;
            }
            return i6 != 0 ? Math.min(i4, this.mixedMimeTypeAdaptiveSupport[i]) : i4;
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

        public SelectionOverride(Factory factory, int i, int... iArr) {
            this.factory = factory;
            this.groupIndex = i;
            this.tracks = iArr;
            this.length = iArr.length;
        }

        public TrackSelection createTrackSelection(TrackGroupArray trackGroupArray) {
            return this.factory.createTrackSelection(trackGroupArray.get(this.groupIndex), this.tracks);
        }

        public boolean containsTrack(int i) {
            for (int i2 : this.tracks) {
                if (i2 == i) {
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

    public final void setRendererDisabled(int i, boolean z) {
        if (this.rendererDisabledFlags.get(i) != z) {
            this.rendererDisabledFlags.put(i, z);
            invalidate();
        }
    }

    public final boolean getRendererDisabled(int i) {
        return this.rendererDisabledFlags.get(i);
    }

    public final void setSelectionOverride(int i, TrackGroupArray trackGroupArray, SelectionOverride selectionOverride) {
        Map map = (Map) this.selectionOverrides.get(i);
        if (map == null) {
            map = new HashMap();
            this.selectionOverrides.put(i, map);
        }
        if (map.containsKey(trackGroupArray) == 0 || Util.areEqual(map.get(trackGroupArray), selectionOverride) == 0) {
            map.put(trackGroupArray, selectionOverride);
            invalidate();
        }
    }

    public final boolean hasSelectionOverride(int i, TrackGroupArray trackGroupArray) {
        Map map = (Map) this.selectionOverrides.get(i);
        return (map == null || map.containsKey(trackGroupArray) == 0) ? false : true;
    }

    public final SelectionOverride getSelectionOverride(int i, TrackGroupArray trackGroupArray) {
        Map map = (Map) this.selectionOverrides.get(i);
        return map != null ? (SelectionOverride) map.get(trackGroupArray) : 0;
    }

    public final void clearSelectionOverride(int i, TrackGroupArray trackGroupArray) {
        Map map = (Map) this.selectionOverrides.get(i);
        if (map != null) {
            if (map.containsKey(trackGroupArray)) {
                map.remove(trackGroupArray);
                if (map.isEmpty() != null) {
                    this.selectionOverrides.remove(i);
                }
                invalidate();
            }
        }
    }

    public final void clearSelectionOverrides(int i) {
        Map map = (Map) this.selectionOverrides.get(i);
        if (map != null) {
            if (!map.isEmpty()) {
                this.selectionOverrides.remove(i);
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

    public void setTunnelingAudioSessionId(int i) {
        if (this.tunnelingAudioSessionId != i) {
            this.tunnelingAudioSessionId = i;
            invalidate();
        }
    }

    public final TrackSelectorResult selectTracks(RendererCapabilities[] rendererCapabilitiesArr, TrackGroupArray trackGroupArray) throws ExoPlaybackException {
        int i;
        MappingTrackSelector mappingTrackSelector = this;
        RendererCapabilities[] rendererCapabilitiesArr2 = rendererCapabilitiesArr;
        TrackGroupArray trackGroupArray2 = trackGroupArray;
        int i2 = 0;
        int[] iArr = new int[(rendererCapabilitiesArr2.length + 1)];
        TrackGroup[][] trackGroupArr = new TrackGroup[(rendererCapabilitiesArr2.length + 1)][];
        int[][][] iArr2 = new int[(rendererCapabilitiesArr2.length + 1)][][];
        for (i = 0; i < trackGroupArr.length; i++) {
            trackGroupArr[i] = new TrackGroup[trackGroupArray2.length];
            iArr2[i] = new int[trackGroupArray2.length][];
        }
        int[] mixedMimeTypeAdaptationSupport = getMixedMimeTypeAdaptationSupport(rendererCapabilitiesArr);
        for (i = 0; i < trackGroupArray2.length; i++) {
            int[] iArr3;
            TrackGroup trackGroup = trackGroupArray2.get(i);
            int findRenderer = findRenderer(rendererCapabilitiesArr2, trackGroup);
            if (findRenderer == rendererCapabilitiesArr2.length) {
                iArr3 = new int[trackGroup.length];
            } else {
                iArr3 = getFormatSupport(rendererCapabilitiesArr2[findRenderer], trackGroup);
            }
            int i3 = iArr[findRenderer];
            trackGroupArr[findRenderer][i3] = trackGroup;
            iArr2[findRenderer][i3] = iArr3;
            iArr[findRenderer] = iArr[findRenderer] + 1;
        }
        TrackGroupArray[] trackGroupArrayArr = new TrackGroupArray[rendererCapabilitiesArr2.length];
        int[] iArr4 = new int[rendererCapabilitiesArr2.length];
        for (int i4 = 0; i4 < rendererCapabilitiesArr2.length; i4++) {
            int i5 = iArr[i4];
            trackGroupArrayArr[i4] = new TrackGroupArray((TrackGroup[]) Arrays.copyOf(trackGroupArr[i4], i5));
            iArr2[i4] = (int[][]) Arrays.copyOf(iArr2[i4], i5);
            iArr4[i4] = rendererCapabilitiesArr2[i4].getTrackType();
        }
        TrackGroupArray trackGroupArray3 = new TrackGroupArray((TrackGroup[]) Arrays.copyOf(trackGroupArr[rendererCapabilitiesArr2.length], iArr[rendererCapabilitiesArr2.length]));
        TrackSelection[] selectTracks = selectTracks(rendererCapabilitiesArr2, trackGroupArrayArr, iArr2);
        int i6 = 0;
        while (true) {
            TrackSelection trackSelection = null;
            if (i6 >= rendererCapabilitiesArr2.length) {
                break;
            }
            if (mappingTrackSelector.rendererDisabledFlags.get(i6)) {
                selectTracks[i6] = null;
            } else {
                TrackGroupArray trackGroupArray4 = trackGroupArrayArr[i6];
                if (hasSelectionOverride(i6, trackGroupArray4)) {
                    SelectionOverride selectionOverride = (SelectionOverride) ((Map) mappingTrackSelector.selectionOverrides.get(i6)).get(trackGroupArray4);
                    if (selectionOverride != null) {
                        trackSelection = selectionOverride.createTrackSelection(trackGroupArray4);
                    }
                    selectTracks[i6] = trackSelection;
                }
            }
            i6++;
        }
        boolean[] determineEnabledRenderers = determineEnabledRenderers(rendererCapabilitiesArr2, selectTracks);
        MappedTrackInfo mappedTrackInfo = new MappedTrackInfo(iArr4, trackGroupArrayArr, mixedMimeTypeAdaptationSupport, iArr2, trackGroupArray3);
        RendererConfiguration[] rendererConfigurationArr = new RendererConfiguration[rendererCapabilitiesArr2.length];
        while (i2 < rendererCapabilitiesArr2.length) {
            rendererConfigurationArr[i2] = determineEnabledRenderers[i2] ? RendererConfiguration.DEFAULT : null;
            i2++;
        }
        maybeConfigureRenderersForTunneling(rendererCapabilitiesArr2, trackGroupArrayArr, iArr2, rendererConfigurationArr, selectTracks, mappingTrackSelector.tunnelingAudioSessionId);
        return new TrackSelectorResult(trackGroupArray2, determineEnabledRenderers, new TrackSelectionArray(selectTracks), mappedTrackInfo, rendererConfigurationArr);
    }

    private boolean[] determineEnabledRenderers(RendererCapabilities[] rendererCapabilitiesArr, TrackSelection[] trackSelectionArr) {
        boolean[] zArr = new boolean[trackSelectionArr.length];
        for (int i = 0; i < zArr.length; i++) {
            boolean z = !this.rendererDisabledFlags.get(i) && (rendererCapabilitiesArr[i].getTrackType() == 5 || trackSelectionArr[i] != null);
            zArr[i] = z;
        }
        return zArr;
    }

    public final void onSelectionActivated(Object obj) {
        this.currentMappedTrackInfo = (MappedTrackInfo) obj;
    }

    private static int findRenderer(RendererCapabilities[] rendererCapabilitiesArr, TrackGroup trackGroup) throws ExoPlaybackException {
        int i = 0;
        int length = rendererCapabilitiesArr.length;
        int i2 = i;
        while (i2 < rendererCapabilitiesArr.length) {
            RendererCapabilities rendererCapabilities = rendererCapabilitiesArr[i2];
            int i3 = length;
            for (length = 0; length < trackGroup.length; length++) {
                int supportsFormat = rendererCapabilities.supportsFormat(trackGroup.getFormat(length)) & 7;
                if (supportsFormat > i) {
                    if (supportsFormat == 4) {
                        return i2;
                    }
                    i3 = i2;
                    i = supportsFormat;
                }
            }
            i2++;
            length = i3;
        }
        return length;
    }

    private static int[] getFormatSupport(RendererCapabilities rendererCapabilities, TrackGroup trackGroup) throws ExoPlaybackException {
        int[] iArr = new int[trackGroup.length];
        for (int i = 0; i < trackGroup.length; i++) {
            iArr[i] = rendererCapabilities.supportsFormat(trackGroup.getFormat(i));
        }
        return iArr;
    }

    private static int[] getMixedMimeTypeAdaptationSupport(RendererCapabilities[] rendererCapabilitiesArr) throws ExoPlaybackException {
        int[] iArr = new int[rendererCapabilitiesArr.length];
        for (int i = 0; i < iArr.length; i++) {
            iArr[i] = rendererCapabilitiesArr[i].supportsMixedMimeTypeAdaptation();
        }
        return iArr;
    }

    private static void maybeConfigureRenderersForTunneling(RendererCapabilities[] rendererCapabilitiesArr, TrackGroupArray[] trackGroupArrayArr, int[][][] iArr, RendererConfiguration[] rendererConfigurationArr, TrackSelection[] trackSelectionArr, int i) {
        if (i != 0) {
            int i2 = 0;
            int i3 = 0;
            int i4 = -1;
            int i5 = i4;
            while (i3 < rendererCapabilitiesArr.length) {
                int trackType = rendererCapabilitiesArr[i3].getTrackType();
                TrackSelection trackSelection = trackSelectionArr[i3];
                if ((trackType == 1 || trackType == 2) && trackSelection != null && rendererSupportsTunneling(iArr[i3], trackGroupArrayArr[i3], trackSelection)) {
                    if (trackType == 1) {
                        if (i4 == -1) {
                            i4 = i3;
                        }
                    } else if (i5 == -1) {
                        i5 = i3;
                    }
                    rendererCapabilitiesArr = null;
                    break;
                }
                i3++;
            }
            rendererCapabilitiesArr = 1;
            if (!(i4 == -1 || i5 == -1)) {
                i2 = 1;
            }
            if ((rendererCapabilitiesArr & i2) != null) {
                rendererCapabilitiesArr = new RendererConfiguration(i);
                rendererConfigurationArr[i4] = rendererCapabilitiesArr;
                rendererConfigurationArr[i5] = rendererCapabilitiesArr;
            }
        }
    }

    private static boolean rendererSupportsTunneling(int[][] iArr, TrackGroupArray trackGroupArray, TrackSelection trackSelection) {
        if (trackSelection == null) {
            return false;
        }
        trackGroupArray = trackGroupArray.indexOf(trackSelection.getTrackGroup());
        for (int i = 0; i < trackSelection.length(); i++) {
            if ((iArr[trackGroupArray][trackSelection.getIndexInTrackGroup(i)] & 32) != 32) {
                return false;
            }
        }
        return 1;
    }
}
