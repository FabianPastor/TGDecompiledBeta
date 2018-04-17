package org.telegram.messenger.exoplayer2.source;

import java.io.IOException;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import org.telegram.messenger.exoplayer2.C0539C;
import org.telegram.messenger.exoplayer2.SeekParameters;
import org.telegram.messenger.exoplayer2.source.MediaPeriod.Callback;
import org.telegram.messenger.exoplayer2.trackselection.TrackSelection;
import org.telegram.messenger.exoplayer2.util.Assertions;

final class MergingMediaPeriod implements MediaPeriod, Callback {
    private Callback callback;
    private SequenceableLoader compositeSequenceableLoader;
    private final CompositeSequenceableLoaderFactory compositeSequenceableLoaderFactory;
    private MediaPeriod[] enabledPeriods;
    private int pendingChildPrepareCount;
    public final MediaPeriod[] periods;
    private final IdentityHashMap<SampleStream, Integer> streamPeriodIndices = new IdentityHashMap();
    private TrackGroupArray trackGroups;

    public MergingMediaPeriod(CompositeSequenceableLoaderFactory compositeSequenceableLoaderFactory, MediaPeriod... periods) {
        this.compositeSequenceableLoaderFactory = compositeSequenceableLoaderFactory;
        this.periods = periods;
    }

    public void prepare(Callback callback, long positionUs) {
        this.callback = callback;
        this.pendingChildPrepareCount = this.periods.length;
        for (MediaPeriod period : this.periods) {
            period.prepare(this, positionUs);
        }
    }

    public void maybeThrowPrepareError() throws IOException {
        for (MediaPeriod period : this.periods) {
            period.maybeThrowPrepareError();
        }
    }

    public TrackGroupArray getTrackGroups() {
        return this.trackGroups;
    }

    public long selectTracks(TrackSelection[] selections, boolean[] mayRetainStreamFlags, SampleStream[] streams, boolean[] streamResetFlags, long positionUs) {
        MergingMediaPeriod mergingMediaPeriod = this;
        TrackSelection[] trackSelectionArr = selections;
        SampleStream[] sampleStreamArr = streams;
        int[] streamChildIndices = new int[trackSelectionArr.length];
        int[] selectionChildIndices = new int[trackSelectionArr.length];
        for (int i = 0; i < trackSelectionArr.length; i++) {
            streamChildIndices[i] = sampleStreamArr[i] == null ? -1 : ((Integer) mergingMediaPeriod.streamPeriodIndices.get(sampleStreamArr[i])).intValue();
            selectionChildIndices[i] = -1;
            if (trackSelectionArr[i] != null) {
                TrackGroup trackGroup = trackSelectionArr[i].getTrackGroup();
                for (int j = 0; j < mergingMediaPeriod.periods.length; j++) {
                    if (mergingMediaPeriod.periods[j].getTrackGroups().indexOf(trackGroup) != -1) {
                        selectionChildIndices[i] = j;
                        break;
                    }
                }
            }
        }
        mergingMediaPeriod.streamPeriodIndices.clear();
        SampleStream[] newStreams = new SampleStream[trackSelectionArr.length];
        SampleStream[] childStreams = new SampleStream[trackSelectionArr.length];
        TrackSelection[] childSelections = new TrackSelection[trackSelectionArr.length];
        ArrayList<MediaPeriod> enabledPeriodsList = new ArrayList(mergingMediaPeriod.periods.length);
        long positionUs2 = positionUs;
        int i2 = 0;
        while (true) {
            int i3 = i2;
            ArrayList<MediaPeriod> enabledPeriodsList2;
            TrackSelection[] childSelections2;
            if (i3 < mergingMediaPeriod.periods.length) {
                i2 = 0;
                while (i2 < trackSelectionArr.length) {
                    TrackSelection trackSelection = null;
                    childStreams[i2] = streamChildIndices[i2] == i3 ? sampleStreamArr[i2] : null;
                    if (selectionChildIndices[i2] == i3) {
                        trackSelection = trackSelectionArr[i2];
                    }
                    childSelections[i2] = trackSelection;
                    i2++;
                }
                enabledPeriodsList2 = enabledPeriodsList;
                childSelections2 = childSelections;
                int i4 = i3;
                long selectPositionUs = mergingMediaPeriod.periods[i3].selectTracks(childSelections, mayRetainStreamFlags, childStreams, streamResetFlags, positionUs2);
                if (i4 == 0) {
                    positionUs2 = selectPositionUs;
                } else if (selectPositionUs != positionUs2) {
                    throw new IllegalStateException("Children enabled at different positions");
                }
                boolean periodEnabled = false;
                for (int j2 = 0; j2 < trackSelectionArr.length; j2++) {
                    boolean z = true;
                    if (selectionChildIndices[j2] == i4) {
                        if (childStreams[j2] == null) {
                            z = false;
                        }
                        Assertions.checkState(z);
                        newStreams[j2] = childStreams[j2];
                        periodEnabled = true;
                        mergingMediaPeriod.streamPeriodIndices.put(childStreams[j2], Integer.valueOf(i4));
                    } else if (streamChildIndices[j2] == i4) {
                        if (childStreams[j2] != null) {
                            z = false;
                        }
                        Assertions.checkState(z);
                    }
                }
                if (periodEnabled) {
                    enabledPeriodsList2.add(mergingMediaPeriod.periods[i4]);
                }
                i2 = i4 + 1;
                enabledPeriodsList = enabledPeriodsList2;
                childSelections = childSelections2;
            } else {
                enabledPeriodsList2 = enabledPeriodsList;
                childSelections2 = childSelections;
                System.arraycopy(newStreams, 0, sampleStreamArr, 0, newStreams.length);
                mergingMediaPeriod.enabledPeriods = new MediaPeriod[enabledPeriodsList2.size()];
                enabledPeriodsList2.toArray(mergingMediaPeriod.enabledPeriods);
                mergingMediaPeriod.compositeSequenceableLoader = mergingMediaPeriod.compositeSequenceableLoaderFactory.createCompositeSequenceableLoader(mergingMediaPeriod.enabledPeriods);
                return positionUs2;
            }
        }
    }

    public void discardBuffer(long positionUs, boolean toKeyframe) {
        for (MediaPeriod period : this.enabledPeriods) {
            period.discardBuffer(positionUs, toKeyframe);
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
        long positionUs = this.periods[0].readDiscontinuity();
        for (int i = 1; i < this.periods.length; i++) {
            if (this.periods[i].readDiscontinuity() != C0539C.TIME_UNSET) {
                throw new IllegalStateException("Child reported discontinuity");
            }
        }
        if (positionUs != C0539C.TIME_UNSET) {
            MediaPeriod[] mediaPeriodArr = this.enabledPeriods;
            int length = mediaPeriodArr.length;
            int i2 = 0;
            while (i2 < length) {
                MediaPeriod enabledPeriod = mediaPeriodArr[i2];
                if (enabledPeriod == this.periods[0] || enabledPeriod.seekToUs(positionUs) == positionUs) {
                    i2++;
                } else {
                    throw new IllegalStateException("Children seeked to different positions");
                }
            }
        }
        return positionUs;
    }

    public long getBufferedPositionUs() {
        return this.compositeSequenceableLoader.getBufferedPositionUs();
    }

    public long seekToUs(long positionUs) {
        positionUs = this.enabledPeriods[0].seekToUs(positionUs);
        for (int i = 1; i < this.enabledPeriods.length; i++) {
            if (this.enabledPeriods[i].seekToUs(positionUs) != positionUs) {
                throw new IllegalStateException("Children seeked to different positions");
            }
        }
        return positionUs;
    }

    public long getAdjustedSeekPositionUs(long positionUs, SeekParameters seekParameters) {
        return this.enabledPeriods[0].getAdjustedSeekPositionUs(positionUs, seekParameters);
    }

    public void onPrepared(MediaPeriod ignored) {
        int i = this.pendingChildPrepareCount - 1;
        this.pendingChildPrepareCount = i;
        if (i <= 0) {
            int totalTrackGroupCount = 0;
            for (MediaPeriod period : this.periods) {
                totalTrackGroupCount += period.getTrackGroups().length;
            }
            TrackGroup[] trackGroupArray = new TrackGroup[totalTrackGroupCount];
            MediaPeriod[] mediaPeriodArr = this.periods;
            int length = mediaPeriodArr.length;
            int trackGroupIndex = 0;
            int trackGroupIndex2 = 0;
            while (trackGroupIndex2 < length) {
                TrackGroupArray periodTrackGroups = mediaPeriodArr[trackGroupIndex2].getTrackGroups();
                int periodTrackGroupCount = periodTrackGroups.length;
                int trackGroupIndex3 = trackGroupIndex;
                trackGroupIndex = 0;
                while (trackGroupIndex < periodTrackGroupCount) {
                    int trackGroupIndex4 = trackGroupIndex3 + 1;
                    trackGroupArray[trackGroupIndex3] = periodTrackGroups.get(trackGroupIndex);
                    trackGroupIndex++;
                    trackGroupIndex3 = trackGroupIndex4;
                }
                trackGroupIndex2++;
                trackGroupIndex = trackGroupIndex3;
            }
            this.trackGroups = new TrackGroupArray(trackGroupArray);
            this.callback.onPrepared(this);
        }
    }

    public void onContinueLoadingRequested(MediaPeriod ignored) {
        if (this.trackGroups != null) {
            this.callback.onContinueLoadingRequested(this);
        }
    }
}
