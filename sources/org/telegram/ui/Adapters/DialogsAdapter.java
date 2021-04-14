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
import j$.util.Comparator;
import j$.util.function.Function;
import j$.util.function.ToDoubleFunction;
import j$.util.function.ToIntFunction;
import j$.util.function.ToLongFunction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import org.telegram.messenger.AndroidUtilities;
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
import org.telegram.ui.Adapters.DialogsAdapter;
import org.telegram.ui.Cells.ArchiveHintCell;
import org.telegram.ui.Cells.DialogCell;
import org.telegram.ui.Cells.DialogMeUrlCell;
import org.telegram.ui.Cells.DialogsEmptyCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.UserCell;
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
    private boolean hasHints;
    private boolean isOnlySelect;
    private boolean isReordering;
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
            if (this.showArchiveHint) {
                this.archiveHintCell = new ArchiveHintCell(context);
            }
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

    public int getItemCount() {
        int i;
        int i2;
        int i3;
        int i4;
        MessagesController instance = MessagesController.getInstance(this.currentAccount);
        int size = this.parentFragment.getDialogsArray(this.currentAccount, this.dialogsType, this.folderId, this.dialogsListFrozen).size();
        this.dialogsCount = size;
        boolean z = false;
        if (this.forceShowEmptyCell || (i3 = this.dialogsType) == 7 || i3 == 8 || i3 == 11 || size != 0 || ((i4 = this.folderId) == 0 && !instance.isLoadingDialogs(i4) && MessagesController.getInstance(this.currentAccount).isDialogsEndReached(this.folderId))) {
            int i5 = this.dialogsCount;
            int i6 = this.dialogsType;
            if (i6 == 7 || i6 == 8 ? i5 == 0 : !(instance.isDialogsEndReached(this.folderId) && this.dialogsCount != 0)) {
                i5++;
            }
            if (this.hasHints) {
                i5 += instance.hintDialogs.size() + 2;
            } else if (this.dialogsType == 0 && instance.dialogs_dict.size() <= 10 && (i2 = this.folderId) == 0 && instance.isDialogsEndReached(i2)) {
                if (ContactsController.getInstance(this.currentAccount).contacts.isEmpty() && ContactsController.getInstance(this.currentAccount).isLoadingContacts()) {
                    this.onlineContacts = null;
                    this.currentCount = 0;
                    return 0;
                } else if (!ContactsController.getInstance(this.currentAccount).contacts.isEmpty()) {
                    if (!(this.onlineContacts != null && this.prevDialogsCount == instance.dialogs_dict.size() && this.prevContactsCount == ContactsController.getInstance(this.currentAccount).contacts.size())) {
                        ArrayList<TLRPC$TL_contact> arrayList = new ArrayList<>(ContactsController.getInstance(this.currentAccount).contacts);
                        this.onlineContacts = arrayList;
                        this.prevContactsCount = arrayList.size();
                        this.prevDialogsCount = instance.dialogs_dict.size();
                        int i7 = UserConfig.getInstance(this.currentAccount).clientUserId;
                        int size2 = this.onlineContacts.size();
                        int i8 = 0;
                        while (i8 < size2) {
                            int i9 = this.onlineContacts.get(i8).user_id;
                            if (i9 == i7 || instance.dialogs_dict.get((long) i9) != null) {
                                this.onlineContacts.remove(i8);
                                i8--;
                                size2--;
                            }
                            i8++;
                        }
                        if (this.onlineContacts.isEmpty()) {
                            this.onlineContacts = null;
                        }
                        sortOnlineContacts(false);
                    }
                    ArrayList<TLRPC$TL_contact> arrayList2 = this.onlineContacts;
                    if (arrayList2 != null) {
                        i5 += arrayList2.size() + 2;
                        z = true;
                    }
                }
            }
            int i10 = this.folderId;
            if (i10 == 0 && this.onlineContacts != null && !z) {
                this.onlineContacts = null;
            }
            if (i10 == 1 && this.showArchiveHint) {
                i5 += 2;
            }
            if (i10 == 0 && (i = this.dialogsCount) != 0) {
                i5++;
                if (i > 10 && this.dialogsType == 0) {
                    i5++;
                }
            }
            int i11 = this.dialogsType;
            if (i11 == 11 || i11 == 13) {
                i5 += 2;
            } else if (i11 == 12) {
                i5++;
            }
            this.currentCount = i5;
            return i5;
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
        return MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.onlineContacts.get(i4).user_id));
    }

    public void sortOnlineContacts(boolean z) {
        if (this.onlineContacts == null) {
            return;
        }
        if (!z || SystemClock.elapsedRealtime() - this.lastSortTime >= 2000) {
            this.lastSortTime = SystemClock.elapsedRealtime();
            try {
                int currentTime = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
                Collections.sort(this.onlineContacts, new Object(currentTime) {
                    public final /* synthetic */ int f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final int compare(Object obj, Object obj2) {
                        return DialogsAdapter.lambda$sortOnlineContacts$0(MessagesController.this, this.f$1, (TLRPC$TL_contact) obj, (TLRPC$TL_contact) obj2);
                    }

                    public /* synthetic */ Comparator reversed() {
                        return Comparator.CC.$default$reversed(this);
                    }

                    public /* synthetic */ java.util.Comparator thenComparing(Function function) {
                        return Comparator.CC.$default$thenComparing((java.util.Comparator) this, function);
                    }

                    public /* synthetic */ java.util.Comparator thenComparing(Function function, java.util.Comparator comparator) {
                        return Comparator.CC.$default$thenComparing(this, function, comparator);
                    }

                    public /* synthetic */ java.util.Comparator thenComparing(java.util.Comparator comparator) {
                        return Comparator.CC.$default$thenComparing((java.util.Comparator) this, comparator);
                    }

                    public /* synthetic */ java.util.Comparator thenComparingDouble(ToDoubleFunction toDoubleFunction) {
                        return Comparator.CC.$default$thenComparingDouble(this, toDoubleFunction);
                    }

                    public /* synthetic */ java.util.Comparator thenComparingInt(ToIntFunction toIntFunction) {
                        return Comparator.CC.$default$thenComparingInt(this, toIntFunction);
                    }

                    public /* synthetic */ java.util.Comparator thenComparingLong(ToLongFunction toLongFunction) {
                        return Comparator.CC.$default$thenComparingLong(this, toLongFunction);
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
        return (itemViewType == 1 || itemViewType == 5 || itemViewType == 3 || itemViewType == 8 || itemViewType == 7 || itemViewType == 9 || itemViewType == 10 || itemViewType == 11) ? false : true;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onCreateViewHolder$1 */
    public /* synthetic */ void lambda$onCreateViewHolder$1$DialogsAdapter(View view) {
        MessagesController.getInstance(this.currentAccount).hintDialogs.clear();
        MessagesController.getGlobalMainSettings().edit().remove("installReferer").commit();
        notifyDataSetChanged();
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v0, resolved type: org.telegram.ui.Cells.DialogCell} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v2, resolved type: org.telegram.ui.Components.FlickerLoadingView} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v3, resolved type: org.telegram.ui.Cells.HeaderCell} */
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
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v25, resolved type: org.telegram.ui.Cells.DialogCell} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v26, resolved type: org.telegram.ui.Cells.DialogCell} */
    /* JADX WARNING: type inference failed for: r5v4, types: [android.widget.FrameLayout, org.telegram.ui.Adapters.DialogsAdapter$1] */
    /* JADX WARNING: type inference failed for: r5v5, types: [org.telegram.ui.Cells.DialogMeUrlCell] */
    /* JADX WARNING: type inference failed for: r5v6, types: [org.telegram.ui.Cells.DialogsEmptyCell] */
    /* JADX WARNING: type inference failed for: r5v7, types: [org.telegram.ui.Cells.UserCell] */
    /* JADX WARNING: type inference failed for: r5v8, types: [org.telegram.ui.Cells.HeaderCell] */
    /* JADX WARNING: type inference failed for: r5v9, types: [org.telegram.ui.Cells.ShadowSectionCell, android.view.View] */
    /* JADX WARNING: type inference failed for: r5v10, types: [org.telegram.ui.Cells.ArchiveHintCell, android.widget.FrameLayout] */
    /* JADX WARNING: type inference failed for: r5v11, types: [org.telegram.ui.Adapters.DialogsAdapter$LastEmptyView] */
    /* JADX WARNING: type inference failed for: r5v12, types: [android.view.View, org.telegram.ui.Adapters.DialogsAdapter$2] */
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
            r2 = 2131165449(0x7var_, float:1.7945115E38)
            java.lang.String r3 = "windowBackgroundGray"
            r4 = 1
            switch(r15) {
                case 0: goto L_0x0147;
                case 1: goto L_0x0138;
                case 2: goto L_0x00cc;
                case 3: goto L_0x00a3;
                case 4: goto L_0x009a;
                case 5: goto L_0x0091;
                case 6: goto L_0x0085;
                case 7: goto L_0x007c;
                case 8: goto L_0x0059;
                case 9: goto L_0x0042;
                case 10: goto L_0x0039;
                case 11: goto L_0x0016;
                default: goto L_0x000d;
            }
        L_0x000d:
            org.telegram.ui.Cells.TextCell r5 = new org.telegram.ui.Cells.TextCell
            android.content.Context r1 = r13.mContext
            r5.<init>(r1)
            goto L_0x015f
        L_0x0016:
            org.telegram.ui.Adapters.DialogsAdapter$2 r5 = new org.telegram.ui.Adapters.DialogsAdapter$2
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
            goto L_0x015f
        L_0x0039:
            org.telegram.ui.Adapters.DialogsAdapter$LastEmptyView r5 = new org.telegram.ui.Adapters.DialogsAdapter$LastEmptyView
            android.content.Context r1 = r13.mContext
            r5.<init>(r1)
            goto L_0x015f
        L_0x0042:
            org.telegram.ui.Cells.ArchiveHintCell r5 = r13.archiveHintCell
            android.view.ViewParent r1 = r5.getParent()
            if (r1 == 0) goto L_0x015f
            org.telegram.ui.Cells.ArchiveHintCell r1 = r13.archiveHintCell
            android.view.ViewParent r1 = r1.getParent()
            android.view.ViewGroup r1 = (android.view.ViewGroup) r1
            org.telegram.ui.Cells.ArchiveHintCell r2 = r13.archiveHintCell
            r1.removeView(r2)
            goto L_0x015f
        L_0x0059:
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
            goto L_0x015f
        L_0x007c:
            org.telegram.ui.Cells.HeaderCell r5 = new org.telegram.ui.Cells.HeaderCell
            android.content.Context r1 = r13.mContext
            r5.<init>(r1)
            goto L_0x015f
        L_0x0085:
            org.telegram.ui.Cells.UserCell r5 = new org.telegram.ui.Cells.UserCell
            android.content.Context r1 = r13.mContext
            r2 = 8
            r3 = 0
            r5.<init>(r1, r2, r3, r3)
            goto L_0x015f
        L_0x0091:
            org.telegram.ui.Cells.DialogsEmptyCell r5 = new org.telegram.ui.Cells.DialogsEmptyCell
            android.content.Context r1 = r13.mContext
            r5.<init>(r1)
            goto L_0x015f
        L_0x009a:
            org.telegram.ui.Cells.DialogMeUrlCell r5 = new org.telegram.ui.Cells.DialogMeUrlCell
            android.content.Context r1 = r13.mContext
            r5.<init>(r1)
            goto L_0x015f
        L_0x00a3:
            org.telegram.ui.Adapters.DialogsAdapter$1 r5 = new org.telegram.ui.Adapters.DialogsAdapter$1
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
            goto L_0x015f
        L_0x00cc:
            org.telegram.ui.Cells.HeaderCell r5 = new org.telegram.ui.Cells.HeaderCell
            android.content.Context r1 = r13.mContext
            r5.<init>(r1)
            r1 = 2131627104(0x7f0e0CLASSNAME, float:1.8881463E38)
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
            r2 = 2131627105(0x7f0e0CLASSNAME, float:1.8881465E38)
            java.lang.String r3 = "RecentlyViewedHide"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r1.setText(r2)
            boolean r2 = org.telegram.messenger.LocaleController.isRTL
            r3 = 3
            if (r2 == 0) goto L_0x0110
            r2 = 3
            goto L_0x0111
        L_0x0110:
            r2 = 5
        L_0x0111:
            r2 = r2 | 16
            r1.setGravity(r2)
            r6 = -1
            r7 = -1082130432(0xffffffffbvar_, float:-1.0)
            boolean r2 = org.telegram.messenger.LocaleController.isRTL
            if (r2 == 0) goto L_0x011e
            goto L_0x011f
        L_0x011e:
            r3 = 5
        L_0x011f:
            r8 = r3 | 48
            r9 = 1099431936(0x41880000, float:17.0)
            r10 = 1097859072(0x41700000, float:15.0)
            r11 = 1099431936(0x41880000, float:17.0)
            r12 = 0
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r6, r7, r8, r9, r10, r11, r12)
            r5.addView(r1, r2)
            org.telegram.ui.Adapters.-$$Lambda$DialogsAdapter$B0qIe6TxDardgaYX-7eWyt6VPHc r2 = new org.telegram.ui.Adapters.-$$Lambda$DialogsAdapter$B0qIe6TxDardgaYX-7eWyt6VPHc
            r2.<init>()
            r1.setOnClickListener(r2)
            goto L_0x015f
        L_0x0138:
            org.telegram.ui.Components.FlickerLoadingView r5 = new org.telegram.ui.Components.FlickerLoadingView
            android.content.Context r1 = r13.mContext
            r5.<init>(r1)
            r5.setIsSingleCell(r4)
            r1 = 7
            r5.setViewType(r1)
            goto L_0x015f
        L_0x0147:
            org.telegram.ui.Cells.DialogCell r5 = new org.telegram.ui.Cells.DialogCell
            org.telegram.ui.DialogsActivity r7 = r13.parentFragment
            android.content.Context r8 = r13.mContext
            r9 = 1
            r10 = 0
            int r11 = r13.currentAccount
            r6 = r5
            r6.<init>(r7, r8, r9, r10, r11)
            org.telegram.ui.Components.PullForegroundDrawable r1 = r13.pullForegroundDrawable
            r5.setArchivedPullAnimation(r1)
            org.telegram.ui.Adapters.DialogsAdapter$DialogsPreloader r1 = r13.preloader
            r5.setPreloader(r1)
        L_0x015f:
            androidx.recyclerview.widget.RecyclerView$LayoutParams r1 = new androidx.recyclerview.widget.RecyclerView$LayoutParams
            if (r15 != r14) goto L_0x0165
            r14 = -1
            goto L_0x0166
        L_0x0165:
            r14 = -2
        L_0x0166:
            r1.<init>((int) r0, (int) r14)
            r5.setLayoutParams(r1)
            org.telegram.ui.Components.RecyclerListView$Holder r14 = new org.telegram.ui.Components.RecyclerListView$Holder
            r14.<init>(r5)
            return r14
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Adapters.DialogsAdapter.onCreateViewHolder(android.view.ViewGroup, int):androidx.recyclerview.widget.RecyclerView$ViewHolder");
    }

    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        int itemViewType = viewHolder.getItemViewType();
        boolean z = true;
        if (itemViewType == 0) {
            DialogCell dialogCell = (DialogCell) viewHolder.itemView;
            TLRPC$Dialog tLRPC$Dialog = (TLRPC$Dialog) getItem(i);
            TLRPC$Dialog tLRPC$Dialog2 = (TLRPC$Dialog) getItem(i + 1);
            dialogCell.useSeparator = tLRPC$Dialog2 != null;
            dialogCell.fullSeparator = tLRPC$Dialog.pinned && tLRPC$Dialog2 != null && !tLRPC$Dialog2.pinned;
            if (this.dialogsType == 0 && AndroidUtilities.isTablet()) {
                if (tLRPC$Dialog.id != this.openedDialogId) {
                    z = false;
                }
                dialogCell.setDialogSelected(z);
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
            int i2 = this.dialogsType;
            if (i2 != 7 && i2 != 8) {
                if (this.onlineContacts == null) {
                    z = false;
                }
                dialogsEmptyCell.setType(z ? 1 : 0);
            } else if (MessagesController.getInstance(this.currentAccount).isDialogsEndReached(this.folderId)) {
                dialogsEmptyCell.setType(2);
            } else {
                dialogsEmptyCell.setType(3);
            }
        } else if (itemViewType == 6) {
            UserCell userCell = (UserCell) viewHolder.itemView;
            int i3 = this.dialogsCount;
            userCell.setData(MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.onlineContacts.get(i3 == 0 ? i - 3 : (i - i3) - 2).user_id)), (CharSequence) null, (CharSequence) null, 0);
        } else if (itemViewType == 7) {
            HeaderCell headerCell = (HeaderCell) viewHolder.itemView;
            int i4 = this.dialogsType;
            if (i4 != 11 && i4 != 12 && i4 != 13) {
                headerCell.setText(LocaleController.getString("YourContacts", NUM));
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
            if (this.dialogsCount == 0) {
                z = false;
            }
            textCell.setTextAndIcon(string, NUM, z);
            textCell.setIsInDialogs();
            textCell.setOffsetFromImage(75);
        }
    }

    public int getItemViewType(int i) {
        int i2;
        if (this.onlineContacts != null) {
            int i3 = this.dialogsCount;
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
        } else {
            if (this.hasHints) {
                int size = MessagesController.getInstance(this.currentAccount).hintDialogs.size();
                int i4 = size + 2;
                if (i >= i4) {
                    i -= i4;
                } else if (i == 0) {
                    return 2;
                } else {
                    return i == size + 1 ? 3 : 4;
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
        Runnable clearNetworkRequestCount = new Runnable() {
            public final void run() {
                DialogsAdapter.DialogsPreloader.this.lambda$new$0$DialogsAdapter$DialogsPreloader();
            }
        };
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
        /* renamed from: lambda$new$0 */
        public /* synthetic */ void lambda$new$0$DialogsAdapter$DialogsPreloader() {
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
                        AndroidUtilities.runOnUIThread(
                        /*  JADX ERROR: Method code generation error
                            jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x0007: INVOKE  
                              (wrap: org.telegram.ui.Adapters.-$$Lambda$DialogsAdapter$DialogsPreloader$1$uLfPHYxzbWAkjtxFDFkg7uuiZ0c : 0x0004: CONSTRUCTOR  (r2v0 org.telegram.ui.Adapters.-$$Lambda$DialogsAdapter$DialogsPreloader$1$uLfPHYxzbWAkjtxFDFkg7uuiZ0c) = 
                              (r3v0 'this' org.telegram.ui.Adapters.DialogsAdapter$DialogsPreloader$1 A[THIS])
                              (r4v0 'z' boolean)
                              (wrap: long : 0x0000: IGET  (r0v0 long) = 
                              (r3v0 'this' org.telegram.ui.Adapters.DialogsAdapter$DialogsPreloader$1 A[THIS])
                             org.telegram.ui.Adapters.DialogsAdapter.DialogsPreloader.1.val$dialog_id long)
                             call: org.telegram.ui.Adapters.-$$Lambda$DialogsAdapter$DialogsPreloader$1$uLfPHYxzbWAkjtxFDFkg7uuiZ0c.<init>(org.telegram.ui.Adapters.DialogsAdapter$DialogsPreloader$1, boolean, long):void type: CONSTRUCTOR)
                             org.telegram.messenger.AndroidUtilities.runOnUIThread(java.lang.Runnable):void type: STATIC in method: org.telegram.ui.Adapters.DialogsAdapter.DialogsPreloader.1.onMessagesLoaded(boolean):void, dex: classes3.dex
                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:256)
                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
                            	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
                            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
                            	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                            	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:211)
                            	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:204)
                            	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:318)
                            	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
                            	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
                            	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                            	at java.util.ArrayList.forEach(ArrayList.java:1259)
                            	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                            	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                            	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                            	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                            	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                            	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                            	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                            	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                            	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                            	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                            	at jadx.core.codegen.InsnGen.inlineAnonymousConstructor(InsnGen.java:676)
                            	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:607)
                            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                            	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                            	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                            	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:787)
                            	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:728)
                            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
                            	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
                            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
                            	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                            	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:98)
                            	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:142)
                            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:62)
                            	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                            	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                            	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:211)
                            	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:204)
                            	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:318)
                            	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
                            	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
                            	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                            	at java.util.ArrayList.forEach(ArrayList.java:1259)
                            	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                            	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                            	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                            	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                            	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                            	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                            	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                            	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                            	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                            	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                            	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
                            	at jadx.core.codegen.ClassGen.addInnerClass(ClassGen.java:249)
                            	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:238)
                            	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                            	at java.util.ArrayList.forEach(ArrayList.java:1259)
                            	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                            	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                            	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                            	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                            	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                            	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                            	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                            	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                            	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                            	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                            	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
                            	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:78)
                            	at jadx.core.codegen.CodeGen.wrapCodeGen(CodeGen.java:44)
                            	at jadx.core.codegen.CodeGen.generateJavaCode(CodeGen.java:33)
                            	at jadx.core.codegen.CodeGen.generate(CodeGen.java:21)
                            	at jadx.core.ProcessClass.generateCode(ProcessClass.java:61)
                            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:273)
                            Caused by: jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x0004: CONSTRUCTOR  (r2v0 org.telegram.ui.Adapters.-$$Lambda$DialogsAdapter$DialogsPreloader$1$uLfPHYxzbWAkjtxFDFkg7uuiZ0c) = 
                              (r3v0 'this' org.telegram.ui.Adapters.DialogsAdapter$DialogsPreloader$1 A[THIS])
                              (r4v0 'z' boolean)
                              (wrap: long : 0x0000: IGET  (r0v0 long) = 
                              (r3v0 'this' org.telegram.ui.Adapters.DialogsAdapter$DialogsPreloader$1 A[THIS])
                             org.telegram.ui.Adapters.DialogsAdapter.DialogsPreloader.1.val$dialog_id long)
                             call: org.telegram.ui.Adapters.-$$Lambda$DialogsAdapter$DialogsPreloader$1$uLfPHYxzbWAkjtxFDFkg7uuiZ0c.<init>(org.telegram.ui.Adapters.DialogsAdapter$DialogsPreloader$1, boolean, long):void type: CONSTRUCTOR in method: org.telegram.ui.Adapters.DialogsAdapter.DialogsPreloader.1.onMessagesLoaded(boolean):void, dex: classes3.dex
                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:256)
                            	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                            	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                            	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:787)
                            	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:728)
                            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
                            	... 83 more
                            Caused by: jadx.core.utils.exceptions.JadxRuntimeException: Expected class to be processed at this point, class: org.telegram.ui.Adapters.-$$Lambda$DialogsAdapter$DialogsPreloader$1$uLfPHYxzbWAkjtxFDFkg7uuiZ0c, state: NOT_LOADED
                            	at jadx.core.dex.nodes.ClassNode.ensureProcessed(ClassNode.java:260)
                            	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:606)
                            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                            	... 89 more
                            */
                        /*
                            this = this;
                            long r0 = r2
                            org.telegram.ui.Adapters.-$$Lambda$DialogsAdapter$DialogsPreloader$1$uLfPHYxzbWAkjtxFDFkg7uuiZ0c r2 = new org.telegram.ui.Adapters.-$$Lambda$DialogsAdapter$DialogsPreloader$1$uLfPHYxzbWAkjtxFDFkg7uuiZ0c
                            r2.<init>(r3, r4, r0)
                            org.telegram.messenger.AndroidUtilities.runOnUIThread(r2)
                            return
                        */
                        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Adapters.DialogsAdapter.DialogsPreloader.AnonymousClass1.onMessagesLoaded(boolean):void");
                    }

                    /* access modifiers changed from: private */
                    /* renamed from: lambda$onMessagesLoaded$0 */
                    public /* synthetic */ void lambda$onMessagesLoaded$0$DialogsAdapter$DialogsPreloader$1(boolean z, long j) {
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
                        AndroidUtilities.runOnUIThread(
                        /*  JADX ERROR: Method code generation error
                            jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x0007: INVOKE  
                              (wrap: org.telegram.ui.Adapters.-$$Lambda$DialogsAdapter$DialogsPreloader$1$3F-ZmIc7iTrW5y3bKNV1GMqz07U : 0x0004: CONSTRUCTOR  (r2v0 org.telegram.ui.Adapters.-$$Lambda$DialogsAdapter$DialogsPreloader$1$3F-ZmIc7iTrW5y3bKNV1GMqz07U) = 
                              (r3v0 'this' org.telegram.ui.Adapters.DialogsAdapter$DialogsPreloader$1 A[THIS])
                              (wrap: long : 0x0000: IGET  (r0v0 long) = 
                              (r3v0 'this' org.telegram.ui.Adapters.DialogsAdapter$DialogsPreloader$1 A[THIS])
                             org.telegram.ui.Adapters.DialogsAdapter.DialogsPreloader.1.val$dialog_id long)
                             call: org.telegram.ui.Adapters.-$$Lambda$DialogsAdapter$DialogsPreloader$1$3F-ZmIc7iTrW5y3bKNV1GMqz07U.<init>(org.telegram.ui.Adapters.DialogsAdapter$DialogsPreloader$1, long):void type: CONSTRUCTOR)
                             org.telegram.messenger.AndroidUtilities.runOnUIThread(java.lang.Runnable):void type: STATIC in method: org.telegram.ui.Adapters.DialogsAdapter.DialogsPreloader.1.onError():void, dex: classes3.dex
                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:256)
                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
                            	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
                            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
                            	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                            	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:211)
                            	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:204)
                            	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:318)
                            	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
                            	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
                            	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                            	at java.util.ArrayList.forEach(ArrayList.java:1259)
                            	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                            	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                            	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                            	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                            	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                            	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                            	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                            	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                            	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                            	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                            	at jadx.core.codegen.InsnGen.inlineAnonymousConstructor(InsnGen.java:676)
                            	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:607)
                            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                            	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                            	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                            	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:787)
                            	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:728)
                            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
                            	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
                            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
                            	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                            	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:98)
                            	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:142)
                            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:62)
                            	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                            	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                            	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:211)
                            	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:204)
                            	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:318)
                            	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
                            	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
                            	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                            	at java.util.ArrayList.forEach(ArrayList.java:1259)
                            	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                            	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                            	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                            	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                            	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                            	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                            	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                            	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                            	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                            	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                            	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
                            	at jadx.core.codegen.ClassGen.addInnerClass(ClassGen.java:249)
                            	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:238)
                            	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                            	at java.util.ArrayList.forEach(ArrayList.java:1259)
                            	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                            	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                            	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                            	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                            	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                            	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                            	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                            	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                            	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                            	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                            	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
                            	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:78)
                            	at jadx.core.codegen.CodeGen.wrapCodeGen(CodeGen.java:44)
                            	at jadx.core.codegen.CodeGen.generateJavaCode(CodeGen.java:33)
                            	at jadx.core.codegen.CodeGen.generate(CodeGen.java:21)
                            	at jadx.core.ProcessClass.generateCode(ProcessClass.java:61)
                            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:273)
                            Caused by: jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x0004: CONSTRUCTOR  (r2v0 org.telegram.ui.Adapters.-$$Lambda$DialogsAdapter$DialogsPreloader$1$3F-ZmIc7iTrW5y3bKNV1GMqz07U) = 
                              (r3v0 'this' org.telegram.ui.Adapters.DialogsAdapter$DialogsPreloader$1 A[THIS])
                              (wrap: long : 0x0000: IGET  (r0v0 long) = 
                              (r3v0 'this' org.telegram.ui.Adapters.DialogsAdapter$DialogsPreloader$1 A[THIS])
                             org.telegram.ui.Adapters.DialogsAdapter.DialogsPreloader.1.val$dialog_id long)
                             call: org.telegram.ui.Adapters.-$$Lambda$DialogsAdapter$DialogsPreloader$1$3F-ZmIc7iTrW5y3bKNV1GMqz07U.<init>(org.telegram.ui.Adapters.DialogsAdapter$DialogsPreloader$1, long):void type: CONSTRUCTOR in method: org.telegram.ui.Adapters.DialogsAdapter.DialogsPreloader.1.onError():void, dex: classes3.dex
                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:256)
                            	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                            	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                            	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:787)
                            	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:728)
                            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
                            	... 83 more
                            Caused by: jadx.core.utils.exceptions.JadxRuntimeException: Expected class to be processed at this point, class: org.telegram.ui.Adapters.-$$Lambda$DialogsAdapter$DialogsPreloader$1$3F-ZmIc7iTrW5y3bKNV1GMqz07U, state: NOT_LOADED
                            	at jadx.core.dex.nodes.ClassNode.ensureProcessed(ClassNode.java:260)
                            	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:606)
                            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                            	... 89 more
                            */
                        /*
                            this = this;
                            long r0 = r2
                            org.telegram.ui.Adapters.-$$Lambda$DialogsAdapter$DialogsPreloader$1$3F-ZmIc7iTrW5y3bKNV1GMqz07U r2 = new org.telegram.ui.Adapters.-$$Lambda$DialogsAdapter$DialogsPreloader$1$3F-ZmIc7iTrW5y3bKNV1GMqz07U
                            r2.<init>(r3, r0)
                            org.telegram.messenger.AndroidUtilities.runOnUIThread(r2)
                            return
                        */
                        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Adapters.DialogsAdapter.DialogsPreloader.AnonymousClass1.onError():void");
                    }

                    /* access modifiers changed from: private */
                    /* renamed from: lambda$onError$1 */
                    public /* synthetic */ void lambda$onError$1$DialogsAdapter$DialogsPreloader$1(long j) {
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
            int paddingTop = view.getPaddingTop();
            if (size != 0 && (paddingTop != 0 || z)) {
                int size2 = View.MeasureSpec.getSize(i2);
                if (size2 == 0) {
                    size2 = view.getMeasuredHeight();
                }
                if (size2 == 0) {
                    size2 = (AndroidUtilities.displaySize.y - ActionBar.getCurrentActionBarHeight()) - (Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0);
                }
                int dp = AndroidUtilities.dp(SharedConfig.useThreeLinesLayout ? 78.0f : 72.0f);
                int i4 = (size * dp) + (size - 1);
                if (DialogsAdapter.this.onlineContacts != null) {
                    i4 += (DialogsAdapter.this.onlineContacts.size() * AndroidUtilities.dp(58.0f)) + (DialogsAdapter.this.onlineContacts.size() - 1) + AndroidUtilities.dp(52.0f);
                }
                int i5 = z ? dp + 1 : 0;
                if (i4 < size2) {
                    int i6 = (size2 - i4) + i5;
                    if (paddingTop == 0 || (i6 = i6 - AndroidUtilities.statusBarHeight) >= 0) {
                        i3 = i6;
                    }
                } else {
                    int i7 = i4 - size2;
                    if (i7 < i5) {
                        int i8 = i5 - i7;
                        if (paddingTop != 0) {
                            i8 -= AndroidUtilities.statusBarHeight;
                        }
                        if (i8 >= 0) {
                            i3 = i8;
                        }
                    }
                }
            }
            setMeasuredDimension(View.MeasureSpec.getSize(i), i3);
        }
    }
}
