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
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.C0446R;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.Adapter;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
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
import org.telegram.ui.GroupCreateActivity.GroupCreateActivityDelegate;

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

    /* renamed from: org.telegram.ui.PrivacyUsersActivity$1 */
    class C22511 extends ActionBarMenuOnItemClick {

        /* renamed from: org.telegram.ui.PrivacyUsersActivity$1$1 */
        class C22501 implements GroupCreateActivityDelegate {
            C22501() {
            }

            public void didSelectUsers(ArrayList<Integer> arrayList) {
                arrayList = arrayList.iterator();
                while (arrayList.hasNext()) {
                    Integer num = (Integer) arrayList.next();
                    if (!PrivacyUsersActivity.this.uidArray.contains(num)) {
                        PrivacyUsersActivity.this.uidArray.add(num);
                    }
                }
                PrivacyUsersActivity.this.listViewAdapter.notifyDataSetChanged();
                if (PrivacyUsersActivity.this.delegate != null) {
                    PrivacyUsersActivity.this.delegate.didUpdatedUserList(PrivacyUsersActivity.this.uidArray, true);
                }
            }
        }

        C22511() {
        }

        public void onItemClick(int i) {
            if (i == -1) {
                PrivacyUsersActivity.this.finishFragment();
            } else if (i == 1) {
                i = new Bundle();
                i.putBoolean(PrivacyUsersActivity.this.isAlwaysShare ? "isAlwaysShare" : "isNeverShare", true);
                i.putBoolean("isGroup", PrivacyUsersActivity.this.isGroup);
                BaseFragment groupCreateActivity = new GroupCreateActivity(i);
                groupCreateActivity.setDelegate(new C22501());
                PrivacyUsersActivity.this.presentFragment(groupCreateActivity);
            }
        }
    }

    /* renamed from: org.telegram.ui.PrivacyUsersActivity$2 */
    class C22522 implements OnItemClickListener {
        C22522() {
        }

        public void onItemClick(View view, int i) {
            if (i < PrivacyUsersActivity.this.uidArray.size()) {
                view = new Bundle();
                view.putInt("user_id", ((Integer) PrivacyUsersActivity.this.uidArray.get(i)).intValue());
                PrivacyUsersActivity.this.presentFragment(new ProfileActivity(view));
            }
        }
    }

    /* renamed from: org.telegram.ui.PrivacyUsersActivity$3 */
    class C22533 implements OnItemLongClickListener {

        /* renamed from: org.telegram.ui.PrivacyUsersActivity$3$1 */
        class C16381 implements OnClickListener {
            C16381() {
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    PrivacyUsersActivity.this.uidArray.remove(Integer.valueOf(PrivacyUsersActivity.this.selectedUserId));
                    PrivacyUsersActivity.this.listViewAdapter.notifyDataSetChanged();
                    if (PrivacyUsersActivity.this.delegate != null) {
                        PrivacyUsersActivity.this.delegate.didUpdatedUserList(PrivacyUsersActivity.this.uidArray, false);
                    }
                }
            }
        }

        C22533() {
        }

        public boolean onItemClick(View view, int i) {
            if (i >= 0 && i < PrivacyUsersActivity.this.uidArray.size()) {
                if (PrivacyUsersActivity.this.getParentActivity() != null) {
                    PrivacyUsersActivity.this.selectedUserId = ((Integer) PrivacyUsersActivity.this.uidArray.get(i)).intValue();
                    i = new Builder(PrivacyUsersActivity.this.getParentActivity());
                    i.setItems(new CharSequence[]{LocaleController.getString("Delete", C0446R.string.Delete)}, new C16381());
                    PrivacyUsersActivity.this.showDialog(i.create());
                    return true;
                }
            }
            return false;
        }
    }

    /* renamed from: org.telegram.ui.PrivacyUsersActivity$4 */
    class C22544 implements ThemeDescriptionDelegate {
        C22544() {
        }

        public void didSetColor() {
            if (PrivacyUsersActivity.this.listView != null) {
                int childCount = PrivacyUsersActivity.this.listView.getChildCount();
                for (int i = 0; i < childCount; i++) {
                    View childAt = PrivacyUsersActivity.this.listView.getChildAt(i);
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

        public boolean isEnabled(ViewHolder viewHolder) {
            return viewHolder.getAdapterPosition() != PrivacyUsersActivity.this.uidArray.size() ? true : null;
        }

        public int getItemCount() {
            if (PrivacyUsersActivity.this.uidArray.isEmpty()) {
                return 0;
            }
            return PrivacyUsersActivity.this.uidArray.size() + 1;
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            if (i != 0) {
                viewGroup = new TextInfoCell(this.mContext);
                ((TextInfoCell) viewGroup).setText(LocaleController.getString("RemoveFromListText", C0446R.string.RemoveFromListText));
            } else {
                viewGroup = new UserCell(this.mContext, 1, 0, false);
            }
            return new Holder(viewGroup);
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            if (viewHolder.getItemViewType() == 0) {
                CharSequence string;
                i = MessagesController.getInstance(PrivacyUsersActivity.this.currentAccount).getUser((Integer) PrivacyUsersActivity.this.uidArray.get(i));
                UserCell userCell = (UserCell) viewHolder.itemView;
                if (i.phone == null || i.phone.length() == 0) {
                    string = LocaleController.getString("NumberUnknown", C0446R.string.NumberUnknown);
                } else {
                    PhoneFormat instance = PhoneFormat.getInstance();
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("+");
                    stringBuilder.append(i.phone);
                    string = instance.format(stringBuilder.toString());
                }
                userCell.setData(i, null, string, 0);
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
        this.actionBar.setBackButtonImage(C0446R.drawable.ic_ab_back);
        int i = 1;
        this.actionBar.setAllowOverlayTitle(true);
        if (this.isGroup) {
            if (this.isAlwaysShare) {
                this.actionBar.setTitle(LocaleController.getString("AlwaysAllow", C0446R.string.AlwaysAllow));
            } else {
                this.actionBar.setTitle(LocaleController.getString("NeverAllow", C0446R.string.NeverAllow));
            }
        } else if (this.isAlwaysShare) {
            this.actionBar.setTitle(LocaleController.getString("AlwaysShareWithTitle", C0446R.string.AlwaysShareWithTitle));
        } else {
            this.actionBar.setTitle(LocaleController.getString("NeverShareWithTitle", C0446R.string.NeverShareWithTitle));
        }
        this.actionBar.setActionBarMenuOnItemClick(new C22511());
        this.actionBar.createMenu().addItem(1, (int) C0446R.drawable.plus);
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        this.emptyView = new EmptyTextProgressView(context);
        this.emptyView.showTextView();
        this.emptyView.setText(LocaleController.getString("NoContacts", C0446R.string.NoContacts));
        frameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView = new RecyclerListView(context);
        this.listView.setEmptyView(this.emptyView);
        this.listView.setVerticalScrollBarEnabled(false);
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
        this.listView.setOnItemClickListener(new C22522());
        this.listView.setOnItemLongClickListener(new C22533());
        return this.fragmentView;
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
        C22544 c22544 = new C22544();
        r11 = new ThemeDescription[19];
        r11[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector);
        r11[6] = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector);
        r11[7] = new ThemeDescription(this.emptyView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_emptyListPlaceholder);
        r11[8] = new ThemeDescription(this.listView, 0, new Class[]{TextInfoCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText5);
        r11[9] = new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, new String[]{"nameTextView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r11[10] = new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, new String[]{"statusColor"}, null, null, (ThemeDescriptionDelegate) c22544, Theme.key_windowBackgroundWhiteGrayText);
        r11[11] = new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, null, new Drawable[]{Theme.avatar_photoDrawable, Theme.avatar_broadcastDrawable, Theme.avatar_savedDrawable}, null, Theme.key_avatar_text);
        C22544 c225442 = c22544;
        r11[12] = new ThemeDescription(null, 0, null, null, null, c225442, Theme.key_avatar_backgroundRed);
        r11[13] = new ThemeDescription(null, 0, null, null, null, c225442, Theme.key_avatar_backgroundOrange);
        r11[14] = new ThemeDescription(null, 0, null, null, null, c225442, Theme.key_avatar_backgroundViolet);
        r11[15] = new ThemeDescription(null, 0, null, null, null, c225442, Theme.key_avatar_backgroundGreen);
        r11[16] = new ThemeDescription(null, 0, null, null, null, c225442, Theme.key_avatar_backgroundCyan);
        r11[17] = new ThemeDescription(null, 0, null, null, null, c225442, Theme.key_avatar_backgroundBlue);
        r11[18] = new ThemeDescription(null, 0, null, null, null, c225442, Theme.key_avatar_backgroundPink);
        return r11;
    }
}
