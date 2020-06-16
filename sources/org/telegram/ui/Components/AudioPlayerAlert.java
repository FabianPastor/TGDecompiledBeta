package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Property;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
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
import org.telegram.tgnet.TLRPC$ReplyMarkup;
import org.telegram.tgnet.TLRPC$TL_documentAttributeAudio;
import org.telegram.tgnet.TLRPC$WebPage;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
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
    /* access modifiers changed from: private */
    public boolean draggingSeekBar;
    /* access modifiers changed from: private */
    public TextView durationTextView;
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
    private LineProgressView progressView;
    private ActionBarMenuItem repeatButton;
    private ActionBarMenuSubItem repeatListItem;
    private ActionBarMenuSubItem repeatSongItem;
    private ActionBarMenuSubItem reverseOrderItem;
    /* access modifiers changed from: private */
    public int scrollOffsetY = Integer.MAX_VALUE;
    /* access modifiers changed from: private */
    public boolean scrollToSong = true;
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
    public AudioPlayerAlert(android.content.Context r21) {
        /*
            r20 = this;
            r0 = r20
            r1 = r21
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
            r8 = 2131624310(0x7f0e0176, float:1.8875796E38)
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
            if (r4 == 0) goto L_0x0154
            long r4 = r4.getDialogId()
            int r9 = (int) r4
            r10 = 32
            long r4 = r4 >> r10
            int r5 = (int) r4
            if (r9 == 0) goto L_0x0125
            if (r9 <= 0) goto L_0x010c
            int r4 = r0.currentAccount
            org.telegram.messenger.MessagesController r4 = org.telegram.messenger.MessagesController.getInstance(r4)
            java.lang.Integer r5 = java.lang.Integer.valueOf(r9)
            org.telegram.tgnet.TLRPC$User r4 = r4.getUser(r5)
            if (r4 == 0) goto L_0x0154
            org.telegram.ui.ActionBar.ActionBar r5 = r0.actionBar
            java.lang.String r9 = r4.first_name
            java.lang.String r4 = r4.last_name
            java.lang.String r4 = org.telegram.messenger.ContactsController.formatName(r9, r4)
            r5.setTitle(r4)
            goto L_0x0154
        L_0x010c:
            int r4 = r0.currentAccount
            org.telegram.messenger.MessagesController r4 = org.telegram.messenger.MessagesController.getInstance(r4)
            int r5 = -r9
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            org.telegram.tgnet.TLRPC$Chat r4 = r4.getChat(r5)
            if (r4 == 0) goto L_0x0154
            org.telegram.ui.ActionBar.ActionBar r5 = r0.actionBar
            java.lang.String r4 = r4.title
            r5.setTitle(r4)
            goto L_0x0154
        L_0x0125:
            int r4 = r0.currentAccount
            org.telegram.messenger.MessagesController r4 = org.telegram.messenger.MessagesController.getInstance(r4)
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            org.telegram.tgnet.TLRPC$EncryptedChat r4 = r4.getEncryptedChat(r5)
            if (r4 == 0) goto L_0x0154
            int r5 = r0.currentAccount
            org.telegram.messenger.MessagesController r5 = org.telegram.messenger.MessagesController.getInstance(r5)
            int r4 = r4.user_id
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
            org.telegram.tgnet.TLRPC$User r4 = r5.getUser(r4)
            if (r4 == 0) goto L_0x0154
            org.telegram.ui.ActionBar.ActionBar r5 = r0.actionBar
            java.lang.String r9 = r4.first_name
            java.lang.String r4 = r4.last_name
            java.lang.String r4 = org.telegram.messenger.ContactsController.formatName(r9, r4)
            r5.setTitle(r4)
        L_0x0154:
            org.telegram.ui.ActionBar.ActionBar r4 = r0.actionBar
            org.telegram.ui.ActionBar.ActionBarMenu r4 = r4.createMenu()
            r5 = 2131165442(0x7var_, float:1.7945101E38)
            org.telegram.ui.ActionBar.ActionBarMenuItem r4 = r4.addItem((int) r6, (int) r5)
            r4.setIsSearchField(r2)
            org.telegram.ui.Components.AudioPlayerAlert$3 r5 = new org.telegram.ui.Components.AudioPlayerAlert$3
            r5.<init>()
            r4.setActionBarMenuItemSearchListener(r5)
            java.lang.String r5 = "Search"
            r9 = 2131626665(0x7f0e0aa9, float:1.8880573E38)
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r5, r9)
            r4.setContentDescription(r10)
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
            boolean r4 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r4 != 0) goto L_0x01b0
            org.telegram.ui.ActionBar.ActionBar r4 = r0.actionBar
            r4.showActionModeTop()
            org.telegram.ui.ActionBar.ActionBar r4 = r0.actionBar
            java.lang.String r9 = "player_actionBarTop"
            int r9 = org.telegram.ui.ActionBar.Theme.getColor(r9)
            r4.setActionModeTopColor(r9)
        L_0x01b0:
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
            int r9 = org.telegram.ui.ActionBar.Theme.getColor(r7)
            r4.setTextColor(r9)
            android.widget.TextView r4 = r0.titleTextView
            r9 = 1099431936(0x41880000, float:17.0)
            r4.setTextSize(r2, r9)
            android.widget.TextView r4 = r0.titleTextView
            java.lang.String r9 = "fonts/rmedium.ttf"
            android.graphics.Typeface r9 = org.telegram.messenger.AndroidUtilities.getTypeface(r9)
            r4.setTypeface(r9)
            android.widget.TextView r4 = r0.titleTextView
            android.text.TextUtils$TruncateAt r9 = android.text.TextUtils.TruncateAt.END
            r4.setEllipsize(r9)
            android.widget.TextView r4 = r0.titleTextView
            r4.setSingleLine(r2)
            android.widget.FrameLayout r4 = r0.playerLayout
            android.widget.TextView r9 = r0.titleTextView
            r10 = -1
            r11 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r12 = 51
            r13 = 1101004800(0x41a00000, float:20.0)
            r15 = 1114636288(0x42700000, float:60.0)
            android.widget.FrameLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createFrame(r10, r11, r12, r13, r14, r15, r16)
            r4.addView(r9, r10)
            android.widget.TextView r4 = new android.widget.TextView
            r4.<init>(r1)
            r0.authorTextView = r4
            int r9 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            r4.setTextColor(r9)
            android.widget.TextView r4 = r0.authorTextView
            r9 = 1095761920(0x41500000, float:13.0)
            r4.setTextSize(r2, r9)
            android.widget.TextView r4 = r0.authorTextView
            android.text.TextUtils$TruncateAt r9 = android.text.TextUtils.TruncateAt.END
            r4.setEllipsize(r9)
            android.widget.TextView r4 = r0.authorTextView
            r4.setSingleLine(r2)
            android.widget.FrameLayout r4 = r0.playerLayout
            android.widget.TextView r9 = r0.authorTextView
            r10 = -1
            r14 = 1111228416(0x423CLASSNAME, float:47.0)
            android.widget.FrameLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createFrame(r10, r11, r12, r13, r14, r15, r16)
            r4.addView(r9, r10)
            org.telegram.ui.Components.SeekBarView r4 = new org.telegram.ui.Components.SeekBarView
            r4.<init>(r1)
            r0.seekBarView = r4
            org.telegram.ui.Components.AudioPlayerAlert$7 r9 = new org.telegram.ui.Components.AudioPlayerAlert$7
            r9.<init>()
            r4.setDelegate(r9)
            org.telegram.ui.Components.SeekBarView r4 = r0.seekBarView
            r4.setReportChanges(r2)
            android.widget.FrameLayout r4 = r0.playerLayout
            org.telegram.ui.Components.SeekBarView r9 = r0.seekBarView
            r10 = -1
            r11 = 1108869120(0x42180000, float:38.0)
            r13 = 1084227584(0x40a00000, float:5.0)
            r14 = 1116471296(0x428CLASSNAME, float:70.0)
            r15 = 1084227584(0x40a00000, float:5.0)
            android.widget.FrameLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createFrame(r10, r11, r12, r13, r14, r15, r16)
            r4.addView(r9, r10)
            org.telegram.ui.Components.LineProgressView r4 = new org.telegram.ui.Components.LineProgressView
            r4.<init>(r1)
            r0.progressView = r4
            r9 = 4
            r4.setVisibility(r9)
            org.telegram.ui.Components.LineProgressView r4 = r0.progressView
            java.lang.String r10 = "player_progressBackground"
            int r10 = org.telegram.ui.ActionBar.Theme.getColor(r10)
            r4.setBackgroundColor(r10)
            org.telegram.ui.Components.LineProgressView r4 = r0.progressView
            java.lang.String r10 = "player_progress"
            int r10 = org.telegram.ui.ActionBar.Theme.getColor(r10)
            r4.setProgressColor(r10)
            android.widget.FrameLayout r4 = r0.playerLayout
            org.telegram.ui.Components.LineProgressView r10 = r0.progressView
            r11 = -1
            r12 = 1073741824(0x40000000, float:2.0)
            r13 = 51
            r14 = 1101529088(0x41a80000, float:21.0)
            r15 = 1119092736(0x42b40000, float:90.0)
            r16 = 1101529088(0x41a80000, float:21.0)
            r17 = 0
            android.widget.FrameLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createFrame(r11, r12, r13, r14, r15, r16, r17)
            r4.addView(r10, r11)
            org.telegram.ui.ActionBar.SimpleTextView r4 = new org.telegram.ui.ActionBar.SimpleTextView
            r4.<init>(r1)
            r0.timeTextView = r4
            r10 = 12
            r4.setTextSize(r10)
            org.telegram.ui.ActionBar.SimpleTextView r4 = r0.timeTextView
            java.lang.String r10 = "0:00"
            r4.setText(r10)
            org.telegram.ui.ActionBar.SimpleTextView r4 = r0.timeTextView
            int r10 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            r4.setTextColor(r10)
            org.telegram.ui.ActionBar.SimpleTextView r4 = r0.timeTextView
            r10 = 2
            r4.setImportantForAccessibility(r10)
            android.widget.FrameLayout r4 = r0.playerLayout
            org.telegram.ui.ActionBar.SimpleTextView r11 = r0.timeTextView
            r12 = 100
            r13 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r14 = 51
            r15 = 1101004800(0x41a00000, float:20.0)
            r16 = 1120141312(0x42CLASSNAME, float:98.0)
            r18 = 0
            android.widget.FrameLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createFrame(r12, r13, r14, r15, r16, r17, r18)
            r4.addView(r11, r12)
            android.widget.TextView r4 = new android.widget.TextView
            r4.<init>(r1)
            r0.durationTextView = r4
            r11 = 1094713344(0x41400000, float:12.0)
            r4.setTextSize(r2, r11)
            android.widget.TextView r4 = r0.durationTextView
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            r4.setTextColor(r5)
            android.widget.TextView r4 = r0.durationTextView
            r5 = 17
            r4.setGravity(r5)
            android.widget.TextView r4 = r0.durationTextView
            r4.setImportantForAccessibility(r10)
            android.widget.FrameLayout r4 = r0.playerLayout
            android.widget.TextView r5 = r0.durationTextView
            r11 = -2
            r12 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r13 = 53
            r14 = 0
            r15 = 1119879168(0x42CLASSNAME, float:96.0)
            r16 = 1101004800(0x41a00000, float:20.0)
            android.widget.FrameLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createFrame(r11, r12, r13, r14, r15, r16, r17)
            r4.addView(r5, r11)
            android.widget.ImageView r4 = new android.widget.ImageView
            r4.<init>(r1)
            r0.playbackSpeedButton = r4
            android.widget.ImageView$ScaleType r5 = android.widget.ImageView.ScaleType.CENTER
            r4.setScaleType(r5)
            android.widget.ImageView r4 = r0.playbackSpeedButton
            r5 = 2131165985(0x7var_, float:1.7946203E38)
            r4.setImageResource(r5)
            android.widget.ImageView r4 = r0.playbackSpeedButton
            r5 = 2131624005(0x7f0e0045, float:1.8875177E38)
            java.lang.String r11 = "AccDescrPlayerSpeed"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r11, r5)
            r4.setContentDescription(r5)
            float r4 = org.telegram.messenger.AndroidUtilities.density
            r5 = 1077936128(0x40400000, float:3.0)
            int r4 = (r4 > r5 ? 1 : (r4 == r5 ? 0 : -1))
            if (r4 < 0) goto L_0x0379
            android.widget.ImageView r4 = r0.playbackSpeedButton
            r4.setPadding(r6, r2, r6, r6)
        L_0x0379:
            android.widget.FrameLayout r4 = r0.playerLayout
            android.widget.ImageView r11 = r0.playbackSpeedButton
            r12 = 36
            r13 = 1108344832(0x42100000, float:36.0)
            r14 = 53
            r15 = 0
            r16 = 1118568448(0x42aCLASSNAME, float:86.0)
            r17 = 1101004800(0x41a00000, float:20.0)
            r18 = 0
            android.widget.FrameLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createFrame(r12, r13, r14, r15, r16, r17, r18)
            r4.addView(r11, r12)
            android.widget.ImageView r4 = r0.playbackSpeedButton
            org.telegram.ui.Components.-$$Lambda$AudioPlayerAlert$rAc8YAca3eZCLAfei_UTWtnjvbQ r11 = new org.telegram.ui.Components.-$$Lambda$AudioPlayerAlert$rAc8YAca3eZCLAfei_UTWtnjvbQ
            r11.<init>()
            r4.setOnClickListener(r11)
            r20.updatePlaybackButton()
            org.telegram.ui.Components.AudioPlayerAlert$8 r4 = new org.telegram.ui.Components.AudioPlayerAlert$8
            r4.<init>(r1)
            android.widget.FrameLayout r11 = r0.playerLayout
            r12 = -1
            r13 = 1115947008(0x42840000, float:66.0)
            r14 = 51
            r16 = 1121845248(0x42de0000, float:111.0)
            r17 = 0
            android.widget.FrameLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createFrame(r12, r13, r14, r15, r16, r17, r18)
            r11.addView(r4, r12)
            android.view.View[] r11 = r0.buttons
            org.telegram.ui.ActionBar.ActionBarMenuItem r12 = new org.telegram.ui.ActionBar.ActionBarMenuItem
            r13 = 0
            r12.<init>(r1, r13, r6, r6)
            r0.repeatButton = r12
            r11[r6] = r12
            r12.setLongClickEnabled(r6)
            org.telegram.ui.ActionBar.ActionBarMenuItem r11 = r0.repeatButton
            r11.setPopupAnimationEnabled(r6)
            org.telegram.ui.ActionBar.ActionBarMenuItem r11 = r0.repeatButton
            r12 = 1126563840(0x43260000, float:166.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            int r12 = -r12
            r11.setAdditionalYOffset(r12)
            int r11 = android.os.Build.VERSION.SDK_INT
            r12 = 1099956224(0x41900000, float:18.0)
            java.lang.String r14 = "listSelectorSDK21"
            r15 = 21
            if (r11 < r15) goto L_0x03f0
            org.telegram.ui.ActionBar.ActionBarMenuItem r11 = r0.repeatButton
            int r8 = org.telegram.ui.ActionBar.Theme.getColor(r14)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r12)
            android.graphics.drawable.Drawable r5 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r8, r2, r5)
            r11.setBackgroundDrawable(r5)
        L_0x03f0:
            org.telegram.ui.ActionBar.ActionBarMenuItem r5 = r0.repeatButton
            r8 = 48
            r11 = 51
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r8, r8, r11)
            r4.addView(r5, r3)
            org.telegram.ui.ActionBar.ActionBarMenuItem r3 = r0.repeatButton
            org.telegram.ui.Components.-$$Lambda$AudioPlayerAlert$L6a3eFAcdzUb5tmijbnBD0GiP9c r5 = new org.telegram.ui.Components.-$$Lambda$AudioPlayerAlert$L6a3eFAcdzUb5tmijbnBD0GiP9c
            r5.<init>()
            r3.setOnClickListener(r5)
            org.telegram.ui.ActionBar.ActionBarMenuSubItem[] r3 = new org.telegram.ui.ActionBar.ActionBarMenuSubItem[r9]
            org.telegram.ui.ActionBar.ActionBarMenuItem r5 = r0.repeatButton
            r12 = 2131165827(0x7var_, float:1.7945882E38)
            r13 = 2131626573(0x7f0e0a4d, float:1.8880386E38)
            java.lang.String r8 = "RepeatSong"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r8, r13)
            r13 = 3
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r5 = r5.addSubItem((int) r13, (int) r12, (java.lang.CharSequence) r8, (boolean) r2)
            r3[r6] = r5
            r0.repeatSongItem = r5
            org.telegram.ui.ActionBar.ActionBarMenuItem r5 = r0.repeatButton
            r8 = 2131165826(0x7var_, float:1.794588E38)
            r12 = 2131626570(0x7f0e0a4a, float:1.888038E38)
            java.lang.String r6 = "RepeatList"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r6, r12)
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r5 = r5.addSubItem((int) r9, (int) r8, (java.lang.CharSequence) r6, (boolean) r2)
            r3[r2] = r5
            r0.repeatListItem = r5
            org.telegram.ui.ActionBar.ActionBarMenuItem r5 = r0.repeatButton
            r6 = 2131165828(0x7var_, float:1.7945884E38)
            r8 = 2131626856(0x7f0e0b68, float:1.888096E38)
            java.lang.String r12 = "ShuffleList"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r12, r8)
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r5 = r5.addSubItem((int) r10, (int) r6, (java.lang.CharSequence) r8, (boolean) r2)
            r3[r10] = r5
            r0.shuffleListItem = r5
            org.telegram.ui.ActionBar.ActionBarMenuItem r5 = r0.repeatButton
            r6 = 2131165822(0x7var_e, float:1.7945872E38)
            r8 = 2131626634(0x7f0e0a8a, float:1.888051E38)
            java.lang.String r12 = "ReverseOrder"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r12, r8)
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r5 = r5.addSubItem((int) r2, (int) r6, (java.lang.CharSequence) r8, (boolean) r2)
            r3[r13] = r5
            r0.reverseOrderItem = r5
            r5 = 0
            r6 = 0
        L_0x0464:
            if (r5 >= r9) goto L_0x0498
            r8 = r3[r5]
            android.widget.TextView r8 = r8.getTextView()
            int r12 = r8.getPaddingLeft()
            int r19 = r8.getPaddingRight()
            int r12 = r12 + r19
            android.text.TextPaint r13 = r8.getPaint()
            java.lang.CharSequence r8 = r8.getText()
            java.lang.String r8 = r8.toString()
            float r8 = r13.measureText(r8)
            double r10 = (double) r8
            double r10 = java.lang.Math.ceil(r10)
            int r8 = (int) r10
            int r12 = r12 + r8
            int r6 = java.lang.Math.max(r6, r12)
            int r5 = r5 + 1
            r10 = 2
            r11 = 51
            r13 = 3
            goto L_0x0464
        L_0x0498:
            r5 = 1111490560(0x42400000, float:48.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r6 = r6 + r5
            r5 = 0
        L_0x04a0:
            if (r5 >= r9) goto L_0x04b1
            r8 = r3[r5]
            android.widget.TextView r8 = r8.getTextView()
            r8.setMinimumWidth(r6)
            r8.setMinWidth(r6)
            int r5 = r5 + 1
            goto L_0x04a0
        L_0x04b1:
            org.telegram.ui.ActionBar.ActionBarMenuItem r3 = r0.repeatButton
            org.telegram.ui.Components.-$$Lambda$AudioPlayerAlert$lZ69jfhPOaoo51qR3Yh6sSR56wc r5 = new org.telegram.ui.Components.-$$Lambda$AudioPlayerAlert$lZ69jfhPOaoo51qR3Yh6sSR56wc
            r5.<init>()
            r3.setDelegate(r5)
            android.view.View[] r3 = r0.buttons
            android.widget.ImageView r5 = new android.widget.ImageView
            r5.<init>(r1)
            r3[r2] = r5
            android.widget.ImageView$ScaleType r3 = android.widget.ImageView.ScaleType.CENTER
            r5.setScaleType(r3)
            android.graphics.PorterDuffColorFilter r3 = new android.graphics.PorterDuffColorFilter
            java.lang.String r6 = "player_button"
            int r8 = org.telegram.ui.ActionBar.Theme.getColor(r6)
            android.graphics.PorterDuff$Mode r10 = android.graphics.PorterDuff.Mode.MULTIPLY
            r3.<init>(r8, r10)
            r5.setColorFilter(r3)
            r3 = 2131165825(0x7var_, float:1.7945878E38)
            r5.setImageResource(r3)
            int r3 = android.os.Build.VERSION.SDK_INT
            r8 = 1102053376(0x41b00000, float:22.0)
            if (r3 < r15) goto L_0x04f4
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r14)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r8)
            android.graphics.drawable.Drawable r3 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r3, r2, r10)
            r5.setBackgroundDrawable(r3)
        L_0x04f4:
            r3 = 51
            r10 = 48
            android.widget.FrameLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createFrame(r10, r10, r3)
            r4.addView(r5, r11)
            org.telegram.ui.Components.-$$Lambda$AudioPlayerAlert$Ag-QGm7oY1scXMvar_yQTJPlAakA r3 = org.telegram.ui.Components.$$Lambda$AudioPlayerAlert$AgQGm7oY1scXMvar_yQTJPlAakA.INSTANCE
            r5.setOnClickListener(r3)
            r3 = 2131624006(0x7f0e0046, float:1.887518E38)
            java.lang.String r10 = "AccDescrPrevious"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r10, r3)
            r5.setContentDescription(r3)
            android.view.View[] r3 = r0.buttons
            android.widget.ImageView r5 = new android.widget.ImageView
            r5.<init>(r1)
            r0.playButton = r5
            r10 = 2
            r3[r10] = r5
            android.widget.ImageView$ScaleType r3 = android.widget.ImageView.ScaleType.CENTER
            r5.setScaleType(r3)
            android.widget.ImageView r3 = r0.playButton
            r5 = 2131165824(0x7var_, float:1.7945876E38)
            r3.setImageResource(r5)
            android.widget.ImageView r3 = r0.playButton
            android.graphics.PorterDuffColorFilter r5 = new android.graphics.PorterDuffColorFilter
            int r10 = org.telegram.ui.ActionBar.Theme.getColor(r6)
            android.graphics.PorterDuff$Mode r11 = android.graphics.PorterDuff.Mode.MULTIPLY
            r5.<init>(r10, r11)
            r3.setColorFilter(r5)
            int r3 = android.os.Build.VERSION.SDK_INT
            if (r3 < r15) goto L_0x0550
            android.widget.ImageView r3 = r0.playButton
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r14)
            r10 = 1103101952(0x41CLASSNAME, float:24.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            android.graphics.drawable.Drawable r5 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r5, r2, r10)
            r3.setBackgroundDrawable(r5)
        L_0x0550:
            android.widget.ImageView r3 = r0.playButton
            r5 = 51
            r10 = 48
            android.widget.FrameLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createFrame(r10, r10, r5)
            r4.addView(r3, r11)
            android.widget.ImageView r3 = r0.playButton
            org.telegram.ui.Components.-$$Lambda$AudioPlayerAlert$4DMl9_Is0AQM3ngWCdPt5fhxPmc r5 = org.telegram.ui.Components.$$Lambda$AudioPlayerAlert$4DMl9_Is0AQM3ngWCdPt5fhxPmc.INSTANCE
            r3.setOnClickListener(r5)
            android.view.View[] r3 = r0.buttons
            android.widget.ImageView r5 = new android.widget.ImageView
            r5.<init>(r1)
            r10 = 3
            r3[r10] = r5
            android.widget.ImageView$ScaleType r3 = android.widget.ImageView.ScaleType.CENTER
            r5.setScaleType(r3)
            android.graphics.PorterDuffColorFilter r3 = new android.graphics.PorterDuffColorFilter
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r6)
            android.graphics.PorterDuff$Mode r10 = android.graphics.PorterDuff.Mode.MULTIPLY
            r3.<init>(r6, r10)
            r5.setColorFilter(r3)
            r3 = 2131165821(0x7var_d, float:1.794587E38)
            r5.setImageResource(r3)
            int r3 = android.os.Build.VERSION.SDK_INT
            if (r3 < r15) goto L_0x059a
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r14)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r8)
            android.graphics.drawable.Drawable r3 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r3, r2, r6)
            r5.setBackgroundDrawable(r3)
        L_0x059a:
            r3 = 51
            r6 = 48
            android.widget.FrameLayout$LayoutParams r8 = org.telegram.ui.Components.LayoutHelper.createFrame(r6, r6, r3)
            r4.addView(r5, r8)
            org.telegram.ui.Components.-$$Lambda$AudioPlayerAlert$_25s4XwmMXC5vKIS0zNvR6jdxfg r3 = org.telegram.ui.Components.$$Lambda$AudioPlayerAlert$_25s4XwmMXC5vKIS0zNvR6jdxfg.INSTANCE
            r5.setOnClickListener(r3)
            r3 = 2131625812(0x7f0e0754, float:1.8878843E38)
            java.lang.String r6 = "Next"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r6, r3)
            r5.setContentDescription(r3)
            android.view.View[] r3 = r0.buttons
            org.telegram.ui.ActionBar.ActionBarMenuItem r6 = new org.telegram.ui.ActionBar.ActionBarMenuItem
            int r7 = org.telegram.ui.ActionBar.Theme.getColor(r7)
            r8 = 0
            r10 = 0
            r6.<init>(r1, r8, r10, r7)
            r0.optionsButton = r6
            r3[r9] = r6
            r6.setLongClickEnabled(r10)
            org.telegram.ui.ActionBar.ActionBarMenuItem r3 = r0.optionsButton
            r6 = 2131165439(0x7var_ff, float:1.7945095E38)
            r3.setIcon((int) r6)
            org.telegram.ui.ActionBar.ActionBarMenuItem r3 = r0.optionsButton
            r6 = 2
            r3.setSubMenuOpenSide(r6)
            org.telegram.ui.ActionBar.ActionBarMenuItem r3 = r0.optionsButton
            r3.setPopupAnimationEnabled(r10)
            org.telegram.ui.ActionBar.ActionBarMenuItem r3 = r0.optionsButton
            r6 = 1121583104(0x42da0000, float:109.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r6 = -r6
            r3.setAdditionalYOffset(r6)
            int r3 = android.os.Build.VERSION.SDK_INT
            if (r3 < r15) goto L_0x05fe
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r14)
            r6 = 1099956224(0x41900000, float:18.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            android.graphics.drawable.Drawable r3 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r3, r2, r6)
            r5.setBackgroundDrawable(r3)
        L_0x05fe:
            org.telegram.ui.ActionBar.ActionBarMenuItem r3 = r0.optionsButton
            r5 = 51
            r6 = 48
            android.widget.FrameLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createFrame(r6, r6, r5)
            r4.addView(r3, r6)
            org.telegram.ui.ActionBar.ActionBarMenuItem r3 = r0.optionsButton
            r4 = 2131165678(0x7var_ee, float:1.794558E38)
            r5 = 2131625308(0x7f0e055c, float:1.887782E38)
            java.lang.String r6 = "Forward"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r6, r5)
            r3.addSubItem(r2, r4, r5)
            org.telegram.ui.ActionBar.ActionBarMenuItem r3 = r0.optionsButton
            r4 = 2131165725(0x7var_d, float:1.7945675E38)
            r5 = 2131626797(0x7f0e0b2d, float:1.888084E38)
            java.lang.String r6 = "ShareFile"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r6, r5)
            r6 = 2
            r3.addSubItem(r6, r4, r5)
            org.telegram.ui.ActionBar.ActionBarMenuItem r3 = r0.optionsButton
            r4 = 2131165698(0x7var_, float:1.794562E38)
            r5 = 2131626845(0x7f0e0b5d, float:1.8880938E38)
            java.lang.String r6 = "ShowInChat"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r6, r5)
            r3.addSubItem(r9, r4, r5)
            org.telegram.ui.ActionBar.ActionBarMenuItem r3 = r0.optionsButton
            org.telegram.ui.Components.-$$Lambda$AudioPlayerAlert$rl-WKm2X4aPzFohQGvIAPMJDY84 r4 = new org.telegram.ui.Components.-$$Lambda$AudioPlayerAlert$rl-WKm2X4aPzFohQGvIAPMJDY84
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
            org.telegram.ui.Components.AudioPlayerAlert$9 r3 = new org.telegram.ui.Components.AudioPlayerAlert$9
            r3.<init>(r1)
            r0.listView = r3
            r4 = 0
            r3.setClipToPadding(r4)
            org.telegram.ui.Components.RecyclerListView r3 = r0.listView
            androidx.recyclerview.widget.LinearLayoutManager r5 = new androidx.recyclerview.widget.LinearLayoutManager
            android.content.Context r6 = r20.getContext()
            r5.<init>(r6, r2, r4)
            r0.layoutManager = r5
            r3.setLayoutManager(r5)
            org.telegram.ui.Components.RecyclerListView r3 = r0.listView
            r3.setHorizontalScrollBarEnabled(r4)
            org.telegram.ui.Components.RecyclerListView r3 = r0.listView
            r3.setVerticalScrollBarEnabled(r4)
            android.view.ViewGroup r3 = r0.containerView
            org.telegram.ui.Components.RecyclerListView r4 = r0.listView
            r5 = 51
            r6 = -1
            android.widget.FrameLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createFrame(r6, r6, r5)
            r3.addView(r4, r5)
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
            org.telegram.ui.Components.-$$Lambda$AudioPlayerAlert$Pu3CGhOEdVpK-Q05Oh0i-XMJ5Dc r4 = org.telegram.ui.Components.$$Lambda$AudioPlayerAlert$Pu3CGhOEdVpKQ05Oh0iXMJ5Dc.INSTANCE
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
            int r8 = org.telegram.messenger.AndroidUtilities.getShadowHeight()
            r5.<init>(r7, r8, r6)
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
            android.widget.FrameLayout r3 = new android.widget.FrameLayout
            r3.<init>(r1)
            r0.blurredView = r3
            r4 = 0
            r3.setAlpha(r4)
            android.widget.FrameLayout r3 = r0.blurredView
            r3.setVisibility(r9)
            android.widget.FrameLayout r3 = r20.getContainer()
            android.widget.FrameLayout r4 = r0.blurredView
            r3.addView(r4)
            org.telegram.ui.Components.BackupImageView r3 = new org.telegram.ui.Components.BackupImageView
            r3.<init>(r1)
            r0.bigAlbumConver = r3
            r3.setAspectFit(r2)
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
            r20.updateRepeatButton()
            r20.updateRepeatButton()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.AudioPlayerAlert.<init>(android.content.Context):void");
    }

    public /* synthetic */ void lambda$new$0$AudioPlayerAlert(View view) {
        if (MediaController.getInstance().getPlaybackSpeed(true) > 1.0f) {
            MediaController.getInstance().setPlaybackSpeed(true, 1.0f);
        } else {
            MediaController.getInstance().setPlaybackSpeed(true, 1.8f);
        }
        updatePlaybackButton();
    }

    public /* synthetic */ void lambda$new$1$AudioPlayerAlert(View view) {
        updateSubMenu();
        this.repeatButton.toggleSubMenu();
    }

    public /* synthetic */ void lambda$new$2$AudioPlayerAlert(int i) {
        if (i == 1 || i == 2) {
            MediaController.getInstance().toggleShuffleMusic(i);
            this.listAdapter.notifyDataSetChanged();
            return;
        }
        if (i == 4) {
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

    static /* synthetic */ void lambda$new$4(View view) {
        if (!MediaController.getInstance().isDownloadingCurrentMessage()) {
            if (MediaController.getInstance().isMessagePaused()) {
                MediaController.getInstance().playMessage(MediaController.getInstance().getPlayingMessageObject());
            } else {
                MediaController.getInstance().lambda$startAudioAgain$7$MediaController(MediaController.getInstance().getPlayingMessageObject());
            }
        }
    }

    public /* synthetic */ void lambda$new$6$AudioPlayerAlert(View view) {
        this.optionsButton.toggleSubMenu();
    }

    static /* synthetic */ void lambda$new$7(View view, int i) {
        if (view instanceof AudioPlayerCell) {
            ((AudioPlayerCell) view).didPressedButton();
        }
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

    private void updateSubMenu() {
        this.shuffleListItem.setChecked(SharedConfig.shuffleMusic);
        this.reverseOrderItem.setChecked(SharedConfig.playOrderReversed);
        boolean z = false;
        this.repeatListItem.setChecked(SharedConfig.repeatMode == 1);
        ActionBarMenuSubItem actionBarMenuSubItem = this.repeatSongItem;
        if (SharedConfig.repeatMode == 2) {
            z = true;
        }
        actionBarMenuSubItem.setChecked(z);
    }

    private void updatePlaybackButton() {
        if (MediaController.getInstance().getPlaybackSpeed(true) > 1.0f) {
            this.playbackSpeedButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("inappPlayerPlayPause"), PorterDuff.Mode.MULTIPLY));
        } else {
            this.playbackSpeedButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("inappPlayerClose"), PorterDuff.Mode.MULTIPLY));
        }
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
            org.telegram.ui.Components.-$$Lambda$AudioPlayerAlert$EnpAFaSP83SQxwWX9b9xc6nshxI r0 = new org.telegram.ui.Components.-$$Lambda$AudioPlayerAlert$EnpAFaSP83SQxwWX9b9xc6nshxI
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
            r2 = 2131626797(0x7f0e0b2d, float:1.888084E38)
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
            r2 = 2131624210(0x7f0e0112, float:1.8875593E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r0, r2)     // Catch:{ Exception -> 0x00f9 }
            r6.setTitle(r0)     // Catch:{ Exception -> 0x00f9 }
            java.lang.String r0 = "OK"
            r2 = 2131626025(0x7f0e0829, float:1.8879275E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r0, r2)     // Catch:{ Exception -> 0x00f9 }
            r6.setPositiveButton(r0, r1)     // Catch:{ Exception -> 0x00f9 }
            java.lang.String r0 = "PleaseDownload"
            r1 = 2131626414(0x7f0e09ae, float:1.8880064E38)
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

    public /* synthetic */ void lambda$onSubItemClick$8$AudioPlayerAlert(ArrayList arrayList, DialogsActivity dialogsActivity, ArrayList arrayList2, CharSequence charSequence, boolean z) {
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
    public void showAlbumCover(boolean z) {
        if (z) {
            if (this.blurredView.getVisibility() != 0 && !this.blurredAnimationInProgress) {
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
            this.blurredAnimationInProgress = true;
            this.blurredView.animate().alpha(0.0f).setDuration(180).setListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    AudioPlayerAlert.this.blurredView.setVisibility(4);
                    AudioPlayerAlert.this.bigAlbumConver.setImageBitmap((Bitmap) null);
                    boolean unused = AudioPlayerAlert.this.blurredAnimationInProgress = false;
                }
            }).start();
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
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.musicDidLoad);
        DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
    }

    public void onBackPressed() {
        ActionBar actionBar2 = this.actionBar;
        if (actionBar2 == null || !actionBar2.isSearchFieldVisible()) {
            super.onBackPressed();
        } else {
            this.actionBar.closeSearchField();
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
        if (i == 0) {
            this.repeatButton.setIcon(NUM);
            this.repeatButton.setTag("player_button");
            this.repeatButton.setIconColor(Theme.getColor("player_button"));
            this.repeatButton.setContentDescription(LocaleController.getString("AccDescrRepeatOff", NUM));
        } else if (i == 1) {
            this.repeatButton.setIcon(NUM);
            this.repeatButton.setTag("player_buttonActive");
            this.repeatButton.setIconColor(Theme.getColor("player_buttonActive"));
            this.repeatButton.setContentDescription(LocaleController.getString("AccDescrRepeatList", NUM));
        } else if (i == 2) {
            this.repeatButton.setIcon(NUM);
            this.repeatButton.setTag("player_buttonActive");
            this.repeatButton.setIconColor(Theme.getColor("player_buttonActive"));
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
                String artworkUrl = playingMessageObject.getArtworkUrl(false);
                if (!TextUtils.isEmpty(artworkUrl)) {
                    this.placeholderImageView.setImage(artworkUrl, (String) null, (Drawable) null);
                } else {
                    this.placeholderImageView.setImageDrawable((Drawable) null);
                }
                this.placeholderImageView.invalidate();
            } else {
                this.placeholderImageView.setImageBitmap(audioInfo.getCover());
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
                return;
            }
            AudioPlayerAlert.this.playerLayout.setBackground((Drawable) null);
            AudioPlayerAlert.this.playerShadow.setVisibility(4);
            AudioPlayerAlert.this.listView.setPadding(0, AudioPlayerAlert.this.listView.getPaddingTop(), 0, 0);
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
                updateSearchResults(new ArrayList());
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
            updateSearchResults(arrayList2);
        }

        private void updateSearchResults(ArrayList<MessageObject> arrayList) {
            AndroidUtilities.runOnUIThread(new Runnable(arrayList) {
                public final /* synthetic */ ArrayList f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    AudioPlayerAlert.ListAdapter.this.lambda$updateSearchResults$3$AudioPlayerAlert$ListAdapter(this.f$1);
                }
            });
        }

        public /* synthetic */ void lambda$updateSearchResults$3$AudioPlayerAlert$ListAdapter(ArrayList arrayList) {
            if (AudioPlayerAlert.this.searching) {
                boolean unused = AudioPlayerAlert.this.searchWas = true;
                this.searchResult = arrayList;
                notifyDataSetChanged();
                AudioPlayerAlert.this.layoutManager.scrollToPosition(0);
            }
        }
    }
}
