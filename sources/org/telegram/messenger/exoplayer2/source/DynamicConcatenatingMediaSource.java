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
import org.telegram.messenger.exoplayer2.C0542C;
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

        public MediaSourceHolder(MediaSource mediaSource, DeferredTimeline deferredTimeline, int i, int i2, Object obj) {
            this.mediaSource = mediaSource;
            this.timeline = deferredTimeline;
            this.firstWindowIndexInChild = i;
            this.firstPeriodIndexInChild = i2;
            this.uid = obj;
        }

        public int compareTo(MediaSourceHolder mediaSourceHolder) {
            return this.firstPeriodIndexInChild - mediaSourceHolder.firstPeriodIndexInChild;
        }
    }

    private static final class MessageData<CustomType> {
        public final EventDispatcher actionOnCompletion;
        public final CustomType customData;
        public final int index;

        public MessageData(int i, CustomType customType, Runnable runnable) {
            this.index = i;
            this.actionOnCompletion = runnable != null ? new EventDispatcher(runnable) : 0;
            this.customData = customType;
        }
    }

    /* renamed from: org.telegram.messenger.exoplayer2.source.DynamicConcatenatingMediaSource$1 */
    class C18521 implements Listener {
        final /* synthetic */ MediaSourceHolder val$newMediaSourceHolder;

        C18521(MediaSourceHolder mediaSourceHolder) {
            this.val$newMediaSourceHolder = mediaSourceHolder;
        }

        public void onSourceInfoRefreshed(MediaSource mediaSource, Timeline timeline, Object obj) {
            DynamicConcatenatingMediaSource.this.updateMediaSourceInternal(this.val$newMediaSourceHolder, timeline);
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

        private DeferredTimeline(Timeline timeline, Object obj) {
            this.timeline = timeline;
            this.replacedID = obj;
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

        public Window getWindow(int i, Window window, boolean z, long j) {
            if (this.timeline != null) {
                return r0.timeline.getWindow(i, window, z, j);
            }
            return window.set(z ? DUMMY_ID : null, C0542C.TIME_UNSET, C0542C.TIME_UNSET, false, true, 0, C0542C.TIME_UNSET, 0, 0, 0);
        }

        public int getPeriodCount() {
            return this.timeline == null ? 1 : this.timeline.getPeriodCount();
        }

        public Period getPeriod(int i, Period period, boolean z) {
            if (this.timeline == null) {
                i = 0;
                Object obj = z ? DUMMY_ID : null;
                if (z) {
                    i = DUMMY_ID;
                }
                return period.set(obj, i, 0, C0542C.TIME_UNSET, C0542C.TIME_UNSET);
            }
            this.timeline.getPeriod(i, period, z);
            if (period.uid == this.replacedID) {
                period.uid = DUMMY_ID;
            }
            return period;
        }

        public int getIndexOfPeriod(Object obj) {
            if (this.timeline == null) {
                return obj == DUMMY_ID ? null : -1;
            } else {
                Timeline timeline = this.timeline;
                if (obj == DUMMY_ID) {
                    obj = this.replacedID;
                }
                return timeline.getIndexOfPeriod(obj);
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

        public ConcatenatedTimeline(Collection<MediaSourceHolder> collection, int i, int i2, ShuffleOrder shuffleOrder) {
            super(shuffleOrder);
            this.windowCount = i;
            this.periodCount = i2;
            i = collection.size();
            this.firstPeriodInChildIndices = new int[i];
            this.firstWindowInChildIndices = new int[i];
            this.timelines = new Timeline[i];
            this.uids = new int[i];
            i = 0;
            for (MediaSourceHolder mediaSourceHolder : collection) {
                this.timelines[i] = mediaSourceHolder.timeline;
                this.firstPeriodInChildIndices[i] = mediaSourceHolder.firstPeriodIndexInChild;
                this.firstWindowInChildIndices[i] = mediaSourceHolder.firstWindowIndexInChild;
                this.uids[i] = ((Integer) mediaSourceHolder.uid).intValue();
                int i3 = i + 1;
                this.childIndexByUid.put(this.uids[i], i);
                i = i3;
            }
        }

        protected int getChildIndexByPeriodIndex(int i) {
            return Util.binarySearchFloor(this.firstPeriodInChildIndices, i + 1, false, false);
        }

        protected int getChildIndexByWindowIndex(int i) {
            return Util.binarySearchFloor(this.firstWindowInChildIndices, i + 1, false, false);
        }

        protected int getChildIndexByChildUid(Object obj) {
            if (!(obj instanceof Integer)) {
                return -1;
            }
            obj = this.childIndexByUid.get(((Integer) obj).intValue(), -1);
            if (obj == -1) {
                obj = -1;
            }
            return obj;
        }

        protected Timeline getTimelineByChildIndex(int i) {
            return this.timelines[i];
        }

        protected int getFirstPeriodIndexByChildIndex(int i) {
            return this.firstPeriodInChildIndices[i];
        }

        protected int getFirstWindowIndexByChildIndex(int i) {
            return this.firstWindowInChildIndices[i];
        }

        protected Object getChildUidByChildIndex(int i) {
            return Integer.valueOf(this.uids[i]);
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

    public synchronized void addMediaSource(MediaSource mediaSource, Runnable runnable) {
        addMediaSource(this.mediaSourcesPublic.size(), mediaSource, runnable);
    }

    public synchronized void addMediaSource(int i, MediaSource mediaSource) {
        addMediaSource(i, mediaSource, null);
    }

    public synchronized void addMediaSource(int i, MediaSource mediaSource, Runnable runnable) {
        Assertions.checkNotNull(mediaSource);
        Assertions.checkArgument(this.mediaSourcesPublic.contains(mediaSource) ^ 1);
        this.mediaSourcesPublic.add(i, mediaSource);
        if (this.player != null) {
            this.player.createMessage(this).setType(0).setPayload(new MessageData(i, mediaSource, runnable)).send();
        } else if (runnable != null) {
            runnable.run();
        }
    }

    public synchronized void addMediaSources(Collection<MediaSource> collection) {
        addMediaSources(this.mediaSourcesPublic.size(), collection, null);
    }

    public synchronized void addMediaSources(Collection<MediaSource> collection, Runnable runnable) {
        addMediaSources(this.mediaSourcesPublic.size(), collection, runnable);
    }

    public synchronized void addMediaSources(int i, Collection<MediaSource> collection) {
        addMediaSources(i, collection, null);
    }

    public synchronized void addMediaSources(int i, Collection<MediaSource> collection, Runnable runnable) {
        for (MediaSource mediaSource : collection) {
            Assertions.checkNotNull(mediaSource);
            Assertions.checkArgument(this.mediaSourcesPublic.contains(mediaSource) ^ true);
        }
        this.mediaSourcesPublic.addAll(i, collection);
        if (this.player != null && !collection.isEmpty()) {
            this.player.createMessage(this).setType(1).setPayload(new MessageData(i, collection, runnable)).send();
        } else if (runnable != null) {
            runnable.run();
        }
    }

    public synchronized void removeMediaSource(int i) {
        removeMediaSource(i, null);
    }

    public synchronized void removeMediaSource(int i, Runnable runnable) {
        this.mediaSourcesPublic.remove(i);
        if (this.player != null) {
            this.player.createMessage(this).setType(2).setPayload(new MessageData(i, null, runnable)).send();
        } else if (runnable != null) {
            runnable.run();
        }
    }

    public synchronized void moveMediaSource(int i, int i2) {
        moveMediaSource(i, i2, null);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized void moveMediaSource(int i, int i2, Runnable runnable) {
        if (i != i2) {
            this.mediaSourcesPublic.add(i2, this.mediaSourcesPublic.remove(i));
            if (this.player != null) {
                this.player.createMessage(this).setType(3).setPayload(new MessageData(i, Integer.valueOf(i2), runnable)).send();
            } else if (runnable != null) {
                runnable.run();
            }
        }
    }

    public synchronized int getSize() {
        return this.mediaSourcesPublic.size();
    }

    public synchronized MediaSource getMediaSource(int i) {
        return (MediaSource) this.mediaSourcesPublic.get(i);
    }

    public synchronized void prepareSource(ExoPlayer exoPlayer, boolean z, Listener listener) {
        Assertions.checkState(!this.listener, MediaSource.MEDIA_SOURCE_REUSED_ERROR_MESSAGE);
        this.player = exoPlayer;
        this.listener = listener;
        this.preventListenerNotification = true;
        this.shuffleOrder = this.shuffleOrder.cloneAndInsert(0, this.mediaSourcesPublic.size());
        addMediaSourcesInternal(0, this.mediaSourcesPublic);
        this.preventListenerNotification = false;
        maybeNotifyListener(null);
    }

    public void maybeThrowSourceInfoRefreshError() throws IOException {
        for (int i = 0; i < this.mediaSourceHolders.size(); i++) {
            ((MediaSourceHolder) this.mediaSourceHolders.get(i)).mediaSource.maybeThrowSourceInfoRefreshError();
        }
    }

    public MediaPeriod createPeriod(MediaPeriodId mediaPeriodId, Allocator allocator) {
        MediaPeriod createPeriod;
        MediaSourceHolder mediaSourceHolder = (MediaSourceHolder) this.mediaSourceHolders.get(findMediaSourceHolderByPeriodIndex(mediaPeriodId.periodIndex));
        mediaPeriodId = mediaPeriodId.copyWithPeriodIndex(mediaPeriodId.periodIndex - mediaSourceHolder.firstPeriodIndexInChild);
        if (mediaSourceHolder.isPrepared) {
            createPeriod = mediaSourceHolder.mediaSource.createPeriod(mediaPeriodId, allocator);
        } else {
            createPeriod = new DeferredMediaPeriod(mediaSourceHolder.mediaSource, mediaPeriodId, allocator);
            this.deferredMediaPeriods.add((DeferredMediaPeriod) createPeriod);
        }
        this.mediaSourceByMediaPeriod.put(createPeriod, mediaSourceHolder.mediaSource);
        return createPeriod;
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

    public void handleMessage(int i, Object obj) throws ExoPlaybackException {
        if (i == 4) {
            ((EventDispatcher) obj).dispatchEvent();
            return;
        }
        this.preventListenerNotification = true;
        MessageData messageData;
        switch (i) {
            case 0:
                messageData = (MessageData) obj;
                this.shuffleOrder = this.shuffleOrder.cloneAndInsert(messageData.index, 1);
                addMediaSourceInternal(messageData.index, (MediaSource) messageData.customData);
                i = messageData.actionOnCompletion;
                break;
            case 1:
                messageData = (MessageData) obj;
                this.shuffleOrder = this.shuffleOrder.cloneAndInsert(messageData.index, ((Collection) messageData.customData).size());
                addMediaSourcesInternal(messageData.index, (Collection) messageData.customData);
                i = messageData.actionOnCompletion;
                break;
            case 2:
                messageData = (MessageData) obj;
                this.shuffleOrder = this.shuffleOrder.cloneAndRemove(messageData.index);
                removeMediaSourceInternal(messageData.index);
                i = messageData.actionOnCompletion;
                break;
            case 3:
                messageData = (MessageData) obj;
                this.shuffleOrder = this.shuffleOrder.cloneAndRemove(messageData.index);
                this.shuffleOrder = this.shuffleOrder.cloneAndInsert(((Integer) messageData.customData).intValue(), 1);
                moveMediaSourceInternal(messageData.index, ((Integer) messageData.customData).intValue());
                i = messageData.actionOnCompletion;
                break;
            default:
                throw new IllegalStateException();
        }
        this.preventListenerNotification = null;
        maybeNotifyListener(i);
    }

    private void maybeNotifyListener(EventDispatcher eventDispatcher) {
        if (!this.preventListenerNotification) {
            this.listener.onSourceInfoRefreshed(this, new ConcatenatedTimeline(this.mediaSourceHolders, this.windowCount, this.periodCount, this.shuffleOrder), null);
            if (eventDispatcher != null) {
                this.player.createMessage(this).setType(4).setPayload(eventDispatcher).send();
            }
        }
    }

    private void addMediaSourceInternal(int r9, org.telegram.messenger.exoplayer2.source.MediaSource r10) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Unknown predecessor block by arg (r7_0 org.telegram.messenger.exoplayer2.source.DynamicConcatenatingMediaSource$MediaSourceHolder) in PHI: PHI: (r7_2 org.telegram.messenger.exoplayer2.source.DynamicConcatenatingMediaSource$MediaSourceHolder) = (r7_0 org.telegram.messenger.exoplayer2.source.DynamicConcatenatingMediaSource$MediaSourceHolder), (r7_1 org.telegram.messenger.exoplayer2.source.DynamicConcatenatingMediaSource$MediaSourceHolder) binds: {(r7_0 org.telegram.messenger.exoplayer2.source.DynamicConcatenatingMediaSource$MediaSourceHolder)=B:2:0x000f, (r7_1 org.telegram.messenger.exoplayer2.source.DynamicConcatenatingMediaSource$MediaSourceHolder)=B:3:0x0036}
	at jadx.core.dex.instructions.PhiInsn.replaceArg(PhiInsn.java:79)
	at jadx.core.dex.visitors.ModVisitor.processInvoke(ModVisitor.java:222)
	at jadx.core.dex.visitors.ModVisitor.replaceStep(ModVisitor.java:83)
	at jadx.core.dex.visitors.ModVisitor.visit(ModVisitor.java:68)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
        /*
        r8 = this;
        r0 = java.lang.System.identityHashCode(r10);
        r6 = java.lang.Integer.valueOf(r0);
        r0 = new org.telegram.messenger.exoplayer2.source.DynamicConcatenatingMediaSource$DeferredTimeline;
        r0.<init>();
        if (r9 <= 0) goto L_0x0036;
    L_0x000f:
        r1 = r8.mediaSourceHolders;
        r2 = r9 + -1;
        r1 = r1.get(r2);
        r1 = (org.telegram.messenger.exoplayer2.source.DynamicConcatenatingMediaSource.MediaSourceHolder) r1;
        r7 = new org.telegram.messenger.exoplayer2.source.DynamicConcatenatingMediaSource$MediaSourceHolder;
        r2 = r1.firstWindowIndexInChild;
        r3 = r1.timeline;
        r3 = r3.getWindowCount();
        r4 = r2 + r3;
        r2 = r1.firstPeriodIndexInChild;
        r1 = r1.timeline;
        r1 = r1.getPeriodCount();
        r5 = r2 + r1;
        r1 = r7;
        r2 = r10;
        r3 = r0;
        r1.<init>(r2, r3, r4, r5, r6);
        goto L_0x0040;
    L_0x0036:
        r7 = new org.telegram.messenger.exoplayer2.source.DynamicConcatenatingMediaSource$MediaSourceHolder;
        r4 = 0;
        r5 = 0;
        r1 = r7;
        r2 = r10;
        r3 = r0;
        r1.<init>(r2, r3, r4, r5, r6);
    L_0x0040:
        r10 = r0.getWindowCount();
        r0 = r0.getPeriodCount();
        r8.correctOffsets(r9, r10, r0);
        r10 = r8.mediaSourceHolders;
        r10.add(r9, r7);
        r9 = r7.mediaSource;
        r10 = r8.player;
        r0 = 0;
        r1 = new org.telegram.messenger.exoplayer2.source.DynamicConcatenatingMediaSource$1;
        r1.<init>(r7);
        r9.prepareSource(r10, r0, r1);
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.exoplayer2.source.DynamicConcatenatingMediaSource.addMediaSourceInternal(int, org.telegram.messenger.exoplayer2.source.MediaSource):void");
    }

    private void addMediaSourcesInternal(int i, Collection<MediaSource> collection) {
        for (MediaSource addMediaSourceInternal : collection) {
            int i2 = i + 1;
            addMediaSourceInternal(i, addMediaSourceInternal);
            i = i2;
        }
    }

    private void updateMediaSourceInternal(MediaSourceHolder mediaSourceHolder, Timeline timeline) {
        if (mediaSourceHolder == null) {
            throw new IllegalArgumentException();
        }
        DeferredTimeline deferredTimeline = mediaSourceHolder.timeline;
        if (deferredTimeline.getTimeline() != timeline) {
            int windowCount = timeline.getWindowCount() - deferredTimeline.getWindowCount();
            int periodCount = timeline.getPeriodCount() - deferredTimeline.getPeriodCount();
            if (!(windowCount == 0 && periodCount == 0)) {
                correctOffsets(findMediaSourceHolderByPeriodIndex(mediaSourceHolder.firstPeriodIndexInChild) + 1, windowCount, periodCount);
            }
            mediaSourceHolder.timeline = deferredTimeline.cloneWithNewTimeline(timeline);
            if (mediaSourceHolder.isPrepared == null) {
                for (timeline = this.deferredMediaPeriods.size() - 1; timeline >= null; timeline--) {
                    if (((DeferredMediaPeriod) this.deferredMediaPeriods.get(timeline)).mediaSource == mediaSourceHolder.mediaSource) {
                        ((DeferredMediaPeriod) this.deferredMediaPeriods.get(timeline)).createPeriod();
                        this.deferredMediaPeriods.remove(timeline);
                    }
                }
            }
            mediaSourceHolder.isPrepared = true;
            maybeNotifyListener(null);
        }
    }

    private void removeMediaSourceInternal(int i) {
        MediaSourceHolder mediaSourceHolder = (MediaSourceHolder) this.mediaSourceHolders.get(i);
        this.mediaSourceHolders.remove(i);
        Timeline timeline = mediaSourceHolder.timeline;
        correctOffsets(i, -timeline.getWindowCount(), -timeline.getPeriodCount());
        mediaSourceHolder.mediaSource.releaseSource();
    }

    private void moveMediaSourceInternal(int i, int i2) {
        int min = Math.min(i, i2);
        int max = Math.max(i, i2);
        int i3 = ((MediaSourceHolder) this.mediaSourceHolders.get(min)).firstWindowIndexInChild;
        int i4 = ((MediaSourceHolder) this.mediaSourceHolders.get(min)).firstPeriodIndexInChild;
        this.mediaSourceHolders.add(i2, this.mediaSourceHolders.remove(i));
        while (min <= max) {
            MediaSourceHolder mediaSourceHolder = (MediaSourceHolder) this.mediaSourceHolders.get(min);
            mediaSourceHolder.firstWindowIndexInChild = i3;
            mediaSourceHolder.firstPeriodIndexInChild = i4;
            i3 += mediaSourceHolder.timeline.getWindowCount();
            i4 += mediaSourceHolder.timeline.getPeriodCount();
            min++;
        }
    }

    private void correctOffsets(int i, int i2, int i3) {
        this.windowCount += i2;
        this.periodCount += i3;
        while (i < this.mediaSourceHolders.size()) {
            MediaSourceHolder mediaSourceHolder = (MediaSourceHolder) this.mediaSourceHolders.get(i);
            mediaSourceHolder.firstWindowIndexInChild += i2;
            mediaSourceHolder = (MediaSourceHolder) this.mediaSourceHolders.get(i);
            mediaSourceHolder.firstPeriodIndexInChild += i3;
            i++;
        }
    }

    private int findMediaSourceHolderByPeriodIndex(int i) {
        this.query.firstPeriodIndexInChild = i;
        int binarySearch = Collections.binarySearch(this.mediaSourceHolders, this.query);
        if (binarySearch < 0) {
            return (-binarySearch) - 2;
        }
        while (binarySearch < this.mediaSourceHolders.size() - 1) {
            int i2 = binarySearch + 1;
            if (((MediaSourceHolder) this.mediaSourceHolders.get(i2)).firstPeriodIndexInChild != i) {
                break;
            }
            binarySearch = i2;
        }
        return binarySearch;
    }
}
