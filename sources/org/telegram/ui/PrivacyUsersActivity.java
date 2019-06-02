package org.telegram.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import java.util.ArrayList;
import java.util.Iterator;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.ManageChatTextCell;
import org.telegram.ui.Cells.ManageChatUserCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;
import org.telegram.ui.ContactsActivity.ContactsActivityDelegate;

public class PrivacyUsersActivity extends BaseFragment implements NotificationCenterDelegate, ContactsActivityDelegate {
    private int blockUserDetailRow;
    private int blockUserRow;
    private boolean blockedUsersActivity = true;
    private PrivacyActivityDelegate delegate;
    private EmptyTextProgressView emptyView;
    private boolean isAlwaysShare;
    private boolean isGroup;
    private RecyclerListView listView;
    private ListAdapter listViewAdapter;
    private int rowCount;
    private ArrayList<Integer> uidArray;
    private int usersDetailRow;
    private int usersEndRow;
    private int usersHeaderRow;
    private int usersStartRow;

    public interface PrivacyActivityDelegate {
        void didUpdateUserList(ArrayList<Integer> arrayList, boolean z);
    }

    private class ListAdapter extends SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public int getItemCount() {
            return PrivacyUsersActivity.this.rowCount;
        }

        public boolean isEnabled(ViewHolder viewHolder) {
            int itemViewType = viewHolder.getItemViewType();
            return itemViewType == 0 || itemViewType == 2;
        }

        public /* synthetic */ boolean lambda$onCreateViewHolder$0$PrivacyUsersActivity$ListAdapter(ManageChatUserCell manageChatUserCell, boolean z) {
            if (z) {
                PrivacyUsersActivity.this.showUnblockAlert(((Integer) manageChatUserCell.getTag()).intValue());
            }
            return true;
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view;
            String str = "windowBackgroundWhite";
            if (i == 0) {
                View manageChatUserCell = new ManageChatUserCell(this.mContext, 7, 6, true);
                manageChatUserCell.setBackgroundColor(Theme.getColor(str));
                manageChatUserCell.setDelegate(new -$$Lambda$PrivacyUsersActivity$ListAdapter$ah_jQyMOHlRewlEcZgEQccTwPTg(this));
                view = manageChatUserCell;
            } else if (i == 1) {
                view = new TextInfoPrivacyCell(this.mContext);
            } else if (i != 2) {
                View headerCell = new HeaderCell(this.mContext, false, 21, 11, false);
                headerCell.setBackgroundColor(Theme.getColor(str));
                headerCell.setHeight(43);
            } else {
                view = new ManageChatTextCell(this.mContext);
                view.setBackgroundColor(Theme.getColor(str));
            }
            return new Holder(view);
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            int itemViewType = viewHolder.getItemViewType();
            boolean z = false;
            if (itemViewType == 0) {
                ManageChatUserCell manageChatUserCell = (ManageChatUserCell) viewHolder.itemView;
                if (PrivacyUsersActivity.this.blockedUsersActivity) {
                    itemViewType = PrivacyUsersActivity.this.getMessagesController().blockedUsers.keyAt(i - PrivacyUsersActivity.this.usersStartRow);
                } else {
                    itemViewType = ((Integer) PrivacyUsersActivity.this.uidArray.get(i - PrivacyUsersActivity.this.usersStartRow)).intValue();
                }
                manageChatUserCell.setTag(Integer.valueOf(itemViewType));
                CharSequence stringBuilder;
                if (itemViewType > 0) {
                    User user = PrivacyUsersActivity.this.getMessagesController().getUser(Integer.valueOf(itemViewType));
                    if (user != null) {
                        if (user.bot) {
                            StringBuilder stringBuilder2 = new StringBuilder();
                            String str = "Bot";
                            stringBuilder2.append(LocaleController.getString(str, NUM).substring(0, 1).toUpperCase());
                            stringBuilder2.append(LocaleController.getString(str, NUM).substring(1));
                            stringBuilder = stringBuilder2.toString();
                        } else {
                            String str2 = user.phone;
                            if (str2 == null || str2.length() == 0) {
                                stringBuilder = LocaleController.getString("NumberUnknown", NUM);
                            } else {
                                PhoneFormat instance = PhoneFormat.getInstance();
                                StringBuilder stringBuilder3 = new StringBuilder();
                                stringBuilder3.append("+");
                                stringBuilder3.append(user.phone);
                                stringBuilder = instance.format(stringBuilder3.toString());
                            }
                        }
                        if (i != PrivacyUsersActivity.this.usersEndRow - 1) {
                            z = true;
                        }
                        manageChatUserCell.setData(user, null, stringBuilder, z);
                        return;
                    }
                    return;
                }
                Chat chat = PrivacyUsersActivity.this.getMessagesController().getChat(Integer.valueOf(-itemViewType));
                if (chat != null) {
                    int i2 = chat.participants_count;
                    if (i2 != 0) {
                        stringBuilder = LocaleController.formatPluralString("Members", i2);
                    } else if (TextUtils.isEmpty(chat.username)) {
                        stringBuilder = LocaleController.getString("MegaPrivate", NUM);
                    } else {
                        stringBuilder = LocaleController.getString("MegaPublic", NUM);
                    }
                    if (i != PrivacyUsersActivity.this.usersEndRow - 1) {
                        z = true;
                    }
                    manageChatUserCell.setData(chat, null, stringBuilder, z);
                }
            } else if (itemViewType == 1) {
                TextInfoPrivacyCell textInfoPrivacyCell = (TextInfoPrivacyCell) viewHolder.itemView;
                String str3 = "windowBackgroundGrayShadow";
                if (i == PrivacyUsersActivity.this.blockUserDetailRow) {
                    if (PrivacyUsersActivity.this.blockedUsersActivity) {
                        textInfoPrivacyCell.setText(LocaleController.getString("BlockedUsersInfo", NUM));
                    } else {
                        textInfoPrivacyCell.setText(null);
                    }
                    if (PrivacyUsersActivity.this.usersStartRow == -1) {
                        textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, str3));
                    } else {
                        textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, str3));
                    }
                } else if (i == PrivacyUsersActivity.this.usersDetailRow) {
                    textInfoPrivacyCell.setText("");
                    textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, str3));
                }
            } else if (itemViewType == 2) {
                ManageChatTextCell manageChatTextCell = (ManageChatTextCell) viewHolder.itemView;
                manageChatTextCell.setColors("windowBackgroundWhiteBlueIcon", "windowBackgroundWhiteBlueButton");
                if (PrivacyUsersActivity.this.blockedUsersActivity) {
                    manageChatTextCell.setText(LocaleController.getString("BlockUser", NUM), null, NUM, false);
                } else {
                    manageChatTextCell.setText(LocaleController.getString("PrivacyAddAnException", NUM), null, NUM, false);
                }
            } else if (itemViewType == 3) {
                HeaderCell headerCell = (HeaderCell) viewHolder.itemView;
                if (i != PrivacyUsersActivity.this.usersHeaderRow) {
                    return;
                }
                if (PrivacyUsersActivity.this.blockedUsersActivity) {
                    headerCell.setText(LocaleController.formatPluralString("BlockedUsersCount", PrivacyUsersActivity.this.getMessagesController().blockedUsers.size()));
                    return;
                }
                headerCell.setText(LocaleController.getString("PrivacyExceptions", NUM));
            }
        }

        public int getItemViewType(int i) {
            if (i == PrivacyUsersActivity.this.usersHeaderRow) {
                return 3;
            }
            if (i == PrivacyUsersActivity.this.blockUserRow) {
                return 2;
            }
            return (i == PrivacyUsersActivity.this.blockUserDetailRow || i == PrivacyUsersActivity.this.usersDetailRow) ? 1 : 0;
        }
    }

    public PrivacyUsersActivity(ArrayList<Integer> arrayList, boolean z, boolean z2) {
        this.uidArray = arrayList;
        this.isAlwaysShare = z2;
        this.isGroup = z;
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.updateInterfaces);
        if (this.blockedUsersActivity) {
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.blockedUsersDidLoad);
            getMessagesController().getBlockedUsers(false);
        }
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.updateInterfaces);
        if (this.blockedUsersActivity) {
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.blockedUsersDidLoad);
        }
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(NUM);
        int i = 1;
        this.actionBar.setAllowOverlayTitle(true);
        if (this.blockedUsersActivity) {
            this.actionBar.setTitle(LocaleController.getString("BlockedUsers", NUM));
        } else if (this.isGroup) {
            if (this.isAlwaysShare) {
                this.actionBar.setTitle(LocaleController.getString("AlwaysAllow", NUM));
            } else {
                this.actionBar.setTitle(LocaleController.getString("NeverAllow", NUM));
            }
        } else if (this.isAlwaysShare) {
            this.actionBar.setTitle(LocaleController.getString("AlwaysShareWithTitle", NUM));
        } else {
            this.actionBar.setTitle(LocaleController.getString("NeverShareWithTitle", NUM));
        }
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    PrivacyUsersActivity.this.finishFragment();
                }
            }
        });
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        frameLayout.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
        this.emptyView = new EmptyTextProgressView(context);
        if (this.blockedUsersActivity) {
            this.emptyView.setText(LocaleController.getString("NoBlocked", NUM));
        } else {
            this.emptyView.setText(LocaleController.getString("NoContacts", NUM));
        }
        frameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView = new RecyclerListView(context);
        this.listView.setEmptyView(this.emptyView);
        this.listView.setLayoutManager(new LinearLayoutManager(context, 1, false));
        this.listView.setVerticalScrollBarEnabled(false);
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
        this.listView.setOnItemClickListener(new -$$Lambda$PrivacyUsersActivity$D3bLTU7NAbbHWcoiH45oxyckkb4(this));
        this.listView.setOnItemLongClickListener(new -$$Lambda$PrivacyUsersActivity$r_SA004H7_gb5NSQvdo4AkX5yCs(this));
        if (getMessagesController().loadingBlockedUsers) {
            this.emptyView.showProgress();
        } else {
            this.emptyView.showTextView();
        }
        updateRows();
        return this.fragmentView;
    }

    public /* synthetic */ void lambda$createView$1$PrivacyUsersActivity(View view, int i) {
        Bundle bundle;
        if (i == this.blockUserRow) {
            if (this.blockedUsersActivity) {
                bundle = new Bundle();
                bundle.putBoolean("onlyUsers", true);
                bundle.putBoolean("destroyAfterSelect", true);
                bundle.putBoolean("returnAsResult", true);
                ContactsActivity contactsActivity = new ContactsActivity(bundle);
                contactsActivity.setDelegate(this);
                presentFragment(contactsActivity);
                return;
            }
            bundle = new Bundle();
            bundle.putBoolean(this.isAlwaysShare ? "isAlwaysShare" : "isNeverShare", true);
            bundle.putBoolean("isGroup", this.isGroup);
            GroupCreateActivity groupCreateActivity = new GroupCreateActivity(bundle);
            groupCreateActivity.setDelegate(new -$$Lambda$PrivacyUsersActivity$SVOOA5DnLR2m99VL4tIuB7GhsBs(this));
            presentFragment(groupCreateActivity);
        } else if (i >= this.usersStartRow && i < this.usersEndRow) {
            String str = "user_id";
            if (this.blockedUsersActivity) {
                bundle = new Bundle();
                bundle.putInt(str, getMessagesController().blockedUsers.keyAt(i - this.usersStartRow));
                presentFragment(new ProfileActivity(bundle));
                return;
            }
            bundle = new Bundle();
            Integer num = (Integer) this.uidArray.get(i - this.usersStartRow);
            if (num.intValue() > 0) {
                bundle.putInt(str, num.intValue());
            } else {
                bundle.putInt("chat_id", -num.intValue());
            }
            presentFragment(new ProfileActivity(bundle));
        }
    }

    public /* synthetic */ void lambda$null$0$PrivacyUsersActivity(ArrayList arrayList) {
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            Integer num = (Integer) it.next();
            if (!this.uidArray.contains(num)) {
                this.uidArray.add(num);
            }
        }
        updateRows();
        PrivacyActivityDelegate privacyActivityDelegate = this.delegate;
        if (privacyActivityDelegate != null) {
            privacyActivityDelegate.didUpdateUserList(this.uidArray, true);
        }
    }

    public /* synthetic */ boolean lambda$createView$2$PrivacyUsersActivity(View view, int i) {
        int i2 = this.usersStartRow;
        if (i < i2 || i >= this.usersEndRow) {
            return false;
        }
        if (this.blockedUsersActivity) {
            showUnblockAlert(getMessagesController().blockedUsers.keyAt(i - this.usersStartRow));
        } else {
            showUnblockAlert(((Integer) this.uidArray.get(i - i2)).intValue());
        }
        return true;
    }

    public void setDelegate(PrivacyActivityDelegate privacyActivityDelegate) {
        this.delegate = privacyActivityDelegate;
    }

    private void showUnblockAlert(int i) {
        if (getParentActivity() != null) {
            Builder builder = new Builder(getParentActivity());
            builder.setItems(this.blockedUsersActivity ? new CharSequence[]{LocaleController.getString("Unblock", NUM)} : new CharSequence[]{LocaleController.getString("Delete", NUM)}, new -$$Lambda$PrivacyUsersActivity$8Pgo4HS6PGjX-d7DerbhGOGCfXc(this, i));
            showDialog(builder.create());
        }
    }

    public /* synthetic */ void lambda$showUnblockAlert$3$PrivacyUsersActivity(int i, DialogInterface dialogInterface, int i2) {
        if (i2 != 0) {
            return;
        }
        if (this.blockedUsersActivity) {
            getMessagesController().unblockUser(i);
            return;
        }
        this.uidArray.remove(Integer.valueOf(i));
        updateRows();
        PrivacyActivityDelegate privacyActivityDelegate = this.delegate;
        if (privacyActivityDelegate != null) {
            privacyActivityDelegate.didUpdateUserList(this.uidArray, false);
        }
        if (this.uidArray.isEmpty()) {
            finishFragment();
        }
    }

    private void updateRows() {
        this.rowCount = 0;
        if (!(this.blockedUsersActivity && getMessagesController().loadingBlockedUsers)) {
            int i = this.rowCount;
            this.rowCount = i + 1;
            this.blockUserRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.blockUserDetailRow = i;
            if (this.blockedUsersActivity) {
                i = getMessagesController().blockedUsers.size();
            } else {
                i = this.uidArray.size();
            }
            if (i != 0) {
                int i2 = this.rowCount;
                this.rowCount = i2 + 1;
                this.usersHeaderRow = i2;
                i2 = this.rowCount;
                this.usersStartRow = i2;
                this.rowCount = i2 + i;
                i = this.rowCount;
                this.usersEndRow = i;
                this.rowCount = i + 1;
                this.usersDetailRow = i;
            } else {
                this.usersHeaderRow = -1;
                this.usersStartRow = -1;
                this.usersEndRow = -1;
                this.usersDetailRow = -1;
            }
        }
        ListAdapter listAdapter = this.listViewAdapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.updateInterfaces) {
            i = ((Integer) objArr[0]).intValue();
            if ((i & 2) != 0 || (i & 1) != 0) {
                updateVisibleRows(i);
            }
        } else if (i == NotificationCenter.blockedUsersDidLoad) {
            this.emptyView.showTextView();
            updateRows();
        }
    }

    private void updateVisibleRows(int i) {
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            int childCount = recyclerListView.getChildCount();
            for (int i2 = 0; i2 < childCount; i2++) {
                View childAt = this.listView.getChildAt(i2);
                if (childAt instanceof ManageChatUserCell) {
                    ((ManageChatUserCell) childAt).update(i);
                }
            }
        }
    }

    public void onResume() {
        super.onResume();
        ListAdapter listAdapter = this.listViewAdapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    public void didSelectContact(User user, String str, ContactsActivity contactsActivity) {
        if (user != null) {
            getMessagesController().blockUser(user.id);
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        -$$Lambda$PrivacyUsersActivity$MXkjS07yNtFLRhkACbitVef2EDw -__lambda_privacyusersactivity_mxkjs07yntflrhkacbitvef2edw = new -$$Lambda$PrivacyUsersActivity$MXkjS07yNtFLRhkACbitVef2EDw(this);
        ThemeDescription[] themeDescriptionArr = new ThemeDescription[27];
        themeDescriptionArr[0] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundGray");
        themeDescriptionArr[1] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{ManageChatUserCell.class, ManageChatTextCell.class, HeaderCell.class}, null, null, null, "windowBackgroundWhite");
        themeDescriptionArr[2] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault");
        themeDescriptionArr[3] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault");
        themeDescriptionArr[4] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon");
        themeDescriptionArr[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle");
        themeDescriptionArr[6] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector");
        themeDescriptionArr[7] = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21");
        themeDescriptionArr[8] = new ThemeDescription(this.emptyView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "emptyListPlaceholder");
        themeDescriptionArr[9] = new ThemeDescription(this.emptyView, ThemeDescription.FLAG_PROGRESSBAR, null, null, null, null, "progressCircle");
        View view = this.listView;
        View view2 = view;
        themeDescriptionArr[10] = new ThemeDescription(view2, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, "windowBackgroundGrayShadow");
        themeDescriptionArr[11] = new ThemeDescription(this.listView, 0, new Class[]{ManageChatUserCell.class}, new String[]{"nameTextView"}, null, null, null, "windowBackgroundWhiteBlackText");
        ThemeDescriptionDelegate themeDescriptionDelegate = -__lambda_privacyusersactivity_mxkjs07yntflrhkacbitvef2edw;
        themeDescriptionArr[12] = new ThemeDescription(this.listView, 0, new Class[]{ManageChatUserCell.class}, new String[]{"statusColor"}, null, null, themeDescriptionDelegate, "windowBackgroundWhiteGrayText");
        themeDescriptionArr[13] = new ThemeDescription(this.listView, 0, new Class[]{ManageChatUserCell.class}, new String[]{"statusOnlineColor"}, null, null, themeDescriptionDelegate, "windowBackgroundWhiteBlueText");
        themeDescriptionArr[14] = new ThemeDescription(this.listView, 0, new Class[]{ManageChatUserCell.class}, null, new Drawable[]{Theme.avatar_broadcastDrawable, Theme.avatar_savedDrawable}, null, "avatar_text");
        -$$Lambda$PrivacyUsersActivity$MXkjS07yNtFLRhkACbitVef2EDw -__lambda_privacyusersactivity_mxkjs07yntflrhkacbitvef2edw2 = -__lambda_privacyusersactivity_mxkjs07yntflrhkacbitvef2edw;
        themeDescriptionArr[15] = new ThemeDescription(null, 0, null, null, null, -__lambda_privacyusersactivity_mxkjs07yntflrhkacbitvef2edw2, "avatar_backgroundRed");
        themeDescriptionArr[16] = new ThemeDescription(null, 0, null, null, null, -__lambda_privacyusersactivity_mxkjs07yntflrhkacbitvef2edw2, "avatar_backgroundOrange");
        themeDescriptionArr[17] = new ThemeDescription(null, 0, null, null, null, -__lambda_privacyusersactivity_mxkjs07yntflrhkacbitvef2edw2, "avatar_backgroundViolet");
        themeDescriptionArr[18] = new ThemeDescription(null, 0, null, null, null, -__lambda_privacyusersactivity_mxkjs07yntflrhkacbitvef2edw2, "avatar_backgroundGreen");
        themeDescriptionArr[19] = new ThemeDescription(null, 0, null, null, null, -__lambda_privacyusersactivity_mxkjs07yntflrhkacbitvef2edw2, "avatar_backgroundCyan");
        themeDescriptionArr[20] = new ThemeDescription(null, 0, null, null, null, -__lambda_privacyusersactivity_mxkjs07yntflrhkacbitvef2edw2, "avatar_backgroundBlue");
        themeDescriptionArr[21] = new ThemeDescription(null, 0, null, null, null, -__lambda_privacyusersactivity_mxkjs07yntflrhkacbitvef2edw2, "avatar_backgroundPink");
        view = this.listView;
        Class[] clsArr = new Class[]{HeaderCell.class};
        String[] strArr = new String[1];
        strArr[0] = "textView";
        themeDescriptionArr[22] = new ThemeDescription(view, 0, clsArr, strArr, null, null, null, "windowBackgroundWhiteBlueHeader");
        view = this.listView;
        View view3 = view;
        themeDescriptionArr[23] = new ThemeDescription(view3, ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText");
        view = this.listView;
        int i = ThemeDescription.FLAG_CHECKTAG;
        clsArr = new Class[]{ManageChatTextCell.class};
        strArr = new String[1];
        strArr[0] = "imageView";
        themeDescriptionArr[24] = new ThemeDescription(view, i, clsArr, strArr, null, null, null, "windowBackgroundWhiteGrayIcon");
        view = this.listView;
        view3 = view;
        themeDescriptionArr[25] = new ThemeDescription(view3, ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"imageView"}, null, null, null, "windowBackgroundWhiteBlueButton");
        view = this.listView;
        view3 = view;
        themeDescriptionArr[26] = new ThemeDescription(view3, ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlueIcon");
        return themeDescriptionArr;
    }

    public /* synthetic */ void lambda$getThemeDescriptions$4$PrivacyUsersActivity() {
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
