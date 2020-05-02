package org.telegram.ui.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.view.View;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Dialog;
import org.telegram.tgnet.TLRPC$RecentMeUrl;
import org.telegram.tgnet.TLRPC$TL_contact;
import org.telegram.ui.Cells.ArchiveHintCell;
import org.telegram.ui.Cells.DialogCell;
import org.telegram.ui.Cells.DialogMeUrlCell;
import org.telegram.ui.Cells.DialogsEmptyCell;
import org.telegram.ui.Cells.UserCell;
import org.telegram.ui.Components.PullForegroundDrawable;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.DialogsActivity;

public class DialogsAdapter extends RecyclerListView.SelectionAdapter {
    private ArchiveHintCell archiveHintCell;
    /* access modifiers changed from: private */
    public int currentAccount = UserConfig.selectedAccount;
    private int currentCount;
    /* access modifiers changed from: private */
    public boolean dialogsListFrozen;
    /* access modifiers changed from: private */
    public int dialogsType;
    /* access modifiers changed from: private */
    public int folderId;
    private boolean hasHints;
    private boolean isOnlySelect;
    private boolean isReordering;
    private long lastSortTime;
    private Context mContext;
    private ArrayList<TLRPC$TL_contact> onlineContacts;
    private long openedDialogId;
    private int prevContactsCount;
    private PullForegroundDrawable pullForegroundDrawable;
    private ArrayList<Long> selectedDialogs;
    private boolean showArchiveHint;

    public DialogsAdapter(Context context, int i, int i2, boolean z, ArrayList<Long> arrayList) {
        this.mContext = context;
        this.dialogsType = i;
        this.folderId = i2;
        this.isOnlySelect = z;
        this.hasHints = i2 == 0 && i == 0 && !z;
        this.selectedDialogs = arrayList;
        if (this.folderId == 1) {
            SharedPreferences globalMainSettings = MessagesController.getGlobalMainSettings();
            this.showArchiveHint = globalMainSettings.getBoolean("archivehint", true);
            globalMainSettings.edit().putBoolean("archivehint", false).commit();
            if (this.showArchiveHint) {
                this.archiveHintCell = new ArchiveHintCell(context);
            }
        }
    }

    public void setOpenedDialogId(long j) {
        this.openedDialogId = j;
    }

    public void onReorderStateChanged(boolean z) {
        this.isReordering = z;
    }

    public int fixPosition(int i) {
        if (this.hasHints) {
            i -= MessagesController.getInstance(this.currentAccount).hintDialogs.size() + 2;
        }
        return this.showArchiveHint ? i - 2 : i;
    }

    public boolean isDataSetChanged() {
        int i = this.currentCount;
        return i != getItemCount() || i == 1;
    }

    public void setDialogsType(int i) {
        this.dialogsType = i;
        notifyDataSetChanged();
    }

    public int getItemCount() {
        int size = DialogsActivity.getDialogsArray(this.currentAccount, this.dialogsType, this.folderId, this.dialogsListFrozen).size();
        int i = this.dialogsType;
        boolean z = false;
        if (i == 7 || i == 8 || size != 0 || (this.folderId == 0 && !MessagesController.getInstance(this.currentAccount).isLoadingDialogs(this.folderId))) {
            int i2 = this.dialogsType;
            int i3 = (i2 == 7 || i2 == 8 ? size != 0 : MessagesController.getInstance(this.currentAccount).isDialogsEndReached(this.folderId) && size != 0) ? size : size + 1;
            if (this.hasHints) {
                i3 += MessagesController.getInstance(this.currentAccount).hintDialogs.size() + 2;
            } else if (this.dialogsType == 0 && size == 0 && this.folderId == 0) {
                if (ContactsController.getInstance(this.currentAccount).contacts.isEmpty() && ContactsController.getInstance(this.currentAccount).isLoadingContacts()) {
                    this.onlineContacts = null;
                    this.currentCount = 0;
                    return 0;
                } else if (!ContactsController.getInstance(this.currentAccount).contacts.isEmpty()) {
                    if (this.onlineContacts == null || this.prevContactsCount != ContactsController.getInstance(this.currentAccount).contacts.size()) {
                        ArrayList<TLRPC$TL_contact> arrayList = new ArrayList<>(ContactsController.getInstance(this.currentAccount).contacts);
                        this.onlineContacts = arrayList;
                        this.prevContactsCount = arrayList.size();
                        int i4 = UserConfig.getInstance(this.currentAccount).clientUserId;
                        int size2 = this.onlineContacts.size();
                        int i5 = 0;
                        while (true) {
                            if (i5 >= size2) {
                                break;
                            } else if (this.onlineContacts.get(i5).user_id == i4) {
                                this.onlineContacts.remove(i5);
                                break;
                            } else {
                                i5++;
                            }
                        }
                        sortOnlineContacts(false);
                    }
                    i3 += this.onlineContacts.size() + 2;
                    z = true;
                }
            }
            if (!z && this.onlineContacts != null) {
                this.onlineContacts = null;
            }
            if (this.folderId == 1 && this.showArchiveHint) {
                i3 += 2;
            }
            if (this.folderId == 0 && size != 0) {
                i3++;
            }
            this.currentCount = i3;
            return i3;
        }
        this.onlineContacts = null;
        if (this.folderId != 1 || !this.showArchiveHint) {
            this.currentCount = 0;
            return 0;
        }
        this.currentCount = 2;
        return 2;
    }

    public TLObject getItem(int i) {
        ArrayList<TLRPC$TL_contact> arrayList = this.onlineContacts;
        if (arrayList != null) {
            int i2 = i - 3;
            if (i2 < 0 || i2 >= arrayList.size()) {
                return null;
            }
            return MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.onlineContacts.get(i2).user_id));
        }
        if (this.showArchiveHint) {
            i -= 2;
        }
        ArrayList<TLRPC$Dialog> dialogsArray = DialogsActivity.getDialogsArray(this.currentAccount, this.dialogsType, this.folderId, this.dialogsListFrozen);
        if (this.hasHints) {
            int size = MessagesController.getInstance(this.currentAccount).hintDialogs.size() + 2;
            if (i < size) {
                return MessagesController.getInstance(this.currentAccount).hintDialogs.get(i - 1);
            }
            i -= size;
        }
        if (i < 0 || i >= dialogsArray.size()) {
            return null;
        }
        return dialogsArray.get(i);
    }

    public void sortOnlineContacts(boolean z) {
        if (this.onlineContacts == null) {
            return;
        }
        if (!z || SystemClock.elapsedRealtime() - this.lastSortTime >= 2000) {
            this.lastSortTime = SystemClock.elapsedRealtime();
            try {
                int currentTime = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
                Collections.sort(this.onlineContacts, new Comparator(currentTime) {
                    private final /* synthetic */ int f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final int compare(Object obj, Object obj2) {
                        return DialogsAdapter.lambda$sortOnlineContacts$0(MessagesController.this, this.f$1, (TLRPC$TL_contact) obj, (TLRPC$TL_contact) obj2);
                    }
                });
                if (z) {
                    notifyDataSetChanged();
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:10:0x002b  */
    /* JADX WARNING: Removed duplicated region for block: B:19:0x003d A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x0048 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x0053 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x005c A[ADDED_TO_REGION] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static /* synthetic */ int lambda$sortOnlineContacts$0(org.telegram.messenger.MessagesController r2, int r3, org.telegram.tgnet.TLRPC$TL_contact r4, org.telegram.tgnet.TLRPC$TL_contact r5) {
        /*
            int r5 = r5.user_id
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            org.telegram.tgnet.TLRPC$User r5 = r2.getUser(r5)
            int r4 = r4.user_id
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
            org.telegram.tgnet.TLRPC$User r2 = r2.getUser(r4)
            r4 = 50000(0xCLASSNAME, float:7.0065E-41)
            r0 = 0
            if (r5 == 0) goto L_0x0028
            boolean r1 = r5.self
            if (r1 == 0) goto L_0x0021
            int r5 = r3 + r4
            goto L_0x0029
        L_0x0021:
            org.telegram.tgnet.TLRPC$UserStatus r5 = r5.status
            if (r5 == 0) goto L_0x0028
            int r5 = r5.expires
            goto L_0x0029
        L_0x0028:
            r5 = 0
        L_0x0029:
            if (r2 == 0) goto L_0x0038
            boolean r1 = r2.self
            if (r1 == 0) goto L_0x0031
            int r3 = r3 + r4
            goto L_0x0039
        L_0x0031:
            org.telegram.tgnet.TLRPC$UserStatus r2 = r2.status
            if (r2 == 0) goto L_0x0038
            int r3 = r2.expires
            goto L_0x0039
        L_0x0038:
            r3 = 0
        L_0x0039:
            r2 = -1
            r4 = 1
            if (r5 <= 0) goto L_0x0046
            if (r3 <= 0) goto L_0x0046
            if (r5 <= r3) goto L_0x0042
            return r4
        L_0x0042:
            if (r5 >= r3) goto L_0x0045
            return r2
        L_0x0045:
            return r0
        L_0x0046:
            if (r5 >= 0) goto L_0x0051
            if (r3 >= 0) goto L_0x0051
            if (r5 <= r3) goto L_0x004d
            return r4
        L_0x004d:
            if (r5 >= r3) goto L_0x0050
            return r2
        L_0x0050:
            return r0
        L_0x0051:
            if (r5 >= 0) goto L_0x0055
            if (r3 > 0) goto L_0x0059
        L_0x0055:
            if (r5 != 0) goto L_0x005a
            if (r3 == 0) goto L_0x005a
        L_0x0059:
            return r2
        L_0x005a:
            if (r3 >= 0) goto L_0x005e
            if (r5 > 0) goto L_0x0062
        L_0x005e:
            if (r3 != 0) goto L_0x0063
            if (r5 == 0) goto L_0x0063
        L_0x0062:
            return r4
        L_0x0063:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Adapters.DialogsAdapter.lambda$sortOnlineContacts$0(org.telegram.messenger.MessagesController, int, org.telegram.tgnet.TLRPC$TL_contact, org.telegram.tgnet.TLRPC$TL_contact):int");
    }

    public void setDialogsListFrozen(boolean z) {
        this.dialogsListFrozen = z;
    }

    public ViewPager getArchiveHintCellPager() {
        ArchiveHintCell archiveHintCell2 = this.archiveHintCell;
        if (archiveHintCell2 != null) {
            return archiveHintCell2.getViewPager();
        }
        return null;
    }

    public void notifyDataSetChanged() {
        this.hasHints = this.folderId == 0 && this.dialogsType == 0 && !this.isOnlySelect && !MessagesController.getInstance(this.currentAccount).hintDialogs.isEmpty();
        super.notifyDataSetChanged();
    }

    public void onViewAttachedToWindow(RecyclerView.ViewHolder viewHolder) {
        View view = viewHolder.itemView;
        if (view instanceof DialogCell) {
            DialogCell dialogCell = (DialogCell) view;
            dialogCell.onReorderStateChanged(this.isReordering, false);
            dialogCell.setDialogIndex(fixPosition(viewHolder.getAdapterPosition()));
            dialogCell.checkCurrentDialogIndex(this.dialogsListFrozen);
            dialogCell.setChecked(this.selectedDialogs.contains(Long.valueOf(dialogCell.getDialogId())), false);
        }
    }

    public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
        int itemViewType = viewHolder.getItemViewType();
        return (itemViewType == 1 || itemViewType == 5 || itemViewType == 3 || itemViewType == 8 || itemViewType == 7 || itemViewType == 9 || itemViewType == 10) ? false : true;
    }

    public /* synthetic */ void lambda$onCreateViewHolder$1$DialogsAdapter(View view) {
        MessagesController.getInstance(this.currentAccount).hintDialogs.clear();
        MessagesController.getGlobalMainSettings().edit().remove("installReferer").commit();
        notifyDataSetChanged();
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v2, resolved type: org.telegram.ui.Adapters.DialogsAdapter$2} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v5, resolved type: org.telegram.ui.Adapters.DialogsAdapter$2} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v6, resolved type: org.telegram.ui.Adapters.DialogsAdapter$2} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v17, resolved type: org.telegram.ui.Cells.DialogCell} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v18, resolved type: org.telegram.ui.Cells.LoadingCell} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v19, resolved type: org.telegram.ui.Cells.HeaderCell} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v8, resolved type: org.telegram.ui.Adapters.DialogsAdapter$2} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v20, resolved type: org.telegram.ui.Cells.DialogMeUrlCell} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v21, resolved type: org.telegram.ui.Cells.DialogsEmptyCell} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v22, resolved type: org.telegram.ui.Cells.UserCell} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v23, resolved type: org.telegram.ui.Cells.HeaderCell} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v9, resolved type: org.telegram.ui.Cells.ShadowSectionCell} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v24, resolved type: org.telegram.ui.Cells.ArchiveHintCell} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v25, resolved type: org.telegram.ui.Cells.ArchiveHintCell} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v26, resolved type: org.telegram.ui.Adapters.DialogsAdapter$2} */
    /* JADX WARNING: type inference failed for: r4v6, types: [android.widget.FrameLayout, org.telegram.ui.Adapters.DialogsAdapter$1] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public androidx.recyclerview.widget.RecyclerView.ViewHolder onCreateViewHolder(android.view.ViewGroup r13, int r14) {
        /*
            r12 = this;
            java.lang.String r13 = "windowBackgroundGrayShadow"
            r0 = 2131165412(0x7var_e4, float:1.794504E38)
            java.lang.String r1 = "windowBackgroundGray"
            r2 = 5
            r3 = -1
            r4 = 0
            r5 = 1
            switch(r14) {
                case 0: goto L_0x0120;
                case 1: goto L_0x0118;
                case 2: goto L_0x00ac;
                case 3: goto L_0x0082;
                case 4: goto L_0x0079;
                case 5: goto L_0x0070;
                case 6: goto L_0x0065;
                case 7: goto L_0x0050;
                case 8: goto L_0x002e;
                case 9: goto L_0x0017;
                default: goto L_0x000e;
            }
        L_0x000e:
            org.telegram.ui.Adapters.DialogsAdapter$2 r13 = new org.telegram.ui.Adapters.DialogsAdapter$2
            android.content.Context r0 = r12.mContext
            r13.<init>(r0)
            goto L_0x012c
        L_0x0017:
            org.telegram.ui.Cells.ArchiveHintCell r13 = r12.archiveHintCell
            android.view.ViewParent r0 = r13.getParent()
            if (r0 == 0) goto L_0x012c
            org.telegram.ui.Cells.ArchiveHintCell r0 = r12.archiveHintCell
            android.view.ViewParent r0 = r0.getParent()
            android.view.ViewGroup r0 = (android.view.ViewGroup) r0
            org.telegram.ui.Cells.ArchiveHintCell r1 = r12.archiveHintCell
            r0.removeView(r1)
            goto L_0x012c
        L_0x002e:
            org.telegram.ui.Cells.ShadowSectionCell r4 = new org.telegram.ui.Cells.ShadowSectionCell
            android.content.Context r6 = r12.mContext
            r4.<init>(r6)
            android.content.Context r6 = r12.mContext
            android.graphics.drawable.Drawable r13 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r6, (int) r0, (java.lang.String) r13)
            org.telegram.ui.Components.CombinedDrawable r0 = new org.telegram.ui.Components.CombinedDrawable
            android.graphics.drawable.ColorDrawable r6 = new android.graphics.drawable.ColorDrawable
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r6.<init>(r1)
            r0.<init>(r6, r13)
            r0.setFullsize(r5)
            r4.setBackgroundDrawable(r0)
            goto L_0x00a9
        L_0x0050:
            org.telegram.ui.Cells.HeaderCell r13 = new org.telegram.ui.Cells.HeaderCell
            android.content.Context r0 = r12.mContext
            r13.<init>(r0)
            r0 = 2131627467(0x7f0e0dcb, float:1.88822E38)
            java.lang.String r1 = "YourContacts"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r13.setText(r0)
            goto L_0x012c
        L_0x0065:
            org.telegram.ui.Cells.UserCell r13 = new org.telegram.ui.Cells.UserCell
            android.content.Context r0 = r12.mContext
            r1 = 8
            r13.<init>(r0, r1, r4, r4)
            goto L_0x012c
        L_0x0070:
            org.telegram.ui.Cells.DialogsEmptyCell r13 = new org.telegram.ui.Cells.DialogsEmptyCell
            android.content.Context r0 = r12.mContext
            r13.<init>(r0)
            goto L_0x012c
        L_0x0079:
            org.telegram.ui.Cells.DialogMeUrlCell r13 = new org.telegram.ui.Cells.DialogMeUrlCell
            android.content.Context r0 = r12.mContext
            r13.<init>(r0)
            goto L_0x012c
        L_0x0082:
            org.telegram.ui.Adapters.DialogsAdapter$1 r4 = new org.telegram.ui.Adapters.DialogsAdapter$1
            android.content.Context r5 = r12.mContext
            r4.<init>(r12, r5)
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r4.setBackgroundColor(r1)
            android.view.View r1 = new android.view.View
            android.content.Context r5 = r12.mContext
            r1.<init>(r5)
            android.content.Context r5 = r12.mContext
            android.graphics.drawable.Drawable r13 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r5, (int) r0, (java.lang.String) r13)
            r1.setBackgroundDrawable(r13)
            r13 = -1082130432(0xffffffffbvar_, float:-1.0)
            android.widget.FrameLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createFrame(r3, r13)
            r4.addView(r1, r13)
        L_0x00a9:
            r13 = r4
            goto L_0x012c
        L_0x00ac:
            org.telegram.ui.Cells.HeaderCell r13 = new org.telegram.ui.Cells.HeaderCell
            android.content.Context r0 = r12.mContext
            r13.<init>(r0)
            r0 = 2131626508(0x7f0e0a0c, float:1.8880254E38)
            java.lang.String r1 = "RecentlyViewed"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r13.setText(r0)
            android.widget.TextView r0 = new android.widget.TextView
            android.content.Context r1 = r12.mContext
            r0.<init>(r1)
            r1 = 1097859072(0x41700000, float:15.0)
            r0.setTextSize(r5, r1)
            java.lang.String r1 = "fonts/rmedium.ttf"
            android.graphics.Typeface r1 = org.telegram.messenger.AndroidUtilities.getTypeface(r1)
            r0.setTypeface(r1)
            java.lang.String r1 = "windowBackgroundWhiteBlueHeader"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.setTextColor(r1)
            r1 = 2131626509(0x7f0e0a0d, float:1.8880256E38)
            java.lang.String r4 = "RecentlyViewedHide"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r4, r1)
            r0.setText(r1)
            boolean r1 = org.telegram.messenger.LocaleController.isRTL
            r4 = 3
            if (r1 == 0) goto L_0x00f0
            r1 = 3
            goto L_0x00f1
        L_0x00f0:
            r1 = 5
        L_0x00f1:
            r1 = r1 | 16
            r0.setGravity(r1)
            r5 = -1
            r6 = -1082130432(0xffffffffbvar_, float:-1.0)
            boolean r1 = org.telegram.messenger.LocaleController.isRTL
            if (r1 == 0) goto L_0x00fe
            goto L_0x00ff
        L_0x00fe:
            r4 = 5
        L_0x00ff:
            r7 = r4 | 48
            r8 = 1099431936(0x41880000, float:17.0)
            r9 = 1097859072(0x41700000, float:15.0)
            r10 = 1099431936(0x41880000, float:17.0)
            r11 = 0
            android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame(r5, r6, r7, r8, r9, r10, r11)
            r13.addView(r0, r1)
            org.telegram.ui.Adapters.-$$Lambda$DialogsAdapter$DWIibGxp-Clk-7MRxEqoD1mmox4 r1 = new org.telegram.ui.Adapters.-$$Lambda$DialogsAdapter$DWIibGxp-Clk-7MRxEqoD1mmox4
            r1.<init>()
            r0.setOnClickListener(r1)
            goto L_0x012c
        L_0x0118:
            org.telegram.ui.Cells.LoadingCell r13 = new org.telegram.ui.Cells.LoadingCell
            android.content.Context r0 = r12.mContext
            r13.<init>(r0)
            goto L_0x012c
        L_0x0120:
            org.telegram.ui.Cells.DialogCell r13 = new org.telegram.ui.Cells.DialogCell
            android.content.Context r0 = r12.mContext
            r13.<init>(r0, r5, r4)
            org.telegram.ui.Components.PullForegroundDrawable r0 = r12.pullForegroundDrawable
            r13.setArchivedPullAnimation(r0)
        L_0x012c:
            androidx.recyclerview.widget.RecyclerView$LayoutParams r0 = new androidx.recyclerview.widget.RecyclerView$LayoutParams
            if (r14 != r2) goto L_0x0132
            r14 = -1
            goto L_0x0133
        L_0x0132:
            r14 = -2
        L_0x0133:
            r0.<init>((int) r3, (int) r14)
            r13.setLayoutParams(r0)
            org.telegram.ui.Components.RecyclerListView$Holder r14 = new org.telegram.ui.Components.RecyclerListView$Holder
            r14.<init>(r13)
            return r14
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Adapters.DialogsAdapter.onCreateViewHolder(android.view.ViewGroup, int):androidx.recyclerview.widget.RecyclerView$ViewHolder");
    }

    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        int itemViewType = viewHolder.getItemViewType();
        int i2 = 0;
        boolean z = true;
        if (itemViewType == 0) {
            DialogCell dialogCell = (DialogCell) viewHolder.itemView;
            TLRPC$Dialog tLRPC$Dialog = (TLRPC$Dialog) getItem(i);
            TLRPC$Dialog tLRPC$Dialog2 = (TLRPC$Dialog) getItem(i + 1);
            if (this.folderId == 0) {
                dialogCell.useSeparator = i != getItemCount() - 2;
            } else {
                dialogCell.useSeparator = i != getItemCount() - 1;
            }
            dialogCell.fullSeparator = tLRPC$Dialog.pinned && tLRPC$Dialog2 != null && !tLRPC$Dialog2.pinned;
            if (this.dialogsType == 0 && AndroidUtilities.isTablet()) {
                if (tLRPC$Dialog.id != this.openedDialogId) {
                    z = false;
                }
                dialogCell.setDialogSelected(z);
            }
            dialogCell.setChecked(this.selectedDialogs.contains(Long.valueOf(tLRPC$Dialog.id)), false);
            dialogCell.setDialog(tLRPC$Dialog, this.dialogsType, this.folderId);
        } else if (itemViewType == 4) {
            ((DialogMeUrlCell) viewHolder.itemView).setRecentMeUrl((TLRPC$RecentMeUrl) getItem(i));
        } else if (itemViewType == 5) {
            DialogsEmptyCell dialogsEmptyCell = (DialogsEmptyCell) viewHolder.itemView;
            int i3 = this.dialogsType;
            if (i3 != 7 && i3 != 8) {
                if (this.onlineContacts != null) {
                    i2 = 1;
                }
                dialogsEmptyCell.setType(i2);
            } else if (MessagesController.getInstance(this.currentAccount).isDialogsEndReached(this.folderId)) {
                dialogsEmptyCell.setType(2);
            } else {
                dialogsEmptyCell.setType(3);
            }
        } else if (itemViewType == 6) {
            ((UserCell) viewHolder.itemView).setData(MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.onlineContacts.get(i - 3).user_id)), (CharSequence) null, (CharSequence) null, 0);
        }
    }

    public int getItemViewType(int i) {
        if (this.onlineContacts == null) {
            if (this.hasHints) {
                int size = MessagesController.getInstance(this.currentAccount).hintDialogs.size();
                int i2 = size + 2;
                if (i >= i2) {
                    i -= i2;
                } else if (i == 0) {
                    return 2;
                } else {
                    return i == size + 1 ? 3 : 4;
                }
            } else if (this.showArchiveHint) {
                if (i == 0) {
                    return 9;
                }
                if (i == 1) {
                    return 8;
                }
                i -= 2;
            }
            int size2 = DialogsActivity.getDialogsArray(this.currentAccount, this.dialogsType, this.folderId, this.dialogsListFrozen).size();
            if (i == size2) {
                int i3 = this.dialogsType;
                if (i3 != 7 && i3 != 8 && !MessagesController.getInstance(this.currentAccount).isDialogsEndReached(this.folderId)) {
                    return 1;
                }
                if (size2 == 0) {
                    return 5;
                }
                return 10;
            } else if (i > size2) {
                return 10;
            } else {
                return 0;
            }
        } else if (i == 0) {
            return 5;
        } else {
            if (i == 1) {
                return 8;
            }
            return i == 2 ? 7 : 6;
        }
    }

    public void notifyItemMoved(int i, int i2) {
        char c = 0;
        ArrayList<TLRPC$Dialog> dialogsArray = DialogsActivity.getDialogsArray(this.currentAccount, this.dialogsType, this.folderId, false);
        int fixPosition = fixPosition(i);
        int fixPosition2 = fixPosition(i2);
        TLRPC$Dialog tLRPC$Dialog = dialogsArray.get(fixPosition);
        TLRPC$Dialog tLRPC$Dialog2 = dialogsArray.get(fixPosition2);
        int i3 = this.dialogsType;
        if (i3 == 7 || i3 == 8) {
            MessagesController.DialogFilter[] dialogFilterArr = MessagesController.getInstance(this.currentAccount).selectedDialogFilter;
            if (this.dialogsType == 8) {
                c = 1;
            }
            MessagesController.DialogFilter dialogFilter = dialogFilterArr[c];
            int intValue = dialogFilter.pinnedDialogs.get(tLRPC$Dialog.id).intValue();
            dialogFilter.pinnedDialogs.put(tLRPC$Dialog.id, Integer.valueOf(dialogFilter.pinnedDialogs.get(tLRPC$Dialog2.id).intValue()));
            dialogFilter.pinnedDialogs.put(tLRPC$Dialog2.id, Integer.valueOf(intValue));
        } else {
            int i4 = tLRPC$Dialog.pinnedNum;
            tLRPC$Dialog.pinnedNum = tLRPC$Dialog2.pinnedNum;
            tLRPC$Dialog2.pinnedNum = i4;
        }
        Collections.swap(dialogsArray, fixPosition, fixPosition2);
        super.notifyItemMoved(i, i2);
    }

    public void setArchivedPullDrawable(PullForegroundDrawable pullForegroundDrawable2) {
        this.pullForegroundDrawable = pullForegroundDrawable2;
    }
}
