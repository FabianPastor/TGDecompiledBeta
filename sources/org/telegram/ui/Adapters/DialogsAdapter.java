package org.telegram.ui.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$Dialog;
import org.telegram.tgnet.TLRPC$EncryptedChat;
import org.telegram.tgnet.TLRPC$RecentMeUrl;
import org.telegram.tgnet.TLRPC$TL_contact;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.ArchiveHintCell;
import org.telegram.ui.Cells.DialogCell;
import org.telegram.ui.Cells.DialogMeUrlCell;
import org.telegram.ui.Cells.DialogsEmptyCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.ProfileSearchCell;
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
            } else if (this.dialogsType == 0 && (i2 = this.folderId) == 0 && instance.isDialogsEndReached(i2)) {
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
                } else if (instance.getAllFoldersDialogsCount() <= 10 && ContactsController.getInstance(this.currentAccount).doneLoadingContacts && !ContactsController.getInstance(this.currentAccount).contacts.isEmpty()) {
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
            if (i8 == 0 && !z && this.dialogsCount == 0 && this.forceUpdatingContacts) {
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

    /* JADX WARNING: type inference failed for: r1v1, types: [android.view.View] */
    /* JADX WARNING: type inference failed for: r1v10 */
    /* JADX WARNING: type inference failed for: r1v15 */
    /* JADX WARNING: type inference failed for: r3v31, types: [org.telegram.ui.Cells.DialogCell] */
    /* JADX WARNING: type inference failed for: r1v22 */
    /* JADX WARNING: type inference failed for: r1v23 */
    /* JADX WARNING: type inference failed for: r1v24 */
    /* JADX WARNING: type inference failed for: r1v25 */
    /* JADX WARNING: type inference failed for: r1v26 */
    /* JADX WARNING: type inference failed for: r1v27 */
    /* JADX WARNING: type inference failed for: r1v28 */
    /* JADX WARNING: type inference failed for: r1v29 */
    /* JADX WARNING: type inference failed for: r1v30 */
    /* JADX WARNING: type inference failed for: r1v31 */
    /* JADX WARNING: type inference failed for: r6v9, types: [org.telegram.ui.Cells.HeaderCell] */
    /* JADX WARNING: type inference failed for: r1v32 */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public androidx.recyclerview.widget.RecyclerView.ViewHolder onCreateViewHolder(android.view.ViewGroup r13, int r14) {
        /*
            r12 = this;
            r13 = 5
            r0 = -1
            java.lang.String r1 = "windowBackgroundGrayShadow"
            r2 = 2131165483(0x7var_b, float:1.7945184E38)
            java.lang.String r3 = "windowBackgroundGray"
            r4 = 1
            r5 = 0
            switch(r14) {
                case 0: goto L_0x017b;
                case 1: goto L_0x014e;
                case 2: goto L_0x00e2;
                case 3: goto L_0x00b8;
                case 4: goto L_0x00af;
                case 5: goto L_0x00a6;
                case 6: goto L_0x009b;
                case 7: goto L_0x0089;
                case 8: goto L_0x0065;
                case 9: goto L_0x005a;
                case 10: goto L_0x0051;
                case 11: goto L_0x002f;
                case 12: goto L_0x000e;
                case 13: goto L_0x014e;
                case 14: goto L_0x0017;
                default: goto L_0x000e;
            }
        L_0x000e:
            org.telegram.ui.Cells.TextCell r1 = new org.telegram.ui.Cells.TextCell
            android.content.Context r2 = r12.mContext
            r1.<init>(r2)
            goto L_0x01a1
        L_0x0017:
            org.telegram.ui.Cells.HeaderCell r1 = new org.telegram.ui.Cells.HeaderCell
            android.content.Context r7 = r12.mContext
            r9 = 16
            r10 = 0
            r11 = 0
            java.lang.String r8 = "key_graySectionText"
            r6 = r1
            r6.<init>(r7, r8, r9, r10, r11)
            r2 = 32
            r1.setHeight(r2)
            r1.setClickable(r5)
            goto L_0x01a1
        L_0x002f:
            org.telegram.ui.Adapters.DialogsAdapter$3 r5 = new org.telegram.ui.Adapters.DialogsAdapter$3
            android.content.Context r6 = r12.mContext
            r5.<init>(r6)
            android.content.Context r6 = r12.mContext
            android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r6, (int) r2, (java.lang.String) r1)
            org.telegram.ui.Components.CombinedDrawable r2 = new org.telegram.ui.Components.CombinedDrawable
            android.graphics.drawable.ColorDrawable r6 = new android.graphics.drawable.ColorDrawable
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            r6.<init>(r3)
            r2.<init>(r6, r1)
            r2.setFullsize(r4)
            r5.setBackgroundDrawable(r2)
            goto L_0x0086
        L_0x0051:
            org.telegram.ui.Adapters.DialogsAdapter$LastEmptyView r1 = new org.telegram.ui.Adapters.DialogsAdapter$LastEmptyView
            android.content.Context r2 = r12.mContext
            r1.<init>(r2)
            goto L_0x01a1
        L_0x005a:
            org.telegram.ui.Cells.ArchiveHintCell r1 = new org.telegram.ui.Cells.ArchiveHintCell
            android.content.Context r2 = r12.mContext
            r1.<init>(r2)
            r12.archiveHintCell = r1
            goto L_0x01a1
        L_0x0065:
            org.telegram.ui.Cells.ShadowSectionCell r5 = new org.telegram.ui.Cells.ShadowSectionCell
            android.content.Context r6 = r12.mContext
            r5.<init>(r6)
            android.content.Context r6 = r12.mContext
            android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r6, (int) r2, (java.lang.String) r1)
            org.telegram.ui.Components.CombinedDrawable r2 = new org.telegram.ui.Components.CombinedDrawable
            android.graphics.drawable.ColorDrawable r6 = new android.graphics.drawable.ColorDrawable
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            r6.<init>(r3)
            r2.<init>(r6, r1)
            r2.setFullsize(r4)
            r5.setBackgroundDrawable(r2)
        L_0x0086:
            r1 = r5
            goto L_0x01a1
        L_0x0089:
            org.telegram.ui.Cells.HeaderCell r1 = new org.telegram.ui.Cells.HeaderCell
            android.content.Context r2 = r12.mContext
            r1.<init>(r2)
            r2 = 1094713344(0x41400000, float:12.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r1.setPadding(r5, r5, r5, r2)
            goto L_0x01a1
        L_0x009b:
            org.telegram.ui.Cells.UserCell r1 = new org.telegram.ui.Cells.UserCell
            android.content.Context r2 = r12.mContext
            r3 = 8
            r1.<init>(r2, r3, r5, r5)
            goto L_0x01a1
        L_0x00a6:
            org.telegram.ui.Cells.DialogsEmptyCell r1 = new org.telegram.ui.Cells.DialogsEmptyCell
            android.content.Context r2 = r12.mContext
            r1.<init>(r2)
            goto L_0x01a1
        L_0x00af:
            org.telegram.ui.Cells.DialogMeUrlCell r1 = new org.telegram.ui.Cells.DialogMeUrlCell
            android.content.Context r2 = r12.mContext
            r1.<init>(r2)
            goto L_0x01a1
        L_0x00b8:
            org.telegram.ui.Adapters.DialogsAdapter$2 r4 = new org.telegram.ui.Adapters.DialogsAdapter$2
            android.content.Context r5 = r12.mContext
            r4.<init>(r12, r5)
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            r4.setBackgroundColor(r3)
            android.view.View r3 = new android.view.View
            android.content.Context r5 = r12.mContext
            r3.<init>(r5)
            android.content.Context r5 = r12.mContext
            android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r5, (int) r2, (java.lang.String) r1)
            r3.setBackgroundDrawable(r1)
            r1 = -1082130432(0xffffffffbvar_, float:-1.0)
            android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame(r0, r1)
            r4.addView(r3, r1)
            r1 = r4
            goto L_0x01a1
        L_0x00e2:
            org.telegram.ui.Cells.HeaderCell r1 = new org.telegram.ui.Cells.HeaderCell
            android.content.Context r2 = r12.mContext
            r1.<init>(r2)
            r2 = 2131627661(0x7f0e0e8d, float:1.8882593E38)
            java.lang.String r3 = "RecentlyViewed"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            android.widget.TextView r2 = new android.widget.TextView
            android.content.Context r3 = r12.mContext
            r2.<init>(r3)
            r3 = 1097859072(0x41700000, float:15.0)
            r2.setTextSize(r4, r3)
            java.lang.String r3 = "fonts/rmedium.ttf"
            android.graphics.Typeface r3 = org.telegram.messenger.AndroidUtilities.getTypeface(r3)
            r2.setTypeface(r3)
            java.lang.String r3 = "windowBackgroundWhiteBlueHeader"
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            r2.setTextColor(r3)
            r3 = 2131627662(0x7f0e0e8e, float:1.8882595E38)
            java.lang.String r4 = "RecentlyViewedHide"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r2.setText(r3)
            boolean r3 = org.telegram.messenger.LocaleController.isRTL
            r4 = 3
            if (r3 == 0) goto L_0x0126
            r3 = 3
            goto L_0x0127
        L_0x0126:
            r3 = 5
        L_0x0127:
            r3 = r3 | 16
            r2.setGravity(r3)
            r5 = -1
            r6 = -1082130432(0xffffffffbvar_, float:-1.0)
            boolean r3 = org.telegram.messenger.LocaleController.isRTL
            if (r3 == 0) goto L_0x0134
            goto L_0x0135
        L_0x0134:
            r4 = 5
        L_0x0135:
            r7 = r4 | 48
            r8 = 1099431936(0x41880000, float:17.0)
            r9 = 1097859072(0x41700000, float:15.0)
            r10 = 1099431936(0x41880000, float:17.0)
            r11 = 0
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r5, r6, r7, r8, r9, r10, r11)
            r1.addView(r2, r3)
            org.telegram.ui.Adapters.DialogsAdapter$$ExternalSyntheticLambda0 r3 = new org.telegram.ui.Adapters.DialogsAdapter$$ExternalSyntheticLambda0
            r3.<init>(r12)
            r2.setOnClickListener(r3)
            goto L_0x01a1
        L_0x014e:
            org.telegram.ui.Components.FlickerLoadingView r1 = new org.telegram.ui.Components.FlickerLoadingView
            android.content.Context r2 = r12.mContext
            r1.<init>(r2)
            r1.setIsSingleCell(r4)
            r2 = 13
            if (r14 != r2) goto L_0x015f
            r3 = 18
            goto L_0x0160
        L_0x015f:
            r3 = 7
        L_0x0160:
            r1.setViewType(r3)
            if (r14 != r2) goto L_0x01a1
            android.graphics.Point r2 = org.telegram.messenger.AndroidUtilities.displaySize
            int r2 = r2.y
            float r2 = (float) r2
            r3 = 1056964608(0x3var_, float:0.5)
            float r2 = r2 * r3
            r3 = 1115684864(0x42800000, float:64.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r3 = (float) r3
            float r2 = r2 / r3
            int r2 = (int) r2
            r1.setItemsCount(r2)
            goto L_0x01a1
        L_0x017b:
            int r1 = r12.dialogsType
            r2 = 2
            if (r1 != r2) goto L_0x0188
            org.telegram.ui.Cells.ProfileSearchCell r1 = new org.telegram.ui.Cells.ProfileSearchCell
            android.content.Context r2 = r12.mContext
            r1.<init>(r2)
            goto L_0x01a1
        L_0x0188:
            org.telegram.ui.Cells.DialogCell r1 = new org.telegram.ui.Cells.DialogCell
            org.telegram.ui.DialogsActivity r4 = r12.parentFragment
            android.content.Context r5 = r12.mContext
            r6 = 1
            r7 = 0
            int r8 = r12.currentAccount
            r9 = 0
            r3 = r1
            r3.<init>(r4, r5, r6, r7, r8, r9)
            org.telegram.ui.Components.PullForegroundDrawable r2 = r12.pullForegroundDrawable
            r1.setArchivedPullAnimation(r2)
            org.telegram.ui.Adapters.DialogsAdapter$DialogsPreloader r2 = r12.preloader
            r1.setPreloader(r2)
        L_0x01a1:
            androidx.recyclerview.widget.RecyclerView$LayoutParams r2 = new androidx.recyclerview.widget.RecyclerView$LayoutParams
            if (r14 != r13) goto L_0x01a7
            r13 = -1
            goto L_0x01a8
        L_0x01a7:
            r13 = -2
        L_0x01a8:
            r2.<init>((int) r0, (int) r13)
            r1.setLayoutParams(r2)
            org.telegram.ui.Components.RecyclerListView$Holder r13 = new org.telegram.ui.Components.RecyclerListView$Holder
            r13.<init>(r1)
            return r13
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Adapters.DialogsAdapter.onCreateViewHolder(android.view.ViewGroup, int):androidx.recyclerview.widget.RecyclerView$ViewHolder");
    }

    public int dialogsEmptyType() {
        int i = this.dialogsType;
        return (i == 7 || i == 8) ? MessagesController.getInstance(this.currentAccount).isDialogsEndReached(this.folderId) ? 2 : 3 : this.onlineContacts != null ? 1 : 0;
    }

    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        TLRPC$Chat tLRPC$Chat;
        String str;
        TLRPC$Chat chat;
        RecyclerView.ViewHolder viewHolder2 = viewHolder;
        int i2 = i;
        int itemViewType = viewHolder.getItemViewType();
        String str2 = null;
        boolean z = false;
        if (itemViewType == 0) {
            TLRPC$Dialog tLRPC$Dialog = (TLRPC$Dialog) getItem(i2);
            TLRPC$Dialog tLRPC$Dialog2 = (TLRPC$Dialog) getItem(i2 + 1);
            int i3 = this.dialogsType;
            if (i3 == 2) {
                ProfileSearchCell profileSearchCell = (ProfileSearchCell) viewHolder2.itemView;
                long dialogId = profileSearchCell.getDialogId();
                if (tLRPC$Dialog.id != 0) {
                    TLRPC$Chat chat2 = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(-tLRPC$Dialog.id));
                    tLRPC$Chat = (chat2 == null || chat2.migrated_to == null || (chat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(chat2.migrated_to.channel_id))) == null) ? chat2 : chat;
                } else {
                    tLRPC$Chat = null;
                }
                if (tLRPC$Chat != null) {
                    str2 = tLRPC$Chat.title;
                    if (!ChatObject.isChannel(tLRPC$Chat) || tLRPC$Chat.megagroup) {
                        int i4 = tLRPC$Chat.participants_count;
                        if (i4 != 0) {
                            str = LocaleController.formatPluralStringComma("Members", i4);
                        } else if (tLRPC$Chat.has_geo) {
                            str = LocaleController.getString("MegaLocation", NUM);
                        } else {
                            str = TextUtils.isEmpty(tLRPC$Chat.username) ? LocaleController.getString("MegaPrivate", NUM).toLowerCase() : LocaleController.getString("MegaPublic", NUM).toLowerCase();
                        }
                    } else {
                        int i5 = tLRPC$Chat.participants_count;
                        if (i5 != 0) {
                            str = LocaleController.formatPluralStringComma("Subscribers", i5);
                        } else if (TextUtils.isEmpty(tLRPC$Chat.username)) {
                            str = LocaleController.getString("ChannelPrivate", NUM).toLowerCase();
                        } else {
                            str = LocaleController.getString("ChannelPublic", NUM).toLowerCase();
                        }
                    }
                } else {
                    str = "";
                }
                String str3 = str;
                String str4 = str2;
                profileSearchCell.useSeparator = tLRPC$Dialog2 != null;
                profileSearchCell.setData(tLRPC$Chat, (TLRPC$EncryptedChat) null, str4, str3, false, false);
                boolean contains = this.selectedDialogs.contains(Long.valueOf(profileSearchCell.getDialogId()));
                if (dialogId == profileSearchCell.getDialogId()) {
                    z = true;
                }
                profileSearchCell.setChecked(contains, z);
            } else {
                DialogCell dialogCell = (DialogCell) viewHolder2.itemView;
                dialogCell.useSeparator = tLRPC$Dialog2 != null;
                dialogCell.fullSeparator = tLRPC$Dialog.pinned && tLRPC$Dialog2 != null && !tLRPC$Dialog2.pinned;
                if (i3 == 0 && AndroidUtilities.isTablet()) {
                    dialogCell.setDialogSelected(tLRPC$Dialog.id == this.openedDialogId);
                }
                dialogCell.setChecked(this.selectedDialogs.contains(Long.valueOf(tLRPC$Dialog.id)), false);
                dialogCell.setDialog(tLRPC$Dialog, this.dialogsType, this.folderId);
                DialogsPreloader dialogsPreloader = this.preloader;
                if (dialogsPreloader != null && i2 < 10) {
                    dialogsPreloader.add(tLRPC$Dialog.id);
                }
            }
        } else if (itemViewType == 14) {
            HeaderCell headerCell = (HeaderCell) viewHolder2.itemView;
            headerCell.setTextSize(14.0f);
            headerCell.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText"));
            headerCell.setBackgroundColor(Theme.getColor("graySection"));
            try {
                MessagesController messagesController = AccountInstance.getInstance(this.currentAccount).getMessagesController();
                if (messagesController.dialogsMyChannels.size() > 0) {
                    if (i2 == 0) {
                        headerCell.setText(LocaleController.getString("MyChannels", NUM));
                    }
                    z = 0 + messagesController.dialogsMyChannels.size() + 1;
                }
                int i6 = z;
                if (messagesController.dialogsMyGroups.size() > 0) {
                    if (i2 == z) {
                        headerCell.setText(LocaleController.getString("MyGroups", NUM));
                    }
                    i6 = (z ? 1 : 0) + messagesController.dialogsMyGroups.size() + 1;
                }
                if (messagesController.dialogsCanAddUsers.size() > 0 && i2 == i6) {
                    headerCell.setText(LocaleController.getString("FilterGroups", NUM));
                }
            } catch (Exception unused) {
            }
        } else if (itemViewType == 4) {
            ((DialogMeUrlCell) viewHolder2.itemView).setRecentMeUrl((TLRPC$RecentMeUrl) getItem(i2));
        } else if (itemViewType == 5) {
            DialogsEmptyCell dialogsEmptyCell = (DialogsEmptyCell) viewHolder2.itemView;
            int i7 = this.lastDialogsEmptyType;
            int dialogsEmptyType = dialogsEmptyType();
            this.lastDialogsEmptyType = dialogsEmptyType;
            dialogsEmptyCell.setType(dialogsEmptyType);
            int i8 = this.dialogsType;
            if (!(i8 == 7 || i8 == 8)) {
                dialogsEmptyCell.setOnUtyanAnimationEndListener(new DialogsAdapter$$ExternalSyntheticLambda2(this));
                dialogsEmptyCell.setOnUtyanAnimationUpdateListener(new DialogsAdapter$$ExternalSyntheticLambda1(this));
                if (!dialogsEmptyCell.isUtyanAnimationTriggered() && this.dialogsCount == 0) {
                    this.parentFragment.setContactsAlpha(0.0f);
                    this.parentFragment.setScrollDisabled(true);
                }
                if (this.onlineContacts == null || i7 != 0) {
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
            UserCell userCell = (UserCell) viewHolder2.itemView;
            int i9 = this.dialogsCount;
            userCell.setData(MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(this.onlineContacts.get(i9 == 0 ? i2 - 3 : (i2 - i9) - 2).user_id)), (CharSequence) null, (CharSequence) null, 0);
        } else if (itemViewType == 7) {
            HeaderCell headerCell2 = (HeaderCell) viewHolder2.itemView;
            int i10 = this.dialogsType;
            if (i10 != 11 && i10 != 12 && i10 != 13) {
                headerCell2.setText(LocaleController.getString((this.dialogsCount != 0 || !this.forceUpdatingContacts) ? NUM : NUM));
            } else if (i2 == 0) {
                headerCell2.setText(LocaleController.getString("ImportHeader", NUM));
            } else {
                headerCell2.setText(LocaleController.getString("ImportHeaderContacts", NUM));
            }
        } else if (itemViewType == 11) {
            TextInfoPrivacyCell textInfoPrivacyCell = (TextInfoPrivacyCell) viewHolder2.itemView;
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
            TextCell textCell = (TextCell) viewHolder2.itemView;
            textCell.setColors("windowBackgroundWhiteBlueText4", "windowBackgroundWhiteBlueText4");
            String string = LocaleController.getString("CreateGroupForImport", NUM);
            if (this.dialogsCount != 0) {
                z = true;
            }
            textCell.setTextAndIcon(string, NUM, z);
            textCell.setIsInDialogs();
            textCell.setOffsetFromImage(75);
        }
        if (i2 >= this.dialogsCount + 1) {
            viewHolder2.itemView.setAlpha(1.0f);
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
        if (i == size2) {
            if (this.forceShowEmptyCell || (i2 = this.dialogsType) == 7 || i2 == 8 || MessagesController.getInstance(this.currentAccount).isDialogsEndReached(this.folderId)) {
                return size2 == 0 ? 5 : 10;
            }
            return 1;
        } else if (i > size2) {
            return 10;
        } else {
            if (this.dialogsType == 2 && getItem(i) == null) {
                return 14;
            }
            return 0;
        }
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
            int paddingTop = view.getPaddingTop() - i4;
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
