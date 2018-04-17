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
import org.telegram.messenger.exoplayer2.C0542C;
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
        int i = 0;
        if (this.sampleStreams != null) {
            for (ChunkSampleStream<DashChunkSource> sampleStream : this.sampleStreams) {
                ((DashChunkSource) sampleStream.getChunkSource()).updateManifest(manifest, periodIndex);
            }
            this.callback.onContinueLoadingRequested(this);
        }
        this.eventStreams = manifest.getPeriod(periodIndex).eventStreams;
        EventSampleStream[] eventSampleStreamArr = this.eventSampleStreams;
        int length = eventSampleStreamArr.length;
        while (i < length) {
            EventSampleStream eventSampleStream = eventSampleStreamArr[i];
            for (EventStream eventStream : this.eventStreams) {
                if (eventStream.id().equals(eventSampleStream.eventStreamId())) {
                    eventSampleStream.updateEventStream(eventStream, manifest.dynamic);
                    break;
                }
            }
            i++;
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
        Map primarySampleStreams = new HashMap();
        List eventSampleStreamList = new ArrayList();
        TrackSelection[] trackSelectionArr = selections;
        boolean[] zArr = mayRetainStreamFlags;
        SampleStream[] sampleStreamArr = streams;
        boolean[] zArr2 = streamResetFlags;
        Map map = primarySampleStreams;
        selectPrimarySampleStreams(trackSelectionArr, zArr, sampleStreamArr, zArr2, positionUs, map);
        selectEventSampleStreams(trackSelectionArr, zArr, sampleStreamArr, zArr2, eventSampleStreamList);
        selectEmbeddedSampleStreams(trackSelectionArr, zArr, sampleStreamArr, zArr2, positionUs, map);
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
            if (streams[i] instanceof ChunkSampleStream) {
                ChunkSampleStream<DashChunkSource> stream = streams[i];
                if (selections[i] != null) {
                    if (mayRetainStreamFlags[i]) {
                        primarySampleStreams.put(Integer.valueOf(this.trackGroups.indexOf(selections[i].getTrackGroup())), stream);
                    }
                }
                stream.release(this);
                streams[i] = null;
            }
            if (streams[i] == null && selections[i] != null) {
                int trackGroupIndex = this.trackGroups.indexOf(selections[i].getTrackGroup());
                TrackGroupInfo trackGroupInfo = this.trackGroupInfos[trackGroupIndex];
                if (trackGroupInfo.trackGroupCategory == 0) {
                    ChunkSampleStream<DashChunkSource> stream2 = buildSampleStream(trackGroupInfo, selections[i], positionUs);
                    primarySampleStreams.put(Integer.valueOf(trackGroupIndex), stream2);
                    streams[i] = stream2;
                    streamResetFlags[i] = true;
                }
            }
            i++;
        }
    }

    private void selectEventSampleStreams(TrackSelection[] selections, boolean[] mayRetainStreamFlags, SampleStream[] streams, boolean[] streamResetFlags, List<EventSampleStream> eventSampleStreamsList) {
        int i = 0;
        while (i < selections.length) {
            if (streams[i] instanceof EventSampleStream) {
                EventSampleStream stream = streams[i];
                if (selections[i] != null) {
                    if (mayRetainStreamFlags[i]) {
                        eventSampleStreamsList.add(stream);
                    }
                }
                streams[i] = null;
            }
            if (streams[i] == null && selections[i] != null) {
                TrackGroupInfo trackGroupInfo = this.trackGroupInfos[this.trackGroups.indexOf(selections[i].getTrackGroup())];
                if (trackGroupInfo.trackGroupCategory == 2) {
                    EventSampleStream stream2 = new EventSampleStream((EventStream) this.eventStreams.get(trackGroupInfo.eventStreamGroupIndex), selections[i].getTrackGroup().getFormat(0), this.manifest.dynamic);
                    streams[i] = stream2;
                    streamResetFlags[i] = true;
                    eventSampleStreamsList.add(stream2);
                }
            }
            i++;
        }
    }

    private void selectEmbeddedSampleStreams(TrackSelection[] selections, boolean[] mayRetainStreamFlags, SampleStream[] streams, boolean[] streamResetFlags, long positionUs, Map<Integer, ChunkSampleStream<DashChunkSource>> primarySampleStreams) {
        long j;
        Map<Integer, ChunkSampleStream<DashChunkSource>> map;
        DashMediaPeriod dashMediaPeriod = this;
        TrackSelection[] trackSelectionArr = selections;
        int i = 0;
        while (i < trackSelectionArr.length) {
            if (((streams[i] instanceof EmbeddedSampleStream) || (streams[i] instanceof EmptySampleStream)) && (trackSelectionArr[i] == null || !mayRetainStreamFlags[i])) {
                releaseIfEmbeddedSampleStream(streams[i]);
                streams[i] = null;
            }
            if (trackSelectionArr[i] != null) {
                TrackGroupInfo trackGroupInfo = dashMediaPeriod.trackGroupInfos[dashMediaPeriod.trackGroups.indexOf(trackSelectionArr[i].getTrackGroup())];
                if (trackGroupInfo.trackGroupCategory == 1) {
                    ChunkSampleStream<?> primaryStream = (ChunkSampleStream) primarySampleStreams.get(Integer.valueOf(trackGroupInfo.primaryTrackGroupIndex));
                    SampleStream stream = streams[i];
                    boolean mayRetainStream = primaryStream == null ? stream instanceof EmptySampleStream : (stream instanceof EmbeddedSampleStream) && ((EmbeddedSampleStream) stream).parent == primaryStream;
                    if (mayRetainStream) {
                        j = positionUs;
                    } else {
                        EmptySampleStream emptySampleStream;
                        releaseIfEmbeddedSampleStream(stream);
                        if (primaryStream == null) {
                            emptySampleStream = new EmptySampleStream();
                            j = positionUs;
                        } else {
                            emptySampleStream = primaryStream.selectEmbeddedTrack(positionUs, trackGroupInfo.trackType);
                        }
                        streams[i] = emptySampleStream;
                        streamResetFlags[i] = true;
                    }
                    i++;
                }
            }
            j = positionUs;
            map = primarySampleStreams;
            i++;
        }
        j = positionUs;
        map = primarySampleStreams;
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
        return C0542C.TIME_UNSET;
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
        List<AdaptationSet> list = adaptationSets;
        int totalGroupCount = (primaryGroupCount + identifyEmbeddedTracks(primaryGroupCount, list, groupedAdaptationSetIndices, primaryGroupHasEventMessageTrackFlags, primaryGroupHasCea608TrackFlags)) + eventStreams.size();
        TrackGroup[] trackGroups = new TrackGroup[totalGroupCount];
        TrackGroupInfo[] trackGroupInfos = new TrackGroupInfo[totalGroupCount];
        buildManifestEventTrackGroupInfos(eventStreams, trackGroups, trackGroupInfos, buildPrimaryAndEmbeddedTrackGroupInfos(list, groupedAdaptationSetIndices, primaryGroupCount, primaryGroupHasEventMessageTrackFlags, primaryGroupHasCea608TrackFlags, trackGroups, trackGroupInfos));
        return Pair.create(new TrackGroupArray(trackGroups), trackGroupInfos);
    }

    private static int[][] getGroupedAdaptationSetIndices(List<AdaptationSet> adaptationSets) {
        int adaptationSetCount = adaptationSets.size();
        SparseIntArray idToIndexMap = new SparseIntArray(adaptationSetCount);
        for (int i = 0; i < adaptationSetCount; i++) {
            idToIndexMap.put(((AdaptationSet) adaptationSets.get(i)).id, i);
        }
        int[][] groupedAdaptationSetIndices = new int[adaptationSetCount][];
        boolean[] adaptationSetUsedFlags = new boolean[adaptationSetCount];
        int groupCount = 0;
        for (int i2 = 0; i2 < adaptationSetCount; i2++) {
            if (!adaptationSetUsedFlags[i2]) {
                adaptationSetUsedFlags[i2] = true;
                Descriptor adaptationSetSwitchingProperty = findAdaptationSetSwitchingProperty(((AdaptationSet) adaptationSets.get(i2)).supplementalProperties);
                if (adaptationSetSwitchingProperty == null) {
                    int groupCount2 = groupCount + 1;
                    groupedAdaptationSetIndices[groupCount] = new int[]{i2};
                    groupCount = groupCount2;
                } else {
                    String[] extraAdaptationSetIds = adaptationSetSwitchingProperty.value.split(",");
                    int[] adaptationSetIndices = new int[(extraAdaptationSetIds.length + 1)];
                    adaptationSetIndices[0] = i2;
                    for (int j = 0; j < extraAdaptationSetIds.length; j++) {
                        int extraIndex = idToIndexMap.get(Integer.parseInt(extraAdaptationSetIds[j]));
                        adaptationSetUsedFlags[extraIndex] = true;
                        adaptationSetIndices[1 + j] = extraIndex;
                    }
                    int groupCount3 = groupCount + 1;
                    groupedAdaptationSetIndices[groupCount] = adaptationSetIndices;
                    groupCount = groupCount3;
                }
            }
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
        List list = adaptationSets;
        int i = 0;
        int trackGroupCount = 0;
        int i2 = 0;
        while (i2 < primaryGroupCount) {
            int i3;
            int trackGroupCount2;
            int trackGroupCount3;
            int i4;
            int[] adaptationSetIndices = groupedAdaptationSetIndices[i2];
            List<Representation> representations = new ArrayList();
            int length = adaptationSetIndices.length;
            for (i3 = i; i3 < length; i3++) {
                representations.addAll(((AdaptationSet) list.get(adaptationSetIndices[i3])).representations);
            }
            Format[] formats = new Format[representations.size()];
            for (i3 = i; i3 < formats.length; i3++) {
                formats[i3] = ((Representation) representations.get(i3)).format;
            }
            AdaptationSet firstAdaptationSet = (AdaptationSet) list.get(adaptationSetIndices[i]);
            int eventMessageTrackGroupIndex = trackGroupCount + 1;
            if (primaryGroupHasEventMessageTrackFlags[i2]) {
                trackGroupCount2 = eventMessageTrackGroupIndex + 1;
            } else {
                trackGroupCount2 = eventMessageTrackGroupIndex;
                eventMessageTrackGroupIndex = -1;
            }
            if (primaryGroupHasCea608TrackFlags[i2]) {
                trackGroupCount3 = trackGroupCount2 + 1;
            } else {
                trackGroupCount3 = trackGroupCount2;
                trackGroupCount2 = -1;
            }
            trackGroups[trackGroupCount] = new TrackGroup(formats);
            trackGroupInfos[trackGroupCount] = TrackGroupInfo.primaryTrack(firstAdaptationSet.type, adaptationSetIndices, trackGroupCount, eventMessageTrackGroupIndex, trackGroupCount2);
            if (eventMessageTrackGroupIndex != -1) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(firstAdaptationSet.id);
                stringBuilder.append(":emsg");
                Format format = Format.createSampleFormat(stringBuilder.toString(), MimeTypes.APPLICATION_EMSG, null, -1, null);
                trackGroups[eventMessageTrackGroupIndex] = new TrackGroup(format);
                trackGroupInfos[eventMessageTrackGroupIndex] = TrackGroupInfo.embeddedEmsgTrack(adaptationSetIndices, trackGroupCount);
            }
            if (trackGroupCount2 != -1) {
                StringBuilder stringBuilder2 = new StringBuilder();
                stringBuilder2.append(firstAdaptationSet.id);
                stringBuilder2.append(":cea608");
                i4 = 0;
                trackGroups[trackGroupCount2] = new TrackGroup(Format.createTextSampleFormat(stringBuilder2.toString(), MimeTypes.APPLICATION_CEA608, 0, null));
                trackGroupInfos[trackGroupCount2] = TrackGroupInfo.embeddedCea608Track(adaptationSetIndices, trackGroupCount);
            } else {
                i4 = 0;
            }
            i2++;
            i = i4;
            trackGroupCount = trackGroupCount3;
            List<AdaptationSet> list2 = adaptationSets;
        }
        return trackGroupCount;
    }

    private static void buildManifestEventTrackGroupInfos(List<EventStream> eventStreams, TrackGroup[] trackGroups, TrackGroupInfo[] trackGroupInfos, int existingTrackGroupCount) {
        int existingTrackGroupCount2 = existingTrackGroupCount;
        existingTrackGroupCount = 0;
        while (existingTrackGroupCount < eventStreams.size()) {
            trackGroups[existingTrackGroupCount2] = new TrackGroup(Format.createSampleFormat(((EventStream) eventStreams.get(existingTrackGroupCount)).id(), MimeTypes.APPLICATION_EMSG, null, -1, null));
            int existingTrackGroupCount3 = existingTrackGroupCount2 + 1;
            trackGroupInfos[existingTrackGroupCount2] = TrackGroupInfo.mpdEventTrack(existingTrackGroupCount);
            existingTrackGroupCount++;
            existingTrackGroupCount2 = existingTrackGroupCount3;
        }
    }

    private ChunkSampleStream<DashChunkSource> buildSampleStream(TrackGroupInfo trackGroupInfo, TrackSelection selection, long positionUs) {
        int embeddedTrackCount;
        Format[] embeddedTrackFormats;
        int[] embeddedTrackTypes;
        DashMediaPeriod dashMediaPeriod = this;
        TrackGroupInfo trackGroupInfo2 = trackGroupInfo;
        int embeddedTrackCount2 = 0;
        int[] embeddedTrackTypes2 = new int[2];
        Format[] embeddedTrackFormats2 = new Format[2];
        boolean z = true;
        boolean enableEventMessageTrack = trackGroupInfo2.embeddedEventMessageTrackGroupIndex != -1;
        if (enableEventMessageTrack) {
            embeddedTrackFormats2[0] = dashMediaPeriod.trackGroups.get(trackGroupInfo2.embeddedEventMessageTrackGroupIndex).getFormat(0);
            int embeddedTrackCount3 = 0 + 1;
            embeddedTrackTypes2[0] = 4;
            embeddedTrackCount2 = embeddedTrackCount3;
        }
        if (trackGroupInfo2.embeddedCea608TrackGroupIndex == -1) {
            z = false;
        }
        boolean enableCea608Track = z;
        if (enableCea608Track) {
            embeddedTrackFormats2[embeddedTrackCount2] = dashMediaPeriod.trackGroups.get(trackGroupInfo2.embeddedCea608TrackGroupIndex).getFormat(0);
            embeddedTrackCount3 = embeddedTrackCount2 + 1;
            embeddedTrackTypes2[embeddedTrackCount2] = 3;
            embeddedTrackCount = embeddedTrackCount3;
        } else {
            embeddedTrackCount = embeddedTrackCount2;
        }
        if (embeddedTrackCount < embeddedTrackTypes2.length) {
            embeddedTrackFormats = (Format[]) Arrays.copyOf(embeddedTrackFormats2, embeddedTrackCount);
            embeddedTrackTypes = Arrays.copyOf(embeddedTrackTypes2, embeddedTrackCount);
        } else {
            embeddedTrackFormats = embeddedTrackFormats2;
            embeddedTrackTypes = embeddedTrackTypes2;
        }
        PlayerTrackEmsgHandler newPlayerTrackEmsgHandler = (dashMediaPeriod.manifest.dynamic && enableEventMessageTrack) ? dashMediaPeriod.playerEmsgHandler.newPlayerTrackEmsgHandler() : null;
        PlayerTrackEmsgHandler trackPlayerEmsgHandler = newPlayerTrackEmsgHandler;
        DashChunkSource chunkSource = dashMediaPeriod.chunkSourceFactory.createDashChunkSource(dashMediaPeriod.manifestLoaderErrorThrower, dashMediaPeriod.manifest, dashMediaPeriod.periodIndex, trackGroupInfo2.adaptationSetIndices, selection, trackGroupInfo2.trackType, dashMediaPeriod.elapsedRealtimeOffset, enableEventMessageTrack, enableCea608Track, trackPlayerEmsgHandler);
        PlayerTrackEmsgHandler trackPlayerEmsgHandler2 = trackPlayerEmsgHandler;
        ChunkSampleStream<DashChunkSource> stream = new ChunkSampleStream(trackGroupInfo2.trackType, embeddedTrackTypes, embeddedTrackFormats, chunkSource, dashMediaPeriod, dashMediaPeriod.allocator, positionUs, dashMediaPeriod.minLoadableRetryCount, dashMediaPeriod.eventDispatcher);
        dashMediaPeriod.trackEmsgHandlerBySampleStream.put(stream, trackPlayerEmsgHandler2);
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
