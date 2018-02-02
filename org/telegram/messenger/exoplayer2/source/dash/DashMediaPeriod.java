package org.telegram.messenger.exoplayer2.source.dash;

import android.util.Pair;
import android.util.SparseIntArray;
import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import org.telegram.messenger.exoplayer2.C;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.SeekParameters;
import org.telegram.messenger.exoplayer2.source.CompositeSequenceableLoaderFactory;
import org.telegram.messenger.exoplayer2.source.EmptySampleStream;
import org.telegram.messenger.exoplayer2.source.MediaPeriod;
import org.telegram.messenger.exoplayer2.source.MediaSourceEventListener.EventDispatcher;
import org.telegram.messenger.exoplayer2.source.SampleStream;
import org.telegram.messenger.exoplayer2.source.SequenceableLoader;
import org.telegram.messenger.exoplayer2.source.SequenceableLoader.Callback;
import org.telegram.messenger.exoplayer2.source.TrackGroup;
import org.telegram.messenger.exoplayer2.source.TrackGroupArray;
import org.telegram.messenger.exoplayer2.source.chunk.ChunkSampleStream;
import org.telegram.messenger.exoplayer2.source.chunk.ChunkSampleStream.EmbeddedSampleStream;
import org.telegram.messenger.exoplayer2.source.chunk.ChunkSampleStream.ReleaseCallback;
import org.telegram.messenger.exoplayer2.source.dash.DashChunkSource.Factory;
import org.telegram.messenger.exoplayer2.source.dash.PlayerEmsgHandler.PlayerEmsgCallback;
import org.telegram.messenger.exoplayer2.source.dash.PlayerEmsgHandler.PlayerTrackEmsgHandler;
import org.telegram.messenger.exoplayer2.source.dash.manifest.AdaptationSet;
import org.telegram.messenger.exoplayer2.source.dash.manifest.DashManifest;
import org.telegram.messenger.exoplayer2.source.dash.manifest.Descriptor;
import org.telegram.messenger.exoplayer2.source.dash.manifest.EventStream;
import org.telegram.messenger.exoplayer2.source.dash.manifest.Period;
import org.telegram.messenger.exoplayer2.source.dash.manifest.Representation;
import org.telegram.messenger.exoplayer2.trackselection.TrackSelection;
import org.telegram.messenger.exoplayer2.upstream.Allocator;
import org.telegram.messenger.exoplayer2.upstream.LoaderErrorThrower;
import org.telegram.messenger.exoplayer2.util.MimeTypes;

final class DashMediaPeriod implements MediaPeriod, Callback<ChunkSampleStream<DashChunkSource>>, ReleaseCallback<DashChunkSource> {
    private final Allocator allocator;
    private MediaPeriod.Callback callback;
    private final Factory chunkSourceFactory;
    private SequenceableLoader compositeSequenceableLoader;
    private final CompositeSequenceableLoaderFactory compositeSequenceableLoaderFactory;
    private final long elapsedRealtimeOffset;
    private final EventDispatcher eventDispatcher;
    private EventSampleStream[] eventSampleStreams = new EventSampleStream[0];
    private List<EventStream> eventStreams;
    final int id;
    private DashManifest manifest;
    private final LoaderErrorThrower manifestLoaderErrorThrower;
    private final int minLoadableRetryCount;
    private int periodIndex;
    private final PlayerEmsgHandler playerEmsgHandler;
    private ChunkSampleStream<DashChunkSource>[] sampleStreams = newSampleStreamArray(0);
    private final IdentityHashMap<ChunkSampleStream<DashChunkSource>, PlayerTrackEmsgHandler> trackEmsgHandlerBySampleStream = new IdentityHashMap();
    private final TrackGroupInfo[] trackGroupInfos;
    private final TrackGroupArray trackGroups;

    private static final class TrackGroupInfo {
        private static final int CATEGORY_EMBEDDED = 1;
        private static final int CATEGORY_MANIFEST_EVENTS = 2;
        private static final int CATEGORY_PRIMARY = 0;
        public final int[] adaptationSetIndices;
        public final int embeddedCea608TrackGroupIndex;
        public final int embeddedEventMessageTrackGroupIndex;
        public final int eventStreamGroupIndex;
        public final int primaryTrackGroupIndex;
        public final int trackGroupCategory;
        public final int trackType;

        @Retention(RetentionPolicy.SOURCE)
        public @interface TrackGroupCategory {
        }

        public static TrackGroupInfo primaryTrack(int trackType, int[] adaptationSetIndices, int primaryTrackGroupIndex, int embeddedEventMessageTrackGroupIndex, int embeddedCea608TrackGroupIndex) {
            return new TrackGroupInfo(trackType, 0, adaptationSetIndices, primaryTrackGroupIndex, embeddedEventMessageTrackGroupIndex, embeddedCea608TrackGroupIndex, -1);
        }

        public static TrackGroupInfo embeddedEmsgTrack(int[] adaptationSetIndices, int primaryTrackGroupIndex) {
            return new TrackGroupInfo(4, 1, adaptationSetIndices, primaryTrackGroupIndex, -1, -1, -1);
        }

        public static TrackGroupInfo embeddedCea608Track(int[] adaptationSetIndices, int primaryTrackGroupIndex) {
            return new TrackGroupInfo(3, 1, adaptationSetIndices, primaryTrackGroupIndex, -1, -1, -1);
        }

        public static TrackGroupInfo mpdEventTrack(int eventStreamIndex) {
            return new TrackGroupInfo(4, 2, null, -1, -1, -1, eventStreamIndex);
        }

        private TrackGroupInfo(int trackType, int trackGroupCategory, int[] adaptationSetIndices, int primaryTrackGroupIndex, int embeddedEventMessageTrackGroupIndex, int embeddedCea608TrackGroupIndex, int eventStreamGroupIndex) {
            this.trackType = trackType;
            this.adaptationSetIndices = adaptationSetIndices;
            this.trackGroupCategory = trackGroupCategory;
            this.primaryTrackGroupIndex = primaryTrackGroupIndex;
            this.embeddedEventMessageTrackGroupIndex = embeddedEventMessageTrackGroupIndex;
            this.embeddedCea608TrackGroupIndex = embeddedCea608TrackGroupIndex;
            this.eventStreamGroupIndex = eventStreamGroupIndex;
        }
    }

    public DashMediaPeriod(int id, DashManifest manifest, int periodIndex, Factory chunkSourceFactory, int minLoadableRetryCount, EventDispatcher eventDispatcher, long elapsedRealtimeOffset, LoaderErrorThrower manifestLoaderErrorThrower, Allocator allocator, CompositeSequenceableLoaderFactory compositeSequenceableLoaderFactory, PlayerEmsgCallback playerEmsgCallback) {
        this.id = id;
        this.manifest = manifest;
        this.periodIndex = periodIndex;
        this.chunkSourceFactory = chunkSourceFactory;
        this.minLoadableRetryCount = minLoadableRetryCount;
        this.eventDispatcher = eventDispatcher;
        this.elapsedRealtimeOffset = elapsedRealtimeOffset;
        this.manifestLoaderErrorThrower = manifestLoaderErrorThrower;
        this.allocator = allocator;
        this.compositeSequenceableLoaderFactory = compositeSequenceableLoaderFactory;
        this.playerEmsgHandler = new PlayerEmsgHandler(manifest, playerEmsgCallback, allocator);
        this.compositeSequenceableLoader = compositeSequenceableLoaderFactory.createCompositeSequenceableLoader(this.sampleStreams);
        Period period = manifest.getPeriod(periodIndex);
        this.eventStreams = period.eventStreams;
        Pair<TrackGroupArray, TrackGroupInfo[]> result = buildTrackGroups(period.adaptationSets, this.eventStreams);
        this.trackGroups = (TrackGroupArray) result.first;
        this.trackGroupInfos = (TrackGroupInfo[]) result.second;
    }

    public void updateManifest(DashManifest manifest, int periodIndex) {
        this.manifest = manifest;
        this.periodIndex = periodIndex;
        this.playerEmsgHandler.updateManifest(manifest);
        if (this.sampleStreams != null) {
            for (ChunkSampleStream<DashChunkSource> sampleStream : this.sampleStreams) {
                ((DashChunkSource) sampleStream.getChunkSource()).updateManifest(manifest, periodIndex);
            }
            this.callback.onContinueLoadingRequested(this);
        }
        this.eventStreams = manifest.getPeriod(periodIndex).eventStreams;
        for (EventSampleStream eventSampleStream : this.eventSampleStreams) {
            for (EventStream eventStream : this.eventStreams) {
                if (eventStream.id().equals(eventSampleStream.eventStreamId())) {
                    eventSampleStream.updateEventStream(eventStream, manifest.dynamic);
                    break;
                }
            }
        }
    }

    public void release() {
        this.playerEmsgHandler.release();
        for (ChunkSampleStream<DashChunkSource> sampleStream : this.sampleStreams) {
            sampleStream.release(this);
        }
    }

    public void onSampleStreamReleased(ChunkSampleStream<DashChunkSource> stream) {
        PlayerTrackEmsgHandler trackEmsgHandler = (PlayerTrackEmsgHandler) this.trackEmsgHandlerBySampleStream.remove(stream);
        if (trackEmsgHandler != null) {
            trackEmsgHandler.release();
        }
    }

    public void prepare(MediaPeriod.Callback callback, long positionUs) {
        this.callback = callback;
        callback.onPrepared(this);
    }

    public void maybeThrowPrepareError() throws IOException {
        this.manifestLoaderErrorThrower.maybeThrowError();
    }

    public TrackGroupArray getTrackGroups() {
        return this.trackGroups;
    }

    public long selectTracks(TrackSelection[] selections, boolean[] mayRetainStreamFlags, SampleStream[] streams, boolean[] streamResetFlags, long positionUs) {
        Map<Integer, ChunkSampleStream<DashChunkSource>> primarySampleStreams = new HashMap();
        List<EventSampleStream> eventSampleStreamList = new ArrayList();
        selectPrimarySampleStreams(selections, mayRetainStreamFlags, streams, streamResetFlags, positionUs, primarySampleStreams);
        selectEventSampleStreams(selections, mayRetainStreamFlags, streams, streamResetFlags, eventSampleStreamList);
        selectEmbeddedSampleStreams(selections, mayRetainStreamFlags, streams, streamResetFlags, positionUs, primarySampleStreams);
        this.sampleStreams = newSampleStreamArray(primarySampleStreams.size());
        primarySampleStreams.values().toArray(this.sampleStreams);
        this.eventSampleStreams = new EventSampleStream[eventSampleStreamList.size()];
        eventSampleStreamList.toArray(this.eventSampleStreams);
        this.compositeSequenceableLoader = this.compositeSequenceableLoaderFactory.createCompositeSequenceableLoader(this.sampleStreams);
        return positionUs;
    }

    private void selectPrimarySampleStreams(TrackSelection[] selections, boolean[] mayRetainStreamFlags, SampleStream[] streams, boolean[] streamResetFlags, long positionUs, Map<Integer, ChunkSampleStream<DashChunkSource>> primarySampleStreams) {
        int i = 0;
        while (i < selections.length) {
            ChunkSampleStream<DashChunkSource> stream;
            if (streams[i] instanceof ChunkSampleStream) {
                stream = streams[i];
                if (selections[i] == null || !mayRetainStreamFlags[i]) {
                    stream.release(this);
                    streams[i] = null;
                } else {
                    primarySampleStreams.put(Integer.valueOf(this.trackGroups.indexOf(selections[i].getTrackGroup())), stream);
                }
            }
            if (streams[i] == null && selections[i] != null) {
                int trackGroupIndex = this.trackGroups.indexOf(selections[i].getTrackGroup());
                TrackGroupInfo trackGroupInfo = this.trackGroupInfos[trackGroupIndex];
                if (trackGroupInfo.trackGroupCategory == 0) {
                    stream = buildSampleStream(trackGroupInfo, selections[i], positionUs);
                    primarySampleStreams.put(Integer.valueOf(trackGroupIndex), stream);
                    streams[i] = stream;
                    streamResetFlags[i] = true;
                }
            }
            i++;
        }
    }

    private void selectEventSampleStreams(TrackSelection[] selections, boolean[] mayRetainStreamFlags, SampleStream[] streams, boolean[] streamResetFlags, List<EventSampleStream> eventSampleStreamsList) {
        int i = 0;
        while (i < selections.length) {
            EventSampleStream stream;
            if (streams[i] instanceof EventSampleStream) {
                stream = streams[i];
                if (selections[i] == null || !mayRetainStreamFlags[i]) {
                    streams[i] = null;
                } else {
                    eventSampleStreamsList.add(stream);
                }
            }
            if (streams[i] == null && selections[i] != null) {
                TrackGroupInfo trackGroupInfo = this.trackGroupInfos[this.trackGroups.indexOf(selections[i].getTrackGroup())];
                if (trackGroupInfo.trackGroupCategory == 2) {
                    stream = new EventSampleStream((EventStream) this.eventStreams.get(trackGroupInfo.eventStreamGroupIndex), selections[i].getTrackGroup().getFormat(0), this.manifest.dynamic);
                    streams[i] = stream;
                    streamResetFlags[i] = true;
                    eventSampleStreamsList.add(stream);
                }
            }
            i++;
        }
    }

    private void selectEmbeddedSampleStreams(TrackSelection[] selections, boolean[] mayRetainStreamFlags, SampleStream[] streams, boolean[] streamResetFlags, long positionUs, Map<Integer, ChunkSampleStream<DashChunkSource>> primarySampleStreams) {
        int i = 0;
        while (i < selections.length) {
            if (((streams[i] instanceof EmbeddedSampleStream) || (streams[i] instanceof EmptySampleStream)) && (selections[i] == null || !mayRetainStreamFlags[i])) {
                releaseIfEmbeddedSampleStream(streams[i]);
                streams[i] = null;
            }
            if (selections[i] != null) {
                TrackGroupInfo trackGroupInfo = this.trackGroupInfos[this.trackGroups.indexOf(selections[i].getTrackGroup())];
                if (trackGroupInfo.trackGroupCategory == 1) {
                    ChunkSampleStream<?> primaryStream = (ChunkSampleStream) primarySampleStreams.get(Integer.valueOf(trackGroupInfo.primaryTrackGroupIndex));
                    SampleStream stream = streams[i];
                    boolean mayRetainStream = primaryStream == null ? stream instanceof EmptySampleStream : (stream instanceof EmbeddedSampleStream) && ((EmbeddedSampleStream) stream).parent == primaryStream;
                    if (!mayRetainStream) {
                        EmptySampleStream emptySampleStream;
                        releaseIfEmbeddedSampleStream(stream);
                        if (primaryStream == null) {
                            emptySampleStream = new EmptySampleStream();
                        } else {
                            emptySampleStream = primaryStream.selectEmbeddedTrack(positionUs, trackGroupInfo.trackType);
                        }
                        streams[i] = emptySampleStream;
                        streamResetFlags[i] = true;
                    }
                }
            }
            i++;
        }
    }

    public void discardBuffer(long positionUs, boolean toKeyframe) {
        for (ChunkSampleStream<DashChunkSource> sampleStream : this.sampleStreams) {
            sampleStream.discardBuffer(positionUs, toKeyframe);
        }
    }

    public void reevaluateBuffer(long positionUs) {
        this.compositeSequenceableLoader.reevaluateBuffer(positionUs);
    }

    public boolean continueLoading(long positionUs) {
        return this.compositeSequenceableLoader.continueLoading(positionUs);
    }

    public long getNextLoadPositionUs() {
        return this.compositeSequenceableLoader.getNextLoadPositionUs();
    }

    public long readDiscontinuity() {
        return C.TIME_UNSET;
    }

    public long getBufferedPositionUs() {
        return this.compositeSequenceableLoader.getBufferedPositionUs();
    }

    public long seekToUs(long positionUs) {
        int i = 0;
        for (ChunkSampleStream<DashChunkSource> sampleStream : this.sampleStreams) {
            sampleStream.seekToUs(positionUs);
        }
        EventSampleStream[] eventSampleStreamArr = this.eventSampleStreams;
        int length = eventSampleStreamArr.length;
        while (i < length) {
            eventSampleStreamArr[i].seekToUs(positionUs);
            i++;
        }
        return positionUs;
    }

    public long getAdjustedSeekPositionUs(long positionUs, SeekParameters seekParameters) {
        for (ChunkSampleStream<DashChunkSource> sampleStream : this.sampleStreams) {
            if (sampleStream.primaryTrackType == 2) {
                return sampleStream.getAdjustedSeekPositionUs(positionUs, seekParameters);
            }
        }
        return positionUs;
    }

    public void onContinueLoadingRequested(ChunkSampleStream<DashChunkSource> chunkSampleStream) {
        this.callback.onContinueLoadingRequested(this);
    }

    private static Pair<TrackGroupArray, TrackGroupInfo[]> buildTrackGroups(List<AdaptationSet> adaptationSets, List<EventStream> eventStreams) {
        int[][] groupedAdaptationSetIndices = getGroupedAdaptationSetIndices(adaptationSets);
        int primaryGroupCount = groupedAdaptationSetIndices.length;
        boolean[] primaryGroupHasEventMessageTrackFlags = new boolean[primaryGroupCount];
        boolean[] primaryGroupHasCea608TrackFlags = new boolean[primaryGroupCount];
        int totalGroupCount = (primaryGroupCount + identifyEmbeddedTracks(primaryGroupCount, adaptationSets, groupedAdaptationSetIndices, primaryGroupHasEventMessageTrackFlags, primaryGroupHasCea608TrackFlags)) + eventStreams.size();
        TrackGroup[] trackGroups = new TrackGroup[totalGroupCount];
        TrackGroupInfo[] trackGroupInfos = new TrackGroupInfo[totalGroupCount];
        buildManifestEventTrackGroupInfos(eventStreams, trackGroups, trackGroupInfos, buildPrimaryAndEmbeddedTrackGroupInfos(adaptationSets, groupedAdaptationSetIndices, primaryGroupCount, primaryGroupHasEventMessageTrackFlags, primaryGroupHasCea608TrackFlags, trackGroups, trackGroupInfos));
        return Pair.create(new TrackGroupArray(trackGroups), trackGroupInfos);
    }

    private static int[][] getGroupedAdaptationSetIndices(List<AdaptationSet> adaptationSets) {
        int i;
        int adaptationSetCount = adaptationSets.size();
        SparseIntArray idToIndexMap = new SparseIntArray(adaptationSetCount);
        for (i = 0; i < adaptationSetCount; i++) {
            idToIndexMap.put(((AdaptationSet) adaptationSets.get(i)).id, i);
        }
        int[][] groupedAdaptationSetIndices = new int[adaptationSetCount][];
        boolean[] adaptationSetUsedFlags = new boolean[adaptationSetCount];
        i = 0;
        int groupCount = 0;
        while (i < adaptationSetCount) {
            int groupCount2;
            if (adaptationSetUsedFlags[i]) {
                groupCount2 = groupCount;
            } else {
                adaptationSetUsedFlags[i] = true;
                Descriptor adaptationSetSwitchingProperty = findAdaptationSetSwitchingProperty(((AdaptationSet) adaptationSets.get(i)).supplementalProperties);
                if (adaptationSetSwitchingProperty == null) {
                    groupCount2 = groupCount + 1;
                    groupedAdaptationSetIndices[groupCount] = new int[]{i};
                } else {
                    String[] extraAdaptationSetIds = adaptationSetSwitchingProperty.value.split(",");
                    int[] adaptationSetIndices = new int[(extraAdaptationSetIds.length + 1)];
                    adaptationSetIndices[0] = i;
                    for (int j = 0; j < extraAdaptationSetIds.length; j++) {
                        int extraIndex = idToIndexMap.get(Integer.parseInt(extraAdaptationSetIds[j]));
                        adaptationSetUsedFlags[extraIndex] = true;
                        adaptationSetIndices[j + 1] = extraIndex;
                    }
                    groupCount2 = groupCount + 1;
                    groupedAdaptationSetIndices[groupCount] = adaptationSetIndices;
                }
            }
            i++;
            groupCount = groupCount2;
        }
        if (groupCount < adaptationSetCount) {
            return (int[][]) Arrays.copyOf(groupedAdaptationSetIndices, groupCount);
        }
        return groupedAdaptationSetIndices;
    }

    private static int identifyEmbeddedTracks(int primaryGroupCount, List<AdaptationSet> adaptationSets, int[][] groupedAdaptationSetIndices, boolean[] primaryGroupHasEventMessageTrackFlags, boolean[] primaryGroupHasCea608TrackFlags) {
        int numEmbeddedTrack = 0;
        for (int i = 0; i < primaryGroupCount; i++) {
            if (hasEventMessageTrack(adaptationSets, groupedAdaptationSetIndices[i])) {
                primaryGroupHasEventMessageTrackFlags[i] = true;
                numEmbeddedTrack++;
            }
            if (hasCea608Track(adaptationSets, groupedAdaptationSetIndices[i])) {
                primaryGroupHasCea608TrackFlags[i] = true;
                numEmbeddedTrack++;
            }
        }
        return numEmbeddedTrack;
    }

    private static int buildPrimaryAndEmbeddedTrackGroupInfos(List<AdaptationSet> adaptationSets, int[][] groupedAdaptationSetIndices, int primaryGroupCount, boolean[] primaryGroupHasEventMessageTrackFlags, boolean[] primaryGroupHasCea608TrackFlags, TrackGroup[] trackGroups, TrackGroupInfo[] trackGroupInfos) {
        int i = 0;
        int trackGroupCount = 0;
        while (i < primaryGroupCount) {
            int eventMessageTrackGroupIndex;
            int cea608TrackGroupIndex;
            int[] adaptationSetIndices = groupedAdaptationSetIndices[i];
            List<Representation> representations = new ArrayList();
            for (int adaptationSetIndex : adaptationSetIndices) {
                representations.addAll(((AdaptationSet) adaptationSets.get(adaptationSetIndex)).representations);
            }
            Format[] formats = new Format[representations.size()];
            for (int j = 0; j < formats.length; j++) {
                formats[j] = ((Representation) representations.get(j)).format;
            }
            AdaptationSet firstAdaptationSet = (AdaptationSet) adaptationSets.get(adaptationSetIndices[0]);
            int i2 = trackGroupCount + 1;
            int primaryTrackGroupIndex = trackGroupCount;
            if (primaryGroupHasEventMessageTrackFlags[i]) {
                eventMessageTrackGroupIndex = i2;
                i2++;
            } else {
                eventMessageTrackGroupIndex = -1;
            }
            if (primaryGroupHasCea608TrackFlags[i]) {
                cea608TrackGroupIndex = i2;
                i2++;
            } else {
                cea608TrackGroupIndex = -1;
            }
            trackGroups[primaryTrackGroupIndex] = new TrackGroup(formats);
            trackGroupInfos[primaryTrackGroupIndex] = TrackGroupInfo.primaryTrack(firstAdaptationSet.type, adaptationSetIndices, primaryTrackGroupIndex, eventMessageTrackGroupIndex, cea608TrackGroupIndex);
            if (eventMessageTrackGroupIndex != -1) {
                Format format = Format.createSampleFormat(firstAdaptationSet.id + ":emsg", MimeTypes.APPLICATION_EMSG, null, -1, null);
                trackGroups[eventMessageTrackGroupIndex] = new TrackGroup(format);
                trackGroupInfos[eventMessageTrackGroupIndex] = TrackGroupInfo.embeddedEmsgTrack(adaptationSetIndices, primaryTrackGroupIndex);
            }
            if (cea608TrackGroupIndex != -1) {
                format = Format.createTextSampleFormat(firstAdaptationSet.id + ":cea608", MimeTypes.APPLICATION_CEA608, 0, null);
                trackGroups[cea608TrackGroupIndex] = new TrackGroup(format);
                trackGroupInfos[cea608TrackGroupIndex] = TrackGroupInfo.embeddedCea608Track(adaptationSetIndices, primaryTrackGroupIndex);
            }
            i++;
            trackGroupCount = i2;
        }
        return trackGroupCount;
    }

    private static void buildManifestEventTrackGroupInfos(List<EventStream> eventStreams, TrackGroup[] trackGroups, TrackGroupInfo[] trackGroupInfos, int existingTrackGroupCount) {
        int i = 0;
        while (i < eventStreams.size()) {
            Format format = Format.createSampleFormat(((EventStream) eventStreams.get(i)).id(), MimeTypes.APPLICATION_EMSG, null, -1, null);
            trackGroups[existingTrackGroupCount] = new TrackGroup(format);
            int existingTrackGroupCount2 = existingTrackGroupCount + 1;
            trackGroupInfos[existingTrackGroupCount] = TrackGroupInfo.mpdEventTrack(i);
            i++;
            existingTrackGroupCount = existingTrackGroupCount2;
        }
    }

    private ChunkSampleStream<DashChunkSource> buildSampleStream(TrackGroupInfo trackGroupInfo, TrackSelection selection, long positionUs) {
        int i = 0;
        int[] embeddedTrackTypes = new int[2];
        Format[] embeddedTrackFormats = new Format[2];
        boolean enableEventMessageTrack = trackGroupInfo.embeddedEventMessageTrackGroupIndex != -1;
        if (enableEventMessageTrack) {
            embeddedTrackFormats[0] = this.trackGroups.get(trackGroupInfo.embeddedEventMessageTrackGroupIndex).getFormat(0);
            int embeddedTrackCount = 0 + 1;
            embeddedTrackTypes[0] = 4;
            i = embeddedTrackCount;
        }
        boolean enableCea608Track = trackGroupInfo.embeddedCea608TrackGroupIndex != -1;
        if (enableCea608Track) {
            embeddedTrackFormats[i] = this.trackGroups.get(trackGroupInfo.embeddedCea608TrackGroupIndex).getFormat(0);
            embeddedTrackCount = i + 1;
            embeddedTrackTypes[i] = 3;
            i = embeddedTrackCount;
        }
        if (i < embeddedTrackTypes.length) {
            embeddedTrackFormats = (Format[]) Arrays.copyOf(embeddedTrackFormats, i);
            embeddedTrackTypes = Arrays.copyOf(embeddedTrackTypes, i);
        }
        PlayerTrackEmsgHandler trackPlayerEmsgHandler = (this.manifest.dynamic && enableEventMessageTrack) ? this.playerEmsgHandler.newPlayerTrackEmsgHandler() : null;
        ChunkSampleStream<DashChunkSource> stream = new ChunkSampleStream(trackGroupInfo.trackType, embeddedTrackTypes, embeddedTrackFormats, this.chunkSourceFactory.createDashChunkSource(this.manifestLoaderErrorThrower, this.manifest, this.periodIndex, trackGroupInfo.adaptationSetIndices, selection, trackGroupInfo.trackType, this.elapsedRealtimeOffset, enableEventMessageTrack, enableCea608Track, trackPlayerEmsgHandler), this, this.allocator, positionUs, this.minLoadableRetryCount, this.eventDispatcher);
        this.trackEmsgHandlerBySampleStream.put(stream, trackPlayerEmsgHandler);
        return stream;
    }

    private static Descriptor findAdaptationSetSwitchingProperty(List<Descriptor> descriptors) {
        for (int i = 0; i < descriptors.size(); i++) {
            Descriptor descriptor = (Descriptor) descriptors.get(i);
            if ("urn:mpeg:dash:adaptation-set-switching:2016".equals(descriptor.schemeIdUri)) {
                return descriptor;
            }
        }
        return null;
    }

    private static boolean hasEventMessageTrack(List<AdaptationSet> adaptationSets, int[] adaptationSetIndices) {
        for (int i : adaptationSetIndices) {
            List<Representation> representations = ((AdaptationSet) adaptationSets.get(i)).representations;
            for (int j = 0; j < representations.size(); j++) {
                if (!((Representation) representations.get(j)).inbandEventStreams.isEmpty()) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean hasCea608Track(List<AdaptationSet> adaptationSets, int[] adaptationSetIndices) {
        for (int i : adaptationSetIndices) {
            List<Descriptor> descriptors = ((AdaptationSet) adaptationSets.get(i)).accessibilityDescriptors;
            for (int j = 0; j < descriptors.size(); j++) {
                if ("urn:scte:dash:cc:cea-608:2015".equals(((Descriptor) descriptors.get(j)).schemeIdUri)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static ChunkSampleStream<DashChunkSource>[] newSampleStreamArray(int length) {
        return new ChunkSampleStream[length];
    }

    private static void releaseIfEmbeddedSampleStream(SampleStream sampleStream) {
        if (sampleStream instanceof EmbeddedSampleStream) {
            ((EmbeddedSampleStream) sampleStream).release();
        }
    }
}
