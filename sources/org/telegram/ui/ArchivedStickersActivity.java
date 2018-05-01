package org.telegram.ui;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.FrameLayout;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.C0446R;
import org.telegram.messenger.DataQuery;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.support.widget.RecyclerView.LayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.LayoutParams;
import org.telegram.messenger.support.widget.RecyclerView.OnScrollListener;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.InputStickerSet;
import org.telegram.tgnet.TLRPC.StickerSet;
import org.telegram.tgnet.TLRPC.StickerSetCovered;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_inputStickerSetID;
import org.telegram.tgnet.TLRPC.TL_inputStickerSetShortName;
import org.telegram.tgnet.TLRPC.TL_messages_archivedStickers;
import org.telegram.tgnet.TLRPC.TL_messages_getArchivedStickers;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.ArchivedStickerSetCell;
import org.telegram.ui.Cells.LoadingCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;
import org.telegram.ui.Components.StickersAlert;
import org.telegram.ui.Components.StickersAlert.StickersAlertInstallDelegate;

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
    class C19041 extends ActionBarMenuOnItemClick {
        C19041() {
        }

        public void onItemClick(int i) {
            if (i == -1) {
                ArchivedStickersActivity.this.finishFragment();
            }
        }
    }

    /* renamed from: org.telegram.ui.ArchivedStickersActivity$2 */
    class C19062 implements OnItemClickListener {
        C19062() {
        }

        public void onItemClick(final View view, int i) {
            if (i >= ArchivedStickersActivity.this.stickersStartRow && i < ArchivedStickersActivity.this.stickersEndRow && ArchivedStickersActivity.this.getParentActivity() != null) {
                InputStickerSet tL_inputStickerSetID;
                StickerSetCovered stickerSetCovered = (StickerSetCovered) ArchivedStickersActivity.this.sets.get(i);
                if (stickerSetCovered.set.id != 0) {
                    tL_inputStickerSetID = new TL_inputStickerSetID();
                    tL_inputStickerSetID.id = stickerSetCovered.set.id;
                } else {
                    tL_inputStickerSetID = new TL_inputStickerSetShortName();
                    tL_inputStickerSetID.short_name = stickerSetCovered.set.short_name;
                }
                InputStickerSet inputStickerSet = tL_inputStickerSetID;
                inputStickerSet.access_hash = stickerSetCovered.set.access_hash;
                StickersAlert stickersAlert = new StickersAlert(ArchivedStickersActivity.this.getParentActivity(), ArchivedStickersActivity.this, inputStickerSet, null, null);
                stickersAlert.setInstallDelegate(new StickersAlertInstallDelegate() {
                    public void onStickerSetInstalled() {
                        ((ArchivedStickerSetCell) view).setChecked(true);
                    }

                    public void onStickerSetUninstalled() {
                        ((ArchivedStickerSetCell) view).setChecked(false);
                    }
                });
                ArchivedStickersActivity.this.showDialog(stickersAlert);
            }
        }
    }

    /* renamed from: org.telegram.ui.ArchivedStickersActivity$3 */
    class C19073 extends OnScrollListener {
        C19073() {
        }

        public void onScrolled(RecyclerView recyclerView, int i, int i2) {
            if (ArchivedStickersActivity.this.loadingStickers == null && ArchivedStickersActivity.this.endReached == null && ArchivedStickersActivity.this.layoutManager.findLastVisibleItemPosition() > ArchivedStickersActivity.this.stickersLoadingRow - 2) {
                ArchivedStickersActivity.this.getStickers();
            }
        }
    }

    /* renamed from: org.telegram.ui.ArchivedStickersActivity$4 */
    class C19084 implements RequestDelegate {
        C19084() {
        }

        public void run(final TLObject tLObject, final TL_error tL_error) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    if (tL_error == null) {
                        TL_messages_archivedStickers tL_messages_archivedStickers = (TL_messages_archivedStickers) tLObject;
                        ArchivedStickersActivity.this.sets.addAll(tL_messages_archivedStickers.sets);
                        ArchivedStickersActivity.this.endReached = tL_messages_archivedStickers.sets.size() != 15;
                        ArchivedStickersActivity.this.loadingStickers = false;
                        ArchivedStickersActivity.this.firstLoaded = true;
                        if (ArchivedStickersActivity.this.emptyView != null) {
                            ArchivedStickersActivity.this.emptyView.showTextView();
                        }
                        ArchivedStickersActivity.this.updateRows();
                    }
                }
            });
        }
    }

    private class ListAdapter extends SelectionAdapter {
        private Context mContext;

        /* renamed from: org.telegram.ui.ArchivedStickersActivity$ListAdapter$1 */
        class C08101 implements OnCheckedChangeListener {
            C08101() {
            }

            public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                compoundButton = ((Integer) ((ArchivedStickerSetCell) compoundButton.getParent()).getTag()).intValue();
                if (compoundButton < ArchivedStickersActivity.this.sets.size()) {
                    StickerSetCovered stickerSetCovered = (StickerSetCovered) ArchivedStickersActivity.this.sets.get(compoundButton);
                    DataQuery instance = DataQuery.getInstance(ArchivedStickersActivity.this.currentAccount);
                    Context parentActivity = ArchivedStickersActivity.this.getParentActivity();
                    StickerSet stickerSet = stickerSetCovered.set;
                    if (z) {
                        int i = 2;
                    } else {
                        compoundButton = true;
                    }
                    instance.removeStickersSet(parentActivity, stickerSet, compoundButton, ArchivedStickersActivity.this, false);
                }
            }
        }

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public int getItemCount() {
            return ArchivedStickersActivity.this.rowCount;
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            if (getItemViewType(i) == 0) {
                ArchivedStickerSetCell archivedStickerSetCell = (ArchivedStickerSetCell) viewHolder.itemView;
                archivedStickerSetCell.setTag(Integer.valueOf(i));
                StickerSetCovered stickerSetCovered = (StickerSetCovered) ArchivedStickersActivity.this.sets.get(i);
                boolean z = true;
                if (i == ArchivedStickersActivity.this.sets.size() - 1) {
                    z = false;
                }
                archivedStickerSetCell.setStickersSet(stickerSetCovered, z);
                archivedStickerSetCell.setChecked(DataQuery.getInstance(ArchivedStickersActivity.this.currentAccount).isStickerPackInstalled(stickerSetCovered.set.id));
            }
        }

        public boolean isEnabled(ViewHolder viewHolder) {
            return viewHolder.getItemViewType() == null ? true : null;
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            switch (i) {
                case 0:
                    i = new ArchivedStickerSetCell(this.mContext, true);
                    i.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    ((ArchivedStickerSetCell) i).setOnCheckClick(new C08101());
                    break;
                case 1:
                    i = new LoadingCell(this.mContext);
                    i.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, C0446R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                    break;
                case 2:
                    i = new TextInfoPrivacyCell(this.mContext);
                    i.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, C0446R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                    break;
                default:
                    i = 0;
                    break;
            }
            i.setLayoutParams(new LayoutParams(-1, -2));
            return new Holder(i);
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
        this.actionBar.setBackButtonImage(C0446R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        if (this.currentType == 0) {
            this.actionBar.setTitle(LocaleController.getString("ArchivedStickers", C0446R.string.ArchivedStickers));
        } else {
            this.actionBar.setTitle(LocaleController.getString("ArchivedMasks", C0446R.string.ArchivedMasks));
        }
        this.actionBar.setActionBarMenuOnItemClick(new C19041());
        this.listAdapter = new ListAdapter(context);
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        this.emptyView = new EmptyTextProgressView(context);
        if (this.currentType == 0) {
            this.emptyView.setText(LocaleController.getString("ArchivedStickersEmpty", C0446R.string.ArchivedStickersEmpty));
        } else {
            this.emptyView.setText(LocaleController.getString("ArchivedMasksEmpty", C0446R.string.ArchivedMasksEmpty));
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
        this.listView.setOnItemClickListener(new C19062());
        this.listView.setOnScrollListener(new C19073());
        return this.fragmentView;
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
        if (!this.loadingStickers) {
            if (!this.endReached) {
                boolean z = true;
                this.loadingStickers = true;
                if (!(this.emptyView == null || this.firstLoaded)) {
                    this.emptyView.showProgress();
                }
                if (this.listAdapter != null) {
                    this.listAdapter.notifyDataSetChanged();
                }
                TLObject tL_messages_getArchivedStickers = new TL_messages_getArchivedStickers();
                tL_messages_getArchivedStickers.offset_id = this.sets.isEmpty() ? 0 : ((StickerSetCovered) this.sets.get(this.sets.size() - 1)).set.id;
                tL_messages_getArchivedStickers.limit = 15;
                if (this.currentType != 1) {
                    z = false;
                }
                tL_messages_getArchivedStickers.masks = z;
                ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_getArchivedStickers, new C19084()), this.classGuid);
            }
        }
    }

    public void onResume() {
        super.onResume();
        if (this.listAdapter != null) {
            this.listAdapter.notifyDataSetChanged();
        }
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.needReloadArchivedStickers) {
            this.firstLoaded = false;
            this.endReached = false;
            this.sets.clear();
            updateRows();
            if (this.emptyView != 0) {
                this.emptyView.showProgress();
            }
            getStickers();
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        ThemeDescription[] themeDescriptionArr = new ThemeDescription[19];
        themeDescriptionArr[0] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{ArchivedStickerSetCell.class}, null, null, null, Theme.key_windowBackgroundWhite);
        themeDescriptionArr[1] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundGray);
        themeDescriptionArr[2] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{LoadingCell.class, TextInfoPrivacyCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow);
        themeDescriptionArr[3] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault);
        themeDescriptionArr[4] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault);
        themeDescriptionArr[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon);
        themeDescriptionArr[6] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle);
        themeDescriptionArr[7] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector);
        themeDescriptionArr[8] = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector);
        themeDescriptionArr[9] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider);
        themeDescriptionArr[10] = new ThemeDescription(this.emptyView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_emptyListPlaceholder);
        themeDescriptionArr[11] = new ThemeDescription(this.emptyView, ThemeDescription.FLAG_PROGRESSBAR, null, null, null, null, Theme.key_progressCircle);
        themeDescriptionArr[12] = new ThemeDescription(this.listView, 0, new Class[]{LoadingCell.class}, new String[]{"progressBar"}, null, null, null, Theme.key_progressCircle);
        themeDescriptionArr[13] = new ThemeDescription(this.listView, 0, new Class[]{ArchivedStickerSetCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        themeDescriptionArr[14] = new ThemeDescription(this.listView, 0, new Class[]{ArchivedStickerSetCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText2);
        themeDescriptionArr[15] = new ThemeDescription(this.listView, 0, new Class[]{ArchivedStickerSetCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchThumb);
        themeDescriptionArr[16] = new ThemeDescription(this.listView, 0, new Class[]{ArchivedStickerSetCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchTrack);
        themeDescriptionArr[17] = new ThemeDescription(this.listView, 0, new Class[]{ArchivedStickerSetCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchThumbChecked);
        themeDescriptionArr[18] = new ThemeDescription(this.listView, 0, new Class[]{ArchivedStickerSetCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchTrackChecked);
        return themeDescriptionArr;
    }
}
