package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BackDrawable;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.MenuDrawable;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Adapters.DialogsSearchAdapter;
import org.telegram.ui.Adapters.FiltersView;
import org.telegram.ui.Cells.ContextLinkCell;
import org.telegram.ui.Cells.DialogCell;
import org.telegram.ui.Cells.HashtagSearchCell;
import org.telegram.ui.Cells.ProfileSearchCell;
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
    public static final int deleteItemId = 202;
    public static final int forwardItemId = 201;
    public static final int gotoItemId = 200;
    int animateFromCount = 0;
    private boolean attached;
    ChatPreviewDelegate chatPreviewDelegate;
    int currentAccount = UserConfig.selectedAccount;
    private ArrayList<FiltersView.MediaFilterData> currentSearchFilters = new ArrayList<>();
    private ActionBarMenuItem deleteItem;
    public DialogsSearchAdapter dialogsSearchAdapter;
    public StickerEmptyView emptyView;
    private FilteredSearchView.Delegate filteredSearchViewDelegate;
    private final int folderId;
    private ActionBarMenuItem forwardItem;
    SizeNotifierFrameLayout fragmentView;
    private ActionBarMenuItem gotoItem;
    private boolean isActionModeShowed;
    /* access modifiers changed from: private */
    public DefaultItemAnimator itemAnimator;
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
    private final ViewPagerAdapter viewPagerAdapter;

    public interface ChatPreviewDelegate {
        void finish();

        void move(float f);

        void startChatPreview(RecyclerListView recyclerListView, DialogCell dialogCell);
    }

    public SearchViewPager(Context context, final DialogsActivity fragment, int type, int initialDialogsType, int folderId2, ChatPreviewDelegate chatPreviewDelegate2) {
        super(context);
        this.folderId = folderId2;
        this.parent = fragment;
        this.chatPreviewDelegate = chatPreviewDelegate2;
        DefaultItemAnimator defaultItemAnimator = new DefaultItemAnimator();
        this.itemAnimator = defaultItemAnimator;
        defaultItemAnimator.setAddDuration(150);
        this.itemAnimator.setMoveDuration(350);
        this.itemAnimator.setChangeDuration(0);
        this.itemAnimator.setRemoveDuration(0);
        this.itemAnimator.setMoveInterpolator(new OvershootInterpolator(1.1f));
        this.itemAnimator.setTranslationInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
        this.dialogsSearchAdapter = new DialogsSearchAdapter(context, type, initialDialogsType, this.itemAnimator) {
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
        this.fragmentView = (SizeNotifierFrameLayout) fragment.getFragmentView();
        AnonymousClass2 r1 = new BlurredRecyclerView(context) {
            /* access modifiers changed from: protected */
            public void dispatchDraw(Canvas canvas) {
                if (SearchViewPager.this.dialogsSearchAdapter != null && SearchViewPager.this.itemAnimator != null && SearchViewPager.this.searchLayoutManager != null && SearchViewPager.this.dialogsSearchAdapter.showMoreAnimation) {
                    canvas.save();
                    invalidate();
                    int lastItemIndex = SearchViewPager.this.dialogsSearchAdapter.getItemCount() - 1;
                    int i = 0;
                    while (true) {
                        if (i >= getChildCount()) {
                            break;
                        }
                        View child = getChildAt(i);
                        if (getChildAdapterPosition(child) == lastItemIndex) {
                            canvas.clipRect(0.0f, 0.0f, (float) getWidth(), ((float) child.getBottom()) + child.getTranslationY());
                            break;
                        }
                        i++;
                    }
                }
                super.dispatchDraw(canvas);
                if (!(SearchViewPager.this.dialogsSearchAdapter == null || SearchViewPager.this.itemAnimator == null || SearchViewPager.this.searchLayoutManager == null || !SearchViewPager.this.dialogsSearchAdapter.showMoreAnimation)) {
                    canvas.restore();
                }
                if (SearchViewPager.this.dialogsSearchAdapter != null && SearchViewPager.this.dialogsSearchAdapter.showMoreHeader != null) {
                    canvas.save();
                    canvas.translate((float) SearchViewPager.this.dialogsSearchAdapter.showMoreHeader.getLeft(), ((float) SearchViewPager.this.dialogsSearchAdapter.showMoreHeader.getTop()) + SearchViewPager.this.dialogsSearchAdapter.showMoreHeader.getTranslationY());
                    SearchViewPager.this.dialogsSearchAdapter.showMoreHeader.draw(canvas);
                    canvas.restore();
                }
            }
        };
        this.searchListView = r1;
        r1.setItemAnimator(this.itemAnimator);
        this.searchListView.setPivotY(0.0f);
        this.searchListView.setAdapter(this.dialogsSearchAdapter);
        this.searchListView.setVerticalScrollBarEnabled(true);
        this.searchListView.setInstantClick(true);
        this.searchListView.setVerticalScrollbarPosition(LocaleController.isRTL ? 1 : 2);
        RecyclerListView recyclerListView = this.searchListView;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, 1, false);
        this.searchLayoutManager = linearLayoutManager;
        recyclerListView.setLayoutManager(linearLayoutManager);
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
                SearchViewPager.this.fragmentView.invalidateBlur();
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
        AnonymousClass4 r4 = new StickerEmptyView(context, loadingView, 1) {
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
        this.searchListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                SearchViewPager.this.fragmentView.invalidateBlur();
            }
        });
        this.itemsEnterAnimator = new RecyclerItemsEnterAnimator(this.searchListView, true);
        ViewPagerAdapter viewPagerAdapter2 = new ViewPagerAdapter();
        this.viewPagerAdapter = viewPagerAdapter2;
        setAdapter(viewPagerAdapter2);
    }

    public void onTextChanged(String text) {
        View view = getCurrentView();
        boolean reset = false;
        if (!this.attached) {
            reset = true;
        }
        if (TextUtils.isEmpty(this.lastSearchString)) {
            reset = true;
        }
        this.lastSearchString = text;
        search(view, getCurrentPosition(), text, reset);
    }

    /* access modifiers changed from: private */
    public void search(View view, int position, String query, boolean reset) {
        boolean reset2;
        boolean reset3;
        View view2 = view;
        String str = query;
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
                this.dialogsSearchAdapter.searchDialogs(str, includeFolder);
                this.dialogsSearchAdapter.setFiltersDelegate(this.filteredSearchViewDelegate, false);
                this.noMediaFiltersSearchView.animate().setListener((Animator.AnimatorListener) null).cancel();
                this.noMediaFiltersSearchView.setDelegate((FilteredSearchView.Delegate) null, false);
                if (reset) {
                    this.emptyView.showProgress(!this.dialogsSearchAdapter.isSearching(), false);
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
                boolean z2 = includeFolder;
            } else {
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
                        reset3 = true;
                    } else {
                        reset3 = reset;
                    }
                    this.noMediaFiltersSearchView.animate().alpha(1.0f).setDuration(150).start();
                    reset2 = reset3;
                }
                boolean z3 = includeFolder;
                this.noMediaFiltersSearchView.search(dialogId, minDate, maxDate, (FiltersView.MediaFilterData) null, includeFolder, query, reset2);
                this.emptyView.setVisibility(8);
            }
            this.emptyView.setKeyboardHeight(this.keyboardSize, false);
            this.noMediaFiltersSearchView.setKeyboardHeight(this.keyboardSize, false);
            return;
        }
        boolean includeFolder2 = includeFolder;
        if (view2 instanceof FilteredSearchView) {
            ((FilteredSearchView) view2).setKeyboardHeight(this.keyboardSize, false);
            ViewPagerAdapter.Item item = this.viewPagerAdapter.items.get(position);
            ViewPagerAdapter.Item item2 = item;
            ((FilteredSearchView) view2).search(dialogId, minDate, maxDate, FiltersView.filters[item.filterIndex], includeFolder2, query, reset);
        } else if (view2 instanceof SearchDownloadsContainer) {
            ((SearchDownloadsContainer) view2).setKeyboardHeight(this.keyboardSize, false);
            ((SearchDownloadsContainer) view2).search(str);
        }
        boolean z4 = reset;
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
                    this.selectedMessagesCountTextView.setOnTouchListener(SearchViewPager$$ExternalSyntheticLambda2.INSTANCE);
                    this.gotoItem = actionMode.addItemWithWidth(200, NUM, AndroidUtilities.dp(54.0f), (CharSequence) LocaleController.getString("AccDescrGoToMessage", NUM));
                    this.forwardItem = actionMode.addItemWithWidth(201, NUM, AndroidUtilities.dp(54.0f), (CharSequence) LocaleController.getString("Forward", NUM));
                    this.deleteItem = actionMode.addItemWithWidth(202, NUM, AndroidUtilities.dp(54.0f), (CharSequence) LocaleController.getString("Delete", NUM));
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
                    this.deleteItem.setVisibility(0);
                    return;
                }
                this.parent.getActionBar().hideActionMode();
                this.selectedFiles.clear();
                for (int i = 0; i < getChildCount(); i++) {
                    if (getChildAt(i) instanceof FilteredSearchView) {
                        ((FilteredSearchView) getChildAt(i)).update();
                    }
                    if (getChildAt(i) instanceof SearchDownloadsContainer) {
                        ((SearchDownloadsContainer) getChildAt(i)).update(true);
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
        if (id == 202) {
            BaseFragment baseFragment = this.parent;
            if (baseFragment != null && baseFragment.getParentActivity() != null) {
                ArrayList<MessageObject> messageObjects = new ArrayList<>(this.selectedFiles.values());
                AlertDialog.Builder builder = new AlertDialog.Builder((Context) this.parent.getParentActivity());
                builder.setTitle(LocaleController.formatPluralString("RemoveDocumentsTitle", this.selectedFiles.size(), new Object[0]));
                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
                spannableStringBuilder.append(AndroidUtilities.replaceTags(LocaleController.formatPluralString("RemoveDocumentsMessage", this.selectedFiles.size(), new Object[0]))).append("\n\n").append(LocaleController.getString("RemoveDocumentsAlertMessage", NUM));
                builder.setMessage(spannableStringBuilder);
                builder.setNegativeButton(LocaleController.getString("Cancel", NUM), SearchViewPager$$ExternalSyntheticLambda1.INSTANCE);
                builder.setPositiveButton(LocaleController.getString("Delete", NUM), new SearchViewPager$$ExternalSyntheticLambda0(this, messageObjects));
                TextView button = (TextView) builder.show().getButton(-1);
                if (button != null) {
                    button.setTextColor(Theme.getColor("dialogTextRed2"));
                }
            }
        } else if (id == 200) {
            if (this.selectedFiles.size() == 1) {
                goToMessage(this.selectedFiles.values().iterator().next());
            }
        } else if (id == 201) {
            Bundle args = new Bundle();
            args.putBoolean("onlySelect", true);
            args.putInt("dialogsType", 3);
            DialogsActivity fragment = new DialogsActivity(args);
            fragment.setDelegate(new SearchViewPager$$ExternalSyntheticLambda3(this));
            this.parent.presentFragment(fragment);
        }
    }

    /* renamed from: lambda$onActionBarItemClick$2$org-telegram-ui-Components-SearchViewPager  reason: not valid java name */
    public /* synthetic */ void m1333x21ad9157(ArrayList messageObjects, DialogInterface dialogInterface, int i) {
        dialogInterface.dismiss();
        this.parent.getDownloadController().deleteRecentFiles(messageObjects);
        hideActionMode();
    }

    /* renamed from: lambda$onActionBarItemClick$3$org-telegram-ui-Components-SearchViewPager  reason: not valid java name */
    public /* synthetic */ void m1334xe499fab6(DialogsActivity fragment1, ArrayList dids, CharSequence message, boolean param) {
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
                actionBarMenuItem.setVisibility(this.selectedFiles.size() == 1 ? 0 : 8);
            }
            if (this.deleteItem != null) {
                boolean canShowDelete = true;
                Iterator<FilteredSearchView.MessageHashId> it = this.selectedFiles.keySet().iterator();
                while (true) {
                    if (!it.hasNext()) {
                        break;
                    }
                    if (!this.selectedFiles.get(it.next()).isDownloadingFile) {
                        canShowDelete = false;
                        break;
                    }
                }
                ActionBarMenuItem actionBarMenuItem2 = this.deleteItem;
                if (!canShowDelete) {
                    i = 8;
                }
                actionBarMenuItem2.setVisibility(i);
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

    public void getThemeDescriptions(ArrayList<ThemeDescription> arrayList) {
        ArrayList<ThemeDescription> arrayList2 = arrayList;
        for (int i = 0; i < this.searchListView.getChildCount(); i++) {
            View child = this.searchListView.getChildAt(i);
            if ((child instanceof ProfileSearchCell) || (child instanceof DialogCell) || (child instanceof HashtagSearchCell)) {
                arrayList2.add(new ThemeDescription(child, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
            }
        }
        for (int i2 = 0; i2 < getChildCount(); i2++) {
            if (getChildAt(i2) instanceof FilteredSearchView) {
                arrayList2.addAll(((FilteredSearchView) getChildAt(i2)).getThemeDescriptions());
            }
        }
        int n = this.viewsByType.size();
        for (int i3 = 0; i3 < n; i3++) {
            View v = (View) this.viewsByType.valueAt(i3);
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
        if (position >= 0) {
            super.setPosition(position);
            this.viewsByType.clear();
            if (this.tabsView != null) {
                this.tabsView.selectTabWithId(position, 1.0f);
            }
            invalidate();
        }
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
            } else if (getChildAt(i) instanceof SearchDownloadsContainer) {
                ((SearchDownloadsContainer) getChildAt(i)).setKeyboardHeight(keyboardSize2, animated);
            }
        }
    }

    public void showOnlyDialogsAdapter(boolean showOnlyDialogsAdapter2) {
        this.showOnlyDialogsAdapter = showOnlyDialogsAdapter2;
    }

    public void messagesDeleted(long channelId, ArrayList<Integer> markAsDeletedMessages) {
        int currentChannelId;
        int n = this.viewsByType.size();
        for (int i = 0; i < n; i++) {
            View v = (View) this.viewsByType.valueAt(i);
            if (v instanceof FilteredSearchView) {
                ((FilteredSearchView) v).messagesDeleted(channelId, markAsDeletedMessages);
            }
        }
        for (int i2 = 0; i2 < getChildCount(); i2++) {
            if (getChildAt(i2) instanceof FilteredSearchView) {
                ((FilteredSearchView) getChildAt(i2)).messagesDeleted(channelId, markAsDeletedMessages);
            }
        }
        this.noMediaFiltersSearchView.messagesDeleted(channelId, markAsDeletedMessages);
        if (!this.selectedFiles.isEmpty()) {
            ArrayList<FilteredSearchView.MessageHashId> toRemove = null;
            ArrayList<FilteredSearchView.MessageHashId> arrayList = new ArrayList<>(this.selectedFiles.keySet());
            int k = 0;
            while (true) {
                currentChannelId = 0;
                if (k >= arrayList.size()) {
                    break;
                }
                FilteredSearchView.MessageHashId hashId = arrayList.get(k);
                MessageObject messageObject = this.selectedFiles.get(hashId);
                if (messageObject != null) {
                    long dialogId = messageObject.getDialogId();
                    if (dialogId < 0 && ChatObject.isChannel((long) ((int) (-dialogId)), this.currentAccount)) {
                        currentChannelId = (int) (-dialogId);
                    }
                    if (((long) currentChannelId) == channelId) {
                        for (int i3 = 0; i3 < markAsDeletedMessages.size(); i3++) {
                            if (messageObject.getId() == markAsDeletedMessages.get(i3).intValue()) {
                                toRemove = new ArrayList<>();
                                toRemove.add(hashId);
                            }
                        }
                    }
                }
                k++;
            }
            if (toRemove != null) {
                int N = toRemove.size();
                for (int a = 0; a < N; a++) {
                    this.selectedFiles.remove(toRemove.get(a));
                }
                this.selectedMessagesCountTextView.setNumber(this.selectedFiles.size(), true);
                ActionBarMenuItem actionBarMenuItem = this.gotoItem;
                if (actionBarMenuItem != null) {
                    if (this.selectedFiles.size() != 1) {
                        currentChannelId = 8;
                    }
                    actionBarMenuItem.setVisibility(currentChannelId);
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

    /* access modifiers changed from: protected */
    public void invalidateBlur() {
        this.fragmentView.invalidateBlur();
    }

    public void cancelEnterAnimation() {
        this.itemsEnterAnimator.cancel();
        this.searchListView.invalidate();
        this.animateFromCount = 0;
    }

    public void showDownloads() {
        setPosition(2);
    }

    public int getPositionForType(int initialSearchType) {
        for (int i = 0; i < this.viewPagerAdapter.items.size(); i++) {
            if (this.viewPagerAdapter.items.get(i).type == 2 && this.viewPagerAdapter.items.get(i).filterIndex == initialSearchType) {
                return i;
            }
        }
        return -1;
    }

    private class ViewPagerAdapter extends ViewPagerFixed.Adapter {
        private static final int DIALOGS_TYPE = 0;
        private static final int DOWNLOADS_TYPE = 1;
        private static final int FILTER_TYPE = 2;
        ArrayList<Item> items;

        public ViewPagerAdapter() {
            ArrayList<Item> arrayList = new ArrayList<>();
            this.items = arrayList;
            arrayList.add(new Item(0));
            if (!SearchViewPager.this.showOnlyDialogsAdapter) {
                Item item = new Item(2);
                item.filterIndex = 0;
                this.items.add(item);
                this.items.add(new Item(1));
                Item item2 = new Item(2);
                item2.filterIndex = 1;
                this.items.add(item2);
                Item item3 = new Item(2);
                item3.filterIndex = 2;
                this.items.add(item3);
                Item item4 = new Item(2);
                item4.filterIndex = 3;
                this.items.add(item4);
                Item item5 = new Item(2);
                item5.filterIndex = 4;
                this.items.add(item5);
            }
        }

        public String getItemTitle(int position) {
            if (this.items.get(position).type == 0) {
                return LocaleController.getString("SearchAllChatsShort", NUM);
            }
            if (this.items.get(position).type == 1) {
                return LocaleController.getString("DownloadsTabs", NUM);
            }
            return FiltersView.filters[this.items.get(position).filterIndex].title;
        }

        public int getItemCount() {
            return this.items.size();
        }

        public View createView(int viewType) {
            if (viewType == 1) {
                return SearchViewPager.this.searchContainer;
            }
            if (viewType == 2) {
                SearchDownloadsContainer downloadsContainer = new SearchDownloadsContainer(SearchViewPager.this.parent, SearchViewPager.this.currentAccount);
                downloadsContainer.recyclerListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        SearchViewPager.this.fragmentView.invalidateBlur();
                    }
                });
                downloadsContainer.setUiCallback(SearchViewPager.this);
                return downloadsContainer;
            }
            FilteredSearchView filteredSearchView = new FilteredSearchView(SearchViewPager.this.parent);
            filteredSearchView.setChatPreviewDelegate(SearchViewPager.this.chatPreviewDelegate);
            filteredSearchView.setUiCallback(SearchViewPager.this);
            filteredSearchView.recyclerListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    SearchViewPager.this.fragmentView.invalidateBlur();
                }
            });
            return filteredSearchView;
        }

        public int getItemViewType(int position) {
            if (this.items.get(position).type == 0) {
                return 1;
            }
            if (this.items.get(position).type == 1) {
                return 2;
            }
            return this.items.get(position).type + position;
        }

        public void bindView(View view, int position, int viewType) {
            SearchViewPager searchViewPager = SearchViewPager.this;
            searchViewPager.search(view, position, searchViewPager.lastSearchString, true);
        }

        private class Item {
            int filterIndex;
            /* access modifiers changed from: private */
            public final int type;

            private Item(int type2) {
                this.type = type2;
            }
        }
    }
}
