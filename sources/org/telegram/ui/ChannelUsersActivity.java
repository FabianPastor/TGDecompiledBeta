package org.telegram.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.support.widget.RecyclerView.Adapter;
import org.telegram.messenger.support.widget.RecyclerView.LayoutParams;
import org.telegram.messenger.support.widget.RecyclerView.OnScrollListener;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.messenger.support.widget.helper.ItemTouchHelper.Callback;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.ChannelParticipant;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.ChatFull;
import org.telegram.tgnet.TLRPC.TL_channelAdminRights;
import org.telegram.tgnet.TLRPC.TL_channelBannedRights;
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
import org.telegram.tgnet.TLRPC.TL_contact;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.Updates;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate;
import org.telegram.ui.Adapters.SearchAdapterHelper;
import org.telegram.ui.Adapters.SearchAdapterHelper.HashtagObject;
import org.telegram.ui.Adapters.SearchAdapterHelper.SearchAdapterHelperDelegate;
import org.telegram.ui.Cells.GraySectionCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.ManageChatTextCell;
import org.telegram.ui.Cells.ManageChatUserCell;
import org.telegram.ui.Cells.ManageChatUserCell.ManageChatUserCellDelegate;
import org.telegram.ui.Cells.RadioCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.ChannelRightsEditActivity.ChannelRightsEditActivityDelegate;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;
import org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;
import org.telegram.ui.ContactsActivity.ContactsActivityDelegate;

public class ChannelUsersActivity extends BaseFragment implements NotificationCenterDelegate {
    private static final int search_button = 0;
    private int addNew2Row;
    private int addNewRow;
    private int addNewSectionRow;
    private int blockedEmptyRow;
    private int changeAddHeaderRow;
    private int changeAddRadio1Row;
    private int changeAddRadio2Row;
    private int changeAddSectionRow;
    private int chatId = this.arguments.getInt("chat_id");
    private Chat currentChat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.chatId));
    private EmptyTextProgressView emptyView;
    private boolean firstEndReached;
    private boolean firstLoaded;
    private RecyclerListView listView;
    private ListAdapter listViewAdapter;
    private boolean loadingUsers;
    private boolean needOpenSearch = this.arguments.getBoolean("open_search");
    private ArrayList<ChannelParticipant> participants = new ArrayList();
    private ArrayList<ChannelParticipant> participants2 = new ArrayList();
    private int participants2EndRow;
    private int participants2StartRow;
    private int participantsDividerRow;
    private int participantsEndRow;
    private int participantsInfoRow;
    private SparseArray<ChannelParticipant> participantsMap = new SparseArray();
    private int participantsStartRow;
    private int restricted1SectionRow;
    private int restricted2SectionRow;
    private int rowCount;
    private ActionBarMenuItem searchItem;
    private SearchAdapter searchListViewAdapter;
    private boolean searchWas;
    private boolean searching;
    private int selectType = this.arguments.getInt("selectType");
    private int type = this.arguments.getInt("type");

    /* renamed from: org.telegram.ui.ChannelUsersActivity$8 */
    class C10118 implements Runnable {
        C10118() {
        }

        public void run() {
            ChannelUsersActivity.this.firstEndReached = false;
            ChannelUsersActivity.this.getChannelParticipants(0, Callback.DEFAULT_DRAG_ANIMATION_DURATION);
        }
    }

    /* renamed from: org.telegram.ui.ChannelUsersActivity$1 */
    class C19961 extends ActionBarMenuOnItemClick {
        C19961() {
        }

        public void onItemClick(int id) {
            if (id == -1) {
                ChannelUsersActivity.this.finishFragment();
            }
        }
    }

    /* renamed from: org.telegram.ui.ChannelUsersActivity$2 */
    class C19972 extends ActionBarMenuItemSearchListener {
        C19972() {
        }

        public void onSearchExpand() {
            ChannelUsersActivity.this.searching = true;
            ChannelUsersActivity.this.emptyView.setShowAtCenter(true);
        }

        public void onSearchCollapse() {
            ChannelUsersActivity.this.searchListViewAdapter.searchDialogs(null);
            ChannelUsersActivity.this.searching = false;
            ChannelUsersActivity.this.searchWas = false;
            ChannelUsersActivity.this.listView.setAdapter(ChannelUsersActivity.this.listViewAdapter);
            ChannelUsersActivity.this.listViewAdapter.notifyDataSetChanged();
            ChannelUsersActivity.this.listView.setFastScrollVisible(true);
            ChannelUsersActivity.this.listView.setVerticalScrollBarEnabled(false);
            ChannelUsersActivity.this.emptyView.setShowAtCenter(false);
        }

        public void onTextChanged(EditText editText) {
            if (ChannelUsersActivity.this.searchListViewAdapter != null) {
                String text = editText.getText().toString();
                if (text.length() != 0) {
                    ChannelUsersActivity.this.searchWas = true;
                    if (ChannelUsersActivity.this.listView != null) {
                        ChannelUsersActivity.this.listView.setAdapter(ChannelUsersActivity.this.searchListViewAdapter);
                        ChannelUsersActivity.this.searchListViewAdapter.notifyDataSetChanged();
                        ChannelUsersActivity.this.listView.setFastScrollVisible(false);
                        ChannelUsersActivity.this.listView.setVerticalScrollBarEnabled(true);
                    }
                }
                ChannelUsersActivity.this.searchListViewAdapter.searchDialogs(text);
            }
        }
    }

    /* renamed from: org.telegram.ui.ChannelUsersActivity$3 */
    class C20013 implements OnItemClickListener {

        /* renamed from: org.telegram.ui.ChannelUsersActivity$3$1 */
        class C19981 implements ContactsActivityDelegate {
            C19981() {
            }

            public void didSelectContact(User user, String param, ContactsActivity activity) {
                MessagesController.getInstance(ChannelUsersActivity.this.currentAccount).addUserToChat(ChannelUsersActivity.this.chatId, user, null, param != null ? Utilities.parseInt(param).intValue() : 0, null, ChannelUsersActivity.this);
            }
        }

        C20013() {
        }

        public void onItemClick(View view, int position) {
            int i = position;
            if (i == ChannelUsersActivity.this.addNewRow) {
                Bundle bundle;
                if (ChannelUsersActivity.this.type == 0) {
                    bundle = new Bundle();
                    bundle.putInt("chat_id", ChannelUsersActivity.this.chatId);
                    bundle.putInt("type", 2);
                    bundle.putInt("selectType", 2);
                    ChannelUsersActivity.this.presentFragment(new ChannelUsersActivity(bundle));
                } else if (ChannelUsersActivity.this.type == 1) {
                    bundle = new Bundle();
                    bundle.putInt("chat_id", ChannelUsersActivity.this.chatId);
                    bundle.putInt("type", 2);
                    bundle.putInt("selectType", 1);
                    ChannelUsersActivity.this.presentFragment(new ChannelUsersActivity(bundle));
                } else if (ChannelUsersActivity.this.type == 2) {
                    bundle = new Bundle();
                    bundle.putBoolean("onlyUsers", true);
                    bundle.putBoolean("destroyAfterSelect", true);
                    bundle.putBoolean("returnAsResult", true);
                    bundle.putBoolean("needForwardCount", false);
                    bundle.putString("selectAlertString", LocaleController.getString("ChannelAddTo", R.string.ChannelAddTo));
                    ContactsActivity fragment = new ContactsActivity(bundle);
                    fragment.setDelegate(new C19981());
                    ChannelUsersActivity.this.presentFragment(fragment);
                }
            } else if (i == ChannelUsersActivity.this.addNew2Row) {
                ChannelUsersActivity.this.presentFragment(new GroupInviteActivity(ChannelUsersActivity.this.chatId));
            } else {
                int user_id;
                boolean z;
                if (i != ChannelUsersActivity.this.changeAddRadio1Row) {
                    if (i != ChannelUsersActivity.this.changeAddRadio2Row) {
                        ChannelParticipant participant;
                        TL_channelBannedRights banned_rights = null;
                        TL_channelAdminRights admin_rights = null;
                        user_id = 0;
                        boolean canEditAdmin = false;
                        if (ChannelUsersActivity.this.listView.getAdapter() == ChannelUsersActivity.this.listViewAdapter) {
                            participant = ChannelUsersActivity.this.listViewAdapter.getItem(i);
                            if (participant != null) {
                                user_id = participant.user_id;
                                banned_rights = participant.banned_rights;
                                admin_rights = participant.admin_rights;
                                z = !((participant instanceof TL_channelParticipantAdmin) || (participant instanceof TL_channelParticipantCreator)) || participant.can_edit;
                                canEditAdmin = z;
                                if (participant instanceof TL_channelParticipantCreator) {
                                    admin_rights = new TL_channelAdminRights();
                                    admin_rights.add_admins = true;
                                    admin_rights.pin_messages = true;
                                    admin_rights.invite_link = true;
                                    admin_rights.invite_users = true;
                                    admin_rights.ban_users = true;
                                    admin_rights.delete_messages = true;
                                    admin_rights.edit_messages = true;
                                    admin_rights.post_messages = true;
                                    admin_rights.change_info = true;
                                }
                            }
                        } else {
                            ChannelParticipant participant2;
                            TLObject object = ChannelUsersActivity.this.searchListViewAdapter.getItem(i);
                            if (object instanceof User) {
                                User user = (User) object;
                                MessagesController.getInstance(ChannelUsersActivity.this.currentAccount).putUser(user, false);
                                SparseArray access$1600 = ChannelUsersActivity.this.participantsMap;
                                int i2 = user.id;
                                user_id = i2;
                                participant2 = (ChannelParticipant) access$1600.get(i2);
                            } else if (object instanceof ChannelParticipant) {
                                participant2 = (ChannelParticipant) object;
                            } else {
                                participant2 = null;
                            }
                            if (participant2 != null) {
                                user_id = participant2.user_id;
                                boolean z2 = !((participant2 instanceof TL_channelParticipantAdmin) || (participant2 instanceof TL_channelParticipantCreator)) || participant2.can_edit;
                                canEditAdmin = z2;
                                banned_rights = participant2.banned_rights;
                                admin_rights = participant2.admin_rights;
                            } else {
                                canEditAdmin = true;
                            }
                            participant = participant2;
                        }
                        if (user_id != 0) {
                            ChannelRightsEditActivity channelRightsEditActivity;
                            if (ChannelUsersActivity.this.selectType != 0) {
                                if (!ChannelUsersActivity.this.currentChat.megagroup) {
                                    if (ChannelUsersActivity.this.selectType != 1) {
                                        MessagesController.getInstance(ChannelUsersActivity.this.currentAccount).deleteUserFromChat(ChannelUsersActivity.this.chatId, MessagesController.getInstance(ChannelUsersActivity.this.currentAccount).getUser(Integer.valueOf(user_id)), null);
                                        ChannelUsersActivity.this.finishFragment();
                                    }
                                }
                                channelRightsEditActivity = new ChannelRightsEditActivity(user_id, ChannelUsersActivity.this.chatId, admin_rights, banned_rights, ChannelUsersActivity.this.selectType == 1 ? 0 : 1, canEditAdmin);
                                channelRightsEditActivity.setDelegate(new ChannelRightsEditActivityDelegate() {
                                    public void didSetRights(int rights, TL_channelAdminRights rightsAdmin, TL_channelBannedRights rightsBanned) {
                                        if (participant != null) {
                                            participant.admin_rights = rightsAdmin;
                                            participant.banned_rights = rightsBanned;
                                            ChannelParticipant p = (ChannelParticipant) ChannelUsersActivity.this.participantsMap.get(participant.user_id);
                                            if (p != null) {
                                                p.admin_rights = rightsAdmin;
                                                p.banned_rights = rightsBanned;
                                            }
                                        }
                                        ChannelUsersActivity.this.removeSelfFromStack();
                                    }
                                });
                                ChannelUsersActivity.this.presentFragment(channelRightsEditActivity);
                            } else {
                                z = false;
                                if (ChannelUsersActivity.this.type == 1) {
                                    boolean z3 = user_id != UserConfig.getInstance(ChannelUsersActivity.this.currentAccount).getClientUserId() && (ChannelUsersActivity.this.currentChat.creator || canEditAdmin);
                                    z = z3;
                                } else if (ChannelUsersActivity.this.type == 0) {
                                    z = ChatObject.canBlockUsers(ChannelUsersActivity.this.currentChat);
                                }
                                if ((ChannelUsersActivity.this.type == 1 || ChannelUsersActivity.this.currentChat.megagroup) && !(ChannelUsersActivity.this.type == 2 && ChannelUsersActivity.this.selectType == 0)) {
                                    if (banned_rights == null) {
                                        banned_rights = new TL_channelBannedRights();
                                        banned_rights.view_messages = true;
                                        banned_rights.send_stickers = true;
                                        banned_rights.send_media = true;
                                        banned_rights.embed_links = true;
                                        banned_rights.send_messages = true;
                                        banned_rights.send_games = true;
                                        banned_rights.send_inline = true;
                                        banned_rights.send_gifs = true;
                                    }
                                    channelRightsEditActivity = new ChannelRightsEditActivity(user_id, ChannelUsersActivity.this.chatId, admin_rights, banned_rights, ChannelUsersActivity.this.type == 1 ? 0 : 1, z);
                                    channelRightsEditActivity.setDelegate(new ChannelRightsEditActivityDelegate() {
                                        public void didSetRights(int rights, TL_channelAdminRights rightsAdmin, TL_channelBannedRights rightsBanned) {
                                            if (participant != null) {
                                                participant.admin_rights = rightsAdmin;
                                                participant.banned_rights = rightsBanned;
                                                ChannelParticipant p = (ChannelParticipant) ChannelUsersActivity.this.participantsMap.get(participant.user_id);
                                                if (p != null) {
                                                    p.admin_rights = rightsAdmin;
                                                    p.banned_rights = rightsBanned;
                                                }
                                            }
                                        }
                                    });
                                    ChannelUsersActivity.this.presentFragment(channelRightsEditActivity);
                                } else {
                                    Bundle args = new Bundle();
                                    args.putInt("user_id", user_id);
                                    ChannelUsersActivity.this.presentFragment(new ProfileActivity(args));
                                }
                            }
                        }
                    }
                }
                Chat chat = MessagesController.getInstance(ChannelUsersActivity.this.currentAccount).getChat(Integer.valueOf(ChannelUsersActivity.this.chatId));
                if (chat != null) {
                    boolean changed = false;
                    if (i == 1 && !chat.democracy) {
                        chat.democracy = true;
                        changed = true;
                    } else if (i == 2 && chat.democracy) {
                        chat.democracy = false;
                        changed = true;
                    }
                    if (changed) {
                        MessagesController.getInstance(ChannelUsersActivity.this.currentAccount).toogleChannelInvites(ChannelUsersActivity.this.chatId, chat.democracy);
                        int count = ChannelUsersActivity.this.listView.getChildCount();
                        for (user_id = 0; user_id < count; user_id++) {
                            View child = ChannelUsersActivity.this.listView.getChildAt(user_id);
                            if (child instanceof RadioCell) {
                                int num = ((Integer) child.getTag()).intValue();
                                RadioCell radioCell = (RadioCell) child;
                                z = (num == 0 && chat.democracy) || (num == 1 && !chat.democracy);
                                radioCell.setChecked(z, true);
                            }
                        }
                    }
                }
            }
        }
    }

    /* renamed from: org.telegram.ui.ChannelUsersActivity$4 */
    class C20024 implements OnItemLongClickListener {
        C20024() {
        }

        public boolean onItemClick(View view, int position) {
            return ChannelUsersActivity.this.getParentActivity() != null && ChannelUsersActivity.this.listView.getAdapter() == ChannelUsersActivity.this.listViewAdapter && ChannelUsersActivity.this.createMenuForParticipant(ChannelUsersActivity.this.listViewAdapter.getItem(position), false);
        }
    }

    /* renamed from: org.telegram.ui.ChannelUsersActivity$5 */
    class C20035 extends OnScrollListener {
        C20035() {
        }

        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            if (newState == 1 && ChannelUsersActivity.this.searching && ChannelUsersActivity.this.searchWas) {
                AndroidUtilities.hideKeyboard(ChannelUsersActivity.this.getParentActivity().getCurrentFocus());
            }
        }

        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        }
    }

    private class ListAdapter extends SelectionAdapter {
        private Context mContext;

        /* renamed from: org.telegram.ui.ChannelUsersActivity$ListAdapter$1 */
        class C20071 implements ManageChatUserCellDelegate {
            C20071() {
            }

            public boolean onOptionsButtonCheck(ManageChatUserCell cell, boolean click) {
                return ChannelUsersActivity.this.createMenuForParticipant(ChannelUsersActivity.this.listViewAdapter.getItem(((Integer) cell.getTag()).intValue()), click ^ 1);
            }
        }

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean isEnabled(ViewHolder holder) {
            int type = holder.getItemViewType();
            if (!(type == 0 || type == 2)) {
                if (type != 6) {
                    return false;
                }
            }
            return true;
        }

        public int getItemCount() {
            if (!ChannelUsersActivity.this.loadingUsers || ChannelUsersActivity.this.firstLoaded) {
                return ChannelUsersActivity.this.rowCount;
            }
            return 0;
        }

        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            ListAdapter listAdapter = this;
            boolean z = true;
            switch (viewType) {
                case 0:
                    Context context = listAdapter.mContext;
                    int i = ChannelUsersActivity.this.type == 0 ? 8 : 1;
                    if (ChannelUsersActivity.this.selectType != 0) {
                        z = false;
                    }
                    view = new ManageChatUserCell(context, i, z);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    ((ManageChatUserCell) view).setDelegate(new C20071());
                    break;
                case 1:
                    view = new TextInfoPrivacyCell(listAdapter.mContext);
                    break;
                case 2:
                    view = new ManageChatTextCell(listAdapter.mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 3:
                    view = new ShadowSectionCell(listAdapter.mContext);
                    break;
                case 4:
                    View view2 = new FrameLayout(listAdapter.mContext) {
                        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                            super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(heightMeasureSpec) - AndroidUtilities.dp(56.0f), NUM));
                        }
                    };
                    FrameLayout frameLayout = (FrameLayout) view2;
                    frameLayout.setBackgroundDrawable(Theme.getThemedDrawable(listAdapter.mContext, R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                    LinearLayout linearLayout = new LinearLayout(listAdapter.mContext);
                    linearLayout.setOrientation(1);
                    frameLayout.addView(linearLayout, LayoutHelper.createFrame(-2, -2.0f, 17, 20.0f, 0.0f, 20.0f, 0.0f));
                    ImageView imageView = new ImageView(listAdapter.mContext);
                    imageView.setImageResource(R.drawable.group_ban_empty);
                    imageView.setScaleType(ScaleType.CENTER);
                    imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_emptyListPlaceholder), Mode.MULTIPLY));
                    linearLayout.addView(imageView, LayoutHelper.createLinear(-2, -2, 1));
                    TextView textView = new TextView(listAdapter.mContext);
                    textView.setText(LocaleController.getString("NoBlockedUsers", R.string.NoBlockedUsers));
                    textView.setTextColor(Theme.getColor(Theme.key_emptyListPlaceholder));
                    textView.setTextSize(1, 16.0f);
                    textView.setGravity(1);
                    textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                    linearLayout.addView(textView, LayoutHelper.createLinear(-2, -2, 1, 0, 10, 0, 0));
                    textView = new TextView(listAdapter.mContext);
                    if (ChannelUsersActivity.this.currentChat.megagroup) {
                        textView.setText(LocaleController.getString("NoBlockedGroup", R.string.NoBlockedGroup));
                    } else {
                        textView.setText(LocaleController.getString("NoBlockedChannel", R.string.NoBlockedChannel));
                    }
                    textView.setTextColor(Theme.getColor(Theme.key_emptyListPlaceholder));
                    textView.setTextSize(1, 15.0f);
                    textView.setGravity(1);
                    linearLayout.addView(textView, LayoutHelper.createLinear(-2, -2, 1, 0, 10, 0, 0));
                    view2.setLayoutParams(new LayoutParams(-1, -1));
                    view = view2;
                    break;
                case 5:
                    view = new HeaderCell(listAdapter.mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                default:
                    view = new RadioCell(listAdapter.mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
            }
            return new Holder(view);
        }

        public void onBindViewHolder(ViewHolder holder, int position) {
            boolean z = false;
            boolean z2 = true;
            switch (holder.getItemViewType()) {
                case 0:
                    ManageChatUserCell userCell = holder.itemView;
                    userCell.setTag(Integer.valueOf(position));
                    ChannelParticipant participant = getItem(position);
                    User user = MessagesController.getInstance(ChannelUsersActivity.this.currentAccount).getUser(Integer.valueOf(participant.user_id));
                    if (user == null) {
                        return;
                    }
                    String role;
                    if (ChannelUsersActivity.this.type == 0) {
                        role = null;
                        if ((participant instanceof TL_channelParticipantBanned) && MessagesController.getInstance(ChannelUsersActivity.this.currentAccount).getUser(Integer.valueOf(participant.kicked_by)) != null) {
                            role = LocaleController.formatString("UserRestrictionsBy", R.string.UserRestrictionsBy, ContactsController.formatName(MessagesController.getInstance(ChannelUsersActivity.this.currentAccount).getUser(Integer.valueOf(participant.kicked_by)).first_name, MessagesController.getInstance(ChannelUsersActivity.this.currentAccount).getUser(Integer.valueOf(participant.kicked_by)).last_name));
                        }
                        userCell.setData(user, null, role);
                        return;
                    } else if (ChannelUsersActivity.this.type == 1) {
                        role = null;
                        if (!(participant instanceof TL_channelParticipantCreator)) {
                            if (!(participant instanceof TL_channelParticipantSelf)) {
                                if ((participant instanceof TL_channelParticipantAdmin) && MessagesController.getInstance(ChannelUsersActivity.this.currentAccount).getUser(Integer.valueOf(participant.promoted_by)) != null) {
                                    role = LocaleController.formatString("EditAdminPromotedBy", R.string.EditAdminPromotedBy, ContactsController.formatName(MessagesController.getInstance(ChannelUsersActivity.this.currentAccount).getUser(Integer.valueOf(participant.promoted_by)).first_name, MessagesController.getInstance(ChannelUsersActivity.this.currentAccount).getUser(Integer.valueOf(participant.promoted_by)).last_name));
                                }
                                userCell.setData(user, null, role);
                                return;
                            }
                        }
                        role = LocaleController.getString("ChannelCreator", R.string.ChannelCreator);
                        userCell.setData(user, null, role);
                        return;
                    } else if (ChannelUsersActivity.this.type == 2) {
                        userCell.setData(user, null, null);
                        return;
                    } else {
                        return;
                    }
                case 1:
                    TextInfoPrivacyCell privacyCell = holder.itemView;
                    if (position != ChannelUsersActivity.this.participantsInfoRow) {
                        return;
                    }
                    if (ChannelUsersActivity.this.type == 0) {
                        if (ChatObject.canBlockUsers(ChannelUsersActivity.this.currentChat)) {
                            if (ChannelUsersActivity.this.currentChat.megagroup) {
                                privacyCell.setText(String.format("%1$s\n\n%2$s", new Object[]{LocaleController.getString("NoBlockedGroup", R.string.NoBlockedGroup), LocaleController.getString("UnbanText", R.string.UnbanText)}));
                            } else {
                                privacyCell.setText(String.format("%1$s\n\n%2$s", new Object[]{LocaleController.getString("NoBlockedChannel", R.string.NoBlockedChannel), LocaleController.getString("UnbanText", R.string.UnbanText)}));
                            }
                        } else if (ChannelUsersActivity.this.currentChat.megagroup) {
                            privacyCell.setText(LocaleController.getString("NoBlockedGroup", R.string.NoBlockedGroup));
                        } else {
                            privacyCell.setText(LocaleController.getString("NoBlockedChannel", R.string.NoBlockedChannel));
                        }
                        privacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                        return;
                    } else if (ChannelUsersActivity.this.type == 1) {
                        if (ChannelUsersActivity.this.addNewRow != -1) {
                            if (ChannelUsersActivity.this.currentChat.megagroup) {
                                privacyCell.setText(LocaleController.getString("MegaAdminsInfo", R.string.MegaAdminsInfo));
                            } else {
                                privacyCell.setText(LocaleController.getString("ChannelAdminsInfo", R.string.ChannelAdminsInfo));
                            }
                            privacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                            return;
                        }
                        privacyCell.setText(TtmlNode.ANONYMOUS_REGION_ID);
                        privacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                        return;
                    } else if (ChannelUsersActivity.this.type == 2) {
                        if (!ChannelUsersActivity.this.currentChat.megagroup) {
                            if (ChannelUsersActivity.this.selectType == 0) {
                                privacyCell.setText(LocaleController.getString("ChannelMembersInfo", R.string.ChannelMembersInfo));
                                privacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                                return;
                            }
                        }
                        privacyCell.setText(TtmlNode.ANONYMOUS_REGION_ID);
                        privacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                        return;
                    } else {
                        return;
                    }
                case 2:
                    ManageChatTextCell actionCell = holder.itemView;
                    if (position == ChannelUsersActivity.this.addNewRow) {
                        if (ChannelUsersActivity.this.type == 0) {
                            actionCell.setText(LocaleController.getString("ChannelBlockUser", R.string.ChannelBlockUser), null, R.drawable.group_ban_new, false);
                            return;
                        } else if (ChannelUsersActivity.this.type == 1) {
                            actionCell.setText(LocaleController.getString("ChannelAddAdmin", R.string.ChannelAddAdmin), null, R.drawable.group_admin_new, false);
                            return;
                        } else if (ChannelUsersActivity.this.type != 2) {
                            return;
                        } else {
                            if (!ChatObject.isChannel(ChannelUsersActivity.this.currentChat) || ChannelUsersActivity.this.currentChat.megagroup) {
                                actionCell.setText(LocaleController.getString("AddMember", R.string.AddMember), null, R.drawable.menu_invite, true);
                                return;
                            } else {
                                actionCell.setText(LocaleController.getString("AddSubscriber", R.string.AddSubscriber), null, R.drawable.menu_invite, true);
                                return;
                            }
                        }
                    } else if (position == ChannelUsersActivity.this.addNew2Row) {
                        actionCell.setText(LocaleController.getString("ChannelInviteViaLink", R.string.ChannelInviteViaLink), null, R.drawable.msg_panel_link, false);
                        return;
                    } else {
                        return;
                    }
                case 5:
                    HeaderCell headerCell = holder.itemView;
                    if (position == ChannelUsersActivity.this.restricted1SectionRow) {
                        headerCell.setText(LocaleController.getString("ChannelRestrictedUsers", R.string.ChannelRestrictedUsers));
                        return;
                    } else if (position == ChannelUsersActivity.this.restricted2SectionRow) {
                        headerCell.setText(LocaleController.getString("ChannelBlockedUsers", R.string.ChannelBlockedUsers));
                        return;
                    } else if (position == ChannelUsersActivity.this.changeAddHeaderRow) {
                        headerCell.setText(LocaleController.getString("WhoCanAddMembers", R.string.WhoCanAddMembers));
                        return;
                    } else {
                        return;
                    }
                case 6:
                    RadioCell radioCell = holder.itemView;
                    Chat chat = MessagesController.getInstance(ChannelUsersActivity.this.currentAccount).getChat(Integer.valueOf(ChannelUsersActivity.this.chatId));
                    String string;
                    if (position == ChannelUsersActivity.this.changeAddRadio1Row) {
                        radioCell.setTag(Integer.valueOf(0));
                        string = LocaleController.getString("WhoCanAddMembersAllMembers", R.string.WhoCanAddMembersAllMembers);
                        if (chat != null && chat.democracy) {
                            z = true;
                        }
                        radioCell.setText(string, z, true);
                        return;
                    } else if (position == ChannelUsersActivity.this.changeAddRadio2Row) {
                        radioCell.setTag(Integer.valueOf(1));
                        string = LocaleController.getString("WhoCanAddMembersAdmins", R.string.WhoCanAddMembersAdmins);
                        if (chat == null || chat.democracy) {
                            z2 = false;
                        }
                        radioCell.setText(string, z2, false);
                        return;
                    } else {
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
            if (position != ChannelUsersActivity.this.addNewRow) {
                if (position != ChannelUsersActivity.this.addNew2Row) {
                    if ((position >= ChannelUsersActivity.this.participantsStartRow && position < ChannelUsersActivity.this.participantsEndRow) || (position >= ChannelUsersActivity.this.participants2StartRow && position < ChannelUsersActivity.this.participants2EndRow)) {
                        return 0;
                    }
                    if (!(position == ChannelUsersActivity.this.addNewSectionRow || position == ChannelUsersActivity.this.changeAddSectionRow)) {
                        if (position != ChannelUsersActivity.this.participantsDividerRow) {
                            if (position == ChannelUsersActivity.this.participantsInfoRow) {
                                return 1;
                            }
                            if (!(position == ChannelUsersActivity.this.changeAddHeaderRow || position == ChannelUsersActivity.this.restricted1SectionRow)) {
                                if (position != ChannelUsersActivity.this.restricted2SectionRow) {
                                    if (position != ChannelUsersActivity.this.changeAddRadio1Row) {
                                        if (position != ChannelUsersActivity.this.changeAddRadio2Row) {
                                            if (position == ChannelUsersActivity.this.blockedEmptyRow) {
                                                return 4;
                                            }
                                            return 0;
                                        }
                                    }
                                    return 6;
                                }
                            }
                            return 5;
                        }
                    }
                    return 3;
                }
            }
            return 2;
        }

        public ChannelParticipant getItem(int position) {
            if (ChannelUsersActivity.this.participantsStartRow != -1 && position >= ChannelUsersActivity.this.participantsStartRow && position < ChannelUsersActivity.this.participantsEndRow) {
                return (ChannelParticipant) ChannelUsersActivity.this.participants.get(position - ChannelUsersActivity.this.participantsStartRow);
            }
            if (ChannelUsersActivity.this.participants2StartRow == -1 || position < ChannelUsersActivity.this.participants2StartRow || position >= ChannelUsersActivity.this.participants2EndRow) {
                return null;
            }
            return (ChannelParticipant) ChannelUsersActivity.this.participants2.get(position - ChannelUsersActivity.this.participants2StartRow);
        }
    }

    private class SearchAdapter extends SelectionAdapter {
        private int contactsStartRow;
        private int globalStartRow;
        private int group2StartRow;
        private int groupStartRow;
        private Context mContext;
        private SearchAdapterHelper searchAdapterHelper;
        private ArrayList<User> searchResult = new ArrayList();
        private ArrayList<CharSequence> searchResultNames = new ArrayList();
        private Timer searchTimer;
        private int totalCount;

        /* renamed from: org.telegram.ui.ChannelUsersActivity$SearchAdapter$5 */
        class C20095 implements ManageChatUserCellDelegate {
            C20095() {
            }

            public boolean onOptionsButtonCheck(ManageChatUserCell cell, boolean click) {
                if (!(SearchAdapter.this.getItem(((Integer) cell.getTag()).intValue()) instanceof ChannelParticipant)) {
                    return false;
                }
                return ChannelUsersActivity.this.createMenuForParticipant((ChannelParticipant) SearchAdapter.this.getItem(((Integer) cell.getTag()).intValue()), click ^ 1);
            }
        }

        public SearchAdapter(Context context) {
            this.mContext = context;
            this.searchAdapterHelper = new SearchAdapterHelper(true);
            this.searchAdapterHelper.setDelegate(new SearchAdapterHelperDelegate(ChannelUsersActivity.this) {
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
                FileLog.m3e(e);
            }
            if (query == null) {
                this.searchResult.clear();
                this.searchResultNames.clear();
                this.searchAdapterHelper.queryServerSearch(null, ChannelUsersActivity.this.type != 0, false, true, true, ChannelUsersActivity.this.chatId, ChannelUsersActivity.this.type == 0);
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
                        FileLog.m3e(e);
                    }
                    SearchAdapter.this.processSearch(query);
                }
            }, 200, 300);
        }

        private void processSearch(final String query) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    SearchAdapter.this.searchAdapterHelper.queryServerSearch(query, ChannelUsersActivity.this.selectType != 0, false, true, true, ChannelUsersActivity.this.chatId, ChannelUsersActivity.this.type == 0);
                    if (ChannelUsersActivity.this.selectType == 1) {
                        final ArrayList<TL_contact> contactsCopy = new ArrayList();
                        contactsCopy.addAll(ContactsController.getInstance(ChannelUsersActivity.this.currentAccount).contacts);
                        Utilities.searchQueue.postRunnable(new Runnable() {
                            public void run() {
                                String search1 = query.trim().toLowerCase();
                                if (search1.length() == 0) {
                                    SearchAdapter.this.updateSearchResults(new ArrayList(), new ArrayList());
                                    return;
                                }
                                String search2 = LocaleController.getInstance().getTranslitString(search1);
                                if (search1.equals(search2) || search2.length() == 0) {
                                    search2 = null;
                                }
                                int i = 0;
                                String[] search = new String[((search2 != null ? 1 : 0) + 1)];
                                search[0] = search1;
                                if (search2 != null) {
                                    search[1] = search2;
                                }
                                ArrayList<User> resultArray = new ArrayList();
                                ArrayList<CharSequence> resultArrayNames = new ArrayList();
                                int a = 0;
                                while (a < contactsCopy.size()) {
                                    String search12;
                                    User user = MessagesController.getInstance(ChannelUsersActivity.this.currentAccount).getUser(Integer.valueOf(((TL_contact) contactsCopy.get(a)).user_id));
                                    if (user.id == UserConfig.getInstance(ChannelUsersActivity.this.currentAccount).getClientUserId()) {
                                        search12 = search1;
                                    } else {
                                        String name = ContactsController.formatName(user.first_name, user.last_name).toLowerCase();
                                        String tName = LocaleController.getInstance().getTranslitString(name);
                                        if (name.equals(tName)) {
                                            tName = null;
                                        }
                                        int length = search.length;
                                        int found = 0;
                                        int found2 = i;
                                        while (found2 < length) {
                                            StringBuilder stringBuilder;
                                            String stringBuilder2;
                                            StringBuilder stringBuilder3;
                                            String q = search[found2];
                                            if (name.startsWith(q)) {
                                                search12 = search1;
                                            } else {
                                                stringBuilder = new StringBuilder();
                                                search12 = search1;
                                                stringBuilder.append(" ");
                                                stringBuilder.append(q);
                                                if (name.contains(stringBuilder.toString()) == null) {
                                                    if (tName != null) {
                                                        if (tName.startsWith(q) == null) {
                                                            search1 = new StringBuilder();
                                                            search1.append(" ");
                                                            search1.append(q);
                                                            if (tName.contains(search1.toString()) != null) {
                                                            }
                                                        }
                                                    }
                                                    if (!(user.username == null || user.username.startsWith(q) == null)) {
                                                        search1 = 2;
                                                        found = search1;
                                                    }
                                                    if (found == 0) {
                                                        if (found != 1) {
                                                            resultArrayNames.add(AndroidUtilities.generateSearchName(user.first_name, user.last_name, q));
                                                        } else {
                                                            stringBuilder = new StringBuilder();
                                                            stringBuilder.append("@");
                                                            stringBuilder.append(user.username);
                                                            stringBuilder2 = stringBuilder.toString();
                                                            stringBuilder3 = new StringBuilder();
                                                            stringBuilder3.append("@");
                                                            stringBuilder3.append(q);
                                                            resultArrayNames.add(AndroidUtilities.generateSearchName(stringBuilder2, null, stringBuilder3.toString()));
                                                        }
                                                        resultArray.add(user);
                                                    } else {
                                                        found2++;
                                                        search1 = search12;
                                                    }
                                                }
                                            }
                                            search1 = true;
                                            found = search1;
                                            if (found == 0) {
                                                found2++;
                                                search1 = search12;
                                            } else {
                                                if (found != 1) {
                                                    stringBuilder = new StringBuilder();
                                                    stringBuilder.append("@");
                                                    stringBuilder.append(user.username);
                                                    stringBuilder2 = stringBuilder.toString();
                                                    stringBuilder3 = new StringBuilder();
                                                    stringBuilder3.append("@");
                                                    stringBuilder3.append(q);
                                                    resultArrayNames.add(AndroidUtilities.generateSearchName(stringBuilder2, null, stringBuilder3.toString()));
                                                } else {
                                                    resultArrayNames.add(AndroidUtilities.generateSearchName(user.first_name, user.last_name, q));
                                                }
                                                resultArray.add(user);
                                            }
                                        }
                                        search12 = search1;
                                    }
                                    a++;
                                    search1 = search12;
                                    i = 0;
                                }
                                SearchAdapter.this.updateSearchResults(resultArray, resultArrayNames);
                            }
                        });
                    }
                }
            });
        }

        private void updateSearchResults(final ArrayList<User> users, final ArrayList<CharSequence> names) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    SearchAdapter.this.searchResult = users;
                    SearchAdapter.this.searchResultNames = names;
                    SearchAdapter.this.notifyDataSetChanged();
                }
            });
        }

        public boolean isEnabled(ViewHolder holder) {
            return holder.getItemViewType() != 1;
        }

        public int getItemCount() {
            int contactsCount = this.searchResult.size();
            int globalCount = this.searchAdapterHelper.getGlobalSearch().size();
            int groupsCount = this.searchAdapterHelper.getGroupSearch().size();
            int groupsCount2 = this.searchAdapterHelper.getGroupSearch2().size();
            int count = 0;
            if (contactsCount != 0) {
                count = 0 + (contactsCount + 1);
            }
            if (globalCount != 0) {
                count += globalCount + 1;
            }
            if (groupsCount != 0) {
                count += groupsCount + 1;
            }
            if (groupsCount2 != 0) {
                return count + (groupsCount2 + 1);
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
            int count2 = this.searchAdapterHelper.getGroupSearch2().size();
            if (count2 != 0) {
                this.group2StartRow = this.totalCount;
                this.totalCount += count2 + 1;
            } else {
                this.group2StartRow = -1;
            }
            count2 = this.searchResult.size();
            if (count2 != 0) {
                this.contactsStartRow = this.totalCount;
                this.totalCount += count2 + 1;
            } else {
                this.contactsStartRow = -1;
            }
            count2 = this.searchAdapterHelper.getGlobalSearch().size();
            if (count2 != 0) {
                this.globalStartRow = this.totalCount;
                this.totalCount += count2 + 1;
            } else {
                this.globalStartRow = -1;
            }
            super.notifyDataSetChanged();
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
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
            count = this.searchAdapterHelper.getGroupSearch2().size();
            if (count != 0) {
                if (count + 1 <= i) {
                    i -= count + 1;
                } else if (i == 0) {
                    return null;
                } else {
                    return (TLObject) this.searchAdapterHelper.getGroupSearch2().get(i - 1);
                }
            }
            count = this.searchResult.size();
            if (count != 0) {
                if (count + 1 <= i) {
                    i -= count + 1;
                } else if (i == 0) {
                    return null;
                } else {
                    return (TLObject) this.searchResult.get(i - 1);
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
            if (viewType != 0) {
                view = new GraySectionCell(this.mContext);
            } else {
                view = new ManageChatUserCell(this.mContext, 2, ChannelUsersActivity.this.selectType == 0);
                view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                ((ManageChatUserCell) view).setDelegate(new C20095());
            }
            return new Holder(view);
        }

        public void onBindViewHolder(ViewHolder holder, int position) {
            Throwable e;
            String u;
            int idx;
            ManageChatUserCell userCell;
            SearchAdapter searchAdapter = this;
            ViewHolder viewHolder = holder;
            int position2 = position;
            switch (holder.getItemViewType()) {
                case 0:
                    User user;
                    TLObject object = getItem(position2);
                    if (object instanceof User) {
                        user = (User) object;
                    } else {
                        user = MessagesController.getInstance(ChannelUsersActivity.this.currentAccount).getUser(Integer.valueOf(((ChannelParticipant) object).user_id));
                    }
                    String un = user.username;
                    CharSequence username = null;
                    CharSequence name = null;
                    int count = searchAdapter.searchAdapterHelper.getGroupSearch().size();
                    boolean ok = false;
                    String nameSearch = null;
                    if (count != 0) {
                        if (count + 1 > position2) {
                            nameSearch = searchAdapter.searchAdapterHelper.getLastFoundChannel();
                            ok = true;
                        } else {
                            position2 -= count + 1;
                        }
                    }
                    if (!ok) {
                        count = searchAdapter.searchAdapterHelper.getGroupSearch2().size();
                        if (count != 0) {
                            if (count + 1 > position2) {
                                nameSearch = searchAdapter.searchAdapterHelper.getLastFoundChannel2();
                            } else {
                                position2 -= count + 1;
                            }
                        }
                    }
                    if (!ok) {
                        count = searchAdapter.searchResult.size();
                        if (count != 0) {
                            if (count + 1 > position2) {
                                ok = true;
                                name = (CharSequence) searchAdapter.searchResultNames.get(position2 - 1);
                                if (!(name == null || un == null || un.length() <= 0)) {
                                    String charSequence = name.toString();
                                    StringBuilder stringBuilder = new StringBuilder();
                                    stringBuilder.append("@");
                                    stringBuilder.append(un);
                                    if (charSequence.startsWith(stringBuilder.toString())) {
                                        username = name;
                                        name = null;
                                    }
                                }
                            } else {
                                position2 -= count + 1;
                            }
                        }
                    }
                    if (!ok) {
                        count = searchAdapter.searchAdapterHelper.getGlobalSearch().size();
                        if (count != 0 && count + 1 > position2) {
                            String foundUserName = searchAdapter.searchAdapterHelper.getLastFoundUsername();
                            if (foundUserName.startsWith("@")) {
                                foundUserName = foundUserName.substring(1);
                            }
                            try {
                                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
                                spannableStringBuilder.append("@");
                                spannableStringBuilder.append(un);
                                int indexOf = un.toLowerCase().indexOf(foundUserName);
                                int len = indexOf;
                                if (indexOf != -1) {
                                    indexOf = foundUserName.length();
                                    if (len == 0) {
                                        indexOf++;
                                    } else {
                                        len++;
                                    }
                                    int i = len;
                                    len = indexOf;
                                    indexOf = i;
                                    try {
                                        spannableStringBuilder.setSpan(new ForegroundColorSpan(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText4)), indexOf, indexOf + len, 33);
                                    } catch (Throwable e2) {
                                        e = e2;
                                        username = un;
                                        FileLog.m3e(e);
                                        if (nameSearch != null) {
                                            u = UserObject.getUserName(user);
                                            name = new SpannableStringBuilder(u);
                                            idx = u.toLowerCase().indexOf(nameSearch);
                                            if (idx != -1) {
                                                ((SpannableStringBuilder) name).setSpan(new ForegroundColorSpan(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText4)), idx, nameSearch.length() + idx, 33);
                                            }
                                        }
                                        userCell = viewHolder.itemView;
                                        userCell.setTag(Integer.valueOf(position2));
                                        userCell.setData(user, name, username);
                                        return;
                                    }
                                }
                                indexOf = len;
                                username = spannableStringBuilder;
                            } catch (Throwable e22) {
                                TLObject tLObject = object;
                                e = e22;
                                username = un;
                                FileLog.m3e(e);
                                if (nameSearch != null) {
                                    u = UserObject.getUserName(user);
                                    name = new SpannableStringBuilder(u);
                                    idx = u.toLowerCase().indexOf(nameSearch);
                                    if (idx != -1) {
                                        ((SpannableStringBuilder) name).setSpan(new ForegroundColorSpan(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText4)), idx, nameSearch.length() + idx, 33);
                                    }
                                }
                                userCell = viewHolder.itemView;
                                userCell.setTag(Integer.valueOf(position2));
                                userCell.setData(user, name, username);
                                return;
                            }
                            if (nameSearch != null) {
                                u = UserObject.getUserName(user);
                                name = new SpannableStringBuilder(u);
                                idx = u.toLowerCase().indexOf(nameSearch);
                                if (idx != -1) {
                                    ((SpannableStringBuilder) name).setSpan(new ForegroundColorSpan(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText4)), idx, nameSearch.length() + idx, 33);
                                }
                            }
                            userCell = viewHolder.itemView;
                            userCell.setTag(Integer.valueOf(position2));
                            userCell.setData(user, name, username);
                            return;
                        }
                    }
                    if (nameSearch != null) {
                        u = UserObject.getUserName(user);
                        name = new SpannableStringBuilder(u);
                        idx = u.toLowerCase().indexOf(nameSearch);
                        if (idx != -1) {
                            ((SpannableStringBuilder) name).setSpan(new ForegroundColorSpan(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText4)), idx, nameSearch.length() + idx, 33);
                        }
                    }
                    userCell = viewHolder.itemView;
                    userCell.setTag(Integer.valueOf(position2));
                    userCell.setData(user, name, username);
                    return;
                case 1:
                    GraySectionCell sectionCell = viewHolder.itemView;
                    if (position2 == searchAdapter.groupStartRow) {
                        if (ChannelUsersActivity.this.type == 0) {
                            sectionCell.setText(LocaleController.getString("ChannelRestrictedUsers", R.string.ChannelRestrictedUsers).toUpperCase());
                            return;
                        } else if (!ChatObject.isChannel(ChannelUsersActivity.this.currentChat) || ChannelUsersActivity.this.currentChat.megagroup) {
                            sectionCell.setText(LocaleController.getString("ChannelMembers", R.string.ChannelMembers).toUpperCase());
                            return;
                        } else {
                            ChannelUsersActivity.this.actionBar.setTitle(LocaleController.getString("ChannelSubscribers", R.string.ChannelSubscribers));
                            return;
                        }
                    } else if (position2 == searchAdapter.group2StartRow) {
                        sectionCell.setText(LocaleController.getString("ChannelBlockedUsers", R.string.ChannelBlockedUsers).toUpperCase());
                        return;
                    } else if (position2 == searchAdapter.globalStartRow) {
                        sectionCell.setText(LocaleController.getString("GlobalSearch", R.string.GlobalSearch).toUpperCase());
                        return;
                    } else if (position2 == searchAdapter.contactsStartRow) {
                        sectionCell.setText(LocaleController.getString("Contacts", R.string.Contacts).toUpperCase());
                        return;
                    } else {
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
            if (!(i == this.globalStartRow || i == this.groupStartRow || i == this.contactsStartRow)) {
                if (i != this.group2StartRow) {
                    return 0;
                }
            }
            return 1;
        }
    }

    public ChannelUsersActivity(Bundle args) {
        super(args);
    }

    private void updateRows() {
        this.currentChat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.chatId));
        if (this.currentChat != null) {
            this.changeAddHeaderRow = -1;
            this.changeAddRadio1Row = -1;
            this.changeAddRadio2Row = -1;
            this.changeAddSectionRow = -1;
            this.addNewRow = -1;
            this.addNew2Row = -1;
            this.addNewSectionRow = -1;
            this.restricted1SectionRow = -1;
            this.participantsStartRow = -1;
            this.participantsDividerRow = -1;
            this.participantsEndRow = -1;
            this.restricted2SectionRow = -1;
            this.participants2StartRow = -1;
            this.participants2EndRow = -1;
            this.participantsInfoRow = -1;
            this.blockedEmptyRow = -1;
            this.rowCount = 0;
            int i;
            if (this.type == 0) {
                int i2;
                if (ChatObject.canBlockUsers(this.currentChat)) {
                    i2 = this.rowCount;
                    this.rowCount = i2 + 1;
                    this.addNewRow = i2;
                    if (!(this.participants.isEmpty() && this.participants2.isEmpty())) {
                        i2 = this.rowCount;
                        this.rowCount = i2 + 1;
                        this.addNewSectionRow = i2;
                    }
                } else {
                    this.addNewRow = -1;
                    this.addNewSectionRow = -1;
                }
                if (!this.participants.isEmpty()) {
                    i2 = this.rowCount;
                    this.rowCount = i2 + 1;
                    this.restricted1SectionRow = i2;
                    this.participantsStartRow = this.rowCount;
                    this.rowCount += this.participants.size();
                    this.participantsEndRow = this.rowCount;
                }
                if (!this.participants2.isEmpty()) {
                    if (this.restricted1SectionRow != -1) {
                        i2 = this.rowCount;
                        this.rowCount = i2 + 1;
                        this.participantsDividerRow = i2;
                    }
                    i2 = this.rowCount;
                    this.rowCount = i2 + 1;
                    this.restricted2SectionRow = i2;
                    this.participants2StartRow = this.rowCount;
                    this.rowCount += this.participants2.size();
                    this.participants2EndRow = this.rowCount;
                }
                if (this.participantsStartRow == -1) {
                    if (this.participants2StartRow == -1) {
                        if (this.searchItem != null) {
                            this.searchItem.setVisibility(4);
                        }
                        i = this.rowCount;
                        this.rowCount = i + 1;
                        this.blockedEmptyRow = i;
                    }
                }
                if (this.searchItem != null) {
                    this.searchItem.setVisibility(0);
                }
                i = this.rowCount;
                this.rowCount = i + 1;
                this.participantsInfoRow = i;
            } else if (this.type == 1) {
                if (this.currentChat.creator && this.currentChat.megagroup) {
                    r1 = this.rowCount;
                    this.rowCount = r1 + 1;
                    this.changeAddHeaderRow = r1;
                    r1 = this.rowCount;
                    this.rowCount = r1 + 1;
                    this.changeAddRadio1Row = r1;
                    r1 = this.rowCount;
                    this.rowCount = r1 + 1;
                    this.changeAddRadio2Row = r1;
                    r1 = this.rowCount;
                    this.rowCount = r1 + 1;
                    this.changeAddSectionRow = r1;
                }
                if (ChatObject.canAddAdmins(this.currentChat)) {
                    r1 = this.rowCount;
                    this.rowCount = r1 + 1;
                    this.addNewRow = r1;
                    r1 = this.rowCount;
                    this.rowCount = r1 + 1;
                    this.addNewSectionRow = r1;
                } else {
                    this.addNewRow = -1;
                    this.addNewSectionRow = -1;
                }
                if (this.participants.isEmpty()) {
                    this.participantsStartRow = -1;
                    this.participantsEndRow = -1;
                } else {
                    this.participantsStartRow = this.rowCount;
                    this.rowCount += this.participants.size();
                    this.participantsEndRow = this.rowCount;
                }
                i = this.rowCount;
                this.rowCount = i + 1;
                this.participantsInfoRow = i;
            } else if (this.type == 2) {
                if (this.selectType == 0 && !this.currentChat.megagroup && ChatObject.canAddUsers(this.currentChat)) {
                    r1 = this.rowCount;
                    this.rowCount = r1 + 1;
                    this.addNewRow = r1;
                    if ((this.currentChat.flags & 64) == 0 && ChatObject.canAddViaLink(this.currentChat)) {
                        r1 = this.rowCount;
                        this.rowCount = r1 + 1;
                        this.addNew2Row = r1;
                    }
                    r1 = this.rowCount;
                    this.rowCount = r1 + 1;
                    this.addNewSectionRow = r1;
                }
                if (this.participants.isEmpty()) {
                    this.participantsStartRow = -1;
                    this.participantsEndRow = -1;
                } else {
                    this.participantsStartRow = this.rowCount;
                    this.rowCount += this.participants.size();
                    this.participantsEndRow = this.rowCount;
                }
                if (this.rowCount != 0) {
                    i = this.rowCount;
                    this.rowCount = i + 1;
                    this.participantsInfoRow = i;
                }
            }
        }
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.chatInfoDidLoaded);
        getChannelParticipants(0, Callback.DEFAULT_DRAG_ANIMATION_DURATION);
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.chatInfoDidLoaded);
    }

    public View createView(Context context) {
        this.searching = false;
        this.searchWas = false;
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        int i = 1;
        this.actionBar.setAllowOverlayTitle(true);
        if (this.type == 0) {
            this.actionBar.setTitle(LocaleController.getString("ChannelBlacklist", R.string.ChannelBlacklist));
        } else if (this.type == 1) {
            this.actionBar.setTitle(LocaleController.getString("ChannelAdministrators", R.string.ChannelAdministrators));
        } else if (this.type == 2) {
            if (this.selectType == 0) {
                if (!ChatObject.isChannel(this.currentChat) || this.currentChat.megagroup) {
                    this.actionBar.setTitle(LocaleController.getString("ChannelMembers", R.string.ChannelMembers));
                } else {
                    this.actionBar.setTitle(LocaleController.getString("ChannelSubscribers", R.string.ChannelSubscribers));
                }
            } else if (this.selectType == 1) {
                this.actionBar.setTitle(LocaleController.getString("ChannelAddAdmin", R.string.ChannelAddAdmin));
            } else if (this.selectType == 2) {
                this.actionBar.setTitle(LocaleController.getString("ChannelBlockUser", R.string.ChannelBlockUser));
            }
        }
        this.actionBar.setActionBarMenuOnItemClick(new C19961());
        if (this.selectType != 0 || this.type == 2 || this.type == 0) {
            this.searchListViewAdapter = new SearchAdapter(context);
            this.searchItem = this.actionBar.createMenu().addItem(0, (int) R.drawable.ic_ab_search).setIsSearchField(true).setActionBarMenuItemSearchListener(new C19972());
            this.searchItem.getSearchField().setHint(LocaleController.getString("Search", R.string.Search));
        }
        this.fragmentView = new FrameLayout(context);
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        FrameLayout frameLayout = this.fragmentView;
        this.emptyView = new EmptyTextProgressView(context);
        if (this.type == 0 || this.type == 2) {
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
        recyclerListView = this.listView;
        if (!LocaleController.isRTL) {
            i = 2;
        }
        recyclerListView.setVerticalScrollbarPosition(i);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setOnItemClickListener(new C20013());
        this.listView.setOnItemLongClickListener(new C20024());
        if (this.searchItem != null) {
            this.listView.setOnScrollListener(new C20035());
        }
        if (this.loadingUsers) {
            this.emptyView.showProgress();
        } else {
            this.emptyView.showTextView();
        }
        updateRows();
        return this.fragmentView;
    }

    private boolean createMenuForParticipant(final ChannelParticipant participant, boolean resultOnly) {
        if (participant != null) {
            if (this.selectType == 0) {
                if (participant.user_id == UserConfig.getInstance(this.currentAccount).getClientUserId()) {
                    return false;
                }
                Builder builder;
                if (this.type == 2) {
                    boolean allowSetAdmin;
                    boolean canEditAdmin;
                    ArrayList<String> items;
                    ArrayList<Integer> actions;
                    final User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(participant.user_id));
                    if (!(participant instanceof TL_channelParticipant)) {
                        if (!(participant instanceof TL_channelParticipantBanned)) {
                            allowSetAdmin = false;
                            canEditAdmin = ((participant instanceof TL_channelParticipantAdmin) && !(participant instanceof TL_channelParticipantCreator)) || participant.can_edit;
                            if (resultOnly) {
                                items = new ArrayList();
                                actions = new ArrayList();
                            } else {
                                items = null;
                                actions = null;
                            }
                            if (allowSetAdmin && ChatObject.canAddAdmins(this.currentChat)) {
                                if (resultOnly) {
                                    return true;
                                }
                                items.add(LocaleController.getString("SetAsAdmin", R.string.SetAsAdmin));
                                actions.add(Integer.valueOf(0));
                            }
                            if (ChatObject.canBlockUsers(this.currentChat) && canEditAdmin) {
                                if (resultOnly) {
                                    return true;
                                }
                                if (this.currentChat.megagroup) {
                                    items.add(LocaleController.getString("ChannelRemoveUser", R.string.ChannelRemoveUser));
                                    actions.add(Integer.valueOf(2));
                                } else {
                                    items.add(LocaleController.getString("KickFromSupergroup", R.string.KickFromSupergroup));
                                    actions.add(Integer.valueOf(1));
                                    items.add(LocaleController.getString("KickFromGroup", R.string.KickFromGroup));
                                    actions.add(Integer.valueOf(2));
                                }
                            }
                            if (actions != null) {
                                if (actions.isEmpty()) {
                                    builder = new Builder(getParentActivity());
                                    builder.setItems((CharSequence[]) items.toArray(new CharSequence[actions.size()]), new OnClickListener() {
                                        public void onClick(DialogInterface dialogInterface, final int i) {
                                            if (((Integer) actions.get(i)).intValue() == 2) {
                                                MessagesController.getInstance(ChannelUsersActivity.this.currentAccount).deleteUserFromChat(ChannelUsersActivity.this.chatId, user, null);
                                                for (int a = 0; a < ChannelUsersActivity.this.participants.size(); a++) {
                                                    if (((ChannelParticipant) ChannelUsersActivity.this.participants.get(a)).user_id == participant.user_id) {
                                                        ChannelUsersActivity.this.participants.remove(a);
                                                        ChannelUsersActivity.this.updateRows();
                                                        ChannelUsersActivity.this.listViewAdapter.notifyDataSetChanged();
                                                        return;
                                                    }
                                                }
                                                return;
                                            }
                                            ChannelRightsEditActivity channelRightsEditActivity = new ChannelRightsEditActivity(user.id, ChannelUsersActivity.this.chatId, participant.admin_rights, participant.banned_rights, ((Integer) actions.get(i)).intValue(), true);
                                            channelRightsEditActivity.setDelegate(new ChannelRightsEditActivityDelegate() {
                                                public void didSetRights(int rights, TL_channelAdminRights rightsAdmin, TL_channelBannedRights rightsBanned) {
                                                    int a = 0;
                                                    int a2;
                                                    if (((Integer) actions.get(i)).intValue() == 0) {
                                                        ChannelParticipant newPart;
                                                        while (true) {
                                                            a2 = a;
                                                            if (a2 >= ChannelUsersActivity.this.participants.size()) {
                                                                return;
                                                            }
                                                            if (((ChannelParticipant) ChannelUsersActivity.this.participants.get(a2)).user_id == participant.user_id) {
                                                                break;
                                                            }
                                                            a = a2 + 1;
                                                        }
                                                        if (rights == 1) {
                                                            newPart = new TL_channelParticipantAdmin();
                                                        } else {
                                                            newPart = new TL_channelParticipant();
                                                        }
                                                        newPart.admin_rights = rightsAdmin;
                                                        newPart.banned_rights = rightsBanned;
                                                        newPart.inviter_id = UserConfig.getInstance(ChannelUsersActivity.this.currentAccount).getClientUserId();
                                                        newPart.user_id = participant.user_id;
                                                        newPart.date = participant.date;
                                                        ChannelUsersActivity.this.participants.set(a2, newPart);
                                                    } else if (((Integer) actions.get(i)).intValue() == 1 && rights == 0) {
                                                        while (true) {
                                                            a2 = a;
                                                            if (a2 >= ChannelUsersActivity.this.participants.size()) {
                                                                return;
                                                            }
                                                            if (((ChannelParticipant) ChannelUsersActivity.this.participants.get(a2)).user_id == participant.user_id) {
                                                                ChannelUsersActivity.this.participants.remove(a2);
                                                                ChannelUsersActivity.this.updateRows();
                                                                ChannelUsersActivity.this.listViewAdapter.notifyDataSetChanged();
                                                                return;
                                                            }
                                                            a = a2 + 1;
                                                        }
                                                    }
                                                }
                                            });
                                            ChannelUsersActivity.this.presentFragment(channelRightsEditActivity);
                                        }
                                    });
                                    showDialog(builder.create());
                                }
                            }
                            return false;
                        }
                    }
                    allowSetAdmin = true;
                    if (participant instanceof TL_channelParticipantAdmin) {
                    }
                    if (resultOnly) {
                        items = null;
                        actions = null;
                    } else {
                        items = new ArrayList();
                        actions = new ArrayList();
                    }
                    if (resultOnly) {
                        return true;
                    }
                    items.add(LocaleController.getString("SetAsAdmin", R.string.SetAsAdmin));
                    actions.add(Integer.valueOf(0));
                    if (resultOnly) {
                        return true;
                    }
                    if (this.currentChat.megagroup) {
                        items.add(LocaleController.getString("ChannelRemoveUser", R.string.ChannelRemoveUser));
                        actions.add(Integer.valueOf(2));
                    } else {
                        items.add(LocaleController.getString("KickFromSupergroup", R.string.KickFromSupergroup));
                        actions.add(Integer.valueOf(1));
                        items.add(LocaleController.getString("KickFromGroup", R.string.KickFromGroup));
                        actions.add(Integer.valueOf(2));
                    }
                    if (actions != null) {
                        if (actions.isEmpty()) {
                            builder = new Builder(getParentActivity());
                            builder.setItems((CharSequence[]) items.toArray(new CharSequence[actions.size()]), /* anonymous class already generated */);
                            showDialog(builder.create());
                        }
                    }
                    return false;
                }
                CharSequence[] items2 = null;
                if (this.type == 0 && ChatObject.canBlockUsers(this.currentChat)) {
                    if (resultOnly) {
                        return true;
                    }
                    items2 = new CharSequence[]{LocaleController.getString("Unban", R.string.Unban)};
                } else if (this.type == 1 && ChatObject.canAddAdmins(this.currentChat) && participant.can_edit) {
                    if (resultOnly) {
                        return true;
                    }
                    items2 = new CharSequence[]{LocaleController.getString("ChannelRemoveUserAdmin", R.string.ChannelRemoveUserAdmin)};
                }
                if (items2 == null) {
                    return false;
                }
                builder = new Builder(getParentActivity());
                builder.setItems(items2, new OnClickListener() {

                    /* renamed from: org.telegram.ui.ChannelUsersActivity$7$1 */
                    class C20051 implements RequestDelegate {
                        C20051() {
                        }

                        public void run(TLObject response, TL_error error) {
                            if (response != null) {
                                final Updates updates = (Updates) response;
                                MessagesController.getInstance(ChannelUsersActivity.this.currentAccount).processUpdates(updates, false);
                                if (!updates.chats.isEmpty()) {
                                    AndroidUtilities.runOnUIThread(new Runnable() {
                                        public void run() {
                                            MessagesController.getInstance(ChannelUsersActivity.this.currentAccount).loadFullChat(((Chat) updates.chats.get(0)).id, 0, true);
                                        }
                                    }, 1000);
                                }
                            }
                        }
                    }

                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i != 0) {
                            return;
                        }
                        if (ChannelUsersActivity.this.type == 0) {
                            ChannelUsersActivity.this.participants.remove(participant);
                            ChannelUsersActivity.this.updateRows();
                            ChannelUsersActivity.this.listViewAdapter.notifyDataSetChanged();
                            TL_channels_editBanned req = new TL_channels_editBanned();
                            req.user_id = MessagesController.getInstance(ChannelUsersActivity.this.currentAccount).getInputUser(participant.user_id);
                            req.channel = MessagesController.getInstance(ChannelUsersActivity.this.currentAccount).getInputChannel(ChannelUsersActivity.this.chatId);
                            req.banned_rights = new TL_channelBannedRights();
                            ConnectionsManager.getInstance(ChannelUsersActivity.this.currentAccount).sendRequest(req, new C20051());
                        } else if (ChannelUsersActivity.this.type == 1) {
                            MessagesController.getInstance(ChannelUsersActivity.this.currentAccount).setUserAdminRole(ChannelUsersActivity.this.chatId, MessagesController.getInstance(ChannelUsersActivity.this.currentAccount).getUser(Integer.valueOf(participant.user_id)), new TL_channelAdminRights(), ChannelUsersActivity.this.currentChat.megagroup, ChannelUsersActivity.this);
                        } else if (ChannelUsersActivity.this.type == 2) {
                            MessagesController.getInstance(ChannelUsersActivity.this.currentAccount).deleteUserFromChat(ChannelUsersActivity.this.chatId, MessagesController.getInstance(ChannelUsersActivity.this.currentAccount).getUser(Integer.valueOf(participant.user_id)), null);
                        }
                    }
                });
                showDialog(builder.create());
                return true;
            }
        }
        return false;
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.chatInfoDidLoaded) {
            ChatFull chatFull = args[0];
            boolean byChannelUsers = ((Boolean) args[2]).booleanValue();
            if (chatFull.id == this.chatId && !byChannelUsers) {
                AndroidUtilities.runOnUIThread(new C10118());
            }
        }
    }

    private int getChannelAdminParticipantType(ChannelParticipant participant) {
        if (!(participant instanceof TL_channelParticipantCreator)) {
            if (!(participant instanceof TL_channelParticipantSelf)) {
                if (participant instanceof TL_channelParticipantAdmin) {
                    return 1;
                }
                return 2;
            }
        }
        return 0;
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
            req.channel = MessagesController.getInstance(this.currentAccount).getInputChannel(this.chatId);
            final boolean byEndReached = this.firstEndReached;
            if (this.type == 0) {
                if (byEndReached) {
                    req.filter = new TL_channelParticipantsKicked();
                } else {
                    req.filter = new TL_channelParticipantsBanned();
                }
            } else if (this.type == 1) {
                req.filter = new TL_channelParticipantsAdmins();
            } else if (this.type == 2) {
                req.filter = new TL_channelParticipantsRecent();
            }
            req.filter.f32q = TtmlNode.ANONYMOUS_REGION_ID;
            req.offset = offset;
            req.limit = count;
            ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
                public void run(final TLObject response, final TL_error error) {
                    AndroidUtilities.runOnUIThread(new Runnable() {

                        /* renamed from: org.telegram.ui.ChannelUsersActivity$9$1$1 */
                        class C10121 implements Comparator<ChannelParticipant> {
                            C10121() {
                            }

                            public int compare(ChannelParticipant lhs, ChannelParticipant rhs) {
                                User user1 = MessagesController.getInstance(ChannelUsersActivity.this.currentAccount).getUser(Integer.valueOf(rhs.user_id));
                                User user2 = MessagesController.getInstance(ChannelUsersActivity.this.currentAccount).getUser(Integer.valueOf(lhs.user_id));
                                int status1 = 0;
                                int status2 = 0;
                                if (!(user1 == null || user1.status == null)) {
                                    status1 = user1.id == UserConfig.getInstance(ChannelUsersActivity.this.currentAccount).getClientUserId() ? ConnectionsManager.getInstance(ChannelUsersActivity.this.currentAccount).getCurrentTime() + 50000 : user1.status.expires;
                                }
                                if (!(user2 == null || user2.status == null)) {
                                    status2 = user2.id == UserConfig.getInstance(ChannelUsersActivity.this.currentAccount).getClientUserId() ? ConnectionsManager.getInstance(ChannelUsersActivity.this.currentAccount).getCurrentTime() + 50000 : user2.status.expires;
                                }
                                if (status1 <= 0 || status2 <= 0) {
                                    if (status1 >= 0 || status2 >= 0) {
                                        if ((status1 >= 0 || status2 <= 0) && (status1 != 0 || status2 == 0)) {
                                            return ((status2 >= 0 || status1 <= 0) && (status2 != 0 || status1 == 0)) ? 0 : 1;
                                        } else {
                                            return -1;
                                        }
                                    } else if (status1 > status2) {
                                        return 1;
                                    } else {
                                        return status1 < status2 ? -1 : 0;
                                    }
                                } else if (status1 > status2) {
                                    return 1;
                                } else {
                                    return status1 < status2 ? -1 : 0;
                                }
                            }
                        }

                        /* renamed from: org.telegram.ui.ChannelUsersActivity$9$1$2 */
                        class C10132 implements Comparator<ChannelParticipant> {
                            C10132() {
                            }

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
                        }

                        public void run() {
                            boolean changeFirst = ChannelUsersActivity.this.firstLoaded ^ true;
                            int a = 0;
                            ChannelUsersActivity.this.loadingUsers = false;
                            ChannelUsersActivity.this.firstLoaded = true;
                            if (ChannelUsersActivity.this.emptyView != null) {
                                ChannelUsersActivity.this.emptyView.showTextView();
                            }
                            if (error == null) {
                                TL_channels_channelParticipants res = response;
                                MessagesController.getInstance(ChannelUsersActivity.this.currentAccount).putUsers(res.users, false);
                                int selfId = UserConfig.getInstance(ChannelUsersActivity.this.currentAccount).getClientUserId();
                                if (ChannelUsersActivity.this.selectType != 0) {
                                    for (int a2 = 0; a2 < res.participants.size(); a2++) {
                                        if (((ChannelParticipant) res.participants.get(a2)).user_id == selfId) {
                                            res.participants.remove(a2);
                                            break;
                                        }
                                    }
                                }
                                if (ChannelUsersActivity.this.type != 0) {
                                    ChannelUsersActivity.this.participantsMap.clear();
                                    ChannelUsersActivity.this.participants = res.participants;
                                } else if (byEndReached) {
                                    ChannelUsersActivity.this.participants2 = res.participants;
                                } else {
                                    ChannelUsersActivity.this.participants2 = new ArrayList();
                                    ChannelUsersActivity.this.participantsMap.clear();
                                    ChannelUsersActivity.this.participants = res.participants;
                                    if (changeFirst) {
                                        ChannelUsersActivity.this.firstLoaded = false;
                                    }
                                    ChannelUsersActivity.this.firstEndReached = true;
                                    ChannelUsersActivity.this.getChannelParticipants(0, Callback.DEFAULT_DRAG_ANIMATION_DURATION);
                                }
                                while (a < res.participants.size()) {
                                    ChannelParticipant participant = (ChannelParticipant) res.participants.get(a);
                                    ChannelUsersActivity.this.participantsMap.put(participant.user_id, participant);
                                    a++;
                                }
                                try {
                                    if (ChannelUsersActivity.this.type != 0) {
                                        if (ChannelUsersActivity.this.type != 2) {
                                            if (ChannelUsersActivity.this.type == 1) {
                                                Collections.sort(res.participants, new C10132());
                                            }
                                        }
                                    }
                                    Collections.sort(res.participants, new C10121());
                                } catch (Throwable e) {
                                    FileLog.m3e(e);
                                }
                            }
                            ChannelUsersActivity.this.updateRows();
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

    protected void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        if (isOpen && !backward && this.needOpenSearch) {
            this.searchItem.openSearch(true);
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        ThemeDescriptionDelegate сellDelegate = new ThemeDescriptionDelegate() {
            public void didSetColor() {
                if (ChannelUsersActivity.this.listView != null) {
                    int count = ChannelUsersActivity.this.listView.getChildCount();
                    for (int a = 0; a < count; a++) {
                        View child = ChannelUsersActivity.this.listView.getChildAt(a);
                        if (child instanceof ManageChatUserCell) {
                            ((ManageChatUserCell) child).update(0);
                        }
                    }
                }
            }
        };
        ThemeDescription[] themeDescriptionArr = new ThemeDescription[32];
        themeDescriptionArr[0] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{ManageChatUserCell.class, TextSettingsCell.class, ManageChatTextCell.class, RadioCell.class}, null, null, null, Theme.key_windowBackgroundWhite);
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
        View view = this.listView;
        View view2 = view;
        themeDescriptionArr[17] = new ThemeDescription(view2, ThemeDescription.FLAG_CHECKBOX, new Class[]{RadioCell.class}, new String[]{"radioButton"}, null, null, null, Theme.key_radioBackground);
        view = this.listView;
        view2 = view;
        themeDescriptionArr[18] = new ThemeDescription(view2, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{RadioCell.class}, new String[]{"radioButton"}, null, null, null, Theme.key_radioBackgroundChecked);
        themeDescriptionArr[19] = new ThemeDescription(this.listView, 0, new Class[]{ManageChatUserCell.class}, new String[]{"nameTextView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        int i = 3;
        int i2 = 1;
        themeDescriptionArr[20] = new ThemeDescription(this.listView, 0, new Class[]{ManageChatUserCell.class}, new String[]{"statusColor"}, null, null, сellDelegate, Theme.key_windowBackgroundWhiteGrayText);
        view = this.listView;
        Class[] clsArr = new Class[i2];
        clsArr[0] = ManageChatUserCell.class;
        String[] strArr = new String[i2];
        strArr[0] = "statusOnlineColor";
        themeDescriptionArr[21] = new ThemeDescription(view, 0, clsArr, strArr, null, null, сellDelegate, Theme.key_windowBackgroundWhiteBlueText);
        view = this.listView;
        clsArr = new Class[i2];
        clsArr[0] = ManageChatUserCell.class;
        Drawable[] drawableArr = new Drawable[i];
        drawableArr[0] = Theme.avatar_photoDrawable;
        drawableArr[i2] = Theme.avatar_broadcastDrawable;
        drawableArr[2] = Theme.avatar_savedDrawable;
        themeDescriptionArr[22] = new ThemeDescription(view, 0, clsArr, null, drawableArr, null, Theme.key_avatar_text);
        ThemeDescriptionDelegate themeDescriptionDelegate = сellDelegate;
        themeDescriptionArr[23] = new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundRed);
        themeDescriptionArr[24] = new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundOrange);
        themeDescriptionArr[25] = new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundViolet);
        themeDescriptionArr[26] = new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundGreen);
        themeDescriptionArr[27] = new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundCyan);
        themeDescriptionArr[28] = new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundBlue);
        themeDescriptionArr[29] = new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundPink);
        view = this.listView;
        clsArr = new Class[i2];
        clsArr[0] = ManageChatTextCell.class;
        strArr = new String[i2];
        strArr[0] = "textView";
        themeDescriptionArr[30] = new ThemeDescription(view, 0, clsArr, strArr, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        view = this.listView;
        clsArr = new Class[i2];
        clsArr[0] = ManageChatTextCell.class;
        strArr = new String[i2];
        strArr[0] = "imageView";
        themeDescriptionArr[31] = new ThemeDescription(view, 0, clsArr, strArr, null, null, null, Theme.key_windowBackgroundWhiteGrayIcon);
        return themeDescriptionArr;
    }
}
