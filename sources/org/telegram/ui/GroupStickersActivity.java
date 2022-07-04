package org.telegram.ui;

import android.annotation.SuppressLint;
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
import androidx.core.util.ObjectsCompat$$ExternalSyntheticBackport0;
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
import org.telegram.tgnet.TLRPC$ChatFull;
import org.telegram.tgnet.TLRPC$InputStickerSet;
import org.telegram.tgnet.TLRPC$StickerSet;
import org.telegram.tgnet.TLRPC$StickerSetCovered;
import org.telegram.tgnet.TLRPC$TL_channels_setStickers;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_inputStickerSetEmpty;
import org.telegram.tgnet.TLRPC$TL_inputStickerSetID;
import org.telegram.tgnet.TLRPC$TL_inputStickerSetShortName;
import org.telegram.tgnet.TLRPC$TL_messages_foundStickerSets;
import org.telegram.tgnet.TLRPC$TL_messages_searchStickerSets;
import org.telegram.tgnet.TLRPC$TL_messages_stickerSet;
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
    public TLRPC$ChatFull info;
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
    public TLRPC$TL_messages_stickerSet selectedStickerSet;
    /* access modifiers changed from: private */
    public int selectedStickerSetIndex = -1;
    /* access modifiers changed from: private */
    public int stickersEndRow;
    /* access modifiers changed from: private */
    public int stickersStartRow;

    public GroupStickersActivity(long j) {
        this.chatId = j;
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
            public void onItemClick(int i) {
                if (i == -1) {
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
                String obj = editText.getText().toString();
                GroupStickersActivity.this.searchAdapter.onSearchStickers(obj);
                boolean z = !TextUtils.isEmpty(obj);
                if (z != GroupStickersActivity.this.searching) {
                    boolean unused = GroupStickersActivity.this.searching = z;
                    if (GroupStickersActivity.this.listView != null) {
                        GroupStickersActivity.this.listView.setAdapter(GroupStickersActivity.this.searching ? GroupStickersActivity.this.searchAdapter : GroupStickersActivity.this.listAdapter);
                    }
                }
            }
        });
        this.searchItem.setSearchFieldHint(LocaleController.getString(NUM));
        this.listAdapter = new ListAdapter(context);
        this.searchAdapter = new SearchAdapter(context);
        FrameLayout frameLayout = new FrameLayout(context);
        this.fragmentView = frameLayout;
        FrameLayout frameLayout2 = frameLayout;
        frameLayout2.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
        this.listView = new RecyclerListView(context);
        DefaultItemAnimator defaultItemAnimator = new DefaultItemAnimator();
        defaultItemAnimator.setSupportsChangeAnimations(true);
        this.listView.setItemAnimator(defaultItemAnimator);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        this.layoutManager = linearLayoutManager;
        linearLayoutManager.setOrientation(1);
        this.listView.setLayoutManager(this.layoutManager);
        FrameLayout frameLayout3 = new FrameLayout(context);
        this.emptyFrameView = frameLayout3;
        frameLayout3.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
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
        frameLayout2.addView(this.emptyFrameView);
        this.emptyFrameView.setVisibility(8);
        this.listView.setEmptyView(this.emptyFrameView);
        frameLayout2.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setAdapter(this.listAdapter);
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new GroupStickersActivity$$ExternalSyntheticLambda2(this));
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrollStateChanged(RecyclerView recyclerView, int i) {
                if (i == 1) {
                    AndroidUtilities.hideKeyboard(GroupStickersActivity.this.getParentActivity().getCurrentFocus());
                }
            }
        });
        return this.fragmentView;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$0(View view, int i) {
        if (getParentActivity() != null) {
            if (this.searching) {
                if (i > this.searchAdapter.searchEntries.size()) {
                    onStickerSetClicked(view, (TLRPC$TL_messages_stickerSet) this.searchAdapter.localSearchEntries.get((i - this.searchAdapter.searchEntries.size()) - 1), false);
                } else if (i != this.searchAdapter.searchEntries.size()) {
                    onStickerSetClicked(view, (TLRPC$TL_messages_stickerSet) this.searchAdapter.searchEntries.get(i), true);
                }
            } else if (i >= this.stickersStartRow && i < this.stickersEndRow) {
                onStickerSetClicked(view, MediaDataController.getInstance(this.currentAccount).getStickerSets(0).get(i - this.stickersStartRow), false);
            }
        }
    }

    private void onStickerSetClicked(View view, final TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet, boolean z) {
        TLRPC$TL_inputStickerSetShortName tLRPC$TL_inputStickerSetShortName;
        if (z) {
            TLRPC$TL_inputStickerSetShortName tLRPC$TL_inputStickerSetShortName2 = new TLRPC$TL_inputStickerSetShortName();
            tLRPC$TL_inputStickerSetShortName2.short_name = tLRPC$TL_messages_stickerSet.set.short_name;
            tLRPC$TL_inputStickerSetShortName = tLRPC$TL_inputStickerSetShortName2;
        } else {
            tLRPC$TL_inputStickerSetShortName = null;
        }
        StickersAlert stickersAlert = new StickersAlert((Context) getParentActivity(), (BaseFragment) this, (TLRPC$InputStickerSet) tLRPC$TL_inputStickerSetShortName, !z ? tLRPC$TL_messages_stickerSet : null, (StickersAlert.StickersAlertDelegate) null);
        final boolean isChecked = ((StickerSetCell) view).isChecked();
        stickersAlert.setCustomButtonDelegate(new StickersAlert.StickersAlertCustomButtonDelegate() {
            public String getCustomButtonTextColorKey() {
                return isChecked ? "dialogTextRed" : "featuredStickers_buttonText";
            }

            public String getCustomButtonRippleColorKey() {
                if (!isChecked) {
                    return "featuredStickers_addButtonPressed";
                }
                return null;
            }

            public String getCustomButtonColorKey() {
                if (!isChecked) {
                    return "featuredStickers_addButton";
                }
                return null;
            }

            public String getCustomButtonText() {
                return LocaleController.getString(isChecked ? NUM : NUM);
            }

            /* JADX WARNING: Removed duplicated region for block: B:21:0x0094  */
            /* JADX WARNING: Removed duplicated region for block: B:35:0x00ee  */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public boolean onCustomButtonPressed() {
                /*
                    r11 = this;
                    org.telegram.ui.GroupStickersActivity r0 = org.telegram.ui.GroupStickersActivity.this
                    androidx.recyclerview.widget.LinearLayoutManager r0 = r0.layoutManager
                    int r0 = r0.findFirstVisibleItemPosition()
                    org.telegram.ui.GroupStickersActivity r1 = org.telegram.ui.GroupStickersActivity.this
                    org.telegram.ui.Components.RecyclerListView r1 = r1.listView
                    androidx.recyclerview.widget.RecyclerView$ViewHolder r1 = r1.findViewHolderForAdapterPosition(r0)
                    org.telegram.ui.Components.RecyclerListView$Holder r1 = (org.telegram.ui.Components.RecyclerListView.Holder) r1
                    r2 = 2147483647(0x7fffffff, float:NaN)
                    if (r1 == 0) goto L_0x0022
                    android.view.View r1 = r1.itemView
                    int r1 = r1.getTop()
                    goto L_0x0025
                L_0x0022:
                    r1 = 2147483647(0x7fffffff, float:NaN)
                L_0x0025:
                    org.telegram.ui.GroupStickersActivity r3 = org.telegram.ui.GroupStickersActivity.this
                    int r3 = r3.selectedStickerSetIndex
                    boolean r4 = r10
                    r5 = 0
                    r6 = 1
                    if (r4 == 0) goto L_0x003d
                    org.telegram.ui.GroupStickersActivity r4 = org.telegram.ui.GroupStickersActivity.this
                    r7 = 0
                    org.telegram.tgnet.TLRPC$TL_messages_stickerSet unused = r4.selectedStickerSet = r7
                    org.telegram.ui.GroupStickersActivity r4 = org.telegram.ui.GroupStickersActivity.this
                    boolean unused = r4.removeStickerSet = r6
                    goto L_0x0049
                L_0x003d:
                    org.telegram.ui.GroupStickersActivity r4 = org.telegram.ui.GroupStickersActivity.this
                    org.telegram.tgnet.TLRPC$TL_messages_stickerSet r7 = r11
                    org.telegram.tgnet.TLRPC$TL_messages_stickerSet unused = r4.selectedStickerSet = r7
                    org.telegram.ui.GroupStickersActivity r4 = org.telegram.ui.GroupStickersActivity.this
                    boolean unused = r4.removeStickerSet = r5
                L_0x0049:
                    org.telegram.ui.GroupStickersActivity r4 = org.telegram.ui.GroupStickersActivity.this
                    r4.updateSelectedStickerSetIndex()
                    r4 = -1
                    if (r3 == r4) goto L_0x009d
                    org.telegram.ui.GroupStickersActivity r7 = org.telegram.ui.GroupStickersActivity.this
                    boolean r7 = r7.searching
                    if (r7 != 0) goto L_0x0091
                    r7 = 0
                L_0x005a:
                    org.telegram.ui.GroupStickersActivity r8 = org.telegram.ui.GroupStickersActivity.this
                    org.telegram.ui.Components.RecyclerListView r8 = r8.listView
                    int r8 = r8.getChildCount()
                    if (r7 >= r8) goto L_0x0091
                    org.telegram.ui.GroupStickersActivity r8 = org.telegram.ui.GroupStickersActivity.this
                    org.telegram.ui.Components.RecyclerListView r8 = r8.listView
                    android.view.View r8 = r8.getChildAt(r7)
                    org.telegram.ui.GroupStickersActivity r9 = org.telegram.ui.GroupStickersActivity.this
                    org.telegram.ui.Components.RecyclerListView r9 = r9.listView
                    androidx.recyclerview.widget.RecyclerView$ViewHolder r9 = r9.getChildViewHolder(r8)
                    int r9 = r9.getAdapterPosition()
                    org.telegram.ui.GroupStickersActivity r10 = org.telegram.ui.GroupStickersActivity.this
                    int r10 = r10.stickersStartRow
                    int r10 = r10 + r3
                    if (r9 != r10) goto L_0x008e
                    org.telegram.ui.Cells.StickerSetCell r8 = (org.telegram.ui.Cells.StickerSetCell) r8
                    r8.setChecked(r5, r6)
                    r7 = 1
                    goto L_0x0092
                L_0x008e:
                    int r7 = r7 + 1
                    goto L_0x005a
                L_0x0091:
                    r7 = 0
                L_0x0092:
                    if (r7 != 0) goto L_0x009d
                    org.telegram.ui.GroupStickersActivity r7 = org.telegram.ui.GroupStickersActivity.this
                    org.telegram.ui.GroupStickersActivity$ListAdapter r7 = r7.listAdapter
                    r7.notifyItemChanged(r3)
                L_0x009d:
                    org.telegram.ui.GroupStickersActivity r3 = org.telegram.ui.GroupStickersActivity.this
                    int r3 = r3.selectedStickerSetIndex
                    if (r3 == r4) goto L_0x00fd
                    org.telegram.ui.GroupStickersActivity r3 = org.telegram.ui.GroupStickersActivity.this
                    boolean r3 = r3.searching
                    if (r3 != 0) goto L_0x00eb
                    r3 = 0
                L_0x00ae:
                    org.telegram.ui.GroupStickersActivity r4 = org.telegram.ui.GroupStickersActivity.this
                    org.telegram.ui.Components.RecyclerListView r4 = r4.listView
                    int r4 = r4.getChildCount()
                    if (r3 >= r4) goto L_0x00eb
                    org.telegram.ui.GroupStickersActivity r4 = org.telegram.ui.GroupStickersActivity.this
                    org.telegram.ui.Components.RecyclerListView r4 = r4.listView
                    android.view.View r4 = r4.getChildAt(r3)
                    org.telegram.ui.GroupStickersActivity r7 = org.telegram.ui.GroupStickersActivity.this
                    org.telegram.ui.Components.RecyclerListView r7 = r7.listView
                    androidx.recyclerview.widget.RecyclerView$ViewHolder r7 = r7.getChildViewHolder(r4)
                    int r7 = r7.getAdapterPosition()
                    org.telegram.ui.GroupStickersActivity r8 = org.telegram.ui.GroupStickersActivity.this
                    int r8 = r8.stickersStartRow
                    org.telegram.ui.GroupStickersActivity r9 = org.telegram.ui.GroupStickersActivity.this
                    int r9 = r9.selectedStickerSetIndex
                    int r8 = r8 + r9
                    if (r7 != r8) goto L_0x00e8
                    org.telegram.ui.Cells.StickerSetCell r4 = (org.telegram.ui.Cells.StickerSetCell) r4
                    r4.setChecked(r6, r6)
                    r3 = 1
                    goto L_0x00ec
                L_0x00e8:
                    int r3 = r3 + 1
                    goto L_0x00ae
                L_0x00eb:
                    r3 = 0
                L_0x00ec:
                    if (r3 != 0) goto L_0x00fd
                    org.telegram.ui.GroupStickersActivity r3 = org.telegram.ui.GroupStickersActivity.this
                    org.telegram.ui.GroupStickersActivity$ListAdapter r3 = r3.listAdapter
                    org.telegram.ui.GroupStickersActivity r4 = org.telegram.ui.GroupStickersActivity.this
                    int r4 = r4.selectedStickerSetIndex
                    r3.notifyItemChanged(r4)
                L_0x00fd:
                    if (r1 == r2) goto L_0x0109
                    org.telegram.ui.GroupStickersActivity r2 = org.telegram.ui.GroupStickersActivity.this
                    androidx.recyclerview.widget.LinearLayoutManager r2 = r2.layoutManager
                    int r0 = r0 + r6
                    r2.scrollToPositionWithOffset(r0, r1)
                L_0x0109:
                    org.telegram.ui.GroupStickersActivity r0 = org.telegram.ui.GroupStickersActivity.this
                    boolean r0 = r0.searching
                    if (r0 == 0) goto L_0x0125
                    org.telegram.ui.GroupStickersActivity r0 = org.telegram.ui.GroupStickersActivity.this
                    org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r0.searchItem
                    java.lang.String r1 = ""
                    r0.setSearchFieldText(r1, r5)
                    org.telegram.ui.GroupStickersActivity r0 = org.telegram.ui.GroupStickersActivity.this
                    org.telegram.ui.ActionBar.ActionBar r0 = r0.actionBar
                    r0.closeSearchField(r6)
                L_0x0125:
                    return r6
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.GroupStickersActivity.AnonymousClass4.onCustomButtonPressed():boolean");
            }
        });
        stickersAlert.show();
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        TLRPC$StickerSet tLRPC$StickerSet;
        if (i == NotificationCenter.stickersDidLoad) {
            if (objArr[0].intValue() == 0) {
                updateRows();
            }
        } else if (i == NotificationCenter.chatInfoDidLoad) {
            TLRPC$ChatFull tLRPC$ChatFull = objArr[0];
            if (tLRPC$ChatFull.id == this.chatId) {
                if (this.info == null && tLRPC$ChatFull.stickerset != null) {
                    this.selectedStickerSet = MediaDataController.getInstance(this.currentAccount).getGroupStickerSetById(tLRPC$ChatFull.stickerset);
                }
                this.info = tLRPC$ChatFull;
                updateRows();
            }
        } else if (i == NotificationCenter.groupStickersDidLoad) {
            long longValue = objArr[0].longValue();
            TLRPC$ChatFull tLRPC$ChatFull2 = this.info;
            if (tLRPC$ChatFull2 != null && (tLRPC$StickerSet = tLRPC$ChatFull2.stickerset) != null && tLRPC$StickerSet.id == longValue) {
                updateRows();
            }
        }
    }

    public void setInfo(TLRPC$ChatFull tLRPC$ChatFull) {
        this.info = tLRPC$ChatFull;
        if (tLRPC$ChatFull != null && tLRPC$ChatFull.stickerset != null) {
            this.selectedStickerSet = MediaDataController.getInstance(this.currentAccount).getGroupStickerSetById(this.info.stickerset);
        }
    }

    private void saveStickerSet() {
        TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet;
        TLRPC$ChatFull tLRPC$ChatFull = this.info;
        if (tLRPC$ChatFull != null) {
            TLRPC$StickerSet tLRPC$StickerSet = tLRPC$ChatFull.stickerset;
            if (tLRPC$StickerSet != null && (tLRPC$TL_messages_stickerSet = this.selectedStickerSet) != null && tLRPC$TL_messages_stickerSet.set.id == tLRPC$StickerSet.id) {
                return;
            }
            if (tLRPC$StickerSet != null || this.selectedStickerSet != null) {
                TLRPC$TL_channels_setStickers tLRPC$TL_channels_setStickers = new TLRPC$TL_channels_setStickers();
                tLRPC$TL_channels_setStickers.channel = MessagesController.getInstance(this.currentAccount).getInputChannel(this.chatId);
                if (this.removeStickerSet) {
                    tLRPC$TL_channels_setStickers.stickerset = new TLRPC$TL_inputStickerSetEmpty();
                } else {
                    SharedPreferences.Editor edit = MessagesController.getEmojiSettings(this.currentAccount).edit();
                    edit.remove("group_hide_stickers_" + this.info.id).apply();
                    TLRPC$TL_inputStickerSetID tLRPC$TL_inputStickerSetID = new TLRPC$TL_inputStickerSetID();
                    tLRPC$TL_channels_setStickers.stickerset = tLRPC$TL_inputStickerSetID;
                    TLRPC$StickerSet tLRPC$StickerSet2 = this.selectedStickerSet.set;
                    tLRPC$TL_inputStickerSetID.id = tLRPC$StickerSet2.id;
                    tLRPC$TL_inputStickerSetID.access_hash = tLRPC$StickerSet2.access_hash;
                }
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_channels_setStickers, new GroupStickersActivity$$ExternalSyntheticLambda1(this));
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$saveStickerSet$2(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new GroupStickersActivity$$ExternalSyntheticLambda0(this, tLRPC$TL_error));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$saveStickerSet$1(TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error == null) {
            TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet = this.selectedStickerSet;
            if (tLRPC$TL_messages_stickerSet == null) {
                this.info.stickerset = null;
            } else {
                this.info.stickerset = tLRPC$TL_messages_stickerSet.set;
                MediaDataController.getInstance(this.currentAccount).putGroupStickerSet(this.selectedStickerSet);
            }
            updateSelectedStickerSetIndex();
            TLRPC$ChatFull tLRPC$ChatFull = this.info;
            if (tLRPC$ChatFull.stickerset == null) {
                tLRPC$ChatFull.flags |= 256;
            } else {
                tLRPC$ChatFull.flags &= -257;
            }
            MessagesStorage.getInstance(this.currentAccount).updateChatInfo(this.info, false);
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.chatInfoDidLoad, this.info, 0, Boolean.TRUE, Boolean.FALSE);
            finishFragment();
            return;
        }
        Toast.makeText(getParentActivity(), LocaleController.getString("ErrorOccurred", NUM) + "\n" + tLRPC$TL_error.text, 0).show();
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:13:0x002d A[LOOP:0: B:13:0x002d->B:18:0x0044, LOOP_START, PHI: r1 
      PHI: (r1v1 int) = (r1v0 int), (r1v2 int) binds: [B:12:0x002b, B:18:0x0044] A[DONT_GENERATE, DONT_INLINE]] */
    /* JADX WARNING: Removed duplicated region for block: B:21:? A[ORIG_RETURN, RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void updateSelectedStickerSetIndex() {
        /*
            r7 = this;
            int r0 = r7.currentAccount
            org.telegram.messenger.MediaDataController r0 = org.telegram.messenger.MediaDataController.getInstance(r0)
            r1 = 0
            java.util.ArrayList r0 = r0.getStickerSets(r1)
            r2 = -1
            r7.selectedStickerSetIndex = r2
            boolean r2 = r7.removeStickerSet
            r3 = 0
            if (r2 == 0) goto L_0x0016
        L_0x0014:
            r5 = r3
            goto L_0x0029
        L_0x0016:
            org.telegram.tgnet.TLRPC$TL_messages_stickerSet r2 = r7.selectedStickerSet
            if (r2 == 0) goto L_0x001f
            org.telegram.tgnet.TLRPC$StickerSet r2 = r2.set
            long r5 = r2.id
            goto L_0x0029
        L_0x001f:
            org.telegram.tgnet.TLRPC$ChatFull r2 = r7.info
            if (r2 == 0) goto L_0x0014
            org.telegram.tgnet.TLRPC$StickerSet r2 = r2.stickerset
            if (r2 == 0) goto L_0x0014
            long r5 = r2.id
        L_0x0029:
            int r2 = (r5 > r3 ? 1 : (r5 == r3 ? 0 : -1))
            if (r2 == 0) goto L_0x0047
        L_0x002d:
            int r2 = r0.size()
            if (r1 >= r2) goto L_0x0047
            java.lang.Object r2 = r0.get(r1)
            org.telegram.tgnet.TLRPC$TL_messages_stickerSet r2 = (org.telegram.tgnet.TLRPC$TL_messages_stickerSet) r2
            org.telegram.tgnet.TLRPC$StickerSet r2 = r2.set
            long r2 = r2.id
            int r4 = (r2 > r5 ? 1 : (r2 == r5 ? 0 : -1))
            if (r4 != 0) goto L_0x0044
            r7.selectedStickerSetIndex = r1
            goto L_0x0047
        L_0x0044:
            int r1 = r1 + 1
            goto L_0x002d
        L_0x0047:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.GroupStickersActivity.updateSelectedStickerSetIndex():void");
    }

    @SuppressLint({"NotifyDataSetChanged"})
    private void updateRows() {
        this.rowCount = 0;
        ArrayList<TLRPC$TL_messages_stickerSet> stickerSets = MediaDataController.getInstance(this.currentAccount).getStickerSets(0);
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
        private Runnable lastCallback;
        private String lastQuery;
        /* access modifiers changed from: private */
        public List<TLRPC$TL_messages_stickerSet> localSearchEntries = new ArrayList();
        private Context mContext;
        private int reqId;
        /* access modifiers changed from: private */
        public List<TLRPC$TL_messages_stickerSet> searchEntries = new ArrayList();

        public SearchAdapter(Context context) {
            this.mContext = context;
            setHasStableIds(true);
        }

        public long getItemId(int i) {
            if (getItemViewType(i) != 0) {
                return -1;
            }
            List<TLRPC$TL_messages_stickerSet> list = i > this.searchEntries.size() ? this.localSearchEntries : this.searchEntries;
            if (i > this.searchEntries.size()) {
                i = (i - this.searchEntries.size()) - 1;
            }
            return list.get(i).set.id;
        }

        /* access modifiers changed from: private */
        @SuppressLint({"NotifyDataSetChanged"})
        public void onSearchStickers(String str) {
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
            int itemCount = getItemCount();
            if (itemCount > 0) {
                this.searchEntries.clear();
                this.localSearchEntries.clear();
                notifyItemRangeRemoved(0, itemCount);
            }
            if (TextUtils.isEmpty(str)) {
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
            GroupStickersActivity$SearchAdapter$$ExternalSyntheticLambda0 groupStickersActivity$SearchAdapter$$ExternalSyntheticLambda0 = new GroupStickersActivity$SearchAdapter$$ExternalSyntheticLambda0(this, str);
            this.lastCallback = groupStickersActivity$SearchAdapter$$ExternalSyntheticLambda0;
            AndroidUtilities.runOnUIThread(groupStickersActivity$SearchAdapter$$ExternalSyntheticLambda0, 300);
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onSearchStickers$2(String str) {
            this.lastQuery = str;
            TLRPC$TL_messages_searchStickerSets tLRPC$TL_messages_searchStickerSets = new TLRPC$TL_messages_searchStickerSets();
            tLRPC$TL_messages_searchStickerSets.q = str;
            this.reqId = GroupStickersActivity.this.getConnectionsManager().sendRequest(tLRPC$TL_messages_searchStickerSets, new GroupStickersActivity$SearchAdapter$$ExternalSyntheticLambda2(this, tLRPC$TL_messages_searchStickerSets, str), 66);
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onSearchStickers$1(TLRPC$TL_messages_searchStickerSets tLRPC$TL_messages_searchStickerSets, String str, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
            if (ObjectsCompat$$ExternalSyntheticBackport0.m(this.lastQuery, tLRPC$TL_messages_searchStickerSets.q) && (tLObject instanceof TLRPC$TL_messages_foundStickerSets)) {
                ArrayList arrayList = new ArrayList();
                Iterator<TLRPC$StickerSetCovered> it = ((TLRPC$TL_messages_foundStickerSets) tLObject).sets.iterator();
                while (it.hasNext()) {
                    TLRPC$StickerSetCovered next = it.next();
                    TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet = new TLRPC$TL_messages_stickerSet();
                    tLRPC$TL_messages_stickerSet.set = next.set;
                    tLRPC$TL_messages_stickerSet.documents = next.covers;
                    arrayList.add(tLRPC$TL_messages_stickerSet);
                }
                String trim = str.toLowerCase(Locale.ROOT).trim();
                ArrayList arrayList2 = new ArrayList();
                Iterator<TLRPC$TL_messages_stickerSet> it2 = MediaDataController.getInstance(GroupStickersActivity.this.currentAccount).getStickerSets(0).iterator();
                while (it2.hasNext()) {
                    TLRPC$TL_messages_stickerSet next2 = it2.next();
                    String str2 = next2.set.short_name;
                    Locale locale = Locale.ROOT;
                    if (str2.toLowerCase(locale).contains(trim) || next2.set.title.toLowerCase(locale).contains(trim)) {
                        arrayList2.add(next2);
                    }
                }
                AndroidUtilities.runOnUIThread(new GroupStickersActivity$SearchAdapter$$ExternalSyntheticLambda1(this, arrayList, arrayList2, str));
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onSearchStickers$0(List list, List list2, String str) {
            this.searchEntries = list;
            this.localSearchEntries = list2;
            notifyDataSetChanged();
            GroupStickersActivity.this.emptyView.title.setVisibility(8);
            GroupStickersActivity.this.emptyView.subtitle.setText(LocaleController.formatString(NUM, str));
            GroupStickersActivity.this.emptyView.showProgress(false, true);
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v2, resolved type: org.telegram.ui.Cells.StickerSetCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v3, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v3, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX WARNING: type inference failed for: r9v1, types: [android.view.View] */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public androidx.recyclerview.widget.RecyclerView.ViewHolder onCreateViewHolder(android.view.ViewGroup r9, int r10) {
            /*
                r8 = this;
                if (r10 == 0) goto L_0x0030
                org.telegram.ui.Cells.HeaderCell r9 = new org.telegram.ui.Cells.HeaderCell
                android.content.Context r1 = r8.mContext
                r3 = 21
                r4 = 0
                r5 = 0
                r6 = 0
                org.telegram.ui.GroupStickersActivity r10 = org.telegram.ui.GroupStickersActivity.this
                org.telegram.ui.ActionBar.Theme$ResourcesProvider r7 = r10.getResourceProvider()
                java.lang.String r2 = "windowBackgroundWhiteGrayText4"
                r0 = r9
                r0.<init>(r1, r2, r3, r4, r5, r6, r7)
                android.content.Context r10 = r8.mContext
                r0 = 2131165436(0x7var_fc, float:1.794509E38)
                java.lang.String r1 = "windowBackgroundGrayShadow"
                android.graphics.drawable.Drawable r10 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r10, (int) r0, (java.lang.String) r1)
                r9.setBackground(r10)
                r10 = 2131625114(0x7f0e049a, float:1.8877427E38)
                java.lang.String r10 = org.telegram.messenger.LocaleController.getString((int) r10)
                r9.setText(r10)
                goto L_0x0041
            L_0x0030:
                org.telegram.ui.Cells.StickerSetCell r9 = new org.telegram.ui.Cells.StickerSetCell
                android.content.Context r10 = r8.mContext
                r0 = 3
                r9.<init>(r10, r0)
                java.lang.String r10 = "windowBackgroundWhite"
                int r10 = org.telegram.ui.ActionBar.Theme.getColor(r10)
                r9.setBackgroundColor(r10)
            L_0x0041:
                androidx.recyclerview.widget.RecyclerView$LayoutParams r10 = new androidx.recyclerview.widget.RecyclerView$LayoutParams
                r0 = -1
                r1 = -2
                r10.<init>((int) r0, (int) r1)
                r9.setLayoutParams(r10)
                org.telegram.ui.Components.RecyclerListView$Holder r10 = new org.telegram.ui.Components.RecyclerListView$Holder
                r10.<init>(r9)
                return r10
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.GroupStickersActivity.SearchAdapter.onCreateViewHolder(android.view.ViewGroup, int):androidx.recyclerview.widget.RecyclerView$ViewHolder");
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            long j;
            if (getItemViewType(i) == 0) {
                boolean z = true;
                boolean z2 = i > this.searchEntries.size();
                List<TLRPC$TL_messages_stickerSet> list = z2 ? this.localSearchEntries : this.searchEntries;
                if (z2) {
                    i = (i - this.searchEntries.size()) - 1;
                }
                StickerSetCell stickerSetCell = (StickerSetCell) viewHolder.itemView;
                TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet = list.get(i);
                stickerSetCell.setStickersSet(tLRPC$TL_messages_stickerSet, i != list.size() - 1, !z2);
                String str = this.lastQuery;
                stickerSetCell.setSearchQuery(tLRPC$TL_messages_stickerSet, str != null ? str.toLowerCase(Locale.ROOT) : "", GroupStickersActivity.this.getResourceProvider());
                if (GroupStickersActivity.this.selectedStickerSet != null) {
                    j = GroupStickersActivity.this.selectedStickerSet.set.id;
                } else {
                    j = (GroupStickersActivity.this.info == null || GroupStickersActivity.this.info.stickerset == null) ? 0 : GroupStickersActivity.this.info.stickerset.id;
                }
                if (tLRPC$TL_messages_stickerSet.set.id != j) {
                    z = false;
                }
                stickerSetCell.setChecked(z, false);
            }
        }

        public int getItemViewType(int i) {
            return this.searchEntries.size() == i ? 1 : 0;
        }

        public int getItemCount() {
            return this.searchEntries.size() + this.localSearchEntries.size() + (this.localSearchEntries.isEmpty() ^ true ? 1 : 0);
        }

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return getItemViewType(viewHolder.getAdapterPosition()) == 0;
        }
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public int getItemCount() {
            return GroupStickersActivity.this.rowCount;
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            long j;
            int itemViewType = viewHolder.getItemViewType();
            boolean z = true;
            if (itemViewType == 0) {
                ArrayList<TLRPC$TL_messages_stickerSet> stickerSets = MediaDataController.getInstance(GroupStickersActivity.this.currentAccount).getStickerSets(0);
                int access$1000 = i - GroupStickersActivity.this.stickersStartRow;
                StickerSetCell stickerSetCell = (StickerSetCell) viewHolder.itemView;
                TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet = stickerSets.get(access$1000);
                stickerSetCell.setStickersSet(stickerSets.get(access$1000), access$1000 != stickerSets.size() - 1);
                if (GroupStickersActivity.this.selectedStickerSet != null) {
                    j = GroupStickersActivity.this.selectedStickerSet.set.id;
                } else {
                    j = (GroupStickersActivity.this.info == null || GroupStickersActivity.this.info.stickerset == null) ? 0 : GroupStickersActivity.this.info.stickerset.id;
                }
                if (tLRPC$TL_messages_stickerSet.set.id != j) {
                    z = false;
                }
                stickerSetCell.setChecked(z, false);
            } else if (itemViewType != 1) {
                if (itemViewType == 4) {
                    ((HeaderCell) viewHolder.itemView).setText(LocaleController.getString(NUM));
                }
            } else if (i == GroupStickersActivity.this.infoRow) {
                String string = LocaleController.getString("ChooseStickerSetMy", NUM);
                int indexOf = string.indexOf("@stickers");
                if (indexOf != -1) {
                    try {
                        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(string);
                        spannableStringBuilder.setSpan(new URLSpanNoUnderline("@stickers") {
                            public void onClick(View view) {
                                MessagesController.getInstance(GroupStickersActivity.this.currentAccount).openByUserName("stickers", GroupStickersActivity.this, 1);
                            }
                        }, indexOf, indexOf + 9, 18);
                        ((TextInfoPrivacyCell) viewHolder.itemView).setText(spannableStringBuilder);
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
                        ((TextInfoPrivacyCell) viewHolder.itemView).setText(string);
                    }
                } else {
                    ((TextInfoPrivacyCell) viewHolder.itemView).setText(string);
                }
            }
        }

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return viewHolder.getItemViewType() == 0;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view;
            if (i == 0) {
                view = new StickerSetCell(this.mContext, 3);
                view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            } else if (i != 1) {
                view = new HeaderCell(this.mContext);
                view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            } else {
                view = new TextInfoPrivacyCell(this.mContext);
                view.setBackground(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
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
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{StickerSetCell.class, TextSettingsCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultIcon"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultTitle"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSelector"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "divider"));
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
        return arrayList;
    }
}
