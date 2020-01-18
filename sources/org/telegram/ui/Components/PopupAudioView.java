package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.view.View;
import android.view.View.MeasureSpec;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.DownloadController.FileDownloadProgressListener;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.tgnet.TLRPC.DocumentAttribute;
import org.telegram.tgnet.TLRPC.TL_documentAttributeAudio;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.BaseCell;
import org.telegram.ui.Components.SeekBar.SeekBarDelegate;
import org.telegram.ui.Components.SeekBar.SeekBarDelegate.-CC;

public class PopupAudioView extends BaseCell implements SeekBarDelegate, FileDownloadProgressListener {
    private int TAG;
    private int buttonPressed = 0;
    private int buttonState = 0;
    private int buttonX;
    private int buttonY;
    private int currentAccount;
    protected MessageObject currentMessageObject;
    private String lastTimeString = null;
    private ProgressView progressView;
    private SeekBar seekBar;
    private int seekBarX;
    private int seekBarY;
    private StaticLayout timeLayout;
    private TextPaint timePaint = new TextPaint(1);
    int timeWidth = 0;
    private int timeX;
    private boolean wasLayout = false;

    public void onProgressUpload(String str, long j, long j2, boolean z) {
    }

    public /* synthetic */ void onSeekBarContinuousDrag(float f) {
        -CC.$default$onSeekBarContinuousDrag(this, f);
    }

    public PopupAudioView(Context context) {
        super(context);
        this.timePaint.setTextSize((float) AndroidUtilities.dp(16.0f));
        this.TAG = DownloadController.getInstance(this.currentAccount).generateObserverTag();
        this.seekBar = new SeekBar(getContext());
        this.seekBar.setDelegate(this);
        this.progressView = new ProgressView();
    }

    public void setMessageObject(MessageObject messageObject) {
        if (this.currentMessageObject != messageObject) {
            this.currentAccount = messageObject.currentAccount;
            SeekBar seekBar = this.seekBar;
            String str = "chat_inAudioSeekbar";
            int color = Theme.getColor(str);
            int color2 = Theme.getColor(str);
            str = "chat_inAudioSeekbarFill";
            seekBar.setColors(color, color2, Theme.getColor(str), Theme.getColor(str), Theme.getColor("chat_inAudioSeekbarSelected"));
            this.progressView.setProgressColors(-2497813, -7944712);
            this.currentMessageObject = messageObject;
            this.wasLayout = false;
            requestLayout();
        }
        updateButtonState();
    }

    public final MessageObject getMessageObject() {
        return this.currentMessageObject;
    }

    /* Access modifiers changed, original: protected */
    public void onMeasure(int i, int i2) {
        setMeasuredDimension(MeasureSpec.getSize(i), AndroidUtilities.dp(56.0f));
    }

    /* Access modifiers changed, original: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        if (this.currentMessageObject != null) {
            this.seekBarX = AndroidUtilities.dp(54.0f);
            this.buttonX = AndroidUtilities.dp(10.0f);
            this.timeX = (getMeasuredWidth() - this.timeWidth) - AndroidUtilities.dp(16.0f);
            this.seekBar.setSize((getMeasuredWidth() - AndroidUtilities.dp(70.0f)) - this.timeWidth, AndroidUtilities.dp(30.0f));
            this.progressView.width = (getMeasuredWidth() - AndroidUtilities.dp(94.0f)) - this.timeWidth;
            this.progressView.height = AndroidUtilities.dp(30.0f);
            this.seekBarY = AndroidUtilities.dp(13.0f);
            this.buttonY = AndroidUtilities.dp(10.0f);
            updateProgress();
            if (z || !this.wasLayout) {
                this.wasLayout = true;
            }
        }
    }

    /* Access modifiers changed, original: protected */
    public void onDraw(Canvas canvas) {
        if (this.currentMessageObject != null) {
            if (this.wasLayout) {
                int i = AndroidUtilities.displaySize.y;
                if (getParent() instanceof View) {
                    i = ((View) getParent()).getMeasuredHeight();
                }
                Theme.chat_msgInMediaDrawable.setTop((int) getY(), i, false, false);
                BaseCell.setDrawableBounds(Theme.chat_msgInMediaDrawable, 0, 0, getMeasuredWidth(), getMeasuredHeight());
                Theme.chat_msgInMediaDrawable.draw(canvas);
                if (this.currentMessageObject != null) {
                    canvas.save();
                    i = this.buttonState;
                    if (i == 0 || i == 1) {
                        canvas.translate((float) this.seekBarX, (float) this.seekBarY);
                        this.seekBar.draw(canvas);
                    } else {
                        canvas.translate((float) (this.seekBarX + AndroidUtilities.dp(12.0f)), (float) this.seekBarY);
                        this.progressView.draw(canvas);
                    }
                    canvas.restore();
                    i = this.buttonState + 5;
                    this.timePaint.setColor(-6182221);
                    Drawable drawable = Theme.chat_fileStatesDrawable[i][this.buttonPressed];
                    int dp = AndroidUtilities.dp(36.0f);
                    BaseCell.setDrawableBounds(drawable, ((dp - drawable.getIntrinsicWidth()) / 2) + this.buttonX, ((dp - drawable.getIntrinsicHeight()) / 2) + this.buttonY);
                    drawable.draw(canvas);
                    canvas.save();
                    canvas.translate((float) this.timeX, (float) AndroidUtilities.dp(18.0f));
                    this.timeLayout.draw(canvas);
                    canvas.restore();
                    return;
                }
                return;
            }
            requestLayout();
        }
    }

    /* Access modifiers changed, original: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
    }

    /* JADX WARNING: Missing block: B:34:0x00a7, code skipped:
            if (r1 <= ((float) (r0 + r4))) goto L_0x00ae;
     */
    public boolean onTouchEvent(android.view.MotionEvent r8) {
        /*
        r7 = this;
        r0 = r8.getX();
        r1 = r8.getY();
        r2 = r7.seekBar;
        r3 = r8.getAction();
        r4 = r8.getX();
        r5 = r7.seekBarX;
        r5 = (float) r5;
        r4 = r4 - r5;
        r5 = r8.getY();
        r6 = r7.seekBarY;
        r6 = (float) r6;
        r5 = r5 - r6;
        r2 = r2.onTouch(r3, r4, r5);
        r3 = 1;
        if (r2 == 0) goto L_0x0037;
    L_0x0025:
        r8 = r8.getAction();
        if (r8 != 0) goto L_0x0032;
    L_0x002b:
        r8 = r7.getParent();
        r8.requestDisallowInterceptTouchEvent(r3);
    L_0x0032:
        r7.invalidate();
        goto L_0x00b4;
    L_0x0037:
        r4 = NUM; // 0x42100000 float:36.0 double:5.47595105E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r5 = r8.getAction();
        if (r5 != 0) goto L_0x0064;
    L_0x0043:
        r5 = r7.buttonX;
        r6 = (float) r5;
        r6 = (r0 > r6 ? 1 : (r0 == r6 ? 0 : -1));
        if (r6 < 0) goto L_0x00ae;
    L_0x004a:
        r5 = r5 + r4;
        r5 = (float) r5;
        r0 = (r0 > r5 ? 1 : (r0 == r5 ? 0 : -1));
        if (r0 > 0) goto L_0x00ae;
    L_0x0050:
        r0 = r7.buttonY;
        r5 = (float) r0;
        r5 = (r1 > r5 ? 1 : (r1 == r5 ? 0 : -1));
        if (r5 < 0) goto L_0x00ae;
    L_0x0057:
        r0 = r0 + r4;
        r0 = (float) r0;
        r0 = (r1 > r0 ? 1 : (r1 == r0 ? 0 : -1));
        if (r0 > 0) goto L_0x00ae;
    L_0x005d:
        r7.buttonPressed = r3;
        r7.invalidate();
        r2 = 1;
        goto L_0x00ae;
    L_0x0064:
        r5 = r7.buttonPressed;
        if (r5 != r3) goto L_0x00ae;
    L_0x0068:
        r5 = r8.getAction();
        r6 = 0;
        if (r5 != r3) goto L_0x007b;
    L_0x006f:
        r7.buttonPressed = r6;
        r7.playSoundEffect(r6);
        r7.didPressedButton();
        r7.invalidate();
        goto L_0x00ae;
    L_0x007b:
        r3 = r8.getAction();
        r5 = 3;
        if (r3 != r5) goto L_0x0088;
    L_0x0082:
        r7.buttonPressed = r6;
        r7.invalidate();
        goto L_0x00ae;
    L_0x0088:
        r3 = r8.getAction();
        r5 = 2;
        if (r3 != r5) goto L_0x00ae;
    L_0x008f:
        r3 = r7.buttonX;
        r5 = (float) r3;
        r5 = (r0 > r5 ? 1 : (r0 == r5 ? 0 : -1));
        if (r5 < 0) goto L_0x00a9;
    L_0x0096:
        r3 = r3 + r4;
        r3 = (float) r3;
        r0 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1));
        if (r0 > 0) goto L_0x00a9;
    L_0x009c:
        r0 = r7.buttonY;
        r3 = (float) r0;
        r3 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1));
        if (r3 < 0) goto L_0x00a9;
    L_0x00a3:
        r0 = r0 + r4;
        r0 = (float) r0;
        r0 = (r1 > r0 ? 1 : (r1 == r0 ? 0 : -1));
        if (r0 <= 0) goto L_0x00ae;
    L_0x00a9:
        r7.buttonPressed = r6;
        r7.invalidate();
    L_0x00ae:
        if (r2 != 0) goto L_0x00b4;
    L_0x00b0:
        r2 = super.onTouchEvent(r8);
    L_0x00b4:
        return r2;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.PopupAudioView.onTouchEvent(android.view.MotionEvent):boolean");
    }

    private void didPressedButton() {
        int i = this.buttonState;
        if (i == 0) {
            boolean playMessage = MediaController.getInstance().playMessage(this.currentMessageObject);
            if (!this.currentMessageObject.isOut() && this.currentMessageObject.isContentUnread() && this.currentMessageObject.messageOwner.to_id.channel_id == 0) {
                MessagesController.getInstance(this.currentAccount).markMessageContentAsRead(this.currentMessageObject);
                this.currentMessageObject.setContentIsRead();
            }
            if (playMessage) {
                this.buttonState = 1;
                invalidate();
            }
        } else if (i == 1) {
            if (MediaController.getInstance().lambda$startAudioAgain$6$MediaController(this.currentMessageObject)) {
                this.buttonState = 0;
                invalidate();
            }
        } else if (i == 2) {
            FileLoader.getInstance(this.currentAccount).loadFile(this.currentMessageObject.getDocument(), this.currentMessageObject, 1, 0);
            this.buttonState = 4;
            invalidate();
        } else if (i == 3) {
            FileLoader.getInstance(this.currentAccount).cancelLoadFile(this.currentMessageObject.getDocument());
            this.buttonState = 2;
            invalidate();
        }
    }

    public void updateProgress() {
        if (this.currentMessageObject != null) {
            int i;
            if (!this.seekBar.isDragging()) {
                this.seekBar.setProgress(this.currentMessageObject.audioProgress);
            }
            if (MediaController.getInstance().isPlayingMessage(this.currentMessageObject)) {
                i = this.currentMessageObject.audioProgressSec;
            } else {
                i = 0;
                for (int i2 = 0; i2 < this.currentMessageObject.getDocument().attributes.size(); i2++) {
                    DocumentAttribute documentAttribute = (DocumentAttribute) this.currentMessageObject.getDocument().attributes.get(i2);
                    if (documentAttribute instanceof TL_documentAttributeAudio) {
                        i = documentAttribute.duration;
                        break;
                    }
                }
            }
            String formatLongDuration = AndroidUtilities.formatLongDuration(i);
            String str = this.lastTimeString;
            if (str == null || !(str == null || str.equals(formatLongDuration))) {
                this.timeWidth = (int) Math.ceil((double) this.timePaint.measureText(formatLongDuration));
                this.timeLayout = new StaticLayout(formatLongDuration, this.timePaint, this.timeWidth, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            }
            invalidate();
        }
    }

    public void downloadAudioIfNeed() {
        if (this.buttonState == 2) {
            FileLoader.getInstance(this.currentAccount).loadFile(this.currentMessageObject.getDocument(), this.currentMessageObject, 1, 0);
            this.buttonState = 3;
            invalidate();
        }
    }

    public void updateButtonState() {
        String fileName = this.currentMessageObject.getFileName();
        if (FileLoader.getPathToMessage(this.currentMessageObject.messageOwner).exists()) {
            DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
            boolean isPlayingMessage = MediaController.getInstance().isPlayingMessage(this.currentMessageObject);
            if (!isPlayingMessage || (isPlayingMessage && MediaController.getInstance().isMessagePaused())) {
                this.buttonState = 0;
            } else {
                this.buttonState = 1;
            }
            this.progressView.setProgress(0.0f);
        } else {
            DownloadController.getInstance(this.currentAccount).addLoadingFileObserver(fileName, this);
            if (FileLoader.getInstance(this.currentAccount).isLoadingFile(fileName)) {
                this.buttonState = 3;
                Float fileProgress = ImageLoader.getInstance().getFileProgress(fileName);
                if (fileProgress != null) {
                    this.progressView.setProgress(fileProgress.floatValue());
                } else {
                    this.progressView.setProgress(0.0f);
                }
            } else {
                this.buttonState = 2;
                this.progressView.setProgress(0.0f);
            }
        }
        updateProgress();
    }

    public void onFailedDownload(String str, boolean z) {
        updateButtonState();
    }

    public void onSuccessDownload(String str) {
        updateButtonState();
    }

    public void onProgressDownload(String str, long j, long j2) {
        this.progressView.setProgress(Math.min(1.0f, ((float) j) / ((float) j2)));
        if (this.buttonState != 3) {
            updateButtonState();
        }
        invalidate();
    }

    public int getObserverTag() {
        return this.TAG;
    }

    public void onSeekBarDrag(float f) {
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject != null) {
            messageObject.audioProgress = f;
            MediaController.getInstance().seekToProgress(this.currentMessageObject, f);
        }
    }
}
