package org.telegram.ui.Cells;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import java.io.File;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.DownloadController.FileDownloadProgressListener;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.RadialProgress;

public class AudioPlayerCell extends View implements FileDownloadProgressListener {
    private int TAG = DownloadController.getInstance(this.currentAccount).generateObserverTag();
    private boolean buttonPressed;
    private int buttonState;
    private int buttonX;
    private int buttonY;
    private int currentAccount = UserConfig.selectedAccount;
    private MessageObject currentMessageObject;
    private StaticLayout descriptionLayout;
    private int descriptionY = AndroidUtilities.dp(29.0f);
    private int hasMiniProgress;
    private boolean miniButtonPressed;
    private int miniButtonState;
    private RadialProgress radialProgress = new RadialProgress(this);
    private StaticLayout titleLayout;
    private int titleY = AndroidUtilities.dp(9.0f);

    public void onProgressUpload(String str, float f, boolean z) {
    }

    public AudioPlayerCell(Context context) {
        super(context);
    }

    @SuppressLint({"DrawAllocation"})
    protected void onMeasure(int i, int i2) {
        this.descriptionLayout = null;
        this.titleLayout = null;
        int size = (MeasureSpec.getSize(i) - AndroidUtilities.dp((float) AndroidUtilities.leftBaseline)) - AndroidUtilities.dp(28.0f);
        try {
            String musicTitle = r1.currentMessageObject.getMusicTitle();
            r1.titleLayout = new StaticLayout(TextUtils.ellipsize(musicTitle.replace('\n', ' '), Theme.chat_contextResult_titleTextPaint, (float) Math.min((int) Math.ceil((double) Theme.chat_contextResult_titleTextPaint.measureText(musicTitle)), size), TruncateAt.END), Theme.chat_contextResult_titleTextPaint, size + AndroidUtilities.dp(4.0f), Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
        } catch (Throwable e) {
            FileLog.m3e(e);
        }
        try {
            musicTitle = r1.currentMessageObject.getMusicAuthor();
            r1.descriptionLayout = new StaticLayout(TextUtils.ellipsize(musicTitle.replace('\n', ' '), Theme.chat_contextResult_descriptionTextPaint, (float) Math.min((int) Math.ceil((double) Theme.chat_contextResult_descriptionTextPaint.measureText(musicTitle)), size), TruncateAt.END), Theme.chat_contextResult_descriptionTextPaint, size + AndroidUtilities.dp(4.0f), Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
        } catch (Throwable e2) {
            FileLog.m3e(e2);
        }
        setMeasuredDimension(MeasureSpec.getSize(i), AndroidUtilities.dp(56.0f));
        int size2 = LocaleController.isRTL ? (MeasureSpec.getSize(i) - AndroidUtilities.dp(8.0f)) - AndroidUtilities.dp(52.0f) : AndroidUtilities.dp(8.0f);
        RadialProgress radialProgress = r1.radialProgress;
        int dp = AndroidUtilities.dp(4.0f) + size2;
        r1.buttonX = dp;
        int dp2 = AndroidUtilities.dp(6.0f);
        r1.buttonY = dp2;
        radialProgress.setProgressRect(dp, dp2, size2 + AndroidUtilities.dp(48.0f), AndroidUtilities.dp(50.0f));
    }

    public void setMessageObject(MessageObject messageObject) {
        this.currentMessageObject = messageObject;
        requestLayout();
        updateButtonState(null);
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
    }

    public MessageObject getMessageObject() {
        return this.currentMessageObject;
    }

    private boolean checkAudioMotionEvent(MotionEvent motionEvent) {
        boolean z;
        int x = (int) motionEvent.getX();
        int y = (int) motionEvent.getY();
        int dp = AndroidUtilities.dp(36.0f);
        if (this.miniButtonState >= 0) {
            int dp2 = AndroidUtilities.dp(27.0f);
            if (x >= this.buttonX + dp2 && x <= (this.buttonX + dp2) + dp && y >= this.buttonY + dp2 && y <= (this.buttonY + dp2) + dp) {
                z = true;
                if (motionEvent.getAction() != 0) {
                    if (z) {
                        this.miniButtonPressed = true;
                        invalidate();
                        updateRadialProgressBackground();
                        return true;
                    }
                } else if (this.miniButtonPressed) {
                    if (motionEvent.getAction() == 1) {
                        this.miniButtonPressed = false;
                        playSoundEffect(0);
                        didPressedMiniButton(true);
                        invalidate();
                    } else if (motionEvent.getAction() != 3) {
                        this.miniButtonPressed = false;
                        invalidate();
                    } else if (motionEvent.getAction() == 2 && !z) {
                        this.miniButtonPressed = false;
                        invalidate();
                    }
                    updateRadialProgressBackground();
                }
                return false;
            }
        }
        z = false;
        if (motionEvent.getAction() != 0) {
            if (this.miniButtonPressed) {
                if (motionEvent.getAction() == 1) {
                    this.miniButtonPressed = false;
                    playSoundEffect(0);
                    didPressedMiniButton(true);
                    invalidate();
                } else if (motionEvent.getAction() != 3) {
                    this.miniButtonPressed = false;
                    invalidate();
                } else {
                    this.miniButtonPressed = false;
                    invalidate();
                }
                updateRadialProgressBackground();
            }
        } else if (z) {
            this.miniButtonPressed = true;
            invalidate();
            updateRadialProgressBackground();
            return true;
        }
        return false;
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (this.currentMessageObject == null) {
            return super.onTouchEvent(motionEvent);
        }
        boolean checkAudioMotionEvent = checkAudioMotionEvent(motionEvent);
        if (motionEvent.getAction() == 3) {
            this.miniButtonPressed = false;
            this.buttonPressed = false;
            checkAudioMotionEvent = false;
        }
        return checkAudioMotionEvent;
    }

    private void updateRadialProgressBackground() {
        this.radialProgress.swapBackground(getDrawableForCurrentState());
        if (this.hasMiniProgress != 0) {
            this.radialProgress.swapMiniBackground(getMiniDrawableForCurrentState());
        }
    }

    private void didPressedMiniButton(boolean z) {
        if (!this.miniButtonState) {
            this.miniButtonState = 1;
            this.radialProgress.setProgress(0.0f, false);
            FileLoader.getInstance(this.currentAccount).loadFile(this.currentMessageObject.getDocument(), true, 0);
            this.radialProgress.setMiniBackground(getMiniDrawableForCurrentState(), true, false);
            invalidate();
        } else if (this.miniButtonState) {
            if (MediaController.getInstance().isPlayingMessage(this.currentMessageObject)) {
                MediaController.getInstance().cleanupPlayer(true, true);
            }
            this.miniButtonState = 0;
            FileLoader.getInstance(this.currentAccount).cancelLoadFile(this.currentMessageObject.getDocument());
            this.radialProgress.setMiniBackground(getMiniDrawableForCurrentState(), true, false);
            invalidate();
        }
    }

    public void didPressedButton() {
        if (this.buttonState == 0) {
            if (this.miniButtonState == 0) {
                FileLoader.getInstance(this.currentAccount).loadFile(this.currentMessageObject.getDocument(), true, 0);
            }
            if (MediaController.getInstance().findMessageInPlaylistAndPlay(this.currentMessageObject)) {
                if (this.hasMiniProgress == 2 && this.miniButtonState != 1) {
                    this.miniButtonState = 1;
                    this.radialProgress.setProgress(0.0f, false);
                    this.radialProgress.setMiniBackground(getMiniDrawableForCurrentState(), true, false);
                }
                this.buttonState = 1;
                this.radialProgress.setBackground(getDrawableForCurrentState(), false, false);
                invalidate();
            }
        } else if (this.buttonState == 1) {
            if (MediaController.getInstance().pauseMessage(this.currentMessageObject)) {
                this.buttonState = 0;
                this.radialProgress.setBackground(getDrawableForCurrentState(), false, false);
                invalidate();
            }
        } else if (this.buttonState == 2) {
            this.radialProgress.setProgress(0.0f, false);
            FileLoader.getInstance(this.currentAccount).loadFile(this.currentMessageObject.getDocument(), true, 0);
            this.buttonState = 4;
            this.radialProgress.setBackground(getDrawableForCurrentState(), true, false);
            invalidate();
        } else if (this.buttonState == 4) {
            FileLoader.getInstance(this.currentAccount).cancelLoadFile(this.currentMessageObject.getDocument());
            this.buttonState = 2;
            this.radialProgress.setBackground(getDrawableForCurrentState(), false, false);
            invalidate();
        }
    }

    protected void onDraw(Canvas canvas) {
        float f = 8.0f;
        if (this.titleLayout != null) {
            canvas.save();
            canvas.translate((float) AndroidUtilities.dp(LocaleController.isRTL ? 8.0f : (float) AndroidUtilities.leftBaseline), (float) this.titleY);
            this.titleLayout.draw(canvas);
            canvas.restore();
        }
        if (this.descriptionLayout != null) {
            Theme.chat_contextResult_descriptionTextPaint.setColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2));
            canvas.save();
            if (!LocaleController.isRTL) {
                f = (float) AndroidUtilities.leftBaseline;
            }
            canvas.translate((float) AndroidUtilities.dp(f), (float) this.descriptionY);
            this.descriptionLayout.draw(canvas);
            canvas.restore();
        }
        this.radialProgress.setProgressColor(Theme.getColor(this.buttonPressed ? Theme.key_chat_inAudioSelectedProgress : Theme.key_chat_inAudioProgress));
        this.radialProgress.draw(canvas);
    }

    private Drawable getMiniDrawableForCurrentState() {
        if (this.miniButtonState < 0) {
            return null;
        }
        this.radialProgress.setAlphaForPrevious(false);
        return Theme.chat_fileMiniStatesDrawable[this.miniButtonState + 2][this.miniButtonPressed];
    }

    private Drawable getDrawableForCurrentState() {
        if (this.buttonState == -1) {
            return null;
        }
        this.radialProgress.setAlphaForPrevious(false);
        return Theme.chat_fileStatesDrawable[this.buttonState + 5][this.buttonPressed];
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void updateButtonState(boolean z) {
        File file;
        String fileName = this.currentMessageObject.getFileName();
        if (!TextUtils.isEmpty(this.currentMessageObject.messageOwner.attachPath)) {
            file = new File(this.currentMessageObject.messageOwner.attachPath);
        }
        file = null;
        if (file == null) {
            file = FileLoader.getPathToAttach(this.currentMessageObject.getDocument());
        }
        boolean z2 = false;
        if (TextUtils.isEmpty(fileName)) {
            this.radialProgress.setBackground(null, false, false);
            return;
        }
        if (file.exists() && file.length() == 0) {
            file.delete();
        }
        boolean exists = file.exists();
        if (SharedConfig.streamMedia && ((int) this.currentMessageObject.getDialogId()) != 0) {
            this.hasMiniProgress = exists ? 1 : 2;
            exists = true;
        }
        Float fileProgress;
        if (this.hasMiniProgress != 0) {
            RadialProgress radialProgress;
            Drawable miniDrawableForCurrentState;
            this.radialProgress.setMiniProgressBackgroundColor(Theme.getColor(this.currentMessageObject.isOutOwner() ? Theme.key_chat_outLoader : Theme.key_chat_inLoader));
            exists = MediaController.getInstance().isPlayingMessage(this.currentMessageObject);
            if (exists) {
                if (!exists || !MediaController.getInstance().isMessagePaused()) {
                    this.buttonState = 1;
                    this.radialProgress.setBackground(getDrawableForCurrentState(), false, z);
                    if (this.hasMiniProgress != 1) {
                        DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
                        this.miniButtonState = -1;
                    } else {
                        DownloadController.getInstance(this.currentAccount).addLoadingFileObserver(fileName, this.currentMessageObject, this);
                        if (FileLoader.getInstance(this.currentAccount).isLoadingFile(fileName)) {
                            this.radialProgress.setProgress(0.0f, z);
                            this.miniButtonState = 0;
                        } else {
                            this.miniButtonState = 1;
                            fileProgress = ImageLoader.getInstance().getFileProgress(fileName);
                            if (fileProgress == null) {
                                this.radialProgress.setProgress(fileProgress.floatValue(), z);
                            } else {
                                this.radialProgress.setProgress(0.0f, z);
                            }
                        }
                    }
                    radialProgress = this.radialProgress;
                    miniDrawableForCurrentState = getMiniDrawableForCurrentState();
                    if (this.miniButtonState == 1) {
                        z2 = true;
                    }
                    radialProgress.setMiniBackground(miniDrawableForCurrentState, z2, z);
                }
            }
            this.buttonState = 0;
            this.radialProgress.setBackground(getDrawableForCurrentState(), false, z);
            if (this.hasMiniProgress != 1) {
                DownloadController.getInstance(this.currentAccount).addLoadingFileObserver(fileName, this.currentMessageObject, this);
                if (FileLoader.getInstance(this.currentAccount).isLoadingFile(fileName)) {
                    this.miniButtonState = 1;
                    fileProgress = ImageLoader.getInstance().getFileProgress(fileName);
                    if (fileProgress == null) {
                        this.radialProgress.setProgress(0.0f, z);
                    } else {
                        this.radialProgress.setProgress(fileProgress.floatValue(), z);
                    }
                } else {
                    this.radialProgress.setProgress(0.0f, z);
                    this.miniButtonState = 0;
                }
            } else {
                DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
                this.miniButtonState = -1;
            }
            radialProgress = this.radialProgress;
            miniDrawableForCurrentState = getMiniDrawableForCurrentState();
            if (this.miniButtonState == 1) {
                z2 = true;
            }
            radialProgress.setMiniBackground(miniDrawableForCurrentState, z2, z);
        } else if (exists) {
            DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
            boolean isPlayingMessage = MediaController.getInstance().isPlayingMessage(this.currentMessageObject);
            if (isPlayingMessage) {
                if (!isPlayingMessage || !MediaController.getInstance().isMessagePaused()) {
                    this.buttonState = 1;
                    this.radialProgress.setBackground(getDrawableForCurrentState(), false, z);
                    invalidate();
                }
            }
            this.buttonState = 0;
            this.radialProgress.setBackground(getDrawableForCurrentState(), false, z);
            invalidate();
        } else {
            DownloadController.getInstance(this.currentAccount).addLoadingFileObserver(fileName, this);
            if (FileLoader.getInstance(this.currentAccount).isLoadingFile(fileName)) {
                this.buttonState = 4;
                fileProgress = ImageLoader.getInstance().getFileProgress(fileName);
                if (fileProgress != null) {
                    this.radialProgress.setProgress(fileProgress.floatValue(), z);
                } else {
                    this.radialProgress.setProgress(0.0f, z);
                }
                this.radialProgress.setBackground(getDrawableForCurrentState(), true, z);
            } else {
                this.buttonState = 2;
                this.radialProgress.setProgress(0.0f, z);
                this.radialProgress.setBackground(getDrawableForCurrentState(), false, z);
            }
            invalidate();
        }
    }

    public void onFailedDownload(String str) {
        updateButtonState(null);
    }

    public void onSuccessDownload(String str) {
        this.radialProgress.setProgress(1.0f, true);
        updateButtonState(true);
    }

    public void onProgressDownload(String str, float f) {
        this.radialProgress.setProgress(f, true);
        if (this.hasMiniProgress != null) {
            if (this.miniButtonState != 1) {
                updateButtonState(false);
            }
        } else if (this.buttonState != 4) {
            updateButtonState(false);
        }
    }

    public int getObserverTag() {
        return this.TAG;
    }
}
