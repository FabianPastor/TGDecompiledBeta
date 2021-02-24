package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.style.CharacterStyle;
import android.text.style.URLSpan;
import android.util.LongSparseArray;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.ViewTreeObserver;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.LayoutAnimationController;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScrollerCustom;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.browser.Browser;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction;
import org.telegram.tgnet.TLRPC$ChannelParticipant;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$ChatFull;
import org.telegram.tgnet.TLRPC$InputStickerSet;
import org.telegram.tgnet.TLRPC$KeyboardButton;
import org.telegram.tgnet.TLRPC$Message;
import org.telegram.tgnet.TLRPC$MessageMedia;
import org.telegram.tgnet.TLRPC$PhotoSize;
import org.telegram.tgnet.TLRPC$TL_channelAdminLogEvent;
import org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeHistoryTTL;
import org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeStickerSet;
import org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionParticipantToggleAdmin;
import org.telegram.tgnet.TLRPC$TL_channelAdminLogEventsFilter;
import org.telegram.tgnet.TLRPC$TL_channelParticipantCreator;
import org.telegram.tgnet.TLRPC$TL_channelParticipantsAdmins;
import org.telegram.tgnet.TLRPC$TL_channels_adminLogResults;
import org.telegram.tgnet.TLRPC$TL_channels_channelParticipants;
import org.telegram.tgnet.TLRPC$TL_channels_getAdminLog;
import org.telegram.tgnet.TLRPC$TL_channels_getParticipants;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_inputStickerSetEmpty;
import org.telegram.tgnet.TLRPC$TL_inputStickerSetID;
import org.telegram.tgnet.TLRPC$TL_inputStickerSetShortName;
import org.telegram.tgnet.TLRPC$TL_messageMediaPhoto;
import org.telegram.tgnet.TLRPC$TL_messageMediaWebPage;
import org.telegram.tgnet.TLRPC$TL_messages_stickerSet;
import org.telegram.tgnet.TLRPC$TL_reactionCount;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.tgnet.TLRPC$UserFull;
import org.telegram.tgnet.TLRPC$WebPage;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BackDrawable;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.AvatarPreviewer;
import org.telegram.ui.Cells.ChatActionCell;
import org.telegram.ui.Cells.ChatLoadingCell;
import org.telegram.ui.Cells.ChatMessageCell;
import org.telegram.ui.Cells.ChatUnreadCell;
import org.telegram.ui.Cells.TextSelectionHelper;
import org.telegram.ui.Components.AdminLogFilterAlert;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.ChatAvatarContainer;
import org.telegram.ui.Components.ClearHistoryAlert;
import org.telegram.ui.Components.EmbedBottomSheet;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.PhonebookShareAlert;
import org.telegram.ui.Components.PipRoundVideoView;
import org.telegram.ui.Components.RadialProgressView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.ShareAlert;
import org.telegram.ui.Components.SizeNotifierFrameLayout;
import org.telegram.ui.Components.StickersAlert;
import org.telegram.ui.Components.URLSpanMono;
import org.telegram.ui.Components.URLSpanNoUnderline;
import org.telegram.ui.Components.URLSpanUserMention;
import org.telegram.ui.Components.UndoView;
import org.telegram.ui.PhotoViewer;

public class ChannelAdminLogActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private ArrayList<TLRPC$ChannelParticipant> admins;
    private int allowAnimationIndex;
    /* access modifiers changed from: private */
    public Paint aspectPaint;
    /* access modifiers changed from: private */
    public Path aspectPath;
    /* access modifiers changed from: private */
    public AspectRatioFrameLayout aspectRatioFrameLayout;
    /* access modifiers changed from: private */
    public ChatAvatarContainer avatarContainer;
    private FrameLayout bottomOverlayChat;
    private TextView bottomOverlayChatText;
    private ImageView bottomOverlayImage;
    private ChatActivityAdapter chatAdapter;
    /* access modifiers changed from: private */
    public LinearLayoutManager chatLayoutManager;
    /* access modifiers changed from: private */
    public RecyclerListView chatListView;
    /* access modifiers changed from: private */
    public ArrayList<ChatMessageCell> chatMessageCellsCache = new ArrayList<>();
    /* access modifiers changed from: private */
    public boolean checkTextureViewPosition;
    /* access modifiers changed from: private */
    public SizeNotifierFrameLayout contentView;
    protected TLRPC$Chat currentChat;
    private TLRPC$TL_channelAdminLogEventsFilter currentFilter = null;
    private boolean currentFloatingDateOnScreen;
    /* access modifiers changed from: private */
    public boolean currentFloatingTopIsNotMessage;
    private TextView emptyView;
    /* access modifiers changed from: private */
    public FrameLayout emptyViewContainer;
    /* access modifiers changed from: private */
    public boolean endReached;
    /* access modifiers changed from: private */
    public AnimatorSet floatingDateAnimation;
    /* access modifiers changed from: private */
    public ChatActionCell floatingDateView;
    private boolean loading;
    /* access modifiers changed from: private */
    public int loadsCount;
    protected ArrayList<MessageObject> messages = new ArrayList<>();
    private HashMap<String, ArrayList<MessageObject>> messagesByDays = new HashMap<>();
    private LongSparseArray<MessageObject> messagesDict = new LongSparseArray<>();
    private int[] mid = {2};
    private long minEventId;
    private boolean paused = true;
    private RadialProgressView progressBar;
    /* access modifiers changed from: private */
    public FrameLayout progressView;
    private View progressView2;
    /* access modifiers changed from: private */
    public PhotoViewer.PhotoViewerProvider provider = new PhotoViewer.EmptyPhotoViewerProvider() {
        /* JADX WARNING: Code restructure failed: missing block: B:13:0x003f, code lost:
            r7 = (org.telegram.ui.Cells.ChatActionCell) r6;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:6:0x0023, code lost:
            r7 = (org.telegram.ui.Cells.ChatMessageCell) r6;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public org.telegram.ui.PhotoViewer.PlaceProviderObject getPlaceForPhoto(org.telegram.messenger.MessageObject r17, org.telegram.tgnet.TLRPC$FileLocation r18, int r19, boolean r20) {
            /*
                r16 = this;
                r0 = r16
                r1 = r18
                org.telegram.ui.ChannelAdminLogActivity r2 = org.telegram.ui.ChannelAdminLogActivity.this
                org.telegram.ui.Components.RecyclerListView r2 = r2.chatListView
                int r2 = r2.getChildCount()
                r3 = 0
                r4 = 0
            L_0x0010:
                r5 = 0
                if (r4 >= r2) goto L_0x00c5
                org.telegram.ui.ChannelAdminLogActivity r6 = org.telegram.ui.ChannelAdminLogActivity.this
                org.telegram.ui.Components.RecyclerListView r6 = r6.chatListView
                android.view.View r6 = r6.getChildAt(r4)
                boolean r7 = r6 instanceof org.telegram.ui.Cells.ChatMessageCell
                if (r7 == 0) goto L_0x003b
                if (r17 == 0) goto L_0x0088
                r7 = r6
                org.telegram.ui.Cells.ChatMessageCell r7 = (org.telegram.ui.Cells.ChatMessageCell) r7
                org.telegram.messenger.MessageObject r8 = r7.getMessageObject()
                if (r8 == 0) goto L_0x0088
                int r8 = r8.getId()
                int r9 = r17.getId()
                if (r8 != r9) goto L_0x0088
                org.telegram.messenger.ImageReceiver r5 = r7.getPhotoImage()
                goto L_0x0088
            L_0x003b:
                boolean r7 = r6 instanceof org.telegram.ui.Cells.ChatActionCell
                if (r7 == 0) goto L_0x0088
                r7 = r6
                org.telegram.ui.Cells.ChatActionCell r7 = (org.telegram.ui.Cells.ChatActionCell) r7
                org.telegram.messenger.MessageObject r8 = r7.getMessageObject()
                if (r8 == 0) goto L_0x0088
                if (r17 == 0) goto L_0x0059
                int r8 = r8.getId()
                int r9 = r17.getId()
                if (r8 != r9) goto L_0x0088
                org.telegram.messenger.ImageReceiver r5 = r7.getPhotoImage()
                goto L_0x0088
            L_0x0059:
                if (r1 == 0) goto L_0x0088
                java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r9 = r8.photoThumbs
                if (r9 == 0) goto L_0x0088
                r9 = 0
            L_0x0060:
                java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r10 = r8.photoThumbs
                int r10 = r10.size()
                if (r9 >= r10) goto L_0x0088
                java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r10 = r8.photoThumbs
                java.lang.Object r10 = r10.get(r9)
                org.telegram.tgnet.TLRPC$PhotoSize r10 = (org.telegram.tgnet.TLRPC$PhotoSize) r10
                org.telegram.tgnet.TLRPC$FileLocation r10 = r10.location
                long r11 = r10.volume_id
                long r13 = r1.volume_id
                int r15 = (r11 > r13 ? 1 : (r11 == r13 ? 0 : -1))
                if (r15 != 0) goto L_0x0085
                int r10 = r10.local_id
                int r11 = r1.local_id
                if (r10 != r11) goto L_0x0085
                org.telegram.messenger.ImageReceiver r5 = r7.getPhotoImage()
                goto L_0x0088
            L_0x0085:
                int r9 = r9 + 1
                goto L_0x0060
            L_0x0088:
                if (r5 == 0) goto L_0x00c1
                r1 = 2
                int[] r1 = new int[r1]
                r6.getLocationInWindow(r1)
                org.telegram.ui.PhotoViewer$PlaceProviderObject r2 = new org.telegram.ui.PhotoViewer$PlaceProviderObject
                r2.<init>()
                r4 = r1[r3]
                r2.viewX = r4
                r4 = 1
                r1 = r1[r4]
                int r6 = android.os.Build.VERSION.SDK_INT
                r7 = 21
                if (r6 < r7) goto L_0x00a3
                goto L_0x00a5
            L_0x00a3:
                int r3 = org.telegram.messenger.AndroidUtilities.statusBarHeight
            L_0x00a5:
                int r1 = r1 - r3
                r2.viewY = r1
                org.telegram.ui.ChannelAdminLogActivity r1 = org.telegram.ui.ChannelAdminLogActivity.this
                org.telegram.ui.Components.RecyclerListView r1 = r1.chatListView
                r2.parentView = r1
                r2.imageReceiver = r5
                org.telegram.messenger.ImageReceiver$BitmapHolder r1 = r5.getBitmapSafe()
                r2.thumb = r1
                int[] r1 = r5.getRoundRadius()
                r2.radius = r1
                r2.isEvent = r4
                return r2
            L_0x00c1:
                int r4 = r4 + 1
                goto L_0x0010
            L_0x00c5:
                return r5
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChannelAdminLogActivity.AnonymousClass1.getPlaceForPhoto(org.telegram.messenger.MessageObject, org.telegram.tgnet.TLRPC$FileLocation, int, boolean):org.telegram.ui.PhotoViewer$PlaceProviderObject");
        }
    };
    /* access modifiers changed from: private */
    public FrameLayout roundVideoContainer;
    /* access modifiers changed from: private */
    public int scrollToOffsetOnRecreate = 0;
    /* access modifiers changed from: private */
    public int scrollToPositionOnRecreate = -1;
    /* access modifiers changed from: private */
    public boolean scrollingFloatingDate;
    private ImageView searchCalendarButton;
    private FrameLayout searchContainer;
    private SimpleTextView searchCountText;
    private ActionBarMenuItem searchItem;
    /* access modifiers changed from: private */
    public String searchQuery = "";
    /* access modifiers changed from: private */
    public boolean searchWas;
    private SparseArray<TLRPC$User> selectedAdmins;
    private MessageObject selectedObject;
    /* access modifiers changed from: private */
    public UndoView undoView;
    private TextureView videoTextureView;
    private boolean wasPaused = false;

    static /* synthetic */ boolean lambda$createView$2(View view, MotionEvent motionEvent) {
        return true;
    }

    /* access modifiers changed from: private */
    public void updateBottomOverlay() {
    }

    public ChannelAdminLogActivity(TLRPC$Chat tLRPC$Chat) {
        this.currentChat = tLRPC$Chat;
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiDidLoad);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagePlayingDidStart);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagePlayingPlayStateChanged);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagePlayingDidReset);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagePlayingProgressDidChanged);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didSetNewWallpapper);
        loadMessages(true);
        loadAdmins();
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingDidStart);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingPlayStateChanged);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingDidReset);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingProgressDidChanged);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didSetNewWallpapper);
        getNotificationCenter().onAnimationFinish(this.allowAnimationIndex);
    }

    private void updateEmptyPlaceholder() {
        if (this.emptyView != null) {
            if (!TextUtils.isEmpty(this.searchQuery)) {
                this.emptyView.setPadding(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(5.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(5.0f));
                this.emptyView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("EventLogEmptyTextSearch", NUM, this.searchQuery)));
            } else if (this.selectedAdmins == null && this.currentFilter == null) {
                this.emptyView.setPadding(AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f));
                if (this.currentChat.megagroup) {
                    this.emptyView.setText(AndroidUtilities.replaceTags(LocaleController.getString("EventLogEmpty", NUM)));
                } else {
                    this.emptyView.setText(AndroidUtilities.replaceTags(LocaleController.getString("EventLogEmptyChannel", NUM)));
                }
            } else {
                this.emptyView.setPadding(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(5.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(5.0f));
                this.emptyView.setText(AndroidUtilities.replaceTags(LocaleController.getString("EventLogEmptySearch", NUM)));
            }
        }
    }

    /* access modifiers changed from: private */
    public void loadMessages(boolean z) {
        ChatActivityAdapter chatActivityAdapter;
        if (!this.loading) {
            if (z) {
                this.minEventId = Long.MAX_VALUE;
                FrameLayout frameLayout = this.progressView;
                if (frameLayout != null) {
                    frameLayout.setVisibility(0);
                    this.emptyViewContainer.setVisibility(4);
                    this.chatListView.setEmptyView((View) null);
                }
                this.messagesDict.clear();
                this.messages.clear();
                this.messagesByDays.clear();
            }
            this.loading = true;
            TLRPC$TL_channels_getAdminLog tLRPC$TL_channels_getAdminLog = new TLRPC$TL_channels_getAdminLog();
            tLRPC$TL_channels_getAdminLog.channel = MessagesController.getInputChannel(this.currentChat);
            tLRPC$TL_channels_getAdminLog.q = this.searchQuery;
            tLRPC$TL_channels_getAdminLog.limit = 50;
            if (z || this.messages.isEmpty()) {
                tLRPC$TL_channels_getAdminLog.max_id = 0;
            } else {
                tLRPC$TL_channels_getAdminLog.max_id = this.minEventId;
            }
            tLRPC$TL_channels_getAdminLog.min_id = 0;
            TLRPC$TL_channelAdminLogEventsFilter tLRPC$TL_channelAdminLogEventsFilter = this.currentFilter;
            if (tLRPC$TL_channelAdminLogEventsFilter != null) {
                tLRPC$TL_channels_getAdminLog.flags = 1 | tLRPC$TL_channels_getAdminLog.flags;
                tLRPC$TL_channels_getAdminLog.events_filter = tLRPC$TL_channelAdminLogEventsFilter;
            }
            if (this.selectedAdmins != null) {
                tLRPC$TL_channels_getAdminLog.flags |= 2;
                for (int i = 0; i < this.selectedAdmins.size(); i++) {
                    tLRPC$TL_channels_getAdminLog.admins.add(MessagesController.getInstance(this.currentAccount).getInputUser(this.selectedAdmins.valueAt(i)));
                }
            }
            updateEmptyPlaceholder();
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_channels_getAdminLog, new RequestDelegate() {
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    ChannelAdminLogActivity.this.lambda$loadMessages$1$ChannelAdminLogActivity(tLObject, tLRPC$TL_error);
                }
            });
            if (z && (chatActivityAdapter = this.chatAdapter) != null) {
                chatActivityAdapter.notifyDataSetChanged();
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$loadMessages$1 */
    public /* synthetic */ void lambda$loadMessages$1$ChannelAdminLogActivity(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLObject != null) {
            AndroidUtilities.runOnUIThread(new Runnable((TLRPC$TL_channels_adminLogResults) tLObject) {
                public final /* synthetic */ TLRPC$TL_channels_adminLogResults f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    ChannelAdminLogActivity.this.lambda$null$0$ChannelAdminLogActivity(this.f$1);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$0 */
    public /* synthetic */ void lambda$null$0$ChannelAdminLogActivity(TLRPC$TL_channels_adminLogResults tLRPC$TL_channels_adminLogResults) {
        int i;
        int i2 = 0;
        MessagesController.getInstance(this.currentAccount).putUsers(tLRPC$TL_channels_adminLogResults.users, false);
        MessagesController.getInstance(this.currentAccount).putChats(tLRPC$TL_channels_adminLogResults.chats, false);
        int size = this.messages.size();
        boolean z = false;
        for (int i3 = 0; i3 < tLRPC$TL_channels_adminLogResults.events.size(); i3++) {
            TLRPC$TL_channelAdminLogEvent tLRPC$TL_channelAdminLogEvent = tLRPC$TL_channels_adminLogResults.events.get(i3);
            if (this.messagesDict.indexOfKey(tLRPC$TL_channelAdminLogEvent.id) < 0) {
                TLRPC$ChannelAdminLogEventAction tLRPC$ChannelAdminLogEventAction = tLRPC$TL_channelAdminLogEvent.action;
                if (tLRPC$ChannelAdminLogEventAction instanceof TLRPC$TL_channelAdminLogEventActionParticipantToggleAdmin) {
                    TLRPC$TL_channelAdminLogEventActionParticipantToggleAdmin tLRPC$TL_channelAdminLogEventActionParticipantToggleAdmin = (TLRPC$TL_channelAdminLogEventActionParticipantToggleAdmin) tLRPC$ChannelAdminLogEventAction;
                    if ((tLRPC$TL_channelAdminLogEventActionParticipantToggleAdmin.prev_participant instanceof TLRPC$TL_channelParticipantCreator) && !(tLRPC$TL_channelAdminLogEventActionParticipantToggleAdmin.new_participant instanceof TLRPC$TL_channelParticipantCreator)) {
                    }
                }
                this.minEventId = Math.min(this.minEventId, tLRPC$TL_channelAdminLogEvent.id);
                MessageObject messageObject = new MessageObject(this.currentAccount, tLRPC$TL_channelAdminLogEvent, this.messages, this.messagesByDays, this.currentChat, this.mid);
                if (messageObject.contentType >= 0) {
                    this.messagesDict.put(tLRPC$TL_channelAdminLogEvent.id, messageObject);
                }
                z = true;
            }
        }
        int size2 = this.messages.size() - size;
        this.loading = false;
        if (!z) {
            this.endReached = true;
        }
        this.progressView.setVisibility(4);
        this.chatListView.setEmptyView(this.emptyViewContainer);
        if (size2 != 0) {
            if (this.endReached) {
                this.chatAdapter.notifyItemRangeChanged(0, 2);
                i = 1;
            } else {
                i = 0;
            }
            int findLastVisibleItemPosition = this.chatLayoutManager.findLastVisibleItemPosition();
            View findViewByPosition = this.chatLayoutManager.findViewByPosition(findLastVisibleItemPosition);
            if (findViewByPosition != null) {
                i2 = findViewByPosition.getTop();
            }
            int paddingTop = i2 - this.chatListView.getPaddingTop();
            if (size2 - i > 0) {
                int i4 = (i ^ 1) + 1;
                this.chatAdapter.notifyItemChanged(i4);
                this.chatAdapter.notifyItemRangeInserted(i4, size2 - i);
            }
            if (findLastVisibleItemPosition != -1) {
                this.chatLayoutManager.scrollToPositionWithOffset((findLastVisibleItemPosition + size2) - i, paddingTop);
            }
        } else if (this.endReached) {
            this.chatAdapter.notifyItemRemoved(0);
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0044, code lost:
        r8 = (org.telegram.ui.Cells.ChatMessageCell) r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:43:0x00a6, code lost:
        r8 = (org.telegram.ui.Cells.ChatMessageCell) r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:65:0x011e, code lost:
        r8 = (org.telegram.ui.Cells.ChatMessageCell) r8;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void didReceivedNotification(int r6, int r7, java.lang.Object... r8) {
        /*
            r5 = this;
            int r7 = org.telegram.messenger.NotificationCenter.emojiDidLoad
            if (r6 != r7) goto L_0x000d
            org.telegram.ui.Components.RecyclerListView r6 = r5.chatListView
            if (r6 == 0) goto L_0x014d
            r6.invalidateViews()
            goto L_0x014d
        L_0x000d:
            int r7 = org.telegram.messenger.NotificationCenter.messagePlayingDidStart
            r0 = 1
            r1 = 0
            if (r6 != r7) goto L_0x0080
            r6 = r8[r1]
            org.telegram.messenger.MessageObject r6 = (org.telegram.messenger.MessageObject) r6
            boolean r6 = r6.isRoundVideo()
            if (r6 == 0) goto L_0x002f
            org.telegram.messenger.MediaController r6 = org.telegram.messenger.MediaController.getInstance()
            android.view.TextureView r7 = r5.createTextureView(r0)
            com.google.android.exoplayer2.ui.AspectRatioFrameLayout r8 = r5.aspectRatioFrameLayout
            android.widget.FrameLayout r2 = r5.roundVideoContainer
            r6.setTextureView(r7, r8, r2, r0)
            r5.updateTextureViewPosition()
        L_0x002f:
            org.telegram.ui.Components.RecyclerListView r6 = r5.chatListView
            if (r6 == 0) goto L_0x014d
            int r6 = r6.getChildCount()
            r7 = 0
        L_0x0038:
            if (r7 >= r6) goto L_0x014d
            org.telegram.ui.Components.RecyclerListView r8 = r5.chatListView
            android.view.View r8 = r8.getChildAt(r7)
            boolean r2 = r8 instanceof org.telegram.ui.Cells.ChatMessageCell
            if (r2 == 0) goto L_0x007d
            org.telegram.ui.Cells.ChatMessageCell r8 = (org.telegram.ui.Cells.ChatMessageCell) r8
            org.telegram.messenger.MessageObject r2 = r8.getMessageObject()
            if (r2 == 0) goto L_0x007d
            boolean r3 = r2.isVoice()
            if (r3 != 0) goto L_0x007a
            boolean r3 = r2.isMusic()
            if (r3 == 0) goto L_0x0059
            goto L_0x007a
        L_0x0059:
            boolean r3 = r2.isRoundVideo()
            if (r3 == 0) goto L_0x007d
            r8.checkVideoPlayback(r1)
            org.telegram.messenger.MediaController r3 = org.telegram.messenger.MediaController.getInstance()
            boolean r3 = r3.isPlayingMessage(r2)
            if (r3 != 0) goto L_0x007d
            float r3 = r2.audioProgress
            r4 = 0
            int r3 = (r3 > r4 ? 1 : (r3 == r4 ? 0 : -1))
            if (r3 == 0) goto L_0x007d
            r2.resetPlayingProgress()
            r8.invalidate()
            goto L_0x007d
        L_0x007a:
            r8.updateButtonState(r1, r0, r1)
        L_0x007d:
            int r7 = r7 + 1
            goto L_0x0038
        L_0x0080:
            int r7 = org.telegram.messenger.NotificationCenter.messagePlayingDidReset
            if (r6 == r7) goto L_0x0109
            int r7 = org.telegram.messenger.NotificationCenter.messagePlayingPlayStateChanged
            if (r6 != r7) goto L_0x008a
            goto L_0x0109
        L_0x008a:
            int r7 = org.telegram.messenger.NotificationCenter.messagePlayingProgressDidChanged
            if (r6 != r7) goto L_0x00d6
            r6 = r8[r1]
            java.lang.Integer r6 = (java.lang.Integer) r6
            org.telegram.ui.Components.RecyclerListView r7 = r5.chatListView
            if (r7 == 0) goto L_0x014d
            int r7 = r7.getChildCount()
        L_0x009a:
            if (r1 >= r7) goto L_0x014d
            org.telegram.ui.Components.RecyclerListView r8 = r5.chatListView
            android.view.View r8 = r8.getChildAt(r1)
            boolean r0 = r8 instanceof org.telegram.ui.Cells.ChatMessageCell
            if (r0 == 0) goto L_0x00d3
            org.telegram.ui.Cells.ChatMessageCell r8 = (org.telegram.ui.Cells.ChatMessageCell) r8
            org.telegram.messenger.MessageObject r0 = r8.getMessageObject()
            if (r0 == 0) goto L_0x00d3
            int r2 = r0.getId()
            int r3 = r6.intValue()
            if (r2 != r3) goto L_0x00d3
            org.telegram.messenger.MediaController r6 = org.telegram.messenger.MediaController.getInstance()
            org.telegram.messenger.MessageObject r6 = r6.getPlayingMessageObject()
            if (r6 == 0) goto L_0x014d
            float r7 = r6.audioProgress
            r0.audioProgress = r7
            int r7 = r6.audioProgressSec
            r0.audioProgressSec = r7
            int r6 = r6.audioPlayerDuration
            r0.audioPlayerDuration = r6
            r8.updatePlayingMessageProgress()
            goto L_0x014d
        L_0x00d3:
            int r1 = r1 + 1
            goto L_0x009a
        L_0x00d6:
            int r7 = org.telegram.messenger.NotificationCenter.didSetNewWallpapper
            if (r6 != r7) goto L_0x014d
            android.view.View r6 = r5.fragmentView
            if (r6 == 0) goto L_0x014d
            org.telegram.ui.Components.SizeNotifierFrameLayout r6 = r5.contentView
            android.graphics.drawable.Drawable r7 = org.telegram.ui.ActionBar.Theme.getCachedWallpaper()
            boolean r8 = org.telegram.ui.ActionBar.Theme.isWallpaperMotion()
            r6.setBackgroundImage(r7, r8)
            android.view.View r6 = r5.progressView2
            android.graphics.drawable.Drawable r6 = r6.getBackground()
            android.graphics.PorterDuffColorFilter r7 = org.telegram.ui.ActionBar.Theme.colorFilter
            r6.setColorFilter(r7)
            android.widget.TextView r6 = r5.emptyView
            if (r6 == 0) goto L_0x0103
            android.graphics.drawable.Drawable r6 = r6.getBackground()
            android.graphics.PorterDuffColorFilter r7 = org.telegram.ui.ActionBar.Theme.colorFilter
            r6.setColorFilter(r7)
        L_0x0103:
            org.telegram.ui.Components.RecyclerListView r6 = r5.chatListView
            r6.invalidateViews()
            goto L_0x014d
        L_0x0109:
            org.telegram.ui.Components.RecyclerListView r6 = r5.chatListView
            if (r6 == 0) goto L_0x014d
            int r6 = r6.getChildCount()
            r7 = 0
        L_0x0112:
            if (r7 >= r6) goto L_0x014d
            org.telegram.ui.Components.RecyclerListView r8 = r5.chatListView
            android.view.View r8 = r8.getChildAt(r7)
            boolean r2 = r8 instanceof org.telegram.ui.Cells.ChatMessageCell
            if (r2 == 0) goto L_0x014a
            org.telegram.ui.Cells.ChatMessageCell r8 = (org.telegram.ui.Cells.ChatMessageCell) r8
            org.telegram.messenger.MessageObject r2 = r8.getMessageObject()
            if (r2 == 0) goto L_0x014a
            boolean r3 = r2.isVoice()
            if (r3 != 0) goto L_0x0147
            boolean r3 = r2.isMusic()
            if (r3 == 0) goto L_0x0133
            goto L_0x0147
        L_0x0133:
            boolean r3 = r2.isRoundVideo()
            if (r3 == 0) goto L_0x014a
            org.telegram.messenger.MediaController r3 = org.telegram.messenger.MediaController.getInstance()
            boolean r2 = r3.isPlayingMessage(r2)
            if (r2 != 0) goto L_0x014a
            r8.checkVideoPlayback(r0)
            goto L_0x014a
        L_0x0147:
            r8.updateButtonState(r1, r0, r1)
        L_0x014a:
            int r7 = r7 + 1
            goto L_0x0112
        L_0x014d:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChannelAdminLogActivity.didReceivedNotification(int, int, java.lang.Object[]):void");
    }

    public View createView(Context context) {
        Context context2 = context;
        if (this.chatMessageCellsCache.isEmpty()) {
            for (int i = 0; i < 8; i++) {
                this.chatMessageCellsCache.add(new ChatMessageCell(context2));
            }
        }
        this.searchWas = false;
        this.hasOwnBackground = true;
        Theme.createChatResources(context2, false);
        this.actionBar.setAddToContainer(false);
        this.actionBar.setOccupyStatusBar(Build.VERSION.SDK_INT >= 21 && !AndroidUtilities.isTablet());
        this.actionBar.setBackButtonDrawable(new BackDrawable(false));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    ChannelAdminLogActivity.this.finishFragment();
                }
            }
        });
        ChatAvatarContainer chatAvatarContainer = new ChatAvatarContainer(context2, (ChatActivity) null, false);
        this.avatarContainer = chatAvatarContainer;
        chatAvatarContainer.setOccupyStatusBar(!AndroidUtilities.isTablet());
        this.actionBar.addView(this.avatarContainer, 0, LayoutHelper.createFrame(-2, -1.0f, 51, 56.0f, 0.0f, 40.0f, 0.0f));
        ActionBarMenuItem addItem = this.actionBar.createMenu().addItem(0, NUM);
        addItem.setIsSearchField(true);
        addItem.setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener() {
            public void onSearchCollapse() {
                String unused = ChannelAdminLogActivity.this.searchQuery = "";
                ChannelAdminLogActivity.this.avatarContainer.setVisibility(0);
                if (ChannelAdminLogActivity.this.searchWas) {
                    boolean unused2 = ChannelAdminLogActivity.this.searchWas = false;
                    ChannelAdminLogActivity.this.loadMessages(true);
                }
                ChannelAdminLogActivity.this.updateBottomOverlay();
            }

            public void onSearchExpand() {
                ChannelAdminLogActivity.this.avatarContainer.setVisibility(8);
                ChannelAdminLogActivity.this.updateBottomOverlay();
            }

            public void onSearchPressed(EditText editText) {
                boolean unused = ChannelAdminLogActivity.this.searchWas = true;
                String unused2 = ChannelAdminLogActivity.this.searchQuery = editText.getText().toString();
                ChannelAdminLogActivity.this.loadMessages(true);
            }
        });
        this.searchItem = addItem;
        addItem.setSearchFieldHint(LocaleController.getString("Search", NUM));
        this.avatarContainer.setEnabled(false);
        this.avatarContainer.setTitle(this.currentChat.title);
        this.avatarContainer.setSubtitle(LocaleController.getString("EventLogAllEvents", NUM));
        this.avatarContainer.setChatAvatar(this.currentChat);
        AnonymousClass4 r4 = new SizeNotifierFrameLayout(context2) {
            /* access modifiers changed from: protected */
            public void onAttachedToWindow() {
                super.onAttachedToWindow();
                MessageObject playingMessageObject = MediaController.getInstance().getPlayingMessageObject();
                if (playingMessageObject != null && playingMessageObject.isRoundVideo() && playingMessageObject.eventId != 0 && playingMessageObject.getDialogId() == ((long) (-ChannelAdminLogActivity.this.currentChat.id))) {
                    MediaController.getInstance().setTextureView(ChannelAdminLogActivity.this.createTextureView(false), ChannelAdminLogActivity.this.aspectRatioFrameLayout, ChannelAdminLogActivity.this.roundVideoContainer, true);
                }
            }

            /* access modifiers changed from: protected */
            public boolean drawChild(Canvas canvas, View view, long j) {
                boolean drawChild = super.drawChild(canvas, view, j);
                if (view == ChannelAdminLogActivity.this.actionBar && ChannelAdminLogActivity.this.parentLayout != null) {
                    ChannelAdminLogActivity.this.parentLayout.drawHeaderShadow(canvas, ChannelAdminLogActivity.this.actionBar.getVisibility() == 0 ? ChannelAdminLogActivity.this.actionBar.getMeasuredHeight() : 0);
                }
                return drawChild;
            }

            /* access modifiers changed from: protected */
            public boolean isActionBarVisible() {
                return ChannelAdminLogActivity.this.actionBar.getVisibility() == 0;
            }

            /* access modifiers changed from: protected */
            public void onMeasure(int i, int i2) {
                int size = View.MeasureSpec.getSize(i);
                int size2 = View.MeasureSpec.getSize(i2);
                setMeasuredDimension(size, size2);
                int paddingTop = size2 - getPaddingTop();
                measureChildWithMargins(ChannelAdminLogActivity.this.actionBar, i, 0, i2, 0);
                int measuredHeight = ChannelAdminLogActivity.this.actionBar.getMeasuredHeight();
                if (ChannelAdminLogActivity.this.actionBar.getVisibility() == 0) {
                    paddingTop -= measuredHeight;
                }
                int childCount = getChildCount();
                for (int i3 = 0; i3 < childCount; i3++) {
                    View childAt = getChildAt(i3);
                    if (!(childAt == null || childAt.getVisibility() == 8 || childAt == ChannelAdminLogActivity.this.actionBar)) {
                        if (childAt == ChannelAdminLogActivity.this.chatListView || childAt == ChannelAdminLogActivity.this.progressView) {
                            childAt.measure(View.MeasureSpec.makeMeasureSpec(size, NUM), View.MeasureSpec.makeMeasureSpec(Math.max(AndroidUtilities.dp(10.0f), paddingTop - AndroidUtilities.dp(50.0f)), NUM));
                        } else if (childAt == ChannelAdminLogActivity.this.emptyViewContainer) {
                            childAt.measure(View.MeasureSpec.makeMeasureSpec(size, NUM), View.MeasureSpec.makeMeasureSpec(paddingTop, NUM));
                        } else {
                            measureChildWithMargins(childAt, i, 0, i2, 0);
                        }
                    }
                }
            }

            /* access modifiers changed from: protected */
            /* JADX WARNING: Removed duplicated region for block: B:17:0x004e  */
            /* JADX WARNING: Removed duplicated region for block: B:28:0x0086  */
            /* JADX WARNING: Removed duplicated region for block: B:32:0x009a  */
            /* JADX WARNING: Removed duplicated region for block: B:37:0x00bc  */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void onLayout(boolean r10, int r11, int r12, int r13, int r14) {
                /*
                    r9 = this;
                    int r10 = r9.getChildCount()
                    r0 = 0
                    r1 = 0
                L_0x0006:
                    if (r1 >= r10) goto L_0x00d2
                    android.view.View r2 = r9.getChildAt(r1)
                    int r3 = r2.getVisibility()
                    r4 = 8
                    if (r3 != r4) goto L_0x0016
                    goto L_0x00ce
                L_0x0016:
                    android.view.ViewGroup$LayoutParams r3 = r2.getLayoutParams()
                    android.widget.FrameLayout$LayoutParams r3 = (android.widget.FrameLayout.LayoutParams) r3
                    int r4 = r2.getMeasuredWidth()
                    int r5 = r2.getMeasuredHeight()
                    int r6 = r3.gravity
                    r7 = -1
                    if (r6 != r7) goto L_0x002b
                    r6 = 51
                L_0x002b:
                    r7 = r6 & 7
                    r6 = r6 & 112(0x70, float:1.57E-43)
                    r7 = r7 & 7
                    r8 = 1
                    if (r7 == r8) goto L_0x003f
                    r8 = 5
                    if (r7 == r8) goto L_0x003a
                    int r7 = r3.leftMargin
                    goto L_0x004a
                L_0x003a:
                    int r7 = r13 - r4
                    int r8 = r3.rightMargin
                    goto L_0x0049
                L_0x003f:
                    int r7 = r13 - r11
                    int r7 = r7 - r4
                    int r7 = r7 / 2
                    int r8 = r3.leftMargin
                    int r7 = r7 + r8
                    int r8 = r3.rightMargin
                L_0x0049:
                    int r7 = r7 - r8
                L_0x004a:
                    r8 = 16
                    if (r6 == r8) goto L_0x0086
                    r8 = 48
                    if (r6 == r8) goto L_0x005f
                    r8 = 80
                    if (r6 == r8) goto L_0x0059
                    int r3 = r3.topMargin
                    goto L_0x0092
                L_0x0059:
                    int r6 = r14 - r12
                    int r6 = r6 - r5
                    int r3 = r3.bottomMargin
                    goto L_0x0090
                L_0x005f:
                    int r3 = r3.topMargin
                    int r6 = r9.getPaddingTop()
                    int r3 = r3 + r6
                    org.telegram.ui.ChannelAdminLogActivity r6 = org.telegram.ui.ChannelAdminLogActivity.this
                    org.telegram.ui.ActionBar.ActionBar r6 = r6.actionBar
                    if (r2 == r6) goto L_0x0092
                    org.telegram.ui.ChannelAdminLogActivity r6 = org.telegram.ui.ChannelAdminLogActivity.this
                    org.telegram.ui.ActionBar.ActionBar r6 = r6.actionBar
                    int r6 = r6.getVisibility()
                    if (r6 != 0) goto L_0x0092
                    org.telegram.ui.ChannelAdminLogActivity r6 = org.telegram.ui.ChannelAdminLogActivity.this
                    org.telegram.ui.ActionBar.ActionBar r6 = r6.actionBar
                    int r6 = r6.getMeasuredHeight()
                    int r3 = r3 + r6
                    goto L_0x0092
                L_0x0086:
                    int r6 = r14 - r12
                    int r6 = r6 - r5
                    int r6 = r6 / 2
                    int r8 = r3.topMargin
                    int r6 = r6 + r8
                    int r3 = r3.bottomMargin
                L_0x0090:
                    int r3 = r6 - r3
                L_0x0092:
                    org.telegram.ui.ChannelAdminLogActivity r6 = org.telegram.ui.ChannelAdminLogActivity.this
                    android.widget.FrameLayout r6 = r6.emptyViewContainer
                    if (r2 != r6) goto L_0x00bc
                    r6 = 1103101952(0x41CLASSNAME, float:24.0)
                    int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
                    org.telegram.ui.ChannelAdminLogActivity r8 = org.telegram.ui.ChannelAdminLogActivity.this
                    org.telegram.ui.ActionBar.ActionBar r8 = r8.actionBar
                    int r8 = r8.getVisibility()
                    if (r8 != 0) goto L_0x00b9
                    org.telegram.ui.ChannelAdminLogActivity r8 = org.telegram.ui.ChannelAdminLogActivity.this
                    org.telegram.ui.ActionBar.ActionBar r8 = r8.actionBar
                    int r8 = r8.getMeasuredHeight()
                    int r8 = r8 / 2
                    goto L_0x00ba
                L_0x00b9:
                    r8 = 0
                L_0x00ba:
                    int r6 = r6 - r8
                    goto L_0x00c8
                L_0x00bc:
                    org.telegram.ui.ChannelAdminLogActivity r6 = org.telegram.ui.ChannelAdminLogActivity.this
                    org.telegram.ui.ActionBar.ActionBar r6 = r6.actionBar
                    if (r2 != r6) goto L_0x00c9
                    int r6 = r9.getPaddingTop()
                L_0x00c8:
                    int r3 = r3 - r6
                L_0x00c9:
                    int r4 = r4 + r7
                    int r5 = r5 + r3
                    r2.layout(r7, r3, r4, r5)
                L_0x00ce:
                    int r1 = r1 + 1
                    goto L_0x0006
                L_0x00d2:
                    org.telegram.ui.ChannelAdminLogActivity r10 = org.telegram.ui.ChannelAdminLogActivity.this
                    r10.updateMessagesVisisblePart()
                    r9.notifyHeightChanged()
                    return
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChannelAdminLogActivity.AnonymousClass4.onLayout(boolean, int, int, int, int):void");
            }

            public boolean dispatchTouchEvent(MotionEvent motionEvent) {
                if (!AvatarPreviewer.hasVisibleInstance()) {
                    return super.dispatchTouchEvent(motionEvent);
                }
                AvatarPreviewer.getInstance().onTouchEvent(motionEvent);
                return true;
            }
        };
        this.fragmentView = r4;
        SizeNotifierFrameLayout sizeNotifierFrameLayout = r4;
        this.contentView = sizeNotifierFrameLayout;
        sizeNotifierFrameLayout.setOccupyStatusBar(!AndroidUtilities.isTablet());
        this.contentView.setBackgroundImage(Theme.getCachedWallpaper(), Theme.isWallpaperMotion());
        FrameLayout frameLayout = new FrameLayout(context2);
        this.emptyViewContainer = frameLayout;
        frameLayout.setVisibility(4);
        this.contentView.addView(this.emptyViewContainer, LayoutHelper.createFrame(-1, -2, 17));
        this.emptyViewContainer.setOnTouchListener($$Lambda$ChannelAdminLogActivity$ELcKKlDFHmfmtJvyr7USZYsM5GE.INSTANCE);
        TextView textView = new TextView(context2);
        this.emptyView = textView;
        textView.setTextSize(1, 14.0f);
        this.emptyView.setGravity(17);
        this.emptyView.setTextColor(Theme.getColor("chat_serviceText"));
        this.emptyView.setBackgroundDrawable(Theme.createRoundRectDrawable(AndroidUtilities.dp(10.0f), Theme.getServiceMessageColor()));
        this.emptyView.setPadding(AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f));
        this.emptyViewContainer.addView(this.emptyView, LayoutHelper.createFrame(-2, -2.0f, 17, 16.0f, 0.0f, 16.0f, 0.0f));
        AnonymousClass5 r42 = new RecyclerListView(context2) {
            /* JADX WARNING: Code restructure failed: missing block: B:2:0x0008, code lost:
                r9 = (org.telegram.ui.Cells.ChatMessageCell) r7;
             */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public boolean drawChild(android.graphics.Canvas r6, android.view.View r7, long r8) {
                /*
                    r5 = this;
                    boolean r8 = super.drawChild(r6, r7, r8)
                    boolean r9 = r7 instanceof org.telegram.ui.Cells.ChatMessageCell
                    if (r9 == 0) goto L_0x00be
                    r9 = r7
                    org.telegram.ui.Cells.ChatMessageCell r9 = (org.telegram.ui.Cells.ChatMessageCell) r9
                    org.telegram.messenger.ImageReceiver r0 = r9.getAvatarImage()
                    if (r0 == 0) goto L_0x00be
                    int r1 = r7.getTop()
                    boolean r2 = r9.isPinnedBottom()
                    if (r2 == 0) goto L_0x0048
                    org.telegram.ui.ChannelAdminLogActivity r2 = org.telegram.ui.ChannelAdminLogActivity.this
                    org.telegram.ui.Components.RecyclerListView r2 = r2.chatListView
                    androidx.recyclerview.widget.RecyclerView$ViewHolder r2 = r2.getChildViewHolder(r7)
                    if (r2 == 0) goto L_0x0048
                    org.telegram.ui.ChannelAdminLogActivity r3 = org.telegram.ui.ChannelAdminLogActivity.this
                    org.telegram.ui.Components.RecyclerListView r3 = r3.chatListView
                    int r2 = r2.getAdapterPosition()
                    int r2 = r2 + 1
                    androidx.recyclerview.widget.RecyclerView$ViewHolder r2 = r3.findViewHolderForAdapterPosition(r2)
                    if (r2 == 0) goto L_0x0048
                    r7 = 1148846080(0x447a0000, float:1000.0)
                    int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
                    int r7 = -r7
                    float r7 = (float) r7
                    r0.setImageY(r7)
                    r0.draw(r6)
                    return r8
                L_0x0048:
                    boolean r2 = r9.isPinnedTop()
                    if (r2 == 0) goto L_0x0080
                    org.telegram.ui.ChannelAdminLogActivity r2 = org.telegram.ui.ChannelAdminLogActivity.this
                    org.telegram.ui.Components.RecyclerListView r2 = r2.chatListView
                    androidx.recyclerview.widget.RecyclerView$ViewHolder r2 = r2.getChildViewHolder(r7)
                    if (r2 == 0) goto L_0x0080
                L_0x005a:
                    org.telegram.ui.ChannelAdminLogActivity r3 = org.telegram.ui.ChannelAdminLogActivity.this
                    org.telegram.ui.Components.RecyclerListView r3 = r3.chatListView
                    int r2 = r2.getAdapterPosition()
                    int r2 = r2 + -1
                    androidx.recyclerview.widget.RecyclerView$ViewHolder r2 = r3.findViewHolderForAdapterPosition(r2)
                    if (r2 == 0) goto L_0x0080
                    android.view.View r1 = r2.itemView
                    int r1 = r1.getTop()
                    android.view.View r3 = r2.itemView
                    boolean r4 = r3 instanceof org.telegram.ui.Cells.ChatMessageCell
                    if (r4 == 0) goto L_0x0080
                    org.telegram.ui.Cells.ChatMessageCell r3 = (org.telegram.ui.Cells.ChatMessageCell) r3
                    boolean r3 = r3.isPinnedTop()
                    if (r3 != 0) goto L_0x005a
                L_0x0080:
                    int r7 = r7.getTop()
                    int r9 = r9.getLayoutHeight()
                    int r7 = r7 + r9
                    org.telegram.ui.ChannelAdminLogActivity r9 = org.telegram.ui.ChannelAdminLogActivity.this
                    org.telegram.ui.Components.RecyclerListView r9 = r9.chatListView
                    int r9 = r9.getHeight()
                    org.telegram.ui.ChannelAdminLogActivity r2 = org.telegram.ui.ChannelAdminLogActivity.this
                    org.telegram.ui.Components.RecyclerListView r2 = r2.chatListView
                    int r2 = r2.getPaddingBottom()
                    int r9 = r9 - r2
                    if (r7 <= r9) goto L_0x00a1
                    r7 = r9
                L_0x00a1:
                    r9 = 1111490560(0x42400000, float:48.0)
                    int r2 = org.telegram.messenger.AndroidUtilities.dp(r9)
                    int r2 = r7 - r2
                    if (r2 >= r1) goto L_0x00b0
                    int r7 = org.telegram.messenger.AndroidUtilities.dp(r9)
                    int r7 = r7 + r1
                L_0x00b0:
                    r9 = 1110441984(0x42300000, float:44.0)
                    int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
                    int r7 = r7 - r9
                    float r7 = (float) r7
                    r0.setImageY(r7)
                    r0.draw(r6)
                L_0x00be:
                    return r8
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChannelAdminLogActivity.AnonymousClass5.drawChild(android.graphics.Canvas, android.view.View, long):boolean");
            }
        };
        this.chatListView = r42;
        r42.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
            public final void onItemClick(View view, int i) {
                ChannelAdminLogActivity.this.lambda$createView$3$ChannelAdminLogActivity(view, i);
            }
        });
        this.chatListView.setTag(1);
        this.chatListView.setVerticalScrollBarEnabled(true);
        RecyclerListView recyclerListView = this.chatListView;
        ChatActivityAdapter chatActivityAdapter = new ChatActivityAdapter(context2);
        this.chatAdapter = chatActivityAdapter;
        recyclerListView.setAdapter(chatActivityAdapter);
        this.chatListView.setClipToPadding(false);
        this.chatListView.setPadding(0, AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(3.0f));
        this.chatListView.setItemAnimator((RecyclerView.ItemAnimator) null);
        this.chatListView.setLayoutAnimation((LayoutAnimationController) null);
        AnonymousClass6 r43 = new LinearLayoutManager(this, context2) {
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }

            public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int i) {
                LinearSmoothScrollerCustom linearSmoothScrollerCustom = new LinearSmoothScrollerCustom(recyclerView.getContext(), 0);
                linearSmoothScrollerCustom.setTargetPosition(i);
                startSmoothScroll(linearSmoothScrollerCustom);
            }
        };
        this.chatLayoutManager = r43;
        r43.setOrientation(1);
        this.chatLayoutManager.setStackFromEnd(true);
        this.chatListView.setLayoutManager(this.chatLayoutManager);
        this.contentView.addView(this.chatListView, LayoutHelper.createFrame(-1, -1.0f));
        this.chatListView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            {
                AndroidUtilities.dp(100.0f);
            }

            public void onScrollStateChanged(RecyclerView recyclerView, int i) {
                if (i == 1) {
                    boolean unused = ChannelAdminLogActivity.this.scrollingFloatingDate = true;
                    boolean unused2 = ChannelAdminLogActivity.this.checkTextureViewPosition = true;
                } else if (i == 0) {
                    boolean unused3 = ChannelAdminLogActivity.this.scrollingFloatingDate = false;
                    boolean unused4 = ChannelAdminLogActivity.this.checkTextureViewPosition = false;
                    ChannelAdminLogActivity.this.hideFloatingDateView(true);
                }
            }

            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                ChannelAdminLogActivity.this.chatListView.invalidate();
                if (i2 != 0 && ChannelAdminLogActivity.this.scrollingFloatingDate && !ChannelAdminLogActivity.this.currentFloatingTopIsNotMessage && ChannelAdminLogActivity.this.floatingDateView.getTag() == null) {
                    if (ChannelAdminLogActivity.this.floatingDateAnimation != null) {
                        ChannelAdminLogActivity.this.floatingDateAnimation.cancel();
                    }
                    ChannelAdminLogActivity.this.floatingDateView.setTag(1);
                    AnimatorSet unused = ChannelAdminLogActivity.this.floatingDateAnimation = new AnimatorSet();
                    ChannelAdminLogActivity.this.floatingDateAnimation.setDuration(150);
                    ChannelAdminLogActivity.this.floatingDateAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(ChannelAdminLogActivity.this.floatingDateView, "alpha", new float[]{1.0f})});
                    ChannelAdminLogActivity.this.floatingDateAnimation.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animator) {
                            if (animator.equals(ChannelAdminLogActivity.this.floatingDateAnimation)) {
                                AnimatorSet unused = ChannelAdminLogActivity.this.floatingDateAnimation = null;
                            }
                        }
                    });
                    ChannelAdminLogActivity.this.floatingDateAnimation.start();
                }
                ChannelAdminLogActivity.this.checkScrollForLoad(true);
                ChannelAdminLogActivity.this.updateMessagesVisisblePart();
            }
        });
        int i2 = this.scrollToPositionOnRecreate;
        if (i2 != -1) {
            this.chatLayoutManager.scrollToPositionWithOffset(i2, this.scrollToOffsetOnRecreate);
            this.scrollToPositionOnRecreate = -1;
        }
        FrameLayout frameLayout2 = new FrameLayout(context2);
        this.progressView = frameLayout2;
        frameLayout2.setVisibility(4);
        this.contentView.addView(this.progressView, LayoutHelper.createFrame(-1, -1, 51));
        View view = new View(context2);
        this.progressView2 = view;
        view.setBackgroundResource(NUM);
        this.progressView2.getBackground().setColorFilter(Theme.colorFilter);
        this.progressView.addView(this.progressView2, LayoutHelper.createFrame(36, 36, 17));
        RadialProgressView radialProgressView = new RadialProgressView(context2);
        this.progressBar = radialProgressView;
        radialProgressView.setSize(AndroidUtilities.dp(28.0f));
        this.progressBar.setProgressColor(Theme.getColor("chat_serviceText"));
        this.progressView.addView(this.progressBar, LayoutHelper.createFrame(32, 32, 17));
        ChatActionCell chatActionCell = new ChatActionCell(context2);
        this.floatingDateView = chatActionCell;
        chatActionCell.setAlpha(0.0f);
        this.floatingDateView.setImportantForAccessibility(2);
        this.contentView.addView(this.floatingDateView, LayoutHelper.createFrame(-2, -2.0f, 49, 0.0f, 4.0f, 0.0f, 0.0f));
        this.contentView.addView(this.actionBar);
        AnonymousClass8 r44 = new FrameLayout(this, context2) {
            public void onDraw(Canvas canvas) {
                int intrinsicHeight = Theme.chat_composeShadowDrawable.getIntrinsicHeight();
                Theme.chat_composeShadowDrawable.setBounds(0, 0, getMeasuredWidth(), intrinsicHeight);
                Theme.chat_composeShadowDrawable.draw(canvas);
                canvas.drawRect(0.0f, (float) intrinsicHeight, (float) getMeasuredWidth(), (float) getMeasuredHeight(), Theme.chat_composeBackgroundPaint);
            }
        };
        this.bottomOverlayChat = r44;
        r44.setWillNotDraw(false);
        this.bottomOverlayChat.setPadding(0, AndroidUtilities.dp(3.0f), 0, 0);
        this.contentView.addView(this.bottomOverlayChat, LayoutHelper.createFrame(-1, 51, 80));
        this.bottomOverlayChat.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                ChannelAdminLogActivity.this.lambda$createView$5$ChannelAdminLogActivity(view);
            }
        });
        TextView textView2 = new TextView(context2);
        this.bottomOverlayChatText = textView2;
        textView2.setTextSize(1, 15.0f);
        this.bottomOverlayChatText.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.bottomOverlayChatText.setTextColor(Theme.getColor("chat_fieldOverlayText"));
        this.bottomOverlayChatText.setText(LocaleController.getString("SETTINGS", NUM).toUpperCase());
        this.bottomOverlayChat.addView(this.bottomOverlayChatText, LayoutHelper.createFrame(-2, -2, 17));
        ImageView imageView = new ImageView(context2);
        this.bottomOverlayImage = imageView;
        imageView.setImageResource(NUM);
        this.bottomOverlayImage.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_fieldOverlayText"), PorterDuff.Mode.MULTIPLY));
        this.bottomOverlayImage.setScaleType(ImageView.ScaleType.CENTER);
        this.bottomOverlayChat.addView(this.bottomOverlayImage, LayoutHelper.createFrame(48, 48.0f, 53, 3.0f, 0.0f, 0.0f, 0.0f));
        this.bottomOverlayImage.setContentDescription(LocaleController.getString("BotHelp", NUM));
        this.bottomOverlayImage.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                ChannelAdminLogActivity.this.lambda$createView$6$ChannelAdminLogActivity(view);
            }
        });
        AnonymousClass9 r45 = new FrameLayout(this, context2) {
            public void onDraw(Canvas canvas) {
                int intrinsicHeight = Theme.chat_composeShadowDrawable.getIntrinsicHeight();
                Theme.chat_composeShadowDrawable.setBounds(0, 0, getMeasuredWidth(), intrinsicHeight);
                Theme.chat_composeShadowDrawable.draw(canvas);
                canvas.drawRect(0.0f, (float) intrinsicHeight, (float) getMeasuredWidth(), (float) getMeasuredHeight(), Theme.chat_composeBackgroundPaint);
            }
        };
        this.searchContainer = r45;
        r45.setWillNotDraw(false);
        this.searchContainer.setVisibility(4);
        this.searchContainer.setFocusable(true);
        this.searchContainer.setFocusableInTouchMode(true);
        this.searchContainer.setClickable(true);
        this.searchContainer.setPadding(0, AndroidUtilities.dp(3.0f), 0, 0);
        this.contentView.addView(this.searchContainer, LayoutHelper.createFrame(-1, 51, 80));
        ImageView imageView2 = new ImageView(context2);
        this.searchCalendarButton = imageView2;
        imageView2.setScaleType(ImageView.ScaleType.CENTER);
        this.searchCalendarButton.setImageResource(NUM);
        this.searchCalendarButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_searchPanelIcons"), PorterDuff.Mode.MULTIPLY));
        this.searchContainer.addView(this.searchCalendarButton, LayoutHelper.createFrame(48, 48, 53));
        this.searchCalendarButton.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                ChannelAdminLogActivity.this.lambda$createView$8$ChannelAdminLogActivity(view);
            }
        });
        SimpleTextView simpleTextView = new SimpleTextView(context2);
        this.searchCountText = simpleTextView;
        simpleTextView.setTextColor(Theme.getColor("chat_searchPanelText"));
        this.searchCountText.setTextSize(15);
        this.searchCountText.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.searchContainer.addView(this.searchCountText, LayoutHelper.createFrame(-1, -2.0f, 19, 108.0f, 0.0f, 0.0f, 0.0f));
        this.chatAdapter.updateRows();
        if (!this.loading || !this.messages.isEmpty()) {
            this.progressView.setVisibility(4);
            this.chatListView.setEmptyView(this.emptyViewContainer);
        } else {
            this.progressView.setVisibility(0);
            this.chatListView.setEmptyView((View) null);
        }
        UndoView undoView2 = new UndoView(context2);
        this.undoView = undoView2;
        undoView2.setAdditionalTranslationY((float) AndroidUtilities.dp(51.0f));
        this.contentView.addView(this.undoView, LayoutHelper.createFrame(-1, -2.0f, 83, 8.0f, 0.0f, 8.0f, 8.0f));
        updateEmptyPlaceholder();
        return this.fragmentView;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$3 */
    public /* synthetic */ void lambda$createView$3$ChannelAdminLogActivity(View view, int i) {
        createMenu(view);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$5 */
    public /* synthetic */ void lambda$createView$5$ChannelAdminLogActivity(View view) {
        if (getParentActivity() != null) {
            AdminLogFilterAlert adminLogFilterAlert = new AdminLogFilterAlert(getParentActivity(), this.currentFilter, this.selectedAdmins, this.currentChat.megagroup);
            adminLogFilterAlert.setCurrentAdmins(this.admins);
            adminLogFilterAlert.setAdminLogFilterAlertDelegate(new AdminLogFilterAlert.AdminLogFilterAlertDelegate() {
                public final void didSelectRights(TLRPC$TL_channelAdminLogEventsFilter tLRPC$TL_channelAdminLogEventsFilter, SparseArray sparseArray) {
                    ChannelAdminLogActivity.this.lambda$null$4$ChannelAdminLogActivity(tLRPC$TL_channelAdminLogEventsFilter, sparseArray);
                }
            });
            showDialog(adminLogFilterAlert);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$4 */
    public /* synthetic */ void lambda$null$4$ChannelAdminLogActivity(TLRPC$TL_channelAdminLogEventsFilter tLRPC$TL_channelAdminLogEventsFilter, SparseArray sparseArray) {
        this.currentFilter = tLRPC$TL_channelAdminLogEventsFilter;
        this.selectedAdmins = sparseArray;
        if (tLRPC$TL_channelAdminLogEventsFilter == null && sparseArray == null) {
            this.avatarContainer.setSubtitle(LocaleController.getString("EventLogAllEvents", NUM));
        } else {
            this.avatarContainer.setSubtitle(LocaleController.getString("EventLogSelectedEvents", NUM));
        }
        loadMessages(true);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$6 */
    public /* synthetic */ void lambda$createView$6$ChannelAdminLogActivity(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
        if (this.currentChat.megagroup) {
            builder.setMessage(AndroidUtilities.replaceTags(LocaleController.getString("EventLogInfoDetail", NUM)));
        } else {
            builder.setMessage(AndroidUtilities.replaceTags(LocaleController.getString("EventLogInfoDetailChannel", NUM)));
        }
        builder.setPositiveButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null);
        builder.setTitle(LocaleController.getString("EventLogInfoTitle", NUM));
        showDialog(builder.create());
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$8 */
    public /* synthetic */ void lambda$createView$8$ChannelAdminLogActivity(View view) {
        if (getParentActivity() != null) {
            AndroidUtilities.hideKeyboard(this.searchItem.getSearchField());
            showDialog(AlertsCreator.createCalendarPickerDialog(getParentActivity(), 1375315200000L, new MessagesStorage.IntCallback() {
                public final void run(int i) {
                    ChannelAdminLogActivity.this.lambda$null$7$ChannelAdminLogActivity(i);
                }
            }).create());
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$7 */
    public /* synthetic */ void lambda$null$7$ChannelAdminLogActivity(int i) {
        loadMessages(true);
    }

    /* access modifiers changed from: private */
    public void createMenu(View view) {
        MessageObject messageObject;
        View view2 = view;
        if (view2 instanceof ChatMessageCell) {
            messageObject = ((ChatMessageCell) view2).getMessageObject();
        } else {
            messageObject = view2 instanceof ChatActionCell ? ((ChatActionCell) view2).getMessageObject() : null;
        }
        if (messageObject != null) {
            int messageType = getMessageType(messageObject);
            this.selectedObject = messageObject;
            if (getParentActivity() != null) {
                AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
                ArrayList arrayList = new ArrayList();
                ArrayList arrayList2 = new ArrayList();
                MessageObject messageObject2 = this.selectedObject;
                if (messageObject2.type == 0 || messageObject2.caption != null) {
                    arrayList.add(LocaleController.getString("Copy", NUM));
                    arrayList2.add(3);
                }
                if (messageType == 1) {
                    TLRPC$TL_channelAdminLogEvent tLRPC$TL_channelAdminLogEvent = this.selectedObject.currentEvent;
                    if (tLRPC$TL_channelAdminLogEvent != null) {
                        TLRPC$ChannelAdminLogEventAction tLRPC$ChannelAdminLogEventAction = tLRPC$TL_channelAdminLogEvent.action;
                        if (tLRPC$ChannelAdminLogEventAction instanceof TLRPC$TL_channelAdminLogEventActionChangeStickerSet) {
                            TLRPC$TL_channelAdminLogEventActionChangeStickerSet tLRPC$TL_channelAdminLogEventActionChangeStickerSet = (TLRPC$TL_channelAdminLogEventActionChangeStickerSet) tLRPC$ChannelAdminLogEventAction;
                            TLRPC$InputStickerSet tLRPC$InputStickerSet = tLRPC$TL_channelAdminLogEventActionChangeStickerSet.new_stickerset;
                            if (tLRPC$InputStickerSet == null || (tLRPC$InputStickerSet instanceof TLRPC$TL_inputStickerSetEmpty)) {
                                tLRPC$InputStickerSet = tLRPC$TL_channelAdminLogEventActionChangeStickerSet.prev_stickerset;
                            }
                            TLRPC$InputStickerSet tLRPC$InputStickerSet2 = tLRPC$InputStickerSet;
                            if (tLRPC$InputStickerSet2 != null) {
                                showDialog(new StickersAlert(getParentActivity(), this, tLRPC$InputStickerSet2, (TLRPC$TL_messages_stickerSet) null, (StickersAlert.StickersAlertDelegate) null));
                                return;
                            }
                        }
                    }
                    if (tLRPC$TL_channelAdminLogEvent != null && (tLRPC$TL_channelAdminLogEvent.action instanceof TLRPC$TL_channelAdminLogEventActionChangeHistoryTTL) && ChatObject.canUserDoAdminAction(this.currentChat, 13)) {
                        ClearHistoryAlert clearHistoryAlert = new ClearHistoryAlert(getParentActivity(), (TLRPC$User) null, this.currentChat, false);
                        clearHistoryAlert.setDelegate(new ClearHistoryAlert.ClearHistoryAlertDelegate() {
                            public /* synthetic */ void onClearHistory(boolean z) {
                                ClearHistoryAlert.ClearHistoryAlertDelegate.CC.$default$onClearHistory(this, z);
                            }

                            public void onAutoDeleteHistory(int i, int i2) {
                                ChannelAdminLogActivity.this.getMessagesController().setDialogHistoryTTL((long) (-ChannelAdminLogActivity.this.currentChat.id), i);
                                TLRPC$ChatFull chatFull = ChannelAdminLogActivity.this.getMessagesController().getChatFull(ChannelAdminLogActivity.this.currentChat.id);
                                if (chatFull != null) {
                                    ChannelAdminLogActivity.this.undoView.showWithAction((long) (-ChannelAdminLogActivity.this.currentChat.id), i2, (Object) null, Integer.valueOf(chatFull.ttl_period), (Runnable) null, (Runnable) null);
                                }
                            }
                        });
                        showDialog(clearHistoryAlert);
                    }
                } else if (messageType == 3) {
                    TLRPC$MessageMedia tLRPC$MessageMedia = this.selectedObject.messageOwner.media;
                    if ((tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaWebPage) && MessageObject.isNewGifDocument(tLRPC$MessageMedia.webpage.document)) {
                        arrayList.add(LocaleController.getString("SaveToGIFs", NUM));
                        arrayList2.add(11);
                    }
                } else if (messageType == 4) {
                    if (this.selectedObject.isVideo()) {
                        arrayList.add(LocaleController.getString("SaveToGallery", NUM));
                        arrayList2.add(4);
                        arrayList.add(LocaleController.getString("ShareFile", NUM));
                        arrayList2.add(6);
                    } else if (this.selectedObject.isMusic()) {
                        arrayList.add(LocaleController.getString("SaveToMusic", NUM));
                        arrayList2.add(10);
                        arrayList.add(LocaleController.getString("ShareFile", NUM));
                        arrayList2.add(6);
                    } else if (this.selectedObject.getDocument() != null) {
                        if (MessageObject.isNewGifDocument(this.selectedObject.getDocument())) {
                            arrayList.add(LocaleController.getString("SaveToGIFs", NUM));
                            arrayList2.add(11);
                        }
                        arrayList.add(LocaleController.getString("SaveToDownloads", NUM));
                        arrayList2.add(10);
                        arrayList.add(LocaleController.getString("ShareFile", NUM));
                        arrayList2.add(6);
                    } else {
                        arrayList.add(LocaleController.getString("SaveToGallery", NUM));
                        arrayList2.add(4);
                    }
                } else if (messageType == 5) {
                    arrayList.add(LocaleController.getString("ApplyLocalizationFile", NUM));
                    arrayList2.add(5);
                    arrayList.add(LocaleController.getString("SaveToDownloads", NUM));
                    arrayList2.add(10);
                    arrayList.add(LocaleController.getString("ShareFile", NUM));
                    arrayList2.add(6);
                } else if (messageType == 10) {
                    arrayList.add(LocaleController.getString("ApplyThemeFile", NUM));
                    arrayList2.add(5);
                    arrayList.add(LocaleController.getString("SaveToDownloads", NUM));
                    arrayList2.add(10);
                    arrayList.add(LocaleController.getString("ShareFile", NUM));
                    arrayList2.add(6);
                } else if (messageType == 6) {
                    arrayList.add(LocaleController.getString("SaveToGallery", NUM));
                    arrayList2.add(7);
                    arrayList.add(LocaleController.getString("SaveToDownloads", NUM));
                    arrayList2.add(10);
                    arrayList.add(LocaleController.getString("ShareFile", NUM));
                    arrayList2.add(6);
                } else if (messageType == 7) {
                    if (this.selectedObject.isMask()) {
                        arrayList.add(LocaleController.getString("AddToMasks", NUM));
                    } else {
                        arrayList.add(LocaleController.getString("AddToStickers", NUM));
                    }
                    arrayList2.add(9);
                } else if (messageType == 8) {
                    TLRPC$User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.selectedObject.messageOwner.media.user_id));
                    if (!(user == null || user.id == UserConfig.getInstance(this.currentAccount).getClientUserId() || ContactsController.getInstance(this.currentAccount).contactsDict.get(Integer.valueOf(user.id)) != null)) {
                        arrayList.add(LocaleController.getString("AddContactTitle", NUM));
                        arrayList2.add(15);
                    }
                    String str = this.selectedObject.messageOwner.media.phone_number;
                    if (!(str == null && str.length() == 0)) {
                        arrayList.add(LocaleController.getString("Copy", NUM));
                        arrayList2.add(16);
                        arrayList.add(LocaleController.getString("Call", NUM));
                        arrayList2.add(17);
                    }
                }
                if (!arrayList2.isEmpty()) {
                    builder.setItems((CharSequence[]) arrayList.toArray(new CharSequence[0]), new DialogInterface.OnClickListener(arrayList2) {
                        public final /* synthetic */ ArrayList f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final void onClick(DialogInterface dialogInterface, int i) {
                            ChannelAdminLogActivity.this.lambda$createMenu$9$ChannelAdminLogActivity(this.f$1, dialogInterface, i);
                        }
                    });
                    builder.setTitle(LocaleController.getString("Message", NUM));
                    showDialog(builder.create());
                }
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createMenu$9 */
    public /* synthetic */ void lambda$createMenu$9$ChannelAdminLogActivity(ArrayList arrayList, DialogInterface dialogInterface, int i) {
        if (this.selectedObject != null && i >= 0 && i < arrayList.size()) {
            processSelectedOption(((Integer) arrayList.get(i)).intValue());
        }
    }

    private String getMessageContent(MessageObject messageObject, int i, boolean z) {
        int fromChatId;
        TLRPC$Chat chat;
        String str = "";
        if (z && i != (fromChatId = messageObject.getFromChatId())) {
            if (fromChatId > 0) {
                TLRPC$User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(fromChatId));
                if (user != null) {
                    str = ContactsController.formatName(user.first_name, user.last_name) + ":\n";
                }
            } else if (fromChatId < 0 && (chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-fromChatId))) != null) {
                str = chat.title + ":\n";
            }
        }
        if (messageObject.type != 0 || messageObject.messageOwner.message == null) {
            TLRPC$Message tLRPC$Message = messageObject.messageOwner;
            if (tLRPC$Message.media == null || tLRPC$Message.message == null) {
                return str + messageObject.messageText;
            }
            return str + messageObject.messageOwner.message;
        }
        return str + messageObject.messageOwner.message;
    }

    /* access modifiers changed from: private */
    public TextureView createTextureView(boolean z) {
        if (this.parentLayout == null) {
            return null;
        }
        if (this.roundVideoContainer == null) {
            if (Build.VERSION.SDK_INT >= 21) {
                AnonymousClass11 r0 = new FrameLayout(getParentActivity()) {
                    public void setTranslationY(float f) {
                        super.setTranslationY(f);
                        ChannelAdminLogActivity.this.contentView.invalidate();
                    }
                };
                this.roundVideoContainer = r0;
                r0.setOutlineProvider(new ViewOutlineProvider(this) {
                    @TargetApi(21)
                    public void getOutline(View view, Outline outline) {
                        int i = AndroidUtilities.roundMessageSize;
                        outline.setOval(0, 0, i, i);
                    }
                });
                this.roundVideoContainer.setClipToOutline(true);
            } else {
                this.roundVideoContainer = new FrameLayout(getParentActivity()) {
                    /* access modifiers changed from: protected */
                    public void onSizeChanged(int i, int i2, int i3, int i4) {
                        super.onSizeChanged(i, i2, i3, i4);
                        ChannelAdminLogActivity.this.aspectPath.reset();
                        float f = (float) (i / 2);
                        ChannelAdminLogActivity.this.aspectPath.addCircle(f, (float) (i2 / 2), f, Path.Direction.CW);
                        ChannelAdminLogActivity.this.aspectPath.toggleInverseFillType();
                    }

                    public void setTranslationY(float f) {
                        super.setTranslationY(f);
                        ChannelAdminLogActivity.this.contentView.invalidate();
                    }

                    public void setVisibility(int i) {
                        super.setVisibility(i);
                        if (i == 0) {
                            setLayerType(2, (Paint) null);
                        }
                    }

                    /* access modifiers changed from: protected */
                    public void dispatchDraw(Canvas canvas) {
                        super.dispatchDraw(canvas);
                        canvas.drawPath(ChannelAdminLogActivity.this.aspectPath, ChannelAdminLogActivity.this.aspectPaint);
                    }
                };
                this.aspectPath = new Path();
                Paint paint = new Paint(1);
                this.aspectPaint = paint;
                paint.setColor(-16777216);
                this.aspectPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            }
            this.roundVideoContainer.setWillNotDraw(false);
            this.roundVideoContainer.setVisibility(4);
            AspectRatioFrameLayout aspectRatioFrameLayout2 = new AspectRatioFrameLayout(getParentActivity());
            this.aspectRatioFrameLayout = aspectRatioFrameLayout2;
            aspectRatioFrameLayout2.setBackgroundColor(0);
            if (z) {
                this.roundVideoContainer.addView(this.aspectRatioFrameLayout, LayoutHelper.createFrame(-1, -1.0f));
            }
            TextureView textureView = new TextureView(getParentActivity());
            this.videoTextureView = textureView;
            textureView.setOpaque(false);
            this.aspectRatioFrameLayout.addView(this.videoTextureView, LayoutHelper.createFrame(-1, -1.0f));
        }
        if (this.roundVideoContainer.getParent() == null) {
            SizeNotifierFrameLayout sizeNotifierFrameLayout = this.contentView;
            FrameLayout frameLayout = this.roundVideoContainer;
            int i = AndroidUtilities.roundMessageSize;
            sizeNotifierFrameLayout.addView(frameLayout, 1, new FrameLayout.LayoutParams(i, i));
        }
        this.roundVideoContainer.setVisibility(4);
        this.aspectRatioFrameLayout.setDrawingReady(false);
        return this.videoTextureView;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:83:0x022d, code lost:
        if (r0.exists() != false) goto L_0x0231;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void processSelectedOption(int r11) {
        /*
            r10 = this;
            org.telegram.messenger.MessageObject r0 = r10.selectedObject
            if (r0 != 0) goto L_0x0005
            return
        L_0x0005:
            r1 = 500(0x1f4, float:7.0E-43)
            r2 = 3
            r3 = 4
            r4 = 23
            java.lang.String r5 = "android.permission.WRITE_EXTERNAL_STORAGE"
            r6 = 0
            r7 = 1
            r8 = 0
            switch(r11) {
                case 3: goto L_0x0386;
                case 4: goto L_0x0327;
                case 5: goto L_0x0212;
                case 6: goto L_0x0187;
                case 7: goto L_0x0136;
                case 8: goto L_0x0013;
                case 9: goto L_0x011e;
                case 10: goto L_0x0098;
                case 11: goto L_0x0087;
                case 12: goto L_0x0013;
                case 13: goto L_0x0013;
                case 14: goto L_0x0013;
                case 15: goto L_0x0058;
                case 16: goto L_0x004d;
                case 17: goto L_0x0015;
                default: goto L_0x0013;
            }
        L_0x0013:
            goto L_0x038d
        L_0x0015:
            android.content.Intent r0 = new android.content.Intent     // Catch:{ Exception -> 0x0047 }
            java.lang.String r2 = "android.intent.action.DIAL"
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0047 }
            r3.<init>()     // Catch:{ Exception -> 0x0047 }
            java.lang.String r4 = "tel:"
            r3.append(r4)     // Catch:{ Exception -> 0x0047 }
            org.telegram.messenger.MessageObject r4 = r10.selectedObject     // Catch:{ Exception -> 0x0047 }
            org.telegram.tgnet.TLRPC$Message r4 = r4.messageOwner     // Catch:{ Exception -> 0x0047 }
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r4.media     // Catch:{ Exception -> 0x0047 }
            java.lang.String r4 = r4.phone_number     // Catch:{ Exception -> 0x0047 }
            r3.append(r4)     // Catch:{ Exception -> 0x0047 }
            java.lang.String r3 = r3.toString()     // Catch:{ Exception -> 0x0047 }
            android.net.Uri r3 = android.net.Uri.parse(r3)     // Catch:{ Exception -> 0x0047 }
            r0.<init>(r2, r3)     // Catch:{ Exception -> 0x0047 }
            r2 = 268435456(0x10000000, float:2.5243549E-29)
            r0.addFlags(r2)     // Catch:{ Exception -> 0x0047 }
            android.app.Activity r2 = r10.getParentActivity()     // Catch:{ Exception -> 0x0047 }
            r2.startActivityForResult(r0, r1)     // Catch:{ Exception -> 0x0047 }
            goto L_0x038d
        L_0x0047:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x038d
        L_0x004d:
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            java.lang.String r0 = r0.phone_number
            org.telegram.messenger.AndroidUtilities.addToClipboard(r0)
            goto L_0x038d
        L_0x0058:
            android.os.Bundle r0 = new android.os.Bundle
            r0.<init>()
            org.telegram.messenger.MessageObject r1 = r10.selectedObject
            org.telegram.tgnet.TLRPC$Message r1 = r1.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r1.media
            int r1 = r1.user_id
            java.lang.String r2 = "user_id"
            r0.putInt(r2, r1)
            org.telegram.messenger.MessageObject r1 = r10.selectedObject
            org.telegram.tgnet.TLRPC$Message r1 = r1.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r1.media
            java.lang.String r1 = r1.phone_number
            java.lang.String r2 = "phone"
            r0.putString(r2, r1)
            java.lang.String r1 = "addContact"
            r0.putBoolean(r1, r7)
            org.telegram.ui.ContactAddActivity r1 = new org.telegram.ui.ContactAddActivity
            r1.<init>(r0)
            r10.presentFragment(r1)
            goto L_0x038d
        L_0x0087:
            org.telegram.tgnet.TLRPC$Document r0 = r0.getDocument()
            int r1 = r10.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
            org.telegram.messenger.MessageObject r2 = r10.selectedObject
            r1.saveGif(r2, r0)
            goto L_0x038d
        L_0x0098:
            int r0 = android.os.Build.VERSION.SDK_INT
            if (r0 < r4) goto L_0x00b4
            android.app.Activity r0 = r10.getParentActivity()
            int r0 = r0.checkSelfPermission(r5)
            if (r0 == 0) goto L_0x00b4
            android.app.Activity r0 = r10.getParentActivity()
            java.lang.String[] r1 = new java.lang.String[r7]
            r1[r6] = r5
            r0.requestPermissions(r1, r3)
            r10.selectedObject = r8
            return
        L_0x00b4:
            org.telegram.messenger.MessageObject r0 = r10.selectedObject
            org.telegram.tgnet.TLRPC$Document r0 = r0.getDocument()
            java.lang.String r0 = org.telegram.messenger.FileLoader.getDocumentFileName(r0)
            boolean r1 = android.text.TextUtils.isEmpty(r0)
            if (r1 == 0) goto L_0x00ca
            org.telegram.messenger.MessageObject r0 = r10.selectedObject
            java.lang.String r0 = r0.getFileName()
        L_0x00ca:
            org.telegram.messenger.MessageObject r1 = r10.selectedObject
            org.telegram.tgnet.TLRPC$Message r1 = r1.messageOwner
            java.lang.String r1 = r1.attachPath
            if (r1 == 0) goto L_0x00e4
            int r3 = r1.length()
            if (r3 <= 0) goto L_0x00e4
            java.io.File r3 = new java.io.File
            r3.<init>(r1)
            boolean r3 = r3.exists()
            if (r3 != 0) goto L_0x00e4
            r1 = r8
        L_0x00e4:
            if (r1 == 0) goto L_0x00ec
            int r3 = r1.length()
            if (r3 != 0) goto L_0x00f8
        L_0x00ec:
            org.telegram.messenger.MessageObject r1 = r10.selectedObject
            org.telegram.tgnet.TLRPC$Message r1 = r1.messageOwner
            java.io.File r1 = org.telegram.messenger.FileLoader.getPathToMessage(r1)
            java.lang.String r1 = r1.toString()
        L_0x00f8:
            android.app.Activity r3 = r10.getParentActivity()
            org.telegram.messenger.MessageObject r4 = r10.selectedObject
            boolean r4 = r4.isMusic()
            if (r4 == 0) goto L_0x0105
            goto L_0x0106
        L_0x0105:
            r2 = 2
        L_0x0106:
            org.telegram.messenger.MessageObject r4 = r10.selectedObject
            org.telegram.tgnet.TLRPC$Document r4 = r4.getDocument()
            if (r4 == 0) goto L_0x0117
            org.telegram.messenger.MessageObject r4 = r10.selectedObject
            org.telegram.tgnet.TLRPC$Document r4 = r4.getDocument()
            java.lang.String r4 = r4.mime_type
            goto L_0x0119
        L_0x0117:
            java.lang.String r4 = ""
        L_0x0119:
            org.telegram.messenger.MediaController.saveFile(r1, r3, r2, r0, r4)
            goto L_0x038d
        L_0x011e:
            org.telegram.ui.Components.StickersAlert r0 = new org.telegram.ui.Components.StickersAlert
            android.app.Activity r2 = r10.getParentActivity()
            org.telegram.messenger.MessageObject r1 = r10.selectedObject
            org.telegram.tgnet.TLRPC$InputStickerSet r4 = r1.getInputStickerSet()
            r5 = 0
            r6 = 0
            r1 = r0
            r3 = r10
            r1.<init>(r2, r3, r4, r5, r6)
            r10.showDialog(r0)
            goto L_0x038d
        L_0x0136:
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.attachPath
            if (r0 == 0) goto L_0x014e
            int r1 = r0.length()
            if (r1 <= 0) goto L_0x014e
            java.io.File r1 = new java.io.File
            r1.<init>(r0)
            boolean r1 = r1.exists()
            if (r1 != 0) goto L_0x014e
            r0 = r8
        L_0x014e:
            if (r0 == 0) goto L_0x0156
            int r1 = r0.length()
            if (r1 != 0) goto L_0x0162
        L_0x0156:
            org.telegram.messenger.MessageObject r0 = r10.selectedObject
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.io.File r0 = org.telegram.messenger.FileLoader.getPathToMessage(r0)
            java.lang.String r0 = r0.toString()
        L_0x0162:
            int r1 = android.os.Build.VERSION.SDK_INT
            if (r1 < r4) goto L_0x017e
            android.app.Activity r1 = r10.getParentActivity()
            int r1 = r1.checkSelfPermission(r5)
            if (r1 == 0) goto L_0x017e
            android.app.Activity r0 = r10.getParentActivity()
            java.lang.String[] r1 = new java.lang.String[r7]
            r1[r6] = r5
            r0.requestPermissions(r1, r3)
            r10.selectedObject = r8
            return
        L_0x017e:
            android.app.Activity r1 = r10.getParentActivity()
            org.telegram.messenger.MediaController.saveFile(r0, r1, r6, r8, r8)
            goto L_0x038d
        L_0x0187:
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.attachPath
            if (r0 == 0) goto L_0x019f
            int r2 = r0.length()
            if (r2 <= 0) goto L_0x019f
            java.io.File r2 = new java.io.File
            r2.<init>(r0)
            boolean r2 = r2.exists()
            if (r2 != 0) goto L_0x019f
            r0 = r8
        L_0x019f:
            if (r0 == 0) goto L_0x01a7
            int r2 = r0.length()
            if (r2 != 0) goto L_0x01b3
        L_0x01a7:
            org.telegram.messenger.MessageObject r0 = r10.selectedObject
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.io.File r0 = org.telegram.messenger.FileLoader.getPathToMessage(r0)
            java.lang.String r0 = r0.toString()
        L_0x01b3:
            android.content.Intent r2 = new android.content.Intent
            java.lang.String r3 = "android.intent.action.SEND"
            r2.<init>(r3)
            org.telegram.messenger.MessageObject r3 = r10.selectedObject
            org.telegram.tgnet.TLRPC$Document r3 = r3.getDocument()
            java.lang.String r3 = r3.mime_type
            r2.setType(r3)
            int r3 = android.os.Build.VERSION.SDK_INT
            r4 = 24
            java.lang.String r5 = "android.intent.extra.STREAM"
            if (r3 < r4) goto L_0x01f0
            android.app.Activity r3 = r10.getParentActivity()     // Catch:{ Exception -> 0x01e3 }
            java.lang.String r4 = "org.telegram.messenger.beta.provider"
            java.io.File r6 = new java.io.File     // Catch:{ Exception -> 0x01e3 }
            r6.<init>(r0)     // Catch:{ Exception -> 0x01e3 }
            android.net.Uri r3 = androidx.core.content.FileProvider.getUriForFile(r3, r4, r6)     // Catch:{ Exception -> 0x01e3 }
            r2.putExtra(r5, r3)     // Catch:{ Exception -> 0x01e3 }
            r2.setFlags(r7)     // Catch:{ Exception -> 0x01e3 }
            goto L_0x01fc
        L_0x01e3:
            java.io.File r3 = new java.io.File
            r3.<init>(r0)
            android.net.Uri r0 = android.net.Uri.fromFile(r3)
            r2.putExtra(r5, r0)
            goto L_0x01fc
        L_0x01f0:
            java.io.File r3 = new java.io.File
            r3.<init>(r0)
            android.net.Uri r0 = android.net.Uri.fromFile(r3)
            r2.putExtra(r5, r0)
        L_0x01fc:
            android.app.Activity r0 = r10.getParentActivity()
            r3 = 2131627377(0x7f0e0d71, float:1.8882017E38)
            java.lang.String r4 = "ShareFile"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            android.content.Intent r2 = android.content.Intent.createChooser(r2, r3)
            r0.startActivityForResult(r2, r1)
            goto L_0x038d
        L_0x0212:
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.attachPath
            if (r0 == 0) goto L_0x0230
            int r0 = r0.length()
            if (r0 == 0) goto L_0x0230
            java.io.File r0 = new java.io.File
            org.telegram.messenger.MessageObject r1 = r10.selectedObject
            org.telegram.tgnet.TLRPC$Message r1 = r1.messageOwner
            java.lang.String r1 = r1.attachPath
            r0.<init>(r1)
            boolean r1 = r0.exists()
            if (r1 == 0) goto L_0x0230
            goto L_0x0231
        L_0x0230:
            r0 = r8
        L_0x0231:
            if (r0 != 0) goto L_0x0242
            org.telegram.messenger.MessageObject r1 = r10.selectedObject
            org.telegram.tgnet.TLRPC$Message r1 = r1.messageOwner
            java.io.File r1 = org.telegram.messenger.FileLoader.getPathToMessage(r1)
            boolean r2 = r1.exists()
            if (r2 == 0) goto L_0x0242
            r0 = r1
        L_0x0242:
            if (r0 == 0) goto L_0x038d
            java.lang.String r1 = r0.getName()
            java.lang.String r1 = r1.toLowerCase()
            java.lang.String r2 = "attheme"
            boolean r1 = r1.endsWith(r2)
            r2 = 2131626473(0x7f0e09e9, float:1.8880183E38)
            java.lang.String r3 = "OK"
            r4 = 2131624280(0x7f0e0158, float:1.8875735E38)
            java.lang.String r5 = "AppName"
            if (r1 == 0) goto L_0x02dd
            androidx.recyclerview.widget.LinearLayoutManager r1 = r10.chatLayoutManager
            r6 = -1
            if (r1 == 0) goto L_0x0290
            int r1 = r1.findLastVisibleItemPosition()
            androidx.recyclerview.widget.LinearLayoutManager r9 = r10.chatLayoutManager
            int r9 = r9.getItemCount()
            int r9 = r9 - r7
            if (r1 >= r9) goto L_0x028e
            androidx.recyclerview.widget.LinearLayoutManager r1 = r10.chatLayoutManager
            int r1 = r1.findFirstVisibleItemPosition()
            r10.scrollToPositionOnRecreate = r1
            org.telegram.ui.Components.RecyclerListView r9 = r10.chatListView
            androidx.recyclerview.widget.RecyclerView$ViewHolder r1 = r9.findViewHolderForAdapterPosition(r1)
            org.telegram.ui.Components.RecyclerListView$Holder r1 = (org.telegram.ui.Components.RecyclerListView.Holder) r1
            if (r1 == 0) goto L_0x028b
            android.view.View r1 = r1.itemView
            int r1 = r1.getTop()
            r10.scrollToOffsetOnRecreate = r1
            goto L_0x0290
        L_0x028b:
            r10.scrollToPositionOnRecreate = r6
            goto L_0x0290
        L_0x028e:
            r10.scrollToPositionOnRecreate = r6
        L_0x0290:
            org.telegram.messenger.MessageObject r1 = r10.selectedObject
            java.lang.String r1 = r1.getDocumentName()
            org.telegram.ui.ActionBar.Theme$ThemeInfo r0 = org.telegram.ui.ActionBar.Theme.applyThemeFile(r0, r1, r8, r7)
            if (r0 == 0) goto L_0x02a6
            org.telegram.ui.ThemePreviewActivity r1 = new org.telegram.ui.ThemePreviewActivity
            r1.<init>(r0)
            r10.presentFragment(r1)
            goto L_0x038d
        L_0x02a6:
            r10.scrollToPositionOnRecreate = r6
            android.app.Activity r0 = r10.getParentActivity()
            if (r0 != 0) goto L_0x02b1
            r10.selectedObject = r8
            return
        L_0x02b1:
            org.telegram.ui.ActionBar.AlertDialog$Builder r0 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            android.app.Activity r1 = r10.getParentActivity()
            r0.<init>((android.content.Context) r1)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r0.setTitle(r1)
            r1 = 2131625797(0x7f0e0745, float:1.8878812E38)
            java.lang.String r4 = "IncorrectTheme"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r4, r1)
            r0.setMessage(r1)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setPositiveButton(r1, r8)
            org.telegram.ui.ActionBar.AlertDialog r0 = r0.create()
            r10.showDialog(r0)
            goto L_0x038d
        L_0x02dd:
            org.telegram.messenger.LocaleController r1 = org.telegram.messenger.LocaleController.getInstance()
            int r6 = r10.currentAccount
            boolean r0 = r1.applyLanguageFile(r0, r6)
            if (r0 == 0) goto L_0x02f3
            org.telegram.ui.LanguageSelectActivity r0 = new org.telegram.ui.LanguageSelectActivity
            r0.<init>()
            r10.presentFragment(r0)
            goto L_0x038d
        L_0x02f3:
            android.app.Activity r0 = r10.getParentActivity()
            if (r0 != 0) goto L_0x02fc
            r10.selectedObject = r8
            return
        L_0x02fc:
            org.telegram.ui.ActionBar.AlertDialog$Builder r0 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            android.app.Activity r1 = r10.getParentActivity()
            r0.<init>((android.content.Context) r1)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r0.setTitle(r1)
            r1 = 2131625796(0x7f0e0744, float:1.887881E38)
            java.lang.String r4 = "IncorrectLocalization"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r4, r1)
            r0.setMessage(r1)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setPositiveButton(r1, r8)
            org.telegram.ui.ActionBar.AlertDialog r0 = r0.create()
            r10.showDialog(r0)
            goto L_0x038d
        L_0x0327:
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.attachPath
            if (r0 == 0) goto L_0x033f
            int r1 = r0.length()
            if (r1 <= 0) goto L_0x033f
            java.io.File r1 = new java.io.File
            r1.<init>(r0)
            boolean r1 = r1.exists()
            if (r1 != 0) goto L_0x033f
            r0 = r8
        L_0x033f:
            if (r0 == 0) goto L_0x0347
            int r1 = r0.length()
            if (r1 != 0) goto L_0x0353
        L_0x0347:
            org.telegram.messenger.MessageObject r0 = r10.selectedObject
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.io.File r0 = org.telegram.messenger.FileLoader.getPathToMessage(r0)
            java.lang.String r0 = r0.toString()
        L_0x0353:
            org.telegram.messenger.MessageObject r1 = r10.selectedObject
            int r1 = r1.type
            if (r1 == r2) goto L_0x035b
            if (r1 != r7) goto L_0x038d
        L_0x035b:
            int r1 = android.os.Build.VERSION.SDK_INT
            if (r1 < r4) goto L_0x0377
            android.app.Activity r1 = r10.getParentActivity()
            int r1 = r1.checkSelfPermission(r5)
            if (r1 == 0) goto L_0x0377
            android.app.Activity r0 = r10.getParentActivity()
            java.lang.String[] r1 = new java.lang.String[r7]
            r1[r6] = r5
            r0.requestPermissions(r1, r3)
            r10.selectedObject = r8
            return
        L_0x0377:
            android.app.Activity r1 = r10.getParentActivity()
            org.telegram.messenger.MessageObject r3 = r10.selectedObject
            int r3 = r3.type
            if (r3 != r2) goto L_0x0382
            r6 = 1
        L_0x0382:
            org.telegram.messenger.MediaController.saveFile(r0, r1, r6, r8, r8)
            goto L_0x038d
        L_0x0386:
            java.lang.String r0 = r10.getMessageContent(r0, r6, r7)
            org.telegram.messenger.AndroidUtilities.addToClipboard(r0)
        L_0x038d:
            r10.selectedObject = r8
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChannelAdminLogActivity.processSelectedOption(int):void");
    }

    private int getMessageType(MessageObject messageObject) {
        int i;
        String str;
        if (messageObject == null || (i = messageObject.type) == 6) {
            return -1;
        }
        boolean z = true;
        if (i == 10 || i == 11 || i == 16) {
            return messageObject.getId() == 0 ? -1 : 1;
        }
        if (messageObject.isVoice()) {
            return 2;
        }
        if (messageObject.isSticker() || messageObject.isAnimatedSticker()) {
            TLRPC$InputStickerSet inputStickerSet = messageObject.getInputStickerSet();
            if (inputStickerSet instanceof TLRPC$TL_inputStickerSetID) {
                if (!MediaDataController.getInstance(this.currentAccount).isStickerPackInstalled(inputStickerSet.id)) {
                    return 7;
                }
            } else if (!(inputStickerSet instanceof TLRPC$TL_inputStickerSetShortName) || MediaDataController.getInstance(this.currentAccount).isStickerPackInstalled(inputStickerSet.short_name)) {
                return 2;
            } else {
                return 7;
            }
        } else if ((!messageObject.isRoundVideo() || (messageObject.isRoundVideo() && BuildVars.DEBUG_VERSION)) && ((messageObject.messageOwner.media instanceof TLRPC$TL_messageMediaPhoto) || messageObject.getDocument() != null || messageObject.isMusic() || messageObject.isVideo())) {
            boolean z2 = false;
            String str2 = messageObject.messageOwner.attachPath;
            if (!(str2 == null || str2.length() == 0 || !new File(messageObject.messageOwner.attachPath).exists())) {
                z2 = true;
            }
            if (z2 || !FileLoader.getPathToMessage(messageObject.messageOwner).exists()) {
                z = z2;
            }
            if (z) {
                if (messageObject.getDocument() == null || (str = messageObject.getDocument().mime_type) == null) {
                    return 4;
                }
                if (messageObject.getDocumentName().toLowerCase().endsWith("attheme")) {
                    return 10;
                }
                if (str.endsWith("/xml")) {
                    return 5;
                }
                if (str.endsWith("/png") || str.endsWith("/jpg") || str.endsWith("/jpeg")) {
                    return 6;
                }
                return 4;
            }
        } else if (messageObject.type == 12) {
            return 8;
        } else {
            if (messageObject.isMediaEmpty()) {
                return 3;
            }
        }
        return 2;
    }

    private void loadAdmins() {
        TLRPC$TL_channels_getParticipants tLRPC$TL_channels_getParticipants = new TLRPC$TL_channels_getParticipants();
        tLRPC$TL_channels_getParticipants.channel = MessagesController.getInputChannel(this.currentChat);
        tLRPC$TL_channels_getParticipants.filter = new TLRPC$TL_channelParticipantsAdmins();
        tLRPC$TL_channels_getParticipants.offset = 0;
        tLRPC$TL_channels_getParticipants.limit = 200;
        ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_channels_getParticipants, new RequestDelegate() {
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                ChannelAdminLogActivity.this.lambda$loadAdmins$11$ChannelAdminLogActivity(tLObject, tLRPC$TL_error);
            }
        }), this.classGuid);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$loadAdmins$11 */
    public /* synthetic */ void lambda$loadAdmins$11$ChannelAdminLogActivity(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLRPC$TL_error, tLObject) {
            public final /* synthetic */ TLRPC$TL_error f$1;
            public final /* synthetic */ TLObject f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                ChannelAdminLogActivity.this.lambda$null$10$ChannelAdminLogActivity(this.f$1, this.f$2);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$10 */
    public /* synthetic */ void lambda$null$10$ChannelAdminLogActivity(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject) {
        if (tLRPC$TL_error == null) {
            TLRPC$TL_channels_channelParticipants tLRPC$TL_channels_channelParticipants = (TLRPC$TL_channels_channelParticipants) tLObject;
            MessagesController.getInstance(this.currentAccount).putUsers(tLRPC$TL_channels_channelParticipants.users, false);
            ArrayList<TLRPC$ChannelParticipant> arrayList = tLRPC$TL_channels_channelParticipants.participants;
            this.admins = arrayList;
            Dialog dialog = this.visibleDialog;
            if (dialog instanceof AdminLogFilterAlert) {
                ((AdminLogFilterAlert) dialog).setCurrentAdmins(arrayList);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onRemoveFromParent() {
        MediaController.getInstance().setTextureView(this.videoTextureView, (AspectRatioFrameLayout) null, (FrameLayout) null, false);
    }

    /* access modifiers changed from: private */
    public void hideFloatingDateView(boolean z) {
        if (this.floatingDateView.getTag() != null && !this.currentFloatingDateOnScreen) {
            if (!this.scrollingFloatingDate || this.currentFloatingTopIsNotMessage) {
                this.floatingDateView.setTag((Object) null);
                if (z) {
                    AnimatorSet animatorSet = new AnimatorSet();
                    this.floatingDateAnimation = animatorSet;
                    animatorSet.setDuration(150);
                    this.floatingDateAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.floatingDateView, "alpha", new float[]{0.0f})});
                    this.floatingDateAnimation.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animator) {
                            if (animator.equals(ChannelAdminLogActivity.this.floatingDateAnimation)) {
                                AnimatorSet unused = ChannelAdminLogActivity.this.floatingDateAnimation = null;
                            }
                        }
                    });
                    this.floatingDateAnimation.setStartDelay(500);
                    this.floatingDateAnimation.start();
                    return;
                }
                AnimatorSet animatorSet2 = this.floatingDateAnimation;
                if (animatorSet2 != null) {
                    animatorSet2.cancel();
                    this.floatingDateAnimation = null;
                }
                this.floatingDateView.setAlpha(0.0f);
            }
        }
    }

    /* access modifiers changed from: private */
    public void checkScrollForLoad(boolean z) {
        int i;
        LinearLayoutManager linearLayoutManager = this.chatLayoutManager;
        if (linearLayoutManager != null && !this.paused) {
            int findFirstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
            if (findFirstVisibleItemPosition == -1) {
                i = 0;
            } else {
                i = Math.abs(this.chatLayoutManager.findLastVisibleItemPosition() - findFirstVisibleItemPosition) + 1;
            }
            if (i > 0) {
                this.chatAdapter.getItemCount();
                if (findFirstVisibleItemPosition <= (z ? 25 : 5) && !this.loading && !this.endReached) {
                    loadMessages(false);
                }
            }
        }
    }

    private void updateTextureViewPosition() {
        boolean z;
        int childCount = this.chatListView.getChildCount();
        int i = 0;
        while (true) {
            if (i >= childCount) {
                z = false;
                break;
            }
            View childAt = this.chatListView.getChildAt(i);
            if (childAt instanceof ChatMessageCell) {
                ChatMessageCell chatMessageCell = (ChatMessageCell) childAt;
                MessageObject messageObject = chatMessageCell.getMessageObject();
                if (this.roundVideoContainer != null && messageObject.isRoundVideo() && MediaController.getInstance().isPlayingMessage(messageObject)) {
                    ImageReceiver photoImage = chatMessageCell.getPhotoImage();
                    this.roundVideoContainer.setTranslationX(photoImage.getImageX());
                    this.roundVideoContainer.setTranslationY(((float) (this.fragmentView.getPaddingTop() + chatMessageCell.getTop())) + photoImage.getImageY());
                    this.fragmentView.invalidate();
                    this.roundVideoContainer.invalidate();
                    z = true;
                    break;
                }
            }
            i++;
        }
        if (this.roundVideoContainer != null) {
            MessageObject playingMessageObject = MediaController.getInstance().getPlayingMessageObject();
            if (!z) {
                this.roundVideoContainer.setTranslationY((float) ((-AndroidUtilities.roundMessageSize) - 100));
                this.fragmentView.invalidate();
                if (playingMessageObject != null && playingMessageObject.isRoundVideo()) {
                    if (this.checkTextureViewPosition || PipRoundVideoView.getInstance() != null) {
                        MediaController.getInstance().setCurrentVideoVisible(false);
                        return;
                    }
                    return;
                }
                return;
            }
            MediaController.getInstance().setCurrentVideoVisible(true);
        }
    }

    /* access modifiers changed from: private */
    public void updateMessagesVisisblePart() {
        boolean z;
        MessageObject messageObject;
        int i;
        int i2;
        RecyclerListView recyclerListView = this.chatListView;
        if (recyclerListView != null) {
            int childCount = recyclerListView.getChildCount();
            int measuredHeight = this.chatListView.getMeasuredHeight();
            int i3 = Integer.MAX_VALUE;
            int i4 = Integer.MAX_VALUE;
            int i5 = 0;
            boolean z2 = false;
            ChatMessageCell chatMessageCell = null;
            View view = null;
            View view2 = null;
            while (i5 < childCount) {
                View childAt = this.chatListView.getChildAt(i5);
                boolean z3 = childAt instanceof ChatMessageCell;
                if (z3) {
                    ChatMessageCell chatMessageCell2 = (ChatMessageCell) childAt;
                    int top = chatMessageCell2.getTop();
                    chatMessageCell2.getBottom();
                    int i6 = top >= 0 ? 0 : -top;
                    int measuredHeight2 = chatMessageCell2.getMeasuredHeight();
                    if (measuredHeight2 > measuredHeight) {
                        measuredHeight2 = i6 + measuredHeight;
                    }
                    i2 = childCount;
                    i = measuredHeight;
                    chatMessageCell2.setVisiblePart(i6, measuredHeight2 - i6, (this.contentView.getHeightWithKeyboard() - AndroidUtilities.dp(48.0f)) - this.chatListView.getTop(), 0.0f);
                    MessageObject messageObject2 = chatMessageCell2.getMessageObject();
                    if (this.roundVideoContainer != null && messageObject2.isRoundVideo() && MediaController.getInstance().isPlayingMessage(messageObject2)) {
                        ImageReceiver photoImage = chatMessageCell2.getPhotoImage();
                        this.roundVideoContainer.setTranslationX(photoImage.getImageX());
                        this.roundVideoContainer.setTranslationY(((float) (this.fragmentView.getPaddingTop() + top)) + photoImage.getImageY());
                        this.fragmentView.invalidate();
                        this.roundVideoContainer.invalidate();
                        z2 = true;
                    }
                } else {
                    i2 = childCount;
                    i = measuredHeight;
                }
                if (childAt.getBottom() > this.chatListView.getPaddingTop()) {
                    int bottom = childAt.getBottom();
                    if (bottom < i3) {
                        if (z3 || (childAt instanceof ChatActionCell)) {
                            chatMessageCell = childAt;
                        }
                        i3 = bottom;
                        view2 = childAt;
                    }
                    if ((childAt instanceof ChatActionCell) && ((ChatActionCell) childAt).getMessageObject().isDateObject) {
                        if (childAt.getAlpha() != 1.0f) {
                            childAt.setAlpha(1.0f);
                        }
                        if (bottom < i4) {
                            i4 = bottom;
                            view = childAt;
                        }
                    }
                }
                i5++;
                childCount = i2;
                measuredHeight = i;
            }
            FrameLayout frameLayout = this.roundVideoContainer;
            if (frameLayout != null) {
                if (!z2) {
                    frameLayout.setTranslationY((float) ((-AndroidUtilities.roundMessageSize) - 100));
                    this.fragmentView.invalidate();
                    MessageObject playingMessageObject = MediaController.getInstance().getPlayingMessageObject();
                    if (playingMessageObject != null && playingMessageObject.isRoundVideo() && this.checkTextureViewPosition) {
                        MediaController.getInstance().setCurrentVideoVisible(false);
                    }
                } else {
                    MediaController.getInstance().setCurrentVideoVisible(true);
                }
            }
            if (chatMessageCell != null) {
                if (chatMessageCell instanceof ChatMessageCell) {
                    messageObject = chatMessageCell.getMessageObject();
                } else {
                    messageObject = ((ChatActionCell) chatMessageCell).getMessageObject();
                }
                z = false;
                this.floatingDateView.setCustomDate(messageObject.messageOwner.date, false, true);
            } else {
                z = false;
            }
            this.currentFloatingDateOnScreen = z;
            this.currentFloatingTopIsNotMessage = !(view2 instanceof ChatMessageCell) && !(view2 instanceof ChatActionCell);
            if (view != null) {
                if (view.getTop() > this.chatListView.getPaddingTop() || this.currentFloatingTopIsNotMessage) {
                    if (view.getAlpha() != 1.0f) {
                        view.setAlpha(1.0f);
                    }
                    hideFloatingDateView(!this.currentFloatingTopIsNotMessage);
                } else {
                    if (view.getAlpha() != 0.0f) {
                        view.setAlpha(0.0f);
                    }
                    AnimatorSet animatorSet = this.floatingDateAnimation;
                    if (animatorSet != null) {
                        animatorSet.cancel();
                        this.floatingDateAnimation = null;
                    }
                    if (this.floatingDateView.getTag() == null) {
                        this.floatingDateView.setTag(1);
                    }
                    if (this.floatingDateView.getAlpha() != 1.0f) {
                        this.floatingDateView.setAlpha(1.0f);
                    }
                    this.currentFloatingDateOnScreen = true;
                }
                int bottom2 = view.getBottom() - this.chatListView.getPaddingTop();
                if (bottom2 <= this.floatingDateView.getMeasuredHeight() || bottom2 >= this.floatingDateView.getMeasuredHeight() * 2) {
                    this.floatingDateView.setTranslationY(0.0f);
                    return;
                }
                ChatActionCell chatActionCell = this.floatingDateView;
                chatActionCell.setTranslationY((float) (((-chatActionCell.getMeasuredHeight()) * 2) + bottom2));
                return;
            }
            hideFloatingDateView(true);
            this.floatingDateView.setTranslationY(0.0f);
        }
    }

    public void onTransitionAnimationStart(boolean z, boolean z2) {
        if (z) {
            this.allowAnimationIndex = getNotificationCenter().setAnimationInProgress(this.allowAnimationIndex, new int[]{NotificationCenter.chatInfoDidLoad, NotificationCenter.dialogsNeedReload, NotificationCenter.closeChats, NotificationCenter.messagesDidLoad, NotificationCenter.botKeyboardDidLoad});
        }
    }

    public void onTransitionAnimationEnd(boolean z, boolean z2) {
        if (z) {
            getNotificationCenter().onAnimationFinish(this.allowAnimationIndex);
        }
    }

    public void onResume() {
        super.onResume();
        SizeNotifierFrameLayout sizeNotifierFrameLayout = this.contentView;
        if (sizeNotifierFrameLayout != null) {
            sizeNotifierFrameLayout.onResume();
        }
        this.paused = false;
        checkScrollForLoad(false);
        if (this.wasPaused) {
            this.wasPaused = false;
            ChatActivityAdapter chatActivityAdapter = this.chatAdapter;
            if (chatActivityAdapter != null) {
                chatActivityAdapter.notifyDataSetChanged();
            }
        }
    }

    public void onPause() {
        super.onPause();
        SizeNotifierFrameLayout sizeNotifierFrameLayout = this.contentView;
        if (sizeNotifierFrameLayout != null) {
            sizeNotifierFrameLayout.onPause();
        }
        UndoView undoView2 = this.undoView;
        if (undoView2 != null) {
            undoView2.hide(true, 0);
        }
        this.paused = true;
        this.wasPaused = true;
        if (AvatarPreviewer.hasVisibleInstance()) {
            AvatarPreviewer.getInstance().close();
        }
    }

    /* access modifiers changed from: protected */
    public void onBecomeFullyHidden() {
        UndoView undoView2 = this.undoView;
        if (undoView2 != null) {
            undoView2.hide(true, 0);
        }
    }

    public void openVCard(TLRPC$User tLRPC$User, String str, String str2, String str3) {
        try {
            File sharingDirectory = AndroidUtilities.getSharingDirectory();
            sharingDirectory.mkdirs();
            File file = new File(sharingDirectory, "vcard.vcf");
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
            bufferedWriter.write(str);
            bufferedWriter.close();
            showDialog(new PhonebookShareAlert(this, (ContactsController.Contact) null, tLRPC$User, (Uri) null, file, str2, str3));
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public void onConfigurationChanged(Configuration configuration) {
        Dialog dialog = this.visibleDialog;
        if (dialog instanceof DatePickerDialog) {
            dialog.dismiss();
        }
    }

    /* access modifiers changed from: private */
    public void alertUserOpenError(MessageObject messageObject) {
        if (getParentActivity() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
            builder.setTitle(LocaleController.getString("AppName", NUM));
            builder.setPositiveButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null);
            if (messageObject.type == 3) {
                builder.setMessage(LocaleController.getString("NoPlayerInstalled", NUM));
            } else {
                builder.setMessage(LocaleController.formatString("NoHandleAppInstalled", NUM, messageObject.getDocument().mime_type));
            }
            showDialog(builder.create());
        }
    }

    /* access modifiers changed from: private */
    public void addCanBanUser(Bundle bundle, int i) {
        TLRPC$Chat tLRPC$Chat = this.currentChat;
        if (tLRPC$Chat.megagroup && this.admins != null && ChatObject.canBlockUsers(tLRPC$Chat)) {
            int i2 = 0;
            while (true) {
                if (i2 >= this.admins.size()) {
                    break;
                }
                TLRPC$ChannelParticipant tLRPC$ChannelParticipant = this.admins.get(i2);
                if (tLRPC$ChannelParticipant.user_id != i) {
                    i2++;
                } else if (!tLRPC$ChannelParticipant.can_edit) {
                    return;
                }
            }
            bundle.putInt("ban_chat_id", this.currentChat.id);
        }
    }

    public void showOpenUrlAlert(String str, boolean z) {
        if (Browser.isInternalUrl(str, (boolean[]) null) || !z) {
            Browser.openUrl((Context) getParentActivity(), str, true);
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
        builder.setTitle(LocaleController.getString("OpenUrlTitle", NUM));
        builder.setMessage(LocaleController.formatString("OpenUrlAlert2", NUM, str));
        builder.setPositiveButton(LocaleController.getString("Open", NUM), new DialogInterface.OnClickListener(str) {
            public final /* synthetic */ String f$1;

            {
                this.f$1 = r2;
            }

            public final void onClick(DialogInterface dialogInterface, int i) {
                ChannelAdminLogActivity.this.lambda$showOpenUrlAlert$12$ChannelAdminLogActivity(this.f$1, dialogInterface, i);
            }
        });
        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
        showDialog(builder.create());
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$showOpenUrlAlert$12 */
    public /* synthetic */ void lambda$showOpenUrlAlert$12$ChannelAdminLogActivity(String str, DialogInterface dialogInterface, int i) {
        Browser.openUrl((Context) getParentActivity(), str, true);
    }

    public class ChatActivityAdapter extends RecyclerView.Adapter {
        private int loadingUpRow;
        /* access modifiers changed from: private */
        public Context mContext;
        private int messagesEndRow;
        private int messagesStartRow;
        private int rowCount;

        public long getItemId(int i) {
            return -1;
        }

        public ChatActivityAdapter(Context context) {
            this.mContext = context;
        }

        public void updateRows() {
            this.rowCount = 0;
            if (!ChannelAdminLogActivity.this.messages.isEmpty()) {
                if (!ChannelAdminLogActivity.this.endReached) {
                    int i = this.rowCount;
                    this.rowCount = i + 1;
                    this.loadingUpRow = i;
                } else {
                    this.loadingUpRow = -1;
                }
                int i2 = this.rowCount;
                this.messagesStartRow = i2;
                int size = i2 + ChannelAdminLogActivity.this.messages.size();
                this.rowCount = size;
                this.messagesEndRow = size;
                return;
            }
            this.loadingUpRow = -1;
            this.messagesStartRow = -1;
            this.messagesEndRow = -1;
        }

        public int getItemCount() {
            return this.rowCount;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            ChatMessageCell chatMessageCell;
            ChatMessageCell chatMessageCell2;
            if (i == 0) {
                if (!ChannelAdminLogActivity.this.chatMessageCellsCache.isEmpty()) {
                    ChannelAdminLogActivity.this.chatMessageCellsCache.remove(0);
                    chatMessageCell2 = (View) ChannelAdminLogActivity.this.chatMessageCellsCache.get(0);
                } else {
                    chatMessageCell2 = new ChatMessageCell(this.mContext);
                }
                ChatMessageCell chatMessageCell3 = chatMessageCell2;
                chatMessageCell3.setDelegate(new ChatMessageCell.ChatMessageCellDelegate() {
                    public boolean canPerformActions() {
                        return true;
                    }

                    public /* synthetic */ boolean didLongPressChannelAvatar(ChatMessageCell chatMessageCell, TLRPC$Chat tLRPC$Chat, int i, float f, float f2) {
                        return ChatMessageCell.ChatMessageCellDelegate.CC.$default$didLongPressChannelAvatar(this, chatMessageCell, tLRPC$Chat, i, f, f2);
                    }

                    public /* synthetic */ void didPressBotButton(ChatMessageCell chatMessageCell, TLRPC$KeyboardButton tLRPC$KeyboardButton) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressBotButton(this, chatMessageCell, tLRPC$KeyboardButton);
                    }

                    public void didPressCancelSendButton(ChatMessageCell chatMessageCell) {
                    }

                    public /* synthetic */ void didPressCommentButton(ChatMessageCell chatMessageCell) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressCommentButton(this, chatMessageCell);
                    }

                    public /* synthetic */ void didPressHiddenForward(ChatMessageCell chatMessageCell) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressHiddenForward(this, chatMessageCell);
                    }

                    public /* synthetic */ void didPressHint(ChatMessageCell chatMessageCell, int i) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressHint(this, chatMessageCell, i);
                    }

                    public /* synthetic */ void didPressReaction(ChatMessageCell chatMessageCell, TLRPC$TL_reactionCount tLRPC$TL_reactionCount) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressReaction(this, chatMessageCell, tLRPC$TL_reactionCount);
                    }

                    public void didPressReplyMessage(ChatMessageCell chatMessageCell, int i) {
                    }

                    public /* synthetic */ void didPressTime(ChatMessageCell chatMessageCell) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressTime(this, chatMessageCell);
                    }

                    public void didPressViaBot(ChatMessageCell chatMessageCell, String str) {
                    }

                    public /* synthetic */ void didPressVoteButtons(ChatMessageCell chatMessageCell, ArrayList arrayList, int i, int i2, int i3) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressVoteButtons(this, chatMessageCell, arrayList, i, i2, i3);
                    }

                    public /* synthetic */ void didStartVideoStream(MessageObject messageObject) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didStartVideoStream(this, messageObject);
                    }

                    public /* synthetic */ String getAdminRank(int i) {
                        return ChatMessageCell.ChatMessageCellDelegate.CC.$default$getAdminRank(this, i);
                    }

                    public /* synthetic */ TextSelectionHelper.ChatListTextSelectionHelper getTextSelectionHelper() {
                        return ChatMessageCell.ChatMessageCellDelegate.CC.$default$getTextSelectionHelper(this);
                    }

                    public /* synthetic */ boolean hasSelectedMessages() {
                        return ChatMessageCell.ChatMessageCellDelegate.CC.$default$hasSelectedMessages(this);
                    }

                    public /* synthetic */ void needReloadPolls() {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$needReloadPolls(this);
                    }

                    public /* synthetic */ void onDiceFinished() {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$onDiceFinished(this);
                    }

                    public /* synthetic */ void setShouldNotRepeatSticker(MessageObject messageObject) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$setShouldNotRepeatSticker(this, messageObject);
                    }

                    public /* synthetic */ boolean shouldDrawThreadProgress(ChatMessageCell chatMessageCell) {
                        return ChatMessageCell.ChatMessageCellDelegate.CC.$default$shouldDrawThreadProgress(this, chatMessageCell);
                    }

                    public /* synthetic */ boolean shouldRepeatSticker(MessageObject messageObject) {
                        return ChatMessageCell.ChatMessageCellDelegate.CC.$default$shouldRepeatSticker(this, messageObject);
                    }

                    public /* synthetic */ void videoTimerReached() {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$videoTimerReached(this);
                    }

                    public void didPressSideButton(ChatMessageCell chatMessageCell) {
                        if (ChannelAdminLogActivity.this.getParentActivity() != null) {
                            ChatActivityAdapter chatActivityAdapter = ChatActivityAdapter.this;
                            ChannelAdminLogActivity.this.showDialog(ShareAlert.createShareAlert(chatActivityAdapter.mContext, chatMessageCell.getMessageObject(), (String) null, ChatObject.isChannel(ChannelAdminLogActivity.this.currentChat) && !ChannelAdminLogActivity.this.currentChat.megagroup, (String) null, false));
                        }
                    }

                    public boolean needPlayMessage(MessageObject messageObject) {
                        if (messageObject.isVoice() || messageObject.isRoundVideo()) {
                            boolean playMessage = MediaController.getInstance().playMessage(messageObject);
                            MediaController.getInstance().setVoiceMessagesPlaylist((ArrayList<MessageObject>) null, false);
                            return playMessage;
                        } else if (messageObject.isMusic()) {
                            return MediaController.getInstance().setPlaylist(ChannelAdminLogActivity.this.messages, messageObject, 0);
                        } else {
                            return false;
                        }
                    }

                    public void didPressChannelAvatar(ChatMessageCell chatMessageCell, TLRPC$Chat tLRPC$Chat, int i, float f, float f2) {
                        if (tLRPC$Chat != null && tLRPC$Chat != ChannelAdminLogActivity.this.currentChat) {
                            Bundle bundle = new Bundle();
                            bundle.putInt("chat_id", tLRPC$Chat.id);
                            if (i != 0) {
                                bundle.putInt("message_id", i);
                            }
                            if (MessagesController.getInstance(ChannelAdminLogActivity.this.currentAccount).checkCanOpenChat(bundle, ChannelAdminLogActivity.this)) {
                                ChannelAdminLogActivity.this.presentFragment(new ChatActivity(bundle), true);
                            }
                        }
                    }

                    public void didPressOther(ChatMessageCell chatMessageCell, float f, float f2) {
                        ChannelAdminLogActivity.this.createMenu(chatMessageCell);
                    }

                    public void didPressUserAvatar(ChatMessageCell chatMessageCell, TLRPC$User tLRPC$User, float f, float f2) {
                        if (tLRPC$User != null && tLRPC$User.id != UserConfig.getInstance(ChannelAdminLogActivity.this.currentAccount).getClientUserId()) {
                            openProfile(tLRPC$User);
                        }
                    }

                    public boolean didLongPressUserAvatar(ChatMessageCell chatMessageCell, TLRPC$User tLRPC$User, float f, float f2) {
                        AvatarPreviewer.Data data;
                        if (!(tLRPC$User == null || tLRPC$User.id == UserConfig.getInstance(ChannelAdminLogActivity.this.currentAccount).getClientUserId())) {
                            AvatarPreviewer.MenuItem[] menuItemArr = {AvatarPreviewer.MenuItem.OPEN_PROFILE, AvatarPreviewer.MenuItem.SEND_MESSAGE};
                            TLRPC$UserFull userFull = ChannelAdminLogActivity.this.getMessagesController().getUserFull(tLRPC$User.id);
                            if (userFull != null) {
                                data = AvatarPreviewer.Data.of(userFull, menuItemArr);
                            } else {
                                data = AvatarPreviewer.Data.of(tLRPC$User, ChannelAdminLogActivity.this.classGuid, menuItemArr);
                            }
                            if (AvatarPreviewer.canPreview(data)) {
                                AvatarPreviewer.getInstance().show((ViewGroup) ChannelAdminLogActivity.this.fragmentView, data, 
                                /*  JADX ERROR: Method code generation error
                                    jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x005d: INVOKE  
                                      (wrap: org.telegram.ui.AvatarPreviewer : 0x004a: INVOKE  (r6v2 org.telegram.ui.AvatarPreviewer) =  org.telegram.ui.AvatarPreviewer.getInstance():org.telegram.ui.AvatarPreviewer type: STATIC)
                                      (wrap: android.view.ViewGroup : 0x0056: CHECK_CAST  (r0v15 android.view.ViewGroup) = (android.view.ViewGroup) (wrap: android.view.View : 0x0052: INVOKE  (r0v14 android.view.View) = 
                                      (wrap: org.telegram.ui.ChannelAdminLogActivity : 0x0050: IGET  (r0v13 org.telegram.ui.ChannelAdminLogActivity) = 
                                      (wrap: org.telegram.ui.ChannelAdminLogActivity$ChatActivityAdapter : 0x004e: IGET  (r0v12 org.telegram.ui.ChannelAdminLogActivity$ChatActivityAdapter) = 
                                      (r3v0 'this' org.telegram.ui.ChannelAdminLogActivity$ChatActivityAdapter$1 A[THIS])
                                     org.telegram.ui.ChannelAdminLogActivity.ChatActivityAdapter.1.this$1 org.telegram.ui.ChannelAdminLogActivity$ChatActivityAdapter)
                                     org.telegram.ui.ChannelAdminLogActivity.ChatActivityAdapter.this$0 org.telegram.ui.ChannelAdminLogActivity)
                                     org.telegram.ui.ChannelAdminLogActivity.access$4800(org.telegram.ui.ChannelAdminLogActivity):android.view.View type: STATIC))
                                      (r7v4 'data' org.telegram.ui.AvatarPreviewer$Data)
                                      (wrap: org.telegram.ui.-$$Lambda$ChannelAdminLogActivity$ChatActivityAdapter$1$3k42rY7MQhpJPdBFoZgEF4Jmq5s : 0x005a: CONSTRUCTOR  (r2v1 org.telegram.ui.-$$Lambda$ChannelAdminLogActivity$ChatActivityAdapter$1$3k42rY7MQhpJPdBFoZgEF4Jmq5s) = 
                                      (r3v0 'this' org.telegram.ui.ChannelAdminLogActivity$ChatActivityAdapter$1 A[THIS])
                                      (r4v0 'chatMessageCell' org.telegram.ui.Cells.ChatMessageCell)
                                      (r5v0 'tLRPC$User' org.telegram.tgnet.TLRPC$User)
                                     call: org.telegram.ui.-$$Lambda$ChannelAdminLogActivity$ChatActivityAdapter$1$3k42rY7MQhpJPdBFoZgEF4Jmq5s.<init>(org.telegram.ui.ChannelAdminLogActivity$ChatActivityAdapter$1, org.telegram.ui.Cells.ChatMessageCell, org.telegram.tgnet.TLRPC$User):void type: CONSTRUCTOR)
                                     org.telegram.ui.AvatarPreviewer.show(android.view.ViewGroup, org.telegram.ui.AvatarPreviewer$Data, org.telegram.ui.AvatarPreviewer$Callback):void type: VIRTUAL in method: org.telegram.ui.ChannelAdminLogActivity.ChatActivityAdapter.1.didLongPressUserAvatar(org.telegram.ui.Cells.ChatMessageCell, org.telegram.tgnet.TLRPC$User, float, float):boolean, dex: classes.dex
                                    	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:256)
                                    	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
                                    	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
                                    	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
                                    	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                                    	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                                    	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:98)
                                    	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:142)
                                    	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:62)
                                    	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                                    	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                                    	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:98)
                                    	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:142)
                                    	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:62)
                                    	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                                    	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                                    	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:211)
                                    	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:204)
                                    	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:318)
                                    	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
                                    	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
                                    	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                                    	at java.util.ArrayList.forEach(ArrayList.java:1259)
                                    	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                                    	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                                    	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                                    	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                                    	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                                    	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                                    	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                                    	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                                    	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                                    	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                                    	at jadx.core.codegen.InsnGen.inlineAnonymousConstructor(InsnGen.java:676)
                                    	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:607)
                                    	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
                                    	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                                    	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                                    	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                                    	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:787)
                                    	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:728)
                                    	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
                                    	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
                                    	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
                                    	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
                                    	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
                                    	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                                    	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                                    	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:98)
                                    	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:142)
                                    	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:62)
                                    	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                                    	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                                    	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:211)
                                    	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:204)
                                    	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:318)
                                    	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
                                    	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
                                    	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                                    	at java.util.ArrayList.forEach(ArrayList.java:1259)
                                    	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                                    	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                                    	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                                    	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                                    	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                                    	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                                    	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                                    	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                                    	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                                    	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                                    	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
                                    	at jadx.core.codegen.ClassGen.addInnerClass(ClassGen.java:249)
                                    	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:238)
                                    	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                                    	at java.util.ArrayList.forEach(ArrayList.java:1259)
                                    	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                                    	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                                    	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                                    	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                                    	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                                    	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                                    	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                                    	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                                    	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                                    	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                                    	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
                                    	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:78)
                                    	at jadx.core.codegen.CodeGen.wrapCodeGen(CodeGen.java:44)
                                    	at jadx.core.codegen.CodeGen.generateJavaCode(CodeGen.java:33)
                                    	at jadx.core.codegen.CodeGen.generate(CodeGen.java:21)
                                    	at jadx.core.ProcessClass.generateCode(ProcessClass.java:61)
                                    	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:273)
                                    Caused by: jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x005a: CONSTRUCTOR  (r2v1 org.telegram.ui.-$$Lambda$ChannelAdminLogActivity$ChatActivityAdapter$1$3k42rY7MQhpJPdBFoZgEF4Jmq5s) = 
                                      (r3v0 'this' org.telegram.ui.ChannelAdminLogActivity$ChatActivityAdapter$1 A[THIS])
                                      (r4v0 'chatMessageCell' org.telegram.ui.Cells.ChatMessageCell)
                                      (r5v0 'tLRPC$User' org.telegram.tgnet.TLRPC$User)
                                     call: org.telegram.ui.-$$Lambda$ChannelAdminLogActivity$ChatActivityAdapter$1$3k42rY7MQhpJPdBFoZgEF4Jmq5s.<init>(org.telegram.ui.ChannelAdminLogActivity$ChatActivityAdapter$1, org.telegram.ui.Cells.ChatMessageCell, org.telegram.tgnet.TLRPC$User):void type: CONSTRUCTOR in method: org.telegram.ui.ChannelAdminLogActivity.ChatActivityAdapter.1.didLongPressUserAvatar(org.telegram.ui.Cells.ChatMessageCell, org.telegram.tgnet.TLRPC$User, float, float):boolean, dex: classes.dex
                                    	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:256)
                                    	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                                    	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                                    	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:787)
                                    	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:728)
                                    	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
                                    	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
                                    	... 91 more
                                    Caused by: jadx.core.utils.exceptions.JadxRuntimeException: Expected class to be processed at this point, class: org.telegram.ui.-$$Lambda$ChannelAdminLogActivity$ChatActivityAdapter$1$3k42rY7MQhpJPdBFoZgEF4Jmq5s, state: NOT_LOADED
                                    	at jadx.core.dex.nodes.ClassNode.ensureProcessed(ClassNode.java:260)
                                    	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:606)
                                    	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
                                    	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                                    	... 97 more
                                    */
                                /*
                                    this = this;
                                    r6 = 0
                                    if (r5 == 0) goto L_0x0061
                                    int r7 = r5.id
                                    org.telegram.ui.ChannelAdminLogActivity$ChatActivityAdapter r0 = org.telegram.ui.ChannelAdminLogActivity.ChatActivityAdapter.this
                                    org.telegram.ui.ChannelAdminLogActivity r0 = org.telegram.ui.ChannelAdminLogActivity.this
                                    int r0 = r0.currentAccount
                                    org.telegram.messenger.UserConfig r0 = org.telegram.messenger.UserConfig.getInstance(r0)
                                    int r0 = r0.getClientUserId()
                                    if (r7 == r0) goto L_0x0061
                                    r7 = 2
                                    org.telegram.ui.AvatarPreviewer$MenuItem[] r7 = new org.telegram.ui.AvatarPreviewer.MenuItem[r7]
                                    org.telegram.ui.AvatarPreviewer$MenuItem r0 = org.telegram.ui.AvatarPreviewer.MenuItem.OPEN_PROFILE
                                    r7[r6] = r0
                                    org.telegram.ui.AvatarPreviewer$MenuItem r0 = org.telegram.ui.AvatarPreviewer.MenuItem.SEND_MESSAGE
                                    r1 = 1
                                    r7[r1] = r0
                                    org.telegram.ui.ChannelAdminLogActivity$ChatActivityAdapter r0 = org.telegram.ui.ChannelAdminLogActivity.ChatActivityAdapter.this
                                    org.telegram.ui.ChannelAdminLogActivity r0 = org.telegram.ui.ChannelAdminLogActivity.this
                                    org.telegram.messenger.MessagesController r0 = r0.getMessagesController()
                                    int r2 = r5.id
                                    org.telegram.tgnet.TLRPC$UserFull r0 = r0.getUserFull(r2)
                                    if (r0 == 0) goto L_0x0038
                                    org.telegram.ui.AvatarPreviewer$Data r7 = org.telegram.ui.AvatarPreviewer.Data.of(r0, r7)
                                    goto L_0x0044
                                L_0x0038:
                                    org.telegram.ui.ChannelAdminLogActivity$ChatActivityAdapter r0 = org.telegram.ui.ChannelAdminLogActivity.ChatActivityAdapter.this
                                    org.telegram.ui.ChannelAdminLogActivity r0 = org.telegram.ui.ChannelAdminLogActivity.this
                                    int r0 = r0.classGuid
                                    org.telegram.ui.AvatarPreviewer$Data r7 = org.telegram.ui.AvatarPreviewer.Data.of((org.telegram.tgnet.TLRPC$User) r5, (int) r0, (org.telegram.ui.AvatarPreviewer.MenuItem[]) r7)
                                L_0x0044:
                                    boolean r0 = org.telegram.ui.AvatarPreviewer.canPreview(r7)
                                    if (r0 == 0) goto L_0x0061
                                    org.telegram.ui.AvatarPreviewer r6 = org.telegram.ui.AvatarPreviewer.getInstance()
                                    org.telegram.ui.ChannelAdminLogActivity$ChatActivityAdapter r0 = org.telegram.ui.ChannelAdminLogActivity.ChatActivityAdapter.this
                                    org.telegram.ui.ChannelAdminLogActivity r0 = org.telegram.ui.ChannelAdminLogActivity.this
                                    android.view.View r0 = r0.fragmentView
                                    android.view.ViewGroup r0 = (android.view.ViewGroup) r0
                                    org.telegram.ui.-$$Lambda$ChannelAdminLogActivity$ChatActivityAdapter$1$3k42rY7MQhpJPdBFoZgEF4Jmq5s r2 = new org.telegram.ui.-$$Lambda$ChannelAdminLogActivity$ChatActivityAdapter$1$3k42rY7MQhpJPdBFoZgEF4Jmq5s
                                    r2.<init>(r3, r4, r5)
                                    r6.show(r0, r7, r2)
                                    return r1
                                L_0x0061:
                                    return r6
                                */
                                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChannelAdminLogActivity.ChatActivityAdapter.AnonymousClass1.didLongPressUserAvatar(org.telegram.ui.Cells.ChatMessageCell, org.telegram.tgnet.TLRPC$User, float, float):boolean");
                            }

                            /* access modifiers changed from: private */
                            /* renamed from: lambda$didLongPressUserAvatar$0 */
                            public /* synthetic */ void lambda$didLongPressUserAvatar$0$ChannelAdminLogActivity$ChatActivityAdapter$1(ChatMessageCell chatMessageCell, TLRPC$User tLRPC$User, AvatarPreviewer.MenuItem menuItem) {
                                int i = AnonymousClass15.$SwitchMap$org$telegram$ui$AvatarPreviewer$MenuItem[menuItem.ordinal()];
                                if (i == 1) {
                                    openDialog(chatMessageCell, tLRPC$User);
                                } else if (i == 2) {
                                    openProfile(tLRPC$User);
                                }
                            }

                            private void openProfile(TLRPC$User tLRPC$User) {
                                Bundle bundle = new Bundle();
                                bundle.putInt("user_id", tLRPC$User.id);
                                ChannelAdminLogActivity.this.addCanBanUser(bundle, tLRPC$User.id);
                                ProfileActivity profileActivity = new ProfileActivity(bundle);
                                profileActivity.setPlayProfileAnimation(0);
                                ChannelAdminLogActivity.this.presentFragment(profileActivity);
                            }

                            private void openDialog(ChatMessageCell chatMessageCell, TLRPC$User tLRPC$User) {
                                if (tLRPC$User != null) {
                                    Bundle bundle = new Bundle();
                                    bundle.putInt("user_id", tLRPC$User.id);
                                    if (ChannelAdminLogActivity.this.getMessagesController().checkCanOpenChat(bundle, ChannelAdminLogActivity.this)) {
                                        ChannelAdminLogActivity.this.presentFragment(new ChatActivity(bundle));
                                    }
                                }
                            }

                            public void didLongPress(ChatMessageCell chatMessageCell, float f, float f2) {
                                ChannelAdminLogActivity.this.createMenu(chatMessageCell);
                            }

                            public void didPressUrl(ChatMessageCell chatMessageCell, CharacterStyle characterStyle, boolean z) {
                                TLRPC$WebPage tLRPC$WebPage;
                                if (characterStyle != null) {
                                    MessageObject messageObject = chatMessageCell.getMessageObject();
                                    if (characterStyle instanceof URLSpanMono) {
                                        ((URLSpanMono) characterStyle).copyToClipboard();
                                        Toast.makeText(ChannelAdminLogActivity.this.getParentActivity(), LocaleController.getString("TextCopied", NUM), 0).show();
                                    } else if (characterStyle instanceof URLSpanUserMention) {
                                        TLRPC$User user = MessagesController.getInstance(ChannelAdminLogActivity.this.currentAccount).getUser(Utilities.parseInt(((URLSpanUserMention) characterStyle).getURL()));
                                        if (user != null) {
                                            MessagesController.openChatOrProfileWith(user, (TLRPC$Chat) null, ChannelAdminLogActivity.this, 0, false);
                                        }
                                    } else if (characterStyle instanceof URLSpanNoUnderline) {
                                        String url = ((URLSpanNoUnderline) characterStyle).getURL();
                                        if (url.startsWith("@")) {
                                            MessagesController.getInstance(ChannelAdminLogActivity.this.currentAccount).openByUserName(url.substring(1), ChannelAdminLogActivity.this, 0);
                                        } else if (url.startsWith("#")) {
                                            DialogsActivity dialogsActivity = new DialogsActivity((Bundle) null);
                                            dialogsActivity.setSearchString(url);
                                            ChannelAdminLogActivity.this.presentFragment(dialogsActivity);
                                        }
                                    } else {
                                        String url2 = ((URLSpan) characterStyle).getURL();
                                        if (z) {
                                            BottomSheet.Builder builder = new BottomSheet.Builder(ChannelAdminLogActivity.this.getParentActivity());
                                            builder.setTitle(url2);
                                            builder.setItems(new CharSequence[]{LocaleController.getString("Open", NUM), LocaleController.getString("Copy", NUM)}, 
                                            /*  JADX ERROR: Method code generation error
                                                jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x00d1: INVOKE  
                                                  (r5v11 'builder' org.telegram.ui.ActionBar.BottomSheet$Builder)
                                                  (wrap: java.lang.CharSequence[] : ?: FILLED_NEW_ARRAY  (r6v16 java.lang.CharSequence[]) = 
                                                  (wrap: java.lang.String : 0x00bb: INVOKE  (r7v13 java.lang.String) = ("Open"), (NUM int) org.telegram.messenger.LocaleController.getString(java.lang.String, int):java.lang.String type: STATIC)
                                                  (wrap: java.lang.String : 0x00c6: INVOKE  (r7v15 java.lang.String) = ("Copy"), (NUM int) org.telegram.messenger.LocaleController.getString(java.lang.String, int):java.lang.String type: STATIC)
                                                 elemType: java.lang.CharSequence)
                                                  (wrap: org.telegram.ui.-$$Lambda$ChannelAdminLogActivity$ChatActivityAdapter$1$rbrOVgG843N56Jyq2QFQkJwMTrI : 0x00ce: CONSTRUCTOR  (r7v16 org.telegram.ui.-$$Lambda$ChannelAdminLogActivity$ChatActivityAdapter$1$rbrOVgG843N56Jyq2QFQkJwMTrI) = 
                                                  (r4v0 'this' org.telegram.ui.ChannelAdminLogActivity$ChatActivityAdapter$1 A[THIS])
                                                  (r0v5 'url2' java.lang.String)
                                                 call: org.telegram.ui.-$$Lambda$ChannelAdminLogActivity$ChatActivityAdapter$1$rbrOVgG843N56Jyq2QFQkJwMTrI.<init>(org.telegram.ui.ChannelAdminLogActivity$ChatActivityAdapter$1, java.lang.String):void type: CONSTRUCTOR)
                                                 org.telegram.ui.ActionBar.BottomSheet.Builder.setItems(java.lang.CharSequence[], android.content.DialogInterface$OnClickListener):org.telegram.ui.ActionBar.BottomSheet$Builder type: VIRTUAL in method: org.telegram.ui.ChannelAdminLogActivity.ChatActivityAdapter.1.didPressUrl(org.telegram.ui.Cells.ChatMessageCell, android.text.style.CharacterStyle, boolean):void, dex: classes.dex
                                                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:256)
                                                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
                                                	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
                                                	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
                                                	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                                                	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                                                	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:98)
                                                	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:142)
                                                	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:62)
                                                	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                                                	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                                                	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:98)
                                                	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:156)
                                                	at jadx.core.codegen.RegionGen.connectElseIf(RegionGen.java:175)
                                                	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:152)
                                                	at jadx.core.codegen.RegionGen.connectElseIf(RegionGen.java:175)
                                                	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:152)
                                                	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:62)
                                                	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                                                	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                                                	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:98)
                                                	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:142)
                                                	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:62)
                                                	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                                                	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                                                	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:211)
                                                	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:204)
                                                	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:318)
                                                	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
                                                	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
                                                	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                                                	at java.util.ArrayList.forEach(ArrayList.java:1259)
                                                	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                                                	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                                                	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                                                	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                                                	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                                                	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                                                	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                                                	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                                                	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                                                	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                                                	at jadx.core.codegen.InsnGen.inlineAnonymousConstructor(InsnGen.java:676)
                                                	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:607)
                                                	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
                                                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                                                	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                                                	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                                                	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:787)
                                                	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:728)
                                                	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
                                                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
                                                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
                                                	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
                                                	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
                                                	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                                                	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                                                	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:98)
                                                	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:142)
                                                	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:62)
                                                	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                                                	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                                                	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:211)
                                                	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:204)
                                                	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:318)
                                                	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
                                                	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
                                                	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                                                	at java.util.ArrayList.forEach(ArrayList.java:1259)
                                                	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                                                	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                                                	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                                                	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                                                	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                                                	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                                                	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                                                	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                                                	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                                                	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                                                	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
                                                	at jadx.core.codegen.ClassGen.addInnerClass(ClassGen.java:249)
                                                	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:238)
                                                	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                                                	at java.util.ArrayList.forEach(ArrayList.java:1259)
                                                	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                                                	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                                                	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                                                	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                                                	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                                                	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                                                	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                                                	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                                                	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                                                	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                                                	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
                                                	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:78)
                                                	at jadx.core.codegen.CodeGen.wrapCodeGen(CodeGen.java:44)
                                                	at jadx.core.codegen.CodeGen.generateJavaCode(CodeGen.java:33)
                                                	at jadx.core.codegen.CodeGen.generate(CodeGen.java:21)
                                                	at jadx.core.ProcessClass.generateCode(ProcessClass.java:61)
                                                	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:273)
                                                Caused by: jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x00ce: CONSTRUCTOR  (r7v16 org.telegram.ui.-$$Lambda$ChannelAdminLogActivity$ChatActivityAdapter$1$rbrOVgG843N56Jyq2QFQkJwMTrI) = 
                                                  (r4v0 'this' org.telegram.ui.ChannelAdminLogActivity$ChatActivityAdapter$1 A[THIS])
                                                  (r0v5 'url2' java.lang.String)
                                                 call: org.telegram.ui.-$$Lambda$ChannelAdminLogActivity$ChatActivityAdapter$1$rbrOVgG843N56Jyq2QFQkJwMTrI.<init>(org.telegram.ui.ChannelAdminLogActivity$ChatActivityAdapter$1, java.lang.String):void type: CONSTRUCTOR in method: org.telegram.ui.ChannelAdminLogActivity.ChatActivityAdapter.1.didPressUrl(org.telegram.ui.Cells.ChatMessageCell, android.text.style.CharacterStyle, boolean):void, dex: classes.dex
                                                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:256)
                                                	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                                                	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                                                	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:787)
                                                	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:728)
                                                	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
                                                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
                                                	... 100 more
                                                Caused by: jadx.core.utils.exceptions.JadxRuntimeException: Expected class to be processed at this point, class: org.telegram.ui.-$$Lambda$ChannelAdminLogActivity$ChatActivityAdapter$1$rbrOVgG843N56Jyq2QFQkJwMTrI, state: NOT_LOADED
                                                	at jadx.core.dex.nodes.ClassNode.ensureProcessed(ClassNode.java:260)
                                                	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:606)
                                                	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
                                                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                                                	... 106 more
                                                */
                                            /*
                                                this = this;
                                                if (r6 != 0) goto L_0x0003
                                                return
                                            L_0x0003:
                                                org.telegram.messenger.MessageObject r5 = r5.getMessageObject()
                                                boolean r0 = r6 instanceof org.telegram.ui.Components.URLSpanMono
                                                r1 = 0
                                                if (r0 == 0) goto L_0x002b
                                                org.telegram.ui.Components.URLSpanMono r6 = (org.telegram.ui.Components.URLSpanMono) r6
                                                r6.copyToClipboard()
                                                org.telegram.ui.ChannelAdminLogActivity$ChatActivityAdapter r5 = org.telegram.ui.ChannelAdminLogActivity.ChatActivityAdapter.this
                                                org.telegram.ui.ChannelAdminLogActivity r5 = org.telegram.ui.ChannelAdminLogActivity.this
                                                android.app.Activity r5 = r5.getParentActivity()
                                                r6 = 2131627606(0x7f0e0e56, float:1.8882481E38)
                                                java.lang.String r7 = "TextCopied"
                                                java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
                                                android.widget.Toast r5 = android.widget.Toast.makeText(r5, r6, r1)
                                                r5.show()
                                                goto L_0x016a
                                            L_0x002b:
                                                boolean r0 = r6 instanceof org.telegram.ui.Components.URLSpanUserMention
                                                r2 = 0
                                                if (r0 == 0) goto L_0x0055
                                                org.telegram.ui.ChannelAdminLogActivity$ChatActivityAdapter r5 = org.telegram.ui.ChannelAdminLogActivity.ChatActivityAdapter.this
                                                org.telegram.ui.ChannelAdminLogActivity r5 = org.telegram.ui.ChannelAdminLogActivity.this
                                                int r5 = r5.currentAccount
                                                org.telegram.messenger.MessagesController r5 = org.telegram.messenger.MessagesController.getInstance(r5)
                                                org.telegram.ui.Components.URLSpanUserMention r6 = (org.telegram.ui.Components.URLSpanUserMention) r6
                                                java.lang.String r6 = r6.getURL()
                                                java.lang.Integer r6 = org.telegram.messenger.Utilities.parseInt(r6)
                                                org.telegram.tgnet.TLRPC$User r5 = r5.getUser(r6)
                                                if (r5 == 0) goto L_0x016a
                                                org.telegram.ui.ChannelAdminLogActivity$ChatActivityAdapter r6 = org.telegram.ui.ChannelAdminLogActivity.ChatActivityAdapter.this
                                                org.telegram.ui.ChannelAdminLogActivity r6 = org.telegram.ui.ChannelAdminLogActivity.this
                                                org.telegram.messenger.MessagesController.openChatOrProfileWith(r5, r2, r6, r1, r1)
                                                goto L_0x016a
                                            L_0x0055:
                                                boolean r0 = r6 instanceof org.telegram.ui.Components.URLSpanNoUnderline
                                                r3 = 1
                                                if (r0 == 0) goto L_0x009a
                                                org.telegram.ui.Components.URLSpanNoUnderline r6 = (org.telegram.ui.Components.URLSpanNoUnderline) r6
                                                java.lang.String r5 = r6.getURL()
                                                java.lang.String r6 = "@"
                                                boolean r6 = r5.startsWith(r6)
                                                if (r6 == 0) goto L_0x0081
                                                org.telegram.ui.ChannelAdminLogActivity$ChatActivityAdapter r6 = org.telegram.ui.ChannelAdminLogActivity.ChatActivityAdapter.this
                                                org.telegram.ui.ChannelAdminLogActivity r6 = org.telegram.ui.ChannelAdminLogActivity.this
                                                int r6 = r6.currentAccount
                                                org.telegram.messenger.MessagesController r6 = org.telegram.messenger.MessagesController.getInstance(r6)
                                                java.lang.String r5 = r5.substring(r3)
                                                org.telegram.ui.ChannelAdminLogActivity$ChatActivityAdapter r7 = org.telegram.ui.ChannelAdminLogActivity.ChatActivityAdapter.this
                                                org.telegram.ui.ChannelAdminLogActivity r7 = org.telegram.ui.ChannelAdminLogActivity.this
                                                r6.openByUserName(r5, r7, r1)
                                                goto L_0x016a
                                            L_0x0081:
                                                java.lang.String r6 = "#"
                                                boolean r6 = r5.startsWith(r6)
                                                if (r6 == 0) goto L_0x016a
                                                org.telegram.ui.DialogsActivity r6 = new org.telegram.ui.DialogsActivity
                                                r6.<init>(r2)
                                                r6.setSearchString(r5)
                                                org.telegram.ui.ChannelAdminLogActivity$ChatActivityAdapter r5 = org.telegram.ui.ChannelAdminLogActivity.ChatActivityAdapter.this
                                                org.telegram.ui.ChannelAdminLogActivity r5 = org.telegram.ui.ChannelAdminLogActivity.this
                                                r5.presentFragment(r6)
                                                goto L_0x016a
                                            L_0x009a:
                                                r0 = r6
                                                android.text.style.URLSpan r0 = (android.text.style.URLSpan) r0
                                                java.lang.String r0 = r0.getURL()
                                                if (r7 == 0) goto L_0x00e1
                                                org.telegram.ui.ActionBar.BottomSheet$Builder r5 = new org.telegram.ui.ActionBar.BottomSheet$Builder
                                                org.telegram.ui.ChannelAdminLogActivity$ChatActivityAdapter r6 = org.telegram.ui.ChannelAdminLogActivity.ChatActivityAdapter.this
                                                org.telegram.ui.ChannelAdminLogActivity r6 = org.telegram.ui.ChannelAdminLogActivity.this
                                                android.app.Activity r6 = r6.getParentActivity()
                                                r5.<init>(r6)
                                                r5.setTitle(r0)
                                                r6 = 2
                                                java.lang.CharSequence[] r6 = new java.lang.CharSequence[r6]
                                                r7 = 2131626488(0x7f0e09f8, float:1.8880214E38)
                                                java.lang.String r2 = "Open"
                                                java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r2, r7)
                                                r6[r1] = r7
                                                r7 = 2131624987(0x7f0e041b, float:1.887717E38)
                                                java.lang.String r1 = "Copy"
                                                java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r1, r7)
                                                r6[r3] = r7
                                                org.telegram.ui.-$$Lambda$ChannelAdminLogActivity$ChatActivityAdapter$1$rbrOVgG843N56Jyq2QFQkJwMTrI r7 = new org.telegram.ui.-$$Lambda$ChannelAdminLogActivity$ChatActivityAdapter$1$rbrOVgG843N56Jyq2QFQkJwMTrI
                                                r7.<init>(r4, r0)
                                                r5.setItems(r6, r7)
                                                org.telegram.ui.ChannelAdminLogActivity$ChatActivityAdapter r6 = org.telegram.ui.ChannelAdminLogActivity.ChatActivityAdapter.this
                                                org.telegram.ui.ChannelAdminLogActivity r6 = org.telegram.ui.ChannelAdminLogActivity.this
                                                org.telegram.ui.ActionBar.BottomSheet r5 = r5.create()
                                                r6.showDialog(r5)
                                                goto L_0x016a
                                            L_0x00e1:
                                                boolean r7 = r6 instanceof org.telegram.ui.Components.URLSpanReplacement
                                                if (r7 == 0) goto L_0x00f4
                                                org.telegram.ui.ChannelAdminLogActivity$ChatActivityAdapter r5 = org.telegram.ui.ChannelAdminLogActivity.ChatActivityAdapter.this
                                                org.telegram.ui.ChannelAdminLogActivity r5 = org.telegram.ui.ChannelAdminLogActivity.this
                                                org.telegram.ui.Components.URLSpanReplacement r6 = (org.telegram.ui.Components.URLSpanReplacement) r6
                                                java.lang.String r6 = r6.getURL()
                                                r5.showOpenUrlAlert(r6, r3)
                                                goto L_0x016a
                                            L_0x00f4:
                                                boolean r7 = r6 instanceof android.text.style.URLSpan
                                                if (r7 == 0) goto L_0x0159
                                                org.telegram.tgnet.TLRPC$Message r6 = r5.messageOwner
                                                org.telegram.tgnet.TLRPC$MessageMedia r6 = r6.media
                                                boolean r7 = r6 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaWebPage
                                                if (r7 == 0) goto L_0x014d
                                                org.telegram.tgnet.TLRPC$WebPage r6 = r6.webpage
                                                if (r6 == 0) goto L_0x014d
                                                org.telegram.tgnet.TLRPC$Page r6 = r6.cached_page
                                                if (r6 == 0) goto L_0x014d
                                                java.lang.String r6 = r0.toLowerCase()
                                                org.telegram.tgnet.TLRPC$Message r7 = r5.messageOwner
                                                org.telegram.tgnet.TLRPC$MessageMedia r7 = r7.media
                                                org.telegram.tgnet.TLRPC$WebPage r7 = r7.webpage
                                                java.lang.String r7 = r7.url
                                                java.lang.String r7 = r7.toLowerCase()
                                                boolean r1 = org.telegram.messenger.browser.Browser.isTelegraphUrl(r6, r1)
                                                if (r1 != 0) goto L_0x0126
                                                java.lang.String r1 = "t.me/iv"
                                                boolean r1 = r6.contains(r1)
                                                if (r1 == 0) goto L_0x014d
                                            L_0x0126:
                                                boolean r1 = r6.contains(r7)
                                                if (r1 != 0) goto L_0x0132
                                                boolean r6 = r7.contains(r6)
                                                if (r6 == 0) goto L_0x014d
                                            L_0x0132:
                                                org.telegram.ui.ArticleViewer r6 = org.telegram.ui.ArticleViewer.getInstance()
                                                org.telegram.ui.ChannelAdminLogActivity$ChatActivityAdapter r7 = org.telegram.ui.ChannelAdminLogActivity.ChatActivityAdapter.this
                                                org.telegram.ui.ChannelAdminLogActivity r7 = org.telegram.ui.ChannelAdminLogActivity.this
                                                android.app.Activity r7 = r7.getParentActivity()
                                                org.telegram.ui.ChannelAdminLogActivity$ChatActivityAdapter r0 = org.telegram.ui.ChannelAdminLogActivity.ChatActivityAdapter.this
                                                org.telegram.ui.ChannelAdminLogActivity r0 = org.telegram.ui.ChannelAdminLogActivity.this
                                                r6.setParentActivity(r7, r0)
                                                org.telegram.ui.ArticleViewer r6 = org.telegram.ui.ArticleViewer.getInstance()
                                                r6.open(r5)
                                                return
                                            L_0x014d:
                                                org.telegram.ui.ChannelAdminLogActivity$ChatActivityAdapter r5 = org.telegram.ui.ChannelAdminLogActivity.ChatActivityAdapter.this
                                                org.telegram.ui.ChannelAdminLogActivity r5 = org.telegram.ui.ChannelAdminLogActivity.this
                                                android.app.Activity r5 = r5.getParentActivity()
                                                org.telegram.messenger.browser.Browser.openUrl((android.content.Context) r5, (java.lang.String) r0, (boolean) r3)
                                                goto L_0x016a
                                            L_0x0159:
                                                boolean r5 = r6 instanceof android.text.style.ClickableSpan
                                                if (r5 == 0) goto L_0x016a
                                                android.text.style.ClickableSpan r6 = (android.text.style.ClickableSpan) r6
                                                org.telegram.ui.ChannelAdminLogActivity$ChatActivityAdapter r5 = org.telegram.ui.ChannelAdminLogActivity.ChatActivityAdapter.this
                                                org.telegram.ui.ChannelAdminLogActivity r5 = org.telegram.ui.ChannelAdminLogActivity.this
                                                android.view.View r5 = r5.fragmentView
                                                r6.onClick(r5)
                                            L_0x016a:
                                                return
                                            */
                                            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChannelAdminLogActivity.ChatActivityAdapter.AnonymousClass1.didPressUrl(org.telegram.ui.Cells.ChatMessageCell, android.text.style.CharacterStyle, boolean):void");
                                        }

                                        /* access modifiers changed from: private */
                                        /* renamed from: lambda$didPressUrl$1 */
                                        public /* synthetic */ void lambda$didPressUrl$1$ChannelAdminLogActivity$ChatActivityAdapter$1(String str, DialogInterface dialogInterface, int i) {
                                            if (i == 0) {
                                                Browser.openUrl((Context) ChannelAdminLogActivity.this.getParentActivity(), str, true);
                                            } else if (i == 1) {
                                                if (str.startsWith("mailto:")) {
                                                    str = str.substring(7);
                                                } else if (str.startsWith("tel:")) {
                                                    str = str.substring(4);
                                                }
                                                AndroidUtilities.addToClipboard(str);
                                            }
                                        }

                                        public void needOpenWebView(String str, String str2, String str3, String str4, int i, int i2) {
                                            EmbedBottomSheet.show(ChatActivityAdapter.this.mContext, str2, str3, str4, str, i, i2, false);
                                        }

                                        /* JADX WARNING: Code restructure failed: missing block: B:49:0x0102, code lost:
                                            if (r9.exists() != false) goto L_0x0106;
                                         */
                                        /* Code decompiled incorrectly, please refer to instructions dump. */
                                        public void didPressImage(org.telegram.ui.Cells.ChatMessageCell r9, float r10, float r11) {
                                            /*
                                                r8 = this;
                                                org.telegram.messenger.MessageObject r1 = r9.getMessageObject()
                                                int r9 = r1.type
                                                r10 = 13
                                                if (r9 != r10) goto L_0x002b
                                                org.telegram.ui.ChannelAdminLogActivity$ChatActivityAdapter r9 = org.telegram.ui.ChannelAdminLogActivity.ChatActivityAdapter.this
                                                org.telegram.ui.ChannelAdminLogActivity r9 = org.telegram.ui.ChannelAdminLogActivity.this
                                                org.telegram.ui.Components.StickersAlert r10 = new org.telegram.ui.Components.StickersAlert
                                                org.telegram.ui.ChannelAdminLogActivity$ChatActivityAdapter r11 = org.telegram.ui.ChannelAdminLogActivity.ChatActivityAdapter.this
                                                org.telegram.ui.ChannelAdminLogActivity r11 = org.telegram.ui.ChannelAdminLogActivity.this
                                                android.app.Activity r3 = r11.getParentActivity()
                                                org.telegram.ui.ChannelAdminLogActivity$ChatActivityAdapter r11 = org.telegram.ui.ChannelAdminLogActivity.ChatActivityAdapter.this
                                                org.telegram.ui.ChannelAdminLogActivity r4 = org.telegram.ui.ChannelAdminLogActivity.this
                                                org.telegram.tgnet.TLRPC$InputStickerSet r5 = r1.getInputStickerSet()
                                                r6 = 0
                                                r7 = 0
                                                r2 = r10
                                                r2.<init>(r3, r4, r5, r6, r7)
                                                r9.showDialog(r10)
                                                goto L_0x01d4
                                            L_0x002b:
                                                boolean r9 = r1.isVideo()
                                                if (r9 != 0) goto L_0x01b1
                                                int r9 = r1.type
                                                r10 = 1
                                                if (r9 == r10) goto L_0x01b1
                                                if (r9 != 0) goto L_0x003e
                                                boolean r9 = r1.isWebpageDocument()
                                                if (r9 == 0) goto L_0x01b1
                                            L_0x003e:
                                                boolean r9 = r1.isGif()
                                                if (r9 == 0) goto L_0x0046
                                                goto L_0x01b1
                                            L_0x0046:
                                                int r9 = r1.type
                                                r11 = 3
                                                r0 = 0
                                                if (r9 != r11) goto L_0x00b3
                                                org.telegram.tgnet.TLRPC$Message r9 = r1.messageOwner     // Catch:{ Exception -> 0x00aa }
                                                java.lang.String r9 = r9.attachPath     // Catch:{ Exception -> 0x00aa }
                                                if (r9 == 0) goto L_0x0061
                                                int r9 = r9.length()     // Catch:{ Exception -> 0x00aa }
                                                if (r9 == 0) goto L_0x0061
                                                java.io.File r0 = new java.io.File     // Catch:{ Exception -> 0x00aa }
                                                org.telegram.tgnet.TLRPC$Message r9 = r1.messageOwner     // Catch:{ Exception -> 0x00aa }
                                                java.lang.String r9 = r9.attachPath     // Catch:{ Exception -> 0x00aa }
                                                r0.<init>(r9)     // Catch:{ Exception -> 0x00aa }
                                            L_0x0061:
                                                if (r0 == 0) goto L_0x0069
                                                boolean r9 = r0.exists()     // Catch:{ Exception -> 0x00aa }
                                                if (r9 != 0) goto L_0x006f
                                            L_0x0069:
                                                org.telegram.tgnet.TLRPC$Message r9 = r1.messageOwner     // Catch:{ Exception -> 0x00aa }
                                                java.io.File r0 = org.telegram.messenger.FileLoader.getPathToMessage(r9)     // Catch:{ Exception -> 0x00aa }
                                            L_0x006f:
                                                android.content.Intent r9 = new android.content.Intent     // Catch:{ Exception -> 0x00aa }
                                                java.lang.String r11 = "android.intent.action.VIEW"
                                                r9.<init>(r11)     // Catch:{ Exception -> 0x00aa }
                                                int r11 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x00aa }
                                                r2 = 24
                                                java.lang.String r3 = "video/mp4"
                                                if (r11 < r2) goto L_0x0094
                                                r9.setFlags(r10)     // Catch:{ Exception -> 0x00aa }
                                                org.telegram.ui.ChannelAdminLogActivity$ChatActivityAdapter r10 = org.telegram.ui.ChannelAdminLogActivity.ChatActivityAdapter.this     // Catch:{ Exception -> 0x00aa }
                                                org.telegram.ui.ChannelAdminLogActivity r10 = org.telegram.ui.ChannelAdminLogActivity.this     // Catch:{ Exception -> 0x00aa }
                                                android.app.Activity r10 = r10.getParentActivity()     // Catch:{ Exception -> 0x00aa }
                                                java.lang.String r11 = "org.telegram.messenger.beta.provider"
                                                android.net.Uri r10 = androidx.core.content.FileProvider.getUriForFile(r10, r11, r0)     // Catch:{ Exception -> 0x00aa }
                                                r9.setDataAndType(r10, r3)     // Catch:{ Exception -> 0x00aa }
                                                goto L_0x009b
                                            L_0x0094:
                                                android.net.Uri r10 = android.net.Uri.fromFile(r0)     // Catch:{ Exception -> 0x00aa }
                                                r9.setDataAndType(r10, r3)     // Catch:{ Exception -> 0x00aa }
                                            L_0x009b:
                                                org.telegram.ui.ChannelAdminLogActivity$ChatActivityAdapter r10 = org.telegram.ui.ChannelAdminLogActivity.ChatActivityAdapter.this     // Catch:{ Exception -> 0x00aa }
                                                org.telegram.ui.ChannelAdminLogActivity r10 = org.telegram.ui.ChannelAdminLogActivity.this     // Catch:{ Exception -> 0x00aa }
                                                android.app.Activity r10 = r10.getParentActivity()     // Catch:{ Exception -> 0x00aa }
                                                r11 = 500(0x1f4, float:7.0E-43)
                                                r10.startActivityForResult(r9, r11)     // Catch:{ Exception -> 0x00aa }
                                                goto L_0x01d4
                                            L_0x00aa:
                                                org.telegram.ui.ChannelAdminLogActivity$ChatActivityAdapter r9 = org.telegram.ui.ChannelAdminLogActivity.ChatActivityAdapter.this
                                                org.telegram.ui.ChannelAdminLogActivity r9 = org.telegram.ui.ChannelAdminLogActivity.this
                                                r9.alertUserOpenError(r1)
                                                goto L_0x01d4
                                            L_0x00b3:
                                                r11 = 4
                                                if (r9 != r11) goto L_0x00d3
                                                org.telegram.ui.ChannelAdminLogActivity$ChatActivityAdapter r9 = org.telegram.ui.ChannelAdminLogActivity.ChatActivityAdapter.this
                                                org.telegram.ui.ChannelAdminLogActivity r9 = org.telegram.ui.ChannelAdminLogActivity.this
                                                boolean r9 = org.telegram.messenger.AndroidUtilities.isGoogleMapsInstalled(r9)
                                                if (r9 != 0) goto L_0x00c1
                                                return
                                            L_0x00c1:
                                                org.telegram.ui.LocationActivity r9 = new org.telegram.ui.LocationActivity
                                                r10 = 0
                                                r9.<init>(r10)
                                                r9.setMessageObject(r1)
                                                org.telegram.ui.ChannelAdminLogActivity$ChatActivityAdapter r10 = org.telegram.ui.ChannelAdminLogActivity.ChatActivityAdapter.this
                                                org.telegram.ui.ChannelAdminLogActivity r10 = org.telegram.ui.ChannelAdminLogActivity.this
                                                r10.presentFragment(r9)
                                                goto L_0x01d4
                                            L_0x00d3:
                                                r11 = 9
                                                if (r9 == r11) goto L_0x00d9
                                                if (r9 != 0) goto L_0x01d4
                                            L_0x00d9:
                                                java.lang.String r9 = r1.getDocumentName()
                                                java.lang.String r9 = r9.toLowerCase()
                                                java.lang.String r11 = "attheme"
                                                boolean r9 = r9.endsWith(r11)
                                                if (r9 == 0) goto L_0x019d
                                                org.telegram.tgnet.TLRPC$Message r9 = r1.messageOwner
                                                java.lang.String r9 = r9.attachPath
                                                if (r9 == 0) goto L_0x0105
                                                int r9 = r9.length()
                                                if (r9 == 0) goto L_0x0105
                                                java.io.File r9 = new java.io.File
                                                org.telegram.tgnet.TLRPC$Message r11 = r1.messageOwner
                                                java.lang.String r11 = r11.attachPath
                                                r9.<init>(r11)
                                                boolean r11 = r9.exists()
                                                if (r11 == 0) goto L_0x0105
                                                goto L_0x0106
                                            L_0x0105:
                                                r9 = r0
                                            L_0x0106:
                                                if (r9 != 0) goto L_0x0115
                                                org.telegram.tgnet.TLRPC$Message r11 = r1.messageOwner
                                                java.io.File r11 = org.telegram.messenger.FileLoader.getPathToMessage(r11)
                                                boolean r2 = r11.exists()
                                                if (r2 == 0) goto L_0x0115
                                                r9 = r11
                                            L_0x0115:
                                                org.telegram.ui.ChannelAdminLogActivity$ChatActivityAdapter r11 = org.telegram.ui.ChannelAdminLogActivity.ChatActivityAdapter.this
                                                org.telegram.ui.ChannelAdminLogActivity r11 = org.telegram.ui.ChannelAdminLogActivity.this
                                                androidx.recyclerview.widget.LinearLayoutManager r11 = r11.chatLayoutManager
                                                r2 = -1
                                                if (r11 == 0) goto L_0x017f
                                                org.telegram.ui.ChannelAdminLogActivity$ChatActivityAdapter r11 = org.telegram.ui.ChannelAdminLogActivity.ChatActivityAdapter.this
                                                org.telegram.ui.ChannelAdminLogActivity r11 = org.telegram.ui.ChannelAdminLogActivity.this
                                                androidx.recyclerview.widget.LinearLayoutManager r11 = r11.chatLayoutManager
                                                int r11 = r11.findLastVisibleItemPosition()
                                                org.telegram.ui.ChannelAdminLogActivity$ChatActivityAdapter r3 = org.telegram.ui.ChannelAdminLogActivity.ChatActivityAdapter.this
                                                org.telegram.ui.ChannelAdminLogActivity r3 = org.telegram.ui.ChannelAdminLogActivity.this
                                                androidx.recyclerview.widget.LinearLayoutManager r3 = r3.chatLayoutManager
                                                int r3 = r3.getItemCount()
                                                int r3 = r3 - r10
                                                if (r11 >= r3) goto L_0x0178
                                                org.telegram.ui.ChannelAdminLogActivity$ChatActivityAdapter r11 = org.telegram.ui.ChannelAdminLogActivity.ChatActivityAdapter.this
                                                org.telegram.ui.ChannelAdminLogActivity r11 = org.telegram.ui.ChannelAdminLogActivity.this
                                                androidx.recyclerview.widget.LinearLayoutManager r3 = r11.chatLayoutManager
                                                int r3 = r3.findFirstVisibleItemPosition()
                                                int unused = r11.scrollToPositionOnRecreate = r3
                                                org.telegram.ui.ChannelAdminLogActivity$ChatActivityAdapter r11 = org.telegram.ui.ChannelAdminLogActivity.ChatActivityAdapter.this
                                                org.telegram.ui.ChannelAdminLogActivity r11 = org.telegram.ui.ChannelAdminLogActivity.this
                                                org.telegram.ui.Components.RecyclerListView r11 = r11.chatListView
                                                org.telegram.ui.ChannelAdminLogActivity$ChatActivityAdapter r3 = org.telegram.ui.ChannelAdminLogActivity.ChatActivityAdapter.this
                                                org.telegram.ui.ChannelAdminLogActivity r3 = org.telegram.ui.ChannelAdminLogActivity.this
                                                int r3 = r3.scrollToPositionOnRecreate
                                                androidx.recyclerview.widget.RecyclerView$ViewHolder r11 = r11.findViewHolderForAdapterPosition(r3)
                                                org.telegram.ui.Components.RecyclerListView$Holder r11 = (org.telegram.ui.Components.RecyclerListView.Holder) r11
                                                if (r11 == 0) goto L_0x0170
                                                org.telegram.ui.ChannelAdminLogActivity$ChatActivityAdapter r3 = org.telegram.ui.ChannelAdminLogActivity.ChatActivityAdapter.this
                                                org.telegram.ui.ChannelAdminLogActivity r3 = org.telegram.ui.ChannelAdminLogActivity.this
                                                android.view.View r11 = r11.itemView
                                                int r11 = r11.getTop()
                                                int unused = r3.scrollToOffsetOnRecreate = r11
                                                goto L_0x017f
                                            L_0x0170:
                                                org.telegram.ui.ChannelAdminLogActivity$ChatActivityAdapter r11 = org.telegram.ui.ChannelAdminLogActivity.ChatActivityAdapter.this
                                                org.telegram.ui.ChannelAdminLogActivity r11 = org.telegram.ui.ChannelAdminLogActivity.this
                                                int unused = r11.scrollToPositionOnRecreate = r2
                                                goto L_0x017f
                                            L_0x0178:
                                                org.telegram.ui.ChannelAdminLogActivity$ChatActivityAdapter r11 = org.telegram.ui.ChannelAdminLogActivity.ChatActivityAdapter.this
                                                org.telegram.ui.ChannelAdminLogActivity r11 = org.telegram.ui.ChannelAdminLogActivity.this
                                                int unused = r11.scrollToPositionOnRecreate = r2
                                            L_0x017f:
                                                java.lang.String r11 = r1.getDocumentName()
                                                org.telegram.ui.ActionBar.Theme$ThemeInfo r9 = org.telegram.ui.ActionBar.Theme.applyThemeFile(r9, r11, r0, r10)
                                                if (r9 == 0) goto L_0x0196
                                                org.telegram.ui.ChannelAdminLogActivity$ChatActivityAdapter r10 = org.telegram.ui.ChannelAdminLogActivity.ChatActivityAdapter.this
                                                org.telegram.ui.ChannelAdminLogActivity r10 = org.telegram.ui.ChannelAdminLogActivity.this
                                                org.telegram.ui.ThemePreviewActivity r11 = new org.telegram.ui.ThemePreviewActivity
                                                r11.<init>(r9)
                                                r10.presentFragment(r11)
                                                return
                                            L_0x0196:
                                                org.telegram.ui.ChannelAdminLogActivity$ChatActivityAdapter r9 = org.telegram.ui.ChannelAdminLogActivity.ChatActivityAdapter.this
                                                org.telegram.ui.ChannelAdminLogActivity r9 = org.telegram.ui.ChannelAdminLogActivity.this
                                                int unused = r9.scrollToPositionOnRecreate = r2
                                            L_0x019d:
                                                org.telegram.ui.ChannelAdminLogActivity$ChatActivityAdapter r9 = org.telegram.ui.ChannelAdminLogActivity.ChatActivityAdapter.this     // Catch:{ Exception -> 0x01a9 }
                                                org.telegram.ui.ChannelAdminLogActivity r9 = org.telegram.ui.ChannelAdminLogActivity.this     // Catch:{ Exception -> 0x01a9 }
                                                android.app.Activity r9 = r9.getParentActivity()     // Catch:{ Exception -> 0x01a9 }
                                                org.telegram.messenger.AndroidUtilities.openForView((org.telegram.messenger.MessageObject) r1, (android.app.Activity) r9)     // Catch:{ Exception -> 0x01a9 }
                                                goto L_0x01d4
                                            L_0x01a9:
                                                org.telegram.ui.ChannelAdminLogActivity$ChatActivityAdapter r9 = org.telegram.ui.ChannelAdminLogActivity.ChatActivityAdapter.this
                                                org.telegram.ui.ChannelAdminLogActivity r9 = org.telegram.ui.ChannelAdminLogActivity.this
                                                r9.alertUserOpenError(r1)
                                                goto L_0x01d4
                                            L_0x01b1:
                                                org.telegram.ui.PhotoViewer r9 = org.telegram.ui.PhotoViewer.getInstance()
                                                org.telegram.ui.ChannelAdminLogActivity$ChatActivityAdapter r10 = org.telegram.ui.ChannelAdminLogActivity.ChatActivityAdapter.this
                                                org.telegram.ui.ChannelAdminLogActivity r10 = org.telegram.ui.ChannelAdminLogActivity.this
                                                android.app.Activity r10 = r10.getParentActivity()
                                                r9.setParentActivity(r10)
                                                org.telegram.ui.PhotoViewer r0 = org.telegram.ui.PhotoViewer.getInstance()
                                                r2 = 0
                                                r3 = 0
                                                r5 = 0
                                                org.telegram.ui.ChannelAdminLogActivity$ChatActivityAdapter r9 = org.telegram.ui.ChannelAdminLogActivity.ChatActivityAdapter.this
                                                org.telegram.ui.ChannelAdminLogActivity r9 = org.telegram.ui.ChannelAdminLogActivity.this
                                                org.telegram.ui.PhotoViewer$PhotoViewerProvider r7 = r9.provider
                                                r0.openPhoto((org.telegram.messenger.MessageObject) r1, (org.telegram.ui.ChatActivity) r2, (long) r3, (long) r5, (org.telegram.ui.PhotoViewer.PhotoViewerProvider) r7)
                                            L_0x01d4:
                                                return
                                            */
                                            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChannelAdminLogActivity.ChatActivityAdapter.AnonymousClass1.didPressImage(org.telegram.ui.Cells.ChatMessageCell, float, float):void");
                                        }

                                        public void didPressInstantButton(ChatMessageCell chatMessageCell, int i) {
                                            TLRPC$WebPage tLRPC$WebPage;
                                            MessageObject messageObject = chatMessageCell.getMessageObject();
                                            if (i == 0) {
                                                TLRPC$MessageMedia tLRPC$MessageMedia = messageObject.messageOwner.media;
                                                if (tLRPC$MessageMedia != null && (tLRPC$WebPage = tLRPC$MessageMedia.webpage) != null && tLRPC$WebPage.cached_page != null) {
                                                    ArticleViewer.getInstance().setParentActivity(ChannelAdminLogActivity.this.getParentActivity(), ChannelAdminLogActivity.this);
                                                    ArticleViewer.getInstance().open(messageObject);
                                                }
                                            } else if (i == 5) {
                                                ChannelAdminLogActivity channelAdminLogActivity = ChannelAdminLogActivity.this;
                                                TLRPC$User user = channelAdminLogActivity.getMessagesController().getUser(Integer.valueOf(messageObject.messageOwner.media.user_id));
                                                TLRPC$MessageMedia tLRPC$MessageMedia2 = messageObject.messageOwner.media;
                                                channelAdminLogActivity.openVCard(user, tLRPC$MessageMedia2.vcard, tLRPC$MessageMedia2.first_name, tLRPC$MessageMedia2.last_name);
                                            } else {
                                                TLRPC$MessageMedia tLRPC$MessageMedia3 = messageObject.messageOwner.media;
                                                if (tLRPC$MessageMedia3 != null && tLRPC$MessageMedia3.webpage != null) {
                                                    Browser.openUrl((Context) ChannelAdminLogActivity.this.getParentActivity(), messageObject.messageOwner.media.webpage.url);
                                                }
                                            }
                                        }
                                    });
                                    chatMessageCell3.setAllowAssistant(true);
                                    chatMessageCell = chatMessageCell2;
                                } else if (i == 1) {
                                    AnonymousClass2 r4 = new ChatActionCell(this, this.mContext) {
                                        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
                                            super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
                                            accessibilityNodeInfo.setVisibleToUser(true);
                                        }
                                    };
                                    r4.setDelegate(new ChatActionCell.ChatActionCellDelegate() {
                                        public void didPressReplyMessage(ChatActionCell chatActionCell, int i) {
                                        }

                                        public void didClickImage(ChatActionCell chatActionCell) {
                                            MessageObject messageObject = chatActionCell.getMessageObject();
                                            PhotoViewer.getInstance().setParentActivity(ChannelAdminLogActivity.this.getParentActivity());
                                            TLRPC$PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(messageObject.photoThumbs, 640);
                                            if (closestPhotoSizeWithSize != null) {
                                                PhotoViewer.getInstance().openPhoto(closestPhotoSizeWithSize.location, ImageLocation.getForPhoto(closestPhotoSizeWithSize, messageObject.messageOwner.action.photo), ChannelAdminLogActivity.this.provider);
                                                return;
                                            }
                                            PhotoViewer.getInstance().openPhoto(messageObject, (ChatActivity) null, 0, 0, ChannelAdminLogActivity.this.provider);
                                        }

                                        public void didLongPress(ChatActionCell chatActionCell, float f, float f2) {
                                            ChannelAdminLogActivity.this.createMenu(chatActionCell);
                                        }

                                        public void needOpenUserProfile(int i) {
                                            if (i < 0) {
                                                Bundle bundle = new Bundle();
                                                bundle.putInt("chat_id", -i);
                                                if (MessagesController.getInstance(ChannelAdminLogActivity.this.currentAccount).checkCanOpenChat(bundle, ChannelAdminLogActivity.this)) {
                                                    ChannelAdminLogActivity.this.presentFragment(new ChatActivity(bundle), true);
                                                }
                                            } else if (i != UserConfig.getInstance(ChannelAdminLogActivity.this.currentAccount).getClientUserId()) {
                                                Bundle bundle2 = new Bundle();
                                                bundle2.putInt("user_id", i);
                                                ChannelAdminLogActivity.this.addCanBanUser(bundle2, i);
                                                ProfileActivity profileActivity = new ProfileActivity(bundle2);
                                                profileActivity.setPlayProfileAnimation(0);
                                                ChannelAdminLogActivity.this.presentFragment(profileActivity);
                                            }
                                        }
                                    });
                                    chatMessageCell = r4;
                                } else if (i == 2) {
                                    chatMessageCell = new ChatUnreadCell(this.mContext);
                                } else {
                                    chatMessageCell = i == 4 ? new ChatLoadingCell(this.mContext) : null;
                                }
                                chatMessageCell.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
                                return new RecyclerListView.Holder(chatMessageCell);
                            }

                            /* JADX WARNING: Code restructure failed: missing block: B:33:0x00d4, code lost:
                                if (java.lang.Math.abs(r11.messageOwner.date - r0.messageOwner.date) <= 300) goto L_0x00d8;
                             */
                            /* JADX WARNING: Removed duplicated region for block: B:26:0x0099  */
                            /* Code decompiled incorrectly, please refer to instructions dump. */
                            public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder r11, int r12) {
                                /*
                                    r10 = this;
                                    int r0 = r10.loadingUpRow
                                    r1 = 0
                                    r2 = 1
                                    if (r12 != r0) goto L_0x0018
                                    android.view.View r11 = r11.itemView
                                    org.telegram.ui.Cells.ChatLoadingCell r11 = (org.telegram.ui.Cells.ChatLoadingCell) r11
                                    org.telegram.ui.ChannelAdminLogActivity r12 = org.telegram.ui.ChannelAdminLogActivity.this
                                    int r12 = r12.loadsCount
                                    if (r12 <= r2) goto L_0x0013
                                    r1 = 1
                                L_0x0013:
                                    r11.setProgressVisible(r1)
                                    goto L_0x00f1
                                L_0x0018:
                                    int r0 = r10.messagesStartRow
                                    if (r12 < r0) goto L_0x00f1
                                    int r0 = r10.messagesEndRow
                                    if (r12 >= r0) goto L_0x00f1
                                    org.telegram.ui.ChannelAdminLogActivity r0 = org.telegram.ui.ChannelAdminLogActivity.this
                                    java.util.ArrayList<org.telegram.messenger.MessageObject> r0 = r0.messages
                                    int r3 = r0.size()
                                    int r4 = r10.messagesStartRow
                                    int r4 = r12 - r4
                                    int r3 = r3 - r4
                                    int r3 = r3 - r2
                                    java.lang.Object r0 = r0.get(r3)
                                    org.telegram.messenger.MessageObject r0 = (org.telegram.messenger.MessageObject) r0
                                    android.view.View r3 = r11.itemView
                                    boolean r4 = r3 instanceof org.telegram.ui.Cells.ChatMessageCell
                                    if (r4 == 0) goto L_0x00e3
                                    org.telegram.ui.Cells.ChatMessageCell r3 = (org.telegram.ui.Cells.ChatMessageCell) r3
                                    r3.isChat = r2
                                    int r4 = r12 + 1
                                    int r5 = r10.getItemViewType(r4)
                                    int r6 = r12 + -1
                                    int r6 = r10.getItemViewType(r6)
                                    org.telegram.tgnet.TLRPC$Message r7 = r0.messageOwner
                                    org.telegram.tgnet.TLRPC$ReplyMarkup r7 = r7.reply_markup
                                    boolean r7 = r7 instanceof org.telegram.tgnet.TLRPC$TL_replyInlineMarkup
                                    r8 = 300(0x12c, float:4.2E-43)
                                    if (r7 != 0) goto L_0x0092
                                    int r7 = r11.getItemViewType()
                                    if (r5 != r7) goto L_0x0092
                                    org.telegram.ui.ChannelAdminLogActivity r5 = org.telegram.ui.ChannelAdminLogActivity.this
                                    java.util.ArrayList<org.telegram.messenger.MessageObject> r5 = r5.messages
                                    int r7 = r5.size()
                                    int r9 = r10.messagesStartRow
                                    int r4 = r4 - r9
                                    int r7 = r7 - r4
                                    int r7 = r7 - r2
                                    java.lang.Object r4 = r5.get(r7)
                                    org.telegram.messenger.MessageObject r4 = (org.telegram.messenger.MessageObject) r4
                                    boolean r5 = r4.isOutOwner()
                                    boolean r7 = r0.isOutOwner()
                                    if (r5 != r7) goto L_0x0092
                                    int r5 = r4.getFromChatId()
                                    int r7 = r0.getFromChatId()
                                    if (r5 != r7) goto L_0x0092
                                    org.telegram.tgnet.TLRPC$Message r4 = r4.messageOwner
                                    int r4 = r4.date
                                    org.telegram.tgnet.TLRPC$Message r5 = r0.messageOwner
                                    int r5 = r5.date
                                    int r4 = r4 - r5
                                    int r4 = java.lang.Math.abs(r4)
                                    if (r4 > r8) goto L_0x0092
                                    r4 = 1
                                    goto L_0x0093
                                L_0x0092:
                                    r4 = 0
                                L_0x0093:
                                    int r11 = r11.getItemViewType()
                                    if (r6 != r11) goto L_0x00d7
                                    org.telegram.ui.ChannelAdminLogActivity r11 = org.telegram.ui.ChannelAdminLogActivity.this
                                    java.util.ArrayList<org.telegram.messenger.MessageObject> r11 = r11.messages
                                    int r5 = r11.size()
                                    int r6 = r10.messagesStartRow
                                    int r12 = r12 - r6
                                    int r5 = r5 - r12
                                    java.lang.Object r11 = r11.get(r5)
                                    org.telegram.messenger.MessageObject r11 = (org.telegram.messenger.MessageObject) r11
                                    org.telegram.tgnet.TLRPC$Message r12 = r11.messageOwner
                                    org.telegram.tgnet.TLRPC$ReplyMarkup r12 = r12.reply_markup
                                    boolean r12 = r12 instanceof org.telegram.tgnet.TLRPC$TL_replyInlineMarkup
                                    if (r12 != 0) goto L_0x00d7
                                    boolean r12 = r11.isOutOwner()
                                    boolean r5 = r0.isOutOwner()
                                    if (r12 != r5) goto L_0x00d7
                                    int r12 = r11.getFromChatId()
                                    int r5 = r0.getFromChatId()
                                    if (r12 != r5) goto L_0x00d7
                                    org.telegram.tgnet.TLRPC$Message r11 = r11.messageOwner
                                    int r11 = r11.date
                                    org.telegram.tgnet.TLRPC$Message r12 = r0.messageOwner
                                    int r12 = r12.date
                                    int r11 = r11 - r12
                                    int r11 = java.lang.Math.abs(r11)
                                    if (r11 > r8) goto L_0x00d7
                                    goto L_0x00d8
                                L_0x00d7:
                                    r2 = 0
                                L_0x00d8:
                                    r11 = 0
                                    r3.setMessageObject(r0, r11, r4, r2)
                                    r3.setHighlighted(r1)
                                    r3.setHighlightedText(r11)
                                    goto L_0x00f1
                                L_0x00e3:
                                    boolean r11 = r3 instanceof org.telegram.ui.Cells.ChatActionCell
                                    if (r11 == 0) goto L_0x00f1
                                    org.telegram.ui.Cells.ChatActionCell r3 = (org.telegram.ui.Cells.ChatActionCell) r3
                                    r3.setMessageObject(r0)
                                    r11 = 1065353216(0x3var_, float:1.0)
                                    r3.setAlpha(r11)
                                L_0x00f1:
                                    return
                                */
                                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChannelAdminLogActivity.ChatActivityAdapter.onBindViewHolder(androidx.recyclerview.widget.RecyclerView$ViewHolder, int):void");
                            }

                            public int getItemViewType(int i) {
                                if (i < this.messagesStartRow || i >= this.messagesEndRow) {
                                    return 4;
                                }
                                ArrayList<MessageObject> arrayList = ChannelAdminLogActivity.this.messages;
                                return arrayList.get((arrayList.size() - (i - this.messagesStartRow)) - 1).contentType;
                            }

                            public void onViewAttachedToWindow(RecyclerView.ViewHolder viewHolder) {
                                View view = viewHolder.itemView;
                                if (view instanceof ChatMessageCell) {
                                    final ChatMessageCell chatMessageCell = (ChatMessageCell) view;
                                    chatMessageCell.getMessageObject();
                                    chatMessageCell.setBackgroundDrawable((Drawable) null);
                                    chatMessageCell.setCheckPressed(true, false);
                                    chatMessageCell.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                                        public boolean onPreDraw() {
                                            chatMessageCell.getViewTreeObserver().removeOnPreDrawListener(this);
                                            int measuredHeight = ChannelAdminLogActivity.this.chatListView.getMeasuredHeight();
                                            int top = chatMessageCell.getTop();
                                            chatMessageCell.getBottom();
                                            int i = top >= 0 ? 0 : -top;
                                            int measuredHeight2 = chatMessageCell.getMeasuredHeight();
                                            if (measuredHeight2 > measuredHeight) {
                                                measuredHeight2 = i + measuredHeight;
                                            }
                                            chatMessageCell.setVisiblePart(i, measuredHeight2 - i, (ChannelAdminLogActivity.this.contentView.getHeightWithKeyboard() - AndroidUtilities.dp(48.0f)) - ChannelAdminLogActivity.this.chatListView.getTop(), 0.0f);
                                            return true;
                                        }
                                    });
                                    chatMessageCell.setHighlighted(false);
                                }
                            }

                            public void notifyDataSetChanged() {
                                updateRows();
                                try {
                                    super.notifyDataSetChanged();
                                } catch (Exception e) {
                                    FileLog.e((Throwable) e);
                                }
                            }

                            public void notifyItemChanged(int i) {
                                updateRows();
                                try {
                                    super.notifyItemChanged(i);
                                } catch (Exception e) {
                                    FileLog.e((Throwable) e);
                                }
                            }

                            public void notifyItemRangeChanged(int i, int i2) {
                                updateRows();
                                try {
                                    super.notifyItemRangeChanged(i, i2);
                                } catch (Exception e) {
                                    FileLog.e((Throwable) e);
                                }
                            }

                            public void notifyItemMoved(int i, int i2) {
                                updateRows();
                                try {
                                    super.notifyItemMoved(i, i2);
                                } catch (Exception e) {
                                    FileLog.e((Throwable) e);
                                }
                            }

                            public void notifyItemRangeInserted(int i, int i2) {
                                updateRows();
                                try {
                                    super.notifyItemRangeInserted(i, i2);
                                } catch (Exception e) {
                                    FileLog.e((Throwable) e);
                                }
                            }

                            public void notifyItemRemoved(int i) {
                                updateRows();
                                try {
                                    super.notifyItemRemoved(i);
                                } catch (Exception e) {
                                    FileLog.e((Throwable) e);
                                }
                            }

                            public void notifyItemRangeRemoved(int i, int i2) {
                                updateRows();
                                try {
                                    super.notifyItemRangeRemoved(i, i2);
                                } catch (Exception e) {
                                    FileLog.e((Throwable) e);
                                }
                            }
                        }

                        /* renamed from: org.telegram.ui.ChannelAdminLogActivity$15  reason: invalid class name */
                        static /* synthetic */ class AnonymousClass15 {
                            static final /* synthetic */ int[] $SwitchMap$org$telegram$ui$AvatarPreviewer$MenuItem;

                            /* JADX WARNING: Can't wrap try/catch for region: R(6:0|1|2|3|4|6) */
                            /* JADX WARNING: Code restructure failed: missing block: B:7:?, code lost:
                                return;
                             */
                            /* JADX WARNING: Failed to process nested try/catch */
                            /* JADX WARNING: Missing exception handler attribute for start block: B:3:0x0012 */
                            static {
                                /*
                                    org.telegram.ui.AvatarPreviewer$MenuItem[] r0 = org.telegram.ui.AvatarPreviewer.MenuItem.values()
                                    int r0 = r0.length
                                    int[] r0 = new int[r0]
                                    $SwitchMap$org$telegram$ui$AvatarPreviewer$MenuItem = r0
                                    org.telegram.ui.AvatarPreviewer$MenuItem r1 = org.telegram.ui.AvatarPreviewer.MenuItem.SEND_MESSAGE     // Catch:{ NoSuchFieldError -> 0x0012 }
                                    int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0012 }
                                    r2 = 1
                                    r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0012 }
                                L_0x0012:
                                    int[] r0 = $SwitchMap$org$telegram$ui$AvatarPreviewer$MenuItem     // Catch:{ NoSuchFieldError -> 0x001d }
                                    org.telegram.ui.AvatarPreviewer$MenuItem r1 = org.telegram.ui.AvatarPreviewer.MenuItem.OPEN_PROFILE     // Catch:{ NoSuchFieldError -> 0x001d }
                                    int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x001d }
                                    r2 = 2
                                    r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x001d }
                                L_0x001d:
                                    return
                                */
                                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChannelAdminLogActivity.AnonymousClass15.<clinit>():void");
                            }
                        }

                        public ArrayList<ThemeDescription> getThemeDescriptions() {
                            ArrayList<ThemeDescription> arrayList = new ArrayList<>();
                            arrayList.add(new ThemeDescription(this.fragmentView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_wallpaper"));
                            arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
                            arrayList.add(new ThemeDescription(this.chatListView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
                            arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultIcon"));
                            arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSelector"));
                            arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUBACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSubmenuBackground"));
                            arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSubmenuItem"));
                            arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM | ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSubmenuItemIcon"));
                            arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
                            arrayList.add(new ThemeDescription(this.chatListView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
                            arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultIcon"));
                            arrayList.add(new ThemeDescription(this.avatarContainer.getTitleTextView(), ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultTitle"));
                            arrayList.add(new ThemeDescription((View) this.avatarContainer.getSubtitleTextView(), ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, new Paint[]{Theme.chat_statusPaint, Theme.chat_statusRecordPaint}, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSubtitle", (Object) null));
                            arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSelector"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, Theme.avatarDrawables, (ThemeDescription.ThemeDescriptionDelegate) null, "avatar_text"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "avatar_backgroundRed"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "avatar_backgroundOrange"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "avatar_backgroundViolet"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "avatar_backgroundGreen"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "avatar_backgroundCyan"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "avatar_backgroundBlue"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "avatar_backgroundPink"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "avatar_nameInMessageRed"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "avatar_nameInMessageOrange"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "avatar_nameInMessageViolet"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "avatar_nameInMessageGreen"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "avatar_nameInMessageCyan"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "avatar_nameInMessageBlue"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "avatar_nameInMessagePink"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgInDrawable, Theme.chat_msgInMediaDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inBubble"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgInSelectedDrawable, Theme.chat_msgInMediaSelectedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inBubbleSelected"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, Theme.chat_msgInDrawable.getShadowDrawables(), (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inBubbleShadow"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, Theme.chat_msgInMediaDrawable.getShadowDrawables(), (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inBubbleShadow"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, Theme.chat_msgOutDrawable.getShadowDrawables(), (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outBubbleShadow"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, Theme.chat_msgOutMediaDrawable.getShadowDrawables(), (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outBubbleShadow"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgOutDrawable, Theme.chat_msgOutMediaDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outBubble"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgOutDrawable, Theme.chat_msgOutMediaDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outBubbleGradient"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgOutSelectedDrawable, Theme.chat_msgOutMediaSelectedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outBubbleSelected"));
                            arrayList.add(new ThemeDescription(this.chatListView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{ChatActionCell.class}, Theme.chat_actionTextPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_serviceText"));
                            arrayList.add(new ThemeDescription(this.chatListView, ThemeDescription.FLAG_LINKCOLOR, new Class[]{ChatActionCell.class}, Theme.chat_actionTextPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_serviceLink"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_shareIconDrawable, Theme.chat_botInlineDrawable, Theme.chat_botLinkDrawalbe, Theme.chat_goIconDrawable, Theme.chat_commentStickerDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_serviceIcon"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class, ChatActionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_serviceBackground"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class, ChatActionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_serviceBackgroundSelected"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_messageTextIn"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_messageTextOut"));
                            arrayList.add(new ThemeDescription((View) this.chatListView, ThemeDescription.FLAG_LINKCOLOR, new Class[]{ChatMessageCell.class}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_messageLinkIn", (Object) null));
                            arrayList.add(new ThemeDescription((View) this.chatListView, ThemeDescription.FLAG_LINKCOLOR, new Class[]{ChatMessageCell.class}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_messageLinkOut", (Object) null));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgOutCheckDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outSentCheck"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgOutCheckSelectedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outSentCheckSelected"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgOutCheckReadDrawable, Theme.chat_msgOutHalfCheckDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outSentCheckRead"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgOutCheckReadSelectedDrawable, Theme.chat_msgOutHalfCheckSelectedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outSentCheckReadSelected"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgOutClockDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outSentClock"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgOutSelectedClockDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outSentClockSelected"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgInClockDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inSentClock"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgInSelectedClockDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inSentClockSelected"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgMediaCheckDrawable, Theme.chat_msgMediaHalfCheckDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_mediaSentCheck"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgStickerHalfCheckDrawable, Theme.chat_msgStickerCheckDrawable, Theme.chat_msgStickerClockDrawable, Theme.chat_msgStickerViewsDrawable, Theme.chat_msgStickerRepliesDrawable, Theme.chat_msgStickerPinnedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_serviceText"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgMediaClockDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_mediaSentClock"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgOutViewsDrawable, Theme.chat_msgOutRepliesDrawable, Theme.chat_msgOutPinnedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outViews"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgOutViewsSelectedDrawable, Theme.chat_msgOutRepliesSelectedDrawable, Theme.chat_msgOutPinnedSelectedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outViewsSelected"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgInViewsDrawable, Theme.chat_msgInRepliesDrawable, Theme.chat_msgInPinnedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inViews"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgInViewsSelectedDrawable, Theme.chat_msgInRepliesSelectedDrawable, Theme.chat_msgInPinnedSelectedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inViewsSelected"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgMediaViewsDrawable, Theme.chat_msgMediaRepliesDrawable, Theme.chat_msgMediaPinnedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_mediaViews"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgOutMenuDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outMenu"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgOutMenuSelectedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outMenuSelected"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgInMenuDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inMenu"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgInMenuSelectedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inMenuSelected"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgMediaMenuDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_mediaMenu"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgOutInstantDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outInstant"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgInInstantDrawable, Theme.chat_commentDrawable, Theme.chat_commentArrowDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inInstant"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, Theme.chat_msgOutCallDrawable, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outInstant"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, Theme.chat_msgOutCallSelectedDrawable, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outInstantSelected"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, Theme.chat_msgInCallDrawable, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inInstant"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, Theme.chat_msgInCallSelectedDrawable, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inInstantSelected"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgCallUpGreenDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outUpCall"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgCallDownRedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inUpCall"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgCallDownGreenDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inDownCall"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, Theme.chat_msgErrorPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_sentError"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgErrorDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_sentErrorIcon"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, Theme.chat_durationPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_previewDurationText"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, Theme.chat_gamePaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_previewGameText"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inPreviewInstantText"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outPreviewInstantText"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inPreviewInstantSelectedText"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outPreviewInstantSelectedText"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, Theme.chat_deleteProgressPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_secretTimeText"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_stickerNameText"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, Theme.chat_botButtonPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_botButtonText"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, Theme.chat_botProgressPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_botProgress"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inForwardedNameText"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outForwardedNameText"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inViaBotNameText"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outViaBotNameText"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_stickerViaBotNameText"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inReplyLine"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outReplyLine"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_stickerReplyLine"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inReplyNameText"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outReplyNameText"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_stickerReplyNameText"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inReplyMessageText"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outReplyMessageText"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inReplyMediaMessageText"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outReplyMediaMessageText"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inReplyMediaMessageSelectedText"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outReplyMediaMessageSelectedText"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_stickerReplyMessageText"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inPreviewLine"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outPreviewLine"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inSiteNameText"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outSiteNameText"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inContactNameText"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outContactNameText"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inContactPhoneText"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outContactPhoneText"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_mediaProgress"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inAudioProgress"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outAudioProgress"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inAudioSelectedProgress"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outAudioSelectedProgress"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_mediaTimeText"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inTimeText"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outTimeText"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inTimeSelectedText"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outTimeSelectedText"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inAudioPerfomerText"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outAudioPerfomerText"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inAudioTitleText"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outAudioTitleText"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inAudioDurationText"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outAudioDurationText"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inAudioDurationSelectedText"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outAudioDurationSelectedText"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inAudioSeekbar"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outAudioSeekbar"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inAudioSeekbarSelected"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outAudioSeekbarSelected"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inAudioSeekbarFill"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inAudioCacheSeekbar"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outAudioSeekbarFill"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outAudioCacheSeekbar"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inVoiceSeekbar"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outVoiceSeekbar"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inVoiceSeekbarSelected"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outVoiceSeekbarSelected"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inVoiceSeekbarFill"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outVoiceSeekbarFill"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inFileProgress"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outFileProgress"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inFileProgressSelected"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outFileProgressSelected"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inFileNameText"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outFileNameText"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inFileInfoText"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outFileInfoText"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inFileInfoSelectedText"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outFileInfoSelectedText"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inFileBackground"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outFileBackground"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inFileBackgroundSelected"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outFileBackgroundSelected"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inVenueInfoText"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outVenueInfoText"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inVenueInfoSelectedText"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outVenueInfoSelectedText"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_mediaInfoText"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, Theme.chat_urlPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_linkSelectBackground"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, Theme.chat_textSearchSelectionPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_textSelectBackground"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outLoader"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outMediaIcon"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outLoaderSelected"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outMediaIconSelected"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inLoader"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inMediaIcon"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inLoaderSelected"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inMediaIconSelected"));
                            Drawable[][] drawableArr = Theme.chat_photoStatesDrawables;
                            arrayList.add(new ThemeDescription(this.chatListView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{drawableArr[0][0], drawableArr[1][0], drawableArr[2][0], drawableArr[3][0]}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_mediaLoaderPhoto"));
                            Drawable[][] drawableArr2 = Theme.chat_photoStatesDrawables;
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{drawableArr2[0][0], drawableArr2[1][0], drawableArr2[2][0], drawableArr2[3][0]}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_mediaLoaderPhotoIcon"));
                            Drawable[][] drawableArr3 = Theme.chat_photoStatesDrawables;
                            arrayList.add(new ThemeDescription(this.chatListView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{drawableArr3[0][1], drawableArr3[1][1], drawableArr3[2][1], drawableArr3[3][1]}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_mediaLoaderPhotoSelected"));
                            Drawable[][] drawableArr4 = Theme.chat_photoStatesDrawables;
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{drawableArr4[0][1], drawableArr4[1][1], drawableArr4[2][1], drawableArr4[3][1]}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_mediaLoaderPhotoIconSelected"));
                            Drawable[][] drawableArr5 = Theme.chat_photoStatesDrawables;
                            arrayList.add(new ThemeDescription(this.chatListView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{drawableArr5[7][0], drawableArr5[8][0]}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outLoaderPhoto"));
                            Drawable[][] drawableArr6 = Theme.chat_photoStatesDrawables;
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{drawableArr6[7][0], drawableArr6[8][0]}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outLoaderPhotoIcon"));
                            Drawable[][] drawableArr7 = Theme.chat_photoStatesDrawables;
                            arrayList.add(new ThemeDescription(this.chatListView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{drawableArr7[7][1], drawableArr7[8][1]}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outLoaderPhotoSelected"));
                            Drawable[][] drawableArr8 = Theme.chat_photoStatesDrawables;
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{drawableArr8[7][1], drawableArr8[8][1]}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outLoaderPhotoIconSelected"));
                            Drawable[][] drawableArr9 = Theme.chat_photoStatesDrawables;
                            arrayList.add(new ThemeDescription(this.chatListView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{drawableArr9[10][0], drawableArr9[11][0]}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inLoaderPhoto"));
                            Drawable[][] drawableArr10 = Theme.chat_photoStatesDrawables;
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{drawableArr10[10][0], drawableArr10[11][0]}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inLoaderPhotoIcon"));
                            Drawable[][] drawableArr11 = Theme.chat_photoStatesDrawables;
                            arrayList.add(new ThemeDescription(this.chatListView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{drawableArr11[10][1], drawableArr11[11][1]}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inLoaderPhotoSelected"));
                            Drawable[][] drawableArr12 = Theme.chat_photoStatesDrawables;
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{drawableArr12[10][1], drawableArr12[11][1]}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inLoaderPhotoIconSelected"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_photoStatesDrawables[9][0]}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outFileIcon"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_photoStatesDrawables[9][1]}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outFileSelectedIcon"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_photoStatesDrawables[12][0]}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inFileIcon"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_photoStatesDrawables[12][1]}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inFileSelectedIcon"));
                            arrayList.add(new ThemeDescription(this.chatListView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_contactDrawable[0]}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inContactBackground"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_contactDrawable[0]}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inContactIcon"));
                            arrayList.add(new ThemeDescription(this.chatListView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_contactDrawable[1]}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outContactBackground"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_contactDrawable[1]}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outContactIcon"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inLocationBackground"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_locationDrawable[0]}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inLocationIcon"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outLocationBackground"));
                            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_locationDrawable[1]}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outLocationIcon"));
                            arrayList.add(new ThemeDescription(this.bottomOverlayChat, 0, (Class[]) null, Theme.chat_composeBackgroundPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_messagePanelBackground"));
                            arrayList.add(new ThemeDescription(this.bottomOverlayChat, 0, (Class[]) null, (Paint) null, new Drawable[]{Theme.chat_composeShadowDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_messagePanelShadow"));
                            arrayList.add(new ThemeDescription(this.bottomOverlayChatText, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_fieldOverlayText"));
                            arrayList.add(new ThemeDescription(this.emptyView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_serviceText"));
                            arrayList.add(new ThemeDescription(this.progressBar, ThemeDescription.FLAG_PROGRESSBAR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_serviceText"));
                            arrayList.add(new ThemeDescription((View) this.chatListView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE, new Class[]{ChatUnreadCell.class}, new String[]{"backgroundLayout"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_unreadMessagesStartBackground"));
                            arrayList.add(new ThemeDescription((View) this.chatListView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{ChatUnreadCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_unreadMessagesStartArrowIcon"));
                            arrayList.add(new ThemeDescription((View) this.chatListView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{ChatUnreadCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_unreadMessagesStartText"));
                            arrayList.add(new ThemeDescription(this.progressView2, ThemeDescription.FLAG_SERVICEBACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_serviceBackground"));
                            arrayList.add(new ThemeDescription(this.emptyView, ThemeDescription.FLAG_SERVICEBACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_serviceBackground"));
                            arrayList.add(new ThemeDescription((View) this.chatListView, ThemeDescription.FLAG_SERVICEBACKGROUND, new Class[]{ChatLoadingCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_serviceBackground"));
                            arrayList.add(new ThemeDescription((View) this.chatListView, ThemeDescription.FLAG_PROGRESSBAR, new Class[]{ChatLoadingCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_serviceText"));
                            ChatAvatarContainer chatAvatarContainer = this.avatarContainer;
                            ImageView imageView = null;
                            arrayList.add(new ThemeDescription(chatAvatarContainer != null ? chatAvatarContainer.getTimeItem() : null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_secretTimerBackground"));
                            ChatAvatarContainer chatAvatarContainer2 = this.avatarContainer;
                            if (chatAvatarContainer2 != null) {
                                imageView = chatAvatarContainer2.getTimeItem();
                            }
                            arrayList.add(new ThemeDescription(imageView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_secretTimerText"));
                            arrayList.add(new ThemeDescription(this.undoView, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_background"));
                            arrayList.add(new ThemeDescription((View) this.undoView, 0, new Class[]{UndoView.class}, new String[]{"undoImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_cancelColor"));
                            arrayList.add(new ThemeDescription((View) this.undoView, 0, new Class[]{UndoView.class}, new String[]{"undoTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_cancelColor"));
                            arrayList.add(new ThemeDescription((View) this.undoView, 0, new Class[]{UndoView.class}, new String[]{"infoTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_infoColor"));
                            arrayList.add(new ThemeDescription((View) this.undoView, 0, new Class[]{UndoView.class}, new String[]{"textPaint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_infoColor"));
                            arrayList.add(new ThemeDescription((View) this.undoView, 0, new Class[]{UndoView.class}, new String[]{"progressPaint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_infoColor"));
                            arrayList.add(new ThemeDescription((View) this.undoView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{UndoView.class}, new String[]{"leftImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_infoColor"));
                            return arrayList;
                        }
                    }
