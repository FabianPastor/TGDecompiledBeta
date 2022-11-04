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
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.CharacterStyle;
import android.text.style.URLSpan;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.ViewTreeObserver;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.collection.LongSparseArray;
import androidx.recyclerview.widget.ChatListItemAnimator;
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
import org.telegram.messenger.R;
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
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$FileLocation;
import org.telegram.tgnet.TLRPC$InputStickerSet;
import org.telegram.tgnet.TLRPC$KeyboardButton;
import org.telegram.tgnet.TLRPC$MessageMedia;
import org.telegram.tgnet.TLRPC$PhotoSize;
import org.telegram.tgnet.TLRPC$ReactionCount;
import org.telegram.tgnet.TLRPC$TL_channelAdminLogEvent;
import org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeHistoryTTL;
import org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeStickerSet;
import org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionExportedInviteDelete;
import org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionExportedInviteEdit;
import org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionExportedInviteRevoke;
import org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionParticipantToggleAdmin;
import org.telegram.tgnet.TLRPC$TL_channelAdminLogEventsFilter;
import org.telegram.tgnet.TLRPC$TL_channelParticipantCreator;
import org.telegram.tgnet.TLRPC$TL_channelParticipantsAdmins;
import org.telegram.tgnet.TLRPC$TL_channels_adminLogResults;
import org.telegram.tgnet.TLRPC$TL_channels_channelParticipants;
import org.telegram.tgnet.TLRPC$TL_channels_getAdminLog;
import org.telegram.tgnet.TLRPC$TL_channels_getParticipants;
import org.telegram.tgnet.TLRPC$TL_chatInviteExported;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_inputStickerSetEmpty;
import org.telegram.tgnet.TLRPC$TL_inputStickerSetID;
import org.telegram.tgnet.TLRPC$TL_inputStickerSetShortName;
import org.telegram.tgnet.TLRPC$TL_messageMediaPhoto;
import org.telegram.tgnet.TLRPC$TL_messageMediaWebPage;
import org.telegram.tgnet.TLRPC$TL_messages_exportedChatInvite;
import org.telegram.tgnet.TLRPC$TL_messages_getExportedChatInvite;
import org.telegram.tgnet.TLRPC$TL_messages_stickerSet;
import org.telegram.tgnet.TLRPC$TL_premiumGiftOption;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.tgnet.TLRPC$UserFull;
import org.telegram.tgnet.TLRPC$VideoSize;
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
import org.telegram.ui.ChannelAdminLogActivity;
import org.telegram.ui.Components.AdminLogFilterAlert;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.AnimatedEmojiSpan;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.ChatAvatarContainer;
import org.telegram.ui.Components.ClearHistoryAlert;
import org.telegram.ui.Components.EmbedBottomSheet;
import org.telegram.ui.Components.InviteLinkBottomSheet;
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
import org.telegram.ui.Components.URLSpanReplacement;
import org.telegram.ui.Components.URLSpanUserMention;
import org.telegram.ui.Components.UndoView;
import org.telegram.ui.PhotoViewer;
/* loaded from: classes3.dex */
public class ChannelAdminLogActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private ArrayList<TLRPC$ChannelParticipant> admins;
    private int allowAnimationIndex;
    private Paint aspectPaint;
    private Path aspectPath;
    private AspectRatioFrameLayout aspectRatioFrameLayout;
    private ChatAvatarContainer avatarContainer;
    private FrameLayout bottomOverlayChat;
    private TextView bottomOverlayChatText;
    private ImageView bottomOverlayImage;
    private ChatActivityAdapter chatAdapter;
    private LinearLayoutManager chatLayoutManager;
    private ChatListItemAnimator chatListItemAnimator;
    private RecyclerListView chatListView;
    private boolean checkTextureViewPosition;
    private SizeNotifierFrameLayout contentView;
    protected TLRPC$Chat currentChat;
    private boolean currentFloatingDateOnScreen;
    private boolean currentFloatingTopIsNotMessage;
    private TextView emptyView;
    private FrameLayout emptyViewContainer;
    private boolean endReached;
    private AnimatorSet floatingDateAnimation;
    private ChatActionCell floatingDateView;
    private boolean linviteLoading;
    private boolean loading;
    private int loadsCount;
    private long minEventId;
    private RadialProgressView progressBar;
    private FrameLayout progressView;
    private View progressView2;
    private FrameLayout roundVideoContainer;
    private boolean scrollingFloatingDate;
    private ImageView searchCalendarButton;
    private FrameLayout searchContainer;
    private SimpleTextView searchCountText;
    private ActionBarMenuItem searchItem;
    private boolean searchWas;
    private LongSparseArray<TLRPC$User> selectedAdmins;
    private MessageObject selectedObject;
    private UndoView undoView;
    private HashMap<Long, TLRPC$User> usersMap;
    private TextureView videoTextureView;
    private ArrayList<ChatMessageCell> chatMessageCellsCache = new ArrayList<>();
    private int[] mid = {2};
    private int scrollToPositionOnRecreate = -1;
    private int scrollToOffsetOnRecreate = 0;
    private boolean paused = true;
    private boolean wasPaused = false;
    private LongSparseArray<MessageObject> messagesDict = new LongSparseArray<>();
    private HashMap<String, ArrayList<MessageObject>> messagesByDays = new HashMap<>();
    protected ArrayList<MessageObject> messages = new ArrayList<>();
    private TLRPC$TL_channelAdminLogEventsFilter currentFilter = null;
    private String searchQuery = "";
    private HashMap<String, Object> invitesCache = new HashMap<>();
    private PhotoViewer.PhotoViewerProvider provider = new PhotoViewer.EmptyPhotoViewerProvider() { // from class: org.telegram.ui.ChannelAdminLogActivity.1
        @Override // org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider, org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public PhotoViewer.PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, TLRPC$FileLocation tLRPC$FileLocation, int i, boolean z) {
            ChatActionCell chatActionCell;
            MessageObject messageObject2;
            ChatMessageCell chatMessageCell;
            MessageObject messageObject3;
            int childCount = ChannelAdminLogActivity.this.chatListView.getChildCount();
            int i2 = 0;
            int i3 = 0;
            while (true) {
                ImageReceiver imageReceiver = null;
                if (i3 < childCount) {
                    View childAt = ChannelAdminLogActivity.this.chatListView.getChildAt(i3);
                    if (childAt instanceof ChatMessageCell) {
                        if (messageObject != null && (messageObject3 = (chatMessageCell = (ChatMessageCell) childAt).getMessageObject()) != null && messageObject3.getId() == messageObject.getId()) {
                            imageReceiver = chatMessageCell.getPhotoImage();
                        }
                    } else if ((childAt instanceof ChatActionCell) && (messageObject2 = (chatActionCell = (ChatActionCell) childAt).getMessageObject()) != null) {
                        if (messageObject != null) {
                            if (messageObject2.getId() == messageObject.getId()) {
                                imageReceiver = chatActionCell.getPhotoImage();
                            }
                        } else if (tLRPC$FileLocation != null && messageObject2.photoThumbs != null) {
                            int i4 = 0;
                            while (true) {
                                if (i4 >= messageObject2.photoThumbs.size()) {
                                    break;
                                }
                                TLRPC$FileLocation tLRPC$FileLocation2 = messageObject2.photoThumbs.get(i4).location;
                                if (tLRPC$FileLocation2.volume_id == tLRPC$FileLocation.volume_id && tLRPC$FileLocation2.local_id == tLRPC$FileLocation.local_id) {
                                    imageReceiver = chatActionCell.getPhotoImage();
                                    break;
                                }
                                i4++;
                            }
                        }
                    }
                    if (imageReceiver != null) {
                        int[] iArr = new int[2];
                        childAt.getLocationInWindow(iArr);
                        PhotoViewer.PlaceProviderObject placeProviderObject = new PhotoViewer.PlaceProviderObject();
                        placeProviderObject.viewX = iArr[0];
                        int i5 = iArr[1];
                        if (Build.VERSION.SDK_INT < 21) {
                            i2 = AndroidUtilities.statusBarHeight;
                        }
                        placeProviderObject.viewY = i5 - i2;
                        placeProviderObject.parentView = ChannelAdminLogActivity.this.chatListView;
                        placeProviderObject.imageReceiver = imageReceiver;
                        placeProviderObject.thumb = imageReceiver.getBitmapSafe();
                        placeProviderObject.radius = imageReceiver.getRoundRadius();
                        placeProviderObject.isEvent = true;
                        return placeProviderObject;
                    }
                    i3++;
                } else {
                    return null;
                }
            }
        }
    };

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$createView$2(View view, MotionEvent motionEvent) {
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateBottomOverlay() {
    }

    public ChannelAdminLogActivity(TLRPC$Chat tLRPC$Chat) {
        this.currentChat = tLRPC$Chat;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiLoaded);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagePlayingDidStart);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagePlayingPlayStateChanged);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagePlayingDidReset);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagePlayingProgressDidChanged);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didSetNewWallpapper);
        loadMessages(true);
        loadAdmins();
        return true;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiLoaded);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingDidStart);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingPlayStateChanged);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingDidReset);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingProgressDidChanged);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didSetNewWallpapper);
        getNotificationCenter().onAnimationFinish(this.allowAnimationIndex);
    }

    private void updateEmptyPlaceholder() {
        if (this.emptyView == null) {
            return;
        }
        if (!TextUtils.isEmpty(this.searchQuery)) {
            this.emptyView.setPadding(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(5.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(5.0f));
            this.emptyView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("EventLogEmptyTextSearch", R.string.EventLogEmptyTextSearch, this.searchQuery)));
        } else if (this.selectedAdmins != null || this.currentFilter != null) {
            this.emptyView.setPadding(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(5.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(5.0f));
            this.emptyView.setText(AndroidUtilities.replaceTags(LocaleController.getString("EventLogEmptySearch", R.string.EventLogEmptySearch)));
        } else {
            this.emptyView.setPadding(AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f));
            if (this.currentChat.megagroup) {
                this.emptyView.setText(AndroidUtilities.replaceTags(LocaleController.getString("EventLogEmpty", R.string.EventLogEmpty)));
            } else {
                this.emptyView.setText(AndroidUtilities.replaceTags(LocaleController.getString("EventLogEmptyChannel", R.string.EventLogEmptyChannel)));
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void loadMessages(boolean z) {
        ChatActivityAdapter chatActivityAdapter;
        if (this.loading) {
            return;
        }
        if (z) {
            this.minEventId = Long.MAX_VALUE;
            FrameLayout frameLayout = this.progressView;
            if (frameLayout != null) {
                AndroidUtilities.updateViewVisibilityAnimated(frameLayout, true, 0.3f, true);
                this.emptyViewContainer.setVisibility(4);
                this.chatListView.setEmptyView(null);
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
        if (!z && !this.messages.isEmpty()) {
            tLRPC$TL_channels_getAdminLog.max_id = this.minEventId;
        } else {
            tLRPC$TL_channels_getAdminLog.max_id = 0L;
        }
        tLRPC$TL_channels_getAdminLog.min_id = 0L;
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
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_channels_getAdminLog, new RequestDelegate() { // from class: org.telegram.ui.ChannelAdminLogActivity$$ExternalSyntheticLambda10
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                ChannelAdminLogActivity.this.lambda$loadMessages$1(tLObject, tLRPC$TL_error);
            }
        });
        if (!z || (chatActivityAdapter = this.chatAdapter) == null) {
            return;
        }
        chatActivityAdapter.notifyDataSetChanged();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$loadMessages$1(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLObject != null) {
            final TLRPC$TL_channels_adminLogResults tLRPC$TL_channels_adminLogResults = (TLRPC$TL_channels_adminLogResults) tLObject;
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ChannelAdminLogActivity$$ExternalSyntheticLambda6
                @Override // java.lang.Runnable
                public final void run() {
                    ChannelAdminLogActivity.this.lambda$loadMessages$0(tLRPC$TL_channels_adminLogResults);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$loadMessages$0(TLRPC$TL_channels_adminLogResults tLRPC$TL_channels_adminLogResults) {
        int i;
        int i2 = 0;
        this.chatListItemAnimator.setShouldAnimateEnterFromBottom(false);
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
                MessageObject messageObject = new MessageObject(this.currentAccount, tLRPC$TL_channelAdminLogEvent, this.messages, this.messagesByDays, this.currentChat, this.mid, false);
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
        AndroidUtilities.updateViewVisibilityAnimated(this.progressView, false, 0.3f, true);
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
            if (findLastVisibleItemPosition == -1) {
                return;
            }
            this.chatLayoutManager.scrollToPositionWithOffset((findLastVisibleItemPosition + size2) - i, paddingTop);
        } else if (this.endReached) {
            this.chatAdapter.notifyItemRemoved(0);
        }
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        ChatMessageCell chatMessageCell;
        MessageObject messageObject;
        ChatMessageCell chatMessageCell2;
        MessageObject messageObject2;
        ChatMessageCell chatMessageCell3;
        MessageObject messageObject3;
        if (i == NotificationCenter.emojiLoaded) {
            RecyclerListView recyclerListView = this.chatListView;
            if (recyclerListView == null) {
                return;
            }
            recyclerListView.invalidateViews();
            return;
        }
        if (i == NotificationCenter.messagePlayingDidStart) {
            if (((MessageObject) objArr[0]).isRoundVideo()) {
                MediaController.getInstance().setTextureView(createTextureView(true), this.aspectRatioFrameLayout, this.roundVideoContainer, true);
                updateTextureViewPosition();
            }
            RecyclerListView recyclerListView2 = this.chatListView;
            if (recyclerListView2 == null) {
                return;
            }
            int childCount = recyclerListView2.getChildCount();
            for (int i3 = 0; i3 < childCount; i3++) {
                View childAt = this.chatListView.getChildAt(i3);
                if ((childAt instanceof ChatMessageCell) && (messageObject3 = (chatMessageCell3 = (ChatMessageCell) childAt).getMessageObject()) != null) {
                    if (messageObject3.isVoice() || messageObject3.isMusic()) {
                        chatMessageCell3.updateButtonState(false, true, false);
                    } else if (messageObject3.isRoundVideo()) {
                        chatMessageCell3.checkVideoPlayback(false, null);
                        if (!MediaController.getInstance().isPlayingMessage(messageObject3) && messageObject3.audioProgress != 0.0f) {
                            messageObject3.resetPlayingProgress();
                            chatMessageCell3.invalidate();
                        }
                    }
                }
            }
        } else if (i == NotificationCenter.messagePlayingDidReset || i == NotificationCenter.messagePlayingPlayStateChanged) {
            RecyclerListView recyclerListView3 = this.chatListView;
            if (recyclerListView3 == null) {
                return;
            }
            int childCount2 = recyclerListView3.getChildCount();
            for (int i4 = 0; i4 < childCount2; i4++) {
                View childAt2 = this.chatListView.getChildAt(i4);
                if ((childAt2 instanceof ChatMessageCell) && (messageObject = (chatMessageCell = (ChatMessageCell) childAt2).getMessageObject()) != null) {
                    if (messageObject.isVoice() || messageObject.isMusic()) {
                        chatMessageCell.updateButtonState(false, true, false);
                    } else if (messageObject.isRoundVideo() && !MediaController.getInstance().isPlayingMessage(messageObject)) {
                        chatMessageCell.checkVideoPlayback(true, null);
                    }
                }
            }
        } else if (i == NotificationCenter.messagePlayingProgressDidChanged) {
            Integer num = (Integer) objArr[0];
            RecyclerListView recyclerListView4 = this.chatListView;
            if (recyclerListView4 == null) {
                return;
            }
            int childCount3 = recyclerListView4.getChildCount();
            for (int i5 = 0; i5 < childCount3; i5++) {
                View childAt3 = this.chatListView.getChildAt(i5);
                if ((childAt3 instanceof ChatMessageCell) && (messageObject2 = (chatMessageCell2 = (ChatMessageCell) childAt3).getMessageObject()) != null && messageObject2.getId() == num.intValue()) {
                    MessageObject playingMessageObject = MediaController.getInstance().getPlayingMessageObject();
                    if (playingMessageObject == null) {
                        return;
                    }
                    messageObject2.audioProgress = playingMessageObject.audioProgress;
                    messageObject2.audioProgressSec = playingMessageObject.audioProgressSec;
                    messageObject2.audioPlayerDuration = playingMessageObject.audioPlayerDuration;
                    chatMessageCell2.updatePlayingMessageProgress();
                    return;
                }
            }
        } else if (i == NotificationCenter.didSetNewWallpapper && this.fragmentView != null) {
            this.contentView.setBackgroundImage(Theme.getCachedWallpaper(), Theme.isWallpaperMotion());
            this.progressView2.invalidate();
            TextView textView = this.emptyView;
            if (textView != null) {
                textView.invalidate();
            }
            this.chatListView.invalidateViews();
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public View createView(Context context) {
        if (this.chatMessageCellsCache.isEmpty()) {
            for (int i = 0; i < 8; i++) {
                this.chatMessageCellsCache.add(new ChatMessageCell(context));
            }
        }
        this.searchWas = false;
        this.hasOwnBackground = true;
        Theme.createChatResources(context, false);
        this.actionBar.setAddToContainer(false);
        this.actionBar.setOccupyStatusBar(Build.VERSION.SDK_INT >= 21 && !AndroidUtilities.isTablet());
        this.actionBar.setBackButtonDrawable(new BackDrawable(false));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() { // from class: org.telegram.ui.ChannelAdminLogActivity.2
            @Override // org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick
            public void onItemClick(int i2) {
                if (i2 == -1) {
                    ChannelAdminLogActivity.this.finishFragment();
                }
            }
        });
        ChatAvatarContainer chatAvatarContainer = new ChatAvatarContainer(context, null, false);
        this.avatarContainer = chatAvatarContainer;
        chatAvatarContainer.setOccupyStatusBar(!AndroidUtilities.isTablet());
        this.actionBar.addView(this.avatarContainer, 0, LayoutHelper.createFrame(-2, -1.0f, 51, 56.0f, 0.0f, 40.0f, 0.0f));
        ActionBarMenuItem actionBarMenuItemSearchListener = this.actionBar.createMenu().addItem(0, R.drawable.ic_ab_search).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener() { // from class: org.telegram.ui.ChannelAdminLogActivity.3
            @Override // org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener
            public void onSearchCollapse() {
                ChannelAdminLogActivity.this.searchQuery = "";
                ChannelAdminLogActivity.this.avatarContainer.setVisibility(0);
                if (ChannelAdminLogActivity.this.searchWas) {
                    ChannelAdminLogActivity.this.searchWas = false;
                    ChannelAdminLogActivity.this.loadMessages(true);
                }
                ChannelAdminLogActivity.this.updateBottomOverlay();
            }

            @Override // org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener
            public void onSearchExpand() {
                ChannelAdminLogActivity.this.avatarContainer.setVisibility(8);
                ChannelAdminLogActivity.this.updateBottomOverlay();
            }

            @Override // org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener
            public void onSearchPressed(EditText editText) {
                ChannelAdminLogActivity.this.searchWas = true;
                ChannelAdminLogActivity.this.searchQuery = editText.getText().toString();
                ChannelAdminLogActivity.this.loadMessages(true);
            }
        });
        this.searchItem = actionBarMenuItemSearchListener;
        actionBarMenuItemSearchListener.setSearchFieldHint(LocaleController.getString("Search", R.string.Search));
        this.avatarContainer.setEnabled(false);
        this.avatarContainer.setTitle(this.currentChat.title);
        this.avatarContainer.setSubtitle(LocaleController.getString("EventLogAllEvents", R.string.EventLogAllEvents));
        this.avatarContainer.setChatAvatar(this.currentChat);
        SizeNotifierFrameLayout sizeNotifierFrameLayout = new SizeNotifierFrameLayout(context) { // from class: org.telegram.ui.ChannelAdminLogActivity.4
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // org.telegram.ui.Components.SizeNotifierFrameLayout, android.view.ViewGroup, android.view.View
            public void onAttachedToWindow() {
                super.onAttachedToWindow();
                MessageObject playingMessageObject = MediaController.getInstance().getPlayingMessageObject();
                if (playingMessageObject == null || !playingMessageObject.isRoundVideo() || playingMessageObject.eventId == 0 || playingMessageObject.getDialogId() != (-ChannelAdminLogActivity.this.currentChat.id)) {
                    return;
                }
                MediaController.getInstance().setTextureView(ChannelAdminLogActivity.this.createTextureView(false), ChannelAdminLogActivity.this.aspectRatioFrameLayout, ChannelAdminLogActivity.this.roundVideoContainer, true);
            }

            @Override // android.view.ViewGroup
            protected boolean drawChild(Canvas canvas, View view, long j) {
                boolean drawChild = super.drawChild(canvas, view, j);
                if (view == ((BaseFragment) ChannelAdminLogActivity.this).actionBar && ((BaseFragment) ChannelAdminLogActivity.this).parentLayout != null) {
                    ((BaseFragment) ChannelAdminLogActivity.this).parentLayout.drawHeaderShadow(canvas, ((BaseFragment) ChannelAdminLogActivity.this).actionBar.getVisibility() == 0 ? ((BaseFragment) ChannelAdminLogActivity.this).actionBar.getMeasuredHeight() : 0);
                }
                return drawChild;
            }

            @Override // org.telegram.ui.Components.SizeNotifierFrameLayout
            protected boolean isActionBarVisible() {
                return ((BaseFragment) ChannelAdminLogActivity.this).actionBar.getVisibility() == 0;
            }

            @Override // android.widget.FrameLayout, android.view.View
            protected void onMeasure(int i2, int i3) {
                int size = View.MeasureSpec.getSize(i2);
                int size2 = View.MeasureSpec.getSize(i3);
                setMeasuredDimension(size, size2);
                int paddingTop = size2 - getPaddingTop();
                measureChildWithMargins(((BaseFragment) ChannelAdminLogActivity.this).actionBar, i2, 0, i3, 0);
                int measuredHeight = ((BaseFragment) ChannelAdminLogActivity.this).actionBar.getMeasuredHeight();
                if (((BaseFragment) ChannelAdminLogActivity.this).actionBar.getVisibility() == 0) {
                    paddingTop -= measuredHeight;
                }
                int childCount = getChildCount();
                for (int i4 = 0; i4 < childCount; i4++) {
                    View childAt = getChildAt(i4);
                    if (childAt != null && childAt.getVisibility() != 8 && childAt != ((BaseFragment) ChannelAdminLogActivity.this).actionBar) {
                        if (childAt != ChannelAdminLogActivity.this.chatListView && childAt != ChannelAdminLogActivity.this.progressView) {
                            if (childAt == ChannelAdminLogActivity.this.emptyViewContainer) {
                                childAt.measure(View.MeasureSpec.makeMeasureSpec(size, NUM), View.MeasureSpec.makeMeasureSpec(paddingTop, NUM));
                            } else {
                                measureChildWithMargins(childAt, i2, 0, i3, 0);
                            }
                        } else {
                            childAt.measure(View.MeasureSpec.makeMeasureSpec(size, NUM), View.MeasureSpec.makeMeasureSpec(Math.max(AndroidUtilities.dp(10.0f), paddingTop - AndroidUtilities.dp(50.0f)), NUM));
                        }
                    }
                }
            }

            /* JADX INFO: Access modifiers changed from: protected */
            /* JADX WARN: Removed duplicated region for block: B:20:0x004e  */
            /* JADX WARN: Removed duplicated region for block: B:31:0x0086  */
            /* JADX WARN: Removed duplicated region for block: B:35:0x009a  */
            /* JADX WARN: Removed duplicated region for block: B:40:0x00bc  */
            @Override // org.telegram.ui.Components.SizeNotifierFrameLayout, android.widget.FrameLayout, android.view.ViewGroup, android.view.View
            /*
                Code decompiled incorrectly, please refer to instructions dump.
                To view partially-correct add '--show-bad-code' argument
            */
            public void onLayout(boolean r10, int r11, int r12, int r13, int r14) {
                /*
                    Method dump skipped, instructions count: 225
                    To view this dump add '--comments-level debug' option
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChannelAdminLogActivity.AnonymousClass4.onLayout(boolean, int, int, int, int):void");
            }

            @Override // android.view.ViewGroup, android.view.View
            public boolean dispatchTouchEvent(MotionEvent motionEvent) {
                if (AvatarPreviewer.hasVisibleInstance()) {
                    AvatarPreviewer.getInstance().onTouchEvent(motionEvent);
                    return true;
                }
                return super.dispatchTouchEvent(motionEvent);
            }
        };
        this.fragmentView = sizeNotifierFrameLayout;
        SizeNotifierFrameLayout sizeNotifierFrameLayout2 = sizeNotifierFrameLayout;
        this.contentView = sizeNotifierFrameLayout2;
        sizeNotifierFrameLayout2.setOccupyStatusBar(!AndroidUtilities.isTablet());
        this.contentView.setBackgroundImage(Theme.getCachedWallpaper(), Theme.isWallpaperMotion());
        FrameLayout frameLayout = new FrameLayout(context);
        this.emptyViewContainer = frameLayout;
        frameLayout.setVisibility(4);
        this.contentView.addView(this.emptyViewContainer, LayoutHelper.createFrame(-1, -2, 17));
        this.emptyViewContainer.setOnTouchListener(ChannelAdminLogActivity$$ExternalSyntheticLambda5.INSTANCE);
        TextView textView = new TextView(context);
        this.emptyView = textView;
        textView.setTextSize(1, 14.0f);
        this.emptyView.setGravity(17);
        this.emptyView.setTextColor(Theme.getColor("chat_serviceText"));
        this.emptyView.setBackground(Theme.createServiceDrawable(AndroidUtilities.dp(6.0f), this.emptyView, this.contentView));
        this.emptyView.setPadding(AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f));
        this.emptyViewContainer.addView(this.emptyView, LayoutHelper.createFrame(-2, -2.0f, 17, 16.0f, 0.0f, 16.0f, 0.0f));
        RecyclerListView recyclerListView = new RecyclerListView(context) { // from class: org.telegram.ui.ChannelAdminLogActivity.5
            @Override // androidx.recyclerview.widget.RecyclerView, android.view.ViewGroup
            public boolean drawChild(Canvas canvas, View view, long j) {
                ChatMessageCell chatMessageCell;
                ImageReceiver avatarImage;
                int y;
                int adapterPosition;
                boolean drawChild = super.drawChild(canvas, view, j);
                if ((view instanceof ChatMessageCell) && (avatarImage = (chatMessageCell = (ChatMessageCell) view).getAvatarImage()) != null) {
                    if (chatMessageCell.getMessageObject().deleted) {
                        avatarImage.setVisible(false, false);
                        return drawChild;
                    }
                    int y2 = (int) view.getY();
                    if (chatMessageCell.drawPinnedBottom() && (adapterPosition = ChannelAdminLogActivity.this.chatListView.getChildViewHolder(view).getAdapterPosition()) >= 0) {
                        if (ChannelAdminLogActivity.this.chatListView.findViewHolderForAdapterPosition(adapterPosition + 1) != null) {
                            avatarImage.setVisible(false, false);
                            return drawChild;
                        }
                    }
                    float slidingOffsetX = chatMessageCell.getSlidingOffsetX() + chatMessageCell.getCheckBoxTranslation();
                    int y3 = ((int) view.getY()) + chatMessageCell.getLayoutHeight();
                    int measuredHeight = ChannelAdminLogActivity.this.chatListView.getMeasuredHeight() - ChannelAdminLogActivity.this.chatListView.getPaddingBottom();
                    if (y3 > measuredHeight) {
                        y3 = measuredHeight;
                    }
                    if (chatMessageCell.drawPinnedTop() && (r11 = ChannelAdminLogActivity.this.chatListView.getChildViewHolder(view).getAdapterPosition()) >= 0) {
                        int i2 = 0;
                        while (i2 < 20) {
                            i2++;
                            int adapterPosition2 = adapterPosition2 - 1;
                            RecyclerView.ViewHolder findViewHolderForAdapterPosition = ChannelAdminLogActivity.this.chatListView.findViewHolderForAdapterPosition(adapterPosition2);
                            if (findViewHolderForAdapterPosition == null) {
                                break;
                            }
                            y2 = findViewHolderForAdapterPosition.itemView.getTop();
                            View view2 = findViewHolderForAdapterPosition.itemView;
                            if (!(view2 instanceof ChatMessageCell)) {
                                break;
                            }
                            chatMessageCell = (ChatMessageCell) view2;
                            if (!chatMessageCell.drawPinnedTop()) {
                                break;
                            }
                        }
                    }
                    if (y3 - AndroidUtilities.dp(48.0f) < y2) {
                        y3 = y2 + AndroidUtilities.dp(48.0f);
                    }
                    if (!chatMessageCell.drawPinnedBottom() && y3 > (y = (int) (chatMessageCell.getY() + chatMessageCell.getMeasuredHeight()))) {
                        y3 = y;
                    }
                    canvas.save();
                    if (slidingOffsetX != 0.0f) {
                        canvas.translate(slidingOffsetX, 0.0f);
                    }
                    if (chatMessageCell.getCurrentMessagesGroup() != null && chatMessageCell.getCurrentMessagesGroup().transitionParams.backgroundChangeBounds) {
                        y3 = (int) (y3 - chatMessageCell.getTranslationY());
                    }
                    avatarImage.setImageY(y3 - AndroidUtilities.dp(44.0f));
                    if (chatMessageCell.shouldDrawAlphaLayer()) {
                        avatarImage.setAlpha(chatMessageCell.getAlpha());
                        canvas.scale(chatMessageCell.getScaleX(), chatMessageCell.getScaleY(), chatMessageCell.getX() + chatMessageCell.getPivotX(), chatMessageCell.getY() + (chatMessageCell.getHeight() >> 1));
                    } else {
                        avatarImage.setAlpha(1.0f);
                    }
                    avatarImage.setVisible(true, false);
                    avatarImage.draw(canvas);
                    canvas.restore();
                }
                return drawChild;
            }
        };
        this.chatListView = recyclerListView;
        recyclerListView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() { // from class: org.telegram.ui.ChannelAdminLogActivity$$ExternalSyntheticLambda12
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
            public final void onItemClick(View view, int i2) {
                ChannelAdminLogActivity.this.lambda$createView$3(view, i2);
            }
        });
        this.chatListView.setTag(1);
        this.chatListView.setVerticalScrollBarEnabled(true);
        RecyclerListView recyclerListView2 = this.chatListView;
        ChatActivityAdapter chatActivityAdapter = new ChatActivityAdapter(context);
        this.chatAdapter = chatActivityAdapter;
        recyclerListView2.setAdapter(chatActivityAdapter);
        this.chatListView.setClipToPadding(false);
        this.chatListView.setPadding(0, AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(3.0f));
        RecyclerListView recyclerListView3 = this.chatListView;
        AnonymousClass6 anonymousClass6 = new AnonymousClass6(null, this.chatListView, null);
        this.chatListItemAnimator = anonymousClass6;
        recyclerListView3.setItemAnimator(anonymousClass6);
        this.chatListItemAnimator.setReversePositions(true);
        this.chatListView.setLayoutAnimation(null);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, context) { // from class: org.telegram.ui.ChannelAdminLogActivity.7
            @Override // androidx.recyclerview.widget.LinearLayoutManager, androidx.recyclerview.widget.RecyclerView.LayoutManager
            public boolean supportsPredictiveItemAnimations() {
                return true;
            }

            @Override // androidx.recyclerview.widget.LinearLayoutManager, androidx.recyclerview.widget.RecyclerView.LayoutManager
            public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int i2) {
                LinearSmoothScrollerCustom linearSmoothScrollerCustom = new LinearSmoothScrollerCustom(recyclerView.getContext(), 0);
                linearSmoothScrollerCustom.setTargetPosition(i2);
                startSmoothScroll(linearSmoothScrollerCustom);
            }
        };
        this.chatLayoutManager = linearLayoutManager;
        linearLayoutManager.setOrientation(1);
        this.chatLayoutManager.setStackFromEnd(true);
        this.chatListView.setLayoutManager(this.chatLayoutManager);
        this.contentView.addView(this.chatListView, LayoutHelper.createFrame(-1, -1.0f));
        this.chatListView.setOnScrollListener(new RecyclerView.OnScrollListener() { // from class: org.telegram.ui.ChannelAdminLogActivity.8
            {
                AndroidUtilities.dp(100.0f);
            }

            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrollStateChanged(RecyclerView recyclerView, int i2) {
                if (i2 == 1) {
                    ChannelAdminLogActivity.this.scrollingFloatingDate = true;
                    ChannelAdminLogActivity.this.checkTextureViewPosition = true;
                } else if (i2 != 0) {
                } else {
                    ChannelAdminLogActivity.this.scrollingFloatingDate = false;
                    ChannelAdminLogActivity.this.checkTextureViewPosition = false;
                    ChannelAdminLogActivity.this.hideFloatingDateView(true);
                }
            }

            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrolled(RecyclerView recyclerView, int i2, int i3) {
                ChannelAdminLogActivity.this.chatListView.invalidate();
                if (i3 != 0 && ChannelAdminLogActivity.this.scrollingFloatingDate && !ChannelAdminLogActivity.this.currentFloatingTopIsNotMessage && ChannelAdminLogActivity.this.floatingDateView.getTag() == null) {
                    if (ChannelAdminLogActivity.this.floatingDateAnimation != null) {
                        ChannelAdminLogActivity.this.floatingDateAnimation.cancel();
                    }
                    ChannelAdminLogActivity.this.floatingDateView.setTag(1);
                    ChannelAdminLogActivity.this.floatingDateAnimation = new AnimatorSet();
                    ChannelAdminLogActivity.this.floatingDateAnimation.setDuration(150L);
                    ChannelAdminLogActivity.this.floatingDateAnimation.playTogether(ObjectAnimator.ofFloat(ChannelAdminLogActivity.this.floatingDateView, "alpha", 1.0f));
                    ChannelAdminLogActivity.this.floatingDateAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.ChannelAdminLogActivity.8.1
                        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                        public void onAnimationEnd(Animator animator) {
                            if (animator.equals(ChannelAdminLogActivity.this.floatingDateAnimation)) {
                                ChannelAdminLogActivity.this.floatingDateAnimation = null;
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
        FrameLayout frameLayout2 = new FrameLayout(context);
        this.progressView = frameLayout2;
        frameLayout2.setVisibility(4);
        this.contentView.addView(this.progressView, LayoutHelper.createFrame(-1, -1, 51));
        View view = new View(context);
        this.progressView2 = view;
        view.setBackground(Theme.createServiceDrawable(AndroidUtilities.dp(18.0f), this.progressView2, this.contentView));
        this.progressView.addView(this.progressView2, LayoutHelper.createFrame(36, 36, 17));
        RadialProgressView radialProgressView = new RadialProgressView(context);
        this.progressBar = radialProgressView;
        radialProgressView.setSize(AndroidUtilities.dp(28.0f));
        this.progressBar.setProgressColor(Theme.getColor("chat_serviceText"));
        this.progressView.addView(this.progressBar, LayoutHelper.createFrame(32, 32, 17));
        ChatActionCell chatActionCell = new ChatActionCell(context);
        this.floatingDateView = chatActionCell;
        chatActionCell.setAlpha(0.0f);
        this.floatingDateView.setImportantForAccessibility(2);
        this.contentView.addView(this.floatingDateView, LayoutHelper.createFrame(-2, -2.0f, 49, 0.0f, 4.0f, 0.0f, 0.0f));
        this.contentView.addView(this.actionBar);
        FrameLayout frameLayout3 = new FrameLayout(this, context) { // from class: org.telegram.ui.ChannelAdminLogActivity.9
            @Override // android.view.View
            public void onDraw(Canvas canvas) {
                int intrinsicHeight = Theme.chat_composeShadowDrawable.getIntrinsicHeight();
                Theme.chat_composeShadowDrawable.setBounds(0, 0, getMeasuredWidth(), intrinsicHeight);
                Theme.chat_composeShadowDrawable.draw(canvas);
                canvas.drawRect(0.0f, intrinsicHeight, getMeasuredWidth(), getMeasuredHeight(), Theme.chat_composeBackgroundPaint);
            }
        };
        this.bottomOverlayChat = frameLayout3;
        frameLayout3.setWillNotDraw(false);
        this.bottomOverlayChat.setPadding(0, AndroidUtilities.dp(3.0f), 0, 0);
        this.contentView.addView(this.bottomOverlayChat, LayoutHelper.createFrame(-1, 51, 80));
        this.bottomOverlayChat.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.ChannelAdminLogActivity$$ExternalSyntheticLambda3
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                ChannelAdminLogActivity.this.lambda$createView$5(view2);
            }
        });
        TextView textView2 = new TextView(context);
        this.bottomOverlayChatText = textView2;
        textView2.setTextSize(1, 15.0f);
        this.bottomOverlayChatText.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.bottomOverlayChatText.setTextColor(Theme.getColor("chat_fieldOverlayText"));
        this.bottomOverlayChatText.setText(LocaleController.getString("SETTINGS", R.string.SETTINGS).toUpperCase());
        this.bottomOverlayChat.addView(this.bottomOverlayChatText, LayoutHelper.createFrame(-2, -2, 17));
        ImageView imageView = new ImageView(context);
        this.bottomOverlayImage = imageView;
        imageView.setImageResource(R.drawable.msg_help);
        this.bottomOverlayImage.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_fieldOverlayText"), PorterDuff.Mode.MULTIPLY));
        this.bottomOverlayImage.setScaleType(ImageView.ScaleType.CENTER);
        this.bottomOverlayChat.addView(this.bottomOverlayImage, LayoutHelper.createFrame(48, 48.0f, 53, 3.0f, 0.0f, 0.0f, 0.0f));
        this.bottomOverlayImage.setContentDescription(LocaleController.getString("BotHelp", R.string.BotHelp));
        this.bottomOverlayImage.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.ChannelAdminLogActivity$$ExternalSyntheticLambda2
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                ChannelAdminLogActivity.this.lambda$createView$6(view2);
            }
        });
        FrameLayout frameLayout4 = new FrameLayout(this, context) { // from class: org.telegram.ui.ChannelAdminLogActivity.10
            @Override // android.view.View
            public void onDraw(Canvas canvas) {
                int intrinsicHeight = Theme.chat_composeShadowDrawable.getIntrinsicHeight();
                Theme.chat_composeShadowDrawable.setBounds(0, 0, getMeasuredWidth(), intrinsicHeight);
                Theme.chat_composeShadowDrawable.draw(canvas);
                canvas.drawRect(0.0f, intrinsicHeight, getMeasuredWidth(), getMeasuredHeight(), Theme.chat_composeBackgroundPaint);
            }
        };
        this.searchContainer = frameLayout4;
        frameLayout4.setWillNotDraw(false);
        this.searchContainer.setVisibility(4);
        this.searchContainer.setFocusable(true);
        this.searchContainer.setFocusableInTouchMode(true);
        this.searchContainer.setClickable(true);
        this.searchContainer.setPadding(0, AndroidUtilities.dp(3.0f), 0, 0);
        this.contentView.addView(this.searchContainer, LayoutHelper.createFrame(-1, 51, 80));
        ImageView imageView2 = new ImageView(context);
        this.searchCalendarButton = imageView2;
        imageView2.setScaleType(ImageView.ScaleType.CENTER);
        this.searchCalendarButton.setImageResource(R.drawable.msg_calendar);
        this.searchCalendarButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_searchPanelIcons"), PorterDuff.Mode.MULTIPLY));
        this.searchContainer.addView(this.searchCalendarButton, LayoutHelper.createFrame(48, 48, 53));
        this.searchCalendarButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.ChannelAdminLogActivity$$ExternalSyntheticLambda4
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                ChannelAdminLogActivity.this.lambda$createView$8(view2);
            }
        });
        SimpleTextView simpleTextView = new SimpleTextView(context);
        this.searchCountText = simpleTextView;
        simpleTextView.setTextColor(Theme.getColor("chat_searchPanelText"));
        this.searchCountText.setTextSize(15);
        this.searchCountText.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.searchContainer.addView(this.searchCountText, LayoutHelper.createFrame(-1, -2.0f, 19, 108.0f, 0.0f, 0.0f, 0.0f));
        this.chatAdapter.updateRows();
        if (this.loading && this.messages.isEmpty()) {
            AndroidUtilities.updateViewVisibilityAnimated(this.progressView, true, 0.3f, true);
            this.chatListView.setEmptyView(null);
        } else {
            AndroidUtilities.updateViewVisibilityAnimated(this.progressView, false, 0.3f, true);
            this.chatListView.setEmptyView(this.emptyViewContainer);
        }
        this.chatListView.setAnimateEmptyView(true, 1);
        UndoView undoView = new UndoView(context);
        this.undoView = undoView;
        undoView.setAdditionalTranslationY(AndroidUtilities.dp(51.0f));
        this.contentView.addView(this.undoView, LayoutHelper.createFrame(-1, -2.0f, 83, 8.0f, 0.0f, 8.0f, 8.0f));
        updateEmptyPlaceholder();
        return this.fragmentView;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$3(View view, int i) {
        createMenu(view);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: org.telegram.ui.ChannelAdminLogActivity$6  reason: invalid class name */
    /* loaded from: classes3.dex */
    public class AnonymousClass6 extends ChatListItemAnimator {
        Runnable finishRunnable;
        int scrollAnimationIndex;

        AnonymousClass6(ChatActivity chatActivity, RecyclerListView recyclerListView, Theme.ResourcesProvider resourcesProvider) {
            super(chatActivity, recyclerListView, resourcesProvider);
            this.scrollAnimationIndex = -1;
        }

        @Override // androidx.recyclerview.widget.ChatListItemAnimator
        public void onAnimationStart() {
            if (this.scrollAnimationIndex == -1) {
                this.scrollAnimationIndex = ChannelAdminLogActivity.this.getNotificationCenter().setAnimationInProgress(this.scrollAnimationIndex, null, false);
            }
            Runnable runnable = this.finishRunnable;
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
                this.finishRunnable = null;
            }
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("admin logs chatItemAnimator disable notifications");
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // androidx.recyclerview.widget.ChatListItemAnimator, androidx.recyclerview.widget.DefaultItemAnimator
        public void onAllAnimationsDone() {
            super.onAllAnimationsDone();
            Runnable runnable = this.finishRunnable;
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
            }
            Runnable runnable2 = new Runnable() { // from class: org.telegram.ui.ChannelAdminLogActivity$6$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    ChannelAdminLogActivity.AnonymousClass6.this.lambda$onAllAnimationsDone$0();
                }
            };
            this.finishRunnable = runnable2;
            AndroidUtilities.runOnUIThread(runnable2);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onAllAnimationsDone$0() {
            if (this.scrollAnimationIndex != -1) {
                ChannelAdminLogActivity.this.getNotificationCenter().onAnimationFinish(this.scrollAnimationIndex);
                this.scrollAnimationIndex = -1;
            }
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("admin logs chatItemAnimator enable notifications");
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$5(View view) {
        if (getParentActivity() == null) {
            return;
        }
        AdminLogFilterAlert adminLogFilterAlert = new AdminLogFilterAlert(getParentActivity(), this.currentFilter, this.selectedAdmins, this.currentChat.megagroup);
        adminLogFilterAlert.setCurrentAdmins(this.admins);
        adminLogFilterAlert.setAdminLogFilterAlertDelegate(new AdminLogFilterAlert.AdminLogFilterAlertDelegate() { // from class: org.telegram.ui.ChannelAdminLogActivity$$ExternalSyntheticLambda11
            @Override // org.telegram.ui.Components.AdminLogFilterAlert.AdminLogFilterAlertDelegate
            public final void didSelectRights(TLRPC$TL_channelAdminLogEventsFilter tLRPC$TL_channelAdminLogEventsFilter, LongSparseArray longSparseArray) {
                ChannelAdminLogActivity.this.lambda$createView$4(tLRPC$TL_channelAdminLogEventsFilter, longSparseArray);
            }
        });
        showDialog(adminLogFilterAlert);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$4(TLRPC$TL_channelAdminLogEventsFilter tLRPC$TL_channelAdminLogEventsFilter, LongSparseArray longSparseArray) {
        this.currentFilter = tLRPC$TL_channelAdminLogEventsFilter;
        this.selectedAdmins = longSparseArray;
        if (tLRPC$TL_channelAdminLogEventsFilter != null || longSparseArray != null) {
            this.avatarContainer.setSubtitle(LocaleController.getString("EventLogSelectedEvents", R.string.EventLogSelectedEvents));
        } else {
            this.avatarContainer.setSubtitle(LocaleController.getString("EventLogAllEvents", R.string.EventLogAllEvents));
        }
        loadMessages(true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$6(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        if (this.currentChat.megagroup) {
            builder.setMessage(AndroidUtilities.replaceTags(LocaleController.getString("EventLogInfoDetail", R.string.EventLogInfoDetail)));
        } else {
            builder.setMessage(AndroidUtilities.replaceTags(LocaleController.getString("EventLogInfoDetailChannel", R.string.EventLogInfoDetailChannel)));
        }
        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
        builder.setTitle(LocaleController.getString("EventLogInfoTitle", R.string.EventLogInfoTitle));
        showDialog(builder.create());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$8(View view) {
        if (getParentActivity() == null) {
            return;
        }
        AndroidUtilities.hideKeyboard(this.searchItem.getSearchField());
        showDialog(AlertsCreator.createCalendarPickerDialog(getParentActivity(), 1375315200000L, new MessagesStorage.IntCallback() { // from class: org.telegram.ui.ChannelAdminLogActivity$$ExternalSyntheticLambda8
            @Override // org.telegram.messenger.MessagesStorage.IntCallback
            public final void run(int i) {
                ChannelAdminLogActivity.this.lambda$createView$7(i);
            }
        }, null).create());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$7(int i) {
        loadMessages(true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean createMenu(View view) {
        MessageObject messageObject;
        if (view instanceof ChatMessageCell) {
            messageObject = ((ChatMessageCell) view).getMessageObject();
        } else {
            messageObject = view instanceof ChatActionCell ? ((ChatActionCell) view).getMessageObject() : null;
        }
        if (messageObject == null) {
            return false;
        }
        int messageType = getMessageType(messageObject);
        this.selectedObject = messageObject;
        if (getParentActivity() == null) {
            return false;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        ArrayList arrayList = new ArrayList();
        final ArrayList arrayList2 = new ArrayList();
        MessageObject messageObject2 = this.selectedObject;
        if (messageObject2.type == 0 || messageObject2.caption != null) {
            arrayList.add(LocaleController.getString("Copy", R.string.Copy));
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
                        return true;
                    }
                }
            }
            if (tLRPC$TL_channelAdminLogEvent != null && (tLRPC$TL_channelAdminLogEvent.action instanceof TLRPC$TL_channelAdminLogEventActionChangeHistoryTTL) && ChatObject.canUserDoAdminAction(this.currentChat, 13)) {
                ClearHistoryAlert clearHistoryAlert = new ClearHistoryAlert(getParentActivity(), null, this.currentChat, false, null);
                clearHistoryAlert.setDelegate(new ClearHistoryAlert.ClearHistoryAlertDelegate() { // from class: org.telegram.ui.ChannelAdminLogActivity.11
                    @Override // org.telegram.ui.Components.ClearHistoryAlert.ClearHistoryAlertDelegate
                    public /* synthetic */ void onClearHistory(boolean z) {
                        ClearHistoryAlert.ClearHistoryAlertDelegate.CC.$default$onClearHistory(this, z);
                    }

                    @Override // org.telegram.ui.Components.ClearHistoryAlert.ClearHistoryAlertDelegate
                    public void onAutoDeleteHistory(int i, int i2) {
                        ChannelAdminLogActivity.this.getMessagesController().setDialogHistoryTTL(-ChannelAdminLogActivity.this.currentChat.id, i);
                        TLRPC$ChatFull chatFull = ChannelAdminLogActivity.this.getMessagesController().getChatFull(ChannelAdminLogActivity.this.currentChat.id);
                        if (chatFull != null) {
                            ChannelAdminLogActivity.this.undoView.showWithAction(-ChannelAdminLogActivity.this.currentChat.id, i2, (Object) null, Integer.valueOf(chatFull.ttl_period), (Runnable) null, (Runnable) null);
                        }
                    }
                });
                showDialog(clearHistoryAlert);
            }
        } else if (messageType == 3) {
            TLRPC$MessageMedia tLRPC$MessageMedia = this.selectedObject.messageOwner.media;
            if ((tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaWebPage) && MessageObject.isNewGifDocument(tLRPC$MessageMedia.webpage.document)) {
                arrayList.add(LocaleController.getString("SaveToGIFs", R.string.SaveToGIFs));
                arrayList2.add(11);
            }
        } else if (messageType == 4) {
            if (this.selectedObject.isVideo()) {
                arrayList.add(LocaleController.getString("SaveToGallery", R.string.SaveToGallery));
                arrayList2.add(4);
                arrayList.add(LocaleController.getString("ShareFile", R.string.ShareFile));
                arrayList2.add(6);
            } else if (this.selectedObject.isMusic()) {
                arrayList.add(LocaleController.getString("SaveToMusic", R.string.SaveToMusic));
                arrayList2.add(10);
                arrayList.add(LocaleController.getString("ShareFile", R.string.ShareFile));
                arrayList2.add(6);
            } else if (this.selectedObject.getDocument() != null) {
                if (MessageObject.isNewGifDocument(this.selectedObject.getDocument())) {
                    arrayList.add(LocaleController.getString("SaveToGIFs", R.string.SaveToGIFs));
                    arrayList2.add(11);
                }
                arrayList.add(LocaleController.getString("SaveToDownloads", R.string.SaveToDownloads));
                arrayList2.add(10);
                arrayList.add(LocaleController.getString("ShareFile", R.string.ShareFile));
                arrayList2.add(6);
            } else {
                arrayList.add(LocaleController.getString("SaveToGallery", R.string.SaveToGallery));
                arrayList2.add(4);
            }
        } else if (messageType == 5) {
            arrayList.add(LocaleController.getString("ApplyLocalizationFile", R.string.ApplyLocalizationFile));
            arrayList2.add(5);
            arrayList.add(LocaleController.getString("SaveToDownloads", R.string.SaveToDownloads));
            arrayList2.add(10);
            arrayList.add(LocaleController.getString("ShareFile", R.string.ShareFile));
            arrayList2.add(6);
        } else if (messageType == 10) {
            arrayList.add(LocaleController.getString("ApplyThemeFile", R.string.ApplyThemeFile));
            arrayList2.add(5);
            arrayList.add(LocaleController.getString("SaveToDownloads", R.string.SaveToDownloads));
            arrayList2.add(10);
            arrayList.add(LocaleController.getString("ShareFile", R.string.ShareFile));
            arrayList2.add(6);
        } else if (messageType == 6) {
            arrayList.add(LocaleController.getString("SaveToGallery", R.string.SaveToGallery));
            arrayList2.add(7);
            arrayList.add(LocaleController.getString("SaveToDownloads", R.string.SaveToDownloads));
            arrayList2.add(10);
            arrayList.add(LocaleController.getString("ShareFile", R.string.ShareFile));
            arrayList2.add(6);
        } else if (messageType == 7) {
            if (this.selectedObject.isMask()) {
                arrayList.add(LocaleController.getString("AddToMasks", R.string.AddToMasks));
            } else {
                arrayList.add(LocaleController.getString("AddToStickers", R.string.AddToStickers));
            }
            arrayList2.add(9);
        } else if (messageType == 8) {
            long j = this.selectedObject.messageOwner.media.user_id;
            TLRPC$User user = j != 0 ? MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(j)) : null;
            if (user != null && user.id != UserConfig.getInstance(this.currentAccount).getClientUserId() && ContactsController.getInstance(this.currentAccount).contactsDict.get(Long.valueOf(user.id)) == null) {
                arrayList.add(LocaleController.getString("AddContactTitle", R.string.AddContactTitle));
                arrayList2.add(15);
            }
            if (!TextUtils.isEmpty(this.selectedObject.messageOwner.media.phone_number)) {
                arrayList.add(LocaleController.getString("Copy", R.string.Copy));
                arrayList2.add(16);
                arrayList.add(LocaleController.getString("Call", R.string.Call));
                arrayList2.add(17);
            }
        }
        if (arrayList2.isEmpty()) {
            return false;
        }
        builder.setItems((CharSequence[]) arrayList.toArray(new CharSequence[0]), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.ChannelAdminLogActivity$$ExternalSyntheticLambda1
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                ChannelAdminLogActivity.this.lambda$createMenu$9(arrayList2, dialogInterface, i);
            }
        });
        builder.setTitle(LocaleController.getString("Message", R.string.Message));
        showDialog(builder.create());
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createMenu$9(ArrayList arrayList, DialogInterface dialogInterface, int i) {
        if (this.selectedObject == null || i < 0 || i >= arrayList.size()) {
            return;
        }
        processSelectedOption(((Integer) arrayList.get(i)).intValue());
    }

    private CharSequence getMessageContent(MessageObject messageObject, int i, boolean z) {
        TLRPC$Chat chat;
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        if (z) {
            long fromChatId = messageObject.getFromChatId();
            if (i != fromChatId) {
                if (fromChatId > 0) {
                    TLRPC$User user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(fromChatId));
                    if (user != null) {
                        spannableStringBuilder.append((CharSequence) ContactsController.formatName(user.first_name, user.last_name)).append((CharSequence) ":\n");
                    }
                } else if (fromChatId < 0 && (chat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(-fromChatId))) != null) {
                    spannableStringBuilder.append((CharSequence) chat.title).append((CharSequence) ":\n");
                }
            }
        }
        if (TextUtils.isEmpty(messageObject.messageText)) {
            spannableStringBuilder.append((CharSequence) messageObject.messageOwner.message);
        } else {
            spannableStringBuilder.append(messageObject.messageText);
        }
        return spannableStringBuilder;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public TextureView createTextureView(boolean z) {
        if (this.parentLayout == null) {
            return null;
        }
        if (this.roundVideoContainer == null) {
            if (Build.VERSION.SDK_INT >= 21) {
                FrameLayout frameLayout = new FrameLayout(getParentActivity()) { // from class: org.telegram.ui.ChannelAdminLogActivity.12
                    @Override // android.view.View
                    public void setTranslationY(float f) {
                        super.setTranslationY(f);
                        ChannelAdminLogActivity.this.contentView.invalidate();
                    }
                };
                this.roundVideoContainer = frameLayout;
                frameLayout.setOutlineProvider(new ViewOutlineProvider(this) { // from class: org.telegram.ui.ChannelAdminLogActivity.13
                    @Override // android.view.ViewOutlineProvider
                    @TargetApi(21)
                    public void getOutline(View view, Outline outline) {
                        int i = AndroidUtilities.roundMessageSize;
                        outline.setOval(0, 0, i, i);
                    }
                });
                this.roundVideoContainer.setClipToOutline(true);
            } else {
                this.roundVideoContainer = new FrameLayout(getParentActivity()) { // from class: org.telegram.ui.ChannelAdminLogActivity.14
                    @Override // android.view.View
                    protected void onSizeChanged(int i, int i2, int i3, int i4) {
                        super.onSizeChanged(i, i2, i3, i4);
                        ChannelAdminLogActivity.this.aspectPath.reset();
                        float f = i / 2;
                        ChannelAdminLogActivity.this.aspectPath.addCircle(f, i2 / 2, f, Path.Direction.CW);
                        ChannelAdminLogActivity.this.aspectPath.toggleInverseFillType();
                    }

                    @Override // android.view.View
                    public void setTranslationY(float f) {
                        super.setTranslationY(f);
                        ChannelAdminLogActivity.this.contentView.invalidate();
                    }

                    @Override // android.view.View
                    public void setVisibility(int i) {
                        super.setVisibility(i);
                        if (i == 0) {
                            setLayerType(2, null);
                        }
                    }

                    @Override // android.view.ViewGroup, android.view.View
                    protected void dispatchDraw(Canvas canvas) {
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
            AspectRatioFrameLayout aspectRatioFrameLayout = new AspectRatioFrameLayout(getParentActivity());
            this.aspectRatioFrameLayout = aspectRatioFrameLayout;
            aspectRatioFrameLayout.setBackgroundColor(0);
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
            FrameLayout frameLayout2 = this.roundVideoContainer;
            int i = AndroidUtilities.roundMessageSize;
            sizeNotifierFrameLayout.addView(frameLayout2, 1, new FrameLayout.LayoutParams(i, i));
        }
        this.roundVideoContainer.setVisibility(4);
        this.aspectRatioFrameLayout.setDrawingReady(false);
        return this.videoTextureView;
    }

    /* JADX WARN: Code restructure failed: missing block: B:93:0x0258, code lost:
        if (r0.exists() != false) goto L91;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void processSelectedOption(int r11) {
        /*
            Method dump skipped, instructions count: 1004
            To view this dump add '--comments-level debug' option
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
        } else if (messageObject.isVoice()) {
            return 2;
        } else {
            if (messageObject.isSticker() || messageObject.isAnimatedSticker()) {
                TLRPC$InputStickerSet inputStickerSet = messageObject.getInputStickerSet();
                if (inputStickerSet instanceof TLRPC$TL_inputStickerSetID) {
                    if (!MediaDataController.getInstance(this.currentAccount).isStickerPackInstalled(inputStickerSet.id)) {
                        return 7;
                    }
                } else if ((inputStickerSet instanceof TLRPC$TL_inputStickerSetShortName) && !MediaDataController.getInstance(this.currentAccount).isStickerPackInstalled(inputStickerSet.short_name)) {
                    return 7;
                }
            } else if ((!messageObject.isRoundVideo() || (messageObject.isRoundVideo() && BuildVars.DEBUG_VERSION)) && ((messageObject.messageOwner.media instanceof TLRPC$TL_messageMediaPhoto) || messageObject.getDocument() != null || messageObject.isMusic() || messageObject.isVideo())) {
                boolean z2 = false;
                String str2 = messageObject.messageOwner.attachPath;
                if (str2 != null && str2.length() != 0 && new File(messageObject.messageOwner.attachPath).exists()) {
                    z2 = true;
                }
                if (z2 || !getFileLoader().getPathToMessage(messageObject.messageOwner).exists()) {
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
                    return (str.endsWith("/png") || str.endsWith("/jpg") || str.endsWith("/jpeg")) ? 6 : 4;
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
    }

    private void loadAdmins() {
        TLRPC$TL_channels_getParticipants tLRPC$TL_channels_getParticipants = new TLRPC$TL_channels_getParticipants();
        tLRPC$TL_channels_getParticipants.channel = MessagesController.getInputChannel(this.currentChat);
        tLRPC$TL_channels_getParticipants.filter = new TLRPC$TL_channelParticipantsAdmins();
        tLRPC$TL_channels_getParticipants.offset = 0;
        tLRPC$TL_channels_getParticipants.limit = 200;
        ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_channels_getParticipants, new RequestDelegate() { // from class: org.telegram.ui.ChannelAdminLogActivity$$ExternalSyntheticLambda9
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                ChannelAdminLogActivity.this.lambda$loadAdmins$11(tLObject, tLRPC$TL_error);
            }
        }), this.classGuid);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$loadAdmins$11(final TLObject tLObject, final TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ChannelAdminLogActivity$$ExternalSyntheticLambda7
            @Override // java.lang.Runnable
            public final void run() {
                ChannelAdminLogActivity.this.lambda$loadAdmins$10(tLRPC$TL_error, tLObject);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$loadAdmins$10(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject) {
        if (tLRPC$TL_error == null) {
            TLRPC$TL_channels_channelParticipants tLRPC$TL_channels_channelParticipants = (TLRPC$TL_channels_channelParticipants) tLObject;
            getMessagesController().putUsers(tLRPC$TL_channels_channelParticipants.users, false);
            getMessagesController().putChats(tLRPC$TL_channels_channelParticipants.chats, false);
            ArrayList<TLRPC$ChannelParticipant> arrayList = tLRPC$TL_channels_channelParticipants.participants;
            this.admins = arrayList;
            Dialog dialog = this.visibleDialog;
            if (!(dialog instanceof AdminLogFilterAlert)) {
                return;
            }
            ((AdminLogFilterAlert) dialog).setCurrentAdmins(arrayList);
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onRemoveFromParent() {
        MediaController.getInstance().setTextureView(this.videoTextureView, null, null, false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void hideFloatingDateView(boolean z) {
        if (this.floatingDateView.getTag() == null || this.currentFloatingDateOnScreen) {
            return;
        }
        if (this.scrollingFloatingDate && !this.currentFloatingTopIsNotMessage) {
            return;
        }
        this.floatingDateView.setTag(null);
        if (z) {
            AnimatorSet animatorSet = new AnimatorSet();
            this.floatingDateAnimation = animatorSet;
            animatorSet.setDuration(150L);
            this.floatingDateAnimation.playTogether(ObjectAnimator.ofFloat(this.floatingDateView, "alpha", 0.0f));
            this.floatingDateAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.ChannelAdminLogActivity.15
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator) {
                    if (animator.equals(ChannelAdminLogActivity.this.floatingDateAnimation)) {
                        ChannelAdminLogActivity.this.floatingDateAnimation = null;
                    }
                }
            });
            this.floatingDateAnimation.setStartDelay(500L);
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

    /* JADX INFO: Access modifiers changed from: private */
    public void checkScrollForLoad(boolean z) {
        LinearLayoutManager linearLayoutManager = this.chatLayoutManager;
        if (linearLayoutManager == null || this.paused) {
            return;
        }
        int findFirstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
        if ((findFirstVisibleItemPosition == -1 ? 0 : Math.abs(this.chatLayoutManager.findLastVisibleItemPosition() - findFirstVisibleItemPosition) + 1) <= 0) {
            return;
        }
        this.chatAdapter.getItemCount();
        if (findFirstVisibleItemPosition > (z ? 25 : 5) || this.loading || this.endReached) {
            return;
        }
        loadMessages(false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void moveScrollToLastMessage() {
        if (this.chatListView == null || this.messages.isEmpty()) {
            return;
        }
        this.chatLayoutManager.scrollToPositionWithOffset(this.messages.size() - 1, (-100000) - this.chatListView.getPaddingTop());
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
                    this.roundVideoContainer.setTranslationY(this.fragmentView.getPaddingTop() + chatMessageCell.getTop() + photoImage.getImageY());
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
                this.roundVideoContainer.setTranslationY((-AndroidUtilities.roundMessageSize) - 100);
                this.fragmentView.invalidate();
                if (playingMessageObject == null || !playingMessageObject.isRoundVideo()) {
                    return;
                }
                if (!this.checkTextureViewPosition && PipRoundVideoView.getInstance() == null) {
                    return;
                }
                MediaController.getInstance().setCurrentVideoVisible(false);
                return;
            }
            MediaController.getInstance().setCurrentVideoVisible(true);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateMessagesVisisblePart() {
        boolean z;
        MessageObject messageObject;
        RecyclerListView recyclerListView = this.chatListView;
        if (recyclerListView == null) {
            return;
        }
        int childCount = recyclerListView.getChildCount();
        int measuredHeight = this.chatListView.getMeasuredHeight();
        int i = Integer.MAX_VALUE;
        int i2 = Integer.MAX_VALUE;
        boolean z2 = false;
        ChatMessageCell chatMessageCell = null;
        View view = null;
        View view2 = null;
        for (int i3 = 0; i3 < childCount; i3++) {
            View childAt = this.chatListView.getChildAt(i3);
            if (childAt instanceof ChatMessageCell) {
                ChatMessageCell chatMessageCell2 = (ChatMessageCell) childAt;
                int top = chatMessageCell2.getTop();
                chatMessageCell2.getBottom();
                int i4 = top >= 0 ? 0 : -top;
                int measuredHeight2 = chatMessageCell2.getMeasuredHeight();
                if (measuredHeight2 > measuredHeight) {
                    measuredHeight2 = i4 + measuredHeight;
                }
                chatMessageCell2.setVisiblePart(i4, measuredHeight2 - i4, (this.contentView.getHeightWithKeyboard() - AndroidUtilities.dp(48.0f)) - this.chatListView.getTop(), 0.0f, (childAt.getY() + this.actionBar.getMeasuredHeight()) - this.contentView.getBackgroundTranslationY(), this.contentView.getMeasuredWidth(), this.contentView.getBackgroundSizeY(), 0, 0);
                MessageObject messageObject2 = chatMessageCell2.getMessageObject();
                if (this.roundVideoContainer != null && messageObject2.isRoundVideo() && MediaController.getInstance().isPlayingMessage(messageObject2)) {
                    ImageReceiver photoImage = chatMessageCell2.getPhotoImage();
                    this.roundVideoContainer.setTranslationX(photoImage.getImageX());
                    this.roundVideoContainer.setTranslationY(this.fragmentView.getPaddingTop() + top + photoImage.getImageY());
                    this.fragmentView.invalidate();
                    this.roundVideoContainer.invalidate();
                    z2 = true;
                }
            } else if (childAt instanceof ChatActionCell) {
                ((ChatActionCell) childAt).setVisiblePart((childAt.getY() + this.actionBar.getMeasuredHeight()) - this.contentView.getBackgroundTranslationY(), this.contentView.getBackgroundSizeY());
            }
            if (childAt.getBottom() > this.chatListView.getPaddingTop()) {
                int bottom = childAt.getBottom();
                if (bottom < i) {
                    if ((childAt instanceof ChatMessageCell) || (childAt instanceof ChatActionCell)) {
                        chatMessageCell = childAt;
                    }
                    i = bottom;
                    view2 = childAt;
                }
                ChatListItemAnimator chatListItemAnimator = this.chatListItemAnimator;
                if ((chatListItemAnimator == null || (!chatListItemAnimator.willRemoved(childAt) && !this.chatListItemAnimator.willAddedFromAlpha(childAt))) && (childAt instanceof ChatActionCell) && ((ChatActionCell) childAt).getMessageObject().isDateObject) {
                    if (childAt.getAlpha() != 1.0f) {
                        childAt.setAlpha(1.0f);
                    }
                    if (bottom < i2) {
                        i2 = bottom;
                        view = childAt;
                    }
                }
            }
        }
        FrameLayout frameLayout = this.roundVideoContainer;
        if (frameLayout != null) {
            if (!z2) {
                frameLayout.setTranslationY((-AndroidUtilities.roundMessageSize) - 100);
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
            if (bottom2 > this.floatingDateView.getMeasuredHeight() && bottom2 < this.floatingDateView.getMeasuredHeight() * 2) {
                ChatActionCell chatActionCell = this.floatingDateView;
                chatActionCell.setTranslationY(((-chatActionCell.getMeasuredHeight()) * 2) + bottom2);
                return;
            }
            this.floatingDateView.setTranslationY(0.0f);
            return;
        }
        hideFloatingDateView(true);
        this.floatingDateView.setTranslationY(0.0f);
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onTransitionAnimationStart(boolean z, boolean z2) {
        if (z) {
            this.allowAnimationIndex = getNotificationCenter().setAnimationInProgress(this.allowAnimationIndex, new int[]{NotificationCenter.chatInfoDidLoad, NotificationCenter.dialogsNeedReload, NotificationCenter.closeChats, NotificationCenter.messagesDidLoad, NotificationCenter.botKeyboardDidLoad});
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onTransitionAnimationEnd(boolean z, boolean z2) {
        if (z) {
            getNotificationCenter().onAnimationFinish(this.allowAnimationIndex);
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
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
            if (chatActivityAdapter == null) {
                return;
            }
            chatActivityAdapter.notifyDataSetChanged();
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onPause() {
        super.onPause();
        SizeNotifierFrameLayout sizeNotifierFrameLayout = this.contentView;
        if (sizeNotifierFrameLayout != null) {
            sizeNotifierFrameLayout.onPause();
        }
        UndoView undoView = this.undoView;
        if (undoView != null) {
            undoView.hide(true, 0);
        }
        this.paused = true;
        this.wasPaused = true;
        if (AvatarPreviewer.hasVisibleInstance()) {
            AvatarPreviewer.getInstance().close();
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onBecomeFullyHidden() {
        UndoView undoView = this.undoView;
        if (undoView != null) {
            undoView.hide(true, 0);
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
            showDialog(new PhonebookShareAlert(this, null, tLRPC$User, null, file, str2, str3));
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onConfigurationChanged(Configuration configuration) {
        Dialog dialog = this.visibleDialog;
        if (dialog instanceof DatePickerDialog) {
            dialog.dismiss();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void alertUserOpenError(MessageObject messageObject) {
        if (getParentActivity() == null) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
        if (messageObject.type == 3) {
            builder.setMessage(LocaleController.getString("NoPlayerInstalled", R.string.NoPlayerInstalled));
        } else {
            builder.setMessage(LocaleController.formatString("NoHandleAppInstalled", R.string.NoHandleAppInstalled, messageObject.getDocument().mime_type));
        }
        showDialog(builder.create());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void addCanBanUser(Bundle bundle, long j) {
        TLRPC$Chat tLRPC$Chat = this.currentChat;
        if (!tLRPC$Chat.megagroup || this.admins == null || !ChatObject.canBlockUsers(tLRPC$Chat)) {
            return;
        }
        int i = 0;
        while (true) {
            if (i >= this.admins.size()) {
                break;
            }
            TLRPC$ChannelParticipant tLRPC$ChannelParticipant = this.admins.get(i);
            if (MessageObject.getPeerId(tLRPC$ChannelParticipant.peer) != j) {
                i++;
            } else if (!tLRPC$ChannelParticipant.can_edit) {
                return;
            }
        }
        bundle.putLong("ban_chat_id", this.currentChat.id);
    }

    public void showOpenUrlAlert(final String str, boolean z) {
        if (Browser.isInternalUrl(str, null) || !z) {
            Browser.openUrl((Context) getParentActivity(), str, true);
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setTitle(LocaleController.getString("OpenUrlTitle", R.string.OpenUrlTitle));
        builder.setMessage(LocaleController.formatString("OpenUrlAlert2", R.string.OpenUrlAlert2, str));
        builder.setPositiveButton(LocaleController.getString("Open", R.string.Open), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.ChannelAdminLogActivity$$ExternalSyntheticLambda0
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                ChannelAdminLogActivity.this.lambda$showOpenUrlAlert$12(str, dialogInterface, i);
            }
        });
        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
        showDialog(builder.create());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$showOpenUrlAlert$12(String str, DialogInterface dialogInterface, int i) {
        Browser.openUrl((Context) getParentActivity(), str, true);
    }

    /* loaded from: classes3.dex */
    public class ChatActivityAdapter extends RecyclerView.Adapter {
        private int loadingUpRow;
        private Context mContext;
        private int messagesEndRow;
        private int messagesStartRow;
        private int rowCount;

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public long getItemId(int i) {
            return -1L;
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

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            return this.rowCount;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        /* renamed from: onCreateViewHolder */
        public RecyclerView.ViewHolder mo1821onCreateViewHolder(ViewGroup viewGroup, int i) {
            ChatActionCell chatActionCell;
            View view;
            if (i == 0) {
                if (!ChannelAdminLogActivity.this.chatMessageCellsCache.isEmpty()) {
                    ChannelAdminLogActivity.this.chatMessageCellsCache.remove(0);
                    view = (View) ChannelAdminLogActivity.this.chatMessageCellsCache.get(0);
                } else {
                    view = new ChatMessageCell(this.mContext);
                }
                ChatMessageCell chatMessageCell = (ChatMessageCell) view;
                chatMessageCell.setDelegate(new AnonymousClass1());
                chatMessageCell.setAllowAssistant(true);
                chatActionCell = view;
            } else if (i == 1) {
                ChatActionCell chatActionCell2 = new ChatActionCell(this, this.mContext) { // from class: org.telegram.ui.ChannelAdminLogActivity.ChatActivityAdapter.2
                    @Override // org.telegram.ui.Cells.ChatActionCell, android.view.View
                    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
                        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
                        accessibilityNodeInfo.setVisibleToUser(true);
                    }
                };
                chatActionCell2.setDelegate(new AnonymousClass3());
                chatActionCell = chatActionCell2;
            } else if (i == 2) {
                chatActionCell = new ChatUnreadCell(this.mContext, null);
            } else {
                chatActionCell = new ChatLoadingCell(this.mContext, ChannelAdminLogActivity.this.contentView, null);
            }
            chatActionCell.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(chatActionCell);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* renamed from: org.telegram.ui.ChannelAdminLogActivity$ChatActivityAdapter$1  reason: invalid class name */
        /* loaded from: classes3.dex */
        public class AnonymousClass1 implements ChatMessageCell.ChatMessageCellDelegate {
            @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
            public boolean canDrawOutboundsContent() {
                return true;
            }

            @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
            public boolean canPerformActions() {
                return true;
            }

            @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
            public /* synthetic */ void didLongPressBotButton(ChatMessageCell chatMessageCell, TLRPC$KeyboardButton tLRPC$KeyboardButton) {
                ChatMessageCell.ChatMessageCellDelegate.CC.$default$didLongPressBotButton(this, chatMessageCell, tLRPC$KeyboardButton);
            }

            @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
            public /* synthetic */ boolean didLongPressChannelAvatar(ChatMessageCell chatMessageCell, TLRPC$Chat tLRPC$Chat, int i, float f, float f2) {
                return ChatMessageCell.ChatMessageCellDelegate.CC.$default$didLongPressChannelAvatar(this, chatMessageCell, tLRPC$Chat, i, f, f2);
            }

            @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
            public /* synthetic */ boolean didPressAnimatedEmoji(AnimatedEmojiSpan animatedEmojiSpan) {
                return ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressAnimatedEmoji(this, animatedEmojiSpan);
            }

            @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
            public /* synthetic */ void didPressBotButton(ChatMessageCell chatMessageCell, TLRPC$KeyboardButton tLRPC$KeyboardButton) {
                ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressBotButton(this, chatMessageCell, tLRPC$KeyboardButton);
            }

            @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
            public void didPressCancelSendButton(ChatMessageCell chatMessageCell) {
            }

            @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
            public /* synthetic */ void didPressCommentButton(ChatMessageCell chatMessageCell) {
                ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressCommentButton(this, chatMessageCell);
            }

            @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
            public /* synthetic */ void didPressExtendedMediaPreview(ChatMessageCell chatMessageCell, TLRPC$KeyboardButton tLRPC$KeyboardButton) {
                ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressExtendedMediaPreview(this, chatMessageCell, tLRPC$KeyboardButton);
            }

            @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
            public /* synthetic */ void didPressHiddenForward(ChatMessageCell chatMessageCell) {
                ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressHiddenForward(this, chatMessageCell);
            }

            @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
            public /* synthetic */ void didPressHint(ChatMessageCell chatMessageCell, int i) {
                ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressHint(this, chatMessageCell, i);
            }

            @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
            public /* synthetic */ void didPressReaction(ChatMessageCell chatMessageCell, TLRPC$ReactionCount tLRPC$ReactionCount, boolean z) {
                ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressReaction(this, chatMessageCell, tLRPC$ReactionCount, z);
            }

            @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
            public void didPressReplyMessage(ChatMessageCell chatMessageCell, int i) {
            }

            @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
            public /* synthetic */ void didPressTime(ChatMessageCell chatMessageCell) {
                ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressTime(this, chatMessageCell);
            }

            @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
            public void didPressViaBot(ChatMessageCell chatMessageCell, String str) {
            }

            @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
            public /* synthetic */ void didPressViaBotNotInline(ChatMessageCell chatMessageCell, long j) {
                ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressViaBotNotInline(this, chatMessageCell, j);
            }

            @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
            public /* synthetic */ void didPressVoteButtons(ChatMessageCell chatMessageCell, ArrayList arrayList, int i, int i2, int i3) {
                ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressVoteButtons(this, chatMessageCell, arrayList, i, i2, i3);
            }

            @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
            public /* synthetic */ void didStartVideoStream(MessageObject messageObject) {
                ChatMessageCell.ChatMessageCellDelegate.CC.$default$didStartVideoStream(this, messageObject);
            }

            @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
            public /* synthetic */ String getAdminRank(long j) {
                return ChatMessageCell.ChatMessageCellDelegate.CC.$default$getAdminRank(this, j);
            }

            @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
            public /* synthetic */ PinchToZoomHelper getPinchToZoomHelper() {
                return ChatMessageCell.ChatMessageCellDelegate.CC.$default$getPinchToZoomHelper(this);
            }

            @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
            public /* synthetic */ TextSelectionHelper.ChatListTextSelectionHelper getTextSelectionHelper() {
                return ChatMessageCell.ChatMessageCellDelegate.CC.$default$getTextSelectionHelper(this);
            }

            @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
            public /* synthetic */ boolean hasSelectedMessages() {
                return ChatMessageCell.ChatMessageCellDelegate.CC.$default$hasSelectedMessages(this);
            }

            @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
            public /* synthetic */ void invalidateBlur() {
                ChatMessageCell.ChatMessageCellDelegate.CC.$default$invalidateBlur(this);
            }

            @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
            public /* synthetic */ boolean isLandscape() {
                return ChatMessageCell.ChatMessageCellDelegate.CC.$default$isLandscape(this);
            }

            @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
            public /* synthetic */ boolean keyboardIsOpened() {
                return ChatMessageCell.ChatMessageCellDelegate.CC.$default$keyboardIsOpened(this);
            }

            @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
            public /* synthetic */ void needReloadPolls() {
                ChatMessageCell.ChatMessageCellDelegate.CC.$default$needReloadPolls(this);
            }

            @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
            public /* synthetic */ void needShowPremiumBulletin(int i) {
                ChatMessageCell.ChatMessageCellDelegate.CC.$default$needShowPremiumBulletin(this, i);
            }

            @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
            public /* synthetic */ boolean onAccessibilityAction(int i, Bundle bundle) {
                return ChatMessageCell.ChatMessageCellDelegate.CC.$default$onAccessibilityAction(this, i, bundle);
            }

            @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
            public /* synthetic */ void onDiceFinished() {
                ChatMessageCell.ChatMessageCellDelegate.CC.$default$onDiceFinished(this);
            }

            @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
            public /* synthetic */ void setShouldNotRepeatSticker(MessageObject messageObject) {
                ChatMessageCell.ChatMessageCellDelegate.CC.$default$setShouldNotRepeatSticker(this, messageObject);
            }

            @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
            public /* synthetic */ boolean shouldDrawThreadProgress(ChatMessageCell chatMessageCell) {
                return ChatMessageCell.ChatMessageCellDelegate.CC.$default$shouldDrawThreadProgress(this, chatMessageCell);
            }

            @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
            public /* synthetic */ boolean shouldRepeatSticker(MessageObject messageObject) {
                return ChatMessageCell.ChatMessageCellDelegate.CC.$default$shouldRepeatSticker(this, messageObject);
            }

            @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
            public /* synthetic */ void videoTimerReached() {
                ChatMessageCell.ChatMessageCellDelegate.CC.$default$videoTimerReached(this);
            }

            AnonymousClass1() {
            }

            @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
            public void didPressSideButton(ChatMessageCell chatMessageCell) {
                if (ChannelAdminLogActivity.this.getParentActivity() == null) {
                    return;
                }
                ChatActivityAdapter chatActivityAdapter = ChatActivityAdapter.this;
                ChannelAdminLogActivity.this.showDialog(ShareAlert.createShareAlert(chatActivityAdapter.mContext, chatMessageCell.getMessageObject(), null, ChatObject.isChannel(ChannelAdminLogActivity.this.currentChat) && !ChannelAdminLogActivity.this.currentChat.megagroup, null, false));
            }

            @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
            public boolean needPlayMessage(MessageObject messageObject, boolean z) {
                if (messageObject.isVoice() || messageObject.isRoundVideo()) {
                    boolean playMessage = MediaController.getInstance().playMessage(messageObject, z);
                    MediaController.getInstance().setVoiceMessagesPlaylist(null, false);
                    return playMessage;
                } else if (!messageObject.isMusic()) {
                    return false;
                } else {
                    return MediaController.getInstance().setPlaylist(ChannelAdminLogActivity.this.messages, messageObject, 0L);
                }
            }

            @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
            public void didPressChannelAvatar(ChatMessageCell chatMessageCell, TLRPC$Chat tLRPC$Chat, int i, float f, float f2) {
                if (tLRPC$Chat == null || tLRPC$Chat == ChannelAdminLogActivity.this.currentChat) {
                    return;
                }
                Bundle bundle = new Bundle();
                bundle.putLong("chat_id", tLRPC$Chat.id);
                if (i != 0) {
                    bundle.putInt("message_id", i);
                }
                if (!MessagesController.getInstance(((BaseFragment) ChannelAdminLogActivity.this).currentAccount).checkCanOpenChat(bundle, ChannelAdminLogActivity.this)) {
                    return;
                }
                ChannelAdminLogActivity.this.presentFragment(new ChatActivity(bundle), true);
            }

            @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
            public void didPressOther(ChatMessageCell chatMessageCell, float f, float f2) {
                ChannelAdminLogActivity.this.createMenu(chatMessageCell);
            }

            @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
            public void didPressUserAvatar(ChatMessageCell chatMessageCell, TLRPC$User tLRPC$User, float f, float f2) {
                if (tLRPC$User == null || tLRPC$User.id == UserConfig.getInstance(((BaseFragment) ChannelAdminLogActivity.this).currentAccount).getClientUserId()) {
                    return;
                }
                openProfile(tLRPC$User);
            }

            @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
            public boolean didLongPressUserAvatar(final ChatMessageCell chatMessageCell, final TLRPC$User tLRPC$User, float f, float f2) {
                AvatarPreviewer.Data of;
                if (tLRPC$User != null && tLRPC$User.id != UserConfig.getInstance(((BaseFragment) ChannelAdminLogActivity.this).currentAccount).getClientUserId()) {
                    AvatarPreviewer.MenuItem[] menuItemArr = {AvatarPreviewer.MenuItem.OPEN_PROFILE, AvatarPreviewer.MenuItem.SEND_MESSAGE};
                    TLRPC$UserFull userFull = ChannelAdminLogActivity.this.getMessagesController().getUserFull(tLRPC$User.id);
                    if (userFull == null) {
                        of = AvatarPreviewer.Data.of(tLRPC$User, ((BaseFragment) ChannelAdminLogActivity.this).classGuid, menuItemArr);
                    } else {
                        of = AvatarPreviewer.Data.of(userFull, menuItemArr);
                    }
                    if (AvatarPreviewer.canPreview(of)) {
                        AvatarPreviewer.getInstance().show((ViewGroup) ((BaseFragment) ChannelAdminLogActivity.this).fragmentView, of, new AvatarPreviewer.Callback() { // from class: org.telegram.ui.ChannelAdminLogActivity$ChatActivityAdapter$1$$ExternalSyntheticLambda1
                            @Override // org.telegram.ui.AvatarPreviewer.Callback
                            public final void onMenuClick(AvatarPreviewer.MenuItem menuItem) {
                                ChannelAdminLogActivity.ChatActivityAdapter.AnonymousClass1.this.lambda$didLongPressUserAvatar$0(chatMessageCell, tLRPC$User, menuItem);
                            }
                        });
                        return true;
                    }
                }
                return false;
            }

            /* JADX INFO: Access modifiers changed from: private */
            public /* synthetic */ void lambda$didLongPressUserAvatar$0(ChatMessageCell chatMessageCell, TLRPC$User tLRPC$User, AvatarPreviewer.MenuItem menuItem) {
                int i = AnonymousClass17.$SwitchMap$org$telegram$ui$AvatarPreviewer$MenuItem[menuItem.ordinal()];
                if (i == 1) {
                    openDialog(chatMessageCell, tLRPC$User);
                } else if (i != 2) {
                } else {
                    openProfile(tLRPC$User);
                }
            }

            private void openProfile(TLRPC$User tLRPC$User) {
                Bundle bundle = new Bundle();
                bundle.putLong("user_id", tLRPC$User.id);
                ChannelAdminLogActivity.this.addCanBanUser(bundle, tLRPC$User.id);
                ProfileActivity profileActivity = new ProfileActivity(bundle);
                profileActivity.setPlayProfileAnimation(0);
                ChannelAdminLogActivity.this.presentFragment(profileActivity);
            }

            private void openDialog(ChatMessageCell chatMessageCell, TLRPC$User tLRPC$User) {
                if (tLRPC$User != null) {
                    Bundle bundle = new Bundle();
                    bundle.putLong("user_id", tLRPC$User.id);
                    if (!ChannelAdminLogActivity.this.getMessagesController().checkCanOpenChat(bundle, ChannelAdminLogActivity.this)) {
                        return;
                    }
                    ChannelAdminLogActivity.this.presentFragment(new ChatActivity(bundle));
                }
            }

            @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
            public void didLongPress(ChatMessageCell chatMessageCell, float f, float f2) {
                ChannelAdminLogActivity.this.createMenu(chatMessageCell);
            }

            @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
            public void didPressUrl(ChatMessageCell chatMessageCell, CharacterStyle characterStyle, boolean z) {
                TLRPC$WebPage tLRPC$WebPage;
                if (characterStyle == null) {
                    return;
                }
                MessageObject messageObject = chatMessageCell.getMessageObject();
                if (characterStyle instanceof URLSpanMono) {
                    ((URLSpanMono) characterStyle).copyToClipboard();
                    if (!AndroidUtilities.shouldShowClipboardToast()) {
                        return;
                    }
                    Toast.makeText(ChannelAdminLogActivity.this.getParentActivity(), LocaleController.getString("TextCopied", R.string.TextCopied), 0).show();
                } else if (characterStyle instanceof URLSpanUserMention) {
                    long longValue = Utilities.parseLong(((URLSpanUserMention) characterStyle).getURL()).longValue();
                    if (longValue > 0) {
                        TLRPC$User user = MessagesController.getInstance(((BaseFragment) ChannelAdminLogActivity.this).currentAccount).getUser(Long.valueOf(longValue));
                        if (user == null) {
                            return;
                        }
                        MessagesController.openChatOrProfileWith(user, null, ChannelAdminLogActivity.this, 0, false);
                        return;
                    }
                    TLRPC$Chat chat = MessagesController.getInstance(((BaseFragment) ChannelAdminLogActivity.this).currentAccount).getChat(Long.valueOf(-longValue));
                    if (chat == null) {
                        return;
                    }
                    MessagesController.openChatOrProfileWith(null, chat, ChannelAdminLogActivity.this, 0, false);
                } else if (characterStyle instanceof URLSpanNoUnderline) {
                    String url = ((URLSpanNoUnderline) characterStyle).getURL();
                    if (url.startsWith("@")) {
                        MessagesController.getInstance(((BaseFragment) ChannelAdminLogActivity.this).currentAccount).openByUserName(url.substring(1), ChannelAdminLogActivity.this, 0);
                    } else if (!url.startsWith("#")) {
                    } else {
                        DialogsActivity dialogsActivity = new DialogsActivity(null);
                        dialogsActivity.setSearchString(url);
                        ChannelAdminLogActivity.this.presentFragment(dialogsActivity);
                    }
                } else {
                    final String url2 = ((URLSpan) characterStyle).getURL();
                    if (z) {
                        BottomSheet.Builder builder = new BottomSheet.Builder(ChannelAdminLogActivity.this.getParentActivity());
                        builder.setTitle(url2);
                        builder.setItems(new CharSequence[]{LocaleController.getString("Open", R.string.Open), LocaleController.getString("Copy", R.string.Copy)}, new DialogInterface.OnClickListener() { // from class: org.telegram.ui.ChannelAdminLogActivity$ChatActivityAdapter$1$$ExternalSyntheticLambda0
                            @Override // android.content.DialogInterface.OnClickListener
                            public final void onClick(DialogInterface dialogInterface, int i) {
                                ChannelAdminLogActivity.ChatActivityAdapter.AnonymousClass1.this.lambda$didPressUrl$1(url2, dialogInterface, i);
                            }
                        });
                        ChannelAdminLogActivity.this.showDialog(builder.create());
                    } else if (characterStyle instanceof URLSpanReplacement) {
                        ChannelAdminLogActivity.this.showOpenUrlAlert(((URLSpanReplacement) characterStyle).getURL(), true);
                    } else {
                        TLRPC$MessageMedia tLRPC$MessageMedia = messageObject.messageOwner.media;
                        if ((tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaWebPage) && (tLRPC$WebPage = tLRPC$MessageMedia.webpage) != null && tLRPC$WebPage.cached_page != null) {
                            String lowerCase = url2.toLowerCase();
                            String lowerCase2 = messageObject.messageOwner.media.webpage.url.toLowerCase();
                            if ((Browser.isTelegraphUrl(lowerCase, false) || lowerCase.contains("t.me/iv")) && (lowerCase.contains(lowerCase2) || lowerCase2.contains(lowerCase))) {
                                ArticleViewer.getInstance().setParentActivity(ChannelAdminLogActivity.this.getParentActivity(), ChannelAdminLogActivity.this);
                                ArticleViewer.getInstance().open(messageObject);
                                return;
                            }
                        }
                        Browser.openUrl((Context) ChannelAdminLogActivity.this.getParentActivity(), url2, true);
                    }
                }
            }

            /* JADX INFO: Access modifiers changed from: private */
            public /* synthetic */ void lambda$didPressUrl$1(String str, DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    Browser.openUrl((Context) ChannelAdminLogActivity.this.getParentActivity(), str, true);
                } else if (i != 1) {
                } else {
                    if (str.startsWith("mailto:")) {
                        str = str.substring(7);
                    } else if (str.startsWith("tel:")) {
                        str = str.substring(4);
                    }
                    AndroidUtilities.addToClipboard(str);
                }
            }

            @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
            public void needOpenWebView(MessageObject messageObject, String str, String str2, String str3, String str4, int i, int i2) {
                ChannelAdminLogActivity channelAdminLogActivity = ChannelAdminLogActivity.this;
                EmbedBottomSheet.show(channelAdminLogActivity, messageObject, channelAdminLogActivity.provider, str2, str3, str4, str, i, i2, false);
            }

            /* JADX WARN: Code restructure failed: missing block: B:50:0x011c, code lost:
                if (r10.exists() != false) goto L36;
             */
            @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
            /*
                Code decompiled incorrectly, please refer to instructions dump.
                To view partially-correct add '--show-bad-code' argument
            */
            public void didPressImage(org.telegram.ui.Cells.ChatMessageCell r10, float r11, float r12) {
                /*
                    Method dump skipped, instructions count: 500
                    To view this dump add '--comments-level debug' option
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChannelAdminLogActivity.ChatActivityAdapter.AnonymousClass1.didPressImage(org.telegram.ui.Cells.ChatMessageCell, float, float):void");
            }

            @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
            public void didPressInstantButton(ChatMessageCell chatMessageCell, int i) {
                TLRPC$WebPage tLRPC$WebPage;
                MessageObject messageObject = chatMessageCell.getMessageObject();
                if (i == 0) {
                    TLRPC$MessageMedia tLRPC$MessageMedia = messageObject.messageOwner.media;
                    if (tLRPC$MessageMedia == null || (tLRPC$WebPage = tLRPC$MessageMedia.webpage) == null || tLRPC$WebPage.cached_page == null) {
                        return;
                    }
                    ArticleViewer.getInstance().setParentActivity(ChannelAdminLogActivity.this.getParentActivity(), ChannelAdminLogActivity.this);
                    ArticleViewer.getInstance().open(messageObject);
                } else if (i == 5) {
                    ChannelAdminLogActivity channelAdminLogActivity = ChannelAdminLogActivity.this;
                    TLRPC$User user = channelAdminLogActivity.getMessagesController().getUser(Long.valueOf(messageObject.messageOwner.media.user_id));
                    TLRPC$MessageMedia tLRPC$MessageMedia2 = messageObject.messageOwner.media;
                    channelAdminLogActivity.openVCard(user, tLRPC$MessageMedia2.vcard, tLRPC$MessageMedia2.first_name, tLRPC$MessageMedia2.last_name);
                } else {
                    TLRPC$MessageMedia tLRPC$MessageMedia3 = messageObject.messageOwner.media;
                    if (tLRPC$MessageMedia3 == null || tLRPC$MessageMedia3.webpage == null) {
                        return;
                    }
                    Browser.openUrl(ChannelAdminLogActivity.this.getParentActivity(), messageObject.messageOwner.media.webpage.url);
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* renamed from: org.telegram.ui.ChannelAdminLogActivity$ChatActivityAdapter$3  reason: invalid class name */
        /* loaded from: classes3.dex */
        public class AnonymousClass3 implements ChatActionCell.ChatActionCellDelegate {
            @Override // org.telegram.ui.Cells.ChatActionCell.ChatActionCellDelegate
            public /* synthetic */ boolean canDrawOutboundsContent() {
                return ChatActionCell.ChatActionCellDelegate.CC.$default$canDrawOutboundsContent(this);
            }

            @Override // org.telegram.ui.Cells.ChatActionCell.ChatActionCellDelegate
            public /* synthetic */ void didOpenPremiumGift(ChatActionCell chatActionCell, TLRPC$TL_premiumGiftOption tLRPC$TL_premiumGiftOption, boolean z) {
                ChatActionCell.ChatActionCellDelegate.CC.$default$didOpenPremiumGift(this, chatActionCell, tLRPC$TL_premiumGiftOption, z);
            }

            @Override // org.telegram.ui.Cells.ChatActionCell.ChatActionCellDelegate
            public void didPressReplyMessage(ChatActionCell chatActionCell, int i) {
            }

            @Override // org.telegram.ui.Cells.ChatActionCell.ChatActionCellDelegate
            public /* synthetic */ int getTopicId() {
                return ChatActionCell.ChatActionCellDelegate.CC.$default$getTopicId(this);
            }

            @Override // org.telegram.ui.Cells.ChatActionCell.ChatActionCellDelegate
            public /* synthetic */ void needShowEffectOverlay(ChatActionCell chatActionCell, TLRPC$Document tLRPC$Document, TLRPC$VideoSize tLRPC$VideoSize) {
                ChatActionCell.ChatActionCellDelegate.CC.$default$needShowEffectOverlay(this, chatActionCell, tLRPC$Document, tLRPC$VideoSize);
            }

            AnonymousClass3() {
            }

            @Override // org.telegram.ui.Cells.ChatActionCell.ChatActionCellDelegate
            public void didClickImage(ChatActionCell chatActionCell) {
                MessageObject messageObject = chatActionCell.getMessageObject();
                PhotoViewer.getInstance().setParentActivity(ChannelAdminLogActivity.this);
                TLRPC$PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(messageObject.photoThumbs, 640);
                if (closestPhotoSizeWithSize == null) {
                    PhotoViewer.getInstance().openPhoto(messageObject, (ChatActivity) null, 0L, 0L, 0, ChannelAdminLogActivity.this.provider);
                    return;
                }
                PhotoViewer.getInstance().openPhoto(closestPhotoSizeWithSize.location, ImageLocation.getForPhoto(closestPhotoSizeWithSize, messageObject.messageOwner.action.photo), ChannelAdminLogActivity.this.provider);
            }

            @Override // org.telegram.ui.Cells.ChatActionCell.ChatActionCellDelegate
            public boolean didLongPress(ChatActionCell chatActionCell, float f, float f2) {
                return ChannelAdminLogActivity.this.createMenu(chatActionCell);
            }

            @Override // org.telegram.ui.Cells.ChatActionCell.ChatActionCellDelegate
            public void needOpenUserProfile(long j) {
                if (j >= 0) {
                    if (j == UserConfig.getInstance(((BaseFragment) ChannelAdminLogActivity.this).currentAccount).getClientUserId()) {
                        return;
                    }
                    Bundle bundle = new Bundle();
                    bundle.putLong("user_id", j);
                    ChannelAdminLogActivity.this.addCanBanUser(bundle, j);
                    ProfileActivity profileActivity = new ProfileActivity(bundle);
                    profileActivity.setPlayProfileAnimation(0);
                    ChannelAdminLogActivity.this.presentFragment(profileActivity);
                    return;
                }
                Bundle bundle2 = new Bundle();
                bundle2.putLong("chat_id", -j);
                if (!MessagesController.getInstance(((BaseFragment) ChannelAdminLogActivity.this).currentAccount).checkCanOpenChat(bundle2, ChannelAdminLogActivity.this)) {
                    return;
                }
                ChannelAdminLogActivity.this.presentFragment(new ChatActivity(bundle2), true);
            }

            @Override // org.telegram.ui.Cells.ChatActionCell.ChatActionCellDelegate
            public void needOpenInviteLink(final TLRPC$TL_chatInviteExported tLRPC$TL_chatInviteExported) {
                if (ChannelAdminLogActivity.this.linviteLoading) {
                    return;
                }
                Object obj = ChannelAdminLogActivity.this.invitesCache.containsKey(tLRPC$TL_chatInviteExported.link) ? ChannelAdminLogActivity.this.invitesCache.get(tLRPC$TL_chatInviteExported.link) : null;
                if (obj == null) {
                    TLRPC$TL_messages_getExportedChatInvite tLRPC$TL_messages_getExportedChatInvite = new TLRPC$TL_messages_getExportedChatInvite();
                    tLRPC$TL_messages_getExportedChatInvite.peer = ChannelAdminLogActivity.this.getMessagesController().getInputPeer(-ChannelAdminLogActivity.this.currentChat.id);
                    tLRPC$TL_messages_getExportedChatInvite.link = tLRPC$TL_chatInviteExported.link;
                    ChannelAdminLogActivity.this.linviteLoading = true;
                    final boolean[] zArr = new boolean[1];
                    final AlertDialog alertDialog = new AlertDialog(ChannelAdminLogActivity.this.getParentActivity(), 3);
                    alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() { // from class: org.telegram.ui.ChannelAdminLogActivity$ChatActivityAdapter$3$$ExternalSyntheticLambda0
                        @Override // android.content.DialogInterface.OnCancelListener
                        public final void onCancel(DialogInterface dialogInterface) {
                            ChannelAdminLogActivity.ChatActivityAdapter.AnonymousClass3.this.lambda$needOpenInviteLink$0(zArr, dialogInterface);
                        }
                    });
                    alertDialog.showDelayed(300L);
                    ChannelAdminLogActivity.this.getConnectionsManager().bindRequestToGuid(ChannelAdminLogActivity.this.getConnectionsManager().sendRequest(tLRPC$TL_messages_getExportedChatInvite, new RequestDelegate() { // from class: org.telegram.ui.ChannelAdminLogActivity$ChatActivityAdapter$3$$ExternalSyntheticLambda2
                        @Override // org.telegram.tgnet.RequestDelegate
                        public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                            ChannelAdminLogActivity.ChatActivityAdapter.AnonymousClass3.this.lambda$needOpenInviteLink$2(tLRPC$TL_chatInviteExported, zArr, alertDialog, tLObject, tLRPC$TL_error);
                        }
                    }), ((BaseFragment) ChannelAdminLogActivity.this).classGuid);
                } else if (obj instanceof TLRPC$TL_messages_exportedChatInvite) {
                    ChannelAdminLogActivity channelAdminLogActivity = ChannelAdminLogActivity.this;
                    channelAdminLogActivity.showInviteLinkBottomSheet((TLRPC$TL_messages_exportedChatInvite) obj, channelAdminLogActivity.usersMap);
                } else {
                    BulletinFactory.of(ChannelAdminLogActivity.this).createSimpleBulletin(R.raw.linkbroken, LocaleController.getString("LinkHashExpired", R.string.LinkHashExpired)).show();
                }
            }

            /* JADX INFO: Access modifiers changed from: private */
            public /* synthetic */ void lambda$needOpenInviteLink$0(boolean[] zArr, DialogInterface dialogInterface) {
                ChannelAdminLogActivity.this.linviteLoading = false;
                zArr[0] = true;
            }

            /* JADX INFO: Access modifiers changed from: private */
            public /* synthetic */ void lambda$needOpenInviteLink$2(final TLRPC$TL_chatInviteExported tLRPC$TL_chatInviteExported, final boolean[] zArr, final AlertDialog alertDialog, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                TLRPC$TL_messages_exportedChatInvite tLRPC$TL_messages_exportedChatInvite;
                if (tLRPC$TL_error == null) {
                    tLRPC$TL_messages_exportedChatInvite = (TLRPC$TL_messages_exportedChatInvite) tLObject;
                    for (int i = 0; i < tLRPC$TL_messages_exportedChatInvite.users.size(); i++) {
                        TLRPC$User tLRPC$User = tLRPC$TL_messages_exportedChatInvite.users.get(i);
                        if (ChannelAdminLogActivity.this.usersMap == null) {
                            ChannelAdminLogActivity.this.usersMap = new HashMap();
                        }
                        ChannelAdminLogActivity.this.usersMap.put(Long.valueOf(tLRPC$User.id), tLRPC$User);
                    }
                } else {
                    tLRPC$TL_messages_exportedChatInvite = null;
                }
                final TLRPC$TL_messages_exportedChatInvite tLRPC$TL_messages_exportedChatInvite2 = tLRPC$TL_messages_exportedChatInvite;
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ChannelAdminLogActivity$ChatActivityAdapter$3$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        ChannelAdminLogActivity.ChatActivityAdapter.AnonymousClass3.this.lambda$needOpenInviteLink$1(tLRPC$TL_chatInviteExported, tLRPC$TL_messages_exportedChatInvite2, zArr, alertDialog);
                    }
                });
            }

            /* JADX INFO: Access modifiers changed from: private */
            public /* synthetic */ void lambda$needOpenInviteLink$1(TLRPC$TL_chatInviteExported tLRPC$TL_chatInviteExported, TLRPC$TL_messages_exportedChatInvite tLRPC$TL_messages_exportedChatInvite, boolean[] zArr, AlertDialog alertDialog) {
                ChannelAdminLogActivity.this.linviteLoading = false;
                ChannelAdminLogActivity.this.invitesCache.put(tLRPC$TL_chatInviteExported.link, tLRPC$TL_messages_exportedChatInvite == null ? 0 : tLRPC$TL_messages_exportedChatInvite);
                if (zArr[0]) {
                    return;
                }
                alertDialog.dismiss();
                if (tLRPC$TL_messages_exportedChatInvite != null) {
                    ChannelAdminLogActivity channelAdminLogActivity = ChannelAdminLogActivity.this;
                    channelAdminLogActivity.showInviteLinkBottomSheet(tLRPC$TL_messages_exportedChatInvite, channelAdminLogActivity.usersMap);
                    return;
                }
                BulletinFactory.of(ChannelAdminLogActivity.this).createSimpleBulletin(R.raw.linkbroken, LocaleController.getString("LinkHashExpired", R.string.LinkHashExpired)).show();
            }

            @Override // org.telegram.ui.Cells.ChatActionCell.ChatActionCellDelegate
            public BaseFragment getBaseFragment() {
                return ChannelAdminLogActivity.this;
            }

            @Override // org.telegram.ui.Cells.ChatActionCell.ChatActionCellDelegate
            public long getDialogId() {
                return -ChannelAdminLogActivity.this.currentChat.id;
            }
        }

        /* JADX WARN: Code restructure failed: missing block: B:35:0x00d8, code lost:
            if (java.lang.Math.abs(r14.messageOwner.date - r0.messageOwner.date) <= 300) goto L36;
         */
        /* JADX WARN: Removed duplicated region for block: B:28:0x009b  */
        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder r14, int r15) {
            /*
                Method dump skipped, instructions count: 246
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChannelAdminLogActivity.ChatActivityAdapter.onBindViewHolder(androidx.recyclerview.widget.RecyclerView$ViewHolder, int):void");
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int i) {
            if (i < this.messagesStartRow || i >= this.messagesEndRow) {
                return 4;
            }
            ArrayList<MessageObject> arrayList = ChannelAdminLogActivity.this.messages;
            return arrayList.get((arrayList.size() - (i - this.messagesStartRow)) - 1).contentType;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onViewAttachedToWindow(final RecyclerView.ViewHolder viewHolder) {
            final View view = viewHolder.itemView;
            if ((view instanceof ChatMessageCell) || (view instanceof ChatActionCell)) {
                view.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() { // from class: org.telegram.ui.ChannelAdminLogActivity.ChatActivityAdapter.4
                    @Override // android.view.ViewTreeObserver.OnPreDrawListener
                    public boolean onPreDraw() {
                        view.getViewTreeObserver().removeOnPreDrawListener(this);
                        int measuredHeight = ChannelAdminLogActivity.this.chatListView.getMeasuredHeight();
                        int top = view.getTop();
                        view.getBottom();
                        int i = top >= 0 ? 0 : -top;
                        int measuredHeight2 = view.getMeasuredHeight();
                        if (measuredHeight2 > measuredHeight) {
                            measuredHeight2 = i + measuredHeight;
                        }
                        View view2 = viewHolder.itemView;
                        if (view2 instanceof ChatMessageCell) {
                            ((ChatMessageCell) view).setVisiblePart(i, measuredHeight2 - i, (ChannelAdminLogActivity.this.contentView.getHeightWithKeyboard() - AndroidUtilities.dp(48.0f)) - ChannelAdminLogActivity.this.chatListView.getTop(), 0.0f, (view.getY() + ((BaseFragment) ChannelAdminLogActivity.this).actionBar.getMeasuredHeight()) - ChannelAdminLogActivity.this.contentView.getBackgroundTranslationY(), ChannelAdminLogActivity.this.contentView.getMeasuredWidth(), ChannelAdminLogActivity.this.contentView.getBackgroundSizeY(), 0, 0);
                            return true;
                        } else if (!(view2 instanceof ChatActionCell) || ((BaseFragment) ChannelAdminLogActivity.this).actionBar == null || ChannelAdminLogActivity.this.contentView == null) {
                            return true;
                        } else {
                            View view3 = view;
                            ((ChatActionCell) view3).setVisiblePart((view3.getY() + ((BaseFragment) ChannelAdminLogActivity.this).actionBar.getMeasuredHeight()) - ChannelAdminLogActivity.this.contentView.getBackgroundTranslationY(), ChannelAdminLogActivity.this.contentView.getBackgroundSizeY());
                            return true;
                        }
                    }
                });
            }
            View view2 = viewHolder.itemView;
            if (view2 instanceof ChatMessageCell) {
                ChatMessageCell chatMessageCell = (ChatMessageCell) view2;
                chatMessageCell.getMessageObject();
                chatMessageCell.setBackgroundDrawable(null);
                chatMessageCell.setCheckPressed(true, false);
                chatMessageCell.setHighlighted(false);
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void notifyDataSetChanged() {
            updateRows();
            try {
                super.notifyDataSetChanged();
            } catch (Exception e) {
                FileLog.e(e);
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void notifyItemChanged(int i) {
            updateRows();
            try {
                super.notifyItemChanged(i);
            } catch (Exception e) {
                FileLog.e(e);
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void notifyItemRangeChanged(int i, int i2) {
            updateRows();
            try {
                super.notifyItemRangeChanged(i, i2);
            } catch (Exception e) {
                FileLog.e(e);
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void notifyItemMoved(int i, int i2) {
            updateRows();
            try {
                super.notifyItemMoved(i, i2);
            } catch (Exception e) {
                FileLog.e(e);
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void notifyItemRangeInserted(int i, int i2) {
            updateRows();
            try {
                super.notifyItemRangeInserted(i, i2);
            } catch (Exception e) {
                FileLog.e(e);
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void notifyItemRemoved(int i) {
            updateRows();
            try {
                super.notifyItemRemoved(i);
            } catch (Exception e) {
                FileLog.e(e);
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void notifyItemRangeRemoved(int i, int i2) {
            updateRows();
            try {
                super.notifyItemRangeRemoved(i, i2);
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: org.telegram.ui.ChannelAdminLogActivity$17  reason: invalid class name */
    /* loaded from: classes3.dex */
    public static /* synthetic */ class AnonymousClass17 {
        static final /* synthetic */ int[] $SwitchMap$org$telegram$ui$AvatarPreviewer$MenuItem;

        static {
            int[] iArr = new int[AvatarPreviewer.MenuItem.values().length];
            $SwitchMap$org$telegram$ui$AvatarPreviewer$MenuItem = iArr;
            try {
                iArr[AvatarPreviewer.MenuItem.SEND_MESSAGE.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$org$telegram$ui$AvatarPreviewer$MenuItem[AvatarPreviewer.MenuItem.OPEN_PROFILE.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showInviteLinkBottomSheet(TLRPC$TL_messages_exportedChatInvite tLRPC$TL_messages_exportedChatInvite, HashMap<Long, TLRPC$User> hashMap) {
        TLRPC$ChatFull chatFull = getMessagesController().getChatFull(this.currentChat.id);
        InviteLinkBottomSheet inviteLinkBottomSheet = new InviteLinkBottomSheet(this.contentView.getContext(), (TLRPC$TL_chatInviteExported) tLRPC$TL_messages_exportedChatInvite.invite, chatFull, hashMap, this, chatFull.id, false, ChatObject.isChannel(this.currentChat));
        inviteLinkBottomSheet.setInviteDelegate(new InviteLinkBottomSheet.InviteDelegate() { // from class: org.telegram.ui.ChannelAdminLogActivity.16
            @Override // org.telegram.ui.Components.InviteLinkBottomSheet.InviteDelegate
            public void permanentLinkReplaced(TLRPC$TL_chatInviteExported tLRPC$TL_chatInviteExported, TLRPC$TL_chatInviteExported tLRPC$TL_chatInviteExported2) {
            }

            @Override // org.telegram.ui.Components.InviteLinkBottomSheet.InviteDelegate
            public void linkRevoked(TLRPC$TL_chatInviteExported tLRPC$TL_chatInviteExported) {
                TLRPC$TL_channelAdminLogEvent tLRPC$TL_channelAdminLogEvent = new TLRPC$TL_channelAdminLogEvent();
                int size = ChannelAdminLogActivity.this.messages.size();
                tLRPC$TL_chatInviteExported.revoked = true;
                TLRPC$TL_channelAdminLogEventActionExportedInviteRevoke tLRPC$TL_channelAdminLogEventActionExportedInviteRevoke = new TLRPC$TL_channelAdminLogEventActionExportedInviteRevoke();
                tLRPC$TL_channelAdminLogEventActionExportedInviteRevoke.invite = tLRPC$TL_chatInviteExported;
                tLRPC$TL_channelAdminLogEvent.action = tLRPC$TL_channelAdminLogEventActionExportedInviteRevoke;
                tLRPC$TL_channelAdminLogEvent.date = (int) (System.currentTimeMillis() / 1000);
                tLRPC$TL_channelAdminLogEvent.user_id = ChannelAdminLogActivity.this.getAccountInstance().getUserConfig().clientUserId;
                int i = ((BaseFragment) ChannelAdminLogActivity.this).currentAccount;
                ChannelAdminLogActivity channelAdminLogActivity = ChannelAdminLogActivity.this;
                ArrayList<MessageObject> arrayList = channelAdminLogActivity.messages;
                HashMap hashMap2 = channelAdminLogActivity.messagesByDays;
                ChannelAdminLogActivity channelAdminLogActivity2 = ChannelAdminLogActivity.this;
                if (new MessageObject(i, tLRPC$TL_channelAdminLogEvent, arrayList, (HashMap<String, ArrayList<MessageObject>>) hashMap2, channelAdminLogActivity2.currentChat, channelAdminLogActivity2.mid, true).contentType < 0) {
                    return;
                }
                int size2 = ChannelAdminLogActivity.this.messages.size() - size;
                if (size2 > 0) {
                    ChannelAdminLogActivity.this.chatListItemAnimator.setShouldAnimateEnterFromBottom(true);
                    ChannelAdminLogActivity.this.chatAdapter.notifyItemRangeInserted(ChannelAdminLogActivity.this.chatAdapter.messagesEndRow, size2);
                    ChannelAdminLogActivity.this.moveScrollToLastMessage();
                }
                ChannelAdminLogActivity.this.invitesCache.remove(tLRPC$TL_chatInviteExported.link);
            }

            @Override // org.telegram.ui.Components.InviteLinkBottomSheet.InviteDelegate
            public void onLinkDeleted(TLRPC$TL_chatInviteExported tLRPC$TL_chatInviteExported) {
                int size = ChannelAdminLogActivity.this.messages.size();
                int unused = ChannelAdminLogActivity.this.chatAdapter.messagesEndRow;
                TLRPC$TL_channelAdminLogEvent tLRPC$TL_channelAdminLogEvent = new TLRPC$TL_channelAdminLogEvent();
                TLRPC$TL_channelAdminLogEventActionExportedInviteDelete tLRPC$TL_channelAdminLogEventActionExportedInviteDelete = new TLRPC$TL_channelAdminLogEventActionExportedInviteDelete();
                tLRPC$TL_channelAdminLogEventActionExportedInviteDelete.invite = tLRPC$TL_chatInviteExported;
                tLRPC$TL_channelAdminLogEvent.action = tLRPC$TL_channelAdminLogEventActionExportedInviteDelete;
                tLRPC$TL_channelAdminLogEvent.date = (int) (System.currentTimeMillis() / 1000);
                tLRPC$TL_channelAdminLogEvent.user_id = ChannelAdminLogActivity.this.getAccountInstance().getUserConfig().clientUserId;
                int i = ((BaseFragment) ChannelAdminLogActivity.this).currentAccount;
                ChannelAdminLogActivity channelAdminLogActivity = ChannelAdminLogActivity.this;
                ArrayList<MessageObject> arrayList = channelAdminLogActivity.messages;
                HashMap hashMap2 = channelAdminLogActivity.messagesByDays;
                ChannelAdminLogActivity channelAdminLogActivity2 = ChannelAdminLogActivity.this;
                if (new MessageObject(i, tLRPC$TL_channelAdminLogEvent, arrayList, (HashMap<String, ArrayList<MessageObject>>) hashMap2, channelAdminLogActivity2.currentChat, channelAdminLogActivity2.mid, true).contentType < 0) {
                    return;
                }
                int size2 = ChannelAdminLogActivity.this.messages.size() - size;
                if (size2 > 0) {
                    ChannelAdminLogActivity.this.chatListItemAnimator.setShouldAnimateEnterFromBottom(true);
                    ChannelAdminLogActivity.this.chatAdapter.notifyItemRangeInserted(ChannelAdminLogActivity.this.chatAdapter.messagesEndRow, size2);
                    ChannelAdminLogActivity.this.moveScrollToLastMessage();
                }
                ChannelAdminLogActivity.this.invitesCache.remove(tLRPC$TL_chatInviteExported.link);
            }

            @Override // org.telegram.ui.Components.InviteLinkBottomSheet.InviteDelegate
            public void onLinkEdited(TLRPC$TL_chatInviteExported tLRPC$TL_chatInviteExported) {
                TLRPC$TL_channelAdminLogEvent tLRPC$TL_channelAdminLogEvent = new TLRPC$TL_channelAdminLogEvent();
                TLRPC$TL_channelAdminLogEventActionExportedInviteEdit tLRPC$TL_channelAdminLogEventActionExportedInviteEdit = new TLRPC$TL_channelAdminLogEventActionExportedInviteEdit();
                tLRPC$TL_channelAdminLogEventActionExportedInviteEdit.new_invite = tLRPC$TL_chatInviteExported;
                tLRPC$TL_channelAdminLogEventActionExportedInviteEdit.prev_invite = tLRPC$TL_chatInviteExported;
                tLRPC$TL_channelAdminLogEvent.action = tLRPC$TL_channelAdminLogEventActionExportedInviteEdit;
                tLRPC$TL_channelAdminLogEvent.date = (int) (System.currentTimeMillis() / 1000);
                tLRPC$TL_channelAdminLogEvent.user_id = ChannelAdminLogActivity.this.getAccountInstance().getUserConfig().clientUserId;
                int i = ((BaseFragment) ChannelAdminLogActivity.this).currentAccount;
                ChannelAdminLogActivity channelAdminLogActivity = ChannelAdminLogActivity.this;
                ArrayList<MessageObject> arrayList = channelAdminLogActivity.messages;
                HashMap hashMap2 = channelAdminLogActivity.messagesByDays;
                ChannelAdminLogActivity channelAdminLogActivity2 = ChannelAdminLogActivity.this;
                if (new MessageObject(i, tLRPC$TL_channelAdminLogEvent, arrayList, (HashMap<String, ArrayList<MessageObject>>) hashMap2, channelAdminLogActivity2.currentChat, channelAdminLogActivity2.mid, true).contentType < 0) {
                    return;
                }
                ChannelAdminLogActivity.this.chatAdapter.notifyDataSetChanged();
                ChannelAdminLogActivity.this.moveScrollToLastMessage();
            }
        });
        inviteLinkBottomSheet.show();
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        arrayList.add(new ThemeDescription(this.fragmentView, 0, null, null, null, null, "chat_wallpaper"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.chatListView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUBACKGROUND, null, null, null, null, "actionBarDefaultSubmenuBackground"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM, null, null, null, null, "actionBarDefaultSubmenuItem"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM | ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, "actionBarDefaultSubmenuItemIcon"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.chatListView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon"));
        arrayList.add(new ThemeDescription(this.avatarContainer.getTitleTextView(), ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "actionBarDefaultTitle"));
        arrayList.add(new ThemeDescription(this.avatarContainer.getSubtitleTextView(), ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, new Paint[]{Theme.chat_statusPaint, Theme.chat_statusRecordPaint}, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSubtitle", (Object) null));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, Theme.avatarDrawables, null, "avatar_text"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "avatar_backgroundRed"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "avatar_backgroundOrange"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "avatar_backgroundViolet"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "avatar_backgroundGreen"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "avatar_backgroundCyan"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "avatar_backgroundBlue"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "avatar_backgroundPink"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "avatar_nameInMessageRed"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "avatar_nameInMessageOrange"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "avatar_nameInMessageViolet"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "avatar_nameInMessageGreen"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "avatar_nameInMessageCyan"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "avatar_nameInMessageBlue"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "avatar_nameInMessagePink"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgInDrawable, Theme.chat_msgInMediaDrawable}, null, "chat_inBubble"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgInSelectedDrawable, Theme.chat_msgInMediaSelectedDrawable}, null, "chat_inBubbleSelected"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, Theme.chat_msgInDrawable.getShadowDrawables(), null, "chat_inBubbleShadow"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, Theme.chat_msgInMediaDrawable.getShadowDrawables(), null, "chat_inBubbleShadow"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, Theme.chat_msgOutDrawable.getShadowDrawables(), null, "chat_outBubbleShadow"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, Theme.chat_msgOutMediaDrawable.getShadowDrawables(), null, "chat_outBubbleShadow"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutDrawable, Theme.chat_msgOutMediaDrawable}, null, "chat_outBubble"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutDrawable, Theme.chat_msgOutMediaDrawable}, null, "chat_outBubbleGradient"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutDrawable, Theme.chat_msgOutMediaDrawable}, null, "chat_outBubbleGradient2"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutDrawable, Theme.chat_msgOutMediaDrawable}, null, "chat_outBubbleGradient3"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutSelectedDrawable, Theme.chat_msgOutMediaSelectedDrawable}, null, "chat_outBubbleSelected"));
        arrayList.add(new ThemeDescription(this.chatListView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{ChatActionCell.class}, Theme.chat_actionTextPaint, null, null, "chat_serviceText"));
        arrayList.add(new ThemeDescription(this.chatListView, ThemeDescription.FLAG_LINKCOLOR, new Class[]{ChatActionCell.class}, Theme.chat_actionTextPaint, null, null, "chat_serviceLink"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_botCardDrawable, Theme.chat_shareIconDrawable, Theme.chat_botInlineDrawable, Theme.chat_botLinkDrawable, Theme.chat_goIconDrawable, Theme.chat_commentStickerDrawable}, null, "chat_serviceIcon"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class, ChatActionCell.class}, null, null, null, "chat_serviceBackground"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class, ChatActionCell.class}, null, null, null, "chat_serviceBackgroundSelected"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_messageTextIn"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_messageTextOut"));
        arrayList.add(new ThemeDescription(this.chatListView, ThemeDescription.FLAG_LINKCOLOR, new Class[]{ChatMessageCell.class}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_messageLinkIn", (Object) null));
        arrayList.add(new ThemeDescription(this.chatListView, ThemeDescription.FLAG_LINKCOLOR, new Class[]{ChatMessageCell.class}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_messageLinkOut", (Object) null));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutCheckDrawable}, null, "chat_outSentCheck"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutCheckSelectedDrawable}, null, "chat_outSentCheckSelected"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutCheckReadDrawable, Theme.chat_msgOutHalfCheckDrawable}, null, "chat_outSentCheckRead"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutCheckReadSelectedDrawable, Theme.chat_msgOutHalfCheckSelectedDrawable}, null, "chat_outSentCheckReadSelected"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgMediaCheckDrawable, Theme.chat_msgMediaHalfCheckDrawable}, null, "chat_mediaSentCheck"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutViewsDrawable, Theme.chat_msgOutRepliesDrawable, Theme.chat_msgOutPinnedDrawable}, null, "chat_outViews"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutViewsSelectedDrawable, Theme.chat_msgOutRepliesSelectedDrawable, Theme.chat_msgOutPinnedSelectedDrawable}, null, "chat_outViewsSelected"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgInViewsDrawable, Theme.chat_msgInRepliesDrawable, Theme.chat_msgInPinnedDrawable}, null, "chat_inViews"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgInViewsSelectedDrawable, Theme.chat_msgInRepliesSelectedDrawable, Theme.chat_msgInPinnedSelectedDrawable}, null, "chat_inViewsSelected"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgMediaViewsDrawable, Theme.chat_msgMediaRepliesDrawable, Theme.chat_msgMediaPinnedDrawable}, null, "chat_mediaViews"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutMenuDrawable}, null, "chat_outMenu"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutMenuSelectedDrawable}, null, "chat_outMenuSelected"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgInMenuDrawable}, null, "chat_inMenu"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgInMenuSelectedDrawable}, null, "chat_inMenuSelected"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgMediaMenuDrawable}, null, "chat_mediaMenu"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutInstantDrawable}, null, "chat_outInstant"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgInInstantDrawable, Theme.chat_commentDrawable, Theme.chat_commentArrowDrawable}, null, "chat_inInstant"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, Theme.chat_msgOutCallDrawable, null, "chat_outInstant"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, Theme.chat_msgOutCallSelectedDrawable, null, "chat_outInstantSelected"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, Theme.chat_msgInCallDrawable, null, "chat_inInstant"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, Theme.chat_msgInCallSelectedDrawable, null, "chat_inInstantSelected"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgCallUpGreenDrawable}, null, "chat_outUpCall"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgCallDownRedDrawable}, null, "chat_inUpCall"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgCallDownGreenDrawable}, null, "chat_inDownCall"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, Theme.chat_msgErrorPaint, null, null, "chat_sentError"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgErrorDrawable}, null, "chat_sentErrorIcon"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, Theme.chat_durationPaint, null, null, "chat_previewDurationText"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, Theme.chat_gamePaint, null, null, "chat_previewGameText"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inPreviewInstantText"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outPreviewInstantText"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inPreviewInstantSelectedText"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outPreviewInstantSelectedText"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, Theme.chat_deleteProgressPaint, null, null, "chat_secretTimeText"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_stickerNameText"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, Theme.chat_botButtonPaint, null, null, "chat_botButtonText"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, Theme.chat_botProgressPaint, null, null, "chat_botProgress"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inForwardedNameText"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outForwardedNameText"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inViaBotNameText"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outViaBotNameText"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_stickerViaBotNameText"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inReplyLine"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outReplyLine"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_stickerReplyLine"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inReplyNameText"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outReplyNameText"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_stickerReplyNameText"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inReplyMessageText"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outReplyMessageText"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inReplyMediaMessageText"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outReplyMediaMessageText"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inReplyMediaMessageSelectedText"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outReplyMediaMessageSelectedText"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_stickerReplyMessageText"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inPreviewLine"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outPreviewLine"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inSiteNameText"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outSiteNameText"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inContactNameText"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outContactNameText"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inContactPhoneText"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outContactPhoneText"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_mediaProgress"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inAudioProgress"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outAudioProgress"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inAudioSelectedProgress"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outAudioSelectedProgress"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_mediaTimeText"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inTimeText"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outTimeText"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inTimeSelectedText"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outTimeSelectedText"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inAudioPerfomerText"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outAudioPerfomerText"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inAudioTitleText"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outAudioTitleText"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inAudioDurationText"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outAudioDurationText"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inAudioDurationSelectedText"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outAudioDurationSelectedText"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inAudioSeekbar"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outAudioSeekbar"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inAudioSeekbarSelected"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outAudioSeekbarSelected"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inAudioSeekbarFill"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inAudioCacheSeekbar"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outAudioSeekbarFill"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outAudioCacheSeekbar"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inVoiceSeekbar"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outVoiceSeekbar"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inVoiceSeekbarSelected"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outVoiceSeekbarSelected"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inVoiceSeekbarFill"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outVoiceSeekbarFill"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inFileProgress"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outFileProgress"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inFileProgressSelected"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outFileProgressSelected"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inFileNameText"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outFileNameText"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inFileInfoText"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outFileInfoText"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inFileInfoSelectedText"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outFileInfoSelectedText"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inFileBackground"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outFileBackground"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inFileBackgroundSelected"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outFileBackgroundSelected"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inVenueInfoText"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outVenueInfoText"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inVenueInfoSelectedText"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outVenueInfoSelectedText"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_mediaInfoText"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, Theme.chat_urlPaint, null, null, "chat_linkSelectBackground"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, Theme.chat_textSearchSelectionPaint, null, null, "chat_textSelectBackground"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outLoader"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outMediaIcon"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outLoaderSelected"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outMediaIconSelected"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inLoader"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inMediaIcon"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inLoaderSelected"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inMediaIconSelected"));
        Drawable[][] drawableArr = Theme.chat_photoStatesDrawables;
        arrayList.add(new ThemeDescription(this.chatListView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ChatMessageCell.class}, null, new Drawable[]{drawableArr[0][0], drawableArr[1][0], drawableArr[2][0], drawableArr[3][0]}, null, "chat_mediaLoaderPhoto"));
        Drawable[][] drawableArr2 = Theme.chat_photoStatesDrawables;
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{drawableArr2[0][0], drawableArr2[1][0], drawableArr2[2][0], drawableArr2[3][0]}, null, "chat_mediaLoaderPhotoIcon"));
        Drawable[][] drawableArr3 = Theme.chat_photoStatesDrawables;
        arrayList.add(new ThemeDescription(this.chatListView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ChatMessageCell.class}, null, new Drawable[]{drawableArr3[0][1], drawableArr3[1][1], drawableArr3[2][1], drawableArr3[3][1]}, null, "chat_mediaLoaderPhotoSelected"));
        Drawable[][] drawableArr4 = Theme.chat_photoStatesDrawables;
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{drawableArr4[0][1], drawableArr4[1][1], drawableArr4[2][1], drawableArr4[3][1]}, null, "chat_mediaLoaderPhotoIconSelected"));
        Drawable[][] drawableArr5 = Theme.chat_photoStatesDrawables;
        arrayList.add(new ThemeDescription(this.chatListView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ChatMessageCell.class}, null, new Drawable[]{drawableArr5[7][0], drawableArr5[8][0]}, null, "chat_outLoaderPhoto"));
        Drawable[][] drawableArr6 = Theme.chat_photoStatesDrawables;
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{drawableArr6[7][0], drawableArr6[8][0]}, null, "chat_outLoaderPhotoIcon"));
        Drawable[][] drawableArr7 = Theme.chat_photoStatesDrawables;
        arrayList.add(new ThemeDescription(this.chatListView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ChatMessageCell.class}, null, new Drawable[]{drawableArr7[7][1], drawableArr7[8][1]}, null, "chat_outLoaderPhotoSelected"));
        Drawable[][] drawableArr8 = Theme.chat_photoStatesDrawables;
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{drawableArr8[7][1], drawableArr8[8][1]}, null, "chat_outLoaderPhotoIconSelected"));
        Drawable[][] drawableArr9 = Theme.chat_photoStatesDrawables;
        arrayList.add(new ThemeDescription(this.chatListView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ChatMessageCell.class}, null, new Drawable[]{drawableArr9[10][0], drawableArr9[11][0]}, null, "chat_inLoaderPhoto"));
        Drawable[][] drawableArr10 = Theme.chat_photoStatesDrawables;
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{drawableArr10[10][0], drawableArr10[11][0]}, null, "chat_inLoaderPhotoIcon"));
        Drawable[][] drawableArr11 = Theme.chat_photoStatesDrawables;
        arrayList.add(new ThemeDescription(this.chatListView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ChatMessageCell.class}, null, new Drawable[]{drawableArr11[10][1], drawableArr11[11][1]}, null, "chat_inLoaderPhotoSelected"));
        Drawable[][] drawableArr12 = Theme.chat_photoStatesDrawables;
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{drawableArr12[10][1], drawableArr12[11][1]}, null, "chat_inLoaderPhotoIconSelected"));
        arrayList.add(new ThemeDescription(this.chatListView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_contactDrawable[0]}, null, "chat_inContactBackground"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_contactDrawable[0]}, null, "chat_inContactIcon"));
        arrayList.add(new ThemeDescription(this.chatListView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_contactDrawable[1]}, null, "chat_outContactBackground"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_contactDrawable[1]}, null, "chat_outContactIcon"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_inLocationBackground"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_locationDrawable[0]}, null, "chat_inLocationIcon"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, "chat_outLocationBackground"));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_locationDrawable[1]}, null, "chat_outLocationIcon"));
        arrayList.add(new ThemeDescription(this.bottomOverlayChat, 0, null, Theme.chat_composeBackgroundPaint, null, null, "chat_messagePanelBackground"));
        arrayList.add(new ThemeDescription(this.bottomOverlayChat, 0, null, null, new Drawable[]{Theme.chat_composeShadowDrawable}, null, "chat_messagePanelShadow"));
        arrayList.add(new ThemeDescription(this.bottomOverlayChatText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "chat_fieldOverlayText"));
        arrayList.add(new ThemeDescription(this.emptyView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "chat_serviceText"));
        arrayList.add(new ThemeDescription(this.progressBar, ThemeDescription.FLAG_PROGRESSBAR, null, null, null, null, "chat_serviceText"));
        arrayList.add(new ThemeDescription(this.chatListView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE, new Class[]{ChatUnreadCell.class}, new String[]{"backgroundLayout"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_unreadMessagesStartBackground"));
        arrayList.add(new ThemeDescription(this.chatListView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{ChatUnreadCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_unreadMessagesStartArrowIcon"));
        arrayList.add(new ThemeDescription(this.chatListView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{ChatUnreadCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_unreadMessagesStartText"));
        arrayList.add(new ThemeDescription(this.progressView2, ThemeDescription.FLAG_SERVICEBACKGROUND, null, null, null, null, "chat_serviceBackground"));
        arrayList.add(new ThemeDescription(this.emptyView, ThemeDescription.FLAG_SERVICEBACKGROUND, null, null, null, null, "chat_serviceBackground"));
        ChatAvatarContainer chatAvatarContainer = this.avatarContainer;
        ImageView imageView = null;
        arrayList.add(new ThemeDescription(chatAvatarContainer != null ? chatAvatarContainer.getTimeItem() : null, 0, null, null, null, null, "chat_secretTimerBackground"));
        ChatAvatarContainer chatAvatarContainer2 = this.avatarContainer;
        if (chatAvatarContainer2 != null) {
            imageView = chatAvatarContainer2.getTimeItem();
        }
        arrayList.add(new ThemeDescription(imageView, 0, null, null, null, null, "chat_secretTimerText"));
        arrayList.add(new ThemeDescription(this.undoView, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "undo_background"));
        arrayList.add(new ThemeDescription(this.undoView, 0, new Class[]{UndoView.class}, new String[]{"undoImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_cancelColor"));
        arrayList.add(new ThemeDescription(this.undoView, 0, new Class[]{UndoView.class}, new String[]{"undoTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_cancelColor"));
        arrayList.add(new ThemeDescription(this.undoView, 0, new Class[]{UndoView.class}, new String[]{"infoTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_infoColor"));
        arrayList.add(new ThemeDescription(this.undoView, 0, new Class[]{UndoView.class}, new String[]{"textPaint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_infoColor"));
        arrayList.add(new ThemeDescription(this.undoView, 0, new Class[]{UndoView.class}, new String[]{"progressPaint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_infoColor"));
        arrayList.add(new ThemeDescription(this.undoView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{UndoView.class}, new String[]{"leftImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_infoColor"));
        return arrayList;
    }
}
