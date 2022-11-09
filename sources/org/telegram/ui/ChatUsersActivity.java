package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.FrameLayout;
import androidx.collection.LongSparseArray;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$ChannelParticipant;
import org.telegram.tgnet.TLRPC$ChannelParticipantsFilter;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$ChatFull;
import org.telegram.tgnet.TLRPC$ChatParticipant;
import org.telegram.tgnet.TLRPC$TL_channelFull;
import org.telegram.tgnet.TLRPC$TL_channelParticipant;
import org.telegram.tgnet.TLRPC$TL_channelParticipantAdmin;
import org.telegram.tgnet.TLRPC$TL_channelParticipantCreator;
import org.telegram.tgnet.TLRPC$TL_channelParticipantSelf;
import org.telegram.tgnet.TLRPC$TL_channelParticipantsAdmins;
import org.telegram.tgnet.TLRPC$TL_channelParticipantsBanned;
import org.telegram.tgnet.TLRPC$TL_channelParticipantsBots;
import org.telegram.tgnet.TLRPC$TL_channelParticipantsContacts;
import org.telegram.tgnet.TLRPC$TL_channelParticipantsKicked;
import org.telegram.tgnet.TLRPC$TL_channelParticipantsRecent;
import org.telegram.tgnet.TLRPC$TL_channels_channelParticipants;
import org.telegram.tgnet.TLRPC$TL_channels_editBanned;
import org.telegram.tgnet.TLRPC$TL_channels_getParticipants;
import org.telegram.tgnet.TLRPC$TL_chatAdminRights;
import org.telegram.tgnet.TLRPC$TL_chatBannedRights;
import org.telegram.tgnet.TLRPC$TL_chatParticipant;
import org.telegram.tgnet.TLRPC$TL_chatParticipantAdmin;
import org.telegram.tgnet.TLRPC$TL_chatParticipantCreator;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_groupCallParticipant;
import org.telegram.tgnet.TLRPC$TL_peerChannel;
import org.telegram.tgnet.TLRPC$TL_peerUser;
import org.telegram.tgnet.TLRPC$Updates;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.tgnet.TLRPC$UserStatus;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Adapters.SearchAdapterHelper;
import org.telegram.ui.Cells.GraySectionCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.LoadingCell;
import org.telegram.ui.Cells.ManageChatTextCell;
import org.telegram.ui.Cells.ManageChatUserCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCheckCell2;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.ChatRightsEditActivity;
import org.telegram.ui.ChatUsersActivity;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.FlickerLoadingView;
import org.telegram.ui.Components.GigagroupConvertAlert;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RadialProgressView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.SlideChooseView;
import org.telegram.ui.Components.StickerEmptyView;
import org.telegram.ui.Components.UndoView;
import org.telegram.ui.GroupCreateActivity;
/* loaded from: classes3.dex */
public class ChatUsersActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private int addNew2Row;
    private int addNewRow;
    private int addNewSectionRow;
    private int addUsersRow;
    private int blockedEmptyRow;
    private int botEndRow;
    private int botHeaderRow;
    private int botStartRow;
    private ArrayList<TLObject> bots;
    private boolean botsEndReached;
    private LongSparseArray<TLObject> botsMap;
    private int changeInfoRow;
    private long chatId;
    private ArrayList<TLObject> contacts;
    private boolean contactsEndReached;
    private int contactsEndRow;
    private int contactsHeaderRow;
    private LongSparseArray<TLObject> contactsMap;
    private int contactsStartRow;
    private TLRPC$Chat currentChat;
    private TLRPC$TL_chatBannedRights defaultBannedRights;
    private int delayResults;
    private ChatUsersActivityDelegate delegate;
    private ActionBarMenuItem doneItem;
    private int embedLinksRow;
    private StickerEmptyView emptyView;
    private boolean firstLoaded;
    private FlickerLoadingView flickerLoadingView;
    private int gigaConvertRow;
    private int gigaHeaderRow;
    private int gigaInfoRow;
    private LongSparseArray<TLRPC$TL_groupCallParticipant> ignoredUsers;
    private TLRPC$ChatFull info;
    private String initialBannedRights;
    private int initialSlowmode;
    private boolean isChannel;
    private boolean isForum;
    private LinearLayoutManager layoutManager;
    private RecyclerListView listView;
    private ListAdapter listViewAdapter;
    private int loadingHeaderRow;
    private int loadingProgressRow;
    private int loadingUserCellRow;
    private boolean loadingUsers;
    private int manageTopicsRow;
    private int membersHeaderRow;
    private boolean needOpenSearch;
    private boolean openTransitionStarted;
    private ArrayList<TLObject> participants;
    private int participantsDivider2Row;
    private int participantsDividerRow;
    private int participantsEndRow;
    private int participantsInfoRow;
    private LongSparseArray<TLObject> participantsMap;
    private int participantsStartRow;
    private int permissionsSectionRow;
    private int pinMessagesRow;
    private View progressBar;
    private int recentActionsRow;
    private int removedUsersRow;
    private int restricted1SectionRow;
    private int rowCount;
    private ActionBarMenuItem searchItem;
    private SearchAdapter searchListViewAdapter;
    private boolean searching;
    private int selectType;
    private int selectedSlowmode;
    private int sendMediaRow;
    private int sendMessagesRow;
    private int sendPollsRow;
    private int sendStickersRow;
    private int slowmodeInfoRow;
    private int slowmodeRow;
    private int slowmodeSelectRow;
    private int type;
    private UndoView undoView;

    /* loaded from: classes3.dex */
    public interface ChatUsersActivityDelegate {

        /* renamed from: org.telegram.ui.ChatUsersActivity$ChatUsersActivityDelegate$-CC  reason: invalid class name */
        /* loaded from: classes3.dex */
        public final /* synthetic */ class CC {
            public static void $default$didChangeOwner(ChatUsersActivityDelegate chatUsersActivityDelegate, TLRPC$User tLRPC$User) {
            }

            public static void $default$didKickParticipant(ChatUsersActivityDelegate chatUsersActivityDelegate, long j) {
            }

            public static void $default$didSelectUser(ChatUsersActivityDelegate chatUsersActivityDelegate, long j) {
            }
        }

        void didAddParticipantToList(long j, TLObject tLObject);

        void didChangeOwner(TLRPC$User tLRPC$User);

        void didKickParticipant(long j);

        void didSelectUser(long j);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getSecondsForIndex(int i) {
        if (i == 1) {
            return 10;
        }
        if (i == 2) {
            return 30;
        }
        if (i == 3) {
            return 60;
        }
        if (i == 4) {
            return 300;
        }
        if (i == 5) {
            return 900;
        }
        return i == 6 ? 3600 : 0;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean needDelayOpenAnimation() {
        return true;
    }

    public ChatUsersActivity(Bundle bundle) {
        super(bundle);
        TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights;
        this.defaultBannedRights = new TLRPC$TL_chatBannedRights();
        this.participants = new ArrayList<>();
        this.bots = new ArrayList<>();
        this.contacts = new ArrayList<>();
        this.participantsMap = new LongSparseArray<>();
        this.botsMap = new LongSparseArray<>();
        this.contactsMap = new LongSparseArray<>();
        this.chatId = this.arguments.getLong("chat_id");
        this.type = this.arguments.getInt("type");
        this.needOpenSearch = this.arguments.getBoolean("open_search");
        this.selectType = this.arguments.getInt("selectType");
        TLRPC$Chat chat = getMessagesController().getChat(Long.valueOf(this.chatId));
        this.currentChat = chat;
        if (chat != null && (tLRPC$TL_chatBannedRights = chat.default_banned_rights) != null) {
            TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights2 = this.defaultBannedRights;
            tLRPC$TL_chatBannedRights2.view_messages = tLRPC$TL_chatBannedRights.view_messages;
            tLRPC$TL_chatBannedRights2.send_stickers = tLRPC$TL_chatBannedRights.send_stickers;
            tLRPC$TL_chatBannedRights2.send_media = tLRPC$TL_chatBannedRights.send_media;
            tLRPC$TL_chatBannedRights2.embed_links = tLRPC$TL_chatBannedRights.embed_links;
            tLRPC$TL_chatBannedRights2.send_messages = tLRPC$TL_chatBannedRights.send_messages;
            tLRPC$TL_chatBannedRights2.send_games = tLRPC$TL_chatBannedRights.send_games;
            tLRPC$TL_chatBannedRights2.send_inline = tLRPC$TL_chatBannedRights.send_inline;
            tLRPC$TL_chatBannedRights2.send_gifs = tLRPC$TL_chatBannedRights.send_gifs;
            tLRPC$TL_chatBannedRights2.pin_messages = tLRPC$TL_chatBannedRights.pin_messages;
            tLRPC$TL_chatBannedRights2.send_polls = tLRPC$TL_chatBannedRights.send_polls;
            tLRPC$TL_chatBannedRights2.invite_users = tLRPC$TL_chatBannedRights.invite_users;
            tLRPC$TL_chatBannedRights2.manage_topics = tLRPC$TL_chatBannedRights.manage_topics;
            tLRPC$TL_chatBannedRights2.change_info = tLRPC$TL_chatBannedRights.change_info;
        }
        this.initialBannedRights = ChatObject.getBannedRightsString(this.defaultBannedRights);
        this.isChannel = ChatObject.isChannel(this.currentChat) && !this.currentChat.megagroup;
        this.isForum = ChatObject.isForum(this.currentChat);
    }

    /* JADX WARN: Code restructure failed: missing block: B:33:0x0109, code lost:
        if (org.telegram.messenger.ChatObject.canBlockUsers(r0) != false) goto L28;
     */
    /* JADX WARN: Removed duplicated region for block: B:40:0x0133  */
    /* JADX WARN: Removed duplicated region for block: B:53:0x0163  */
    /* JADX WARN: Removed duplicated region for block: B:60:0x017d  */
    /* JADX WARN: Removed duplicated region for block: B:70:0x019d  */
    /* JADX WARN: Removed duplicated region for block: B:73:0x01b0  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void updateRows() {
        /*
            Method dump skipped, instructions count: 886
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChatUsersActivity.updateRows():void");
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        getNotificationCenter().addObserver(this, NotificationCenter.chatInfoDidLoad);
        loadChatParticipants(0, 200);
        return true;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        getNotificationCenter().removeObserver(this, NotificationCenter.chatInfoDidLoad);
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public View createView(final Context context) {
        int i;
        this.searching = false;
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        int i2 = 1;
        this.actionBar.setAllowOverlayTitle(true);
        int i3 = this.type;
        if (i3 == 3) {
            this.actionBar.setTitle(LocaleController.getString("ChannelPermissions", R.string.ChannelPermissions));
        } else if (i3 == 0) {
            this.actionBar.setTitle(LocaleController.getString("ChannelBlacklist", R.string.ChannelBlacklist));
        } else if (i3 == 1) {
            this.actionBar.setTitle(LocaleController.getString("ChannelAdministrators", R.string.ChannelAdministrators));
        } else if (i3 == 2) {
            int i4 = this.selectType;
            if (i4 == 0) {
                if (this.isChannel) {
                    this.actionBar.setTitle(LocaleController.getString("ChannelSubscribers", R.string.ChannelSubscribers));
                } else {
                    this.actionBar.setTitle(LocaleController.getString("ChannelMembers", R.string.ChannelMembers));
                }
            } else if (i4 == 1) {
                this.actionBar.setTitle(LocaleController.getString("ChannelAddAdmin", R.string.ChannelAddAdmin));
            } else if (i4 == 2) {
                this.actionBar.setTitle(LocaleController.getString("ChannelBlockUser", R.string.ChannelBlockUser));
            } else if (i4 == 3) {
                this.actionBar.setTitle(LocaleController.getString("ChannelAddException", R.string.ChannelAddException));
            }
        }
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() { // from class: org.telegram.ui.ChatUsersActivity.1
            @Override // org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick
            public void onItemClick(int i5) {
                if (i5 == -1) {
                    if (!ChatUsersActivity.this.checkDiscard()) {
                        return;
                    }
                    ChatUsersActivity.this.finishFragment();
                } else if (i5 != 1) {
                } else {
                    ChatUsersActivity.this.processDone();
                }
            }
        });
        if (this.selectType != 0 || (i = this.type) == 2 || i == 0 || i == 3) {
            this.searchListViewAdapter = new SearchAdapter(context);
            ActionBarMenu createMenu = this.actionBar.createMenu();
            ActionBarMenuItem actionBarMenuItemSearchListener = createMenu.addItem(0, R.drawable.ic_ab_search).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener() { // from class: org.telegram.ui.ChatUsersActivity.2
                @Override // org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener
                public void onSearchExpand() {
                    ChatUsersActivity.this.searching = true;
                    if (ChatUsersActivity.this.doneItem != null) {
                        ChatUsersActivity.this.doneItem.setVisibility(8);
                    }
                }

                @Override // org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener
                public void onSearchCollapse() {
                    ChatUsersActivity.this.searchListViewAdapter.searchUsers(null);
                    ChatUsersActivity.this.searching = false;
                    ChatUsersActivity.this.listView.setAnimateEmptyView(false, 0);
                    ChatUsersActivity.this.listView.setAdapter(ChatUsersActivity.this.listViewAdapter);
                    ChatUsersActivity.this.listViewAdapter.notifyDataSetChanged();
                    ChatUsersActivity.this.listView.setFastScrollVisible(true);
                    ChatUsersActivity.this.listView.setVerticalScrollBarEnabled(false);
                    if (ChatUsersActivity.this.doneItem != null) {
                        ChatUsersActivity.this.doneItem.setVisibility(0);
                    }
                }

                @Override // org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener
                public void onTextChanged(EditText editText) {
                    if (ChatUsersActivity.this.searchListViewAdapter == null) {
                        return;
                    }
                    String obj = editText.getText().toString();
                    int itemCount = ChatUsersActivity.this.listView.getAdapter() == null ? 0 : ChatUsersActivity.this.listView.getAdapter().getItemCount();
                    ChatUsersActivity.this.searchListViewAdapter.searchUsers(obj);
                    if (TextUtils.isEmpty(obj) && ChatUsersActivity.this.listView != null && ChatUsersActivity.this.listView.getAdapter() != ChatUsersActivity.this.listViewAdapter) {
                        ChatUsersActivity.this.listView.setAnimateEmptyView(false, 0);
                        ChatUsersActivity.this.listView.setAdapter(ChatUsersActivity.this.listViewAdapter);
                        if (itemCount == 0) {
                            ChatUsersActivity.this.showItemsAnimated(0);
                        }
                    }
                    ChatUsersActivity.this.progressBar.setVisibility(8);
                    ChatUsersActivity.this.flickerLoadingView.setVisibility(0);
                }
            });
            this.searchItem = actionBarMenuItemSearchListener;
            if (this.type == 0 && !this.firstLoaded) {
                actionBarMenuItemSearchListener.setVisibility(8);
            }
            if (this.type == 3) {
                this.searchItem.setSearchFieldHint(LocaleController.getString("ChannelSearchException", R.string.ChannelSearchException));
            } else {
                this.searchItem.setSearchFieldHint(LocaleController.getString("Search", R.string.Search));
            }
            if (!ChatObject.isChannel(this.currentChat) && !this.currentChat.creator) {
                this.searchItem.setVisibility(8);
            }
            if (this.type == 3) {
                this.doneItem = createMenu.addItemWithWidth(1, R.drawable.ic_ab_done, AndroidUtilities.dp(56.0f), LocaleController.getString("Done", R.string.Done));
            }
        }
        FrameLayout frameLayout = new FrameLayout(context) { // from class: org.telegram.ui.ChatUsersActivity.3
            @Override // android.view.ViewGroup, android.view.View
            protected void dispatchDraw(Canvas canvas) {
                canvas.drawColor(Theme.getColor(ChatUsersActivity.this.listView.getAdapter() == ChatUsersActivity.this.searchListViewAdapter ? "windowBackgroundWhite" : "windowBackgroundGray"));
                super.dispatchDraw(canvas);
            }
        };
        this.fragmentView = frameLayout;
        FrameLayout frameLayout2 = frameLayout;
        FrameLayout frameLayout3 = new FrameLayout(context);
        FlickerLoadingView flickerLoadingView = new FlickerLoadingView(context);
        this.flickerLoadingView = flickerLoadingView;
        flickerLoadingView.setViewType(6);
        this.flickerLoadingView.showDate(false);
        this.flickerLoadingView.setUseHeaderOffset(true);
        frameLayout3.addView(this.flickerLoadingView);
        RadialProgressView radialProgressView = new RadialProgressView(context);
        this.progressBar = radialProgressView;
        frameLayout3.addView(radialProgressView, LayoutHelper.createFrame(-2, -2, 17));
        this.flickerLoadingView.setVisibility(8);
        this.progressBar.setVisibility(8);
        StickerEmptyView stickerEmptyView = new StickerEmptyView(context, frameLayout3, 1);
        this.emptyView = stickerEmptyView;
        stickerEmptyView.title.setText(LocaleController.getString("NoResult", R.string.NoResult));
        this.emptyView.subtitle.setText(LocaleController.getString("SearchEmptyViewFilteredSubtitle2", R.string.SearchEmptyViewFilteredSubtitle2));
        this.emptyView.setVisibility(8);
        this.emptyView.setAnimateLayoutChange(true);
        this.emptyView.showProgress(true, false);
        frameLayout2.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f));
        this.emptyView.addView(frameLayout3, 0);
        RecyclerListView recyclerListView = new RecyclerListView(context) { // from class: org.telegram.ui.ChatUsersActivity.4
            @Override // android.view.View
            public void invalidate() {
                super.invalidate();
                if (((BaseFragment) ChatUsersActivity.this).fragmentView != null) {
                    ((BaseFragment) ChatUsersActivity.this).fragmentView.invalidate();
                }
            }
        };
        this.listView = recyclerListView;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, 1, false) { // from class: org.telegram.ui.ChatUsersActivity.5
            @Override // androidx.recyclerview.widget.LinearLayoutManager, androidx.recyclerview.widget.RecyclerView.LayoutManager
            public int scrollVerticallyBy(int i5, RecyclerView.Recycler recycler, RecyclerView.State state) {
                if (!ChatUsersActivity.this.firstLoaded && ChatUsersActivity.this.type == 0 && ChatUsersActivity.this.participants.size() == 0) {
                    return 0;
                }
                return super.scrollVerticallyBy(i5, recycler, state);
            }
        };
        this.layoutManager = linearLayoutManager;
        recyclerListView.setLayoutManager(linearLayoutManager);
        DefaultItemAnimator defaultItemAnimator = new DefaultItemAnimator() { // from class: org.telegram.ui.ChatUsersActivity.6
            int animationIndex = -1;

            @Override // androidx.recyclerview.widget.DefaultItemAnimator
            protected long getAddAnimationDelay(long j, long j2, long j3) {
                return 0L;
            }

            @Override // androidx.recyclerview.widget.RecyclerView.ItemAnimator
            public long getAddDuration() {
                return 220L;
            }

            @Override // androidx.recyclerview.widget.DefaultItemAnimator
            protected long getMoveAnimationDelay() {
                return 0L;
            }

            @Override // androidx.recyclerview.widget.RecyclerView.ItemAnimator
            public long getMoveDuration() {
                return 220L;
            }

            @Override // androidx.recyclerview.widget.RecyclerView.ItemAnimator
            public long getRemoveDuration() {
                return 220L;
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // androidx.recyclerview.widget.DefaultItemAnimator
            public void onAllAnimationsDone() {
                super.onAllAnimationsDone();
                ChatUsersActivity.this.getNotificationCenter().onAnimationFinish(this.animationIndex);
            }

            @Override // androidx.recyclerview.widget.DefaultItemAnimator, androidx.recyclerview.widget.RecyclerView.ItemAnimator
            public void runPendingAnimations() {
                boolean z = !this.mPendingRemovals.isEmpty();
                boolean z2 = !this.mPendingMoves.isEmpty();
                boolean z3 = !this.mPendingChanges.isEmpty();
                boolean z4 = !this.mPendingAdditions.isEmpty();
                if (z || z2 || z4 || z3) {
                    this.animationIndex = ChatUsersActivity.this.getNotificationCenter().setAnimationInProgress(this.animationIndex, null);
                }
                super.runPendingAnimations();
            }
        };
        this.listView.setItemAnimator(defaultItemAnimator);
        defaultItemAnimator.setSupportsChangeAnimations(false);
        this.listView.setAnimateEmptyView(true, 0);
        RecyclerListView recyclerListView2 = this.listView;
        ListAdapter listAdapter = new ListAdapter(context);
        this.listViewAdapter = listAdapter;
        recyclerListView2.setAdapter(listAdapter);
        RecyclerListView recyclerListView3 = this.listView;
        if (!LocaleController.isRTL) {
            i2 = 2;
        }
        recyclerListView3.setVerticalScrollbarPosition(i2);
        frameLayout2.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() { // from class: org.telegram.ui.ChatUsersActivity$$ExternalSyntheticLambda17
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
            public final void onItemClick(View view, int i5) {
                ChatUsersActivity.this.lambda$createView$1(context, view, i5);
            }
        });
        this.listView.setOnItemLongClickListener(new RecyclerListView.OnItemLongClickListener() { // from class: org.telegram.ui.ChatUsersActivity$$ExternalSyntheticLambda18
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener
            public final boolean onItemClick(View view, int i5) {
                boolean lambda$createView$2;
                lambda$createView$2 = ChatUsersActivity.this.lambda$createView$2(view, i5);
                return lambda$createView$2;
            }
        });
        if (this.searchItem != null) {
            this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() { // from class: org.telegram.ui.ChatUsersActivity.12
                @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
                public void onScrolled(RecyclerView recyclerView, int i5, int i6) {
                }

                @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
                public void onScrollStateChanged(RecyclerView recyclerView, int i5) {
                    if (i5 == 1) {
                        AndroidUtilities.hideKeyboard(ChatUsersActivity.this.getParentActivity().getCurrentFocus());
                    }
                }
            });
        }
        UndoView undoView = new UndoView(context);
        this.undoView = undoView;
        frameLayout2.addView(undoView, LayoutHelper.createFrame(-1, -2.0f, 83, 8.0f, 0.0f, 8.0f, 8.0f));
        updateRows();
        this.listView.setEmptyView(this.emptyView);
        this.listView.setAnimateEmptyView(false, 0);
        if (this.needOpenSearch) {
            this.searchItem.openSearch(false);
        }
        return this.fragmentView;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:275:0x051a A[RETURN] */
    /* JADX WARN: Removed duplicated region for block: B:276:0x051b  */
    /* JADX WARN: Type inference failed for: r21v1 */
    /* JADX WARN: Type inference failed for: r31v0, types: [org.telegram.ui.ActionBar.BaseFragment, org.telegram.ui.ChatUsersActivity] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void lambda$createView$1(android.content.Context r32, android.view.View r33, int r34) {
        /*
            Method dump skipped, instructions count: 1335
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChatUsersActivity.lambda$createView$1(android.content.Context, android.view.View, int):void");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: org.telegram.ui.ChatUsersActivity$8  reason: invalid class name */
    /* loaded from: classes3.dex */
    public class AnonymousClass8 implements ChatUsersActivityDelegate {
        @Override // org.telegram.ui.ChatUsersActivity.ChatUsersActivityDelegate
        public /* synthetic */ void didKickParticipant(long j) {
            ChatUsersActivityDelegate.CC.$default$didKickParticipant(this, j);
        }

        AnonymousClass8() {
        }

        @Override // org.telegram.ui.ChatUsersActivity.ChatUsersActivityDelegate
        public void didAddParticipantToList(long j, TLObject tLObject) {
            if (tLObject == null || ChatUsersActivity.this.participantsMap.get(j) != null) {
                return;
            }
            DiffCallback saveState = ChatUsersActivity.this.saveState();
            ChatUsersActivity.this.participants.add(tLObject);
            ChatUsersActivity.this.participantsMap.put(j, tLObject);
            ChatUsersActivity chatUsersActivity = ChatUsersActivity.this;
            chatUsersActivity.sortAdmins(chatUsersActivity.participants);
            ChatUsersActivity.this.updateListAnimated(saveState);
        }

        @Override // org.telegram.ui.ChatUsersActivity.ChatUsersActivityDelegate
        public void didChangeOwner(TLRPC$User tLRPC$User) {
            ChatUsersActivity.this.onOwnerChaged(tLRPC$User);
        }

        @Override // org.telegram.ui.ChatUsersActivity.ChatUsersActivityDelegate
        public void didSelectUser(long j) {
            final TLRPC$User user = ChatUsersActivity.this.getMessagesController().getUser(Long.valueOf(j));
            if (user != null) {
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ChatUsersActivity$8$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        ChatUsersActivity.AnonymousClass8.this.lambda$didSelectUser$0(user);
                    }
                }, 200L);
            }
            if (ChatUsersActivity.this.participantsMap.get(j) == null) {
                DiffCallback saveState = ChatUsersActivity.this.saveState();
                TLRPC$TL_channelParticipantAdmin tLRPC$TL_channelParticipantAdmin = new TLRPC$TL_channelParticipantAdmin();
                TLRPC$TL_peerUser tLRPC$TL_peerUser = new TLRPC$TL_peerUser();
                tLRPC$TL_channelParticipantAdmin.peer = tLRPC$TL_peerUser;
                tLRPC$TL_peerUser.user_id = user.id;
                tLRPC$TL_channelParticipantAdmin.date = ChatUsersActivity.this.getConnectionsManager().getCurrentTime();
                tLRPC$TL_channelParticipantAdmin.promoted_by = ChatUsersActivity.this.getAccountInstance().getUserConfig().clientUserId;
                ChatUsersActivity.this.participants.add(tLRPC$TL_channelParticipantAdmin);
                ChatUsersActivity.this.participantsMap.put(user.id, tLRPC$TL_channelParticipantAdmin);
                ChatUsersActivity chatUsersActivity = ChatUsersActivity.this;
                chatUsersActivity.sortAdmins(chatUsersActivity.participants);
                ChatUsersActivity.this.updateListAnimated(saveState);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$didSelectUser$0(TLRPC$User tLRPC$User) {
            if (BulletinFactory.canShowBulletin(ChatUsersActivity.this)) {
                BulletinFactory.createPromoteToAdminBulletin(ChatUsersActivity.this, tLRPC$User.first_name).show();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: org.telegram.ui.ChatUsersActivity$9  reason: invalid class name */
    /* loaded from: classes3.dex */
    public class AnonymousClass9 implements GroupCreateActivity.ContactsAddActivityDelegate {
        final /* synthetic */ Context val$context;

        AnonymousClass9(Context context) {
            this.val$context = context;
        }

        @Override // org.telegram.ui.GroupCreateActivity.ContactsAddActivityDelegate
        public void didSelectUsers(ArrayList<TLRPC$User> arrayList, int i) {
            final int size = arrayList.size();
            final ArrayList arrayList2 = new ArrayList();
            final int[] iArr = {0};
            final Context context = this.val$context;
            final Runnable runnable = new Runnable() { // from class: org.telegram.ui.ChatUsersActivity$9$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    ChatUsersActivity.AnonymousClass9.lambda$didSelectUsers$0(arrayList2, size, context);
                }
            };
            for (int i2 = 0; i2 < size; i2++) {
                final TLRPC$User tLRPC$User = arrayList.get(i2);
                ChatUsersActivity.this.getMessagesController().addUserToChat(ChatUsersActivity.this.chatId, tLRPC$User, i, null, ChatUsersActivity.this, false, new Runnable() { // from class: org.telegram.ui.ChatUsersActivity$9$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        ChatUsersActivity.AnonymousClass9.this.lambda$didSelectUsers$1(iArr, size, arrayList2, runnable, tLRPC$User);
                    }
                }, new MessagesController.ErrorDelegate() { // from class: org.telegram.ui.ChatUsersActivity$9$$ExternalSyntheticLambda2
                    @Override // org.telegram.messenger.MessagesController.ErrorDelegate
                    public final boolean run(TLRPC$TL_error tLRPC$TL_error) {
                        boolean lambda$didSelectUsers$2;
                        lambda$didSelectUsers$2 = ChatUsersActivity.AnonymousClass9.lambda$didSelectUsers$2(iArr, arrayList2, tLRPC$User, size, runnable, tLRPC$TL_error);
                        return lambda$didSelectUsers$2;
                    }
                });
                ChatUsersActivity.this.getMessagesController().putUser(tLRPC$User, false);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static /* synthetic */ void lambda$didSelectUsers$0(ArrayList arrayList, int i, Context context) {
            String string;
            CharSequence string2;
            if (arrayList.size() == 1) {
                if (i > 1) {
                    string = LocaleController.getString("InviteToGroupErrorTitleAUser", R.string.InviteToGroupErrorTitleAUser);
                } else {
                    string = LocaleController.getString("InviteToGroupErrorTitleThisUser", R.string.InviteToGroupErrorTitleThisUser);
                }
                string2 = AndroidUtilities.replaceTags(LocaleController.formatString("InviteToGroupErrorMessageSingle", R.string.InviteToGroupErrorMessageSingle, UserObject.getFirstName((TLRPC$User) arrayList.get(0))));
            } else if (arrayList.size() == 2) {
                string = LocaleController.getString("InviteToGroupErrorTitleSomeUsers", R.string.InviteToGroupErrorTitleSomeUsers);
                string2 = AndroidUtilities.replaceTags(LocaleController.formatString("InviteToGroupErrorMessageDouble", R.string.InviteToGroupErrorMessageDouble, UserObject.getFirstName((TLRPC$User) arrayList.get(0)), UserObject.getFirstName((TLRPC$User) arrayList.get(1))));
            } else if (arrayList.size() == i) {
                string = LocaleController.getString("InviteToGroupErrorTitleTheseUsers", R.string.InviteToGroupErrorTitleTheseUsers);
                string2 = LocaleController.getString("InviteToGroupErrorMessageMultipleAll", R.string.InviteToGroupErrorMessageMultipleAll);
            } else {
                string = LocaleController.getString("InviteToGroupErrorTitleSomeUsers", R.string.InviteToGroupErrorTitleSomeUsers);
                string2 = LocaleController.getString("InviteToGroupErrorMessageMultipleSome", R.string.InviteToGroupErrorMessageMultipleSome);
            }
            new AlertDialog.Builder(context).setTitle(string).setMessage(string2).setPositiveButton(LocaleController.getString("OK", R.string.OK), null).show();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$didSelectUsers$1(int[] iArr, int i, ArrayList arrayList, Runnable runnable, TLRPC$User tLRPC$User) {
            iArr[0] = iArr[0] + 1;
            if (iArr[0] >= i && arrayList.size() > 0) {
                runnable.run();
            }
            DiffCallback saveState = ChatUsersActivity.this.saveState();
            ArrayList arrayList2 = (ChatUsersActivity.this.contactsMap == null || ChatUsersActivity.this.contactsMap.size() == 0) ? ChatUsersActivity.this.participants : ChatUsersActivity.this.contacts;
            LongSparseArray longSparseArray = (ChatUsersActivity.this.contactsMap == null || ChatUsersActivity.this.contactsMap.size() == 0) ? ChatUsersActivity.this.participantsMap : ChatUsersActivity.this.contactsMap;
            if (longSparseArray.get(tLRPC$User.id) == null) {
                if (ChatObject.isChannel(ChatUsersActivity.this.currentChat)) {
                    TLRPC$TL_channelParticipant tLRPC$TL_channelParticipant = new TLRPC$TL_channelParticipant();
                    tLRPC$TL_channelParticipant.inviter_id = ChatUsersActivity.this.getUserConfig().getClientUserId();
                    TLRPC$TL_peerUser tLRPC$TL_peerUser = new TLRPC$TL_peerUser();
                    tLRPC$TL_channelParticipant.peer = tLRPC$TL_peerUser;
                    tLRPC$TL_peerUser.user_id = tLRPC$User.id;
                    tLRPC$TL_channelParticipant.date = ChatUsersActivity.this.getConnectionsManager().getCurrentTime();
                    arrayList2.add(0, tLRPC$TL_channelParticipant);
                    longSparseArray.put(tLRPC$User.id, tLRPC$TL_channelParticipant);
                } else {
                    TLRPC$TL_chatParticipant tLRPC$TL_chatParticipant = new TLRPC$TL_chatParticipant();
                    tLRPC$TL_chatParticipant.user_id = tLRPC$User.id;
                    tLRPC$TL_chatParticipant.inviter_id = ChatUsersActivity.this.getUserConfig().getClientUserId();
                    arrayList2.add(0, tLRPC$TL_chatParticipant);
                    longSparseArray.put(tLRPC$User.id, tLRPC$TL_chatParticipant);
                }
            }
            if (arrayList2 == ChatUsersActivity.this.participants) {
                ChatUsersActivity chatUsersActivity = ChatUsersActivity.this;
                chatUsersActivity.sortAdmins(chatUsersActivity.participants);
            }
            ChatUsersActivity.this.updateListAnimated(saveState);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static /* synthetic */ boolean lambda$didSelectUsers$2(int[] iArr, ArrayList arrayList, TLRPC$User tLRPC$User, int i, Runnable runnable, TLRPC$TL_error tLRPC$TL_error) {
            iArr[0] = iArr[0] + 1;
            boolean z = tLRPC$TL_error != null && "USER_PRIVACY_RESTRICTED".equals(tLRPC$TL_error.text);
            if (z) {
                arrayList.add(tLRPC$User);
            }
            if (iArr[0] >= i && arrayList.size() > 0) {
                runnable.run();
            }
            return !z;
        }

        @Override // org.telegram.ui.GroupCreateActivity.ContactsAddActivityDelegate
        public void needAddBot(TLRPC$User tLRPC$User) {
            ChatUsersActivity.this.openRightsEdit(tLRPC$User.id, null, null, null, "", true, 0, false);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: org.telegram.ui.ChatUsersActivity$10  reason: invalid class name */
    /* loaded from: classes3.dex */
    public class AnonymousClass10 extends GigagroupConvertAlert {
        @Override // org.telegram.ui.Components.GigagroupConvertAlert
        protected void onCancel() {
        }

        AnonymousClass10(Context context, BaseFragment baseFragment) {
            super(context, baseFragment);
        }

        @Override // org.telegram.ui.Components.GigagroupConvertAlert
        protected void onCovert() {
            ChatUsersActivity.this.getMessagesController().convertToGigaGroup(ChatUsersActivity.this.getParentActivity(), ChatUsersActivity.this.currentChat, ChatUsersActivity.this, new MessagesStorage.BooleanCallback() { // from class: org.telegram.ui.ChatUsersActivity$10$$ExternalSyntheticLambda0
                @Override // org.telegram.messenger.MessagesStorage.BooleanCallback
                public final void run(boolean z) {
                    ChatUsersActivity.AnonymousClass10.this.lambda$onCovert$0(z);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onCovert$0(boolean z) {
            if (!z || ((BaseFragment) ChatUsersActivity.this).parentLayout == null) {
                return;
            }
            BaseFragment baseFragment = ((BaseFragment) ChatUsersActivity.this).parentLayout.getFragmentStack().get(((BaseFragment) ChatUsersActivity.this).parentLayout.getFragmentStack().size() - 2);
            if (baseFragment instanceof ChatEditActivity) {
                baseFragment.removeSelfFromStack();
                Bundle bundle = new Bundle();
                bundle.putLong("chat_id", ChatUsersActivity.this.chatId);
                ChatEditActivity chatEditActivity = new ChatEditActivity(bundle);
                chatEditActivity.setInfo(ChatUsersActivity.this.info);
                ((BaseFragment) ChatUsersActivity.this).parentLayout.addFragmentToStack(chatEditActivity, ((BaseFragment) ChatUsersActivity.this).parentLayout.getFragmentStack().size() - 1);
                ChatUsersActivity.this.finishFragment();
                chatEditActivity.showConvertTooltip();
                return;
            }
            ChatUsersActivity.this.finishFragment();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$0(TLRPC$User tLRPC$User, TLObject tLObject, TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights, TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights, String str, boolean z, DialogInterface dialogInterface, int i) {
        openRightsEdit(tLRPC$User.id, tLObject, tLRPC$TL_chatAdminRights, tLRPC$TL_chatBannedRights, str, z, this.selectType == 1 ? 0 : 1, false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$createView$2(View view, int i) {
        if (getParentActivity() != null) {
            RecyclerView.Adapter adapter = this.listView.getAdapter();
            ListAdapter listAdapter = this.listViewAdapter;
            return adapter == listAdapter && createMenuForParticipant(listAdapter.getItem(i), false);
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sortAdmins(ArrayList<TLObject> arrayList) {
        Collections.sort(arrayList, new Comparator() { // from class: org.telegram.ui.ChatUsersActivity$$ExternalSyntheticLambda11
            @Override // java.util.Comparator
            public final int compare(Object obj, Object obj2) {
                int lambda$sortAdmins$3;
                lambda$sortAdmins$3 = ChatUsersActivity.this.lambda$sortAdmins$3((TLObject) obj, (TLObject) obj2);
                return lambda$sortAdmins$3;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ int lambda$sortAdmins$3(TLObject tLObject, TLObject tLObject2) {
        int channelAdminParticipantType = getChannelAdminParticipantType(tLObject);
        int channelAdminParticipantType2 = getChannelAdminParticipantType(tLObject2);
        if (channelAdminParticipantType > channelAdminParticipantType2) {
            return 1;
        }
        if (channelAdminParticipantType < channelAdminParticipantType2) {
            return -1;
        }
        if ((tLObject instanceof TLRPC$ChannelParticipant) && (tLObject2 instanceof TLRPC$ChannelParticipant)) {
            return (int) (MessageObject.getPeerId(((TLRPC$ChannelParticipant) tLObject).peer) - MessageObject.getPeerId(((TLRPC$ChannelParticipant) tLObject2).peer));
        }
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showItemsAnimated(final int i) {
        if (this.isPaused || !this.openTransitionStarted) {
            return;
        }
        if (this.listView.getAdapter() == this.listViewAdapter && this.firstLoaded) {
            return;
        }
        final View view = null;
        for (int i2 = 0; i2 < this.listView.getChildCount(); i2++) {
            View childAt = this.listView.getChildAt(i2);
            if (childAt instanceof FlickerLoadingView) {
                view = childAt;
            }
        }
        if (view != null) {
            this.listView.removeView(view);
            i--;
        }
        this.listView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() { // from class: org.telegram.ui.ChatUsersActivity.13
            @Override // android.view.ViewTreeObserver.OnPreDrawListener
            public boolean onPreDraw() {
                ChatUsersActivity.this.listView.getViewTreeObserver().removeOnPreDrawListener(this);
                int childCount = ChatUsersActivity.this.listView.getChildCount();
                AnimatorSet animatorSet = new AnimatorSet();
                for (int i3 = 0; i3 < childCount; i3++) {
                    View childAt2 = ChatUsersActivity.this.listView.getChildAt(i3);
                    if (childAt2 != view && ChatUsersActivity.this.listView.getChildAdapterPosition(childAt2) >= i) {
                        childAt2.setAlpha(0.0f);
                        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(childAt2, View.ALPHA, 0.0f, 1.0f);
                        ofFloat.setStartDelay((int) ((Math.min(ChatUsersActivity.this.listView.getMeasuredHeight(), Math.max(0, childAt2.getTop())) / ChatUsersActivity.this.listView.getMeasuredHeight()) * 100.0f));
                        ofFloat.setDuration(200L);
                        animatorSet.playTogether(ofFloat);
                    }
                }
                View view2 = view;
                if (view2 != null && view2.getParent() == null) {
                    ChatUsersActivity.this.listView.addView(view);
                    final RecyclerView.LayoutManager layoutManager = ChatUsersActivity.this.listView.getLayoutManager();
                    if (layoutManager != null) {
                        layoutManager.ignoreView(view);
                        View view3 = view;
                        ObjectAnimator ofFloat2 = ObjectAnimator.ofFloat(view3, View.ALPHA, view3.getAlpha(), 0.0f);
                        ofFloat2.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.ChatUsersActivity.13.1
                            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                            public void onAnimationEnd(Animator animator) {
                                view.setAlpha(1.0f);
                                layoutManager.stopIgnoringView(view);
                                ChatUsersActivity.this.listView.removeView(view);
                            }
                        });
                        ofFloat2.start();
                    }
                }
                animatorSet.start();
                return true;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onOwnerChaged(TLRPC$User tLRPC$User) {
        LongSparseArray<TLObject> longSparseArray;
        ArrayList<TLObject> arrayList;
        boolean z;
        this.undoView.showWithAction(-this.chatId, this.isChannel ? 9 : 10, tLRPC$User);
        this.currentChat.creator = false;
        boolean z2 = false;
        for (int i = 0; i < 3; i++) {
            boolean z3 = true;
            if (i == 0) {
                longSparseArray = this.contactsMap;
                arrayList = this.contacts;
            } else if (i == 1) {
                longSparseArray = this.botsMap;
                arrayList = this.bots;
            } else {
                longSparseArray = this.participantsMap;
                arrayList = this.participants;
            }
            TLObject tLObject = longSparseArray.get(tLRPC$User.id);
            if (tLObject instanceof TLRPC$ChannelParticipant) {
                TLRPC$TL_channelParticipantCreator tLRPC$TL_channelParticipantCreator = new TLRPC$TL_channelParticipantCreator();
                TLRPC$TL_peerUser tLRPC$TL_peerUser = new TLRPC$TL_peerUser();
                tLRPC$TL_channelParticipantCreator.peer = tLRPC$TL_peerUser;
                long j = tLRPC$User.id;
                tLRPC$TL_peerUser.user_id = j;
                longSparseArray.put(j, tLRPC$TL_channelParticipantCreator);
                int indexOf = arrayList.indexOf(tLObject);
                if (indexOf >= 0) {
                    arrayList.set(indexOf, tLRPC$TL_channelParticipantCreator);
                }
                z2 = true;
                z = true;
            } else {
                z = false;
            }
            long clientUserId = getUserConfig().getClientUserId();
            TLObject tLObject2 = longSparseArray.get(clientUserId);
            if (tLObject2 instanceof TLRPC$ChannelParticipant) {
                TLRPC$TL_channelParticipantAdmin tLRPC$TL_channelParticipantAdmin = new TLRPC$TL_channelParticipantAdmin();
                TLRPC$TL_peerUser tLRPC$TL_peerUser2 = new TLRPC$TL_peerUser();
                tLRPC$TL_channelParticipantAdmin.peer = tLRPC$TL_peerUser2;
                tLRPC$TL_peerUser2.user_id = clientUserId;
                tLRPC$TL_channelParticipantAdmin.self = true;
                tLRPC$TL_channelParticipantAdmin.inviter_id = clientUserId;
                tLRPC$TL_channelParticipantAdmin.promoted_by = clientUserId;
                tLRPC$TL_channelParticipantAdmin.date = (int) (System.currentTimeMillis() / 1000);
                TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights = new TLRPC$TL_chatAdminRights();
                tLRPC$TL_channelParticipantAdmin.admin_rights = tLRPC$TL_chatAdminRights;
                tLRPC$TL_chatAdminRights.add_admins = true;
                tLRPC$TL_chatAdminRights.pin_messages = true;
                tLRPC$TL_chatAdminRights.manage_topics = true;
                tLRPC$TL_chatAdminRights.invite_users = true;
                tLRPC$TL_chatAdminRights.ban_users = true;
                tLRPC$TL_chatAdminRights.delete_messages = true;
                tLRPC$TL_chatAdminRights.edit_messages = true;
                tLRPC$TL_chatAdminRights.post_messages = true;
                tLRPC$TL_chatAdminRights.change_info = true;
                if (!this.isChannel) {
                    tLRPC$TL_chatAdminRights.manage_call = true;
                }
                longSparseArray.put(clientUserId, tLRPC$TL_channelParticipantAdmin);
                int indexOf2 = arrayList.indexOf(tLObject2);
                if (indexOf2 >= 0) {
                    arrayList.set(indexOf2, tLRPC$TL_channelParticipantAdmin);
                }
            } else {
                z3 = z;
            }
            if (z3) {
                Collections.sort(arrayList, new Comparator() { // from class: org.telegram.ui.ChatUsersActivity$$ExternalSyntheticLambda10
                    @Override // java.util.Comparator
                    public final int compare(Object obj, Object obj2) {
                        int lambda$onOwnerChaged$4;
                        lambda$onOwnerChaged$4 = ChatUsersActivity.this.lambda$onOwnerChaged$4((TLObject) obj, (TLObject) obj2);
                        return lambda$onOwnerChaged$4;
                    }
                });
            }
        }
        if (!z2) {
            TLRPC$TL_channelParticipantCreator tLRPC$TL_channelParticipantCreator2 = new TLRPC$TL_channelParticipantCreator();
            TLRPC$TL_peerUser tLRPC$TL_peerUser3 = new TLRPC$TL_peerUser();
            tLRPC$TL_channelParticipantCreator2.peer = tLRPC$TL_peerUser3;
            long j2 = tLRPC$User.id;
            tLRPC$TL_peerUser3.user_id = j2;
            this.participantsMap.put(j2, tLRPC$TL_channelParticipantCreator2);
            this.participants.add(tLRPC$TL_channelParticipantCreator2);
            sortAdmins(this.participants);
            updateRows();
        }
        this.listViewAdapter.notifyDataSetChanged();
        ChatUsersActivityDelegate chatUsersActivityDelegate = this.delegate;
        if (chatUsersActivityDelegate != null) {
            chatUsersActivityDelegate.didChangeOwner(tLRPC$User);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ int lambda$onOwnerChaged$4(TLObject tLObject, TLObject tLObject2) {
        int channelAdminParticipantType = getChannelAdminParticipantType(tLObject);
        int channelAdminParticipantType2 = getChannelAdminParticipantType(tLObject2);
        if (channelAdminParticipantType > channelAdminParticipantType2) {
            return 1;
        }
        return channelAdminParticipantType < channelAdminParticipantType2 ? -1 : 0;
    }

    private void openRightsEdit2(final long j, final int i, TLObject tLObject, TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights, TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights, String str, boolean z, final int i2, boolean z2) {
        final boolean[] zArr = new boolean[1];
        boolean z3 = (tLObject instanceof TLRPC$TL_channelParticipantAdmin) || (tLObject instanceof TLRPC$TL_chatParticipantAdmin);
        ChatRightsEditActivity chatRightsEditActivity = new ChatRightsEditActivity(j, this.chatId, tLRPC$TL_chatAdminRights, this.defaultBannedRights, tLRPC$TL_chatBannedRights, str, i2, true, false, null) { // from class: org.telegram.ui.ChatUsersActivity.14
            @Override // org.telegram.ui.ActionBar.BaseFragment
            public void onTransitionAnimationEnd(boolean z4, boolean z5) {
                if (z4 || !z5 || !zArr[0] || !BulletinFactory.canShowBulletin(ChatUsersActivity.this)) {
                    return;
                }
                if (j > 0) {
                    TLRPC$User user = getMessagesController().getUser(Long.valueOf(j));
                    if (user == null) {
                        return;
                    }
                    BulletinFactory.createPromoteToAdminBulletin(ChatUsersActivity.this, user.first_name).show();
                    return;
                }
                TLRPC$Chat chat = getMessagesController().getChat(Long.valueOf(-j));
                if (chat == null) {
                    return;
                }
                BulletinFactory.createPromoteToAdminBulletin(ChatUsersActivity.this, chat.title).show();
            }
        };
        final boolean z4 = z3;
        chatRightsEditActivity.setDelegate(new ChatRightsEditActivity.ChatRightsEditActivityDelegate() { // from class: org.telegram.ui.ChatUsersActivity.15
            @Override // org.telegram.ui.ChatRightsEditActivity.ChatRightsEditActivityDelegate
            public void didSetRights(int i3, TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights2, TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights2, String str2) {
                TLRPC$ChatParticipant tLRPC$TL_chatParticipant;
                TLRPC$ChannelParticipant tLRPC$TL_channelParticipant;
                int i4 = i2;
                if (i4 != 0) {
                    if (i4 != 1 || i3 != 0) {
                        return;
                    }
                    ChatUsersActivity.this.removeParticipants(j);
                    return;
                }
                int i5 = 0;
                while (true) {
                    if (i5 >= ChatUsersActivity.this.participants.size()) {
                        break;
                    }
                    TLObject tLObject2 = (TLObject) ChatUsersActivity.this.participants.get(i5);
                    if (tLObject2 instanceof TLRPC$ChannelParticipant) {
                        if (MessageObject.getPeerId(((TLRPC$ChannelParticipant) tLObject2).peer) == j) {
                            if (i3 == 1) {
                                tLRPC$TL_channelParticipant = new TLRPC$TL_channelParticipantAdmin();
                            } else {
                                tLRPC$TL_channelParticipant = new TLRPC$TL_channelParticipant();
                            }
                            tLRPC$TL_channelParticipant.admin_rights = tLRPC$TL_chatAdminRights2;
                            tLRPC$TL_channelParticipant.banned_rights = tLRPC$TL_chatBannedRights2;
                            tLRPC$TL_channelParticipant.inviter_id = ChatUsersActivity.this.getUserConfig().getClientUserId();
                            if (j > 0) {
                                TLRPC$TL_peerUser tLRPC$TL_peerUser = new TLRPC$TL_peerUser();
                                tLRPC$TL_channelParticipant.peer = tLRPC$TL_peerUser;
                                tLRPC$TL_peerUser.user_id = j;
                            } else {
                                TLRPC$TL_peerChannel tLRPC$TL_peerChannel = new TLRPC$TL_peerChannel();
                                tLRPC$TL_channelParticipant.peer = tLRPC$TL_peerChannel;
                                tLRPC$TL_peerChannel.channel_id = -j;
                            }
                            tLRPC$TL_channelParticipant.date = i;
                            tLRPC$TL_channelParticipant.flags |= 4;
                            tLRPC$TL_channelParticipant.rank = str2;
                            ChatUsersActivity.this.participants.set(i5, tLRPC$TL_channelParticipant);
                        }
                    } else if (tLObject2 instanceof TLRPC$ChatParticipant) {
                        TLRPC$ChatParticipant tLRPC$ChatParticipant = (TLRPC$ChatParticipant) tLObject2;
                        if (i3 == 1) {
                            tLRPC$TL_chatParticipant = new TLRPC$TL_chatParticipantAdmin();
                        } else {
                            tLRPC$TL_chatParticipant = new TLRPC$TL_chatParticipant();
                        }
                        tLRPC$TL_chatParticipant.user_id = tLRPC$ChatParticipant.user_id;
                        tLRPC$TL_chatParticipant.date = tLRPC$ChatParticipant.date;
                        tLRPC$TL_chatParticipant.inviter_id = tLRPC$ChatParticipant.inviter_id;
                        int indexOf = ChatUsersActivity.this.info.participants.participants.indexOf(tLRPC$ChatParticipant);
                        if (indexOf >= 0) {
                            ChatUsersActivity.this.info.participants.participants.set(indexOf, tLRPC$TL_chatParticipant);
                        }
                        ChatUsersActivity.this.loadChatParticipants(0, 200);
                    }
                    i5++;
                }
                if (i3 != 1 || z4) {
                    return;
                }
                zArr[0] = true;
            }

            @Override // org.telegram.ui.ChatRightsEditActivity.ChatRightsEditActivityDelegate
            public void didChangeOwner(TLRPC$User tLRPC$User) {
                ChatUsersActivity.this.onOwnerChaged(tLRPC$User);
            }
        });
        presentFragment(chatRightsEditActivity);
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean canBeginSlide() {
        return checkDiscard();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void openRightsEdit(final long j, final TLObject tLObject, TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights, TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights, String str, boolean z, int i, final boolean z2) {
        ChatRightsEditActivity chatRightsEditActivity = new ChatRightsEditActivity(j, this.chatId, tLRPC$TL_chatAdminRights, this.defaultBannedRights, tLRPC$TL_chatBannedRights, str, i, z, tLObject == null, null);
        chatRightsEditActivity.setDelegate(new ChatRightsEditActivity.ChatRightsEditActivityDelegate() { // from class: org.telegram.ui.ChatUsersActivity.16
            @Override // org.telegram.ui.ChatRightsEditActivity.ChatRightsEditActivityDelegate
            public void didSetRights(int i2, TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights2, TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights2, String str2) {
                TLObject tLObject2 = tLObject;
                if (tLObject2 instanceof TLRPC$ChannelParticipant) {
                    TLRPC$ChannelParticipant tLRPC$ChannelParticipant = (TLRPC$ChannelParticipant) tLObject2;
                    tLRPC$ChannelParticipant.admin_rights = tLRPC$TL_chatAdminRights2;
                    tLRPC$ChannelParticipant.banned_rights = tLRPC$TL_chatBannedRights2;
                    tLRPC$ChannelParticipant.rank = str2;
                }
                if (ChatUsersActivity.this.delegate == null || i2 != 1) {
                    if (ChatUsersActivity.this.delegate != null) {
                        ChatUsersActivity.this.delegate.didAddParticipantToList(j, tLObject);
                    }
                } else {
                    ChatUsersActivity.this.delegate.didSelectUser(j);
                }
                if (z2) {
                    ChatUsersActivity.this.removeSelfFromStack();
                }
            }

            @Override // org.telegram.ui.ChatRightsEditActivity.ChatRightsEditActivityDelegate
            public void didChangeOwner(TLRPC$User tLRPC$User) {
                ChatUsersActivity.this.onOwnerChaged(tLRPC$User);
            }
        });
        presentFragment(chatRightsEditActivity, z2);
    }

    private void removeParticipant(long j) {
        if (!ChatObject.isChannel(this.currentChat)) {
            return;
        }
        getMessagesController().deleteParticipantFromChat(this.chatId, getMessagesController().getUser(Long.valueOf(j)));
        ChatUsersActivityDelegate chatUsersActivityDelegate = this.delegate;
        if (chatUsersActivityDelegate != null) {
            chatUsersActivityDelegate.didKickParticipant(j);
        }
        finishFragment();
    }

    private TLObject getAnyParticipant(long j) {
        LongSparseArray<TLObject> longSparseArray;
        for (int i = 0; i < 3; i++) {
            if (i == 0) {
                longSparseArray = this.contactsMap;
            } else if (i == 1) {
                longSparseArray = this.botsMap;
            } else {
                longSparseArray = this.participantsMap;
            }
            TLObject tLObject = longSparseArray.get(j);
            if (tLObject != null) {
                return tLObject;
            }
        }
        return null;
    }

    private void removeParticipants(TLObject tLObject) {
        if (tLObject instanceof TLRPC$ChatParticipant) {
            removeParticipants(((TLRPC$ChatParticipant) tLObject).user_id);
        } else if (!(tLObject instanceof TLRPC$ChannelParticipant)) {
        } else {
            removeParticipants(MessageObject.getPeerId(((TLRPC$ChannelParticipant) tLObject).peer));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void removeParticipants(long j) {
        LongSparseArray<TLObject> longSparseArray;
        ArrayList<TLObject> arrayList;
        TLRPC$ChatFull tLRPC$ChatFull;
        DiffCallback saveState = saveState();
        boolean z = false;
        for (int i = 0; i < 3; i++) {
            if (i == 0) {
                longSparseArray = this.contactsMap;
                arrayList = this.contacts;
            } else if (i == 1) {
                longSparseArray = this.botsMap;
                arrayList = this.bots;
            } else {
                longSparseArray = this.participantsMap;
                arrayList = this.participants;
            }
            TLObject tLObject = longSparseArray.get(j);
            if (tLObject != null) {
                longSparseArray.remove(j);
                arrayList.remove(tLObject);
                if (this.type == 0 && (tLRPC$ChatFull = this.info) != null) {
                    tLRPC$ChatFull.kicked_count--;
                }
                z = true;
            }
        }
        if (z) {
            updateListAnimated(saveState);
        }
        RecyclerView.Adapter adapter = this.listView.getAdapter();
        SearchAdapter searchAdapter = this.searchListViewAdapter;
        if (adapter == searchAdapter) {
            searchAdapter.removeUserId(j);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateParticipantWithRights(TLRPC$ChannelParticipant tLRPC$ChannelParticipant, TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights, TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights, long j, boolean z) {
        LongSparseArray<TLObject> longSparseArray;
        ChatUsersActivityDelegate chatUsersActivityDelegate;
        boolean z2 = false;
        for (int i = 0; i < 3; i++) {
            if (i == 0) {
                longSparseArray = this.contactsMap;
            } else if (i == 1) {
                longSparseArray = this.botsMap;
            } else {
                longSparseArray = this.participantsMap;
            }
            TLObject tLObject = longSparseArray.get(MessageObject.getPeerId(tLRPC$ChannelParticipant.peer));
            if (tLObject instanceof TLRPC$ChannelParticipant) {
                tLRPC$ChannelParticipant = (TLRPC$ChannelParticipant) tLObject;
                tLRPC$ChannelParticipant.admin_rights = tLRPC$TL_chatAdminRights;
                tLRPC$ChannelParticipant.banned_rights = tLRPC$TL_chatBannedRights;
                if (z) {
                    tLRPC$ChannelParticipant.promoted_by = getUserConfig().getClientUserId();
                }
            }
            if (z && tLObject != null && !z2 && (chatUsersActivityDelegate = this.delegate) != null) {
                chatUsersActivityDelegate.didAddParticipantToList(j, tLObject);
                z2 = true;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:136:0x02ad A[RETURN] */
    /* JADX WARN: Removed duplicated region for block: B:137:0x02ae  */
    /* JADX WARN: Type inference failed for: r23v0, types: [org.telegram.ui.ActionBar.BaseFragment, org.telegram.ui.ChatUsersActivity] */
    /* JADX WARN: Type inference failed for: r2v2 */
    /* JADX WARN: Type inference failed for: r2v4 */
    /* JADX WARN: Type inference failed for: r2v6 */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public boolean createMenuForParticipant(final org.telegram.tgnet.TLObject r24, boolean r25) {
        /*
            Method dump skipped, instructions count: 747
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChatUsersActivity.createMenuForParticipant(org.telegram.tgnet.TLObject, boolean):boolean");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createMenuForParticipant$6(final ArrayList arrayList, TLRPC$User tLRPC$User, final long j, final boolean z, final TLObject tLObject, final int i, final TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights, final TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights, final String str, DialogInterface dialogInterface, final int i2) {
        if (((Integer) arrayList.get(i2)).intValue() == 2) {
            getMessagesController().deleteParticipantFromChat(this.chatId, tLRPC$User);
            removeParticipants(j);
            if (this.currentChat == null || tLRPC$User == null || !BulletinFactory.canShowBulletin(this)) {
                return;
            }
            BulletinFactory.createRemoveFromChatBulletin(this, tLRPC$User, this.currentChat.title).show();
        } else if (((Integer) arrayList.get(i2)).intValue() == 1 && z && ((tLObject instanceof TLRPC$TL_channelParticipantAdmin) || (tLObject instanceof TLRPC$TL_chatParticipantAdmin))) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
            builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
            builder.setMessage(LocaleController.formatString("AdminWillBeRemoved", R.string.AdminWillBeRemoved, UserObject.getUserName(tLRPC$User)));
            builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.ChatUsersActivity$$ExternalSyntheticLambda2
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface2, int i3) {
                    ChatUsersActivity.this.lambda$createMenuForParticipant$5(j, i, tLObject, tLRPC$TL_chatAdminRights, tLRPC$TL_chatBannedRights, str, z, arrayList, i2, dialogInterface2, i3);
                }
            });
            builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
            showDialog(builder.create());
        } else {
            openRightsEdit2(j, i, tLObject, tLRPC$TL_chatAdminRights, tLRPC$TL_chatBannedRights, str, z, ((Integer) arrayList.get(i2)).intValue(), false);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createMenuForParticipant$5(long j, int i, TLObject tLObject, TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights, TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights, String str, boolean z, ArrayList arrayList, int i2, DialogInterface dialogInterface, int i3) {
        openRightsEdit2(j, i, tLObject, tLRPC$TL_chatAdminRights, tLRPC$TL_chatBannedRights, str, z, ((Integer) arrayList.get(i2)).intValue(), false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createMenuForParticipant$9(CharSequence[] charSequenceArr, long j, TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights, String str, final TLObject tLObject, TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights, DialogInterface dialogInterface, int i) {
        TLRPC$Chat chat;
        int i2 = this.type;
        if (i2 == 1) {
            if (i == 0 && charSequenceArr.length == 2) {
                ChatRightsEditActivity chatRightsEditActivity = new ChatRightsEditActivity(j, this.chatId, tLRPC$TL_chatAdminRights, null, null, str, 0, true, false, null);
                chatRightsEditActivity.setDelegate(new ChatRightsEditActivity.ChatRightsEditActivityDelegate() { // from class: org.telegram.ui.ChatUsersActivity.17
                    @Override // org.telegram.ui.ChatRightsEditActivity.ChatRightsEditActivityDelegate
                    public void didSetRights(int i3, TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights2, TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights2, String str2) {
                        TLObject tLObject2 = tLObject;
                        if (tLObject2 instanceof TLRPC$ChannelParticipant) {
                            TLRPC$ChannelParticipant tLRPC$ChannelParticipant = (TLRPC$ChannelParticipant) tLObject2;
                            tLRPC$ChannelParticipant.admin_rights = tLRPC$TL_chatAdminRights2;
                            tLRPC$ChannelParticipant.banned_rights = tLRPC$TL_chatBannedRights2;
                            tLRPC$ChannelParticipant.rank = str2;
                            ChatUsersActivity.this.updateParticipantWithRights(tLRPC$ChannelParticipant, tLRPC$TL_chatAdminRights2, tLRPC$TL_chatBannedRights2, 0L, false);
                        }
                    }

                    @Override // org.telegram.ui.ChatRightsEditActivity.ChatRightsEditActivityDelegate
                    public void didChangeOwner(TLRPC$User tLRPC$User) {
                        ChatUsersActivity.this.onOwnerChaged(tLRPC$User);
                    }
                });
                presentFragment(chatRightsEditActivity);
                return;
            }
            getMessagesController().setUserAdminRole(this.chatId, getMessagesController().getUser(Long.valueOf(j)), new TLRPC$TL_chatAdminRights(), "", !this.isChannel, this, false, false, null, null);
            removeParticipants(j);
        } else if (i2 != 0 && i2 != 3) {
            if (i != 0) {
                return;
            }
            TLRPC$User tLRPC$User = null;
            if (j > 0) {
                chat = null;
                tLRPC$User = getMessagesController().getUser(Long.valueOf(j));
            } else {
                chat = getMessagesController().getChat(Long.valueOf(-j));
            }
            getMessagesController().deleteParticipantFromChat(this.chatId, tLRPC$User, chat, false, false);
        } else {
            if (i == 0) {
                if (i2 == 3) {
                    ChatRightsEditActivity chatRightsEditActivity2 = new ChatRightsEditActivity(j, this.chatId, null, this.defaultBannedRights, tLRPC$TL_chatBannedRights, str, 1, true, false, null);
                    chatRightsEditActivity2.setDelegate(new ChatRightsEditActivity.ChatRightsEditActivityDelegate() { // from class: org.telegram.ui.ChatUsersActivity.18
                        @Override // org.telegram.ui.ChatRightsEditActivity.ChatRightsEditActivityDelegate
                        public void didSetRights(int i3, TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights2, TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights2, String str2) {
                            TLObject tLObject2 = tLObject;
                            if (tLObject2 instanceof TLRPC$ChannelParticipant) {
                                TLRPC$ChannelParticipant tLRPC$ChannelParticipant = (TLRPC$ChannelParticipant) tLObject2;
                                tLRPC$ChannelParticipant.admin_rights = tLRPC$TL_chatAdminRights2;
                                tLRPC$ChannelParticipant.banned_rights = tLRPC$TL_chatBannedRights2;
                                tLRPC$ChannelParticipant.rank = str2;
                                ChatUsersActivity.this.updateParticipantWithRights(tLRPC$ChannelParticipant, tLRPC$TL_chatAdminRights2, tLRPC$TL_chatBannedRights2, 0L, false);
                            }
                        }

                        @Override // org.telegram.ui.ChatRightsEditActivity.ChatRightsEditActivityDelegate
                        public void didChangeOwner(TLRPC$User tLRPC$User2) {
                            ChatUsersActivity.this.onOwnerChaged(tLRPC$User2);
                        }
                    });
                    presentFragment(chatRightsEditActivity2);
                } else if (i2 == 0 && j > 0) {
                    getMessagesController().addUserToChat(this.chatId, getMessagesController().getUser(Long.valueOf(j)), 0, null, this, null);
                }
            } else if (i == 1) {
                TLRPC$TL_channels_editBanned tLRPC$TL_channels_editBanned = new TLRPC$TL_channels_editBanned();
                tLRPC$TL_channels_editBanned.participant = getMessagesController().getInputPeer(j);
                tLRPC$TL_channels_editBanned.channel = getMessagesController().getInputChannel(this.chatId);
                tLRPC$TL_channels_editBanned.banned_rights = new TLRPC$TL_chatBannedRights();
                getConnectionsManager().sendRequest(tLRPC$TL_channels_editBanned, new RequestDelegate() { // from class: org.telegram.ui.ChatUsersActivity$$ExternalSyntheticLambda15
                    @Override // org.telegram.tgnet.RequestDelegate
                    public final void run(TLObject tLObject2, TLRPC$TL_error tLRPC$TL_error) {
                        ChatUsersActivity.this.lambda$createMenuForParticipant$8(tLObject2, tLRPC$TL_error);
                    }
                });
            }
            if ((i != 0 || this.type != 0) && i != 1) {
                return;
            }
            removeParticipants(tLObject);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createMenuForParticipant$8(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLObject != null) {
            final TLRPC$Updates tLRPC$Updates = (TLRPC$Updates) tLObject;
            getMessagesController().processUpdates(tLRPC$Updates, false);
            if (tLRPC$Updates.chats.isEmpty()) {
                return;
            }
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ChatUsersActivity$$ExternalSyntheticLambda9
                @Override // java.lang.Runnable
                public final void run() {
                    ChatUsersActivity.this.lambda$createMenuForParticipant$7(tLRPC$Updates);
                }
            }, 1000L);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createMenuForParticipant$7(TLRPC$Updates tLRPC$Updates) {
        getMessagesController().loadFullChat(tLRPC$Updates.chats.get(0).id, 0, true);
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.chatInfoDidLoad) {
            boolean z = false;
            TLRPC$ChatFull tLRPC$ChatFull = (TLRPC$ChatFull) objArr[0];
            boolean booleanValue = ((Boolean) objArr[2]).booleanValue();
            if (tLRPC$ChatFull.id != this.chatId) {
                return;
            }
            if (booleanValue && ChatObject.isChannel(this.currentChat)) {
                return;
            }
            if (this.info != null) {
                z = true;
            }
            this.info = tLRPC$ChatFull;
            if (!z) {
                int currentSlowmode = getCurrentSlowmode();
                this.initialSlowmode = currentSlowmode;
                this.selectedSlowmode = currentSlowmode;
            }
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ChatUsersActivity$$ExternalSyntheticLambda7
                @Override // java.lang.Runnable
                public final void run() {
                    ChatUsersActivity.this.lambda$didReceivedNotification$10();
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$didReceivedNotification$10() {
        loadChatParticipants(0, 200);
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean onBackPressed() {
        return checkDiscard();
    }

    public void setDelegate(ChatUsersActivityDelegate chatUsersActivityDelegate) {
        this.delegate = chatUsersActivityDelegate;
    }

    private int getCurrentSlowmode() {
        TLRPC$ChatFull tLRPC$ChatFull = this.info;
        if (tLRPC$ChatFull != null) {
            int i = tLRPC$ChatFull.slowmode_seconds;
            if (i == 10) {
                return 1;
            }
            if (i == 30) {
                return 2;
            }
            if (i == 60) {
                return 3;
            }
            if (i == 300) {
                return 4;
            }
            if (i == 900) {
                return 5;
            }
            return i == 3600 ? 6 : 0;
        }
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String formatSeconds(int i) {
        if (i < 60) {
            return LocaleController.formatPluralString("Seconds", i, new Object[0]);
        }
        if (i < 3600) {
            return LocaleController.formatPluralString("Minutes", i / 60, new Object[0]);
        }
        return LocaleController.formatPluralString("Hours", (i / 60) / 60, new Object[0]);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean checkDiscard() {
        if (!ChatObject.getBannedRightsString(this.defaultBannedRights).equals(this.initialBannedRights) || this.initialSlowmode != this.selectedSlowmode) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
            builder.setTitle(LocaleController.getString("UserRestrictionsApplyChanges", R.string.UserRestrictionsApplyChanges));
            if (this.isChannel) {
                builder.setMessage(LocaleController.getString("ChannelSettingsChangedAlert", R.string.ChannelSettingsChangedAlert));
            } else {
                builder.setMessage(LocaleController.getString("GroupSettingsChangedAlert", R.string.GroupSettingsChangedAlert));
            }
            builder.setPositiveButton(LocaleController.getString("ApplyTheme", R.string.ApplyTheme), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.ChatUsersActivity$$ExternalSyntheticLambda1
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    ChatUsersActivity.this.lambda$checkDiscard$11(dialogInterface, i);
                }
            });
            builder.setNegativeButton(LocaleController.getString("PassportDiscard", R.string.PassportDiscard), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.ChatUsersActivity$$ExternalSyntheticLambda0
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    ChatUsersActivity.this.lambda$checkDiscard$12(dialogInterface, i);
                }
            });
            showDialog(builder.create());
            return false;
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$checkDiscard$11(DialogInterface dialogInterface, int i) {
        processDone();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$checkDiscard$12(DialogInterface dialogInterface, int i) {
        finishFragment();
    }

    public boolean hasSelectType() {
        return this.selectType != 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String formatUserPermissions(TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights) {
        if (tLRPC$TL_chatBannedRights == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        boolean z = tLRPC$TL_chatBannedRights.view_messages;
        if (z && this.defaultBannedRights.view_messages != z) {
            sb.append(LocaleController.getString("UserRestrictionsNoRead", R.string.UserRestrictionsNoRead));
        }
        boolean z2 = tLRPC$TL_chatBannedRights.send_messages;
        if (z2 && this.defaultBannedRights.send_messages != z2) {
            if (sb.length() != 0) {
                sb.append(", ");
            }
            sb.append(LocaleController.getString("UserRestrictionsNoSend", R.string.UserRestrictionsNoSend));
        }
        boolean z3 = tLRPC$TL_chatBannedRights.send_media;
        if (z3 && this.defaultBannedRights.send_media != z3) {
            if (sb.length() != 0) {
                sb.append(", ");
            }
            sb.append(LocaleController.getString("UserRestrictionsNoSendMedia", R.string.UserRestrictionsNoSendMedia));
        }
        boolean z4 = tLRPC$TL_chatBannedRights.send_stickers;
        if (z4 && this.defaultBannedRights.send_stickers != z4) {
            if (sb.length() != 0) {
                sb.append(", ");
            }
            sb.append(LocaleController.getString("UserRestrictionsNoSendStickers", R.string.UserRestrictionsNoSendStickers));
        }
        boolean z5 = tLRPC$TL_chatBannedRights.send_polls;
        if (z5 && this.defaultBannedRights.send_polls != z5) {
            if (sb.length() != 0) {
                sb.append(", ");
            }
            sb.append(LocaleController.getString("UserRestrictionsNoSendPolls", R.string.UserRestrictionsNoSendPolls));
        }
        boolean z6 = tLRPC$TL_chatBannedRights.embed_links;
        if (z6 && this.defaultBannedRights.embed_links != z6) {
            if (sb.length() != 0) {
                sb.append(", ");
            }
            sb.append(LocaleController.getString("UserRestrictionsNoEmbedLinks", R.string.UserRestrictionsNoEmbedLinks));
        }
        boolean z7 = tLRPC$TL_chatBannedRights.invite_users;
        if (z7 && this.defaultBannedRights.invite_users != z7) {
            if (sb.length() != 0) {
                sb.append(", ");
            }
            sb.append(LocaleController.getString("UserRestrictionsNoInviteUsers", R.string.UserRestrictionsNoInviteUsers));
        }
        boolean z8 = tLRPC$TL_chatBannedRights.pin_messages;
        if (z8 && this.defaultBannedRights.pin_messages != z8) {
            if (sb.length() != 0) {
                sb.append(", ");
            }
            sb.append(LocaleController.getString("UserRestrictionsNoPinMessages", R.string.UserRestrictionsNoPinMessages));
        }
        boolean z9 = tLRPC$TL_chatBannedRights.change_info;
        if (z9 && this.defaultBannedRights.change_info != z9) {
            if (sb.length() != 0) {
                sb.append(", ");
            }
            sb.append(LocaleController.getString("UserRestrictionsNoChangeInfo", R.string.UserRestrictionsNoChangeInfo));
        }
        if (sb.length() != 0) {
            sb.replace(0, 1, sb.substring(0, 1).toUpperCase());
            sb.append('.');
        }
        return sb.toString();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void processDone() {
        TLRPC$ChatFull tLRPC$ChatFull;
        if (this.type != 3) {
            return;
        }
        TLRPC$Chat tLRPC$Chat = this.currentChat;
        if (tLRPC$Chat.creator && !ChatObject.isChannel(tLRPC$Chat) && this.selectedSlowmode != this.initialSlowmode && this.info != null) {
            MessagesController.getInstance(this.currentAccount).convertToMegaGroup(getParentActivity(), this.chatId, this, new MessagesStorage.LongCallback() { // from class: org.telegram.ui.ChatUsersActivity$$ExternalSyntheticLambda13
                @Override // org.telegram.messenger.MessagesStorage.LongCallback
                public final void run(long j) {
                    ChatUsersActivity.this.lambda$processDone$13(j);
                }
            });
            return;
        }
        if (!ChatObject.getBannedRightsString(this.defaultBannedRights).equals(this.initialBannedRights)) {
            getMessagesController().setDefaultBannedRole(this.chatId, this.defaultBannedRights, ChatObject.isChannel(this.currentChat), this);
            TLRPC$Chat chat = getMessagesController().getChat(Long.valueOf(this.chatId));
            if (chat != null) {
                chat.default_banned_rights = this.defaultBannedRights;
            }
        }
        int i = this.selectedSlowmode;
        if (i != this.initialSlowmode && (tLRPC$ChatFull = this.info) != null) {
            tLRPC$ChatFull.slowmode_seconds = getSecondsForIndex(i);
            this.info.flags |= 131072;
            getMessagesController().setChannelSlowMode(this.chatId, this.info.slowmode_seconds);
        }
        finishFragment();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$processDone$13(long j) {
        if (j != 0) {
            this.chatId = j;
            this.currentChat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(j));
            processDone();
        }
    }

    public void setInfo(TLRPC$ChatFull tLRPC$ChatFull) {
        this.info = tLRPC$ChatFull;
        if (tLRPC$ChatFull != null) {
            int currentSlowmode = getCurrentSlowmode();
            this.initialSlowmode = currentSlowmode;
            this.selectedSlowmode = currentSlowmode;
        }
    }

    private int getChannelAdminParticipantType(TLObject tLObject) {
        if ((tLObject instanceof TLRPC$TL_channelParticipantCreator) || (tLObject instanceof TLRPC$TL_channelParticipantSelf)) {
            return 0;
        }
        return ((tLObject instanceof TLRPC$TL_channelParticipantAdmin) || (tLObject instanceof TLRPC$TL_channelParticipant)) ? 1 : 2;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void loadChatParticipants(int i, int i2) {
        if (this.loadingUsers) {
            return;
        }
        this.contactsEndReached = false;
        this.botsEndReached = false;
        loadChatParticipants(i, i2, true);
    }

    private ArrayList<TLRPC$TL_channels_getParticipants> loadChatParticipantsRequests(int i, int i2, boolean z) {
        TLRPC$Chat tLRPC$Chat;
        TLRPC$TL_channels_getParticipants tLRPC$TL_channels_getParticipants = new TLRPC$TL_channels_getParticipants();
        ArrayList<TLRPC$TL_channels_getParticipants> arrayList = new ArrayList<>();
        arrayList.add(tLRPC$TL_channels_getParticipants);
        tLRPC$TL_channels_getParticipants.channel = getMessagesController().getInputChannel(this.chatId);
        int i3 = this.type;
        if (i3 == 0) {
            tLRPC$TL_channels_getParticipants.filter = new TLRPC$TL_channelParticipantsKicked();
        } else if (i3 == 1) {
            tLRPC$TL_channels_getParticipants.filter = new TLRPC$TL_channelParticipantsAdmins();
        } else if (i3 == 2) {
            TLRPC$ChatFull tLRPC$ChatFull = this.info;
            if (tLRPC$ChatFull != null && tLRPC$ChatFull.participants_count <= 200 && (tLRPC$Chat = this.currentChat) != null && tLRPC$Chat.megagroup) {
                tLRPC$TL_channels_getParticipants.filter = new TLRPC$TL_channelParticipantsRecent();
            } else if (this.selectType == 1) {
                if (!this.contactsEndReached) {
                    this.delayResults = 2;
                    tLRPC$TL_channels_getParticipants.filter = new TLRPC$TL_channelParticipantsContacts();
                    this.contactsEndReached = true;
                    arrayList.addAll(loadChatParticipantsRequests(0, 200, false));
                } else {
                    tLRPC$TL_channels_getParticipants.filter = new TLRPC$TL_channelParticipantsRecent();
                }
            } else if (!this.contactsEndReached) {
                this.delayResults = 3;
                tLRPC$TL_channels_getParticipants.filter = new TLRPC$TL_channelParticipantsContacts();
                this.contactsEndReached = true;
                arrayList.addAll(loadChatParticipantsRequests(0, 200, false));
            } else if (!this.botsEndReached) {
                tLRPC$TL_channels_getParticipants.filter = new TLRPC$ChannelParticipantsFilter() { // from class: org.telegram.tgnet.TLRPC$TL_channelParticipantsBots
                    public static int constructor = -NUM;

                    @Override // org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
                        abstractSerializedData.writeInt32(constructor);
                    }
                };
                this.botsEndReached = true;
                arrayList.addAll(loadChatParticipantsRequests(0, 200, false));
            } else {
                tLRPC$TL_channels_getParticipants.filter = new TLRPC$TL_channelParticipantsRecent();
            }
        } else if (i3 == 3) {
            tLRPC$TL_channels_getParticipants.filter = new TLRPC$TL_channelParticipantsBanned();
        }
        tLRPC$TL_channels_getParticipants.filter.q = "";
        tLRPC$TL_channels_getParticipants.offset = i;
        tLRPC$TL_channels_getParticipants.limit = i2;
        return arrayList;
    }

    private void loadChatParticipants(int i, int i2, boolean z) {
        LongSparseArray<TLRPC$TL_groupCallParticipant> longSparseArray;
        int i3 = 0;
        if (!ChatObject.isChannel(this.currentChat)) {
            this.loadingUsers = false;
            this.participants.clear();
            this.bots.clear();
            this.contacts.clear();
            this.participantsMap.clear();
            this.contactsMap.clear();
            this.botsMap.clear();
            int i4 = this.type;
            if (i4 == 1) {
                TLRPC$ChatFull tLRPC$ChatFull = this.info;
                if (tLRPC$ChatFull != null) {
                    int size = tLRPC$ChatFull.participants.participants.size();
                    while (i3 < size) {
                        TLRPC$ChatParticipant tLRPC$ChatParticipant = this.info.participants.participants.get(i3);
                        if ((tLRPC$ChatParticipant instanceof TLRPC$TL_chatParticipantCreator) || (tLRPC$ChatParticipant instanceof TLRPC$TL_chatParticipantAdmin)) {
                            this.participants.add(tLRPC$ChatParticipant);
                        }
                        this.participantsMap.put(tLRPC$ChatParticipant.user_id, tLRPC$ChatParticipant);
                        i3++;
                    }
                }
            } else if (i4 == 2 && this.info != null) {
                long j = getUserConfig().clientUserId;
                int size2 = this.info.participants.participants.size();
                while (i3 < size2) {
                    TLRPC$ChatParticipant tLRPC$ChatParticipant2 = this.info.participants.participants.get(i3);
                    if ((this.selectType == 0 || tLRPC$ChatParticipant2.user_id != j) && ((longSparseArray = this.ignoredUsers) == null || longSparseArray.indexOfKey(tLRPC$ChatParticipant2.user_id) < 0)) {
                        if (this.selectType == 1) {
                            if (getContactsController().isContact(tLRPC$ChatParticipant2.user_id)) {
                                this.contacts.add(tLRPC$ChatParticipant2);
                                this.contactsMap.put(tLRPC$ChatParticipant2.user_id, tLRPC$ChatParticipant2);
                            } else if (!UserObject.isDeleted(getMessagesController().getUser(Long.valueOf(tLRPC$ChatParticipant2.user_id)))) {
                                this.participants.add(tLRPC$ChatParticipant2);
                                this.participantsMap.put(tLRPC$ChatParticipant2.user_id, tLRPC$ChatParticipant2);
                            }
                        } else if (getContactsController().isContact(tLRPC$ChatParticipant2.user_id)) {
                            this.contacts.add(tLRPC$ChatParticipant2);
                            this.contactsMap.put(tLRPC$ChatParticipant2.user_id, tLRPC$ChatParticipant2);
                        } else {
                            TLRPC$User user = getMessagesController().getUser(Long.valueOf(tLRPC$ChatParticipant2.user_id));
                            if (user != null && user.bot) {
                                this.bots.add(tLRPC$ChatParticipant2);
                                this.botsMap.put(tLRPC$ChatParticipant2.user_id, tLRPC$ChatParticipant2);
                            } else {
                                this.participants.add(tLRPC$ChatParticipant2);
                                this.participantsMap.put(tLRPC$ChatParticipant2.user_id, tLRPC$ChatParticipant2);
                            }
                        }
                    }
                    i3++;
                }
            }
            ListAdapter listAdapter = this.listViewAdapter;
            if (listAdapter != null) {
                listAdapter.notifyDataSetChanged();
            }
            updateRows();
            ListAdapter listAdapter2 = this.listViewAdapter;
            if (listAdapter2 == null) {
                return;
            }
            listAdapter2.notifyDataSetChanged();
            return;
        }
        this.loadingUsers = true;
        StickerEmptyView stickerEmptyView = this.emptyView;
        if (stickerEmptyView != null) {
            stickerEmptyView.showProgress(true, false);
        }
        ListAdapter listAdapter3 = this.listViewAdapter;
        if (listAdapter3 != null) {
            listAdapter3.notifyDataSetChanged();
        }
        final ArrayList<TLRPC$TL_channels_getParticipants> loadChatParticipantsRequests = loadChatParticipantsRequests(i, i2, z);
        final ArrayList arrayList = new ArrayList();
        final Runnable runnable = new Runnable() { // from class: org.telegram.ui.ChatUsersActivity$$ExternalSyntheticLambda8
            @Override // java.lang.Runnable
            public final void run() {
                ChatUsersActivity.this.lambda$loadChatParticipants$14(loadChatParticipantsRequests, arrayList);
            }
        };
        final AtomicInteger atomicInteger = new AtomicInteger(0);
        while (i3 < loadChatParticipantsRequests.size()) {
            arrayList.add(null);
            final int i5 = i3;
            getConnectionsManager().bindRequestToGuid(getConnectionsManager().sendRequest(loadChatParticipantsRequests.get(i3), new RequestDelegate() { // from class: org.telegram.ui.ChatUsersActivity$$ExternalSyntheticLambda14
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    ChatUsersActivity.lambda$loadChatParticipants$16(arrayList, i5, atomicInteger, loadChatParticipantsRequests, runnable, tLObject, tLRPC$TL_error);
                }
            }), this.classGuid);
            i3++;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$loadChatParticipants$14(ArrayList arrayList, ArrayList arrayList2) {
        int i;
        ArrayList<TLObject> arrayList3;
        LongSparseArray<TLObject> longSparseArray;
        int i2;
        TLRPC$Chat tLRPC$Chat;
        LongSparseArray<TLRPC$TL_groupCallParticipant> longSparseArray2;
        boolean z = false;
        int i3 = 0;
        int i4 = 0;
        while (i4 < arrayList.size()) {
            TLRPC$TL_channels_getParticipants tLRPC$TL_channels_getParticipants = (TLRPC$TL_channels_getParticipants) arrayList.get(i4);
            TLRPC$TL_channels_channelParticipants tLRPC$TL_channels_channelParticipants = (TLRPC$TL_channels_channelParticipants) arrayList2.get(i4);
            if (tLRPC$TL_channels_getParticipants == null || tLRPC$TL_channels_channelParticipants == null) {
                i = i4;
            } else {
                if (this.type == 1) {
                    getMessagesController().processLoadedAdminsResponse(this.chatId, tLRPC$TL_channels_channelParticipants);
                }
                getMessagesController().putUsers(tLRPC$TL_channels_channelParticipants.users, z);
                getMessagesController().putChats(tLRPC$TL_channels_channelParticipants.chats, z);
                long clientUserId = getUserConfig().getClientUserId();
                if (this.selectType != 0) {
                    int i5 = 0;
                    while (true) {
                        if (i5 >= tLRPC$TL_channels_channelParticipants.participants.size()) {
                            break;
                        } else if (MessageObject.getPeerId(tLRPC$TL_channels_channelParticipants.participants.get(i5).peer) == clientUserId) {
                            tLRPC$TL_channels_channelParticipants.participants.remove(i5);
                            break;
                        } else {
                            i5++;
                        }
                    }
                }
                if (this.type == 2) {
                    this.delayResults--;
                    TLRPC$ChannelParticipantsFilter tLRPC$ChannelParticipantsFilter = tLRPC$TL_channels_getParticipants.filter;
                    if (tLRPC$ChannelParticipantsFilter instanceof TLRPC$TL_channelParticipantsContacts) {
                        arrayList3 = this.contacts;
                        longSparseArray = this.contactsMap;
                    } else if (tLRPC$ChannelParticipantsFilter instanceof TLRPC$TL_channelParticipantsBots) {
                        arrayList3 = this.bots;
                        longSparseArray = this.botsMap;
                    } else {
                        arrayList3 = this.participants;
                        longSparseArray = this.participantsMap;
                    }
                } else {
                    arrayList3 = this.participants;
                    longSparseArray = this.participantsMap;
                    longSparseArray.clear();
                }
                arrayList3.clear();
                arrayList3.addAll(tLRPC$TL_channels_channelParticipants.participants);
                int size = tLRPC$TL_channels_channelParticipants.participants.size();
                int i6 = 0;
                while (i6 < size) {
                    TLRPC$ChannelParticipant tLRPC$ChannelParticipant = tLRPC$TL_channels_channelParticipants.participants.get(i6);
                    int i7 = i4;
                    if (tLRPC$ChannelParticipant.user_id == clientUserId) {
                        arrayList3.remove(tLRPC$ChannelParticipant);
                    } else {
                        longSparseArray.put(MessageObject.getPeerId(tLRPC$ChannelParticipant.peer), tLRPC$ChannelParticipant);
                    }
                    i6++;
                    i4 = i7;
                }
                i = i4;
                int size2 = arrayList3.size() + i3;
                if (this.type == 2) {
                    int size3 = this.participants.size();
                    int i8 = 0;
                    while (i8 < size3) {
                        TLObject tLObject = this.participants.get(i8);
                        if (!(tLObject instanceof TLRPC$ChannelParticipant)) {
                            this.participants.remove(i8);
                        } else {
                            long peerId = MessageObject.getPeerId(((TLRPC$ChannelParticipant) tLObject).peer);
                            if ((this.contactsMap.get(peerId) == null && this.botsMap.get(peerId) == null && (this.selectType != 1 || peerId <= 0 || !UserObject.isDeleted(getMessagesController().getUser(Long.valueOf(peerId)))) && ((longSparseArray2 = this.ignoredUsers) == null || longSparseArray2.indexOfKey(peerId) < 0)) ? false : true) {
                                this.participants.remove(i8);
                                this.participantsMap.remove(peerId);
                            } else {
                                i8++;
                            }
                        }
                        i8--;
                        size3--;
                        i8++;
                    }
                }
                try {
                    i2 = this.type;
                } catch (Exception e) {
                    FileLog.e(e);
                }
                if ((i2 == 0 || i2 == 3 || i2 == 2) && (tLRPC$Chat = this.currentChat) != null && tLRPC$Chat.megagroup) {
                    TLRPC$ChatFull tLRPC$ChatFull = this.info;
                    if ((tLRPC$ChatFull instanceof TLRPC$TL_channelFull) && tLRPC$ChatFull.participants_count <= 200) {
                        sortUsers(arrayList3);
                        i3 = size2;
                    }
                }
                if (i2 == 1) {
                    sortAdmins(this.participants);
                }
                i3 = size2;
            }
            i4 = i + 1;
            z = false;
        }
        if (this.type != 2 || this.delayResults <= 0) {
            ListAdapter listAdapter = this.listViewAdapter;
            showItemsAnimated(listAdapter != null ? listAdapter.getItemCount() : 0);
            this.loadingUsers = false;
            this.firstLoaded = true;
            ActionBarMenuItem actionBarMenuItem = this.searchItem;
            if (actionBarMenuItem != null) {
                actionBarMenuItem.setVisibility((this.type != 0 || i3 > 5) ? 0 : 8);
            }
        }
        updateRows();
        if (this.listViewAdapter != null) {
            this.listView.setAnimateEmptyView(this.openTransitionStarted, 0);
            this.listViewAdapter.notifyDataSetChanged();
            if (this.emptyView != null && this.listViewAdapter.getItemCount() == 0 && this.firstLoaded) {
                this.emptyView.showProgress(false, true);
            }
        }
        resumeDelayedFragmentAnimation();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$loadChatParticipants$16(final ArrayList arrayList, final int i, final AtomicInteger atomicInteger, final ArrayList arrayList2, final Runnable runnable, final TLObject tLObject, final TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ChatUsersActivity$$ExternalSyntheticLambda6
            @Override // java.lang.Runnable
            public final void run() {
                ChatUsersActivity.lambda$loadChatParticipants$15(TLRPC$TL_error.this, tLObject, arrayList, i, atomicInteger, arrayList2, runnable);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$loadChatParticipants$15(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, ArrayList arrayList, int i, AtomicInteger atomicInteger, ArrayList arrayList2, Runnable runnable) {
        if (tLRPC$TL_error == null && (tLObject instanceof TLRPC$TL_channels_channelParticipants)) {
            arrayList.set(i, (TLRPC$TL_channels_channelParticipants) tLObject);
        }
        atomicInteger.getAndIncrement();
        if (atomicInteger.get() == arrayList2.size()) {
            runnable.run();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sortUsers(ArrayList<TLObject> arrayList) {
        final int currentTime = getConnectionsManager().getCurrentTime();
        Collections.sort(arrayList, new Comparator() { // from class: org.telegram.ui.ChatUsersActivity$$ExternalSyntheticLambda12
            @Override // java.util.Comparator
            public final int compare(Object obj, Object obj2) {
                int lambda$sortUsers$17;
                lambda$sortUsers$17 = ChatUsersActivity.this.lambda$sortUsers$17(currentTime, (TLObject) obj, (TLObject) obj2);
                return lambda$sortUsers$17;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ int lambda$sortUsers$17(int i, TLObject tLObject, TLObject tLObject2) {
        int i2;
        TLRPC$UserStatus tLRPC$UserStatus;
        TLRPC$UserStatus tLRPC$UserStatus2;
        TLRPC$ChannelParticipant tLRPC$ChannelParticipant = (TLRPC$ChannelParticipant) tLObject;
        TLRPC$ChannelParticipant tLRPC$ChannelParticipant2 = (TLRPC$ChannelParticipant) tLObject2;
        long peerId = MessageObject.getPeerId(tLRPC$ChannelParticipant.peer);
        long peerId2 = MessageObject.getPeerId(tLRPC$ChannelParticipant2.peer);
        int i3 = -100;
        if (peerId > 0) {
            TLRPC$User user = getMessagesController().getUser(Long.valueOf(MessageObject.getPeerId(tLRPC$ChannelParticipant.peer)));
            if (user == null || (tLRPC$UserStatus2 = user.status) == null) {
                i2 = 0;
            } else {
                i2 = user.self ? i + 50000 : tLRPC$UserStatus2.expires;
            }
        } else {
            i2 = -100;
        }
        if (peerId2 > 0) {
            TLRPC$User user2 = getMessagesController().getUser(Long.valueOf(MessageObject.getPeerId(tLRPC$ChannelParticipant2.peer)));
            if (user2 == null || (tLRPC$UserStatus = user2.status) == null) {
                i3 = 0;
            } else {
                i3 = user2.self ? i + 50000 : tLRPC$UserStatus.expires;
            }
        }
        if (i2 > 0 && i3 > 0) {
            if (i2 > i3) {
                return 1;
            }
            return i2 < i3 ? -1 : 0;
        } else if (i2 < 0 && i3 < 0) {
            if (i2 > i3) {
                return 1;
            }
            return i2 < i3 ? -1 : 0;
        } else if ((i2 < 0 && i3 > 0) || (i2 == 0 && i3 != 0)) {
            return -1;
        } else {
            return ((i3 >= 0 || i2 <= 0) && (i3 != 0 || i2 == 0)) ? 0 : 1;
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onResume() {
        super.onResume();
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
        ListAdapter listAdapter = this.listViewAdapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
        StickerEmptyView stickerEmptyView = this.emptyView;
        if (stickerEmptyView != null) {
            stickerEmptyView.requestLayout();
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onPause() {
        super.onPause();
        UndoView undoView = this.undoView;
        if (undoView != null) {
            undoView.hide(true, 0);
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onBecomeFullyHidden() {
        UndoView undoView = this.undoView;
        if (undoView != null) {
            undoView.hide(true, 0);
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onTransitionAnimationEnd(boolean z, boolean z2) {
        if (z) {
            this.openTransitionStarted = true;
        }
        if (!z || z2 || !this.needOpenSearch) {
            return;
        }
        this.searchItem.getSearchField().requestFocus();
        AndroidUtilities.showKeyboard(this.searchItem.getSearchField());
        this.searchItem.setVisibility(8);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public class SearchAdapter extends RecyclerListView.SelectionAdapter {
        private int contactsStartRow;
        private int globalStartRow;
        private int groupStartRow;
        private Context mContext;
        private SearchAdapterHelper searchAdapterHelper;
        private boolean searchInProgress;
        private Runnable searchRunnable;
        private ArrayList<Object> searchResult = new ArrayList<>();
        private LongSparseArray<TLObject> searchResultMap = new LongSparseArray<>();
        private ArrayList<CharSequence> searchResultNames = new ArrayList<>();
        private int totalCount = 0;

        public SearchAdapter(Context context) {
            this.mContext = context;
            SearchAdapterHelper searchAdapterHelper = new SearchAdapterHelper(true);
            this.searchAdapterHelper = searchAdapterHelper;
            searchAdapterHelper.setDelegate(new SearchAdapterHelper.SearchAdapterHelperDelegate() { // from class: org.telegram.ui.ChatUsersActivity$SearchAdapter$$ExternalSyntheticLambda4
                @Override // org.telegram.ui.Adapters.SearchAdapterHelper.SearchAdapterHelperDelegate
                public /* synthetic */ boolean canApplySearchResults(int i) {
                    return SearchAdapterHelper.SearchAdapterHelperDelegate.CC.$default$canApplySearchResults(this, i);
                }

                @Override // org.telegram.ui.Adapters.SearchAdapterHelper.SearchAdapterHelperDelegate
                public /* synthetic */ LongSparseArray getExcludeCallParticipants() {
                    return SearchAdapterHelper.SearchAdapterHelperDelegate.CC.$default$getExcludeCallParticipants(this);
                }

                @Override // org.telegram.ui.Adapters.SearchAdapterHelper.SearchAdapterHelperDelegate
                public /* synthetic */ LongSparseArray getExcludeUsers() {
                    return SearchAdapterHelper.SearchAdapterHelperDelegate.CC.$default$getExcludeUsers(this);
                }

                @Override // org.telegram.ui.Adapters.SearchAdapterHelper.SearchAdapterHelperDelegate
                public final void onDataSetChanged(int i) {
                    ChatUsersActivity.SearchAdapter.this.lambda$new$0(i);
                }

                @Override // org.telegram.ui.Adapters.SearchAdapterHelper.SearchAdapterHelperDelegate
                public /* synthetic */ void onSetHashtags(ArrayList arrayList, HashMap hashMap) {
                    SearchAdapterHelper.SearchAdapterHelperDelegate.CC.$default$onSetHashtags(this, arrayList, hashMap);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$0(int i) {
            if (!this.searchAdapterHelper.isSearchInProgress()) {
                int itemCount = getItemCount();
                notifyDataSetChanged();
                if (getItemCount() > itemCount) {
                    ChatUsersActivity.this.showItemsAnimated(itemCount);
                }
                if (this.searchInProgress || getItemCount() != 0 || i == 0) {
                    return;
                }
                ChatUsersActivity.this.emptyView.showProgress(false, true);
            }
        }

        public void searchUsers(final String str) {
            if (this.searchRunnable != null) {
                Utilities.searchQueue.cancelRunnable(this.searchRunnable);
                this.searchRunnable = null;
            }
            this.searchResult.clear();
            this.searchResultMap.clear();
            this.searchResultNames.clear();
            this.searchAdapterHelper.mergeResults(null);
            this.searchAdapterHelper.queryServerSearch(null, ChatUsersActivity.this.type != 0, false, true, false, false, ChatObject.isChannel(ChatUsersActivity.this.currentChat) ? ChatUsersActivity.this.chatId : 0L, false, ChatUsersActivity.this.type, 0);
            notifyDataSetChanged();
            if (!TextUtils.isEmpty(str)) {
                this.searchInProgress = true;
                ChatUsersActivity.this.emptyView.showProgress(true, true);
                DispatchQueue dispatchQueue = Utilities.searchQueue;
                Runnable runnable = new Runnable() { // from class: org.telegram.ui.ChatUsersActivity$SearchAdapter$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        ChatUsersActivity.SearchAdapter.this.lambda$searchUsers$1(str);
                    }
                };
                this.searchRunnable = runnable;
                dispatchQueue.postRunnable(runnable, 300L);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* renamed from: processSearch */
        public void lambda$searchUsers$1(final String str) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ChatUsersActivity$SearchAdapter$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    ChatUsersActivity.SearchAdapter.this.lambda$processSearch$3(str);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$processSearch$3(final String str) {
            Runnable runnable = null;
            this.searchRunnable = null;
            final ArrayList arrayList = (ChatObject.isChannel(ChatUsersActivity.this.currentChat) || ChatUsersActivity.this.info == null) ? null : new ArrayList(ChatUsersActivity.this.info.participants.participants);
            final ArrayList arrayList2 = ChatUsersActivity.this.selectType == 1 ? new ArrayList(ChatUsersActivity.this.getContactsController().contacts) : null;
            if (arrayList != null || arrayList2 != null) {
                runnable = new Runnable() { // from class: org.telegram.ui.ChatUsersActivity$SearchAdapter$$ExternalSyntheticLambda2
                    @Override // java.lang.Runnable
                    public final void run() {
                        ChatUsersActivity.SearchAdapter.this.lambda$processSearch$2(str, arrayList, arrayList2);
                    }
                };
            } else {
                this.searchInProgress = false;
            }
            this.searchAdapterHelper.queryServerSearch(str, ChatUsersActivity.this.selectType != 0, false, true, false, false, ChatObject.isChannel(ChatUsersActivity.this.currentChat) ? ChatUsersActivity.this.chatId : 0L, false, ChatUsersActivity.this.type, 1, runnable);
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* JADX WARN: Code restructure failed: missing block: B:49:0x0142, code lost:
            if (r15.contains(" " + r4) != false) goto L58;
         */
        /* JADX WARN: Code restructure failed: missing block: B:90:0x024b, code lost:
            if (r5.contains(" " + r9) != false) goto L109;
         */
        /* JADX WARN: Multi-variable type inference failed */
        /* JADX WARN: Removed duplicated region for block: B:104:0x02a9 A[LOOP:3: B:81:0x0211->B:104:0x02a9, LOOP_END] */
        /* JADX WARN: Removed duplicated region for block: B:114:0x0157 A[SYNTHETIC] */
        /* JADX WARN: Removed duplicated region for block: B:119:0x025f A[SYNTHETIC] */
        /* JADX WARN: Removed duplicated region for block: B:64:0x018c A[LOOP:1: B:39:0x0104->B:64:0x018c, LOOP_END] */
        /* JADX WARN: Type inference failed for: r15v3, types: [java.util.ArrayList] */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public /* synthetic */ void lambda$processSearch$2(java.lang.String r24, java.util.ArrayList r25, java.util.ArrayList r26) {
            /*
                Method dump skipped, instructions count: 714
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChatUsersActivity.SearchAdapter.lambda$processSearch$2(java.lang.String, java.util.ArrayList, java.util.ArrayList):void");
        }

        private void updateSearchResults(final ArrayList<Object> arrayList, final LongSparseArray<TLObject> longSparseArray, final ArrayList<CharSequence> arrayList2, final ArrayList<TLObject> arrayList3) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ChatUsersActivity$SearchAdapter$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    ChatUsersActivity.SearchAdapter.this.lambda$updateSearchResults$4(arrayList, longSparseArray, arrayList2, arrayList3);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$updateSearchResults$4(ArrayList arrayList, LongSparseArray longSparseArray, ArrayList arrayList2, ArrayList arrayList3) {
            if (!ChatUsersActivity.this.searching) {
                return;
            }
            this.searchInProgress = false;
            this.searchResult = arrayList;
            this.searchResultMap = longSparseArray;
            this.searchResultNames = arrayList2;
            this.searchAdapterHelper.mergeResults(arrayList);
            if (!ChatObject.isChannel(ChatUsersActivity.this.currentChat)) {
                ArrayList<TLObject> groupSearch = this.searchAdapterHelper.getGroupSearch();
                groupSearch.clear();
                groupSearch.addAll(arrayList3);
            }
            int itemCount = getItemCount();
            notifyDataSetChanged();
            if (getItemCount() > itemCount) {
                ChatUsersActivity.this.showItemsAnimated(itemCount);
            }
            if (this.searchAdapterHelper.isSearchInProgress() || getItemCount() != 0) {
                return;
            }
            ChatUsersActivity.this.emptyView.showProgress(false, true);
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return viewHolder.getItemViewType() != 1;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            return this.totalCount;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void notifyDataSetChanged() {
            this.totalCount = 0;
            int size = this.searchAdapterHelper.getGroupSearch().size();
            if (size != 0) {
                this.groupStartRow = 0;
                this.totalCount += size + 1;
            } else {
                this.groupStartRow = -1;
            }
            int size2 = this.searchResult.size();
            if (size2 != 0) {
                int i = this.totalCount;
                this.contactsStartRow = i;
                this.totalCount = i + size2 + 1;
            } else {
                this.contactsStartRow = -1;
            }
            int size3 = this.searchAdapterHelper.getGlobalSearch().size();
            if (size3 != 0) {
                int i2 = this.totalCount;
                this.globalStartRow = i2;
                this.totalCount = i2 + size3 + 1;
            } else {
                this.globalStartRow = -1;
            }
            if (ChatUsersActivity.this.searching && ChatUsersActivity.this.listView != null && ChatUsersActivity.this.listView.getAdapter() != ChatUsersActivity.this.searchListViewAdapter) {
                ChatUsersActivity.this.listView.setAnimateEmptyView(true, 0);
                ChatUsersActivity.this.listView.setAdapter(ChatUsersActivity.this.searchListViewAdapter);
                ChatUsersActivity.this.listView.setFastScrollVisible(false);
                ChatUsersActivity.this.listView.setVerticalScrollBarEnabled(true);
            }
            super.notifyDataSetChanged();
        }

        public void removeUserId(long j) {
            this.searchAdapterHelper.removeUserId(j);
            TLObject tLObject = this.searchResultMap.get(j);
            if (tLObject != null) {
                this.searchResult.remove(tLObject);
            }
            notifyDataSetChanged();
        }

        public TLObject getItem(int i) {
            int size = this.searchAdapterHelper.getGroupSearch().size();
            if (size != 0) {
                int i2 = size + 1;
                if (i2 > i) {
                    if (i != 0) {
                        return this.searchAdapterHelper.getGroupSearch().get(i - 1);
                    }
                    return null;
                }
                i -= i2;
            }
            int size2 = this.searchResult.size();
            if (size2 != 0) {
                int i3 = size2 + 1;
                if (i3 > i) {
                    if (i != 0) {
                        return (TLObject) this.searchResult.get(i - 1);
                    }
                    return null;
                }
                i -= i3;
            }
            int size3 = this.searchAdapterHelper.getGlobalSearch().size();
            if (size3 == 0 || size3 + 1 <= i || i == 0) {
                return null;
            }
            return this.searchAdapterHelper.getGlobalSearch().get(i - 1);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ boolean lambda$onCreateViewHolder$5(ManageChatUserCell manageChatUserCell, boolean z) {
            TLObject item = getItem(((Integer) manageChatUserCell.getTag()).intValue());
            if (item instanceof TLRPC$ChannelParticipant) {
                return ChatUsersActivity.this.createMenuForParticipant((TLRPC$ChannelParticipant) item, !z);
            }
            return false;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        /* renamed from: onCreateViewHolder */
        public RecyclerView.ViewHolder mo1822onCreateViewHolder(ViewGroup viewGroup, int i) {
            FrameLayout frameLayout;
            if (i == 0) {
                ManageChatUserCell manageChatUserCell = new ManageChatUserCell(this.mContext, 2, 2, ChatUsersActivity.this.selectType == 0);
                manageChatUserCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                manageChatUserCell.setDelegate(new ManageChatUserCell.ManageChatUserCellDelegate() { // from class: org.telegram.ui.ChatUsersActivity$SearchAdapter$$ExternalSyntheticLambda5
                    @Override // org.telegram.ui.Cells.ManageChatUserCell.ManageChatUserCellDelegate
                    public final boolean onOptionsButtonCheck(ManageChatUserCell manageChatUserCell2, boolean z) {
                        boolean lambda$onCreateViewHolder$5;
                        lambda$onCreateViewHolder$5 = ChatUsersActivity.SearchAdapter.this.lambda$onCreateViewHolder$5(manageChatUserCell2, z);
                        return lambda$onCreateViewHolder$5;
                    }
                });
                frameLayout = manageChatUserCell;
            } else {
                frameLayout = new GraySectionCell(this.mContext);
            }
            return new RecyclerListView.Holder(frameLayout);
        }

        /* JADX WARN: Multi-variable type inference failed */
        /* JADX WARN: Removed duplicated region for block: B:42:0x00f4  */
        /* JADX WARN: Removed duplicated region for block: B:49:0x0106  */
        /* JADX WARN: Removed duplicated region for block: B:53:0x0111  */
        /* JADX WARN: Removed duplicated region for block: B:62:0x0143  */
        /* JADX WARN: Removed duplicated region for block: B:66:0x014f A[ADDED_TO_REGION] */
        /* JADX WARN: Removed duplicated region for block: B:69:0x015d  */
        /* JADX WARN: Removed duplicated region for block: B:85:0x01a1 A[ADDED_TO_REGION] */
        /* JADX WARN: Removed duplicated region for block: B:88:0x01ae  */
        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder r17, int r18) {
            /*
                Method dump skipped, instructions count: 462
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChatUsersActivity.SearchAdapter.onBindViewHolder(androidx.recyclerview.widget.RecyclerView$ViewHolder, int):void");
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onViewRecycled(RecyclerView.ViewHolder viewHolder) {
            View view = viewHolder.itemView;
            if (view instanceof ManageChatUserCell) {
                ((ManageChatUserCell) view).recycle();
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int i) {
            return (i == this.globalStartRow || i == this.groupStartRow || i == this.contactsStartRow) ? 1 : 0;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            int itemViewType = viewHolder.getItemViewType();
            if (itemViewType == 7) {
                return ChatObject.canBlockUsers(ChatUsersActivity.this.currentChat);
            }
            if (itemViewType != 0) {
                return itemViewType == 0 || itemViewType == 2 || itemViewType == 6;
            }
            Object currentObject = ((ManageChatUserCell) viewHolder.itemView).getCurrentObject();
            return ChatUsersActivity.this.type == 1 || !(currentObject instanceof TLRPC$User) || !((TLRPC$User) currentObject).self;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            return ChatUsersActivity.this.rowCount;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ boolean lambda$onCreateViewHolder$0(ManageChatUserCell manageChatUserCell, boolean z) {
            return ChatUsersActivity.this.createMenuForParticipant(ChatUsersActivity.this.listViewAdapter.getItem(((Integer) manageChatUserCell.getTag()).intValue()), !z);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        /* renamed from: onCreateViewHolder */
        public RecyclerView.ViewHolder mo1822onCreateViewHolder(ViewGroup viewGroup, int i) {
            FlickerLoadingView flickerLoadingView;
            SlideChooseView slideChooseView;
            int i2 = 2;
            int i3 = 7;
            boolean z = false;
            switch (i) {
                case 0:
                    Context context = this.mContext;
                    if (ChatUsersActivity.this.type != 0 && ChatUsersActivity.this.type != 3) {
                        i3 = 6;
                    }
                    if (ChatUsersActivity.this.type == 0 || ChatUsersActivity.this.type == 3) {
                        i2 = 6;
                    }
                    if (ChatUsersActivity.this.selectType == 0) {
                        z = true;
                    }
                    ManageChatUserCell manageChatUserCell = new ManageChatUserCell(context, i3, i2, z);
                    manageChatUserCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    manageChatUserCell.setDelegate(new ManageChatUserCell.ManageChatUserCellDelegate() { // from class: org.telegram.ui.ChatUsersActivity$ListAdapter$$ExternalSyntheticLambda0
                        @Override // org.telegram.ui.Cells.ManageChatUserCell.ManageChatUserCellDelegate
                        public final boolean onOptionsButtonCheck(ManageChatUserCell manageChatUserCell2, boolean z2) {
                            boolean lambda$onCreateViewHolder$0;
                            lambda$onCreateViewHolder$0 = ChatUsersActivity.ListAdapter.this.lambda$onCreateViewHolder$0(manageChatUserCell2, z2);
                            return lambda$onCreateViewHolder$0;
                        }
                    });
                    slideChooseView = manageChatUserCell;
                    flickerLoadingView = slideChooseView;
                    break;
                case 1:
                    flickerLoadingView = new TextInfoPrivacyCell(this.mContext);
                    break;
                case 2:
                    View manageChatTextCell = new ManageChatTextCell(this.mContext);
                    manageChatTextCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    flickerLoadingView = manageChatTextCell;
                    break;
                case 3:
                    flickerLoadingView = new ShadowSectionCell(this.mContext);
                    break;
                case 4:
                    TextInfoPrivacyCell textInfoPrivacyCell = new TextInfoPrivacyCell(this.mContext);
                    if (ChatUsersActivity.this.isChannel) {
                        textInfoPrivacyCell.setText(LocaleController.getString(R.string.NoBlockedChannel2));
                    } else {
                        textInfoPrivacyCell.setText(LocaleController.getString(R.string.NoBlockedGroup2));
                    }
                    textInfoPrivacyCell.setBackground(Theme.getThemedDrawable(this.mContext, R.drawable.greydivider_bottom, "windowBackgroundGrayShadow"));
                    flickerLoadingView = textInfoPrivacyCell;
                    break;
                case 5:
                    HeaderCell headerCell = new HeaderCell(this.mContext, "windowBackgroundWhiteBlueHeader", 21, 11, false);
                    headerCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    headerCell.setHeight(43);
                    flickerLoadingView = headerCell;
                    break;
                case 6:
                    View textSettingsCell = new TextSettingsCell(this.mContext);
                    textSettingsCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    flickerLoadingView = textSettingsCell;
                    break;
                case 7:
                    View textCheckCell2 = new TextCheckCell2(this.mContext);
                    textCheckCell2.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    flickerLoadingView = textCheckCell2;
                    break;
                case 8:
                    View graySectionCell = new GraySectionCell(this.mContext);
                    graySectionCell.setBackground(null);
                    flickerLoadingView = graySectionCell;
                    break;
                case 9:
                default:
                    SlideChooseView slideChooseView2 = new SlideChooseView(this.mContext);
                    slideChooseView2.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    int i4 = ChatUsersActivity.this.selectedSlowmode;
                    int i5 = R.string.SlowmodeSeconds;
                    int i6 = R.string.SlowmodeMinutes;
                    slideChooseView2.setOptions(i4, LocaleController.getString("SlowmodeOff", R.string.SlowmodeOff), LocaleController.formatString("SlowmodeSeconds", i5, 10), LocaleController.formatString("SlowmodeSeconds", i5, 30), LocaleController.formatString("SlowmodeMinutes", i6, 1), LocaleController.formatString("SlowmodeMinutes", i6, 5), LocaleController.formatString("SlowmodeMinutes", i6, 15), LocaleController.formatString("SlowmodeHours", R.string.SlowmodeHours, 1));
                    slideChooseView2.setCallback(new SlideChooseView.Callback() { // from class: org.telegram.ui.ChatUsersActivity$ListAdapter$$ExternalSyntheticLambda1
                        @Override // org.telegram.ui.Components.SlideChooseView.Callback
                        public final void onOptionSelected(int i7) {
                            ChatUsersActivity.ListAdapter.this.lambda$onCreateViewHolder$1(i7);
                        }

                        @Override // org.telegram.ui.Components.SlideChooseView.Callback
                        public /* synthetic */ void onTouchEnd() {
                            SlideChooseView.Callback.CC.$default$onTouchEnd(this);
                        }
                    });
                    slideChooseView = slideChooseView2;
                    flickerLoadingView = slideChooseView;
                    break;
                case 10:
                    flickerLoadingView = new LoadingCell(this.mContext, AndroidUtilities.dp(40.0f), AndroidUtilities.dp(120.0f));
                    break;
                case 11:
                    FlickerLoadingView flickerLoadingView2 = new FlickerLoadingView(this.mContext);
                    flickerLoadingView2.setIsSingleCell(true);
                    flickerLoadingView2.setViewType(6);
                    flickerLoadingView2.showDate(false);
                    flickerLoadingView2.setPaddingLeft(AndroidUtilities.dp(5.0f));
                    flickerLoadingView2.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    flickerLoadingView2.setLayoutParams(new RecyclerView.LayoutParams(-1, -1));
                    flickerLoadingView = flickerLoadingView2;
                    break;
            }
            return new RecyclerListView.Holder(flickerLoadingView);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onCreateViewHolder$1(int i) {
            if (ChatUsersActivity.this.info == null) {
                return;
            }
            ChatUsersActivity.this.selectedSlowmode = i;
        }

        /* JADX WARN: Code restructure failed: missing block: B:281:0x06af, code lost:
            if (r20.this$0.currentChat.megagroup == false) goto L316;
         */
        /* JADX WARN: Code restructure failed: missing block: B:282:0x06b1, code lost:
            r10 = true;
         */
        /* JADX WARN: Code restructure failed: missing block: B:290:0x06dd, code lost:
            if (r20.this$0.currentChat.megagroup == false) goto L316;
         */
        /* JADX WARN: Removed duplicated region for block: B:340:0x07f5  */
        /* JADX WARN: Removed duplicated region for block: B:341:0x07f8  */
        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder r21, int r22) {
            /*
                Method dump skipped, instructions count: 2085
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChatUsersActivity.ListAdapter.onBindViewHolder(androidx.recyclerview.widget.RecyclerView$ViewHolder, int):void");
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onViewRecycled(RecyclerView.ViewHolder viewHolder) {
            View view = viewHolder.itemView;
            if (view instanceof ManageChatUserCell) {
                ((ManageChatUserCell) view).recycle();
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int i) {
            if (i == ChatUsersActivity.this.addNewRow || i == ChatUsersActivity.this.addNew2Row || i == ChatUsersActivity.this.recentActionsRow || i == ChatUsersActivity.this.gigaConvertRow) {
                return 2;
            }
            if ((i >= ChatUsersActivity.this.participantsStartRow && i < ChatUsersActivity.this.participantsEndRow) || ((i >= ChatUsersActivity.this.botStartRow && i < ChatUsersActivity.this.botEndRow) || (i >= ChatUsersActivity.this.contactsStartRow && i < ChatUsersActivity.this.contactsEndRow))) {
                return 0;
            }
            if (i == ChatUsersActivity.this.addNewSectionRow || i == ChatUsersActivity.this.participantsDividerRow || i == ChatUsersActivity.this.participantsDivider2Row) {
                return 3;
            }
            if (i == ChatUsersActivity.this.restricted1SectionRow || i == ChatUsersActivity.this.permissionsSectionRow || i == ChatUsersActivity.this.slowmodeRow || i == ChatUsersActivity.this.gigaHeaderRow) {
                return 5;
            }
            if (i == ChatUsersActivity.this.participantsInfoRow || i == ChatUsersActivity.this.slowmodeInfoRow || i == ChatUsersActivity.this.gigaInfoRow) {
                return 1;
            }
            if (i == ChatUsersActivity.this.blockedEmptyRow) {
                return 4;
            }
            if (i == ChatUsersActivity.this.removedUsersRow) {
                return 6;
            }
            if (i == ChatUsersActivity.this.changeInfoRow || i == ChatUsersActivity.this.addUsersRow || i == ChatUsersActivity.this.pinMessagesRow || i == ChatUsersActivity.this.sendMessagesRow || i == ChatUsersActivity.this.sendMediaRow || i == ChatUsersActivity.this.sendStickersRow || i == ChatUsersActivity.this.embedLinksRow || i == ChatUsersActivity.this.sendPollsRow || i == ChatUsersActivity.this.manageTopicsRow) {
                return 7;
            }
            if (i == ChatUsersActivity.this.membersHeaderRow || i == ChatUsersActivity.this.contactsHeaderRow || i == ChatUsersActivity.this.botHeaderRow || i == ChatUsersActivity.this.loadingHeaderRow) {
                return 8;
            }
            if (i == ChatUsersActivity.this.slowmodeSelectRow) {
                return 9;
            }
            if (i == ChatUsersActivity.this.loadingProgressRow) {
                return 10;
            }
            return i == ChatUsersActivity.this.loadingUserCellRow ? 11 : 0;
        }

        public TLObject getItem(int i) {
            if (i < ChatUsersActivity.this.participantsStartRow || i >= ChatUsersActivity.this.participantsEndRow) {
                if (i < ChatUsersActivity.this.contactsStartRow || i >= ChatUsersActivity.this.contactsEndRow) {
                    if (i >= ChatUsersActivity.this.botStartRow && i < ChatUsersActivity.this.botEndRow) {
                        return (TLObject) ChatUsersActivity.this.bots.get(i - ChatUsersActivity.this.botStartRow);
                    }
                    return null;
                }
                return (TLObject) ChatUsersActivity.this.contacts.get(i - ChatUsersActivity.this.contactsStartRow);
            }
            return (TLObject) ChatUsersActivity.this.participants.get(i - ChatUsersActivity.this.participantsStartRow);
        }
    }

    public DiffCallback saveState() {
        DiffCallback diffCallback = new DiffCallback();
        diffCallback.oldRowCount = this.rowCount;
        diffCallback.oldBotStartRow = this.botStartRow;
        diffCallback.oldBotEndRow = this.botEndRow;
        diffCallback.oldBots.clear();
        diffCallback.oldBots.addAll(this.bots);
        diffCallback.oldContactsEndRow = this.contactsEndRow;
        diffCallback.oldContactsStartRow = this.contactsStartRow;
        diffCallback.oldContacts.clear();
        diffCallback.oldContacts.addAll(this.contacts);
        diffCallback.oldParticipantsStartRow = this.participantsStartRow;
        diffCallback.oldParticipantsEndRow = this.participantsEndRow;
        diffCallback.oldParticipants.clear();
        diffCallback.oldParticipants.addAll(this.participants);
        diffCallback.fillPositions(diffCallback.oldPositionToItem);
        return diffCallback;
    }

    public void updateListAnimated(DiffCallback diffCallback) {
        if (this.listViewAdapter == null) {
            updateRows();
            return;
        }
        updateRows();
        diffCallback.fillPositions(diffCallback.newPositionToItem);
        DiffUtil.calculateDiff(diffCallback).dispatchUpdatesTo(this.listViewAdapter);
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView == null || this.layoutManager == null || recyclerListView.getChildCount() <= 0) {
            return;
        }
        View view = null;
        int i = 0;
        int i2 = -1;
        while (true) {
            if (i >= this.listView.getChildCount()) {
                break;
            }
            RecyclerListView recyclerListView2 = this.listView;
            i2 = recyclerListView2.getChildAdapterPosition(recyclerListView2.getChildAt(i));
            if (i2 != -1) {
                view = this.listView.getChildAt(i);
                break;
            }
            i++;
        }
        if (view == null) {
            return;
        }
        this.layoutManager.scrollToPositionWithOffset(i2, view.getTop() - this.listView.getPaddingTop());
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public class DiffCallback extends DiffUtil.Callback {
        SparseIntArray newPositionToItem;
        int oldBotEndRow;
        int oldBotStartRow;
        private ArrayList<TLObject> oldBots;
        private ArrayList<TLObject> oldContacts;
        int oldContactsEndRow;
        int oldContactsStartRow;
        private ArrayList<TLObject> oldParticipants;
        int oldParticipantsEndRow;
        int oldParticipantsStartRow;
        SparseIntArray oldPositionToItem;
        int oldRowCount;

        private DiffCallback() {
            this.oldPositionToItem = new SparseIntArray();
            this.newPositionToItem = new SparseIntArray();
            this.oldParticipants = new ArrayList<>();
            this.oldBots = new ArrayList<>();
            this.oldContacts = new ArrayList<>();
        }

        @Override // androidx.recyclerview.widget.DiffUtil.Callback
        public int getOldListSize() {
            return this.oldRowCount;
        }

        @Override // androidx.recyclerview.widget.DiffUtil.Callback
        public int getNewListSize() {
            return ChatUsersActivity.this.rowCount;
        }

        @Override // androidx.recyclerview.widget.DiffUtil.Callback
        public boolean areItemsTheSame(int i, int i2) {
            if (i >= this.oldBotStartRow && i < this.oldBotEndRow && i2 >= ChatUsersActivity.this.botStartRow && i2 < ChatUsersActivity.this.botEndRow) {
                return this.oldBots.get(i - this.oldBotStartRow).equals(ChatUsersActivity.this.bots.get(i2 - ChatUsersActivity.this.botStartRow));
            }
            if (i >= this.oldContactsStartRow && i < this.oldContactsEndRow && i2 >= ChatUsersActivity.this.contactsStartRow && i2 < ChatUsersActivity.this.contactsEndRow) {
                return this.oldContacts.get(i - this.oldContactsStartRow).equals(ChatUsersActivity.this.contacts.get(i2 - ChatUsersActivity.this.contactsStartRow));
            }
            if (i >= this.oldParticipantsStartRow && i < this.oldParticipantsEndRow && i2 >= ChatUsersActivity.this.participantsStartRow && i2 < ChatUsersActivity.this.participantsEndRow) {
                return this.oldParticipants.get(i - this.oldParticipantsStartRow).equals(ChatUsersActivity.this.participants.get(i2 - ChatUsersActivity.this.participantsStartRow));
            }
            return this.oldPositionToItem.get(i) == this.newPositionToItem.get(i2);
        }

        @Override // androidx.recyclerview.widget.DiffUtil.Callback
        public boolean areContentsTheSame(int i, int i2) {
            return areItemsTheSame(i, i2) && ChatUsersActivity.this.restricted1SectionRow != i2;
        }

        public void fillPositions(SparseIntArray sparseIntArray) {
            sparseIntArray.clear();
            put(1, ChatUsersActivity.this.recentActionsRow, sparseIntArray);
            put(2, ChatUsersActivity.this.addNewRow, sparseIntArray);
            put(3, ChatUsersActivity.this.addNew2Row, sparseIntArray);
            put(4, ChatUsersActivity.this.addNewSectionRow, sparseIntArray);
            put(5, ChatUsersActivity.this.restricted1SectionRow, sparseIntArray);
            put(6, ChatUsersActivity.this.participantsDividerRow, sparseIntArray);
            put(7, ChatUsersActivity.this.participantsDivider2Row, sparseIntArray);
            put(8, ChatUsersActivity.this.gigaHeaderRow, sparseIntArray);
            put(9, ChatUsersActivity.this.gigaConvertRow, sparseIntArray);
            put(10, ChatUsersActivity.this.gigaInfoRow, sparseIntArray);
            put(11, ChatUsersActivity.this.participantsInfoRow, sparseIntArray);
            put(12, ChatUsersActivity.this.blockedEmptyRow, sparseIntArray);
            put(13, ChatUsersActivity.this.permissionsSectionRow, sparseIntArray);
            put(14, ChatUsersActivity.this.sendMessagesRow, sparseIntArray);
            put(15, ChatUsersActivity.this.sendMediaRow, sparseIntArray);
            put(16, ChatUsersActivity.this.sendStickersRow, sparseIntArray);
            put(17, ChatUsersActivity.this.sendPollsRow, sparseIntArray);
            put(18, ChatUsersActivity.this.embedLinksRow, sparseIntArray);
            put(19, ChatUsersActivity.this.addUsersRow, sparseIntArray);
            int i = 20;
            put(20, ChatUsersActivity.this.pinMessagesRow, sparseIntArray);
            if (ChatUsersActivity.this.isForum) {
                i = 21;
                put(21, ChatUsersActivity.this.manageTopicsRow, sparseIntArray);
            }
            int i2 = i + 1;
            put(i2, ChatUsersActivity.this.changeInfoRow, sparseIntArray);
            int i3 = i2 + 1;
            put(i3, ChatUsersActivity.this.removedUsersRow, sparseIntArray);
            int i4 = i3 + 1;
            put(i4, ChatUsersActivity.this.contactsHeaderRow, sparseIntArray);
            int i5 = i4 + 1;
            put(i5, ChatUsersActivity.this.botHeaderRow, sparseIntArray);
            int i6 = i5 + 1;
            put(i6, ChatUsersActivity.this.membersHeaderRow, sparseIntArray);
            int i7 = i6 + 1;
            put(i7, ChatUsersActivity.this.slowmodeRow, sparseIntArray);
            int i8 = i7 + 1;
            put(i8, ChatUsersActivity.this.slowmodeSelectRow, sparseIntArray);
            int i9 = i8 + 1;
            put(i9, ChatUsersActivity.this.slowmodeInfoRow, sparseIntArray);
            int i10 = i9 + 1;
            put(i10, ChatUsersActivity.this.loadingProgressRow, sparseIntArray);
            int i11 = i10 + 1;
            put(i11, ChatUsersActivity.this.loadingUserCellRow, sparseIntArray);
            put(i11 + 1, ChatUsersActivity.this.loadingHeaderRow, sparseIntArray);
        }

        private void put(int i, int i2, SparseIntArray sparseIntArray) {
            if (i2 >= 0) {
                sparseIntArray.put(i2, i);
            }
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate = new ThemeDescription.ThemeDescriptionDelegate() { // from class: org.telegram.ui.ChatUsersActivity$$ExternalSyntheticLambda16
            @Override // org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate
            public final void didSetColor() {
                ChatUsersActivity.this.lambda$getThemeDescriptions$18();
            }

            @Override // org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate
            public /* synthetic */ void onAnimationProgress(float f) {
                ThemeDescription.ThemeDescriptionDelegate.CC.$default$onAnimationProgress(this, f);
            }
        };
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{HeaderCell.class, ManageChatUserCell.class, ManageChatTextCell.class, TextCheckCell2.class, TextSettingsCell.class, SlideChooseView.class}, null, null, null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundGray"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, "divider"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, "windowBackgroundGrayShadow"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText4"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, "windowBackgroundGrayShadow"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueHeader"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{GraySectionCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "key_graySectionText"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{GraySectionCell.class}, null, null, null, "graySection"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteValueText"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell2.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell2.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText2"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell2.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switch2Track"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell2.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switch2TrackChecked"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{ManageChatUserCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{ManageChatUserCell.class}, new String[]{"statusColor"}, (Paint[]) null, (Drawable[]) null, themeDescriptionDelegate, "windowBackgroundWhiteGrayText"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{ManageChatUserCell.class}, new String[]{"statusOnlineColor"}, (Paint[]) null, (Drawable[]) null, themeDescriptionDelegate, "windowBackgroundWhiteBlueText"));
        arrayList.add(new ThemeDescription(this.undoView, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "undo_background"));
        arrayList.add(new ThemeDescription(this.undoView, 0, new Class[]{UndoView.class}, new String[]{"undoImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_cancelColor"));
        arrayList.add(new ThemeDescription(this.undoView, 0, new Class[]{UndoView.class}, new String[]{"undoTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_cancelColor"));
        arrayList.add(new ThemeDescription(this.undoView, 0, new Class[]{UndoView.class}, new String[]{"infoTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_infoColor"));
        arrayList.add(new ThemeDescription(this.undoView, 0, new Class[]{UndoView.class}, new String[]{"textPaint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_infoColor"));
        arrayList.add(new ThemeDescription(this.undoView, 0, new Class[]{UndoView.class}, new String[]{"progressPaint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_infoColor"));
        arrayList.add(new ThemeDescription(this.undoView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{UndoView.class}, new String[]{"leftImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_infoColor"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayIcon"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueButton"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueIcon"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{StickerEmptyView.class}, new String[]{"title"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{StickerEmptyView.class}, new String[]{"subtitle"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(this.emptyView.title, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(this.emptyView.subtitle, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{ManageChatUserCell.class}, null, Theme.avatarDrawables, null, "avatar_text"));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, "avatar_backgroundRed"));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, "avatar_backgroundOrange"));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, "avatar_backgroundViolet"));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, "avatar_backgroundGreen"));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, "avatar_backgroundCyan"));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, "avatar_backgroundBlue"));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, "avatar_backgroundPink"));
        return arrayList;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$getThemeDescriptions$18() {
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            int childCount = recyclerListView.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = this.listView.getChildAt(i);
                if (childAt instanceof ManageChatUserCell) {
                    ((ManageChatUserCell) childAt).update(0);
                }
            }
        }
    }
}
