package org.telegram.messenger.exoplayer2.trackselection;

import android.content.Context;
import android.graphics.Point;
import android.text.TextUtils;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.telegram.messenger.exoplayer2.C;
import org.telegram.messenger.exoplayer2.ExoPlaybackException;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.RendererCapabilities;
import org.telegram.messenger.exoplayer2.source.TrackGroup;
import org.telegram.messenger.exoplayer2.source.TrackGroupArray;
import org.telegram.messenger.exoplayer2.trackselection.TrackSelection.Factory;
import org.telegram.messenger.exoplayer2.upstream.BandwidthMeter;
import org.telegram.messenger.exoplayer2.util.Assertions;
import org.telegram.messenger.exoplayer2.util.Util;
import org.telegram.tgnet.ConnectionsManager;

public class DefaultTrackSelector extends MappingTrackSelector {
    private static final float FRACTION_TO_CONSIDER_FULLSCREEN = 0.98f;
    private static final int[] NO_TRACKS = new int[0];
    private static final int WITHIN_RENDERER_CAPABILITIES_BONUS = 1000;
    private final Factory adaptiveTrackSelectionFactory;
    private final AtomicReference<Parameters> paramsReference;

    private static final class AudioConfigurationTuple {
        public final int channelCount;
        public final String mimeType;
        public final int sampleRate;

        public AudioConfigurationTuple(int channelCount, int sampleRate, String mimeType) {
            this.channelCount = channelCount;
            this.sampleRate = sampleRate;
            this.mimeType = mimeType;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            AudioConfigurationTuple other = (AudioConfigurationTuple) obj;
            if (this.channelCount == other.channelCount && this.sampleRate == other.sampleRate && TextUtils.equals(this.mimeType, other.mimeType)) {
                return true;
            }
            return false;
        }

        public int hashCode() {
            return (((this.channelCount * 31) + this.sampleRate) * 31) + (this.mimeType != null ? this.mimeType.hashCode() : 0);
        }
    }

    private static final class AudioTrackScore implements Comparable<AudioTrackScore> {
        private final int bitrate;
        private final int channelCount;
        private final int defaultSelectionFlagScore;
        private final int matchLanguageScore;
        private final Parameters parameters;
        private final int sampleRate;
        private final int withinRendererCapabilitiesScore;

        public AudioTrackScore(Format format, Parameters parameters, int formatSupport) {
            int i;
            int i2 = 1;
            this.parameters = parameters;
            this.withinRendererCapabilitiesScore = DefaultTrackSelector.isSupported(formatSupport, false) ? 1 : 0;
            if (DefaultTrackSelector.formatHasLanguage(format, parameters.preferredAudioLanguage)) {
                i = 1;
            } else {
                i = 0;
            }
            this.matchLanguageScore = i;
            if ((format.selectionFlags & 1) == 0) {
                i2 = 0;
            }
            this.defaultSelectionFlagScore = i2;
            this.channelCount = format.channelCount;
            this.sampleRate = format.sampleRate;
            this.bitrate = format.bitrate;
        }

        public int compareTo(AudioTrackScore other) {
            int resultSign = 1;
            if (this.withinRendererCapabilitiesScore != other.withinRendererCapabilitiesScore) {
                return DefaultTrackSelector.compareInts(this.withinRendererCapabilitiesScore, other.withinRendererCapabilitiesScore);
            }
            if (this.matchLanguageScore != other.matchLanguageScore) {
                return DefaultTrackSelector.compareInts(this.matchLanguageScore, other.matchLanguageScore);
            }
            if (this.defaultSelectionFlagScore != other.defaultSelectionFlagScore) {
                return DefaultTrackSelector.compareInts(this.defaultSelectionFlagScore, other.defaultSelectionFlagScore);
            }
            if (this.parameters.forceLowestBitrate) {
                return DefaultTrackSelector.compareInts(other.bitrate, this.bitrate);
            }
            if (this.withinRendererCapabilitiesScore != 1) {
                resultSign = -1;
            }
            if (this.channelCount != other.channelCount) {
                return DefaultTrackSelector.compareInts(this.channelCount, other.channelCount) * resultSign;
            }
            if (this.sampleRate != other.sampleRate) {
                return DefaultTrackSelector.compareInts(this.sampleRate, other.sampleRate) * resultSign;
            }
            return DefaultTrackSelector.compareInts(this.bitrate, other.bitrate) * resultSign;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            AudioTrackScore that = (AudioTrackScore) o;
            if (this.withinRendererCapabilitiesScore == that.withinRendererCapabilitiesScore && this.matchLanguageScore == that.matchLanguageScore && this.defaultSelectionFlagScore == that.defaultSelectionFlagScore && this.channelCount == that.channelCount && this.sampleRate == that.sampleRate && this.bitrate == that.bitrate) {
                return true;
            }
            return false;
        }

        public int hashCode() {
            return (((((((((this.withinRendererCapabilitiesScore * 31) + this.matchLanguageScore) * 31) + this.defaultSelectionFlagScore) * 31) + this.channelCount) * 31) + this.sampleRate) * 31) + this.bitrate;
        }
    }

    public static final class Parameters {
        public static final Parameters DEFAULT = new Parameters();
        public final boolean allowMixedMimeAdaptiveness;
        public final boolean allowNonSeamlessAdaptiveness;
        public final int disabledTextTrackSelectionFlags;
        public final boolean exceedRendererCapabilitiesIfNecessary;
        public final boolean exceedVideoConstraintsIfNecessary;
        public final boolean forceLowestBitrate;
        public final int maxVideoBitrate;
        public final int maxVideoHeight;
        public final int maxVideoWidth;
        public final String preferredAudioLanguage;
        public final String preferredTextLanguage;
        public final boolean selectUndeterminedTextLanguage;
        public final int viewportHeight;
        public final boolean viewportOrientationMayChange;
        public final int viewportWidth;

        private Parameters() {
            this(null, null, false, 0, false, false, true, ConnectionsManager.DEFAULT_DATACENTER_ID, ConnectionsManager.DEFAULT_DATACENTER_ID, ConnectionsManager.DEFAULT_DATACENTER_ID, true, true, ConnectionsManager.DEFAULT_DATACENTER_ID, ConnectionsManager.DEFAULT_DATACENTER_ID, true);
        }

        private Parameters(String preferredAudioLanguage, String preferredTextLanguage, boolean selectUndeterminedTextLanguage, int disabledTextTrackSelectionFlags, boolean forceLowestBitrate, boolean allowMixedMimeAdaptiveness, boolean allowNonSeamlessAdaptiveness, int maxVideoWidth, int maxVideoHeight, int maxVideoBitrate, boolean exceedVideoConstraintsIfNecessary, boolean exceedRendererCapabilitiesIfNecessary, int viewportWidth, int viewportHeight, boolean viewportOrientationMayChange) {
            this.preferredAudioLanguage = Util.normalizeLanguageCode(preferredAudioLanguage);
            this.preferredTextLanguage = Util.normalizeLanguageCode(preferredTextLanguage);
            this.selectUndeterminedTextLanguage = selectUndeterminedTextLanguage;
            this.disabledTextTrackSelectionFlags = disabledTextTrackSelectionFlags;
            this.forceLowestBitrate = forceLowestBitrate;
            this.allowMixedMimeAdaptiveness = allowMixedMimeAdaptiveness;
            this.allowNonSeamlessAdaptiveness = allowNonSeamlessAdaptiveness;
            this.maxVideoWidth = maxVideoWidth;
            this.maxVideoHeight = maxVideoHeight;
            this.maxVideoBitrate = maxVideoBitrate;
            this.exceedVideoConstraintsIfNecessary = exceedVideoConstraintsIfNecessary;
            this.exceedRendererCapabilitiesIfNecessary = exceedRendererCapabilitiesIfNecessary;
            this.viewportWidth = viewportWidth;
            this.viewportHeight = viewportHeight;
            this.viewportOrientationMayChange = viewportOrientationMayChange;
        }

        public ParametersBuilder buildUpon() {
            return new ParametersBuilder();
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            Parameters other = (Parameters) obj;
            if (this.selectUndeterminedTextLanguage == other.selectUndeterminedTextLanguage && this.disabledTextTrackSelectionFlags == other.disabledTextTrackSelectionFlags && this.forceLowestBitrate == other.forceLowestBitrate && this.allowMixedMimeAdaptiveness == other.allowMixedMimeAdaptiveness && this.allowNonSeamlessAdaptiveness == other.allowNonSeamlessAdaptiveness && this.maxVideoWidth == other.maxVideoWidth && this.maxVideoHeight == other.maxVideoHeight && this.exceedVideoConstraintsIfNecessary == other.exceedVideoConstraintsIfNecessary && this.exceedRendererCapabilitiesIfNecessary == other.exceedRendererCapabilitiesIfNecessary && this.viewportOrientationMayChange == other.viewportOrientationMayChange && this.viewportWidth == other.viewportWidth && this.viewportHeight == other.viewportHeight && this.maxVideoBitrate == other.maxVideoBitrate && TextUtils.equals(this.preferredAudioLanguage, other.preferredAudioLanguage) && TextUtils.equals(this.preferredTextLanguage, other.preferredTextLanguage)) {
                return true;
            }
            return false;
        }

        public int hashCode() {
            int result;
            int i;
            int i2 = 1;
            if (this.selectUndeterminedTextLanguage) {
                result = 1;
            } else {
                result = 0;
            }
            int i3 = ((result * 31) + this.disabledTextTrackSelectionFlags) * 31;
            if (this.forceLowestBitrate) {
                i = 1;
            } else {
                i = 0;
            }
            i3 = (i3 + i) * 31;
            if (this.allowMixedMimeAdaptiveness) {
                i = 1;
            } else {
                i = 0;
            }
            i3 = (i3 + i) * 31;
            if (this.allowNonSeamlessAdaptiveness) {
                i = 1;
            } else {
                i = 0;
            }
            i3 = (((((i3 + i) * 31) + this.maxVideoWidth) * 31) + this.maxVideoHeight) * 31;
            if (this.exceedVideoConstraintsIfNecessary) {
                i = 1;
            } else {
                i = 0;
            }
            i3 = (i3 + i) * 31;
            if (this.exceedRendererCapabilitiesIfNecessary) {
                i = 1;
            } else {
                i = 0;
            }
            i = (i3 + i) * 31;
            if (!this.viewportOrientationMayChange) {
                i2 = 0;
            }
            return ((((((((((i + i2) * 31) + this.viewportWidth) * 31) + this.viewportHeight) * 31) + this.maxVideoBitrate) * 31) + this.preferredAudioLanguage.hashCode()) * 31) + this.preferredTextLanguage.hashCode();
        }
    }

    public static final class ParametersBuilder {
        private boolean allowMixedMimeAdaptiveness;
        private boolean allowNonSeamlessAdaptiveness;
        private int disabledTextTrackSelectionFlags;
        private boolean exceedRendererCapabilitiesIfNecessary;
        private boolean exceedVideoConstraintsIfNecessary;
        private boolean forceLowestBitrate;
        private int maxVideoBitrate;
        private int maxVideoHeight;
        private int maxVideoWidth;
        private String preferredAudioLanguage;
        private String preferredTextLanguage;
        private boolean selectUndeterminedTextLanguage;
        private int viewportHeight;
        private boolean viewportOrientationMayChange;
        private int viewportWidth;

        public ParametersBuilder() {
            this(Parameters.DEFAULT);
        }

        private ParametersBuilder(Parameters initialValues) {
            this.preferredAudioLanguage = initialValues.preferredAudioLanguage;
            this.preferredTextLanguage = initialValues.preferredTextLanguage;
            this.selectUndeterminedTextLanguage = initialValues.selectUndeterminedTextLanguage;
            this.disabledTextTrackSelectionFlags = initialValues.disabledTextTrackSelectionFlags;
            this.forceLowestBitrate = initialValues.forceLowestBitrate;
            this.allowMixedMimeAdaptiveness = initialValues.allowMixedMimeAdaptiveness;
            this.allowNonSeamlessAdaptiveness = initialValues.allowNonSeamlessAdaptiveness;
            this.maxVideoWidth = initialValues.maxVideoWidth;
            this.maxVideoHeight = initialValues.maxVideoHeight;
            this.maxVideoBitrate = initialValues.maxVideoBitrate;
            this.exceedVideoConstraintsIfNecessary = initialValues.exceedVideoConstraintsIfNecessary;
            this.exceedRendererCapabilitiesIfNecessary = initialValues.exceedRendererCapabilitiesIfNecessary;
            this.viewportWidth = initialValues.viewportWidth;
            this.viewportHeight = initialValues.viewportHeight;
            this.viewportOrientationMayChange = initialValues.viewportOrientationMayChange;
        }

        public ParametersBuilder setPreferredAudioLanguage(String preferredAudioLanguage) {
            this.preferredAudioLanguage = preferredAudioLanguage;
            return this;
        }

        public ParametersBuilder setPreferredTextLanguage(String preferredTextLanguage) {
            this.preferredTextLanguage = preferredTextLanguage;
            return this;
        }

        public ParametersBuilder setSelectUndeterminedTextLanguage(boolean selectUndeterminedTextLanguage) {
            this.selectUndeterminedTextLanguage = selectUndeterminedTextLanguage;
            return this;
        }

        public ParametersBuilder setDisabledTextTrackSelectionFlags(int disabledTextTrackSelectionFlags) {
            this.disabledTextTrackSelectionFlags = disabledTextTrackSelectionFlags;
            return this;
        }

        public ParametersBuilder setForceLowestBitrate(boolean forceLowestBitrate) {
            this.forceLowestBitrate = forceLowestBitrate;
            return this;
        }

        public ParametersBuilder setAllowMixedMimeAdaptiveness(boolean allowMixedMimeAdaptiveness) {
            this.allowMixedMimeAdaptiveness = allowMixedMimeAdaptiveness;
            return this;
        }

        public ParametersBuilder setAllowNonSeamlessAdaptiveness(boolean allowNonSeamlessAdaptiveness) {
            this.allowNonSeamlessAdaptiveness = allowNonSeamlessAdaptiveness;
            return this;
        }

        public ParametersBuilder setMaxVideoSizeSd() {
            return setMaxVideoSize(1279, 719);
        }

        public ParametersBuilder clearVideoSizeConstraints() {
            return setMaxVideoSize(ConnectionsManager.DEFAULT_DATACENTER_ID, ConnectionsManager.DEFAULT_DATACENTER_ID);
        }

        public ParametersBuilder setMaxVideoSize(int maxVideoWidth, int maxVideoHeight) {
            this.maxVideoWidth = maxVideoWidth;
            this.maxVideoHeight = maxVideoHeight;
            return this;
        }

        public ParametersBuilder setMaxVideoBitrate(int maxVideoBitrate) {
            this.maxVideoBitrate = maxVideoBitrate;
            return this;
        }

        public ParametersBuilder setExceedVideoConstraintsIfNecessary(boolean exceedVideoConstraintsIfNecessary) {
            this.exceedVideoConstraintsIfNecessary = exceedVideoConstraintsIfNecessary;
            return this;
        }

        public ParametersBuilder setExceedRendererCapabilitiesIfNecessary(boolean exceedRendererCapabilitiesIfNecessary) {
            this.exceedRendererCapabilitiesIfNecessary = exceedRendererCapabilitiesIfNecessary;
            return this;
        }

        public ParametersBuilder setViewportSizeToPhysicalDisplaySize(Context context, boolean viewportOrientationMayChange) {
            Point viewportSize = Util.getPhysicalDisplaySize(context);
            return setViewportSize(viewportSize.x, viewportSize.y, viewportOrientationMayChange);
        }

        public ParametersBuilder clearViewportSizeConstraints() {
            return setViewportSize(ConnectionsManager.DEFAULT_DATACENTER_ID, ConnectionsManager.DEFAULT_DATACENTER_ID, true);
        }

        public ParametersBuilder setViewportSize(int viewportWidth, int viewportHeight, boolean viewportOrientationMayChange) {
            this.viewportWidth = viewportWidth;
            this.viewportHeight = viewportHeight;
            this.viewportOrientationMayChange = viewportOrientationMayChange;
            return this;
        }

        public Parameters build() {
            return new Parameters(this.preferredAudioLanguage, this.preferredTextLanguage, this.selectUndeterminedTextLanguage, this.disabledTextTrackSelectionFlags, this.forceLowestBitrate, this.allowMixedMimeAdaptiveness, this.allowNonSeamlessAdaptiveness, this.maxVideoWidth, this.maxVideoHeight, this.maxVideoBitrate, this.exceedVideoConstraintsIfNecessary, this.exceedRendererCapabilitiesIfNecessary, this.viewportWidth, this.viewportHeight, this.viewportOrientationMayChange);
        }
    }

    public DefaultTrackSelector() {
        this((Factory) null);
    }

    public DefaultTrackSelector(BandwidthMeter bandwidthMeter) {
        this(new AdaptiveTrackSelection.Factory(bandwidthMeter));
    }

    public DefaultTrackSelector(Factory adaptiveTrackSelectionFactory) {
        this.adaptiveTrackSelectionFactory = adaptiveTrackSelectionFactory;
        this.paramsReference = new AtomicReference(Parameters.DEFAULT);
    }

    public void setParameters(Parameters params) {
        Assertions.checkNotNull(params);
        if (!((Parameters) this.paramsReference.getAndSet(params)).equals(params)) {
            invalidate();
        }
    }

    public Parameters getParameters() {
        return (Parameters) this.paramsReference.get();
    }

    protected TrackSelection[] selectTracks(RendererCapabilities[] rendererCapabilities, TrackGroupArray[] rendererTrackGroupArrays, int[][][] rendererFormatSupports) throws ExoPlaybackException {
        int i;
        int rendererCount = rendererCapabilities.length;
        TrackSelection[] rendererTrackSelections = new TrackSelection[rendererCount];
        Parameters params = (Parameters) this.paramsReference.get();
        boolean seenVideoRendererWithMappedTracks = false;
        boolean selectedVideoTracks = false;
        for (i = 0; i < rendererCount; i++) {
            if (2 == rendererCapabilities[i].getTrackType()) {
                int i2;
                if (!selectedVideoTracks) {
                    rendererTrackSelections[i] = selectVideoTrack(rendererCapabilities[i], rendererTrackGroupArrays[i], rendererFormatSupports[i], params, this.adaptiveTrackSelectionFactory);
                    selectedVideoTracks = rendererTrackSelections[i] != null;
                }
                if (rendererTrackGroupArrays[i].length > 0) {
                    i2 = 1;
                } else {
                    i2 = 0;
                }
                seenVideoRendererWithMappedTracks |= i2;
            }
        }
        boolean selectedAudioTracks = false;
        boolean selectedTextTracks = false;
        for (i = 0; i < rendererCount; i++) {
            switch (rendererCapabilities[i].getTrackType()) {
                case 1:
                    if (!selectedAudioTracks) {
                        rendererTrackSelections[i] = selectAudioTrack(rendererTrackGroupArrays[i], rendererFormatSupports[i], params, seenVideoRendererWithMappedTracks ? null : this.adaptiveTrackSelectionFactory);
                        selectedAudioTracks = rendererTrackSelections[i] != null;
                        break;
                    }
                    break;
                case 2:
                    break;
                case 3:
                    if (!selectedTextTracks) {
                        rendererTrackSelections[i] = selectTextTrack(rendererTrackGroupArrays[i], rendererFormatSupports[i], params);
                        selectedTextTracks = rendererTrackSelections[i] != null;
                        break;
                    }
                    break;
                default:
                    rendererTrackSelections[i] = selectOtherTrack(rendererCapabilities[i].getTrackType(), rendererTrackGroupArrays[i], rendererFormatSupports[i], params);
                    break;
            }
        }
        return rendererTrackSelections;
    }

    protected TrackSelection selectVideoTrack(RendererCapabilities rendererCapabilities, TrackGroupArray groups, int[][] formatSupport, Parameters params, Factory adaptiveTrackSelectionFactory) throws ExoPlaybackException {
        TrackSelection selection = null;
        if (!(params.forceLowestBitrate || adaptiveTrackSelectionFactory == null)) {
            selection = selectAdaptiveVideoTrack(rendererCapabilities, groups, formatSupport, params, adaptiveTrackSelectionFactory);
        }
        if (selection == null) {
            return selectFixedVideoTrack(groups, formatSupport, params);
        }
        return selection;
    }

    private static TrackSelection selectAdaptiveVideoTrack(RendererCapabilities rendererCapabilities, TrackGroupArray groups, int[][] formatSupport, Parameters params, Factory adaptiveTrackSelectionFactory) throws ExoPlaybackException {
        int requiredAdaptiveSupport = params.allowNonSeamlessAdaptiveness ? 24 : 16;
        boolean allowMixedMimeTypes = params.allowMixedMimeAdaptiveness && (rendererCapabilities.supportsMixedMimeTypeAdaptation() & requiredAdaptiveSupport) != 0;
        for (int i = 0; i < groups.length; i++) {
            TrackGroup group = groups.get(i);
            int[] adaptiveTracks = getAdaptiveVideoTracksForGroup(group, formatSupport[i], allowMixedMimeTypes, requiredAdaptiveSupport, params.maxVideoWidth, params.maxVideoHeight, params.maxVideoBitrate, params.viewportWidth, params.viewportHeight, params.viewportOrientationMayChange);
            if (adaptiveTracks.length > 0) {
                return adaptiveTrackSelectionFactory.createTrackSelection(group, adaptiveTracks);
            }
        }
        return null;
    }

    private static int[] getAdaptiveVideoTracksForGroup(TrackGroup group, int[] formatSupport, boolean allowMixedMimeTypes, int requiredAdaptiveSupport, int maxVideoWidth, int maxVideoHeight, int maxVideoBitrate, int viewportWidth, int viewportHeight, boolean viewportOrientationMayChange) {
        if (group.length < 2) {
            return NO_TRACKS;
        }
        List<Integer> selectedTrackIndices = getViewportFilteredTrackIndices(group, viewportWidth, viewportHeight, viewportOrientationMayChange);
        if (selectedTrackIndices.size() < 2) {
            return NO_TRACKS;
        }
        String selectedMimeType = null;
        if (!allowMixedMimeTypes) {
            HashSet<String> seenMimeTypes = new HashSet();
            int selectedMimeTypeTrackCount = 0;
            for (int i = 0; i < selectedTrackIndices.size(); i++) {
                String sampleMimeType = group.getFormat(((Integer) selectedTrackIndices.get(i)).intValue()).sampleMimeType;
                if (seenMimeTypes.add(sampleMimeType)) {
                    int countForMimeType = getAdaptiveVideoTrackCountForMimeType(group, formatSupport, requiredAdaptiveSupport, sampleMimeType, maxVideoWidth, maxVideoHeight, maxVideoBitrate, selectedTrackIndices);
                    if (countForMimeType > selectedMimeTypeTrackCount) {
                        selectedMimeType = sampleMimeType;
                        selectedMimeTypeTrackCount = countForMimeType;
                    }
                }
            }
        }
        filterAdaptiveVideoTrackCountForMimeType(group, formatSupport, requiredAdaptiveSupport, selectedMimeType, maxVideoWidth, maxVideoHeight, maxVideoBitrate, selectedTrackIndices);
        return selectedTrackIndices.size() < 2 ? NO_TRACKS : Util.toArray(selectedTrackIndices);
    }

    private static int getAdaptiveVideoTrackCountForMimeType(TrackGroup group, int[] formatSupport, int requiredAdaptiveSupport, String mimeType, int maxVideoWidth, int maxVideoHeight, int maxVideoBitrate, List<Integer> selectedTrackIndices) {
        int adaptiveTrackCount = 0;
        for (int i = 0; i < selectedTrackIndices.size(); i++) {
            int trackIndex = ((Integer) selectedTrackIndices.get(i)).intValue();
            if (isSupportedAdaptiveVideoTrack(group.getFormat(trackIndex), mimeType, formatSupport[trackIndex], requiredAdaptiveSupport, maxVideoWidth, maxVideoHeight, maxVideoBitrate)) {
                adaptiveTrackCount++;
            }
        }
        return adaptiveTrackCount;
    }

    private static void filterAdaptiveVideoTrackCountForMimeType(TrackGroup group, int[] formatSupport, int requiredAdaptiveSupport, String mimeType, int maxVideoWidth, int maxVideoHeight, int maxVideoBitrate, List<Integer> selectedTrackIndices) {
        for (int i = selectedTrackIndices.size() - 1; i >= 0; i--) {
            int trackIndex = ((Integer) selectedTrackIndices.get(i)).intValue();
            if (!isSupportedAdaptiveVideoTrack(group.getFormat(trackIndex), mimeType, formatSupport[trackIndex], requiredAdaptiveSupport, maxVideoWidth, maxVideoHeight, maxVideoBitrate)) {
                selectedTrackIndices.remove(i);
            }
        }
    }

    private static boolean isSupportedAdaptiveVideoTrack(Format format, String mimeType, int formatSupport, int requiredAdaptiveSupport, int maxVideoWidth, int maxVideoHeight, int maxVideoBitrate) {
        if (!isSupported(formatSupport, false) || (formatSupport & requiredAdaptiveSupport) == 0) {
            return false;
        }
        if (mimeType != null && !Util.areEqual(format.sampleMimeType, mimeType)) {
            return false;
        }
        if (format.width != -1 && format.width > maxVideoWidth) {
            return false;
        }
        if (format.height != -1 && format.height > maxVideoHeight) {
            return false;
        }
        if (format.bitrate == -1 || format.bitrate <= maxVideoBitrate) {
            return true;
        }
        return false;
    }

    private static TrackSelection selectFixedVideoTrack(TrackGroupArray groups, int[][] formatSupport, Parameters params) {
        TrackGroup selectedGroup = null;
        int selectedTrackIndex = 0;
        int selectedTrackScore = 0;
        int selectedBitrate = -1;
        int selectedPixelCount = -1;
        for (int groupIndex = 0; groupIndex < groups.length; groupIndex++) {
            TrackGroup trackGroup = groups.get(groupIndex);
            List<Integer> selectedTrackIndices = getViewportFilteredTrackIndices(trackGroup, params.viewportWidth, params.viewportHeight, params.viewportOrientationMayChange);
            int[] trackFormatSupport = formatSupport[groupIndex];
            for (int trackIndex = 0; trackIndex < trackGroup.length; trackIndex++) {
                if (isSupported(trackFormatSupport[trackIndex], params.exceedRendererCapabilitiesIfNecessary)) {
                    Format format = trackGroup.getFormat(trackIndex);
                    boolean isWithinConstraints = selectedTrackIndices.contains(Integer.valueOf(trackIndex)) && ((format.width == -1 || format.width <= params.maxVideoWidth) && ((format.height == -1 || format.height <= params.maxVideoHeight) && (format.bitrate == -1 || format.bitrate <= params.maxVideoBitrate)));
                    if (isWithinConstraints || params.exceedVideoConstraintsIfNecessary) {
                        int trackScore = isWithinConstraints ? 2 : 1;
                        boolean isWithinCapabilities = isSupported(trackFormatSupport[trackIndex], false);
                        if (isWithinCapabilities) {
                            trackScore += WITHIN_RENDERER_CAPABILITIES_BONUS;
                        }
                        boolean selectTrack = trackScore > selectedTrackScore;
                        if (trackScore == selectedTrackScore) {
                            if (!params.forceLowestBitrate) {
                                int comparisonResult;
                                int formatPixelCount = format.getPixelCount();
                                if (formatPixelCount != selectedPixelCount) {
                                    comparisonResult = compareFormatValues(formatPixelCount, selectedPixelCount);
                                } else {
                                    comparisonResult = compareFormatValues(format.bitrate, selectedBitrate);
                                }
                                selectTrack = (isWithinCapabilities && isWithinConstraints) ? comparisonResult > 0 : comparisonResult < 0;
                            } else if (compareFormatValues(format.bitrate, selectedBitrate) < 0) {
                                selectTrack = true;
                            } else {
                                selectTrack = false;
                            }
                        }
                        if (selectTrack) {
                            selectedGroup = trackGroup;
                            selectedTrackIndex = trackIndex;
                            selectedTrackScore = trackScore;
                            selectedBitrate = format.bitrate;
                            selectedPixelCount = format.getPixelCount();
                        }
                    }
                }
            }
        }
        return selectedGroup == null ? null : new FixedTrackSelection(selectedGroup, selectedTrackIndex);
    }

    private static int compareFormatValues(int first, int second) {
        return first == -1 ? second == -1 ? 0 : -1 : second == -1 ? 1 : first - second;
    }

    protected TrackSelection selectAudioTrack(TrackGroupArray groups, int[][] formatSupport, Parameters params, Factory adaptiveTrackSelectionFactory) throws ExoPlaybackException {
        int selectedTrackIndex = -1;
        int selectedGroupIndex = -1;
        AudioTrackScore selectedTrackScore = null;
        for (int groupIndex = 0; groupIndex < groups.length; groupIndex++) {
            TrackGroup trackGroup = groups.get(groupIndex);
            int[] trackFormatSupport = formatSupport[groupIndex];
            for (int trackIndex = 0; trackIndex < trackGroup.length; trackIndex++) {
                if (isSupported(trackFormatSupport[trackIndex], params.exceedRendererCapabilitiesIfNecessary)) {
                    AudioTrackScore trackScore = new AudioTrackScore(trackGroup.getFormat(trackIndex), params, trackFormatSupport[trackIndex]);
                    if (selectedTrackScore == null || trackScore.compareTo(selectedTrackScore) > 0) {
                        selectedGroupIndex = groupIndex;
                        selectedTrackIndex = trackIndex;
                        selectedTrackScore = trackScore;
                    }
                }
            }
        }
        if (selectedGroupIndex == -1) {
            return null;
        }
        TrackGroup selectedGroup = groups.get(selectedGroupIndex);
        if (!(params.forceLowestBitrate || adaptiveTrackSelectionFactory == null)) {
            int[] adaptiveTracks = getAdaptiveAudioTracks(selectedGroup, formatSupport[selectedGroupIndex], params.allowMixedMimeAdaptiveness);
            if (adaptiveTracks.length > 0) {
                return adaptiveTrackSelectionFactory.createTrackSelection(selectedGroup, adaptiveTracks);
            }
        }
        return new FixedTrackSelection(selectedGroup, selectedTrackIndex);
    }

    private static int[] getAdaptiveAudioTracks(TrackGroup group, int[] formatSupport, boolean allowMixedMimeTypes) {
        int i;
        int selectedConfigurationTrackCount = 0;
        AudioConfigurationTuple selectedConfiguration = null;
        HashSet<AudioConfigurationTuple> seenConfigurationTuples = new HashSet();
        for (i = 0; i < group.length; i++) {
            Format format = group.getFormat(i);
            AudioConfigurationTuple configuration = new AudioConfigurationTuple(format.channelCount, format.sampleRate, allowMixedMimeTypes ? null : format.sampleMimeType);
            if (seenConfigurationTuples.add(configuration)) {
                int configurationCount = getAdaptiveAudioTrackCount(group, formatSupport, configuration);
                if (configurationCount > selectedConfigurationTrackCount) {
                    selectedConfiguration = configuration;
                    selectedConfigurationTrackCount = configurationCount;
                }
            }
        }
        if (selectedConfigurationTrackCount <= 1) {
            return NO_TRACKS;
        }
        int[] iArr = new int[selectedConfigurationTrackCount];
        int index = 0;
        for (i = 0; i < group.length; i++) {
            if (isSupportedAdaptiveAudioTrack(group.getFormat(i), formatSupport[i], selectedConfiguration)) {
                int index2 = index + 1;
                iArr[index] = i;
                index = index2;
            }
        }
        return iArr;
    }

    private static int getAdaptiveAudioTrackCount(TrackGroup group, int[] formatSupport, AudioConfigurationTuple configuration) {
        int count = 0;
        for (int i = 0; i < group.length; i++) {
            if (isSupportedAdaptiveAudioTrack(group.getFormat(i), formatSupport[i], configuration)) {
                count++;
            }
        }
        return count;
    }

    private static boolean isSupportedAdaptiveAudioTrack(Format format, int formatSupport, AudioConfigurationTuple configuration) {
        if (!isSupported(formatSupport, false) || format.channelCount != configuration.channelCount || format.sampleRate != configuration.sampleRate) {
            return false;
        }
        if (configuration.mimeType == null || TextUtils.equals(configuration.mimeType, format.sampleMimeType)) {
            return true;
        }
        return false;
    }

    protected TrackSelection selectTextTrack(TrackGroupArray groups, int[][] formatSupport, Parameters params) throws ExoPlaybackException {
        TrackGroup selectedGroup = null;
        int selectedTrackIndex = 0;
        int selectedTrackScore = 0;
        for (int groupIndex = 0; groupIndex < groups.length; groupIndex++) {
            TrackGroup trackGroup = groups.get(groupIndex);
            int[] trackFormatSupport = formatSupport[groupIndex];
            for (int trackIndex = 0; trackIndex < trackGroup.length; trackIndex++) {
                if (isSupported(trackFormatSupport[trackIndex], params.exceedRendererCapabilitiesIfNecessary)) {
                    int trackScore;
                    Format format = trackGroup.getFormat(trackIndex);
                    int maskedSelectionFlags = format.selectionFlags & (params.disabledTextTrackSelectionFlags ^ -1);
                    boolean isDefault = (maskedSelectionFlags & 1) != 0;
                    boolean isForced = (maskedSelectionFlags & 2) != 0;
                    boolean preferredLanguageFound = formatHasLanguage(format, params.preferredTextLanguage);
                    if (preferredLanguageFound || (params.selectUndeterminedTextLanguage && formatHasNoLanguage(format))) {
                        int i;
                        if (isDefault) {
                            trackScore = 8;
                        } else if (isForced) {
                            trackScore = 4;
                        } else {
                            trackScore = 6;
                        }
                        if (preferredLanguageFound) {
                            i = 1;
                        } else {
                            i = 0;
                        }
                        trackScore += i;
                    } else if (isDefault) {
                        trackScore = 3;
                    } else if (isForced) {
                        if (formatHasLanguage(format, params.preferredAudioLanguage)) {
                            trackScore = 2;
                        } else {
                            trackScore = 1;
                        }
                    }
                    if (isSupported(trackFormatSupport[trackIndex], false)) {
                        trackScore += WITHIN_RENDERER_CAPABILITIES_BONUS;
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

    protected TrackSelection selectOtherTrack(int trackType, TrackGroupArray groups, int[][] formatSupport, Parameters params) throws ExoPlaybackException {
        TrackGroup selectedGroup = null;
        int selectedTrackIndex = 0;
        int selectedTrackScore = 0;
        for (int groupIndex = 0; groupIndex < groups.length; groupIndex++) {
            TrackGroup trackGroup = groups.get(groupIndex);
            int[] trackFormatSupport = formatSupport[groupIndex];
            for (int trackIndex = 0; trackIndex < trackGroup.length; trackIndex++) {
                if (isSupported(trackFormatSupport[trackIndex], params.exceedRendererCapabilitiesIfNecessary)) {
                    int trackScore = (trackGroup.getFormat(trackIndex).selectionFlags & 1) != 0 ? 2 : 1;
                    if (isSupported(trackFormatSupport[trackIndex], false)) {
                        trackScore += WITHIN_RENDERER_CAPABILITIES_BONUS;
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

    protected static boolean isSupported(int formatSupport, boolean allowExceedsCapabilities) {
        int maskedSupport = formatSupport & 7;
        return maskedSupport == 4 || (allowExceedsCapabilities && maskedSupport == 3);
    }

    protected static boolean formatHasNoLanguage(Format format) {
        return TextUtils.isEmpty(format.language) || formatHasLanguage(format, C.LANGUAGE_UNDETERMINED);
    }

    protected static boolean formatHasLanguage(Format format, String language) {
        return language != null && TextUtils.equals(language, Util.normalizeLanguageCode(format.language));
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

    private static int compareInts(int first, int second) {
        if (first > second) {
            return 1;
        }
        return second > first ? -1 : 0;
    }
}
