package org.telegram.ui;

import android.app.Dialog;
import android.content.Context;
import android.util.LongSparseArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import java.util.ArrayList;
import java.util.Collection;
import org.telegram.messenger.C0446R;
import org.telegram.messenger.DataQuery;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.LayoutParams;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.tgnet.TLRPC.InputStickerSet;
import org.telegram.tgnet.TLRPC.StickerSetCovered;
import org.telegram.tgnet.TLRPC.TL_inputStickerSetID;
import org.telegram.tgnet.TLRPC.TL_inputStickerSetShortName;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.FeaturedStickerSetCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;
import org.telegram.ui.Components.StickersAlert;
import org.telegram.ui.Components.StickersAlert.StickersAlertInstallDelegate;

public class FeaturedStickersActivity extends BaseFragment implements NotificationCenterDelegate {
    private LongSparseArray<StickerSetCovered> installingStickerSets = new LongSparseArray();
    private LinearLayoutManager layoutManager;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    private int rowCount;
    private int stickersEndRow;
    private int stickersShadowRow;
    private int stickersStartRow;
    private ArrayList<Long> unreadStickers = null;

    /* renamed from: org.telegram.ui.FeaturedStickersActivity$1 */
    class C21361 extends ActionBarMenuOnItemClick {
        C21361() {
        }

        public void onItemClick(int i) {
            if (i == -1) {
                FeaturedStickersActivity.this.finishFragment();
            }
        }
    }

    /* renamed from: org.telegram.ui.FeaturedStickersActivity$3 */
    class C21383 implements OnItemClickListener {
        C21383() {
        }

        public void onItemClick(final View view, int i) {
            if (i >= FeaturedStickersActivity.this.stickersStartRow && i < FeaturedStickersActivity.this.stickersEndRow && FeaturedStickersActivity.this.getParentActivity() != null) {
                InputStickerSet tL_inputStickerSetID;
                final StickerSetCovered stickerSetCovered = (StickerSetCovered) DataQuery.getInstance(FeaturedStickersActivity.this.currentAccount).getFeaturedStickerSets().get(i);
                if (stickerSetCovered.set.id != 0) {
                    tL_inputStickerSetID = new TL_inputStickerSetID();
                    tL_inputStickerSetID.id = stickerSetCovered.set.id;
                } else {
                    tL_inputStickerSetID = new TL_inputStickerSetShortName();
                    tL_inputStickerSetID.short_name = stickerSetCovered.set.short_name;
                }
                InputStickerSet inputStickerSet = tL_inputStickerSetID;
                inputStickerSet.access_hash = stickerSetCovered.set.access_hash;
                Dialog stickersAlert = new StickersAlert(FeaturedStickersActivity.this.getParentActivity(), FeaturedStickersActivity.this, inputStickerSet, null, null);
                stickersAlert.setInstallDelegate(new StickersAlertInstallDelegate() {
                    public void onStickerSetUninstalled() {
                    }

                    public void onStickerSetInstalled() {
                        ((FeaturedStickerSetCell) view).setDrawProgress(true);
                        FeaturedStickersActivity.this.installingStickerSets.put(stickerSetCovered.set.id, stickerSetCovered);
                    }
                });
                FeaturedStickersActivity.this.showDialog(stickersAlert);
            }
        }
    }

    private class ListAdapter extends SelectionAdapter {
        private Context mContext;

        /* renamed from: org.telegram.ui.FeaturedStickersActivity$ListAdapter$1 */
        class C13921 implements OnClickListener {
            C13921() {
            }

            public void onClick(View view) {
                FeaturedStickerSetCell featuredStickerSetCell = (FeaturedStickerSetCell) view.getParent();
                StickerSetCovered stickerSet = featuredStickerSetCell.getStickerSet();
                if (FeaturedStickersActivity.this.installingStickerSets.indexOfKey(stickerSet.set.id) < 0) {
                    FeaturedStickersActivity.this.installingStickerSets.put(stickerSet.set.id, stickerSet);
                    DataQuery.getInstance(FeaturedStickersActivity.this.currentAccount).removeStickersSet(FeaturedStickersActivity.this.getParentActivity(), stickerSet.set, 2, FeaturedStickersActivity.this, false);
                    featuredStickerSetCell.setDrawProgress(true);
                }
            }
        }

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public int getItemCount() {
            return FeaturedStickersActivity.this.rowCount;
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            if (getItemViewType(i) == 0) {
                ArrayList featuredStickerSets = DataQuery.getInstance(FeaturedStickersActivity.this.currentAccount).getFeaturedStickerSets();
                FeaturedStickerSetCell featuredStickerSetCell = (FeaturedStickerSetCell) viewHolder.itemView;
                featuredStickerSetCell.setTag(Integer.valueOf(i));
                StickerSetCovered stickerSetCovered = (StickerSetCovered) featuredStickerSets.get(i);
                boolean z = true;
                i = i != featuredStickerSets.size() - 1 ? 1 : 0;
                boolean z2 = FeaturedStickersActivity.this.unreadStickers != null && FeaturedStickersActivity.this.unreadStickers.contains(Long.valueOf(stickerSetCovered.set.id));
                featuredStickerSetCell.setStickersSet(stickerSetCovered, i, z2);
                if (FeaturedStickersActivity.this.installingStickerSets.indexOfKey(stickerSetCovered.set.id) < 0) {
                    z = false;
                }
                if (z && featuredStickerSetCell.isInstalled() != 0) {
                    FeaturedStickersActivity.this.installingStickerSets.remove(stickerSetCovered.set.id);
                    featuredStickerSetCell.setDrawProgress(false);
                    z = false;
                }
                featuredStickerSetCell.setDrawProgress(z);
            }
        }

        public boolean isEnabled(ViewHolder viewHolder) {
            return viewHolder.getItemViewType() == null ? true : null;
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            switch (i) {
                case 0:
                    viewGroup = new FeaturedStickerSetCell(this.mContext);
                    viewGroup.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    ((FeaturedStickerSetCell) viewGroup).setAddOnClickListener(new C13921());
                    break;
                case 1:
                    viewGroup = new TextInfoPrivacyCell(this.mContext);
                    viewGroup.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, C0446R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                    break;
                default:
                    viewGroup = null;
                    break;
            }
            viewGroup.setLayoutParams(new LayoutParams(-1, -2));
            return new Holder(viewGroup);
        }

        public int getItemViewType(int i) {
            if ((i < FeaturedStickersActivity.this.stickersStartRow || i >= FeaturedStickersActivity.this.stickersEndRow) && i == FeaturedStickersActivity.this.stickersShadowRow) {
                return 1;
            }
            return 0;
        }
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        DataQuery.getInstance(this.currentAccount).checkFeaturedStickers();
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.featuredStickersDidLoaded);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.stickersDidLoaded);
        Collection unreadStickerSets = DataQuery.getInstance(this.currentAccount).getUnreadStickerSets();
        if (unreadStickerSets != null) {
            this.unreadStickers = new ArrayList(unreadStickerSets);
        }
        updateRows();
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.featuredStickersDidLoaded);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.stickersDidLoaded);
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(C0446R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("FeaturedStickers", C0446R.string.FeaturedStickers));
        this.actionBar.setActionBarMenuOnItemClick(new C21361());
        this.listAdapter = new ListAdapter(context);
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        this.listView = new RecyclerListView(context);
        this.listView.setItemAnimator(null);
        this.listView.setLayoutAnimation(null);
        this.listView.setFocusable(true);
        this.listView.setTag(Integer.valueOf(14));
        this.layoutManager = new LinearLayoutManager(context) {
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        };
        this.layoutManager.setOrientation(1);
        this.listView.setLayoutManager(this.layoutManager);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setAdapter(this.listAdapter);
        this.listView.setOnItemClickListener(new C21383());
        return this.fragmentView;
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.featuredStickersDidLoaded) {
            if (this.unreadStickers == 0) {
                this.unreadStickers = DataQuery.getInstance(this.currentAccount).getUnreadStickerSets();
            }
            updateRows();
        } else if (i == NotificationCenter.stickersDidLoaded) {
            updateVisibleTrendingSets();
        }
    }

    private void updateVisibleTrendingSets() {
        if (this.layoutManager != null) {
            int findFirstVisibleItemPosition = this.layoutManager.findFirstVisibleItemPosition();
            if (findFirstVisibleItemPosition != -1) {
                int findLastVisibleItemPosition = this.layoutManager.findLastVisibleItemPosition();
                if (findLastVisibleItemPosition != -1) {
                    this.listAdapter.notifyItemRangeChanged(findFirstVisibleItemPosition, (findLastVisibleItemPosition - findFirstVisibleItemPosition) + 1);
                }
            }
        }
    }

    private void updateRows() {
        this.rowCount = 0;
        ArrayList featuredStickerSets = DataQuery.getInstance(this.currentAccount).getFeaturedStickerSets();
        if (featuredStickerSets.isEmpty()) {
            this.stickersStartRow = -1;
            this.stickersEndRow = -1;
            this.stickersShadowRow = -1;
        } else {
            this.stickersStartRow = this.rowCount;
            this.stickersEndRow = this.rowCount + featuredStickerSets.size();
            this.rowCount += featuredStickerSets.size();
            int i = this.rowCount;
            this.rowCount = i + 1;
            this.stickersShadowRow = i;
        }
        if (this.listAdapter != null) {
            this.listAdapter.notifyDataSetChanged();
        }
        DataQuery.getInstance(this.currentAccount).markFaturedStickersAsRead(true);
    }

    public void onResume() {
        super.onResume();
        if (this.listAdapter != null) {
            this.listAdapter.notifyDataSetChanged();
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        r1 = new ThemeDescription[17];
        r1[0] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{FeaturedStickerSetCell.class}, null, null, null, Theme.key_windowBackgroundWhite);
        r1[1] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundGray);
        r1[2] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault);
        r1[3] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault);
        r1[4] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon);
        r1[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle);
        r1[6] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector);
        r1[7] = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector);
        r1[8] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider);
        r1[9] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow);
        r1[10] = new ThemeDescription(this.listView, 0, new Class[]{FeaturedStickerSetCell.class}, new String[]{"progressPaint"}, null, null, null, Theme.key_featuredStickers_buttonProgress);
        r1[11] = new ThemeDescription(this.listView, 0, new Class[]{FeaturedStickerSetCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r1[12] = new ThemeDescription(this.listView, 0, new Class[]{FeaturedStickerSetCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText2);
        r1[13] = new ThemeDescription(this.listView, 0, new Class[]{FeaturedStickerSetCell.class}, new String[]{"addButton"}, null, null, null, Theme.key_featuredStickers_buttonText);
        r1[14] = new ThemeDescription(this.listView, 0, new Class[]{FeaturedStickerSetCell.class}, new String[]{"checkImage"}, null, null, null, Theme.key_featuredStickers_addedIcon);
        r1[15] = new ThemeDescription(this.listView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE, new Class[]{FeaturedStickerSetCell.class}, new String[]{"addButton"}, null, null, null, Theme.key_featuredStickers_addButton);
        r1[16] = new ThemeDescription(this.listView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, new Class[]{FeaturedStickerSetCell.class}, new String[]{"addButton"}, null, null, null, Theme.key_featuredStickers_addButtonPressed);
        return r1;
    }
}
