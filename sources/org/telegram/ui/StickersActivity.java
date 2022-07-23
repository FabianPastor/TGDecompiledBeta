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
import org.telegram.tgnet.TLRPC$TL_stickerSetFullCovered;
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
    private ArrayList<TLRPC$TL_messages_stickerSet> emojiPacks;
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
    private DefaultItemAnimator itemAnimator;
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
    public int suggestRow;
    private TrendingStickersAlert trendingStickersAlert;

    /* access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$createView$0(View view, MotionEvent motionEvent) {
        return true;
    }

    public class TouchHelperCallback extends ItemTouchHelper.Callback {
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int i) {
        }

        public TouchHelperCallback() {
        }

        public boolean isLongPressDragEnabled() {
            return StickersActivity.this.listAdapter.hasSelected() && StickersActivity.this.currentType != 5;
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
        boolean z;
        this.actionBar.setBackButtonDrawable(new BackDrawable(false));
        this.actionBar.setAllowOverlayTitle(true);
        int i = this.currentType;
        if (i == 0) {
            this.actionBar.setTitle(LocaleController.getString("StickersName", NUM));
        } else if (i == 1) {
            this.actionBar.setTitle(LocaleController.getString("Masks", NUM));
        } else if (i == 5) {
            this.actionBar.setTitle(LocaleController.getString("Emoji", NUM));
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
        createActionMode.addItemWithWidth(2, NUM, AndroidUtilities.dp(54.0f));
        if (this.currentType != 5) {
            createActionMode.addItemWithWidth(0, NUM, AndroidUtilities.dp(54.0f));
        }
        this.deleteMenuItem = createActionMode.addItemWithWidth(1, NUM, AndroidUtilities.dp(54.0f));
        ArrayList<TLRPC$TL_messages_stickerSet> arrayList = new ArrayList<>(MessagesController.getInstance(this.currentAccount).filterPremiumStickers(MediaDataController.getInstance(this.currentAccount).getStickerSets(this.currentType)));
        ArrayList<TLRPC$StickerSetCovered> featuredEmojiSets = this.currentType == 5 ? MediaDataController.getInstance(this.currentAccount).getFeaturedEmojiSets() : MediaDataController.getInstance(this.currentAccount).getFeaturedStickerSets();
        if (this.currentType == 5) {
            this.emojiPacks = new ArrayList<>();
            if (!UserConfig.getInstance(this.currentAccount).isPremium()) {
                int i2 = 0;
                while (i2 < arrayList.size()) {
                    if (arrayList.get(i2) != null && !MessageObject.isPremiumEmojiPack(arrayList.get(i2))) {
                        this.emojiPacks.add(arrayList.get(i2));
                        arrayList.remove(i2);
                        i2--;
                    }
                    i2++;
                }
            }
            this.emojiPacks.addAll(arrayList);
            for (int i3 = 0; i3 < featuredEmojiSets.size(); i3++) {
                int i4 = 0;
                while (true) {
                    if (i4 >= this.emojiPacks.size()) {
                        z = false;
                        break;
                    } else if (featuredEmojiSets.get(i3).set.id == this.emojiPacks.get(i4).set.id) {
                        z = true;
                        break;
                    } else {
                        i4++;
                    }
                }
                if (!z) {
                    this.emojiPacks.add(convertFeatured(featuredEmojiSets.get(i3)));
                }
            }
            arrayList = this.emojiPacks;
            featuredEmojiSets = new ArrayList<>();
        }
        this.listAdapter = new ListAdapter(context, arrayList, featuredEmojiSets);
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
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new StickersActivity$$ExternalSyntheticLambda4(this, context));
        this.listView.setOnItemLongClickListener((RecyclerListView.OnItemLongClickListener) new StickersActivity$$ExternalSyntheticLambda5(this));
        return this.fragmentView;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$2(Context context, View view, int i) {
        if (i < this.featuredStickersStartRow || i >= this.featuredStickersEndRow || getParentActivity() == null) {
            int i2 = 0;
            if (i == this.featuredStickersShowMoreRow) {
                if (this.currentType == 5) {
                    ArrayList arrayList = new ArrayList();
                    ArrayList<TLRPC$StickerSetCovered> featuredEmojiSets = MediaDataController.getInstance(this.currentAccount).getFeaturedEmojiSets();
                    while (featuredEmojiSets != null && i2 < featuredEmojiSets.size()) {
                        TLRPC$StickerSetCovered tLRPC$StickerSetCovered = featuredEmojiSets.get(i2);
                        if (!(tLRPC$StickerSetCovered == null || tLRPC$StickerSetCovered.set == null)) {
                            TLRPC$TL_inputStickerSetID tLRPC$TL_inputStickerSetID = new TLRPC$TL_inputStickerSetID();
                            TLRPC$StickerSet tLRPC$StickerSet = tLRPC$StickerSetCovered.set;
                            tLRPC$TL_inputStickerSetID.id = tLRPC$StickerSet.id;
                            tLRPC$TL_inputStickerSetID.access_hash = tLRPC$StickerSet.access_hash;
                            arrayList.add(tLRPC$TL_inputStickerSetID);
                        }
                        i2++;
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
                        builder.setTitle(LocaleController.getString("SuggestStickers", NUM));
                        String[] strArr = {LocaleController.getString("SuggestStickersAll", NUM), LocaleController.getString("SuggestStickersInstalled", NUM), LocaleController.getString("SuggestStickersNone", NUM)};
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
        DiffUtil.DiffResult diffResult;
        int i;
        MediaDataController instance = MediaDataController.getInstance(this.currentAccount);
        final ArrayList arrayList = new ArrayList(MessagesController.getInstance(this.currentAccount).filterPremiumStickers(instance.getStickerSets(this.currentType)));
        List featuredEmojiSets = this.currentType == 5 ? instance.getFeaturedEmojiSets() : instance.getFeaturedStickerSets();
        if (featuredEmojiSets.size() <= 3 || this.currentType == 5) {
            z = false;
        } else {
            featuredEmojiSets = featuredEmojiSets.subList(0, 3);
            z = true;
        }
        final ArrayList arrayList2 = new ArrayList(featuredEmojiSets);
        if (this.currentType == 5) {
            for (int i2 = 0; i2 < arrayList.size(); i2++) {
                checkPack((TLRPC$TL_messages_stickerSet) arrayList.get(i2));
            }
            for (int i3 = 0; i3 < arrayList2.size(); i3++) {
                checkPack((TLRPC$StickerSetCovered) arrayList2.get(i3));
            }
            arrayList2.clear();
            arrayList.clear();
            ArrayList<TLRPC$TL_messages_stickerSet> arrayList3 = this.emojiPacks;
            if (arrayList3 != null) {
                arrayList.addAll(arrayList3);
            }
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
        if (i4 == 0) {
            int i8 = this.rowCount;
            int i9 = i8 + 1;
            this.rowCount = i9;
            this.masksRow = i8;
            this.rowCount = i9 + 1;
            this.emojiPacksRow = i9;
        } else {
            this.masksRow = -1;
            this.emojiPacksRow = -1;
        }
        int archivedStickersCount = instance.getArchivedStickersCount(i4);
        int i10 = 2;
        if (archivedStickersCount != 0) {
            boolean z2 = this.archivedRow == -1;
            int i11 = this.rowCount;
            int i12 = i11 + 1;
            this.rowCount = i12;
            this.archivedRow = i11;
            if (this.currentType == 1) {
                this.rowCount = i12 + 1;
            } else {
                i12 = -1;
            }
            this.archivedInfoRow = i12;
            ListAdapter listAdapter2 = this.listAdapter;
            if (listAdapter2 != null && z2) {
                if (i12 == -1) {
                    i10 = 1;
                }
                listAdapter2.notifyItemRangeInserted(i11, i10);
            }
        } else {
            int i13 = this.archivedRow;
            int i14 = this.archivedInfoRow;
            this.archivedRow = -1;
            this.archivedInfoRow = -1;
            ListAdapter listAdapter3 = this.listAdapter;
            if (!(listAdapter3 == null || i13 == -1)) {
                if (i14 == -1) {
                    i10 = 1;
                }
                listAdapter3.notifyItemRangeRemoved(i13, i10);
            }
        }
        if (this.currentType == 0) {
            int i15 = this.rowCount;
            int i16 = i15 + 1;
            this.rowCount = i16;
            this.reactionsDoubleTapRow = i15;
            this.rowCount = i16 + 1;
            this.stickersBotInfo = i16;
        } else {
            this.reactionsDoubleTapRow = -1;
            this.stickersBotInfo = -1;
        }
        if (arrayList2.isEmpty() || !((i = this.currentType) == 0 || i == 5)) {
            this.featuredStickersHeaderRow = -1;
            this.featuredStickersStartRow = -1;
            this.featuredStickersEndRow = -1;
            this.featuredStickersShowMoreRow = -1;
            this.featuredStickersShadowRow = -1;
        } else {
            int i17 = this.rowCount;
            int i18 = i17 + 1;
            this.rowCount = i18;
            this.featuredStickersHeaderRow = i17;
            this.featuredStickersStartRow = i18;
            int size = i18 + arrayList2.size();
            this.rowCount = size;
            this.featuredStickersEndRow = size;
            if (z) {
                this.rowCount = size + 1;
                this.featuredStickersShowMoreRow = size;
            } else {
                this.featuredStickersShowMoreRow = -1;
            }
            int i19 = this.rowCount;
            this.rowCount = i19 + 1;
            this.featuredStickersShadowRow = i19;
        }
        int size2 = arrayList.size();
        if (size2 > 0) {
            if (this.featuredStickersHeaderRow != -1) {
                int i20 = this.rowCount;
                this.rowCount = i20 + 1;
                this.stickersHeaderRow = i20;
            } else {
                this.stickersHeaderRow = -1;
            }
            int i21 = this.rowCount;
            this.stickersStartRow = i21;
            int i22 = i21 + size2;
            this.rowCount = i22;
            this.stickersEndRow = i22;
            if (this.currentType != 1) {
                this.rowCount = i22 + 1;
                this.stickersShadowRow = i22;
                this.masksInfoRow = -1;
            } else {
                this.rowCount = i22 + 1;
                this.masksInfoRow = i22;
                this.stickersShadowRow = -1;
            }
        } else {
            this.stickersHeaderRow = -1;
            this.stickersStartRow = -1;
            this.stickersEndRow = -1;
            this.stickersShadowRow = -1;
            this.masksInfoRow = -1;
        }
        ListAdapter listAdapter4 = this.listAdapter;
        if (listAdapter4 != null) {
            if (diffResult2 != null) {
                final int i23 = this.stickersStartRow;
                if (i23 < 0) {
                    i23 = this.rowCount;
                }
                listAdapter4.notifyItemRangeChanged(0, i23);
                diffResult2.dispatchUpdatesTo((ListUpdateCallback) new ListUpdateCallback() {
                    public void onInserted(int i, int i2) {
                        StickersActivity.this.listAdapter.notifyItemRangeInserted(i23 + i, i2);
                    }

                    public void onRemoved(int i, int i2) {
                        StickersActivity.this.listAdapter.notifyItemRangeRemoved(i23 + i, i2);
                    }

                    public void onMoved(int i, int i2) {
                        if (StickersActivity.this.currentType == 5) {
                            ListAdapter access$000 = StickersActivity.this.listAdapter;
                            int i3 = i23;
                            access$000.notifyItemMoved(i + i3, i3 + i2);
                        }
                    }

                    public void onChanged(int i, int i2, Object obj) {
                        StickersActivity.this.listAdapter.notifyItemRangeChanged(i23 + i, i2);
                    }
                });
            }
            if (diffResult != null) {
                final int i24 = this.featuredStickersStartRow;
                if (i24 < 0) {
                    i24 = this.rowCount;
                }
                this.listAdapter.notifyItemRangeChanged(0, i24);
                diffResult.dispatchUpdatesTo((ListUpdateCallback) new ListUpdateCallback() {
                    public void onMoved(int i, int i2) {
                    }

                    public void onInserted(int i, int i2) {
                        StickersActivity.this.listAdapter.notifyItemRangeInserted(i24 + i, i2);
                    }

                    public void onRemoved(int i, int i2) {
                        StickersActivity.this.listAdapter.notifyItemRangeRemoved(i24 + i, i2);
                    }

                    public void onChanged(int i, int i2, Object obj) {
                        StickersActivity.this.listAdapter.notifyItemRangeChanged(i24 + i, i2);
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
                        builder.setTitle(LocaleController.formatString("DeleteStickerSetsAlertTitle", NUM, LocaleController.formatPluralString("StickerSets", size3, new Object[0])));
                        builder.setMessage(LocaleController.formatString("DeleteStickersAlertMessage", NUM, Integer.valueOf(size3)));
                        str = LocaleController.getString("Delete", NUM);
                    } else {
                        builder.setTitle(LocaleController.formatString("ArchiveStickerSetsAlertTitle", NUM, LocaleController.formatPluralString("StickerSets", size3, new Object[0])));
                        builder.setMessage(LocaleController.formatString("ArchiveStickersAlertMessage", NUM, Integer.valueOf(size3)));
                        str = LocaleController.getString("Archive", NUM);
                    }
                    builder.setPositiveButton(str, new StickersActivity$ListAdapter$$ExternalSyntheticLambda0(this, arrayList, i));
                    builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
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
                    StickersActivity.this.getParentActivity().startActivityForResult(Intent.createChooser(intent, LocaleController.getString("StickersShare", NUM)), 500);
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

        /* JADX WARNING: Code restructure failed: missing block: B:116:0x03ae, code lost:
            if (r5 == false) goto L_0x03b2;
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
                    case 0: goto L_0x0320;
                    case 1: goto L_0x02aa;
                    case 2: goto L_0x014f;
                    case 3: goto L_0x0135;
                    case 4: goto L_0x0105;
                    case 5: goto L_0x00a6;
                    case 6: goto L_0x0062;
                    case 7: goto L_0x000c;
                    default: goto L_0x000a;
                }
            L_0x000a:
                goto L_0x03ca
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
                goto L_0x03ca
            L_0x0062:
                android.view.View r11 = r11.itemView
                org.telegram.ui.Cells.HeaderCell r11 = (org.telegram.ui.Cells.HeaderCell) r11
                org.telegram.ui.StickersActivity r0 = org.telegram.ui.StickersActivity.this
                int r0 = r0.featuredStickersHeaderRow
                if (r12 != r0) goto L_0x0086
                org.telegram.ui.StickersActivity r12 = org.telegram.ui.StickersActivity.this
                int r12 = r12.currentType
                if (r12 != r1) goto L_0x007a
                r12 = 2131625840(0x7f0e0770, float:1.88789E38)
                goto L_0x007d
            L_0x007a:
                r12 = 2131625842(0x7f0e0772, float:1.8878903E38)
            L_0x007d:
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString((int) r12)
                r11.setText(r12)
                goto L_0x03ca
            L_0x0086:
                org.telegram.ui.StickersActivity r0 = org.telegram.ui.StickersActivity.this
                int r0 = r0.stickersHeaderRow
                if (r12 != r0) goto L_0x03ca
                org.telegram.ui.StickersActivity r12 = org.telegram.ui.StickersActivity.this
                int r12 = r12.currentType
                if (r12 != r1) goto L_0x009a
                r12 = 2131625127(0x7f0e04a7, float:1.8877453E38)
                goto L_0x009d
            L_0x009a:
                r12 = 2131625128(0x7f0e04a8, float:1.8877455E38)
            L_0x009d:
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString((int) r12)
                r11.setText(r12)
                goto L_0x03ca
            L_0x00a6:
                android.view.View r11 = r11.itemView
                org.telegram.ui.Cells.TextSettingsCell r11 = (org.telegram.ui.Cells.TextSettingsCell) r11
                r12 = 2131625546(0x7f0e064a, float:1.8878303E38)
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString((int) r12)
                r11.setText(r12, r2)
                r12 = 2131165894(0x7var_c6, float:1.7946018E38)
                r11.setIcon(r12)
                org.telegram.ui.StickersActivity r12 = org.telegram.ui.StickersActivity.this
                int r12 = r12.currentAccount
                org.telegram.messenger.MediaDataController r12 = org.telegram.messenger.MediaDataController.getInstance(r12)
                java.lang.String r12 = r12.getDoubleTapReaction()
                if (r12 == 0) goto L_0x03ca
                org.telegram.ui.StickersActivity r0 = org.telegram.ui.StickersActivity.this
                int r0 = r0.currentAccount
                org.telegram.messenger.MediaDataController r0 = org.telegram.messenger.MediaDataController.getInstance(r0)
                java.util.HashMap r0 = r0.getReactionsMap()
                java.lang.Object r12 = r0.get(r12)
                r5 = r12
                org.telegram.tgnet.TLRPC$TL_availableReaction r5 = (org.telegram.tgnet.TLRPC$TL_availableReaction) r5
                if (r5 == 0) goto L_0x03ca
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
                goto L_0x03ca
            L_0x0105:
                android.view.View r11 = r11.itemView
                org.telegram.ui.Cells.TextCheckCell r11 = (org.telegram.ui.Cells.TextCheckCell) r11
                org.telegram.ui.StickersActivity r0 = org.telegram.ui.StickersActivity.this
                int r0 = r0.loopRow
                if (r12 != r0) goto L_0x011f
                r12 = 2131626555(0x7f0e0a3b, float:1.888035E38)
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString((int) r12)
                boolean r0 = org.telegram.messenger.SharedConfig.loopStickers
                r11.setTextAndCheck(r12, r0, r3)
                goto L_0x03ca
            L_0x011f:
                org.telegram.ui.StickersActivity r0 = org.telegram.ui.StickersActivity.this
                int r0 = r0.largeEmojiRow
                if (r12 != r0) goto L_0x03ca
                r12 = 2131626413(0x7f0e09ad, float:1.8880061E38)
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString((int) r12)
                boolean r0 = org.telegram.messenger.SharedConfig.allowBigEmoji
                r11.setTextAndCheck(r12, r0, r3)
                goto L_0x03ca
            L_0x0135:
                org.telegram.ui.StickersActivity r0 = org.telegram.ui.StickersActivity.this
                int r0 = r0.stickersShadowRow
                if (r12 != r0) goto L_0x03ca
                android.view.View r11 = r11.itemView
                android.content.Context r12 = r10.mContext
                r0 = 2131165436(0x7var_fc, float:1.794509E38)
                java.lang.String r1 = "windowBackgroundGrayShadow"
                android.graphics.drawable.Drawable r12 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r12, (int) r0, (java.lang.String) r1)
                r11.setBackground(r12)
                goto L_0x03ca
            L_0x014f:
                android.view.View r11 = r11.itemView
                org.telegram.ui.Cells.TextCell r11 = (org.telegram.ui.Cells.TextCell) r11
                org.telegram.ui.StickersActivity r0 = org.telegram.ui.StickersActivity.this
                int r0 = r0.featuredStickersShowMoreRow
                if (r12 != r0) goto L_0x0183
                java.lang.String r12 = "windowBackgroundWhiteBlueText4"
                r11.setColors(r12, r12)
                org.telegram.ui.StickersActivity r12 = org.telegram.ui.StickersActivity.this
                int r12 = r12.currentType
                r0 = 2131165969(0x7var_, float:1.794617E38)
                if (r12 != r1) goto L_0x0177
                r12 = 2131628404(0x7f0e1174, float:1.88841E38)
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString((int) r12)
                r11.setTextAndIcon((java.lang.String) r12, (int) r0, (boolean) r2)
                goto L_0x03ca
            L_0x0177:
                r12 = 2131628405(0x7f0e1175, float:1.8884102E38)
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString((int) r12)
                r11.setTextAndIcon((java.lang.String) r12, (int) r0, (boolean) r2)
                goto L_0x03ca
            L_0x0183:
                org.telegram.ui.Components.RLottieImageView r0 = r11.imageView
                r2 = 0
                r0.setTranslationX(r2)
                java.lang.String r0 = "windowBackgroundWhiteGrayIcon"
                java.lang.String r2 = "windowBackgroundWhiteBlackText"
                r11.setColors(r0, r2)
                org.telegram.ui.StickersActivity r0 = org.telegram.ui.StickersActivity.this
                int r0 = r0.archivedRow
                java.lang.String r2 = ""
                if (r12 != r0) goto L_0x01ef
                org.telegram.ui.StickersActivity r12 = org.telegram.ui.StickersActivity.this
                int r12 = r12.currentAccount
                org.telegram.messenger.MediaDataController r12 = org.telegram.messenger.MediaDataController.getInstance(r12)
                org.telegram.ui.StickersActivity r0 = org.telegram.ui.StickersActivity.this
                int r0 = r0.currentType
                int r12 = r12.getArchivedStickersCount(r0)
                if (r12 <= 0) goto L_0x01b4
                java.lang.String r2 = java.lang.Integer.toString(r12)
            L_0x01b4:
                org.telegram.ui.StickersActivity r12 = org.telegram.ui.StickersActivity.this
                int r12 = r12.currentType
                if (r12 != 0) goto L_0x01cb
                r12 = 2131624425(0x7f0e01e9, float:1.887603E38)
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString((int) r12)
                r0 = 2131165638(0x7var_c6, float:1.7945499E38)
                r11.setTextAndValueAndIcon(r12, r2, r0, r3)
                goto L_0x03ca
            L_0x01cb:
                org.telegram.ui.StickersActivity r12 = org.telegram.ui.StickersActivity.this
                int r12 = r12.currentType
                if (r12 != r1) goto L_0x01e1
                r12 = 2131624418(0x7f0e01e2, float:1.8876015E38)
                java.lang.String r0 = "ArchivedEmojiPacks"
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r0, r12)
                r11.setTextAndValue(r12, r2, r3)
                goto L_0x03ca
            L_0x01e1:
                r12 = 2131624420(0x7f0e01e4, float:1.887602E38)
                java.lang.String r0 = "ArchivedMasks"
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r0, r12)
                r11.setTextAndValue(r12, r2, r3)
                goto L_0x03ca
            L_0x01ef:
                org.telegram.ui.StickersActivity r0 = org.telegram.ui.StickersActivity.this
                int r0 = r0.masksRow
                if (r12 != r0) goto L_0x0233
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
                r12 = 2131626591(0x7f0e0a5f, float:1.8880423E38)
                java.lang.String r1 = "Masks"
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r1, r12)
                if (r0 <= 0) goto L_0x022b
                java.lang.String r2 = java.lang.Integer.toString(r0)
            L_0x022b:
                r0 = 2131165797(0x7var_, float:1.7945821E38)
                r11.setTextAndValueAndIcon(r12, r2, r0, r3)
                goto L_0x03ca
            L_0x0233:
                org.telegram.ui.StickersActivity r0 = org.telegram.ui.StickersActivity.this
                int r0 = r0.emojiPacksRow
                if (r12 != r0) goto L_0x0271
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
                r0 = 2131625616(0x7f0e0690, float:1.8878445E38)
                java.lang.String r1 = "Emoji"
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
                if (r12 <= 0) goto L_0x0269
                java.lang.String r2 = java.lang.Integer.toString(r12)
            L_0x0269:
                r12 = 2131166159(0x7var_cf, float:1.7946555E38)
                r11.setTextAndValueAndIcon(r0, r2, r12, r3)
                goto L_0x03ca
            L_0x0271:
                org.telegram.ui.StickersActivity r0 = org.telegram.ui.StickersActivity.this
                int r0 = r0.suggestRow
                if (r12 != r0) goto L_0x03ca
                int r12 = org.telegram.messenger.SharedConfig.suggestStickers
                if (r12 == 0) goto L_0x0293
                if (r12 == r3) goto L_0x0289
                r12 = 2131628569(0x7f0e1219, float:1.8884434E38)
                java.lang.String r0 = "SuggestStickersNone"
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r0, r12)
                goto L_0x029c
            L_0x0289:
                r12 = 2131628568(0x7f0e1218, float:1.8884432E38)
                java.lang.String r0 = "SuggestStickersInstalled"
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r0, r12)
                goto L_0x029c
            L_0x0293:
                r12 = 2131628567(0x7f0e1217, float:1.888443E38)
                java.lang.String r0 = "SuggestStickersAll"
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r0, r12)
            L_0x029c:
                r0 = 2131628566(0x7f0e1216, float:1.8884428E38)
                java.lang.String r1 = "SuggestStickers"
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
                r11.setTextAndValue(r0, r12, r3)
                goto L_0x03ca
            L_0x02aa:
                android.view.View r11 = r11.itemView
                org.telegram.ui.Cells.TextInfoPrivacyCell r11 = (org.telegram.ui.Cells.TextInfoPrivacyCell) r11
                org.telegram.ui.StickersActivity r0 = org.telegram.ui.StickersActivity.this
                int r0 = r0.stickersBotInfo
                if (r12 != r0) goto L_0x02c8
                r12 = 2131628511(0x7f0e11df, float:1.8884317E38)
                java.lang.String r0 = "StickersBotInfo"
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r0, r12)
                java.lang.CharSequence r12 = r10.addStickersBotSpan(r12)
                r11.setText(r12)
                goto L_0x03ca
            L_0x02c8:
                org.telegram.ui.StickersActivity r0 = org.telegram.ui.StickersActivity.this
                int r0 = r0.archivedInfoRow
                if (r12 != r0) goto L_0x02f4
                org.telegram.ui.StickersActivity r12 = org.telegram.ui.StickersActivity.this
                int r12 = r12.currentType
                if (r12 != 0) goto L_0x02e6
                r12 = 2131624429(0x7f0e01ed, float:1.8876037E38)
                java.lang.String r0 = "ArchivedStickersInfo"
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r0, r12)
                r11.setText(r12)
                goto L_0x03ca
            L_0x02e6:
                r12 = 2131624424(0x7f0e01e8, float:1.8876027E38)
                java.lang.String r0 = "ArchivedMasksInfo"
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r0, r12)
                r11.setText(r12)
                goto L_0x03ca
            L_0x02f4:
                org.telegram.ui.StickersActivity r0 = org.telegram.ui.StickersActivity.this
                int r0 = r0.loopInfoRow
                if (r12 != r0) goto L_0x030a
                r12 = 2131626556(0x7f0e0a3c, float:1.8880352E38)
                java.lang.String r0 = "LoopAnimatedStickersInfo"
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r0, r12)
                r11.setText(r12)
                goto L_0x03ca
            L_0x030a:
                org.telegram.ui.StickersActivity r0 = org.telegram.ui.StickersActivity.this
                int r0 = r0.masksInfoRow
                if (r12 != r0) goto L_0x03ca
                r12 = 2131626600(0x7f0e0a68, float:1.888044E38)
                java.lang.String r0 = "MasksInfo"
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r0, r12)
                r11.setText(r12)
                goto L_0x03ca
            L_0x0320:
                android.view.View r11 = r11.itemView
                org.telegram.ui.Cells.StickerSetCell r11 = (org.telegram.ui.Cells.StickerSetCell) r11
                org.telegram.ui.StickersActivity r0 = org.telegram.ui.StickersActivity.this
                int r0 = r0.stickersStartRow
                int r0 = r12 - r0
                java.util.List<org.telegram.tgnet.TLRPC$TL_messages_stickerSet> r1 = r10.stickerSets
                java.lang.Object r1 = r1.get(r0)
                org.telegram.tgnet.TLRPC$TL_messages_stickerSet r1 = (org.telegram.tgnet.TLRPC$TL_messages_stickerSet) r1
                org.telegram.tgnet.TLRPC$TL_messages_stickerSet r4 = r11.getStickersSet()
                if (r4 == r1) goto L_0x033c
                r4 = 1
                goto L_0x033d
            L_0x033c:
                r4 = 0
            L_0x033d:
                java.util.List<org.telegram.tgnet.TLRPC$TL_messages_stickerSet> r5 = r10.stickerSets
                int r5 = r5.size()
                int r5 = r5 - r3
                if (r0 == r5) goto L_0x0348
                r0 = 1
                goto L_0x0349
            L_0x0348:
                r0 = 0
            L_0x0349:
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
                if (r1 == 0) goto L_0x03ca
                org.telegram.tgnet.TLRPC$StickerSet r12 = r1.set
                if (r12 == 0) goto L_0x03ca
                boolean r12 = r12.emojis
                if (r12 == 0) goto L_0x03ca
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
                if (r0 == 0) goto L_0x03b1
                r5 = 0
            L_0x0392:
                java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r6 = r1.documents
                int r6 = r6.size()
                if (r5 >= r6) goto L_0x03ad
                java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r6 = r1.documents
                java.lang.Object r6 = r6.get(r5)
                org.telegram.tgnet.TLRPC$Document r6 = (org.telegram.tgnet.TLRPC$Document) r6
                boolean r6 = org.telegram.messenger.MessageObject.isFreeEmoji(r6)
                if (r6 != 0) goto L_0x03aa
                r5 = 1
                goto L_0x03ae
            L_0x03aa:
                int r5 = r5 + 1
                goto L_0x0392
            L_0x03ad:
                r5 = 0
            L_0x03ae:
                if (r5 != 0) goto L_0x03b1
                goto L_0x03b2
            L_0x03b1:
                r2 = r0
            L_0x03b2:
                if (r2 == 0) goto L_0x03c0
                if (r12 == 0) goto L_0x03be
                org.telegram.tgnet.TLRPC$StickerSet r12 = r1.set
                boolean r12 = r12.official
                if (r12 != 0) goto L_0x03be
                r12 = 2
                goto L_0x03c5
            L_0x03be:
                r12 = 1
                goto L_0x03c5
            L_0x03c0:
                if (r12 == 0) goto L_0x03c4
                r12 = 4
                goto L_0x03c5
            L_0x03c4:
                r12 = 3
            L_0x03c5:
                r0 = r4 ^ 1
                r11.updateButtonState(r12, r0)
            L_0x03ca:
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
                charSequenceArr = new CharSequence[]{LocaleController.getString("StickersHide", NUM), LocaleController.getString("StickersReorder", NUM)};
                iArr = new int[]{NUM, NUM};
            } else {
                iArr = new int[]{NUM, NUM, NUM, NUM, NUM};
                iArr2 = new int[]{0, 3, 4, 2, 1};
                charSequenceArr = new CharSequence[]{LocaleController.getString("StickersHide", NUM), LocaleController.getString("StickersCopy", NUM), LocaleController.getString("StickersReorder", NUM), LocaleController.getString("StickersShare", NUM), LocaleController.getString("StickersRemove", NUM)};
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
                AnonymousClass2 r4 = new StickerSetCell(this.mContext, 1) {
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
                            updateButtonState(4, true);
                            MediaDataController.getInstance(StickersActivity.this.currentAccount).toggleStickerSet(StickersActivity.this.getParentActivity(), tLRPC$StickerSetCovered == null ? stickersSet : tLRPC$StickerSetCovered, 2, StickersActivity.this, false, false);
                        }
                    }

                    /* access modifiers changed from: protected */
                    public void onRemoveButtonClick() {
                        updateButtonState(3, true);
                        MediaDataController.getInstance(StickersActivity.this.currentAccount).toggleStickerSet(StickersActivity.this.getParentActivity(), getStickersSet(), 0, StickersActivity.this, false, false);
                    }
                };
                r4.setBackgroundColor(StickersActivity.this.getThemedColor("windowBackgroundWhite"));
                r4.setOnReorderButtonTouchListener(new StickersActivity$ListAdapter$$ExternalSyntheticLambda4(this, r4));
                r4.setOnOptionsClick(new StickersActivity$ListAdapter$$ExternalSyntheticLambda2(this));
                textCheckCell = r4;
            } else if (i == 1) {
                TextInfoPrivacyCell textInfoPrivacyCell = new TextInfoPrivacyCell(this.mContext);
                textInfoPrivacyCell.setBackground(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
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
                FeaturedStickerSetCell2 featuredStickerSetCell2 = new FeaturedStickerSetCell2(this.mContext, StickersActivity.this.getResourceProvider());
                featuredStickerSetCell2.setBackgroundColor(StickersActivity.this.getThemedColor("windowBackgroundWhite"));
                featuredStickerSetCell2.getTextView().setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                textCheckCell = featuredStickerSetCell2;
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
            if (i == StickersActivity.this.stickersBotInfo || i == StickersActivity.this.archivedInfoRow || i == StickersActivity.this.loopInfoRow || i == StickersActivity.this.masksInfoRow) {
                return 1;
            }
            if (i == StickersActivity.this.archivedRow || i == StickersActivity.this.masksRow || i == StickersActivity.this.emojiPacksRow || i == StickersActivity.this.suggestRow || i == StickersActivity.this.featuredStickersShowMoreRow) {
                return 2;
            }
            if (i == StickersActivity.this.stickersShadowRow || i == StickersActivity.this.featuredStickersShadowRow) {
                return 3;
            }
            if (i == StickersActivity.this.loopRow || i == StickersActivity.this.largeEmojiRow) {
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
                    if (!SharedConfig.stickersReorderingHintUsed) {
                        SharedConfig.setStickersReorderingHintUsed(true);
                        Bulletin.make((FrameLayout) StickersActivity.this.parentLayout, (Bulletin.Layout) new ReorderingBulletinLayout(this.mContext, LocaleController.getString("StickersReorderHint", NUM), (Theme.ResourcesProvider) null), 3250).show();
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
                    if (i2 >= size) {
                        z = true;
                        break;
                    } else if (this.stickerSets.get(i2).set.emojis || (this.selectedItems.get(this.stickerSets.get(i2).set.id, Boolean.FALSE).booleanValue() && this.stickerSets.get(i2).set.official)) {
                        z = false;
                    } else {
                        i2++;
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

    private void checkPack(TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet) {
        if (tLRPC$TL_messages_stickerSet != null) {
            if (this.emojiPacks == null) {
                ArrayList<TLRPC$TL_messages_stickerSet> arrayList = new ArrayList<>();
                this.emojiPacks = arrayList;
                arrayList.add(tLRPC$TL_messages_stickerSet);
                return;
            }
            boolean z = false;
            int i = 0;
            while (true) {
                if (i >= this.emojiPacks.size()) {
                    break;
                } else if (this.emojiPacks.get(i).set.id == tLRPC$TL_messages_stickerSet.set.id) {
                    z = true;
                    break;
                } else {
                    i++;
                }
            }
            if (!z) {
                this.emojiPacks.add(tLRPC$TL_messages_stickerSet);
            }
        }
    }

    private void checkPack(TLRPC$StickerSetCovered tLRPC$StickerSetCovered) {
        if (tLRPC$StickerSetCovered != null) {
            if (this.emojiPacks == null) {
                ArrayList<TLRPC$TL_messages_stickerSet> arrayList = new ArrayList<>();
                this.emojiPacks = arrayList;
                arrayList.add(convertFeatured(tLRPC$StickerSetCovered));
                return;
            }
            boolean z = false;
            int i = 0;
            while (true) {
                if (i >= this.emojiPacks.size()) {
                    break;
                } else if (this.emojiPacks.get(i).set.id == tLRPC$StickerSetCovered.set.id) {
                    z = true;
                    break;
                } else {
                    i++;
                }
            }
            if (!z) {
                this.emojiPacks.add(convertFeatured(tLRPC$StickerSetCovered));
            }
        }
    }

    private TLRPC$TL_messages_stickerSet convertFeatured(TLRPC$StickerSetCovered tLRPC$StickerSetCovered) {
        if (tLRPC$StickerSetCovered == null) {
            return null;
        }
        TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet = new TLRPC$TL_messages_stickerSet();
        tLRPC$TL_messages_stickerSet.set = tLRPC$StickerSetCovered.set;
        if (tLRPC$StickerSetCovered instanceof TLRPC$TL_stickerSetFullCovered) {
            TLRPC$TL_stickerSetFullCovered tLRPC$TL_stickerSetFullCovered = (TLRPC$TL_stickerSetFullCovered) tLRPC$StickerSetCovered;
            tLRPC$TL_messages_stickerSet.documents = tLRPC$TL_stickerSetFullCovered.documents;
            tLRPC$TL_messages_stickerSet.packs = tLRPC$TL_stickerSetFullCovered.packs;
        } else {
            tLRPC$TL_messages_stickerSet.documents = tLRPC$StickerSetCovered.covers;
        }
        return tLRPC$TL_messages_stickerSet;
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
