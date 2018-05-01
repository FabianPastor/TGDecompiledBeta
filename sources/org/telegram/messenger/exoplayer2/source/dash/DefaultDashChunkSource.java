package org.telegram.messenger.exoplayer2.source.dash;

import android.net.Uri;
import android.os.SystemClock;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.telegram.messenger.exoplayer2.C0542C;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.SeekParameters;
import org.telegram.messenger.exoplayer2.extractor.ChunkIndex;
import org.telegram.messenger.exoplayer2.extractor.SeekMap;
import org.telegram.messenger.exoplayer2.source.BehindLiveWindowException;
import org.telegram.messenger.exoplayer2.source.chunk.Chunk;
import org.telegram.messenger.exoplayer2.source.chunk.ChunkExtractorWrapper;
import org.telegram.messenger.exoplayer2.source.chunk.ChunkHolder;
import org.telegram.messenger.exoplayer2.source.chunk.ChunkedTrackBlacklistUtil;
import org.telegram.messenger.exoplayer2.source.chunk.ContainerMediaChunk;
import org.telegram.messenger.exoplayer2.source.chunk.InitializationChunk;
import org.telegram.messenger.exoplayer2.source.chunk.MediaChunk;
import org.telegram.messenger.exoplayer2.source.chunk.SingleSampleMediaChunk;
import org.telegram.messenger.exoplayer2.source.dash.PlayerEmsgHandler.PlayerTrackEmsgHandler;
import org.telegram.messenger.exoplayer2.source.dash.manifest.AdaptationSet;
import org.telegram.messenger.exoplayer2.source.dash.manifest.DashManifest;
import org.telegram.messenger.exoplayer2.source.dash.manifest.RangedUri;
import org.telegram.messenger.exoplayer2.source.dash.manifest.Representation;
import org.telegram.messenger.exoplayer2.trackselection.TrackSelection;
import org.telegram.messenger.exoplayer2.upstream.DataSource;
import org.telegram.messenger.exoplayer2.upstream.DataSpec;
import org.telegram.messenger.exoplayer2.upstream.HttpDataSource.InvalidResponseCodeException;
import org.telegram.messenger.exoplayer2.upstream.LoaderErrorThrower;
import org.telegram.messenger.exoplayer2.util.MimeTypes;
import org.telegram.messenger.exoplayer2.util.Util;

public class DefaultDashChunkSource implements DashChunkSource {
    private final int[] adaptationSetIndices;
    private final DataSource dataSource;
    private final long elapsedRealtimeOffsetMs;
    private IOException fatalError;
    private long liveEdgeTimeUs = C0542C.TIME_UNSET;
    private DashManifest manifest;
    private final LoaderErrorThrower manifestLoaderErrorThrower;
    private final int maxSegmentsPerLoad;
    private boolean missingLastSegment;
    private int periodIndex;
    private final PlayerTrackEmsgHandler playerTrackEmsgHandler;
    protected final RepresentationHolder[] representationHolders;
    private final TrackSelection trackSelection;
    private final int trackType;

    protected static final class RepresentationHolder {
        final ChunkExtractorWrapper extractorWrapper;
        private long periodDurationUs;
        public Representation representation;
        public DashSegmentIndex segmentIndex;
        private int segmentNumShift;

        RepresentationHolder(long r9, int r11, org.telegram.messenger.exoplayer2.source.dash.manifest.Representation r12, boolean r13, boolean r14, org.telegram.messenger.exoplayer2.extractor.TrackOutput r15) {
            /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Unknown predecessor block by arg (r9_10 long) in PHI: PHI: (r9_12 long) = (r9_3 long), (r9_5 long), (r9_10 long) binds: {(r9_3 long)=B:5:0x001d, (r9_5 long)=B:8:0x002b, (r9_10 long)=B:17:0x004c}
	at jadx.core.dex.instructions.PhiInsn.replaceArg(PhiInsn.java:79)
	at jadx.core.dex.visitors.ModVisitor.processInvoke(ModVisitor.java:222)
	at jadx.core.dex.visitors.ModVisitor.replaceStep(ModVisitor.java:83)
	at jadx.core.dex.visitors.ModVisitor.visit(ModVisitor.java:68)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
            /*
            r8 = this;
            r8.<init>();
            r8.periodDurationUs = r9;
            r8.representation = r12;
            r9 = r12.format;
            r9 = r9.containerMimeType;
            r10 = mimeTypeIsRawText(r9);
            r0 = 0;
            if (r10 == 0) goto L_0x0015;
        L_0x0012:
            r8.extractorWrapper = r0;
            goto L_0x005f;
        L_0x0015:
            r10 = "application/x-rawcc";
            r10 = r10.equals(r9);
            if (r10 == 0) goto L_0x0025;
        L_0x001d:
            r9 = new org.telegram.messenger.exoplayer2.extractor.rawcc.RawCcExtractor;
            r10 = r12.format;
            r9.<init>(r10);
            goto L_0x0056;
        L_0x0025:
            r9 = mimeTypeIsWebm(r9);
            if (r9 == 0) goto L_0x0032;
        L_0x002b:
            r9 = new org.telegram.messenger.exoplayer2.extractor.mkv.MatroskaExtractor;
            r10 = 1;
            r9.<init>(r10);
            goto L_0x0056;
        L_0x0032:
            r9 = 0;
            if (r13 == 0) goto L_0x0038;
        L_0x0035:
            r10 = 4;
            r2 = r10;
            goto L_0x0039;
        L_0x0038:
            r2 = r9;
        L_0x0039:
            if (r14 == 0) goto L_0x0047;
        L_0x003b:
            r10 = "application/cea-608";
            r9 = org.telegram.messenger.exoplayer2.Format.createTextSampleFormat(r0, r10, r9, r0);
            r9 = java.util.Collections.singletonList(r9);
        L_0x0045:
            r6 = r9;
            goto L_0x004c;
        L_0x0047:
            r9 = java.util.Collections.emptyList();
            goto L_0x0045;
        L_0x004c:
            r9 = new org.telegram.messenger.exoplayer2.extractor.mp4.FragmentedMp4Extractor;
            r3 = 0;
            r4 = 0;
            r5 = 0;
            r1 = r9;
            r7 = r15;
            r1.<init>(r2, r3, r4, r5, r6, r7);
        L_0x0056:
            r10 = new org.telegram.messenger.exoplayer2.source.chunk.ChunkExtractorWrapper;
            r13 = r12.format;
            r10.<init>(r9, r11, r13);
            r8.extractorWrapper = r10;
        L_0x005f:
            r9 = r12.getIndex();
            r8.segmentIndex = r9;
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.exoplayer2.source.dash.DefaultDashChunkSource.RepresentationHolder.<init>(long, int, org.telegram.messenger.exoplayer2.source.dash.manifest.Representation, boolean, boolean, org.telegram.messenger.exoplayer2.extractor.TrackOutput):void");
        }

        void updateRepresentation(long j, Representation representation) throws BehindLiveWindowException {
            DashSegmentIndex index = this.representation.getIndex();
            DashSegmentIndex index2 = representation.getIndex();
            this.periodDurationUs = j;
            this.representation = representation;
            if (index != null) {
                this.segmentIndex = index2;
                if (index.isExplicit() != null) {
                    j = index.getSegmentCount(this.periodDurationUs);
                    if (j != null) {
                        int firstSegmentNum = (index.getFirstSegmentNum() + j) - 1;
                        long timeUs = index.getTimeUs(firstSegmentNum) + index.getDurationUs(firstSegmentNum, this.periodDurationUs);
                        j = index2.getFirstSegmentNum();
                        long timeUs2 = index2.getTimeUs(j);
                        if (timeUs == timeUs2) {
                            this.segmentNumShift += (firstSegmentNum + 1) - j;
                        } else if (timeUs < timeUs2) {
                            throw new BehindLiveWindowException();
                        } else {
                            this.segmentNumShift += index.getSegmentNum(timeUs2, this.periodDurationUs) - j;
                        }
                    }
                }
            }
        }

        public int getFirstSegmentNum() {
            return this.segmentIndex.getFirstSegmentNum() + this.segmentNumShift;
        }

        public int getSegmentCount() {
            return this.segmentIndex.getSegmentCount(this.periodDurationUs);
        }

        public long getSegmentStartTimeUs(int i) {
            return this.segmentIndex.getTimeUs(i - this.segmentNumShift);
        }

        public long getSegmentEndTimeUs(int i) {
            return getSegmentStartTimeUs(i) + this.segmentIndex.getDurationUs(i - this.segmentNumShift, this.periodDurationUs);
        }

        public int getSegmentNum(long j) {
            return this.segmentIndex.getSegmentNum(j, this.periodDurationUs) + this.segmentNumShift;
        }

        public RangedUri getSegmentUrl(int i) {
            return this.segmentIndex.getSegmentUrl(i - this.segmentNumShift);
        }

        private static boolean mimeTypeIsWebm(String str) {
            if (!(str.startsWith(MimeTypes.VIDEO_WEBM) || str.startsWith(MimeTypes.AUDIO_WEBM))) {
                if (str.startsWith(MimeTypes.APPLICATION_WEBM) == null) {
                    return null;
                }
            }
            return true;
        }

        private static boolean mimeTypeIsRawText(String str) {
            if (!MimeTypes.isText(str)) {
                if (MimeTypes.APPLICATION_TTML.equals(str) == null) {
                    return null;
                }
            }
            return true;
        }
    }

    public static final class Factory implements org.telegram.messenger.exoplayer2.source.dash.DashChunkSource.Factory {
        private final org.telegram.messenger.exoplayer2.upstream.DataSource.Factory dataSourceFactory;
        private final int maxSegmentsPerLoad;

        public Factory(org.telegram.messenger.exoplayer2.upstream.DataSource.Factory factory) {
            this(factory, 1);
        }

        public Factory(org.telegram.messenger.exoplayer2.upstream.DataSource.Factory factory, int i) {
            this.dataSourceFactory = factory;
            this.maxSegmentsPerLoad = i;
        }

        public DashChunkSource createDashChunkSource(LoaderErrorThrower loaderErrorThrower, DashManifest dashManifest, int i, int[] iArr, TrackSelection trackSelection, int i2, long j, boolean z, boolean z2, PlayerTrackEmsgHandler playerTrackEmsgHandler) {
            return new DefaultDashChunkSource(loaderErrorThrower, dashManifest, i, iArr, trackSelection, i2, this.dataSourceFactory.createDataSource(), j, this.maxSegmentsPerLoad, z, z2, playerTrackEmsgHandler);
        }
    }

    public DefaultDashChunkSource(LoaderErrorThrower loaderErrorThrower, DashManifest dashManifest, int i, int[] iArr, TrackSelection trackSelection, int i2, DataSource dataSource, long j, int i3, boolean z, boolean z2, PlayerTrackEmsgHandler playerTrackEmsgHandler) {
        TrackSelection trackSelection2 = trackSelection;
        this.manifestLoaderErrorThrower = loaderErrorThrower;
        this.manifest = dashManifest;
        this.adaptationSetIndices = iArr;
        this.trackSelection = trackSelection2;
        int i4 = i2;
        this.trackType = i4;
        this.dataSource = dataSource;
        this.periodIndex = i;
        this.elapsedRealtimeOffsetMs = j;
        this.maxSegmentsPerLoad = i3;
        PlayerTrackEmsgHandler playerTrackEmsgHandler2 = playerTrackEmsgHandler;
        this.playerTrackEmsgHandler = playerTrackEmsgHandler2;
        long periodDurationUs = dashManifest.getPeriodDurationUs(i);
        List representations = getRepresentations();
        this.representationHolders = new RepresentationHolder[trackSelection.length()];
        for (int i5 = 0; i5 < r0.representationHolders.length; i5++) {
            r0.representationHolders[i5] = new RepresentationHolder(periodDurationUs, i4, (Representation) representations.get(trackSelection2.getIndexInTrackGroup(i5)), z, z2, playerTrackEmsgHandler2);
        }
    }

    public long getAdjustedSeekPositionUs(long j, SeekParameters seekParameters) {
        for (RepresentationHolder representationHolder : this.representationHolders) {
            if (representationHolder.segmentIndex != null) {
                int segmentNum = representationHolder.getSegmentNum(j);
                long segmentStartTimeUs = representationHolder.getSegmentStartTimeUs(segmentNum);
                long segmentStartTimeUs2 = (segmentStartTimeUs >= j || segmentNum >= representationHolder.getSegmentCount() - 1) ? segmentStartTimeUs : representationHolder.getSegmentStartTimeUs(segmentNum + 1);
                return Util.resolveSeekPositionUs(j, seekParameters, segmentStartTimeUs, segmentStartTimeUs2);
            }
        }
        return j;
    }

    public void updateManifest(DashManifest dashManifest, int i) {
        try {
            this.manifest = dashManifest;
            this.periodIndex = i;
            dashManifest = this.manifest.getPeriodDurationUs(this.periodIndex);
            List representations = getRepresentations();
            for (int i2 = 0; i2 < this.representationHolders.length; i2++) {
                this.representationHolders[i2].updateRepresentation(dashManifest, (Representation) representations.get(this.trackSelection.getIndexInTrackGroup(i2)));
            }
        } catch (DashManifest dashManifest2) {
            this.fatalError = dashManifest2;
        }
    }

    public void maybeThrowError() throws IOException {
        if (this.fatalError != null) {
            throw this.fatalError;
        }
        this.manifestLoaderErrorThrower.maybeThrowError();
    }

    public int getPreferredQueueSize(long j, List<? extends MediaChunk> list) {
        if (this.fatalError == null) {
            if (this.trackSelection.length() >= 2) {
                return this.trackSelection.evaluateQueueSize(j, list);
            }
        }
        return list.size();
    }

    public void getNextChunk(MediaChunk mediaChunk, long j, long j2, ChunkHolder chunkHolder) {
        long j3 = j;
        long j4 = j2;
        ChunkHolder chunkHolder2 = chunkHolder;
        if (this.fatalError == null) {
            long j5 = j4 - j3;
            long resolveTimeToLiveEdgeUs = resolveTimeToLiveEdgeUs(j3);
            long msToUs = (C0542C.msToUs(r0.manifest.availabilityStartTimeMs) + C0542C.msToUs(r0.manifest.getPeriod(r0.periodIndex).startMs)) + j4;
            if (r0.playerTrackEmsgHandler == null || !r0.playerTrackEmsgHandler.maybeRefreshManifestBeforeLoadingNextChunk(msToUs)) {
                r0.trackSelection.updateSelectedTrack(j3, j5, resolveTimeToLiveEdgeUs);
                RepresentationHolder representationHolder = r0.representationHolders[r0.trackSelection.getSelectedIndex()];
                if (representationHolder.extractorWrapper != null) {
                    Representation representation = representationHolder.representation;
                    RangedUri initializationUri = representationHolder.extractorWrapper.getSampleFormats() == null ? representation.getInitializationUri() : null;
                    RangedUri indexUri = representationHolder.segmentIndex == null ? representation.getIndexUri() : null;
                    if (!(initializationUri == null && indexUri == null)) {
                        chunkHolder2.chunk = newInitializationChunk(representationHolder, r0.dataSource, r0.trackSelection.getSelectedFormat(), r0.trackSelection.getSelectionReason(), r0.trackSelection.getSelectionData(), initializationUri, indexUri);
                        return;
                    }
                }
                int segmentCount = representationHolder.getSegmentCount();
                boolean z = false;
                if (segmentCount == 0) {
                    if (!r0.manifest.dynamic || r0.periodIndex < r0.manifest.getPeriodCount() - 1) {
                        z = true;
                    }
                    chunkHolder2.endOfStream = z;
                    return;
                }
                int firstSegmentNum = representationHolder.getFirstSegmentNum();
                if (segmentCount == -1) {
                    long nowUnixTimeUs = (getNowUnixTimeUs() - C0542C.msToUs(r0.manifest.availabilityStartTimeMs)) - C0542C.msToUs(r0.manifest.getPeriod(r0.periodIndex).startMs);
                    if (r0.manifest.timeShiftBufferDepthMs != C0542C.TIME_UNSET) {
                        firstSegmentNum = Math.max(firstSegmentNum, representationHolder.getSegmentNum(nowUnixTimeUs - C0542C.msToUs(r0.manifest.timeShiftBufferDepthMs)));
                    }
                    segmentCount = representationHolder.getSegmentNum(nowUnixTimeUs) - 1;
                } else {
                    segmentCount = (segmentCount + firstSegmentNum) - 1;
                }
                updateLiveEdgeTimeUs(representationHolder, segmentCount);
                if (mediaChunk == null) {
                    firstSegmentNum = Util.constrainValue(representationHolder.getSegmentNum(j4), firstSegmentNum, segmentCount);
                } else {
                    int nextChunkIndex = mediaChunk.getNextChunkIndex();
                    if (nextChunkIndex < firstSegmentNum) {
                        r0.fatalError = new BehindLiveWindowException();
                        return;
                    }
                    firstSegmentNum = nextChunkIndex;
                }
                if (firstSegmentNum <= segmentCount) {
                    if (!r0.missingLastSegment || firstSegmentNum < segmentCount) {
                        chunkHolder2.chunk = newMediaChunk(representationHolder, r0.dataSource, r0.trackType, r0.trackSelection.getSelectedFormat(), r0.trackSelection.getSelectionReason(), r0.trackSelection.getSelectionData(), firstSegmentNum, Math.min(r0.maxSegmentsPerLoad, (segmentCount - firstSegmentNum) + 1));
                        return;
                    }
                }
                if (!r0.manifest.dynamic || r0.periodIndex < r0.manifest.getPeriodCount() - 1) {
                    z = true;
                }
                chunkHolder2.endOfStream = z;
            }
        }
    }

    public void onChunkLoadCompleted(Chunk chunk) {
        if (chunk instanceof InitializationChunk) {
            RepresentationHolder representationHolder = this.representationHolders[this.trackSelection.indexOf(((InitializationChunk) chunk).trackFormat)];
            if (representationHolder.segmentIndex == null) {
                SeekMap seekMap = representationHolder.extractorWrapper.getSeekMap();
                if (seekMap != null) {
                    representationHolder.segmentIndex = new DashWrappingSegmentIndex((ChunkIndex) seekMap);
                }
            }
        }
        if (this.playerTrackEmsgHandler != null) {
            this.playerTrackEmsgHandler.onChunkLoadCompleted(chunk);
        }
    }

    public boolean onChunkLoadError(Chunk chunk, boolean z, Exception exception) {
        if (!z) {
            return null;
        }
        if (this.playerTrackEmsgHandler && this.playerTrackEmsgHandler.maybeRefreshManifestOnLoadingError(chunk)) {
            return true;
        }
        if (!this.manifest.dynamic && (chunk instanceof MediaChunk) && (exception instanceof InvalidResponseCodeException) && ((InvalidResponseCodeException) exception).responseCode) {
            z = this.representationHolders[this.trackSelection.indexOf(chunk.trackFormat)];
            int segmentCount = z.getSegmentCount();
            if (!(segmentCount == -1 || segmentCount == 0 || ((MediaChunk) chunk).getNextChunkIndex() <= (z.getFirstSegmentNum() + segmentCount) - true)) {
                this.missingLastSegment = true;
                return true;
            }
        }
        return ChunkedTrackBlacklistUtil.maybeBlacklistTrack(this.trackSelection, this.trackSelection.indexOf(chunk.trackFormat), exception);
    }

    private ArrayList<Representation> getRepresentations() {
        List list = this.manifest.getPeriod(this.periodIndex).adaptationSets;
        ArrayList<Representation> arrayList = new ArrayList();
        for (int i : this.adaptationSetIndices) {
            arrayList.addAll(((AdaptationSet) list.get(i)).representations);
        }
        return arrayList;
    }

    private void updateLiveEdgeTimeUs(RepresentationHolder representationHolder, int i) {
        this.liveEdgeTimeUs = this.manifest.dynamic ? representationHolder.getSegmentEndTimeUs(i) : 1;
    }

    private long getNowUnixTimeUs() {
        if (this.elapsedRealtimeOffsetMs != 0) {
            return (SystemClock.elapsedRealtime() + this.elapsedRealtimeOffsetMs) * 1000;
        }
        return System.currentTimeMillis() * 1000;
    }

    private long resolveTimeToLiveEdgeUs(long j) {
        Object obj = (!this.manifest.dynamic || this.liveEdgeTimeUs == C0542C.TIME_UNSET) ? null : 1;
        if (obj != null) {
            return this.liveEdgeTimeUs - j;
        }
        return C0542C.TIME_UNSET;
    }

    protected static Chunk newInitializationChunk(RepresentationHolder representationHolder, DataSource dataSource, Format format, int i, Object obj, RangedUri rangedUri, RangedUri rangedUri2) {
        String str = representationHolder.representation.baseUrl;
        if (rangedUri != null) {
            rangedUri2 = rangedUri.attemptMerge(rangedUri2, str);
            if (rangedUri2 == null) {
                rangedUri2 = rangedUri;
            }
        }
        return new InitializationChunk(dataSource, new DataSpec(rangedUri2.resolveUri(str), rangedUri2.start, rangedUri2.length, representationHolder.representation.getCacheKey()), format, i, obj, representationHolder.extractorWrapper);
    }

    protected static Chunk newMediaChunk(RepresentationHolder representationHolder, DataSource dataSource, int i, Format format, int i2, Object obj, int i3, int i4) {
        RepresentationHolder representationHolder2 = representationHolder;
        int i5 = i3;
        Representation representation = representationHolder2.representation;
        long segmentStartTimeUs = representationHolder2.getSegmentStartTimeUs(i5);
        RangedUri segmentUrl = representationHolder2.getSegmentUrl(i5);
        String str = representation.baseUrl;
        if (representationHolder2.extractorWrapper == null) {
            return new SingleSampleMediaChunk(dataSource, new DataSpec(segmentUrl.resolveUri(str), segmentUrl.start, segmentUrl.length, representation.getCacheKey()), format, i2, obj, segmentStartTimeUs, representationHolder2.getSegmentEndTimeUs(i5), i5, i, format);
        }
        RangedUri rangedUri = segmentUrl;
        int i6 = 1;
        int i7 = i6;
        int i8 = i4;
        while (i6 < i8) {
            RangedUri attemptMerge = rangedUri.attemptMerge(representationHolder2.getSegmentUrl(i5 + i6), str);
            if (attemptMerge == null) {
                break;
            }
            i7++;
            i6++;
            rangedUri = attemptMerge;
        }
        long segmentEndTimeUs = representationHolder2.getSegmentEndTimeUs((i5 + i7) - 1);
        Uri resolveUri = rangedUri.resolveUri(str);
        long j = rangedUri.start;
        return new ContainerMediaChunk(dataSource, new DataSpec(resolveUri, j, rangedUri.length, representation.getCacheKey()), format, i2, obj, segmentStartTimeUs, segmentEndTimeUs, i5, i7, -representation.presentationTimeOffsetUs, representationHolder2.extractorWrapper);
    }
}
