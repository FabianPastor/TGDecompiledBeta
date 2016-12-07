package org.telegram.messenger.exoplayer2.trackselection;

import android.content.Context;
import android.graphics.Point;
import android.text.TextUtils;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.telegram.messenger.exoplayer2.ExoPlaybackException;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.RendererCapabilities;
import org.telegram.messenger.exoplayer2.source.TrackGroup;
import org.telegram.messenger.exoplayer2.source.TrackGroupArray;
import org.telegram.messenger.exoplayer2.trackselection.TrackSelection.Factory;
import org.telegram.messenger.exoplayer2.util.Assertions;
import org.telegram.messenger.exoplayer2.util.Util;
import org.telegram.tgnet.ConnectionsManager;

public class DefaultTrackSelector extends MappingTrackSelector {
    private static final float FRACTION_TO_CONSIDER_FULLSCREEN = 0.98f;
    private static final int[] NO_TRACKS = new int[0];
    private final Factory adaptiveVideoTrackSelectionFactory;
    private final AtomicReference<Parameters> params;

    public static final class Parameters {
        public final boolean allowMixedMimeAdaptiveness;
        public final boolean allowNonSeamlessAdaptiveness;
        public final boolean exceedVideoConstraintsIfNecessary;
        public final int maxVideoHeight;
        public final int maxVideoWidth;
        public final boolean orientationMayChange;
        public final String preferredAudioLanguage;
        public final String preferredTextLanguage;
        public final int viewportHeight;
        public final int viewportWidth;

        public Parameters() {
            this(null, null, false, true, ConnectionsManager.DEFAULT_DATACENTER_ID, ConnectionsManager.DEFAULT_DATACENTER_ID, true, ConnectionsManager.DEFAULT_DATACENTER_ID, ConnectionsManager.DEFAULT_DATACENTER_ID, true);
        }

        public Parameters(String preferredAudioLanguage, String preferredTextLanguage, boolean allowMixedMimeAdaptiveness, boolean allowNonSeamlessAdaptiveness, int maxVideoWidth, int maxVideoHeight, boolean exceedVideoConstraintsIfNecessary, int viewportWidth, int viewportHeight, boolean orientationMayChange) {
            this.preferredAudioLanguage = preferredAudioLanguage;
            this.preferredTextLanguage = preferredTextLanguage;
            this.allowMixedMimeAdaptiveness = allowMixedMimeAdaptiveness;
            this.allowNonSeamlessAdaptiveness = allowNonSeamlessAdaptiveness;
            this.maxVideoWidth = maxVideoWidth;
            this.maxVideoHeight = maxVideoHeight;
            this.exceedVideoConstraintsIfNecessary = exceedVideoConstraintsIfNecessary;
            this.viewportWidth = viewportWidth;
            this.viewportHeight = viewportHeight;
            this.orientationMayChange = orientationMayChange;
        }

        public Parameters withPreferredAudioLanguage(String preferredAudioLanguage) {
            preferredAudioLanguage = Util.normalizeLanguageCode(preferredAudioLanguage);
            if (TextUtils.equals(preferredAudioLanguage, this.preferredAudioLanguage)) {
                return this;
            }
            return new Parameters(preferredAudioLanguage, this.preferredTextLanguage, this.allowMixedMimeAdaptiveness, this.allowNonSeamlessAdaptiveness, this.maxVideoWidth, this.maxVideoHeight, this.exceedVideoConstraintsIfNecessary, this.viewportWidth, this.viewportHeight, this.orientationMayChange);
        }

        public Parameters withPreferredTextLanguage(String preferredTextLanguage) {
            preferredTextLanguage = Util.normalizeLanguageCode(preferredTextLanguage);
            if (TextUtils.equals(preferredTextLanguage, this.preferredTextLanguage)) {
                return this;
            }
            return new Parameters(this.preferredAudioLanguage, preferredTextLanguage, this.allowMixedMimeAdaptiveness, this.allowNonSeamlessAdaptiveness, this.maxVideoWidth, this.maxVideoHeight, this.exceedVideoConstraintsIfNecessary, this.viewportWidth, this.viewportHeight, this.orientationMayChange);
        }

        public Parameters withAllowMixedMimeAdaptiveness(boolean allowMixedMimeAdaptiveness) {
            if (allowMixedMimeAdaptiveness == this.allowMixedMimeAdaptiveness) {
                return this;
            }
            return new Parameters(this.preferredAudioLanguage, this.preferredTextLanguage, allowMixedMimeAdaptiveness, this.allowNonSeamlessAdaptiveness, this.maxVideoWidth, this.maxVideoHeight, this.exceedVideoConstraintsIfNecessary, this.viewportWidth, this.viewportHeight, this.orientationMayChange);
        }

        public Parameters withAllowNonSeamlessAdaptiveness(boolean allowNonSeamlessAdaptiveness) {
            if (allowNonSeamlessAdaptiveness == this.allowNonSeamlessAdaptiveness) {
                return this;
            }
            return new Parameters(this.preferredAudioLanguage, this.preferredTextLanguage, this.allowMixedMimeAdaptiveness, allowNonSeamlessAdaptiveness, this.maxVideoWidth, this.maxVideoHeight, this.exceedVideoConstraintsIfNecessary, this.viewportWidth, this.viewportHeight, this.orientationMayChange);
        }

        public Parameters withMaxVideoSize(int maxVideoWidth, int maxVideoHeight) {
            if (maxVideoWidth == this.maxVideoWidth && maxVideoHeight == this.maxVideoHeight) {
                return this;
            }
            return new Parameters(this.preferredAudioLanguage, this.preferredTextLanguage, this.allowMixedMimeAdaptiveness, this.allowNonSeamlessAdaptiveness, maxVideoWidth, maxVideoHeight, this.exceedVideoConstraintsIfNecessary, this.viewportWidth, this.viewportHeight, this.orientationMayChange);
        }

        public Parameters withMaxVideoSizeSd() {
            return withMaxVideoSize(1279, 719);
        }

        public Parameters withoutVideoSizeConstraints() {
            return withMaxVideoSize(ConnectionsManager.DEFAULT_DATACENTER_ID, ConnectionsManager.DEFAULT_DATACENTER_ID);
        }

        public Parameters withExceedVideoConstraintsIfNecessary(boolean exceedVideoConstraintsIfNecessary) {
            if (exceedVideoConstraintsIfNecessary == this.exceedVideoConstraintsIfNecessary) {
                return this;
            }
            return new Parameters(this.preferredAudioLanguage, this.preferredTextLanguage, this.allowMixedMimeAdaptiveness, this.allowNonSeamlessAdaptiveness, this.maxVideoWidth, this.maxVideoHeight, exceedVideoConstraintsIfNecessary, this.viewportWidth, this.viewportHeight, this.orientationMayChange);
        }

        public Parameters withViewportSize(int viewportWidth, int viewportHeight, boolean orientationMayChange) {
            return (viewportWidth == this.viewportWidth && viewportHeight == this.viewportHeight && orientationMayChange == this.orientationMayChange) ? this : new Parameters(this.preferredAudioLanguage, this.preferredTextLanguage, this.allowMixedMimeAdaptiveness, this.allowNonSeamlessAdaptiveness, this.maxVideoWidth, this.maxVideoHeight, this.exceedVideoConstraintsIfNecessary, viewportWidth, viewportHeight, orientationMayChange);
        }

        public Parameters withViewportSizeFromContext(Context context, boolean orientationMayChange) {
            Point viewportSize = Util.getPhysicalDisplaySize(context);
            return withViewportSize(viewportSize.x, viewportSize.y, orientationMayChange);
        }

        public Parameters withoutViewportSizeConstraints() {
            return withViewportSize(ConnectionsManager.DEFAULT_DATACENTER_ID, ConnectionsManager.DEFAULT_DATACENTER_ID, true);
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            Parameters other = (Parameters) obj;
            if (this.allowMixedMimeAdaptiveness == other.allowMixedMimeAdaptiveness && this.allowNonSeamlessAdaptiveness == other.allowNonSeamlessAdaptiveness && this.maxVideoWidth == other.maxVideoWidth && this.maxVideoHeight == other.maxVideoHeight && this.exceedVideoConstraintsIfNecessary == other.exceedVideoConstraintsIfNecessary && this.orientationMayChange == other.orientationMayChange && this.viewportWidth == other.viewportWidth && this.viewportHeight == other.viewportHeight && TextUtils.equals(this.preferredAudioLanguage, other.preferredAudioLanguage) && TextUtils.equals(this.preferredTextLanguage, other.preferredTextLanguage)) {
                return true;
            }
            return false;
        }

        public int hashCode() {
            int i;
            int i2 = 1;
            int hashCode = ((((this.preferredAudioLanguage.hashCode() * 31) + this.preferredTextLanguage.hashCode()) * 31) + (this.allowMixedMimeAdaptiveness ? 1 : 0)) * 31;
            if (this.allowNonSeamlessAdaptiveness) {
                i = 1;
            } else {
                i = 0;
            }
            hashCode = (((((hashCode + i) * 31) + this.maxVideoWidth) * 31) + this.maxVideoHeight) * 31;
            if (this.exceedVideoConstraintsIfNecessary) {
                i = 1;
            } else {
                i = 0;
            }
            i = (hashCode + i) * 31;
            if (!this.orientationMayChange) {
                i2 = 0;
            }
            return ((((i + i2) * 31) + this.viewportWidth) * 31) + this.viewportHeight;
        }
    }

    public DefaultTrackSelector() {
        this(null);
    }

    public DefaultTrackSelector(Factory adaptiveVideoTrackSelectionFactory) {
        this.adaptiveVideoTrackSelectionFactory = adaptiveVideoTrackSelectionFactory;
        this.params = new AtomicReference(new Parameters());
    }

    public void setParameters(Parameters params) {
        if (!((Parameters) this.params.get()).equals(params)) {
            this.params.set(Assertions.checkNotNull(params));
            invalidate();
        }
    }

    public Parameters getParameters() {
        return (Parameters) this.params.get();
    }

    protected TrackSelection[] selectTracks(RendererCapabilities[] rendererCapabilities, TrackGroupArray[] rendererTrackGroupArrays, int[][][] rendererFormatSupports) throws ExoPlaybackException {
        TrackSelection[] rendererTrackSelections = new TrackSelection[rendererCapabilities.length];
        Parameters params = (Parameters) this.params.get();
        for (int i = 0; i < rendererCapabilities.length; i++) {
            switch (rendererCapabilities[i].getTrackType()) {
                case 1:
                    rendererTrackSelections[i] = selectAudioTrack(rendererTrackGroupArrays[i], rendererFormatSupports[i], params.preferredAudioLanguage);
                    break;
                case 2:
                    rendererTrackSelections[i] = selectVideoTrack(rendererCapabilities[i], rendererTrackGroupArrays[i], rendererFormatSupports[i], params.maxVideoWidth, params.maxVideoHeight, params.allowNonSeamlessAdaptiveness, params.allowMixedMimeAdaptiveness, params.viewportWidth, params.viewportHeight, params.orientationMayChange, this.adaptiveVideoTrackSelectionFactory, params.exceedVideoConstraintsIfNecessary);
                    break;
                case 3:
                    rendererTrackSelections[i] = selectTextTrack(rendererTrackGroupArrays[i], rendererFormatSupports[i], params.preferredTextLanguage, params.preferredAudioLanguage);
                    break;
                default:
                    rendererTrackSelections[i] = selectOtherTrack(rendererCapabilities[i].getTrackType(), rendererTrackGroupArrays[i], rendererFormatSupports[i]);
                    break;
            }
        }
        return rendererTrackSelections;
    }

    protected TrackSelection selectVideoTrack(RendererCapabilities rendererCapabilities, TrackGroupArray groups, int[][] formatSupport, int maxVideoWidth, int maxVideoHeight, boolean allowNonSeamlessAdaptiveness, boolean allowMixedMimeAdaptiveness, int viewportWidth, int viewportHeight, boolean orientationMayChange, Factory adaptiveVideoTrackSelectionFactory, boolean exceedConstraintsIfNecessary) throws ExoPlaybackException {
        TrackSelection selection = null;
        if (adaptiveVideoTrackSelectionFactory != null) {
            selection = selectAdaptiveVideoTrack(rendererCapabilities, groups, formatSupport, maxVideoWidth, maxVideoHeight, allowNonSeamlessAdaptiveness, allowMixedMimeAdaptiveness, viewportWidth, viewportHeight, orientationMayChange, adaptiveVideoTrackSelectionFactory);
        }
        if (selection == null) {
            return selectFixedVideoTrack(groups, formatSupport, maxVideoWidth, maxVideoHeight, viewportWidth, viewportHeight, orientationMayChange, exceedConstraintsIfNecessary);
        }
        return selection;
    }

    private static TrackSelection selectAdaptiveVideoTrack(RendererCapabilities rendererCapabilities, TrackGroupArray groups, int[][] formatSupport, int maxVideoWidth, int maxVideoHeight, boolean allowNonSeamlessAdaptiveness, boolean allowMixedMimeAdaptiveness, int viewportWidth, int viewportHeight, boolean orientationMayChange, Factory adaptiveVideoTrackSelectionFactory) throws ExoPlaybackException {
        int requiredAdaptiveSupport = allowNonSeamlessAdaptiveness ? 12 : 8;
        boolean allowMixedMimeTypes = allowMixedMimeAdaptiveness && (rendererCapabilities.supportsMixedMimeTypeAdaptation() & requiredAdaptiveSupport) != 0;
        for (int i = 0; i < groups.length; i++) {
            TrackGroup group = groups.get(i);
            int[] adaptiveTracks = getAdaptiveTracksForGroup(group, formatSupport[i], allowMixedMimeTypes, requiredAdaptiveSupport, maxVideoWidth, maxVideoHeight, viewportWidth, viewportHeight, orientationMayChange);
            if (adaptiveTracks.length > 0) {
                return adaptiveVideoTrackSelectionFactory.createTrackSelection(group, adaptiveTracks);
            }
        }
        return null;
    }

    private static int[] getAdaptiveTracksForGroup(TrackGroup group, int[] formatSupport, boolean allowMixedMimeTypes, int requiredAdaptiveSupport, int maxVideoWidth, int maxVideoHeight, int viewportWidth, int viewportHeight, boolean orientationMayChange) {
        if (group.length < 2) {
            return NO_TRACKS;
        }
        List<Integer> selectedTrackIndices = getViewportFilteredTrackIndices(group, viewportWidth, viewportHeight, orientationMayChange);
        if (selectedTrackIndices.size() < 2) {
            return NO_TRACKS;
        }
        String selectedMimeType = null;
        if (!allowMixedMimeTypes) {
            HashSet<String> seenMimeTypes = new HashSet();
            int selectedMimeTypeTrackCount = 0;
            for (int i = 0; i < selectedTrackIndices.size(); i++) {
                String sampleMimeType = group.getFormat(((Integer) selectedTrackIndices.get(i)).intValue()).sampleMimeType;
                if (!seenMimeTypes.contains(sampleMimeType)) {
                    seenMimeTypes.add(sampleMimeType);
                    int countForMimeType = getAdaptiveTrackCountForMimeType(group, formatSupport, requiredAdaptiveSupport, sampleMimeType, maxVideoWidth, maxVideoHeight, selectedTrackIndices);
                    if (countForMimeType > selectedMimeTypeTrackCount) {
                        selectedMimeType = sampleMimeType;
                        selectedMimeTypeTrackCount = countForMimeType;
                    }
                }
            }
        }
        filterAdaptiveTrackCountForMimeType(group, formatSupport, requiredAdaptiveSupport, selectedMimeType, maxVideoWidth, maxVideoHeight, selectedTrackIndices);
        return selectedTrackIndices.size() < 2 ? NO_TRACKS : Util.toArray(selectedTrackIndices);
    }

    private static int getAdaptiveTrackCountForMimeType(TrackGroup group, int[] formatSupport, int requiredAdaptiveSupport, String mimeType, int maxVideoWidth, int maxVideoHeight, List<Integer> selectedTrackIndices) {
        int adaptiveTrackCount = 0;
        for (int i = 0; i < selectedTrackIndices.size(); i++) {
            int trackIndex = ((Integer) selectedTrackIndices.get(i)).intValue();
            if (isSupportedAdaptiveVideoTrack(group.getFormat(trackIndex), mimeType, formatSupport[trackIndex], requiredAdaptiveSupport, maxVideoWidth, maxVideoHeight)) {
                adaptiveTrackCount++;
            }
        }
        return adaptiveTrackCount;
    }

    private static void filterAdaptiveTrackCountForMimeType(TrackGroup group, int[] formatSupport, int requiredAdaptiveSupport, String mimeType, int maxVideoWidth, int maxVideoHeight, List<Integer> selectedTrackIndices) {
        for (int i = selectedTrackIndices.size() - 1; i >= 0; i--) {
            int trackIndex = ((Integer) selectedTrackIndices.get(i)).intValue();
            if (!isSupportedAdaptiveVideoTrack(group.getFormat(trackIndex), mimeType, formatSupport[trackIndex], requiredAdaptiveSupport, maxVideoWidth, maxVideoHeight)) {
                selectedTrackIndices.remove(i);
            }
        }
    }

    private static boolean isSupportedAdaptiveVideoTrack(Format format, String mimeType, int formatSupport, int requiredAdaptiveSupport, int maxVideoWidth, int maxVideoHeight) {
        return isSupported(formatSupport) && (formatSupport & requiredAdaptiveSupport) != 0 && ((mimeType == null || Util.areEqual(format.sampleMimeType, mimeType)) && ((format.width == -1 || format.width <= maxVideoWidth) && (format.height == -1 || format.height <= maxVideoHeight)));
    }

    private static TrackSelection selectFixedVideoTrack(TrackGroupArray groups, int[][] formatSupport, int maxVideoWidth, int maxVideoHeight, int viewportWidth, int viewportHeight, boolean orientationMayChange, boolean exceedConstraintsIfNecessary) {
        TrackGroup selectedGroup = null;
        int selectedTrackIndex = 0;
        int selectedPixelCount = -1;
        boolean selectedIsWithinConstraints = false;
        for (int groupIndex = 0; groupIndex < groups.length; groupIndex++) {
            TrackGroup group = groups.get(groupIndex);
            List<Integer> selectedTrackIndices = getViewportFilteredTrackIndices(group, viewportWidth, viewportHeight, orientationMayChange);
            int[] trackFormatSupport = formatSupport[groupIndex];
            for (int trackIndex = 0; trackIndex < group.length; trackIndex++) {
                if (isSupported(trackFormatSupport[trackIndex])) {
                    Format format = group.getFormat(trackIndex);
                    boolean isWithinConstraints = selectedTrackIndices.contains(Integer.valueOf(trackIndex)) && ((format.width == -1 || format.width <= maxVideoWidth) && (format.height == -1 || format.height <= maxVideoHeight));
                    int pixelCount = format.getPixelCount();
                    boolean selectTrack = selectedIsWithinConstraints ? isWithinConstraints && comparePixelCounts(pixelCount, selectedPixelCount) > 0 : isWithinConstraints || (exceedConstraintsIfNecessary && (selectedGroup == null || comparePixelCounts(pixelCount, selectedPixelCount) < 0));
                    if (selectTrack) {
                        selectedGroup = group;
                        selectedTrackIndex = trackIndex;
                        selectedPixelCount = pixelCount;
                        selectedIsWithinConstraints = isWithinConstraints;
                    }
                }
            }
        }
        return selectedGroup == null ? null : new FixedTrackSelection(selectedGroup, selectedTrackIndex);
    }

    private static int comparePixelCounts(int first, int second) {
        return first == -1 ? second == -1 ? 0 : -1 : second == -1 ? 1 : first - second;
    }

    protected TrackSelection selectAudioTrack(TrackGroupArray groups, int[][] formatSupport, String preferredAudioLanguage) {
        TrackGroup selectedGroup = null;
        int selectedTrackIndex = 0;
        int selectedTrackScore = 0;
        for (int groupIndex = 0; groupIndex < groups.length; groupIndex++) {
            TrackGroup trackGroup = groups.get(groupIndex);
            int[] trackFormatSupport = formatSupport[groupIndex];
            for (int trackIndex = 0; trackIndex < trackGroup.length; trackIndex++) {
                if (isSupported(trackFormatSupport[trackIndex])) {
                    int trackScore;
                    Format format = trackGroup.getFormat(trackIndex);
                    boolean isDefault = (format.selectionFlags & 1) != 0;
                    if (formatHasLanguage(format, preferredAudioLanguage)) {
                        if (isDefault) {
                            trackScore = 4;
                        } else {
                            trackScore = 3;
                        }
                    } else if (isDefault) {
                        trackScore = 2;
                    } else {
                        trackScore = 1;
                    }
                    if (trackScore > selectedTrackScore) {
                        selectedGroup = trackGroup;
                        selectedTrackIndex = trackIndex;
                        selectedTrackScore = trackScore;
                    }
                }
            }
        }
        return selectedGroup == null ? null : new FixedTrackSelection(selectedGroup, selectedTrackIndex);
    }

    protected TrackSelection selectTextTrack(TrackGroupArray groups, int[][] formatSupport, String preferredTextLanguage, String preferredAudioLanguage) {
        TrackGroup selectedGroup = null;
        int selectedTrackIndex = 0;
        int selectedTrackScore = 0;
        for (int groupIndex = 0; groupIndex < groups.length; groupIndex++) {
            TrackGroup trackGroup = groups.get(groupIndex);
            int[] trackFormatSupport = formatSupport[groupIndex];
            for (int trackIndex = 0; trackIndex < trackGroup.length; trackIndex++) {
                if (isSupported(trackFormatSupport[trackIndex])) {
                    int trackScore;
                    Format format = trackGroup.getFormat(trackIndex);
                    boolean isDefault = (format.selectionFlags & 1) != 0;
                    boolean isForced = (format.selectionFlags & 2) != 0;
                    if (formatHasLanguage(format, preferredTextLanguage)) {
                        if (isDefault) {
                            trackScore = 6;
                        } else if (isForced) {
                            trackScore = 4;
                        } else {
                            trackScore = 5;
                        }
                    } else if (isDefault) {
                        trackScore = 3;
                    } else if (!isForced) {
                        trackScore = 0;
                    } else if (formatHasLanguage(format, preferredAudioLanguage)) {
                        trackScore = 2;
                    } else {
                        trackScore = 1;
                    }
                    if (trackScore > selectedTrackScore) {
                        selectedGroup = trackGroup;
                        selectedTrackIndex = trackIndex;
                        selectedTrackScore = trackScore;
                    }
                }
            }
        }
        return selectedGroup == null ? null : new FixedTrackSelection(selectedGroup, selectedTrackIndex);
    }

    protected TrackSelection selectOtherTrack(int trackType, TrackGroupArray groups, int[][] formatSupport) {
        TrackGroup selectedGroup = null;
        int selectedTrackIndex = 0;
        int selectedTrackScore = 0;
        for (int groupIndex = 0; groupIndex < groups.length; groupIndex++) {
            TrackGroup trackGroup = groups.get(groupIndex);
            int[] trackFormatSupport = formatSupport[groupIndex];
            for (int trackIndex = 0; trackIndex < trackGroup.length; trackIndex++) {
                if (isSupported(trackFormatSupport[trackIndex])) {
                    int trackScore;
                    if ((trackGroup.getFormat(trackIndex).selectionFlags & 1) != 0) {
                        trackScore = 2;
                    } else {
                        trackScore = 1;
                    }
                    if (trackScore > selectedTrackScore) {
                        selectedGroup = trackGroup;
                        selectedTrackIndex = trackIndex;
                        selectedTrackScore = trackScore;
                    }
                }
            }
        }
        return selectedGroup == null ? null : new FixedTrackSelection(selectedGroup, selectedTrackIndex);
    }

    private static boolean isSupported(int formatSupport) {
        return (formatSupport & 3) == 3;
    }

    private static boolean formatHasLanguage(Format format, String language) {
        return language != null && language.equals(Util.normalizeLanguageCode(format.language));
    }

    private static List<Integer> getViewportFilteredTrackIndices(TrackGroup group, int viewportWidth, int viewportHeight, boolean orientationMayChange) {
        int i;
        ArrayList<Integer> selectedTrackIndices = new ArrayList(group.length);
        for (i = 0; i < group.length; i++) {
            selectedTrackIndices.add(Integer.valueOf(i));
        }
        if (!(viewportWidth == ConnectionsManager.DEFAULT_DATACENTER_ID || viewportHeight == ConnectionsManager.DEFAULT_DATACENTER_ID)) {
            int maxVideoPixelsToRetain = ConnectionsManager.DEFAULT_DATACENTER_ID;
            for (i = 0; i < group.length; i++) {
                Format format = group.getFormat(i);
                if (format.width > 0 && format.height > 0) {
                    Point maxVideoSizeInViewport = getMaxVideoSizeInViewport(orientationMayChange, viewportWidth, viewportHeight, format.width, format.height);
                    int videoPixels = format.width * format.height;
                    if (format.width >= ((int) (((float) maxVideoSizeInViewport.x) * FRACTION_TO_CONSIDER_FULLSCREEN)) && format.height >= ((int) (((float) maxVideoSizeInViewport.y) * FRACTION_TO_CONSIDER_FULLSCREEN)) && videoPixels < maxVideoPixelsToRetain) {
                        maxVideoPixelsToRetain = videoPixels;
                    }
                }
            }
            if (maxVideoPixelsToRetain != ConnectionsManager.DEFAULT_DATACENTER_ID) {
                for (i = selectedTrackIndices.size() - 1; i >= 0; i--) {
                    int pixelCount = group.getFormat(((Integer) selectedTrackIndices.get(i)).intValue()).getPixelCount();
                    if (pixelCount == -1 || pixelCount > maxVideoPixelsToRetain) {
                        selectedTrackIndices.remove(i);
                    }
                }
            }
        }
        return selectedTrackIndices;
    }

    private static Point getMaxVideoSizeInViewport(boolean orientationMayChange, int viewportWidth, int viewportHeight, int videoWidth, int videoHeight) {
        Object obj = 1;
        if (orientationMayChange) {
            Object obj2 = videoWidth > videoHeight ? 1 : null;
            if (viewportWidth <= viewportHeight) {
                obj = null;
            }
            if (obj2 != obj) {
                int tempViewportWidth = viewportWidth;
                viewportWidth = viewportHeight;
                viewportHeight = tempViewportWidth;
            }
        }
        if (videoWidth * viewportHeight >= videoHeight * viewportWidth) {
            return new Point(viewportWidth, Util.ceilDivide(viewportWidth * videoHeight, videoWidth));
        }
        return new Point(Util.ceilDivide(viewportHeight * videoWidth, videoHeight), viewportHeight);
    }
}
