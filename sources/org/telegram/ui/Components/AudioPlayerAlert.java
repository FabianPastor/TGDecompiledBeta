package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.net.Uri;
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
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
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
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.AlertDialog;
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
    private static final int menu_speed_fast = 3;
    private static final int menu_speed_normal = 2;
    private static final int menu_speed_slow = 1;
    private static final int menu_speed_veryfast = 4;
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
            long dt;
            long duration = MediaController.getInstance().getDuration();
            if (duration == 0) {
            } else if (duration == -9223372036854775807L) {
                long j = duration;
            } else {
                float currentProgress = AudioPlayerAlert.this.rewindingProgress;
                long t = System.currentTimeMillis();
                long dt2 = t - AudioPlayerAlert.this.lastRewindingTime;
                AudioPlayerAlert.this.lastRewindingTime = t;
                long updateDt = t - AudioPlayerAlert.this.lastUpdateRewindingPlayerTime;
                if (AudioPlayerAlert.this.rewindingForwardPressedCount == 1) {
                    dt = (3 * dt2) - dt2;
                } else if (AudioPlayerAlert.this.rewindingForwardPressedCount == 2) {
                    dt = (6 * dt2) - dt2;
                } else {
                    dt = (12 * dt2) - dt2;
                }
                float currentProgress2 = ((float) ((long) ((((float) duration) * currentProgress) + ((float) dt)))) / ((float) duration);
                if (currentProgress2 < 0.0f) {
                    currentProgress2 = 0.0f;
                }
                AudioPlayerAlert.this.rewindingProgress = currentProgress2;
                MessageObject messageObject = MediaController.getInstance().getPlayingMessageObject();
                if (messageObject != null && messageObject.isMusic()) {
                    if (!MediaController.getInstance().isMessagePaused()) {
                        MediaController.getInstance().getPlayingMessageObject().audioProgress = AudioPlayerAlert.this.rewindingProgress;
                    }
                    AudioPlayerAlert.this.updateProgress(messageObject);
                }
                if (AudioPlayerAlert.this.rewindingState != 1 || AudioPlayerAlert.this.rewindingForwardPressedCount <= 0 || !MediaController.getInstance().isMessagePaused()) {
                    return;
                }
                if (updateDt > 200 || AudioPlayerAlert.this.rewindingProgress == 0.0f) {
                    AudioPlayerAlert.this.lastUpdateRewindingPlayerTime = t;
                    MediaController.getInstance().seekToProgress(MediaController.getInstance().getPlayingMessageObject(), currentProgress2);
                }
                if (AudioPlayerAlert.this.rewindingForwardPressedCount <= 0 || AudioPlayerAlert.this.rewindingProgress <= 0.0f) {
                    return;
                }
                long j2 = duration;
                AndroidUtilities.runOnUIThread(AudioPlayerAlert.this.forwardSeek, 16);
                return;
            }
            AudioPlayerAlert.this.lastRewindingTime = System.currentTimeMillis();
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
    private int topBeforeSwitch;

    /* JADX WARNING: Illegal instructions before constructor call */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public AudioPlayerAlert(android.content.Context r41, org.telegram.ui.ActionBar.Theme.ResourcesProvider r42) {
        /*
            r40 = this;
            r0 = r40
            r8 = r41
            r9 = r42
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
            org.telegram.messenger.MessageObject r15 = r1.getPlayingMessageObject()
            if (r15 == 0) goto L_0x0038
            int r1 = r15.currentAccount
            r0.currentAccount = r1
            goto L_0x003c
        L_0x0038:
            int r1 = org.telegram.messenger.UserConfig.selectedAccount
            r0.currentAccount = r1
        L_0x003c:
            r1 = r8
            org.telegram.ui.LaunchActivity r1 = (org.telegram.ui.LaunchActivity) r1
            r0.parentActivity = r1
            int r1 = r0.currentAccount
            org.telegram.messenger.DownloadController r1 = org.telegram.messenger.DownloadController.getInstance(r1)
            int r1 = r1.generateObserverTag()
            r0.TAG = r1
            int r1 = r0.currentAccount
            org.telegram.messenger.NotificationCenter r1 = org.telegram.messenger.NotificationCenter.getInstance(r1)
            int r2 = org.telegram.messenger.NotificationCenter.messagePlayingDidReset
            r1.addObserver(r0, r2)
            int r1 = r0.currentAccount
            org.telegram.messenger.NotificationCenter r1 = org.telegram.messenger.NotificationCenter.getInstance(r1)
            int r2 = org.telegram.messenger.NotificationCenter.messagePlayingPlayStateChanged
            r1.addObserver(r0, r2)
            int r1 = r0.currentAccount
            org.telegram.messenger.NotificationCenter r1 = org.telegram.messenger.NotificationCenter.getInstance(r1)
            int r2 = org.telegram.messenger.NotificationCenter.messagePlayingDidStart
            r1.addObserver(r0, r2)
            int r1 = r0.currentAccount
            org.telegram.messenger.NotificationCenter r1 = org.telegram.messenger.NotificationCenter.getInstance(r1)
            int r2 = org.telegram.messenger.NotificationCenter.messagePlayingProgressDidChanged
            r1.addObserver(r0, r2)
            int r1 = r0.currentAccount
            org.telegram.messenger.NotificationCenter r1 = org.telegram.messenger.NotificationCenter.getInstance(r1)
            int r2 = org.telegram.messenger.NotificationCenter.fileLoaded
            r1.addObserver(r0, r2)
            int r1 = r0.currentAccount
            org.telegram.messenger.NotificationCenter r1 = org.telegram.messenger.NotificationCenter.getInstance(r1)
            int r2 = org.telegram.messenger.NotificationCenter.fileLoadProgressChanged
            r1.addObserver(r0, r2)
            int r1 = r0.currentAccount
            org.telegram.messenger.NotificationCenter r1 = org.telegram.messenger.NotificationCenter.getInstance(r1)
            int r2 = org.telegram.messenger.NotificationCenter.musicDidLoad
            r1.addObserver(r0, r2)
            int r1 = r0.currentAccount
            org.telegram.messenger.NotificationCenter r1 = org.telegram.messenger.NotificationCenter.getInstance(r1)
            int r2 = org.telegram.messenger.NotificationCenter.moreMusicDidLoad
            r1.addObserver(r0, r2)
            org.telegram.ui.Components.AudioPlayerAlert$2 r1 = new org.telegram.ui.Components.AudioPlayerAlert$2
            r1.<init>(r8)
            r0.containerView = r1
            android.view.ViewGroup r1 = r0.containerView
            r7 = 0
            r1.setWillNotDraw(r7)
            android.view.ViewGroup r1 = r0.containerView
            int r2 = r0.backgroundPaddingLeft
            int r3 = r0.backgroundPaddingLeft
            r1.setPadding(r2, r7, r3, r7)
            org.telegram.ui.Components.AudioPlayerAlert$3 r1 = new org.telegram.ui.Components.AudioPlayerAlert$3
            r1.<init>(r8, r9)
            r0.actionBar = r1
            java.lang.String r2 = "player_actionBar"
            int r2 = r0.getThemedColor(r2)
            r1.setBackgroundColor(r2)
            org.telegram.ui.ActionBar.ActionBar r1 = r0.actionBar
            r2 = 2131165503(0x7var_f, float:1.7945225E38)
            r1.setBackButtonImage(r2)
            org.telegram.ui.ActionBar.ActionBar r1 = r0.actionBar
            java.lang.String r2 = "player_actionBarTitle"
            int r3 = r0.getThemedColor(r2)
            r1.setItemsColor(r3, r7)
            org.telegram.ui.ActionBar.ActionBar r1 = r0.actionBar
            java.lang.String r3 = "player_actionBarSelector"
            int r3 = r0.getThemedColor(r3)
            r1.setItemsBackgroundColor(r3, r7)
            org.telegram.ui.ActionBar.ActionBar r1 = r0.actionBar
            int r3 = r0.getThemedColor(r2)
            r1.setTitleColor(r3)
            org.telegram.ui.ActionBar.ActionBar r1 = r0.actionBar
            r3 = 2131624442(0x7f0e01fa, float:1.8876064E38)
            java.lang.String r4 = "AttachMusic"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r1.setTitle(r3)
            org.telegram.ui.ActionBar.ActionBar r1 = r0.actionBar
            java.lang.String r3 = "player_actionBarSubtitle"
            int r3 = r0.getThemedColor(r3)
            r1.setSubtitleColor(r3)
            org.telegram.ui.ActionBar.ActionBar r1 = r0.actionBar
            r1.setOccupyStatusBar(r7)
            org.telegram.ui.ActionBar.ActionBar r1 = r0.actionBar
            r6 = 0
            r1.setAlpha(r6)
            if (r15 == 0) goto L_0x019c
            org.telegram.messenger.MediaController r1 = org.telegram.messenger.MediaController.getInstance()
            boolean r1 = r1.currentPlaylistIsGlobalSearch()
            if (r1 != 0) goto L_0x019c
            long r3 = r15.getDialogId()
            boolean r1 = org.telegram.messenger.DialogObject.isEncryptedDialog(r3)
            if (r1 == 0) goto L_0x0160
            int r1 = r0.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
            int r5 = org.telegram.messenger.DialogObject.getEncryptedChatId(r3)
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            org.telegram.tgnet.TLRPC$EncryptedChat r1 = r1.getEncryptedChat(r5)
            if (r1 == 0) goto L_0x015f
            int r5 = r0.currentAccount
            org.telegram.messenger.MessagesController r5 = org.telegram.messenger.MessagesController.getInstance(r5)
            long r13 = r1.user_id
            java.lang.Long r13 = java.lang.Long.valueOf(r13)
            org.telegram.tgnet.TLRPC$User r5 = r5.getUser(r13)
            if (r5 == 0) goto L_0x015f
            org.telegram.ui.ActionBar.ActionBar r13 = r0.actionBar
            java.lang.String r14 = r5.first_name
            java.lang.String r12 = r5.last_name
            java.lang.String r12 = org.telegram.messenger.ContactsController.formatName(r14, r12)
            r13.setTitle(r12)
        L_0x015f:
            goto L_0x019c
        L_0x0160:
            boolean r1 = org.telegram.messenger.DialogObject.isUserDialog(r3)
            if (r1 == 0) goto L_0x0184
            int r1 = r0.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
            java.lang.Long r5 = java.lang.Long.valueOf(r3)
            org.telegram.tgnet.TLRPC$User r1 = r1.getUser(r5)
            if (r1 == 0) goto L_0x0183
            org.telegram.ui.ActionBar.ActionBar r5 = r0.actionBar
            java.lang.String r12 = r1.first_name
            java.lang.String r13 = r1.last_name
            java.lang.String r12 = org.telegram.messenger.ContactsController.formatName(r12, r13)
            r5.setTitle(r12)
        L_0x0183:
            goto L_0x019c
        L_0x0184:
            int r1 = r0.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
            long r12 = -r3
            java.lang.Long r5 = java.lang.Long.valueOf(r12)
            org.telegram.tgnet.TLRPC$Chat r1 = r1.getChat(r5)
            if (r1 == 0) goto L_0x019c
            org.telegram.ui.ActionBar.ActionBar r5 = r0.actionBar
            java.lang.String r12 = r1.title
            r5.setTitle(r12)
        L_0x019c:
            org.telegram.ui.ActionBar.ActionBar r1 = r0.actionBar
            org.telegram.ui.ActionBar.ActionBarMenu r12 = r1.createMenu()
            r1 = 2131165513(0x7var_, float:1.7945245E38)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r12.addItem((int) r7, (int) r1)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r1.setIsSearchField(r10)
            org.telegram.ui.Components.AudioPlayerAlert$4 r3 = new org.telegram.ui.Components.AudioPlayerAlert$4
            r3.<init>()
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r1.setActionBarMenuItemSearchListener(r3)
            r0.searchItem = r1
            java.lang.String r3 = "Search"
            r4 = 2131627881(0x7f0e0var_, float:1.8883039E38)
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r3, r4)
            r1.setContentDescription(r5)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.searchItem
            org.telegram.ui.Components.EditTextBoldCursor r13 = r1.getSearchField()
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r3, r4)
            r13.setHint(r1)
            int r1 = r0.getThemedColor(r2)
            r13.setTextColor(r1)
            java.lang.String r1 = "player_time"
            int r3 = r0.getThemedColor(r1)
            r13.setHintTextColor(r3)
            int r2 = r0.getThemedColor(r2)
            r13.setCursorColor(r2)
            org.telegram.ui.ActionBar.ActionBar r2 = r0.actionBar
            org.telegram.ui.Components.AudioPlayerAlert$5 r3 = new org.telegram.ui.Components.AudioPlayerAlert$5
            r3.<init>()
            r2.setActionBarMenuOnItemClick(r3)
            android.view.View r2 = new android.view.View
            r2.<init>(r8)
            r0.actionBarShadow = r2
            r2.setAlpha(r6)
            android.view.View r2 = r0.actionBarShadow
            r3 = 2131165500(0x7var_c, float:1.7945219E38)
            r2.setBackgroundResource(r3)
            android.view.View r2 = new android.view.View
            r2.<init>(r8)
            r0.playerShadow = r2
            java.lang.String r3 = "dialogShadowLine"
            int r3 = r0.getThemedColor(r3)
            r2.setBackgroundColor(r3)
            org.telegram.ui.Components.AudioPlayerAlert$6 r2 = new org.telegram.ui.Components.AudioPlayerAlert$6
            r2.<init>(r8)
            r0.playerLayout = r2
            org.telegram.ui.Components.AudioPlayerAlert$7 r2 = new org.telegram.ui.Components.AudioPlayerAlert$7
            r2.<init>(r8)
            r0.coverContainer = r2
            android.widget.FrameLayout r3 = r0.playerLayout
            r16 = 44
            r17 = 1110441984(0x42300000, float:44.0)
            r18 = 53
            r19 = 0
            r20 = 1101004800(0x41a00000, float:20.0)
            r21 = 1101004800(0x41a00000, float:20.0)
            r22 = 0
            android.widget.FrameLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r16, r17, r18, r19, r20, r21, r22)
            r3.addView(r2, r4)
            org.telegram.ui.Components.AudioPlayerAlert$8 r2 = new org.telegram.ui.Components.AudioPlayerAlert$8
            r2.<init>(r8, r8)
            r0.titleTextView = r2
            android.widget.FrameLayout r3 = r0.playerLayout
            r16 = -1
            r17 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r18 = 51
            r19 = 1101004800(0x41a00000, float:20.0)
            r21 = 1116733440(0x42900000, float:72.0)
            android.widget.FrameLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r16, r17, r18, r19, r20, r21, r22)
            r3.addView(r2, r4)
            org.telegram.ui.Components.AudioPlayerAlert$9 r2 = new org.telegram.ui.Components.AudioPlayerAlert$9
            r2.<init>(r8, r8)
            r0.authorTextView = r2
            android.widget.FrameLayout r3 = r0.playerLayout
            r19 = 1096810496(0x41600000, float:14.0)
            r20 = 1111228416(0x423CLASSNAME, float:47.0)
            android.widget.FrameLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r16, r17, r18, r19, r20, r21, r22)
            r3.addView(r2, r4)
            org.telegram.ui.Components.AudioPlayerAlert$10 r2 = new org.telegram.ui.Components.AudioPlayerAlert$10
            r2.<init>(r8, r9)
            r0.seekBarView = r2
            org.telegram.ui.Components.AudioPlayerAlert$11 r3 = new org.telegram.ui.Components.AudioPlayerAlert$11
            r3.<init>()
            r2.setDelegate(r3)
            org.telegram.ui.Components.SeekBarView r2 = r0.seekBarView
            r2.setReportChanges(r10)
            android.widget.FrameLayout r2 = r0.playerLayout
            org.telegram.ui.Components.SeekBarView r3 = r0.seekBarView
            r17 = 1108869120(0x42180000, float:38.0)
            r19 = 1084227584(0x40a00000, float:5.0)
            r20 = 1116471296(0x428CLASSNAME, float:70.0)
            r21 = 1084227584(0x40a00000, float:5.0)
            android.widget.FrameLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r16, r17, r18, r19, r20, r21, r22)
            r2.addView(r3, r4)
            org.telegram.ui.Components.LineProgressView r2 = new org.telegram.ui.Components.LineProgressView
            r2.<init>(r8)
            r0.progressView = r2
            r2.setVisibility(r11)
            org.telegram.ui.Components.LineProgressView r2 = r0.progressView
            java.lang.String r3 = "player_progressBackground"
            int r3 = r0.getThemedColor(r3)
            r2.setBackgroundColor(r3)
            org.telegram.ui.Components.LineProgressView r2 = r0.progressView
            java.lang.String r3 = "player_progress"
            int r3 = r0.getThemedColor(r3)
            r2.setProgressColor(r3)
            android.widget.FrameLayout r2 = r0.playerLayout
            org.telegram.ui.Components.LineProgressView r3 = r0.progressView
            r17 = 1073741824(0x40000000, float:2.0)
            r19 = 1101529088(0x41a80000, float:21.0)
            r20 = 1119092736(0x42b40000, float:90.0)
            r21 = 1101529088(0x41a80000, float:21.0)
            android.widget.FrameLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r16, r17, r18, r19, r20, r21, r22)
            r2.addView(r3, r4)
            org.telegram.ui.ActionBar.SimpleTextView r2 = new org.telegram.ui.ActionBar.SimpleTextView
            r2.<init>(r8)
            r0.timeTextView = r2
            r3 = 12
            r2.setTextSize(r3)
            org.telegram.ui.ActionBar.SimpleTextView r2 = r0.timeTextView
            java.lang.String r3 = "0:00"
            r2.setText(r3)
            org.telegram.ui.ActionBar.SimpleTextView r2 = r0.timeTextView
            int r3 = r0.getThemedColor(r1)
            r2.setTextColor(r3)
            org.telegram.ui.ActionBar.SimpleTextView r2 = r0.timeTextView
            r14 = 2
            r2.setImportantForAccessibility(r14)
            android.widget.FrameLayout r2 = r0.playerLayout
            org.telegram.ui.ActionBar.SimpleTextView r3 = r0.timeTextView
            r16 = 100
            r17 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r19 = 1101004800(0x41a00000, float:20.0)
            r20 = 1120141312(0x42CLASSNAME, float:98.0)
            r21 = 0
            android.widget.FrameLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r16, r17, r18, r19, r20, r21, r22)
            r2.addView(r3, r4)
            android.widget.TextView r2 = new android.widget.TextView
            r2.<init>(r8)
            r0.durationTextView = r2
            r3 = 1094713344(0x41400000, float:12.0)
            r2.setTextSize(r10, r3)
            android.widget.TextView r2 = r0.durationTextView
            int r1 = r0.getThemedColor(r1)
            r2.setTextColor(r1)
            android.widget.TextView r1 = r0.durationTextView
            r5 = 17
            r1.setGravity(r5)
            android.widget.TextView r1 = r0.durationTextView
            r1.setImportantForAccessibility(r14)
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
            r2 = r41
            r11 = r4
            r4 = r16
            r5 = r17
            r6 = r18
            r14 = 0
            r7 = r42
            r1.<init>(r2, r3, r4, r5, r6, r7)
            r0.playbackSpeedButton = r11
            r11.setLongClickEnabled(r14)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.playbackSpeedButton
            r1.setShowSubmenuByMove(r14)
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
            org.telegram.ui.Components.AudioPlayerAlert$$ExternalSyntheticLambda10 r2 = new org.telegram.ui.Components.AudioPlayerAlert$$ExternalSyntheticLambda10
            r2.<init>(r0)
            r1.setDelegate(r2)
            org.telegram.ui.ActionBar.ActionBarMenuSubItem[] r1 = r0.speedItems
            org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r0.playbackSpeedButton
            r3 = 2131165904(0x7var_d0, float:1.7946038E38)
            r4 = 2131628185(0x7f0e1099, float:1.8883656E38)
            java.lang.String r5 = "SpeedSlow"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r2 = r2.addSubItem((int) r10, (int) r3, (java.lang.CharSequence) r4)
            r1[r14] = r2
            org.telegram.ui.ActionBar.ActionBarMenuSubItem[] r1 = r0.speedItems
            org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r0.playbackSpeedButton
            r3 = 2131165905(0x7var_d1, float:1.794604E38)
            r4 = 2131628184(0x7f0e1098, float:1.8883654E38)
            java.lang.String r5 = "SpeedNormal"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r5 = 2
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r2 = r2.addSubItem((int) r5, (int) r3, (java.lang.CharSequence) r4)
            r1[r10] = r2
            org.telegram.ui.ActionBar.ActionBarMenuSubItem[] r1 = r0.speedItems
            org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r0.playbackSpeedButton
            r3 = 2131165906(0x7var_d2, float:1.7946042E38)
            r4 = 2131628183(0x7f0e1097, float:1.8883651E38)
            java.lang.String r6 = "SpeedFast"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r6, r4)
            r11 = 3
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r2 = r2.addSubItem((int) r11, (int) r3, (java.lang.CharSequence) r4)
            r1[r5] = r2
            org.telegram.ui.ActionBar.ActionBarMenuSubItem[] r1 = r0.speedItems
            org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r0.playbackSpeedButton
            r3 = 2131165907(0x7var_d3, float:1.7946044E38)
            r4 = 2131628186(0x7f0e109a, float:1.8883658E38)
            java.lang.String r5 = "SpeedVeryFast"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r5 = 4
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r2 = r2.addSubItem((int) r5, (int) r3, (java.lang.CharSequence) r4)
            r1[r11] = r2
            float r1 = org.telegram.messenger.AndroidUtilities.density
            r7 = 1077936128(0x40400000, float:3.0)
            int r1 = (r1 > r7 ? 1 : (r1 == r7 ? 0 : -1))
            if (r1 < 0) goto L_0x03e1
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.playbackSpeedButton
            r1.setPadding(r14, r10, r14, r14)
        L_0x03e1:
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.playbackSpeedButton
            r17 = 1090519040(0x41000000, float:8.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r17)
            r1.setAdditionalXOffset(r2)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.playbackSpeedButton
            r1.setShowedFromBottom(r10)
            android.widget.FrameLayout r1 = r0.playerLayout
            org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r0.playbackSpeedButton
            r25 = 36
            r26 = 1108344832(0x42100000, float:36.0)
            r27 = 53
            r28 = 0
            r29 = 1118568448(0x42aCLASSNAME, float:86.0)
            r30 = 1101004800(0x41a00000, float:20.0)
            r31 = 0
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r25, r26, r27, r28, r29, r30, r31)
            r1.addView(r2, r3)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.playbackSpeedButton
            org.telegram.ui.Components.AudioPlayerAlert$$ExternalSyntheticLambda0 r2 = new org.telegram.ui.Components.AudioPlayerAlert$$ExternalSyntheticLambda0
            r2.<init>(r0)
            r1.setOnClickListener(r2)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.playbackSpeedButton
            org.telegram.ui.Components.AudioPlayerAlert$$ExternalSyntheticLambda7 r2 = new org.telegram.ui.Components.AudioPlayerAlert$$ExternalSyntheticLambda7
            r2.<init>(r0)
            r1.setOnLongClickListener(r2)
            r40.updatePlaybackButton()
            org.telegram.ui.Components.AudioPlayerAlert$12 r1 = new org.telegram.ui.Components.AudioPlayerAlert$12
            r1.<init>(r8)
            r6 = r1
            android.widget.FrameLayout r1 = r0.playerLayout
            r25 = -1
            r26 = 1115947008(0x42840000, float:66.0)
            r27 = 51
            r29 = 1121845248(0x42de0000, float:111.0)
            r30 = 0
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r25, r26, r27, r28, r29, r30, r31)
            r1.addView(r6, r2)
            android.view.View[] r5 = r0.buttons
            org.telegram.ui.ActionBar.ActionBarMenuItem r4 = new org.telegram.ui.ActionBar.ActionBarMenuItem
            r3 = 0
            r18 = 0
            r20 = 0
            r21 = 0
            r1 = r4
            r2 = r41
            r11 = r4
            r4 = r18
            r18 = r5
            r5 = r20
            r32 = r6
            r6 = r21
            r7 = r42
            r1.<init>(r2, r3, r4, r5, r6, r7)
            r0.repeatButton = r11
            r18[r14] = r11
            r11.setLongClickEnabled(r14)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.repeatButton
            r1.setShowSubmenuByMove(r14)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.repeatButton
            r2 = 1126563840(0x43260000, float:166.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r2 = -r2
            r1.setAdditionalYOffset(r2)
            int r1 = android.os.Build.VERSION.SDK_INT
            java.lang.String r11 = "listSelectorSDK21"
            r7 = 21
            if (r1 < r7) goto L_0x048b
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.repeatButton
            int r2 = r0.getThemedColor(r11)
            r3 = 1099956224(0x41900000, float:18.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r2, r10, r3)
            r1.setBackgroundDrawable(r2)
        L_0x048b:
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.repeatButton
            r6 = 48
            r5 = 51
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r6, (int) r6, (int) r5)
            r4 = r32
            r4.addView(r1, r2)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.repeatButton
            org.telegram.ui.Components.AudioPlayerAlert$$ExternalSyntheticLambda4 r2 = new org.telegram.ui.Components.AudioPlayerAlert$$ExternalSyntheticLambda4
            r2.<init>(r0)
            r1.setOnClickListener(r2)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.repeatButton
            r2 = 2131166048(0x7var_, float:1.794633E38)
            r3 = 2131627722(0x7f0e0eca, float:1.8882716E38)
            java.lang.String r14 = "RepeatSong"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r14, r3)
            r14 = 3
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r1 = r1.addSubItem((int) r14, (int) r2, (java.lang.CharSequence) r3)
            r0.repeatSongItem = r1
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.repeatButton
            r2 = 2131166047(0x7var_f, float:1.7946328E38)
            r3 = 2131627719(0x7f0e0ec7, float:1.888271E38)
            java.lang.String r14 = "RepeatList"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r14, r3)
            r14 = 4
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r1 = r1.addSubItem((int) r14, (int) r2, (java.lang.CharSequence) r3)
            r0.repeatListItem = r1
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.repeatButton
            r2 = 2131166049(0x7var_, float:1.7946332E38)
            r3 = 2131628140(0x7f0e106c, float:1.8883564E38)
            java.lang.String r14 = "ShuffleList"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r14, r3)
            r14 = 2
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r1 = r1.addSubItem((int) r14, (int) r2, (java.lang.CharSequence) r3)
            r0.shuffleListItem = r1
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.repeatButton
            r2 = 2131166041(0x7var_, float:1.7946316E38)
            r3 = 2131627841(0x7f0e0var_, float:1.8882958E38)
            java.lang.String r14 = "ReverseOrder"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r14, r3)
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r1 = r1.addSubItem((int) r10, (int) r2, (java.lang.CharSequence) r3)
            r0.reverseOrderItem = r1
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.repeatButton
            r1.setShowedFromBottom(r10)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.repeatButton
            org.telegram.ui.Components.AudioPlayerAlert$$ExternalSyntheticLambda11 r2 = new org.telegram.ui.Components.AudioPlayerAlert$$ExternalSyntheticLambda11
            r2.<init>(r0)
            r1.setDelegate(r2)
            java.lang.String r1 = "player_button"
            int r14 = r0.getThemedColor(r1)
            android.view.ViewConfiguration r1 = android.view.ViewConfiguration.get(r41)
            int r1 = r1.getScaledTouchSlop()
            float r3 = (float) r1
            android.view.View[] r1 = r0.buttons
            org.telegram.ui.Components.AudioPlayerAlert$13 r2 = new org.telegram.ui.Components.AudioPlayerAlert$13
            r2.<init>(r8, r3)
            r0.prevButton = r2
            r1[r10] = r2
            android.widget.ImageView$ScaleType r1 = android.widget.ImageView.ScaleType.CENTER
            r2.setScaleType(r1)
            org.telegram.ui.Components.RLottieImageView r1 = r0.prevButton
            r2 = 2131558501(0x7f0d0065, float:1.874232E38)
            r5 = 20
            r1.setAnimation(r2, r5, r5)
            org.telegram.ui.Components.RLottieImageView r1 = r0.prevButton
            java.lang.String r2 = "Triangle 3.**"
            r1.setLayerColor(r2, r14)
            org.telegram.ui.Components.RLottieImageView r1 = r0.prevButton
            java.lang.String r2 = "Triangle 4.**"
            r1.setLayerColor(r2, r14)
            org.telegram.ui.Components.RLottieImageView r1 = r0.prevButton
            java.lang.String r2 = "Rectangle 4.**"
            r1.setLayerColor(r2, r14)
            int r1 = android.os.Build.VERSION.SDK_INT
            if (r1 < r7) goto L_0x055b
            org.telegram.ui.Components.RLottieImageView r1 = r0.prevButton
            int r2 = r0.getThemedColor(r11)
            r21 = 1102053376(0x41b00000, float:22.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r21)
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r2, r10, r5)
            r1.setBackgroundDrawable(r2)
        L_0x055b:
            org.telegram.ui.Components.RLottieImageView r1 = r0.prevButton
            r2 = 51
            android.widget.FrameLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r6, (int) r6, (int) r2)
            r4.addView(r1, r5)
            org.telegram.ui.Components.RLottieImageView r1 = r0.prevButton
            r2 = 2131624014(0x7f0e004e, float:1.8875196E38)
            java.lang.String r5 = "AccDescrPrevious"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r5, r2)
            r1.setContentDescription(r2)
            android.view.View[] r1 = r0.buttons
            android.widget.ImageView r2 = new android.widget.ImageView
            r2.<init>(r8)
            r0.playButton = r2
            r5 = 2
            r1[r5] = r2
            android.widget.ImageView$ScaleType r1 = android.widget.ImageView.ScaleType.CENTER
            r2.setScaleType(r1)
            android.widget.ImageView r1 = r0.playButton
            org.telegram.ui.Components.PlayPauseDrawable r2 = new org.telegram.ui.Components.PlayPauseDrawable
            r5 = 28
            r2.<init>(r5)
            r0.playPauseDrawable = r2
            r1.setImageDrawable(r2)
            org.telegram.ui.Components.PlayPauseDrawable r1 = r0.playPauseDrawable
            org.telegram.messenger.MediaController r2 = org.telegram.messenger.MediaController.getInstance()
            boolean r2 = r2.isMessagePaused()
            r2 = r2 ^ r10
            r5 = 0
            r1.setPause(r2, r5)
            android.widget.ImageView r1 = r0.playButton
            android.graphics.PorterDuffColorFilter r2 = new android.graphics.PorterDuffColorFilter
            java.lang.String r5 = "player_button"
            int r5 = r0.getThemedColor(r5)
            android.graphics.PorterDuff$Mode r6 = android.graphics.PorterDuff.Mode.MULTIPLY
            r2.<init>(r5, r6)
            r1.setColorFilter(r2)
            int r1 = android.os.Build.VERSION.SDK_INT
            if (r1 < r7) goto L_0x05cb
            android.widget.ImageView r1 = r0.playButton
            int r2 = r0.getThemedColor(r11)
            r5 = 1103101952(0x41CLASSNAME, float:24.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r2, r10, r5)
            r1.setBackgroundDrawable(r2)
        L_0x05cb:
            android.widget.ImageView r1 = r0.playButton
            r2 = 51
            r5 = 48
            android.widget.FrameLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r5, (int) r5, (int) r2)
            r4.addView(r1, r6)
            android.widget.ImageView r1 = r0.playButton
            org.telegram.ui.Components.AudioPlayerAlert$$ExternalSyntheticLambda6 r2 = org.telegram.ui.Components.AudioPlayerAlert$$ExternalSyntheticLambda6.INSTANCE
            r1.setOnClickListener(r2)
            android.view.View[] r1 = r0.buttons
            org.telegram.ui.Components.AudioPlayerAlert$14 r2 = new org.telegram.ui.Components.AudioPlayerAlert$14
            r2.<init>(r8, r3)
            r0.nextButton = r2
            r5 = 3
            r1[r5] = r2
            android.widget.ImageView$ScaleType r1 = android.widget.ImageView.ScaleType.CENTER
            r2.setScaleType(r1)
            org.telegram.ui.Components.RLottieImageView r1 = r0.nextButton
            r2 = 2131558501(0x7f0d0065, float:1.874232E38)
            r5 = 20
            r1.setAnimation(r2, r5, r5)
            org.telegram.ui.Components.RLottieImageView r1 = r0.nextButton
            java.lang.String r2 = "Triangle 3.**"
            r1.setLayerColor(r2, r14)
            org.telegram.ui.Components.RLottieImageView r1 = r0.nextButton
            java.lang.String r2 = "Triangle 4.**"
            r1.setLayerColor(r2, r14)
            org.telegram.ui.Components.RLottieImageView r1 = r0.nextButton
            java.lang.String r2 = "Rectangle 4.**"
            r1.setLayerColor(r2, r14)
            org.telegram.ui.Components.RLottieImageView r1 = r0.nextButton
            r2 = 1127481344(0x43340000, float:180.0)
            r1.setRotation(r2)
            int r1 = android.os.Build.VERSION.SDK_INT
            if (r1 < r7) goto L_0x062d
            org.telegram.ui.Components.RLottieImageView r1 = r0.nextButton
            int r2 = r0.getThemedColor(r11)
            r5 = 1102053376(0x41b00000, float:22.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r2, r10, r5)
            r1.setBackgroundDrawable(r2)
        L_0x062d:
            org.telegram.ui.Components.RLottieImageView r1 = r0.nextButton
            r5 = 51
            r6 = 48
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r6, (int) r6, (int) r5)
            r4.addView(r1, r2)
            org.telegram.ui.Components.RLottieImageView r1 = r0.nextButton
            r2 = 2131626648(0x7f0e0a98, float:1.8880538E38)
            java.lang.String r5 = "Next"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r5, r2)
            r1.setContentDescription(r2)
            android.view.View[] r5 = r0.buttons
            org.telegram.ui.ActionBar.ActionBarMenuItem r2 = new org.telegram.ui.ActionBar.ActionBarMenuItem
            r21 = 0
            r22 = 0
            r25 = 0
            r1 = r2
            r10 = r2
            r2 = r41
            r27 = r3
            r3 = r21
            r33 = r4
            r4 = r22
            r20 = r5
            r5 = r14
            r6 = r25
            r9 = 21
            r7 = r42
            r1.<init>(r2, r3, r4, r5, r6, r7)
            r0.optionsButton = r10
            r1 = 4
            r20[r1] = r10
            r1 = 0
            r10.setLongClickEnabled(r1)
            org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r0.optionsButton
            r2.setShowSubmenuByMove(r1)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.optionsButton
            r2 = 2131165510(0x7var_, float:1.794524E38)
            r1.setIcon((int) r2)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.optionsButton
            r2 = 2
            r1.setSubMenuOpenSide(r2)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.optionsButton
            r2 = 1125974016(0x431d0000, float:157.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r2 = -r2
            r1.setAdditionalYOffset(r2)
            int r1 = android.os.Build.VERSION.SDK_INT
            if (r1 < r9) goto L_0x06aa
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.optionsButton
            int r2 = r0.getThemedColor(r11)
            r3 = 1099956224(0x41900000, float:18.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r4 = 1
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r2, r4, r3)
            r1.setBackgroundDrawable(r2)
        L_0x06aa:
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.optionsButton
            r2 = 51
            r3 = 48
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r3, (int) r3, (int) r2)
            r4 = r33
            r4.addView(r1, r3)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.optionsButton
            r3 = 2131165796(0x7var_, float:1.794582E38)
            r5 = 2131625839(0x7f0e076f, float:1.8878897E38)
            java.lang.String r6 = "Forward"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r6, r5)
            r6 = 1
            r1.addSubItem((int) r6, (int) r3, (java.lang.CharSequence) r5)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.optionsButton
            r3 = 2131165900(0x7var_cc, float:1.794603E38)
            r5 = 2131628061(0x7f0e101d, float:1.8883404E38)
            java.lang.String r6 = "ShareFile"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r6, r5)
            r6 = 2
            r1.addSubItem((int) r6, (int) r3, (java.lang.CharSequence) r5)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.optionsButton
            r3 = 2131165788(0x7var_c, float:1.7945803E38)
            r5 = 2131627865(0x7f0e0var_, float:1.8883006E38)
            java.lang.String r6 = "SaveToMusic"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r6, r5)
            r6 = 5
            r1.addSubItem((int) r6, (int) r3, (java.lang.CharSequence) r5)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.optionsButton
            r3 = 2131165828(0x7var_, float:1.7945884E38)
            r5 = 2131628125(0x7f0e105d, float:1.8883534E38)
            java.lang.String r6 = "ShowInChat"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r6, r5)
            r6 = 4
            r1.addSubItem((int) r6, (int) r3, (java.lang.CharSequence) r5)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.optionsButton
            r3 = 1
            r1.setShowedFromBottom(r3)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.optionsButton
            org.telegram.ui.Components.AudioPlayerAlert$$ExternalSyntheticLambda5 r3 = new org.telegram.ui.Components.AudioPlayerAlert$$ExternalSyntheticLambda5
            r3.<init>(r0)
            r1.setOnClickListener(r3)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.optionsButton
            org.telegram.ui.Components.AudioPlayerAlert$$ExternalSyntheticLambda12 r3 = new org.telegram.ui.Components.AudioPlayerAlert$$ExternalSyntheticLambda12
            r3.<init>(r0)
            r1.setDelegate(r3)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.optionsButton
            r3 = 2131623987(0x7f0e0033, float:1.887514E38)
            java.lang.String r5 = "AccDescrMoreOptions"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r5, r3)
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
            r5 = 8
            r1.setVisibility(r5)
            android.view.ViewGroup r1 = r0.containerView
            android.widget.LinearLayout r5 = r0.emptyView
            r6 = -1082130432(0xffffffffbvar_, float:-1.0)
            r7 = -1
            android.widget.FrameLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createFrame(r7, r6)
            r1.addView(r5, r6)
            android.widget.LinearLayout r1 = r0.emptyView
            org.telegram.ui.Components.AudioPlayerAlert$$ExternalSyntheticLambda8 r5 = org.telegram.ui.Components.AudioPlayerAlert$$ExternalSyntheticLambda8.INSTANCE
            r1.setOnTouchListener(r5)
            android.widget.ImageView r1 = new android.widget.ImageView
            r1.<init>(r8)
            r0.emptyImageView = r1
            r5 = 2131165948(0x7var_fc, float:1.7946128E38)
            r1.setImageResource(r5)
            android.widget.ImageView r1 = r0.emptyImageView
            android.graphics.PorterDuffColorFilter r5 = new android.graphics.PorterDuffColorFilter
            java.lang.String r6 = "dialogEmptyImage"
            int r6 = r0.getThemedColor(r6)
            android.graphics.PorterDuff$Mode r7 = android.graphics.PorterDuff.Mode.MULTIPLY
            r5.<init>(r6, r7)
            r1.setColorFilter(r5)
            android.widget.LinearLayout r1 = r0.emptyView
            android.widget.ImageView r5 = r0.emptyImageView
            r6 = -2
            r7 = -2
            android.widget.LinearLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createLinear(r6, r7)
            r1.addView(r5, r6)
            android.widget.TextView r1 = new android.widget.TextView
            r1.<init>(r8)
            r0.emptyTitleTextView = r1
            java.lang.String r5 = "dialogEmptyText"
            int r5 = r0.getThemedColor(r5)
            r1.setTextColor(r5)
            android.widget.TextView r1 = r0.emptyTitleTextView
            r1.setGravity(r3)
            android.widget.TextView r1 = r0.emptyTitleTextView
            r5 = 2131626651(0x7f0e0a9b, float:1.8880544E38)
            java.lang.String r6 = "NoAudioFound"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r6, r5)
            r1.setText(r5)
            android.widget.TextView r1 = r0.emptyTitleTextView
            java.lang.String r5 = "fonts/rmedium.ttf"
            android.graphics.Typeface r5 = org.telegram.messenger.AndroidUtilities.getTypeface(r5)
            r1.setTypeface(r5)
            android.widget.TextView r1 = r0.emptyTitleTextView
            r5 = 1099431936(0x41880000, float:17.0)
            r6 = 1
            r1.setTextSize(r6, r5)
            android.widget.TextView r1 = r0.emptyTitleTextView
            r5 = 1109393408(0x42200000, float:40.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r9 = 0
            r1.setPadding(r6, r9, r7, r9)
            android.widget.LinearLayout r1 = r0.emptyView
            android.widget.TextView r6 = r0.emptyTitleTextView
            r33 = -2
            r34 = -2
            r35 = 17
            r36 = 0
            r37 = 11
            r38 = 0
            r39 = 0
            android.widget.LinearLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r33, (int) r34, (int) r35, (int) r36, (int) r37, (int) r38, (int) r39)
            r1.addView(r6, r7)
            android.widget.TextView r1 = new android.widget.TextView
            r1.<init>(r8)
            r0.emptySubtitleTextView = r1
            java.lang.String r6 = "dialogEmptyText"
            int r6 = r0.getThemedColor(r6)
            r1.setTextColor(r6)
            android.widget.TextView r1 = r0.emptySubtitleTextView
            r1.setGravity(r3)
            android.widget.TextView r1 = r0.emptySubtitleTextView
            r3 = 1097859072(0x41700000, float:15.0)
            r6 = 1
            r1.setTextSize(r6, r3)
            android.widget.TextView r1 = r0.emptySubtitleTextView
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r6 = 0
            r1.setPadding(r3, r6, r5, r6)
            android.widget.LinearLayout r1 = r0.emptyView
            android.widget.TextView r3 = r0.emptySubtitleTextView
            r37 = 6
            android.widget.LinearLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r33, (int) r34, (int) r35, (int) r36, (int) r37, (int) r38, (int) r39)
            r1.addView(r3, r5)
            org.telegram.ui.Components.AudioPlayerAlert$15 r1 = new org.telegram.ui.Components.AudioPlayerAlert$15
            r1.<init>(r8)
            r0.listView = r1
            r3 = 0
            r1.setClipToPadding(r3)
            org.telegram.ui.Components.RecyclerListView r1 = r0.listView
            androidx.recyclerview.widget.LinearLayoutManager r5 = new androidx.recyclerview.widget.LinearLayoutManager
            android.content.Context r6 = r40.getContext()
            r7 = 1
            r5.<init>(r6, r7, r3)
            r0.layoutManager = r5
            r1.setLayoutManager(r5)
            org.telegram.ui.Components.RecyclerListView r1 = r0.listView
            r1.setHorizontalScrollBarEnabled(r3)
            org.telegram.ui.Components.RecyclerListView r1 = r0.listView
            r1.setVerticalScrollBarEnabled(r3)
            android.view.ViewGroup r1 = r0.containerView
            org.telegram.ui.Components.RecyclerListView r3 = r0.listView
            r5 = -1
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r5, (int) r5, (int) r2)
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
            org.telegram.ui.Components.AudioPlayerAlert$$ExternalSyntheticLambda2 r2 = org.telegram.ui.Components.AudioPlayerAlert$$ExternalSyntheticLambda2.INSTANCE
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
            r5 = 83
            r6 = -1
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r6, (int) r3, (int) r5)
            r1.addView(r2, r3)
            android.view.ViewGroup r1 = r0.containerView
            android.view.View r2 = r0.playerShadow
            android.widget.FrameLayout$LayoutParams r3 = new android.widget.FrameLayout$LayoutParams
            int r5 = org.telegram.messenger.AndroidUtilities.getShadowHeight()
            r7 = 83
            r3.<init>(r6, r5, r7)
            r1.addView(r2, r3)
            android.view.View r1 = r0.playerShadow
            android.view.ViewGroup$LayoutParams r1 = r1.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r1 = (android.widget.FrameLayout.LayoutParams) r1
            r2 = 1127415808(0x43330000, float:179.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r1.bottomMargin = r2
            android.view.ViewGroup r2 = r0.containerView
            android.view.View r3 = r0.actionBarShadow
            r5 = 1077936128(0x40400000, float:3.0)
            r6 = -1
            android.widget.FrameLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createFrame(r6, r5)
            r2.addView(r3, r5)
            android.view.ViewGroup r2 = r0.containerView
            org.telegram.ui.ActionBar.ActionBar r3 = r0.actionBar
            r2.addView(r3)
            org.telegram.ui.Components.AudioPlayerAlert$17 r2 = new org.telegram.ui.Components.AudioPlayerAlert$17
            r2.<init>(r8)
            r0.blurredView = r2
            r3 = 0
            r2.setAlpha(r3)
            android.widget.FrameLayout r2 = r0.blurredView
            r3 = 4
            r2.setVisibility(r3)
            android.widget.FrameLayout r2 = r40.getContainer()
            android.widget.FrameLayout r3 = r0.blurredView
            r2.addView(r3)
            org.telegram.ui.Components.BackupImageView r2 = new org.telegram.ui.Components.BackupImageView
            r2.<init>(r8)
            r0.bigAlbumConver = r2
            r3 = 1
            r2.setAspectFit(r3)
            org.telegram.ui.Components.BackupImageView r2 = r0.bigAlbumConver
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r17)
            r2.setRoundRadius(r3)
            org.telegram.ui.Components.BackupImageView r2 = r0.bigAlbumConver
            r3 = 1063675494(0x3var_, float:0.9)
            r2.setScaleX(r3)
            org.telegram.ui.Components.BackupImageView r2 = r0.bigAlbumConver
            r2.setScaleY(r3)
            android.widget.FrameLayout r2 = r0.blurredView
            org.telegram.ui.Components.BackupImageView r3 = r0.bigAlbumConver
            r19 = -1
            r20 = -1082130432(0xffffffffbvar_, float:-1.0)
            r21 = 51
            r22 = 1106247680(0x41var_, float:30.0)
            r23 = 1106247680(0x41var_, float:30.0)
            r24 = 1106247680(0x41var_, float:30.0)
            r25 = 1106247680(0x41var_, float:30.0)
            android.widget.FrameLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createFrame(r19, r20, r21, r22, r23, r24, r25)
            r2.addView(r3, r5)
            r2 = 0
            r0.updateTitle(r2)
            r40.updateRepeatButton()
            r40.updateEmptyView()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.AudioPlayerAlert.<init>(android.content.Context, org.telegram.ui.ActionBar.Theme$ResourcesProvider):void");
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-AudioPlayerAlert  reason: not valid java name */
    public /* synthetic */ void m3599lambda$new$0$orgtelegramuiComponentsAudioPlayerAlert(int id) {
        float playbackSpeed = MediaController.getInstance().getPlaybackSpeed(true);
        if (id == 1) {
            MediaController.getInstance().setPlaybackSpeed(true, 0.5f);
        } else if (id == 2) {
            MediaController.getInstance().setPlaybackSpeed(true, 1.0f);
        } else if (id == 3) {
            MediaController.getInstance().setPlaybackSpeed(true, 1.5f);
        } else {
            MediaController.getInstance().setPlaybackSpeed(true, 1.8f);
        }
        updatePlaybackButton();
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Components-AudioPlayerAlert  reason: not valid java name */
    public /* synthetic */ void m3600lambda$new$1$orgtelegramuiComponentsAudioPlayerAlert(View v) {
        if (Math.abs(MediaController.getInstance().getPlaybackSpeed(true) - 1.0f) > 0.001f) {
            MediaController.getInstance().setPlaybackSpeed(true, 1.0f);
        } else {
            MediaController.getInstance().setPlaybackSpeed(true, MediaController.getInstance().getFastPlaybackSpeed(true));
        }
        updatePlaybackButton();
    }

    /* renamed from: lambda$new$2$org-telegram-ui-Components-AudioPlayerAlert  reason: not valid java name */
    public /* synthetic */ boolean m3601lambda$new$2$orgtelegramuiComponentsAudioPlayerAlert(View view) {
        this.playbackSpeedButton.toggleSubMenu();
        return true;
    }

    /* renamed from: lambda$new$3$org-telegram-ui-Components-AudioPlayerAlert  reason: not valid java name */
    public /* synthetic */ void m3602lambda$new$3$orgtelegramuiComponentsAudioPlayerAlert(View v) {
        updateSubMenu();
        this.repeatButton.toggleSubMenu();
    }

    /* renamed from: lambda$new$4$org-telegram-ui-Components-AudioPlayerAlert  reason: not valid java name */
    public /* synthetic */ void m3603lambda$new$4$orgtelegramuiComponentsAudioPlayerAlert(int id) {
        if (id == 1 || id == 2) {
            boolean oldReversed = SharedConfig.playOrderReversed;
            if ((!SharedConfig.playOrderReversed || id != 1) && (!SharedConfig.shuffleMusic || id != 2)) {
                MediaController.getInstance().setPlaybackOrderType(id);
            } else {
                MediaController.getInstance().setPlaybackOrderType(0);
            }
            this.listAdapter.notifyDataSetChanged();
            if (oldReversed != SharedConfig.playOrderReversed) {
                this.listView.stopScroll();
                scrollToCurrentSong(false);
            }
        } else if (id == 4) {
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

    static /* synthetic */ void lambda$new$5(View v) {
        if (!MediaController.getInstance().isDownloadingCurrentMessage()) {
            if (MediaController.getInstance().isMessagePaused()) {
                MediaController.getInstance().playMessage(MediaController.getInstance().getPlayingMessageObject());
            } else {
                MediaController.getInstance().m102lambda$startAudioAgain$7$orgtelegrammessengerMediaController(MediaController.getInstance().getPlayingMessageObject());
            }
        }
    }

    /* renamed from: lambda$new$6$org-telegram-ui-Components-AudioPlayerAlert  reason: not valid java name */
    public /* synthetic */ void m3604lambda$new$6$orgtelegramuiComponentsAudioPlayerAlert(View v) {
        this.optionsButton.toggleSubMenu();
    }

    static /* synthetic */ boolean lambda$new$7(View v, MotionEvent event) {
        return true;
    }

    static /* synthetic */ void lambda$new$8(View view, int position) {
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
            int h = this.playerLayout.getVisibility() == 0 ? AndroidUtilities.dp(150.0f) : -AndroidUtilities.dp(30.0f);
            LinearLayout linearLayout = this.emptyView;
            linearLayout.setTranslationY((float) (((linearLayout.getMeasuredHeight() - this.containerView.getMeasuredHeight()) - h) / 2));
        }
    }

    /* access modifiers changed from: private */
    public void updateEmptyView() {
        this.emptyView.setVisibility((!this.searching || this.listAdapter.getItemCount() != 0) ? 8 : 0);
        updateEmptyViewPosition();
    }

    /* access modifiers changed from: private */
    public boolean scrollToCurrentSong(boolean search) {
        int idx;
        MessageObject playingMessageObject = MediaController.getInstance().getPlayingMessageObject();
        if (playingMessageObject == null) {
            return false;
        }
        boolean found = false;
        if (search) {
            int count = this.listView.getChildCount();
            int a = 0;
            while (true) {
                if (a >= count) {
                    break;
                }
                View child = this.listView.getChildAt(a);
                if (!(child instanceof AudioPlayerCell) || ((AudioPlayerCell) child).getMessageObject() != playingMessageObject) {
                    a++;
                } else if (child.getBottom() <= this.listView.getMeasuredHeight()) {
                    found = true;
                }
            }
        }
        if (found || (idx = this.playlist.indexOf(playingMessageObject)) < 0) {
            return false;
        }
        if (SharedConfig.playOrderReversed) {
            this.layoutManager.scrollToPosition(idx);
            return true;
        }
        this.layoutManager.scrollToPosition(this.playlist.size() - idx);
        return true;
    }

    public boolean onCustomMeasure(View view, int width, int height) {
        if (width < height) {
        }
        FrameLayout frameLayout = this.blurredView;
        if (view != frameLayout) {
            return false;
        }
        frameLayout.measure(View.MeasureSpec.makeMeasureSpec(width, NUM), View.MeasureSpec.makeMeasureSpec(height, NUM));
        return true;
    }

    /* access modifiers changed from: protected */
    public boolean onCustomLayout(View view, int left, int top, int right, int bottom) {
        int width = right - left;
        int height = bottom - top;
        if (width < height) {
        }
        FrameLayout frameLayout = this.blurredView;
        if (view != frameLayout) {
            return false;
        }
        frameLayout.layout(left, 0, left + width, height);
        return true;
    }

    private void setMenuItemChecked(ActionBarMenuSubItem item, boolean checked) {
        if (checked) {
            item.setTextColor(getThemedColor("player_buttonActive"));
            item.setIconColor(getThemedColor("player_buttonActive"));
            return;
        }
        item.setTextColor(getThemedColor("actionBarDefaultSubmenuItem"));
        item.setIconColor(getThemedColor("actionBarDefaultSubmenuItem"));
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
        String key;
        float currentPlaybackSpeed = MediaController.getInstance().getPlaybackSpeed(true);
        if (Math.abs(currentPlaybackSpeed - 1.0f) > 0.001f) {
            key = "inappPlayerPlayPause";
        } else {
            key = "inappPlayerClose";
        }
        this.playbackSpeedButton.setTag(key);
        float speed = MediaController.getInstance().getFastPlaybackSpeed(true);
        if (Math.abs(speed - 1.8f) < 0.001f) {
            this.playbackSpeedButton.setIcon(NUM);
        } else if (Math.abs(speed - 1.5f) < 0.001f) {
            this.playbackSpeedButton.setIcon(NUM);
        } else {
            this.playbackSpeedButton.setIcon(NUM);
        }
        this.playbackSpeedButton.setIconColor(getThemedColor(key));
        if (Build.VERSION.SDK_INT >= 21) {
            this.playbackSpeedButton.setBackgroundDrawable(Theme.createSelectorDrawable(getThemedColor(key) & NUM, 1, AndroidUtilities.dp(14.0f)));
        }
        for (int a = 0; a < this.speedItems.length; a++) {
            if ((a != 0 || Math.abs(currentPlaybackSpeed - 0.5f) >= 0.001f) && ((a != 1 || Math.abs(currentPlaybackSpeed - 1.0f) >= 0.001f) && ((a != 2 || Math.abs(currentPlaybackSpeed - 1.5f) >= 0.001f) && (a != 3 || Math.abs(currentPlaybackSpeed - 1.8f) >= 0.001f)))) {
                this.speedItems[a].setColors(getThemedColor("actionBarDefaultSubmenuItem"), getThemedColor("actionBarDefaultSubmenuItemIcon"));
            } else {
                this.speedItems[a].setColors(getThemedColor("inappPlayerPlayPause"), getThemedColor("inappPlayerPlayPause"));
            }
        }
    }

    /* access modifiers changed from: private */
    public void onSubItemClick(int id) {
        String path;
        MessageObject messageObject = MediaController.getInstance().getPlayingMessageObject();
        if (messageObject != null && this.parentActivity != null) {
            if (id == 1) {
                if (UserConfig.selectedAccount != this.currentAccount) {
                    this.parentActivity.switchToAccount(this.currentAccount, true);
                }
                Bundle args = new Bundle();
                args.putBoolean("onlySelect", true);
                args.putInt("dialogsType", 3);
                DialogsActivity fragment = new DialogsActivity(args);
                ArrayList<MessageObject> fmessages = new ArrayList<>();
                fmessages.add(messageObject);
                fragment.setDelegate(new AudioPlayerAlert$$ExternalSyntheticLambda3(this, fmessages));
                this.parentActivity.m2367lambda$runLinkRequest$54$orgtelegramuiLaunchActivity(fragment);
                dismiss();
            } else if (id == 2) {
                File f = null;
                try {
                    if (!TextUtils.isEmpty(messageObject.messageOwner.attachPath)) {
                        f = new File(messageObject.messageOwner.attachPath);
                        if (!f.exists()) {
                            f = null;
                        }
                    }
                    if (f == null) {
                        f = FileLoader.getPathToMessage(messageObject.messageOwner);
                    }
                    if (f.exists()) {
                        Intent intent = new Intent("android.intent.action.SEND");
                        intent.setType(messageObject.getMimeType());
                        if (Build.VERSION.SDK_INT >= 24) {
                            try {
                                intent.putExtra("android.intent.extra.STREAM", FileProvider.getUriForFile(ApplicationLoader.applicationContext, "org.telegram.messenger.beta.provider", f));
                                intent.setFlags(1);
                            } catch (Exception e) {
                                intent.putExtra("android.intent.extra.STREAM", Uri.fromFile(f));
                            }
                        } else {
                            intent.putExtra("android.intent.extra.STREAM", Uri.fromFile(f));
                        }
                        this.parentActivity.startActivityForResult(Intent.createChooser(intent, LocaleController.getString("ShareFile", NUM)), 500);
                        return;
                    }
                    AlertDialog.Builder builder = new AlertDialog.Builder((Context) this.parentActivity);
                    builder.setTitle(LocaleController.getString("AppName", NUM));
                    builder.setPositiveButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null);
                    builder.setMessage(LocaleController.getString("PleaseDownload", NUM));
                    builder.show();
                } catch (Exception e2) {
                    FileLog.e((Throwable) e2);
                }
            } else if (id == 4) {
                if (UserConfig.selectedAccount != this.currentAccount) {
                    this.parentActivity.switchToAccount(this.currentAccount, true);
                }
                Bundle args2 = new Bundle();
                long did = messageObject.getDialogId();
                if (DialogObject.isEncryptedDialog(did)) {
                    args2.putInt("enc_id", DialogObject.getEncryptedChatId(did));
                } else if (DialogObject.isUserDialog(did)) {
                    args2.putLong("user_id", did);
                } else {
                    TLRPC.Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(-did));
                    if (!(chat == null || chat.migrated_to == null)) {
                        args2.putLong("migrated_to", did);
                        did = -chat.migrated_to.channel_id;
                    }
                    args2.putLong("chat_id", -did);
                }
                args2.putInt("message_id", messageObject.getId());
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                this.parentActivity.presentFragment(new ChatActivity(args2), false, false);
                dismiss();
            } else if (id != 5) {
            } else {
                if (Build.VERSION.SDK_INT < 23 || ((Build.VERSION.SDK_INT > 28 && !BuildVars.NO_SCOPED_STORAGE) || this.parentActivity.checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") == 0)) {
                    String fileName = FileLoader.getDocumentFileName(messageObject.getDocument());
                    if (TextUtils.isEmpty(fileName)) {
                        fileName = messageObject.getFileName();
                    }
                    String path2 = messageObject.messageOwner.attachPath;
                    if (path2 != null && path2.length() > 0 && !new File(path2).exists()) {
                        path2 = null;
                    }
                    if (path2 == null || path2.length() == 0) {
                        path = FileLoader.getPathToMessage(messageObject.messageOwner).toString();
                    } else {
                        path = path2;
                    }
                    MediaController.saveFile(path, this.parentActivity, 3, fileName, messageObject.getDocument() != null ? messageObject.getDocument().mime_type : "", new AudioPlayerAlert$$ExternalSyntheticLambda9(this));
                    return;
                }
                this.parentActivity.requestPermissions(new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 4);
            }
        }
    }

    /* renamed from: lambda$onSubItemClick$9$org-telegram-ui-Components-AudioPlayerAlert  reason: not valid java name */
    public /* synthetic */ void m3606x38var_a48(ArrayList fmessages, DialogsActivity fragment1, ArrayList dids, CharSequence message, boolean param) {
        ArrayList arrayList = dids;
        if (dids.size() > 1 || ((Long) arrayList.get(0)).longValue() == UserConfig.getInstance(this.currentAccount).getClientUserId()) {
            ArrayList arrayList2 = fmessages;
        } else if (message != null) {
            ArrayList arrayList3 = fmessages;
        } else {
            long did = ((Long) arrayList.get(0)).longValue();
            Bundle args1 = new Bundle();
            args1.putBoolean("scrollToTopOnResume", true);
            if (DialogObject.isEncryptedDialog(did)) {
                args1.putInt("enc_id", DialogObject.getEncryptedChatId(did));
            } else if (DialogObject.isUserDialog(did)) {
                args1.putLong("user_id", did);
            } else {
                args1.putLong("chat_id", -did);
            }
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
            ChatActivity chatActivity = new ChatActivity(args1);
            if (this.parentActivity.presentFragment(chatActivity, true, false)) {
                chatActivity.showFieldPanelForForward(true, fmessages);
                return;
            }
            ArrayList arrayList4 = fmessages;
            fragment1.finishFragment();
            return;
        }
        for (int a = 0; a < dids.size(); a++) {
            long did2 = ((Long) arrayList.get(a)).longValue();
            if (message != null) {
                SendMessagesHelper.getInstance(this.currentAccount).sendMessage(message.toString(), did2, (MessageObject) null, (MessageObject) null, (TLRPC.WebPage) null, true, (ArrayList<TLRPC.MessageEntity>) null, (TLRPC.ReplyMarkup) null, (HashMap<String, String>) null, true, 0, (MessageObject.SendAnimationData) null);
            }
            SendMessagesHelper.getInstance(this.currentAccount).sendMessage((ArrayList<MessageObject>) fmessages, did2, false, false, true, 0);
        }
        fragment1.finishFragment();
    }

    /* renamed from: lambda$onSubItemClick$10$org-telegram-ui-Components-AudioPlayerAlert  reason: not valid java name */
    public /* synthetic */ void m3605x607d8ff4() {
        BulletinFactory.of((FrameLayout) this.containerView, this.resourcesProvider).createDownloadBulletin(BulletinFactory.FileType.AUDIO).show();
    }

    /* access modifiers changed from: private */
    public void showAlbumCover(boolean show, boolean animated) {
        if (show) {
            if (this.blurredView.getVisibility() != 0 && !this.blurredAnimationInProgress) {
                this.blurredView.setTag(1);
                this.bigAlbumConver.setImageBitmap(this.coverContainer.getImageReceiver().getBitmap());
                this.blurredAnimationInProgress = true;
                View fragmentView = this.parentActivity.getActionBarLayout().fragmentsStack.get(this.parentActivity.getActionBarLayout().fragmentsStack.size() - 1).getFragmentView();
                int w = (int) (((float) fragmentView.getMeasuredWidth()) / 6.0f);
                int h = (int) (((float) fragmentView.getMeasuredHeight()) / 6.0f);
                Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                canvas.scale(0.16666667f, 0.16666667f);
                fragmentView.draw(canvas);
                canvas.translate((float) (this.containerView.getLeft() - getLeftInset()), 0.0f);
                this.containerView.draw(canvas);
                Utilities.stackBlurBitmap(bitmap, Math.max(7, Math.max(w, h) / 180));
                this.blurredView.setBackground(new BitmapDrawable(bitmap));
                this.blurredView.setVisibility(0);
                this.blurredView.animate().alpha(1.0f).setDuration(180).setListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        boolean unused = AudioPlayerAlert.this.blurredAnimationInProgress = false;
                    }
                }).start();
                this.bigAlbumConver.animate().scaleX(1.0f).scaleY(1.0f).setDuration(180).start();
            }
        } else if (this.blurredView.getVisibility() == 0) {
            this.blurredView.setTag((Object) null);
            if (animated) {
                this.blurredAnimationInProgress = true;
                this.blurredView.animate().alpha(0.0f).setDuration(180).setListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
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

    public void didReceivedNotification(int id, int account, Object... args) {
        AudioPlayerCell cell;
        MessageObject messageObject;
        AudioPlayerCell cell2;
        MessageObject messageObject1;
        MessageObject messageObject2;
        float bufferedProgress;
        int offset = 0;
        if (id == NotificationCenter.messagePlayingDidStart || id == NotificationCenter.messagePlayingPlayStateChanged || id == NotificationCenter.messagePlayingDidReset) {
            updateTitle(id == NotificationCenter.messagePlayingDidReset && args[1].booleanValue());
            if (id == NotificationCenter.messagePlayingDidReset || id == NotificationCenter.messagePlayingPlayStateChanged) {
                int count = this.listView.getChildCount();
                for (int a = 0; a < count; a++) {
                    View view = this.listView.getChildAt(a);
                    if ((view instanceof AudioPlayerCell) && (messageObject = cell.getMessageObject()) != null && (messageObject.isVoice() || messageObject.isMusic())) {
                        (cell = (AudioPlayerCell) view).updateButtonState(false, true);
                    }
                }
                if (id == NotificationCenter.messagePlayingPlayStateChanged && MediaController.getInstance().getPlayingMessageObject() != null) {
                    if (MediaController.getInstance().isMessagePaused()) {
                        startForwardRewindingSeek();
                    } else if (this.rewindingState == 1 && this.rewindingProgress != -1.0f) {
                        AndroidUtilities.cancelRunOnUIThread(this.forwardSeek);
                        this.lastUpdateRewindingPlayerTime = 0;
                        this.forwardSeek.run();
                        this.rewindingProgress = -1.0f;
                    }
                }
            } else if (args[0].eventId == 0) {
                int count2 = this.listView.getChildCount();
                for (int a2 = 0; a2 < count2; a2++) {
                    View view2 = this.listView.getChildAt(a2);
                    if ((view2 instanceof AudioPlayerCell) && (messageObject1 = cell2.getMessageObject()) != null && (messageObject1.isVoice() || messageObject1.isMusic())) {
                        (cell2 = (AudioPlayerCell) view2).updateButtonState(false, true);
                    }
                }
            }
        } else if (id == NotificationCenter.messagePlayingProgressDidChanged) {
            MessageObject messageObject3 = MediaController.getInstance().getPlayingMessageObject();
            if (messageObject3 != null && messageObject3.isMusic()) {
                updateProgress(messageObject3);
            }
        } else if (id == NotificationCenter.musicDidLoad) {
            this.playlist = MediaController.getInstance().getPlaylist();
            this.listAdapter.notifyDataSetChanged();
        } else if (id == NotificationCenter.moreMusicDidLoad) {
            this.playlist = MediaController.getInstance().getPlaylist();
            this.listAdapter.notifyDataSetChanged();
            if (SharedConfig.playOrderReversed) {
                this.listView.stopScroll();
                int addedCount = args[0].intValue();
                int findFirstVisibleItemPosition = this.layoutManager.findFirstVisibleItemPosition();
                int position = this.layoutManager.findLastVisibleItemPosition();
                if (position != -1) {
                    View firstVisView = this.layoutManager.findViewByPosition(position);
                    if (firstVisView != null) {
                        offset = firstVisView.getTop();
                    }
                    this.layoutManager.scrollToPositionWithOffset(position + addedCount, offset);
                }
            }
        } else if (id == NotificationCenter.fileLoaded) {
            if (args[0].equals(this.currentFile)) {
                updateTitle(false);
                this.currentAudioFinishedLoading = true;
            }
        } else if (id == NotificationCenter.fileLoadProgressChanged && args[0].equals(this.currentFile) && (messageObject2 = MediaController.getInstance().getPlayingMessageObject()) != null) {
            Long l = args[1];
            Long l2 = args[2];
            if (this.currentAudioFinishedLoading) {
                bufferedProgress = 1.0f;
            } else {
                long newTime = SystemClock.elapsedRealtime();
                if (Math.abs(newTime - this.lastBufferedPositionCheck) >= 500) {
                    float bufferedProgress2 = MediaController.getInstance().isStreamingCurrentAudio() ? FileLoader.getInstance(this.currentAccount).getBufferedProgressFromPosition(messageObject2.audioProgress, this.currentFile) : 1.0f;
                    this.lastBufferedPositionCheck = newTime;
                    bufferedProgress = bufferedProgress2;
                } else {
                    bufferedProgress = -1.0f;
                }
            }
            if (bufferedProgress != -1.0f) {
                this.seekBarView.setBufferedProgress(bufferedProgress);
            }
        }
    }

    /* access modifiers changed from: protected */
    public boolean canDismissWithSwipe() {
        return false;
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
        View child = this.listView.getChildAt(0);
        RecyclerListView.Holder holder = (RecyclerListView.Holder) this.listView.findContainingViewHolder(child);
        int top = child.getTop();
        int newOffset = AndroidUtilities.dp(7.0f);
        if (top >= AndroidUtilities.dp(7.0f) && holder != null && holder.getAdapterPosition() == 0) {
            newOffset = top;
        }
        boolean show = newOffset <= AndroidUtilities.dp(12.0f);
        if ((show && this.actionBar.getTag() == null) || (!show && this.actionBar.getTag() != null)) {
            this.actionBar.setTag(show ? 1 : null);
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
            fArr[0] = show ? 1.0f : 0.0f;
            animatorArr[0] = ObjectAnimator.ofFloat(actionBar2, property, fArr);
            View view = this.actionBarShadow;
            Property property2 = View.ALPHA;
            float[] fArr2 = new float[1];
            if (!show) {
                f = 0.0f;
            }
            fArr2[0] = f;
            animatorArr[1] = ObjectAnimator.ofFloat(view, property2, fArr2);
            animatorSet3.playTogether(animatorArr);
            this.actionBarAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                }

                public void onAnimationCancel(Animator animation) {
                    AnimatorSet unused = AudioPlayerAlert.this.actionBarAnimation = null;
                }
            });
            this.actionBarAnimation.start();
        }
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.listView.getLayoutParams();
        int newOffset2 = newOffset + (layoutParams.topMargin - AndroidUtilities.dp(11.0f));
        if (this.scrollOffsetY != newOffset2) {
            RecyclerListView recyclerListView2 = this.listView;
            this.scrollOffsetY = newOffset2;
            recyclerListView2.setTopGlowOffset(newOffset2 - layoutParams.topMargin);
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

    public void onFailedDownload(String fileName, boolean canceled) {
    }

    public void onSuccessDownload(String fileName) {
    }

    public void onProgressDownload(String fileName, long downloadedSize, long totalSize) {
        this.progressView.setProgress(Math.min(1.0f, ((float) downloadedSize) / ((float) totalSize)), true);
    }

    public void onProgressUpload(String fileName, long uploadedSize, long totalSize, boolean isEncrypted) {
    }

    public int getObserverTag() {
        return this.TAG;
    }

    private void updateRepeatButton() {
        int mode = SharedConfig.repeatMode;
        if (mode == 0 || mode == 1) {
            if (SharedConfig.shuffleMusic) {
                if (mode == 0) {
                    this.repeatButton.setIcon(NUM);
                } else {
                    this.repeatButton.setIcon(NUM);
                }
            } else if (!SharedConfig.playOrderReversed) {
                this.repeatButton.setIcon(NUM);
            } else if (mode == 0) {
                this.repeatButton.setIcon(NUM);
            } else {
                this.repeatButton.setIcon(NUM);
            }
            if (mode != 0 || SharedConfig.shuffleMusic || SharedConfig.playOrderReversed) {
                this.repeatButton.setTag("player_buttonActive");
                this.repeatButton.setIconColor(getThemedColor("player_buttonActive"));
                Theme.setSelectorDrawableColor(this.repeatButton.getBackground(), NUM & getThemedColor("player_buttonActive"), true);
                if (mode != 0) {
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
        } else if (mode == 2) {
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

    private void updateProgress(MessageObject messageObject, boolean animated) {
        int newTime;
        float bufferedProgress;
        int i;
        SeekBarView seekBarView2 = this.seekBarView;
        if (seekBarView2 != null) {
            if (seekBarView2.isDragging()) {
                newTime = (int) (((float) messageObject.getDuration()) * this.seekBarView.getProgress());
            } else {
                boolean z = true;
                if (this.rewindingProgress < 0.0f || ((i = this.rewindingState) != -1 && (i != 1 || !MediaController.getInstance().isMessagePaused()))) {
                    z = false;
                }
                boolean updateRewinding = z;
                if (updateRewinding) {
                    this.seekBarView.setProgress(this.rewindingProgress, animated);
                } else {
                    this.seekBarView.setProgress(messageObject.audioProgress, animated);
                }
                if (this.currentAudioFinishedLoading) {
                    bufferedProgress = 1.0f;
                } else {
                    long time = SystemClock.elapsedRealtime();
                    if (Math.abs(time - this.lastBufferedPositionCheck) >= 500) {
                        float bufferedProgress2 = MediaController.getInstance().isStreamingCurrentAudio() ? FileLoader.getInstance(this.currentAccount).getBufferedProgressFromPosition(messageObject.audioProgress, this.currentFile) : 1.0f;
                        this.lastBufferedPositionCheck = time;
                        bufferedProgress = bufferedProgress2;
                    } else {
                        bufferedProgress = -1.0f;
                    }
                }
                if (bufferedProgress != -1.0f) {
                    this.seekBarView.setBufferedProgress(bufferedProgress);
                }
                if (updateRewinding) {
                    int newTime2 = (int) (((float) messageObject.getDuration()) * this.seekBarView.getProgress());
                    messageObject.audioProgressSec = newTime2;
                    newTime = newTime2;
                } else {
                    newTime = messageObject.audioProgressSec;
                }
            }
            if (this.lastTime != newTime) {
                this.lastTime = newTime;
                this.timeTextView.setText(AndroidUtilities.formatShortDuration(newTime));
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
        boolean canStream = SharedConfig.streamMedia && ((int) messageObject.getDialogId()) != 0 && messageObject.isMusic();
        if (cacheFile.exists() || canStream) {
            DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
            this.progressView.setVisibility(4);
            this.seekBarView.setVisibility(0);
            this.playButton.setEnabled(true);
            return;
        }
        String fileName = messageObject.getFileName();
        DownloadController.getInstance(this.currentAccount).addLoadingFileObserver(fileName, this);
        Float progress = ImageLoader.getInstance().getFileProgress(fileName);
        this.progressView.setProgress(progress != null ? progress.floatValue() : 0.0f, false);
        this.progressView.setVisibility(0);
        this.seekBarView.setVisibility(4);
        this.playButton.setEnabled(false);
    }

    private void updateTitle(boolean shutdown) {
        MessageObject messageObject = MediaController.getInstance().getPlayingMessageObject();
        if ((messageObject == null && shutdown) || (messageObject != null && !messageObject.isMusic())) {
            dismiss();
        } else if (messageObject == null) {
            this.lastMessageObject = null;
        } else {
            boolean sameMessageObject = messageObject == this.lastMessageObject;
            this.lastMessageObject = messageObject;
            if (messageObject.eventId != 0 || messageObject.getId() <= -NUM) {
                this.optionsButton.setVisibility(4);
            } else {
                this.optionsButton.setVisibility(0);
            }
            if (MessagesController.getInstance(this.currentAccount).isChatNoForwards(messageObject.getChatId())) {
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
            checkIfMusicDownloaded(messageObject);
            updateProgress(messageObject, !sameMessageObject);
            updateCover(messageObject, !sameMessageObject);
            if (MediaController.getInstance().isMessagePaused()) {
                this.playPauseDrawable.setPause(false);
                this.playButton.setContentDescription(LocaleController.getString("AccActionPlay", NUM));
            } else {
                this.playPauseDrawable.setPause(true);
                this.playButton.setContentDescription(LocaleController.getString("AccActionPause", NUM));
            }
            String title = messageObject.getMusicTitle();
            String author = messageObject.getMusicAuthor();
            this.titleTextView.setText(title);
            this.authorTextView.setText(author);
            int duration = messageObject.getDuration();
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
            if (!sameMessageObject) {
                preloadNeighboringThumbs();
            }
        }
    }

    private void updateCover(MessageObject messageObject, boolean animated) {
        CoverContainer coverContainer2 = this.coverContainer;
        BackupImageView imageView = animated ? coverContainer2.getNextImageView() : coverContainer2.getImageView();
        AudioInfo audioInfo = MediaController.getInstance().getAudioInfo();
        if (audioInfo == null || audioInfo.getCover() == null) {
            this.currentFile = FileLoader.getAttachFileName(messageObject.getDocument());
            this.currentAudioFinishedLoading = false;
            String artworkUrl = messageObject.getArtworkUrl(false);
            ImageLocation thumbImageLocation = getArtworkThumbImageLocation(messageObject);
            if (!TextUtils.isEmpty(artworkUrl)) {
                imageView.setImage(ImageLocation.getForPath(artworkUrl), (String) null, thumbImageLocation, (String) null, (String) null, 0, 1, messageObject);
            } else if (thumbImageLocation != null) {
                imageView.setImage((ImageLocation) null, (String) null, thumbImageLocation, (String) null, (String) null, 0, 1, messageObject);
            } else {
                imageView.setImageDrawable((Drawable) null);
            }
            imageView.invalidate();
        } else {
            imageView.setImageBitmap(audioInfo.getCover());
            this.currentFile = null;
            this.currentAudioFinishedLoading = true;
            MessageObject messageObject2 = messageObject;
        }
        if (animated) {
            this.coverContainer.switchImageViews();
        }
    }

    private ImageLocation getArtworkThumbImageLocation(MessageObject messageObject) {
        TLRPC.Document document = messageObject.getDocument();
        TLRPC.PhotoSize thumb = document != null ? FileLoader.getClosestPhotoSizeWithSize(document.thumbs, 360) : null;
        if (!(thumb instanceof TLRPC.TL_photoSize) && !(thumb instanceof TLRPC.TL_photoSizeProgressive)) {
            thumb = null;
        }
        if (thumb != null) {
            return ImageLocation.getForDocument(thumb, document);
        }
        String smallArtworkUrl = messageObject.getArtworkUrl(true);
        if (smallArtworkUrl != null) {
            return ImageLocation.getForPath(smallArtworkUrl);
        }
        return null;
    }

    private void preloadNeighboringThumbs() {
        MediaController mediaController = MediaController.getInstance();
        List<MessageObject> playlist2 = mediaController.getPlaylist();
        if (playlist2.size() > 1) {
            List<MessageObject> neighboringItems = new ArrayList<>();
            int playingIndex = mediaController.getPlayingMessageObjectNum();
            int nextIndex = playingIndex + 1;
            int prevIndex = playingIndex - 1;
            if (nextIndex >= playlist2.size()) {
                nextIndex = 0;
            }
            if (prevIndex <= -1) {
                prevIndex = playlist2.size() - 1;
            }
            neighboringItems.add(playlist2.get(nextIndex));
            if (nextIndex != prevIndex) {
                neighboringItems.add(playlist2.get(prevIndex));
            }
            int N = neighboringItems.size();
            for (int i = 0; i < N; i++) {
                MessageObject messageObject = neighboringItems.get(i);
                ImageLocation thumbImageLocation = getArtworkThumbImageLocation(messageObject);
                if (thumbImageLocation == null) {
                } else if (thumbImageLocation.path != null) {
                    ImageLoader.getInstance().preloadArtwork(thumbImageLocation.path);
                } else {
                    ImageLocation imageLocation = thumbImageLocation;
                    FileLoader.getInstance(this.currentAccount).loadFile(thumbImageLocation, messageObject, (String) null, 0, 1);
                }
            }
        }
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context context;
        private ArrayList<MessageObject> searchResult = new ArrayList<>();
        private Runnable searchRunnable;

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

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return true;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context2 = this.context;
            boolean currentPlaylistIsGlobalSearch = MediaController.getInstance().currentPlaylistIsGlobalSearch();
            return new RecyclerListView.Holder(new AudioPlayerCell(context2, currentPlaylistIsGlobalSearch ? 1 : 0, AudioPlayerAlert.this.resourcesProvider));
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            AudioPlayerCell cell = (AudioPlayerCell) holder.itemView;
            if (AudioPlayerAlert.this.searchWas) {
                cell.setMessageObject(this.searchResult.get(position));
            } else if (SharedConfig.playOrderReversed) {
                cell.setMessageObject((MessageObject) AudioPlayerAlert.this.playlist.get(position));
            } else {
                cell.setMessageObject((MessageObject) AudioPlayerAlert.this.playlist.get((AudioPlayerAlert.this.playlist.size() - position) - 1));
            }
        }

        public int getItemViewType(int i) {
            return 0;
        }

        public void search(String query) {
            if (this.searchRunnable != null) {
                Utilities.searchQueue.cancelRunnable(this.searchRunnable);
                this.searchRunnable = null;
            }
            if (query == null) {
                this.searchResult.clear();
                notifyDataSetChanged();
                return;
            }
            DispatchQueue dispatchQueue = Utilities.searchQueue;
            AudioPlayerAlert$ListAdapter$$ExternalSyntheticLambda1 audioPlayerAlert$ListAdapter$$ExternalSyntheticLambda1 = new AudioPlayerAlert$ListAdapter$$ExternalSyntheticLambda1(this, query);
            this.searchRunnable = audioPlayerAlert$ListAdapter$$ExternalSyntheticLambda1;
            dispatchQueue.postRunnable(audioPlayerAlert$ListAdapter$$ExternalSyntheticLambda1, 300);
        }

        /* renamed from: lambda$search$0$org-telegram-ui-Components-AudioPlayerAlert$ListAdapter  reason: not valid java name */
        public /* synthetic */ void m3613xe1d541a0(String query) {
            this.searchRunnable = null;
            processSearch(query);
        }

        private void processSearch(String query) {
            AndroidUtilities.runOnUIThread(new AudioPlayerAlert$ListAdapter$$ExternalSyntheticLambda0(this, query));
        }

        /* renamed from: lambda$processSearch$2$org-telegram-ui-Components-AudioPlayerAlert$ListAdapter  reason: not valid java name */
        public /* synthetic */ void m3612x5e2e9517(String query) {
            Utilities.searchQueue.postRunnable(new AudioPlayerAlert$ListAdapter$$ExternalSyntheticLambda2(this, query, new ArrayList<>(AudioPlayerAlert.this.playlist)));
        }

        /* renamed from: lambda$processSearch$1$org-telegram-ui-Components-AudioPlayerAlert$ListAdapter  reason: not valid java name */
        public /* synthetic */ void m3611x389a8CLASSNAME(String query, ArrayList copy) {
            String search1;
            TLRPC.Document document;
            String str = query;
            String search12 = query.trim().toLowerCase();
            if (search12.length() == 0) {
                updateSearchResults(new ArrayList(), str);
                return;
            }
            String search2 = LocaleController.getInstance().getTranslitString(search12);
            if (search12.equals(search2) || search2.length() == 0) {
                search2 = null;
            }
            String[] search = new String[((search2 != null ? 1 : 0) + 1)];
            search[0] = search12;
            if (search2 != null) {
                search[1] = search2;
            }
            ArrayList<MessageObject> resultArray = new ArrayList<>();
            int a = 0;
            while (a < copy.size()) {
                MessageObject messageObject = (MessageObject) copy.get(a);
                int b = 0;
                while (true) {
                    if (b >= search.length) {
                        search1 = search12;
                        break;
                    }
                    String q = search[b];
                    String name = messageObject.getDocumentName();
                    if (name == null) {
                        search1 = search12;
                    } else if (name.length() == 0) {
                        search1 = search12;
                    } else if (name.toLowerCase().contains(q)) {
                        resultArray.add(messageObject);
                        search1 = search12;
                        break;
                    } else {
                        if (messageObject.type == 0) {
                            document = messageObject.messageOwner.media.webpage.document;
                        } else {
                            document = messageObject.messageOwner.media.document;
                        }
                        boolean ok = false;
                        int c = 0;
                        while (true) {
                            if (c >= document.attributes.size()) {
                                search1 = search12;
                                break;
                            }
                            TLRPC.DocumentAttribute attribute = document.attributes.get(c);
                            search1 = search12;
                            if (attribute instanceof TLRPC.TL_documentAttributeAudio) {
                                if (attribute.performer != null) {
                                    ok = attribute.performer.toLowerCase().contains(q);
                                }
                                if (!ok && attribute.title != null) {
                                    ok = attribute.title.toLowerCase().contains(q);
                                }
                            } else {
                                c++;
                                search12 = search1;
                            }
                        }
                        if (ok) {
                            resultArray.add(messageObject);
                            break;
                        }
                    }
                    b++;
                    search12 = search1;
                }
                a++;
                search12 = search1;
            }
            updateSearchResults(resultArray, str);
        }

        private void updateSearchResults(ArrayList<MessageObject> documents, String query) {
            AndroidUtilities.runOnUIThread(new AudioPlayerAlert$ListAdapter$$ExternalSyntheticLambda3(this, documents, query));
        }

        /* renamed from: lambda$updateSearchResults$3$org-telegram-ui-Components-AudioPlayerAlert$ListAdapter  reason: not valid java name */
        public /* synthetic */ void m3614xe828b846(ArrayList documents, String query) {
            if (AudioPlayerAlert.this.searching) {
                boolean unused = AudioPlayerAlert.this.searchWas = true;
                this.searchResult = documents;
                notifyDataSetChanged();
                AudioPlayerAlert.this.layoutManager.scrollToPosition(0);
                AudioPlayerAlert.this.emptySubtitleTextView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("NoAudioFoundPlayerInfo", NUM, query)));
            }
        }
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();
        ThemeDescription.ThemeDescriptionDelegate delegate = new AudioPlayerAlert$$ExternalSyntheticLambda1(this);
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "player_actionBar"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, delegate, "player_actionBarTitle"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "player_actionBarTitle"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBTITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "player_actionBarTitle"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "player_actionBarSelector"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCH, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "player_actionBarTitle"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCHPLACEHOLDER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "player_time"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{AudioPlayerCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inLoader"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{AudioPlayerCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outLoader"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{AudioPlayerCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inLoaderSelected"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{AudioPlayerCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inMediaIcon"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{AudioPlayerCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inMediaIconSelected"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{AudioPlayerCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText2"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{AudioPlayerCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inAudioSelectedProgress"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{AudioPlayerCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inAudioProgress"));
        themeDescriptions.add(new ThemeDescription(this.containerView, 0, (Class[]) null, (Paint) null, new Drawable[]{this.shadowDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogBackground"));
        themeDescriptions.add(new ThemeDescription(this.progressView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "player_progressBackground"));
        themeDescriptions.add(new ThemeDescription(this.progressView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "player_progress"));
        themeDescriptions.add(new ThemeDescription(this.seekBarView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "player_progressBackground"));
        themeDescriptions.add(new ThemeDescription(this.seekBarView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "key_player_progressCachedBackground"));
        themeDescriptions.add(new ThemeDescription(this.seekBarView, ThemeDescription.FLAG_PROGRESSBAR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "player_progress"));
        themeDescriptions.add(new ThemeDescription(this.playbackSpeedButton, ThemeDescription.FLAG_CHECKTAG | ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "inappPlayerPlayPause"));
        themeDescriptions.add(new ThemeDescription(this.playbackSpeedButton, ThemeDescription.FLAG_CHECKTAG | ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "inappPlayerClose"));
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate = delegate;
        themeDescriptions.add(new ThemeDescription(this.repeatButton, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, "player_button"));
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate2 = delegate;
        themeDescriptions.add(new ThemeDescription(this.repeatButton, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, "player_buttonActive"));
        themeDescriptions.add(new ThemeDescription(this.repeatButton, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, "listSelectorSDK21"));
        themeDescriptions.add(new ThemeDescription(this.repeatButton, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, "actionBarDefaultSubmenuItem"));
        themeDescriptions.add(new ThemeDescription(this.repeatButton, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, "actionBarDefaultSubmenuBackground"));
        themeDescriptions.add(new ThemeDescription(this.optionsButton, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, "player_button"));
        themeDescriptions.add(new ThemeDescription(this.optionsButton, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, "listSelectorSDK21"));
        themeDescriptions.add(new ThemeDescription(this.optionsButton, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, "actionBarDefaultSubmenuItem"));
        themeDescriptions.add(new ThemeDescription(this.optionsButton, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, "actionBarDefaultSubmenuBackground"));
        RLottieImageView rLottieImageView = this.prevButton;
        themeDescriptions.add(new ThemeDescription((View) rLottieImageView, 0, (Class[]) null, new RLottieDrawable[]{rLottieImageView.getAnimatedDrawable()}, "Triangle 3", "player_button"));
        RLottieImageView rLottieImageView2 = this.prevButton;
        themeDescriptions.add(new ThemeDescription((View) rLottieImageView2, 0, (Class[]) null, new RLottieDrawable[]{rLottieImageView2.getAnimatedDrawable()}, "Triangle 4", "player_button"));
        RLottieImageView rLottieImageView3 = this.prevButton;
        themeDescriptions.add(new ThemeDescription((View) rLottieImageView3, 0, (Class[]) null, new RLottieDrawable[]{rLottieImageView3.getAnimatedDrawable()}, "Rectangle 4", "player_button"));
        themeDescriptions.add(new ThemeDescription(this.prevButton, ThemeDescription.FLAG_IMAGECOLOR | ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        themeDescriptions.add(new ThemeDescription(this.playButton, ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "player_button"));
        themeDescriptions.add(new ThemeDescription(this.playButton, ThemeDescription.FLAG_IMAGECOLOR | ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        RLottieImageView rLottieImageView4 = this.nextButton;
        themeDescriptions.add(new ThemeDescription((View) rLottieImageView4, 0, (Class[]) null, new RLottieDrawable[]{rLottieImageView4.getAnimatedDrawable()}, "Triangle 3", "player_button"));
        RLottieImageView rLottieImageView5 = this.nextButton;
        themeDescriptions.add(new ThemeDescription((View) rLottieImageView5, 0, (Class[]) null, new RLottieDrawable[]{rLottieImageView5.getAnimatedDrawable()}, "Triangle 4", "player_button"));
        RLottieImageView rLottieImageView6 = this.nextButton;
        themeDescriptions.add(new ThemeDescription((View) rLottieImageView6, 0, (Class[]) null, new RLottieDrawable[]{rLottieImageView6.getAnimatedDrawable()}, "Rectangle 4", "player_button"));
        themeDescriptions.add(new ThemeDescription(this.nextButton, ThemeDescription.FLAG_IMAGECOLOR | ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        themeDescriptions.add(new ThemeDescription(this.playerLayout, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "player_background"));
        themeDescriptions.add(new ThemeDescription(this.playerShadow, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogShadowLine"));
        themeDescriptions.add(new ThemeDescription(this.emptyImageView, ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogEmptyImage"));
        themeDescriptions.add(new ThemeDescription(this.emptyTitleTextView, ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogEmptyText"));
        themeDescriptions.add(new ThemeDescription(this.emptySubtitleTextView, ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogEmptyText"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogScrollGlow"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "divider"));
        themeDescriptions.add(new ThemeDescription(this.progressView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "emptyListPlaceholder"));
        themeDescriptions.add(new ThemeDescription(this.progressView, ThemeDescription.FLAG_PROGRESSBAR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "progressCircle"));
        themeDescriptions.add(new ThemeDescription(this.durationTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "player_time"));
        themeDescriptions.add(new ThemeDescription(this.timeTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "player_time"));
        themeDescriptions.add(new ThemeDescription(this.titleTextView.getTextView(), ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "player_actionBarTitle"));
        themeDescriptions.add(new ThemeDescription(this.titleTextView.getNextTextView(), ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "player_actionBarTitle"));
        themeDescriptions.add(new ThemeDescription(this.authorTextView.getTextView(), ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "player_time"));
        themeDescriptions.add(new ThemeDescription(this.authorTextView.getNextTextView(), ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "player_time"));
        themeDescriptions.add(new ThemeDescription(this.containerView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "key_sheet_scrollUp"));
        return themeDescriptions;
    }

    /* renamed from: lambda$getThemeDescriptions$11$org-telegram-ui-Components-AudioPlayerAlert  reason: not valid java name */
    public /* synthetic */ void m3598xb100d91f() {
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

        /* renamed from: lambda$new$0$org-telegram-ui-Components-AudioPlayerAlert$CoverContainer  reason: not valid java name */
        public /* synthetic */ void m3610x457889fd(int index, ImageReceiver imageReceiver, boolean set, boolean thumb, boolean memCache) {
            if (index == this.activeIndex) {
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
            final BackupImageView prevImageView = backupImageViewArr[i ^ 1];
            BackupImageView currImageView = backupImageViewArr[i];
            boolean hasBitmapImage = prevImageView.getImageReceiver().hasBitmapImage();
            currImageView.setAlpha(hasBitmapImage ? 1.0f : 0.0f);
            currImageView.setScaleX(0.8f);
            currImageView.setScaleY(0.8f);
            currImageView.setVisibility(0);
            if (hasBitmapImage) {
                prevImageView.bringToFront();
            } else {
                prevImageView.setVisibility(8);
                prevImageView.setImageDrawable((Drawable) null);
            }
            ValueAnimator expandAnimator = ValueAnimator.ofFloat(new float[]{0.8f, 1.0f});
            expandAnimator.setDuration(125);
            expandAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT);
            expandAnimator.addUpdateListener(new AudioPlayerAlert$CoverContainer$$ExternalSyntheticLambda1(currImageView, hasBitmapImage));
            if (hasBitmapImage) {
                ValueAnimator collapseAnimator = ValueAnimator.ofFloat(new float[]{prevImageView.getScaleX(), 0.8f});
                collapseAnimator.setDuration(125);
                collapseAnimator.setInterpolator(CubicBezierInterpolator.EASE_IN);
                collapseAnimator.addUpdateListener(new AudioPlayerAlert$CoverContainer$$ExternalSyntheticLambda0(prevImageView, currImageView));
                collapseAnimator.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        prevImageView.setVisibility(8);
                        prevImageView.setImageDrawable((Drawable) null);
                        prevImageView.setAlpha(1.0f);
                    }
                });
                this.animatorSet.playSequentially(new Animator[]{collapseAnimator, expandAnimator});
            } else {
                this.animatorSet.play(expandAnimator);
            }
            this.animatorSet.start();
        }

        static /* synthetic */ void lambda$switchImageViews$1(BackupImageView currImageView, boolean hasBitmapImage, ValueAnimator a) {
            float animatedValue = ((Float) a.getAnimatedValue()).floatValue();
            currImageView.setScaleX(animatedValue);
            currImageView.setScaleY(animatedValue);
            if (!hasBitmapImage) {
                currImageView.setAlpha(a.getAnimatedFraction());
            }
        }

        static /* synthetic */ void lambda$switchImageViews$2(BackupImageView prevImageView, BackupImageView currImageView, ValueAnimator a) {
            float animatedValue = ((Float) a.getAnimatedValue()).floatValue();
            prevImageView.setScaleX(animatedValue);
            prevImageView.setScaleY(animatedValue);
            float fraction = a.getAnimatedFraction();
            if (fraction > 0.25f && !currImageView.getImageReceiver().hasBitmapImage()) {
                prevImageView.setAlpha(1.0f - ((fraction - 0.25f) * 1.3333334f));
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
        public void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
            LinearGradient linearGradient = new LinearGradient((float) this.gradientSize, 0.0f, 0.0f, 0.0f, 0, -16777216, Shader.TileMode.CLAMP);
            this.gradientShader = linearGradient;
            this.gradientPaint.setShader(linearGradient);
        }

        /* access modifiers changed from: protected */
        public boolean drawChild(Canvas canvas, View child, long drawingTime) {
            Canvas canvas2 = canvas;
            TextView[] textViewArr = this.textViews;
            int index = child == textViewArr[0] ? 0 : 1;
            boolean hasStableRect = false;
            if (!(this.stableOffest <= 0 || textViewArr[this.activeIndex].getAlpha() == 1.0f || this.textViews[this.activeIndex].getLayout() == null)) {
                float x1 = this.textViews[this.activeIndex].getLayout().getPrimaryHorizontal(0);
                float x2 = this.textViews[this.activeIndex].getLayout().getPrimaryHorizontal(this.stableOffest);
                hasStableRect = true;
                if (x1 == x2) {
                    hasStableRect = false;
                } else if (x2 > x1) {
                    this.rectF.set(x1, 0.0f, x2, (float) getMeasuredHeight());
                } else {
                    this.rectF.set(x2, 0.0f, x1, (float) getMeasuredHeight());
                }
                if (hasStableRect && index == this.activeIndex) {
                    canvas.save();
                    canvas2.clipRect(this.rectF);
                    this.textViews[0].draw(canvas2);
                    canvas.restore();
                }
            }
            boolean hasStableRect2 = hasStableRect;
            if (this.clipProgress[index] <= 0.0f && !hasStableRect2) {
                return super.drawChild(canvas, child, drawingTime);
            }
            int width = child.getWidth();
            int height = child.getHeight();
            int saveCount = canvas.saveLayer(0.0f, 0.0f, (float) width, (float) height, (Paint) null, 31);
            boolean result = super.drawChild(canvas, child, drawingTime);
            float gradientStart = (1.0f - this.clipProgress[index]) * ((float) width);
            float gradientEnd = gradientStart + ((float) this.gradientSize);
            this.gradientMatrix.setTranslate(gradientStart, 0.0f);
            this.gradientShader.setLocalMatrix(this.gradientMatrix);
            canvas.drawRect(gradientStart, 0.0f, gradientEnd, (float) height, this.gradientPaint);
            if (((float) width) > gradientEnd) {
                canvas.drawRect(gradientEnd, 0.0f, (float) width, (float) height, this.erasePaint);
            }
            if (hasStableRect2) {
                canvas2.drawRect(this.rectF, this.erasePaint);
            }
            canvas2.restoreToCount(saveCount);
            return result;
        }

        public void setText(CharSequence text) {
            setText(text, true);
        }

        public void setText(CharSequence text, boolean animated) {
            CharSequence charSequence = text;
            CharSequence currentText = this.textViews[this.activeIndex].getText();
            if (TextUtils.isEmpty(currentText) || !animated) {
                this.textViews[this.activeIndex].setText(charSequence);
            } else if (!TextUtils.equals(charSequence, currentText)) {
                this.stableOffest = 0;
                int n = Math.min(text.length(), currentText.length());
                int i = 0;
                while (i < n && charSequence.charAt(i) == currentText.charAt(i)) {
                    this.stableOffest++;
                    i++;
                }
                if (this.stableOffest <= 3) {
                    this.stableOffest = -1;
                }
                int index = this.activeIndex == 0 ? 1 : 0;
                final int prevIndex = this.activeIndex;
                this.activeIndex = index;
                AnimatorSet animatorSet2 = this.animatorSet;
                if (animatorSet2 != null) {
                    animatorSet2.cancel();
                }
                AnimatorSet animatorSet3 = new AnimatorSet();
                this.animatorSet = animatorSet3;
                animatorSet3.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        ClippingTextViewSwitcher.this.textViews[prevIndex].setVisibility(8);
                    }
                });
                this.textViews[index].setText(charSequence);
                this.textViews[index].bringToFront();
                this.textViews[index].setVisibility(0);
                ValueAnimator collapseAnimator = ValueAnimator.ofFloat(new float[]{this.clipProgress[prevIndex], 0.75f});
                collapseAnimator.setDuration(200);
                collapseAnimator.addUpdateListener(new AudioPlayerAlert$ClippingTextViewSwitcher$$ExternalSyntheticLambda0(this, prevIndex));
                ValueAnimator expandAnimator = ValueAnimator.ofFloat(new float[]{this.clipProgress[index], 0.0f});
                expandAnimator.setStartDelay(100);
                expandAnimator.setDuration(200);
                expandAnimator.addUpdateListener(new AudioPlayerAlert$ClippingTextViewSwitcher$$ExternalSyntheticLambda1(this, index));
                ObjectAnimator fadeOutAnimator = ObjectAnimator.ofFloat(this.textViews[prevIndex], View.ALPHA, new float[]{0.0f});
                fadeOutAnimator.setStartDelay(75);
                int i2 = prevIndex;
                fadeOutAnimator.setDuration(150);
                ObjectAnimator fadeInAnimator = ObjectAnimator.ofFloat(this.textViews[index], View.ALPHA, new float[]{1.0f});
                fadeInAnimator.setStartDelay(75);
                fadeInAnimator.setDuration(150);
                this.animatorSet.playTogether(new Animator[]{collapseAnimator, expandAnimator, fadeOutAnimator, fadeInAnimator});
                this.animatorSet.start();
            }
        }

        /* renamed from: lambda$setText$0$org-telegram-ui-Components-AudioPlayerAlert$ClippingTextViewSwitcher  reason: not valid java name */
        public /* synthetic */ void m3608x278a1299(int prevIndex, ValueAnimator a) {
            this.clipProgress[prevIndex] = ((Float) a.getAnimatedValue()).floatValue();
            invalidate();
        }

        /* renamed from: lambda$setText$1$org-telegram-ui-Components-AudioPlayerAlert$ClippingTextViewSwitcher  reason: not valid java name */
        public /* synthetic */ void m3609x606a7338(int index, ValueAnimator a) {
            this.clipProgress[index] = ((Float) a.getAnimatedValue()).floatValue();
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
