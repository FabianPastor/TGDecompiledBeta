package org.telegram.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.SparseArray;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.support.widget.RecyclerView.Adapter;
import org.telegram.messenger.support.widget.RecyclerView.LayoutParams;
import org.telegram.messenger.support.widget.RecyclerView.OnScrollListener;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.ChannelParticipant;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.ChatFull;
import org.telegram.tgnet.TLRPC.ChatParticipant;
import org.telegram.tgnet.TLRPC.TL_channelParticipant;
import org.telegram.tgnet.TLRPC.TL_channelParticipantAdmin;
import org.telegram.tgnet.TLRPC.TL_channelParticipantBanned;
import org.telegram.tgnet.TLRPC.TL_channelParticipantCreator;
import org.telegram.tgnet.TLRPC.TL_channelParticipantSelf;
import org.telegram.tgnet.TLRPC.TL_channelParticipantsAdmins;
import org.telegram.tgnet.TLRPC.TL_channelParticipantsBanned;
import org.telegram.tgnet.TLRPC.TL_channelParticipantsKicked;
import org.telegram.tgnet.TLRPC.TL_channelParticipantsRecent;
import org.telegram.tgnet.TLRPC.TL_channels_channelParticipants;
import org.telegram.tgnet.TLRPC.TL_channels_editBanned;
import org.telegram.tgnet.TLRPC.TL_channels_getParticipants;
import org.telegram.tgnet.TLRPC.TL_chatAdminRights;
import org.telegram.tgnet.TLRPC.TL_chatBannedRights;
import org.telegram.tgnet.TLRPC.TL_chatParticipant;
import org.telegram.tgnet.TLRPC.TL_chatParticipantAdmin;
import org.telegram.tgnet.TLRPC.TL_chatParticipantCreator;
import org.telegram.tgnet.TLRPC.TL_contact;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.Updates;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate;
import org.telegram.ui.Adapters.SearchAdapterHelper;
import org.telegram.ui.Adapters.SearchAdapterHelper$SearchAdapterHelperDelegate$$CC;
import org.telegram.ui.Adapters.SearchAdapterHelper.HashtagObject;
import org.telegram.ui.Adapters.SearchAdapterHelper.SearchAdapterHelperDelegate;
import org.telegram.ui.Cells.GraySectionCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.ManageChatTextCell;
import org.telegram.ui.Cells.ManageChatUserCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCheckCell2;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;

public class ChatUsersActivity extends BaseFragment implements NotificationCenterDelegate {
    public static final int TYPE_ADMIN = 1;
    public static final int TYPE_BANNED = 0;
    public static final int TYPE_KICKED = 3;
    public static final int TYPE_USERS = 2;
    private static final int done_button = 1;
    private static final int search_button = 0;
    private int addNew2Row;
    private int addNewRow;
    private int addNewSectionRow;
    private int addUsersRow;
    private int blockedEmptyRow;
    private int changeInfoRow;
    private int chatId = this.arguments.getInt("chat_id");
    private Chat currentChat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.chatId));
    private TL_chatBannedRights defaultBannedRights = new TL_chatBannedRights();
    private ChatUsersActivityDelegate delegate;
    private ActionBarMenuItem doneItem;
    private int embedLinksRow;
    private EmptyTextProgressView emptyView;
    private boolean firstLoaded;
    private ChatFull info;
    private String initialBannedRights;
    private boolean isChannel;
    private RecyclerListView listView;
    private ListAdapter listViewAdapter;
    private boolean loadingUsers;
    private boolean needOpenSearch = this.arguments.getBoolean("open_search");
    private ArrayList<TLObject> participants = new ArrayList();
    private int participantsDivider2Row;
    private int participantsDividerRow;
    private int participantsEndRow;
    private int participantsInfoRow;
    private SparseArray<TLObject> participantsMap = new SparseArray();
    private int participantsStartRow;
    private int permissionsSectionRow;
    private int pinMessagesRow;
    private int recentActionsRow;
    private int removedUsersRow;
    private int restricted1SectionRow;
    private int rowCount;
    private ActionBarMenuItem searchItem;
    private SearchAdapter searchListViewAdapter;
    private boolean searchWas;
    private boolean searching;
    private int selectType = this.arguments.getInt("selectType");
    private int sendMediaRow;
    private int sendMessagesRow;
    private int sendPollsRow;
    private int sendStickersRow;
    private int type = this.arguments.getInt("type");

    public interface ChatUsersActivityDelegate {
        void didAddParticipantToList(int i, TLObject tLObject);
    }

    private class ListAdapter extends SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean isEnabled(ViewHolder holder) {
            boolean z = false;
            int type = holder.getItemViewType();
            if (type == 7) {
                return ChatObject.canBlockUsers(ChatUsersActivity.this.currentChat);
            }
            if (type == 0) {
                User user = holder.itemView.getCurrentUser();
                if (user == null || user.self) {
                    return false;
                }
                return true;
            }
            if (type == 0 || type == 2 || type == 6) {
                z = true;
            }
            return z;
        }

        public int getItemCount() {
            if (!ChatUsersActivity.this.loadingUsers || ChatUsersActivity.this.firstLoaded) {
                return ChatUsersActivity.this.rowCount;
            }
            return 0;
        }

        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    Context context = this.mContext;
                    int i = (ChatUsersActivity.this.type == 0 || ChatUsersActivity.this.type == 3) ? 7 : 6;
                    int i2 = (ChatUsersActivity.this.type == 0 || ChatUsersActivity.this.type == 3) ? 6 : 2;
                    view = new ManageChatUserCell(context, i, i2, ChatUsersActivity.this.selectType == 0);
                    view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    ((ManageChatUserCell) view).setDelegate(new ChatUsersActivity$ListAdapter$$Lambda$0(this));
                    break;
                case 1:
                    view = new TextInfoPrivacyCell(this.mContext);
                    break;
                case 2:
                    view = new ManageChatTextCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    break;
                case 3:
                    view = new ShadowSectionCell(this.mContext);
                    break;
                case 4:
                    view = new FrameLayout(this.mContext) {
                        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                            super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(heightMeasureSpec) - AndroidUtilities.dp(56.0f), NUM));
                        }
                    };
                    FrameLayout frameLayout = (FrameLayout) view;
                    frameLayout.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider_bottom, "windowBackgroundGrayShadow"));
                    LinearLayout linearLayout = new LinearLayout(this.mContext);
                    linearLayout.setOrientation(1);
                    frameLayout.addView(linearLayout, LayoutHelper.createFrame(-2, -2.0f, 17, 20.0f, 0.0f, 20.0f, 0.0f));
                    ImageView imageView = new ImageView(this.mContext);
                    imageView.setImageResource(R.drawable.group_ban_empty);
                    imageView.setScaleType(ScaleType.CENTER);
                    imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("emptyListPlaceholder"), Mode.MULTIPLY));
                    linearLayout.addView(imageView, LayoutHelper.createLinear(-2, -2, 1));
                    TextView textView = new TextView(this.mContext);
                    textView.setText(LocaleController.getString("NoBlockedUsers", R.string.NoBlockedUsers));
                    textView.setTextColor(Theme.getColor("emptyListPlaceholder"));
                    textView.setTextSize(1, 16.0f);
                    textView.setGravity(1);
                    textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                    linearLayout.addView(textView, LayoutHelper.createLinear(-2, -2, 1, 0, 10, 0, 0));
                    textView = new TextView(this.mContext);
                    if (ChatUsersActivity.this.isChannel) {
                        textView.setText(LocaleController.getString("NoBlockedChannel2", R.string.NoBlockedChannel2));
                    } else {
                        textView.setText(LocaleController.getString("NoBlockedGroup2", R.string.NoBlockedGroup2));
                    }
                    textView.setTextColor(Theme.getColor("emptyListPlaceholder"));
                    textView.setTextSize(1, 15.0f);
                    textView.setGravity(1);
                    linearLayout.addView(textView, LayoutHelper.createLinear(-2, -2, 1, 0, 10, 0, 0));
                    view.setLayoutParams(new LayoutParams(-1, -1));
                    break;
                case 5:
                    View headerCell = new HeaderCell(this.mContext, false, 21, 11, false);
                    headerCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    headerCell.setHeight(43);
                    view = headerCell;
                    break;
                case 6:
                    view = new TextSettingsCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    break;
                default:
                    view = new TextCheckCell2(this.mContext);
                    view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    break;
            }
            return new Holder(view);
        }

        final /* synthetic */ boolean lambda$onCreateViewHolder$0$ChatUsersActivity$ListAdapter(ManageChatUserCell cell, boolean click) {
            return ChatUsersActivity.this.createMenuForParticipant(ChatUsersActivity.this.listViewAdapter.getItem(((Integer) cell.getTag()).intValue()), !click);
        }

        public void onBindViewHolder(ViewHolder holder, int position) {
            boolean z;
            String string;
            switch (holder.getItemViewType()) {
                case 0:
                    int userId;
                    int kickedBy;
                    int promotedBy;
                    TL_chatBannedRights bannedRights;
                    boolean banned;
                    boolean creator;
                    boolean admin;
                    ManageChatUserCell userCell = (ManageChatUserCell) holder.itemView;
                    userCell.setTag(Integer.valueOf(position));
                    TLObject item = getItem(position);
                    if (item instanceof ChannelParticipant) {
                        ChannelParticipant participant = (ChannelParticipant) item;
                        userId = participant.user_id;
                        kickedBy = participant.kicked_by;
                        promotedBy = participant.promoted_by;
                        bannedRights = participant.banned_rights;
                        banned = participant instanceof TL_channelParticipantBanned;
                        creator = participant instanceof TL_channelParticipantCreator;
                        admin = participant instanceof TL_channelParticipantAdmin;
                    } else {
                        ChatParticipant participant2 = (ChatParticipant) item;
                        userId = participant2.user_id;
                        kickedBy = 0;
                        promotedBy = 0;
                        bannedRights = null;
                        banned = false;
                        creator = participant2 instanceof TL_chatParticipantCreator;
                        admin = participant2 instanceof TL_chatParticipantAdmin;
                    }
                    User user = MessagesController.getInstance(ChatUsersActivity.this.currentAccount).getUser(Integer.valueOf(userId));
                    if (user == null) {
                        return;
                    }
                    String role;
                    if (ChatUsersActivity.this.type == 3) {
                        userCell.setData(user, null, ChatUsersActivity.this.formatUserPermissions(bannedRights), position != ChatUsersActivity.this.participantsEndRow + -1);
                        return;
                    } else if (ChatUsersActivity.this.type == 0) {
                        role = null;
                        if (banned && MessagesController.getInstance(ChatUsersActivity.this.currentAccount).getUser(Integer.valueOf(kickedBy)) != null) {
                            role = LocaleController.formatString("UserRemovedBy", R.string.UserRemovedBy, ContactsController.formatName(MessagesController.getInstance(ChatUsersActivity.this.currentAccount).getUser(Integer.valueOf(kickedBy)).first_name, MessagesController.getInstance(ChatUsersActivity.this.currentAccount).getUser(Integer.valueOf(kickedBy)).last_name));
                        }
                        userCell.setData(user, null, role, position != ChatUsersActivity.this.participantsEndRow + -1);
                        return;
                    } else if (ChatUsersActivity.this.type == 1) {
                        role = null;
                        if (creator) {
                            role = LocaleController.getString("ChannelCreator", R.string.ChannelCreator);
                        } else if (admin && MessagesController.getInstance(ChatUsersActivity.this.currentAccount).getUser(Integer.valueOf(promotedBy)) != null) {
                            role = LocaleController.formatString("EditAdminPromotedBy", R.string.EditAdminPromotedBy, ContactsController.formatName(MessagesController.getInstance(ChatUsersActivity.this.currentAccount).getUser(Integer.valueOf(promotedBy)).first_name, MessagesController.getInstance(ChatUsersActivity.this.currentAccount).getUser(Integer.valueOf(promotedBy)).last_name));
                        }
                        if (position != ChatUsersActivity.this.participantsEndRow - 1) {
                            z = true;
                        } else {
                            z = false;
                        }
                        userCell.setData(user, null, role, z);
                        return;
                    } else if (ChatUsersActivity.this.type == 2) {
                        userCell.setData(user, null, null, position != ChatUsersActivity.this.participantsEndRow + -1);
                        return;
                    } else {
                        return;
                    }
                case 1:
                    TextInfoPrivacyCell privacyCell = (TextInfoPrivacyCell) holder.itemView;
                    if (position != ChatUsersActivity.this.participantsInfoRow) {
                        return;
                    }
                    if (ChatUsersActivity.this.type == 0 || ChatUsersActivity.this.type == 3) {
                        if (ChatObject.canBlockUsers(ChatUsersActivity.this.currentChat)) {
                            if (ChatUsersActivity.this.isChannel) {
                                privacyCell.setText(LocaleController.getString("NoBlockedChannel2", R.string.NoBlockedChannel2));
                            } else {
                                privacyCell.setText(LocaleController.getString("NoBlockedGroup2", R.string.NoBlockedGroup2));
                            }
                        } else if (ChatUsersActivity.this.isChannel) {
                            privacyCell.setText(LocaleController.getString("NoBlockedChannel2", R.string.NoBlockedChannel2));
                        } else {
                            privacyCell.setText(LocaleController.getString("NoBlockedGroup2", R.string.NoBlockedGroup2));
                        }
                        privacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider_bottom, "windowBackgroundGrayShadow"));
                        return;
                    } else if (ChatUsersActivity.this.type == 1) {
                        if (ChatUsersActivity.this.addNewRow != -1) {
                            if (ChatUsersActivity.this.isChannel) {
                                privacyCell.setText(LocaleController.getString("ChannelAdminsInfo", R.string.ChannelAdminsInfo));
                            } else {
                                privacyCell.setText(LocaleController.getString("MegaAdminsInfo", R.string.MegaAdminsInfo));
                            }
                            privacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider_bottom, "windowBackgroundGrayShadow"));
                            return;
                        }
                        privacyCell.setText("");
                        privacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider_bottom, "windowBackgroundGrayShadow"));
                        return;
                    } else if (ChatUsersActivity.this.type == 2) {
                        if (ChatUsersActivity.this.isChannel && ChatUsersActivity.this.selectType == 0) {
                            privacyCell.setText(LocaleController.getString("ChannelMembersInfo", R.string.ChannelMembersInfo));
                        } else {
                            privacyCell.setText("");
                        }
                        privacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider_bottom, "windowBackgroundGrayShadow"));
                        return;
                    } else {
                        return;
                    }
                case 2:
                    ManageChatTextCell actionCell = holder.itemView;
                    actionCell.setColors("windowBackgroundWhiteGrayIcon", "windowBackgroundWhiteBlackText");
                    if (position == ChatUsersActivity.this.addNewRow) {
                        if (ChatUsersActivity.this.type == 3) {
                            actionCell.setColors("windowBackgroundWhiteBlueIcon", "windowBackgroundWhiteBlueButton");
                            actionCell.setText(LocaleController.getString("ChannelAddException", R.string.ChannelAddException), null, R.drawable.actions_removed, ChatUsersActivity.this.participantsStartRow != -1);
                            return;
                        } else if (ChatUsersActivity.this.type == 0) {
                            actionCell.setText(LocaleController.getString("ChannelBlockUser", R.string.ChannelBlockUser), null, R.drawable.actions_removed, false);
                            return;
                        } else if (ChatUsersActivity.this.type == 1) {
                            actionCell.setColors("windowBackgroundWhiteBlueIcon", "windowBackgroundWhiteBlueButton");
                            actionCell.setText(LocaleController.getString("ChannelAddAdmin", R.string.ChannelAddAdmin), null, R.drawable.add_admin, true);
                            return;
                        } else if (ChatUsersActivity.this.type == 2) {
                            actionCell.setColors("windowBackgroundWhiteBlueIcon", "windowBackgroundWhiteBlueButton");
                            if (ChatUsersActivity.this.isChannel) {
                                actionCell.setText(LocaleController.getString("AddSubscriber", R.string.AddSubscriber), null, R.drawable.actions_addmember2, true);
                                return;
                            } else {
                                actionCell.setText(LocaleController.getString("AddMember", R.string.AddMember), null, R.drawable.actions_addmember2, true);
                                return;
                            }
                        } else {
                            return;
                        }
                    } else if (position == ChatUsersActivity.this.recentActionsRow) {
                        actionCell.setText(LocaleController.getString("EventLog", R.string.EventLog), null, R.drawable.group_log, false);
                        return;
                    } else if (position == ChatUsersActivity.this.addNew2Row) {
                        actionCell.setText(LocaleController.getString("ChannelInviteViaLink", R.string.ChannelInviteViaLink), null, R.drawable.profile_link, false);
                        return;
                    } else {
                        return;
                    }
                case 3:
                    if (position == ChatUsersActivity.this.addNewSectionRow || (ChatUsersActivity.this.type == 3 && position == ChatUsersActivity.this.participantsDividerRow && ChatUsersActivity.this.addNewRow == -1 && ChatUsersActivity.this.participantsStartRow == -1)) {
                        holder.itemView.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider_bottom, "windowBackgroundGrayShadow"));
                        return;
                    } else {
                        holder.itemView.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider, "windowBackgroundGrayShadow"));
                        return;
                    }
                case 5:
                    HeaderCell headerCell = holder.itemView;
                    if (position == ChatUsersActivity.this.restricted1SectionRow) {
                        if (ChatUsersActivity.this.type == 0) {
                            int count = ChatUsersActivity.this.info != null ? ChatUsersActivity.this.info.kicked_count : ChatUsersActivity.this.participants.size();
                            if (count != 0) {
                                headerCell.setText(LocaleController.formatPluralString("RemovedUser", count));
                                return;
                            } else {
                                headerCell.setText(LocaleController.getString("ChannelBlockedUsers", R.string.ChannelBlockedUsers));
                                return;
                            }
                        }
                        headerCell.setText(LocaleController.getString("ChannelRestrictedUsers", R.string.ChannelRestrictedUsers));
                        return;
                    } else if (position == ChatUsersActivity.this.permissionsSectionRow) {
                        headerCell.setText(LocaleController.getString("ChannelPermissionsHeader", R.string.ChannelPermissionsHeader));
                        return;
                    } else {
                        return;
                    }
                case 6:
                    TextSettingsCell settingsCell = (TextSettingsCell) holder.itemView;
                    string = LocaleController.getString("ChannelBlacklist", R.string.ChannelBlacklist);
                    String str = "%d";
                    Object[] objArr = new Object[1];
                    objArr[0] = Integer.valueOf(ChatUsersActivity.this.info != null ? ChatUsersActivity.this.info.kicked_count : 0);
                    settingsCell.setTextAndValue(string, String.format(str, objArr), false);
                    return;
                case 7:
                    TextCheckCell2 checkCell = holder.itemView;
                    if (position == ChatUsersActivity.this.changeInfoRow) {
                        string = LocaleController.getString("UserRestrictionsChangeInfo", R.string.UserRestrictionsChangeInfo);
                        if (ChatUsersActivity.this.defaultBannedRights.change_info || !TextUtils.isEmpty(ChatUsersActivity.this.currentChat.username)) {
                            z = false;
                        } else {
                            z = true;
                        }
                        checkCell.setTextAndCheck(string, z, false);
                    } else if (position == ChatUsersActivity.this.addUsersRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("UserRestrictionsInviteUsers", R.string.UserRestrictionsInviteUsers), !ChatUsersActivity.this.defaultBannedRights.invite_users, true);
                    } else if (position == ChatUsersActivity.this.pinMessagesRow) {
                        string = LocaleController.getString("UserRestrictionsPinMessages", R.string.UserRestrictionsPinMessages);
                        z = !ChatUsersActivity.this.defaultBannedRights.pin_messages && TextUtils.isEmpty(ChatUsersActivity.this.currentChat.username);
                        checkCell.setTextAndCheck(string, z, true);
                    } else if (position == ChatUsersActivity.this.sendMessagesRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("UserRestrictionsSend", R.string.UserRestrictionsSend), !ChatUsersActivity.this.defaultBannedRights.send_messages, true);
                    } else if (position == ChatUsersActivity.this.sendMediaRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("UserRestrictionsSendMedia", R.string.UserRestrictionsSendMedia), !ChatUsersActivity.this.defaultBannedRights.send_media, true);
                    } else if (position == ChatUsersActivity.this.sendStickersRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("UserRestrictionsSendStickers", R.string.UserRestrictionsSendStickers), !ChatUsersActivity.this.defaultBannedRights.send_stickers, true);
                    } else if (position == ChatUsersActivity.this.embedLinksRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("UserRestrictionsEmbedLinks", R.string.UserRestrictionsEmbedLinks), !ChatUsersActivity.this.defaultBannedRights.embed_links, true);
                    } else if (position == ChatUsersActivity.this.sendPollsRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("UserRestrictionsSendPolls", R.string.UserRestrictionsSendPolls), !ChatUsersActivity.this.defaultBannedRights.send_polls, true);
                    }
                    if (position == ChatUsersActivity.this.sendMediaRow || position == ChatUsersActivity.this.sendStickersRow || position == ChatUsersActivity.this.embedLinksRow || position == ChatUsersActivity.this.sendPollsRow) {
                        z = (ChatUsersActivity.this.defaultBannedRights.send_messages || ChatUsersActivity.this.defaultBannedRights.view_messages) ? false : true;
                        checkCell.setEnabled(z);
                    } else if (position == ChatUsersActivity.this.sendMessagesRow) {
                        checkCell.setEnabled(!ChatUsersActivity.this.defaultBannedRights.view_messages);
                    }
                    if (!ChatObject.canBlockUsers(ChatUsersActivity.this.currentChat)) {
                        checkCell.setIcon(0);
                        return;
                    } else if ((position != ChatUsersActivity.this.addUsersRow || ChatObject.canUserDoAdminAction(ChatUsersActivity.this.currentChat, 3)) && ((position != ChatUsersActivity.this.pinMessagesRow || ChatObject.canUserDoAdminAction(ChatUsersActivity.this.currentChat, 0)) && ((position != ChatUsersActivity.this.changeInfoRow || ChatObject.canUserDoAdminAction(ChatUsersActivity.this.currentChat, 1)) && (TextUtils.isEmpty(ChatUsersActivity.this.currentChat.username) || !(position == ChatUsersActivity.this.pinMessagesRow || position == ChatUsersActivity.this.changeInfoRow))))) {
                        checkCell.setIcon(0);
                        return;
                    } else {
                        checkCell.setIcon(R.drawable.permission_locked);
                        return;
                    }
                default:
                    return;
            }
        }

        public void onViewRecycled(ViewHolder holder) {
            if (holder.itemView instanceof ManageChatUserCell) {
                ((ManageChatUserCell) holder.itemView).recycle();
            }
        }

        public int getItemViewType(int position) {
            if (position == ChatUsersActivity.this.addNewRow || position == ChatUsersActivity.this.addNew2Row || position == ChatUsersActivity.this.recentActionsRow) {
                return 2;
            }
            if (position >= ChatUsersActivity.this.participantsStartRow && position < ChatUsersActivity.this.participantsEndRow) {
                return 0;
            }
            if (position == ChatUsersActivity.this.addNewSectionRow || position == ChatUsersActivity.this.participantsDividerRow || position == ChatUsersActivity.this.participantsDivider2Row) {
                return 3;
            }
            if (position == ChatUsersActivity.this.restricted1SectionRow || position == ChatUsersActivity.this.permissionsSectionRow) {
                return 5;
            }
            if (position == ChatUsersActivity.this.participantsInfoRow) {
                return 1;
            }
            if (position == ChatUsersActivity.this.blockedEmptyRow) {
                return 4;
            }
            if (position == ChatUsersActivity.this.removedUsersRow) {
                return 6;
            }
            if (position == ChatUsersActivity.this.changeInfoRow || position == ChatUsersActivity.this.addUsersRow || position == ChatUsersActivity.this.pinMessagesRow || position == ChatUsersActivity.this.sendMessagesRow || position == ChatUsersActivity.this.sendMediaRow || position == ChatUsersActivity.this.sendStickersRow || position == ChatUsersActivity.this.embedLinksRow || position == ChatUsersActivity.this.sendPollsRow) {
                return 7;
            }
            return 0;
        }

        public TLObject getItem(int position) {
            if (ChatUsersActivity.this.participantsStartRow == -1 || position < ChatUsersActivity.this.participantsStartRow || position >= ChatUsersActivity.this.participantsEndRow) {
                return null;
            }
            return (TLObject) ChatUsersActivity.this.participants.get(position - ChatUsersActivity.this.participantsStartRow);
        }
    }

    private class SearchAdapter extends SelectionAdapter {
        private int contactsStartRow;
        private int globalStartRow;
        private int groupStartRow;
        private Context mContext;
        private SearchAdapterHelper searchAdapterHelper;
        private ArrayList<TLObject> searchResult = new ArrayList();
        private ArrayList<CharSequence> searchResultNames = new ArrayList();
        private Timer searchTimer;
        private int totalCount;

        public SearchAdapter(Context context) {
            this.mContext = context;
            this.searchAdapterHelper = new SearchAdapterHelper(true);
            this.searchAdapterHelper.setDelegate(new SearchAdapterHelperDelegate(ChatUsersActivity.this) {
                public SparseArray getExcludeUsers() {
                    return SearchAdapterHelper$SearchAdapterHelperDelegate$$CC.getExcludeUsers(this);
                }

                public void onDataSetChanged() {
                    SearchAdapter.this.notifyDataSetChanged();
                }

                public void onSetHashtags(ArrayList<HashtagObject> arrayList, HashMap<String, HashtagObject> hashMap) {
                }
            });
        }

        public void searchDialogs(final String query) {
            try {
                if (this.searchTimer != null) {
                    this.searchTimer.cancel();
                }
            } catch (Throwable e) {
                FileLog.e(e);
            }
            if (TextUtils.isEmpty(query)) {
                boolean z;
                this.searchResult.clear();
                this.searchResultNames.clear();
                this.searchAdapterHelper.mergeResults(null);
                SearchAdapterHelper searchAdapterHelper = this.searchAdapterHelper;
                if (ChatUsersActivity.this.type != 0) {
                    z = true;
                } else {
                    z = false;
                }
                searchAdapterHelper.queryServerSearch(null, z, false, true, false, ChatObject.isChannel(ChatUsersActivity.this.currentChat) ? ChatUsersActivity.this.chatId : 0, ChatUsersActivity.this.type);
                notifyDataSetChanged();
                return;
            }
            this.searchTimer = new Timer();
            this.searchTimer.schedule(new TimerTask() {
                public void run() {
                    try {
                        SearchAdapter.this.searchTimer.cancel();
                        SearchAdapter.this.searchTimer = null;
                    } catch (Throwable e) {
                        FileLog.e(e);
                    }
                    SearchAdapter.this.processSearch(query);
                }
            }, 200, 300);
        }

        private void processSearch(String query) {
            AndroidUtilities.runOnUIThread(new ChatUsersActivity$SearchAdapter$$Lambda$0(this, query));
        }

        final /* synthetic */ void lambda$processSearch$1$ChatUsersActivity$SearchAdapter(String query) {
            ArrayList<ChatParticipant> participantsCopy;
            ArrayList<TL_contact> contactsCopy;
            boolean z;
            if (ChatObject.isChannel(ChatUsersActivity.this.currentChat) || ChatUsersActivity.this.info == null) {
                participantsCopy = null;
            } else {
                participantsCopy = new ArrayList(ChatUsersActivity.this.info.participants.participants);
            }
            if (ChatUsersActivity.this.selectType == 1) {
                contactsCopy = new ArrayList(ContactsController.getInstance(ChatUsersActivity.this.currentAccount).contacts);
            } else {
                contactsCopy = null;
            }
            SearchAdapterHelper searchAdapterHelper = this.searchAdapterHelper;
            if (ChatUsersActivity.this.selectType != 0) {
                z = true;
            } else {
                z = false;
            }
            searchAdapterHelper.queryServerSearch(query, z, false, true, false, ChatObject.isChannel(ChatUsersActivity.this.currentChat) ? ChatUsersActivity.this.chatId : 0, ChatUsersActivity.this.type);
            if (participantsCopy != null || contactsCopy != null) {
                Utilities.searchQueue.postRunnable(new ChatUsersActivity$SearchAdapter$$Lambda$3(this, query, participantsCopy, contactsCopy));
            }
        }

        final /* synthetic */ void lambda$null$0$ChatUsersActivity$SearchAdapter(String query, ArrayList participantsCopy, ArrayList contactsCopy) {
            String search1 = query.trim().toLowerCase();
            if (search1.length() == 0) {
                updateSearchResults(new ArrayList(), new ArrayList(), new ArrayList());
                return;
            }
            int a;
            User user;
            String name;
            String tName;
            int found;
            int length;
            int i;
            String q;
            String search2 = LocaleController.getInstance().getTranslitString(search1);
            if (search1.equals(search2) || search2.length() == 0) {
                search2 = null;
            }
            String[] search = new String[((search2 != null ? 1 : 0) + 1)];
            search[0] = search1;
            if (search2 != null) {
                search[1] = search2;
            }
            ArrayList<TLObject> resultArray = new ArrayList();
            ArrayList<CharSequence> resultArrayNames = new ArrayList();
            ArrayList<TLObject> resultArray2 = new ArrayList();
            if (participantsCopy != null) {
                for (a = 0; a < participantsCopy.size(); a++) {
                    ChatParticipant participant = (ChatParticipant) participantsCopy.get(a);
                    user = MessagesController.getInstance(ChatUsersActivity.this.currentAccount).getUser(Integer.valueOf(participant.user_id));
                    if (user.id != UserConfig.getInstance(ChatUsersActivity.this.currentAccount).getClientUserId()) {
                        name = ContactsController.formatName(user.first_name, user.last_name).toLowerCase();
                        tName = LocaleController.getInstance().getTranslitString(name);
                        if (name.equals(tName)) {
                            tName = null;
                        }
                        found = 0;
                        length = search.length;
                        i = 0;
                        while (i < length) {
                            q = search[i];
                            if (name.startsWith(q) || name.contains(" " + q) || (tName != null && (tName.startsWith(q) || tName.contains(" " + q)))) {
                                found = 1;
                            } else if (user.username != null && user.username.startsWith(q)) {
                                found = 2;
                            }
                            if (found != 0) {
                                if (found == 1) {
                                    resultArrayNames.add(AndroidUtilities.generateSearchName(user.first_name, user.last_name, q));
                                } else {
                                    resultArrayNames.add(AndroidUtilities.generateSearchName("@" + user.username, null, "@" + q));
                                }
                                resultArray2.add(participant);
                            } else {
                                i++;
                            }
                        }
                    }
                }
            }
            if (contactsCopy != null) {
                for (a = 0; a < contactsCopy.size(); a++) {
                    user = MessagesController.getInstance(ChatUsersActivity.this.currentAccount).getUser(Integer.valueOf(((TL_contact) contactsCopy.get(a)).user_id));
                    if (user.id != UserConfig.getInstance(ChatUsersActivity.this.currentAccount).getClientUserId()) {
                        name = ContactsController.formatName(user.first_name, user.last_name).toLowerCase();
                        tName = LocaleController.getInstance().getTranslitString(name);
                        if (name.equals(tName)) {
                            tName = null;
                        }
                        found = 0;
                        length = search.length;
                        i = 0;
                        while (i < length) {
                            q = search[i];
                            if (name.startsWith(q) || name.contains(" " + q) || (tName != null && (tName.startsWith(q) || tName.contains(" " + q)))) {
                                found = 1;
                            } else if (user.username != null && user.username.startsWith(q)) {
                                found = 2;
                            }
                            if (found != 0) {
                                if (found == 1) {
                                    resultArrayNames.add(AndroidUtilities.generateSearchName(user.first_name, user.last_name, q));
                                } else {
                                    resultArrayNames.add(AndroidUtilities.generateSearchName("@" + user.username, null, "@" + q));
                                }
                                resultArray.add(user);
                            } else {
                                i++;
                            }
                        }
                    }
                }
            }
            updateSearchResults(resultArray, resultArrayNames, resultArray2);
        }

        private void updateSearchResults(ArrayList<TLObject> users, ArrayList<CharSequence> names, ArrayList<TLObject> participants) {
            AndroidUtilities.runOnUIThread(new ChatUsersActivity$SearchAdapter$$Lambda$1(this, users, names, participants));
        }

        final /* synthetic */ void lambda$updateSearchResults$2$ChatUsersActivity$SearchAdapter(ArrayList users, ArrayList names, ArrayList participants) {
            this.searchResult = users;
            this.searchResultNames = names;
            this.searchAdapterHelper.mergeResults(this.searchResult);
            if (!ChatObject.isChannel(ChatUsersActivity.this.currentChat)) {
                ArrayList<TLObject> search = this.searchAdapterHelper.getGroupSearch();
                search.clear();
                search.addAll(participants);
            }
            notifyDataSetChanged();
        }

        public boolean isEnabled(ViewHolder holder) {
            return holder.getItemViewType() != 1;
        }

        public int getItemCount() {
            int contactsCount = this.searchResult.size();
            int globalCount = this.searchAdapterHelper.getGlobalSearch().size();
            int groupsCount = this.searchAdapterHelper.getGroupSearch().size();
            int count = 0;
            if (contactsCount != 0) {
                count = 0 + (contactsCount + 1);
            }
            if (globalCount != 0) {
                count += globalCount + 1;
            }
            if (groupsCount != 0) {
                return count + (groupsCount + 1);
            }
            return count;
        }

        public void notifyDataSetChanged() {
            this.totalCount = 0;
            int count = this.searchAdapterHelper.getGroupSearch().size();
            if (count != 0) {
                this.groupStartRow = 0;
                this.totalCount += count + 1;
            } else {
                this.groupStartRow = -1;
            }
            count = this.searchResult.size();
            if (count != 0) {
                this.contactsStartRow = this.totalCount;
                this.totalCount += count + 1;
            } else {
                this.contactsStartRow = -1;
            }
            count = this.searchAdapterHelper.getGlobalSearch().size();
            if (count != 0) {
                this.globalStartRow = this.totalCount;
                this.totalCount += count + 1;
            } else {
                this.globalStartRow = -1;
            }
            super.notifyDataSetChanged();
        }

        public TLObject getItem(int i) {
            int count = this.searchAdapterHelper.getGroupSearch().size();
            if (count != 0) {
                if (count + 1 <= i) {
                    i -= count + 1;
                } else if (i == 0) {
                    return null;
                } else {
                    return (TLObject) this.searchAdapterHelper.getGroupSearch().get(i - 1);
                }
            }
            count = this.searchResult.size();
            if (count != 0) {
                if (count + 1 <= i) {
                    i -= count + 1;
                } else if (i != 0) {
                    return (TLObject) this.searchResult.get(i - 1);
                } else {
                    return null;
                }
            }
            count = this.searchAdapterHelper.getGlobalSearch().size();
            if (count == 0 || count + 1 <= i || i == 0) {
                return null;
            }
            return (TLObject) this.searchAdapterHelper.getGlobalSearch().get(i - 1);
        }

        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    view = new ManageChatUserCell(this.mContext, 2, 2, ChatUsersActivity.this.selectType == 0);
                    view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    ((ManageChatUserCell) view).setDelegate(new ChatUsersActivity$SearchAdapter$$Lambda$2(this));
                    break;
                default:
                    view = new GraySectionCell(this.mContext);
                    break;
            }
            return new Holder(view);
        }

        final /* synthetic */ boolean lambda$onCreateViewHolder$3$ChatUsersActivity$SearchAdapter(ManageChatUserCell cell, boolean click) {
            if (!(getItem(((Integer) cell.getTag()).intValue()) instanceof ChannelParticipant)) {
                return false;
            }
            boolean z;
            ChannelParticipant participant = (ChannelParticipant) getItem(((Integer) cell.getTag()).intValue());
            ChatUsersActivity chatUsersActivity = ChatUsersActivity.this;
            if (click) {
                z = false;
            } else {
                z = true;
            }
            return chatUsersActivity.createMenuForParticipant(participant, z);
        }

        public void onBindViewHolder(ViewHolder holder, int position) {
            switch (holder.getItemViewType()) {
                case 0:
                    User user;
                    TLObject object = getItem(position);
                    if (object instanceof User) {
                        user = (User) object;
                    } else if (object instanceof ChannelParticipant) {
                        user = MessagesController.getInstance(ChatUsersActivity.this.currentAccount).getUser(Integer.valueOf(((ChannelParticipant) object).user_id));
                    } else if (object instanceof ChatParticipant) {
                        user = MessagesController.getInstance(ChatUsersActivity.this.currentAccount).getUser(Integer.valueOf(((ChatParticipant) object).user_id));
                    } else {
                        return;
                    }
                    String un = user.username;
                    CharSequence username = null;
                    CharSequence name = null;
                    int count = this.searchAdapterHelper.getGroupSearch().size();
                    boolean ok = false;
                    String nameSearch = null;
                    if (count != 0) {
                        if (count + 1 > position) {
                            nameSearch = this.searchAdapterHelper.getLastFoundChannel();
                            ok = true;
                        } else {
                            position -= count + 1;
                        }
                    }
                    if (!ok) {
                        count = this.searchResult.size();
                        if (count != 0) {
                            if (count + 1 > position) {
                                ok = true;
                                name = (CharSequence) this.searchResultNames.get(position - 1);
                                if (!(name == null || TextUtils.isEmpty(un) || !name.toString().startsWith("@" + un))) {
                                    username = name;
                                    name = null;
                                }
                            } else {
                                position -= count + 1;
                            }
                        }
                    }
                    if (!(ok || un == null)) {
                        count = this.searchAdapterHelper.getGlobalSearch().size();
                        if (count != 0 && count + 1 > position) {
                            String foundUserName = this.searchAdapterHelper.getLastFoundUsername();
                            if (foundUserName.startsWith("@")) {
                                foundUserName = foundUserName.substring(1);
                            }
                            try {
                                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
                                spannableStringBuilder.append("@");
                                spannableStringBuilder.append(un);
                                int index = un.toLowerCase().indexOf(foundUserName);
                                if (index != -1) {
                                    int len = foundUserName.length();
                                    if (index == 0) {
                                        len++;
                                    } else {
                                        index++;
                                    }
                                    spannableStringBuilder.setSpan(new ForegroundColorSpan(Theme.getColor("windowBackgroundWhiteBlueText4")), index, index + len, 33);
                                }
                                username = spannableStringBuilder;
                            } catch (Throwable e) {
                                Object username2 = un;
                                FileLog.e(e);
                            }
                        }
                    }
                    if (nameSearch != null) {
                        String u = UserObject.getUserName(user);
                        name = new SpannableStringBuilder(u);
                        int idx = u.toLowerCase().indexOf(nameSearch);
                        if (idx != -1) {
                            ((SpannableStringBuilder) name).setSpan(new ForegroundColorSpan(Theme.getColor("windowBackgroundWhiteBlueText4")), idx, nameSearch.length() + idx, 33);
                        }
                    }
                    ManageChatUserCell userCell = (ManageChatUserCell) holder.itemView;
                    userCell.setTag(Integer.valueOf(position));
                    userCell.setData(user, name, username, false);
                    return;
                case 1:
                    GraySectionCell sectionCell = holder.itemView;
                    if (position != this.groupStartRow) {
                        if (position == this.globalStartRow) {
                            sectionCell.setText(LocaleController.getString("GlobalSearch", R.string.GlobalSearch));
                            return;
                        }
                        if (position == this.contactsStartRow) {
                            sectionCell.setText(LocaleController.getString("Contacts", R.string.Contacts));
                            return;
                        }
                        return;
                    } else if (ChatUsersActivity.this.type == 0) {
                        sectionCell.setText(LocaleController.getString("ChannelBlockedUsers", R.string.ChannelBlockedUsers));
                        return;
                    } else if (ChatUsersActivity.this.type == 3) {
                        sectionCell.setText(LocaleController.getString("ChannelRestrictedUsers", R.string.ChannelRestrictedUsers));
                        return;
                    } else if (ChatUsersActivity.this.isChannel) {
                        sectionCell.setText(LocaleController.getString("ChannelSubscribers", R.string.ChannelSubscribers));
                        return;
                    } else {
                        sectionCell.setText(LocaleController.getString("ChannelMembers", R.string.ChannelMembers));
                        return;
                    }
                default:
                    return;
            }
        }

        public void onViewRecycled(ViewHolder holder) {
            if (holder.itemView instanceof ManageChatUserCell) {
                ((ManageChatUserCell) holder.itemView).recycle();
            }
        }

        public int getItemViewType(int i) {
            if (i == this.globalStartRow || i == this.groupStartRow || i == this.contactsStartRow) {
                return 1;
            }
            return 0;
        }
    }

    public ChatUsersActivity(Bundle args) {
        super(args);
        if (!(this.currentChat == null || this.currentChat.default_banned_rights == null)) {
            this.defaultBannedRights.view_messages = this.currentChat.default_banned_rights.view_messages;
            this.defaultBannedRights.send_stickers = this.currentChat.default_banned_rights.send_stickers;
            this.defaultBannedRights.send_media = this.currentChat.default_banned_rights.send_media;
            this.defaultBannedRights.embed_links = this.currentChat.default_banned_rights.embed_links;
            this.defaultBannedRights.send_messages = this.currentChat.default_banned_rights.send_messages;
            this.defaultBannedRights.send_games = this.currentChat.default_banned_rights.send_games;
            this.defaultBannedRights.send_inline = this.currentChat.default_banned_rights.send_inline;
            this.defaultBannedRights.send_gifs = this.currentChat.default_banned_rights.send_gifs;
            this.defaultBannedRights.pin_messages = this.currentChat.default_banned_rights.pin_messages;
            this.defaultBannedRights.send_polls = this.currentChat.default_banned_rights.send_polls;
            this.defaultBannedRights.invite_users = this.currentChat.default_banned_rights.invite_users;
            this.defaultBannedRights.change_info = this.currentChat.default_banned_rights.change_info;
        }
        this.initialBannedRights = ChatObject.getBannedRightsString(this.defaultBannedRights);
        boolean z = ChatObject.isChannel(this.currentChat) && !this.currentChat.megagroup;
        this.isChannel = z;
    }

    private void updateRows() {
        int i = 0;
        this.currentChat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.chatId));
        if (this.currentChat != null) {
            this.recentActionsRow = -1;
            this.addNewRow = -1;
            this.addNew2Row = -1;
            this.addNewSectionRow = -1;
            this.restricted1SectionRow = -1;
            this.participantsStartRow = -1;
            this.participantsDividerRow = -1;
            this.participantsDivider2Row = -1;
            this.participantsEndRow = -1;
            this.participantsInfoRow = -1;
            this.blockedEmptyRow = -1;
            this.permissionsSectionRow = -1;
            this.sendMessagesRow = -1;
            this.sendMediaRow = -1;
            this.sendStickersRow = -1;
            this.sendPollsRow = -1;
            this.embedLinksRow = -1;
            this.addUsersRow = -1;
            this.pinMessagesRow = -1;
            this.changeInfoRow = -1;
            this.removedUsersRow = -1;
            this.rowCount = 0;
            int i2;
            if (this.type == 3) {
                i2 = this.rowCount;
                this.rowCount = i2 + 1;
                this.permissionsSectionRow = i2;
                i2 = this.rowCount;
                this.rowCount = i2 + 1;
                this.sendMessagesRow = i2;
                i2 = this.rowCount;
                this.rowCount = i2 + 1;
                this.sendMediaRow = i2;
                i2 = this.rowCount;
                this.rowCount = i2 + 1;
                this.sendStickersRow = i2;
                i2 = this.rowCount;
                this.rowCount = i2 + 1;
                this.sendPollsRow = i2;
                i2 = this.rowCount;
                this.rowCount = i2 + 1;
                this.embedLinksRow = i2;
                i2 = this.rowCount;
                this.rowCount = i2 + 1;
                this.addUsersRow = i2;
                i2 = this.rowCount;
                this.rowCount = i2 + 1;
                this.pinMessagesRow = i2;
                i2 = this.rowCount;
                this.rowCount = i2 + 1;
                this.changeInfoRow = i2;
                if (ChatObject.isChannel(this.currentChat)) {
                    i2 = this.rowCount;
                    this.rowCount = i2 + 1;
                    this.participantsDivider2Row = i2;
                    i2 = this.rowCount;
                    this.rowCount = i2 + 1;
                    this.removedUsersRow = i2;
                }
                i2 = this.rowCount;
                this.rowCount = i2 + 1;
                this.participantsDividerRow = i2;
                if (ChatObject.canBlockUsers(this.currentChat)) {
                    i2 = this.rowCount;
                    this.rowCount = i2 + 1;
                    this.addNewRow = i2;
                }
                if (!this.participants.isEmpty()) {
                    this.participantsStartRow = this.rowCount;
                    this.rowCount += this.participants.size();
                    this.participantsEndRow = this.rowCount;
                }
                if (!(this.addNewRow == -1 && this.participantsStartRow == -1)) {
                    i2 = this.rowCount;
                    this.rowCount = i2 + 1;
                    this.addNewSectionRow = i2;
                }
            } else if (this.type == 0) {
                if (ChatObject.canBlockUsers(this.currentChat)) {
                    i2 = this.rowCount;
                    this.rowCount = i2 + 1;
                    this.addNewRow = i2;
                    if (!this.participants.isEmpty()) {
                        i2 = this.rowCount;
                        this.rowCount = i2 + 1;
                        this.participantsInfoRow = i2;
                    }
                }
                if (!this.participants.isEmpty()) {
                    i2 = this.rowCount;
                    this.rowCount = i2 + 1;
                    this.restricted1SectionRow = i2;
                    this.participantsStartRow = this.rowCount;
                    this.rowCount += this.participants.size();
                    this.participantsEndRow = this.rowCount;
                }
                if (this.participantsStartRow == -1) {
                    if (this.searchItem != null) {
                        this.searchItem.setVisibility(4);
                    }
                    i2 = this.rowCount;
                    this.rowCount = i2 + 1;
                    this.blockedEmptyRow = i2;
                } else if (this.participantsInfoRow == -1) {
                    i2 = this.rowCount;
                    this.rowCount = i2 + 1;
                    this.participantsInfoRow = i2;
                } else {
                    i2 = this.rowCount;
                    this.rowCount = i2 + 1;
                    this.addNewSectionRow = i2;
                }
            } else if (this.type == 1) {
                if (ChatObject.isChannel(this.currentChat) && this.currentChat.megagroup && (this.info == null || this.info.participants_count <= 200)) {
                    i2 = this.rowCount;
                    this.rowCount = i2 + 1;
                    this.recentActionsRow = i2;
                    i2 = this.rowCount;
                    this.rowCount = i2 + 1;
                    this.addNewSectionRow = i2;
                }
                if (ChatObject.canAddAdmins(this.currentChat)) {
                    i2 = this.rowCount;
                    this.rowCount = i2 + 1;
                    this.addNewRow = i2;
                }
                if (!this.participants.isEmpty()) {
                    this.participantsStartRow = this.rowCount;
                    this.rowCount += this.participants.size();
                    this.participantsEndRow = this.rowCount;
                }
                i2 = this.rowCount;
                this.rowCount = i2 + 1;
                this.participantsInfoRow = i2;
            } else if (this.type == 2) {
                if (this.selectType == 0 && ChatObject.canAddUsers(this.currentChat)) {
                    if (!ChatObject.isChannel(this.currentChat) || this.currentChat.megagroup || TextUtils.isEmpty(this.currentChat.username)) {
                        i2 = this.rowCount;
                        this.rowCount = i2 + 1;
                        this.addNew2Row = i2;
                        i2 = this.rowCount;
                        this.rowCount = i2 + 1;
                        this.addNewSectionRow = i2;
                    }
                    i2 = this.rowCount;
                    this.rowCount = i2 + 1;
                    this.addNewRow = i2;
                }
                if (!this.participants.isEmpty()) {
                    this.participantsStartRow = this.rowCount;
                    this.rowCount += this.participants.size();
                    this.participantsEndRow = this.rowCount;
                }
                if (this.rowCount != 0) {
                    i2 = this.rowCount;
                    this.rowCount = i2 + 1;
                    this.participantsInfoRow = i2;
                }
            }
            if (this.searchItem != null && !this.actionBar.isSearchFieldVisible()) {
                ActionBarMenuItem actionBarMenuItem = this.searchItem;
                if (this.selectType == 0 && this.participants.isEmpty()) {
                    i = 8;
                }
                actionBarMenuItem.setVisibility(i);
            }
        }
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.chatInfoDidLoad);
        loadChatParticipants(0, 200);
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.chatInfoDidLoad);
    }

    public View createView(Context context) {
        int i;
        this.searching = false;
        this.searchWas = false;
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        if (this.type == 3) {
            this.actionBar.setTitle(LocaleController.getString("ChannelPermissions", R.string.ChannelPermissions));
        } else if (this.type == 0) {
            this.actionBar.setTitle(LocaleController.getString("ChannelBlacklist", R.string.ChannelBlacklist));
        } else if (this.type == 1) {
            this.actionBar.setTitle(LocaleController.getString("ChannelAdministrators", R.string.ChannelAdministrators));
        } else if (this.type == 2) {
            if (this.selectType == 0) {
                if (this.isChannel) {
                    this.actionBar.setTitle(LocaleController.getString("ChannelSubscribers", R.string.ChannelSubscribers));
                } else {
                    this.actionBar.setTitle(LocaleController.getString("ChannelMembers", R.string.ChannelMembers));
                }
            } else if (this.selectType == 1) {
                this.actionBar.setTitle(LocaleController.getString("ChannelAddAdmin", R.string.ChannelAddAdmin));
            } else if (this.selectType == 2) {
                this.actionBar.setTitle(LocaleController.getString("ChannelBlockUser", R.string.ChannelBlockUser));
            } else if (this.selectType == 3) {
                this.actionBar.setTitle(LocaleController.getString("ChannelAddException", R.string.ChannelAddException));
            }
        }
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    if (ChatUsersActivity.this.checkDiscard()) {
                        ChatUsersActivity.this.lambda$checkDiscard$18$ChatUsersActivity();
                    }
                } else if (id == 1) {
                    ChatUsersActivity.this.processDone();
                }
            }
        });
        if (this.selectType != 0 || this.type == 2 || this.type == 0 || this.type == 3) {
            this.searchListViewAdapter = new SearchAdapter(context);
            ActionBarMenu menu = this.actionBar.createMenu();
            this.searchItem = menu.addItem(0, (int) R.drawable.ic_ab_search).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItemSearchListener() {
                public void onSearchExpand() {
                    ChatUsersActivity.this.searching = true;
                    ChatUsersActivity.this.emptyView.setShowAtCenter(true);
                    if (ChatUsersActivity.this.doneItem != null) {
                        ChatUsersActivity.this.doneItem.setVisibility(8);
                    }
                }

                public void onSearchCollapse() {
                    ChatUsersActivity.this.searchListViewAdapter.searchDialogs(null);
                    ChatUsersActivity.this.searching = false;
                    ChatUsersActivity.this.searchWas = false;
                    ChatUsersActivity.this.listView.setAdapter(ChatUsersActivity.this.listViewAdapter);
                    ChatUsersActivity.this.listViewAdapter.notifyDataSetChanged();
                    ChatUsersActivity.this.listView.setFastScrollVisible(true);
                    ChatUsersActivity.this.listView.setVerticalScrollBarEnabled(false);
                    ChatUsersActivity.this.emptyView.setShowAtCenter(false);
                    if (ChatUsersActivity.this.doneItem != null) {
                        ChatUsersActivity.this.doneItem.setVisibility(0);
                    }
                }

                public void onTextChanged(EditText editText) {
                    if (ChatUsersActivity.this.searchListViewAdapter != null) {
                        String text = editText.getText().toString();
                        if (text.length() != 0) {
                            ChatUsersActivity.this.searchWas = true;
                            if (!(ChatUsersActivity.this.listView == null || ChatUsersActivity.this.listView.getAdapter() == ChatUsersActivity.this.searchListViewAdapter)) {
                                ChatUsersActivity.this.listView.setAdapter(ChatUsersActivity.this.searchListViewAdapter);
                                ChatUsersActivity.this.searchListViewAdapter.notifyDataSetChanged();
                                ChatUsersActivity.this.listView.setFastScrollVisible(false);
                                ChatUsersActivity.this.listView.setVerticalScrollBarEnabled(true);
                            }
                        }
                        ChatUsersActivity.this.searchListViewAdapter.searchDialogs(text);
                    }
                }
            });
            if (this.type == 3) {
                this.searchItem.setSearchFieldHint(LocaleController.getString("ChannelSearchException", R.string.ChannelSearchException));
            } else {
                this.searchItem.setSearchFieldHint(LocaleController.getString("Search", R.string.Search));
            }
            ActionBarMenuItem actionBarMenuItem = this.searchItem;
            if (this.selectType == 0 && this.participants.isEmpty()) {
                i = 8;
            } else {
                i = 0;
            }
            actionBarMenuItem.setVisibility(i);
            if (this.type == 3) {
                this.doneItem = menu.addItemWithWidth(1, R.drawable.ic_done, AndroidUtilities.dp(56.0f));
            }
        }
        this.fragmentView = new FrameLayout(context);
        this.fragmentView.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
        FrameLayout frameLayout = this.fragmentView;
        this.emptyView = new EmptyTextProgressView(context);
        if (this.type == 0 || this.type == 2 || this.type == 3) {
            this.emptyView.setText(LocaleController.getString("NoResult", R.string.NoResult));
        }
        frameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView = new RecyclerListView(context);
        this.listView.setEmptyView(this.emptyView);
        this.listView.setLayoutManager(new LinearLayoutManager(context, 1, false));
        RecyclerListView recyclerListView = this.listView;
        Adapter listAdapter = new ListAdapter(context);
        this.listViewAdapter = listAdapter;
        recyclerListView.setAdapter(listAdapter);
        RecyclerListView recyclerListView2 = this.listView;
        if (LocaleController.isRTL) {
            i = 1;
        } else {
            i = 2;
        }
        recyclerListView2.setVerticalScrollbarPosition(i);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setOnItemClickListener(new ChatUsersActivity$$Lambda$0(this));
        this.listView.setOnItemLongClickListener(new ChatUsersActivity$$Lambda$1(this));
        if (this.searchItem != null) {
            this.listView.setOnScrollListener(new OnScrollListener() {
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    if (newState == 1 && ChatUsersActivity.this.searching && ChatUsersActivity.this.searchWas) {
                        AndroidUtilities.hideKeyboard(ChatUsersActivity.this.getParentActivity().getCurrentFocus());
                    }
                }

                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                }
            });
        }
        if (this.loadingUsers) {
            this.emptyView.showProgress();
        } else {
            this.emptyView.showTextView();
        }
        updateRows();
        return this.fragmentView;
    }

    final /* synthetic */ void lambda$createView$5$ChatUsersActivity(View view, int position) {
        Bundle args;
        TLObject participant;
        User user;
        boolean listAdapter = this.listView.getAdapter() == this.listViewAdapter;
        if (listAdapter) {
            ChatUsersActivity fragment;
            if (position == this.addNewRow) {
                Bundle bundle;
                if (this.type == 0 || this.type == 3) {
                    bundle = new Bundle();
                    bundle.putInt("chat_id", this.chatId);
                    bundle.putInt("type", 2);
                    bundle.putInt("selectType", this.type == 0 ? 2 : 3);
                    fragment = new ChatUsersActivity(bundle);
                    fragment.setInfo(this.info);
                    presentFragment(fragment);
                    return;
                } else if (this.type == 1) {
                    bundle = new Bundle();
                    bundle.putInt("chat_id", this.chatId);
                    bundle.putInt("type", 2);
                    bundle.putInt("selectType", 1);
                    fragment = new ChatUsersActivity(bundle);
                    fragment.setDelegate(new ChatUsersActivity$$Lambda$19(this));
                    fragment.setInfo(this.info);
                    presentFragment(fragment);
                    return;
                } else if (this.type == 2) {
                    args = new Bundle();
                    args.putBoolean("onlyUsers", true);
                    args.putBoolean("destroyAfterSelect", true);
                    args.putBoolean("returnAsResult", true);
                    args.putBoolean("needForwardCount", false);
                    if (this.isChannel) {
                        args.putString("selectAlertString", LocaleController.getString("ChannelAddTo", R.string.ChannelAddTo));
                        args.putInt("channelId", this.currentChat.id);
                    } else {
                        if (!ChatObject.isChannel(this.currentChat)) {
                            args.putInt("chat_id", this.currentChat.id);
                        }
                        args.putString("selectAlertString", LocaleController.getString("AddToTheGroup", R.string.AddToTheGroup));
                    }
                    ContactsActivity fragment2 = new ContactsActivity(args);
                    fragment2.setDelegate(new ChatUsersActivity$$Lambda$20(this));
                    presentFragment(fragment2);
                    return;
                } else {
                    return;
                }
            } else if (position == this.recentActionsRow) {
                presentFragment(new ChannelAdminLogActivity(this.currentChat));
                return;
            } else if (position == this.removedUsersRow) {
                args = new Bundle();
                args.putInt("chat_id", this.chatId);
                args.putInt("type", 0);
                fragment = new ChatUsersActivity(args);
                fragment.setInfo(this.info);
                presentFragment(fragment);
                return;
            } else if (position == this.addNew2Row) {
                presentFragment(new GroupInviteActivity(this.chatId));
                return;
            } else if (position > this.permissionsSectionRow && position <= this.changeInfoRow) {
                TextCheckCell2 checkCell = (TextCheckCell2) view;
                if (!checkCell.isEnabled()) {
                    return;
                }
                if (!checkCell.hasIcon()) {
                    checkCell.setChecked(!checkCell.isChecked());
                    if (position == this.changeInfoRow) {
                        this.defaultBannedRights.change_info = !this.defaultBannedRights.change_info;
                        return;
                    } else if (position == this.addUsersRow) {
                        this.defaultBannedRights.invite_users = !this.defaultBannedRights.invite_users;
                        return;
                    } else if (position == this.pinMessagesRow) {
                        this.defaultBannedRights.pin_messages = !this.defaultBannedRights.pin_messages;
                        return;
                    } else {
                        TL_chatBannedRights tL_chatBannedRights;
                        TL_chatBannedRights tL_chatBannedRights2;
                        boolean disabled = !checkCell.isChecked();
                        if (position == this.sendMessagesRow) {
                            this.defaultBannedRights.send_messages = !this.defaultBannedRights.send_messages;
                        } else if (position == this.sendMediaRow) {
                            this.defaultBannedRights.send_media = !this.defaultBannedRights.send_media;
                        } else if (position == this.sendStickersRow) {
                            tL_chatBannedRights = this.defaultBannedRights;
                            tL_chatBannedRights2 = this.defaultBannedRights;
                            TL_chatBannedRights tL_chatBannedRights3 = this.defaultBannedRights;
                            TL_chatBannedRights tL_chatBannedRights4 = this.defaultBannedRights;
                            boolean z = !this.defaultBannedRights.send_stickers;
                            tL_chatBannedRights4.send_inline = z;
                            tL_chatBannedRights3.send_gifs = z;
                            tL_chatBannedRights2.send_games = z;
                            tL_chatBannedRights.send_stickers = z;
                        } else if (position == this.embedLinksRow) {
                            this.defaultBannedRights.embed_links = !this.defaultBannedRights.embed_links;
                        } else if (position == this.sendPollsRow) {
                            this.defaultBannedRights.send_polls = !this.defaultBannedRights.send_polls;
                        }
                        ViewHolder holder;
                        if (disabled) {
                            if (this.defaultBannedRights.view_messages && !this.defaultBannedRights.send_messages) {
                                this.defaultBannedRights.send_messages = true;
                                holder = this.listView.findViewHolderForAdapterPosition(this.sendMessagesRow);
                                if (holder != null) {
                                    ((TextCheckCell2) holder.itemView).setChecked(false);
                                }
                            }
                            if ((this.defaultBannedRights.view_messages || this.defaultBannedRights.send_messages) && !this.defaultBannedRights.send_media) {
                                this.defaultBannedRights.send_media = true;
                                holder = this.listView.findViewHolderForAdapterPosition(this.sendMediaRow);
                                if (holder != null) {
                                    ((TextCheckCell2) holder.itemView).setChecked(false);
                                }
                            }
                            if ((this.defaultBannedRights.view_messages || this.defaultBannedRights.send_messages) && !this.defaultBannedRights.send_polls) {
                                this.defaultBannedRights.send_polls = true;
                                holder = this.listView.findViewHolderForAdapterPosition(this.sendPollsRow);
                                if (holder != null) {
                                    ((TextCheckCell2) holder.itemView).setChecked(false);
                                }
                            }
                            if ((this.defaultBannedRights.view_messages || this.defaultBannedRights.send_messages) && !this.defaultBannedRights.send_stickers) {
                                TL_chatBannedRights tL_chatBannedRights5 = this.defaultBannedRights;
                                tL_chatBannedRights = this.defaultBannedRights;
                                tL_chatBannedRights2 = this.defaultBannedRights;
                                this.defaultBannedRights.send_inline = true;
                                tL_chatBannedRights2.send_gifs = true;
                                tL_chatBannedRights.send_games = true;
                                tL_chatBannedRights5.send_stickers = true;
                                holder = this.listView.findViewHolderForAdapterPosition(this.sendStickersRow);
                                if (holder != null) {
                                    ((TextCheckCell2) holder.itemView).setChecked(false);
                                }
                            }
                            if ((this.defaultBannedRights.view_messages || this.defaultBannedRights.send_messages) && !this.defaultBannedRights.embed_links) {
                                this.defaultBannedRights.embed_links = true;
                                holder = this.listView.findViewHolderForAdapterPosition(this.embedLinksRow);
                                if (holder != null) {
                                    ((TextCheckCell2) holder.itemView).setChecked(false);
                                    return;
                                }
                                return;
                            }
                            return;
                        } else if (!(this.defaultBannedRights.embed_links && this.defaultBannedRights.send_inline && this.defaultBannedRights.send_media && this.defaultBannedRights.send_polls) && this.defaultBannedRights.send_messages) {
                            this.defaultBannedRights.send_messages = false;
                            holder = this.listView.findViewHolderForAdapterPosition(this.sendMessagesRow);
                            if (holder != null) {
                                ((TextCheckCell2) holder.itemView).setChecked(true);
                                return;
                            }
                            return;
                        } else {
                            return;
                        }
                    }
                } else if (TextUtils.isEmpty(this.currentChat.username) || !(position == this.pinMessagesRow || position == this.changeInfoRow)) {
                    Toast.makeText(getParentActivity(), LocaleController.getString("EditCantEditPermissions", R.string.EditCantEditPermissions), 0).show();
                    return;
                } else {
                    Toast.makeText(getParentActivity(), LocaleController.getString("EditCantEditPermissionsPublic", R.string.EditCantEditPermissionsPublic), 0).show();
                    return;
                }
            }
        }
        TL_chatBannedRights bannedRights = null;
        TL_chatAdminRights adminRights = null;
        int user_id = 0;
        boolean canEditAdmin = false;
        ChannelParticipant channelParticipant;
        if (listAdapter) {
            participant = this.listViewAdapter.getItem(position);
            if (participant instanceof ChannelParticipant) {
                channelParticipant = (ChannelParticipant) participant;
                user_id = channelParticipant.user_id;
                bannedRights = channelParticipant.banned_rights;
                adminRights = channelParticipant.admin_rights;
                canEditAdmin = !((channelParticipant instanceof TL_channelParticipantAdmin) || (channelParticipant instanceof TL_channelParticipantCreator)) || channelParticipant.can_edit;
                if (participant instanceof TL_channelParticipantCreator) {
                    adminRights = new TL_chatAdminRights();
                    adminRights.add_admins = true;
                    adminRights.pin_messages = true;
                    adminRights.invite_users = true;
                    adminRights.ban_users = true;
                    adminRights.delete_messages = true;
                    adminRights.edit_messages = true;
                    adminRights.post_messages = true;
                    adminRights.change_info = true;
                }
            } else if (participant instanceof ChatParticipant) {
                user_id = ((ChatParticipant) participant).user_id;
                canEditAdmin = this.currentChat.creator;
                if (participant instanceof TL_chatParticipantCreator) {
                    adminRights = new TL_chatAdminRights();
                    adminRights.add_admins = true;
                    adminRights.pin_messages = true;
                    adminRights.invite_users = true;
                    adminRights.ban_users = true;
                    adminRights.delete_messages = true;
                    adminRights.edit_messages = true;
                    adminRights.post_messages = true;
                    adminRights.change_info = true;
                }
            }
        } else {
            TLObject object = this.searchListViewAdapter.getItem(position);
            if (object instanceof User) {
                user = (User) object;
                MessagesController.getInstance(this.currentAccount).putUser(user, false);
                SparseArray sparseArray = this.participantsMap;
                user_id = user.id;
                participant = (TLObject) sparseArray.get(user_id);
            } else if ((object instanceof ChannelParticipant) || (object instanceof ChatParticipant)) {
                participant = object;
            } else {
                participant = null;
            }
            if (participant instanceof ChannelParticipant) {
                if (!(participant instanceof TL_channelParticipantCreator)) {
                    channelParticipant = (ChannelParticipant) participant;
                    user_id = channelParticipant.user_id;
                    canEditAdmin = !((channelParticipant instanceof TL_channelParticipantAdmin) || (channelParticipant instanceof TL_channelParticipantCreator)) || channelParticipant.can_edit;
                    bannedRights = channelParticipant.banned_rights;
                    adminRights = channelParticipant.admin_rights;
                } else {
                    return;
                }
            } else if (participant instanceof ChatParticipant) {
                if (!(participant instanceof TL_chatParticipantCreator)) {
                    user_id = ((ChatParticipant) participant).user_id;
                    canEditAdmin = this.currentChat.creator;
                    bannedRights = null;
                    adminRights = null;
                } else {
                    return;
                }
            } else if (participant == null) {
                canEditAdmin = true;
            }
        }
        if (user_id == 0) {
            return;
        }
        boolean canEdit;
        if (this.selectType != 0) {
            if (this.selectType != 3 && this.selectType != 1) {
                removeUser(user_id);
            } else if (canEditAdmin && ((participant instanceof TL_channelParticipantAdmin) || (participant instanceof TL_chatParticipantAdmin))) {
                user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(user_id));
                TL_chatBannedRights br = bannedRights;
                TL_chatAdminRights ar = adminRights;
                canEdit = canEditAdmin;
                Builder builder = new Builder(getParentActivity());
                builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                builder.setMessage(LocaleController.formatString("AdminWillBeRemoved", R.string.AdminWillBeRemoved, ContactsController.formatName(user.first_name, user.last_name)));
                builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new ChatUsersActivity$$Lambda$21(this, user, participant, ar, br, canEdit));
                builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                showDialog(builder.create());
            } else {
                openRightsEdit(user_id, participant, adminRights, bannedRights, canEditAdmin, this.selectType == 1 ? 0 : 1, false);
            }
        } else if (this.type == 0) {
            createMenuForParticipant(this.listViewAdapter.getItem(position), false);
        } else {
            canEdit = false;
            if (this.type == 1) {
                canEdit = user_id != UserConfig.getInstance(this.currentAccount).getClientUserId() && (this.currentChat.creator || canEditAdmin);
            } else if (this.type == 0 || this.type == 3) {
                canEdit = ChatObject.canBlockUsers(this.currentChat);
            }
            if ((this.type == 1 || !this.isChannel) && !(this.type == 2 && this.selectType == 0)) {
                if (bannedRights == null) {
                    bannedRights = new TL_chatBannedRights();
                    bannedRights.view_messages = true;
                    bannedRights.send_stickers = true;
                    bannedRights.send_media = true;
                    bannedRights.embed_links = true;
                    bannedRights.send_messages = true;
                    bannedRights.send_games = true;
                    bannedRights.send_inline = true;
                    bannedRights.send_gifs = true;
                    bannedRights.pin_messages = true;
                    bannedRights.send_polls = true;
                    bannedRights.invite_users = true;
                    bannedRights.change_info = true;
                }
                ChatRightsEditActivity fragment3 = new ChatRightsEditActivity(user_id, this.chatId, adminRights, this.defaultBannedRights, bannedRights, this.type == 1 ? 0 : 1, canEdit, participant == null);
                fragment3.setDelegate(new ChatUsersActivity$$Lambda$22(this, participant));
                presentFragment(fragment3);
                return;
            }
            args = new Bundle();
            args.putInt("user_id", user_id);
            presentFragment(new ProfileActivity(args));
        }
    }

    final /* synthetic */ void lambda$null$1$ChatUsersActivity(int uid, TLObject participant) {
        if (participant != null && this.participantsMap.get(uid) == null) {
            this.participants.add(participant);
            Collections.sort(this.participants, new ChatUsersActivity$$Lambda$23(this));
            updateRows();
            this.listViewAdapter.notifyDataSetChanged();
        }
    }

    final /* synthetic */ int lambda$null$0$ChatUsersActivity(TLObject lhs, TLObject rhs) {
        int type1 = getChannelAdminParticipantType(lhs);
        int type2 = getChannelAdminParticipantType(rhs);
        if (type1 > type2) {
            return 1;
        }
        if (type1 < type2) {
            return -1;
        }
        return 0;
    }

    final /* synthetic */ void lambda$null$2$ChatUsersActivity(User user, String param, ContactsActivity activity) {
        if (user != null && user.bot && this.isChannel) {
            openRightsEdit(user.id, null, null, null, true, 0, true);
        } else {
            MessagesController.getInstance(this.currentAccount).addUserToChat(this.chatId, user, null, param != null ? Utilities.parseInt(param).intValue() : 0, null, this, null);
        }
    }

    final /* synthetic */ void lambda$null$3$ChatUsersActivity(User user, TLObject participant, TL_chatAdminRights ar, TL_chatBannedRights br, boolean canEdit, DialogInterface dialog, int which) {
        int i = 1;
        int i2 = user.id;
        if (this.selectType == 1) {
            i = 0;
        }
        openRightsEdit(i2, participant, ar, br, canEdit, i, false);
    }

    final /* synthetic */ void lambda$null$4$ChatUsersActivity(TLObject participant, int rights, TL_chatAdminRights rightsAdmin, TL_chatBannedRights rightsBanned) {
        if (participant instanceof ChannelParticipant) {
            ChannelParticipant channelParticipant = (ChannelParticipant) participant;
            channelParticipant.admin_rights = rightsAdmin;
            channelParticipant.banned_rights = rightsBanned;
            TLObject p = (TLObject) this.participantsMap.get(channelParticipant.user_id);
            if (p instanceof ChannelParticipant) {
                channelParticipant = (ChannelParticipant) p;
                channelParticipant.admin_rights = rightsAdmin;
                channelParticipant.banned_rights = rightsBanned;
            }
        }
    }

    final /* synthetic */ boolean lambda$createView$6$ChatUsersActivity(View view, int position) {
        return getParentActivity() != null && this.listView.getAdapter() == this.listViewAdapter && createMenuForParticipant(this.listViewAdapter.getItem(position), false);
    }

    private void openRightsEdit2(int userId, int date, TLObject participant, TL_chatAdminRights adminRights, TL_chatBannedRights bannedRights, boolean canEditAdmin, int type, boolean removeFragment) {
        ChatRightsEditActivity fragment = new ChatRightsEditActivity(userId, this.chatId, adminRights, this.defaultBannedRights, bannedRights, type, true, false);
        fragment.setDelegate(new ChatUsersActivity$$Lambda$2(this, type, userId, date));
        presentFragment(fragment);
    }

    final /* synthetic */ void lambda$openRightsEdit2$7$ChatUsersActivity(int type, int userId, int date, int rights, TL_chatAdminRights rightsAdmin, TL_chatBannedRights rightsBanned) {
        int a;
        TLObject p;
        if (type == 0) {
            for (a = 0; a < this.participants.size(); a++) {
                p = (TLObject) this.participants.get(a);
                if (p instanceof ChannelParticipant) {
                    if (((ChannelParticipant) p).user_id == userId) {
                        ChannelParticipant newPart;
                        if (rights == 1) {
                            newPart = new TL_channelParticipantAdmin();
                        } else {
                            newPart = new TL_channelParticipant();
                        }
                        newPart.admin_rights = rightsAdmin;
                        newPart.banned_rights = rightsBanned;
                        newPart.inviter_id = UserConfig.getInstance(this.currentAccount).getClientUserId();
                        newPart.user_id = userId;
                        newPart.date = date;
                        this.participants.set(a, newPart);
                        return;
                    }
                } else if (p instanceof ChatParticipant) {
                    ChatParticipant newParticipant;
                    ChatParticipant chatParticipant = (ChatParticipant) p;
                    if (rights == 1) {
                        newParticipant = new TL_chatParticipantAdmin();
                    } else {
                        newParticipant = new TL_chatParticipant();
                    }
                    newParticipant.user_id = chatParticipant.user_id;
                    newParticipant.date = chatParticipant.date;
                    newParticipant.inviter_id = chatParticipant.inviter_id;
                    int index = this.info.participants.participants.indexOf(chatParticipant);
                    if (index >= 0) {
                        this.info.participants.participants.set(index, newParticipant);
                    }
                    loadChatParticipants(0, 200);
                }
            }
        } else if (type == 1 && rights == 0) {
            for (a = 0; a < this.participants.size(); a++) {
                p = (TLObject) this.participants.get(a);
                if ((p instanceof ChannelParticipant) && ((ChannelParticipant) p).user_id == userId) {
                    this.participants.remove(a);
                    updateRows();
                    this.listViewAdapter.notifyDataSetChanged();
                    return;
                }
            }
        }
    }

    private void openRightsEdit(int user_id, TLObject participant, TL_chatAdminRights adminRights, TL_chatBannedRights bannedRights, boolean canEditAdmin, int type, boolean removeFragment) {
        ChatRightsEditActivity fragment = new ChatRightsEditActivity(user_id, this.chatId, adminRights, this.defaultBannedRights, bannedRights, type, canEditAdmin, participant == null);
        fragment.setDelegate(new ChatUsersActivity$$Lambda$3(this, participant, user_id));
        presentFragment(fragment, removeFragment);
    }

    final /* synthetic */ void lambda$openRightsEdit$8$ChatUsersActivity(TLObject participant, int user_id, int rights, TL_chatAdminRights rightsAdmin, TL_chatBannedRights rightsBanned) {
        if (participant instanceof ChannelParticipant) {
            ChannelParticipant channelParticipant = (ChannelParticipant) participant;
            channelParticipant.admin_rights = rightsAdmin;
            channelParticipant.banned_rights = rightsBanned;
            TLObject p = (TLObject) this.participantsMap.get(channelParticipant.user_id);
            if (p instanceof ChannelParticipant) {
                channelParticipant = (ChannelParticipant) p;
                channelParticipant.admin_rights = rightsAdmin;
                channelParticipant.banned_rights = rightsBanned;
                channelParticipant.promoted_by = UserConfig.getInstance(this.currentAccount).getClientUserId();
            }
            if (this.delegate != null) {
                this.delegate.didAddParticipantToList(user_id, p);
            }
        }
        lambda$null$9$ProfileActivity();
    }

    private void removeUser(int userId) {
        if (ChatObject.isChannel(this.currentChat)) {
            MessagesController.getInstance(this.currentAccount).deleteUserFromChat(this.chatId, MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(userId)), null);
            lambda$checkDiscard$18$ChatUsersActivity();
        }
    }

    private boolean createMenuForParticipant(TLObject participant, boolean resultOnly) {
        if (participant == null || this.selectType != 0) {
            return false;
        }
        int userId;
        boolean canEdit;
        TL_chatBannedRights bannedRights;
        TL_chatAdminRights adminRights;
        int date;
        if (participant instanceof ChannelParticipant) {
            ChannelParticipant channelParticipant = (ChannelParticipant) participant;
            userId = channelParticipant.user_id;
            canEdit = channelParticipant.can_edit;
            bannedRights = channelParticipant.banned_rights;
            adminRights = channelParticipant.admin_rights;
            date = channelParticipant.date;
        } else if (participant instanceof ChatParticipant) {
            ChatParticipant chatParticipant = (ChatParticipant) participant;
            userId = chatParticipant.user_id;
            date = chatParticipant.date;
            canEdit = ChatObject.canAddAdmins(this.currentChat);
            bannedRights = null;
            adminRights = null;
        } else {
            userId = 0;
            canEdit = false;
            bannedRights = null;
            adminRights = null;
            date = 0;
        }
        if (userId == 0 || userId == UserConfig.getInstance(this.currentAccount).getClientUserId()) {
            return false;
        }
        Builder builder;
        Dialog alertDialog;
        if (this.type == 2) {
            ArrayList<String> items;
            ArrayList<Integer> actions;
            ArrayList<Integer> icons;
            User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(userId));
            boolean allowSetAdmin = ChatObject.canAddAdmins(this.currentChat) && ((participant instanceof TL_channelParticipant) || (participant instanceof TL_channelParticipantBanned) || (participant instanceof TL_chatParticipant) || canEdit);
            boolean canEditAdmin = !((participant instanceof TL_channelParticipantAdmin) || (participant instanceof TL_channelParticipantCreator) || (participant instanceof TL_chatParticipantCreator) || (participant instanceof TL_chatParticipantAdmin)) || canEdit;
            boolean editingAdmin = (participant instanceof TL_channelParticipantAdmin) || (participant instanceof TL_chatParticipantAdmin);
            if (resultOnly) {
                items = null;
                actions = null;
                icons = null;
            } else {
                items = new ArrayList();
                actions = new ArrayList();
                icons = new ArrayList();
            }
            if (allowSetAdmin) {
                if (resultOnly) {
                    return true;
                }
                items.add(editingAdmin ? LocaleController.getString("EditAdminRights", R.string.EditAdminRights) : LocaleController.getString("SetAsAdmin", R.string.SetAsAdmin));
                icons.add(Integer.valueOf(R.drawable.actions_addadmin));
                actions.add(Integer.valueOf(0));
            }
            boolean hasRemove = false;
            if (ChatObject.canBlockUsers(this.currentChat) && canEditAdmin) {
                if (resultOnly) {
                    return true;
                }
                if (this.isChannel) {
                    items.add(LocaleController.getString("ChannelRemoveUser", R.string.ChannelRemoveUser));
                    icons.add(Integer.valueOf(R.drawable.actions_remove_user));
                    actions.add(Integer.valueOf(2));
                } else {
                    if (ChatObject.isChannel(this.currentChat)) {
                        items.add(LocaleController.getString("ChangePermissions", R.string.ChangePermissions));
                        icons.add(Integer.valueOf(R.drawable.actions_permissions));
                        actions.add(Integer.valueOf(1));
                    }
                    items.add(LocaleController.getString("KickFromGroup", R.string.KickFromGroup));
                    icons.add(Integer.valueOf(R.drawable.actions_remove_user));
                    actions.add(Integer.valueOf(2));
                }
                hasRemove = true;
            }
            if (actions == null || actions.isEmpty()) {
                return false;
            }
            builder = new Builder(getParentActivity());
            builder.setItems((CharSequence[]) items.toArray(new CharSequence[actions.size()]), AndroidUtilities.toIntArray(icons), new ChatUsersActivity$$Lambda$4(this, actions, user, userId, canEditAdmin, participant, date, adminRights, bannedRights));
            alertDialog = builder.create();
            showDialog(alertDialog);
            if (hasRemove) {
                alertDialog.setItemColor(items.size() - 1, Theme.getColor("dialogTextRed2"), Theme.getColor("dialogRedIcon"));
            }
        } else {
            CharSequence[] items2;
            int[] iArr;
            if (this.type == 3 && ChatObject.canBlockUsers(this.currentChat)) {
                if (resultOnly) {
                    return true;
                }
                items2 = new CharSequence[]{LocaleController.getString("ChannelEditPermissions", R.string.ChannelEditPermissions), LocaleController.getString("ChannelDeleteFromList", R.string.ChannelDeleteFromList)};
                iArr = new int[2];
                iArr = new int[]{R.drawable.actions_permissions, R.drawable.chats_delete};
            } else if (this.type == 0 && ChatObject.canBlockUsers(this.currentChat)) {
                if (resultOnly) {
                    return true;
                }
                items2 = new CharSequence[2];
                items2[0] = this.isChannel ? LocaleController.getString("ChannelAddToChannel", R.string.ChannelAddToChannel) : LocaleController.getString("ChannelAddToGroup", R.string.ChannelAddToGroup);
                items2[1] = LocaleController.getString("ChannelDeleteFromList", R.string.ChannelDeleteFromList);
                iArr = new int[2];
                iArr = new int[]{R.drawable.actions_addmember2, R.drawable.chats_delete};
            } else if (this.type != 1 || !ChatObject.canAddAdmins(this.currentChat) || !canEdit) {
                items2 = null;
                iArr = null;
            } else if (resultOnly) {
                return true;
            } else {
                if (this.currentChat.creator || (!(participant instanceof TL_channelParticipantCreator) && canEdit)) {
                    items2 = new CharSequence[]{LocaleController.getString("EditAdminRights", R.string.EditAdminRights), LocaleController.getString("ChannelRemoveUserAdmin", R.string.ChannelRemoveUserAdmin)};
                    iArr = new int[2];
                    iArr = new int[]{R.drawable.actions_addadmin, R.drawable.actions_remove_user};
                } else {
                    items2 = new CharSequence[]{LocaleController.getString("ChannelRemoveUserAdmin", R.string.ChannelRemoveUserAdmin)};
                    iArr = new int[]{R.drawable.actions_remove_user};
                }
            }
            if (items2 == null) {
                return false;
            }
            builder = new Builder(getParentActivity());
            builder.setItems(items2, iArr, new ChatUsersActivity$$Lambda$5(this, items2, userId, adminRights, participant, bannedRights));
            alertDialog = builder.create();
            showDialog(alertDialog);
            if (this.type == 1) {
                alertDialog.setItemColor(items2.length - 1, Theme.getColor("dialogTextRed2"), Theme.getColor("dialogRedIcon"));
            }
        }
        return true;
    }

    final /* synthetic */ void lambda$createMenuForParticipant$10$ChatUsersActivity(ArrayList actions, User user, int userId, boolean canEditAdmin, TLObject participant, int date, TL_chatAdminRights adminRights, TL_chatBannedRights bannedRights, DialogInterface dialogInterface, int i) {
        if (((Integer) actions.get(i)).intValue() == 2) {
            MessagesController.getInstance(this.currentAccount).deleteUserFromChat(this.chatId, user, null);
            for (int a = 0; a < this.participants.size(); a++) {
                TLObject p = (TLObject) this.participants.get(a);
                if ((p instanceof ChannelParticipant) && ((ChannelParticipant) p).user_id == userId) {
                    this.participants.remove(a);
                    updateRows();
                    this.listViewAdapter.notifyDataSetChanged();
                    break;
                }
            }
            if (this.searchItem != null && this.actionBar.isSearchFieldVisible()) {
                this.actionBar.closeSearchField();
            }
        } else if (canEditAdmin && ((participant instanceof TL_channelParticipantAdmin) || (participant instanceof TL_chatParticipantAdmin))) {
            Builder builder2 = new Builder(getParentActivity());
            builder2.setTitle(LocaleController.getString("AppName", R.string.AppName));
            builder2.setMessage(LocaleController.formatString("AdminWillBeRemoved", R.string.AdminWillBeRemoved, ContactsController.formatName(user.first_name, user.last_name)));
            builder2.setPositiveButton(LocaleController.getString("OK", R.string.OK), new ChatUsersActivity$$Lambda$18(this, userId, date, participant, adminRights, bannedRights, canEditAdmin, actions, i));
            builder2.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
            showDialog(builder2.create());
        } else {
            openRightsEdit2(userId, date, participant, adminRights, bannedRights, canEditAdmin, ((Integer) actions.get(i)).intValue(), false);
        }
    }

    final /* synthetic */ void lambda$null$9$ChatUsersActivity(int userId, int date, TLObject participant, TL_chatAdminRights adminRights, TL_chatBannedRights bannedRights, boolean canEditAdmin, ArrayList actions, int i, DialogInterface dialog, int which) {
        openRightsEdit2(userId, date, participant, adminRights, bannedRights, canEditAdmin, ((Integer) actions.get(i)).intValue(), false);
    }

    final /* synthetic */ void lambda$createMenuForParticipant$15$ChatUsersActivity(CharSequence[] items, int userId, TL_chatAdminRights adminRights, TLObject participant, TL_chatBannedRights bannedRights, DialogInterface dialogInterface, int i) {
        ChatRightsEditActivity fragment;
        if (this.type == 1) {
            if (i == 0 && items.length == 2) {
                fragment = new ChatRightsEditActivity(userId, this.chatId, adminRights, null, null, 0, true, false);
                fragment.setDelegate(new ChatUsersActivity$$Lambda$14(this, participant, userId));
                presentFragment(fragment);
                return;
            }
            MessagesController.getInstance(this.currentAccount).setUserAdminRole(this.chatId, MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(userId)), new TL_chatAdminRights(), !this.isChannel, this, false);
            TLObject p = (TLObject) this.participantsMap.get(userId);
            if (p != null) {
                this.participantsMap.remove(userId);
                this.participants.remove(p);
                updateRows();
                this.listViewAdapter.notifyDataSetChanged();
            }
        } else if (this.type == 0 || this.type == 3) {
            if (i == 0) {
                if (this.type == 3) {
                    fragment = new ChatRightsEditActivity(userId, this.chatId, null, this.defaultBannedRights, bannedRights, 1, true, false);
                    fragment.setDelegate(new ChatUsersActivity$$Lambda$15(this, participant, userId));
                    presentFragment(fragment);
                } else if (this.type == 0) {
                    MessagesController.getInstance(this.currentAccount).addUserToChat(this.chatId, MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(userId)), null, 0, null, this, null);
                }
            } else if (i == 1) {
                TL_channels_editBanned req = new TL_channels_editBanned();
                req.user_id = MessagesController.getInstance(this.currentAccount).getInputUser(userId);
                req.channel = MessagesController.getInstance(this.currentAccount).getInputChannel(this.chatId);
                req.banned_rights = new TL_chatBannedRights();
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new ChatUsersActivity$$Lambda$16(this));
                if (this.searchItem != null && this.actionBar.isSearchFieldVisible()) {
                    this.actionBar.closeSearchField();
                }
            }
            if ((i == 0 && this.type == 0) || i == 1) {
                this.participants.remove(participant);
                updateRows();
                this.listViewAdapter.notifyDataSetChanged();
            }
        } else if (i == 0) {
            MessagesController.getInstance(this.currentAccount).deleteUserFromChat(this.chatId, MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(userId)), null);
        }
    }

    final /* synthetic */ void lambda$null$11$ChatUsersActivity(TLObject participant, int userId, int rights, TL_chatAdminRights rightsAdmin, TL_chatBannedRights rightsBanned) {
        if (participant != null) {
            ChannelParticipant channelParticipant;
            if (participant instanceof ChannelParticipant) {
                channelParticipant = (ChannelParticipant) participant;
                channelParticipant.admin_rights = rightsAdmin;
                channelParticipant.banned_rights = rightsBanned;
            }
            TLObject p = (TLObject) this.participantsMap.get(userId);
            if (p instanceof ChannelParticipant) {
                channelParticipant = (ChannelParticipant) p;
                channelParticipant.admin_rights = rightsAdmin;
                channelParticipant.banned_rights = rightsBanned;
            }
        }
    }

    final /* synthetic */ void lambda$null$12$ChatUsersActivity(TLObject participant, int userId, int rights, TL_chatAdminRights rightsAdmin, TL_chatBannedRights rightsBanned) {
        if (participant != null) {
            ChannelParticipant channelParticipant;
            if (participant instanceof ChannelParticipant) {
                channelParticipant = (ChannelParticipant) participant;
                channelParticipant.admin_rights = rightsAdmin;
                channelParticipant.banned_rights = rightsBanned;
            }
            TLObject p = (TLObject) this.participantsMap.get(userId);
            if (p instanceof ChannelParticipant) {
                channelParticipant = (ChannelParticipant) p;
                channelParticipant.admin_rights = rightsAdmin;
                channelParticipant.banned_rights = rightsBanned;
            }
        }
    }

    final /* synthetic */ void lambda$null$14$ChatUsersActivity(TLObject response, TL_error error) {
        if (response != null) {
            Updates updates = (Updates) response;
            MessagesController.getInstance(this.currentAccount).processUpdates(updates, false);
            if (!updates.chats.isEmpty()) {
                AndroidUtilities.runOnUIThread(new ChatUsersActivity$$Lambda$17(this, updates), 1000);
            }
        }
    }

    final /* synthetic */ void lambda$null$13$ChatUsersActivity(Updates updates) {
        MessagesController.getInstance(this.currentAccount).loadFullChat(((Chat) updates.chats.get(0)).id, 0, true);
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.chatInfoDidLoad) {
            ChatFull chatFull = args[0];
            boolean byChannelUsers = ((Boolean) args[2]).booleanValue();
            if (chatFull.id != this.chatId) {
                return;
            }
            if (!byChannelUsers || !ChatObject.isChannel(this.currentChat)) {
                this.info = chatFull;
                AndroidUtilities.runOnUIThread(new ChatUsersActivity$$Lambda$6(this));
            }
        }
    }

    final /* synthetic */ void lambda$didReceivedNotification$16$ChatUsersActivity() {
        loadChatParticipants(0, 200);
    }

    public boolean onBackPressed() {
        return checkDiscard();
    }

    public void setDelegate(ChatUsersActivityDelegate chatUsersActivityDelegate) {
        this.delegate = chatUsersActivityDelegate;
    }

    private boolean checkDiscard() {
        if (ChatObject.getBannedRightsString(this.defaultBannedRights).equals(this.initialBannedRights)) {
            return true;
        }
        Builder builder = new Builder(getParentActivity());
        builder.setTitle(LocaleController.getString("UserRestrictionsApplyChanges", R.string.UserRestrictionsApplyChanges));
        if (this.isChannel) {
            builder.setMessage(LocaleController.getString("ChannelSettingsChangedAlert", R.string.ChannelSettingsChangedAlert));
        } else {
            builder.setMessage(LocaleController.getString("GroupSettingsChangedAlert", R.string.GroupSettingsChangedAlert));
        }
        builder.setPositiveButton(LocaleController.getString("ApplyTheme", R.string.ApplyTheme), new ChatUsersActivity$$Lambda$7(this));
        builder.setNegativeButton(LocaleController.getString("PassportDiscard", R.string.PassportDiscard), new ChatUsersActivity$$Lambda$8(this));
        showDialog(builder.create());
        return false;
    }

    public boolean hasSelectType() {
        return this.selectType != 0;
    }

    private String formatUserPermissions(TL_chatBannedRights rights) {
        if (rights == null) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        if (rights.view_messages && this.defaultBannedRights.view_messages != rights.view_messages) {
            builder.append(LocaleController.getString("UserRestrictionsNoRead", R.string.UserRestrictionsNoRead));
        }
        if (rights.send_messages && this.defaultBannedRights.send_messages != rights.send_messages) {
            if (builder.length() != 0) {
                builder.append(", ");
            }
            builder.append(LocaleController.getString("UserRestrictionsNoSend", R.string.UserRestrictionsNoSend));
        }
        if (rights.send_media && this.defaultBannedRights.send_media != rights.send_media) {
            if (builder.length() != 0) {
                builder.append(", ");
            }
            builder.append(LocaleController.getString("UserRestrictionsNoSendMedia", R.string.UserRestrictionsNoSendMedia));
        }
        if (rights.send_stickers && this.defaultBannedRights.send_stickers != rights.send_stickers) {
            if (builder.length() != 0) {
                builder.append(", ");
            }
            builder.append(LocaleController.getString("UserRestrictionsNoSendStickers", R.string.UserRestrictionsNoSendStickers));
        }
        if (rights.send_polls && this.defaultBannedRights.send_polls != rights.send_polls) {
            if (builder.length() != 0) {
                builder.append(", ");
            }
            builder.append(LocaleController.getString("UserRestrictionsNoSendPolls", R.string.UserRestrictionsNoSendPolls));
        }
        if (rights.embed_links && this.defaultBannedRights.embed_links != rights.embed_links) {
            if (builder.length() != 0) {
                builder.append(", ");
            }
            builder.append(LocaleController.getString("UserRestrictionsNoEmbedLinks", R.string.UserRestrictionsNoEmbedLinks));
        }
        if (rights.invite_users && this.defaultBannedRights.invite_users != rights.invite_users) {
            if (builder.length() != 0) {
                builder.append(", ");
            }
            builder.append(LocaleController.getString("UserRestrictionsNoInviteUsers", R.string.UserRestrictionsNoInviteUsers));
        }
        if (rights.pin_messages && this.defaultBannedRights.pin_messages != rights.pin_messages) {
            if (builder.length() != 0) {
                builder.append(", ");
            }
            builder.append(LocaleController.getString("UserRestrictionsNoPinMessages", R.string.UserRestrictionsNoPinMessages));
        }
        if (rights.change_info && this.defaultBannedRights.change_info != rights.change_info) {
            if (builder.length() != 0) {
                builder.append(", ");
            }
            builder.append(LocaleController.getString("UserRestrictionsNoChangeInfo", R.string.UserRestrictionsNoChangeInfo));
        }
        if (builder.length() != 0) {
            builder.replace(0, 1, builder.substring(0, 1).toUpperCase());
            builder.append('.');
        }
        return builder.toString();
    }

    private void processDone() {
        if (this.type == 3) {
            if (!ChatObject.getBannedRightsString(this.defaultBannedRights).equals(this.initialBannedRights)) {
                MessagesController.getInstance(this.currentAccount).setDefaultBannedRole(this.chatId, this.defaultBannedRights, ChatObject.isChannel(this.currentChat), this);
                Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.chatId));
                if (chat != null) {
                    chat.default_banned_rights = this.defaultBannedRights;
                }
            }
            lambda$checkDiscard$18$ChatUsersActivity();
        }
    }

    public void setInfo(ChatFull chatFull) {
        this.info = chatFull;
    }

    private int getChannelAdminParticipantType(TLObject participant) {
        if ((participant instanceof TL_channelParticipantCreator) || (participant instanceof TL_channelParticipantSelf)) {
            return 0;
        }
        if ((participant instanceof TL_channelParticipantAdmin) || (participant instanceof TL_channelParticipant)) {
            return 1;
        }
        return 2;
    }

    private void loadChatParticipants(int offset, int count) {
        int i = 0;
        if (!this.loadingUsers) {
            if (ChatObject.isChannel(this.currentChat)) {
                this.loadingUsers = true;
                if (!(this.emptyView == null || this.firstLoaded)) {
                    this.emptyView.showProgress();
                }
                if (this.listViewAdapter != null) {
                    this.listViewAdapter.notifyDataSetChanged();
                }
                TL_channels_getParticipants req = new TL_channels_getParticipants();
                req.channel = MessagesController.getInstance(this.currentAccount).getInputChannel(this.chatId);
                if (this.type == 0) {
                    req.filter = new TL_channelParticipantsKicked();
                } else if (this.type == 1) {
                    req.filter = new TL_channelParticipantsAdmins();
                } else if (this.type == 2) {
                    req.filter = new TL_channelParticipantsRecent();
                } else if (this.type == 3) {
                    req.filter = new TL_channelParticipantsBanned();
                }
                req.filter.q = "";
                req.offset = offset;
                req.limit = count;
                ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new ChatUsersActivity$$Lambda$9(this)), this.classGuid);
                return;
            }
            this.loadingUsers = false;
            this.participants.clear();
            this.participantsMap.clear();
            int size;
            int a;
            ChatParticipant participant;
            if (this.type == 1) {
                if (this.info != null) {
                    size = this.info.participants.participants.size();
                    for (a = 0; a < size; a++) {
                        participant = (ChatParticipant) this.info.participants.participants.get(a);
                        if ((participant instanceof TL_chatParticipantCreator) || (participant instanceof TL_chatParticipantAdmin)) {
                            this.participants.add(participant);
                        }
                        this.participantsMap.put(participant.user_id, participant);
                    }
                }
            } else if (this.type == 2 && this.info != null) {
                int selfUserId = UserConfig.getInstance(this.currentAccount).clientUserId;
                size = this.info.participants.participants.size();
                for (a = 0; a < size; a++) {
                    participant = (ChatParticipant) this.info.participants.participants.get(a);
                    if (this.selectType == 0 || participant.user_id != selfUserId) {
                        this.participants.add(participant);
                        this.participantsMap.put(participant.user_id, participant);
                    }
                }
            }
            if (this.listViewAdapter != null) {
                this.listViewAdapter.notifyDataSetChanged();
            }
            if (!(this.searchItem == null || this.actionBar.isSearchFieldVisible())) {
                ActionBarMenuItem actionBarMenuItem = this.searchItem;
                if (this.selectType == 0 && this.participants.isEmpty()) {
                    i = 8;
                }
                actionBarMenuItem.setVisibility(i);
            }
            updateRows();
            if (this.listViewAdapter != null) {
                this.listViewAdapter.notifyDataSetChanged();
            }
        }
    }

    final /* synthetic */ void lambda$loadChatParticipants$22$ChatUsersActivity(TLObject response, TL_error error) {
        AndroidUtilities.runOnUIThread(new ChatUsersActivity$$Lambda$11(this, error, response));
    }

    final /* synthetic */ void lambda$null$21$ChatUsersActivity(TL_error error, TLObject response) {
        this.loadingUsers = false;
        this.firstLoaded = true;
        if (this.emptyView != null) {
            this.emptyView.showTextView();
        }
        if (error == null) {
            int a;
            TL_channels_channelParticipants res = (TL_channels_channelParticipants) response;
            MessagesController.getInstance(this.currentAccount).putUsers(res.users, false);
            int selfId = UserConfig.getInstance(this.currentAccount).getClientUserId();
            if (this.selectType != 0) {
                for (a = 0; a < res.participants.size(); a++) {
                    if (((ChannelParticipant) res.participants.get(a)).user_id == selfId) {
                        res.participants.remove(a);
                        break;
                    }
                }
            }
            this.participants.clear();
            this.participants.addAll(res.participants);
            this.participantsMap.clear();
            int size = res.participants.size();
            for (a = 0; a < size; a++) {
                ChannelParticipant participant = (ChannelParticipant) res.participants.get(a);
                this.participantsMap.put(participant.user_id, participant);
            }
            try {
                if (this.type == 0 || this.type == 3 || this.type == 2) {
                    Collections.sort(res.participants, new ChatUsersActivity$$Lambda$12(this, ConnectionsManager.getInstance(this.currentAccount).getCurrentTime()));
                } else if (this.type == 1) {
                    Collections.sort(res.participants, new ChatUsersActivity$$Lambda$13(this));
                }
            } catch (Throwable e) {
                FileLog.e(e);
            }
        }
        updateRows();
        if (this.listViewAdapter != null) {
            this.listViewAdapter.notifyDataSetChanged();
        }
    }

    final /* synthetic */ int lambda$null$19$ChatUsersActivity(int currentTime, ChannelParticipant lhs, ChannelParticipant rhs) {
        User user1 = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(rhs.user_id));
        User user2 = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(lhs.user_id));
        int status1 = 0;
        int status2 = 0;
        if (!(user1 == null || user1.status == null)) {
            status1 = user1.self ? currentTime + 50000 : user1.status.expires;
        }
        if (!(user2 == null || user2.status == null)) {
            status2 = user2.self ? currentTime + 50000 : user2.status.expires;
        }
        if (status1 <= 0 || status2 <= 0) {
            if (status1 >= 0 || status2 >= 0) {
                if ((status1 < 0 && status2 > 0) || (status1 == 0 && status2 != 0)) {
                    return -1;
                }
                if (status2 < 0 && status1 > 0) {
                    return 1;
                }
                if (status2 != 0 || status1 == 0) {
                    return 0;
                }
                return 1;
            } else if (status1 > status2) {
                return 1;
            } else {
                if (status1 < status2) {
                    return -1;
                }
                return 0;
            }
        } else if (status1 > status2) {
            return 1;
        } else {
            if (status1 < status2) {
                return -1;
            }
            return 0;
        }
    }

    final /* synthetic */ int lambda$null$20$ChatUsersActivity(ChannelParticipant lhs, ChannelParticipant rhs) {
        int type1 = getChannelAdminParticipantType(lhs);
        int type2 = getChannelAdminParticipantType(rhs);
        if (type1 > type2) {
            return 1;
        }
        if (type1 < type2) {
            return -1;
        }
        return 0;
    }

    public void onResume() {
        super.onResume();
        if (this.listViewAdapter != null) {
            this.listViewAdapter.notifyDataSetChanged();
        }
    }

    protected void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        if (isOpen && !backward && this.needOpenSearch) {
            this.searchItem.openSearch(true);
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        ThemeDescriptionDelegate cellDelegate = new ChatUsersActivity$$Lambda$10(this);
        ThemeDescription[] themeDescriptionArr = new ThemeDescription[36];
        themeDescriptionArr[0] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{ManageChatUserCell.class, ManageChatTextCell.class, TextCheckCell2.class, TextSettingsCell.class}, null, null, null, "windowBackgroundWhite");
        themeDescriptionArr[1] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundGray");
        themeDescriptionArr[2] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault");
        themeDescriptionArr[3] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault");
        themeDescriptionArr[4] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon");
        themeDescriptionArr[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle");
        themeDescriptionArr[6] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector");
        themeDescriptionArr[7] = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21");
        themeDescriptionArr[8] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, "divider");
        themeDescriptionArr[9] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, "windowBackgroundGrayShadow");
        themeDescriptionArr[10] = new ThemeDescription(this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteGrayText4");
        themeDescriptionArr[11] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, "windowBackgroundGrayShadow");
        themeDescriptionArr[12] = new ThemeDescription(this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlueHeader");
        themeDescriptionArr[13] = new ThemeDescription(this.listView, 0, new Class[]{GraySectionCell.class}, new String[]{"textView"}, null, null, null, "key_graySectionText");
        themeDescriptionArr[14] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{GraySectionCell.class}, null, null, null, "graySection");
        themeDescriptionArr[15] = new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText");
        themeDescriptionArr[16] = new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, null, null, null, "windowBackgroundWhiteValueText");
        themeDescriptionArr[17] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell2.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText");
        themeDescriptionArr[18] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell2.class}, new String[]{"valueTextView"}, null, null, null, "windowBackgroundWhiteGrayText2");
        themeDescriptionArr[19] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell2.class}, new String[]{"checkBox"}, null, null, null, "switch2Track");
        themeDescriptionArr[20] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell2.class}, new String[]{"checkBox"}, null, null, null, "switch2TrackChecked");
        themeDescriptionArr[21] = new ThemeDescription(this.listView, 0, new Class[]{ManageChatUserCell.class}, new String[]{"nameTextView"}, null, null, null, "windowBackgroundWhiteBlackText");
        themeDescriptionArr[22] = new ThemeDescription(this.listView, 0, new Class[]{ManageChatUserCell.class}, new String[]{"statusColor"}, null, null, cellDelegate, "windowBackgroundWhiteGrayText");
        themeDescriptionArr[23] = new ThemeDescription(this.listView, 0, new Class[]{ManageChatUserCell.class}, new String[]{"statusOnlineColor"}, null, null, cellDelegate, "windowBackgroundWhiteBlueText");
        themeDescriptionArr[24] = new ThemeDescription(this.listView, 0, new Class[]{ManageChatUserCell.class}, null, new Drawable[]{Theme.avatar_broadcastDrawable, Theme.avatar_savedDrawable}, null, "avatar_text");
        themeDescriptionArr[25] = new ThemeDescription(null, 0, null, null, null, cellDelegate, "avatar_backgroundRed");
        themeDescriptionArr[26] = new ThemeDescription(null, 0, null, null, null, cellDelegate, "avatar_backgroundOrange");
        themeDescriptionArr[27] = new ThemeDescription(null, 0, null, null, null, cellDelegate, "avatar_backgroundViolet");
        themeDescriptionArr[28] = new ThemeDescription(null, 0, null, null, null, cellDelegate, "avatar_backgroundGreen");
        themeDescriptionArr[29] = new ThemeDescription(null, 0, null, null, null, cellDelegate, "avatar_backgroundCyan");
        themeDescriptionArr[30] = new ThemeDescription(null, 0, null, null, null, cellDelegate, "avatar_backgroundBlue");
        themeDescriptionArr[31] = new ThemeDescription(null, 0, null, null, null, cellDelegate, "avatar_backgroundPink");
        themeDescriptionArr[32] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText");
        themeDescriptionArr[33] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"imageView"}, null, null, null, "windowBackgroundWhiteGrayIcon");
        themeDescriptionArr[34] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"imageView"}, null, null, null, "windowBackgroundWhiteBlueButton");
        themeDescriptionArr[35] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlueIcon");
        return themeDescriptionArr;
    }

    final /* synthetic */ void lambda$getThemeDescriptions$23$ChatUsersActivity() {
        if (this.listView != null) {
            int count = this.listView.getChildCount();
            for (int a = 0; a < count; a++) {
                View child = this.listView.getChildAt(a);
                if (child instanceof ManageChatUserCell) {
                    ((ManageChatUserCell) child).update(0);
                }
            }
        }
    }
}
