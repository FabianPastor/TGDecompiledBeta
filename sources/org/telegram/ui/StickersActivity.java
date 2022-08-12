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
import org.telegram.messenger.R;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$InputStickerSet;
import org.telegram.tgnet.TLRPC$StickerSet;
import org.telegram.tgnet.TLRPC$StickerSetCovered;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_inputStickerSetID;
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
import org.telegram.ui.Cells.FeaturedStickerSetCell2;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.RadioColorCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.StickerSetCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.Bulletin;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.EmojiPacksAlert;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.NumberTextView;
import org.telegram.ui.Components.Premium.PremiumFeatureBottomSheet;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.ReorderingBulletinLayout;
import org.telegram.ui.Components.ShareAlert;
import org.telegram.ui.Components.StickersAlert;
import org.telegram.ui.Components.TrendingStickersAlert;
import org.telegram.ui.Components.TrendingStickersLayout;
import org.telegram.ui.Components.URLSpanNoUnderline;

public class StickersActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    /* access modifiers changed from: private */
    public int activeReorderingRequests;
    /* access modifiers changed from: private */
    public int archivedInfoRow;
    /* access modifiers changed from: private */
    public int archivedRow;
    /* access modifiers changed from: private */
    public int currentType;
    /* access modifiers changed from: private */
    public ActionBarMenuItem deleteMenuItem;
    /* access modifiers changed from: private */
    public int emojiPacksRow;
    /* access modifiers changed from: private */
    public int featuredStickersEndRow;
    /* access modifiers changed from: private */
    public int featuredStickersHeaderRow;
    /* access modifiers changed from: private */
    public int featuredStickersShadowRow;
    /* access modifiers changed from: private */
    public int featuredStickersShowMoreRow;
    /* access modifiers changed from: private */
    public int featuredStickersStartRow;
    /* access modifiers changed from: private */
    public boolean isListeningForFeaturedUpdate;
    /* access modifiers changed from: private */
    public ItemTouchHelper itemTouchHelper;
    /* access modifiers changed from: private */
    public int largeEmojiRow;
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
    public int reactionsDoubleTapRow;
    /* access modifiers changed from: private */
    public int rowCount;
    /* access modifiers changed from: private */
    public NumberTextView selectedCountTextView;
    /* access modifiers changed from: private */
    public int stickersBotInfo;
    /* access modifiers changed from: private */
    public int stickersEndRow;
    /* access modifiers changed from: private */
    public int stickersHeaderRow;
    /* access modifiers changed from: private */
    public int stickersShadowRow;
    /* access modifiers changed from: private */
    public int stickersStartRow;
    /* access modifiers changed from: private */
    public int suggestAnimatedEmojiInfoRow;
    /* access modifiers changed from: private */
    public int suggestAnimatedEmojiRow;
    /* access modifiers changed from: private */
    public int suggestRow;
    private TrendingStickersAlert trendingStickersAlert;

    /* access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$createView$0(View view, MotionEvent motionEvent) {
        return true;
    }

    private List<TLRPC$StickerSetCovered> getFeaturedSets() {
        MediaDataController instance = MediaDataController.getInstance(this.currentAccount);
        if (this.currentType != 5) {
            return instance.getFeaturedStickerSets();
        }
        ArrayList arrayList = new ArrayList(instance.getFeaturedEmojiSets());
        int i = 0;
        while (i < arrayList.size()) {
            if (arrayList.get(i) == null || instance.isStickerPackInstalled(((TLRPC$StickerSetCovered) arrayList.get(i)).set.id, false)) {
                arrayList.remove(i);
                i--;
            }
            i++;
        }
        return arrayList;
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
        int i = this.currentType;
        if (i == 0) {
            MediaDataController.getInstance(this.currentAccount).checkFeaturedStickers();
            MediaDataController.getInstance(this.currentAccount).checkStickers(1);
            MediaDataController.getInstance(this.currentAccount).checkStickers(5);
        } else if (i == 6) {
            MediaDataController.getInstance(this.currentAccount).checkFeaturedEmoji();
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.featuredEmojiDidLoad);
        }
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.stickersDidLoad);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.archivedStickersCountDidLoad);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.featuredStickersDidLoad);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.currentUserPremiumStatusChanged);
        updateRows();
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        if (this.currentType == 6) {
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.featuredEmojiDidLoad);
        }
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.stickersDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.archivedStickersCountDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.featuredStickersDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.currentUserPremiumStatusChanged);
    }

    @SuppressLint({"ClickableViewAccessibility"})
    public View createView(Context context) {
        this.actionBar.setBackButtonDrawable(new BackDrawable(false));
        this.actionBar.setAllowOverlayTitle(true);
        int i = this.currentType;
        if (i == 0) {
            this.actionBar.setTitle(LocaleController.getString("StickersName", R.string.StickersName));
        } else if (i == 1) {
            this.actionBar.setTitle(LocaleController.getString("Masks", R.string.Masks));
        } else if (i == 5) {
            this.actionBar.setTitle(LocaleController.getString("Emoji", R.string.Emoji));
        }
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    if (StickersActivity.this.onBackPressed()) {
                        StickersActivity.this.finishFragment();
                    }
                } else if (i != 0 && i != 1 && i != 2) {
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
        this.selectedCountTextView.setOnTouchListener(StickersActivity$$ExternalSyntheticLambda1.INSTANCE);
        createActionMode.addItemWithWidth(2, R.drawable.msg_share, AndroidUtilities.dp(54.0f));
        if (this.currentType != 5) {
            createActionMode.addItemWithWidth(0, R.drawable.msg_archive, AndroidUtilities.dp(54.0f));
        }
        this.deleteMenuItem = createActionMode.addItemWithWidth(1, R.drawable.msg_delete, AndroidUtilities.dp(54.0f));
        this.listAdapter = new ListAdapter(context, new ArrayList(MessagesController.getInstance(this.currentAccount).filterPremiumStickers(MediaDataController.getInstance(this.currentAccount).getStickerSets(this.currentType))), getFeaturedSets());
        FrameLayout frameLayout = new FrameLayout(context);
        this.fragmentView = frameLayout;
        FrameLayout frameLayout2 = frameLayout;
        frameLayout2.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
        RecyclerListView recyclerListView = new RecyclerListView(context);
        this.listView = recyclerListView;
        recyclerListView.setFocusable(true);
        this.listView.setTag(7);
        DefaultItemAnimator defaultItemAnimator = new DefaultItemAnimator();
        defaultItemAnimator.setMoveDuration(350);
        defaultItemAnimator.setMoveInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
        this.listView.setItemAnimator(defaultItemAnimator);
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
        ((DefaultItemAnimator) this.listView.getItemAnimator()).setSupportsChangeAnimations(false);
        frameLayout2.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setAdapter(this.listAdapter);
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new StickersActivity$$ExternalSyntheticLambda4(this, context));
        this.listView.setOnItemLongClickListener((RecyclerListView.OnItemLongClickListener) new StickersActivity$$ExternalSyntheticLambda5(this));
        return this.fragmentView;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$2(Context context, View view, int i) {
        if (i < this.featuredStickersStartRow || i >= this.featuredStickersEndRow || getParentActivity() == null) {
            if (i == this.featuredStickersShowMoreRow) {
                if (this.currentType == 5) {
                    ArrayList arrayList = new ArrayList();
                    List<TLRPC$StickerSetCovered> featuredSets = getFeaturedSets();
                    if (featuredSets != null) {
                        for (int i2 = 0; i2 < featuredSets.size(); i2++) {
                            TLRPC$StickerSetCovered tLRPC$StickerSetCovered = featuredSets.get(i2);
                            if (!(tLRPC$StickerSetCovered == null || tLRPC$StickerSetCovered.set == null)) {
                                TLRPC$TL_inputStickerSetID tLRPC$TL_inputStickerSetID = new TLRPC$TL_inputStickerSetID();
                                TLRPC$StickerSet tLRPC$StickerSet = tLRPC$StickerSetCovered.set;
                                tLRPC$TL_inputStickerSetID.id = tLRPC$StickerSet.id;
                                tLRPC$TL_inputStickerSetID.access_hash = tLRPC$StickerSet.access_hash;
                                arrayList.add(tLRPC$TL_inputStickerSetID);
                            }
                        }
                    }
                    MediaDataController.getInstance(this.currentAccount).markFeaturedStickersAsRead(true, true);
                    showDialog(new EmojiPacksAlert(this, getParentActivity(), getResourceProvider(), arrayList));
                    return;
                }
                TrendingStickersAlert trendingStickersAlert2 = new TrendingStickersAlert(context, this, new TrendingStickersLayout(context, new TrendingStickersLayout.Delegate() {
                    public void onStickerSetAdd(TLRPC$StickerSetCovered tLRPC$StickerSetCovered, boolean z) {
                        MediaDataController.getInstance(StickersActivity.this.currentAccount).toggleStickerSet(StickersActivity.this.getParentActivity(), tLRPC$StickerSetCovered, 2, StickersActivity.this, false, false);
                    }

                    public void onStickerSetRemove(TLRPC$StickerSetCovered tLRPC$StickerSetCovered) {
                        MediaDataController.getInstance(StickersActivity.this.currentAccount).toggleStickerSet(StickersActivity.this.getParentActivity(), tLRPC$StickerSetCovered, 0, StickersActivity.this, false, false);
                    }
                }), (Theme.ResourcesProvider) null);
                this.trendingStickersAlert = trendingStickersAlert2;
                trendingStickersAlert2.show();
            } else if (i < this.stickersStartRow || i >= this.stickersEndRow || getParentActivity() == null) {
                if (i == this.archivedRow) {
                    presentFragment(new ArchivedStickersActivity(this.currentType));
                } else if (i == this.masksRow) {
                    presentFragment(new StickersActivity(1));
                } else if (i == this.emojiPacksRow) {
                    presentFragment(new StickersActivity(5));
                } else if (i == this.suggestRow) {
                    if (getParentActivity() != null) {
                        AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
                        builder.setTitle(LocaleController.getString("SuggestStickers", R.string.SuggestStickers));
                        String[] strArr = {LocaleController.getString("SuggestStickersAll", R.string.SuggestStickersAll), LocaleController.getString("SuggestStickersInstalled", R.string.SuggestStickersInstalled), LocaleController.getString("SuggestStickersNone", R.string.SuggestStickersNone)};
                        LinearLayout linearLayout = new LinearLayout(getParentActivity());
                        linearLayout.setOrientation(1);
                        builder.setView(linearLayout);
                        int i3 = 0;
                        while (i3 < 3) {
                            RadioColorCell radioColorCell = new RadioColorCell(getParentActivity());
                            radioColorCell.setPadding(AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f), 0);
                            radioColorCell.setTag(Integer.valueOf(i3));
                            radioColorCell.setCheckColor(Theme.getColor("radioBackground"), Theme.getColor("dialogRadioBackgroundChecked"));
                            radioColorCell.setTextAndValue(strArr[i3], SharedConfig.suggestStickers == i3);
                            linearLayout.addView(radioColorCell);
                            radioColorCell.setOnClickListener(new StickersActivity$$ExternalSyntheticLambda0(this, builder));
                            i3++;
                        }
                        showDialog(builder.create());
                    }
                } else if (i == this.loopRow) {
                    SharedConfig.toggleLoopStickers();
                    this.listAdapter.notifyItemChanged(this.loopRow, 0);
                } else if (i == this.largeEmojiRow) {
                    SharedConfig.toggleBigEmoji();
                    ((TextCheckCell) view).setChecked(SharedConfig.allowBigEmoji);
                } else if (i == this.suggestAnimatedEmojiRow) {
                    SharedConfig.toggleSuggestAnimatedEmoji();
                    ((TextCheckCell) view).setChecked(SharedConfig.suggestAnimatedEmoji);
                } else if (i == this.reactionsDoubleTapRow) {
                    presentFragment(new ReactionsDoubleTapManageActivity());
                }
            } else if (!this.listAdapter.hasSelected()) {
                TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet = (TLRPC$TL_messages_stickerSet) this.listAdapter.stickerSets.get(i - this.stickersStartRow);
                ArrayList<TLRPC$Document> arrayList2 = tLRPC$TL_messages_stickerSet.documents;
                if (arrayList2 != null && !arrayList2.isEmpty()) {
                    TLRPC$StickerSet tLRPC$StickerSet2 = tLRPC$TL_messages_stickerSet.set;
                    if (tLRPC$StickerSet2 == null || !tLRPC$StickerSet2.emojis) {
                        showDialog(new StickersAlert((Context) getParentActivity(), (BaseFragment) this, (TLRPC$InputStickerSet) null, tLRPC$TL_messages_stickerSet, (StickersAlert.StickersAlertDelegate) null));
                        return;
                    }
                    ArrayList arrayList3 = new ArrayList();
                    TLRPC$TL_inputStickerSetID tLRPC$TL_inputStickerSetID2 = new TLRPC$TL_inputStickerSetID();
                    TLRPC$StickerSet tLRPC$StickerSet3 = tLRPC$TL_messages_stickerSet.set;
                    tLRPC$TL_inputStickerSetID2.id = tLRPC$StickerSet3.id;
                    tLRPC$TL_inputStickerSetID2.access_hash = tLRPC$StickerSet3.access_hash;
                    arrayList3.add(tLRPC$TL_inputStickerSetID2);
                    showDialog(new EmojiPacksAlert(this, getParentActivity(), getResourceProvider(), arrayList3));
                }
            } else {
                this.listAdapter.toggleSelected(i);
            }
        } else {
            TLRPC$TL_inputStickerSetID tLRPC$TL_inputStickerSetID3 = new TLRPC$TL_inputStickerSetID();
            TLRPC$StickerSet tLRPC$StickerSet4 = ((TLRPC$StickerSetCovered) this.listAdapter.featuredStickerSets.get(i - this.featuredStickersStartRow)).set;
            tLRPC$TL_inputStickerSetID3.id = tLRPC$StickerSet4.id;
            tLRPC$TL_inputStickerSetID3.access_hash = tLRPC$StickerSet4.access_hash;
            if (this.currentType == 5) {
                ArrayList arrayList4 = new ArrayList(1);
                arrayList4.add(tLRPC$TL_inputStickerSetID3);
                showDialog(new EmojiPacksAlert(this, getParentActivity(), getResourceProvider(), arrayList4));
                return;
            }
            showDialog(new StickersAlert((Context) getParentActivity(), (BaseFragment) this, (TLRPC$InputStickerSet) tLRPC$TL_inputStickerSetID3, (TLRPC$TL_messages_stickerSet) null, (StickersAlert.StickersAlertDelegate) null));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$1(AlertDialog.Builder builder, View view) {
        SharedConfig.setSuggestStickers(((Integer) view.getTag()).intValue());
        this.listAdapter.notifyItemChanged(this.suggestRow);
        builder.getDismissRunnable().run();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$createView$3(View view, int i) {
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
                this.listAdapter.loadingFeaturedStickerSets.clear();
                updateRows();
            } else if (i3 == 0 && intValue == 1) {
                this.listAdapter.notifyItemChanged(this.masksRow);
            }
        } else if (i == NotificationCenter.featuredStickersDidLoad || i == NotificationCenter.featuredEmojiDidLoad) {
            updateRows();
        } else if (i != NotificationCenter.archivedStickersCountDidLoad) {
            int i4 = NotificationCenter.currentUserPremiumStatusChanged;
        } else if (objArr[0].intValue() == this.currentType) {
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
            int i = this.currentType;
            tLRPC$TL_messages_reorderStickerSets.masks = i == 1;
            tLRPC$TL_messages_reorderStickerSets.emojis = i == 5;
            for (int i2 = 0; i2 < this.listAdapter.stickerSets.size(); i2++) {
                tLRPC$TL_messages_reorderStickerSets.order.add(Long.valueOf(((TLRPC$TL_messages_stickerSet) this.listAdapter.stickerSets.get(i2)).set.id));
            }
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_messages_reorderStickerSets, new StickersActivity$$ExternalSyntheticLambda3(this));
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.stickersDidLoad, Integer.valueOf(this.currentType));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$sendReorder$4() {
        this.activeReorderingRequests--;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$sendReorder$5(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new StickersActivity$$ExternalSyntheticLambda2(this));
    }

    private void updateRows() {
        boolean z;
        boolean z2;
        DiffUtil.DiffResult diffResult;
        int i;
        MediaDataController instance = MediaDataController.getInstance(this.currentAccount);
        final ArrayList arrayList = new ArrayList(MessagesController.getInstance(this.currentAccount).filterPremiumStickers(instance.getStickerSets(this.currentType)));
        List<TLRPC$StickerSetCovered> featuredSets = getFeaturedSets();
        if (featuredSets.size() > 3) {
            featuredSets = featuredSets.subList(0, 3);
            z = true;
        } else {
            z = false;
        }
        final ArrayList arrayList2 = new ArrayList(featuredSets);
        if (this.currentType == 5) {
            z2 = UserConfig.getInstance(this.currentAccount).isPremium();
            if (!z2) {
                int i2 = 0;
                while (true) {
                    if (i2 >= arrayList.size()) {
                        break;
                    } else if (!MessageObject.isPremiumEmojiPack((TLRPC$TL_messages_stickerSet) arrayList.get(i2))) {
                        z2 = true;
                        break;
                    } else {
                        i2++;
                    }
                }
            }
            if (!z2) {
                int i3 = 0;
                while (true) {
                    if (i3 >= featuredSets.size()) {
                        break;
                    } else if (!MessageObject.isPremiumEmojiPack(featuredSets.get(i3))) {
                        z2 = true;
                        break;
                    } else {
                        i3++;
                    }
                }
            }
        } else {
            z2 = false;
        }
        DiffUtil.DiffResult diffResult2 = null;
        if (this.listAdapter != null) {
            if (!this.isPaused) {
                diffResult2 = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                    List<TLRPC$TL_messages_stickerSet> oldList;

                    {
                        this.oldList = StickersActivity.this.listAdapter.stickerSets;
                    }

                    public int getOldListSize() {
                        return this.oldList.size();
                    }

                    public int getNewListSize() {
                        return arrayList.size();
                    }

                    public boolean areItemsTheSame(int i, int i2) {
                        return this.oldList.get(i).set.id == ((TLRPC$TL_messages_stickerSet) arrayList.get(i2)).set.id;
                    }

                    public boolean areContentsTheSame(int i, int i2) {
                        TLRPC$StickerSet tLRPC$StickerSet = this.oldList.get(i).set;
                        TLRPC$StickerSet tLRPC$StickerSet2 = ((TLRPC$TL_messages_stickerSet) arrayList.get(i2)).set;
                        return TextUtils.equals(tLRPC$StickerSet.title, tLRPC$StickerSet2.title) && tLRPC$StickerSet.count == tLRPC$StickerSet2.count;
                    }
                });
                diffResult = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                    List<TLRPC$StickerSetCovered> oldList;

                    {
                        this.oldList = StickersActivity.this.listAdapter.featuredStickerSets;
                    }

                    public int getOldListSize() {
                        return this.oldList.size();
                    }

                    public int getNewListSize() {
                        return arrayList2.size();
                    }

                    public boolean areItemsTheSame(int i, int i2) {
                        return this.oldList.get(i).set.id == ((TLRPC$StickerSetCovered) arrayList2.get(i2)).set.id;
                    }

                    public boolean areContentsTheSame(int i, int i2) {
                        TLRPC$StickerSet tLRPC$StickerSet = this.oldList.get(i).set;
                        TLRPC$StickerSet tLRPC$StickerSet2 = ((TLRPC$StickerSetCovered) arrayList2.get(i2)).set;
                        return TextUtils.equals(tLRPC$StickerSet.title, tLRPC$StickerSet2.title) && tLRPC$StickerSet.count == tLRPC$StickerSet2.count && tLRPC$StickerSet.installed == tLRPC$StickerSet2.installed;
                    }
                });
            } else {
                diffResult = null;
            }
            this.listAdapter.setStickerSets(arrayList);
            this.listAdapter.setFeaturedStickerSets(arrayList2);
        } else {
            diffResult = null;
        }
        this.rowCount = 0;
        int i4 = this.currentType;
        if (i4 == 0) {
            int i5 = 0 + 1;
            this.rowCount = i5;
            this.suggestRow = 0;
            int i6 = i5 + 1;
            this.rowCount = i6;
            this.largeEmojiRow = i5;
            int i7 = i6 + 1;
            this.rowCount = i7;
            this.loopRow = i6;
            this.rowCount = i7 + 1;
            this.loopInfoRow = i7;
        } else {
            this.suggestRow = -1;
            this.largeEmojiRow = -1;
            this.loopRow = -1;
            this.loopInfoRow = -1;
        }
        if (i4 != 5 || !z2) {
            this.suggestAnimatedEmojiRow = -1;
            this.suggestAnimatedEmojiInfoRow = -1;
        } else {
            int i8 = this.rowCount;
            int i9 = i8 + 1;
            this.rowCount = i9;
            this.suggestAnimatedEmojiRow = i8;
            this.rowCount = i9 + 1;
            this.suggestAnimatedEmojiInfoRow = i9;
        }
        if (i4 == 0) {
            int i10 = this.rowCount;
            int i11 = i10 + 1;
            this.rowCount = i11;
            this.masksRow = i10;
            this.rowCount = i11 + 1;
            this.emojiPacksRow = i11;
        } else {
            this.masksRow = -1;
            this.emojiPacksRow = -1;
        }
        int i12 = 2;
        if (instance.getArchivedStickersCount(i4) == 0 || (i = this.currentType) == 5) {
            int i13 = this.archivedRow;
            int i14 = this.archivedInfoRow;
            this.archivedRow = -1;
            this.archivedInfoRow = -1;
            ListAdapter listAdapter2 = this.listAdapter;
            if (!(listAdapter2 == null || i13 == -1)) {
                if (i14 == -1) {
                    i12 = 1;
                }
                listAdapter2.notifyItemRangeRemoved(i13, i12);
            }
        } else {
            boolean z3 = this.archivedRow == -1;
            int i15 = this.rowCount;
            int i16 = i15 + 1;
            this.rowCount = i16;
            this.archivedRow = i15;
            if (i == 1) {
                this.rowCount = i16 + 1;
            } else {
                i16 = -1;
            }
            this.archivedInfoRow = i16;
            ListAdapter listAdapter3 = this.listAdapter;
            if (listAdapter3 != null && z3) {
                if (i16 == -1) {
                    i12 = 1;
                }
                listAdapter3.notifyItemRangeInserted(i15, i12);
            }
        }
        int i17 = this.currentType;
        if (i17 == 0) {
            int i18 = this.rowCount;
            this.rowCount = i18 + 1;
            this.reactionsDoubleTapRow = i18;
        } else {
            this.reactionsDoubleTapRow = -1;
        }
        this.stickersBotInfo = -1;
        if (i17 == 0) {
            int i19 = this.rowCount;
            this.rowCount = i19 + 1;
            this.stickersBotInfo = i19;
        }
        this.featuredStickersHeaderRow = -1;
        this.featuredStickersStartRow = -1;
        this.featuredStickersEndRow = -1;
        this.featuredStickersShowMoreRow = -1;
        this.featuredStickersShadowRow = -1;
        if (!arrayList2.isEmpty() && this.currentType == 0) {
            int i20 = this.rowCount;
            int i21 = i20 + 1;
            this.rowCount = i21;
            this.featuredStickersHeaderRow = i20;
            this.featuredStickersStartRow = i21;
            int size = i21 + arrayList2.size();
            this.rowCount = size;
            this.featuredStickersEndRow = size;
            if (z) {
                this.rowCount = size + 1;
                this.featuredStickersShowMoreRow = size;
            }
            int i22 = this.rowCount;
            this.rowCount = i22 + 1;
            this.featuredStickersShadowRow = i22;
        }
        int size2 = arrayList.size();
        if (size2 > 0) {
            if (this.currentType == 5 || (!arrayList2.isEmpty() && this.currentType == 0)) {
                int i23 = this.rowCount;
                this.rowCount = i23 + 1;
                this.stickersHeaderRow = i23;
            } else {
                this.stickersHeaderRow = -1;
            }
            int i24 = this.rowCount;
            this.stickersStartRow = i24;
            int i25 = i24 + size2;
            this.rowCount = i25;
            this.stickersEndRow = i25;
            int i26 = this.currentType;
            if (i26 != 1 && i26 != 5) {
                this.rowCount = i25 + 1;
                this.stickersShadowRow = i25;
                this.masksInfoRow = -1;
            } else if (i26 == 1) {
                this.rowCount = i25 + 1;
                this.masksInfoRow = i25;
                this.stickersShadowRow = -1;
            } else {
                this.stickersShadowRow = -1;
                this.masksInfoRow = -1;
            }
        } else {
            this.stickersHeaderRow = -1;
            this.stickersStartRow = -1;
            this.stickersEndRow = -1;
            this.stickersShadowRow = -1;
            this.masksInfoRow = -1;
        }
        if (!arrayList2.isEmpty() && this.currentType == 5) {
            if (size2 > 0) {
                int i27 = this.rowCount;
                this.rowCount = i27 + 1;
                this.featuredStickersShadowRow = i27;
            }
            int i28 = this.rowCount;
            int i29 = i28 + 1;
            this.rowCount = i29;
            this.featuredStickersHeaderRow = i28;
            this.featuredStickersStartRow = i29;
            int size3 = i29 + arrayList2.size();
            this.rowCount = size3;
            this.featuredStickersEndRow = size3;
            if (z) {
                this.rowCount = size3 + 1;
                this.featuredStickersShowMoreRow = size3;
            }
        }
        if (this.currentType == 5) {
            int i30 = this.rowCount;
            this.rowCount = i30 + 1;
            this.stickersBotInfo = i30;
        }
        ListAdapter listAdapter4 = this.listAdapter;
        if (listAdapter4 != null) {
            if (diffResult2 != null) {
                final int i31 = this.stickersStartRow;
                if (i31 < 0) {
                    i31 = this.rowCount;
                }
                listAdapter4.notifyItemRangeChanged(0, i31);
                diffResult2.dispatchUpdatesTo((ListUpdateCallback) new ListUpdateCallback() {
                    public void onInserted(int i, int i2) {
                        StickersActivity.this.listAdapter.notifyItemRangeInserted(i31 + i, i2);
                    }

                    public void onRemoved(int i, int i2) {
                        StickersActivity.this.listAdapter.notifyItemRangeRemoved(i31 + i, i2);
                    }

                    public void onMoved(int i, int i2) {
                        if (StickersActivity.this.currentType == 5) {
                            ListAdapter access$000 = StickersActivity.this.listAdapter;
                            int i3 = i31;
                            access$000.notifyItemMoved(i + i3, i3 + i2);
                        }
                    }

                    public void onChanged(int i, int i2, Object obj) {
                        StickersActivity.this.listAdapter.notifyItemRangeChanged(i31 + i, i2);
                    }
                });
            }
            if (diffResult != null) {
                final int i32 = this.featuredStickersStartRow;
                if (i32 < 0) {
                    i32 = this.rowCount;
                }
                this.listAdapter.notifyItemRangeChanged(0, i32);
                diffResult.dispatchUpdatesTo((ListUpdateCallback) new ListUpdateCallback() {
                    public void onMoved(int i, int i2) {
                    }

                    public void onInserted(int i, int i2) {
                        StickersActivity.this.listAdapter.notifyItemRangeInserted(i32 + i, i2);
                    }

                    public void onRemoved(int i, int i2) {
                        StickersActivity.this.listAdapter.notifyItemRangeRemoved(i32 + i, i2);
                    }

                    public void onChanged(int i, int i2, Object obj) {
                        StickersActivity.this.listAdapter.notifyItemRangeChanged(i32 + i, i2);
                    }
                });
            }
        }
    }

    @SuppressLint({"NotifyDataSetChanged"})
    public void onResume() {
        super.onResume();
        ListAdapter listAdapter2 = this.listAdapter;
        if (listAdapter2 != null) {
            listAdapter2.notifyDataSetChanged();
        }
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        /* access modifiers changed from: private */
        public final List<TLRPC$StickerSetCovered> featuredStickerSets = new ArrayList();
        /* access modifiers changed from: private */
        public final List<Long> loadingFeaturedStickerSets = new ArrayList();
        private Context mContext;
        private final LongSparseArray<Boolean> selectedItems = new LongSparseArray<>();
        /* access modifiers changed from: private */
        public final List<TLRPC$TL_messages_stickerSet> stickerSets = new ArrayList();

        public ListAdapter(Context context, List<TLRPC$TL_messages_stickerSet> list, List<TLRPC$StickerSetCovered> list2) {
            this.mContext = context;
            setStickerSets(list);
            if (list2.size() > 3) {
                setFeaturedStickerSets(list2.subList(0, 3));
            } else {
                setFeaturedStickerSets(list2);
            }
        }

        @SuppressLint({"NotifyDataSetChanged"})
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
            if (StickersActivity.this.isListeningForFeaturedUpdate) {
                boolean unused = StickersActivity.this.isListeningForFeaturedUpdate = false;
            }
        }

        public void setStickerSets(List<TLRPC$TL_messages_stickerSet> list) {
            this.stickerSets.clear();
            this.stickerSets.addAll(list);
        }

        public void setFeaturedStickerSets(List<TLRPC$StickerSetCovered> list) {
            this.featuredStickerSets.clear();
            this.featuredStickerSets.addAll(list);
        }

        public int getItemCount() {
            return StickersActivity.this.rowCount;
        }

        public long getItemId(int i) {
            if (i < StickersActivity.this.featuredStickersStartRow || i >= StickersActivity.this.featuredStickersEndRow) {
                return (i < StickersActivity.this.stickersStartRow || i >= StickersActivity.this.stickersEndRow) ? (long) i : this.stickerSets.get(i - StickersActivity.this.stickersStartRow).set.id;
            }
            return this.featuredStickerSets.get(i - StickersActivity.this.featuredStickersStartRow).set.id;
        }

        /* access modifiers changed from: private */
        public void processSelectionMenu(int i) {
            String str;
            TextView textView;
            int i2 = 0;
            if (i == 2) {
                StringBuilder sb = new StringBuilder();
                int size = this.stickerSets.size();
                while (i2 < size) {
                    TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet = this.stickerSets.get(i2);
                    if (this.selectedItems.get(tLRPC$TL_messages_stickerSet.set.id, Boolean.FALSE).booleanValue()) {
                        if (sb.length() != 0) {
                            sb.append("\n");
                        }
                        sb.append(StickersActivity.this.getLinkForSet(tLRPC$TL_messages_stickerSet));
                    }
                    i2++;
                }
                String sb2 = sb.toString();
                ShareAlert createShareAlert = ShareAlert.createShareAlert(StickersActivity.this.fragmentView.getContext(), (MessageObject) null, sb2, false, sb2, false);
                createShareAlert.setDelegate(new ShareAlert.ShareAlertDelegate() {
                    public void didShare() {
                        ListAdapter.this.clearSelected();
                    }

                    public boolean didCopy() {
                        ListAdapter.this.clearSelected();
                        return true;
                    }
                });
                createShareAlert.show();
            } else if (i == 0 || i == 1) {
                ArrayList arrayList = new ArrayList(this.selectedItems.size());
                int size2 = this.stickerSets.size();
                for (int i3 = 0; i3 < size2; i3++) {
                    TLRPC$StickerSet tLRPC$StickerSet = this.stickerSets.get(i3).set;
                    if (this.selectedItems.get(tLRPC$StickerSet.id, Boolean.FALSE).booleanValue()) {
                        arrayList.add(tLRPC$StickerSet);
                    }
                }
                int size3 = arrayList.size();
                if (size3 == 0) {
                    return;
                }
                if (size3 != 1) {
                    AlertDialog.Builder builder = new AlertDialog.Builder((Context) StickersActivity.this.getParentActivity());
                    if (i == 1) {
                        builder.setTitle(LocaleController.formatString("DeleteStickerSetsAlertTitle", R.string.DeleteStickerSetsAlertTitle, LocaleController.formatPluralString("StickerSets", size3, new Object[0])));
                        builder.setMessage(LocaleController.formatString("DeleteStickersAlertMessage", R.string.DeleteStickersAlertMessage, Integer.valueOf(size3)));
                        str = LocaleController.getString("Delete", R.string.Delete);
                    } else {
                        builder.setTitle(LocaleController.formatString("ArchiveStickerSetsAlertTitle", R.string.ArchiveStickerSetsAlertTitle, LocaleController.formatPluralString("StickerSets", size3, new Object[0])));
                        builder.setMessage(LocaleController.formatString("ArchiveStickersAlertMessage", R.string.ArchiveStickersAlertMessage, Integer.valueOf(size3)));
                        str = LocaleController.getString("Archive", R.string.Archive);
                    }
                    builder.setPositiveButton(str, new StickersActivity$ListAdapter$$ExternalSyntheticLambda0(this, arrayList, i));
                    builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (DialogInterface.OnClickListener) null);
                    AlertDialog create = builder.create();
                    StickersActivity.this.showDialog(create);
                    if (i == 1 && (textView = (TextView) create.getButton(-1)) != null) {
                        textView.setTextColor(Theme.getColor("dialogTextRed2"));
                        return;
                    }
                    return;
                }
                int size4 = this.stickerSets.size();
                while (true) {
                    if (i2 >= size4) {
                        break;
                    }
                    TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet2 = this.stickerSets.get(i2);
                    if (this.selectedItems.get(tLRPC$TL_messages_stickerSet2.set.id, Boolean.FALSE).booleanValue()) {
                        processSelectionOption(i, tLRPC$TL_messages_stickerSet2);
                        break;
                    }
                    i2++;
                }
                StickersActivity.this.listAdapter.clearSelected();
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$processSelectionMenu$0(ArrayList arrayList, int i, DialogInterface dialogInterface, int i2) {
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
                    intent.putExtra("android.intent.extra.TEXT", StickersActivity.this.getLinkForSet(tLRPC$TL_messages_stickerSet));
                    StickersActivity.this.getParentActivity().startActivityForResult(Intent.createChooser(intent, LocaleController.getString("StickersShare", R.string.StickersShare)), 500);
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
            } else if (i == 3) {
                try {
                    Locale locale = Locale.US;
                    StringBuilder sb = new StringBuilder();
                    sb.append("https://");
                    sb.append(MessagesController.getInstance(StickersActivity.this.currentAccount).linkPrefix);
                    sb.append("/");
                    sb.append(tLRPC$TL_messages_stickerSet.set.emojis ? "addemoji" : "addstickers");
                    sb.append("/%s");
                    ((ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", String.format(locale, sb.toString(), new Object[]{tLRPC$TL_messages_stickerSet.set.short_name})));
                    BulletinFactory.createCopyLinkBulletin((BaseFragment) StickersActivity.this).show();
                } catch (Exception e2) {
                    FileLog.e((Throwable) e2);
                }
            } else if (i == 4 && (indexOf = this.stickerSets.indexOf(tLRPC$TL_messages_stickerSet)) >= 0) {
                StickersActivity.this.listAdapter.toggleSelected(StickersActivity.this.stickersStartRow + indexOf);
            }
        }

        /* JADX WARNING: Code restructure failed: missing block: B:132:0x03f0, code lost:
            if (r5 == false) goto L_0x03f4;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder r11, int r12) {
            /*
                r10 = this;
                int r0 = r11.getItemViewType()
                r1 = 5
                r2 = 0
                r3 = 1
                switch(r0) {
                    case 0: goto L_0x0348;
                    case 1: goto L_0x02b1;
                    case 2: goto L_0x0163;
                    case 3: goto L_0x014a;
                    case 4: goto L_0x0101;
                    case 5: goto L_0x00a2;
                    case 6: goto L_0x0062;
                    case 7: goto L_0x000c;
                    default: goto L_0x000a;
                }
            L_0x000a:
                goto L_0x0408
            L_0x000c:
                android.view.View r11 = r11.itemView
                org.telegram.ui.Cells.FeaturedStickerSetCell2 r11 = (org.telegram.ui.Cells.FeaturedStickerSetCell2) r11
                org.telegram.ui.StickersActivity r0 = org.telegram.ui.StickersActivity.this
                int r0 = r0.featuredStickersStartRow
                int r12 = r12 - r0
                java.util.List<org.telegram.tgnet.TLRPC$StickerSetCovered> r0 = r10.featuredStickerSets
                java.lang.Object r12 = r0.get(r12)
                org.telegram.tgnet.TLRPC$StickerSetCovered r12 = (org.telegram.tgnet.TLRPC$StickerSetCovered) r12
                org.telegram.ui.StickersActivity r0 = org.telegram.ui.StickersActivity.this
                boolean r0 = r0.isListeningForFeaturedUpdate
                if (r0 != 0) goto L_0x003d
                org.telegram.tgnet.TLRPC$StickerSetCovered r0 = r11.getStickerSet()
                if (r0 == 0) goto L_0x003e
                org.telegram.tgnet.TLRPC$StickerSetCovered r0 = r11.getStickerSet()
                org.telegram.tgnet.TLRPC$StickerSet r0 = r0.set
                long r0 = r0.id
                org.telegram.tgnet.TLRPC$StickerSet r4 = r12.set
                long r4 = r4.id
                int r6 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1))
                if (r6 != 0) goto L_0x003e
            L_0x003d:
                r2 = 1
            L_0x003e:
                r6 = 1
                r7 = 0
                r8 = 0
                r4 = r11
                r5 = r12
                r9 = r2
                r4.setStickersSet(r5, r6, r7, r8, r9)
                java.util.List<java.lang.Long> r0 = r10.loadingFeaturedStickerSets
                org.telegram.tgnet.TLRPC$StickerSet r12 = r12.set
                long r3 = r12.id
                java.lang.Long r12 = java.lang.Long.valueOf(r3)
                boolean r12 = r0.contains(r12)
                r11.setDrawProgress(r12, r2)
                org.telegram.ui.StickersActivity$ListAdapter$$ExternalSyntheticLambda3 r12 = new org.telegram.ui.StickersActivity$ListAdapter$$ExternalSyntheticLambda3
                r12.<init>(r10)
                r11.setAddOnClickListener(r12)
                goto L_0x0408
            L_0x0062:
                android.view.View r11 = r11.itemView
                org.telegram.ui.Cells.HeaderCell r11 = (org.telegram.ui.Cells.HeaderCell) r11
                org.telegram.ui.StickersActivity r0 = org.telegram.ui.StickersActivity.this
                int r0 = r0.featuredStickersHeaderRow
                if (r12 != r0) goto L_0x0084
                org.telegram.ui.StickersActivity r12 = org.telegram.ui.StickersActivity.this
                int r12 = r12.currentType
                if (r12 != r1) goto L_0x0079
                int r12 = org.telegram.messenger.R.string.FeaturedEmojiPacks
                goto L_0x007b
            L_0x0079:
                int r12 = org.telegram.messenger.R.string.FeaturedStickers
            L_0x007b:
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString((int) r12)
                r11.setText(r12)
                goto L_0x0408
            L_0x0084:
                org.telegram.ui.StickersActivity r0 = org.telegram.ui.StickersActivity.this
                int r0 = r0.stickersHeaderRow
                if (r12 != r0) goto L_0x0408
                org.telegram.ui.StickersActivity r12 = org.telegram.ui.StickersActivity.this
                int r12 = r12.currentType
                if (r12 != r1) goto L_0x0097
                int r12 = org.telegram.messenger.R.string.ChooseStickerMyEmojiPacks
                goto L_0x0099
            L_0x0097:
                int r12 = org.telegram.messenger.R.string.ChooseStickerMyStickerSets
            L_0x0099:
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString((int) r12)
                r11.setText(r12)
                goto L_0x0408
            L_0x00a2:
                android.view.View r11 = r11.itemView
                org.telegram.ui.Cells.TextSettingsCell r11 = (org.telegram.ui.Cells.TextSettingsCell) r11
                int r12 = org.telegram.messenger.R.string.DoubleTapSetting
                java.lang.String r0 = "DoubleTapSetting"
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r0, r12)
                r11.setText(r12, r2)
                int r12 = org.telegram.messenger.R.drawable.msg_reactions2
                r11.setIcon(r12)
                org.telegram.ui.StickersActivity r12 = org.telegram.ui.StickersActivity.this
                int r12 = r12.currentAccount
                org.telegram.messenger.MediaDataController r12 = org.telegram.messenger.MediaDataController.getInstance(r12)
                java.lang.String r12 = r12.getDoubleTapReaction()
                if (r12 == 0) goto L_0x0408
                org.telegram.ui.StickersActivity r0 = org.telegram.ui.StickersActivity.this
                int r0 = r0.currentAccount
                org.telegram.messenger.MediaDataController r0 = org.telegram.messenger.MediaDataController.getInstance(r0)
                java.util.HashMap r0 = r0.getReactionsMap()
                java.lang.Object r12 = r0.get(r12)
                r5 = r12
                org.telegram.tgnet.TLRPC$TL_availableReaction r5 = (org.telegram.tgnet.TLRPC$TL_availableReaction) r5
                if (r5 == 0) goto L_0x0408
                org.telegram.tgnet.TLRPC$Document r12 = r5.static_icon
                java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r12 = r12.thumbs
                r0 = 1065353216(0x3var_, float:1.0)
                java.lang.String r1 = "windowBackgroundGray"
                org.telegram.messenger.SvgHelper$SvgDrawable r3 = org.telegram.messenger.DocumentObject.getSvgThumb((java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize>) r12, (java.lang.String) r1, (float) r0)
                org.telegram.ui.Components.BackupImageView r11 = r11.getValueBackupImageView()
                org.telegram.messenger.ImageReceiver r0 = r11.getImageReceiver()
                org.telegram.tgnet.TLRPC$Document r11 = r5.center_icon
                org.telegram.messenger.ImageLocation r1 = org.telegram.messenger.ImageLocation.getForDocument(r11)
                r6 = 1
                java.lang.String r2 = "100_100_lastframe"
                java.lang.String r4 = "webp"
                r0.setImage(r1, r2, r3, r4, r5, r6)
                goto L_0x0408
            L_0x0101:
                android.view.View r11 = r11.itemView
                org.telegram.ui.Cells.TextCheckCell r11 = (org.telegram.ui.Cells.TextCheckCell) r11
                org.telegram.ui.StickersActivity r0 = org.telegram.ui.StickersActivity.this
                int r0 = r0.loopRow
                if (r12 != r0) goto L_0x011c
                int r12 = org.telegram.messenger.R.string.LoopAnimatedStickers
                java.lang.String r0 = "LoopAnimatedStickers"
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r0, r12)
                boolean r0 = org.telegram.messenger.SharedConfig.loopStickers
                r11.setTextAndCheck(r12, r0, r3)
                goto L_0x0408
            L_0x011c:
                org.telegram.ui.StickersActivity r0 = org.telegram.ui.StickersActivity.this
                int r0 = r0.largeEmojiRow
                if (r12 != r0) goto L_0x0133
                int r12 = org.telegram.messenger.R.string.LargeEmoji
                java.lang.String r0 = "LargeEmoji"
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r0, r12)
                boolean r0 = org.telegram.messenger.SharedConfig.allowBigEmoji
                r11.setTextAndCheck(r12, r0, r3)
                goto L_0x0408
            L_0x0133:
                org.telegram.ui.StickersActivity r0 = org.telegram.ui.StickersActivity.this
                int r0 = r0.suggestAnimatedEmojiRow
                if (r12 != r0) goto L_0x0408
                int r12 = org.telegram.messenger.R.string.SuggestAnimatedEmoji
                java.lang.String r0 = "SuggestAnimatedEmoji"
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r0, r12)
                boolean r0 = org.telegram.messenger.SharedConfig.suggestAnimatedEmoji
                r11.setTextAndCheck(r12, r0, r2)
                goto L_0x0408
            L_0x014a:
                org.telegram.ui.StickersActivity r0 = org.telegram.ui.StickersActivity.this
                int r0 = r0.stickersShadowRow
                if (r12 != r0) goto L_0x0408
                android.view.View r11 = r11.itemView
                android.content.Context r12 = r10.mContext
                int r0 = org.telegram.messenger.R.drawable.greydivider_bottom
                java.lang.String r1 = "windowBackgroundGrayShadow"
                android.graphics.drawable.Drawable r12 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r12, (int) r0, (java.lang.String) r1)
                r11.setBackground(r12)
                goto L_0x0408
            L_0x0163:
                android.view.View r11 = r11.itemView
                org.telegram.ui.Cells.TextCell r11 = (org.telegram.ui.Cells.TextCell) r11
                org.telegram.ui.StickersActivity r0 = org.telegram.ui.StickersActivity.this
                int r0 = r0.featuredStickersShowMoreRow
                if (r12 != r0) goto L_0x0196
                java.lang.String r12 = "windowBackgroundWhiteBlueText4"
                r11.setColors(r12, r12)
                org.telegram.ui.StickersActivity r12 = org.telegram.ui.StickersActivity.this
                int r12 = r12.currentType
                if (r12 != r1) goto L_0x0189
                int r12 = org.telegram.messenger.R.string.ShowMoreEmojiPacks
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString((int) r12)
                int r0 = org.telegram.messenger.R.drawable.msg_trending
                r11.setTextAndIcon((java.lang.String) r12, (int) r0, (boolean) r2)
                goto L_0x0408
            L_0x0189:
                int r12 = org.telegram.messenger.R.string.ShowMoreStickers
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString((int) r12)
                int r0 = org.telegram.messenger.R.drawable.msg_trending
                r11.setTextAndIcon((java.lang.String) r12, (int) r0, (boolean) r2)
                goto L_0x0408
            L_0x0196:
                org.telegram.ui.Components.RLottieImageView r0 = r11.imageView
                r2 = 0
                r0.setTranslationX(r2)
                java.lang.String r0 = "windowBackgroundWhiteGrayIcon"
                java.lang.String r2 = "windowBackgroundWhiteBlackText"
                r11.setColors(r0, r2)
                org.telegram.ui.StickersActivity r0 = org.telegram.ui.StickersActivity.this
                int r0 = r0.archivedRow
                java.lang.String r2 = ""
                if (r12 != r0) goto L_0x01fe
                org.telegram.ui.StickersActivity r12 = org.telegram.ui.StickersActivity.this
                int r12 = r12.currentAccount
                org.telegram.messenger.MediaDataController r12 = org.telegram.messenger.MediaDataController.getInstance(r12)
                org.telegram.ui.StickersActivity r0 = org.telegram.ui.StickersActivity.this
                int r0 = r0.currentType
                int r12 = r12.getArchivedStickersCount(r0)
                if (r12 <= 0) goto L_0x01c7
                java.lang.String r2 = java.lang.Integer.toString(r12)
            L_0x01c7:
                org.telegram.ui.StickersActivity r12 = org.telegram.ui.StickersActivity.this
                int r12 = r12.currentType
                if (r12 != 0) goto L_0x01dc
                int r12 = org.telegram.messenger.R.string.ArchivedStickers
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString((int) r12)
                int r0 = org.telegram.messenger.R.drawable.msg_archived_stickers
                r11.setTextAndValueAndIcon(r12, r2, r0, r3)
                goto L_0x0408
            L_0x01dc:
                org.telegram.ui.StickersActivity r12 = org.telegram.ui.StickersActivity.this
                int r12 = r12.currentType
                if (r12 != r1) goto L_0x01f1
                int r12 = org.telegram.messenger.R.string.ArchivedEmojiPacks
                java.lang.String r0 = "ArchivedEmojiPacks"
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r0, r12)
                r11.setTextAndValue(r12, r2, r3)
                goto L_0x0408
            L_0x01f1:
                int r12 = org.telegram.messenger.R.string.ArchivedMasks
                java.lang.String r0 = "ArchivedMasks"
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r0, r12)
                r11.setTextAndValue(r12, r2, r3)
                goto L_0x0408
            L_0x01fe:
                org.telegram.ui.StickersActivity r0 = org.telegram.ui.StickersActivity.this
                int r0 = r0.masksRow
                if (r12 != r0) goto L_0x0240
                org.telegram.ui.StickersActivity r12 = org.telegram.ui.StickersActivity.this
                int r12 = r12.currentAccount
                org.telegram.messenger.MediaDataController r12 = org.telegram.messenger.MediaDataController.getInstance(r12)
                org.telegram.ui.StickersActivity r0 = org.telegram.ui.StickersActivity.this
                int r0 = r0.currentAccount
                org.telegram.messenger.MessagesController r0 = org.telegram.messenger.MessagesController.getInstance(r0)
                java.util.ArrayList r1 = r12.getStickerSets(r3)
                java.util.ArrayList r0 = r0.filterPremiumStickers((java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_messages_stickerSet>) r1)
                int r0 = r0.size()
                int r12 = r12.getArchivedStickersCount(r3)
                int r0 = r0 + r12
                int r12 = org.telegram.messenger.R.string.Masks
                java.lang.String r1 = "Masks"
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r1, r12)
                if (r0 <= 0) goto L_0x0239
                java.lang.String r2 = java.lang.Integer.toString(r0)
            L_0x0239:
                int r0 = org.telegram.messenger.R.drawable.msg_mask
                r11.setTextAndValueAndIcon(r12, r2, r0, r3)
                goto L_0x0408
            L_0x0240:
                org.telegram.ui.StickersActivity r0 = org.telegram.ui.StickersActivity.this
                int r0 = r0.emojiPacksRow
                if (r12 != r0) goto L_0x027c
                org.telegram.ui.StickersActivity r12 = org.telegram.ui.StickersActivity.this
                int r12 = r12.currentAccount
                org.telegram.messenger.MediaDataController r12 = org.telegram.messenger.MediaDataController.getInstance(r12)
                java.util.ArrayList r12 = r12.getStickerSets(r1)
                int r12 = r12.size()
                org.telegram.ui.Components.RLottieImageView r0 = r11.imageView
                r1 = 1073741824(0x40000000, float:2.0)
                int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
                int r1 = -r1
                float r1 = (float) r1
                r0.setTranslationX(r1)
                int r0 = org.telegram.messenger.R.string.Emoji
                java.lang.String r1 = "Emoji"
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
                if (r12 <= 0) goto L_0x0275
                java.lang.String r2 = java.lang.Integer.toString(r12)
            L_0x0275:
                int r12 = org.telegram.messenger.R.drawable.input_smile
                r11.setTextAndValueAndIcon(r0, r2, r12, r3)
                goto L_0x0408
            L_0x027c:
                org.telegram.ui.StickersActivity r0 = org.telegram.ui.StickersActivity.this
                int r0 = r0.suggestRow
                if (r12 != r0) goto L_0x0408
                int r12 = org.telegram.messenger.SharedConfig.suggestStickers
                if (r12 == 0) goto L_0x029c
                if (r12 == r3) goto L_0x0293
                int r12 = org.telegram.messenger.R.string.SuggestStickersNone
                java.lang.String r0 = "SuggestStickersNone"
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r0, r12)
                goto L_0x02a4
            L_0x0293:
                int r12 = org.telegram.messenger.R.string.SuggestStickersInstalled
                java.lang.String r0 = "SuggestStickersInstalled"
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r0, r12)
                goto L_0x02a4
            L_0x029c:
                int r12 = org.telegram.messenger.R.string.SuggestStickersAll
                java.lang.String r0 = "SuggestStickersAll"
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r0, r12)
            L_0x02a4:
                int r0 = org.telegram.messenger.R.string.SuggestStickers
                java.lang.String r1 = "SuggestStickers"
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
                r11.setTextAndValue(r0, r12, r3)
                goto L_0x0408
            L_0x02b1:
                android.view.View r11 = r11.itemView
                org.telegram.ui.Cells.TextInfoPrivacyCell r11 = (org.telegram.ui.Cells.TextInfoPrivacyCell) r11
                org.telegram.ui.StickersActivity r0 = org.telegram.ui.StickersActivity.this
                int r0 = r0.stickersBotInfo
                if (r12 != r0) goto L_0x02df
                org.telegram.ui.StickersActivity r12 = org.telegram.ui.StickersActivity.this
                int r12 = r12.currentType
                if (r12 != r1) goto L_0x02ce
                int r12 = org.telegram.messenger.R.string.EmojiBotInfo
                java.lang.String r0 = "EmojiBotInfo"
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r0, r12)
                goto L_0x02d6
            L_0x02ce:
                int r12 = org.telegram.messenger.R.string.StickersBotInfo
                java.lang.String r0 = "StickersBotInfo"
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r0, r12)
            L_0x02d6:
                java.lang.CharSequence r12 = r10.addStickersBotSpan(r12)
                r11.setText(r12)
                goto L_0x0408
            L_0x02df:
                org.telegram.ui.StickersActivity r0 = org.telegram.ui.StickersActivity.this
                int r0 = r0.archivedInfoRow
                if (r12 != r0) goto L_0x0309
                org.telegram.ui.StickersActivity r12 = org.telegram.ui.StickersActivity.this
                int r12 = r12.currentType
                if (r12 != 0) goto L_0x02fc
                int r12 = org.telegram.messenger.R.string.ArchivedStickersInfo
                java.lang.String r0 = "ArchivedStickersInfo"
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r0, r12)
                r11.setText(r12)
                goto L_0x0408
            L_0x02fc:
                int r12 = org.telegram.messenger.R.string.ArchivedMasksInfo
                java.lang.String r0 = "ArchivedMasksInfo"
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r0, r12)
                r11.setText(r12)
                goto L_0x0408
            L_0x0309:
                org.telegram.ui.StickersActivity r0 = org.telegram.ui.StickersActivity.this
                int r0 = r0.loopInfoRow
                if (r12 != r0) goto L_0x031e
                int r12 = org.telegram.messenger.R.string.LoopAnimatedStickersInfo
                java.lang.String r0 = "LoopAnimatedStickersInfo"
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r0, r12)
                r11.setText(r12)
                goto L_0x0408
            L_0x031e:
                org.telegram.ui.StickersActivity r0 = org.telegram.ui.StickersActivity.this
                int r0 = r0.suggestAnimatedEmojiInfoRow
                if (r12 != r0) goto L_0x0333
                int r12 = org.telegram.messenger.R.string.SuggestAnimatedEmojiInfo
                java.lang.String r0 = "SuggestAnimatedEmojiInfo"
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r0, r12)
                r11.setText(r12)
                goto L_0x0408
            L_0x0333:
                org.telegram.ui.StickersActivity r0 = org.telegram.ui.StickersActivity.this
                int r0 = r0.masksInfoRow
                if (r12 != r0) goto L_0x0408
                int r12 = org.telegram.messenger.R.string.MasksInfo
                java.lang.String r0 = "MasksInfo"
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r0, r12)
                r11.setText(r12)
                goto L_0x0408
            L_0x0348:
                android.view.View r11 = r11.itemView
                org.telegram.ui.Cells.StickerSetCell r11 = (org.telegram.ui.Cells.StickerSetCell) r11
                org.telegram.ui.StickersActivity r0 = org.telegram.ui.StickersActivity.this
                int r0 = r0.stickersStartRow
                int r0 = r12 - r0
                java.util.List<org.telegram.tgnet.TLRPC$TL_messages_stickerSet> r1 = r10.stickerSets
                java.lang.Object r1 = r1.get(r0)
                org.telegram.tgnet.TLRPC$TL_messages_stickerSet r1 = (org.telegram.tgnet.TLRPC$TL_messages_stickerSet) r1
                org.telegram.tgnet.TLRPC$TL_messages_stickerSet r4 = r11.getStickersSet()
                if (r4 != 0) goto L_0x0364
                if (r1 == 0) goto L_0x037c
            L_0x0364:
                org.telegram.tgnet.TLRPC$TL_messages_stickerSet r4 = r11.getStickersSet()
                if (r4 == 0) goto L_0x037e
                if (r1 == 0) goto L_0x037e
                org.telegram.tgnet.TLRPC$TL_messages_stickerSet r4 = r11.getStickersSet()
                org.telegram.tgnet.TLRPC$StickerSet r4 = r4.set
                long r4 = r4.id
                org.telegram.tgnet.TLRPC$StickerSet r6 = r1.set
                long r6 = r6.id
                int r8 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
                if (r8 != 0) goto L_0x037e
            L_0x037c:
                r4 = 1
                goto L_0x037f
            L_0x037e:
                r4 = 0
            L_0x037f:
                java.util.List<org.telegram.tgnet.TLRPC$TL_messages_stickerSet> r5 = r10.stickerSets
                int r5 = r5.size()
                int r5 = r5 - r3
                if (r0 == r5) goto L_0x038a
                r0 = 1
                goto L_0x038b
            L_0x038a:
                r0 = 0
            L_0x038b:
                r11.setStickersSet(r1, r0)
                androidx.collection.LongSparseArray<java.lang.Boolean> r0 = r10.selectedItems
                long r5 = r10.getItemId(r12)
                java.lang.Boolean r12 = java.lang.Boolean.FALSE
                java.lang.Object r12 = r0.get(r5, r12)
                java.lang.Boolean r12 = (java.lang.Boolean) r12
                boolean r12 = r12.booleanValue()
                r11.setChecked(r12, r2)
                boolean r12 = r10.hasSelected()
                r11.setReorderable(r12, r2)
                if (r1 == 0) goto L_0x0408
                org.telegram.tgnet.TLRPC$StickerSet r12 = r1.set
                if (r12 == 0) goto L_0x0408
                boolean r12 = r12.emojis
                if (r12 == 0) goto L_0x0408
                org.telegram.ui.StickersActivity r12 = org.telegram.ui.StickersActivity.this
                org.telegram.messenger.MediaDataController r12 = r12.getMediaDataController()
                org.telegram.tgnet.TLRPC$StickerSet r0 = r1.set
                long r5 = r0.id
                boolean r12 = r12.isStickerPackInstalled((long) r5)
                org.telegram.ui.StickersActivity r0 = org.telegram.ui.StickersActivity.this
                int r0 = r0.currentAccount
                org.telegram.messenger.UserConfig r0 = org.telegram.messenger.UserConfig.getInstance(r0)
                boolean r0 = r0.isPremium()
                r0 = r0 ^ r3
                if (r0 == 0) goto L_0x03f3
                r5 = 0
            L_0x03d4:
                java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r6 = r1.documents
                int r6 = r6.size()
                if (r5 >= r6) goto L_0x03ef
                java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r6 = r1.documents
                java.lang.Object r6 = r6.get(r5)
                org.telegram.tgnet.TLRPC$Document r6 = (org.telegram.tgnet.TLRPC$Document) r6
                boolean r6 = org.telegram.messenger.MessageObject.isFreeEmoji(r6)
                if (r6 != 0) goto L_0x03ec
                r5 = 1
                goto L_0x03f0
            L_0x03ec:
                int r5 = r5 + 1
                goto L_0x03d4
            L_0x03ef:
                r5 = 0
            L_0x03f0:
                if (r5 != 0) goto L_0x03f3
                goto L_0x03f4
            L_0x03f3:
                r2 = r0
            L_0x03f4:
                if (r2 == 0) goto L_0x0400
                if (r12 == 0) goto L_0x0405
                org.telegram.tgnet.TLRPC$StickerSet r12 = r1.set
                boolean r12 = r12.official
                if (r12 != 0) goto L_0x0405
                r3 = 2
                goto L_0x0405
            L_0x0400:
                if (r12 == 0) goto L_0x0404
                r3 = 4
                goto L_0x0405
            L_0x0404:
                r3 = 3
            L_0x0405:
                r11.updateButtonState(r3, r4)
            L_0x0408:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.StickersActivity.ListAdapter.onBindViewHolder(androidx.recyclerview.widget.RecyclerView$ViewHolder, int):void");
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onBindViewHolder$1(View view) {
            FeaturedStickerSetCell2 featuredStickerSetCell2 = (FeaturedStickerSetCell2) view.getParent();
            TLRPC$StickerSetCovered stickerSet = featuredStickerSetCell2.getStickerSet();
            if (!this.loadingFeaturedStickerSets.contains(Long.valueOf(stickerSet.set.id))) {
                boolean unused = StickersActivity.this.isListeningForFeaturedUpdate = true;
                this.loadingFeaturedStickerSets.add(Long.valueOf(stickerSet.set.id));
                featuredStickerSetCell2.setDrawProgress(true, true);
                if (featuredStickerSetCell2.isInstalled()) {
                    MediaDataController.getInstance(StickersActivity.this.currentAccount).toggleStickerSet(StickersActivity.this.getParentActivity(), stickerSet, 0, StickersActivity.this, false, false);
                } else {
                    MediaDataController.getInstance(StickersActivity.this.currentAccount).toggleStickerSet(StickersActivity.this.getParentActivity(), stickerSet, 2, StickersActivity.this, false, false);
                }
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
                if (itemViewType != 4) {
                    if (itemViewType == 7 && list.contains(4) && i >= StickersActivity.this.featuredStickersStartRow && i <= StickersActivity.this.featuredStickersEndRow) {
                        ((FeaturedStickerSetCell2) viewHolder.itemView).setStickersSet(this.featuredStickerSets.get(i - StickersActivity.this.featuredStickersStartRow), true, false, false, true);
                    }
                } else if (list.contains(0) && i == StickersActivity.this.loopRow) {
                    ((TextCheckCell) viewHolder.itemView).setChecked(SharedConfig.loopStickers);
                }
            } else if (i >= StickersActivity.this.stickersStartRow && i < StickersActivity.this.stickersEndRow) {
                StickerSetCell stickerSetCell = (StickerSetCell) viewHolder.itemView;
                if (list.contains(1)) {
                    stickerSetCell.setChecked(this.selectedItems.get(getItemId(i), Boolean.FALSE).booleanValue());
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
            return itemViewType == 0 || itemViewType == 7 || itemViewType == 2 || itemViewType == 4 || itemViewType == 5;
        }

        /* access modifiers changed from: private */
        public /* synthetic */ boolean lambda$onCreateViewHolder$2(StickerSetCell stickerSetCell, View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() != 0) {
                return false;
            }
            StickersActivity.this.itemTouchHelper.startDrag(StickersActivity.this.listView.getChildViewHolder(stickerSetCell));
            return false;
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onCreateViewHolder$4(View view) {
            CharSequence[] charSequenceArr;
            int[] iArr;
            int[] iArr2;
            TLRPC$TL_messages_stickerSet stickersSet = ((StickerSetCell) view.getParent()).getStickersSet();
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) StickersActivity.this.getParentActivity());
            builder.setTitle(stickersSet.set.title);
            if (stickersSet.set.official) {
                iArr2 = new int[]{0, 4};
                charSequenceArr = new CharSequence[]{LocaleController.getString("StickersHide", R.string.StickersHide), LocaleController.getString("StickersReorder", R.string.StickersReorder)};
                iArr = new int[]{R.drawable.msg_archive, R.drawable.msg_reorder};
            } else {
                CharSequence[] charSequenceArr2 = {LocaleController.getString("StickersHide", R.string.StickersHide), LocaleController.getString("StickersCopy", R.string.StickersCopy), LocaleController.getString("StickersReorder", R.string.StickersReorder), LocaleController.getString("StickersShare", R.string.StickersShare), LocaleController.getString("StickersRemove", R.string.StickersRemove)};
                iArr = new int[]{R.drawable.msg_archive, R.drawable.msg_link, R.drawable.msg_reorder, R.drawable.msg_share, R.drawable.msg_delete};
                iArr2 = new int[]{0, 3, 4, 2, 1};
                charSequenceArr = charSequenceArr2;
            }
            builder.setItems(charSequenceArr, iArr, new StickersActivity$ListAdapter$$ExternalSyntheticLambda1(this, iArr2, stickersSet));
            AlertDialog create = builder.create();
            StickersActivity.this.showDialog(create);
            if (iArr2[iArr2.length - 1] == 1) {
                create.setItemColor(charSequenceArr.length - 1, Theme.getColor("dialogTextRed2"), Theme.getColor("dialogRedIcon"));
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onCreateViewHolder$3(int[] iArr, TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet, DialogInterface dialogInterface, int i) {
            processSelectionOption(iArr[i], tLRPC$TL_messages_stickerSet);
        }

        @SuppressLint({"ClickableViewAccessibility"})
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            TextCheckCell textCheckCell;
            if (i == 0) {
                AnonymousClass3 r4 = new StickerSetCell(this.mContext, 1) {
                    /* access modifiers changed from: protected */
                    public void onPremiumButtonClick() {
                        StickersActivity.this.showDialog(new PremiumFeatureBottomSheet(StickersActivity.this, 11, false));
                    }

                    /* access modifiers changed from: protected */
                    public void onAddButtonClick() {
                        TLRPC$TL_messages_stickerSet stickersSet = getStickersSet();
                        if (stickersSet != null && stickersSet.set != null) {
                            ArrayList<TLRPC$StickerSetCovered> featuredEmojiSets = StickersActivity.this.getMediaDataController().getFeaturedEmojiSets();
                            TLRPC$StickerSetCovered tLRPC$StickerSetCovered = null;
                            int i = 0;
                            while (true) {
                                if (i >= featuredEmojiSets.size()) {
                                    break;
                                } else if (stickersSet.set.id == featuredEmojiSets.get(i).set.id) {
                                    tLRPC$StickerSetCovered = featuredEmojiSets.get(i);
                                    break;
                                } else {
                                    i++;
                                }
                            }
                            if (tLRPC$StickerSetCovered != null) {
                                if (!ListAdapter.this.loadingFeaturedStickerSets.contains(Long.valueOf(tLRPC$StickerSetCovered.set.id))) {
                                    ListAdapter.this.loadingFeaturedStickerSets.add(Long.valueOf(tLRPC$StickerSetCovered.set.id));
                                } else {
                                    return;
                                }
                            }
                            MediaDataController.getInstance(StickersActivity.this.currentAccount).toggleStickerSet(StickersActivity.this.getParentActivity(), tLRPC$StickerSetCovered == null ? stickersSet : tLRPC$StickerSetCovered, 2, StickersActivity.this, false, false);
                        }
                    }

                    /* access modifiers changed from: protected */
                    public void onRemoveButtonClick() {
                        MediaDataController.getInstance(StickersActivity.this.currentAccount).toggleStickerSet(StickersActivity.this.getParentActivity(), getStickersSet(), 0, StickersActivity.this, false, true);
                    }
                };
                r4.setBackgroundColor(StickersActivity.this.getThemedColor("windowBackgroundWhite"));
                r4.setOnReorderButtonTouchListener(new StickersActivity$ListAdapter$$ExternalSyntheticLambda4(this, r4));
                r4.setOnOptionsClick(new StickersActivity$ListAdapter$$ExternalSyntheticLambda2(this));
                textCheckCell = r4;
            } else if (i == 1) {
                TextInfoPrivacyCell textInfoPrivacyCell = new TextInfoPrivacyCell(this.mContext);
                textInfoPrivacyCell.setBackground(Theme.getThemedDrawable(this.mContext, R.drawable.greydivider_bottom, "windowBackgroundGrayShadow"));
                textCheckCell = textInfoPrivacyCell;
            } else if (i == 2) {
                TextCell textCell = new TextCell(this.mContext);
                textCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                textCheckCell = textCell;
            } else if (i == 3) {
                textCheckCell = new ShadowSectionCell(this.mContext);
            } else if (i == 5) {
                TextSettingsCell textSettingsCell = new TextSettingsCell(this.mContext);
                textSettingsCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                textCheckCell = textSettingsCell;
            } else if (i == 6) {
                HeaderCell headerCell = new HeaderCell(this.mContext);
                headerCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                textCheckCell = headerCell;
            } else if (i != 7) {
                TextCheckCell textCheckCell2 = new TextCheckCell(this.mContext);
                textCheckCell2.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                textCheckCell = textCheckCell2;
            } else {
                AnonymousClass2 r3 = new FeaturedStickerSetCell2(this.mContext, StickersActivity.this.getResourceProvider()) {
                    /* access modifiers changed from: protected */
                    public void onPremiumButtonClick() {
                        StickersActivity.this.showDialog(new PremiumFeatureBottomSheet(StickersActivity.this, 11, false));
                    }
                };
                r3.setBackgroundColor(StickersActivity.this.getThemedColor("windowBackgroundWhite"));
                r3.getTextView().setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                textCheckCell = r3;
            }
            textCheckCell.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(textCheckCell);
        }

        public int getItemViewType(int i) {
            if (i >= StickersActivity.this.featuredStickersStartRow && i < StickersActivity.this.featuredStickersEndRow) {
                return 7;
            }
            if (i >= StickersActivity.this.stickersStartRow && i < StickersActivity.this.stickersEndRow) {
                return 0;
            }
            if (i == StickersActivity.this.stickersBotInfo || i == StickersActivity.this.archivedInfoRow || i == StickersActivity.this.loopInfoRow || i == StickersActivity.this.suggestAnimatedEmojiInfoRow || i == StickersActivity.this.masksInfoRow) {
                return 1;
            }
            if (i == StickersActivity.this.archivedRow || i == StickersActivity.this.masksRow || i == StickersActivity.this.emojiPacksRow || i == StickersActivity.this.suggestRow || i == StickersActivity.this.featuredStickersShowMoreRow) {
                return 2;
            }
            if (i == StickersActivity.this.stickersShadowRow || i == StickersActivity.this.featuredStickersShadowRow) {
                return 3;
            }
            if (i == StickersActivity.this.loopRow || i == StickersActivity.this.largeEmojiRow || i == StickersActivity.this.suggestAnimatedEmojiRow) {
                return 4;
            }
            if (i == StickersActivity.this.reactionsDoubleTapRow) {
                return 5;
            }
            if (i == StickersActivity.this.featuredStickersHeaderRow || i == StickersActivity.this.stickersHeaderRow) {
                return 6;
            }
            return 0;
        }

        public void swapElements(int i, int i2) {
            if (i != i2) {
                boolean unused = StickersActivity.this.needReorder = true;
            }
            MediaDataController instance = MediaDataController.getInstance(StickersActivity.this.currentAccount);
            int access$1400 = i - StickersActivity.this.stickersStartRow;
            int access$14002 = i2 - StickersActivity.this.stickersStartRow;
            swapListElements(this.stickerSets, access$1400, access$14002);
            swapListElements(instance.getStickerSets(StickersActivity.this.currentType), access$1400, access$14002);
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
            longSparseArray.put(itemId, Boolean.valueOf(!longSparseArray.get(itemId, Boolean.FALSE).booleanValue()));
            notifyItemChanged(i, 1);
            checkActionMode();
        }

        public void clearSelected() {
            this.selectedItems.clear();
            notifyStickersItemsChanged(1);
            checkActionMode();
        }

        public boolean hasSelected() {
            return this.selectedItems.indexOfValue(Boolean.TRUE) != -1;
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
                    if (!SharedConfig.stickersReorderingHintUsed && StickersActivity.this.currentType != 5) {
                        SharedConfig.setStickersReorderingHintUsed(true);
                        Bulletin.make((FrameLayout) StickersActivity.this.parentLayout, (Bulletin.Layout) new ReorderingBulletinLayout(this.mContext, LocaleController.getString("StickersReorderHint", R.string.StickersReorderHint), (Theme.ResourcesProvider) null), 3250).show();
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
                        if (this.selectedItems.get(this.stickerSets.get(i2).set.id, Boolean.FALSE).booleanValue() && this.stickerSets.get(i2).set.official && !this.stickerSets.get(i2).set.emojis) {
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

    /* access modifiers changed from: private */
    public String getLinkForSet(TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet) {
        Locale locale = Locale.US;
        StringBuilder sb = new StringBuilder();
        sb.append("https://");
        sb.append(MessagesController.getInstance(this.currentAccount).linkPrefix);
        sb.append("/");
        sb.append(tLRPC$TL_messages_stickerSet.set.emojis ? "addemoji" : "addstickers");
        sb.append("/%s");
        return String.format(locale, sb.toString(), new Object[]{tLRPC$TL_messages_stickerSet.set.short_name});
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
