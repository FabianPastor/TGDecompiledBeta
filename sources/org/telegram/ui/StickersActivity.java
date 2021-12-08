package org.telegram.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.collection.LongSparseArray;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.ListUpdateCallback;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SharedConfig;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BackDrawable;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.RadioColorCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.StickerSetCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.Bulletin;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.NumberTextView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.ReorderingBulletinLayout;
import org.telegram.ui.Components.ShareAlert;
import org.telegram.ui.Components.StickersAlert;
import org.telegram.ui.Components.TrendingStickersAlert;
import org.telegram.ui.Components.TrendingStickersLayout;
import org.telegram.ui.Components.URLSpanNoUnderline;

public class StickersActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private static final int MENU_ARCHIVE = 0;
    private static final int MENU_DELETE = 1;
    private static final int MENU_SHARE = 2;
    /* access modifiers changed from: private */
    public int activeReorderingRequests;
    private ActionBarMenuItem archiveMenuItem;
    /* access modifiers changed from: private */
    public int archivedInfoRow;
    /* access modifiers changed from: private */
    public int archivedRow;
    /* access modifiers changed from: private */
    public final int currentType;
    /* access modifiers changed from: private */
    public ActionBarMenuItem deleteMenuItem;
    /* access modifiers changed from: private */
    public int featuredRow;
    private DefaultItemAnimator itemAnimator;
    /* access modifiers changed from: private */
    public ItemTouchHelper itemTouchHelper;
    private LinearLayoutManager layoutManager;
    /* access modifiers changed from: private */
    public ListAdapter listAdapter;
    /* access modifiers changed from: private */
    public RecyclerListView listView;
    /* access modifiers changed from: private */
    public int loopInfoRow;
    /* access modifiers changed from: private */
    public int loopRow;
    /* access modifiers changed from: private */
    public int masksInfoRow;
    /* access modifiers changed from: private */
    public int masksRow;
    /* access modifiers changed from: private */
    public boolean needReorder;
    /* access modifiers changed from: private */
    public int rowCount;
    /* access modifiers changed from: private */
    public NumberTextView selectedCountTextView;
    private ActionBarMenuItem shareMenuItem;
    /* access modifiers changed from: private */
    public int stickersBotInfo;
    /* access modifiers changed from: private */
    public int stickersEndRow;
    /* access modifiers changed from: private */
    public int stickersShadowRow;
    /* access modifiers changed from: private */
    public int stickersStartRow;
    /* access modifiers changed from: private */
    public int suggestRow;
    private TrendingStickersAlert trendingStickersAlert;

    public class TouchHelperCallback extends ItemTouchHelper.Callback {
        public TouchHelperCallback() {
        }

        public boolean isLongPressDragEnabled() {
            return StickersActivity.this.listAdapter.hasSelected();
        }

        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            if (viewHolder.getItemViewType() != 0) {
                return makeMovementFlags(0, 0);
            }
            return makeMovementFlags(3, 0);
        }

        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder source, RecyclerView.ViewHolder target) {
            if (source.getItemViewType() != target.getItemViewType()) {
                return false;
            }
            StickersActivity.this.listAdapter.swapElements(source.getAdapterPosition(), target.getAdapterPosition());
            return true;
        }

        public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }

        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
            if (actionState == 0) {
                StickersActivity.this.sendReorder();
            } else {
                StickersActivity.this.listView.cancelClickRunnables(false);
                viewHolder.itemView.setPressed(true);
            }
            super.onSelectedChanged(viewHolder, actionState);
        }

        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        }

        public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            super.clearView(recyclerView, viewHolder);
            viewHolder.itemView.setPressed(false);
        }
    }

    public StickersActivity(int type) {
        this.currentType = type;
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        MediaDataController.getInstance(this.currentAccount).checkStickers(this.currentType);
        if (this.currentType == 0) {
            MediaDataController.getInstance(this.currentAccount).checkFeaturedStickers();
            MediaDataController.getInstance(this.currentAccount).checkStickers(1);
        }
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.stickersDidLoad);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.archivedStickersCountDidLoad);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.featuredStickersDidLoad);
        updateRows();
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.stickersDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.archivedStickersCountDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.featuredStickersDidLoad);
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonDrawable(new BackDrawable(false));
        this.actionBar.setAllowOverlayTitle(true);
        if (this.currentType == 0) {
            this.actionBar.setTitle(LocaleController.getString("StickersName", NUM));
        } else {
            this.actionBar.setTitle(LocaleController.getString("Masks", NUM));
        }
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    if (StickersActivity.this.onBackPressed()) {
                        StickersActivity.this.finishFragment();
                    }
                } else if (id != 0 && id != 1 && id != 2) {
                } else {
                    if (StickersActivity.this.needReorder) {
                        StickersActivity.this.sendReorder();
                    } else if (StickersActivity.this.activeReorderingRequests == 0) {
                        StickersActivity.this.listAdapter.processSelectionMenu(id);
                    }
                }
            }
        });
        ActionBarMenu actionMode = this.actionBar.createActionMode();
        NumberTextView numberTextView = new NumberTextView(actionMode.getContext());
        this.selectedCountTextView = numberTextView;
        numberTextView.setTextSize(18);
        this.selectedCountTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.selectedCountTextView.setTextColor(Theme.getColor("actionBarActionModeDefaultIcon"));
        actionMode.addView(this.selectedCountTextView, LayoutHelper.createLinear(0, -1, 1.0f, 72, 0, 0, 0));
        this.selectedCountTextView.setOnTouchListener(StickersActivity$$ExternalSyntheticLambda1.INSTANCE);
        this.shareMenuItem = actionMode.addItemWithWidth(2, NUM, AndroidUtilities.dp(54.0f));
        this.archiveMenuItem = actionMode.addItemWithWidth(0, NUM, AndroidUtilities.dp(54.0f));
        this.deleteMenuItem = actionMode.addItemWithWidth(1, NUM, AndroidUtilities.dp(54.0f));
        this.listAdapter = new ListAdapter(context, MediaDataController.getInstance(this.currentAccount).getStickerSets(this.currentType));
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        frameLayout.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
        RecyclerListView recyclerListView = new RecyclerListView(context);
        this.listView = recyclerListView;
        recyclerListView.setFocusable(true);
        this.listView.setTag(7);
        AnonymousClass2 r4 = new LinearLayoutManager(context) {
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }

            /* access modifiers changed from: protected */
            public void calculateExtraLayoutSpace(RecyclerView.State state, int[] extraLayoutSpace) {
                extraLayoutSpace[1] = StickersActivity.this.listView.getHeight();
            }
        };
        this.layoutManager = r4;
        r4.setOrientation(1);
        this.listView.setLayoutManager(this.layoutManager);
        ItemTouchHelper itemTouchHelper2 = new ItemTouchHelper(new TouchHelperCallback());
        this.itemTouchHelper = itemTouchHelper2;
        itemTouchHelper2.attachToRecyclerView(this.listView);
        DefaultItemAnimator defaultItemAnimator = (DefaultItemAnimator) this.listView.getItemAnimator();
        this.itemAnimator = defaultItemAnimator;
        defaultItemAnimator.setSupportsChangeAnimations(false);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setAdapter(this.listAdapter);
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new StickersActivity$$ExternalSyntheticLambda4(this, context));
        this.listView.setOnItemLongClickListener((RecyclerListView.OnItemLongClickListener) new StickersActivity$$ExternalSyntheticLambda5(this));
        return this.fragmentView;
    }

    static /* synthetic */ boolean lambda$createView$0(View v, MotionEvent event) {
        return true;
    }

    /* renamed from: lambda$createView$2$org-telegram-ui-StickersActivity  reason: not valid java name */
    public /* synthetic */ void m3899lambda$createView$2$orgtelegramuiStickersActivity(Context context, View view, int position) {
        if (position < this.stickersStartRow || position >= this.stickersEndRow || getParentActivity() == null) {
            if (position == this.featuredRow) {
                TrendingStickersAlert trendingStickersAlert2 = new TrendingStickersAlert(context, this, new TrendingStickersLayout(context, new TrendingStickersLayout.Delegate() {
                    public void onStickerSetAdd(TLRPC.StickerSetCovered stickerSet, boolean primary) {
                        MediaDataController.getInstance(StickersActivity.this.currentAccount).toggleStickerSet(StickersActivity.this.getParentActivity(), stickerSet, 2, StickersActivity.this, false, false);
                    }

                    public void onStickerSetRemove(TLRPC.StickerSetCovered stickerSet) {
                        MediaDataController.getInstance(StickersActivity.this.currentAccount).toggleStickerSet(StickersActivity.this.getParentActivity(), stickerSet, 0, StickersActivity.this, false, false);
                    }
                }), (Theme.ResourcesProvider) null);
                this.trendingStickersAlert = trendingStickersAlert2;
                trendingStickersAlert2.show();
            } else if (position == this.archivedRow) {
                presentFragment(new ArchivedStickersActivity(this.currentType));
            } else if (position == this.masksRow) {
                presentFragment(new StickersActivity(1));
            } else if (position == this.suggestRow) {
                if (getParentActivity() != null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
                    builder.setTitle(LocaleController.getString("SuggestStickers", NUM));
                    String[] items = {LocaleController.getString("SuggestStickersAll", NUM), LocaleController.getString("SuggestStickersInstalled", NUM), LocaleController.getString("SuggestStickersNone", NUM)};
                    LinearLayout linearLayout = new LinearLayout(getParentActivity());
                    linearLayout.setOrientation(1);
                    builder.setView(linearLayout);
                    int a = 0;
                    while (a < items.length) {
                        RadioColorCell cell = new RadioColorCell(getParentActivity());
                        cell.setPadding(AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f), 0);
                        cell.setTag(Integer.valueOf(a));
                        cell.setCheckColor(Theme.getColor("radioBackground"), Theme.getColor("dialogRadioBackgroundChecked"));
                        cell.setTextAndValue(items[a], SharedConfig.suggestStickers == a);
                        linearLayout.addView(cell);
                        cell.setOnClickListener(new StickersActivity$$ExternalSyntheticLambda0(this, builder));
                        a++;
                    }
                    showDialog(builder.create());
                }
            } else if (position == this.loopRow) {
                SharedConfig.toggleLoopStickers();
                this.listAdapter.notifyItemChanged(this.loopRow, 0);
            }
        } else if (!this.listAdapter.hasSelected()) {
            TLRPC.TL_messages_stickerSet stickerSet = (TLRPC.TL_messages_stickerSet) this.listAdapter.stickerSets.get(position - this.stickersStartRow);
            ArrayList<TLRPC.Document> stickers = stickerSet.documents;
            if (stickers != null && !stickers.isEmpty()) {
                showDialog(new StickersAlert((Context) getParentActivity(), (BaseFragment) this, (TLRPC.InputStickerSet) null, stickerSet, (StickersAlert.StickersAlertDelegate) null));
            }
        } else {
            this.listAdapter.toggleSelected(position);
        }
    }

    /* renamed from: lambda$createView$1$org-telegram-ui-StickersActivity  reason: not valid java name */
    public /* synthetic */ void m3898lambda$createView$1$orgtelegramuiStickersActivity(AlertDialog.Builder builder, View v) {
        SharedConfig.setSuggestStickers(((Integer) v.getTag()).intValue());
        this.listAdapter.notifyItemChanged(this.suggestRow);
        builder.getDismissRunnable().run();
    }

    /* renamed from: lambda$createView$3$org-telegram-ui-StickersActivity  reason: not valid java name */
    public /* synthetic */ boolean m3900lambda$createView$3$orgtelegramuiStickersActivity(View view, int position) {
        if (this.listAdapter.hasSelected() || position < this.stickersStartRow || position >= this.stickersEndRow) {
            return false;
        }
        this.listAdapter.toggleSelected(position);
        return true;
    }

    public boolean onBackPressed() {
        if (!this.listAdapter.hasSelected()) {
            return super.onBackPressed();
        }
        this.listAdapter.clearSelected();
        return false;
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.stickersDidLoad) {
            int type = args[0].intValue();
            int i = this.currentType;
            if (type == i) {
                updateRows();
            } else if (i == 0 && type == 1) {
                this.listAdapter.notifyItemChanged(this.masksRow);
            }
        } else if (id == NotificationCenter.featuredStickersDidLoad) {
            ListAdapter listAdapter2 = this.listAdapter;
            if (listAdapter2 != null) {
                listAdapter2.notifyItemChanged(0);
            }
        } else if (id == NotificationCenter.archivedStickersCountDidLoad && args[0].intValue() == this.currentType) {
            updateRows();
        }
    }

    /* access modifiers changed from: private */
    public void sendReorder() {
        if (this.needReorder) {
            MediaDataController.getInstance(this.currentAccount).calcNewHash(this.currentType);
            this.needReorder = false;
            this.activeReorderingRequests++;
            TLRPC.TL_messages_reorderStickerSets req = new TLRPC.TL_messages_reorderStickerSets();
            req.masks = this.currentType == 1;
            for (int a = 0; a < this.listAdapter.stickerSets.size(); a++) {
                req.order.add(Long.valueOf(((TLRPC.TL_messages_stickerSet) this.listAdapter.stickerSets.get(a)).set.id));
            }
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new StickersActivity$$ExternalSyntheticLambda3(this));
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.stickersDidLoad, Integer.valueOf(this.currentType));
        }
    }

    /* renamed from: lambda$sendReorder$4$org-telegram-ui-StickersActivity  reason: not valid java name */
    public /* synthetic */ void m3901lambda$sendReorder$4$orgtelegramuiStickersActivity() {
        this.activeReorderingRequests--;
    }

    /* renamed from: lambda$sendReorder$5$org-telegram-ui-StickersActivity  reason: not valid java name */
    public /* synthetic */ void m3902lambda$sendReorder$5$orgtelegramuiStickersActivity(TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new StickersActivity$$ExternalSyntheticLambda2(this));
    }

    private void updateRows() {
        MediaDataController mediaDataController = MediaDataController.getInstance(this.currentAccount);
        final List<TLRPC.TL_messages_stickerSet> newList = mediaDataController.getStickerSets(this.currentType);
        DiffUtil.DiffResult diffResult = null;
        if (this.listAdapter != null) {
            if (!this.isPaused) {
                diffResult = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                    final List<TLRPC.TL_messages_stickerSet> oldList;

                    {
                        this.oldList = StickersActivity.this.listAdapter.stickerSets;
                    }

                    public int getOldListSize() {
                        return this.oldList.size();
                    }

                    public int getNewListSize() {
                        return newList.size();
                    }

                    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                        return this.oldList.get(oldItemPosition).set.id == ((TLRPC.TL_messages_stickerSet) newList.get(newItemPosition)).set.id;
                    }

                    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                        TLRPC.StickerSet oldSet = this.oldList.get(oldItemPosition).set;
                        TLRPC.StickerSet newSet = ((TLRPC.TL_messages_stickerSet) newList.get(newItemPosition)).set;
                        return TextUtils.equals(oldSet.title, newSet.title) && oldSet.count == newSet.count;
                    }
                });
            }
            this.listAdapter.setStickerSets(newList);
        }
        this.rowCount = 0;
        int i = this.currentType;
        if (i == 0) {
            int i2 = 0 + 1;
            this.rowCount = i2;
            this.suggestRow = 0;
            int i3 = i2 + 1;
            this.rowCount = i3;
            this.loopRow = i2;
            int i4 = i3 + 1;
            this.rowCount = i4;
            this.loopInfoRow = i3;
            this.rowCount = i4 + 1;
            this.featuredRow = i4;
        } else {
            this.suggestRow = -1;
            this.loopRow = -1;
            this.loopInfoRow = -1;
            this.featuredRow = -1;
        }
        int i5 = 2;
        if (mediaDataController.getArchivedStickersCount(i) != 0) {
            boolean inserted = this.archivedRow == -1;
            int i6 = this.rowCount;
            int i7 = i6 + 1;
            this.rowCount = i7;
            this.archivedRow = i6;
            if (this.currentType == 1) {
                this.rowCount = i7 + 1;
            } else {
                i7 = -1;
            }
            this.archivedInfoRow = i7;
            ListAdapter listAdapter2 = this.listAdapter;
            if (listAdapter2 != null && inserted) {
                if (i7 == -1) {
                    i5 = 1;
                }
                listAdapter2.notifyItemRangeInserted(i6, i5);
            }
        } else {
            int oldArchivedRow = this.archivedRow;
            int oldArchivedInfoRow = this.archivedInfoRow;
            this.archivedRow = -1;
            this.archivedInfoRow = -1;
            ListAdapter listAdapter3 = this.listAdapter;
            if (!(listAdapter3 == null || oldArchivedRow == -1)) {
                if (oldArchivedInfoRow == -1) {
                    i5 = 1;
                }
                listAdapter3.notifyItemRangeRemoved(oldArchivedRow, i5);
            }
        }
        if (this.currentType == 0) {
            int i8 = this.rowCount;
            int i9 = i8 + 1;
            this.rowCount = i9;
            this.masksRow = i8;
            this.rowCount = i9 + 1;
            this.stickersBotInfo = i9;
        } else {
            this.masksRow = -1;
            this.stickersBotInfo = -1;
        }
        int stickerSetsCount = newList.size();
        if (stickerSetsCount > 0) {
            int i10 = this.rowCount;
            this.stickersStartRow = i10;
            int i11 = i10 + stickerSetsCount;
            this.rowCount = i11;
            this.stickersEndRow = i11;
            if (this.currentType != 1) {
                this.rowCount = i11 + 1;
                this.stickersShadowRow = i11;
                this.masksInfoRow = -1;
            } else {
                this.rowCount = i11 + 1;
                this.masksInfoRow = i11;
                this.stickersShadowRow = -1;
            }
        } else {
            this.stickersStartRow = -1;
            this.stickersEndRow = -1;
            this.stickersShadowRow = -1;
            this.masksInfoRow = -1;
        }
        ListAdapter listAdapter4 = this.listAdapter;
        if (listAdapter4 != null && diffResult != null) {
            final int startRow = this.stickersStartRow;
            if (startRow < 0) {
                startRow = this.rowCount;
            }
            listAdapter4.notifyItemRangeChanged(0, startRow);
            diffResult.dispatchUpdatesTo((ListUpdateCallback) new ListUpdateCallback() {
                public void onInserted(int position, int count) {
                    StickersActivity.this.listAdapter.notifyItemRangeInserted(startRow + position, count);
                }

                public void onRemoved(int position, int count) {
                    StickersActivity.this.listAdapter.notifyItemRangeRemoved(startRow + position, count);
                }

                public void onMoved(int fromPosition, int toPosition) {
                }

                public void onChanged(int position, int count, Object payload) {
                    StickersActivity.this.listAdapter.notifyItemRangeChanged(startRow + position, count);
                }
            });
        }
    }

    public void onResume() {
        super.onResume();
        ListAdapter listAdapter2 = this.listAdapter;
        if (listAdapter2 != null) {
            listAdapter2.notifyDataSetChanged();
        }
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        public static final int UPDATE_DIVIDER = 3;
        public static final int UPDATE_LOOP_STICKERS = 0;
        public static final int UPDATE_REORDERABLE = 2;
        public static final int UPDATE_SELECTION = 1;
        private Context mContext;
        private final LongSparseArray<Boolean> selectedItems = new LongSparseArray<>();
        /* access modifiers changed from: private */
        public final List<TLRPC.TL_messages_stickerSet> stickerSets;

        public ListAdapter(Context context, List<TLRPC.TL_messages_stickerSet> stickerSets2) {
            ArrayList arrayList = new ArrayList();
            this.stickerSets = arrayList;
            this.mContext = context;
            arrayList.addAll(stickerSets2);
        }

        public void setStickerSets(List<TLRPC.TL_messages_stickerSet> stickerSets2) {
            this.stickerSets.clear();
            this.stickerSets.addAll(stickerSets2);
        }

        public int getItemCount() {
            return StickersActivity.this.rowCount;
        }

        public long getItemId(int i) {
            if (i >= StickersActivity.this.stickersStartRow && i < StickersActivity.this.stickersEndRow) {
                return this.stickerSets.get(i - StickersActivity.this.stickersStartRow).set.id;
            }
            if (i == StickersActivity.this.suggestRow || i == StickersActivity.this.loopInfoRow || i == StickersActivity.this.archivedRow || i == StickersActivity.this.archivedInfoRow || i == StickersActivity.this.featuredRow || i == StickersActivity.this.stickersBotInfo || i == StickersActivity.this.masksRow) {
                return -2147483648L;
            }
            return (long) i;
        }

        /* access modifiers changed from: private */
        public void processSelectionMenu(int which) {
            String buttonText;
            TextView button;
            if (which == 2) {
                StringBuilder stringBuilder = new StringBuilder();
                int size = this.stickerSets.size();
                for (int i = 0; i < size; i++) {
                    TLRPC.TL_messages_stickerSet stickerSet = this.stickerSets.get(i);
                    if (this.selectedItems.get(stickerSet.set.id, false).booleanValue()) {
                        if (stringBuilder.length() != 0) {
                            stringBuilder.append("\n");
                        }
                        stringBuilder.append(StickersActivity.this.getLinkForSet(stickerSet));
                    }
                }
                String link = stringBuilder.toString();
                ShareAlert shareAlert = ShareAlert.createShareAlert(StickersActivity.this.fragmentView.getContext(), (MessageObject) null, link, false, link, false);
                shareAlert.setDelegate(new ShareAlert.ShareAlertDelegate() {
                    public void didShare() {
                        ListAdapter.this.clearSelected();
                    }

                    public boolean didCopy() {
                        ListAdapter.this.clearSelected();
                        return true;
                    }
                });
                shareAlert.show();
            } else if (which == 0 || which == 1) {
                ArrayList<TLRPC.StickerSet> stickerSetList = new ArrayList<>(this.selectedItems.size());
                int size2 = this.stickerSets.size();
                for (int i2 = 0; i2 < size2; i2++) {
                    TLRPC.StickerSet stickerSet2 = this.stickerSets.get(i2).set;
                    if (this.selectedItems.get(stickerSet2.id, false).booleanValue()) {
                        stickerSetList.add(stickerSet2);
                    }
                }
                int count = stickerSetList.size();
                switch (count) {
                    case 0:
                        return;
                    case 1:
                        int i3 = 0;
                        int size3 = this.stickerSets.size();
                        while (true) {
                            if (i3 < size3) {
                                TLRPC.TL_messages_stickerSet stickerSet3 = this.stickerSets.get(i3);
                                if (this.selectedItems.get(stickerSet3.set.id, false).booleanValue()) {
                                    processSelectionOption(which, stickerSet3);
                                } else {
                                    i3++;
                                }
                            }
                        }
                        StickersActivity.this.listAdapter.clearSelected();
                        return;
                    default:
                        AlertDialog.Builder builder = new AlertDialog.Builder((Context) StickersActivity.this.getParentActivity());
                        if (which == 1) {
                            builder.setTitle(LocaleController.formatString("DeleteStickerSetsAlertTitle", NUM, LocaleController.formatPluralString("StickerSets", count)));
                            builder.setMessage(LocaleController.formatString("DeleteStickersAlertMessage", NUM, Integer.valueOf(count)));
                            buttonText = LocaleController.getString("Delete", NUM);
                        } else {
                            builder.setTitle(LocaleController.formatString("ArchiveStickerSetsAlertTitle", NUM, LocaleController.formatPluralString("StickerSets", count)));
                            builder.setMessage(LocaleController.formatString("ArchiveStickersAlertMessage", NUM, Integer.valueOf(count)));
                            buttonText = LocaleController.getString("Archive", NUM);
                        }
                        builder.setPositiveButton(buttonText, new StickersActivity$ListAdapter$$ExternalSyntheticLambda0(this, stickerSetList, which));
                        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                        AlertDialog dialog = builder.create();
                        StickersActivity.this.showDialog(dialog);
                        if (which == 1 && (button = (TextView) dialog.getButton(-1)) != null) {
                            button.setTextColor(Theme.getColor("dialogTextRed2"));
                            return;
                        }
                        return;
                }
            }
        }

        /* renamed from: lambda$processSelectionMenu$0$org-telegram-ui-StickersActivity$ListAdapter  reason: not valid java name */
        public /* synthetic */ void m3906xb873efa7(ArrayList stickerSetList, int which, DialogInterface dialog, int which1) {
            StickersActivity.this.listAdapter.clearSelected();
            MediaDataController.getInstance(StickersActivity.this.currentAccount).toggleStickerSets(stickerSetList, StickersActivity.this.currentType, which == 1 ? 0 : 1, StickersActivity.this, true);
        }

        private void processSelectionOption(int which, TLRPC.TL_messages_stickerSet stickerSet) {
            int index;
            if (which == 0) {
                MediaDataController.getInstance(StickersActivity.this.currentAccount).toggleStickerSet(StickersActivity.this.getParentActivity(), stickerSet, !stickerSet.set.archived ? 1 : 2, StickersActivity.this, true, true);
            } else if (which == 1) {
                MediaDataController.getInstance(StickersActivity.this.currentAccount).toggleStickerSet(StickersActivity.this.getParentActivity(), stickerSet, 0, StickersActivity.this, true, true);
            } else if (which == 2) {
                try {
                    Intent intent = new Intent("android.intent.action.SEND");
                    intent.setType("text/plain");
                    intent.putExtra("android.intent.extra.TEXT", StickersActivity.this.getLinkForSet(stickerSet));
                    StickersActivity.this.getParentActivity().startActivityForResult(Intent.createChooser(intent, LocaleController.getString("StickersShare", NUM)), 500);
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
            } else if (which == 3) {
                try {
                    Locale locale = Locale.US;
                    ((ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", String.format(locale, "https://" + MessagesController.getInstance(StickersActivity.this.currentAccount).linkPrefix + "/addstickers/%s", new Object[]{stickerSet.set.short_name})));
                    BulletinFactory.createCopyLinkBulletin((BaseFragment) StickersActivity.this).show();
                } catch (Exception e2) {
                    FileLog.e((Throwable) e2);
                }
            } else if (which == 4 && (index = this.stickerSets.indexOf(stickerSet)) >= 0) {
                StickersActivity.this.listAdapter.toggleSelected(StickersActivity.this.stickersStartRow + index);
            }
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            String value;
            boolean z = true;
            switch (holder.getItemViewType()) {
                case 0:
                    int row = position - StickersActivity.this.stickersStartRow;
                    StickerSetCell stickerSetCell = (StickerSetCell) holder.itemView;
                    TLRPC.TL_messages_stickerSet tL_messages_stickerSet = this.stickerSets.get(row);
                    if (row == this.stickerSets.size() - 1) {
                        z = false;
                    }
                    stickerSetCell.setStickersSet(tL_messages_stickerSet, z);
                    stickerSetCell.setChecked(this.selectedItems.get(getItemId(position), false).booleanValue(), false);
                    stickerSetCell.setReorderable(hasSelected(), false);
                    return;
                case 1:
                    TextInfoPrivacyCell infoPrivacyCell = (TextInfoPrivacyCell) holder.itemView;
                    if (position == StickersActivity.this.stickersBotInfo) {
                        infoPrivacyCell.setText(addStickersBotSpan(LocaleController.getString("StickersBotInfo", NUM)));
                        return;
                    } else if (position == StickersActivity.this.archivedInfoRow) {
                        if (StickersActivity.this.currentType == 0) {
                            infoPrivacyCell.setText(LocaleController.getString("ArchivedStickersInfo", NUM));
                            return;
                        } else {
                            infoPrivacyCell.setText(LocaleController.getString("ArchivedMasksInfo", NUM));
                            return;
                        }
                    } else if (position == StickersActivity.this.loopInfoRow) {
                        infoPrivacyCell.setText(LocaleController.getString("LoopAnimatedStickersInfo", NUM));
                        return;
                    } else if (position == StickersActivity.this.masksInfoRow) {
                        infoPrivacyCell.setText(LocaleController.getString("MasksInfo", NUM));
                        return;
                    } else {
                        return;
                    }
                case 2:
                    TextSettingsCell settingsCell = (TextSettingsCell) holder.itemView;
                    String str = "";
                    if (position == StickersActivity.this.featuredRow) {
                        int count = MediaDataController.getInstance(StickersActivity.this.currentAccount).getFeaturedStickerSets().size();
                        String string = LocaleController.getString("FeaturedStickers", NUM);
                        if (count > 0) {
                            str = Integer.toString(count);
                        }
                        settingsCell.setTextAndValue(string, str, true);
                        return;
                    } else if (position == StickersActivity.this.archivedRow) {
                        int count2 = MediaDataController.getInstance(StickersActivity.this.currentAccount).getArchivedStickersCount(StickersActivity.this.currentType);
                        if (count2 > 0) {
                            str = Integer.toString(count2);
                        }
                        String value2 = str;
                        if (StickersActivity.this.currentType == 0) {
                            settingsCell.setTextAndValue(LocaleController.getString("ArchivedStickers", NUM), value2, true);
                            return;
                        } else {
                            settingsCell.setTextAndValue(LocaleController.getString("ArchivedMasks", NUM), value2, true);
                            return;
                        }
                    } else if (position == StickersActivity.this.masksRow) {
                        MediaDataController mediaDataController = MediaDataController.getInstance(StickersActivity.this.currentAccount);
                        int count3 = mediaDataController.getStickerSets(1).size() + mediaDataController.getArchivedStickersCount(1);
                        String string2 = LocaleController.getString("Masks", NUM);
                        if (count3 > 0) {
                            str = Integer.toString(count3);
                        }
                        settingsCell.setTextAndValue(string2, str, false);
                        return;
                    } else if (position == StickersActivity.this.suggestRow) {
                        switch (SharedConfig.suggestStickers) {
                            case 0:
                                value = LocaleController.getString("SuggestStickersAll", NUM);
                                break;
                            case 1:
                                value = LocaleController.getString("SuggestStickersInstalled", NUM);
                                break;
                            default:
                                value = LocaleController.getString("SuggestStickersNone", NUM);
                                break;
                        }
                        settingsCell.setTextAndValue(LocaleController.getString("SuggestStickers", NUM), value, true);
                        return;
                    } else {
                        return;
                    }
                case 3:
                    if (position == StickersActivity.this.stickersShadowRow) {
                        holder.itemView.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                        return;
                    }
                    return;
                case 4:
                    if (position == StickersActivity.this.loopRow) {
                        ((TextCheckCell) holder.itemView).setTextAndCheck(LocaleController.getString("LoopAnimatedStickers", NUM), SharedConfig.loopStickers, true);
                        return;
                    }
                    return;
                default:
                    return;
            }
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, List payloads) {
            if (payloads.isEmpty()) {
                onBindViewHolder(holder, position);
                return;
            }
            boolean z = false;
            switch (holder.getItemViewType()) {
                case 0:
                    if (position >= StickersActivity.this.stickersStartRow && position < StickersActivity.this.stickersEndRow) {
                        StickerSetCell stickerSetCell = (StickerSetCell) holder.itemView;
                        if (payloads.contains(1)) {
                            stickerSetCell.setChecked(this.selectedItems.get(getItemId(position), false).booleanValue());
                        }
                        if (payloads.contains(2)) {
                            stickerSetCell.setReorderable(hasSelected());
                        }
                        if (payloads.contains(3)) {
                            if (position - StickersActivity.this.stickersStartRow != this.stickerSets.size() - 1) {
                                z = true;
                            }
                            stickerSetCell.setNeedDivider(z);
                            return;
                        }
                        return;
                    }
                    return;
                case 4:
                    if (payloads.contains(0) && position == StickersActivity.this.loopRow) {
                        ((TextCheckCell) holder.itemView).setChecked(SharedConfig.loopStickers);
                        return;
                    }
                    return;
                default:
                    return;
            }
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int type = holder.getItemViewType();
            return type == 0 || type == 2 || type == 4;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    StickerSetCell stickerSetCell = new StickerSetCell(this.mContext, 1);
                    stickerSetCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    StickerSetCell stickerSetCell2 = stickerSetCell;
                    stickerSetCell2.setOnReorderButtonTouchListener(new StickersActivity$ListAdapter$$ExternalSyntheticLambda3(this, stickerSetCell2));
                    stickerSetCell2.setOnOptionsClick(new StickersActivity$ListAdapter$$ExternalSyntheticLambda2(this));
                    view = stickerSetCell;
                    break;
                case 1:
                    view = new TextInfoPrivacyCell(this.mContext);
                    view.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                    break;
                case 2:
                    View view2 = new TextSettingsCell(this.mContext);
                    view2.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    view = view2;
                    break;
                case 3:
                    view = new ShadowSectionCell(this.mContext);
                    break;
                default:
                    View view3 = new TextCheckCell(this.mContext);
                    view3.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    view = view3;
                    break;
            }
            view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(view);
        }

        /* renamed from: lambda$onCreateViewHolder$1$org-telegram-ui-StickersActivity$ListAdapter  reason: not valid java name */
        public /* synthetic */ boolean m3903x20043416(StickerSetCell stickerSetCell, View v, MotionEvent event) {
            if (event.getAction() != 0) {
                return false;
            }
            StickersActivity.this.itemTouchHelper.startDrag(StickersActivity.this.listView.getChildViewHolder(stickerSetCell));
            return false;
        }

        /* renamed from: lambda$onCreateViewHolder$3$org-telegram-ui-StickersActivity$ListAdapter  reason: not valid java name */
        public /* synthetic */ void m3905x92var_var_(View v) {
            int[] icons;
            CharSequence[] items;
            int[] options;
            TLRPC.TL_messages_stickerSet stickerSet = ((StickerSetCell) v.getParent()).getStickersSet();
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) StickersActivity.this.getParentActivity());
            builder.setTitle(stickerSet.set.title);
            if (stickerSet.set.official) {
                options = new int[]{0, 4};
                items = new CharSequence[]{LocaleController.getString("StickersHide", NUM), LocaleController.getString("StickersReorder", NUM)};
                icons = new int[]{NUM, NUM};
            } else {
                items = new CharSequence[]{LocaleController.getString("StickersHide", NUM), LocaleController.getString("StickersCopy", NUM), LocaleController.getString("StickersReorder", NUM), LocaleController.getString("StickersShare", NUM), LocaleController.getString("StickersRemove", NUM)};
                icons = new int[]{NUM, NUM, NUM, NUM, NUM};
                options = new int[]{0, 3, 4, 2, 1};
            }
            builder.setItems(items, icons, new StickersActivity$ListAdapter$$ExternalSyntheticLambda1(this, options, stickerSet));
            AlertDialog dialog = builder.create();
            StickersActivity.this.showDialog(dialog);
            if (options[options.length - 1] == 1) {
                dialog.setItemColor(items.length - 1, Theme.getColor("dialogTextRed2"), Theme.getColor("dialogRedIcon"));
            }
        }

        /* renamed from: lambda$onCreateViewHolder$2$org-telegram-ui-StickersActivity$ListAdapter  reason: not valid java name */
        public /* synthetic */ void m3904xd97bc1b5(int[] options, TLRPC.TL_messages_stickerSet stickerSet, DialogInterface dialog, int which) {
            processSelectionOption(options[which], stickerSet);
        }

        public int getItemViewType(int i) {
            if (i >= StickersActivity.this.stickersStartRow && i < StickersActivity.this.stickersEndRow) {
                return 0;
            }
            if (i == StickersActivity.this.stickersBotInfo || i == StickersActivity.this.archivedInfoRow || i == StickersActivity.this.loopInfoRow || i == StickersActivity.this.masksInfoRow) {
                return 1;
            }
            if (i == StickersActivity.this.featuredRow || i == StickersActivity.this.archivedRow || i == StickersActivity.this.masksRow || i == StickersActivity.this.suggestRow) {
                return 2;
            }
            if (i == StickersActivity.this.stickersShadowRow) {
                return 3;
            }
            if (i == StickersActivity.this.loopRow) {
                return 4;
            }
            return 0;
        }

        public void swapElements(int fromIndex, int toIndex) {
            if (fromIndex != toIndex) {
                boolean unused = StickersActivity.this.needReorder = true;
            }
            MediaDataController mediaDataController = MediaDataController.getInstance(StickersActivity.this.currentAccount);
            int index1 = fromIndex - StickersActivity.this.stickersStartRow;
            int index2 = toIndex - StickersActivity.this.stickersStartRow;
            swapListElements(this.stickerSets, index1, index2);
            swapListElements(mediaDataController.getStickerSets(StickersActivity.this.currentType), index1, index2);
            notifyItemMoved(fromIndex, toIndex);
            if (fromIndex == StickersActivity.this.stickersEndRow - 1 || toIndex == StickersActivity.this.stickersEndRow - 1) {
                notifyItemRangeChanged(fromIndex, 3);
                notifyItemRangeChanged(toIndex, 3);
            }
        }

        private void swapListElements(List<TLRPC.TL_messages_stickerSet> list, int index1, int index2) {
            list.set(index1, list.get(index2));
            list.set(index2, list.get(index1));
        }

        public void toggleSelected(int position) {
            long id = getItemId(position);
            LongSparseArray<Boolean> longSparseArray = this.selectedItems;
            longSparseArray.put(id, Boolean.valueOf(!longSparseArray.get(id, false).booleanValue()));
            notifyItemChanged(position, 1);
            checkActionMode();
        }

        public void clearSelected() {
            this.selectedItems.clear();
            notifyStickersItemsChanged(1);
            checkActionMode();
        }

        public boolean hasSelected() {
            return this.selectedItems.indexOfValue(true) != -1;
        }

        public int getSelectedCount() {
            int count = 0;
            int size = this.selectedItems.size();
            for (int i = 0; i < size; i++) {
                if (this.selectedItems.valueAt(i).booleanValue()) {
                    count++;
                }
            }
            return count;
        }

        private void checkActionMode() {
            int selectedCount = StickersActivity.this.listAdapter.getSelectedCount();
            boolean actionModeShowed = StickersActivity.this.actionBar.isActionModeShowed();
            if (selectedCount > 0) {
                checkActionModeIcons();
                StickersActivity.this.selectedCountTextView.setNumber(selectedCount, actionModeShowed);
                if (!actionModeShowed) {
                    StickersActivity.this.actionBar.showActionMode();
                    notifyStickersItemsChanged(2);
                    if (!SharedConfig.stickersReorderingHintUsed) {
                        SharedConfig.setStickersReorderingHintUsed(true);
                        Bulletin.make((FrameLayout) StickersActivity.this.parentLayout, (Bulletin.Layout) new ReorderingBulletinLayout(this.mContext, LocaleController.getString("StickersReorderHint", NUM), (Theme.ResourcesProvider) null), 3250).show();
                    }
                }
            } else if (actionModeShowed) {
                StickersActivity.this.actionBar.hideActionMode();
                notifyStickersItemsChanged(2);
            }
        }

        private void checkActionModeIcons() {
            int i;
            if (hasSelected()) {
                boolean canDelete = true;
                int i2 = 0;
                int size = this.stickerSets.size();
                while (true) {
                    i = 0;
                    if (i2 < size) {
                        if (this.selectedItems.get(this.stickerSets.get(i2).set.id, false).booleanValue() && this.stickerSets.get(i2).set.official) {
                            canDelete = false;
                            break;
                        }
                        i2++;
                    } else {
                        break;
                    }
                }
                if (!canDelete) {
                    i = 8;
                }
                int visibility = i;
                if (StickersActivity.this.deleteMenuItem.getVisibility() != visibility) {
                    StickersActivity.this.deleteMenuItem.setVisibility(visibility);
                }
            }
        }

        private void notifyStickersItemsChanged(Object payload) {
            notifyItemRangeChanged(StickersActivity.this.stickersStartRow, StickersActivity.this.stickersEndRow - StickersActivity.this.stickersStartRow, payload);
        }

        private CharSequence addStickersBotSpan(String text) {
            int index = text.indexOf("@stickers");
            if (index == -1) {
                return text;
            }
            try {
                SpannableStringBuilder stringBuilder = new SpannableStringBuilder(text);
                stringBuilder.setSpan(new URLSpanNoUnderline("@stickers") {
                    public void onClick(View widget) {
                        MessagesController.getInstance(StickersActivity.this.currentAccount).openByUserName("stickers", StickersActivity.this, 3);
                    }
                }, index, "@stickers".length() + index, 18);
                return stringBuilder;
            } catch (Exception e) {
                FileLog.e((Throwable) e);
                return text;
            }
        }
    }

    /* access modifiers changed from: private */
    public String getLinkForSet(TLRPC.TL_messages_stickerSet stickerSet) {
        Locale locale = Locale.US;
        return String.format(locale, "https://" + MessagesController.getInstance(this.currentAccount).linkPrefix + "/addstickers/%s", new Object[]{stickerSet.set.short_name});
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{StickerSetCell.class, TextSettingsCell.class, TextCheckCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        themeDescriptions.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultIcon"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultTitle"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSelector"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_AM_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarActionModeDefaultIcon"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_AM_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarActionModeDefault"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_AM_TOPBACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarActionModeDefaultTop"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_AM_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarActionModeDefaultSelector"));
        themeDescriptions.add(new ThemeDescription(this.selectedCountTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarActionModeDefaultIcon"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "divider"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrack"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrackChecked"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText4"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_LINKCOLOR, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteLinkText"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteValueText"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{StickerSetCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{StickerSetCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText2"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, new Class[]{StickerSetCell.class}, new String[]{"optionsButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "stickers_menuSelector"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{StickerSetCell.class}, new String[]{"optionsButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "stickers_menu"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{StickerSetCell.class}, new String[]{"reorderButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "stickers_menu"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKBOX, new Class[]{StickerSetCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{StickerSetCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "checkboxCheck"));
        TrendingStickersAlert trendingStickersAlert2 = this.trendingStickersAlert;
        if (trendingStickersAlert2 != null) {
            themeDescriptions.addAll(trendingStickersAlert2.getThemeDescriptions());
        }
        return themeDescriptions;
    }
}
