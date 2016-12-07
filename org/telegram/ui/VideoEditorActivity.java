package org.telegram.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.graphics.SurfaceTexture;
import android.media.MediaCodecInfo;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.view.Surface;
import android.view.TextureView;
import android.view.TextureView.SurfaceTextureListener;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import com.coremedia.iso.IsoFile;
import com.coremedia.iso.boxes.Box;
import com.coremedia.iso.boxes.Container;
import com.coremedia.iso.boxes.MediaBox;
import com.coremedia.iso.boxes.MediaHeaderBox;
import com.coremedia.iso.boxes.TrackBox;
import com.coremedia.iso.boxes.TrackHeaderBox;
import com.googlecode.mp4parser.util.Matrix;
import com.googlecode.mp4parser.util.Path;
import java.io.File;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.exoplayer.DefaultLoadControl;
import org.telegram.messenger.volley.DefaultRetryPolicy;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.PickerBottomLayoutViewer;
import org.telegram.ui.Components.VideoSeekBarView;
import org.telegram.ui.Components.VideoSeekBarView.SeekBarDelegate;
import org.telegram.ui.Components.VideoTimelineView;
import org.telegram.ui.Components.VideoTimelineView.VideoTimelineViewDelegate;

@TargetApi(16)
public class VideoEditorActivity extends BaseFragment implements NotificationCenterDelegate {
    private long audioFramesSize;
    private int bitrate;
    private ImageView captionItem;
    private ImageView compressItem;
    private boolean created;
    private VideoEditorActivityDelegate delegate;
    private long endTime;
    private long esimatedDuration;
    private int estimatedSize;
    private float lastProgress;
    private ImageView muteItem;
    private boolean muteVideo;
    private boolean needCompressVideo;
    private boolean needSeek;
    private int originalBitrate;
    private int originalHeight;
    private long originalSize;
    private int originalWidth;
    private ImageView playButton;
    private boolean playerPrepared;
    private Runnable progressRunnable = new Runnable() {
        public void run() {
            while (true) {
                synchronized (VideoEditorActivity.this.sync) {
                    try {
                        boolean playerCheck = VideoEditorActivity.this.videoPlayer != null && VideoEditorActivity.this.videoPlayer.isPlaying();
                    } catch (Throwable e) {
                        playerCheck = false;
                        FileLog.e("tmessages", e);
                    }
                }
                if (playerCheck) {
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            if (VideoEditorActivity.this.videoPlayer != null && VideoEditorActivity.this.videoPlayer.isPlaying()) {
                                float startTime = VideoEditorActivity.this.videoTimelineView.getLeftProgress() * VideoEditorActivity.this.videoDuration;
                                float endTime = VideoEditorActivity.this.videoTimelineView.getRightProgress() * VideoEditorActivity.this.videoDuration;
                                if (startTime == endTime) {
                                    startTime = endTime - 0.01f;
                                }
                                float lrdiff = VideoEditorActivity.this.videoTimelineView.getRightProgress() - VideoEditorActivity.this.videoTimelineView.getLeftProgress();
                                float progress = VideoEditorActivity.this.videoTimelineView.getLeftProgress() + (lrdiff * ((((float) VideoEditorActivity.this.videoPlayer.getCurrentPosition()) - startTime) / (endTime - startTime)));
                                if (progress > VideoEditorActivity.this.lastProgress) {
                                    VideoEditorActivity.this.videoSeekBarView.setProgress(progress);
                                    VideoEditorActivity.this.lastProgress = progress;
                                }
                                if (((float) VideoEditorActivity.this.videoPlayer.getCurrentPosition()) >= endTime) {
                                    try {
                                        VideoEditorActivity.this.videoPlayer.pause();
                                        VideoEditorActivity.this.onPlayComplete();
                                    } catch (Throwable e) {
                                        FileLog.e("tmessages", e);
                                    }
                                }
                            }
                        }
                    });
                    try {
                        Thread.sleep(50);
                    } catch (Throwable e2) {
                        FileLog.e("tmessages", e2);
                    }
                } else {
                    synchronized (VideoEditorActivity.this.sync) {
                        VideoEditorActivity.this.thread = null;
                    }
                    return;
                }
            }
        }
    };
    private int resultHeight;
    private int resultWidth;
    private int rotationValue;
    private long startTime;
    private final Object sync = new Object();
    private TextureView textureView;
    private Thread thread;
    private float videoDuration;
    private long videoFramesSize;
    private String videoPath;
    private MediaPlayer videoPlayer;
    private VideoSeekBarView videoSeekBarView;
    private VideoTimelineView videoTimelineView;

    public interface VideoEditorActivityDelegate {
        void didFinishEditVideo(String str, long j, long j2, int i, int i2, int i3, int i4, int i5, int i6, long j3, long j4);
    }

    public VideoEditorActivity(Bundle args) {
        super(args);
        this.videoPath = args.getString("videoPath");
    }

    public boolean onFragmentCreate() {
        if (this.created) {
            return true;
        }
        if (this.videoPath == null || !processOpenVideo()) {
            return false;
        }
        this.videoPlayer = new MediaPlayer();
        this.videoPlayer.setOnCompletionListener(new OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        VideoEditorActivity.this.onPlayComplete();
                    }
                });
            }
        });
        this.videoPlayer.setOnPreparedListener(new OnPreparedListener() {
            public void onPrepared(MediaPlayer mp) {
                VideoEditorActivity.this.playerPrepared = true;
                if (VideoEditorActivity.this.videoTimelineView != null && VideoEditorActivity.this.videoPlayer != null) {
                    VideoEditorActivity.this.videoPlayer.seekTo((int) (VideoEditorActivity.this.videoTimelineView.getLeftProgress() * VideoEditorActivity.this.videoDuration));
                }
            }
        });
        try {
            this.videoPlayer.setDataSource(this.videoPath);
            this.videoPlayer.prepareAsync();
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.closeChats);
            this.created = true;
            return super.onFragmentCreate();
        } catch (Throwable e) {
            FileLog.e("tmessages", e);
            return false;
        }
    }

    public void onFragmentDestroy() {
        if (this.videoTimelineView != null) {
            this.videoTimelineView.destroy();
        }
        if (this.videoPlayer != null) {
            try {
                this.videoPlayer.stop();
                this.videoPlayer.release();
                this.videoPlayer = null;
            } catch (Throwable e) {
                FileLog.e("tmessages", e);
            }
        }
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.closeChats);
        super.onFragmentDestroy();
    }

    public View createView(Context context) {
        this.needCompressVideo = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).getBoolean("compress_video", true);
        this.actionBar.setBackgroundColor(-16777216);
        this.actionBar.setItemsBackgroundColor(Theme.ACTION_BAR_PICKER_SELECTOR_COLOR);
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setTitle(LocaleController.getString("AttachVideo", R.string.AttachVideo));
        this.actionBar.setSubtitleColor(-1);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    VideoEditorActivity.this.finishFragment();
                }
            }
        });
        this.fragmentView = new FrameLayout(context) {
            int lastWidth;

            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int width = MeasureSpec.getSize(widthMeasureSpec);
                int height = MeasureSpec.getSize(heightMeasureSpec) - AndroidUtilities.dp(166.0f);
                int vwidth = (VideoEditorActivity.this.rotationValue == 90 || VideoEditorActivity.this.rotationValue == 270) ? VideoEditorActivity.this.originalHeight : VideoEditorActivity.this.originalWidth;
                int vheight = (VideoEditorActivity.this.rotationValue == 90 || VideoEditorActivity.this.rotationValue == 270) ? VideoEditorActivity.this.originalWidth : VideoEditorActivity.this.originalHeight;
                float ar = ((float) vwidth) / ((float) vheight);
                if (((float) width) / ((float) vwidth) > ((float) height) / ((float) vheight)) {
                    width = (int) (((float) height) * ar);
                } else {
                    height = (int) (((float) width) / ar);
                }
                if (VideoEditorActivity.this.textureView != null) {
                    LayoutParams layoutParams = (LayoutParams) VideoEditorActivity.this.textureView.getLayoutParams();
                    layoutParams.width = width;
                    layoutParams.height = height;
                }
                if (this.lastWidth != width) {
                    VideoEditorActivity.this.videoTimelineView.clearFrames();
                }
                this.lastWidth = width;
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            }

            protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
                super.onLayout(changed, left, top, right, bottom);
                int l = ((right - left) - VideoEditorActivity.this.textureView.getWidth()) / 2;
                VideoEditorActivity.this.textureView.layout(l, AndroidUtilities.dp(14.0f), VideoEditorActivity.this.textureView.getMeasuredWidth() + l, AndroidUtilities.dp(14.0f) + VideoEditorActivity.this.textureView.getMeasuredHeight());
            }
        };
        this.fragmentView.setBackgroundColor(-16777216);
        FrameLayout frameLayout = this.fragmentView;
        PickerBottomLayoutViewer pickerView = new PickerBottomLayoutViewer(context);
        pickerView.setBackgroundColor(0);
        pickerView.updateSelectedCount(0, false);
        frameLayout.addView(pickerView, LayoutHelper.createFrame(-1, 48, 83));
        pickerView.cancelButton.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                VideoEditorActivity.this.finishFragment();
            }
        });
        pickerView.doneButton.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                synchronized (VideoEditorActivity.this.sync) {
                    if (VideoEditorActivity.this.videoPlayer != null) {
                        try {
                            VideoEditorActivity.this.videoPlayer.stop();
                            VideoEditorActivity.this.videoPlayer.release();
                            VideoEditorActivity.this.videoPlayer = null;
                        } catch (Throwable e) {
                            FileLog.e("tmessages", e);
                        }
                    }
                }
                if (VideoEditorActivity.this.delegate != null) {
                    if (VideoEditorActivity.this.compressItem.getVisibility() == 8 || (VideoEditorActivity.this.compressItem.getVisibility() == 0 && !VideoEditorActivity.this.needCompressVideo)) {
                        VideoEditorActivity.this.delegate.didFinishEditVideo(VideoEditorActivity.this.videoPath, VideoEditorActivity.this.startTime, VideoEditorActivity.this.endTime, VideoEditorActivity.this.originalWidth, VideoEditorActivity.this.originalHeight, VideoEditorActivity.this.rotationValue, VideoEditorActivity.this.originalWidth, VideoEditorActivity.this.originalHeight, VideoEditorActivity.this.muteVideo ? -1 : VideoEditorActivity.this.originalBitrate, (long) VideoEditorActivity.this.estimatedSize, VideoEditorActivity.this.esimatedDuration);
                    } else {
                        VideoEditorActivity.this.delegate.didFinishEditVideo(VideoEditorActivity.this.videoPath, VideoEditorActivity.this.startTime, VideoEditorActivity.this.endTime, VideoEditorActivity.this.resultWidth, VideoEditorActivity.this.resultHeight, VideoEditorActivity.this.rotationValue, VideoEditorActivity.this.originalWidth, VideoEditorActivity.this.originalHeight, VideoEditorActivity.this.muteVideo ? -1 : VideoEditorActivity.this.bitrate, (long) VideoEditorActivity.this.estimatedSize, VideoEditorActivity.this.esimatedDuration);
                    }
                }
                VideoEditorActivity.this.finishFragment();
            }
        });
        LinearLayout itemsLayout = new LinearLayout(context);
        itemsLayout.setOrientation(0);
        pickerView.addView(itemsLayout, LayoutHelper.createFrame(-2, 48, 49));
        this.captionItem = new ImageView(context);
        this.captionItem.setScaleType(ScaleType.CENTER);
        this.captionItem.setImageResource(R.drawable.photo_text);
        this.captionItem.setBackgroundDrawable(Theme.createBarSelectorDrawable(Theme.ACTION_BAR_WHITE_SELECTOR_COLOR));
        itemsLayout.addView(this.captionItem, LayoutHelper.createLinear(56, 48));
        this.captionItem.setVisibility(8);
        this.captionItem.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            }
        });
        this.compressItem = new ImageView(context);
        this.compressItem.setScaleType(ScaleType.CENTER);
        this.compressItem.setImageResource(this.needCompressVideo ? R.drawable.hd_off : R.drawable.hd_on);
        this.compressItem.setBackgroundDrawable(Theme.createBarSelectorDrawable(Theme.ACTION_BAR_WHITE_SELECTOR_COLOR));
        ImageView imageView = this.compressItem;
        int i = (this.originalHeight == this.resultHeight && this.originalWidth == this.resultWidth) ? 8 : 0;
        imageView.setVisibility(i);
        itemsLayout.addView(this.compressItem, LayoutHelper.createLinear(56, 48));
        this.compressItem.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                boolean z;
                VideoEditorActivity videoEditorActivity = VideoEditorActivity.this;
                if (VideoEditorActivity.this.needCompressVideo) {
                    z = false;
                } else {
                    z = true;
                }
                videoEditorActivity.needCompressVideo = z;
                Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit();
                editor.putBoolean("compress_video", VideoEditorActivity.this.needCompressVideo);
                editor.commit();
                VideoEditorActivity.this.compressItem.setImageResource(VideoEditorActivity.this.needCompressVideo ? R.drawable.hd_off : R.drawable.hd_on);
                VideoEditorActivity.this.updateVideoInfo();
            }
        });
        if (VERSION.SDK_INT < 18) {
            try {
                MediaCodecInfo codecInfo = MediaController.selectCodec("video/avc");
                if (codecInfo == null) {
                    this.compressItem.setVisibility(8);
                } else {
                    String name = codecInfo.getName();
                    if (name.equals("OMX.google.h264.encoder") || name.equals("OMX.ST.VFM.H264Enc") || name.equals("OMX.Exynos.avc.enc") || name.equals("OMX.MARVELL.VIDEO.HW.CODA7542ENCODER") || name.equals("OMX.MARVELL.VIDEO.H264ENCODER") || name.equals("OMX.k3.video.encoder.avc") || name.equals("OMX.TI.DUCATI1.VIDEO.H264E")) {
                        this.compressItem.setVisibility(8);
                    } else if (MediaController.selectColorFormat(codecInfo, "video/avc") == 0) {
                        this.compressItem.setVisibility(8);
                    }
                }
            } catch (Throwable e) {
                this.compressItem.setVisibility(8);
                FileLog.e("tmessages", e);
            }
        }
        this.muteItem = new ImageView(context);
        this.muteItem.setScaleType(ScaleType.CENTER);
        this.muteItem.setBackgroundDrawable(Theme.createBarSelectorDrawable(Theme.ACTION_BAR_WHITE_SELECTOR_COLOR));
        itemsLayout.addView(this.muteItem, LayoutHelper.createLinear(56, 48));
        this.muteItem.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                VideoEditorActivity.this.muteVideo = !VideoEditorActivity.this.muteVideo;
                VideoEditorActivity.this.updateMuteButton();
            }
        });
        this.videoTimelineView = new VideoTimelineView(context);
        this.videoTimelineView.setVideoPath(this.videoPath);
        this.videoTimelineView.setDelegate(new VideoTimelineViewDelegate() {
            public void onLeftProgressChanged(float progress) {
                if (VideoEditorActivity.this.videoPlayer != null && VideoEditorActivity.this.playerPrepared) {
                    try {
                        if (VideoEditorActivity.this.videoPlayer.isPlaying()) {
                            VideoEditorActivity.this.videoPlayer.pause();
                            VideoEditorActivity.this.playButton.setImageResource(R.drawable.video_edit_play);
                        }
                        VideoEditorActivity.this.videoPlayer.setOnSeekCompleteListener(null);
                        VideoEditorActivity.this.videoPlayer.seekTo((int) (VideoEditorActivity.this.videoDuration * progress));
                    } catch (Throwable e) {
                        FileLog.e("tmessages", e);
                    }
                    VideoEditorActivity.this.needSeek = true;
                    VideoEditorActivity.this.videoSeekBarView.setProgress(VideoEditorActivity.this.videoTimelineView.getLeftProgress());
                    VideoEditorActivity.this.updateVideoInfo();
                }
            }

            public void onRifhtProgressChanged(float progress) {
                if (VideoEditorActivity.this.videoPlayer != null && VideoEditorActivity.this.playerPrepared) {
                    try {
                        if (VideoEditorActivity.this.videoPlayer.isPlaying()) {
                            VideoEditorActivity.this.videoPlayer.pause();
                            VideoEditorActivity.this.playButton.setImageResource(R.drawable.video_edit_play);
                        }
                        VideoEditorActivity.this.videoPlayer.setOnSeekCompleteListener(null);
                        VideoEditorActivity.this.videoPlayer.seekTo((int) (VideoEditorActivity.this.videoDuration * progress));
                    } catch (Throwable e) {
                        FileLog.e("tmessages", e);
                    }
                    VideoEditorActivity.this.needSeek = true;
                    VideoEditorActivity.this.videoSeekBarView.setProgress(VideoEditorActivity.this.videoTimelineView.getLeftProgress());
                    VideoEditorActivity.this.updateVideoInfo();
                }
            }
        });
        frameLayout.addView(this.videoTimelineView, LayoutHelper.createFrame(-1, 44.0f, 83, 0.0f, 0.0f, 0.0f, 67.0f));
        this.videoSeekBarView = new VideoSeekBarView(context);
        this.videoSeekBarView.setDelegate(new SeekBarDelegate() {
            public void onSeekBarDrag(float progress) {
                if (progress < VideoEditorActivity.this.videoTimelineView.getLeftProgress()) {
                    progress = VideoEditorActivity.this.videoTimelineView.getLeftProgress();
                    VideoEditorActivity.this.videoSeekBarView.setProgress(progress);
                } else if (progress > VideoEditorActivity.this.videoTimelineView.getRightProgress()) {
                    progress = VideoEditorActivity.this.videoTimelineView.getRightProgress();
                    VideoEditorActivity.this.videoSeekBarView.setProgress(progress);
                }
                if (VideoEditorActivity.this.videoPlayer != null && VideoEditorActivity.this.playerPrepared) {
                    if (VideoEditorActivity.this.videoPlayer.isPlaying()) {
                        try {
                            VideoEditorActivity.this.videoPlayer.seekTo((int) (VideoEditorActivity.this.videoDuration * progress));
                            VideoEditorActivity.this.lastProgress = progress;
                            return;
                        } catch (Throwable e) {
                            FileLog.e("tmessages", e);
                            return;
                        }
                    }
                    VideoEditorActivity.this.lastProgress = progress;
                    VideoEditorActivity.this.needSeek = true;
                }
            }
        });
        frameLayout.addView(this.videoSeekBarView, LayoutHelper.createFrame(-1, 40.0f, 83, 11.0f, 0.0f, 11.0f, 112.0f));
        this.textureView = new TextureView(context);
        this.textureView.setSurfaceTextureListener(new SurfaceTextureListener() {
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                if (VideoEditorActivity.this.textureView != null && VideoEditorActivity.this.textureView.isAvailable() && VideoEditorActivity.this.videoPlayer != null) {
                    try {
                        VideoEditorActivity.this.videoPlayer.setSurface(new Surface(VideoEditorActivity.this.textureView.getSurfaceTexture()));
                        if (VideoEditorActivity.this.playerPrepared) {
                            VideoEditorActivity.this.videoPlayer.seekTo((int) (VideoEditorActivity.this.videoTimelineView.getLeftProgress() * VideoEditorActivity.this.videoDuration));
                        }
                    } catch (Throwable e) {
                        FileLog.e("tmessages", e);
                    }
                }
            }

            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            }

            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                if (VideoEditorActivity.this.videoPlayer != null) {
                    VideoEditorActivity.this.videoPlayer.setDisplay(null);
                }
                return true;
            }

            public void onSurfaceTextureUpdated(SurfaceTexture surface) {
            }
        });
        frameLayout.addView(this.textureView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 14.0f, 0.0f, 140.0f));
        this.playButton = new ImageView(context);
        this.playButton.setScaleType(ScaleType.CENTER);
        this.playButton.setImageResource(R.drawable.video_edit_play);
        this.playButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (VideoEditorActivity.this.videoPlayer != null && VideoEditorActivity.this.playerPrepared) {
                    if (VideoEditorActivity.this.videoPlayer.isPlaying()) {
                        VideoEditorActivity.this.videoPlayer.pause();
                        VideoEditorActivity.this.playButton.setImageResource(R.drawable.video_edit_play);
                        return;
                    }
                    try {
                        VideoEditorActivity.this.playButton.setImageDrawable(null);
                        VideoEditorActivity.this.lastProgress = 0.0f;
                        if (VideoEditorActivity.this.needSeek) {
                            VideoEditorActivity.this.videoPlayer.seekTo((int) (VideoEditorActivity.this.videoDuration * VideoEditorActivity.this.videoSeekBarView.getProgress()));
                            VideoEditorActivity.this.needSeek = false;
                        }
                        VideoEditorActivity.this.videoPlayer.setOnSeekCompleteListener(new OnSeekCompleteListener() {
                            public void onSeekComplete(MediaPlayer mp) {
                                float startTime = VideoEditorActivity.this.videoTimelineView.getLeftProgress() * VideoEditorActivity.this.videoDuration;
                                float endTime = VideoEditorActivity.this.videoTimelineView.getRightProgress() * VideoEditorActivity.this.videoDuration;
                                if (startTime == endTime) {
                                    startTime = endTime - 0.01f;
                                }
                                VideoEditorActivity.this.lastProgress = (((float) VideoEditorActivity.this.videoPlayer.getCurrentPosition()) - startTime) / (endTime - startTime);
                                VideoEditorActivity.this.lastProgress = VideoEditorActivity.this.videoTimelineView.getLeftProgress() + (VideoEditorActivity.this.lastProgress * (VideoEditorActivity.this.videoTimelineView.getRightProgress() - VideoEditorActivity.this.videoTimelineView.getLeftProgress()));
                                VideoEditorActivity.this.videoSeekBarView.setProgress(VideoEditorActivity.this.lastProgress);
                            }
                        });
                        VideoEditorActivity.this.videoPlayer.start();
                        synchronized (VideoEditorActivity.this.sync) {
                            if (VideoEditorActivity.this.thread == null) {
                                VideoEditorActivity.this.thread = new Thread(VideoEditorActivity.this.progressRunnable);
                                VideoEditorActivity.this.thread.start();
                            }
                        }
                    } catch (Throwable e) {
                        FileLog.e("tmessages", e);
                    }
                }
            }
        });
        frameLayout.addView(this.playButton, LayoutHelper.createFrame(100, 100.0f, 17, 0.0f, 0.0f, 0.0f, 70.0f));
        updateVideoInfo();
        updateMuteButton();
        return this.fragmentView;
    }

    public void didReceivedNotification(int id, Object... args) {
        if (id == NotificationCenter.closeChats) {
            removeSelfFromStack();
        }
    }

    public void updateMuteButton() {
        if (this.videoPlayer != null) {
            float volume = this.muteVideo ? 0.0f : DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
            if (this.videoPlayer != null) {
                this.videoPlayer.setVolume(volume, volume);
            }
        }
        if (this.muteVideo) {
            this.actionBar.setTitle(LocaleController.getString("AttachGif", R.string.AttachGif));
            this.muteItem.setImageResource(R.drawable.volume_off);
            if (this.captionItem.getVisibility() == 0) {
                this.needCompressVideo = true;
                this.compressItem.setImageResource(R.drawable.hd_off);
                this.compressItem.setClickable(false);
                this.compressItem.setAlpha(DefaultLoadControl.DEFAULT_HIGH_BUFFER_LOAD);
                this.captionItem.setEnabled(false);
                return;
            }
            return;
        }
        this.actionBar.setTitle(LocaleController.getString("AttachVideo", R.string.AttachVideo));
        this.muteItem.setImageResource(R.drawable.volume_on);
        if (this.captionItem.getVisibility() == 0) {
            this.compressItem.setClickable(true);
            this.compressItem.setAlpha(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            this.captionItem.setEnabled(true);
        }
    }

    private void onPlayComplete() {
        if (this.playButton != null) {
            this.playButton.setImageResource(R.drawable.video_edit_play);
        }
        if (!(this.videoSeekBarView == null || this.videoTimelineView == null)) {
            this.videoSeekBarView.setProgress(this.videoTimelineView.getLeftProgress());
        }
        try {
            if (this.videoPlayer != null && this.videoTimelineView != null) {
                this.videoPlayer.seekTo((int) (this.videoTimelineView.getLeftProgress() * this.videoDuration));
            }
        } catch (Throwable e) {
            FileLog.e("tmessages", e);
        }
    }

    private void updateVideoInfo() {
        if (this.actionBar != null) {
            int width;
            int height;
            this.esimatedDuration = (long) Math.ceil((double) ((this.videoTimelineView.getRightProgress() - this.videoTimelineView.getLeftProgress()) * this.videoDuration));
            if (this.compressItem.getVisibility() == 8 || (this.compressItem.getVisibility() == 0 && !this.needCompressVideo)) {
                width = (this.rotationValue == 90 || this.rotationValue == 270) ? this.originalHeight : this.originalWidth;
                height = (this.rotationValue == 90 || this.rotationValue == 270) ? this.originalWidth : this.originalHeight;
                this.estimatedSize = (int) (((float) this.originalSize) * (((float) this.esimatedDuration) / this.videoDuration));
            } else {
                width = (this.rotationValue == 90 || this.rotationValue == 270) ? this.resultHeight : this.resultWidth;
                height = (this.rotationValue == 90 || this.rotationValue == 270) ? this.resultWidth : this.resultHeight;
                this.estimatedSize = calculateEstimatedSize(((float) this.esimatedDuration) / this.videoDuration);
            }
            if (this.videoTimelineView.getLeftProgress() == 0.0f) {
                this.startTime = -1;
            } else {
                this.startTime = ((long) (this.videoTimelineView.getLeftProgress() * this.videoDuration)) * 1000;
            }
            if (this.videoTimelineView.getRightProgress() == DefaultRetryPolicy.DEFAULT_BACKOFF_MULT) {
                this.endTime = -1;
            } else {
                this.endTime = ((long) (this.videoTimelineView.getRightProgress() * this.videoDuration)) * 1000;
            }
            String videoDimension = String.format("%dx%d", new Object[]{Integer.valueOf(width), Integer.valueOf(height)});
            int seconds = ((int) Math.ceil((double) (this.esimatedDuration / 1000))) - (((int) ((this.esimatedDuration / 1000) / 60)) * 60);
            String videoTimeSize = String.format("%d:%02d, ~%s", new Object[]{Integer.valueOf((int) ((this.esimatedDuration / 1000) / 60)), Integer.valueOf(seconds), AndroidUtilities.formatFileSize((long) this.estimatedSize)});
            this.actionBar.setSubtitle(String.format("%s, %s", new Object[]{videoDimension, videoTimeSize}));
        }
    }

    public void setDelegate(VideoEditorActivityDelegate videoEditorActivityDelegate) {
        this.delegate = videoEditorActivityDelegate;
    }

    private boolean processOpenVideo() {
        try {
            this.originalSize = new File(this.videoPath).length();
            Container isoFile = new IsoFile(this.videoPath);
            List<Box> boxes = Path.getPaths(isoFile, "/moov/trak/");
            TrackHeaderBox trackHeaderBox = null;
            boolean isAvc = true;
            boolean isMp4A = true;
            if (Path.getPath(isoFile, "/moov/trak/mdia/minf/stbl/stsd/mp4a/") == null) {
                isMp4A = false;
            }
            if (!isMp4A) {
                return false;
            }
            int i;
            if (Path.getPath(isoFile, "/moov/trak/mdia/minf/stbl/stsd/avc1/") == null) {
                isAvc = false;
            }
            for (Box box : boxes) {
                TrackBox trackBox = (TrackBox) box;
                long sampleSizes = 0;
                long trackBitrate = 0;
                try {
                    MediaBox mediaBox = trackBox.getMediaBox();
                    MediaHeaderBox mediaHeaderBox = mediaBox.getMediaHeaderBox();
                    for (long size : mediaBox.getMediaInformationBox().getSampleTableBox().getSampleSizeBox().getSampleSizes()) {
                        sampleSizes += size;
                    }
                    this.videoDuration = ((float) mediaHeaderBox.getDuration()) / ((float) mediaHeaderBox.getTimescale());
                    trackBitrate = (long) ((int) (((float) (8 * sampleSizes)) / this.videoDuration));
                } catch (Throwable e) {
                    FileLog.e("tmessages", e);
                }
                TrackHeaderBox headerBox = trackBox.getTrackHeaderBox();
                if (headerBox.getWidth() == 0.0d || headerBox.getHeight() == 0.0d) {
                    this.audioFramesSize += sampleSizes;
                } else {
                    trackHeaderBox = headerBox;
                    i = (int) ((trackBitrate / 100000) * 100000);
                    this.bitrate = i;
                    this.originalBitrate = i;
                    if (this.bitrate > 900000) {
                        this.bitrate = 900000;
                    }
                    this.videoFramesSize += sampleSizes;
                }
            }
            if (trackHeaderBox == null) {
                return false;
            }
            Matrix matrix = trackHeaderBox.getMatrix();
            if (matrix.equals(Matrix.ROTATE_90)) {
                this.rotationValue = 90;
            } else if (matrix.equals(Matrix.ROTATE_180)) {
                this.rotationValue = 180;
            } else if (matrix.equals(Matrix.ROTATE_270)) {
                this.rotationValue = 270;
            }
            i = (int) trackHeaderBox.getWidth();
            this.originalWidth = i;
            this.resultWidth = i;
            i = (int) trackHeaderBox.getHeight();
            this.originalHeight = i;
            this.resultHeight = i;
            if (this.resultWidth > 640 || this.resultHeight > 640) {
                float scale;
                if (this.resultWidth > this.resultHeight) {
                    scale = 640.0f / ((float) this.resultWidth);
                } else {
                    scale = 640.0f / ((float) this.resultHeight);
                }
                this.resultWidth = (int) (((float) this.resultWidth) * scale);
                this.resultHeight = (int) (((float) this.resultHeight) * scale);
                if (this.bitrate != 0) {
                    this.bitrate = (int) (((float) this.bitrate) * Math.max(0.5f, scale));
                    this.videoFramesSize = (long) (((float) (this.bitrate / 8)) * this.videoDuration);
                }
            }
            if (!isAvc && (this.resultWidth == this.originalWidth || this.resultHeight == this.originalHeight)) {
                return false;
            }
            this.videoDuration *= 1000.0f;
            updateVideoInfo();
            return true;
        } catch (Throwable e2) {
            FileLog.e("tmessages", e2);
            return false;
        }
    }

    private int calculateEstimatedSize(float timeDelta) {
        int size = (int) (((float) (this.audioFramesSize + this.videoFramesSize)) * timeDelta);
        return size + ((size / 32768) * 16);
    }
}
