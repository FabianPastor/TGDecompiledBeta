package org.telegram.messenger.exoplayer2.trackselection;

import android.content.Context;
import android.graphics.Point;
import android.text.TextUtils;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.telegram.messenger.exoplayer2.C0542C;
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

        public AudioConfigurationTuple(int i, int i2, String str) {
            this.channelCount = i;
            this.sampleRate = i2;
            this.mimeType = str;
        }

        public boolean equals(Object obj) {
            boolean z = true;
            if (this == obj) {
                return true;
            }
            if (obj != null) {
                if (getClass() == obj.getClass()) {
                    AudioConfigurationTuple audioConfigurationTuple = (AudioConfigurationTuple) obj;
                    if (this.channelCount != audioConfigurationTuple.channelCount || this.sampleRate != audioConfigurationTuple.sampleRate || TextUtils.equals(this.mimeType, audioConfigurationTuple.mimeType) == null) {
                        z = false;
                    }
                    return z;
                }
            }
            return false;
        }

        public int hashCode() {
            return (31 * ((this.channelCount * 31) + this.sampleRate)) + (this.mimeType != null ? this.mimeType.hashCode() : 0);
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

        public AudioTrackScore(Format format, Parameters parameters, int i) {
            this.parameters = parameters;
            this.withinRendererCapabilitiesScore = DefaultTrackSelector.isSupported(i, false);
            this.matchLanguageScore = DefaultTrackSelector.formatHasLanguage(format, parameters.preferredAudioLanguage);
            i = 1;
            if ((format.selectionFlags & 1) == null) {
                i = 0;
            }
            this.defaultSelectionFlagScore = i;
            this.channelCount = format.channelCount;
            this.sampleRate = format.sampleRate;
            this.bitrate = format.bitrate;
        }

        public int compareTo(AudioTrackScore audioTrackScore) {
            if (this.withinRendererCapabilitiesScore != audioTrackScore.withinRendererCapabilitiesScore) {
                return DefaultTrackSelector.compareInts(this.withinRendererCapabilitiesScore, audioTrackScore.withinRendererCapabilitiesScore);
            }
            if (this.matchLanguageScore != audioTrackScore.matchLanguageScore) {
                return DefaultTrackSelector.compareInts(this.matchLanguageScore, audioTrackScore.matchLanguageScore);
            }
            if (this.defaultSelectionFlagScore != audioTrackScore.defaultSelectionFlagScore) {
                return DefaultTrackSelector.compareInts(this.defaultSelectionFlagScore, audioTrackScore.defaultSelectionFlagScore);
            }
            if (this.parameters.forceLowestBitrate) {
                return DefaultTrackSelector.compareInts(audioTrackScore.bitrate, this.bitrate);
            }
            int i = 1;
            if (this.withinRendererCapabilitiesScore != 1) {
                i = -1;
            }
            if (this.channelCount != audioTrackScore.channelCount) {
                return i * DefaultTrackSelector.compareInts(this.channelCount, audioTrackScore.channelCount);
            }
            if (this.sampleRate != audioTrackScore.sampleRate) {
                return i * DefaultTrackSelector.compareInts(this.sampleRate, audioTrackScore.sampleRate);
            }
            return i * DefaultTrackSelector.compareInts(this.bitrate, audioTrackScore.bitrate);
        }

        public boolean equals(Object obj) {
            boolean z = true;
            if (this == obj) {
                return true;
            }
            if (obj != null) {
                if (getClass() == obj.getClass()) {
                    AudioTrackScore audioTrackScore = (AudioTrackScore) obj;
                    if (this.withinRendererCapabilitiesScore != audioTrackScore.withinRendererCapabilitiesScore || this.matchLanguageScore != audioTrackScore.matchLanguageScore || this.defaultSelectionFlagScore != audioTrackScore.defaultSelectionFlagScore || this.channelCount != audioTrackScore.channelCount || this.sampleRate != audioTrackScore.sampleRate || this.bitrate != audioTrackScore.bitrate) {
                        z = false;
                    }
                    return z;
                }
            }
            return false;
        }

        public int hashCode() {
            return (31 * ((((((((this.withinRendererCapabilitiesScore * 31) + this.matchLanguageScore) * 31) + this.defaultSelectionFlagScore) * 31) + this.channelCount) * 31) + this.sampleRate)) + this.bitrate;
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

        private Parameters(String str, String str2, boolean z, int i, boolean z2, boolean z3, boolean z4, int i2, int i3, int i4, boolean z5, boolean z6, int i5, int i6, boolean z7) {
            this.preferredAudioLanguage = Util.normalizeLanguageCode(str);
            this.preferredTextLanguage = Util.normalizeLanguageCode(str2);
            this.selectUndeterminedTextLanguage = z;
            this.disabledTextTrackSelectionFlags = i;
            this.forceLowestBitrate = z2;
            this.allowMixedMimeAdaptiveness = z3;
            this.allowNonSeamlessAdaptiveness = z4;
            this.maxVideoWidth = i2;
            this.maxVideoHeight = i3;
            this.maxVideoBitrate = i4;
            this.exceedVideoConstraintsIfNecessary = z5;
            this.exceedRendererCapabilitiesIfNecessary = z6;
            this.viewportWidth = i5;
            this.viewportHeight = i6;
            this.viewportOrientationMayChange = z7;
        }

        public ParametersBuilder buildUpon() {
            return new ParametersBuilder();
        }

        public boolean equals(Object obj) {
            boolean z = true;
            if (this == obj) {
                return true;
            }
            if (obj != null) {
                if (getClass() == obj.getClass()) {
                    Parameters parameters = (Parameters) obj;
                    if (this.selectUndeterminedTextLanguage != parameters.selectUndeterminedTextLanguage || this.disabledTextTrackSelectionFlags != parameters.disabledTextTrackSelectionFlags || this.forceLowestBitrate != parameters.forceLowestBitrate || this.allowMixedMimeAdaptiveness != parameters.allowMixedMimeAdaptiveness || this.allowNonSeamlessAdaptiveness != parameters.allowNonSeamlessAdaptiveness || this.maxVideoWidth != parameters.maxVideoWidth || this.maxVideoHeight != parameters.maxVideoHeight || this.exceedVideoConstraintsIfNecessary != parameters.exceedVideoConstraintsIfNecessary || this.exceedRendererCapabilitiesIfNecessary != parameters.exceedRendererCapabilitiesIfNecessary || this.viewportOrientationMayChange != parameters.viewportOrientationMayChange || this.viewportWidth != parameters.viewportWidth || this.viewportHeight != parameters.viewportHeight || this.maxVideoBitrate != parameters.maxVideoBitrate || !TextUtils.equals(this.preferredAudioLanguage, parameters.preferredAudioLanguage) || TextUtils.equals(this.preferredTextLanguage, parameters.preferredTextLanguage) == null) {
                        z = false;
                    }
                    return z;
                }
            }
            return false;
        }

        public int hashCode() {
            return (31 * ((((((((((((((((((((((((((this.selectUndeterminedTextLanguage * 31) + this.disabledTextTrackSelectionFlags) * 31) + this.forceLowestBitrate) * 31) + this.allowMixedMimeAdaptiveness) * 31) + this.allowNonSeamlessAdaptiveness) * 31) + this.maxVideoWidth) * 31) + this.maxVideoHeight) * 31) + this.exceedVideoConstraintsIfNecessary) * 31) + this.exceedRendererCapabilitiesIfNecessary) * 31) + this.viewportOrientationMayChange) * 31) + this.viewportWidth) * 31) + this.viewportHeight) * 31) + this.maxVideoBitrate) * 31) + this.preferredAudioLanguage.hashCode())) + this.preferredTextLanguage.hashCode();
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

        private ParametersBuilder(Parameters parameters) {
            this.preferredAudioLanguage = parameters.preferredAudioLanguage;
            this.preferredTextLanguage = parameters.preferredTextLanguage;
            this.selectUndeterminedTextLanguage = parameters.selectUndeterminedTextLanguage;
            this.disabledTextTrackSelectionFlags = parameters.disabledTextTrackSelectionFlags;
            this.forceLowestBitrate = parameters.forceLowestBitrate;
            this.allowMixedMimeAdaptiveness = parameters.allowMixedMimeAdaptiveness;
            this.allowNonSeamlessAdaptiveness = parameters.allowNonSeamlessAdaptiveness;
            this.maxVideoWidth = parameters.maxVideoWidth;
            this.maxVideoHeight = parameters.maxVideoHeight;
            this.maxVideoBitrate = parameters.maxVideoBitrate;
            this.exceedVideoConstraintsIfNecessary = parameters.exceedVideoConstraintsIfNecessary;
            this.exceedRendererCapabilitiesIfNecessary = parameters.exceedRendererCapabilitiesIfNecessary;
            this.viewportWidth = parameters.viewportWidth;
            this.viewportHeight = parameters.viewportHeight;
            this.viewportOrientationMayChange = parameters.viewportOrientationMayChange;
        }

        public ParametersBuilder setPreferredAudioLanguage(String str) {
            this.preferredAudioLanguage = str;
            return this;
        }

        public ParametersBuilder setPreferredTextLanguage(String str) {
            this.preferredTextLanguage = str;
            return this;
        }

        public ParametersBuilder setSelectUndeterminedTextLanguage(boolean z) {
            this.selectUndeterminedTextLanguage = z;
            return this;
        }

        public ParametersBuilder setDisabledTextTrackSelectionFlags(int i) {
            this.disabledTextTrackSelectionFlags = i;
            return this;
        }

        public ParametersBuilder setForceLowestBitrate(boolean z) {
            this.forceLowestBitrate = z;
            return this;
        }

        public ParametersBuilder setAllowMixedMimeAdaptiveness(boolean z) {
            this.allowMixedMimeAdaptiveness = z;
            return this;
        }

        public ParametersBuilder setAllowNonSeamlessAdaptiveness(boolean z) {
            this.allowNonSeamlessAdaptiveness = z;
            return this;
        }

        public ParametersBuilder setMaxVideoSizeSd() {
            return setMaxVideoSize(1279, 719);
        }

        public ParametersBuilder clearVideoSizeConstraints() {
            return setMaxVideoSize(ConnectionsManager.DEFAULT_DATACENTER_ID, ConnectionsManager.DEFAULT_DATACENTER_ID);
        }

        public ParametersBuilder setMaxVideoSize(int i, int i2) {
            this.maxVideoWidth = i;
            this.maxVideoHeight = i2;
            return this;
        }

        public ParametersBuilder setMaxVideoBitrate(int i) {
            this.maxVideoBitrate = i;
            return this;
        }

        public ParametersBuilder setExceedVideoConstraintsIfNecessary(boolean z) {
            this.exceedVideoConstraintsIfNecessary = z;
            return this;
        }

        public ParametersBuilder setExceedRendererCapabilitiesIfNecessary(boolean z) {
            this.exceedRendererCapabilitiesIfNecessary = z;
            return this;
        }

        public ParametersBuilder setViewportSizeToPhysicalDisplaySize(Context context, boolean z) {
            context = Util.getPhysicalDisplaySize(context);
            return setViewportSize(context.x, context.y, z);
        }

        public ParametersBuilder clearViewportSizeConstraints() {
            return setViewportSize(ConnectionsManager.DEFAULT_DATACENTER_ID, ConnectionsManager.DEFAULT_DATACENTER_ID, true);
        }

        public ParametersBuilder setViewportSize(int i, int i2, boolean z) {
            this.viewportWidth = i;
            this.viewportHeight = i2;
            this.viewportOrientationMayChange = z;
            return this;
        }

        public Parameters build() {
            return new Parameters(this.preferredAudioLanguage, this.preferredTextLanguage, this.selectUndeterminedTextLanguage, this.disabledTextTrackSelectionFlags, this.forceLowestBitrate, this.allowMixedMimeAdaptiveness, this.allowNonSeamlessAdaptiveness, this.maxVideoWidth, this.maxVideoHeight, this.maxVideoBitrate, this.exceedVideoConstraintsIfNecessary, this.exceedRendererCapabilitiesIfNecessary, this.viewportWidth, this.viewportHeight, this.viewportOrientationMayChange);
        }
    }

    private static int compareFormatValues(int i, int i2) {
        return i == -1 ? i2 == -1 ? 0 : -1 : i2 == -1 ? 1 : i - i2;
    }

    private static int compareInts(int i, int i2) {
        return i > i2 ? 1 : i2 > i ? -1 : 0;
    }

    protected static boolean isSupported(int i, boolean z) {
        i &= 7;
        if (i != 4) {
            if (!z || i != 3) {
                return false;
            }
        }
        return true;
    }

    public DefaultTrackSelector() {
        this((Factory) null);
    }

    public DefaultTrackSelector(BandwidthMeter bandwidthMeter) {
        this(new AdaptiveTrackSelection.Factory(bandwidthMeter));
    }

    public DefaultTrackSelector(Factory factory) {
        this.adaptiveTrackSelectionFactory = factory;
        this.paramsReference = new AtomicReference(Parameters.DEFAULT);
    }

    public void setParameters(Parameters parameters) {
        Assertions.checkNotNull(parameters);
        if (((Parameters) this.paramsReference.getAndSet(parameters)).equals(parameters) == null) {
            invalidate();
        }
    }

    public Parameters getParameters() {
        return (Parameters) this.paramsReference.get();
    }

    protected TrackSelection[] selectTracks(RendererCapabilities[] rendererCapabilitiesArr, TrackGroupArray[] trackGroupArrayArr, int[][][] iArr) throws ExoPlaybackException {
        RendererCapabilities[] rendererCapabilitiesArr2 = rendererCapabilitiesArr;
        int length = rendererCapabilitiesArr2.length;
        TrackSelection[] trackSelectionArr = new TrackSelection[length];
        Parameters parameters = (Parameters) this.paramsReference.get();
        int i = 0;
        int i2 = i;
        int i3 = i2;
        while (true) {
            int i4 = 1;
            if (i2 >= length) {
                break;
            }
            if (2 == rendererCapabilitiesArr2[i2].getTrackType()) {
                if (i == 0) {
                    trackSelectionArr[i2] = selectVideoTrack(rendererCapabilitiesArr2[i2], trackGroupArrayArr[i2], iArr[i2], parameters, r6.adaptiveTrackSelectionFactory);
                    i = trackSelectionArr[i2] != null ? 1 : 0;
                }
                if (trackGroupArrayArr[i2].length <= 0) {
                    i4 = 0;
                }
                i3 |= i4;
            }
            i2++;
        }
        i = 0;
        int i5 = i;
        int i6 = i5;
        while (i < length) {
            switch (rendererCapabilitiesArr2[i].getTrackType()) {
                case 1:
                    if (i5 != 0) {
                        break;
                    }
                    trackSelectionArr[i] = selectAudioTrack(trackGroupArrayArr[i], iArr[i], parameters, i3 != 0 ? null : r6.adaptiveTrackSelectionFactory);
                    if (trackSelectionArr[i] == null) {
                        i5 = 0;
                        break;
                    }
                    i5 = 1;
                    break;
                case 2:
                    break;
                case 3:
                    if (i6 != 0) {
                        break;
                    }
                    trackSelectionArr[i] = selectTextTrack(trackGroupArrayArr[i], iArr[i], parameters);
                    if (trackSelectionArr[i] == null) {
                        i6 = 0;
                        break;
                    }
                    i6 = 1;
                    break;
                default:
                    trackSelectionArr[i] = selectOtherTrack(rendererCapabilitiesArr2[i].getTrackType(), trackGroupArrayArr[i], iArr[i], parameters);
                    break;
            }
            i++;
        }
        return trackSelectionArr;
    }

    protected TrackSelection selectVideoTrack(RendererCapabilities rendererCapabilities, TrackGroupArray trackGroupArray, int[][] iArr, Parameters parameters, Factory factory) throws ExoPlaybackException {
        rendererCapabilities = (parameters.forceLowestBitrate || factory == null) ? null : selectAdaptiveVideoTrack(rendererCapabilities, trackGroupArray, iArr, parameters, factory);
        return rendererCapabilities == null ? selectFixedVideoTrack(trackGroupArray, iArr, parameters) : rendererCapabilities;
    }

    private static TrackSelection selectAdaptiveVideoTrack(RendererCapabilities rendererCapabilities, TrackGroupArray trackGroupArray, int[][] iArr, Parameters parameters, Factory factory) throws ExoPlaybackException {
        TrackGroupArray trackGroupArray2 = trackGroupArray;
        Parameters parameters2 = parameters;
        int i = parameters2.allowNonSeamlessAdaptiveness ? 24 : 16;
        boolean z = parameters2.allowMixedMimeAdaptiveness && (rendererCapabilities.supportsMixedMimeTypeAdaptation() & i) != 0;
        for (int i2 = 0; i2 < trackGroupArray2.length; i2++) {
            TrackGroup trackGroup = trackGroupArray2.get(i2);
            int[] adaptiveVideoTracksForGroup = getAdaptiveVideoTracksForGroup(trackGroup, iArr[i2], z, i, parameters2.maxVideoWidth, parameters2.maxVideoHeight, parameters2.maxVideoBitrate, parameters2.viewportWidth, parameters2.viewportHeight, parameters2.viewportOrientationMayChange);
            if (adaptiveVideoTracksForGroup.length > 0) {
                return factory.createTrackSelection(trackGroup, adaptiveVideoTracksForGroup);
            }
            Factory factory2 = factory;
        }
        return null;
    }

    private static int[] getAdaptiveVideoTracksForGroup(TrackGroup trackGroup, int[] iArr, boolean z, int i, int i2, int i3, int i4, int i5, int i6, boolean z2) {
        TrackGroup trackGroup2 = trackGroup;
        if (trackGroup2.length < 2) {
            return NO_TRACKS;
        }
        List viewportFilteredTrackIndices = getViewportFilteredTrackIndices(trackGroup2, i5, i6, z2);
        if (viewportFilteredTrackIndices.size() < 2) {
            return NO_TRACKS;
        }
        String str;
        if (z) {
            str = null;
        } else {
            HashSet hashSet = new HashSet();
            String str2 = null;
            int i7 = 0;
            int i8 = i7;
            while (i7 < viewportFilteredTrackIndices.size()) {
                String str3 = trackGroup2.getFormat(((Integer) viewportFilteredTrackIndices.get(i7)).intValue()).sampleMimeType;
                if (hashSet.add(str3)) {
                    int adaptiveVideoTrackCountForMimeType = getAdaptiveVideoTrackCountForMimeType(trackGroup2, iArr, i, str3, i2, i3, i4, viewportFilteredTrackIndices);
                    if (adaptiveVideoTrackCountForMimeType > i8) {
                        i8 = adaptiveVideoTrackCountForMimeType;
                        str2 = str3;
                    }
                }
                i7++;
            }
            str = str2;
        }
        filterAdaptiveVideoTrackCountForMimeType(trackGroup2, iArr, i, str, i2, i3, i4, viewportFilteredTrackIndices);
        return viewportFilteredTrackIndices.size() < 2 ? NO_TRACKS : Util.toArray(viewportFilteredTrackIndices);
    }

    private static int getAdaptiveVideoTrackCountForMimeType(TrackGroup trackGroup, int[] iArr, int i, String str, int i2, int i3, int i4, List<Integer> list) {
        int i5 = 0;
        int i6 = 0;
        while (i5 < list.size()) {
            int intValue = ((Integer) list.get(i5)).intValue();
            if (isSupportedAdaptiveVideoTrack(trackGroup.getFormat(intValue), str, iArr[intValue], i, i2, i3, i4)) {
                i6++;
            }
            i5++;
        }
        return i6;
    }

    private static void filterAdaptiveVideoTrackCountForMimeType(TrackGroup trackGroup, int[] iArr, int i, String str, int i2, int i3, int i4, List<Integer> list) {
        List<Integer> list2 = list;
        for (int size = list.size() - 1; size >= 0; size--) {
            int intValue = ((Integer) list2.get(size)).intValue();
            if (!isSupportedAdaptiveVideoTrack(trackGroup.getFormat(intValue), str, iArr[intValue], i, i2, i3, i4)) {
                list2.remove(size);
            }
        }
    }

    private static boolean isSupportedAdaptiveVideoTrack(Format format, String str, int i, int i2, int i3, int i4, int i5) {
        if (!isSupported(i, false) || (i & i2) == 0) {
            return false;
        }
        if (str != null && Util.areEqual(format.sampleMimeType, str) == null) {
            return false;
        }
        if (format.width != -1 && format.width > i3) {
            return false;
        }
        if (format.height != -1 && format.height > i4) {
            return false;
        }
        if (format.bitrate == -1 || format.bitrate <= i5) {
            return true;
        }
        return false;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static TrackSelection selectFixedVideoTrack(TrackGroupArray trackGroupArray, int[][] iArr, Parameters parameters) {
        TrackGroupArray trackGroupArray2 = trackGroupArray;
        Parameters parameters2 = parameters;
        int i = -1;
        int i2 = -1;
        int i3 = i2;
        int i4 = 0;
        TrackGroup trackGroup = null;
        int i5 = 0;
        int i6 = 0;
        while (i4 < trackGroupArray2.length) {
            int i7;
            TrackGroup trackGroup2 = trackGroupArray2.get(i4);
            List viewportFilteredTrackIndices = getViewportFilteredTrackIndices(trackGroup2, parameters2.viewportWidth, parameters2.viewportHeight, parameters2.viewportOrientationMayChange);
            int[] iArr2 = iArr[i4];
            int i8 = i3;
            i3 = i2;
            i2 = i6;
            i6 = i5;
            TrackGroup trackGroup3 = trackGroup;
            int i9 = 0;
            while (i9 < trackGroup2.length) {
                if (isSupported(iArr2[i9], parameters2.exceedRendererCapabilitiesIfNecessary)) {
                    Format format = trackGroup2.getFormat(i9);
                    Object obj = (!viewportFilteredTrackIndices.contains(Integer.valueOf(i9)) || ((format.width != i && format.width > parameters2.maxVideoWidth) || ((format.height != -1 && format.height > parameters2.maxVideoHeight) || (format.bitrate != -1 && format.bitrate > parameters2.maxVideoBitrate)))) ? null : 1;
                    if (obj != null || parameters2.exceedVideoConstraintsIfNecessary) {
                        int i10 = obj != null ? 2 : 1;
                        i7 = i6;
                        boolean isSupported = isSupported(iArr2[i9], false);
                        if (isSupported) {
                            i10 += WITHIN_RENDERER_CAPABILITIES_BONUS;
                        }
                        boolean z = i10 > i2;
                        if (i10 == i2) {
                            if (!parameters2.forceLowestBitrate) {
                                i6 = format.getPixelCount();
                                if (i6 != i8) {
                                    i6 = compareFormatValues(i6, i8);
                                } else {
                                    i6 = compareFormatValues(format.bitrate, i3);
                                }
                                if (!isSupported || obj == null) {
                                    if (i6 < 0) {
                                    }
                                    z = false;
                                } else {
                                    if (i6 > 0) {
                                    }
                                    z = false;
                                }
                            }
                            z = true;
                        }
                        if (z) {
                            i3 = format.bitrate;
                            i8 = format.getPixelCount();
                            i2 = i10;
                            i6 = i9;
                            trackGroup3 = trackGroup2;
                            i9++;
                            trackGroupArray2 = trackGroupArray;
                            i = -1;
                        }
                        i6 = i7;
                        i9++;
                        trackGroupArray2 = trackGroupArray;
                        i = -1;
                    }
                }
                i7 = i6;
                i6 = i7;
                i9++;
                trackGroupArray2 = trackGroupArray;
                i = -1;
            }
            i7 = i6;
            i4++;
            trackGroup = trackGroup3;
            i6 = i2;
            i2 = i3;
            i3 = i8;
            i5 = i7;
            trackGroupArray2 = trackGroupArray;
            i = -1;
        }
        if (trackGroup == null) {
            return null;
        }
        return new FixedTrackSelection(trackGroup, i5);
    }

    protected TrackSelection selectAudioTrack(TrackGroupArray trackGroupArray, int[][] iArr, Parameters parameters, Factory factory) throws ExoPlaybackException {
        TrackGroupArray trackGroupArray2 = trackGroupArray;
        Parameters parameters2 = parameters;
        Factory factory2 = factory;
        TrackSelection trackSelection = null;
        int i = -1;
        int i2 = i;
        int i3 = 0;
        while (i3 < trackGroupArray2.length) {
            TrackGroup trackGroup = trackGroupArray2.get(i3);
            int[] iArr2 = iArr[i3];
            int i4 = i2;
            AudioTrackScore audioTrackScore = trackSelection;
            int i5 = i;
            for (i = 0; i < trackGroup.length; i++) {
                if (isSupported(iArr2[i], parameters2.exceedRendererCapabilitiesIfNecessary)) {
                    AudioTrackScore audioTrackScore2 = new AudioTrackScore(trackGroup.getFormat(i), parameters2, iArr2[i]);
                    if (audioTrackScore == null || audioTrackScore2.compareTo(audioTrackScore) > 0) {
                        i5 = i3;
                        i4 = i;
                        audioTrackScore = audioTrackScore2;
                    }
                }
            }
            i3++;
            i = i5;
            Object obj = audioTrackScore;
            i2 = i4;
        }
        if (i == -1) {
            return null;
        }
        TrackGroup trackGroup2 = trackGroupArray2.get(i);
        if (!(parameters2.forceLowestBitrate || factory2 == null)) {
            int[] adaptiveAudioTracks = getAdaptiveAudioTracks(trackGroup2, iArr[i], parameters2.allowMixedMimeAdaptiveness);
            if (adaptiveAudioTracks.length > 0) {
                return factory2.createTrackSelection(trackGroup2, adaptiveAudioTracks);
            }
        }
        return new FixedTrackSelection(trackGroup2, i2);
    }

    private static int[] getAdaptiveAudioTracks(TrackGroup trackGroup, int[] iArr, boolean z) {
        HashSet hashSet = new HashSet();
        int i = 0;
        AudioConfigurationTuple audioConfigurationTuple = null;
        int i2 = 0;
        boolean z2 = i2;
        while (i2 < trackGroup.length) {
            Format format = trackGroup.getFormat(i2);
            AudioConfigurationTuple audioConfigurationTuple2 = new AudioConfigurationTuple(format.channelCount, format.sampleRate, z ? null : format.sampleMimeType);
            if (hashSet.add(audioConfigurationTuple2)) {
                boolean adaptiveAudioTrackCount = getAdaptiveAudioTrackCount(trackGroup, iArr, audioConfigurationTuple2);
                if (adaptiveAudioTrackCount > z2) {
                    z2 = adaptiveAudioTrackCount;
                    audioConfigurationTuple = audioConfigurationTuple2;
                }
            }
            i2++;
        }
        if (z2 <= true) {
            return NO_TRACKS;
        }
        z = new int[z2];
        int i3 = 0;
        while (i < trackGroup.length) {
            if (isSupportedAdaptiveAudioTrack(trackGroup.getFormat(i), iArr[i], audioConfigurationTuple)) {
                int i4 = i3 + 1;
                z[i3] = i;
                i3 = i4;
            }
            i++;
        }
        return z;
    }

    private static int getAdaptiveAudioTrackCount(TrackGroup trackGroup, int[] iArr, AudioConfigurationTuple audioConfigurationTuple) {
        int i = 0;
        int i2 = 0;
        while (i < trackGroup.length) {
            if (isSupportedAdaptiveAudioTrack(trackGroup.getFormat(i), iArr[i], audioConfigurationTuple)) {
                i2++;
            }
            i++;
        }
        return i2;
    }

    private static boolean isSupportedAdaptiveAudioTrack(Format format, int i, AudioConfigurationTuple audioConfigurationTuple) {
        if (isSupported(i, false) == 0 || format.channelCount != audioConfigurationTuple.channelCount || format.sampleRate != audioConfigurationTuple.sampleRate) {
            return false;
        }
        if (audioConfigurationTuple.mimeType == 0 || TextUtils.equals(audioConfigurationTuple.mimeType, format.sampleMimeType) != null) {
            return true;
        }
        return false;
    }

    protected TrackSelection selectTextTrack(TrackGroupArray trackGroupArray, int[][] iArr, Parameters parameters) throws ExoPlaybackException {
        TrackGroupArray trackGroupArray2 = trackGroupArray;
        Parameters parameters2 = parameters;
        int i = 0;
        int i2 = i;
        int i3 = i2;
        TrackGroup trackGroup = null;
        while (i < trackGroupArray2.length) {
            TrackGroup trackGroup2 = trackGroupArray2.get(i);
            int[] iArr2 = iArr[i];
            int i4 = i3;
            i3 = i2;
            TrackGroup trackGroup3 = trackGroup;
            for (int i5 = 0; i5 < trackGroup2.length; i5++) {
                if (isSupported(iArr2[i5], parameters2.exceedRendererCapabilitiesIfNecessary)) {
                    int i6;
                    Format format = trackGroup2.getFormat(i5);
                    int i7 = format.selectionFlags & (parameters2.disabledTextTrackSelectionFlags ^ -1);
                    boolean z = (i7 & 1) != 0;
                    boolean z2 = (i7 & 2) != 0;
                    boolean formatHasLanguage = formatHasLanguage(format, parameters2.preferredTextLanguage);
                    if (!formatHasLanguage) {
                        if (!parameters2.selectUndeterminedTextLanguage || !formatHasNoLanguage(format)) {
                            if (z) {
                                i6 = 3;
                            } else if (z2) {
                                i6 = formatHasLanguage(format, parameters2.preferredAudioLanguage) ? 2 : 1;
                            }
                            if (isSupported(iArr2[i5], false)) {
                                i6 += WITHIN_RENDERER_CAPABILITIES_BONUS;
                            }
                            if (i6 > i4) {
                                i3 = i5;
                                trackGroup3 = trackGroup2;
                                i4 = i6;
                            }
                        }
                    }
                    int i8 = z ? 8 : !z2 ? 6 : 4;
                    i6 = i8 + formatHasLanguage;
                    if (isSupported(iArr2[i5], false)) {
                        i6 += WITHIN_RENDERER_CAPABILITIES_BONUS;
                    }
                    if (i6 > i4) {
                        i3 = i5;
                        trackGroup3 = trackGroup2;
                        i4 = i6;
                    }
                }
            }
            i++;
            trackGroup = trackGroup3;
            i2 = i3;
            i3 = i4;
        }
        return trackGroup == null ? null : new FixedTrackSelection(trackGroup, i2);
    }

    protected TrackSelection selectOtherTrack(int i, TrackGroupArray trackGroupArray, int[][] iArr, Parameters parameters) throws ExoPlaybackException {
        TrackGroup trackGroup = null;
        int i2 = 0;
        int i3 = i2;
        int i4 = i3;
        while (i2 < trackGroupArray.length) {
            TrackGroup trackGroup2 = trackGroupArray.get(i2);
            int[] iArr2 = iArr[i2];
            int i5 = i4;
            i4 = i3;
            TrackGroup trackGroup3 = trackGroup;
            for (int i6 = 0; i6 < trackGroup2.length; i6++) {
                if (isSupported(iArr2[i6], parameters.exceedRendererCapabilitiesIfNecessary)) {
                    int i7 = 1;
                    if (((trackGroup2.getFormat(i6).selectionFlags & 1) != 0 ? 1 : false) != 0) {
                        i7 = 2;
                    }
                    if (isSupported(iArr2[i6], false)) {
                        i7 += WITHIN_RENDERER_CAPABILITIES_BONUS;
                    }
                    if (i7 > i5) {
                        i4 = i6;
                        trackGroup3 = trackGroup2;
                        i5 = i7;
                    }
                }
            }
            i2++;
            trackGroup = trackGroup3;
            i3 = i4;
            i4 = i5;
        }
        if (trackGroup == null) {
            return null;
        }
        return new FixedTrackSelection(trackGroup, i3);
    }

    protected static boolean formatHasNoLanguage(Format format) {
        if (!TextUtils.isEmpty(format.language)) {
            if (formatHasLanguage(format, C0542C.LANGUAGE_UNDETERMINED) == null) {
                return null;
            }
        }
        return true;
    }

    protected static boolean formatHasLanguage(Format format, String str) {
        return (str == null || TextUtils.equals(str, Util.normalizeLanguageCode(format.language)) == null) ? null : true;
    }

    private static List<Integer> getViewportFilteredTrackIndices(TrackGroup trackGroup, int i, int i2, boolean z) {
        List<Integer> arrayList = new ArrayList(trackGroup.length);
        int i3 = 0;
        for (int i4 = 0; i4 < trackGroup.length; i4++) {
            arrayList.add(Integer.valueOf(i4));
        }
        if (i != ConnectionsManager.DEFAULT_DATACENTER_ID) {
            if (i2 != ConnectionsManager.DEFAULT_DATACENTER_ID) {
                boolean z2 = true;
                while (i3 < trackGroup.length) {
                    Format format = trackGroup.getFormat(i3);
                    if (format.width > 0 && format.height > 0) {
                        Point maxVideoSizeInViewport = getMaxVideoSizeInViewport(z, i, i2, format.width, format.height);
                        boolean z3 = format.width * format.height;
                        if (format.width >= ((int) (((float) maxVideoSizeInViewport.x) * FRACTION_TO_CONSIDER_FULLSCREEN)) && format.height >= ((int) (((float) maxVideoSizeInViewport.y) * FRACTION_TO_CONSIDER_FULLSCREEN)) && z3 < z2) {
                            z2 = z3;
                        }
                    }
                    i3++;
                }
                if (!z2) {
                    for (i = arrayList.size() - 1; i >= 0; i--) {
                        boolean pixelCount = trackGroup.getFormat(((Integer) arrayList.get(i)).intValue()).getPixelCount();
                        if (pixelCount || pixelCount > z2) {
                            arrayList.remove(i);
                        }
                    }
                }
                return arrayList;
            }
        }
        return arrayList;
    }

    private static Point getMaxVideoSizeInViewport(boolean z, int i, int i2, int i3, int i4) {
        int i5;
        int i6;
        if (z) {
            z = false;
            boolean z2 = i3 > i4;
            if (i > i2) {
                z = true;
            }
            if (z2 != z) {
                i5 = i3 * i;
                i6 = i4 * i2;
                if (i5 < i6) {
                    return new Point(i2, Util.ceilDivide(i6, i3));
                }
                return new Point(Util.ceilDivide(i5, i4), i);
            }
        }
        int i7 = i2;
        i2 = i;
        i = i7;
        i5 = i3 * i;
        i6 = i4 * i2;
        if (i5 < i6) {
            return new Point(Util.ceilDivide(i5, i4), i);
        }
        return new Point(i2, Util.ceilDivide(i6, i3));
    }
}
