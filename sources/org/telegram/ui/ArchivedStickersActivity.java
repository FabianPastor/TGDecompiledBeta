package org.telegram.ui;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.ArchivedStickersActivity;
import org.telegram.ui.Cells.ArchivedStickerSetCell;
import org.telegram.ui.Cells.LoadingCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.StickersAlert;
import org.telegram.ui.Components.Switch;

public class ArchivedStickersActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private int currentType;
    private EmptyTextProgressView emptyView;
    /* access modifiers changed from: private */
    public boolean endReached;
    private boolean firstLoaded;
    /* access modifiers changed from: private */
    public LinearLayoutManager layoutManager;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    /* access modifiers changed from: private */
    public boolean loadingStickers;
    /* access modifiers changed from: private */
    public int rowCount;
    /* access modifiers changed from: private */
    public ArrayList<TLRPC.StickerSetCovered> sets = new ArrayList<>();
    /* access modifiers changed from: private */
    public int stickersEndRow;
    /* access modifiers changed from: private */
    public int stickersLoadingRow;
    /* access modifiers changed from: private */
    public int stickersShadowRow;
    /* access modifiers changed from: private */
    public int stickersStartRow;

    public ArchivedStickersActivity(int i) {
        this.currentType = i;
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        getStickers();
        updateRows();
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.needReloadArchivedStickers);
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.needReloadArchivedStickers);
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setAllowOverlayTitle(true);
        if (this.currentType == 0) {
            this.actionBar.setTitle(LocaleController.getString("ArchivedStickers", NUM));
        } else {
            this.actionBar.setTitle(LocaleController.getString("ArchivedMasks", NUM));
        }
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    ArchivedStickersActivity.this.finishFragment();
                }
            }
        });
        this.listAdapter = new ListAdapter(context);
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        frameLayout.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
        this.emptyView = new EmptyTextProgressView(context);
        if (this.currentType == 0) {
            this.emptyView.setText(LocaleController.getString("ArchivedStickersEmpty", NUM));
        } else {
            this.emptyView.setText(LocaleController.getString("ArchivedMasksEmpty", NUM));
        }
        frameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f));
        if (this.loadingStickers) {
            this.emptyView.showProgress();
        } else {
            this.emptyView.showTextView();
        }
        this.listView = new RecyclerListView(context);
        this.listView.setFocusable(true);
        this.listView.setEmptyView(this.emptyView);
        RecyclerListView recyclerListView = this.listView;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, 1, false);
        this.layoutManager = linearLayoutManager;
        recyclerListView.setLayoutManager(linearLayoutManager);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setAdapter(this.listAdapter);
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
            public final void onItemClick(View view, int i) {
                ArchivedStickersActivity.this.lambda$createView$0$ArchivedStickersActivity(view, i);
            }
        });
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                if (!ArchivedStickersActivity.this.loadingStickers && !ArchivedStickersActivity.this.endReached && ArchivedStickersActivity.this.layoutManager.findLastVisibleItemPosition() > ArchivedStickersActivity.this.stickersLoadingRow - 2) {
                    ArchivedStickersActivity.this.getStickers();
                }
            }
        });
        return this.fragmentView;
    }

    public /* synthetic */ void lambda$createView$0$ArchivedStickersActivity(final View view, int i) {
        TLRPC.InputStickerSet inputStickerSet;
        if (i >= this.stickersStartRow && i < this.stickersEndRow && getParentActivity() != null) {
            TLRPC.StickerSetCovered stickerSetCovered = this.sets.get(i);
            if (stickerSetCovered.set.id != 0) {
                inputStickerSet = new TLRPC.TL_inputStickerSetID();
                inputStickerSet.id = stickerSetCovered.set.id;
            } else {
                inputStickerSet = new TLRPC.TL_inputStickerSetShortName();
                inputStickerSet.short_name = stickerSetCovered.set.short_name;
            }
            TLRPC.InputStickerSet inputStickerSet2 = inputStickerSet;
            inputStickerSet2.access_hash = stickerSetCovered.set.access_hash;
            StickersAlert stickersAlert = new StickersAlert(getParentActivity(), this, inputStickerSet2, (TLRPC.TL_messages_stickerSet) null, (StickersAlert.StickersAlertDelegate) null);
            stickersAlert.setInstallDelegate(new StickersAlert.StickersAlertInstallDelegate() {
                public void onStickerSetInstalled() {
                    ((ArchivedStickerSetCell) view).setChecked(true);
                }

                public void onStickerSetUninstalled() {
                    ((ArchivedStickerSetCell) view).setChecked(false);
                }
            });
            showDialog(stickersAlert);
        }
    }

    private void updateRows() {
        this.rowCount = 0;
        if (!this.sets.isEmpty()) {
            int i = this.rowCount;
            this.stickersStartRow = i;
            this.stickersEndRow = i + this.sets.size();
            this.rowCount += this.sets.size();
            if (!this.endReached) {
                int i2 = this.rowCount;
                this.rowCount = i2 + 1;
                this.stickersLoadingRow = i2;
                this.stickersShadowRow = -1;
            } else {
                int i3 = this.rowCount;
                this.rowCount = i3 + 1;
                this.stickersShadowRow = i3;
                this.stickersLoadingRow = -1;
            }
        } else {
            this.stickersStartRow = -1;
            this.stickersEndRow = -1;
            this.stickersLoadingRow = -1;
            this.stickersShadowRow = -1;
        }
        ListAdapter listAdapter2 = this.listAdapter;
        if (listAdapter2 != null) {
            listAdapter2.notifyDataSetChanged();
        }
    }

    /* access modifiers changed from: private */
    public void getStickers() {
        long j;
        if (!this.loadingStickers && !this.endReached) {
            boolean z = true;
            this.loadingStickers = true;
            EmptyTextProgressView emptyTextProgressView = this.emptyView;
            if (emptyTextProgressView != null && !this.firstLoaded) {
                emptyTextProgressView.showProgress();
            }
            ListAdapter listAdapter2 = this.listAdapter;
            if (listAdapter2 != null) {
                listAdapter2.notifyDataSetChanged();
            }
            TLRPC.TL_messages_getArchivedStickers tL_messages_getArchivedStickers = new TLRPC.TL_messages_getArchivedStickers();
            if (this.sets.isEmpty()) {
                j = 0;
            } else {
                ArrayList<TLRPC.StickerSetCovered> arrayList = this.sets;
                j = arrayList.get(arrayList.size() - 1).set.id;
            }
            tL_messages_getArchivedStickers.offset_id = j;
            tL_messages_getArchivedStickers.limit = 15;
            if (this.currentType != 1) {
                z = false;
            }
            tL_messages_getArchivedStickers.masks = z;
            ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_getArchivedStickers, new RequestDelegate() {
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    ArchivedStickersActivity.this.lambda$getStickers$2$ArchivedStickersActivity(tLObject, tL_error);
                }
            }), this.classGuid);
        }
    }

    public /* synthetic */ void lambda$getStickers$2$ArchivedStickersActivity(TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tL_error, tLObject) {
            private final /* synthetic */ TLRPC.TL_error f$1;
            private final /* synthetic */ TLObject f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                ArchivedStickersActivity.this.lambda$null$1$ArchivedStickersActivity(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$null$1$ArchivedStickersActivity(TLRPC.TL_error tL_error, TLObject tLObject) {
        if (tL_error == null) {
            TLRPC.TL_messages_archivedStickers tL_messages_archivedStickers = (TLRPC.TL_messages_archivedStickers) tLObject;
            this.sets.addAll(tL_messages_archivedStickers.sets);
            this.endReached = tL_messages_archivedStickers.sets.size() != 15;
            this.loadingStickers = false;
            this.firstLoaded = true;
            EmptyTextProgressView emptyTextProgressView = this.emptyView;
            if (emptyTextProgressView != null) {
                emptyTextProgressView.showTextView();
            }
            updateRows();
        }
    }

    public void onResume() {
        super.onResume();
        ListAdapter listAdapter2 = this.listAdapter;
        if (listAdapter2 != null) {
            listAdapter2.notifyDataSetChanged();
        }
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.needReloadArchivedStickers) {
            this.firstLoaded = false;
            this.endReached = false;
            this.sets.clear();
            updateRows();
            EmptyTextProgressView emptyTextProgressView = this.emptyView;
            if (emptyTextProgressView != null) {
                emptyTextProgressView.showProgress();
            }
            getStickers();
        }
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public int getItemCount() {
            return ArchivedStickersActivity.this.rowCount;
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            if (getItemViewType(i) == 0) {
                ArchivedStickerSetCell archivedStickerSetCell = (ArchivedStickerSetCell) viewHolder.itemView;
                archivedStickerSetCell.setTag(Integer.valueOf(i));
                TLRPC.StickerSetCovered stickerSetCovered = (TLRPC.StickerSetCovered) ArchivedStickersActivity.this.sets.get(i);
                boolean z = true;
                if (i == ArchivedStickersActivity.this.sets.size() - 1) {
                    z = false;
                }
                archivedStickerSetCell.setStickersSet(stickerSetCovered, z);
                archivedStickerSetCell.setChecked(MediaDataController.getInstance(ArchivedStickersActivity.this.currentAccount).isStickerPackInstalled(stickerSetCovered.set.id));
            }
        }

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return viewHolder.getItemViewType() == 0;
        }

        public /* synthetic */ void lambda$onCreateViewHolder$0$ArchivedStickersActivity$ListAdapter(Switch switchR, boolean z) {
            int intValue = ((Integer) ((ArchivedStickerSetCell) switchR.getParent()).getTag()).intValue();
            if (intValue < ArchivedStickersActivity.this.sets.size()) {
                MediaDataController.getInstance(ArchivedStickersActivity.this.currentAccount).removeStickersSet(ArchivedStickersActivity.this.getParentActivity(), ((TLRPC.StickerSetCovered) ArchivedStickersActivity.this.sets.get(intValue)).set, !z ? 1 : 2, ArchivedStickersActivity.this, false);
            }
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view;
            if (i == 0) {
                ArchivedStickerSetCell archivedStickerSetCell = new ArchivedStickerSetCell(this.mContext, true);
                archivedStickerSetCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                archivedStickerSetCell.setOnCheckClick(new Switch.OnCheckedChangeListener() {
                    public final void onCheckedChanged(Switch switchR, boolean z) {
                        ArchivedStickersActivity.ListAdapter.this.lambda$onCreateViewHolder$0$ArchivedStickersActivity$ListAdapter(switchR, z);
                    }
                });
                view = archivedStickerSetCell;
            } else if (i == 1) {
                view = new LoadingCell(this.mContext);
                view.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
            } else if (i != 2) {
                view = null;
            } else {
                view = new TextInfoPrivacyCell(this.mContext);
                view.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
            }
            view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(view);
        }

        public int getItemViewType(int i) {
            if (i >= ArchivedStickersActivity.this.stickersStartRow && i < ArchivedStickersActivity.this.stickersEndRow) {
                return 0;
            }
            if (i == ArchivedStickersActivity.this.stickersLoadingRow) {
                return 1;
            }
            if (i == ArchivedStickersActivity.this.stickersShadowRow) {
                return 2;
            }
            return 0;
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        return new ThemeDescription[]{new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{ArchivedStickerSetCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"), new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{LoadingCell.class, TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"), new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultIcon"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultTitle"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSelector"), new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"), new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "divider"), new ThemeDescription(this.emptyView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "emptyListPlaceholder"), new ThemeDescription(this.emptyView, ThemeDescription.FLAG_PROGRESSBAR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "progressCircle"), new ThemeDescription((View) this.listView, 0, new Class[]{LoadingCell.class}, new String[]{"progressBar"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "progressCircle"), new ThemeDescription((View) this.listView, 0, new Class[]{ArchivedStickerSetCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"), new ThemeDescription((View) this.listView, 0, new Class[]{ArchivedStickerSetCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText2"), new ThemeDescription((View) this.listView, 0, new Class[]{ArchivedStickerSetCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrack"), new ThemeDescription((View) this.listView, 0, new Class[]{ArchivedStickerSetCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrackChecked")};
    }
}
