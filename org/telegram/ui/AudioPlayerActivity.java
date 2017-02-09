package org.telegram.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import java.io.File;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MediaController.FileDownloadProgressListener;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.audioinfo.AudioInfo;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.DocumentAttribute;
import org.telegram.tgnet.TLRPC.TL_documentAttributeAudio;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.LineProgressView;

public class AudioPlayerActivity extends BaseFragment implements NotificationCenterDelegate, FileDownloadProgressListener {
    private int TAG;
    private ImageView[] buttons = new ImageView[5];
    private TextView durationTextView;
    private MessageObject lastMessageObject;
    private String lastTimeString;
    private ImageView placeholder;
    private ImageView playButton;
    private LineProgressView progressView;
    private ImageView repeatButton;
    private SeekBarView seekBarView;
    private ImageView shuffleButton;
    private TextView timeTextView;

    private class SeekBarView extends FrameLayout {
        private Paint innerPaint1;
        private Paint outerPaint1;
        private boolean pressed = false;
        public int thumbDX = 0;
        private int thumbHeight;
        private int thumbWidth;
        public int thumbX = 0;

        public SeekBarView(Context context) {
            super(context);
            setWillNotDraw(false);
            this.innerPaint1 = new Paint(1);
            this.innerPaint1.setColor(419430400);
            this.outerPaint1 = new Paint(1);
            this.outerPaint1.setColor(-14438417);
            this.thumbWidth = AndroidUtilities.dp(24.0f);
            this.thumbHeight = AndroidUtilities.dp(24.0f);
        }

        public boolean onInterceptTouchEvent(MotionEvent ev) {
            return onTouch(ev);
        }

        public boolean onTouchEvent(MotionEvent event) {
            return onTouch(event);
        }

        boolean onTouch(MotionEvent ev) {
            if (ev.getAction() == 0) {
                getParent().requestDisallowInterceptTouchEvent(true);
                int additionWidth = (getMeasuredHeight() - this.thumbWidth) / 2;
                if (((float) (this.thumbX - additionWidth)) <= ev.getX() && ev.getX() <= ((float) ((this.thumbX + this.thumbWidth) + additionWidth)) && ev.getY() >= 0.0f && ev.getY() <= ((float) getMeasuredHeight())) {
                    this.pressed = true;
                    this.thumbDX = (int) (ev.getX() - ((float) this.thumbX));
                    invalidate();
                    return true;
                }
            } else if (ev.getAction() == 1 || ev.getAction() == 3) {
                if (this.pressed) {
                    if (ev.getAction() == 1) {
                        AudioPlayerActivity.this.onSeekBarDrag(((float) this.thumbX) / ((float) (getMeasuredWidth() - this.thumbWidth)));
                    }
                    this.pressed = false;
                    invalidate();
                    return true;
                }
            } else if (ev.getAction() == 2 && this.pressed) {
                this.thumbX = (int) (ev.getX() - ((float) this.thumbDX));
                if (this.thumbX < 0) {
                    this.thumbX = 0;
                } else if (this.thumbX > getMeasuredWidth() - this.thumbWidth) {
                    this.thumbX = getMeasuredWidth() - this.thumbWidth;
                }
                invalidate();
                return true;
            }
            return false;
        }

        public void setProgress(float progress) {
            int newThumbX = (int) Math.ceil((double) (((float) (getMeasuredWidth() - this.thumbWidth)) * progress));
            if (this.thumbX != newThumbX) {
                this.thumbX = newThumbX;
                if (this.thumbX < 0) {
                    this.thumbX = 0;
                } else if (this.thumbX > getMeasuredWidth() - this.thumbWidth) {
                    this.thumbX = getMeasuredWidth() - this.thumbWidth;
                }
                invalidate();
            }
        }

        public boolean isDragging() {
            return this.pressed;
        }

        protected void onDraw(Canvas canvas) {
            int y = (getMeasuredHeight() - this.thumbHeight) / 2;
            canvas.drawRect((float) (this.thumbWidth / 2), (float) ((getMeasuredHeight() / 2) - AndroidUtilities.dp(1.0f)), (float) (getMeasuredWidth() - (this.thumbWidth / 2)), (float) ((getMeasuredHeight() / 2) + AndroidUtilities.dp(1.0f)), this.innerPaint1);
            canvas.drawRect((float) (this.thumbWidth / 2), (float) ((getMeasuredHeight() / 2) - AndroidUtilities.dp(1.0f)), (float) ((this.thumbWidth / 2) + this.thumbX), (float) ((getMeasuredHeight() / 2) + AndroidUtilities.dp(1.0f)), this.outerPaint1);
            canvas.drawCircle((float) (this.thumbX + (this.thumbWidth / 2)), (float) ((this.thumbHeight / 2) + y), (float) AndroidUtilities.dp(this.pressed ? 8.0f : 6.0f), this.outerPaint1);
        }
    }

    public boolean onFragmentCreate() {
        this.TAG = MediaController.getInstance().generateObserverTag();
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.audioDidReset);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.audioPlayStateChanged);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.audioDidStarted);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.audioProgressDidChanged);
        return super.onFragmentCreate();
    }

    public void onFragmentDestroy() {
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.audioDidReset);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.audioPlayStateChanged);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.audioDidStarted);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.audioProgressDidChanged);
        MediaController.getInstance().removeLoadingFileObserver(this);
        super.onFragmentDestroy();
    }

    public View createView(Context context) {
        FrameLayout frameLayout = new FrameLayout(context);
        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        frameLayout.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        this.fragmentView = frameLayout;
        this.actionBar.setBackgroundColor(-1);
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setItemsColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2), false);
        this.actionBar.setItemsBackgroundColor(Theme.ACTION_BAR_AUDIO_SELECTOR_COLOR, false);
        if (!AndroidUtilities.isTablet()) {
            this.actionBar.showActionModeTop();
        }
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    AudioPlayerActivity.this.finishFragment();
                }
            }
        });
        this.placeholder = new ImageView(context);
        frameLayout.addView(this.placeholder, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 0.0f, 66.0f));
        View shadow = new View(context);
        shadow.setBackgroundResource(R.drawable.header_shadow_reverse);
        frameLayout.addView(shadow, LayoutHelper.createFrame(-1, 3.0f, 83, 0.0f, 0.0f, 0.0f, 96.0f));
        FrameLayout seekBarContainer = new FrameLayout(context);
        seekBarContainer.setBackgroundColor(-436207617);
        frameLayout.addView(seekBarContainer, LayoutHelper.createFrame(-1, BitmapDescriptorFactory.HUE_ORANGE, 83, 0.0f, 0.0f, 0.0f, 66.0f));
        this.timeTextView = new TextView(context);
        this.timeTextView.setTextSize(1, 12.0f);
        this.timeTextView.setTextColor(-15095832);
        this.timeTextView.setGravity(17);
        this.timeTextView.setText("0:00");
        seekBarContainer.addView(this.timeTextView, LayoutHelper.createFrame(44, -1, 51));
        this.durationTextView = new TextView(context);
        this.durationTextView.setTextSize(1, 12.0f);
        this.durationTextView.setTextColor(-7697782);
        this.durationTextView.setGravity(17);
        this.durationTextView.setText("3:00");
        seekBarContainer.addView(this.durationTextView, LayoutHelper.createFrame(44, -1, 53));
        this.seekBarView = new SeekBarView(context);
        seekBarContainer.addView(this.seekBarView, LayoutHelper.createFrame(-1, -1.0f, 51, 32.0f, 0.0f, 32.0f, 0.0f));
        this.progressView = new LineProgressView(context);
        this.progressView.setVisibility(4);
        this.progressView.setBackgroundColor(419430400);
        this.progressView.setProgressColor(-14438417);
        seekBarContainer.addView(this.progressView, LayoutHelper.createFrame(-1, 2.0f, 19, 44.0f, 0.0f, 44.0f, 0.0f));
        FrameLayout bottomView = new FrameLayout(context) {
            protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
                int dist = ((right - left) - AndroidUtilities.dp(BitmapDescriptorFactory.HUE_VIOLET)) / 4;
                for (int a = 0; a < 5; a++) {
                    int l = AndroidUtilities.dp((float) ((a * 48) + 15)) + (dist * a);
                    int t = AndroidUtilities.dp(9.0f);
                    AudioPlayerActivity.this.buttons[a].layout(l, t, AudioPlayerActivity.this.buttons[a].getMeasuredWidth() + l, AudioPlayerActivity.this.buttons[a].getMeasuredHeight() + t);
                }
            }
        };
        bottomView.setBackgroundColor(-1);
        frameLayout.addView(bottomView, LayoutHelper.createFrame(-1, 66, 83));
        ImageView[] imageViewArr = this.buttons;
        ImageView imageView = new ImageView(context);
        this.repeatButton = imageView;
        imageViewArr[0] = imageView;
        this.repeatButton.setScaleType(ScaleType.CENTER);
        bottomView.addView(this.repeatButton, LayoutHelper.createFrame(48, 48, 51));
        this.repeatButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                MediaController.getInstance().toggleRepeatMode();
                AudioPlayerActivity.this.updateRepeatButton();
            }
        });
        imageViewArr = this.buttons;
        ImageView prevButton = new ImageView(context);
        imageViewArr[1] = prevButton;
        prevButton.setScaleType(ScaleType.CENTER);
        prevButton.setImageDrawable(Theme.createSimpleSelectorDrawable(context, R.drawable.pl_previous, Theme.getColor(Theme.key_player_button), Theme.getColor(Theme.key_player_buttonActive)));
        bottomView.addView(prevButton, LayoutHelper.createFrame(48, 48, 51));
        prevButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                MediaController.getInstance().playPreviousMessage();
            }
        });
        imageViewArr = this.buttons;
        imageView = new ImageView(context);
        this.playButton = imageView;
        imageViewArr[2] = imageView;
        this.playButton.setScaleType(ScaleType.CENTER);
        this.playButton.setImageDrawable(Theme.createSimpleSelectorDrawable(context, R.drawable.pl_play, Theme.getColor(Theme.key_player_button), Theme.getColor(Theme.key_player_buttonActive)));
        bottomView.addView(this.playButton, LayoutHelper.createFrame(48, 48, 51));
        this.playButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (!MediaController.getInstance().isDownloadingCurrentMessage()) {
                    if (MediaController.getInstance().isAudioPaused()) {
                        MediaController.getInstance().playAudio(MediaController.getInstance().getPlayingMessageObject());
                    } else {
                        MediaController.getInstance().pauseAudio(MediaController.getInstance().getPlayingMessageObject());
                    }
                }
            }
        });
        imageViewArr = this.buttons;
        ImageView nextButton = new ImageView(context);
        imageViewArr[3] = nextButton;
        nextButton.setScaleType(ScaleType.CENTER);
        nextButton.setImageDrawable(Theme.createSimpleSelectorDrawable(context, R.drawable.pl_next, Theme.getColor(Theme.key_player_button), Theme.getColor(Theme.key_player_buttonActive)));
        bottomView.addView(nextButton, LayoutHelper.createFrame(48, 48, 51));
        nextButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                MediaController.getInstance().playNextMessage();
            }
        });
        imageViewArr = this.buttons;
        imageView = new ImageView(context);
        this.shuffleButton = imageView;
        imageViewArr[4] = imageView;
        this.shuffleButton.setImageResource(R.drawable.pl_shuffle);
        this.shuffleButton.setScaleType(ScaleType.CENTER);
        bottomView.addView(this.shuffleButton, LayoutHelper.createFrame(48, 48, 51));
        this.shuffleButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                MediaController.getInstance().toggleShuffleMusic();
                AudioPlayerActivity.this.updateShuffleButton();
            }
        });
        updateTitle(false);
        updateRepeatButton();
        updateShuffleButton();
        return frameLayout;
    }

    public void didReceivedNotification(int id, Object... args) {
        if (id == NotificationCenter.audioDidStarted || id == NotificationCenter.audioPlayStateChanged || id == NotificationCenter.audioDidReset) {
            boolean z = id == NotificationCenter.audioDidReset && ((Boolean) args[1]).booleanValue();
            updateTitle(z);
        } else if (id == NotificationCenter.audioProgressDidChanged) {
            MessageObject messageObject = MediaController.getInstance().getPlayingMessageObject();
            if (messageObject != null && messageObject.isMusic()) {
                updateProgress(messageObject);
            }
        }
    }

    public void onFailedDownload(String fileName) {
    }

    public void onSuccessDownload(String fileName) {
    }

    public void onProgressDownload(String fileName, float progress) {
        this.progressView.setProgress(progress, true);
    }

    public void onProgressUpload(String fileName, float progress, boolean isEncrypted) {
    }

    public int getObserverTag() {
        return this.TAG;
    }

    private void onSeekBarDrag(float progress) {
        MediaController.getInstance().seekToProgress(MediaController.getInstance().getPlayingMessageObject(), progress);
    }

    private void updateShuffleButton() {
        if (MediaController.getInstance().isShuffleMusic()) {
            this.shuffleButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_player_buttonActive), Mode.MULTIPLY));
        } else {
            this.shuffleButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_player_button), Mode.MULTIPLY));
        }
    }

    private void updateRepeatButton() {
        int mode = MediaController.getInstance().getRepeatMode();
        if (mode == 0) {
            this.repeatButton.setImageResource(R.drawable.pl_repeat);
            this.repeatButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_player_button), Mode.MULTIPLY));
        } else if (mode == 1) {
            this.repeatButton.setImageResource(R.drawable.pl_repeat);
            this.repeatButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_player_buttonActive), Mode.MULTIPLY));
        } else if (mode == 2) {
            this.repeatButton.setImageResource(R.drawable.pl_repeat1);
            this.repeatButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_player_buttonActive), Mode.MULTIPLY));
        }
    }

    private void updateProgress(MessageObject messageObject) {
        if (this.seekBarView != null) {
            if (!this.seekBarView.isDragging()) {
                this.seekBarView.setProgress(messageObject.audioProgress);
            }
            String timeString = String.format("%d:%02d", new Object[]{Integer.valueOf(messageObject.audioProgressSec / 60), Integer.valueOf(messageObject.audioProgressSec % 60)});
            if (this.lastTimeString == null || !(this.lastTimeString == null || this.lastTimeString.equals(timeString))) {
                this.lastTimeString = timeString;
                this.timeTextView.setText(timeString);
            }
        }
    }

    private void checkIfMusicDownloaded(MessageObject messageObject) {
        File cacheFile = null;
        if (messageObject.messageOwner.attachPath != null && messageObject.messageOwner.attachPath.length() > 0) {
            cacheFile = new File(messageObject.messageOwner.attachPath);
            if (!cacheFile.exists()) {
                cacheFile = null;
            }
        }
        if (cacheFile == null) {
            cacheFile = FileLoader.getPathToMessage(messageObject.messageOwner);
        }
        if (cacheFile.exists()) {
            MediaController.getInstance().removeLoadingFileObserver(this);
            this.progressView.setVisibility(4);
            this.seekBarView.setVisibility(0);
            this.playButton.setEnabled(true);
            return;
        }
        String fileName = messageObject.getFileName();
        MediaController.getInstance().addLoadingFileObserver(fileName, this);
        Float progress = ImageLoader.getInstance().getFileProgress(fileName);
        this.progressView.setProgress(progress != null ? progress.floatValue() : 0.0f, false);
        this.progressView.setVisibility(0);
        this.seekBarView.setVisibility(4);
        this.playButton.setEnabled(false);
    }

    private void updateTitle(boolean shutdown) {
        MessageObject messageObject = MediaController.getInstance().getPlayingMessageObject();
        if (!(messageObject == null && shutdown) && (messageObject == null || messageObject.isMusic())) {
            if (messageObject != null) {
                checkIfMusicDownloaded(messageObject);
                updateProgress(messageObject);
                if (MediaController.getInstance().isAudioPaused()) {
                    this.playButton.setImageDrawable(Theme.createSimpleSelectorDrawable(this.playButton.getContext(), R.drawable.pl_play, Theme.getColor(Theme.key_player_button), Theme.getColor(Theme.key_player_buttonActive)));
                } else {
                    this.playButton.setImageDrawable(Theme.createSimpleSelectorDrawable(this.playButton.getContext(), R.drawable.pl_pause, Theme.getColor(Theme.key_player_button), Theme.getColor(Theme.key_player_buttonActive)));
                }
                if (this.actionBar != null) {
                    this.actionBar.setTitle(messageObject.getMusicTitle());
                    this.actionBar.getTitleTextView().setTextColor(-14606047);
                    this.actionBar.setSubtitle(messageObject.getMusicAuthor());
                    this.actionBar.getSubtitleTextView().setTextColor(-7697782);
                }
                AudioInfo audioInfo = MediaController.getInstance().getAudioInfo();
                if (audioInfo == null || audioInfo.getCover() == null) {
                    this.placeholder.setImageResource(R.drawable.nocover);
                    this.placeholder.setPadding(0, 0, 0, AndroidUtilities.dp(BitmapDescriptorFactory.HUE_ORANGE));
                    this.placeholder.setScaleType(ScaleType.CENTER);
                } else {
                    this.placeholder.setImageBitmap(audioInfo.getCover());
                    this.placeholder.setPadding(0, 0, 0, 0);
                    this.placeholder.setScaleType(ScaleType.CENTER_CROP);
                }
                if (this.durationTextView != null) {
                    CharSequence format;
                    int duration = 0;
                    Document document = messageObject.getDocument();
                    if (document != null) {
                        for (int a = 0; a < document.attributes.size(); a++) {
                            DocumentAttribute attribute = (DocumentAttribute) document.attributes.get(a);
                            if (attribute instanceof TL_documentAttributeAudio) {
                                duration = attribute.duration;
                                break;
                            }
                        }
                    }
                    TextView textView = this.durationTextView;
                    if (duration != 0) {
                        format = String.format("%d:%02d", new Object[]{Integer.valueOf(duration / 60), Integer.valueOf(duration % 60)});
                    } else {
                        format = "-:--";
                    }
                    textView.setText(format);
                }
            }
        } else if (this.parentLayout == null || this.parentLayout.fragmentsStack.isEmpty() || this.parentLayout.fragmentsStack.get(this.parentLayout.fragmentsStack.size() - 1) != this) {
            removeSelfFromStack();
        } else {
            finishFragment();
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        ThemeDescription[] themeDescriptionArr = new ThemeDescription[7];
        themeDescriptionArr[0] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite);
        themeDescriptionArr[1] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault);
        themeDescriptionArr[2] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        themeDescriptionArr[3] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault);
        themeDescriptionArr[4] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon);
        themeDescriptionArr[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle);
        themeDescriptionArr[6] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector);
        return themeDescriptionArr;
    }
}
