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
import org.telegram.messenger.C0446R;
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

        public void onItemClick(int i) {
            if (i == -1) {
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
                editText = editText.getText().toString();
                if (editText.length() != 0) {
                    ChannelUsersActivity.this.searchWas = true;
                    if (ChannelUsersActivity.this.listView != null) {
                        ChannelUsersActivity.this.listView.setAdapter(ChannelUsersActivity.this.searchListViewAdapter);
                        ChannelUsersActivity.this.searchListViewAdapter.notifyDataSetChanged();
                        ChannelUsersActivity.this.listView.setFastScrollVisible(false);
                        ChannelUsersActivity.this.listView.setVerticalScrollBarEnabled(true);
                    }
                }
                ChannelUsersActivity.this.searchListViewAdapter.searchDialogs(editText);
            }
        }
    }

    /* renamed from: org.telegram.ui.ChannelUsersActivity$3 */
    class C20013 implements OnItemClickListener {

        /* renamed from: org.telegram.ui.ChannelUsersActivity$3$1 */
        class C19981 implements ContactsActivityDelegate {
            C19981() {
            }

            public void didSelectContact(User user, String str, ContactsActivity contactsActivity) {
                MessagesController.getInstance(ChannelUsersActivity.this.currentAccount).addUserToChat(ChannelUsersActivity.this.chatId, user, null, str != null ? Utilities.parseInt(str).intValue() : null, null, ChannelUsersActivity.this);
            }
        }

        C20013() {
        }

        public void onItemClick(View view, int i) {
            int i2 = i;
            Bundle bundle;
            if (i2 == ChannelUsersActivity.this.addNewRow) {
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
                    bundle.putString("selectAlertString", LocaleController.getString("ChannelAddTo", C0446R.string.ChannelAddTo));
                    BaseFragment contactsActivity = new ContactsActivity(bundle);
                    contactsActivity.setDelegate(new C19981());
                    ChannelUsersActivity.this.presentFragment(contactsActivity);
                }
            } else if (i2 == ChannelUsersActivity.this.addNew2Row) {
                ChannelUsersActivity.this.presentFragment(new GroupInviteActivity(ChannelUsersActivity.this.chatId));
            } else {
                if (i2 != ChannelUsersActivity.this.changeAddRadio1Row) {
                    if (i2 != ChannelUsersActivity.this.changeAddRadio2Row) {
                        ChannelParticipant item;
                        int i3;
                        TL_channelBannedRights tL_channelBannedRights;
                        TL_channelAdminRights tL_channelAdminRights;
                        boolean z;
                        BaseFragment channelRightsEditActivity;
                        boolean z2;
                        TL_channelBannedRights tL_channelBannedRights2;
                        int i4;
                        if (ChannelUsersActivity.this.listView.getAdapter() == ChannelUsersActivity.this.listViewAdapter) {
                            item = ChannelUsersActivity.this.listViewAdapter.getItem(i2);
                            if (item != null) {
                                i4 = item.user_id;
                                TL_channelBannedRights tL_channelBannedRights3 = item.banned_rights;
                                TL_channelAdminRights tL_channelAdminRights2 = item.admin_rights;
                                boolean z3 = !((item instanceof TL_channelParticipantAdmin) || (item instanceof TL_channelParticipantCreator)) || item.can_edit;
                                if (item instanceof TL_channelParticipantCreator) {
                                    tL_channelAdminRights2 = new TL_channelAdminRights();
                                    tL_channelAdminRights2.add_admins = true;
                                    tL_channelAdminRights2.pin_messages = true;
                                    tL_channelAdminRights2.invite_link = true;
                                    tL_channelAdminRights2.invite_users = true;
                                    tL_channelAdminRights2.ban_users = true;
                                    tL_channelAdminRights2.delete_messages = true;
                                    tL_channelAdminRights2.edit_messages = true;
                                    tL_channelAdminRights2.post_messages = true;
                                    tL_channelAdminRights2.change_info = true;
                                }
                                i3 = i4;
                                tL_channelBannedRights = tL_channelBannedRights3;
                                tL_channelAdminRights = tL_channelAdminRights2;
                                z = z3;
                                if (i3 != 0) {
                                    if (ChannelUsersActivity.this.selectType != 0) {
                                        if (!ChannelUsersActivity.this.currentChat.megagroup) {
                                            if (ChannelUsersActivity.this.selectType == 1) {
                                                MessagesController.getInstance(ChannelUsersActivity.this.currentAccount).deleteUserFromChat(ChannelUsersActivity.this.chatId, MessagesController.getInstance(ChannelUsersActivity.this.currentAccount).getUser(Integer.valueOf(i3)), null);
                                                ChannelUsersActivity.this.finishFragment();
                                            }
                                        }
                                        channelRightsEditActivity = new ChannelRightsEditActivity(i3, ChannelUsersActivity.this.chatId, tL_channelAdminRights, tL_channelBannedRights, ChannelUsersActivity.this.selectType == 1 ? 0 : 1, z);
                                        channelRightsEditActivity.setDelegate(new ChannelRightsEditActivityDelegate() {
                                            public void didSetRights(int i, TL_channelAdminRights tL_channelAdminRights, TL_channelBannedRights tL_channelBannedRights) {
                                                if (item != 0) {
                                                    item.admin_rights = tL_channelAdminRights;
                                                    item.banned_rights = tL_channelBannedRights;
                                                    ChannelParticipant channelParticipant = (ChannelParticipant) ChannelUsersActivity.this.participantsMap.get(item.user_id);
                                                    if (channelParticipant != null) {
                                                        channelParticipant.admin_rights = tL_channelAdminRights;
                                                        channelParticipant.banned_rights = tL_channelBannedRights;
                                                    }
                                                }
                                                ChannelUsersActivity.this.removeSelfFromStack();
                                            }
                                        });
                                        ChannelUsersActivity.this.presentFragment(channelRightsEditActivity);
                                    } else {
                                        if (ChannelUsersActivity.this.type != 1) {
                                            z2 = i3 == UserConfig.getInstance(ChannelUsersActivity.this.currentAccount).getClientUserId() && (ChannelUsersActivity.this.currentChat.creator || z);
                                        } else if (ChannelUsersActivity.this.type == 0) {
                                            z2 = ChatObject.canBlockUsers(ChannelUsersActivity.this.currentChat);
                                        } else {
                                            z = false;
                                            if ((ChannelUsersActivity.this.type != 1 || ChannelUsersActivity.this.currentChat.megagroup) && !(ChannelUsersActivity.this.type == 2 && ChannelUsersActivity.this.selectType == 0)) {
                                                if (tL_channelBannedRights == null) {
                                                    tL_channelBannedRights2 = new TL_channelBannedRights();
                                                    tL_channelBannedRights2.view_messages = true;
                                                    tL_channelBannedRights2.send_stickers = true;
                                                    tL_channelBannedRights2.send_media = true;
                                                    tL_channelBannedRights2.embed_links = true;
                                                    tL_channelBannedRights2.send_messages = true;
                                                    tL_channelBannedRights2.send_games = true;
                                                    tL_channelBannedRights2.send_inline = true;
                                                    tL_channelBannedRights2.send_gifs = true;
                                                    tL_channelBannedRights = tL_channelBannedRights2;
                                                }
                                                channelRightsEditActivity = new ChannelRightsEditActivity(i3, ChannelUsersActivity.this.chatId, tL_channelAdminRights, tL_channelBannedRights, ChannelUsersActivity.this.type != 1 ? 0 : 1, z);
                                                channelRightsEditActivity.setDelegate(new ChannelRightsEditActivityDelegate() {
                                                    public void didSetRights(int i, TL_channelAdminRights tL_channelAdminRights, TL_channelBannedRights tL_channelBannedRights) {
                                                        if (item != 0) {
                                                            item.admin_rights = tL_channelAdminRights;
                                                            item.banned_rights = tL_channelBannedRights;
                                                            ChannelParticipant channelParticipant = (ChannelParticipant) ChannelUsersActivity.this.participantsMap.get(item.user_id);
                                                            if (channelParticipant != null) {
                                                                channelParticipant.admin_rights = tL_channelAdminRights;
                                                                channelParticipant.banned_rights = tL_channelBannedRights;
                                                            }
                                                        }
                                                    }
                                                });
                                                ChannelUsersActivity.this.presentFragment(channelRightsEditActivity);
                                            } else {
                                                bundle = new Bundle();
                                                bundle.putInt("user_id", i3);
                                                ChannelUsersActivity.this.presentFragment(new ProfileActivity(bundle));
                                            }
                                        }
                                        z = z2;
                                        if (ChannelUsersActivity.this.type != 1) {
                                        }
                                        if (tL_channelBannedRights == null) {
                                            tL_channelBannedRights2 = new TL_channelBannedRights();
                                            tL_channelBannedRights2.view_messages = true;
                                            tL_channelBannedRights2.send_stickers = true;
                                            tL_channelBannedRights2.send_media = true;
                                            tL_channelBannedRights2.embed_links = true;
                                            tL_channelBannedRights2.send_messages = true;
                                            tL_channelBannedRights2.send_games = true;
                                            tL_channelBannedRights2.send_inline = true;
                                            tL_channelBannedRights2.send_gifs = true;
                                            tL_channelBannedRights = tL_channelBannedRights2;
                                        }
                                        if (ChannelUsersActivity.this.type != 1) {
                                        }
                                        channelRightsEditActivity = new ChannelRightsEditActivity(i3, ChannelUsersActivity.this.chatId, tL_channelAdminRights, tL_channelBannedRights, ChannelUsersActivity.this.type != 1 ? 0 : 1, z);
                                        channelRightsEditActivity.setDelegate(/* anonymous class already generated */);
                                        ChannelUsersActivity.this.presentFragment(channelRightsEditActivity);
                                    }
                                }
                            } else {
                                i3 = 0;
                                z = i3;
                            }
                        } else {
                            TLObject item2 = ChannelUsersActivity.this.searchListViewAdapter.getItem(i2);
                            if (item2 instanceof User) {
                                User user = (User) item2;
                                MessagesController.getInstance(ChannelUsersActivity.this.currentAccount).putUser(user, false);
                                SparseArray access$1600 = ChannelUsersActivity.this.participantsMap;
                                i2 = user.id;
                                ChannelParticipant channelParticipant = (ChannelParticipant) access$1600.get(i2);
                                i4 = i2;
                                item = channelParticipant;
                            } else if (item2 instanceof ChannelParticipant) {
                                item = (ChannelParticipant) item2;
                                i4 = false;
                            } else {
                                i4 = false;
                                item = null;
                            }
                            if (item != null) {
                                i4 = item.user_id;
                                boolean z4 = !((item instanceof TL_channelParticipantAdmin) || (item instanceof TL_channelParticipantCreator)) || item.can_edit;
                                i3 = i4;
                                z = z4;
                                tL_channelBannedRights = item.banned_rights;
                                tL_channelAdminRights = item.admin_rights;
                                if (i3 != 0) {
                                    if (ChannelUsersActivity.this.selectType != 0) {
                                        if (ChannelUsersActivity.this.type != 1) {
                                            if (i3 == UserConfig.getInstance(ChannelUsersActivity.this.currentAccount).getClientUserId()) {
                                            }
                                        } else if (ChannelUsersActivity.this.type == 0) {
                                            z = false;
                                            if (ChannelUsersActivity.this.type != 1) {
                                            }
                                            if (tL_channelBannedRights == null) {
                                                tL_channelBannedRights2 = new TL_channelBannedRights();
                                                tL_channelBannedRights2.view_messages = true;
                                                tL_channelBannedRights2.send_stickers = true;
                                                tL_channelBannedRights2.send_media = true;
                                                tL_channelBannedRights2.embed_links = true;
                                                tL_channelBannedRights2.send_messages = true;
                                                tL_channelBannedRights2.send_games = true;
                                                tL_channelBannedRights2.send_inline = true;
                                                tL_channelBannedRights2.send_gifs = true;
                                                tL_channelBannedRights = tL_channelBannedRights2;
                                            }
                                            if (ChannelUsersActivity.this.type != 1) {
                                            }
                                            channelRightsEditActivity = new ChannelRightsEditActivity(i3, ChannelUsersActivity.this.chatId, tL_channelAdminRights, tL_channelBannedRights, ChannelUsersActivity.this.type != 1 ? 0 : 1, z);
                                            channelRightsEditActivity.setDelegate(/* anonymous class already generated */);
                                            ChannelUsersActivity.this.presentFragment(channelRightsEditActivity);
                                        } else {
                                            z2 = ChatObject.canBlockUsers(ChannelUsersActivity.this.currentChat);
                                        }
                                        z = z2;
                                        if (ChannelUsersActivity.this.type != 1) {
                                        }
                                        if (tL_channelBannedRights == null) {
                                            tL_channelBannedRights2 = new TL_channelBannedRights();
                                            tL_channelBannedRights2.view_messages = true;
                                            tL_channelBannedRights2.send_stickers = true;
                                            tL_channelBannedRights2.send_media = true;
                                            tL_channelBannedRights2.embed_links = true;
                                            tL_channelBannedRights2.send_messages = true;
                                            tL_channelBannedRights2.send_games = true;
                                            tL_channelBannedRights2.send_inline = true;
                                            tL_channelBannedRights2.send_gifs = true;
                                            tL_channelBannedRights = tL_channelBannedRights2;
                                        }
                                        if (ChannelUsersActivity.this.type != 1) {
                                        }
                                        channelRightsEditActivity = new ChannelRightsEditActivity(i3, ChannelUsersActivity.this.chatId, tL_channelAdminRights, tL_channelBannedRights, ChannelUsersActivity.this.type != 1 ? 0 : 1, z);
                                        channelRightsEditActivity.setDelegate(/* anonymous class already generated */);
                                        ChannelUsersActivity.this.presentFragment(channelRightsEditActivity);
                                    } else {
                                        if (ChannelUsersActivity.this.currentChat.megagroup) {
                                            if (ChannelUsersActivity.this.selectType == 1) {
                                                MessagesController.getInstance(ChannelUsersActivity.this.currentAccount).deleteUserFromChat(ChannelUsersActivity.this.chatId, MessagesController.getInstance(ChannelUsersActivity.this.currentAccount).getUser(Integer.valueOf(i3)), null);
                                                ChannelUsersActivity.this.finishFragment();
                                            }
                                        }
                                        if (ChannelUsersActivity.this.selectType == 1) {
                                        }
                                        channelRightsEditActivity = new ChannelRightsEditActivity(i3, ChannelUsersActivity.this.chatId, tL_channelAdminRights, tL_channelBannedRights, ChannelUsersActivity.this.selectType == 1 ? 0 : 1, z);
                                        channelRightsEditActivity.setDelegate(/* anonymous class already generated */);
                                        ChannelUsersActivity.this.presentFragment(channelRightsEditActivity);
                                    }
                                }
                            } else {
                                i3 = i4;
                                z = true;
                            }
                        }
                        tL_channelAdminRights = null;
                        tL_channelBannedRights = tL_channelAdminRights;
                        if (i3 != 0) {
                            if (ChannelUsersActivity.this.selectType != 0) {
                                if (ChannelUsersActivity.this.currentChat.megagroup) {
                                    if (ChannelUsersActivity.this.selectType == 1) {
                                        MessagesController.getInstance(ChannelUsersActivity.this.currentAccount).deleteUserFromChat(ChannelUsersActivity.this.chatId, MessagesController.getInstance(ChannelUsersActivity.this.currentAccount).getUser(Integer.valueOf(i3)), null);
                                        ChannelUsersActivity.this.finishFragment();
                                    }
                                }
                                if (ChannelUsersActivity.this.selectType == 1) {
                                }
                                channelRightsEditActivity = new ChannelRightsEditActivity(i3, ChannelUsersActivity.this.chatId, tL_channelAdminRights, tL_channelBannedRights, ChannelUsersActivity.this.selectType == 1 ? 0 : 1, z);
                                channelRightsEditActivity.setDelegate(/* anonymous class already generated */);
                                ChannelUsersActivity.this.presentFragment(channelRightsEditActivity);
                            } else {
                                if (ChannelUsersActivity.this.type != 1) {
                                    if (i3 == UserConfig.getInstance(ChannelUsersActivity.this.currentAccount).getClientUserId()) {
                                    }
                                } else if (ChannelUsersActivity.this.type == 0) {
                                    z2 = ChatObject.canBlockUsers(ChannelUsersActivity.this.currentChat);
                                } else {
                                    z = false;
                                    if (ChannelUsersActivity.this.type != 1) {
                                    }
                                    if (tL_channelBannedRights == null) {
                                        tL_channelBannedRights2 = new TL_channelBannedRights();
                                        tL_channelBannedRights2.view_messages = true;
                                        tL_channelBannedRights2.send_stickers = true;
                                        tL_channelBannedRights2.send_media = true;
                                        tL_channelBannedRights2.embed_links = true;
                                        tL_channelBannedRights2.send_messages = true;
                                        tL_channelBannedRights2.send_games = true;
                                        tL_channelBannedRights2.send_inline = true;
                                        tL_channelBannedRights2.send_gifs = true;
                                        tL_channelBannedRights = tL_channelBannedRights2;
                                    }
                                    if (ChannelUsersActivity.this.type != 1) {
                                    }
                                    channelRightsEditActivity = new ChannelRightsEditActivity(i3, ChannelUsersActivity.this.chatId, tL_channelAdminRights, tL_channelBannedRights, ChannelUsersActivity.this.type != 1 ? 0 : 1, z);
                                    channelRightsEditActivity.setDelegate(/* anonymous class already generated */);
                                    ChannelUsersActivity.this.presentFragment(channelRightsEditActivity);
                                }
                                z = z2;
                                if (ChannelUsersActivity.this.type != 1) {
                                }
                                if (tL_channelBannedRights == null) {
                                    tL_channelBannedRights2 = new TL_channelBannedRights();
                                    tL_channelBannedRights2.view_messages = true;
                                    tL_channelBannedRights2.send_stickers = true;
                                    tL_channelBannedRights2.send_media = true;
                                    tL_channelBannedRights2.embed_links = true;
                                    tL_channelBannedRights2.send_messages = true;
                                    tL_channelBannedRights2.send_games = true;
                                    tL_channelBannedRights2.send_inline = true;
                                    tL_channelBannedRights2.send_gifs = true;
                                    tL_channelBannedRights = tL_channelBannedRights2;
                                }
                                if (ChannelUsersActivity.this.type != 1) {
                                }
                                channelRightsEditActivity = new ChannelRightsEditActivity(i3, ChannelUsersActivity.this.chatId, tL_channelAdminRights, tL_channelBannedRights, ChannelUsersActivity.this.type != 1 ? 0 : 1, z);
                                channelRightsEditActivity.setDelegate(/* anonymous class already generated */);
                                ChannelUsersActivity.this.presentFragment(channelRightsEditActivity);
                            }
                        }
                    }
                }
                Chat chat = MessagesController.getInstance(ChannelUsersActivity.this.currentAccount).getChat(Integer.valueOf(ChannelUsersActivity.this.chatId));
                if (chat != null) {
                    boolean z5;
                    int i5;
                    View childAt;
                    int intValue;
                    RadioCell radioCell;
                    boolean z6;
                    if (i2 == 1 && !chat.democracy) {
                        chat.democracy = true;
                    } else if (i2 == 2 && chat.democracy) {
                        chat.democracy = false;
                    } else {
                        z5 = false;
                        if (z5) {
                            MessagesController.getInstance(ChannelUsersActivity.this.currentAccount).toogleChannelInvites(ChannelUsersActivity.this.chatId, chat.democracy);
                            i2 = ChannelUsersActivity.this.listView.getChildCount();
                            for (i5 = 0; i5 < i2; i5++) {
                                childAt = ChannelUsersActivity.this.listView.getChildAt(i5);
                                if (childAt instanceof RadioCell) {
                                    intValue = ((Integer) childAt.getTag()).intValue();
                                    radioCell = (RadioCell) childAt;
                                    z6 = (intValue != 0 && chat.democracy) || (intValue == 1 && !chat.democracy);
                                    radioCell.setChecked(z6, true);
                                }
                            }
                        }
                    }
                    z5 = true;
                    if (z5) {
                        MessagesController.getInstance(ChannelUsersActivity.this.currentAccount).toogleChannelInvites(ChannelUsersActivity.this.chatId, chat.democracy);
                        i2 = ChannelUsersActivity.this.listView.getChildCount();
                        for (i5 = 0; i5 < i2; i5++) {
                            childAt = ChannelUsersActivity.this.listView.getChildAt(i5);
                            if (childAt instanceof RadioCell) {
                                intValue = ((Integer) childAt.getTag()).intValue();
                                radioCell = (RadioCell) childAt;
                                if (intValue != 0) {
                                }
                                radioCell.setChecked(z6, true);
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

        public boolean onItemClick(View view, int i) {
            return (ChannelUsersActivity.this.getParentActivity() == null || ChannelUsersActivity.this.listView.getAdapter() != ChannelUsersActivity.this.listViewAdapter || ChannelUsersActivity.this.createMenuForParticipant(ChannelUsersActivity.this.listViewAdapter.getItem(i), false) == null) ? false : true;
        }
    }

    /* renamed from: org.telegram.ui.ChannelUsersActivity$5 */
    class C20035 extends OnScrollListener {
        public void onScrolled(RecyclerView recyclerView, int i, int i2) {
        }

        C20035() {
        }

        public void onScrollStateChanged(RecyclerView recyclerView, int i) {
            if (i == 1 && ChannelUsersActivity.this.searching != null && ChannelUsersActivity.this.searchWas != null) {
                AndroidUtilities.hideKeyboard(ChannelUsersActivity.this.getParentActivity().getCurrentFocus());
            }
        }
    }

    private class ListAdapter extends SelectionAdapter {
        private Context mContext;

        /* renamed from: org.telegram.ui.ChannelUsersActivity$ListAdapter$1 */
        class C20071 implements ManageChatUserCellDelegate {
            C20071() {
            }

            public boolean onOptionsButtonCheck(ManageChatUserCell manageChatUserCell, boolean z) {
                return ChannelUsersActivity.this.createMenuForParticipant(ChannelUsersActivity.this.listViewAdapter.getItem(((Integer) manageChatUserCell.getTag()).intValue()), z ^ 1);
            }
        }

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean isEnabled(ViewHolder viewHolder) {
            viewHolder = viewHolder.getItemViewType();
            if (!(viewHolder == null || viewHolder == 2)) {
                if (viewHolder != 6) {
                    return null;
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

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            viewGroup = true;
            switch (i) {
                case 0:
                    Context context = this.mContext;
                    int i2 = ChannelUsersActivity.this.type == 0 ? 8 : 1;
                    if (ChannelUsersActivity.this.selectType != 0) {
                        viewGroup = null;
                    }
                    i = new ManageChatUserCell(context, i2, viewGroup);
                    i.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    ((ManageChatUserCell) i).setDelegate(new C20071());
                    break;
                case 1:
                    viewGroup = new TextInfoPrivacyCell(this.mContext);
                    break;
                case 2:
                    viewGroup = new ManageChatTextCell(this.mContext);
                    viewGroup.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 3:
                    viewGroup = new ShadowSectionCell(this.mContext);
                    break;
                case 4:
                    i = new FrameLayout(this.mContext) {
                        protected void onMeasure(int i, int i2) {
                            super.onMeasure(i, MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(i2) - AndroidUtilities.dp(56.0f), NUM));
                        }
                    };
                    FrameLayout frameLayout = (FrameLayout) i;
                    frameLayout.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, C0446R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                    View linearLayout = new LinearLayout(this.mContext);
                    linearLayout.setOrientation(1);
                    frameLayout.addView(linearLayout, LayoutHelper.createFrame(-2, -2.0f, 17, 20.0f, 0.0f, 20.0f, 0.0f));
                    View imageView = new ImageView(this.mContext);
                    imageView.setImageResource(C0446R.drawable.group_ban_empty);
                    imageView.setScaleType(ScaleType.CENTER);
                    imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_emptyListPlaceholder), Mode.MULTIPLY));
                    linearLayout.addView(imageView, LayoutHelper.createLinear(-2, -2, 1));
                    imageView = new TextView(this.mContext);
                    imageView.setText(LocaleController.getString("NoBlockedUsers", C0446R.string.NoBlockedUsers));
                    imageView.setTextColor(Theme.getColor(Theme.key_emptyListPlaceholder));
                    imageView.setTextSize(1, 16.0f);
                    imageView.setGravity(1);
                    imageView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                    linearLayout.addView(imageView, LayoutHelper.createLinear(-2, -2, 1, 0, 10, 0, 0));
                    imageView = new TextView(this.mContext);
                    if (ChannelUsersActivity.this.currentChat.megagroup) {
                        imageView.setText(LocaleController.getString("NoBlockedGroup", C0446R.string.NoBlockedGroup));
                    } else {
                        imageView.setText(LocaleController.getString("NoBlockedChannel", C0446R.string.NoBlockedChannel));
                    }
                    imageView.setTextColor(Theme.getColor(Theme.key_emptyListPlaceholder));
                    imageView.setTextSize(1, 15.0f);
                    imageView.setGravity(1);
                    linearLayout.addView(imageView, LayoutHelper.createLinear(-2, -2, 1, 0, 10, 0, 0));
                    i.setLayoutParams(new LayoutParams(-1, -1));
                    break;
                case 5:
                    viewGroup = new HeaderCell(this.mContext);
                    viewGroup.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                default:
                    viewGroup = new RadioCell(this.mContext);
                    viewGroup.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
            }
            viewGroup = i;
            return new Holder(viewGroup);
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            boolean z = false;
            boolean z2 = true;
            switch (viewHolder.getItemViewType()) {
                case 0:
                    ManageChatUserCell manageChatUserCell = (ManageChatUserCell) viewHolder.itemView;
                    manageChatUserCell.setTag(Integer.valueOf(i));
                    i = getItem(i);
                    User user = MessagesController.getInstance(ChannelUsersActivity.this.currentAccount).getUser(Integer.valueOf(i.user_id));
                    if (user == null) {
                        return;
                    }
                    if (ChannelUsersActivity.this.type == 0) {
                        if (!(i instanceof TL_channelParticipantBanned) || MessagesController.getInstance(ChannelUsersActivity.this.currentAccount).getUser(Integer.valueOf(i.kicked_by)) == 0) {
                            i = 0;
                        } else {
                            i = LocaleController.formatString("UserRestrictionsBy", C0446R.string.UserRestrictionsBy, ContactsController.formatName(MessagesController.getInstance(ChannelUsersActivity.this.currentAccount).getUser(Integer.valueOf(i.kicked_by)).first_name, MessagesController.getInstance(ChannelUsersActivity.this.currentAccount).getUser(Integer.valueOf(i.kicked_by)).last_name));
                        }
                        manageChatUserCell.setData(user, null, i);
                        return;
                    } else if (ChannelUsersActivity.this.type == 1) {
                        if (!(i instanceof TL_channelParticipantCreator)) {
                            if (!(i instanceof TL_channelParticipantSelf)) {
                                if (!(i instanceof TL_channelParticipantAdmin) || MessagesController.getInstance(ChannelUsersActivity.this.currentAccount).getUser(Integer.valueOf(i.promoted_by)) == 0) {
                                    i = 0;
                                } else {
                                    i = LocaleController.formatString("EditAdminPromotedBy", C0446R.string.EditAdminPromotedBy, ContactsController.formatName(MessagesController.getInstance(ChannelUsersActivity.this.currentAccount).getUser(Integer.valueOf(i.promoted_by)).first_name, MessagesController.getInstance(ChannelUsersActivity.this.currentAccount).getUser(Integer.valueOf(i.promoted_by)).last_name));
                                }
                                manageChatUserCell.setData(user, null, i);
                                return;
                            }
                        }
                        i = LocaleController.getString("ChannelCreator", C0446R.string.ChannelCreator);
                        manageChatUserCell.setData(user, null, i);
                        return;
                    } else if (ChannelUsersActivity.this.type == 2) {
                        manageChatUserCell.setData(user, null, null);
                        return;
                    } else {
                        return;
                    }
                case 1:
                    TextInfoPrivacyCell textInfoPrivacyCell = (TextInfoPrivacyCell) viewHolder.itemView;
                    if (i != ChannelUsersActivity.this.participantsInfoRow) {
                        return;
                    }
                    if (ChannelUsersActivity.this.type == 0) {
                        if (ChatObject.canBlockUsers(ChannelUsersActivity.this.currentChat) != 0) {
                            if (ChannelUsersActivity.this.currentChat.megagroup != 0) {
                                textInfoPrivacyCell.setText(String.format("%1$s\n\n%2$s", new Object[]{LocaleController.getString("NoBlockedGroup", C0446R.string.NoBlockedGroup), LocaleController.getString("UnbanText", C0446R.string.UnbanText)}));
                            } else {
                                textInfoPrivacyCell.setText(String.format("%1$s\n\n%2$s", new Object[]{LocaleController.getString("NoBlockedChannel", C0446R.string.NoBlockedChannel), LocaleController.getString("UnbanText", C0446R.string.UnbanText)}));
                            }
                        } else if (ChannelUsersActivity.this.currentChat.megagroup != 0) {
                            textInfoPrivacyCell.setText(LocaleController.getString("NoBlockedGroup", C0446R.string.NoBlockedGroup));
                        } else {
                            textInfoPrivacyCell.setText(LocaleController.getString("NoBlockedChannel", C0446R.string.NoBlockedChannel));
                        }
                        textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, C0446R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                        return;
                    } else if (ChannelUsersActivity.this.type == 1) {
                        if (ChannelUsersActivity.this.addNewRow != -1) {
                            if (ChannelUsersActivity.this.currentChat.megagroup != 0) {
                                textInfoPrivacyCell.setText(LocaleController.getString("MegaAdminsInfo", C0446R.string.MegaAdminsInfo));
                            } else {
                                textInfoPrivacyCell.setText(LocaleController.getString("ChannelAdminsInfo", C0446R.string.ChannelAdminsInfo));
                            }
                            textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, C0446R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                            return;
                        }
                        textInfoPrivacyCell.setText(TtmlNode.ANONYMOUS_REGION_ID);
                        textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, C0446R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                        return;
                    } else if (ChannelUsersActivity.this.type == 2) {
                        if (ChannelUsersActivity.this.currentChat.megagroup == 0) {
                            if (ChannelUsersActivity.this.selectType == 0) {
                                textInfoPrivacyCell.setText(LocaleController.getString("ChannelMembersInfo", C0446R.string.ChannelMembersInfo));
                                textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, C0446R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                                return;
                            }
                        }
                        textInfoPrivacyCell.setText(TtmlNode.ANONYMOUS_REGION_ID);
                        textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, C0446R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                        return;
                    } else {
                        return;
                    }
                case 2:
                    ManageChatTextCell manageChatTextCell = (ManageChatTextCell) viewHolder.itemView;
                    if (i == ChannelUsersActivity.this.addNewRow) {
                        if (ChannelUsersActivity.this.type == 0) {
                            manageChatTextCell.setText(LocaleController.getString("ChannelBlockUser", C0446R.string.ChannelBlockUser), null, C0446R.drawable.group_ban_new, false);
                            return;
                        } else if (ChannelUsersActivity.this.type == 1) {
                            manageChatTextCell.setText(LocaleController.getString("ChannelAddAdmin", C0446R.string.ChannelAddAdmin), null, C0446R.drawable.group_admin_new, false);
                            return;
                        } else if (ChannelUsersActivity.this.type != 2) {
                            return;
                        } else {
                            if (ChatObject.isChannel(ChannelUsersActivity.this.currentChat) == 0 || ChannelUsersActivity.this.currentChat.megagroup != 0) {
                                manageChatTextCell.setText(LocaleController.getString("AddMember", C0446R.string.AddMember), null, C0446R.drawable.menu_invite, true);
                                return;
                            } else {
                                manageChatTextCell.setText(LocaleController.getString("AddSubscriber", C0446R.string.AddSubscriber), null, C0446R.drawable.menu_invite, true);
                                return;
                            }
                        }
                    } else if (i == ChannelUsersActivity.this.addNew2Row) {
                        manageChatTextCell.setText(LocaleController.getString("ChannelInviteViaLink", C0446R.string.ChannelInviteViaLink), null, C0446R.drawable.msg_panel_link, false);
                        return;
                    } else {
                        return;
                    }
                case 5:
                    HeaderCell headerCell = (HeaderCell) viewHolder.itemView;
                    if (i == ChannelUsersActivity.this.restricted1SectionRow) {
                        headerCell.setText(LocaleController.getString("ChannelRestrictedUsers", C0446R.string.ChannelRestrictedUsers));
                        return;
                    } else if (i == ChannelUsersActivity.this.restricted2SectionRow) {
                        headerCell.setText(LocaleController.getString("ChannelBlockedUsers", C0446R.string.ChannelBlockedUsers));
                        return;
                    } else if (i == ChannelUsersActivity.this.changeAddHeaderRow) {
                        headerCell.setText(LocaleController.getString("WhoCanAddMembers", C0446R.string.WhoCanAddMembers));
                        return;
                    } else {
                        return;
                    }
                case 6:
                    RadioCell radioCell = (RadioCell) viewHolder.itemView;
                    Chat chat = MessagesController.getInstance(ChannelUsersActivity.this.currentAccount).getChat(Integer.valueOf(ChannelUsersActivity.this.chatId));
                    if (i == ChannelUsersActivity.this.changeAddRadio1Row) {
                        radioCell.setTag(Integer.valueOf(0));
                        i = LocaleController.getString("WhoCanAddMembersAllMembers", C0446R.string.WhoCanAddMembersAllMembers);
                        if (chat != null && chat.democracy) {
                            z = true;
                        }
                        radioCell.setText(i, z, true);
                        return;
                    } else if (i == ChannelUsersActivity.this.changeAddRadio2Row) {
                        radioCell.setTag(Integer.valueOf(1));
                        i = LocaleController.getString("WhoCanAddMembersAdmins", C0446R.string.WhoCanAddMembersAdmins);
                        if (chat == null || chat.democracy) {
                            z2 = false;
                        }
                        radioCell.setText(i, z2, false);
                        return;
                    } else {
                        return;
                    }
                default:
                    return;
            }
        }

        public void onViewRecycled(ViewHolder viewHolder) {
            if (viewHolder.itemView instanceof ManageChatUserCell) {
                ((ManageChatUserCell) viewHolder.itemView).recycle();
            }
        }

        public int getItemViewType(int i) {
            if (i != ChannelUsersActivity.this.addNewRow) {
                if (i != ChannelUsersActivity.this.addNew2Row) {
                    if ((i >= ChannelUsersActivity.this.participantsStartRow && i < ChannelUsersActivity.this.participantsEndRow) || (i >= ChannelUsersActivity.this.participants2StartRow && i < ChannelUsersActivity.this.participants2EndRow)) {
                        return 0;
                    }
                    if (!(i == ChannelUsersActivity.this.addNewSectionRow || i == ChannelUsersActivity.this.changeAddSectionRow)) {
                        if (i != ChannelUsersActivity.this.participantsDividerRow) {
                            if (i == ChannelUsersActivity.this.participantsInfoRow) {
                                return 1;
                            }
                            if (!(i == ChannelUsersActivity.this.changeAddHeaderRow || i == ChannelUsersActivity.this.restricted1SectionRow)) {
                                if (i != ChannelUsersActivity.this.restricted2SectionRow) {
                                    if (i != ChannelUsersActivity.this.changeAddRadio1Row) {
                                        if (i != ChannelUsersActivity.this.changeAddRadio2Row) {
                                            if (i == ChannelUsersActivity.this.blockedEmptyRow) {
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

        public ChannelParticipant getItem(int i) {
            if (ChannelUsersActivity.this.participantsStartRow == -1 || i < ChannelUsersActivity.this.participantsStartRow || i >= ChannelUsersActivity.this.participantsEndRow) {
                return (ChannelUsersActivity.this.participants2StartRow == -1 || i < ChannelUsersActivity.this.participants2StartRow || i >= ChannelUsersActivity.this.participants2EndRow) ? 0 : (ChannelParticipant) ChannelUsersActivity.this.participants2.get(i - ChannelUsersActivity.this.participants2StartRow);
            } else {
                return (ChannelParticipant) ChannelUsersActivity.this.participants.get(i - ChannelUsersActivity.this.participantsStartRow);
            }
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

            public boolean onOptionsButtonCheck(ManageChatUserCell manageChatUserCell, boolean z) {
                if (!(SearchAdapter.this.getItem(((Integer) manageChatUserCell.getTag()).intValue()) instanceof ChannelParticipant)) {
                    return null;
                }
                return ChannelUsersActivity.this.createMenuForParticipant((ChannelParticipant) SearchAdapter.this.getItem(((Integer) manageChatUserCell.getTag()).intValue()), z ^ 1);
            }
        }

        public SearchAdapter(Context context) {
            this.mContext = context;
            this.searchAdapterHelper = new SearchAdapterHelper(true);
            this.searchAdapterHelper.setDelegate(new SearchAdapterHelperDelegate(ChannelUsersActivity.this) {
                public void onSetHashtags(ArrayList<HashtagObject> arrayList, HashMap<String, HashtagObject> hashMap) {
                }

                public void onDataSetChanged() {
                    SearchAdapter.this.notifyDataSetChanged();
                }
            });
        }

        public void searchDialogs(final String str) {
            try {
                if (this.searchTimer != null) {
                    this.searchTimer.cancel();
                }
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
            if (str == null) {
                this.searchResult.clear();
                this.searchResultNames.clear();
                this.searchAdapterHelper.queryServerSearch(null, ChannelUsersActivity.this.type != null ? 1 : null, false, true, true, ChannelUsersActivity.this.chatId, ChannelUsersActivity.this.type == 0);
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
                    SearchAdapter.this.processSearch(str);
                }
            }, 200, 300);
        }

        private void processSearch(final String str) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    SearchAdapter.this.searchAdapterHelper.queryServerSearch(str, ChannelUsersActivity.this.selectType != 0, false, true, true, ChannelUsersActivity.this.chatId, ChannelUsersActivity.this.type == 0);
                    if (ChannelUsersActivity.this.selectType == 1) {
                        final ArrayList arrayList = new ArrayList();
                        arrayList.addAll(ContactsController.getInstance(ChannelUsersActivity.this.currentAccount).contacts);
                        Utilities.searchQueue.postRunnable(new Runnable() {
                            public void run() {
                                String toLowerCase = str.trim().toLowerCase();
                                if (toLowerCase.length() == 0) {
                                    SearchAdapter.this.updateSearchResults(new ArrayList(), new ArrayList());
                                    return;
                                }
                                String translitString = LocaleController.getInstance().getTranslitString(toLowerCase);
                                if (toLowerCase.equals(translitString) || translitString.length() == 0) {
                                    translitString = null;
                                }
                                int i = 0;
                                String[] strArr = new String[((translitString != null ? 1 : 0) + 1)];
                                strArr[0] = toLowerCase;
                                if (translitString != null) {
                                    strArr[1] = translitString;
                                }
                                ArrayList arrayList = new ArrayList();
                                ArrayList arrayList2 = new ArrayList();
                                int i2 = 0;
                                while (i2 < arrayList.size()) {
                                    User user = MessagesController.getInstance(ChannelUsersActivity.this.currentAccount).getUser(Integer.valueOf(((TL_contact) arrayList.get(i2)).user_id));
                                    if (user.id != UserConfig.getInstance(ChannelUsersActivity.this.currentAccount).getClientUserId()) {
                                        String toLowerCase2 = ContactsController.formatName(user.first_name, user.last_name).toLowerCase();
                                        String translitString2 = LocaleController.getInstance().getTranslitString(toLowerCase2);
                                        if (toLowerCase2.equals(translitString2)) {
                                            translitString2 = null;
                                        }
                                        int length = strArr.length;
                                        int i3 = i;
                                        int i4 = i3;
                                        while (i3 < length) {
                                            StringBuilder stringBuilder;
                                            String stringBuilder2;
                                            StringBuilder stringBuilder3;
                                            String str = strArr[i3];
                                            if (!toLowerCase2.startsWith(str)) {
                                                StringBuilder stringBuilder4 = new StringBuilder();
                                                stringBuilder4.append(" ");
                                                stringBuilder4.append(str);
                                                if (!toLowerCase2.contains(stringBuilder4.toString())) {
                                                    if (translitString2 != null) {
                                                        if (!translitString2.startsWith(str)) {
                                                            stringBuilder = new StringBuilder();
                                                            stringBuilder.append(" ");
                                                            stringBuilder.append(str);
                                                            if (translitString2.contains(stringBuilder.toString())) {
                                                            }
                                                        }
                                                    }
                                                    if (user.username != null && user.username.startsWith(str)) {
                                                        i4 = 2;
                                                    }
                                                    if (i4 == 0) {
                                                        if (i4 != 1) {
                                                            arrayList2.add(AndroidUtilities.generateSearchName(user.first_name, user.last_name, str));
                                                        } else {
                                                            stringBuilder = new StringBuilder();
                                                            stringBuilder.append("@");
                                                            stringBuilder.append(user.username);
                                                            stringBuilder2 = stringBuilder.toString();
                                                            stringBuilder3 = new StringBuilder();
                                                            stringBuilder3.append("@");
                                                            stringBuilder3.append(str);
                                                            arrayList2.add(AndroidUtilities.generateSearchName(stringBuilder2, null, stringBuilder3.toString()));
                                                        }
                                                        arrayList.add(user);
                                                    } else {
                                                        i3++;
                                                    }
                                                }
                                            }
                                            i4 = 1;
                                            if (i4 == 0) {
                                                i3++;
                                            } else {
                                                if (i4 != 1) {
                                                    stringBuilder = new StringBuilder();
                                                    stringBuilder.append("@");
                                                    stringBuilder.append(user.username);
                                                    stringBuilder2 = stringBuilder.toString();
                                                    stringBuilder3 = new StringBuilder();
                                                    stringBuilder3.append("@");
                                                    stringBuilder3.append(str);
                                                    arrayList2.add(AndroidUtilities.generateSearchName(stringBuilder2, null, stringBuilder3.toString()));
                                                } else {
                                                    arrayList2.add(AndroidUtilities.generateSearchName(user.first_name, user.last_name, str));
                                                }
                                                arrayList.add(user);
                                            }
                                        }
                                    }
                                    i2++;
                                    i = 0;
                                }
                                SearchAdapter.this.updateSearchResults(arrayList, arrayList2);
                            }
                        });
                    }
                }
            });
        }

        private void updateSearchResults(final ArrayList<User> arrayList, final ArrayList<CharSequence> arrayList2) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    SearchAdapter.this.searchResult = arrayList;
                    SearchAdapter.this.searchResultNames = arrayList2;
                    SearchAdapter.this.notifyDataSetChanged();
                }
            });
        }

        public boolean isEnabled(ViewHolder viewHolder) {
            return viewHolder.getItemViewType() != 1;
        }

        public int getItemCount() {
            int size = this.searchResult.size();
            int size2 = this.searchAdapterHelper.getGlobalSearch().size();
            int size3 = this.searchAdapterHelper.getGroupSearch().size();
            int size4 = this.searchAdapterHelper.getGroupSearch2().size();
            int i = 0;
            if (size != 0) {
                i = 0 + (size + 1);
            }
            if (size2 != 0) {
                i += size2 + 1;
            }
            if (size3 != 0) {
                i += size3 + 1;
            }
            return size4 != 0 ? i + (size4 + 1) : i;
        }

        public void notifyDataSetChanged() {
            this.totalCount = 0;
            int size = this.searchAdapterHelper.getGroupSearch().size();
            if (size != 0) {
                this.groupStartRow = 0;
                this.totalCount += size + 1;
            } else {
                this.groupStartRow = -1;
            }
            int size2 = this.searchAdapterHelper.getGroupSearch2().size();
            if (size2 != 0) {
                this.group2StartRow = this.totalCount;
                this.totalCount += size2 + 1;
            } else {
                this.group2StartRow = -1;
            }
            size2 = this.searchResult.size();
            if (size2 != 0) {
                this.contactsStartRow = this.totalCount;
                this.totalCount += size2 + 1;
            } else {
                this.contactsStartRow = -1;
            }
            size2 = this.searchAdapterHelper.getGlobalSearch().size();
            if (size2 != 0) {
                this.globalStartRow = this.totalCount;
                this.totalCount += size2 + 1;
            } else {
                this.globalStartRow = -1;
            }
            super.notifyDataSetChanged();
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public TLObject getItem(int i) {
            int size = this.searchAdapterHelper.getGroupSearch().size();
            if (size != 0) {
                size++;
                if (size <= i) {
                    i -= size;
                } else if (i == 0) {
                    return null;
                } else {
                    return (TLObject) this.searchAdapterHelper.getGroupSearch().get(i - 1);
                }
            }
            size = this.searchAdapterHelper.getGroupSearch2().size();
            if (size != 0) {
                size++;
                if (size <= i) {
                    i -= size;
                } else if (i == 0) {
                    return null;
                } else {
                    return (TLObject) this.searchAdapterHelper.getGroupSearch2().get(i - 1);
                }
            }
            size = this.searchResult.size();
            if (size != 0) {
                size++;
                if (size <= i) {
                    i -= size;
                } else if (i == 0) {
                    return null;
                } else {
                    return (TLObject) this.searchResult.get(i - 1);
                }
            }
            size = this.searchAdapterHelper.getGlobalSearch().size();
            if (size == 0 || size + 1 <= i || i == 0) {
                return null;
            }
            return (TLObject) this.searchAdapterHelper.getGlobalSearch().get(i - 1);
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            if (i != 0) {
                viewGroup = new GraySectionCell(this.mContext);
            } else {
                viewGroup = new ManageChatUserCell(this.mContext, 2, ChannelUsersActivity.this.selectType == 0);
                viewGroup.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                ((ManageChatUserCell) viewGroup).setDelegate(new C20095());
            }
            return new Holder(viewGroup);
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            switch (viewHolder.getItemViewType()) {
                case 0:
                    User user;
                    String lastFoundChannel;
                    int size;
                    CharSequence charSequence;
                    String charSequence2;
                    StringBuilder stringBuilder;
                    CharSequence charSequence3;
                    String lastFoundUsername;
                    int indexOf;
                    Object userName;
                    int indexOf2;
                    ManageChatUserCell manageChatUserCell;
                    TLObject item = getItem(i);
                    if (item instanceof User) {
                        user = (User) item;
                    } else {
                        user = MessagesController.getInstance(ChannelUsersActivity.this.currentAccount).getUser(Integer.valueOf(((ChannelParticipant) item).user_id));
                    }
                    CharSequence charSequence4 = user.username;
                    int size2 = this.searchAdapterHelper.getGroupSearch().size();
                    int i2 = 0;
                    CharSequence charSequence5 = null;
                    if (size2 != 0) {
                        size2++;
                        if (size2 > i) {
                            lastFoundChannel = this.searchAdapterHelper.getLastFoundChannel();
                            i2 = 1;
                            if (i2 == 0) {
                                size = this.searchAdapterHelper.getGroupSearch2().size();
                                if (size != 0) {
                                    size++;
                                    if (size <= i) {
                                        lastFoundChannel = this.searchAdapterHelper.getLastFoundChannel2();
                                    } else {
                                        i -= size;
                                    }
                                }
                            }
                            if (i2 == 0) {
                                size = this.searchResult.size();
                                if (size != 0) {
                                    size++;
                                    if (size <= i) {
                                        charSequence = (CharSequence) this.searchResultNames.get(i - 1);
                                        if (!(charSequence == null || charSequence4 == null || charSequence4.length() <= 0)) {
                                            charSequence2 = charSequence.toString();
                                            stringBuilder = new StringBuilder();
                                            stringBuilder.append("@");
                                            stringBuilder.append(charSequence4);
                                            if (charSequence2.startsWith(stringBuilder.toString())) {
                                                charSequence3 = null;
                                                charSequence5 = charSequence;
                                                i2 = 1;
                                                if (i2 == 0) {
                                                    i2 = this.searchAdapterHelper.getGlobalSearch().size();
                                                    if (i2 != 0 && i2 + 1 > i) {
                                                        lastFoundUsername = this.searchAdapterHelper.getLastFoundUsername();
                                                        if (lastFoundUsername.startsWith("@")) {
                                                            lastFoundUsername = lastFoundUsername.substring(1);
                                                        }
                                                        try {
                                                            charSequence5 = new SpannableStringBuilder();
                                                            charSequence5.append("@");
                                                            charSequence5.append(charSequence4);
                                                            indexOf = charSequence4.toLowerCase().indexOf(lastFoundUsername);
                                                            if (indexOf != -1) {
                                                                i2 = lastFoundUsername.length();
                                                                if (indexOf != 0) {
                                                                    i2++;
                                                                } else {
                                                                    indexOf++;
                                                                }
                                                                charSequence5.setSpan(new ForegroundColorSpan(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText4)), indexOf, i2 + indexOf, 33);
                                                            }
                                                        } catch (Throwable e) {
                                                            FileLog.m3e(e);
                                                            charSequence5 = charSequence4;
                                                        }
                                                    }
                                                }
                                                if (lastFoundChannel != null) {
                                                    userName = UserObject.getUserName(user);
                                                    charSequence3 = new SpannableStringBuilder(userName);
                                                    indexOf2 = userName.toLowerCase().indexOf(lastFoundChannel);
                                                    if (indexOf2 != -1) {
                                                        ((SpannableStringBuilder) charSequence3).setSpan(new ForegroundColorSpan(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText4)), indexOf2, lastFoundChannel.length() + indexOf2, 33);
                                                    }
                                                }
                                                manageChatUserCell = (ManageChatUserCell) viewHolder.itemView;
                                                manageChatUserCell.setTag(Integer.valueOf(i));
                                                manageChatUserCell.setData(user, charSequence3, charSequence5);
                                                return;
                                            }
                                        }
                                        charSequence3 = charSequence;
                                        i2 = 1;
                                        if (i2 == 0) {
                                            i2 = this.searchAdapterHelper.getGlobalSearch().size();
                                            lastFoundUsername = this.searchAdapterHelper.getLastFoundUsername();
                                            if (lastFoundUsername.startsWith("@")) {
                                                lastFoundUsername = lastFoundUsername.substring(1);
                                            }
                                            charSequence5 = new SpannableStringBuilder();
                                            charSequence5.append("@");
                                            charSequence5.append(charSequence4);
                                            indexOf = charSequence4.toLowerCase().indexOf(lastFoundUsername);
                                            if (indexOf != -1) {
                                                i2 = lastFoundUsername.length();
                                                if (indexOf != 0) {
                                                    indexOf++;
                                                } else {
                                                    i2++;
                                                }
                                                charSequence5.setSpan(new ForegroundColorSpan(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText4)), indexOf, i2 + indexOf, 33);
                                            }
                                            break;
                                        }
                                        if (lastFoundChannel != null) {
                                            userName = UserObject.getUserName(user);
                                            charSequence3 = new SpannableStringBuilder(userName);
                                            indexOf2 = userName.toLowerCase().indexOf(lastFoundChannel);
                                            if (indexOf2 != -1) {
                                                ((SpannableStringBuilder) charSequence3).setSpan(new ForegroundColorSpan(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText4)), indexOf2, lastFoundChannel.length() + indexOf2, 33);
                                            }
                                        }
                                        manageChatUserCell = (ManageChatUserCell) viewHolder.itemView;
                                        manageChatUserCell.setTag(Integer.valueOf(i));
                                        manageChatUserCell.setData(user, charSequence3, charSequence5);
                                        return;
                                    }
                                    i -= size;
                                }
                            }
                            charSequence3 = null;
                            if (i2 == 0) {
                                i2 = this.searchAdapterHelper.getGlobalSearch().size();
                                lastFoundUsername = this.searchAdapterHelper.getLastFoundUsername();
                                if (lastFoundUsername.startsWith("@")) {
                                    lastFoundUsername = lastFoundUsername.substring(1);
                                }
                                charSequence5 = new SpannableStringBuilder();
                                charSequence5.append("@");
                                charSequence5.append(charSequence4);
                                indexOf = charSequence4.toLowerCase().indexOf(lastFoundUsername);
                                if (indexOf != -1) {
                                    i2 = lastFoundUsername.length();
                                    if (indexOf != 0) {
                                        i2++;
                                    } else {
                                        indexOf++;
                                    }
                                    charSequence5.setSpan(new ForegroundColorSpan(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText4)), indexOf, i2 + indexOf, 33);
                                }
                            }
                            if (lastFoundChannel != null) {
                                userName = UserObject.getUserName(user);
                                charSequence3 = new SpannableStringBuilder(userName);
                                indexOf2 = userName.toLowerCase().indexOf(lastFoundChannel);
                                if (indexOf2 != -1) {
                                    ((SpannableStringBuilder) charSequence3).setSpan(new ForegroundColorSpan(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText4)), indexOf2, lastFoundChannel.length() + indexOf2, 33);
                                }
                            }
                            manageChatUserCell = (ManageChatUserCell) viewHolder.itemView;
                            manageChatUserCell.setTag(Integer.valueOf(i));
                            manageChatUserCell.setData(user, charSequence3, charSequence5);
                            return;
                        }
                        i -= size2;
                    }
                    lastFoundChannel = null;
                    if (i2 == 0) {
                        size = this.searchAdapterHelper.getGroupSearch2().size();
                        if (size != 0) {
                            size++;
                            if (size <= i) {
                                i -= size;
                            } else {
                                lastFoundChannel = this.searchAdapterHelper.getLastFoundChannel2();
                            }
                        }
                    }
                    if (i2 == 0) {
                        size = this.searchResult.size();
                        if (size != 0) {
                            size++;
                            if (size <= i) {
                                i -= size;
                            } else {
                                charSequence = (CharSequence) this.searchResultNames.get(i - 1);
                                charSequence2 = charSequence.toString();
                                stringBuilder = new StringBuilder();
                                stringBuilder.append("@");
                                stringBuilder.append(charSequence4);
                                if (charSequence2.startsWith(stringBuilder.toString())) {
                                    charSequence3 = null;
                                    charSequence5 = charSequence;
                                    i2 = 1;
                                    if (i2 == 0) {
                                        i2 = this.searchAdapterHelper.getGlobalSearch().size();
                                        lastFoundUsername = this.searchAdapterHelper.getLastFoundUsername();
                                        if (lastFoundUsername.startsWith("@")) {
                                            lastFoundUsername = lastFoundUsername.substring(1);
                                        }
                                        charSequence5 = new SpannableStringBuilder();
                                        charSequence5.append("@");
                                        charSequence5.append(charSequence4);
                                        indexOf = charSequence4.toLowerCase().indexOf(lastFoundUsername);
                                        if (indexOf != -1) {
                                            i2 = lastFoundUsername.length();
                                            if (indexOf != 0) {
                                                indexOf++;
                                            } else {
                                                i2++;
                                            }
                                            charSequence5.setSpan(new ForegroundColorSpan(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText4)), indexOf, i2 + indexOf, 33);
                                        }
                                    }
                                    if (lastFoundChannel != null) {
                                        userName = UserObject.getUserName(user);
                                        charSequence3 = new SpannableStringBuilder(userName);
                                        indexOf2 = userName.toLowerCase().indexOf(lastFoundChannel);
                                        if (indexOf2 != -1) {
                                            ((SpannableStringBuilder) charSequence3).setSpan(new ForegroundColorSpan(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText4)), indexOf2, lastFoundChannel.length() + indexOf2, 33);
                                        }
                                    }
                                    manageChatUserCell = (ManageChatUserCell) viewHolder.itemView;
                                    manageChatUserCell.setTag(Integer.valueOf(i));
                                    manageChatUserCell.setData(user, charSequence3, charSequence5);
                                    return;
                                }
                                charSequence3 = charSequence;
                                i2 = 1;
                                if (i2 == 0) {
                                    i2 = this.searchAdapterHelper.getGlobalSearch().size();
                                    lastFoundUsername = this.searchAdapterHelper.getLastFoundUsername();
                                    if (lastFoundUsername.startsWith("@")) {
                                        lastFoundUsername = lastFoundUsername.substring(1);
                                    }
                                    charSequence5 = new SpannableStringBuilder();
                                    charSequence5.append("@");
                                    charSequence5.append(charSequence4);
                                    indexOf = charSequence4.toLowerCase().indexOf(lastFoundUsername);
                                    if (indexOf != -1) {
                                        i2 = lastFoundUsername.length();
                                        if (indexOf != 0) {
                                            i2++;
                                        } else {
                                            indexOf++;
                                        }
                                        charSequence5.setSpan(new ForegroundColorSpan(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText4)), indexOf, i2 + indexOf, 33);
                                    }
                                }
                                if (lastFoundChannel != null) {
                                    userName = UserObject.getUserName(user);
                                    charSequence3 = new SpannableStringBuilder(userName);
                                    indexOf2 = userName.toLowerCase().indexOf(lastFoundChannel);
                                    if (indexOf2 != -1) {
                                        ((SpannableStringBuilder) charSequence3).setSpan(new ForegroundColorSpan(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText4)), indexOf2, lastFoundChannel.length() + indexOf2, 33);
                                    }
                                }
                                manageChatUserCell = (ManageChatUserCell) viewHolder.itemView;
                                manageChatUserCell.setTag(Integer.valueOf(i));
                                manageChatUserCell.setData(user, charSequence3, charSequence5);
                                return;
                            }
                        }
                    }
                    charSequence3 = null;
                    if (i2 == 0) {
                        i2 = this.searchAdapterHelper.getGlobalSearch().size();
                        lastFoundUsername = this.searchAdapterHelper.getLastFoundUsername();
                        if (lastFoundUsername.startsWith("@")) {
                            lastFoundUsername = lastFoundUsername.substring(1);
                        }
                        charSequence5 = new SpannableStringBuilder();
                        charSequence5.append("@");
                        charSequence5.append(charSequence4);
                        indexOf = charSequence4.toLowerCase().indexOf(lastFoundUsername);
                        if (indexOf != -1) {
                            i2 = lastFoundUsername.length();
                            if (indexOf != 0) {
                                indexOf++;
                            } else {
                                i2++;
                            }
                            charSequence5.setSpan(new ForegroundColorSpan(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText4)), indexOf, i2 + indexOf, 33);
                        }
                    }
                    if (lastFoundChannel != null) {
                        userName = UserObject.getUserName(user);
                        charSequence3 = new SpannableStringBuilder(userName);
                        indexOf2 = userName.toLowerCase().indexOf(lastFoundChannel);
                        if (indexOf2 != -1) {
                            ((SpannableStringBuilder) charSequence3).setSpan(new ForegroundColorSpan(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText4)), indexOf2, lastFoundChannel.length() + indexOf2, 33);
                        }
                    }
                    manageChatUserCell = (ManageChatUserCell) viewHolder.itemView;
                    manageChatUserCell.setTag(Integer.valueOf(i));
                    manageChatUserCell.setData(user, charSequence3, charSequence5);
                    return;
                case 1:
                    GraySectionCell graySectionCell = (GraySectionCell) viewHolder.itemView;
                    if (i == this.groupStartRow) {
                        if (ChannelUsersActivity.this.type == 0) {
                            graySectionCell.setText(LocaleController.getString("ChannelRestrictedUsers", C0446R.string.ChannelRestrictedUsers).toUpperCase());
                            return;
                        } else if (ChatObject.isChannel(ChannelUsersActivity.this.currentChat) == 0 || ChannelUsersActivity.this.currentChat.megagroup != 0) {
                            graySectionCell.setText(LocaleController.getString("ChannelMembers", C0446R.string.ChannelMembers).toUpperCase());
                            return;
                        } else {
                            ChannelUsersActivity.this.actionBar.setTitle(LocaleController.getString("ChannelSubscribers", C0446R.string.ChannelSubscribers));
                            return;
                        }
                    } else if (i == this.group2StartRow) {
                        graySectionCell.setText(LocaleController.getString("ChannelBlockedUsers", C0446R.string.ChannelBlockedUsers).toUpperCase());
                        return;
                    } else if (i == this.globalStartRow) {
                        graySectionCell.setText(LocaleController.getString("GlobalSearch", C0446R.string.GlobalSearch).toUpperCase());
                        return;
                    } else if (i == this.contactsStartRow) {
                        graySectionCell.setText(LocaleController.getString("Contacts", C0446R.string.Contacts).toUpperCase());
                        return;
                    } else {
                        return;
                    }
                default:
                    return;
            }
        }

        public void onViewRecycled(ViewHolder viewHolder) {
            if (viewHolder.itemView instanceof ManageChatUserCell) {
                ((ManageChatUserCell) viewHolder.itemView).recycle();
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

    public ChannelUsersActivity(Bundle bundle) {
        super(bundle);
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
        this.actionBar.setBackButtonImage(C0446R.drawable.ic_ab_back);
        int i = 1;
        this.actionBar.setAllowOverlayTitle(true);
        if (this.type == 0) {
            this.actionBar.setTitle(LocaleController.getString("ChannelBlacklist", C0446R.string.ChannelBlacklist));
        } else if (this.type == 1) {
            this.actionBar.setTitle(LocaleController.getString("ChannelAdministrators", C0446R.string.ChannelAdministrators));
        } else if (this.type == 2) {
            if (this.selectType == 0) {
                if (!ChatObject.isChannel(this.currentChat) || this.currentChat.megagroup) {
                    this.actionBar.setTitle(LocaleController.getString("ChannelMembers", C0446R.string.ChannelMembers));
                } else {
                    this.actionBar.setTitle(LocaleController.getString("ChannelSubscribers", C0446R.string.ChannelSubscribers));
                }
            } else if (this.selectType == 1) {
                this.actionBar.setTitle(LocaleController.getString("ChannelAddAdmin", C0446R.string.ChannelAddAdmin));
            } else if (this.selectType == 2) {
                this.actionBar.setTitle(LocaleController.getString("ChannelBlockUser", C0446R.string.ChannelBlockUser));
            }
        }
        this.actionBar.setActionBarMenuOnItemClick(new C19961());
        if (this.selectType != 0 || this.type == 2 || this.type == 0) {
            this.searchListViewAdapter = new SearchAdapter(context);
            this.searchItem = this.actionBar.createMenu().addItem(0, (int) C0446R.drawable.ic_ab_search).setIsSearchField(true).setActionBarMenuItemSearchListener(new C19972());
            this.searchItem.getSearchField().setHint(LocaleController.getString("Search", C0446R.string.Search));
        }
        this.fragmentView = new FrameLayout(context);
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        this.emptyView = new EmptyTextProgressView(context);
        if (this.type == 0 || this.type == 2) {
            this.emptyView.setText(LocaleController.getString("NoResult", C0446R.string.NoResult));
        }
        frameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView = new RecyclerListView(context);
        this.listView.setEmptyView(this.emptyView);
        this.listView.setLayoutManager(new LinearLayoutManager(context, 1, false));
        RecyclerListView recyclerListView = this.listView;
        Adapter listAdapter = new ListAdapter(context);
        this.listViewAdapter = listAdapter;
        recyclerListView.setAdapter(listAdapter);
        context = this.listView;
        if (!LocaleController.isRTL) {
            i = 2;
        }
        context.setVerticalScrollbarPosition(i);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setOnItemClickListener(new C20013());
        this.listView.setOnItemLongClickListener(new C20024());
        if (this.searchItem != null) {
            this.listView.setOnScrollListener(new C20035());
        }
        if (this.loadingUsers != null) {
            this.emptyView.showProgress();
        } else {
            this.emptyView.showTextView();
        }
        updateRows();
        return this.fragmentView;
    }

    private boolean createMenuForParticipant(final ChannelParticipant channelParticipant, boolean z) {
        if (channelParticipant != null) {
            if (this.selectType == 0) {
                if (channelParticipant.user_id == UserConfig.getInstance(this.currentAccount).getClientUserId()) {
                    return false;
                }
                CharSequence[] charSequenceArr = null;
                if (this.type == 2) {
                    boolean z2;
                    boolean z3;
                    ArrayList arrayList;
                    ArrayList arrayList2;
                    final User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(channelParticipant.user_id));
                    if (!(channelParticipant instanceof TL_channelParticipant)) {
                        if (!(channelParticipant instanceof TL_channelParticipantBanned)) {
                            z2 = false;
                            z3 = ((channelParticipant instanceof TL_channelParticipantAdmin) && !(channelParticipant instanceof TL_channelParticipantCreator)) || channelParticipant.can_edit;
                            if (z) {
                                arrayList = new ArrayList();
                                arrayList2 = new ArrayList();
                            } else {
                                arrayList2 = null;
                            }
                            if (z2 && ChatObject.canAddAdmins(this.currentChat)) {
                                if (z) {
                                    return true;
                                }
                                arrayList.add(LocaleController.getString("SetAsAdmin", C0446R.string.SetAsAdmin));
                                arrayList2.add(Integer.valueOf(0));
                            }
                            if (ChatObject.canBlockUsers(this.currentChat) && z3) {
                                if (z) {
                                    return true;
                                }
                                if (this.currentChat.megagroup) {
                                    arrayList.add(LocaleController.getString("ChannelRemoveUser", C0446R.string.ChannelRemoveUser));
                                    arrayList2.add(Integer.valueOf(2));
                                } else {
                                    arrayList.add(LocaleController.getString("KickFromSupergroup", C0446R.string.KickFromSupergroup));
                                    arrayList2.add(Integer.valueOf(1));
                                    arrayList.add(LocaleController.getString("KickFromGroup", C0446R.string.KickFromGroup));
                                    arrayList2.add(Integer.valueOf(2));
                                }
                            }
                            if (arrayList2 != null) {
                                if (arrayList2.isEmpty()) {
                                    z = new Builder(getParentActivity());
                                    z.setItems((CharSequence[]) arrayList.toArray(new CharSequence[arrayList2.size()]), new OnClickListener() {
                                        public void onClick(DialogInterface dialogInterface, final int i) {
                                            if (((Integer) arrayList2.get(i)).intValue() == 2) {
                                                MessagesController.getInstance(ChannelUsersActivity.this.currentAccount).deleteUserFromChat(ChannelUsersActivity.this.chatId, user, null);
                                                for (dialogInterface = null; dialogInterface < ChannelUsersActivity.this.participants.size(); dialogInterface++) {
                                                    if (((ChannelParticipant) ChannelUsersActivity.this.participants.get(dialogInterface)).user_id == channelParticipant.user_id) {
                                                        ChannelUsersActivity.this.participants.remove(dialogInterface);
                                                        ChannelUsersActivity.this.updateRows();
                                                        ChannelUsersActivity.this.listViewAdapter.notifyDataSetChanged();
                                                        return;
                                                    }
                                                }
                                                return;
                                            }
                                            DialogInterface channelRightsEditActivity = new ChannelRightsEditActivity(user.id, ChannelUsersActivity.this.chatId, channelParticipant.admin_rights, channelParticipant.banned_rights, ((Integer) arrayList2.get(i)).intValue(), true);
                                            channelRightsEditActivity.setDelegate(new ChannelRightsEditActivityDelegate() {
                                                public void didSetRights(int i, TL_channelAdminRights tL_channelAdminRights, TL_channelBannedRights tL_channelBannedRights) {
                                                    int i2 = 0;
                                                    if (((Integer) arrayList2.get(i)).intValue() == 0) {
                                                        while (i2 < ChannelUsersActivity.this.participants.size()) {
                                                            if (((ChannelParticipant) ChannelUsersActivity.this.participants.get(i2)).user_id == channelParticipant.user_id) {
                                                                if (i == 1) {
                                                                    i = new TL_channelParticipantAdmin();
                                                                } else {
                                                                    i = new TL_channelParticipant();
                                                                }
                                                                i.admin_rights = tL_channelAdminRights;
                                                                i.banned_rights = tL_channelBannedRights;
                                                                i.inviter_id = UserConfig.getInstance(ChannelUsersActivity.this.currentAccount).getClientUserId();
                                                                i.user_id = channelParticipant.user_id;
                                                                i.date = channelParticipant.date;
                                                                ChannelUsersActivity.this.participants.set(i2, i);
                                                                return;
                                                            }
                                                            i2++;
                                                        }
                                                    } else if (((Integer) arrayList2.get(i)).intValue() == 1 && i == 0) {
                                                        while (i2 < ChannelUsersActivity.this.participants.size()) {
                                                            if (((ChannelParticipant) ChannelUsersActivity.this.participants.get(i2)).user_id == channelParticipant.user_id) {
                                                                ChannelUsersActivity.this.participants.remove(i2);
                                                                ChannelUsersActivity.this.updateRows();
                                                                ChannelUsersActivity.this.listViewAdapter.notifyDataSetChanged();
                                                                return;
                                                            }
                                                            i2++;
                                                        }
                                                    }
                                                }
                                            });
                                            ChannelUsersActivity.this.presentFragment(channelRightsEditActivity);
                                        }
                                    });
                                    showDialog(z.create());
                                }
                            }
                            return false;
                        }
                    }
                    z2 = true;
                    if (channelParticipant instanceof TL_channelParticipantAdmin) {
                    }
                    if (z) {
                        arrayList2 = null;
                    } else {
                        arrayList = new ArrayList();
                        arrayList2 = new ArrayList();
                    }
                    if (z) {
                        return true;
                    }
                    arrayList.add(LocaleController.getString("SetAsAdmin", C0446R.string.SetAsAdmin));
                    arrayList2.add(Integer.valueOf(0));
                    if (z) {
                        return true;
                    }
                    if (this.currentChat.megagroup) {
                        arrayList.add(LocaleController.getString("ChannelRemoveUser", C0446R.string.ChannelRemoveUser));
                        arrayList2.add(Integer.valueOf(2));
                    } else {
                        arrayList.add(LocaleController.getString("KickFromSupergroup", C0446R.string.KickFromSupergroup));
                        arrayList2.add(Integer.valueOf(1));
                        arrayList.add(LocaleController.getString("KickFromGroup", C0446R.string.KickFromGroup));
                        arrayList2.add(Integer.valueOf(2));
                    }
                    if (arrayList2 != null) {
                        if (arrayList2.isEmpty()) {
                            z = new Builder(getParentActivity());
                            z.setItems((CharSequence[]) arrayList.toArray(new CharSequence[arrayList2.size()]), /* anonymous class already generated */);
                            showDialog(z.create());
                        }
                    }
                    return false;
                }
                if (this.type == 0 && ChatObject.canBlockUsers(this.currentChat)) {
                    if (z) {
                        return true;
                    }
                    charSequenceArr = new CharSequence[]{LocaleController.getString("Unban", C0446R.string.Unban)};
                } else if (this.type == 1 && ChatObject.canAddAdmins(this.currentChat) && channelParticipant.can_edit) {
                    if (z) {
                        return true;
                    }
                    charSequenceArr = new CharSequence[]{LocaleController.getString("ChannelRemoveUserAdmin", C0446R.string.ChannelRemoveUserAdmin)};
                }
                if (charSequenceArr == null) {
                    return false;
                }
                z = new Builder(getParentActivity());
                z.setItems(charSequenceArr, new OnClickListener() {

                    /* renamed from: org.telegram.ui.ChannelUsersActivity$7$1 */
                    class C20051 implements RequestDelegate {
                        C20051() {
                        }

                        public void run(TLObject tLObject, TL_error tL_error) {
                            if (tLObject != null) {
                                final Updates updates = (Updates) tLObject;
                                MessagesController.getInstance(ChannelUsersActivity.this.currentAccount).processUpdates(updates, false);
                                if (updates.chats.isEmpty() == null) {
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
                        if (ChannelUsersActivity.this.type == null) {
                            ChannelUsersActivity.this.participants.remove(channelParticipant);
                            ChannelUsersActivity.this.updateRows();
                            ChannelUsersActivity.this.listViewAdapter.notifyDataSetChanged();
                            dialogInterface = new TL_channels_editBanned();
                            dialogInterface.user_id = MessagesController.getInstance(ChannelUsersActivity.this.currentAccount).getInputUser(channelParticipant.user_id);
                            dialogInterface.channel = MessagesController.getInstance(ChannelUsersActivity.this.currentAccount).getInputChannel(ChannelUsersActivity.this.chatId);
                            dialogInterface.banned_rights = new TL_channelBannedRights();
                            ConnectionsManager.getInstance(ChannelUsersActivity.this.currentAccount).sendRequest(dialogInterface, new C20051());
                        } else if (ChannelUsersActivity.this.type == 1) {
                            MessagesController.getInstance(ChannelUsersActivity.this.currentAccount).setUserAdminRole(ChannelUsersActivity.this.chatId, MessagesController.getInstance(ChannelUsersActivity.this.currentAccount).getUser(Integer.valueOf(channelParticipant.user_id)), new TL_channelAdminRights(), ChannelUsersActivity.this.currentChat.megagroup, ChannelUsersActivity.this);
                        } else if (ChannelUsersActivity.this.type == 2) {
                            MessagesController.getInstance(ChannelUsersActivity.this.currentAccount).deleteUserFromChat(ChannelUsersActivity.this.chatId, MessagesController.getInstance(ChannelUsersActivity.this.currentAccount).getUser(Integer.valueOf(channelParticipant.user_id)), null);
                        }
                    }
                });
                showDialog(z.create());
                return true;
            }
        }
        return false;
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.chatInfoDidLoaded) {
            ChatFull chatFull = (ChatFull) objArr[0];
            i2 = ((Boolean) objArr[2]).booleanValue();
            if (chatFull.id == this.chatId && i2 == 0) {
                AndroidUtilities.runOnUIThread(new C10118());
            }
        }
    }

    private int getChannelAdminParticipantType(ChannelParticipant channelParticipant) {
        if (!(channelParticipant instanceof TL_channelParticipantCreator)) {
            if (!(channelParticipant instanceof TL_channelParticipantSelf)) {
                return (channelParticipant instanceof TL_channelParticipantAdmin) != null ? 1 : 2;
            }
        }
        return null;
    }

    private void getChannelParticipants(int i, int i2) {
        if (!this.loadingUsers) {
            this.loadingUsers = true;
            if (!(this.emptyView == null || this.firstLoaded)) {
                this.emptyView.showProgress();
            }
            if (this.listViewAdapter != null) {
                this.listViewAdapter.notifyDataSetChanged();
            }
            TLObject tL_channels_getParticipants = new TL_channels_getParticipants();
            tL_channels_getParticipants.channel = MessagesController.getInstance(this.currentAccount).getInputChannel(this.chatId);
            final boolean z = this.firstEndReached;
            if (this.type == 0) {
                if (z) {
                    tL_channels_getParticipants.filter = new TL_channelParticipantsKicked();
                } else {
                    tL_channels_getParticipants.filter = new TL_channelParticipantsBanned();
                }
            } else if (this.type == 1) {
                tL_channels_getParticipants.filter = new TL_channelParticipantsAdmins();
            } else if (this.type == 2) {
                tL_channels_getParticipants.filter = new TL_channelParticipantsRecent();
            }
            tL_channels_getParticipants.filter.f32q = TtmlNode.ANONYMOUS_REGION_ID;
            tL_channels_getParticipants.offset = i;
            tL_channels_getParticipants.limit = i2;
            ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_channels_getParticipants, new RequestDelegate() {
                public void run(final TLObject tLObject, final TL_error tL_error) {
                    AndroidUtilities.runOnUIThread(new Runnable() {

                        /* renamed from: org.telegram.ui.ChannelUsersActivity$9$1$1 */
                        class C10121 implements Comparator<ChannelParticipant> {
                            C10121() {
                            }

                            public int compare(ChannelParticipant channelParticipant, ChannelParticipant channelParticipant2) {
                                channelParticipant2 = MessagesController.getInstance(ChannelUsersActivity.this.currentAccount).getUser(Integer.valueOf(channelParticipant2.user_id));
                                channelParticipant = MessagesController.getInstance(ChannelUsersActivity.this.currentAccount).getUser(Integer.valueOf(channelParticipant.user_id));
                                channelParticipant2 = (channelParticipant2 == null || channelParticipant2.status == null) ? null : channelParticipant2.id == UserConfig.getInstance(ChannelUsersActivity.this.currentAccount).getClientUserId() ? ConnectionsManager.getInstance(ChannelUsersActivity.this.currentAccount).getCurrentTime() + 50000 : channelParticipant2.status.expires;
                                channelParticipant = (channelParticipant == null || channelParticipant.status == null) ? null : channelParticipant.id == UserConfig.getInstance(ChannelUsersActivity.this.currentAccount).getClientUserId() ? ConnectionsManager.getInstance(ChannelUsersActivity.this.currentAccount).getCurrentTime() + 50000 : channelParticipant.status.expires;
                                if (channelParticipant2 <= null || channelParticipant <= null) {
                                    if (channelParticipant2 >= null || channelParticipant >= null) {
                                        if ((channelParticipant2 < null && channelParticipant > null) || (channelParticipant2 == null && channelParticipant != null)) {
                                            return -1;
                                        }
                                        if ((channelParticipant >= null || channelParticipant2 <= null) && (channelParticipant != null || channelParticipant2 == null)) {
                                            return 0;
                                        }
                                        return 1;
                                    } else if (channelParticipant2 > channelParticipant) {
                                        return 1;
                                    } else {
                                        if (channelParticipant2 < channelParticipant) {
                                            return -1;
                                        }
                                        return 0;
                                    }
                                } else if (channelParticipant2 > channelParticipant) {
                                    return 1;
                                } else {
                                    if (channelParticipant2 < channelParticipant) {
                                        return -1;
                                    }
                                    return 0;
                                }
                            }
                        }

                        /* renamed from: org.telegram.ui.ChannelUsersActivity$9$1$2 */
                        class C10132 implements Comparator<ChannelParticipant> {
                            C10132() {
                            }

                            public int compare(ChannelParticipant channelParticipant, ChannelParticipant channelParticipant2) {
                                channelParticipant = ChannelUsersActivity.this.getChannelAdminParticipantType(channelParticipant);
                                channelParticipant2 = ChannelUsersActivity.this.getChannelAdminParticipantType(channelParticipant2);
                                if (channelParticipant > channelParticipant2) {
                                    return 1;
                                }
                                return channelParticipant < channelParticipant2 ? -1 : null;
                            }
                        }

                        public void run() {
                            int access$3800 = ChannelUsersActivity.this.firstLoaded ^ 1;
                            int i = 0;
                            ChannelUsersActivity.this.loadingUsers = false;
                            ChannelUsersActivity.this.firstLoaded = true;
                            if (ChannelUsersActivity.this.emptyView != null) {
                                ChannelUsersActivity.this.emptyView.showTextView();
                            }
                            if (tL_error == null) {
                                TL_channels_channelParticipants tL_channels_channelParticipants = (TL_channels_channelParticipants) tLObject;
                                MessagesController.getInstance(ChannelUsersActivity.this.currentAccount).putUsers(tL_channels_channelParticipants.users, false);
                                int clientUserId = UserConfig.getInstance(ChannelUsersActivity.this.currentAccount).getClientUserId();
                                if (ChannelUsersActivity.this.selectType != 0) {
                                    for (int i2 = 0; i2 < tL_channels_channelParticipants.participants.size(); i2++) {
                                        if (((ChannelParticipant) tL_channels_channelParticipants.participants.get(i2)).user_id == clientUserId) {
                                            tL_channels_channelParticipants.participants.remove(i2);
                                            break;
                                        }
                                    }
                                }
                                if (ChannelUsersActivity.this.type != 0) {
                                    ChannelUsersActivity.this.participantsMap.clear();
                                    ChannelUsersActivity.this.participants = tL_channels_channelParticipants.participants;
                                } else if (z) {
                                    ChannelUsersActivity.this.participants2 = tL_channels_channelParticipants.participants;
                                } else {
                                    ChannelUsersActivity.this.participants2 = new ArrayList();
                                    ChannelUsersActivity.this.participantsMap.clear();
                                    ChannelUsersActivity.this.participants = tL_channels_channelParticipants.participants;
                                    if (access$3800 != 0) {
                                        ChannelUsersActivity.this.firstLoaded = false;
                                    }
                                    ChannelUsersActivity.this.firstEndReached = true;
                                    ChannelUsersActivity.this.getChannelParticipants(0, Callback.DEFAULT_DRAG_ANIMATION_DURATION);
                                }
                                while (i < tL_channels_channelParticipants.participants.size()) {
                                    ChannelParticipant channelParticipant = (ChannelParticipant) tL_channels_channelParticipants.participants.get(i);
                                    ChannelUsersActivity.this.participantsMap.put(channelParticipant.user_id, channelParticipant);
                                    i++;
                                }
                                try {
                                    if (ChannelUsersActivity.this.type != 0) {
                                        if (ChannelUsersActivity.this.type != 2) {
                                            if (ChannelUsersActivity.this.type == 1) {
                                                Collections.sort(tL_channels_channelParticipants.participants, new C10132());
                                            }
                                        }
                                    }
                                    Collections.sort(tL_channels_channelParticipants.participants, new C10121());
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

    protected void onTransitionAnimationEnd(boolean z, boolean z2) {
        if (z && !z2 && this.needOpenSearch) {
            this.searchItem.openSearch(true);
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        AnonymousClass10 anonymousClass10 = new ThemeDescriptionDelegate() {
            public void didSetColor() {
                if (ChannelUsersActivity.this.listView != null) {
                    int childCount = ChannelUsersActivity.this.listView.getChildCount();
                    for (int i = 0; i < childCount; i++) {
                        View childAt = ChannelUsersActivity.this.listView.getChildAt(i);
                        if (childAt instanceof ManageChatUserCell) {
                            ((ManageChatUserCell) childAt).update(0);
                        }
                    }
                }
            }
        };
        ThemeDescription[] themeDescriptionArr = new ThemeDescription[32];
        int i = 3;
        themeDescriptionArr[0] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{ManageChatUserCell.class, TextSettingsCell.class, ManageChatTextCell.class, RadioCell.class}, null, null, null, Theme.key_windowBackgroundWhite);
        themeDescriptionArr[1] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundGray);
        themeDescriptionArr[2] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault);
        themeDescriptionArr[i] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault);
        themeDescriptionArr[4] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon);
        themeDescriptionArr[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle);
        themeDescriptionArr[6] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector);
        themeDescriptionArr[7] = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector);
        themeDescriptionArr[8] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider);
        View view = this.listView;
        View view2 = view;
        themeDescriptionArr[9] = new ThemeDescription(view2, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow);
        themeDescriptionArr[10] = new ThemeDescription(this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText4);
        themeDescriptionArr[11] = new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        themeDescriptionArr[12] = new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteValueText);
        themeDescriptionArr[13] = new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueImageView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayIcon);
        view = this.listView;
        view2 = view;
        themeDescriptionArr[14] = new ThemeDescription(view2, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow);
        themeDescriptionArr[15] = new ThemeDescription(this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlueHeader);
        themeDescriptionArr[16] = new ThemeDescription(this.listView, 0, new Class[]{RadioCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        view = this.listView;
        view2 = view;
        themeDescriptionArr[17] = new ThemeDescription(view2, ThemeDescription.FLAG_CHECKBOX, new Class[]{RadioCell.class}, new String[]{"radioButton"}, null, null, null, Theme.key_radioBackground);
        view = this.listView;
        view2 = view;
        themeDescriptionArr[18] = new ThemeDescription(view2, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{RadioCell.class}, new String[]{"radioButton"}, null, null, null, Theme.key_radioBackgroundChecked);
        themeDescriptionArr[19] = new ThemeDescription(this.listView, 0, new Class[]{ManageChatUserCell.class}, new String[]{"nameTextView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        ThemeDescriptionDelegate themeDescriptionDelegate = anonymousClass10;
        themeDescriptionArr[20] = new ThemeDescription(this.listView, 0, new Class[]{ManageChatUserCell.class}, new String[]{"statusColor"}, null, null, themeDescriptionDelegate, Theme.key_windowBackgroundWhiteGrayText);
        themeDescriptionArr[21] = new ThemeDescription(this.listView, 0, new Class[]{ManageChatUserCell.class}, new String[]{"statusOnlineColor"}, null, null, themeDescriptionDelegate, Theme.key_windowBackgroundWhiteBlueText);
        View view3 = this.listView;
        Class[] clsArr = new Class[]{ManageChatUserCell.class};
        Drawable[] drawableArr = new Drawable[i];
        drawableArr[0] = Theme.avatar_photoDrawable;
        drawableArr[1] = Theme.avatar_broadcastDrawable;
        drawableArr[2] = Theme.avatar_savedDrawable;
        themeDescriptionArr[22] = new ThemeDescription(view3, 0, clsArr, null, drawableArr, null, Theme.key_avatar_text);
        AnonymousClass10 anonymousClass102 = anonymousClass10;
        themeDescriptionArr[23] = new ThemeDescription(null, 0, null, null, null, anonymousClass102, Theme.key_avatar_backgroundRed);
        themeDescriptionArr[24] = new ThemeDescription(null, 0, null, null, null, anonymousClass102, Theme.key_avatar_backgroundOrange);
        themeDescriptionArr[25] = new ThemeDescription(null, 0, null, null, null, anonymousClass102, Theme.key_avatar_backgroundViolet);
        themeDescriptionArr[26] = new ThemeDescription(null, 0, null, null, null, anonymousClass102, Theme.key_avatar_backgroundGreen);
        themeDescriptionArr[27] = new ThemeDescription(null, 0, null, null, null, anonymousClass102, Theme.key_avatar_backgroundCyan);
        themeDescriptionArr[28] = new ThemeDescription(null, 0, null, null, null, anonymousClass102, Theme.key_avatar_backgroundBlue);
        themeDescriptionArr[29] = new ThemeDescription(null, 0, null, null, null, anonymousClass102, Theme.key_avatar_backgroundPink);
        themeDescriptionArr[30] = new ThemeDescription(this.listView, 0, new Class[]{ManageChatTextCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        themeDescriptionArr[31] = new ThemeDescription(this.listView, 0, new Class[]{ManageChatTextCell.class}, new String[]{"imageView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayIcon);
        return themeDescriptionArr;
    }
}
