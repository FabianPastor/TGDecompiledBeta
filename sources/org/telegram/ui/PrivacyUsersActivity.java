package org.telegram.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Iterator;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.ManageChatTextCell;
import org.telegram.ui.Cells.ManageChatUserCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.ContactsActivity;
import org.telegram.ui.GroupCreateActivity;

public class PrivacyUsersActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate, ContactsActivity.ContactsActivityDelegate {
    /* access modifiers changed from: private */
    public int blockUserDetailRow;
    /* access modifiers changed from: private */
    public int blockUserRow;
    private boolean blockedUsersActivity;
    /* access modifiers changed from: private */
    public int currentType;
    private PrivacyActivityDelegate delegate;
    private EmptyTextProgressView emptyView;
    private boolean isAlwaysShare;
    private boolean isGroup;
    /* access modifiers changed from: private */
    public LinearLayoutManager layoutManager;
    private RecyclerListView listView;
    private ListAdapter listViewAdapter;
    /* access modifiers changed from: private */
    public int rowCount;
    /* access modifiers changed from: private */
    public ArrayList<Long> uidArray;
    /* access modifiers changed from: private */
    public int usersDetailRow;
    /* access modifiers changed from: private */
    public int usersEndRow;
    /* access modifiers changed from: private */
    public int usersHeaderRow;
    /* access modifiers changed from: private */
    public int usersStartRow;

    public interface PrivacyActivityDelegate {
        void didUpdateUserList(ArrayList<Long> arrayList, boolean z);
    }

    public PrivacyUsersActivity() {
        this.currentType = 1;
        this.blockedUsersActivity = true;
    }

    public PrivacyUsersActivity(int i, ArrayList<Long> arrayList, boolean z, boolean z2) {
        this.uidArray = arrayList;
        this.isAlwaysShare = z2;
        this.isGroup = z;
        this.blockedUsersActivity = false;
        this.currentType = i;
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.updateInterfaces);
        if (this.currentType == 1) {
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.blockedUsersDidLoad);
        }
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.updateInterfaces);
        if (this.currentType == 1) {
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.blockedUsersDidLoad);
        }
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setAllowOverlayTitle(true);
        int i = this.currentType;
        int i2 = 2;
        if (i == 1) {
            this.actionBar.setTitle(LocaleController.getString("BlockedUsers", NUM));
        } else if (i == 2) {
            if (this.isAlwaysShare) {
                this.actionBar.setTitle(LocaleController.getString("FilterAlwaysShow", NUM));
            } else {
                this.actionBar.setTitle(LocaleController.getString("FilterNeverShow", NUM));
            }
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
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    PrivacyUsersActivity.this.finishFragment();
                }
            }
        });
        FrameLayout frameLayout = new FrameLayout(context);
        this.fragmentView = frameLayout;
        FrameLayout frameLayout2 = frameLayout;
        frameLayout2.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
        EmptyTextProgressView emptyTextProgressView = new EmptyTextProgressView(context);
        this.emptyView = emptyTextProgressView;
        if (this.currentType == 1) {
            emptyTextProgressView.setText(LocaleController.getString("NoBlocked", NUM));
        } else {
            emptyTextProgressView.setText(LocaleController.getString("NoContacts", NUM));
        }
        frameLayout2.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f));
        RecyclerListView recyclerListView = new RecyclerListView(context);
        this.listView = recyclerListView;
        recyclerListView.setEmptyView(this.emptyView);
        RecyclerListView recyclerListView2 = this.listView;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, 1, false);
        this.layoutManager = linearLayoutManager;
        recyclerListView2.setLayoutManager(linearLayoutManager);
        this.listView.setVerticalScrollBarEnabled(false);
        RecyclerListView recyclerListView3 = this.listView;
        ListAdapter listAdapter = new ListAdapter(context);
        this.listViewAdapter = listAdapter;
        recyclerListView3.setAdapter(listAdapter);
        RecyclerListView recyclerListView4 = this.listView;
        if (LocaleController.isRTL) {
            i2 = 1;
        }
        recyclerListView4.setVerticalScrollbarPosition(i2);
        frameLayout2.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new PrivacyUsersActivity$$ExternalSyntheticLambda2(this));
        this.listView.setOnItemLongClickListener((RecyclerListView.OnItemLongClickListener) new PrivacyUsersActivity$$ExternalSyntheticLambda3(this));
        if (this.currentType == 1) {
            this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
                public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                    if (!PrivacyUsersActivity.this.getMessagesController().blockedEndReached) {
                        int abs = Math.abs(PrivacyUsersActivity.this.layoutManager.findLastVisibleItemPosition() - PrivacyUsersActivity.this.layoutManager.findFirstVisibleItemPosition()) + 1;
                        int itemCount = recyclerView.getAdapter().getItemCount();
                        if (abs > 0 && PrivacyUsersActivity.this.layoutManager.findLastVisibleItemPosition() >= itemCount - 10) {
                            PrivacyUsersActivity.this.getMessagesController().getBlockedPeers(false);
                        }
                    }
                }
            });
            if (getMessagesController().totalBlockedCount < 0) {
                this.emptyView.showProgress();
            } else {
                this.emptyView.showTextView();
            }
        }
        updateRows();
        return this.fragmentView;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$1(View view, int i) {
        if (i == this.blockUserRow) {
            if (this.currentType == 1) {
                presentFragment(new DialogOrContactPickerActivity());
                return;
            }
            Bundle bundle = new Bundle();
            bundle.putBoolean(this.isAlwaysShare ? "isAlwaysShare" : "isNeverShare", true);
            if (this.isGroup) {
                bundle.putInt("chatAddType", 1);
            } else if (this.currentType == 2) {
                bundle.putInt("chatAddType", 2);
            }
            GroupCreateActivity groupCreateActivity = new GroupCreateActivity(bundle);
            groupCreateActivity.setDelegate((GroupCreateActivity.GroupCreateActivityDelegate) new PrivacyUsersActivity$$ExternalSyntheticLambda4(this));
            presentFragment(groupCreateActivity);
        } else if (i >= this.usersStartRow && i < this.usersEndRow) {
            if (this.currentType == 1) {
                Bundle bundle2 = new Bundle();
                bundle2.putLong("user_id", getMessagesController().blockePeers.keyAt(i - this.usersStartRow));
                presentFragment(new ProfileActivity(bundle2));
                return;
            }
            Bundle bundle3 = new Bundle();
            long longValue = this.uidArray.get(i - this.usersStartRow).longValue();
            if (DialogObject.isUserDialog(longValue)) {
                bundle3.putLong("user_id", longValue);
            } else {
                bundle3.putLong("chat_id", -longValue);
            }
            presentFragment(new ProfileActivity(bundle3));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$0(ArrayList arrayList) {
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            Long l = (Long) it.next();
            if (!this.uidArray.contains(l)) {
                this.uidArray.add(l);
            }
        }
        updateRows();
        PrivacyActivityDelegate privacyActivityDelegate = this.delegate;
        if (privacyActivityDelegate != null) {
            privacyActivityDelegate.didUpdateUserList(this.uidArray, true);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$createView$2(View view, int i) {
        int i2 = this.usersStartRow;
        if (i < i2 || i >= this.usersEndRow) {
            return false;
        }
        if (this.currentType == 1) {
            showUnblockAlert(Long.valueOf(getMessagesController().blockePeers.keyAt(i - this.usersStartRow)));
        } else {
            showUnblockAlert(this.uidArray.get(i - i2));
        }
        return true;
    }

    public void setDelegate(PrivacyActivityDelegate privacyActivityDelegate) {
        this.delegate = privacyActivityDelegate;
    }

    /* access modifiers changed from: private */
    public void showUnblockAlert(Long l) {
        if (getParentActivity() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
            builder.setItems(this.currentType == 1 ? new CharSequence[]{LocaleController.getString("Unblock", NUM)} : new CharSequence[]{LocaleController.getString("Delete", NUM)}, new PrivacyUsersActivity$$ExternalSyntheticLambda0(this, l));
            showDialog(builder.create());
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showUnblockAlert$3(Long l, DialogInterface dialogInterface, int i) {
        if (i != 0) {
            return;
        }
        if (this.currentType == 1) {
            getMessagesController().unblockPeer(l.longValue());
            return;
        }
        this.uidArray.remove(l);
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
        int i;
        this.rowCount = 0;
        if (!this.blockedUsersActivity || getMessagesController().totalBlockedCount >= 0) {
            int i2 = this.rowCount;
            int i3 = i2 + 1;
            this.rowCount = i3;
            this.blockUserRow = i2;
            this.rowCount = i3 + 1;
            this.blockUserDetailRow = i3;
            if (this.currentType == 1) {
                i = getMessagesController().blockePeers.size();
            } else {
                i = this.uidArray.size();
            }
            if (i != 0) {
                int i4 = this.rowCount;
                int i5 = i4 + 1;
                this.rowCount = i5;
                this.usersHeaderRow = i4;
                this.usersStartRow = i5;
                int i6 = i5 + i;
                this.rowCount = i6;
                this.usersEndRow = i6;
                this.rowCount = i6 + 1;
                this.usersDetailRow = i6;
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
            int intValue = objArr[0].intValue();
            if ((MessagesController.UPDATE_MASK_AVATAR & intValue) != 0 || (MessagesController.UPDATE_MASK_NAME & intValue) != 0) {
                updateVisibleRows(intValue);
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

    public void didSelectContact(TLRPC$User tLRPC$User, String str, ContactsActivity contactsActivity) {
        if (tLRPC$User != null) {
            getMessagesController().blockPeer(tLRPC$User.id);
        }
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public int getItemCount() {
            return PrivacyUsersActivity.this.rowCount;
        }

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            int itemViewType = viewHolder.getItemViewType();
            return itemViewType == 0 || itemViewType == 2;
        }

        /* access modifiers changed from: private */
        public /* synthetic */ boolean lambda$onCreateViewHolder$0(ManageChatUserCell manageChatUserCell, boolean z) {
            if (!z) {
                return true;
            }
            PrivacyUsersActivity.this.showUnblockAlert((Long) manageChatUserCell.getTag());
            return true;
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v2, resolved type: org.telegram.ui.Cells.ManageChatUserCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v5, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v6, resolved type: org.telegram.ui.Cells.TextInfoPrivacyCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v8, resolved type: org.telegram.ui.Cells.ManageChatTextCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v9, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v10, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v2, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX WARNING: type inference failed for: r8v2, types: [android.view.View] */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public androidx.recyclerview.widget.RecyclerView.ViewHolder onCreateViewHolder(android.view.ViewGroup r8, int r9) {
            /*
                r7 = this;
                r8 = 1
                java.lang.String r0 = "windowBackgroundWhite"
                if (r9 == 0) goto L_0x003d
                if (r9 == r8) goto L_0x0035
                r8 = 2
                if (r9 == r8) goto L_0x0026
                org.telegram.ui.Cells.HeaderCell r8 = new org.telegram.ui.Cells.HeaderCell
                android.content.Context r2 = r7.mContext
                r4 = 21
                r5 = 11
                r6 = 0
                java.lang.String r3 = "windowBackgroundWhiteBlueHeader"
                r1 = r8
                r1.<init>(r2, r3, r4, r5, r6)
                int r9 = org.telegram.ui.ActionBar.Theme.getColor(r0)
                r8.setBackgroundColor(r9)
                r9 = 43
                r8.setHeight(r9)
                goto L_0x0056
            L_0x0026:
                org.telegram.ui.Cells.ManageChatTextCell r8 = new org.telegram.ui.Cells.ManageChatTextCell
                android.content.Context r9 = r7.mContext
                r8.<init>(r9)
                int r9 = org.telegram.ui.ActionBar.Theme.getColor(r0)
                r8.setBackgroundColor(r9)
                goto L_0x0056
            L_0x0035:
                org.telegram.ui.Cells.TextInfoPrivacyCell r8 = new org.telegram.ui.Cells.TextInfoPrivacyCell
                android.content.Context r9 = r7.mContext
                r8.<init>(r9)
                goto L_0x0056
            L_0x003d:
                org.telegram.ui.Cells.ManageChatUserCell r9 = new org.telegram.ui.Cells.ManageChatUserCell
                android.content.Context r1 = r7.mContext
                r2 = 7
                r3 = 6
                r9.<init>(r1, r2, r3, r8)
                int r8 = org.telegram.ui.ActionBar.Theme.getColor(r0)
                r9.setBackgroundColor(r8)
                org.telegram.ui.PrivacyUsersActivity$ListAdapter$$ExternalSyntheticLambda0 r8 = new org.telegram.ui.PrivacyUsersActivity$ListAdapter$$ExternalSyntheticLambda0
                r8.<init>(r7)
                r9.setDelegate(r8)
                r8 = r9
            L_0x0056:
                org.telegram.ui.Components.RecyclerListView$Holder r9 = new org.telegram.ui.Components.RecyclerListView$Holder
                r9.<init>(r8)
                return r9
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PrivacyUsersActivity.ListAdapter.onCreateViewHolder(android.view.ViewGroup, int):androidx.recyclerview.widget.RecyclerView$ViewHolder");
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            long j;
            String str;
            String str2;
            int itemViewType = viewHolder.getItemViewType();
            boolean z = false;
            if (itemViewType == 0) {
                ManageChatUserCell manageChatUserCell = (ManageChatUserCell) viewHolder.itemView;
                if (PrivacyUsersActivity.this.currentType == 1) {
                    j = PrivacyUsersActivity.this.getMessagesController().blockePeers.keyAt(i - PrivacyUsersActivity.this.usersStartRow);
                } else {
                    j = ((Long) PrivacyUsersActivity.this.uidArray.get(i - PrivacyUsersActivity.this.usersStartRow)).longValue();
                }
                manageChatUserCell.setTag(Long.valueOf(j));
                if (j > 0) {
                    TLRPC$User user = PrivacyUsersActivity.this.getMessagesController().getUser(Long.valueOf(j));
                    if (user != null) {
                        if (user.bot) {
                            str2 = LocaleController.getString("Bot", NUM).substring(0, 1).toUpperCase() + LocaleController.getString("Bot", NUM).substring(1);
                        } else {
                            String str3 = user.phone;
                            if (str3 == null || str3.length() == 0) {
                                str2 = LocaleController.getString("NumberUnknown", NUM);
                            } else {
                                str2 = PhoneFormat.getInstance().format("+" + user.phone);
                            }
                        }
                        if (i != PrivacyUsersActivity.this.usersEndRow - 1) {
                            z = true;
                        }
                        manageChatUserCell.setData(user, (CharSequence) null, str2, z);
                        return;
                    }
                    return;
                }
                TLRPC$Chat chat = PrivacyUsersActivity.this.getMessagesController().getChat(Long.valueOf(-j));
                if (chat != null) {
                    int i2 = chat.participants_count;
                    if (i2 != 0) {
                        str = LocaleController.formatPluralString("Members", i2);
                    } else if (chat.has_geo) {
                        str = LocaleController.getString("MegaLocation", NUM);
                    } else if (TextUtils.isEmpty(chat.username)) {
                        str = LocaleController.getString("MegaPrivate", NUM);
                    } else {
                        str = LocaleController.getString("MegaPublic", NUM);
                    }
                    if (i != PrivacyUsersActivity.this.usersEndRow - 1) {
                        z = true;
                    }
                    manageChatUserCell.setData(chat, (CharSequence) null, str, z);
                }
            } else if (itemViewType == 1) {
                TextInfoPrivacyCell textInfoPrivacyCell = (TextInfoPrivacyCell) viewHolder.itemView;
                if (i == PrivacyUsersActivity.this.blockUserDetailRow) {
                    if (PrivacyUsersActivity.this.currentType == 1) {
                        textInfoPrivacyCell.setText(LocaleController.getString("BlockedUsersInfo", NUM));
                    } else {
                        textInfoPrivacyCell.setText((CharSequence) null);
                    }
                    if (PrivacyUsersActivity.this.usersStartRow == -1) {
                        textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                    } else {
                        textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                    }
                } else if (i == PrivacyUsersActivity.this.usersDetailRow) {
                    textInfoPrivacyCell.setText("");
                    textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                }
            } else if (itemViewType == 2) {
                ManageChatTextCell manageChatTextCell = (ManageChatTextCell) viewHolder.itemView;
                manageChatTextCell.setColors("windowBackgroundWhiteBlueIcon", "windowBackgroundWhiteBlueButton");
                if (PrivacyUsersActivity.this.currentType == 1) {
                    manageChatTextCell.setText(LocaleController.getString("BlockUser", NUM), (String) null, NUM, false);
                } else {
                    manageChatTextCell.setText(LocaleController.getString("PrivacyAddAnException", NUM), (String) null, NUM, false);
                }
            } else if (itemViewType == 3) {
                HeaderCell headerCell = (HeaderCell) viewHolder.itemView;
                if (i != PrivacyUsersActivity.this.usersHeaderRow) {
                    return;
                }
                if (PrivacyUsersActivity.this.currentType == 1) {
                    headerCell.setText(LocaleController.formatPluralString("BlockedUsersCount", PrivacyUsersActivity.this.getMessagesController().totalBlockedCount));
                } else {
                    headerCell.setText(LocaleController.getString("PrivacyExceptions", NUM));
                }
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

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        PrivacyUsersActivity$$ExternalSyntheticLambda1 privacyUsersActivity$$ExternalSyntheticLambda1 = new PrivacyUsersActivity$$ExternalSyntheticLambda1(this);
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{ManageChatUserCell.class, ManageChatTextCell.class, HeaderCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultIcon"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultTitle"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSelector"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        arrayList.add(new ThemeDescription(this.emptyView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "emptyListPlaceholder"));
        arrayList.add(new ThemeDescription(this.emptyView, ThemeDescription.FLAG_PROGRESSBAR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "progressCircle"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{ManageChatUserCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        PrivacyUsersActivity$$ExternalSyntheticLambda1 privacyUsersActivity$$ExternalSyntheticLambda12 = privacyUsersActivity$$ExternalSyntheticLambda1;
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{ManageChatUserCell.class}, new String[]{"statusColor"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) privacyUsersActivity$$ExternalSyntheticLambda12, "windowBackgroundWhiteGrayText"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{ManageChatUserCell.class}, new String[]{"statusOnlineColor"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) privacyUsersActivity$$ExternalSyntheticLambda12, "windowBackgroundWhiteBlueText"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{ManageChatUserCell.class}, (Paint) null, Theme.avatarDrawables, (ThemeDescription.ThemeDescriptionDelegate) null, "avatar_text"));
        PrivacyUsersActivity$$ExternalSyntheticLambda1 privacyUsersActivity$$ExternalSyntheticLambda13 = privacyUsersActivity$$ExternalSyntheticLambda1;
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, privacyUsersActivity$$ExternalSyntheticLambda13, "avatar_backgroundRed"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, privacyUsersActivity$$ExternalSyntheticLambda13, "avatar_backgroundOrange"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, privacyUsersActivity$$ExternalSyntheticLambda13, "avatar_backgroundViolet"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, privacyUsersActivity$$ExternalSyntheticLambda13, "avatar_backgroundGreen"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, privacyUsersActivity$$ExternalSyntheticLambda13, "avatar_backgroundCyan"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, privacyUsersActivity$$ExternalSyntheticLambda13, "avatar_backgroundBlue"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, privacyUsersActivity$$ExternalSyntheticLambda13, "avatar_backgroundPink"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueHeader"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayIcon"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueButton"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueIcon"));
        return arrayList;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$getThemeDescriptions$4() {
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
