package org.telegram.messenger.exoplayer2.source;

import android.os.Handler;
import android.os.Looper;
import android.util.SparseIntArray;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import org.telegram.messenger.exoplayer2.C0554C;
import org.telegram.messenger.exoplayer2.ExoPlaybackException;
import org.telegram.messenger.exoplayer2.ExoPlayer;
import org.telegram.messenger.exoplayer2.PlayerMessage.Target;
import org.telegram.messenger.exoplayer2.Timeline;
import org.telegram.messenger.exoplayer2.Timeline.Period;
import org.telegram.messenger.exoplayer2.Timeline.Window;
import org.telegram.messenger.exoplayer2.source.MediaSource.MediaPeriodId;
import org.telegram.messenger.exoplayer2.source.ShuffleOrder.DefaultShuffleOrder;
import org.telegram.messenger.exoplayer2.upstream.Allocator;
import org.telegram.messenger.exoplayer2.util.Assertions;
import org.telegram.messenger.exoplayer2.util.Util;

public class ConcatenatingMediaSource extends CompositeMediaSource<MediaSourceHolder> implements Target {
    private static final int MSG_ADD = 0;
    private static final int MSG_ADD_MULTIPLE = 1;
    private static final int MSG_CLEAR = 4;
    private static final int MSG_MOVE = 3;
    private static final int MSG_NOTIFY_LISTENER = 5;
    private static final int MSG_ON_COMPLETION = 6;
    private static final int MSG_REMOVE = 2;
    private final boolean isAtomic;
    private boolean listenerNotificationScheduled;
    private final Map<MediaPeriod, MediaSourceHolder> mediaSourceByMediaPeriod;
    private final List<MediaSourceHolder> mediaSourceHolders;
    private final List<MediaSourceHolder> mediaSourcesPublic;
    private final List<EventDispatcher> pendingOnCompletionActions;
    private int periodCount;
    private ExoPlayer player;
    private final MediaSourceHolder query;
    private ShuffleOrder shuffleOrder;
    private final Window window;
    private int windowCount;

    private static final class EventDispatcher {
        public final Handler eventHandler;
        public final Runnable runnable;

        public EventDispatcher(Runnable runnable) {
            this.runnable = runnable;
            this.eventHandler = new Handler(Looper.myLooper() != null ? Looper.myLooper() : Looper.getMainLooper());
        }

        public void dispatchEvent() {
            this.eventHandler.post(this.runnable);
        }
    }

    static final class MediaSourceHolder implements Comparable<MediaSourceHolder> {
        public List<DeferredMediaPeriod> activeMediaPeriods = new ArrayList();
        public int childIndex;
        public int firstPeriodIndexInChild;
        public int firstWindowIndexInChild;
        public boolean isPrepared;
        public boolean isRemoved;
        public final MediaSource mediaSource;
        public DeferredTimeline timeline = new DeferredTimeline();
        public final int uid = System.identityHashCode(this);

        public MediaSourceHolder(MediaSource mediaSource) {
            this.mediaSource = mediaSource;
        }

        public void reset(int childIndex, int firstWindowIndexInChild, int firstPeriodIndexInChild) {
            this.childIndex = childIndex;
            this.firstWindowIndexInChild = firstWindowIndexInChild;
            this.firstPeriodIndexInChild = firstPeriodIndexInChild;
            this.isPrepared = false;
            this.isRemoved = false;
            this.activeMediaPeriods.clear();
        }

        public int compareTo(MediaSourceHolder other) {
            return this.firstPeriodIndexInChild - other.firstPeriodIndexInChild;
        }
    }

    private static final class MessageData<T> {
        public final EventDispatcher actionOnCompletion;
        public final T customData;
        public final int index;

        public MessageData(int index, T customData, Runnable actionOnCompletion) {
            this.index = index;
            this.actionOnCompletion = actionOnCompletion != null ? new EventDispatcher(actionOnCompletion) : null;
            this.customData = customData;
        }
    }

    private static final class DummyTimeline extends Timeline {
        private DummyTimeline() {
        }

        public int getWindowCount() {
            return 1;
        }

        public Window getWindow(int windowIndex, Window window, boolean setTag, long defaultPositionProjectionUs) {
            return window.set(null, C0554C.TIME_UNSET, C0554C.TIME_UNSET, false, true, defaultPositionProjectionUs > 0 ? C0554C.TIME_UNSET : 0, C0554C.TIME_UNSET, 0, 0, 0);
        }

        public int getPeriodCount() {
            return 1;
        }

        public Period getPeriod(int periodIndex, Period period, boolean setIds) {
            return period.set(null, null, 0, C0554C.TIME_UNSET, 0);
        }

        public int getIndexOfPeriod(Object uid) {
            return uid == null ? 0 : -1;
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

        public ConcatenatedTimeline(Collection<MediaSourceHolder> mediaSourceHolders, int windowCount, int periodCount, ShuffleOrder shuffleOrder, boolean isAtomic) {
            super(isAtomic, shuffleOrder);
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
                this.uids[index] = mediaSourceHolder.uid;
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

    private static final class DeferredTimeline extends ForwardingTimeline {
        private static final Object DUMMY_ID = new Object();
        private static final DummyTimeline dummyTimeline = new DummyTimeline();
        private static final Period period = new Period();
        private final Object replacedId;

        public DeferredTimeline() {
            this(dummyTimeline, null);
        }

        private DeferredTimeline(Timeline timeline, Object replacedId) {
            super(timeline);
            this.replacedId = replacedId;
        }

        public DeferredTimeline cloneWithNewTimeline(Timeline timeline) {
            Object obj = (this.replacedId != null || timeline.getPeriodCount() <= 0) ? this.replacedId : timeline.getPeriod(0, period, true).uid;
            return new DeferredTimeline(timeline, obj);
        }

        public Timeline getTimeline() {
            return this.timeline;
        }

        public Period getPeriod(int periodIndex, Period period, boolean setIds) {
            this.timeline.getPeriod(periodIndex, period, setIds);
            if (Util.areEqual(period.uid, this.replacedId)) {
                period.uid = DUMMY_ID;
            }
            return period;
        }

        public int getIndexOfPeriod(Object uid) {
            Timeline timeline = this.timeline;
            if (DUMMY_ID.equals(uid)) {
                uid = this.replacedId;
            }
            return timeline.getIndexOfPeriod(uid);
        }
    }

    public ConcatenatingMediaSource() {
        this(false, new DefaultShuffleOrder(0));
    }

    public ConcatenatingMediaSource(boolean isAtomic) {
        this(isAtomic, new DefaultShuffleOrder(0));
    }

    public ConcatenatingMediaSource(boolean isAtomic, ShuffleOrder shuffleOrder) {
        this(isAtomic, shuffleOrder, new MediaSource[0]);
    }

    public ConcatenatingMediaSource(MediaSource... mediaSources) {
        this(false, mediaSources);
    }

    public ConcatenatingMediaSource(boolean isAtomic, MediaSource... mediaSources) {
        this(isAtomic, new DefaultShuffleOrder(0), mediaSources);
    }

    public ConcatenatingMediaSource(boolean isAtomic, ShuffleOrder shuffleOrder, MediaSource... mediaSources) {
        for (MediaSource mediaSource : mediaSources) {
            Assertions.checkNotNull(mediaSource);
        }
        if (shuffleOrder.getLength() > 0) {
            shuffleOrder = shuffleOrder.cloneAndClear();
        }
        this.shuffleOrder = shuffleOrder;
        this.mediaSourceByMediaPeriod = new IdentityHashMap();
        this.mediaSourcesPublic = new ArrayList();
        this.mediaSourceHolders = new ArrayList();
        this.pendingOnCompletionActions = new ArrayList();
        this.query = new MediaSourceHolder(null);
        this.isAtomic = isAtomic;
        this.window = new Window();
        addMediaSources(Arrays.asList(mediaSources));
    }

    public final synchronized void addMediaSource(MediaSource mediaSource) {
        addMediaSource(this.mediaSourcesPublic.size(), mediaSource, null);
    }

    public final synchronized void addMediaSource(MediaSource mediaSource, Runnable actionOnCompletion) {
        addMediaSource(this.mediaSourcesPublic.size(), mediaSource, actionOnCompletion);
    }

    public final synchronized void addMediaSource(int index, MediaSource mediaSource) {
        addMediaSource(index, mediaSource, null);
    }

    public final synchronized void addMediaSource(int index, MediaSource mediaSource, Runnable actionOnCompletion) {
        Assertions.checkNotNull(mediaSource);
        MediaSourceHolder mediaSourceHolder = new MediaSourceHolder(mediaSource);
        this.mediaSourcesPublic.add(index, mediaSourceHolder);
        if (this.player != null) {
            this.player.createMessage(this).setType(0).setPayload(new MessageData(index, mediaSourceHolder, actionOnCompletion)).send();
        } else if (actionOnCompletion != null) {
            actionOnCompletion.run();
        }
    }

    public final synchronized void addMediaSources(Collection<MediaSource> mediaSources) {
        addMediaSources(this.mediaSourcesPublic.size(), mediaSources, null);
    }

    public final synchronized void addMediaSources(Collection<MediaSource> mediaSources, Runnable actionOnCompletion) {
        addMediaSources(this.mediaSourcesPublic.size(), mediaSources, actionOnCompletion);
    }

    public final synchronized void addMediaSources(int index, Collection<MediaSource> mediaSources) {
        addMediaSources(index, mediaSources, null);
    }

    public final synchronized void addMediaSources(int index, Collection<MediaSource> mediaSources, Runnable actionOnCompletion) {
        for (MediaSource mediaSource : mediaSources) {
            Assertions.checkNotNull(mediaSource);
        }
        List<MediaSourceHolder> mediaSourceHolders = new ArrayList(mediaSources.size());
        for (MediaSource mediaSource2 : mediaSources) {
            mediaSourceHolders.add(new MediaSourceHolder(mediaSource2));
        }
        this.mediaSourcesPublic.addAll(index, mediaSourceHolders);
        if (this.player != null && !mediaSources.isEmpty()) {
            this.player.createMessage(this).setType(1).setPayload(new MessageData(index, mediaSourceHolders, actionOnCompletion)).send();
        } else if (actionOnCompletion != null) {
            actionOnCompletion.run();
        }
    }

    public final synchronized void removeMediaSource(int index) {
        removeMediaSource(index, null);
    }

    public final synchronized void removeMediaSource(int index, Runnable actionOnCompletion) {
        this.mediaSourcesPublic.remove(index);
        if (this.player != null) {
            this.player.createMessage(this).setType(2).setPayload(new MessageData(index, null, actionOnCompletion)).send();
        } else if (actionOnCompletion != null) {
            actionOnCompletion.run();
        }
    }

    public final synchronized void moveMediaSource(int currentIndex, int newIndex) {
        moveMediaSource(currentIndex, newIndex, null);
    }

    public final synchronized void moveMediaSource(int currentIndex, int newIndex, Runnable actionOnCompletion) {
        if (currentIndex != newIndex) {
            this.mediaSourcesPublic.add(newIndex, this.mediaSourcesPublic.remove(currentIndex));
            if (this.player != null) {
                this.player.createMessage(this).setType(3).setPayload(new MessageData(currentIndex, Integer.valueOf(newIndex), actionOnCompletion)).send();
            } else if (actionOnCompletion != null) {
                actionOnCompletion.run();
            }
        }
    }

    public final synchronized void clear() {
        clear(null);
    }

    public final synchronized void clear(Runnable actionOnCompletion) {
        this.mediaSourcesPublic.clear();
        if (this.player != null) {
            this.player.createMessage(this).setType(4).setPayload(actionOnCompletion != null ? new EventDispatcher(actionOnCompletion) : null).send();
        } else if (actionOnCompletion != null) {
            actionOnCompletion.run();
        }
    }

    public final synchronized int getSize() {
        return this.mediaSourcesPublic.size();
    }

    public final synchronized MediaSource getMediaSource(int index) {
        return ((MediaSourceHolder) this.mediaSourcesPublic.get(index)).mediaSource;
    }

    public final synchronized void prepareSourceInternal(ExoPlayer player, boolean isTopLevelSource) {
        super.prepareSourceInternal(player, isTopLevelSource);
        this.player = player;
        if (this.mediaSourcesPublic.isEmpty()) {
            notifyListener();
        } else {
            this.shuffleOrder = this.shuffleOrder.cloneAndInsert(0, this.mediaSourcesPublic.size());
            addMediaSourcesInternal(0, this.mediaSourcesPublic);
            scheduleListenerNotification(null);
        }
    }

    public final MediaPeriod createPeriod(MediaPeriodId id, Allocator allocator) {
        MediaSourceHolder holder = (MediaSourceHolder) this.mediaSourceHolders.get(findMediaSourceHolderByPeriodIndex(id.periodIndex));
        DeferredMediaPeriod mediaPeriod = new DeferredMediaPeriod(holder.mediaSource, id.copyWithPeriodIndex(id.periodIndex - holder.firstPeriodIndexInChild), allocator);
        this.mediaSourceByMediaPeriod.put(mediaPeriod, holder);
        holder.activeMediaPeriods.add(mediaPeriod);
        if (holder.isPrepared) {
            mediaPeriod.createPeriod();
        }
        return mediaPeriod;
    }

    public final void releasePeriod(MediaPeriod mediaPeriod) {
        MediaSourceHolder holder = (MediaSourceHolder) this.mediaSourceByMediaPeriod.remove(mediaPeriod);
        ((DeferredMediaPeriod) mediaPeriod).releasePeriod();
        holder.activeMediaPeriods.remove(mediaPeriod);
        if (holder.activeMediaPeriods.isEmpty() && holder.isRemoved) {
            releaseChildSource(holder);
        }
    }

    public final void releaseSourceInternal() {
        super.releaseSourceInternal();
        this.mediaSourceHolders.clear();
        this.player = null;
        this.shuffleOrder = this.shuffleOrder.cloneAndClear();
        this.windowCount = 0;
        this.periodCount = 0;
    }

    protected final void onChildSourceInfoRefreshed(MediaSourceHolder mediaSourceHolder, MediaSource mediaSource, Timeline timeline, Object manifest) {
        updateMediaSourceInternal(mediaSourceHolder, timeline);
    }

    protected MediaPeriodId getMediaPeriodIdForChildMediaPeriodId(MediaSourceHolder mediaSourceHolder, MediaPeriodId mediaPeriodId) {
        for (int i = 0; i < mediaSourceHolder.activeMediaPeriods.size(); i++) {
            if (((DeferredMediaPeriod) mediaSourceHolder.activeMediaPeriods.get(i)).id.windowSequenceNumber == mediaPeriodId.windowSequenceNumber) {
                return mediaPeriodId.copyWithPeriodIndex(mediaPeriodId.periodIndex + mediaSourceHolder.firstPeriodIndexInChild);
            }
        }
        return null;
    }

    protected int getWindowIndexForChildWindowIndex(MediaSourceHolder mediaSourceHolder, int windowIndex) {
        return mediaSourceHolder.firstWindowIndexInChild + windowIndex;
    }

    public final void handleMessage(int messageType, Object message) throws ExoPlaybackException {
        switch (messageType) {
            case 0:
                MessageData<MediaSourceHolder> addMessage = (MessageData) message;
                this.shuffleOrder = this.shuffleOrder.cloneAndInsert(addMessage.index, 1);
                addMediaSourceInternal(addMessage.index, (MediaSourceHolder) addMessage.customData);
                scheduleListenerNotification(addMessage.actionOnCompletion);
                return;
            case 1:
                MessageData<Collection<MediaSourceHolder>> addMultipleMessage = (MessageData) message;
                this.shuffleOrder = this.shuffleOrder.cloneAndInsert(addMultipleMessage.index, ((Collection) addMultipleMessage.customData).size());
                addMediaSourcesInternal(addMultipleMessage.index, (Collection) addMultipleMessage.customData);
                scheduleListenerNotification(addMultipleMessage.actionOnCompletion);
                return;
            case 2:
                MessageData<Void> removeMessage = (MessageData) message;
                this.shuffleOrder = this.shuffleOrder.cloneAndRemove(removeMessage.index);
                removeMediaSourceInternal(removeMessage.index);
                scheduleListenerNotification(removeMessage.actionOnCompletion);
                return;
            case 3:
                MessageData<Integer> moveMessage = (MessageData) message;
                this.shuffleOrder = this.shuffleOrder.cloneAndRemove(moveMessage.index);
                this.shuffleOrder = this.shuffleOrder.cloneAndInsert(((Integer) moveMessage.customData).intValue(), 1);
                moveMediaSourceInternal(moveMessage.index, ((Integer) moveMessage.customData).intValue());
                scheduleListenerNotification(moveMessage.actionOnCompletion);
                return;
            case 4:
                clearInternal();
                scheduleListenerNotification((EventDispatcher) message);
                return;
            case 5:
                notifyListener();
                return;
            case 6:
                List<EventDispatcher> actionsOnCompletion = (List) message;
                for (int i = 0; i < actionsOnCompletion.size(); i++) {
                    ((EventDispatcher) actionsOnCompletion.get(i)).dispatchEvent();
                }
                return;
            default:
                throw new IllegalStateException();
        }
    }

    private void scheduleListenerNotification(EventDispatcher actionOnCompletion) {
        if (!this.listenerNotificationScheduled) {
            this.player.createMessage(this).setType(5).send();
            this.listenerNotificationScheduled = true;
        }
        if (actionOnCompletion != null) {
            this.pendingOnCompletionActions.add(actionOnCompletion);
        }
    }

    private void notifyListener() {
        this.listenerNotificationScheduled = false;
        List<EventDispatcher> actionsOnCompletion = this.pendingOnCompletionActions.isEmpty() ? Collections.emptyList() : new ArrayList(this.pendingOnCompletionActions);
        this.pendingOnCompletionActions.clear();
        refreshSourceInfo(new ConcatenatedTimeline(this.mediaSourceHolders, this.windowCount, this.periodCount, this.shuffleOrder, this.isAtomic), null);
        if (!actionsOnCompletion.isEmpty()) {
            this.player.createMessage(this).setType(6).setPayload(actionsOnCompletion).send();
        }
    }

    private void addMediaSourceInternal(int newIndex, MediaSourceHolder newMediaSourceHolder) {
        if (newIndex > 0) {
            MediaSourceHolder previousHolder = (MediaSourceHolder) this.mediaSourceHolders.get(newIndex - 1);
            newMediaSourceHolder.reset(newIndex, previousHolder.firstWindowIndexInChild + previousHolder.timeline.getWindowCount(), previousHolder.firstPeriodIndexInChild + previousHolder.timeline.getPeriodCount());
        } else {
            newMediaSourceHolder.reset(newIndex, 0, 0);
        }
        correctOffsets(newIndex, 1, newMediaSourceHolder.timeline.getWindowCount(), newMediaSourceHolder.timeline.getPeriodCount());
        this.mediaSourceHolders.add(newIndex, newMediaSourceHolder);
        prepareChildSource(newMediaSourceHolder, newMediaSourceHolder.mediaSource);
    }

    private void addMediaSourcesInternal(int index, Collection<MediaSourceHolder> mediaSourceHolders) {
        for (MediaSourceHolder mediaSourceHolder : mediaSourceHolders) {
            int index2 = index + 1;
            addMediaSourceInternal(index, mediaSourceHolder);
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
                correctOffsets(mediaSourceHolder.childIndex + 1, 0, windowOffsetUpdate, periodOffsetUpdate);
            }
            mediaSourceHolder.timeline = deferredTimeline.cloneWithNewTimeline(timeline);
            if (!(mediaSourceHolder.isPrepared || timeline.isEmpty())) {
                timeline.getWindow(0, this.window);
                long defaultPeriodPositionUs = this.window.getPositionInFirstPeriodUs() + this.window.getDefaultPositionUs();
                for (int i = 0; i < mediaSourceHolder.activeMediaPeriods.size(); i++) {
                    DeferredMediaPeriod deferredMediaPeriod = (DeferredMediaPeriod) mediaSourceHolder.activeMediaPeriods.get(i);
                    deferredMediaPeriod.setDefaultPreparePositionUs(defaultPeriodPositionUs);
                    deferredMediaPeriod.createPeriod();
                }
                mediaSourceHolder.isPrepared = true;
            }
            scheduleListenerNotification(null);
        }
    }

    private void clearInternal() {
        for (int index = this.mediaSourceHolders.size() - 1; index >= 0; index--) {
            removeMediaSourceInternal(index);
        }
    }

    private void removeMediaSourceInternal(int index) {
        MediaSourceHolder holder = (MediaSourceHolder) this.mediaSourceHolders.remove(index);
        Timeline oldTimeline = holder.timeline;
        correctOffsets(index, -1, -oldTimeline.getWindowCount(), -oldTimeline.getPeriodCount());
        holder.isRemoved = true;
        if (holder.activeMediaPeriods.isEmpty()) {
            releaseChildSource(holder);
        }
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

    private void correctOffsets(int startIndex, int childIndexUpdate, int windowOffsetUpdate, int periodOffsetUpdate) {
        this.windowCount += windowOffsetUpdate;
        this.periodCount += periodOffsetUpdate;
        for (int i = startIndex; i < this.mediaSourceHolders.size(); i++) {
            MediaSourceHolder mediaSourceHolder = (MediaSourceHolder) this.mediaSourceHolders.get(i);
            mediaSourceHolder.childIndex += childIndexUpdate;
            mediaSourceHolder = (MediaSourceHolder) this.mediaSourceHolders.get(i);
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
        int i = index;
        return index;
    }
}
