package org.telegram.messenger.video;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.Components.PhotoViewerWebView;
import org.telegram.ui.Components.VideoPlayer;

public class VideoPlayerRewinder {
    /* access modifiers changed from: private */
    public final Runnable backSeek = new Runnable() {
        public void run() {
            if (VideoPlayerRewinder.this.videoPlayer != null || VideoPlayerRewinder.this.webView != null) {
                long access$200 = VideoPlayerRewinder.this.getDuration();
                if (access$200 == 0 || access$200 == -9223372036854775807L) {
                    long unused = VideoPlayerRewinder.this.rewindLastTime = System.currentTimeMillis();
                    return;
                }
                long currentTimeMillis = System.currentTimeMillis();
                long access$300 = currentTimeMillis - VideoPlayerRewinder.this.rewindLastTime;
                long unused2 = VideoPlayerRewinder.this.rewindLastTime = currentTimeMillis;
                VideoPlayerRewinder videoPlayerRewinder = VideoPlayerRewinder.this;
                int i = videoPlayerRewinder.rewindCount;
                long j = access$300 * (i == 1 ? 3 : i == 2 ? 6 : 12);
                if (videoPlayerRewinder.rewindForward) {
                    VideoPlayerRewinder.access$514(VideoPlayerRewinder.this, j);
                } else {
                    VideoPlayerRewinder.access$522(VideoPlayerRewinder.this, j);
                }
                if (VideoPlayerRewinder.this.rewindBackSeekPlayerPosition < 0) {
                    long unused3 = VideoPlayerRewinder.this.rewindBackSeekPlayerPosition = 0;
                } else if (VideoPlayerRewinder.this.rewindBackSeekPlayerPosition > access$200) {
                    long unused4 = VideoPlayerRewinder.this.rewindBackSeekPlayerPosition = access$200;
                }
                VideoPlayerRewinder videoPlayerRewinder2 = VideoPlayerRewinder.this;
                if (videoPlayerRewinder2.rewindByBackSeek && videoPlayerRewinder2.rewindLastTime - VideoPlayerRewinder.this.rewindLastUpdatePlayerTime > 350) {
                    VideoPlayerRewinder videoPlayerRewinder3 = VideoPlayerRewinder.this;
                    long unused5 = videoPlayerRewinder3.rewindLastUpdatePlayerTime = videoPlayerRewinder3.rewindLastTime;
                    VideoPlayerRewinder videoPlayerRewinder4 = VideoPlayerRewinder.this;
                    videoPlayerRewinder4.seekTo(videoPlayerRewinder4.rewindBackSeekPlayerPosition);
                }
                long access$500 = VideoPlayerRewinder.this.rewindBackSeekPlayerPosition - VideoPlayerRewinder.this.startRewindFrom;
                float access$5002 = ((float) VideoPlayerRewinder.this.rewindBackSeekPlayerPosition) / ((float) VideoPlayerRewinder.this.getDuration());
                VideoPlayerRewinder videoPlayerRewinder5 = VideoPlayerRewinder.this;
                videoPlayerRewinder5.updateRewindProgressUi(access$500, access$5002, videoPlayerRewinder5.rewindByBackSeek);
                if (VideoPlayerRewinder.this.rewindBackSeekPlayerPosition == 0 || VideoPlayerRewinder.this.rewindBackSeekPlayerPosition >= access$200) {
                    VideoPlayerRewinder videoPlayerRewinder6 = VideoPlayerRewinder.this;
                    if (videoPlayerRewinder6.rewindByBackSeek) {
                        long unused6 = videoPlayerRewinder6.rewindLastUpdatePlayerTime = videoPlayerRewinder6.rewindLastTime;
                        VideoPlayerRewinder videoPlayerRewinder7 = VideoPlayerRewinder.this;
                        videoPlayerRewinder7.seekTo(videoPlayerRewinder7.rewindBackSeekPlayerPosition);
                    }
                    VideoPlayerRewinder.this.cancelRewind();
                }
                VideoPlayerRewinder videoPlayerRewinder8 = VideoPlayerRewinder.this;
                if (videoPlayerRewinder8.rewindCount > 0) {
                    AndroidUtilities.runOnUIThread(videoPlayerRewinder8.backSeek, 16);
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
    /* access modifiers changed from: private */
    public PhotoViewerWebView webView;

    /* access modifiers changed from: protected */
    public void onRewindCanceled() {
    }

    /* access modifiers changed from: protected */
    public void onRewindStart(boolean z) {
    }

    /* access modifiers changed from: protected */
    public void updateRewindProgressUi(long j, float f, boolean z) {
    }

    static /* synthetic */ long access$514(VideoPlayerRewinder videoPlayerRewinder, long j) {
        long j2 = videoPlayerRewinder.rewindBackSeekPlayerPosition + j;
        videoPlayerRewinder.rewindBackSeekPlayerPosition = j2;
        return j2;
    }

    static /* synthetic */ long access$522(VideoPlayerRewinder videoPlayerRewinder, long j) {
        long j2 = videoPlayerRewinder.rewindBackSeekPlayerPosition - j;
        videoPlayerRewinder.rewindBackSeekPlayerPosition = j2;
        return j2;
    }

    public void startRewind(PhotoViewerWebView photoViewerWebView, boolean z, float f) {
        this.webView = photoViewerWebView;
        this.playSpeed = f;
        this.rewindForward = z;
        cancelRewind();
        incrementRewindCount();
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
            if (!(this.videoPlayer == null && this.webView == null)) {
                if (this.rewindByBackSeek) {
                    seekTo(this.rewindBackSeekPlayerPosition);
                } else {
                    seekTo(getCurrentPosition());
                }
                setPlaybackSpeed(this.playSpeed);
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

    /* JADX WARNING: Code restructure failed: missing block: B:25:0x0046, code lost:
        if (r0 != 2) goto L_0x0049;
     */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x004d  */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x0072  */
    /* JADX WARNING: Removed duplicated region for block: B:37:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void incrementRewindCount() {
        /*
            r4 = this;
            org.telegram.ui.Components.VideoPlayer r0 = r4.videoPlayer
            if (r0 != 0) goto L_0x0009
            org.telegram.ui.Components.PhotoViewerWebView r0 = r4.webView
            if (r0 != 0) goto L_0x0009
            return
        L_0x0009:
            int r0 = r4.rewindCount
            r1 = 1
            int r0 = r0 + r1
            r4.rewindCount = r0
            r2 = 0
            if (r0 != r1) goto L_0x0021
            boolean r0 = r4.rewindForward
            if (r0 == 0) goto L_0x001f
            boolean r0 = r4.isPlaying()
            if (r0 == 0) goto L_0x001f
            r4.rewindByBackSeek = r2
            goto L_0x0021
        L_0x001f:
            r4.rewindByBackSeek = r1
        L_0x0021:
            boolean r0 = r4.rewindForward
            r3 = 2
            if (r0 == 0) goto L_0x0042
            boolean r0 = r4.rewindByBackSeek
            if (r0 != 0) goto L_0x0042
            int r0 = r4.rewindCount
            if (r0 != r1) goto L_0x0034
            r0 = 1082130432(0x40800000, float:4.0)
            r4.setPlaybackSpeed(r0)
            goto L_0x0048
        L_0x0034:
            if (r0 != r3) goto L_0x003c
            r0 = 1088421888(0x40e00000, float:7.0)
            r4.setPlaybackSpeed(r0)
            goto L_0x0048
        L_0x003c:
            r0 = 1095761920(0x41500000, float:13.0)
            r4.setPlaybackSpeed(r0)
            goto L_0x0049
        L_0x0042:
            int r0 = r4.rewindCount
            if (r0 == r1) goto L_0x0048
            if (r0 != r3) goto L_0x0049
        L_0x0048:
            r2 = 1
        L_0x0049:
            int r0 = r4.rewindCount
            if (r0 != r1) goto L_0x0066
            long r0 = r4.getCurrentPosition()
            r4.rewindBackSeekPlayerPosition = r0
            long r0 = java.lang.System.currentTimeMillis()
            r4.rewindLastTime = r0
            r4.rewindLastUpdatePlayerTime = r0
            long r0 = r4.getCurrentPosition()
            r4.startRewindFrom = r0
            boolean r0 = r4.rewindForward
            r4.onRewindStart(r0)
        L_0x0066:
            java.lang.Runnable r0 = r4.backSeek
            org.telegram.messenger.AndroidUtilities.cancelRunOnUIThread(r0)
            java.lang.Runnable r0 = r4.backSeek
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r0)
            if (r2 == 0) goto L_0x0085
            java.lang.Runnable r0 = r4.updateRewindRunnable
            if (r0 == 0) goto L_0x0079
            org.telegram.messenger.AndroidUtilities.cancelRunOnUIThread(r0)
        L_0x0079:
            org.telegram.messenger.video.VideoPlayerRewinder$$ExternalSyntheticLambda0 r0 = new org.telegram.messenger.video.VideoPlayerRewinder$$ExternalSyntheticLambda0
            r0.<init>(r4)
            r4.updateRewindRunnable = r0
            r1 = 2000(0x7d0, double:9.88E-321)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r0, r1)
        L_0x0085:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.video.VideoPlayerRewinder.incrementRewindCount():void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$incrementRewindCount$0() {
        this.updateRewindRunnable = null;
        incrementRewindCount();
    }

    /* access modifiers changed from: private */
    public void seekTo(long j) {
        PhotoViewerWebView photoViewerWebView = this.webView;
        if (photoViewerWebView != null) {
            photoViewerWebView.seekTo(j);
            return;
        }
        VideoPlayer videoPlayer2 = this.videoPlayer;
        if (videoPlayer2 != null) {
            videoPlayer2.seekTo(j);
        }
    }

    private void setPlaybackSpeed(float f) {
        PhotoViewerWebView photoViewerWebView = this.webView;
        if (photoViewerWebView != null) {
            photoViewerWebView.setPlaybackSpeed(f);
            return;
        }
        VideoPlayer videoPlayer2 = this.videoPlayer;
        if (videoPlayer2 != null) {
            videoPlayer2.setPlaybackSpeed(f);
        }
    }

    private long getCurrentPosition() {
        PhotoViewerWebView photoViewerWebView = this.webView;
        if (photoViewerWebView != null) {
            return (long) photoViewerWebView.getCurrentPosition();
        }
        VideoPlayer videoPlayer2 = this.videoPlayer;
        if (videoPlayer2 == null) {
            return 0;
        }
        return videoPlayer2.getCurrentPosition();
    }

    /* access modifiers changed from: private */
    public long getDuration() {
        PhotoViewerWebView photoViewerWebView = this.webView;
        if (photoViewerWebView != null) {
            return (long) photoViewerWebView.getVideoDuration();
        }
        VideoPlayer videoPlayer2 = this.videoPlayer;
        if (videoPlayer2 == null) {
            return 0;
        }
        return videoPlayer2.getDuration();
    }

    private boolean isPlaying() {
        PhotoViewerWebView photoViewerWebView = this.webView;
        if (photoViewerWebView != null) {
            return photoViewerWebView.isPlaying();
        }
        VideoPlayer videoPlayer2 = this.videoPlayer;
        if (videoPlayer2 == null) {
            return false;
        }
        return videoPlayer2.isPlaying();
    }

    public float getVideoProgress() {
        return ((float) this.rewindBackSeekPlayerPosition) / ((float) getDuration());
    }
}
