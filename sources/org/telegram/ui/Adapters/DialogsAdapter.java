package org.telegram.ui.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Build.VERSION;
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
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
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
    private Context mContext;
    private long openedDialogId;
    private ArrayList<Long> selectedDialogs;
    private boolean showArchiveHint;
    private boolean showContacts;

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
        this.showContacts = false;
        int size = DialogsActivity.getDialogsArray(this.currentAccount, this.dialogsType, this.folderId, this.dialogsListFrozen).size();
        if (size != 0 || (this.folderId == 0 && !MessagesController.getInstance(this.currentAccount).isLoadingDialogs(this.folderId))) {
            int i = (!MessagesController.getInstance(this.currentAccount).isDialogsEndReached(this.folderId) || size == 0) ? size + 1 : size;
            if (this.hasHints) {
                i += MessagesController.getInstance(this.currentAccount).hintDialogs.size() + 2;
            } else if (this.dialogsType == 0 && size == 0 && this.folderId == 0) {
                if (ContactsController.getInstance(this.currentAccount).contacts.isEmpty() && ContactsController.getInstance(this.currentAccount).isLoadingContacts()) {
                    return 0;
                }
                if (!ContactsController.getInstance(this.currentAccount).contacts.isEmpty()) {
                    i += ContactsController.getInstance(this.currentAccount).contacts.size() + 2;
                    this.showContacts = true;
                }
            }
            if (this.folderId == 1 && this.showArchiveHint) {
                i += 2;
            }
            if (this.folderId == 0 && size != 0) {
                i++;
            }
            this.currentCount = i;
            return i;
        } else if (this.folderId == 1 && this.showArchiveHint) {
            return 2;
        } else {
            return 0;
        }
    }

    public TLObject getItem(int i) {
        if (this.showContacts) {
            i -= 3;
            if (i < 0 || i >= ContactsController.getInstance(this.currentAccount).contacts.size()) {
                return null;
            }
            return MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(((TL_contact) ContactsController.getInstance(this.currentAccount).contacts.get(i)).user_id));
        }
        if (this.showArchiveHint) {
            i -= 2;
        }
        ArrayList dialogsArray = DialogsActivity.getDialogsArray(this.currentAccount, this.dialogsType, this.folderId, this.dialogsListFrozen);
        if (this.hasHints) {
            int size = MessagesController.getInstance(this.currentAccount).hintDialogs.size() + 2;
            if (i < size) {
                return (TLObject) MessagesController.getInstance(this.currentAccount).hintDialogs.get(i - 1);
            }
            i -= size;
        }
        if (i < 0 || i >= dialogsArray.size()) {
            return null;
        }
        return (TLObject) dialogsArray.get(i);
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
            dialogCell.onReorderStateChanged(this.isReordering);
            dialogCell.setDialogIndex(fixPosition(viewHolder.getAdapterPosition()));
            dialogCell.checkCurrentDialogIndex(this.dialogsListFrozen);
            dialogCell.setChecked(this.selectedDialogs.contains(Long.valueOf(dialogCell.getDialogId())), false);
        }
    }

    public boolean isEnabled(ViewHolder viewHolder) {
        int itemViewType = viewHolder.getItemViewType();
        return (itemViewType == 1 || itemViewType == 5 || itemViewType == 3 || itemViewType == 8 || itemViewType == 7 || itemViewType == 9 || itemViewType == 10) ? false : true;
    }

    public /* synthetic */ void lambda$onCreateViewHolder$0$DialogsAdapter(View view) {
        MessagesController.getInstance(this.currentAccount).hintDialogs.clear();
        MessagesController.getGlobalMainSettings().edit().remove("installReferer").commit();
        notifyDataSetChanged();
    }

    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View dialogCell;
        String str = "windowBackgroundGrayShadow";
        String str2 = "windowBackgroundGray";
        switch (i) {
            case 0:
                dialogCell = new DialogCell(this.mContext, true);
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
                textView.setOnClickListener(new -$$Lambda$DialogsAdapter$9rZHEUZrGiCwZ807CAFufwsMqa0(this));
                break;
            case 3:
                View anonymousClass1 = new FrameLayout(this.mContext) {
                    /* Access modifiers changed, original: protected */
                    public void onMeasure(int i, int i2) {
                        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(i), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(12.0f), NUM));
                    }
                };
                anonymousClass1.setBackgroundColor(Theme.getColor(str2));
                View view = new View(this.mContext);
                view.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, str));
                anonymousClass1.addView(view, LayoutHelper.createFrame(-1, -1.0f));
                dialogCell = anonymousClass1;
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
                View shadowSectionCell = new ShadowSectionCell(this.mContext);
                CombinedDrawable combinedDrawable = new CombinedDrawable(new ColorDrawable(Theme.getColor(str2)), Theme.getThemedDrawable(this.mContext, NUM, str));
                combinedDrawable.setFullsize(true);
                shadowSectionCell.setBackgroundDrawable(combinedDrawable);
                dialogCell = shadowSectionCell;
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
        dialogCell.setLayoutParams(new LayoutParams(-1, i == 5 ? -1 : -2));
        return new Holder(dialogCell);
    }

    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        int itemViewType = viewHolder.getItemViewType();
        if (itemViewType == 0) {
            DialogCell dialogCell = (DialogCell) viewHolder.itemView;
            Dialog dialog = (Dialog) getItem(i);
            Dialog dialog2 = (Dialog) getItem(i + 1);
            boolean z = true;
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
            ((DialogsEmptyCell) viewHolder.itemView).setType(this.showContacts);
        } else if (itemViewType == 6) {
            ((UserCell) viewHolder.itemView).setData(MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(((TL_contact) ContactsController.getInstance(this.currentAccount).contacts.get(i - 3)).user_id)), null, null, 0);
        }
    }

    public int getItemViewType(int i) {
        if (!this.showContacts) {
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
}
