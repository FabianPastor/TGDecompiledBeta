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

        public boolean isEnabled(ViewHolder viewHolder) {
            boolean z = false;
            if (!ChatRightsEditActivity.this.canEdit) {
                return false;
            }
            int itemViewType = viewHolder.getItemViewType();
            if (ChatRightsEditActivity.this.currentType == 0 && itemViewType == 4) {
                int adapterPosition = viewHolder.getAdapterPosition();
                if (adapterPosition == ChatRightsEditActivity.this.changeInfoRow) {
                    return ChatRightsEditActivity.this.myAdminRights.change_info;
                }
                if (adapterPosition == ChatRightsEditActivity.this.postMessagesRow) {
                    return ChatRightsEditActivity.this.myAdminRights.post_messages;
                }
                if (adapterPosition == ChatRightsEditActivity.this.editMesagesRow) {
                    return ChatRightsEditActivity.this.myAdminRights.edit_messages;
                }
                if (adapterPosition == ChatRightsEditActivity.this.deleteMessagesRow) {
                    return ChatRightsEditActivity.this.myAdminRights.delete_messages;
                }
                if (adapterPosition == ChatRightsEditActivity.this.addAdminsRow) {
                    return ChatRightsEditActivity.this.myAdminRights.add_admins;
                }
                if (adapterPosition == ChatRightsEditActivity.this.banUsersRow) {
                    return ChatRightsEditActivity.this.myAdminRights.ban_users;
                }
                if (adapterPosition == ChatRightsEditActivity.this.addUsersRow) {
                    return ChatRightsEditActivity.this.myAdminRights.invite_users;
                }
                if (adapterPosition == ChatRightsEditActivity.this.pinMessagesRow) {
                    return ChatRightsEditActivity.this.myAdminRights.pin_messages;
                }
            }
            if (!(itemViewType == 3 || itemViewType == 1 || itemViewType == 5)) {
                z = true;
            }
            return z;
        }

        public int getItemCount() {
            return ChatRightsEditActivity.this.rowCount;
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view;
            String str = "windowBackgroundWhite";
            if (i == 0) {
                View userCell = new UserCell(this.mContext, 4, 0);
                userCell.setBackgroundColor(Theme.getColor(str));
                view = userCell;
            } else if (i == 1) {
                view = new TextInfoPrivacyCell(this.mContext);
                view.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
            } else if (i == 2) {
                view = new TextSettingsCell(this.mContext);
                view.setBackgroundColor(Theme.getColor(str));
            } else if (i == 3) {
                view = new HeaderCell(this.mContext);
                view.setBackgroundColor(Theme.getColor(str));
            } else if (i == 4) {
                view = new TextCheckCell2(this.mContext);
                view.setBackgroundColor(Theme.getColor(str));
            } else if (i != 5) {
                view = new TextDetailCell(this.mContext);
                view.setBackgroundColor(Theme.getColor(str));
            } else {
                view = new ShadowSectionCell(this.mContext);
            }
            return new Holder(view);
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            boolean z = false;
            String str;
            switch (viewHolder.getItemViewType()) {
                case 0:
                    ((UserCell) viewHolder.itemView).setData(ChatRightsEditActivity.this.currentUser, null, null, 0);
                    return;
                case 1:
                    TextInfoPrivacyCell textInfoPrivacyCell = (TextInfoPrivacyCell) viewHolder.itemView;
                    if (i == ChatRightsEditActivity.this.cantEditInfoRow) {
                        textInfoPrivacyCell.setText(LocaleController.getString("EditAdminCantEdit", NUM));
                        return;
                    }
                    return;
                case 2:
                    TextSettingsCell textSettingsCell = (TextSettingsCell) viewHolder.itemView;
                    if (i == ChatRightsEditActivity.this.removeAdminRow) {
                        str = "windowBackgroundWhiteRedText5";
                        textSettingsCell.setTextColor(Theme.getColor(str));
                        textSettingsCell.setTag(str);
                        if (ChatRightsEditActivity.this.currentType == 0) {
                            textSettingsCell.setText(LocaleController.getString("EditAdminRemoveAdmin", NUM), false);
                            return;
                        } else if (ChatRightsEditActivity.this.currentType == 1) {
                            textSettingsCell.setText(LocaleController.getString("UserRestrictionsBlock", NUM), false);
                            return;
                        } else {
                            return;
                        }
                    }
                    return;
                case 3:
                    HeaderCell headerCell = (HeaderCell) viewHolder.itemView;
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
                    TextCheckCell2 textCheckCell2 = (TextCheckCell2) viewHolder.itemView;
                    int i2 = NUM;
                    String string;
                    boolean z2;
                    if (i == ChatRightsEditActivity.this.changeInfoRow) {
                        if (ChatRightsEditActivity.this.currentType == 0) {
                            if (ChatRightsEditActivity.this.isChannel) {
                                textCheckCell2.setTextAndCheck(LocaleController.getString("EditAdminChangeChannelInfo", NUM), ChatRightsEditActivity.this.adminRights.change_info, true);
                            } else {
                                textCheckCell2.setTextAndCheck(LocaleController.getString("EditAdminChangeGroupInfo", NUM), ChatRightsEditActivity.this.adminRights.change_info, true);
                            }
                        } else if (ChatRightsEditActivity.this.currentType == 1) {
                            string = LocaleController.getString("UserRestrictionsChangeInfo", NUM);
                            z2 = (ChatRightsEditActivity.this.bannedRights.change_info || ChatRightsEditActivity.this.defaultBannedRights.change_info) ? false : true;
                            textCheckCell2.setTextAndCheck(string, z2, false);
                            if (!ChatRightsEditActivity.this.defaultBannedRights.change_info) {
                                i2 = 0;
                            }
                            textCheckCell2.setIcon(i2);
                        }
                    } else if (i == ChatRightsEditActivity.this.postMessagesRow) {
                        textCheckCell2.setTextAndCheck(LocaleController.getString("EditAdminPostMessages", NUM), ChatRightsEditActivity.this.adminRights.post_messages, true);
                    } else if (i == ChatRightsEditActivity.this.editMesagesRow) {
                        textCheckCell2.setTextAndCheck(LocaleController.getString("EditAdminEditMessages", NUM), ChatRightsEditActivity.this.adminRights.edit_messages, true);
                    } else if (i == ChatRightsEditActivity.this.deleteMessagesRow) {
                        if (ChatRightsEditActivity.this.isChannel) {
                            textCheckCell2.setTextAndCheck(LocaleController.getString("EditAdminDeleteMessages", NUM), ChatRightsEditActivity.this.adminRights.delete_messages, true);
                        } else {
                            textCheckCell2.setTextAndCheck(LocaleController.getString("EditAdminGroupDeleteMessages", NUM), ChatRightsEditActivity.this.adminRights.delete_messages, true);
                        }
                    } else if (i == ChatRightsEditActivity.this.addAdminsRow) {
                        textCheckCell2.setTextAndCheck(LocaleController.getString("EditAdminAddAdmins", NUM), ChatRightsEditActivity.this.adminRights.add_admins, false);
                    } else if (i == ChatRightsEditActivity.this.banUsersRow) {
                        textCheckCell2.setTextAndCheck(LocaleController.getString("EditAdminBanUsers", NUM), ChatRightsEditActivity.this.adminRights.ban_users, true);
                    } else if (i == ChatRightsEditActivity.this.addUsersRow) {
                        if (ChatRightsEditActivity.this.currentType == 0) {
                            if (ChatObject.isActionBannedByDefault(ChatRightsEditActivity.this.currentChat, 3)) {
                                textCheckCell2.setTextAndCheck(LocaleController.getString("EditAdminAddUsers", NUM), ChatRightsEditActivity.this.adminRights.invite_users, true);
                            } else {
                                textCheckCell2.setTextAndCheck(LocaleController.getString("EditAdminAddUsersViaLink", NUM), ChatRightsEditActivity.this.adminRights.invite_users, true);
                            }
                        } else if (ChatRightsEditActivity.this.currentType == 1) {
                            string = LocaleController.getString("UserRestrictionsInviteUsers", NUM);
                            z2 = (ChatRightsEditActivity.this.bannedRights.invite_users || ChatRightsEditActivity.this.defaultBannedRights.invite_users) ? false : true;
                            textCheckCell2.setTextAndCheck(string, z2, true);
                            if (!ChatRightsEditActivity.this.defaultBannedRights.invite_users) {
                                i2 = 0;
                            }
                            textCheckCell2.setIcon(i2);
                        }
                    } else if (i == ChatRightsEditActivity.this.pinMessagesRow) {
                        if (ChatRightsEditActivity.this.currentType == 0) {
                            textCheckCell2.setTextAndCheck(LocaleController.getString("EditAdminPinMessages", NUM), ChatRightsEditActivity.this.adminRights.pin_messages, true);
                        } else if (ChatRightsEditActivity.this.currentType == 1) {
                            string = LocaleController.getString("UserRestrictionsPinMessages", NUM);
                            z2 = (ChatRightsEditActivity.this.bannedRights.pin_messages || ChatRightsEditActivity.this.defaultBannedRights.pin_messages) ? false : true;
                            textCheckCell2.setTextAndCheck(string, z2, true);
                            if (!ChatRightsEditActivity.this.defaultBannedRights.pin_messages) {
                                i2 = 0;
                            }
                            textCheckCell2.setIcon(i2);
                        }
                    } else if (i == ChatRightsEditActivity.this.sendMessagesRow) {
                        string = LocaleController.getString("UserRestrictionsSend", NUM);
                        z2 = (ChatRightsEditActivity.this.bannedRights.send_messages || ChatRightsEditActivity.this.defaultBannedRights.send_messages) ? false : true;
                        textCheckCell2.setTextAndCheck(string, z2, true);
                        if (!ChatRightsEditActivity.this.defaultBannedRights.send_messages) {
                            i2 = 0;
                        }
                        textCheckCell2.setIcon(i2);
                    } else if (i == ChatRightsEditActivity.this.sendMediaRow) {
                        string = LocaleController.getString("UserRestrictionsSendMedia", NUM);
                        z2 = (ChatRightsEditActivity.this.bannedRights.send_media || ChatRightsEditActivity.this.defaultBannedRights.send_media) ? false : true;
                        textCheckCell2.setTextAndCheck(string, z2, true);
                        if (!ChatRightsEditActivity.this.defaultBannedRights.send_media) {
                            i2 = 0;
                        }
                        textCheckCell2.setIcon(i2);
                    } else if (i == ChatRightsEditActivity.this.sendStickersRow) {
                        string = LocaleController.getString("UserRestrictionsSendStickers", NUM);
                        z2 = (ChatRightsEditActivity.this.bannedRights.send_stickers || ChatRightsEditActivity.this.defaultBannedRights.send_stickers) ? false : true;
                        textCheckCell2.setTextAndCheck(string, z2, true);
                        if (!ChatRightsEditActivity.this.defaultBannedRights.send_stickers) {
                            i2 = 0;
                        }
                        textCheckCell2.setIcon(i2);
                    } else if (i == ChatRightsEditActivity.this.embedLinksRow) {
                        string = LocaleController.getString("UserRestrictionsEmbedLinks", NUM);
                        z2 = (ChatRightsEditActivity.this.bannedRights.embed_links || ChatRightsEditActivity.this.defaultBannedRights.embed_links) ? false : true;
                        textCheckCell2.setTextAndCheck(string, z2, true);
                        if (!ChatRightsEditActivity.this.defaultBannedRights.embed_links) {
                            i2 = 0;
                        }
                        textCheckCell2.setIcon(i2);
                    } else if (i == ChatRightsEditActivity.this.sendPollsRow) {
                        string = LocaleController.getString("UserRestrictionsSendPolls", NUM);
                        z2 = (ChatRightsEditActivity.this.bannedRights.send_polls || ChatRightsEditActivity.this.defaultBannedRights.send_polls) ? false : true;
                        textCheckCell2.setTextAndCheck(string, z2, true);
                        if (!ChatRightsEditActivity.this.defaultBannedRights.send_polls) {
                            i2 = 0;
                        }
                        textCheckCell2.setIcon(i2);
                    }
                    if (i == ChatRightsEditActivity.this.sendMediaRow || i == ChatRightsEditActivity.this.sendStickersRow || i == ChatRightsEditActivity.this.embedLinksRow || i == ChatRightsEditActivity.this.sendPollsRow) {
                        if (!(ChatRightsEditActivity.this.bannedRights.send_messages || ChatRightsEditActivity.this.bannedRights.view_messages || ChatRightsEditActivity.this.defaultBannedRights.send_messages || ChatRightsEditActivity.this.defaultBannedRights.view_messages)) {
                            z = true;
                        }
                        textCheckCell2.setEnabled(z);
                        return;
                    } else if (i == ChatRightsEditActivity.this.sendMessagesRow) {
                        if (!(ChatRightsEditActivity.this.bannedRights.view_messages || ChatRightsEditActivity.this.defaultBannedRights.view_messages)) {
                            z = true;
                        }
                        textCheckCell2.setEnabled(z);
                        return;
                    } else {
                        return;
                    }
                case 5:
                    ShadowSectionCell shadowSectionCell = (ShadowSectionCell) viewHolder.itemView;
                    int i3 = NUM;
                    String str2 = "windowBackgroundGrayShadow";
                    if (i == ChatRightsEditActivity.this.rightsShadowRow) {
                        Context context = this.mContext;
                        if (ChatRightsEditActivity.this.removeAdminRow != -1) {
                            i3 = NUM;
                        }
                        shadowSectionCell.setBackgroundDrawable(Theme.getThemedDrawable(context, i3, str2));
                        return;
                    } else if (i == ChatRightsEditActivity.this.removeAdminShadowRow) {
                        shadowSectionCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, str2));
                        return;
                    } else {
                        shadowSectionCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, str2));
                        return;
                    }
                case 6:
                    TextDetailCell textDetailCell = (TextDetailCell) viewHolder.itemView;
                    if (i == ChatRightsEditActivity.this.untilDateRow) {
                        if (ChatRightsEditActivity.this.bannedRights.until_date == 0 || Math.abs(((long) ChatRightsEditActivity.this.bannedRights.until_date) - (System.currentTimeMillis() / 1000)) > NUM) {
                            str = LocaleController.getString("UserRestrictionsUntilForever", NUM);
                        } else {
                            str = LocaleController.formatDateForBan((long) ChatRightsEditActivity.this.bannedRights.until_date);
                        }
                        textDetailCell.setTextAndValue(LocaleController.getString("UserRestrictionsDuration", NUM), str, false);
                        return;
                    }
                    return;
                default:
                    return;
            }
        }

        public int getItemViewType(int i) {
            if (i == 0) {
                return 0;
            }
            if (i == 1 || i == ChatRightsEditActivity.this.rightsShadowRow || i == ChatRightsEditActivity.this.removeAdminShadowRow || i == ChatRightsEditActivity.this.untilSectionRow) {
                return 5;
            }
            if (i == 2) {
                return 3;
            }
            if (i == ChatRightsEditActivity.this.changeInfoRow || i == ChatRightsEditActivity.this.postMessagesRow || i == ChatRightsEditActivity.this.editMesagesRow || i == ChatRightsEditActivity.this.deleteMessagesRow || i == ChatRightsEditActivity.this.addAdminsRow || i == ChatRightsEditActivity.this.banUsersRow || i == ChatRightsEditActivity.this.addUsersRow || i == ChatRightsEditActivity.this.pinMessagesRow || i == ChatRightsEditActivity.this.sendMessagesRow || i == ChatRightsEditActivity.this.sendMediaRow || i == ChatRightsEditActivity.this.sendStickersRow || i == ChatRightsEditActivity.this.embedLinksRow || i == ChatRightsEditActivity.this.sendPollsRow) {
                return 4;
            }
            if (i == ChatRightsEditActivity.this.cantEditInfoRow) {
                return 1;
            }
            if (i == ChatRightsEditActivity.this.untilDateRow) {
                return 6;
            }
            return 2;
        }
    }

    static /* synthetic */ void lambda$null$1(DialogInterface dialogInterface, int i) {
    }

    static /* synthetic */ void lambda$null$3(DialogInterface dialogInterface, int i) {
    }

    /* JADX WARNING: Removed duplicated region for block: B:86:0x0255  */
    /* JADX WARNING: Removed duplicated region for block: B:82:0x01ee  */
    /* JADX WARNING: Missing block: B:31:0x00d8, code skipped:
            if (r2.add_admins == false) goto L_0x01e6;
     */
    /* JADX WARNING: Missing block: B:79:0x01e2, code skipped:
            if (r6.view_messages != false) goto L_0x01e6;
     */
    public ChatRightsEditActivity(int r2, int r3, org.telegram.tgnet.TLRPC.TL_chatAdminRights r4, org.telegram.tgnet.TLRPC.TL_chatBannedRights r5, org.telegram.tgnet.TLRPC.TL_chatBannedRights r6, int r7, boolean r8, boolean r9) {
        /*
        r1 = this;
        r1.<init>();
        r0 = "";
        r1.currentBannedRights = r0;
        r1.isAddingNew = r9;
        r1.chatId = r3;
        r3 = r1.currentAccount;
        r3 = org.telegram.messenger.MessagesController.getInstance(r3);
        r2 = java.lang.Integer.valueOf(r2);
        r2 = r3.getUser(r2);
        r1.currentUser = r2;
        r1.currentType = r7;
        r1.canEdit = r8;
        r2 = r1.currentAccount;
        r2 = org.telegram.messenger.MessagesController.getInstance(r2);
        r3 = r1.chatId;
        r3 = java.lang.Integer.valueOf(r3);
        r2 = r2.getChat(r3);
        r1.currentChat = r2;
        r2 = r1.currentChat;
        r3 = 0;
        r8 = 1;
        if (r2 == 0) goto L_0x004e;
    L_0x0037:
        r2 = org.telegram.messenger.ChatObject.isChannel(r2);
        if (r2 == 0) goto L_0x0045;
    L_0x003d:
        r2 = r1.currentChat;
        r2 = r2.megagroup;
        if (r2 != 0) goto L_0x0045;
    L_0x0043:
        r2 = 1;
        goto L_0x0046;
    L_0x0045:
        r2 = 0;
    L_0x0046:
        r1.isChannel = r2;
        r2 = r1.currentChat;
        r2 = r2.admin_rights;
        r1.myAdminRights = r2;
    L_0x004e:
        r2 = r1.myAdminRights;
        if (r2 != 0) goto L_0x006b;
    L_0x0052:
        r2 = new org.telegram.tgnet.TLRPC$TL_chatAdminRights;
        r2.<init>();
        r1.myAdminRights = r2;
        r2 = r1.myAdminRights;
        r2.add_admins = r8;
        r2.pin_messages = r8;
        r2.invite_users = r8;
        r2.ban_users = r8;
        r2.delete_messages = r8;
        r2.edit_messages = r8;
        r2.post_messages = r8;
        r2.change_info = r8;
    L_0x006b:
        if (r7 != 0) goto L_0x00dd;
    L_0x006d:
        r2 = new org.telegram.tgnet.TLRPC$TL_chatAdminRights;
        r2.<init>();
        r1.adminRights = r2;
        if (r4 != 0) goto L_0x0098;
    L_0x0076:
        r2 = r1.adminRights;
        r4 = r1.myAdminRights;
        r5 = r4.change_info;
        r2.change_info = r5;
        r5 = r4.post_messages;
        r2.post_messages = r5;
        r5 = r4.edit_messages;
        r2.edit_messages = r5;
        r5 = r4.delete_messages;
        r2.delete_messages = r5;
        r5 = r4.ban_users;
        r2.ban_users = r5;
        r5 = r4.invite_users;
        r2.invite_users = r5;
        r4 = r4.pin_messages;
        r2.pin_messages = r4;
        goto L_0x01e6;
    L_0x0098:
        r2 = r1.adminRights;
        r5 = r4.change_info;
        r2.change_info = r5;
        r5 = r4.post_messages;
        r2.post_messages = r5;
        r5 = r4.edit_messages;
        r2.edit_messages = r5;
        r5 = r4.delete_messages;
        r2.delete_messages = r5;
        r5 = r4.ban_users;
        r2.ban_users = r5;
        r5 = r4.invite_users;
        r2.invite_users = r5;
        r5 = r4.pin_messages;
        r2.pin_messages = r5;
        r4 = r4.add_admins;
        r2.add_admins = r4;
        r4 = r2.change_info;
        if (r4 != 0) goto L_0x00da;
    L_0x00be:
        r4 = r2.post_messages;
        if (r4 != 0) goto L_0x00da;
    L_0x00c2:
        r4 = r2.edit_messages;
        if (r4 != 0) goto L_0x00da;
    L_0x00c6:
        r4 = r2.delete_messages;
        if (r4 != 0) goto L_0x00da;
    L_0x00ca:
        r4 = r2.ban_users;
        if (r4 != 0) goto L_0x00da;
    L_0x00ce:
        r4 = r2.invite_users;
        if (r4 != 0) goto L_0x00da;
    L_0x00d2:
        r4 = r2.pin_messages;
        if (r4 != 0) goto L_0x00da;
    L_0x00d6:
        r2 = r2.add_admins;
        if (r2 == 0) goto L_0x01e6;
    L_0x00da:
        r3 = 1;
        goto L_0x01e6;
    L_0x00dd:
        r1.defaultBannedRights = r5;
        r2 = r1.defaultBannedRights;
        if (r2 != 0) goto L_0x0104;
    L_0x00e3:
        r2 = new org.telegram.tgnet.TLRPC$TL_chatBannedRights;
        r2.<init>();
        r1.defaultBannedRights = r2;
        r2 = r1.defaultBannedRights;
        r2.pin_messages = r3;
        r2.change_info = r3;
        r2.invite_users = r3;
        r2.send_polls = r3;
        r2.send_inline = r3;
        r2.send_games = r3;
        r2.send_gifs = r3;
        r2.send_stickers = r3;
        r2.embed_links = r3;
        r2.send_messages = r3;
        r2.send_media = r3;
        r2.view_messages = r3;
    L_0x0104:
        r2 = new org.telegram.tgnet.TLRPC$TL_chatBannedRights;
        r2.<init>();
        r1.bannedRights = r2;
        if (r6 != 0) goto L_0x0128;
    L_0x010d:
        r2 = r1.bannedRights;
        r2.pin_messages = r3;
        r2.change_info = r3;
        r2.invite_users = r3;
        r2.send_polls = r3;
        r2.send_inline = r3;
        r2.send_games = r3;
        r2.send_gifs = r3;
        r2.send_stickers = r3;
        r2.embed_links = r3;
        r2.send_messages = r3;
        r2.send_media = r3;
        r2.view_messages = r3;
        goto L_0x015e;
    L_0x0128:
        r2 = r1.bannedRights;
        r4 = r6.view_messages;
        r2.view_messages = r4;
        r4 = r6.send_messages;
        r2.send_messages = r4;
        r4 = r6.send_media;
        r2.send_media = r4;
        r4 = r6.send_stickers;
        r2.send_stickers = r4;
        r4 = r6.send_gifs;
        r2.send_gifs = r4;
        r4 = r6.send_games;
        r2.send_games = r4;
        r4 = r6.send_inline;
        r2.send_inline = r4;
        r4 = r6.embed_links;
        r2.embed_links = r4;
        r4 = r6.send_polls;
        r2.send_polls = r4;
        r4 = r6.invite_users;
        r2.invite_users = r4;
        r4 = r6.change_info;
        r2.change_info = r4;
        r4 = r6.pin_messages;
        r2.pin_messages = r4;
        r4 = r6.until_date;
        r2.until_date = r4;
    L_0x015e:
        r2 = r1.defaultBannedRights;
        r2 = r2.view_messages;
        if (r2 == 0) goto L_0x0168;
    L_0x0164:
        r2 = r1.bannedRights;
        r2.view_messages = r8;
    L_0x0168:
        r2 = r1.defaultBannedRights;
        r2 = r2.send_messages;
        if (r2 == 0) goto L_0x0172;
    L_0x016e:
        r2 = r1.bannedRights;
        r2.send_messages = r8;
    L_0x0172:
        r2 = r1.defaultBannedRights;
        r2 = r2.send_media;
        if (r2 == 0) goto L_0x017c;
    L_0x0178:
        r2 = r1.bannedRights;
        r2.send_media = r8;
    L_0x017c:
        r2 = r1.defaultBannedRights;
        r2 = r2.send_stickers;
        if (r2 == 0) goto L_0x0186;
    L_0x0182:
        r2 = r1.bannedRights;
        r2.send_stickers = r8;
    L_0x0186:
        r2 = r1.defaultBannedRights;
        r2 = r2.send_gifs;
        if (r2 == 0) goto L_0x0190;
    L_0x018c:
        r2 = r1.bannedRights;
        r2.send_gifs = r8;
    L_0x0190:
        r2 = r1.defaultBannedRights;
        r2 = r2.send_games;
        if (r2 == 0) goto L_0x019a;
    L_0x0196:
        r2 = r1.bannedRights;
        r2.send_games = r8;
    L_0x019a:
        r2 = r1.defaultBannedRights;
        r2 = r2.send_inline;
        if (r2 == 0) goto L_0x01a4;
    L_0x01a0:
        r2 = r1.bannedRights;
        r2.send_inline = r8;
    L_0x01a4:
        r2 = r1.defaultBannedRights;
        r2 = r2.embed_links;
        if (r2 == 0) goto L_0x01ae;
    L_0x01aa:
        r2 = r1.bannedRights;
        r2.embed_links = r8;
    L_0x01ae:
        r2 = r1.defaultBannedRights;
        r2 = r2.send_polls;
        if (r2 == 0) goto L_0x01b8;
    L_0x01b4:
        r2 = r1.bannedRights;
        r2.send_polls = r8;
    L_0x01b8:
        r2 = r1.defaultBannedRights;
        r2 = r2.invite_users;
        if (r2 == 0) goto L_0x01c2;
    L_0x01be:
        r2 = r1.bannedRights;
        r2.invite_users = r8;
    L_0x01c2:
        r2 = r1.defaultBannedRights;
        r2 = r2.change_info;
        if (r2 == 0) goto L_0x01cc;
    L_0x01c8:
        r2 = r1.bannedRights;
        r2.change_info = r8;
    L_0x01cc:
        r2 = r1.defaultBannedRights;
        r2 = r2.pin_messages;
        if (r2 == 0) goto L_0x01d6;
    L_0x01d2:
        r2 = r1.bannedRights;
        r2.pin_messages = r8;
    L_0x01d6:
        r2 = r1.bannedRights;
        r2 = org.telegram.messenger.ChatObject.getBannedRightsString(r2);
        r1.currentBannedRights = r2;
        if (r6 == 0) goto L_0x00da;
    L_0x01e0:
        r2 = r6.view_messages;
        if (r2 != 0) goto L_0x01e6;
    L_0x01e4:
        goto L_0x00da;
    L_0x01e6:
        r2 = r1.rowCount;
        r2 = r2 + 3;
        r1.rowCount = r2;
        if (r7 != 0) goto L_0x0255;
    L_0x01ee:
        r2 = r1.isChannel;
        if (r2 == 0) goto L_0x0224;
    L_0x01f2:
        r2 = r1.rowCount;
        r4 = r2 + 1;
        r1.rowCount = r4;
        r1.changeInfoRow = r2;
        r2 = r1.rowCount;
        r4 = r2 + 1;
        r1.rowCount = r4;
        r1.postMessagesRow = r2;
        r2 = r1.rowCount;
        r4 = r2 + 1;
        r1.rowCount = r4;
        r1.editMesagesRow = r2;
        r2 = r1.rowCount;
        r4 = r2 + 1;
        r1.rowCount = r4;
        r1.deleteMessagesRow = r2;
        r2 = r1.rowCount;
        r4 = r2 + 1;
        r1.rowCount = r4;
        r1.addUsersRow = r2;
        r2 = r1.rowCount;
        r4 = r2 + 1;
        r1.rowCount = r4;
        r1.addAdminsRow = r2;
        goto L_0x02a7;
    L_0x0224:
        r2 = r1.rowCount;
        r4 = r2 + 1;
        r1.rowCount = r4;
        r1.changeInfoRow = r2;
        r2 = r1.rowCount;
        r4 = r2 + 1;
        r1.rowCount = r4;
        r1.deleteMessagesRow = r2;
        r2 = r1.rowCount;
        r4 = r2 + 1;
        r1.rowCount = r4;
        r1.banUsersRow = r2;
        r2 = r1.rowCount;
        r4 = r2 + 1;
        r1.rowCount = r4;
        r1.addUsersRow = r2;
        r2 = r1.rowCount;
        r4 = r2 + 1;
        r1.rowCount = r4;
        r1.pinMessagesRow = r2;
        r2 = r1.rowCount;
        r4 = r2 + 1;
        r1.rowCount = r4;
        r1.addAdminsRow = r2;
        goto L_0x02a7;
    L_0x0255:
        if (r7 != r8) goto L_0x02a7;
    L_0x0257:
        r2 = r1.rowCount;
        r4 = r2 + 1;
        r1.rowCount = r4;
        r1.sendMessagesRow = r2;
        r2 = r1.rowCount;
        r4 = r2 + 1;
        r1.rowCount = r4;
        r1.sendMediaRow = r2;
        r2 = r1.rowCount;
        r4 = r2 + 1;
        r1.rowCount = r4;
        r1.sendStickersRow = r2;
        r2 = r1.rowCount;
        r4 = r2 + 1;
        r1.rowCount = r4;
        r1.sendPollsRow = r2;
        r2 = r1.rowCount;
        r4 = r2 + 1;
        r1.rowCount = r4;
        r1.embedLinksRow = r2;
        r2 = r1.rowCount;
        r4 = r2 + 1;
        r1.rowCount = r4;
        r1.addUsersRow = r2;
        r2 = r1.rowCount;
        r4 = r2 + 1;
        r1.rowCount = r4;
        r1.pinMessagesRow = r2;
        r2 = r1.rowCount;
        r4 = r2 + 1;
        r1.rowCount = r4;
        r1.changeInfoRow = r2;
        r2 = r1.rowCount;
        r4 = r2 + 1;
        r1.rowCount = r4;
        r1.untilSectionRow = r2;
        r2 = r1.rowCount;
        r4 = r2 + 1;
        r1.rowCount = r4;
        r1.untilDateRow = r2;
    L_0x02a7:
        r2 = r1.canEdit;
        r4 = -1;
        if (r2 == 0) goto L_0x02c9;
    L_0x02ac:
        if (r3 == 0) goto L_0x02c9;
    L_0x02ae:
        r2 = r1.rowCount;
        r3 = r2 + 1;
        r1.rowCount = r3;
        r1.rightsShadowRow = r2;
        r2 = r1.rowCount;
        r3 = r2 + 1;
        r1.rowCount = r3;
        r1.removeAdminRow = r2;
        r2 = r1.rowCount;
        r3 = r2 + 1;
        r1.rowCount = r3;
        r1.removeAdminShadowRow = r2;
        r1.cantEditInfoRow = r4;
        goto L_0x02e6;
    L_0x02c9:
        r1.removeAdminRow = r4;
        r1.removeAdminShadowRow = r4;
        if (r7 != 0) goto L_0x02de;
    L_0x02cf:
        r2 = r1.canEdit;
        if (r2 != 0) goto L_0x02de;
    L_0x02d3:
        r1.rightsShadowRow = r4;
        r2 = r1.rowCount;
        r3 = r2 + 1;
        r1.rowCount = r3;
        r1.cantEditInfoRow = r2;
        goto L_0x02e6;
    L_0x02de:
        r2 = r1.rowCount;
        r3 = r2 + 1;
        r1.rowCount = r3;
        r1.rightsShadowRow = r2;
    L_0x02e6:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChatRightsEditActivity.<init>(int, int, org.telegram.tgnet.TLRPC$TL_chatAdminRights, org.telegram.tgnet.TLRPC$TL_chatBannedRights, org.telegram.tgnet.TLRPC$TL_chatBannedRights, int, boolean, boolean):void");
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
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    if (ChatRightsEditActivity.this.checkDiscard()) {
                        ChatRightsEditActivity.this.finishFragment();
                    }
                } else if (i == 1) {
                    ChatRightsEditActivity.this.onDonePressed();
                }
            }
        });
        if (this.canEdit) {
            this.actionBar.createMenu().addItemWithWidth(1, NUM, AndroidUtilities.dp(56.0f), LocaleController.getString("Done", NUM));
        }
        this.fragmentView = new FrameLayout(context);
        this.fragmentView.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        this.listView = new RecyclerListView(context);
        AnonymousClass2 anonymousClass2 = new LinearLayoutManager(context, 1, false) {
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        };
        this.listView.setItemAnimator(null);
        this.listView.setLayoutAnimation(null);
        this.listView.setLayoutManager(anonymousClass2);
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
        this.listView.setOnItemClickListener(new -$$Lambda$ChatRightsEditActivity$aMVVQ8fOnltbn3fHM_guwcH1dhE(this, context));
        return this.fragmentView;
    }

    public /* synthetic */ void lambda$createView$6$ChatRightsEditActivity(Context context, View view, int i) {
        if (this.canEdit) {
            int i2;
            TL_chatBannedRights tL_chatBannedRights;
            if (i == 0) {
                Bundle bundle = new Bundle();
                bundle.putInt("user_id", this.currentUser.id);
                presentFragment(new ProfileActivity(bundle));
            } else if (i == this.removeAdminRow) {
                i2 = this.currentType;
                if (i2 == 0) {
                    MessagesController.getInstance(this.currentAccount).setUserAdminRole(this.chatId, this.currentUser, new TL_chatAdminRights(), this.isChannel, getFragmentForAlert(0), this.isAddingNew);
                } else if (i2 == 1) {
                    this.bannedRights = new TL_chatBannedRights();
                    tL_chatBannedRights = this.bannedRights;
                    tL_chatBannedRights.view_messages = true;
                    tL_chatBannedRights.send_media = true;
                    tL_chatBannedRights.send_messages = true;
                    tL_chatBannedRights.send_stickers = true;
                    tL_chatBannedRights.send_gifs = true;
                    tL_chatBannedRights.send_games = true;
                    tL_chatBannedRights.send_inline = true;
                    tL_chatBannedRights.embed_links = true;
                    tL_chatBannedRights.pin_messages = true;
                    tL_chatBannedRights.send_polls = true;
                    tL_chatBannedRights.invite_users = true;
                    tL_chatBannedRights.change_info = true;
                    tL_chatBannedRights.until_date = 0;
                    MessagesController.getInstance(this.currentAccount).setUserBannedRole(this.chatId, this.currentUser, this.bannedRights, this.isChannel, getFragmentForAlert(0));
                }
                ChatRightsEditActivityDelegate chatRightsEditActivityDelegate = this.delegate;
                if (chatRightsEditActivityDelegate != null) {
                    chatRightsEditActivityDelegate.didSetRights(0, this.adminRights, this.bannedRights);
                }
                finishFragment();
            } else if (i == this.untilDateRow) {
                if (getParentActivity() != null) {
                    Builder builder = new Builder(context);
                    builder.setApplyTopPadding(false);
                    LinearLayout linearLayout = new LinearLayout(context);
                    linearLayout.setOrientation(1);
                    HeaderCell headerCell = new HeaderCell(context, true, 23, 15, false);
                    headerCell.setHeight(47);
                    headerCell.setText(LocaleController.getString("UserRestrictionsDuration", NUM));
                    linearLayout.addView(headerCell);
                    LinearLayout linearLayout2 = new LinearLayout(context);
                    linearLayout2.setOrientation(1);
                    linearLayout.addView(linearLayout2, LayoutHelper.createLinear(-1, -2));
                    BottomSheetCell[] bottomSheetCellArr = new BottomSheetCell[5];
                    for (int i3 = 0; i3 < bottomSheetCellArr.length; i3++) {
                        CharSequence string;
                        bottomSheetCellArr[i3] = new BottomSheetCell(context, 0);
                        bottomSheetCellArr[i3].setPadding(AndroidUtilities.dp(23.0f), 0, AndroidUtilities.dp(23.0f), 0);
                        bottomSheetCellArr[i3].setTag(Integer.valueOf(i3));
                        bottomSheetCellArr[i3].setBackgroundDrawable(Theme.getSelectorDrawable(false));
                        if (i3 == 0) {
                            string = LocaleController.getString("UserRestrictionsUntilForever", NUM);
                        } else if (i3 == 1) {
                            string = LocaleController.formatPluralString("Days", 1);
                        } else if (i3 == 2) {
                            string = LocaleController.formatPluralString("Weeks", 1);
                        } else if (i3 != 3) {
                            string = LocaleController.getString("NotificationsCustom", NUM);
                        } else {
                            string = LocaleController.formatPluralString("Months", 1);
                        }
                        bottomSheetCellArr[i3].setTextAndIcon(string, 0);
                        linearLayout2.addView(bottomSheetCellArr[i3], LayoutHelper.createLinear(-1, -2));
                        bottomSheetCellArr[i3].setOnClickListener(new -$$Lambda$ChatRightsEditActivity$CgALXvrSg_HbFAY5rAsHrlvl1wY(this, builder));
                    }
                    builder.setCustomView(linearLayout);
                    showDialog(builder.create());
                }
            } else if (view instanceof TextCheckCell2) {
                TextCheckCell2 textCheckCell2 = (TextCheckCell2) view;
                if (textCheckCell2.hasIcon()) {
                    Toast.makeText(getParentActivity(), LocaleController.getString("UserRestrictionsDisabled", NUM), 0).show();
                } else if (textCheckCell2.isEnabled()) {
                    textCheckCell2.setChecked(textCheckCell2.isChecked() ^ 1);
                    TL_chatAdminRights tL_chatAdminRights;
                    if (i == this.changeInfoRow) {
                        if (this.currentType == 0) {
                            tL_chatAdminRights = this.adminRights;
                            tL_chatAdminRights.change_info ^= 1;
                        } else {
                            tL_chatBannedRights = this.bannedRights;
                            tL_chatBannedRights.change_info ^= 1;
                        }
                    } else if (i == this.postMessagesRow) {
                        tL_chatAdminRights = this.adminRights;
                        tL_chatAdminRights.post_messages ^= 1;
                    } else if (i == this.editMesagesRow) {
                        tL_chatAdminRights = this.adminRights;
                        tL_chatAdminRights.edit_messages ^= 1;
                    } else if (i == this.deleteMessagesRow) {
                        tL_chatAdminRights = this.adminRights;
                        tL_chatAdminRights.delete_messages ^= 1;
                    } else if (i == this.addAdminsRow) {
                        tL_chatAdminRights = this.adminRights;
                        tL_chatAdminRights.add_admins ^= 1;
                    } else if (i == this.banUsersRow) {
                        tL_chatAdminRights = this.adminRights;
                        tL_chatAdminRights.ban_users ^= 1;
                    } else if (i == this.addUsersRow) {
                        if (this.currentType == 0) {
                            tL_chatAdminRights = this.adminRights;
                            tL_chatAdminRights.invite_users ^= 1;
                        } else {
                            tL_chatBannedRights = this.bannedRights;
                            tL_chatBannedRights.invite_users ^= 1;
                        }
                    } else if (i == this.pinMessagesRow) {
                        if (this.currentType == 0) {
                            tL_chatAdminRights = this.adminRights;
                            tL_chatAdminRights.pin_messages ^= 1;
                        } else {
                            tL_chatBannedRights = this.bannedRights;
                            tL_chatBannedRights.pin_messages ^= 1;
                        }
                    } else if (this.bannedRights != null) {
                        i2 = textCheckCell2.isChecked() ^ 1;
                        TL_chatBannedRights tL_chatBannedRights2;
                        if (i == this.sendMessagesRow) {
                            tL_chatBannedRights2 = this.bannedRights;
                            tL_chatBannedRights2.send_messages ^= 1;
                        } else if (i == this.sendMediaRow) {
                            tL_chatBannedRights2 = this.bannedRights;
                            tL_chatBannedRights2.send_media ^= 1;
                        } else if (i == this.sendStickersRow) {
                            tL_chatBannedRights2 = this.bannedRights;
                            i = tL_chatBannedRights2.send_stickers ^ 1;
                            tL_chatBannedRights2.send_inline = i;
                            tL_chatBannedRights2.send_gifs = i;
                            tL_chatBannedRights2.send_games = i;
                            tL_chatBannedRights2.send_stickers = i;
                        } else if (i == this.embedLinksRow) {
                            tL_chatBannedRights2 = this.bannedRights;
                            tL_chatBannedRights2.embed_links ^= 1;
                        } else if (i == this.sendPollsRow) {
                            tL_chatBannedRights2 = this.bannedRights;
                            tL_chatBannedRights2.send_polls ^= 1;
                        }
                        ViewHolder findViewHolderForAdapterPosition;
                        if (i2 != 0) {
                            tL_chatBannedRights = this.bannedRights;
                            if (tL_chatBannedRights.view_messages && !tL_chatBannedRights.send_messages) {
                                tL_chatBannedRights.send_messages = true;
                                findViewHolderForAdapterPosition = this.listView.findViewHolderForAdapterPosition(this.sendMessagesRow);
                                if (findViewHolderForAdapterPosition != null) {
                                    ((TextCheckCell2) findViewHolderForAdapterPosition.itemView).setChecked(false);
                                }
                            }
                            tL_chatBannedRights = this.bannedRights;
                            if (tL_chatBannedRights.view_messages || tL_chatBannedRights.send_messages) {
                                tL_chatBannedRights = this.bannedRights;
                                if (!tL_chatBannedRights.send_media) {
                                    tL_chatBannedRights.send_media = true;
                                    findViewHolderForAdapterPosition = this.listView.findViewHolderForAdapterPosition(this.sendMediaRow);
                                    if (findViewHolderForAdapterPosition != null) {
                                        ((TextCheckCell2) findViewHolderForAdapterPosition.itemView).setChecked(false);
                                    }
                                }
                            }
                            tL_chatBannedRights = this.bannedRights;
                            if (tL_chatBannedRights.view_messages || tL_chatBannedRights.send_messages) {
                                tL_chatBannedRights = this.bannedRights;
                                if (!tL_chatBannedRights.send_polls) {
                                    tL_chatBannedRights.send_polls = true;
                                    findViewHolderForAdapterPosition = this.listView.findViewHolderForAdapterPosition(this.sendPollsRow);
                                    if (findViewHolderForAdapterPosition != null) {
                                        ((TextCheckCell2) findViewHolderForAdapterPosition.itemView).setChecked(false);
                                    }
                                }
                            }
                            tL_chatBannedRights = this.bannedRights;
                            if (tL_chatBannedRights.view_messages || tL_chatBannedRights.send_messages) {
                                tL_chatBannedRights = this.bannedRights;
                                if (!tL_chatBannedRights.send_stickers) {
                                    tL_chatBannedRights.send_inline = true;
                                    tL_chatBannedRights.send_gifs = true;
                                    tL_chatBannedRights.send_games = true;
                                    tL_chatBannedRights.send_stickers = true;
                                    findViewHolderForAdapterPosition = this.listView.findViewHolderForAdapterPosition(this.sendStickersRow);
                                    if (findViewHolderForAdapterPosition != null) {
                                        ((TextCheckCell2) findViewHolderForAdapterPosition.itemView).setChecked(false);
                                    }
                                }
                            }
                            tL_chatBannedRights = this.bannedRights;
                            if (tL_chatBannedRights.view_messages || tL_chatBannedRights.send_messages) {
                                tL_chatBannedRights = this.bannedRights;
                                if (!tL_chatBannedRights.embed_links) {
                                    tL_chatBannedRights.embed_links = true;
                                    findViewHolderForAdapterPosition = this.listView.findViewHolderForAdapterPosition(this.embedLinksRow);
                                    if (findViewHolderForAdapterPosition != null) {
                                        ((TextCheckCell2) findViewHolderForAdapterPosition.itemView).setChecked(false);
                                    }
                                }
                            }
                        } else {
                            tL_chatBannedRights = this.bannedRights;
                            if (!(tL_chatBannedRights.send_messages && tL_chatBannedRights.embed_links && tL_chatBannedRights.send_inline && tL_chatBannedRights.send_media && tL_chatBannedRights.send_polls)) {
                                tL_chatBannedRights = this.bannedRights;
                                if (tL_chatBannedRights.view_messages) {
                                    tL_chatBannedRights.view_messages = false;
                                }
                            }
                            tL_chatBannedRights = this.bannedRights;
                            if (!(tL_chatBannedRights.embed_links && tL_chatBannedRights.send_inline && tL_chatBannedRights.send_media && tL_chatBannedRights.send_polls)) {
                                tL_chatBannedRights = this.bannedRights;
                                if (tL_chatBannedRights.send_messages) {
                                    tL_chatBannedRights.send_messages = false;
                                    findViewHolderForAdapterPosition = this.listView.findViewHolderForAdapterPosition(this.sendMessagesRow);
                                    if (findViewHolderForAdapterPosition != null) {
                                        ((TextCheckCell2) findViewHolderForAdapterPosition.itemView).setChecked(true);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public /* synthetic */ void lambda$null$5$ChatRightsEditActivity(Builder builder, View view) {
        int intValue = ((Integer) view.getTag()).intValue();
        if (intValue == 0) {
            this.bannedRights.until_date = 0;
            this.listViewAdapter.notifyItemChanged(this.untilDateRow);
        } else if (intValue == 1) {
            this.bannedRights.until_date = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime() + 86400;
            this.listViewAdapter.notifyItemChanged(this.untilDateRow);
        } else if (intValue == 2) {
            this.bannedRights.until_date = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime() + 604800;
            this.listViewAdapter.notifyItemChanged(this.untilDateRow);
        } else if (intValue == 3) {
            this.bannedRights.until_date = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime() + 2592000;
            this.listViewAdapter.notifyItemChanged(this.untilDateRow);
        } else if (intValue == 4) {
            Calendar instance = Calendar.getInstance();
            try {
                DatePickerDialog datePickerDialog = new DatePickerDialog(getParentActivity(), new -$$Lambda$ChatRightsEditActivity$6G9ZDTyeTzElkJks0Z6TcSqzhXs(this), instance.get(1), instance.get(2), instance.get(5));
                DatePicker datePicker = datePickerDialog.getDatePicker();
                Calendar instance2 = Calendar.getInstance();
                instance2.setTimeInMillis(System.currentTimeMillis());
                instance2.set(11, instance2.getMinimum(11));
                instance2.set(12, instance2.getMinimum(12));
                instance2.set(13, instance2.getMinimum(13));
                instance2.set(14, instance2.getMinimum(14));
                datePicker.setMinDate(instance2.getTimeInMillis());
                instance2.setTimeInMillis(System.currentTimeMillis() + 31536000000L);
                instance2.set(11, instance2.getMaximum(11));
                instance2.set(12, instance2.getMaximum(12));
                instance2.set(13, instance2.getMaximum(13));
                instance2.set(14, instance2.getMaximum(14));
                datePicker.setMaxDate(instance2.getTimeInMillis());
                datePickerDialog.setButton(-1, LocaleController.getString("Set", NUM), datePickerDialog);
                datePickerDialog.setButton(-2, LocaleController.getString("Cancel", NUM), -$$Lambda$ChatRightsEditActivity$nJ41ofgPd7pnNhbrTqNHlhWO7ws.INSTANCE);
                if (VERSION.SDK_INT >= 21) {
                    datePickerDialog.setOnShowListener(new -$$Lambda$ChatRightsEditActivity$Z9VfsFUkfVTZPmGc9vtBuBrNWOo(datePicker));
                }
                showDialog(datePickerDialog);
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
        builder.getDismissRunnable().run();
    }

    public /* synthetic */ void lambda$null$2$ChatRightsEditActivity(DatePicker datePicker, int i, int i2, int i3) {
        Calendar instance = Calendar.getInstance();
        instance.clear();
        instance.set(i, i2, i3);
        try {
            TimePickerDialog timePickerDialog = new TimePickerDialog(getParentActivity(), new -$$Lambda$ChatRightsEditActivity$DIaVrokvmQgt4iHoxcLzGLzsq3k(this, (int) (instance.getTime().getTime() / 1000)), 0, 0, true);
            timePickerDialog.setButton(-1, LocaleController.getString("Set", NUM), timePickerDialog);
            timePickerDialog.setButton(-2, LocaleController.getString("Cancel", NUM), -$$Lambda$ChatRightsEditActivity$JCoxEgngqfok3q7QQt4m73nFirw.INSTANCE);
            showDialog(timePickerDialog);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public /* synthetic */ void lambda$null$0$ChatRightsEditActivity(int i, TimePicker timePicker, int i2, int i3) {
        this.bannedRights.until_date = (i + (i2 * 3600)) + (i3 * 60);
        this.listViewAdapter.notifyItemChanged(this.untilDateRow);
    }

    static /* synthetic */ void lambda$null$4(DatePicker datePicker, DialogInterface dialogInterface) {
        int childCount = datePicker.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = datePicker.getChildAt(i);
            LayoutParams layoutParams = childAt.getLayoutParams();
            layoutParams.width = -1;
            childAt.setLayoutParams(layoutParams);
        }
    }

    public void onResume() {
        super.onResume();
        ListAdapter listAdapter = this.listViewAdapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    private boolean isDefaultAdminRights() {
        TL_chatAdminRights tL_chatAdminRights = this.adminRights;
        if (!(tL_chatAdminRights.change_info && tL_chatAdminRights.delete_messages && tL_chatAdminRights.ban_users && tL_chatAdminRights.invite_users && tL_chatAdminRights.pin_messages && !tL_chatAdminRights.add_admins)) {
            tL_chatAdminRights = this.adminRights;
            if (tL_chatAdminRights.change_info || tL_chatAdminRights.delete_messages || tL_chatAdminRights.ban_users || tL_chatAdminRights.invite_users || tL_chatAdminRights.pin_messages || tL_chatAdminRights.add_admins) {
                return false;
            }
        }
        return true;
    }

    private void onDonePressed() {
        int i;
        int i2 = 1;
        if (!ChatObject.isChannel(this.currentChat)) {
            i = this.currentType;
            if (i == 1 || (i == 0 && !isDefaultAdminRights())) {
                MessagesController.getInstance(this.currentAccount).convertToMegaGroup(getParentActivity(), this.chatId, new -$$Lambda$ChatRightsEditActivity$hXfQmiw2gnL9RhXwZou1PDR6kt8(this));
                return;
            }
        }
        i = this.currentType;
        ChatRightsEditActivityDelegate chatRightsEditActivityDelegate;
        if (i == 0) {
            TL_chatAdminRights tL_chatAdminRights;
            if (this.isChannel) {
                tL_chatAdminRights = this.adminRights;
                tL_chatAdminRights.ban_users = false;
                tL_chatAdminRights.pin_messages = false;
            } else {
                tL_chatAdminRights = this.adminRights;
                tL_chatAdminRights.edit_messages = false;
                tL_chatAdminRights.post_messages = false;
            }
            MessagesController.getInstance(this.currentAccount).setUserAdminRole(this.chatId, this.currentUser, this.adminRights, this.isChannel, getFragmentForAlert(1), this.isAddingNew);
            chatRightsEditActivityDelegate = this.delegate;
            if (chatRightsEditActivityDelegate != null) {
                TL_chatAdminRights tL_chatAdminRights2 = this.adminRights;
                if (!(tL_chatAdminRights2.change_info || tL_chatAdminRights2.post_messages || tL_chatAdminRights2.edit_messages || tL_chatAdminRights2.delete_messages || tL_chatAdminRights2.ban_users || tL_chatAdminRights2.invite_users || tL_chatAdminRights2.pin_messages || tL_chatAdminRights2.add_admins)) {
                    i2 = 0;
                }
                chatRightsEditActivityDelegate.didSetRights(i2, this.adminRights, this.bannedRights);
            }
        } else if (i == 1) {
            MessagesController.getInstance(this.currentAccount).setUserBannedRole(this.chatId, this.currentUser, this.bannedRights, this.isChannel, getFragmentForAlert(1));
            TL_chatBannedRights tL_chatBannedRights = this.bannedRights;
            if (!(tL_chatBannedRights.send_messages || tL_chatBannedRights.send_stickers || tL_chatBannedRights.embed_links || tL_chatBannedRights.send_media || tL_chatBannedRights.send_gifs || tL_chatBannedRights.send_games || tL_chatBannedRights.send_inline)) {
                tL_chatBannedRights.until_date = 0;
                i2 = 2;
            }
            chatRightsEditActivityDelegate = this.delegate;
            if (chatRightsEditActivityDelegate != null) {
                chatRightsEditActivityDelegate.didSetRights(i2, this.adminRights, this.bannedRights);
            }
        }
        finishFragment();
    }

    public /* synthetic */ void lambda$onDonePressed$7$ChatRightsEditActivity(int i) {
        this.chatId = i;
        this.currentChat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(i));
        onDonePressed();
    }

    public void setDelegate(ChatRightsEditActivityDelegate chatRightsEditActivityDelegate) {
        this.delegate = chatRightsEditActivityDelegate;
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
        builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("UserRestrictionsApplyChangesText", NUM, MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.chatId)).title)));
        builder.setPositiveButton(LocaleController.getString("ApplyTheme", NUM), new -$$Lambda$ChatRightsEditActivity$H_3StnNI8nSWDF0VmiZshr2iV-s(this));
        builder.setNegativeButton(LocaleController.getString("PassportDiscard", NUM), new -$$Lambda$ChatRightsEditActivity$JCX77xMHDnfbaKObjN8bjfUr2po(this));
        showDialog(builder.create());
        return false;
    }

    public /* synthetic */ void lambda$checkDiscard$8$ChatRightsEditActivity(DialogInterface dialogInterface, int i) {
        onDonePressed();
    }

    public /* synthetic */ void lambda$checkDiscard$9$ChatRightsEditActivity(DialogInterface dialogInterface, int i) {
        finishFragment();
    }

    public boolean onBackPressed() {
        return checkDiscard();
    }

    public ThemeDescription[] getThemeDescriptions() {
        -$$Lambda$ChatRightsEditActivity$GNRN9rFTP86SvIwNef-1LwNrqM8 -__lambda_chatrightseditactivity_gnrn9rftp86sviwnef-1lwnrqm8 = new -$$Lambda$ChatRightsEditActivity$GNRN9rFTP86SvIwNef-1LwNrqM8(this);
        ThemeDescription[] themeDescriptionArr = new ThemeDescription[38];
        themeDescriptionArr[0] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{UserCell.class, TextSettingsCell.class, TextCheckCell2.class, HeaderCell.class, TextDetailCell.class}, null, null, null, "windowBackgroundWhite");
        themeDescriptionArr[1] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundGray");
        themeDescriptionArr[2] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault");
        themeDescriptionArr[3] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault");
        themeDescriptionArr[4] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon");
        themeDescriptionArr[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle");
        themeDescriptionArr[6] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector");
        themeDescriptionArr[7] = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21");
        themeDescriptionArr[8] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, "divider");
        View view = this.listView;
        View view2 = view;
        themeDescriptionArr[9] = new ThemeDescription(view2, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, "windowBackgroundGrayShadow");
        view = this.listView;
        Class[] clsArr = new Class[]{TextInfoPrivacyCell.class};
        String[] strArr = new String[1];
        strArr[0] = "textView";
        themeDescriptionArr[10] = new ThemeDescription(view, 0, clsArr, strArr, null, null, null, "windowBackgroundWhiteGrayText4");
        view = this.listView;
        view2 = view;
        themeDescriptionArr[11] = new ThemeDescription(view2, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteRedText5");
        view = this.listView;
        view2 = view;
        themeDescriptionArr[12] = new ThemeDescription(view2, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText");
        view = this.listView;
        clsArr = new Class[]{TextSettingsCell.class};
        strArr = new String[1];
        strArr[0] = "valueTextView";
        themeDescriptionArr[13] = new ThemeDescription(view, 0, clsArr, strArr, null, null, null, "windowBackgroundWhiteValueText");
        themeDescriptionArr[14] = new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueImageView"}, null, null, null, "windowBackgroundWhiteGrayIcon");
        themeDescriptionArr[15] = new ThemeDescription(this.listView, 0, new Class[]{TextDetailCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText");
        themeDescriptionArr[16] = new ThemeDescription(this.listView, 0, new Class[]{TextDetailCell.class}, new String[]{"valueTextView"}, null, null, null, "windowBackgroundWhiteGrayText2");
        themeDescriptionArr[17] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell2.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText");
        themeDescriptionArr[18] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell2.class}, new String[]{"valueTextView"}, null, null, null, "windowBackgroundWhiteGrayText2");
        view = this.listView;
        clsArr = new Class[]{TextCheckCell2.class};
        strArr = new String[1];
        strArr[0] = "checkBox";
        themeDescriptionArr[19] = new ThemeDescription(view, 0, clsArr, strArr, null, null, null, "switch2Track");
        themeDescriptionArr[20] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell2.class}, new String[]{"checkBox"}, null, null, null, "switch2TrackChecked");
        view = this.listView;
        view2 = view;
        themeDescriptionArr[21] = new ThemeDescription(view2, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, "windowBackgroundGrayShadow");
        themeDescriptionArr[22] = new ThemeDescription(this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlueHeader");
        themeDescriptionArr[23] = new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, new String[]{"nameTextView"}, null, null, null, "windowBackgroundWhiteBlackText");
        ThemeDescriptionDelegate themeDescriptionDelegate = -__lambda_chatrightseditactivity_gnrn9rftp86sviwnef-1lwnrqm8;
        themeDescriptionArr[24] = new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, new String[]{"statusColor"}, null, null, themeDescriptionDelegate, "windowBackgroundWhiteGrayText");
        themeDescriptionArr[25] = new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, new String[]{"statusOnlineColor"}, null, null, themeDescriptionDelegate, "windowBackgroundWhiteBlueText");
        themeDescriptionArr[26] = new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, null, new Drawable[]{Theme.avatar_broadcastDrawable, Theme.avatar_savedDrawable}, null, "avatar_text");
        -$$Lambda$ChatRightsEditActivity$GNRN9rFTP86SvIwNef-1LwNrqM8 -__lambda_chatrightseditactivity_gnrn9rftp86sviwnef-1lwnrqm82 = -__lambda_chatrightseditactivity_gnrn9rftp86sviwnef-1lwnrqm8;
        themeDescriptionArr[27] = new ThemeDescription(null, 0, null, null, null, -__lambda_chatrightseditactivity_gnrn9rftp86sviwnef-1lwnrqm82, "avatar_backgroundRed");
        themeDescriptionArr[28] = new ThemeDescription(null, 0, null, null, null, -__lambda_chatrightseditactivity_gnrn9rftp86sviwnef-1lwnrqm82, "avatar_backgroundOrange");
        themeDescriptionArr[29] = new ThemeDescription(null, 0, null, null, null, -__lambda_chatrightseditactivity_gnrn9rftp86sviwnef-1lwnrqm82, "avatar_backgroundViolet");
        themeDescriptionArr[30] = new ThemeDescription(null, 0, null, null, null, -__lambda_chatrightseditactivity_gnrn9rftp86sviwnef-1lwnrqm82, "avatar_backgroundGreen");
        themeDescriptionArr[31] = new ThemeDescription(null, 0, null, null, null, -__lambda_chatrightseditactivity_gnrn9rftp86sviwnef-1lwnrqm82, "avatar_backgroundCyan");
        themeDescriptionArr[32] = new ThemeDescription(null, 0, null, null, null, -__lambda_chatrightseditactivity_gnrn9rftp86sviwnef-1lwnrqm82, "avatar_backgroundBlue");
        themeDescriptionArr[33] = new ThemeDescription(null, 0, null, null, null, -__lambda_chatrightseditactivity_gnrn9rftp86sviwnef-1lwnrqm82, "avatar_backgroundPink");
        themeDescriptionArr[34] = new ThemeDescription(null, 0, new Class[]{DialogRadioCell.class}, new String[]{"textView"}, null, null, null, "dialogTextBlack");
        themeDescriptionArr[35] = new ThemeDescription(null, 0, new Class[]{DialogRadioCell.class}, new String[]{"textView"}, null, null, null, "dialogTextGray2");
        int i = ThemeDescription.FLAG_CHECKBOX;
        Class[] clsArr2 = new Class[]{DialogRadioCell.class};
        String[] strArr2 = new String[1];
        strArr2[0] = "radioButton";
        themeDescriptionArr[36] = new ThemeDescription(null, i, clsArr2, strArr2, null, null, null, "dialogRadioBackground");
        themeDescriptionArr[37] = new ThemeDescription(null, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{DialogRadioCell.class}, new String[]{"radioButton"}, null, null, null, "dialogRadioBackgroundChecked");
        return themeDescriptionArr;
    }

    public /* synthetic */ void lambda$getThemeDescriptions$10$ChatRightsEditActivity() {
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            int childCount = recyclerListView.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = this.listView.getChildAt(i);
                if (childAt instanceof UserCell) {
                    ((UserCell) childAt).update(0);
                }
            }
        }
    }
}
