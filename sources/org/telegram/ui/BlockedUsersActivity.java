package org.telegram.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.support.widget.LinearLayoutManager;
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
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;
import org.telegram.ui.ContactsActivity.ContactsActivityDelegate;

public class BlockedUsersActivity extends BaseFragment implements NotificationCenterDelegate, ContactsActivityDelegate {
    private static final int block_user = 1;
    private EmptyTextProgressView emptyView;
    private RecyclerListView listView;
    private ListAdapter listViewAdapter;
    private int selectedUserId;

    private class ListAdapter extends SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public int getItemCount() {
            if (MessagesController.getInstance(BlockedUsersActivity.this.currentAccount).blockedUsers.size() == 0) {
                return 0;
            }
            return MessagesController.getInstance(BlockedUsersActivity.this.currentAccount).blockedUsers.size() + 1;
        }

        public boolean isEnabled(ViewHolder viewHolder) {
            return viewHolder.getItemViewType() == 0;
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View textInfoCell;
            if (i != 0) {
                textInfoCell = new TextInfoCell(this.mContext);
                textInfoCell.setText(LocaleController.getString("UnblockText", NUM));
            } else {
                textInfoCell = new UserCell(this.mContext, 1, 0, false);
            }
            return new Holder(textInfoCell);
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            if (viewHolder.getItemViewType() == 0) {
                User user = MessagesController.getInstance(BlockedUsersActivity.this.currentAccount).getUser(Integer.valueOf(MessagesController.getInstance(BlockedUsersActivity.this.currentAccount).blockedUsers.keyAt(i)));
                if (user != null) {
                    CharSequence stringBuilder;
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
                    ((UserCell) viewHolder.itemView).setData(user, null, stringBuilder, 0);
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
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.blockedUsersDidLoad);
        MessagesController.getInstance(this.currentAccount).getBlockedUsers(false);
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.updateInterfaces);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.blockedUsersDidLoad);
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(NUM);
        int i = 1;
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("BlockedUsers", NUM));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    BlockedUsersActivity.this.finishFragment();
                } else if (i == 1) {
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("onlyUsers", true);
                    bundle.putBoolean("destroyAfterSelect", true);
                    bundle.putBoolean("returnAsResult", true);
                    ContactsActivity contactsActivity = new ContactsActivity(bundle);
                    contactsActivity.setDelegate(BlockedUsersActivity.this);
                    BlockedUsersActivity.this.presentFragment(contactsActivity);
                }
            }
        });
        this.actionBar.createMenu().addItem(1, NUM);
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        this.emptyView = new EmptyTextProgressView(context);
        this.emptyView.setText(LocaleController.getString("NoBlocked", NUM));
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
        this.listView.setOnItemClickListener(new -$$Lambda$BlockedUsersActivity$Hq8mFhcGhTwPCLASSNAMELN_u855TJY4(this));
        this.listView.setOnItemLongClickListener(new -$$Lambda$BlockedUsersActivity$NwqYpt7ad6K2bZSDUlRTCTYm5t8(this));
        if (MessagesController.getInstance(this.currentAccount).loadingBlockedUsers) {
            this.emptyView.showProgress();
        } else {
            this.emptyView.showTextView();
        }
        return this.fragmentView;
    }

    public /* synthetic */ void lambda$createView$0$BlockedUsersActivity(View view, int i) {
        if (i < MessagesController.getInstance(this.currentAccount).blockedUsers.size()) {
            Bundle bundle = new Bundle();
            bundle.putInt("user_id", MessagesController.getInstance(this.currentAccount).blockedUsers.keyAt(i));
            presentFragment(new ProfileActivity(bundle));
        }
    }

    public /* synthetic */ boolean lambda$createView$2$BlockedUsersActivity(View view, int i) {
        if (i < MessagesController.getInstance(this.currentAccount).blockedUsers.size() && getParentActivity() != null) {
            this.selectedUserId = MessagesController.getInstance(this.currentAccount).blockedUsers.keyAt(i);
            Builder builder = new Builder(getParentActivity());
            builder.setItems(new CharSequence[]{LocaleController.getString("Unblock", NUM)}, new -$$Lambda$BlockedUsersActivity$0DhC_S1t4hVyk6KsdC3UxQGSm-k(this));
            showDialog(builder.create());
        }
        return true;
    }

    public /* synthetic */ void lambda$null$1$BlockedUsersActivity(DialogInterface dialogInterface, int i) {
        if (i == 0) {
            MessagesController.getInstance(this.currentAccount).unblockUser(this.selectedUserId);
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
            ListAdapter listAdapter = this.listViewAdapter;
            if (listAdapter != null) {
                listAdapter.notifyDataSetChanged();
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

    public void onResume() {
        super.onResume();
        ListAdapter listAdapter = this.listViewAdapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    public void didSelectContact(User user, String str, ContactsActivity contactsActivity) {
        if (user != null) {
            MessagesController.getInstance(this.currentAccount).blockUser(user.id);
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        -$$Lambda$BlockedUsersActivity$QTCdbbYBKIAF6D5ghMrtIj3D9RE -__lambda_blockedusersactivity_qtcdbbybkiaf6d5ghmrtij3d9re = new -$$Lambda$BlockedUsersActivity$QTCdbbYBKIAF6D5ghMrtIj3D9RE(this);
        ThemeDescription[] themeDescriptionArr = new ThemeDescription[20];
        themeDescriptionArr[0] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite");
        themeDescriptionArr[1] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault");
        themeDescriptionArr[2] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault");
        themeDescriptionArr[3] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon");
        themeDescriptionArr[4] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle");
        themeDescriptionArr[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector");
        themeDescriptionArr[6] = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21");
        themeDescriptionArr[7] = new ThemeDescription(this.emptyView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "emptyListPlaceholder");
        themeDescriptionArr[8] = new ThemeDescription(this.emptyView, ThemeDescription.FLAG_PROGRESSBAR, null, null, null, null, "progressCircle");
        themeDescriptionArr[9] = new ThemeDescription(this.listView, 0, new Class[]{TextInfoCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteGrayText5");
        themeDescriptionArr[10] = new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, new String[]{"nameTextView"}, null, null, null, "windowBackgroundWhiteBlackText");
        themeDescriptionArr[11] = new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, new String[]{"statusColor"}, null, null, (ThemeDescriptionDelegate) -__lambda_blockedusersactivity_qtcdbbybkiaf6d5ghmrtij3d9re, "windowBackgroundWhiteGrayText");
        themeDescriptionArr[12] = new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, null, new Drawable[]{Theme.avatar_broadcastDrawable, Theme.avatar_savedDrawable}, null, "avatar_text");
        -$$Lambda$BlockedUsersActivity$QTCdbbYBKIAF6D5ghMrtIj3D9RE -__lambda_blockedusersactivity_qtcdbbybkiaf6d5ghmrtij3d9re2 = -__lambda_blockedusersactivity_qtcdbbybkiaf6d5ghmrtij3d9re;
        themeDescriptionArr[13] = new ThemeDescription(null, 0, null, null, null, -__lambda_blockedusersactivity_qtcdbbybkiaf6d5ghmrtij3d9re2, "avatar_backgroundRed");
        themeDescriptionArr[14] = new ThemeDescription(null, 0, null, null, null, -__lambda_blockedusersactivity_qtcdbbybkiaf6d5ghmrtij3d9re2, "avatar_backgroundOrange");
        themeDescriptionArr[15] = new ThemeDescription(null, 0, null, null, null, -__lambda_blockedusersactivity_qtcdbbybkiaf6d5ghmrtij3d9re2, "avatar_backgroundViolet");
        themeDescriptionArr[16] = new ThemeDescription(null, 0, null, null, null, -__lambda_blockedusersactivity_qtcdbbybkiaf6d5ghmrtij3d9re2, "avatar_backgroundGreen");
        themeDescriptionArr[17] = new ThemeDescription(null, 0, null, null, null, -__lambda_blockedusersactivity_qtcdbbybkiaf6d5ghmrtij3d9re2, "avatar_backgroundCyan");
        themeDescriptionArr[18] = new ThemeDescription(null, 0, null, null, null, -__lambda_blockedusersactivity_qtcdbbybkiaf6d5ghmrtij3d9re2, "avatar_backgroundBlue");
        themeDescriptionArr[19] = new ThemeDescription(null, 0, null, null, null, -__lambda_blockedusersactivity_qtcdbbybkiaf6d5ghmrtij3d9re2, "avatar_backgroundPink");
        return themeDescriptionArr;
    }

    public /* synthetic */ void lambda$getThemeDescriptions$3$BlockedUsersActivity() {
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
