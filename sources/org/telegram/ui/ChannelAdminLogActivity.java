package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import androidx.collection.LongSparseArray;
import androidx.core.content.FileProvider;
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
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.browser.Browser;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
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

public class ChannelAdminLogActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private ArrayList<TLRPC.ChannelParticipant> admins;
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
    /* access modifiers changed from: private */
    public ChatActivityAdapter chatAdapter;
    /* access modifiers changed from: private */
    public LinearLayoutManager chatLayoutManager;
    /* access modifiers changed from: private */
    public ChatListItemAnimator chatListItemAnimator;
    /* access modifiers changed from: private */
    public RecyclerListView chatListView;
    /* access modifiers changed from: private */
    public ArrayList<ChatMessageCell> chatMessageCellsCache = new ArrayList<>();
    /* access modifiers changed from: private */
    public boolean checkTextureViewPosition;
    /* access modifiers changed from: private */
    public SizeNotifierFrameLayout contentView;
    protected TLRPC.Chat currentChat;
    private TLRPC.TL_channelAdminLogEventsFilter currentFilter = null;
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
    /* access modifiers changed from: private */
    public HashMap<String, Object> invitesCache = new HashMap<>();
    /* access modifiers changed from: private */
    public boolean linviteLoading;
    private boolean loading;
    /* access modifiers changed from: private */
    public int loadsCount;
    protected ArrayList<MessageObject> messages = new ArrayList<>();
    /* access modifiers changed from: private */
    public HashMap<String, ArrayList<MessageObject>> messagesByDays = new HashMap<>();
    private LongSparseArray<MessageObject> messagesDict = new LongSparseArray<>();
    /* access modifiers changed from: private */
    public int[] mid = {2};
    private int minDate;
    private long minEventId;
    private boolean openAnimationEnded;
    private boolean paused = true;
    private RadialProgressView progressBar;
    /* access modifiers changed from: private */
    public FrameLayout progressView;
    private View progressView2;
    /* access modifiers changed from: private */
    public PhotoViewer.PhotoViewerProvider provider = new PhotoViewer.EmptyPhotoViewerProvider() {
        /* JADX WARNING: Code restructure failed: missing block: B:12:0x003d, code lost:
            r6 = (org.telegram.ui.Cells.ChatActionCell) r5;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:5:0x0021, code lost:
            r6 = (org.telegram.ui.Cells.ChatMessageCell) r5;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public org.telegram.ui.PhotoViewer.PlaceProviderObject getPlaceForPhoto(org.telegram.messenger.MessageObject r16, org.telegram.tgnet.TLRPC.FileLocation r17, int r18, boolean r19) {
            /*
                r15 = this;
                r0 = r15
                r1 = r17
                org.telegram.ui.ChannelAdminLogActivity r2 = org.telegram.ui.ChannelAdminLogActivity.this
                org.telegram.ui.Components.RecyclerListView r2 = r2.chatListView
                int r2 = r2.getChildCount()
                r3 = 0
            L_0x000e:
                if (r3 >= r2) goto L_0x00c6
                r4 = 0
                org.telegram.ui.ChannelAdminLogActivity r5 = org.telegram.ui.ChannelAdminLogActivity.this
                org.telegram.ui.Components.RecyclerListView r5 = r5.chatListView
                android.view.View r5 = r5.getChildAt(r3)
                boolean r6 = r5 instanceof org.telegram.ui.Cells.ChatMessageCell
                if (r6 == 0) goto L_0x0039
                if (r16 == 0) goto L_0x0088
                r6 = r5
                org.telegram.ui.Cells.ChatMessageCell r6 = (org.telegram.ui.Cells.ChatMessageCell) r6
                org.telegram.messenger.MessageObject r7 = r6.getMessageObject()
                if (r7 == 0) goto L_0x0038
                int r8 = r7.getId()
                int r9 = r16.getId()
                if (r8 != r9) goto L_0x0038
                org.telegram.messenger.ImageReceiver r4 = r6.getPhotoImage()
            L_0x0038:
                goto L_0x0088
            L_0x0039:
                boolean r6 = r5 instanceof org.telegram.ui.Cells.ChatActionCell
                if (r6 == 0) goto L_0x0088
                r6 = r5
                org.telegram.ui.Cells.ChatActionCell r6 = (org.telegram.ui.Cells.ChatActionCell) r6
                org.telegram.messenger.MessageObject r7 = r6.getMessageObject()
                if (r7 == 0) goto L_0x0088
                if (r16 == 0) goto L_0x0057
                int r8 = r7.getId()
                int r9 = r16.getId()
                if (r8 != r9) goto L_0x0088
                org.telegram.messenger.ImageReceiver r4 = r6.getPhotoImage()
                goto L_0x0088
            L_0x0057:
                if (r1 == 0) goto L_0x0088
                java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r8 = r7.photoThumbs
                if (r8 == 0) goto L_0x0088
                r8 = 0
            L_0x005e:
                java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r9 = r7.photoThumbs
                int r9 = r9.size()
                if (r8 >= r9) goto L_0x0088
                java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r9 = r7.photoThumbs
                java.lang.Object r9 = r9.get(r8)
                org.telegram.tgnet.TLRPC$PhotoSize r9 = (org.telegram.tgnet.TLRPC.PhotoSize) r9
                org.telegram.tgnet.TLRPC$FileLocation r10 = r9.location
                long r10 = r10.volume_id
                long r12 = r1.volume_id
                int r14 = (r10 > r12 ? 1 : (r10 == r12 ? 0 : -1))
                if (r14 != 0) goto L_0x0085
                org.telegram.tgnet.TLRPC$FileLocation r10 = r9.location
                int r10 = r10.local_id
                int r11 = r1.local_id
                if (r10 != r11) goto L_0x0085
                org.telegram.messenger.ImageReceiver r4 = r6.getPhotoImage()
                goto L_0x0088
            L_0x0085:
                int r8 = r8 + 1
                goto L_0x005e
            L_0x0088:
                if (r4 == 0) goto L_0x00c2
                r6 = 2
                int[] r6 = new int[r6]
                r5.getLocationInWindow(r6)
                org.telegram.ui.PhotoViewer$PlaceProviderObject r7 = new org.telegram.ui.PhotoViewer$PlaceProviderObject
                r7.<init>()
                r8 = 0
                r9 = r6[r8]
                r7.viewX = r9
                r9 = 1
                r10 = r6[r9]
                int r11 = android.os.Build.VERSION.SDK_INT
                r12 = 21
                if (r11 < r12) goto L_0x00a4
                goto L_0x00a6
            L_0x00a4:
                int r8 = org.telegram.messenger.AndroidUtilities.statusBarHeight
            L_0x00a6:
                int r10 = r10 - r8
                r7.viewY = r10
                org.telegram.ui.ChannelAdminLogActivity r8 = org.telegram.ui.ChannelAdminLogActivity.this
                org.telegram.ui.Components.RecyclerListView r8 = r8.chatListView
                r7.parentView = r8
                r7.imageReceiver = r4
                org.telegram.messenger.ImageReceiver$BitmapHolder r8 = r4.getBitmapSafe()
                r7.thumb = r8
                int[] r8 = r4.getRoundRadius()
                r7.radius = r8
                r7.isEvent = r9
                return r7
            L_0x00c2:
                int r3 = r3 + 1
                goto L_0x000e
            L_0x00c6:
                r3 = 0
                return r3
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChannelAdminLogActivity.AnonymousClass1.getPlaceForPhoto(org.telegram.messenger.MessageObject, org.telegram.tgnet.TLRPC$FileLocation, int, boolean):org.telegram.ui.PhotoViewer$PlaceProviderObject");
        }
    };
    /* access modifiers changed from: private */
    public FrameLayout roundVideoContainer;
    private MessageObject scrollToMessage;
    /* access modifiers changed from: private */
    public int scrollToOffsetOnRecreate = 0;
    /* access modifiers changed from: private */
    public int scrollToPositionOnRecreate = -1;
    /* access modifiers changed from: private */
    public boolean scrollingFloatingDate;
    private ImageView searchCalendarButton;
    private FrameLayout searchContainer;
    private SimpleTextView searchCountText;
    private ImageView searchDownButton;
    private ActionBarMenuItem searchItem;
    /* access modifiers changed from: private */
    public String searchQuery = "";
    private ImageView searchUpButton;
    /* access modifiers changed from: private */
    public boolean searchWas;
    private LongSparseArray<TLRPC.User> selectedAdmins;
    private MessageObject selectedObject;
    /* access modifiers changed from: private */
    public UndoView undoView;
    /* access modifiers changed from: private */
    public HashMap<Long, TLRPC.User> usersMap;
    private TextureView videoTextureView;
    private boolean wasPaused = false;

    public ChannelAdminLogActivity(TLRPC.Chat chat) {
        this.currentChat = chat;
    }

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
    public void loadMessages(boolean reset) {
        ChatActivityAdapter chatActivityAdapter;
        if (!this.loading) {
            if (reset) {
                this.minEventId = Long.MAX_VALUE;
                FrameLayout frameLayout = this.progressView;
                if (frameLayout != null) {
                    AndroidUtilities.updateViewVisibilityAnimated(frameLayout, true, 0.3f, true);
                    this.emptyViewContainer.setVisibility(4);
                    this.chatListView.setEmptyView((View) null);
                }
                this.messagesDict.clear();
                this.messages.clear();
                this.messagesByDays.clear();
            }
            this.loading = true;
            TLRPC.TL_channels_getAdminLog req = new TLRPC.TL_channels_getAdminLog();
            req.channel = MessagesController.getInputChannel(this.currentChat);
            req.q = this.searchQuery;
            req.limit = 50;
            if (reset || this.messages.isEmpty()) {
                req.max_id = 0;
            } else {
                req.max_id = this.minEventId;
            }
            req.min_id = 0;
            if (this.currentFilter != null) {
                req.flags = 1 | req.flags;
                req.events_filter = this.currentFilter;
            }
            if (this.selectedAdmins != null) {
                req.flags |= 2;
                for (int a = 0; a < this.selectedAdmins.size(); a++) {
                    req.admins.add(MessagesController.getInstance(this.currentAccount).getInputUser(this.selectedAdmins.valueAt(a)));
                }
            }
            updateEmptyPlaceholder();
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new ChannelAdminLogActivity$$ExternalSyntheticLambda1(this));
            if (reset && (chatActivityAdapter = this.chatAdapter) != null) {
                chatActivityAdapter.notifyDataSetChanged();
            }
        }
    }

    /* renamed from: lambda$loadMessages$1$org-telegram-ui-ChannelAdminLogActivity  reason: not valid java name */
    public /* synthetic */ void m1590lambda$loadMessages$1$orgtelegramuiChannelAdminLogActivity(TLObject response, TLRPC.TL_error error) {
        if (response != null) {
            AndroidUtilities.runOnUIThread(new ChannelAdminLogActivity$$ExternalSyntheticLambda9(this, (TLRPC.TL_channels_adminLogResults) response));
        }
    }

    /* renamed from: lambda$loadMessages$0$org-telegram-ui-ChannelAdminLogActivity  reason: not valid java name */
    public /* synthetic */ void m1589lambda$loadMessages$0$orgtelegramuiChannelAdminLogActivity(TLRPC.TL_channels_adminLogResults res) {
        int i = 0;
        this.chatListItemAnimator.setShouldAnimateEnterFromBottom(false);
        MessagesController.getInstance(this.currentAccount).putUsers(res.users, false);
        MessagesController.getInstance(this.currentAccount).putChats(res.chats, false);
        boolean added = false;
        int oldRowsCount = this.messages.size();
        for (int a = 0; a < res.events.size(); a++) {
            TLRPC.TL_channelAdminLogEvent event = res.events.get(a);
            if (this.messagesDict.indexOfKey(event.id) < 0) {
                if (event.action instanceof TLRPC.TL_channelAdminLogEventActionParticipantToggleAdmin) {
                    TLRPC.TL_channelAdminLogEventActionParticipantToggleAdmin action = (TLRPC.TL_channelAdminLogEventActionParticipantToggleAdmin) event.action;
                    if ((action.prev_participant instanceof TLRPC.TL_channelParticipantCreator) && !(action.new_participant instanceof TLRPC.TL_channelParticipantCreator)) {
                    }
                }
                this.minEventId = Math.min(this.minEventId, event.id);
                added = true;
                MessageObject messageObject = new MessageObject(this.currentAccount, event, this.messages, this.messagesByDays, this.currentChat, this.mid, false);
                if (messageObject.contentType >= 0) {
                    this.messagesDict.put(event.id, messageObject);
                }
            }
        }
        int newRowsCount = this.messages.size() - oldRowsCount;
        this.loading = false;
        if (!added) {
            this.endReached = true;
        }
        AndroidUtilities.updateViewVisibilityAnimated(this.progressView, false, 0.3f, true);
        this.chatListView.setEmptyView(this.emptyViewContainer);
        if (newRowsCount != 0) {
            boolean end = false;
            if (this.endReached) {
                end = true;
                this.chatAdapter.notifyItemRangeChanged(0, 2);
            }
            int firstVisPos = this.chatLayoutManager.findLastVisibleItemPosition();
            View firstVisView = this.chatLayoutManager.findViewByPosition(firstVisPos);
            if (firstVisView != null) {
                i = firstVisView.getTop();
            }
            int top = i - this.chatListView.getPaddingTop();
            if (newRowsCount - end > 0) {
                int insertStart = (!end) + true;
                this.chatAdapter.notifyItemChanged(insertStart);
                this.chatAdapter.notifyItemRangeInserted(insertStart, newRowsCount - end);
            }
            if (firstVisPos != -1) {
                this.chatLayoutManager.scrollToPositionWithOffset((firstVisPos + newRowsCount) - end, top);
            }
        } else if (this.endReached) {
            this.chatAdapter.notifyItemRemoved(0);
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0045, code lost:
        r7 = (org.telegram.ui.Cells.ChatMessageCell) r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:43:0x00ab, code lost:
        r4 = (org.telegram.ui.Cells.ChatMessageCell) r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:65:0x0118, code lost:
        r6 = (org.telegram.ui.Cells.ChatMessageCell) r5;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void didReceivedNotification(int r12, int r13, java.lang.Object... r14) {
        /*
            r11 = this;
            int r0 = org.telegram.messenger.NotificationCenter.emojiLoaded
            if (r12 != r0) goto L_0x000d
            org.telegram.ui.Components.RecyclerListView r0 = r11.chatListView
            if (r0 == 0) goto L_0x0148
            r0.invalidateViews()
            goto L_0x0148
        L_0x000d:
            int r0 = org.telegram.messenger.NotificationCenter.messagePlayingDidStart
            r1 = 0
            r2 = 0
            r3 = 1
            if (r12 != r0) goto L_0x0084
            r0 = r14[r2]
            org.telegram.messenger.MessageObject r0 = (org.telegram.messenger.MessageObject) r0
            boolean r4 = r0.isRoundVideo()
            if (r4 == 0) goto L_0x0030
            org.telegram.messenger.MediaController r4 = org.telegram.messenger.MediaController.getInstance()
            android.view.TextureView r5 = r11.createTextureView(r3)
            com.google.android.exoplayer2.ui.AspectRatioFrameLayout r6 = r11.aspectRatioFrameLayout
            android.widget.FrameLayout r7 = r11.roundVideoContainer
            r4.setTextureView(r5, r6, r7, r3)
            r11.updateTextureViewPosition()
        L_0x0030:
            org.telegram.ui.Components.RecyclerListView r4 = r11.chatListView
            if (r4 == 0) goto L_0x0082
            int r4 = r4.getChildCount()
            r5 = 0
        L_0x0039:
            if (r5 >= r4) goto L_0x0082
            org.telegram.ui.Components.RecyclerListView r6 = r11.chatListView
            android.view.View r6 = r6.getChildAt(r5)
            boolean r7 = r6 instanceof org.telegram.ui.Cells.ChatMessageCell
            if (r7 == 0) goto L_0x007f
            r7 = r6
            org.telegram.ui.Cells.ChatMessageCell r7 = (org.telegram.ui.Cells.ChatMessageCell) r7
            org.telegram.messenger.MessageObject r8 = r7.getMessageObject()
            if (r8 == 0) goto L_0x007f
            boolean r9 = r8.isVoice()
            if (r9 != 0) goto L_0x007c
            boolean r9 = r8.isMusic()
            if (r9 == 0) goto L_0x005b
            goto L_0x007c
        L_0x005b:
            boolean r9 = r8.isRoundVideo()
            if (r9 == 0) goto L_0x007f
            r7.checkVideoPlayback(r2, r1)
            org.telegram.messenger.MediaController r9 = org.telegram.messenger.MediaController.getInstance()
            boolean r9 = r9.isPlayingMessage(r8)
            if (r9 != 0) goto L_0x007f
            float r9 = r8.audioProgress
            r10 = 0
            int r9 = (r9 > r10 ? 1 : (r9 == r10 ? 0 : -1))
            if (r9 == 0) goto L_0x007f
            r8.resetPlayingProgress()
            r7.invalidate()
            goto L_0x007f
        L_0x007c:
            r7.updateButtonState(r2, r3, r2)
        L_0x007f:
            int r5 = r5 + 1
            goto L_0x0039
        L_0x0082:
            goto L_0x0148
        L_0x0084:
            int r0 = org.telegram.messenger.NotificationCenter.messagePlayingDidReset
            if (r12 == r0) goto L_0x0103
            int r0 = org.telegram.messenger.NotificationCenter.messagePlayingPlayStateChanged
            if (r12 != r0) goto L_0x008e
            goto L_0x0103
        L_0x008e:
            int r0 = org.telegram.messenger.NotificationCenter.messagePlayingProgressDidChanged
            if (r12 != r0) goto L_0x00db
            r0 = r14[r2]
            java.lang.Integer r0 = (java.lang.Integer) r0
            org.telegram.ui.Components.RecyclerListView r1 = r11.chatListView
            if (r1 == 0) goto L_0x0102
            int r1 = r1.getChildCount()
            r2 = 0
        L_0x009f:
            if (r2 >= r1) goto L_0x0102
            org.telegram.ui.Components.RecyclerListView r3 = r11.chatListView
            android.view.View r3 = r3.getChildAt(r2)
            boolean r4 = r3 instanceof org.telegram.ui.Cells.ChatMessageCell
            if (r4 == 0) goto L_0x00d8
            r4 = r3
            org.telegram.ui.Cells.ChatMessageCell r4 = (org.telegram.ui.Cells.ChatMessageCell) r4
            org.telegram.messenger.MessageObject r5 = r4.getMessageObject()
            if (r5 == 0) goto L_0x00d8
            int r6 = r5.getId()
            int r7 = r0.intValue()
            if (r6 != r7) goto L_0x00d8
            org.telegram.messenger.MediaController r6 = org.telegram.messenger.MediaController.getInstance()
            org.telegram.messenger.MessageObject r6 = r6.getPlayingMessageObject()
            if (r6 == 0) goto L_0x0102
            float r7 = r6.audioProgress
            r5.audioProgress = r7
            int r7 = r6.audioProgressSec
            r5.audioProgressSec = r7
            int r7 = r6.audioPlayerDuration
            r5.audioPlayerDuration = r7
            r4.updatePlayingMessageProgress()
            goto L_0x0102
        L_0x00d8:
            int r2 = r2 + 1
            goto L_0x009f
        L_0x00db:
            int r0 = org.telegram.messenger.NotificationCenter.didSetNewWallpapper
            if (r12 != r0) goto L_0x0102
            android.view.View r0 = r11.fragmentView
            if (r0 == 0) goto L_0x0148
            org.telegram.ui.Components.SizeNotifierFrameLayout r0 = r11.contentView
            android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.getCachedWallpaper()
            boolean r2 = org.telegram.ui.ActionBar.Theme.isWallpaperMotion()
            r0.setBackgroundImage(r1, r2)
            android.view.View r0 = r11.progressView2
            r0.invalidate()
            android.widget.TextView r0 = r11.emptyView
            if (r0 == 0) goto L_0x00fc
            r0.invalidate()
        L_0x00fc:
            org.telegram.ui.Components.RecyclerListView r0 = r11.chatListView
            r0.invalidateViews()
            goto L_0x0148
        L_0x0102:
            goto L_0x0148
        L_0x0103:
            org.telegram.ui.Components.RecyclerListView r0 = r11.chatListView
            if (r0 == 0) goto L_0x0148
            int r0 = r0.getChildCount()
            r4 = 0
        L_0x010c:
            if (r4 >= r0) goto L_0x0148
            org.telegram.ui.Components.RecyclerListView r5 = r11.chatListView
            android.view.View r5 = r5.getChildAt(r4)
            boolean r6 = r5 instanceof org.telegram.ui.Cells.ChatMessageCell
            if (r6 == 0) goto L_0x0145
            r6 = r5
            org.telegram.ui.Cells.ChatMessageCell r6 = (org.telegram.ui.Cells.ChatMessageCell) r6
            org.telegram.messenger.MessageObject r7 = r6.getMessageObject()
            if (r7 == 0) goto L_0x0145
            boolean r8 = r7.isVoice()
            if (r8 != 0) goto L_0x0142
            boolean r8 = r7.isMusic()
            if (r8 == 0) goto L_0x012e
            goto L_0x0142
        L_0x012e:
            boolean r8 = r7.isRoundVideo()
            if (r8 == 0) goto L_0x0145
            org.telegram.messenger.MediaController r8 = org.telegram.messenger.MediaController.getInstance()
            boolean r8 = r8.isPlayingMessage(r7)
            if (r8 != 0) goto L_0x0145
            r6.checkVideoPlayback(r3, r1)
            goto L_0x0145
        L_0x0142:
            r6.updateButtonState(r2, r3, r2)
        L_0x0145:
            int r4 = r4 + 1
            goto L_0x010c
        L_0x0148:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChannelAdminLogActivity.didReceivedNotification(int, int, java.lang.Object[]):void");
    }

    /* access modifiers changed from: private */
    public void updateBottomOverlay() {
    }

    public View createView(Context context) {
        Context context2 = context;
        if (this.chatMessageCellsCache.isEmpty()) {
            for (int a = 0; a < 8; a++) {
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
            public void onItemClick(int id) {
                if (id == -1) {
                    ChannelAdminLogActivity.this.finishFragment();
                }
            }
        });
        ChatAvatarContainer chatAvatarContainer = new ChatAvatarContainer(context2, (ChatActivity) null, false);
        this.avatarContainer = chatAvatarContainer;
        chatAvatarContainer.setOccupyStatusBar(!AndroidUtilities.isTablet());
        this.actionBar.addView(this.avatarContainer, 0, LayoutHelper.createFrame(-2, -1.0f, 51, 56.0f, 0.0f, 40.0f, 0.0f));
        ActionBarMenuItem actionBarMenuItemSearchListener = this.actionBar.createMenu().addItem(0, NUM).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener() {
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
        this.searchItem = actionBarMenuItemSearchListener;
        actionBarMenuItemSearchListener.setSearchFieldHint(LocaleController.getString("Search", NUM));
        this.avatarContainer.setEnabled(false);
        this.avatarContainer.setTitle(this.currentChat.title);
        this.avatarContainer.setSubtitle(LocaleController.getString("EventLogAllEvents", NUM));
        this.avatarContainer.setChatAvatar(this.currentChat);
        this.fragmentView = new SizeNotifierFrameLayout(context2) {
            /* access modifiers changed from: protected */
            public void onAttachedToWindow() {
                super.onAttachedToWindow();
                MessageObject messageObject = MediaController.getInstance().getPlayingMessageObject();
                if (messageObject != null && messageObject.isRoundVideo() && messageObject.eventId != 0 && messageObject.getDialogId() == (-ChannelAdminLogActivity.this.currentChat.id)) {
                    MediaController.getInstance().setTextureView(ChannelAdminLogActivity.this.createTextureView(false), ChannelAdminLogActivity.this.aspectRatioFrameLayout, ChannelAdminLogActivity.this.roundVideoContainer, true);
                }
            }

            /* access modifiers changed from: protected */
            public boolean drawChild(Canvas canvas, View child, long drawingTime) {
                boolean result = super.drawChild(canvas, child, drawingTime);
                if (child == ChannelAdminLogActivity.this.actionBar && ChannelAdminLogActivity.this.parentLayout != null) {
                    ChannelAdminLogActivity.this.parentLayout.drawHeaderShadow(canvas, ChannelAdminLogActivity.this.actionBar.getVisibility() == 0 ? ChannelAdminLogActivity.this.actionBar.getMeasuredHeight() : 0);
                }
                return result;
            }

            /* access modifiers changed from: protected */
            public boolean isActionBarVisible() {
                return ChannelAdminLogActivity.this.actionBar.getVisibility() == 0;
            }

            /* access modifiers changed from: protected */
            public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
                int heightSize = View.MeasureSpec.getSize(heightMeasureSpec);
                setMeasuredDimension(widthSize, heightSize);
                int heightSize2 = heightSize - getPaddingTop();
                measureChildWithMargins(ChannelAdminLogActivity.this.actionBar, widthMeasureSpec, 0, heightMeasureSpec, 0);
                int actionBarHeight = ChannelAdminLogActivity.this.actionBar.getMeasuredHeight();
                if (ChannelAdminLogActivity.this.actionBar.getVisibility() == 0) {
                    heightSize2 -= actionBarHeight;
                }
                int childCount = getChildCount();
                for (int i = 0; i < childCount; i++) {
                    View child = getChildAt(i);
                    if (!(child == null || child.getVisibility() == 8 || child == ChannelAdminLogActivity.this.actionBar)) {
                        if (child == ChannelAdminLogActivity.this.chatListView || child == ChannelAdminLogActivity.this.progressView) {
                            child.measure(View.MeasureSpec.makeMeasureSpec(widthSize, NUM), View.MeasureSpec.makeMeasureSpec(Math.max(AndroidUtilities.dp(10.0f), heightSize2 - AndroidUtilities.dp(50.0f)), NUM));
                        } else if (child == ChannelAdminLogActivity.this.emptyViewContainer) {
                            child.measure(View.MeasureSpec.makeMeasureSpec(widthSize, NUM), View.MeasureSpec.makeMeasureSpec(heightSize2, NUM));
                        } else {
                            measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);
                        }
                    }
                }
            }

            /* access modifiers changed from: protected */
            public void onLayout(boolean changed, int l, int t, int r, int b) {
                int childLeft;
                int childTop;
                int count = getChildCount();
                for (int i = 0; i < count; i++) {
                    View child = getChildAt(i);
                    if (child.getVisibility() != 8) {
                        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) child.getLayoutParams();
                        int width = child.getMeasuredWidth();
                        int height = child.getMeasuredHeight();
                        int gravity = lp.gravity;
                        if (gravity == -1) {
                            gravity = 51;
                        }
                        int verticalGravity = gravity & 112;
                        switch (gravity & 7 & 7) {
                            case 1:
                                childLeft = ((((r - l) - width) / 2) + lp.leftMargin) - lp.rightMargin;
                                break;
                            case 5:
                                childLeft = (r - width) - lp.rightMargin;
                                break;
                            default:
                                childLeft = lp.leftMargin;
                                break;
                        }
                        switch (verticalGravity) {
                            case 16:
                                childTop = ((((b - t) - height) / 2) + lp.topMargin) - lp.bottomMargin;
                                break;
                            case 48:
                                childTop = lp.topMargin + getPaddingTop();
                                if (child != ChannelAdminLogActivity.this.actionBar && ChannelAdminLogActivity.this.actionBar.getVisibility() == 0) {
                                    childTop += ChannelAdminLogActivity.this.actionBar.getMeasuredHeight();
                                    break;
                                }
                            case 80:
                                childTop = ((b - t) - height) - lp.bottomMargin;
                                break;
                            default:
                                childTop = lp.topMargin;
                                break;
                        }
                        if (child == ChannelAdminLogActivity.this.emptyViewContainer) {
                            childTop -= AndroidUtilities.dp(24.0f) - (ChannelAdminLogActivity.this.actionBar.getVisibility() == 0 ? ChannelAdminLogActivity.this.actionBar.getMeasuredHeight() / 2 : 0);
                        } else if (child == ChannelAdminLogActivity.this.actionBar) {
                            childTop -= getPaddingTop();
                        } else if (child == this.backgroundView) {
                            childTop = 0;
                        }
                        child.layout(childLeft, childTop, childLeft + width, childTop + height);
                    }
                }
                ChannelAdminLogActivity.this.updateMessagesVisisblePart();
                notifyHeightChanged();
            }

            public boolean dispatchTouchEvent(MotionEvent ev) {
                if (!AvatarPreviewer.hasVisibleInstance()) {
                    return super.dispatchTouchEvent(ev);
                }
                AvatarPreviewer.getInstance().onTouchEvent(ev);
                return true;
            }
        };
        SizeNotifierFrameLayout sizeNotifierFrameLayout = (SizeNotifierFrameLayout) this.fragmentView;
        this.contentView = sizeNotifierFrameLayout;
        sizeNotifierFrameLayout.setOccupyStatusBar(!AndroidUtilities.isTablet());
        this.contentView.setBackgroundImage(Theme.getCachedWallpaper(), Theme.isWallpaperMotion());
        FrameLayout frameLayout = new FrameLayout(context2);
        this.emptyViewContainer = frameLayout;
        frameLayout.setVisibility(4);
        this.contentView.addView(this.emptyViewContainer, LayoutHelper.createFrame(-1, -2, 17));
        this.emptyViewContainer.setOnTouchListener(ChannelAdminLogActivity$$ExternalSyntheticLambda8.INSTANCE);
        TextView textView = new TextView(context2);
        this.emptyView = textView;
        textView.setTextSize(1, 14.0f);
        this.emptyView.setGravity(17);
        this.emptyView.setTextColor(Theme.getColor("chat_serviceText"));
        this.emptyView.setBackground(Theme.createServiceDrawable(AndroidUtilities.dp(6.0f), this.emptyView, this.contentView));
        this.emptyView.setPadding(AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f));
        this.emptyViewContainer.addView(this.emptyView, LayoutHelper.createFrame(-2, -2.0f, 17, 16.0f, 0.0f, 16.0f, 0.0f));
        AnonymousClass5 r6 = new RecyclerListView(context2) {
            /* JADX WARNING: type inference failed for: r15v8, types: [android.view.View] */
            /* JADX WARNING: Multi-variable type inference failed */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public boolean drawChild(android.graphics.Canvas r18, android.view.View r19, long r20) {
                /*
                    r17 = this;
                    r0 = r17
                    r1 = r18
                    r2 = r19
                    boolean r3 = super.drawChild(r18, r19, r20)
                    boolean r4 = r2 instanceof org.telegram.ui.Cells.ChatMessageCell
                    if (r4 == 0) goto L_0x0153
                    r4 = r2
                    org.telegram.ui.Cells.ChatMessageCell r4 = (org.telegram.ui.Cells.ChatMessageCell) r4
                    org.telegram.messenger.ImageReceiver r5 = r4.getAvatarImage()
                    if (r5 == 0) goto L_0x0153
                    org.telegram.messenger.MessageObject r6 = r4.getMessageObject()
                    boolean r6 = r6.deleted
                    r7 = 0
                    if (r6 == 0) goto L_0x0024
                    r5.setVisible(r7, r7)
                    return r3
                L_0x0024:
                    float r6 = r19.getY()
                    int r6 = (int) r6
                    boolean r8 = r4.drawPinnedBottom()
                    if (r8 == 0) goto L_0x0051
                    org.telegram.ui.ChannelAdminLogActivity r8 = org.telegram.ui.ChannelAdminLogActivity.this
                    org.telegram.ui.Components.RecyclerListView r8 = r8.chatListView
                    androidx.recyclerview.widget.RecyclerView$ViewHolder r8 = r8.getChildViewHolder(r2)
                    int r9 = r8.getAdapterPosition()
                    if (r9 < 0) goto L_0x0051
                    int r10 = r9 + 1
                    org.telegram.ui.ChannelAdminLogActivity r11 = org.telegram.ui.ChannelAdminLogActivity.this
                    org.telegram.ui.Components.RecyclerListView r11 = r11.chatListView
                    androidx.recyclerview.widget.RecyclerView$ViewHolder r8 = r11.findViewHolderForAdapterPosition(r10)
                    if (r8 == 0) goto L_0x0051
                    r5.setVisible(r7, r7)
                    return r3
                L_0x0051:
                    float r8 = r4.getSlidingOffsetX()
                    float r9 = r4.getCheckBoxTranslation()
                    float r8 = r8 + r9
                    float r9 = r19.getY()
                    int r9 = (int) r9
                    int r10 = r4.getLayoutHeight()
                    int r9 = r9 + r10
                    org.telegram.ui.ChannelAdminLogActivity r10 = org.telegram.ui.ChannelAdminLogActivity.this
                    org.telegram.ui.Components.RecyclerListView r10 = r10.chatListView
                    int r10 = r10.getMeasuredHeight()
                    org.telegram.ui.ChannelAdminLogActivity r11 = org.telegram.ui.ChannelAdminLogActivity.this
                    org.telegram.ui.Components.RecyclerListView r11 = r11.chatListView
                    int r11 = r11.getPaddingBottom()
                    int r10 = r10 - r11
                    if (r9 <= r10) goto L_0x007c
                    r9 = r10
                L_0x007c:
                    boolean r11 = r4.drawPinnedTop()
                    if (r11 == 0) goto L_0x00c2
                    org.telegram.ui.ChannelAdminLogActivity r11 = org.telegram.ui.ChannelAdminLogActivity.this
                    org.telegram.ui.Components.RecyclerListView r11 = r11.chatListView
                    androidx.recyclerview.widget.RecyclerView$ViewHolder r11 = r11.getChildViewHolder(r2)
                    int r12 = r11.getAdapterPosition()
                    if (r12 < 0) goto L_0x00c2
                    r13 = 0
                L_0x0093:
                    r14 = 20
                    if (r13 < r14) goto L_0x0098
                    goto L_0x00c2
                L_0x0098:
                    int r13 = r13 + 1
                    int r14 = r12 + -1
                    org.telegram.ui.ChannelAdminLogActivity r15 = org.telegram.ui.ChannelAdminLogActivity.this
                    org.telegram.ui.Components.RecyclerListView r15 = r15.chatListView
                    androidx.recyclerview.widget.RecyclerView$ViewHolder r11 = r15.findViewHolderForAdapterPosition(r14)
                    if (r11 == 0) goto L_0x00c2
                    android.view.View r15 = r11.itemView
                    int r6 = r15.getTop()
                    android.view.View r15 = r11.itemView
                    boolean r15 = r15 instanceof org.telegram.ui.Cells.ChatMessageCell
                    if (r15 == 0) goto L_0x00c2
                    android.view.View r15 = r11.itemView
                    r4 = r15
                    org.telegram.ui.Cells.ChatMessageCell r4 = (org.telegram.ui.Cells.ChatMessageCell) r4
                    boolean r15 = r4.drawPinnedTop()
                    if (r15 != 0) goto L_0x00c0
                    goto L_0x00c2
                L_0x00c0:
                    r12 = r14
                    goto L_0x0093
                L_0x00c2:
                    r11 = 1111490560(0x42400000, float:48.0)
                    int r12 = org.telegram.messenger.AndroidUtilities.dp(r11)
                    int r12 = r9 - r12
                    if (r12 >= r6) goto L_0x00d2
                    int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
                    int r9 = r6 + r11
                L_0x00d2:
                    boolean r11 = r4.drawPinnedBottom()
                    if (r11 != 0) goto L_0x00e6
                    float r11 = r4.getY()
                    int r12 = r4.getMeasuredHeight()
                    float r12 = (float) r12
                    float r11 = r11 + r12
                    int r11 = (int) r11
                    if (r9 <= r11) goto L_0x00e6
                    r9 = r11
                L_0x00e6:
                    r18.save()
                    r11 = 0
                    int r12 = (r8 > r11 ? 1 : (r8 == r11 ? 0 : -1))
                    if (r12 == 0) goto L_0x00f1
                    r1.translate(r8, r11)
                L_0x00f1:
                    org.telegram.messenger.MessageObject$GroupedMessages r11 = r4.getCurrentMessagesGroup()
                    if (r11 == 0) goto L_0x0108
                    org.telegram.messenger.MessageObject$GroupedMessages r11 = r4.getCurrentMessagesGroup()
                    org.telegram.messenger.MessageObject$GroupedMessages$TransitionParams r11 = r11.transitionParams
                    boolean r11 = r11.backgroundChangeBounds
                    if (r11 == 0) goto L_0x0108
                    float r11 = (float) r9
                    float r12 = r4.getTranslationY()
                    float r11 = r11 - r12
                    int r9 = (int) r11
                L_0x0108:
                    r11 = 1110441984(0x42300000, float:44.0)
                    int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
                    int r11 = r9 - r11
                    float r11 = (float) r11
                    r5.setImageY(r11)
                    boolean r11 = r4.shouldDrawAlphaLayer()
                    r12 = 1
                    if (r11 == 0) goto L_0x0144
                    float r11 = r4.getAlpha()
                    r5.setAlpha(r11)
                    float r11 = r4.getScaleX()
                    float r13 = r4.getScaleY()
                    float r14 = r4.getX()
                    float r15 = r4.getPivotX()
                    float r14 = r14 + r15
                    float r15 = r4.getY()
                    int r16 = r4.getHeight()
                    int r7 = r16 >> 1
                    float r7 = (float) r7
                    float r15 = r15 + r7
                    r1.scale(r11, r13, r14, r15)
                    goto L_0x0149
                L_0x0144:
                    r7 = 1065353216(0x3var_, float:1.0)
                    r5.setAlpha(r7)
                L_0x0149:
                    r7 = 0
                    r5.setVisible(r12, r7)
                    r5.draw(r1)
                    r18.restore()
                L_0x0153:
                    return r3
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChannelAdminLogActivity.AnonymousClass5.drawChild(android.graphics.Canvas, android.view.View, long):boolean");
            }
        };
        this.chatListView = r6;
        r6.setOnItemClickListener((RecyclerListView.OnItemClickListener) new ChannelAdminLogActivity$$ExternalSyntheticLambda3(this));
        this.chatListView.setTag(1);
        this.chatListView.setVerticalScrollBarEnabled(true);
        RecyclerListView recyclerListView = this.chatListView;
        ChatActivityAdapter chatActivityAdapter = new ChatActivityAdapter(context2);
        this.chatAdapter = chatActivityAdapter;
        recyclerListView.setAdapter(chatActivityAdapter);
        this.chatListView.setClipToPadding(false);
        this.chatListView.setPadding(0, AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(3.0f));
        RecyclerListView recyclerListView2 = this.chatListView;
        AnonymousClass6 r12 = new ChatListItemAnimator((ChatActivity) null, this.chatListView, (Theme.ResourcesProvider) null) {
            Runnable finishRunnable;
            int scrollAnimationIndex = -1;

            public void onAnimationStart() {
                if (this.scrollAnimationIndex == -1) {
                    this.scrollAnimationIndex = ChannelAdminLogActivity.this.getNotificationCenter().setAnimationInProgress(this.scrollAnimationIndex, (int[]) null, false);
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

            /* access modifiers changed from: protected */
            public void onAllAnimationsDone() {
                super.onAllAnimationsDone();
                Runnable runnable = this.finishRunnable;
                if (runnable != null) {
                    AndroidUtilities.cancelRunOnUIThread(runnable);
                }
                ChannelAdminLogActivity$6$$ExternalSyntheticLambda0 channelAdminLogActivity$6$$ExternalSyntheticLambda0 = new ChannelAdminLogActivity$6$$ExternalSyntheticLambda0(this);
                this.finishRunnable = channelAdminLogActivity$6$$ExternalSyntheticLambda0;
                AndroidUtilities.runOnUIThread(channelAdminLogActivity$6$$ExternalSyntheticLambda0);
            }

            /* renamed from: lambda$onAllAnimationsDone$0$org-telegram-ui-ChannelAdminLogActivity$6  reason: not valid java name */
            public /* synthetic */ void m1592xa4fb96a1() {
                if (this.scrollAnimationIndex != -1) {
                    ChannelAdminLogActivity.this.getNotificationCenter().onAnimationFinish(this.scrollAnimationIndex);
                    this.scrollAnimationIndex = -1;
                }
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("admin logs chatItemAnimator enable notifications");
                }
            }
        };
        this.chatListItemAnimator = r12;
        recyclerListView2.setItemAnimator(r12);
        this.chatListItemAnimator.setReversePositions(true);
        this.chatListView.setLayoutAnimation((LayoutAnimationController) null);
        AnonymousClass7 r62 = new LinearLayoutManager(context2) {
            public boolean supportsPredictiveItemAnimations() {
                return true;
            }

            public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
                LinearSmoothScrollerCustom linearSmoothScroller = new LinearSmoothScrollerCustom(recyclerView.getContext(), 0);
                linearSmoothScroller.setTargetPosition(position);
                startSmoothScroll(linearSmoothScroller);
            }
        };
        this.chatLayoutManager = r62;
        r62.setOrientation(1);
        this.chatLayoutManager.setStackFromEnd(true);
        this.chatListView.setLayoutManager(this.chatLayoutManager);
        this.contentView.addView(this.chatListView, LayoutHelper.createFrame(-1, -1.0f));
        this.chatListView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            private final int scrollValue = AndroidUtilities.dp(100.0f);
            private float totalDy = 0.0f;

            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == 1) {
                    boolean unused = ChannelAdminLogActivity.this.scrollingFloatingDate = true;
                    boolean unused2 = ChannelAdminLogActivity.this.checkTextureViewPosition = true;
                } else if (newState == 0) {
                    boolean unused3 = ChannelAdminLogActivity.this.scrollingFloatingDate = false;
                    boolean unused4 = ChannelAdminLogActivity.this.checkTextureViewPosition = false;
                    ChannelAdminLogActivity.this.hideFloatingDateView(true);
                }
            }

            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                ChannelAdminLogActivity.this.chatListView.invalidate();
                if (dy != 0 && ChannelAdminLogActivity.this.scrollingFloatingDate && !ChannelAdminLogActivity.this.currentFloatingTopIsNotMessage && ChannelAdminLogActivity.this.floatingDateView.getTag() == null) {
                    if (ChannelAdminLogActivity.this.floatingDateAnimation != null) {
                        ChannelAdminLogActivity.this.floatingDateAnimation.cancel();
                    }
                    ChannelAdminLogActivity.this.floatingDateView.setTag(1);
                    AnimatorSet unused = ChannelAdminLogActivity.this.floatingDateAnimation = new AnimatorSet();
                    ChannelAdminLogActivity.this.floatingDateAnimation.setDuration(150);
                    ChannelAdminLogActivity.this.floatingDateAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(ChannelAdminLogActivity.this.floatingDateView, "alpha", new float[]{1.0f})});
                    ChannelAdminLogActivity.this.floatingDateAnimation.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animation) {
                            if (animation.equals(ChannelAdminLogActivity.this.floatingDateAnimation)) {
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
        int i = this.scrollToPositionOnRecreate;
        if (i != -1) {
            this.chatLayoutManager.scrollToPositionWithOffset(i, this.scrollToOffsetOnRecreate);
            this.scrollToPositionOnRecreate = -1;
        }
        FrameLayout frameLayout2 = new FrameLayout(context2);
        this.progressView = frameLayout2;
        frameLayout2.setVisibility(4);
        this.contentView.addView(this.progressView, LayoutHelper.createFrame(-1, -1, 51));
        View view = new View(context2);
        this.progressView2 = view;
        view.setBackground(Theme.createServiceDrawable(AndroidUtilities.dp(18.0f), this.progressView2, this.contentView));
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
        AnonymousClass9 r5 = new FrameLayout(context2) {
            public void onDraw(Canvas canvas) {
                int bottom = Theme.chat_composeShadowDrawable.getIntrinsicHeight();
                Theme.chat_composeShadowDrawable.setBounds(0, 0, getMeasuredWidth(), bottom);
                Theme.chat_composeShadowDrawable.draw(canvas);
                canvas.drawRect(0.0f, (float) bottom, (float) getMeasuredWidth(), (float) getMeasuredHeight(), Theme.chat_composeBackgroundPaint);
            }
        };
        this.bottomOverlayChat = r5;
        r5.setWillNotDraw(false);
        this.bottomOverlayChat.setPadding(0, AndroidUtilities.dp(3.0f), 0, 0);
        this.contentView.addView(this.bottomOverlayChat, LayoutHelper.createFrame(-1, 51, 80));
        this.bottomOverlayChat.setOnClickListener(new ChannelAdminLogActivity$$ExternalSyntheticLambda5(this));
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
        this.bottomOverlayImage.setOnClickListener(new ChannelAdminLogActivity$$ExternalSyntheticLambda6(this));
        AnonymousClass10 r52 = new FrameLayout(context2) {
            public void onDraw(Canvas canvas) {
                int bottom = Theme.chat_composeShadowDrawable.getIntrinsicHeight();
                Theme.chat_composeShadowDrawable.setBounds(0, 0, getMeasuredWidth(), bottom);
                Theme.chat_composeShadowDrawable.draw(canvas);
                canvas.drawRect(0.0f, (float) bottom, (float) getMeasuredWidth(), (float) getMeasuredHeight(), Theme.chat_composeBackgroundPaint);
            }
        };
        this.searchContainer = r52;
        r52.setWillNotDraw(false);
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
        this.searchCalendarButton.setOnClickListener(new ChannelAdminLogActivity$$ExternalSyntheticLambda7(this));
        SimpleTextView simpleTextView = new SimpleTextView(context2);
        this.searchCountText = simpleTextView;
        simpleTextView.setTextColor(Theme.getColor("chat_searchPanelText"));
        this.searchCountText.setTextSize(15);
        this.searchCountText.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.searchContainer.addView(this.searchCountText, LayoutHelper.createFrame(-1, -2.0f, 19, 108.0f, 0.0f, 0.0f, 0.0f));
        this.chatAdapter.updateRows();
        if (!this.loading || !this.messages.isEmpty()) {
            AndroidUtilities.updateViewVisibilityAnimated(this.progressView, false, 0.3f, true);
            this.chatListView.setEmptyView(this.emptyViewContainer);
        } else {
            AndroidUtilities.updateViewVisibilityAnimated(this.progressView, true, 0.3f, true);
            this.chatListView.setEmptyView((View) null);
        }
        this.chatListView.setAnimateEmptyView(true, 1);
        UndoView undoView2 = new UndoView(context2);
        this.undoView = undoView2;
        undoView2.setAdditionalTranslationY((float) AndroidUtilities.dp(51.0f));
        this.contentView.addView(this.undoView, LayoutHelper.createFrame(-1, -2.0f, 83, 8.0f, 0.0f, 8.0f, 8.0f));
        updateEmptyPlaceholder();
        return this.fragmentView;
    }

    static /* synthetic */ boolean lambda$createView$2(View v, MotionEvent event) {
        return true;
    }

    /* renamed from: lambda$createView$3$org-telegram-ui-ChannelAdminLogActivity  reason: not valid java name */
    public /* synthetic */ void m1581lambda$createView$3$orgtelegramuiChannelAdminLogActivity(View view, int position) {
        createMenu(view);
    }

    /* renamed from: lambda$createView$5$org-telegram-ui-ChannelAdminLogActivity  reason: not valid java name */
    public /* synthetic */ void m1583lambda$createView$5$orgtelegramuiChannelAdminLogActivity(View view) {
        if (getParentActivity() != null) {
            AdminLogFilterAlert adminLogFilterAlert = new AdminLogFilterAlert(getParentActivity(), this.currentFilter, this.selectedAdmins, this.currentChat.megagroup);
            adminLogFilterAlert.setCurrentAdmins(this.admins);
            adminLogFilterAlert.setAdminLogFilterAlertDelegate(new ChannelAdminLogActivity$$ExternalSyntheticLambda2(this));
            showDialog(adminLogFilterAlert);
        }
    }

    /* renamed from: lambda$createView$4$org-telegram-ui-ChannelAdminLogActivity  reason: not valid java name */
    public /* synthetic */ void m1582lambda$createView$4$orgtelegramuiChannelAdminLogActivity(TLRPC.TL_channelAdminLogEventsFilter filter, LongSparseArray admins2) {
        this.currentFilter = filter;
        this.selectedAdmins = admins2;
        if (filter == null && admins2 == null) {
            this.avatarContainer.setSubtitle(LocaleController.getString("EventLogAllEvents", NUM));
        } else {
            this.avatarContainer.setSubtitle(LocaleController.getString("EventLogSelectedEvents", NUM));
        }
        loadMessages(true);
    }

    /* renamed from: lambda$createView$6$org-telegram-ui-ChannelAdminLogActivity  reason: not valid java name */
    public /* synthetic */ void m1584lambda$createView$6$orgtelegramuiChannelAdminLogActivity(View v) {
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

    /* renamed from: lambda$createView$8$org-telegram-ui-ChannelAdminLogActivity  reason: not valid java name */
    public /* synthetic */ void m1586lambda$createView$8$orgtelegramuiChannelAdminLogActivity(View view) {
        if (getParentActivity() != null) {
            AndroidUtilities.hideKeyboard(this.searchItem.getSearchField());
            showDialog(AlertsCreator.createCalendarPickerDialog(getParentActivity(), 1375315200000L, new ChannelAdminLogActivity$$ExternalSyntheticLambda11(this), (Theme.ResourcesProvider) null).create());
        }
    }

    /* renamed from: lambda$createView$7$org-telegram-ui-ChannelAdminLogActivity  reason: not valid java name */
    public /* synthetic */ void m1585lambda$createView$7$orgtelegramuiChannelAdminLogActivity(int param) {
        loadMessages(true);
    }

    /* access modifiers changed from: private */
    public boolean createMenu(View v) {
        MessageObject message;
        TLRPC.InputStickerSet stickerSet;
        View view = v;
        if (view instanceof ChatMessageCell) {
            message = ((ChatMessageCell) view).getMessageObject();
        } else if (view instanceof ChatActionCell) {
            message = ((ChatActionCell) view).getMessageObject();
        } else {
            message = null;
        }
        if (message == null) {
            return false;
        }
        int type = getMessageType(message);
        this.selectedObject = message;
        if (getParentActivity() == null) {
            return false;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
        ArrayList arrayList = new ArrayList();
        ArrayList<Integer> options = new ArrayList<>();
        if (this.selectedObject.type == 0 || this.selectedObject.caption != null) {
            arrayList.add(LocaleController.getString("Copy", NUM));
            options.add(3);
        }
        if (type == 1) {
            if (this.selectedObject.currentEvent != null && (this.selectedObject.currentEvent.action instanceof TLRPC.TL_channelAdminLogEventActionChangeStickerSet)) {
                TLRPC.TL_channelAdminLogEventActionChangeStickerSet action = (TLRPC.TL_channelAdminLogEventActionChangeStickerSet) this.selectedObject.currentEvent.action;
                TLRPC.InputStickerSet stickerSet2 = action.new_stickerset;
                if (stickerSet2 == null || (stickerSet2 instanceof TLRPC.TL_inputStickerSetEmpty)) {
                    stickerSet = action.prev_stickerset;
                } else {
                    stickerSet = stickerSet2;
                }
                if (stickerSet != null) {
                    StickersAlert stickersAlert = r0;
                    StickersAlert stickersAlert2 = new StickersAlert((Context) getParentActivity(), (BaseFragment) this, stickerSet, (TLRPC.TL_messages_stickerSet) null, (StickersAlert.StickersAlertDelegate) null);
                    showDialog(stickersAlert);
                    return true;
                }
            } else if (this.selectedObject.currentEvent != null && (this.selectedObject.currentEvent.action instanceof TLRPC.TL_channelAdminLogEventActionChangeHistoryTTL) && ChatObject.canUserDoAdminAction(this.currentChat, 13)) {
                ClearHistoryAlert clearHistoryAlert = new ClearHistoryAlert(getParentActivity(), (TLRPC.User) null, this.currentChat, false, (Theme.ResourcesProvider) null);
                clearHistoryAlert.setDelegate(new ClearHistoryAlert.ClearHistoryAlertDelegate() {
                    public /* synthetic */ void onClearHistory(boolean z) {
                        ClearHistoryAlert.ClearHistoryAlertDelegate.CC.$default$onClearHistory(this, z);
                    }

                    public void onAutoDeleteHistory(int ttl, int action) {
                        ChannelAdminLogActivity.this.getMessagesController().setDialogHistoryTTL(-ChannelAdminLogActivity.this.currentChat.id, ttl);
                        TLRPC.ChatFull chatInfo = ChannelAdminLogActivity.this.getMessagesController().getChatFull(ChannelAdminLogActivity.this.currentChat.id);
                        if (chatInfo != null) {
                            ChannelAdminLogActivity.this.undoView.showWithAction(-ChannelAdminLogActivity.this.currentChat.id, action, (Object) null, (Object) Integer.valueOf(chatInfo.ttl_period), (Runnable) null, (Runnable) null);
                        }
                    }
                });
                showDialog(clearHistoryAlert);
            }
        } else if (type == 3) {
            if ((this.selectedObject.messageOwner.media instanceof TLRPC.TL_messageMediaWebPage) && MessageObject.isNewGifDocument(this.selectedObject.messageOwner.media.webpage.document)) {
                arrayList.add(LocaleController.getString("SaveToGIFs", NUM));
                options.add(11);
            }
        } else if (type == 4) {
            if (this.selectedObject.isVideo()) {
                arrayList.add(LocaleController.getString("SaveToGallery", NUM));
                options.add(4);
                arrayList.add(LocaleController.getString("ShareFile", NUM));
                options.add(6);
            } else if (this.selectedObject.isMusic()) {
                arrayList.add(LocaleController.getString("SaveToMusic", NUM));
                options.add(10);
                arrayList.add(LocaleController.getString("ShareFile", NUM));
                options.add(6);
            } else if (this.selectedObject.getDocument() != null) {
                if (MessageObject.isNewGifDocument(this.selectedObject.getDocument())) {
                    arrayList.add(LocaleController.getString("SaveToGIFs", NUM));
                    options.add(11);
                }
                arrayList.add(LocaleController.getString("SaveToDownloads", NUM));
                options.add(10);
                arrayList.add(LocaleController.getString("ShareFile", NUM));
                options.add(6);
            } else {
                arrayList.add(LocaleController.getString("SaveToGallery", NUM));
                options.add(4);
            }
        } else if (type == 5) {
            arrayList.add(LocaleController.getString("ApplyLocalizationFile", NUM));
            options.add(5);
            arrayList.add(LocaleController.getString("SaveToDownloads", NUM));
            options.add(10);
            arrayList.add(LocaleController.getString("ShareFile", NUM));
            options.add(6);
        } else if (type == 10) {
            arrayList.add(LocaleController.getString("ApplyThemeFile", NUM));
            options.add(5);
            arrayList.add(LocaleController.getString("SaveToDownloads", NUM));
            options.add(10);
            arrayList.add(LocaleController.getString("ShareFile", NUM));
            options.add(6);
        } else if (type == 6) {
            arrayList.add(LocaleController.getString("SaveToGallery", NUM));
            options.add(7);
            arrayList.add(LocaleController.getString("SaveToDownloads", NUM));
            options.add(10);
            arrayList.add(LocaleController.getString("ShareFile", NUM));
            options.add(6);
        } else if (type == 7) {
            if (this.selectedObject.isMask()) {
                arrayList.add(LocaleController.getString("AddToMasks", NUM));
            } else {
                arrayList.add(LocaleController.getString("AddToStickers", NUM));
            }
            options.add(9);
        } else if (type == 8) {
            long uid = this.selectedObject.messageOwner.media.user_id;
            TLRPC.User user = null;
            if (uid != 0) {
                user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(uid));
            }
            if (!(user == null || user.id == UserConfig.getInstance(this.currentAccount).getClientUserId() || ContactsController.getInstance(this.currentAccount).contactsDict.get(Long.valueOf(user.id)) != null)) {
                arrayList.add(LocaleController.getString("AddContactTitle", NUM));
                options.add(15);
            }
            if (!TextUtils.isEmpty(this.selectedObject.messageOwner.media.phone_number)) {
                arrayList.add(LocaleController.getString("Copy", NUM));
                options.add(16);
                arrayList.add(LocaleController.getString("Call", NUM));
                options.add(17);
            }
        }
        if (options.isEmpty()) {
            return false;
        }
        builder.setItems((CharSequence[]) arrayList.toArray(new CharSequence[0]), new ChannelAdminLogActivity$$ExternalSyntheticLambda4(this, options));
        builder.setTitle(LocaleController.getString("Message", NUM));
        showDialog(builder.create());
        return true;
    }

    /* renamed from: lambda$createMenu$9$org-telegram-ui-ChannelAdminLogActivity  reason: not valid java name */
    public /* synthetic */ void m1580lambda$createMenu$9$orgtelegramuiChannelAdminLogActivity(ArrayList options, DialogInterface dialogInterface, int i) {
        if (this.selectedObject != null && i >= 0 && i < options.size()) {
            processSelectedOption(((Integer) options.get(i)).intValue());
        }
    }

    private String getMessageContent(MessageObject messageObject, int previousUid, boolean name) {
        TLRPC.Chat chat;
        String str = "";
        if (name) {
            long fromId = messageObject.getFromChatId();
            if (((long) previousUid) != fromId) {
                if (fromId > 0) {
                    TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(fromId));
                    if (user != null) {
                        str = ContactsController.formatName(user.first_name, user.last_name) + ":\n";
                    }
                } else if (fromId < 0 && (chat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(-fromId))) != null) {
                    str = chat.title + ":\n";
                }
            }
        }
        if (messageObject.type == 0 && messageObject.messageOwner.message != null) {
            return str + messageObject.messageOwner.message;
        } else if (messageObject.messageOwner.media == null || messageObject.messageOwner.message == null) {
            return str + messageObject.messageText;
        } else {
            return str + messageObject.messageOwner.message;
        }
    }

    /* access modifiers changed from: private */
    public TextureView createTextureView(boolean add) {
        if (this.parentLayout == null) {
            return null;
        }
        if (this.roundVideoContainer == null) {
            if (Build.VERSION.SDK_INT >= 21) {
                AnonymousClass12 r0 = new FrameLayout(getParentActivity()) {
                    public void setTranslationY(float translationY) {
                        super.setTranslationY(translationY);
                        ChannelAdminLogActivity.this.contentView.invalidate();
                    }
                };
                this.roundVideoContainer = r0;
                r0.setOutlineProvider(new ViewOutlineProvider() {
                    public void getOutline(View view, Outline outline) {
                        outline.setOval(0, 0, AndroidUtilities.roundMessageSize, AndroidUtilities.roundMessageSize);
                    }
                });
                this.roundVideoContainer.setClipToOutline(true);
            } else {
                this.roundVideoContainer = new FrameLayout(getParentActivity()) {
                    /* access modifiers changed from: protected */
                    public void onSizeChanged(int w, int h, int oldw, int oldh) {
                        super.onSizeChanged(w, h, oldw, oldh);
                        ChannelAdminLogActivity.this.aspectPath.reset();
                        ChannelAdminLogActivity.this.aspectPath.addCircle((float) (w / 2), (float) (h / 2), (float) (w / 2), Path.Direction.CW);
                        ChannelAdminLogActivity.this.aspectPath.toggleInverseFillType();
                    }

                    public void setTranslationY(float translationY) {
                        super.setTranslationY(translationY);
                        ChannelAdminLogActivity.this.contentView.invalidate();
                    }

                    public void setVisibility(int visibility) {
                        super.setVisibility(visibility);
                        if (visibility == 0) {
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
            if (add) {
                this.roundVideoContainer.addView(this.aspectRatioFrameLayout, LayoutHelper.createFrame(-1, -1.0f));
            }
            TextureView textureView = new TextureView(getParentActivity());
            this.videoTextureView = textureView;
            textureView.setOpaque(false);
            this.aspectRatioFrameLayout.addView(this.videoTextureView, LayoutHelper.createFrame(-1, -1.0f));
        }
        if (this.roundVideoContainer.getParent() == null) {
            this.contentView.addView(this.roundVideoContainer, 1, new FrameLayout.LayoutParams(AndroidUtilities.roundMessageSize, AndroidUtilities.roundMessageSize));
        }
        this.roundVideoContainer.setVisibility(4);
        this.aspectRatioFrameLayout.setDrawingReady(false);
        return this.videoTextureView;
    }

    private void destroyTextureView() {
        FrameLayout frameLayout = this.roundVideoContainer;
        if (frameLayout != null && frameLayout.getParent() != null) {
            this.contentView.removeView(this.roundVideoContainer);
            this.aspectRatioFrameLayout.setDrawingReady(false);
            this.roundVideoContainer.setVisibility(4);
            if (Build.VERSION.SDK_INT < 21) {
                this.roundVideoContainer.setLayerType(0, (Paint) null);
            }
        }
    }

    private void processSelectedOption(int option) {
        String path;
        MessageObject messageObject = this.selectedObject;
        if (messageObject != null) {
            int i = 3;
            int i2 = 0;
            switch (option) {
                case 3:
                    AndroidUtilities.addToClipboard(getMessageContent(messageObject, 0, true));
                    break;
                case 4:
                    String path2 = messageObject.messageOwner.attachPath;
                    if (path2 != null && path2.length() > 0 && !new File(path2).exists()) {
                        path2 = null;
                    }
                    if (path2 == null || path2.length() == 0) {
                        path2 = FileLoader.getPathToMessage(this.selectedObject.messageOwner).toString();
                    }
                    if (this.selectedObject.type == 3 || this.selectedObject.type == 1) {
                        if (Build.VERSION.SDK_INT < 23 || ((Build.VERSION.SDK_INT > 28 && !BuildVars.NO_SCOPED_STORAGE) || getParentActivity().checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") == 0)) {
                            Activity parentActivity = getParentActivity();
                            if (this.selectedObject.type == 3) {
                                i2 = 1;
                            }
                            MediaController.saveFile(path2, parentActivity, i2, (String) null, (String) null);
                            break;
                        } else {
                            getParentActivity().requestPermissions(new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 4);
                            this.selectedObject = null;
                            return;
                        }
                    }
                    break;
                case 5:
                    File locFile = null;
                    if (!(messageObject.messageOwner.attachPath == null || this.selectedObject.messageOwner.attachPath.length() == 0)) {
                        File f = new File(this.selectedObject.messageOwner.attachPath);
                        if (f.exists()) {
                            locFile = f;
                        }
                    }
                    if (locFile == null) {
                        File f2 = FileLoader.getPathToMessage(this.selectedObject.messageOwner);
                        if (f2.exists()) {
                            locFile = f2;
                        }
                    }
                    if (locFile != null) {
                        if (!locFile.getName().toLowerCase().endsWith("attheme")) {
                            if (!LocaleController.getInstance().applyLanguageFile(locFile, this.currentAccount)) {
                                if (getParentActivity() != null) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
                                    builder.setTitle(LocaleController.getString("AppName", NUM));
                                    builder.setMessage(LocaleController.getString("IncorrectLocalization", NUM));
                                    builder.setPositiveButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null);
                                    showDialog(builder.create());
                                    break;
                                } else {
                                    this.selectedObject = null;
                                    return;
                                }
                            } else {
                                presentFragment(new LanguageSelectActivity());
                                break;
                            }
                        } else {
                            LinearLayoutManager linearLayoutManager = this.chatLayoutManager;
                            if (linearLayoutManager != null) {
                                if (linearLayoutManager.findLastVisibleItemPosition() < this.chatLayoutManager.getItemCount() - 1) {
                                    int findFirstVisibleItemPosition = this.chatLayoutManager.findFirstVisibleItemPosition();
                                    this.scrollToPositionOnRecreate = findFirstVisibleItemPosition;
                                    RecyclerListView.Holder holder = (RecyclerListView.Holder) this.chatListView.findViewHolderForAdapterPosition(findFirstVisibleItemPosition);
                                    if (holder != null) {
                                        this.scrollToOffsetOnRecreate = holder.itemView.getTop();
                                    } else {
                                        this.scrollToPositionOnRecreate = -1;
                                    }
                                } else {
                                    this.scrollToPositionOnRecreate = -1;
                                }
                            }
                            Theme.ThemeInfo themeInfo = Theme.applyThemeFile(locFile, this.selectedObject.getDocumentName(), (TLRPC.TL_theme) null, true);
                            if (themeInfo == null) {
                                this.scrollToPositionOnRecreate = -1;
                                if (getParentActivity() != null) {
                                    AlertDialog.Builder builder2 = new AlertDialog.Builder((Context) getParentActivity());
                                    builder2.setTitle(LocaleController.getString("AppName", NUM));
                                    builder2.setMessage(LocaleController.getString("IncorrectTheme", NUM));
                                    builder2.setPositiveButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null);
                                    showDialog(builder2.create());
                                    break;
                                } else {
                                    this.selectedObject = null;
                                    return;
                                }
                            } else {
                                presentFragment(new ThemePreviewActivity(themeInfo));
                                break;
                            }
                        }
                    }
                    break;
                case 6:
                    String path3 = messageObject.messageOwner.attachPath;
                    if (path3 != null && path3.length() > 0 && !new File(path3).exists()) {
                        path3 = null;
                    }
                    if (path3 == null || path3.length() == 0) {
                        path = FileLoader.getPathToMessage(this.selectedObject.messageOwner).toString();
                    } else {
                        path = path3;
                    }
                    Intent intent = new Intent("android.intent.action.SEND");
                    intent.setType(this.selectedObject.getDocument().mime_type);
                    if (Build.VERSION.SDK_INT >= 24) {
                        try {
                            intent.putExtra("android.intent.extra.STREAM", FileProvider.getUriForFile(getParentActivity(), "org.telegram.messenger.beta.provider", new File(path)));
                            intent.setFlags(1);
                        } catch (Exception e) {
                            intent.putExtra("android.intent.extra.STREAM", Uri.fromFile(new File(path)));
                        }
                    } else {
                        intent.putExtra("android.intent.extra.STREAM", Uri.fromFile(new File(path)));
                    }
                    getParentActivity().startActivityForResult(Intent.createChooser(intent, LocaleController.getString("ShareFile", NUM)), 500);
                    break;
                case 7:
                    String path4 = messageObject.messageOwner.attachPath;
                    if (path4 != null && path4.length() > 0 && !new File(path4).exists()) {
                        path4 = null;
                    }
                    if (path4 == null || path4.length() == 0) {
                        path4 = FileLoader.getPathToMessage(this.selectedObject.messageOwner).toString();
                    }
                    if (Build.VERSION.SDK_INT < 23 || ((Build.VERSION.SDK_INT > 28 && !BuildVars.NO_SCOPED_STORAGE) || getParentActivity().checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") == 0)) {
                        MediaController.saveFile(path4, getParentActivity(), 0, (String) null, (String) null);
                        break;
                    } else {
                        getParentActivity().requestPermissions(new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 4);
                        this.selectedObject = null;
                        return;
                    }
                    break;
                case 9:
                    showDialog(new StickersAlert((Context) getParentActivity(), (BaseFragment) this, this.selectedObject.getInputStickerSet(), (TLRPC.TL_messages_stickerSet) null, (StickersAlert.StickersAlertDelegate) null));
                    break;
                case 10:
                    if (Build.VERSION.SDK_INT < 23 || ((Build.VERSION.SDK_INT > 28 && !BuildVars.NO_SCOPED_STORAGE) || getParentActivity().checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") == 0)) {
                        String fileName = FileLoader.getDocumentFileName(this.selectedObject.getDocument());
                        if (TextUtils.isEmpty(fileName)) {
                            fileName = this.selectedObject.getFileName();
                        }
                        String path5 = this.selectedObject.messageOwner.attachPath;
                        if (path5 != null && path5.length() > 0 && !new File(path5).exists()) {
                            path5 = null;
                        }
                        if (path5 == null || path5.length() == 0) {
                            path5 = FileLoader.getPathToMessage(this.selectedObject.messageOwner).toString();
                        }
                        Activity parentActivity2 = getParentActivity();
                        if (!this.selectedObject.isMusic()) {
                            i = 2;
                        }
                        MediaController.saveFile(path5, parentActivity2, i, fileName, this.selectedObject.getDocument() != null ? this.selectedObject.getDocument().mime_type : "");
                        break;
                    } else {
                        getParentActivity().requestPermissions(new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 4);
                        this.selectedObject = null;
                        return;
                    }
                    break;
                case 11:
                    MessagesController.getInstance(this.currentAccount).saveGif(this.selectedObject, messageObject.getDocument());
                    break;
                case 15:
                    Bundle args = new Bundle();
                    args.putLong("user_id", this.selectedObject.messageOwner.media.user_id);
                    args.putString("phone", this.selectedObject.messageOwner.media.phone_number);
                    args.putBoolean("addContact", true);
                    presentFragment(new ContactAddActivity(args));
                    break;
                case 16:
                    AndroidUtilities.addToClipboard(messageObject.messageOwner.media.phone_number);
                    break;
                case 17:
                    try {
                        Intent intent2 = new Intent("android.intent.action.DIAL", Uri.parse("tel:" + this.selectedObject.messageOwner.media.phone_number));
                        intent2.addFlags(NUM);
                        getParentActivity().startActivityForResult(intent2, 500);
                        break;
                    } catch (Exception e2) {
                        FileLog.e((Throwable) e2);
                        break;
                    }
            }
            this.selectedObject = null;
        }
    }

    private int getMessageType(MessageObject messageObject) {
        String mime;
        if (messageObject == null || messageObject.type == 6) {
            return -1;
        }
        if (messageObject.type == 10 || messageObject.type == 11 || messageObject.type == 16) {
            if (messageObject.getId() == 0) {
                return -1;
            }
            return 1;
        } else if (messageObject.isVoice()) {
            return 2;
        } else {
            if (messageObject.isSticker() || messageObject.isAnimatedSticker()) {
                TLRPC.InputStickerSet inputStickerSet = messageObject.getInputStickerSet();
                if (inputStickerSet instanceof TLRPC.TL_inputStickerSetID) {
                    if (!MediaDataController.getInstance(this.currentAccount).isStickerPackInstalled(inputStickerSet.id)) {
                        return 7;
                    }
                } else if (!(inputStickerSet instanceof TLRPC.TL_inputStickerSetShortName) || MediaDataController.getInstance(this.currentAccount).isStickerPackInstalled(inputStickerSet.short_name)) {
                    return 2;
                } else {
                    return 7;
                }
            } else if ((!messageObject.isRoundVideo() || (messageObject.isRoundVideo() && BuildVars.DEBUG_VERSION)) && ((messageObject.messageOwner.media instanceof TLRPC.TL_messageMediaPhoto) || messageObject.getDocument() != null || messageObject.isMusic() || messageObject.isVideo())) {
                boolean canSave = false;
                if (!(messageObject.messageOwner.attachPath == null || messageObject.messageOwner.attachPath.length() == 0 || !new File(messageObject.messageOwner.attachPath).exists())) {
                    canSave = true;
                }
                if (!canSave && FileLoader.getPathToMessage(messageObject.messageOwner).exists()) {
                    canSave = true;
                }
                if (canSave) {
                    if (messageObject.getDocument() == null || (mime = messageObject.getDocument().mime_type) == null) {
                        return 4;
                    }
                    if (messageObject.getDocumentName().toLowerCase().endsWith("attheme")) {
                        return 10;
                    }
                    if (mime.endsWith("/xml")) {
                        return 5;
                    }
                    if (mime.endsWith("/png") || mime.endsWith("/jpg") || mime.endsWith("/jpeg")) {
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
    }

    private void loadAdmins() {
        TLRPC.TL_channels_getParticipants req = new TLRPC.TL_channels_getParticipants();
        req.channel = MessagesController.getInputChannel(this.currentChat);
        req.filter = new TLRPC.TL_channelParticipantsAdmins();
        req.offset = 0;
        req.limit = 200;
        ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new ChannelAdminLogActivity$$ExternalSyntheticLambda12(this)), this.classGuid);
    }

    /* renamed from: lambda$loadAdmins$11$org-telegram-ui-ChannelAdminLogActivity  reason: not valid java name */
    public /* synthetic */ void m1588lambda$loadAdmins$11$orgtelegramuiChannelAdminLogActivity(TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new ChannelAdminLogActivity$$ExternalSyntheticLambda10(this, error, response));
    }

    /* renamed from: lambda$loadAdmins$10$org-telegram-ui-ChannelAdminLogActivity  reason: not valid java name */
    public /* synthetic */ void m1587lambda$loadAdmins$10$orgtelegramuiChannelAdminLogActivity(TLRPC.TL_error error, TLObject response) {
        if (error == null) {
            TLRPC.TL_channels_channelParticipants res = (TLRPC.TL_channels_channelParticipants) response;
            getMessagesController().putUsers(res.users, false);
            getMessagesController().putChats(res.chats, false);
            this.admins = res.participants;
            if (this.visibleDialog instanceof AdminLogFilterAlert) {
                ((AdminLogFilterAlert) this.visibleDialog).setCurrentAdmins(this.admins);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onRemoveFromParent() {
        MediaController.getInstance().setTextureView(this.videoTextureView, (AspectRatioFrameLayout) null, (FrameLayout) null, false);
    }

    /* access modifiers changed from: private */
    public void hideFloatingDateView(boolean animated) {
        if (this.floatingDateView.getTag() != null && !this.currentFloatingDateOnScreen) {
            if (!this.scrollingFloatingDate || this.currentFloatingTopIsNotMessage) {
                this.floatingDateView.setTag((Object) null);
                if (animated) {
                    AnimatorSet animatorSet = new AnimatorSet();
                    this.floatingDateAnimation = animatorSet;
                    animatorSet.setDuration(150);
                    this.floatingDateAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.floatingDateView, "alpha", new float[]{0.0f})});
                    this.floatingDateAnimation.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animation) {
                            if (animation.equals(ChannelAdminLogActivity.this.floatingDateAnimation)) {
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
    public void checkScrollForLoad(boolean scroll) {
        int checkLoadCount;
        LinearLayoutManager linearLayoutManager = this.chatLayoutManager;
        if (linearLayoutManager != null && !this.paused) {
            int firstVisibleItem = linearLayoutManager.findFirstVisibleItemPosition();
            if ((firstVisibleItem == -1 ? 0 : Math.abs(this.chatLayoutManager.findLastVisibleItemPosition() - firstVisibleItem) + 1) > 0) {
                int itemCount = this.chatAdapter.getItemCount();
                if (scroll) {
                    checkLoadCount = 25;
                } else {
                    checkLoadCount = 5;
                }
                if (firstVisibleItem <= checkLoadCount && !this.loading && !this.endReached) {
                    loadMessages(false);
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void moveScrollToLastMessage() {
        if (this.chatListView != null && !this.messages.isEmpty()) {
            this.chatLayoutManager.scrollToPositionWithOffset(this.messages.size() - 1, -100000 - this.chatListView.getPaddingTop());
        }
    }

    private void updateTextureViewPosition() {
        boolean foundTextureViewMessage = false;
        int count = this.chatListView.getChildCount();
        int a = 0;
        while (true) {
            if (a >= count) {
                break;
            }
            View view = this.chatListView.getChildAt(a);
            if (view instanceof ChatMessageCell) {
                ChatMessageCell messageCell = (ChatMessageCell) view;
                MessageObject messageObject = messageCell.getMessageObject();
                if (this.roundVideoContainer != null && messageObject.isRoundVideo() && MediaController.getInstance().isPlayingMessage(messageObject)) {
                    ImageReceiver imageReceiver = messageCell.getPhotoImage();
                    this.roundVideoContainer.setTranslationX(imageReceiver.getImageX());
                    this.roundVideoContainer.setTranslationY(((float) (this.fragmentView.getPaddingTop() + messageCell.getTop())) + imageReceiver.getImageY());
                    this.fragmentView.invalidate();
                    this.roundVideoContainer.invalidate();
                    foundTextureViewMessage = true;
                    break;
                }
            }
            a++;
        }
        if (this.roundVideoContainer != null) {
            MessageObject messageObject2 = MediaController.getInstance().getPlayingMessageObject();
            if (!foundTextureViewMessage) {
                this.roundVideoContainer.setTranslationY((float) ((-AndroidUtilities.roundMessageSize) - 100));
                this.fragmentView.invalidate();
                if (messageObject2 != null && messageObject2.isRoundVideo()) {
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
    /* JADX WARNING: Removed duplicated region for block: B:103:0x0240  */
    /* JADX WARNING: Removed duplicated region for block: B:104:0x024f  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void updateMessagesVisisblePart() {
        /*
            r26 = this;
            r0 = r26
            org.telegram.ui.Components.RecyclerListView r1 = r0.chatListView
            if (r1 != 0) goto L_0x0007
            return
        L_0x0007:
            int r1 = r1.getChildCount()
            org.telegram.ui.Components.RecyclerListView r2 = r0.chatListView
            int r2 = r2.getMeasuredHeight()
            r3 = 2147483647(0x7fffffff, float:NaN)
            r4 = 2147483647(0x7fffffff, float:NaN)
            r5 = 0
            r6 = 0
            r7 = 0
            r8 = 0
            r9 = 0
        L_0x001c:
            r10 = 0
            if (r9 >= r1) goto L_0x0159
            org.telegram.ui.Components.RecyclerListView r12 = r0.chatListView
            android.view.View r12 = r12.getChildAt(r9)
            boolean r13 = r12 instanceof org.telegram.ui.Cells.ChatMessageCell
            if (r13 == 0) goto L_0x00d7
            r13 = r12
            org.telegram.ui.Cells.ChatMessageCell r13 = (org.telegram.ui.Cells.ChatMessageCell) r13
            int r15 = r13.getTop()
            int r24 = r13.getBottom()
            if (r15 < 0) goto L_0x0037
            goto L_0x0038
        L_0x0037:
            int r10 = -r15
        L_0x0038:
            int r14 = r13.getMeasuredHeight()
            if (r14 <= r2) goto L_0x0043
            int r14 = r10 + r2
            r25 = r14
            goto L_0x0045
        L_0x0043:
            r25 = r14
        L_0x0045:
            int r16 = r25 - r10
            org.telegram.ui.Components.SizeNotifierFrameLayout r14 = r0.contentView
            int r14 = r14.getHeightWithKeyboard()
            r17 = 1111490560(0x42400000, float:48.0)
            int r17 = org.telegram.messenger.AndroidUtilities.dp(r17)
            int r14 = r14 - r17
            org.telegram.ui.Components.RecyclerListView r11 = r0.chatListView
            int r11 = r11.getTop()
            int r17 = r14 - r11
            r18 = 0
            float r11 = r12.getY()
            org.telegram.ui.ActionBar.ActionBar r14 = r0.actionBar
            int r14 = r14.getMeasuredHeight()
            float r14 = (float) r14
            float r11 = r11 + r14
            org.telegram.ui.Components.SizeNotifierFrameLayout r14 = r0.contentView
            int r14 = r14.getBackgroundTranslationY()
            float r14 = (float) r14
            float r19 = r11 - r14
            org.telegram.ui.Components.SizeNotifierFrameLayout r11 = r0.contentView
            int r20 = r11.getMeasuredWidth()
            org.telegram.ui.Components.SizeNotifierFrameLayout r11 = r0.contentView
            int r21 = r11.getBackgroundSizeY()
            r22 = 0
            r23 = 0
            r14 = r13
            r11 = r15
            r15 = r10
            r14.setVisiblePart(r15, r16, r17, r18, r19, r20, r21, r22, r23)
            org.telegram.messenger.MessageObject r14 = r13.getMessageObject()
            android.widget.FrameLayout r15 = r0.roundVideoContainer
            if (r15 == 0) goto L_0x00d2
            boolean r15 = r14.isRoundVideo()
            if (r15 == 0) goto L_0x00d2
            org.telegram.messenger.MediaController r15 = org.telegram.messenger.MediaController.getInstance()
            boolean r15 = r15.isPlayingMessage(r14)
            if (r15 == 0) goto L_0x00d2
            org.telegram.messenger.ImageReceiver r15 = r13.getPhotoImage()
            r16 = r1
            android.widget.FrameLayout r1 = r0.roundVideoContainer
            r17 = r2
            float r2 = r15.getImageX()
            r1.setTranslationX(r2)
            android.widget.FrameLayout r1 = r0.roundVideoContainer
            android.view.View r2 = r0.fragmentView
            int r2 = r2.getPaddingTop()
            int r2 = r2 + r11
            float r2 = (float) r2
            float r18 = r15.getImageY()
            float r2 = r2 + r18
            r1.setTranslationY(r2)
            android.view.View r1 = r0.fragmentView
            r1.invalidate()
            android.widget.FrameLayout r1 = r0.roundVideoContainer
            r1.invalidate()
            r8 = 1
            goto L_0x00d6
        L_0x00d2:
            r16 = r1
            r17 = r2
        L_0x00d6:
            goto L_0x00ff
        L_0x00d7:
            r16 = r1
            r17 = r2
            boolean r1 = r12 instanceof org.telegram.ui.Cells.ChatActionCell
            if (r1 == 0) goto L_0x00d6
            r1 = r12
            org.telegram.ui.Cells.ChatActionCell r1 = (org.telegram.ui.Cells.ChatActionCell) r1
            float r2 = r12.getY()
            org.telegram.ui.ActionBar.ActionBar r10 = r0.actionBar
            int r10 = r10.getMeasuredHeight()
            float r10 = (float) r10
            float r2 = r2 + r10
            org.telegram.ui.Components.SizeNotifierFrameLayout r10 = r0.contentView
            int r10 = r10.getBackgroundTranslationY()
            float r10 = (float) r10
            float r2 = r2 - r10
            org.telegram.ui.Components.SizeNotifierFrameLayout r10 = r0.contentView
            int r10 = r10.getBackgroundSizeY()
            r1.setVisiblePart(r2, r10)
        L_0x00ff:
            int r1 = r12.getBottom()
            org.telegram.ui.Components.RecyclerListView r2 = r0.chatListView
            int r2 = r2.getPaddingTop()
            if (r1 > r2) goto L_0x010c
            goto L_0x0151
        L_0x010c:
            int r1 = r12.getBottom()
            if (r1 >= r3) goto L_0x011d
            r3 = r1
            boolean r2 = r12 instanceof org.telegram.ui.Cells.ChatMessageCell
            if (r2 != 0) goto L_0x011b
            boolean r2 = r12 instanceof org.telegram.ui.Cells.ChatActionCell
            if (r2 == 0) goto L_0x011c
        L_0x011b:
            r7 = r12
        L_0x011c:
            r6 = r12
        L_0x011d:
            androidx.recyclerview.widget.ChatListItemAnimator r2 = r0.chatListItemAnimator
            if (r2 == 0) goto L_0x012f
            boolean r2 = r2.willRemoved(r12)
            if (r2 != 0) goto L_0x0151
            androidx.recyclerview.widget.ChatListItemAnimator r2 = r0.chatListItemAnimator
            boolean r2 = r2.willAddedFromAlpha(r12)
            if (r2 != 0) goto L_0x0151
        L_0x012f:
            boolean r2 = r12 instanceof org.telegram.ui.Cells.ChatActionCell
            if (r2 == 0) goto L_0x0151
            r2 = r12
            org.telegram.ui.Cells.ChatActionCell r2 = (org.telegram.ui.Cells.ChatActionCell) r2
            org.telegram.messenger.MessageObject r2 = r2.getMessageObject()
            boolean r2 = r2.isDateObject
            if (r2 == 0) goto L_0x0151
            float r2 = r12.getAlpha()
            r10 = 1065353216(0x3var_, float:1.0)
            int r2 = (r2 > r10 ? 1 : (r2 == r10 ? 0 : -1))
            if (r2 == 0) goto L_0x014b
            r12.setAlpha(r10)
        L_0x014b:
            if (r1 >= r4) goto L_0x0151
            r2 = r1
            r4 = r12
            r5 = r4
            r4 = r2
        L_0x0151:
            int r9 = r9 + 1
            r1 = r16
            r2 = r17
            goto L_0x001c
        L_0x0159:
            r16 = r1
            r17 = r2
            android.widget.FrameLayout r1 = r0.roundVideoContainer
            r2 = 1
            if (r1 == 0) goto L_0x0195
            if (r8 != 0) goto L_0x018e
            int r9 = org.telegram.messenger.AndroidUtilities.roundMessageSize
            int r9 = -r9
            int r9 = r9 + -100
            float r9 = (float) r9
            r1.setTranslationY(r9)
            android.view.View r1 = r0.fragmentView
            r1.invalidate()
            org.telegram.messenger.MediaController r1 = org.telegram.messenger.MediaController.getInstance()
            org.telegram.messenger.MessageObject r1 = r1.getPlayingMessageObject()
            if (r1 == 0) goto L_0x018d
            boolean r9 = r1.isRoundVideo()
            if (r9 == 0) goto L_0x018d
            boolean r9 = r0.checkTextureViewPosition
            if (r9 == 0) goto L_0x018d
            org.telegram.messenger.MediaController r9 = org.telegram.messenger.MediaController.getInstance()
            r9.setCurrentVideoVisible(r10)
        L_0x018d:
            goto L_0x0195
        L_0x018e:
            org.telegram.messenger.MediaController r1 = org.telegram.messenger.MediaController.getInstance()
            r1.setCurrentVideoVisible(r2)
        L_0x0195:
            if (r7 == 0) goto L_0x01b3
            boolean r1 = r7 instanceof org.telegram.ui.Cells.ChatMessageCell
            if (r1 == 0) goto L_0x01a3
            r1 = r7
            org.telegram.ui.Cells.ChatMessageCell r1 = (org.telegram.ui.Cells.ChatMessageCell) r1
            org.telegram.messenger.MessageObject r1 = r1.getMessageObject()
            goto L_0x01aa
        L_0x01a3:
            r1 = r7
            org.telegram.ui.Cells.ChatActionCell r1 = (org.telegram.ui.Cells.ChatActionCell) r1
            org.telegram.messenger.MessageObject r1 = r1.getMessageObject()
        L_0x01aa:
            org.telegram.ui.Cells.ChatActionCell r9 = r0.floatingDateView
            org.telegram.tgnet.TLRPC$Message r11 = r1.messageOwner
            int r11 = r11.date
            r9.setCustomDate(r11, r10, r2)
        L_0x01b3:
            r0.currentFloatingDateOnScreen = r10
            boolean r1 = r6 instanceof org.telegram.ui.Cells.ChatMessageCell
            if (r1 != 0) goto L_0x01be
            boolean r1 = r6 instanceof org.telegram.ui.Cells.ChatActionCell
            if (r1 != 0) goto L_0x01be
            r10 = 1
        L_0x01be:
            r0.currentFloatingTopIsNotMessage = r10
            r1 = 0
            if (r5 == 0) goto L_0x0255
            int r9 = r5.getTop()
            org.telegram.ui.Components.RecyclerListView r10 = r0.chatListView
            int r10 = r10.getPaddingTop()
            if (r9 > r10) goto L_0x0210
            boolean r9 = r0.currentFloatingTopIsNotMessage
            if (r9 == 0) goto L_0x01d6
            r10 = 1065353216(0x3var_, float:1.0)
            goto L_0x0212
        L_0x01d6:
            float r9 = r5.getAlpha()
            int r9 = (r9 > r1 ? 1 : (r9 == r1 ? 0 : -1))
            if (r9 == 0) goto L_0x01e1
            r5.setAlpha(r1)
        L_0x01e1:
            android.animation.AnimatorSet r9 = r0.floatingDateAnimation
            if (r9 == 0) goto L_0x01eb
            r9.cancel()
            r9 = 0
            r0.floatingDateAnimation = r9
        L_0x01eb:
            org.telegram.ui.Cells.ChatActionCell r9 = r0.floatingDateView
            java.lang.Object r9 = r9.getTag()
            if (r9 != 0) goto L_0x01fc
            org.telegram.ui.Cells.ChatActionCell r9 = r0.floatingDateView
            java.lang.Integer r10 = java.lang.Integer.valueOf(r2)
            r9.setTag(r10)
        L_0x01fc:
            org.telegram.ui.Cells.ChatActionCell r9 = r0.floatingDateView
            float r9 = r9.getAlpha()
            r10 = 1065353216(0x3var_, float:1.0)
            int r9 = (r9 > r10 ? 1 : (r9 == r10 ? 0 : -1))
            if (r9 == 0) goto L_0x020d
            org.telegram.ui.Cells.ChatActionCell r9 = r0.floatingDateView
            r9.setAlpha(r10)
        L_0x020d:
            r0.currentFloatingDateOnScreen = r2
            goto L_0x0223
        L_0x0210:
            r10 = 1065353216(0x3var_, float:1.0)
        L_0x0212:
            float r9 = r5.getAlpha()
            int r9 = (r9 > r10 ? 1 : (r9 == r10 ? 0 : -1))
            if (r9 == 0) goto L_0x021d
            r5.setAlpha(r10)
        L_0x021d:
            boolean r9 = r0.currentFloatingTopIsNotMessage
            r2 = r2 ^ r9
            r0.hideFloatingDateView(r2)
        L_0x0223:
            int r2 = r5.getBottom()
            org.telegram.ui.Components.RecyclerListView r9 = r0.chatListView
            int r9 = r9.getPaddingTop()
            int r2 = r2 - r9
            org.telegram.ui.Cells.ChatActionCell r9 = r0.floatingDateView
            int r9 = r9.getMeasuredHeight()
            if (r2 <= r9) goto L_0x024f
            org.telegram.ui.Cells.ChatActionCell r9 = r0.floatingDateView
            int r9 = r9.getMeasuredHeight()
            int r9 = r9 * 2
            if (r2 >= r9) goto L_0x024f
            org.telegram.ui.Cells.ChatActionCell r1 = r0.floatingDateView
            int r9 = r1.getMeasuredHeight()
            int r9 = -r9
            int r9 = r9 * 2
            int r9 = r9 + r2
            float r9 = (float) r9
            r1.setTranslationY(r9)
            goto L_0x0254
        L_0x024f:
            org.telegram.ui.Cells.ChatActionCell r9 = r0.floatingDateView
            r9.setTranslationY(r1)
        L_0x0254:
            goto L_0x025d
        L_0x0255:
            r0.hideFloatingDateView(r2)
            org.telegram.ui.Cells.ChatActionCell r2 = r0.floatingDateView
            r2.setTranslationY(r1)
        L_0x025d:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChannelAdminLogActivity.updateMessagesVisisblePart():void");
    }

    public void onTransitionAnimationStart(boolean isOpen, boolean backward) {
        if (isOpen) {
            this.allowAnimationIndex = getNotificationCenter().setAnimationInProgress(this.allowAnimationIndex, new int[]{NotificationCenter.chatInfoDidLoad, NotificationCenter.dialogsNeedReload, NotificationCenter.closeChats, NotificationCenter.messagesDidLoad, NotificationCenter.botKeyboardDidLoad});
            this.openAnimationEnded = false;
        }
    }

    public void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        if (isOpen) {
            getNotificationCenter().onAnimationFinish(this.allowAnimationIndex);
            this.openAnimationEnded = true;
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

    public void openVCard(TLRPC.User user, String vcard, String first_name, String last_name) {
        try {
            File f = AndroidUtilities.getSharingDirectory();
            f.mkdirs();
            File f2 = new File(f, "vcard.vcf");
            BufferedWriter writer = new BufferedWriter(new FileWriter(f2));
            writer.write(vcard);
            writer.close();
            showDialog(new PhonebookShareAlert(this, (ContactsController.Contact) null, user, (Uri) null, f2, first_name, last_name));
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public void onConfigurationChanged(Configuration newConfig) {
        if (this.visibleDialog instanceof DatePickerDialog) {
            this.visibleDialog.dismiss();
        }
    }

    /* access modifiers changed from: private */
    public void alertUserOpenError(MessageObject message) {
        if (getParentActivity() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
            builder.setTitle(LocaleController.getString("AppName", NUM));
            builder.setPositiveButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null);
            if (message.type == 3) {
                builder.setMessage(LocaleController.getString("NoPlayerInstalled", NUM));
            } else {
                builder.setMessage(LocaleController.formatString("NoHandleAppInstalled", NUM, message.getDocument().mime_type));
            }
            showDialog(builder.create());
        }
    }

    public TLRPC.Chat getCurrentChat() {
        return this.currentChat;
    }

    /* access modifiers changed from: private */
    public void addCanBanUser(Bundle bundle, long uid) {
        if (this.currentChat.megagroup && this.admins != null && ChatObject.canBlockUsers(this.currentChat)) {
            int a = 0;
            while (true) {
                if (a >= this.admins.size()) {
                    break;
                }
                TLRPC.ChannelParticipant channelParticipant = this.admins.get(a);
                if (MessageObject.getPeerId(channelParticipant.peer) != uid) {
                    a++;
                } else if (!channelParticipant.can_edit) {
                    return;
                }
            }
            bundle.putLong("ban_chat_id", this.currentChat.id);
        }
    }

    public void showOpenUrlAlert(String url, boolean ask) {
        if (Browser.isInternalUrl(url, (boolean[]) null) || !ask) {
            Browser.openUrl((Context) getParentActivity(), url, true);
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
        builder.setTitle(LocaleController.getString("OpenUrlTitle", NUM));
        builder.setMessage(LocaleController.formatString("OpenUrlAlert2", NUM, url));
        builder.setPositiveButton(LocaleController.getString("Open", NUM), new ChannelAdminLogActivity$$ExternalSyntheticLambda0(this, url));
        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
        showDialog(builder.create());
    }

    /* renamed from: lambda$showOpenUrlAlert$12$org-telegram-ui-ChannelAdminLogActivity  reason: not valid java name */
    public /* synthetic */ void m1591xd8caeae1(String url, DialogInterface dialogInterface, int i) {
        Browser.openUrl((Context) getParentActivity(), url, true);
    }

    private void removeMessageObject(MessageObject messageObject) {
        int index = this.messages.indexOf(messageObject);
        if (index != -1) {
            this.messages.remove(index);
            ChatActivityAdapter chatActivityAdapter = this.chatAdapter;
            if (chatActivityAdapter != null) {
                chatActivityAdapter.notifyItemRemoved(((chatActivityAdapter.messagesStartRow + this.messages.size()) - index) - 1);
            }
        }
    }

    public class ChatActivityAdapter extends RecyclerView.Adapter {
        private int loadingUpRow;
        /* access modifiers changed from: private */
        public Context mContext;
        /* access modifiers changed from: private */
        public int messagesEndRow;
        /* access modifiers changed from: private */
        public int messagesStartRow;
        private int rowCount;

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

        public long getItemId(int i) {
            return -1;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            if (viewType == 0) {
                if (!ChannelAdminLogActivity.this.chatMessageCellsCache.isEmpty()) {
                    view = (View) ChannelAdminLogActivity.this.chatMessageCellsCache.get(0);
                    ChannelAdminLogActivity.this.chatMessageCellsCache.remove(0);
                } else {
                    view = new ChatMessageCell(this.mContext);
                }
                ChatMessageCell chatMessageCell = (ChatMessageCell) view;
                chatMessageCell.setDelegate(new ChatMessageCell.ChatMessageCellDelegate() {
                    public /* synthetic */ boolean canDrawOutboundsContent() {
                        return ChatMessageCell.ChatMessageCellDelegate.CC.$default$canDrawOutboundsContent(this);
                    }

                    public /* synthetic */ boolean didLongPressChannelAvatar(ChatMessageCell chatMessageCell, TLRPC.Chat chat, int i, float f, float f2) {
                        return ChatMessageCell.ChatMessageCellDelegate.CC.$default$didLongPressChannelAvatar(this, chatMessageCell, chat, i, f, f2);
                    }

                    public /* synthetic */ void didPressBotButton(ChatMessageCell chatMessageCell, TLRPC.KeyboardButton keyboardButton) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressBotButton(this, chatMessageCell, keyboardButton);
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

                    public /* synthetic */ void didPressReaction(ChatMessageCell chatMessageCell, TLRPC.TL_reactionCount tL_reactionCount, boolean z) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressReaction(this, chatMessageCell, tL_reactionCount, z);
                    }

                    public /* synthetic */ void didPressTime(ChatMessageCell chatMessageCell) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressTime(this, chatMessageCell);
                    }

                    public /* synthetic */ void didPressViaBotNotInline(ChatMessageCell chatMessageCell, long j) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressViaBotNotInline(this, chatMessageCell, j);
                    }

                    public /* synthetic */ void didPressVoteButtons(ChatMessageCell chatMessageCell, ArrayList arrayList, int i, int i2, int i3) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressVoteButtons(this, chatMessageCell, arrayList, i, i2, i3);
                    }

                    public /* synthetic */ void didStartVideoStream(MessageObject messageObject) {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$didStartVideoStream(this, messageObject);
                    }

                    public /* synthetic */ String getAdminRank(long j) {
                        return ChatMessageCell.ChatMessageCellDelegate.CC.$default$getAdminRank(this, j);
                    }

                    public /* synthetic */ PinchToZoomHelper getPinchToZoomHelper() {
                        return ChatMessageCell.ChatMessageCellDelegate.CC.$default$getPinchToZoomHelper(this);
                    }

                    public /* synthetic */ TextSelectionHelper.ChatListTextSelectionHelper getTextSelectionHelper() {
                        return ChatMessageCell.ChatMessageCellDelegate.CC.$default$getTextSelectionHelper(this);
                    }

                    public /* synthetic */ boolean hasSelectedMessages() {
                        return ChatMessageCell.ChatMessageCellDelegate.CC.$default$hasSelectedMessages(this);
                    }

                    public /* synthetic */ void invalidateBlur() {
                        ChatMessageCell.ChatMessageCellDelegate.CC.$default$invalidateBlur(this);
                    }

                    public /* synthetic */ boolean isLandscape() {
                        return ChatMessageCell.ChatMessageCellDelegate.CC.$default$isLandscape(this);
                    }

                    public /* synthetic */ boolean keyboardIsOpened() {
                        return ChatMessageCell.ChatMessageCellDelegate.CC.$default$keyboardIsOpened(this);
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

                    public void didPressSideButton(ChatMessageCell cell) {
                        if (ChannelAdminLogActivity.this.getParentActivity() != null) {
                            ChannelAdminLogActivity.this.showDialog(ShareAlert.createShareAlert(ChatActivityAdapter.this.mContext, cell.getMessageObject(), (String) null, ChatObject.isChannel(ChannelAdminLogActivity.this.currentChat) && !ChannelAdminLogActivity.this.currentChat.megagroup, (String) null, false));
                        }
                    }

                    public boolean needPlayMessage(MessageObject messageObject) {
                        if (messageObject.isVoice() || messageObject.isRoundVideo()) {
                            boolean result = MediaController.getInstance().playMessage(messageObject);
                            MediaController.getInstance().setVoiceMessagesPlaylist((ArrayList<MessageObject>) null, false);
                            return result;
                        } else if (messageObject.isMusic()) {
                            return MediaController.getInstance().setPlaylist(ChannelAdminLogActivity.this.messages, messageObject, 0);
                        } else {
                            return false;
                        }
                    }

                    public void didPressChannelAvatar(ChatMessageCell cell, TLRPC.Chat chat, int postId, float touchX, float touchY) {
                        if (chat != null && chat != ChannelAdminLogActivity.this.currentChat) {
                            Bundle args = new Bundle();
                            args.putLong("chat_id", chat.id);
                            if (postId != 0) {
                                args.putInt("message_id", postId);
                            }
                            if (MessagesController.getInstance(ChannelAdminLogActivity.this.currentAccount).checkCanOpenChat(args, ChannelAdminLogActivity.this)) {
                                ChannelAdminLogActivity.this.presentFragment(new ChatActivity(args), true);
                            }
                        }
                    }

                    public void didPressOther(ChatMessageCell cell, float x, float y) {
                        boolean unused = ChannelAdminLogActivity.this.createMenu(cell);
                    }

                    public void didPressUserAvatar(ChatMessageCell cell, TLRPC.User user, float touchX, float touchY) {
                        if (user != null && user.id != UserConfig.getInstance(ChannelAdminLogActivity.this.currentAccount).getClientUserId()) {
                            openProfile(user);
                        }
                    }

                    public boolean didLongPressUserAvatar(ChatMessageCell cell, TLRPC.User user, float touchX, float touchY) {
                        AvatarPreviewer.Data data;
                        if (!(user == null || user.id == UserConfig.getInstance(ChannelAdminLogActivity.this.currentAccount).getClientUserId())) {
                            AvatarPreviewer.MenuItem[] menuItems = {AvatarPreviewer.MenuItem.OPEN_PROFILE, AvatarPreviewer.MenuItem.SEND_MESSAGE};
                            TLRPC.UserFull userFull = ChannelAdminLogActivity.this.getMessagesController().getUserFull(user.id);
                            if (userFull != null) {
                                data = AvatarPreviewer.Data.of(userFull, menuItems);
                            } else {
                                data = AvatarPreviewer.Data.of(user, ChannelAdminLogActivity.this.classGuid, menuItems);
                            }
                            if (AvatarPreviewer.canPreview(data)) {
                                AvatarPreviewer.getInstance().show((ViewGroup) ChannelAdminLogActivity.this.fragmentView, data, new ChannelAdminLogActivity$ChatActivityAdapter$1$$ExternalSyntheticLambda1(this, cell, user));
                                return true;
                            }
                        }
                        return false;
                    }

                    /* renamed from: lambda$didLongPressUserAvatar$0$org-telegram-ui-ChannelAdminLogActivity$ChatActivityAdapter$1  reason: not valid java name */
                    public /* synthetic */ void m1593xd0592d13(ChatMessageCell cell, TLRPC.User user, AvatarPreviewer.MenuItem item) {
                        switch (AnonymousClass17.$SwitchMap$org$telegram$ui$AvatarPreviewer$MenuItem[item.ordinal()]) {
                            case 1:
                                openDialog(cell, user);
                                return;
                            case 2:
                                openProfile(user);
                                return;
                            default:
                                return;
                        }
                    }

                    private void openProfile(TLRPC.User user) {
                        Bundle args = new Bundle();
                        args.putLong("user_id", user.id);
                        ChannelAdminLogActivity.this.addCanBanUser(args, user.id);
                        ProfileActivity fragment = new ProfileActivity(args);
                        fragment.setPlayProfileAnimation(0);
                        ChannelAdminLogActivity.this.presentFragment(fragment);
                    }

                    private void openDialog(ChatMessageCell cell, TLRPC.User user) {
                        if (user != null) {
                            Bundle args = new Bundle();
                            args.putLong("user_id", user.id);
                            if (ChannelAdminLogActivity.this.getMessagesController().checkCanOpenChat(args, ChannelAdminLogActivity.this)) {
                                ChannelAdminLogActivity.this.presentFragment(new ChatActivity(args));
                            }
                        }
                    }

                    public void didPressCancelSendButton(ChatMessageCell cell) {
                    }

                    public void didLongPress(ChatMessageCell cell, float x, float y) {
                        boolean unused = ChannelAdminLogActivity.this.createMenu(cell);
                    }

                    public boolean canPerformActions() {
                        return true;
                    }

                    public void didPressUrl(ChatMessageCell cell, CharacterStyle url, boolean longPress) {
                        if (url != null) {
                            MessageObject messageObject = cell.getMessageObject();
                            if (url instanceof URLSpanMono) {
                                ((URLSpanMono) url).copyToClipboard();
                                if (Build.VERSION.SDK_INT < 31) {
                                    Toast.makeText(ChannelAdminLogActivity.this.getParentActivity(), LocaleController.getString("TextCopied", NUM), 0).show();
                                }
                            } else if (url instanceof URLSpanUserMention) {
                                long peerId = Utilities.parseLong(((URLSpanUserMention) url).getURL()).longValue();
                                if (peerId > 0) {
                                    TLRPC.User user = MessagesController.getInstance(ChannelAdminLogActivity.this.currentAccount).getUser(Long.valueOf(peerId));
                                    if (user != null) {
                                        MessagesController.openChatOrProfileWith(user, (TLRPC.Chat) null, ChannelAdminLogActivity.this, 0, false);
                                        return;
                                    }
                                    return;
                                }
                                TLRPC.Chat chat = MessagesController.getInstance(ChannelAdminLogActivity.this.currentAccount).getChat(Long.valueOf(-peerId));
                                if (chat != null) {
                                    MessagesController.openChatOrProfileWith((TLRPC.User) null, chat, ChannelAdminLogActivity.this, 0, false);
                                }
                            } else if (url instanceof URLSpanNoUnderline) {
                                String str = ((URLSpanNoUnderline) url).getURL();
                                if (str.startsWith("@")) {
                                    MessagesController.getInstance(ChannelAdminLogActivity.this.currentAccount).openByUserName(str.substring(1), ChannelAdminLogActivity.this, 0);
                                } else if (str.startsWith("#")) {
                                    DialogsActivity fragment = new DialogsActivity((Bundle) null);
                                    fragment.setSearchString(str);
                                    ChannelAdminLogActivity.this.presentFragment(fragment);
                                }
                            } else {
                                String urlFinal = ((URLSpan) url).getURL();
                                if (longPress) {
                                    BottomSheet.Builder builder = new BottomSheet.Builder(ChannelAdminLogActivity.this.getParentActivity());
                                    builder.setTitle(urlFinal);
                                    builder.setItems(new CharSequence[]{LocaleController.getString("Open", NUM), LocaleController.getString("Copy", NUM)}, new ChannelAdminLogActivity$ChatActivityAdapter$1$$ExternalSyntheticLambda0(this, urlFinal));
                                    ChannelAdminLogActivity.this.showDialog(builder.create());
                                } else if (url instanceof URLSpanReplacement) {
                                    ChannelAdminLogActivity.this.showOpenUrlAlert(((URLSpanReplacement) url).getURL(), true);
                                } else {
                                    if (!(!(messageObject.messageOwner.media instanceof TLRPC.TL_messageMediaWebPage) || messageObject.messageOwner.media.webpage == null || messageObject.messageOwner.media.webpage.cached_page == null)) {
                                        String lowerUrl = urlFinal.toLowerCase();
                                        String lowerUrl2 = messageObject.messageOwner.media.webpage.url.toLowerCase();
                                        if ((Browser.isTelegraphUrl(lowerUrl, false) || lowerUrl.contains("t.me/iv")) && (lowerUrl.contains(lowerUrl2) || lowerUrl2.contains(lowerUrl))) {
                                            ArticleViewer.getInstance().setParentActivity(ChannelAdminLogActivity.this.getParentActivity(), ChannelAdminLogActivity.this);
                                            ArticleViewer.getInstance().open(messageObject);
                                            return;
                                        }
                                    }
                                    Browser.openUrl((Context) ChannelAdminLogActivity.this.getParentActivity(), urlFinal, true);
                                }
                            }
                        }
                    }

                    /* renamed from: lambda$didPressUrl$1$org-telegram-ui-ChannelAdminLogActivity$ChatActivityAdapter$1  reason: not valid java name */
                    public /* synthetic */ void m1594x8aea75b9(String urlFinal, DialogInterface dialog, int which) {
                        if (which == 0) {
                            Browser.openUrl((Context) ChannelAdminLogActivity.this.getParentActivity(), urlFinal, true);
                        } else if (which == 1) {
                            String url1 = urlFinal;
                            if (url1.startsWith("mailto:")) {
                                url1 = url1.substring(7);
                            } else if (url1.startsWith("tel:")) {
                                url1 = url1.substring(4);
                            }
                            AndroidUtilities.addToClipboard(url1);
                        }
                    }

                    public void needOpenWebView(MessageObject message, String url, String title, String description, String originalUrl, int w, int h) {
                        EmbedBottomSheet.show(ChannelAdminLogActivity.this.getParentActivity(), message, ChannelAdminLogActivity.this.provider, title, description, originalUrl, url, w, h, false);
                    }

                    public void didPressReplyMessage(ChatMessageCell cell, int id) {
                    }

                    public void didPressViaBot(ChatMessageCell cell, String username) {
                    }

                    public void didPressImage(ChatMessageCell cell, float x, float y) {
                        MessageObject message = cell.getMessageObject();
                        if (message.getInputStickerSet() != null) {
                            ChannelAdminLogActivity.this.showDialog(new StickersAlert((Context) ChannelAdminLogActivity.this.getParentActivity(), (BaseFragment) ChannelAdminLogActivity.this, message.getInputStickerSet(), (TLRPC.TL_messages_stickerSet) null, (StickersAlert.StickersAlertDelegate) null));
                        } else if (message.isVideo() || message.type == 1 || ((message.type == 0 && !message.isWebpageDocument()) || message.isGif())) {
                            PhotoViewer.getInstance().setParentActivity(ChannelAdminLogActivity.this.getParentActivity());
                            PhotoViewer.getInstance().openPhoto(message, (ChatActivity) null, 0, 0, ChannelAdminLogActivity.this.provider);
                        } else if (message.type == 3) {
                            File f = null;
                            try {
                                if (!(message.messageOwner.attachPath == null || message.messageOwner.attachPath.length() == 0)) {
                                    f = new File(message.messageOwner.attachPath);
                                }
                                if (f == null || !f.exists()) {
                                    f = FileLoader.getPathToMessage(message.messageOwner);
                                }
                                Intent intent = new Intent("android.intent.action.VIEW");
                                if (Build.VERSION.SDK_INT >= 24) {
                                    intent.setFlags(1);
                                    intent.setDataAndType(FileProvider.getUriForFile(ChannelAdminLogActivity.this.getParentActivity(), "org.telegram.messenger.beta.provider", f), "video/mp4");
                                } else {
                                    intent.setDataAndType(Uri.fromFile(f), "video/mp4");
                                }
                                ChannelAdminLogActivity.this.getParentActivity().startActivityForResult(intent, 500);
                            } catch (Exception e) {
                                ChannelAdminLogActivity.this.alertUserOpenError(message);
                            }
                        } else if (message.type == 4) {
                            if (AndroidUtilities.isGoogleMapsInstalled(ChannelAdminLogActivity.this)) {
                                LocationActivity fragment = new LocationActivity(0);
                                fragment.setMessageObject(message);
                                ChannelAdminLogActivity.this.presentFragment(fragment);
                            }
                        } else if (message.type == 9 || message.type == 0) {
                            if (message.getDocumentName().toLowerCase().endsWith("attheme")) {
                                File locFile = null;
                                if (!(message.messageOwner.attachPath == null || message.messageOwner.attachPath.length() == 0)) {
                                    File f2 = new File(message.messageOwner.attachPath);
                                    if (f2.exists()) {
                                        locFile = f2;
                                    }
                                }
                                if (locFile == null) {
                                    File f3 = FileLoader.getPathToMessage(message.messageOwner);
                                    if (f3.exists()) {
                                        locFile = f3;
                                    }
                                }
                                if (ChannelAdminLogActivity.this.chatLayoutManager != null) {
                                    if (ChannelAdminLogActivity.this.chatLayoutManager.findLastVisibleItemPosition() < ChannelAdminLogActivity.this.chatLayoutManager.getItemCount() - 1) {
                                        int unused = ChannelAdminLogActivity.this.scrollToPositionOnRecreate = ChannelAdminLogActivity.this.chatLayoutManager.findFirstVisibleItemPosition();
                                        RecyclerListView.Holder holder = (RecyclerListView.Holder) ChannelAdminLogActivity.this.chatListView.findViewHolderForAdapterPosition(ChannelAdminLogActivity.this.scrollToPositionOnRecreate);
                                        if (holder != null) {
                                            int unused2 = ChannelAdminLogActivity.this.scrollToOffsetOnRecreate = holder.itemView.getTop();
                                        } else {
                                            int unused3 = ChannelAdminLogActivity.this.scrollToPositionOnRecreate = -1;
                                        }
                                    } else {
                                        int unused4 = ChannelAdminLogActivity.this.scrollToPositionOnRecreate = -1;
                                    }
                                }
                                Theme.ThemeInfo themeInfo = Theme.applyThemeFile(locFile, message.getDocumentName(), (TLRPC.TL_theme) null, true);
                                if (themeInfo != null) {
                                    ChannelAdminLogActivity.this.presentFragment(new ThemePreviewActivity(themeInfo));
                                    return;
                                }
                                int unused5 = ChannelAdminLogActivity.this.scrollToPositionOnRecreate = -1;
                            }
                            try {
                                AndroidUtilities.openForView(message, ChannelAdminLogActivity.this.getParentActivity(), (Theme.ResourcesProvider) null);
                            } catch (Exception e2) {
                                ChannelAdminLogActivity.this.alertUserOpenError(message);
                            }
                        }
                    }

                    public void didPressInstantButton(ChatMessageCell cell, int type) {
                        MessageObject messageObject = cell.getMessageObject();
                        if (type == 0) {
                            if (messageObject.messageOwner.media != null && messageObject.messageOwner.media.webpage != null && messageObject.messageOwner.media.webpage.cached_page != null) {
                                ArticleViewer.getInstance().setParentActivity(ChannelAdminLogActivity.this.getParentActivity(), ChannelAdminLogActivity.this);
                                ArticleViewer.getInstance().open(messageObject);
                            }
                        } else if (type == 5) {
                            ChannelAdminLogActivity.this.openVCard(ChannelAdminLogActivity.this.getMessagesController().getUser(Long.valueOf(messageObject.messageOwner.media.user_id)), messageObject.messageOwner.media.vcard, messageObject.messageOwner.media.first_name, messageObject.messageOwner.media.last_name);
                        } else if (messageObject.messageOwner.media != null && messageObject.messageOwner.media.webpage != null) {
                            Browser.openUrl((Context) ChannelAdminLogActivity.this.getParentActivity(), messageObject.messageOwner.media.webpage.url);
                        }
                    }
                });
                chatMessageCell.setAllowAssistant(true);
            } else if (viewType == 1) {
                view = new ChatActionCell(this.mContext) {
                    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
                        super.onInitializeAccessibilityNodeInfo(info);
                        info.setVisibleToUser(true);
                    }
                };
                ((ChatActionCell) view).setDelegate(new ChatActionCell.ChatActionCellDelegate() {
                    public void didClickImage(ChatActionCell cell) {
                        MessageObject message = cell.getMessageObject();
                        PhotoViewer.getInstance().setParentActivity(ChannelAdminLogActivity.this.getParentActivity());
                        TLRPC.PhotoSize photoSize = FileLoader.getClosestPhotoSizeWithSize(message.photoThumbs, 640);
                        if (photoSize != null) {
                            PhotoViewer.getInstance().openPhoto(photoSize.location, ImageLocation.getForPhoto(photoSize, message.messageOwner.action.photo), ChannelAdminLogActivity.this.provider);
                            return;
                        }
                        PhotoViewer.getInstance().openPhoto(message, (ChatActivity) null, 0, 0, ChannelAdminLogActivity.this.provider);
                    }

                    public boolean didLongPress(ChatActionCell cell, float x, float y) {
                        return ChannelAdminLogActivity.this.createMenu(cell);
                    }

                    public void needOpenUserProfile(long uid) {
                        if (uid < 0) {
                            Bundle args = new Bundle();
                            args.putLong("chat_id", -uid);
                            if (MessagesController.getInstance(ChannelAdminLogActivity.this.currentAccount).checkCanOpenChat(args, ChannelAdminLogActivity.this)) {
                                ChannelAdminLogActivity.this.presentFragment(new ChatActivity(args), true);
                            }
                        } else if (uid != UserConfig.getInstance(ChannelAdminLogActivity.this.currentAccount).getClientUserId()) {
                            Bundle args2 = new Bundle();
                            args2.putLong("user_id", uid);
                            ChannelAdminLogActivity.this.addCanBanUser(args2, uid);
                            ProfileActivity fragment = new ProfileActivity(args2);
                            fragment.setPlayProfileAnimation(0);
                            ChannelAdminLogActivity.this.presentFragment(fragment);
                        }
                    }

                    public void needOpenInviteLink(TLRPC.TL_chatInviteExported invite) {
                        if (!ChannelAdminLogActivity.this.linviteLoading) {
                            Object cachedInvite = ChannelAdminLogActivity.this.invitesCache.containsKey(invite.link) ? ChannelAdminLogActivity.this.invitesCache.get(invite.link) : null;
                            if (cachedInvite == null) {
                                TLRPC.TL_messages_getExportedChatInvite req = new TLRPC.TL_messages_getExportedChatInvite();
                                req.peer = ChannelAdminLogActivity.this.getMessagesController().getInputPeer(-ChannelAdminLogActivity.this.currentChat.id);
                                req.link = invite.link;
                                boolean unused = ChannelAdminLogActivity.this.linviteLoading = true;
                                boolean[] canceled = new boolean[1];
                                AlertDialog progressDialog = new AlertDialog(ChannelAdminLogActivity.this.getParentActivity(), 3);
                                progressDialog.setOnCancelListener(new ChannelAdminLogActivity$ChatActivityAdapter$3$$ExternalSyntheticLambda0(this, canceled));
                                progressDialog.showDelayed(300);
                                ChannelAdminLogActivity.this.getConnectionsManager().bindRequestToGuid(ChannelAdminLogActivity.this.getConnectionsManager().sendRequest(req, new ChannelAdminLogActivity$ChatActivityAdapter$3$$ExternalSyntheticLambda2(this, invite, canceled, progressDialog)), ChannelAdminLogActivity.this.classGuid);
                            } else if (cachedInvite instanceof TLRPC.TL_messages_exportedChatInvite) {
                                ChannelAdminLogActivity.this.showInviteLinkBottomSheet((TLRPC.TL_messages_exportedChatInvite) cachedInvite, ChannelAdminLogActivity.this.usersMap);
                            } else {
                                BulletinFactory.of(ChannelAdminLogActivity.this).createSimpleBulletin(NUM, LocaleController.getString("LinkHashExpired", NUM)).show();
                            }
                        }
                    }

                    /* renamed from: lambda$needOpenInviteLink$0$org-telegram-ui-ChannelAdminLogActivity$ChatActivityAdapter$3  reason: not valid java name */
                    public /* synthetic */ void m1595xaf0CLASSNAMEcc(boolean[] canceled, DialogInterface dialogInterface) {
                        boolean unused = ChannelAdminLogActivity.this.linviteLoading = false;
                        canceled[0] = true;
                    }

                    /* renamed from: lambda$needOpenInviteLink$2$org-telegram-ui-ChannelAdminLogActivity$ChatActivityAdapter$3  reason: not valid java name */
                    public /* synthetic */ void m1597x962b4a4e(TLRPC.TL_chatInviteExported invite, boolean[] canceled, AlertDialog progressDialog, TLObject response, TLRPC.TL_error error) {
                        TLRPC.TL_messages_exportedChatInvite resInvite = null;
                        if (error == null) {
                            resInvite = (TLRPC.TL_messages_exportedChatInvite) response;
                            for (int i = 0; i < resInvite.users.size(); i++) {
                                TLRPC.User user = (TLRPC.User) resInvite.users.get(i);
                                if (ChannelAdminLogActivity.this.usersMap == null) {
                                    HashMap unused = ChannelAdminLogActivity.this.usersMap = new HashMap();
                                }
                                ChannelAdminLogActivity.this.usersMap.put(Long.valueOf(user.id), user);
                            }
                        }
                        AndroidUtilities.runOnUIThread(new ChannelAdminLogActivity$ChatActivityAdapter$3$$ExternalSyntheticLambda1(this, invite, resInvite, canceled, progressDialog));
                    }

                    /* renamed from: lambda$needOpenInviteLink$1$org-telegram-ui-ChannelAdminLogActivity$ChatActivityAdapter$3  reason: not valid java name */
                    public /* synthetic */ void m1596xa29bCLASSNAMEd(TLRPC.TL_chatInviteExported invite, TLRPC.TL_messages_exportedChatInvite finalInvite, boolean[] canceled, AlertDialog progressDialog) {
                        boolean unused = ChannelAdminLogActivity.this.linviteLoading = false;
                        ChannelAdminLogActivity.this.invitesCache.put(invite.link, finalInvite == null ? 0 : finalInvite);
                        if (!canceled[0]) {
                            progressDialog.dismiss();
                            if (finalInvite != null) {
                                ChannelAdminLogActivity.this.showInviteLinkBottomSheet(finalInvite, ChannelAdminLogActivity.this.usersMap);
                            } else {
                                BulletinFactory.of(ChannelAdminLogActivity.this).createSimpleBulletin(NUM, LocaleController.getString("LinkHashExpired", NUM)).show();
                            }
                        }
                    }

                    public void didPressReplyMessage(ChatActionCell cell, int id) {
                    }

                    public void didPressBotButton(MessageObject messageObject, TLRPC.KeyboardButton button) {
                    }
                });
            } else if (viewType == 2) {
                view = new ChatUnreadCell(this.mContext, (Theme.ResourcesProvider) null);
            } else {
                view = new ChatLoadingCell(this.mContext, ChannelAdminLogActivity.this.contentView, (Theme.ResourcesProvider) null);
            }
            view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(view);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            boolean pinnedBotton;
            RecyclerView.ViewHolder viewHolder = holder;
            int i = position;
            boolean z = false;
            boolean pinnedTop = true;
            if (i == this.loadingUpRow) {
                ChatLoadingCell loadingCell = (ChatLoadingCell) viewHolder.itemView;
                if (ChannelAdminLogActivity.this.loadsCount > 1) {
                    z = true;
                }
                loadingCell.setProgressVisible(z);
            } else if (i >= this.messagesStartRow && i < this.messagesEndRow) {
                MessageObject message = ChannelAdminLogActivity.this.messages.get((ChannelAdminLogActivity.this.messages.size() - (i - this.messagesStartRow)) - 1);
                View view = viewHolder.itemView;
                if (view instanceof ChatMessageCell) {
                    ChatMessageCell messageCell = (ChatMessageCell) view;
                    messageCell.isChat = true;
                    int nextType = getItemViewType(i + 1);
                    int prevType = getItemViewType(i - 1);
                    if ((message.messageOwner.reply_markup instanceof TLRPC.TL_replyInlineMarkup) || nextType != holder.getItemViewType()) {
                        pinnedBotton = false;
                    } else {
                        MessageObject nextMessage = ChannelAdminLogActivity.this.messages.get((ChannelAdminLogActivity.this.messages.size() - ((i + 1) - this.messagesStartRow)) - 1);
                        pinnedBotton = nextMessage.isOutOwner() == message.isOutOwner() && nextMessage.getFromChatId() == message.getFromChatId() && Math.abs(nextMessage.messageOwner.date - message.messageOwner.date) <= 300;
                    }
                    if (prevType == holder.getItemViewType()) {
                        MessageObject prevMessage = ChannelAdminLogActivity.this.messages.get(ChannelAdminLogActivity.this.messages.size() - (i - this.messagesStartRow));
                        if ((prevMessage.messageOwner.reply_markup instanceof TLRPC.TL_replyInlineMarkup) || prevMessage.isOutOwner() != message.isOutOwner() || prevMessage.getFromChatId() != message.getFromChatId() || Math.abs(prevMessage.messageOwner.date - message.messageOwner.date) > 300) {
                            pinnedTop = false;
                        }
                    } else {
                        pinnedTop = false;
                    }
                    messageCell.setMessageObject(message, (MessageObject.GroupedMessages) null, pinnedBotton, pinnedTop);
                    messageCell.setHighlighted(false);
                    messageCell.setHighlightedText((String) null);
                } else if (view instanceof ChatActionCell) {
                    ChatActionCell actionCell = (ChatActionCell) view;
                    actionCell.setMessageObject(message);
                    actionCell.setAlpha(1.0f);
                }
            }
        }

        public int getItemViewType(int position) {
            if (position < this.messagesStartRow || position >= this.messagesEndRow) {
                return 4;
            }
            return ChannelAdminLogActivity.this.messages.get((ChannelAdminLogActivity.this.messages.size() - (position - this.messagesStartRow)) - 1).contentType;
        }

        public void onViewAttachedToWindow(final RecyclerView.ViewHolder holder) {
            if ((holder.itemView instanceof ChatMessageCell) || (holder.itemView instanceof ChatActionCell)) {
                final View view = holder.itemView;
                holder.itemView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                    public boolean onPreDraw() {
                        int viewBottom;
                        view.getViewTreeObserver().removeOnPreDrawListener(this);
                        int height = ChannelAdminLogActivity.this.chatListView.getMeasuredHeight();
                        int top = view.getTop();
                        int bottom = view.getBottom();
                        int viewTop = top >= 0 ? 0 : -top;
                        int viewBottom2 = view.getMeasuredHeight();
                        if (viewBottom2 > height) {
                            viewBottom = viewTop + height;
                        } else {
                            viewBottom = viewBottom2;
                        }
                        if (holder.itemView instanceof ChatMessageCell) {
                            ((ChatMessageCell) view).setVisiblePart(viewTop, viewBottom - viewTop, (ChannelAdminLogActivity.this.contentView.getHeightWithKeyboard() - AndroidUtilities.dp(48.0f)) - ChannelAdminLogActivity.this.chatListView.getTop(), 0.0f, (view.getY() + ((float) ChannelAdminLogActivity.this.actionBar.getMeasuredHeight())) - ((float) ChannelAdminLogActivity.this.contentView.getBackgroundTranslationY()), ChannelAdminLogActivity.this.contentView.getMeasuredWidth(), ChannelAdminLogActivity.this.contentView.getBackgroundSizeY(), 0, 0);
                            return true;
                        } else if (!(holder.itemView instanceof ChatActionCell) || ChannelAdminLogActivity.this.actionBar == null || ChannelAdminLogActivity.this.contentView == null) {
                            return true;
                        } else {
                            View view = view;
                            ((ChatActionCell) view).setVisiblePart((view.getY() + ((float) ChannelAdminLogActivity.this.actionBar.getMeasuredHeight())) - ((float) ChannelAdminLogActivity.this.contentView.getBackgroundTranslationY()), ChannelAdminLogActivity.this.contentView.getBackgroundSizeY());
                            return true;
                        }
                    }
                });
            }
            if (holder.itemView instanceof ChatMessageCell) {
                ChatMessageCell messageCell = (ChatMessageCell) holder.itemView;
                MessageObject messageObject = messageCell.getMessageObject();
                messageCell.setBackgroundDrawable((Drawable) null);
                messageCell.setCheckPressed(true, false);
                messageCell.setHighlighted(false);
            }
        }

        public void updateRowWithMessageObject(MessageObject messageObject) {
            int index = ChannelAdminLogActivity.this.messages.indexOf(messageObject);
            if (index != -1) {
                notifyItemChanged(((this.messagesStartRow + ChannelAdminLogActivity.this.messages.size()) - index) - 1);
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

        public void notifyItemChanged(int position) {
            updateRows();
            try {
                super.notifyItemChanged(position);
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }

        public void notifyItemRangeChanged(int positionStart, int itemCount) {
            updateRows();
            try {
                super.notifyItemRangeChanged(positionStart, itemCount);
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }

        public void notifyItemInserted(int position) {
            updateRows();
            try {
                super.notifyItemInserted(position);
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }

        public void notifyItemMoved(int fromPosition, int toPosition) {
            updateRows();
            try {
                super.notifyItemMoved(fromPosition, toPosition);
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }

        public void notifyItemRangeInserted(int positionStart, int itemCount) {
            updateRows();
            try {
                super.notifyItemRangeInserted(positionStart, itemCount);
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }

        public void notifyItemRemoved(int position) {
            updateRows();
            try {
                super.notifyItemRemoved(position);
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }

        public void notifyItemRangeRemoved(int positionStart, int itemCount) {
            updateRows();
            try {
                super.notifyItemRangeRemoved(positionStart, itemCount);
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    /* renamed from: org.telegram.ui.ChannelAdminLogActivity$17  reason: invalid class name */
    static /* synthetic */ class AnonymousClass17 {
        static final /* synthetic */ int[] $SwitchMap$org$telegram$ui$AvatarPreviewer$MenuItem;

        static {
            int[] iArr = new int[AvatarPreviewer.MenuItem.values().length];
            $SwitchMap$org$telegram$ui$AvatarPreviewer$MenuItem = iArr;
            try {
                iArr[AvatarPreviewer.MenuItem.SEND_MESSAGE.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$telegram$ui$AvatarPreviewer$MenuItem[AvatarPreviewer.MenuItem.OPEN_PROFILE.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
        }
    }

    /* access modifiers changed from: private */
    public void showInviteLinkBottomSheet(TLRPC.TL_messages_exportedChatInvite invite, HashMap<Long, TLRPC.User> usersMap2) {
        TLRPC.ChatFull chatInfo = getMessagesController().getChatFull(this.currentChat.id);
        InviteLinkBottomSheet inviteLinkBottomSheet = new InviteLinkBottomSheet(this.contentView.getContext(), (TLRPC.TL_chatInviteExported) invite.invite, chatInfo, usersMap2, this, chatInfo.id, false, ChatObject.isChannel(this.currentChat));
        inviteLinkBottomSheet.setInviteDelegate(new InviteLinkBottomSheet.InviteDelegate() {
            public void permanentLinkReplaced(TLRPC.TL_chatInviteExported oldLink, TLRPC.TL_chatInviteExported newLink) {
            }

            public void linkRevoked(TLRPC.TL_chatInviteExported invite) {
                TLRPC.TL_channelAdminLogEvent event = new TLRPC.TL_channelAdminLogEvent();
                int size = ChannelAdminLogActivity.this.messages.size();
                invite.revoked = true;
                TLRPC.TL_channelAdminLogEventActionExportedInviteRevoke revokeAction = new TLRPC.TL_channelAdminLogEventActionExportedInviteRevoke();
                revokeAction.invite = invite;
                event.action = revokeAction;
                event.date = (int) (System.currentTimeMillis() / 1000);
                event.user_id = ChannelAdminLogActivity.this.getAccountInstance().getUserConfig().clientUserId;
                if (new MessageObject(ChannelAdminLogActivity.this.currentAccount, event, ChannelAdminLogActivity.this.messages, (HashMap<String, ArrayList<MessageObject>>) ChannelAdminLogActivity.this.messagesByDays, ChannelAdminLogActivity.this.currentChat, ChannelAdminLogActivity.this.mid, true).contentType >= 0) {
                    int addCount = ChannelAdminLogActivity.this.messages.size() - size;
                    if (addCount > 0) {
                        ChannelAdminLogActivity.this.chatListItemAnimator.setShouldAnimateEnterFromBottom(true);
                        ChannelAdminLogActivity.this.chatAdapter.notifyItemRangeInserted(ChannelAdminLogActivity.this.chatAdapter.messagesEndRow, addCount);
                        ChannelAdminLogActivity.this.moveScrollToLastMessage();
                    }
                    ChannelAdminLogActivity.this.invitesCache.remove(invite.link);
                }
            }

            public void onLinkDeleted(TLRPC.TL_chatInviteExported invite) {
                int size = ChannelAdminLogActivity.this.messages.size();
                int access$7400 = ChannelAdminLogActivity.this.chatAdapter.messagesEndRow;
                TLRPC.TL_channelAdminLogEvent event = new TLRPC.TL_channelAdminLogEvent();
                TLRPC.TL_channelAdminLogEventActionExportedInviteDelete deleteAction = new TLRPC.TL_channelAdminLogEventActionExportedInviteDelete();
                deleteAction.invite = invite;
                event.action = deleteAction;
                event.date = (int) (System.currentTimeMillis() / 1000);
                event.user_id = ChannelAdminLogActivity.this.getAccountInstance().getUserConfig().clientUserId;
                if (new MessageObject(ChannelAdminLogActivity.this.currentAccount, event, ChannelAdminLogActivity.this.messages, (HashMap<String, ArrayList<MessageObject>>) ChannelAdminLogActivity.this.messagesByDays, ChannelAdminLogActivity.this.currentChat, ChannelAdminLogActivity.this.mid, true).contentType >= 0) {
                    int addCount = ChannelAdminLogActivity.this.messages.size() - size;
                    if (addCount > 0) {
                        ChannelAdminLogActivity.this.chatListItemAnimator.setShouldAnimateEnterFromBottom(true);
                        ChannelAdminLogActivity.this.chatAdapter.notifyItemRangeInserted(ChannelAdminLogActivity.this.chatAdapter.messagesEndRow, addCount);
                        ChannelAdminLogActivity.this.moveScrollToLastMessage();
                    }
                    ChannelAdminLogActivity.this.invitesCache.remove(invite.link);
                }
            }

            public void onLinkEdited(TLRPC.TL_chatInviteExported invite) {
                TLRPC.TL_channelAdminLogEvent event = new TLRPC.TL_channelAdminLogEvent();
                TLRPC.TL_channelAdminLogEventActionExportedInviteEdit editAction = new TLRPC.TL_channelAdminLogEventActionExportedInviteEdit();
                editAction.new_invite = invite;
                editAction.prev_invite = invite;
                event.action = editAction;
                event.date = (int) (System.currentTimeMillis() / 1000);
                event.user_id = ChannelAdminLogActivity.this.getAccountInstance().getUserConfig().clientUserId;
                if (new MessageObject(ChannelAdminLogActivity.this.currentAccount, event, ChannelAdminLogActivity.this.messages, (HashMap<String, ArrayList<MessageObject>>) ChannelAdminLogActivity.this.messagesByDays, ChannelAdminLogActivity.this.currentChat, ChannelAdminLogActivity.this.mid, true).contentType >= 0) {
                    ChannelAdminLogActivity.this.chatAdapter.notifyDataSetChanged();
                    ChannelAdminLogActivity.this.moveScrollToLastMessage();
                }
            }
        });
        inviteLinkBottomSheet.show();
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();
        themeDescriptions.add(new ThemeDescription(this.fragmentView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_wallpaper"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultIcon"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSelector"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUBACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSubmenuBackground"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSubmenuItem"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM | ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSubmenuItemIcon"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultIcon"));
        themeDescriptions.add(new ThemeDescription(this.avatarContainer.getTitleTextView(), ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultTitle"));
        themeDescriptions.add(new ThemeDescription((View) this.avatarContainer.getSubtitleTextView(), ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, new Paint[]{Theme.chat_statusPaint, Theme.chat_statusRecordPaint}, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSubtitle", (Object) null));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSelector"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, Theme.avatarDrawables, (ThemeDescription.ThemeDescriptionDelegate) null, "avatar_text"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "avatar_backgroundRed"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "avatar_backgroundOrange"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "avatar_backgroundViolet"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "avatar_backgroundGreen"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "avatar_backgroundCyan"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "avatar_backgroundBlue"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "avatar_backgroundPink"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "avatar_nameInMessageRed"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "avatar_nameInMessageOrange"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "avatar_nameInMessageViolet"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "avatar_nameInMessageGreen"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "avatar_nameInMessageCyan"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "avatar_nameInMessageBlue"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "avatar_nameInMessagePink"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgInDrawable, Theme.chat_msgInMediaDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inBubble"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgInSelectedDrawable, Theme.chat_msgInMediaSelectedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inBubbleSelected"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, Theme.chat_msgInDrawable.getShadowDrawables(), (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inBubbleShadow"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, Theme.chat_msgInMediaDrawable.getShadowDrawables(), (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inBubbleShadow"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, Theme.chat_msgOutDrawable.getShadowDrawables(), (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outBubbleShadow"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, Theme.chat_msgOutMediaDrawable.getShadowDrawables(), (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outBubbleShadow"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgOutDrawable, Theme.chat_msgOutMediaDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outBubble"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgOutDrawable, Theme.chat_msgOutMediaDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outBubbleGradient"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgOutDrawable, Theme.chat_msgOutMediaDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outBubbleGradient2"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgOutDrawable, Theme.chat_msgOutMediaDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outBubbleGradient3"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgOutSelectedDrawable, Theme.chat_msgOutMediaSelectedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outBubbleSelected"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{ChatActionCell.class}, Theme.chat_actionTextPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_serviceText"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, ThemeDescription.FLAG_LINKCOLOR, new Class[]{ChatActionCell.class}, Theme.chat_actionTextPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_serviceLink"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_botCardDrawable, Theme.chat_shareIconDrawable, Theme.chat_botInlineDrawable, Theme.chat_botLinkDrawable, Theme.chat_goIconDrawable, Theme.chat_commentStickerDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_serviceIcon"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class, ChatActionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_serviceBackground"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class, ChatActionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_serviceBackgroundSelected"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_messageTextIn"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_messageTextOut"));
        themeDescriptions.add(new ThemeDescription((View) this.chatListView, ThemeDescription.FLAG_LINKCOLOR, new Class[]{ChatMessageCell.class}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_messageLinkIn", (Object) null));
        themeDescriptions.add(new ThemeDescription((View) this.chatListView, ThemeDescription.FLAG_LINKCOLOR, new Class[]{ChatMessageCell.class}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_messageLinkOut", (Object) null));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgOutCheckDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outSentCheck"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgOutCheckSelectedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outSentCheckSelected"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgOutCheckReadDrawable, Theme.chat_msgOutHalfCheckDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outSentCheckRead"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgOutCheckReadSelectedDrawable, Theme.chat_msgOutHalfCheckSelectedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outSentCheckReadSelected"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgMediaCheckDrawable, Theme.chat_msgMediaHalfCheckDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_mediaSentCheck"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgOutViewsDrawable, Theme.chat_msgOutRepliesDrawable, Theme.chat_msgOutPinnedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outViews"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgOutViewsSelectedDrawable, Theme.chat_msgOutRepliesSelectedDrawable, Theme.chat_msgOutPinnedSelectedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outViewsSelected"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgInViewsDrawable, Theme.chat_msgInRepliesDrawable, Theme.chat_msgInPinnedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inViews"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgInViewsSelectedDrawable, Theme.chat_msgInRepliesSelectedDrawable, Theme.chat_msgInPinnedSelectedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inViewsSelected"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgMediaViewsDrawable, Theme.chat_msgMediaRepliesDrawable, Theme.chat_msgMediaPinnedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_mediaViews"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgOutMenuDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outMenu"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgOutMenuSelectedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outMenuSelected"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgInMenuDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inMenu"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgInMenuSelectedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inMenuSelected"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgMediaMenuDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_mediaMenu"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgOutInstantDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outInstant"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgInInstantDrawable, Theme.chat_commentDrawable, Theme.chat_commentArrowDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inInstant"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, Theme.chat_msgOutCallDrawable, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outInstant"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, Theme.chat_msgOutCallSelectedDrawable, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outInstantSelected"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, Theme.chat_msgInCallDrawable, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inInstant"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, Theme.chat_msgInCallSelectedDrawable, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inInstantSelected"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgCallUpGreenDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outUpCall"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgCallDownRedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inUpCall"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgCallDownGreenDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inDownCall"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, Theme.chat_msgErrorPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_sentError"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgErrorDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_sentErrorIcon"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, Theme.chat_durationPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_previewDurationText"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, Theme.chat_gamePaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_previewGameText"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inPreviewInstantText"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outPreviewInstantText"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inPreviewInstantSelectedText"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outPreviewInstantSelectedText"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, Theme.chat_deleteProgressPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_secretTimeText"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_stickerNameText"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, Theme.chat_botButtonPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_botButtonText"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, Theme.chat_botProgressPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_botProgress"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inForwardedNameText"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outForwardedNameText"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inViaBotNameText"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outViaBotNameText"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_stickerViaBotNameText"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inReplyLine"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outReplyLine"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_stickerReplyLine"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inReplyNameText"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outReplyNameText"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_stickerReplyNameText"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inReplyMessageText"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outReplyMessageText"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inReplyMediaMessageText"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outReplyMediaMessageText"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inReplyMediaMessageSelectedText"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outReplyMediaMessageSelectedText"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_stickerReplyMessageText"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inPreviewLine"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outPreviewLine"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inSiteNameText"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outSiteNameText"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inContactNameText"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outContactNameText"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inContactPhoneText"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outContactPhoneText"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_mediaProgress"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inAudioProgress"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outAudioProgress"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inAudioSelectedProgress"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outAudioSelectedProgress"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_mediaTimeText"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inTimeText"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outTimeText"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inTimeSelectedText"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outTimeSelectedText"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inAudioPerfomerText"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outAudioPerfomerText"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inAudioTitleText"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outAudioTitleText"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inAudioDurationText"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outAudioDurationText"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inAudioDurationSelectedText"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outAudioDurationSelectedText"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inAudioSeekbar"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outAudioSeekbar"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inAudioSeekbarSelected"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outAudioSeekbarSelected"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inAudioSeekbarFill"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inAudioCacheSeekbar"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outAudioSeekbarFill"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outAudioCacheSeekbar"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inVoiceSeekbar"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outVoiceSeekbar"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inVoiceSeekbarSelected"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outVoiceSeekbarSelected"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inVoiceSeekbarFill"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outVoiceSeekbarFill"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inFileProgress"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outFileProgress"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inFileProgressSelected"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outFileProgressSelected"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inFileNameText"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outFileNameText"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inFileInfoText"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outFileInfoText"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inFileInfoSelectedText"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outFileInfoSelectedText"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inFileBackground"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outFileBackground"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inFileBackgroundSelected"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outFileBackgroundSelected"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inVenueInfoText"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outVenueInfoText"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inVenueInfoSelectedText"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outVenueInfoSelectedText"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_mediaInfoText"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, Theme.chat_urlPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_linkSelectBackground"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, Theme.chat_textSearchSelectionPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_textSelectBackground"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outLoader"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outMediaIcon"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outLoaderSelected"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outMediaIconSelected"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inLoader"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inMediaIcon"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inLoaderSelected"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inMediaIconSelected"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_photoStatesDrawables[0][0], Theme.chat_photoStatesDrawables[1][0], Theme.chat_photoStatesDrawables[2][0], Theme.chat_photoStatesDrawables[3][0]}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_mediaLoaderPhoto"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_photoStatesDrawables[0][0], Theme.chat_photoStatesDrawables[1][0], Theme.chat_photoStatesDrawables[2][0], Theme.chat_photoStatesDrawables[3][0]}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_mediaLoaderPhotoIcon"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_photoStatesDrawables[0][1], Theme.chat_photoStatesDrawables[1][1], Theme.chat_photoStatesDrawables[2][1], Theme.chat_photoStatesDrawables[3][1]}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_mediaLoaderPhotoSelected"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_photoStatesDrawables[0][1], Theme.chat_photoStatesDrawables[1][1], Theme.chat_photoStatesDrawables[2][1], Theme.chat_photoStatesDrawables[3][1]}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_mediaLoaderPhotoIconSelected"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_photoStatesDrawables[7][0], Theme.chat_photoStatesDrawables[8][0]}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outLoaderPhoto"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_photoStatesDrawables[7][0], Theme.chat_photoStatesDrawables[8][0]}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outLoaderPhotoIcon"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_photoStatesDrawables[7][1], Theme.chat_photoStatesDrawables[8][1]}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outLoaderPhotoSelected"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_photoStatesDrawables[7][1], Theme.chat_photoStatesDrawables[8][1]}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outLoaderPhotoIconSelected"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_photoStatesDrawables[10][0], Theme.chat_photoStatesDrawables[11][0]}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inLoaderPhoto"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_photoStatesDrawables[10][0], Theme.chat_photoStatesDrawables[11][0]}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inLoaderPhotoIcon"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_photoStatesDrawables[10][1], Theme.chat_photoStatesDrawables[11][1]}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inLoaderPhotoSelected"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_photoStatesDrawables[10][1], Theme.chat_photoStatesDrawables[11][1]}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inLoaderPhotoIconSelected"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_photoStatesDrawables[9][0]}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outFileIcon"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_photoStatesDrawables[9][1]}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outFileSelectedIcon"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_photoStatesDrawables[12][0]}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inFileIcon"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_photoStatesDrawables[12][1]}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inFileSelectedIcon"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_contactDrawable[0]}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inContactBackground"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_contactDrawable[0]}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inContactIcon"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_contactDrawable[1]}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outContactBackground"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_contactDrawable[1]}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outContactIcon"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inLocationBackground"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_locationDrawable[0]}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inLocationIcon"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outLocationBackground"));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, (Paint) null, new Drawable[]{Theme.chat_locationDrawable[1]}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outLocationIcon"));
        themeDescriptions.add(new ThemeDescription(this.bottomOverlayChat, 0, (Class[]) null, Theme.chat_composeBackgroundPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_messagePanelBackground"));
        themeDescriptions.add(new ThemeDescription(this.bottomOverlayChat, 0, (Class[]) null, (Paint) null, new Drawable[]{Theme.chat_composeShadowDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_messagePanelShadow"));
        themeDescriptions.add(new ThemeDescription(this.bottomOverlayChatText, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_fieldOverlayText"));
        themeDescriptions.add(new ThemeDescription(this.emptyView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_serviceText"));
        themeDescriptions.add(new ThemeDescription(this.progressBar, ThemeDescription.FLAG_PROGRESSBAR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_serviceText"));
        themeDescriptions.add(new ThemeDescription((View) this.chatListView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE, new Class[]{ChatUnreadCell.class}, new String[]{"backgroundLayout"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_unreadMessagesStartBackground"));
        themeDescriptions.add(new ThemeDescription((View) this.chatListView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{ChatUnreadCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_unreadMessagesStartArrowIcon"));
        themeDescriptions.add(new ThemeDescription((View) this.chatListView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{ChatUnreadCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_unreadMessagesStartText"));
        themeDescriptions.add(new ThemeDescription(this.progressView2, ThemeDescription.FLAG_SERVICEBACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_serviceBackground"));
        themeDescriptions.add(new ThemeDescription(this.emptyView, ThemeDescription.FLAG_SERVICEBACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_serviceBackground"));
        themeDescriptions.add(new ThemeDescription((View) this.chatListView, ThemeDescription.FLAG_SERVICEBACKGROUND, new Class[]{ChatLoadingCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_serviceBackground"));
        themeDescriptions.add(new ThemeDescription((View) this.chatListView, ThemeDescription.FLAG_PROGRESSBAR, new Class[]{ChatLoadingCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_serviceText"));
        ChatAvatarContainer chatAvatarContainer = this.avatarContainer;
        ImageView imageView = null;
        themeDescriptions.add(new ThemeDescription(chatAvatarContainer != null ? chatAvatarContainer.getTimeItem() : null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_secretTimerBackground"));
        ChatAvatarContainer chatAvatarContainer2 = this.avatarContainer;
        if (chatAvatarContainer2 != null) {
            imageView = chatAvatarContainer2.getTimeItem();
        }
        themeDescriptions.add(new ThemeDescription(imageView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_secretTimerText"));
        themeDescriptions.add(new ThemeDescription(this.undoView, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_background"));
        themeDescriptions.add(new ThemeDescription((View) this.undoView, 0, new Class[]{UndoView.class}, new String[]{"undoImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_cancelColor"));
        themeDescriptions.add(new ThemeDescription((View) this.undoView, 0, new Class[]{UndoView.class}, new String[]{"undoTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_cancelColor"));
        themeDescriptions.add(new ThemeDescription((View) this.undoView, 0, new Class[]{UndoView.class}, new String[]{"infoTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_infoColor"));
        themeDescriptions.add(new ThemeDescription((View) this.undoView, 0, new Class[]{UndoView.class}, new String[]{"textPaint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_infoColor"));
        themeDescriptions.add(new ThemeDescription((View) this.undoView, 0, new Class[]{UndoView.class}, new String[]{"progressPaint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_infoColor"));
        themeDescriptions.add(new ThemeDescription((View) this.undoView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{UndoView.class}, new String[]{"leftImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_infoColor"));
        return themeDescriptions;
    }
}
