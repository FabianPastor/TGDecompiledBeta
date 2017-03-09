package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences.Editor;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.SurfaceTexture;
import android.media.MediaCodecInfo;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.TextureView.SurfaceTextureListener;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.googlecode.mp4parser.util.Matrix;
import com.googlecode.mp4parser.util.Path;
import java.io.File;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.VideoEditedInfo;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.Adapter;
import org.telegram.tgnet.TLRPC.BotInlineResult;
import org.telegram.tgnet.TLRPC.TL_message;
import org.telegram.tgnet.TLRPC.TL_messageActionEmpty;
import org.telegram.tgnet.TLRPC.TL_messageMediaEmpty;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Adapters.MentionsAdapter;
import org.telegram.ui.Adapters.MentionsAdapter.MentionsAdapterDelegate;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.PhotoViewerCaptionEnterView;
import org.telegram.ui.Components.PhotoViewerCaptionEnterView.PhotoViewerCaptionEnterViewDelegate;
import org.telegram.ui.Components.PickerBottomLayoutViewer;
import org.telegram.ui.Components.RadialProgressView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;
import org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener;
import org.telegram.ui.Components.SizeNotifierFrameLayoutPhoto;
import org.telegram.ui.Components.VideoSeekBarView;
import org.telegram.ui.Components.VideoSeekBarView.SeekBarDelegate;
import org.telegram.ui.Components.VideoTimelineView;
import org.telegram.ui.Components.VideoTimelineView.VideoTimelineViewDelegate;

@TargetApi(16)
public class VideoEditorActivity extends BaseFragment implements NotificationCenterDelegate {
    private boolean allowMentions;
    private long audioFramesSize;
    private int bitrate;
    private PhotoViewerCaptionEnterView captionEditText;
    private ImageView captionItem;
    private ImageView compressItem;
    private int compressionsCount = -1;
    private boolean created;
    private CharSequence currentCaption;
    private String currentSubtitle;
    private VideoEditorActivityDelegate delegate;
    private long endTime;
    private long esimatedDuration;
    private int estimatedSize;
    private boolean firstCaptionLayout;
    private boolean inPreview;
    private float lastProgress;
    private boolean loadInitialVideo;
    private LinearLayoutManager mentionLayoutManager;
    private AnimatorSet mentionListAnimation;
    private RecyclerListView mentionListView;
    private MentionsAdapter mentionsAdapter;
    private ImageView muteItem;
    private boolean muteVideo;
    private boolean needSeek;
    private int originalBitrate;
    private int originalHeight;
    private long originalSize;
    private int originalWidth;
    private ChatActivity parentChatActivity;
    private PickerBottomLayoutViewer pickerView;
    private ImageView playButton;
    private boolean playerPrepared;
    private int previewViewEnd;
    private int previousCompression;
    private Runnable progressRunnable = new Runnable() {
        public void run() {
            while (true) {
                synchronized (VideoEditorActivity.this.sync) {
                    try {
                        boolean playerCheck = VideoEditorActivity.this.videoPlayer != null && VideoEditorActivity.this.videoPlayer.isPlaying();
                    } catch (Throwable e) {
                        playerCheck = false;
                        FileLog.e(e);
                    }
                }
                if (playerCheck) {
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            if (VideoEditorActivity.this.videoPlayer != null && VideoEditorActivity.this.videoPlayer.isPlaying()) {
                                float startTime;
                                float endTime;
                                float lrdiff;
                                if (VideoEditorActivity.this.inPreview) {
                                    startTime = 0.0f;
                                    endTime = (float) VideoEditorActivity.this.previewViewEnd;
                                    lrdiff = 1.0f;
                                } else {
                                    startTime = VideoEditorActivity.this.videoTimelineView.getLeftProgress() * VideoEditorActivity.this.videoDuration;
                                    endTime = VideoEditorActivity.this.videoTimelineView.getRightProgress() * VideoEditorActivity.this.videoDuration;
                                    lrdiff = VideoEditorActivity.this.videoTimelineView.getRightProgress() - VideoEditorActivity.this.videoTimelineView.getLeftProgress();
                                }
                                if (startTime == endTime) {
                                    startTime = endTime - 0.01f;
                                }
                                float progress = (((float) VideoEditorActivity.this.videoPlayer.getCurrentPosition()) - startTime) / (endTime - startTime);
                                if (!VideoEditorActivity.this.inPreview) {
                                    progress = VideoEditorActivity.this.videoTimelineView.getLeftProgress() + (lrdiff * progress);
                                }
                                if (progress > VideoEditorActivity.this.lastProgress) {
                                    VideoEditorActivity.this.videoSeekBarView.setProgress(progress);
                                    VideoEditorActivity.this.lastProgress = progress;
                                }
                                int position = VideoEditorActivity.this.videoPlayer.getCurrentPosition();
                                if (((float) VideoEditorActivity.this.videoPlayer.getCurrentPosition()) >= endTime) {
                                    try {
                                        VideoEditorActivity.this.videoPlayer.pause();
                                        VideoEditorActivity.this.onPlayComplete();
                                        try {
                                            VideoEditorActivity.this.getParentActivity().getWindow().clearFlags(128);
                                        } catch (Throwable e) {
                                            FileLog.e(e);
                                        }
                                    } catch (Throwable e2) {
                                        FileLog.e(e2);
                                    }
                                }
                            }
                        }
                    });
                    try {
                        Thread.sleep(50);
                    } catch (Throwable e2) {
                        FileLog.e(e2);
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
    private RadialProgressView progressView;
    private QualityChooseView qualityChooseView;
    private PickerBottomLayoutViewer qualityPicker;
    private boolean requestingPreview;
    private int resultHeight;
    private int resultWidth;
    private int rotationValue;
    private int selectedCompression;
    private long startTime;
    private final Object sync = new Object();
    private TextureView textureView;
    private Thread thread;
    private boolean tryStartRequestPreviewOnFinish;
    private float videoDuration;
    private long videoFramesSize;
    private String videoPath;
    private MediaPlayer videoPlayer;
    private MessageObject videoPreviewMessageObject;
    private VideoSeekBarView videoSeekBarView;
    private VideoTimelineView videoTimelineView;

    private class QualityChooseView extends View {
        private int circleSize;
        private int gapSize;
        private int lineSize;
        private boolean moving;
        private Paint paint = new Paint(1);
        private int sideSide;
        private boolean startMoving;
        private int startMovingQuality;
        private float startX;
        private TextPaint textPaint = new TextPaint(1);

        public QualityChooseView(Context context) {
            super(context);
            this.textPaint.setTextSize((float) AndroidUtilities.dp(12.0f));
            this.textPaint.setColor(-3289651);
        }

        public boolean onTouchEvent(MotionEvent event) {
            boolean z = false;
            float x = event.getX();
            int a;
            int cx;
            if (event.getAction() == 0) {
                getParent().requestDisallowInterceptTouchEvent(true);
                a = 0;
                while (a < VideoEditorActivity.this.compressionsCount) {
                    cx = (this.sideSide + (((this.lineSize + (this.gapSize * 2)) + this.circleSize) * a)) + (this.circleSize / 2);
                    if (x <= ((float) (cx - AndroidUtilities.dp(15.0f))) || x >= ((float) (AndroidUtilities.dp(15.0f) + cx))) {
                        a++;
                    } else {
                        if (a == VideoEditorActivity.this.selectedCompression) {
                            z = true;
                        }
                        this.startMoving = z;
                        this.startX = x;
                        this.startMovingQuality = VideoEditorActivity.this.selectedCompression;
                    }
                }
            } else if (event.getAction() == 2) {
                if (this.startMoving) {
                    if (Math.abs(this.startX - x) >= AndroidUtilities.getPixelsInCM(0.5f, true)) {
                        this.moving = true;
                        this.startMoving = false;
                    }
                } else if (this.moving) {
                    a = 0;
                    while (a < VideoEditorActivity.this.compressionsCount) {
                        cx = (this.sideSide + (((this.lineSize + (this.gapSize * 2)) + this.circleSize) * a)) + (this.circleSize / 2);
                        int diff = ((this.lineSize / 2) + (this.circleSize / 2)) + this.gapSize;
                        if (x <= ((float) (cx - diff)) || x >= ((float) (cx + diff))) {
                            a++;
                        } else if (VideoEditorActivity.this.selectedCompression != a) {
                            VideoEditorActivity.this.selectedCompression = a;
                            VideoEditorActivity.this.didChangedCompressionLevel(false);
                            invalidate();
                        }
                    }
                }
            } else if (event.getAction() == 1 || event.getAction() == 3) {
                if (!this.moving) {
                    a = 0;
                    while (a < VideoEditorActivity.this.compressionsCount) {
                        cx = (this.sideSide + (((this.lineSize + (this.gapSize * 2)) + this.circleSize) * a)) + (this.circleSize / 2);
                        if (x <= ((float) (cx - AndroidUtilities.dp(15.0f))) || x >= ((float) (AndroidUtilities.dp(15.0f) + cx))) {
                            a++;
                        } else if (VideoEditorActivity.this.selectedCompression != a) {
                            VideoEditorActivity.this.selectedCompression = a;
                            VideoEditorActivity.this.didChangedCompressionLevel(true);
                            invalidate();
                        }
                    }
                } else if (VideoEditorActivity.this.selectedCompression != this.startMovingQuality) {
                    VideoEditorActivity.this.requestVideoPreview(1);
                }
                this.startMoving = false;
                this.moving = false;
            }
            return true;
        }

        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            int width = MeasureSpec.getSize(widthMeasureSpec);
            this.circleSize = AndroidUtilities.dp(12.0f);
            this.gapSize = AndroidUtilities.dp(2.0f);
            this.sideSide = AndroidUtilities.dp(18.0f);
            this.lineSize = (((getMeasuredWidth() - (this.circleSize * VideoEditorActivity.this.compressionsCount)) - (this.gapSize * 8)) - (this.sideSide * 2)) / (VideoEditorActivity.this.compressionsCount - 1);
        }

        protected void onDraw(Canvas canvas) {
            int cy = (getMeasuredHeight() / 2) + AndroidUtilities.dp(6.0f);
            int a = 0;
            while (a < VideoEditorActivity.this.compressionsCount) {
                String text;
                int cx = (this.sideSide + (((this.lineSize + (this.gapSize * 2)) + this.circleSize) * a)) + (this.circleSize / 2);
                if (a <= VideoEditorActivity.this.selectedCompression) {
                    this.paint.setColor(-11292945);
                } else {
                    this.paint.setColor(-14540254);
                }
                if (a == VideoEditorActivity.this.compressionsCount - 1) {
                    text = VideoEditorActivity.this.originalHeight + TtmlNode.TAG_P;
                } else if (a == 0) {
                    text = "240p";
                } else if (a == 1) {
                    text = "360p";
                } else if (a == 2) {
                    text = "480p";
                } else {
                    text = "720p";
                }
                float width = this.textPaint.measureText(text);
                canvas.drawCircle((float) cx, (float) cy, a == VideoEditorActivity.this.selectedCompression ? (float) AndroidUtilities.dp(8.0f) : (float) (this.circleSize / 2), this.paint);
                canvas.drawText(text, ((float) cx) - (width / 2.0f), (float) (cy - AndroidUtilities.dp(16.0f)), this.textPaint);
                if (a != 0) {
                    int x = ((cx - (this.circleSize / 2)) - this.gapSize) - this.lineSize;
                    canvas.drawRect((float) x, (float) (cy - AndroidUtilities.dp(1.0f)), (float) (this.lineSize + x), (float) (AndroidUtilities.dp(2.0f) + cy), this.paint);
                }
                a++;
            }
        }
    }

    public interface VideoEditorActivityDelegate {
        void didFinishEditVideo(String str, long j, long j2, int i, int i2, int i3, int i4, int i5, int i6, long j3, long j4, String str2);
    }

    public VideoEditorActivity(Bundle args) {
        super(args);
        this.videoPath = args.getString("videoPath");
    }

    private void destroyPlayer() {
        if (this.videoPlayer != null) {
            try {
                if (this.videoPlayer != null) {
                    this.videoPlayer.stop();
                }
            } catch (Exception e) {
            }
            try {
                if (this.videoPlayer != null) {
                    this.videoPlayer.release();
                }
            } catch (Exception e2) {
            }
            this.videoPlayer = null;
        }
    }

    private boolean reinitPlayer(String path) {
        boolean z = false;
        float volume = 0.0f;
        destroyPlayer();
        if (this.playButton != null) {
            this.playButton.setImageResource(R.drawable.video_edit_play);
        }
        this.lastProgress = 0.0f;
        this.videoPlayer = new MediaPlayer();
        this.videoPlayer.setOnPreparedListener(new OnPreparedListener() {
            public void onPrepared(MediaPlayer mp) {
                VideoEditorActivity.this.playerPrepared = true;
                VideoEditorActivity.this.previewViewEnd = VideoEditorActivity.this.videoPlayer.getDuration();
                if (VideoEditorActivity.this.videoTimelineView != null && VideoEditorActivity.this.videoPlayer != null) {
                    if (VideoEditorActivity.this.inPreview) {
                        VideoEditorActivity.this.videoPlayer.seekTo(0);
                    } else {
                        VideoEditorActivity.this.videoPlayer.seekTo((int) (VideoEditorActivity.this.videoTimelineView.getLeftProgress() * VideoEditorActivity.this.videoDuration));
                    }
                }
            }
        });
        try {
            this.videoPlayer.setDataSource(path);
            this.videoPlayer.prepareAsync();
            if (!this.muteVideo) {
                volume = 1.0f;
            }
            if (this.videoPlayer != null) {
                this.videoPlayer.setVolume(volume, volume);
            }
            if (!path.equals(this.videoPath)) {
                z = true;
            }
            this.inPreview = z;
            if (this.textureView == null) {
                return true;
            }
            try {
                this.videoPlayer.setSurface(new Surface(this.textureView.getSurfaceTexture()));
                return true;
            } catch (Throwable e) {
                FileLog.e(e);
                return true;
            }
        } catch (Throwable e2) {
            FileLog.e(e2);
            return false;
        }
    }

    public boolean onFragmentCreate() {
        if (this.created) {
            return true;
        }
        if (this.videoPath == null || !processOpenVideo()) {
            return false;
        }
        if (!reinitPlayer(this.videoPath)) {
            return false;
        }
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.closeChats);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.FilePreparingFailed);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.FileNewChunkAvailable);
        this.created = true;
        return super.onFragmentCreate();
    }

    public void onFragmentDestroy() {
        try {
            getParentActivity().getWindow().clearFlags(128);
        } catch (Throwable e) {
            FileLog.e(e);
        }
        if (this.videoTimelineView != null) {
            this.videoTimelineView.destroy();
        }
        if (this.videoPlayer != null) {
            try {
                this.videoPlayer.stop();
                this.videoPlayer.release();
                this.videoPlayer = null;
            } catch (Throwable e2) {
                FileLog.e(e2);
            }
        }
        if (this.captionEditText != null) {
            this.captionEditText.onDestroy();
        }
        requestVideoPreview(0);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.closeChats);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.FilePreparingFailed);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.FileNewChunkAvailable);
        super.onFragmentDestroy();
    }

    public View createView(Context context) {
        this.actionBar.setBackgroundColor(-16777216);
        this.actionBar.setTitleColor(-1);
        this.actionBar.setItemsBackgroundColor(Theme.ACTION_BAR_PICKER_SELECTOR_COLOR, false);
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setTitle(LocaleController.getString("AttachVideo", R.string.AttachVideo));
        this.actionBar.setSubtitleColor(-1);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    if (VideoEditorActivity.this.pickerView.getVisibility() != 0) {
                        VideoEditorActivity.this.closeCaptionEnter(false);
                    } else {
                        VideoEditorActivity.this.finishFragment();
                    }
                } else if (id == 1) {
                    VideoEditorActivity.this.closeCaptionEnter(true);
                }
            }
        });
        this.fragmentView = new SizeNotifierFrameLayoutPhoto(context) {
            int lastWidth;

            public boolean dispatchKeyEventPreIme(KeyEvent event) {
                if (event == null || event.getKeyCode() != 4 || event.getAction() != 1 || (!VideoEditorActivity.this.captionEditText.isPopupShowing() && !VideoEditorActivity.this.captionEditText.isKeyboardVisible())) {
                    return super.dispatchKeyEventPreIme(event);
                }
                VideoEditorActivity.this.closeCaptionEnter(false);
                return false;
            }

            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int heightSize;
                int widthSize = MeasureSpec.getSize(widthMeasureSpec);
                setMeasuredDimension(widthSize, MeasureSpec.getSize(heightMeasureSpec));
                if (AndroidUtilities.isTablet()) {
                    heightSize = AndroidUtilities.dp(424.0f);
                } else {
                    heightSize = AndroidUtilities.displaySize.y - ActionBar.getCurrentActionBarHeight();
                }
                measureChildWithMargins(VideoEditorActivity.this.captionEditText, widthMeasureSpec, 0, heightMeasureSpec, 0);
                int inputFieldHeight = VideoEditorActivity.this.captionEditText.getMeasuredHeight();
                int childCount = getChildCount();
                for (int i = 0; i < childCount; i++) {
                    View child = getChildAt(i);
                    if (!(child.getVisibility() == 8 || child == VideoEditorActivity.this.captionEditText)) {
                        if (VideoEditorActivity.this.captionEditText.isPopupView(child)) {
                            if (!AndroidUtilities.isInMultiwindow && !AndroidUtilities.isTablet()) {
                                child.measure(MeasureSpec.makeMeasureSpec(widthSize, NUM), MeasureSpec.makeMeasureSpec(child.getLayoutParams().height, NUM));
                            } else if (AndroidUtilities.isTablet()) {
                                child.measure(MeasureSpec.makeMeasureSpec(widthSize, NUM), MeasureSpec.makeMeasureSpec(Math.min(AndroidUtilities.dp(320.0f), MeasureSpec.getSize(heightMeasureSpec) - inputFieldHeight), NUM));
                            } else {
                                child.measure(MeasureSpec.makeMeasureSpec(widthSize, NUM), MeasureSpec.makeMeasureSpec((heightSize - inputFieldHeight) - AndroidUtilities.statusBarHeight, NUM));
                            }
                        } else if (child == VideoEditorActivity.this.textureView) {
                            int width = widthSize;
                            int height = heightSize - AndroidUtilities.dp(166.0f);
                            int vwidth = (VideoEditorActivity.this.rotationValue == 90 || VideoEditorActivity.this.rotationValue == 270) ? VideoEditorActivity.this.originalHeight : VideoEditorActivity.this.originalWidth;
                            int vheight = (VideoEditorActivity.this.rotationValue == 90 || VideoEditorActivity.this.rotationValue == 270) ? VideoEditorActivity.this.originalWidth : VideoEditorActivity.this.originalHeight;
                            float ar = ((float) vwidth) / ((float) vheight);
                            if (((float) width) / ((float) vwidth) > ((float) height) / ((float) vheight)) {
                                width = (int) (((float) height) * ar);
                            } else {
                                height = (int) (((float) width) / ar);
                            }
                            child.measure(MeasureSpec.makeMeasureSpec(width, NUM), MeasureSpec.makeMeasureSpec(height, NUM));
                        } else {
                            measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);
                        }
                    }
                }
                if (this.lastWidth != widthSize) {
                    VideoEditorActivity.this.videoTimelineView.clearFrames();
                    this.lastWidth = widthSize;
                }
            }

            protected void onLayout(boolean changed, int l, int t, int r, int b) {
                int heightSize;
                int count = getChildCount();
                int paddingBottom = (getKeyboardHeight() > AndroidUtilities.dp(20.0f) || AndroidUtilities.isInMultiwindow || AndroidUtilities.isTablet()) ? 0 : VideoEditorActivity.this.captionEditText.getEmojiPadding();
                if (AndroidUtilities.isTablet()) {
                    heightSize = AndroidUtilities.dp(424.0f);
                } else {
                    heightSize = AndroidUtilities.displaySize.y - ActionBar.getCurrentActionBarHeight();
                }
                for (int i = 0; i < count; i++) {
                    View child = getChildAt(i);
                    if (child.getVisibility() != 8) {
                        int childLeft;
                        int childTop;
                        LayoutParams lp = (LayoutParams) child.getLayoutParams();
                        int width = child.getMeasuredWidth();
                        int height = child.getMeasuredHeight();
                        int gravity = lp.gravity;
                        if (gravity == -1) {
                            gravity = 51;
                        }
                        int verticalGravity = gravity & 112;
                        switch ((gravity & 7) & 7) {
                            case 1:
                                childLeft = ((((r - l) - width) / 2) + lp.leftMargin) - lp.rightMargin;
                                break;
                            case 5:
                                childLeft = (r - width) - lp.rightMargin;
                                break;
                            default:
                                childLeft = lp.leftMargin;
                                break;
                        }
                        switch (verticalGravity) {
                            case 16:
                                childTop = (((heightSize - height) / 2) + lp.topMargin) - lp.bottomMargin;
                                break;
                            case 48:
                                childTop = lp.topMargin;
                                break;
                            case 80:
                                childTop = (heightSize - height) - lp.bottomMargin;
                                break;
                            default:
                                childTop = lp.topMargin;
                                break;
                        }
                        if (child == VideoEditorActivity.this.mentionListView) {
                            childTop = (((b - paddingBottom) - t) - height) - lp.bottomMargin;
                            if (VideoEditorActivity.this.pickerView.getVisibility() == 0 || (VideoEditorActivity.this.firstCaptionLayout && !VideoEditorActivity.this.captionEditText.isPopupShowing() && !VideoEditorActivity.this.captionEditText.isKeyboardVisible() && VideoEditorActivity.this.captionEditText.getEmojiPadding() == 0)) {
                                childTop += AndroidUtilities.dp(400.0f);
                            } else {
                                childTop -= VideoEditorActivity.this.captionEditText.getMeasuredHeight();
                            }
                        } else if (child == VideoEditorActivity.this.captionEditText) {
                            childTop = (((b - paddingBottom) - t) - height) - lp.bottomMargin;
                            if (VideoEditorActivity.this.pickerView.getVisibility() == 0 || (VideoEditorActivity.this.firstCaptionLayout && !VideoEditorActivity.this.captionEditText.isPopupShowing() && !VideoEditorActivity.this.captionEditText.isKeyboardVisible() && VideoEditorActivity.this.captionEditText.getEmojiPadding() == 0)) {
                                childTop += AndroidUtilities.dp(400.0f);
                            } else {
                                VideoEditorActivity.this.firstCaptionLayout = false;
                            }
                        } else if (VideoEditorActivity.this.captionEditText.isPopupView(child)) {
                            if (AndroidUtilities.isInMultiwindow || AndroidUtilities.isTablet()) {
                                childTop = (VideoEditorActivity.this.captionEditText.getTop() - child.getMeasuredHeight()) + AndroidUtilities.dp(1.0f);
                            } else {
                                childTop = VideoEditorActivity.this.captionEditText.getBottom();
                            }
                        } else if (child == VideoEditorActivity.this.textureView) {
                            childLeft = ((r - l) - VideoEditorActivity.this.textureView.getMeasuredWidth()) / 2;
                            if (AndroidUtilities.isTablet()) {
                                childTop = (((heightSize - AndroidUtilities.dp(166.0f)) - VideoEditorActivity.this.textureView.getMeasuredHeight()) / 2) + AndroidUtilities.dp(14.0f);
                            } else {
                                childTop = (((heightSize - AndroidUtilities.dp(166.0f)) - VideoEditorActivity.this.textureView.getMeasuredHeight()) / 2) + AndroidUtilities.dp(14.0f);
                            }
                        }
                        child.layout(childLeft, childTop, childLeft + width, childTop + height);
                    }
                }
                notifyHeightChanged();
            }
        };
        this.fragmentView.setBackgroundColor(-16777216);
        SizeNotifierFrameLayoutPhoto frameLayout = this.fragmentView;
        frameLayout.setWithoutWindow(true);
        this.fragmentView.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        this.pickerView = new PickerBottomLayoutViewer(context);
        this.pickerView.setBackgroundColor(0);
        this.pickerView.updateSelectedCount(0, false);
        frameLayout.addView(this.pickerView, LayoutHelper.createFrame(-1, 48, 83));
        this.pickerView.cancelButton.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                VideoEditorActivity.this.finishFragment();
            }
        });
        this.pickerView.doneButton.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                synchronized (VideoEditorActivity.this.sync) {
                    if (VideoEditorActivity.this.videoPlayer != null) {
                        try {
                            VideoEditorActivity.this.videoPlayer.stop();
                            VideoEditorActivity.this.videoPlayer.release();
                            VideoEditorActivity.this.videoPlayer = null;
                        } catch (Throwable e) {
                            FileLog.e(e);
                        }
                    }
                }
                if (VideoEditorActivity.this.delegate != null) {
                    if (VideoEditorActivity.this.muteVideo) {
                    }
                    if (VideoEditorActivity.this.compressItem.getVisibility() == 8 || (VideoEditorActivity.this.compressItem.getVisibility() == 0 && VideoEditorActivity.this.selectedCompression == VideoEditorActivity.this.compressionsCount - 1)) {
                        VideoEditorActivity.this.delegate.didFinishEditVideo(VideoEditorActivity.this.videoPath, VideoEditorActivity.this.startTime, VideoEditorActivity.this.endTime, VideoEditorActivity.this.originalWidth, VideoEditorActivity.this.originalHeight, VideoEditorActivity.this.rotationValue, VideoEditorActivity.this.originalWidth, VideoEditorActivity.this.originalHeight, VideoEditorActivity.this.muteVideo ? -1 : VideoEditorActivity.this.originalBitrate, (long) VideoEditorActivity.this.estimatedSize, VideoEditorActivity.this.esimatedDuration, VideoEditorActivity.this.currentCaption != null ? VideoEditorActivity.this.currentCaption.toString() : null);
                    } else {
                        if (VideoEditorActivity.this.muteVideo) {
                            VideoEditorActivity.this.selectedCompression = 1;
                            VideoEditorActivity.this.updateWidthHeightBitrateForCompression();
                        }
                        VideoEditorActivity.this.delegate.didFinishEditVideo(VideoEditorActivity.this.videoPath, VideoEditorActivity.this.startTime, VideoEditorActivity.this.endTime, VideoEditorActivity.this.resultWidth, VideoEditorActivity.this.resultHeight, VideoEditorActivity.this.rotationValue, VideoEditorActivity.this.originalWidth, VideoEditorActivity.this.originalHeight, VideoEditorActivity.this.muteVideo ? -1 : VideoEditorActivity.this.bitrate, (long) VideoEditorActivity.this.estimatedSize, VideoEditorActivity.this.esimatedDuration, VideoEditorActivity.this.currentCaption != null ? VideoEditorActivity.this.currentCaption.toString() : null);
                    }
                }
                VideoEditorActivity.this.finishFragment();
            }
        });
        LinearLayout itemsLayout = new LinearLayout(context);
        itemsLayout.setOrientation(0);
        this.pickerView.addView(itemsLayout, LayoutHelper.createFrame(-2, 48, 49));
        this.captionItem = new ImageView(context);
        this.captionItem.setScaleType(ScaleType.CENTER);
        this.captionItem.setImageResource(TextUtils.isEmpty(this.currentCaption) ? R.drawable.photo_text : R.drawable.photo_text2);
        this.captionItem.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.ACTION_BAR_WHITE_SELECTOR_COLOR));
        itemsLayout.addView(this.captionItem, LayoutHelper.createLinear(56, 48));
        this.captionItem.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                VideoEditorActivity.this.captionEditText.setFieldText(VideoEditorActivity.this.currentCaption);
                VideoEditorActivity.this.pickerView.setVisibility(8);
                VideoEditorActivity.this.firstCaptionLayout = true;
                if (!AndroidUtilities.isTablet()) {
                    VideoEditorActivity.this.videoSeekBarView.setVisibility(8);
                    VideoEditorActivity.this.videoTimelineView.setVisibility(8);
                }
                VideoEditorActivity.this.captionEditText.openKeyboard();
                VideoEditorActivity.this.actionBar.setTitle(VideoEditorActivity.this.muteVideo ? LocaleController.getString("GifCaption", R.string.GifCaption) : LocaleController.getString("VideoCaption", R.string.VideoCaption));
                VideoEditorActivity.this.actionBar.setSubtitle(null);
            }
        });
        this.compressItem = new ImageView(context);
        this.compressItem.setScaleType(ScaleType.CENTER);
        this.compressItem.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.ACTION_BAR_WHITE_SELECTOR_COLOR));
        this.compressItem.setVisibility(this.compressionsCount > 1 ? 0 : 8);
        itemsLayout.addView(this.compressItem, LayoutHelper.createLinear(56, 48));
        this.compressItem.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                VideoEditorActivity.this.showQualityView(true);
                VideoEditorActivity.this.requestVideoPreview(1);
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
                FileLog.e(e);
            }
        }
        this.muteItem = new ImageView(context);
        this.muteItem.setScaleType(ScaleType.CENTER);
        this.muteItem.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.ACTION_BAR_WHITE_SELECTOR_COLOR));
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
                            try {
                                VideoEditorActivity.this.getParentActivity().getWindow().clearFlags(128);
                            } catch (Throwable e) {
                                FileLog.e(e);
                            }
                        }
                        VideoEditorActivity.this.videoPlayer.setOnSeekCompleteListener(null);
                        VideoEditorActivity.this.videoPlayer.seekTo((int) (VideoEditorActivity.this.videoDuration * progress));
                    } catch (Throwable e2) {
                        FileLog.e(e2);
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
                            try {
                                VideoEditorActivity.this.getParentActivity().getWindow().clearFlags(128);
                            } catch (Throwable e) {
                                FileLog.e(e);
                            }
                        }
                        VideoEditorActivity.this.videoPlayer.setOnSeekCompleteListener(null);
                        VideoEditorActivity.this.videoPlayer.seekTo((int) (VideoEditorActivity.this.videoDuration * progress));
                    } catch (Throwable e2) {
                        FileLog.e(e2);
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
                    try {
                        VideoEditorActivity.this.videoPlayer.seekTo((int) (VideoEditorActivity.this.videoDuration * progress));
                        VideoEditorActivity.this.lastProgress = progress;
                    } catch (Throwable e) {
                        FileLog.e(e);
                    }
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
                        if (!VideoEditorActivity.this.playerPrepared) {
                            return;
                        }
                        if (VideoEditorActivity.this.inPreview) {
                            VideoEditorActivity.this.videoPlayer.seekTo(0);
                        } else {
                            VideoEditorActivity.this.videoPlayer.seekTo((int) (VideoEditorActivity.this.videoTimelineView.getLeftProgress() * VideoEditorActivity.this.videoDuration));
                        }
                    } catch (Throwable e) {
                        FileLog.e(e);
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
        this.progressView = new RadialProgressView(context);
        this.progressView.setProgressColor(-1);
        this.progressView.setBackgroundResource(R.drawable.circle_big);
        this.progressView.setVisibility(4);
        frameLayout.addView(this.progressView, LayoutHelper.createFrame(54, 54.0f, 17, 0.0f, 0.0f, 0.0f, 70.0f));
        this.playButton = new ImageView(context);
        this.playButton.setScaleType(ScaleType.CENTER);
        this.playButton.setImageResource(R.drawable.video_edit_play);
        this.playButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (VideoEditorActivity.this.videoPlayer != null && VideoEditorActivity.this.playerPrepared && !VideoEditorActivity.this.requestingPreview && !VideoEditorActivity.this.loadInitialVideo) {
                    if (VideoEditorActivity.this.videoPlayer.isPlaying()) {
                        VideoEditorActivity.this.videoPlayer.pause();
                        VideoEditorActivity.this.playButton.setImageResource(R.drawable.video_edit_play);
                        try {
                            VideoEditorActivity.this.getParentActivity().getWindow().clearFlags(128);
                            return;
                        } catch (Throwable e) {
                            FileLog.e(e);
                            return;
                        }
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
                                if (VideoEditorActivity.this.inPreview) {
                                    VideoEditorActivity.this.lastProgress = (((float) VideoEditorActivity.this.videoPlayer.getCurrentPosition()) - 0.0f) / (1.0f - 0.0f);
                                    return;
                                }
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
                        VideoEditorActivity.this.videoPlayer.setOnCompletionListener(new OnCompletionListener() {
                            public void onCompletion(MediaPlayer mp) {
                                try {
                                    VideoEditorActivity.this.getParentActivity().getWindow().clearFlags(128);
                                } catch (Throwable e) {
                                    FileLog.e(e);
                                }
                                VideoEditorActivity.this.onPlayComplete();
                            }
                        });
                        VideoEditorActivity.this.videoPlayer.start();
                        try {
                            VideoEditorActivity.this.getParentActivity().getWindow().addFlags(128);
                        } catch (Throwable e2) {
                            FileLog.e(e2);
                        }
                        synchronized (VideoEditorActivity.this.sync) {
                            if (VideoEditorActivity.this.thread == null) {
                                VideoEditorActivity.this.thread = new Thread(VideoEditorActivity.this.progressRunnable);
                                VideoEditorActivity.this.thread.start();
                            }
                        }
                    } catch (Throwable e22) {
                        FileLog.e(e22);
                    }
                }
            }
        });
        frameLayout.addView(this.playButton, LayoutHelper.createFrame(100, 100.0f, 17, 0.0f, 0.0f, 0.0f, 70.0f));
        if (this.captionEditText != null) {
            this.captionEditText.onDestroy();
        }
        this.captionEditText = new PhotoViewerCaptionEnterView(context, frameLayout, null);
        this.captionEditText.setForceFloatingEmoji(AndroidUtilities.isTablet());
        this.captionEditText.setDelegate(new PhotoViewerCaptionEnterViewDelegate() {
            private int[] location = new int[2];
            private int previousSize;
            private int previousY;

            public void onCaptionEnter() {
                VideoEditorActivity.this.closeCaptionEnter(true);
            }

            public void onTextChanged(CharSequence text) {
                if (VideoEditorActivity.this.mentionsAdapter != null && VideoEditorActivity.this.captionEditText != null && VideoEditorActivity.this.parentChatActivity != null && text != null) {
                    VideoEditorActivity.this.mentionsAdapter.searchUsernameOrHashtag(text.toString(), VideoEditorActivity.this.captionEditText.getCursorPosition(), VideoEditorActivity.this.parentChatActivity.messages);
                }
            }

            public void onWindowSizeChanged(int size) {
                int i;
                int min = Math.min(3, VideoEditorActivity.this.mentionsAdapter.getItemCount()) * 36;
                if (VideoEditorActivity.this.mentionsAdapter.getItemCount() > 3) {
                    i = 18;
                } else {
                    i = 0;
                }
                if (size - (ActionBar.getCurrentActionBarHeight() * 2) < AndroidUtilities.dp((float) (i + min))) {
                    VideoEditorActivity.this.allowMentions = false;
                    if (VideoEditorActivity.this.mentionListView != null && VideoEditorActivity.this.mentionListView.getVisibility() == 0) {
                        VideoEditorActivity.this.mentionListView.setVisibility(4);
                    }
                } else {
                    VideoEditorActivity.this.allowMentions = true;
                    if (VideoEditorActivity.this.mentionListView != null && VideoEditorActivity.this.mentionListView.getVisibility() == 4) {
                        VideoEditorActivity.this.mentionListView.setVisibility(0);
                    }
                }
                VideoEditorActivity.this.fragmentView.getLocationInWindow(this.location);
                if (this.previousSize != size || this.previousY != this.location[1]) {
                    VideoEditorActivity.this.fragmentView.requestLayout();
                    this.previousSize = size;
                    this.previousY = this.location[1];
                }
            }
        });
        frameLayout.addView(this.captionEditText, LayoutHelper.createFrame(-1, -2, 83));
        this.captionEditText.onCreate();
        this.mentionListView = new RecyclerListView(context);
        this.mentionListView.setTag(Integer.valueOf(5));
        this.mentionLayoutManager = new LinearLayoutManager(context) {
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        };
        this.mentionLayoutManager.setOrientation(1);
        this.mentionListView.setLayoutManager(this.mentionLayoutManager);
        this.mentionListView.setBackgroundColor(Theme.ACTION_BAR_PHOTO_VIEWER_COLOR);
        this.mentionListView.setVisibility(8);
        this.mentionListView.setClipToPadding(true);
        this.mentionListView.setOverScrollMode(2);
        frameLayout.addView(this.mentionListView, LayoutHelper.createFrame(-1, 110, 83));
        RecyclerListView recyclerListView = this.mentionListView;
        Adapter mentionsAdapter = new MentionsAdapter(context, true, 0, new MentionsAdapterDelegate() {
            public void needChangePanelVisibility(boolean show) {
                if (show) {
                    int i;
                    LayoutParams layoutParams3 = (LayoutParams) VideoEditorActivity.this.mentionListView.getLayoutParams();
                    int min = Math.min(3, VideoEditorActivity.this.mentionsAdapter.getItemCount()) * 36;
                    if (VideoEditorActivity.this.mentionsAdapter.getItemCount() > 3) {
                        i = 18;
                    } else {
                        i = 0;
                    }
                    int height = min + i;
                    layoutParams3.height = AndroidUtilities.dp((float) height);
                    layoutParams3.topMargin = -AndroidUtilities.dp((float) height);
                    VideoEditorActivity.this.mentionListView.setLayoutParams(layoutParams3);
                    if (VideoEditorActivity.this.mentionListAnimation != null) {
                        VideoEditorActivity.this.mentionListAnimation.cancel();
                        VideoEditorActivity.this.mentionListAnimation = null;
                    }
                    if (VideoEditorActivity.this.mentionListView.getVisibility() == 0) {
                        VideoEditorActivity.this.mentionListView.setAlpha(1.0f);
                        return;
                    }
                    VideoEditorActivity.this.mentionLayoutManager.scrollToPositionWithOffset(0, 10000);
                    if (VideoEditorActivity.this.allowMentions) {
                        VideoEditorActivity.this.mentionListView.setVisibility(0);
                        VideoEditorActivity.this.mentionListAnimation = new AnimatorSet();
                        VideoEditorActivity.this.mentionListAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(VideoEditorActivity.this.mentionListView, "alpha", new float[]{0.0f, 1.0f})});
                        VideoEditorActivity.this.mentionListAnimation.addListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animation) {
                                if (VideoEditorActivity.this.mentionListAnimation != null && VideoEditorActivity.this.mentionListAnimation.equals(animation)) {
                                    VideoEditorActivity.this.mentionListAnimation = null;
                                }
                            }
                        });
                        VideoEditorActivity.this.mentionListAnimation.setDuration(200);
                        VideoEditorActivity.this.mentionListAnimation.start();
                        return;
                    }
                    VideoEditorActivity.this.mentionListView.setAlpha(1.0f);
                    VideoEditorActivity.this.mentionListView.setVisibility(4);
                    return;
                }
                if (VideoEditorActivity.this.mentionListAnimation != null) {
                    VideoEditorActivity.this.mentionListAnimation.cancel();
                    VideoEditorActivity.this.mentionListAnimation = null;
                }
                if (VideoEditorActivity.this.mentionListView.getVisibility() == 8) {
                    return;
                }
                if (VideoEditorActivity.this.allowMentions) {
                    VideoEditorActivity.this.mentionListAnimation = new AnimatorSet();
                    AnimatorSet access$5200 = VideoEditorActivity.this.mentionListAnimation;
                    Animator[] animatorArr = new Animator[1];
                    animatorArr[0] = ObjectAnimator.ofFloat(VideoEditorActivity.this.mentionListView, "alpha", new float[]{0.0f});
                    access$5200.playTogether(animatorArr);
                    VideoEditorActivity.this.mentionListAnimation.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animation) {
                            if (VideoEditorActivity.this.mentionListAnimation != null && VideoEditorActivity.this.mentionListAnimation.equals(animation)) {
                                VideoEditorActivity.this.mentionListView.setVisibility(8);
                                VideoEditorActivity.this.mentionListAnimation = null;
                            }
                        }
                    });
                    VideoEditorActivity.this.mentionListAnimation.setDuration(200);
                    VideoEditorActivity.this.mentionListAnimation.start();
                    return;
                }
                VideoEditorActivity.this.mentionListView.setVisibility(8);
            }

            public void onContextSearch(boolean searching) {
            }

            public void onContextClick(BotInlineResult result) {
            }
        });
        this.mentionsAdapter = mentionsAdapter;
        recyclerListView.setAdapter(mentionsAdapter);
        this.mentionsAdapter.setAllowNewMentions(false);
        this.mentionListView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(View view, int position) {
                User object = VideoEditorActivity.this.mentionsAdapter.getItem(position);
                int start = VideoEditorActivity.this.mentionsAdapter.getResultStartPosition();
                int len = VideoEditorActivity.this.mentionsAdapter.getResultLength();
                if (object instanceof User) {
                    User user = object;
                    if (user != null) {
                        VideoEditorActivity.this.captionEditText.replaceWithText(start, len, "@" + user.username + " ");
                    }
                } else if (object instanceof String) {
                    VideoEditorActivity.this.captionEditText.replaceWithText(start, len, object + " ");
                }
            }
        });
        this.mentionListView.setOnItemLongClickListener(new OnItemLongClickListener() {
            public boolean onItemClick(View view, int position) {
                if (VideoEditorActivity.this.getParentActivity() == null || !(VideoEditorActivity.this.mentionsAdapter.getItem(position) instanceof String)) {
                    return false;
                }
                Builder builder = new Builder(VideoEditorActivity.this.getParentActivity());
                builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                builder.setMessage(LocaleController.getString("ClearSearch", R.string.ClearSearch));
                builder.setPositiveButton(LocaleController.getString("ClearButton", R.string.ClearButton).toUpperCase(), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        VideoEditorActivity.this.mentionsAdapter.clearRecentHashtags();
                    }
                });
                builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                VideoEditorActivity.this.showDialog(builder.create());
                return true;
            }
        });
        if (this.compressionsCount > 1) {
            this.qualityPicker = new PickerBottomLayoutViewer(context);
            this.qualityPicker.setBackgroundColor(0);
            this.qualityPicker.updateSelectedCount(0, false);
            this.qualityPicker.setTranslationY((float) AndroidUtilities.dp(BitmapDescriptorFactory.HUE_GREEN));
            this.qualityPicker.doneButton.setText(LocaleController.getString("Done", R.string.Done).toUpperCase());
            frameLayout.addView(this.qualityPicker, LayoutHelper.createFrame(-1, 48, 83));
            this.qualityPicker.cancelButton.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    VideoEditorActivity.this.selectedCompression = VideoEditorActivity.this.previousCompression;
                    VideoEditorActivity.this.didChangedCompressionLevel(false);
                    VideoEditorActivity.this.showQualityView(false);
                }
            });
            this.qualityPicker.doneButton.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    VideoEditorActivity.this.showQualityView(false);
                    VideoEditorActivity.this.requestVideoPreview(2);
                }
            });
            this.qualityChooseView = new QualityChooseView(context);
            this.qualityChooseView.setTranslationY((float) AndroidUtilities.dp(BitmapDescriptorFactory.HUE_GREEN));
            this.qualityChooseView.setVisibility(4);
            frameLayout.addView(this.qualityChooseView, LayoutHelper.createFrame(-1, 90.0f, 83, 0.0f, 0.0f, 0.0f, 44.0f));
        }
        updateVideoInfo();
        updateMuteButton();
        return this.fragmentView;
    }

    private void didChangedCompressionLevel(boolean request) {
        Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit();
        editor.putInt("compress_video2", this.selectedCompression);
        editor.commit();
        updateWidthHeightBitrateForCompression();
        updateVideoInfo();
        if (request) {
            requestVideoPreview(1);
        }
    }

    public void onPause() {
        super.onPause();
        if (this.pickerView.getVisibility() == 8) {
            closeCaptionEnter(true);
        }
    }

    public void onResume() {
        super.onResume();
        if (this.textureView != null) {
            try {
                if (this.playerPrepared && !this.videoPlayer.isPlaying()) {
                    this.videoPlayer.seekTo((int) (this.videoSeekBarView.getProgress() * this.videoDuration));
                }
            } catch (Throwable e) {
                FileLog.e(e);
            }
        }
    }

    private void showQualityView(final boolean show) {
        if (show) {
            this.previousCompression = this.selectedCompression;
        }
        AnimatorSet animatorSet = new AnimatorSet();
        Animator[] animatorArr;
        if (show) {
            animatorArr = new Animator[1];
            animatorArr[0] = ObjectAnimator.ofFloat(this.pickerView, "translationY", new float[]{0.0f, (float) AndroidUtilities.dp(152.0f)});
            animatorSet.playTogether(animatorArr);
            animatorArr = new Animator[1];
            animatorArr[0] = ObjectAnimator.ofFloat(this.videoTimelineView, "translationY", new float[]{0.0f, (float) AndroidUtilities.dp(152.0f)});
            animatorSet.playTogether(animatorArr);
            animatorArr = new Animator[1];
            animatorArr[0] = ObjectAnimator.ofFloat(this.videoSeekBarView, "translationY", new float[]{0.0f, (float) AndroidUtilities.dp(152.0f)});
            animatorSet.playTogether(animatorArr);
        } else {
            animatorArr = new Animator[1];
            animatorArr[0] = ObjectAnimator.ofFloat(this.qualityChooseView, "translationY", new float[]{0.0f, (float) AndroidUtilities.dp(BitmapDescriptorFactory.HUE_GREEN)});
            animatorSet.playTogether(animatorArr);
            animatorArr = new Animator[1];
            animatorArr[0] = ObjectAnimator.ofFloat(this.qualityPicker, "translationY", new float[]{0.0f, (float) AndroidUtilities.dp(BitmapDescriptorFactory.HUE_GREEN)});
            animatorSet.playTogether(animatorArr);
        }
        animatorSet.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                AnimatorSet animatorSet = new AnimatorSet();
                Animator[] animatorArr;
                if (show) {
                    VideoEditorActivity.this.qualityChooseView.setVisibility(0);
                    VideoEditorActivity.this.qualityPicker.setVisibility(0);
                    animatorArr = new Animator[1];
                    animatorArr[0] = ObjectAnimator.ofFloat(VideoEditorActivity.this.qualityChooseView, "translationY", new float[]{0.0f});
                    animatorSet.playTogether(animatorArr);
                    animatorArr = new Animator[1];
                    animatorArr[0] = ObjectAnimator.ofFloat(VideoEditorActivity.this.qualityPicker, "translationY", new float[]{0.0f});
                    animatorSet.playTogether(animatorArr);
                } else {
                    VideoEditorActivity.this.qualityChooseView.setVisibility(4);
                    VideoEditorActivity.this.qualityPicker.setVisibility(4);
                    animatorArr = new Animator[1];
                    animatorArr[0] = ObjectAnimator.ofFloat(VideoEditorActivity.this.pickerView, "translationY", new float[]{0.0f});
                    animatorSet.playTogether(animatorArr);
                    animatorArr = new Animator[1];
                    animatorArr[0] = ObjectAnimator.ofFloat(VideoEditorActivity.this.videoTimelineView, "translationY", new float[]{0.0f});
                    animatorSet.playTogether(animatorArr);
                    animatorArr = new Animator[1];
                    animatorArr[0] = ObjectAnimator.ofFloat(VideoEditorActivity.this.videoSeekBarView, "translationY", new float[]{0.0f});
                    animatorSet.playTogether(animatorArr);
                }
                animatorSet.setDuration(200);
                animatorSet.setInterpolator(new AccelerateInterpolator());
                animatorSet.start();
            }
        });
        animatorSet.setDuration(200);
        animatorSet.setInterpolator(new DecelerateInterpolator());
        animatorSet.start();
    }

    public void setParentChatActivity(ChatActivity chatActivity) {
        this.parentChatActivity = chatActivity;
    }

    private void closeCaptionEnter(boolean apply) {
        if (apply) {
            this.currentCaption = this.captionEditText.getFieldCharSequence();
        }
        this.actionBar.setSubtitle(this.currentSubtitle);
        this.pickerView.setVisibility(0);
        if (!AndroidUtilities.isTablet()) {
            this.videoSeekBarView.setVisibility(0);
            this.videoTimelineView.setVisibility(0);
        }
        this.actionBar.setTitle(this.muteVideo ? LocaleController.getString("AttachGif", R.string.AttachGif) : LocaleController.getString("AttachVideo", R.string.AttachVideo));
        this.actionBar.setSubtitle(this.muteVideo ? null : this.currentSubtitle);
        this.captionItem.setImageResource(TextUtils.isEmpty(this.currentCaption) ? R.drawable.photo_text : R.drawable.photo_text2);
        if (this.captionEditText.isPopupShowing()) {
            this.captionEditText.hidePopup();
        }
        this.captionEditText.closeKeyboard();
    }

    private void requestVideoPreview(int request) {
        if (this.videoPreviewMessageObject != null) {
            MediaController.getInstance().cancelVideoConvert(this.videoPreviewMessageObject);
        }
        boolean wasRequestingPreview = this.requestingPreview && !this.tryStartRequestPreviewOnFinish;
        this.requestingPreview = false;
        this.loadInitialVideo = false;
        this.progressView.setVisibility(4);
        if (request != 1) {
            this.tryStartRequestPreviewOnFinish = false;
            if (request == 2) {
                reinitPlayer(this.videoPath);
            }
        } else if (this.selectedCompression == this.compressionsCount - 1) {
            this.tryStartRequestPreviewOnFinish = false;
            if (wasRequestingPreview) {
                this.playButton.setImageDrawable(null);
                this.progressView.setVisibility(0);
                this.loadInitialVideo = true;
                return;
            }
            reinitPlayer(this.videoPath);
        } else {
            destroyPlayer();
            if (this.videoPreviewMessageObject == null) {
                TL_message message = new TL_message();
                message.id = 0;
                message.message = "";
                message.media = new TL_messageMediaEmpty();
                message.action = new TL_messageActionEmpty();
                this.videoPreviewMessageObject = new MessageObject(message, null, false);
                this.videoPreviewMessageObject.messageOwner.attachPath = new File(FileLoader.getInstance().getDirectory(4), "video_preview.mp4").getAbsolutePath();
                this.videoPreviewMessageObject.videoEditedInfo = new VideoEditedInfo();
                this.videoPreviewMessageObject.videoEditedInfo.rotationValue = this.rotationValue;
                this.videoPreviewMessageObject.videoEditedInfo.originalWidth = this.originalWidth;
                this.videoPreviewMessageObject.videoEditedInfo.originalHeight = this.originalHeight;
                this.videoPreviewMessageObject.videoEditedInfo.originalPath = this.videoPath;
            }
            VideoEditedInfo videoEditedInfo = this.videoPreviewMessageObject.videoEditedInfo;
            long start = this.startTime;
            videoEditedInfo.startTime = start;
            videoEditedInfo = this.videoPreviewMessageObject.videoEditedInfo;
            long end = this.endTime;
            videoEditedInfo.endTime = end;
            if (start == -1) {
                start = 0;
            }
            if (end == -1) {
                end = (long) (this.videoDuration * 1000.0f);
            }
            if (end - start > 5000000) {
                this.videoPreviewMessageObject.videoEditedInfo.endTime = 5000000 + start;
            }
            this.videoPreviewMessageObject.videoEditedInfo.bitrate = this.bitrate;
            this.videoPreviewMessageObject.videoEditedInfo.resultWidth = this.resultWidth;
            this.videoPreviewMessageObject.videoEditedInfo.resultHeight = this.resultHeight;
            if (!MediaController.getInstance().scheduleVideoConvert(this.videoPreviewMessageObject, true)) {
                this.tryStartRequestPreviewOnFinish = true;
            }
            if (this.videoPlayer == null) {
                this.requestingPreview = true;
                this.playButton.setImageDrawable(null);
                this.progressView.setVisibility(0);
            }
        }
    }

    public void didReceivedNotification(int id, Object... args) {
        boolean z = true;
        if (id == NotificationCenter.closeChats) {
            removeSelfFromStack();
        } else if (id == NotificationCenter.FilePreparingFailed) {
            MessageObject messageObject = args[0];
            if (this.loadInitialVideo) {
                this.loadInitialVideo = false;
                this.progressView.setVisibility(4);
                reinitPlayer(this.videoPath);
            } else if (this.tryStartRequestPreviewOnFinish) {
                destroyPlayer();
                if (MediaController.getInstance().scheduleVideoConvert(this.videoPreviewMessageObject, true)) {
                    z = false;
                }
                this.tryStartRequestPreviewOnFinish = z;
            } else if (messageObject == this.videoPreviewMessageObject) {
                this.requestingPreview = false;
                this.progressView.setVisibility(4);
                this.playButton.setImageResource(R.drawable.video_edit_play);
            }
        } else if (id == NotificationCenter.FileNewChunkAvailable && ((MessageObject) args[0]) == this.videoPreviewMessageObject) {
            String finalPath = args[1];
            if (((Long) args[2]).longValue() != 0) {
                this.requestingPreview = false;
                this.progressView.setVisibility(4);
                reinitPlayer(finalPath);
            }
        }
    }

    public void updateMuteButton() {
        if (this.videoPlayer != null) {
            float volume = this.muteVideo ? 0.0f : 1.0f;
            if (this.videoPlayer != null) {
                this.videoPlayer.setVolume(volume, volume);
            }
        }
        if (this.muteVideo) {
            this.actionBar.setTitle(LocaleController.getString("AttachGif", R.string.AttachGif));
            this.actionBar.setSubtitle(null);
            this.muteItem.setImageResource(R.drawable.volume_off);
            if (this.compressItem.getVisibility() == 0) {
                this.compressItem.setClickable(false);
                this.compressItem.setAlpha(0.5f);
                this.compressItem.setEnabled(false);
            }
            this.videoTimelineView.setMaxProgressDiff(30000.0f / this.videoDuration);
            return;
        }
        this.actionBar.setTitle(LocaleController.getString("AttachVideo", R.string.AttachVideo));
        this.actionBar.setSubtitle(this.currentSubtitle);
        this.muteItem.setImageResource(R.drawable.volume_on);
        if (this.compressItem.getVisibility() == 0) {
            this.compressItem.setClickable(true);
            this.compressItem.setAlpha(1.0f);
            this.compressItem.setEnabled(true);
        }
        this.videoTimelineView.setMaxProgressDiff(1.0f);
    }

    private void onPlayComplete() {
        if (this.playButton != null) {
            this.playButton.setImageResource(R.drawable.video_edit_play);
        }
        if (!(this.videoSeekBarView == null || this.videoTimelineView == null)) {
            if (this.inPreview) {
                this.videoSeekBarView.setProgress(0.0f);
            } else {
                this.videoSeekBarView.setProgress(this.videoTimelineView.getLeftProgress());
            }
        }
        try {
            if (this.videoPlayer != null && this.videoTimelineView != null) {
                if (this.inPreview) {
                    this.videoPlayer.seekTo(0);
                } else {
                    this.videoPlayer.seekTo((int) (this.videoTimelineView.getLeftProgress() * this.videoDuration));
                }
            }
        } catch (Throwable e) {
            FileLog.e(e);
        }
    }

    private void updateVideoInfo() {
        if (this.actionBar != null) {
            int width;
            int height;
            CharSequence charSequence;
            if (this.selectedCompression == 0) {
                this.compressItem.setImageResource(R.drawable.video_240);
            } else if (this.selectedCompression == 1) {
                this.compressItem.setImageResource(R.drawable.video_360);
            } else if (this.selectedCompression == 2) {
                this.compressItem.setImageResource(R.drawable.video_480);
            } else if (this.selectedCompression == 3) {
                this.compressItem.setImageResource(R.drawable.video_720);
            } else if (this.selectedCompression == 4) {
                this.compressItem.setImageResource(R.drawable.video_1080);
            }
            this.esimatedDuration = (long) Math.ceil((double) ((this.videoTimelineView.getRightProgress() - this.videoTimelineView.getLeftProgress()) * this.videoDuration));
            if (this.compressItem.getVisibility() == 8 || (this.compressItem.getVisibility() == 0 && this.selectedCompression == this.compressionsCount - 1)) {
                width = (this.rotationValue == 90 || this.rotationValue == 270) ? this.originalHeight : this.originalWidth;
                height = (this.rotationValue == 90 || this.rotationValue == 270) ? this.originalWidth : this.originalHeight;
                this.estimatedSize = (int) (((float) this.originalSize) * (((float) this.esimatedDuration) / this.videoDuration));
            } else {
                width = (this.rotationValue == 90 || this.rotationValue == 270) ? this.resultHeight : this.resultWidth;
                height = (this.rotationValue == 90 || this.rotationValue == 270) ? this.resultWidth : this.resultHeight;
                this.estimatedSize = (int) (((float) (this.audioFramesSize + this.videoFramesSize)) * (((float) this.esimatedDuration) / this.videoDuration));
                this.estimatedSize += (this.estimatedSize / 32768) * 16;
            }
            if (this.videoTimelineView.getLeftProgress() == 0.0f) {
                this.startTime = -1;
            } else {
                this.startTime = ((long) (this.videoTimelineView.getLeftProgress() * this.videoDuration)) * 1000;
            }
            if (this.videoTimelineView.getRightProgress() == 1.0f) {
                this.endTime = -1;
            } else {
                this.endTime = ((long) (this.videoTimelineView.getRightProgress() * this.videoDuration)) * 1000;
            }
            String videoDimension = String.format("%dx%d", new Object[]{Integer.valueOf(width), Integer.valueOf(height)});
            int seconds = ((int) Math.ceil((double) (this.esimatedDuration / 1000))) - (((int) ((this.esimatedDuration / 1000) / 60)) * 60);
            String videoTimeSize = String.format("%d:%02d, ~%s", new Object[]{Integer.valueOf((int) ((this.esimatedDuration / 1000) / 60)), Integer.valueOf(seconds), AndroidUtilities.formatFileSize((long) this.estimatedSize)});
            this.currentSubtitle = String.format("%s, %s", new Object[]{videoDimension, videoTimeSize});
            ActionBar actionBar = this.actionBar;
            if (this.muteVideo) {
                charSequence = null;
            } else {
                charSequence = this.currentSubtitle;
            }
            actionBar.setSubtitle(charSequence);
        }
    }

    public void setDelegate(VideoEditorActivityDelegate videoEditorActivityDelegate) {
        this.delegate = videoEditorActivityDelegate;
    }

    private void updateWidthHeightBitrateForCompression() {
        if (this.compressionsCount == -1) {
            if (this.originalWidth > 1280 || this.originalHeight > 1280) {
                this.compressionsCount = 5;
            } else if (this.originalWidth > 848 || this.originalHeight > 848) {
                this.compressionsCount = 4;
            } else if (this.originalWidth > 640 || this.originalHeight > 640) {
                this.compressionsCount = 3;
            } else if (this.originalWidth > 480 || this.originalHeight > 480) {
                this.compressionsCount = 2;
            } else {
                this.compressionsCount = 1;
            }
        }
        if (this.selectedCompression >= this.compressionsCount) {
            this.selectedCompression = this.compressionsCount - 1;
        }
        if (this.selectedCompression != this.compressionsCount - 1) {
            float maxSize;
            int targetBitrate;
            float scale;
            switch (this.selectedCompression) {
                case 0:
                    maxSize = 432.0f;
                    targetBitrate = 400000;
                    break;
                case 1:
                    maxSize = 640.0f;
                    targetBitrate = 900000;
                    break;
                case 2:
                    maxSize = 848.0f;
                    targetBitrate = 1100000;
                    break;
                default:
                    targetBitrate = 1600000;
                    maxSize = 1280.0f;
                    break;
            }
            if (this.originalWidth > this.originalHeight) {
                scale = maxSize / ((float) this.originalWidth);
            } else {
                scale = maxSize / ((float) this.originalHeight);
            }
            this.resultWidth = Math.round((((float) this.originalWidth) * scale) / 2.0f) * 2;
            this.resultHeight = Math.round((((float) this.originalHeight) * scale) / 2.0f) * 2;
            if (this.bitrate != 0) {
                this.bitrate = Math.min(targetBitrate, (int) (((float) this.originalBitrate) / scale));
                this.videoFramesSize = (long) ((((float) (this.bitrate / 8)) * this.videoDuration) / 1000.0f);
            }
        }
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
            for (int b = 0; b < boxes.size(); b++) {
                TrackBox trackBox = (TrackBox) ((Box) boxes.get(b));
                long sampleSizes = 0;
                long trackBitrate = 0;
                try {
                    MediaBox mediaBox = trackBox.getMediaBox();
                    MediaHeaderBox mediaHeaderBox = mediaBox.getMediaHeaderBox();
                    for (long j : mediaBox.getMediaInformationBox().getSampleTableBox().getSampleSizeBox().getSampleSizes()) {
                        sampleSizes += j;
                    }
                    this.videoDuration = ((float) mediaHeaderBox.getDuration()) / ((float) mediaHeaderBox.getTimescale());
                    trackBitrate = (long) ((int) (((float) (8 * sampleSizes)) / this.videoDuration));
                } catch (Throwable e) {
                    FileLog.e(e);
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
            this.videoDuration *= 1000.0f;
            this.selectedCompression = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).getInt("compress_video2", 1);
            updateWidthHeightBitrateForCompression();
            if (!isAvc && (this.resultWidth == this.originalWidth || this.resultHeight == this.originalHeight)) {
                return false;
            }
            updateVideoInfo();
            return true;
        } catch (Throwable e2) {
            FileLog.e(e2);
            return false;
        }
    }
}
