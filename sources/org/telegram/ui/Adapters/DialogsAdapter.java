package org.telegram.ui.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Build.VERSION;
import android.os.SystemClock;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView.LayoutParams;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import androidx.viewpager.widget.ViewPager;
import java.util.ArrayList;
import java.util.Collections;
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
import org.telegram.tgnet.TLRPC.Dialog;
import org.telegram.tgnet.TLRPC.RecentMeUrl;
import org.telegram.tgnet.TLRPC.TL_contact;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.ArchiveHintCell;
import org.telegram.ui.Cells.DialogCell;
import org.telegram.ui.Cells.DialogMeUrlCell;
import org.telegram.ui.Cells.DialogsEmptyCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.LoadingCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.UserCell;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.PullForegroundDrawable;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;
import org.telegram.ui.DialogsActivity;

public class DialogsAdapter extends SelectionAdapter {
    private ArchiveHintCell archiveHintCell;
    private int currentAccount = UserConfig.selectedAccount;
    private int currentCount;
    private boolean dialogsListFrozen;
    private int dialogsType;
    private int folderId;
    private boolean hasHints;
    private boolean isOnlySelect;
    private boolean isReordering;
    private long lastSortTime;
    private Context mContext;
    private ArrayList<TL_contact> onlineContacts;
    private long openedDialogId;
    private int prevContactsCount;
    private PullForegroundDrawable pullForegroundDrawable;
    private ArrayList<Long> selectedDialogs;
    private boolean showArchiveHint;

    public DialogsAdapter(Context context, int i, int i2, boolean z) {
        this.mContext = context;
        this.dialogsType = i;
        this.folderId = i2;
        this.isOnlySelect = z;
        boolean z2 = i2 == 0 && i == 0 && !z;
        this.hasHints = z2;
        this.selectedDialogs = new ArrayList();
        if (this.folderId == 1) {
            SharedPreferences globalMainSettings = MessagesController.getGlobalMainSettings();
            String str = "archivehint";
            this.showArchiveHint = globalMainSettings.getBoolean(str, true);
            globalMainSettings.edit().putBoolean(str, false).commit();
            if (this.showArchiveHint) {
                this.archiveHintCell = new ArchiveHintCell(context);
            }
        }
    }

    public void setOpenedDialogId(long j) {
        this.openedDialogId = j;
    }

    public boolean hasSelectedDialogs() {
        ArrayList arrayList = this.selectedDialogs;
        return (arrayList == null || arrayList.isEmpty()) ? false : true;
    }

    public boolean addOrRemoveSelectedDialog(long j, View view) {
        if (this.selectedDialogs.contains(Long.valueOf(j))) {
            this.selectedDialogs.remove(Long.valueOf(j));
            if (view instanceof DialogCell) {
                ((DialogCell) view).setChecked(false, true);
            }
            return false;
        }
        this.selectedDialogs.add(Long.valueOf(j));
        if (view instanceof DialogCell) {
            ((DialogCell) view).setChecked(true, true);
        }
        return true;
    }

    public ArrayList<Long> getSelectedDialogs() {
        return this.selectedDialogs;
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

    public int getItemCount() {
        int size = DialogsActivity.getDialogsArray(this.currentAccount, this.dialogsType, this.folderId, this.dialogsListFrozen).size();
        int i = 0;
        if (size != 0 || (this.folderId == 0 && !MessagesController.getInstance(this.currentAccount).isLoadingDialogs(this.folderId))) {
            int i2 = (!MessagesController.getInstance(this.currentAccount).isDialogsEndReached(this.folderId) || size == 0) ? size + 1 : size;
            if (this.hasHints) {
                i2 += MessagesController.getInstance(this.currentAccount).hintDialogs.size() + 2;
            } else if (this.dialogsType == 0 && size == 0 && this.folderId == 0) {
                if (ContactsController.getInstance(this.currentAccount).contacts.isEmpty() && ContactsController.getInstance(this.currentAccount).isLoadingContacts()) {
                    this.onlineContacts = null;
                    this.currentCount = 0;
                    return 0;
                } else if (!ContactsController.getInstance(this.currentAccount).contacts.isEmpty()) {
                    if (this.onlineContacts == null || this.prevContactsCount != ContactsController.getInstance(this.currentAccount).contacts.size()) {
                        this.onlineContacts = new ArrayList(ContactsController.getInstance(this.currentAccount).contacts);
                        this.prevContactsCount = this.onlineContacts.size();
                        int i3 = UserConfig.getInstance(this.currentAccount).clientUserId;
                        int size2 = this.onlineContacts.size();
                        for (int i4 = 0; i4 < size2; i4++) {
                            if (((TL_contact) this.onlineContacts.get(i4)).user_id == i3) {
                                this.onlineContacts.remove(i4);
                                break;
                            }
                        }
                        sortOnlineContacts(false);
                    }
                    i2 += this.onlineContacts.size() + 2;
                    i = 1;
                }
            }
            if (i == 0 && this.onlineContacts != null) {
                this.onlineContacts = null;
            }
            if (this.folderId == 1 && this.showArchiveHint) {
                i2 += 2;
            }
            if (this.folderId == 0 && size != 0) {
                i2++;
            }
            this.currentCount = i2;
            return i2;
        }
        this.onlineContacts = null;
        if (this.folderId == 1 && this.showArchiveHint) {
            this.currentCount = 2;
            return 2;
        }
        this.currentCount = 0;
        return 0;
    }

    public TLObject getItem(int i) {
        ArrayList arrayList = this.onlineContacts;
        if (arrayList != null) {
            i -= 3;
            if (i < 0 || i >= arrayList.size()) {
                return null;
            }
            return MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(((TL_contact) this.onlineContacts.get(i)).user_id));
        }
        if (this.showArchiveHint) {
            i -= 2;
        }
        arrayList = DialogsActivity.getDialogsArray(this.currentAccount, this.dialogsType, this.folderId, this.dialogsListFrozen);
        if (this.hasHints) {
            int size = MessagesController.getInstance(this.currentAccount).hintDialogs.size() + 2;
            if (i < size) {
                return (TLObject) MessagesController.getInstance(this.currentAccount).hintDialogs.get(i - 1);
            }
            i -= size;
        }
        if (i < 0 || i >= arrayList.size()) {
            return null;
        }
        return (TLObject) arrayList.get(i);
    }

    public void sortOnlineContacts(boolean z) {
        if (this.onlineContacts == null) {
            return;
        }
        if (!z || SystemClock.uptimeMillis() - this.lastSortTime >= 2000) {
            this.lastSortTime = SystemClock.uptimeMillis();
            try {
                int currentTime = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
                Collections.sort(this.onlineContacts, new -$$Lambda$DialogsAdapter$Xl2Fm6ABchAKa8HnZJtWg3ArO0E(MessagesController.getInstance(this.currentAccount), currentTime));
                if (z) {
                    notifyDataSetChanged();
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:10:0x002b  */
    /* JADX WARNING: Removed duplicated region for block: B:19:0x003e A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x0049 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x0054 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x005d A:{SKIP} */
    static /* synthetic */ int lambda$sortOnlineContacts$0(org.telegram.messenger.MessagesController r2, int r3, org.telegram.tgnet.TLRPC.TL_contact r4, org.telegram.tgnet.TLRPC.TL_contact r5) {
        /*
        r5 = r5.user_id;
        r5 = java.lang.Integer.valueOf(r5);
        r5 = r2.getUser(r5);
        r4 = r4.user_id;
        r4 = java.lang.Integer.valueOf(r4);
        r2 = r2.getUser(r4);
        r4 = 50000; // 0xCLASSNAME float:7.0065E-41 double:2.47033E-319;
        r0 = 0;
        if (r5 == 0) goto L_0x0028;
    L_0x001a:
        r1 = r5.self;
        if (r1 == 0) goto L_0x0021;
    L_0x001e:
        r5 = r3 + r4;
        goto L_0x0029;
    L_0x0021:
        r5 = r5.status;
        if (r5 == 0) goto L_0x0028;
    L_0x0025:
        r5 = r5.expires;
        goto L_0x0029;
    L_0x0028:
        r5 = 0;
    L_0x0029:
        if (r2 == 0) goto L_0x0039;
    L_0x002b:
        r1 = r2.self;
        if (r1 == 0) goto L_0x0032;
    L_0x002f:
        r2 = r3 + r4;
        goto L_0x003a;
    L_0x0032:
        r2 = r2.status;
        if (r2 == 0) goto L_0x0039;
    L_0x0036:
        r2 = r2.expires;
        goto L_0x003a;
    L_0x0039:
        r2 = 0;
    L_0x003a:
        r3 = -1;
        r4 = 1;
        if (r5 <= 0) goto L_0x0047;
    L_0x003e:
        if (r2 <= 0) goto L_0x0047;
    L_0x0040:
        if (r5 <= r2) goto L_0x0043;
    L_0x0042:
        return r4;
    L_0x0043:
        if (r5 >= r2) goto L_0x0046;
    L_0x0045:
        return r3;
    L_0x0046:
        return r0;
    L_0x0047:
        if (r5 >= 0) goto L_0x0052;
    L_0x0049:
        if (r2 >= 0) goto L_0x0052;
    L_0x004b:
        if (r5 <= r2) goto L_0x004e;
    L_0x004d:
        return r4;
    L_0x004e:
        if (r5 >= r2) goto L_0x0051;
    L_0x0050:
        return r3;
    L_0x0051:
        return r0;
    L_0x0052:
        if (r5 >= 0) goto L_0x0056;
    L_0x0054:
        if (r2 > 0) goto L_0x005a;
    L_0x0056:
        if (r5 != 0) goto L_0x005b;
    L_0x0058:
        if (r2 == 0) goto L_0x005b;
    L_0x005a:
        return r3;
    L_0x005b:
        if (r2 >= 0) goto L_0x005f;
    L_0x005d:
        if (r5 > 0) goto L_0x0063;
    L_0x005f:
        if (r2 != 0) goto L_0x0064;
    L_0x0061:
        if (r5 == 0) goto L_0x0064;
    L_0x0063:
        return r4;
    L_0x0064:
        return r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Adapters.DialogsAdapter.lambda$sortOnlineContacts$0(org.telegram.messenger.MessagesController, int, org.telegram.tgnet.TLRPC$TL_contact, org.telegram.tgnet.TLRPC$TL_contact):int");
    }

    public void setDialogsListFrozen(boolean z) {
        this.dialogsListFrozen = z;
    }

    public ViewPager getArchiveHintCellPager() {
        ArchiveHintCell archiveHintCell = this.archiveHintCell;
        return archiveHintCell != null ? archiveHintCell.getViewPager() : null;
    }

    public void notifyDataSetChanged() {
        boolean z = this.folderId == 0 && this.dialogsType == 0 && !this.isOnlySelect && !MessagesController.getInstance(this.currentAccount).hintDialogs.isEmpty();
        this.hasHints = z;
        super.notifyDataSetChanged();
    }

    public void onViewAttachedToWindow(ViewHolder viewHolder) {
        View view = viewHolder.itemView;
        if (view instanceof DialogCell) {
            DialogCell dialogCell = (DialogCell) view;
            dialogCell.onReorderStateChanged(this.isReordering, false);
            dialogCell.setDialogIndex(fixPosition(viewHolder.getAdapterPosition()));
            dialogCell.checkCurrentDialogIndex(this.dialogsListFrozen);
            dialogCell.setChecked(this.selectedDialogs.contains(Long.valueOf(dialogCell.getDialogId())), false);
        }
    }

    public boolean isEnabled(ViewHolder viewHolder) {
        int itemViewType = viewHolder.getItemViewType();
        return (itemViewType == 1 || itemViewType == 5 || itemViewType == 3 || itemViewType == 8 || itemViewType == 7 || itemViewType == 9 || itemViewType == 10) ? false : true;
    }

    public /* synthetic */ void lambda$onCreateViewHolder$1$DialogsAdapter(View view) {
        MessagesController.getInstance(this.currentAccount).hintDialogs.clear();
        MessagesController.getGlobalMainSettings().edit().remove("installReferer").commit();
        notifyDataSetChanged();
    }

    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View dialogCell;
        View anonymousClass1;
        String str = "windowBackgroundGrayShadow";
        String str2 = "windowBackgroundGray";
        switch (i) {
            case 0:
                dialogCell = new DialogCell(this.mContext, true, false);
                dialogCell.setArchivedPullAnimation(this.pullForegroundDrawable);
                break;
            case 1:
                dialogCell = new LoadingCell(this.mContext);
                break;
            case 2:
                dialogCell = new HeaderCell(this.mContext);
                dialogCell.setText(LocaleController.getString("RecentlyViewed", NUM));
                TextView textView = new TextView(this.mContext);
                textView.setTextSize(1, 15.0f);
                textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                textView.setTextColor(Theme.getColor("windowBackgroundWhiteBlueHeader"));
                textView.setText(LocaleController.getString("RecentlyViewedHide", NUM));
                int i2 = 3;
                textView.setGravity((LocaleController.isRTL ? 3 : 5) | 16);
                if (!LocaleController.isRTL) {
                    i2 = 5;
                }
                dialogCell.addView(textView, LayoutHelper.createFrame(-1, -1.0f, i2 | 48, 17.0f, 15.0f, 17.0f, 0.0f));
                textView.setOnClickListener(new -$$Lambda$DialogsAdapter$DWIibGxp-Clk-7MRxEqoD1mmox4(this));
                break;
            case 3:
                anonymousClass1 = new FrameLayout(this.mContext) {
                    /* Access modifiers changed, original: protected */
                    public void onMeasure(int i, int i2) {
                        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(i), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(12.0f), NUM));
                    }
                };
                anonymousClass1.setBackgroundColor(Theme.getColor(str2));
                View view = new View(this.mContext);
                view.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, str));
                anonymousClass1.addView(view, LayoutHelper.createFrame(-1, -1.0f));
                break;
            case 4:
                dialogCell = new DialogMeUrlCell(this.mContext);
                break;
            case 5:
                dialogCell = new DialogsEmptyCell(this.mContext);
                break;
            case 6:
                dialogCell = new UserCell(this.mContext, 8, 0, false);
                break;
            case 7:
                dialogCell = new HeaderCell(this.mContext);
                dialogCell.setText(LocaleController.getString("YourContacts", NUM));
                break;
            case 8:
                anonymousClass1 = new ShadowSectionCell(this.mContext);
                CombinedDrawable combinedDrawable = new CombinedDrawable(new ColorDrawable(Theme.getColor(str2)), Theme.getThemedDrawable(this.mContext, NUM, str));
                combinedDrawable.setFullsize(true);
                anonymousClass1.setBackgroundDrawable(combinedDrawable);
                break;
            case 9:
                dialogCell = this.archiveHintCell;
                if (dialogCell.getParent() != null) {
                    ((ViewGroup) this.archiveHintCell.getParent()).removeView(this.archiveHintCell);
                    break;
                }
                break;
            default:
                dialogCell = new View(this.mContext) {
                    /* Access modifiers changed, original: protected */
                    public void onMeasure(int i, int i2) {
                        int size = DialogsActivity.getDialogsArray(DialogsAdapter.this.currentAccount, DialogsAdapter.this.dialogsType, DialogsAdapter.this.folderId, DialogsAdapter.this.dialogsListFrozen).size();
                        int i3 = 0;
                        Object obj = MessagesController.getInstance(DialogsAdapter.this.currentAccount).dialogs_dict.get(DialogObject.makeFolderDialogId(1)) != null ? 1 : null;
                        if (!(size == 0 || obj == null)) {
                            i2 = MeasureSpec.getSize(i2);
                            if (i2 == 0) {
                                View view = (View) getParent();
                                if (view != null) {
                                    i2 = view.getMeasuredHeight();
                                }
                            }
                            if (i2 == 0) {
                                i2 = (AndroidUtilities.displaySize.y - ActionBar.getCurrentActionBarHeight()) - (VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0);
                            }
                            int dp = AndroidUtilities.dp(SharedConfig.useThreeLinesLayout ? 78.0f : 72.0f);
                            int i4 = (size * dp) + (size - 1);
                            if (i4 < i2) {
                                i3 = ((i2 - i4) + dp) + 1;
                            } else {
                                i4 -= i2;
                                dp++;
                                if (i4 < dp) {
                                    i3 = dp - i4;
                                }
                            }
                        }
                        setMeasuredDimension(MeasureSpec.getSize(i), i3);
                    }
                };
                break;
        }
        dialogCell = anonymousClass1;
        dialogCell.setLayoutParams(new LayoutParams(-1, i == 5 ? -1 : -2));
        return new Holder(dialogCell);
    }

    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        int itemViewType = viewHolder.getItemViewType();
        int i2 = 0;
        boolean z = true;
        if (itemViewType == 0) {
            DialogCell dialogCell = (DialogCell) viewHolder.itemView;
            Dialog dialog = (Dialog) getItem(i);
            Dialog dialog2 = (Dialog) getItem(i + 1);
            if (this.folderId == 0) {
                dialogCell.useSeparator = i != getItemCount() + -2;
            } else {
                dialogCell.useSeparator = i != getItemCount() - 1;
            }
            boolean z2 = (!dialog.pinned || dialog2 == null || dialog2.pinned) ? false : true;
            dialogCell.fullSeparator = z2;
            if (this.dialogsType == 0 && AndroidUtilities.isTablet()) {
                if (dialog.id != this.openedDialogId) {
                    z = false;
                }
                dialogCell.setDialogSelected(z);
            }
            dialogCell.setChecked(this.selectedDialogs.contains(Long.valueOf(dialog.id)), false);
            dialogCell.setDialog(dialog, this.dialogsType, this.folderId);
        } else if (itemViewType == 4) {
            ((DialogMeUrlCell) viewHolder.itemView).setRecentMeUrl((RecentMeUrl) getItem(i));
        } else if (itemViewType == 5) {
            DialogsEmptyCell dialogsEmptyCell = (DialogsEmptyCell) viewHolder.itemView;
            if (this.onlineContacts != null) {
                i2 = 1;
            }
            dialogsEmptyCell.setType(i2);
        } else if (itemViewType == 6) {
            ((UserCell) viewHolder.itemView).setData(MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(((TL_contact) this.onlineContacts.get(i - 3)).user_id)), null, null, 0);
        }
    }

    public int getItemViewType(int i) {
        if (this.onlineContacts == null) {
            int size;
            if (this.hasHints) {
                size = MessagesController.getInstance(this.currentAccount).hintDialogs.size();
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
            size = DialogsActivity.getDialogsArray(this.currentAccount, this.dialogsType, this.folderId, this.dialogsListFrozen).size();
            if (i == size) {
                if (!MessagesController.getInstance(this.currentAccount).isDialogsEndReached(this.folderId)) {
                    return 1;
                }
                if (size == 0) {
                    return 5;
                }
                return 10;
            } else if (i > size) {
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
        ArrayList dialogsArray = DialogsActivity.getDialogsArray(this.currentAccount, this.dialogsType, this.folderId, false);
        int fixPosition = fixPosition(i);
        int fixPosition2 = fixPosition(i2);
        Dialog dialog = (Dialog) dialogsArray.get(fixPosition);
        Dialog dialog2 = (Dialog) dialogsArray.get(fixPosition2);
        int i3 = dialog.pinnedNum;
        dialog.pinnedNum = dialog2.pinnedNum;
        dialog2.pinnedNum = i3;
        Collections.swap(dialogsArray, fixPosition, fixPosition2);
        super.notifyItemMoved(i, i2);
    }

    public void setArchivedPullDrawable(PullForegroundDrawable pullForegroundDrawable) {
        this.pullForegroundDrawable = pullForegroundDrawable;
    }
}
