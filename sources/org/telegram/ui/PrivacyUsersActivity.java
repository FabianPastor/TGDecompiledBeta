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
import org.telegram.tgnet.TLRPC;
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
    public static final int TYPE_BLOCKED = 1;
    public static final int TYPE_FILTER = 2;
    public static final int TYPE_PRIVACY = 0;
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

    public PrivacyUsersActivity(int type, ArrayList<Long> users, boolean group, boolean always) {
        this.uidArray = users;
        this.isAlwaysShare = always;
        this.isGroup = group;
        this.blockedUsersActivity = false;
        this.currentType = type;
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
            public void onItemClick(int id) {
                if (id == -1) {
                    PrivacyUsersActivity.this.finishFragment();
                }
            }
        });
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        frameLayout.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
        EmptyTextProgressView emptyTextProgressView = new EmptyTextProgressView(context);
        this.emptyView = emptyTextProgressView;
        if (this.currentType == 1) {
            emptyTextProgressView.setText(LocaleController.getString("NoBlocked", NUM));
        } else {
            emptyTextProgressView.setText(LocaleController.getString("NoContacts", NUM));
        }
        frameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f));
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
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new PrivacyUsersActivity$$ExternalSyntheticLambda2(this));
        this.listView.setOnItemLongClickListener((RecyclerListView.OnItemLongClickListener) new PrivacyUsersActivity$$ExternalSyntheticLambda3(this));
        if (this.currentType == 1) {
            this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    if (!PrivacyUsersActivity.this.getMessagesController().blockedEndReached) {
                        int visibleItemCount = Math.abs(PrivacyUsersActivity.this.layoutManager.findLastVisibleItemPosition() - PrivacyUsersActivity.this.layoutManager.findFirstVisibleItemPosition()) + 1;
                        int totalItemCount = recyclerView.getAdapter().getItemCount();
                        if (visibleItemCount > 0 && PrivacyUsersActivity.this.layoutManager.findLastVisibleItemPosition() >= totalItemCount - 10) {
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

    /* renamed from: lambda$createView$1$org-telegram-ui-PrivacyUsersActivity  reason: not valid java name */
    public /* synthetic */ void m4383lambda$createView$1$orgtelegramuiPrivacyUsersActivity(View view, int position) {
        if (position == this.blockUserRow) {
            if (this.currentType == 1) {
                presentFragment(new DialogOrContactPickerActivity());
                return;
            }
            Bundle args = new Bundle();
            args.putBoolean(this.isAlwaysShare ? "isAlwaysShare" : "isNeverShare", true);
            if (this.isGroup) {
                args.putInt("chatAddType", 1);
            } else if (this.currentType == 2) {
                args.putInt("chatAddType", 2);
            }
            GroupCreateActivity fragment = new GroupCreateActivity(args);
            fragment.setDelegate((GroupCreateActivity.GroupCreateActivityDelegate) new PrivacyUsersActivity$$ExternalSyntheticLambda4(this));
            presentFragment(fragment);
        } else if (position >= this.usersStartRow && position < this.usersEndRow) {
            if (this.currentType == 1) {
                Bundle args2 = new Bundle();
                args2.putLong("user_id", getMessagesController().blockePeers.keyAt(position - this.usersStartRow));
                presentFragment(new ProfileActivity(args2));
                return;
            }
            Bundle args3 = new Bundle();
            long uid = this.uidArray.get(position - this.usersStartRow).longValue();
            if (DialogObject.isUserDialog(uid)) {
                args3.putLong("user_id", uid);
            } else {
                args3.putLong("chat_id", -uid);
            }
            presentFragment(new ProfileActivity(args3));
        }
    }

    /* renamed from: lambda$createView$0$org-telegram-ui-PrivacyUsersActivity  reason: not valid java name */
    public /* synthetic */ void m4382lambda$createView$0$orgtelegramuiPrivacyUsersActivity(ArrayList ids) {
        Iterator it = ids.iterator();
        while (it.hasNext()) {
            Long id1 = (Long) it.next();
            if (!this.uidArray.contains(id1)) {
                this.uidArray.add(id1);
            }
        }
        updateRows();
        PrivacyActivityDelegate privacyActivityDelegate = this.delegate;
        if (privacyActivityDelegate != null) {
            privacyActivityDelegate.didUpdateUserList(this.uidArray, true);
        }
    }

    /* renamed from: lambda$createView$2$org-telegram-ui-PrivacyUsersActivity  reason: not valid java name */
    public /* synthetic */ boolean m4384lambda$createView$2$orgtelegramuiPrivacyUsersActivity(View view, int position) {
        int i = this.usersStartRow;
        if (position < i || position >= this.usersEndRow) {
            return false;
        }
        if (this.currentType == 1) {
            showUnblockAlert(Long.valueOf(getMessagesController().blockePeers.keyAt(position - this.usersStartRow)));
        } else {
            showUnblockAlert(this.uidArray.get(position - i));
        }
        return true;
    }

    public void setDelegate(PrivacyActivityDelegate privacyActivityDelegate) {
        this.delegate = privacyActivityDelegate;
    }

    /* access modifiers changed from: private */
    public void showUnblockAlert(Long uid) {
        if (getParentActivity() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
            builder.setItems(this.currentType == 1 ? new CharSequence[]{LocaleController.getString("Unblock", NUM)} : new CharSequence[]{LocaleController.getString("Delete", NUM)}, new PrivacyUsersActivity$$ExternalSyntheticLambda0(this, uid));
            showDialog(builder.create());
        }
    }

    /* renamed from: lambda$showUnblockAlert$3$org-telegram-ui-PrivacyUsersActivity  reason: not valid java name */
    public /* synthetic */ void m4386lambda$showUnblockAlert$3$orgtelegramuiPrivacyUsersActivity(Long uid, DialogInterface dialogInterface, int i) {
        if (i != 0) {
            return;
        }
        if (this.currentType == 1) {
            getMessagesController().unblockPeer(uid.longValue());
            return;
        }
        this.uidArray.remove(uid);
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
        int count;
        this.rowCount = 0;
        if (!this.blockedUsersActivity || getMessagesController().totalBlockedCount >= 0) {
            int i = this.rowCount;
            int i2 = i + 1;
            this.rowCount = i2;
            this.blockUserRow = i;
            this.rowCount = i2 + 1;
            this.blockUserDetailRow = i2;
            if (this.currentType == 1) {
                count = getMessagesController().blockePeers.size();
            } else {
                count = this.uidArray.size();
            }
            if (count != 0) {
                int i3 = this.rowCount;
                int i4 = i3 + 1;
                this.rowCount = i4;
                this.usersHeaderRow = i3;
                this.usersStartRow = i4;
                int i5 = i4 + count;
                this.rowCount = i5;
                this.usersEndRow = i5;
                this.rowCount = i5 + 1;
                this.usersDetailRow = i5;
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

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.updateInterfaces) {
            int mask = args[0].intValue();
            if ((MessagesController.UPDATE_MASK_AVATAR & mask) != 0 || (MessagesController.UPDATE_MASK_NAME & mask) != 0) {
                updateVisibleRows(mask);
            }
        } else if (id == NotificationCenter.blockedUsersDidLoad) {
            this.emptyView.showTextView();
            updateRows();
        }
    }

    private void updateVisibleRows(int mask) {
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            int count = recyclerListView.getChildCount();
            for (int a = 0; a < count; a++) {
                View child = this.listView.getChildAt(a);
                if (child instanceof ManageChatUserCell) {
                    ((ManageChatUserCell) child).update(mask);
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

    public void didSelectContact(TLRPC.User user, String param, ContactsActivity activity) {
        if (user != null) {
            getMessagesController().blockPeer(user.id);
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

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int viewType = holder.getItemViewType();
            return viewType == 0 || viewType == 2;
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v1, resolved type: org.telegram.ui.Cells.ManageChatUserCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v5, resolved type: org.telegram.ui.Cells.TextInfoPrivacyCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v3, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v4, resolved type: org.telegram.ui.Cells.ManageChatTextCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v5, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v6, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v2, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX WARNING: type inference failed for: r1v0, types: [android.view.View] */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public androidx.recyclerview.widget.RecyclerView.ViewHolder onCreateViewHolder(android.view.ViewGroup r10, int r11) {
            /*
                r9 = this;
                java.lang.String r0 = "windowBackgroundWhite"
                switch(r11) {
                    case 0: goto L_0x003a;
                    case 1: goto L_0x0031;
                    case 2: goto L_0x0022;
                    default: goto L_0x0005;
                }
            L_0x0005:
                org.telegram.ui.Cells.HeaderCell r1 = new org.telegram.ui.Cells.HeaderCell
                android.content.Context r4 = r9.mContext
                r6 = 21
                r7 = 11
                r8 = 0
                java.lang.String r5 = "windowBackgroundWhiteBlueHeader"
                r3 = r1
                r3.<init>(r4, r5, r6, r7, r8)
                int r0 = org.telegram.ui.ActionBar.Theme.getColor(r0)
                r1.setBackgroundColor(r0)
                r0 = 43
                r1.setHeight(r0)
                r0 = r1
                goto L_0x0057
            L_0x0022:
                org.telegram.ui.Cells.ManageChatTextCell r1 = new org.telegram.ui.Cells.ManageChatTextCell
                android.content.Context r2 = r9.mContext
                r1.<init>(r2)
                int r0 = org.telegram.ui.ActionBar.Theme.getColor(r0)
                r1.setBackgroundColor(r0)
                goto L_0x0057
            L_0x0031:
                org.telegram.ui.Cells.TextInfoPrivacyCell r0 = new org.telegram.ui.Cells.TextInfoPrivacyCell
                android.content.Context r1 = r9.mContext
                r0.<init>(r1)
                r1 = r0
                goto L_0x0057
            L_0x003a:
                org.telegram.ui.Cells.ManageChatUserCell r1 = new org.telegram.ui.Cells.ManageChatUserCell
                android.content.Context r2 = r9.mContext
                r3 = 7
                r4 = 6
                r5 = 1
                r1.<init>(r2, r3, r4, r5)
                int r0 = org.telegram.ui.ActionBar.Theme.getColor(r0)
                r1.setBackgroundColor(r0)
                r0 = r1
                org.telegram.ui.Cells.ManageChatUserCell r0 = (org.telegram.ui.Cells.ManageChatUserCell) r0
                org.telegram.ui.PrivacyUsersActivity$ListAdapter$$ExternalSyntheticLambda0 r2 = new org.telegram.ui.PrivacyUsersActivity$ListAdapter$$ExternalSyntheticLambda0
                r2.<init>(r9)
                r0.setDelegate(r2)
            L_0x0057:
                org.telegram.ui.Components.RecyclerListView$Holder r0 = new org.telegram.ui.Components.RecyclerListView$Holder
                r0.<init>(r1)
                return r0
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PrivacyUsersActivity.ListAdapter.onCreateViewHolder(android.view.ViewGroup, int):androidx.recyclerview.widget.RecyclerView$ViewHolder");
        }

        /* renamed from: lambda$onCreateViewHolder$0$org-telegram-ui-PrivacyUsersActivity$ListAdapter  reason: not valid java name */
        public /* synthetic */ boolean m4387xd7d3d381(ManageChatUserCell cell, boolean click) {
            if (!click) {
                return true;
            }
            PrivacyUsersActivity.this.showUnblockAlert((Long) cell.getTag());
            return true;
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            long uid;
            String subtitle;
            String number;
            boolean z = false;
            switch (holder.getItemViewType()) {
                case 0:
                    ManageChatUserCell userCell = (ManageChatUserCell) holder.itemView;
                    if (PrivacyUsersActivity.this.currentType == 1) {
                        uid = PrivacyUsersActivity.this.getMessagesController().blockePeers.keyAt(position - PrivacyUsersActivity.this.usersStartRow);
                    } else {
                        uid = ((Long) PrivacyUsersActivity.this.uidArray.get(position - PrivacyUsersActivity.this.usersStartRow)).longValue();
                    }
                    userCell.setTag(Long.valueOf(uid));
                    if (uid > 0) {
                        TLRPC.User user = PrivacyUsersActivity.this.getMessagesController().getUser(Long.valueOf(uid));
                        if (user != null) {
                            if (user.bot) {
                                number = LocaleController.getString("Bot", NUM).substring(0, 1).toUpperCase() + LocaleController.getString("Bot", NUM).substring(1);
                            } else if (user.phone == null || user.phone.length() == 0) {
                                number = LocaleController.getString("NumberUnknown", NUM);
                            } else {
                                number = PhoneFormat.getInstance().format("+" + user.phone);
                            }
                            if (position != PrivacyUsersActivity.this.usersEndRow - 1) {
                                z = true;
                            }
                            userCell.setData(user, (CharSequence) null, number, z);
                            return;
                        }
                        return;
                    }
                    TLRPC.Chat chat = PrivacyUsersActivity.this.getMessagesController().getChat(Long.valueOf(-uid));
                    if (chat != null) {
                        if (chat.participants_count != 0) {
                            subtitle = LocaleController.formatPluralString("Members", chat.participants_count, new Object[0]);
                        } else if (chat.has_geo) {
                            subtitle = LocaleController.getString("MegaLocation", NUM);
                        } else if (TextUtils.isEmpty(chat.username)) {
                            subtitle = LocaleController.getString("MegaPrivate", NUM);
                        } else {
                            subtitle = LocaleController.getString("MegaPublic", NUM);
                        }
                        if (position != PrivacyUsersActivity.this.usersEndRow - 1) {
                            z = true;
                        }
                        userCell.setData(chat, (CharSequence) null, subtitle, z);
                        return;
                    }
                    return;
                case 1:
                    TextInfoPrivacyCell privacyCell = (TextInfoPrivacyCell) holder.itemView;
                    if (position == PrivacyUsersActivity.this.blockUserDetailRow) {
                        if (PrivacyUsersActivity.this.currentType == 1) {
                            privacyCell.setText(LocaleController.getString("BlockedUsersInfo", NUM));
                        } else {
                            privacyCell.setText((CharSequence) null);
                        }
                        if (PrivacyUsersActivity.this.usersStartRow == -1) {
                            privacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                            return;
                        } else {
                            privacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                            return;
                        }
                    } else if (position == PrivacyUsersActivity.this.usersDetailRow) {
                        privacyCell.setText("");
                        privacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                        return;
                    } else {
                        return;
                    }
                case 2:
                    ManageChatTextCell actionCell = (ManageChatTextCell) holder.itemView;
                    actionCell.setColors("windowBackgroundWhiteBlueIcon", "windowBackgroundWhiteBlueButton");
                    if (PrivacyUsersActivity.this.currentType == 1) {
                        actionCell.setText(LocaleController.getString("BlockUser", NUM), (String) null, NUM, false);
                        return;
                    } else {
                        actionCell.setText(LocaleController.getString("PrivacyAddAnException", NUM), (String) null, NUM, false);
                        return;
                    }
                case 3:
                    HeaderCell headerCell = (HeaderCell) holder.itemView;
                    if (position != PrivacyUsersActivity.this.usersHeaderRow) {
                        return;
                    }
                    if (PrivacyUsersActivity.this.currentType == 1) {
                        headerCell.setText(LocaleController.formatPluralString("BlockedUsersCount", PrivacyUsersActivity.this.getMessagesController().totalBlockedCount, new Object[0]));
                        return;
                    } else {
                        headerCell.setText(LocaleController.getString("PrivacyExceptions", NUM));
                        return;
                    }
                default:
                    return;
            }
        }

        public int getItemViewType(int position) {
            if (position == PrivacyUsersActivity.this.usersHeaderRow) {
                return 3;
            }
            if (position == PrivacyUsersActivity.this.blockUserRow) {
                return 2;
            }
            if (position == PrivacyUsersActivity.this.blockUserDetailRow || position == PrivacyUsersActivity.this.usersDetailRow) {
                return 1;
            }
            return 0;
        }
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();
        ThemeDescription.ThemeDescriptionDelegate cellDelegate = new PrivacyUsersActivity$$ExternalSyntheticLambda1(this);
        themeDescriptions.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{ManageChatUserCell.class, ManageChatTextCell.class, HeaderCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultIcon"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultTitle"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSelector"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        themeDescriptions.add(new ThemeDescription(this.emptyView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "emptyListPlaceholder"));
        themeDescriptions.add(new ThemeDescription(this.emptyView, ThemeDescription.FLAG_PROGRESSBAR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "progressCircle"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{ManageChatUserCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{ManageChatUserCell.class}, new String[]{"statusColor"}, (Paint[]) null, (Drawable[]) null, cellDelegate, "windowBackgroundWhiteGrayText"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{ManageChatUserCell.class}, new String[]{"statusOnlineColor"}, (Paint[]) null, (Drawable[]) null, cellDelegate, "windowBackgroundWhiteBlueText"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{ManageChatUserCell.class}, (Paint) null, Theme.avatarDrawables, (ThemeDescription.ThemeDescriptionDelegate) null, "avatar_text"));
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate = cellDelegate;
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, "avatar_backgroundRed"));
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate2 = cellDelegate;
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, "avatar_backgroundOrange"));
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, "avatar_backgroundViolet"));
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, "avatar_backgroundGreen"));
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, "avatar_backgroundCyan"));
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, "avatar_backgroundBlue"));
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, "avatar_backgroundPink"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueHeader"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayIcon"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueButton"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueIcon"));
        return themeDescriptions;
    }

    /* renamed from: lambda$getThemeDescriptions$4$org-telegram-ui-PrivacyUsersActivity  reason: not valid java name */
    public /* synthetic */ void m4385xCLASSNAME() {
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            int count = recyclerListView.getChildCount();
            for (int a = 0; a < count; a++) {
                View child = this.listView.getChildAt(a);
                if (child instanceof ManageChatUserCell) {
                    ((ManageChatUserCell) child).update(0);
                }
            }
        }
    }
}
