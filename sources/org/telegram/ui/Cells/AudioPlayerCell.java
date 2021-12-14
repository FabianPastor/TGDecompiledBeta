package org.telegram.ui.Cells;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import java.io.File;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$PhotoSize;
import org.telegram.tgnet.TLRPC$TL_photoSize;
import org.telegram.tgnet.TLRPC$TL_photoSizeProgressive;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.DotDividerSpan;
import org.telegram.ui.Components.RadialProgress2;
import org.telegram.ui.FilteredSearchView;

public class AudioPlayerCell extends View implements DownloadController.FileDownloadProgressListener {
    private int TAG;
    private boolean buttonPressed;
    private int buttonState;
    private int buttonX;
    private int buttonY;
    private int currentAccount = UserConfig.selectedAccount;
    private MessageObject currentMessageObject;
    private StaticLayout descriptionLayout;
    private int descriptionY = AndroidUtilities.dp(29.0f);
    private SpannableStringBuilder dotSpan;
    private int hasMiniProgress;
    private boolean miniButtonPressed;
    private int miniButtonState;
    private RadialProgress2 radialProgress;
    private final Theme.ResourcesProvider resourcesProvider;
    private StaticLayout titleLayout;
    private int titleY = AndroidUtilities.dp(9.0f);
    private int viewType;

    public void onProgressUpload(String str, long j, long j2, boolean z) {
    }

    public AudioPlayerCell(Context context, int i, Theme.ResourcesProvider resourcesProvider2) {
        super(context);
        this.resourcesProvider = resourcesProvider2;
        this.viewType = i;
        RadialProgress2 radialProgress2 = new RadialProgress2(this, resourcesProvider2);
        this.radialProgress = radialProgress2;
        radialProgress2.setColors("chat_inLoader", "chat_inLoaderSelected", "chat_inMediaIcon", "chat_inMediaIconSelected");
        this.TAG = DownloadController.getInstance(this.currentAccount).generateObserverTag();
        setFocusable(true);
        if (i == 1) {
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(".");
            this.dotSpan = spannableStringBuilder;
            spannableStringBuilder.setSpan(new DotDividerSpan(), 0, 1, 0);
        }
    }

    /* access modifiers changed from: protected */
    @SuppressLint({"DrawAllocation"})
    public void onMeasure(int i, int i2) {
        this.descriptionLayout = null;
        this.titleLayout = null;
        int size = (View.MeasureSpec.getSize(i) - AndroidUtilities.dp((float) AndroidUtilities.leftBaseline)) - AndroidUtilities.dp(28.0f);
        try {
            String musicTitle = this.currentMessageObject.getMusicTitle();
            this.titleLayout = new StaticLayout(TextUtils.ellipsize(musicTitle.replace(10, ' '), Theme.chat_contextResult_titleTextPaint, (float) Math.min((int) Math.ceil((double) Theme.chat_contextResult_titleTextPaint.measureText(musicTitle)), size), TextUtils.TruncateAt.END), Theme.chat_contextResult_titleTextPaint, size + AndroidUtilities.dp(4.0f), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        try {
            CharSequence replace = this.currentMessageObject.getMusicAuthor().replace(10, ' ');
            if (this.viewType == 1) {
                replace = new SpannableStringBuilder(replace).append(' ').append(this.dotSpan).append(' ').append(FilteredSearchView.createFromInfoString(this.currentMessageObject));
            }
            this.descriptionLayout = new StaticLayout(TextUtils.ellipsize(replace, Theme.chat_contextResult_descriptionTextPaint, (float) size, TextUtils.TruncateAt.END), Theme.chat_contextResult_descriptionTextPaint, size + AndroidUtilities.dp(4.0f), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
        } catch (Exception e2) {
            FileLog.e((Throwable) e2);
        }
        setMeasuredDimension(View.MeasureSpec.getSize(i), AndroidUtilities.dp(56.0f));
        int size2 = LocaleController.isRTL ? (View.MeasureSpec.getSize(i) - AndroidUtilities.dp(8.0f)) - AndroidUtilities.dp(52.0f) : AndroidUtilities.dp(8.0f);
        RadialProgress2 radialProgress2 = this.radialProgress;
        int dp = AndroidUtilities.dp(4.0f) + size2;
        this.buttonX = dp;
        int dp2 = AndroidUtilities.dp(6.0f);
        this.buttonY = dp2;
        radialProgress2.setProgressRect(dp, dp2, size2 + AndroidUtilities.dp(48.0f), AndroidUtilities.dp(50.0f));
    }

    public void setMessageObject(MessageObject messageObject) {
        this.currentMessageObject = messageObject;
        TLRPC$Document document = messageObject.getDocument();
        TLRPC$PhotoSize closestPhotoSizeWithSize = document != null ? FileLoader.getClosestPhotoSizeWithSize(document.thumbs, 90) : null;
        if ((closestPhotoSizeWithSize instanceof TLRPC$TL_photoSize) || (closestPhotoSizeWithSize instanceof TLRPC$TL_photoSizeProgressive)) {
            this.radialProgress.setImageOverlay(closestPhotoSizeWithSize, document, messageObject);
        } else {
            String artworkUrl = messageObject.getArtworkUrl(true);
            if (!TextUtils.isEmpty(artworkUrl)) {
                this.radialProgress.setImageOverlay(artworkUrl);
            } else {
                this.radialProgress.setImageOverlay((TLRPC$PhotoSize) null, (TLRPC$Document) null, (Object) null);
            }
        }
        requestLayout();
        updateButtonState(false, false);
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.radialProgress.onDetachedFromWindow();
        DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.radialProgress.onAttachedToWindow();
    }

    public MessageObject getMessageObject() {
        return this.currentMessageObject;
    }

    /* JADX WARNING: Removed duplicated region for block: B:14:0x0039  */
    /* JADX WARNING: Removed duplicated region for block: B:16:0x0046  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean checkAudioMotionEvent(android.view.MotionEvent r9) {
        /*
            r8 = this;
            float r0 = r9.getX()
            int r0 = (int) r0
            float r1 = r9.getY()
            int r1 = (int) r1
            r2 = 1108344832(0x42100000, float:36.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r3 = r8.miniButtonState
            r4 = 1
            r5 = 0
            if (r3 < 0) goto L_0x0032
            r3 = 1104674816(0x41d80000, float:27.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r6 = r8.buttonX
            int r7 = r6 + r3
            if (r0 < r7) goto L_0x0032
            int r6 = r6 + r3
            int r6 = r6 + r2
            if (r0 > r6) goto L_0x0032
            int r0 = r8.buttonY
            int r6 = r0 + r3
            if (r1 < r6) goto L_0x0032
            int r0 = r0 + r3
            int r0 = r0 + r2
            if (r1 > r0) goto L_0x0032
            r0 = 1
            goto L_0x0033
        L_0x0032:
            r0 = 0
        L_0x0033:
            int r1 = r9.getAction()
            if (r1 != 0) goto L_0x0046
            if (r0 == 0) goto L_0x007e
            r8.miniButtonPressed = r4
            org.telegram.ui.Components.RadialProgress2 r9 = r8.radialProgress
            r9.setPressed(r4, r4)
            r8.invalidate()
            goto L_0x007f
        L_0x0046:
            boolean r1 = r8.miniButtonPressed
            if (r1 == 0) goto L_0x007e
            int r1 = r9.getAction()
            if (r1 != r4) goto L_0x005c
            r8.miniButtonPressed = r5
            r8.playSoundEffect(r5)
            r8.didPressedMiniButton(r4)
            r8.invalidate()
            goto L_0x0077
        L_0x005c:
            int r1 = r9.getAction()
            r2 = 3
            if (r1 != r2) goto L_0x0069
            r8.miniButtonPressed = r5
            r8.invalidate()
            goto L_0x0077
        L_0x0069:
            int r9 = r9.getAction()
            r1 = 2
            if (r9 != r1) goto L_0x0077
            if (r0 != 0) goto L_0x0077
            r8.miniButtonPressed = r5
            r8.invalidate()
        L_0x0077:
            org.telegram.ui.Components.RadialProgress2 r9 = r8.radialProgress
            boolean r0 = r8.miniButtonPressed
            r9.setPressed(r0, r4)
        L_0x007e:
            r4 = 0
        L_0x007f:
            return r4
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.AudioPlayerCell.checkAudioMotionEvent(android.view.MotionEvent):boolean");
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (this.currentMessageObject == null) {
            return super.onTouchEvent(motionEvent);
        }
        boolean checkAudioMotionEvent = checkAudioMotionEvent(motionEvent);
        if (motionEvent.getAction() != 3) {
            return checkAudioMotionEvent;
        }
        this.miniButtonPressed = false;
        this.buttonPressed = false;
        return false;
    }

    private void didPressedMiniButton(boolean z) {
        int i = this.miniButtonState;
        if (i == 0) {
            this.miniButtonState = 1;
            this.radialProgress.setProgress(0.0f, false);
            FileLoader.getInstance(this.currentAccount).loadFile(this.currentMessageObject.getDocument(), this.currentMessageObject, 1, 0);
            this.radialProgress.setMiniIcon(getMiniIconForCurrentState(), false, true);
            invalidate();
        } else if (i == 1) {
            if (MediaController.getInstance().isPlayingMessage(this.currentMessageObject)) {
                MediaController.getInstance().cleanupPlayer(true, true);
            }
            this.miniButtonState = 0;
            FileLoader.getInstance(this.currentAccount).cancelLoadFile(this.currentMessageObject.getDocument());
            this.radialProgress.setMiniIcon(getMiniIconForCurrentState(), false, true);
            invalidate();
        }
    }

    public void didPressedButton() {
        int i = this.buttonState;
        if (i == 0) {
            if (this.miniButtonState == 0) {
                FileLoader.getInstance(this.currentAccount).loadFile(this.currentMessageObject.getDocument(), this.currentMessageObject, 1, 0);
            }
            if (MediaController.getInstance().findMessageInPlaylistAndPlay(this.currentMessageObject)) {
                if (this.hasMiniProgress == 2 && this.miniButtonState != 1) {
                    this.miniButtonState = 1;
                    this.radialProgress.setProgress(0.0f, false);
                    this.radialProgress.setMiniIcon(getMiniIconForCurrentState(), false, true);
                }
                this.buttonState = 1;
                this.radialProgress.setIcon(getIconForCurrentState(), false, true);
                invalidate();
            }
        } else if (i == 1) {
            if (MediaController.getInstance().lambda$startAudioAgain$7(this.currentMessageObject)) {
                this.buttonState = 0;
                this.radialProgress.setIcon(getIconForCurrentState(), false, true);
                invalidate();
            }
        } else if (i == 2) {
            this.radialProgress.setProgress(0.0f, false);
            FileLoader.getInstance(this.currentAccount).loadFile(this.currentMessageObject.getDocument(), this.currentMessageObject, 1, 0);
            this.buttonState = 4;
            this.radialProgress.setIcon(getIconForCurrentState(), false, true);
            invalidate();
        } else if (i == 4) {
            FileLoader.getInstance(this.currentAccount).cancelLoadFile(this.currentMessageObject.getDocument());
            this.buttonState = 2;
            this.radialProgress.setIcon(getIconForCurrentState(), false, true);
            invalidate();
        }
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        float f = 8.0f;
        if (this.titleLayout != null) {
            canvas.save();
            canvas.translate((float) AndroidUtilities.dp(LocaleController.isRTL ? 8.0f : (float) AndroidUtilities.leftBaseline), (float) this.titleY);
            this.titleLayout.draw(canvas);
            canvas.restore();
        }
        if (this.descriptionLayout != null) {
            Theme.chat_contextResult_descriptionTextPaint.setColor(getThemedColor("windowBackgroundWhiteGrayText2"));
            canvas.save();
            if (!LocaleController.isRTL) {
                f = (float) AndroidUtilities.leftBaseline;
            }
            canvas.translate((float) AndroidUtilities.dp(f), (float) this.descriptionY);
            this.descriptionLayout.draw(canvas);
            canvas.restore();
        }
        this.radialProgress.setProgressColor(getThemedColor(this.buttonPressed ? "chat_inAudioSelectedProgress" : "chat_inAudioProgress"));
        this.radialProgress.draw(canvas);
    }

    private int getMiniIconForCurrentState() {
        int i = this.miniButtonState;
        if (i < 0) {
            return 4;
        }
        return i == 0 ? 2 : 3;
    }

    private int getIconForCurrentState() {
        int i = this.buttonState;
        if (i == 1) {
            return 1;
        }
        if (i == 2) {
            return 2;
        }
        return i == 4 ? 3 : 0;
    }

    public void updateButtonState(boolean z, boolean z2) {
        String fileName = this.currentMessageObject.getFileName();
        File file = null;
        if (!TextUtils.isEmpty(this.currentMessageObject.messageOwner.attachPath)) {
            File file2 = new File(this.currentMessageObject.messageOwner.attachPath);
            if (file2.exists()) {
                file = file2;
            }
        }
        if (file == null) {
            file = FileLoader.getPathToAttach(this.currentMessageObject.getDocument());
        }
        if (!TextUtils.isEmpty(fileName)) {
            if (file.exists() && file.length() == 0) {
                file.delete();
            }
            boolean exists = file.exists();
            if (!SharedConfig.streamMedia || ((int) this.currentMessageObject.getDialogId()) == 0) {
                this.miniButtonState = -1;
            } else {
                this.hasMiniProgress = exists ? 1 : 2;
                exists = true;
            }
            if (this.hasMiniProgress != 0) {
                this.radialProgress.setMiniProgressBackgroundColor(getThemedColor(this.currentMessageObject.isOutOwner() ? "chat_outLoader" : "chat_inLoader"));
                boolean isPlayingMessage = MediaController.getInstance().isPlayingMessage(this.currentMessageObject);
                if (!isPlayingMessage || (isPlayingMessage && MediaController.getInstance().isMessagePaused())) {
                    this.buttonState = 0;
                } else {
                    this.buttonState = 1;
                }
                this.radialProgress.setIcon(getIconForCurrentState(), z, z2);
                if (this.hasMiniProgress == 1) {
                    DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
                    this.miniButtonState = -1;
                    this.radialProgress.setMiniIcon(getMiniIconForCurrentState(), z, z2);
                    return;
                }
                DownloadController.getInstance(this.currentAccount).addLoadingFileObserver(fileName, this.currentMessageObject, this);
                if (!FileLoader.getInstance(this.currentAccount).isLoadingFile(fileName)) {
                    this.miniButtonState = 0;
                    this.radialProgress.setMiniIcon(getMiniIconForCurrentState(), z, z2);
                    return;
                }
                this.miniButtonState = 1;
                this.radialProgress.setMiniIcon(getMiniIconForCurrentState(), z, z2);
                Float fileProgress = ImageLoader.getInstance().getFileProgress(fileName);
                if (fileProgress != null) {
                    this.radialProgress.setProgress(fileProgress.floatValue(), z2);
                } else {
                    this.radialProgress.setProgress(0.0f, z2);
                }
            } else if (exists) {
                DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
                boolean isPlayingMessage2 = MediaController.getInstance().isPlayingMessage(this.currentMessageObject);
                if (!isPlayingMessage2 || (isPlayingMessage2 && MediaController.getInstance().isMessagePaused())) {
                    this.buttonState = 0;
                } else {
                    this.buttonState = 1;
                }
                this.radialProgress.setProgress(1.0f, z2);
                this.radialProgress.setIcon(getIconForCurrentState(), z, z2);
                invalidate();
            } else {
                DownloadController.getInstance(this.currentAccount).addLoadingFileObserver(fileName, this);
                if (!FileLoader.getInstance(this.currentAccount).isLoadingFile(fileName)) {
                    this.buttonState = 2;
                    this.radialProgress.setIcon(getIconForCurrentState(), z, z2);
                } else {
                    this.buttonState = 4;
                    Float fileProgress2 = ImageLoader.getInstance().getFileProgress(fileName);
                    if (fileProgress2 != null) {
                        this.radialProgress.setProgress(fileProgress2.floatValue(), z2);
                    } else {
                        this.radialProgress.setProgress(0.0f, z2);
                    }
                    this.radialProgress.setIcon(getIconForCurrentState(), z, z2);
                }
                invalidate();
            }
        }
    }

    public void onFailedDownload(String str, boolean z) {
        updateButtonState(true, z);
    }

    public void onSuccessDownload(String str) {
        this.radialProgress.setProgress(1.0f, true);
        updateButtonState(false, true);
    }

    public void onProgressDownload(String str, long j, long j2) {
        this.radialProgress.setProgress(Math.min(1.0f, ((float) j) / ((float) j2)), true);
        if (this.hasMiniProgress != 0) {
            if (this.miniButtonState != 1) {
                updateButtonState(false, true);
            }
        } else if (this.buttonState != 4) {
            updateButtonState(false, true);
        }
    }

    public int getObserverTag() {
        return this.TAG;
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        if (this.currentMessageObject.isMusic()) {
            accessibilityNodeInfo.setText(LocaleController.formatString("AccDescrMusicInfo", NUM, this.currentMessageObject.getMusicAuthor(), this.currentMessageObject.getMusicTitle()));
            return;
        }
        accessibilityNodeInfo.setText(this.titleLayout.getText() + ", " + this.descriptionLayout.getText());
    }

    private int getThemedColor(String str) {
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        Integer color = resourcesProvider2 != null ? resourcesProvider2.getColor(str) : null;
        return color != null ? color.intValue() : Theme.getColor(str);
    }
}
