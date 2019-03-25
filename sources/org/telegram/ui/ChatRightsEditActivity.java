package org.telegram.ui;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TimePicker;
import android.widget.Toast;
import java.util.Calendar;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.TL_chatAdminRights;
import org.telegram.tgnet.TLRPC.TL_chatBannedRights;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet.BottomSheetCell;
import org.telegram.ui.ActionBar.BottomSheet.Builder;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate;
import org.telegram.ui.Cells.DialogRadioCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCheckCell2;
import org.telegram.ui.Cells.TextDetailCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Cells2.UserCell;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;

public class ChatRightsEditActivity extends BaseFragment {
    public static final int TYPE_ADMIN = 0;
    public static final int TYPE_BANNED = 1;
    private static final int done_button = 1;
    private int addAdminsRow;
    private int addUsersRow;
    private TL_chatAdminRights adminRights;
    private int banUsersRow;
    private TL_chatBannedRights bannedRights;
    private boolean canEdit;
    private int cantEditInfoRow;
    private int changeInfoRow;
    private int chatId;
    private String currentBannedRights = "";
    private Chat currentChat;
    private int currentType;
    private User currentUser;
    private TL_chatBannedRights defaultBannedRights;
    private ChatRightsEditActivityDelegate delegate;
    private int deleteMessagesRow;
    private int editMesagesRow;
    private int embedLinksRow;
    private boolean isAddingNew;
    private boolean isChannel;
    private RecyclerListView listView;
    private ListAdapter listViewAdapter;
    private TL_chatAdminRights myAdminRights;
    private int pinMessagesRow;
    private int postMessagesRow;
    private int removeAdminRow;
    private int removeAdminShadowRow;
    private int rightsShadowRow;
    private int rowCount;
    private int sendMediaRow;
    private int sendMessagesRow;
    private int sendPollsRow;
    private int sendStickersRow;
    private int untilDateRow;
    private int untilSectionRow;

    public interface ChatRightsEditActivityDelegate {
        void didSetRights(int i, TL_chatAdminRights tL_chatAdminRights, TL_chatBannedRights tL_chatBannedRights);
    }

    private class ListAdapter extends SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean isEnabled(ViewHolder holder) {
            boolean z = true;
            if (!ChatRightsEditActivity.this.canEdit) {
                return false;
            }
            int type = holder.getItemViewType();
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
                if (position == ChatRightsEditActivity.this.addAdminsRow) {
                    return ChatRightsEditActivity.this.myAdminRights.add_admins;
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
                z = false;
            }
            return z;
        }

        public int getItemCount() {
            return ChatRightsEditActivity.this.rowCount;
        }

        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    view = new UserCell(this.mContext, 4, 0);
                    view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    break;
                case 1:
                    view = new TextInfoPrivacyCell(this.mContext);
                    view.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                    break;
                case 2:
                    view = new TextSettingsCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    break;
                case 3:
                    view = new HeaderCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    break;
                case 4:
                    view = new TextCheckCell2(this.mContext);
                    view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    break;
                case 5:
                    view = new ShadowSectionCell(this.mContext);
                    break;
                default:
                    view = new TextDetailCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    break;
            }
            return new Holder(view);
        }

        public void onBindViewHolder(ViewHolder holder, int position) {
            switch (holder.getItemViewType()) {
                case 0:
                    holder.itemView.setData(ChatRightsEditActivity.this.currentUser, null, null, 0);
                    return;
                case 1:
                    TextInfoPrivacyCell privacyCell = holder.itemView;
                    if (position == ChatRightsEditActivity.this.cantEditInfoRow) {
                        privacyCell.setText(LocaleController.getString("EditAdminCantEdit", NUM));
                        return;
                    }
                    return;
                case 2:
                    TextSettingsCell actionCell = holder.itemView;
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
                    }
                    return;
                case 3:
                    HeaderCell headerCell = holder.itemView;
                    if (ChatRightsEditActivity.this.currentType == 0) {
                        headerCell.setText(LocaleController.getString("EditAdminWhatCanDo", NUM));
                        return;
                    } else if (ChatRightsEditActivity.this.currentType == 1) {
                        headerCell.setText(LocaleController.getString("UserRestrictionsCanDo", NUM));
                        return;
                    } else {
                        return;
                    }
                case 4:
                    boolean z;
                    TextCheckCell2 checkCell = holder.itemView;
                    String string;
                    if (position == ChatRightsEditActivity.this.changeInfoRow) {
                        if (ChatRightsEditActivity.this.currentType == 0) {
                            if (ChatRightsEditActivity.this.isChannel) {
                                checkCell.setTextAndCheck(LocaleController.getString("EditAdminChangeChannelInfo", NUM), ChatRightsEditActivity.this.adminRights.change_info, true);
                            } else {
                                checkCell.setTextAndCheck(LocaleController.getString("EditAdminChangeGroupInfo", NUM), ChatRightsEditActivity.this.adminRights.change_info, true);
                            }
                        } else if (ChatRightsEditActivity.this.currentType == 1) {
                            string = LocaleController.getString("UserRestrictionsChangeInfo", NUM);
                            z = (ChatRightsEditActivity.this.bannedRights.change_info || ChatRightsEditActivity.this.defaultBannedRights.change_info) ? false : true;
                            checkCell.setTextAndCheck(string, z, false);
                            checkCell.setIcon(ChatRightsEditActivity.this.defaultBannedRights.change_info ? NUM : 0);
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
                        checkCell.setTextAndCheck(LocaleController.getString("EditAdminAddAdmins", NUM), ChatRightsEditActivity.this.adminRights.add_admins, false);
                    } else if (position == ChatRightsEditActivity.this.banUsersRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("EditAdminBanUsers", NUM), ChatRightsEditActivity.this.adminRights.ban_users, true);
                    } else if (position == ChatRightsEditActivity.this.addUsersRow) {
                        if (ChatRightsEditActivity.this.currentType == 0) {
                            if (ChatObject.isActionBannedByDefault(ChatRightsEditActivity.this.currentChat, 3)) {
                                checkCell.setTextAndCheck(LocaleController.getString("EditAdminAddUsers", NUM), ChatRightsEditActivity.this.adminRights.invite_users, true);
                            } else {
                                checkCell.setTextAndCheck(LocaleController.getString("EditAdminAddUsersViaLink", NUM), ChatRightsEditActivity.this.adminRights.invite_users, true);
                            }
                        } else if (ChatRightsEditActivity.this.currentType == 1) {
                            string = LocaleController.getString("UserRestrictionsInviteUsers", NUM);
                            z = (ChatRightsEditActivity.this.bannedRights.invite_users || ChatRightsEditActivity.this.defaultBannedRights.invite_users) ? false : true;
                            checkCell.setTextAndCheck(string, z, true);
                            checkCell.setIcon(ChatRightsEditActivity.this.defaultBannedRights.invite_users ? NUM : 0);
                        }
                    } else if (position == ChatRightsEditActivity.this.pinMessagesRow) {
                        if (ChatRightsEditActivity.this.currentType == 0) {
                            checkCell.setTextAndCheck(LocaleController.getString("EditAdminPinMessages", NUM), ChatRightsEditActivity.this.adminRights.pin_messages, true);
                        } else if (ChatRightsEditActivity.this.currentType == 1) {
                            string = LocaleController.getString("UserRestrictionsPinMessages", NUM);
                            z = (ChatRightsEditActivity.this.bannedRights.pin_messages || ChatRightsEditActivity.this.defaultBannedRights.pin_messages) ? false : true;
                            checkCell.setTextAndCheck(string, z, true);
                            checkCell.setIcon(ChatRightsEditActivity.this.defaultBannedRights.pin_messages ? NUM : 0);
                        }
                    } else if (position == ChatRightsEditActivity.this.sendMessagesRow) {
                        string = LocaleController.getString("UserRestrictionsSend", NUM);
                        z = (ChatRightsEditActivity.this.bannedRights.send_messages || ChatRightsEditActivity.this.defaultBannedRights.send_messages) ? false : true;
                        checkCell.setTextAndCheck(string, z, true);
                        checkCell.setIcon(ChatRightsEditActivity.this.defaultBannedRights.send_messages ? NUM : 0);
                    } else if (position == ChatRightsEditActivity.this.sendMediaRow) {
                        string = LocaleController.getString("UserRestrictionsSendMedia", NUM);
                        z = (ChatRightsEditActivity.this.bannedRights.send_media || ChatRightsEditActivity.this.defaultBannedRights.send_media) ? false : true;
                        checkCell.setTextAndCheck(string, z, true);
                        checkCell.setIcon(ChatRightsEditActivity.this.defaultBannedRights.send_media ? NUM : 0);
                    } else if (position == ChatRightsEditActivity.this.sendStickersRow) {
                        string = LocaleController.getString("UserRestrictionsSendStickers", NUM);
                        z = (ChatRightsEditActivity.this.bannedRights.send_stickers || ChatRightsEditActivity.this.defaultBannedRights.send_stickers) ? false : true;
                        checkCell.setTextAndCheck(string, z, true);
                        checkCell.setIcon(ChatRightsEditActivity.this.defaultBannedRights.send_stickers ? NUM : 0);
                    } else if (position == ChatRightsEditActivity.this.embedLinksRow) {
                        string = LocaleController.getString("UserRestrictionsEmbedLinks", NUM);
                        z = (ChatRightsEditActivity.this.bannedRights.embed_links || ChatRightsEditActivity.this.defaultBannedRights.embed_links) ? false : true;
                        checkCell.setTextAndCheck(string, z, true);
                        checkCell.setIcon(ChatRightsEditActivity.this.defaultBannedRights.embed_links ? NUM : 0);
                    } else if (position == ChatRightsEditActivity.this.sendPollsRow) {
                        string = LocaleController.getString("UserRestrictionsSendPolls", NUM);
                        z = (ChatRightsEditActivity.this.bannedRights.send_polls || ChatRightsEditActivity.this.defaultBannedRights.send_polls) ? false : true;
                        checkCell.setTextAndCheck(string, z, true);
                        checkCell.setIcon(ChatRightsEditActivity.this.defaultBannedRights.send_polls ? NUM : 0);
                    }
                    if (position == ChatRightsEditActivity.this.sendMediaRow || position == ChatRightsEditActivity.this.sendStickersRow || position == ChatRightsEditActivity.this.embedLinksRow || position == ChatRightsEditActivity.this.sendPollsRow) {
                        if (ChatRightsEditActivity.this.bannedRights.send_messages || ChatRightsEditActivity.this.bannedRights.view_messages || ChatRightsEditActivity.this.defaultBannedRights.send_messages || ChatRightsEditActivity.this.defaultBannedRights.view_messages) {
                            z = false;
                        } else {
                            z = true;
                        }
                        checkCell.setEnabled(z);
                        return;
                    } else if (position == ChatRightsEditActivity.this.sendMessagesRow) {
                        z = (ChatRightsEditActivity.this.bannedRights.view_messages || ChatRightsEditActivity.this.defaultBannedRights.view_messages) ? false : true;
                        checkCell.setEnabled(z);
                        return;
                    } else {
                        return;
                    }
                case 5:
                    ShadowSectionCell shadowCell = holder.itemView;
                    if (position == ChatRightsEditActivity.this.rightsShadowRow) {
                        shadowCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, ChatRightsEditActivity.this.removeAdminRow == -1 ? NUM : NUM, "windowBackgroundGrayShadow"));
                        return;
                    } else if (position == ChatRightsEditActivity.this.removeAdminShadowRow) {
                        shadowCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                        return;
                    } else {
                        shadowCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                        return;
                    }
                case 6:
                    TextDetailCell detailCell = holder.itemView;
                    if (position == ChatRightsEditActivity.this.untilDateRow) {
                        String value;
                        if (ChatRightsEditActivity.this.bannedRights.until_date == 0 || Math.abs(((long) ChatRightsEditActivity.this.bannedRights.until_date) - (System.currentTimeMillis() / 1000)) > NUM) {
                            value = LocaleController.getString("UserRestrictionsUntilForever", NUM);
                        } else {
                            value = LocaleController.formatDateForBan((long) ChatRightsEditActivity.this.bannedRights.until_date);
                        }
                        detailCell.setTextAndValue(LocaleController.getString("UserRestrictionsDuration", NUM), value, false);
                        return;
                    }
                    return;
                default:
                    return;
            }
        }

        public int getItemViewType(int position) {
            if (position == 0) {
                return 0;
            }
            if (position == 1 || position == ChatRightsEditActivity.this.rightsShadowRow || position == ChatRightsEditActivity.this.removeAdminShadowRow || position == ChatRightsEditActivity.this.untilSectionRow) {
                return 5;
            }
            if (position == 2) {
                return 3;
            }
            if (position == ChatRightsEditActivity.this.changeInfoRow || position == ChatRightsEditActivity.this.postMessagesRow || position == ChatRightsEditActivity.this.editMesagesRow || position == ChatRightsEditActivity.this.deleteMessagesRow || position == ChatRightsEditActivity.this.addAdminsRow || position == ChatRightsEditActivity.this.banUsersRow || position == ChatRightsEditActivity.this.addUsersRow || position == ChatRightsEditActivity.this.pinMessagesRow || position == ChatRightsEditActivity.this.sendMessagesRow || position == ChatRightsEditActivity.this.sendMediaRow || position == ChatRightsEditActivity.this.sendStickersRow || position == ChatRightsEditActivity.this.embedLinksRow || position == ChatRightsEditActivity.this.sendPollsRow) {
                return 4;
            }
            if (position != ChatRightsEditActivity.this.cantEditInfoRow) {
                return position == ChatRightsEditActivity.this.untilDateRow ? 6 : 2;
            } else {
                return 1;
            }
        }
    }

    public ChatRightsEditActivity(int userId, int channelId, TL_chatAdminRights rightsAdmin, TL_chatBannedRights rightsBannedDefault, TL_chatBannedRights rightsBanned, int type, boolean edit, boolean addingNew) {
        int i;
        this.isAddingNew = addingNew;
        this.chatId = channelId;
        this.currentUser = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(userId));
        this.currentType = type;
        this.canEdit = edit;
        this.currentChat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.chatId));
        if (this.currentChat != null) {
            boolean z = ChatObject.isChannel(this.currentChat) && !this.currentChat.megagroup;
            this.isChannel = z;
            this.myAdminRights = this.currentChat.admin_rights;
        }
        if (this.myAdminRights == null) {
            this.myAdminRights = new TL_chatAdminRights();
            TL_chatAdminRights tL_chatAdminRights = this.myAdminRights;
            TL_chatAdminRights tL_chatAdminRights2 = this.myAdminRights;
            TL_chatAdminRights tL_chatAdminRights3 = this.myAdminRights;
            TL_chatAdminRights tL_chatAdminRights4 = this.myAdminRights;
            TL_chatAdminRights tL_chatAdminRights5 = this.myAdminRights;
            TL_chatAdminRights tL_chatAdminRights6 = this.myAdminRights;
            TL_chatAdminRights tL_chatAdminRights7 = this.myAdminRights;
            this.myAdminRights.add_admins = true;
            tL_chatAdminRights7.pin_messages = true;
            tL_chatAdminRights6.invite_users = true;
            tL_chatAdminRights5.ban_users = true;
            tL_chatAdminRights4.delete_messages = true;
            tL_chatAdminRights3.edit_messages = true;
            tL_chatAdminRights2.post_messages = true;
            tL_chatAdminRights.change_info = true;
        }
        boolean initialIsSet;
        if (type == 0) {
            this.adminRights = new TL_chatAdminRights();
            if (rightsAdmin == null) {
                this.adminRights.change_info = this.myAdminRights.change_info;
                this.adminRights.post_messages = this.myAdminRights.post_messages;
                this.adminRights.edit_messages = this.myAdminRights.edit_messages;
                this.adminRights.delete_messages = this.myAdminRights.delete_messages;
                this.adminRights.ban_users = this.myAdminRights.ban_users;
                this.adminRights.invite_users = this.myAdminRights.invite_users;
                this.adminRights.pin_messages = this.myAdminRights.pin_messages;
                initialIsSet = false;
            } else {
                this.adminRights.change_info = rightsAdmin.change_info;
                this.adminRights.post_messages = rightsAdmin.post_messages;
                this.adminRights.edit_messages = rightsAdmin.edit_messages;
                this.adminRights.delete_messages = rightsAdmin.delete_messages;
                this.adminRights.ban_users = rightsAdmin.ban_users;
                this.adminRights.invite_users = rightsAdmin.invite_users;
                this.adminRights.pin_messages = rightsAdmin.pin_messages;
                this.adminRights.add_admins = rightsAdmin.add_admins;
                initialIsSet = this.adminRights.change_info || this.adminRights.post_messages || this.adminRights.edit_messages || this.adminRights.delete_messages || this.adminRights.ban_users || this.adminRights.invite_users || this.adminRights.pin_messages || this.adminRights.add_admins;
            }
        } else {
            TL_chatBannedRights tL_chatBannedRights;
            TL_chatBannedRights tL_chatBannedRights2;
            TL_chatBannedRights tL_chatBannedRights3;
            TL_chatBannedRights tL_chatBannedRights4;
            TL_chatBannedRights tL_chatBannedRights5;
            TL_chatBannedRights tL_chatBannedRights6;
            TL_chatBannedRights tL_chatBannedRights7;
            TL_chatBannedRights tL_chatBannedRights8;
            TL_chatBannedRights tL_chatBannedRights9;
            TL_chatBannedRights tL_chatBannedRights10;
            TL_chatBannedRights tL_chatBannedRights11;
            this.defaultBannedRights = rightsBannedDefault;
            if (this.defaultBannedRights == null) {
                this.defaultBannedRights = new TL_chatBannedRights();
                tL_chatBannedRights = this.defaultBannedRights;
                tL_chatBannedRights2 = this.defaultBannedRights;
                tL_chatBannedRights3 = this.defaultBannedRights;
                tL_chatBannedRights4 = this.defaultBannedRights;
                tL_chatBannedRights5 = this.defaultBannedRights;
                tL_chatBannedRights6 = this.defaultBannedRights;
                tL_chatBannedRights7 = this.defaultBannedRights;
                tL_chatBannedRights8 = this.defaultBannedRights;
                tL_chatBannedRights9 = this.defaultBannedRights;
                tL_chatBannedRights10 = this.defaultBannedRights;
                tL_chatBannedRights11 = this.defaultBannedRights;
                this.defaultBannedRights.pin_messages = false;
                tL_chatBannedRights11.change_info = false;
                tL_chatBannedRights10.invite_users = false;
                tL_chatBannedRights9.send_polls = false;
                tL_chatBannedRights8.send_inline = false;
                tL_chatBannedRights7.send_games = false;
                tL_chatBannedRights6.send_gifs = false;
                tL_chatBannedRights5.send_stickers = false;
                tL_chatBannedRights4.embed_links = false;
                tL_chatBannedRights3.send_messages = false;
                tL_chatBannedRights2.send_media = false;
                tL_chatBannedRights.view_messages = false;
            }
            this.bannedRights = new TL_chatBannedRights();
            if (rightsBanned == null) {
                tL_chatBannedRights = this.bannedRights;
                tL_chatBannedRights2 = this.bannedRights;
                tL_chatBannedRights3 = this.bannedRights;
                tL_chatBannedRights4 = this.bannedRights;
                tL_chatBannedRights5 = this.bannedRights;
                tL_chatBannedRights6 = this.bannedRights;
                tL_chatBannedRights7 = this.bannedRights;
                tL_chatBannedRights8 = this.bannedRights;
                tL_chatBannedRights9 = this.bannedRights;
                tL_chatBannedRights10 = this.bannedRights;
                tL_chatBannedRights11 = this.bannedRights;
                this.bannedRights.pin_messages = false;
                tL_chatBannedRights11.change_info = false;
                tL_chatBannedRights10.invite_users = false;
                tL_chatBannedRights9.send_polls = false;
                tL_chatBannedRights8.send_inline = false;
                tL_chatBannedRights7.send_games = false;
                tL_chatBannedRights6.send_gifs = false;
                tL_chatBannedRights5.send_stickers = false;
                tL_chatBannedRights4.embed_links = false;
                tL_chatBannedRights3.send_messages = false;
                tL_chatBannedRights2.send_media = false;
                tL_chatBannedRights.view_messages = false;
            } else {
                this.bannedRights.view_messages = rightsBanned.view_messages;
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
            initialIsSet = rightsBanned == null || !rightsBanned.view_messages;
        }
        this.rowCount += 3;
        if (type == 0) {
            if (this.isChannel) {
                i = this.rowCount;
                this.rowCount = i + 1;
                this.changeInfoRow = i;
                i = this.rowCount;
                this.rowCount = i + 1;
                this.postMessagesRow = i;
                i = this.rowCount;
                this.rowCount = i + 1;
                this.editMesagesRow = i;
                i = this.rowCount;
                this.rowCount = i + 1;
                this.deleteMessagesRow = i;
                i = this.rowCount;
                this.rowCount = i + 1;
                this.addUsersRow = i;
                i = this.rowCount;
                this.rowCount = i + 1;
                this.addAdminsRow = i;
            } else {
                i = this.rowCount;
                this.rowCount = i + 1;
                this.changeInfoRow = i;
                i = this.rowCount;
                this.rowCount = i + 1;
                this.deleteMessagesRow = i;
                i = this.rowCount;
                this.rowCount = i + 1;
                this.banUsersRow = i;
                i = this.rowCount;
                this.rowCount = i + 1;
                this.addUsersRow = i;
                i = this.rowCount;
                this.rowCount = i + 1;
                this.pinMessagesRow = i;
                i = this.rowCount;
                this.rowCount = i + 1;
                this.addAdminsRow = i;
            }
        } else if (type == 1) {
            i = this.rowCount;
            this.rowCount = i + 1;
            this.sendMessagesRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.sendMediaRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.sendStickersRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.sendPollsRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.embedLinksRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.addUsersRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.pinMessagesRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.changeInfoRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.untilSectionRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.untilDateRow = i;
        }
        if (this.canEdit && initialIsSet) {
            i = this.rowCount;
            this.rowCount = i + 1;
            this.rightsShadowRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.removeAdminRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.removeAdminShadowRow = i;
            this.cantEditInfoRow = -1;
            return;
        }
        this.removeAdminRow = -1;
        this.removeAdminShadowRow = -1;
        if (type != 0 || this.canEdit) {
            i = this.rowCount;
            this.rowCount = i + 1;
            this.rightsShadowRow = i;
            return;
        }
        this.rightsShadowRow = -1;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.cantEditInfoRow = i;
    }

    public View createView(Context context) {
        int i = 1;
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setAllowOverlayTitle(true);
        if (this.currentType == 0) {
            this.actionBar.setTitle(LocaleController.getString("EditAdmin", NUM));
        } else {
            this.actionBar.setTitle(LocaleController.getString("UserRestrictions", NUM));
        }
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
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
        if (this.canEdit) {
            this.actionBar.createMenu().addItemWithWidth(1, NUM, AndroidUtilities.dp(56.0f), LocaleController.getString("Done", NUM));
        }
        this.fragmentView = new FrameLayout(context);
        this.fragmentView.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
        FrameLayout frameLayout = this.fragmentView;
        this.listView = new RecyclerListView(context);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, 1, false) {
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        };
        this.listView.setItemAnimator(null);
        this.listView.setLayoutAnimation(null);
        this.listView.setLayoutManager(linearLayoutManager);
        RecyclerListView recyclerListView = this.listView;
        ListAdapter listAdapter = new ListAdapter(context);
        this.listViewAdapter = listAdapter;
        recyclerListView.setAdapter(listAdapter);
        recyclerListView = this.listView;
        if (!LocaleController.isRTL) {
            i = 2;
        }
        recyclerListView.setVerticalScrollbarPosition(i);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setOnItemClickListener(new ChatRightsEditActivity$$Lambda$0(this, context));
        return this.fragmentView;
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$createView$6$ChatRightsEditActivity(Context context, View view, int position) {
        if (!this.canEdit) {
            return;
        }
        if (position == 0) {
            Bundle args = new Bundle();
            args.putInt("user_id", this.currentUser.id);
            presentFragment(new ProfileActivity(args));
        } else if (position == this.removeAdminRow) {
            if (this.currentType == 0) {
                MessagesController.getInstance(this.currentAccount).setUserAdminRole(this.chatId, this.currentUser, new TL_chatAdminRights(), this.isChannel, getFragmentForAlert(0), this.isAddingNew);
            } else if (this.currentType == 1) {
                this.bannedRights = new TL_chatBannedRights();
                this.bannedRights.view_messages = true;
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
                MessagesController.getInstance(this.currentAccount).setUserBannedRole(this.chatId, this.currentUser, this.bannedRights, this.isChannel, getFragmentForAlert(0));
            }
            if (this.delegate != null) {
                this.delegate.didSetRights(0, this.adminRights, this.bannedRights);
            }
            finishFragment();
        } else if (position == this.untilDateRow) {
            if (getParentActivity() != null) {
                Builder builder = new Builder(context);
                builder.setApplyTopPadding(false);
                View linearLayout = new LinearLayout(context);
                linearLayout.setOrientation(1);
                HeaderCell headerCell = new HeaderCell(context, true, 23, 15, false);
                headerCell.setHeight(47);
                headerCell.setText(LocaleController.getString("UserRestrictionsDuration", NUM));
                linearLayout.addView(headerCell);
                linearLayout = new LinearLayout(context);
                linearLayout.setOrientation(1);
                linearLayout.addView(linearLayout, LayoutHelper.createLinear(-1, -2));
                BottomSheetCell[] buttons = new BottomSheetCell[5];
                for (int a = 0; a < buttons.length; a++) {
                    String text;
                    buttons[a] = new BottomSheetCell(context, 0);
                    buttons[a].setPadding(AndroidUtilities.dp(23.0f), 0, AndroidUtilities.dp(23.0f), 0);
                    buttons[a].setTag(Integer.valueOf(a));
                    buttons[a].setBackgroundDrawable(Theme.getSelectorDrawable(false));
                    switch (a) {
                        case 0:
                            text = LocaleController.getString("UserRestrictionsUntilForever", NUM);
                            break;
                        case 1:
                            text = LocaleController.formatPluralString("Days", 1);
                            break;
                        case 2:
                            text = LocaleController.formatPluralString("Weeks", 1);
                            break;
                        case 3:
                            text = LocaleController.formatPluralString("Months", 1);
                            break;
                        default:
                            text = LocaleController.getString("NotificationsCustom", NUM);
                            break;
                    }
                    buttons[a].setTextAndIcon(text, 0);
                    linearLayout.addView(buttons[a], LayoutHelper.createLinear(-1, -2));
                    buttons[a].setOnClickListener(new ChatRightsEditActivity$$Lambda$5(this, builder));
                }
                builder.setCustomView(linearLayout);
                showDialog(builder.create());
            }
        } else if (view instanceof TextCheckCell2) {
            TextCheckCell2 checkCell = (TextCheckCell2) view;
            if (checkCell.hasIcon()) {
                Toast.makeText(getParentActivity(), LocaleController.getString("UserRestrictionsDisabled", NUM), 0).show();
            } else if (checkCell.isEnabled()) {
                checkCell.setChecked(!checkCell.isChecked());
                if (position == this.changeInfoRow) {
                    if (this.currentType == 0) {
                        this.adminRights.change_info = !this.adminRights.change_info;
                    } else {
                        this.bannedRights.change_info = !this.bannedRights.change_info;
                    }
                } else if (position == this.postMessagesRow) {
                    this.adminRights.post_messages = !this.adminRights.post_messages;
                } else if (position == this.editMesagesRow) {
                    this.adminRights.edit_messages = !this.adminRights.edit_messages;
                } else if (position == this.deleteMessagesRow) {
                    this.adminRights.delete_messages = !this.adminRights.delete_messages;
                } else if (position == this.addAdminsRow) {
                    this.adminRights.add_admins = !this.adminRights.add_admins;
                } else if (position == this.banUsersRow) {
                    this.adminRights.ban_users = !this.adminRights.ban_users;
                } else if (position == this.addUsersRow) {
                    if (this.currentType == 0) {
                        this.adminRights.invite_users = !this.adminRights.invite_users;
                    } else {
                        this.bannedRights.invite_users = !this.bannedRights.invite_users;
                    }
                } else if (position == this.pinMessagesRow) {
                    if (this.currentType == 0) {
                        this.adminRights.pin_messages = !this.adminRights.pin_messages;
                    } else {
                        this.bannedRights.pin_messages = !this.bannedRights.pin_messages;
                    }
                } else if (this.bannedRights != null) {
                    TL_chatBannedRights tL_chatBannedRights;
                    TL_chatBannedRights tL_chatBannedRights2;
                    boolean disabled = !checkCell.isChecked();
                    if (position == this.sendMessagesRow) {
                        this.bannedRights.send_messages = !this.bannedRights.send_messages;
                    } else if (position == this.sendMediaRow) {
                        this.bannedRights.send_media = !this.bannedRights.send_media;
                    } else if (position == this.sendStickersRow) {
                        tL_chatBannedRights = this.bannedRights;
                        tL_chatBannedRights2 = this.bannedRights;
                        TL_chatBannedRights tL_chatBannedRights3 = this.bannedRights;
                        TL_chatBannedRights tL_chatBannedRights4 = this.bannedRights;
                        boolean z = !this.bannedRights.send_stickers;
                        tL_chatBannedRights4.send_inline = z;
                        tL_chatBannedRights3.send_gifs = z;
                        tL_chatBannedRights2.send_games = z;
                        tL_chatBannedRights.send_stickers = z;
                    } else if (position == this.embedLinksRow) {
                        this.bannedRights.embed_links = !this.bannedRights.embed_links;
                    } else if (position == this.sendPollsRow) {
                        this.bannedRights.send_polls = !this.bannedRights.send_polls;
                    }
                    ViewHolder holder;
                    if (disabled) {
                        if (this.bannedRights.view_messages && !this.bannedRights.send_messages) {
                            this.bannedRights.send_messages = true;
                            holder = this.listView.findViewHolderForAdapterPosition(this.sendMessagesRow);
                            if (holder != null) {
                                ((TextCheckCell2) holder.itemView).setChecked(false);
                            }
                        }
                        if ((this.bannedRights.view_messages || this.bannedRights.send_messages) && !this.bannedRights.send_media) {
                            this.bannedRights.send_media = true;
                            holder = this.listView.findViewHolderForAdapterPosition(this.sendMediaRow);
                            if (holder != null) {
                                ((TextCheckCell2) holder.itemView).setChecked(false);
                            }
                        }
                        if ((this.bannedRights.view_messages || this.bannedRights.send_messages) && !this.bannedRights.send_polls) {
                            this.bannedRights.send_polls = true;
                            holder = this.listView.findViewHolderForAdapterPosition(this.sendPollsRow);
                            if (holder != null) {
                                ((TextCheckCell2) holder.itemView).setChecked(false);
                            }
                        }
                        if ((this.bannedRights.view_messages || this.bannedRights.send_messages) && !this.bannedRights.send_stickers) {
                            TL_chatBannedRights tL_chatBannedRights5 = this.bannedRights;
                            tL_chatBannedRights = this.bannedRights;
                            tL_chatBannedRights2 = this.bannedRights;
                            this.bannedRights.send_inline = true;
                            tL_chatBannedRights2.send_gifs = true;
                            tL_chatBannedRights.send_games = true;
                            tL_chatBannedRights5.send_stickers = true;
                            holder = this.listView.findViewHolderForAdapterPosition(this.sendStickersRow);
                            if (holder != null) {
                                ((TextCheckCell2) holder.itemView).setChecked(false);
                            }
                        }
                        if ((this.bannedRights.view_messages || this.bannedRights.send_messages) && !this.bannedRights.embed_links) {
                            this.bannedRights.embed_links = true;
                            holder = this.listView.findViewHolderForAdapterPosition(this.embedLinksRow);
                            if (holder != null) {
                                ((TextCheckCell2) holder.itemView).setChecked(false);
                                return;
                            }
                            return;
                        }
                        return;
                    }
                    if (!(this.bannedRights.send_messages && this.bannedRights.embed_links && this.bannedRights.send_inline && this.bannedRights.send_media && this.bannedRights.send_polls) && this.bannedRights.view_messages) {
                        this.bannedRights.view_messages = false;
                    }
                    if (!(this.bannedRights.embed_links && this.bannedRights.send_inline && this.bannedRights.send_media && this.bannedRights.send_polls) && this.bannedRights.send_messages) {
                        this.bannedRights.send_messages = false;
                        holder = this.listView.findViewHolderForAdapterPosition(this.sendMessagesRow);
                        if (holder != null) {
                            ((TextCheckCell2) holder.itemView).setChecked(true);
                        }
                    }
                }
            }
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$5$ChatRightsEditActivity(Builder builder, View v2) {
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
                    DatePickerDialog dialog = new DatePickerDialog(getParentActivity(), new ChatRightsEditActivity$$Lambda$6(this), calendar.get(1), calendar.get(2), calendar.get(5));
                    DatePicker datePicker = dialog.getDatePicker();
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
                    dialog.setButton(-1, LocaleController.getString("Set", NUM), dialog);
                    dialog.setButton(-2, LocaleController.getString("Cancel", NUM), ChatRightsEditActivity$$Lambda$7.$instance);
                    if (VERSION.SDK_INT >= 21) {
                        dialog.setOnShowListener(new ChatRightsEditActivity$$Lambda$8(datePicker));
                    }
                    showDialog(dialog);
                    break;
                } catch (Exception e) {
                    FileLog.e(e);
                    break;
                }
        }
        builder.getDismissRunnable().run();
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$2$ChatRightsEditActivity(DatePicker view1, int year1, int month, int dayOfMonth1) {
        Calendar calendar1 = Calendar.getInstance();
        calendar1.clear();
        calendar1.set(year1, month, dayOfMonth1);
        try {
            TimePickerDialog dialog13 = new TimePickerDialog(getParentActivity(), new ChatRightsEditActivity$$Lambda$9(this, (int) (calendar1.getTime().getTime() / 1000)), 0, 0, true);
            dialog13.setButton(-1, LocaleController.getString("Set", NUM), dialog13);
            dialog13.setButton(-2, LocaleController.getString("Cancel", NUM), ChatRightsEditActivity$$Lambda$10.$instance);
            showDialog(dialog13);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$0$ChatRightsEditActivity(int time, TimePicker view11, int hourOfDay, int minute) {
        this.bannedRights.until_date = ((hourOfDay * 3600) + time) + (minute * 60);
        this.listViewAdapter.notifyItemChanged(this.untilDateRow);
    }

    static final /* synthetic */ void lambda$null$1$ChatRightsEditActivity(DialogInterface dialog131, int which) {
    }

    static final /* synthetic */ void lambda$null$3$ChatRightsEditActivity(DialogInterface dialog1, int which) {
    }

    static final /* synthetic */ void lambda$null$4$ChatRightsEditActivity(DatePicker datePicker, DialogInterface dialog12) {
        int count = datePicker.getChildCount();
        for (int b = 0; b < count; b++) {
            View child = datePicker.getChildAt(b);
            LayoutParams layoutParams = child.getLayoutParams();
            layoutParams.width = -1;
            child.setLayoutParams(layoutParams);
        }
    }

    public void onResume() {
        super.onResume();
        if (this.listViewAdapter != null) {
            this.listViewAdapter.notifyDataSetChanged();
        }
    }

    private boolean isDefaultAdminRights() {
        return (this.adminRights.change_info && this.adminRights.delete_messages && this.adminRights.ban_users && this.adminRights.invite_users && this.adminRights.pin_messages && !this.adminRights.add_admins) || !(this.adminRights.change_info || this.adminRights.delete_messages || this.adminRights.ban_users || this.adminRights.invite_users || this.adminRights.pin_messages || this.adminRights.add_admins);
    }

    private void onDonePressed() {
        if (ChatObject.isChannel(this.currentChat) || (this.currentType != 1 && (this.currentType != 0 || isDefaultAdminRights()))) {
            if (this.currentType == 0) {
                TL_chatAdminRights tL_chatAdminRights;
                if (this.isChannel) {
                    tL_chatAdminRights = this.adminRights;
                    this.adminRights.ban_users = false;
                    tL_chatAdminRights.pin_messages = false;
                } else {
                    tL_chatAdminRights = this.adminRights;
                    this.adminRights.edit_messages = false;
                    tL_chatAdminRights.post_messages = false;
                }
                MessagesController.getInstance(this.currentAccount).setUserAdminRole(this.chatId, this.currentUser, this.adminRights, this.isChannel, getFragmentForAlert(1), this.isAddingNew);
                if (this.delegate != null) {
                    int i;
                    ChatRightsEditActivityDelegate chatRightsEditActivityDelegate = this.delegate;
                    if (this.adminRights.change_info || this.adminRights.post_messages || this.adminRights.edit_messages || this.adminRights.delete_messages || this.adminRights.ban_users || this.adminRights.invite_users || this.adminRights.pin_messages || this.adminRights.add_admins) {
                        i = 1;
                    } else {
                        i = 0;
                    }
                    chatRightsEditActivityDelegate.didSetRights(i, this.adminRights, this.bannedRights);
                }
            } else if (this.currentType == 1) {
                int rights;
                MessagesController.getInstance(this.currentAccount).setUserBannedRole(this.chatId, this.currentUser, this.bannedRights, this.isChannel, getFragmentForAlert(1));
                if (this.bannedRights.send_messages || this.bannedRights.send_stickers || this.bannedRights.embed_links || this.bannedRights.send_media || this.bannedRights.send_gifs || this.bannedRights.send_games || this.bannedRights.send_inline) {
                    rights = 1;
                } else {
                    this.bannedRights.until_date = 0;
                    rights = 2;
                }
                if (this.delegate != null) {
                    this.delegate.didSetRights(rights, this.adminRights, this.bannedRights);
                }
            }
            finishFragment();
            return;
        }
        MessagesController.getInstance(this.currentAccount).convertToMegaGroup(getParentActivity(), this.chatId, new ChatRightsEditActivity$$Lambda$1(this));
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$onDonePressed$7$ChatRightsEditActivity(int param) {
        this.chatId = param;
        this.currentChat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(param));
        onDonePressed();
    }

    public void setDelegate(ChatRightsEditActivityDelegate channelRightsEditActivityDelegate) {
        this.delegate = channelRightsEditActivityDelegate;
    }

    private boolean checkDiscard() {
        if (this.currentType != 1) {
            return true;
        }
        if (this.currentBannedRights.equals(ChatObject.getBannedRightsString(this.bannedRights))) {
            return true;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setTitle(LocaleController.getString("UserRestrictionsApplyChanges", NUM));
        Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.chatId));
        builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("UserRestrictionsApplyChangesText", NUM, chat.title)));
        builder.setPositiveButton(LocaleController.getString("ApplyTheme", NUM), new ChatRightsEditActivity$$Lambda$2(this));
        builder.setNegativeButton(LocaleController.getString("PassportDiscard", NUM), new ChatRightsEditActivity$$Lambda$3(this));
        showDialog(builder.create());
        return false;
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$checkDiscard$8$ChatRightsEditActivity(DialogInterface dialogInterface, int i) {
        onDonePressed();
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$checkDiscard$9$ChatRightsEditActivity(DialogInterface dialog, int which) {
        finishFragment();
    }

    public boolean onBackPressed() {
        return checkDiscard();
    }

    public ThemeDescription[] getThemeDescriptions() {
        ThemeDescriptionDelegate cellDelegate = new ChatRightsEditActivity$$Lambda$4(this);
        r10 = new ThemeDescription[38];
        r10[0] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{UserCell.class, TextSettingsCell.class, TextCheckCell2.class, HeaderCell.class, TextDetailCell.class}, null, null, null, "windowBackgroundWhite");
        r10[1] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundGray");
        r10[2] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault");
        r10[3] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault");
        r10[4] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon");
        r10[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle");
        r10[6] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector");
        r10[7] = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21");
        r10[8] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, "divider");
        r10[9] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, "windowBackgroundGrayShadow");
        r10[10] = new ThemeDescription(this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteGrayText4");
        r10[11] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteRedText5");
        r10[12] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText");
        r10[13] = new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, null, null, null, "windowBackgroundWhiteValueText");
        r10[14] = new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueImageView"}, null, null, null, "windowBackgroundWhiteGrayIcon");
        r10[15] = new ThemeDescription(this.listView, 0, new Class[]{TextDetailCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText");
        r10[16] = new ThemeDescription(this.listView, 0, new Class[]{TextDetailCell.class}, new String[]{"valueTextView"}, null, null, null, "windowBackgroundWhiteGrayText2");
        r10[17] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell2.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText");
        r10[18] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell2.class}, new String[]{"valueTextView"}, null, null, null, "windowBackgroundWhiteGrayText2");
        r10[19] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell2.class}, new String[]{"checkBox"}, null, null, null, "switch2Track");
        r10[20] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell2.class}, new String[]{"checkBox"}, null, null, null, "switch2TrackChecked");
        r10[21] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, "windowBackgroundGrayShadow");
        r10[22] = new ThemeDescription(this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlueHeader");
        r10[23] = new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, new String[]{"nameTextView"}, null, null, null, "windowBackgroundWhiteBlackText");
        r10[24] = new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, new String[]{"statusColor"}, null, null, cellDelegate, "windowBackgroundWhiteGrayText");
        r10[25] = new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, new String[]{"statusOnlineColor"}, null, null, cellDelegate, "windowBackgroundWhiteBlueText");
        r10[26] = new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, null, new Drawable[]{Theme.avatar_broadcastDrawable, Theme.avatar_savedDrawable}, null, "avatar_text");
        r10[27] = new ThemeDescription(null, 0, null, null, null, cellDelegate, "avatar_backgroundRed");
        r10[28] = new ThemeDescription(null, 0, null, null, null, cellDelegate, "avatar_backgroundOrange");
        r10[29] = new ThemeDescription(null, 0, null, null, null, cellDelegate, "avatar_backgroundViolet");
        r10[30] = new ThemeDescription(null, 0, null, null, null, cellDelegate, "avatar_backgroundGreen");
        r10[31] = new ThemeDescription(null, 0, null, null, null, cellDelegate, "avatar_backgroundCyan");
        r10[32] = new ThemeDescription(null, 0, null, null, null, cellDelegate, "avatar_backgroundBlue");
        r10[33] = new ThemeDescription(null, 0, null, null, null, cellDelegate, "avatar_backgroundPink");
        r10[34] = new ThemeDescription(null, 0, new Class[]{DialogRadioCell.class}, new String[]{"textView"}, null, null, null, "dialogTextBlack");
        r10[35] = new ThemeDescription(null, 0, new Class[]{DialogRadioCell.class}, new String[]{"textView"}, null, null, null, "dialogTextGray2");
        r10[36] = new ThemeDescription(null, ThemeDescription.FLAG_CHECKBOX, new Class[]{DialogRadioCell.class}, new String[]{"radioButton"}, null, null, null, "dialogRadioBackground");
        r10[37] = new ThemeDescription(null, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{DialogRadioCell.class}, new String[]{"radioButton"}, null, null, null, "dialogRadioBackgroundChecked");
        return r10;
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$getThemeDescriptions$10$ChatRightsEditActivity() {
        if (this.listView != null) {
            int count = this.listView.getChildCount();
            for (int a = 0; a < count; a++) {
                View child = this.listView.getChildAt(a);
                if (child instanceof UserCell) {
                    ((UserCell) child).update(0);
                }
            }
        }
    }
}
