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
import android.util.LongSparseArray;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.ViewTreeObserver;
import android.view.animation.LayoutAnimationController;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScrollerCustom;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.browser.Browser;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction;
import org.telegram.tgnet.TLRPC$ChannelParticipant;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$InputStickerSet;
import org.telegram.tgnet.TLRPC$Message;
import org.telegram.tgnet.TLRPC$MessageMedia;
import org.telegram.tgnet.TLRPC$TL_channelAdminLogEvent;
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
import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BackDrawable;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.ChatActionCell;
import org.telegram.ui.Cells.ChatLoadingCell;
import org.telegram.ui.Cells.ChatMessageCell;
import org.telegram.ui.Cells.ChatUnreadCell;
import org.telegram.ui.Components.AdminLogFilterAlert;
import org.telegram.ui.Components.ChatAvatarContainer;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.PhonebookShareAlert;
import org.telegram.ui.Components.PipRoundVideoView;
import org.telegram.ui.Components.RadialProgressView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.SizeNotifierFrameLayout;
import org.telegram.ui.Components.StickersAlert;
import org.telegram.ui.PhotoViewer;

public class ChannelAdminLogActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private ArrayList<TLRPC$ChannelParticipant> admins;
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
    private TextureView videoTextureView;
    private boolean wasPaused = false;

    static /* synthetic */ boolean lambda$createView$2(View view, MotionEvent motionEvent) {
        return true;
    }

    static /* synthetic */ void lambda$null$8(DialogInterface dialogInterface, int i) {
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

    public /* synthetic */ void lambda$loadMessages$1$ChannelAdminLogActivity(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLObject != null) {
            AndroidUtilities.runOnUIThread(new Runnable((TLRPC$TL_channels_adminLogResults) tLObject) {
                private final /* synthetic */ TLRPC$TL_channels_adminLogResults f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    ChannelAdminLogActivity.this.lambda$null$0$ChannelAdminLogActivity(this.f$1);
                }
            });
        }
    }

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
                if (!(tLRPC$ChannelAdminLogEventAction instanceof TLRPC$TL_channelAdminLogEventActionParticipantToggleAdmin) || !(tLRPC$ChannelAdminLogEventAction.prev_participant instanceof TLRPC$TL_channelParticipantCreator) || (tLRPC$ChannelAdminLogEventAction.new_participant instanceof TLRPC$TL_channelParticipantCreator)) {
                    this.minEventId = Math.min(this.minEventId, tLRPC$TL_channelAdminLogEvent.id);
                    MessageObject messageObject = new MessageObject(this.currentAccount, tLRPC$TL_channelAdminLogEvent, this.messages, this.messagesByDays, this.currentChat, this.mid);
                    if (messageObject.contentType >= 0) {
                        this.messagesDict.put(tLRPC$TL_channelAdminLogEvent.id, messageObject);
                    }
                    z = true;
                }
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
        AnonymousClass4 r4 = new SizeNotifierFrameLayout(context2, SharedConfig.smoothKeyboard) {
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
        this.emptyViewContainer.setOnTouchListener($$Lambda$ChannelAdminLogActivity$Xh6zBvXYfqVbautwsQ1g7PDF8js.INSTANCE);
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
                    if (r9 == 0) goto L_0x00bc
                    r9 = r7
                    org.telegram.ui.Cells.ChatMessageCell r9 = (org.telegram.ui.Cells.ChatMessageCell) r9
                    org.telegram.messenger.ImageReceiver r0 = r9.getAvatarImage()
                    if (r0 == 0) goto L_0x00bc
                    int r1 = r7.getTop()
                    boolean r2 = r9.isPinnedBottom()
                    if (r2 == 0) goto L_0x0047
                    org.telegram.ui.ChannelAdminLogActivity r2 = org.telegram.ui.ChannelAdminLogActivity.this
                    org.telegram.ui.Components.RecyclerListView r2 = r2.chatListView
                    androidx.recyclerview.widget.RecyclerView$ViewHolder r2 = r2.getChildViewHolder(r7)
                    if (r2 == 0) goto L_0x0047
                    org.telegram.ui.ChannelAdminLogActivity r3 = org.telegram.ui.ChannelAdminLogActivity.this
                    org.telegram.ui.Components.RecyclerListView r3 = r3.chatListView
                    int r2 = r2.getAdapterPosition()
                    int r2 = r2 + 1
                    androidx.recyclerview.widget.RecyclerView$ViewHolder r2 = r3.findViewHolderForAdapterPosition(r2)
                    if (r2 == 0) goto L_0x0047
                    r7 = 1148846080(0x447a0000, float:1000.0)
                    int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
                    int r7 = -r7
                    r0.setImageY(r7)
                    r0.draw(r6)
                    return r8
                L_0x0047:
                    boolean r2 = r9.isPinnedTop()
                    if (r2 == 0) goto L_0x007f
                    org.telegram.ui.ChannelAdminLogActivity r2 = org.telegram.ui.ChannelAdminLogActivity.this
                    org.telegram.ui.Components.RecyclerListView r2 = r2.chatListView
                    androidx.recyclerview.widget.RecyclerView$ViewHolder r2 = r2.getChildViewHolder(r7)
                    if (r2 == 0) goto L_0x007f
                L_0x0059:
                    org.telegram.ui.ChannelAdminLogActivity r3 = org.telegram.ui.ChannelAdminLogActivity.this
                    org.telegram.ui.Components.RecyclerListView r3 = r3.chatListView
                    int r2 = r2.getAdapterPosition()
                    int r2 = r2 + -1
                    androidx.recyclerview.widget.RecyclerView$ViewHolder r2 = r3.findViewHolderForAdapterPosition(r2)
                    if (r2 == 0) goto L_0x007f
                    android.view.View r1 = r2.itemView
                    int r1 = r1.getTop()
                    android.view.View r3 = r2.itemView
                    boolean r4 = r3 instanceof org.telegram.ui.Cells.ChatMessageCell
                    if (r4 == 0) goto L_0x007f
                    org.telegram.ui.Cells.ChatMessageCell r3 = (org.telegram.ui.Cells.ChatMessageCell) r3
                    boolean r3 = r3.isPinnedTop()
                    if (r3 != 0) goto L_0x0059
                L_0x007f:
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
                    if (r7 <= r9) goto L_0x00a0
                    r7 = r9
                L_0x00a0:
                    r9 = 1111490560(0x42400000, float:48.0)
                    int r2 = org.telegram.messenger.AndroidUtilities.dp(r9)
                    int r2 = r7 - r2
                    if (r2 >= r1) goto L_0x00af
                    int r7 = org.telegram.messenger.AndroidUtilities.dp(r9)
                    int r7 = r7 + r1
                L_0x00af:
                    r9 = 1110441984(0x42300000, float:44.0)
                    int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
                    int r7 = r7 - r9
                    r0.setImageY(r7)
                    r0.draw(r6)
                L_0x00bc:
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
                ChannelAdminLogActivity.this.lambda$createView$10$ChannelAdminLogActivity(view);
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
        updateEmptyPlaceholder();
        return this.fragmentView;
    }

    public /* synthetic */ void lambda$createView$3$ChannelAdminLogActivity(View view, int i) {
        createMenu(view);
    }

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

    public /* synthetic */ void lambda$createView$10$ChannelAdminLogActivity(View view) {
        if (getParentActivity() != null) {
            AndroidUtilities.hideKeyboard(this.searchItem.getSearchField());
            Calendar instance = Calendar.getInstance();
            try {
                DatePickerDialog datePickerDialog = new DatePickerDialog(getParentActivity(), new DatePickerDialog.OnDateSetListener() {
                    public final void onDateSet(DatePicker datePicker, int i, int i2, int i3) {
                        ChannelAdminLogActivity.this.lambda$null$7$ChannelAdminLogActivity(datePicker, i, i2, i3);
                    }
                }, instance.get(1), instance.get(2), instance.get(5));
                DatePicker datePicker = datePickerDialog.getDatePicker();
                datePicker.setMinDate(1375315200000L);
                datePicker.setMaxDate(System.currentTimeMillis());
                datePickerDialog.setButton(-1, LocaleController.getString("JumpToDate", NUM), datePickerDialog);
                datePickerDialog.setButton(-2, LocaleController.getString("Cancel", NUM), $$Lambda$ChannelAdminLogActivity$v157pu_gWSgDQsCgDIoOuRIDsE.INSTANCE);
                if (Build.VERSION.SDK_INT >= 21) {
                    datePickerDialog.setOnShowListener(new DialogInterface.OnShowListener(datePicker) {
                        private final /* synthetic */ DatePicker f$0;

                        {
                            this.f$0 = r1;
                        }

                        public final void onShow(DialogInterface dialogInterface) {
                            ChannelAdminLogActivity.lambda$null$9(this.f$0, dialogInterface);
                        }
                    });
                }
                showDialog(datePickerDialog);
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    public /* synthetic */ void lambda$null$7$ChannelAdminLogActivity(DatePicker datePicker, int i, int i2, int i3) {
        Calendar instance = Calendar.getInstance();
        instance.clear();
        instance.set(i, i2, i3);
        long time = instance.getTime().getTime() / 1000;
        loadMessages(true);
    }

    static /* synthetic */ void lambda$null$9(DatePicker datePicker, DialogInterface dialogInterface) {
        int childCount = datePicker.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = datePicker.getChildAt(i);
            ViewGroup.LayoutParams layoutParams = childAt.getLayoutParams();
            layoutParams.width = -1;
            childAt.setLayoutParams(layoutParams);
        }
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
                            TLRPC$InputStickerSet tLRPC$InputStickerSet = tLRPC$ChannelAdminLogEventAction.new_stickerset;
                            if (tLRPC$InputStickerSet == null || (tLRPC$InputStickerSet instanceof TLRPC$TL_inputStickerSetEmpty)) {
                                tLRPC$InputStickerSet = this.selectedObject.currentEvent.action.prev_stickerset;
                            }
                            TLRPC$InputStickerSet tLRPC$InputStickerSet2 = tLRPC$InputStickerSet;
                            if (tLRPC$InputStickerSet2 != null) {
                                showDialog(new StickersAlert(getParentActivity(), this, tLRPC$InputStickerSet2, (TLRPC$TL_messages_stickerSet) null, (StickersAlert.StickersAlertDelegate) null));
                                return;
                            }
                        }
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
                        private final /* synthetic */ ArrayList f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final void onClick(DialogInterface dialogInterface, int i) {
                            ChannelAdminLogActivity.this.lambda$createMenu$11$ChannelAdminLogActivity(this.f$1, dialogInterface, i);
                        }
                    });
                    builder.setTitle(LocaleController.getString("Message", NUM));
                    showDialog(builder.create());
                }
            }
        }
    }

    public /* synthetic */ void lambda$createMenu$11$ChannelAdminLogActivity(ArrayList arrayList, DialogInterface dialogInterface, int i) {
        if (this.selectedObject != null && i >= 0 && i < arrayList.size()) {
            processSelectedOption(((Integer) arrayList.get(i)).intValue());
        }
    }

    private String getMessageContent(MessageObject messageObject, int i, boolean z) {
        int i2;
        TLRPC$Chat chat;
        String str = "";
        if (z && i != (i2 = messageObject.messageOwner.from_id)) {
            if (i2 > 0) {
                TLRPC$User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(messageObject.messageOwner.from_id));
                if (user != null) {
                    str = ContactsController.formatName(user.first_name, user.last_name) + ":\n";
                }
            } else if (i2 < 0 && (chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-messageObject.messageOwner.from_id))) != null) {
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
                AnonymousClass10 r0 = new FrameLayout(getParentActivity()) {
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

    /* JADX WARNING: Code restructure failed: missing block: B:83:0x022c, code lost:
        if (r0.exists() != false) goto L_0x0230;
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
                case 3: goto L_0x0385;
                case 4: goto L_0x0326;
                case 5: goto L_0x0211;
                case 6: goto L_0x0186;
                case 7: goto L_0x0135;
                case 8: goto L_0x0013;
                case 9: goto L_0x011d;
                case 10: goto L_0x0097;
                case 11: goto L_0x0086;
                case 12: goto L_0x0013;
                case 13: goto L_0x0013;
                case 14: goto L_0x0013;
                case 15: goto L_0x0058;
                case 16: goto L_0x004d;
                case 17: goto L_0x0015;
                default: goto L_0x0013;
            }
        L_0x0013:
            goto L_0x038c
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
            goto L_0x038c
        L_0x0047:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x038c
        L_0x004d:
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            java.lang.String r0 = r0.phone_number
            org.telegram.messenger.AndroidUtilities.addToClipboard(r0)
            goto L_0x038c
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
            goto L_0x038c
        L_0x0086:
            org.telegram.tgnet.TLRPC$Document r0 = r0.getDocument()
            int r1 = r10.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
            org.telegram.messenger.MessageObject r2 = r10.selectedObject
            r1.saveGif(r2, r0)
            goto L_0x038c
        L_0x0097:
            int r0 = android.os.Build.VERSION.SDK_INT
            if (r0 < r4) goto L_0x00b3
            android.app.Activity r0 = r10.getParentActivity()
            int r0 = r0.checkSelfPermission(r5)
            if (r0 == 0) goto L_0x00b3
            android.app.Activity r0 = r10.getParentActivity()
            java.lang.String[] r1 = new java.lang.String[r7]
            r1[r6] = r5
            r0.requestPermissions(r1, r3)
            r10.selectedObject = r8
            return
        L_0x00b3:
            org.telegram.messenger.MessageObject r0 = r10.selectedObject
            org.telegram.tgnet.TLRPC$Document r0 = r0.getDocument()
            java.lang.String r0 = org.telegram.messenger.FileLoader.getDocumentFileName(r0)
            boolean r1 = android.text.TextUtils.isEmpty(r0)
            if (r1 == 0) goto L_0x00c9
            org.telegram.messenger.MessageObject r0 = r10.selectedObject
            java.lang.String r0 = r0.getFileName()
        L_0x00c9:
            org.telegram.messenger.MessageObject r1 = r10.selectedObject
            org.telegram.tgnet.TLRPC$Message r1 = r1.messageOwner
            java.lang.String r1 = r1.attachPath
            if (r1 == 0) goto L_0x00e3
            int r3 = r1.length()
            if (r3 <= 0) goto L_0x00e3
            java.io.File r3 = new java.io.File
            r3.<init>(r1)
            boolean r3 = r3.exists()
            if (r3 != 0) goto L_0x00e3
            r1 = r8
        L_0x00e3:
            if (r1 == 0) goto L_0x00eb
            int r3 = r1.length()
            if (r3 != 0) goto L_0x00f7
        L_0x00eb:
            org.telegram.messenger.MessageObject r1 = r10.selectedObject
            org.telegram.tgnet.TLRPC$Message r1 = r1.messageOwner
            java.io.File r1 = org.telegram.messenger.FileLoader.getPathToMessage(r1)
            java.lang.String r1 = r1.toString()
        L_0x00f7:
            android.app.Activity r3 = r10.getParentActivity()
            org.telegram.messenger.MessageObject r4 = r10.selectedObject
            boolean r4 = r4.isMusic()
            if (r4 == 0) goto L_0x0104
            goto L_0x0105
        L_0x0104:
            r2 = 2
        L_0x0105:
            org.telegram.messenger.MessageObject r4 = r10.selectedObject
            org.telegram.tgnet.TLRPC$Document r4 = r4.getDocument()
            if (r4 == 0) goto L_0x0116
            org.telegram.messenger.MessageObject r4 = r10.selectedObject
            org.telegram.tgnet.TLRPC$Document r4 = r4.getDocument()
            java.lang.String r4 = r4.mime_type
            goto L_0x0118
        L_0x0116:
            java.lang.String r4 = ""
        L_0x0118:
            org.telegram.messenger.MediaController.saveFile(r1, r3, r2, r0, r4)
            goto L_0x038c
        L_0x011d:
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
            goto L_0x038c
        L_0x0135:
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.attachPath
            if (r0 == 0) goto L_0x014d
            int r1 = r0.length()
            if (r1 <= 0) goto L_0x014d
            java.io.File r1 = new java.io.File
            r1.<init>(r0)
            boolean r1 = r1.exists()
            if (r1 != 0) goto L_0x014d
            r0 = r8
        L_0x014d:
            if (r0 == 0) goto L_0x0155
            int r1 = r0.length()
            if (r1 != 0) goto L_0x0161
        L_0x0155:
            org.telegram.messenger.MessageObject r0 = r10.selectedObject
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.io.File r0 = org.telegram.messenger.FileLoader.getPathToMessage(r0)
            java.lang.String r0 = r0.toString()
        L_0x0161:
            int r1 = android.os.Build.VERSION.SDK_INT
            if (r1 < r4) goto L_0x017d
            android.app.Activity r1 = r10.getParentActivity()
            int r1 = r1.checkSelfPermission(r5)
            if (r1 == 0) goto L_0x017d
            android.app.Activity r0 = r10.getParentActivity()
            java.lang.String[] r1 = new java.lang.String[r7]
            r1[r6] = r5
            r0.requestPermissions(r1, r3)
            r10.selectedObject = r8
            return
        L_0x017d:
            android.app.Activity r1 = r10.getParentActivity()
            org.telegram.messenger.MediaController.saveFile(r0, r1, r6, r8, r8)
            goto L_0x038c
        L_0x0186:
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.attachPath
            if (r0 == 0) goto L_0x019e
            int r2 = r0.length()
            if (r2 <= 0) goto L_0x019e
            java.io.File r2 = new java.io.File
            r2.<init>(r0)
            boolean r2 = r2.exists()
            if (r2 != 0) goto L_0x019e
            r0 = r8
        L_0x019e:
            if (r0 == 0) goto L_0x01a6
            int r2 = r0.length()
            if (r2 != 0) goto L_0x01b2
        L_0x01a6:
            org.telegram.messenger.MessageObject r0 = r10.selectedObject
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.io.File r0 = org.telegram.messenger.FileLoader.getPathToMessage(r0)
            java.lang.String r0 = r0.toString()
        L_0x01b2:
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
            if (r3 < r4) goto L_0x01ef
            android.app.Activity r3 = r10.getParentActivity()     // Catch:{ Exception -> 0x01e2 }
            java.lang.String r4 = "org.telegram.messenger.beta.provider"
            java.io.File r6 = new java.io.File     // Catch:{ Exception -> 0x01e2 }
            r6.<init>(r0)     // Catch:{ Exception -> 0x01e2 }
            android.net.Uri r3 = androidx.core.content.FileProvider.getUriForFile(r3, r4, r6)     // Catch:{ Exception -> 0x01e2 }
            r2.putExtra(r5, r3)     // Catch:{ Exception -> 0x01e2 }
            r2.setFlags(r7)     // Catch:{ Exception -> 0x01e2 }
            goto L_0x01fb
        L_0x01e2:
            java.io.File r3 = new java.io.File
            r3.<init>(r0)
            android.net.Uri r0 = android.net.Uri.fromFile(r3)
            r2.putExtra(r5, r0)
            goto L_0x01fb
        L_0x01ef:
            java.io.File r3 = new java.io.File
            r3.<init>(r0)
            android.net.Uri r0 = android.net.Uri.fromFile(r3)
            r2.putExtra(r5, r0)
        L_0x01fb:
            android.app.Activity r0 = r10.getParentActivity()
            r3 = 2131626751(0x7f0e0aff, float:1.8880747E38)
            java.lang.String r4 = "ShareFile"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            android.content.Intent r2 = android.content.Intent.createChooser(r2, r3)
            r0.startActivityForResult(r2, r1)
            goto L_0x038c
        L_0x0211:
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.attachPath
            if (r0 == 0) goto L_0x022f
            int r0 = r0.length()
            if (r0 == 0) goto L_0x022f
            java.io.File r0 = new java.io.File
            org.telegram.messenger.MessageObject r1 = r10.selectedObject
            org.telegram.tgnet.TLRPC$Message r1 = r1.messageOwner
            java.lang.String r1 = r1.attachPath
            r0.<init>(r1)
            boolean r1 = r0.exists()
            if (r1 == 0) goto L_0x022f
            goto L_0x0230
        L_0x022f:
            r0 = r8
        L_0x0230:
            if (r0 != 0) goto L_0x0241
            org.telegram.messenger.MessageObject r1 = r10.selectedObject
            org.telegram.tgnet.TLRPC$Message r1 = r1.messageOwner
            java.io.File r1 = org.telegram.messenger.FileLoader.getPathToMessage(r1)
            boolean r2 = r1.exists()
            if (r2 == 0) goto L_0x0241
            r0 = r1
        L_0x0241:
            if (r0 == 0) goto L_0x038c
            java.lang.String r1 = r0.getName()
            java.lang.String r1 = r1.toLowerCase()
            java.lang.String r2 = "attheme"
            boolean r1 = r1.endsWith(r2)
            r2 = 2131626003(0x7f0e0813, float:1.887923E38)
            java.lang.String r3 = "OK"
            r4 = 2131624198(0x7f0e0106, float:1.8875569E38)
            java.lang.String r5 = "AppName"
            if (r1 == 0) goto L_0x02dc
            androidx.recyclerview.widget.LinearLayoutManager r1 = r10.chatLayoutManager
            r6 = -1
            if (r1 == 0) goto L_0x028f
            int r1 = r1.findLastVisibleItemPosition()
            androidx.recyclerview.widget.LinearLayoutManager r9 = r10.chatLayoutManager
            int r9 = r9.getItemCount()
            int r9 = r9 - r7
            if (r1 >= r9) goto L_0x028d
            androidx.recyclerview.widget.LinearLayoutManager r1 = r10.chatLayoutManager
            int r1 = r1.findFirstVisibleItemPosition()
            r10.scrollToPositionOnRecreate = r1
            org.telegram.ui.Components.RecyclerListView r9 = r10.chatListView
            androidx.recyclerview.widget.RecyclerView$ViewHolder r1 = r9.findViewHolderForAdapterPosition(r1)
            org.telegram.ui.Components.RecyclerListView$Holder r1 = (org.telegram.ui.Components.RecyclerListView.Holder) r1
            if (r1 == 0) goto L_0x028a
            android.view.View r1 = r1.itemView
            int r1 = r1.getTop()
            r10.scrollToOffsetOnRecreate = r1
            goto L_0x028f
        L_0x028a:
            r10.scrollToPositionOnRecreate = r6
            goto L_0x028f
        L_0x028d:
            r10.scrollToPositionOnRecreate = r6
        L_0x028f:
            org.telegram.messenger.MessageObject r1 = r10.selectedObject
            java.lang.String r1 = r1.getDocumentName()
            org.telegram.ui.ActionBar.Theme$ThemeInfo r0 = org.telegram.ui.ActionBar.Theme.applyThemeFile(r0, r1, r8, r7)
            if (r0 == 0) goto L_0x02a5
            org.telegram.ui.ThemePreviewActivity r1 = new org.telegram.ui.ThemePreviewActivity
            r1.<init>(r0)
            r10.presentFragment(r1)
            goto L_0x038c
        L_0x02a5:
            r10.scrollToPositionOnRecreate = r6
            android.app.Activity r0 = r10.getParentActivity()
            if (r0 != 0) goto L_0x02b0
            r10.selectedObject = r8
            return
        L_0x02b0:
            org.telegram.ui.ActionBar.AlertDialog$Builder r0 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            android.app.Activity r1 = r10.getParentActivity()
            r0.<init>((android.content.Context) r1)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r0.setTitle(r1)
            r1 = 2131625469(0x7f0e05fd, float:1.8878147E38)
            java.lang.String r4 = "IncorrectTheme"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r4, r1)
            r0.setMessage(r1)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setPositiveButton(r1, r8)
            org.telegram.ui.ActionBar.AlertDialog r0 = r0.create()
            r10.showDialog(r0)
            goto L_0x038c
        L_0x02dc:
            org.telegram.messenger.LocaleController r1 = org.telegram.messenger.LocaleController.getInstance()
            int r6 = r10.currentAccount
            boolean r0 = r1.applyLanguageFile(r0, r6)
            if (r0 == 0) goto L_0x02f2
            org.telegram.ui.LanguageSelectActivity r0 = new org.telegram.ui.LanguageSelectActivity
            r0.<init>()
            r10.presentFragment(r0)
            goto L_0x038c
        L_0x02f2:
            android.app.Activity r0 = r10.getParentActivity()
            if (r0 != 0) goto L_0x02fb
            r10.selectedObject = r8
            return
        L_0x02fb:
            org.telegram.ui.ActionBar.AlertDialog$Builder r0 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            android.app.Activity r1 = r10.getParentActivity()
            r0.<init>((android.content.Context) r1)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r0.setTitle(r1)
            r1 = 2131625468(0x7f0e05fc, float:1.8878145E38)
            java.lang.String r4 = "IncorrectLocalization"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r4, r1)
            r0.setMessage(r1)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setPositiveButton(r1, r8)
            org.telegram.ui.ActionBar.AlertDialog r0 = r0.create()
            r10.showDialog(r0)
            goto L_0x038c
        L_0x0326:
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.attachPath
            if (r0 == 0) goto L_0x033e
            int r1 = r0.length()
            if (r1 <= 0) goto L_0x033e
            java.io.File r1 = new java.io.File
            r1.<init>(r0)
            boolean r1 = r1.exists()
            if (r1 != 0) goto L_0x033e
            r0 = r8
        L_0x033e:
            if (r0 == 0) goto L_0x0346
            int r1 = r0.length()
            if (r1 != 0) goto L_0x0352
        L_0x0346:
            org.telegram.messenger.MessageObject r0 = r10.selectedObject
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.io.File r0 = org.telegram.messenger.FileLoader.getPathToMessage(r0)
            java.lang.String r0 = r0.toString()
        L_0x0352:
            org.telegram.messenger.MessageObject r1 = r10.selectedObject
            int r1 = r1.type
            if (r1 == r2) goto L_0x035a
            if (r1 != r7) goto L_0x038c
        L_0x035a:
            int r1 = android.os.Build.VERSION.SDK_INT
            if (r1 < r4) goto L_0x0376
            android.app.Activity r1 = r10.getParentActivity()
            int r1 = r1.checkSelfPermission(r5)
            if (r1 == 0) goto L_0x0376
            android.app.Activity r0 = r10.getParentActivity()
            java.lang.String[] r1 = new java.lang.String[r7]
            r1[r6] = r5
            r0.requestPermissions(r1, r3)
            r10.selectedObject = r8
            return
        L_0x0376:
            android.app.Activity r1 = r10.getParentActivity()
            org.telegram.messenger.MessageObject r3 = r10.selectedObject
            int r3 = r3.type
            if (r3 != r2) goto L_0x0381
            r6 = 1
        L_0x0381:
            org.telegram.messenger.MediaController.saveFile(r0, r1, r6, r8, r8)
            goto L_0x038c
        L_0x0385:
            java.lang.String r0 = r10.getMessageContent(r0, r6, r7)
            org.telegram.messenger.AndroidUtilities.addToClipboard(r0)
        L_0x038c:
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
                ChannelAdminLogActivity.this.lambda$loadAdmins$13$ChannelAdminLogActivity(tLObject, tLRPC$TL_error);
            }
        }), this.classGuid);
    }

    public /* synthetic */ void lambda$loadAdmins$13$ChannelAdminLogActivity(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLRPC$TL_error, tLObject) {
            private final /* synthetic */ TLRPC$TL_error f$1;
            private final /* synthetic */ TLObject f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                ChannelAdminLogActivity.this.lambda$null$12$ChannelAdminLogActivity(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$null$12$ChannelAdminLogActivity(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject) {
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
                    this.roundVideoContainer.setTranslationX((float) photoImage.getImageX());
                    this.roundVideoContainer.setTranslationY((float) (this.fragmentView.getPaddingTop() + chatMessageCell.getTop() + photoImage.getImageY()));
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
                    chatMessageCell2.setVisiblePart(i6, measuredHeight2 - i6, (this.contentView.getHeightWithKeyboard() - AndroidUtilities.dp(48.0f)) - this.chatListView.getTop());
                    MessageObject messageObject2 = chatMessageCell2.getMessageObject();
                    if (this.roundVideoContainer != null && messageObject2.isRoundVideo() && MediaController.getInstance().isPlayingMessage(messageObject2)) {
                        ImageReceiver photoImage = chatMessageCell2.getPhotoImage();
                        this.roundVideoContainer.setTranslationX((float) photoImage.getImageX());
                        this.roundVideoContainer.setTranslationY((float) (this.fragmentView.getPaddingTop() + top + photoImage.getImageY()));
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
            NotificationCenter.getInstance(this.currentAccount).setAllowedNotificationsDutingAnimation(new int[]{NotificationCenter.chatInfoDidLoad, NotificationCenter.dialogsNeedReload, NotificationCenter.closeChats, NotificationCenter.messagesDidLoad, NotificationCenter.botKeyboardDidLoad});
            NotificationCenter.getInstance(this.currentAccount).setAnimationInProgress(true);
        }
    }

    public void onTransitionAnimationEnd(boolean z, boolean z2) {
        if (z) {
            NotificationCenter.getInstance(this.currentAccount).setAnimationInProgress(false);
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
        this.paused = true;
        this.wasPaused = true;
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
            private final /* synthetic */ String f$1;

            {
                this.f$1 = r2;
            }

            public final void onClick(DialogInterface dialogInterface, int i) {
                ChannelAdminLogActivity.this.lambda$showOpenUrlAlert$14$ChannelAdminLogActivity(this.f$1, dialogInterface, i);
            }
        });
        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
        showDialog(builder.create());
    }

    public /* synthetic */ void lambda$showOpenUrlAlert$14$ChannelAdminLogActivity(String str, DialogInterface dialogInterface, int i) {
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

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v1, resolved type: org.telegram.ui.Cells.ChatMessageCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v2, resolved type: org.telegram.ui.Cells.ChatMessageCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v4, resolved type: org.telegram.ui.Cells.BotHelpCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v10, resolved type: org.telegram.ui.Cells.ChatMessageCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v16, resolved type: org.telegram.ui.Cells.ChatLoadingCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v17, resolved type: org.telegram.ui.Cells.ChatLoadingCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v18, resolved type: org.telegram.ui.Cells.ChatUnreadCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v19, resolved type: org.telegram.ui.Cells.ChatActionCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v20, resolved type: org.telegram.ui.Cells.ChatMessageCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v21, resolved type: org.telegram.ui.Cells.ChatMessageCell} */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public androidx.recyclerview.widget.RecyclerView.ViewHolder onCreateViewHolder(android.view.ViewGroup r3, int r4) {
            /*
                r2 = this;
                r3 = 1
                if (r4 != 0) goto L_0x003c
                org.telegram.ui.ChannelAdminLogActivity r4 = org.telegram.ui.ChannelAdminLogActivity.this
                java.util.ArrayList r4 = r4.chatMessageCellsCache
                boolean r4 = r4.isEmpty()
                if (r4 != 0) goto L_0x0026
                org.telegram.ui.ChannelAdminLogActivity r4 = org.telegram.ui.ChannelAdminLogActivity.this
                java.util.ArrayList r4 = r4.chatMessageCellsCache
                r0 = 0
                java.lang.Object r4 = r4.get(r0)
                android.view.View r4 = (android.view.View) r4
                org.telegram.ui.ChannelAdminLogActivity r1 = org.telegram.ui.ChannelAdminLogActivity.this
                java.util.ArrayList r1 = r1.chatMessageCellsCache
                r1.remove(r0)
                goto L_0x002d
            L_0x0026:
                org.telegram.ui.Cells.ChatMessageCell r4 = new org.telegram.ui.Cells.ChatMessageCell
                android.content.Context r0 = r2.mContext
                r4.<init>(r0)
            L_0x002d:
                r0 = r4
                org.telegram.ui.Cells.ChatMessageCell r0 = (org.telegram.ui.Cells.ChatMessageCell) r0
                org.telegram.ui.ChannelAdminLogActivity$ChatActivityAdapter$1 r1 = new org.telegram.ui.ChannelAdminLogActivity$ChatActivityAdapter$1
                r1.<init>()
                r0.setDelegate(r1)
                r0.setAllowAssistant(r3)
                goto L_0x0078
            L_0x003c:
                if (r4 != r3) goto L_0x004e
                org.telegram.ui.Cells.ChatActionCell r4 = new org.telegram.ui.Cells.ChatActionCell
                android.content.Context r3 = r2.mContext
                r4.<init>(r3)
                org.telegram.ui.ChannelAdminLogActivity$ChatActivityAdapter$2 r3 = new org.telegram.ui.ChannelAdminLogActivity$ChatActivityAdapter$2
                r3.<init>()
                r4.setDelegate(r3)
                goto L_0x0078
            L_0x004e:
                r3 = 2
                if (r4 != r3) goto L_0x0059
                org.telegram.ui.Cells.ChatUnreadCell r4 = new org.telegram.ui.Cells.ChatUnreadCell
                android.content.Context r3 = r2.mContext
                r4.<init>(r3)
                goto L_0x0078
            L_0x0059:
                r3 = 3
                if (r4 != r3) goto L_0x006c
                org.telegram.ui.Cells.BotHelpCell r4 = new org.telegram.ui.Cells.BotHelpCell
                android.content.Context r3 = r2.mContext
                r4.<init>(r3)
                org.telegram.ui.-$$Lambda$ChannelAdminLogActivity$ChatActivityAdapter$17meO0dSqjA1BWDg9Mv1dvdogDs r3 = new org.telegram.ui.-$$Lambda$ChannelAdminLogActivity$ChatActivityAdapter$17meO0dSqjA1BWDg9Mv1dvdogDs
                r3.<init>()
                r4.setDelegate(r3)
                goto L_0x0078
            L_0x006c:
                r3 = 4
                if (r4 != r3) goto L_0x0077
                org.telegram.ui.Cells.ChatLoadingCell r4 = new org.telegram.ui.Cells.ChatLoadingCell
                android.content.Context r3 = r2.mContext
                r4.<init>(r3)
                goto L_0x0078
            L_0x0077:
                r4 = 0
            L_0x0078:
                androidx.recyclerview.widget.RecyclerView$LayoutParams r3 = new androidx.recyclerview.widget.RecyclerView$LayoutParams
                r0 = -1
                r1 = -2
                r3.<init>((int) r0, (int) r1)
                r4.setLayoutParams(r3)
                org.telegram.ui.Components.RecyclerListView$Holder r3 = new org.telegram.ui.Components.RecyclerListView$Holder
                r3.<init>(r4)
                return r3
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChannelAdminLogActivity.ChatActivityAdapter.onCreateViewHolder(android.view.ViewGroup, int):androidx.recyclerview.widget.RecyclerView$ViewHolder");
        }

        public /* synthetic */ void lambda$onCreateViewHolder$0$ChannelAdminLogActivity$ChatActivityAdapter(String str) {
            if (str.startsWith("@")) {
                MessagesController.getInstance(ChannelAdminLogActivity.this.currentAccount).openByUserName(str.substring(1), ChannelAdminLogActivity.this, 0);
            } else if (str.startsWith("#")) {
                DialogsActivity dialogsActivity = new DialogsActivity((Bundle) null);
                dialogsActivity.setSearchString(str);
                ChannelAdminLogActivity.this.presentFragment(dialogsActivity);
            }
        }

        /* JADX WARNING: Code restructure failed: missing block: B:33:0x00cc, code lost:
            if (java.lang.Math.abs(r11.date - r5.date) <= 300) goto L_0x00d0;
         */
        /* JADX WARNING: Removed duplicated region for block: B:26:0x0095  */
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
                goto L_0x00e9
            L_0x0018:
                int r0 = r10.messagesStartRow
                if (r12 < r0) goto L_0x00e9
                int r0 = r10.messagesEndRow
                if (r12 >= r0) goto L_0x00e9
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
                if (r4 == 0) goto L_0x00db
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
                if (r7 != 0) goto L_0x008e
                int r7 = r11.getItemViewType()
                if (r5 != r7) goto L_0x008e
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
                if (r5 != r7) goto L_0x008e
                org.telegram.tgnet.TLRPC$Message r4 = r4.messageOwner
                int r5 = r4.from_id
                org.telegram.tgnet.TLRPC$Message r7 = r0.messageOwner
                int r9 = r7.from_id
                if (r5 != r9) goto L_0x008e
                int r4 = r4.date
                int r5 = r7.date
                int r4 = r4 - r5
                int r4 = java.lang.Math.abs(r4)
                if (r4 > r8) goto L_0x008e
                r4 = 1
                goto L_0x008f
            L_0x008e:
                r4 = 0
            L_0x008f:
                int r11 = r11.getItemViewType()
                if (r6 != r11) goto L_0x00cf
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
                if (r12 != 0) goto L_0x00cf
                boolean r12 = r11.isOutOwner()
                boolean r5 = r0.isOutOwner()
                if (r12 != r5) goto L_0x00cf
                org.telegram.tgnet.TLRPC$Message r11 = r11.messageOwner
                int r12 = r11.from_id
                org.telegram.tgnet.TLRPC$Message r5 = r0.messageOwner
                int r6 = r5.from_id
                if (r12 != r6) goto L_0x00cf
                int r11 = r11.date
                int r12 = r5.date
                int r11 = r11 - r12
                int r11 = java.lang.Math.abs(r11)
                if (r11 > r8) goto L_0x00cf
                goto L_0x00d0
            L_0x00cf:
                r2 = 0
            L_0x00d0:
                r11 = 0
                r3.setMessageObject(r0, r11, r4, r2)
                r3.setHighlighted(r1)
                r3.setHighlightedText(r11)
                goto L_0x00e9
            L_0x00db:
                boolean r11 = r3 instanceof org.telegram.ui.Cells.ChatActionCell
                if (r11 == 0) goto L_0x00e9
                org.telegram.ui.Cells.ChatActionCell r3 = (org.telegram.ui.Cells.ChatActionCell) r3
                r3.setMessageObject(r0)
                r11 = 1065353216(0x3var_, float:1.0)
                r3.setAlpha(r11)
            L_0x00e9:
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
                        chatMessageCell.setVisiblePart(i, measuredHeight2 - i, (ChannelAdminLogActivity.this.contentView.getHeightWithKeyboard() - AndroidUtilities.dp(48.0f)) - ChannelAdminLogActivity.this.chatListView.getTop());
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

        public void notifyItemInserted(int i) {
            updateRows();
            try {
                super.notifyItemInserted(i);
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
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_shareIconDrawable, Theme.chat_botInlineDrawable, Theme.chat_botLinkDrawalbe, Theme.chat_goIconDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_serviceIcon"));
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
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgStickerHalfCheckDrawable, Theme.chat_msgStickerCheckDrawable, Theme.chat_msgStickerClockDrawable, Theme.chat_msgStickerViewsDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_serviceText"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgMediaClockDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_mediaSentClock"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgOutViewsDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outViews"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgOutViewsSelectedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outViewsSelected"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgInViewsDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inViews"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgInViewsSelectedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inViewsSelected"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgMediaViewsDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_mediaViews"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgOutMenuDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outMenu"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgOutMenuSelectedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outMenuSelected"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgInMenuDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inMenu"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgInMenuSelectedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inMenuSelected"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgMediaMenuDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_mediaMenu"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgOutInstantDrawable, Theme.chat_msgOutCallDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outInstant"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgOutCallSelectedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outInstantSelected"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgInInstantDrawable, Theme.chat_msgInCallDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inInstant"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgInCallSelectedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inInstantSelected"));
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
        return arrayList;
    }
}
