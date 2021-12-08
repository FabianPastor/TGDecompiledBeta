package org.telegram.ui;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.LongSparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LayoutAnimationController;
import android.widget.FrameLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.FeaturedStickerSetCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.StickersAlert;

public class FeaturedStickersActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    /* access modifiers changed from: private */
    public LongSparseArray<TLRPC.StickerSetCovered> installingStickerSets = new LongSparseArray<>();
    private LinearLayoutManager layoutManager;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    /* access modifiers changed from: private */
    public int rowCount;
    /* access modifiers changed from: private */
    public int stickersEndRow;
    /* access modifiers changed from: private */
    public int stickersShadowRow;
    /* access modifiers changed from: private */
    public int stickersStartRow;
    /* access modifiers changed from: private */
    public ArrayList<Long> unreadStickers = null;

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        MediaDataController.getInstance(this.currentAccount).checkFeaturedStickers();
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.featuredStickersDidLoad);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.stickersDidLoad);
        ArrayList<Long> arrayList = MediaDataController.getInstance(this.currentAccount).getUnreadStickerSets();
        if (arrayList != null) {
            this.unreadStickers = new ArrayList<>(arrayList);
        }
        updateRows();
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.featuredStickersDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.stickersDidLoad);
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("FeaturedStickers", NUM));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    FeaturedStickersActivity.this.finishFragment();
                }
            }
        });
        this.listAdapter = new ListAdapter(context);
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        frameLayout.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
        RecyclerListView recyclerListView = new RecyclerListView(context);
        this.listView = recyclerListView;
        recyclerListView.setItemAnimator((RecyclerView.ItemAnimator) null);
        this.listView.setLayoutAnimation((LayoutAnimationController) null);
        this.listView.setFocusable(true);
        this.listView.setTag(14);
        AnonymousClass2 r2 = new LinearLayoutManager(context) {
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        };
        this.layoutManager = r2;
        r2.setOrientation(1);
        this.listView.setLayoutManager(this.layoutManager);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setAdapter(this.listAdapter);
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new FeaturedStickersActivity$$ExternalSyntheticLambda0(this));
        return this.fragmentView;
    }

    /* renamed from: lambda$createView$0$org-telegram-ui-FeaturedStickersActivity  reason: not valid java name */
    public /* synthetic */ void m2903lambda$createView$0$orgtelegramuiFeaturedStickersActivity(final View view, int position) {
        TLRPC.InputStickerSet inputStickerSet;
        if (position >= this.stickersStartRow && position < this.stickersEndRow && getParentActivity() != null) {
            final TLRPC.StickerSetCovered stickerSet = MediaDataController.getInstance(this.currentAccount).getFeaturedStickerSets().get(position);
            if (stickerSet.set.id != 0) {
                inputStickerSet = new TLRPC.TL_inputStickerSetID();
                inputStickerSet.id = stickerSet.set.id;
            } else {
                inputStickerSet = new TLRPC.TL_inputStickerSetShortName();
                inputStickerSet.short_name = stickerSet.set.short_name;
            }
            inputStickerSet.access_hash = stickerSet.set.access_hash;
            StickersAlert stickersAlert = new StickersAlert((Context) getParentActivity(), (BaseFragment) this, inputStickerSet, (TLRPC.TL_messages_stickerSet) null, (StickersAlert.StickersAlertDelegate) null);
            stickersAlert.setInstallDelegate(new StickersAlert.StickersAlertInstallDelegate() {
                public void onStickerSetInstalled() {
                    ((FeaturedStickerSetCell) view).setDrawProgress(true, true);
                    FeaturedStickersActivity.this.installingStickerSets.put(stickerSet.set.id, stickerSet);
                }

                public void onStickerSetUninstalled() {
                }
            });
            showDialog(stickersAlert);
        }
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.featuredStickersDidLoad) {
            if (this.unreadStickers == null) {
                this.unreadStickers = MediaDataController.getInstance(this.currentAccount).getUnreadStickerSets();
            }
            updateRows();
        } else if (id == NotificationCenter.stickersDidLoad) {
            updateVisibleTrendingSets();
        }
    }

    private void updateVisibleTrendingSets() {
        int first;
        int last;
        LinearLayoutManager linearLayoutManager = this.layoutManager;
        if (linearLayoutManager != null && (first = linearLayoutManager.findFirstVisibleItemPosition()) != -1 && (last = this.layoutManager.findLastVisibleItemPosition()) != -1) {
            this.listAdapter.notifyItemRangeChanged(first, (last - first) + 1);
        }
    }

    private void updateRows() {
        this.rowCount = 0;
        ArrayList<TLRPC.StickerSetCovered> stickerSets = MediaDataController.getInstance(this.currentAccount).getFeaturedStickerSets();
        if (!stickerSets.isEmpty()) {
            int i = this.rowCount;
            this.stickersStartRow = i;
            this.stickersEndRow = i + stickerSets.size();
            int size = this.rowCount + stickerSets.size();
            this.rowCount = size;
            this.rowCount = size + 1;
            this.stickersShadowRow = size;
        } else {
            this.stickersStartRow = -1;
            this.stickersEndRow = -1;
            this.stickersShadowRow = -1;
        }
        ListAdapter listAdapter2 = this.listAdapter;
        if (listAdapter2 != null) {
            listAdapter2.notifyDataSetChanged();
        }
        MediaDataController.getInstance(this.currentAccount).markFaturedStickersAsRead(true);
    }

    public void onResume() {
        super.onResume();
        ListAdapter listAdapter2 = this.listAdapter;
        if (listAdapter2 != null) {
            listAdapter2.notifyDataSetChanged();
        }
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public int getItemCount() {
            return FeaturedStickersActivity.this.rowCount;
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (getItemViewType(position) == 0) {
                ArrayList<TLRPC.StickerSetCovered> arrayList = MediaDataController.getInstance(FeaturedStickersActivity.this.currentAccount).getFeaturedStickerSets();
                FeaturedStickerSetCell cell = (FeaturedStickerSetCell) holder.itemView;
                cell.setTag(Integer.valueOf(position));
                TLRPC.StickerSetCovered stickerSet = arrayList.get(position);
                boolean z = true;
                cell.setStickersSet(stickerSet, position != arrayList.size() - 1, FeaturedStickersActivity.this.unreadStickers != null && FeaturedStickersActivity.this.unreadStickers.contains(Long.valueOf(stickerSet.set.id)));
                if (FeaturedStickersActivity.this.installingStickerSets.indexOfKey(stickerSet.set.id) < 0) {
                    z = false;
                }
                boolean installing = z;
                if (installing && cell.isInstalled()) {
                    FeaturedStickersActivity.this.installingStickerSets.remove(stickerSet.set.id);
                    installing = false;
                }
                cell.setDrawProgress(installing, false);
            }
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return holder.getItemViewType() == 0;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    view = new FeaturedStickerSetCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    ((FeaturedStickerSetCell) view).setAddOnClickListener(new FeaturedStickersActivity$ListAdapter$$ExternalSyntheticLambda0(this));
                    break;
                default:
                    view = new TextInfoPrivacyCell(this.mContext);
                    view.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                    break;
            }
            view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(view);
        }

        /* renamed from: lambda$onCreateViewHolder$0$org-telegram-ui-FeaturedStickersActivity$ListAdapter  reason: not valid java name */
        public /* synthetic */ void m2904x669df8c5(View v) {
            FeaturedStickerSetCell parent1 = (FeaturedStickerSetCell) v.getParent();
            TLRPC.StickerSetCovered pack = parent1.getStickerSet();
            if (FeaturedStickersActivity.this.installingStickerSets.indexOfKey(pack.set.id) < 0) {
                FeaturedStickersActivity.this.installingStickerSets.put(pack.set.id, pack);
                MediaDataController.getInstance(FeaturedStickersActivity.this.currentAccount).toggleStickerSet(FeaturedStickersActivity.this.getParentActivity(), pack, 2, FeaturedStickersActivity.this, false, false);
                parent1.setDrawProgress(true, true);
            }
        }

        public int getItemViewType(int i) {
            if ((i < FeaturedStickersActivity.this.stickersStartRow || i >= FeaturedStickersActivity.this.stickersEndRow) && i == FeaturedStickersActivity.this.stickersShadowRow) {
                return 1;
            }
            return 0;
        }
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{FeaturedStickerSetCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        themeDescriptions.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultIcon"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultTitle"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSelector"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "divider"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{FeaturedStickerSetCell.class}, new String[]{"progressPaint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "featuredStickers_buttonProgress"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{FeaturedStickerSetCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{FeaturedStickerSetCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText2"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{FeaturedStickerSetCell.class}, new String[]{"addButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "featuredStickers_buttonText"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{FeaturedStickerSetCell.class}, new String[]{"checkImage"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "featuredStickers_addedIcon"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE, new Class[]{FeaturedStickerSetCell.class}, new String[]{"addButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "featuredStickers_addButton"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, new Class[]{FeaturedStickerSetCell.class}, new String[]{"addButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "featuredStickers_addButtonPressed"));
        return themeDescriptions;
    }
}
