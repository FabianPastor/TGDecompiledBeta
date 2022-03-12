package org.telegram.ui.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.SystemClock;
import android.view.View;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Dialog;
import org.telegram.tgnet.TLRPC$RecentMeUrl;
import org.telegram.tgnet.TLRPC$TL_contact;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.ArchiveHintCell;
import org.telegram.ui.Cells.DialogCell;
import org.telegram.ui.Cells.DialogMeUrlCell;
import org.telegram.ui.Cells.DialogsEmptyCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.UserCell;
import org.telegram.ui.Components.BlurredRecyclerView;
import org.telegram.ui.Components.PullForegroundDrawable;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.DialogsActivity;

public class DialogsAdapter extends RecyclerListView.SelectionAdapter {
    private ArchiveHintCell archiveHintCell;
    /* access modifiers changed from: private */
    public Drawable arrowDrawable;
    /* access modifiers changed from: private */
    public int currentAccount;
    private int currentCount;
    private int dialogsCount;
    /* access modifiers changed from: private */
    public boolean dialogsListFrozen;
    /* access modifiers changed from: private */
    public int dialogsType;
    /* access modifiers changed from: private */
    public int folderId;
    private boolean forceShowEmptyCell;
    private boolean forceUpdatingContacts;
    private boolean hasHints;
    private boolean isOnlySelect;
    private boolean isReordering;
    public int lastDialogsEmptyType = -1;
    private long lastSortTime;
    private Context mContext;
    /* access modifiers changed from: private */
    public ArrayList<TLRPC$TL_contact> onlineContacts;
    private long openedDialogId;
    /* access modifiers changed from: private */
    public DialogsActivity parentFragment;
    private DialogsPreloader preloader;
    private int prevContactsCount;
    private int prevDialogsCount;
    private PullForegroundDrawable pullForegroundDrawable;
    private ArrayList<Long> selectedDialogs;
    private boolean showArchiveHint;

    public DialogsAdapter(DialogsActivity dialogsActivity, Context context, int i, int i2, boolean z, ArrayList<Long> arrayList, int i3) {
        this.mContext = context;
        this.parentFragment = dialogsActivity;
        this.dialogsType = i;
        this.folderId = i2;
        this.isOnlySelect = z;
        this.hasHints = i2 == 0 && i == 0 && !z;
        this.selectedDialogs = arrayList;
        this.currentAccount = i3;
        if (i2 == 1) {
            SharedPreferences globalMainSettings = MessagesController.getGlobalMainSettings();
            this.showArchiveHint = globalMainSettings.getBoolean("archivehint", true);
            globalMainSettings.edit().putBoolean("archivehint", false).commit();
        }
        if (i2 == 0) {
            this.preloader = new DialogsPreloader();
        }
    }

    public void setOpenedDialogId(long j) {
        this.openedDialogId = j;
    }

    public void onReorderStateChanged(boolean z) {
        this.isReordering = z;
    }

    public int fixPosition(int i) {
        int i2;
        if (this.hasHints) {
            i -= MessagesController.getInstance(this.currentAccount).hintDialogs.size() + 2;
        }
        if (this.showArchiveHint || (i2 = this.dialogsType) == 11 || i2 == 13) {
            return i - 2;
        }
        return i2 == 12 ? i - 1 : i;
    }

    public boolean isDataSetChanged() {
        int i = this.currentCount;
        return i != getItemCount() || i == 1;
    }

    public void setDialogsType(int i) {
        this.dialogsType = i;
        notifyDataSetChanged();
    }

    public int getDialogsType() {
        return this.dialogsType;
    }

    public int getDialogsCount() {
        return this.dialogsCount;
    }

    public int getItemCount() {
        int i;
        int i2;
        int i3;
        int i4;
        MessagesController instance = MessagesController.getInstance(this.currentAccount);
        int size = this.parentFragment.getDialogsArray(this.currentAccount, this.dialogsType, this.folderId, this.dialogsListFrozen).size();
        this.dialogsCount = size;
        boolean z = false;
        boolean z2 = true;
        if (this.forceUpdatingContacts || this.forceShowEmptyCell || (i3 = this.dialogsType) == 7 || i3 == 8 || i3 == 11 || size != 0 || ((i4 = this.folderId) == 0 && !instance.isLoadingDialogs(i4) && MessagesController.getInstance(this.currentAccount).isDialogsEndReached(this.folderId))) {
            int i5 = this.dialogsCount;
            int i6 = this.dialogsType;
            if (i6 == 7 || i6 == 8 ? i5 == 0 : !(instance.isDialogsEndReached(this.folderId) && this.dialogsCount != 0)) {
                i5++;
            }
            if (this.hasHints) {
                i5 += instance.hintDialogs.size() + 2;
            } else if (this.dialogsType == 0 && this.dialogsCount <= 10 && (i2 = this.folderId) == 0 && instance.isDialogsEndReached(i2)) {
                if (ContactsController.getInstance(this.currentAccount).contacts.isEmpty() && !ContactsController.getInstance(this.currentAccount).doneLoadingContacts && !this.forceUpdatingContacts) {
                    this.onlineContacts = null;
                    if (BuildVars.LOGS_ENABLED) {
                        StringBuilder sb = new StringBuilder();
                        sb.append("DialogsAdapter loadingContacts=");
                        if (!ContactsController.getInstance(this.currentAccount).contacts.isEmpty() || ContactsController.getInstance(this.currentAccount).doneLoadingContacts) {
                            z2 = false;
                        }
                        sb.append(z2);
                        sb.append("dialogsCount=");
                        sb.append(this.dialogsCount);
                        sb.append(" dialogsType=");
                        sb.append(this.dialogsType);
                        FileLog.d(sb.toString());
                    }
                    this.currentCount = 0;
                    return 0;
                } else if (ContactsController.getInstance(this.currentAccount).doneLoadingContacts && !ContactsController.getInstance(this.currentAccount).contacts.isEmpty()) {
                    if (!(this.onlineContacts != null && this.prevDialogsCount == this.dialogsCount && this.prevContactsCount == ContactsController.getInstance(this.currentAccount).contacts.size())) {
                        ArrayList<TLRPC$TL_contact> arrayList = new ArrayList<>(ContactsController.getInstance(this.currentAccount).contacts);
                        this.onlineContacts = arrayList;
                        this.prevContactsCount = arrayList.size();
                        this.prevDialogsCount = instance.dialogs_dict.size();
                        long j = UserConfig.getInstance(this.currentAccount).clientUserId;
                        int size2 = this.onlineContacts.size();
                        int i7 = 0;
                        while (i7 < size2) {
                            long j2 = this.onlineContacts.get(i7).user_id;
                            if (j2 == j || instance.dialogs_dict.get(j2) != null) {
                                this.onlineContacts.remove(i7);
                                i7--;
                                size2--;
                            }
                            i7++;
                        }
                        if (this.onlineContacts.isEmpty()) {
                            this.onlineContacts = null;
                        }
                        sortOnlineContacts(false);
                        if (this.parentFragment.getContactsAlpha() == 0.0f) {
                            registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                                public void onChanged() {
                                    DialogsAdapter.this.parentFragment.setContactsAlpha(0.0f);
                                    DialogsAdapter.this.parentFragment.animateContactsAlpha(1.0f);
                                    DialogsAdapter.this.unregisterAdapterDataObserver(this);
                                }
                            });
                        }
                    }
                    ArrayList<TLRPC$TL_contact> arrayList2 = this.onlineContacts;
                    if (arrayList2 != null) {
                        i5 += arrayList2.size() + 2;
                        z = true;
                    }
                }
            }
            int i8 = this.folderId;
            if (i8 == 0 && !z && this.forceUpdatingContacts) {
                i5 += 3;
            }
            if (i8 == 0 && this.onlineContacts != null && !z) {
                this.onlineContacts = null;
            }
            if (i8 == 1 && this.showArchiveHint) {
                i5 += 2;
            }
            if (i8 == 0 && (i = this.dialogsCount) != 0) {
                i5++;
                if (i > 10 && this.dialogsType == 0) {
                    i5++;
                }
            }
            int i9 = this.dialogsType;
            if (i9 == 11 || i9 == 13) {
                i5 += 2;
            } else if (i9 == 12) {
                i5++;
            }
            this.currentCount = i5;
            return i5;
        }
        this.onlineContacts = null;
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("DialogsAdapter dialogsCount=" + this.dialogsCount + " dialogsType=" + this.dialogsType + " isLoadingDialogs=" + instance.isLoadingDialogs(this.folderId) + " isDialogsEndReached=" + MessagesController.getInstance(this.currentAccount).isDialogsEndReached(this.folderId));
        }
        if (this.folderId != 1 || !this.showArchiveHint) {
            this.currentCount = 0;
            return 0;
        }
        this.currentCount = 2;
        return 2;
    }

    public TLObject getItem(int i) {
        int i2;
        int i3;
        ArrayList<TLRPC$TL_contact> arrayList = this.onlineContacts;
        if (arrayList == null || ((i3 = this.dialogsCount) != 0 && i < i3)) {
            if (this.showArchiveHint || (i2 = this.dialogsType) == 11 || i2 == 13) {
                i -= 2;
            } else if (i2 == 12) {
                i--;
            }
            ArrayList<TLRPC$Dialog> dialogsArray = this.parentFragment.getDialogsArray(this.currentAccount, this.dialogsType, this.folderId, this.dialogsListFrozen);
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
        int i4 = i3 == 0 ? i - 3 : i - (i3 + 2);
        if (i4 < 0 || i4 >= arrayList.size()) {
            return null;
        }
        return MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(this.onlineContacts.get(i4).user_id));
    }

    public void sortOnlineContacts(boolean z) {
        if (this.onlineContacts == null) {
            return;
        }
        if (!z || SystemClock.elapsedRealtime() - this.lastSortTime >= 2000) {
            this.lastSortTime = SystemClock.elapsedRealtime();
            try {
                int currentTime = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
                Collections.sort(this.onlineContacts, new DialogsAdapter$$ExternalSyntheticLambda3(MessagesController.getInstance(this.currentAccount), currentTime));
                if (z) {
                    notifyDataSetChanged();
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:10:0x002b  */
    /* JADX WARNING: Removed duplicated region for block: B:19:0x003d A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x0048 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x0053 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x005c A[ADDED_TO_REGION] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static /* synthetic */ int lambda$sortOnlineContacts$0(org.telegram.messenger.MessagesController r2, int r3, org.telegram.tgnet.TLRPC$TL_contact r4, org.telegram.tgnet.TLRPC$TL_contact r5) {
        /*
            long r0 = r5.user_id
            java.lang.Long r5 = java.lang.Long.valueOf(r0)
            org.telegram.tgnet.TLRPC$User r5 = r2.getUser(r5)
            long r0 = r4.user_id
            java.lang.Long r4 = java.lang.Long.valueOf(r0)
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
            if (r3 < 0) goto L_0x0060
            if (r5 == 0) goto L_0x005f
            goto L_0x0060
        L_0x005f:
            return r0
        L_0x0060:
            return r4
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

    public void updateHasHints() {
        this.hasHints = this.folderId == 0 && this.dialogsType == 0 && !this.isOnlySelect && !MessagesController.getInstance(this.currentAccount).hintDialogs.isEmpty();
    }

    public void notifyDataSetChanged() {
        updateHasHints();
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
        return (itemViewType == 1 || itemViewType == 5 || itemViewType == 3 || itemViewType == 8 || itemViewType == 7 || itemViewType == 9 || itemViewType == 10 || itemViewType == 11 || itemViewType == 13) ? false : true;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreateViewHolder$1(View view) {
        MessagesController.getInstance(this.currentAccount).hintDialogs.clear();
        MessagesController.getGlobalMainSettings().edit().remove("installReferer").commit();
        notifyDataSetChanged();
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v1, resolved type: org.telegram.ui.Cells.DialogCell} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v3, resolved type: org.telegram.ui.Components.FlickerLoadingView} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v4, resolved type: org.telegram.ui.Cells.HeaderCell} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v8, resolved type: org.telegram.ui.Cells.DialogCell} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v10, resolved type: org.telegram.ui.Cells.ArchiveHintCell} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v8, resolved type: org.telegram.ui.Cells.DialogCell} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v14, resolved type: org.telegram.ui.Cells.DialogCell} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v15, resolved type: org.telegram.ui.Cells.DialogCell} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v16, resolved type: org.telegram.ui.Cells.DialogCell} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v17, resolved type: org.telegram.ui.Cells.DialogCell} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v18, resolved type: org.telegram.ui.Cells.DialogCell} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v19, resolved type: org.telegram.ui.Cells.DialogCell} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v20, resolved type: org.telegram.ui.Cells.DialogCell} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v21, resolved type: org.telegram.ui.Cells.DialogCell} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v22, resolved type: org.telegram.ui.Cells.DialogCell} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v23, resolved type: org.telegram.ui.Cells.DialogCell} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v24, resolved type: org.telegram.ui.Cells.DialogCell} */
    /* JADX WARNING: type inference failed for: r5v5, types: [android.widget.FrameLayout, org.telegram.ui.Adapters.DialogsAdapter$2] */
    /* JADX WARNING: type inference failed for: r5v6, types: [org.telegram.ui.Cells.DialogMeUrlCell] */
    /* JADX WARNING: type inference failed for: r5v7, types: [org.telegram.ui.Cells.DialogsEmptyCell] */
    /* JADX WARNING: type inference failed for: r1v21 */
    /* JADX WARNING: type inference failed for: r1v22, types: [org.telegram.ui.Cells.UserCell] */
    /* JADX WARNING: type inference failed for: r1v23, types: [android.view.View, org.telegram.ui.Cells.HeaderCell] */
    /* JADX WARNING: type inference failed for: r5v9, types: [org.telegram.ui.Cells.ShadowSectionCell, android.view.View] */
    /* JADX WARNING: type inference failed for: r5v11, types: [org.telegram.ui.Adapters.DialogsAdapter$LastEmptyView] */
    /* JADX WARNING: type inference failed for: r5v12, types: [android.view.View, org.telegram.ui.Adapters.DialogsAdapter$3] */
    /* JADX WARNING: type inference failed for: r5v13, types: [org.telegram.ui.Cells.TextCell] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Unknown variable types count: 4 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public androidx.recyclerview.widget.RecyclerView.ViewHolder onCreateViewHolder(android.view.ViewGroup r14, int r15) {
        /*
            r13 = this;
            r14 = 5
            r0 = -1
            java.lang.String r1 = "windowBackgroundGrayShadow"
            r2 = 2131165471(0x7var_f, float:1.794516E38)
            java.lang.String r3 = "windowBackgroundGray"
            r4 = 1
            r5 = 0
            switch(r15) {
                case 0: goto L_0x0162;
                case 1: goto L_0x0135;
                case 2: goto L_0x00c9;
                case 3: goto L_0x00a0;
                case 4: goto L_0x0097;
                case 5: goto L_0x008e;
                case 6: goto L_0x0082;
                case 7: goto L_0x0071;
                case 8: goto L_0x004e;
                case 9: goto L_0x0043;
                case 10: goto L_0x003a;
                case 11: goto L_0x0017;
                case 12: goto L_0x000e;
                case 13: goto L_0x0135;
                default: goto L_0x000e;
            }
        L_0x000e:
            org.telegram.ui.Cells.TextCell r5 = new org.telegram.ui.Cells.TextCell
            android.content.Context r1 = r13.mContext
            r5.<init>(r1)
            goto L_0x017b
        L_0x0017:
            org.telegram.ui.Adapters.DialogsAdapter$3 r5 = new org.telegram.ui.Adapters.DialogsAdapter$3
            android.content.Context r6 = r13.mContext
            r5.<init>(r6)
            android.content.Context r6 = r13.mContext
            android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r6, (int) r2, (java.lang.String) r1)
            org.telegram.ui.Components.CombinedDrawable r2 = new org.telegram.ui.Components.CombinedDrawable
            android.graphics.drawable.ColorDrawable r6 = new android.graphics.drawable.ColorDrawable
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            r6.<init>(r3)
            r2.<init>(r6, r1)
            r2.setFullsize(r4)
            r5.setBackgroundDrawable(r2)
            goto L_0x017b
        L_0x003a:
            org.telegram.ui.Adapters.DialogsAdapter$LastEmptyView r5 = new org.telegram.ui.Adapters.DialogsAdapter$LastEmptyView
            android.content.Context r1 = r13.mContext
            r5.<init>(r1)
            goto L_0x017b
        L_0x0043:
            org.telegram.ui.Cells.ArchiveHintCell r5 = new org.telegram.ui.Cells.ArchiveHintCell
            android.content.Context r1 = r13.mContext
            r5.<init>(r1)
            r13.archiveHintCell = r5
            goto L_0x017b
        L_0x004e:
            org.telegram.ui.Cells.ShadowSectionCell r5 = new org.telegram.ui.Cells.ShadowSectionCell
            android.content.Context r6 = r13.mContext
            r5.<init>(r6)
            android.content.Context r6 = r13.mContext
            android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r6, (int) r2, (java.lang.String) r1)
            org.telegram.ui.Components.CombinedDrawable r2 = new org.telegram.ui.Components.CombinedDrawable
            android.graphics.drawable.ColorDrawable r6 = new android.graphics.drawable.ColorDrawable
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            r6.<init>(r3)
            r2.<init>(r6, r1)
            r2.setFullsize(r4)
            r5.setBackgroundDrawable(r2)
            goto L_0x017b
        L_0x0071:
            org.telegram.ui.Cells.HeaderCell r1 = new org.telegram.ui.Cells.HeaderCell
            android.content.Context r2 = r13.mContext
            r1.<init>(r2)
            r2 = 1094713344(0x41400000, float:12.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r1.setPadding(r5, r5, r5, r2)
            goto L_0x008b
        L_0x0082:
            org.telegram.ui.Cells.UserCell r1 = new org.telegram.ui.Cells.UserCell
            android.content.Context r2 = r13.mContext
            r3 = 8
            r1.<init>(r2, r3, r5, r5)
        L_0x008b:
            r5 = r1
            goto L_0x017b
        L_0x008e:
            org.telegram.ui.Cells.DialogsEmptyCell r5 = new org.telegram.ui.Cells.DialogsEmptyCell
            android.content.Context r1 = r13.mContext
            r5.<init>(r1)
            goto L_0x017b
        L_0x0097:
            org.telegram.ui.Cells.DialogMeUrlCell r5 = new org.telegram.ui.Cells.DialogMeUrlCell
            android.content.Context r1 = r13.mContext
            r5.<init>(r1)
            goto L_0x017b
        L_0x00a0:
            org.telegram.ui.Adapters.DialogsAdapter$2 r5 = new org.telegram.ui.Adapters.DialogsAdapter$2
            android.content.Context r4 = r13.mContext
            r5.<init>(r13, r4)
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            r5.setBackgroundColor(r3)
            android.view.View r3 = new android.view.View
            android.content.Context r4 = r13.mContext
            r3.<init>(r4)
            android.content.Context r4 = r13.mContext
            android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r4, (int) r2, (java.lang.String) r1)
            r3.setBackgroundDrawable(r1)
            r1 = -1082130432(0xffffffffbvar_, float:-1.0)
            android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame(r0, r1)
            r5.addView(r3, r1)
            goto L_0x017b
        L_0x00c9:
            org.telegram.ui.Cells.HeaderCell r5 = new org.telegram.ui.Cells.HeaderCell
            android.content.Context r1 = r13.mContext
            r5.<init>(r1)
            r1 = 2131627580(0x7f0e0e3c, float:1.8882428E38)
            java.lang.String r2 = "RecentlyViewed"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r5.setText(r1)
            android.widget.TextView r1 = new android.widget.TextView
            android.content.Context r2 = r13.mContext
            r1.<init>(r2)
            r2 = 1097859072(0x41700000, float:15.0)
            r1.setTextSize(r4, r2)
            java.lang.String r2 = "fonts/rmedium.ttf"
            android.graphics.Typeface r2 = org.telegram.messenger.AndroidUtilities.getTypeface(r2)
            r1.setTypeface(r2)
            java.lang.String r2 = "windowBackgroundWhiteBlueHeader"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r1.setTextColor(r2)
            r2 = 2131627581(0x7f0e0e3d, float:1.888243E38)
            java.lang.String r3 = "RecentlyViewedHide"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            boolean r2 = org.telegram.messenger.LocaleController.isRTL
            r3 = 3
            if (r2 == 0) goto L_0x010d
            r2 = 3
            goto L_0x010e
        L_0x010d:
            r2 = 5
        L_0x010e:
            r2 = r2 | 16
            r1.setGravity(r2)
            r6 = -1
            r7 = -1082130432(0xffffffffbvar_, float:-1.0)
            boolean r2 = org.telegram.messenger.LocaleController.isRTL
            if (r2 == 0) goto L_0x011b
            goto L_0x011c
        L_0x011b:
            r3 = 5
        L_0x011c:
            r8 = r3 | 48
            r9 = 1099431936(0x41880000, float:17.0)
            r10 = 1097859072(0x41700000, float:15.0)
            r11 = 1099431936(0x41880000, float:17.0)
            r12 = 0
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r6, r7, r8, r9, r10, r11, r12)
            r5.addView(r1, r2)
            org.telegram.ui.Adapters.DialogsAdapter$$ExternalSyntheticLambda0 r2 = new org.telegram.ui.Adapters.DialogsAdapter$$ExternalSyntheticLambda0
            r2.<init>(r13)
            r1.setOnClickListener(r2)
            goto L_0x017b
        L_0x0135:
            org.telegram.ui.Components.FlickerLoadingView r5 = new org.telegram.ui.Components.FlickerLoadingView
            android.content.Context r1 = r13.mContext
            r5.<init>(r1)
            r5.setIsSingleCell(r4)
            r1 = 13
            if (r15 != r1) goto L_0x0146
            r2 = 18
            goto L_0x0147
        L_0x0146:
            r2 = 7
        L_0x0147:
            r5.setViewType(r2)
            if (r15 != r1) goto L_0x017b
            android.graphics.Point r1 = org.telegram.messenger.AndroidUtilities.displaySize
            int r1 = r1.y
            float r1 = (float) r1
            r2 = 1056964608(0x3var_, float:0.5)
            float r1 = r1 * r2
            r2 = 1115684864(0x42800000, float:64.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r2 = (float) r2
            float r1 = r1 / r2
            int r1 = (int) r1
            r5.setItemsCount(r1)
            goto L_0x017b
        L_0x0162:
            org.telegram.ui.Cells.DialogCell r5 = new org.telegram.ui.Cells.DialogCell
            org.telegram.ui.DialogsActivity r7 = r13.parentFragment
            android.content.Context r8 = r13.mContext
            r9 = 1
            r10 = 0
            int r11 = r13.currentAccount
            r12 = 0
            r6 = r5
            r6.<init>(r7, r8, r9, r10, r11, r12)
            org.telegram.ui.Components.PullForegroundDrawable r1 = r13.pullForegroundDrawable
            r5.setArchivedPullAnimation(r1)
            org.telegram.ui.Adapters.DialogsAdapter$DialogsPreloader r1 = r13.preloader
            r5.setPreloader(r1)
        L_0x017b:
            androidx.recyclerview.widget.RecyclerView$LayoutParams r1 = new androidx.recyclerview.widget.RecyclerView$LayoutParams
            if (r15 != r14) goto L_0x0181
            r14 = -1
            goto L_0x0182
        L_0x0181:
            r14 = -2
        L_0x0182:
            r1.<init>((int) r0, (int) r14)
            r5.setLayoutParams(r1)
            org.telegram.ui.Components.RecyclerListView$Holder r14 = new org.telegram.ui.Components.RecyclerListView$Holder
            r14.<init>(r5)
            return r14
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Adapters.DialogsAdapter.onCreateViewHolder(android.view.ViewGroup, int):androidx.recyclerview.widget.RecyclerView$ViewHolder");
    }

    public int dialogsEmptyType() {
        int i = this.dialogsType;
        return (i == 7 || i == 8) ? MessagesController.getInstance(this.currentAccount).isDialogsEndReached(this.folderId) ? 2 : 3 : this.onlineContacts != null ? 1 : 0;
    }

    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        int itemViewType = viewHolder.getItemViewType();
        boolean z = false;
        if (itemViewType == 0) {
            DialogCell dialogCell = (DialogCell) viewHolder.itemView;
            TLRPC$Dialog tLRPC$Dialog = (TLRPC$Dialog) getItem(i);
            TLRPC$Dialog tLRPC$Dialog2 = (TLRPC$Dialog) getItem(i + 1);
            dialogCell.useSeparator = tLRPC$Dialog2 != null;
            dialogCell.fullSeparator = tLRPC$Dialog.pinned && tLRPC$Dialog2 != null && !tLRPC$Dialog2.pinned;
            if (this.dialogsType == 0 && AndroidUtilities.isTablet()) {
                dialogCell.setDialogSelected(tLRPC$Dialog.id == this.openedDialogId);
            }
            dialogCell.setChecked(this.selectedDialogs.contains(Long.valueOf(tLRPC$Dialog.id)), false);
            dialogCell.setDialog(tLRPC$Dialog, this.dialogsType, this.folderId);
            DialogsPreloader dialogsPreloader = this.preloader;
            if (dialogsPreloader != null && i < 10) {
                dialogsPreloader.add(tLRPC$Dialog.id);
            }
        } else if (itemViewType == 4) {
            ((DialogMeUrlCell) viewHolder.itemView).setRecentMeUrl((TLRPC$RecentMeUrl) getItem(i));
        } else if (itemViewType == 5) {
            DialogsEmptyCell dialogsEmptyCell = (DialogsEmptyCell) viewHolder.itemView;
            int i2 = this.lastDialogsEmptyType;
            int dialogsEmptyType = dialogsEmptyType();
            this.lastDialogsEmptyType = dialogsEmptyType;
            dialogsEmptyCell.setType(dialogsEmptyType);
            int i3 = this.dialogsType;
            if (!(i3 == 7 || i3 == 8)) {
                dialogsEmptyCell.setOnUtyanAnimationEndListener(new DialogsAdapter$$ExternalSyntheticLambda2(this));
                dialogsEmptyCell.setOnUtyanAnimationUpdateListener(new DialogsAdapter$$ExternalSyntheticLambda1(this));
                if (!dialogsEmptyCell.isUtyanAnimationTriggered() && this.dialogsCount == 0) {
                    this.parentFragment.setContactsAlpha(0.0f);
                    this.parentFragment.setScrollDisabled(true);
                }
                if (this.onlineContacts == null || i2 != 0) {
                    if (this.forceUpdatingContacts) {
                        if (this.dialogsCount == 0) {
                            dialogsEmptyCell.startUtyanCollapseAnimation(false);
                        }
                    } else if (dialogsEmptyCell.isUtyanAnimationTriggered() && this.lastDialogsEmptyType == 0) {
                        dialogsEmptyCell.startUtyanExpandAnimation();
                    }
                } else if (!dialogsEmptyCell.isUtyanAnimationTriggered()) {
                    dialogsEmptyCell.startUtyanCollapseAnimation(true);
                }
            }
        } else if (itemViewType == 6) {
            UserCell userCell = (UserCell) viewHolder.itemView;
            int i4 = this.dialogsCount;
            userCell.setData(MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(this.onlineContacts.get(i4 == 0 ? i - 3 : (i - i4) - 2).user_id)), (CharSequence) null, (CharSequence) null, 0);
        } else if (itemViewType == 7) {
            HeaderCell headerCell = (HeaderCell) viewHolder.itemView;
            int i5 = this.dialogsType;
            if (i5 != 11 && i5 != 12 && i5 != 13) {
                headerCell.setText(LocaleController.getString(this.forceUpdatingContacts ? NUM : NUM));
            } else if (i == 0) {
                headerCell.setText(LocaleController.getString("ImportHeader", NUM));
            } else {
                headerCell.setText(LocaleController.getString("ImportHeaderContacts", NUM));
            }
        } else if (itemViewType == 11) {
            TextInfoPrivacyCell textInfoPrivacyCell = (TextInfoPrivacyCell) viewHolder.itemView;
            textInfoPrivacyCell.setText(LocaleController.getString("TapOnThePencil", NUM));
            if (this.arrowDrawable == null) {
                Drawable drawable = this.mContext.getResources().getDrawable(NUM);
                this.arrowDrawable = drawable;
                drawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor("windowBackgroundWhiteGrayText4"), PorterDuff.Mode.MULTIPLY));
            }
            TextView textView = textInfoPrivacyCell.getTextView();
            textView.setCompoundDrawablePadding(AndroidUtilities.dp(4.0f));
            textView.setCompoundDrawablesWithIntrinsicBounds((Drawable) null, (Drawable) null, this.arrowDrawable, (Drawable) null);
            textView.getLayoutParams().width = -2;
        } else if (itemViewType == 12) {
            TextCell textCell = (TextCell) viewHolder.itemView;
            textCell.setColors("windowBackgroundWhiteBlueText4", "windowBackgroundWhiteBlueText4");
            String string = LocaleController.getString("CreateGroupForImport", NUM);
            if (this.dialogsCount != 0) {
                z = true;
            }
            textCell.setTextAndIcon(string, NUM, z);
            textCell.setIsInDialogs();
            textCell.setOffsetFromImage(75);
        }
        if (i >= this.dialogsCount + 1) {
            viewHolder.itemView.setAlpha(1.0f);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onBindViewHolder$2() {
        this.parentFragment.setScrollDisabled(false);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onBindViewHolder$3(Float f) {
        this.parentFragment.setContactsAlpha(f.floatValue());
    }

    public void setForceUpdatingContacts(boolean z) {
        this.forceUpdatingContacts = z;
    }

    public int getItemViewType(int i) {
        int i2;
        int i3 = this.dialogsCount;
        if (i3 != 0 || !this.forceUpdatingContacts) {
            if (this.onlineContacts != null) {
                if (i3 == 0) {
                    if (i == 0) {
                        return 5;
                    }
                    if (i == 1) {
                        return 8;
                    }
                    if (i == 2) {
                        return 7;
                    }
                    return 6;
                } else if (i < i3) {
                    return 0;
                } else {
                    if (i == i3) {
                        return 8;
                    }
                    if (i == i3 + 1) {
                        return 7;
                    }
                    if (i == this.currentCount - 1) {
                        return 10;
                    }
                    return 6;
                }
            } else if (this.hasHints) {
                int size = MessagesController.getInstance(this.currentAccount).hintDialogs.size();
                int i4 = size + 2;
                if (i >= i4) {
                    i -= i4;
                } else if (i == 0) {
                    return 2;
                } else {
                    if (i == size + 1) {
                        return 3;
                    }
                    return 4;
                }
            } else {
                if (!this.showArchiveHint) {
                    int i5 = this.dialogsType;
                    if (i5 == 11 || i5 == 13) {
                        if (i == 0) {
                            return 7;
                        }
                        if (i == 1) {
                            return 12;
                        }
                    } else if (i5 == 12) {
                        if (i == 0) {
                            return 7;
                        }
                        i--;
                    }
                } else if (i == 0) {
                    return 9;
                } else {
                    if (i == 1) {
                        return 8;
                    }
                }
                i -= 2;
            }
        } else if (i == 0) {
            return 5;
        } else {
            if (i == 1) {
                return 8;
            }
            if (i == 2) {
                return 7;
            }
            if (i == 3) {
                return 13;
            }
        }
        int i6 = this.folderId;
        if (i6 == 0 && this.dialogsCount > 10 && i == this.currentCount - 2 && this.dialogsType == 0) {
            return 11;
        }
        int size2 = this.parentFragment.getDialogsArray(this.currentAccount, this.dialogsType, i6, this.dialogsListFrozen).size();
        if (i != size2) {
            return i > size2 ? 10 : 0;
        }
        if (this.forceShowEmptyCell || (i2 = this.dialogsType) == 7 || i2 == 8 || MessagesController.getInstance(this.currentAccount).isDialogsEndReached(this.folderId)) {
            return size2 == 0 ? 5 : 10;
        }
        return 1;
    }

    public void notifyItemMoved(int i, int i2) {
        char c = 0;
        ArrayList<TLRPC$Dialog> dialogsArray = this.parentFragment.getDialogsArray(this.currentAccount, this.dialogsType, this.folderId, false);
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
            int i4 = dialogFilter.pinnedDialogs.get(tLRPC$Dialog.id);
            dialogFilter.pinnedDialogs.put(tLRPC$Dialog.id, dialogFilter.pinnedDialogs.get(tLRPC$Dialog2.id));
            dialogFilter.pinnedDialogs.put(tLRPC$Dialog2.id, i4);
        } else {
            int i5 = tLRPC$Dialog.pinnedNum;
            tLRPC$Dialog.pinnedNum = tLRPC$Dialog2.pinnedNum;
            tLRPC$Dialog2.pinnedNum = i5;
        }
        Collections.swap(dialogsArray, fixPosition, fixPosition2);
        super.notifyItemMoved(i, i2);
    }

    public void setArchivedPullDrawable(PullForegroundDrawable pullForegroundDrawable2) {
        this.pullForegroundDrawable = pullForegroundDrawable2;
    }

    public void didDatabaseCleared() {
        DialogsPreloader dialogsPreloader = this.preloader;
        if (dialogsPreloader != null) {
            dialogsPreloader.clear();
        }
    }

    public void resume() {
        DialogsPreloader dialogsPreloader = this.preloader;
        if (dialogsPreloader != null) {
            dialogsPreloader.resume();
        }
    }

    public void pause() {
        DialogsPreloader dialogsPreloader = this.preloader;
        if (dialogsPreloader != null) {
            dialogsPreloader.pause();
        }
    }

    public static class DialogsPreloader {
        Runnable clearNetworkRequestCount = new DialogsAdapter$DialogsPreloader$$ExternalSyntheticLambda0(this);
        int currentRequestCount;
        HashSet<Long> dialogsReadyMap = new HashSet<>();
        HashSet<Long> loadingDialogs = new HashSet<>();
        int networkRequestCount;
        ArrayList<Long> preloadDialogsPool = new ArrayList<>();
        HashSet<Long> preloadedErrorMap = new HashSet<>();
        boolean resumed;

        private boolean preloadIsAvilable() {
            return false;
        }

        public void updateList() {
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$0() {
            this.networkRequestCount = 0;
            start();
        }

        public void add(long j) {
            if (!isReady(j) && !this.preloadedErrorMap.contains(Long.valueOf(j)) && !this.loadingDialogs.contains(Long.valueOf(j)) && !this.preloadDialogsPool.contains(Long.valueOf(j))) {
                this.preloadDialogsPool.add(Long.valueOf(j));
                start();
            }
        }

        /* access modifiers changed from: private */
        public void start() {
            if (preloadIsAvilable() && this.resumed && !this.preloadDialogsPool.isEmpty() && this.currentRequestCount < 4 && this.networkRequestCount <= 6) {
                final long longValue = this.preloadDialogsPool.remove(0).longValue();
                this.currentRequestCount++;
                this.loadingDialogs.add(Long.valueOf(longValue));
                MessagesController.getInstance(UserConfig.selectedAccount).ensureMessagesLoaded(longValue, 0, new MessagesController.MessagesLoadedCallback() {
                    public void onMessagesLoaded(boolean z) {
                        AndroidUtilities.runOnUIThread(new DialogsAdapter$DialogsPreloader$1$$ExternalSyntheticLambda1(this, z, longValue));
                    }

                    /* access modifiers changed from: private */
                    public /* synthetic */ void lambda$onMessagesLoaded$0(boolean z, long j) {
                        if (!z) {
                            DialogsPreloader dialogsPreloader = DialogsPreloader.this;
                            int i = dialogsPreloader.networkRequestCount + 1;
                            dialogsPreloader.networkRequestCount = i;
                            if (i >= 6) {
                                AndroidUtilities.cancelRunOnUIThread(dialogsPreloader.clearNetworkRequestCount);
                                AndroidUtilities.runOnUIThread(DialogsPreloader.this.clearNetworkRequestCount, 60000);
                            }
                        }
                        if (DialogsPreloader.this.loadingDialogs.remove(Long.valueOf(j))) {
                            DialogsPreloader.this.dialogsReadyMap.add(Long.valueOf(j));
                            DialogsPreloader.this.updateList();
                            DialogsPreloader dialogsPreloader2 = DialogsPreloader.this;
                            dialogsPreloader2.currentRequestCount--;
                            dialogsPreloader2.start();
                        }
                    }

                    public void onError() {
                        AndroidUtilities.runOnUIThread(new DialogsAdapter$DialogsPreloader$1$$ExternalSyntheticLambda0(this, longValue));
                    }

                    /* access modifiers changed from: private */
                    public /* synthetic */ void lambda$onError$1(long j) {
                        if (DialogsPreloader.this.loadingDialogs.remove(Long.valueOf(j))) {
                            DialogsPreloader.this.preloadedErrorMap.add(Long.valueOf(j));
                            DialogsPreloader dialogsPreloader = DialogsPreloader.this;
                            dialogsPreloader.currentRequestCount--;
                            dialogsPreloader.start();
                        }
                    }
                });
            }
        }

        public boolean isReady(long j) {
            return this.dialogsReadyMap.contains(Long.valueOf(j));
        }

        public void remove(long j) {
            this.preloadDialogsPool.remove(Long.valueOf(j));
        }

        public void clear() {
            this.dialogsReadyMap.clear();
            this.preloadedErrorMap.clear();
            this.loadingDialogs.clear();
            this.preloadDialogsPool.clear();
            this.currentRequestCount = 0;
            this.networkRequestCount = 0;
            AndroidUtilities.cancelRunOnUIThread(this.clearNetworkRequestCount);
            updateList();
        }

        public void resume() {
            this.resumed = true;
            start();
        }

        public void pause() {
            this.resumed = false;
        }
    }

    public int getCurrentCount() {
        return this.currentCount;
    }

    public void setForceShowEmptyCell(boolean z) {
        this.forceShowEmptyCell = z;
    }

    public class LastEmptyView extends View {
        public boolean moving;

        public LastEmptyView(Context context) {
            super(context);
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int i, int i2) {
            int size = DialogsAdapter.this.parentFragment.getDialogsArray(DialogsAdapter.this.currentAccount, DialogsAdapter.this.dialogsType, DialogsAdapter.this.folderId, DialogsAdapter.this.dialogsListFrozen).size();
            int i3 = 0;
            boolean z = DialogsAdapter.this.dialogsType == 0 && MessagesController.getInstance(DialogsAdapter.this.currentAccount).dialogs_dict.get(DialogObject.makeFolderDialogId(1)) != null;
            View view = (View) getParent();
            int i4 = view instanceof BlurredRecyclerView ? ((BlurredRecyclerView) view).blurTopPadding : 0;
            int paddingTop = view.getPaddingTop();
            if (size != 0 && (paddingTop != 0 || z)) {
                int size2 = View.MeasureSpec.getSize(i2);
                if (size2 == 0) {
                    size2 = view.getMeasuredHeight();
                }
                if (size2 == 0) {
                    size2 = (AndroidUtilities.displaySize.y - ActionBar.getCurrentActionBarHeight()) - (Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0);
                }
                int i5 = size2 - i4;
                int dp = AndroidUtilities.dp(SharedConfig.useThreeLinesLayout ? 78.0f : 72.0f);
                int i6 = (size * dp) + (size - 1);
                if (DialogsAdapter.this.onlineContacts != null) {
                    i6 += (DialogsAdapter.this.onlineContacts.size() * AndroidUtilities.dp(58.0f)) + (DialogsAdapter.this.onlineContacts.size() - 1) + AndroidUtilities.dp(52.0f);
                }
                int i7 = z ? dp + 1 : 0;
                if (i6 < i5) {
                    int i8 = (i5 - i6) + i7;
                    if (paddingTop == 0 || (i8 = i8 - AndroidUtilities.statusBarHeight) >= 0) {
                        i3 = i8;
                    }
                } else {
                    int i9 = i6 - i5;
                    if (i9 < i7) {
                        int i10 = i7 - i9;
                        if (paddingTop != 0) {
                            i10 -= AndroidUtilities.statusBarHeight;
                        }
                        if (i10 >= 0) {
                            i3 = i10;
                        }
                    }
                }
            }
            setMeasuredDimension(View.MeasureSpec.getSize(i), i3);
        }
    }
}
