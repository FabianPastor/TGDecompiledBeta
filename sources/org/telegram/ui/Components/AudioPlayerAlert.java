package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Property;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.audioinfo.AudioInfo;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$DocumentAttribute;
import org.telegram.tgnet.TLRPC$MessageEntity;
import org.telegram.tgnet.TLRPC$PhotoSize;
import org.telegram.tgnet.TLRPC$ReplyMarkup;
import org.telegram.tgnet.TLRPC$TL_documentAttributeAudio;
import org.telegram.tgnet.TLRPC$TL_photoSize;
import org.telegram.tgnet.TLRPC$TL_photoSizeProgressive;
import org.telegram.tgnet.TLRPC$WebPage;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.AudioPlayerCell;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.DialogsActivity;
import org.telegram.ui.LaunchActivity;

public class AudioPlayerAlert extends BottomSheet implements NotificationCenter.NotificationCenterDelegate, DownloadController.FileDownloadProgressListener {
    private int TAG;
    /* access modifiers changed from: private */
    public ActionBar actionBar;
    /* access modifiers changed from: private */
    public AnimatorSet actionBarAnimation;
    /* access modifiers changed from: private */
    public View actionBarShadow;
    private ClippingTextViewSwitcher authorTextView;
    /* access modifiers changed from: private */
    public BackupImageView bigAlbumConver;
    /* access modifiers changed from: private */
    public boolean blurredAnimationInProgress;
    /* access modifiers changed from: private */
    public FrameLayout blurredView;
    /* access modifiers changed from: private */
    public View[] buttons = new View[5];
    private CoverContainer coverContainer;
    private boolean currentAudioFinishedLoading;
    private String currentFile;
    /* access modifiers changed from: private */
    public boolean draggingSeekBar;
    /* access modifiers changed from: private */
    public TextView durationTextView;
    private ImageView emptyImageView;
    /* access modifiers changed from: private */
    public TextView emptySubtitleTextView;
    private TextView emptyTitleTextView;
    private LinearLayout emptyView;
    /* access modifiers changed from: private */
    public final Runnable forwardSeek = new Runnable() {
        public void run() {
            long duration = MediaController.getInstance().getDuration();
            if (duration == 0 || duration == -9223372036854775807L) {
                AudioPlayerAlert.this.lastRewindingTime = System.currentTimeMillis();
                return;
            }
            float f = AudioPlayerAlert.this.rewindingProgress;
            long currentTimeMillis = System.currentTimeMillis();
            AudioPlayerAlert audioPlayerAlert = AudioPlayerAlert.this;
            long j = currentTimeMillis - audioPlayerAlert.lastRewindingTime;
            audioPlayerAlert.lastRewindingTime = currentTimeMillis;
            long j2 = currentTimeMillis - audioPlayerAlert.lastUpdateRewindingPlayerTime;
            int i = audioPlayerAlert.rewindingForwardPressedCount;
            float f2 = (float) duration;
            float f3 = ((float) ((long) ((f * f2) + ((float) (((i == 1 ? 3 : i == 2 ? 6 : 12) * j) - j))))) / f2;
            if (f3 < 0.0f) {
                f3 = 0.0f;
            }
            audioPlayerAlert.rewindingProgress = f3;
            MessageObject playingMessageObject = MediaController.getInstance().getPlayingMessageObject();
            if (playingMessageObject != null && playingMessageObject.isMusic()) {
                if (!MediaController.getInstance().isMessagePaused()) {
                    MediaController.getInstance().getPlayingMessageObject().audioProgress = AudioPlayerAlert.this.rewindingProgress;
                }
                AudioPlayerAlert.this.updateProgress(playingMessageObject);
            }
            AudioPlayerAlert audioPlayerAlert2 = AudioPlayerAlert.this;
            if (audioPlayerAlert2.rewindingState == 1 && audioPlayerAlert2.rewindingForwardPressedCount > 0 && MediaController.getInstance().isMessagePaused()) {
                if (j2 > 200 || AudioPlayerAlert.this.rewindingProgress == 0.0f) {
                    AudioPlayerAlert.this.lastUpdateRewindingPlayerTime = currentTimeMillis;
                    MediaController.getInstance().seekToProgress(MediaController.getInstance().getPlayingMessageObject(), f3);
                }
                AudioPlayerAlert audioPlayerAlert3 = AudioPlayerAlert.this;
                if (audioPlayerAlert3.rewindingForwardPressedCount > 0 && audioPlayerAlert3.rewindingProgress > 0.0f) {
                    AndroidUtilities.runOnUIThread(audioPlayerAlert3.forwardSeek, 16);
                }
            }
        }
    };
    /* access modifiers changed from: private */
    public boolean inFullSize;
    private long lastBufferedPositionCheck;
    /* access modifiers changed from: private */
    public int lastDuration;
    private MessageObject lastMessageObject;
    long lastRewindingTime;
    /* access modifiers changed from: private */
    public int lastTime;
    long lastUpdateRewindingPlayerTime;
    /* access modifiers changed from: private */
    public LinearLayoutManager layoutManager;
    /* access modifiers changed from: private */
    public ListAdapter listAdapter;
    /* access modifiers changed from: private */
    public RecyclerListView listView;
    /* access modifiers changed from: private */
    public RLottieImageView nextButton;
    private ActionBarMenuItem optionsButton;
    /* access modifiers changed from: private */
    public LaunchActivity parentActivity;
    private ImageView playButton;
    private PlayPauseDrawable playPauseDrawable;
    /* access modifiers changed from: private */
    public ActionBarMenuItem playbackSpeedButton;
    /* access modifiers changed from: private */
    public FrameLayout playerLayout;
    /* access modifiers changed from: private */
    public View playerShadow;
    /* access modifiers changed from: private */
    public ArrayList<MessageObject> playlist;
    /* access modifiers changed from: private */
    public RLottieImageView prevButton;
    private LineProgressView progressView;
    private ActionBarMenuItem repeatButton;
    private ActionBarMenuSubItem repeatListItem;
    private ActionBarMenuSubItem repeatSongItem;
    private ActionBarMenuSubItem reverseOrderItem;
    int rewindingForwardPressedCount;
    float rewindingProgress = -1.0f;
    int rewindingState;
    /* access modifiers changed from: private */
    public int scrollOffsetY = Integer.MAX_VALUE;
    /* access modifiers changed from: private */
    public boolean scrollToSong = true;
    private ActionBarMenuItem searchItem;
    /* access modifiers changed from: private */
    public int searchOpenOffset;
    /* access modifiers changed from: private */
    public int searchOpenPosition = -1;
    /* access modifiers changed from: private */
    public boolean searchWas;
    /* access modifiers changed from: private */
    public boolean searching;
    /* access modifiers changed from: private */
    public SeekBarView seekBarView;
    private ActionBarMenuSubItem shuffleListItem;
    private ActionBarMenuSubItem[] speedItems = new ActionBarMenuSubItem[4];
    private SimpleTextView timeTextView;
    private ClippingTextViewSwitcher titleTextView;

    /* access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$new$7(View view, MotionEvent motionEvent) {
        return true;
    }

    /* access modifiers changed from: protected */
    public boolean canDismissWithSwipe() {
        return false;
    }

    public void onFailedDownload(String str, boolean z) {
    }

    public void onProgressUpload(String str, long j, long j2, boolean z) {
    }

    public void onSuccessDownload(String str) {
    }

    /* JADX WARNING: Illegal instructions before constructor call */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public AudioPlayerAlert(android.content.Context r31, org.telegram.ui.ActionBar.Theme.ResourcesProvider r32) {
        /*
            r30 = this;
            r0 = r30
            r8 = r31
            r9 = r32
            r10 = 1
            r0.<init>(r8, r10, r9)
            r11 = 4
            org.telegram.ui.ActionBar.ActionBarMenuSubItem[] r1 = new org.telegram.ui.ActionBar.ActionBarMenuSubItem[r11]
            r0.speedItems = r1
            r12 = 5
            android.view.View[] r1 = new android.view.View[r12]
            r0.buttons = r1
            r0.scrollToSong = r10
            r13 = -1
            r0.searchOpenPosition = r13
            r1 = 2147483647(0x7fffffff, float:NaN)
            r0.scrollOffsetY = r1
            r14 = -1082130432(0xffffffffbvar_, float:-1.0)
            r0.rewindingProgress = r14
            org.telegram.ui.Components.AudioPlayerAlert$1 r1 = new org.telegram.ui.Components.AudioPlayerAlert$1
            r1.<init>()
            r0.forwardSeek = r1
            org.telegram.messenger.MediaController r1 = org.telegram.messenger.MediaController.getInstance()
            org.telegram.messenger.MessageObject r1 = r1.getPlayingMessageObject()
            if (r1 == 0) goto L_0x0038
            int r2 = r1.currentAccount
            r0.currentAccount = r2
            goto L_0x003c
        L_0x0038:
            int r2 = org.telegram.messenger.UserConfig.selectedAccount
            r0.currentAccount = r2
        L_0x003c:
            r2 = r8
            org.telegram.ui.LaunchActivity r2 = (org.telegram.ui.LaunchActivity) r2
            r0.parentActivity = r2
            int r2 = r0.currentAccount
            org.telegram.messenger.DownloadController r2 = org.telegram.messenger.DownloadController.getInstance(r2)
            int r2 = r2.generateObserverTag()
            r0.TAG = r2
            int r2 = r0.currentAccount
            org.telegram.messenger.NotificationCenter r2 = org.telegram.messenger.NotificationCenter.getInstance(r2)
            int r3 = org.telegram.messenger.NotificationCenter.messagePlayingDidReset
            r2.addObserver(r0, r3)
            int r2 = r0.currentAccount
            org.telegram.messenger.NotificationCenter r2 = org.telegram.messenger.NotificationCenter.getInstance(r2)
            int r3 = org.telegram.messenger.NotificationCenter.messagePlayingPlayStateChanged
            r2.addObserver(r0, r3)
            int r2 = r0.currentAccount
            org.telegram.messenger.NotificationCenter r2 = org.telegram.messenger.NotificationCenter.getInstance(r2)
            int r3 = org.telegram.messenger.NotificationCenter.messagePlayingDidStart
            r2.addObserver(r0, r3)
            int r2 = r0.currentAccount
            org.telegram.messenger.NotificationCenter r2 = org.telegram.messenger.NotificationCenter.getInstance(r2)
            int r3 = org.telegram.messenger.NotificationCenter.messagePlayingProgressDidChanged
            r2.addObserver(r0, r3)
            int r2 = r0.currentAccount
            org.telegram.messenger.NotificationCenter r2 = org.telegram.messenger.NotificationCenter.getInstance(r2)
            int r3 = org.telegram.messenger.NotificationCenter.fileLoaded
            r2.addObserver(r0, r3)
            int r2 = r0.currentAccount
            org.telegram.messenger.NotificationCenter r2 = org.telegram.messenger.NotificationCenter.getInstance(r2)
            int r3 = org.telegram.messenger.NotificationCenter.fileLoadProgressChanged
            r2.addObserver(r0, r3)
            int r2 = r0.currentAccount
            org.telegram.messenger.NotificationCenter r2 = org.telegram.messenger.NotificationCenter.getInstance(r2)
            int r3 = org.telegram.messenger.NotificationCenter.musicDidLoad
            r2.addObserver(r0, r3)
            int r2 = r0.currentAccount
            org.telegram.messenger.NotificationCenter r2 = org.telegram.messenger.NotificationCenter.getInstance(r2)
            int r3 = org.telegram.messenger.NotificationCenter.moreMusicDidLoad
            r2.addObserver(r0, r3)
            org.telegram.ui.Components.AudioPlayerAlert$2 r2 = new org.telegram.ui.Components.AudioPlayerAlert$2
            r2.<init>(r8)
            r0.containerView = r2
            r15 = 0
            r2.setWillNotDraw(r15)
            android.view.ViewGroup r2 = r0.containerView
            int r3 = r0.backgroundPaddingLeft
            r2.setPadding(r3, r15, r3, r15)
            org.telegram.ui.Components.AudioPlayerAlert$3 r2 = new org.telegram.ui.Components.AudioPlayerAlert$3
            r2.<init>(r8, r9)
            r0.actionBar = r2
            java.lang.String r3 = "player_actionBar"
            int r3 = r0.getThemedColor(r3)
            r2.setBackgroundColor(r3)
            org.telegram.ui.ActionBar.ActionBar r2 = r0.actionBar
            r3 = 2131165491(0x7var_, float:1.79452E38)
            r2.setBackButtonImage(r3)
            org.telegram.ui.ActionBar.ActionBar r2 = r0.actionBar
            java.lang.String r3 = "player_actionBarTitle"
            int r4 = r0.getThemedColor(r3)
            r2.setItemsColor(r4, r15)
            org.telegram.ui.ActionBar.ActionBar r2 = r0.actionBar
            java.lang.String r4 = "player_actionBarSelector"
            int r4 = r0.getThemedColor(r4)
            r2.setItemsBackgroundColor(r4, r15)
            org.telegram.ui.ActionBar.ActionBar r2 = r0.actionBar
            int r4 = r0.getThemedColor(r3)
            r2.setTitleColor(r4)
            org.telegram.ui.ActionBar.ActionBar r2 = r0.actionBar
            r4 = 2131624429(0x7f0e01ed, float:1.8876037E38)
            java.lang.String r5 = "AttachMusic"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r2.setTitle(r4)
            org.telegram.ui.ActionBar.ActionBar r2 = r0.actionBar
            java.lang.String r4 = "player_actionBarSubtitle"
            int r4 = r0.getThemedColor(r4)
            r2.setSubtitleColor(r4)
            org.telegram.ui.ActionBar.ActionBar r2 = r0.actionBar
            r2.setOccupyStatusBar(r15)
            org.telegram.ui.ActionBar.ActionBar r2 = r0.actionBar
            r7 = 0
            r2.setAlpha(r7)
            if (r1 == 0) goto L_0x0198
            org.telegram.messenger.MediaController r2 = org.telegram.messenger.MediaController.getInstance()
            boolean r2 = r2.currentPlaylistIsGlobalSearch()
            if (r2 != 0) goto L_0x0198
            long r1 = r1.getDialogId()
            boolean r4 = org.telegram.messenger.DialogObject.isEncryptedDialog(r1)
            if (r4 == 0) goto L_0x015c
            int r4 = r0.currentAccount
            org.telegram.messenger.MessagesController r4 = org.telegram.messenger.MessagesController.getInstance(r4)
            int r1 = org.telegram.messenger.DialogObject.getEncryptedChatId(r1)
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            org.telegram.tgnet.TLRPC$EncryptedChat r1 = r4.getEncryptedChat(r1)
            if (r1 == 0) goto L_0x0198
            int r2 = r0.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            long r4 = r1.user_id
            java.lang.Long r1 = java.lang.Long.valueOf(r4)
            org.telegram.tgnet.TLRPC$User r1 = r2.getUser(r1)
            if (r1 == 0) goto L_0x0198
            org.telegram.ui.ActionBar.ActionBar r2 = r0.actionBar
            java.lang.String r4 = r1.first_name
            java.lang.String r1 = r1.last_name
            java.lang.String r1 = org.telegram.messenger.ContactsController.formatName(r4, r1)
            r2.setTitle(r1)
            goto L_0x0198
        L_0x015c:
            boolean r4 = org.telegram.messenger.DialogObject.isUserDialog(r1)
            if (r4 == 0) goto L_0x0180
            int r4 = r0.currentAccount
            org.telegram.messenger.MessagesController r4 = org.telegram.messenger.MessagesController.getInstance(r4)
            java.lang.Long r1 = java.lang.Long.valueOf(r1)
            org.telegram.tgnet.TLRPC$User r1 = r4.getUser(r1)
            if (r1 == 0) goto L_0x0198
            org.telegram.ui.ActionBar.ActionBar r2 = r0.actionBar
            java.lang.String r4 = r1.first_name
            java.lang.String r1 = r1.last_name
            java.lang.String r1 = org.telegram.messenger.ContactsController.formatName(r4, r1)
            r2.setTitle(r1)
            goto L_0x0198
        L_0x0180:
            int r4 = r0.currentAccount
            org.telegram.messenger.MessagesController r4 = org.telegram.messenger.MessagesController.getInstance(r4)
            long r1 = -r1
            java.lang.Long r1 = java.lang.Long.valueOf(r1)
            org.telegram.tgnet.TLRPC$Chat r1 = r4.getChat(r1)
            if (r1 == 0) goto L_0x0198
            org.telegram.ui.ActionBar.ActionBar r2 = r0.actionBar
            java.lang.String r1 = r1.title
            r2.setTitle(r1)
        L_0x0198:
            org.telegram.ui.ActionBar.ActionBar r1 = r0.actionBar
            org.telegram.ui.ActionBar.ActionBarMenu r1 = r1.createMenu()
            r2 = 2131165501(0x7var_d, float:1.794522E38)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r1.addItem((int) r15, (int) r2)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r1.setIsSearchField(r10)
            org.telegram.ui.Components.AudioPlayerAlert$4 r2 = new org.telegram.ui.Components.AudioPlayerAlert$4
            r2.<init>()
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r1.setActionBarMenuItemSearchListener(r2)
            r0.searchItem = r1
            java.lang.String r2 = "Search"
            r4 = 2131627789(0x7f0e0f0d, float:1.8882852E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r2, r4)
            r1.setContentDescription(r5)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.searchItem
            org.telegram.ui.Components.EditTextBoldCursor r1 = r1.getSearchField()
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r4)
            r1.setHint(r2)
            int r2 = r0.getThemedColor(r3)
            r1.setTextColor(r2)
            java.lang.String r2 = "player_time"
            int r4 = r0.getThemedColor(r2)
            r1.setHintTextColor(r4)
            int r3 = r0.getThemedColor(r3)
            r1.setCursorColor(r3)
            org.telegram.ui.ActionBar.ActionBar r1 = r0.actionBar
            org.telegram.ui.Components.AudioPlayerAlert$5 r3 = new org.telegram.ui.Components.AudioPlayerAlert$5
            r3.<init>()
            r1.setActionBarMenuOnItemClick(r3)
            android.view.View r1 = new android.view.View
            r1.<init>(r8)
            r0.actionBarShadow = r1
            r1.setAlpha(r7)
            android.view.View r1 = r0.actionBarShadow
            r3 = 2131165488(0x7var_, float:1.7945195E38)
            r1.setBackgroundResource(r3)
            android.view.View r1 = new android.view.View
            r1.<init>(r8)
            r0.playerShadow = r1
            java.lang.String r3 = "dialogShadowLine"
            int r3 = r0.getThemedColor(r3)
            r1.setBackgroundColor(r3)
            org.telegram.ui.Components.AudioPlayerAlert$6 r1 = new org.telegram.ui.Components.AudioPlayerAlert$6
            r1.<init>(r8)
            r0.playerLayout = r1
            org.telegram.ui.Components.AudioPlayerAlert$7 r1 = new org.telegram.ui.Components.AudioPlayerAlert$7
            r1.<init>(r8)
            r0.coverContainer = r1
            android.widget.FrameLayout r3 = r0.playerLayout
            r16 = 44
            r17 = 1110441984(0x42300000, float:44.0)
            r18 = 53
            r19 = 0
            r20 = 1101004800(0x41a00000, float:20.0)
            r21 = 1101004800(0x41a00000, float:20.0)
            r22 = 0
            android.widget.FrameLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r16, r17, r18, r19, r20, r21, r22)
            r3.addView(r1, r4)
            org.telegram.ui.Components.AudioPlayerAlert$8 r1 = new org.telegram.ui.Components.AudioPlayerAlert$8
            r1.<init>(r8, r8)
            r0.titleTextView = r1
            android.widget.FrameLayout r3 = r0.playerLayout
            r16 = -1
            r17 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r18 = 51
            r19 = 1101004800(0x41a00000, float:20.0)
            r21 = 1116733440(0x42900000, float:72.0)
            android.widget.FrameLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r16, r17, r18, r19, r20, r21, r22)
            r3.addView(r1, r4)
            org.telegram.ui.Components.AudioPlayerAlert$9 r1 = new org.telegram.ui.Components.AudioPlayerAlert$9
            r1.<init>(r8, r8)
            r0.authorTextView = r1
            android.widget.FrameLayout r3 = r0.playerLayout
            r19 = 1096810496(0x41600000, float:14.0)
            r20 = 1111228416(0x423CLASSNAME, float:47.0)
            android.widget.FrameLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r16, r17, r18, r19, r20, r21, r22)
            r3.addView(r1, r4)
            org.telegram.ui.Components.AudioPlayerAlert$10 r1 = new org.telegram.ui.Components.AudioPlayerAlert$10
            r1.<init>(r8, r9)
            r0.seekBarView = r1
            org.telegram.ui.Components.AudioPlayerAlert$11 r3 = new org.telegram.ui.Components.AudioPlayerAlert$11
            r3.<init>()
            r1.setDelegate(r3)
            org.telegram.ui.Components.SeekBarView r1 = r0.seekBarView
            r1.setReportChanges(r10)
            android.widget.FrameLayout r1 = r0.playerLayout
            org.telegram.ui.Components.SeekBarView r3 = r0.seekBarView
            r17 = 1108869120(0x42180000, float:38.0)
            r19 = 1084227584(0x40a00000, float:5.0)
            r20 = 1116471296(0x428CLASSNAME, float:70.0)
            r21 = 1084227584(0x40a00000, float:5.0)
            android.widget.FrameLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r16, r17, r18, r19, r20, r21, r22)
            r1.addView(r3, r4)
            org.telegram.ui.Components.LineProgressView r1 = new org.telegram.ui.Components.LineProgressView
            r1.<init>(r8)
            r0.progressView = r1
            r1.setVisibility(r11)
            org.telegram.ui.Components.LineProgressView r1 = r0.progressView
            java.lang.String r3 = "player_progressBackground"
            int r3 = r0.getThemedColor(r3)
            r1.setBackgroundColor(r3)
            org.telegram.ui.Components.LineProgressView r1 = r0.progressView
            java.lang.String r3 = "player_progress"
            int r3 = r0.getThemedColor(r3)
            r1.setProgressColor(r3)
            android.widget.FrameLayout r1 = r0.playerLayout
            org.telegram.ui.Components.LineProgressView r3 = r0.progressView
            r17 = 1073741824(0x40000000, float:2.0)
            r19 = 1101529088(0x41a80000, float:21.0)
            r20 = 1119092736(0x42b40000, float:90.0)
            r21 = 1101529088(0x41a80000, float:21.0)
            android.widget.FrameLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r16, r17, r18, r19, r20, r21, r22)
            r1.addView(r3, r4)
            org.telegram.ui.ActionBar.SimpleTextView r1 = new org.telegram.ui.ActionBar.SimpleTextView
            r1.<init>(r8)
            r0.timeTextView = r1
            r3 = 12
            r1.setTextSize(r3)
            org.telegram.ui.ActionBar.SimpleTextView r1 = r0.timeTextView
            java.lang.String r3 = "0:00"
            r1.setText(r3)
            org.telegram.ui.ActionBar.SimpleTextView r1 = r0.timeTextView
            int r3 = r0.getThemedColor(r2)
            r1.setTextColor(r3)
            org.telegram.ui.ActionBar.SimpleTextView r1 = r0.timeTextView
            r6 = 2
            r1.setImportantForAccessibility(r6)
            android.widget.FrameLayout r1 = r0.playerLayout
            org.telegram.ui.ActionBar.SimpleTextView r3 = r0.timeTextView
            r16 = 100
            r17 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r19 = 1101004800(0x41a00000, float:20.0)
            r20 = 1120141312(0x42CLASSNAME, float:98.0)
            r21 = 0
            android.widget.FrameLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r16, r17, r18, r19, r20, r21, r22)
            r1.addView(r3, r4)
            android.widget.TextView r1 = new android.widget.TextView
            r1.<init>(r8)
            r0.durationTextView = r1
            r3 = 1094713344(0x41400000, float:12.0)
            r1.setTextSize(r10, r3)
            android.widget.TextView r1 = r0.durationTextView
            int r2 = r0.getThemedColor(r2)
            r1.setTextColor(r2)
            android.widget.TextView r1 = r0.durationTextView
            r5 = 17
            r1.setGravity(r5)
            android.widget.TextView r1 = r0.durationTextView
            r1.setImportantForAccessibility(r6)
            android.widget.FrameLayout r1 = r0.playerLayout
            android.widget.TextView r2 = r0.durationTextView
            r16 = -2
            r18 = 53
            r19 = 0
            r20 = 1119879168(0x42CLASSNAME, float:96.0)
            r21 = 1101004800(0x41a00000, float:20.0)
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r16, r17, r18, r19, r20, r21, r22)
            r1.addView(r2, r3)
            org.telegram.ui.ActionBar.ActionBarMenuItem r4 = new org.telegram.ui.ActionBar.ActionBarMenuItem
            r3 = 0
            r16 = 0
            java.lang.String r1 = "dialogTextBlack"
            int r17 = r0.getThemedColor(r1)
            r18 = 0
            r1 = r4
            r2 = r31
            r13 = r4
            r4 = r16
            r14 = 17
            r5 = r17
            r14 = 2
            r6 = r18
            r7 = r32
            r1.<init>(r2, r3, r4, r5, r6, r7)
            r0.playbackSpeedButton = r13
            r13.setLongClickEnabled(r15)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.playbackSpeedButton
            r1.setShowSubmenuByMove(r15)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.playbackSpeedButton
            r2 = 1130364928(0x43600000, float:224.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r2 = -r2
            r1.setAdditionalYOffset(r2)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.playbackSpeedButton
            r2 = 2131624013(0x7f0e004d, float:1.8875194E38)
            java.lang.String r3 = "AccDescrPlayerSpeed"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setContentDescription(r2)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.playbackSpeedButton
            org.telegram.ui.Components.AudioPlayerAlert$$ExternalSyntheticLambda8 r2 = new org.telegram.ui.Components.AudioPlayerAlert$$ExternalSyntheticLambda8
            r2.<init>(r0)
            r1.setDelegate(r2)
            org.telegram.ui.ActionBar.ActionBarMenuSubItem[] r1 = r0.speedItems
            org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r0.playbackSpeedButton
            r3 = 2131165877(0x7var_b5, float:1.7945984E38)
            r4 = 2131628086(0x7f0e1036, float:1.8883455E38)
            java.lang.String r5 = "SpeedSlow"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r2 = r2.addSubItem(r10, r3, r4)
            r1[r15] = r2
            org.telegram.ui.ActionBar.ActionBarMenuSubItem[] r1 = r0.speedItems
            org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r0.playbackSpeedButton
            r3 = 2131165878(0x7var_b6, float:1.7945986E38)
            r4 = 2131628085(0x7f0e1035, float:1.8883453E38)
            java.lang.String r5 = "SpeedNormal"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r2 = r2.addSubItem(r14, r3, r4)
            r1[r10] = r2
            org.telegram.ui.ActionBar.ActionBarMenuSubItem[] r1 = r0.speedItems
            org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r0.playbackSpeedButton
            r3 = 2131165879(0x7var_b7, float:1.7945988E38)
            r4 = 2131628084(0x7f0e1034, float:1.888345E38)
            java.lang.String r5 = "SpeedFast"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r13 = 3
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r2 = r2.addSubItem(r13, r3, r4)
            r1[r14] = r2
            org.telegram.ui.ActionBar.ActionBarMenuSubItem[] r1 = r0.speedItems
            org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r0.playbackSpeedButton
            r3 = 2131165880(0x7var_b8, float:1.794599E38)
            r4 = 2131628087(0x7f0e1037, float:1.8883457E38)
            java.lang.String r5 = "SpeedVeryFast"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r2 = r2.addSubItem(r11, r3, r4)
            r1[r13] = r2
            float r1 = org.telegram.messenger.AndroidUtilities.density
            r7 = 1077936128(0x40400000, float:3.0)
            int r1 = (r1 > r7 ? 1 : (r1 == r7 ? 0 : -1))
            if (r1 < 0) goto L_0x03dd
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.playbackSpeedButton
            r1.setPadding(r15, r10, r15, r15)
        L_0x03dd:
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.playbackSpeedButton
            r18 = 1090519040(0x41000000, float:8.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r18)
            r1.setAdditionalXOffset(r2)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.playbackSpeedButton
            r1.setShowedFromBottom(r10)
            android.widget.FrameLayout r1 = r0.playerLayout
            org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r0.playbackSpeedButton
            r23 = 36
            r24 = 1108344832(0x42100000, float:36.0)
            r25 = 53
            r26 = 0
            r27 = 1118568448(0x42aCLASSNAME, float:86.0)
            r28 = 1101004800(0x41a00000, float:20.0)
            r29 = 0
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r23, r24, r25, r26, r27, r28, r29)
            r1.addView(r2, r3)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.playbackSpeedButton
            org.telegram.ui.Components.AudioPlayerAlert$$ExternalSyntheticLambda0 r2 = new org.telegram.ui.Components.AudioPlayerAlert$$ExternalSyntheticLambda0
            r2.<init>(r0)
            r1.setOnClickListener(r2)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.playbackSpeedButton
            org.telegram.ui.Components.AudioPlayerAlert$$ExternalSyntheticLambda4 r2 = new org.telegram.ui.Components.AudioPlayerAlert$$ExternalSyntheticLambda4
            r2.<init>(r0)
            r1.setOnLongClickListener(r2)
            r30.updatePlaybackButton()
            org.telegram.ui.Components.AudioPlayerAlert$12 r6 = new org.telegram.ui.Components.AudioPlayerAlert$12
            r6.<init>(r8)
            android.widget.FrameLayout r1 = r0.playerLayout
            r23 = -1
            r24 = 1115947008(0x42840000, float:66.0)
            r25 = 51
            r27 = 1121845248(0x42de0000, float:111.0)
            r28 = 0
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r23, r24, r25, r26, r27, r28, r29)
            r1.addView(r6, r2)
            android.view.View[] r5 = r0.buttons
            org.telegram.ui.ActionBar.ActionBarMenuItem r4 = new org.telegram.ui.ActionBar.ActionBarMenuItem
            r3 = 0
            r20 = 0
            r21 = 0
            r22 = 0
            r1 = r4
            r2 = r31
            r12 = r4
            r4 = r20
            r20 = r5
            r5 = r21
            r14 = r6
            r6 = r22
            r7 = r32
            r1.<init>(r2, r3, r4, r5, r6, r7)
            r0.repeatButton = r12
            r20[r15] = r12
            r12.setLongClickEnabled(r15)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.repeatButton
            r1.setShowSubmenuByMove(r15)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.repeatButton
            r2 = 1126563840(0x43260000, float:166.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r2 = -r2
            r1.setAdditionalYOffset(r2)
            int r12 = android.os.Build.VERSION.SDK_INT
            java.lang.String r7 = "listSelectorSDK21"
            r6 = 21
            if (r12 < r6) goto L_0x0485
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.repeatButton
            int r2 = r0.getThemedColor(r7)
            r3 = 1099956224(0x41900000, float:18.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r2, r10, r3)
            r1.setBackgroundDrawable(r2)
        L_0x0485:
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.repeatButton
            r5 = 48
            r4 = 51
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r5, r5, r4)
            r14.addView(r1, r2)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.repeatButton
            org.telegram.ui.Components.AudioPlayerAlert$$ExternalSyntheticLambda1 r2 = new org.telegram.ui.Components.AudioPlayerAlert$$ExternalSyntheticLambda1
            r2.<init>(r0)
            r1.setOnClickListener(r2)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.repeatButton
            r2 = 2131166015(0x7var_f, float:1.7946263E38)
            r3 = 2131627631(0x7f0e0e6f, float:1.8882532E38)
            java.lang.String r15 = "RepeatSong"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r15, r3)
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r1 = r1.addSubItem(r13, r2, r3)
            r0.repeatSongItem = r1
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.repeatButton
            r2 = 2131166014(0x7var_e, float:1.7946261E38)
            r3 = 2131627628(0x7f0e0e6c, float:1.8882526E38)
            java.lang.String r15 = "RepeatList"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r15, r3)
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r1 = r1.addSubItem(r11, r2, r3)
            r0.repeatListItem = r1
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.repeatButton
            r2 = 2131166016(0x7var_, float:1.7946265E38)
            r3 = 2131628048(0x7f0e1010, float:1.8883378E38)
            java.lang.String r15 = "ShuffleList"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r15, r3)
            r15 = 2
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r1 = r1.addSubItem(r15, r2, r3)
            r0.shuffleListItem = r1
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.repeatButton
            r2 = 2131166008(0x7var_, float:1.794625E38)
            r3 = 2131627750(0x7f0e0ee6, float:1.8882773E38)
            java.lang.String r15 = "ReverseOrder"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r15, r3)
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r1 = r1.addSubItem(r10, r2, r3)
            r0.reverseOrderItem = r1
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.repeatButton
            r1.setShowedFromBottom(r10)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.repeatButton
            org.telegram.ui.Components.AudioPlayerAlert$$ExternalSyntheticLambda9 r2 = new org.telegram.ui.Components.AudioPlayerAlert$$ExternalSyntheticLambda9
            r2.<init>(r0)
            r1.setDelegate(r2)
            java.lang.String r1 = "player_button"
            int r15 = r0.getThemedColor(r1)
            android.view.ViewConfiguration r1 = android.view.ViewConfiguration.get(r31)
            int r1 = r1.getScaledTouchSlop()
            float r1 = (float) r1
            android.view.View[] r2 = r0.buttons
            org.telegram.ui.Components.AudioPlayerAlert$13 r3 = new org.telegram.ui.Components.AudioPlayerAlert$13
            r3.<init>(r8, r1)
            r0.prevButton = r3
            r2[r10] = r3
            android.widget.ImageView$ScaleType r2 = android.widget.ImageView.ScaleType.CENTER
            r3.setScaleType(r2)
            org.telegram.ui.Components.RLottieImageView r2 = r0.prevButton
            r3 = 2131558497(0x7f0d0061, float:1.8742311E38)
            r11 = 20
            r2.setAnimation(r3, r11, r11)
            org.telegram.ui.Components.RLottieImageView r2 = r0.prevButton
            java.lang.String r3 = "Triangle 3.**"
            r2.setLayerColor(r3, r15)
            org.telegram.ui.Components.RLottieImageView r2 = r0.prevButton
            java.lang.String r3 = "Triangle 4.**"
            r2.setLayerColor(r3, r15)
            org.telegram.ui.Components.RLottieImageView r2 = r0.prevButton
            java.lang.String r3 = "Rectangle 4.**"
            r2.setLayerColor(r3, r15)
            if (r12 < r6) goto L_0x054f
            org.telegram.ui.Components.RLottieImageView r2 = r0.prevButton
            int r3 = r0.getThemedColor(r7)
            r24 = 1102053376(0x41b00000, float:22.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r24)
            android.graphics.drawable.Drawable r3 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r3, r10, r11)
            r2.setBackgroundDrawable(r3)
        L_0x054f:
            org.telegram.ui.Components.RLottieImageView r2 = r0.prevButton
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r5, r5, r4)
            r14.addView(r2, r3)
            org.telegram.ui.Components.RLottieImageView r2 = r0.prevButton
            r3 = 2131624014(0x7f0e004e, float:1.8875196E38)
            java.lang.String r11 = "AccDescrPrevious"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r11, r3)
            r2.setContentDescription(r3)
            android.view.View[] r2 = r0.buttons
            android.widget.ImageView r3 = new android.widget.ImageView
            r3.<init>(r8)
            r0.playButton = r3
            r11 = 2
            r2[r11] = r3
            android.widget.ImageView$ScaleType r2 = android.widget.ImageView.ScaleType.CENTER
            r3.setScaleType(r2)
            android.widget.ImageView r2 = r0.playButton
            org.telegram.ui.Components.PlayPauseDrawable r3 = new org.telegram.ui.Components.PlayPauseDrawable
            r11 = 28
            r3.<init>(r11)
            r0.playPauseDrawable = r3
            r2.setImageDrawable(r3)
            org.telegram.ui.Components.PlayPauseDrawable r2 = r0.playPauseDrawable
            org.telegram.messenger.MediaController r3 = org.telegram.messenger.MediaController.getInstance()
            boolean r3 = r3.isMessagePaused()
            r3 = r3 ^ r10
            r11 = 0
            r2.setPause(r3, r11)
            android.widget.ImageView r2 = r0.playButton
            android.graphics.PorterDuffColorFilter r3 = new android.graphics.PorterDuffColorFilter
            java.lang.String r11 = "player_button"
            int r11 = r0.getThemedColor(r11)
            android.graphics.PorterDuff$Mode r13 = android.graphics.PorterDuff.Mode.MULTIPLY
            r3.<init>(r11, r13)
            r2.setColorFilter(r3)
            if (r12 < r6) goto L_0x05bb
            android.widget.ImageView r2 = r0.playButton
            int r3 = r0.getThemedColor(r7)
            r11 = 1103101952(0x41CLASSNAME, float:24.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            android.graphics.drawable.Drawable r3 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r3, r10, r11)
            r2.setBackgroundDrawable(r3)
        L_0x05bb:
            android.widget.ImageView r2 = r0.playButton
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r5, r5, r4)
            r14.addView(r2, r3)
            android.widget.ImageView r2 = r0.playButton
            org.telegram.ui.Components.AudioPlayerAlert$$ExternalSyntheticLambda3 r3 = org.telegram.ui.Components.AudioPlayerAlert$$ExternalSyntheticLambda3.INSTANCE
            r2.setOnClickListener(r3)
            android.view.View[] r2 = r0.buttons
            org.telegram.ui.Components.AudioPlayerAlert$14 r3 = new org.telegram.ui.Components.AudioPlayerAlert$14
            r3.<init>(r8, r1)
            r0.nextButton = r3
            r1 = 3
            r2[r1] = r3
            android.widget.ImageView$ScaleType r1 = android.widget.ImageView.ScaleType.CENTER
            r3.setScaleType(r1)
            org.telegram.ui.Components.RLottieImageView r1 = r0.nextButton
            r2 = 2131558497(0x7f0d0061, float:1.8742311E38)
            r3 = 20
            r1.setAnimation(r2, r3, r3)
            org.telegram.ui.Components.RLottieImageView r1 = r0.nextButton
            java.lang.String r2 = "Triangle 3.**"
            r1.setLayerColor(r2, r15)
            org.telegram.ui.Components.RLottieImageView r1 = r0.nextButton
            java.lang.String r2 = "Triangle 4.**"
            r1.setLayerColor(r2, r15)
            org.telegram.ui.Components.RLottieImageView r1 = r0.nextButton
            java.lang.String r2 = "Rectangle 4.**"
            r1.setLayerColor(r2, r15)
            org.telegram.ui.Components.RLottieImageView r1 = r0.nextButton
            r2 = 1127481344(0x43340000, float:180.0)
            r1.setRotation(r2)
            if (r12 < r6) goto L_0x0617
            org.telegram.ui.Components.RLottieImageView r1 = r0.nextButton
            int r2 = r0.getThemedColor(r7)
            r3 = 1102053376(0x41b00000, float:22.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r2, r10, r3)
            r1.setBackgroundDrawable(r2)
        L_0x0617:
            org.telegram.ui.Components.RLottieImageView r1 = r0.nextButton
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r5, r5, r4)
            r14.addView(r1, r2)
            org.telegram.ui.Components.RLottieImageView r1 = r0.nextButton
            r2 = 2131626564(0x7f0e0a44, float:1.8880368E38)
            java.lang.String r3 = "Next"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setContentDescription(r2)
            android.view.View[] r11 = r0.buttons
            org.telegram.ui.ActionBar.ActionBarMenuItem r13 = new org.telegram.ui.ActionBar.ActionBarMenuItem
            r3 = 0
            r24 = 0
            r25 = 0
            r1 = r13
            r2 = r31
            r4 = r24
            r5 = r15
            r15 = 21
            r6 = r25
            r10 = r7
            r7 = r32
            r1.<init>(r2, r3, r4, r5, r6, r7)
            r0.optionsButton = r13
            r1 = 4
            r11[r1] = r13
            r1 = 0
            r13.setLongClickEnabled(r1)
            org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r0.optionsButton
            r2.setShowSubmenuByMove(r1)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.optionsButton
            r2 = 2131165498(0x7var_a, float:1.7945215E38)
            r1.setIcon((int) r2)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.optionsButton
            r2 = 2
            r1.setSubMenuOpenSide(r2)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.optionsButton
            r2 = 1125974016(0x431d0000, float:157.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r2 = -r2
            r1.setAdditionalYOffset(r2)
            if (r12 < r15) goto L_0x0685
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.optionsButton
            int r2 = r0.getThemedColor(r10)
            r3 = 1099956224(0x41900000, float:18.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r4 = 1
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r2, r4, r3)
            r1.setBackgroundDrawable(r2)
        L_0x0685:
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.optionsButton
            r2 = 51
            r3 = 48
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r3, r3, r2)
            r14.addView(r1, r3)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.optionsButton
            r3 = 2131165776(0x7var_, float:1.7945779E38)
            r4 = 2131625764(0x7f0e0724, float:1.8878745E38)
            java.lang.String r5 = "Forward"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r5 = 1
            r1.addSubItem(r5, r3, r4)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.optionsButton
            r3 = 2131165874(0x7var_b2, float:1.7945977E38)
            r4 = 2131627969(0x7f0e0fc1, float:1.8883217E38)
            java.lang.String r5 = "ShareFile"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r5 = 2
            r1.addSubItem(r5, r3, r4)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.optionsButton
            r3 = 2131165768(0x7var_, float:1.7945762E38)
            r4 = 2131627773(0x7f0e0efd, float:1.888282E38)
            java.lang.String r5 = "SaveToMusic"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r5 = 5
            r1.addSubItem(r5, r3, r4)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.optionsButton
            r3 = 2131165808(0x7var_, float:1.7945844E38)
            r4 = 2131628033(0x7f0e1001, float:1.8883347E38)
            java.lang.String r5 = "ShowInChat"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r5 = 4
            r1.addSubItem(r5, r3, r4)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.optionsButton
            r3 = 1
            r1.setShowedFromBottom(r3)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.optionsButton
            org.telegram.ui.Components.AudioPlayerAlert$$ExternalSyntheticLambda2 r3 = new org.telegram.ui.Components.AudioPlayerAlert$$ExternalSyntheticLambda2
            r3.<init>(r0)
            r1.setOnClickListener(r3)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.optionsButton
            org.telegram.ui.Components.AudioPlayerAlert$$ExternalSyntheticLambda7 r3 = new org.telegram.ui.Components.AudioPlayerAlert$$ExternalSyntheticLambda7
            r3.<init>(r0)
            r1.setDelegate(r3)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.optionsButton
            r3 = 2131623987(0x7f0e0033, float:1.887514E38)
            java.lang.String r4 = "AccDescrMoreOptions"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r1.setContentDescription(r3)
            android.widget.LinearLayout r1 = new android.widget.LinearLayout
            r1.<init>(r8)
            r0.emptyView = r1
            r3 = 1
            r1.setOrientation(r3)
            android.widget.LinearLayout r1 = r0.emptyView
            r3 = 17
            r1.setGravity(r3)
            android.widget.LinearLayout r1 = r0.emptyView
            r3 = 8
            r1.setVisibility(r3)
            android.view.ViewGroup r1 = r0.containerView
            android.widget.LinearLayout r3 = r0.emptyView
            r4 = -1082130432(0xffffffffbvar_, float:-1.0)
            r5 = -1
            android.widget.FrameLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r5, r4)
            r1.addView(r3, r4)
            android.widget.LinearLayout r1 = r0.emptyView
            org.telegram.ui.Components.AudioPlayerAlert$$ExternalSyntheticLambda5 r3 = org.telegram.ui.Components.AudioPlayerAlert$$ExternalSyntheticLambda5.INSTANCE
            r1.setOnTouchListener(r3)
            android.widget.ImageView r1 = new android.widget.ImageView
            r1.<init>(r8)
            r0.emptyImageView = r1
            r3 = 2131165918(0x7var_de, float:1.7946067E38)
            r1.setImageResource(r3)
            android.widget.ImageView r1 = r0.emptyImageView
            android.graphics.PorterDuffColorFilter r3 = new android.graphics.PorterDuffColorFilter
            java.lang.String r4 = "dialogEmptyImage"
            int r4 = r0.getThemedColor(r4)
            android.graphics.PorterDuff$Mode r5 = android.graphics.PorterDuff.Mode.MULTIPLY
            r3.<init>(r4, r5)
            r1.setColorFilter(r3)
            android.widget.LinearLayout r1 = r0.emptyView
            android.widget.ImageView r3 = r0.emptyImageView
            r4 = -2
            r5 = -2
            android.widget.LinearLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createLinear(r4, r5)
            r1.addView(r3, r4)
            android.widget.TextView r1 = new android.widget.TextView
            r1.<init>(r8)
            r0.emptyTitleTextView = r1
            java.lang.String r3 = "dialogEmptyText"
            int r3 = r0.getThemedColor(r3)
            r1.setTextColor(r3)
            android.widget.TextView r1 = r0.emptyTitleTextView
            r3 = 17
            r1.setGravity(r3)
            android.widget.TextView r1 = r0.emptyTitleTextView
            r3 = 2131626567(0x7f0e0a47, float:1.8880374E38)
            java.lang.String r4 = "NoAudioFound"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r1.setText(r3)
            android.widget.TextView r1 = r0.emptyTitleTextView
            java.lang.String r3 = "fonts/rmedium.ttf"
            android.graphics.Typeface r3 = org.telegram.messenger.AndroidUtilities.getTypeface(r3)
            r1.setTypeface(r3)
            android.widget.TextView r1 = r0.emptyTitleTextView
            r3 = 1099431936(0x41880000, float:17.0)
            r4 = 1
            r1.setTextSize(r4, r3)
            android.widget.TextView r1 = r0.emptyTitleTextView
            r3 = 1109393408(0x42200000, float:40.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r6 = 0
            r1.setPadding(r4, r6, r5, r6)
            android.widget.LinearLayout r1 = r0.emptyView
            android.widget.TextView r4 = r0.emptyTitleTextView
            r9 = -2
            r10 = -2
            r11 = 17
            r12 = 0
            r13 = 11
            r14 = 0
            r15 = 0
            android.widget.LinearLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r9, (int) r10, (int) r11, (int) r12, (int) r13, (int) r14, (int) r15)
            r1.addView(r4, r5)
            android.widget.TextView r1 = new android.widget.TextView
            r1.<init>(r8)
            r0.emptySubtitleTextView = r1
            java.lang.String r4 = "dialogEmptyText"
            int r4 = r0.getThemedColor(r4)
            r1.setTextColor(r4)
            android.widget.TextView r1 = r0.emptySubtitleTextView
            r4 = 17
            r1.setGravity(r4)
            android.widget.TextView r1 = r0.emptySubtitleTextView
            r4 = 1097859072(0x41700000, float:15.0)
            r5 = 1
            r1.setTextSize(r5, r4)
            android.widget.TextView r1 = r0.emptySubtitleTextView
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r5 = 0
            r1.setPadding(r4, r5, r3, r5)
            android.widget.LinearLayout r1 = r0.emptyView
            android.widget.TextView r3 = r0.emptySubtitleTextView
            r13 = 6
            android.widget.LinearLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r9, (int) r10, (int) r11, (int) r12, (int) r13, (int) r14, (int) r15)
            r1.addView(r3, r4)
            org.telegram.ui.Components.AudioPlayerAlert$15 r1 = new org.telegram.ui.Components.AudioPlayerAlert$15
            r1.<init>(r8)
            r0.listView = r1
            r3 = 0
            r1.setClipToPadding(r3)
            org.telegram.ui.Components.RecyclerListView r1 = r0.listView
            androidx.recyclerview.widget.LinearLayoutManager r4 = new androidx.recyclerview.widget.LinearLayoutManager
            android.content.Context r5 = r30.getContext()
            r6 = 1
            r4.<init>(r5, r6, r3)
            r0.layoutManager = r4
            r1.setLayoutManager(r4)
            org.telegram.ui.Components.RecyclerListView r1 = r0.listView
            r1.setHorizontalScrollBarEnabled(r3)
            org.telegram.ui.Components.RecyclerListView r1 = r0.listView
            r1.setVerticalScrollBarEnabled(r3)
            android.view.ViewGroup r1 = r0.containerView
            org.telegram.ui.Components.RecyclerListView r3 = r0.listView
            r4 = -1
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r4, r4, r2)
            r1.addView(r3, r2)
            org.telegram.ui.Components.RecyclerListView r1 = r0.listView
            org.telegram.ui.Components.AudioPlayerAlert$ListAdapter r2 = new org.telegram.ui.Components.AudioPlayerAlert$ListAdapter
            r2.<init>(r8)
            r0.listAdapter = r2
            r1.setAdapter(r2)
            org.telegram.ui.Components.RecyclerListView r1 = r0.listView
            java.lang.String r2 = "dialogScrollGlow"
            int r2 = r0.getThemedColor(r2)
            r1.setGlowColor(r2)
            org.telegram.ui.Components.RecyclerListView r1 = r0.listView
            org.telegram.ui.Components.AudioPlayerAlert$$ExternalSyntheticLambda11 r2 = org.telegram.ui.Components.AudioPlayerAlert$$ExternalSyntheticLambda11.INSTANCE
            r1.setOnItemClickListener((org.telegram.ui.Components.RecyclerListView.OnItemClickListener) r2)
            org.telegram.ui.Components.RecyclerListView r1 = r0.listView
            org.telegram.ui.Components.AudioPlayerAlert$16 r2 = new org.telegram.ui.Components.AudioPlayerAlert$16
            r2.<init>()
            r1.setOnScrollListener(r2)
            org.telegram.messenger.MediaController r1 = org.telegram.messenger.MediaController.getInstance()
            java.util.ArrayList r1 = r1.getPlaylist()
            r0.playlist = r1
            org.telegram.ui.Components.AudioPlayerAlert$ListAdapter r1 = r0.listAdapter
            r1.notifyDataSetChanged()
            android.view.ViewGroup r1 = r0.containerView
            android.widget.FrameLayout r2 = r0.playerLayout
            r3 = 179(0xb3, float:2.51E-43)
            r4 = 83
            r5 = -1
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r5, r3, r4)
            r1.addView(r2, r3)
            android.view.ViewGroup r1 = r0.containerView
            android.view.View r2 = r0.playerShadow
            android.widget.FrameLayout$LayoutParams r3 = new android.widget.FrameLayout$LayoutParams
            int r4 = org.telegram.messenger.AndroidUtilities.getShadowHeight()
            r6 = 83
            r3.<init>(r5, r4, r6)
            r1.addView(r2, r3)
            android.view.View r1 = r0.playerShadow
            android.view.ViewGroup$LayoutParams r1 = r1.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r1 = (android.widget.FrameLayout.LayoutParams) r1
            r2 = 1127415808(0x43330000, float:179.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r1.bottomMargin = r2
            android.view.ViewGroup r1 = r0.containerView
            android.view.View r2 = r0.actionBarShadow
            r3 = 1077936128(0x40400000, float:3.0)
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r5, r3)
            r1.addView(r2, r3)
            android.view.ViewGroup r1 = r0.containerView
            org.telegram.ui.ActionBar.ActionBar r2 = r0.actionBar
            r1.addView(r2)
            org.telegram.ui.Components.AudioPlayerAlert$17 r1 = new org.telegram.ui.Components.AudioPlayerAlert$17
            r1.<init>(r8)
            r0.blurredView = r1
            r2 = 0
            r1.setAlpha(r2)
            android.widget.FrameLayout r1 = r0.blurredView
            r2 = 4
            r1.setVisibility(r2)
            android.widget.FrameLayout r1 = r30.getContainer()
            android.widget.FrameLayout r2 = r0.blurredView
            r1.addView(r2)
            org.telegram.ui.Components.BackupImageView r1 = new org.telegram.ui.Components.BackupImageView
            r1.<init>(r8)
            r0.bigAlbumConver = r1
            r2 = 1
            r1.setAspectFit(r2)
            org.telegram.ui.Components.BackupImageView r1 = r0.bigAlbumConver
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r18)
            r1.setRoundRadius(r2)
            org.telegram.ui.Components.BackupImageView r1 = r0.bigAlbumConver
            r2 = 1063675494(0x3var_, float:0.9)
            r1.setScaleX(r2)
            org.telegram.ui.Components.BackupImageView r1 = r0.bigAlbumConver
            r1.setScaleY(r2)
            android.widget.FrameLayout r1 = r0.blurredView
            org.telegram.ui.Components.BackupImageView r2 = r0.bigAlbumConver
            r3 = -1
            r4 = -1082130432(0xffffffffbvar_, float:-1.0)
            r5 = 51
            r6 = 1106247680(0x41var_, float:30.0)
            r7 = 1106247680(0x41var_, float:30.0)
            r8 = 1106247680(0x41var_, float:30.0)
            r9 = 1106247680(0x41var_, float:30.0)
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r3, r4, r5, r6, r7, r8, r9)
            r1.addView(r2, r3)
            r1 = 0
            r0.updateTitle(r1)
            r30.updateRepeatButton()
            r30.updateEmptyView()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.AudioPlayerAlert.<init>(android.content.Context, org.telegram.ui.ActionBar.Theme$ResourcesProvider):void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(int i) {
        MediaController.getInstance().getPlaybackSpeed(true);
        if (i == 1) {
            MediaController.getInstance().setPlaybackSpeed(true, 0.5f);
        } else if (i == 2) {
            MediaController.getInstance().setPlaybackSpeed(true, 1.0f);
        } else if (i == 3) {
            MediaController.getInstance().setPlaybackSpeed(true, 1.5f);
        } else {
            MediaController.getInstance().setPlaybackSpeed(true, 1.8f);
        }
        updatePlaybackButton();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$1(View view) {
        if (Math.abs(MediaController.getInstance().getPlaybackSpeed(true) - 1.0f) > 0.001f) {
            MediaController.getInstance().setPlaybackSpeed(true, 1.0f);
        } else {
            MediaController.getInstance().setPlaybackSpeed(true, MediaController.getInstance().getFastPlaybackSpeed(true));
        }
        updatePlaybackButton();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$new$2(View view) {
        this.playbackSpeedButton.toggleSubMenu();
        return true;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$3(View view) {
        updateSubMenu();
        this.repeatButton.toggleSubMenu();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$4(int i) {
        if (i == 1 || i == 2) {
            boolean z = SharedConfig.playOrderReversed;
            if ((!z || i != 1) && (!SharedConfig.shuffleMusic || i != 2)) {
                MediaController.getInstance().setPlaybackOrderType(i);
            } else {
                MediaController.getInstance().setPlaybackOrderType(0);
            }
            this.listAdapter.notifyDataSetChanged();
            if (z != SharedConfig.playOrderReversed) {
                this.listView.stopScroll();
                scrollToCurrentSong(false);
            }
        } else if (i == 4) {
            if (SharedConfig.repeatMode == 1) {
                SharedConfig.setRepeatMode(0);
            } else {
                SharedConfig.setRepeatMode(1);
            }
        } else if (SharedConfig.repeatMode == 2) {
            SharedConfig.setRepeatMode(0);
        } else {
            SharedConfig.setRepeatMode(2);
        }
        updateRepeatButton();
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$new$5(View view) {
        if (!MediaController.getInstance().isDownloadingCurrentMessage()) {
            if (MediaController.getInstance().isMessagePaused()) {
                MediaController.getInstance().playMessage(MediaController.getInstance().getPlayingMessageObject());
            } else {
                MediaController.getInstance().lambda$startAudioAgain$7(MediaController.getInstance().getPlayingMessageObject());
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$6(View view) {
        this.optionsButton.toggleSubMenu();
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$new$8(View view, int i) {
        if (view instanceof AudioPlayerCell) {
            ((AudioPlayerCell) view).didPressedButton();
        }
    }

    /* access modifiers changed from: private */
    public void startForwardRewindingSeek() {
        if (this.rewindingState == 1) {
            this.lastRewindingTime = System.currentTimeMillis();
            this.rewindingProgress = MediaController.getInstance().getPlayingMessageObject().audioProgress;
            AndroidUtilities.cancelRunOnUIThread(this.forwardSeek);
            AndroidUtilities.runOnUIThread(this.forwardSeek);
        }
    }

    /* access modifiers changed from: private */
    public void updateEmptyViewPosition() {
        if (this.emptyView.getVisibility() == 0) {
            int dp = this.playerLayout.getVisibility() == 0 ? AndroidUtilities.dp(150.0f) : -AndroidUtilities.dp(30.0f);
            LinearLayout linearLayout = this.emptyView;
            linearLayout.setTranslationY((float) (((linearLayout.getMeasuredHeight() - this.containerView.getMeasuredHeight()) - dp) / 2));
        }
    }

    /* access modifiers changed from: private */
    public void updateEmptyView() {
        this.emptyView.setVisibility((!this.searching || this.listAdapter.getItemCount() != 0) ? 8 : 0);
        updateEmptyViewPosition();
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:20:0x004a  */
    /* JADX WARNING: Removed duplicated region for block: B:21:0x0050  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean scrollToCurrentSong(boolean r7) {
        /*
            r6 = this;
            org.telegram.messenger.MediaController r0 = org.telegram.messenger.MediaController.getInstance()
            org.telegram.messenger.MessageObject r0 = r0.getPlayingMessageObject()
            r1 = 0
            if (r0 == 0) goto L_0x005d
            r2 = 1
            if (r7 == 0) goto L_0x003b
            org.telegram.ui.Components.RecyclerListView r7 = r6.listView
            int r7 = r7.getChildCount()
            r3 = 0
        L_0x0015:
            if (r3 >= r7) goto L_0x003b
            org.telegram.ui.Components.RecyclerListView r4 = r6.listView
            android.view.View r4 = r4.getChildAt(r3)
            boolean r5 = r4 instanceof org.telegram.ui.Cells.AudioPlayerCell
            if (r5 == 0) goto L_0x0038
            r5 = r4
            org.telegram.ui.Cells.AudioPlayerCell r5 = (org.telegram.ui.Cells.AudioPlayerCell) r5
            org.telegram.messenger.MessageObject r5 = r5.getMessageObject()
            if (r5 != r0) goto L_0x0038
            int r7 = r4.getBottom()
            org.telegram.ui.Components.RecyclerListView r3 = r6.listView
            int r3 = r3.getMeasuredHeight()
            if (r7 > r3) goto L_0x003b
            r7 = 1
            goto L_0x003c
        L_0x0038:
            int r3 = r3 + 1
            goto L_0x0015
        L_0x003b:
            r7 = 0
        L_0x003c:
            if (r7 != 0) goto L_0x005d
            java.util.ArrayList<org.telegram.messenger.MessageObject> r7 = r6.playlist
            int r7 = r7.indexOf(r0)
            if (r7 < 0) goto L_0x005d
            boolean r0 = org.telegram.messenger.SharedConfig.playOrderReversed
            if (r0 == 0) goto L_0x0050
            androidx.recyclerview.widget.LinearLayoutManager r0 = r6.layoutManager
            r0.scrollToPosition(r7)
            goto L_0x005c
        L_0x0050:
            androidx.recyclerview.widget.LinearLayoutManager r0 = r6.layoutManager
            java.util.ArrayList<org.telegram.messenger.MessageObject> r1 = r6.playlist
            int r1 = r1.size()
            int r1 = r1 - r7
            r0.scrollToPosition(r1)
        L_0x005c:
            return r2
        L_0x005d:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.AudioPlayerAlert.scrollToCurrentSong(boolean):boolean");
    }

    public boolean onCustomMeasure(View view, int i, int i2) {
        FrameLayout frameLayout = this.blurredView;
        if (view != frameLayout) {
            return false;
        }
        frameLayout.measure(View.MeasureSpec.makeMeasureSpec(i, NUM), View.MeasureSpec.makeMeasureSpec(i2, NUM));
        return true;
    }

    /* access modifiers changed from: protected */
    public boolean onCustomLayout(View view, int i, int i2, int i3, int i4) {
        int i5 = i3 - i;
        int i6 = i4 - i2;
        FrameLayout frameLayout = this.blurredView;
        if (view != frameLayout) {
            return false;
        }
        frameLayout.layout(i, 0, i5 + i, i6);
        return true;
    }

    private void setMenuItemChecked(ActionBarMenuSubItem actionBarMenuSubItem, boolean z) {
        if (z) {
            actionBarMenuSubItem.setTextColor(getThemedColor("player_buttonActive"));
            actionBarMenuSubItem.setIconColor(getThemedColor("player_buttonActive"));
            return;
        }
        actionBarMenuSubItem.setTextColor(getThemedColor("actionBarDefaultSubmenuItem"));
        actionBarMenuSubItem.setIconColor(getThemedColor("actionBarDefaultSubmenuItem"));
    }

    private void updateSubMenu() {
        setMenuItemChecked(this.shuffleListItem, SharedConfig.shuffleMusic);
        setMenuItemChecked(this.reverseOrderItem, SharedConfig.playOrderReversed);
        boolean z = false;
        setMenuItemChecked(this.repeatListItem, SharedConfig.repeatMode == 1);
        ActionBarMenuSubItem actionBarMenuSubItem = this.repeatSongItem;
        if (SharedConfig.repeatMode == 2) {
            z = true;
        }
        setMenuItemChecked(actionBarMenuSubItem, z);
    }

    private void updatePlaybackButton() {
        float playbackSpeed = MediaController.getInstance().getPlaybackSpeed(true);
        float f = playbackSpeed - 1.0f;
        String str = Math.abs(f) > 0.001f ? "inappPlayerPlayPause" : "inappPlayerClose";
        this.playbackSpeedButton.setTag(str);
        float fastPlaybackSpeed = MediaController.getInstance().getFastPlaybackSpeed(true);
        if (Math.abs(fastPlaybackSpeed - 1.8f) < 0.001f) {
            this.playbackSpeedButton.setIcon(NUM);
        } else if (Math.abs(fastPlaybackSpeed - 1.5f) < 0.001f) {
            this.playbackSpeedButton.setIcon(NUM);
        } else {
            this.playbackSpeedButton.setIcon(NUM);
        }
        this.playbackSpeedButton.setIconColor(getThemedColor(str));
        if (Build.VERSION.SDK_INT >= 21) {
            this.playbackSpeedButton.setBackgroundDrawable(Theme.createSelectorDrawable(getThemedColor(str) & NUM, 1, AndroidUtilities.dp(14.0f)));
        }
        for (int i = 0; i < this.speedItems.length; i++) {
            if ((i != 0 || Math.abs(playbackSpeed - 0.5f) >= 0.001f) && ((i != 1 || Math.abs(f) >= 0.001f) && ((i != 2 || Math.abs(playbackSpeed - 1.5f) >= 0.001f) && (i != 3 || Math.abs(playbackSpeed - 1.8f) >= 0.001f)))) {
                this.speedItems[i].setColors(getThemedColor("actionBarDefaultSubmenuItem"), getThemedColor("actionBarDefaultSubmenuItemIcon"));
            } else {
                this.speedItems[i].setColors(getThemedColor("inappPlayerPlayPause"), getThemedColor("inappPlayerPlayPause"));
            }
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Can't wrap try/catch for region: R(4:25|26|27|28) */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0066, code lost:
        if (r12.exists() == false) goto L_0x0068;
     */
    /* JADX WARNING: Missing exception handler attribute for start block: B:27:0x009c */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onSubItemClick(int r12) {
        /*
            r11 = this;
            org.telegram.messenger.MediaController r0 = org.telegram.messenger.MediaController.getInstance()
            org.telegram.messenger.MessageObject r0 = r0.getPlayingMessageObject()
            if (r0 == 0) goto L_0x01f3
            org.telegram.ui.LaunchActivity r1 = r11.parentActivity
            if (r1 != 0) goto L_0x0010
            goto L_0x01f3
        L_0x0010:
            r2 = 1
            if (r12 != r2) goto L_0x004b
            int r12 = org.telegram.messenger.UserConfig.selectedAccount
            int r3 = r11.currentAccount
            if (r12 == r3) goto L_0x001c
            r1.switchToAccount(r3, r2)
        L_0x001c:
            android.os.Bundle r12 = new android.os.Bundle
            r12.<init>()
            java.lang.String r1 = "onlySelect"
            r12.putBoolean(r1, r2)
            r1 = 3
            java.lang.String r2 = "dialogsType"
            r12.putInt(r2, r1)
            org.telegram.ui.DialogsActivity r1 = new org.telegram.ui.DialogsActivity
            r1.<init>(r12)
            java.util.ArrayList r12 = new java.util.ArrayList
            r12.<init>()
            r12.add(r0)
            org.telegram.ui.Components.AudioPlayerAlert$$ExternalSyntheticLambda12 r0 = new org.telegram.ui.Components.AudioPlayerAlert$$ExternalSyntheticLambda12
            r0.<init>(r11, r12)
            r1.setDelegate(r0)
            org.telegram.ui.LaunchActivity r12 = r11.parentActivity
            r12.lambda$runLinkRequest$47(r1)
            r11.dismiss()
            goto L_0x01f3
        L_0x004b:
            r3 = 2
            r4 = 0
            if (r12 != r3) goto L_0x00f7
            org.telegram.tgnet.TLRPC$Message r12 = r0.messageOwner     // Catch:{ Exception -> 0x00f1 }
            java.lang.String r12 = r12.attachPath     // Catch:{ Exception -> 0x00f1 }
            boolean r12 = android.text.TextUtils.isEmpty(r12)     // Catch:{ Exception -> 0x00f1 }
            if (r12 != 0) goto L_0x0068
            java.io.File r12 = new java.io.File     // Catch:{ Exception -> 0x00f1 }
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner     // Catch:{ Exception -> 0x00f1 }
            java.lang.String r1 = r1.attachPath     // Catch:{ Exception -> 0x00f1 }
            r12.<init>(r1)     // Catch:{ Exception -> 0x00f1 }
            boolean r1 = r12.exists()     // Catch:{ Exception -> 0x00f1 }
            if (r1 != 0) goto L_0x0069
        L_0x0068:
            r12 = r4
        L_0x0069:
            if (r12 != 0) goto L_0x0071
            org.telegram.tgnet.TLRPC$Message r12 = r0.messageOwner     // Catch:{ Exception -> 0x00f1 }
            java.io.File r12 = org.telegram.messenger.FileLoader.getPathToMessage(r12)     // Catch:{ Exception -> 0x00f1 }
        L_0x0071:
            boolean r1 = r12.exists()     // Catch:{ Exception -> 0x00f1 }
            if (r1 == 0) goto L_0x00c1
            android.content.Intent r1 = new android.content.Intent     // Catch:{ Exception -> 0x00f1 }
            java.lang.String r3 = "android.intent.action.SEND"
            r1.<init>(r3)     // Catch:{ Exception -> 0x00f1 }
            java.lang.String r0 = r0.getMimeType()     // Catch:{ Exception -> 0x00f1 }
            r1.setType(r0)     // Catch:{ Exception -> 0x00f1 }
            int r0 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x00f1 }
            r3 = 24
            java.lang.String r4 = "android.intent.extra.STREAM"
            if (r0 < r3) goto L_0x00a4
            android.content.Context r0 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x009c }
            java.lang.String r3 = "org.telegram.messenger.web.provider"
            android.net.Uri r0 = androidx.core.content.FileProvider.getUriForFile(r0, r3, r12)     // Catch:{ Exception -> 0x009c }
            r1.putExtra(r4, r0)     // Catch:{ Exception -> 0x009c }
            r1.setFlags(r2)     // Catch:{ Exception -> 0x009c }
            goto L_0x00ab
        L_0x009c:
            android.net.Uri r12 = android.net.Uri.fromFile(r12)     // Catch:{ Exception -> 0x00f1 }
            r1.putExtra(r4, r12)     // Catch:{ Exception -> 0x00f1 }
            goto L_0x00ab
        L_0x00a4:
            android.net.Uri r12 = android.net.Uri.fromFile(r12)     // Catch:{ Exception -> 0x00f1 }
            r1.putExtra(r4, r12)     // Catch:{ Exception -> 0x00f1 }
        L_0x00ab:
            org.telegram.ui.LaunchActivity r12 = r11.parentActivity     // Catch:{ Exception -> 0x00f1 }
            java.lang.String r0 = "ShareFile"
            r2 = 2131627969(0x7f0e0fc1, float:1.8883217E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r0, r2)     // Catch:{ Exception -> 0x00f1 }
            android.content.Intent r0 = android.content.Intent.createChooser(r1, r0)     // Catch:{ Exception -> 0x00f1 }
            r1 = 500(0x1f4, float:7.0E-43)
            r12.startActivityForResult(r0, r1)     // Catch:{ Exception -> 0x00f1 }
            goto L_0x01f3
        L_0x00c1:
            org.telegram.ui.ActionBar.AlertDialog$Builder r12 = new org.telegram.ui.ActionBar.AlertDialog$Builder     // Catch:{ Exception -> 0x00f1 }
            org.telegram.ui.LaunchActivity r0 = r11.parentActivity     // Catch:{ Exception -> 0x00f1 }
            r12.<init>((android.content.Context) r0)     // Catch:{ Exception -> 0x00f1 }
            java.lang.String r0 = "AppName"
            r1 = 2131624304(0x7f0e0170, float:1.8875784E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r0, r1)     // Catch:{ Exception -> 0x00f1 }
            r12.setTitle(r0)     // Catch:{ Exception -> 0x00f1 }
            java.lang.String r0 = "OK"
            r1 = 2131626828(0x7f0e0b4c, float:1.8880903E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r0, r1)     // Catch:{ Exception -> 0x00f1 }
            r12.setPositiveButton(r0, r4)     // Catch:{ Exception -> 0x00f1 }
            java.lang.String r0 = "PleaseDownload"
            r1 = 2131627305(0x7f0e0d29, float:1.888187E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r0, r1)     // Catch:{ Exception -> 0x00f1 }
            r12.setMessage(r0)     // Catch:{ Exception -> 0x00f1 }
            r12.show()     // Catch:{ Exception -> 0x00f1 }
            goto L_0x01f3
        L_0x00f1:
            r12 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r12)
            goto L_0x01f3
        L_0x00f7:
            r3 = 4
            r5 = 0
            if (r12 != r3) goto L_0x0173
            int r12 = org.telegram.messenger.UserConfig.selectedAccount
            int r3 = r11.currentAccount
            if (r12 == r3) goto L_0x0104
            r1.switchToAccount(r3, r2)
        L_0x0104:
            android.os.Bundle r12 = new android.os.Bundle
            r12.<init>()
            long r1 = r0.getDialogId()
            boolean r3 = org.telegram.messenger.DialogObject.isEncryptedDialog(r1)
            if (r3 == 0) goto L_0x011d
            int r1 = org.telegram.messenger.DialogObject.getEncryptedChatId(r1)
            java.lang.String r2 = "enc_id"
            r12.putInt(r2, r1)
            goto L_0x014e
        L_0x011d:
            boolean r3 = org.telegram.messenger.DialogObject.isUserDialog(r1)
            if (r3 == 0) goto L_0x0129
            java.lang.String r3 = "user_id"
            r12.putLong(r3, r1)
            goto L_0x014e
        L_0x0129:
            int r3 = r11.currentAccount
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
            long r6 = -r1
            java.lang.Long r4 = java.lang.Long.valueOf(r6)
            org.telegram.tgnet.TLRPC$Chat r3 = r3.getChat(r4)
            if (r3 == 0) goto L_0x0148
            org.telegram.tgnet.TLRPC$InputChannel r4 = r3.migrated_to
            if (r4 == 0) goto L_0x0148
            java.lang.String r4 = "migrated_to"
            r12.putLong(r4, r1)
            org.telegram.tgnet.TLRPC$InputChannel r1 = r3.migrated_to
            long r1 = r1.channel_id
            long r1 = -r1
        L_0x0148:
            long r1 = -r1
            java.lang.String r3 = "chat_id"
            r12.putLong(r3, r1)
        L_0x014e:
            int r0 = r0.getId()
            java.lang.String r1 = "message_id"
            r12.putInt(r1, r0)
            int r0 = r11.currentAccount
            org.telegram.messenger.NotificationCenter r0 = org.telegram.messenger.NotificationCenter.getInstance(r0)
            int r1 = org.telegram.messenger.NotificationCenter.closeChats
            java.lang.Object[] r2 = new java.lang.Object[r5]
            r0.postNotificationName(r1, r2)
            org.telegram.ui.LaunchActivity r0 = r11.parentActivity
            org.telegram.ui.ChatActivity r1 = new org.telegram.ui.ChatActivity
            r1.<init>(r12)
            r0.presentFragment(r1, r5, r5)
            r11.dismiss()
            goto L_0x01f3
        L_0x0173:
            r6 = 5
            if (r12 != r6) goto L_0x01f3
            int r12 = android.os.Build.VERSION.SDK_INT
            r6 = 23
            if (r12 < r6) goto L_0x0196
            r6 = 28
            if (r12 <= r6) goto L_0x0184
            boolean r12 = org.telegram.messenger.BuildVars.NO_SCOPED_STORAGE
            if (r12 == 0) goto L_0x0196
        L_0x0184:
            java.lang.String r12 = "android.permission.WRITE_EXTERNAL_STORAGE"
            int r1 = r1.checkSelfPermission(r12)
            if (r1 == 0) goto L_0x0196
            org.telegram.ui.LaunchActivity r0 = r11.parentActivity
            java.lang.String[] r1 = new java.lang.String[r2]
            r1[r5] = r12
            r0.requestPermissions(r1, r3)
            return
        L_0x0196:
            org.telegram.tgnet.TLRPC$Document r12 = r0.getDocument()
            java.lang.String r12 = org.telegram.messenger.FileLoader.getDocumentFileName(r12)
            boolean r1 = android.text.TextUtils.isEmpty(r12)
            if (r1 == 0) goto L_0x01a8
            java.lang.String r12 = r0.getFileName()
        L_0x01a8:
            r8 = r12
            org.telegram.tgnet.TLRPC$Message r12 = r0.messageOwner
            java.lang.String r12 = r12.attachPath
            if (r12 == 0) goto L_0x01c1
            int r1 = r12.length()
            if (r1 <= 0) goto L_0x01c1
            java.io.File r1 = new java.io.File
            r1.<init>(r12)
            boolean r1 = r1.exists()
            if (r1 != 0) goto L_0x01c1
            goto L_0x01c2
        L_0x01c1:
            r4 = r12
        L_0x01c2:
            if (r4 == 0) goto L_0x01cd
            int r12 = r4.length()
            if (r12 != 0) goto L_0x01cb
            goto L_0x01cd
        L_0x01cb:
            r5 = r4
            goto L_0x01d8
        L_0x01cd:
            org.telegram.tgnet.TLRPC$Message r12 = r0.messageOwner
            java.io.File r12 = org.telegram.messenger.FileLoader.getPathToMessage(r12)
            java.lang.String r12 = r12.toString()
            r5 = r12
        L_0x01d8:
            org.telegram.ui.LaunchActivity r6 = r11.parentActivity
            r7 = 3
            org.telegram.tgnet.TLRPC$Document r12 = r0.getDocument()
            if (r12 == 0) goto L_0x01e8
            org.telegram.tgnet.TLRPC$Document r12 = r0.getDocument()
            java.lang.String r12 = r12.mime_type
            goto L_0x01ea
        L_0x01e8:
            java.lang.String r12 = ""
        L_0x01ea:
            r9 = r12
            org.telegram.ui.Components.AudioPlayerAlert$$ExternalSyntheticLambda6 r10 = new org.telegram.ui.Components.AudioPlayerAlert$$ExternalSyntheticLambda6
            r10.<init>(r11)
            org.telegram.messenger.MediaController.saveFile(r5, r6, r7, r8, r9, r10)
        L_0x01f3:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.AudioPlayerAlert.onSubItemClick(int):void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onSubItemClick$9(ArrayList arrayList, DialogsActivity dialogsActivity, ArrayList arrayList2, CharSequence charSequence, boolean z) {
        ArrayList arrayList3 = arrayList2;
        if (arrayList2.size() > 1 || ((Long) arrayList3.get(0)).longValue() == UserConfig.getInstance(this.currentAccount).getClientUserId() || charSequence != null) {
            ArrayList arrayList4 = arrayList;
            for (int i = 0; i < arrayList2.size(); i++) {
                long longValue = ((Long) arrayList3.get(i)).longValue();
                if (charSequence != null) {
                    SendMessagesHelper.getInstance(this.currentAccount).sendMessage(charSequence.toString(), longValue, (MessageObject) null, (MessageObject) null, (TLRPC$WebPage) null, true, (ArrayList<TLRPC$MessageEntity>) null, (TLRPC$ReplyMarkup) null, (HashMap<String, String>) null, true, 0, (MessageObject.SendAnimationData) null);
                }
                SendMessagesHelper.getInstance(this.currentAccount).sendMessage((ArrayList<MessageObject>) arrayList, longValue, false, false, true, 0);
            }
            dialogsActivity.finishFragment();
            return;
        }
        long longValue2 = ((Long) arrayList3.get(0)).longValue();
        Bundle bundle = new Bundle();
        bundle.putBoolean("scrollToTopOnResume", true);
        if (DialogObject.isEncryptedDialog(longValue2)) {
            bundle.putInt("enc_id", DialogObject.getEncryptedChatId(longValue2));
        } else if (DialogObject.isUserDialog(longValue2)) {
            bundle.putLong("user_id", longValue2);
        } else {
            bundle.putLong("chat_id", -longValue2);
        }
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
        ChatActivity chatActivity = new ChatActivity(bundle);
        if (this.parentActivity.presentFragment(chatActivity, true, false)) {
            chatActivity.showFieldPanelForForward(true, arrayList);
        } else {
            dialogsActivity.finishFragment();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onSubItemClick$10() {
        BulletinFactory.of((FrameLayout) this.containerView, this.resourcesProvider).createDownloadBulletin(BulletinFactory.FileType.AUDIO).show();
    }

    /* access modifiers changed from: private */
    public void showAlbumCover(boolean z, boolean z2) {
        if (z) {
            if (this.blurredView.getVisibility() != 0 && !this.blurredAnimationInProgress) {
                this.blurredView.setTag(1);
                this.bigAlbumConver.setImageBitmap(this.coverContainer.getImageReceiver().getBitmap());
                this.blurredAnimationInProgress = true;
                View fragmentView = this.parentActivity.getActionBarLayout().fragmentsStack.get(this.parentActivity.getActionBarLayout().fragmentsStack.size() - 1).getFragmentView();
                int measuredWidth = (int) (((float) fragmentView.getMeasuredWidth()) / 6.0f);
                int measuredHeight = (int) (((float) fragmentView.getMeasuredHeight()) / 6.0f);
                Bitmap createBitmap = Bitmap.createBitmap(measuredWidth, measuredHeight, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(createBitmap);
                canvas.scale(0.16666667f, 0.16666667f);
                fragmentView.draw(canvas);
                canvas.translate((float) (this.containerView.getLeft() - getLeftInset()), 0.0f);
                this.containerView.draw(canvas);
                Utilities.stackBlurBitmap(createBitmap, Math.max(7, Math.max(measuredWidth, measuredHeight) / 180));
                this.blurredView.setBackground(new BitmapDrawable(createBitmap));
                this.blurredView.setVisibility(0);
                this.blurredView.animate().alpha(1.0f).setDuration(180).setListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        boolean unused = AudioPlayerAlert.this.blurredAnimationInProgress = false;
                    }
                }).start();
                this.bigAlbumConver.animate().scaleX(1.0f).scaleY(1.0f).setDuration(180).start();
            }
        } else if (this.blurredView.getVisibility() == 0) {
            this.blurredView.setTag((Object) null);
            if (z2) {
                this.blurredAnimationInProgress = true;
                this.blurredView.animate().alpha(0.0f).setDuration(180).setListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        AudioPlayerAlert.this.blurredView.setVisibility(4);
                        AudioPlayerAlert.this.bigAlbumConver.setImageBitmap((Bitmap) null);
                        boolean unused = AudioPlayerAlert.this.blurredAnimationInProgress = false;
                    }
                }).start();
                this.bigAlbumConver.animate().scaleX(0.9f).scaleY(0.9f).setDuration(180).start();
                return;
            }
            this.blurredView.setAlpha(0.0f);
            this.blurredView.setVisibility(4);
            this.bigAlbumConver.setImageBitmap((Bitmap) null);
            this.bigAlbumConver.setScaleX(0.9f);
            this.bigAlbumConver.setScaleY(0.9f);
        }
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        AudioPlayerCell audioPlayerCell;
        MessageObject messageObject;
        AudioPlayerCell audioPlayerCell2;
        MessageObject messageObject2;
        MessageObject playingMessageObject;
        int i3 = 0;
        if (i == NotificationCenter.messagePlayingDidStart || i == NotificationCenter.messagePlayingPlayStateChanged || i == NotificationCenter.messagePlayingDidReset) {
            int i4 = NotificationCenter.messagePlayingDidReset;
            updateTitle(i == i4 && objArr[1].booleanValue());
            if (i == i4 || i == NotificationCenter.messagePlayingPlayStateChanged) {
                int childCount = this.listView.getChildCount();
                for (int i5 = 0; i5 < childCount; i5++) {
                    View childAt = this.listView.getChildAt(i5);
                    if ((childAt instanceof AudioPlayerCell) && (messageObject = audioPlayerCell.getMessageObject()) != null && (messageObject.isVoice() || messageObject.isMusic())) {
                        (audioPlayerCell = (AudioPlayerCell) childAt).updateButtonState(false, true);
                    }
                }
                if (i == NotificationCenter.messagePlayingPlayStateChanged && MediaController.getInstance().getPlayingMessageObject() != null) {
                    if (MediaController.getInstance().isMessagePaused()) {
                        startForwardRewindingSeek();
                    } else if (this.rewindingState == 1 && this.rewindingProgress != -1.0f) {
                        AndroidUtilities.cancelRunOnUIThread(this.forwardSeek);
                        this.lastUpdateRewindingPlayerTime = 0;
                        this.forwardSeek.run();
                        this.rewindingProgress = -1.0f;
                    }
                }
            } else if (objArr[0].eventId == 0) {
                int childCount2 = this.listView.getChildCount();
                for (int i6 = 0; i6 < childCount2; i6++) {
                    View childAt2 = this.listView.getChildAt(i6);
                    if ((childAt2 instanceof AudioPlayerCell) && (messageObject2 = audioPlayerCell2.getMessageObject()) != null && (messageObject2.isVoice() || messageObject2.isMusic())) {
                        (audioPlayerCell2 = (AudioPlayerCell) childAt2).updateButtonState(false, true);
                    }
                }
            }
        } else if (i == NotificationCenter.messagePlayingProgressDidChanged) {
            MessageObject playingMessageObject2 = MediaController.getInstance().getPlayingMessageObject();
            if (playingMessageObject2 != null && playingMessageObject2.isMusic()) {
                updateProgress(playingMessageObject2);
            }
        } else if (i == NotificationCenter.musicDidLoad) {
            this.playlist = MediaController.getInstance().getPlaylist();
            this.listAdapter.notifyDataSetChanged();
        } else if (i == NotificationCenter.moreMusicDidLoad) {
            this.playlist = MediaController.getInstance().getPlaylist();
            this.listAdapter.notifyDataSetChanged();
            if (SharedConfig.playOrderReversed) {
                this.listView.stopScroll();
                int intValue = objArr[0].intValue();
                this.layoutManager.findFirstVisibleItemPosition();
                int findLastVisibleItemPosition = this.layoutManager.findLastVisibleItemPosition();
                if (findLastVisibleItemPosition != -1) {
                    View findViewByPosition = this.layoutManager.findViewByPosition(findLastVisibleItemPosition);
                    if (findViewByPosition != null) {
                        i3 = findViewByPosition.getTop();
                    }
                    this.layoutManager.scrollToPositionWithOffset(findLastVisibleItemPosition + intValue, i3);
                }
            }
        } else if (i == NotificationCenter.fileLoaded) {
            if (objArr[0].equals(this.currentFile)) {
                updateTitle(false);
                this.currentAudioFinishedLoading = true;
            }
        } else if (i == NotificationCenter.fileLoadProgressChanged && objArr[0].equals(this.currentFile) && (playingMessageObject = MediaController.getInstance().getPlayingMessageObject()) != null) {
            Long l = objArr[1];
            Long l2 = objArr[2];
            float f = 1.0f;
            if (!this.currentAudioFinishedLoading) {
                long elapsedRealtime = SystemClock.elapsedRealtime();
                if (Math.abs(elapsedRealtime - this.lastBufferedPositionCheck) >= 500) {
                    if (MediaController.getInstance().isStreamingCurrentAudio()) {
                        f = FileLoader.getInstance(this.currentAccount).getBufferedProgressFromPosition(playingMessageObject.audioProgress, this.currentFile);
                    }
                    this.lastBufferedPositionCheck = elapsedRealtime;
                } else {
                    f = -1.0f;
                }
            }
            if (f != -1.0f) {
                this.seekBarView.setBufferedProgress(f);
            }
        }
    }

    /* access modifiers changed from: private */
    public void updateLayout() {
        if (this.listView.getChildCount() <= 0) {
            RecyclerListView recyclerListView = this.listView;
            int paddingTop = recyclerListView.getPaddingTop();
            this.scrollOffsetY = paddingTop;
            recyclerListView.setTopGlowOffset(paddingTop);
            this.containerView.invalidate();
            return;
        }
        View childAt = this.listView.getChildAt(0);
        RecyclerListView.Holder holder = (RecyclerListView.Holder) this.listView.findContainingViewHolder(childAt);
        int top = childAt.getTop();
        int dp = AndroidUtilities.dp(7.0f);
        if (top < AndroidUtilities.dp(7.0f) || holder == null || holder.getAdapterPosition() != 0) {
            top = dp;
        }
        boolean z = top <= AndroidUtilities.dp(12.0f);
        if ((z && this.actionBar.getTag() == null) || (!z && this.actionBar.getTag() != null)) {
            this.actionBar.setTag(z ? 1 : null);
            AnimatorSet animatorSet = this.actionBarAnimation;
            if (animatorSet != null) {
                animatorSet.cancel();
                this.actionBarAnimation = null;
            }
            AnimatorSet animatorSet2 = new AnimatorSet();
            this.actionBarAnimation = animatorSet2;
            animatorSet2.setDuration(180);
            AnimatorSet animatorSet3 = this.actionBarAnimation;
            Animator[] animatorArr = new Animator[2];
            ActionBar actionBar2 = this.actionBar;
            Property property = View.ALPHA;
            float[] fArr = new float[1];
            float f = 1.0f;
            fArr[0] = z ? 1.0f : 0.0f;
            animatorArr[0] = ObjectAnimator.ofFloat(actionBar2, property, fArr);
            View view = this.actionBarShadow;
            Property property2 = View.ALPHA;
            float[] fArr2 = new float[1];
            if (!z) {
                f = 0.0f;
            }
            fArr2[0] = f;
            animatorArr[1] = ObjectAnimator.ofFloat(view, property2, fArr2);
            animatorSet3.playTogether(animatorArr);
            this.actionBarAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                }

                public void onAnimationCancel(Animator animator) {
                    AnimatorSet unused = AudioPlayerAlert.this.actionBarAnimation = null;
                }
            });
            this.actionBarAnimation.start();
        }
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.listView.getLayoutParams();
        int dp2 = top + (layoutParams.topMargin - AndroidUtilities.dp(11.0f));
        if (this.scrollOffsetY != dp2) {
            RecyclerListView recyclerListView2 = this.listView;
            this.scrollOffsetY = dp2;
            recyclerListView2.setTopGlowOffset(dp2 - layoutParams.topMargin);
            this.containerView.invalidate();
        }
    }

    public void dismiss() {
        super.dismiss();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingDidReset);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingPlayStateChanged);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingDidStart);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingProgressDidChanged);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileLoaded);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileLoadProgressChanged);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.musicDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.moreMusicDidLoad);
        DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
    }

    public void onBackPressed() {
        ActionBar actionBar2 = this.actionBar;
        if (actionBar2 != null && actionBar2.isSearchFieldVisible()) {
            this.actionBar.closeSearchField();
        } else if (this.blurredView.getTag() != null) {
            showAlbumCover(false, true);
        } else {
            super.onBackPressed();
        }
    }

    public void onProgressDownload(String str, long j, long j2) {
        this.progressView.setProgress(Math.min(1.0f, ((float) j) / ((float) j2)), true);
    }

    public int getObserverTag() {
        return this.TAG;
    }

    private void updateRepeatButton() {
        int i = SharedConfig.repeatMode;
        if (i == 0 || i == 1) {
            if (SharedConfig.shuffleMusic) {
                if (i == 0) {
                    this.repeatButton.setIcon(NUM);
                } else {
                    this.repeatButton.setIcon(NUM);
                }
            } else if (!SharedConfig.playOrderReversed) {
                this.repeatButton.setIcon(NUM);
            } else if (i == 0) {
                this.repeatButton.setIcon(NUM);
            } else {
                this.repeatButton.setIcon(NUM);
            }
            if (i != 0 || SharedConfig.shuffleMusic || SharedConfig.playOrderReversed) {
                this.repeatButton.setTag("player_buttonActive");
                this.repeatButton.setIconColor(getThemedColor("player_buttonActive"));
                Theme.setSelectorDrawableColor(this.repeatButton.getBackground(), NUM & getThemedColor("player_buttonActive"), true);
                if (i != 0) {
                    this.repeatButton.setContentDescription(LocaleController.getString("AccDescrRepeatList", NUM));
                } else if (SharedConfig.shuffleMusic) {
                    this.repeatButton.setContentDescription(LocaleController.getString("ShuffleList", NUM));
                } else {
                    this.repeatButton.setContentDescription(LocaleController.getString("ReverseOrder", NUM));
                }
            } else {
                this.repeatButton.setTag("player_button");
                this.repeatButton.setIconColor(getThemedColor("player_button"));
                Theme.setSelectorDrawableColor(this.repeatButton.getBackground(), getThemedColor("listSelectorSDK21"), true);
                this.repeatButton.setContentDescription(LocaleController.getString("AccDescrRepeatOff", NUM));
            }
        } else if (i == 2) {
            this.repeatButton.setIcon(NUM);
            this.repeatButton.setTag("player_buttonActive");
            this.repeatButton.setIconColor(getThemedColor("player_buttonActive"));
            Theme.setSelectorDrawableColor(this.repeatButton.getBackground(), NUM & getThemedColor("player_buttonActive"), true);
            this.repeatButton.setContentDescription(LocaleController.getString("AccDescrRepeatOne", NUM));
        }
    }

    /* access modifiers changed from: private */
    public void updateProgress(MessageObject messageObject) {
        updateProgress(messageObject, false);
    }

    private void updateProgress(MessageObject messageObject, boolean z) {
        int i;
        int i2;
        SeekBarView seekBarView2 = this.seekBarView;
        if (seekBarView2 != null) {
            if (seekBarView2.isDragging()) {
                i = (int) (((float) messageObject.getDuration()) * this.seekBarView.getProgress());
            } else {
                boolean z2 = true;
                if (this.rewindingProgress < 0.0f || ((i2 = this.rewindingState) != -1 && (i2 != 1 || !MediaController.getInstance().isMessagePaused()))) {
                    z2 = false;
                }
                if (z2) {
                    this.seekBarView.setProgress(this.rewindingProgress, z);
                } else {
                    this.seekBarView.setProgress(messageObject.audioProgress, z);
                }
                float f = 1.0f;
                if (!this.currentAudioFinishedLoading) {
                    long elapsedRealtime = SystemClock.elapsedRealtime();
                    if (Math.abs(elapsedRealtime - this.lastBufferedPositionCheck) >= 500) {
                        if (MediaController.getInstance().isStreamingCurrentAudio()) {
                            f = FileLoader.getInstance(this.currentAccount).getBufferedProgressFromPosition(messageObject.audioProgress, this.currentFile);
                        }
                        this.lastBufferedPositionCheck = elapsedRealtime;
                    } else {
                        f = -1.0f;
                    }
                }
                if (f != -1.0f) {
                    this.seekBarView.setBufferedProgress(f);
                }
                if (z2) {
                    int duration = (int) (((float) messageObject.getDuration()) * this.seekBarView.getProgress());
                    messageObject.audioProgressSec = duration;
                    i = duration;
                } else {
                    i = messageObject.audioProgressSec;
                }
            }
            if (this.lastTime != i) {
                this.lastTime = i;
                this.timeTextView.setText(AndroidUtilities.formatShortDuration(i));
            }
        }
    }

    private void checkIfMusicDownloaded(MessageObject messageObject) {
        String str = messageObject.messageOwner.attachPath;
        File file = null;
        if (str != null && str.length() > 0) {
            File file2 = new File(messageObject.messageOwner.attachPath);
            if (file2.exists()) {
                file = file2;
            }
        }
        if (file == null) {
            file = FileLoader.getPathToMessage(messageObject.messageOwner);
        }
        boolean z = SharedConfig.streamMedia && ((int) messageObject.getDialogId()) != 0 && messageObject.isMusic();
        if (file.exists() || z) {
            DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
            this.progressView.setVisibility(4);
            this.seekBarView.setVisibility(0);
            this.playButton.setEnabled(true);
            return;
        }
        String fileName = messageObject.getFileName();
        DownloadController.getInstance(this.currentAccount).addLoadingFileObserver(fileName, this);
        Float fileProgress = ImageLoader.getInstance().getFileProgress(fileName);
        this.progressView.setProgress(fileProgress != null ? fileProgress.floatValue() : 0.0f, false);
        this.progressView.setVisibility(0);
        this.seekBarView.setVisibility(4);
        this.playButton.setEnabled(false);
    }

    private void updateTitle(boolean z) {
        MessageObject playingMessageObject = MediaController.getInstance().getPlayingMessageObject();
        if ((playingMessageObject == null && z) || (playingMessageObject != null && !playingMessageObject.isMusic())) {
            dismiss();
        } else if (playingMessageObject == null) {
            this.lastMessageObject = null;
        } else {
            boolean z2 = playingMessageObject == this.lastMessageObject;
            this.lastMessageObject = playingMessageObject;
            if (playingMessageObject.eventId != 0 || playingMessageObject.getId() <= -NUM) {
                this.optionsButton.setVisibility(4);
            } else {
                this.optionsButton.setVisibility(0);
            }
            if (MessagesController.getInstance(this.currentAccount).isChatNoForwards(playingMessageObject.getChatId())) {
                this.optionsButton.hideSubItem(1);
                this.optionsButton.hideSubItem(2);
                this.optionsButton.hideSubItem(5);
                this.optionsButton.setAdditionalYOffset(-AndroidUtilities.dp(16.0f));
            } else {
                this.optionsButton.showSubItem(1);
                this.optionsButton.showSubItem(2);
                this.optionsButton.showSubItem(5);
                this.optionsButton.setAdditionalYOffset(-AndroidUtilities.dp(157.0f));
            }
            checkIfMusicDownloaded(playingMessageObject);
            updateProgress(playingMessageObject, !z2);
            updateCover(playingMessageObject, !z2);
            if (MediaController.getInstance().isMessagePaused()) {
                this.playPauseDrawable.setPause(false);
                this.playButton.setContentDescription(LocaleController.getString("AccActionPlay", NUM));
            } else {
                this.playPauseDrawable.setPause(true);
                this.playButton.setContentDescription(LocaleController.getString("AccActionPause", NUM));
            }
            String musicTitle = playingMessageObject.getMusicTitle();
            String musicAuthor = playingMessageObject.getMusicAuthor();
            this.titleTextView.setText(musicTitle);
            this.authorTextView.setText(musicAuthor);
            int duration = playingMessageObject.getDuration();
            this.lastDuration = duration;
            TextView textView = this.durationTextView;
            if (textView != null) {
                textView.setText(duration != 0 ? AndroidUtilities.formatShortDuration(duration) : "-:--");
            }
            if (duration > 600) {
                this.playbackSpeedButton.setVisibility(0);
            } else {
                this.playbackSpeedButton.setVisibility(8);
            }
            if (!z2) {
                preloadNeighboringThumbs();
            }
        }
    }

    private void updateCover(MessageObject messageObject, boolean z) {
        CoverContainer coverContainer2 = this.coverContainer;
        BackupImageView nextImageView = z ? coverContainer2.getNextImageView() : coverContainer2.getImageView();
        AudioInfo audioInfo = MediaController.getInstance().getAudioInfo();
        if (audioInfo == null || audioInfo.getCover() == null) {
            this.currentFile = FileLoader.getAttachFileName(messageObject.getDocument());
            this.currentAudioFinishedLoading = false;
            String artworkUrl = messageObject.getArtworkUrl(false);
            ImageLocation artworkThumbImageLocation = getArtworkThumbImageLocation(messageObject);
            if (!TextUtils.isEmpty(artworkUrl)) {
                nextImageView.setImage(ImageLocation.getForPath(artworkUrl), (String) null, artworkThumbImageLocation, (String) null, (String) null, 0, 1, messageObject);
            } else if (artworkThumbImageLocation != null) {
                nextImageView.setImage((ImageLocation) null, (String) null, artworkThumbImageLocation, (String) null, (String) null, 0, 1, messageObject);
            } else {
                nextImageView.setImageDrawable((Drawable) null);
            }
            nextImageView.invalidate();
        } else {
            nextImageView.setImageBitmap(audioInfo.getCover());
            this.currentFile = null;
            this.currentAudioFinishedLoading = true;
        }
        if (z) {
            this.coverContainer.switchImageViews();
        }
    }

    private ImageLocation getArtworkThumbImageLocation(MessageObject messageObject) {
        TLRPC$Document document = messageObject.getDocument();
        TLRPC$PhotoSize closestPhotoSizeWithSize = document != null ? FileLoader.getClosestPhotoSizeWithSize(document.thumbs, 360) : null;
        if (!(closestPhotoSizeWithSize instanceof TLRPC$TL_photoSize) && !(closestPhotoSizeWithSize instanceof TLRPC$TL_photoSizeProgressive)) {
            closestPhotoSizeWithSize = null;
        }
        if (closestPhotoSizeWithSize != null) {
            return ImageLocation.getForDocument(closestPhotoSizeWithSize, document);
        }
        String artworkUrl = messageObject.getArtworkUrl(true);
        if (artworkUrl != null) {
            return ImageLocation.getForPath(artworkUrl);
        }
        return null;
    }

    private void preloadNeighboringThumbs() {
        MediaController instance = MediaController.getInstance();
        ArrayList<MessageObject> playlist2 = instance.getPlaylist();
        if (playlist2.size() > 1) {
            ArrayList arrayList = new ArrayList();
            int playingMessageObjectNum = instance.getPlayingMessageObjectNum();
            int i = playingMessageObjectNum + 1;
            int i2 = playingMessageObjectNum - 1;
            if (i >= playlist2.size()) {
                i = 0;
            }
            if (i2 <= -1) {
                i2 = playlist2.size() - 1;
            }
            arrayList.add(playlist2.get(i));
            if (i != i2) {
                arrayList.add(playlist2.get(i2));
            }
            int size = arrayList.size();
            for (int i3 = 0; i3 < size; i3++) {
                MessageObject messageObject = (MessageObject) arrayList.get(i3);
                ImageLocation artworkThumbImageLocation = getArtworkThumbImageLocation(messageObject);
                if (artworkThumbImageLocation != null) {
                    if (artworkThumbImageLocation.path != null) {
                        ImageLoader.getInstance().preloadArtwork(artworkThumbImageLocation.path);
                    } else {
                        FileLoader.getInstance(this.currentAccount).loadFile(artworkThumbImageLocation, messageObject, (String) null, 0, 1);
                    }
                }
            }
        }
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context context;
        private ArrayList<MessageObject> searchResult = new ArrayList<>();
        private Runnable searchRunnable;

        public int getItemViewType(int i) {
            return 0;
        }

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return true;
        }

        public ListAdapter(Context context2) {
            this.context = context2;
        }

        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
            if (AudioPlayerAlert.this.playlist.size() > 1) {
                AudioPlayerAlert.this.playerLayout.setBackgroundColor(AudioPlayerAlert.this.getThemedColor("player_background"));
                AudioPlayerAlert.this.playerShadow.setVisibility(0);
                AudioPlayerAlert.this.listView.setPadding(0, AudioPlayerAlert.this.listView.getPaddingTop(), 0, AndroidUtilities.dp(179.0f));
            } else {
                AudioPlayerAlert.this.playerLayout.setBackground((Drawable) null);
                AudioPlayerAlert.this.playerShadow.setVisibility(4);
                AudioPlayerAlert.this.listView.setPadding(0, AudioPlayerAlert.this.listView.getPaddingTop(), 0, 0);
            }
            AudioPlayerAlert.this.updateEmptyView();
        }

        public int getItemCount() {
            if (AudioPlayerAlert.this.searchWas) {
                return this.searchResult.size();
            }
            if (AudioPlayerAlert.this.playlist.size() > 1) {
                return AudioPlayerAlert.this.playlist.size();
            }
            return 0;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            Context context2 = this.context;
            boolean currentPlaylistIsGlobalSearch = MediaController.getInstance().currentPlaylistIsGlobalSearch();
            return new RecyclerListView.Holder(new AudioPlayerCell(context2, currentPlaylistIsGlobalSearch ? 1 : 0, AudioPlayerAlert.this.resourcesProvider));
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            AudioPlayerCell audioPlayerCell = (AudioPlayerCell) viewHolder.itemView;
            if (AudioPlayerAlert.this.searchWas) {
                audioPlayerCell.setMessageObject(this.searchResult.get(i));
            } else if (SharedConfig.playOrderReversed) {
                audioPlayerCell.setMessageObject((MessageObject) AudioPlayerAlert.this.playlist.get(i));
            } else {
                audioPlayerCell.setMessageObject((MessageObject) AudioPlayerAlert.this.playlist.get((AudioPlayerAlert.this.playlist.size() - i) - 1));
            }
        }

        public void search(String str) {
            if (this.searchRunnable != null) {
                Utilities.searchQueue.cancelRunnable(this.searchRunnable);
                this.searchRunnable = null;
            }
            if (str == null) {
                this.searchResult.clear();
                notifyDataSetChanged();
                return;
            }
            DispatchQueue dispatchQueue = Utilities.searchQueue;
            AudioPlayerAlert$ListAdapter$$ExternalSyntheticLambda1 audioPlayerAlert$ListAdapter$$ExternalSyntheticLambda1 = new AudioPlayerAlert$ListAdapter$$ExternalSyntheticLambda1(this, str);
            this.searchRunnable = audioPlayerAlert$ListAdapter$$ExternalSyntheticLambda1;
            dispatchQueue.postRunnable(audioPlayerAlert$ListAdapter$$ExternalSyntheticLambda1, 300);
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$search$0(String str) {
            this.searchRunnable = null;
            processSearch(str);
        }

        private void processSearch(String str) {
            AndroidUtilities.runOnUIThread(new AudioPlayerAlert$ListAdapter$$ExternalSyntheticLambda0(this, str));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$processSearch$2(String str) {
            Utilities.searchQueue.postRunnable(new AudioPlayerAlert$ListAdapter$$ExternalSyntheticLambda2(this, str, new ArrayList(AudioPlayerAlert.this.playlist)));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$processSearch$1(String str, ArrayList arrayList) {
            TLRPC$Document tLRPC$Document;
            boolean z;
            String str2;
            String lowerCase = str.trim().toLowerCase();
            if (lowerCase.length() == 0) {
                updateSearchResults(new ArrayList(), str);
                return;
            }
            String translitString = LocaleController.getInstance().getTranslitString(lowerCase);
            if (lowerCase.equals(translitString) || translitString.length() == 0) {
                translitString = null;
            }
            int i = (translitString != null ? 1 : 0) + 1;
            String[] strArr = new String[i];
            strArr[0] = lowerCase;
            if (translitString != null) {
                strArr[1] = translitString;
            }
            ArrayList arrayList2 = new ArrayList();
            for (int i2 = 0; i2 < arrayList.size(); i2++) {
                MessageObject messageObject = (MessageObject) arrayList.get(i2);
                int i3 = 0;
                while (true) {
                    if (i3 >= i) {
                        break;
                    }
                    String str3 = strArr[i3];
                    String documentName = messageObject.getDocumentName();
                    if (!(documentName == null || documentName.length() == 0)) {
                        if (documentName.toLowerCase().contains(str3)) {
                            arrayList2.add(messageObject);
                            break;
                        }
                        if (messageObject.type == 0) {
                            tLRPC$Document = messageObject.messageOwner.media.webpage.document;
                        } else {
                            tLRPC$Document = messageObject.messageOwner.media.document;
                        }
                        int i4 = 0;
                        while (true) {
                            if (i4 >= tLRPC$Document.attributes.size()) {
                                z = false;
                                break;
                            }
                            TLRPC$DocumentAttribute tLRPC$DocumentAttribute = tLRPC$Document.attributes.get(i4);
                            if (tLRPC$DocumentAttribute instanceof TLRPC$TL_documentAttributeAudio) {
                                String str4 = tLRPC$DocumentAttribute.performer;
                                z = str4 != null ? str4.toLowerCase().contains(str3) : false;
                                if (!z && (str2 = tLRPC$DocumentAttribute.title) != null) {
                                    z = str2.toLowerCase().contains(str3);
                                }
                            } else {
                                i4++;
                            }
                        }
                        if (z) {
                            arrayList2.add(messageObject);
                            break;
                        }
                    }
                    i3++;
                }
            }
            updateSearchResults(arrayList2, str);
        }

        private void updateSearchResults(ArrayList<MessageObject> arrayList, String str) {
            AndroidUtilities.runOnUIThread(new AudioPlayerAlert$ListAdapter$$ExternalSyntheticLambda3(this, arrayList, str));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$updateSearchResults$3(ArrayList arrayList, String str) {
            if (AudioPlayerAlert.this.searching) {
                boolean unused = AudioPlayerAlert.this.searchWas = true;
                this.searchResult = arrayList;
                notifyDataSetChanged();
                AudioPlayerAlert.this.layoutManager.scrollToPosition(0);
                AudioPlayerAlert.this.emptySubtitleTextView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("NoAudioFoundPlayerInfo", NUM, str)));
            }
        }
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        AudioPlayerAlert$$ExternalSyntheticLambda10 audioPlayerAlert$$ExternalSyntheticLambda10 = new AudioPlayerAlert$$ExternalSyntheticLambda10(this);
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "player_actionBar"));
        AudioPlayerAlert$$ExternalSyntheticLambda10 audioPlayerAlert$$ExternalSyntheticLambda102 = audioPlayerAlert$$ExternalSyntheticLambda10;
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, audioPlayerAlert$$ExternalSyntheticLambda102, "player_actionBarTitle"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "player_actionBarTitle"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBTITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "player_actionBarTitle"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "player_actionBarSelector"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCH, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "player_actionBarTitle"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCHPLACEHOLDER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "player_time"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{AudioPlayerCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inLoader"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{AudioPlayerCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outLoader"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{AudioPlayerCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inLoaderSelected"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{AudioPlayerCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inMediaIcon"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{AudioPlayerCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inMediaIconSelected"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{AudioPlayerCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText2"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{AudioPlayerCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inAudioSelectedProgress"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{AudioPlayerCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inAudioProgress"));
        arrayList.add(new ThemeDescription(this.containerView, 0, (Class[]) null, (Paint) null, new Drawable[]{this.shadowDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogBackground"));
        arrayList.add(new ThemeDescription(this.progressView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "player_progressBackground"));
        arrayList.add(new ThemeDescription(this.progressView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "player_progress"));
        arrayList.add(new ThemeDescription(this.seekBarView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "player_progressBackground"));
        arrayList.add(new ThemeDescription(this.seekBarView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "key_player_progressCachedBackground"));
        arrayList.add(new ThemeDescription(this.seekBarView, ThemeDescription.FLAG_PROGRESSBAR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "player_progress"));
        arrayList.add(new ThemeDescription(this.playbackSpeedButton, ThemeDescription.FLAG_CHECKTAG | ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "inappPlayerPlayPause"));
        arrayList.add(new ThemeDescription(this.playbackSpeedButton, ThemeDescription.FLAG_CHECKTAG | ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "inappPlayerClose"));
        arrayList.add(new ThemeDescription(this.repeatButton, 0, (Class[]) null, (Paint) null, (Drawable[]) null, audioPlayerAlert$$ExternalSyntheticLambda102, "player_button"));
        arrayList.add(new ThemeDescription(this.repeatButton, 0, (Class[]) null, (Paint) null, (Drawable[]) null, audioPlayerAlert$$ExternalSyntheticLambda102, "player_buttonActive"));
        arrayList.add(new ThemeDescription(this.repeatButton, 0, (Class[]) null, (Paint) null, (Drawable[]) null, audioPlayerAlert$$ExternalSyntheticLambda102, "listSelectorSDK21"));
        arrayList.add(new ThemeDescription(this.repeatButton, 0, (Class[]) null, (Paint) null, (Drawable[]) null, audioPlayerAlert$$ExternalSyntheticLambda102, "actionBarDefaultSubmenuItem"));
        arrayList.add(new ThemeDescription(this.repeatButton, 0, (Class[]) null, (Paint) null, (Drawable[]) null, audioPlayerAlert$$ExternalSyntheticLambda102, "actionBarDefaultSubmenuBackground"));
        arrayList.add(new ThemeDescription(this.optionsButton, 0, (Class[]) null, (Paint) null, (Drawable[]) null, audioPlayerAlert$$ExternalSyntheticLambda102, "player_button"));
        arrayList.add(new ThemeDescription(this.optionsButton, 0, (Class[]) null, (Paint) null, (Drawable[]) null, audioPlayerAlert$$ExternalSyntheticLambda102, "listSelectorSDK21"));
        arrayList.add(new ThemeDescription(this.optionsButton, 0, (Class[]) null, (Paint) null, (Drawable[]) null, audioPlayerAlert$$ExternalSyntheticLambda102, "actionBarDefaultSubmenuItem"));
        arrayList.add(new ThemeDescription(this.optionsButton, 0, (Class[]) null, (Paint) null, (Drawable[]) null, audioPlayerAlert$$ExternalSyntheticLambda102, "actionBarDefaultSubmenuBackground"));
        RLottieImageView rLottieImageView = this.prevButton;
        arrayList.add(new ThemeDescription((View) rLottieImageView, 0, (Class[]) null, new RLottieDrawable[]{rLottieImageView.getAnimatedDrawable()}, "Triangle 3", "player_button"));
        RLottieImageView rLottieImageView2 = this.prevButton;
        arrayList.add(new ThemeDescription((View) rLottieImageView2, 0, (Class[]) null, new RLottieDrawable[]{rLottieImageView2.getAnimatedDrawable()}, "Triangle 4", "player_button"));
        RLottieImageView rLottieImageView3 = this.prevButton;
        arrayList.add(new ThemeDescription((View) rLottieImageView3, 0, (Class[]) null, new RLottieDrawable[]{rLottieImageView3.getAnimatedDrawable()}, "Rectangle 4", "player_button"));
        arrayList.add(new ThemeDescription(this.prevButton, ThemeDescription.FLAG_IMAGECOLOR | ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        arrayList.add(new ThemeDescription(this.playButton, ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "player_button"));
        arrayList.add(new ThemeDescription(this.playButton, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE | ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        RLottieImageView rLottieImageView4 = this.nextButton;
        arrayList.add(new ThemeDescription((View) rLottieImageView4, 0, (Class[]) null, new RLottieDrawable[]{rLottieImageView4.getAnimatedDrawable()}, "Triangle 3", "player_button"));
        RLottieImageView rLottieImageView5 = this.nextButton;
        arrayList.add(new ThemeDescription((View) rLottieImageView5, 0, (Class[]) null, new RLottieDrawable[]{rLottieImageView5.getAnimatedDrawable()}, "Triangle 4", "player_button"));
        RLottieImageView rLottieImageView6 = this.nextButton;
        arrayList.add(new ThemeDescription((View) rLottieImageView6, 0, (Class[]) null, new RLottieDrawable[]{rLottieImageView6.getAnimatedDrawable()}, "Rectangle 4", "player_button"));
        arrayList.add(new ThemeDescription(this.nextButton, ThemeDescription.FLAG_IMAGECOLOR | ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        arrayList.add(new ThemeDescription(this.playerLayout, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "player_background"));
        arrayList.add(new ThemeDescription(this.playerShadow, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogShadowLine"));
        arrayList.add(new ThemeDescription(this.emptyImageView, ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogEmptyImage"));
        arrayList.add(new ThemeDescription(this.emptyTitleTextView, ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogEmptyText"));
        arrayList.add(new ThemeDescription(this.emptySubtitleTextView, ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogEmptyText"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogScrollGlow"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "divider"));
        arrayList.add(new ThemeDescription(this.progressView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "emptyListPlaceholder"));
        arrayList.add(new ThemeDescription(this.progressView, ThemeDescription.FLAG_PROGRESSBAR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "progressCircle"));
        arrayList.add(new ThemeDescription(this.durationTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "player_time"));
        arrayList.add(new ThemeDescription(this.timeTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "player_time"));
        arrayList.add(new ThemeDescription(this.titleTextView.getTextView(), ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "player_actionBarTitle"));
        arrayList.add(new ThemeDescription(this.titleTextView.getNextTextView(), ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "player_actionBarTitle"));
        arrayList.add(new ThemeDescription(this.authorTextView.getTextView(), ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "player_time"));
        arrayList.add(new ThemeDescription(this.authorTextView.getNextTextView(), ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "player_time"));
        arrayList.add(new ThemeDescription(this.containerView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "key_sheet_scrollUp"));
        return arrayList;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$getThemeDescriptions$11() {
        this.searchItem.getSearchField().setCursorColor(getThemedColor("player_actionBarTitle"));
        ActionBarMenuItem actionBarMenuItem = this.repeatButton;
        actionBarMenuItem.setIconColor(getThemedColor((String) actionBarMenuItem.getTag()));
        Theme.setSelectorDrawableColor(this.repeatButton.getBackground(), getThemedColor("listSelectorSDK21"), true);
        this.optionsButton.setIconColor(getThemedColor("player_button"));
        Theme.setSelectorDrawableColor(this.optionsButton.getBackground(), getThemedColor("listSelectorSDK21"), true);
        this.progressView.setBackgroundColor(getThemedColor("player_progressBackground"));
        this.progressView.setProgressColor(getThemedColor("player_progress"));
        updateSubMenu();
        this.repeatButton.redrawPopup(getThemedColor("actionBarDefaultSubmenuBackground"));
        this.optionsButton.setPopupItemsColor(getThemedColor("actionBarDefaultSubmenuItem"), false);
        this.optionsButton.setPopupItemsColor(getThemedColor("actionBarDefaultSubmenuItem"), true);
        this.optionsButton.redrawPopup(getThemedColor("actionBarDefaultSubmenuBackground"));
    }

    private static abstract class CoverContainer extends FrameLayout {
        private int activeIndex;
        private AnimatorSet animatorSet;
        private final BackupImageView[] imageViews = new BackupImageView[2];

        /* access modifiers changed from: protected */
        public abstract void onImageUpdated(ImageReceiver imageReceiver);

        public CoverContainer(Context context) {
            super(context);
            for (int i = 0; i < 2; i++) {
                this.imageViews[i] = new BackupImageView(context);
                this.imageViews[i].getImageReceiver().setDelegate(new AudioPlayerAlert$CoverContainer$$ExternalSyntheticLambda2(this, i));
                this.imageViews[i].setRoundRadius(AndroidUtilities.dp(4.0f));
                if (i == 1) {
                    this.imageViews[i].setVisibility(8);
                }
                addView(this.imageViews[i], LayoutHelper.createFrame(-1, -1.0f));
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$0(int i, ImageReceiver imageReceiver, boolean z, boolean z2, boolean z3) {
            if (i == this.activeIndex) {
                onImageUpdated(imageReceiver);
            }
        }

        public final void switchImageViews() {
            AnimatorSet animatorSet2 = this.animatorSet;
            if (animatorSet2 != null) {
                animatorSet2.cancel();
            }
            this.animatorSet = new AnimatorSet();
            int i = this.activeIndex == 0 ? 1 : 0;
            this.activeIndex = i;
            BackupImageView[] backupImageViewArr = this.imageViews;
            final BackupImageView backupImageView = backupImageViewArr[i ^ 1];
            BackupImageView backupImageView2 = backupImageViewArr[i];
            boolean hasBitmapImage = backupImageView.getImageReceiver().hasBitmapImage();
            backupImageView2.setAlpha(hasBitmapImage ? 1.0f : 0.0f);
            backupImageView2.setScaleX(0.8f);
            backupImageView2.setScaleY(0.8f);
            backupImageView2.setVisibility(0);
            if (hasBitmapImage) {
                backupImageView.bringToFront();
            } else {
                backupImageView.setVisibility(8);
                backupImageView.setImageDrawable((Drawable) null);
            }
            ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.8f, 1.0f});
            ofFloat.setDuration(125);
            ofFloat.setInterpolator(CubicBezierInterpolator.EASE_OUT);
            ofFloat.addUpdateListener(new AudioPlayerAlert$CoverContainer$$ExternalSyntheticLambda1(backupImageView2, hasBitmapImage));
            if (hasBitmapImage) {
                ValueAnimator ofFloat2 = ValueAnimator.ofFloat(new float[]{backupImageView.getScaleX(), 0.8f});
                ofFloat2.setDuration(125);
                ofFloat2.setInterpolator(CubicBezierInterpolator.EASE_IN);
                ofFloat2.addUpdateListener(new AudioPlayerAlert$CoverContainer$$ExternalSyntheticLambda0(backupImageView, backupImageView2));
                ofFloat2.addListener(new AnimatorListenerAdapter(this) {
                    public void onAnimationEnd(Animator animator) {
                        backupImageView.setVisibility(8);
                        backupImageView.setImageDrawable((Drawable) null);
                        backupImageView.setAlpha(1.0f);
                    }
                });
                this.animatorSet.playSequentially(new Animator[]{ofFloat2, ofFloat});
            } else {
                this.animatorSet.play(ofFloat);
            }
            this.animatorSet.start();
        }

        /* access modifiers changed from: private */
        public static /* synthetic */ void lambda$switchImageViews$1(BackupImageView backupImageView, boolean z, ValueAnimator valueAnimator) {
            float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            backupImageView.setScaleX(floatValue);
            backupImageView.setScaleY(floatValue);
            if (!z) {
                backupImageView.setAlpha(valueAnimator.getAnimatedFraction());
            }
        }

        /* access modifiers changed from: private */
        public static /* synthetic */ void lambda$switchImageViews$2(BackupImageView backupImageView, BackupImageView backupImageView2, ValueAnimator valueAnimator) {
            float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            backupImageView.setScaleX(floatValue);
            backupImageView.setScaleY(floatValue);
            float animatedFraction = valueAnimator.getAnimatedFraction();
            if (animatedFraction > 0.25f && !backupImageView2.getImageReceiver().hasBitmapImage()) {
                backupImageView.setAlpha(1.0f - ((animatedFraction - 0.25f) * 1.3333334f));
            }
        }

        public final BackupImageView getImageView() {
            return this.imageViews[this.activeIndex];
        }

        public final BackupImageView getNextImageView() {
            return this.imageViews[this.activeIndex == 0 ? (char) 1 : 0];
        }

        public final ImageReceiver getImageReceiver() {
            return getImageView().getImageReceiver();
        }
    }

    public static abstract class ClippingTextViewSwitcher extends FrameLayout {
        private int activeIndex;
        private AnimatorSet animatorSet;
        private final float[] clipProgress = {0.0f, 0.75f};
        private final Paint erasePaint;
        private final Matrix gradientMatrix;
        private final Paint gradientPaint;
        private LinearGradient gradientShader;
        private final int gradientSize = AndroidUtilities.dp(24.0f);
        private final RectF rectF = new RectF();
        private int stableOffest = -1;
        /* access modifiers changed from: private */
        public final TextView[] textViews = new TextView[2];

        /* access modifiers changed from: protected */
        public abstract TextView createTextView();

        public ClippingTextViewSwitcher(Context context) {
            super(context);
            for (int i = 0; i < 2; i++) {
                this.textViews[i] = createTextView();
                if (i == 1) {
                    this.textViews[i].setAlpha(0.0f);
                    this.textViews[i].setVisibility(8);
                }
                addView(this.textViews[i], LayoutHelper.createFrame(-2, -1.0f));
            }
            this.gradientMatrix = new Matrix();
            Paint paint = new Paint(1);
            this.gradientPaint = paint;
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
            Paint paint2 = new Paint(1);
            this.erasePaint = paint2;
            paint2.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        }

        /* access modifiers changed from: protected */
        public void onSizeChanged(int i, int i2, int i3, int i4) {
            super.onSizeChanged(i, i2, i3, i4);
            LinearGradient linearGradient = new LinearGradient((float) this.gradientSize, 0.0f, 0.0f, 0.0f, 0, -16777216, Shader.TileMode.CLAMP);
            this.gradientShader = linearGradient;
            this.gradientPaint.setShader(linearGradient);
        }

        /* access modifiers changed from: protected */
        public boolean drawChild(Canvas canvas, View view, long j) {
            boolean z;
            Canvas canvas2 = canvas;
            TextView[] textViewArr = this.textViews;
            boolean z2 = true;
            int i = view == textViewArr[0] ? 0 : 1;
            if (this.stableOffest <= 0 || textViewArr[this.activeIndex].getAlpha() == 1.0f || this.textViews[this.activeIndex].getLayout() == null) {
                z = false;
            } else {
                float primaryHorizontal = this.textViews[this.activeIndex].getLayout().getPrimaryHorizontal(0);
                float primaryHorizontal2 = this.textViews[this.activeIndex].getLayout().getPrimaryHorizontal(this.stableOffest);
                if (primaryHorizontal == primaryHorizontal2) {
                    z2 = false;
                } else if (primaryHorizontal2 > primaryHorizontal) {
                    this.rectF.set(primaryHorizontal, 0.0f, primaryHorizontal2, (float) getMeasuredHeight());
                } else {
                    this.rectF.set(primaryHorizontal2, 0.0f, primaryHorizontal, (float) getMeasuredHeight());
                }
                if (z2 && i == this.activeIndex) {
                    canvas.save();
                    canvas2.clipRect(this.rectF);
                    this.textViews[0].draw(canvas2);
                    canvas.restore();
                }
                z = z2;
            }
            if (this.clipProgress[i] <= 0.0f && !z) {
                return super.drawChild(canvas, view, j);
            }
            float width = (float) view.getWidth();
            float height = (float) view.getHeight();
            float f = height;
            int saveLayer = canvas.saveLayer(0.0f, 0.0f, width, f, (Paint) null, 31);
            boolean drawChild = super.drawChild(canvas, view, j);
            float f2 = width * (1.0f - this.clipProgress[i]);
            float f3 = f2 + ((float) this.gradientSize);
            this.gradientMatrix.setTranslate(f2, 0.0f);
            this.gradientShader.setLocalMatrix(this.gradientMatrix);
            canvas.drawRect(f2, 0.0f, f3, f, this.gradientPaint);
            if (width > f3) {
                canvas.drawRect(f3, 0.0f, width, height, this.erasePaint);
            }
            if (z) {
                canvas2.drawRect(this.rectF, this.erasePaint);
            }
            canvas2.restoreToCount(saveLayer);
            return drawChild;
        }

        public void setText(CharSequence charSequence) {
            setText(charSequence, true);
        }

        public void setText(CharSequence charSequence, boolean z) {
            CharSequence text = this.textViews[this.activeIndex].getText();
            if (TextUtils.isEmpty(text) || !z) {
                this.textViews[this.activeIndex].setText(charSequence);
            } else if (!TextUtils.equals(charSequence, text)) {
                this.stableOffest = 0;
                int min = Math.min(charSequence.length(), text.length());
                int i = 0;
                while (i < min && charSequence.charAt(i) == text.charAt(i)) {
                    this.stableOffest++;
                    i++;
                }
                if (this.stableOffest <= 3) {
                    this.stableOffest = -1;
                }
                final int i2 = this.activeIndex;
                int i3 = i2 == 0 ? 1 : 0;
                this.activeIndex = i3;
                AnimatorSet animatorSet2 = this.animatorSet;
                if (animatorSet2 != null) {
                    animatorSet2.cancel();
                }
                AnimatorSet animatorSet3 = new AnimatorSet();
                this.animatorSet = animatorSet3;
                animatorSet3.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        ClippingTextViewSwitcher.this.textViews[i2].setVisibility(8);
                    }
                });
                this.textViews[i3].setText(charSequence);
                this.textViews[i3].bringToFront();
                this.textViews[i3].setVisibility(0);
                ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{this.clipProgress[i2], 0.75f});
                ofFloat.setDuration(200);
                ofFloat.addUpdateListener(new AudioPlayerAlert$ClippingTextViewSwitcher$$ExternalSyntheticLambda0(this, i2));
                ValueAnimator ofFloat2 = ValueAnimator.ofFloat(new float[]{this.clipProgress[i3], 0.0f});
                ofFloat2.setStartDelay(100);
                ofFloat2.setDuration(200);
                ofFloat2.addUpdateListener(new AudioPlayerAlert$ClippingTextViewSwitcher$$ExternalSyntheticLambda1(this, i3));
                ObjectAnimator ofFloat3 = ObjectAnimator.ofFloat(this.textViews[i2], View.ALPHA, new float[]{0.0f});
                ofFloat3.setStartDelay(75);
                ofFloat3.setDuration(150);
                ObjectAnimator ofFloat4 = ObjectAnimator.ofFloat(this.textViews[i3], View.ALPHA, new float[]{1.0f});
                ofFloat4.setStartDelay(75);
                ofFloat4.setDuration(150);
                this.animatorSet.playTogether(new Animator[]{ofFloat, ofFloat2, ofFloat3, ofFloat4});
                this.animatorSet.start();
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$setText$0(int i, ValueAnimator valueAnimator) {
            this.clipProgress[i] = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            invalidate();
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$setText$1(int i, ValueAnimator valueAnimator) {
            this.clipProgress[i] = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            invalidate();
        }

        public TextView getTextView() {
            return this.textViews[this.activeIndex];
        }

        public TextView getNextTextView() {
            return this.textViews[this.activeIndex == 0 ? (char) 1 : 0];
        }
    }
}
