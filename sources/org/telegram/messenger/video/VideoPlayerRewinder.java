package org.telegram.messenger.video;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.Components.VideoPlayer;

public class VideoPlayerRewinder {
    /* access modifiers changed from: private */
    public final Runnable backSeek = new Runnable() {
        public void run() {
            long dt;
            if (VideoPlayerRewinder.this.videoPlayer != null) {
                long duration = VideoPlayerRewinder.this.videoPlayer.getDuration();
                if (duration == 0 || duration == -9223372036854775807L) {
                    long unused = VideoPlayerRewinder.this.rewindLastTime = System.currentTimeMillis();
                    return;
                }
                long t = System.currentTimeMillis();
                long dt2 = t - VideoPlayerRewinder.this.rewindLastTime;
                long unused2 = VideoPlayerRewinder.this.rewindLastTime = t;
                if (VideoPlayerRewinder.this.rewindCount == 1) {
                    dt = dt2 * 3;
                } else if (VideoPlayerRewinder.this.rewindCount == 2) {
                    dt = dt2 * 6;
                } else {
                    dt = dt2 * 12;
                }
                if (VideoPlayerRewinder.this.rewindForward) {
                    VideoPlayerRewinder.access$314(VideoPlayerRewinder.this, dt);
                } else {
                    VideoPlayerRewinder.access$322(VideoPlayerRewinder.this, dt);
                }
                if (VideoPlayerRewinder.this.rewindBackSeekPlayerPosition < 0) {
                    long unused3 = VideoPlayerRewinder.this.rewindBackSeekPlayerPosition = 0;
                } else if (VideoPlayerRewinder.this.rewindBackSeekPlayerPosition > duration) {
                    long unused4 = VideoPlayerRewinder.this.rewindBackSeekPlayerPosition = duration;
                }
                if (VideoPlayerRewinder.this.rewindByBackSeek && VideoPlayerRewinder.this.videoPlayer != null && VideoPlayerRewinder.this.rewindLastTime - VideoPlayerRewinder.this.rewindLastUpdatePlayerTime > 350) {
                    VideoPlayerRewinder videoPlayerRewinder = VideoPlayerRewinder.this;
                    long unused5 = videoPlayerRewinder.rewindLastUpdatePlayerTime = videoPlayerRewinder.rewindLastTime;
                    VideoPlayerRewinder.this.videoPlayer.seekTo(VideoPlayerRewinder.this.rewindBackSeekPlayerPosition);
                }
                if (VideoPlayerRewinder.this.videoPlayer != null) {
                    long timeDiff = VideoPlayerRewinder.this.rewindBackSeekPlayerPosition - VideoPlayerRewinder.this.startRewindFrom;
                    float progress = ((float) VideoPlayerRewinder.this.rewindBackSeekPlayerPosition) / ((float) VideoPlayerRewinder.this.videoPlayer.getDuration());
                    VideoPlayerRewinder videoPlayerRewinder2 = VideoPlayerRewinder.this;
                    videoPlayerRewinder2.updateRewindProgressUi(timeDiff, progress, videoPlayerRewinder2.rewindByBackSeek);
                }
                if (VideoPlayerRewinder.this.rewindBackSeekPlayerPosition == 0 || VideoPlayerRewinder.this.rewindBackSeekPlayerPosition >= duration) {
                    if (VideoPlayerRewinder.this.rewindByBackSeek && VideoPlayerRewinder.this.videoPlayer != null) {
                        VideoPlayerRewinder videoPlayerRewinder3 = VideoPlayerRewinder.this;
                        long unused6 = videoPlayerRewinder3.rewindLastUpdatePlayerTime = videoPlayerRewinder3.rewindLastTime;
                        VideoPlayerRewinder.this.videoPlayer.seekTo(VideoPlayerRewinder.this.rewindBackSeekPlayerPosition);
                    }
                    VideoPlayerRewinder.this.cancelRewind();
                }
                if (VideoPlayerRewinder.this.rewindCount > 0) {
                    AndroidUtilities.runOnUIThread(VideoPlayerRewinder.this.backSeek, 16);
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

    static /* synthetic */ long access$314(VideoPlayerRewinder x0, long x1) {
        long j = x0.rewindBackSeekPlayerPosition + x1;
        x0.rewindBackSeekPlayerPosition = j;
        return j;
    }

    static /* synthetic */ long access$322(VideoPlayerRewinder x0, long x1) {
        long j = x0.rewindBackSeekPlayerPosition - x1;
        x0.rewindBackSeekPlayerPosition = j;
        return j;
    }

    public void startRewind(VideoPlayer videoPlayer2, boolean forward, float playbackSpeed) {
        this.videoPlayer = videoPlayer2;
        this.playSpeed = playbackSpeed;
        this.rewindForward = forward;
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

    private void incrementRewindCount() {
        VideoPlayer videoPlayer2 = this.videoPlayer;
        if (videoPlayer2 != null) {
            int i = this.rewindCount + 1;
            this.rewindCount = i;
            boolean needUpdate = false;
            if (i == 1) {
                if (!this.rewindForward || !videoPlayer2.isPlaying()) {
                    this.rewindByBackSeek = true;
                } else {
                    this.rewindByBackSeek = false;
                }
            }
            if (!this.rewindForward || this.rewindByBackSeek) {
                int i2 = this.rewindCount;
                if (i2 == 1 || i2 == 2) {
                    needUpdate = true;
                }
            } else {
                int i3 = this.rewindCount;
                if (i3 == 1) {
                    this.videoPlayer.setPlaybackSpeed(4.0f);
                    needUpdate = true;
                } else if (i3 == 2) {
                    this.videoPlayer.setPlaybackSpeed(7.0f);
                    needUpdate = true;
                } else {
                    this.videoPlayer.setPlaybackSpeed(13.0f);
                }
            }
            if (this.rewindCount == 1) {
                this.rewindBackSeekPlayerPosition = this.videoPlayer.getCurrentPosition();
                long currentTimeMillis = System.currentTimeMillis();
                this.rewindLastTime = currentTimeMillis;
                this.rewindLastUpdatePlayerTime = currentTimeMillis;
                this.startRewindFrom = this.videoPlayer.getCurrentPosition();
                onRewindStart(this.rewindForward);
            }
            AndroidUtilities.cancelRunOnUIThread(this.backSeek);
            AndroidUtilities.runOnUIThread(this.backSeek);
            if (needUpdate) {
                Runnable runnable = this.updateRewindRunnable;
                if (runnable != null) {
                    AndroidUtilities.cancelRunOnUIThread(runnable);
                }
                VideoPlayerRewinder$$ExternalSyntheticLambda0 videoPlayerRewinder$$ExternalSyntheticLambda0 = new VideoPlayerRewinder$$ExternalSyntheticLambda0(this);
                this.updateRewindRunnable = videoPlayerRewinder$$ExternalSyntheticLambda0;
                AndroidUtilities.runOnUIThread(videoPlayerRewinder$$ExternalSyntheticLambda0, 2000);
            }
        }
    }

    /* renamed from: lambda$incrementRewindCount$0$org-telegram-messenger-video-VideoPlayerRewinder  reason: not valid java name */
    public /* synthetic */ void m1138xfe904fcb() {
        this.updateRewindRunnable = null;
        incrementRewindCount();
    }

    /* access modifiers changed from: protected */
    public void updateRewindProgressUi(long timeDiff, float progress, boolean rewindByBackSeek2) {
    }

    /* access modifiers changed from: protected */
    public void onRewindStart(boolean rewindForward2) {
    }

    /* access modifiers changed from: protected */
    public void onRewindCanceled() {
    }

    public float getVideoProgress() {
        return ((float) this.rewindBackSeekPlayerPosition) / ((float) this.videoPlayer.getDuration());
    }
}
