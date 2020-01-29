package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.Keep;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
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
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
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
    private AnimatorSet actionBarAnimation;
    /* access modifiers changed from: private */
    public AnimatorSet animatorSet;
    private TextView authorTextView;
    /* access modifiers changed from: private */
    public ChatAvatarContainer avatarContainer;
    /* access modifiers changed from: private */
    public View[] buttons = new View[5];
    /* access modifiers changed from: private */
    public boolean draggingSeekBar;
    /* access modifiers changed from: private */
    public TextView durationTextView;
    /* access modifiers changed from: private */
    public float endTranslation;
    /* access modifiers changed from: private */
    public float fullAnimationProgress;
    /* access modifiers changed from: private */
    public int hasNoCover;
    /* access modifiers changed from: private */
    public boolean hasOptions = true;
    /* access modifiers changed from: private */
    public boolean inFullSize;
    /* access modifiers changed from: private */
    public boolean isInFullMode;
    private int lastTime;
    /* access modifiers changed from: private */
    public LinearLayoutManager layoutManager;
    /* access modifiers changed from: private */
    public ListAdapter listAdapter;
    /* access modifiers changed from: private */
    public RecyclerListView listView;
    /* access modifiers changed from: private */
    public ActionBarMenuItem menuItem;
    /* access modifiers changed from: private */
    public Drawable noCoverDrawable;
    private ActionBarMenuItem optionsButton;
    /* access modifiers changed from: private */
    public Paint paint = new Paint(1);
    /* access modifiers changed from: private */
    public float panelEndTranslation;
    private float panelStartTranslation;
    private LaunchActivity parentActivity;
    /* access modifiers changed from: private */
    public BackupImageView placeholderImageView;
    private ImageView playButton;
    private Drawable[] playOrderButtons = new Drawable[2];
    /* access modifiers changed from: private */
    public ImageView playbackSpeedButton;
    /* access modifiers changed from: private */
    public FrameLayout playerLayout;
    /* access modifiers changed from: private */
    public ArrayList<MessageObject> playlist;
    private LineProgressView progressView;
    private ImageView repeatButton;
    /* access modifiers changed from: private */
    public int scrollOffsetY = Integer.MAX_VALUE;
    /* access modifiers changed from: private */
    public boolean scrollToSong = true;
    /* access modifiers changed from: private */
    public ActionBarMenuItem searchItem;
    /* access modifiers changed from: private */
    public int searchOpenOffset;
    /* access modifiers changed from: private */
    public int searchOpenPosition = -1;
    /* access modifiers changed from: private */
    public boolean searchWas;
    /* access modifiers changed from: private */
    public boolean searching;
    private SeekBarView seekBarView;
    /* access modifiers changed from: private */
    public View shadow;
    private View shadow2;
    /* access modifiers changed from: private */
    public Drawable shadowDrawable;
    private ActionBarMenuItem shuffleButton;
    private float startTranslation;
    /* access modifiers changed from: private */
    public float thumbMaxScale;
    /* access modifiers changed from: private */
    public int thumbMaxX;
    /* access modifiers changed from: private */
    public int thumbMaxY;
    private SimpleTextView timeTextView;
    private TextView titleTextView;
    private int topBeforeSwitch;

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
            r3 = 2
            android.graphics.drawable.Drawable[] r4 = new android.graphics.drawable.Drawable[r3]
            r0.playOrderButtons = r4
            r0.hasOptions = r2
            r0.scrollToSong = r2
            r4 = -1
            r0.searchOpenPosition = r4
            android.graphics.Paint r5 = new android.graphics.Paint
            r5.<init>(r2)
            r0.paint = r5
            r5 = 2147483647(0x7fffffff, float:NaN)
            r0.scrollOffsetY = r5
            org.telegram.messenger.MediaController r5 = org.telegram.messenger.MediaController.getInstance()
            org.telegram.messenger.MessageObject r5 = r5.getPlayingMessageObject()
            if (r5 == 0) goto L_0x0034
            int r6 = r5.currentAccount
            r0.currentAccount = r6
            goto L_0x0038
        L_0x0034:
            int r6 = org.telegram.messenger.UserConfig.selectedAccount
            r0.currentAccount = r6
        L_0x0038:
            r6 = r1
            org.telegram.ui.LaunchActivity r6 = (org.telegram.ui.LaunchActivity) r6
            r0.parentActivity = r6
            android.content.res.Resources r6 = r22.getResources()
            r7 = 2131165719(0x7var_, float:1.7945663E38)
            android.graphics.drawable.Drawable r6 = r6.getDrawable(r7)
            android.graphics.drawable.Drawable r6 = r6.mutate()
            r0.noCoverDrawable = r6
            android.graphics.drawable.Drawable r6 = r0.noCoverDrawable
            android.graphics.PorterDuffColorFilter r7 = new android.graphics.PorterDuffColorFilter
            java.lang.String r8 = "player_placeholder"
            int r8 = org.telegram.ui.ActionBar.Theme.getColor(r8)
            android.graphics.PorterDuff$Mode r9 = android.graphics.PorterDuff.Mode.MULTIPLY
            r7.<init>(r8, r9)
            r6.setColorFilter(r7)
            int r6 = r0.currentAccount
            org.telegram.messenger.DownloadController r6 = org.telegram.messenger.DownloadController.getInstance(r6)
            int r6 = r6.generateObserverTag()
            r0.TAG = r6
            int r6 = r0.currentAccount
            org.telegram.messenger.NotificationCenter r6 = org.telegram.messenger.NotificationCenter.getInstance(r6)
            int r7 = org.telegram.messenger.NotificationCenter.messagePlayingDidReset
            r6.addObserver(r0, r7)
            int r6 = r0.currentAccount
            org.telegram.messenger.NotificationCenter r6 = org.telegram.messenger.NotificationCenter.getInstance(r6)
            int r7 = org.telegram.messenger.NotificationCenter.messagePlayingPlayStateChanged
            r6.addObserver(r0, r7)
            int r6 = r0.currentAccount
            org.telegram.messenger.NotificationCenter r6 = org.telegram.messenger.NotificationCenter.getInstance(r6)
            int r7 = org.telegram.messenger.NotificationCenter.messagePlayingDidStart
            r6.addObserver(r0, r7)
            int r6 = r0.currentAccount
            org.telegram.messenger.NotificationCenter r6 = org.telegram.messenger.NotificationCenter.getInstance(r6)
            int r7 = org.telegram.messenger.NotificationCenter.messagePlayingProgressDidChanged
            r6.addObserver(r0, r7)
            int r6 = r0.currentAccount
            org.telegram.messenger.NotificationCenter r6 = org.telegram.messenger.NotificationCenter.getInstance(r6)
            int r7 = org.telegram.messenger.NotificationCenter.musicDidLoad
            r6.addObserver(r0, r7)
            android.content.res.Resources r6 = r22.getResources()
            r7 = 2131165852(0x7var_c, float:1.7945933E38)
            android.graphics.drawable.Drawable r6 = r6.getDrawable(r7)
            android.graphics.drawable.Drawable r6 = r6.mutate()
            r0.shadowDrawable = r6
            android.graphics.drawable.Drawable r6 = r0.shadowDrawable
            android.graphics.PorterDuffColorFilter r7 = new android.graphics.PorterDuffColorFilter
            java.lang.String r8 = "player_background"
            int r8 = org.telegram.ui.ActionBar.Theme.getColor(r8)
            android.graphics.PorterDuff$Mode r9 = android.graphics.PorterDuff.Mode.MULTIPLY
            r7.<init>(r8, r9)
            r6.setColorFilter(r7)
            android.graphics.Paint r6 = r0.paint
            java.lang.String r7 = "player_placeholderBackground"
            int r7 = org.telegram.ui.ActionBar.Theme.getColor(r7)
            r6.setColor(r7)
            org.telegram.ui.Components.AudioPlayerAlert$1 r6 = new org.telegram.ui.Components.AudioPlayerAlert$1
            r6.<init>(r1)
            r0.containerView = r6
            android.view.ViewGroup r6 = r0.containerView
            r7 = 0
            r6.setWillNotDraw(r7)
            android.view.ViewGroup r6 = r0.containerView
            int r8 = r0.backgroundPaddingLeft
            r6.setPadding(r8, r7, r8, r7)
            org.telegram.ui.ActionBar.ActionBar r6 = new org.telegram.ui.ActionBar.ActionBar
            r6.<init>(r1)
            r0.actionBar = r6
            org.telegram.ui.ActionBar.ActionBar r6 = r0.actionBar
            java.lang.String r8 = "player_actionBar"
            int r8 = org.telegram.ui.ActionBar.Theme.getColor(r8)
            r6.setBackgroundColor(r8)
            org.telegram.ui.ActionBar.ActionBar r6 = r0.actionBar
            r8 = 2131165427(0x7var_f3, float:1.794507E38)
            r6.setBackButtonImage(r8)
            org.telegram.ui.ActionBar.ActionBar r6 = r0.actionBar
            java.lang.String r8 = "player_actionBarItems"
            int r8 = org.telegram.ui.ActionBar.Theme.getColor(r8)
            r6.setItemsColor(r8, r7)
            org.telegram.ui.ActionBar.ActionBar r6 = r0.actionBar
            java.lang.String r8 = "player_actionBarSelector"
            int r8 = org.telegram.ui.ActionBar.Theme.getColor(r8)
            r6.setItemsBackgroundColor(r8, r7)
            org.telegram.ui.ActionBar.ActionBar r6 = r0.actionBar
            java.lang.String r8 = "player_actionBarTitle"
            int r9 = org.telegram.ui.ActionBar.Theme.getColor(r8)
            r6.setTitleColor(r9)
            org.telegram.ui.ActionBar.ActionBar r6 = r0.actionBar
            java.lang.String r9 = "player_actionBarSubtitle"
            int r9 = org.telegram.ui.ActionBar.Theme.getColor(r9)
            r6.setSubtitleColor(r9)
            org.telegram.ui.ActionBar.ActionBar r6 = r0.actionBar
            r9 = 0
            r6.setAlpha(r9)
            org.telegram.ui.ActionBar.ActionBar r6 = r0.actionBar
            java.lang.String r10 = "1"
            r6.setTitle(r10)
            org.telegram.ui.ActionBar.ActionBar r6 = r0.actionBar
            java.lang.String r10 = "1"
            r6.setSubtitle(r10)
            org.telegram.ui.ActionBar.ActionBar r6 = r0.actionBar
            org.telegram.ui.ActionBar.SimpleTextView r6 = r6.getTitleTextView()
            r6.setAlpha(r9)
            org.telegram.ui.ActionBar.ActionBar r6 = r0.actionBar
            org.telegram.ui.ActionBar.SimpleTextView r6 = r6.getSubtitleTextView()
            r6.setAlpha(r9)
            org.telegram.ui.Components.ChatAvatarContainer r6 = new org.telegram.ui.Components.ChatAvatarContainer
            r10 = 0
            r6.<init>(r1, r10, r7)
            r0.avatarContainer = r6
            org.telegram.ui.Components.ChatAvatarContainer r6 = r0.avatarContainer
            r6.setEnabled(r7)
            org.telegram.ui.Components.ChatAvatarContainer r6 = r0.avatarContainer
            int r11 = org.telegram.ui.ActionBar.Theme.getColor(r8)
            java.lang.String r12 = "player_actionBarSubtitle"
            int r12 = org.telegram.ui.ActionBar.Theme.getColor(r12)
            r6.setTitleColors(r11, r12)
            if (r5 == 0) goto L_0x01f1
            long r5 = r5.getDialogId()
            int r11 = (int) r5
            r12 = 32
            long r5 = r5 >> r12
            int r6 = (int) r5
            if (r11 == 0) goto L_0x01bd
            if (r11 <= 0) goto L_0x019f
            int r5 = r0.currentAccount
            org.telegram.messenger.MessagesController r5 = org.telegram.messenger.MessagesController.getInstance(r5)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r11)
            org.telegram.tgnet.TLRPC$User r5 = r5.getUser(r6)
            if (r5 == 0) goto L_0x01f1
            org.telegram.ui.Components.ChatAvatarContainer r6 = r0.avatarContainer
            java.lang.String r11 = r5.first_name
            java.lang.String r12 = r5.last_name
            java.lang.String r11 = org.telegram.messenger.ContactsController.formatName(r11, r12)
            r6.setTitle(r11)
            org.telegram.ui.Components.ChatAvatarContainer r6 = r0.avatarContainer
            r6.setUserAvatar(r5)
            goto L_0x01f1
        L_0x019f:
            int r5 = r0.currentAccount
            org.telegram.messenger.MessagesController r5 = org.telegram.messenger.MessagesController.getInstance(r5)
            int r6 = -r11
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            org.telegram.tgnet.TLRPC$Chat r5 = r5.getChat(r6)
            if (r5 == 0) goto L_0x01f1
            org.telegram.ui.Components.ChatAvatarContainer r6 = r0.avatarContainer
            java.lang.String r11 = r5.title
            r6.setTitle(r11)
            org.telegram.ui.Components.ChatAvatarContainer r6 = r0.avatarContainer
            r6.setChatAvatar(r5)
            goto L_0x01f1
        L_0x01bd:
            int r5 = r0.currentAccount
            org.telegram.messenger.MessagesController r5 = org.telegram.messenger.MessagesController.getInstance(r5)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            org.telegram.tgnet.TLRPC$EncryptedChat r5 = r5.getEncryptedChat(r6)
            if (r5 == 0) goto L_0x01f1
            int r6 = r0.currentAccount
            org.telegram.messenger.MessagesController r6 = org.telegram.messenger.MessagesController.getInstance(r6)
            int r5 = r5.user_id
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            org.telegram.tgnet.TLRPC$User r5 = r6.getUser(r5)
            if (r5 == 0) goto L_0x01f1
            org.telegram.ui.Components.ChatAvatarContainer r6 = r0.avatarContainer
            java.lang.String r11 = r5.first_name
            java.lang.String r12 = r5.last_name
            java.lang.String r11 = org.telegram.messenger.ContactsController.formatName(r11, r12)
            r6.setTitle(r11)
            org.telegram.ui.Components.ChatAvatarContainer r6 = r0.avatarContainer
            r6.setUserAvatar(r5)
        L_0x01f1:
            org.telegram.ui.Components.ChatAvatarContainer r5 = r0.avatarContainer
            r6 = 2131624299(0x7f0e016b, float:1.8875774E38)
            java.lang.String r11 = "AudioTitle"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r11, r6)
            r5.setSubtitle(r6)
            org.telegram.ui.ActionBar.ActionBar r5 = r0.actionBar
            org.telegram.ui.Components.ChatAvatarContainer r6 = r0.avatarContainer
            r11 = -2
            r12 = -1082130432(0xffffffffbvar_, float:-1.0)
            r13 = 51
            r14 = 1113587712(0x42600000, float:56.0)
            r15 = 0
            r16 = 1109393408(0x42200000, float:40.0)
            r17 = 0
            android.widget.FrameLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createFrame(r11, r12, r13, r14, r15, r16, r17)
            r5.addView(r6, r7, r11)
            org.telegram.ui.ActionBar.ActionBar r5 = r0.actionBar
            org.telegram.ui.ActionBar.ActionBarMenu r5 = r5.createMenu()
            r6 = 2131165434(0x7var_fa, float:1.7945085E38)
            org.telegram.ui.ActionBar.ActionBarMenuItem r6 = r5.addItem((int) r7, (int) r6)
            r0.menuItem = r6
            org.telegram.ui.ActionBar.ActionBarMenuItem r6 = r0.menuItem
            r11 = 2131165652(0x7var_d4, float:1.7945527E38)
            r12 = 2131625173(0x7f0e04d5, float:1.8877546E38)
            java.lang.String r13 = "Forward"
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r13, r12)
            r6.addSubItem((int) r2, (int) r11, (java.lang.CharSequence) r12)
            org.telegram.ui.ActionBar.ActionBarMenuItem r6 = r0.menuItem
            r11 = 2131165696(0x7var_, float:1.7945616E38)
            r12 = 2131626584(0x7f0e0a58, float:1.8880408E38)
            java.lang.String r13 = "ShareFile"
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r13, r12)
            r6.addSubItem((int) r3, (int) r11, (java.lang.CharSequence) r12)
            org.telegram.ui.ActionBar.ActionBarMenuItem r6 = r0.menuItem
            r11 = 2131165672(0x7var_e8, float:1.7945568E38)
            r12 = 2131626621(0x7f0e0a7d, float:1.8880483E38)
            java.lang.String r13 = "ShowInChat"
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r13, r12)
            r13 = 4
            r6.addSubItem((int) r13, (int) r11, (java.lang.CharSequence) r12)
            org.telegram.ui.ActionBar.ActionBarMenuItem r6 = r0.menuItem
            r11 = 2131623980(0x7f0e002c, float:1.8875127E38)
            java.lang.String r12 = "AccDescrMoreOptions"
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r12, r11)
            r6.setContentDescription(r11)
            org.telegram.ui.ActionBar.ActionBarMenuItem r6 = r0.menuItem
            r11 = 1111490560(0x42400000, float:48.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            float r11 = (float) r11
            r6.setTranslationX(r11)
            org.telegram.ui.ActionBar.ActionBarMenuItem r6 = r0.menuItem
            r6.setAlpha(r9)
            r6 = 2131165437(0x7var_fd, float:1.7945091E38)
            org.telegram.ui.ActionBar.ActionBarMenuItem r5 = r5.addItem((int) r7, (int) r6)
            org.telegram.ui.ActionBar.ActionBarMenuItem r5 = r5.setIsSearchField(r2)
            org.telegram.ui.Components.AudioPlayerAlert$2 r6 = new org.telegram.ui.Components.AudioPlayerAlert$2
            r6.<init>()
            org.telegram.ui.ActionBar.ActionBarMenuItem r5 = r5.setActionBarMenuItemSearchListener(r6)
            r0.searchItem = r5
            org.telegram.ui.ActionBar.ActionBarMenuItem r5 = r0.searchItem
            r6 = 2131626457(0x7f0e09d9, float:1.888015E38)
            java.lang.String r11 = "Search"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r11, r6)
            r5.setContentDescription(r6)
            org.telegram.ui.ActionBar.ActionBarMenuItem r5 = r0.searchItem
            org.telegram.ui.Components.EditTextBoldCursor r5 = r5.getSearchField()
            r6 = 2131626457(0x7f0e09d9, float:1.888015E38)
            java.lang.String r11 = "Search"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r11, r6)
            r5.setHint(r6)
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r8)
            r5.setTextColor(r6)
            java.lang.String r6 = "player_time"
            int r11 = org.telegram.ui.ActionBar.Theme.getColor(r6)
            r5.setHintTextColor(r11)
            int r11 = org.telegram.ui.ActionBar.Theme.getColor(r8)
            r5.setCursorColor(r11)
            boolean r5 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r5 != 0) goto L_0x02db
            org.telegram.ui.ActionBar.ActionBar r5 = r0.actionBar
            r5.showActionModeTop()
            org.telegram.ui.ActionBar.ActionBar r5 = r0.actionBar
            java.lang.String r11 = "player_actionBarTop"
            int r11 = org.telegram.ui.ActionBar.Theme.getColor(r11)
            r5.setActionModeTopColor(r11)
        L_0x02db:
            org.telegram.ui.ActionBar.ActionBar r5 = r0.actionBar
            org.telegram.ui.Components.AudioPlayerAlert$3 r11 = new org.telegram.ui.Components.AudioPlayerAlert$3
            r11.<init>()
            r5.setActionBarMenuOnItemClick(r11)
            android.view.View r5 = new android.view.View
            r5.<init>(r1)
            r0.shadow = r5
            android.view.View r5 = r0.shadow
            r5.setAlpha(r9)
            android.view.View r5 = r0.shadow
            r11 = 2131165424(0x7var_f0, float:1.7945065E38)
            r5.setBackgroundResource(r11)
            android.view.View r5 = new android.view.View
            r5.<init>(r1)
            r0.shadow2 = r5
            android.view.View r5 = r0.shadow2
            r5.setAlpha(r9)
            android.view.View r5 = r0.shadow2
            r5.setBackgroundResource(r11)
            org.telegram.ui.Components.AudioPlayerAlert$4 r5 = new org.telegram.ui.Components.AudioPlayerAlert$4
            r5.<init>(r1)
            r0.playerLayout = r5
            android.widget.FrameLayout r5 = r0.playerLayout
            java.lang.String r11 = "player_background"
            int r11 = org.telegram.ui.ActionBar.Theme.getColor(r11)
            r5.setBackgroundColor(r11)
            org.telegram.ui.Components.AudioPlayerAlert$5 r5 = new org.telegram.ui.Components.AudioPlayerAlert$5
            r5.<init>(r1)
            r0.placeholderImageView = r5
            org.telegram.ui.Components.BackupImageView r5 = r0.placeholderImageView
            r11 = 1101004800(0x41a00000, float:20.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            r5.setRoundRadius(r11)
            org.telegram.ui.Components.BackupImageView r5 = r0.placeholderImageView
            r5.setPivotX(r9)
            org.telegram.ui.Components.BackupImageView r5 = r0.placeholderImageView
            r5.setPivotY(r9)
            org.telegram.ui.Components.BackupImageView r5 = r0.placeholderImageView
            org.telegram.ui.Components.-$$Lambda$AudioPlayerAlert$rAc8YAca3eZCLAfei_UTWtnjvbQ r9 = new org.telegram.ui.Components.-$$Lambda$AudioPlayerAlert$rAc8YAca3eZCLAfei_UTWtnjvbQ
            r9.<init>()
            r5.setOnClickListener(r9)
            android.widget.TextView r5 = new android.widget.TextView
            r5.<init>(r1)
            r0.titleTextView = r5
            android.widget.TextView r5 = r0.titleTextView
            int r8 = org.telegram.ui.ActionBar.Theme.getColor(r8)
            r5.setTextColor(r8)
            android.widget.TextView r5 = r0.titleTextView
            r8 = 1097859072(0x41700000, float:15.0)
            r5.setTextSize(r2, r8)
            android.widget.TextView r5 = r0.titleTextView
            java.lang.String r8 = "fonts/rmedium.ttf"
            android.graphics.Typeface r8 = org.telegram.messenger.AndroidUtilities.getTypeface(r8)
            r5.setTypeface(r8)
            android.widget.TextView r5 = r0.titleTextView
            android.text.TextUtils$TruncateAt r8 = android.text.TextUtils.TruncateAt.END
            r5.setEllipsize(r8)
            android.widget.TextView r5 = r0.titleTextView
            r5.setSingleLine(r2)
            android.widget.FrameLayout r5 = r0.playerLayout
            android.widget.TextView r8 = r0.titleTextView
            r14 = -1
            r15 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r16 = 51
            r17 = 1116733440(0x42900000, float:72.0)
            r18 = 1099956224(0x41900000, float:18.0)
            r19 = 1114636288(0x42700000, float:60.0)
            r20 = 0
            android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r14, r15, r16, r17, r18, r19, r20)
            r5.addView(r8, r9)
            android.widget.TextView r5 = new android.widget.TextView
            r5.<init>(r1)
            r0.authorTextView = r5
            android.widget.TextView r5 = r0.authorTextView
            int r8 = org.telegram.ui.ActionBar.Theme.getColor(r6)
            r5.setTextColor(r8)
            android.widget.TextView r5 = r0.authorTextView
            r8 = 1096810496(0x41600000, float:14.0)
            r5.setTextSize(r2, r8)
            android.widget.TextView r5 = r0.authorTextView
            android.text.TextUtils$TruncateAt r8 = android.text.TextUtils.TruncateAt.END
            r5.setEllipsize(r8)
            android.widget.TextView r5 = r0.authorTextView
            r5.setSingleLine(r2)
            android.widget.FrameLayout r5 = r0.playerLayout
            android.widget.TextView r8 = r0.authorTextView
            r18 = 1109393408(0x42200000, float:40.0)
            android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r14, r15, r16, r17, r18, r19, r20)
            r5.addView(r8, r9)
            org.telegram.ui.ActionBar.ActionBarMenuItem r5 = new org.telegram.ui.ActionBar.ActionBarMenuItem
            java.lang.String r8 = "player_actionBarItems"
            int r8 = org.telegram.ui.ActionBar.Theme.getColor(r8)
            r5.<init>(r1, r10, r7, r8)
            r0.optionsButton = r5
            org.telegram.ui.ActionBar.ActionBarMenuItem r5 = r0.optionsButton
            r5.setLongClickEnabled(r7)
            org.telegram.ui.ActionBar.ActionBarMenuItem r5 = r0.optionsButton
            r8 = 2131165434(0x7var_fa, float:1.7945085E38)
            r5.setIcon((int) r8)
            org.telegram.ui.ActionBar.ActionBarMenuItem r5 = r0.optionsButton
            r8 = 1123024896(0x42var_, float:120.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            int r8 = -r8
            r5.setAdditionalYOffset(r8)
            android.widget.FrameLayout r5 = r0.playerLayout
            org.telegram.ui.ActionBar.ActionBarMenuItem r8 = r0.optionsButton
            r14 = 40
            r15 = 1109393408(0x42200000, float:40.0)
            r16 = 53
            r17 = 0
            r18 = 1100480512(0x41980000, float:19.0)
            r19 = 1092616192(0x41200000, float:10.0)
            android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r14, r15, r16, r17, r18, r19, r20)
            r5.addView(r8, r9)
            org.telegram.ui.ActionBar.ActionBarMenuItem r5 = r0.optionsButton
            r8 = 2131165652(0x7var_d4, float:1.7945527E38)
            r9 = 2131625173(0x7f0e04d5, float:1.8877546E38)
            java.lang.String r11 = "Forward"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r11, r9)
            r5.addSubItem((int) r2, (int) r8, (java.lang.CharSequence) r9)
            org.telegram.ui.ActionBar.ActionBarMenuItem r5 = r0.optionsButton
            r8 = 2131165696(0x7var_, float:1.7945616E38)
            r9 = 2131626584(0x7f0e0a58, float:1.8880408E38)
            java.lang.String r11 = "ShareFile"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r11, r9)
            r5.addSubItem((int) r3, (int) r8, (java.lang.CharSequence) r9)
            org.telegram.ui.ActionBar.ActionBarMenuItem r5 = r0.optionsButton
            r8 = 2131165672(0x7var_e8, float:1.7945568E38)
            r9 = 2131626621(0x7f0e0a7d, float:1.8880483E38)
            java.lang.String r11 = "ShowInChat"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r11, r9)
            r5.addSubItem((int) r13, (int) r8, (java.lang.CharSequence) r9)
            org.telegram.ui.ActionBar.ActionBarMenuItem r5 = r0.optionsButton
            org.telegram.ui.Components.-$$Lambda$AudioPlayerAlert$L6a3eFAcdzUb5tmijbnBD0GiP9c r8 = new org.telegram.ui.Components.-$$Lambda$AudioPlayerAlert$L6a3eFAcdzUb5tmijbnBD0GiP9c
            r8.<init>()
            r5.setOnClickListener(r8)
            org.telegram.ui.ActionBar.ActionBarMenuItem r5 = r0.optionsButton
            org.telegram.ui.Components.-$$Lambda$AudioPlayerAlert$2le4yIg27yXBU0W-K7geAc_qlH0 r8 = new org.telegram.ui.Components.-$$Lambda$AudioPlayerAlert$2le4yIg27yXBU0W-K7geAc_qlH0
            r8.<init>()
            r5.setDelegate(r8)
            org.telegram.ui.ActionBar.ActionBarMenuItem r5 = r0.optionsButton
            r8 = 2131623980(0x7f0e002c, float:1.8875127E38)
            java.lang.String r9 = "AccDescrMoreOptions"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r9, r8)
            r5.setContentDescription(r8)
            org.telegram.ui.Components.SeekBarView r5 = new org.telegram.ui.Components.SeekBarView
            r5.<init>(r1)
            r0.seekBarView = r5
            org.telegram.ui.Components.SeekBarView r5 = r0.seekBarView
            org.telegram.ui.Components.AudioPlayerAlert$7 r8 = new org.telegram.ui.Components.AudioPlayerAlert$7
            r8.<init>()
            r5.setDelegate(r8)
            org.telegram.ui.Components.SeekBarView r5 = r0.seekBarView
            r5.setReportChanges(r2)
            android.widget.FrameLayout r5 = r0.playerLayout
            org.telegram.ui.Components.SeekBarView r8 = r0.seekBarView
            r14 = -1
            r15 = 1108869120(0x42180000, float:38.0)
            r16 = 51
            r17 = 1082130432(0x40800000, float:4.0)
            r18 = 1114112000(0x42680000, float:58.0)
            r19 = 1082130432(0x40800000, float:4.0)
            android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r14, r15, r16, r17, r18, r19, r20)
            r5.addView(r8, r9)
            org.telegram.ui.Components.LineProgressView r5 = new org.telegram.ui.Components.LineProgressView
            r5.<init>(r1)
            r0.progressView = r5
            org.telegram.ui.Components.LineProgressView r5 = r0.progressView
            r5.setVisibility(r13)
            org.telegram.ui.Components.LineProgressView r5 = r0.progressView
            java.lang.String r8 = "player_progressBackground"
            int r8 = org.telegram.ui.ActionBar.Theme.getColor(r8)
            r5.setBackgroundColor(r8)
            org.telegram.ui.Components.LineProgressView r5 = r0.progressView
            java.lang.String r8 = "player_progress"
            int r8 = org.telegram.ui.ActionBar.Theme.getColor(r8)
            r5.setProgressColor(r8)
            android.widget.FrameLayout r5 = r0.playerLayout
            org.telegram.ui.Components.LineProgressView r8 = r0.progressView
            r15 = 1073741824(0x40000000, float:2.0)
            r17 = 1101004800(0x41a00000, float:20.0)
            r18 = 1117519872(0x429CLASSNAME, float:78.0)
            r19 = 1101004800(0x41a00000, float:20.0)
            android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r14, r15, r16, r17, r18, r19, r20)
            r5.addView(r8, r9)
            org.telegram.ui.ActionBar.SimpleTextView r5 = new org.telegram.ui.ActionBar.SimpleTextView
            r5.<init>(r1)
            r0.timeTextView = r5
            org.telegram.ui.ActionBar.SimpleTextView r5 = r0.timeTextView
            r8 = 12
            r5.setTextSize(r8)
            org.telegram.ui.ActionBar.SimpleTextView r5 = r0.timeTextView
            java.lang.String r8 = "0:00"
            r5.setText(r8)
            org.telegram.ui.ActionBar.SimpleTextView r5 = r0.timeTextView
            int r8 = org.telegram.ui.ActionBar.Theme.getColor(r6)
            r5.setTextColor(r8)
            android.widget.FrameLayout r5 = r0.playerLayout
            org.telegram.ui.ActionBar.SimpleTextView r8 = r0.timeTextView
            r14 = 100
            r15 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r18 = 1119354880(0x42b80000, float:92.0)
            r19 = 0
            android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r14, r15, r16, r17, r18, r19, r20)
            r5.addView(r8, r9)
            android.widget.TextView r5 = new android.widget.TextView
            r5.<init>(r1)
            r0.durationTextView = r5
            android.widget.TextView r5 = r0.durationTextView
            r8 = 1094713344(0x41400000, float:12.0)
            r5.setTextSize(r2, r8)
            android.widget.TextView r5 = r0.durationTextView
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r6)
            r5.setTextColor(r6)
            android.widget.TextView r5 = r0.durationTextView
            r6 = 17
            r5.setGravity(r6)
            android.widget.FrameLayout r5 = r0.playerLayout
            android.widget.TextView r6 = r0.durationTextView
            r14 = -2
            r16 = 53
            r17 = 0
            r18 = 1119092736(0x42b40000, float:90.0)
            r19 = 1101004800(0x41a00000, float:20.0)
            android.widget.FrameLayout$LayoutParams r8 = org.telegram.ui.Components.LayoutHelper.createFrame(r14, r15, r16, r17, r18, r19, r20)
            r5.addView(r6, r8)
            android.widget.ImageView r5 = new android.widget.ImageView
            r5.<init>(r1)
            r0.playbackSpeedButton = r5
            android.widget.ImageView r5 = r0.playbackSpeedButton
            android.widget.ImageView$ScaleType r6 = android.widget.ImageView.ScaleType.CENTER
            r5.setScaleType(r6)
            android.widget.ImageView r5 = r0.playbackSpeedButton
            r6 = 2131165940(0x7var_f4, float:1.7946111E38)
            r5.setImageResource(r6)
            android.widget.ImageView r5 = r0.playbackSpeedButton
            r6 = 2131623995(0x7f0e003b, float:1.8875157E38)
            java.lang.String r8 = "AccDescrPlayerSpeed"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r8, r6)
            r5.setContentDescription(r6)
            float r5 = org.telegram.messenger.AndroidUtilities.density
            r6 = 1077936128(0x40400000, float:3.0)
            int r5 = (r5 > r6 ? 1 : (r5 == r6 ? 0 : -1))
            if (r5 < 0) goto L_0x053f
            android.widget.ImageView r5 = r0.playbackSpeedButton
            r5.setPadding(r7, r2, r7, r7)
        L_0x053f:
            android.widget.FrameLayout r5 = r0.playerLayout
            android.widget.ImageView r8 = r0.playbackSpeedButton
            r14 = 36
            r15 = 1108344832(0x42100000, float:36.0)
            r16 = 53
            r17 = 0
            r18 = 1117782016(0x42a00000, float:80.0)
            r19 = 1101004800(0x41a00000, float:20.0)
            r20 = 0
            android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r14, r15, r16, r17, r18, r19, r20)
            r5.addView(r8, r9)
            android.widget.ImageView r5 = r0.playbackSpeedButton
            org.telegram.ui.Components.-$$Lambda$AudioPlayerAlert$lbtPQBFbXxQUDmU4R4w6uSm1EGI r8 = new org.telegram.ui.Components.-$$Lambda$AudioPlayerAlert$lbtPQBFbXxQUDmU4R4w6uSm1EGI
            r8.<init>()
            r5.setOnClickListener(r8)
            r21.updatePlaybackButton()
            org.telegram.ui.Components.AudioPlayerAlert$8 r5 = new org.telegram.ui.Components.AudioPlayerAlert$8
            r5.<init>(r1)
            android.widget.FrameLayout r8 = r0.playerLayout
            r14 = -1
            r15 = 1115947008(0x42840000, float:66.0)
            r16 = 51
            r18 = 1121189888(0x42d40000, float:106.0)
            r19 = 0
            android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r14, r15, r16, r17, r18, r19, r20)
            r8.addView(r5, r9)
            android.view.View[] r8 = r0.buttons
            org.telegram.ui.ActionBar.ActionBarMenuItem r9 = new org.telegram.ui.ActionBar.ActionBarMenuItem
            r9.<init>(r1, r10, r7, r7)
            r0.shuffleButton = r9
            r8[r7] = r9
            org.telegram.ui.ActionBar.ActionBarMenuItem r8 = r0.shuffleButton
            r8.setLongClickEnabled(r7)
            org.telegram.ui.ActionBar.ActionBarMenuItem r8 = r0.shuffleButton
            r9 = 1092616192(0x41200000, float:10.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            int r9 = -r9
            r8.setAdditionalYOffset(r9)
            org.telegram.ui.ActionBar.ActionBarMenuItem r8 = r0.shuffleButton
            r9 = 51
            r11 = 48
            android.widget.FrameLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r11, (int) r11, (int) r9)
            r5.addView(r8, r12)
            org.telegram.ui.ActionBar.ActionBarMenuItem r8 = r0.shuffleButton
            org.telegram.ui.Components.-$$Lambda$AudioPlayerAlert$vDsQnotDAAYl8VqVL3Roz4WO8JM r12 = new org.telegram.ui.Components.-$$Lambda$AudioPlayerAlert$vDsQnotDAAYl8VqVL3Roz4WO8JM
            r12.<init>()
            r8.setOnClickListener(r12)
            org.telegram.ui.ActionBar.ActionBarMenuItem r8 = r0.shuffleButton
            r12 = 2131626426(0x7f0e09ba, float:1.8880088E38)
            java.lang.String r14 = "ReverseOrder"
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r14, r12)
            android.widget.TextView r8 = r8.addSubItem(r2, r12)
            r12 = 1090519040(0x41000000, float:8.0)
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r12)
            r15 = 1098907648(0x41800000, float:16.0)
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r15)
            r8.setPadding(r14, r7, r15, r7)
            android.graphics.drawable.Drawable[] r14 = r0.playOrderButtons
            android.content.res.Resources r15 = r22.getResources()
            r6 = 2131165710(0x7var_e, float:1.7945645E38)
            android.graphics.drawable.Drawable r6 = r15.getDrawable(r6)
            android.graphics.drawable.Drawable r6 = r6.mutate()
            r14[r7] = r6
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r12)
            r8.setCompoundDrawablePadding(r6)
            android.graphics.drawable.Drawable[] r6 = r0.playOrderButtons
            r6 = r6[r7]
            r8.setCompoundDrawablesWithIntrinsicBounds(r6, r10, r10, r10)
            org.telegram.ui.ActionBar.ActionBarMenuItem r6 = r0.shuffleButton
            r8 = 2131626630(0x7f0e0a86, float:1.8880502E38)
            java.lang.String r14 = "Shuffle"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r14, r8)
            android.widget.TextView r6 = r6.addSubItem(r3, r8)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r12)
            r14 = 1098907648(0x41800000, float:16.0)
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r14)
            r6.setPadding(r8, r7, r14, r7)
            android.graphics.drawable.Drawable[] r8 = r0.playOrderButtons
            android.content.res.Resources r14 = r22.getResources()
            r15 = 2131165792(0x7var_, float:1.7945811E38)
            android.graphics.drawable.Drawable r14 = r14.getDrawable(r15)
            android.graphics.drawable.Drawable r14 = r14.mutate()
            r8[r2] = r14
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r12)
            r6.setCompoundDrawablePadding(r8)
            android.graphics.drawable.Drawable[] r8 = r0.playOrderButtons
            r8 = r8[r2]
            r6.setCompoundDrawablesWithIntrinsicBounds(r8, r10, r10, r10)
            org.telegram.ui.ActionBar.ActionBarMenuItem r6 = r0.shuffleButton
            org.telegram.ui.Components.-$$Lambda$AudioPlayerAlert$HopcJEXECX0ZxGGa4UewdstQ_LI r8 = new org.telegram.ui.Components.-$$Lambda$AudioPlayerAlert$HopcJEXECX0ZxGGa4UewdstQ_LI
            r8.<init>()
            r6.setDelegate(r8)
            android.view.View[] r6 = r0.buttons
            android.widget.ImageView r8 = new android.widget.ImageView
            r8.<init>(r1)
            r6[r2] = r8
            android.widget.ImageView$ScaleType r6 = android.widget.ImageView.ScaleType.CENTER
            r8.setScaleType(r6)
            r6 = 2131165789(0x7var_d, float:1.7945805E38)
            java.lang.String r10 = "player_button"
            int r14 = org.telegram.ui.ActionBar.Theme.getColor(r10)
            java.lang.String r15 = "player_buttonActive"
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r15)
            android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.createSimpleSelectorDrawable(r1, r6, r14, r4)
            r8.setImageDrawable(r4)
            android.widget.FrameLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r11, (int) r11, (int) r9)
            r5.addView(r8, r4)
            org.telegram.ui.Components.-$$Lambda$AudioPlayerAlert$_25s4XwmMXC5vKIS0zNvR6jdxfg r4 = org.telegram.ui.Components.$$Lambda$AudioPlayerAlert$_25s4XwmMXC5vKIS0zNvR6jdxfg.INSTANCE
            r8.setOnClickListener(r4)
            r4 = 2131623996(0x7f0e003c, float:1.887516E38)
            java.lang.String r6 = "AccDescrPrevious"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r6, r4)
            r8.setContentDescription(r4)
            android.view.View[] r4 = r0.buttons
            android.widget.ImageView r6 = new android.widget.ImageView
            r6.<init>(r1)
            r0.playButton = r6
            r4[r3] = r6
            android.widget.ImageView r3 = r0.playButton
            android.widget.ImageView$ScaleType r4 = android.widget.ImageView.ScaleType.CENTER
            r3.setScaleType(r4)
            android.widget.ImageView r3 = r0.playButton
            r4 = 2131165788(0x7var_c, float:1.7945803E38)
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r10)
            int r8 = org.telegram.ui.ActionBar.Theme.getColor(r15)
            android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.createSimpleSelectorDrawable(r1, r4, r6, r8)
            r3.setImageDrawable(r4)
            android.widget.ImageView r3 = r0.playButton
            android.widget.FrameLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r11, (int) r11, (int) r9)
            r5.addView(r3, r4)
            android.widget.ImageView r3 = r0.playButton
            org.telegram.ui.Components.-$$Lambda$AudioPlayerAlert$TTY43GWhuW3FlUWxplw8ac-chYU r4 = org.telegram.ui.Components.$$Lambda$AudioPlayerAlert$TTY43GWhuW3FlUWxplw8acchYU.INSTANCE
            r3.setOnClickListener(r4)
            android.view.View[] r3 = r0.buttons
            r4 = 3
            android.widget.ImageView r6 = new android.widget.ImageView
            r6.<init>(r1)
            r3[r4] = r6
            android.widget.ImageView$ScaleType r3 = android.widget.ImageView.ScaleType.CENTER
            r6.setScaleType(r3)
            r3 = 2131165786(0x7var_a, float:1.7945799E38)
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r10)
            int r8 = org.telegram.ui.ActionBar.Theme.getColor(r15)
            android.graphics.drawable.Drawable r3 = org.telegram.ui.ActionBar.Theme.createSimpleSelectorDrawable(r1, r3, r4, r8)
            r6.setImageDrawable(r3)
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r11, (int) r11, (int) r9)
            r5.addView(r6, r3)
            org.telegram.ui.Components.-$$Lambda$AudioPlayerAlert$4X94PKs6A-0UhkXhWsjRM6iEc3I r3 = org.telegram.ui.Components.$$Lambda$AudioPlayerAlert$4X94PKs6A0UhkXhWsjRM6iEc3I.INSTANCE
            r6.setOnClickListener(r3)
            r3 = 2131625636(0x7f0e06a4, float:1.8878486E38)
            java.lang.String r4 = "Next"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r6.setContentDescription(r3)
            android.view.View[] r3 = r0.buttons
            android.widget.ImageView r4 = new android.widget.ImageView
            r4.<init>(r1)
            r0.repeatButton = r4
            r3[r13] = r4
            android.widget.ImageView r3 = r0.repeatButton
            android.widget.ImageView$ScaleType r4 = android.widget.ImageView.ScaleType.CENTER
            r3.setScaleType(r4)
            android.widget.ImageView r3 = r0.repeatButton
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r12)
            r3.setPadding(r7, r7, r4, r7)
            android.widget.ImageView r3 = r0.repeatButton
            r4 = 50
            android.widget.FrameLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r4, (int) r11, (int) r9)
            r5.addView(r3, r4)
            android.widget.ImageView r3 = r0.repeatButton
            org.telegram.ui.Components.-$$Lambda$AudioPlayerAlert$dXFO6JRbR1ZdMBRfhd2XydhikZ0 r4 = new org.telegram.ui.Components.-$$Lambda$AudioPlayerAlert$dXFO6JRbR1ZdMBRfhd2XydhikZ0
            r4.<init>()
            r3.setOnClickListener(r4)
            org.telegram.ui.Components.AudioPlayerAlert$9 r3 = new org.telegram.ui.Components.AudioPlayerAlert$9
            r3.<init>(r1)
            r0.listView = r3
            org.telegram.ui.Components.RecyclerListView r3 = r0.listView
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r12)
            r3.setPadding(r7, r7, r7, r4)
            org.telegram.ui.Components.RecyclerListView r3 = r0.listView
            r3.setClipToPadding(r7)
            org.telegram.ui.Components.RecyclerListView r3 = r0.listView
            androidx.recyclerview.widget.LinearLayoutManager r4 = new androidx.recyclerview.widget.LinearLayoutManager
            android.content.Context r5 = r21.getContext()
            r4.<init>(r5, r2, r7)
            r0.layoutManager = r4
            r3.setLayoutManager(r4)
            org.telegram.ui.Components.RecyclerListView r2 = r0.listView
            r2.setHorizontalScrollBarEnabled(r7)
            org.telegram.ui.Components.RecyclerListView r2 = r0.listView
            r2.setVerticalScrollBarEnabled(r7)
            android.view.ViewGroup r2 = r0.containerView
            org.telegram.ui.Components.RecyclerListView r3 = r0.listView
            r4 = -1
            android.widget.FrameLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r4, (int) r4, (int) r9)
            r2.addView(r3, r5)
            org.telegram.ui.Components.RecyclerListView r2 = r0.listView
            org.telegram.ui.Components.AudioPlayerAlert$ListAdapter r3 = new org.telegram.ui.Components.AudioPlayerAlert$ListAdapter
            r3.<init>(r1)
            r0.listAdapter = r3
            r2.setAdapter(r3)
            org.telegram.ui.Components.RecyclerListView r1 = r0.listView
            java.lang.String r2 = "dialogScrollGlow"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r1.setGlowColor(r2)
            org.telegram.ui.Components.RecyclerListView r1 = r0.listView
            org.telegram.ui.Components.-$$Lambda$AudioPlayerAlert$48MEuBkln-g5kGMLmNUMeP-Rsutc r2 = org.telegram.ui.Components.$$Lambda$AudioPlayerAlert$48MEuBklng5kGMLmNUMePRsutc.INSTANCE
            r1.setOnItemClickListener((org.telegram.ui.Components.RecyclerListView.OnItemClickListener) r2)
            org.telegram.ui.Components.RecyclerListView r1 = r0.listView
            org.telegram.ui.Components.AudioPlayerAlert$10 r2 = new org.telegram.ui.Components.AudioPlayerAlert$10
            r2.<init>()
            r1.setOnScrollListener(r2)
            org.telegram.messenger.MediaController r1 = org.telegram.messenger.MediaController.getInstance()
            java.util.ArrayList r1 = r1.getPlaylist()
            r0.playlist = r1
            org.telegram.ui.Components.AudioPlayerAlert$ListAdapter r1 = r0.listAdapter
            r1.notifyDataSetChanged()
            android.view.ViewGroup r1 = r0.containerView
            android.widget.FrameLayout r2 = r0.playerLayout
            r3 = 1127350272(0x43320000, float:178.0)
            r4 = -1
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r4, r3)
            r1.addView(r2, r3)
            android.view.ViewGroup r1 = r0.containerView
            android.view.View r2 = r0.shadow2
            r3 = 1077936128(0x40400000, float:3.0)
            android.widget.FrameLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createFrame(r4, r3)
            r1.addView(r2, r5)
            android.view.ViewGroup r1 = r0.containerView
            org.telegram.ui.Components.BackupImageView r2 = r0.placeholderImageView
            r8 = 40
            r9 = 1109393408(0x42200000, float:40.0)
            r10 = 51
            r11 = 1099431936(0x41880000, float:17.0)
            r12 = 1100480512(0x41980000, float:19.0)
            r13 = 0
            r14 = 0
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r8, r9, r10, r11, r12, r13, r14)
            r1.addView(r2, r3)
            android.view.ViewGroup r1 = r0.containerView
            android.view.View r2 = r0.shadow
            r3 = 1077936128(0x40400000, float:3.0)
            r4 = -1
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r4, r3)
            r1.addView(r2, r3)
            android.view.ViewGroup r1 = r0.containerView
            org.telegram.ui.ActionBar.ActionBar r2 = r0.actionBar
            r1.addView(r2)
            r0.updateTitle(r7)
            r21.updateRepeatButton()
            r21.updateShuffleButton()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.AudioPlayerAlert.<init>(android.content.Context):void");
    }

    public /* synthetic */ void lambda$new$0$AudioPlayerAlert(View view) {
        AnimatorSet animatorSet2 = this.animatorSet;
        if (animatorSet2 != null) {
            animatorSet2.cancel();
            this.animatorSet = null;
        }
        this.animatorSet = new AnimatorSet();
        float f = 0.0f;
        if (this.scrollOffsetY <= this.actionBar.getMeasuredHeight()) {
            AnimatorSet animatorSet3 = this.animatorSet;
            Animator[] animatorArr = new Animator[1];
            float[] fArr = new float[1];
            if (!this.isInFullMode) {
                f = 1.0f;
            }
            fArr[0] = f;
            animatorArr[0] = ObjectAnimator.ofFloat(this, "fullAnimationProgress", fArr);
            animatorSet3.playTogether(animatorArr);
        } else {
            AnimatorSet animatorSet4 = this.animatorSet;
            Animator[] animatorArr2 = new Animator[4];
            float[] fArr2 = new float[1];
            fArr2[0] = this.isInFullMode ? 0.0f : 1.0f;
            animatorArr2[0] = ObjectAnimator.ofFloat(this, "fullAnimationProgress", fArr2);
            ActionBar actionBar2 = this.actionBar;
            float[] fArr3 = new float[1];
            fArr3[0] = this.isInFullMode ? 0.0f : 1.0f;
            animatorArr2[1] = ObjectAnimator.ofFloat(actionBar2, "alpha", fArr3);
            View view2 = this.shadow;
            float[] fArr4 = new float[1];
            fArr4[0] = this.isInFullMode ? 0.0f : 1.0f;
            animatorArr2[2] = ObjectAnimator.ofFloat(view2, "alpha", fArr4);
            View view3 = this.shadow2;
            float[] fArr5 = new float[1];
            if (!this.isInFullMode) {
                f = 1.0f;
            }
            fArr5[0] = f;
            animatorArr2[3] = ObjectAnimator.ofFloat(view3, "alpha", fArr5);
            animatorSet4.playTogether(animatorArr2);
        }
        this.animatorSet.setInterpolator(new DecelerateInterpolator());
        this.animatorSet.setDuration(250);
        this.animatorSet.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                if (animator.equals(AudioPlayerAlert.this.animatorSet)) {
                    if (!AudioPlayerAlert.this.isInFullMode) {
                        AudioPlayerAlert.this.listView.setScrollEnabled(true);
                        if (AudioPlayerAlert.this.hasOptions) {
                            AudioPlayerAlert.this.menuItem.setVisibility(4);
                        }
                        AudioPlayerAlert.this.searchItem.setVisibility(0);
                    } else {
                        if (AudioPlayerAlert.this.hasOptions) {
                            AudioPlayerAlert.this.menuItem.setVisibility(0);
                        }
                        AudioPlayerAlert.this.searchItem.setVisibility(4);
                    }
                    AnimatorSet unused = AudioPlayerAlert.this.animatorSet = null;
                }
            }
        });
        this.animatorSet.start();
        if (this.hasOptions) {
            this.menuItem.setVisibility(0);
        }
        this.searchItem.setVisibility(0);
        this.isInFullMode = !this.isInFullMode;
        this.listView.setScrollEnabled(false);
        if (this.isInFullMode) {
            this.shuffleButton.setAdditionalYOffset(-AndroidUtilities.dp(68.0f));
        } else {
            this.shuffleButton.setAdditionalYOffset(-AndroidUtilities.dp(10.0f));
        }
    }

    public /* synthetic */ void lambda$new$1$AudioPlayerAlert(View view) {
        this.optionsButton.toggleSubMenu();
    }

    public /* synthetic */ void lambda$new$2$AudioPlayerAlert(View view) {
        if (MediaController.getInstance().getPlaybackSpeed(true) > 1.0f) {
            MediaController.getInstance().setPlaybackSpeed(true, 1.0f);
        } else {
            MediaController.getInstance().setPlaybackSpeed(true, 1.8f);
        }
        updatePlaybackButton();
    }

    public /* synthetic */ void lambda$new$3$AudioPlayerAlert(View view) {
        this.shuffleButton.toggleSubMenu();
    }

    public /* synthetic */ void lambda$new$4$AudioPlayerAlert(int i) {
        MediaController.getInstance().toggleShuffleMusic(i);
        updateShuffleButton();
        this.listAdapter.notifyDataSetChanged();
    }

    static /* synthetic */ void lambda$new$6(View view) {
        if (!MediaController.getInstance().isDownloadingCurrentMessage()) {
            if (MediaController.getInstance().isMessagePaused()) {
                MediaController.getInstance().playMessage(MediaController.getInstance().getPlayingMessageObject());
            } else {
                MediaController.getInstance().lambda$startAudioAgain$6$MediaController(MediaController.getInstance().getPlayingMessageObject());
            }
        }
    }

    public /* synthetic */ void lambda$new$8$AudioPlayerAlert(View view) {
        SharedConfig.toggleRepeatMode();
        updateRepeatButton();
    }

    static /* synthetic */ void lambda$new$9(View view, int i) {
        if (view instanceof AudioPlayerCell) {
            ((AudioPlayerCell) view).didPressedButton();
        }
    }

    @Keep
    public void setFullAnimationProgress(float f) {
        this.fullAnimationProgress = f;
        this.placeholderImageView.setRoundRadius(AndroidUtilities.dp((1.0f - this.fullAnimationProgress) * 20.0f));
        float f2 = (this.thumbMaxScale * this.fullAnimationProgress) + 1.0f;
        this.placeholderImageView.setScaleX(f2);
        this.placeholderImageView.setScaleY(f2);
        this.placeholderImageView.getTranslationY();
        this.placeholderImageView.setTranslationX(((float) this.thumbMaxX) * this.fullAnimationProgress);
        BackupImageView backupImageView = this.placeholderImageView;
        float f3 = this.startTranslation;
        backupImageView.setTranslationY(f3 + ((this.endTranslation - f3) * this.fullAnimationProgress));
        FrameLayout frameLayout = this.playerLayout;
        float f4 = this.panelStartTranslation;
        frameLayout.setTranslationY(f4 + ((this.panelEndTranslation - f4) * this.fullAnimationProgress));
        View view = this.shadow2;
        float f5 = this.panelStartTranslation;
        view.setTranslationY(f5 + ((this.panelEndTranslation - f5) * this.fullAnimationProgress) + ((float) this.playerLayout.getMeasuredHeight()));
        this.menuItem.setAlpha(this.fullAnimationProgress);
        this.searchItem.setAlpha(1.0f - this.fullAnimationProgress);
        this.avatarContainer.setAlpha(1.0f - this.fullAnimationProgress);
        this.actionBar.getTitleTextView().setAlpha(this.fullAnimationProgress);
        this.actionBar.getSubtitleTextView().setAlpha(this.fullAnimationProgress);
    }

    @Keep
    public float getFullAnimationProgress() {
        return this.fullAnimationProgress;
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
            if (r0 == 0) goto L_0x0175
            org.telegram.ui.LaunchActivity r1 = r5.parentActivity
            if (r1 != 0) goto L_0x0010
            goto L_0x0175
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
            r6.lambda$runLinkRequest$30$LaunchActivity(r1)
            r5.dismiss()
            goto L_0x0175
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
            r2 = 2131626584(0x7f0e0a58, float:1.8880408E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r0, r2)     // Catch:{ Exception -> 0x00f9 }
            android.content.Intent r0 = android.content.Intent.createChooser(r1, r0)     // Catch:{ Exception -> 0x00f9 }
            r1 = 500(0x1f4, float:7.0E-43)
            r6.startActivityForResult(r0, r1)     // Catch:{ Exception -> 0x00f9 }
            goto L_0x0175
        L_0x00c9:
            org.telegram.ui.ActionBar.AlertDialog$Builder r6 = new org.telegram.ui.ActionBar.AlertDialog$Builder     // Catch:{ Exception -> 0x00f9 }
            org.telegram.ui.LaunchActivity r0 = r5.parentActivity     // Catch:{ Exception -> 0x00f9 }
            r6.<init>((android.content.Context) r0)     // Catch:{ Exception -> 0x00f9 }
            java.lang.String r0 = "AppName"
            r2 = 2131624192(0x7f0e0100, float:1.8875557E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r0, r2)     // Catch:{ Exception -> 0x00f9 }
            r6.setTitle(r0)     // Catch:{ Exception -> 0x00f9 }
            java.lang.String r0 = "OK"
            r2 = 2131625846(0x7f0e0776, float:1.8878911E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r0, r2)     // Catch:{ Exception -> 0x00f9 }
            r6.setPositiveButton(r0, r1)     // Catch:{ Exception -> 0x00f9 }
            java.lang.String r0 = "PleaseDownload"
            r1 = 2131626225(0x7f0e08f1, float:1.887968E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r0, r1)     // Catch:{ Exception -> 0x00f9 }
            r6.setMessage(r0)     // Catch:{ Exception -> 0x00f9 }
            r6.show()     // Catch:{ Exception -> 0x00f9 }
            goto L_0x0175
        L_0x00f9:
            r6 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r6)
            goto L_0x0175
        L_0x00ff:
            r3 = 4
            if (r6 != r3) goto L_0x0175
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
            if (r3 == 0) goto L_0x014c
            if (r3 <= 0) goto L_0x0124
            java.lang.String r1 = "user_id"
            r6.putInt(r1, r3)
            goto L_0x0151
        L_0x0124:
            if (r3 >= 0) goto L_0x0151
            int r1 = r5.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
            int r2 = -r3
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            org.telegram.tgnet.TLRPC$Chat r1 = r1.getChat(r2)
            if (r1 == 0) goto L_0x0145
            org.telegram.tgnet.TLRPC$InputChannel r2 = r1.migrated_to
            if (r2 == 0) goto L_0x0145
            java.lang.String r2 = "migrated_to"
            r6.putInt(r2, r3)
            org.telegram.tgnet.TLRPC$InputChannel r1 = r1.migrated_to
            int r1 = r1.channel_id
            int r3 = -r1
        L_0x0145:
            int r1 = -r3
            java.lang.String r2 = "chat_id"
            r6.putInt(r2, r1)
            goto L_0x0151
        L_0x014c:
            java.lang.String r1 = "enc_id"
            r6.putInt(r1, r2)
        L_0x0151:
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
        L_0x0175:
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
                    SendMessagesHelper.getInstance(this.currentAccount).sendMessage(charSequence.toString(), longValue, (MessageObject) null, (TLRPC.WebPage) null, true, (ArrayList<TLRPC.MessageEntity>) null, (TLRPC.ReplyMarkup) null, (HashMap<String, String>) null, true, 0);
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

    private int getCurrentTop() {
        if (this.listView.getChildCount() == 0) {
            return -1000;
        }
        int i = 0;
        View childAt = this.listView.getChildAt(0);
        RecyclerListView.Holder holder = (RecyclerListView.Holder) this.listView.findContainingViewHolder(childAt);
        if (holder == null) {
            return -1000;
        }
        int paddingTop = this.listView.getPaddingTop();
        if (holder.getAdapterPosition() == 0 && childAt.getTop() >= 0) {
            i = childAt.getTop();
        }
        return paddingTop - i;
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
        if (this.listView.getChildCount() > 0) {
            View childAt = this.listView.getChildAt(0);
            RecyclerListView.Holder holder = (RecyclerListView.Holder) this.listView.findContainingViewHolder(childAt);
            int top = childAt.getTop();
            if (top <= 0 || holder == null || holder.getAdapterPosition() != 0) {
                top = 0;
            }
            if (this.searchWas || this.searching) {
                top = 0;
            }
            if (this.scrollOffsetY != top) {
                RecyclerListView recyclerListView = this.listView;
                this.scrollOffsetY = top;
                recyclerListView.setTopGlowOffset(top);
                this.playerLayout.setTranslationY((float) Math.max(this.actionBar.getMeasuredHeight(), this.scrollOffsetY));
                this.placeholderImageView.setTranslationY((float) Math.max(this.actionBar.getMeasuredHeight(), this.scrollOffsetY));
                this.shadow2.setTranslationY((float) (Math.max(this.actionBar.getMeasuredHeight(), this.scrollOffsetY) + this.playerLayout.getMeasuredHeight()));
                this.containerView.invalidate();
                if ((!this.inFullSize || this.scrollOffsetY > this.actionBar.getMeasuredHeight()) && !this.searchWas) {
                    if (this.actionBar.getTag() != null) {
                        AnimatorSet animatorSet2 = this.actionBarAnimation;
                        if (animatorSet2 != null) {
                            animatorSet2.cancel();
                        }
                        this.actionBar.setTag((Object) null);
                        this.actionBarAnimation = new AnimatorSet();
                        this.actionBarAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.actionBar, "alpha", new float[]{0.0f}), ObjectAnimator.ofFloat(this.shadow, "alpha", new float[]{0.0f}), ObjectAnimator.ofFloat(this.shadow2, "alpha", new float[]{0.0f})});
                        this.actionBarAnimation.setDuration(180);
                        this.actionBarAnimation.start();
                    }
                } else if (this.actionBar.getTag() == null) {
                    AnimatorSet animatorSet3 = this.actionBarAnimation;
                    if (animatorSet3 != null) {
                        animatorSet3.cancel();
                    }
                    this.actionBar.setTag(1);
                    this.actionBarAnimation = new AnimatorSet();
                    this.actionBarAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.actionBar, "alpha", new float[]{1.0f}), ObjectAnimator.ofFloat(this.shadow, "alpha", new float[]{1.0f}), ObjectAnimator.ofFloat(this.shadow2, "alpha", new float[]{1.0f})});
                    this.actionBarAnimation.setDuration(180);
                    this.actionBarAnimation.start();
                }
            }
            this.startTranslation = (float) Math.max(this.actionBar.getMeasuredHeight(), this.scrollOffsetY);
            this.panelStartTranslation = (float) Math.max(this.actionBar.getMeasuredHeight(), this.scrollOffsetY);
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

    private void updateShuffleButton() {
        String str;
        String str2 = "player_button";
        if (SharedConfig.shuffleMusic) {
            Drawable mutate = getContext().getResources().getDrawable(NUM).mutate();
            mutate.setColorFilter(new PorterDuffColorFilter(Theme.getColor("player_buttonActive"), PorterDuff.Mode.MULTIPLY));
            this.shuffleButton.setIcon(mutate);
            this.shuffleButton.setContentDescription(LocaleController.getString("Shuffle", NUM));
        } else {
            Drawable mutate2 = getContext().getResources().getDrawable(NUM).mutate();
            if (SharedConfig.playOrderReversed) {
                mutate2.setColorFilter(new PorterDuffColorFilter(Theme.getColor("player_buttonActive"), PorterDuff.Mode.MULTIPLY));
            } else {
                mutate2.setColorFilter(new PorterDuffColorFilter(Theme.getColor(str2), PorterDuff.Mode.MULTIPLY));
            }
            this.shuffleButton.setIcon(mutate2);
            this.shuffleButton.setContentDescription(LocaleController.getString("ReverseOrder", NUM));
        }
        Drawable drawable = this.playOrderButtons[0];
        if (SharedConfig.playOrderReversed) {
            str = "player_buttonActive";
        } else {
            str = str2;
        }
        drawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(str), PorterDuff.Mode.MULTIPLY));
        Drawable drawable2 = this.playOrderButtons[1];
        if (SharedConfig.shuffleMusic) {
            str2 = "player_buttonActive";
        }
        drawable2.setColorFilter(new PorterDuffColorFilter(Theme.getColor(str2), PorterDuff.Mode.MULTIPLY));
    }

    private void updateRepeatButton() {
        int i = SharedConfig.repeatMode;
        if (i == 0) {
            this.repeatButton.setImageResource(NUM);
            this.repeatButton.setTag("player_button");
            this.repeatButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("player_button"), PorterDuff.Mode.MULTIPLY));
            this.repeatButton.setContentDescription(LocaleController.getString("AccDescrRepeatOff", NUM));
        } else if (i == 1) {
            this.repeatButton.setImageResource(NUM);
            this.repeatButton.setTag("player_buttonActive");
            this.repeatButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("player_buttonActive"), PorterDuff.Mode.MULTIPLY));
            this.repeatButton.setContentDescription(LocaleController.getString("AccDescrRepeatList", NUM));
        } else if (i == 2) {
            this.repeatButton.setImageResource(NUM);
            this.repeatButton.setTag("player_buttonActive");
            this.repeatButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("player_buttonActive"), PorterDuff.Mode.MULTIPLY));
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
                this.hasOptions = false;
                this.menuItem.setVisibility(4);
                this.optionsButton.setVisibility(4);
            } else {
                this.hasOptions = true;
                if (!this.actionBar.isSearchFieldVisible()) {
                    this.menuItem.setVisibility(0);
                }
                this.optionsButton.setVisibility(0);
            }
            checkIfMusicDownloaded(playingMessageObject);
            updateProgress(playingMessageObject);
            if (MediaController.getInstance().isMessagePaused()) {
                ImageView imageView = this.playButton;
                imageView.setImageDrawable(Theme.createSimpleSelectorDrawable(imageView.getContext(), NUM, Theme.getColor("player_button"), Theme.getColor("player_buttonActive")));
                this.playButton.setContentDescription(LocaleController.getString("AccActionPlay", NUM));
            } else {
                ImageView imageView2 = this.playButton;
                imageView2.setImageDrawable(Theme.createSimpleSelectorDrawable(imageView2.getContext(), NUM, Theme.getColor("player_button"), Theme.getColor("player_buttonActive")));
                this.playButton.setContentDescription(LocaleController.getString("AccActionPause", NUM));
            }
            String musicTitle = playingMessageObject.getMusicTitle();
            String musicAuthor = playingMessageObject.getMusicAuthor();
            this.titleTextView.setText(musicTitle);
            this.authorTextView.setText(musicAuthor);
            this.actionBar.setTitle(musicTitle);
            this.actionBar.setSubtitle(musicAuthor);
            musicAuthor + " " + musicTitle;
            AudioInfo audioInfo = MediaController.getInstance().getAudioInfo();
            if (audioInfo == null || audioInfo.getCover() == null) {
                String artworkUrl = playingMessageObject.getArtworkUrl(false);
                if (!TextUtils.isEmpty(artworkUrl)) {
                    this.placeholderImageView.setImage(artworkUrl, (String) null, (Drawable) null);
                    this.hasNoCover = 2;
                } else {
                    this.placeholderImageView.setImageDrawable((Drawable) null);
                    this.hasNoCover = 1;
                }
                this.placeholderImageView.invalidate();
            } else {
                this.hasNoCover = 0;
                this.placeholderImageView.setImageBitmap(audioInfo.getCover());
            }
            int duration = playingMessageObject.getDuration();
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
        /* access modifiers changed from: private */
        public Timer searchTimer;

        public ListAdapter(Context context2) {
            this.context = context2;
        }

        public int getItemCount() {
            if (AudioPlayerAlert.this.searchWas) {
                return this.searchResult.size();
            }
            if (AudioPlayerAlert.this.searching) {
                return AudioPlayerAlert.this.playlist.size();
            }
            return AudioPlayerAlert.this.playlist.size() + 1;
        }

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return AudioPlayerAlert.this.searchWas || viewHolder.getAdapterPosition() > 0;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view;
            if (i != 0) {
                view = new AudioPlayerCell(this.context);
            } else {
                view = new View(this.context);
                view.setLayoutParams(new RecyclerView.LayoutParams(-1, AndroidUtilities.dp(178.0f)));
            }
            return new RecyclerListView.Holder(view);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            if (viewHolder.getItemViewType() == 1) {
                AudioPlayerCell audioPlayerCell = (AudioPlayerCell) viewHolder.itemView;
                if (AudioPlayerAlert.this.searchWas) {
                    audioPlayerCell.setMessageObject(this.searchResult.get(i));
                } else if (AudioPlayerAlert.this.searching) {
                    if (SharedConfig.playOrderReversed) {
                        audioPlayerCell.setMessageObject((MessageObject) AudioPlayerAlert.this.playlist.get(i));
                    } else {
                        audioPlayerCell.setMessageObject((MessageObject) AudioPlayerAlert.this.playlist.get((AudioPlayerAlert.this.playlist.size() - i) - 1));
                    }
                } else if (i <= 0) {
                } else {
                    if (SharedConfig.playOrderReversed) {
                        audioPlayerCell.setMessageObject((MessageObject) AudioPlayerAlert.this.playlist.get(i - 1));
                    } else {
                        audioPlayerCell.setMessageObject((MessageObject) AudioPlayerAlert.this.playlist.get(AudioPlayerAlert.this.playlist.size() - i));
                    }
                }
            }
        }

        public int getItemViewType(int i) {
            return (AudioPlayerAlert.this.searchWas || AudioPlayerAlert.this.searching || i != 0) ? 1 : 0;
        }

        public void search(final String str) {
            try {
                if (this.searchTimer != null) {
                    this.searchTimer.cancel();
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
            if (str == null) {
                this.searchResult.clear();
                notifyDataSetChanged();
                return;
            }
            this.searchTimer = new Timer();
            this.searchTimer.schedule(new TimerTask() {
                public void run() {
                    try {
                        ListAdapter.this.searchTimer.cancel();
                        Timer unused = ListAdapter.this.searchTimer = null;
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
                    }
                    ListAdapter.this.processSearch(str);
                }
            }, 200, 300);
        }

        /* access modifiers changed from: private */
        public void processSearch(String str) {
            AndroidUtilities.runOnUIThread(new Runnable(str) {
                private final /* synthetic */ String f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    AudioPlayerAlert.ListAdapter.this.lambda$processSearch$1$AudioPlayerAlert$ListAdapter(this.f$1);
                }
            });
        }

        public /* synthetic */ void lambda$processSearch$1$AudioPlayerAlert$ListAdapter(String str) {
            Utilities.searchQueue.postRunnable(new Runnable(str, new ArrayList(AudioPlayerAlert.this.playlist)) {
                private final /* synthetic */ String f$1;
                private final /* synthetic */ ArrayList f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    AudioPlayerAlert.ListAdapter.this.lambda$null$0$AudioPlayerAlert$ListAdapter(this.f$1, this.f$2);
                }
            });
        }

        public /* synthetic */ void lambda$null$0$AudioPlayerAlert$ListAdapter(String str, ArrayList arrayList) {
            TLRPC.Document document;
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
            String[] strArr = new String[((translitString != null ? 1 : 0) + 1)];
            strArr[0] = lowerCase;
            if (translitString != null) {
                strArr[1] = translitString;
            }
            ArrayList arrayList2 = new ArrayList();
            for (int i = 0; i < arrayList.size(); i++) {
                MessageObject messageObject = (MessageObject) arrayList.get(i);
                int i2 = 0;
                while (true) {
                    if (i2 >= strArr.length) {
                        break;
                    }
                    String str3 = strArr[i2];
                    String documentName = messageObject.getDocumentName();
                    if (!(documentName == null || documentName.length() == 0)) {
                        if (documentName.toLowerCase().contains(str3)) {
                            arrayList2.add(messageObject);
                            break;
                        }
                        if (messageObject.type == 0) {
                            document = messageObject.messageOwner.media.webpage.document;
                        } else {
                            document = messageObject.messageOwner.media.document;
                        }
                        int i3 = 0;
                        while (true) {
                            if (i3 >= document.attributes.size()) {
                                z = false;
                                break;
                            }
                            TLRPC.DocumentAttribute documentAttribute = document.attributes.get(i3);
                            if (documentAttribute instanceof TLRPC.TL_documentAttributeAudio) {
                                String str4 = documentAttribute.performer;
                                boolean contains = str4 != null ? str4.toLowerCase().contains(str3) : false;
                                z = (contains || (str2 = documentAttribute.title) == null) ? contains : str2.toLowerCase().contains(str3);
                            } else {
                                i3++;
                            }
                        }
                        if (z) {
                            arrayList2.add(messageObject);
                            break;
                        }
                    }
                    i2++;
                }
            }
            updateSearchResults(arrayList2);
        }

        private void updateSearchResults(ArrayList<MessageObject> arrayList) {
            AndroidUtilities.runOnUIThread(new Runnable(arrayList) {
                private final /* synthetic */ ArrayList f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    AudioPlayerAlert.ListAdapter.this.lambda$updateSearchResults$2$AudioPlayerAlert$ListAdapter(this.f$1);
                }
            });
        }

        public /* synthetic */ void lambda$updateSearchResults$2$AudioPlayerAlert$ListAdapter(ArrayList arrayList) {
            if (AudioPlayerAlert.this.searching) {
                boolean unused = AudioPlayerAlert.this.searchWas = true;
                this.searchResult = arrayList;
                notifyDataSetChanged();
                AudioPlayerAlert.this.layoutManager.scrollToPosition(0);
            }
        }
    }
}
