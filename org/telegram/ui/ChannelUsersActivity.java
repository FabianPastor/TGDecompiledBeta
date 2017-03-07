package org.telegram.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.Adapter;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.messenger.support.widget.helper.ItemTouchHelper.Callback;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.ChannelParticipant;
import org.telegram.tgnet.TLRPC.ChannelParticipantRole;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.TL_channelParticipantCreator;
import org.telegram.tgnet.TLRPC.TL_channelParticipantEditor;
import org.telegram.tgnet.TLRPC.TL_channelParticipantModerator;
import org.telegram.tgnet.TLRPC.TL_channelParticipantSelf;
import org.telegram.tgnet.TLRPC.TL_channelParticipantsAdmins;
import org.telegram.tgnet.TLRPC.TL_channelParticipantsKicked;
import org.telegram.tgnet.TLRPC.TL_channelParticipantsRecent;
import org.telegram.tgnet.TLRPC.TL_channelRoleEditor;
import org.telegram.tgnet.TLRPC.TL_channelRoleEmpty;
import org.telegram.tgnet.TLRPC.TL_channels_channelParticipants;
import org.telegram.tgnet.TLRPC.TL_channels_editAdmin;
import org.telegram.tgnet.TLRPC.TL_channels_getParticipants;
import org.telegram.tgnet.TLRPC.TL_channels_kickFromChannel;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.Updates;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.RadioCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Cells.UserCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;
import org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;
import org.telegram.ui.ContactsActivity.ContactsActivityDelegate;

public class ChannelUsersActivity extends BaseFragment implements NotificationCenterDelegate {
    private int chatId = this.arguments.getInt("chat_id");
    private EmptyTextProgressView emptyView;
    private boolean firstLoaded;
    private boolean isAdmin;
    private boolean isMegagroup;
    private boolean isPublic;
    private RecyclerListView listView;
    private ListAdapter listViewAdapter;
    private boolean loadingUsers;
    private ArrayList<ChannelParticipant> participants = new ArrayList();
    private int participantsStartRow;
    private int type = this.arguments.getInt("type");

    private class ListAdapter extends SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean isEnabled(ViewHolder holder) {
            boolean z = true;
            int postion = holder.getAdapterPosition();
            if (ChannelUsersActivity.this.type == 2) {
                if (ChannelUsersActivity.this.isAdmin) {
                    if (ChannelUsersActivity.this.isPublic) {
                        if (postion == 0) {
                            return true;
                        }
                        if (postion == 1) {
                            return false;
                        }
                    } else if (postion == 0 || postion == 1) {
                        return true;
                    } else {
                        if (postion == 2) {
                            return false;
                        }
                    }
                }
            } else if (ChannelUsersActivity.this.type == 1) {
                if (postion == ChannelUsersActivity.this.participantsStartRow + ChannelUsersActivity.this.participants.size()) {
                    return ChannelUsersActivity.this.isAdmin;
                }
                if (postion == (ChannelUsersActivity.this.participantsStartRow + ChannelUsersActivity.this.participants.size()) + 1) {
                    return false;
                }
                if (ChannelUsersActivity.this.isMegagroup && ChannelUsersActivity.this.isAdmin && postion < 4) {
                    boolean z2 = postion == 1 || postion == 2;
                    return z2;
                }
            }
            if (postion == ChannelUsersActivity.this.participants.size() + ChannelUsersActivity.this.participantsStartRow || ((ChannelParticipant) ChannelUsersActivity.this.participants.get(postion - ChannelUsersActivity.this.participantsStartRow)).user_id == UserConfig.getClientUserId()) {
                z = false;
            }
            return z;
        }

        public int getItemCount() {
            int i = 1;
            if (ChannelUsersActivity.this.participants.isEmpty() && ChannelUsersActivity.this.type == 0) {
                return 0;
            }
            if (ChannelUsersActivity.this.loadingUsers && !ChannelUsersActivity.this.firstLoaded) {
                return 0;
            }
            if (ChannelUsersActivity.this.type != 1) {
                return (ChannelUsersActivity.this.participants.size() + ChannelUsersActivity.this.participantsStartRow) + 1;
            }
            int size = ChannelUsersActivity.this.participants.size();
            if (ChannelUsersActivity.this.isAdmin) {
                i = 2;
            }
            size += i;
            i = (ChannelUsersActivity.this.isAdmin && ChannelUsersActivity.this.isMegagroup) ? 4 : 0;
            return size + i;
        }

        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    view = new UserCell(this.mContext, 1, 0, false);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 1:
                    view = new TextInfoPrivacyCell(this.mContext);
                    break;
                case 2:
                    view = new TextSettingsCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 3:
                    view = new ShadowSectionCell(this.mContext);
                    break;
                case 4:
                    view = new TextCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 5:
                    view = new HeaderCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                default:
                    view = new RadioCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
            }
            return new Holder(view);
        }

        public void onBindViewHolder(ViewHolder holder, int position) {
            switch (holder.getItemViewType()) {
                case 0:
                    UserCell userCell = holder.itemView;
                    ChannelParticipant participant = (ChannelParticipant) ChannelUsersActivity.this.participants.get(position - ChannelUsersActivity.this.participantsStartRow);
                    User user = MessagesController.getInstance().getUser(Integer.valueOf(participant.user_id));
                    if (user == null) {
                        return;
                    }
                    if (ChannelUsersActivity.this.type == 0) {
                        CharSequence string = (user.phone == null || user.phone.length() == 0) ? LocaleController.getString("NumberUnknown", R.string.NumberUnknown) : PhoneFormat.getInstance().format("+" + user.phone);
                        userCell.setData(user, null, string, 0);
                        return;
                    } else if (ChannelUsersActivity.this.type == 1) {
                        String role = null;
                        if ((participant instanceof TL_channelParticipantCreator) || (participant instanceof TL_channelParticipantSelf)) {
                            role = LocaleController.getString("ChannelCreator", R.string.ChannelCreator);
                        } else if (participant instanceof TL_channelParticipantModerator) {
                            role = LocaleController.getString("ChannelModerator", R.string.ChannelModerator);
                        } else if (participant instanceof TL_channelParticipantEditor) {
                            role = LocaleController.getString("ChannelEditor", R.string.ChannelEditor);
                        }
                        userCell.setData(user, null, role, 0);
                        return;
                    } else if (ChannelUsersActivity.this.type == 2) {
                        userCell.setData(user, null, null, 0);
                        return;
                    } else {
                        return;
                    }
                case 1:
                    TextInfoPrivacyCell privacyCell = holder.itemView;
                    if (ChannelUsersActivity.this.type == 0) {
                        privacyCell.setText(String.format("%1$s\n\n%2$s", new Object[]{LocaleController.getString("NoBlockedGroup", R.string.NoBlockedGroup), LocaleController.getString("UnblockText", R.string.UnblockText)}));
                        privacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                        return;
                    } else if (ChannelUsersActivity.this.type == 1) {
                        if (ChannelUsersActivity.this.isAdmin) {
                            if (ChannelUsersActivity.this.isMegagroup) {
                                privacyCell.setText(LocaleController.getString("MegaAdminsInfo", R.string.MegaAdminsInfo));
                            } else {
                                privacyCell.setText(LocaleController.getString("ChannelAdminsInfo", R.string.ChannelAdminsInfo));
                            }
                            privacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                            return;
                        }
                        privacyCell.setText("");
                        privacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                        return;
                    } else if (ChannelUsersActivity.this.type != 2) {
                        return;
                    } else {
                        if (((ChannelUsersActivity.this.isPublic || position != 2) && position != 1) || !ChannelUsersActivity.this.isAdmin) {
                            privacyCell.setText("");
                            privacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                            return;
                        }
                        if (ChannelUsersActivity.this.isMegagroup) {
                            privacyCell.setText("");
                        } else {
                            privacyCell.setText(LocaleController.getString("ChannelMembersInfo", R.string.ChannelMembersInfo));
                        }
                        privacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                        return;
                    }
                case 2:
                    TextSettingsCell actionCell = holder.itemView;
                    if (ChannelUsersActivity.this.type == 2) {
                        if (position == 0) {
                            actionCell.setText(LocaleController.getString("AddMember", R.string.AddMember), true);
                            return;
                        } else if (position == 1) {
                            actionCell.setText(LocaleController.getString("ChannelInviteViaLink", R.string.ChannelInviteViaLink), false);
                            return;
                        } else {
                            return;
                        }
                    } else if (ChannelUsersActivity.this.type == 1) {
                        actionCell.setTextAndIcon(LocaleController.getString("ChannelAddAdmin", R.string.ChannelAddAdmin), R.drawable.managers, false);
                        return;
                    } else {
                        return;
                    }
                case 4:
                    ((TextCell) holder.itemView).setTextAndIcon(LocaleController.getString("ChannelAddAdmin", R.string.ChannelAddAdmin), R.drawable.managers);
                    return;
                case 5:
                    ((HeaderCell) holder.itemView).setText(LocaleController.getString("WhoCanAddMembers", R.string.WhoCanAddMembers));
                    return;
                case 6:
                    RadioCell radioCell = holder.itemView;
                    Chat chat = MessagesController.getInstance().getChat(Integer.valueOf(ChannelUsersActivity.this.chatId));
                    String string2;
                    boolean z;
                    if (position == 1) {
                        radioCell.setTag(Integer.valueOf(0));
                        string2 = LocaleController.getString("WhoCanAddMembersAllMembers", R.string.WhoCanAddMembersAllMembers);
                        z = chat != null && chat.democracy;
                        radioCell.setText(string2, z, true);
                        return;
                    } else if (position == 2) {
                        radioCell.setTag(Integer.valueOf(1));
                        string2 = LocaleController.getString("WhoCanAddMembersAdmins", R.string.WhoCanAddMembersAdmins);
                        z = (chat == null || chat.democracy) ? false : true;
                        radioCell.setText(string2, z, false);
                        return;
                    } else {
                        return;
                    }
                default:
                    return;
            }
        }

        public int getItemViewType(int i) {
            if (ChannelUsersActivity.this.type == 1) {
                if (ChannelUsersActivity.this.isAdmin) {
                    if (ChannelUsersActivity.this.isMegagroup) {
                        if (i == 0) {
                            return 5;
                        }
                        if (i == 1 || i == 2) {
                            return 6;
                        }
                        if (i == 3) {
                            return 3;
                        }
                    }
                    if (i == ChannelUsersActivity.this.participantsStartRow + ChannelUsersActivity.this.participants.size()) {
                        return 4;
                    }
                    if (i == (ChannelUsersActivity.this.participantsStartRow + ChannelUsersActivity.this.participants.size()) + 1) {
                        return 1;
                    }
                }
            } else if (ChannelUsersActivity.this.type == 2 && ChannelUsersActivity.this.isAdmin) {
                if (ChannelUsersActivity.this.isPublic) {
                    if (i == 0) {
                        return 2;
                    }
                    if (i == 1) {
                        return 1;
                    }
                } else if (i == 0 || i == 1) {
                    return 2;
                } else {
                    if (i == 2) {
                        return 1;
                    }
                }
            }
            if (i == ChannelUsersActivity.this.participants.size() + ChannelUsersActivity.this.participantsStartRow) {
                return 1;
            }
            return 0;
        }
    }

    public ChannelUsersActivity(Bundle args) {
        int i = 0;
        super(args);
        Chat chat = MessagesController.getInstance().getChat(Integer.valueOf(this.chatId));
        if (chat != null) {
            if (chat.creator) {
                this.isAdmin = true;
                this.isPublic = (chat.flags & 64) != 0;
            }
            this.isMegagroup = chat.megagroup;
        }
        if (this.type == 0) {
            this.participantsStartRow = 0;
        } else if (this.type == 1) {
            if (this.isAdmin && this.isMegagroup) {
                i = 4;
            }
            this.participantsStartRow = i;
        } else if (this.type == 2) {
            if (this.isAdmin) {
                i = this.isPublic ? 2 : 3;
            }
            this.participantsStartRow = i;
        }
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.chatInfoDidLoaded);
        getChannelParticipants(0, Callback.DEFAULT_DRAG_ANIMATION_DURATION);
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.chatInfoDidLoaded);
    }

    public View createView(Context context) {
        int i = 1;
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        if (this.type == 0) {
            this.actionBar.setTitle(LocaleController.getString("ChannelBlockedUsers", R.string.ChannelBlockedUsers));
        } else if (this.type == 1) {
            this.actionBar.setTitle(LocaleController.getString("ChannelAdministrators", R.string.ChannelAdministrators));
        } else if (this.type == 2) {
            this.actionBar.setTitle(LocaleController.getString("ChannelMembers", R.string.ChannelMembers));
        }
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    ChannelUsersActivity.this.finishFragment();
                }
            }
        });
        this.fragmentView = new FrameLayout(context);
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        FrameLayout frameLayout = this.fragmentView;
        this.emptyView = new EmptyTextProgressView(context);
        if (this.type == 0) {
            if (this.isMegagroup) {
                this.emptyView.setText(LocaleController.getString("NoBlockedGroup", R.string.NoBlockedGroup));
            } else {
                this.emptyView.setText(LocaleController.getString("NoBlocked", R.string.NoBlocked));
            }
        }
        frameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView = new RecyclerListView(context);
        this.listView.setEmptyView(this.emptyView);
        this.listView.setLayoutManager(new LinearLayoutManager(context, 1, false));
        RecyclerListView recyclerListView = this.listView;
        Adapter listAdapter = new ListAdapter(context);
        this.listViewAdapter = listAdapter;
        recyclerListView.setAdapter(listAdapter);
        recyclerListView = this.listView;
        if (!LocaleController.isRTL) {
            i = 2;
        }
        recyclerListView.setVerticalScrollbarPosition(i);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(View view, int position) {
                Bundle args;
                ContactsActivity fragment;
                if (ChannelUsersActivity.this.type == 2) {
                    if (ChannelUsersActivity.this.isAdmin) {
                        if (position == 0) {
                            args = new Bundle();
                            args.putBoolean("onlyUsers", true);
                            args.putBoolean("destroyAfterSelect", true);
                            args.putBoolean("returnAsResult", true);
                            args.putBoolean("needForwardCount", false);
                            args.putString("selectAlertString", LocaleController.getString("ChannelAddTo", R.string.ChannelAddTo));
                            fragment = new ContactsActivity(args);
                            fragment.setDelegate(new ContactsActivityDelegate() {
                                public void didSelectContact(User user, String param) {
                                    MessagesController.getInstance().addUserToChat(ChannelUsersActivity.this.chatId, user, null, param != null ? Utilities.parseInt(param).intValue() : 0, null, ChannelUsersActivity.this);
                                }
                            });
                            ChannelUsersActivity.this.presentFragment(fragment);
                        } else if (!ChannelUsersActivity.this.isPublic && position == 1) {
                            ChannelUsersActivity.this.presentFragment(new GroupInviteActivity(ChannelUsersActivity.this.chatId));
                        }
                    }
                } else if (ChannelUsersActivity.this.type == 1 && ChannelUsersActivity.this.isAdmin) {
                    if (ChannelUsersActivity.this.isMegagroup && (position == 1 || position == 2)) {
                        Chat chat = MessagesController.getInstance().getChat(Integer.valueOf(ChannelUsersActivity.this.chatId));
                        if (chat != null) {
                            boolean changed = false;
                            if (position == 1 && !chat.democracy) {
                                chat.democracy = true;
                                changed = true;
                            } else if (position == 2 && chat.democracy) {
                                chat.democracy = false;
                                changed = true;
                            }
                            if (changed) {
                                MessagesController.getInstance().toogleChannelInvites(ChannelUsersActivity.this.chatId, chat.democracy);
                                int count = ChannelUsersActivity.this.listView.getChildCount();
                                for (int a = 0; a < count; a++) {
                                    View child = ChannelUsersActivity.this.listView.getChildAt(a);
                                    if (child instanceof RadioCell) {
                                        int num = ((Integer) child.getTag()).intValue();
                                        RadioCell radioCell = (RadioCell) child;
                                        boolean z = (num == 0 && chat.democracy) || (num == 1 && !chat.democracy);
                                        radioCell.setChecked(z, true);
                                    }
                                }
                                return;
                            }
                            return;
                        }
                        return;
                    } else if (position == ChannelUsersActivity.this.participantsStartRow + ChannelUsersActivity.this.participants.size()) {
                        args = new Bundle();
                        args.putBoolean("onlyUsers", true);
                        args.putBoolean("destroyAfterSelect", true);
                        args.putBoolean("returnAsResult", true);
                        args.putBoolean("needForwardCount", false);
                        args.putBoolean("addingToChannel", !ChannelUsersActivity.this.isMegagroup);
                        args.putString("selectAlertString", LocaleController.getString("ChannelAddUserAdminAlert", R.string.ChannelAddUserAdminAlert));
                        fragment = new ContactsActivity(args);
                        fragment.setDelegate(new ContactsActivityDelegate() {
                            public void didSelectContact(User user, String param) {
                                ChannelUsersActivity.this.setUserChannelRole(user, new TL_channelRoleEditor());
                            }
                        });
                        ChannelUsersActivity.this.presentFragment(fragment);
                        return;
                    }
                }
                ChannelParticipant participant = null;
                if (position >= ChannelUsersActivity.this.participantsStartRow && position < ChannelUsersActivity.this.participants.size() + ChannelUsersActivity.this.participantsStartRow) {
                    participant = (ChannelParticipant) ChannelUsersActivity.this.participants.get(position - ChannelUsersActivity.this.participantsStartRow);
                }
                if (participant != null) {
                    args = new Bundle();
                    args.putInt("user_id", participant.user_id);
                    ChannelUsersActivity.this.presentFragment(new ProfileActivity(args));
                }
            }
        });
        if (this.isAdmin || (this.isMegagroup && this.type == 0)) {
            this.listView.setOnItemLongClickListener(new OnItemLongClickListener() {
                public boolean onItemClick(View view, int position) {
                    if (ChannelUsersActivity.this.getParentActivity() == null) {
                        return false;
                    }
                    ChannelParticipant participant = null;
                    if (position >= ChannelUsersActivity.this.participantsStartRow && position < ChannelUsersActivity.this.participants.size() + ChannelUsersActivity.this.participantsStartRow) {
                        participant = (ChannelParticipant) ChannelUsersActivity.this.participants.get(position - ChannelUsersActivity.this.participantsStartRow);
                    }
                    if (participant == null) {
                        return false;
                    }
                    final ChannelParticipant finalParticipant = participant;
                    Builder builder = new Builder(ChannelUsersActivity.this.getParentActivity());
                    CharSequence[] items = null;
                    if (ChannelUsersActivity.this.type == 0) {
                        items = new CharSequence[]{LocaleController.getString("Unblock", R.string.Unblock)};
                    } else if (ChannelUsersActivity.this.type == 1) {
                        items = new CharSequence[]{LocaleController.getString("ChannelRemoveUserAdmin", R.string.ChannelRemoveUserAdmin)};
                    } else if (ChannelUsersActivity.this.type == 2) {
                        items = new CharSequence[]{LocaleController.getString("ChannelRemoveUser", R.string.ChannelRemoveUser)};
                    }
                    builder.setItems(items, new OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (i != 0) {
                                return;
                            }
                            if (ChannelUsersActivity.this.type == 0) {
                                ChannelUsersActivity.this.participants.remove(finalParticipant);
                                ChannelUsersActivity.this.listViewAdapter.notifyDataSetChanged();
                                TL_channels_kickFromChannel req = new TL_channels_kickFromChannel();
                                req.kicked = false;
                                req.user_id = MessagesController.getInputUser(finalParticipant.user_id);
                                req.channel = MessagesController.getInputChannel(ChannelUsersActivity.this.chatId);
                                ConnectionsManager.getInstance().sendRequest(req, new RequestDelegate() {
                                    public void run(TLObject response, TL_error error) {
                                        if (response != null) {
                                            final Updates updates = (Updates) response;
                                            MessagesController.getInstance().processUpdates(updates, false);
                                            if (!updates.chats.isEmpty()) {
                                                AndroidUtilities.runOnUIThread(new Runnable() {
                                                    public void run() {
                                                        MessagesController.getInstance().loadFullChat(((Chat) updates.chats.get(0)).id, 0, true);
                                                    }
                                                }, 1000);
                                            }
                                        }
                                    }
                                });
                            } else if (ChannelUsersActivity.this.type == 1) {
                                ChannelUsersActivity.this.setUserChannelRole(MessagesController.getInstance().getUser(Integer.valueOf(finalParticipant.user_id)), new TL_channelRoleEmpty());
                            } else if (ChannelUsersActivity.this.type == 2) {
                                MessagesController.getInstance().deleteUserFromChat(ChannelUsersActivity.this.chatId, MessagesController.getInstance().getUser(Integer.valueOf(finalParticipant.user_id)), null);
                            }
                        }
                    });
                    ChannelUsersActivity.this.showDialog(builder.create());
                    return true;
                }
            });
        }
        if (this.loadingUsers) {
            this.emptyView.showProgress();
        } else {
            this.emptyView.showTextView();
        }
        return this.fragmentView;
    }

    public void setUserChannelRole(User user, ChannelParticipantRole role) {
        if (user != null && role != null) {
            final TL_channels_editAdmin req = new TL_channels_editAdmin();
            req.channel = MessagesController.getInputChannel(this.chatId);
            req.user_id = MessagesController.getInputUser(user);
            req.role = role;
            ConnectionsManager.getInstance().sendRequest(req, new RequestDelegate() {
                public void run(TLObject response, final TL_error error) {
                    if (error == null) {
                        MessagesController.getInstance().processUpdates((Updates) response, false);
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                MessagesController.getInstance().loadFullChat(ChannelUsersActivity.this.chatId, 0, true);
                            }
                        }, 1000);
                        return;
                    }
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            boolean z = true;
                            TL_error tL_error = error;
                            BaseFragment baseFragment = ChannelUsersActivity.this;
                            TLObject tLObject = req;
                            Object[] objArr = new Object[1];
                            if (ChannelUsersActivity.this.isMegagroup) {
                                z = false;
                            }
                            objArr[0] = Boolean.valueOf(z);
                            AlertsCreator.processError(tL_error, baseFragment, tLObject, objArr);
                        }
                    });
                }
            });
        }
    }

    public void didReceivedNotification(int id, Object... args) {
        if (id == NotificationCenter.chatInfoDidLoaded && args[0].id == this.chatId) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    ChannelUsersActivity.this.getChannelParticipants(0, Callback.DEFAULT_DRAG_ANIMATION_DURATION);
                }
            });
        }
    }

    private int getChannelAdminParticipantType(ChannelParticipant participant) {
        if ((participant instanceof TL_channelParticipantCreator) || (participant instanceof TL_channelParticipantSelf)) {
            return 0;
        }
        if (participant instanceof TL_channelParticipantEditor) {
            return 1;
        }
        return 2;
    }

    private void getChannelParticipants(int offset, int count) {
        if (!this.loadingUsers) {
            this.loadingUsers = true;
            if (!(this.emptyView == null || this.firstLoaded)) {
                this.emptyView.showProgress();
            }
            if (this.listViewAdapter != null) {
                this.listViewAdapter.notifyDataSetChanged();
            }
            TL_channels_getParticipants req = new TL_channels_getParticipants();
            req.channel = MessagesController.getInputChannel(this.chatId);
            if (this.type == 0) {
                req.filter = new TL_channelParticipantsKicked();
            } else if (this.type == 1) {
                req.filter = new TL_channelParticipantsAdmins();
            } else if (this.type == 2) {
                req.filter = new TL_channelParticipantsRecent();
            }
            req.offset = offset;
            req.limit = count;
            ConnectionsManager.getInstance().bindRequestToGuid(ConnectionsManager.getInstance().sendRequest(req, new RequestDelegate() {
                public void run(final TLObject response, final TL_error error) {
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            if (error == null) {
                                TL_channels_channelParticipants res = response;
                                MessagesController.getInstance().putUsers(res.users, false);
                                ChannelUsersActivity.this.participants = res.participants;
                                try {
                                    if (ChannelUsersActivity.this.type == 0 || ChannelUsersActivity.this.type == 2) {
                                        Collections.sort(ChannelUsersActivity.this.participants, new Comparator<ChannelParticipant>() {
                                            public int compare(ChannelParticipant lhs, ChannelParticipant rhs) {
                                                User user1 = MessagesController.getInstance().getUser(Integer.valueOf(rhs.user_id));
                                                User user2 = MessagesController.getInstance().getUser(Integer.valueOf(lhs.user_id));
                                                int status1 = 0;
                                                int status2 = 0;
                                                if (!(user1 == null || user1.status == null)) {
                                                    status1 = user1.id == UserConfig.getClientUserId() ? ConnectionsManager.getInstance().getCurrentTime() + 50000 : user1.status.expires;
                                                }
                                                if (!(user2 == null || user2.status == null)) {
                                                    status2 = user2.id == UserConfig.getClientUserId() ? ConnectionsManager.getInstance().getCurrentTime() + 50000 : user2.status.expires;
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
                                        });
                                    } else if (ChannelUsersActivity.this.type == 1) {
                                        Collections.sort(res.participants, new Comparator<ChannelParticipant>() {
                                            public int compare(ChannelParticipant lhs, ChannelParticipant rhs) {
                                                int type1 = ChannelUsersActivity.this.getChannelAdminParticipantType(lhs);
                                                int type2 = ChannelUsersActivity.this.getChannelAdminParticipantType(rhs);
                                                if (type1 > type2) {
                                                    return 1;
                                                }
                                                if (type1 < type2) {
                                                    return -1;
                                                }
                                                return 0;
                                            }
                                        });
                                    }
                                } catch (Throwable e) {
                                    FileLog.e(e);
                                }
                            }
                            ChannelUsersActivity.this.loadingUsers = false;
                            ChannelUsersActivity.this.firstLoaded = true;
                            if (ChannelUsersActivity.this.emptyView != null) {
                                ChannelUsersActivity.this.emptyView.showTextView();
                            }
                            if (ChannelUsersActivity.this.listViewAdapter != null) {
                                ChannelUsersActivity.this.listViewAdapter.notifyDataSetChanged();
                            }
                        }
                    });
                }
            }), this.classGuid);
        }
    }

    public void onResume() {
        super.onResume();
        if (this.listViewAdapter != null) {
            this.listViewAdapter.notifyDataSetChanged();
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        ThemeDescriptionDelegate сellDelegate = new ThemeDescriptionDelegate() {
            public void didSetColor(int color) {
                int count = ChannelUsersActivity.this.listView.getChildCount();
                for (int a = 0; a < count; a++) {
                    View child = ChannelUsersActivity.this.listView.getChildAt(a);
                    if (child instanceof UserCell) {
                        ((UserCell) child).update(0);
                    }
                }
            }
        };
        ThemeDescription[] themeDescriptionArr = new ThemeDescription[32];
        themeDescriptionArr[0] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{UserCell.class, TextSettingsCell.class, TextCell.class, RadioCell.class}, null, null, null, Theme.key_windowBackgroundWhite);
        themeDescriptionArr[1] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundGray);
        themeDescriptionArr[2] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault);
        themeDescriptionArr[3] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault);
        themeDescriptionArr[4] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon);
        themeDescriptionArr[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle);
        themeDescriptionArr[6] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector);
        themeDescriptionArr[7] = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector);
        themeDescriptionArr[8] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider);
        themeDescriptionArr[9] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow);
        themeDescriptionArr[10] = new ThemeDescription(this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText4);
        themeDescriptionArr[11] = new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        themeDescriptionArr[12] = new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteValueText);
        themeDescriptionArr[13] = new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueImageView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayIcon);
        themeDescriptionArr[14] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow);
        themeDescriptionArr[15] = new ThemeDescription(this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlueHeader);
        themeDescriptionArr[16] = new ThemeDescription(this.listView, 0, new Class[]{RadioCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        themeDescriptionArr[17] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKBOX, new Class[]{RadioCell.class}, new String[]{"radioButton"}, null, null, null, Theme.key_radioBackground);
        themeDescriptionArr[18] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{RadioCell.class}, new String[]{"radioButton"}, null, null, null, Theme.key_radioBackgroundChecked);
        themeDescriptionArr[19] = new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, new String[]{"nameTextView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        themeDescriptionArr[20] = new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, new String[]{"statusColor"}, null, null, сellDelegate, Theme.key_windowBackgroundWhiteGrayText);
        themeDescriptionArr[21] = new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, new String[]{"statusOnlineColor"}, null, null, сellDelegate, Theme.key_windowBackgroundWhiteBlueText);
        themeDescriptionArr[22] = new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, null, new Drawable[]{Theme.avatar_photoDrawable, Theme.avatar_broadcastDrawable}, null, Theme.key_avatar_text);
        themeDescriptionArr[23] = new ThemeDescription(null, 0, null, null, null, сellDelegate, Theme.key_avatar_backgroundRed);
        themeDescriptionArr[24] = new ThemeDescription(null, 0, null, null, null, сellDelegate, Theme.key_avatar_backgroundOrange);
        themeDescriptionArr[25] = new ThemeDescription(null, 0, null, null, null, сellDelegate, Theme.key_avatar_backgroundViolet);
        themeDescriptionArr[26] = new ThemeDescription(null, 0, null, null, null, сellDelegate, Theme.key_avatar_backgroundGreen);
        themeDescriptionArr[27] = new ThemeDescription(null, 0, null, null, null, сellDelegate, Theme.key_avatar_backgroundCyan);
        themeDescriptionArr[28] = new ThemeDescription(null, 0, null, null, null, сellDelegate, Theme.key_avatar_backgroundBlue);
        themeDescriptionArr[29] = new ThemeDescription(null, 0, null, null, null, сellDelegate, Theme.key_avatar_backgroundPink);
        themeDescriptionArr[30] = new ThemeDescription(this.listView, 0, new Class[]{TextCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        themeDescriptionArr[31] = new ThemeDescription(this.listView, 0, new Class[]{TextCell.class}, new String[]{"imageView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayIcon);
        return themeDescriptionArr;
    }
}
