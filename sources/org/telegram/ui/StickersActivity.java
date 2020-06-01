package org.telegram.ui;

import android.annotation.SuppressLint;
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
import android.widget.Toast;
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
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SharedConfig;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$InputStickerSet;
import org.telegram.tgnet.TLRPC$StickerSet;
import org.telegram.tgnet.TLRPC$StickerSetCovered;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_messages_reorderStickerSets;
import org.telegram.tgnet.TLRPC$TL_messages_stickerSet;
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
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.NumberTextView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.ReorderingBulletinLayout;
import org.telegram.ui.Components.StickersAlert;
import org.telegram.ui.Components.TrendingStickersAlert;
import org.telegram.ui.Components.TrendingStickersLayout;
import org.telegram.ui.Components.URLSpanNoUnderline;
import org.telegram.ui.StickersActivity;

public class StickersActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    /* access modifiers changed from: private */
    public int activeReorderingRequests;
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

    static /* synthetic */ boolean lambda$createView$0(View view, MotionEvent motionEvent) {
        return true;
    }

    public class TouchHelperCallback extends ItemTouchHelper.Callback {
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int i) {
        }

        public TouchHelperCallback() {
        }

        public boolean isLongPressDragEnabled() {
            return StickersActivity.this.listAdapter.hasSelected();
        }

        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            if (viewHolder.getItemViewType() != 0) {
                return ItemTouchHelper.Callback.makeMovementFlags(0, 0);
            }
            return ItemTouchHelper.Callback.makeMovementFlags(3, 0);
        }

        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder viewHolder2) {
            if (viewHolder.getItemViewType() != viewHolder2.getItemViewType()) {
                return false;
            }
            StickersActivity.this.listAdapter.swapElements(viewHolder.getAdapterPosition(), viewHolder2.getAdapterPosition());
            return true;
        }

        public void onChildDraw(Canvas canvas, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float f, float f2, int i, boolean z) {
            super.onChildDraw(canvas, recyclerView, viewHolder, f, f2, i, z);
        }

        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int i) {
            if (i == 0) {
                StickersActivity.this.sendReorder();
            } else {
                StickersActivity.this.listView.cancelClickRunnables(false);
                viewHolder.itemView.setPressed(true);
            }
            super.onSelectedChanged(viewHolder, i);
        }

        public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            super.clearView(recyclerView, viewHolder);
            viewHolder.itemView.setPressed(false);
        }
    }

    public StickersActivity(int i) {
        this.currentType = i;
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
            public void onItemClick(int i) {
                if (i == -1) {
                    if (StickersActivity.this.onBackPressed()) {
                        StickersActivity.this.finishFragment();
                    }
                } else if (i != 0 && i != 1) {
                } else {
                    if (StickersActivity.this.needReorder) {
                        StickersActivity.this.sendReorder();
                    } else if (StickersActivity.this.activeReorderingRequests == 0) {
                        StickersActivity.this.listAdapter.processSelectionMenu(i);
                    }
                }
            }
        });
        ActionBarMenu createActionMode = this.actionBar.createActionMode();
        NumberTextView numberTextView = new NumberTextView(createActionMode.getContext());
        this.selectedCountTextView = numberTextView;
        numberTextView.setTextSize(18);
        this.selectedCountTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.selectedCountTextView.setTextColor(Theme.getColor("actionBarActionModeDefaultIcon"));
        createActionMode.addView(this.selectedCountTextView, LayoutHelper.createLinear(0, -1, 1.0f, 72, 0, 0, 0));
        this.selectedCountTextView.setOnTouchListener($$Lambda$StickersActivity$HUiGgK08vUbN9lswklOPdO_GEU.INSTANCE);
        createActionMode.addItemWithWidth(0, NUM, AndroidUtilities.dp(54.0f));
        this.deleteMenuItem = createActionMode.addItemWithWidth(1, NUM, AndroidUtilities.dp(54.0f));
        this.listAdapter = new ListAdapter(context, MediaDataController.getInstance(this.currentAccount).getStickerSets(this.currentType));
        FrameLayout frameLayout = new FrameLayout(context);
        this.fragmentView = frameLayout;
        FrameLayout frameLayout2 = frameLayout;
        frameLayout2.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
        RecyclerListView recyclerListView = new RecyclerListView(context);
        this.listView = recyclerListView;
        recyclerListView.setFocusable(true);
        this.listView.setTag(7);
        AnonymousClass2 r3 = new LinearLayoutManager(context) {
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }

            /* access modifiers changed from: protected */
            public void calculateExtraLayoutSpace(RecyclerView.State state, int[] iArr) {
                iArr[1] = StickersActivity.this.listView.getHeight();
            }
        };
        this.layoutManager = r3;
        r3.setOrientation(1);
        this.listView.setLayoutManager(this.layoutManager);
        ItemTouchHelper itemTouchHelper2 = new ItemTouchHelper(new TouchHelperCallback());
        this.itemTouchHelper = itemTouchHelper2;
        itemTouchHelper2.attachToRecyclerView(this.listView);
        DefaultItemAnimator defaultItemAnimator = (DefaultItemAnimator) this.listView.getItemAnimator();
        this.itemAnimator = defaultItemAnimator;
        defaultItemAnimator.setSupportsChangeAnimations(false);
        frameLayout2.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setAdapter(this.listAdapter);
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener(context) {
            private final /* synthetic */ Context f$1;

            {
                this.f$1 = r2;
            }

            public final void onItemClick(View view, int i) {
                StickersActivity.this.lambda$createView$2$StickersActivity(this.f$1, view, i);
            }
        });
        this.listView.setOnItemLongClickListener((RecyclerListView.OnItemLongClickListener) new RecyclerListView.OnItemLongClickListener() {
            public final boolean onItemClick(View view, int i) {
                return StickersActivity.this.lambda$createView$3$StickersActivity(view, i);
            }
        });
        return this.fragmentView;
    }

    public /* synthetic */ void lambda$createView$2$StickersActivity(Context context, View view, int i) {
        if (i < this.stickersStartRow || i >= this.stickersEndRow || getParentActivity() == null) {
            if (i == this.featuredRow) {
                TrendingStickersAlert trendingStickersAlert2 = new TrendingStickersAlert(context, this, new TrendingStickersLayout(context, new TrendingStickersLayout.Delegate() {
                    public void onStickerSetAdd(TLRPC$StickerSetCovered tLRPC$StickerSetCovered, boolean z) {
                        MediaDataController.getInstance(StickersActivity.this.currentAccount).toggleStickerSet(StickersActivity.this.getParentActivity(), tLRPC$StickerSetCovered, 2, StickersActivity.this, false, false);
                    }

                    public void onStickerSetRemove(TLRPC$StickerSetCovered tLRPC$StickerSetCovered) {
                        MediaDataController.getInstance(StickersActivity.this.currentAccount).toggleStickerSet(StickersActivity.this.getParentActivity(), tLRPC$StickerSetCovered, 0, StickersActivity.this, false, false);
                    }
                }));
                this.trendingStickersAlert = trendingStickersAlert2;
                trendingStickersAlert2.show();
            } else if (i == this.archivedRow) {
                presentFragment(new ArchivedStickersActivity(this.currentType));
            } else if (i == this.masksRow) {
                presentFragment(new StickersActivity(1));
            } else if (i == this.suggestRow) {
                if (getParentActivity() != null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
                    builder.setTitle(LocaleController.getString("SuggestStickers", NUM));
                    String[] strArr = {LocaleController.getString("SuggestStickersAll", NUM), LocaleController.getString("SuggestStickersInstalled", NUM), LocaleController.getString("SuggestStickersNone", NUM)};
                    LinearLayout linearLayout = new LinearLayout(getParentActivity());
                    linearLayout.setOrientation(1);
                    builder.setView(linearLayout);
                    int i2 = 0;
                    while (i2 < 3) {
                        RadioColorCell radioColorCell = new RadioColorCell(getParentActivity());
                        radioColorCell.setPadding(AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f), 0);
                        radioColorCell.setTag(Integer.valueOf(i2));
                        radioColorCell.setCheckColor(Theme.getColor("radioBackground"), Theme.getColor("dialogRadioBackgroundChecked"));
                        radioColorCell.setTextAndValue(strArr[i2], SharedConfig.suggestStickers == i2);
                        linearLayout.addView(radioColorCell);
                        radioColorCell.setOnClickListener(new View.OnClickListener(builder) {
                            private final /* synthetic */ AlertDialog.Builder f$1;

                            {
                                this.f$1 = r2;
                            }

                            public final void onClick(View view) {
                                StickersActivity.this.lambda$null$1$StickersActivity(this.f$1, view);
                            }
                        });
                        i2++;
                    }
                    showDialog(builder.create());
                }
            } else if (i == this.loopRow) {
                SharedConfig.toggleLoopStickers();
                this.listAdapter.notifyItemChanged(this.loopRow, 0);
            }
        } else if (!this.listAdapter.hasSelected()) {
            TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet = (TLRPC$TL_messages_stickerSet) this.listAdapter.stickerSets.get(i - this.stickersStartRow);
            ArrayList<TLRPC$Document> arrayList = tLRPC$TL_messages_stickerSet.documents;
            if (arrayList != null && !arrayList.isEmpty()) {
                showDialog(new StickersAlert(getParentActivity(), this, (TLRPC$InputStickerSet) null, tLRPC$TL_messages_stickerSet, (StickersAlert.StickersAlertDelegate) null));
            }
        } else {
            this.listAdapter.toggleSelected(i);
        }
    }

    public /* synthetic */ void lambda$null$1$StickersActivity(AlertDialog.Builder builder, View view) {
        SharedConfig.setSuggestStickers(((Integer) view.getTag()).intValue());
        this.listAdapter.notifyItemChanged(this.suggestRow);
        builder.getDismissRunnable().run();
    }

    public /* synthetic */ boolean lambda$createView$3$StickersActivity(View view, int i) {
        if (this.listAdapter.hasSelected() || i < this.stickersStartRow || i >= this.stickersEndRow) {
            return false;
        }
        this.listAdapter.toggleSelected(i);
        return true;
    }

    public boolean onBackPressed() {
        if (!this.listAdapter.hasSelected()) {
            return super.onBackPressed();
        }
        this.listAdapter.clearSelected();
        return false;
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.stickersDidLoad) {
            int intValue = objArr[0].intValue();
            int i3 = this.currentType;
            if (intValue == i3) {
                updateRows();
            } else if (i3 == 0 && intValue == 1) {
                this.listAdapter.notifyItemChanged(this.masksRow);
            }
        } else if (i == NotificationCenter.featuredStickersDidLoad) {
            ListAdapter listAdapter2 = this.listAdapter;
            if (listAdapter2 != null) {
                listAdapter2.notifyItemChanged(0);
            }
        } else if (i == NotificationCenter.archivedStickersCountDidLoad && objArr[0].intValue() == this.currentType) {
            updateRows();
        }
    }

    /* access modifiers changed from: private */
    public void sendReorder() {
        if (this.needReorder) {
            MediaDataController.getInstance(this.currentAccount).calcNewHash(this.currentType);
            this.needReorder = false;
            this.activeReorderingRequests++;
            TLRPC$TL_messages_reorderStickerSets tLRPC$TL_messages_reorderStickerSets = new TLRPC$TL_messages_reorderStickerSets();
            tLRPC$TL_messages_reorderStickerSets.masks = this.currentType == 1;
            for (int i = 0; i < this.listAdapter.stickerSets.size(); i++) {
                tLRPC$TL_messages_reorderStickerSets.order.add(Long.valueOf(((TLRPC$TL_messages_stickerSet) this.listAdapter.stickerSets.get(i)).set.id));
            }
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_messages_reorderStickerSets, new RequestDelegate() {
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    StickersActivity.this.lambda$sendReorder$5$StickersActivity(tLObject, tLRPC$TL_error);
                }
            });
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.stickersDidLoad, Integer.valueOf(this.currentType));
        }
    }

    public /* synthetic */ void lambda$null$4$StickersActivity() {
        this.activeReorderingRequests--;
    }

    public /* synthetic */ void lambda$sendReorder$5$StickersActivity(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            public final void run() {
                StickersActivity.this.lambda$null$4$StickersActivity();
            }
        });
    }

    private void updateRows() {
        MediaDataController instance = MediaDataController.getInstance(this.currentAccount);
        final ArrayList<TLRPC$TL_messages_stickerSet> stickerSets = instance.getStickerSets(this.currentType);
        DiffUtil.DiffResult diffResult = null;
        if (this.listAdapter != null) {
            if (!this.isPaused) {
                diffResult = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                    final List<TLRPC$TL_messages_stickerSet> oldList = StickersActivity.this.listAdapter.stickerSets;

                    public int getOldListSize() {
                        return this.oldList.size();
                    }

                    public int getNewListSize() {
                        return stickerSets.size();
                    }

                    public boolean areItemsTheSame(int i, int i2) {
                        return this.oldList.get(i).set.id == ((TLRPC$TL_messages_stickerSet) stickerSets.get(i2)).set.id;
                    }

                    public boolean areContentsTheSame(int i, int i2) {
                        TLRPC$StickerSet tLRPC$StickerSet = this.oldList.get(i).set;
                        TLRPC$StickerSet tLRPC$StickerSet2 = ((TLRPC$TL_messages_stickerSet) stickerSets.get(i2)).set;
                        return TextUtils.equals(tLRPC$StickerSet.title, tLRPC$StickerSet2.title) && tLRPC$StickerSet.count == tLRPC$StickerSet2.count;
                    }
                });
            }
            this.listAdapter.setStickerSets(stickerSets);
        }
        this.rowCount = 0;
        if (this.currentType == 0) {
            int i = 0 + 1;
            this.rowCount = i;
            this.suggestRow = 0;
            int i2 = i + 1;
            this.rowCount = i2;
            this.loopRow = i;
            int i3 = i2 + 1;
            this.rowCount = i3;
            this.loopInfoRow = i2;
            this.rowCount = i3 + 1;
            this.featuredRow = i3;
        } else {
            this.suggestRow = -1;
            this.loopRow = -1;
            this.loopInfoRow = -1;
            this.featuredRow = -1;
        }
        int i4 = 2;
        if (instance.getArchivedStickersCount(this.currentType) != 0) {
            boolean z = this.archivedRow == -1;
            int i5 = this.rowCount;
            int i6 = i5 + 1;
            this.rowCount = i6;
            this.archivedRow = i5;
            if (this.currentType == 1) {
                this.rowCount = i6 + 1;
            } else {
                i6 = -1;
            }
            this.archivedInfoRow = i6;
            ListAdapter listAdapter2 = this.listAdapter;
            if (listAdapter2 != null && z) {
                int i7 = this.archivedRow;
                if (i6 == -1) {
                    i4 = 1;
                }
                listAdapter2.notifyItemRangeInserted(i7, i4);
            }
        } else {
            int i8 = this.archivedRow;
            int i9 = this.archivedInfoRow;
            this.archivedRow = -1;
            this.archivedInfoRow = -1;
            ListAdapter listAdapter3 = this.listAdapter;
            if (!(listAdapter3 == null || i8 == -1)) {
                if (i9 == -1) {
                    i4 = 1;
                }
                listAdapter3.notifyItemRangeRemoved(i8, i4);
            }
        }
        if (this.currentType == 0) {
            int i10 = this.rowCount;
            int i11 = i10 + 1;
            this.rowCount = i11;
            this.masksRow = i10;
            this.rowCount = i11 + 1;
            this.stickersBotInfo = i11;
        } else {
            this.masksRow = -1;
            this.stickersBotInfo = -1;
        }
        int size = stickerSets.size();
        if (size > 0) {
            int i12 = this.rowCount;
            this.stickersStartRow = i12;
            int i13 = i12 + size;
            this.rowCount = i13;
            this.stickersEndRow = i13;
            if (this.currentType != 1) {
                this.rowCount = i13 + 1;
                this.stickersShadowRow = i13;
                this.masksInfoRow = -1;
            } else {
                this.rowCount = i13 + 1;
                this.masksInfoRow = i13;
                this.stickersShadowRow = -1;
            }
        } else {
            this.stickersStartRow = -1;
            this.stickersEndRow = -1;
            this.stickersShadowRow = -1;
            this.masksInfoRow = -1;
        }
        if (this.listAdapter != null && diffResult != null) {
            final int i14 = this.stickersStartRow;
            if (i14 < 0) {
                i14 = this.rowCount;
            }
            this.listAdapter.notifyItemRangeChanged(0, i14);
            diffResult.dispatchUpdatesTo((ListUpdateCallback) new ListUpdateCallback() {
                public void onMoved(int i, int i2) {
                }

                public void onInserted(int i, int i2) {
                    StickersActivity.this.listAdapter.notifyItemRangeInserted(i14 + i, i2);
                }

                public void onRemoved(int i, int i2) {
                    StickersActivity.this.listAdapter.notifyItemRangeRemoved(i14 + i, i2);
                }

                public void onChanged(int i, int i2, Object obj) {
                    StickersActivity.this.listAdapter.notifyItemRangeChanged(i14 + i, i2);
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
        private Context mContext;
        private final LongSparseArray<Boolean> selectedItems = new LongSparseArray<>();
        /* access modifiers changed from: private */
        public final List<TLRPC$TL_messages_stickerSet> stickerSets;

        public ListAdapter(Context context, List<TLRPC$TL_messages_stickerSet> list) {
            ArrayList arrayList = new ArrayList();
            this.stickerSets = arrayList;
            this.mContext = context;
            arrayList.addAll(list);
        }

        public void setStickerSets(List<TLRPC$TL_messages_stickerSet> list) {
            this.stickerSets.clear();
            this.stickerSets.addAll(list);
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
        public void processSelectionMenu(int i) {
            String str;
            TextView textView;
            if (i == 0 || i == 1) {
                ArrayList arrayList = new ArrayList(this.selectedItems.size());
                int size = this.stickerSets.size();
                for (int i2 = 0; i2 < size; i2++) {
                    TLRPC$StickerSet tLRPC$StickerSet = this.stickerSets.get(i2).set;
                    if (this.selectedItems.get(tLRPC$StickerSet.id, false).booleanValue()) {
                        arrayList.add(tLRPC$StickerSet);
                    }
                }
                int size2 = arrayList.size();
                if (size2 == 0) {
                    return;
                }
                if (size2 != 1) {
                    AlertDialog.Builder builder = new AlertDialog.Builder((Context) StickersActivity.this.getParentActivity());
                    if (i == 1) {
                        builder.setTitle(LocaleController.formatString("DeleteStickerSetsAlertTitle", NUM, LocaleController.formatPluralString("StickerSets", size2)));
                        builder.setMessage(LocaleController.formatString("DeleteStickersAlertMessage", NUM, Integer.valueOf(size2)));
                        str = LocaleController.getString("Delete", NUM);
                    } else {
                        builder.setTitle(LocaleController.formatString("ArchiveStickerSetsAlertTitle", NUM, LocaleController.formatPluralString("StickerSets", size2)));
                        builder.setMessage(LocaleController.formatString("ArchiveStickersAlertMessage", NUM, Integer.valueOf(size2)));
                        str = LocaleController.getString("Archive", NUM);
                    }
                    builder.setPositiveButton(str, new DialogInterface.OnClickListener(arrayList, i) {
                        private final /* synthetic */ ArrayList f$1;
                        private final /* synthetic */ int f$2;

                        {
                            this.f$1 = r2;
                            this.f$2 = r3;
                        }

                        public final void onClick(DialogInterface dialogInterface, int i) {
                            StickersActivity.ListAdapter.this.lambda$processSelectionMenu$0$StickersActivity$ListAdapter(this.f$1, this.f$2, dialogInterface, i);
                        }
                    });
                    builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                    AlertDialog create = builder.create();
                    StickersActivity.this.showDialog(create);
                    if (i == 1 && (textView = (TextView) create.getButton(-1)) != null) {
                        textView.setTextColor(Theme.getColor("dialogTextRed2"));
                        return;
                    }
                    return;
                }
                int size3 = this.stickerSets.size();
                int i3 = 0;
                while (true) {
                    if (i3 >= size3) {
                        break;
                    }
                    TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet = this.stickerSets.get(i3);
                    if (this.selectedItems.get(tLRPC$TL_messages_stickerSet.set.id, false).booleanValue()) {
                        processSelectionOption(i, tLRPC$TL_messages_stickerSet);
                        break;
                    }
                    i3++;
                }
                StickersActivity.this.listAdapter.clearSelected();
            }
        }

        public /* synthetic */ void lambda$processSelectionMenu$0$StickersActivity$ListAdapter(ArrayList arrayList, int i, DialogInterface dialogInterface, int i2) {
            StickersActivity.this.listAdapter.clearSelected();
            MediaDataController.getInstance(StickersActivity.this.currentAccount).toggleStickerSets(arrayList, StickersActivity.this.currentType, i == 1 ? 0 : 1, StickersActivity.this, true);
        }

        private void processSelectionOption(int i, TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet) {
            int indexOf;
            if (i == 0) {
                MediaDataController.getInstance(StickersActivity.this.currentAccount).toggleStickerSet(StickersActivity.this.getParentActivity(), tLRPC$TL_messages_stickerSet, !tLRPC$TL_messages_stickerSet.set.archived ? 1 : 2, StickersActivity.this, true, true);
            } else if (i == 1) {
                MediaDataController.getInstance(StickersActivity.this.currentAccount).toggleStickerSet(StickersActivity.this.getParentActivity(), tLRPC$TL_messages_stickerSet, 0, StickersActivity.this, true, true);
            } else if (i == 2) {
                try {
                    Intent intent = new Intent("android.intent.action.SEND");
                    intent.setType("text/plain");
                    Locale locale = Locale.US;
                    intent.putExtra("android.intent.extra.TEXT", String.format(locale, "https://" + MessagesController.getInstance(StickersActivity.this.currentAccount).linkPrefix + "/addstickers/%s", new Object[]{tLRPC$TL_messages_stickerSet.set.short_name}));
                    StickersActivity.this.getParentActivity().startActivityForResult(Intent.createChooser(intent, LocaleController.getString("StickersShare", NUM)), 500);
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
            } else if (i == 3) {
                try {
                    Locale locale2 = Locale.US;
                    ((ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", String.format(locale2, "https://" + MessagesController.getInstance(StickersActivity.this.currentAccount).linkPrefix + "/addstickers/%s", new Object[]{tLRPC$TL_messages_stickerSet.set.short_name})));
                    Toast.makeText(StickersActivity.this.getParentActivity(), LocaleController.getString("LinkCopied", NUM), 0).show();
                } catch (Exception e2) {
                    FileLog.e((Throwable) e2);
                }
            } else if (i == 4 && (indexOf = this.stickerSets.indexOf(tLRPC$TL_messages_stickerSet)) >= 0) {
                StickersActivity.this.listAdapter.toggleSelected(StickersActivity.this.stickersStartRow + indexOf);
            }
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            String str;
            int itemViewType = viewHolder.getItemViewType();
            boolean z = true;
            if (itemViewType == 0) {
                int access$800 = i - StickersActivity.this.stickersStartRow;
                StickerSetCell stickerSetCell = (StickerSetCell) viewHolder.itemView;
                TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet = this.stickerSets.get(access$800);
                if (access$800 == this.stickerSets.size() - 1) {
                    z = false;
                }
                stickerSetCell.setStickersSet(tLRPC$TL_messages_stickerSet, z);
                stickerSetCell.setChecked(this.selectedItems.get(getItemId(i), false).booleanValue(), false);
                stickerSetCell.setReorderable(hasSelected(), false);
            } else if (itemViewType == 1) {
                TextInfoPrivacyCell textInfoPrivacyCell = (TextInfoPrivacyCell) viewHolder.itemView;
                if (i == StickersActivity.this.stickersBotInfo) {
                    textInfoPrivacyCell.setText(addStickersBotSpan(LocaleController.getString("StickersBotInfo", NUM)));
                } else if (i == StickersActivity.this.archivedInfoRow) {
                    if (StickersActivity.this.currentType == 0) {
                        textInfoPrivacyCell.setText(LocaleController.getString("ArchivedStickersInfo", NUM));
                    } else {
                        textInfoPrivacyCell.setText(LocaleController.getString("ArchivedMasksInfo", NUM));
                    }
                } else if (i == StickersActivity.this.loopInfoRow) {
                    textInfoPrivacyCell.setText(LocaleController.getString("LoopAnimatedStickersInfo", NUM));
                } else if (i == StickersActivity.this.masksInfoRow) {
                    textInfoPrivacyCell.setText(LocaleController.getString("MasksInfo", NUM));
                }
            } else if (itemViewType == 2) {
                TextSettingsCell textSettingsCell = (TextSettingsCell) viewHolder.itemView;
                String str2 = "";
                if (i == StickersActivity.this.featuredRow) {
                    int size = MediaDataController.getInstance(StickersActivity.this.currentAccount).getFeaturedStickerSets().size();
                    String string = LocaleController.getString("FeaturedStickers", NUM);
                    if (size > 0) {
                        str2 = Integer.toString(size);
                    }
                    textSettingsCell.setTextAndValue(string, str2, true);
                } else if (i == StickersActivity.this.archivedRow) {
                    int archivedStickersCount = MediaDataController.getInstance(StickersActivity.this.currentAccount).getArchivedStickersCount(StickersActivity.this.currentType);
                    if (archivedStickersCount > 0) {
                        str2 = Integer.toString(archivedStickersCount);
                    }
                    if (StickersActivity.this.currentType == 0) {
                        textSettingsCell.setTextAndValue(LocaleController.getString("ArchivedStickers", NUM), str2, true);
                    } else {
                        textSettingsCell.setTextAndValue(LocaleController.getString("ArchivedMasks", NUM), str2, true);
                    }
                } else if (i == StickersActivity.this.masksRow) {
                    MediaDataController instance = MediaDataController.getInstance(StickersActivity.this.currentAccount);
                    int size2 = instance.getStickerSets(1).size() + instance.getArchivedStickersCount(1);
                    String string2 = LocaleController.getString("Masks", NUM);
                    if (size2 > 0) {
                        str2 = Integer.toString(size2);
                    }
                    textSettingsCell.setTextAndValue(string2, str2, false);
                } else if (i == StickersActivity.this.suggestRow) {
                    int i2 = SharedConfig.suggestStickers;
                    if (i2 == 0) {
                        str = LocaleController.getString("SuggestStickersAll", NUM);
                    } else if (i2 != 1) {
                        str = LocaleController.getString("SuggestStickersNone", NUM);
                    } else {
                        str = LocaleController.getString("SuggestStickersInstalled", NUM);
                    }
                    textSettingsCell.setTextAndValue(LocaleController.getString("SuggestStickers", NUM), str, true);
                }
            } else if (itemViewType != 3) {
                if (itemViewType == 4 && i == StickersActivity.this.loopRow) {
                    ((TextCheckCell) viewHolder.itemView).setTextAndCheck(LocaleController.getString("LoopAnimatedStickers", NUM), SharedConfig.loopStickers, true);
                }
            } else if (i == StickersActivity.this.stickersShadowRow) {
                viewHolder.itemView.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
            }
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i, List list) {
            if (list.isEmpty()) {
                onBindViewHolder(viewHolder, i);
                return;
            }
            int itemViewType = viewHolder.getItemViewType();
            boolean z = false;
            if (itemViewType != 0) {
                if (itemViewType == 4 && list.contains(0) && i == StickersActivity.this.loopRow) {
                    ((TextCheckCell) viewHolder.itemView).setChecked(SharedConfig.loopStickers);
                }
            } else if (i >= StickersActivity.this.stickersStartRow && i < StickersActivity.this.stickersEndRow) {
                StickerSetCell stickerSetCell = (StickerSetCell) viewHolder.itemView;
                if (list.contains(1)) {
                    stickerSetCell.setChecked(this.selectedItems.get(getItemId(i), false).booleanValue());
                }
                if (list.contains(2)) {
                    stickerSetCell.setReorderable(hasSelected());
                }
                if (list.contains(3)) {
                    if (i - StickersActivity.this.stickersStartRow != this.stickerSets.size() - 1) {
                        z = true;
                    }
                    stickerSetCell.setNeedDivider(z);
                }
            }
        }

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            int itemViewType = viewHolder.getItemViewType();
            return itemViewType == 0 || itemViewType == 2 || itemViewType == 4;
        }

        public /* synthetic */ boolean lambda$onCreateViewHolder$1$StickersActivity$ListAdapter(StickerSetCell stickerSetCell, View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() != 0) {
                return false;
            }
            StickersActivity.this.itemTouchHelper.startDrag(StickersActivity.this.listView.getChildViewHolder(stickerSetCell));
            return false;
        }

        public /* synthetic */ void lambda$onCreateViewHolder$3$StickersActivity$ListAdapter(View view) {
            CharSequence[] charSequenceArr;
            int[] iArr;
            int[] iArr2;
            TLRPC$TL_messages_stickerSet stickersSet = ((StickerSetCell) view.getParent()).getStickersSet();
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) StickersActivity.this.getParentActivity());
            builder.setTitle(stickersSet.set.title);
            if (stickersSet.set.official) {
                iArr2 = new int[]{0, 4};
                charSequenceArr = new CharSequence[]{LocaleController.getString("StickersHide", NUM), LocaleController.getString("StickersReorder", NUM)};
                iArr = new int[]{NUM, NUM};
            } else {
                iArr = new int[]{NUM, NUM, NUM, NUM, NUM};
                iArr2 = new int[]{0, 3, 4, 2, 1};
                charSequenceArr = new CharSequence[]{LocaleController.getString("StickersHide", NUM), LocaleController.getString("StickersCopy", NUM), LocaleController.getString("StickersReorder", NUM), LocaleController.getString("StickersShare", NUM), LocaleController.getString("StickersRemove", NUM)};
            }
            builder.setItems(charSequenceArr, iArr, new DialogInterface.OnClickListener(iArr2, stickersSet) {
                private final /* synthetic */ int[] f$1;
                private final /* synthetic */ TLRPC$TL_messages_stickerSet f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void onClick(DialogInterface dialogInterface, int i) {
                    StickersActivity.ListAdapter.this.lambda$null$2$StickersActivity$ListAdapter(this.f$1, this.f$2, dialogInterface, i);
                }
            });
            AlertDialog create = builder.create();
            StickersActivity.this.showDialog(create);
            if (iArr2[iArr2.length - 1] == 1) {
                create.setItemColor(charSequenceArr.length - 1, Theme.getColor("dialogTextRed2"), Theme.getColor("dialogRedIcon"));
            }
        }

        public /* synthetic */ void lambda$null$2$StickersActivity$ListAdapter(int[] iArr, TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet, DialogInterface dialogInterface, int i) {
            processSelectionOption(iArr[i], tLRPC$TL_messages_stickerSet);
        }

        @SuppressLint({"ClickableViewAccessibility"})
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            TextCheckCell textCheckCell;
            if (i == 0) {
                StickerSetCell stickerSetCell = new StickerSetCell(this.mContext, 1);
                stickerSetCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                stickerSetCell.setOnReorderButtonTouchListener(new View.OnTouchListener(stickerSetCell) {
                    private final /* synthetic */ StickerSetCell f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final boolean onTouch(View view, MotionEvent motionEvent) {
                        return StickersActivity.ListAdapter.this.lambda$onCreateViewHolder$1$StickersActivity$ListAdapter(this.f$1, view, motionEvent);
                    }
                });
                stickerSetCell.setOnOptionsClick(new View.OnClickListener() {
                    public final void onClick(View view) {
                        StickersActivity.ListAdapter.this.lambda$onCreateViewHolder$3$StickersActivity$ListAdapter(view);
                    }
                });
                textCheckCell = stickerSetCell;
            } else if (i == 1) {
                TextInfoPrivacyCell textInfoPrivacyCell = new TextInfoPrivacyCell(this.mContext);
                textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                textCheckCell = textInfoPrivacyCell;
            } else if (i == 2) {
                TextSettingsCell textSettingsCell = new TextSettingsCell(this.mContext);
                textSettingsCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                textCheckCell = textSettingsCell;
            } else if (i == 3) {
                textCheckCell = new ShadowSectionCell(this.mContext);
            } else if (i != 4) {
                textCheckCell = null;
            } else {
                TextCheckCell textCheckCell2 = new TextCheckCell(this.mContext);
                textCheckCell2.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                textCheckCell = textCheckCell2;
            }
            textCheckCell.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(textCheckCell);
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

        public void swapElements(int i, int i2) {
            if (i != i2) {
                boolean unused = StickersActivity.this.needReorder = true;
            }
            MediaDataController instance = MediaDataController.getInstance(StickersActivity.this.currentAccount);
            int access$800 = i - StickersActivity.this.stickersStartRow;
            int access$8002 = i2 - StickersActivity.this.stickersStartRow;
            swapListElements(this.stickerSets, access$800, access$8002);
            swapListElements(instance.getStickerSets(StickersActivity.this.currentType), access$800, access$8002);
            notifyItemMoved(i, i2);
            if (i == StickersActivity.this.stickersEndRow - 1 || i2 == StickersActivity.this.stickersEndRow - 1) {
                notifyItemRangeChanged(i, 3);
                notifyItemRangeChanged(i2, 3);
            }
        }

        private void swapListElements(List<TLRPC$TL_messages_stickerSet> list, int i, int i2) {
            list.set(i, list.get(i2));
            list.set(i2, list.get(i));
        }

        public void toggleSelected(int i) {
            long itemId = getItemId(i);
            LongSparseArray<Boolean> longSparseArray = this.selectedItems;
            longSparseArray.put(itemId, Boolean.valueOf(!longSparseArray.get(itemId, false).booleanValue()));
            notifyItemChanged(i, 1);
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
            int size = this.selectedItems.size();
            int i = 0;
            for (int i2 = 0; i2 < size; i2++) {
                if (this.selectedItems.valueAt(i2).booleanValue()) {
                    i++;
                }
            }
            return i;
        }

        private void checkActionMode() {
            int selectedCount = StickersActivity.this.listAdapter.getSelectedCount();
            boolean isActionModeShowed = StickersActivity.this.actionBar.isActionModeShowed();
            if (selectedCount > 0) {
                checkActionModeIcons();
                StickersActivity.this.selectedCountTextView.setNumber(selectedCount, isActionModeShowed);
                if (!isActionModeShowed) {
                    StickersActivity.this.actionBar.showActionMode();
                    notifyStickersItemsChanged(2);
                    if (!SharedConfig.stickersReorderingHintUsed) {
                        SharedConfig.setStickersReorderingHintUsed(true);
                        Bulletin.make((FrameLayout) StickersActivity.this.parentLayout, (Bulletin.Layout) new ReorderingBulletinLayout(this.mContext, LocaleController.getString("StickersReorderHint", NUM)), 3250).show();
                    }
                }
            } else if (isActionModeShowed) {
                StickersActivity.this.actionBar.hideActionMode();
                notifyStickersItemsChanged(2);
            }
        }

        private void checkActionModeIcons() {
            boolean z;
            if (hasSelected()) {
                int size = this.stickerSets.size();
                int i = 0;
                int i2 = 0;
                while (true) {
                    if (i2 < size) {
                        if (this.selectedItems.get(this.stickerSets.get(i2).set.id, false).booleanValue() && this.stickerSets.get(i2).set.official) {
                            z = false;
                            break;
                        }
                        i2++;
                    } else {
                        z = true;
                        break;
                    }
                }
                if (!z) {
                    i = 8;
                }
                if (StickersActivity.this.deleteMenuItem.getVisibility() != i) {
                    StickersActivity.this.deleteMenuItem.setVisibility(i);
                }
            }
        }

        private void notifyStickersItemsChanged(Object obj) {
            notifyItemRangeChanged(StickersActivity.this.stickersStartRow, StickersActivity.this.stickersEndRow - StickersActivity.this.stickersStartRow, obj);
        }

        private CharSequence addStickersBotSpan(String str) {
            int indexOf = str.indexOf("@stickers");
            if (indexOf != -1) {
                try {
                    SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(str);
                    spannableStringBuilder.setSpan(new URLSpanNoUnderline("@stickers") {
                        public void onClick(View view) {
                            MessagesController.getInstance(StickersActivity.this.currentAccount).openByUserName("stickers", StickersActivity.this, 3);
                        }
                    }, indexOf, indexOf + 9, 18);
                    return spannableStringBuilder;
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
            }
            return str;
        }
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{StickerSetCell.class, TextSettingsCell.class, TextCheckCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultIcon"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultTitle"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSelector"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_AM_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarActionModeDefaultIcon"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_AM_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarActionModeDefault"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_AM_TOPBACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarActionModeDefaultTop"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_AM_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarActionModeDefaultSelector"));
        arrayList.add(new ThemeDescription(this.selectedCountTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarActionModeDefaultIcon"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "divider"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrack"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrackChecked"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText4"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_LINKCOLOR, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteLinkText"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteValueText"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{StickerSetCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{StickerSetCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText2"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, new Class[]{StickerSetCell.class}, new String[]{"optionsButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "stickers_menuSelector"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{StickerSetCell.class}, new String[]{"optionsButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "stickers_menu"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{StickerSetCell.class}, new String[]{"reorderButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "stickers_menu"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKBOX, new Class[]{StickerSetCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{StickerSetCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "checkboxCheck"));
        TrendingStickersAlert trendingStickersAlert2 = this.trendingStickersAlert;
        if (trendingStickersAlert2 != null) {
            arrayList.addAll(trendingStickersAlert2.getThemeDescriptions());
        }
        return arrayList;
    }
}
