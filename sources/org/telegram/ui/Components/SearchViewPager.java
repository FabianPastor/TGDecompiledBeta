package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.HashMap;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.BackDrawable;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.MenuDrawable;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Adapters.DialogsSearchAdapter;
import org.telegram.ui.Adapters.FiltersView;
import org.telegram.ui.Cells.ContextLinkCell;
import org.telegram.ui.Cells.DialogCell;
import org.telegram.ui.Cells.SharedAudioCell;
import org.telegram.ui.Cells.SharedDocumentCell;
import org.telegram.ui.Cells.SharedLinkCell;
import org.telegram.ui.Cells.SharedPhotoVideoCell;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.ViewPagerFixed;
import org.telegram.ui.DialogsActivity;
import org.telegram.ui.FilteredSearchView;

public class SearchViewPager extends ViewPagerFixed implements FilteredSearchView.UiCallback {
    private static final String actionModeTag = "search_view_pager";
    public static final int forwardItemId = 201;
    public static final int gotoItemId = 200;
    int animateFromCount = 0;
    private boolean attached;
    ChatPreviewDelegate chatPreviewDelegate;
    int currentAccount = UserConfig.selectedAccount;
    private ArrayList<FiltersView.MediaFilterData> currentSearchFilters = new ArrayList<>();
    public DialogsSearchAdapter dialogsSearchAdapter;
    public StickerEmptyView emptyView;
    private FilteredSearchView.Delegate filteredSearchViewDelegate;
    private final int folderId;
    private ActionBarMenuItem forwardItem;
    private ActionBarMenuItem gotoItem;
    private boolean isActionModeShowed;
    private RecyclerItemsEnterAnimator itemsEnterAnimator;
    private int keyboardSize;
    /* access modifiers changed from: private */
    public boolean lastSearchScrolledToTop;
    String lastSearchString;
    /* access modifiers changed from: private */
    public FilteredSearchView noMediaFiltersSearchView;
    BaseFragment parent;
    public FrameLayout searchContainer;
    /* access modifiers changed from: private */
    public LinearLayoutManager searchLayoutManager;
    public RecyclerListView searchListView;
    private HashMap<FilteredSearchView.MessageHashId, MessageObject> selectedFiles = new HashMap<>();
    private NumberTextView selectedMessagesCountTextView;
    /* access modifiers changed from: private */
    public boolean showOnlyDialogsAdapter;

    public interface ChatPreviewDelegate {
        void finish();

        void move(float f);

        void startChatPreview(DialogCell dialogCell);
    }

    public SearchViewPager(Context context, final BaseFragment fragment, int type, int initialDialogsType, int folderId2, final ChatPreviewDelegate chatPreviewDelegate2) {
        super(context);
        this.folderId = folderId2;
        this.parent = fragment;
        this.chatPreviewDelegate = chatPreviewDelegate2;
        this.dialogsSearchAdapter = new DialogsSearchAdapter(context, type, initialDialogsType) {
            public void notifyDataSetChanged() {
                int itemCount = getCurrentItemCount();
                super.notifyDataSetChanged();
                if (!SearchViewPager.this.lastSearchScrolledToTop && SearchViewPager.this.searchListView != null) {
                    SearchViewPager.this.searchListView.scrollToPosition(0);
                    boolean unused = SearchViewPager.this.lastSearchScrolledToTop = true;
                }
                if (getItemCount() == 0 && itemCount != 0 && !isSearching()) {
                    SearchViewPager.this.emptyView.showProgress(false, false);
                }
            }
        };
        RecyclerListView recyclerListView = new RecyclerListView(context);
        this.searchListView = recyclerListView;
        recyclerListView.setPivotY(0.0f);
        this.searchListView.setAdapter(this.dialogsSearchAdapter);
        this.searchListView.setVerticalScrollBarEnabled(true);
        this.searchListView.setInstantClick(true);
        this.searchListView.setVerticalScrollbarPosition(LocaleController.isRTL ? 1 : 2);
        RecyclerListView recyclerListView2 = this.searchListView;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, 1, false);
        this.searchLayoutManager = linearLayoutManager;
        recyclerListView2.setLayoutManager(linearLayoutManager);
        this.searchListView.setAnimateEmptyView(true, 0);
        this.searchListView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == 1) {
                    AndroidUtilities.hideKeyboard(fragment.getParentActivity().getCurrentFocus());
                }
            }

            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int visibleItemCount = Math.abs(SearchViewPager.this.searchLayoutManager.findLastVisibleItemPosition() - SearchViewPager.this.searchLayoutManager.findFirstVisibleItemPosition()) + 1;
                int totalItemCount = recyclerView.getAdapter().getItemCount();
                if (visibleItemCount > 0 && SearchViewPager.this.searchLayoutManager.findLastVisibleItemPosition() == totalItemCount - 1 && !SearchViewPager.this.dialogsSearchAdapter.isMessagesSearchEndReached()) {
                    SearchViewPager.this.dialogsSearchAdapter.loadMoreSearchMessages();
                }
            }
        });
        FilteredSearchView filteredSearchView = new FilteredSearchView(this.parent);
        this.noMediaFiltersSearchView = filteredSearchView;
        filteredSearchView.setUiCallback(this);
        this.noMediaFiltersSearchView.setVisibility(8);
        this.noMediaFiltersSearchView.setChatPreviewDelegate(chatPreviewDelegate2);
        this.searchContainer = new FrameLayout(context);
        FlickerLoadingView loadingView = new FlickerLoadingView(context);
        loadingView.setViewType(1);
        AnonymousClass3 r4 = new StickerEmptyView(context, loadingView, 1) {
            public void setVisibility(int visibility) {
                if (SearchViewPager.this.noMediaFiltersSearchView.getTag() != null) {
                    super.setVisibility(8);
                } else {
                    super.setVisibility(visibility);
                }
            }
        };
        this.emptyView = r4;
        r4.title.setText(LocaleController.getString("NoResult", NUM));
        this.emptyView.subtitle.setVisibility(8);
        this.emptyView.setVisibility(8);
        this.emptyView.addView(loadingView, 0);
        this.emptyView.showProgress(true, false);
        this.searchContainer.addView(this.emptyView);
        this.searchContainer.addView(this.searchListView);
        this.searchContainer.addView(this.noMediaFiltersSearchView);
        this.searchListView.setEmptyView(this.emptyView);
        this.itemsEnterAnimator = new RecyclerItemsEnterAnimator(this.searchListView, true);
        setAdapter(new ViewPagerFixed.Adapter() {
            public String getItemTitle(int position) {
                if (position == 0) {
                    return LocaleController.getString("SearchAllChatsShort", NUM);
                }
                return FiltersView.filters[position - 1].title;
            }

            public int getItemCount() {
                if (SearchViewPager.this.showOnlyDialogsAdapter) {
                    return 1;
                }
                return 1 + FiltersView.filters.length;
            }

            public View createView(int viewType) {
                if (viewType == 1) {
                    return SearchViewPager.this.searchContainer;
                }
                FilteredSearchView filteredSearchView = new FilteredSearchView(SearchViewPager.this.parent);
                filteredSearchView.setChatPreviewDelegate(chatPreviewDelegate2);
                filteredSearchView.setUiCallback(SearchViewPager.this);
                return filteredSearchView;
            }

            public int getItemViewType(int position) {
                if (position == 0) {
                    return 1;
                }
                return position + 2;
            }

            public void bindView(View view, int position, int viewType) {
                SearchViewPager searchViewPager = SearchViewPager.this;
                searchViewPager.search(view, position, searchViewPager.lastSearchString, true);
            }
        });
    }

    public void onTextChanged(String text) {
        this.lastSearchString = text;
        View view = getCurrentView();
        boolean reset = false;
        if (!this.attached) {
            reset = true;
        }
        search(view, getCurrentPosition(), text, reset);
    }

    /* access modifiers changed from: private */
    public void search(View view, int position, String query, boolean reset) {
        boolean reset2;
        View view2 = view;
        long dialogId = 0;
        long minDate = 0;
        long maxDate = 0;
        boolean includeFolder = false;
        for (int i = 0; i < this.currentSearchFilters.size(); i++) {
            FiltersView.MediaFilterData data = this.currentSearchFilters.get(i);
            if (data.filterType == 4) {
                if (data.chat instanceof TLRPC.User) {
                    dialogId = ((TLRPC.User) data.chat).id;
                } else if (data.chat instanceof TLRPC.Chat) {
                    dialogId = -((TLRPC.Chat) data.chat).id;
                }
            } else if (data.filterType == 6) {
                minDate = data.dateData.minDate;
                maxDate = data.dateData.maxDate;
            } else if (data.filterType == 7) {
                includeFolder = true;
            }
        }
        if (view2 == this.searchContainer) {
            if (dialogId == 0 && minDate == 0 && maxDate == 0) {
                this.lastSearchScrolledToTop = false;
                this.dialogsSearchAdapter.searchDialogs(query, includeFolder);
                this.dialogsSearchAdapter.setFiltersDelegate(this.filteredSearchViewDelegate, false);
                this.noMediaFiltersSearchView.animate().setListener((Animator.AnimatorListener) null).cancel();
                this.noMediaFiltersSearchView.setDelegate((FilteredSearchView.Delegate) null, false);
                if (reset) {
                    this.emptyView.showProgress(true ^ this.dialogsSearchAdapter.isSearching(), false);
                    this.emptyView.showProgress(this.dialogsSearchAdapter.isSearching(), false);
                } else if (!this.dialogsSearchAdapter.hasRecentSearch()) {
                    this.emptyView.showProgress(this.dialogsSearchAdapter.isSearching(), true);
                }
                if (reset) {
                    this.noMediaFiltersSearchView.setVisibility(8);
                } else if (this.noMediaFiltersSearchView.getVisibility() != 8) {
                    this.noMediaFiltersSearchView.animate().alpha(0.0f).setListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animation) {
                            SearchViewPager.this.noMediaFiltersSearchView.setVisibility(8);
                        }
                    }).setDuration(150).start();
                }
                this.noMediaFiltersSearchView.setTag((Object) null);
                boolean z = reset;
            } else {
                String str = query;
                this.noMediaFiltersSearchView.setTag(1);
                this.noMediaFiltersSearchView.setDelegate(this.filteredSearchViewDelegate, false);
                this.noMediaFiltersSearchView.animate().setListener((Animator.AnimatorListener) null).cancel();
                if (reset) {
                    this.noMediaFiltersSearchView.setVisibility(0);
                    this.noMediaFiltersSearchView.setAlpha(1.0f);
                    reset2 = reset;
                } else {
                    if (this.noMediaFiltersSearchView.getVisibility() != 0) {
                        this.noMediaFiltersSearchView.setVisibility(0);
                        this.noMediaFiltersSearchView.setAlpha(0.0f);
                        reset2 = true;
                    } else {
                        reset2 = reset;
                    }
                    this.noMediaFiltersSearchView.animate().alpha(1.0f).setDuration(150).start();
                }
                this.noMediaFiltersSearchView.search(dialogId, minDate, maxDate, (FiltersView.MediaFilterData) null, includeFolder, query, reset2);
                this.emptyView.setVisibility(8);
            }
            this.emptyView.setKeyboardHeight(this.keyboardSize, false);
            this.noMediaFiltersSearchView.setKeyboardHeight(this.keyboardSize, false);
            boolean z2 = includeFolder;
            return;
        }
        String str2 = query;
        ((FilteredSearchView) view2).setKeyboardHeight(this.keyboardSize, false);
        ((FilteredSearchView) view2).search(dialogId, minDate, maxDate, FiltersView.filters[position - 1], includeFolder, query, reset);
        boolean z3 = reset;
    }

    public void onResume() {
        DialogsSearchAdapter dialogsSearchAdapter2 = this.dialogsSearchAdapter;
        if (dialogsSearchAdapter2 != null) {
            dialogsSearchAdapter2.notifyDataSetChanged();
        }
    }

    public void removeSearchFilter(FiltersView.MediaFilterData filterData) {
        this.currentSearchFilters.remove(filterData);
    }

    public ArrayList<FiltersView.MediaFilterData> getCurrentSearchFilters() {
        return this.currentSearchFilters;
    }

    public void clear() {
        this.currentSearchFilters.clear();
    }

    public void setFilteredSearchViewDelegate(FilteredSearchView.Delegate filteredSearchViewDelegate2) {
        this.filteredSearchViewDelegate = filteredSearchViewDelegate2;
    }

    private void showActionMode(boolean show) {
        if (this.isActionModeShowed != show) {
            if (!show || !this.parent.getActionBar().isActionModeShowed()) {
                if (show && !this.parent.getActionBar().actionModeIsExist("search_view_pager")) {
                    ActionBarMenu actionMode = this.parent.getActionBar().createActionMode(true, "search_view_pager");
                    NumberTextView numberTextView = new NumberTextView(actionMode.getContext());
                    this.selectedMessagesCountTextView = numberTextView;
                    numberTextView.setTextSize(18);
                    this.selectedMessagesCountTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                    this.selectedMessagesCountTextView.setTextColor(Theme.getColor("actionBarActionModeDefaultIcon"));
                    actionMode.addView(this.selectedMessagesCountTextView, LayoutHelper.createLinear(0, -1, 1.0f, 72, 0, 0, 0));
                    this.selectedMessagesCountTextView.setOnTouchListener(SearchViewPager$$ExternalSyntheticLambda0.INSTANCE);
                    this.gotoItem = actionMode.addItemWithWidth(200, NUM, AndroidUtilities.dp(54.0f), LocaleController.getString("AccDescrGoToMessage", NUM));
                    this.forwardItem = actionMode.addItemWithWidth(201, NUM, AndroidUtilities.dp(54.0f), LocaleController.getString("Forward", NUM));
                }
                if (this.parent.getActionBar().getBackButton().getDrawable() instanceof MenuDrawable) {
                    this.parent.getActionBar().setBackButtonDrawable(new BackDrawable(false));
                }
                this.isActionModeShowed = show;
                if (show) {
                    AndroidUtilities.hideKeyboard(this.parent.getParentActivity().getCurrentFocus());
                    this.parent.getActionBar().showActionMode();
                    this.selectedMessagesCountTextView.setNumber(this.selectedFiles.size(), false);
                    this.gotoItem.setVisibility(0);
                    this.forwardItem.setVisibility(0);
                    return;
                }
                this.parent.getActionBar().hideActionMode();
                this.selectedFiles.clear();
                for (int i = 0; i < getChildCount(); i++) {
                    if (getChildAt(i) instanceof FilteredSearchView) {
                        ((FilteredSearchView) getChildAt(i)).update();
                    }
                }
                FilteredSearchView filteredSearchView = this.noMediaFiltersSearchView;
                if (filteredSearchView != null) {
                    filteredSearchView.update();
                }
                int n = this.viewsByType.size();
                for (int i2 = 0; i2 < n; i2++) {
                    View v = (View) this.viewsByType.valueAt(i2);
                    if (v instanceof FilteredSearchView) {
                        ((FilteredSearchView) v).update();
                    }
                }
            }
        }
    }

    static /* synthetic */ boolean lambda$showActionMode$0(View v, MotionEvent event) {
        return true;
    }

    public void onActionBarItemClick(int id) {
        if (id == 200) {
            if (this.selectedFiles.size() == 1) {
                goToMessage(this.selectedFiles.values().iterator().next());
            }
        } else if (id == 201) {
            Bundle args = new Bundle();
            args.putBoolean("onlySelect", true);
            args.putInt("dialogsType", 3);
            DialogsActivity fragment = new DialogsActivity(args);
            fragment.setDelegate(new SearchViewPager$$ExternalSyntheticLambda1(this));
            this.parent.presentFragment(fragment);
        }
    }

    /* renamed from: lambda$onActionBarItemClick$1$org-telegram-ui-Components-SearchViewPager  reason: not valid java name */
    public /* synthetic */ void m2554x5eCLASSNAMEf8(DialogsActivity fragment1, ArrayList dids, CharSequence message, boolean param) {
        DialogsActivity dialogsActivity = fragment1;
        ArrayList arrayList = dids;
        ArrayList<MessageObject> fmessages = new ArrayList<>();
        for (FilteredSearchView.MessageHashId hashId : this.selectedFiles.keySet()) {
            fmessages.add(this.selectedFiles.get(hashId));
        }
        this.selectedFiles.clear();
        showActionMode(false);
        if (dids.size() > 1 || ((Long) arrayList.get(0)).longValue() == AccountInstance.getInstance(this.currentAccount).getUserConfig().getClientUserId() || message != null) {
            for (int a = 0; a < dids.size(); a++) {
                long did = ((Long) arrayList.get(a)).longValue();
                if (message != null) {
                    AccountInstance.getInstance(this.currentAccount).getSendMessagesHelper().sendMessage(message.toString(), did, (MessageObject) null, (MessageObject) null, (TLRPC.WebPage) null, true, (ArrayList<TLRPC.MessageEntity>) null, (TLRPC.ReplyMarkup) null, (HashMap<String, String>) null, true, 0, (MessageObject.SendAnimationData) null);
                }
                AccountInstance.getInstance(this.currentAccount).getSendMessagesHelper().sendMessage(fmessages, did, false, false, true, 0);
            }
            fragment1.finishFragment();
            return;
        }
        long did2 = ((Long) arrayList.get(0)).longValue();
        Bundle args1 = new Bundle();
        args1.putBoolean("scrollToTopOnResume", true);
        if (DialogObject.isEncryptedDialog(did2)) {
            args1.putInt("enc_id", DialogObject.getEncryptedChatId(did2));
        } else {
            if (DialogObject.isUserDialog(did2)) {
                args1.putLong("user_id", did2);
            } else {
                args1.putLong("chat_id", -did2);
            }
            if (!AccountInstance.getInstance(this.currentAccount).getMessagesController().checkCanOpenChat(args1, dialogsActivity)) {
                return;
            }
        }
        ChatActivity chatActivity = new ChatActivity(args1);
        dialogsActivity.presentFragment(chatActivity, true);
        chatActivity.showFieldPanelForForward(true, fmessages);
    }

    public void goToMessage(MessageObject messageObject) {
        Bundle args = new Bundle();
        long dialogId = messageObject.getDialogId();
        if (DialogObject.isEncryptedDialog(dialogId)) {
            args.putInt("enc_id", DialogObject.getEncryptedChatId(dialogId));
        } else if (DialogObject.isUserDialog(dialogId)) {
            args.putLong("user_id", dialogId);
        } else {
            TLRPC.Chat chat = AccountInstance.getInstance(this.currentAccount).getMessagesController().getChat(Long.valueOf(-dialogId));
            if (!(chat == null || chat.migrated_to == null)) {
                args.putLong("migrated_to", dialogId);
                dialogId = -chat.migrated_to.channel_id;
            }
            args.putLong("chat_id", -dialogId);
        }
        args.putInt("message_id", messageObject.getId());
        this.parent.presentFragment(new ChatActivity(args));
        showActionMode(false);
    }

    public int getFolderId() {
        return this.folderId;
    }

    public boolean actionModeShowing() {
        return this.isActionModeShowed;
    }

    public void hideActionMode() {
        showActionMode(false);
    }

    public void toggleItemSelection(MessageObject message, View view, int a) {
        FilteredSearchView.MessageHashId hashId = new FilteredSearchView.MessageHashId(message.getId(), message.getDialogId());
        if (this.selectedFiles.containsKey(hashId)) {
            this.selectedFiles.remove(hashId);
        } else if (this.selectedFiles.size() < 100) {
            this.selectedFiles.put(hashId, message);
        } else {
            return;
        }
        int i = 0;
        if (this.selectedFiles.size() == 0) {
            showActionMode(false);
        } else {
            this.selectedMessagesCountTextView.setNumber(this.selectedFiles.size(), true);
            ActionBarMenuItem actionBarMenuItem = this.gotoItem;
            if (actionBarMenuItem != null) {
                if (this.selectedFiles.size() != 1) {
                    i = 8;
                }
                actionBarMenuItem.setVisibility(i);
            }
        }
        if (view instanceof SharedDocumentCell) {
            ((SharedDocumentCell) view).setChecked(this.selectedFiles.containsKey(hashId), true);
        } else if (view instanceof SharedPhotoVideoCell) {
            ((SharedPhotoVideoCell) view).setChecked(a, this.selectedFiles.containsKey(hashId), true);
        } else if (view instanceof SharedLinkCell) {
            ((SharedLinkCell) view).setChecked(this.selectedFiles.containsKey(hashId), true);
        } else if (view instanceof SharedAudioCell) {
            ((SharedAudioCell) view).setChecked(this.selectedFiles.containsKey(hashId), true);
        } else if (view instanceof ContextLinkCell) {
            ((ContextLinkCell) view).setChecked(this.selectedFiles.containsKey(hashId), true);
        } else if (view instanceof DialogCell) {
            ((DialogCell) view).setChecked(this.selectedFiles.containsKey(hashId), true);
        }
    }

    public boolean isSelected(FilteredSearchView.MessageHashId messageHashId) {
        return this.selectedFiles.containsKey(messageHashId);
    }

    public void showActionMode() {
        showActionMode(true);
    }

    /* access modifiers changed from: protected */
    public void onItemSelected(View currentPage, View oldPage, int position, int oldPosition) {
        if (position == 0) {
            if (this.noMediaFiltersSearchView.getVisibility() == 0) {
                this.noMediaFiltersSearchView.setDelegate(this.filteredSearchViewDelegate, false);
                this.dialogsSearchAdapter.setFiltersDelegate((FilteredSearchView.Delegate) null, false);
            } else {
                this.noMediaFiltersSearchView.setDelegate((FilteredSearchView.Delegate) null, false);
                this.dialogsSearchAdapter.setFiltersDelegate(this.filteredSearchViewDelegate, true);
            }
        } else if (currentPage instanceof FilteredSearchView) {
            boolean update = false;
            if (oldPosition == 0 && this.noMediaFiltersSearchView.getVisibility() != 0) {
                update = true;
            }
            ((FilteredSearchView) currentPage).setDelegate(this.filteredSearchViewDelegate, update);
        }
        if (oldPage instanceof FilteredSearchView) {
            ((FilteredSearchView) oldPage).setDelegate((FilteredSearchView.Delegate) null, false);
            return;
        }
        this.dialogsSearchAdapter.setFiltersDelegate((FilteredSearchView.Delegate) null, false);
        this.noMediaFiltersSearchView.setDelegate((FilteredSearchView.Delegate) null, false);
    }

    public void getThemeDescriptors(ArrayList<ThemeDescription> arrayList) {
        ArrayList<ThemeDescription> arrayList2 = arrayList;
        for (int i = 0; i < getChildCount(); i++) {
            if (getChildAt(i) instanceof FilteredSearchView) {
                arrayList2.addAll(((FilteredSearchView) getChildAt(i)).getThemeDescriptions());
            }
        }
        int n = this.viewsByType.size();
        for (int i2 = 0; i2 < n; i2++) {
            View v = (View) this.viewsByType.valueAt(i2);
            if (v instanceof FilteredSearchView) {
                arrayList2.addAll(((FilteredSearchView) v).getThemeDescriptions());
            }
        }
        FilteredSearchView filteredSearchView = this.noMediaFiltersSearchView;
        if (filteredSearchView != null) {
            arrayList2.addAll(filteredSearchView.getThemeDescriptions());
        }
        arrayList2.add(new ThemeDescription(this.emptyView.title, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList2.add(new ThemeDescription(this.emptyView.subtitle, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText"));
    }

    public void updateColors() {
        for (int i = 0; i < getChildCount(); i++) {
            if (getChildAt(i) instanceof FilteredSearchView) {
                RecyclerListView recyclerListView = ((FilteredSearchView) getChildAt(i)).recyclerListView;
                int count = recyclerListView.getChildCount();
                for (int a = 0; a < count; a++) {
                    View child = recyclerListView.getChildAt(a);
                    if (child instanceof DialogCell) {
                        ((DialogCell) child).update(0);
                    }
                }
            }
        }
        int n = this.viewsByType.size();
        for (int i2 = 0; i2 < n; i2++) {
            View v = (View) this.viewsByType.valueAt(i2);
            if (v instanceof FilteredSearchView) {
                RecyclerListView recyclerListView2 = ((FilteredSearchView) v).recyclerListView;
                int count2 = recyclerListView2.getChildCount();
                for (int a2 = 0; a2 < count2; a2++) {
                    View child2 = recyclerListView2.getChildAt(a2);
                    if (child2 instanceof DialogCell) {
                        ((DialogCell) child2).update(0);
                    }
                }
            }
        }
        FilteredSearchView filteredSearchView = this.noMediaFiltersSearchView;
        if (filteredSearchView != null) {
            RecyclerListView recyclerListView3 = filteredSearchView.recyclerListView;
            int count3 = recyclerListView3.getChildCount();
            for (int a3 = 0; a3 < count3; a3++) {
                View child3 = recyclerListView3.getChildAt(a3);
                if (child3 instanceof DialogCell) {
                    ((DialogCell) child3).update(0);
                }
            }
        }
    }

    public void reset() {
        setPosition(0);
        if (this.dialogsSearchAdapter.getItemCount() > 0) {
            this.searchLayoutManager.scrollToPositionWithOffset(0, 0);
        }
        this.viewsByType.clear();
    }

    public void setPosition(int position) {
        super.setPosition(position);
        this.viewsByType.clear();
        if (this.tabsView != null) {
            this.tabsView.selectTabWithId(position, 1.0f);
        }
        invalidate();
    }

    public void setKeyboardHeight(int keyboardSize2) {
        this.keyboardSize = keyboardSize2;
        boolean animated = getVisibility() == 0 && getAlpha() > 0.0f;
        for (int i = 0; i < getChildCount(); i++) {
            if (getChildAt(i) instanceof FilteredSearchView) {
                ((FilteredSearchView) getChildAt(i)).setKeyboardHeight(keyboardSize2, animated);
            } else if (getChildAt(i) == this.searchContainer) {
                this.emptyView.setKeyboardHeight(keyboardSize2, animated);
                this.noMediaFiltersSearchView.setKeyboardHeight(keyboardSize2, animated);
            }
        }
    }

    public void showOnlyDialogsAdapter(boolean showOnlyDialogsAdapter2) {
        this.showOnlyDialogsAdapter = showOnlyDialogsAdapter2;
    }

    public void messagesDeleted(long channelId, ArrayList<Integer> markAsDeletedMessages) {
        long j = channelId;
        ArrayList<Integer> arrayList = markAsDeletedMessages;
        int n = this.viewsByType.size();
        for (int i = 0; i < n; i++) {
            View v = (View) this.viewsByType.valueAt(i);
            if (v instanceof FilteredSearchView) {
                ((FilteredSearchView) v).messagesDeleted(j, arrayList);
            }
        }
        for (int i2 = 0; i2 < getChildCount(); i2++) {
            if (getChildAt(i2) instanceof FilteredSearchView) {
                ((FilteredSearchView) getChildAt(i2)).messagesDeleted(j, arrayList);
            }
        }
        this.noMediaFiltersSearchView.messagesDeleted(j, arrayList);
        if (!this.selectedFiles.isEmpty()) {
            ArrayList<FilteredSearchView.MessageHashId> toRemove = null;
            for (FilteredSearchView.MessageHashId hashId : this.selectedFiles.keySet()) {
                MessageObject messageObject = this.selectedFiles.get(hashId);
                long dialogId = messageObject.getDialogId();
                if (((long) ((dialogId >= 0 || !ChatObject.isChannel((long) ((int) (-dialogId)), this.currentAccount)) ? 0 : (int) (-dialogId))) == j) {
                    for (int i3 = 0; i3 < markAsDeletedMessages.size(); i3++) {
                        if (messageObject.getId() == arrayList.get(i3).intValue()) {
                            toRemove = new ArrayList<>();
                            toRemove.add(hashId);
                        }
                    }
                }
                if (toRemove != null) {
                    int N = toRemove.size();
                    for (int a = 0; a < N; a++) {
                        this.selectedFiles.remove(toRemove.get(a));
                    }
                    this.selectedMessagesCountTextView.setNumber(this.selectedFiles.size(), true);
                    ActionBarMenuItem actionBarMenuItem = this.gotoItem;
                    if (actionBarMenuItem != null) {
                        actionBarMenuItem.setVisibility(this.selectedFiles.size() == 1 ? 0 : 8);
                    }
                }
            }
        }
    }

    public void runResultsEnterAnimation() {
        RecyclerItemsEnterAnimator recyclerItemsEnterAnimator = this.itemsEnterAnimator;
        int i = this.animateFromCount;
        recyclerItemsEnterAnimator.showItemsAnimated(i > 0 ? i + 1 : 0);
        this.animateFromCount = this.dialogsSearchAdapter.getItemCount();
    }

    public ViewPagerFixed.TabsView getTabsView() {
        return this.tabsView;
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.attached = true;
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.attached = false;
    }

    public void cancelEnterAnimation() {
        this.itemsEnterAnimator.cancel();
        this.searchListView.invalidate();
        this.animateFromCount = 0;
    }
}
