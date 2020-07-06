package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
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
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
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
import org.telegram.ui.Components.AudioPlayerAlert;
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
    private TextView authorTextView;
    /* access modifiers changed from: private */
    public BackupImageView bigAlbumConver;
    /* access modifiers changed from: private */
    public boolean blurredAnimationInProgress;
    /* access modifiers changed from: private */
    public FrameLayout blurredView;
    /* access modifiers changed from: private */
    public View[] buttons = new View[5];
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
    public boolean inFullSize;
    /* access modifiers changed from: private */
    public int lastDuration;
    /* access modifiers changed from: private */
    public int lastTime;
    /* access modifiers changed from: private */
    public LinearLayoutManager layoutManager;
    /* access modifiers changed from: private */
    public ListAdapter listAdapter;
    /* access modifiers changed from: private */
    public RecyclerListView listView;
    private ImageView nextButton;
    private ActionBarMenuItem optionsButton;
    private LaunchActivity parentActivity;
    private BackupImageView placeholderImageView;
    private ImageView playButton;
    /* access modifiers changed from: private */
    public ImageView playbackSpeedButton;
    /* access modifiers changed from: private */
    public FrameLayout playerLayout;
    /* access modifiers changed from: private */
    public View playerShadow;
    /* access modifiers changed from: private */
    public ArrayList<MessageObject> playlist;
    private ImageView prevButton;
    private LineProgressView progressView;
    private ActionBarMenuItem repeatButton;
    private ActionBarMenuSubItem repeatListItem;
    private ActionBarMenuSubItem repeatSongItem;
    private ActionBarMenuSubItem reverseOrderItem;
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
    private SeekBarView seekBarView;
    private ActionBarMenuSubItem shuffleListItem;
    private SimpleTextView timeTextView;
    private TextView titleTextView;

    static /* synthetic */ boolean lambda$new$8(View view, MotionEvent motionEvent) {
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
    public AudioPlayerAlert(android.content.Context r22) {
        /*
            r21 = this;
            r0 = r21
            r1 = r22
            r2 = 1
            r0.<init>(r1, r2)
            r3 = 5
            android.view.View[] r3 = new android.view.View[r3]
            r0.buttons = r3
            r0.scrollToSong = r2
            r3 = -1
            r0.searchOpenPosition = r3
            r4 = 2147483647(0x7fffffff, float:NaN)
            r0.scrollOffsetY = r4
            org.telegram.messenger.MediaController r4 = org.telegram.messenger.MediaController.getInstance()
            org.telegram.messenger.MessageObject r4 = r4.getPlayingMessageObject()
            if (r4 == 0) goto L_0x0026
            int r5 = r4.currentAccount
            r0.currentAccount = r5
            goto L_0x002a
        L_0x0026:
            int r5 = org.telegram.messenger.UserConfig.selectedAccount
            r0.currentAccount = r5
        L_0x002a:
            r5 = r1
            org.telegram.ui.LaunchActivity r5 = (org.telegram.ui.LaunchActivity) r5
            r0.parentActivity = r5
            int r5 = r0.currentAccount
            org.telegram.messenger.DownloadController r5 = org.telegram.messenger.DownloadController.getInstance(r5)
            int r5 = r5.generateObserverTag()
            r0.TAG = r5
            int r5 = r0.currentAccount
            org.telegram.messenger.NotificationCenter r5 = org.telegram.messenger.NotificationCenter.getInstance(r5)
            int r6 = org.telegram.messenger.NotificationCenter.messagePlayingDidReset
            r5.addObserver(r0, r6)
            int r5 = r0.currentAccount
            org.telegram.messenger.NotificationCenter r5 = org.telegram.messenger.NotificationCenter.getInstance(r5)
            int r6 = org.telegram.messenger.NotificationCenter.messagePlayingPlayStateChanged
            r5.addObserver(r0, r6)
            int r5 = r0.currentAccount
            org.telegram.messenger.NotificationCenter r5 = org.telegram.messenger.NotificationCenter.getInstance(r5)
            int r6 = org.telegram.messenger.NotificationCenter.messagePlayingDidStart
            r5.addObserver(r0, r6)
            int r5 = r0.currentAccount
            org.telegram.messenger.NotificationCenter r5 = org.telegram.messenger.NotificationCenter.getInstance(r5)
            int r6 = org.telegram.messenger.NotificationCenter.messagePlayingProgressDidChanged
            r5.addObserver(r0, r6)
            int r5 = r0.currentAccount
            org.telegram.messenger.NotificationCenter r5 = org.telegram.messenger.NotificationCenter.getInstance(r5)
            int r6 = org.telegram.messenger.NotificationCenter.fileDidLoad
            r5.addObserver(r0, r6)
            int r5 = r0.currentAccount
            org.telegram.messenger.NotificationCenter r5 = org.telegram.messenger.NotificationCenter.getInstance(r5)
            int r6 = org.telegram.messenger.NotificationCenter.musicDidLoad
            r5.addObserver(r0, r6)
            org.telegram.ui.Components.AudioPlayerAlert$1 r5 = new org.telegram.ui.Components.AudioPlayerAlert$1
            r5.<init>(r1)
            r0.containerView = r5
            r6 = 0
            r5.setWillNotDraw(r6)
            android.view.ViewGroup r5 = r0.containerView
            int r7 = r0.backgroundPaddingLeft
            r5.setPadding(r7, r6, r7, r6)
            org.telegram.ui.Components.AudioPlayerAlert$2 r5 = new org.telegram.ui.Components.AudioPlayerAlert$2
            r5.<init>(r1)
            r0.actionBar = r5
            java.lang.String r7 = "player_actionBar"
            int r7 = org.telegram.ui.ActionBar.Theme.getColor(r7)
            r5.setBackgroundColor(r7)
            org.telegram.ui.ActionBar.ActionBar r5 = r0.actionBar
            r7 = 2131165432(0x7var_f8, float:1.794508E38)
            r5.setBackButtonImage(r7)
            org.telegram.ui.ActionBar.ActionBar r5 = r0.actionBar
            java.lang.String r7 = "player_actionBarTitle"
            int r8 = org.telegram.ui.ActionBar.Theme.getColor(r7)
            r5.setItemsColor(r8, r6)
            org.telegram.ui.ActionBar.ActionBar r5 = r0.actionBar
            java.lang.String r8 = "player_actionBarSelector"
            int r8 = org.telegram.ui.ActionBar.Theme.getColor(r8)
            r5.setItemsBackgroundColor(r8, r6)
            org.telegram.ui.ActionBar.ActionBar r5 = r0.actionBar
            int r8 = org.telegram.ui.ActionBar.Theme.getColor(r7)
            r5.setTitleColor(r8)
            org.telegram.ui.ActionBar.ActionBar r5 = r0.actionBar
            r8 = 2131624314(0x7f0e017a, float:1.8875804E38)
            java.lang.String r9 = "AttachMusic"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r9, r8)
            r5.setTitle(r8)
            org.telegram.ui.ActionBar.ActionBar r5 = r0.actionBar
            java.lang.String r8 = "player_actionBarSubtitle"
            int r8 = org.telegram.ui.ActionBar.Theme.getColor(r8)
            r5.setSubtitleColor(r8)
            org.telegram.ui.ActionBar.ActionBar r5 = r0.actionBar
            r5.setOccupyStatusBar(r6)
            org.telegram.ui.ActionBar.ActionBar r5 = r0.actionBar
            r8 = 0
            r5.setAlpha(r8)
            if (r4 == 0) goto L_0x015f
            long r4 = r4.getDialogId()
            int r9 = (int) r4
            r10 = 32
            long r4 = r4 >> r10
            int r5 = (int) r4
            if (r9 == 0) goto L_0x0130
            if (r9 <= 0) goto L_0x0117
            int r4 = r0.currentAccount
            org.telegram.messenger.MessagesController r4 = org.telegram.messenger.MessagesController.getInstance(r4)
            java.lang.Integer r5 = java.lang.Integer.valueOf(r9)
            org.telegram.tgnet.TLRPC$User r4 = r4.getUser(r5)
            if (r4 == 0) goto L_0x015f
            org.telegram.ui.ActionBar.ActionBar r5 = r0.actionBar
            java.lang.String r9 = r4.first_name
            java.lang.String r4 = r4.last_name
            java.lang.String r4 = org.telegram.messenger.ContactsController.formatName(r9, r4)
            r5.setTitle(r4)
            goto L_0x015f
        L_0x0117:
            int r4 = r0.currentAccount
            org.telegram.messenger.MessagesController r4 = org.telegram.messenger.MessagesController.getInstance(r4)
            int r5 = -r9
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            org.telegram.tgnet.TLRPC$Chat r4 = r4.getChat(r5)
            if (r4 == 0) goto L_0x015f
            org.telegram.ui.ActionBar.ActionBar r5 = r0.actionBar
            java.lang.String r4 = r4.title
            r5.setTitle(r4)
            goto L_0x015f
        L_0x0130:
            int r4 = r0.currentAccount
            org.telegram.messenger.MessagesController r4 = org.telegram.messenger.MessagesController.getInstance(r4)
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            org.telegram.tgnet.TLRPC$EncryptedChat r4 = r4.getEncryptedChat(r5)
            if (r4 == 0) goto L_0x015f
            int r5 = r0.currentAccount
            org.telegram.messenger.MessagesController r5 = org.telegram.messenger.MessagesController.getInstance(r5)
            int r4 = r4.user_id
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
            org.telegram.tgnet.TLRPC$User r4 = r5.getUser(r4)
            if (r4 == 0) goto L_0x015f
            org.telegram.ui.ActionBar.ActionBar r5 = r0.actionBar
            java.lang.String r9 = r4.first_name
            java.lang.String r4 = r4.last_name
            java.lang.String r4 = org.telegram.messenger.ContactsController.formatName(r9, r4)
            r5.setTitle(r4)
        L_0x015f:
            org.telegram.ui.ActionBar.ActionBar r4 = r0.actionBar
            org.telegram.ui.ActionBar.ActionBarMenu r4 = r4.createMenu()
            r5 = 2131165442(0x7var_, float:1.7945101E38)
            org.telegram.ui.ActionBar.ActionBarMenuItem r4 = r4.addItem((int) r6, (int) r5)
            r4.setIsSearchField(r2)
            org.telegram.ui.Components.AudioPlayerAlert$3 r5 = new org.telegram.ui.Components.AudioPlayerAlert$3
            r5.<init>()
            r4.setActionBarMenuItemSearchListener(r5)
            r0.searchItem = r4
            java.lang.String r5 = "Search"
            r9 = 2131626731(0x7f0e0aeb, float:1.8880706E38)
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r5, r9)
            r4.setContentDescription(r10)
            org.telegram.ui.ActionBar.ActionBarMenuItem r4 = r0.searchItem
            org.telegram.ui.Components.EditTextBoldCursor r4 = r4.getSearchField()
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r5, r9)
            r4.setHint(r5)
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r7)
            r4.setTextColor(r5)
            java.lang.String r5 = "player_time"
            int r9 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            r4.setHintTextColor(r9)
            int r9 = org.telegram.ui.ActionBar.Theme.getColor(r7)
            r4.setCursorColor(r9)
            org.telegram.ui.ActionBar.ActionBar r4 = r0.actionBar
            org.telegram.ui.Components.AudioPlayerAlert$4 r9 = new org.telegram.ui.Components.AudioPlayerAlert$4
            r9.<init>()
            r4.setActionBarMenuOnItemClick(r9)
            android.view.View r4 = new android.view.View
            r4.<init>(r1)
            r0.actionBarShadow = r4
            r4.setAlpha(r8)
            android.view.View r4 = r0.actionBarShadow
            r9 = 2131165429(0x7var_f5, float:1.7945075E38)
            r4.setBackgroundResource(r9)
            android.view.View r4 = new android.view.View
            r4.<init>(r1)
            r0.playerShadow = r4
            java.lang.String r9 = "dialogShadowLine"
            int r9 = org.telegram.ui.ActionBar.Theme.getColor(r9)
            r4.setBackgroundColor(r9)
            org.telegram.ui.Components.AudioPlayerAlert$5 r4 = new org.telegram.ui.Components.AudioPlayerAlert$5
            r4.<init>(r1)
            r0.playerLayout = r4
            org.telegram.ui.Components.AudioPlayerAlert$6 r4 = new org.telegram.ui.Components.AudioPlayerAlert$6
            r4.<init>(r1)
            r0.placeholderImageView = r4
            org.telegram.messenger.ImageReceiver r4 = r4.getImageReceiver()
            org.telegram.ui.Components.-$$Lambda$AudioPlayerAlert$Ij3VgNGSO2JGjeC-t6EfuSpOyvw r9 = new org.telegram.ui.Components.-$$Lambda$AudioPlayerAlert$Ij3VgNGSO2JGjeC-t6EfuSpOyvw
            r9.<init>()
            r4.setDelegate(r9)
            org.telegram.ui.Components.BackupImageView r4 = r0.placeholderImageView
            r9 = 1082130432(0x40800000, float:4.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            r4.setRoundRadius(r9)
            android.widget.FrameLayout r4 = r0.playerLayout
            org.telegram.ui.Components.BackupImageView r9 = r0.placeholderImageView
            r10 = 44
            r11 = 1110441984(0x42300000, float:44.0)
            r12 = 53
            r13 = 0
            r14 = 1101004800(0x41a00000, float:20.0)
            r15 = 1101004800(0x41a00000, float:20.0)
            r16 = 0
            android.widget.FrameLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createFrame(r10, r11, r12, r13, r14, r15, r16)
            r4.addView(r9, r10)
            android.widget.TextView r4 = new android.widget.TextView
            r4.<init>(r1)
            r0.titleTextView = r4
            int r7 = org.telegram.ui.ActionBar.Theme.getColor(r7)
            r4.setTextColor(r7)
            android.widget.TextView r4 = r0.titleTextView
            r7 = 1099431936(0x41880000, float:17.0)
            r4.setTextSize(r2, r7)
            android.widget.TextView r4 = r0.titleTextView
            java.lang.String r9 = "fonts/rmedium.ttf"
            android.graphics.Typeface r10 = org.telegram.messenger.AndroidUtilities.getTypeface(r9)
            r4.setTypeface(r10)
            android.widget.TextView r4 = r0.titleTextView
            android.text.TextUtils$TruncateAt r10 = android.text.TextUtils.TruncateAt.END
            r4.setEllipsize(r10)
            android.widget.TextView r4 = r0.titleTextView
            r4.setSingleLine(r2)
            android.widget.FrameLayout r4 = r0.playerLayout
            android.widget.TextView r10 = r0.titleTextView
            r11 = -1
            r12 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r13 = 51
            r16 = 1116733440(0x42900000, float:72.0)
            r17 = 0
            android.widget.FrameLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createFrame(r11, r12, r13, r14, r15, r16, r17)
            r4.addView(r10, r11)
            android.widget.TextView r4 = new android.widget.TextView
            r4.<init>(r1)
            r0.authorTextView = r4
            int r10 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            r4.setTextColor(r10)
            android.widget.TextView r4 = r0.authorTextView
            r10 = 1095761920(0x41500000, float:13.0)
            r4.setTextSize(r2, r10)
            android.widget.TextView r4 = r0.authorTextView
            android.text.TextUtils$TruncateAt r10 = android.text.TextUtils.TruncateAt.END
            r4.setEllipsize(r10)
            android.widget.TextView r4 = r0.authorTextView
            r4.setSingleLine(r2)
            android.widget.FrameLayout r4 = r0.playerLayout
            android.widget.TextView r10 = r0.authorTextView
            r11 = -1
            r15 = 1111228416(0x423CLASSNAME, float:47.0)
            android.widget.FrameLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createFrame(r11, r12, r13, r14, r15, r16, r17)
            r4.addView(r10, r11)
            org.telegram.ui.Components.SeekBarView r4 = new org.telegram.ui.Components.SeekBarView
            r4.<init>(r1)
            r0.seekBarView = r4
            org.telegram.ui.Components.AudioPlayerAlert$7 r10 = new org.telegram.ui.Components.AudioPlayerAlert$7
            r10.<init>()
            r4.setDelegate(r10)
            org.telegram.ui.Components.SeekBarView r4 = r0.seekBarView
            r4.setReportChanges(r2)
            android.widget.FrameLayout r4 = r0.playerLayout
            org.telegram.ui.Components.SeekBarView r10 = r0.seekBarView
            r11 = -1
            r12 = 1108869120(0x42180000, float:38.0)
            r14 = 1084227584(0x40a00000, float:5.0)
            r15 = 1116471296(0x428CLASSNAME, float:70.0)
            r16 = 1084227584(0x40a00000, float:5.0)
            android.widget.FrameLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createFrame(r11, r12, r13, r14, r15, r16, r17)
            r4.addView(r10, r11)
            org.telegram.ui.Components.LineProgressView r4 = new org.telegram.ui.Components.LineProgressView
            r4.<init>(r1)
            r0.progressView = r4
            r10 = 4
            r4.setVisibility(r10)
            org.telegram.ui.Components.LineProgressView r4 = r0.progressView
            java.lang.String r11 = "player_progressBackground"
            int r11 = org.telegram.ui.ActionBar.Theme.getColor(r11)
            r4.setBackgroundColor(r11)
            org.telegram.ui.Components.LineProgressView r4 = r0.progressView
            java.lang.String r11 = "player_progress"
            int r11 = org.telegram.ui.ActionBar.Theme.getColor(r11)
            r4.setProgressColor(r11)
            android.widget.FrameLayout r4 = r0.playerLayout
            org.telegram.ui.Components.LineProgressView r11 = r0.progressView
            r12 = -1
            r13 = 1073741824(0x40000000, float:2.0)
            r14 = 51
            r15 = 1101529088(0x41a80000, float:21.0)
            r16 = 1119092736(0x42b40000, float:90.0)
            r17 = 1101529088(0x41a80000, float:21.0)
            r18 = 0
            android.widget.FrameLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createFrame(r12, r13, r14, r15, r16, r17, r18)
            r4.addView(r11, r12)
            org.telegram.ui.ActionBar.SimpleTextView r4 = new org.telegram.ui.ActionBar.SimpleTextView
            r4.<init>(r1)
            r0.timeTextView = r4
            r11 = 12
            r4.setTextSize(r11)
            org.telegram.ui.ActionBar.SimpleTextView r4 = r0.timeTextView
            java.lang.String r11 = "0:00"
            r4.setText(r11)
            org.telegram.ui.ActionBar.SimpleTextView r4 = r0.timeTextView
            int r11 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            r4.setTextColor(r11)
            org.telegram.ui.ActionBar.SimpleTextView r4 = r0.timeTextView
            r11 = 2
            r4.setImportantForAccessibility(r11)
            android.widget.FrameLayout r4 = r0.playerLayout
            org.telegram.ui.ActionBar.SimpleTextView r12 = r0.timeTextView
            r13 = 100
            r14 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r15 = 51
            r16 = 1101004800(0x41a00000, float:20.0)
            r17 = 1120141312(0x42CLASSNAME, float:98.0)
            r19 = 0
            android.widget.FrameLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createFrame(r13, r14, r15, r16, r17, r18, r19)
            r4.addView(r12, r13)
            android.widget.TextView r4 = new android.widget.TextView
            r4.<init>(r1)
            r0.durationTextView = r4
            r12 = 1094713344(0x41400000, float:12.0)
            r4.setTextSize(r2, r12)
            android.widget.TextView r4 = r0.durationTextView
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            r4.setTextColor(r5)
            android.widget.TextView r4 = r0.durationTextView
            r5 = 17
            r4.setGravity(r5)
            android.widget.TextView r4 = r0.durationTextView
            r4.setImportantForAccessibility(r11)
            android.widget.FrameLayout r4 = r0.playerLayout
            android.widget.TextView r12 = r0.durationTextView
            r13 = -2
            r15 = 53
            r16 = 0
            r17 = 1119879168(0x42CLASSNAME, float:96.0)
            r18 = 1101004800(0x41a00000, float:20.0)
            android.widget.FrameLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createFrame(r13, r14, r15, r16, r17, r18, r19)
            r4.addView(r12, r13)
            android.widget.ImageView r4 = new android.widget.ImageView
            r4.<init>(r1)
            r0.playbackSpeedButton = r4
            android.widget.ImageView$ScaleType r12 = android.widget.ImageView.ScaleType.CENTER
            r4.setScaleType(r12)
            android.widget.ImageView r4 = r0.playbackSpeedButton
            r12 = 2131165990(0x7var_, float:1.7946213E38)
            r4.setImageResource(r12)
            android.widget.ImageView r4 = r0.playbackSpeedButton
            r12 = 2131624005(0x7f0e0045, float:1.8875177E38)
            java.lang.String r13 = "AccDescrPlayerSpeed"
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r13, r12)
            r4.setContentDescription(r12)
            float r4 = org.telegram.messenger.AndroidUtilities.density
            r12 = 1077936128(0x40400000, float:3.0)
            int r4 = (r4 > r12 ? 1 : (r4 == r12 ? 0 : -1))
            if (r4 < 0) goto L_0x037f
            android.widget.ImageView r4 = r0.playbackSpeedButton
            r4.setPadding(r6, r2, r6, r6)
        L_0x037f:
            android.widget.FrameLayout r4 = r0.playerLayout
            android.widget.ImageView r13 = r0.playbackSpeedButton
            r14 = 36
            r15 = 1108344832(0x42100000, float:36.0)
            r16 = 53
            r17 = 0
            r18 = 1118568448(0x42aCLASSNAME, float:86.0)
            r19 = 1101004800(0x41a00000, float:20.0)
            r20 = 0
            android.widget.FrameLayout$LayoutParams r14 = org.telegram.ui.Components.LayoutHelper.createFrame(r14, r15, r16, r17, r18, r19, r20)
            r4.addView(r13, r14)
            android.widget.ImageView r4 = r0.playbackSpeedButton
            org.telegram.ui.Components.-$$Lambda$AudioPlayerAlert$L6a3eFAcdzUb5tmijbnBD0GiP9c r13 = new org.telegram.ui.Components.-$$Lambda$AudioPlayerAlert$L6a3eFAcdzUb5tmijbnBD0GiP9c
            r13.<init>()
            r4.setOnClickListener(r13)
            r21.updatePlaybackButton()
            org.telegram.ui.Components.AudioPlayerAlert$8 r4 = new org.telegram.ui.Components.AudioPlayerAlert$8
            r4.<init>(r1)
            android.widget.FrameLayout r13 = r0.playerLayout
            r14 = -1
            r15 = 1115947008(0x42840000, float:66.0)
            r16 = 51
            r18 = 1121845248(0x42de0000, float:111.0)
            r19 = 0
            android.widget.FrameLayout$LayoutParams r14 = org.telegram.ui.Components.LayoutHelper.createFrame(r14, r15, r16, r17, r18, r19, r20)
            r13.addView(r4, r14)
            android.view.View[] r13 = r0.buttons
            org.telegram.ui.ActionBar.ActionBarMenuItem r14 = new org.telegram.ui.ActionBar.ActionBarMenuItem
            r15 = 0
            r14.<init>(r1, r15, r6, r6)
            r0.repeatButton = r14
            r13[r6] = r14
            r14.setLongClickEnabled(r6)
            org.telegram.ui.ActionBar.ActionBarMenuItem r13 = r0.repeatButton
            r13.setPopupAnimationEnabled(r6)
            org.telegram.ui.ActionBar.ActionBarMenuItem r13 = r0.repeatButton
            r13.setShowSubmenuByMove(r6)
            org.telegram.ui.ActionBar.ActionBarMenuItem r13 = r0.repeatButton
            r14 = 1126563840(0x43260000, float:166.0)
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r14)
            int r14 = -r14
            r13.setAdditionalYOffset(r14)
            int r13 = android.os.Build.VERSION.SDK_INT
            java.lang.String r14 = "listSelectorSDK21"
            r8 = 21
            if (r13 < r8) goto L_0x03fc
            org.telegram.ui.ActionBar.ActionBarMenuItem r13 = r0.repeatButton
            int r12 = org.telegram.ui.ActionBar.Theme.getColor(r14)
            r18 = 1099956224(0x41900000, float:18.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r18)
            android.graphics.drawable.Drawable r7 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r12, r2, r7)
            r13.setBackgroundDrawable(r7)
        L_0x03fc:
            org.telegram.ui.ActionBar.ActionBarMenuItem r7 = r0.repeatButton
            r12 = 48
            r13 = 51
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r12, r12, r13)
            r4.addView(r7, r3)
            org.telegram.ui.ActionBar.ActionBarMenuItem r3 = r0.repeatButton
            org.telegram.ui.Components.-$$Lambda$AudioPlayerAlert$lbtPQBFbXxQUDmU4R4w6uSm1EGI r7 = new org.telegram.ui.Components.-$$Lambda$AudioPlayerAlert$lbtPQBFbXxQUDmU4R4w6uSm1EGI
            r7.<init>()
            r3.setOnClickListener(r7)
            org.telegram.ui.ActionBar.ActionBarMenuItem r3 = r0.repeatButton
            r7 = 2131165832(0x7var_, float:1.7945892E38)
            r5 = 2131626632(0x7f0e0a88, float:1.8880506E38)
            java.lang.String r6 = "RepeatSong"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r6, r5)
            r6 = 3
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r3 = r3.addSubItem(r6, r7, r5)
            r0.repeatSongItem = r3
            org.telegram.ui.ActionBar.ActionBarMenuItem r3 = r0.repeatButton
            r5 = 2131165831(0x7var_, float:1.794589E38)
            r7 = 2131626629(0x7f0e0a85, float:1.88805E38)
            java.lang.String r15 = "RepeatList"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r15, r7)
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r3 = r3.addSubItem(r10, r5, r7)
            r0.repeatListItem = r3
            org.telegram.ui.ActionBar.ActionBarMenuItem r3 = r0.repeatButton
            r5 = 2131165833(0x7var_, float:1.7945894E38)
            r7 = 2131626929(0x7f0e0bb1, float:1.8881108E38)
            java.lang.String r15 = "ShuffleList"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r15, r7)
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r3 = r3.addSubItem(r11, r5, r7)
            r0.shuffleListItem = r3
            org.telegram.ui.ActionBar.ActionBarMenuItem r3 = r0.repeatButton
            r5 = 2131165825(0x7var_, float:1.7945878E38)
            r7 = 2131626700(0x7f0e0acc, float:1.8880644E38)
            java.lang.String r15 = "ReverseOrder"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r15, r7)
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r3 = r3.addSubItem(r2, r5, r7)
            r0.reverseOrderItem = r3
            org.telegram.ui.ActionBar.ActionBarMenuItem r3 = r0.repeatButton
            org.telegram.ui.Components.-$$Lambda$AudioPlayerAlert$IevMM0nISen7ZvHChKpl6gdg6G8 r5 = new org.telegram.ui.Components.-$$Lambda$AudioPlayerAlert$IevMM0nISen7ZvHChKpl6gdg6G8
            r5.<init>()
            r3.setDelegate(r5)
            android.view.View[] r3 = r0.buttons
            android.widget.ImageView r5 = new android.widget.ImageView
            r5.<init>(r1)
            r0.prevButton = r5
            r3[r2] = r5
            android.widget.ImageView$ScaleType r3 = android.widget.ImageView.ScaleType.CENTER
            r5.setScaleType(r3)
            android.widget.ImageView r3 = r0.prevButton
            android.graphics.PorterDuffColorFilter r5 = new android.graphics.PorterDuffColorFilter
            java.lang.String r7 = "player_button"
            int r15 = org.telegram.ui.ActionBar.Theme.getColor(r7)
            android.graphics.PorterDuff$Mode r10 = android.graphics.PorterDuff.Mode.MULTIPLY
            r5.<init>(r15, r10)
            r3.setColorFilter(r5)
            android.widget.ImageView r3 = r0.prevButton
            r5 = 2131165828(0x7var_, float:1.7945884E38)
            r3.setImageResource(r5)
            int r3 = android.os.Build.VERSION.SDK_INT
            if (r3 < r8) goto L_0x04af
            android.widget.ImageView r3 = r0.prevButton
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r14)
            r10 = 1102053376(0x41b00000, float:22.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            android.graphics.drawable.Drawable r5 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r5, r2, r10)
            r3.setBackgroundDrawable(r5)
        L_0x04af:
            android.widget.ImageView r3 = r0.prevButton
            android.widget.FrameLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createFrame(r12, r12, r13)
            r4.addView(r3, r5)
            android.widget.ImageView r3 = r0.prevButton
            org.telegram.ui.Components.-$$Lambda$AudioPlayerAlert$4DMl9_Is0AQM3ngWCdPt5fhxPmc r5 = org.telegram.ui.Components.$$Lambda$AudioPlayerAlert$4DMl9_Is0AQM3ngWCdPt5fhxPmc.INSTANCE
            r3.setOnClickListener(r5)
            android.widget.ImageView r3 = r0.prevButton
            r5 = 2131624006(0x7f0e0046, float:1.887518E38)
            java.lang.String r10 = "AccDescrPrevious"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r10, r5)
            r3.setContentDescription(r5)
            android.view.View[] r3 = r0.buttons
            android.widget.ImageView r5 = new android.widget.ImageView
            r5.<init>(r1)
            r0.playButton = r5
            r3[r11] = r5
            android.widget.ImageView$ScaleType r3 = android.widget.ImageView.ScaleType.CENTER
            r5.setScaleType(r3)
            android.widget.ImageView r3 = r0.playButton
            r5 = 2131165827(0x7var_, float:1.7945882E38)
            r3.setImageResource(r5)
            android.widget.ImageView r3 = r0.playButton
            android.graphics.PorterDuffColorFilter r5 = new android.graphics.PorterDuffColorFilter
            int r10 = org.telegram.ui.ActionBar.Theme.getColor(r7)
            android.graphics.PorterDuff$Mode r15 = android.graphics.PorterDuff.Mode.MULTIPLY
            r5.<init>(r10, r15)
            r3.setColorFilter(r5)
            int r3 = android.os.Build.VERSION.SDK_INT
            if (r3 < r8) goto L_0x050c
            android.widget.ImageView r3 = r0.playButton
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r14)
            r10 = 1103101952(0x41CLASSNAME, float:24.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            android.graphics.drawable.Drawable r5 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r5, r2, r10)
            r3.setBackgroundDrawable(r5)
        L_0x050c:
            android.widget.ImageView r3 = r0.playButton
            android.widget.FrameLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createFrame(r12, r12, r13)
            r4.addView(r3, r5)
            android.widget.ImageView r3 = r0.playButton
            org.telegram.ui.Components.-$$Lambda$AudioPlayerAlert$_25s4XwmMXC5vKIS0zNvR6jdxfg r5 = org.telegram.ui.Components.$$Lambda$AudioPlayerAlert$_25s4XwmMXC5vKIS0zNvR6jdxfg.INSTANCE
            r3.setOnClickListener(r5)
            android.view.View[] r3 = r0.buttons
            android.widget.ImageView r5 = new android.widget.ImageView
            r5.<init>(r1)
            r0.nextButton = r5
            r3[r6] = r5
            android.widget.ImageView$ScaleType r3 = android.widget.ImageView.ScaleType.CENTER
            r5.setScaleType(r3)
            android.widget.ImageView r3 = r0.nextButton
            android.graphics.PorterDuffColorFilter r5 = new android.graphics.PorterDuffColorFilter
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r7)
            android.graphics.PorterDuff$Mode r10 = android.graphics.PorterDuff.Mode.MULTIPLY
            r5.<init>(r6, r10)
            r3.setColorFilter(r5)
            android.widget.ImageView r3 = r0.nextButton
            r5 = 2131165824(0x7var_, float:1.7945876E38)
            r3.setImageResource(r5)
            int r3 = android.os.Build.VERSION.SDK_INT
            if (r3 < r8) goto L_0x055b
            android.widget.ImageView r3 = r0.nextButton
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r14)
            r6 = 1102053376(0x41b00000, float:22.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            android.graphics.drawable.Drawable r5 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r5, r2, r6)
            r3.setBackgroundDrawable(r5)
        L_0x055b:
            android.widget.ImageView r3 = r0.nextButton
            android.widget.FrameLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createFrame(r12, r12, r13)
            r4.addView(r3, r5)
            android.widget.ImageView r3 = r0.nextButton
            org.telegram.ui.Components.-$$Lambda$AudioPlayerAlert$TTY43GWhuW3FlUWxplw8ac-chYU r5 = org.telegram.ui.Components.$$Lambda$AudioPlayerAlert$TTY43GWhuW3FlUWxplw8acchYU.INSTANCE
            r3.setOnClickListener(r5)
            android.widget.ImageView r3 = r0.nextButton
            r5 = 2131625868(0x7f0e078c, float:1.8878956E38)
            java.lang.String r6 = "Next"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r6, r5)
            r3.setContentDescription(r5)
            android.view.View[] r3 = r0.buttons
            org.telegram.ui.ActionBar.ActionBarMenuItem r5 = new org.telegram.ui.ActionBar.ActionBarMenuItem
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r7)
            r7 = 0
            r10 = 0
            r5.<init>(r1, r7, r10, r6)
            r0.optionsButton = r5
            r6 = 4
            r3[r6] = r5
            r5.setLongClickEnabled(r10)
            org.telegram.ui.ActionBar.ActionBarMenuItem r3 = r0.optionsButton
            r3.setShowSubmenuByMove(r10)
            org.telegram.ui.ActionBar.ActionBarMenuItem r3 = r0.optionsButton
            r5 = 2131165439(0x7var_ff, float:1.7945095E38)
            r3.setIcon((int) r5)
            org.telegram.ui.ActionBar.ActionBarMenuItem r3 = r0.optionsButton
            r3.setSubMenuOpenSide(r11)
            org.telegram.ui.ActionBar.ActionBarMenuItem r3 = r0.optionsButton
            r3.setPopupAnimationEnabled(r10)
            org.telegram.ui.ActionBar.ActionBarMenuItem r3 = r0.optionsButton
            r5 = 1121583104(0x42da0000, float:109.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r5 = -r5
            r3.setAdditionalYOffset(r5)
            int r3 = android.os.Build.VERSION.SDK_INT
            if (r3 < r8) goto L_0x05c8
            org.telegram.ui.ActionBar.ActionBarMenuItem r3 = r0.optionsButton
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r14)
            r6 = 1099956224(0x41900000, float:18.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            android.graphics.drawable.Drawable r5 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r5, r2, r6)
            r3.setBackgroundDrawable(r5)
        L_0x05c8:
            org.telegram.ui.ActionBar.ActionBarMenuItem r3 = r0.optionsButton
            android.widget.FrameLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createFrame(r12, r12, r13)
            r4.addView(r3, r5)
            org.telegram.ui.ActionBar.ActionBarMenuItem r3 = r0.optionsButton
            r4 = 2131165680(0x7var_f0, float:1.7945584E38)
            r5 = 2131625341(0x7f0e057d, float:1.8877887E38)
            java.lang.String r6 = "Forward"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r6, r5)
            r3.addSubItem(r2, r4, r5)
            org.telegram.ui.ActionBar.ActionBarMenuItem r3 = r0.optionsButton
            r4 = 2131165727(0x7var_f, float:1.794568E38)
            r5 = 2131626870(0x7f0e0b76, float:1.8880988E38)
            java.lang.String r6 = "ShareFile"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r6, r5)
            r3.addSubItem(r11, r4, r5)
            org.telegram.ui.ActionBar.ActionBarMenuItem r3 = r0.optionsButton
            r4 = 2131165700(0x7var_, float:1.7945625E38)
            r5 = 2131626918(0x7f0e0ba6, float:1.8881086E38)
            java.lang.String r6 = "ShowInChat"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r6, r5)
            r6 = 4
            r3.addSubItem(r6, r4, r5)
            org.telegram.ui.ActionBar.ActionBarMenuItem r3 = r0.optionsButton
            org.telegram.ui.Components.-$$Lambda$AudioPlayerAlert$RQkUm9w15pKsPCtwRprruwdsZN8 r4 = new org.telegram.ui.Components.-$$Lambda$AudioPlayerAlert$RQkUm9w15pKsPCtwRprruwdsZN8
            r4.<init>()
            r3.setOnClickListener(r4)
            org.telegram.ui.ActionBar.ActionBarMenuItem r3 = r0.optionsButton
            org.telegram.ui.Components.-$$Lambda$AudioPlayerAlert$2le4yIg27yXBU0W-K7geAc_qlH0 r4 = new org.telegram.ui.Components.-$$Lambda$AudioPlayerAlert$2le4yIg27yXBU0W-K7geAc_qlH0
            r4.<init>()
            r3.setDelegate(r4)
            org.telegram.ui.ActionBar.ActionBarMenuItem r3 = r0.optionsButton
            r4 = 2131623982(0x7f0e002e, float:1.887513E38)
            java.lang.String r5 = "AccDescrMoreOptions"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r3.setContentDescription(r4)
            android.widget.LinearLayout r3 = new android.widget.LinearLayout
            r3.<init>(r1)
            r0.emptyView = r3
            r3.setOrientation(r2)
            android.widget.LinearLayout r3 = r0.emptyView
            r4 = 17
            r3.setGravity(r4)
            android.widget.LinearLayout r3 = r0.emptyView
            r4 = 8
            r3.setVisibility(r4)
            android.view.ViewGroup r3 = r0.containerView
            android.widget.LinearLayout r4 = r0.emptyView
            r5 = -1082130432(0xffffffffbvar_, float:-1.0)
            r6 = -1
            android.widget.FrameLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createFrame(r6, r5)
            r3.addView(r4, r5)
            android.widget.LinearLayout r3 = r0.emptyView
            org.telegram.ui.Components.-$$Lambda$AudioPlayerAlert$6h_bOIEKm9jaLMN61FOO2fjdx4I r4 = org.telegram.ui.Components.$$Lambda$AudioPlayerAlert$6h_bOIEKm9jaLMN61FOO2fjdx4I.INSTANCE
            r3.setOnTouchListener(r4)
            android.widget.ImageView r3 = new android.widget.ImageView
            r3.<init>(r1)
            r0.emptyImageView = r3
            r4 = 2131165746(0x7var_, float:1.7945718E38)
            r3.setImageResource(r4)
            android.widget.ImageView r3 = r0.emptyImageView
            android.graphics.PorterDuffColorFilter r4 = new android.graphics.PorterDuffColorFilter
            java.lang.String r5 = "dialogEmptyImage"
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            android.graphics.PorterDuff$Mode r6 = android.graphics.PorterDuff.Mode.MULTIPLY
            r4.<init>(r5, r6)
            r3.setColorFilter(r4)
            android.widget.LinearLayout r3 = r0.emptyView
            android.widget.ImageView r4 = r0.emptyImageView
            r5 = -2
            r6 = -2
            android.widget.LinearLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createLinear(r5, r6)
            r3.addView(r4, r5)
            android.widget.TextView r3 = new android.widget.TextView
            r3.<init>(r1)
            r0.emptyTitleTextView = r3
            java.lang.String r4 = "dialogEmptyText"
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r4)
            r3.setTextColor(r4)
            android.widget.TextView r3 = r0.emptyTitleTextView
            r4 = 17
            r3.setGravity(r4)
            android.widget.TextView r3 = r0.emptyTitleTextView
            r4 = 2131625871(0x7f0e078f, float:1.8878962E38)
            java.lang.String r5 = "NoAudioFound"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r3.setText(r4)
            android.widget.TextView r3 = r0.emptyTitleTextView
            android.graphics.Typeface r4 = org.telegram.messenger.AndroidUtilities.getTypeface(r9)
            r3.setTypeface(r4)
            android.widget.TextView r3 = r0.emptyTitleTextView
            r4 = 1099431936(0x41880000, float:17.0)
            r3.setTextSize(r2, r4)
            android.widget.TextView r3 = r0.emptyTitleTextView
            r4 = 1109393408(0x42200000, float:40.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r7 = 0
            r3.setPadding(r5, r7, r6, r7)
            android.widget.LinearLayout r3 = r0.emptyView
            android.widget.TextView r5 = r0.emptyTitleTextView
            r6 = -2
            r7 = -2
            r8 = 17
            r9 = 0
            r10 = 11
            r11 = 0
            r12 = 0
            android.widget.LinearLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r6, (int) r7, (int) r8, (int) r9, (int) r10, (int) r11, (int) r12)
            r3.addView(r5, r6)
            android.widget.TextView r3 = new android.widget.TextView
            r3.<init>(r1)
            r0.emptySubtitleTextView = r3
            java.lang.String r5 = "dialogEmptyText"
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            r3.setTextColor(r5)
            android.widget.TextView r3 = r0.emptySubtitleTextView
            r5 = 17
            r3.setGravity(r5)
            android.widget.TextView r3 = r0.emptySubtitleTextView
            r5 = 1097859072(0x41700000, float:15.0)
            r3.setTextSize(r2, r5)
            android.widget.TextView r3 = r0.emptySubtitleTextView
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r6 = 0
            r3.setPadding(r5, r6, r4, r6)
            android.widget.LinearLayout r3 = r0.emptyView
            android.widget.TextView r4 = r0.emptySubtitleTextView
            r5 = -2
            r6 = -2
            r7 = 17
            r8 = 0
            r9 = 6
            r10 = 0
            android.widget.LinearLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r5, (int) r6, (int) r7, (int) r8, (int) r9, (int) r10, (int) r11)
            r3.addView(r4, r5)
            org.telegram.ui.Components.AudioPlayerAlert$9 r3 = new org.telegram.ui.Components.AudioPlayerAlert$9
            r3.<init>(r1)
            r0.listView = r3
            r4 = 0
            r3.setClipToPadding(r4)
            org.telegram.ui.Components.RecyclerListView r3 = r0.listView
            androidx.recyclerview.widget.LinearLayoutManager r5 = new androidx.recyclerview.widget.LinearLayoutManager
            android.content.Context r6 = r21.getContext()
            r5.<init>(r6, r2, r4)
            r0.layoutManager = r5
            r3.setLayoutManager(r5)
            org.telegram.ui.Components.RecyclerListView r3 = r0.listView
            r3.setHorizontalScrollBarEnabled(r4)
            org.telegram.ui.Components.RecyclerListView r3 = r0.listView
            r3.setVerticalScrollBarEnabled(r4)
            android.view.ViewGroup r3 = r0.containerView
            org.telegram.ui.Components.RecyclerListView r4 = r0.listView
            r5 = -1
            android.widget.FrameLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createFrame(r5, r5, r13)
            r3.addView(r4, r6)
            org.telegram.ui.Components.RecyclerListView r3 = r0.listView
            org.telegram.ui.Components.AudioPlayerAlert$ListAdapter r4 = new org.telegram.ui.Components.AudioPlayerAlert$ListAdapter
            r4.<init>(r1)
            r0.listAdapter = r4
            r3.setAdapter(r4)
            org.telegram.ui.Components.RecyclerListView r3 = r0.listView
            java.lang.String r4 = "dialogScrollGlow"
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r4)
            r3.setGlowColor(r4)
            org.telegram.ui.Components.RecyclerListView r3 = r0.listView
            org.telegram.ui.Components.-$$Lambda$AudioPlayerAlert$48MEuBkln-g5kGMLmNUMeP-Rsutc r4 = org.telegram.ui.Components.$$Lambda$AudioPlayerAlert$48MEuBklng5kGMLmNUMePRsutc.INSTANCE
            r3.setOnItemClickListener((org.telegram.ui.Components.RecyclerListView.OnItemClickListener) r4)
            org.telegram.ui.Components.RecyclerListView r3 = r0.listView
            org.telegram.ui.Components.AudioPlayerAlert$10 r4 = new org.telegram.ui.Components.AudioPlayerAlert$10
            r4.<init>()
            r3.setOnScrollListener(r4)
            org.telegram.messenger.MediaController r3 = org.telegram.messenger.MediaController.getInstance()
            java.util.ArrayList r3 = r3.getPlaylist()
            r0.playlist = r3
            org.telegram.ui.Components.AudioPlayerAlert$ListAdapter r3 = r0.listAdapter
            r3.notifyDataSetChanged()
            android.view.ViewGroup r3 = r0.containerView
            android.widget.FrameLayout r4 = r0.playerLayout
            r5 = 179(0xb3, float:2.51E-43)
            r6 = 83
            r7 = -1
            android.widget.FrameLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createFrame(r7, r5, r6)
            r3.addView(r4, r5)
            android.view.ViewGroup r3 = r0.containerView
            android.view.View r4 = r0.playerShadow
            android.widget.FrameLayout$LayoutParams r5 = new android.widget.FrameLayout$LayoutParams
            int r6 = org.telegram.messenger.AndroidUtilities.getShadowHeight()
            r8 = 83
            r5.<init>(r7, r6, r8)
            r3.addView(r4, r5)
            android.view.View r3 = r0.playerShadow
            android.view.ViewGroup$LayoutParams r3 = r3.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r3 = (android.widget.FrameLayout.LayoutParams) r3
            r4 = 1127415808(0x43330000, float:179.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r3.bottomMargin = r4
            android.view.ViewGroup r3 = r0.containerView
            android.view.View r4 = r0.actionBarShadow
            r5 = 1077936128(0x40400000, float:3.0)
            android.widget.FrameLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createFrame(r7, r5)
            r3.addView(r4, r5)
            android.view.ViewGroup r3 = r0.containerView
            org.telegram.ui.ActionBar.ActionBar r4 = r0.actionBar
            r3.addView(r4)
            org.telegram.ui.Components.AudioPlayerAlert$11 r3 = new org.telegram.ui.Components.AudioPlayerAlert$11
            r3.<init>(r1)
            r0.blurredView = r3
            r4 = 0
            r3.setAlpha(r4)
            android.widget.FrameLayout r3 = r0.blurredView
            r4 = 4
            r3.setVisibility(r4)
            android.widget.FrameLayout r3 = r21.getContainer()
            android.widget.FrameLayout r4 = r0.blurredView
            r3.addView(r4)
            org.telegram.ui.Components.BackupImageView r3 = new org.telegram.ui.Components.BackupImageView
            r3.<init>(r1)
            r0.bigAlbumConver = r3
            r3.setAspectFit(r2)
            org.telegram.ui.Components.BackupImageView r1 = r0.bigAlbumConver
            r2 = 1090519040(0x41000000, float:8.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r1.setRoundRadius(r2)
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
            r21.updateRepeatButton()
            r21.updateEmptyView()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.AudioPlayerAlert.<init>(android.content.Context):void");
    }

    public /* synthetic */ void lambda$new$0$AudioPlayerAlert(ImageReceiver imageReceiver, boolean z, boolean z2, boolean z3) {
        if (this.blurredView.getTag() != null) {
            this.bigAlbumConver.setImageBitmap(this.placeholderImageView.imageReceiver.getBitmap());
        }
    }

    public /* synthetic */ void lambda$new$1$AudioPlayerAlert(View view) {
        if (MediaController.getInstance().getPlaybackSpeed(true) > 1.0f) {
            MediaController.getInstance().setPlaybackSpeed(true, 1.0f);
        } else {
            MediaController.getInstance().setPlaybackSpeed(true, 1.8f);
        }
        updatePlaybackButton();
    }

    public /* synthetic */ void lambda$new$2$AudioPlayerAlert(View view) {
        updateSubMenu();
        this.repeatButton.toggleSubMenu();
    }

    public /* synthetic */ void lambda$new$3$AudioPlayerAlert(int i) {
        if (i == 1 || i == 2) {
            if ((!SharedConfig.playOrderReversed || i != 1) && (!SharedConfig.shuffleMusic || i != 2)) {
                MediaController.getInstance().setPlaybackOrderType(i);
            } else {
                MediaController.getInstance().setPlaybackOrderType(0);
            }
            this.listAdapter.notifyDataSetChanged();
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

    static /* synthetic */ void lambda$new$5(View view) {
        if (!MediaController.getInstance().isDownloadingCurrentMessage()) {
            if (MediaController.getInstance().isMessagePaused()) {
                MediaController.getInstance().playMessage(MediaController.getInstance().getPlayingMessageObject());
            } else {
                MediaController.getInstance().lambda$startAudioAgain$7$MediaController(MediaController.getInstance().getPlayingMessageObject());
            }
        }
    }

    public /* synthetic */ void lambda$new$7$AudioPlayerAlert(View view) {
        this.optionsButton.toggleSubMenu();
    }

    static /* synthetic */ void lambda$new$9(View view, int i) {
        if (view instanceof AudioPlayerCell) {
            ((AudioPlayerCell) view).didPressedButton();
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
            actionBarMenuSubItem.setTextColor(Theme.getColor("player_buttonActive"));
            actionBarMenuSubItem.setIconColor(Theme.getColor("player_buttonActive"));
            return;
        }
        actionBarMenuSubItem.setTextColor(Theme.getColor("actionBarDefaultSubmenuItem"));
        actionBarMenuSubItem.setIconColor(Theme.getColor("actionBarDefaultSubmenuItem"));
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
        if (MediaController.getInstance().getPlaybackSpeed(true) > 1.0f) {
            this.playbackSpeedButton.setTag("inappPlayerPlayPause");
            this.playbackSpeedButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("inappPlayerPlayPause"), PorterDuff.Mode.MULTIPLY));
            return;
        }
        this.playbackSpeedButton.setTag("inappPlayerClose");
        this.playbackSpeedButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("inappPlayerClose"), PorterDuff.Mode.MULTIPLY));
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Can't wrap try/catch for region: R(4:29|30|31|32) */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0066, code lost:
        if (r6.exists() == false) goto L_0x0068;
     */
    /* JADX WARNING: Missing exception handler attribute for start block: B:31:0x00a4 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onSubItemClick(int r6) {
        /*
            r5 = this;
            org.telegram.messenger.MediaController r0 = org.telegram.messenger.MediaController.getInstance()
            org.telegram.messenger.MessageObject r0 = r0.getPlayingMessageObject()
            if (r0 == 0) goto L_0x0174
            org.telegram.ui.LaunchActivity r1 = r5.parentActivity
            if (r1 != 0) goto L_0x0010
            goto L_0x0174
        L_0x0010:
            r2 = 1
            if (r6 != r2) goto L_0x004b
            int r6 = org.telegram.messenger.UserConfig.selectedAccount
            int r3 = r5.currentAccount
            if (r6 == r3) goto L_0x001c
            r1.switchToAccount(r3, r2)
        L_0x001c:
            android.os.Bundle r6 = new android.os.Bundle
            r6.<init>()
            java.lang.String r1 = "onlySelect"
            r6.putBoolean(r1, r2)
            r1 = 3
            java.lang.String r2 = "dialogsType"
            r6.putInt(r2, r1)
            org.telegram.ui.DialogsActivity r1 = new org.telegram.ui.DialogsActivity
            r1.<init>(r6)
            java.util.ArrayList r6 = new java.util.ArrayList
            r6.<init>()
            r6.add(r0)
            org.telegram.ui.Components.-$$Lambda$AudioPlayerAlert$bZwv44or3-083Y0MYIMNRo1ORcQ r0 = new org.telegram.ui.Components.-$$Lambda$AudioPlayerAlert$bZwv44or3-083Y0MYIMNRo1ORcQ
            r0.<init>(r6)
            r1.setDelegate(r0)
            org.telegram.ui.LaunchActivity r6 = r5.parentActivity
            r6.lambda$runLinkRequest$32$LaunchActivity(r1)
            r5.dismiss()
            goto L_0x0174
        L_0x004b:
            r3 = 2
            if (r6 != r3) goto L_0x00ff
            org.telegram.tgnet.TLRPC$Message r6 = r0.messageOwner     // Catch:{ Exception -> 0x00f9 }
            java.lang.String r6 = r6.attachPath     // Catch:{ Exception -> 0x00f9 }
            boolean r6 = android.text.TextUtils.isEmpty(r6)     // Catch:{ Exception -> 0x00f9 }
            r1 = 0
            if (r6 != 0) goto L_0x0068
            java.io.File r6 = new java.io.File     // Catch:{ Exception -> 0x00f9 }
            org.telegram.tgnet.TLRPC$Message r3 = r0.messageOwner     // Catch:{ Exception -> 0x00f9 }
            java.lang.String r3 = r3.attachPath     // Catch:{ Exception -> 0x00f9 }
            r6.<init>(r3)     // Catch:{ Exception -> 0x00f9 }
            boolean r3 = r6.exists()     // Catch:{ Exception -> 0x00f9 }
            if (r3 != 0) goto L_0x0069
        L_0x0068:
            r6 = r1
        L_0x0069:
            if (r6 != 0) goto L_0x0071
            org.telegram.tgnet.TLRPC$Message r6 = r0.messageOwner     // Catch:{ Exception -> 0x00f9 }
            java.io.File r6 = org.telegram.messenger.FileLoader.getPathToMessage(r6)     // Catch:{ Exception -> 0x00f9 }
        L_0x0071:
            boolean r3 = r6.exists()     // Catch:{ Exception -> 0x00f9 }
            if (r3 == 0) goto L_0x00c9
            android.content.Intent r1 = new android.content.Intent     // Catch:{ Exception -> 0x00f9 }
            java.lang.String r3 = "android.intent.action.SEND"
            r1.<init>(r3)     // Catch:{ Exception -> 0x00f9 }
            if (r0 == 0) goto L_0x0088
            java.lang.String r0 = r0.getMimeType()     // Catch:{ Exception -> 0x00f9 }
            r1.setType(r0)     // Catch:{ Exception -> 0x00f9 }
            goto L_0x008d
        L_0x0088:
            java.lang.String r0 = "audio/mp3"
            r1.setType(r0)     // Catch:{ Exception -> 0x00f9 }
        L_0x008d:
            int r0 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x00f9 }
            r3 = 24
            java.lang.String r4 = "android.intent.extra.STREAM"
            if (r0 < r3) goto L_0x00ac
            android.content.Context r0 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x00a4 }
            java.lang.String r3 = "org.telegram.messenger.beta.provider"
            android.net.Uri r0 = androidx.core.content.FileProvider.getUriForFile(r0, r3, r6)     // Catch:{ Exception -> 0x00a4 }
            r1.putExtra(r4, r0)     // Catch:{ Exception -> 0x00a4 }
            r1.setFlags(r2)     // Catch:{ Exception -> 0x00a4 }
            goto L_0x00b3
        L_0x00a4:
            android.net.Uri r6 = android.net.Uri.fromFile(r6)     // Catch:{ Exception -> 0x00f9 }
            r1.putExtra(r4, r6)     // Catch:{ Exception -> 0x00f9 }
            goto L_0x00b3
        L_0x00ac:
            android.net.Uri r6 = android.net.Uri.fromFile(r6)     // Catch:{ Exception -> 0x00f9 }
            r1.putExtra(r4, r6)     // Catch:{ Exception -> 0x00f9 }
        L_0x00b3:
            org.telegram.ui.LaunchActivity r6 = r5.parentActivity     // Catch:{ Exception -> 0x00f9 }
            java.lang.String r0 = "ShareFile"
            r2 = 2131626870(0x7f0e0b76, float:1.8880988E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r0, r2)     // Catch:{ Exception -> 0x00f9 }
            android.content.Intent r0 = android.content.Intent.createChooser(r1, r0)     // Catch:{ Exception -> 0x00f9 }
            r1 = 500(0x1f4, float:7.0E-43)
            r6.startActivityForResult(r0, r1)     // Catch:{ Exception -> 0x00f9 }
            goto L_0x0174
        L_0x00c9:
            org.telegram.ui.ActionBar.AlertDialog$Builder r6 = new org.telegram.ui.ActionBar.AlertDialog$Builder     // Catch:{ Exception -> 0x00f9 }
            org.telegram.ui.LaunchActivity r0 = r5.parentActivity     // Catch:{ Exception -> 0x00f9 }
            r6.<init>((android.content.Context) r0)     // Catch:{ Exception -> 0x00f9 }
            java.lang.String r0 = "AppName"
            r2 = 2131624212(0x7f0e0114, float:1.8875597E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r0, r2)     // Catch:{ Exception -> 0x00f9 }
            r6.setTitle(r0)     // Catch:{ Exception -> 0x00f9 }
            java.lang.String r0 = "OK"
            r2 = 2131626082(0x7f0e0862, float:1.887939E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r0, r2)     // Catch:{ Exception -> 0x00f9 }
            r6.setPositiveButton(r0, r1)     // Catch:{ Exception -> 0x00f9 }
            java.lang.String r0 = "PleaseDownload"
            r1 = 2131626472(0x7f0e09e8, float:1.8880181E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r0, r1)     // Catch:{ Exception -> 0x00f9 }
            r6.setMessage(r0)     // Catch:{ Exception -> 0x00f9 }
            r6.show()     // Catch:{ Exception -> 0x00f9 }
            goto L_0x0174
        L_0x00f9:
            r6 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r6)
            goto L_0x0174
        L_0x00ff:
            r3 = 4
            if (r6 != r3) goto L_0x0174
            int r6 = org.telegram.messenger.UserConfig.selectedAccount
            int r3 = r5.currentAccount
            if (r6 == r3) goto L_0x010b
            r1.switchToAccount(r3, r2)
        L_0x010b:
            android.os.Bundle r6 = new android.os.Bundle
            r6.<init>()
            long r1 = r0.getDialogId()
            int r3 = (int) r1
            r4 = 32
            long r1 = r1 >> r4
            int r2 = (int) r1
            if (r3 == 0) goto L_0x014b
            if (r3 <= 0) goto L_0x0123
            java.lang.String r1 = "user_id"
            r6.putInt(r1, r3)
            goto L_0x0150
        L_0x0123:
            if (r3 >= 0) goto L_0x0150
            int r1 = r5.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
            int r2 = -r3
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            org.telegram.tgnet.TLRPC$Chat r1 = r1.getChat(r2)
            if (r1 == 0) goto L_0x0144
            org.telegram.tgnet.TLRPC$InputChannel r2 = r1.migrated_to
            if (r2 == 0) goto L_0x0144
            java.lang.String r2 = "migrated_to"
            r6.putInt(r2, r3)
            org.telegram.tgnet.TLRPC$InputChannel r1 = r1.migrated_to
            int r1 = r1.channel_id
            int r3 = -r1
        L_0x0144:
            int r1 = -r3
            java.lang.String r2 = "chat_id"
            r6.putInt(r2, r1)
            goto L_0x0150
        L_0x014b:
            java.lang.String r1 = "enc_id"
            r6.putInt(r1, r2)
        L_0x0150:
            int r0 = r0.getId()
            java.lang.String r1 = "message_id"
            r6.putInt(r1, r0)
            int r0 = r5.currentAccount
            org.telegram.messenger.NotificationCenter r0 = org.telegram.messenger.NotificationCenter.getInstance(r0)
            int r1 = org.telegram.messenger.NotificationCenter.closeChats
            r2 = 0
            java.lang.Object[] r3 = new java.lang.Object[r2]
            r0.postNotificationName(r1, r3)
            org.telegram.ui.LaunchActivity r0 = r5.parentActivity
            org.telegram.ui.ChatActivity r1 = new org.telegram.ui.ChatActivity
            r1.<init>(r6)
            r0.presentFragment(r1, r2, r2)
            r5.dismiss()
        L_0x0174:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.AudioPlayerAlert.onSubItemClick(int):void");
    }

    public /* synthetic */ void lambda$onSubItemClick$10$AudioPlayerAlert(ArrayList arrayList, DialogsActivity dialogsActivity, ArrayList arrayList2, CharSequence charSequence, boolean z) {
        ArrayList arrayList3 = arrayList2;
        if (arrayList2.size() > 1 || ((Long) arrayList3.get(0)).longValue() == ((long) UserConfig.getInstance(this.currentAccount).getClientUserId()) || charSequence != null) {
            ArrayList arrayList4 = arrayList;
            for (int i = 0; i < arrayList2.size(); i++) {
                long longValue = ((Long) arrayList3.get(i)).longValue();
                if (charSequence != null) {
                    SendMessagesHelper.getInstance(this.currentAccount).sendMessage(charSequence.toString(), longValue, (MessageObject) null, (TLRPC$WebPage) null, true, (ArrayList<TLRPC$MessageEntity>) null, (TLRPC$ReplyMarkup) null, (HashMap<String, String>) null, true, 0);
                }
                SendMessagesHelper.getInstance(this.currentAccount).sendMessage(arrayList, longValue, true, 0);
            }
            dialogsActivity.finishFragment();
            return;
        }
        long longValue2 = ((Long) arrayList3.get(0)).longValue();
        int i2 = (int) longValue2;
        int i3 = (int) (longValue2 >> 32);
        Bundle bundle = new Bundle();
        bundle.putBoolean("scrollToTopOnResume", true);
        if (i2 == 0) {
            bundle.putInt("enc_id", i3);
        } else if (i2 > 0) {
            bundle.putInt("user_id", i2);
        } else if (i2 < 0) {
            bundle.putInt("chat_id", -i2);
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
    public void showAlbumCover(boolean z, boolean z2) {
        if (z) {
            if (this.blurredView.getVisibility() != 0 && !this.blurredAnimationInProgress) {
                this.blurredView.setTag(1);
                this.bigAlbumConver.setImageBitmap(this.placeholderImageView.imageReceiver.getBitmap());
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
                return;
            }
            this.blurredView.setAlpha(0.0f);
            this.blurredView.setVisibility(4);
            this.bigAlbumConver.setImageBitmap((Bitmap) null);
        }
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        AudioPlayerCell audioPlayerCell;
        MessageObject messageObject;
        AudioPlayerCell audioPlayerCell2;
        MessageObject messageObject2;
        if (i == NotificationCenter.messagePlayingDidStart || i == NotificationCenter.messagePlayingPlayStateChanged || i == NotificationCenter.messagePlayingDidReset) {
            updateTitle(i == NotificationCenter.messagePlayingDidReset && objArr[1].booleanValue());
            if (i == NotificationCenter.messagePlayingDidReset || i == NotificationCenter.messagePlayingPlayStateChanged) {
                int childCount = this.listView.getChildCount();
                for (int i3 = 0; i3 < childCount; i3++) {
                    View childAt = this.listView.getChildAt(i3);
                    if ((childAt instanceof AudioPlayerCell) && (messageObject = audioPlayerCell.getMessageObject()) != null && (messageObject.isVoice() || messageObject.isMusic())) {
                        (audioPlayerCell = (AudioPlayerCell) childAt).updateButtonState(false, true);
                    }
                }
            } else if (i == NotificationCenter.messagePlayingDidStart && objArr[0].eventId == 0) {
                int childCount2 = this.listView.getChildCount();
                for (int i4 = 0; i4 < childCount2; i4++) {
                    View childAt2 = this.listView.getChildAt(i4);
                    if ((childAt2 instanceof AudioPlayerCell) && (messageObject2 = audioPlayerCell2.getMessageObject()) != null && (messageObject2.isVoice() || messageObject2.isMusic())) {
                        (audioPlayerCell2 = (AudioPlayerCell) childAt2).updateButtonState(false, true);
                    }
                }
            }
        } else if (i == NotificationCenter.messagePlayingProgressDidChanged) {
            MessageObject playingMessageObject = MediaController.getInstance().getPlayingMessageObject();
            if (playingMessageObject != null && playingMessageObject.isMusic()) {
                updateProgress(playingMessageObject);
            }
        } else if (i == NotificationCenter.musicDidLoad) {
            this.playlist = MediaController.getInstance().getPlaylist();
            this.listAdapter.notifyDataSetChanged();
        } else if (i == NotificationCenter.fileDidLoad && objArr[0].equals(this.currentFile)) {
            updateTitle(false);
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
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.musicDidLoad);
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
                this.repeatButton.setIconColor(Theme.getColor("player_buttonActive"));
                Theme.setSelectorDrawableColor(this.repeatButton.getBackground(), NUM & Theme.getColor("player_buttonActive"), true);
                if (i != 0) {
                    this.repeatButton.setContentDescription(LocaleController.getString("AccDescrRepeatList", NUM));
                } else if (SharedConfig.shuffleMusic) {
                    this.repeatButton.setContentDescription(LocaleController.getString("ShuffleList", NUM));
                } else {
                    this.repeatButton.setContentDescription(LocaleController.getString("ReverseOrder", NUM));
                }
            } else {
                this.repeatButton.setTag("player_button");
                this.repeatButton.setIconColor(Theme.getColor("player_button"));
                Theme.setSelectorDrawableColor(this.repeatButton.getBackground(), Theme.getColor("listSelectorSDK21"), true);
                this.repeatButton.setContentDescription(LocaleController.getString("AccDescrRepeatOff", NUM));
            }
        } else if (i == 2) {
            this.repeatButton.setIcon(NUM);
            this.repeatButton.setTag("player_buttonActive");
            this.repeatButton.setIconColor(Theme.getColor("player_buttonActive"));
            Theme.setSelectorDrawableColor(this.repeatButton.getBackground(), NUM & Theme.getColor("player_buttonActive"), true);
            this.repeatButton.setContentDescription(LocaleController.getString("AccDescrRepeatOne", NUM));
        }
    }

    /* access modifiers changed from: private */
    public void updateProgress(MessageObject messageObject) {
        int i;
        SeekBarView seekBarView2 = this.seekBarView;
        if (seekBarView2 != null) {
            if (seekBarView2.isDragging()) {
                i = (int) (((float) messageObject.getDuration()) * this.seekBarView.getProgress());
            } else {
                this.seekBarView.setProgress(messageObject.audioProgress);
                this.seekBarView.setBufferedProgress(messageObject.bufferedProgress);
                i = messageObject.audioProgressSec;
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
        } else if (playingMessageObject != null) {
            if (playingMessageObject.eventId != 0 || playingMessageObject.getId() <= -NUM) {
                this.optionsButton.setVisibility(4);
            } else {
                this.optionsButton.setVisibility(0);
            }
            checkIfMusicDownloaded(playingMessageObject);
            updateProgress(playingMessageObject);
            if (MediaController.getInstance().isMessagePaused()) {
                this.playButton.setImageResource(NUM);
                this.playButton.setContentDescription(LocaleController.getString("AccActionPlay", NUM));
            } else {
                this.playButton.setImageResource(NUM);
                this.playButton.setContentDescription(LocaleController.getString("AccActionPause", NUM));
            }
            String musicTitle = playingMessageObject.getMusicTitle();
            String musicAuthor = playingMessageObject.getMusicAuthor();
            this.titleTextView.setText(musicTitle);
            this.authorTextView.setText(musicAuthor);
            musicAuthor + " " + musicTitle;
            AudioInfo audioInfo = MediaController.getInstance().getAudioInfo();
            if (audioInfo == null || audioInfo.getCover() == null) {
                TLRPC$Document document = playingMessageObject.getDocument();
                this.currentFile = FileLoader.getAttachFileName(document);
                TLRPC$PhotoSize closestPhotoSizeWithSize = document != null ? FileLoader.getClosestPhotoSizeWithSize(document.thumbs, 240) : null;
                if (!(closestPhotoSizeWithSize instanceof TLRPC$TL_photoSize)) {
                    closestPhotoSizeWithSize = null;
                }
                String artworkUrl = playingMessageObject.getArtworkUrl(false);
                if (!TextUtils.isEmpty(artworkUrl)) {
                    if (closestPhotoSizeWithSize != null) {
                        this.placeholderImageView.setImage(ImageLocation.getForPath(artworkUrl), (String) null, ImageLocation.getForDocument(closestPhotoSizeWithSize, document), (String) null, (String) null, 0, 1, playingMessageObject);
                    } else {
                        this.placeholderImageView.setImage(artworkUrl, (String) null, (Drawable) null);
                    }
                } else if (closestPhotoSizeWithSize != null) {
                    this.placeholderImageView.setImage((ImageLocation) null, (String) null, ImageLocation.getForDocument(closestPhotoSizeWithSize, document), (String) null, (String) null, 0, 1, playingMessageObject);
                } else {
                    this.placeholderImageView.setImageDrawable((Drawable) null);
                }
                this.placeholderImageView.invalidate();
            } else {
                this.placeholderImageView.setImageBitmap(audioInfo.getCover());
                this.currentFile = null;
            }
            int duration = playingMessageObject.getDuration();
            this.lastDuration = duration;
            TextView textView = this.durationTextView;
            if (textView != null) {
                textView.setText(duration != 0 ? AndroidUtilities.formatShortDuration(duration) : "-:--");
            }
            if (duration > 1200) {
                this.playbackSpeedButton.setVisibility(0);
            } else {
                this.playbackSpeedButton.setVisibility(8);
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
                AudioPlayerAlert.this.playerLayout.setBackgroundColor(Theme.getColor("player_background"));
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
            return new RecyclerListView.Holder(new AudioPlayerCell(this.context));
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
            $$Lambda$AudioPlayerAlert$ListAdapter$1SUa5kwvInSdv5uJO41aYrH2SUM r1 = new Runnable(str) {
                public final /* synthetic */ String f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    AudioPlayerAlert.ListAdapter.this.lambda$search$0$AudioPlayerAlert$ListAdapter(this.f$1);
                }
            };
            this.searchRunnable = r1;
            dispatchQueue.postRunnable(r1, 300);
        }

        public /* synthetic */ void lambda$search$0$AudioPlayerAlert$ListAdapter(String str) {
            this.searchRunnable = null;
            processSearch(str);
        }

        private void processSearch(String str) {
            AndroidUtilities.runOnUIThread(new Runnable(str) {
                public final /* synthetic */ String f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    AudioPlayerAlert.ListAdapter.this.lambda$processSearch$2$AudioPlayerAlert$ListAdapter(this.f$1);
                }
            });
        }

        public /* synthetic */ void lambda$processSearch$2$AudioPlayerAlert$ListAdapter(String str) {
            Utilities.searchQueue.postRunnable(new Runnable(str, new ArrayList(AudioPlayerAlert.this.playlist)) {
                public final /* synthetic */ String f$1;
                public final /* synthetic */ ArrayList f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    AudioPlayerAlert.ListAdapter.this.lambda$null$1$AudioPlayerAlert$ListAdapter(this.f$1, this.f$2);
                }
            });
        }

        public /* synthetic */ void lambda$null$1$AudioPlayerAlert$ListAdapter(String str, ArrayList arrayList) {
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
            AndroidUtilities.runOnUIThread(new Runnable(arrayList, str) {
                public final /* synthetic */ ArrayList f$1;
                public final /* synthetic */ String f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    AudioPlayerAlert.ListAdapter.this.lambda$updateSearchResults$3$AudioPlayerAlert$ListAdapter(this.f$1, this.f$2);
                }
            });
        }

        public /* synthetic */ void lambda$updateSearchResults$3$AudioPlayerAlert$ListAdapter(ArrayList arrayList, String str) {
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
        $$Lambda$AudioPlayerAlert$xB68ynKhd2Fqgxsd3klAZLmbP4Q r10 = new ThemeDescription.ThemeDescriptionDelegate() {
            public final void didSetColor() {
                AudioPlayerAlert.this.lambda$getThemeDescriptions$11$AudioPlayerAlert();
            }
        };
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "player_actionBar"));
        $$Lambda$AudioPlayerAlert$xB68ynKhd2Fqgxsd3klAZLmbP4Q r8 = r10;
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "player_actionBarTitle"));
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
        arrayList.add(new ThemeDescription(this.seekBarView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "player_progress"));
        arrayList.add(new ThemeDescription(this.seekBarView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "key_player_progressCachedBackground"));
        arrayList.add(new ThemeDescription(this.playbackSpeedButton, ThemeDescription.FLAG_CHECKTAG | ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "inappPlayerPlayPause"));
        arrayList.add(new ThemeDescription(this.playbackSpeedButton, ThemeDescription.FLAG_CHECKTAG | ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "inappPlayerClose"));
        arrayList.add(new ThemeDescription(this.repeatButton, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "player_button"));
        arrayList.add(new ThemeDescription(this.repeatButton, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "player_buttonActive"));
        arrayList.add(new ThemeDescription(this.repeatButton, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "listSelectorSDK21"));
        arrayList.add(new ThemeDescription(this.repeatButton, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "actionBarDefaultSubmenuItem"));
        arrayList.add(new ThemeDescription(this.repeatButton, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "actionBarDefaultSubmenuBackground"));
        arrayList.add(new ThemeDescription(this.optionsButton, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "player_button"));
        arrayList.add(new ThemeDescription(this.optionsButton, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "listSelectorSDK21"));
        arrayList.add(new ThemeDescription(this.optionsButton, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "actionBarDefaultSubmenuItem"));
        arrayList.add(new ThemeDescription(this.optionsButton, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "actionBarDefaultSubmenuBackground"));
        arrayList.add(new ThemeDescription(this.prevButton, ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "player_button"));
        arrayList.add(new ThemeDescription(this.prevButton, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE | ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        arrayList.add(new ThemeDescription(this.playButton, ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "player_button"));
        arrayList.add(new ThemeDescription(this.playButton, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE | ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        arrayList.add(new ThemeDescription(this.nextButton, ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "player_button"));
        arrayList.add(new ThemeDescription(this.nextButton, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE | ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
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
        arrayList.add(new ThemeDescription(this.titleTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "player_actionBarTitle"));
        arrayList.add(new ThemeDescription(this.authorTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "player_time"));
        arrayList.add(new ThemeDescription(this.containerView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "key_sheet_scrollUp"));
        return arrayList;
    }

    public /* synthetic */ void lambda$getThemeDescriptions$11$AudioPlayerAlert() {
        this.searchItem.getSearchField().setCursorColor(Theme.getColor("player_actionBarTitle"));
        ActionBarMenuItem actionBarMenuItem = this.repeatButton;
        actionBarMenuItem.setIconColor(Theme.getColor((String) actionBarMenuItem.getTag()));
        Theme.setSelectorDrawableColor(this.repeatButton.getBackground(), Theme.getColor("listSelectorSDK21"), true);
        this.optionsButton.setIconColor(Theme.getColor("player_button"));
        Theme.setSelectorDrawableColor(this.optionsButton.getBackground(), Theme.getColor("listSelectorSDK21"), true);
        this.progressView.setBackgroundColor(Theme.getColor("player_progressBackground"));
        this.progressView.setProgressColor(Theme.getColor("player_progress"));
        updateSubMenu();
        this.repeatButton.redrawPopup(Theme.getColor("actionBarDefaultSubmenuBackground"));
        this.optionsButton.setPopupItemsColor(Theme.getColor("actionBarDefaultSubmenuItem"), false);
        this.optionsButton.setPopupItemsColor(Theme.getColor("actionBarDefaultSubmenuItem"), true);
        this.optionsButton.redrawPopup(Theme.getColor("actionBarDefaultSubmenuBackground"));
    }
}
