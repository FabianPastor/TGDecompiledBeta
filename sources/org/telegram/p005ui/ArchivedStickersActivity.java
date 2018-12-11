package org.telegram.p005ui;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DataQuery;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.support.widget.RecyclerView.LayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.LayoutParams;
import org.telegram.messenger.support.widget.RecyclerView.OnScrollListener;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.p005ui.ActionBar.BaseFragment;
import org.telegram.p005ui.ActionBar.CLASSNAMEActionBar.ActionBarMenuOnItemClick;
import org.telegram.p005ui.ActionBar.Theme;
import org.telegram.p005ui.ActionBar.ThemeDescription;
import org.telegram.p005ui.Cells.ArchivedStickerSetCell;
import org.telegram.p005ui.Cells.LoadingCell;
import org.telegram.p005ui.Cells.TextInfoPrivacyCell;
import org.telegram.p005ui.Components.EmptyTextProgressView;
import org.telegram.p005ui.Components.LayoutHelper;
import org.telegram.p005ui.Components.RecyclerListView;
import org.telegram.p005ui.Components.RecyclerListView.Holder;
import org.telegram.p005ui.Components.RecyclerListView.SelectionAdapter;
import org.telegram.p005ui.Components.StickersAlert;
import org.telegram.p005ui.Components.StickersAlert.StickersAlertInstallDelegate;
import org.telegram.p005ui.Components.Switch;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.InputStickerSet;
import org.telegram.tgnet.TLRPC.StickerSetCovered;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_inputStickerSetID;
import org.telegram.tgnet.TLRPC.TL_inputStickerSetShortName;
import org.telegram.tgnet.TLRPC.TL_messages_archivedStickers;
import org.telegram.tgnet.TLRPC.TL_messages_getArchivedStickers;

/* renamed from: org.telegram.ui.ArchivedStickersActivity */
public class ArchivedStickersActivity extends BaseFragment implements NotificationCenterDelegate {
    private int currentType;
    private EmptyTextProgressView emptyView;
    private boolean endReached;
    private boolean firstLoaded;
    private LinearLayoutManager layoutManager;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    private boolean loadingStickers;
    private int rowCount;
    private ArrayList<StickerSetCovered> sets = new ArrayList();
    private int stickersEndRow;
    private int stickersLoadingRow;
    private int stickersShadowRow;
    private int stickersStartRow;

    /* renamed from: org.telegram.ui.ArchivedStickersActivity$1 */
    class CLASSNAME extends ActionBarMenuOnItemClick {
        CLASSNAME() {
        }

        public void onItemClick(int id) {
            if (id == -1) {
                ArchivedStickersActivity.this.lambda$checkDiscard$70$PassportActivity();
            }
        }
    }

    /* renamed from: org.telegram.ui.ArchivedStickersActivity$3 */
    class CLASSNAME extends OnScrollListener {
        CLASSNAME() {
        }

        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            if (!ArchivedStickersActivity.this.loadingStickers && !ArchivedStickersActivity.this.endReached && ArchivedStickersActivity.this.layoutManager.findLastVisibleItemPosition() > ArchivedStickersActivity.this.stickersLoadingRow - 2) {
                ArchivedStickersActivity.this.getStickers();
            }
        }
    }

    /* renamed from: org.telegram.ui.ArchivedStickersActivity$ListAdapter */
    private class ListAdapter extends SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public int getItemCount() {
            return ArchivedStickersActivity.this.rowCount;
        }

        public void onBindViewHolder(ViewHolder holder, int position) {
            if (getItemViewType(position) == 0) {
                ArchivedStickerSetCell cell = holder.itemView;
                cell.setTag(Integer.valueOf(position));
                StickerSetCovered stickerSet = (StickerSetCovered) ArchivedStickersActivity.this.sets.get(position);
                cell.setStickersSet(stickerSet, position != ArchivedStickersActivity.this.sets.size() + -1);
                cell.setChecked(DataQuery.getInstance(ArchivedStickersActivity.this.currentAccount).isStickerPackInstalled(stickerSet.set.var_id));
            }
        }

        public boolean isEnabled(ViewHolder holder) {
            return holder.getItemViewType() == 0;
        }

        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = null;
            switch (viewType) {
                case 0:
                    view = new ArchivedStickerSetCell(this.mContext, true);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    ((ArchivedStickerSetCell) view).setOnCheckClick(new ArchivedStickersActivity$ListAdapter$$Lambda$0(this));
                    break;
                case 1:
                    view = new LoadingCell(this.mContext);
                    view.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                    break;
                case 2:
                    view = new TextInfoPrivacyCell(this.mContext);
                    view.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                    break;
            }
            view.setLayoutParams(new LayoutParams(-1, -2));
            return new Holder(view);
        }

        final /* synthetic */ void lambda$onCreateViewHolder$0$ArchivedStickersActivity$ListAdapter(Switch buttonView, boolean isChecked) {
            int num = ((Integer) ((ArchivedStickerSetCell) buttonView.getParent()).getTag()).intValue();
            if (num < ArchivedStickersActivity.this.sets.size()) {
                DataQuery.getInstance(ArchivedStickersActivity.this.currentAccount).removeStickersSet(ArchivedStickersActivity.this.getParentActivity(), ((StickerSetCovered) ArchivedStickersActivity.this.sets.get(num)).set, !isChecked ? 1 : 2, ArchivedStickersActivity.this, false);
            }
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

    public ArchivedStickersActivity(int type) {
        this.currentType = type;
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
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        if (this.currentType == 0) {
            this.actionBar.setTitle(LocaleController.getString("ArchivedStickers", R.string.ArchivedStickers));
        } else {
            this.actionBar.setTitle(LocaleController.getString("ArchivedMasks", R.string.ArchivedMasks));
        }
        this.actionBar.setActionBarMenuOnItemClick(new CLASSNAME());
        this.listAdapter = new ListAdapter(context);
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = this.fragmentView;
        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        this.emptyView = new EmptyTextProgressView(context);
        if (this.currentType == 0) {
            this.emptyView.setText(LocaleController.getString("ArchivedStickersEmpty", R.string.ArchivedStickersEmpty));
        } else {
            this.emptyView.setText(LocaleController.getString("ArchivedMasksEmpty", R.string.ArchivedMasksEmpty));
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
        LayoutManager linearLayoutManager = new LinearLayoutManager(context, 1, false);
        this.layoutManager = linearLayoutManager;
        recyclerListView.setLayoutManager(linearLayoutManager);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setAdapter(this.listAdapter);
        this.listView.setOnItemClickListener(new ArchivedStickersActivity$$Lambda$0(this));
        this.listView.setOnScrollListener(new CLASSNAME());
        return this.fragmentView;
    }

    final /* synthetic */ void lambda$createView$0$ArchivedStickersActivity(final View view, int position) {
        if (position >= this.stickersStartRow && position < this.stickersEndRow && getParentActivity() != null) {
            InputStickerSet inputStickerSet;
            StickerSetCovered stickerSet = (StickerSetCovered) this.sets.get(position);
            if (stickerSet.set.var_id != 0) {
                inputStickerSet = new TL_inputStickerSetID();
                inputStickerSet.var_id = stickerSet.set.var_id;
            } else {
                inputStickerSet = new TL_inputStickerSetShortName();
                inputStickerSet.short_name = stickerSet.set.short_name;
            }
            inputStickerSet.access_hash = stickerSet.set.access_hash;
            StickersAlert stickersAlert = new StickersAlert(getParentActivity(), this, inputStickerSet, null, null);
            stickersAlert.setInstallDelegate(new StickersAlertInstallDelegate() {
                public void onStickerSetInstalled() {
                    view.setChecked(true);
                }

                public void onStickerSetUninstalled() {
                    view.setChecked(false);
                }
            });
            showDialog(stickersAlert);
        }
    }

    private void updateRows() {
        this.rowCount = 0;
        if (this.sets.isEmpty()) {
            this.stickersStartRow = -1;
            this.stickersEndRow = -1;
            this.stickersLoadingRow = -1;
            this.stickersShadowRow = -1;
        } else {
            this.stickersStartRow = this.rowCount;
            this.stickersEndRow = this.rowCount + this.sets.size();
            this.rowCount += this.sets.size();
            int i;
            if (this.endReached) {
                i = this.rowCount;
                this.rowCount = i + 1;
                this.stickersShadowRow = i;
                this.stickersLoadingRow = -1;
            } else {
                i = this.rowCount;
                this.rowCount = i + 1;
                this.stickersLoadingRow = i;
                this.stickersShadowRow = -1;
            }
        }
        if (this.listAdapter != null) {
            this.listAdapter.notifyDataSetChanged();
        }
    }

    private void getStickers() {
        if (!this.loadingStickers && !this.endReached) {
            this.loadingStickers = true;
            if (!(this.emptyView == null || this.firstLoaded)) {
                this.emptyView.showProgress();
            }
            if (this.listAdapter != null) {
                this.listAdapter.notifyDataSetChanged();
            }
            TL_messages_getArchivedStickers req = new TL_messages_getArchivedStickers();
            req.offset_id = this.sets.isEmpty() ? 0 : ((StickerSetCovered) this.sets.get(this.sets.size() - 1)).set.var_id;
            req.limit = 15;
            req.masks = this.currentType == 1;
            ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new ArchivedStickersActivity$$Lambda$1(this)), this.classGuid);
        }
    }

    final /* synthetic */ void lambda$getStickers$2$ArchivedStickersActivity(TLObject response, TL_error error) {
        AndroidUtilities.runOnUIThread(new ArchivedStickersActivity$$Lambda$2(this, error, response));
    }

    final /* synthetic */ void lambda$null$1$ArchivedStickersActivity(TL_error error, TLObject response) {
        if (error == null) {
            boolean z;
            TL_messages_archivedStickers res = (TL_messages_archivedStickers) response;
            this.sets.addAll(res.sets);
            if (res.sets.size() != 15) {
                z = true;
            } else {
                z = false;
            }
            this.endReached = z;
            this.loadingStickers = false;
            this.firstLoaded = true;
            if (this.emptyView != null) {
                this.emptyView.showTextView();
            }
            updateRows();
        }
    }

    public void onResume() {
        super.onResume();
        if (this.listAdapter != null) {
            this.listAdapter.notifyDataSetChanged();
        }
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.needReloadArchivedStickers) {
            this.firstLoaded = false;
            this.endReached = false;
            this.sets.clear();
            updateRows();
            if (this.emptyView != null) {
                this.emptyView.showProgress();
            }
            getStickers();
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        r9 = new ThemeDescription[17];
        r9[0] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{ArchivedStickerSetCell.class}, null, null, null, Theme.key_windowBackgroundWhite);
        r9[1] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundGray);
        r9[2] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{LoadingCell.class, TextInfoPrivacyCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow);
        r9[3] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault);
        r9[4] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault);
        r9[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon);
        r9[6] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle);
        r9[7] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector);
        r9[8] = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector);
        r9[9] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider);
        r9[10] = new ThemeDescription(this.emptyView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_emptyListPlaceholder);
        r9[11] = new ThemeDescription(this.emptyView, ThemeDescription.FLAG_PROGRESSBAR, null, null, null, null, Theme.key_progressCircle);
        r9[12] = new ThemeDescription(this.listView, 0, new Class[]{LoadingCell.class}, new String[]{"progressBar"}, null, null, null, Theme.key_progressCircle);
        r9[13] = new ThemeDescription(this.listView, 0, new Class[]{ArchivedStickerSetCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r9[14] = new ThemeDescription(this.listView, 0, new Class[]{ArchivedStickerSetCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText2);
        r9[15] = new ThemeDescription(this.listView, 0, new Class[]{ArchivedStickerSetCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchTrack);
        r9[16] = new ThemeDescription(this.listView, 0, new Class[]{ArchivedStickerSetCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchTrackChecked);
        return r9;
    }
}
