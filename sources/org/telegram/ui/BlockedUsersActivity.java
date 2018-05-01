package org.telegram.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.C0446R;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.Adapter;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate;
import org.telegram.ui.Cells.TextInfoCell;
import org.telegram.ui.Cells.UserCell;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;
import org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;
import org.telegram.ui.ContactsActivity.ContactsActivityDelegate;

public class BlockedUsersActivity extends BaseFragment implements NotificationCenterDelegate, ContactsActivityDelegate {
    private static final int block_user = 1;
    private EmptyTextProgressView emptyView;
    private RecyclerListView listView;
    private ListAdapter listViewAdapter;
    private int selectedUserId;

    /* renamed from: org.telegram.ui.BlockedUsersActivity$1 */
    class C19241 extends ActionBarMenuOnItemClick {
        C19241() {
        }

        public void onItemClick(int i) {
            if (i == -1) {
                BlockedUsersActivity.this.finishFragment();
            } else if (i == 1) {
                i = new Bundle();
                i.putBoolean("onlyUsers", true);
                i.putBoolean("destroyAfterSelect", true);
                i.putBoolean("returnAsResult", true);
                BaseFragment contactsActivity = new ContactsActivity(i);
                contactsActivity.setDelegate(BlockedUsersActivity.this);
                BlockedUsersActivity.this.presentFragment(contactsActivity);
            }
        }
    }

    /* renamed from: org.telegram.ui.BlockedUsersActivity$2 */
    class C19252 implements OnItemClickListener {
        C19252() {
        }

        public void onItemClick(View view, int i) {
            if (i < MessagesController.getInstance(BlockedUsersActivity.this.currentAccount).blockedUsers.size()) {
                view = new Bundle();
                view.putInt("user_id", ((Integer) MessagesController.getInstance(BlockedUsersActivity.this.currentAccount).blockedUsers.get(i)).intValue());
                BlockedUsersActivity.this.presentFragment(new ProfileActivity(view));
            }
        }
    }

    /* renamed from: org.telegram.ui.BlockedUsersActivity$3 */
    class C19263 implements OnItemLongClickListener {

        /* renamed from: org.telegram.ui.BlockedUsersActivity$3$1 */
        class C08381 implements OnClickListener {
            C08381() {
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    MessagesController.getInstance(BlockedUsersActivity.this.currentAccount).unblockUser(BlockedUsersActivity.this.selectedUserId);
                }
            }
        }

        C19263() {
        }

        public boolean onItemClick(View view, int i) {
            if (i < MessagesController.getInstance(BlockedUsersActivity.this.currentAccount).blockedUsers.size()) {
                if (BlockedUsersActivity.this.getParentActivity() != null) {
                    BlockedUsersActivity.this.selectedUserId = ((Integer) MessagesController.getInstance(BlockedUsersActivity.this.currentAccount).blockedUsers.get(i)).intValue();
                    view = new Builder(BlockedUsersActivity.this.getParentActivity());
                    view.setItems(new CharSequence[]{LocaleController.getString("Unblock", C0446R.string.Unblock)}, new C08381());
                    BlockedUsersActivity.this.showDialog(view.create());
                    return true;
                }
            }
            return true;
        }
    }

    /* renamed from: org.telegram.ui.BlockedUsersActivity$4 */
    class C19274 implements ThemeDescriptionDelegate {
        C19274() {
        }

        public void didSetColor() {
            if (BlockedUsersActivity.this.listView != null) {
                int childCount = BlockedUsersActivity.this.listView.getChildCount();
                for (int i = 0; i < childCount; i++) {
                    View childAt = BlockedUsersActivity.this.listView.getChildAt(i);
                    if (childAt instanceof UserCell) {
                        ((UserCell) childAt).update(0);
                    }
                }
            }
        }
    }

    private class ListAdapter extends SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public int getItemCount() {
            if (MessagesController.getInstance(BlockedUsersActivity.this.currentAccount).blockedUsers.isEmpty()) {
                return 0;
            }
            return MessagesController.getInstance(BlockedUsersActivity.this.currentAccount).blockedUsers.size() + 1;
        }

        public boolean isEnabled(ViewHolder viewHolder) {
            return viewHolder.getItemViewType() == null ? true : null;
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            if (i != 0) {
                viewGroup = new TextInfoCell(this.mContext);
                ((TextInfoCell) viewGroup).setText(LocaleController.getString("UnblockText", C0446R.string.UnblockText));
            } else {
                viewGroup = new UserCell(this.mContext, 1, 0, false);
            }
            return new Holder(viewGroup);
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            if (viewHolder.getItemViewType() == 0) {
                i = MessagesController.getInstance(BlockedUsersActivity.this.currentAccount).getUser((Integer) MessagesController.getInstance(BlockedUsersActivity.this.currentAccount).blockedUsers.get(i));
                if (i != 0) {
                    CharSequence stringBuilder;
                    if (i.bot) {
                        StringBuilder stringBuilder2 = new StringBuilder();
                        stringBuilder2.append(LocaleController.getString("Bot", C0446R.string.Bot).substring(0, 1).toUpperCase());
                        stringBuilder2.append(LocaleController.getString("Bot", C0446R.string.Bot).substring(1));
                        stringBuilder = stringBuilder2.toString();
                    } else if (i.phone == null || i.phone.length() == 0) {
                        stringBuilder = LocaleController.getString("NumberUnknown", C0446R.string.NumberUnknown);
                    } else {
                        PhoneFormat instance = PhoneFormat.getInstance();
                        StringBuilder stringBuilder3 = new StringBuilder();
                        stringBuilder3.append("+");
                        stringBuilder3.append(i.phone);
                        stringBuilder = instance.format(stringBuilder3.toString());
                    }
                    ((UserCell) viewHolder.itemView).setData(i, null, stringBuilder, 0);
                }
            }
        }

        public int getItemViewType(int i) {
            return i == MessagesController.getInstance(BlockedUsersActivity.this.currentAccount).blockedUsers.size() ? 1 : 0;
        }
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.updateInterfaces);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.blockedUsersDidLoaded);
        MessagesController.getInstance(this.currentAccount).getBlockedUsers(false);
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.updateInterfaces);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.blockedUsersDidLoaded);
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(C0446R.drawable.ic_ab_back);
        int i = 1;
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("BlockedUsers", C0446R.string.BlockedUsers));
        this.actionBar.setActionBarMenuOnItemClick(new C19241());
        this.actionBar.createMenu().addItem(1, (int) C0446R.drawable.plus);
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        this.emptyView = new EmptyTextProgressView(context);
        this.emptyView.setText(LocaleController.getString("NoBlocked", C0446R.string.NoBlocked));
        frameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView = new RecyclerListView(context);
        this.listView.setEmptyView(this.emptyView);
        this.listView.setLayoutManager(new LinearLayoutManager(context, 1, false));
        this.listView.setVerticalScrollBarEnabled(false);
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
        this.listView.setOnItemClickListener(new C19252());
        this.listView.setOnItemLongClickListener(new C19263());
        if (MessagesController.getInstance(this.currentAccount).loadingBlockedUsers != null) {
            this.emptyView.showProgress();
        } else {
            this.emptyView.showTextView();
        }
        return this.fragmentView;
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.updateInterfaces) {
            i = ((Integer) objArr[0]).intValue();
            if ((i & 2) != 0 || (i & 1) != 0) {
                updateVisibleRows(i);
            }
        } else if (i == NotificationCenter.blockedUsersDidLoaded) {
            this.emptyView.showTextView();
            if (this.listViewAdapter != 0) {
                this.listViewAdapter.notifyDataSetChanged();
            }
        }
    }

    private void updateVisibleRows(int i) {
        if (this.listView != null) {
            int childCount = this.listView.getChildCount();
            for (int i2 = 0; i2 < childCount; i2++) {
                View childAt = this.listView.getChildAt(i2);
                if (childAt instanceof UserCell) {
                    ((UserCell) childAt).update(i);
                }
            }
        }
    }

    public void onResume() {
        super.onResume();
        if (this.listViewAdapter != null) {
            this.listViewAdapter.notifyDataSetChanged();
        }
    }

    public void didSelectContact(User user, String str, ContactsActivity contactsActivity) {
        if (user != null) {
            MessagesController.getInstance(this.currentAccount).blockUser(user.id);
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        C19274 c19274 = new C19274();
        r11 = new ThemeDescription[20];
        r11[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector);
        r11[6] = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector);
        r11[7] = new ThemeDescription(this.emptyView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_emptyListPlaceholder);
        r11[8] = new ThemeDescription(this.emptyView, ThemeDescription.FLAG_PROGRESSBAR, null, null, null, null, Theme.key_progressCircle);
        r11[9] = new ThemeDescription(this.listView, 0, new Class[]{TextInfoCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText5);
        r11[10] = new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, new String[]{"nameTextView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r11[11] = new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, new String[]{"statusColor"}, null, null, (ThemeDescriptionDelegate) c19274, Theme.key_windowBackgroundWhiteGrayText);
        r11[12] = new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, null, new Drawable[]{Theme.avatar_photoDrawable, Theme.avatar_broadcastDrawable, Theme.avatar_savedDrawable}, null, Theme.key_avatar_text);
        C19274 c192742 = c19274;
        r11[13] = new ThemeDescription(null, 0, null, null, null, c192742, Theme.key_avatar_backgroundRed);
        r11[14] = new ThemeDescription(null, 0, null, null, null, c192742, Theme.key_avatar_backgroundOrange);
        r11[15] = new ThemeDescription(null, 0, null, null, null, c192742, Theme.key_avatar_backgroundViolet);
        r11[16] = new ThemeDescription(null, 0, null, null, null, c192742, Theme.key_avatar_backgroundGreen);
        r11[17] = new ThemeDescription(null, 0, null, null, null, c192742, Theme.key_avatar_backgroundCyan);
        r11[18] = new ThemeDescription(null, 0, null, null, null, c192742, Theme.key_avatar_backgroundBlue);
        r11[19] = new ThemeDescription(null, 0, null, null, null, c192742, Theme.key_avatar_backgroundPink);
        return r11;
    }
}
