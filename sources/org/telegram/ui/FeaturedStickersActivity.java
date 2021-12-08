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
import org.telegram.tgnet.TLRPC$InputStickerSet;
import org.telegram.tgnet.TLRPC$StickerSetCovered;
import org.telegram.tgnet.TLRPC$TL_inputStickerSetID;
import org.telegram.tgnet.TLRPC$TL_inputStickerSetShortName;
import org.telegram.tgnet.TLRPC$TL_messages_stickerSet;
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
    public LongSparseArray<TLRPC$StickerSetCovered> installingStickerSets = new LongSparseArray<>();
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
        ArrayList<Long> unreadStickerSets = MediaDataController.getInstance(this.currentAccount).getUnreadStickerSets();
        if (unreadStickerSets != null) {
            this.unreadStickers = new ArrayList<>(unreadStickerSets);
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
            public void onItemClick(int i) {
                if (i == -1) {
                    FeaturedStickersActivity.this.finishFragment();
                }
            }
        });
        this.listAdapter = new ListAdapter(context);
        FrameLayout frameLayout = new FrameLayout(context);
        this.fragmentView = frameLayout;
        FrameLayout frameLayout2 = frameLayout;
        frameLayout2.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
        RecyclerListView recyclerListView = new RecyclerListView(context);
        this.listView = recyclerListView;
        recyclerListView.setItemAnimator((RecyclerView.ItemAnimator) null);
        this.listView.setLayoutAnimation((LayoutAnimationController) null);
        this.listView.setFocusable(true);
        this.listView.setTag(14);
        AnonymousClass2 r2 = new LinearLayoutManager(this, context) {
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        };
        this.layoutManager = r2;
        r2.setOrientation(1);
        this.listView.setLayoutManager(this.layoutManager);
        frameLayout2.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setAdapter(this.listAdapter);
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new FeaturedStickersActivity$$ExternalSyntheticLambda0(this));
        return this.fragmentView;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$0(final View view, int i) {
        TLRPC$InputStickerSet tLRPC$InputStickerSet;
        if (i >= this.stickersStartRow && i < this.stickersEndRow && getParentActivity() != null) {
            final TLRPC$StickerSetCovered tLRPC$StickerSetCovered = MediaDataController.getInstance(this.currentAccount).getFeaturedStickerSets().get(i);
            if (tLRPC$StickerSetCovered.set.id != 0) {
                tLRPC$InputStickerSet = new TLRPC$TL_inputStickerSetID();
                tLRPC$InputStickerSet.id = tLRPC$StickerSetCovered.set.id;
            } else {
                tLRPC$InputStickerSet = new TLRPC$TL_inputStickerSetShortName();
                tLRPC$InputStickerSet.short_name = tLRPC$StickerSetCovered.set.short_name;
            }
            TLRPC$InputStickerSet tLRPC$InputStickerSet2 = tLRPC$InputStickerSet;
            tLRPC$InputStickerSet2.access_hash = tLRPC$StickerSetCovered.set.access_hash;
            StickersAlert stickersAlert = new StickersAlert((Context) getParentActivity(), (BaseFragment) this, tLRPC$InputStickerSet2, (TLRPC$TL_messages_stickerSet) null, (StickersAlert.StickersAlertDelegate) null);
            stickersAlert.setInstallDelegate(new StickersAlert.StickersAlertInstallDelegate() {
                public void onStickerSetUninstalled() {
                }

                public void onStickerSetInstalled() {
                    ((FeaturedStickerSetCell) view).setDrawProgress(true, true);
                    LongSparseArray access$300 = FeaturedStickersActivity.this.installingStickerSets;
                    TLRPC$StickerSetCovered tLRPC$StickerSetCovered = tLRPC$StickerSetCovered;
                    access$300.put(tLRPC$StickerSetCovered.set.id, tLRPC$StickerSetCovered);
                }
            });
            showDialog(stickersAlert);
        }
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.featuredStickersDidLoad) {
            if (this.unreadStickers == null) {
                this.unreadStickers = MediaDataController.getInstance(this.currentAccount).getUnreadStickerSets();
            }
            updateRows();
        } else if (i == NotificationCenter.stickersDidLoad) {
            updateVisibleTrendingSets();
        }
    }

    private void updateVisibleTrendingSets() {
        int findFirstVisibleItemPosition;
        int findLastVisibleItemPosition;
        LinearLayoutManager linearLayoutManager = this.layoutManager;
        if (linearLayoutManager != null && (findFirstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition()) != -1 && (findLastVisibleItemPosition = this.layoutManager.findLastVisibleItemPosition()) != -1) {
            this.listAdapter.notifyItemRangeChanged(findFirstVisibleItemPosition, (findLastVisibleItemPosition - findFirstVisibleItemPosition) + 1);
        }
    }

    private void updateRows() {
        this.rowCount = 0;
        ArrayList<TLRPC$StickerSetCovered> featuredStickerSets = MediaDataController.getInstance(this.currentAccount).getFeaturedStickerSets();
        if (!featuredStickerSets.isEmpty()) {
            int i = this.rowCount;
            this.stickersStartRow = i;
            this.stickersEndRow = i + featuredStickerSets.size();
            int size = this.rowCount + featuredStickerSets.size();
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

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            if (getItemViewType(i) == 0) {
                ArrayList<TLRPC$StickerSetCovered> featuredStickerSets = MediaDataController.getInstance(FeaturedStickersActivity.this.currentAccount).getFeaturedStickerSets();
                FeaturedStickerSetCell featuredStickerSetCell = (FeaturedStickerSetCell) viewHolder.itemView;
                featuredStickerSetCell.setTag(Integer.valueOf(i));
                TLRPC$StickerSetCovered tLRPC$StickerSetCovered = featuredStickerSets.get(i);
                boolean z = true;
                featuredStickerSetCell.setStickersSet(tLRPC$StickerSetCovered, i != featuredStickerSets.size() - 1, FeaturedStickersActivity.this.unreadStickers != null && FeaturedStickersActivity.this.unreadStickers.contains(Long.valueOf(tLRPC$StickerSetCovered.set.id)));
                if (FeaturedStickersActivity.this.installingStickerSets.indexOfKey(tLRPC$StickerSetCovered.set.id) < 0) {
                    z = false;
                }
                if (z && featuredStickerSetCell.isInstalled()) {
                    FeaturedStickersActivity.this.installingStickerSets.remove(tLRPC$StickerSetCovered.set.id);
                    z = false;
                }
                featuredStickerSetCell.setDrawProgress(z, false);
            }
        }

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return viewHolder.getItemViewType() == 0;
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onCreateViewHolder$0(View view) {
            FeaturedStickerSetCell featuredStickerSetCell = (FeaturedStickerSetCell) view.getParent();
            TLRPC$StickerSetCovered stickerSet = featuredStickerSetCell.getStickerSet();
            if (FeaturedStickersActivity.this.installingStickerSets.indexOfKey(stickerSet.set.id) < 0) {
                FeaturedStickersActivity.this.installingStickerSets.put(stickerSet.set.id, stickerSet);
                MediaDataController.getInstance(FeaturedStickersActivity.this.currentAccount).toggleStickerSet(FeaturedStickersActivity.this.getParentActivity(), stickerSet, 2, FeaturedStickersActivity.this, false, false);
                featuredStickerSetCell.setDrawProgress(true, true);
            }
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            TextInfoPrivacyCell textInfoPrivacyCell;
            if (i != 0) {
                TextInfoPrivacyCell textInfoPrivacyCell2 = new TextInfoPrivacyCell(this.mContext);
                textInfoPrivacyCell2.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                textInfoPrivacyCell = textInfoPrivacyCell2;
            } else {
                FeaturedStickerSetCell featuredStickerSetCell = new FeaturedStickerSetCell(this.mContext);
                featuredStickerSetCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                featuredStickerSetCell.setAddOnClickListener(new FeaturedStickersActivity$ListAdapter$$ExternalSyntheticLambda0(this));
                textInfoPrivacyCell = featuredStickerSetCell;
            }
            textInfoPrivacyCell.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(textInfoPrivacyCell);
        }

        public int getItemViewType(int i) {
            if ((i < FeaturedStickersActivity.this.stickersStartRow || i >= FeaturedStickersActivity.this.stickersEndRow) && i == FeaturedStickersActivity.this.stickersShadowRow) {
                return 1;
            }
            return 0;
        }
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{FeaturedStickerSetCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultIcon"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultTitle"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSelector"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "divider"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{FeaturedStickerSetCell.class}, new String[]{"progressPaint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "featuredStickers_buttonProgress"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{FeaturedStickerSetCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{FeaturedStickerSetCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText2"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{FeaturedStickerSetCell.class}, new String[]{"addButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "featuredStickers_buttonText"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{FeaturedStickerSetCell.class}, new String[]{"checkImage"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "featuredStickers_addedIcon"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE, new Class[]{FeaturedStickerSetCell.class}, new String[]{"addButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "featuredStickers_addButton"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, new Class[]{FeaturedStickerSetCell.class}, new String[]{"addButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "featuredStickers_addButtonPressed"));
        return arrayList;
    }
}
