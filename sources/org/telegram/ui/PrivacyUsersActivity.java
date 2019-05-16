package org.telegram.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import java.util.ArrayList;
import java.util.Iterator;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
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
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;

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

    public interface PrivacyActivityDelegate {
        void didUpdatedUserList(ArrayList<Integer> arrayList, boolean z);
    }

    private class ListAdapter extends SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean isEnabled(ViewHolder viewHolder) {
            return viewHolder.getAdapterPosition() != PrivacyUsersActivity.this.uidArray.size();
        }

        public int getItemCount() {
            if (PrivacyUsersActivity.this.uidArray.isEmpty()) {
                return 0;
            }
            return PrivacyUsersActivity.this.uidArray.size() + 1;
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View textInfoCell;
            if (i != 0) {
                textInfoCell = new TextInfoCell(this.mContext);
                textInfoCell.setText(LocaleController.getString("RemoveFromListText", NUM));
            } else {
                textInfoCell = new UserCell(this.mContext, 1, 0, false);
            }
            return new Holder(textInfoCell);
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            if (viewHolder.getItemViewType() == 0) {
                CharSequence string;
                User user = MessagesController.getInstance(PrivacyUsersActivity.this.currentAccount).getUser((Integer) PrivacyUsersActivity.this.uidArray.get(i));
                UserCell userCell = (UserCell) viewHolder.itemView;
                String str = user.phone;
                if (str == null || str.length() == 0) {
                    string = LocaleController.getString("NumberUnknown", NUM);
                } else {
                    PhoneFormat instance = PhoneFormat.getInstance();
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("+");
                    stringBuilder.append(user.phone);
                    string = instance.format(stringBuilder.toString());
                }
                userCell.setData(user, null, string, 0);
            }
        }

        public int getItemViewType(int i) {
            return i == PrivacyUsersActivity.this.uidArray.size() ? 1 : 0;
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
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.updateInterfaces);
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(NUM);
        int i = 1;
        this.actionBar.setAllowOverlayTitle(true);
        if (this.isGroup) {
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
                } else if (i == 1) {
                    Bundle bundle = new Bundle();
                    bundle.putBoolean(PrivacyUsersActivity.this.isAlwaysShare ? "isAlwaysShare" : "isNeverShare", true);
                    bundle.putBoolean("isGroup", PrivacyUsersActivity.this.isGroup);
                    GroupCreateActivity groupCreateActivity = new GroupCreateActivity(bundle);
                    groupCreateActivity.setDelegate(new -$$Lambda$PrivacyUsersActivity$1$szUnG8jdJxzpgLH-a2za5o_9XU8(this));
                    PrivacyUsersActivity.this.presentFragment(groupCreateActivity);
                }
            }

            public /* synthetic */ void lambda$onItemClick$0$PrivacyUsersActivity$1(ArrayList arrayList) {
                Iterator it = arrayList.iterator();
                while (it.hasNext()) {
                    Integer num = (Integer) it.next();
                    if (!PrivacyUsersActivity.this.uidArray.contains(num)) {
                        PrivacyUsersActivity.this.uidArray.add(num);
                    }
                }
                PrivacyUsersActivity.this.listViewAdapter.notifyDataSetChanged();
                if (PrivacyUsersActivity.this.delegate != null) {
                    PrivacyUsersActivity.this.delegate.didUpdatedUserList(PrivacyUsersActivity.this.uidArray, true);
                }
            }
        });
        this.actionBar.createMenu().addItem(1, NUM);
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        this.emptyView = new EmptyTextProgressView(context);
        this.emptyView.showTextView();
        this.emptyView.setText(LocaleController.getString("NoContacts", NUM));
        frameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView = new RecyclerListView(context);
        this.listView.setEmptyView(this.emptyView);
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setLayoutManager(new LinearLayoutManager(context, 1, false));
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
        this.listView.setOnItemClickListener(new -$$Lambda$PrivacyUsersActivity$TfBeJKw94IYjQGIi82R1rn26EIk(this));
        this.listView.setOnItemLongClickListener(new -$$Lambda$PrivacyUsersActivity$r_SA004H7_gb5NSQvdo4AkX5yCs(this));
        return this.fragmentView;
    }

    public /* synthetic */ void lambda$createView$0$PrivacyUsersActivity(View view, int i) {
        if (i < this.uidArray.size()) {
            Bundle bundle = new Bundle();
            bundle.putInt("user_id", ((Integer) this.uidArray.get(i)).intValue());
            presentFragment(new ProfileActivity(bundle));
        }
    }

    public /* synthetic */ boolean lambda$createView$2$PrivacyUsersActivity(View view, int i) {
        if (i < 0 || i >= this.uidArray.size() || getParentActivity() == null) {
            return false;
        }
        this.selectedUserId = ((Integer) this.uidArray.get(i)).intValue();
        Builder builder = new Builder(getParentActivity());
        builder.setItems(new CharSequence[]{LocaleController.getString("Delete", NUM)}, new -$$Lambda$PrivacyUsersActivity$npaFGCU0vEPaOg9Ky5tzGbF2McY(this));
        showDialog(builder.create());
        return true;
    }

    public /* synthetic */ void lambda$null$1$PrivacyUsersActivity(DialogInterface dialogInterface, int i) {
        if (i == 0) {
            this.uidArray.remove(Integer.valueOf(this.selectedUserId));
            this.listViewAdapter.notifyDataSetChanged();
            PrivacyActivityDelegate privacyActivityDelegate = this.delegate;
            if (privacyActivityDelegate != null) {
                privacyActivityDelegate.didUpdatedUserList(this.uidArray, false);
            }
        }
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.updateInterfaces) {
            i = ((Integer) objArr[0]).intValue();
            if ((i & 2) != 0 || (i & 1) != 0) {
                updateVisibleRows(i);
            }
        }
    }

    private void updateVisibleRows(int i) {
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            int childCount = recyclerListView.getChildCount();
            for (int i2 = 0; i2 < childCount; i2++) {
                View childAt = this.listView.getChildAt(i2);
                if (childAt instanceof UserCell) {
                    ((UserCell) childAt).update(i);
                }
            }
        }
    }

    public void setDelegate(PrivacyActivityDelegate privacyActivityDelegate) {
        this.delegate = privacyActivityDelegate;
    }

    public void onResume() {
        super.onResume();
        ListAdapter listAdapter = this.listViewAdapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        -$$Lambda$PrivacyUsersActivity$uYajhemS_7G_pwutrZXos-oxznE -__lambda_privacyusersactivity_uyajhems_7g_pwutrzxos-oxzne = new -$$Lambda$PrivacyUsersActivity$uYajhemS_7G_pwutrZXos-oxznE(this);
        r11 = new ThemeDescription[19];
        r11[4] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle");
        r11[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector");
        r11[6] = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21");
        r11[7] = new ThemeDescription(this.emptyView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "emptyListPlaceholder");
        r11[8] = new ThemeDescription(this.listView, 0, new Class[]{TextInfoCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteGrayText5");
        r11[9] = new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, new String[]{"nameTextView"}, null, null, null, "windowBackgroundWhiteBlackText");
        r11[10] = new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, new String[]{"statusColor"}, null, null, (ThemeDescriptionDelegate) -__lambda_privacyusersactivity_uyajhems_7g_pwutrzxos-oxzne, "windowBackgroundWhiteGrayText");
        r11[11] = new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, null, new Drawable[]{Theme.avatar_broadcastDrawable, Theme.avatar_savedDrawable}, null, "avatar_text");
        -$$Lambda$PrivacyUsersActivity$uYajhemS_7G_pwutrZXos-oxznE -__lambda_privacyusersactivity_uyajhems_7g_pwutrzxos-oxzne2 = -__lambda_privacyusersactivity_uyajhems_7g_pwutrzxos-oxzne;
        r11[12] = new ThemeDescription(null, 0, null, null, null, -__lambda_privacyusersactivity_uyajhems_7g_pwutrzxos-oxzne2, "avatar_backgroundRed");
        r11[13] = new ThemeDescription(null, 0, null, null, null, -__lambda_privacyusersactivity_uyajhems_7g_pwutrzxos-oxzne2, "avatar_backgroundOrange");
        r11[14] = new ThemeDescription(null, 0, null, null, null, -__lambda_privacyusersactivity_uyajhems_7g_pwutrzxos-oxzne2, "avatar_backgroundViolet");
        r11[15] = new ThemeDescription(null, 0, null, null, null, -__lambda_privacyusersactivity_uyajhems_7g_pwutrzxos-oxzne2, "avatar_backgroundGreen");
        r11[16] = new ThemeDescription(null, 0, null, null, null, -__lambda_privacyusersactivity_uyajhems_7g_pwutrzxos-oxzne2, "avatar_backgroundCyan");
        r11[17] = new ThemeDescription(null, 0, null, null, null, -__lambda_privacyusersactivity_uyajhems_7g_pwutrzxos-oxzne2, "avatar_backgroundBlue");
        r11[18] = new ThemeDescription(null, 0, null, null, null, -__lambda_privacyusersactivity_uyajhems_7g_pwutrzxos-oxzne2, "avatar_backgroundPink");
        return r11;
    }

    public /* synthetic */ void lambda$getThemeDescriptions$3$PrivacyUsersActivity() {
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
