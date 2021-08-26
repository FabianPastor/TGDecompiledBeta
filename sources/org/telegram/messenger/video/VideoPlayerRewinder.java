package org.telegram.messenger.video;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.Components.VideoPlayer;

public class VideoPlayerRewinder {
    /* access modifiers changed from: private */
    public final Runnable backSeek = new Runnable() {
        public void run() {
            if (VideoPlayerRewinder.this.videoPlayer != null) {
                long duration = VideoPlayerRewinder.this.videoPlayer.getDuration();
                if (duration == 0 || duration == -9223372036854775807L) {
                    long unused = VideoPlayerRewinder.this.rewindLastTime = System.currentTimeMillis();
                    return;
                }
                long currentTimeMillis = System.currentTimeMillis();
                long access$100 = currentTimeMillis - VideoPlayerRewinder.this.rewindLastTime;
                long unused2 = VideoPlayerRewinder.this.rewindLastTime = currentTimeMillis;
                VideoPlayerRewinder videoPlayerRewinder = VideoPlayerRewinder.this;
                int i = videoPlayerRewinder.rewindCount;
                long j = access$100 * (i == 1 ? 3 : i == 2 ? 6 : 12);
                if (videoPlayerRewinder.rewindForward) {
                    VideoPlayerRewinder.access$314(VideoPlayerRewinder.this, j);
                } else {
                    VideoPlayerRewinder.access$322(VideoPlayerRewinder.this, j);
                }
                if (VideoPlayerRewinder.this.rewindBackSeekPlayerPosition < 0) {
                    long unused3 = VideoPlayerRewinder.this.rewindBackSeekPlayerPosition = 0;
                } else if (VideoPlayerRewinder.this.rewindBackSeekPlayerPosition > duration) {
                    long unused4 = VideoPlayerRewinder.this.rewindBackSeekPlayerPosition = duration;
                }
                VideoPlayerRewinder videoPlayerRewinder2 = VideoPlayerRewinder.this;
                if (videoPlayerRewinder2.rewindByBackSeek && videoPlayerRewinder2.videoPlayer != null && VideoPlayerRewinder.this.rewindLastTime - VideoPlayerRewinder.this.rewindLastUpdatePlayerTime > 350) {
                    VideoPlayerRewinder videoPlayerRewinder3 = VideoPlayerRewinder.this;
                    long unused5 = videoPlayerRewinder3.rewindLastUpdatePlayerTime = videoPlayerRewinder3.rewindLastTime;
                    VideoPlayerRewinder.this.videoPlayer.seekTo(VideoPlayerRewinder.this.rewindBackSeekPlayerPosition);
                }
                if (VideoPlayerRewinder.this.videoPlayer != null) {
                    long access$300 = VideoPlayerRewinder.this.rewindBackSeekPlayerPosition - VideoPlayerRewinder.this.startRewindFrom;
                    float access$3002 = ((float) VideoPlayerRewinder.this.rewindBackSeekPlayerPosition) / ((float) VideoPlayerRewinder.this.videoPlayer.getDuration());
                    VideoPlayerRewinder videoPlayerRewinder4 = VideoPlayerRewinder.this;
                    videoPlayerRewinder4.updateRewindProgressUi(access$300, access$3002, videoPlayerRewinder4.rewindByBackSeek);
                }
                if (VideoPlayerRewinder.this.rewindBackSeekPlayerPosition == 0 || VideoPlayerRewinder.this.rewindBackSeekPlayerPosition >= duration) {
                    VideoPlayerRewinder videoPlayerRewinder5 = VideoPlayerRewinder.this;
                    if (videoPlayerRewinder5.rewindByBackSeek && videoPlayerRewinder5.videoPlayer != null) {
                        VideoPlayerRewinder videoPlayerRewinder6 = VideoPlayerRewinder.this;
                        long unused6 = videoPlayerRewinder6.rewindLastUpdatePlayerTime = videoPlayerRewinder6.rewindLastTime;
                        VideoPlayerRewinder.this.videoPlayer.seekTo(VideoPlayerRewinder.this.rewindBackSeekPlayerPosition);
                    }
                    VideoPlayerRewinder.this.cancelRewind();
                }
                VideoPlayerRewinder videoPlayerRewinder7 = VideoPlayerRewinder.this;
                if (videoPlayerRewinder7.rewindCount > 0) {
                    AndroidUtilities.runOnUIThread(videoPlayerRewinder7.backSeek, 16);
                }
            }
        }
    };
    private float playSpeed = 1.0f;
    /* access modifiers changed from: private */
    public long rewindBackSeekPlayerPosition;
    public boolean rewindByBackSeek;
    public int rewindCount;
    /* access modifiers changed from: private */
    public boolean rewindForward;
    /* access modifiers changed from: private */
    public long rewindLastTime;
    /* access modifiers changed from: private */
    public long rewindLastUpdatePlayerTime;
    /* access modifiers changed from: private */
    public long startRewindFrom;
    private Runnable updateRewindRunnable;
    /* access modifiers changed from: private */
    public VideoPlayer videoPlayer;

    /* access modifiers changed from: protected */
    public void onRewindCanceled() {
    }

    /* access modifiers changed from: protected */
    public void onRewindStart(boolean z) {
    }

    /* access modifiers changed from: protected */
    public void updateRewindProgressUi(long j, float f, boolean z) {
    }

    static /* synthetic */ long access$314(VideoPlayerRewinder videoPlayerRewinder, long j) {
        long j2 = videoPlayerRewinder.rewindBackSeekPlayerPosition + j;
        videoPlayerRewinder.rewindBackSeekPlayerPosition = j2;
        return j2;
    }

    static /* synthetic */ long access$322(VideoPlayerRewinder videoPlayerRewinder, long j) {
        long j2 = videoPlayerRewinder.rewindBackSeekPlayerPosition - j;
        videoPlayerRewinder.rewindBackSeekPlayerPosition = j2;
        return j2;
    }

    public void startRewind(VideoPlayer videoPlayer2, boolean z, float f) {
        this.videoPlayer = videoPlayer2;
        this.playSpeed = f;
        this.rewindForward = z;
        cancelRewind();
        incrementRewindCount();
    }

    public void cancelRewind() {
        if (this.rewindCount != 0) {
            this.rewindCount = 0;
            VideoPlayer videoPlayer2 = this.videoPlayer;
            if (videoPlayer2 != null) {
                if (this.rewindByBackSeek) {
                    videoPlayer2.seekTo(this.rewindBackSeekPlayerPosition);
                } else {
                    this.videoPlayer.seekTo(videoPlayer2.getCurrentPosition());
                }
                this.videoPlayer.setPlaybackSpeed(this.playSpeed);
            }
        }
        AndroidUtilities.cancelRunOnUIThread(this.backSeek);
        Runnable runnable = this.updateRewindRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.updateRewindRunnable = null;
        }
        onRewindCanceled();
    }

    /* JADX WARNING: Code restructure failed: missing block: B:23:0x0048, code lost:
        if (r0 != 2) goto L_0x004b;
     */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x004f  */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x0078  */
    /* JADX WARNING: Removed duplicated region for block: B:35:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void incrementRewindCount() {
        /*
            r4 = this;
            org.telegram.ui.Components.VideoPlayer r0 = r4.videoPlayer
            if (r0 != 0) goto L_0x0005
            return
        L_0x0005:
            int r1 = r4.rewindCount
            r2 = 1
            int r1 = r1 + r2
            r4.rewindCount = r1
            r3 = 0
            if (r1 != r2) goto L_0x001d
            boolean r1 = r4.rewindForward
            if (r1 == 0) goto L_0x001b
            boolean r0 = r0.isPlaying()
            if (r0 == 0) goto L_0x001b
            r4.rewindByBackSeek = r3
            goto L_0x001d
        L_0x001b:
            r4.rewindByBackSeek = r2
        L_0x001d:
            boolean r0 = r4.rewindForward
            r1 = 2
            if (r0 == 0) goto L_0x0044
            boolean r0 = r4.rewindByBackSeek
            if (r0 != 0) goto L_0x0044
            int r0 = r4.rewindCount
            if (r0 != r2) goto L_0x0032
            org.telegram.ui.Components.VideoPlayer r0 = r4.videoPlayer
            r1 = 1082130432(0x40800000, float:4.0)
            r0.setPlaybackSpeed(r1)
            goto L_0x004a
        L_0x0032:
            if (r0 != r1) goto L_0x003c
            org.telegram.ui.Components.VideoPlayer r0 = r4.videoPlayer
            r1 = 1088421888(0x40e00000, float:7.0)
            r0.setPlaybackSpeed(r1)
            goto L_0x004a
        L_0x003c:
            org.telegram.ui.Components.VideoPlayer r0 = r4.videoPlayer
            r1 = 1095761920(0x41500000, float:13.0)
            r0.setPlaybackSpeed(r1)
            goto L_0x004b
        L_0x0044:
            int r0 = r4.rewindCount
            if (r0 == r2) goto L_0x004a
            if (r0 != r1) goto L_0x004b
        L_0x004a:
            r3 = 1
        L_0x004b:
            int r0 = r4.rewindCount
            if (r0 != r2) goto L_0x006c
            org.telegram.ui.Components.VideoPlayer r0 = r4.videoPlayer
            long r0 = r0.getCurrentPosition()
            r4.rewindBackSeekPlayerPosition = r0
            long r0 = java.lang.System.currentTimeMillis()
            r4.rewindLastTime = r0
            r4.rewindLastUpdatePlayerTime = r0
            org.telegram.ui.Components.VideoPlayer r0 = r4.videoPlayer
            long r0 = r0.getCurrentPosition()
            r4.startRewindFrom = r0
            boolean r0 = r4.rewindForward
            r4.onRewindStart(r0)
        L_0x006c:
            java.lang.Runnable r0 = r4.backSeek
            org.telegram.messenger.AndroidUtilities.cancelRunOnUIThread(r0)
            java.lang.Runnable r0 = r4.backSeek
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r0)
            if (r3 == 0) goto L_0x008b
            java.lang.Runnable r0 = r4.updateRewindRunnable
            if (r0 == 0) goto L_0x007f
            org.telegram.messenger.AndroidUtilities.cancelRunOnUIThread(r0)
        L_0x007f:
            org.telegram.messenger.video.VideoPlayerRewinder$$ExternalSyntheticLambda0 r0 = new org.telegram.messenger.video.VideoPlayerRewinder$$ExternalSyntheticLambda0
            r0.<init>(r4)
            r4.updateRewindRunnable = r0
            r1 = 2000(0x7d0, double:9.88E-321)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r0, r1)
        L_0x008b:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.video.VideoPlayerRewinder.incrementRewindCount():void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$incrementRewindCount$0() {
        this.updateRewindRunnable = null;
        incrementRewindCount();
    }

    public float getVideoProgress() {
        return ((float) this.rewindBackSeekPlayerPosition) / ((float) this.videoPlayer.getDuration());
    }
}
