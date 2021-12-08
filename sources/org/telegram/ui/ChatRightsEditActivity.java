package org.telegram.ui;

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
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Calendar;
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
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;

public class ChatRightsEditActivity extends BaseFragment {
    private static final int MAX_RANK_LENGTH = 16;
    public static final int TYPE_ADMIN = 0;
    public static final int TYPE_BANNED = 1;
    private static final int done_button = 1;
    /* access modifiers changed from: private */
    public int addAdminsRow;
    /* access modifiers changed from: private */
    public int addUsersRow;
    /* access modifiers changed from: private */
    public TLRPC.TL_chatAdminRights adminRights;
    /* access modifiers changed from: private */
    public int anonymousRow;
    /* access modifiers changed from: private */
    public int banUsersRow;
    /* access modifiers changed from: private */
    public TLRPC.TL_chatBannedRights bannedRights;
    /* access modifiers changed from: private */
    public boolean canEdit;
    /* access modifiers changed from: private */
    public int cantEditInfoRow;
    /* access modifiers changed from: private */
    public int changeInfoRow;
    private long chatId;
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
    /* access modifiers changed from: private */
    public int editMesagesRow;
    /* access modifiers changed from: private */
    public int embedLinksRow;
    private boolean initialIsSet;
    private String initialRank;
    private boolean isAddingNew;
    /* access modifiers changed from: private */
    public boolean isChannel;
    /* access modifiers changed from: private */
    public RecyclerListView listView;
    private ListAdapter listViewAdapter;
    /* access modifiers changed from: private */
    public TLRPC.TL_chatAdminRights myAdminRights;
    /* access modifiers changed from: private */
    public int pinMessagesRow;
    /* access modifiers changed from: private */
    public int postMessagesRow;
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

    public ChatRightsEditActivity(long userId, long channelId, TLRPC.TL_chatAdminRights rightsAdmin, TLRPC.TL_chatBannedRights rightsBannedDefault, TLRPC.TL_chatBannedRights rightsBanned, String rank, int type, boolean edit, boolean addingNew) {
        this.isAddingNew = addingNew;
        this.chatId = channelId;
        this.currentUser = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(userId));
        this.currentType = type;
        this.canEdit = edit;
        TLRPC.Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(this.chatId));
        this.currentChat = chat;
        rank = rank == null ? "" : rank;
        this.currentRank = rank;
        this.initialRank = rank;
        boolean z = true;
        if (chat != null) {
            this.isChannel = ChatObject.isChannel(chat) && !this.currentChat.megagroup;
            this.myAdminRights = this.currentChat.admin_rights;
        }
        if (this.myAdminRights == null) {
            TLRPC.TL_chatAdminRights tL_chatAdminRights = new TLRPC.TL_chatAdminRights();
            this.myAdminRights = tL_chatAdminRights;
            tL_chatAdminRights.manage_call = true;
            tL_chatAdminRights.add_admins = true;
            tL_chatAdminRights.pin_messages = true;
            tL_chatAdminRights.invite_users = true;
            tL_chatAdminRights.ban_users = true;
            tL_chatAdminRights.delete_messages = true;
            tL_chatAdminRights.edit_messages = true;
            tL_chatAdminRights.post_messages = true;
            tL_chatAdminRights.change_info = true;
        }
        if (type == 0) {
            TLRPC.TL_chatAdminRights tL_chatAdminRights2 = new TLRPC.TL_chatAdminRights();
            this.adminRights = tL_chatAdminRights2;
            if (rightsAdmin == null) {
                tL_chatAdminRights2.change_info = this.myAdminRights.change_info;
                this.adminRights.post_messages = this.myAdminRights.post_messages;
                this.adminRights.edit_messages = this.myAdminRights.edit_messages;
                this.adminRights.delete_messages = this.myAdminRights.delete_messages;
                this.adminRights.manage_call = this.myAdminRights.manage_call;
                this.adminRights.ban_users = this.myAdminRights.ban_users;
                this.adminRights.invite_users = this.myAdminRights.invite_users;
                this.adminRights.pin_messages = this.myAdminRights.pin_messages;
                this.initialIsSet = false;
            } else {
                tL_chatAdminRights2.change_info = rightsAdmin.change_info;
                this.adminRights.post_messages = rightsAdmin.post_messages;
                this.adminRights.edit_messages = rightsAdmin.edit_messages;
                this.adminRights.delete_messages = rightsAdmin.delete_messages;
                this.adminRights.manage_call = rightsAdmin.manage_call;
                this.adminRights.ban_users = rightsAdmin.ban_users;
                this.adminRights.invite_users = rightsAdmin.invite_users;
                this.adminRights.pin_messages = rightsAdmin.pin_messages;
                this.adminRights.add_admins = rightsAdmin.add_admins;
                this.adminRights.anonymous = rightsAdmin.anonymous;
                if (!this.adminRights.change_info && !this.adminRights.post_messages && !this.adminRights.edit_messages && !this.adminRights.delete_messages && !this.adminRights.ban_users && !this.adminRights.invite_users && !this.adminRights.pin_messages && !this.adminRights.add_admins && !this.adminRights.manage_call && !this.adminRights.anonymous) {
                    z = false;
                }
                this.initialIsSet = z;
            }
        } else {
            this.defaultBannedRights = rightsBannedDefault;
            if (rightsBannedDefault == null) {
                TLRPC.TL_chatBannedRights tL_chatBannedRights = new TLRPC.TL_chatBannedRights();
                this.defaultBannedRights = tL_chatBannedRights;
                tL_chatBannedRights.pin_messages = false;
                tL_chatBannedRights.change_info = false;
                tL_chatBannedRights.invite_users = false;
                tL_chatBannedRights.send_polls = false;
                tL_chatBannedRights.send_inline = false;
                tL_chatBannedRights.send_games = false;
                tL_chatBannedRights.send_gifs = false;
                tL_chatBannedRights.send_stickers = false;
                tL_chatBannedRights.embed_links = false;
                tL_chatBannedRights.send_messages = false;
                tL_chatBannedRights.send_media = false;
                tL_chatBannedRights.view_messages = false;
            }
            TLRPC.TL_chatBannedRights tL_chatBannedRights2 = new TLRPC.TL_chatBannedRights();
            this.bannedRights = tL_chatBannedRights2;
            if (rightsBanned == null) {
                tL_chatBannedRights2.pin_messages = false;
                tL_chatBannedRights2.change_info = false;
                tL_chatBannedRights2.invite_users = false;
                tL_chatBannedRights2.send_polls = false;
                tL_chatBannedRights2.send_inline = false;
                tL_chatBannedRights2.send_games = false;
                tL_chatBannedRights2.send_gifs = false;
                tL_chatBannedRights2.send_stickers = false;
                tL_chatBannedRights2.embed_links = false;
                tL_chatBannedRights2.send_messages = false;
                tL_chatBannedRights2.send_media = false;
                tL_chatBannedRights2.view_messages = false;
            } else {
                tL_chatBannedRights2.view_messages = rightsBanned.view_messages;
                this.bannedRights.send_messages = rightsBanned.send_messages;
                this.bannedRights.send_media = rightsBanned.send_media;
                this.bannedRights.send_stickers = rightsBanned.send_stickers;
                this.bannedRights.send_gifs = rightsBanned.send_gifs;
                this.bannedRights.send_games = rightsBanned.send_games;
                this.bannedRights.send_inline = rightsBanned.send_inline;
                this.bannedRights.embed_links = rightsBanned.embed_links;
                this.bannedRights.send_polls = rightsBanned.send_polls;
                this.bannedRights.invite_users = rightsBanned.invite_users;
                this.bannedRights.change_info = rightsBanned.change_info;
                this.bannedRights.pin_messages = rightsBanned.pin_messages;
                this.bannedRights.until_date = rightsBanned.until_date;
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
            if (rightsBanned != null && rightsBanned.view_messages) {
                z = false;
            }
            this.initialIsSet = z;
        }
        updateRows(false);
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(NUM);
        int i = 1;
        this.actionBar.setAllowOverlayTitle(true);
        if (this.currentType == 0) {
            this.actionBar.setTitle(LocaleController.getString("EditAdmin", NUM));
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
            this.actionBar.createMenu().addItemWithWidth(1, NUM, AndroidUtilities.dp(56.0f), LocaleController.getString("Done", NUM));
        }
        this.fragmentView = new FrameLayout(context);
        this.fragmentView.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        this.fragmentView.setFocusableInTouchMode(true);
        this.listView = new RecyclerListView(context);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, 1, false);
        ((DefaultItemAnimator) this.listView.getItemAnimator()).setDelayAnimations(false);
        this.listView.setLayoutManager(linearLayoutManager);
        RecyclerListView recyclerListView = this.listView;
        ListAdapter listAdapter = new ListAdapter(context);
        this.listViewAdapter = listAdapter;
        recyclerListView.setAdapter(listAdapter);
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
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new ChatRightsEditActivity$$ExternalSyntheticLambda8(this, context));
        return this.fragmentView;
    }

    /* renamed from: lambda$createView$6$org-telegram-ui-ChatRightsEditActivity  reason: not valid java name */
    public /* synthetic */ void m1947lambda$createView$6$orgtelegramuiChatRightsEditActivity(Context context, View view, int position) {
        boolean z;
        String text;
        Context context2 = context;
        View view2 = view;
        int i = position;
        if (!this.canEdit && (!this.currentChat.creator || this.currentType != 0 || i != this.anonymousRow)) {
            return;
        }
        if (i == 0) {
            Bundle args = new Bundle();
            args.putLong("user_id", this.currentUser.id);
            presentFragment(new ProfileActivity(args));
            return;
        }
        int i2 = 1;
        if (i == this.removeAdminRow) {
            int i3 = this.currentType;
            if (i3 == 0) {
                MessagesController.getInstance(this.currentAccount).setUserAdminRole(this.chatId, this.currentUser, new TLRPC.TL_chatAdminRights(), this.currentRank, this.isChannel, getFragmentForAlert(0), this.isAddingNew);
                ChatRightsEditActivityDelegate chatRightsEditActivityDelegate = this.delegate;
                if (chatRightsEditActivityDelegate != null) {
                    chatRightsEditActivityDelegate.didSetRights(0, this.adminRights, this.bannedRights, this.currentRank);
                }
                finishFragment();
            } else if (i3 == 1) {
                TLRPC.TL_chatBannedRights tL_chatBannedRights = new TLRPC.TL_chatBannedRights();
                this.bannedRights = tL_chatBannedRights;
                tL_chatBannedRights.view_messages = true;
                this.bannedRights.send_media = true;
                this.bannedRights.send_messages = true;
                this.bannedRights.send_stickers = true;
                this.bannedRights.send_gifs = true;
                this.bannedRights.send_games = true;
                this.bannedRights.send_inline = true;
                this.bannedRights.embed_links = true;
                this.bannedRights.pin_messages = true;
                this.bannedRights.send_polls = true;
                this.bannedRights.invite_users = true;
                this.bannedRights.change_info = true;
                this.bannedRights.until_date = 0;
                onDonePressed();
            }
        } else if (i == this.transferOwnerRow) {
            m1955lambda$initTransfer$8$orgtelegramuiChatRightsEditActivity((TLRPC.InputCheckPasswordSRP) null, (TwoStepVerificationActivity) null);
        } else if (i == this.untilDateRow) {
            if (getParentActivity() != null) {
                BottomSheet.Builder builder = new BottomSheet.Builder(context2);
                builder.setApplyTopPadding(false);
                LinearLayout linearLayout = new LinearLayout(context2);
                linearLayout.setOrientation(1);
                HeaderCell headerCell = new HeaderCell(context, "dialogTextBlue2", 23, 15, false);
                headerCell.setHeight(47);
                headerCell.setText(LocaleController.getString("UserRestrictionsDuration", NUM));
                linearLayout.addView(headerCell);
                LinearLayout linearLayoutInviteContainer = new LinearLayout(context2);
                linearLayoutInviteContainer.setOrientation(1);
                linearLayout.addView(linearLayoutInviteContainer, LayoutHelper.createLinear(-1, -2));
                BottomSheet.BottomSheetCell[] buttons = new BottomSheet.BottomSheetCell[5];
                int a = 0;
                while (a < buttons.length) {
                    buttons[a] = new BottomSheet.BottomSheetCell(context2, 0);
                    buttons[a].setPadding(AndroidUtilities.dp(7.0f), 0, AndroidUtilities.dp(7.0f), 0);
                    buttons[a].setTag(Integer.valueOf(a));
                    buttons[a].setBackgroundDrawable(Theme.getSelectorDrawable(false));
                    switch (a) {
                        case 0:
                            text = LocaleController.getString("UserRestrictionsUntilForever", NUM);
                            break;
                        case 1:
                            text = LocaleController.formatPluralString("Days", i2);
                            break;
                        case 2:
                            text = LocaleController.formatPluralString("Weeks", i2);
                            break;
                        case 3:
                            text = LocaleController.formatPluralString("Months", i2);
                            break;
                        default:
                            text = LocaleController.getString("UserRestrictionsCustom", NUM);
                            break;
                    }
                    buttons[a].setTextAndIcon((CharSequence) text, 0);
                    linearLayoutInviteContainer.addView(buttons[a], LayoutHelper.createLinear(-1, -2));
                    buttons[a].setOnClickListener(new ChatRightsEditActivity$$ExternalSyntheticLambda18(this, builder));
                    a++;
                    i2 = 1;
                }
                builder.setCustomView(linearLayout);
                showDialog(builder.create());
            }
        } else if (view2 instanceof TextCheckCell2) {
            TextCheckCell2 checkCell = (TextCheckCell2) view2;
            if (checkCell.hasIcon()) {
                Toast.makeText(getParentActivity(), LocaleController.getString("UserRestrictionsDisabled", NUM), 0).show();
            } else if (checkCell.isEnabled()) {
                checkCell.setChecked(!checkCell.isChecked());
                if (i == this.changeInfoRow) {
                    if (this.currentType == 0) {
                        TLRPC.TL_chatAdminRights tL_chatAdminRights = this.adminRights;
                        tL_chatAdminRights.change_info = !tL_chatAdminRights.change_info;
                        z = true;
                    } else {
                        TLRPC.TL_chatBannedRights tL_chatBannedRights2 = this.bannedRights;
                        tL_chatBannedRights2.change_info = !tL_chatBannedRights2.change_info;
                        z = true;
                    }
                } else if (i == this.postMessagesRow) {
                    TLRPC.TL_chatAdminRights tL_chatAdminRights2 = this.adminRights;
                    tL_chatAdminRights2.post_messages = !tL_chatAdminRights2.post_messages;
                    z = true;
                } else if (i == this.editMesagesRow) {
                    TLRPC.TL_chatAdminRights tL_chatAdminRights3 = this.adminRights;
                    tL_chatAdminRights3.edit_messages = !tL_chatAdminRights3.edit_messages;
                    z = true;
                } else if (i == this.deleteMessagesRow) {
                    TLRPC.TL_chatAdminRights tL_chatAdminRights4 = this.adminRights;
                    tL_chatAdminRights4.delete_messages = !tL_chatAdminRights4.delete_messages;
                    z = true;
                } else if (i == this.addAdminsRow) {
                    TLRPC.TL_chatAdminRights tL_chatAdminRights5 = this.adminRights;
                    tL_chatAdminRights5.add_admins = !tL_chatAdminRights5.add_admins;
                    z = true;
                } else if (i == this.anonymousRow) {
                    TLRPC.TL_chatAdminRights tL_chatAdminRights6 = this.adminRights;
                    tL_chatAdminRights6.anonymous = !tL_chatAdminRights6.anonymous;
                    z = true;
                } else if (i == this.banUsersRow) {
                    TLRPC.TL_chatAdminRights tL_chatAdminRights7 = this.adminRights;
                    tL_chatAdminRights7.ban_users = !tL_chatAdminRights7.ban_users;
                    z = true;
                } else if (i == this.startVoiceChatRow) {
                    TLRPC.TL_chatAdminRights tL_chatAdminRights8 = this.adminRights;
                    tL_chatAdminRights8.manage_call = !tL_chatAdminRights8.manage_call;
                    z = true;
                } else if (i == this.addUsersRow) {
                    if (this.currentType == 0) {
                        TLRPC.TL_chatAdminRights tL_chatAdminRights9 = this.adminRights;
                        tL_chatAdminRights9.invite_users = !tL_chatAdminRights9.invite_users;
                        z = true;
                    } else {
                        TLRPC.TL_chatBannedRights tL_chatBannedRights3 = this.bannedRights;
                        tL_chatBannedRights3.invite_users = !tL_chatBannedRights3.invite_users;
                        z = true;
                    }
                } else if (i == this.pinMessagesRow) {
                    if (this.currentType == 0) {
                        TLRPC.TL_chatAdminRights tL_chatAdminRights10 = this.adminRights;
                        tL_chatAdminRights10.pin_messages = !tL_chatAdminRights10.pin_messages;
                        z = true;
                    } else {
                        TLRPC.TL_chatBannedRights tL_chatBannedRights4 = this.bannedRights;
                        tL_chatBannedRights4.pin_messages = !tL_chatBannedRights4.pin_messages;
                        z = true;
                    }
                } else if (this.bannedRights != null) {
                    boolean disabled = !checkCell.isChecked();
                    if (i == this.sendMessagesRow) {
                        TLRPC.TL_chatBannedRights tL_chatBannedRights5 = this.bannedRights;
                        tL_chatBannedRights5.send_messages = !tL_chatBannedRights5.send_messages;
                    } else if (i == this.sendMediaRow) {
                        TLRPC.TL_chatBannedRights tL_chatBannedRights6 = this.bannedRights;
                        tL_chatBannedRights6.send_media = !tL_chatBannedRights6.send_media;
                    } else if (i == this.sendStickersRow) {
                        TLRPC.TL_chatBannedRights tL_chatBannedRights7 = this.bannedRights;
                        boolean z2 = !tL_chatBannedRights7.send_stickers;
                        tL_chatBannedRights7.send_inline = z2;
                        tL_chatBannedRights7.send_gifs = z2;
                        tL_chatBannedRights7.send_games = z2;
                        tL_chatBannedRights7.send_stickers = z2;
                    } else if (i == this.embedLinksRow) {
                        TLRPC.TL_chatBannedRights tL_chatBannedRights8 = this.bannedRights;
                        tL_chatBannedRights8.embed_links = !tL_chatBannedRights8.embed_links;
                    } else if (i == this.sendPollsRow) {
                        TLRPC.TL_chatBannedRights tL_chatBannedRights9 = this.bannedRights;
                        tL_chatBannedRights9.send_polls = !tL_chatBannedRights9.send_polls;
                    }
                    if (disabled) {
                        if (this.bannedRights.view_messages && !this.bannedRights.send_messages) {
                            this.bannedRights.send_messages = true;
                            RecyclerView.ViewHolder holder = this.listView.findViewHolderForAdapterPosition(this.sendMessagesRow);
                            if (holder != null) {
                                ((TextCheckCell2) holder.itemView).setChecked(false);
                            }
                        }
                        if ((this.bannedRights.view_messages || this.bannedRights.send_messages) && !this.bannedRights.send_media) {
                            this.bannedRights.send_media = true;
                            RecyclerView.ViewHolder holder2 = this.listView.findViewHolderForAdapterPosition(this.sendMediaRow);
                            if (holder2 != null) {
                                ((TextCheckCell2) holder2.itemView).setChecked(false);
                            }
                        }
                        if ((this.bannedRights.view_messages || this.bannedRights.send_messages) && !this.bannedRights.send_polls) {
                            this.bannedRights.send_polls = true;
                            RecyclerView.ViewHolder holder3 = this.listView.findViewHolderForAdapterPosition(this.sendPollsRow);
                            if (holder3 != null) {
                                ((TextCheckCell2) holder3.itemView).setChecked(false);
                            }
                        }
                        if ((this.bannedRights.view_messages || this.bannedRights.send_messages) && !this.bannedRights.send_stickers) {
                            TLRPC.TL_chatBannedRights tL_chatBannedRights10 = this.bannedRights;
                            tL_chatBannedRights10.send_inline = true;
                            tL_chatBannedRights10.send_gifs = true;
                            tL_chatBannedRights10.send_games = true;
                            tL_chatBannedRights10.send_stickers = true;
                            RecyclerView.ViewHolder holder4 = this.listView.findViewHolderForAdapterPosition(this.sendStickersRow);
                            if (holder4 != null) {
                                ((TextCheckCell2) holder4.itemView).setChecked(false);
                            }
                        }
                        if ((this.bannedRights.view_messages || this.bannedRights.send_messages) && !this.bannedRights.embed_links) {
                            this.bannedRights.embed_links = true;
                            RecyclerView.ViewHolder holder5 = this.listView.findViewHolderForAdapterPosition(this.embedLinksRow);
                            if (holder5 != null) {
                                ((TextCheckCell2) holder5.itemView).setChecked(false);
                            }
                            z = true;
                        } else {
                            z = true;
                        }
                    } else {
                        if ((!this.bannedRights.send_messages || !this.bannedRights.embed_links || !this.bannedRights.send_inline || !this.bannedRights.send_media || !this.bannedRights.send_polls) && this.bannedRights.view_messages) {
                            this.bannedRights.view_messages = false;
                        }
                        if (this.bannedRights.embed_links && this.bannedRights.send_inline && this.bannedRights.send_media && this.bannedRights.send_polls) {
                            z = true;
                        } else if (this.bannedRights.send_messages) {
                            this.bannedRights.send_messages = false;
                            RecyclerView.ViewHolder holder6 = this.listView.findViewHolderForAdapterPosition(this.sendMessagesRow);
                            if (holder6 != null) {
                                z = true;
                                ((TextCheckCell2) holder6.itemView).setChecked(true);
                            } else {
                                z = true;
                            }
                        } else {
                            z = true;
                        }
                    }
                } else {
                    z = true;
                }
                updateRows(z);
            }
        }
    }

    /* renamed from: lambda$createView$5$org-telegram-ui-ChatRightsEditActivity  reason: not valid java name */
    public /* synthetic */ void m1946lambda$createView$5$orgtelegramuiChatRightsEditActivity(BottomSheet.Builder builder, View v2) {
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
                    DatePickerDialog datePickerDialog = new DatePickerDialog(getParentActivity(), new ChatRightsEditActivity$$ExternalSyntheticLambda0(this), calendar.get(1), calendar.get(2), calendar.get(5));
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
                    datePickerDialog.setButton(-2, LocaleController.getString("Cancel", NUM), ChatRightsEditActivity$$ExternalSyntheticLambda16.INSTANCE);
                    if (Build.VERSION.SDK_INT >= 21) {
                        datePickerDialog.setOnShowListener(new ChatRightsEditActivity$$ExternalSyntheticLambda17(datePicker));
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
    public /* synthetic */ void m1945lambda$createView$2$orgtelegramuiChatRightsEditActivity(DatePicker view1, int year1, int month, int dayOfMonth1) {
        Calendar calendar1 = Calendar.getInstance();
        calendar1.clear();
        calendar1.set(year1, month, dayOfMonth1);
        try {
            TimePickerDialog timePickerDialog = new TimePickerDialog(getParentActivity(), new ChatRightsEditActivity$$ExternalSyntheticLambda10(this, (int) (calendar1.getTime().getTime() / 1000)), 0, 0, true);
            timePickerDialog.setButton(-1, LocaleController.getString("Set", NUM), timePickerDialog);
            timePickerDialog.setButton(-2, LocaleController.getString("Cancel", NUM), ChatRightsEditActivity$$ExternalSyntheticLambda15.INSTANCE);
            showDialog(timePickerDialog);
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    /* renamed from: lambda$createView$0$org-telegram-ui-ChatRightsEditActivity  reason: not valid java name */
    public /* synthetic */ void m1944lambda$createView$0$orgtelegramuiChatRightsEditActivity(int time, TimePicker view11, int hourOfDay, int minute) {
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
    public void m1955lambda$initTransfer$8$orgtelegramuiChatRightsEditActivity(TLRPC.InputCheckPasswordSRP srp, TwoStepVerificationActivity passwordFragment) {
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
                getConnectionsManager().sendRequest(req, new ChatRightsEditActivity$$ExternalSyntheticLambda5(this, srp, passwordFragment, req));
                return;
            }
            MessagesController.getInstance(this.currentAccount).convertToMegaGroup(getParentActivity(), this.chatId, this, new ChatRightsEditActivity$$ExternalSyntheticLambda4(this, srp, passwordFragment));
        }
    }

    /* renamed from: lambda$initTransfer$7$org-telegram-ui-ChatRightsEditActivity  reason: not valid java name */
    public /* synthetic */ void m1954lambda$initTransfer$7$orgtelegramuiChatRightsEditActivity(TLRPC.InputCheckPasswordSRP srp, TwoStepVerificationActivity passwordFragment, long param) {
        if (param != 0) {
            this.chatId = param;
            this.currentChat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(param));
            m1955lambda$initTransfer$8$orgtelegramuiChatRightsEditActivity(srp, passwordFragment);
        }
    }

    /* renamed from: lambda$initTransfer$14$org-telegram-ui-ChatRightsEditActivity  reason: not valid java name */
    public /* synthetic */ void m1953lambda$initTransfer$14$orgtelegramuiChatRightsEditActivity(TLRPC.InputCheckPasswordSRP srp, TwoStepVerificationActivity passwordFragment, TLRPC.TL_channels_editCreator req, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new ChatRightsEditActivity$$ExternalSyntheticLambda2(this, error, srp, passwordFragment, req));
    }

    /* renamed from: lambda$initTransfer$13$org-telegram-ui-ChatRightsEditActivity  reason: not valid java name */
    public /* synthetic */ void m1952lambda$initTransfer$13$orgtelegramuiChatRightsEditActivity(TLRPC.TL_error error, TLRPC.InputCheckPasswordSRP srp, TwoStepVerificationActivity passwordFragment, TLRPC.TL_channels_editCreator req) {
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
                    ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC.TL_account_getPassword(), new ChatRightsEditActivity$$ExternalSyntheticLambda6(this, twoStepVerificationActivity), 8);
                    TLRPC.TL_channels_editCreator tL_channels_editCreator4 = req;
                    return;
                } else if (tL_error.text.equals("CHANNELS_TOO_MUCH")) {
                    presentFragment(new TooManyCommunitiesActivity(1));
                    TLRPC.TL_channels_editCreator tL_channels_editCreator5 = req;
                    return;
                } else {
                    if (twoStepVerificationActivity != null) {
                        passwordFragment.needHideProgress();
                        passwordFragment.finishFragment();
                    }
                    AlertsCreator.showAddUserAlert(tL_error.text, this, this.isChannel, req);
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
                int i = 3;
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
                    builder.setPositiveButton(LocaleController.getString("EditAdminTransferSetPassword", NUM), new ChatRightsEditActivity$$ExternalSyntheticLambda13(this));
                    builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                } else {
                    TextView messageTextView4 = new TextView(getParentActivity());
                    messageTextView4.setTextColor(Theme.getColor("dialogTextBlack"));
                    messageTextView4.setTextSize(1, 16.0f);
                    if (LocaleController.isRTL) {
                        i = 5;
                    }
                    messageTextView4.setGravity(i | 48);
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
                builder2.setPositiveButton(LocaleController.getString("EditAdminTransferChangeOwner", NUM), new ChatRightsEditActivity$$ExternalSyntheticLambda14(this));
                builder2.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                showDialog(builder2.create());
                TLRPC.TL_channels_editCreator tL_channels_editCreator6 = req;
            } else {
                TLRPC.TL_channels_editCreator tL_channels_editCreator7 = req;
            }
        }
    }

    /* renamed from: lambda$initTransfer$9$org-telegram-ui-ChatRightsEditActivity  reason: not valid java name */
    public /* synthetic */ void m1956lambda$initTransfer$9$orgtelegramuiChatRightsEditActivity(DialogInterface dialogInterface, int i) {
        TwoStepVerificationActivity fragment = new TwoStepVerificationActivity();
        fragment.setDelegate(new ChatRightsEditActivity$$ExternalSyntheticLambda9(this, fragment));
        presentFragment(fragment);
    }

    /* renamed from: lambda$initTransfer$10$org-telegram-ui-ChatRightsEditActivity  reason: not valid java name */
    public /* synthetic */ void m1949lambda$initTransfer$10$orgtelegramuiChatRightsEditActivity(DialogInterface dialogInterface, int i) {
        presentFragment(new TwoStepVerificationSetupActivity(6, (TLRPC.TL_account_password) null));
    }

    /* renamed from: lambda$initTransfer$12$org-telegram-ui-ChatRightsEditActivity  reason: not valid java name */
    public /* synthetic */ void m1951lambda$initTransfer$12$orgtelegramuiChatRightsEditActivity(TwoStepVerificationActivity passwordFragment, TLObject response2, TLRPC.TL_error error2) {
        AndroidUtilities.runOnUIThread(new ChatRightsEditActivity$$ExternalSyntheticLambda1(this, error2, response2, passwordFragment));
    }

    /* renamed from: lambda$initTransfer$11$org-telegram-ui-ChatRightsEditActivity  reason: not valid java name */
    public /* synthetic */ void m1950lambda$initTransfer$11$orgtelegramuiChatRightsEditActivity(TLRPC.TL_error error2, TLObject response2, TwoStepVerificationActivity passwordFragment) {
        if (error2 == null) {
            TLRPC.TL_account_password currentPassword = (TLRPC.TL_account_password) response2;
            passwordFragment.setCurrentPasswordInfo((byte[]) null, currentPassword);
            TwoStepVerificationActivity.initPasswordNewAlgo(currentPassword);
            m1955lambda$initTransfer$8$orgtelegramuiChatRightsEditActivity(passwordFragment.getNewSrpPassword(), passwordFragment);
        }
    }

    private void updateRows(boolean update) {
        int i;
        int transferOwnerShadowRowPrev = Math.min(this.transferOwnerShadowRow, this.transferOwnerRow);
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
        this.rowCount = 3;
        int i2 = this.currentType;
        if (i2 == 0) {
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
                int i9 = 3 + 1;
                this.rowCount = i9;
                this.changeInfoRow = 3;
                int i10 = i9 + 1;
                this.rowCount = i10;
                this.deleteMessagesRow = i9;
                int i11 = i10 + 1;
                this.rowCount = i11;
                this.banUsersRow = i10;
                int i12 = i11 + 1;
                this.rowCount = i12;
                this.addUsersRow = i11;
                int i13 = i12 + 1;
                this.rowCount = i13;
                this.pinMessagesRow = i12;
                int i14 = i13 + 1;
                this.rowCount = i14;
                this.startVoiceChatRow = i13;
                int i15 = i14 + 1;
                this.rowCount = i15;
                this.addAdminsRow = i14;
                this.rowCount = i15 + 1;
                this.anonymousRow = i15;
            }
        } else if (i2 == 1) {
            int i16 = 3 + 1;
            this.rowCount = i16;
            this.sendMessagesRow = 3;
            int i17 = i16 + 1;
            this.rowCount = i17;
            this.sendMediaRow = i16;
            int i18 = i17 + 1;
            this.rowCount = i18;
            this.sendStickersRow = i17;
            int i19 = i18 + 1;
            this.rowCount = i19;
            this.sendPollsRow = i18;
            int i20 = i19 + 1;
            this.rowCount = i20;
            this.embedLinksRow = i19;
            int i21 = i20 + 1;
            this.rowCount = i21;
            this.addUsersRow = i20;
            int i22 = i21 + 1;
            this.rowCount = i22;
            this.pinMessagesRow = i21;
            int i23 = i22 + 1;
            this.rowCount = i23;
            this.changeInfoRow = i22;
            int i24 = i23 + 1;
            this.rowCount = i24;
            this.untilSectionRow = i23;
            this.rowCount = i24 + 1;
            this.untilDateRow = i24;
        }
        if (this.canEdit) {
            if (!this.isChannel && i2 == 0) {
                int i25 = this.rowCount;
                int i26 = i25 + 1;
                this.rowCount = i26;
                this.rightsShadowRow = i25;
                int i27 = i26 + 1;
                this.rowCount = i27;
                this.rankHeaderRow = i26;
                int i28 = i27 + 1;
                this.rowCount = i28;
                this.rankRow = i27;
                this.rowCount = i28 + 1;
                this.rankInfoRow = i28;
            }
            TLRPC.Chat chat = this.currentChat;
            if (chat != null && chat.creator && this.currentType == 0 && hasAllAdminRights() && !this.currentUser.bot) {
                int i29 = this.rightsShadowRow;
                if (i29 == -1) {
                    int i30 = this.rowCount;
                    this.rowCount = i30 + 1;
                    this.transferOwnerShadowRow = i30;
                }
                int i31 = this.rowCount;
                int i32 = i31 + 1;
                this.rowCount = i32;
                this.transferOwnerRow = i31;
                if (i29 != -1) {
                    this.rowCount = i32 + 1;
                    this.transferOwnerShadowRow = i32;
                }
            }
            if (this.initialIsSet) {
                if (this.rightsShadowRow == -1) {
                    int i33 = this.rowCount;
                    this.rowCount = i33 + 1;
                    this.rightsShadowRow = i33;
                }
                int i34 = this.rowCount;
                int i35 = i34 + 1;
                this.rowCount = i35;
                this.removeAdminRow = i34;
                this.rowCount = i35 + 1;
                this.removeAdminShadowRow = i35;
            }
        } else if (i2 != 0) {
            int i36 = this.rowCount;
            this.rowCount = i36 + 1;
            this.rightsShadowRow = i36;
        } else if (this.isChannel || i2 != 0 || (this.currentRank.isEmpty() && (!this.currentChat.creator || !UserObject.isUserSelf(this.currentUser)))) {
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
    /* JADX WARNING: Code restructure failed: missing block: B:10:0x0027, code lost:
        if (r0.codePointCount(0, r0.length()) > 16) goto L_0x0029;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onDonePressed() {
        /*
            r14 = this;
            org.telegram.tgnet.TLRPC$Chat r0 = r14.currentChat
            boolean r0 = org.telegram.messenger.ChatObject.isChannel(r0)
            r1 = 16
            r2 = -1
            r3 = 1
            r4 = 0
            if (r0 != 0) goto L_0x003f
            int r0 = r14.currentType
            if (r0 == r3) goto L_0x0029
            if (r0 != 0) goto L_0x003f
            boolean r0 = r14.isDefaultAdminRights()
            if (r0 == 0) goto L_0x0029
            int r0 = r14.rankRow
            if (r0 == r2) goto L_0x003f
            java.lang.String r0 = r14.currentRank
            int r5 = r0.length()
            int r0 = r0.codePointCount(r4, r5)
            if (r0 <= r1) goto L_0x003f
        L_0x0029:
            int r0 = r14.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r0)
            android.app.Activity r2 = r14.getParentActivity()
            long r3 = r14.chatId
            org.telegram.ui.ChatRightsEditActivity$$ExternalSyntheticLambda3 r6 = new org.telegram.ui.ChatRightsEditActivity$$ExternalSyntheticLambda3
            r6.<init>(r14)
            r5 = r14
            r1.convertToMegaGroup(r2, r3, r5, r6)
            return
        L_0x003f:
            int r0 = r14.currentType
            if (r0 != 0) goto L_0x0143
            int r0 = r14.rankRow
            if (r0 == r2) goto L_0x007f
            java.lang.String r0 = r14.currentRank
            int r2 = r0.length()
            int r0 = r0.codePointCount(r4, r2)
            if (r0 <= r1) goto L_0x007f
            org.telegram.ui.Components.RecyclerListView r0 = r14.listView
            int r1 = r14.rankRow
            r0.smoothScrollToPosition(r1)
            android.app.Activity r0 = r14.getParentActivity()
            java.lang.String r1 = "vibrator"
            java.lang.Object r0 = r0.getSystemService(r1)
            android.os.Vibrator r0 = (android.os.Vibrator) r0
            if (r0 == 0) goto L_0x006d
            r1 = 200(0xc8, double:9.9E-322)
            r0.vibrate(r1)
        L_0x006d:
            org.telegram.ui.Components.RecyclerListView r1 = r14.listView
            int r2 = r14.rankHeaderRow
            androidx.recyclerview.widget.RecyclerView$ViewHolder r1 = r1.findViewHolderForAdapterPosition(r2)
            if (r1 == 0) goto L_0x007e
            android.view.View r2 = r1.itemView
            r3 = 1073741824(0x40000000, float:2.0)
            org.telegram.messenger.AndroidUtilities.shakeView(r2, r3, r4)
        L_0x007e:
            return
        L_0x007f:
            boolean r0 = r14.isChannel
            if (r0 == 0) goto L_0x008a
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r0 = r14.adminRights
            r0.ban_users = r4
            r0.pin_messages = r4
            goto L_0x0090
        L_0x008a:
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r0 = r14.adminRights
            r0.edit_messages = r4
            r0.post_messages = r4
        L_0x0090:
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r0 = r14.adminRights
            boolean r0 = r0.change_info
            if (r0 != 0) goto L_0x00d1
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r0 = r14.adminRights
            boolean r0 = r0.post_messages
            if (r0 != 0) goto L_0x00d1
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r0 = r14.adminRights
            boolean r0 = r0.edit_messages
            if (r0 != 0) goto L_0x00d1
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r0 = r14.adminRights
            boolean r0 = r0.delete_messages
            if (r0 != 0) goto L_0x00d1
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r0 = r14.adminRights
            boolean r0 = r0.ban_users
            if (r0 != 0) goto L_0x00d1
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r0 = r14.adminRights
            boolean r0 = r0.invite_users
            if (r0 != 0) goto L_0x00d1
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r0 = r14.adminRights
            boolean r0 = r0.pin_messages
            if (r0 != 0) goto L_0x00d1
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r0 = r14.adminRights
            boolean r0 = r0.add_admins
            if (r0 != 0) goto L_0x00d1
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r0 = r14.adminRights
            boolean r0 = r0.anonymous
            if (r0 != 0) goto L_0x00d1
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r0 = r14.adminRights
            boolean r0 = r0.manage_call
            if (r0 != 0) goto L_0x00d1
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r0 = r14.adminRights
            r0.other = r3
            goto L_0x00d5
        L_0x00d1:
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r0 = r14.adminRights
            r0.other = r4
        L_0x00d5:
            int r0 = r14.currentAccount
            org.telegram.messenger.MessagesController r5 = org.telegram.messenger.MessagesController.getInstance(r0)
            long r6 = r14.chatId
            org.telegram.tgnet.TLRPC$User r8 = r14.currentUser
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r9 = r14.adminRights
            java.lang.String r10 = r14.currentRank
            boolean r11 = r14.isChannel
            org.telegram.ui.ActionBar.BaseFragment r12 = r14.getFragmentForAlert(r3)
            boolean r13 = r14.isAddingNew
            r5.setUserAdminRole(r6, r8, r9, r10, r11, r12, r13)
            org.telegram.ui.ChatRightsEditActivity$ChatRightsEditActivityDelegate r0 = r14.delegate
            if (r0 == 0) goto L_0x019a
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r1 = r14.adminRights
            boolean r1 = r1.change_info
            if (r1 != 0) goto L_0x0138
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r1 = r14.adminRights
            boolean r1 = r1.post_messages
            if (r1 != 0) goto L_0x0138
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r1 = r14.adminRights
            boolean r1 = r1.edit_messages
            if (r1 != 0) goto L_0x0138
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r1 = r14.adminRights
            boolean r1 = r1.delete_messages
            if (r1 != 0) goto L_0x0138
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r1 = r14.adminRights
            boolean r1 = r1.ban_users
            if (r1 != 0) goto L_0x0138
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r1 = r14.adminRights
            boolean r1 = r1.invite_users
            if (r1 != 0) goto L_0x0138
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r1 = r14.adminRights
            boolean r1 = r1.pin_messages
            if (r1 != 0) goto L_0x0138
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r1 = r14.adminRights
            boolean r1 = r1.add_admins
            if (r1 != 0) goto L_0x0138
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r1 = r14.adminRights
            boolean r1 = r1.anonymous
            if (r1 != 0) goto L_0x0138
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r1 = r14.adminRights
            boolean r1 = r1.manage_call
            if (r1 != 0) goto L_0x0138
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r1 = r14.adminRights
            boolean r1 = r1.other
            if (r1 == 0) goto L_0x0136
            goto L_0x0138
        L_0x0136:
            r3 = 0
            goto L_0x0139
        L_0x0138:
        L_0x0139:
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r1 = r14.adminRights
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r2 = r14.bannedRights
            java.lang.String r4 = r14.currentRank
            r0.didSetRights(r3, r1, r2, r4)
            goto L_0x019a
        L_0x0143:
            if (r0 != r3) goto L_0x019a
            int r0 = r14.currentAccount
            org.telegram.messenger.MessagesController r5 = org.telegram.messenger.MessagesController.getInstance(r0)
            long r6 = r14.chatId
            org.telegram.tgnet.TLRPC$User r8 = r14.currentUser
            r9 = 0
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r10 = r14.bannedRights
            boolean r11 = r14.isChannel
            org.telegram.ui.ActionBar.BaseFragment r12 = r14.getFragmentForAlert(r3)
            r5.setParticipantBannedRole(r6, r8, r9, r10, r11, r12)
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r0 = r14.bannedRights
            boolean r0 = r0.send_messages
            if (r0 != 0) goto L_0x018c
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r0 = r14.bannedRights
            boolean r0 = r0.send_stickers
            if (r0 != 0) goto L_0x018c
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r0 = r14.bannedRights
            boolean r0 = r0.embed_links
            if (r0 != 0) goto L_0x018c
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r0 = r14.bannedRights
            boolean r0 = r0.send_media
            if (r0 != 0) goto L_0x018c
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r0 = r14.bannedRights
            boolean r0 = r0.send_gifs
            if (r0 != 0) goto L_0x018c
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r0 = r14.bannedRights
            boolean r0 = r0.send_games
            if (r0 != 0) goto L_0x018c
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r0 = r14.bannedRights
            boolean r0 = r0.send_inline
            if (r0 == 0) goto L_0x0186
            goto L_0x018c
        L_0x0186:
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r0 = r14.bannedRights
            r0.until_date = r4
            r0 = 2
            goto L_0x018d
        L_0x018c:
            r0 = 1
        L_0x018d:
            org.telegram.ui.ChatRightsEditActivity$ChatRightsEditActivityDelegate r1 = r14.delegate
            if (r1 == 0) goto L_0x019a
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r2 = r14.adminRights
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r3 = r14.bannedRights
            java.lang.String r4 = r14.currentRank
            r1.didSetRights(r0, r2, r3, r4)
        L_0x019a:
            r14.finishFragment()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChatRightsEditActivity.onDonePressed():void");
    }

    /* renamed from: lambda$onDonePressed$15$org-telegram-ui-ChatRightsEditActivity  reason: not valid java name */
    public /* synthetic */ void m1957lambda$onDonePressed$15$orgtelegramuiChatRightsEditActivity(long param) {
        if (param != 0) {
            this.chatId = param;
            this.currentChat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(param));
            onDonePressed();
        }
    }

    public void setDelegate(ChatRightsEditActivityDelegate channelRightsEditActivityDelegate) {
        this.delegate = channelRightsEditActivityDelegate;
    }

    /* access modifiers changed from: private */
    public boolean checkDiscard() {
        boolean changed;
        if (this.currentType == 1) {
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
        builder.setPositiveButton(LocaleController.getString("ApplyTheme", NUM), new ChatRightsEditActivity$$ExternalSyntheticLambda11(this));
        builder.setNegativeButton(LocaleController.getString("PassportDiscard", NUM), new ChatRightsEditActivity$$ExternalSyntheticLambda12(this));
        showDialog(builder.create());
        return false;
    }

    /* renamed from: lambda$checkDiscard$16$org-telegram-ui-ChatRightsEditActivity  reason: not valid java name */
    public /* synthetic */ void m1942lambda$checkDiscard$16$orgtelegramuiChatRightsEditActivity(DialogInterface dialogInterface, int i) {
        onDonePressed();
    }

    /* renamed from: lambda$checkDiscard$17$org-telegram-ui-ChatRightsEditActivity  reason: not valid java name */
    public /* synthetic */ void m1943lambda$checkDiscard$17$orgtelegramuiChatRightsEditActivity(DialogInterface dialog, int which) {
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
        /* access modifiers changed from: private */
        public boolean ignoreTextChange;
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int type = holder.getItemViewType();
            if (ChatRightsEditActivity.this.currentChat.creator && ChatRightsEditActivity.this.currentType == 0 && type == 4 && holder.getAdapterPosition() == ChatRightsEditActivity.this.anonymousRow) {
                return true;
            }
            if (!ChatRightsEditActivity.this.canEdit) {
                return false;
            }
            if (ChatRightsEditActivity.this.currentType == 0 && type == 4) {
                int position = holder.getAdapterPosition();
                if (position == ChatRightsEditActivity.this.changeInfoRow) {
                    return ChatRightsEditActivity.this.myAdminRights.change_info;
                }
                if (position == ChatRightsEditActivity.this.postMessagesRow) {
                    return ChatRightsEditActivity.this.myAdminRights.post_messages;
                }
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
                    return ChatRightsEditActivity.this.myAdminRights.pin_messages;
                }
            }
            if (type == 3 || type == 1 || type == 5) {
                return false;
            }
            return true;
        }

        public int getItemCount() {
            return ChatRightsEditActivity.this.rowCount;
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v0, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v1, resolved type: org.telegram.ui.Cells.UserCell2} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v3, resolved type: org.telegram.ui.Cells.TextInfoPrivacyCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v4, resolved type: org.telegram.ui.Cells.TextSettingsCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v6, resolved type: org.telegram.ui.Cells.TextCheckCell2} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v8, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v9, resolved type: org.telegram.ui.Cells.TextDetailCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v10, resolved type: org.telegram.ui.Cells.PollEditTextCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v11, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v12, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v13, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v7, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v14, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v15, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v16, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX WARNING: type inference failed for: r0v9, types: [org.telegram.ui.Cells.ShadowSectionCell] */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public androidx.recyclerview.widget.RecyclerView.ViewHolder onCreateViewHolder(android.view.ViewGroup r9, int r10) {
            /*
                r8 = this;
                java.lang.String r0 = "windowBackgroundWhite"
                switch(r10) {
                    case 0: goto L_0x0083;
                    case 1: goto L_0x006c;
                    case 2: goto L_0x005d;
                    case 3: goto L_0x0046;
                    case 4: goto L_0x0037;
                    case 5: goto L_0x002e;
                    case 6: goto L_0x001f;
                    default: goto L_0x0005;
                }
            L_0x0005:
                org.telegram.ui.Cells.PollEditTextCell r1 = new org.telegram.ui.Cells.PollEditTextCell
                android.content.Context r2 = r8.mContext
                r3 = 0
                r1.<init>(r2, r3)
                int r0 = org.telegram.ui.ActionBar.Theme.getColor(r0)
                r1.setBackgroundColor(r0)
                org.telegram.ui.ChatRightsEditActivity$ListAdapter$1 r0 = new org.telegram.ui.ChatRightsEditActivity$ListAdapter$1
                r0.<init>()
                r1.addTextWatcher(r0)
                r0 = r1
                goto L_0x0094
            L_0x001f:
                org.telegram.ui.Cells.TextDetailCell r1 = new org.telegram.ui.Cells.TextDetailCell
                android.content.Context r2 = r8.mContext
                r1.<init>(r2)
                int r0 = org.telegram.ui.ActionBar.Theme.getColor(r0)
                r1.setBackgroundColor(r0)
                goto L_0x0094
            L_0x002e:
                org.telegram.ui.Cells.ShadowSectionCell r0 = new org.telegram.ui.Cells.ShadowSectionCell
                android.content.Context r1 = r8.mContext
                r0.<init>(r1)
                r1 = r0
                goto L_0x0094
            L_0x0037:
                org.telegram.ui.Cells.TextCheckCell2 r1 = new org.telegram.ui.Cells.TextCheckCell2
                android.content.Context r2 = r8.mContext
                r1.<init>(r2)
                int r0 = org.telegram.ui.ActionBar.Theme.getColor(r0)
                r1.setBackgroundColor(r0)
                goto L_0x0094
            L_0x0046:
                org.telegram.ui.Cells.HeaderCell r1 = new org.telegram.ui.Cells.HeaderCell
                android.content.Context r3 = r8.mContext
                r5 = 21
                r6 = 15
                r7 = 1
                java.lang.String r4 = "windowBackgroundWhiteBlueHeader"
                r2 = r1
                r2.<init>(r3, r4, r5, r6, r7)
                int r0 = org.telegram.ui.ActionBar.Theme.getColor(r0)
                r1.setBackgroundColor(r0)
                goto L_0x0094
            L_0x005d:
                org.telegram.ui.Cells.TextSettingsCell r1 = new org.telegram.ui.Cells.TextSettingsCell
                android.content.Context r2 = r8.mContext
                r1.<init>(r2)
                int r0 = org.telegram.ui.ActionBar.Theme.getColor(r0)
                r1.setBackgroundColor(r0)
                goto L_0x0094
            L_0x006c:
                org.telegram.ui.Cells.TextInfoPrivacyCell r0 = new org.telegram.ui.Cells.TextInfoPrivacyCell
                android.content.Context r1 = r8.mContext
                r0.<init>(r1)
                r1 = r0
                android.content.Context r0 = r8.mContext
                r2 = 2131165466(0x7var_a, float:1.794515E38)
                java.lang.String r3 = "windowBackgroundGrayShadow"
                android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r0, (int) r2, (java.lang.String) r3)
                r1.setBackgroundDrawable(r0)
                goto L_0x0094
            L_0x0083:
                org.telegram.ui.Cells.UserCell2 r1 = new org.telegram.ui.Cells.UserCell2
                android.content.Context r2 = r8.mContext
                r3 = 4
                r4 = 0
                r1.<init>(r2, r3, r4)
                int r0 = org.telegram.ui.ActionBar.Theme.getColor(r0)
                r1.setBackgroundColor(r0)
            L_0x0094:
                org.telegram.ui.Components.RecyclerListView$Holder r0 = new org.telegram.ui.Components.RecyclerListView$Holder
                r0.<init>(r1)
                return r0
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChatRightsEditActivity.ListAdapter.onCreateViewHolder(android.view.ViewGroup, int):androidx.recyclerview.widget.RecyclerView$ViewHolder");
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            String hint;
            String value;
            String hint2;
            boolean z = false;
            switch (holder.getItemViewType()) {
                case 0:
                    ((UserCell2) holder.itemView).setData(ChatRightsEditActivity.this.currentUser, (CharSequence) null, (CharSequence) null, 0);
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
                        if (ChatRightsEditActivity.this.currentType == 0) {
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
                    int i = NUM;
                    if (position == ChatRightsEditActivity.this.changeInfoRow) {
                        if (ChatRightsEditActivity.this.currentType == 0) {
                            if (ChatRightsEditActivity.this.isChannel) {
                                checkCell.setTextAndCheck(LocaleController.getString("EditAdminChangeChannelInfo", NUM), ChatRightsEditActivity.this.adminRights.change_info, true);
                            } else {
                                checkCell.setTextAndCheck(LocaleController.getString("EditAdminChangeGroupInfo", NUM), ChatRightsEditActivity.this.adminRights.change_info, true);
                            }
                        } else if (ChatRightsEditActivity.this.currentType == 1) {
                            checkCell.setTextAndCheck(LocaleController.getString("UserRestrictionsChangeInfo", NUM), !ChatRightsEditActivity.this.bannedRights.change_info && !ChatRightsEditActivity.this.defaultBannedRights.change_info, false);
                            if (!ChatRightsEditActivity.this.defaultBannedRights.change_info) {
                                i = 0;
                            }
                            checkCell.setIcon(i);
                        }
                    } else if (position == ChatRightsEditActivity.this.postMessagesRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("EditAdminPostMessages", NUM), ChatRightsEditActivity.this.adminRights.post_messages, true);
                    } else if (position == ChatRightsEditActivity.this.editMesagesRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("EditAdminEditMessages", NUM), ChatRightsEditActivity.this.adminRights.edit_messages, true);
                    } else if (position == ChatRightsEditActivity.this.deleteMessagesRow) {
                        if (ChatRightsEditActivity.this.isChannel) {
                            checkCell.setTextAndCheck(LocaleController.getString("EditAdminDeleteMessages", NUM), ChatRightsEditActivity.this.adminRights.delete_messages, true);
                        } else {
                            checkCell.setTextAndCheck(LocaleController.getString("EditAdminGroupDeleteMessages", NUM), ChatRightsEditActivity.this.adminRights.delete_messages, true);
                        }
                    } else if (position == ChatRightsEditActivity.this.addAdminsRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("EditAdminAddAdmins", NUM), ChatRightsEditActivity.this.adminRights.add_admins, ChatRightsEditActivity.this.anonymousRow != -1);
                    } else if (position == ChatRightsEditActivity.this.anonymousRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("EditAdminSendAnonymously", NUM), ChatRightsEditActivity.this.adminRights.anonymous, false);
                    } else if (position == ChatRightsEditActivity.this.banUsersRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("EditAdminBanUsers", NUM), ChatRightsEditActivity.this.adminRights.ban_users, true);
                    } else if (position == ChatRightsEditActivity.this.startVoiceChatRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("StartVoipChatPermission", NUM), ChatRightsEditActivity.this.adminRights.manage_call, true);
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
                        }
                    } else if (position == ChatRightsEditActivity.this.pinMessagesRow) {
                        if (ChatRightsEditActivity.this.currentType == 0) {
                            checkCell.setTextAndCheck(LocaleController.getString("EditAdminPinMessages", NUM), ChatRightsEditActivity.this.adminRights.pin_messages, true);
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
                    if (position == ChatRightsEditActivity.this.sendMediaRow || position == ChatRightsEditActivity.this.sendStickersRow || position == ChatRightsEditActivity.this.embedLinksRow || position == ChatRightsEditActivity.this.sendPollsRow) {
                        if (!ChatRightsEditActivity.this.bannedRights.send_messages && !ChatRightsEditActivity.this.bannedRights.view_messages && !ChatRightsEditActivity.this.defaultBannedRights.send_messages && !ChatRightsEditActivity.this.defaultBannedRights.view_messages) {
                            z = true;
                        }
                        checkCell.setEnabled(z);
                        return;
                    } else if (position == ChatRightsEditActivity.this.sendMessagesRow) {
                        if (!ChatRightsEditActivity.this.bannedRights.view_messages && !ChatRightsEditActivity.this.defaultBannedRights.view_messages) {
                            z = true;
                        }
                        checkCell.setEnabled(z);
                        return;
                    } else {
                        return;
                    }
                case 5:
                    ShadowSectionCell shadowCell = (ShadowSectionCell) holder.itemView;
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
            if (position == ChatRightsEditActivity.this.changeInfoRow || position == ChatRightsEditActivity.this.postMessagesRow || position == ChatRightsEditActivity.this.editMesagesRow || position == ChatRightsEditActivity.this.deleteMessagesRow || position == ChatRightsEditActivity.this.addAdminsRow || position == ChatRightsEditActivity.this.banUsersRow || position == ChatRightsEditActivity.this.addUsersRow || position == ChatRightsEditActivity.this.pinMessagesRow || position == ChatRightsEditActivity.this.sendMessagesRow || position == ChatRightsEditActivity.this.sendMediaRow || position == ChatRightsEditActivity.this.sendStickersRow || position == ChatRightsEditActivity.this.embedLinksRow || position == ChatRightsEditActivity.this.sendPollsRow || position == ChatRightsEditActivity.this.anonymousRow || position == ChatRightsEditActivity.this.startVoiceChatRow) {
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
            return 2;
        }
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();
        ThemeDescription.ThemeDescriptionDelegate cellDelegate = new ChatRightsEditActivity$$ExternalSyntheticLambda7(this);
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

    /* renamed from: lambda$getThemeDescriptions$18$org-telegram-ui-ChatRightsEditActivity  reason: not valid java name */
    public /* synthetic */ void m1948x35330e7e() {
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
