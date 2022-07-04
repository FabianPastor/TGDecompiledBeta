package org.telegram.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;
import androidx.core.graphics.ColorUtils$$ExternalSyntheticBackport0;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.StickerSetCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.FlickerLoadingView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.StickerEmptyView;
import org.telegram.ui.Components.StickersAlert;
import org.telegram.ui.Components.URLSpanNoUnderline;
import org.telegram.ui.Components.VerticalPositionAutoAnimator;

public class GroupStickersActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private long chatId;
    private FrameLayout emptyFrameView;
    /* access modifiers changed from: private */
    public StickerEmptyView emptyView;
    /* access modifiers changed from: private */
    public int headerRow;
    /* access modifiers changed from: private */
    public TLRPC.ChatFull info;
    /* access modifiers changed from: private */
    public int infoRow;
    /* access modifiers changed from: private */
    public LinearLayoutManager layoutManager;
    /* access modifiers changed from: private */
    public ListAdapter listAdapter;
    /* access modifiers changed from: private */
    public RecyclerListView listView;
    private FlickerLoadingView loadingView;
    /* access modifiers changed from: private */
    public boolean removeStickerSet;
    /* access modifiers changed from: private */
    public int rowCount;
    /* access modifiers changed from: private */
    public SearchAdapter searchAdapter;
    /* access modifiers changed from: private */
    public ActionBarMenuItem searchItem;
    /* access modifiers changed from: private */
    public boolean searching;
    /* access modifiers changed from: private */
    public TLRPC.TL_messages_stickerSet selectedStickerSet;
    /* access modifiers changed from: private */
    public int selectedStickerSetIndex = -1;
    /* access modifiers changed from: private */
    public int stickersEndRow;
    /* access modifiers changed from: private */
    public int stickersStartRow;

    public GroupStickersActivity(long id) {
        this.chatId = id;
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        MediaDataController.getInstance(this.currentAccount).checkStickers(0);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.stickersDidLoad);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.chatInfoDidLoad);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.groupStickersDidLoad);
        updateRows();
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.stickersDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.chatInfoDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.groupStickersDidLoad);
        if (this.selectedStickerSet != null || this.removeStickerSet) {
            saveStickerSet();
        }
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("GroupStickers", NUM));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    GroupStickersActivity.this.finishFragment();
                }
            }
        });
        ActionBarMenuItem addItem = this.actionBar.createMenu().addItem(0, NUM);
        this.searchItem = addItem;
        addItem.setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener() {
            public void onSearchExpand() {
            }

            public void onSearchCollapse() {
                if (GroupStickersActivity.this.searching) {
                    GroupStickersActivity.this.searchAdapter.onSearchStickers((String) null);
                    boolean unused = GroupStickersActivity.this.searching = false;
                    GroupStickersActivity.this.listView.setAdapter(GroupStickersActivity.this.listAdapter);
                }
            }

            public void onTextChanged(EditText editText) {
                String text = editText.getText().toString();
                GroupStickersActivity.this.searchAdapter.onSearchStickers(text);
                boolean newSearching = !TextUtils.isEmpty(text);
                if (newSearching != GroupStickersActivity.this.searching) {
                    boolean unused = GroupStickersActivity.this.searching = newSearching;
                    if (GroupStickersActivity.this.listView != null) {
                        GroupStickersActivity.this.listView.setAdapter(GroupStickersActivity.this.searching ? GroupStickersActivity.this.searchAdapter : GroupStickersActivity.this.listAdapter);
                    }
                }
            }
        });
        this.searchItem.setSearchFieldHint(LocaleController.getString(NUM));
        this.listAdapter = new ListAdapter(context);
        this.searchAdapter = new SearchAdapter(context);
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        frameLayout.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
        this.listView = new RecyclerListView(context);
        DefaultItemAnimator defaultItemAnimator = new DefaultItemAnimator();
        defaultItemAnimator.setSupportsChangeAnimations(true);
        this.listView.setItemAnimator(defaultItemAnimator);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        this.layoutManager = linearLayoutManager;
        linearLayoutManager.setOrientation(1);
        this.listView.setLayoutManager(this.layoutManager);
        FrameLayout frameLayout2 = new FrameLayout(context);
        this.emptyFrameView = frameLayout2;
        frameLayout2.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        FlickerLoadingView flickerLoadingView = new FlickerLoadingView(context, getResourceProvider());
        this.loadingView = flickerLoadingView;
        flickerLoadingView.setViewType(19);
        this.loadingView.setIsSingleCell(true);
        this.loadingView.setItemsCount((int) Math.ceil((double) (((float) AndroidUtilities.displaySize.y) / AndroidUtilities.dpf2(58.0f))));
        this.emptyFrameView.addView(this.loadingView, LayoutHelper.createFrame(-1, -1.0f));
        StickerEmptyView stickerEmptyView = new StickerEmptyView(context, this.loadingView, 1);
        this.emptyView = stickerEmptyView;
        VerticalPositionAutoAnimator.attach(stickerEmptyView);
        this.emptyFrameView.addView(this.emptyView);
        frameLayout.addView(this.emptyFrameView);
        this.emptyFrameView.setVisibility(8);
        this.listView.setEmptyView(this.emptyFrameView);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setAdapter(this.listAdapter);
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new GroupStickersActivity$$ExternalSyntheticLambda2(this));
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == 1) {
                    AndroidUtilities.hideKeyboard(GroupStickersActivity.this.getParentActivity().getCurrentFocus());
                }
            }
        });
        return this.fragmentView;
    }

    /* renamed from: lambda$createView$0$org-telegram-ui-GroupStickersActivity  reason: not valid java name */
    public /* synthetic */ void m3594lambda$createView$0$orgtelegramuiGroupStickersActivity(View view, int position) {
        if (getParentActivity() != null) {
            if (this.searching) {
                if (position > this.searchAdapter.searchEntries.size()) {
                    onStickerSetClicked(view, (TLRPC.TL_messages_stickerSet) this.searchAdapter.localSearchEntries.get((position - this.searchAdapter.searchEntries.size()) - 1), false);
                } else if (position != this.searchAdapter.searchEntries.size()) {
                    onStickerSetClicked(view, (TLRPC.TL_messages_stickerSet) this.searchAdapter.searchEntries.get(position), true);
                }
            } else if (position >= this.stickersStartRow && position < this.stickersEndRow) {
                onStickerSetClicked(view, MediaDataController.getInstance(this.currentAccount).getStickerSets(0).get(position - this.stickersStartRow), false);
            }
        }
    }

    private void onStickerSetClicked(View view, final TLRPC.TL_messages_stickerSet stickerSet, boolean remote) {
        TLRPC.InputStickerSet inputStickerSet = null;
        if (remote) {
            TLRPC.TL_inputStickerSetShortName inputStickerSetShortName = new TLRPC.TL_inputStickerSetShortName();
            inputStickerSetShortName.short_name = stickerSet.set.short_name;
            inputStickerSet = inputStickerSetShortName;
        }
        StickersAlert stickersAlert = new StickersAlert((Context) getParentActivity(), (BaseFragment) this, inputStickerSet, !remote ? stickerSet : null, (StickersAlert.StickersAlertDelegate) null);
        final boolean isSelected = ((StickerSetCell) view).isChecked();
        stickersAlert.setCustomButtonDelegate(new StickersAlert.StickersAlertCustomButtonDelegate() {
            public String getCustomButtonTextColorKey() {
                return isSelected ? "dialogTextRed" : "featuredStickers_buttonText";
            }

            public String getCustomButtonRippleColorKey() {
                if (!isSelected) {
                    return "featuredStickers_addButtonPressed";
                }
                return null;
            }

            public String getCustomButtonColorKey() {
                if (!isSelected) {
                    return "featuredStickers_addButton";
                }
                return null;
            }

            public String getCustomButtonText() {
                return LocaleController.getString(isSelected ? NUM : NUM);
            }

            public boolean onCustomButtonPressed() {
                int row = GroupStickersActivity.this.layoutManager.findFirstVisibleItemPosition();
                int top = Integer.MAX_VALUE;
                RecyclerListView.Holder holder = (RecyclerListView.Holder) GroupStickersActivity.this.listView.findViewHolderForAdapterPosition(row);
                if (holder != null) {
                    top = holder.itemView.getTop();
                }
                int prevIndex = GroupStickersActivity.this.selectedStickerSetIndex;
                if (isSelected) {
                    TLRPC.TL_messages_stickerSet unused = GroupStickersActivity.this.selectedStickerSet = null;
                    boolean unused2 = GroupStickersActivity.this.removeStickerSet = true;
                } else {
                    TLRPC.TL_messages_stickerSet unused3 = GroupStickersActivity.this.selectedStickerSet = stickerSet;
                    boolean unused4 = GroupStickersActivity.this.removeStickerSet = false;
                }
                GroupStickersActivity.this.updateSelectedStickerSetIndex();
                if (prevIndex != -1) {
                    boolean found = false;
                    if (!GroupStickersActivity.this.searching) {
                        int i = 0;
                        while (true) {
                            if (i >= GroupStickersActivity.this.listView.getChildCount()) {
                                break;
                            }
                            View ch = GroupStickersActivity.this.listView.getChildAt(i);
                            if (GroupStickersActivity.this.listView.getChildViewHolder(ch).getAdapterPosition() == GroupStickersActivity.this.stickersStartRow + prevIndex) {
                                ((StickerSetCell) ch).setChecked(false, true);
                                found = true;
                                break;
                            }
                            i++;
                        }
                    }
                    if (!found) {
                        GroupStickersActivity.this.listAdapter.notifyItemChanged(prevIndex);
                    }
                }
                if (GroupStickersActivity.this.selectedStickerSetIndex != -1) {
                    boolean found2 = false;
                    if (!GroupStickersActivity.this.searching) {
                        int i2 = 0;
                        while (true) {
                            if (i2 >= GroupStickersActivity.this.listView.getChildCount()) {
                                break;
                            }
                            View ch2 = GroupStickersActivity.this.listView.getChildAt(i2);
                            if (GroupStickersActivity.this.listView.getChildViewHolder(ch2).getAdapterPosition() == GroupStickersActivity.this.stickersStartRow + GroupStickersActivity.this.selectedStickerSetIndex) {
                                ((StickerSetCell) ch2).setChecked(true, true);
                                found2 = true;
                                break;
                            }
                            i2++;
                        }
                    }
                    if (!found2) {
                        GroupStickersActivity.this.listAdapter.notifyItemChanged(GroupStickersActivity.this.selectedStickerSetIndex);
                    }
                }
                if (top != Integer.MAX_VALUE) {
                    GroupStickersActivity.this.layoutManager.scrollToPositionWithOffset(row + 1, top);
                }
                if (GroupStickersActivity.this.searching) {
                    GroupStickersActivity.this.searchItem.setSearchFieldText("", false);
                    GroupStickersActivity.this.actionBar.closeSearchField(true);
                }
                return true;
            }
        });
        stickersAlert.show();
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.stickersDidLoad) {
            if (args[0].intValue() == 0) {
                updateRows();
            }
        } else if (id == NotificationCenter.chatInfoDidLoad) {
            TLRPC.ChatFull chatFull = args[0];
            if (chatFull.id == this.chatId) {
                if (this.info == null && chatFull.stickerset != null) {
                    this.selectedStickerSet = MediaDataController.getInstance(this.currentAccount).getGroupStickerSetById(chatFull.stickerset);
                }
                this.info = chatFull;
                updateRows();
            }
        } else if (id == NotificationCenter.groupStickersDidLoad) {
            long setId = args[0].longValue();
            TLRPC.ChatFull chatFull2 = this.info;
            if (chatFull2 != null && chatFull2.stickerset != null && this.info.stickerset.id == setId) {
                updateRows();
            }
        }
    }

    public void setInfo(TLRPC.ChatFull chatFull) {
        this.info = chatFull;
        if (chatFull != null && chatFull.stickerset != null) {
            this.selectedStickerSet = MediaDataController.getInstance(this.currentAccount).getGroupStickerSetById(this.info.stickerset);
        }
    }

    private void saveStickerSet() {
        TLRPC.TL_messages_stickerSet tL_messages_stickerSet;
        TLRPC.ChatFull chatFull = this.info;
        if (chatFull == null) {
            return;
        }
        if (chatFull.stickerset != null && (tL_messages_stickerSet = this.selectedStickerSet) != null && tL_messages_stickerSet.set.id == this.info.stickerset.id) {
            return;
        }
        if (this.info.stickerset != null || this.selectedStickerSet != null) {
            TLRPC.TL_channels_setStickers req = new TLRPC.TL_channels_setStickers();
            req.channel = MessagesController.getInstance(this.currentAccount).getInputChannel(this.chatId);
            if (this.removeStickerSet) {
                req.stickerset = new TLRPC.TL_inputStickerSetEmpty();
            } else {
                SharedPreferences.Editor edit = MessagesController.getEmojiSettings(this.currentAccount).edit();
                edit.remove("group_hide_stickers_" + this.info.id).apply();
                req.stickerset = new TLRPC.TL_inputStickerSetID();
                req.stickerset.id = this.selectedStickerSet.set.id;
                req.stickerset.access_hash = this.selectedStickerSet.set.access_hash;
            }
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new GroupStickersActivity$$ExternalSyntheticLambda1(this));
        }
    }

    /* renamed from: lambda$saveStickerSet$2$org-telegram-ui-GroupStickersActivity  reason: not valid java name */
    public /* synthetic */ void m3596lambda$saveStickerSet$2$orgtelegramuiGroupStickersActivity(TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new GroupStickersActivity$$ExternalSyntheticLambda0(this, error));
    }

    /* renamed from: lambda$saveStickerSet$1$org-telegram-ui-GroupStickersActivity  reason: not valid java name */
    public /* synthetic */ void m3595lambda$saveStickerSet$1$orgtelegramuiGroupStickersActivity(TLRPC.TL_error error) {
        if (error == null) {
            TLRPC.TL_messages_stickerSet tL_messages_stickerSet = this.selectedStickerSet;
            if (tL_messages_stickerSet == null) {
                this.info.stickerset = null;
            } else {
                this.info.stickerset = tL_messages_stickerSet.set;
                MediaDataController.getInstance(this.currentAccount).putGroupStickerSet(this.selectedStickerSet);
            }
            updateSelectedStickerSetIndex();
            if (this.info.stickerset == null) {
                this.info.flags |= 256;
            } else {
                this.info.flags &= -257;
            }
            MessagesStorage.getInstance(this.currentAccount).updateChatInfo(this.info, false);
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.chatInfoDidLoad, this.info, 0, true, false);
            finishFragment();
        } else if (getParentActivity() != null) {
            Toast.makeText(getParentActivity(), LocaleController.getString("ErrorOccurred", NUM) + "\n" + error.text, 0).show();
        }
    }

    /* access modifiers changed from: private */
    public void updateSelectedStickerSetIndex() {
        long selectedSet;
        ArrayList<TLRPC.TL_messages_stickerSet> stickerSets = MediaDataController.getInstance(this.currentAccount).getStickerSets(0);
        this.selectedStickerSetIndex = -1;
        if (this.removeStickerSet) {
            selectedSet = 0;
        } else {
            TLRPC.TL_messages_stickerSet tL_messages_stickerSet = this.selectedStickerSet;
            if (tL_messages_stickerSet != null) {
                selectedSet = tL_messages_stickerSet.set.id;
            } else {
                TLRPC.ChatFull chatFull = this.info;
                if (chatFull == null || chatFull.stickerset == null) {
                    selectedSet = 0;
                } else {
                    selectedSet = this.info.stickerset.id;
                }
            }
        }
        if (selectedSet != 0) {
            for (int i = 0; i < stickerSets.size(); i++) {
                if (stickerSets.get(i).set.id == selectedSet) {
                    this.selectedStickerSetIndex = i;
                    return;
                }
            }
        }
    }

    private void updateRows() {
        this.rowCount = 0;
        ArrayList<TLRPC.TL_messages_stickerSet> stickerSets = MediaDataController.getInstance(this.currentAccount).getStickerSets(0);
        if (!stickerSets.isEmpty()) {
            int i = this.rowCount;
            int i2 = i + 1;
            this.rowCount = i2;
            this.headerRow = i;
            this.stickersStartRow = i2;
            this.stickersEndRow = i2 + stickerSets.size();
            this.rowCount += stickerSets.size();
        } else {
            this.headerRow = -1;
            this.stickersStartRow = -1;
            this.stickersEndRow = -1;
        }
        int i3 = this.rowCount;
        this.rowCount = i3 + 1;
        this.infoRow = i3;
        updateSelectedStickerSetIndex();
        ListAdapter listAdapter2 = this.listAdapter;
        if (listAdapter2 != null) {
            listAdapter2.notifyDataSetChanged();
        }
    }

    private class SearchAdapter extends RecyclerListView.SelectionAdapter {
        private static final int TYPE_MY_STICKERS_HEADER = 1;
        private static final int TYPE_STICKER_SET = 0;
        private Runnable lastCallback;
        private String lastQuery;
        /* access modifiers changed from: private */
        public List<TLRPC.TL_messages_stickerSet> localSearchEntries = new ArrayList();
        private Context mContext;
        private int reqId;
        /* access modifiers changed from: private */
        public List<TLRPC.TL_messages_stickerSet> searchEntries = new ArrayList();

        public SearchAdapter(Context context) {
            this.mContext = context;
            setHasStableIds(true);
        }

        public long getItemId(int position) {
            if (getItemViewType(position) != 0) {
                return -1;
            }
            return (position > this.searchEntries.size() ? this.localSearchEntries : this.searchEntries).get(position > this.searchEntries.size() ? (position - this.searchEntries.size()) - 1 : position).set.id;
        }

        /* access modifiers changed from: private */
        public void onSearchStickers(String query) {
            if (this.reqId != 0) {
                GroupStickersActivity.this.getConnectionsManager().cancelRequest(this.reqId, true);
                this.reqId = 0;
            }
            Runnable runnable = this.lastCallback;
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
                this.lastCallback = null;
            }
            this.lastQuery = null;
            int count = getItemCount();
            if (count > 0) {
                this.searchEntries.clear();
                this.localSearchEntries.clear();
                notifyItemRangeRemoved(0, count);
            }
            if (TextUtils.isEmpty(query)) {
                GroupStickersActivity.this.emptyView.setVisibility(8);
                GroupStickersActivity.this.emptyView.showProgress(false, true);
                return;
            }
            if (GroupStickersActivity.this.emptyView.getVisibility() != 0) {
                GroupStickersActivity.this.emptyView.setVisibility(0);
                GroupStickersActivity.this.emptyView.showProgress(true, false);
            } else {
                GroupStickersActivity.this.emptyView.showProgress(true, true);
            }
            GroupStickersActivity$SearchAdapter$$ExternalSyntheticLambda0 groupStickersActivity$SearchAdapter$$ExternalSyntheticLambda0 = new GroupStickersActivity$SearchAdapter$$ExternalSyntheticLambda0(this, query);
            this.lastCallback = groupStickersActivity$SearchAdapter$$ExternalSyntheticLambda0;
            AndroidUtilities.runOnUIThread(groupStickersActivity$SearchAdapter$$ExternalSyntheticLambda0, 300);
        }

        /* renamed from: lambda$onSearchStickers$2$org-telegram-ui-GroupStickersActivity$SearchAdapter  reason: not valid java name */
        public /* synthetic */ void m3599x6d5579f5(String query) {
            this.lastQuery = query;
            TLRPC.TL_messages_searchStickerSets searchStickerSets = new TLRPC.TL_messages_searchStickerSets();
            searchStickerSets.q = query;
            this.reqId = GroupStickersActivity.this.getConnectionsManager().sendRequest(searchStickerSets, new GroupStickersActivity$SearchAdapter$$ExternalSyntheticLambda2(this, searchStickerSets, query), 66);
        }

        /* renamed from: lambda$onSearchStickers$1$org-telegram-ui-GroupStickersActivity$SearchAdapter  reason: not valid java name */
        public /* synthetic */ void m3598xe01aCLASSNAME(TLRPC.TL_messages_searchStickerSets searchStickerSets, String query, TLObject response, TLRPC.TL_error error) {
            if (ColorUtils$$ExternalSyntheticBackport0.m(this.lastQuery, searchStickerSets.q) && (response instanceof TLRPC.TL_messages_foundStickerSets)) {
                List<TLRPC.TL_messages_stickerSet> newSearchEntries = new ArrayList<>();
                Iterator<TLRPC.StickerSetCovered> it = ((TLRPC.TL_messages_foundStickerSets) response).sets.iterator();
                while (it.hasNext()) {
                    TLRPC.StickerSetCovered stickerSetCovered = it.next();
                    TLRPC.TL_messages_stickerSet set = new TLRPC.TL_messages_stickerSet();
                    set.set = stickerSetCovered.set;
                    set.documents = stickerSetCovered.covers;
                    newSearchEntries.add(set);
                }
                String lowQuery = query.toLowerCase(Locale.ROOT).trim();
                List<TLRPC.TL_messages_stickerSet> newLocalEntries = new ArrayList<>();
                Iterator<TLRPC.TL_messages_stickerSet> it2 = MediaDataController.getInstance(GroupStickersActivity.this.currentAccount).getStickerSets(0).iterator();
                while (it2.hasNext()) {
                    TLRPC.TL_messages_stickerSet localSet = it2.next();
                    if (localSet.set.short_name.toLowerCase(Locale.ROOT).contains(lowQuery) || localSet.set.title.toLowerCase(Locale.ROOT).contains(lowQuery)) {
                        newLocalEntries.add(localSet);
                    }
                }
                AndroidUtilities.runOnUIThread(new GroupStickersActivity$SearchAdapter$$ExternalSyntheticLambda1(this, newSearchEntries, newLocalEntries, query));
            }
        }

        /* renamed from: lambda$onSearchStickers$0$org-telegram-ui-GroupStickersActivity$SearchAdapter  reason: not valid java name */
        public /* synthetic */ void m3597x52e016f3(List newSearchEntries, List newLocalEntries, String query) {
            this.searchEntries = newSearchEntries;
            this.localSearchEntries = newLocalEntries;
            notifyDataSetChanged();
            GroupStickersActivity.this.emptyView.title.setVisibility(8);
            GroupStickersActivity.this.emptyView.subtitle.setText(LocaleController.formatString(NUM, query));
            GroupStickersActivity.this.emptyView.showProgress(false, true);
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v1, resolved type: org.telegram.ui.Cells.StickerSetCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v2, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v6, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX WARNING: type inference failed for: r0v0, types: [android.view.View] */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public androidx.recyclerview.widget.RecyclerView.ViewHolder onCreateViewHolder(android.view.ViewGroup r11, int r12) {
            /*
                r10 = this;
                switch(r12) {
                    case 0: goto L_0x0034;
                    default: goto L_0x0003;
                }
            L_0x0003:
                org.telegram.ui.Cells.HeaderCell r0 = new org.telegram.ui.Cells.HeaderCell
                android.content.Context r3 = r10.mContext
                r5 = 21
                r6 = 0
                r7 = 0
                r8 = 0
                org.telegram.ui.GroupStickersActivity r1 = org.telegram.ui.GroupStickersActivity.this
                org.telegram.ui.ActionBar.Theme$ResourcesProvider r9 = r1.getResourceProvider()
                java.lang.String r4 = "windowBackgroundWhiteGrayText4"
                r2 = r0
                r2.<init>(r3, r4, r5, r6, r7, r8, r9)
                android.content.Context r1 = r10.mContext
                r2 = 2131165436(0x7var_fc, float:1.794509E38)
                java.lang.String r3 = "windowBackgroundGrayShadow"
                android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r1, (int) r2, (java.lang.String) r3)
                r0.setBackground(r1)
                r1 = r0
                org.telegram.ui.Cells.HeaderCell r1 = (org.telegram.ui.Cells.HeaderCell) r1
                r2 = 2131625114(0x7f0e049a, float:1.8877427E38)
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString((int) r2)
                r1.setText(r2)
                goto L_0x0046
            L_0x0034:
                org.telegram.ui.Cells.StickerSetCell r0 = new org.telegram.ui.Cells.StickerSetCell
                android.content.Context r1 = r10.mContext
                r2 = 3
                r0.<init>(r1, r2)
                java.lang.String r1 = "windowBackgroundWhite"
                int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
                r0.setBackgroundColor(r1)
            L_0x0046:
                androidx.recyclerview.widget.RecyclerView$LayoutParams r1 = new androidx.recyclerview.widget.RecyclerView$LayoutParams
                r2 = -1
                r3 = -2
                r1.<init>((int) r2, (int) r3)
                r0.setLayoutParams(r1)
                org.telegram.ui.Components.RecyclerListView$Holder r1 = new org.telegram.ui.Components.RecyclerListView$Holder
                r1.<init>(r0)
                return r1
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.GroupStickersActivity.SearchAdapter.onCreateViewHolder(android.view.ViewGroup, int):androidx.recyclerview.widget.RecyclerView$ViewHolder");
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            long id;
            switch (getItemViewType(position)) {
                case 0:
                    boolean z = true;
                    boolean local = position > this.searchEntries.size();
                    List<TLRPC.TL_messages_stickerSet> arrayList = local ? this.localSearchEntries : this.searchEntries;
                    int row = local ? (position - this.searchEntries.size()) - 1 : position;
                    StickerSetCell cell = (StickerSetCell) holder.itemView;
                    TLRPC.TL_messages_stickerSet set = arrayList.get(row);
                    cell.setStickersSet(set, row != arrayList.size() - 1, !local);
                    String str = this.lastQuery;
                    cell.setSearchQuery(set, str != null ? str.toLowerCase(Locale.ROOT) : "", GroupStickersActivity.this.getResourceProvider());
                    if (GroupStickersActivity.this.selectedStickerSet != null) {
                        id = GroupStickersActivity.this.selectedStickerSet.set.id;
                    } else if (GroupStickersActivity.this.info == null || GroupStickersActivity.this.info.stickerset == null) {
                        id = 0;
                    } else {
                        id = GroupStickersActivity.this.info.stickerset.id;
                    }
                    if (set.set.id != id) {
                        z = false;
                    }
                    cell.setChecked(z, false);
                    return;
                default:
                    return;
            }
        }

        public int getItemViewType(int position) {
            return this.searchEntries.size() == position ? 1 : 0;
        }

        public int getItemCount() {
            return this.searchEntries.size() + this.localSearchEntries.size() + (this.localSearchEntries.isEmpty() ^ true ? 1 : 0);
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return getItemViewType(holder.getAdapterPosition()) == 0;
        }
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private static final int TYPE_CHOOSE_HEADER = 4;
        private static final int TYPE_INFO = 1;
        private static final int TYPE_STICKER_SET = 0;
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public int getItemCount() {
            return GroupStickersActivity.this.rowCount;
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            long id;
            switch (holder.getItemViewType()) {
                case 0:
                    ArrayList<TLRPC.TL_messages_stickerSet> arrayList = MediaDataController.getInstance(GroupStickersActivity.this.currentAccount).getStickerSets(0);
                    int row = position - GroupStickersActivity.this.stickersStartRow;
                    StickerSetCell cell = (StickerSetCell) holder.itemView;
                    TLRPC.TL_messages_stickerSet set = arrayList.get(row);
                    boolean z = true;
                    cell.setStickersSet(arrayList.get(row), row != arrayList.size() - 1);
                    if (GroupStickersActivity.this.selectedStickerSet != null) {
                        id = GroupStickersActivity.this.selectedStickerSet.set.id;
                    } else if (GroupStickersActivity.this.info == null || GroupStickersActivity.this.info.stickerset == null) {
                        id = 0;
                    } else {
                        id = GroupStickersActivity.this.info.stickerset.id;
                    }
                    if (set.set.id != id) {
                        z = false;
                    }
                    cell.setChecked(z, false);
                    return;
                case 1:
                    if (position == GroupStickersActivity.this.infoRow) {
                        String text = LocaleController.getString("ChooseStickerSetMy", NUM);
                        int index = text.indexOf("@stickers");
                        if (index != -1) {
                            try {
                                SpannableStringBuilder stringBuilder = new SpannableStringBuilder(text);
                                stringBuilder.setSpan(new URLSpanNoUnderline("@stickers") {
                                    public void onClick(View widget) {
                                        MessagesController.getInstance(GroupStickersActivity.this.currentAccount).openByUserName("stickers", GroupStickersActivity.this, 1);
                                    }
                                }, index, "@stickers".length() + index, 18);
                                ((TextInfoPrivacyCell) holder.itemView).setText(stringBuilder);
                                return;
                            } catch (Exception e) {
                                FileLog.e((Throwable) e);
                                ((TextInfoPrivacyCell) holder.itemView).setText(text);
                                return;
                            }
                        } else {
                            ((TextInfoPrivacyCell) holder.itemView).setText(text);
                            return;
                        }
                    } else {
                        return;
                    }
                case 4:
                    ((HeaderCell) holder.itemView).setText(LocaleController.getString(NUM));
                    return;
                default:
                    return;
            }
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return holder.getItemViewType() == 0;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    View view2 = new StickerSetCell(this.mContext, 3);
                    view2.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    view = view2;
                    break;
                case 1:
                    view = new TextInfoPrivacyCell(this.mContext);
                    view.setBackground(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                    break;
                default:
                    View view3 = new HeaderCell(this.mContext);
                    view3.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    view = view3;
                    break;
            }
            view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(view);
        }

        public int getItemViewType(int i) {
            if (i >= GroupStickersActivity.this.stickersStartRow && i < GroupStickersActivity.this.stickersEndRow) {
                return 0;
            }
            if (i == GroupStickersActivity.this.headerRow) {
                return 4;
            }
            if (i == GroupStickersActivity.this.infoRow) {
                return 1;
            }
            return 0;
        }
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{StickerSetCell.class, TextSettingsCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        themeDescriptions.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultIcon"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultTitle"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSelector"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "divider"));
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
        return themeDescriptions;
    }
}
