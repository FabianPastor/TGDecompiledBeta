package org.telegram.ui.Adapters;

import android.content.Context;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.C0446R;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.support.widget.RecyclerView.LayoutParams;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.RecentMeUrl;
import org.telegram.tgnet.TLRPC.TL_dialog;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.DialogCell;
import org.telegram.ui.Cells.DialogMeUrlCell;
import org.telegram.ui.Cells.DialogsEmptyCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.LoadingCell;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;

public class DialogsAdapter extends SelectionAdapter {
    private int currentAccount = UserConfig.selectedAccount;
    private int currentCount;
    private int dialogsType;
    private boolean hasHints;
    private boolean isOnlySelect;
    private Context mContext;
    private long openedDialogId;
    private ArrayList<Long> selectedDialogs;

    /* renamed from: org.telegram.ui.Adapters.DialogsAdapter$1 */
    class C07711 implements OnClickListener {
        C07711() {
        }

        public void onClick(View view) {
            MessagesController.getInstance(DialogsAdapter.this.currentAccount).hintDialogs.clear();
            MessagesController.getGlobalMainSettings().edit().remove("installReferer").commit();
            DialogsAdapter.this.notifyDataSetChanged();
        }
    }

    public DialogsAdapter(Context context, int i, boolean z) {
        this.mContext = context;
        this.dialogsType = i;
        this.isOnlySelect = z;
        context = (i != 0 || z) ? null : true;
        this.hasHints = context;
        if (z) {
            this.selectedDialogs = new ArrayList();
        }
    }

    public void setOpenedDialogId(long j) {
        this.openedDialogId = j;
    }

    public boolean hasSelectedDialogs() {
        return (this.selectedDialogs == null || this.selectedDialogs.isEmpty()) ? false : true;
    }

    public void addOrRemoveSelectedDialog(long j, View view) {
        if (this.selectedDialogs.contains(Long.valueOf(j))) {
            this.selectedDialogs.remove(Long.valueOf(j));
            if ((view instanceof DialogCell) != null) {
                ((DialogCell) view).setChecked(0, true);
                return;
            }
            return;
        }
        this.selectedDialogs.add(Long.valueOf(j));
        if ((view instanceof DialogCell) != null) {
            ((DialogCell) view).setChecked(true, true);
        }
    }

    public ArrayList<Long> getSelectedDialogs() {
        return this.selectedDialogs;
    }

    public boolean isDataSetChanged() {
        int i = this.currentCount;
        if (i == getItemCount()) {
            return i == 1;
        } else {
            return true;
        }
    }

    private ArrayList<TL_dialog> getDialogsArray() {
        if (this.dialogsType == 0) {
            return MessagesController.getInstance(this.currentAccount).dialogs;
        }
        if (this.dialogsType == 1) {
            return MessagesController.getInstance(this.currentAccount).dialogsServerOnly;
        }
        if (this.dialogsType == 2) {
            return MessagesController.getInstance(this.currentAccount).dialogsGroupsOnly;
        }
        return this.dialogsType == 3 ? MessagesController.getInstance(this.currentAccount).dialogsForward : null;
    }

    public int getItemCount() {
        int size = getDialogsArray().size();
        if (size == 0 && MessagesController.getInstance(this.currentAccount).loadingDialogs) {
            return 0;
        }
        if (!MessagesController.getInstance(this.currentAccount).dialogsEndReached || size == 0) {
            size++;
        }
        if (this.hasHints) {
            size += 2 + MessagesController.getInstance(this.currentAccount).hintDialogs.size();
        }
        this.currentCount = size;
        return size;
    }

    public TLObject getItem(int i) {
        ArrayList dialogsArray = getDialogsArray();
        if (this.hasHints) {
            int size = 2 + MessagesController.getInstance(this.currentAccount).hintDialogs.size();
            if (i < size) {
                return (TLObject) MessagesController.getInstance(this.currentAccount).hintDialogs.get(i - 1);
            }
            i -= size;
        }
        if (i >= 0) {
            if (i < dialogsArray.size()) {
                return (TLObject) dialogsArray.get(i);
            }
        }
        return 0;
    }

    public void notifyDataSetChanged() {
        boolean z = (this.dialogsType != 0 || this.isOnlySelect || MessagesController.getInstance(this.currentAccount).hintDialogs.isEmpty()) ? false : true;
        this.hasHints = z;
        super.notifyDataSetChanged();
    }

    public void onViewAttachedToWindow(ViewHolder viewHolder) {
        if (viewHolder.itemView instanceof DialogCell) {
            ((DialogCell) viewHolder.itemView).checkCurrentDialogIndex();
        }
    }

    public boolean isEnabled(ViewHolder viewHolder) {
        viewHolder = viewHolder.getItemViewType();
        return (viewHolder == 1 || viewHolder == 5 || viewHolder == 3) ? false : true;
    }

    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View dialogCell;
        View textView;
        switch (i) {
            case 0:
                dialogCell = new DialogCell(this.mContext, this.isOnlySelect);
                break;
            case 1:
                dialogCell = new LoadingCell(this.mContext);
                break;
            case 2:
                dialogCell = new HeaderCell(this.mContext);
                dialogCell.setText(LocaleController.getString("RecentlyViewed", C0446R.string.RecentlyViewed));
                textView = new TextView(this.mContext);
                textView.setTextSize(1, 15.0f);
                textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueHeader));
                textView.setText(LocaleController.getString("RecentlyViewedHide", C0446R.string.RecentlyViewedHide));
                int i2 = 3;
                textView.setGravity((LocaleController.isRTL ? 3 : 5) | 16);
                if (!LocaleController.isRTL) {
                    i2 = 5;
                }
                dialogCell.addView(textView, LayoutHelper.createFrame(-1, -1.0f, i2 | 48, 17.0f, 15.0f, 17.0f, 0.0f));
                textView.setOnClickListener(new C07711());
                break;
            case 3:
                dialogCell = new FrameLayout(this.mContext) {
                    protected void onMeasure(int i, int i2) {
                        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(i), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(12.0f), NUM));
                    }
                };
                dialogCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
                textView = new View(this.mContext);
                textView.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, C0446R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                dialogCell.addView(textView, LayoutHelper.createFrame(-1, -1.0f));
                break;
            case 4:
                dialogCell = new DialogMeUrlCell(this.mContext);
                break;
            default:
                dialogCell = new DialogsEmptyCell(this.mContext);
                break;
        }
        dialogCell.setLayoutParams(new LayoutParams(-1, i == 5 ? -1 : -2));
        return new Holder(dialogCell);
    }

    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        int itemViewType = viewHolder.getItemViewType();
        if (itemViewType == 0) {
            DialogCell dialogCell = (DialogCell) viewHolder.itemView;
            TL_dialog tL_dialog = (TL_dialog) getItem(i);
            if (this.hasHints) {
                i -= 2 + MessagesController.getInstance(this.currentAccount).hintDialogs.size();
            }
            boolean z = true;
            dialogCell.useSeparator = i != getItemCount() - 1;
            if (this.dialogsType == 0 && AndroidUtilities.isTablet()) {
                if (tL_dialog.id != this.openedDialogId) {
                    z = false;
                }
                dialogCell.setDialogSelected(z);
            }
            if (this.selectedDialogs != null) {
                dialogCell.setChecked(this.selectedDialogs.contains(Long.valueOf(tL_dialog.id)), false);
            }
            dialogCell.setDialog(tL_dialog, i, this.dialogsType);
        } else if (itemViewType == 4) {
            ((DialogMeUrlCell) viewHolder.itemView).setRecentMeUrl((RecentMeUrl) getItem(i));
        }
    }

    public int getItemViewType(int i) {
        if (this.hasHints) {
            int size = MessagesController.getInstance(this.currentAccount).hintDialogs.size();
            int i2 = 2 + size;
            if (i >= i2) {
                i -= i2;
            } else if (i == 0) {
                return 2;
            } else {
                return i == 1 + size ? 3 : 4;
            }
        }
        if (i != getDialogsArray().size()) {
            return 0;
        }
        if (MessagesController.getInstance(this.currentAccount).dialogsEndReached == 0) {
            return 1;
        }
        return 5;
    }
}
