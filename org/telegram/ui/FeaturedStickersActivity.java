package org.telegram.ui;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import java.util.ArrayList;
import java.util.HashMap;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.query.StickersQuery;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.Adapter;
import org.telegram.messenger.support.widget.RecyclerView.LayoutParams;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.tgnet.TLRPC.InputStickerSet;
import org.telegram.tgnet.TLRPC.StickerSetCovered;
import org.telegram.tgnet.TLRPC.TL_inputStickerSetID;
import org.telegram.tgnet.TLRPC.TL_inputStickerSetShortName;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.FeaturedStickerSetCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;
import org.telegram.ui.Components.StickersAlert;
import org.telegram.ui.Components.StickersAlert.StickersAlertInstallDelegate;

public class FeaturedStickersActivity extends BaseFragment implements NotificationCenterDelegate {
    private HashMap<Long, StickerSetCovered> installingStickerSets = new HashMap();
    private LinearLayoutManager layoutManager;
    private ListAdapter listAdapter;
    private int rowCount;
    private int stickersEndRow;
    private int stickersShadowRow;
    private int stickersStartRow;
    private ArrayList<Long> unreadStickers = null;

    private class ListAdapter extends Adapter {
        private Context mContext;

        private class Holder extends ViewHolder {
            public Holder(View itemView) {
                super(itemView);
            }
        }

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public int getItemCount() {
            return FeaturedStickersActivity.this.rowCount;
        }

        public void onBindViewHolder(ViewHolder holder, int position) {
            boolean z = true;
            if (getItemViewType(position) == 0) {
                boolean z2;
                ArrayList<StickerSetCovered> arrayList = StickersQuery.getFeaturedStickerSets();
                FeaturedStickerSetCell cell = holder.itemView;
                cell.setTag(Integer.valueOf(position));
                StickerSetCovered stickerSet = (StickerSetCovered) arrayList.get(position);
                if (position != arrayList.size() - 1) {
                    z2 = true;
                } else {
                    z2 = false;
                }
                if (FeaturedStickersActivity.this.unreadStickers == null || !FeaturedStickersActivity.this.unreadStickers.contains(Long.valueOf(stickerSet.set.id))) {
                    z = false;
                }
                cell.setStickersSet(stickerSet, z2, z);
                boolean installing = FeaturedStickersActivity.this.installingStickerSets.containsKey(Long.valueOf(stickerSet.set.id));
                if (installing && cell.isInstalled()) {
                    FeaturedStickersActivity.this.installingStickerSets.remove(Long.valueOf(stickerSet.set.id));
                    installing = false;
                    cell.setDrawProgress(false);
                }
                cell.setDrawProgress(installing);
            }
        }

        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = null;
            switch (viewType) {
                case 0:
                    view = new FeaturedStickerSetCell(this.mContext);
                    view.setBackgroundResource(R.drawable.list_selector_white);
                    ((FeaturedStickerSetCell) view).setAddOnClickListener(new OnClickListener() {
                        public void onClick(View v) {
                            FeaturedStickerSetCell parent = (FeaturedStickerSetCell) v.getParent();
                            StickerSetCovered pack = parent.getStickerSet();
                            if (!FeaturedStickersActivity.this.installingStickerSets.containsKey(Long.valueOf(pack.set.id))) {
                                FeaturedStickersActivity.this.installingStickerSets.put(Long.valueOf(pack.set.id), pack);
                                StickersQuery.removeStickersSet(FeaturedStickersActivity.this.getParentActivity(), pack.set, 2, FeaturedStickersActivity.this, false);
                                parent.setDrawProgress(true);
                            }
                        }
                    });
                    break;
                case 1:
                    view = new TextInfoPrivacyCell(this.mContext);
                    view.setBackgroundResource(R.drawable.greydivider_bottom);
                    break;
            }
            view.setLayoutParams(new LayoutParams(-1, -2));
            return new Holder(view);
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
        StickersQuery.checkFeaturedStickers();
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.featuredStickersDidLoaded);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.stickersDidLoaded);
        ArrayList<Long> arrayList = StickersQuery.getUnreadStickerSets();
        if (arrayList != null) {
            this.unreadStickers = new ArrayList(arrayList);
        }
        updateRows();
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.featuredStickersDidLoaded);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.stickersDidLoaded);
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("FeaturedStickers", R.string.FeaturedStickers));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    FeaturedStickersActivity.this.finishFragment();
                }
            }
        });
        this.listAdapter = new ListAdapter(context);
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = this.fragmentView;
        frameLayout.setBackgroundColor(Theme.ACTION_BAR_MODE_SELECTOR_COLOR);
        RecyclerListView listView = new RecyclerListView(context);
        listView.setItemAnimator(null);
        listView.setLayoutAnimation(null);
        listView.setFocusable(true);
        listView.setTag(Integer.valueOf(14));
        this.layoutManager = new LinearLayoutManager(context) {
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        };
        this.layoutManager.setOrientation(1);
        listView.setLayoutManager(this.layoutManager);
        frameLayout.addView(listView, LayoutHelper.createFrame(-1, -1.0f));
        listView.setAdapter(this.listAdapter);
        listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(final View view, int position) {
                if (position >= FeaturedStickersActivity.this.stickersStartRow && position < FeaturedStickersActivity.this.stickersEndRow && FeaturedStickersActivity.this.getParentActivity() != null) {
                    InputStickerSet inputStickerSet;
                    final StickerSetCovered stickerSet = (StickerSetCovered) StickersQuery.getFeaturedStickerSets().get(position);
                    if (stickerSet.set.id != 0) {
                        inputStickerSet = new TL_inputStickerSetID();
                        inputStickerSet.id = stickerSet.set.id;
                    } else {
                        inputStickerSet = new TL_inputStickerSetShortName();
                        inputStickerSet.short_name = stickerSet.set.short_name;
                    }
                    inputStickerSet.access_hash = stickerSet.set.access_hash;
                    StickersAlert stickersAlert = new StickersAlert(FeaturedStickersActivity.this.getParentActivity(), FeaturedStickersActivity.this, inputStickerSet, null, null);
                    stickersAlert.setInstallDelegate(new StickersAlertInstallDelegate() {
                        public void onStickerSetInstalled() {
                            view.setDrawProgress(true);
                            FeaturedStickersActivity.this.installingStickerSets.put(Long.valueOf(stickerSet.set.id), stickerSet);
                        }

                        public void onStickerSetUninstalled() {
                        }
                    });
                    FeaturedStickersActivity.this.showDialog(stickersAlert);
                }
            }
        });
        return this.fragmentView;
    }

    public void didReceivedNotification(int id, Object... args) {
        if (id == NotificationCenter.featuredStickersDidLoaded) {
            if (this.unreadStickers == null) {
                this.unreadStickers = StickersQuery.getUnreadStickerSets();
            }
            updateRows();
        } else if (id == NotificationCenter.stickersDidLoaded) {
            updateVisibleTrendingSets();
        }
    }

    private void updateVisibleTrendingSets() {
        if (this.layoutManager != null) {
            int first = this.layoutManager.findFirstVisibleItemPosition();
            if (first != -1) {
                int last = this.layoutManager.findLastVisibleItemPosition();
                if (last != -1) {
                    this.listAdapter.notifyItemRangeChanged(first, (last - first) + 1);
                }
            }
        }
    }

    private void updateRows() {
        this.rowCount = 0;
        ArrayList<StickerSetCovered> stickerSets = StickersQuery.getFeaturedStickerSets();
        if (stickerSets.isEmpty()) {
            this.stickersStartRow = -1;
            this.stickersEndRow = -1;
            this.stickersShadowRow = -1;
        } else {
            this.stickersStartRow = this.rowCount;
            this.stickersEndRow = this.rowCount + stickerSets.size();
            this.rowCount += stickerSets.size();
            int i = this.rowCount;
            this.rowCount = i + 1;
            this.stickersShadowRow = i;
        }
        if (this.listAdapter != null) {
            this.listAdapter.notifyDataSetChanged();
        }
        StickersQuery.markFaturedStickersAsRead(true);
    }

    public void onResume() {
        super.onResume();
        if (this.listAdapter != null) {
            this.listAdapter.notifyDataSetChanged();
        }
    }
}
