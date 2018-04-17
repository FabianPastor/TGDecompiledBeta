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
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.beta.R;
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

    public DialogsAdapter(Context context, int type, boolean onlySelect) {
        this.mContext = context;
        this.dialogsType = type;
        this.isOnlySelect = onlySelect;
        boolean z = type == 0 && !onlySelect;
        this.hasHints = z;
        if (onlySelect) {
            this.selectedDialogs = new ArrayList();
        }
    }

    public void setOpenedDialogId(long id) {
        this.openedDialogId = id;
    }

    public boolean hasSelectedDialogs() {
        return (this.selectedDialogs == null || this.selectedDialogs.isEmpty()) ? false : true;
    }

    public void addOrRemoveSelectedDialog(long did, View cell) {
        if (this.selectedDialogs.contains(Long.valueOf(did))) {
            this.selectedDialogs.remove(Long.valueOf(did));
            if (cell instanceof DialogCell) {
                ((DialogCell) cell).setChecked(false, true);
                return;
            }
            return;
        }
        this.selectedDialogs.add(Long.valueOf(did));
        if (cell instanceof DialogCell) {
            ((DialogCell) cell).setChecked(true, true);
        }
    }

    public ArrayList<Long> getSelectedDialogs() {
        return this.selectedDialogs;
    }

    public boolean isDataSetChanged() {
        int current = this.currentCount;
        if (current == getItemCount()) {
            return current == 1;
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
        if (this.dialogsType == 3) {
            return MessagesController.getInstance(this.currentAccount).dialogsForward;
        }
        return null;
    }

    public int getItemCount() {
        int count = getDialogsArray().size();
        if (count == 0 && MessagesController.getInstance(this.currentAccount).loadingDialogs) {
            return 0;
        }
        if (!MessagesController.getInstance(this.currentAccount).dialogsEndReached || count == 0) {
            count++;
        }
        if (this.hasHints) {
            count += 2 + MessagesController.getInstance(this.currentAccount).hintDialogs.size();
        }
        this.currentCount = count;
        return count;
    }

    public TLObject getItem(int i) {
        ArrayList<TL_dialog> arrayList = getDialogsArray();
        if (this.hasHints) {
            int count = MessagesController.getInstance(this.currentAccount).hintDialogs.size();
            if (i < 2 + count) {
                return (TLObject) MessagesController.getInstance(this.currentAccount).hintDialogs.get(i - 1);
            }
            i -= count + 2;
        }
        if (i >= 0) {
            if (i < arrayList.size()) {
                return (TLObject) arrayList.get(i);
            }
        }
        return null;
    }

    public void notifyDataSetChanged() {
        boolean z = (this.dialogsType != 0 || this.isOnlySelect || MessagesController.getInstance(this.currentAccount).hintDialogs.isEmpty()) ? false : true;
        this.hasHints = z;
        super.notifyDataSetChanged();
    }

    public void onViewAttachedToWindow(ViewHolder holder) {
        if (holder.itemView instanceof DialogCell) {
            ((DialogCell) holder.itemView).checkCurrentDialogIndex();
        }
    }

    public boolean isEnabled(ViewHolder holder) {
        int viewType = holder.getItemViewType();
        return (viewType == 1 || viewType == 5 || viewType == 3) ? false : true;
    }

    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view;
        switch (viewType) {
            case 0:
                view = new DialogCell(this.mContext, this.isOnlySelect);
                break;
            case 1:
                view = new LoadingCell(this.mContext);
                break;
            case 2:
                view = new HeaderCell(this.mContext);
                view.setText(LocaleController.getString("RecentlyViewed", R.string.RecentlyViewed));
                TextView textView = new TextView(this.mContext);
                textView.setTextSize(1, 15.0f);
                textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueHeader));
                textView.setText(LocaleController.getString("RecentlyViewedHide", R.string.RecentlyViewedHide));
                int i = 3;
                textView.setGravity((LocaleController.isRTL ? 3 : 5) | 16);
                if (!LocaleController.isRTL) {
                    i = 5;
                }
                view.addView(textView, LayoutHelper.createFrame(-1, -1.0f, i | 48, 17.0f, 15.0f, 17.0f, 0.0f));
                textView.setOnClickListener(new C07711());
                View view2 = view;
                break;
            case 3:
                view = new FrameLayout(this.mContext) {
                    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(12.0f), NUM));
                    }
                };
                view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
                View v = new View(this.mContext);
                v.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                view.addView(v, LayoutHelper.createFrame(-1, -1.0f));
                break;
            case 4:
                view = new DialogMeUrlCell(this.mContext);
                break;
            default:
                view = new DialogsEmptyCell(this.mContext);
                break;
        }
        view.setLayoutParams(new LayoutParams(-1, viewType == 5 ? -1 : -2));
        return new Holder(view);
    }

    public void onBindViewHolder(ViewHolder holder, int i) {
        int itemViewType = holder.getItemViewType();
        if (itemViewType == 0) {
            DialogCell cell = holder.itemView;
            TL_dialog dialog = (TL_dialog) getItem(i);
            if (this.hasHints) {
                i -= 2 + MessagesController.getInstance(this.currentAccount).hintDialogs.size();
            }
            boolean z = true;
            cell.useSeparator = i != getItemCount() - 1;
            if (this.dialogsType == 0 && AndroidUtilities.isTablet()) {
                if (dialog.id != this.openedDialogId) {
                    z = false;
                }
                cell.setDialogSelected(z);
            }
            if (this.selectedDialogs != null) {
                cell.setChecked(this.selectedDialogs.contains(Long.valueOf(dialog.id)), false);
            }
            cell.setDialog(dialog, i, this.dialogsType);
        } else if (itemViewType == 4) {
            holder.itemView.setRecentMeUrl((RecentMeUrl) getItem(i));
        }
    }

    public int getItemViewType(int i) {
        if (this.hasHints) {
            int count = MessagesController.getInstance(this.currentAccount).hintDialogs.size();
            if (i >= 2 + count) {
                i -= 2 + count;
            } else if (i == 0) {
                return 2;
            } else {
                if (i == 1 + count) {
                    return 3;
                }
                return 4;
            }
        }
        if (i != getDialogsArray().size()) {
            return 0;
        }
        if (MessagesController.getInstance(this.currentAccount).dialogsEndReached) {
            return 5;
        }
        return 1;
    }
}
