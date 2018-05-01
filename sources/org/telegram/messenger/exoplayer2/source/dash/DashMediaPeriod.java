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

        public static TrackGroupInfo primaryTrack(int i, int[] iArr, int i2, int i3, int i4) {
            return new TrackGroupInfo(i, 0, iArr, i2, i3, i4, -1);
        }

        public static TrackGroupInfo embeddedEmsgTrack(int[] iArr, int i) {
            return new TrackGroupInfo(4, 1, iArr, i, -1, -1, -1);
        }

        public static TrackGroupInfo embeddedCea608Track(int[] iArr, int i) {
            return new TrackGroupInfo(3, 1, iArr, i, -1, -1, -1);
        }

        public static TrackGroupInfo mpdEventTrack(int i) {
            return new TrackGroupInfo(4, 2, null, -1, -1, -1, i);
        }

        private TrackGroupInfo(int i, int i2, int[] iArr, int i3, int i4, int i5, int i6) {
            this.trackType = i;
            this.adaptationSetIndices = iArr;
            this.trackGroupCategory = i2;
            this.primaryTrackGroupIndex = i3;
            this.embeddedEventMessageTrackGroupIndex = i4;
            this.embeddedCea608TrackGroupIndex = i5;
            this.eventStreamGroupIndex = i6;
        }
    }

    public long readDiscontinuity() {
        return C0542C.TIME_UNSET;
    }

    public DashMediaPeriod(int i, DashManifest dashManifest, int i2, Factory factory, int i3, EventDispatcher eventDispatcher, long j, LoaderErrorThrower loaderErrorThrower, Allocator allocator, CompositeSequenceableLoaderFactory compositeSequenceableLoaderFactory, PlayerEmsgCallback playerEmsgCallback) {
        this.id = i;
        this.manifest = dashManifest;
        this.periodIndex = i2;
        this.chunkSourceFactory = factory;
        this.minLoadableRetryCount = i3;
        this.eventDispatcher = eventDispatcher;
        this.elapsedRealtimeOffset = j;
        this.manifestLoaderErrorThrower = loaderErrorThrower;
        this.allocator = allocator;
        this.compositeSequenceableLoaderFactory = compositeSequenceableLoaderFactory;
        this.playerEmsgHandler = new PlayerEmsgHandler(dashManifest, playerEmsgCallback, allocator);
        this.compositeSequenceableLoader = compositeSequenceableLoaderFactory.createCompositeSequenceableLoader(this.sampleStreams);
        i = dashManifest.getPeriod(i2);
        this.eventStreams = i.eventStreams;
        i = buildTrackGroups(i.adaptationSets, this.eventStreams);
        this.trackGroups = (TrackGroupArray) i.first;
        this.trackGroupInfos = (TrackGroupInfo[]) i.second;
    }

    public void updateManifest(DashManifest dashManifest, int i) {
        this.manifest = dashManifest;
        this.periodIndex = i;
        this.playerEmsgHandler.updateManifest(dashManifest);
        int i2 = 0;
        if (this.sampleStreams != null) {
            for (ChunkSampleStream chunkSource : this.sampleStreams) {
                ((DashChunkSource) chunkSource.getChunkSource()).updateManifest(dashManifest, i);
            }
            this.callback.onContinueLoadingRequested(this);
        }
        this.eventStreams = dashManifest.getPeriod(i).eventStreams;
        i = this.eventSampleStreams;
        int length = i.length;
        while (i2 < length) {
            EventSampleStream eventSampleStream = i[i2];
            for (EventStream eventStream : this.eventStreams) {
                if (eventStream.id().equals(eventSampleStream.eventStreamId())) {
                    eventSampleStream.updateEventStream(eventStream, dashManifest.dynamic);
                    break;
                }
            }
            i2++;
        }
    }

    public void release() {
        this.playerEmsgHandler.release();
        for (ChunkSampleStream release : this.sampleStreams) {
            release.release(this);
        }
    }

    public void onSampleStreamReleased(ChunkSampleStream<DashChunkSource> chunkSampleStream) {
        PlayerTrackEmsgHandler playerTrackEmsgHandler = (PlayerTrackEmsgHandler) this.trackEmsgHandlerBySampleStream.remove(chunkSampleStream);
        if (playerTrackEmsgHandler != null) {
            playerTrackEmsgHandler.release();
        }
    }

    public void prepare(MediaPeriod.Callback callback, long j) {
        this.callback = callback;
        callback.onPrepared(this);
    }

    public void maybeThrowPrepareError() throws IOException {
        this.manifestLoaderErrorThrower.maybeThrowError();
    }

    public TrackGroupArray getTrackGroups() {
        return this.trackGroups;
    }

    public long selectTracks(TrackSelection[] trackSelectionArr, boolean[] zArr, SampleStream[] sampleStreamArr, boolean[] zArr2, long j) {
        Map hashMap = new HashMap();
        List arrayList = new ArrayList();
        TrackSelection[] trackSelectionArr2 = trackSelectionArr;
        boolean[] zArr3 = zArr;
        SampleStream[] sampleStreamArr2 = sampleStreamArr;
        boolean[] zArr4 = zArr2;
        Map map = hashMap;
        selectPrimarySampleStreams(trackSelectionArr2, zArr3, sampleStreamArr2, zArr4, j, map);
        selectEventSampleStreams(trackSelectionArr2, zArr3, sampleStreamArr2, zArr4, arrayList);
        selectEmbeddedSampleStreams(trackSelectionArr2, zArr3, sampleStreamArr2, zArr4, j, map);
        this.sampleStreams = newSampleStreamArray(hashMap.size());
        hashMap.values().toArray(this.sampleStreams);
        this.eventSampleStreams = new EventSampleStream[arrayList.size()];
        arrayList.toArray(this.eventSampleStreams);
        this.compositeSequenceableLoader = this.compositeSequenceableLoaderFactory.createCompositeSequenceableLoader(this.sampleStreams);
        return j;
    }

    private void selectPrimarySampleStreams(TrackSelection[] trackSelectionArr, boolean[] zArr, SampleStream[] sampleStreamArr, boolean[] zArr2, long j, Map<Integer, ChunkSampleStream<DashChunkSource>> map) {
        int i = 0;
        while (i < trackSelectionArr.length) {
            if (sampleStreamArr[i] instanceof ChunkSampleStream) {
                ChunkSampleStream chunkSampleStream = (ChunkSampleStream) sampleStreamArr[i];
                if (trackSelectionArr[i] != null) {
                    if (zArr[i]) {
                        map.put(Integer.valueOf(this.trackGroups.indexOf(trackSelectionArr[i].getTrackGroup())), chunkSampleStream);
                    }
                }
                chunkSampleStream.release(this);
                sampleStreamArr[i] = null;
            }
            if (sampleStreamArr[i] == null && trackSelectionArr[i] != null) {
                int indexOf = this.trackGroups.indexOf(trackSelectionArr[i].getTrackGroup());
                TrackGroupInfo trackGroupInfo = this.trackGroupInfos[indexOf];
                if (trackGroupInfo.trackGroupCategory == 0) {
                    ChunkSampleStream buildSampleStream = buildSampleStream(trackGroupInfo, trackSelectionArr[i], j);
                    map.put(Integer.valueOf(indexOf), buildSampleStream);
                    sampleStreamArr[i] = buildSampleStream;
                    zArr2[i] = true;
                }
            }
            i++;
        }
    }

    private void selectEventSampleStreams(TrackSelection[] trackSelectionArr, boolean[] zArr, SampleStream[] sampleStreamArr, boolean[] zArr2, List<EventSampleStream> list) {
        int i = 0;
        while (i < trackSelectionArr.length) {
            if (sampleStreamArr[i] instanceof EventSampleStream) {
                EventSampleStream eventSampleStream = (EventSampleStream) sampleStreamArr[i];
                if (trackSelectionArr[i] != null) {
                    if (zArr[i]) {
                        list.add(eventSampleStream);
                    }
                }
                sampleStreamArr[i] = null;
            }
            if (sampleStreamArr[i] == null && trackSelectionArr[i] != null) {
                TrackGroupInfo trackGroupInfo = this.trackGroupInfos[this.trackGroups.indexOf(trackSelectionArr[i].getTrackGroup())];
                if (trackGroupInfo.trackGroupCategory == 2) {
                    EventSampleStream eventSampleStream2 = new EventSampleStream((EventStream) this.eventStreams.get(trackGroupInfo.eventStreamGroupIndex), trackSelectionArr[i].getTrackGroup().getFormat(0), this.manifest.dynamic);
                    sampleStreamArr[i] = eventSampleStream2;
                    zArr2[i] = true;
                    list.add(eventSampleStream2);
                }
            }
            i++;
        }
    }

    private void selectEmbeddedSampleStreams(TrackSelection[] trackSelectionArr, boolean[] zArr, SampleStream[] sampleStreamArr, boolean[] zArr2, long j, Map<Integer, ChunkSampleStream<DashChunkSource>> map) {
        int i = 0;
        while (i < trackSelectionArr.length) {
            if (((sampleStreamArr[i] instanceof EmbeddedSampleStream) || (sampleStreamArr[i] instanceof EmptySampleStream)) && (trackSelectionArr[i] == null || !zArr[i])) {
                releaseIfEmbeddedSampleStream(sampleStreamArr[i]);
                sampleStreamArr[i] = null;
            }
            if (trackSelectionArr[i] != null) {
                TrackGroupInfo trackGroupInfo = this.trackGroupInfos[this.trackGroups.indexOf(trackSelectionArr[i].getTrackGroup())];
                if (trackGroupInfo.trackGroupCategory == 1) {
                    ChunkSampleStream chunkSampleStream = (ChunkSampleStream) map.get(Integer.valueOf(trackGroupInfo.primaryTrackGroupIndex));
                    SampleStream sampleStream = sampleStreamArr[i];
                    boolean z = chunkSampleStream == null ? sampleStream instanceof EmptySampleStream : (sampleStream instanceof EmbeddedSampleStream) && ((EmbeddedSampleStream) sampleStream).parent == chunkSampleStream;
                    if (!z) {
                        EmptySampleStream emptySampleStream;
                        releaseIfEmbeddedSampleStream(sampleStream);
                        if (chunkSampleStream == null) {
                            emptySampleStream = new EmptySampleStream();
                        } else {
                            emptySampleStream = chunkSampleStream.selectEmbeddedTrack(j, trackGroupInfo.trackType);
                        }
                        sampleStreamArr[i] = emptySampleStream;
                        zArr2[i] = true;
                    }
                }
            }
            i++;
        }
    }

    public void discardBuffer(long j, boolean z) {
        for (ChunkSampleStream discardBuffer : this.sampleStreams) {
            discardBuffer.discardBuffer(j, z);
        }
    }

    public void reevaluateBuffer(long j) {
        this.compositeSequenceableLoader.reevaluateBuffer(j);
    }

    public boolean continueLoading(long j) {
        return this.compositeSequenceableLoader.continueLoading(j);
    }

    public long getNextLoadPositionUs() {
        return this.compositeSequenceableLoader.getNextLoadPositionUs();
    }

    public long getBufferedPositionUs() {
        return this.compositeSequenceableLoader.getBufferedPositionUs();
    }

    public long seekToUs(long j) {
        int i = 0;
        for (ChunkSampleStream seekToUs : this.sampleStreams) {
            seekToUs.seekToUs(j);
        }
        EventSampleStream[] eventSampleStreamArr = this.eventSampleStreams;
        int length = eventSampleStreamArr.length;
        while (i < length) {
            eventSampleStreamArr[i].seekToUs(j);
            i++;
        }
        return j;
    }

    public long getAdjustedSeekPositionUs(long j, SeekParameters seekParameters) {
        for (ChunkSampleStream chunkSampleStream : this.sampleStreams) {
            if (chunkSampleStream.primaryTrackType == 2) {
                return chunkSampleStream.getAdjustedSeekPositionUs(j, seekParameters);
            }
        }
        return j;
    }

    public void onContinueLoadingRequested(ChunkSampleStream<DashChunkSource> chunkSampleStream) {
        this.callback.onContinueLoadingRequested(this);
    }

    private static Pair<TrackGroupArray, TrackGroupInfo[]> buildTrackGroups(List<AdaptationSet> list, List<EventStream> list2) {
        int[][] groupedAdaptationSetIndices = getGroupedAdaptationSetIndices(list);
        int length = groupedAdaptationSetIndices.length;
        boolean[] zArr = new boolean[length];
        boolean[] zArr2 = new boolean[length];
        int identifyEmbeddedTracks = (identifyEmbeddedTracks(length, list, groupedAdaptationSetIndices, zArr, zArr2) + length) + list2.size();
        TrackGroup[] trackGroupArr = new TrackGroup[identifyEmbeddedTracks];
        Object obj = new TrackGroupInfo[identifyEmbeddedTracks];
        buildManifestEventTrackGroupInfos(list2, trackGroupArr, obj, buildPrimaryAndEmbeddedTrackGroupInfos(list, groupedAdaptationSetIndices, length, zArr, zArr2, trackGroupArr, obj));
        return Pair.create(new TrackGroupArray(trackGroupArr), obj);
    }

    private static int[][] getGroupedAdaptationSetIndices(List<AdaptationSet> list) {
        int size = list.size();
        SparseIntArray sparseIntArray = new SparseIntArray(size);
        for (int i = 0; i < size; i++) {
            sparseIntArray.put(((AdaptationSet) list.get(i)).id, i);
        }
        int[][] iArr = new int[size][];
        boolean[] zArr = new boolean[size];
        int i2 = 0;
        int i3 = i2;
        while (i2 < size) {
            if (!zArr[i2]) {
                zArr[i2] = true;
                Descriptor findAdaptationSetSwitchingProperty = findAdaptationSetSwitchingProperty(((AdaptationSet) list.get(i2)).supplementalProperties);
                if (findAdaptationSetSwitchingProperty == null) {
                    int i4 = i3 + 1;
                    iArr[i3] = new int[]{i2};
                    i3 = i4;
                } else {
                    String[] split = findAdaptationSetSwitchingProperty.value.split(",");
                    int[] iArr2 = new int[(split.length + 1)];
                    iArr2[0] = i2;
                    int i5 = 0;
                    while (i5 < split.length) {
                        int i6 = sparseIntArray.get(Integer.parseInt(split[i5]));
                        zArr[i6] = true;
                        i5++;
                        iArr2[i5] = i6;
                    }
                    int i7 = i3 + 1;
                    iArr[i3] = iArr2;
                    i3 = i7;
                }
            }
            i2++;
        }
        return i3 < size ? (int[][]) Arrays.copyOf(iArr, i3) : iArr;
    }

    private static int identifyEmbeddedTracks(int i, List<AdaptationSet> list, int[][] iArr, boolean[] zArr, boolean[] zArr2) {
        int i2 = 0;
        int i3 = 0;
        while (i2 < i) {
            if (hasEventMessageTrack(list, iArr[i2])) {
                zArr[i2] = true;
                i3++;
            }
            if (hasCea608Track(list, iArr[i2])) {
                zArr2[i2] = true;
                i3++;
            }
            i2++;
        }
        return i3;
    }

    private static int buildPrimaryAndEmbeddedTrackGroupInfos(List<AdaptationSet> list, int[][] iArr, int i, boolean[] zArr, boolean[] zArr2, TrackGroup[] trackGroupArr, TrackGroupInfo[] trackGroupInfoArr) {
        List list2 = list;
        int i2 = 0;
        int i3 = i;
        int i4 = 0;
        int i5 = i4;
        while (i4 < i3) {
            int i6;
            int i7;
            int i8;
            int i9;
            int i10;
            int i11;
            int[] iArr2 = iArr[i4];
            List arrayList = new ArrayList();
            int length = iArr2.length;
            for (i6 = i2; i6 < length; i6++) {
                arrayList.addAll(((AdaptationSet) list2.get(iArr2[i6])).representations);
            }
            Format[] formatArr = new Format[arrayList.size()];
            for (i6 = i2; i6 < formatArr.length; i6++) {
                formatArr[i6] = ((Representation) arrayList.get(i6)).format;
            }
            AdaptationSet adaptationSet = (AdaptationSet) list2.get(iArr2[i2]);
            i6 = i5 + 1;
            if (zArr[i4]) {
                i7 = i6 + 1;
                i8 = i6;
            } else {
                i7 = i6;
                i8 = -1;
            }
            if (zArr2[i4]) {
                i9 = i7 + 1;
            } else {
                i9 = i7;
                i7 = -1;
            }
            trackGroupArr[i5] = new TrackGroup(formatArr);
            trackGroupInfoArr[i5] = TrackGroupInfo.primaryTrack(adaptationSet.type, iArr2, i5, i8, i7);
            if (i8 != -1) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(adaptationSet.id);
                stringBuilder.append(":emsg");
                trackGroupArr[i8] = new TrackGroup(Format.createSampleFormat(stringBuilder.toString(), MimeTypes.APPLICATION_EMSG, null, -1, null));
                trackGroupInfoArr[i8] = TrackGroupInfo.embeddedEmsgTrack(iArr2, i5);
                i10 = -1;
            } else {
                i10 = -1;
            }
            if (i7 != i10) {
                StringBuilder stringBuilder2 = new StringBuilder();
                stringBuilder2.append(adaptationSet.id);
                stringBuilder2.append(":cea608");
                i11 = 0;
                trackGroupArr[i7] = new TrackGroup(Format.createTextSampleFormat(stringBuilder2.toString(), MimeTypes.APPLICATION_CEA608, 0, null));
                trackGroupInfoArr[i7] = TrackGroupInfo.embeddedCea608Track(iArr2, i5);
            } else {
                i11 = 0;
            }
            i4++;
            i2 = i11;
            i5 = i9;
            List<AdaptationSet> list3 = list;
        }
        return i5;
    }

    private static void buildManifestEventTrackGroupInfos(List<EventStream> list, TrackGroup[] trackGroupArr, TrackGroupInfo[] trackGroupInfoArr, int i) {
        int i2 = i;
        i = 0;
        while (i < list.size()) {
            trackGroupArr[i2] = new TrackGroup(Format.createSampleFormat(((EventStream) list.get(i)).id(), MimeTypes.APPLICATION_EMSG, null, -1, null));
            int i3 = i2 + 1;
            trackGroupInfoArr[i2] = TrackGroupInfo.mpdEventTrack(i);
            i++;
            i2 = i3;
        }
    }

    private ChunkSampleStream<DashChunkSource> buildSampleStream(TrackGroupInfo trackGroupInfo, TrackSelection trackSelection, long j) {
        int i;
        DashMediaPeriod dashMediaPeriod = this;
        TrackGroupInfo trackGroupInfo2 = trackGroupInfo;
        int[] iArr = new int[2];
        Format[] formatArr = new Format[2];
        boolean z = trackGroupInfo2.embeddedEventMessageTrackGroupIndex != -1;
        if (z) {
            formatArr[0] = dashMediaPeriod.trackGroups.get(trackGroupInfo2.embeddedEventMessageTrackGroupIndex).getFormat(0);
            iArr[0] = 4;
            i = 1;
        } else {
            i = 0;
        }
        boolean z2 = trackGroupInfo2.embeddedCea608TrackGroupIndex != -1;
        if (z2) {
            formatArr[i] = dashMediaPeriod.trackGroups.get(trackGroupInfo2.embeddedCea608TrackGroupIndex).getFormat(0);
            int i2 = i + 1;
            iArr[i] = 3;
            i = i2;
        }
        if (i < iArr.length) {
            formatArr = (Format[]) Arrays.copyOf(formatArr, i);
            iArr = Arrays.copyOf(iArr, i);
        }
        Format[] formatArr2 = formatArr;
        PlayerTrackEmsgHandler newPlayerTrackEmsgHandler = (dashMediaPeriod.manifest.dynamic && z) ? dashMediaPeriod.playerEmsgHandler.newPlayerTrackEmsgHandler() : null;
        PlayerTrackEmsgHandler playerTrackEmsgHandler = newPlayerTrackEmsgHandler;
        PlayerTrackEmsgHandler playerTrackEmsgHandler2 = playerTrackEmsgHandler;
        ChunkSampleStream<DashChunkSource> chunkSampleStream = new ChunkSampleStream(trackGroupInfo2.trackType, iArr, formatArr2, dashMediaPeriod.chunkSourceFactory.createDashChunkSource(dashMediaPeriod.manifestLoaderErrorThrower, dashMediaPeriod.manifest, dashMediaPeriod.periodIndex, trackGroupInfo2.adaptationSetIndices, trackSelection, trackGroupInfo2.trackType, dashMediaPeriod.elapsedRealtimeOffset, z, z2, playerTrackEmsgHandler), dashMediaPeriod, dashMediaPeriod.allocator, j, dashMediaPeriod.minLoadableRetryCount, dashMediaPeriod.eventDispatcher);
        dashMediaPeriod.trackEmsgHandlerBySampleStream.put(chunkSampleStream, playerTrackEmsgHandler2);
        return chunkSampleStream;
    }

    private static Descriptor findAdaptationSetSwitchingProperty(List<Descriptor> list) {
        for (int i = 0; i < list.size(); i++) {
            Descriptor descriptor = (Descriptor) list.get(i);
            if ("urn:mpeg:dash:adaptation-set-switching:2016".equals(descriptor.schemeIdUri)) {
                return descriptor;
            }
        }
        return null;
    }

    private static boolean hasEventMessageTrack(List<AdaptationSet> list, int[] iArr) {
        for (int i : iArr) {
            List list2 = ((AdaptationSet) list.get(i)).representations;
            for (int i2 = 0; i2 < list2.size(); i2++) {
                if (!((Representation) list2.get(i2)).inbandEventStreams.isEmpty()) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean hasCea608Track(List<AdaptationSet> list, int[] iArr) {
        for (int i : iArr) {
            List list2 = ((AdaptationSet) list.get(i)).accessibilityDescriptors;
            for (int i2 = 0; i2 < list2.size(); i2++) {
                if ("urn:scte:dash:cc:cea-608:2015".equals(((Descriptor) list2.get(i2)).schemeIdUri)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static ChunkSampleStream<DashChunkSource>[] newSampleStreamArray(int i) {
        return new ChunkSampleStream[i];
    }

    private static void releaseIfEmbeddedSampleStream(SampleStream sampleStream) {
        if (sampleStream instanceof EmbeddedSampleStream) {
            ((EmbeddedSampleStream) sampleStream).release();
        }
    }
}
