package org.telegram.ui;

import android.animation.ValueAnimator;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Calendar;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.DialogRadioCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.PollEditTextCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCheckCell2;
import org.telegram.ui.Cells.TextDetailCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Cells.UserCell2;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.AnimatedTextView;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.CircularProgressDrawable;
import org.telegram.ui.Components.CrossfadeDrawable;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.Premium.LimitReachedBottomSheet;
import org.telegram.ui.Components.RecyclerListView;

public class ChatRightsEditActivity extends BaseFragment {
    private static final int MAX_RANK_LENGTH = 16;
    public static final int TYPE_ADD_BOT = 2;
    public static final int TYPE_ADMIN = 0;
    public static final int TYPE_BANNED = 1;
    private static final int done_button = 1;
    /* access modifiers changed from: private */
    public int addAdminsRow;
    /* access modifiers changed from: private */
    public FrameLayout addBotButton;
    /* access modifiers changed from: private */
    public FrameLayout addBotButtonContainer;
    /* access modifiers changed from: private */
    public int addBotButtonRow;
    /* access modifiers changed from: private */
    public AnimatedTextView addBotButtonText;
    /* access modifiers changed from: private */
    public int addUsersRow;
    /* access modifiers changed from: private */
    public TLRPC.TL_chatAdminRights adminRights;
    /* access modifiers changed from: private */
    public int anonymousRow;
    /* access modifiers changed from: private */
    public boolean asAdmin = false;
    private ValueAnimator asAdminAnimator;
    /* access modifiers changed from: private */
    public float asAdminT = 0.0f;
    /* access modifiers changed from: private */
    public int banUsersRow;
    /* access modifiers changed from: private */
    public TLRPC.TL_chatBannedRights bannedRights;
    private String botHash;
    /* access modifiers changed from: private */
    public boolean canEdit;
    /* access modifiers changed from: private */
    public int cantEditInfoRow;
    /* access modifiers changed from: private */
    public int changeInfoRow;
    private long chatId;
    private boolean closingKeyboardAfterFinish = false;
    private String currentBannedRights = "";
    /* access modifiers changed from: private */
    public TLRPC.Chat currentChat;
    /* access modifiers changed from: private */
    public String currentRank;
    /* access modifiers changed from: private */
    public int currentType;
    /* access modifiers changed from: private */
    public TLRPC.User currentUser;
    /* access modifiers changed from: private */
    public TLRPC.TL_chatBannedRights defaultBannedRights;
    private ChatRightsEditActivityDelegate delegate;
    /* access modifiers changed from: private */
    public int deleteMessagesRow;
    private CrossfadeDrawable doneDrawable;
    private ValueAnimator doneDrawableAnimator;
    /* access modifiers changed from: private */
    public int editMesagesRow;
    /* access modifiers changed from: private */
    public int embedLinksRow;
    private boolean initialAsAdmin = false;
    private boolean initialIsSet;
    private String initialRank;
    private boolean isAddingNew;
    /* access modifiers changed from: private */
    public boolean isChannel;
    private LinearLayoutManager linearLayoutManager;
    /* access modifiers changed from: private */
    public RecyclerListView listView;
    private ListAdapter listViewAdapter;
    /* access modifiers changed from: private */
    public boolean loading = false;
    /* access modifiers changed from: private */
    public int manageRow;
    /* access modifiers changed from: private */
    public TLRPC.TL_chatAdminRights myAdminRights;
    private int permissionsEndRow;
    private int permissionsStartRow;
    /* access modifiers changed from: private */
    public int pinMessagesRow;
    /* access modifiers changed from: private */
    public int postMessagesRow;
    /* access modifiers changed from: private */
    public PollEditTextCell rankEditTextCell;
    /* access modifiers changed from: private */
    public int rankHeaderRow;
    /* access modifiers changed from: private */
    public int rankInfoRow;
    /* access modifiers changed from: private */
    public int rankRow;
    /* access modifiers changed from: private */
    public int removeAdminRow;
    /* access modifiers changed from: private */
    public int removeAdminShadowRow;
    /* access modifiers changed from: private */
    public int rightsShadowRow;
    /* access modifiers changed from: private */
    public int rowCount;
    /* access modifiers changed from: private */
    public int sendMediaRow;
    /* access modifiers changed from: private */
    public int sendMessagesRow;
    /* access modifiers changed from: private */
    public int sendPollsRow;
    /* access modifiers changed from: private */
    public int sendStickersRow;
    /* access modifiers changed from: private */
    public int startVoiceChatRow;
    /* access modifiers changed from: private */
    public int transferOwnerRow;
    /* access modifiers changed from: private */
    public int transferOwnerShadowRow;
    /* access modifiers changed from: private */
    public int untilDateRow;
    /* access modifiers changed from: private */
    public int untilSectionRow;

    public interface ChatRightsEditActivityDelegate {
        void didChangeOwner(TLRPC.User user);

        void didSetRights(int i, TLRPC.TL_chatAdminRights tL_chatAdminRights, TLRPC.TL_chatBannedRights tL_chatBannedRights, String str);
    }

    public ChatRightsEditActivity(long userId, long channelId, TLRPC.TL_chatAdminRights rightsAdmin, TLRPC.TL_chatBannedRights rightsBannedDefault, TLRPC.TL_chatBannedRights rightsBanned, String rank, int type, boolean edit, boolean addingNew, String addingNewBotHash) {
        String rank2;
        boolean z;
        TLRPC.Chat chat;
        TLRPC.TL_chatAdminRights rightsAdmin2 = rightsAdmin;
        TLRPC.TL_chatBannedRights tL_chatBannedRights = rightsBannedDefault;
        TLRPC.TL_chatBannedRights tL_chatBannedRights2 = rightsBanned;
        int i = type;
        this.isAddingNew = addingNew;
        this.chatId = channelId;
        this.currentUser = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(userId));
        this.currentType = i;
        this.canEdit = edit;
        this.botHash = addingNewBotHash;
        TLRPC.Chat chat2 = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(this.chatId));
        this.currentChat = chat2;
        if (rank == null) {
            rank2 = "";
        } else {
            rank2 = rank;
        }
        this.currentRank = rank2;
        this.initialRank = rank2;
        boolean z2 = true;
        if (chat2 != null) {
            this.isChannel = ChatObject.isChannel(chat2) && !this.currentChat.megagroup;
            this.myAdminRights = this.currentChat.admin_rights;
        }
        if (this.myAdminRights == null) {
            this.myAdminRights = emptyAdminRights(this.currentType != 2 || ((chat = this.currentChat) != null && chat.creator));
        }
        if (i == 0 || i == 2) {
            if (i == 2) {
                TLRPC.UserFull userFull = getMessagesController().getUserFull(userId);
                if (userFull != null) {
                    TLRPC.TL_chatAdminRights botDefaultRights = this.isChannel ? userFull.bot_broadcast_admin_rights : userFull.bot_group_admin_rights;
                    if (botDefaultRights != null) {
                        if (rightsAdmin2 == null) {
                            rightsAdmin2 = botDefaultRights;
                        } else {
                            rightsAdmin2.ban_users = rightsAdmin2.ban_users || botDefaultRights.ban_users;
                            rightsAdmin2.add_admins = rightsAdmin2.add_admins || botDefaultRights.add_admins;
                            rightsAdmin2.post_messages = rightsAdmin2.post_messages || botDefaultRights.post_messages;
                            rightsAdmin2.pin_messages = rightsAdmin2.pin_messages || botDefaultRights.pin_messages;
                            rightsAdmin2.delete_messages = rightsAdmin2.delete_messages || botDefaultRights.delete_messages;
                            rightsAdmin2.change_info = rightsAdmin2.change_info || botDefaultRights.change_info;
                            rightsAdmin2.anonymous = rightsAdmin2.anonymous || botDefaultRights.anonymous;
                            rightsAdmin2.edit_messages = rightsAdmin2.edit_messages || botDefaultRights.edit_messages;
                            rightsAdmin2.manage_call = rightsAdmin2.manage_call || botDefaultRights.manage_call;
                            rightsAdmin2.other = rightsAdmin2.other || botDefaultRights.other;
                        }
                    }
                }
            } else {
                long j = userId;
            }
            float f = 1.0f;
            if (rightsAdmin2 == null) {
                this.initialAsAdmin = false;
                if (i == 2) {
                    this.adminRights = emptyAdminRights(false);
                    boolean z3 = this.isChannel;
                    this.asAdmin = z3;
                    this.asAdminT = !z3 ? 0.0f : f;
                    this.initialIsSet = false;
                } else {
                    TLRPC.TL_chatAdminRights tL_chatAdminRights = new TLRPC.TL_chatAdminRights();
                    this.adminRights = tL_chatAdminRights;
                    tL_chatAdminRights.change_info = this.myAdminRights.change_info;
                    this.adminRights.post_messages = this.myAdminRights.post_messages;
                    this.adminRights.edit_messages = this.myAdminRights.edit_messages;
                    this.adminRights.delete_messages = this.myAdminRights.delete_messages;
                    this.adminRights.manage_call = this.myAdminRights.manage_call;
                    this.adminRights.ban_users = this.myAdminRights.ban_users;
                    this.adminRights.invite_users = this.myAdminRights.invite_users;
                    this.adminRights.pin_messages = this.myAdminRights.pin_messages;
                    this.adminRights.other = this.myAdminRights.other;
                    this.initialIsSet = false;
                }
            } else {
                this.initialAsAdmin = true;
                TLRPC.TL_chatAdminRights tL_chatAdminRights2 = new TLRPC.TL_chatAdminRights();
                this.adminRights = tL_chatAdminRights2;
                tL_chatAdminRights2.change_info = rightsAdmin2.change_info;
                this.adminRights.post_messages = rightsAdmin2.post_messages;
                this.adminRights.edit_messages = rightsAdmin2.edit_messages;
                this.adminRights.delete_messages = rightsAdmin2.delete_messages;
                this.adminRights.manage_call = rightsAdmin2.manage_call;
                this.adminRights.ban_users = rightsAdmin2.ban_users;
                this.adminRights.invite_users = rightsAdmin2.invite_users;
                this.adminRights.pin_messages = rightsAdmin2.pin_messages;
                this.adminRights.add_admins = rightsAdmin2.add_admins;
                this.adminRights.anonymous = rightsAdmin2.anonymous;
                this.adminRights.other = rightsAdmin2.other;
                boolean z4 = this.adminRights.change_info || this.adminRights.post_messages || this.adminRights.edit_messages || this.adminRights.delete_messages || this.adminRights.ban_users || this.adminRights.invite_users || this.adminRights.pin_messages || this.adminRights.add_admins || this.adminRights.manage_call || this.adminRights.anonymous || this.adminRights.other;
                this.initialIsSet = z4;
                if (i == 2) {
                    boolean z5 = this.isChannel || z4;
                    this.asAdmin = z5;
                    this.asAdminT = !z5 ? 0.0f : f;
                    this.initialIsSet = false;
                }
            }
            TLRPC.Chat chat3 = this.currentChat;
            if (chat3 != null) {
                this.defaultBannedRights = chat3.default_banned_rights;
            }
            if (this.defaultBannedRights == null) {
                TLRPC.TL_chatBannedRights tL_chatBannedRights3 = new TLRPC.TL_chatBannedRights();
                this.defaultBannedRights = tL_chatBannedRights3;
                z = true;
                tL_chatBannedRights3.pin_messages = true;
                tL_chatBannedRights3.change_info = true;
                tL_chatBannedRights3.invite_users = true;
                tL_chatBannedRights3.send_polls = true;
                tL_chatBannedRights3.send_inline = true;
                tL_chatBannedRights3.send_games = true;
                tL_chatBannedRights3.send_gifs = true;
                tL_chatBannedRights3.send_stickers = true;
                tL_chatBannedRights3.embed_links = true;
                tL_chatBannedRights3.send_messages = true;
                tL_chatBannedRights3.send_media = true;
                tL_chatBannedRights3.view_messages = true;
            } else {
                z = true;
            }
            if (!this.defaultBannedRights.change_info) {
                this.adminRights.change_info = z;
            }
            if (!this.defaultBannedRights.pin_messages) {
                this.adminRights.pin_messages = z;
            }
        } else {
            if (i == 1) {
                this.defaultBannedRights = tL_chatBannedRights;
                if (tL_chatBannedRights == null) {
                    TLRPC.TL_chatBannedRights tL_chatBannedRights4 = new TLRPC.TL_chatBannedRights();
                    this.defaultBannedRights = tL_chatBannedRights4;
                    tL_chatBannedRights4.pin_messages = false;
                    tL_chatBannedRights4.change_info = false;
                    tL_chatBannedRights4.invite_users = false;
                    tL_chatBannedRights4.send_polls = false;
                    tL_chatBannedRights4.send_inline = false;
                    tL_chatBannedRights4.send_games = false;
                    tL_chatBannedRights4.send_gifs = false;
                    tL_chatBannedRights4.send_stickers = false;
                    tL_chatBannedRights4.embed_links = false;
                    tL_chatBannedRights4.send_messages = false;
                    tL_chatBannedRights4.send_media = false;
                    tL_chatBannedRights4.view_messages = false;
                }
                TLRPC.TL_chatBannedRights tL_chatBannedRights5 = new TLRPC.TL_chatBannedRights();
                this.bannedRights = tL_chatBannedRights5;
                if (tL_chatBannedRights2 == null) {
                    tL_chatBannedRights5.pin_messages = false;
                    tL_chatBannedRights5.change_info = false;
                    tL_chatBannedRights5.invite_users = false;
                    tL_chatBannedRights5.send_polls = false;
                    tL_chatBannedRights5.send_inline = false;
                    tL_chatBannedRights5.send_games = false;
                    tL_chatBannedRights5.send_gifs = false;
                    tL_chatBannedRights5.send_stickers = false;
                    tL_chatBannedRights5.embed_links = false;
                    tL_chatBannedRights5.send_messages = false;
                    tL_chatBannedRights5.send_media = false;
                    tL_chatBannedRights5.view_messages = false;
                } else {
                    tL_chatBannedRights5.view_messages = tL_chatBannedRights2.view_messages;
                    this.bannedRights.send_messages = tL_chatBannedRights2.send_messages;
                    this.bannedRights.send_media = tL_chatBannedRights2.send_media;
                    this.bannedRights.send_stickers = tL_chatBannedRights2.send_stickers;
                    this.bannedRights.send_gifs = tL_chatBannedRights2.send_gifs;
                    this.bannedRights.send_games = tL_chatBannedRights2.send_games;
                    this.bannedRights.send_inline = tL_chatBannedRights2.send_inline;
                    this.bannedRights.embed_links = tL_chatBannedRights2.embed_links;
                    this.bannedRights.send_polls = tL_chatBannedRights2.send_polls;
                    this.bannedRights.invite_users = tL_chatBannedRights2.invite_users;
                    this.bannedRights.change_info = tL_chatBannedRights2.change_info;
                    this.bannedRights.pin_messages = tL_chatBannedRights2.pin_messages;
                    this.bannedRights.until_date = tL_chatBannedRights2.until_date;
                }
                if (this.defaultBannedRights.view_messages) {
                    this.bannedRights.view_messages = true;
                }
                if (this.defaultBannedRights.send_messages) {
                    this.bannedRights.send_messages = true;
                }
                if (this.defaultBannedRights.send_media) {
                    this.bannedRights.send_media = true;
                }
                if (this.defaultBannedRights.send_stickers) {
                    this.bannedRights.send_stickers = true;
                }
                if (this.defaultBannedRights.send_gifs) {
                    this.bannedRights.send_gifs = true;
                }
                if (this.defaultBannedRights.send_games) {
                    this.bannedRights.send_games = true;
                }
                if (this.defaultBannedRights.send_inline) {
                    this.bannedRights.send_inline = true;
                }
                if (this.defaultBannedRights.embed_links) {
                    this.bannedRights.embed_links = true;
                }
                if (this.defaultBannedRights.send_polls) {
                    this.bannedRights.send_polls = true;
                }
                if (this.defaultBannedRights.invite_users) {
                    this.bannedRights.invite_users = true;
                }
                if (this.defaultBannedRights.change_info) {
                    this.bannedRights.change_info = true;
                }
                if (this.defaultBannedRights.pin_messages) {
                    this.bannedRights.pin_messages = true;
                }
                this.currentBannedRights = ChatObject.getBannedRightsString(this.bannedRights);
                if (tL_chatBannedRights2 != null && tL_chatBannedRights2.view_messages) {
                    z2 = false;
                }
                this.initialIsSet = z2;
            }
            long j2 = userId;
        }
        updateRows(false);
    }

    public static TLRPC.TL_chatAdminRights emptyAdminRights(boolean value) {
        TLRPC.TL_chatAdminRights adminRights2 = new TLRPC.TL_chatAdminRights();
        adminRights2.manage_call = value;
        adminRights2.add_admins = value;
        adminRights2.pin_messages = value;
        adminRights2.invite_users = value;
        adminRights2.ban_users = value;
        adminRights2.delete_messages = value;
        adminRights2.edit_messages = value;
        adminRights2.post_messages = value;
        adminRights2.change_info = value;
        return adminRights2;
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(NUM);
        int i = 1;
        this.actionBar.setAllowOverlayTitle(true);
        int i2 = this.currentType;
        if (i2 == 0) {
            this.actionBar.setTitle(LocaleController.getString("EditAdmin", NUM));
        } else if (i2 == 2) {
            this.actionBar.setTitle(LocaleController.getString("AddBot", NUM));
        } else {
            this.actionBar.setTitle(LocaleController.getString("UserRestrictions", NUM));
        }
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    if (ChatRightsEditActivity.this.checkDiscard()) {
                        ChatRightsEditActivity.this.finishFragment();
                    }
                } else if (id == 1) {
                    ChatRightsEditActivity.this.onDonePressed();
                }
            }
        });
        if (this.canEdit || (!this.isChannel && this.currentChat.creator && UserObject.isUserSelf(this.currentUser))) {
            ActionBarMenu menu = this.actionBar.createMenu();
            Drawable checkmark = context.getResources().getDrawable(NUM).mutate();
            checkmark.setColorFilter(new PorterDuffColorFilter(Theme.getColor("actionBarDefaultIcon"), PorterDuff.Mode.MULTIPLY));
            this.doneDrawable = new CrossfadeDrawable(checkmark, new CircularProgressDrawable(Theme.getColor("actionBarDefaultIcon")));
            menu.addItemWithWidth(1, 0, AndroidUtilities.dp(56.0f), (CharSequence) LocaleController.getString("Done", NUM));
            menu.getItem(1).setIcon((Drawable) this.doneDrawable);
        }
        this.fragmentView = new FrameLayout(context) {
            private int previousHeight = -1;

            /* access modifiers changed from: protected */
            public void onLayout(boolean changed, int left, int top, int right, int bottom) {
                super.onLayout(changed, left, top, right, bottom);
                int height = bottom - top;
                int i = this.previousHeight;
                if (i != -1 && Math.abs(i - height) > AndroidUtilities.dp(20.0f)) {
                    ChatRightsEditActivity.this.listView.smoothScrollToPosition(ChatRightsEditActivity.this.rowCount - 1);
                }
                this.previousHeight = height;
            }
        };
        this.fragmentView.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        this.fragmentView.setFocusableInTouchMode(true);
        AnonymousClass3 r4 = new RecyclerListView(context) {
            public boolean onTouchEvent(MotionEvent e) {
                if (ChatRightsEditActivity.this.loading) {
                    return false;
                }
                return super.onTouchEvent(e);
            }

            public boolean onInterceptTouchEvent(MotionEvent e) {
                if (ChatRightsEditActivity.this.loading) {
                    return false;
                }
                return super.onInterceptTouchEvent(e);
            }
        };
        this.listView = r4;
        r4.setClipChildren(this.currentType != 2);
        AnonymousClass4 r42 = new LinearLayoutManager(context, 1, false) {
            /* access modifiers changed from: protected */
            public int getExtraLayoutSpace(RecyclerView.State state) {
                return 5000;
            }
        };
        this.linearLayoutManager = r42;
        r42.setInitialPrefetchItemCount(100);
        this.listView.setLayoutManager(this.linearLayoutManager);
        RecyclerListView recyclerListView = this.listView;
        ListAdapter listAdapter = new ListAdapter(context);
        this.listViewAdapter = listAdapter;
        recyclerListView.setAdapter(listAdapter);
        DefaultItemAnimator itemAnimator = new DefaultItemAnimator();
        if (this.currentType == 2) {
            this.listView.setResetSelectorOnChanged(false);
        }
        itemAnimator.setDelayAnimations(false);
        this.listView.setItemAnimator(itemAnimator);
        RecyclerListView recyclerListView2 = this.listView;
        if (!LocaleController.isRTL) {
            i = 2;
        }
        recyclerListView2.setVerticalScrollbarPosition(i);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == 1) {
                    AndroidUtilities.hideKeyboard(ChatRightsEditActivity.this.getParentActivity().getCurrentFocus());
                }
            }
        });
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new ChatRightsEditActivity$$ExternalSyntheticLambda17(this, context));
        return this.fragmentView;
    }

    /* JADX WARNING: type inference failed for: r10v0 */
    /* JADX WARNING: type inference failed for: r10v10 */
    /* JADX WARNING: Incorrect type for immutable var: ssa=int, code=?, for r10v7, types: [int, boolean] */
    /* renamed from: lambda$createView$6$org-telegram-ui-ChatRightsEditActivity  reason: not valid java name */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void m3277lambda$createView$6$orgtelegramuiChatRightsEditActivity(android.content.Context r24, android.view.View r25, int r26) {
        /*
            r23 = this;
            r0 = r23
            r7 = r24
            r8 = r25
            r9 = r26
            boolean r1 = r0.canEdit
            if (r1 != 0) goto L_0x001b
            org.telegram.tgnet.TLRPC$Chat r1 = r0.currentChat
            boolean r1 = r1.creator
            if (r1 == 0) goto L_0x001a
            int r1 = r0.currentType
            if (r1 != 0) goto L_0x001a
            int r1 = r0.anonymousRow
            if (r9 == r1) goto L_0x001b
        L_0x001a:
            return
        L_0x001b:
            if (r9 != 0) goto L_0x0035
            android.os.Bundle r1 = new android.os.Bundle
            r1.<init>()
            org.telegram.tgnet.TLRPC$User r2 = r0.currentUser
            long r2 = r2.id
            java.lang.String r4 = "user_id"
            r1.putLong(r4, r2)
            org.telegram.ui.ProfileActivity r2 = new org.telegram.ui.ProfileActivity
            r2.<init>(r1)
            r0.presentFragment(r2)
            goto L_0x04c1
        L_0x0035:
            int r1 = r0.removeAdminRow
            r10 = 0
            r11 = 1
            if (r9 != r1) goto L_0x00b9
            int r1 = r0.currentType
            if (r1 != 0) goto L_0x0079
            int r1 = r0.currentAccount
            org.telegram.messenger.MessagesController r11 = org.telegram.messenger.MessagesController.getInstance(r1)
            long r12 = r0.chatId
            org.telegram.tgnet.TLRPC$User r14 = r0.currentUser
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r15 = new org.telegram.tgnet.TLRPC$TL_chatAdminRights
            r15.<init>()
            java.lang.String r1 = r0.currentRank
            boolean r2 = r0.isChannel
            org.telegram.ui.ActionBar.BaseFragment r18 = r0.getFragmentForAlert(r10)
            boolean r3 = r0.isAddingNew
            r20 = 0
            r21 = 0
            r22 = 0
            r16 = r1
            r17 = r2
            r19 = r3
            r11.setUserAdminRole(r12, r14, r15, r16, r17, r18, r19, r20, r21, r22)
            org.telegram.ui.ChatRightsEditActivity$ChatRightsEditActivityDelegate r1 = r0.delegate
            if (r1 == 0) goto L_0x0074
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r2 = r0.adminRights
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r3 = r0.bannedRights
            java.lang.String r4 = r0.currentRank
            r1.didSetRights(r10, r2, r3, r4)
        L_0x0074:
            r23.finishFragment()
            goto L_0x04c1
        L_0x0079:
            if (r1 != r11) goto L_0x04c1
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r1 = new org.telegram.tgnet.TLRPC$TL_chatBannedRights
            r1.<init>()
            r0.bannedRights = r1
            r1.view_messages = r11
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r1 = r0.bannedRights
            r1.send_media = r11
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r1 = r0.bannedRights
            r1.send_messages = r11
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r1 = r0.bannedRights
            r1.send_stickers = r11
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r1 = r0.bannedRights
            r1.send_gifs = r11
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r1 = r0.bannedRights
            r1.send_games = r11
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r1 = r0.bannedRights
            r1.send_inline = r11
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r1 = r0.bannedRights
            r1.embed_links = r11
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r1 = r0.bannedRights
            r1.pin_messages = r11
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r1 = r0.bannedRights
            r1.send_polls = r11
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r1 = r0.bannedRights
            r1.invite_users = r11
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r1 = r0.bannedRights
            r1.change_info = r11
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r1 = r0.bannedRights
            r1.until_date = r10
            r23.onDonePressed()
            goto L_0x04c1
        L_0x00b9:
            int r1 = r0.transferOwnerRow
            r2 = 0
            if (r9 != r1) goto L_0x00c3
            r0.m3285lambda$initTransfer$8$orgtelegramuiChatRightsEditActivity(r2, r2)
            goto L_0x04c1
        L_0x00c3:
            int r1 = r0.untilDateRow
            if (r9 != r1) goto L_0x019f
            android.app.Activity r1 = r23.getParentActivity()
            if (r1 != 0) goto L_0x00ce
            return
        L_0x00ce:
            org.telegram.ui.ActionBar.BottomSheet$Builder r1 = new org.telegram.ui.ActionBar.BottomSheet$Builder
            r1.<init>(r7)
            r12 = r1
            r12.setApplyTopPadding(r10)
            android.widget.LinearLayout r1 = new android.widget.LinearLayout
            r1.<init>(r7)
            r13 = r1
            r13.setOrientation(r11)
            org.telegram.ui.Cells.HeaderCell r14 = new org.telegram.ui.Cells.HeaderCell
            r4 = 23
            r5 = 15
            r6 = 0
            java.lang.String r3 = "dialogTextBlue2"
            r1 = r14
            r2 = r24
            r1.<init>(r2, r3, r4, r5, r6)
            r2 = 47
            r1.setHeight(r2)
            r2 = 2131628822(0x7f0e1316, float:1.8884948E38)
            java.lang.String r3 = "UserRestrictionsDuration"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            r13.addView(r1)
            android.widget.LinearLayout r2 = new android.widget.LinearLayout
            r2.<init>(r7)
            r2.setOrientation(r11)
            r3 = -1
            r4 = -2
            android.widget.LinearLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createLinear(r3, r4)
            r13.addView(r2, r5)
            r5 = 5
            org.telegram.ui.ActionBar.BottomSheet$BottomSheetCell[] r5 = new org.telegram.ui.ActionBar.BottomSheet.BottomSheetCell[r5]
            r6 = 0
        L_0x0118:
            int r14 = r5.length
            if (r6 >= r14) goto L_0x0193
            org.telegram.ui.ActionBar.BottomSheet$BottomSheetCell r14 = new org.telegram.ui.ActionBar.BottomSheet$BottomSheetCell
            r14.<init>(r7, r10)
            r5[r6] = r14
            r14 = r5[r6]
            r15 = 1088421888(0x40e00000, float:7.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r15)
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r15)
            r14.setPadding(r3, r10, r15, r10)
            r3 = r5[r6]
            java.lang.Integer r14 = java.lang.Integer.valueOf(r6)
            r3.setTag(r14)
            r3 = r5[r6]
            android.graphics.drawable.Drawable r14 = org.telegram.ui.ActionBar.Theme.getSelectorDrawable(r10)
            r3.setBackgroundDrawable(r14)
            switch(r6) {
                case 0: goto L_0x016b;
                case 1: goto L_0x0162;
                case 2: goto L_0x0159;
                case 3: goto L_0x0150;
                default: goto L_0x0146;
            }
        L_0x0146:
            r3 = 2131628820(0x7f0e1314, float:1.8884943E38)
            java.lang.String r14 = "UserRestrictionsCustom"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r14, r3)
            goto L_0x0175
        L_0x0150:
            java.lang.Object[] r3 = new java.lang.Object[r10]
            java.lang.String r14 = "Months"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatPluralString(r14, r11, r3)
            goto L_0x0175
        L_0x0159:
            java.lang.Object[] r3 = new java.lang.Object[r10]
            java.lang.String r14 = "Weeks"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatPluralString(r14, r11, r3)
            goto L_0x0175
        L_0x0162:
            java.lang.Object[] r3 = new java.lang.Object[r10]
            java.lang.String r14 = "Days"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatPluralString(r14, r11, r3)
            goto L_0x0175
        L_0x016b:
            r3 = 2131628840(0x7f0e1328, float:1.8884984E38)
            java.lang.String r14 = "UserRestrictionsUntilForever"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r14, r3)
        L_0x0175:
            r14 = r5[r6]
            r14.setTextAndIcon((java.lang.CharSequence) r3, (int) r10)
            r14 = r5[r6]
            r15 = -1
            android.widget.LinearLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createLinear(r15, r4)
            r2.addView(r14, r10)
            r10 = r5[r6]
            org.telegram.ui.ChatRightsEditActivity$$ExternalSyntheticLambda3 r14 = new org.telegram.ui.ChatRightsEditActivity$$ExternalSyntheticLambda3
            r14.<init>(r0, r12)
            r10.setOnClickListener(r14)
            int r6 = r6 + 1
            r3 = -1
            r10 = 0
            goto L_0x0118
        L_0x0193:
            r12.setCustomView(r13)
            org.telegram.ui.ActionBar.BottomSheet r3 = r12.create()
            r0.showDialog(r3)
            goto L_0x04c0
        L_0x019f:
            boolean r1 = r8 instanceof org.telegram.ui.Cells.TextCheckCell2
            if (r1 == 0) goto L_0x04c0
            r1 = r8
            org.telegram.ui.Cells.TextCheckCell2 r1 = (org.telegram.ui.Cells.TextCheckCell2) r1
            boolean r3 = r1.hasIcon()
            r4 = 2131627075(0x7f0e0CLASSNAME, float:1.8881404E38)
            java.lang.String r5 = "OK"
            r6 = 2131628816(0x7f0e1310, float:1.8884935E38)
            java.lang.String r10 = "UserRestrictionsCantModify"
            r12 = 2
            if (r3 == 0) goto L_0x01e9
            int r3 = r0.currentType
            if (r3 == r12) goto L_0x01e8
            org.telegram.ui.ActionBar.AlertDialog$Builder r3 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            android.app.Activity r11 = r23.getParentActivity()
            r3.<init>((android.content.Context) r11)
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r10, r6)
            org.telegram.ui.ActionBar.AlertDialog$Builder r3 = r3.setTitle(r6)
            r6 = 2131628817(0x7f0e1311, float:1.8884937E38)
            java.lang.String r10 = "UserRestrictionsCantModifyDisabled"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r10, r6)
            org.telegram.ui.ActionBar.AlertDialog$Builder r3 = r3.setMessage(r6)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            org.telegram.ui.ActionBar.AlertDialog$Builder r2 = r3.setPositiveButton(r4, r2)
            org.telegram.ui.ActionBar.AlertDialog r2 = r2.create()
            r2.show()
        L_0x01e8:
            return
        L_0x01e9:
            boolean r3 = r1.isEnabled()
            if (r3 != 0) goto L_0x023b
            int r3 = r0.currentType
            if (r3 == r12) goto L_0x01f5
            if (r3 != 0) goto L_0x023a
        L_0x01f5:
            int r3 = r0.changeInfoRow
            if (r9 != r3) goto L_0x0201
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r3 = r0.defaultBannedRights
            if (r3 == 0) goto L_0x0201
            boolean r3 = r3.change_info
            if (r3 == 0) goto L_0x020d
        L_0x0201:
            int r3 = r0.pinMessagesRow
            if (r9 != r3) goto L_0x023a
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r3 = r0.defaultBannedRights
            if (r3 == 0) goto L_0x023a
            boolean r3 = r3.pin_messages
            if (r3 != 0) goto L_0x023a
        L_0x020d:
            org.telegram.ui.ActionBar.AlertDialog$Builder r3 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            android.app.Activity r11 = r23.getParentActivity()
            r3.<init>((android.content.Context) r11)
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r10, r6)
            org.telegram.ui.ActionBar.AlertDialog$Builder r3 = r3.setTitle(r6)
            r6 = 2131628818(0x7f0e1312, float:1.888494E38)
            java.lang.String r10 = "UserRestrictionsCantModifyEnabled"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r10, r6)
            org.telegram.ui.ActionBar.AlertDialog$Builder r3 = r3.setMessage(r6)
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            org.telegram.ui.ActionBar.AlertDialog$Builder r2 = r3.setPositiveButton(r4, r2)
            org.telegram.ui.ActionBar.AlertDialog r2 = r2.create()
            r2.show()
        L_0x023a:
            return
        L_0x023b:
            int r2 = r0.currentType
            if (r2 == r12) goto L_0x0247
            boolean r2 = r1.isChecked()
            r2 = r2 ^ r11
            r1.setChecked(r2)
        L_0x0247:
            boolean r2 = r1.isChecked()
            int r3 = r0.manageRow
            if (r9 != r3) goto L_0x025b
            boolean r3 = r0.asAdmin
            r3 = r3 ^ r11
            r0.asAdmin = r3
            r2 = r3
            r0.updateAsAdmin(r11)
            r5 = 0
            goto L_0x04ac
        L_0x025b:
            int r3 = r0.changeInfoRow
            if (r9 != r3) goto L_0x027c
            int r3 = r0.currentType
            if (r3 == 0) goto L_0x0271
            if (r3 != r12) goto L_0x0266
            goto L_0x0271
        L_0x0266:
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r3 = r0.bannedRights
            boolean r4 = r3.change_info
            r4 = r4 ^ r11
            r3.change_info = r4
            r2 = r4
            r5 = 0
            goto L_0x04ac
        L_0x0271:
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r3 = r0.adminRights
            boolean r4 = r3.change_info
            r4 = r4 ^ r11
            r3.change_info = r4
            r2 = r4
            r5 = 0
            goto L_0x04ac
        L_0x027c:
            int r3 = r0.postMessagesRow
            if (r9 != r3) goto L_0x028b
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r3 = r0.adminRights
            boolean r4 = r3.post_messages
            r4 = r4 ^ r11
            r3.post_messages = r4
            r2 = r4
            r5 = 0
            goto L_0x04ac
        L_0x028b:
            int r3 = r0.editMesagesRow
            if (r9 != r3) goto L_0x029a
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r3 = r0.adminRights
            boolean r4 = r3.edit_messages
            r4 = r4 ^ r11
            r3.edit_messages = r4
            r2 = r4
            r5 = 0
            goto L_0x04ac
        L_0x029a:
            int r3 = r0.deleteMessagesRow
            if (r9 != r3) goto L_0x02a9
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r3 = r0.adminRights
            boolean r4 = r3.delete_messages
            r4 = r4 ^ r11
            r3.delete_messages = r4
            r2 = r4
            r5 = 0
            goto L_0x04ac
        L_0x02a9:
            int r3 = r0.addAdminsRow
            if (r9 != r3) goto L_0x02b8
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r3 = r0.adminRights
            boolean r4 = r3.add_admins
            r4 = r4 ^ r11
            r3.add_admins = r4
            r2 = r4
            r5 = 0
            goto L_0x04ac
        L_0x02b8:
            int r3 = r0.anonymousRow
            if (r9 != r3) goto L_0x02c7
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r3 = r0.adminRights
            boolean r4 = r3.anonymous
            r4 = r4 ^ r11
            r3.anonymous = r4
            r2 = r4
            r5 = 0
            goto L_0x04ac
        L_0x02c7:
            int r3 = r0.banUsersRow
            if (r9 != r3) goto L_0x02d6
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r3 = r0.adminRights
            boolean r4 = r3.ban_users
            r4 = r4 ^ r11
            r3.ban_users = r4
            r2 = r4
            r5 = 0
            goto L_0x04ac
        L_0x02d6:
            int r3 = r0.startVoiceChatRow
            if (r9 != r3) goto L_0x02e5
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r3 = r0.adminRights
            boolean r4 = r3.manage_call
            r4 = r4 ^ r11
            r3.manage_call = r4
            r2 = r4
            r5 = 0
            goto L_0x04ac
        L_0x02e5:
            int r3 = r0.addUsersRow
            if (r9 != r3) goto L_0x0306
            int r3 = r0.currentType
            if (r3 == 0) goto L_0x02fb
            if (r3 != r12) goto L_0x02f0
            goto L_0x02fb
        L_0x02f0:
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r3 = r0.bannedRights
            boolean r4 = r3.invite_users
            r4 = r4 ^ r11
            r3.invite_users = r4
            r2 = r4
            r5 = 0
            goto L_0x04ac
        L_0x02fb:
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r3 = r0.adminRights
            boolean r4 = r3.invite_users
            r4 = r4 ^ r11
            r3.invite_users = r4
            r2 = r4
            r5 = 0
            goto L_0x04ac
        L_0x0306:
            int r3 = r0.pinMessagesRow
            if (r9 != r3) goto L_0x0327
            int r3 = r0.currentType
            if (r3 == 0) goto L_0x031c
            if (r3 != r12) goto L_0x0311
            goto L_0x031c
        L_0x0311:
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r3 = r0.bannedRights
            boolean r4 = r3.pin_messages
            r4 = r4 ^ r11
            r3.pin_messages = r4
            r2 = r4
            r5 = 0
            goto L_0x04ac
        L_0x031c:
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r3 = r0.adminRights
            boolean r4 = r3.pin_messages
            r4 = r4 ^ r11
            r3.pin_messages = r4
            r2 = r4
            r5 = 0
            goto L_0x04ac
        L_0x0327:
            int r3 = r0.currentType
            if (r3 != r11) goto L_0x04ab
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r3 = r0.bannedRights
            if (r3 == 0) goto L_0x04ab
            boolean r3 = r1.isChecked()
            r3 = r3 ^ r11
            int r4 = r0.sendMessagesRow
            if (r9 != r4) goto L_0x0341
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r4 = r0.bannedRights
            boolean r5 = r4.send_messages
            r5 = r5 ^ r11
            r4.send_messages = r5
            r2 = r5
            goto L_0x037a
        L_0x0341:
            int r4 = r0.sendMediaRow
            if (r9 != r4) goto L_0x034e
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r4 = r0.bannedRights
            boolean r5 = r4.send_media
            r5 = r5 ^ r11
            r4.send_media = r5
            r2 = r5
            goto L_0x037a
        L_0x034e:
            int r4 = r0.sendStickersRow
            if (r9 != r4) goto L_0x0361
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r4 = r0.bannedRights
            boolean r5 = r4.send_stickers
            r5 = r5 ^ r11
            r4.send_inline = r5
            r4.send_gifs = r5
            r4.send_games = r5
            r4.send_stickers = r5
            r2 = r5
            goto L_0x037a
        L_0x0361:
            int r4 = r0.embedLinksRow
            if (r9 != r4) goto L_0x036e
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r4 = r0.bannedRights
            boolean r5 = r4.embed_links
            r5 = r5 ^ r11
            r4.embed_links = r5
            r2 = r5
            goto L_0x037a
        L_0x036e:
            int r4 = r0.sendPollsRow
            if (r9 != r4) goto L_0x037a
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r4 = r0.bannedRights
            boolean r5 = r4.send_polls
            r5 = r5 ^ r11
            r4.send_polls = r5
            r2 = r5
        L_0x037a:
            if (r3 == 0) goto L_0x0448
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r4 = r0.bannedRights
            boolean r4 = r4.view_messages
            if (r4 == 0) goto L_0x039e
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r4 = r0.bannedRights
            boolean r4 = r4.send_messages
            if (r4 != 0) goto L_0x039e
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r4 = r0.bannedRights
            r4.send_messages = r11
            org.telegram.ui.Components.RecyclerListView r4 = r0.listView
            int r5 = r0.sendMessagesRow
            androidx.recyclerview.widget.RecyclerView$ViewHolder r4 = r4.findViewHolderForAdapterPosition(r5)
            if (r4 == 0) goto L_0x039e
            android.view.View r5 = r4.itemView
            org.telegram.ui.Cells.TextCheckCell2 r5 = (org.telegram.ui.Cells.TextCheckCell2) r5
            r6 = 0
            r5.setChecked(r6)
        L_0x039e:
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r4 = r0.bannedRights
            boolean r4 = r4.view_messages
            if (r4 != 0) goto L_0x03aa
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r4 = r0.bannedRights
            boolean r4 = r4.send_messages
            if (r4 == 0) goto L_0x03c6
        L_0x03aa:
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r4 = r0.bannedRights
            boolean r4 = r4.send_media
            if (r4 != 0) goto L_0x03c6
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r4 = r0.bannedRights
            r4.send_media = r11
            org.telegram.ui.Components.RecyclerListView r4 = r0.listView
            int r5 = r0.sendMediaRow
            androidx.recyclerview.widget.RecyclerView$ViewHolder r4 = r4.findViewHolderForAdapterPosition(r5)
            if (r4 == 0) goto L_0x03c6
            android.view.View r5 = r4.itemView
            org.telegram.ui.Cells.TextCheckCell2 r5 = (org.telegram.ui.Cells.TextCheckCell2) r5
            r6 = 0
            r5.setChecked(r6)
        L_0x03c6:
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r4 = r0.bannedRights
            boolean r4 = r4.view_messages
            if (r4 != 0) goto L_0x03d2
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r4 = r0.bannedRights
            boolean r4 = r4.send_messages
            if (r4 == 0) goto L_0x03ee
        L_0x03d2:
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r4 = r0.bannedRights
            boolean r4 = r4.send_polls
            if (r4 != 0) goto L_0x03ee
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r4 = r0.bannedRights
            r4.send_polls = r11
            org.telegram.ui.Components.RecyclerListView r4 = r0.listView
            int r5 = r0.sendPollsRow
            androidx.recyclerview.widget.RecyclerView$ViewHolder r4 = r4.findViewHolderForAdapterPosition(r5)
            if (r4 == 0) goto L_0x03ee
            android.view.View r5 = r4.itemView
            org.telegram.ui.Cells.TextCheckCell2 r5 = (org.telegram.ui.Cells.TextCheckCell2) r5
            r6 = 0
            r5.setChecked(r6)
        L_0x03ee:
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r4 = r0.bannedRights
            boolean r4 = r4.view_messages
            if (r4 != 0) goto L_0x03fa
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r4 = r0.bannedRights
            boolean r4 = r4.send_messages
            if (r4 == 0) goto L_0x041c
        L_0x03fa:
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r4 = r0.bannedRights
            boolean r4 = r4.send_stickers
            if (r4 != 0) goto L_0x041c
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r4 = r0.bannedRights
            r4.send_inline = r11
            r4.send_gifs = r11
            r4.send_games = r11
            r4.send_stickers = r11
            org.telegram.ui.Components.RecyclerListView r4 = r0.listView
            int r5 = r0.sendStickersRow
            androidx.recyclerview.widget.RecyclerView$ViewHolder r4 = r4.findViewHolderForAdapterPosition(r5)
            if (r4 == 0) goto L_0x041c
            android.view.View r5 = r4.itemView
            org.telegram.ui.Cells.TextCheckCell2 r5 = (org.telegram.ui.Cells.TextCheckCell2) r5
            r6 = 0
            r5.setChecked(r6)
        L_0x041c:
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r4 = r0.bannedRights
            boolean r4 = r4.view_messages
            if (r4 != 0) goto L_0x0428
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r4 = r0.bannedRights
            boolean r4 = r4.send_messages
            if (r4 == 0) goto L_0x0446
        L_0x0428:
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r4 = r0.bannedRights
            boolean r4 = r4.embed_links
            if (r4 != 0) goto L_0x0446
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r4 = r0.bannedRights
            r4.embed_links = r11
            org.telegram.ui.Components.RecyclerListView r4 = r0.listView
            int r5 = r0.embedLinksRow
            androidx.recyclerview.widget.RecyclerView$ViewHolder r4 = r4.findViewHolderForAdapterPosition(r5)
            if (r4 == 0) goto L_0x0444
            android.view.View r5 = r4.itemView
            org.telegram.ui.Cells.TextCheckCell2 r5 = (org.telegram.ui.Cells.TextCheckCell2) r5
            r6 = 0
            r5.setChecked(r6)
        L_0x0444:
            r5 = 0
            goto L_0x04ac
        L_0x0446:
            r5 = 0
            goto L_0x04ac
        L_0x0448:
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r4 = r0.bannedRights
            boolean r4 = r4.send_messages
            if (r4 == 0) goto L_0x0466
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r4 = r0.bannedRights
            boolean r4 = r4.embed_links
            if (r4 == 0) goto L_0x0466
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r4 = r0.bannedRights
            boolean r4 = r4.send_inline
            if (r4 == 0) goto L_0x0466
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r4 = r0.bannedRights
            boolean r4 = r4.send_media
            if (r4 == 0) goto L_0x0466
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r4 = r0.bannedRights
            boolean r4 = r4.send_polls
            if (r4 != 0) goto L_0x0471
        L_0x0466:
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r4 = r0.bannedRights
            boolean r4 = r4.view_messages
            if (r4 == 0) goto L_0x0471
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r4 = r0.bannedRights
            r5 = 0
            r4.view_messages = r5
        L_0x0471:
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r4 = r0.bannedRights
            boolean r4 = r4.embed_links
            if (r4 == 0) goto L_0x048c
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r4 = r0.bannedRights
            boolean r4 = r4.send_inline
            if (r4 == 0) goto L_0x048c
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r4 = r0.bannedRights
            boolean r4 = r4.send_media
            if (r4 == 0) goto L_0x048c
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r4 = r0.bannedRights
            boolean r4 = r4.send_polls
            if (r4 != 0) goto L_0x048a
            goto L_0x048c
        L_0x048a:
            r5 = 0
            goto L_0x04ac
        L_0x048c:
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r4 = r0.bannedRights
            boolean r4 = r4.send_messages
            if (r4 == 0) goto L_0x04a9
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r4 = r0.bannedRights
            r5 = 0
            r4.send_messages = r5
            org.telegram.ui.Components.RecyclerListView r4 = r0.listView
            int r6 = r0.sendMessagesRow
            androidx.recyclerview.widget.RecyclerView$ViewHolder r4 = r4.findViewHolderForAdapterPosition(r6)
            if (r4 == 0) goto L_0x04ac
            android.view.View r6 = r4.itemView
            org.telegram.ui.Cells.TextCheckCell2 r6 = (org.telegram.ui.Cells.TextCheckCell2) r6
            r6.setChecked(r11)
            goto L_0x04ac
        L_0x04a9:
            r5 = 0
            goto L_0x04ac
        L_0x04ab:
            r5 = 0
        L_0x04ac:
            int r3 = r0.currentType
            if (r3 != r12) goto L_0x04bc
            boolean r3 = r0.asAdmin
            if (r3 == 0) goto L_0x04b8
            if (r2 == 0) goto L_0x04b8
            r10 = 1
            goto L_0x04b9
        L_0x04b8:
            r10 = 0
        L_0x04b9:
            r1.setChecked(r10)
        L_0x04bc:
            r0.updateRows(r11)
            goto L_0x04c1
        L_0x04c0:
        L_0x04c1:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChatRightsEditActivity.m3277lambda$createView$6$orgtelegramuiChatRightsEditActivity(android.content.Context, android.view.View, int):void");
    }

    /* renamed from: lambda$createView$5$org-telegram-ui-ChatRightsEditActivity  reason: not valid java name */
    public /* synthetic */ void m3276lambda$createView$5$orgtelegramuiChatRightsEditActivity(BottomSheet.Builder builder, View v2) {
        switch (((Integer) v2.getTag()).intValue()) {
            case 0:
                this.bannedRights.until_date = 0;
                this.listViewAdapter.notifyItemChanged(this.untilDateRow);
                break;
            case 1:
                this.bannedRights.until_date = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime() + 86400;
                this.listViewAdapter.notifyItemChanged(this.untilDateRow);
                break;
            case 2:
                this.bannedRights.until_date = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime() + 604800;
                this.listViewAdapter.notifyItemChanged(this.untilDateRow);
                break;
            case 3:
                this.bannedRights.until_date = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime() + 2592000;
                this.listViewAdapter.notifyItemChanged(this.untilDateRow);
                break;
            case 4:
                Calendar calendar = Calendar.getInstance();
                try {
                    DatePickerDialog datePickerDialog = new DatePickerDialog(getParentActivity(), new ChatRightsEditActivity$$ExternalSyntheticLambda19(this), calendar.get(1), calendar.get(2), calendar.get(5));
                    DatePicker datePicker = datePickerDialog.getDatePicker();
                    Calendar date = Calendar.getInstance();
                    date.setTimeInMillis(System.currentTimeMillis());
                    date.set(11, date.getMinimum(11));
                    date.set(12, date.getMinimum(12));
                    date.set(13, date.getMinimum(13));
                    date.set(14, date.getMinimum(14));
                    datePicker.setMinDate(date.getTimeInMillis());
                    date.setTimeInMillis(System.currentTimeMillis() + 31536000000L);
                    date.set(11, date.getMaximum(11));
                    date.set(12, date.getMaximum(12));
                    date.set(13, date.getMaximum(13));
                    date.set(14, date.getMaximum(14));
                    datePicker.setMaxDate(date.getTimeInMillis());
                    datePickerDialog.setButton(-1, LocaleController.getString("Set", NUM), datePickerDialog);
                    datePickerDialog.setButton(-2, LocaleController.getString("Cancel", NUM), ChatRightsEditActivity$$ExternalSyntheticLambda1.INSTANCE);
                    if (Build.VERSION.SDK_INT >= 21) {
                        datePickerDialog.setOnShowListener(new ChatRightsEditActivity$$ExternalSyntheticLambda2(datePicker));
                    }
                    showDialog(datePickerDialog);
                    break;
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                    break;
                }
        }
        builder.getDismissRunnable().run();
    }

    /* renamed from: lambda$createView$2$org-telegram-ui-ChatRightsEditActivity  reason: not valid java name */
    public /* synthetic */ void m3275lambda$createView$2$orgtelegramuiChatRightsEditActivity(DatePicker view1, int year1, int month, int dayOfMonth1) {
        Calendar calendar1 = Calendar.getInstance();
        calendar1.clear();
        calendar1.set(year1, month, dayOfMonth1);
        try {
            TimePickerDialog timePickerDialog = new TimePickerDialog(getParentActivity(), new ChatRightsEditActivity$$ExternalSyntheticLambda20(this, (int) (calendar1.getTime().getTime() / 1000)), 0, 0, true);
            timePickerDialog.setButton(-1, LocaleController.getString("Set", NUM), timePickerDialog);
            timePickerDialog.setButton(-2, LocaleController.getString("Cancel", NUM), ChatRightsEditActivity$$ExternalSyntheticLambda26.INSTANCE);
            showDialog(timePickerDialog);
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    /* renamed from: lambda$createView$0$org-telegram-ui-ChatRightsEditActivity  reason: not valid java name */
    public /* synthetic */ void m3274lambda$createView$0$orgtelegramuiChatRightsEditActivity(int time, TimePicker view11, int hourOfDay, int minute) {
        this.bannedRights.until_date = (hourOfDay * 3600) + time + (minute * 60);
        this.listViewAdapter.notifyItemChanged(this.untilDateRow);
    }

    static /* synthetic */ void lambda$createView$1(DialogInterface dialog131, int which) {
    }

    static /* synthetic */ void lambda$createView$3(DialogInterface dialog1, int which) {
    }

    static /* synthetic */ void lambda$createView$4(DatePicker datePicker, DialogInterface dialog12) {
        int count = datePicker.getChildCount();
        for (int b = 0; b < count; b++) {
            View child = datePicker.getChildAt(b);
            ViewGroup.LayoutParams layoutParams = child.getLayoutParams();
            layoutParams.width = -1;
            child.setLayoutParams(layoutParams);
        }
    }

    public void onResume() {
        super.onResume();
        ListAdapter listAdapter = this.listViewAdapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
    }

    private boolean isDefaultAdminRights() {
        return (this.adminRights.change_info && this.adminRights.delete_messages && this.adminRights.ban_users && this.adminRights.invite_users && this.adminRights.pin_messages && this.adminRights.manage_call && !this.adminRights.add_admins && !this.adminRights.anonymous) || (!this.adminRights.change_info && !this.adminRights.delete_messages && !this.adminRights.ban_users && !this.adminRights.invite_users && !this.adminRights.pin_messages && !this.adminRights.manage_call && !this.adminRights.add_admins && !this.adminRights.anonymous);
    }

    private boolean hasAllAdminRights() {
        if (this.isChannel) {
            if (!this.adminRights.change_info || !this.adminRights.post_messages || !this.adminRights.edit_messages || !this.adminRights.delete_messages || !this.adminRights.invite_users || !this.adminRights.add_admins || !this.adminRights.manage_call) {
                return false;
            }
            return true;
        } else if (!this.adminRights.change_info || !this.adminRights.delete_messages || !this.adminRights.ban_users || !this.adminRights.invite_users || !this.adminRights.pin_messages || !this.adminRights.add_admins || !this.adminRights.manage_call) {
            return false;
        } else {
            return true;
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: initTransfer */
    public void m3285lambda$initTransfer$8$orgtelegramuiChatRightsEditActivity(TLRPC.InputCheckPasswordSRP srp, TwoStepVerificationActivity passwordFragment) {
        if (getParentActivity() != null) {
            if (srp == null || ChatObject.isChannel(this.currentChat)) {
                TLRPC.TL_channels_editCreator req = new TLRPC.TL_channels_editCreator();
                if (ChatObject.isChannel(this.currentChat)) {
                    req.channel = new TLRPC.TL_inputChannel();
                    req.channel.channel_id = this.currentChat.id;
                    req.channel.access_hash = this.currentChat.access_hash;
                } else {
                    req.channel = new TLRPC.TL_inputChannelEmpty();
                }
                req.password = srp != null ? srp : new TLRPC.TL_inputCheckPasswordEmpty();
                req.user_id = getMessagesController().getInputUser(this.currentUser);
                getConnectionsManager().sendRequest(req, new ChatRightsEditActivity$$ExternalSyntheticLambda14(this, srp, passwordFragment, req));
                return;
            }
            MessagesController.getInstance(this.currentAccount).convertToMegaGroup(getParentActivity(), this.chatId, this, new ChatRightsEditActivity$$ExternalSyntheticLambda13(this, srp, passwordFragment));
        }
    }

    /* renamed from: lambda$initTransfer$7$org-telegram-ui-ChatRightsEditActivity  reason: not valid java name */
    public /* synthetic */ void m3284lambda$initTransfer$7$orgtelegramuiChatRightsEditActivity(TLRPC.InputCheckPasswordSRP srp, TwoStepVerificationActivity passwordFragment, long param) {
        if (param != 0) {
            this.chatId = param;
            this.currentChat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(param));
            m3285lambda$initTransfer$8$orgtelegramuiChatRightsEditActivity(srp, passwordFragment);
        }
    }

    /* renamed from: lambda$initTransfer$14$org-telegram-ui-ChatRightsEditActivity  reason: not valid java name */
    public /* synthetic */ void m3283lambda$initTransfer$14$orgtelegramuiChatRightsEditActivity(TLRPC.InputCheckPasswordSRP srp, TwoStepVerificationActivity passwordFragment, TLRPC.TL_channels_editCreator req, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new ChatRightsEditActivity$$ExternalSyntheticLambda7(this, error, srp, passwordFragment, req));
    }

    /* renamed from: lambda$initTransfer$13$org-telegram-ui-ChatRightsEditActivity  reason: not valid java name */
    public /* synthetic */ void m3282lambda$initTransfer$13$orgtelegramuiChatRightsEditActivity(TLRPC.TL_error error, TLRPC.InputCheckPasswordSRP srp, TwoStepVerificationActivity passwordFragment, TLRPC.TL_channels_editCreator req) {
        TLRPC.TL_error tL_error = error;
        TwoStepVerificationActivity twoStepVerificationActivity = passwordFragment;
        if (tL_error == null) {
            TLRPC.TL_channels_editCreator tL_channels_editCreator = req;
            if (srp != null) {
                this.delegate.didChangeOwner(this.currentUser);
                removeSelfFromStack();
                passwordFragment.needHideProgress();
                passwordFragment.finishFragment();
            }
        } else if (getParentActivity() != null) {
            if (!"PASSWORD_HASH_INVALID".equals(tL_error.text)) {
                if ("PASSWORD_MISSING".equals(tL_error.text) || tL_error.text.startsWith("PASSWORD_TOO_FRESH_")) {
                    TLRPC.TL_channels_editCreator tL_channels_editCreator2 = req;
                } else if (tL_error.text.startsWith("SESSION_TOO_FRESH_")) {
                    TLRPC.TL_channels_editCreator tL_channels_editCreator3 = req;
                } else if ("SRP_ID_INVALID".equals(tL_error.text)) {
                    ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC.TL_account_getPassword(), new ChatRightsEditActivity$$ExternalSyntheticLambda15(this, twoStepVerificationActivity), 8);
                    TLRPC.TL_channels_editCreator tL_channels_editCreator4 = req;
                    return;
                } else if (!tL_error.text.equals("CHANNELS_TOO_MUCH")) {
                    if (twoStepVerificationActivity != null) {
                        passwordFragment.needHideProgress();
                        passwordFragment.finishFragment();
                    }
                    AlertsCreator.showAddUserAlert(tL_error.text, this, this.isChannel, req);
                    return;
                } else if (getParentActivity() == null || AccountInstance.getInstance(this.currentAccount).getUserConfig().isPremium()) {
                    presentFragment(new TooManyCommunitiesActivity(1));
                    TLRPC.TL_channels_editCreator tL_channels_editCreator5 = req;
                    return;
                } else {
                    showDialog(new LimitReachedBottomSheet(this, getParentActivity(), 5, this.currentAccount));
                    TLRPC.TL_channels_editCreator tL_channels_editCreator6 = req;
                    return;
                }
                if (twoStepVerificationActivity != null) {
                    passwordFragment.needHideProgress();
                }
                AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
                builder.setTitle(LocaleController.getString("EditAdminTransferAlertTitle", NUM));
                LinearLayout linearLayout = new LinearLayout(getParentActivity());
                linearLayout.setPadding(AndroidUtilities.dp(24.0f), AndroidUtilities.dp(2.0f), AndroidUtilities.dp(24.0f), 0);
                linearLayout.setOrientation(1);
                builder.setView(linearLayout);
                TextView messageTextView = new TextView(getParentActivity());
                messageTextView.setTextColor(Theme.getColor("dialogTextBlack"));
                messageTextView.setTextSize(1, 16.0f);
                messageTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
                if (this.isChannel) {
                    messageTextView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("EditChannelAdminTransferAlertText", NUM, UserObject.getFirstName(this.currentUser))));
                } else {
                    messageTextView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("EditAdminTransferAlertText", NUM, UserObject.getFirstName(this.currentUser))));
                }
                linearLayout.addView(messageTextView, LayoutHelper.createLinear(-1, -2));
                LinearLayout linearLayout2 = new LinearLayout(getParentActivity());
                linearLayout2.setOrientation(0);
                linearLayout.addView(linearLayout2, LayoutHelper.createLinear(-1, -2, 0.0f, 11.0f, 0.0f, 0.0f));
                ImageView dotImageView = new ImageView(getParentActivity());
                dotImageView.setImageResource(NUM);
                dotImageView.setPadding(LocaleController.isRTL ? AndroidUtilities.dp(11.0f) : 0, AndroidUtilities.dp(9.0f), LocaleController.isRTL ? 0 : AndroidUtilities.dp(11.0f), 0);
                dotImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("dialogTextBlack"), PorterDuff.Mode.MULTIPLY));
                TextView messageTextView2 = new TextView(getParentActivity());
                messageTextView2.setTextColor(Theme.getColor("dialogTextBlack"));
                messageTextView2.setTextSize(1, 16.0f);
                messageTextView2.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
                messageTextView2.setText(AndroidUtilities.replaceTags(LocaleController.getString("EditAdminTransferAlertText1", NUM)));
                if (LocaleController.isRTL) {
                    linearLayout2.addView(messageTextView2, LayoutHelper.createLinear(-1, -2));
                    linearLayout2.addView(dotImageView, LayoutHelper.createLinear(-2, -2, 5));
                } else {
                    linearLayout2.addView(dotImageView, LayoutHelper.createLinear(-2, -2));
                    linearLayout2.addView(messageTextView2, LayoutHelper.createLinear(-1, -2));
                }
                LinearLayout linearLayout22 = new LinearLayout(getParentActivity());
                linearLayout22.setOrientation(0);
                linearLayout.addView(linearLayout22, LayoutHelper.createLinear(-1, -2, 0.0f, 11.0f, 0.0f, 0.0f));
                ImageView dotImageView2 = new ImageView(getParentActivity());
                dotImageView2.setImageResource(NUM);
                dotImageView2.setPadding(LocaleController.isRTL ? AndroidUtilities.dp(11.0f) : 0, AndroidUtilities.dp(9.0f), LocaleController.isRTL ? 0 : AndroidUtilities.dp(11.0f), 0);
                dotImageView2.setColorFilter(new PorterDuffColorFilter(Theme.getColor("dialogTextBlack"), PorterDuff.Mode.MULTIPLY));
                TextView messageTextView3 = new TextView(getParentActivity());
                messageTextView3.setTextColor(Theme.getColor("dialogTextBlack"));
                messageTextView3.setTextSize(1, 16.0f);
                messageTextView3.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
                messageTextView3.setText(AndroidUtilities.replaceTags(LocaleController.getString("EditAdminTransferAlertText2", NUM)));
                if (LocaleController.isRTL) {
                    linearLayout22.addView(messageTextView3, LayoutHelper.createLinear(-1, -2));
                    linearLayout22.addView(dotImageView2, LayoutHelper.createLinear(-2, -2, 5));
                } else {
                    linearLayout22.addView(dotImageView2, LayoutHelper.createLinear(-2, -2));
                    linearLayout22.addView(messageTextView3, LayoutHelper.createLinear(-1, -2));
                }
                if ("PASSWORD_MISSING".equals(tL_error.text)) {
                    builder.setPositiveButton(LocaleController.getString("EditAdminTransferSetPassword", NUM), new ChatRightsEditActivity$$ExternalSyntheticLambda23(this));
                    builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                } else {
                    TextView messageTextView4 = new TextView(getParentActivity());
                    messageTextView4.setTextColor(Theme.getColor("dialogTextBlack"));
                    messageTextView4.setTextSize(1, 16.0f);
                    messageTextView4.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
                    messageTextView4.setText(LocaleController.getString("EditAdminTransferAlertText3", NUM));
                    linearLayout.addView(messageTextView4, LayoutHelper.createLinear(-1, -2, 0.0f, 11.0f, 0.0f, 0.0f));
                    builder.setNegativeButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null);
                }
                showDialog(builder.create());
            } else if (srp == null) {
                AlertDialog.Builder builder2 = new AlertDialog.Builder((Context) getParentActivity());
                if (this.isChannel) {
                    builder2.setTitle(LocaleController.getString("EditAdminChannelTransfer", NUM));
                } else {
                    builder2.setTitle(LocaleController.getString("EditAdminGroupTransfer", NUM));
                }
                builder2.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("EditAdminTransferReadyAlertText", NUM, this.currentChat.title, UserObject.getFirstName(this.currentUser))));
                builder2.setPositiveButton(LocaleController.getString("EditAdminTransferChangeOwner", NUM), new ChatRightsEditActivity$$ExternalSyntheticLambda24(this));
                builder2.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                showDialog(builder2.create());
                TLRPC.TL_channels_editCreator tL_channels_editCreator7 = req;
            } else {
                TLRPC.TL_channels_editCreator tL_channels_editCreator8 = req;
            }
        }
    }

    /* renamed from: lambda$initTransfer$9$org-telegram-ui-ChatRightsEditActivity  reason: not valid java name */
    public /* synthetic */ void m3286lambda$initTransfer$9$orgtelegramuiChatRightsEditActivity(DialogInterface dialogInterface, int i) {
        TwoStepVerificationActivity fragment = new TwoStepVerificationActivity();
        fragment.setDelegate(new ChatRightsEditActivity$$ExternalSyntheticLambda18(this, fragment));
        presentFragment(fragment);
    }

    /* renamed from: lambda$initTransfer$10$org-telegram-ui-ChatRightsEditActivity  reason: not valid java name */
    public /* synthetic */ void m3279lambda$initTransfer$10$orgtelegramuiChatRightsEditActivity(DialogInterface dialogInterface, int i) {
        presentFragment(new TwoStepVerificationSetupActivity(6, (TLRPC.TL_account_password) null));
    }

    /* renamed from: lambda$initTransfer$12$org-telegram-ui-ChatRightsEditActivity  reason: not valid java name */
    public /* synthetic */ void m3281lambda$initTransfer$12$orgtelegramuiChatRightsEditActivity(TwoStepVerificationActivity passwordFragment, TLObject response2, TLRPC.TL_error error2) {
        AndroidUtilities.runOnUIThread(new ChatRightsEditActivity$$ExternalSyntheticLambda6(this, error2, response2, passwordFragment));
    }

    /* renamed from: lambda$initTransfer$11$org-telegram-ui-ChatRightsEditActivity  reason: not valid java name */
    public /* synthetic */ void m3280lambda$initTransfer$11$orgtelegramuiChatRightsEditActivity(TLRPC.TL_error error2, TLObject response2, TwoStepVerificationActivity passwordFragment) {
        if (error2 == null) {
            TLRPC.TL_account_password currentPassword = (TLRPC.TL_account_password) response2;
            passwordFragment.setCurrentPasswordInfo((byte[]) null, currentPassword);
            TwoStepVerificationActivity.initPasswordNewAlgo(currentPassword);
            m3285lambda$initTransfer$8$orgtelegramuiChatRightsEditActivity(passwordFragment.getNewSrpPassword(), passwordFragment);
        }
    }

    private void updateRows(boolean update) {
        int i;
        int transferOwnerShadowRowPrev = Math.min(this.transferOwnerShadowRow, this.transferOwnerRow);
        this.manageRow = -1;
        this.changeInfoRow = -1;
        this.postMessagesRow = -1;
        this.editMesagesRow = -1;
        this.deleteMessagesRow = -1;
        this.addAdminsRow = -1;
        this.anonymousRow = -1;
        this.banUsersRow = -1;
        this.addUsersRow = -1;
        this.pinMessagesRow = -1;
        this.rightsShadowRow = -1;
        this.removeAdminRow = -1;
        this.removeAdminShadowRow = -1;
        this.cantEditInfoRow = -1;
        this.transferOwnerShadowRow = -1;
        this.transferOwnerRow = -1;
        this.rankHeaderRow = -1;
        this.rankRow = -1;
        this.rankInfoRow = -1;
        this.sendMessagesRow = -1;
        this.sendMediaRow = -1;
        this.sendStickersRow = -1;
        this.sendPollsRow = -1;
        this.embedLinksRow = -1;
        this.startVoiceChatRow = -1;
        this.untilSectionRow = -1;
        this.untilDateRow = -1;
        this.addBotButtonRow = -1;
        this.rowCount = 3;
        this.permissionsStartRow = 3;
        int i2 = this.currentType;
        if (i2 == 0 || i2 == 2) {
            if (this.isChannel) {
                int i3 = 3 + 1;
                this.rowCount = i3;
                this.changeInfoRow = 3;
                int i4 = i3 + 1;
                this.rowCount = i4;
                this.postMessagesRow = i3;
                int i5 = i4 + 1;
                this.rowCount = i5;
                this.editMesagesRow = i4;
                int i6 = i5 + 1;
                this.rowCount = i6;
                this.deleteMessagesRow = i5;
                int i7 = i6 + 1;
                this.rowCount = i7;
                this.addUsersRow = i6;
                int i8 = i7 + 1;
                this.rowCount = i8;
                this.startVoiceChatRow = i7;
                this.rowCount = i8 + 1;
                this.addAdminsRow = i8;
            } else {
                if (i2 == 2) {
                    this.rowCount = 3 + 1;
                    this.manageRow = 3;
                }
                int i9 = this.rowCount;
                int i10 = i9 + 1;
                this.rowCount = i10;
                this.changeInfoRow = i9;
                int i11 = i10 + 1;
                this.rowCount = i11;
                this.deleteMessagesRow = i10;
                int i12 = i11 + 1;
                this.rowCount = i12;
                this.banUsersRow = i11;
                int i13 = i12 + 1;
                this.rowCount = i13;
                this.addUsersRow = i12;
                int i14 = i13 + 1;
                this.rowCount = i14;
                this.pinMessagesRow = i13;
                int i15 = i14 + 1;
                this.rowCount = i15;
                this.startVoiceChatRow = i14;
                int i16 = i15 + 1;
                this.rowCount = i16;
                this.addAdminsRow = i15;
                this.rowCount = i16 + 1;
                this.anonymousRow = i16;
            }
        } else if (i2 == 1) {
            int i17 = 3 + 1;
            this.rowCount = i17;
            this.sendMessagesRow = 3;
            int i18 = i17 + 1;
            this.rowCount = i18;
            this.sendMediaRow = i17;
            int i19 = i18 + 1;
            this.rowCount = i19;
            this.sendStickersRow = i18;
            int i20 = i19 + 1;
            this.rowCount = i20;
            this.sendPollsRow = i19;
            int i21 = i20 + 1;
            this.rowCount = i21;
            this.embedLinksRow = i20;
            int i22 = i21 + 1;
            this.rowCount = i22;
            this.addUsersRow = i21;
            int i23 = i22 + 1;
            this.rowCount = i23;
            this.pinMessagesRow = i22;
            int i24 = i23 + 1;
            this.rowCount = i24;
            this.changeInfoRow = i23;
            int i25 = i24 + 1;
            this.rowCount = i25;
            this.untilSectionRow = i24;
            this.rowCount = i25 + 1;
            this.untilDateRow = i25;
        }
        int i26 = this.rowCount;
        this.permissionsEndRow = i26;
        if (this.canEdit) {
            if (!this.isChannel && (i2 == 0 || (i2 == 2 && this.asAdmin))) {
                int i27 = i26 + 1;
                this.rowCount = i27;
                this.rightsShadowRow = i26;
                int i28 = i27 + 1;
                this.rowCount = i28;
                this.rankHeaderRow = i27;
                int i29 = i28 + 1;
                this.rowCount = i29;
                this.rankRow = i28;
                this.rowCount = i29 + 1;
                this.rankInfoRow = i29;
            }
            TLRPC.Chat chat = this.currentChat;
            if (chat != null && chat.creator && this.currentType == 0 && hasAllAdminRights() && !this.currentUser.bot) {
                int i30 = this.rightsShadowRow;
                if (i30 == -1) {
                    int i31 = this.rowCount;
                    this.rowCount = i31 + 1;
                    this.transferOwnerShadowRow = i31;
                }
                int i32 = this.rowCount;
                int i33 = i32 + 1;
                this.rowCount = i33;
                this.transferOwnerRow = i32;
                if (i30 != -1) {
                    this.rowCount = i33 + 1;
                    this.transferOwnerShadowRow = i33;
                }
            }
            if (this.initialIsSet) {
                if (this.rightsShadowRow == -1) {
                    int i34 = this.rowCount;
                    this.rowCount = i34 + 1;
                    this.rightsShadowRow = i34;
                }
                int i35 = this.rowCount;
                int i36 = i35 + 1;
                this.rowCount = i36;
                this.removeAdminRow = i35;
                this.rowCount = i36 + 1;
                this.removeAdminShadowRow = i36;
            }
        } else if (i2 != 0) {
            this.rowCount = i26 + 1;
            this.rightsShadowRow = i26;
        } else if (this.isChannel || (this.currentRank.isEmpty() && (!this.currentChat.creator || !UserObject.isUserSelf(this.currentUser)))) {
            int i37 = this.rowCount;
            this.rowCount = i37 + 1;
            this.cantEditInfoRow = i37;
        } else {
            int i38 = this.rowCount;
            int i39 = i38 + 1;
            this.rowCount = i39;
            this.rightsShadowRow = i38;
            int i40 = i39 + 1;
            this.rowCount = i40;
            this.rankHeaderRow = i39;
            this.rowCount = i40 + 1;
            this.rankRow = i40;
            if (!this.currentChat.creator || !UserObject.isUserSelf(this.currentUser)) {
                int i41 = this.rowCount;
                this.rowCount = i41 + 1;
                this.cantEditInfoRow = i41;
            } else {
                int i42 = this.rowCount;
                this.rowCount = i42 + 1;
                this.rankInfoRow = i42;
            }
        }
        if (this.currentType == 2) {
            int i43 = this.rowCount;
            this.rowCount = i43 + 1;
            this.addBotButtonRow = i43;
        }
        if (!update) {
            return;
        }
        if (transferOwnerShadowRowPrev == -1 && (i = this.transferOwnerShadowRow) != -1) {
            this.listViewAdapter.notifyItemRangeInserted(Math.min(i, this.transferOwnerRow), 2);
        } else if (transferOwnerShadowRowPrev != -1 && this.transferOwnerShadowRow == -1) {
            this.listViewAdapter.notifyItemRangeRemoved(transferOwnerShadowRowPrev, 2);
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x002d, code lost:
        if (r0.codePointCount(0, r0.length()) <= 16) goto L_0x002f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x003b, code lost:
        if (isDefaultAdminRights() == false) goto L_0x003d;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onDonePressed() {
        /*
            r15 = this;
            boolean r0 = r15.loading
            if (r0 == 0) goto L_0x0005
            return
        L_0x0005:
            org.telegram.tgnet.TLRPC$Chat r0 = r15.currentChat
            boolean r0 = org.telegram.messenger.ChatObject.isChannel(r0)
            r1 = 16
            r2 = -1
            r3 = 2
            r4 = 1
            r5 = 0
            if (r0 != 0) goto L_0x0053
            int r0 = r15.currentType
            if (r0 == r4) goto L_0x003d
            if (r0 != 0) goto L_0x002f
            boolean r0 = r15.isDefaultAdminRights()
            if (r0 == 0) goto L_0x003d
            int r0 = r15.rankRow
            if (r0 == r2) goto L_0x002f
            java.lang.String r0 = r15.currentRank
            int r6 = r0.length()
            int r0 = r0.codePointCount(r5, r6)
            if (r0 > r1) goto L_0x003d
        L_0x002f:
            int r0 = r15.currentType
            if (r0 != r3) goto L_0x0053
            java.lang.String r0 = r15.currentRank
            if (r0 != 0) goto L_0x003d
            boolean r0 = r15.isDefaultAdminRights()
            if (r0 != 0) goto L_0x0053
        L_0x003d:
            int r0 = r15.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r0)
            android.app.Activity r2 = r15.getParentActivity()
            long r3 = r15.chatId
            org.telegram.ui.ChatRightsEditActivity$$ExternalSyntheticLambda12 r6 = new org.telegram.ui.ChatRightsEditActivity$$ExternalSyntheticLambda12
            r6.<init>(r15)
            r5 = r15
            r1.convertToMegaGroup(r2, r3, r5, r6)
            return
        L_0x0053:
            int r0 = r15.currentType
            if (r0 == 0) goto L_0x0059
            if (r0 != r3) goto L_0x00eb
        L_0x0059:
            int r0 = r15.rankRow
            if (r0 == r2) goto L_0x0095
            java.lang.String r0 = r15.currentRank
            int r2 = r0.length()
            int r0 = r0.codePointCount(r5, r2)
            if (r0 <= r1) goto L_0x0095
            org.telegram.ui.Components.RecyclerListView r0 = r15.listView
            int r1 = r15.rankRow
            r0.smoothScrollToPosition(r1)
            android.app.Activity r0 = r15.getParentActivity()
            java.lang.String r1 = "vibrator"
            java.lang.Object r0 = r0.getSystemService(r1)
            android.os.Vibrator r0 = (android.os.Vibrator) r0
            if (r0 == 0) goto L_0x0083
            r1 = 200(0xc8, double:9.9E-322)
            r0.vibrate(r1)
        L_0x0083:
            org.telegram.ui.Components.RecyclerListView r1 = r15.listView
            int r2 = r15.rankHeaderRow
            androidx.recyclerview.widget.RecyclerView$ViewHolder r1 = r1.findViewHolderForAdapterPosition(r2)
            if (r1 == 0) goto L_0x0094
            android.view.View r2 = r1.itemView
            r3 = 1073741824(0x40000000, float:2.0)
            org.telegram.messenger.AndroidUtilities.shakeView(r2, r3, r5)
        L_0x0094:
            return
        L_0x0095:
            boolean r0 = r15.isChannel
            if (r0 == 0) goto L_0x00a0
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r0 = r15.adminRights
            r0.ban_users = r5
            r0.pin_messages = r5
            goto L_0x00a6
        L_0x00a0:
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r0 = r15.adminRights
            r0.edit_messages = r5
            r0.post_messages = r5
        L_0x00a6:
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r0 = r15.adminRights
            boolean r0 = r0.change_info
            if (r0 != 0) goto L_0x00e7
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r0 = r15.adminRights
            boolean r0 = r0.post_messages
            if (r0 != 0) goto L_0x00e7
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r0 = r15.adminRights
            boolean r0 = r0.edit_messages
            if (r0 != 0) goto L_0x00e7
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r0 = r15.adminRights
            boolean r0 = r0.delete_messages
            if (r0 != 0) goto L_0x00e7
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r0 = r15.adminRights
            boolean r0 = r0.ban_users
            if (r0 != 0) goto L_0x00e7
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r0 = r15.adminRights
            boolean r0 = r0.invite_users
            if (r0 != 0) goto L_0x00e7
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r0 = r15.adminRights
            boolean r0 = r0.pin_messages
            if (r0 != 0) goto L_0x00e7
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r0 = r15.adminRights
            boolean r0 = r0.add_admins
            if (r0 != 0) goto L_0x00e7
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r0 = r15.adminRights
            boolean r0 = r0.anonymous
            if (r0 != 0) goto L_0x00e7
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r0 = r15.adminRights
            boolean r0 = r0.manage_call
            if (r0 != 0) goto L_0x00e7
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r0 = r15.adminRights
            r0.other = r4
            goto L_0x00eb
        L_0x00e7:
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r0 = r15.adminRights
            r0.other = r5
        L_0x00eb:
            r0 = 1
            int r1 = r15.currentType
            if (r1 != 0) goto L_0x011d
            org.telegram.ui.ChatRightsEditActivity$ChatRightsEditActivityDelegate r1 = r15.delegate
            if (r1 != 0) goto L_0x00f5
            r5 = 1
        L_0x00f5:
            r0 = r5
            r15.setLoading(r4)
            int r1 = r15.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r1)
            long r3 = r15.chatId
            org.telegram.tgnet.TLRPC$User r5 = r15.currentUser
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r6 = r15.adminRights
            java.lang.String r7 = r15.currentRank
            boolean r8 = r15.isChannel
            boolean r10 = r15.isAddingNew
            r11 = 0
            r12 = 0
            org.telegram.ui.ChatRightsEditActivity$$ExternalSyntheticLambda4 r13 = new org.telegram.ui.ChatRightsEditActivity$$ExternalSyntheticLambda4
            r13.<init>(r15)
            org.telegram.ui.ChatRightsEditActivity$$ExternalSyntheticLambda8 r14 = new org.telegram.ui.ChatRightsEditActivity$$ExternalSyntheticLambda8
            r14.<init>(r15)
            r9 = r15
            r2.setUserAdminRole(r3, r5, r6, r7, r8, r9, r10, r11, r12, r13, r14)
            goto L_0x0225
        L_0x011d:
            if (r1 != r4) goto L_0x0176
            int r1 = r15.currentAccount
            org.telegram.messenger.MessagesController r6 = org.telegram.messenger.MessagesController.getInstance(r1)
            long r7 = r15.chatId
            org.telegram.tgnet.TLRPC$User r9 = r15.currentUser
            r10 = 0
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r11 = r15.bannedRights
            boolean r12 = r15.isChannel
            org.telegram.ui.ActionBar.BaseFragment r13 = r15.getFragmentForAlert(r4)
            r6.setParticipantBannedRole(r7, r9, r10, r11, r12, r13)
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r1 = r15.bannedRights
            boolean r1 = r1.send_messages
            if (r1 != 0) goto L_0x0166
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r1 = r15.bannedRights
            boolean r1 = r1.send_stickers
            if (r1 != 0) goto L_0x0166
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r1 = r15.bannedRights
            boolean r1 = r1.embed_links
            if (r1 != 0) goto L_0x0166
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r1 = r15.bannedRights
            boolean r1 = r1.send_media
            if (r1 != 0) goto L_0x0166
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r1 = r15.bannedRights
            boolean r1 = r1.send_gifs
            if (r1 != 0) goto L_0x0166
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r1 = r15.bannedRights
            boolean r1 = r1.send_games
            if (r1 != 0) goto L_0x0166
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r1 = r15.bannedRights
            boolean r1 = r1.send_inline
            if (r1 == 0) goto L_0x0160
            goto L_0x0166
        L_0x0160:
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r1 = r15.bannedRights
            r1.until_date = r5
            r1 = 2
            goto L_0x0167
        L_0x0166:
            r1 = 1
        L_0x0167:
            org.telegram.ui.ChatRightsEditActivity$ChatRightsEditActivityDelegate r2 = r15.delegate
            if (r2 == 0) goto L_0x0224
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r3 = r15.adminRights
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r4 = r15.bannedRights
            java.lang.String r5 = r15.currentRank
            r2.didSetRights(r1, r3, r4, r5)
            goto L_0x0224
        L_0x0176:
            if (r1 != r3) goto L_0x0224
            org.telegram.ui.ActionBar.AlertDialog$Builder r1 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            android.app.Activity r2 = r15.getParentActivity()
            r1.<init>((android.content.Context) r2)
            boolean r2 = r15.asAdmin
            r6 = 2131624250(0x7f0e013a, float:1.8875674E38)
            java.lang.String r7 = "AddBot"
            if (r2 == 0) goto L_0x0194
            r2 = 2131624251(0x7f0e013b, float:1.8875676E38)
            java.lang.String r8 = "AddBotAdmin"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r8, r2)
            goto L_0x0198
        L_0x0194:
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r7, r6)
        L_0x0198:
            r1.setTitle(r2)
            org.telegram.tgnet.TLRPC$Chat r2 = r15.currentChat
            boolean r2 = org.telegram.messenger.ChatObject.isChannel(r2)
            if (r2 == 0) goto L_0x01ab
            org.telegram.tgnet.TLRPC$Chat r2 = r15.currentChat
            boolean r2 = r2.megagroup
            if (r2 != 0) goto L_0x01ab
            r2 = 1
            goto L_0x01ac
        L_0x01ab:
            r2 = 0
        L_0x01ac:
            org.telegram.tgnet.TLRPC$Chat r8 = r15.currentChat
            if (r8 != 0) goto L_0x01b3
            java.lang.String r8 = ""
            goto L_0x01b5
        L_0x01b3:
            java.lang.String r8 = r8.title
        L_0x01b5:
            boolean r9 = r15.asAdmin
            if (r9 == 0) goto L_0x01d8
            if (r2 == 0) goto L_0x01ca
            r3 = 2131624256(0x7f0e0140, float:1.8875687E38)
            java.lang.Object[] r4 = new java.lang.Object[r4]
            r4[r5] = r8
            java.lang.String r5 = "AddBotMessageAdminChannel"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r5, r3, r4)
            goto L_0x01ed
        L_0x01ca:
            r3 = 2131624257(0x7f0e0141, float:1.8875689E38)
            java.lang.Object[] r4 = new java.lang.Object[r4]
            r4[r5] = r8
            java.lang.String r5 = "AddBotMessageAdminGroup"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r5, r3, r4)
            goto L_0x01ed
        L_0x01d8:
            r9 = 2131624271(0x7f0e014f, float:1.8875717E38)
            java.lang.Object[] r3 = new java.lang.Object[r3]
            org.telegram.tgnet.TLRPC$User r10 = r15.currentUser
            java.lang.String r10 = org.telegram.messenger.UserObject.getUserName(r10)
            r3[r5] = r10
            r3[r4] = r8
            java.lang.String r4 = "AddMembersAlertNamesText"
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r4, r9, r3)
        L_0x01ed:
            android.text.SpannableStringBuilder r3 = org.telegram.messenger.AndroidUtilities.replaceTags(r3)
            r1.setMessage(r3)
            r3 = 2131624819(0x7f0e0373, float:1.8876828E38)
            java.lang.String r4 = "Cancel"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r4 = 0
            r1.setNegativeButton(r3, r4)
            boolean r3 = r15.asAdmin
            if (r3 == 0) goto L_0x020f
            r3 = 2131624248(0x7f0e0138, float:1.887567E38)
            java.lang.String r4 = "AddAsAdmin"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            goto L_0x0213
        L_0x020f:
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r7, r6)
        L_0x0213:
            org.telegram.ui.ChatRightsEditActivity$$ExternalSyntheticLambda25 r4 = new org.telegram.ui.ChatRightsEditActivity$$ExternalSyntheticLambda25
            r4.<init>(r15)
            r1.setPositiveButton(r3, r4)
            org.telegram.ui.ActionBar.AlertDialog r3 = r1.create()
            r15.showDialog(r3)
            r0 = 0
            goto L_0x0225
        L_0x0224:
        L_0x0225:
            if (r0 == 0) goto L_0x022a
            r15.finishFragment()
        L_0x022a:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChatRightsEditActivity.onDonePressed():void");
    }

    /* renamed from: lambda$onDonePressed$15$org-telegram-ui-ChatRightsEditActivity  reason: not valid java name */
    public /* synthetic */ void m3287lambda$onDonePressed$15$orgtelegramuiChatRightsEditActivity(long param) {
        if (param != 0) {
            this.chatId = param;
            this.currentChat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(param));
            onDonePressed();
        }
    }

    /* renamed from: lambda$onDonePressed$16$org-telegram-ui-ChatRightsEditActivity  reason: not valid java name */
    public /* synthetic */ void m3288lambda$onDonePressed$16$orgtelegramuiChatRightsEditActivity() {
        ChatRightsEditActivityDelegate chatRightsEditActivityDelegate = this.delegate;
        if (chatRightsEditActivityDelegate != null) {
            chatRightsEditActivityDelegate.didSetRights((this.adminRights.change_info || this.adminRights.post_messages || this.adminRights.edit_messages || this.adminRights.delete_messages || this.adminRights.ban_users || this.adminRights.invite_users || this.adminRights.pin_messages || this.adminRights.add_admins || this.adminRights.anonymous || this.adminRights.manage_call || this.adminRights.other) ? 1 : 0, this.adminRights, this.bannedRights, this.currentRank);
            finishFragment();
        }
    }

    /* renamed from: lambda$onDonePressed$17$org-telegram-ui-ChatRightsEditActivity  reason: not valid java name */
    public /* synthetic */ boolean m3289lambda$onDonePressed$17$orgtelegramuiChatRightsEditActivity(TLRPC.TL_error err) {
        setLoading(false);
        return true;
    }

    /* renamed from: lambda$onDonePressed$21$org-telegram-ui-ChatRightsEditActivity  reason: not valid java name */
    public /* synthetic */ void m3293lambda$onDonePressed$21$orgtelegramuiChatRightsEditActivity(DialogInterface di, int i) {
        setLoading(true);
        ChatRightsEditActivity$$ExternalSyntheticLambda5 chatRightsEditActivity$$ExternalSyntheticLambda5 = new ChatRightsEditActivity$$ExternalSyntheticLambda5(this);
        if (this.asAdmin || this.initialAsAdmin) {
            getMessagesController().setUserAdminRole(this.currentChat.id, this.currentUser, this.asAdmin ? this.adminRights : emptyAdminRights(false), this.currentRank, false, this, this.isAddingNew, this.asAdmin, this.botHash, chatRightsEditActivity$$ExternalSyntheticLambda5, new ChatRightsEditActivity$$ExternalSyntheticLambda9(this));
            return;
        }
        getMessagesController().addUserToChat(this.currentChat.id, this.currentUser, 0, this.botHash, this, true, chatRightsEditActivity$$ExternalSyntheticLambda5, new ChatRightsEditActivity$$ExternalSyntheticLambda10(this));
    }

    /* renamed from: lambda$onDonePressed$18$org-telegram-ui-ChatRightsEditActivity  reason: not valid java name */
    public /* synthetic */ void m3290lambda$onDonePressed$18$orgtelegramuiChatRightsEditActivity() {
        ChatRightsEditActivityDelegate chatRightsEditActivityDelegate = this.delegate;
        if (chatRightsEditActivityDelegate != null) {
            chatRightsEditActivityDelegate.didSetRights(0, this.asAdmin ? this.adminRights : null, (TLRPC.TL_chatBannedRights) null, this.currentRank);
        }
        this.closingKeyboardAfterFinish = true;
        Bundle args1 = new Bundle();
        args1.putBoolean("scrollToTopOnResume", true);
        args1.putLong("chat_id", this.currentChat.id);
        if (!getMessagesController().checkCanOpenChat(args1, this)) {
            setLoading(false);
            return;
        }
        ChatActivity chatActivity = new ChatActivity(args1);
        presentFragment(chatActivity, true);
        if (BulletinFactory.canShowBulletin(chatActivity)) {
            boolean z = this.isAddingNew;
            if (z && this.asAdmin) {
                BulletinFactory.createAddedAsAdminBulletin(chatActivity, this.currentUser.first_name).show();
            } else if (!z && !this.initialAsAdmin && this.asAdmin) {
                BulletinFactory.createPromoteToAdminBulletin(chatActivity, this.currentUser.first_name).show();
            }
        }
    }

    /* renamed from: lambda$onDonePressed$19$org-telegram-ui-ChatRightsEditActivity  reason: not valid java name */
    public /* synthetic */ boolean m3291lambda$onDonePressed$19$orgtelegramuiChatRightsEditActivity(TLRPC.TL_error err) {
        setLoading(false);
        return true;
    }

    /* renamed from: lambda$onDonePressed$20$org-telegram-ui-ChatRightsEditActivity  reason: not valid java name */
    public /* synthetic */ boolean m3292lambda$onDonePressed$20$orgtelegramuiChatRightsEditActivity(TLRPC.TL_error err) {
        setLoading(false);
        return true;
    }

    public void setLoading(boolean enable) {
        ValueAnimator valueAnimator = this.doneDrawableAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        this.loading = !enable;
        this.actionBar.getBackButton().setEnabled(!enable);
        CrossfadeDrawable crossfadeDrawable = this.doneDrawable;
        if (crossfadeDrawable != null) {
            float[] fArr = new float[2];
            fArr[0] = crossfadeDrawable.getProgress();
            fArr[1] = enable ? 1.0f : 0.0f;
            ValueAnimator ofFloat = ValueAnimator.ofFloat(fArr);
            this.doneDrawableAnimator = ofFloat;
            ofFloat.addUpdateListener(new ChatRightsEditActivity$$ExternalSyntheticLambda0(this));
            this.doneDrawableAnimator.setDuration((long) (Math.abs(this.doneDrawable.getProgress() - (enable ? 1.0f : 0.0f)) * 150.0f));
            this.doneDrawableAnimator.start();
        }
    }

    /* renamed from: lambda$setLoading$22$org-telegram-ui-ChatRightsEditActivity  reason: not valid java name */
    public /* synthetic */ void m3294lambda$setLoading$22$orgtelegramuiChatRightsEditActivity(ValueAnimator a) {
        this.doneDrawable.setProgress(((Float) a.getAnimatedValue()).floatValue());
        this.doneDrawable.invalidateSelf();
    }

    public void setDelegate(ChatRightsEditActivityDelegate channelRightsEditActivityDelegate) {
        this.delegate = channelRightsEditActivityDelegate;
    }

    /* access modifiers changed from: private */
    public boolean checkDiscard() {
        boolean changed;
        int i = this.currentType;
        if (i == 2) {
            return true;
        }
        if (i == 1) {
            changed = !this.currentBannedRights.equals(ChatObject.getBannedRightsString(this.bannedRights));
        } else {
            changed = !this.initialRank.equals(this.currentRank);
        }
        if (!changed) {
            return true;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
        builder.setTitle(LocaleController.getString("UserRestrictionsApplyChanges", NUM));
        builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("UserRestrictionsApplyChangesText", NUM, MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(this.chatId)).title)));
        builder.setPositiveButton(LocaleController.getString("ApplyTheme", NUM), new ChatRightsEditActivity$$ExternalSyntheticLambda21(this));
        builder.setNegativeButton(LocaleController.getString("PassportDiscard", NUM), new ChatRightsEditActivity$$ExternalSyntheticLambda22(this));
        showDialog(builder.create());
        return false;
    }

    /* renamed from: lambda$checkDiscard$23$org-telegram-ui-ChatRightsEditActivity  reason: not valid java name */
    public /* synthetic */ void m3272lambda$checkDiscard$23$orgtelegramuiChatRightsEditActivity(DialogInterface dialogInterface, int i) {
        onDonePressed();
    }

    /* renamed from: lambda$checkDiscard$24$org-telegram-ui-ChatRightsEditActivity  reason: not valid java name */
    public /* synthetic */ void m3273lambda$checkDiscard$24$orgtelegramuiChatRightsEditActivity(DialogInterface dialog, int which) {
        finishFragment();
    }

    /* access modifiers changed from: private */
    public void setTextLeft(View cell) {
        if (cell instanceof HeaderCell) {
            HeaderCell headerCell = (HeaderCell) cell;
            String str = this.currentRank;
            int left = 16 - (str != null ? str.codePointCount(0, str.length()) : 0);
            if (((float) left) <= 4.8f) {
                headerCell.setText2(String.format("%d", new Object[]{Integer.valueOf(left)}));
                SimpleTextView textView = headerCell.getTextView2();
                String key = left < 0 ? "windowBackgroundWhiteRedText5" : "windowBackgroundWhiteGrayText3";
                textView.setTextColor(Theme.getColor(key));
                textView.setTag(key);
                return;
            }
            headerCell.setText2("");
        }
    }

    public boolean onBackPressed() {
        return checkDiscard();
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private final int VIEW_TYPE_ADD_BOT_CELL = 8;
        private final int VIEW_TYPE_HEADER_CELL = 3;
        private final int VIEW_TYPE_INFO_CELL = 1;
        private final int VIEW_TYPE_RANK_CELL = 7;
        private final int VIEW_TYPE_SHADOW_CELL = 5;
        private final int VIEW_TYPE_SWITCH_CELL = 4;
        private final int VIEW_TYPE_TRANSFER_CELL = 2;
        private final int VIEW_TYPE_UNTIL_DATE_CELL = 6;
        private final int VIEW_TYPE_USER_CELL = 0;
        /* access modifiers changed from: private */
        public boolean ignoreTextChange;
        private Context mContext;

        public ListAdapter(Context context) {
            if (ChatRightsEditActivity.this.currentType == 2) {
                setHasStableIds(true);
            }
            this.mContext = context;
        }

        public long getItemId(int position) {
            if (ChatRightsEditActivity.this.currentType != 2) {
                return super.getItemId(position);
            }
            if (position == ChatRightsEditActivity.this.manageRow) {
                return 1;
            }
            if (position == ChatRightsEditActivity.this.changeInfoRow) {
                return 2;
            }
            if (position == ChatRightsEditActivity.this.postMessagesRow) {
                return 3;
            }
            if (position == ChatRightsEditActivity.this.editMesagesRow) {
                return 4;
            }
            if (position == ChatRightsEditActivity.this.deleteMessagesRow) {
                return 5;
            }
            if (position == ChatRightsEditActivity.this.addAdminsRow) {
                return 6;
            }
            if (position == ChatRightsEditActivity.this.anonymousRow) {
                return 7;
            }
            if (position == ChatRightsEditActivity.this.banUsersRow) {
                return 8;
            }
            if (position == ChatRightsEditActivity.this.addUsersRow) {
                return 9;
            }
            if (position == ChatRightsEditActivity.this.pinMessagesRow) {
                return 10;
            }
            if (position == ChatRightsEditActivity.this.rightsShadowRow) {
                return 11;
            }
            if (position == ChatRightsEditActivity.this.removeAdminRow) {
                return 12;
            }
            if (position == ChatRightsEditActivity.this.removeAdminShadowRow) {
                return 13;
            }
            if (position == ChatRightsEditActivity.this.cantEditInfoRow) {
                return 14;
            }
            if (position == ChatRightsEditActivity.this.transferOwnerShadowRow) {
                return 15;
            }
            if (position == ChatRightsEditActivity.this.transferOwnerRow) {
                return 16;
            }
            if (position == ChatRightsEditActivity.this.rankHeaderRow) {
                return 17;
            }
            if (position == ChatRightsEditActivity.this.rankRow) {
                return 18;
            }
            if (position == ChatRightsEditActivity.this.rankInfoRow) {
                return 19;
            }
            if (position == ChatRightsEditActivity.this.sendMessagesRow) {
                return 20;
            }
            if (position == ChatRightsEditActivity.this.sendMediaRow) {
                return 21;
            }
            if (position == ChatRightsEditActivity.this.sendStickersRow) {
                return 22;
            }
            if (position == ChatRightsEditActivity.this.sendPollsRow) {
                return 23;
            }
            if (position == ChatRightsEditActivity.this.embedLinksRow) {
                return 24;
            }
            if (position == ChatRightsEditActivity.this.startVoiceChatRow) {
                return 25;
            }
            if (position == ChatRightsEditActivity.this.untilSectionRow) {
                return 26;
            }
            if (position == ChatRightsEditActivity.this.untilDateRow) {
                return 27;
            }
            if (position == ChatRightsEditActivity.this.addBotButtonRow) {
                return 28;
            }
            return 0;
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int type = holder.getItemViewType();
            if (ChatRightsEditActivity.this.currentChat.creator && ((ChatRightsEditActivity.this.currentType == 0 || (ChatRightsEditActivity.this.currentType == 2 && ChatRightsEditActivity.this.asAdmin)) && type == 4 && holder.getAdapterPosition() == ChatRightsEditActivity.this.anonymousRow)) {
                return true;
            }
            if (!ChatRightsEditActivity.this.canEdit) {
                return false;
            }
            if ((ChatRightsEditActivity.this.currentType == 0 || ChatRightsEditActivity.this.currentType == 2) && type == 4) {
                int position = holder.getAdapterPosition();
                if (position == ChatRightsEditActivity.this.manageRow) {
                    if (ChatRightsEditActivity.this.myAdminRights.add_admins) {
                        return true;
                    }
                    if (ChatRightsEditActivity.this.currentChat == null || !ChatRightsEditActivity.this.currentChat.creator) {
                        return false;
                    }
                    return true;
                } else if (ChatRightsEditActivity.this.currentType == 2 && !ChatRightsEditActivity.this.asAdmin) {
                    return false;
                } else {
                    if (position == ChatRightsEditActivity.this.changeInfoRow) {
                        if (!ChatRightsEditActivity.this.myAdminRights.change_info || (ChatRightsEditActivity.this.defaultBannedRights != null && !ChatRightsEditActivity.this.defaultBannedRights.change_info)) {
                            return false;
                        }
                        return true;
                    } else if (position == ChatRightsEditActivity.this.postMessagesRow) {
                        return ChatRightsEditActivity.this.myAdminRights.post_messages;
                    } else {
                        if (position == ChatRightsEditActivity.this.editMesagesRow) {
                            return ChatRightsEditActivity.this.myAdminRights.edit_messages;
                        }
                        if (position == ChatRightsEditActivity.this.deleteMessagesRow) {
                            return ChatRightsEditActivity.this.myAdminRights.delete_messages;
                        }
                        if (position == ChatRightsEditActivity.this.startVoiceChatRow) {
                            return ChatRightsEditActivity.this.myAdminRights.manage_call;
                        }
                        if (position == ChatRightsEditActivity.this.addAdminsRow) {
                            return ChatRightsEditActivity.this.myAdminRights.add_admins;
                        }
                        if (position == ChatRightsEditActivity.this.anonymousRow) {
                            return ChatRightsEditActivity.this.myAdminRights.anonymous;
                        }
                        if (position == ChatRightsEditActivity.this.banUsersRow) {
                            return ChatRightsEditActivity.this.myAdminRights.ban_users;
                        }
                        if (position == ChatRightsEditActivity.this.addUsersRow) {
                            return ChatRightsEditActivity.this.myAdminRights.invite_users;
                        }
                        if (position == ChatRightsEditActivity.this.pinMessagesRow) {
                            if (!ChatRightsEditActivity.this.myAdminRights.pin_messages || (ChatRightsEditActivity.this.defaultBannedRights != null && !ChatRightsEditActivity.this.defaultBannedRights.pin_messages)) {
                                return false;
                            }
                            return true;
                        }
                    }
                }
            }
            if (type == 3 || type == 1 || type == 5 || type == 8) {
                return false;
            }
            return true;
        }

        public int getItemCount() {
            return ChatRightsEditActivity.this.rowCount;
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v1, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v2, resolved type: org.telegram.ui.Cells.UserCell2} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v4, resolved type: org.telegram.ui.Cells.TextInfoPrivacyCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v6, resolved type: org.telegram.ui.Cells.TextCheckCell2} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v8, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v9, resolved type: org.telegram.ui.Cells.TextDetailCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v11, resolved type: org.telegram.ui.Cells.PollEditTextCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v15, resolved type: android.widget.FrameLayout} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v16, resolved type: org.telegram.ui.Cells.TextSettingsCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v17, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v18, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v16, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v19, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v20, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v21, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v22, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v23, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX WARNING: type inference failed for: r1v8, types: [org.telegram.ui.Cells.ShadowSectionCell] */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public androidx.recyclerview.widget.RecyclerView.ViewHolder onCreateViewHolder(android.view.ViewGroup r17, int r18) {
            /*
                r16 = this;
                r0 = r16
                java.lang.String r1 = "windowBackgroundWhite"
                r2 = 0
                switch(r18) {
                    case 0: goto L_0x01d5;
                    case 1: goto L_0x01be;
                    case 2: goto L_0x0008;
                    case 3: goto L_0x01a7;
                    case 4: goto L_0x0198;
                    case 5: goto L_0x018f;
                    case 6: goto L_0x0180;
                    case 7: goto L_0x0161;
                    case 8: goto L_0x0018;
                    default: goto L_0x0008;
                }
            L_0x0008:
                org.telegram.ui.Cells.TextSettingsCell r2 = new org.telegram.ui.Cells.TextSettingsCell
                android.content.Context r3 = r0.mContext
                r2.<init>(r3)
                int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
                r2.setBackgroundColor(r1)
                goto L_0x01e6
            L_0x0018:
                org.telegram.ui.ChatRightsEditActivity r1 = org.telegram.ui.ChatRightsEditActivity.this
                android.widget.FrameLayout r3 = new android.widget.FrameLayout
                android.content.Context r4 = r0.mContext
                r3.<init>(r4)
                android.widget.FrameLayout unused = r1.addBotButtonContainer = r3
                org.telegram.ui.ChatRightsEditActivity r1 = org.telegram.ui.ChatRightsEditActivity.this
                android.widget.FrameLayout r1 = r1.addBotButtonContainer
                java.lang.String r3 = "windowBackgroundGray"
                int r4 = org.telegram.ui.ActionBar.Theme.getColor(r3)
                r1.setBackgroundColor(r4)
                org.telegram.ui.ChatRightsEditActivity r1 = org.telegram.ui.ChatRightsEditActivity.this
                android.widget.FrameLayout r4 = new android.widget.FrameLayout
                android.content.Context r5 = r0.mContext
                r4.<init>(r5)
                android.widget.FrameLayout unused = r1.addBotButton = r4
                org.telegram.ui.ChatRightsEditActivity r1 = org.telegram.ui.ChatRightsEditActivity.this
                org.telegram.ui.Components.AnimatedTextView r4 = new org.telegram.ui.Components.AnimatedTextView
                android.content.Context r5 = r0.mContext
                r6 = 1
                r4.<init>(r5, r6, r2, r2)
                org.telegram.ui.Components.AnimatedTextView unused = r1.addBotButtonText = r4
                org.telegram.ui.ChatRightsEditActivity r1 = org.telegram.ui.ChatRightsEditActivity.this
                org.telegram.ui.Components.AnimatedTextView r1 = r1.addBotButtonText
                java.lang.String r4 = "fonts/rmedium.ttf"
                android.graphics.Typeface r4 = org.telegram.messenger.AndroidUtilities.getTypeface(r4)
                r1.setTypeface(r4)
                org.telegram.ui.ChatRightsEditActivity r1 = org.telegram.ui.ChatRightsEditActivity.this
                org.telegram.ui.Components.AnimatedTextView r1 = r1.addBotButtonText
                r4 = -1
                r1.setTextColor(r4)
                org.telegram.ui.ChatRightsEditActivity r1 = org.telegram.ui.ChatRightsEditActivity.this
                org.telegram.ui.Components.AnimatedTextView r1 = r1.addBotButtonText
                r5 = 1096810496(0x41600000, float:14.0)
                int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
                float r5 = (float) r5
                r1.setTextSize(r5)
                org.telegram.ui.ChatRightsEditActivity r1 = org.telegram.ui.ChatRightsEditActivity.this
                org.telegram.ui.Components.AnimatedTextView r1 = r1.addBotButtonText
                r5 = 17
                r1.setGravity(r5)
                org.telegram.ui.ChatRightsEditActivity r1 = org.telegram.ui.ChatRightsEditActivity.this
                org.telegram.ui.Components.AnimatedTextView r1 = r1.addBotButtonText
                java.lang.StringBuilder r7 = new java.lang.StringBuilder
                r7.<init>()
                r8 = 2131624253(0x7f0e013d, float:1.887568E38)
                java.lang.String r9 = "AddBotButton"
                java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r9, r8)
                r7.append(r8)
                java.lang.String r8 = " "
                r7.append(r8)
                org.telegram.ui.ChatRightsEditActivity r8 = org.telegram.ui.ChatRightsEditActivity.this
                boolean r8 = r8.asAdmin
                if (r8 == 0) goto L_0x00aa
                r8 = 2131624254(0x7f0e013e, float:1.8875683E38)
                java.lang.String r9 = "AddBotButtonAsAdmin"
                goto L_0x00af
            L_0x00aa:
                r8 = 2131624255(0x7f0e013f, float:1.8875685E38)
                java.lang.String r9 = "AddBotButtonAsMember"
            L_0x00af:
                java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r9, r8)
                r7.append(r8)
                java.lang.String r7 = r7.toString()
                r1.setText(r7)
                org.telegram.ui.ChatRightsEditActivity r1 = org.telegram.ui.ChatRightsEditActivity.this
                android.widget.FrameLayout r1 = r1.addBotButton
                org.telegram.ui.ChatRightsEditActivity r7 = org.telegram.ui.ChatRightsEditActivity.this
                org.telegram.ui.Components.AnimatedTextView r7 = r7.addBotButtonText
                r8 = -2
                android.widget.FrameLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r8, (int) r8, (int) r5)
                r1.addView(r7, r5)
                org.telegram.ui.ChatRightsEditActivity r1 = org.telegram.ui.ChatRightsEditActivity.this
                android.widget.FrameLayout r1 = r1.addBotButton
                float[] r5 = new float[r6]
                r6 = 1082130432(0x40800000, float:4.0)
                r5[r2] = r6
                java.lang.String r6 = "featuredStickers_addButton"
                android.graphics.drawable.Drawable r5 = org.telegram.ui.ActionBar.Theme.AdaptiveRipple.filledRect((java.lang.String) r6, (float[]) r5)
                r1.setBackground(r5)
                org.telegram.ui.ChatRightsEditActivity r1 = org.telegram.ui.ChatRightsEditActivity.this
                android.widget.FrameLayout r1 = r1.addBotButton
                org.telegram.ui.ChatRightsEditActivity$ListAdapter$$ExternalSyntheticLambda0 r5 = new org.telegram.ui.ChatRightsEditActivity$ListAdapter$$ExternalSyntheticLambda0
                r5.<init>(r0)
                r1.setOnClickListener(r5)
                org.telegram.ui.ChatRightsEditActivity r1 = org.telegram.ui.ChatRightsEditActivity.this
                android.widget.FrameLayout r1 = r1.addBotButtonContainer
                org.telegram.ui.ChatRightsEditActivity r5 = org.telegram.ui.ChatRightsEditActivity.this
                android.widget.FrameLayout r5 = r5.addBotButton
                r9 = -1
                r10 = 1111490560(0x42400000, float:48.0)
                r11 = 119(0x77, float:1.67E-43)
                r12 = 1096810496(0x41600000, float:14.0)
                r13 = 1105199104(0x41e00000, float:28.0)
                r14 = 1096810496(0x41600000, float:14.0)
                r15 = 1096810496(0x41600000, float:14.0)
                android.widget.FrameLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createFrame(r9, r10, r11, r12, r13, r14, r15)
                r1.addView(r5, r6)
                org.telegram.ui.ChatRightsEditActivity r1 = org.telegram.ui.ChatRightsEditActivity.this
                android.widget.FrameLayout r1 = r1.addBotButtonContainer
                androidx.recyclerview.widget.RecyclerView$LayoutParams r5 = new androidx.recyclerview.widget.RecyclerView$LayoutParams
                r5.<init>((int) r4, (int) r8)
                r1.setLayoutParams(r5)
                android.view.View r1 = new android.view.View
                android.content.Context r4 = r0.mContext
                r1.<init>(r4)
                int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
                r1.setBackgroundColor(r3)
                org.telegram.ui.ChatRightsEditActivity r3 = org.telegram.ui.ChatRightsEditActivity.this
                android.widget.FrameLayout r3 = r3.addBotButtonContainer
                r3.setClipChildren(r2)
                org.telegram.ui.ChatRightsEditActivity r3 = org.telegram.ui.ChatRightsEditActivity.this
                android.widget.FrameLayout r3 = r3.addBotButtonContainer
                r3.setClipToPadding(r2)
                org.telegram.ui.ChatRightsEditActivity r2 = org.telegram.ui.ChatRightsEditActivity.this
                android.widget.FrameLayout r2 = r2.addBotButtonContainer
                r3 = -1
                r4 = 1145569280(0x44480000, float:800.0)
                r5 = 87
                r6 = 0
                r7 = 0
                r8 = 0
                r9 = -1001914368(0xffffffffCLASSNAME, float:-800.0)
                android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r3, r4, r5, r6, r7, r8, r9)
                r2.addView(r1, r3)
                org.telegram.ui.ChatRightsEditActivity r2 = org.telegram.ui.ChatRightsEditActivity.this
                android.widget.FrameLayout r2 = r2.addBotButtonContainer
                goto L_0x01e6
            L_0x0161:
                org.telegram.ui.ChatRightsEditActivity r2 = org.telegram.ui.ChatRightsEditActivity.this
                org.telegram.ui.Cells.PollEditTextCell r3 = new org.telegram.ui.Cells.PollEditTextCell
                android.content.Context r4 = r0.mContext
                r5 = 0
                r3.<init>(r4, r5)
                org.telegram.ui.Cells.PollEditTextCell r2 = r2.rankEditTextCell = r3
                int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
                r2.setBackgroundColor(r1)
                org.telegram.ui.ChatRightsEditActivity$ListAdapter$1 r1 = new org.telegram.ui.ChatRightsEditActivity$ListAdapter$1
                r1.<init>()
                r2.addTextWatcher(r1)
                r1 = r2
                goto L_0x01e6
            L_0x0180:
                org.telegram.ui.Cells.TextDetailCell r2 = new org.telegram.ui.Cells.TextDetailCell
                android.content.Context r3 = r0.mContext
                r2.<init>(r3)
                int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
                r2.setBackgroundColor(r1)
                goto L_0x01e6
            L_0x018f:
                org.telegram.ui.Cells.ShadowSectionCell r1 = new org.telegram.ui.Cells.ShadowSectionCell
                android.content.Context r2 = r0.mContext
                r1.<init>(r2)
                r2 = r1
                goto L_0x01e6
            L_0x0198:
                org.telegram.ui.Cells.TextCheckCell2 r2 = new org.telegram.ui.Cells.TextCheckCell2
                android.content.Context r3 = r0.mContext
                r2.<init>(r3)
                int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
                r2.setBackgroundColor(r1)
                goto L_0x01e6
            L_0x01a7:
                org.telegram.ui.Cells.HeaderCell r2 = new org.telegram.ui.Cells.HeaderCell
                android.content.Context r4 = r0.mContext
                r6 = 21
                r7 = 15
                r8 = 1
                java.lang.String r5 = "windowBackgroundWhiteBlueHeader"
                r3 = r2
                r3.<init>(r4, r5, r6, r7, r8)
                int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
                r2.setBackgroundColor(r1)
                goto L_0x01e6
            L_0x01be:
                org.telegram.ui.Cells.TextInfoPrivacyCell r1 = new org.telegram.ui.Cells.TextInfoPrivacyCell
                android.content.Context r2 = r0.mContext
                r1.<init>(r2)
                r2 = r1
                android.content.Context r1 = r0.mContext
                r3 = 2131165436(0x7var_fc, float:1.794509E38)
                java.lang.String r4 = "windowBackgroundGrayShadow"
                android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r1, (int) r3, (java.lang.String) r4)
                r2.setBackgroundDrawable(r1)
                goto L_0x01e6
            L_0x01d5:
                org.telegram.ui.Cells.UserCell2 r3 = new org.telegram.ui.Cells.UserCell2
                android.content.Context r4 = r0.mContext
                r5 = 4
                r3.<init>(r4, r5, r2)
                r2 = r3
                int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
                r2.setBackgroundColor(r1)
            L_0x01e6:
                org.telegram.ui.Components.RecyclerListView$Holder r1 = new org.telegram.ui.Components.RecyclerListView$Holder
                r1.<init>(r2)
                return r1
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChatRightsEditActivity.ListAdapter.onCreateViewHolder(android.view.ViewGroup, int):androidx.recyclerview.widget.RecyclerView$ViewHolder");
        }

        /* renamed from: lambda$onCreateViewHolder$0$org-telegram-ui-ChatRightsEditActivity$ListAdapter  reason: not valid java name */
        public /* synthetic */ void m3296x1beb1a5a(View e) {
            ChatRightsEditActivity.this.onDonePressed();
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            String hint;
            String value;
            String hint2;
            boolean z = true;
            switch (holder.getItemViewType()) {
                case 0:
                    UserCell2 userCell2 = (UserCell2) holder.itemView;
                    String status = null;
                    if (ChatRightsEditActivity.this.currentType == 2) {
                        status = LocaleController.getString("Bot", NUM);
                    }
                    userCell2.setData(ChatRightsEditActivity.this.currentUser, (CharSequence) null, status, 0);
                    return;
                case 1:
                    TextInfoPrivacyCell privacyCell = (TextInfoPrivacyCell) holder.itemView;
                    if (position == ChatRightsEditActivity.this.cantEditInfoRow) {
                        privacyCell.setText(LocaleController.getString("EditAdminCantEdit", NUM));
                        return;
                    } else if (position == ChatRightsEditActivity.this.rankInfoRow) {
                        if (!UserObject.isUserSelf(ChatRightsEditActivity.this.currentUser) || !ChatRightsEditActivity.this.currentChat.creator) {
                            hint = LocaleController.getString("ChannelAdmin", NUM);
                        } else {
                            hint = LocaleController.getString("ChannelCreator", NUM);
                        }
                        privacyCell.setText(LocaleController.formatString("EditAdminRankInfo", NUM, hint));
                        return;
                    } else {
                        return;
                    }
                case 2:
                    TextSettingsCell actionCell = (TextSettingsCell) holder.itemView;
                    if (position == ChatRightsEditActivity.this.removeAdminRow) {
                        actionCell.setTextColor(Theme.getColor("windowBackgroundWhiteRedText5"));
                        actionCell.setTag("windowBackgroundWhiteRedText5");
                        if (ChatRightsEditActivity.this.currentType == 0) {
                            actionCell.setText(LocaleController.getString("EditAdminRemoveAdmin", NUM), false);
                            return;
                        } else if (ChatRightsEditActivity.this.currentType == 1) {
                            actionCell.setText(LocaleController.getString("UserRestrictionsBlock", NUM), false);
                            return;
                        } else {
                            return;
                        }
                    } else if (position == ChatRightsEditActivity.this.transferOwnerRow) {
                        actionCell.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
                        actionCell.setTag("windowBackgroundWhiteBlackText");
                        if (ChatRightsEditActivity.this.isChannel) {
                            actionCell.setText(LocaleController.getString("EditAdminChannelTransfer", NUM), false);
                            return;
                        } else {
                            actionCell.setText(LocaleController.getString("EditAdminGroupTransfer", NUM), false);
                            return;
                        }
                    } else {
                        return;
                    }
                case 3:
                    HeaderCell headerCell = (HeaderCell) holder.itemView;
                    if (position == 2) {
                        if (ChatRightsEditActivity.this.currentType == 2 || (ChatRightsEditActivity.this.currentUser != null && ChatRightsEditActivity.this.currentUser.bot)) {
                            headerCell.setText(LocaleController.getString("BotRestrictionsCanDo", NUM));
                            return;
                        } else if (ChatRightsEditActivity.this.currentType == 0) {
                            headerCell.setText(LocaleController.getString("EditAdminWhatCanDo", NUM));
                            return;
                        } else if (ChatRightsEditActivity.this.currentType == 1) {
                            headerCell.setText(LocaleController.getString("UserRestrictionsCanDo", NUM));
                            return;
                        } else {
                            return;
                        }
                    } else if (position == ChatRightsEditActivity.this.rankHeaderRow) {
                        headerCell.setText(LocaleController.getString("EditAdminRank", NUM));
                        return;
                    } else {
                        return;
                    }
                case 4:
                    TextCheckCell2 checkCell = (TextCheckCell2) holder.itemView;
                    boolean asAdminValue = ChatRightsEditActivity.this.currentType != 2 || ChatRightsEditActivity.this.asAdmin;
                    boolean isCreator = ChatRightsEditActivity.this.currentChat != null && ChatRightsEditActivity.this.currentChat.creator;
                    int i = NUM;
                    if (position == ChatRightsEditActivity.this.manageRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("ManageGroup", NUM), ChatRightsEditActivity.this.asAdmin, true);
                        if (ChatRightsEditActivity.this.myAdminRights.add_admins || isCreator) {
                            i = 0;
                        }
                        checkCell.setIcon(i);
                    } else if (position == ChatRightsEditActivity.this.changeInfoRow) {
                        if (ChatRightsEditActivity.this.currentType == 0 || ChatRightsEditActivity.this.currentType == 2) {
                            if (ChatRightsEditActivity.this.isChannel) {
                                checkCell.setTextAndCheck(LocaleController.getString("EditAdminChangeChannelInfo", NUM), (asAdminValue && ChatRightsEditActivity.this.adminRights.change_info) || !ChatRightsEditActivity.this.defaultBannedRights.change_info, true);
                            } else {
                                checkCell.setTextAndCheck(LocaleController.getString("EditAdminChangeGroupInfo", NUM), (asAdminValue && ChatRightsEditActivity.this.adminRights.change_info) || !ChatRightsEditActivity.this.defaultBannedRights.change_info, true);
                            }
                            if (ChatRightsEditActivity.this.currentType == 2) {
                                if (ChatRightsEditActivity.this.myAdminRights.change_info || isCreator) {
                                    i = 0;
                                }
                                checkCell.setIcon(i);
                            }
                        } else if (ChatRightsEditActivity.this.currentType == 1) {
                            checkCell.setTextAndCheck(LocaleController.getString("UserRestrictionsChangeInfo", NUM), !ChatRightsEditActivity.this.bannedRights.change_info && !ChatRightsEditActivity.this.defaultBannedRights.change_info, false);
                            if (!ChatRightsEditActivity.this.defaultBannedRights.change_info) {
                                i = 0;
                            }
                            checkCell.setIcon(i);
                        }
                    } else if (position == ChatRightsEditActivity.this.postMessagesRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("EditAdminPostMessages", NUM), asAdminValue && ChatRightsEditActivity.this.adminRights.post_messages, true);
                        if (ChatRightsEditActivity.this.currentType == 2) {
                            if (ChatRightsEditActivity.this.myAdminRights.post_messages || isCreator) {
                                i = 0;
                            }
                            checkCell.setIcon(i);
                        }
                    } else if (position == ChatRightsEditActivity.this.editMesagesRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("EditAdminEditMessages", NUM), asAdminValue && ChatRightsEditActivity.this.adminRights.edit_messages, true);
                        if (ChatRightsEditActivity.this.currentType == 2) {
                            if (ChatRightsEditActivity.this.myAdminRights.edit_messages || isCreator) {
                                i = 0;
                            }
                            checkCell.setIcon(i);
                        }
                    } else if (position == ChatRightsEditActivity.this.deleteMessagesRow) {
                        if (ChatRightsEditActivity.this.isChannel) {
                            checkCell.setTextAndCheck(LocaleController.getString("EditAdminDeleteMessages", NUM), asAdminValue && ChatRightsEditActivity.this.adminRights.delete_messages, true);
                        } else {
                            checkCell.setTextAndCheck(LocaleController.getString("EditAdminGroupDeleteMessages", NUM), asAdminValue && ChatRightsEditActivity.this.adminRights.delete_messages, true);
                        }
                        if (ChatRightsEditActivity.this.currentType == 2) {
                            if (ChatRightsEditActivity.this.myAdminRights.delete_messages || isCreator) {
                                i = 0;
                            }
                            checkCell.setIcon(i);
                        }
                    } else if (position == ChatRightsEditActivity.this.addAdminsRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("EditAdminAddAdmins", NUM), asAdminValue && ChatRightsEditActivity.this.adminRights.add_admins, ChatRightsEditActivity.this.anonymousRow != -1);
                        if (ChatRightsEditActivity.this.currentType == 2) {
                            if (ChatRightsEditActivity.this.myAdminRights.add_admins || isCreator) {
                                i = 0;
                            }
                            checkCell.setIcon(i);
                        }
                    } else if (position == ChatRightsEditActivity.this.anonymousRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("EditAdminSendAnonymously", NUM), asAdminValue && ChatRightsEditActivity.this.adminRights.anonymous, false);
                        if (ChatRightsEditActivity.this.currentType == 2) {
                            if (ChatRightsEditActivity.this.myAdminRights.anonymous || isCreator) {
                                i = 0;
                            }
                            checkCell.setIcon(i);
                        }
                    } else if (position == ChatRightsEditActivity.this.banUsersRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("EditAdminBanUsers", NUM), asAdminValue && ChatRightsEditActivity.this.adminRights.ban_users, true);
                        if (ChatRightsEditActivity.this.currentType == 2) {
                            if (ChatRightsEditActivity.this.myAdminRights.ban_users || isCreator) {
                                i = 0;
                            }
                            checkCell.setIcon(i);
                        }
                    } else if (position == ChatRightsEditActivity.this.startVoiceChatRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("StartVoipChatPermission", NUM), asAdminValue && ChatRightsEditActivity.this.adminRights.manage_call, true);
                        if (ChatRightsEditActivity.this.currentType == 2) {
                            if (ChatRightsEditActivity.this.myAdminRights.manage_call || isCreator) {
                                i = 0;
                            }
                            checkCell.setIcon(i);
                        }
                    } else if (position == ChatRightsEditActivity.this.addUsersRow) {
                        if (ChatRightsEditActivity.this.currentType == 0) {
                            if (ChatObject.isActionBannedByDefault(ChatRightsEditActivity.this.currentChat, 3)) {
                                checkCell.setTextAndCheck(LocaleController.getString("EditAdminAddUsers", NUM), ChatRightsEditActivity.this.adminRights.invite_users, true);
                            } else {
                                checkCell.setTextAndCheck(LocaleController.getString("EditAdminAddUsersViaLink", NUM), ChatRightsEditActivity.this.adminRights.invite_users, true);
                            }
                        } else if (ChatRightsEditActivity.this.currentType == 1) {
                            checkCell.setTextAndCheck(LocaleController.getString("UserRestrictionsInviteUsers", NUM), !ChatRightsEditActivity.this.bannedRights.invite_users && !ChatRightsEditActivity.this.defaultBannedRights.invite_users, true);
                            if (!ChatRightsEditActivity.this.defaultBannedRights.invite_users) {
                                i = 0;
                            }
                            checkCell.setIcon(i);
                        } else if (ChatRightsEditActivity.this.currentType == 2) {
                            checkCell.setTextAndCheck(LocaleController.getString("EditAdminAddUsersViaLink", NUM), asAdminValue && ChatRightsEditActivity.this.adminRights.invite_users, true);
                            if (ChatRightsEditActivity.this.myAdminRights.invite_users || isCreator) {
                                i = 0;
                            }
                            checkCell.setIcon(i);
                        }
                    } else if (position == ChatRightsEditActivity.this.pinMessagesRow) {
                        if (ChatRightsEditActivity.this.currentType == 0 || ChatRightsEditActivity.this.currentType == 2) {
                            checkCell.setTextAndCheck(LocaleController.getString("EditAdminPinMessages", NUM), (asAdminValue && ChatRightsEditActivity.this.adminRights.pin_messages) || !ChatRightsEditActivity.this.defaultBannedRights.pin_messages, true);
                            if (ChatRightsEditActivity.this.currentType == 2) {
                                if (ChatRightsEditActivity.this.myAdminRights.pin_messages || isCreator) {
                                    i = 0;
                                }
                                checkCell.setIcon(i);
                            }
                        } else if (ChatRightsEditActivity.this.currentType == 1) {
                            checkCell.setTextAndCheck(LocaleController.getString("UserRestrictionsPinMessages", NUM), !ChatRightsEditActivity.this.bannedRights.pin_messages && !ChatRightsEditActivity.this.defaultBannedRights.pin_messages, true);
                            if (!ChatRightsEditActivity.this.defaultBannedRights.pin_messages) {
                                i = 0;
                            }
                            checkCell.setIcon(i);
                        }
                    } else if (position == ChatRightsEditActivity.this.sendMessagesRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("UserRestrictionsSend", NUM), !ChatRightsEditActivity.this.bannedRights.send_messages && !ChatRightsEditActivity.this.defaultBannedRights.send_messages, true);
                        if (!ChatRightsEditActivity.this.defaultBannedRights.send_messages) {
                            i = 0;
                        }
                        checkCell.setIcon(i);
                    } else if (position == ChatRightsEditActivity.this.sendMediaRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("UserRestrictionsSendMedia", NUM), !ChatRightsEditActivity.this.bannedRights.send_media && !ChatRightsEditActivity.this.defaultBannedRights.send_media, true);
                        if (!ChatRightsEditActivity.this.defaultBannedRights.send_media) {
                            i = 0;
                        }
                        checkCell.setIcon(i);
                    } else if (position == ChatRightsEditActivity.this.sendStickersRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("UserRestrictionsSendStickers", NUM), !ChatRightsEditActivity.this.bannedRights.send_stickers && !ChatRightsEditActivity.this.defaultBannedRights.send_stickers, true);
                        if (!ChatRightsEditActivity.this.defaultBannedRights.send_stickers) {
                            i = 0;
                        }
                        checkCell.setIcon(i);
                    } else if (position == ChatRightsEditActivity.this.embedLinksRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("UserRestrictionsEmbedLinks", NUM), !ChatRightsEditActivity.this.bannedRights.embed_links && !ChatRightsEditActivity.this.defaultBannedRights.embed_links, true);
                        if (!ChatRightsEditActivity.this.defaultBannedRights.embed_links) {
                            i = 0;
                        }
                        checkCell.setIcon(i);
                    } else if (position == ChatRightsEditActivity.this.sendPollsRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("UserRestrictionsSendPolls", NUM), !ChatRightsEditActivity.this.bannedRights.send_polls && !ChatRightsEditActivity.this.defaultBannedRights.send_polls, true);
                        if (!ChatRightsEditActivity.this.defaultBannedRights.send_polls) {
                            i = 0;
                        }
                        checkCell.setIcon(i);
                    }
                    if (ChatRightsEditActivity.this.currentType != 2) {
                        if (position == ChatRightsEditActivity.this.sendMediaRow || position == ChatRightsEditActivity.this.sendStickersRow || position == ChatRightsEditActivity.this.embedLinksRow || position == ChatRightsEditActivity.this.sendPollsRow) {
                            if (ChatRightsEditActivity.this.bannedRights.send_messages || ChatRightsEditActivity.this.bannedRights.view_messages || ChatRightsEditActivity.this.defaultBannedRights.send_messages || ChatRightsEditActivity.this.defaultBannedRights.view_messages) {
                                z = false;
                            }
                            checkCell.setEnabled(z);
                            return;
                        } else if (position == ChatRightsEditActivity.this.sendMessagesRow) {
                            if (ChatRightsEditActivity.this.bannedRights.view_messages || ChatRightsEditActivity.this.defaultBannedRights.view_messages) {
                                z = false;
                            }
                            checkCell.setEnabled(z);
                            return;
                        } else {
                            return;
                        }
                    } else {
                        return;
                    }
                case 5:
                    ShadowSectionCell shadowCell = (ShadowSectionCell) holder.itemView;
                    if (ChatRightsEditActivity.this.currentType == 2 && (position == ChatRightsEditActivity.this.rightsShadowRow || position == ChatRightsEditActivity.this.rankInfoRow)) {
                        shadowCell.setAlpha(ChatRightsEditActivity.this.asAdminT);
                    } else {
                        shadowCell.setAlpha(1.0f);
                    }
                    int i2 = NUM;
                    if (position == ChatRightsEditActivity.this.rightsShadowRow) {
                        Context context = this.mContext;
                        if (ChatRightsEditActivity.this.removeAdminRow == -1 && ChatRightsEditActivity.this.rankRow == -1) {
                            i2 = NUM;
                        }
                        shadowCell.setBackgroundDrawable(Theme.getThemedDrawable(context, i2, "windowBackgroundGrayShadow"));
                        return;
                    } else if (position == ChatRightsEditActivity.this.removeAdminShadowRow) {
                        shadowCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                        return;
                    } else if (position == ChatRightsEditActivity.this.rankInfoRow) {
                        Context context2 = this.mContext;
                        if (!ChatRightsEditActivity.this.canEdit) {
                            i2 = NUM;
                        }
                        shadowCell.setBackgroundDrawable(Theme.getThemedDrawable(context2, i2, "windowBackgroundGrayShadow"));
                        return;
                    } else {
                        shadowCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                        return;
                    }
                case 6:
                    TextDetailCell detailCell = (TextDetailCell) holder.itemView;
                    if (position == ChatRightsEditActivity.this.untilDateRow) {
                        if (ChatRightsEditActivity.this.bannedRights.until_date == 0 || Math.abs(((long) ChatRightsEditActivity.this.bannedRights.until_date) - (System.currentTimeMillis() / 1000)) > NUM) {
                            value = LocaleController.getString("UserRestrictionsUntilForever", NUM);
                        } else {
                            value = LocaleController.formatDateForBan((long) ChatRightsEditActivity.this.bannedRights.until_date);
                        }
                        detailCell.setTextAndValue(LocaleController.getString("UserRestrictionsDuration", NUM), value, false);
                        return;
                    }
                    return;
                case 7:
                    PollEditTextCell textCell = (PollEditTextCell) holder.itemView;
                    if (!UserObject.isUserSelf(ChatRightsEditActivity.this.currentUser) || !ChatRightsEditActivity.this.currentChat.creator) {
                        hint2 = LocaleController.getString("ChannelAdmin", NUM);
                    } else {
                        hint2 = LocaleController.getString("ChannelCreator", NUM);
                    }
                    this.ignoreTextChange = true;
                    textCell.getTextView().setEnabled(ChatRightsEditActivity.this.canEdit || ChatRightsEditActivity.this.currentChat.creator);
                    textCell.getTextView().setSingleLine(true);
                    textCell.getTextView().setImeOptions(6);
                    textCell.setTextAndHint(ChatRightsEditActivity.this.currentRank, hint2, false);
                    this.ignoreTextChange = false;
                    return;
                default:
                    return;
            }
        }

        public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
            if (holder.getAdapterPosition() == ChatRightsEditActivity.this.rankHeaderRow) {
                ChatRightsEditActivity.this.setTextLeft(holder.itemView);
            }
        }

        public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
            if (holder.getAdapterPosition() == ChatRightsEditActivity.this.rankRow && ChatRightsEditActivity.this.getParentActivity() != null) {
                AndroidUtilities.hideKeyboard(ChatRightsEditActivity.this.getParentActivity().getCurrentFocus());
            }
        }

        public int getItemViewType(int position) {
            if (position == 0) {
                return 0;
            }
            if (position == 1 || position == ChatRightsEditActivity.this.rightsShadowRow || position == ChatRightsEditActivity.this.removeAdminShadowRow || position == ChatRightsEditActivity.this.untilSectionRow || position == ChatRightsEditActivity.this.transferOwnerShadowRow) {
                return 5;
            }
            if (position == 2 || position == ChatRightsEditActivity.this.rankHeaderRow) {
                return 3;
            }
            if (position == ChatRightsEditActivity.this.changeInfoRow || position == ChatRightsEditActivity.this.postMessagesRow || position == ChatRightsEditActivity.this.editMesagesRow || position == ChatRightsEditActivity.this.deleteMessagesRow || position == ChatRightsEditActivity.this.addAdminsRow || position == ChatRightsEditActivity.this.banUsersRow || position == ChatRightsEditActivity.this.addUsersRow || position == ChatRightsEditActivity.this.pinMessagesRow || position == ChatRightsEditActivity.this.sendMessagesRow || position == ChatRightsEditActivity.this.sendMediaRow || position == ChatRightsEditActivity.this.sendStickersRow || position == ChatRightsEditActivity.this.embedLinksRow || position == ChatRightsEditActivity.this.sendPollsRow || position == ChatRightsEditActivity.this.anonymousRow || position == ChatRightsEditActivity.this.startVoiceChatRow || position == ChatRightsEditActivity.this.manageRow) {
                return 4;
            }
            if (position == ChatRightsEditActivity.this.cantEditInfoRow || position == ChatRightsEditActivity.this.rankInfoRow) {
                return 1;
            }
            if (position == ChatRightsEditActivity.this.untilDateRow) {
                return 6;
            }
            if (position == ChatRightsEditActivity.this.rankRow) {
                return 7;
            }
            if (position == ChatRightsEditActivity.this.addBotButtonRow) {
                return 8;
            }
            return 2;
        }
    }

    private void updateAsAdmin(boolean animated) {
        String str;
        int i;
        TLRPC.Chat chat;
        TLRPC.Chat chat2;
        FrameLayout frameLayout = this.addBotButton;
        if (frameLayout != null) {
            frameLayout.invalidate();
        }
        int count = this.listView.getChildCount();
        int i2 = 0;
        while (true) {
            boolean z = true;
            if (i2 >= count) {
                break;
            }
            View child = this.listView.getChildAt(i2);
            int childPosition = this.listView.getChildAdapterPosition(child);
            if (child instanceof TextCheckCell2) {
                if (this.asAdmin) {
                    boolean childValue = false;
                    boolean childEnabled = false;
                    if (childPosition == this.manageRow) {
                        childValue = this.asAdmin;
                        if (!this.myAdminRights.add_admins && ((chat2 = this.currentChat) == null || !chat2.creator)) {
                            z = false;
                        }
                        childEnabled = z;
                    } else if (childPosition == this.changeInfoRow) {
                        childValue = this.adminRights.change_info;
                        if (!this.myAdminRights.change_info || !this.defaultBannedRights.change_info) {
                            z = false;
                        }
                        childEnabled = z;
                    } else if (childPosition == this.postMessagesRow) {
                        childValue = this.adminRights.post_messages;
                        childEnabled = this.myAdminRights.post_messages;
                    } else if (childPosition == this.editMesagesRow) {
                        childValue = this.adminRights.edit_messages;
                        childEnabled = this.myAdminRights.edit_messages;
                    } else if (childPosition == this.deleteMessagesRow) {
                        childValue = this.adminRights.delete_messages;
                        childEnabled = this.myAdminRights.delete_messages;
                    } else if (childPosition == this.banUsersRow) {
                        childValue = this.adminRights.ban_users;
                        childEnabled = this.myAdminRights.ban_users;
                    } else if (childPosition == this.addUsersRow) {
                        childValue = this.adminRights.invite_users;
                        childEnabled = this.myAdminRights.invite_users;
                    } else if (childPosition == this.pinMessagesRow) {
                        childValue = this.adminRights.pin_messages;
                        if (!this.myAdminRights.pin_messages || !this.defaultBannedRights.pin_messages) {
                            z = false;
                        }
                        childEnabled = z;
                    } else if (childPosition == this.startVoiceChatRow) {
                        childValue = this.adminRights.manage_call;
                        childEnabled = this.myAdminRights.manage_call;
                    } else if (childPosition == this.addAdminsRow) {
                        childValue = this.adminRights.add_admins;
                        childEnabled = this.myAdminRights.add_admins;
                    } else if (childPosition == this.anonymousRow) {
                        childValue = this.adminRights.anonymous;
                        if (!this.myAdminRights.anonymous && ((chat = this.currentChat) == null || !chat.creator)) {
                            z = false;
                        }
                        childEnabled = z;
                    }
                    ((TextCheckCell2) child).setChecked(childValue);
                    ((TextCheckCell2) child).setEnabled(childEnabled, animated);
                } else if ((childPosition != this.changeInfoRow || this.defaultBannedRights.change_info) && (childPosition != this.pinMessagesRow || this.defaultBannedRights.pin_messages)) {
                    ((TextCheckCell2) child).setChecked(false);
                    TextCheckCell2 textCheckCell2 = (TextCheckCell2) child;
                    if (childPosition != this.manageRow) {
                        z = false;
                    }
                    textCheckCell2.setEnabled(z, animated);
                } else {
                    ((TextCheckCell2) child).setChecked(true);
                    ((TextCheckCell2) child).setEnabled(false, false);
                }
            }
            i2++;
        }
        this.listViewAdapter.notifyDataSetChanged();
        AnimatedTextView animatedTextView = this.addBotButtonText;
        if (animatedTextView != null) {
            StringBuilder sb = new StringBuilder();
            sb.append(LocaleController.getString("AddBotButton", NUM));
            sb.append(" ");
            if (this.asAdmin) {
                i = NUM;
                str = "AddBotButtonAsAdmin";
            } else {
                i = NUM;
                str = "AddBotButtonAsMember";
            }
            sb.append(LocaleController.getString(str, i));
            animatedTextView.setText(sb.toString(), animated, this.asAdmin);
        }
        ValueAnimator valueAnimator = this.asAdminAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
            this.asAdminAnimator = null;
        }
        float f = 1.0f;
        if (animated) {
            float[] fArr = new float[2];
            fArr[0] = this.asAdminT;
            fArr[1] = this.asAdmin ? 1.0f : 0.0f;
            ValueAnimator ofFloat = ValueAnimator.ofFloat(fArr);
            this.asAdminAnimator = ofFloat;
            ofFloat.addUpdateListener(new ChatRightsEditActivity$$ExternalSyntheticLambda11(this));
            ValueAnimator valueAnimator2 = this.asAdminAnimator;
            float f2 = this.asAdminT;
            if (!this.asAdmin) {
                f = 0.0f;
            }
            valueAnimator2.setDuration((long) (Math.abs(f2 - f) * 200.0f));
            this.asAdminAnimator.start();
            return;
        }
        if (!this.asAdmin) {
            f = 0.0f;
        }
        this.asAdminT = f;
        FrameLayout frameLayout2 = this.addBotButton;
        if (frameLayout2 != null) {
            frameLayout2.invalidate();
        }
    }

    /* renamed from: lambda$updateAsAdmin$25$org-telegram-ui-ChatRightsEditActivity  reason: not valid java name */
    public /* synthetic */ void m3295lambda$updateAsAdmin$25$orgtelegramuiChatRightsEditActivity(ValueAnimator a) {
        this.asAdminT = ((Float) a.getAnimatedValue()).floatValue();
        FrameLayout frameLayout = this.addBotButton;
        if (frameLayout != null) {
            frameLayout.invalidate();
        }
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();
        ThemeDescription.ThemeDescriptionDelegate cellDelegate = new ChatRightsEditActivity$$ExternalSyntheticLambda16(this);
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{UserCell2.class, TextSettingsCell.class, TextCheckCell2.class, HeaderCell.class, TextDetailCell.class, PollEditTextCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        themeDescriptions.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultIcon"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultTitle"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSelector"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "divider"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText4"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteRedText5"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteValueText"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayIcon"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextDetailCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextDetailCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText2"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell2.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell2.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText2"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell2.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switch2Track"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell2.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switch2TrackChecked"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueHeader"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{HeaderCell.class}, new String[]{"textView2"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteRedText5"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{HeaderCell.class}, new String[]{"textView2"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText3"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{PollEditTextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_HINTTEXTCOLOR, new Class[]{PollEditTextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteHintText"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{UserCell2.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{UserCell2.class}, new String[]{"statusColor"}, (Paint[]) null, (Drawable[]) null, cellDelegate, "windowBackgroundWhiteGrayText"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{UserCell2.class}, new String[]{"statusOnlineColor"}, (Paint[]) null, (Drawable[]) null, cellDelegate, "windowBackgroundWhiteBlueText"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{UserCell2.class}, (Paint) null, Theme.avatarDrawables, (ThemeDescription.ThemeDescriptionDelegate) null, "avatar_text"));
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate = cellDelegate;
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, "avatar_backgroundRed"));
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate2 = cellDelegate;
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, "avatar_backgroundOrange"));
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, "avatar_backgroundViolet"));
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, "avatar_backgroundGreen"));
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, "avatar_backgroundCyan"));
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, "avatar_backgroundBlue"));
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, "avatar_backgroundPink"));
        themeDescriptions.add(new ThemeDescription((View) null, 0, new Class[]{DialogRadioCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogTextBlack"));
        themeDescriptions.add(new ThemeDescription((View) null, 0, new Class[]{DialogRadioCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogTextGray2"));
        themeDescriptions.add(new ThemeDescription((View) null, ThemeDescription.FLAG_CHECKBOX, new Class[]{DialogRadioCell.class}, new String[]{"radioButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogRadioBackground"));
        themeDescriptions.add(new ThemeDescription((View) null, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{DialogRadioCell.class}, new String[]{"radioButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogRadioBackgroundChecked"));
        return themeDescriptions;
    }

    /* renamed from: lambda$getThemeDescriptions$26$org-telegram-ui-ChatRightsEditActivity  reason: not valid java name */
    public /* synthetic */ void m3278x139757c1() {
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            int count = recyclerListView.getChildCount();
            for (int a = 0; a < count; a++) {
                View child = this.listView.getChildAt(a);
                if (child instanceof UserCell2) {
                    ((UserCell2) child).update(0);
                }
            }
        }
    }
}
