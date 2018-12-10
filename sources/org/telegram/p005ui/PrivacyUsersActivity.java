package org.telegram.p005ui;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import java.util.ArrayList;
import java.util.Iterator;
import org.telegram.PhoneFormat.CLASSNAMEPhoneFormat;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.Adapter;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.p005ui.ActionBar.AlertDialog.Builder;
import org.telegram.p005ui.ActionBar.BaseFragment;
import org.telegram.p005ui.ActionBar.CLASSNAMEActionBar.ActionBarMenuOnItemClick;
import org.telegram.p005ui.ActionBar.Theme;
import org.telegram.p005ui.ActionBar.ThemeDescription;
import org.telegram.p005ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate;
import org.telegram.p005ui.Cells.TextInfoCell;
import org.telegram.p005ui.Cells.UserCell;
import org.telegram.p005ui.Components.EmptyTextProgressView;
import org.telegram.p005ui.Components.LayoutHelper;
import org.telegram.p005ui.Components.RecyclerListView;
import org.telegram.p005ui.Components.RecyclerListView.Holder;
import org.telegram.p005ui.Components.RecyclerListView.SelectionAdapter;
import org.telegram.tgnet.TLRPC.User;

/* renamed from: org.telegram.ui.PrivacyUsersActivity */
public class PrivacyUsersActivity extends BaseFragment implements NotificationCenterDelegate {
    private static final int block_user = 1;
    private PrivacyActivityDelegate delegate;
    private EmptyTextProgressView emptyView;
    private boolean isAlwaysShare;
    private boolean isGroup;
    private RecyclerListView listView;
    private ListAdapter listViewAdapter;
    private int selectedUserId;
    private ArrayList<Integer> uidArray;

    /* renamed from: org.telegram.ui.PrivacyUsersActivity$PrivacyActivityDelegate */
    public interface PrivacyActivityDelegate {
        void didUpdatedUserList(ArrayList<Integer> arrayList, boolean z);
    }

    /* renamed from: org.telegram.ui.PrivacyUsersActivity$1 */
    class CLASSNAME extends ActionBarMenuOnItemClick {
        CLASSNAME() {
        }

        public void onItemClick(int id) {
            if (id == -1) {
                PrivacyUsersActivity.this.lambda$checkDiscard$70$PassportActivity();
            } else if (id == 1) {
                Bundle args = new Bundle();
                args.putBoolean(PrivacyUsersActivity.this.isAlwaysShare ? "isAlwaysShare" : "isNeverShare", true);
                args.putBoolean("isGroup", PrivacyUsersActivity.this.isGroup);
                GroupCreateActivity fragment = new GroupCreateActivity(args);
                fragment.setDelegate(new PrivacyUsersActivity$1$$Lambda$0(this));
                PrivacyUsersActivity.this.presentFragment(fragment);
            }
        }

        final /* synthetic */ void lambda$onItemClick$0$PrivacyUsersActivity$1(ArrayList ids) {
            Iterator it = ids.iterator();
            while (it.hasNext()) {
                Integer id1 = (Integer) it.next();
                if (!PrivacyUsersActivity.this.uidArray.contains(id1)) {
                    PrivacyUsersActivity.this.uidArray.add(id1);
                }
            }
            PrivacyUsersActivity.this.listViewAdapter.notifyDataSetChanged();
            if (PrivacyUsersActivity.this.delegate != null) {
                PrivacyUsersActivity.this.delegate.didUpdatedUserList(PrivacyUsersActivity.this.uidArray, true);
            }
        }
    }

    /* renamed from: org.telegram.ui.PrivacyUsersActivity$ListAdapter */
    private class ListAdapter extends SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean isEnabled(ViewHolder holder) {
            return holder.getAdapterPosition() != PrivacyUsersActivity.this.uidArray.size();
        }

        public int getItemCount() {
            if (PrivacyUsersActivity.this.uidArray.isEmpty()) {
                return 0;
            }
            return PrivacyUsersActivity.this.uidArray.size() + 1;
        }

        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    view = new UserCell(this.mContext, 1, 0, false);
                    break;
                default:
                    view = new TextInfoCell(this.mContext);
                    ((TextInfoCell) view).setText(LocaleController.getString("RemoveFromListText", R.string.RemoveFromListText));
                    break;
            }
            return new Holder(view);
        }

        public void onBindViewHolder(ViewHolder holder, int position) {
            if (holder.getItemViewType() == 0) {
                User user = MessagesController.getInstance(PrivacyUsersActivity.this.currentAccount).getUser((Integer) PrivacyUsersActivity.this.uidArray.get(position));
                UserCell userCell = (UserCell) holder.itemView;
                CharSequence string = (user.phone == null || user.phone.length() == 0) ? LocaleController.getString("NumberUnknown", R.string.NumberUnknown) : CLASSNAMEPhoneFormat.getInstance().format("+" + user.phone);
                userCell.setData(user, null, string, 0);
            }
        }

        public int getItemViewType(int i) {
            if (i == PrivacyUsersActivity.this.uidArray.size()) {
                return 1;
            }
            return 0;
        }
    }

    public PrivacyUsersActivity(ArrayList<Integer> users, boolean group, boolean always) {
        this.uidArray = users;
        this.isAlwaysShare = always;
        this.isGroup = group;
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.updateInterfaces);
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.updateInterfaces);
    }

    public View createView(Context context) {
        int i = 1;
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        if (this.isGroup) {
            if (this.isAlwaysShare) {
                this.actionBar.setTitle(LocaleController.getString("AlwaysAllow", R.string.AlwaysAllow));
            } else {
                this.actionBar.setTitle(LocaleController.getString("NeverAllow", R.string.NeverAllow));
            }
        } else if (this.isAlwaysShare) {
            this.actionBar.setTitle(LocaleController.getString("AlwaysShareWithTitle", R.string.AlwaysShareWithTitle));
        } else {
            this.actionBar.setTitle(LocaleController.getString("NeverShareWithTitle", R.string.NeverShareWithTitle));
        }
        this.actionBar.setActionBarMenuOnItemClick(new CLASSNAME());
        this.actionBar.createMenu().addItem(1, (int) R.drawable.plus);
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = this.fragmentView;
        this.emptyView = new EmptyTextProgressView(context);
        this.emptyView.showTextView();
        this.emptyView.setText(LocaleController.getString("NoContacts", R.string.NoContacts));
        frameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView = new RecyclerListView(context);
        this.listView.setEmptyView(this.emptyView);
        this.listView.setVerticalScrollBarEnabled(false);
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
        this.listView.setOnItemClickListener(new PrivacyUsersActivity$$Lambda$0(this));
        this.listView.setOnItemLongClickListener(new PrivacyUsersActivity$$Lambda$1(this));
        return this.fragmentView;
    }

    final /* synthetic */ void lambda$createView$0$PrivacyUsersActivity(View view, int position) {
        if (position < this.uidArray.size()) {
            Bundle args = new Bundle();
            args.putInt("user_id", ((Integer) this.uidArray.get(position)).intValue());
            presentFragment(new ProfileActivity(args));
        }
    }

    final /* synthetic */ boolean lambda$createView$2$PrivacyUsersActivity(View view, int position) {
        if (position < 0 || position >= this.uidArray.size() || getParentActivity() == null) {
            return false;
        }
        this.selectedUserId = ((Integer) this.uidArray.get(position)).intValue();
        Builder builder = new Builder(getParentActivity());
        builder.setItems(new CharSequence[]{LocaleController.getString("Delete", R.string.Delete)}, new PrivacyUsersActivity$$Lambda$3(this));
        showDialog(builder.create());
        return true;
    }

    final /* synthetic */ void lambda$null$1$PrivacyUsersActivity(DialogInterface dialogInterface, int i) {
        if (i == 0) {
            this.uidArray.remove(Integer.valueOf(this.selectedUserId));
            this.listViewAdapter.notifyDataSetChanged();
            if (this.delegate != null) {
                this.delegate.didUpdatedUserList(this.uidArray, false);
            }
        }
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.updateInterfaces) {
            int mask = ((Integer) args[0]).intValue();
            if ((mask & 2) != 0 || (mask & 1) != 0) {
                updateVisibleRows(mask);
            }
        }
    }

    private void updateVisibleRows(int mask) {
        if (this.listView != null) {
            int count = this.listView.getChildCount();
            for (int a = 0; a < count; a++) {
                View child = this.listView.getChildAt(a);
                if (child instanceof UserCell) {
                    ((UserCell) child).update(mask);
                }
            }
        }
    }

    public void setDelegate(PrivacyActivityDelegate privacyActivityDelegate) {
        this.delegate = privacyActivityDelegate;
    }

    public void onResume() {
        super.onResume();
        if (this.listViewAdapter != null) {
            this.listViewAdapter.notifyDataSetChanged();
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        ThemeDescriptionDelegate cellDelegate = new PrivacyUsersActivity$$Lambda$2(this);
        r10 = new ThemeDescription[19];
        r10[8] = new ThemeDescription(this.listView, 0, new Class[]{TextInfoCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText5);
        r10[9] = new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, new String[]{"nameTextView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r10[10] = new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, new String[]{"statusColor"}, null, null, cellDelegate, Theme.key_windowBackgroundWhiteGrayText);
        r10[11] = new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, null, new Drawable[]{Theme.avatar_broadcastDrawable, Theme.avatar_savedDrawable}, null, Theme.key_avatar_text);
        r10[12] = new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundRed);
        r10[13] = new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundOrange);
        r10[14] = new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundViolet);
        r10[15] = new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundGreen);
        r10[16] = new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundCyan);
        r10[17] = new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundBlue);
        r10[18] = new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundPink);
        return r10;
    }

    final /* synthetic */ void lambda$getThemeDescriptions$3$PrivacyUsersActivity() {
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
