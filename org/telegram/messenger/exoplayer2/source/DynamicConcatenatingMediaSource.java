package org.telegram.messenger.exoplayer2.source;

import android.os.Handler;
import android.os.Looper;
import android.util.SparseIntArray;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import org.telegram.messenger.exoplayer2.C;
import org.telegram.messenger.exoplayer2.ExoPlaybackException;
import org.telegram.messenger.exoplayer2.ExoPlayer;
import org.telegram.messenger.exoplayer2.PlayerMessage.Target;
import org.telegram.messenger.exoplayer2.Timeline;
import org.telegram.messenger.exoplayer2.Timeline.Period;
import org.telegram.messenger.exoplayer2.Timeline.Window;
import org.telegram.messenger.exoplayer2.source.MediaSource.Listener;
import org.telegram.messenger.exoplayer2.source.MediaSource.MediaPeriodId;
import org.telegram.messenger.exoplayer2.source.ShuffleOrder.DefaultShuffleOrder;
import org.telegram.messenger.exoplayer2.upstream.Allocator;
import org.telegram.messenger.exoplayer2.util.Assertions;
import org.telegram.messenger.exoplayer2.util.Util;

public final class DynamicConcatenatingMediaSource implements Target, MediaSource {
    private static final int MSG_ADD = 0;
    private static final int MSG_ADD_MULTIPLE = 1;
    private static final int MSG_MOVE = 3;
    private static final int MSG_ON_COMPLETION = 4;
    private static final int MSG_REMOVE = 2;
    private final List<DeferredMediaPeriod> deferredMediaPeriods;
    private Listener listener;
    private final Map<MediaPeriod, MediaSource> mediaSourceByMediaPeriod;
    private final List<MediaSourceHolder> mediaSourceHolders;
    private final List<MediaSource> mediaSourcesPublic;
    private int periodCount;
    private ExoPlayer player;
    private boolean preventListenerNotification;
    private final MediaSourceHolder query;
    private ShuffleOrder shuffleOrder;
    private int windowCount;

    private static final class EventDispatcher {
        public final Handler eventHandler;
        public final Runnable runnable;

        public EventDispatcher(Runnable runnable) {
            Looper myLooper;
            this.runnable = runnable;
            if (Looper.myLooper() != null) {
                myLooper = Looper.myLooper();
            } else {
                myLooper = Looper.getMainLooper();
            }
            this.eventHandler = new Handler(myLooper);
        }

        public void dispatchEvent() {
            this.eventHandler.post(this.runnable);
        }
    }

    private static final class MediaSourceHolder implements Comparable<MediaSourceHolder> {
        public int firstPeriodIndexInChild;
        public int firstWindowIndexInChild;
        public boolean isPrepared;
        public final MediaSource mediaSource;
        public DeferredTimeline timeline;
        public final Object uid;

        public MediaSourceHolder(MediaSource mediaSource, DeferredTimeline timeline, int window, int period, Object uid) {
            this.mediaSource = mediaSource;
            this.timeline = timeline;
            this.firstWindowIndexInChild = window;
            this.firstPeriodIndexInChild = period;
            this.uid = uid;
        }

        public int compareTo(MediaSourceHolder other) {
            return this.firstPeriodIndexInChild - other.firstPeriodIndexInChild;
        }
    }

    private static final class MessageData<CustomType> {
        public final EventDispatcher actionOnCompletion;
        public final CustomType customData;
        public final int index;

        public MessageData(int index, CustomType customData, Runnable actionOnCompletion) {
            this.index = index;
            this.actionOnCompletion = actionOnCompletion != null ? new EventDispatcher(actionOnCompletion) : null;
            this.customData = customData;
        }
    }

    private static final class DeferredTimeline extends Timeline {
        private static final Object DUMMY_ID = new Object();
        private static final Period period = new Period();
        private final Object replacedID;
        private final Timeline timeline;

        public DeferredTimeline() {
            this.timeline = null;
            this.replacedID = null;
        }

        private DeferredTimeline(Timeline timeline, Object replacedID) {
            this.timeline = timeline;
            this.replacedID = replacedID;
        }

        public DeferredTimeline cloneWithNewTimeline(Timeline timeline) {
            Object obj = (this.replacedID != null || timeline.getPeriodCount() <= 0) ? this.replacedID : timeline.getPeriod(0, period, true).uid;
            return new DeferredTimeline(timeline, obj);
        }

        public Timeline getTimeline() {
            return this.timeline;
        }

        public int getWindowCount() {
            return this.timeline == null ? 1 : this.timeline.getWindowCount();
        }

        public Window getWindow(int windowIndex, Window window, boolean setIds, long defaultPositionProjectionUs) {
            if (this.timeline != null) {
                return this.timeline.getWindow(windowIndex, window, setIds, defaultPositionProjectionUs);
            }
            return window.set(setIds ? DUMMY_ID : null, C.TIME_UNSET, C.TIME_UNSET, false, true, 0, C.TIME_UNSET, 0, 0, 0);
        }

        public int getPeriodCount() {
            return this.timeline == null ? 1 : this.timeline.getPeriodCount();
        }

        public Period getPeriod(int periodIndex, Period period, boolean setIds) {
            Object obj = null;
            if (this.timeline == null) {
                Object obj2;
                if (setIds) {
                    obj2 = DUMMY_ID;
                } else {
                    obj2 = null;
                }
                if (setIds) {
                    obj = DUMMY_ID;
                }
                return period.set(obj2, obj, 0, C.TIME_UNSET, C.TIME_UNSET);
            }
            this.timeline.getPeriod(periodIndex, period, setIds);
            if (period.uid != this.replacedID) {
                return period;
            }
            period.uid = DUMMY_ID;
            return period;
        }

        public int getIndexOfPeriod(Object uid) {
            if (this.timeline == null) {
                return uid == DUMMY_ID ? 0 : -1;
            } else {
                Timeline timeline = this.timeline;
                if (uid == DUMMY_ID) {
                    uid = this.replacedID;
                }
                return timeline.getIndexOfPeriod(uid);
            }
        }
    }

    private static final class ConcatenatedTimeline extends AbstractConcatenatedTimeline {
        private final SparseIntArray childIndexByUid = new SparseIntArray();
        private final int[] firstPeriodInChildIndices;
        private final int[] firstWindowInChildIndices;
        private final int periodCount;
        private final Timeline[] timelines;
        private final int[] uids;
        private final int windowCount;

        public ConcatenatedTimeline(Collection<MediaSourceHolder> mediaSourceHolders, int windowCount, int periodCount, ShuffleOrder shuffleOrder) {
            super(shuffleOrder);
            this.windowCount = windowCount;
            this.periodCount = periodCount;
            int childCount = mediaSourceHolders.size();
            this.firstPeriodInChildIndices = new int[childCount];
            this.firstWindowInChildIndices = new int[childCount];
            this.timelines = new Timeline[childCount];
            this.uids = new int[childCount];
            int index = 0;
            for (MediaSourceHolder mediaSourceHolder : mediaSourceHolders) {
                this.timelines[index] = mediaSourceHolder.timeline;
                this.firstPeriodInChildIndices[index] = mediaSourceHolder.firstPeriodIndexInChild;
                this.firstWindowInChildIndices[index] = mediaSourceHolder.firstWindowIndexInChild;
                this.uids[index] = ((Integer) mediaSourceHolder.uid).intValue();
                int index2 = index + 1;
                this.childIndexByUid.put(this.uids[index], index);
                index = index2;
            }
        }

        protected int getChildIndexByPeriodIndex(int periodIndex) {
            return Util.binarySearchFloor(this.firstPeriodInChildIndices, periodIndex + 1, false, false);
        }

        protected int getChildIndexByWindowIndex(int windowIndex) {
            return Util.binarySearchFloor(this.firstWindowInChildIndices, windowIndex + 1, false, false);
        }

        protected int getChildIndexByChildUid(Object childUid) {
            if (!(childUid instanceof Integer)) {
                return -1;
            }
            int index = this.childIndexByUid.get(((Integer) childUid).intValue(), -1);
            if (index == -1) {
                index = -1;
            }
            return index;
        }

        protected Timeline getTimelineByChildIndex(int childIndex) {
            return this.timelines[childIndex];
        }

        protected int getFirstPeriodIndexByChildIndex(int childIndex) {
            return this.firstPeriodInChildIndices[childIndex];
        }

        protected int getFirstWindowIndexByChildIndex(int childIndex) {
            return this.firstWindowInChildIndices[childIndex];
        }

        protected Object getChildUidByChildIndex(int childIndex) {
            return Integer.valueOf(this.uids[childIndex]);
        }

        public int getWindowCount() {
            return this.windowCount;
        }

        public int getPeriodCount() {
            return this.periodCount;
        }
    }

    public DynamicConcatenatingMediaSource() {
        this(new DefaultShuffleOrder(0));
    }

    public DynamicConcatenatingMediaSource(ShuffleOrder shuffleOrder) {
        this.shuffleOrder = shuffleOrder;
        this.mediaSourceByMediaPeriod = new IdentityHashMap();
        this.mediaSourcesPublic = new ArrayList();
        this.mediaSourceHolders = new ArrayList();
        this.deferredMediaPeriods = new ArrayList(1);
        this.query = new MediaSourceHolder(null, null, -1, -1, Integer.valueOf(-1));
    }

    public synchronized void addMediaSource(MediaSource mediaSource) {
        addMediaSource(this.mediaSourcesPublic.size(), mediaSource, null);
    }

    public synchronized void addMediaSource(MediaSource mediaSource, Runnable actionOnCompletion) {
        addMediaSource(this.mediaSourcesPublic.size(), mediaSource, actionOnCompletion);
    }

    public synchronized void addMediaSource(int index, MediaSource mediaSource) {
        addMediaSource(index, mediaSource, null);
    }

    public synchronized void addMediaSource(int index, MediaSource mediaSource, Runnable actionOnCompletion) {
        boolean z = false;
        synchronized (this) {
            Assertions.checkNotNull(mediaSource);
            if (!this.mediaSourcesPublic.contains(mediaSource)) {
                z = true;
            }
            Assertions.checkArgument(z);
            this.mediaSourcesPublic.add(index, mediaSource);
            if (this.player != null) {
                this.player.createMessage(this).setType(0).setPayload(new MessageData(index, mediaSource, actionOnCompletion)).send();
            } else if (actionOnCompletion != null) {
                actionOnCompletion.run();
            }
        }
    }

    public synchronized void addMediaSources(Collection<MediaSource> mediaSources) {
        addMediaSources(this.mediaSourcesPublic.size(), mediaSources, null);
    }

    public synchronized void addMediaSources(Collection<MediaSource> mediaSources, Runnable actionOnCompletion) {
        addMediaSources(this.mediaSourcesPublic.size(), mediaSources, actionOnCompletion);
    }

    public synchronized void addMediaSources(int index, Collection<MediaSource> mediaSources) {
        addMediaSources(index, mediaSources, null);
    }

    public synchronized void addMediaSources(int index, Collection<MediaSource> mediaSources, Runnable actionOnCompletion) {
        for (MediaSource mediaSource : mediaSources) {
            Assertions.checkNotNull(mediaSource);
            Assertions.checkArgument(!this.mediaSourcesPublic.contains(mediaSource));
        }
        this.mediaSourcesPublic.addAll(index, mediaSources);
        if (this.player != null && !mediaSources.isEmpty()) {
            this.player.createMessage(this).setType(1).setPayload(new MessageData(index, mediaSources, actionOnCompletion)).send();
        } else if (actionOnCompletion != null) {
            actionOnCompletion.run();
        }
    }

    public synchronized void removeMediaSource(int index) {
        removeMediaSource(index, null);
    }

    public synchronized void removeMediaSource(int index, Runnable actionOnCompletion) {
        this.mediaSourcesPublic.remove(index);
        if (this.player != null) {
            this.player.createMessage(this).setType(2).setPayload(new MessageData(index, null, actionOnCompletion)).send();
        } else if (actionOnCompletion != null) {
            actionOnCompletion.run();
        }
    }

    public synchronized void moveMediaSource(int currentIndex, int newIndex) {
        moveMediaSource(currentIndex, newIndex, null);
    }

    public synchronized void moveMediaSource(int currentIndex, int newIndex, Runnable actionOnCompletion) {
        if (currentIndex != newIndex) {
            this.mediaSourcesPublic.add(newIndex, this.mediaSourcesPublic.remove(currentIndex));
            if (this.player != null) {
                this.player.createMessage(this).setType(3).setPayload(new MessageData(currentIndex, Integer.valueOf(newIndex), actionOnCompletion)).send();
            } else if (actionOnCompletion != null) {
                actionOnCompletion.run();
            }
        }
    }

    public synchronized int getSize() {
        return this.mediaSourcesPublic.size();
    }

    public synchronized MediaSource getMediaSource(int index) {
        return (MediaSource) this.mediaSourcesPublic.get(index);
    }

    public synchronized void prepareSource(ExoPlayer player, boolean isTopLevelSource, Listener listener) {
        boolean z = true;
        synchronized (this) {
            if (this.listener != null) {
                z = false;
            }
            Assertions.checkState(z, MediaSource.MEDIA_SOURCE_REUSED_ERROR_MESSAGE);
            this.player = player;
            this.listener = listener;
            this.preventListenerNotification = true;
            this.shuffleOrder = this.shuffleOrder.cloneAndInsert(0, this.mediaSourcesPublic.size());
            addMediaSourcesInternal(0, this.mediaSourcesPublic);
            this.preventListenerNotification = false;
            maybeNotifyListener(null);
        }
    }

    public void maybeThrowSourceInfoRefreshError() throws IOException {
        for (int i = 0; i < this.mediaSourceHolders.size(); i++) {
            ((MediaSourceHolder) this.mediaSourceHolders.get(i)).mediaSource.maybeThrowSourceInfoRefreshError();
        }
    }

    public MediaPeriod createPeriod(MediaPeriodId id, Allocator allocator) {
        MediaPeriod mediaPeriod;
        MediaSourceHolder holder = (MediaSourceHolder) this.mediaSourceHolders.get(findMediaSourceHolderByPeriodIndex(id.periodIndex));
        MediaPeriodId idInSource = id.copyWithPeriodIndex(id.periodIndex - holder.firstPeriodIndexInChild);
        if (holder.isPrepared) {
            mediaPeriod = holder.mediaSource.createPeriod(idInSource, allocator);
        } else {
            mediaPeriod = new DeferredMediaPeriod(holder.mediaSource, idInSource, allocator);
            this.deferredMediaPeriods.add((DeferredMediaPeriod) mediaPeriod);
        }
        this.mediaSourceByMediaPeriod.put(mediaPeriod, holder.mediaSource);
        return mediaPeriod;
    }

    public void releasePeriod(MediaPeriod mediaPeriod) {
        MediaSource mediaSource = (MediaSource) this.mediaSourceByMediaPeriod.get(mediaPeriod);
        this.mediaSourceByMediaPeriod.remove(mediaPeriod);
        if (mediaPeriod instanceof DeferredMediaPeriod) {
            this.deferredMediaPeriods.remove(mediaPeriod);
            ((DeferredMediaPeriod) mediaPeriod).releasePeriod();
            return;
        }
        mediaSource.releasePeriod(mediaPeriod);
    }

    public void releaseSource() {
        for (int i = 0; i < this.mediaSourceHolders.size(); i++) {
            ((MediaSourceHolder) this.mediaSourceHolders.get(i)).mediaSource.releaseSource();
        }
    }

    public void handleMessage(int messageType, Object message) throws ExoPlaybackException {
        if (messageType == 4) {
            ((EventDispatcher) message).dispatchEvent();
            return;
        }
        EventDispatcher actionOnCompletion;
        this.preventListenerNotification = true;
        switch (messageType) {
            case 0:
                MessageData<MediaSource> messageData = (MessageData) message;
                this.shuffleOrder = this.shuffleOrder.cloneAndInsert(messageData.index, 1);
                addMediaSourceInternal(messageData.index, (MediaSource) messageData.customData);
                actionOnCompletion = messageData.actionOnCompletion;
                break;
            case 1:
                MessageData<Collection<MediaSource>> messageData2 = (MessageData) message;
                this.shuffleOrder = this.shuffleOrder.cloneAndInsert(messageData2.index, ((Collection) messageData2.customData).size());
                addMediaSourcesInternal(messageData2.index, (Collection) messageData2.customData);
                actionOnCompletion = messageData2.actionOnCompletion;
                break;
            case 2:
                MessageData<Void> messageData3 = (MessageData) message;
                this.shuffleOrder = this.shuffleOrder.cloneAndRemove(messageData3.index);
                removeMediaSourceInternal(messageData3.index);
                actionOnCompletion = messageData3.actionOnCompletion;
                break;
            case 3:
                MessageData<Integer> messageData4 = (MessageData) message;
                this.shuffleOrder = this.shuffleOrder.cloneAndRemove(messageData4.index);
                this.shuffleOrder = this.shuffleOrder.cloneAndInsert(((Integer) messageData4.customData).intValue(), 1);
                moveMediaSourceInternal(messageData4.index, ((Integer) messageData4.customData).intValue());
                actionOnCompletion = messageData4.actionOnCompletion;
                break;
            default:
                throw new IllegalStateException();
        }
        this.preventListenerNotification = false;
        maybeNotifyListener(actionOnCompletion);
    }

    private void maybeNotifyListener(EventDispatcher actionOnCompletion) {
        if (!this.preventListenerNotification) {
            this.listener.onSourceInfoRefreshed(this, new ConcatenatedTimeline(this.mediaSourceHolders, this.windowCount, this.periodCount, this.shuffleOrder), null);
            if (actionOnCompletion != null) {
                this.player.createMessage(this).setType(4).setPayload(actionOnCompletion).send();
            }
        }
    }

    private void addMediaSourceInternal(int newIndex, MediaSource newMediaSource) {
        MediaSourceHolder newMediaSourceHolder;
        Integer newUid = Integer.valueOf(System.identityHashCode(newMediaSource));
        DeferredTimeline newTimeline = new DeferredTimeline();
        if (newIndex > 0) {
            MediaSourceHolder previousHolder = (MediaSourceHolder) this.mediaSourceHolders.get(newIndex - 1);
            newMediaSourceHolder = new MediaSourceHolder(newMediaSource, newTimeline, previousHolder.timeline.getWindowCount() + previousHolder.firstWindowIndexInChild, previousHolder.timeline.getPeriodCount() + previousHolder.firstPeriodIndexInChild, newUid);
        } else {
            newMediaSourceHolder = new MediaSourceHolder(newMediaSource, newTimeline, 0, 0, newUid);
        }
        correctOffsets(newIndex, newTimeline.getWindowCount(), newTimeline.getPeriodCount());
        this.mediaSourceHolders.add(newIndex, newMediaSourceHolder);
        newMediaSourceHolder.mediaSource.prepareSource(this.player, false, new Listener() {
            public void onSourceInfoRefreshed(MediaSource source, Timeline newTimeline, Object manifest) {
                DynamicConcatenatingMediaSource.this.updateMediaSourceInternal(newMediaSourceHolder, newTimeline);
            }
        });
    }

    private void addMediaSourcesInternal(int index, Collection<MediaSource> mediaSources) {
        for (MediaSource mediaSource : mediaSources) {
            int index2 = index + 1;
            addMediaSourceInternal(index, mediaSource);
            index = index2;
        }
    }

    private void updateMediaSourceInternal(MediaSourceHolder mediaSourceHolder, Timeline timeline) {
        if (mediaSourceHolder == null) {
            throw new IllegalArgumentException();
        }
        DeferredTimeline deferredTimeline = mediaSourceHolder.timeline;
        if (deferredTimeline.getTimeline() != timeline) {
            int windowOffsetUpdate = timeline.getWindowCount() - deferredTimeline.getWindowCount();
            int periodOffsetUpdate = timeline.getPeriodCount() - deferredTimeline.getPeriodCount();
            if (!(windowOffsetUpdate == 0 && periodOffsetUpdate == 0)) {
                correctOffsets(findMediaSourceHolderByPeriodIndex(mediaSourceHolder.firstPeriodIndexInChild) + 1, windowOffsetUpdate, periodOffsetUpdate);
            }
            mediaSourceHolder.timeline = deferredTimeline.cloneWithNewTimeline(timeline);
            if (!mediaSourceHolder.isPrepared) {
                for (int i = this.deferredMediaPeriods.size() - 1; i >= 0; i--) {
                    if (((DeferredMediaPeriod) this.deferredMediaPeriods.get(i)).mediaSource == mediaSourceHolder.mediaSource) {
                        ((DeferredMediaPeriod) this.deferredMediaPeriods.get(i)).createPeriod();
                        this.deferredMediaPeriods.remove(i);
                    }
                }
            }
            mediaSourceHolder.isPrepared = true;
            maybeNotifyListener(null);
        }
    }

    private void removeMediaSourceInternal(int index) {
        MediaSourceHolder holder = (MediaSourceHolder) this.mediaSourceHolders.get(index);
        this.mediaSourceHolders.remove(index);
        Timeline oldTimeline = holder.timeline;
        correctOffsets(index, -oldTimeline.getWindowCount(), -oldTimeline.getPeriodCount());
        holder.mediaSource.releaseSource();
    }

    private void moveMediaSourceInternal(int currentIndex, int newIndex) {
        int startIndex = Math.min(currentIndex, newIndex);
        int endIndex = Math.max(currentIndex, newIndex);
        int windowOffset = ((MediaSourceHolder) this.mediaSourceHolders.get(startIndex)).firstWindowIndexInChild;
        int periodOffset = ((MediaSourceHolder) this.mediaSourceHolders.get(startIndex)).firstPeriodIndexInChild;
        this.mediaSourceHolders.add(newIndex, this.mediaSourceHolders.remove(currentIndex));
        for (int i = startIndex; i <= endIndex; i++) {
            MediaSourceHolder holder = (MediaSourceHolder) this.mediaSourceHolders.get(i);
            holder.firstWindowIndexInChild = windowOffset;
            holder.firstPeriodIndexInChild = periodOffset;
            windowOffset += holder.timeline.getWindowCount();
            periodOffset += holder.timeline.getPeriodCount();
        }
    }

    private void correctOffsets(int startIndex, int windowOffsetUpdate, int periodOffsetUpdate) {
        this.windowCount += windowOffsetUpdate;
        this.periodCount += periodOffsetUpdate;
        for (int i = startIndex; i < this.mediaSourceHolders.size(); i++) {
            MediaSourceHolder mediaSourceHolder = (MediaSourceHolder) this.mediaSourceHolders.get(i);
            mediaSourceHolder.firstWindowIndexInChild += windowOffsetUpdate;
            mediaSourceHolder = (MediaSourceHolder) this.mediaSourceHolders.get(i);
            mediaSourceHolder.firstPeriodIndexInChild += periodOffsetUpdate;
        }
    }

    private int findMediaSourceHolderByPeriodIndex(int periodIndex) {
        this.query.firstPeriodIndexInChild = periodIndex;
        int index = Collections.binarySearch(this.mediaSourceHolders, this.query);
        if (index < 0) {
            return (-index) - 2;
        }
        while (index < this.mediaSourceHolders.size() - 1 && ((MediaSourceHolder) this.mediaSourceHolders.get(index + 1)).firstPeriodIndexInChild == periodIndex) {
            index++;
        }
        return index;
    }
}
